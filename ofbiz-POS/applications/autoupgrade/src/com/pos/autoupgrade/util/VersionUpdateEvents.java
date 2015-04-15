package com.pos.autoupgrade.util;


import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;



public class VersionUpdateEvents {  
    public static String module = VersionUpdateEvents.class.getName();
    
    public static void upgrade(HttpServletRequest request, HttpServletResponse response) throws Exception{

    	String systemDir = System.getProperty("ofbiz.home");
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String attachmentId = request.getParameter("attachmentId");
		String versionNo = request.getParameter("curVersion");

		if (!UtilValidate.isEmpty(attachmentId) && !UtilValidate.isEmpty(versionNo)) {
	    	updateLocalVersionAttach(delegator,attachmentId,versionNo);
	    	batExec(systemDir+"/autoupgrade.bat");
		}
    }  

	public static void batExec(String exeBatFile){  
	    try {
	    	//exeBatFile = exeBatFile.replaceAll("\\", "/");
	        Runtime.getRuntime().exec("cmd.exe /c start "+exeBatFile);  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	}

    private static void updateLocalVersionAttach(Delegator delegator,String curVersionId,String curVersionNo){
        try {

        	List<GenericValue> curVerList = delegator.findByAnd("VersionAttachment", UtilMisc.toMap("attachmentId", curVersionId));
     	   //如果有该实体定义的记录，则更新，否则添加
     	   if (!UtilValidate.isEmpty(curVerList)) {
 				GenericValue verItem = curVerList.get(0);
 				verItem.set("status",Long.valueOf(2));
 				verItem.store();
     	   } else {
 				GenericValue verItem = delegator.makeValue("VersionAttachment", 
 						UtilMisc.toMap("attachmentId", delegator.getNextSeqId("VersionAttachment"),
 						"contentType","application/zip;charset=UTF-8",
 						"curVersion",curVersionNo,
 						"status",Long.valueOf(2)));
 				verItem.create();
     	   }
        } catch (Exception e) {
     	   Debug.logWarning(e.getMessage(), module);

        }

     }    

}  