package org.ofbiz.order.returninwhsorder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.parameter.Parameter;
import org.ofbiz.order.preorder.OrderGenericValue;
import org.ofbiz.order.returnorder.ReturnOrderWorker;
import org.ofbiz.receive.webService.ReceiveWebWork;
import org.ofbiz.service.DispatchContext;
/**
 * Worker methods for ReturnInWhsOrderWorker Information
 */
public class ReturnInWhsOrderWorker {
	// 创建退货入库单
	public static BasePosObject createReturnInWhsOrder(DispatchContext dctx,
			ReturnInWhsOrder returnInWhsOrder) {
		BasePosObject pObject = new BasePosObject();
		Map<String, Object> detailMap = null;
		Map<String, Object> headerMap = null;
		
		List<Map<String, Object>> list = FastList.newInstance();
		try {
			// 获取HeaderMap 和 DetailList
			headerMap = getGVHeaderMap(returnInWhsOrder.getHeader());
			int len = returnInWhsOrder.getItem().size();
			//获取移动类型中的仓库
			Delegator delegator = dctx.getDelegator();
			String facilityId = null;
			GenericValue moveType = delegator.findByPrimaryKey("MovementType",
					UtilMisc.toMap("movementTypeId", (String) returnInWhsOrder
							.getHeader().getMovementTypeId()));
			if (UtilValidate.isNotEmpty(moveType.get("tofacilityTypeId"))) {
				List<GenericValue> gvFacility = delegator.findByAnd(
						"StoreFacilityView", UtilMisc.toMap("productStoreId",
								(String) returnInWhsOrder.getHeader()
										.getProductStoreId(), "facilityTypeId",
								(String) moveType.get("tofacilityTypeId")));
				if (UtilValidate.isNotEmpty(gvFacility)&&gvFacility.size()>0){
					facilityId = (String)gvFacility.get(0).get("facilityId");
				}
			}
			
			for (int i = 0; i < len; i++) {
				detailMap = getGVDtlMap(facilityId, returnInWhsOrder.getHeader(),
						returnInWhsOrder.getItem().get(i));
				list.add(detailMap);
			}
			// 调用添加入库单方法
			BasePosObject pObjectResult = ReceiveWebWork.reReceive(headerMap,
					list, dctx);
			if (pObjectResult.getFlag().equals(Constant.NG)) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg(pObjectResult.getMsg());
				return pObject;
			}
			// 更新退货单明细的退货入库数
			BasePosObject pObjectReturnOrder = ReturnOrderWorker
					.addReturnOrderWhsQty(dctx, returnInWhsOrder);
			if (pObjectReturnOrder.getFlag().equals(Constant.NG)) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg("更新退货入库数量未成功");
				return pObject;
			}
			pObject.setFlag(Constant.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pObject;
	}
	//修改退货入库单状态
	@SuppressWarnings("unused")
	public static BasePosObject updateReturnInWhsOrderStatus(DispatchContext dctx, Parameter parameter) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		GenericValue ReturnInWhsOrderHeader = null;
		try {
			String lastUpdateUserId = parameter.getLastUpdateUserId();
			Timestamp lastUpdateDocDate = parameter.getLastUpdateDocDate();//待用
			String docId = parameter.getDocId();
			String docStatus = parameter.getDocStatus();
			String status = parameter.getStatus();
			//根据主键docId 查询Header
			ReturnInWhsOrderHeader = delegator.findByPrimaryKey("ReceiveDoc", UtilMisc.toMap("docId", docId));
			String returnInWhsOrderDocStatus = (String)ReturnInWhsOrderHeader.get("docStatus");
			String returnInWhsOrderStatus = (String)ReturnInWhsOrderHeader.get("status");
			//订单状态为草稿,才能修改
			 if(!returnInWhsOrderDocStatus.equals(Constant.DRAFT)){
				 pObject.setFlag(Constant.NG);
	    		 pObject.setMsg("订单状态不是草稿");
				 return pObject;
			  }
			
			//查询非空且不是部分已清的订单状态（0,2）
			if(UtilValidate.isNotEmpty(returnInWhsOrderStatus) && !returnInWhsOrderStatus.equals(Constant.PARTCLEARED)){
				if( returnInWhsOrderStatus.equals(status)){
					pObject.setFlag(Constant.NG);
					pObject.setMsg("未清、已清状态不能更新为它本身");
					return pObject;
				}
			}
			//更新docStatus
			if(UtilValidate.isNotEmpty(docStatus)){
				ReturnInWhsOrderHeader.set("docStatus",docStatus);
			}
			//更新Status
			if(UtilValidate.isNotEmpty(status)){
				ReturnInWhsOrderHeader.set("status",status);
			}
			//跟新修改人
			ReturnInWhsOrderHeader.set("UpdateUserId",lastUpdateUserId);
			//更新修改时间
			Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			ReturnInWhsOrderHeader.set("UpdateDate",timestamp);
			ReturnInWhsOrderHeader.store();
			//如果业务状态都为已清，订单状态置为已清
			ReturnInWhsOrderHeader = delegator.findByPrimaryKey("ReceiveDoc", UtilMisc.toMap("docId", docId));
		    returnInWhsOrderStatus = (String)ReturnInWhsOrderHeader.get("status");
		    if(returnInWhsOrderStatus.equals(Constant.CLEAR)){
		    	ReturnInWhsOrderHeader.set("docStatus",Constant.CLEARED);
		    	ReturnInWhsOrderHeader.store();
		    }
			pObject.setFlag(Constant.OK);
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//修改退货入库单
	public static BasePosObject updateReturnInWhsOrder(DispatchContext dctx,ReturnInWhsOrder returnInWhsOrder) {
		//更新退订入库单头和行
		BasePosObject pObject = new BasePosObject ();
		Map<String, Object> detailMap = null;
		Map<String, Object> headerMap = null;
		List<Map<String,Object>> list = FastList.newInstance(); 
		try{
			//获取HeaderMap 和 DetailList
		    headerMap = getGVHeaderMap(returnInWhsOrder.getHeader());
		  //获取移动类型中的仓库
			Delegator delegator = dctx.getDelegator();
			String facilityId = null;
			GenericValue moveType = delegator.findByPrimaryKey("MovementType",
					UtilMisc.toMap("movementTypeId", (String) returnInWhsOrder
							.getHeader().getMovementTypeId()));
			if (UtilValidate.isNotEmpty(moveType.get("tofacilityTypeId"))) {
				List<GenericValue> gvFacility = delegator.findByAnd(
						"StoreFacilityView", UtilMisc.toMap("productStoreId",
								(String) returnInWhsOrder.getHeader()
										.getProductStoreId(), "facilityTypeId",
								(String) moveType.get("tofacilityTypeId")));
				if (UtilValidate.isNotEmpty(gvFacility)&&gvFacility.size()>0){
					facilityId = (String)gvFacility.get(0).get("facilityId");
				}
			}			
			int len=returnInWhsOrder.getItem().size();
			for(int i=0;i<len;i++){
				detailMap = getGVDtlMap(facilityId,returnInWhsOrder.getHeader(),returnInWhsOrder.getItem().get(i));
				list.add(detailMap);
			}
			//调用修改退货入库方法
			BasePosObject pObjectReceive = ReceiveWebWork.updateTReceive(headerMap, list, dctx);
			if (pObjectReceive.getFlag().equals(Constant.NG)) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg(pObjectReceive.getMsg());
				return pObject;
			}
			pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
	    	pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//根据Id查询退货入库单
	@SuppressWarnings("unchecked")
	public static BasePosObject getReturnInWhsOrderById(DispatchContext dctx,Parameter parameter) {
		BasePosObject pObject = new BasePosObject ();
		List<GenericValue> values = null;
		OrderValue order = new OrderValue();
		BasePosObject result = null;
		try{
	    	//查订单头
			result = getReturnInWhsOrderHeaderByCondition(dctx,parameter);
			values = (List<GenericValue>)result.getData();
			if (UtilValidate.isNotEmpty(result.getData())) {
				order.setHeader(values.get(0));
			}
	    	//查订单明细
			result = getReturnInWhsOrderDtlByCondition(dctx,parameter);
			values = (List<GenericValue>)result.getData();
	    	if (UtilValidate.isNotEmpty(result.getData())) {
	    		 order.setItem(values);
			}
	        pObject.setFlag(Constant.OK);
	    	pObject.setData(order);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//根据条件查询退货入库单
	@SuppressWarnings("unchecked")
	public static BasePosObject getReturnInWhsOrderByCondition(DispatchContext dctx, Map<String, Object> view) {
		BasePosObject pObject = new BasePosObject ();
		List<GenericValue> values = null;
		OrderGenericValue order = new OrderGenericValue();
		BasePosObject result = null;
		try{
			String docId = (String)view.get("docId");
			Parameter parameter = new Parameter();
	    	parameter.setDocId(docId);
	    	//查订单头
	    	result = getReturnInWhsOrderHeaderByCondition(dctx,parameter);
	    	values = (List<GenericValue>)result.getData();
			if (UtilValidate.isNotEmpty(result.getData())) {
				order.setHeader(values.get(0));	
			}
	    	//查订单明细
			 result = getReturnInWhsOrderDtlByCondition(dctx,parameter);
			 values = (List<GenericValue>)result.getData();
	    	 if (UtilValidate.isNotEmpty(result.getData())) {
	    		 order.setDetail(values);
			 }
	    	 pObject.setFlag(Constant.OK);
	    	 pObject.setData(order);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//根据条件查询退货入库单头
	public static BasePosObject getReturnInWhsOrderHeaderByCondition(DispatchContext dctx, Parameter parameter) {
		//传入condition查询
		BasePosObject pObject = new BasePosObject ();
		BasePosObject result = null;
		try{
			Map<String,Object> mapIn = FastMap.newInstance();
			String docId = parameter.getDocId();
			String baseEntry = parameter.getBaseEntry();
			String productStoreIdFrom = parameter.getProductStoreIdFrom();
			String productStoreId = parameter.getProductStoreId();
			String docStatus = parameter.getDocStatus();
			String startDate = parameter.getStartDate();
			String endDate = parameter.getEndDate();
			Timestamp docDate = parameter.getDocDate();
			mapIn.put("docId", docId);
	        mapIn.put("baseEntry", baseEntry);
	        mapIn.put("productStoreIdFrom", productStoreIdFrom);
	        mapIn.put("productStoreId", productStoreId);
		    mapIn.put("docStatus", docStatus);
			mapIn.put("startDate", startDate);
		    mapIn.put("endDate", endDate);
		    mapIn.put("docDate", docDate);
		    result = ReceiveWebWork.findReceiveDoc(mapIn, dctx);
			if(UtilValidate.isNotEmpty(result.getData())){
				pObject.setData(result.getData());
			}
			pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//根据条件查询退货入库单明细
	public static BasePosObject getReturnInWhsOrderDtlByCondition(DispatchContext dctx,  Parameter parameter) {
		//传入condition查询
		BasePosObject pObject = new BasePosObject ();
		Map<String,Object> mapIn = FastMap.newInstance();
		BasePosObject result = null;
		try{
			String docId = parameter.getDocId();
			mapIn.put("docId", docId);
			result = ReceiveWebWork.findReceiveItem(mapIn, dctx);
			if(UtilValidate.isNotEmpty(result.getData())){
				pObject.setData(result.getData());
			}
			pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
   
	// 根据参数的到Map<String, Object> ReturnInWhspOrderHeaderMap
	public static Map<String, Object> getGVHeaderMap(ReturnInWhsOrderHeader header) {
		Map<String, Object> headerMap = FastMap.newInstance();
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		try {
			headerMap.put("docId", header.getDocId());
			headerMap.put("extSystemNo", header.getExtSystemNo());
			headerMap.put("extDocNo", header.getExtDocNo());
			headerMap.put("docDate", header.getDocDate());
			headerMap.put("postingDate", header.getPostingDate());
			headerMap.put("docStatus", header.getDocStatus());
			headerMap.put("memo", header.getMemo());
			headerMap.put("updateDate",timestamp);
			headerMap.put("status", header.getStatus());
			 //订单状态非草稿状态置"1"
	        if(!header.getDocStatus().equals(Constant.DRAFT)){
	        	headerMap.put("isSync",Constant.ONE);
	        }else{
	        	headerMap.put("isSync",header.getIsSync());
	        }
			headerMap.put("baseEntry", header.getBaseEntry());
			headerMap.put("movementTypeId",header.getMovementTypeId());
			headerMap.put("userId", header.getUserId());
			headerMap.put("updateUserId",header.getUpdateUserId());
			headerMap.put("productStoreId",header.getProductStoreId());
			headerMap.put("productStoreIdFrom",header.getProductStoreIdFrom());
			headerMap.put("partyId", header.getPartyId());
			headerMap.put("partyIdFrom", header.getPartyIdFrom());
			headerMap.put("printNum", header.getPrintNum());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return headerMap;
	}
		
	// 根据参数的到Map<String, Object> ReturnInWhspOrderDtlMap
	public static Map<String, Object> getGVDtlMap(String facilityId,ReturnInWhsOrderHeader returnHeader,ReturnInWhsOrderDtl detail) {
		Map<String, Object> detailMap = FastMap.newInstance();
		try {
			detailMap.put("docId", detail.getDocId());
			detailMap.put("baseEntry", detail.getBaseEntry());
			detailMap.put("baseLineNo", detail.getBaseLineNo());
			detailMap.put("lineNo", detail.getLineNo());
			detailMap.put("memo", detail.getMemo());
			detailMap.put("quantity", detail.getQuantity());
			detailMap.put("isSequence", detail.getIsSequence());
			detailMap.put("idValue", detail.getIdValue());
			detailMap.put("productId", detail.getProductId());
			detailMap.put("sequenceId", detail.getSequenceId());
			if(UtilValidate.isNotEmpty(detail.getFacilityId())){
				detailMap.put("facilityId", detail.getFacilityId());
			}else{
				detailMap.put("facilityId", facilityId);
				returnHeader.getMovementTypeId();
			}
			detailMap.put("facilityIdFrom", detail.getFacilityIdFrom());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return detailMap;
	}
		
	//根据参数值修改GenericValue ReturnInWhspOrderHeader(set)
	public static GenericValue updateGVreturnInWhsOrderHeader(GenericValue returnInWhsOrderHeader, ReturnInWhsOrderHeader header) {
		try {
			Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			returnInWhsOrderHeader.set("docId", header.getDocId());
			returnInWhsOrderHeader.set("extSystemNo", header.getExtSystemNo());
			returnInWhsOrderHeader.set("extDocNo", header.getExtDocNo());
			returnInWhsOrderHeader.set("docDate", header.getDocDate());
			returnInWhsOrderHeader.set("postingDate", header.getPostingDate());
			returnInWhsOrderHeader.set("docStatus", header.getDocStatus());
			returnInWhsOrderHeader.set("memo", header.getMemo());
			returnInWhsOrderHeader.set("updateDate",timestamp);
			returnInWhsOrderHeader.set("status", header.getStatus());
			returnInWhsOrderHeader.set("isSync", header.getIsSync());
			returnInWhsOrderHeader.set("baseEntry", header.getBaseEntry());
			returnInWhsOrderHeader.set("movementTypeId",header.getMovementTypeId());
			returnInWhsOrderHeader.set("userId", header.getUserId());
			returnInWhsOrderHeader.set("updateUserId",header.getUpdateUserId());
			returnInWhsOrderHeader.set("productStoreId",header.getProductStoreId());
			returnInWhsOrderHeader.set("productStoreIdFrom",header.getProductStoreIdFrom());
			returnInWhsOrderHeader.set("partyId", header.getPartyId());
			returnInWhsOrderHeader.set("partyIdFrom", header.getPartyIdFrom());
			returnInWhsOrderHeader.set("printNum", header.getPrintNum());
			returnInWhsOrderHeader.store();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return returnInWhsOrderHeader;
	}


}
