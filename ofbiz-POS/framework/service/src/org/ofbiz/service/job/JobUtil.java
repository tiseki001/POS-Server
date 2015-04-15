package org.ofbiz.service.job;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;


public class JobUtil {


    public static List<String> excludeIdList = new ArrayList<String>(Arrays.asList("import2StoreJob","downloadNewVersion","uploadData2Store"));

    public static boolean checkAddJob(String serviceName,String jobName,Delegator delegator) {
    	boolean bRet = true;
    	try {
			if (!UtilValidate.isEmpty(serviceName) && !UtilValidate.isEmpty(jobName)
					&& excludeIdList.contains(serviceName)){
				List <GenericValue> jobEnt = delegator.findByAnd("JobSandbox", 
						UtilMisc.toMap("jobName", jobName,
						"serviceName", serviceName/*,"statusId", "SERVICE_PENDING"*/));
	
	            bRet = jobEnt.isEmpty();
			}
    	} catch(Exception e){
    		
    	}
		return bRet;
	}
    
    public static boolean checkSyncJob(String serviceName) {
    	boolean bRet = false;
    	if (!UtilValidate.isEmpty(serviceName)
				&& excludeIdList.contains(serviceName)){
				bRet = true;
    	}
		return bRet;
	}

    public static boolean checkSyncJobByName(String serviceName) {
    	boolean bRet = false;
    	if (!UtilValidate.isEmpty(serviceName)
				&& serviceName.startsWith("sync")){
				bRet = true;
    	}
		return bRet;
	}

    public static boolean checkEbsJobByName(String jobName) {
    	boolean bRet = false;
    	if (!UtilValidate.isEmpty(jobName)
				&& jobName.contains("EbsToPos")){
				bRet = true;
    	}
		return bRet;
	}

}