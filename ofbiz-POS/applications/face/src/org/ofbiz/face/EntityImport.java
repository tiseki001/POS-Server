package org.ofbiz.face;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javolution.util.FastMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelReader;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.util.JSONUtil;

public class EntityImport {
	public static Map<String, Object> importEntity(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> resultJson = FastMap.newInstance();

		String jsonEntities = (String) context.get("default");
		JSONObject entitiesObject = JSONObject.fromObject(jsonEntities);
		Set<String> entityNames = entitiesObject.keySet();
		String jsonEntity = "";
		JSONArray jsonEntityArray = null;
		GenericValue entityValue = null;
		JSONObject object = null;
		String entityName = "";
		try {
			for (String key : entityNames) {

				jsonEntities = entitiesObject.get(key) == null ? ""
						: entitiesObject.get(key).toString();
				// JSONObject.toBean(jsonEntity);
				jsonEntityArray = JSONArray.fromObject(jsonEntities);

				for (Object jsonEntity1 : jsonEntityArray) {
					jsonEntity = jsonEntity1.toString();
					object = JSONObject.fromObject(jsonEntity);
				

					importEntityOne(key, object, delegator);

				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			resultJson.put("jsonOut", e.getMessage());
			e.printStackTrace();
			return resultJson;
		}
		resultJson.put("jsonOut", "S");
		return resultJson;
	}
	
	public static Map<String, Object> getAllEntity(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> resultJson = FastMap.newInstance();
		Set<String> entityNames = context.keySet();
		
		ModelReader reader = delegator.getModelReader();
		try{
		entityNames = new TreeSet(reader.getEntityNames());
		List<String> entitylist = new ArrayList<String>(entityNames);  
		String strResult = JSONUtil.listToJson(entitylist);
		resultJson.put("outJsonParam", strResult);
		}catch(Exception e){
			e.printStackTrace();
		}
		return resultJson;
	}

	// public static Map<String, Object> getEntityFeild(DispatchContext dctx,
	// Map<String, ? extends Object> context) {
	// Delegator delegator = dctx.getDelegator();
	// LocalDispatcher dispatcher = dctx.getDispatcher();
	// Map<String, Object> resultJson = FastMap.newInstance();
	// String entityName =
	// context.get("entity")==null?"":(String)context.get("entity");
	// ModelEntity entityModel = delegator.getModelEntity(entityName);
	// JSONObject jsonEntity = JSONUtil.bean2Json(entityModel);
	// String json = jsonEntity.toString();
	// resultJson.put("outJsonParam", strResult);
	// return resultJson;
	// }

	public static void importEntityOne(String entityName,
			Map<String, Object> entityValue, Delegator delegator)
			throws GenericEntityException {
		ModelEntity entityModel = delegator.getModelEntity(entityName);
		Set<String> feildNames = entityValue.keySet();
		Map<String, Object> entityValueC = FastMap.newInstance();
		for (String key : feildNames) {
			if(UtilValidate.isNotEmpty(entityValue.get(key))&&!"".equals(entityValue.get(key).toString()))
					entityValueC.put(key, entityModel.convertFieldValue(key,
							entityValue.get(key), delegator));
		}

		// entityModel.getField("").
		List<String> pks = entityModel.getPkFieldNames();
		Map<String, Object> pkMap = FastMap.newInstance();
		for (String pk : pks) {
			if (!UtilValidate.isNotEmpty(entityValueC.get(pk))
					|| entityValueC.get(pk).toString().equals(""))
				return;
			pkMap.put(pk, entityValueC.get(pk));

		}

		GenericValue entity;

		entity = delegator.findByPrimaryKey(entityName, pkMap);

		if (!UtilValidate.isNotEmpty(entity)) {
			entity = delegator.makeValue(entityName, entityValueC);
			entity.create();
		} else {
			entity = delegator.makeValue(entityName, entityValueC);
			entity.store();
		}

	}
}
