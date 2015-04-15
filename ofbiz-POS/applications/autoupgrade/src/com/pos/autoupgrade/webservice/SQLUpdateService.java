package com.pos.autoupgrade.webservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.jdbc.SQLProcessor;
import org.ofbiz.service.DispatchContext;

public class SQLUpdateService {
	 public static String module = SQLUpdateService.class.getName();
	 //读取文件
	 public static void readFileByLines(DispatchContext dctx) {
		Delegator delegator = dctx.getDelegator();
		String systemDir = System.getProperty("ofbiz.home");
	 	File file = new File(systemDir+"/upgrade.sql");
	 	if (!file.exists()) {  // 如果不存在 返回
	 		return;
	 	}  
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            SQLProcessor du=new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz"));
            
            while ((tempString = reader.readLine()) != null) {
            	
                //执行sql
                if(!tempString.toUpperCase().startsWith("SELECT")){
                	try {
	                	du.prepareStatement(tempString);
	                	du.executeUpdate();
	                	du.commit();
                	}
                	catch(Exception ex){
                		du.rollback();
                		ex.printStackTrace();
                	}
                }
                line++;
            }
            reader.close();
            du.close();
            file.delete();//删除  
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                	
                }
            }
        }
    }
}
