package org.ofbiz.delivery.webService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.UtilDateTime;
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
import org.ofbiz.inventory.webService.InventoryWebWork;
import org.ofbiz.receive.webService.ReceiveWebWork;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.util.ConvertUtil;

public class DeliveryWebWork {
	/*
	 * 查询固件类型
	 */
	public static BasePosObject findFaultType(DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			List<GenericValue> list = delegator.findList("FaultType", null, null, null, null, false);
			pObject.setFlag(Constant.OK);
			if(list!=null && list.size()>0){
				pObject.setData(list);
			}
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG); 
			pObject.setMsg(e.getMessage());
			return pObject; 
		}
		return pObject; 
		
	}
	/*
	 * 查询移动类型
	 */
	public static BasePosObject findMovementType(DispatchContext dctx,Map<String,Object> mapIn){
		EntityCondition whereEntityCondition = null;
		Set<String> fieldsToSelect = FastSet.newInstance();// 条件值
    	List<EntityCondition> andExprs = FastList.newInstance();// 添加条件值
    	if(Constant.isNotNull(mapIn.get("orderType"))){
    		andExprs.add(EntityCondition.makeCondition("orderType", EntityOperator.EQUALS,(String)mapIn.get("orderType")));
    	}
    	if (andExprs.size() > 0){
        	//mainCond = EntityCondition.makeConditionWhere(jsonStr);
			whereEntityCondition = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
        }
    	BasePosObject pObject  = findDelivery(dctx, "MovementTypeView", whereEntityCondition, fieldsToSelect,null);
    	return pObject;
	}
	/*
	 * 根据店面跟移动类型获取仓库
	 */
	public static BasePosObject findFacility(DispatchContext dctx,Map<String,Object> mapIn){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			List<GenericValue> facilityList = delegator.findByAnd("FacilityAndStoreAndMove", UtilMisc.toMap("productStoreId", (String)mapIn.get("productStoreId"),"movementTypeId",(String)mapIn.get("movementTypeId")));
			pObject.setFlag(Constant.OK);
			if(facilityList!=null && facilityList.size()>0){
				pObject.setData(facilityList);
			}
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG); 
			pObject.setMsg(e.getMessage());
			return pObject;
			
		}
		
		return pObject;
	}
	/*
	 * 查询发货指令头信息deliveryCommand
	 */
	public static BasePosObject findDeliveryCommand(Map<String,Object> mapIn,DispatchContext dctx) {
		// 方法参数
		EntityCondition whereEntityCondition = null;//条件
		Set<String> fieldsToSelect = FastSet.newInstance();// 要查询的列
		List<EntityCondition> andExprs = FastList.newInstance();// 添加条件值
		List<String> orderBy = FastList.newInstance();
		orderBy.add("updateDate DESC");
		if (Constant.isNotNull(mapIn.get("docId"))) {
			andExprs.add(EntityCondition.makeCondition("docId", EntityOperator.EQUALS, (String)mapIn.get("docId")));
		}
		List<Object> listDate = FastList.newInstance();
		if(Constant.isNotNull(mapIn.get("startDate"))){
			listDate.add(ConvertUtil.convertStringToTimeStamp(mapIn.get("startDate")+""));
		}
		if (Constant.isNotNull(mapIn.get("endDate"))) {
			listDate.add(ConvertUtil.convertStringToTimeStamp(mapIn.get("endDate")+""));
		}
		if(listDate.size()>0){
			andExprs.add(EntityCondition.makeCondition("docDate", EntityOperator.BETWEEN, listDate));
		}
		if(Constant.isNotNull(mapIn.get("status"))){
			andExprs.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS,(String)mapIn.get("status")));
		}
		if(Constant.isNotNull(mapIn.get("orderType"))){
    		andExprs.add(EntityCondition.makeCondition("orderType", EntityOperator.EQUALS, (String)mapIn.get("orderType")));
    	}
		if(Constant.isNotNull(mapIn.get("productStoreId"))){
    		andExprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, (String)mapIn.get("productStoreId")));
    	}
		//获取状态
//		List<String> docStatus = new ArrayList<String>();
//		docStatus.add("0");//
//		docStatus.add("1");//
//		andExprs.add(EntityCondition.makeCondition("docStatus", EntityOperator.EQUALS, "1"));
		if (andExprs.size() > 0){
        	//mainCond = EntityCondition.makeConditionWhere(jsonStr);
			whereEntityCondition = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
        }
		
		BasePosObject pObject  = findDelivery(dctx, "DeliveryCommandAndProductStore", whereEntityCondition, fieldsToSelect,orderBy);
		return pObject;
	}
	/*
	 * 查询发货指令明细信息deliveryItemCommand
	 */
	public static BasePosObject findDeliveryItemcommand(Map<String,Object> mapIn,DispatchContext dctx){
		// 方法参数
		EntityCondition whereEntityCondition = null;//条件
		Set<String> fieldsToSelect = FastSet.newInstance();// 要查询的列
//		List<String> orderBy = FastList.newInstance();
		List<EntityCondition> andExprs = FastList.newInstance();// 添加条件值
		andExprs.add(EntityCondition.makeCondition("docId", EntityOperator.EQUALS,(String) mapIn.get("docId")));
//		andExprs.add(EntityCondition.makeCondition("quantity"),EntityOperator.GREATER_THAN,"quantity")));
//		orderBy.add("lineNo");
		if (andExprs.size() > 0){
        	//mainCond = EntityCondition.makeConditionWhere(jsonStr);
			whereEntityCondition = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
        }
		BasePosObject pObject  = findDelivery(dctx,"DeliveryItemcommandAndProductStore", whereEntityCondition, fieldsToSelect,null);
		return pObject;
	}
	/*
	 * 查询状态
	 */
	public static BasePosObject findDllConstant(Map<String,Object> mapIn,DispatchContext dctx){
		// 方法参数DllConstant
		EntityCondition whereEntityCondition = null;//条件
		Set<String> fieldsToSelect = FastSet.newInstance();// 要查询的列
		List<EntityCondition> andExprs = FastList.newInstance();// 添加条件值
		andExprs.add(EntityCondition.makeCondition("storeId", EntityOperator.EQUALS, (String)mapIn.get("storeId")));
		whereEntityCondition = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
		BasePosObject pObject  = findDelivery(dctx,"DllConstantView", whereEntityCondition, fieldsToSelect,null);
		return pObject;
	}
	/*
	 * 查询发货单头信息
	 */
	public static BasePosObject findDeliveryDoc(Map<String,Object> mapIn,DispatchContext dctx){
		// 方法参数
		EntityCondition whereEntityCondition = null;//条件
		Set<String> fieldsToSelect = FastSet.newInstance();// 要查询的列
		List<EntityCondition> andExprs = FastList.newInstance();// 添加条件值
		List<String> orderBy = FastList.newInstance();
		orderBy.add("updateDate DESC");//倒序
		if (Constant.isNotNull(mapIn.get("docId"))) {
			andExprs.add(EntityCondition.makeCondition("docId", EntityOperator.EQUALS,(String) mapIn.get("docId")));
		}
		if (Constant.isNotNull(mapIn.get("baseEntry"))) {
			andExprs.add(EntityCondition.makeCondition("baseEntry", EntityOperator.EQUALS, (String)mapIn.get("baseEntry")));
		}
		if (Constant.isNotNull(mapIn.get("productStoreIdTo"))) {
			andExprs.add(EntityCondition.makeCondition("productStoreIdTo", EntityOperator.EQUALS, (String)mapIn.get("productStoreIdTo")));
		}
		if (Constant.isNotNull(mapIn.get("productStoreId"))) {
			andExprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS,(String) mapIn.get("productStoreId")));
		}
		if (Constant.isNotNull(mapIn.get("docStatus"))) {
			andExprs.add(EntityCondition.makeCondition("docStatus", EntityOperator.EQUALS, (String)mapIn.get("docStatus")));
		}
		if(Constant.isNotNull(mapIn.get("orderType"))){
    		andExprs.add(EntityCondition.makeCondition("orderType", EntityOperator.EQUALS, (String)mapIn.get("orderType")));
    	}
		/*
		if(Constant.isNotNull(mapIn.get("orderType"))){
			BasePosObject moveType = findMovementType(dctx, mapIn);
			List<Object> moveTypeList=new ArrayList<Object>();
			if(moveType!=null && moveType.getData()!=null){
				List<GenericValue> list =(List<GenericValue>) moveType.getData();
				for (GenericValue genericValue : list) {
					moveTypeList.add(genericValue.get("movementTypeId"));
				}
			}
			andExprs.add(EntityCondition.makeCondition("movementTypeId"), EntityOperator.IN, moveTypeList));
		}
		*/
		List<Object> listDate = FastList.newInstance();
		if(Constant.isNotNull(mapIn.get("startDate"))){
			listDate.add(ConvertUtil.convertStringToTimeStamp(mapIn.get("startDate")+""));
		}
		if (Constant.isNotNull(mapIn.get("endDate"))) {
			listDate.add(ConvertUtil.convertStringToTimeStamp(mapIn.get("endDate")+""));
		}
		if(listDate.size()>0){
			andExprs.add(EntityCondition.makeCondition("docDate", EntityOperator.BETWEEN, listDate));
		}
		if (andExprs.size() > 0){
        	//mainCond = EntityCondition.makeConditionWhere(jsonStr);
			whereEntityCondition = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
        }
		BasePosObject pObject  = findDelivery(dctx,"DeliveryDocAndProductStore", whereEntityCondition, fieldsToSelect,orderBy);
		return pObject;
	}
	/*
	 * 查询发货单明细deliveryItem
	 */
	public static BasePosObject findDeliveryItem(Map<String,Object> mapIn,DispatchContext dctx) {
		// 方法参数
		EntityCondition whereEntityCondition = null;//条件
		Set<String> fieldsToSelect = FastSet.newInstance();// 要查询的列
		List<EntityCondition> andExprs = FastList.newInstance();// 添加条件值
		andExprs.add(EntityCondition.makeCondition("docId", EntityOperator.EQUALS, (String)mapIn.get("docId")));
		if (andExprs.size() > 0){
			whereEntityCondition = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
        }
		List<String> orderBy = FastList.newInstance();
		orderBy.add("lineNo");
		BasePosObject pObject  = findDelivery(dctx,"DeliveryItemAndProduct", whereEntityCondition, fieldsToSelect,orderBy);
		return pObject;
	}
	/*
	 *公用查询方法
	 */
	public static BasePosObject findDelivery(DispatchContext dctx,String viewName,EntityCondition whereEntityCondition,Set<String> fieldsToSelect ,List<String> orderBy){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			List<GenericValue> list = delegator.findList(viewName,whereEntityCondition, fieldsToSelect, orderBy, null, false );
			pObject.setFlag("S");
			if(list!=null && list.size()>0){
				pObject.setData(list);
			}else{
				if(viewName.equals("DllConstantView")){
					 List<EntityCondition> andExprs = FastList.newInstance();// 添加条件值
					 andExprs.add(EntityCondition.makeCondition("storeId", EntityOperator.EQUALS, "default"));
					 whereEntityCondition = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
					 list = delegator.findList(viewName,whereEntityCondition, fieldsToSelect, orderBy, null, false );
					 if(list!=null && list.size()>0){
							pObject.setData(list);
					 }
				}
			}
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
        	pObject.setMsg(e.getMessage());
        	return pObject;
		}
		return pObject;
	}
	/*
	 * 删除发货单
	 */
	public static BasePosObject deleteDeliveryCommand(Map<String,Object> mapIn,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			delegator.removeByAnd("DeliveryDoc", UtilMisc.toMap("docId", (String)mapIn.get("docId")));
			delegator.removeByAnd("DeliveryItem", UtilMisc.toMap("docId", (String)mapIn.get("docId")));
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
	 * 各种出库
	 * 1.添加发货单
	 * 2.把发货单明细添加到中转库发货数onhand(A-B+)
	 * 3.修改库存数据(A)---库存数
	 * 4.修改中转库数据承诺数(A)
	 * 5.修改库存数据(A)---承诺数
	 * 6.修改发货指令数据(A)
	 */
	@SuppressWarnings("unchecked")
	public static BasePosObject allocateDelivery(Map<String,Object> mapHeader,List<Map<String,Object>> listItem,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			String productStoreId1 = (String)mapHeader.get("productStoreId");
			String productStoreIdto1 = (String)mapHeader.get("productStoreIdTo");
			if(((String)mapHeader.get("movementTypeId")).equals("DZ")||
					((String)mapHeader.get("movementTypeId")).equals("DY")||
					((String)mapHeader.get("movementTypeId")).equals("DG")){
				List<GenericValue> attList = delegator.findByAnd("ProductStoreAttribute", UtilMisc.toMap("productStoreId",productStoreId1,"productStoreAttrTypeId","COMPANY"));
				List<GenericValue> attListto = delegator.findByAnd("ProductStoreAttribute", UtilMisc.toMap("productStoreId",productStoreIdto1,"productStoreAttrTypeId","COMPANY"));
				String att="";
				String attTo = "";
				if (UtilValidate.isNotEmpty(attList)&&attList.size()>0) {
					att = (String)attList.get(0).get("attrValue");
				}
				if (UtilValidate.isNotEmpty(attListto)&&attListto.size()>0) {
					attTo = (String)attListto.get(0).get("attrValue");
				}
				if(!att.equals(attTo)){
					pObject.setFlag(Constant.NG);
					pObject.setMsg("不同法人不允许自主调拨");
					return pObject;
				}
			}
			BasePosObject szObject = szDelivery(mapHeader, listItem, dctx);
			if(szObject.getFlag().equals(Constant.NG)){
				pObject.setFlag(Constant.NG);
				pObject.setMsg(szObject.getMsg());
				return pObject;
			}
			if(mapHeader.get("docStatus").equals(Constant.DRAFT)){
				pObject.setFlag(Constant.OK);
				pObject.setMsg("保存成功");
				return pObject;
			}
			//是否是有发货指令
			GenericValue docCommand = null;
			List<GenericValue> inventnTypeList = delegator.findByAnd("MoveTypeTransferTypeView", UtilMisc.toMap("movementTypeId",(String) mapHeader.get("movementTypeId")));
			GenericValue inventnTransType =null;
			if(inventnTypeList!=null && inventnTypeList.size()>0){
				inventnTransType=inventnTypeList.get(0);
			}
			if(Constant.isNotNull(mapHeader.get("baseEntry"))){
				//判断是否是deliveryCommand发货 
				if(inventnTransType!=null && inventnTransType.get("baseEntity").equals(Constant.DELIVERYCOMMAND)){
					docCommand = delegator.findByPrimaryKey("DeliveryCommand", UtilMisc.toMap("docId", (String)mapHeader.get("baseEntry")));
					//判断指令状态
					if(docCommand.get("status").equals(Constant.CLEAR)){
						pObject.setFlag(Constant.NG);
						pObject.setMsg("该指令已清。无法再次发货");
						return pObject;
					}else{
						//修改明细
						BasePosObject updateDelive = updateDeliveryItemcommand(listItem, dctx);
						if(updateDelive.getFlag().equals(Constant.NG)){
							pObject.setFlag(Constant.NG);
							pObject.setMsg(updateDelive.getMsg());
							return pObject;
						}
						//修改状态
						BasePosObject updateDel = updateDeliverycommand(docCommand, dctx);
						if(updateDel.getFlag().equals(Constant.NG)){
							pObject.setFlag(Constant.NG);
							pObject.setMsg(updateDel.getMsg());
							return pObject;
						}
						//命令 1.是命名，解除占用  2.修改库存承诺数
						mapHeader.put("command", "command");
						mapHeader.put("leixing", "delivery");
						//解析参数,中转库所需要的数据
						BasePosObject  para= InventoryWebWork.conversionParameter(mapHeader, listItem,dctx);
						if(para.getFlag().equals(Constant.NG)){
							pObject.setFlag(Constant.NG);
							pObject.setMsg(para.getMsg());
							return pObject;
						}
						List<Map<String,Object>> mapList = (List<Map<String, Object>>) para.getData();
						BasePosObject delInventoryn= InventoryWebWork.delInventorynTransfer(mapList, dctx);
						if(delInventoryn.getFlag().equals(Constant.NG)){
							pObject.setFlag(Constant.NG);
							pObject.setMsg(delInventoryn.getMsg());
							return pObject;
						}
					}
				}
			}
			List<GenericValue> list = delegator.findByAnd("ReceiveCommand", UtilMisc.toMap("baseEntry",mapHeader.get("docId")));
			if(list!=null && list.size()>0){
				//有收货指令
				pObject.setFlag(Constant.OK);
				pObject.setData("保存成功");
				return pObject;
			}
			//没有收货指令，判断是否是调拨收货
			if(inventnTransType !=null && inventnTransType.get("movementTypeIdTo")!=null && Constant.isNotNull(mapHeader.get("productStoreId"))){
				GenericValue productStore = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", (String)mapHeader.get("productStoreIdTo")));
				if(productStore!=null && !Constant.ISSEQ.equals(productStore.get("primaryStoreGroupId"))){
					//自主调拨，生成收货指令单--先判断可用库存，如何可用库存<0，回滚
					Map<String,Object> mapId = FastMap.newInstance();
					mapId.put("type", "J");
					mapId.put("storeId",(String) mapHeader.get("productStoreId"));
					BasePosObject idPos =QueryCommonWorker.getOrderId(dctx,mapId);
					String docId = (String) idPos.getData();
					mapHeader.put("baseEntry",(String) mapHeader.get("docId"));
					mapHeader.put("docId",docId);
					mapHeader.put("leixing","receive");
					//根据移动类型获取仓库id
					Object facilityId=null;
					Object facilityIdFrom=null;
					Object movementTypeId =null;
	 				List<GenericValue> facilityList = delegator.findByAnd("ToFacilityAndStoreAndMove", UtilMisc.toMap("productStoreId",(String) mapHeader.get("productStoreId"),"movementTypeId",mapHeader.get("movementTypeId")));
					if(facilityList!=null && facilityList.size()>0){
						facilityIdFrom=facilityList.get(0).get("facilityId");
						movementTypeId =facilityList.get(0).get("movementTypeIdTo");
						List<GenericValue> facilityListFrom = delegator.findByAnd("ToFacilityAndStoreAndMove", UtilMisc.toMap("productStoreId",(String) mapHeader.get("productStoreIdTo"),"movementTypeId",movementTypeId));
						if(facilityListFrom!=null && facilityListFrom.size()>0){
							facilityId=facilityListFrom.get(0).get("facilityId");
						}
					}
					mapHeader.put("movementTypeId", movementTypeId);
					Object productStoreId = mapHeader.get("productStoreIdTo");
					Object productStoreIdFrom = mapHeader.get("productStoreId");
					mapHeader.put("productStoreId", productStoreId);
					mapHeader.put("productStoreIdFrom", productStoreIdFrom);
					List<Map<String,Object>> itemLisdt = new ArrayList<Map<String,Object>>();
					for(int i=0;i<listItem.size();i++){
						Map<String,Object> map = listItem.get(i);
						if(facilityId==null){
							Object oldFacilityId=map.get("facilityId");
							GenericValue oldFacility =delegator.findByPrimaryKeyCache("Facility", UtilMisc.toMap("facilityId", oldFacilityId.toString()));
							//得到仓库类型
							List<GenericValue> facility = delegator.findByAnd("FacilityAndProductStore", UtilMisc.toMap("productStoreId", productStoreId));
							for (int j = 0; j < facility.size(); j++) {
								Map<String,Object> facilityMap = facility.get(j);
								if(facilityMap.get("facilityTypeId").equals(oldFacility.get("facilityTypeId"))){
									facilityId =facilityMap.get("facilityId");
								}
							}
						}
						map.put("receiveQuantity", Long.parseLong(String.valueOf(0)));
						map.put("facilityId", facilityId);
						map.put("facilityIdFrom", facilityIdFrom);
						map.put("baseEntry", map.get("docId"));
						map.put("baseLineNo", map.get("lineNo"));
						map.put("docId", docId);
						itemLisdt.add(map);
					}
					
					BasePosObject receObject=ReceiveWebWork.saveReceiveCommand(mapHeader, itemLisdt, dctx);
					if(receObject.getFlag().equals(Constant.NG)){
						pObject.setFlag(Constant.NG);
						pObject.setMsg(receObject.getMsg());
						return pObject;
					}
					//添加中转库
					//解析参数,中转库所需要的数据
					BasePosObject  para= InventoryWebWork.conversionParameter(mapHeader, listItem,dctx);
					if(para.getFlag().equals(Constant.NG)){
						pObject.setFlag(Constant.NG);
						pObject.setMsg(para.getMsg());
						return pObject;
					}
					List<Map<String,Object>> mapList = (List<Map<String, Object>>) para.getData();
					BasePosObject delInventoryn= InventoryWebWork.saveReceInventorynTransfer(mapList, dctx);
					if(delInventoryn.getFlag().equals(Constant.NG)){
						pObject.setFlag(Constant.NG);
						pObject.setMsg(delInventoryn.getMsg());
						return pObject;
					}
				}
			}
			pObject.setFlag(Constant.OK);
			pObject.setData("保存成功");
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			return pObject;
		}
		return pObject;
	}
	/*
	 * 销售出库
	 */
	@SuppressWarnings("unchecked")
	public static BasePosObject szDelivery(Map<String,Object> mapHeader,List<Map<String,Object>> listItem,DispatchContext dctx){
		BasePosObject pObject = new BasePosObject();
		if(Constant.isNull(mapHeader.get("movementTypeId"))){
			pObject.setFlag(Constant.NG);
			pObject.setMsg("无移动类型");
			return pObject;
		}
		if(Constant.isNotNull(mapHeader.get("postingDate"))){
			mapHeader.put("postingDate", ConvertUtil.convertStringToTimeStamp(mapHeader.get("postingDate").toString()));
		}
		//获取系统时间
		mapHeader.put("docDate",UtilDateTime.nowTimestamp() );
		mapHeader.put("updateDate", UtilDateTime.nowTimestamp());
		//添加发货单
		BasePosObject saveDeli =saveDelivery(mapHeader, listItem, dctx);
		if(saveDeli.getFlag().equals(Constant.NG)){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(saveDeli.getMsg());
			return pObject;
		}
		//判断发货单状态--草稿结束
		if(mapHeader.get("docStatus").equals(Constant.DRAFT)){
			pObject.setFlag(Constant.OK);
			pObject.setMsg("保存成功");
			return pObject;
		}
		//解析参数,中转库所需要的数据
		mapHeader.put("leixing", "delivery");
		BasePosObject  para= InventoryWebWork.conversionParameter(mapHeader, listItem,dctx);
		if(para.getFlag().equals(Constant.NG)){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(para.getMsg());
			return pObject;
		}
		List<Map<String,Object>> mapList = (List<Map<String, Object>>) para.getData();
		//添加中转库  
		BasePosObject createInventoryn= InventoryWebWork.createInventorynTransfer(mapList, dctx);
		if(createInventoryn.getFlag().equals(Constant.NG)){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(createInventoryn.getMsg());
			return pObject;
		}else{
			pObject.setFlag(Constant.OK);
			return pObject;
		}
		
	}
	/*
	 * 指令或者订单添加中转库(占用)
	 */
	@SuppressWarnings("unchecked")
	public static BasePosObject commadDelivery(Map<String,Object> mapHeader,List<Map<String,Object>> listItem,DispatchContext dctx){
		BasePosObject pObject = new BasePosObject();
		//解析参数,中转库所需要的数据
		mapHeader.put("command","command");
		mapHeader.put("leixing", "delivery");
		BasePosObject  para= InventoryWebWork.conversionParameter(mapHeader, listItem,dctx);
		if(para.getFlag().equals(Constant.NG)){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(para.getMsg());
			return pObject;
		}
		List<Map<String,Object>> mapList = (List<Map<String, Object>>) para.getData();
		//添加中转库
		
		BasePosObject createInventoryn= InventoryWebWork.saveInventorynTransfer(mapList, dctx);
		if(createInventoryn.getFlag().equals(Constant.NG)){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(createInventoryn.getMsg());
			return pObject;
		}else{
			pObject.setFlag(Constant.OK);
			return pObject;
		}
	}
	/*
	 * 添加出库单
	 */
	public static BasePosObject saveDelivery(Map<String,Object> mapHeader,List<Map<String,Object>> listItem,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<GenericValue> list = null;
		EntityCondition mainCond = null;
		List<EntityCondition> andExprs = FastList.newInstance();
		try {
			GenericValue deliveryDoc = delegator.makeValidValue("DeliveryDoc", mapHeader);
			GenericValue deliveryDoc1 = delegator.findByPrimaryKey("DeliveryDoc", UtilMisc.toMap("docId",(String) mapHeader.get("docId")));
			if(!deliveryDoc.get("docStatus").equals(Constant.DRAFT)){
				deliveryDoc.set("isSync", Constant.ONE);
			}
			if(deliveryDoc1==null){ 
				deliveryDoc.create();
			}else{
				deliveryDoc1=delegator.makeValidValue("DeliveryDoc", mapHeader);
				deliveryDoc1.store();
			}
			for (int i = 0; i < listItem.size(); i++) {
				Map<String,Object> itemMap = listItem.get(i);
				if(!deliveryDoc.get("docStatus").equals(Constant.DRAFT)){
					
					if(Constant.isNull(itemMap.get("facilityId"))){
						List<GenericValue> facilityList = delegator.findByAnd("FacilityAndStoreAndMove", UtilMisc.toMap("productStoreId",(String) mapHeader.get("productStoreId"),"movementTypeId",mapHeader.get("movementTypeId")));
						if(facilityList!=null && facilityList.size()>0){
							//根据移动类型获取仓库id
							itemMap.put("facilityId", facilityList.get(0).get("facilityId"));
						}else{
							pObject.setFlag(Constant.NG);
							pObject.setMsg("没有适合的仓库");
							return pObject;
						}
					}
					if(Constant.isNull(itemMap.get("facilityIdTo"))){
						List<GenericValue> facilityList = delegator.findByAnd("ToFacilityAndStoreAndMove", UtilMisc.toMap("productStoreId",(String) mapHeader.get("productStoreId"),"movementTypeId",mapHeader.get("movementTypeId")));
						if(facilityList!=null && facilityList.size()>0){
							//根据移动类型获取仓库id
							if(!Constant.isNull(facilityList.get(0).get("facilityId")))
								itemMap.put("facilityIdTo", facilityList.get(0).get("facilityId"));
							
						}
					}
					
					
					GenericValue productSeq = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId", itemMap.get("productId")));
					if(productSeq!=null && productSeq.get("requireInventory").equals(Constant.ISSEQ)&&productSeq.get("isSequence").equals(Constant.ISSEQ)){
						//GenericValue proSeq = delegator.findByPrimaryKey("ProductSequence", UtilMisc.toMap("productId", itemMap.get("productId"),"sequenceId",itemMap.get("sequenceId")));
						EntityCondition mainCond1 = EntityCondition.makeCondition(UtilMisc.toMap("productId", itemMap.get("productId"),"sequenceId",itemMap.get("sequenceId")));
						List<GenericValue>  lists = delegator.findList("ProductSequence",mainCond1, null, null,null, false);
						if(UtilValidate.isEmpty(lists)){
							pObject.setFlag(Constant.NG);
							pObject.setMsg(productSeq.get("productName")+"没有串号商品信息");
							return pObject;
						}
						GenericValue proSeq = lists.get(0);
						if(Constant.isNull(proSeq.get("facilityId"))||!proSeq.get("facilityId").equals(itemMap.get("facilityId"))){
							pObject.setFlag(Constant.NG);
							pObject.setMsg(proSeq.get("sequenceId")+"库位不正确");
							return pObject;
						}
						if(!itemMap.get("sequenceId").equals(proSeq.get("sequenceId"))){
							//如果传入商品串号和查询商品串号大小写不同，发货单明细中写入查询的结果。
							itemMap.put("sequenceId", proSeq.get("sequenceId"));
						}
						GenericValue productStore = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", (String)mapHeader.get("productStoreIdTo")));
						if(productStore!=null && Constant.ISSEQ.equals(productStore.get("primaryStoreGroupId"))){
							itemMap.put("facilityIdTo", null);
						}

						if(mapHeader.get("productStoreId").equals(mapHeader.get("productStoreIdTo"))){
							//移库
							List<GenericValue> facilityList = delegator.findByAnd("ToFacilityAndStoreAndMove", UtilMisc.toMap("productStoreId", (String)mapHeader.get("productStoreId"),"movementTypeId",(String)mapHeader.get("movementTypeId")));
							if (UtilValidate.isNotEmpty(facilityList)) {
								proSeq.set("facilityId",(String) facilityList.get(0).get("facilityId"));
								proSeq.set("isOccupied",Constant.NOSEQ);
								proSeq.set("isSync", Constant.ONE);
								proSeq.store();
							}else {
								proSeq.set("facilityId",(String) itemMap.get("facilityIdTo"));
								proSeq.set("isOccupied",Constant.NOSEQ);
								proSeq.set("isSync", Constant.ONE);
								proSeq.store();
							}
						}else if(Constant.isNotNull(proSeq.get("facilityId"))){
							proSeq.set("facilityId", itemMap.get("facilityIdTo"));
							proSeq.set("isOccupied", Constant.NOSEQ);
							proSeq.set("isSync", Constant.ONE);
							proSeq.store();
							
						}else{
							pObject.setFlag(Constant.NG);
							pObject.setMsg(productSeq.get("productName")+"串号商品已出库");
							return pObject;
						}
					}
				}
				//当销售出库(SZ)时，当商品是库存管理时，如果库存大于等于出库数且库存数减承诺数大于等于出库数时，出库单才创建成功
				if(Constant.MOVE.equals((deliveryDoc.get("movementTypeId")))){
					andExprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS,deliveryDoc.get("productStoreId")));
					andExprs.add(EntityCondition.makeCondition("movementTypeId",EntityOperator.EQUALS, deliveryDoc.get("movementTypeId")));
					andExprs.add(EntityCondition.makeCondition("productId",EntityOperator.EQUALS,  itemMap.get("productId")));
					mainCond = EntityCondition.makeCondition(andExprs,EntityOperator.AND);
					list = delegator.findList("InventorynItemAndProductAndMOve",mainCond, null, null, null, false );
				    if(UtilValidate.isNotEmpty(list)&&list.size()==1){
				    	if("Y".equals((String)list.get(0).get("requireInventory"))){
				    		if((Long)list.get(0).get("onhand") < Long.parseLong(String.valueOf(itemMap.get("quantity"))) || 
				    				(Long)list.get(0).get("onhand")-(Long)list.get(0).get("promise") <
				    				         Long.parseLong(String.valueOf(itemMap.get("quantity")))){
				    			pObject.setFlag(Constant.NG);
				    			pObject.setMsg((String)list.get(0).get("productName")+"("+(String)list.get(0).get("productId")+")"+"库存不足！");
				    			return pObject;
				    		}
				    	}
				    }
				}
				Long lineNo =Long.parseLong(String.valueOf(itemMap.get("lineNo")));
				listItem.get(i).put("lineNo", lineNo);
				if(Constant.isNotNull(itemMap.get("baseLineNo"))){
					listItem.get(i).put("baseLineNo", Long.parseLong(String.valueOf(itemMap.get("baseLineNo"))));
				}
				itemMap.put("quantity",  Long.parseLong(String.valueOf(itemMap.get("quantity"))));
				GenericValue deliveryItem = delegator.makeValidValue("DeliveryItem", itemMap);
				deliveryItem.create();
			}
			pObject.setFlag(Constant.OK);
			pObject.setData("保存成功");
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			return pObject;
		}
		return pObject;
	}
	/*
	 * 销售修改出库单
	 */
	public static BasePosObject updateSzDelivery(Map<String,Object> mapHeader,List<Map<String,Object>> listItem,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			GenericValue deliveryDoc =delegator.findByPrimaryKey("DeliveryDoc", UtilMisc.toMap("docId",(String) mapHeader.get("docId")));
			if(mapHeader.get("postingDate")!=null){
				mapHeader.put("postingDate", ConvertUtil.convertStringToTimeStamp(mapHeader.get("postingDate").toString()));
			}
			//获取系统时间
			mapHeader.put("updateDate", UtilDateTime.nowTimestamp());
			mapHeader.put("docDate", deliveryDoc.get("docDate"));
			GenericValue deliDoc = delegator.makeValue("DeliveryDoc", mapHeader);
			if(deliveryDoc.get("docStatus").equals(Constant.DRAFT)){
				delegator.removeByAnd("DeliveryItem", UtilMisc.toMap("docId",(String) mapHeader.get("docId")));
				BasePosObject szObject = szDelivery(mapHeader, listItem, dctx);
				if(szObject.getFlag().equals(Constant.NG)){
					pObject.setFlag(Constant.NG);
					pObject.setMsg(szObject.getMsg());
					return pObject;
				}
			}
			deliveryDoc.setFields(deliDoc);
			deliveryDoc.store();
			//添加明细
			pObject.setFlag(Constant.OK);
			pObject.setData("修改成功");
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			return pObject;
		}
		return pObject;
	}
	/*
	 * 修改出库单
	 */
	public static BasePosObject updateDelivery(Map<String,Object> mapHeader,List<Map<String,Object>> listItem,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			GenericValue deliveryDoc =delegator.findByPrimaryKey("DeliveryDoc", UtilMisc.toMap("docId",(String) mapHeader.get("docId")));
			if(Constant.isNotNull(mapHeader.get("postingDate"))){
				mapHeader.put("postingDate", ConvertUtil.convertStringToTimeStamp(mapHeader.get("postingDate").toString()));
			}
			//获取系统时间
			mapHeader.put("updateDate", UtilDateTime.nowTimestamp());
			mapHeader.put("docDate", deliveryDoc.get("docDate"));
			GenericValue deliDoc = delegator.makeValue("DeliveryDoc", mapHeader);
//			for (int i = 0; i < listItem.size(); i++) {
//				Map<String,Object> itemMap=listItem.get(i);
//				if(Long.parseLong(String.valueOf(itemMap.get("quantity")))>Long.parseLong(String.valueOf(itemMap.get("deliveryQuantity")))){
//					pObject.setFlag(Constant.NG);
//					pObject.setMsg("实际发货数大于计划发货数");
//					return pObject;
//				}
//			}
			if(deliveryDoc.get("docStatus").equals(Constant.DRAFT)){
				delegator.removeByAnd("DeliveryItem", UtilMisc.toMap("docId",(String) mapHeader.get("docId")));
				BasePosObject saveObject =allocateDelivery(mapHeader, listItem, dctx);
				if(saveObject.getFlag().equals(Constant.NG)){
					pObject.setFlag(Constant.NG);
					pObject.setMsg(saveObject.getMsg());
					return pObject;
				}
			}
			deliveryDoc.setFields(deliDoc);
			deliveryDoc.store();
			//添加明细
			pObject.setFlag(Constant.OK);
			pObject.setData("修改成功");
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			return pObject;
		}
		return pObject;
	}
	/*
	 * 修改指令明细
	 */
	public static BasePosObject updateDeliveryItemcommand(List<Map<String,Object>> itemComList,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		
		try {
			for (int i = 0; i < itemComList.size(); i++) {
				Map<String,Object> delItemCommand =itemComList.get(i);
				GenericValue deliveryItemcommand =delegator.findByPrimaryKey("DeliveryItemcommand", UtilMisc.toMap("docId", (String)delItemCommand.get("baseEntry"),"lineNo",delItemCommand.get("baseLineNo")));
				deliveryItemcommand.set("deliveryQuantity", deliveryItemcommand.getLong("deliveryQuantity")+Long.parseLong(String.valueOf(delItemCommand.get("quantity"))));
				deliveryItemcommand.store();
			}
			pObject.setFlag(Constant.OK);
			pObject.setData("修改成功");
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			return pObject;
		}
		return pObject;
	}
	/*
	 * 修改指令头状态(强制修改成已清状态)
	 */
	public static BasePosObject updateDeliverycommandStatus(Map<String, Object> jsonObj,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			GenericValue deliveryDoc = delegator.findByPrimaryKey("DeliveryCommand", UtilMisc.toMap("docId",(String) jsonObj.get("docId")));
			//创建虚拟的入库指令单
			List<GenericValue> deliveryItemList = delegator.findByAnd("DeliveryItemcommand", UtilMisc.toMap("docId",(String) jsonObj.get("docId")));
			List<Map<String,Object>> listItem = new ArrayList<Map<String,Object>>();
			if(deliveryItemList!=null && deliveryItemList.size()>0){
				for (int i = 0; i<deliveryItemList.size();i++) {
					GenericValue deliveryItem = deliveryItemList.get(i);
					Map<String,Object> map = new HashMap<String,Object>();
					Long leng = deliveryItem.getLong("quantity")-deliveryItem.getLong("deliveryQuantity");
					if(leng>0){
						//生成记录数据
						List<GenericValue> list = delegator.findByAnd("InventorynItem", UtilMisc.toMap("productId", deliveryItem.get("productId"),"facilityId", deliveryItem.get("facilityId"),"productStoreId", deliveryDoc.get("productStoreId")));
						map.put("fromInventoryItemId", list.get(0).get("inventoryItemId"));
						map.put("productStoreId", deliveryDoc.get("productStoreId"));
//						map.put("movementTypeId", deliveryDoc.get("movementTypeId"));
						map.put("statusId", deliveryDoc.get("statusId"));
						map.put("partyId", deliveryDoc.get("partyId"));
						map.put("updateDate", UtilDateTime.nowTimestamp());
						map.put("updateUserId", jsonObj.get("updateUserId"));
						map.put("baseEntry", deliveryDoc.get("docId"));
						map.put("sequenceId", deliveryItem.get("sequenceId"));
						map.put("productId", deliveryItem.get("productId"));
						map.put("facilityId", deliveryItem.get("facilityId"));
						
//						map.put("inventoryTransferTypeId", deliveryItem.get("inventoryTransferTypeId"));
						map.put("promise", leng );
						listItem.add(map);
					}
					
				}
			}
			//解除占用
			BasePosObject receObject =InventoryWebWork.delInventorynTransfer(listItem, dctx);
			if(receObject.getFlag().equals(Constant.NG)){
				pObject.setFlag(receObject.getFlag());
				pObject.setMsg(receObject.getMsg());
				return pObject;
			}
			//修改状态
			deliveryDoc.set("isSync", "1");
			deliveryDoc.set("status", Constant.CLEAR);
			deliveryDoc.set("docStatus", Constant.INVALID);
			deliveryDoc.store();
			pObject.setFlag(Constant.OK);
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			return pObject;
		}
		return pObject;
	}
	/*
	 * 修改指令头状态
	 */
	public static BasePosObject updateDeliverycommand(GenericValue mapIn,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			//获取指令明细的条数
			List<GenericValue> list=delegator.findByAnd("DeliveryItemcommand", UtilMisc.toMap("docId",(String) mapIn.get("docId")));
			GenericValue deliverycommand = delegator.findByPrimaryKey("DeliveryCommand", UtilMisc.toMap("docId",(String) mapIn.get("docId")));
			int lang =0;
			boolean b=false;
			for (GenericValue genericValue : list) {
				if(genericValue.getLong("quantity") < genericValue.getLong("deliveryQuantity")){
					pObject.setFlag(Constant.NG);
					pObject.setMsg("实际发货数大于计划发货数");
					return pObject;
				}
				if(genericValue.getLong("deliveryQuantity")>0 && genericValue.get("quantity")!=genericValue.get("deliveryQuantity")){
					b=true;//未清
				}else if(genericValue.getLong("quantity")==genericValue.getLong("deliveryQuantity")){
					lang++;
				}
			}
			if(lang==0 && !b){
				deliverycommand.set("status",Constant.NOTCLEARED);//NOTCLEARED
			}else if(lang==list.size()){
				deliverycommand.set("status",Constant.CLEAR);
			}else{
				deliverycommand.set("status",Constant.PARTCLEARED);
			}
			deliverycommand.set("isSync", Constant.ONE);
			deliverycommand.store();
			pObject.setFlag(Constant.OK);
			pObject.setData("修改成功");
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			return pObject;
		}
		return pObject;
	}
	public static BasePosObject updateDeliveryPrint(Map<String,Object> mapIn,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			GenericValue geValue = delegator.findByPrimaryKey("DeliveryDoc", UtilMisc.toMap("docId", mapIn.get("docId")));
			if(geValue.get("printNum")==null){
				geValue.set("printNum", Long.parseLong("0"));
			}
			Long print =  geValue.getLong("printNum");
			geValue.set("printNum", print+1);
			geValue.store();
		} catch (Exception e) {
			pObject.setFlag(Constant.NG); 
			pObject.setMsg(e.getMessage());
			return pObject;
		}
		pObject.setFlag(Constant.OK); 
		return pObject; 
	}
}