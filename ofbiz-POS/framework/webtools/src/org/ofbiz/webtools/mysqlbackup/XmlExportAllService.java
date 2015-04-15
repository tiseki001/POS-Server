package org.ofbiz.webtools.mysqlbackup;

import java.util.Map;

import javolution.util.FastMap;
import net.sf.json.JSONSerializer;

import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ModelService;

public class XmlExportAllService {
public static final String module = XmlExportAllService.class.getName();
	public static final String resource = "XmlExportAllServiceUiLabels";
    public static final String resourceError = "XmlExortAllServiceErrorUiLabels";
	
    public static Map<String, Object> xmlExportAll(DispatchContext dctx, Map<String, ? extends Object> context) {    	 
    	Map<String,Object> results = FastMap.newInstance();
    	BasePosObject pObject = null;
    	 String strResult = "";
    	 try{
    		 pObject  =XmlExportAllWrok.XmlExportAll(dctx, context);
    		 strResult = JSONSerializer.toJSON(pObject).toString();	
		results.put("result", strResult);
		if(pObject.getFlag().equals(Constant.OK)){
			results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		}else{
			results.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
		}
		
	 	}catch(Exception e){
	 		e.printStackTrace();		
	 	}
     return results;

	 }
    }



