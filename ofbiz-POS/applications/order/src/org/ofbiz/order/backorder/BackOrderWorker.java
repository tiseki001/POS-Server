package org.ofbiz.order.backorder;
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
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.order.parameter.Parameter;
import org.ofbiz.order.preorder.OrderGenericValue;
import org.ofbiz.order.preorder.PreOrderWorker;
import org.ofbiz.order.prerefundorder.PreRefundOrder;
import org.ofbiz.order.prerefundorder.PreRefundOrderWorker;
import org.ofbiz.service.DispatchContext;
/**
 * Worker methods for BackOrderWorker Information
 */
public class BackOrderWorker {
	//创建退订单和订金返还单
	public static BasePosObject createBackOrderAll(DispatchContext dctx,
			BackOrder backOrder, PreRefundOrder preRefundOrder) {
		BasePosObject pObject = new BasePosObject();
		BasePosObject pObjectResult = null;
		try {
			// 预订单不为空写入预订单
			if (UtilValidate.isNotEmpty(backOrder)) {
				pObjectResult = createBackOrder(dctx, backOrder);
				if (pObjectResult.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectResult.getMsg());
					return pObject;
				}
			}
			// 订金预收单不为空写入订金预收单
			if (UtilValidate.isNotEmpty(preRefundOrder)) {
				pObjectResult = PreRefundOrderWorker.createPreRefundOrder(dctx,preRefundOrder);
				if (pObjectResult.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectResult.getMsg());
					return pObject;
				}
			}
			if (UtilValidate.isEmpty(backOrder)&& UtilValidate.isEmpty(preRefundOrder)) {
				pObject.setFlag(Constant.NG);
			} else {
				pObject.setFlag(Constant.OK);
			}
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//同时修改退订单和创建订金返还单
	public static BasePosObject updateBackOrderAll(DispatchContext dctx,
			BackOrder backOrder, PreRefundOrder preRefundOrder) {
		BasePosObject pObject = new BasePosObject();
		BasePosObject pObjectResult = null;
		try {
			// 退订单不为空写入退订单
			if (UtilValidate.isNotEmpty(backOrder)) {
				pObjectResult = updateBackOrder(dctx, backOrder);
				if (pObjectResult.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectResult.getMsg());
					return pObject;
				}
			}
			// 订金预收单不为空写入订金预收单
			if (UtilValidate.isNotEmpty(preRefundOrder)) {
				pObjectResult = PreRefundOrderWorker.createPreRefundOrder(dctx,preRefundOrder);
				if (pObjectResult.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectResult.getMsg());
					return pObject;
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
	//创建退订单
	public static BasePosObject createBackOrder(DispatchContext dctx, BackOrder backOrder) {
		GenericValue backOrderDtl = null;
		BasePosObject pObject =  new BasePosObject();
		try{
			//写入BackOrderHeader
			GenericValue backOrderHeader = getGVBackOrderHeader(dctx,backOrder.getHeader());
			if(UtilValidate.isEmpty(backOrderHeader)){
				pObject.setFlag(Constant.NG);
				pObject.setMsg("创建退订单头错误");
				return pObject;
			}
			//获取退订单明细的数量len
			int len = backOrder.getDetail().size();
			for(int i=0;i<len;i++){
				//写入BackOrderDtl
				backOrderDtl = getGVBackOrderDtl(dctx,backOrder.getDetail().get(i));
				if(UtilValidate.isEmpty(backOrderDtl)){
					pObject.setFlag(Constant.NG);
					pObject.setMsg("创建退订单明细错误");
					return pObject;
				}
			}
			//预定单明细中写入退订数量
		    BasePosObject pObjectResult =  PreOrderWorker.addPreOrderRtnQt(dctx,backOrder);
		    if(pObjectResult.getFlag().equals(Constant.NG)){
		    	pObject.setFlag(Constant.NG);
				pObject.setMsg("预定单明细中写入退订数量错误");
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
	//更新退订单状态
	@SuppressWarnings("unused")
	public static BasePosObject updateBackOrderStatus(DispatchContext dctx, Parameter parameter) {
		BasePosObject pObject =  new BasePosObject();
		Delegator delegator = dctx.getDelegator();
		GenericValue backOrderHeader = null;
		try{
			String lastUpdateUserId = parameter.getLastUpdateUserId();
			Timestamp lastUpdateDocDate = parameter.getLastUpdateDocDate();
			String docId = parameter.getDocId();
			String docStatus = parameter.getDocStatus();
			String refundStatus = parameter.getRefundStatus();
			//根据主键docId 查询BackOrderHeader
			backOrderHeader = delegator.findByPrimaryKey("BackOrderHeader",UtilMisc.toMap("docId", docId));
			if(UtilValidate.isEmpty(backOrderHeader)){
				pObject.setFlag(Constant.NG);
				pObject.setMsg("无此退订单");
				return pObject;
			}
			String	backOrderDocStatus = (String) backOrderHeader.get("docStatus");
			String  backOrderRefundStatus = (String)backOrderHeader.get("refundStatus");
			 //如订单状态为已清或作废，则不能再改变状态
		    if(backOrderDocStatus.equals(Constant.CLEARED)||backOrderDocStatus.equals(Constant.INVALID)){
		    	pObject.setFlag(Constant.NG);
				pObject.setMsg("订单状态为已清或作废时不能再改变其状态");
				return pObject;
		    }
			 if(UtilValidate.isNotEmpty(backOrderRefundStatus) && !backOrderRefundStatus.equals(Constant.PARTCLEARED)){
				if(backOrderRefundStatus.equals(refundStatus)){
					pObject.setFlag(Constant.NG);
					pObject.setMsg("未清、已清状态不能更新为其本身");
					return pObject;
				}
			 }
			//更新docStatus
			if(UtilValidate.isNotEmpty(docStatus)){
				backOrderHeader.set("docStatus",docStatus);
			}
			//更新reFundStatus
			if(UtilValidate.isNotEmpty(refundStatus)){
				backOrderHeader.set("refundStatus",refundStatus);
			}
			//更新修改人
			backOrderHeader.set("lastUpdateUserId",lastUpdateUserId);
			//更新修改时间
			Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			backOrderHeader.set("lastUpdateDocDate",timestamp);
			backOrderHeader.set("isSync",Constant.ONE);
			backOrderHeader.store();
			//如果业务状态都为已清，订单状态置为已清
			backOrderHeader = delegator.findByPrimaryKey("BackOrderHeader",UtilMisc.toMap("docId", docId));
			refundStatus = (String)backOrderHeader.get("refundStatus");
		    if(refundStatus.equals(Constant.CLEAR)){
		    	backOrderHeader.set("docStatus",Constant.CLEARED);
		    	backOrderHeader.store();
		    }
			pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//更新退订单
	public static BasePosObject updateBackOrder(DispatchContext dctx, BackOrder backOrder) {
		Delegator delegator = dctx.getDelegator();
		GenericValue backOrderHeader = null;
		BasePosObject pObject =  new BasePosObject();
		try{
			String docId = backOrder.getHeader().getDocId();
		    //根据docId 查询 退订单头
		    backOrderHeader = delegator.findByPrimaryKey("BackOrderHeader", UtilMisc.toMap("docId", docId));
		    if(UtilValidate.isEmpty(backOrderHeader)){
		    	pObject.setFlag(Constant.NG);
				pObject.setMsg("无此订单");
				return pObject;
		    }
		    //订单状态为草稿,才能修改
			 String docStatus = (String)backOrderHeader.get("docStatus");
			 if(!docStatus.equals(Constant.DRAFT)){
				 pObject.setFlag(Constant.NG);
	    		 pObject.setMsg("订单状态不是草稿");
				 return pObject;
			  }
		    //更新退订单头
		    updateGVBackOrderHeader(backOrderHeader,backOrder.getHeader());
		    //删除原有明细
		    delegator.removeByAnd("BackOrderDtl",UtilMisc.toMap("docId",docId));
			//写入新的明细
			int len = backOrder.getDetail().size();
			for(int i=0;i<len;i++){
				//创建新的preOrderDtl
				getGVBackOrderDtl(dctx,backOrder.getDetail().get(i));
			}
			pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//根据docId查找BackOrder
	@SuppressWarnings("unchecked")
	public static BasePosObject getBackOrderById(DispatchContext dctx, Map<String, Object> view) {
	 	 BasePosObject pObject =  new BasePosObject();
	 	 List<GenericValue> values = null;
		 OrderGenericValue order = new OrderGenericValue();
		 BasePosObject objectResult =  null;
	     try{
	    	 String docId = (String)view.get("docId");
	    	 String where = "DOC_ID='" + docId + "'";
			 Map<String, Object> viewMap = FastMap.newInstance();
			 viewMap.put("where", where);
			 //查询Headers
			 objectResult =  getBackOrderHeaderByCondition(dctx,viewMap);
			 values = (List<GenericValue>) objectResult.getData();
			 if (UtilValidate.isNotEmpty(objectResult.getData())) {
				 order.setHeader(values.get(0));
			 }
			 // 查询Details
			 objectResult = getBackOrderDtlByCondition(dctx,viewMap);
			 values = (List<GenericValue>) objectResult.getData();
			 if (UtilValidate.isNotEmpty(objectResult.getData())) {
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
	
	//根据Id查询BackOrderHeader
	@SuppressWarnings("unchecked")
	public static BasePosObject getBackOrderHeaderById(DispatchContext dctx,JSONObject jsonObj) {
		BasePosObject pObject = new BasePosObject ();
		List<GenericValue> headers = null;
		BasePosObject result = null; 
		try {
			String docId = (String)jsonObj.get("docId");
			String where = "DOC_ID='"+docId+"'";
			Map<String, Object> viewMap = FastMap.newInstance();
			viewMap.put("where",where);
			result = getBackOrderHeaderByCondition(dctx,viewMap);
			headers = (List<GenericValue>)result.getData();
			if(UtilValidate.isNotEmpty(headers)){
				pObject.setData(headers.get(0));
			}
			pObject.setFlag(Constant.OK);
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//根据条件查询BackOrderHeader
	public static BasePosObject getBackOrderHeaderByCondition(DispatchContext dctx, Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject =  new BasePosObject();
		List<String> orderBy = FastList.newInstance();
		List<GenericValue> backOrderHeaders1 = null;
		try {
			String where = (String)view.get("where");
			EntityCondition mainCond = EntityCondition.makeConditionWhere(where);
			orderBy.add("lastUpdateDocDate DESC");//更新时间倒序排列
		    backOrderHeaders1 = delegator.findList("BackOrderHeaderMessage",mainCond, null,orderBy,null, false);
			pObject.setFlag(Constant.OK);
			if(UtilValidate.isNotEmpty(backOrderHeaders1)){
				pObject.setData(backOrderHeaders1);
			}
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//根据Id查询BackOrderDtl
	@SuppressWarnings("unchecked")
	public static BasePosObject getBackOrderDtlById(DispatchContext dctx,JSONObject jsonObj) {
		BasePosObject pObject = new BasePosObject ();
		List<GenericValue> details = null;
		BasePosObject result = null; 
		try {
			String docId = (String)jsonObj.get("docId");
			String where = "DOC_ID='"+docId+"'";
			Map<String, Object> viewMap = FastMap.newInstance();
			viewMap.put("where",where);
			result = getBackOrderDtlByCondition(dctx,viewMap);
			details = (List<GenericValue>)result.getData();
			if(UtilValidate.isNotEmpty(details)){
				pObject.setData(details);
			}
			pObject.setFlag(Constant.OK);
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//根据条件查询BackOrderDtl
	public static BasePosObject getBackOrderDtlByCondition(DispatchContext dctx, Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		List<GenericValue> backOrderDtls1 = null;
		BasePosObject pObject =  new BasePosObject();
		try {
			String where = (String)view.get("where");
			EntityCondition mainCond = EntityCondition.makeConditionWhere(where);
			//根据条件查找 List<GenericValue>
			backOrderDtls1 = delegator.findList("BackOrderDtlMessage",mainCond, null, UtilMisc.toList("docId"),null, false);
			pObject.setFlag(Constant.OK);
			if(UtilValidate.isNotEmpty(backOrderDtls1)){
				pObject.setData(backOrderDtls1);
			}
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//添加退订单退款额
	public static BasePosObject addBackOrderPreRefundAmount(DispatchContext dctx,Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		//根据SQL查询PreRefundAmount 退款额,RebateAmount订金总额,FundStatus退款状态
		BasePosObject pObject =  new BasePosObject();
		try {
			String docId = (String)view.get("docId");
			String lastUpdateUserId = (String)view.get("lastUpdateUserId");
			Timestamp lastUpdateDocDate = (Timestamp)view.get("lastUpdateDocDate");
			BigDecimal amount = (BigDecimal)view.get("amount");
			boolean fundStatusCleared = true;
			GenericValue backOrderHeader = delegator.findByPrimaryKey("BackOrderHeader",UtilMisc.toMap("docId", docId));
        	BigDecimal preRefundAmount = (BigDecimal)backOrderHeader.get("preRefundAmount");
        	BigDecimal rebateAmount = (BigDecimal)backOrderHeader.get("rebateAmount");
        	BigDecimal newPreRefundAmount = preRefundAmount.add(amount) ;
        	double raAmount = rebateAmount.doubleValue();
        	double prAmount = newPreRefundAmount.doubleValue();
        	if (prAmount > raAmount) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg("退订金额大于订单总金额");
				return pObject;
			} else if (prAmount < raAmount) {
				fundStatusCleared = false;
			}
        	backOrderHeader.set("preRefundAmount", newPreRefundAmount);
        	backOrderHeader.store();
        	Parameter parameter = new Parameter();
            parameter.setDocId(docId);
            parameter.setLastUpdateUserId(lastUpdateUserId);
            parameter.setLastUpdateDocDate(lastUpdateDocDate);
            if(fundStatusCleared){
            	parameter.setRefundStatus(Constant.CLEAR);
            }else{
            	parameter.setRefundStatus(Constant.PARTCLEARED);
            }
        	//更新退订单付款状态
        	updateBackOrderStatus( dctx,parameter);
	        pObject.setFlag(Constant.OK);
	    }catch(Exception e){
	    	pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
	    	e.printStackTrace();
	    }   
		return pObject;
	}
	
	//根据参数得到GenericValue backOrderHeader(put)
	public static GenericValue getGVBackOrderHeader(DispatchContext dctx,BackOrderHeader backOrderHeader){
		GenericValue gbackOrderHeader = null;
		Map<String,Object> backOrderHeaderMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		try{
			backOrderHeaderMap.put("docId",backOrderHeader.getDocId());
			backOrderHeaderMap.put("docNo",backOrderHeader.getDocNo());
			backOrderHeaderMap.put("partyId",backOrderHeader.getPartyId());
			backOrderHeaderMap.put("storeId",backOrderHeader.getStoreId());
			backOrderHeaderMap.put("baseEntry",backOrderHeader.getBaseEntry());
			backOrderHeaderMap.put("extSystemNo",backOrderHeader.getExtSystemNo());
			backOrderHeaderMap.put("extDocNo",backOrderHeader.getExtDocNo());
			backOrderHeaderMap.put("postingDate",backOrderHeader.getPostingDate());
			backOrderHeaderMap.put("docStatus",backOrderHeader.getDocStatus());
			backOrderHeaderMap.put("memo",backOrderHeader.getMemo());
			backOrderHeaderMap.put("createUserId",backOrderHeader.getCreateUserId());
	        Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			backOrderHeaderMap.put("createDocDate",timestamp);
			backOrderHeaderMap.put("lastUpdateUserId",backOrderHeader.getLastUpdateUserId());
			backOrderHeaderMap.put("lastUpdateDocDate",timestamp);
			backOrderHeaderMap.put("memberId",backOrderHeader.getMemberId());
			backOrderHeaderMap.put("primeAmount",backOrderHeader.getPrimeAmount());
			backOrderHeaderMap.put("rebateAmount",backOrderHeader.getRebateAmount());
			backOrderHeaderMap.put("preRefundAmount",backOrderHeader.getPreRefundAmount());
			backOrderHeaderMap.put("refundStatus",backOrderHeader.getRefundStatus());
			backOrderHeaderMap.put("memberPointAmount",backOrderHeader.getMemberPointAmount());
			backOrderHeaderMap.put("preRefundStoreId",backOrderHeader.getPreRefundStoreId());
			backOrderHeaderMap.put("approvalUserId",backOrderHeader.getApprovalUserId());
			backOrderHeaderMap.put("approvalDate",backOrderHeader.getApprovalDate());
			  //订单状态非草稿状态置"1"
	        if(!backOrderHeader.getDocStatus().equals(Constant.DRAFT)){
	        	backOrderHeaderMap.put("isSync",Constant.ONE);
	        }else{
	        	backOrderHeaderMap.put("isSync",backOrderHeader.getIsSync());
	        }
			backOrderHeaderMap.put("printNum",backOrderHeader.getPrintNum());
			backOrderHeaderMap.put("salesId",backOrderHeader.getSalesId());
			backOrderHeaderMap.put("attrName1",backOrderHeader.getAttrName1());
			backOrderHeaderMap.put("attrName2",backOrderHeader.getAttrName2());
			backOrderHeaderMap.put("attrName3",backOrderHeader.getAttrName3());
			backOrderHeaderMap.put("attrName4",backOrderHeader.getAttrName4());
			backOrderHeaderMap.put("attrName5",backOrderHeader.getAttrName5());
			backOrderHeaderMap.put("attrName6",backOrderHeader.getAttrName6());
			backOrderHeaderMap.put("attrName7",backOrderHeader.getAttrName7());
			backOrderHeaderMap.put("attrName8",backOrderHeader.getAttrName8());
			backOrderHeaderMap.put("attrName9",backOrderHeader.getAttrName9());
			gbackOrderHeader = delegator.makeValue("BackOrderHeader", backOrderHeaderMap);
			gbackOrderHeader.create();
		}catch(Exception e){
			e.printStackTrace();
		}
		return gbackOrderHeader;
	}
	
	//根据参数得到 GenericValue backOrderDtl(put)
	public static GenericValue getGVBackOrderDtl(DispatchContext dctx,BackOrderDtl backOrderDtl){
		Delegator delegator = dctx.getDelegator();
		Map<String,Object> backOrderDtlMap = FastMap.newInstance();
		GenericValue gbackOrderDtl = null;
		 try{
			backOrderDtlMap.put("docId",backOrderDtl.getDocId());
			backOrderDtlMap.put("lineNo",backOrderDtl.getLineNo());
			backOrderDtlMap.put("lineNoBaseEntry",backOrderDtl.getLineNoBaseEntry());
			backOrderDtlMap.put("productId", backOrderDtl.getProductId());
			backOrderDtlMap.put("productName", backOrderDtl.getProductName());
			backOrderDtlMap.put("barCodes", backOrderDtl.getBarCodes());
			backOrderDtlMap.put("extNo", backOrderDtl.getExtNo());
			backOrderDtlMap.put("isSequence", backOrderDtl.getIsSequence());
			backOrderDtlMap.put("serialNo", backOrderDtl.getSerialNo());
			backOrderDtlMap.put("unitPrice", backOrderDtl.getUnitPrice());
			backOrderDtlMap.put("rebatePrice", backOrderDtl.getRebatePrice());
			backOrderDtlMap.put("quantity", backOrderDtl.getQuantity());
			backOrderDtlMap.put("rebateAmount", backOrderDtl.getRebateAmount());
			backOrderDtlMap.put("bomAmount", backOrderDtl.getBomAmount());
			backOrderDtlMap.put("memo", backOrderDtl.getMemo());
			backOrderDtlMap.put("productSalesPolicyId", backOrderDtl.getProductSalesPolicyId());
			backOrderDtlMap.put("productSalesPolicyNo", backOrderDtl.getProductSalesPolicyNo());
			backOrderDtlMap.put("bomId", backOrderDtl.getBomId());
			backOrderDtlMap.put("salesId", backOrderDtl.getSalesId());
			backOrderDtlMap.put("memberPoint", backOrderDtl.getMemberPoint());
			backOrderDtlMap.put("isGift", backOrderDtl.getIsGift());
			backOrderDtlMap.put("approvalUserId", backOrderDtl.getApprovalUserId());
			backOrderDtlMap.put("isMainProduct", backOrderDtl.getIsMainProduct());
			backOrderDtlMap.put("isSequence", backOrderDtl.getIsSequence());
			backOrderDtlMap.put("productSalesPolicyId", backOrderDtl.getProductSalesPolicyId());
			backOrderDtlMap.put("productSalesPolicyNo", backOrderDtl.getProductSalesPolicyNo());
			backOrderDtlMap.put("bomId", backOrderDtl.getBomId());
	        backOrderDtlMap.put("attrName1",backOrderDtl.getAttrName1());
	        backOrderDtlMap.put("attrName2",backOrderDtl.getAttrName2());
	        backOrderDtlMap.put("attrName3",backOrderDtl.getAttrName3());
	        backOrderDtlMap.put("attrName4",backOrderDtl.getAttrName4());
	        backOrderDtlMap.put("attrName5",backOrderDtl.getAttrName5());
	        backOrderDtlMap.put("attrName6",backOrderDtl.getAttrName6());
	        backOrderDtlMap.put("attrName7",backOrderDtl.getAttrName7());
	        backOrderDtlMap.put("attrName8",backOrderDtl.getAttrName8());
	        backOrderDtlMap.put("attrName9",backOrderDtl.getAttrName9());
	        gbackOrderDtl = delegator.makeValue("BackOrderDtl", backOrderDtlMap);
	        gbackOrderDtl.create();
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return gbackOrderDtl;
	}
		
	//根据参数值修改GenericValue backOrderHeader(set)
	public static GenericValue updateGVBackOrderHeader(GenericValue gbackOrderHeader,BackOrderHeader backOrderHeader){
		try{
			gbackOrderHeader.set("docId",backOrderHeader.getDocId());
	        gbackOrderHeader.set("docNo",backOrderHeader.getDocNo());
	        gbackOrderHeader.set("partyId",backOrderHeader.getPartyId());
	        gbackOrderHeader.set("storeId",backOrderHeader.getStoreId());
	        gbackOrderHeader.set("baseEntry",backOrderHeader.getBaseEntry());
	        gbackOrderHeader.set("extSystemNo",backOrderHeader.getExtSystemNo());
	        gbackOrderHeader.set("extDocNo",backOrderHeader.getExtDocNo());
	        gbackOrderHeader.set("postingDate",backOrderHeader.getPostingDate());
	        gbackOrderHeader.set("docStatus",backOrderHeader.getDocStatus());
	        gbackOrderHeader.set("memo",backOrderHeader.getMemo());
	        gbackOrderHeader.set("createUserId",backOrderHeader.getCreateUserId());
	        gbackOrderHeader.set("createDocDate",backOrderHeader.getCreateDocDate());
	        gbackOrderHeader.set("lastUpdateUserId",backOrderHeader.getLastUpdateUserId());
	        Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			gbackOrderHeader.set("lastUpdateDocDate",timestamp);
	        gbackOrderHeader.set("memberId",backOrderHeader.getMemberId());
	        gbackOrderHeader.set("primeAmount",backOrderHeader.getPrimeAmount());
	        gbackOrderHeader.set("rebateAmount",backOrderHeader.getRebateAmount());
	        gbackOrderHeader.set("preRefundAmount",backOrderHeader.getPreRefundAmount());
	        gbackOrderHeader.set("refundStatus",backOrderHeader.getRefundStatus());
	        gbackOrderHeader.set("memberPointAmount",backOrderHeader.getMemberPointAmount());
	        gbackOrderHeader.set("preRefundStoreId",backOrderHeader.getPreRefundStoreId());
	        gbackOrderHeader.set("approvalUserId",backOrderHeader.getApprovalUserId());
	        gbackOrderHeader.set("approvalDate",backOrderHeader.getApprovalDate());
	        gbackOrderHeader.set("isSync",backOrderHeader.getIsSync());
	        gbackOrderHeader.set("printNum",backOrderHeader.getPrintNum());
	        gbackOrderHeader.set("salesId",backOrderHeader.getSalesId());
	        gbackOrderHeader.set("attrName1",backOrderHeader.getAttrName1());
	        gbackOrderHeader.set("attrName2",backOrderHeader.getAttrName2());
	        gbackOrderHeader.set("attrName3",backOrderHeader.getAttrName3());
	        gbackOrderHeader.set("attrName4",backOrderHeader.getAttrName4());
	        gbackOrderHeader.set("attrName5",backOrderHeader.getAttrName5());
	        gbackOrderHeader.set("attrName6",backOrderHeader.getAttrName6());
	        gbackOrderHeader.set("attrName7",backOrderHeader.getAttrName7());
	        gbackOrderHeader.set("attrName8",backOrderHeader.getAttrName8());
	        gbackOrderHeader.set("attrName9",backOrderHeader.getAttrName9());
	        gbackOrderHeader.store();
		}catch(Exception e){
			e.printStackTrace();
		}
		return gbackOrderHeader;
	}
	
	
}
