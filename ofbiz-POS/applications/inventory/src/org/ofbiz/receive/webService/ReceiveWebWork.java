package org.ofbiz.receive.webService;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.inventory.webService.InventoryWebWork;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.util.ConvertUtil;

public class ReceiveWebWork {
	/*
	 * 查询收货指令头信息ReceiveCommand
	 */
	public static BasePosObject findReceiveCommand(Map<String,Object> mapIn,DispatchContext dctx) {
		// 方法参数
		EntityCondition whereEntityCondition = null;//条件
		Set<String> fieldsToSelect = FastSet.newInstance();// 要查询的列
		List<EntityCondition> andExprs = FastList.newInstance();// 添加条件值
		List<String> orderBy = FastList.newInstance();
		orderBy.add("updateDate DESC");//倒序
		
		if (Constant.isNotNull(mapIn.get("docId"))) {
			andExprs.add(EntityCondition.makeCondition("docId", EntityOperator.EQUALS,(String) mapIn.get("docId")));
		}
		if (Constant.isNotNull(mapIn.get("productStoreIdFrom"))) {
			andExprs.add(EntityCondition.makeCondition("productStoreIdFrom", EntityOperator.EQUALS, (String)mapIn.get("productStoreIdFrom")));
		}
		if (Constant.isNotNull(mapIn.get("productStoreId"))) {
			andExprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS,(String) mapIn.get("productStoreId")));
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
		
		if(Constant.isNotNull(mapIn.get("orderType"))){
			andExprs.add(EntityCondition.makeCondition("orderType", EntityOperator.EQUALS,(String) mapIn.get("orderType")));
		}
		if(Constant.isNotNull(mapIn.get("status"))){
			andExprs.add(EntityCondition.makeCondition("status", EntityOperator.EQUALS,(String) mapIn.get("status")));
		}
		//获取状态
//		List<String> docStatus = new ArrayList<String>();
//		docStatus.add("0");//未清
//		docStatus.add("1");//部分已清
//		andExprs.add(EntityCondition.makeCondition("docStatus", EntityOperator.EQUALS, "1"));
		if (andExprs.size() > 0){
        	//mainCond = EntityCondition.makeConditionWhere(jsonStr);
			whereEntityCondition = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
        }
		
		BasePosObject pObject  = findReceive(dctx, "ReceiveCommandAndProductStore", whereEntityCondition, fieldsToSelect,orderBy);
		return pObject;
	}
	/*
	 * 查询收货指令明细信息ReceiveItemCommand
	 */
	public static BasePosObject findReceiveItemcommand(Map<String,Object> mapIn,DispatchContext dctx){
		// 方法参数
		EntityCondition whereEntityCondition = null;//条件
		Set<String> fieldsToSelect = FastSet.newInstance();// 要查询的列
		List<EntityCondition> andExprs = FastList.newInstance();// 添加条件值
		andExprs.add(EntityCondition.makeCondition("docId", EntityOperator.EQUALS,(String) mapIn.get("docId")));
//		andExprs.add(EntityCondition.makeCondition("quantity"),EntityOperator.GREATER_THAN,"quantity")));
		
		if (andExprs.size() > 0){
        	//mainCond = EntityCondition.makeConditionWhere(jsonStr);
			whereEntityCondition = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
        }
		BasePosObject pObject  = findReceive(dctx,"ReceiveItemcommandAndProductStore", whereEntityCondition, fieldsToSelect,null);
		return pObject;
	}
	/*
	 * 查询收货单头信息
	 */
	public static BasePosObject findReceiveDoc(Map<String,Object> mapIn,DispatchContext dctx){
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
			andExprs.add(EntityCondition.makeCondition("baseEntry", EntityOperator.EQUALS,(String) mapIn.get("baseEntry")));
		}
		if (Constant.isNotNull(mapIn.get("productStoreIdFrom"))) {
			andExprs.add(EntityCondition.makeCondition("productStoreIdFrom", EntityOperator.EQUALS,(String) mapIn.get("productStoreIdFrom")));
		}
		if (Constant.isNotNull(mapIn.get("productStoreId"))) {
			andExprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS,(String) mapIn.get("productStoreId")));
		}
		if (Constant.isNotNull(mapIn.get("docStatus"))) {
			andExprs.add(EntityCondition.makeCondition("docStatus", EntityOperator.EQUALS,(String) mapIn.get("docStatus")));
		}
		if(Constant.isNotNull(mapIn.get("orderType"))){
    		andExprs.add(EntityCondition.makeCondition("orderType", EntityOperator.EQUALS,(String) mapIn.get("orderType")));
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
		if (andExprs.size() > 0){
        	//mainCond = EntityCondition.makeConditionWhere(jsonStr);
			whereEntityCondition = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
        }
		BasePosObject pObject  = findReceive(dctx,"ReceiveDocAndProductStore", whereEntityCondition, fieldsToSelect,orderBy);
		return pObject;
	}
	/*
	 * 查询收货单明细ReceiveItem
	 */
	public static BasePosObject findReceiveItem(Map<String,Object> mapIn,DispatchContext dctx) {
		// 方法参数
		EntityCondition whereEntityCondition = null;//条件
		Set<String> fieldsToSelect = FastSet.newInstance();// 要查询的列
		List<EntityCondition> andExprs = FastList.newInstance();// 添加条件值
		andExprs.add(EntityCondition.makeCondition("docId", EntityOperator.EQUALS,(String) mapIn.get("docId")));
		if (andExprs.size() > 0){
			whereEntityCondition = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
        }
		BasePosObject pObject  = findReceive(dctx,"ReceiveItemAndProduct", whereEntityCondition, fieldsToSelect,null);
		return pObject;
	}
	/*
	 * 销售修改入库单
	 */
	public static BasePosObject updateTReceive(Map<String,Object> mapHeader,List<Map<String,Object>> listItem,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			GenericValue receiveDoc =delegator.findByPrimaryKey("ReceiveDoc", UtilMisc.toMap("docId",(String) mapHeader.get("docId")));
			if(Constant.isNotNull(mapHeader.get("postingDate"))){
				mapHeader.put("postingDate", ConvertUtil.convertStringToTimeStamp(mapHeader.get("postingDate").toString()));
			}
			//获取系统时间
			mapHeader.put("updateDate",UtilDateTime.nowTimestamp() );
			mapHeader.put("docDate",receiveDoc.get("docDate"));
			GenericValue receDoc = delegator.makeValue("ReceiveDoc", mapHeader);
			if(receiveDoc.get("docStatus").equals(Constant.DRAFT)){
				delegator.removeByAnd("ReceiveItem", UtilMisc.toMap("docId",(String) mapHeader.get("docId")));
				BasePosObject saObject =reReceive(mapHeader, listItem, dctx);
				if(saObject.getFlag().equals(Constant.NG)){
					pObject.setFlag(Constant.NG);
					pObject.setMsg(saObject.getMsg());
					return pObject;
				}
			}
			receiveDoc.setFields(receDoc);
			receiveDoc.store();
			//添加明细
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
	 * 指令修改入库单
	 */
	public static BasePosObject updateReceive(Map<String,Object> mapHeader,List<Map<String,Object>> listItem,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			GenericValue receiveDoc =delegator.findByPrimaryKey("ReceiveDoc", UtilMisc.toMap("docId",(String) mapHeader.get("docId")));
			if(Constant.isNotNull(mapHeader.get("postingDate"))){
				mapHeader.put("postingDate", ConvertUtil.convertStringToTimeStamp(mapHeader.get("postingDate").toString()));
			}
			//获取系统时间
			mapHeader.put("updateDate",UtilDateTime.nowTimestamp() );
			mapHeader.put("docDate",receiveDoc.get("docDate"));
			GenericValue receDoc = delegator.makeValue("ReceiveDoc", mapHeader);
			if(receiveDoc.get("docStatus").equals(Constant.DRAFT)){
				delegator.removeByAnd("ReceiveItem", UtilMisc.toMap("docId",(String) mapHeader.get("docId")));
				BasePosObject saObject =allocateReceive(mapHeader, listItem, dctx);
				if(saObject.getFlag().equals(Constant.NG)){
					pObject.setFlag(Constant.NG);
					pObject.setMsg(saObject.getMsg());
					return pObject;
				}
			}
			receiveDoc.setFields(receDoc);
			receiveDoc.store();
			//添加明细
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
	 * 公用的查询方法
	 */
	public static BasePosObject findReceive(DispatchContext dctx,String viewName,EntityCondition whereEntityCondition,Set<String> fieldsToSelect,List<String> orderby){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			List<GenericValue> list = delegator.findList(viewName,whereEntityCondition, fieldsToSelect, orderby, null, false );
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
	 * 删除收货单
	 */
	public static BasePosObject deleteReceiveCommand(Map<String,Object> mapIn,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			delegator.removeByAnd("ReceiveDoc", UtilMisc.toMap("docId",(String) mapIn.get("docId")));
			delegator.removeByAnd("ReceiveItem", UtilMisc.toMap("docId",(String) mapIn.get("docId")));
			
			pObject.setFlag(Constant.OK);
			pObject.setData("删除成功");
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
        	pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	/*
	 * 入库
	 * 1.添加收货单
	 * 2.把收货单明细添加到中转库   收货数onhand(A+B-)
	 * 3.修改库存数据(A)---库存数  inventoryn_item表的onhand
	 * 4.修改中转库数据承诺数(A)  inventory_transfer表的promise
	 * 5.修改库存数据(A)---承诺数  inventoryn_item表的promise
	 * 6.修改收货指令数据(A)     receive_command表
	 */
	public static BasePosObject allocateReceive(Map<String,Object> mapHeader,List<Map<String,Object>> listItem,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			BasePosObject szObject = reReceive(mapHeader, listItem, dctx);
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
			//是否是有收货指令
			GenericValue docCommand = null;
			if(Constant.isNotNull(mapHeader.get("baseEntry"))){
				//判断是否是ReceiveCommand收货
				List<GenericValue> inventnTypeList = delegator.findByAnd("MoveTypeTransferTypeView", UtilMisc.toMap("movementTypeId",(String) mapHeader.get("movementTypeId")));
				if(inventnTypeList!=null && inventnTypeList.size()>0){
					GenericValue inventnTransType=inventnTypeList.get(0);
					if(inventnTransType.get("baseEntity").equals(Constant.RECEIVECOMMAND)){
						docCommand = delegator.findByPrimaryKey("ReceiveCommand", UtilMisc.toMap("docId",(String) mapHeader.get("baseEntry")));
						//判断指令状态
						if(docCommand.get("status").equals(Constant.CLEAR)){
							pObject.setFlag(Constant.NG);
							pObject.setMsg("该指令已清。无法再次收货");
							return pObject;
						}else{
							//修改明细
							BasePosObject updateReceive = updateReceiveItemcommand(listItem, dctx);
							if(updateReceive.getFlag().equals(Constant.NG)){
								pObject.setFlag(Constant.NG);
								pObject.setMsg(updateReceive.getMsg());
								return pObject;
							}
							//修改状态
							BasePosObject updateRec = updateReceivecommand(docCommand, dctx);
							if(updateRec.getFlag().equals(Constant.NG)){
								pObject.setFlag(Constant.NG);
								pObject.setMsg(updateRec.getMsg());
								return pObject;
							}
						}
					}
				}
			}
			pObject.setFlag(Constant.OK);
			pObject.setData("保存成功");
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	/*
	 * 指令或者订单添加中转库(取消占用)
	 */
	@SuppressWarnings("unchecked")
	public static BasePosObject commadReceive(Map<String,Object> mapHeader,List<Map<String,Object>> listItem,DispatchContext dctx){
		BasePosObject pObject = new BasePosObject();
		//解析参数,中转库所需要的数据
		mapHeader.put("command", "command");
		mapHeader.put("leixing", "receive");
		BasePosObject  para= InventoryWebWork.conversionParameter(mapHeader, listItem,dctx);
		if(para.getFlag().equals(Constant.NG)){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(para.getMsg());
			return pObject;
		}
		List<Map<String,Object>> mapList = (List<Map<String, Object>>) para.getData();
		//添加中转库
		
		BasePosObject createInventoryn= InventoryWebWork.delInventorynTransfer(mapList, dctx);
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
	 * 退货入库
	 */
	@SuppressWarnings("unchecked")
	public static BasePosObject reReceive(Map<String,Object> mapHeader,List<Map<String,Object>> listItem,DispatchContext dctx){
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
		//添加收货单
		BasePosObject saveRece =saveReceive(mapHeader, listItem, dctx);
		if(saveRece.getFlag().equals(Constant.NG)){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(saveRece.getMsg());
			return pObject;
		}
		//判断收货单状态--草稿结束
		if(mapHeader.get("docStatus").equals(Constant.DRAFT)){
			pObject.setFlag(Constant.OK);
			pObject.setMsg("保存成功");
			return pObject;
		}
		//解析参数,中转库所需要的数据
		mapHeader.put("leixing", "receive");
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
	 * 添加入库单
	 */
	public static BasePosObject saveReceive(Map<String, Object> mapHeader,List<Map<String, Object>> listItem,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			
			GenericValue receiveDoc = delegator.makeValidValue("ReceiveDoc", mapHeader);
			GenericValue receiveDoc1 = delegator.findByPrimaryKey("ReceiveDoc", UtilMisc.toMap("docId",(String) mapHeader.get("docId")));
			if(!receiveDoc.get("docStatus").equals(Constant.DRAFT)){
				receiveDoc.set("isSync", Constant.ONE);
			}
			if(receiveDoc1==null){ 
				receiveDoc.create();
			}else{
				receiveDoc1=delegator.makeValidValue("ReceiveDoc", mapHeader);
				receiveDoc1.store();
			}
			for (int i = 0; i < listItem.size(); i++) {
				Map<String,Object> itemMap = listItem.get(i);
				//行明细仓库为空，不进行后续操作
				if(itemMap.get("facilityId") == null){
					pObject.setFlag(Constant.NG);
					pObject.setMsg("仓库不存在，无法进行入库操作");
					return pObject;
				}
				if(!receiveDoc.get("docStatus").equals(Constant.DRAFT)){
					GenericValue product = delegator.findByPrimaryKey("Product", UtilMisc.toMap("productId",(String) itemMap.get("productId")));
					if(product!=null){
						if(product.get("isSequence").equals(Constant.ISSEQ)){
							/**
							 * 串号表无商品，添加商品串号
							 * 串号表有商品，更新商品串号
							 * modify by f.t 2015.1.19
							 */
							GenericValue proSeq = delegator.findByPrimaryKey("ProductSequence", UtilMisc.toMap("productId",(String) itemMap.get("productId"),"sequenceId",itemMap.get("sequenceId")));
							if(proSeq == null){
								Map<String,Object> seqMap = FastMap.newInstance();
								seqMap.put("productId", itemMap.get("productId"));
								seqMap.put("sequenceId", itemMap.get("sequenceId"));
								seqMap.put("facilityId", itemMap.get("facilityId"));
								seqMap.put("isOccupied", Constant.NOSEQ);
								seqMap.put("isSync", Constant.ONE);
								GenericValue seq = delegator.makeValidValue("ProductSequence", seqMap);
								seq.create();
							}else{
								if(proSeq.get("facilityId") == null || !itemMap.get("facilityId").equals(proSeq.get("facilityId"))){
									proSeq.set("facilityId", itemMap.get("facilityId"));
									//proSeq.set("isOccupied", Constant.NOSEQ);
									proSeq.set("isSync", Constant.ONE);
									proSeq.store();
								}else{
									pObject.setFlag(Constant.NG);
									pObject.setMsg("串号商品已入库");
									return pObject;
								}
							}
						}
					}else{
						pObject.setFlag(Constant.NG);
						pObject.setMsg("无此主商品");
					}
				}
				itemMap.put("lineNo", Long.parseLong(String.valueOf(itemMap.get("lineNo"))));
				if(Constant.isNotNull(itemMap.get("baseLineNo"))){
					itemMap.put("baseLineNo", Long.parseLong(String.valueOf(itemMap.get("baseLineNo"))));
					List<GenericValue> receiveItem=delegator.findByAnd("ReceiveItem", UtilMisc.toMap("baseEntry", itemMap.get("baseEntry"),"baseLineNo",itemMap.get("baseLineNo")));
					if(receiveItem!=null&&receiveItem.size()>0){
						pObject.setFlag(Constant.NG);
						pObject.setMsg("该指令已收货");
						return pObject;
					}
				}
				
				itemMap.put("quantity",  Long.parseLong(String.valueOf(itemMap.get("quantity"))));
				GenericValue receiveItem = delegator.makeValidValue("ReceiveItem",itemMap);
				receiveItem.create();
			}
			pObject.setFlag(Constant.OK);
			pObject.setData("保存成功");
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	/*
	 * 添加入库指令单
	 */
	public static BasePosObject saveReceiveCommand(Map<String, Object> mapHeader,List<Map<String, Object>> listItem,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			GenericValue receiveDoc = delegator.makeValidValue("ReceiveCommand", mapHeader);
			
			if(!receiveDoc.get("docStatus").equals(Constant.DRAFT)){
				receiveDoc.set("isSync", Constant.ONE);
			}
			receiveDoc.create();
			for (int i = 0; i < listItem.size(); i++) {
				Map<String,Object> itemMap = listItem.get(i);
				GenericValue receiveItem = delegator.makeValidValue("ReceiveItemcommand",itemMap);
				receiveItem.create();
			}
			pObject.setFlag(Constant.OK);
			pObject.setData("保存成功");
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	/*
	 * 修改指令明细
	 */
	public static BasePosObject updateReceiveItemcommand(List<Map<String,Object>> itemComList ,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		
		try {
			for (int i = 0; i < itemComList.size(); i++) {
				Map<String,Object> delItemCommand =itemComList.get(i);
				GenericValue receiveItemcommand =delegator.findByPrimaryKey("ReceiveItemcommand", UtilMisc.toMap("docId", delItemCommand.get("baseEntry"),"lineNo",delItemCommand.get("baseLineNo")));
				
				if(receiveItemcommand.get("receiveQuantity")==null){
					receiveItemcommand.set("receiveQuantity", Long.parseLong(String.valueOf(0)));
				}
				receiveItemcommand.set("receiveQuantity", receiveItemcommand.getLong("receiveQuantity")+Long.parseLong(String.valueOf(delItemCommand.get("quantity"))));
				receiveItemcommand.store();
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
	 * 修改指令头状态
	 */
	public static BasePosObject updateReceivecommand(GenericValue mapIn,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			//获取指令明细的条数
			List<GenericValue> list=delegator.findByAnd("ReceiveItemcommand", UtilMisc.toMap("docId",(String) mapIn.get("docId")));
			GenericValue receivecommand = delegator.findByPrimaryKey("ReceiveCommand", UtilMisc.toMap("docId",(String) mapIn.get("docId")));
			int lang =0;
			boolean b=false;
			for (GenericValue genericValue : list) {
				if(genericValue.getLong("quantity") < genericValue.getLong("receiveQuantity")){
					pObject.setFlag(Constant.NG);
					pObject.setMsg("实际收货数大于计划发货数");
					return pObject;
				}
				if(genericValue.getLong("receiveQuantity")>0 && !genericValue.get("quantity").equals(genericValue.get("receiveQuantity"))){
					b=true;
				}else if(genericValue.getLong("quantity").equals(genericValue.getLong("receiveQuantity"))){
					lang++;
				}
			}
			if(lang==0 && !b){
				receivecommand.set("status",Constant.NOTCLEARED);//NOTCLEARED
			}else if(lang==list.size()){
				receivecommand.set("status",Constant.CLEAR);
			}else{
				receivecommand.set("status",Constant.PARTCLEARED);
			}
			receivecommand.set("isSync", Constant.ONE);
			receivecommand.store();
			pObject.setFlag(Constant.OK);
			pObject.setData("修改成功");
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	public static BasePosObject updateReceivePrint(Map<String,Object> mapIn,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			GenericValue geValue = delegator.findByPrimaryKey("ReceiveDoc", UtilMisc.toMap("docId", mapIn.get("docId")));
			if(geValue.get("printNum")==null){
				geValue.set("printNum", Long.parseLong("0"));
			}
			Long print =  geValue.getLong("printNum");
			geValue.set("printNum", print+1);
			geValue.store();
		} catch (Exception e) {
			pObject.setFlag(Constant.NG); 
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		pObject.setFlag(Constant.OK);
		return pObject; 
	}
}
