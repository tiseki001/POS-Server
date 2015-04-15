package org.ofbiz.face.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class SaveXml {
	public static void saveXml(String path,String fileName,Document documen){
		XMLWriter xmlWriter = null;
		try {
			Date syncDate = new Date();
			SimpleDateFormat dd = new SimpleDateFormat("yyyyMMddHH");		
			String sDateTime = dd.format(syncDate);
			path = path+"/"+sDateTime;
			if(!createDir(path)){
				return;
			}
			path += "/"+fileName;
			OutputFormat outFormat = OutputFormat.createPrettyPrint();
			outFormat.setEncoding("UTF-8");
			outFormat.setTrimText(false);
			xmlWriter = new XMLWriter(new FileOutputStream(path),
					outFormat);
			xmlWriter.write(documen);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (xmlWriter != null)
				try {
					xmlWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	public static void saveXmlByStr(String path,String fileName,String xml){
		XMLWriter xmlWriter = null;
		try {
			Document documen1 = null;
			try {
				documen1 = DocumentHelper.parseText(xml);
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
			//documen1.
			Date syncDate = new Date();
			SimpleDateFormat dd = new SimpleDateFormat("yyyyMMddHH");		
			String sDateTime = dd.format(syncDate);
			path = path+"/"+sDateTime;
			if(!createDir(path)){
				return;
			}
			dd = new SimpleDateFormat("HHmmss");
			sDateTime = dd.format(syncDate);
			path += "/"+fileName+sDateTime+".xml";
			OutputFormat outFormat = OutputFormat.createPrettyPrint();
			outFormat.setEncoding("UTF-8");
			outFormat.setTrimText(false);
			xmlWriter = new XMLWriter(new FileOutputStream(path),
					outFormat);
			xmlWriter.write(documen1);

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (xmlWriter != null)
				try {
					xmlWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
	public static boolean createDir(String destDirName) {  
		File dir = new File(destDirName);  
		if(dir.exists()) {  
			
			return true;  
		}  
		if(!destDirName.endsWith(File.separator))  
			destDirName = destDirName + File.separator;  

		if(dir.mkdirs()) {  			
			return true;  
		} else {  			
			return false;  
		}  
	}  

	
	
	
}
