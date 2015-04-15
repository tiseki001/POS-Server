package org.ofbiz.inventoryDetail.webService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastSet;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.inventory.webService.InventoryWebWork;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.util.ConvertUtil;

public class InventoryDetailWebWork {
	/*
	 * 查询盘点头信息
	 */
	public static BasePosObject findInventory(DispatchContext dctx,Map<String, Object> mapIn) {
		BasePosObject pObject =new BasePosObject();
		Delegator delegator = dctx.getDelegator();
		// 方法参数
		EntityCondition whereEntityCondition = null;// 条件
		Set<String> fieldsToSelect = FastSet.newInstance();// 要查询的列
		List<EntityCondition> andExpre = FastList.newInstance();// 添加条件值
		List<String> orderby = FastList.newInstance();// 排序
		orderby.add("updateDate DESC");
		if(Constant.isNotNull(mapIn.get("docStatus"))){
			andExpre.add(EntityCondition.makeCondition("docStatus",EntityOperator.EQUALS,mapIn.get("docStatus")));
		}
		if(Constant.isNotNull(mapIn.get("docId"))){
			andExpre.add(EntityCondition.makeCondition("docId",EntityOperator.LIKE,"%"+mapIn.get("docId")+"%"));
		}
		if(Constant.isNotNull(mapIn.get("productStoreId"))){
			andExpre.add(EntityCondition.makeCondition("productStoreId",EntityOperator.EQUALS,mapIn.get("productStoreId")));
		}
		if(Constant.isNotNull(mapIn.get("baseEntry"))){
			andExpre.add(EntityCondition.makeCondition("baseEntry",EntityOperator.LIKE,"%"+mapIn.get("baseEntry")+"%"));
		}
		if(Constant.isNotNull(mapIn.get("startDate"))){
			andExpre.add(EntityCondition.makeConditionWhere("UPDATE_DATE >='"+mapIn.get("startDate")+"'"));
		}
		if(Constant.isNotNull(mapIn.get("endDate"))){
			andExpre.add(EntityCondition.makeConditionWhere("UPDATE_DATE <='"+mapIn.get("endDate")+"'"));
		}
		if(andExpre.size()>0){
			whereEntityCondition = EntityCondition.makeCondition(andExpre,EntityOperator.AND);
		}
		try {
			List<GenericValue> list = delegator.findList("InventoryAndUser",whereEntityCondition, fieldsToSelect, orderby, null, false);
			pObject.setFlag(Constant.OK);
			pObject.setData(list);
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	/*
	 * 查询盘点明细信息
	 */
	public static BasePosObject findInventoryDetail(DispatchContext dctx,Map<String, Object> mapIn) {
		BasePosObject pObject =new BasePosObject();
		Delegator delegator = dctx.getDelegator();
		if(Constant.isNull(mapIn.get("docId"))){
			pObject.setFlag(Constant.NG);
			pObject.setMsg("未选择单号");
			return pObject;
		}
		try {
			List<GenericValue> list = delegator.findByAnd("InventoryDetailAndProductAndFacility", UtilMisc.toMap("docId", mapIn.get("docId")));
			pObject.setFlag(Constant.OK);
			pObject.setData(list);
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	/*
	 * 创建盘点信息
	 */
	public static BasePosObject createInventory(Map<String, Object> mapHeader,List<Map<String,Object>> itemList,DispatchContext dctx) {
		BasePosObject pObject =new BasePosObject();
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue invent =null;
			if(Constant.isNotNull(mapHeader.get("baseEntry"))){
				invent = delegator.findByPrimaryKey("InventoryCommand", UtilMisc.toMap("docId", mapHeader.get("baseEntry")));
			}
			if(invent!=null && (!invent.get("status").equals(Constant.NOTCLEARED)||!invent.get("docStatus").equals(Constant.VALID)) ){
				pObject.setFlag(Constant.NG);
				pObject.setMsg("该指令已清");
				return pObject;
			}
			mapHeader.put("docDate",UtilDateTime.nowTimestamp() );
			mapHeader.put("updateDate", UtilDateTime.nowTimestamp());
			if(Constant.isNotNull(mapHeader.get("printNum"))){
				mapHeader.put("printNum", Long.parseLong(mapHeader.get("printNum")+""));
			}
			if(Constant.isNotNull(mapHeader.get("approveDate"))){
				mapHeader.put("approveDate", ConvertUtil.convertStringToTimeStamp(mapHeader.get("approveDate")+""));
			}
			if(!mapHeader.get("docStatus").equals(Constant.DRAFT)){
				mapHeader.put("status", Constant.CLEAR);
			}
			GenericValue inventory =delegator.makeValidValue("Inventory", mapHeader);
			inventory.create();
			for(int i=0;i<itemList.size();i++){
				Map<String,Object> item =itemList.get(i);
				if(Constant.isNotNull(item.get("lineNo"))){
					item.put("lineNo", Long.parseLong(item.get("lineNo")+""));
				}
				if(Constant.isNotNull(item.get("baseLienNo"))){
					item.put("baseLienNo", Long.parseLong(item.get("baseLienNo")+""));
				}
				if(Constant.isNotNull(item.get("quantity"))){
					item.put("quantity", Long.parseLong(item.get("quantity")+""));
				}
				if(Constant.isNotNull(item.get("onHand"))){
					item.put("onHand", Long.parseLong(item.get("onHand")+""));
				}
				GenericValue inventoryDetail =delegator.makeValidValue("InventoryDetail", item);
				inventoryDetail.create();
			}
			//修改指令状态
			if(Constant.isNotNull(mapHeader.get("baseEntry"))){
				
				invent.set("status", Constant.CLEAR);
				invent.store();
			}
			pObject.setFlag(Constant.OK);
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	/*
	 * 修改盘点信息
	 */
	@SuppressWarnings("unused")
	public static BasePosObject updateInventory(Map<String, Object> mapHeader,List<Map<String,Object>> itemList,DispatchContext dctx) {
		BasePosObject pObject =new BasePosObject();
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue invent =null;
			if(Constant.isNotNull(mapHeader.get("baseEntry"))){
				invent = delegator.findByPrimaryKey("InventoryCommand", UtilMisc.toMap("docId", mapHeader.get("baseEntry")));
			}
			if(invent!=null && !invent.get("status").equals(Constant.NOTCLEARED)){
				pObject.setFlag(Constant.NG);
				pObject.setMsg("该指令已清");
				return pObject;
			}
			GenericValue inventory1=delegator.findByPrimaryKey("Inventory", UtilMisc.toMap("docId", mapHeader.get("docId")));
			if(!inventory1.get("docStatus").equals(Constant.DRAFT)){
				pObject.setFlag(Constant.NG);
				pObject.setMsg("只有草稿状态能修改");
				return pObject;
			}else{
				if(!mapHeader.get("docStatus").equals(Constant.DRAFT)){
					mapHeader.put("status", Constant.CLEAR);
				}
			}
			if(Constant.isNotNull(mapHeader.get("printNum"))){
				mapHeader.put("printNum", Long.parseLong(mapHeader.get("printNum")+""));
			}
			if(Constant.isNotNull(mapHeader.get("docDate"))){
				mapHeader.put("docDate", ConvertUtil.convertStringToTimeStamp(mapHeader.get("docDate")+""));
			}
			if(Constant.isNotNull(mapHeader.get("approveDate"))){
				mapHeader.put("approveDate", ConvertUtil.convertStringToTimeStamp(mapHeader.get("approveDate")+""));
			}
			GenericValue inventory =delegator.makeValidValue("Inventory", mapHeader);
			inventory.set("updateDate", UtilDateTime.nowTimestamp());
			if(inventory1==null){
				inventory.create();
			}
			inventory1.setFields(inventory);
			inventory1.store();
			delegator.removeByAnd("InventoryDetail", UtilMisc.toMap("docId", (String)mapHeader.get("docId")));
			for(int i=0;i<itemList.size();i++){
				Map<String,Object> item =itemList.get(i);
				if(Constant.isNotNull(item.get("lineNo"))){
					item.put("lineNo", Long.parseLong(item.get("lineNo")+""));
				}
				if(Constant.isNotNull(item.get("baseLienNo"))){
					item.put("baseLienNo", Long.parseLong(item.get("baseLienNo")+""));
				}
				if(Constant.isNotNull(item.get("quantity"))){
					item.put("quantity", Long.parseLong(item.get("quantity")+""));
				}
				if(Constant.isNotNull(item.get("onHand"))){
					item.put("onHand", Long.parseLong(item.get("onHand")+""));
				}
				GenericValue inventoryDetail =delegator.makeValidValue("InventoryDetail", item);
				inventoryDetail.create();
			}
			//修改指令状态
			if(Constant.isNotNull(mapHeader.get("baseEntry"))){
				invent.set("status", Constant.CLEAR);
				invent.store();
			}
			pObject.setFlag(Constant.OK);
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	/*
	 * 删除盘点
	 */
	public static BasePosObject deleteInventory(Map<String,Object> mapIn,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			delegator.removeByAnd("Inventory", UtilMisc.toMap("docId", (String)mapIn.get("docId")));
			delegator.removeByAnd("InventoryDetail", UtilMisc.toMap("docId", (String)mapIn.get("docId")));
			pObject.setFlag("S");
			pObject.setData("删除成功");
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
        	pObject.setMsg(e.getMessage());
        	return pObject;
		}
		return pObject;
	}
	/*
	 * 查询盘点指令
	 */
	public static BasePosObject findInventoryCommand(Map<String,Object> mapIn,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		// 方法参数
		EntityCondition whereEntityCondition = null;// 条件
		Set<String> fieldsToSelect = FastSet.newInstance();// 要查询的列
		List<EntityCondition> andExpre = FastList.newInstance();// 添加条件值
		List<String> orderby = FastList.newInstance();// 排序
		orderby.add("updateDate DESC");
		try {
			
			if(Constant.isNotNull(mapIn.get("docId"))){
				andExpre.add(EntityCondition.makeCondition("docId",EntityOperator.LIKE,"%"+mapIn.get("docId")+"%"));
			}
			if(Constant.isNotNull(mapIn.get("startDate"))){
				andExpre.add(EntityCondition.makeConditionWhere("DOC_DATE >= '"+mapIn.get("startDate")+"'"));
			}
			if(Constant.isNotNull(mapIn.get("endDate"))){
				andExpre.add(EntityCondition.makeConditionWhere("DOC_DATE < '"+mapIn.get("endDate")+"'"));
			}
			if(Constant.isNotNull(mapIn.get("status"))){
				andExpre.add(EntityCondition.makeCondition("status",EntityOperator.EQUALS,mapIn.get("status")));
			}
			if(Constant.isNotNull(mapIn.get("extDocNo"))){
				andExpre.add(EntityCondition.makeCondition("extDocNo",EntityOperator.LIKE,"%"+mapIn.get("extDocNo")+"%"));
			}
			if(andExpre.size()>0){
				whereEntityCondition = EntityCondition.makeCondition(andExpre,EntityOperator.AND);
			}
			List<GenericValue> list = delegator.findList("InventoryCommand", whereEntityCondition, fieldsToSelect, orderby, null, false);
			pObject.setFlag(Constant.OK);
			pObject.setData(list);
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg("查询失败");
			return pObject;
		}
		return pObject;
	}
	/*
	 * 查询盘点商店指令
	 */
	public static BasePosObject findInventoryCommandStore(Map<String,Object> mapIn,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		Map<String,Object> map = new HashMap<String,Object>();
		if(Constant.isNotNull(mapIn.get("docId"))){
			map.put("docId", mapIn.get("docId"));
		}
		if(Constant.isNotNull(mapIn.get("productStoreId"))){
			map.put("productStoreId", mapIn.get("productStoreId"));
		}
		try {
			List<GenericValue> list = delegator.findByAnd("CommandStoreAndCommand", map);
			pObject.setFlag(Constant.OK);
			pObject.setData(list);
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg("查询失败");
			return pObject;
		}
		return pObject;
	}
	/*
	 * 查询盘点仓库指令
	 */
	public static BasePosObject findInventoryCommandFacility(Map<String,Object> mapIn,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		Map<String,Object> map = new HashMap<String,Object>();
		if(Constant.isNotNull(mapIn.get("docId"))){
			map.put("docId", mapIn.get("docId"));
		}
		if(Constant.isNotNull(mapIn.get("facilityId"))){
			map.put("facilityId", mapIn.get("facilityId"));
		}
		try {
			List<GenericValue> list = delegator.findByAnd("CommandFacilityAndCommand", map);
			pObject.setFlag(Constant.OK);
			pObject.setData(list);
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg("查询失败");
			return pObject;
		}
		return pObject;
	}
	/*
	 * 查询盘点仓库指令
	 */
	@SuppressWarnings("unchecked")
	public static BasePosObject createInventoryDetailOther(Map<String,Object> mapIn,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<String> listQ = null;
		Map<String,List<String>> mapS = null;
		try {
			//根据docId查询inventoryCommand 的commandType 判断 是根据商品代码查还是根据串号查
			GenericValue iventoryCommand  = delegator.findByPrimaryKey("InventoryCommand", UtilMisc.toMap("docId", mapIn.get("docId")));
			if(UtilValidate.isNotEmpty(iventoryCommand)){
				if(UtilValidate.isEmpty(iventoryCommand.get("commandType"))){
					pObject.setFlag(Constant.NG);
					pObject.setMsg("commandType不能为空");
					return pObject;
				}
				String commandType = (String)iventoryCommand.get("commandType");
				//调用方法，根据指令类型获取商品代码或商品串号信息
				List<Map<String, Object>> mapList = InventoryWebWork.getInventoryCommandDetail(delegator);
				for(int i=0;i<mapList.size();i++){
					Map<String, Object> resultMap = mapList.get(i);
					if(resultMap.containsKey("dataQ")){
						listQ = (List<String>)mapList.get(i).get("dataQ");
					}
					if(resultMap.containsKey("dataS")){
					    mapS = (Map<String,List<String>>)mapList.get(i).get("dataS");
					}
				}
				if("1".equals(commandType)){
					//根据代码查询
					for(int i=0;i<listQ.size();i++){
						List<EntityCondition> andExpre = FastList.newInstance();// 添加条件值
						EntityCondition whereEntityCondition = null;// 条件
						andExpre.add(EntityCondition.makeCondition("productStoreId",EntityOperator.EQUALS,(String)mapIn.get("productStoreId")));
						andExpre.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,listQ.get(i)));
						//andExpre.add(EntityCondition.makeCondition("facilityId",EntityOperator.NOT_LIKE,"%T"));
						whereEntityCondition = EntityCondition.makeCondition(andExpre,EntityOperator.AND);
						List<GenericValue> Proist =delegator.findList("InventorynItemAndProductView", whereEntityCondition, null, null, null, false); 
						for(GenericValue gv:Proist){
							Map<String,Object> inventDeil= new HashMap<String,Object>();
							inventDeil.put("productId", gv.get("productId"));
							inventDeil.put("productName", gv.get("productName"));
							inventDeil.put("isSequence", gv.get("isSequence"));
							inventDeil.put("config", gv.get("config"));
							inventDeil.put("onhand", gv.get("onhand"));
							//inventDeil.put("brand", gv.get("brand"));
							//inventDeil.put("model", gv.get("model"));
							list.add(inventDeil);
						}
					}
					
				}else if("2".equals(commandType)){
					//根据串号查询
					for (String key : mapS.keySet()) {
						List<String> keyList = mapS.get(key);
						for(int i=0;i<keyList.size();i++){
							List<EntityCondition> andExpre = FastList.newInstance();// 添加条件值
							EntityCondition whereEntityCondition = null;// 条件
							//andExpre.add(EntityCondition.makeCondition("productStoreId",EntityOperator.EQUALS,(String)mapIn.get("productStoreId")));
							andExpre.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,key));
							andExpre.add(EntityCondition.makeCondition("sequenceId",EntityOperator.EQUALS,keyList.get(i)));

							//andExpre.add(EntityCondition.makeCondition("facilityId",EntityOperator.NOT_EQUAL,"NULL"));
							andExpre.add(EntityCondition.makeCondition("facilityId",EntityOperator.NOT_LIKE,"%"+(String)mapIn.get("productStoreId")+"%T"));
							whereEntityCondition = EntityCondition.makeCondition(andExpre,EntityOperator.AND);
							
							List<GenericValue> Proist =delegator.findList("InventorynItemAndProductSequenceView", whereEntityCondition, null, null, null, false); 
							
							if(UtilValidate.isEmpty(Proist)){
								GenericValue gv = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", key));
								
								Map<String,Object> inventDeil= new HashMap<String,Object>();
								inventDeil.put("productId", gv.get("productId"));
								inventDeil.put("productName", gv.get("productName"));
								inventDeil.put("isSequence", gv.get("isSequence"));
								inventDeil.put("sequenceId", keyList.get(i));
								inventDeil.put("config", gv.get("config"));
							    inventDeil.put("onhand",0);
								list.add(inventDeil);
								continue;
							}
							GenericValue gv = Proist.get(0);
							Map<String,Object> inventDeil= new HashMap<String,Object>();
							inventDeil.put("productId", gv.get("productId"));
							inventDeil.put("productName", gv.get("productName"));
							inventDeil.put("isSequence", gv.get("isSequence"));
							inventDeil.put("sequenceId", gv.get("sequenceId"));
							inventDeil.put("config", gv.get("config"));
							if(UtilValidate.isEmpty(gv.get("facilityId"))){
								inventDeil.put("onhand",0);
							}else{
								inventDeil.put("onhand",1);
							}
							list.add(inventDeil);
							
						}
						
					}
				}
				pObject.setFlag(Constant.OK);
				pObject.setData(list);
			}
			
			
			
			
			//获取商品Id List
//			List<String> productIdList =new ArrayList<String>();
//			productIdList.add("1000000000001");
//			productIdList.add("1000000000002");
//			productIdList.add("1000000000003");
//			productIdList.add("1000000000004");
			
			//获取仓库List
			//List<GenericValue> facilityObject=delegator.findByAnd("CommandFacilityAndCommand", UtilMisc.toMap("docId", mapIn.get("docId")));
			//if(facilityObject!=null){
			//	List<String> facilityList = new ArrayList<String>();
			//	for (GenericValue facility : facilityObject) {
			//		facilityList.add(facility.getString("facilityId"));
			//	}
				//获取库存List
				// 方法参数
//				EntityCondition whereEntityCondition = null;// 条件
//				Set<String> fieldsToSelect = FastSet.newInstance();// 要查询的列
//				List<EntityCondition> andExpre = FastList.newInstance();// 添加条件值
//				fieldsToSelect.add("onhand");
//				fieldsToSelect.add("productId");
//				fieldsToSelect.add("facilityId");
//				andExpre.add(EntityCondition.makeCondition("productId",EntityOperator.IN,productIdList));
//				//andExpre.add(EntityCondition.makeCondition("facilityId",EntityOperator.IN,facilityList));
//				whereEntityCondition = EntityCondition.makeCondition(andExpre,EntityOperator.AND);
//				List<GenericValue> inventItemList =delegator.findList("InventorynItem", whereEntityCondition, fieldsToSelect, null, null, false); 
//				//要返回的对象
//				List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
//				for(GenericValue inventoryItem:inventItemList){
//					for(GenericValue facility:facilityObject){
//						if(inventoryItem.get("facilityId").equals(facility.get("facilityId"))){
//							GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", inventoryItem.get("productId")));
//							Map<String,Object> inventDeil= new HashMap<String,Object>();
//							inventDeil.put("productId", product.get("productId"));
//							inventDeil.put("productName", product.get("productName"));
//							inventDeil.put("config", product.get("config"));
//							inventDeil.put("facilityId", facility.get("facilityId"));
//							inventDeil.put("facilityName", facility.get("facilityName"));
//							inventDeil.put("onhand", inventoryItem.get("onhand"));
//							list.add(inventDeil);
//						}
//					}
//				}	
//				pObject.setFlag(Constant.OK);
//				pObject.setData(list);
//			}
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			return pObject;
		}
		return pObject;
	}
	
	
}
