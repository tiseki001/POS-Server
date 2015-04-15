package org.ofbiz.party.party;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;



import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;

import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.util.*;

public class TestService {
	public static final String module = TestService.class.getName();
	public static final String resourceError = "PartyErrorUiLabels";
	
    public static Map<String,Object> findPersonByfirstName(DispatchContext dctx, Map context) {

        Delegator delegator = dctx.getDelegator();
        String firstName = (String)context.get("firstName");
        Map<String,Object> result = new HashMap<String,Object>();

        List<GenericValue> toBeStored = null;
        List<Object> idList = FastList.newInstance();

        
        try {

            EntityConditionList<EntityExpr> ecl = EntityCondition.makeCondition(
                    EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + firstName.toUpperCase() + "%"))
                    );
            toBeStored = delegator.findList("Person", ecl, null, UtilMisc.toList("lastName", "firstName", "partyId"), null, false);


            //toBeStored = delegator.findByAnd("Person", UtilMisc.toMap("firstName", firstName));

            Iterator<GenericValue> it = toBeStored.iterator();
            while(it.hasNext()){
                GenericValue p = (GenericValue)it.next();
                idList.add(UtilMisc.toMap("partyId", (String)p.get("partyId"), 
                                          "firstName", (String)p.get("firstName"),
                                          "lastName", (String)p.get("lastName")));
            }
            result.put("personList", idList);
        }catch(GenericEntityException e){
            return ServiceUtil.returnError("firstName #" + firstName + "not found!");
        }
                
        return result;
    }    

    public static Map<String,Object> findPersonByfirstNameJson(DispatchContext dctx, Map context) {

        Delegator delegator = dctx.getDelegator();
        String firstNameJson = (String)context.get("firstName");
        Map<String,Object> result = new HashMap<String,Object>();
        try {
        	
	        JSONObject jsonObj = new JSONObject();
	        jsonObj = JSONObject.fromObject(firstNameJson);
	        String firstName = jsonObj.get("firstName").toString();
	        
	
	        List<GenericValue> toBeStored = null;
	        List<Object> idList = FastList.newInstance();

       
        //try{
            EntityConditionList<EntityExpr> ecl = EntityCondition.makeCondition(
                    EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + firstName.toUpperCase() + "%"))
                    );
            toBeStored = delegator.findList("Person", ecl, null, UtilMisc.toList("lastName", "firstName", "partyId"), null, false);


            //toBeStored = delegator.findByAnd("Person", UtilMisc.toMap("firstName", firstName));

            Iterator<GenericValue> it = toBeStored.iterator();
            while(it.hasNext()){
                GenericValue p = (GenericValue)it.next();
                idList.add(UtilMisc.toMap("partyId", (String)p.get("partyId"), 
                                          "firstName", (String)p.get("firstName"),
                                          "lastName", (String)p.get("lastName")));
            }
            for (int i=0;i<10000;i++){
            	idList.add(UtilMisc.toMap("partyId", "partyId1", 
                        "firstName", "firstName1",
                        "lastName", "lastName1"));
            }
            String strResult = JSONUtil.listToJson(idList);
            
            result.put("personList", strResult);
        }catch(GenericEntityException e){
            return ServiceUtil.returnError("firstName #" + firstNameJson + "not found!");
        }
        catch(Exception e){
	    	e.printStackTrace();
	    }                
        return result;
    }    

    public static Map<String,Object> findSoap(DispatchContext dctx, Map context) {

		/**介绍如何结合soapui测试这个服务*/

		//return ServiceUtil.returnSuccess("成功运行TestSoap服务");
		Map<String, Object> result = new HashMap<String,Object>();//ServiceUtil.returnSuccess("成功运行TestSoap服务");
		result.putAll(context);
		return result;
	}
    
    public static Map<String, Object> createPersonS(DispatchContext ctx, Map<String, ? extends Object> context) {
        Map<String, Object> result = FastMap.newInstance();
        //Delegator delegator = ctx.getDelegator();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");
        String paramJson = (String)context.get("params");

        try {
        	
	        JSONObject jsonObj = new JSONObject();
	        jsonObj = JSONObject.fromObject(paramJson);
	        String partyId = jsonObj.get("partyId").toString();
	        String firstName = jsonObj.get("firstName").toString();
	        String lastName = jsonObj.get("lastName").toString();

	        dispatcher.runSync("createUpdatePerson", UtilMisc.toMap("partyId", partyId, "firstName", firstName, "lastName", lastName));
        } catch (GenericServiceException e) {
                Debug.logWarning(e.getMessage(), module);
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "person.update.write_failure", new Object[] { e.getMessage() }, locale));
        }

        result.put("results", ModelService.RESPOND_SUCCESS);
        /*result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
        result.put(ModelService.SUCCESS_MESSAGE, 
                UtilProperties.getMessage(resourceError, "person.update.success", locale));*/
        return result;
    }
}