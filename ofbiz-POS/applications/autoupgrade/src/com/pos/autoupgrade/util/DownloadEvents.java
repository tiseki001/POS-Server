package com.pos.autoupgrade.util;


import java.io.*;

import javax.servlet.*;
import javax.servlet.http.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;


public class DownloadEvents {
	public static final String module = DownloadEvents.class.getName();
	
	public static String download(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//get attachment info from database
		GenericDelegator delegator = (GenericDelegator) request.getAttribute("delegator");
		String attachmentId = request.getParameter("attachmentId");
	
		GenericValue attachment = null;
		try {
			attachment = delegator.findByPrimaryKey("VersionAttachment", UtilMisc.toMap("attachmentId", attachmentId));
		
			if (!UtilValidate.isEmpty(attachment)) {
				String filePath = (String)attachment.get("zippath");
				File f = new File(filePath);
				FileInputStream in = new FileInputStream(f);		
				
				response.setContentType((String)attachment.get("contentType"));
				response.setHeader("Content-Disposition", "filename="+(String)attachment.get("fileName"));
				response.setContentLength((int)f.length());
						
				//fetch the file
			 	int length = (int)f.length();
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
			}
		} catch (GenericEntityException gee) {
			Debug.logError(gee, module);
			return "error";
		}
		return "success";
	}
}
