package com.pos.autoupgrade.webservice;



import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.ByteBuffer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import net.sf.json.JSONObject;


import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;


import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.util.CommServiceUtil;

import com.pos.autoupgrade.util.DefaultUtil;
import com.pos.autoupgrade.util.VersionUpdateEvents;



public class VersionUpdateService {  

    public static String module = VersionUpdateService.class.getName();

    protected static final int connectTimeout = 30 * 1000; // 连接超时:30s  
    protected static final int readTimeout = 1 * 1000 * 1000; // IO超时  
      
      
    private static void trustAllHttpsCertificates() throws Exception {  
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];  
        javax.net.ssl.TrustManager tm = new miTM();  
        trustAllCerts[0] = tm;  
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext  
                .getInstance("SSL");  
        sc.init(null, trustAllCerts, null);  
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc  
                .getSocketFactory());  
    }  
  
    static class miTM implements javax.net.ssl.TrustManager,  
            javax.net.ssl.X509TrustManager {  
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {  
            return null;  
        }  
  
        public boolean isServerTrusted(  
                java.security.cert.X509Certificate[] certs) {  
            return true;  
        }  
  
        public boolean isClientTrusted(  
                java.security.cert.X509Certificate[] certs) {  
            return true;  
        }  
  
        public void checkServerTrusted(  
                java.security.cert.X509Certificate[] certs, String authType)  
                throws java.security.cert.CertificateException {  
            return;  
        }  
  
        public void checkClientTrusted(  
                java.security.cert.X509Certificate[] certs, String authType)  
                throws java.security.cert.CertificateException {  
            return;  
        }  
    }

    public static Map<String,Object> getNewVersion(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        Map<String,Object> result = new HashMap<String,Object>();
        JSONObject retJsonObj = new JSONObject();
        retJsonObj.put(DefaultUtil.newVersionJsonM, "");
        retJsonObj.put(DefaultUtil.newVerFnameJsonM, "");
        retJsonObj.put(DefaultUtil.newVersionIDJsonM, "");

        try {

	        List<GenericValue> netVersions = null;
        	netVersions = delegator.findList("VersionAttachment", null, null, UtilMisc.toList("-lastUpdatedStamp"), null, false);

	        if (!UtilValidate.isEmpty(netVersions)) {
	            // No version matches, return an empty
	        	GenericValue version = netVersions.get(0);
	        	String newVersionNo = (String)version.get("curVersion");

	        	retJsonObj.put(DefaultUtil.newVersionJsonM, newVersionNo);
                retJsonObj.put(DefaultUtil.newVerFnameJsonM, (String)version.get("fileName"));
                retJsonObj.put(DefaultUtil.newVersionIDJsonM, (String)version.get("attachmentId"));

	        }
            
        }catch(Exception e){
        	Debug.logError(e, module);
        }
        
        String strResult = retJsonObj.toString();
        result.put(DefaultUtil.outJsonParam, strResult);
        return result;
    }
    
    public static Map<String,Object> downloadVersion(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        //标识,是否存在新的更新文件  
        boolean isUpdated = false;
        String curVersionNo = "";
      //初始化门店时local没有用户数据，需要访问总店服务器获得
    	String strServerType = System.getProperty("posstore.type");
    	if (strServerType.equals("0")) {	//本机为总部服务器
    		return ServiceUtil.returnSuccess();
    	}
        String strServerUrl = System.getProperty("posstore.server.url");
        try {
	        List<GenericValue> curVersions = delegator.findList("VersionAttachment", null, null, UtilMisc.toList("-lastUpdatedStamp"), null, false);

	        if (!UtilValidate.isEmpty(curVersions)) {
	        	GenericValue version = curVersions.get(0);
	        	curVersionNo = ((String)version.get("curVersion")).trim();

	        }
        }catch(Exception e){
        	Debug.logError(e, module);
        }

        String newVerNo="";
        String newVerId="";
        //调用总店版本服务获得版本号
        String strJson = CommServiceUtil.sendRectWebService(strServerUrl+"/webtools/control/SOAPService","getNewVersion", null);
        try{
	        JSONObject jsonRoot = JSONObject.fromObject(strJson);
	        newVerNo = (String)jsonRoot.get(DefaultUtil.newVersionJsonM);
	        newVerId = (String)jsonRoot.get(DefaultUtil.newVersionIDJsonM);
	        if (!newVerNo.trim().isEmpty() && !newVerNo.trim().equals(curVersionNo)){
	        	isUpdated = true;
	        }
        } catch (Exception e){}
        if (isUpdated) {  

	        HostnameVerifier hv = new HostnameVerifier() {  
	            public boolean verify(String urlHostName, SSLSession session) {  
	                System.out.println("Warning: URL Host: " + urlHostName + " vs. "  
	                                   + session.getPeerHost());  
	                return true;  
	            }  
	        };  
	
			String systemDir = System.getProperty("ofbiz.home");

            //缓存网络上下载的文件  
            File newFile = new File(systemDir+"/newversion.zip");  
            //获取网络上的文件位置 
            strServerUrl = System.getProperty("posstore.server.ssl.url");
            String netFileAddress = strServerUrl+"/autoupgrade/control/DownloadNewVersion";  
            FileOutputStream fos = null;  
            BufferedInputStream bis = null;  
            boolean bFileSave = false;
            try {  
                 //打开URL通道  
            	trustAllHttpsCertificates();  
            	HttpsURLConnection.setDefaultHostnameVerifier(hv);

                URL url = new URL(netFileAddress);  
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
                conn.setConnectTimeout(connectTimeout);  
                conn.setReadTimeout(readTimeout);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.connect();
                
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                String content = "attachmentId="+newVerId;  
                out.writeBytes(content);  
                out.flush();  
                out.close(); 

                bis = new BufferedInputStream(conn.getInputStream());  
                byte[] buffer = new byte[1024];  
                int size = 0;  
                fos = new FileOutputStream(newFile);

                //保存文件
                int nFileSize = 0;
                while ((size = bis.read(buffer)) != -1) {
                	String temp1 = new String(buffer);
                    if ((nFileSize==0) && temp1.indexOf("<html>")!=-1){
                    	break;
                    }
                    fos.write(buffer, 0, size);
                    nFileSize += size;
                    fos.flush();
                    bFileSave = true;
                }
 
                System.out.println("文件下载完成");  
            } catch (Exception ex) {
            	bFileSave = false;
                System.out.println("文件读取错误");  
            } finally {  
                try{  
                    if(bis!=null){  
                        bis.close();  
                    }  
                }catch(Exception exp){  
                    exp.printStackTrace();  
                }  
                try {  
                    if(fos!=null && bFileSave){  
                        fos.close();  
                    }  
                } catch (IOException exp) {  
                    exp.printStackTrace();  
                }  
            }
            if (bFileSave)
            	updateLocalVersionAttach(delegator,newVerNo);
        }
        return ServiceUtil.returnSuccess();
    }  
    
    private static void updateLocalVersionAttach(Delegator delegator,String curVersionNo){

        try {

     	   List<GenericValue> curVerList = delegator.findList("VersionAttachment", null, null, UtilMisc.toList("-lastUpdatedStamp"), null, false);

     	   //如果有该实体定义的记录，则更新，否则添加
     	   if (!UtilValidate.isEmpty(curVerList)) {
 				GenericValue verItem = curVerList.get(0);
 				verItem.set("curVersion",curVersionNo);
 				verItem.set("status",Long.valueOf(1));
 				verItem.store();
     	   } else {
 				GenericValue verItem = delegator.makeValue("VersionAttachment", 
 						UtilMisc.toMap("attachmentId", delegator.getNextSeqId("VersionAttachment"),
 						"contentType","application/zip;charset=UTF-8",
 						"curVersion",curVersionNo,
 						"status",Long.valueOf(1)));
 				verItem.create();
     	   }
        } catch (Exception e) {
     	   Debug.logWarning(e.getMessage(), module);

        }

     }    

    public static Map<String,Object> deleteVersion(DispatchContext dctx, Map<String, ? extends Object> context) throws Exception{

        Delegator delegator = dctx.getDelegator();
		String attachmentId = (String) context.get("attachmentId");
		try {

			GenericValue curVerItem = delegator.findOne("VersionAttachment", UtilMisc.toMap("attachmentId", attachmentId),false);
			//如果有该实体定义的记录，则更新，否则添加
			if (!UtilValidate.isEmpty(curVerItem)) {
				curVerItem.remove();
			}
		} catch (Exception e) {
			Debug.logWarning(e.getMessage(), module);
		}
        return ServiceUtil.returnSuccess();
    }  
    public static Map<String,Object> upload(DispatchContext dctx, Map<String, ? extends Object> context) throws Exception{
        Delegator delegator = dctx.getDelegator();

        ByteBuffer fileData = (ByteBuffer) context.get("uploadFile");
        String fileName = (String) context.get("_uploadFile_fileName");
        //String contentType = (String) context.get("_uploadFile_contentType");
        String versionNo = (String) context.get("versionNo");
        String description = (String) context.get("description");
        

        if (UtilValidate.isNotEmpty(fileName)) {
            String imageServerPath = FlexibleStringExpander.expandString(UtilProperties.getPropertyValue("autoupgrade", "upload.server.path"), context);
   		
            File file = new File(imageServerPath + fileName);
            Debug.logInfo("upload file to " + file.getAbsolutePath(), "");

            try {
                RandomAccessFile out = new RandomAccessFile(file, "rw");
                out.write(fileData.array());
                out.close();
            } catch (FileNotFoundException e) {
                Debug.logError(e, "");
                return ServiceUtil.returnError("file can't write, fileName:" + file.getAbsolutePath());
            } catch (IOException e) {
                Debug.logError(e, "");
                return ServiceUtil.returnError("file can't write, fileName:" + file.getAbsolutePath());
            }
            
    		GenericValue verItem = delegator.makeValue("VersionAttachment", 
    		UtilMisc.toMap("attachmentId", delegator.getNextSeqId("VersionAttachment"),
    		"contentType","application/zip;charset=UTF-8",
    		"zippath",imageServerPath + fileName,
    		"curVersion",versionNo,
    		"description",description,
    		"status",Long.valueOf(0)));
     		verItem.create();

        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String,Object> getVersionStatus(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        Map<String,Object> result = new HashMap<String,Object>();
        JSONObject retJsonObj = new JSONObject();
        retJsonObj.put(DefaultUtil.versionStatus, "2");

        try {

	        List<GenericValue> netVersions = null;
        	netVersions = delegator.findList("VersionAttachment", null, null, UtilMisc.toList("-lastUpdatedStamp"), null, false);

	        if (!UtilValidate.isEmpty(netVersions)) {
	            // No version matches, return an empty
	        	GenericValue version = netVersions.get(0);
	        	long newVersionF = version.getLong("status");

	        	retJsonObj.put(DefaultUtil.versionStatus, newVersionF);
	        }
            
        }catch(Exception e){
        	Debug.logError(e, module);
        }
        
        String strResult = retJsonObj.toString();
        result.put(DefaultUtil.outJsonParam, strResult);
        return result;
    }
    
    public static Map<String,Object> upgrade(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Map<String,Object> result = new HashMap<String,Object>();
    	String systemDir = System.getProperty("ofbiz.home");
    	Delegator delegator = dctx.getDelegator();

    	if (updateLocalVersionAttach(delegator))
    		VersionUpdateEvents.batExec(systemDir+"/autoupgrade.bat");
    	else
    		result.put(DefaultUtil.errMsg, "upgrade error");
        return result;
    }  

    private static boolean updateLocalVersionAttach(Delegator delegator){
    	boolean bRet=false;
    	boolean beganTransaction= true;
        try {
        	//事务开始
        	TransactionUtil.setTransactionTimeout(600);
        	beganTransaction = TransactionUtil.begin();

      	   List<GenericValue> curVerList = delegator.findList("VersionAttachment", null, null, UtilMisc.toList("-lastUpdatedStamp"), null, false);
		   //如果有该实体定义的记录，则更新
		   if (!UtilValidate.isEmpty(curVerList)) {
				GenericValue verItem = curVerList.get(0);
				if (verItem.getLong("status") == 1 || verItem.getLong("status") == 2){
					verItem.set("status",Long.valueOf(2));
					verItem.store();
					bRet = true;
			   		//事务提交
			   		TransactionUtil.commit(beganTransaction);
				}
		   }
        } catch (Exception e) {
        	Debug.logWarning(e.getMessage(), module);
        	
			try {
				TransactionUtil.rollback(beganTransaction, e.getMessage(), new Exception(e.getMessage()));
			} catch (GenericTransactionException eG){
				Debug.logWarning(eG.getMessage(), module);
			}

        }
        return bRet;
     }
    
    public static Map<String,Object> downloadAndUpgrade(DispatchContext dctx, Map<String, ? extends Object> context) {

    	Delegator delegator = dctx.getDelegator();
    	long newVersionF=0;
    	SQLUpdateService.readFileByLines(dctx);
    	downloadVersion(dctx,context);
        try {

	        List<GenericValue> netVersions = null;
        	netVersions = delegator.findList("VersionAttachment", null, null, UtilMisc.toList("-lastUpdatedStamp"), null, false);

	        if (!UtilValidate.isEmpty(netVersions)) {
	            // No version matches, return an empty
	        	GenericValue version = netVersions.get(0);
	        	newVersionF = version.getLong("status");
	        }
            
        }catch(Exception e){
        	Debug.logError(e, module);
        }
        if (1==newVersionF) {
    	
	    	String systemDir = System.getProperty("ofbiz.home");
	
	    	if (updateLocalVersionAttach(delegator))
	    		VersionUpdateEvents.batExec(systemDir+"/autoupgrade.bat");
	    	else {
	    		ServiceUtil.returnError("upgrade error");
	    	}
        }
        return ServiceUtil.returnSuccess();
    }
}