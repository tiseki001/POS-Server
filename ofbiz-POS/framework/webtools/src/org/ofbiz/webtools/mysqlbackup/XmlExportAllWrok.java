package org.ofbiz.webtools.mysqlbackup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.entity.Delegator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webtools.WebToolsServices;




public class XmlExportAllWrok {
    public static String module = XmlExportAllWrok.class.getName();

	@SuppressWarnings("unused")
	public static BasePosObject XmlExportAll(DispatchContext dctx,Map<String, ? extends Object> context) {
		Map<String, Object> view =new HashMap<String,Object>();
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
	try{
		String systemDir = System.getProperty("ofbiz.home");
		File file = new File(systemDir);
		String dirS = file.getParent();
		File files=new File(dirS);
		files.mkdir();
		String dirs= files.getParent();
		String path=dirs;
		String paths=path+"\\xmlExport";
		File xmlExport = new File(paths);
          if (!xmlExport.exists()) {
        	  xmlExport.mkdir();
          }
    
		String xmlpath=xmlExport.getPath();
		
		view.put("outpath",xmlpath);
		Map<String, Object> results =new HashMap<String,Object>();
		results.putAll(view);
		results.putAll(context);

		Map<String, Object> Result =WebToolsServices.entityExportAll(dctx, results);
		//pObject.setData(Result);
		backup(dctx,context);
		pObject.setFlag(Constant.OK);
		
	}catch (Exception e) {
		pObject.setFlag(Constant.NG);
		pObject.setMsg(e.getMessage());
	}
			return pObject;

	}

            //执行复制
	public static Map<String,Object>  backup (DispatchContext dctx,Map<String, ? extends Object> context){
	try{
		// 获取mysql_home
		String systemDir = System.getProperty("ofbiz.home");
		
		batExec(systemDir + "/xmlExport.bat");
	}catch (Exception e) {
		e.printStackTrace();
	}
	return ServiceUtil.returnSuccess();

	}

	public static void batExec(String exeBatFile) {
			   try {
			    	//exeBatFile = exeBatFile.replaceAll("\\", "/");
			        Runtime.getRuntime().exec("cmd.exe /c start "+exeBatFile);  
			    } catch (IOException e) {  
			        e.printStackTrace();  
			    }  
			}
}

