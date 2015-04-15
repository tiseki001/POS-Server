package org.ofbiz.order.returnorder;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.order.parameter.Parameter;
import org.ofbiz.order.preorder.OrderGenericValue;
import org.ofbiz.order.refundorder.RefundOrder;
import org.ofbiz.order.refundorder.RefundOrderWork;
import org.ofbiz.order.returninwhsorder.ReturnInWhsOrder;
import org.ofbiz.order.returninwhsorder.ReturnInWhsOrderWorker;
import org.ofbiz.order.saleorder.SaleOrderWork;
import org.ofbiz.receive.webService.ReceiveWebWork;
import org.ofbiz.service.DispatchContext;

/**
 * Worker methods for ReturnOrderWorker Information
 */
public class ReturnOrderWorker {
	//生成退货单，退款单，退货入库单
	public static BasePosObject createReturnOrderAll(DispatchContext dctx,
			ReturnOrder returnOrder, RefundOrder refundOrder,
			ReturnInWhsOrder returnInWhsOrder) {
		BasePosObject pObject = new BasePosObject();
		BasePosObject pObjectResult = null;
		try{
			// 退货单不为空写入退货单
			if (UtilValidate.isNotEmpty(returnOrder)) {
				pObjectResult = createReturnOrder(dctx, returnOrder);
				if (pObjectResult.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectResult.getMsg());
					return pObject;
				}
			}
			// 退款单不为空写入退款单
			if (UtilValidate.isNotEmpty(refundOrder)) {
				pObjectResult = RefundOrderWork.createRefundOrder(dctx,refundOrder);
				if (pObjectResult.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectResult.getMsg());
					return pObject;
				}
			}
			// 退货入库单不为空写入退货入库单
			if (UtilValidate.isNotEmpty(returnInWhsOrder)) {
				pObjectResult = ReturnInWhsOrderWorker.createReturnInWhsOrder(dctx, returnInWhsOrder);
				if (pObjectResult.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectResult.getMsg());
					return pObject;
				}
			}
			//退货单/退款单/退货入库单同时为空时报错
			if(UtilValidate.isEmpty(returnOrder)&&UtilValidate.isEmpty(refundOrder)&&UtilValidate.isEmpty(returnInWhsOrder)){
				pObject.setFlag(Constant.NG);
				pObject.setMsg("退货单/退款单/退货入库单同时为空");
			} else {
				pObject.setFlag(Constant.OK);
			}
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//修改退货单，生成退款单，退货入库单
	public static BasePosObject updateReturnOrderAll(DispatchContext dctx,
			ReturnOrder returnOrder, RefundOrder refundOrder,
			ReturnInWhsOrder returnInWhsOrder) {
		BasePosObject pObject = new BasePosObject();
		BasePosObject pObjectResult = null;
		try{
			// 退货单不为空修改退货单
			if (UtilValidate.isNotEmpty(returnOrder)) {
				pObjectResult = updateReturnOrder(dctx, returnOrder);
				if (pObjectResult.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectResult.getMsg());
					return pObject;
				}
			}
			// 退款单不为空写入退款单
			if (UtilValidate.isNotEmpty(refundOrder)) {
				pObjectResult = RefundOrderWork.createRefundOrder(dctx,refundOrder);
				if (pObjectResult.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectResult.getMsg());
					return pObject;
				}
			}
			pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	
	//创建退货单
	public static BasePosObject createReturnOrder(DispatchContext dctx,ReturnOrder returnOrder) {
		BasePosObject pObject = new BasePosObject ();
		GenericValue returnOrderHeader = null;
		GenericValue returnOrderDtl = null;
		try{
			//写入returnOrderHeader
			returnOrderHeader = getGVReturnOrderHeader(dctx,returnOrder.getHeader());
			if(UtilValidate.isEmpty(returnOrderHeader)){
				pObject.setFlag(Constant.NG);
				pObject.setMsg("创建退货单头错误");
				return pObject;
			}
		    //获取退货单明细的数量len
			int len = returnOrder.getDetail().size();
		    for(int i=0;i<len;i++){
				//写入returnOrderDtl
				returnOrderDtl = getGVReturnOrderDtl(dctx,returnOrder.getDetail().get(i));
				if(UtilValidate.isEmpty(returnOrderDtl)){
					pObject.setFlag(Constant.NG);
					pObject.setMsg("创建退订单明细错误");
					return pObject;
				}
			}
		    //销售订单中修改退货数量
		    BasePosObject pObjectResult =  SaleOrderWork.addSalesOrderRtnQty(dctx,returnOrder);
		    if(pObjectResult.getFlag().equals(Constant.NG)){
		    	pObject.setFlag(Constant.NG);
				pObject.setMsg("销售订单明细中更新退货数量错误");
				return pObject;
		    }
		    //明细解锁数量非空不等于零，解锁中转库占用
		    Map<String,Object> mapHeader = getMapHeader(returnOrder.getHeader());
		    List<Map<String,Object>> listItem = FastList.newInstance();
		    for(int i=0;i<len;i++){
		    	Long unlockedQuantity = returnOrder.getDetail().get(i).getUnLockedQuantity();
		    	if(UtilValidate.isNotEmpty(unlockedQuantity) && unlockedQuantity!=0){
		    		listItem.add(getMapDtl(returnOrder.getDetail().get(i)));
		    	}
		    }
		    if(UtilValidate.isNotEmpty(listItem) && listItem.size()>0){
			    BasePosObject pObjectReceive =  ReceiveWebWork.commadReceive(mapHeader, listItem, dctx);
			    if(pObjectReceive.getFlag().equals(Constant.NG)){
			    	pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectReceive.getMsg());
					return pObject;
			    }
		    }
			pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//修改退货单状态
	@SuppressWarnings("unused")
	public static BasePosObject updateReturnOrderStatus(DispatchContext dctx,Parameter parameter) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject ();
		GenericValue returnOrderHeader = null;//数据库中的数值对象
		try{
			String lastUpdateUserId = parameter.getLastUpdateUserId();
			Timestamp lastUpdateDocDate = parameter.getLastUpdateDocDate();//待用
			String docId = parameter.getDocId();
			String docStatus = parameter.getDocStatus();
			String refundStatus = parameter.getRefundStatus();
			String warehouseStatus = parameter.getWarehouseStatus();
			String exchangeStatus = parameter.getExchangeStatus();
			
			//根据主键docId 查询PreOrderHeader
			returnOrderHeader = delegator.findByPrimaryKey("ReturnOrderHeader", UtilMisc.toMap("docId", docId));
			String returnOrderDocStatus = (String)returnOrderHeader.get("docStatus");
			String returnOrderReFundStatus = (String)returnOrderHeader.get("refundStatus");
			String returnOrderWarehouseStatus = (String)returnOrderHeader.get("warehouseStatus");
			String returnOrderExchangeStatus = (String)returnOrderHeader.get("exchangeStatus");
			//如订单状态为已清或作废，则不能再改变状态
		    if(returnOrderDocStatus.equals(Constant.CLEARED)||returnOrderDocStatus.equals(Constant.INVALID)){
		    	pObject.setFlag(Constant.NG);
				pObject.setMsg("订单状态为已清或作废时不能再改变其状态");
				return pObject;
		    }
			//参数 docStatus，fundStatus，backStatus 非空的且不是部分已清的Status状态
			//如果所有状态都一致返回错误信息
			if(UtilValidate.isNotEmpty(returnOrderReFundStatus) && !returnOrderReFundStatus.equals(Constant.PARTCLEARED)
					&&  UtilValidate.isNotEmpty(returnOrderWarehouseStatus) && !returnOrderWarehouseStatus.equals(Constant.PARTCLEARED)
					&&  UtilValidate.isNotEmpty(returnOrderExchangeStatus) && !returnOrderExchangeStatus.equals(Constant.PARTCLEARED)
					){
				if( returnOrderReFundStatus.equals(refundStatus)||returnOrderWarehouseStatus.equals(warehouseStatus)||returnOrderExchangeStatus.equals(exchangeStatus)){
					pObject.setFlag(Constant.NG);
					pObject.setMsg("未清、已清状态不能更新为它本身");
					return pObject;
				}
			}
			
			//更新docStatus
			if(UtilValidate.isNotEmpty(docStatus)){
				returnOrderHeader.set("docStatus",docStatus);
			}
			//更新refundStatus
			if(UtilValidate.isNotEmpty(refundStatus)){
				returnOrderHeader.set("refundStatus",refundStatus);
			}
			//更新WarehouseStatus
			if(UtilValidate.isNotEmpty(warehouseStatus)){
				returnOrderHeader.set("warehouseStatus",warehouseStatus);
			}
			//更新exchangeStatus
			if(UtilValidate.isNotEmpty(exchangeStatus)){
				returnOrderHeader.set("exchangeStatus",exchangeStatus);
			}
			//跟新修改人
			returnOrderHeader.set("lastUpdateUserId",lastUpdateUserId);
			//更新修改时间
			Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			returnOrderHeader.set("lastUpdateDocDate",timestamp);
			returnOrderHeader.store();
			//如果业务状态都为已清，订单状态置为已清
			returnOrderHeader = delegator.findByPrimaryKey("ReturnOrderHeader", UtilMisc.toMap("docId", docId));
			returnOrderReFundStatus = (String)returnOrderHeader.get("refundStatus");
			//returnOrderWarehouseStatus = (String)returnOrderHeader.get("warehouseStatus");
			returnOrderExchangeStatus = (String)returnOrderHeader.get("exchangeStatus");
		    if(returnOrderReFundStatus.equals(Constant.CLEAR) && returnOrderExchangeStatus.equals(Constant.CLEAR)){
		    	returnOrderHeader.set("docStatus",Constant.CLEARED);
		    	returnOrderHeader.store();
		    }
			pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//修改退货单
	public static BasePosObject updateReturnOrder(DispatchContext dctx,ReturnOrder returnOrder) {
		BasePosObject pObject = new BasePosObject ();
		Delegator delegator = dctx.getDelegator();
		GenericValue returnOrderHeader = null;
		try{
		    String docId = returnOrder.getHeader().getDocId();
		    //根据docId 查询 退货单头
		    returnOrderHeader = delegator.findByPrimaryKey("ReturnOrderHeader", UtilMisc.toMap("docId", docId));
		    if(UtilValidate.isEmpty(returnOrderHeader)){
		    	pObject.setFlag(Constant.NG);
		    	pObject.setMsg("退货单ID错误");
		    	return pObject;
		    }
		    //订单状态为草稿,才能修改
			 String docStatus = (String)returnOrderHeader.get("docStatus");
			 if(!docStatus.equals(Constant.DRAFT)){
				 pObject.setFlag(Constant.NG);
	    		 pObject.setMsg("订单状态不是草稿");
				 return pObject;
			  }
		    //更新退货单头
		    updateGVReturnOrderHeader(returnOrderHeader,returnOrder.getHeader());
		    //删除原有明细
		    delegator.removeByAnd("ReturnOrderDtl",UtilMisc.toMap("docId",docId));
			//写入新的明细
			int len = returnOrder.getDetail().size();
			for(int i=0;i<len;i++){
				//创建新的returnOrderDtl
				getGVReturnOrderDtl(dctx,returnOrder.getDetail().get(i));
			}
			pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
	    	pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//根据Id查询退货单
	@SuppressWarnings("unchecked")
	public static BasePosObject getReturnOrderById(DispatchContext dctx,Map<String, Object>view) {
		BasePosObject pObject = new BasePosObject ();
	    List<GenericValue> values= null;
	    OrderGenericValue order = new OrderGenericValue();
	    BasePosObject result = null;
	     try {
	    	 String docId = (String)view.get("docId");
	    	 String where = "DOC_ID='" + docId + "'";
			 Map<String, Object> viewMap = FastMap.newInstance();
			 viewMap.put("where", where);
			 //查询Headers
			 result =  getReturnOrderHeaderByCondition(dctx,viewMap);
			 values = (List<GenericValue>) result.getData();
			 if (UtilValidate.isNotEmpty(result.getData())) {
				 order.setHeader(values.get(0));
			 }
			// 查询Details
			result = getReturnOrderDtlByCondition(dctx,viewMap);
			values = (List<GenericValue>) result.getData();
			if (UtilValidate.isNotEmpty(result.getData())) {
				 order.setDetail(values);
			}
			 pObject.setData(order);
			 pObject.setFlag(Constant.OK);
		} catch (Exception e) {
			 pObject.setFlag(Constant.NG);
			 pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}

	//根据ID查询退货单头
	@SuppressWarnings("unchecked")
	public static BasePosObject getReturnOrderHeaderById(DispatchContext dctx,JSONObject jsonObj) {
		BasePosObject pObject = new BasePosObject ();
		List<GenericValue> orderHeaders = null;
		BasePosObject result = null;
		try {
			String docId = (String)jsonObj.get("docId");
			String where = "DOC_ID='"+docId+"'";
			Map<String, Object> viewMap = FastMap.newInstance();
			viewMap.put("where",where);
			result = getReturnOrderHeaderByCondition(dctx,viewMap);
			orderHeaders = (List<GenericValue>)result.getData();
			pObject.setFlag(Constant.OK);
			if(UtilValidate.isNotEmpty(orderHeaders)){
				pObject.setData(orderHeaders.get(0));
			}
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	
	//根据条件查询退货单头
	public static BasePosObject getReturnOrderHeaderByCondition(DispatchContext dctx, Map<String, Object> view) {
		BasePosObject pObject = new BasePosObject ();
		Delegator delegator = dctx.getDelegator();
		List<GenericValue> returnOrderHeaders1 = null;
		List<String> orderBy = FastList.newInstance();
		try {
			String where = (String)view.get("where");
			EntityCondition mainCond = EntityCondition.makeConditionWhere(where);
			orderBy.add("lastUpdateDocDate DESC");//更新时间倒序排列
			returnOrderHeaders1 = delegator.findList("ReturnOrderHeaderMessage",mainCond, null,orderBy,null,false);
			if(UtilValidate.isNotEmpty(returnOrderHeaders1)){
				pObject.setData(returnOrderHeaders1);
			}
			pObject.setFlag(Constant.OK);
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//根据ID查询退货单明细
	@SuppressWarnings("unchecked")
	public static BasePosObject getReturnOrderDtlById(DispatchContext dctx,JSONObject jsonObj) {
		BasePosObject pObject = new BasePosObject ();
		List<GenericValue> orderDtls = null;
		BasePosObject result = null; 
		try {
			String docId = (String)jsonObj.get("docId");
			String where = "DOC_ID='"+docId+"'";
			Map<String, Object> viewMap = FastMap.newInstance();
			viewMap.put("where",where);
			result = getReturnOrderDtlByCondition(dctx,viewMap);
			orderDtls = (List<GenericValue>)result.getData();
			if(UtilValidate.isNotEmpty(orderDtls)){
				pObject.setData(orderDtls);
			}
			pObject.setFlag(Constant.OK);
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//根据条件查询退货单明细
	public static BasePosObject getReturnOrderDtlByCondition(DispatchContext dctx, Map<String, Object> view) {
		BasePosObject pObject = new BasePosObject ();
		Delegator delegator = dctx.getDelegator();
		List<GenericValue> returnOrderDtls1 = null;
		EntityCondition mainCond = null;
		try{
			String where = (String)view.get("where");
			mainCond = EntityCondition.makeConditionWhere(where);
			//根据条件查找 List<GenericValue>
			returnOrderDtls1 = delegator.findList("ReturnOrderDtlMessage",mainCond, null, UtilMisc.toList("docId"),null, false);
			if(UtilValidate.isNotEmpty(returnOrderDtls1)){
				pObject.setData(returnOrderDtls1);
			}
			pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//增加退货单退款金额(退款单创建时调用)
	public static BasePosObject addReturnOrderRefundAmount(DispatchContext dctx,Map<String, Object> view) {
		BasePosObject pObject = new BasePosObject ();
		Delegator delegator = dctx.getDelegator();
		Parameter parameter = null;
		 try{
			 String docId = (String)view.get("docId");
		     String lastUpdateUserId = (String)view.get("lastUpdateUserId");
		     Timestamp lastUpdateDocDate = (Timestamp)view.get("lastUpdateDocDate");
			 BigDecimal amount = (BigDecimal)view.get("amount");
			 boolean refundStatusCleared = true;
			 GenericValue returnOrderHeader = delegator.findByPrimaryKey("ReturnOrderHeader",UtilMisc.toMap("docId", docId));
	         BigDecimal returnOrderRefundAmount = (BigDecimal)returnOrderHeader.get("refundAmount");//退款额
	         BigDecimal rebateAmount = (BigDecimal)returnOrderHeader.get("rebateAmount");
	         BigDecimal newPreRefundAmount = returnOrderRefundAmount.add(amount) ;
	         double raAmount = rebateAmount.doubleValue();
	         double prAmount = newPreRefundAmount.doubleValue();
	         if (prAmount > raAmount) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg("退货金额大于订单总金额");
					return pObject;
				} else if (prAmount < raAmount) {
					refundStatusCleared = false;
				}
	            returnOrderHeader.set("refundAmount", newPreRefundAmount);
	            returnOrderHeader.store();
    			parameter = new Parameter();
    			parameter.setDocId(docId);
    			parameter.setLastUpdateUserId(lastUpdateUserId);
    			parameter.setLastUpdateDocDate(lastUpdateDocDate);
    			if(refundStatusCleared){
    	            parameter.setRefundStatus(Constant.CLEAR);
    	        }else{
    	            parameter.setRefundStatus(Constant.PARTCLEARED);
    	        }
    			//更新退货单表头的退款状态
    			updateReturnOrderStatus( dctx,parameter);
		        pObject.setFlag(Constant.OK);
		    }catch(Exception e){
		    	pObject.setFlag(Constant.NG);
	    		pObject.setMsg(e.getMessage());
		    	e.printStackTrace();
		    }   
		return pObject;
	}
	
	//增加退货入库单退货数量(退货入库单创建时调用)
	public static BasePosObject addReturnOrderWhsQty(DispatchContext dctx,ReturnInWhsOrder returnInWhsOrder) {
		BasePosObject pObject = new BasePosObject ();
		Delegator delegator = dctx.getDelegator();
		GenericValue returnOrderDtl = null;
		List<GenericValue> returnOrderDtls=null;
		Parameter parameter = null;
		 try{
			String docId = returnInWhsOrder.getHeader().getBaseEntry();
	    	String lastUpdateUserId = returnInWhsOrder.getHeader().getUpdateUserId();
	    	Timestamp lastUpdateDocDate = returnInWhsOrder.getHeader().getUpdateDate();
	    	Long returnInWhsOrderQuantity = null;
			Long lineNo= null;
			Long returnOrderDtlQuantity = null;
			boolean WarehouseStatusCleared = true;
			int len = returnInWhsOrder.getItem().size();
			for(int i= 0; i<len;i++){
				returnInWhsOrderQuantity = returnInWhsOrder.getItem().get(i).getQuantity();
				lineNo = Long.valueOf(returnInWhsOrder.getItem().get(i).getBaseLineNo());
				returnOrderDtl = delegator.findByPrimaryKey("ReturnOrderDtl",UtilMisc.toMap("docId",docId,"lineNo",lineNo));
				returnOrderDtlQuantity = (Long)returnOrderDtl.get("warehouseQuantity") + returnInWhsOrderQuantity;		
				if (returnOrderDtlQuantity > (Long)returnOrderDtl.get("quantity")) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg("退货数量大于预订数量");
					return pObject;
				} 
				returnOrderDtl.set("warehouseQuantity", returnOrderDtlQuantity);
				returnOrderDtl.store();
			}
			returnOrderDtls = delegator.findByAnd("ReturnOrderDtl",UtilMisc.toMap("docId",docId));
			for(int i= 0; i<returnOrderDtls.size();i++){
				returnInWhsOrderQuantity = (Long)returnOrderDtl.get("warehouseQuantity"); 
				returnOrderDtlQuantity = (Long)returnOrderDtl.get("quantity"); 
				if (returnInWhsOrderQuantity < returnOrderDtlQuantity) {
					WarehouseStatusCleared = false;
					break;
				}
			}
			parameter = new Parameter();
			parameter.setDocId(docId);
			parameter.setLastUpdateUserId(lastUpdateUserId);
			parameter.setLastUpdateDocDate(lastUpdateDocDate);
			if(WarehouseStatusCleared){
	            parameter.setWarehouseStatus(Constant.CLEAR);
	        }else{
	            parameter.setWarehouseStatus(Constant.PARTCLEARED);
	        }
			//更新退货单表头的入库状态
			updateReturnOrderStatus( dctx,parameter);
		    pObject.setFlag(Constant.OK);
		    }catch(Exception e){
		    	pObject.setFlag(Constant.NG);
				pObject.setMsg(e.getMessage());
		    	e.printStackTrace();
		    }   
		return pObject;
	}
	
	//获取mapHeader
	public static Map<String,Object> getMapHeader(ReturnOrderHeader returnOrderHeader){
		Map<String,Object> returnOrderHeaderMap = FastMap.newInstance();
		try{
			returnOrderHeaderMap.put("baseEntry",returnOrderHeader.getDocId());
			returnOrderHeaderMap.put("movementTypeId",returnOrderHeader.getMovementTypeId());//需要添加
			returnOrderHeaderMap.put("partyId",returnOrderHeader.getPartyId());
			//returnOrderHeaderMap.put("statusId",returnOrderHeader.getDocStatus());
			returnOrderHeaderMap.put("productStoreId",returnOrderHeader.getStoreId());
			returnOrderHeaderMap.put("updateUserId",returnOrderHeader.getLastUpdateUserId());
			returnOrderHeaderMap.put("updateDate",returnOrderHeader.getLastUpdateDocDate());
		}catch(Exception e){
			e.printStackTrace();
		}
		return returnOrderHeaderMap;
	}
	//获取mapDtl
	public static Map<String,Object> getMapDtl(ReturnOrderDtl returnOrderDtl){
		Map<String,Object> returnOrderDtlMap = FastMap.newInstance();
		 try{
			 returnOrderDtlMap.put("docId",returnOrderDtl.getDocId());
			 returnOrderDtlMap.put("lineNo",returnOrderDtl.getLineNo());
			 returnOrderDtlMap.put("facilityId",returnOrderDtl.getFacilityId());//仓库ID需要添加
			 returnOrderDtlMap.put("productId", returnOrderDtl.getProductId());
			 returnOrderDtlMap.put("sequenceId", returnOrderDtl.getSerialNo());
			 returnOrderDtlMap.put("quantity", returnOrderDtl.getUnLockedQuantity());
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return returnOrderDtlMap;
	}
	
	//根据参数得到GenericValue returnOrderHeader(put)
	public static GenericValue getGVReturnOrderHeader(DispatchContext dctx,ReturnOrderHeader returnOrderHeader){
		GenericValue greturnOrderHeader = null;
		Map<String,Object> returnOrderHeaderMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		try{
			returnOrderHeaderMap.put("docId",returnOrderHeader.getDocId());
			returnOrderHeaderMap.put("docNo",returnOrderHeader.getDocNo());
			returnOrderHeaderMap.put("partyId",returnOrderHeader.getPartyId());
			returnOrderHeaderMap.put("storeId",returnOrderHeader.getStoreId());
			returnOrderHeaderMap.put("movementTypeId",returnOrderHeader.getMovementTypeId());
			returnOrderHeaderMap.put("baseEntry",returnOrderHeader.getBaseEntry());
			returnOrderHeaderMap.put("extSystemNo",returnOrderHeader.getExtSystemNo());
			returnOrderHeaderMap.put("extDocNo",returnOrderHeader.getExtDocNo());
			returnOrderHeaderMap.put("postingDate",returnOrderHeader.getPostingDate());
			returnOrderHeaderMap.put("docStatus",returnOrderHeader.getDocStatus());
			returnOrderHeaderMap.put("memo",returnOrderHeader.getMemo());
			returnOrderHeaderMap.put("createUserId",returnOrderHeader.getCreateUserId());
	        Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			returnOrderHeaderMap.put("createDocDate",timestamp);
			returnOrderHeaderMap.put("lastUpdateUserId",returnOrderHeader.getLastUpdateUserId());
			returnOrderHeaderMap.put("lastUpdateDocDate",timestamp);
			returnOrderHeaderMap.put("memberId",returnOrderHeader.getMemberId());
			returnOrderHeaderMap.put("primeAmount",returnOrderHeader.getPrimeAmount());
			returnOrderHeaderMap.put("rebateAmount",returnOrderHeader.getRebateAmount());
			returnOrderHeaderMap.put("refundAmount",returnOrderHeader.getRefundAmount());
			returnOrderHeaderMap.put("refundStatus",returnOrderHeader.getRefundStatus());
			returnOrderHeaderMap.put("warehouseStatus",returnOrderHeader.getWarehouseStatus());
			returnOrderHeaderMap.put("memberPointAmount",returnOrderHeader.getMemberPointAmount());
			returnOrderHeaderMap.put("refundStoreId",returnOrderHeader.getRefundStoreId());
			returnOrderHeaderMap.put("receiveStoreId",returnOrderHeader.getReceiveStoreId());
			returnOrderHeaderMap.put("approvalUserId",returnOrderHeader.getApprovalUserId());
			returnOrderHeaderMap.put("approvalDate",returnOrderHeader.getApprovalDate());
			  //订单状态非草稿状态置"1"
	        if(!returnOrderHeader.getDocStatus().equals(Constant.DRAFT)){
	        	returnOrderHeaderMap.put("isSync",Constant.ONE);
	        }else{
	        	returnOrderHeaderMap.put("isSync",returnOrderHeader.getIsSync());
	        }
	        returnOrderHeaderMap.put("printNum",returnOrderHeader.getPrintNum());
	        returnOrderHeaderMap.put("salesId",returnOrderHeader.getSalesId());
	        returnOrderHeaderMap.put("exchangeStatus",returnOrderHeader.getExchangeStatus());
			returnOrderHeaderMap.put("attrName1",returnOrderHeader.getAttrName1());
			returnOrderHeaderMap.put("attrName2",returnOrderHeader.getAttrName2());
			returnOrderHeaderMap.put("attrName3",returnOrderHeader.getAttrName3());
			returnOrderHeaderMap.put("attrName4",returnOrderHeader.getAttrName4());
			returnOrderHeaderMap.put("attrName5",returnOrderHeader.getAttrName5());
			returnOrderHeaderMap.put("attrName6",returnOrderHeader.getAttrName6());
			returnOrderHeaderMap.put("attrName7",returnOrderHeader.getAttrName7());
			returnOrderHeaderMap.put("attrName8",returnOrderHeader.getAttrName8());
			returnOrderHeaderMap.put("attrName9",returnOrderHeader.getAttrName9());
			greturnOrderHeader = delegator.makeValue("ReturnOrderHeader", returnOrderHeaderMap);
			greturnOrderHeader.create();
		}catch(Exception e){
			e.printStackTrace();
		}
		return greturnOrderHeader;
	}
	
	//根据参数得到 GenericValue returnOrderDtl(put)
	public static GenericValue getGVReturnOrderDtl(DispatchContext dctx,ReturnOrderDtl returnOrderDtl){
		Delegator delegator = dctx.getDelegator();
		Map<String,Object> returnOrderDtlMap = FastMap.newInstance();
		GenericValue greturnOrderDtl = null;
		 try{
			returnOrderDtlMap.put("docId",returnOrderDtl.getDocId());
			returnOrderDtlMap.put("lineNo",returnOrderDtl.getLineNo());
			returnOrderDtlMap.put("lineNoBaseEntry",returnOrderDtl.getLineNoBaseEntry());
			returnOrderDtlMap.put("productId", returnOrderDtl.getProductId());
			returnOrderDtlMap.put("productName", returnOrderDtl.getProductName());
			returnOrderDtlMap.put("barCodes", returnOrderDtl.getBarCodes());
			returnOrderDtlMap.put("extNo", returnOrderDtl.getExtNo());
			returnOrderDtlMap.put("isSequence", returnOrderDtl.getIsSequence());
			returnOrderDtlMap.put("serialNo", returnOrderDtl.getSerialNo());
			returnOrderDtlMap.put("unitPrice", returnOrderDtl.getUnitPrice());
			returnOrderDtlMap.put("rebatePrice", returnOrderDtl.getRebatePrice());
			returnOrderDtlMap.put("quantity", returnOrderDtl.getQuantity());
			returnOrderDtlMap.put("warehouseQuantity", returnOrderDtl.getWarehouseQuantity());
			returnOrderDtlMap.put("unLockedQuantity", returnOrderDtl.getUnLockedQuantity());
			returnOrderDtlMap.put("rebateAmount", returnOrderDtl.getRebateAmount());
			returnOrderDtlMap.put("bomAmount", returnOrderDtl.getBomAmount());
			returnOrderDtlMap.put("memo", returnOrderDtl.getMemo());
			returnOrderDtlMap.put("isMainProduct", returnOrderDtl.getIsMainProduct());
			returnOrderDtlMap.put("productSalesPolicyId", returnOrderDtl.getProductSalesPolicyId());
			returnOrderDtlMap.put("productSalesPolicyNo", returnOrderDtl.getProductSalesPolicyNo());
			returnOrderDtlMap.put("bomId", returnOrderDtl.getBomId());
			returnOrderDtlMap.put("salesId", returnOrderDtl.getSalesId());
			returnOrderDtlMap.put("memberPoint", returnOrderDtl.getMemberPoint());
			returnOrderDtlMap.put("isGift", returnOrderDtl.getIsGift());
			returnOrderDtlMap.put("facilityId", returnOrderDtl.getFacilityId());
			returnOrderDtlMap.put("approvalUserId", returnOrderDtl.getApprovalUserId());
			returnOrderDtlMap.put("faultTypeId", returnOrderDtl.getFaultTypeId());
			returnOrderDtlMap.put("faultDescription", returnOrderDtl.getFaultDescription());
			returnOrderDtlMap.put("baseEntry", returnOrderDtl.getBaseEntry());
	        returnOrderDtlMap.put("attrName1",returnOrderDtl.getAttrName1());
	        returnOrderDtlMap.put("attrName2",returnOrderDtl.getAttrName2());
	        returnOrderDtlMap.put("attrName3",returnOrderDtl.getAttrName3());
	        returnOrderDtlMap.put("attrName4",returnOrderDtl.getAttrName4());
	        returnOrderDtlMap.put("attrName5",returnOrderDtl.getAttrName5());
	        returnOrderDtlMap.put("attrName6",returnOrderDtl.getAttrName6());
	        returnOrderDtlMap.put("attrName7",returnOrderDtl.getAttrName7());
	        returnOrderDtlMap.put("attrName8",returnOrderDtl.getAttrName8());
	        returnOrderDtlMap.put("attrName9",returnOrderDtl.getAttrName9());
	        greturnOrderDtl = delegator.makeValue("ReturnOrderDtl", returnOrderDtlMap);
	        greturnOrderDtl.create();
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return greturnOrderDtl;
	}
	//根据参数值修改GenericValue returnOrderHeader(set)
	public static GenericValue updateGVReturnOrderHeader(GenericValue greturnOrderHeader,ReturnOrderHeader returnOrderHeader){
		try{
			greturnOrderHeader.set("docId",returnOrderHeader.getDocId());
	        greturnOrderHeader.set("docNo",returnOrderHeader.getDocNo());
	        greturnOrderHeader.set("partyId",returnOrderHeader.getPartyId());
	        greturnOrderHeader.set("storeId",returnOrderHeader.getStoreId());
	        greturnOrderHeader.set("movementTypeId",returnOrderHeader.getMovementTypeId());
	        greturnOrderHeader.set("baseEntry",returnOrderHeader.getBaseEntry());
	        greturnOrderHeader.set("extSystemNo",returnOrderHeader.getExtSystemNo());
	        greturnOrderHeader.set("extDocNo",returnOrderHeader.getExtDocNo());
	        greturnOrderHeader.set("postingDate",returnOrderHeader.getPostingDate());
	        greturnOrderHeader.set("docStatus",returnOrderHeader.getDocStatus());
	        greturnOrderHeader.set("memo",returnOrderHeader.getMemo());
	        greturnOrderHeader.set("createUserId",returnOrderHeader.getCreateUserId());
	        greturnOrderHeader.set("createDocDate",returnOrderHeader.getCreateDocDate());
	        greturnOrderHeader.set("lastUpdateUserId",returnOrderHeader.getLastUpdateUserId());
	        Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			greturnOrderHeader.set("lastUpdateDocDate",timestamp);
	        greturnOrderHeader.set("memberId",returnOrderHeader.getMemberId());
	        greturnOrderHeader.set("primeAmount",returnOrderHeader.getPrimeAmount());
	        greturnOrderHeader.set("rebateAmount",returnOrderHeader.getRebateAmount());
	        greturnOrderHeader.set("refundAmount",returnOrderHeader.getRefundAmount());
	        greturnOrderHeader.set("refundStatus",returnOrderHeader.getRefundStatus());
	        greturnOrderHeader.set("warehouseStatus",returnOrderHeader.getWarehouseStatus());
	        greturnOrderHeader.set("memberPointAmount",returnOrderHeader.getMemberPointAmount());
	        greturnOrderHeader.set("refundStoreId",returnOrderHeader.getRefundStoreId());
	        greturnOrderHeader.set("receiveStoreId",returnOrderHeader.getReceiveStoreId());
	        greturnOrderHeader.set("approvalUserId",returnOrderHeader.getApprovalUserId());
	        greturnOrderHeader.set("approvalDate",returnOrderHeader.getApprovalDate());
	        greturnOrderHeader.set("isSync",returnOrderHeader.getIsSync());
	        greturnOrderHeader.set("printNum",returnOrderHeader.getPrintNum());
	        greturnOrderHeader.set("salesId",returnOrderHeader.getSalesId());
	        greturnOrderHeader.set("exchangeStatus",returnOrderHeader.getExchangeStatus());
	        greturnOrderHeader.set("attrName1",returnOrderHeader.getAttrName1());
	        greturnOrderHeader.set("attrName2",returnOrderHeader.getAttrName2());
	        greturnOrderHeader.set("attrName3",returnOrderHeader.getAttrName3());
	        greturnOrderHeader.set("attrName4",returnOrderHeader.getAttrName4());
	        greturnOrderHeader.set("attrName5",returnOrderHeader.getAttrName5());
	        greturnOrderHeader.set("attrName6",returnOrderHeader.getAttrName6());
	        greturnOrderHeader.set("attrName7",returnOrderHeader.getAttrName7());
	        greturnOrderHeader.set("attrName8",returnOrderHeader.getAttrName8());
	        greturnOrderHeader.set("attrName9",returnOrderHeader.getAttrName9());
	        greturnOrderHeader.store();
		}catch(Exception e){
			e.printStackTrace();
		}
		return greturnOrderHeader;
	}
	
	
}
