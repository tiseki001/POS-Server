package org.ofbiz.inventory.webService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.common.QueryCommonWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.service.DispatchContext;

public class InventoryWebWork {
	/*
	 * 模糊查询
	 */
	public static BasePosObject operatingparameters(DispatchContext dctx,Map<String,Object> map) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		// 添加参数
		EntityCondition mainCond = null;// 条件
		List<String> orderBy = FastList.newInstance();// 条件值
		List<EntityCondition> andExprs = FastList.newInstance();
		
		if (Constant.isNotNull(map.get("productStoreId"))) {
			andExprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS,map.get("productStoreId")));
		}
		if (Constant.isNotNull(map.get("otherStore"))) {
			andExprs.add(EntityCondition.makeCondition("productStoreId",EntityOperator.IN, map.get("otherStore")));
			if (Constant.isNotNull(map.get("firstClass"))) {
				andExprs.add(EntityCondition.makeConditionWhere("T2.P1_PRODUCT_FEATURE_ID='"+map.get("firstClass")+"'"));
			} 
			if (Constant.isNotNull(map.get("secondClass"))) {
				andExprs.add(EntityCondition.makeConditionWhere("T3.P1_PRODUCT_FEATURE_ID='"+map.get("secondClass")+"'"));
			}
			if (Constant.isNotNull(map.get("brands"))) {
			    andExprs.add(EntityCondition.makeConditionWhere("T4.P1_PRODUCT_FEATURE_ID='"+map.get("brands")+"'"));
			}
			if (Constant.isNotNull(map.get("area"))) {
			    andExprs.add(EntityCondition.makeCondition("area",EntityOperator.IN, map.get("area")));
			}
		}
		if (Constant.isNotNull(map.get("facilityId"))) {
			andExprs.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS,map.get("facilityId")));
		}
		if (Constant.isNotNull(map.get("productId"))) {
			//andExprs.add(EntityCondition.makeCondition("productId", EntityOperator.LIKE,"%"+(String)map.get("productId")+"%"));
			andExprs.add(EntityCondition.makeCondition("productId", EntityOperator.LIKE,"%"+map.get("productId")+"%"));
			andExprs.add(EntityCondition.makeConditionWhere("PRO.REQUIRE_INVENTORY='"+Constant.ISSEQ+"'"));
		}
		if (Constant.isNotNull(map.get("productName"))) {
			andExprs.add(EntityCondition.makeCondition("productName", EntityOperator.LIKE,"%"+map.get("productName")+"%"));
			andExprs.add(EntityCondition.makeConditionWhere("PRO.REQUIRE_INVENTORY='"+Constant.ISSEQ+"'"));
		}
		if (Constant.isNotNull(map.get("sequenceId"))) {
			andExprs.add(EntityCondition.makeCondition("sequenceId", EntityOperator.LIKE,"%"+map.get("sequenceId")));
		}
		if (Constant.isNotNull(map.get("idValue"))) {
			andExprs.add(EntityCondition.makeCondition("idValue", EntityOperator.LIKE,"%"+map.get("idValue")+"%"));
		}
		
		//特征描述
		if (Constant.isNotNull(map.get("description"))) {
			andExprs.add(EntityCondition.makeCondition("pfdescription", EntityOperator.EQUALS,map.get("description")));
		}
		if(Constant.isNotNull(map.get("isInventory")) && map.get("isInventory").equals(Constant.ISSEQ)){
			andExprs.add(EntityCondition.makeCondition("onhand", EntityOperator.GREATER_THAN,Long.parseLong(String.valueOf(0))));
			orderBy.add("onhand DESC");
		}
		/*
		 * 判断需要调用的方法
		 */
		String viewName = "";
		if (map.get("mag").equals("findProductToInventory")) {
			viewName="InventorynItemAndProduct";
		} else if (map.get("mag").equals("findProductToInventoryOther")) {
			viewName="InventorynItemAndProduct";
		} else if (map.get("mag").equals("findFacility")) {
			viewName="FacilityAndProductStore";
		}  else if (map.get("mag").equals("InventorynItemAndProductStoreView")) {
			viewName="InventorynItemAndProductStoreView";
		} else if (map.get("mag").equals("findProductToSeq")) {
			viewName="InventorynItemAndProductAndSeq";
			andExprs.add(EntityCondition.makeConditionWhere("PRSE.FACILITY_ID = II.FACILITY_ID"));
		}
		
		if (andExprs.size() > 0) {
			mainCond = EntityCondition.makeCondition(andExprs,EntityOperator.AND);
		}
		try {
			TransactionUtil.begin();
			List<GenericValue> list = delegator.findList(viewName,mainCond, null, orderBy, null, false );
			pObject.setFlag(Constant.OK);
			if(list!=null&&list.size()>0){
				pObject.setData(list);
				
			}
			TransactionUtil.commit();
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
        	pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	/*
	 * 精确查询
	 */
	public static BasePosObject findInventoreItem(DispatchContext dctx,Map<String,Object> map) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<GenericValue> list1 = null;
		// 添加参数
		String viewName="InventorynItemAndProductAndMOve";
		EntityCondition mainCond = null;// 条件
		EntityCondition mainCond1 = null;// 条件
		List<EntityCondition> andExprs = FastList.newInstance();
		List<EntityCondition> andExprs1 = FastList.newInstance();
		if (Constant.isNotNull(map.get("productId"))) {
			andExprs.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,map.get("productId")));
			andExprs1.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,map.get("productId")));
			if (!Constant.isNotNull(map.get("isStock"))) {
				andExprs.add(EntityCondition.makeConditionWhere("PRO.REQUIRE_INVENTORY='"+Constant.ISSEQ+"'"));
			}
		}
		if (Constant.isNotNull(map.get("productName"))) {
			andExprs.add(EntityCondition.makeCondition("productName", EntityOperator.EQUALS,map.get("productName")));
			andExprs1.add(EntityCondition.makeCondition("productName", EntityOperator.EQUALS,map.get("productName")));
		}
		if (Constant.isNotNull(map.get("idValue"))) {
			andExprs.add(EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, map.get("idValue") ));
			andExprs1.add(EntityCondition.makeCondition("idValue", EntityOperator.EQUALS, map.get("idValue") ));
		}
		if (andExprs1.size() > 0) {
			mainCond1 = EntityCondition.makeCondition(andExprs1,EntityOperator.AND);
			try {
				list1 = delegator.findList("Product",mainCond1, null, null, null, false );
				if(UtilValidate.isNotEmpty(list1) && list1.size()==1){
					if("N".equals(list1.get(0).getString("requireInventory"))){
						pObject.setFlag(Constant.OK);
						pObject.setData(list1);
						return pObject;
					}
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		if (Constant.isNotNull(map.get("productId"))) {
			andExprs.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS,map.get("productId")));
			if (!Constant.isNotNull(map.get("isStock"))) {
				andExprs.add(EntityCondition.makeConditionWhere("PRO.REQUIRE_INVENTORY='"+Constant.ISSEQ+"'"));
			}
		}
		if (Constant.isNotNull(map.get("productStoreId"))) {
			andExprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS,map.get("productStoreId")));
		}
		if(Constant.isNotNull(map.get("movementTypeId"))){
			andExprs.add(EntityCondition.makeCondition("movementTypeId",EntityOperator.EQUALS, map.get("movementTypeId")));
		}
		if (Constant.isNotNull(map.get("sequenceId"))) {
			andExprs.add(EntityCondition.makeCondition("sequenceId", EntityOperator.EQUALS,map.get("sequenceId")));
			andExprs.add(EntityCondition.makeCondition("isOccupied", EntityOperator.NOT_EQUAL,Constant.ISSEQ));
			andExprs.add(EntityCondition.makeConditionWhere("PRSE.FACILITY_ID = II.FACILITY_ID"));
			viewName ="InventorynItemAndProductAndSeqAndMOve";
		}
		
		if(Constant.isNotNull(map.get("facilityId"))){
			andExprs.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, map.get("facilityId") ));
		}
		if(Constant.isNotNull(map.get("mag"))){
			if(map.get("mag").equals(Constant.ISSEQ)){
				andExprs.add(EntityCondition.makeConditionWhere("II.onhand < II.promise"));
			}else{
				andExprs.add(EntityCondition.makeCondition("onhand", EntityOperator.LESS_THAN,Long.parseLong(String.valueOf(0))));
			}
		}
		if (andExprs.size() > 0) {
			mainCond = EntityCondition.makeCondition(andExprs,EntityOperator.AND);
		}
		try {
			List<GenericValue> list = delegator.findList(viewName,mainCond, null, null, null, false );
			if(Constant.isNotNull(map.get("mag")) && map.get("mag").equals(Constant.ISSEQ)){
				if(list!=null&&list.size()>0){
					pObject.setFlag(Constant.NG);
					pObject.setMsg(list.get(0).get("productName")+"没有可用库存数");
					return pObject;
				}
			}
			pObject.setFlag(Constant.OK);
			if(list!=null&&list.size()>0){
				pObject.setData(list);
			}
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
        	pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	/*
	 * 修改库存承诺数
	 */
	public static BasePosObject updateInventorynItemToPromise(GenericValue mapIn,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			List<GenericValue> inventoryItemList =delegator.findByAnd("InventorynItem", UtilMisc.toMap("productId", mapIn.get("productId"),"facilityId", mapIn.get("facilityId")));
			if(inventoryItemList!=null && inventoryItemList.size()>0){
				GenericValue inventoryItem = inventoryItemList.get(0);
				if(mapIn.getLong("promise")==null){
					mapIn.put("promise", Long.parseLong(String.valueOf(0)));
				}
				if(inventoryItem.getLong("promise")==null){
					inventoryItem.set("promise", Long.parseLong(String.valueOf(0)));
				}
				inventoryItem.set("promise", inventoryItem.getLong("promise")+mapIn.getLong("promise"));
				if(inventoryItem.getLong("promise")<0){
					inventoryItem.set("promise", Long.parseLong(String.valueOf(0)));
				}
				inventoryItem.store();
//			}else{
//				BasePosObject inventObject=createInventoryItem(mapIn, dctx);
//				if(inventObject.getFlag().equals(Constant.NG)){
//					pObject.setFlag(Constant.NG);
//					pObject.setMsg(inventObject.getMsg());
//					return pObject;
//				}
			}
			
			pObject.setFlag(Constant.OK);
			pObject.setData("修改成功");
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	/*
	 * 修改库存数量
	 */
	public static BasePosObject updateInventorynItemToOnhead(GenericValue mapIn,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		pObject.setFlag(Constant.OK);
//		int inventoryOnhead=Integer.parseInt((String)inventoryItem.get("onhand"))-Integer.parseInt((String)deliveryItem.get("quantity")); 
		try {
			GenericValue product; 
			product = delegator.findByPrimaryKey("Product",UtilMisc.toMap("productId", mapIn.get("productId")));
			if (!Constant.ISSEQ.equals(product.get("requireInventory"))) {
				return pObject;
			}
			List<GenericValue> inventoryItemList =delegator.findByAnd("InventorynItem", UtilMisc.toMap("productId", mapIn.get("productId"),"facilityId", mapIn.get("facilityId")));
			if(inventoryItemList!=null && inventoryItemList.size()>0){
				GenericValue inventoryItem = inventoryItemList.get(0);
				if(mapIn.getLong("onhand")==null){
					mapIn.put("onhand", Long.parseLong(String.valueOf(0)));
				}
				if(inventoryItem.getLong("onhand")==null){
					inventoryItem.set("onhand", Long.parseLong(String.valueOf(0)));
				}
				String facilityId = mapIn.get("facilityId").toString();
				
				inventoryItem.set("onhand", inventoryItem.getLong("onhand")+mapIn.getLong("onhand"));
				if(inventoryItem.getLong("onhand")<0&&mapIn.getLong("onhand")<0){
					if(facilityId.endsWith("T")){
						inventoryItem.set("onhand", Long.parseLong(String.valueOf(0)));
						inventoryItem.store();
						pObject.setFlag(Constant.OK);
						pObject.setData("修改成功");
					}else{
						pObject.setFlag(Constant.NG);
						pObject.setMsg(mapIn.get("productId").toString()+"库存不足");
					}
					
				}else{
					inventoryItem.store();
					pObject.setFlag(Constant.OK);
					pObject.setData("修改成功");
				}
//			}else{
//				BasePosObject inventObject=createInventoryItem(mapIn, dctx);
//				if(inventObject.getFlag().equals(Constant.NG)){
//					pObject.setFlag(Constant.NG);
//					pObject.setMsg(inventObject.getMsg());
//					return pObject;
//				}
			}
			
			
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg("修改失败");
			e.printStackTrace();
		}
		return pObject;
	}
	/*
	 * 添加库存
	 
	public static BasePosObject createInventoryItem(Map<String,Object> mapIn, DispatchContext dctx){
		BasePosObject pObject = new BasePosObject();
		Delegator delegator = dctx.getDelegator();
		Map<String,Object> map = FastMap.newInstance();
		Map<String,Object> mapId = FastMap.newInstance();
		//生成ID
		mapId.put("type", "M");
		mapId.put("storeId", mapIn.get("productStoreId"));
		map.put("inventoryItemId", QueryCommonWorker.getOrderId(dctx,mapId));
		map.put("inventoryItemType", "SERIALIZED_INV_ITEM");
		map.put("onhand", mapIn.get("onhand"));
		map.put("promise", mapIn.get("promise"));
		map.put("unitCost", null);
		map.put("baseEntryId", mapIn.get("baseEntry"));
		map.put("baseEntryType", null);
		map.put("productStoreId", mapIn.get("productStoreId"));
		map.put("productId", mapIn.get("productId"));
		map.put("partyId", mapIn.get("partyId"));
		map.put("facilityId", mapIn.get("facilityId"));
		
		try {
			GenericValue inventoryItem =delegator.makeValidValue("InventorynItem", map);
			inventoryItem.create();
			pObject.setFlag(Constant.OK);
			return pObject;
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg("添加失败");
			e.printStackTrace();
		}
		return pObject;
	}*/
	/*
	 * 转换参数--根据头和明细，转换成中转库的方法
	 */
	public static BasePosObject conversionParameter(Map<String,Object> deliveryDoc,List<Map<String,Object>> listItem, DispatchContext dctx){
		BasePosObject pObject = new BasePosObject();
		Delegator delegator = dctx.getDelegator();
		List<Map<String,Object>> mapList = FastList.newInstance();
		/*
		 * (deliveryDoc)(from,to)
		 * movementTypeId,productStoreId,statusId,baseEntry,partyId,updateDate,updateUserId
		 * (deliveryItem)
		 * sequenceId,productId,onhand,facilityId
		 * 
		 * inventoryTransferId(自增)
		 * promise(根据指令的时候才会有)
		 * (自己获取)
		 * inventoryItemId,inventoryTransferTypeId
		 * 
		 */
		try {
			for (int i = 0; i < listItem.size(); i++) {
				Map<String,Object> deliveryItem = listItem.get(i);
				
				Map<String,Object> map = FastMap.newInstance();
				
				//是指令的话获取承诺数
				GenericValue deliveDoc = null;
				if(Constant.isNotNull(deliveryDoc.get("command"))){ 
					List<GenericValue> inventnTypeList = delegator.findByAnd("MoveTypeTransferTypeView", UtilMisc.toMap("movementTypeId", deliveryDoc.get("movementTypeId")));
					if(inventnTypeList!=null && inventnTypeList.size()>0){
						GenericValue inventnTransType=inventnTypeList.get(0);
						if(inventnTransType.get("baseEntity").equals(Constant.DELIVERYCOMMAND)){
//							deliveDoc = delegator.findByPrimaryKey(Constant.DELIVERYITEMCOMMAND, UtilMisc.toMap("docId", deliveryItem.get("docId"),"lineNo",deliveryItem.get("lineNo")));
							map.put("promise", deliveryItem.get("quantity"));
							map.put("onhand", deliveryItem.get("quantity"));
						}else if(inventnTransType.get("baseEntity").equals(Constant.RECEIVECOMMAND)){
							deliveDoc = delegator.findByPrimaryKey(Constant.RECEIVEITEMCOMMAND, UtilMisc.toMap("docId", deliveryItem.get("docId"),"lineNo",deliveryItem.get("lineNo")));
							map.put("promise", 0-Long.parseLong(deliveDoc.get("deliveryQuantity").toString()));
							map.put("onhand", deliveryItem.get("quantity"));
						}else if(inventnTransType.get("baseEntity").equals(Constant.SALEORDERHEADER)){
							deliveDoc = delegator.findByPrimaryKey(Constant.SALEORDERDTL, UtilMisc.toMap("docId", deliveryItem.get("docId"),"lineNo",deliveryItem.get("lineNo")));
							//map.put("promise",deliveDoc.get("quantity"));
							//map.put("onhand", deliveryItem.get("quantity"));
							map.put("onhand",deliveDoc.get("quantity"));
							map.put("promise", deliveryItem.get("quantity"));
						}else if(inventnTransType.get("baseEntity").equals(Constant.RETURNORDERHEADER)){
							deliveDoc = delegator.findByPrimaryKey(Constant.RETURNORDERDTL, UtilMisc.toMap("docId", deliveryItem.get("docId"),"lineNo",deliveryItem.get("lineNo")));
							//map.put("promise",0-Long.parseLong(deliveDoc.get("quantity").toString()));
							//map.put("onhand", deliveryItem.get("quantity"));
							map.put("onhand", Long.parseLong(deliveDoc.get("quantity").toString()));
							map.put("promise", deliveryItem.get("quantity"));
						}
					}
				} else {
					// 出库单是否根据指令，放入承诺数
					if (Constant.isNotNull(deliveryDoc.get("baseEntry"))) {
						map.put("promise", deliveryItem.get("quantity"));
					}
					map.put("onhand", deliveryItem.get("quantity"));
				}

				map.put("movementTypeId", deliveryDoc.get("movementTypeId"));
				map.put("statusId", deliveryDoc.get("statusId"));
				map.put("partyId", deliveryDoc.get("partyId"));
				map.put("updateDate", deliveryDoc.get("updateDate"));
				map.put("updateUserId", deliveryDoc.get("updateUserId"));
				map.put("baseEntry", deliveryDoc.get("baseEntry"));

				map.put("sequenceId", deliveryItem.get("sequenceId"));
				map.put("productId", deliveryItem.get("productId"));
				map.put("facilityId", deliveryItem.get("facilityId"));
				Object fromProductStoreId =null;
				Object toProductStoreId =null;
				Object fromInventoryItemId =null;
				Object toInventoryItemId =null;
				//获取库存id from -  to +
				Object fromFacilityId = null;
				Object toFacilityId = null;
				// 获取仓库类型id
				GenericValue inventoryTransferType = delegator.findByPrimaryKey("MovementType", UtilMisc.toMap("movementTypeId", deliveryDoc.get("movementTypeId")));
				Object inventoryTransferTypeId =null;
				if(inventoryTransferType!=null){ 
					inventoryTransferTypeId= inventoryTransferType.get("inventoryTransferTypeId");
					map.put("inventoryTransferTypeId", inventoryTransferTypeId);
					if(deliveryDoc.get("leixing") !=null && deliveryDoc.get("leixing").equals("delivery")){
						fromProductStoreId=deliveryDoc.get("productStoreId");
						toProductStoreId = deliveryDoc.get("productStoreIdTo");
						if(fromProductStoreId!=null){
							map.put("productStoreId", fromProductStoreId);
						}
						if(deliveryItem.get("facilityId")!=null){
							fromFacilityId =deliveryItem.get("facilityId");
						}else{
							List<GenericValue> fromFacilityList = delegator.findByAnd("FacilityAndStore", UtilMisc.toMap("productStoreId", deliveryDoc.get("productStoreId"),"facilityTypeId",inventoryTransferType.get("fromfacilityTypeId")));
							if (fromFacilityList!=null && fromFacilityList.size()>0) {
								fromFacilityId=fromFacilityList.get(0).get("facilityId");
							}
							if(fromFacilityId==null){
								pObject.setFlag(Constant.OK);
								return pObject;
							}
						}
						if(deliveryItem.get("facilityIdTo")!=null){
							toFacilityId =deliveryItem.get("facilityIdTo");
						}else{
							List<GenericValue> toFacilityList = delegator.findByAnd("ToFacilityAndStore", UtilMisc.toMap("productStoreId", deliveryDoc.get("productStoreId"),"facilityTypeId",inventoryTransferType.get("tofacilityTypeId")));
							if(toFacilityList!=null && toFacilityList.size()>0){
								toFacilityId =toFacilityList.get(0).get("facilityId");
							}
						}
						List<GenericValue> inventoryListTo = delegator.findByAnd("InventorynItem", UtilMisc.toMap("productStoreId", fromProductStoreId, "productId", deliveryItem.get("productId"),"facilityId",toFacilityId));
						if(inventoryListTo!=null && inventoryListTo.size()>0){
							GenericValue inventoryItem = inventoryListTo.get(0);
							toInventoryItemId = inventoryItem.get("inventoryItemId");
						}
					}
					if(deliveryDoc.get("leixing") !=null && deliveryDoc.get("leixing").equals("receive")){
						fromProductStoreId = deliveryDoc.get("productStoreIdFrom");
						toProductStoreId=deliveryDoc.get("productStoreId");
						if(toProductStoreId!=null){
							map.put("productStoreId", toProductStoreId);
						}
						if(deliveryItem.get("facilityId")!=null){
							toFacilityId=deliveryItem.get("facilityId");
						}else{
							List<GenericValue> toFacilityList = delegator.findByAnd("ToFacilityAndStore", UtilMisc.toMap("productStoreId", deliveryDoc.get("productStoreId"),"facilityTypeId",inventoryTransferType.get("tofacilityTypeId")));
							if (toFacilityList!=null && toFacilityList.size()>0) {
								toFacilityId=toFacilityList.get(0).get("facilityId");
							}
						}
						if(deliveryItem.get("facilityIdFrom")!=null){
							fromFacilityId =deliveryItem.get("facilityIdFrom");
						}else{
							List<GenericValue> toFacilityList = delegator.findByAnd("ToFacilityAndStore", UtilMisc.toMap("productStoreId", deliveryDoc.get("productStoreId"),"facilityTypeId",inventoryTransferType.get("fromfacilityTypeId")));
							if(toFacilityList!=null && toFacilityList.size()>0){
								fromFacilityId=toFacilityList.get(0).get("facilityId");
							}
						}
						List<GenericValue> inventoryListTo = delegator.findByAnd("InventorynItem", UtilMisc.toMap("productStoreId", toProductStoreId, "productId", deliveryItem.get("productId"),"facilityId",toFacilityId));
						if(inventoryListTo!=null && inventoryListTo.size()>0){
							GenericValue inventoryItem = inventoryListTo.get(0);
							toInventoryItemId = inventoryItem.get("inventoryItemId");
						}
					}
					List<GenericValue> inventoryList = delegator.findByAnd("InventorynItem", UtilMisc.toMap("productStoreId", fromProductStoreId, "productId", deliveryItem.get("productId"),"facilityId",fromFacilityId));
					if(inventoryList!=null && inventoryList.size()>0){
						GenericValue inventoryItem = inventoryList.get(0);
						fromInventoryItemId = inventoryItem.get("inventoryItemId");
					}
				}
				map.put("fromProductStoreId", fromProductStoreId);
				map.put("toProductStoreId", toProductStoreId);
				map.put("fromFacilityId", fromFacilityId);
				map.put("fromInventoryItemId", fromInventoryItemId);
				map.put("toInventoryItemId", toInventoryItemId);
				map.put("toFacilityId", toFacilityId);
				mapList.add(map);
			}
			deliveryDoc.remove("command");
			deliveryDoc.remove("leixing");
			pObject.setFlag(Constant.OK);
			pObject.setData(mapList);
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}

		return pObject;
	}
	/*
	 * 判断中转库添加后，库存是否超出
	 */
	public static BasePosObject panInventorynTransfer(Map<String,Object> inventorynTransfer,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		// 判断是否是库存
		GenericValue product; 
		try {
			product = delegator.findByPrimaryKey("Product",UtilMisc.toMap("productId", inventorynTransfer.get("productId")));
			if (product.get("requireInventory").equals(Constant.ISSEQ)) {
				Map<String, Object> map = FastMap.newInstance();
				//判断是否是自主调拨
				if(inventorynTransfer.get("baseEntry")!=null){
					map.put("mag", Constant.NOSEQ);
				}else{
					//自主调拨
					map.put("mag", Constant.ISSEQ);
				}
				map.put("productId", inventorynTransfer.get("productId"));
				map.put("facilityId", inventorynTransfer.get("facilityId"));
				BasePosObject pOb = findInventoreItem(dctx, map);
				if (pOb.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pOb.getMsg());
					return pObject;
				}
			}
			pObject.setFlag(Constant.OK);
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		
		return pObject;
	}
	/*
	 * 创建中转库数据
	 */
	public static BasePosObject createInventorynTransfer(List<Map<String, Object>> mapIn, DispatchContext dctx) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		Map<String,Object> mapId = FastMap.newInstance();
		
		try {
			for (int i = 0; i < mapIn.size(); i++) {
				Map<String, Object> inventTransfer = mapIn.get(i);
				
				Object fromProductStoreId=inventTransfer.get("fromProductStoreId");
				Object toProductStoreId =inventTransfer.get("toProductStoreId");
				Object fromInventoryItemId =inventTransfer.get("fromInventoryItemId");
				Object toInventoryItemId =inventTransfer.get("toInventoryItemId");
				Object fromFacilityId =inventTransfer.get("fromFacilityId");
				Object toFacilityId =inventTransfer.get("toFacilityId");
				inventTransfer.remove("fromProductStoreId");
				inventTransfer.remove("toProductStoreId");
				inventTransfer.remove("fromInventoryItemId");
				inventTransfer.remove("toInventoryItemId");
				inventTransfer.remove("fromFacilityId");
				inventTransfer.remove("toFacilityId");
				
				GenericValue moveType = delegator.findByPrimaryKey("MovementType", UtilMisc.toMap("movementTypeId",(String)inventTransfer.get("movementTypeId")));
				if(!moveType.get("inventoryTransferTypeId").equals(Constant.RETURNIN)){
					if(fromProductStoreId ==null){
						fromProductStoreId =inventTransfer.get("productStoreId");
					}
					inventTransfer.put("productStoreId", fromProductStoreId);
					GenericValue productStoreFrom = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", inventTransfer.get("productStoreId")));
					if(productStoreFrom!=null && !Constant.ISSEQ.equals(productStoreFrom.get("primaryStoreGroupId"))){
						if (Constant.isNotNull(fromFacilityId)){
							inventTransfer.put("inventoryItemId", fromInventoryItemId);
							inventTransfer.put("facilityId", fromFacilityId);
	//						if(inventTransfer.get("inventoryItemId")!=null){
								GenericValue inventorynTransfer = delegator.makeValue("InventorynTransfer", inventTransfer);
								mapId.clear();
								mapId.put("type", "T");
								mapId.put("storeId",fromProductStoreId);
								BasePosObject idPos =QueryCommonWorker.getOrderId(dctx,mapId);
								String docId = (String) idPos.getData();
								inventorynTransfer.set("inventoryTransferId", docId);
								if (inventTransfer.get("onhand")!=null) {
									inventorynTransfer.set("onhand", 0 - Long.parseLong(inventTransfer.get("onhand").toString()));
								}
								inventorynTransfer.set("promise", Long.parseLong(String.valueOf(0)));
								inventorynTransfer.set("isSync", Constant.ONE);
								inventorynTransfer.create();
								Thread.sleep(100);

	//						}
						
							// 判断是否是库存
							BasePosObject panObject = panInventorynTransfer(inventTransfer, dctx);
							if(panObject.getFlag().equals(Constant.NG)){
								pObject.setFlag(Constant.NG);
								pObject.setMsg(panObject.getMsg());
								return pObject;
							}
						}
					}
				}
				if(!moveType.get("inventoryTransferTypeId").equals(Constant.SALESOUT)){
					if(toProductStoreId ==null){
						toProductStoreId =inventTransfer.get("productStoreId");
					}
					inventTransfer.put("productStoreId", toProductStoreId);
					GenericValue productStoreTo = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", inventTransfer.get("productStoreId")));
					if(productStoreTo!=null && !Constant.ISSEQ.equals(productStoreTo.get("primaryStoreGroupId"))){
						if (Constant.isNotNull(toFacilityId)) {
							inventTransfer.put("inventoryItemId", toInventoryItemId);
							inventTransfer.put("facilityId", toFacilityId);
	//						if(inventTransfer.get("inventoryItemId")!=null){
								GenericValue inventorynTransfer = delegator.makeValue("InventorynTransfer", inventTransfer);
								mapId.clear();
								mapId.put("type", "T");
								mapId.put("storeId",toProductStoreId);
								BasePosObject idPos =QueryCommonWorker.getOrderId(dctx,mapId);
								String docId = (String) idPos.getData();
								inventorynTransfer.set("inventoryTransferId", docId);
								if (inventTransfer.get("onhand")!=null) {
									inventorynTransfer.set("onhand", Long.parseLong(inventTransfer.get("onhand").toString()));
								}else{
									inventorynTransfer.set("onhand", Long.parseLong(String.valueOf(0)));
								}
								inventorynTransfer.set("promise", Long.parseLong(String.valueOf(0)));
								inventorynTransfer.set("isSync", Constant.ONE);
								inventorynTransfer.create();
	//						}
							// 判断是否是库存
							BasePosObject panObject = panInventorynTransfer(inventTransfer, dctx);
							if(panObject.getData()!=null){
								pObject.setFlag(Constant.NG);
								pObject.setMsg(panObject.getMsg());
								return pObject;
							}
						}
					}
				}
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
	 * 指令创建中转库数据(占用)
	 */
	public static BasePosObject saveInventorynTransfer(List<Map<String, Object>> mapIn, DispatchContext dctx) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		Map<String,Object> mapId = FastMap.newInstance();
		try {
			for (int i = 0; i < mapIn.size(); i++) {
				Map<String, Object> inventTransfer = mapIn.get(i);
				Object inventotryId = inventTransfer.get("fromInventoryItemId");
				Object productStoreId =inventTransfer.get("productStoreId");
				inventTransfer.remove("fromProductStoreId");
				inventTransfer.remove("toProductStoreId");
				inventTransfer.remove("fromProductStoreId");
				inventTransfer.remove("toProductStoreId");
				inventTransfer.remove("fromInventoryItemId");
				inventTransfer.remove("toInventoryItemId");
				inventTransfer.remove("fromFacilityId");
				inventTransfer.remove("toFacilityId");
				
				GenericValue product = delegator.findByPrimaryKey("Product",UtilMisc.toMap("productId",(String) inventTransfer.get("productId")));
				if(product!=null){
					if(product.get("isSequence").equals(Constant.ISSEQ)){
					GenericValue productSeq = delegator.findByPrimaryKey("ProductSequence", UtilMisc.toMap("productId",(String)inventTransfer.get("productId"), "sequenceId",(String)inventTransfer.get("sequenceId")));
					if ( productSeq!=null) {
						if (productSeq.get("isOccupied").equals(Constant.ISSEQ)) {
							pObject.setFlag(Constant.NG);
							pObject.setMsg(product.get("productName")+"该串号商品已被占用");
							return pObject;
						} else {
							productSeq.set("isOccupied", Constant.ISSEQ);
							productSeq.set("isSync", Constant.ONE);
							productSeq.store();
						}
//					} else {
//						pObject.setFlag(Constant.NG);
//						pObject.setMsg(product.get("productName")+"是串号管理商品，但无串号");
//						return pObject;
					}
				}
				}else{
					pObject.setFlag(Constant.NG);
					pObject.setMsg("无此主商品");
					return pObject;
				}
				if(productStoreId!=null){
					inventTransfer.put("productStoreId", productStoreId);
				}else{
					pObject.setFlag(Constant.NG);
					pObject.setData("解析错误,没得到店面");
					return pObject;
				}
				GenericValue inventorynTransfer = delegator.makeValue("InventorynTransfer", inventTransfer);

				if (inventorynTransfer.get("facilityId")!=null) {
					inventorynTransfer.set("inventoryItemId", inventotryId);
					mapId.clear();
					mapId.put("type", "T");
					mapId.put("storeId",productStoreId);
					BasePosObject idPos =QueryCommonWorker.getOrderId(dctx,mapId);
					String docId = (String) idPos.getData();
					inventorynTransfer.set("inventoryTransferId", docId);
					inventorynTransfer.set("onhand",Long.parseLong(String.valueOf(0)));
					inventorynTransfer.set("promise", Long.parseLong(inventTransfer.get("promise").toString()));
					inventorynTransfer.set("isSync", Constant.ONE);
					inventorynTransfer.create();
				}
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
	 * 指令创建中转库数据(取消占用)
	 */
	public static BasePosObject delInventorynTransfer(List<Map<String, Object>> mapIn, DispatchContext dctx) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		Map<String,Object> mapId = FastMap.newInstance();
		try {
			for (int i = 0; i < mapIn.size(); i++) {
				Map<String, Object> inventTransfer = mapIn.get(i);
				Object inventotryId = inventTransfer.get("fromInventoryItemId");
				Object productStoreId =inventTransfer.get("productStoreId");
				inventTransfer.remove("fromProductStoreId");
				inventTransfer.remove("toProductStoreId");
				inventTransfer.remove("fromInventoryItemId");
				inventTransfer.remove("toInventoryItemId");
				inventTransfer.remove("fromFacilityId");
				inventTransfer.remove("toFacilityId");
				GenericValue product = delegator.findByPrimaryKey("Product",UtilMisc.toMap("productId",(String) inventTransfer.get("productId")));
				if(product!=null){
					if(product.get("isSequence").equals(Constant.ISSEQ)){
						GenericValue productSeq = delegator.findByPrimaryKey("ProductSequence", UtilMisc.toMap("productId",(String)inventTransfer.get("productId"), "sequenceId",(String)inventTransfer.get("sequenceId")));
						if (productSeq!=null) {
//							if (productSeq.get("isOccupied").equals(Constant.NOSEQ)) {
//								pObject.setFlag(Constant.NG);
//								pObject.setMsg(product.get("productName")+"该串号商品没有被占用");
//								return pObject;
//							} else {
								productSeq.set("isOccupied", Constant.NOSEQ);
								productSeq.set("isSync", Constant.ONE);
								productSeq.store();
//							}
//						} else {
//							pObject.setFlag(Constant.NG);
//							pObject.setMsg(product.get("productName")+"是串号管理商品，但无串号");
//							return pObject;
						}
					}
				}else{
					pObject.setFlag(Constant.NG);
					pObject.setMsg("无此主商品");
					return pObject;
				}
				if(productStoreId!=null){
					inventTransfer.put("productStoreId", productStoreId);
				}else{
					pObject.setFlag(Constant.NG);
					pObject.setMsg("解析错误,没得到店面");
					return pObject;
				}
				GenericValue inventorynTransfer = delegator.makeValue("InventorynTransfer", inventTransfer);

				if (inventorynTransfer.get("facilityId")!=null) {
					inventorynTransfer.set("inventoryItemId", inventotryId);
					mapId.clear();
					mapId.put("type", "T");
					mapId.put("storeId",productStoreId);
					BasePosObject idPos =QueryCommonWorker.getOrderId(dctx,mapId);
					String docId = (String) idPos.getData();
					inventorynTransfer.set("inventoryTransferId", docId);
					inventorynTransfer.set("onhand",Long.parseLong(String.valueOf(0)));
					inventorynTransfer.set("promise", 0-Long.parseLong(inventTransfer.get("promise").toString()));
					inventorynTransfer.set("isSync", Constant.ONE);
					inventorynTransfer.create();
				}
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
	 * 收货指令创建中转库
	 */
	public static BasePosObject saveReceInventorynTransfer(List<Map<String, Object>> mapIn, DispatchContext dctx) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		Map<String,Object> mapId = FastMap.newInstance();
		try {
			for (int i = 0; i < mapIn.size(); i++) {
				Map<String, Object> inventTransfer = mapIn.get(i);
				Object inventotryId = inventTransfer.get("fromInventoryItemId");
				Object productStoreId =inventTransfer.get("productStoreId");
				inventTransfer.remove("fromProductStoreId");
				inventTransfer.remove("toProductStoreId");
				inventTransfer.remove("fromInventoryItemId");
				inventTransfer.remove("toInventoryItemId");
				inventTransfer.remove("fromFacilityId");
				inventTransfer.remove("toFacilityId");
				GenericValue product = delegator.findByPrimaryKey("Product",UtilMisc.toMap("productId",(String) inventTransfer.get("productId")));
				if(product==null){
					pObject.setFlag(Constant.NG);
					pObject.setMsg("无此主商品");
					return pObject;
				}
				if(productStoreId!=null){
					inventTransfer.put("productStoreId", productStoreId);
				}else{
					pObject.setFlag(Constant.NG);
					pObject.setMsg("解析错误,没得到店面");
					return pObject;
				}
				GenericValue inventorynTransfer = delegator.makeValue("InventorynTransfer", inventTransfer);

				if (inventorynTransfer.get("facilityId")!=null) {
					inventorynTransfer.set("inventoryItemId", inventotryId);
					mapId.clear();
					mapId.put("type", "T");
					mapId.put("storeId",productStoreId);
					BasePosObject idPos =QueryCommonWorker.getOrderId(dctx,mapId);
					String docId = (String) idPos.getData();
					inventorynTransfer.set("inventoryTransferId", docId);
					inventorynTransfer.set("onhand", Long.parseLong(String.valueOf(0)));
					inventorynTransfer.set("promise", Long.parseLong(String.valueOf(0)));
					inventorynTransfer.set("isSync", Constant.ONE);
					inventorynTransfer.create();
				}
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
	 * 搜索帮助用的模糊查询
	 */
	public static BasePosObject findProductSeq(DispatchContext dctx,Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<EntityCondition> andExprs = FastList.newInstance();
		EntityCondition whereEntityCondition = null;//条件
		String viewName="ProductAndInventorynItem";
		try {
			if (Constant.isNotNull(view.get("productId"))) {
				andExprs.add(EntityCondition.makeCondition("productId",EntityOperator.LIKE,"%" + view.get("productId")));
			}
			if (Constant.isNotNull(view.get("idValue"))) {
				andExprs.add(EntityCondition.makeCondition("idValue",EntityOperator.LIKE,"%" + view.get("idValue")));
			}
			if (Constant.isNotNull(view.get("productName"))) {
				andExprs.add(EntityCondition.makeCondition("productName",EntityOperator.LIKE,"%" + view.get("productName") + "%"));
			}
			if (Constant.isNotNull(view.get("productStoreId"))) {
				andExprs.add(EntityCondition.makeCondition("productStoreId",EntityOperator.EQUALS, view.get("productStoreId")));
			}
			if (Constant.isNotNull(view.get("facilityId"))) {
				if(Constant.isNull(view.get("sequenceId"))){
					andExprs.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, view.get("facilityId")));
				}
			}else{
				pObject.setFlag(Constant.NG);
				pObject.setMsg("无仓库");
				return pObject;
			}
			if(Constant.isNotNull(view.get("sequenceId"))){
				if (Constant.isNotNull(view.get("facilityId"))) {
					andExprs.add(EntityCondition.makeCondition("facilityId",EntityOperator.EQUALS, view.get("facilityId")));
				}
				andExprs.add(EntityCondition.makeCondition("sequenceId",EntityOperator.LIKE,"%" + view.get("sequenceId")));
//				andExprs.add(EntityCondition.makeCondition("isOccupied"),EntityOperator.NOT_EQUAL,Constant.ISSEQ)));
				andExprs.add(EntityCondition.makeCondition("facilityId",EntityOperator.NOT_EQUAL, null));
				viewName="ProductAndIIAndIsSeq";
			}
//			andExprs.add(EntityCondition.makeCondition("onhand"),EntityOperator.GREATER_THAN,"promise")));
			whereEntityCondition = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
			
			List<GenericValue> list = delegator.findList(viewName,whereEntityCondition, null, null, null, false );
			pObject.setFlag(Constant.OK);
			if(list!=null&&list.size()>0){
				pObject.setData(list);
			}
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	
	/*
	 * 搜索查询2 
	 */
	public static BasePosObject findProductSeqGroup(DispatchContext dctx,Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<EntityCondition> andExprs = FastList.newInstance();
		List<EntityCondition> andExprs2 = FastList.newInstance();
		EntityCondition whereEntityCondition = null;//条件
		EntityCondition where2 = null;//条件
		String viewName="ProductAndInventorynItemGroup";
		try {
			if (Constant.isNotNull(view.get("productId"))) {
				andExprs.add(EntityCondition.makeCondition("productId",EntityOperator.LIKE,"%" + view.get("productId")));
			}
			if (Constant.isNotNull(view.get("idValue"))) {
				andExprs.add(EntityCondition.makeCondition("idValue",EntityOperator.LIKE,"%" + view.get("idValue")));
			}
			if (Constant.isNotNull(view.get("productName"))) {
				andExprs.add(EntityCondition.makeCondition("productName",EntityOperator.LIKE,"%" + view.get("productName") + "%"));
			}
			if (Constant.isNotNull(view.get("productStoreId"))) {
				andExprs.add(EntityCondition.makeCondition("productStoreId",EntityOperator.EQUALS, view.get("productStoreId")));
			}
			if(Constant.isNotNull(view.get("sequenceId"))){
				andExprs2.add(EntityCondition.makeCondition("sequenceId",EntityOperator.LIKE, "%"+view.get("sequenceId")));
				andExprs2.add(EntityCondition.makeCondition("facilityId",EntityOperator.NOT_LIKE,"%T"));
				andExprs2.add(EntityCondition.makeCondition("facilityId",EntityOperator.NOT_EQUAL,"NULL"));
				andExprs2.add(EntityCondition.makeCondition("facilityId",EntityOperator.LIKE,view.get("productStoreId")+"%"));
				where2 = EntityCondition.makeCondition(andExprs2, EntityOperator.AND);
				List<GenericValue> pList = delegator.findList("ProductSequence",where2, null, null, null, false );
				if(UtilValidate.isNotEmpty(pList)){
					List<Map<String,Object>> listObje = new ArrayList<Map<String,Object>>();
					for(GenericValue gv:pList ){
						andExprs.add(EntityCondition.makeCondition("productId",EntityOperator.LIKE,"%" + gv.get("productId")));
						whereEntityCondition = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
						List<GenericValue> list = delegator.findList("ProductAndInventorynItemGroup",whereEntityCondition, null, null, null, false );
						for(int i=0;i<list.size();i++){
							Map<String,Object> map = FastMap.newInstance();
							map.put("sequenceId", (String)gv.get("sequenceId"));
							map.put("productId",list.get(i).get("productId"));
							map.put("productName",list.get(i).get("productName"));
							map.put("isSequence",list.get(i).get("isSequence"));
							map.put("config",list.get(i).get("config"));
							map.put("onhand",list.get(i).get("onhand"));
							map.put("idValue",list.get(i).get("idValue"));
							listObje.add(map);
						}
					}
				    pObject.setData(listObje);
				}
				pObject.setFlag(Constant.OK);
				return pObject;
			}
			whereEntityCondition = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
			List<GenericValue> list = delegator.findList(viewName,whereEntityCondition, null, null, null, false );
			pObject.setFlag(Constant.OK);
			if(list!=null&&list.size()>0){
				pObject.setData(list);
			}
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	/**
	 * create deliverCommend lock ProductSeq
	 * @param delegator
	 * @param productId
	 * @param seq
	 */
	public static void lockProductSeq(Delegator delegator, String productId, String seq) {
		GenericValue productSeq = null;
		if (UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(seq)) {
			try {
				productSeq = delegator.findByPrimaryKey("ProductSequence", UtilMisc.toMap("productId", productId, "sequenceId", seq));
				if (UtilValidate.isEmpty(productSeq)) {
					productSeq = delegator.makeValue("ProductSequence", UtilMisc.toMap("productId", productId, 
							"sequenceId", seq,
							"partyId", "10000",
							"isSync", "1",
							"isOuts", "N",
							"isOccupied", "Y"));
					productSeq.create();
				}else {
					productSeq.set("isOccupied", "Y");
					productSeq.store();
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * create receiveItem 
	 * 修改串号库存
	 * @param delegator
	 * @param productId
	 * @param seq
	 */
	public static void updateProductSeq(Delegator delegator, String productId, String seq, String facilityId) {
		GenericValue productSeq = null;
		if (UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(seq)) {
			try {
				productSeq = delegator.findByPrimaryKey("ProductSequence", 
						UtilMisc.toMap("productId", productId, "sequenceId", seq));
				if (UtilValidate.isNotEmpty(productSeq)) {
					if (UtilValidate.isNotEmpty(productSeq.get("facilityId"))
							&& productSeq.get("facilityId").toString().indexOf("T") != -1) {
						productSeq.set("facilityId", facilityId);
						productSeq.store();
					}
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 查询销售区域门店信息
	 * @param delegator
	 * @param productStoreId
	 */
	public static BasePosObject findAreaStore(DispatchContext dctx,Map<String, Object> jsonObj) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<GenericValue> storeList = null;
		EntityCondition mainCond = null;// 条件
		EntityFindOptions findOptions = new EntityFindOptions();
		findOptions.setDistinct(true);
		List<EntityCondition> andExprs = FastList.newInstance();
		try {
			if (UtilValidate.isNotEmpty(jsonObj.get("productStoreId"))) {
				try {
					andExprs.add(EntityCondition.makeConditionWhere("SAI.PRODUCT_STORE_ID='"+jsonObj.get("productStoreId")+"'"));
					mainCond = EntityCondition.makeCondition(andExprs,EntityOperator.AND);
					
					storeList = delegator.findList("StoreAreaAndAttribute", mainCond, null, null, findOptions, false);
					if (UtilValidate.isEmpty(storeList)) {
						Set<String> fieldsToSelect = FastSet.newInstance();
						fieldsToSelect.add("productStoreId");
						fieldsToSelect.add("storeName");
						storeList = delegator.findList("ProductStore", null, fieldsToSelect, null, null, false);
					}
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
			pObject.setFlag(Constant.OK);
			if(storeList!=null&&storeList.size()>0){
				pObject.setData(storeList);
			}
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
        	pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
		
	}
	
	
	/**
	 * getInventoryCommandDetail
	 * 盘点指令获取数据方法
	 * @param delegator
	 */
	public static List<Map<String, Object>> getInventoryCommandDetail(Delegator delegator) {
		List<Map<String, Object>> result = FastList.newInstance();
		List<Object> list = null;
		Map<String, Object> resultMap = FastMap.newInstance();
		Map<Object, Object> sMap = FastMap.newInstance();
		
		List<GenericValue> gvs = null;
		try {
			//按数量盘点
			gvs = delegator.findList("InventoryCommandAndProductView", EntityCondition.makeCondition("commandType", "1"), null, null, null, false);
			list = FastList.newInstance();
			for (GenericValue gv : gvs) {
				list.add(gv.get("productId"));
			}
			resultMap.put("dataQ", list);
			//按串号盘点
			gvs = delegator.findList("InventoryCommandAndProductView", EntityCondition.makeCondition("commandType", "2"), null, null, null, false);
			for (GenericValue gv : gvs) {
				gvs = delegator.findList("InventoryCommandAndProductView", EntityCondition.makeCondition("productId", gv.get("productId")), null, null, null, false);
				list = FastList.newInstance();
				for (GenericValue seqs : gvs) {
					list.add(seqs.get("sequenceId"));
				}
				sMap.put(gv.get("productId"), list);
			}
			resultMap.put("dataS", sMap);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result.add(resultMap);
		return result;
	}

}
