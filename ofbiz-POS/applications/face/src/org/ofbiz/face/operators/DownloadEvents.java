package org.ofbiz.face.operators;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericValue;


public class DownloadEvents {
	public static final String module = DownloadEvents.class.getName();
	
	public static String download(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//get attachment info from database
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		
		List<GenericValue> gvs = delegator.findList("ErpOptBusinessCheck", null, null, null, null, false);
		
		String systemDir = System.getProperty("ofbiz.home");
		String fileAddress = systemDir + UtilProperties.getPropertyValue("operator",
				"download.server.path");
		String filePath = fileAddress + UtilDateTime.nowDate().getTime() + ".xlsx";
		File file = new File(filePath);
	
		// 导出excel
		OperatorWorker.write(filePath, gvs);
		
		try {
			FileInputStream in = new FileInputStream(file);		
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "filename=check" + 
					UtilDateTime.nowDate().getTime() + ".xlsx");
			response.setContentLength((int)file.length());
					
			//fetch the file
		 	int length = (int)file.length();
		 	if(length != 0)  {
		   		byte[] buf = new byte[4096]; 
		   		ServletOutputStream op = response.getOutputStream();
				while ((in != null) && ((length = in.read(buf)) != -1))  {
					op.write(buf,0, length);
				}
			   	in.close();
		   		op.flush();
		   		op.close();
		 	}
		} catch (IOException ioe) {
			Debug.logError(ioe, module);
			return "error";
		}
		return "success";
	}
}
