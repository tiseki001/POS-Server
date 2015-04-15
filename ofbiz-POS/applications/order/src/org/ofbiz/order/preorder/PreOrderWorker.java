package org.ofbiz.order.preorder;
import java.math.BigDecimal;
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
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.order.backorder.BackOrder;
import org.ofbiz.order.parameter.Parameter;
import org.ofbiz.order.precollectionorder.PreCollectionOrder;
import org.ofbiz.order.precollectionorder.PreCollectionOrderWorker;
import org.ofbiz.service.DispatchContext;
/**
 * Worker methods for PreOrderWorker Information
 */
public class PreOrderWorker {
	public static String module = PreOrderWorker.class.getName();
	//创建预订单和订金预收单
	public static BasePosObject createSalesOrderAll(DispatchContext dctx,
			PreOrder preOrder, PreCollectionOrder preCollectionOrder) {
		BasePosObject pObject = new BasePosObject();
		BasePosObject pObjectResult = null;
		try {
			// 预订单不为空写入预订单
			if (UtilValidate.isNotEmpty(preOrder)) {
				pObjectResult = createPreOrder(dctx, preOrder);
				if (pObjectResult.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectResult.getMsg());
					return pObject;
				}
			}
			// 订金预收单不为空写入订金预收单
			if (UtilValidate.isNotEmpty(preCollectionOrder)) {
				pObjectResult = PreCollectionOrderWorker.createPreCollectionOrder(dctx,preCollectionOrder);
				if (pObjectResult.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectResult.getMsg());
					return pObject;
				}
			}
			if (UtilValidate.isEmpty(preOrder)&& UtilValidate.isEmpty(preCollectionOrder)) {
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
	//同时修改预订单创建订金预收单
	public static BasePosObject updatePreOrderAll(DispatchContext dctx,
			PreOrder preOrder, PreCollectionOrder preCollectionOrder) {
		BasePosObject pObject = new BasePosObject();
		BasePosObject pObjectResult = null;
		try {
			// 预订单不为空修改预订单
			if (UtilValidate.isNotEmpty(preOrder)) {
				pObjectResult = updatePreOrder(dctx, preOrder);
				if (pObjectResult.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectResult.getMsg());
					return pObject;
				}
			}
			// 订金预收单不为空创建订金预收单
			if (UtilValidate.isNotEmpty(preCollectionOrder)) {
				pObjectResult = PreCollectionOrderWorker.createPreCollectionOrder(dctx, preCollectionOrder);
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
	 //创建预订单
	 public static BasePosObject createPreOrder(DispatchContext dctx,PreOrder preOrder){
		 GenericValue syncDoc = null;
		 Map<String,Object> syncDocMap = FastMap.newInstance();
		 BasePosObject pObject = new BasePosObject ();
		 Delegator delegator = dctx.getDelegator();
		 try{
			 //写入Header
			 getGVPreOrderHeader(dctx,preOrder.getHeader());
			 int len = preOrder.getDetail().size();
			 //写入Detail
			 for(int i=0;i<len;i++){
				getGVPreOrderDtl(dctx,preOrder.getDetail().get(i));
			 }
			 String storeId = preOrder.getHeader().getStoreId();
			 String collectionStoreId = preOrder.getHeader().getCollectionStoreId();
			 if(UtilValidate.isNotEmpty(storeId)&& UtilValidate.isNotEmpty(collectionStoreId)){
				if(!storeId.equals(collectionStoreId)){
					//其他门店同步表中插入预订单storeId = collectionStoreId
					syncDocMap.put("docId",preOrder.getHeader().getDocId());
					syncDocMap.put("docType",Constant.PRE);
					syncDocMap.put("storeId",collectionStoreId);
					syncDoc = delegator.makeValue("SyncDoc", syncDocMap);
					syncDoc.create();
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
	 //更新预订单状态
	@SuppressWarnings("unused")
	public static BasePosObject updatePreOrderStatus(DispatchContext dctx, Parameter parameter) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject ();
		GenericValue preOrderHeader = null;
		try{
			String lastUpdateUserId = parameter.getLastUpdateUserId();
			Timestamp lastUpdateDocDate = parameter.getLastUpdateDocDate();//待用
			String docId = parameter.getDocId();
			String docStatus = parameter.getDocStatus();
			String fundStatus = parameter.getFundStatus();
			String backStatus = parameter.getBackStatus();
			String salesStatus = parameter.getSalesStatus();
			//根据主键docId 查询Header状态
			preOrderHeader = delegator.findByPrimaryKey("PreOrderHeader", UtilMisc.toMap("docId", docId));
			String   preOrderDocStatus = (String)preOrderHeader.get("docStatus");
			String   preOrderFundStatus = (String)preOrderHeader.get("fundStatus");
			String   preOrderBackStatus = (String)preOrderHeader.get("backStatus");
			String   preOrderSalesStatus = (String)preOrderHeader.get("salesStatus");
		    //如订单状态为已清或作废，则不能再改变状态
		    if(preOrderDocStatus.equals(Constant.CLEARED)||preOrderDocStatus.equals(Constant.INVALID)){
		    	pObject.setFlag(Constant.NG);
				pObject.setMsg("订单状态为已清或作废时不能再改变其状态");
				return pObject;
		    }
			//参数 docStatus，fundStatus，backStatus，salesStatus 非空的且不是部分已清的Status状态（0,2）
			if(UtilValidate.isNotEmpty(preOrderFundStatus)&& !preOrderFundStatus.equals(Constant.PARTCLEARED) 
					&& UtilValidate.isNotEmpty(preOrderBackStatus)&& !preOrderBackStatus.equals(Constant.PARTCLEARED)
					&& UtilValidate.isNotEmpty(preOrderSalesStatus)&& !preOrderSalesStatus.equals(Constant.PARTCLEARED)){
				if(preOrderFundStatus.equals(fundStatus) || preOrderBackStatus.equals(backStatus) || 
						preOrderSalesStatus.equals(salesStatus)){
					pObject.setFlag(Constant.NG);
					pObject.setMsg("未清、已清状态不能更新为它本身");
					return pObject;
				}
			}
			//更新docStatus
			if(UtilValidate.isNotEmpty(docStatus)){
				preOrderHeader.set("docStatus",docStatus);
			}
			//更新fundStatus
			if(UtilValidate.isNotEmpty(fundStatus)){
				preOrderHeader.set("fundStatus",fundStatus);
			}
			//更新backStatus
			if(UtilValidate.isNotEmpty(backStatus)){
				preOrderHeader.set("backStatus",backStatus);
			}
			//更新salesStatus
			if(UtilValidate.isNotEmpty(salesStatus)){
				preOrderHeader.set("salesStatus",salesStatus);
			}
			//更新修改人
			preOrderHeader.set("lastUpdateUserId",lastUpdateUserId);
			//更新修改时间
			Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			preOrderHeader.set("lastUpdateDocDate",timestamp);
			preOrderHeader.set("isSync",Constant.ONE);
			preOrderHeader.store();
			//如果（收款，退订）或（收款，已销售）状态为已清，订单状态置为已清
			preOrderHeader = delegator.findByPrimaryKey("PreOrderHeader", UtilMisc.toMap("docId", docId));
		    preOrderFundStatus = (String)preOrderHeader.get("fundStatus");
		    preOrderBackStatus = (String)preOrderHeader.get("backStatus");
		    preOrderSalesStatus = (String)preOrderHeader.get("salesStatus");
		    if((preOrderFundStatus.equals(Constant.CLEAR) && preOrderBackStatus.equals(Constant.CLEAR)) || 
		    		(preOrderFundStatus.equals(Constant.CLEAR) && preOrderSalesStatus.equals(Constant.CLEAR)) ){
		    	preOrderHeader.set("docStatus",Constant.CLEARED);
		    	preOrderHeader.store();
		    }
			pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	 //更新预订单
	public static BasePosObject updatePreOrder(DispatchContext dctx, PreOrder preOrder) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject ();
		GenericValue preOrderHeader = null;
		try{
		    String docId = preOrder.getHeader().getDocId();
		    //根据docId 查询 预订单头
		    preOrderHeader = delegator.findByPrimaryKey("PreOrderHeader", UtilMisc.toMap("docId", docId));
		    if(UtilValidate.isEmpty(preOrderHeader)){
		    	pObject.setFlag(Constant.NG);
		    	pObject.setMsg("订单ID错误");
		    	return pObject;
		    }
		    //订单状态为草稿,才能修改
			 String docStatus = (String)preOrderHeader.get("docStatus");
			 if(!docStatus.equals(Constant.DRAFT)){
				 pObject.setFlag(Constant.NG);
	    		 pObject.setMsg("订单状态不是草稿");
				 return pObject;
			  }
		    //更新预订单头
		    updateGVPreOrderHeader(preOrderHeader,preOrder.getHeader());
		    //删除原有明细
		    delegator.removeByAnd("PreOrderDtl",UtilMisc.toMap("docId",docId));
			//写入新的明细
			int len = preOrder.getDetail().size();
			for(int i=0;i<len;i++){
				//创建新的preOrderDtl
				getGVPreOrderDtl(dctx,preOrder.getDetail().get(i));
			}
			pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
	    	pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
	        return pObject;
		}
    //根据docId查找PreOrder
	 @SuppressWarnings("unchecked")
	public static BasePosObject getPreOrderById(DispatchContext dctx, Map<String, Object> view) {
		 BasePosObject pObject = new BasePosObject ();
	     List<GenericValue> values = null;
		 OrderGenericValue order = new OrderGenericValue();
		 BasePosObject result = null;
	     try {
	    	 String docId = (String)view.get("docId");
	    	 String where = "DOC_ID='" + docId + "'";
			 Map<String, Object> viewMap = FastMap.newInstance();
			 viewMap.put("where", where);
			 //查询Headers
			 result =  getPreOrderHeaderByCondition(dctx,viewMap);
			 values = (List<GenericValue>) result.getData();
			 if(UtilValidate.isNotEmpty(values)){
				 order.setHeader(values.get(0));
			 }
			 // 查询Details
			 result = getPreOrderDtlByCondition(dctx,viewMap);
			 values = (List<GenericValue>) result.getData();
			 if(UtilValidate.isNotEmpty(values)){
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
	 
	 //根据docId删除PreOrder
	 public static BasePosObject deletePreOrderById(DispatchContext dctx, Map<String, Object> view) {
		 Delegator delegator = dctx.getDelegator();
		 BasePosObject pObject = new BasePosObject ();
		 GenericValue preOrderHeader = null;
	     try {
	    	 String docId = (String)view.get("docId");
	    	 //根据ID查询PreOrderHeader数值对象
			 preOrderHeader = delegator.findByPrimaryKey("PreOrderHeader", UtilMisc.toMap("docId", docId));
			 if(UtilValidate.isEmpty(preOrderHeader)){
	    		 pObject.setFlag(Constant.NG);
	    		 pObject.setMsg("订单ID错误");
				 return pObject;
			  }
			 //订单状态为草稿，才能删除订单
			 String docStatus = (String)preOrderHeader.get("docStatus");
			 if(docStatus.equals(Constant.DRAFT)){
				 //删除原有头
				 delegator.removeValue(preOrderHeader);
				//删除原有明细
				delegator.removeByAnd("PreOrderDtl", UtilMisc.toMap("docId", UtilMisc.toMap("docId",docId)));
			 }else{
				 pObject.setFlag(Constant.NG);
	    		 pObject.setMsg("订单状态不是草稿");
				 return pObject;
			 }
			 pObject.setFlag(Constant.OK);
		} catch (GenericEntityException e) {
			 pObject.setFlag(Constant.NG);
			 pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
	     return pObject;
	}
	
	//根据ID查询PreOrderHeader
	@SuppressWarnings("unchecked")
	public static  BasePosObject getPreOrderHeaderById(DispatchContext dctx,Map<String, Object> view) {
		BasePosObject pObject = new BasePosObject ();
		List<GenericValue> preOrderHeaders = null;
		BasePosObject result = null;
		try {
			String docId = (String)view.get("docId");
			String where = "DOC_ID='"+docId+"'";
			Map<String, Object> viewMap = FastMap.newInstance();
			viewMap.put("where",where);
			result = getPreOrderHeaderByCondition(dctx,viewMap);
			preOrderHeaders = (List<GenericValue>)result.getData();
			pObject.setFlag(Constant.OK);
			if(UtilValidate.isNotEmpty(preOrderHeaders)){
				pObject.setData(preOrderHeaders.get(0));
			}
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	 
	//根据条件查询PreOrderHeader
	public static  BasePosObject getPreOrderHeaderByCondition(DispatchContext dctx,Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject ();
		List<GenericValue> preOrderHeaders1 = null;
		List<String> orderBy = FastList.newInstance();
		try {
			String where = (String)view.get("where");
			EntityCondition mainCond = EntityCondition.makeConditionWhere(where);
			orderBy.add("lastUpdateDocDate DESC");//更新时间倒序排列
			preOrderHeaders1 = delegator.findList("PreOrderHeaderMessage",mainCond, null,orderBy,null, false);
			pObject.setFlag(Constant.OK);
			if(UtilValidate.isNotEmpty(preOrderHeaders1)){
				pObject.setData(preOrderHeaders1);
			}
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	
	//根据Id查询PreOrderDtl
	@SuppressWarnings("unchecked")
	public static  BasePosObject getPreOrderDtlById(DispatchContext dctx,Map<String, Object> view) {
		BasePosObject pObject = new BasePosObject ();
		List<GenericValue> preOrderDtls1 = null;
		BasePosObject result = null; 
		try {
			String docId = (String)view.get("docId");
			String where = "DOC_ID='"+docId+"'";
			Map<String, Object> viewMap = FastMap.newInstance();
			viewMap.put("where",where);
			result = getPreOrderDtlByCondition(dctx,viewMap);
			preOrderDtls1 = (List<GenericValue>)result.getData();
			if(UtilValidate.isNotEmpty(preOrderDtls1)){
				pObject.setData(preOrderDtls1);
			}
			pObject.setFlag(Constant.OK);
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	
	//根据条件查询PreOrderDtl
	public static BasePosObject getPreOrderDtlByCondition(DispatchContext dctx, Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject ();
		List<GenericValue> preOrderDtls1 = null;
		try{
			String where = (String)view.get("where");
			EntityCondition mainCond = EntityCondition.makeConditionWhere(where);
			//根据条件查找 List<GenericValue>
			preOrderDtls1 = delegator.findList("PreOrderDtlMessage",mainCond, null, UtilMisc.toList("docId"),null, false);
			pObject.setFlag(Constant.OK);
			if(UtilValidate.isNotEmpty(preOrderDtls1)){
				pObject.setData(preOrderDtls1);
			}
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	
	//添加退订数量(退订单创建时引用)
	public static BasePosObject addPreOrderRtnQt(DispatchContext dctx,BackOrder backOrder) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		GenericValue preOrderDtl = null;
		List<GenericValue> preOrderDtls = null;
		//根据SQL查询行list 中的 订货数量 和 退订数量
	    try{
	    	String docId = backOrder.getHeader().getBaseEntry();
	    	String lastUpdateUserId = backOrder.getHeader().getLastUpdateUserId();
	    	Timestamp lastUpdateDocDate = backOrder.getHeader().getLastUpdateDocDate();
			Long backOrderQuantity = null;
			Long backOrderLineNo= null;
			Long preOrderDtlQuantity = null;
			boolean backStatusCleared = true;
			int len = backOrder.getDetail().size();
			for(int i= 0; i<len;i++){
				backOrderQuantity = backOrder.getDetail().get(i).getQuantity();
				backOrderLineNo = backOrder.getDetail().get(i).getLineNoBaseEntry();
				preOrderDtl = delegator.findByPrimaryKey("PreOrderDtl",UtilMisc.toMap("docId", docId, "lineNo", backOrderLineNo));
				preOrderDtlQuantity = (Long)preOrderDtl.get("backQuantity") + backOrderQuantity;		
				if (preOrderDtlQuantity > (Long)preOrderDtl.get("quantity")) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg("退订数量大于预订数量");
					return pObject;
				}
				preOrderDtl.set("backQuantity", preOrderDtlQuantity);
				preOrderDtl.store();
			}
			
			preOrderDtls = delegator.findByAnd("PreOrderDtl", UtilMisc.toMap("docId", docId));
			for(int i=0;i<preOrderDtls.size();i++){
				backOrderQuantity = (Long)preOrderDtl.get("backQuantity");
				preOrderDtlQuantity = (Long)preOrderDtl.get("quantity");
				if (preOrderDtlQuantity < preOrderDtlQuantity) {
					backStatusCleared = false;
					break;
				}
			}
			Parameter  parameter = new Parameter();
            parameter.setDocId(docId);
            parameter.setLastUpdateUserId(lastUpdateUserId);
            parameter.setLastUpdateDocDate(lastUpdateDocDate);
            if(backStatusCleared){
            	parameter.setBackStatus(Constant.CLEAR);
            }else{
            	parameter.setBackStatus(Constant.PARTCLEARED);
            }
	        //则更新预订单头的退货状态
	        updatePreOrderStatus(dctx,parameter);
	        pObject.setFlag(Constant.OK);
	    }catch(Exception e){
	    	pObject.setFlag(Constant.NG);
        	pObject.setMsg(e.getMessage());
	    	e.printStackTrace();
	    }   
		return pObject;
	}
	
	//添加预订单付款(订金预收单创建时引用)
	public static BasePosObject addPreOrderCollectionAmount(DispatchContext dctx,  Map<String, Object> view) {
		BasePosObject pObject = new BasePosObject ();
		Delegator delegator = dctx.getDelegator();
		//根据SQL查询PreCollectionAmount 付款额,RebateAmount订金总额,FundStatus收款状态
	    try{
	    	String docId = (String)view.get("docId");
	    	String lastUpdateUserId = (String)view.get("lastUpdateUserId");
	    	Timestamp lastUpdateDocDate = (Timestamp)view.get("lastUpdateDocDate");
	    	BigDecimal preCollectionOrderAmount = (BigDecimal)view.get("amount");
			BigDecimal preCollectionAmount = null;
			BigDecimal rebateAmount = null;
			boolean fundStatusCleared = true;
			GenericValue preOrderHeader = delegator.findByPrimaryKey("PreOrderHeader",UtilMisc.toMap("docId", docId));
		    preCollectionAmount = preCollectionOrderAmount.add((BigDecimal)preOrderHeader.get("preCollectionAmount"));
		    rebateAmount = (BigDecimal)preOrderHeader.get("rebateAmount");
		    double raAmount = rebateAmount.doubleValue();
		    double pcAmount=preCollectionAmount.doubleValue();
			if (pcAmount > raAmount) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg("预收金额大于销售总金额");
				return pObject;
			} else if (pcAmount < raAmount) {
				fundStatusCleared = false;
			}
			preOrderHeader.set("preCollectionAmount", preCollectionAmount);
			preOrderHeader.store();
			Parameter parameter = new Parameter();
            parameter.setDocId(docId);
            parameter.setLastUpdateUserId(lastUpdateUserId);
            parameter.setLastUpdateDocDate(lastUpdateDocDate);
            if(fundStatusCleared){
            	parameter.setFundStatus(Constant.CLEAR);
            }else{
            	parameter.setFundStatus(Constant.PARTCLEARED);
            }
			//更新预订单表头的付款状态
			updatePreOrderStatus( dctx,parameter);
	        pObject.setFlag(Constant.OK);
	    }catch(Exception e){
	    	pObject.setFlag(Constant.NG);
    		pObject.setMsg(e.getMessage());
	    	e.printStackTrace();
	    }   
		return pObject;
	}
	
	//根据参数得到GenericValue preOrderHeader(put)
	public static GenericValue getGVPreOrderHeader(DispatchContext dctx,PreOrderHeader preOrderHeader){
		GenericValue gpreOrderHeader = null;
		Map<String,Object> preOrderHeaderMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		try{
			preOrderHeaderMap.put("docId",preOrderHeader.getDocId());
	        preOrderHeaderMap.put("docNo",preOrderHeader.getDocNo());
	        preOrderHeaderMap.put("partyId",preOrderHeader.getPartyId());
	        preOrderHeaderMap.put("storeId",preOrderHeader.getStoreId());
	        preOrderHeaderMap.put("baseEntry",preOrderHeader.getBaseEntry());
	        preOrderHeaderMap.put("extSystemNo",preOrderHeader.getExtSystemNo());
	        preOrderHeaderMap.put("extDocNo",preOrderHeader.getExtDocNo());
	        preOrderHeaderMap.put("postingDate",preOrderHeader.getPostingDate());
	        preOrderHeaderMap.put("docStatus",preOrderHeader.getDocStatus());
	        preOrderHeaderMap.put("memo",preOrderHeader.getMemo());
	        preOrderHeaderMap.put("createUserId",preOrderHeader.getCreateUserId());
	        Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			preOrderHeaderMap.put("createDocDate",timestamp);
	        preOrderHeaderMap.put("lastUpdateUserId",preOrderHeader.getLastUpdateUserId());
	        preOrderHeaderMap.put("lastUpdateDocDate",timestamp);
	        preOrderHeaderMap.put("memberId",preOrderHeader.getMemberId());
	        preOrderHeaderMap.put("primeAmount",preOrderHeader.getPrimeAmount());
	        preOrderHeaderMap.put("rebateAmount",preOrderHeader.getRebateAmount());
	        preOrderHeaderMap.put("preCollectionAmount",preOrderHeader.getPreCollectionAmount());
	        preOrderHeaderMap.put("fundStatus",preOrderHeader.getFundStatus());
	        preOrderHeaderMap.put("backStatus",preOrderHeader.getBackStatus());
	        preOrderHeaderMap.put("salesStatus",preOrderHeader.getSalesStatus());
	        preOrderHeaderMap.put("memberPointAmount",preOrderHeader.getMemberPointAmount());
	        preOrderHeaderMap.put("salesId",preOrderHeader.getSalesId());
	        preOrderHeaderMap.put("collectionStoreId",preOrderHeader.getCollectionStoreId());
	        preOrderHeaderMap.put("approvalUserId",preOrderHeader.getApprovalUserId());
	        preOrderHeaderMap.put("approvalDate",preOrderHeader.getApprovalDate());
	        //订单状态非草稿状态置"1"
	        if(!preOrderHeader.getDocStatus().equals(Constant.DRAFT)){
	        	 preOrderHeaderMap.put("isSync",Constant.ONE);
	        }else{
	        	 preOrderHeaderMap.put("isSync",preOrderHeader.getIsSync());
	        }
	        preOrderHeaderMap.put("printNum",preOrderHeader.getPrintNum());
	        preOrderHeaderMap.put("attrName1",preOrderHeader.getAttrName1());
	        preOrderHeaderMap.put("attrName2",preOrderHeader.getAttrName2());
	        preOrderHeaderMap.put("attrName3",preOrderHeader.getAttrName3());
	        preOrderHeaderMap.put("attrName4",preOrderHeader.getAttrName4());
	        preOrderHeaderMap.put("attrName5",preOrderHeader.getAttrName5());
	        preOrderHeaderMap.put("attrName6",preOrderHeader.getAttrName6());
	        preOrderHeaderMap.put("attrName7",preOrderHeader.getAttrName7());
	        preOrderHeaderMap.put("attrName8",preOrderHeader.getAttrName8());
	        preOrderHeaderMap.put("attrName9",preOrderHeader.getAttrName9());
	        gpreOrderHeader = delegator.makeValue("PreOrderHeader", preOrderHeaderMap);
	        gpreOrderHeader.create();
		}catch(Exception e){
			e.printStackTrace();
		}
		return gpreOrderHeader;
	}
	//根据参数得到 GenericValue preOrderDtl(put)
	public static GenericValue getGVPreOrderDtl(DispatchContext dctx,PreOrderDtl preOrderDtl){
		Delegator delegator = dctx.getDelegator();
		Map<String,Object> preOrderDtlMap = FastMap.newInstance();
		GenericValue gpreOrderDtl = null;
		 try{
			preOrderDtlMap.put("docId",preOrderDtl.getDocId());
			preOrderDtlMap.put("lineNo",preOrderDtl.getLineNo());
			preOrderDtlMap.put("lineNoBaseEntry",preOrderDtl.getLineNoBaseEntry());
			preOrderDtlMap.put("productId", preOrderDtl.getProductId());
			preOrderDtlMap.put("productName", preOrderDtl.getProductName());
			preOrderDtlMap.put("barCodes", preOrderDtl.getBarCodes());
			preOrderDtlMap.put("extNo", preOrderDtl.getExtNo());
			preOrderDtlMap.put("serialNo", preOrderDtl.getSerialNo());
			preOrderDtlMap.put("unitPrice", preOrderDtl.getUnitPrice());
			preOrderDtlMap.put("rebatePrice", preOrderDtl.getRebatePrice());
			preOrderDtlMap.put("quantity", preOrderDtl.getQuantity());
			preOrderDtlMap.put("saleQuantity", preOrderDtl.getSaleQuantity());
			preOrderDtlMap.put("backQuantity", preOrderDtl.getBackQuantity());
			preOrderDtlMap.put("rebateAmount",preOrderDtl.getRebateAmount());
			preOrderDtlMap.put("bomAmount",preOrderDtl.getBomAmount());
			preOrderDtlMap.put("memo", preOrderDtl.getMemo());
			preOrderDtlMap.put("isMainProduct", preOrderDtl.getIsMainProduct());
			preOrderDtlMap.put("productSalesPolicyId", preOrderDtl.getProductSalesPolicyId());
			preOrderDtlMap.put("productSalesPolicyNo", preOrderDtl.getProductSalesPolicyNo());
			preOrderDtlMap.put("bomId", preOrderDtl.getBomId());
			preOrderDtlMap.put("salesId", preOrderDtl.getSalesId());
			preOrderDtlMap.put("memberPoint", preOrderDtl.getMemberPoint());
			preOrderDtlMap.put("isGift", preOrderDtl.getIsGift());
			preOrderDtlMap.put("isSequence", preOrderDtl.getIsSequence());
			preOrderDtlMap.put("approvalUserId", preOrderDtl.getApprovalUserId());
			preOrderDtlMap.put("bomAmount", preOrderDtl.getBomAmount());
	        preOrderDtlMap.put("attrName1",preOrderDtl.getAttrName1());
	        preOrderDtlMap.put("attrName2",preOrderDtl.getAttrName2());
	        preOrderDtlMap.put("attrName3",preOrderDtl.getAttrName3());
	        preOrderDtlMap.put("attrName4",preOrderDtl.getAttrName4());
	        preOrderDtlMap.put("attrName5",preOrderDtl.getAttrName5());
	        preOrderDtlMap.put("attrName6",preOrderDtl.getAttrName6());
	        preOrderDtlMap.put("attrName7",preOrderDtl.getAttrName7());
	        preOrderDtlMap.put("attrName8",preOrderDtl.getAttrName8());
	        preOrderDtlMap.put("attrName9",preOrderDtl.getAttrName9());   
	        gpreOrderDtl = delegator.makeValue("PreOrderDtl", preOrderDtlMap);
	        gpreOrderDtl.create();
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return gpreOrderDtl;
	}
	
	//根据参数值修改GenericValue preOrderHeader(set)
	public static GenericValue updateGVPreOrderHeader(GenericValue gpreOrderHeader,PreOrderHeader preOrderHeader){
		try{
			gpreOrderHeader.set("docId",preOrderHeader.getDocId());
	        gpreOrderHeader.set("docNo",preOrderHeader.getDocNo());
	        gpreOrderHeader.set("partyId",preOrderHeader.getPartyId());
	        gpreOrderHeader.set("storeId",preOrderHeader.getStoreId());
	        gpreOrderHeader.set("baseEntry",preOrderHeader.getBaseEntry());
	        gpreOrderHeader.set("extSystemNo",preOrderHeader.getExtSystemNo());
	        gpreOrderHeader.set("extDocNo",preOrderHeader.getExtDocNo());
	        gpreOrderHeader.set("postingDate",preOrderHeader.getPostingDate());
	        gpreOrderHeader.set("docStatus",preOrderHeader.getDocStatus());
	        gpreOrderHeader.set("memo",preOrderHeader.getMemo());
	        gpreOrderHeader.set("createUserId",preOrderHeader.getCreateUserId());
	        gpreOrderHeader.set("createDocDate",preOrderHeader.getCreateDocDate());
	        gpreOrderHeader.set("lastUpdateUserId",preOrderHeader.getLastUpdateUserId());
	        Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			gpreOrderHeader.set("lastUpdateDocDate",timestamp);
	        gpreOrderHeader.set("memberId",preOrderHeader.getMemberId());
	        gpreOrderHeader.set("primeAmount",preOrderHeader.getPrimeAmount());
	        gpreOrderHeader.set("rebateAmount",preOrderHeader.getRebateAmount());
	        gpreOrderHeader.set("preCollectionAmount",preOrderHeader.getPreCollectionAmount());
	        gpreOrderHeader.set("fundStatus",preOrderHeader.getFundStatus());
	        gpreOrderHeader.set("backStatus",preOrderHeader.getBackStatus());
	        gpreOrderHeader.set("salesStatus",preOrderHeader.getSalesStatus());
	        gpreOrderHeader.set("memberPointAmount",preOrderHeader.getMemberPointAmount());
	        gpreOrderHeader.set("salesId",preOrderHeader.getSalesId());
	        gpreOrderHeader.set("collectionStoreId",preOrderHeader.getCollectionStoreId());
	        gpreOrderHeader.set("approvalUserId",preOrderHeader.getApprovalUserId());
	        gpreOrderHeader.set("approvalDate",preOrderHeader.getApprovalDate());
	        gpreOrderHeader.set("isSync",preOrderHeader.getIsSync());
	        gpreOrderHeader.set("printNum",preOrderHeader.getPrintNum());
	        gpreOrderHeader.set("attrName1",preOrderHeader.getAttrName1());
	        gpreOrderHeader.set("attrName2",preOrderHeader.getAttrName2());
	        gpreOrderHeader.set("attrName3",preOrderHeader.getAttrName3());
	        gpreOrderHeader.set("attrName4",preOrderHeader.getAttrName4());
	        gpreOrderHeader.set("attrName5",preOrderHeader.getAttrName5());
	        gpreOrderHeader.set("attrName6",preOrderHeader.getAttrName6());
	        gpreOrderHeader.set("attrName7",preOrderHeader.getAttrName7());
	        gpreOrderHeader.set("attrName8",preOrderHeader.getAttrName8());
	        gpreOrderHeader.set("attrName9",preOrderHeader.getAttrName9());
	        gpreOrderHeader.store();
		}catch(Exception e){
			e.printStackTrace();
		}
		return gpreOrderHeader;
	}
	
}
