package org.ofbiz.order.refundorder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.order.parameter.Parameter;
import org.ofbiz.order.returnorder.ReturnOrderWorker;
import org.ofbiz.service.DispatchContext;
public class RefundOrderWork {

	public static final String module = RefundOrderWork.class.getName();

	private RefundOrderWork(){ }
	
	/*
	 * createRefundOrder
	 */
	public static BasePosObject createRefundOrder (DispatchContext dctx,RefundOrder refundorder){
	 BasePosObject pObject = new BasePosObject();
	  int lenth = refundorder.getDetail().size();
	try{
		
	  //写入退款头数据
	  getGVRefundOrderHeader(dctx,refundorder.getHeader());
	  
	
	  //写入退款item
	  for(int i =0;i<lenth;i++){
		  getGVRrefundOrderDtl(dctx, refundorder.getDetail().get(i)); 
	  }
	  //更新退货单表头的退款金额
	  Map<String,Object>view = FastMap.newInstance();
	  view.put("docId", refundorder.getHeader().getBaseEntry());
	  view.put("amount", refundorder.getHeader().getAmount());//退款金额
	  view.put("lastUpdateUserId",refundorder.getHeader().getLastUpdateUserId());
	 view.put("lastUpdateDocDate",refundorder.getHeader().getLastUpdateDocDate());
	  pObject =ReturnOrderWorker.addReturnOrderRefundAmount(dctx, view);
	  if(pObject.getFlag().equals(Constant.NG)){
		  pObject.setFlag(Constant.NG);
		  pObject.setMsg("更新退货退款金额失败");
		  return pObject;
		  
	  }
	  pObject.setFlag(Constant.OK);
	}catch (Exception e) {
		e.printStackTrace();
	}
	  return pObject;
	}
	
	/*
	 *  更新订单状态
	 */
	@SuppressWarnings("unused")
	public static BasePosObject updateRefundOrderStatus(
			DispatchContext dctx, Parameter parameter) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		GenericValue refundOrderHeader = null;// 数据库中的数值对象
		// 根据主键docId 查询RefundOrderHeader的非空的且不是部分已清的Status状态
		try {
			 if (UtilValidate.isNotEmpty(parameter)) {
					String docId = parameter.getDocId();
					String docStatus = parameter.getDocStatus(); // 销售订单状态
					String lastUpdateUserId =parameter.getLastUpdateUserId();//更新用户
					Timestamp lastUpdateDocDate =parameter.getLastUpdateDocDate();
			refundOrderHeader = delegator.findByPrimaryKey(
					"RefundOrderHeader", UtilMisc.toMap("docId", docId));
			String SdocStatus =(String)refundOrderHeader.get("docStatus");
		if (UtilValidate.isNotEmpty(docStatus) && !docStatus.equals(SdocStatus)) {
				// 更新docStatus
			refundOrderHeader.set("docStatus", docStatus);
			if(!docStatus.equals(Constant.DRAFT)){
				refundOrderHeader.set("isSync",Constant.ONE);
			}
			refundOrderHeader.set("lastUpdateDocDate",UtilDateTime.nowTimestamp());
			refundOrderHeader.set("lastUpdateUserId",lastUpdateUserId);
			refundOrderHeader.store();
		}
			pObject.setFlag(Constant.OK);
			 }
			 } catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	
	/**
	 * update RefundOrder
	 */
	public static BasePosObject updateRefundOrder(DispatchContext dctx,
			RefundOrder refundOrder) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		GenericValue refundOrderHeader = null;
		List<GenericValue> refundOrderDtls = null;
		try {
			String docId = refundOrder.getHeader().getDocId();
			if (UtilValidate.isEmpty(docId)) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg("ID错误");
				return pObject;
			}
			// 根据ID 查询 销售单头
			refundOrderHeader = delegator.findByPrimaryKey("RefundOrderHeader",
					UtilMisc.toMap("docId", docId));
			// 草稿修改
			String docStatus = (String) refundOrderHeader.get("docStatus");
				if (UtilValidate.isEmpty(docStatus) ||!docStatus.equals(Constant.DRAFT)) {
						pObject.setFlag(Constant.NG);
						pObject.setMsg("订单状态不是草稿");
						return pObject;
						}
			// 更新销售单头
			updateRefundOrderHeader(refundOrderHeader, refundOrder.getHeader());
			// 根据id查询订单明细
			refundOrderDtls = delegator.findByAnd("RefundOrderDtl",
					UtilMisc.toMap("docId", docId));
			// 删除原有明细
			delegator.removeAll(refundOrderDtls);
			// 写入明细
			int length= refundOrder.getDetail().size();
			for (int i = 0; i < length; i++) {
				getGVRrefundOrderDtl(dctx, refundOrder.getDetail().get(i));
			}
			pObject.setFlag(Constant.OK);
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}

		return pObject;

	}
	
	/**
	 * 根据条件查询RefundOrderHeader
	 */

	public static BasePosObject getRefundOrderHeaderByCondition(DispatchContext dctx, Map<String, Object> view) {
		BasePosObject pObject = new BasePosObject ();
		Delegator delegator = dctx.getDelegator();
		List<GenericValue> LGrefundOrderHeaders = null;
		EntityCondition mainCond = null;
		try {
			String where = (String)view.get("where");
			mainCond = EntityCondition.makeConditionWhere(where);
			LGrefundOrderHeaders = delegator.findList("RefundOrderHeader",mainCond, null,null,null, false);
			if(UtilValidate.isEmpty(LGrefundOrderHeaders)){
				pObject.setFlag(Constant.OK);
		    	return pObject;
			}
			
			pObject.setFlag(Constant.OK);
			pObject.setData(LGrefundOrderHeaders);
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	
	/**
	 * 根据条件查询RefundOrderDtl
	 */
		public static BasePosObject getRefundOrderDtlByCondition(DispatchContext dctx, Map<String, Object> view) {
			BasePosObject pObject = new BasePosObject ();
			Delegator delegator = dctx.getDelegator();
			List<GenericValue> LGrefundOrderDtls = null;
			EntityCondition mainCond = null;
			try{
				String where = (String)view.get("where");
				mainCond = EntityCondition.makeConditionWhere(where);
				//根据条件查找 List<GenericValue>
				LGrefundOrderDtls = delegator.findList("RefundOrderDtl",mainCond, null, UtilMisc.toList("docId"),null, false);
				if(UtilValidate.isEmpty(LGrefundOrderDtls)){
					pObject.setFlag(Constant.OK);
			    	return pObject;
				}
		
				pObject.setFlag(Constant.OK);
				pObject.setData(LGrefundOrderDtls);
			}catch(Exception e){
				pObject.setFlag(Constant.NG);
				pObject.setMsg(e.getMessage());
				e.printStackTrace();
			}
			return pObject;
		}
	
	/*
	 * getrefundOrderBy Id
	 */
		
		public static BasePosObject getRefundOrderById(DispatchContext dctx,Map<String, Object>view) {
			BasePosObject pObject = new BasePosObject ();
			Delegator delegator = dctx.getDelegator();
			GenericValue refundOrderHeader = null;
		    List<GenericValue> refundOrderDtls  = null;
		    ObjectOrder ojbectOrder = new ObjectOrder();
		     try {
		    	 String docId = (String)view.get("docId");
		    	//根据ID查询RefundOrderHeader数值对象(map形式)
				 refundOrderHeader = delegator.findByPrimaryKey("RefundOrderHeader", UtilMisc.toMap("docId", docId));
				 if(UtilValidate.isEmpty(refundOrderHeader)){
					 pObject.setFlag(Constant.OK);
				     return pObject;
				    }
				//获取java对象 RefundOrderHeader
			//RefundOrderHeader RrefundOrderHeader = getRefundOrderHeader(refundOrderHeader);		
				
				//根据ID查询List<GenericValue> RefundOrderDtls
				refundOrderDtls= delegator.findByAnd("RefundOrderDtl", UtilMisc.toMap("docId",docId));
				 if(UtilValidate.isEmpty(refundOrderDtls)){
					 pObject.setFlag(Constant.OK);
				     return pObject;
				    }
				//设置RefundOrder的头和行
				 ojbectOrder.setHeader(refundOrderHeader);
				 ojbectOrder.setDetail(refundOrderDtls);
				 pObject.setFlag(Constant.OK);
				 pObject.setData(ojbectOrder);
			} catch (GenericEntityException e) {
				 pObject.setFlag(Constant.NG);
				 pObject.setMsg(e.getMessage());
				e.printStackTrace();
			}
			return pObject;
		}	
	
		/**
		 * 根据条件查询RefundOrder
		 */
		public static BasePosObject getRefundOrderByCondition(DispatchContext dctx,
				Map<String, Object> view) {
			Delegator delegator = dctx.getDelegator();
			BasePosObject pObject = new BasePosObject();
			List<GenericValue> LGrefundOrderHeader = null;
			EntityCondition mainCond = null;
			List<GenericValue> LGrefundOrderDtls = null;
			try {
				String where = (String) view.get("where");
				mainCond = EntityCondition.makeConditionWhere(where);
				LGrefundOrderHeader = delegator.findList("RefundOrderHeader",
						mainCond, null, null, null, false);
				if (UtilValidate.isEmpty(LGrefundOrderHeader)) {
					pObject.setFlag(Constant.OK);
					return pObject;
				}
				mainCond = EntityCondition.makeConditionWhere(where);
				LGrefundOrderDtls = delegator.findList("RefundOrderDtl", mainCond, null,
						UtilMisc.toList("docId"), null, false);
				if (UtilValidate.isEmpty(LGrefundOrderDtls)) {
					pObject.setFlag(Constant.OK);
					return pObject;
				}
			    ObjectOrder ojbectOrder = new ObjectOrder();
			    ojbectOrder.setHeader(LGrefundOrderHeader);
			    ojbectOrder.setDetail(LGrefundOrderDtls);
				pObject.setFlag(Constant.OK);
				pObject.setData(ojbectOrder);
			} catch (Exception e) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg(e.getMessage());
				e.printStackTrace();
			}
			return pObject;
		}
		// 根据参数的到GenericValue Refundorderheader,放入map
		public static GenericValue getGVRefundOrderHeader(DispatchContext dctx,
				RefundOrderHeader refundorderheader) {
			GenericValue RrefundOrderHeader = null;
			Map<String, Object> RefundOrderHeaderMap = FastMap.newInstance();
			Delegator delegator = dctx.getDelegator();
			try {
				RefundOrderHeaderMap.put("docId",
						refundorderheader.getDocId());
				RefundOrderHeaderMap.put("docNo",
						refundorderheader.getDocNo());
				RefundOrderHeaderMap.put("partyId",
						refundorderheader.getPartyId());
				RefundOrderHeaderMap.put("storeId",
						refundorderheader.getStoreId());
				RefundOrderHeaderMap.put("baseEntry",
						refundorderheader.getBaseEntry());
				RefundOrderHeaderMap.put("extSystemNo",
						refundorderheader.getExtSystemNo());
				RefundOrderHeaderMap.put("extDocNo",
						refundorderheader.getExtDocNo());
				RefundOrderHeaderMap.put("postingDate",
						refundorderheader.getPostingDate());
				RefundOrderHeaderMap.put("docStatus",
						refundorderheader.getDocStatus());
				RefundOrderHeaderMap.put("memo",
						refundorderheader.getMemo());
				RefundOrderHeaderMap.put("createUserId",
						refundorderheader.getCreateUserId());
				RefundOrderHeaderMap.put("createDocDate", UtilDateTime.nowTimestamp());
				RefundOrderHeaderMap.put("lastUpdateUserId",
						refundorderheader.getLastUpdateUserId());
				RefundOrderHeaderMap.put("lastUpdateDocDate", UtilDateTime.nowTimestamp());
				RefundOrderHeaderMap.put("amount",
						refundorderheader.getAmount());
				RefundOrderHeaderMap.put("approvalUserId",
						refundorderheader.getApprovalUserId());
				RefundOrderHeaderMap.put("approvalDate",
						refundorderheader.getApprovalDate());
				if(!refundorderheader.getDocStatus().equals(Constant.DRAFT)){
					RefundOrderHeaderMap.put("isSync",
							Constant.ONE);}else{	
				RefundOrderHeaderMap.put("isSync",
						refundorderheader.getIsSync());}
				RefundOrderHeaderMap.put("printNum",
						refundorderheader.getPrintNum());
				RefundOrderHeaderMap.put("attrName1",
						refundorderheader.getAttrName1());
				RefundOrderHeaderMap.put("attrName2",
						refundorderheader.getAttrName2());
				RefundOrderHeaderMap.put("attrName3",
						refundorderheader.getAttrName3());
				RefundOrderHeaderMap.put("attrName4",
						refundorderheader.getAttrName4());
				RefundOrderHeaderMap.put("attrName5",
						refundorderheader.getAttrName5());
				RefundOrderHeaderMap.put("attrName6",
						refundorderheader.getAttrName6());
				RefundOrderHeaderMap.put("attrName7",
						refundorderheader.getAttrName7());
				RefundOrderHeaderMap.put("attrName8",
						refundorderheader.getAttrName8());
				RefundOrderHeaderMap.put("attrName9",
						refundorderheader.getAttrName9());
				RrefundOrderHeader = delegator.makeValue(
						"RefundOrderHeader", RefundOrderHeaderMap);
				RrefundOrderHeader.create();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return RrefundOrderHeader;
		}
		
		// 根据参数得到 GenericValue CollectionOrderDtl 放入map
				public static GenericValue getGVRrefundOrderDtl(DispatchContext dctx,
						RefundOrderDtl refundOrderDtl) {
					Delegator delegator = dctx.getDelegator();
					Map<String, Object> RefundOrderDtlMap = FastMap.newInstance();
					GenericValue RrefundOrderDtl = null;
					try {
						RefundOrderDtlMap.put("docId", refundOrderDtl.getDocId());
						RefundOrderDtlMap.put("lineNo", refundOrderDtl.getLineNo());
						RefundOrderDtlMap.put("lineNoBaseEntry",refundOrderDtl.getLineNoBaseEntry());
						RefundOrderDtlMap.put("style", refundOrderDtl.getStyle());
						RefundOrderDtlMap.put("type", refundOrderDtl.getType());
						RefundOrderDtlMap.put("extNo", refundOrderDtl.getExtNo());
						RefundOrderDtlMap.put("amount", refundOrderDtl.getAmount());
						RefundOrderDtlMap.put("cardCode",
								refundOrderDtl.getCardCode());
						RefundOrderDtlMap.put("code",
								refundOrderDtl.getCode());
						RefundOrderDtlMap.put("cardName",
								refundOrderDtl.getCardName());
						RefundOrderDtlMap.put("serialNo",
								refundOrderDtl.getSerialNo());
						RefundOrderDtlMap.put("memo", refundOrderDtl.getMemo());
						RefundOrderDtlMap.put("approvalUserId",
								refundOrderDtl.getApprovalUserId());
						RefundOrderDtlMap.put("attrName1",
								refundOrderDtl.getAttrName1());
						RefundOrderDtlMap.put("attrName2",
								refundOrderDtl.getAttrName2());
						RefundOrderDtlMap.put("attrName3",
								refundOrderDtl.getAttrName3());
						RefundOrderDtlMap.put("attrName4",
								refundOrderDtl.getAttrName4());
						RefundOrderDtlMap.put("attrName5",
								refundOrderDtl.getAttrName5());
						RefundOrderDtlMap.put("attrName6",
								refundOrderDtl.getAttrName6());
						RefundOrderDtlMap.put("attrName7",
								refundOrderDtl.getAttrName7());
						RefundOrderDtlMap.put("attrName8",
								refundOrderDtl.getAttrName8());
						RefundOrderDtlMap.put("attrName9",
								refundOrderDtl.getAttrName9());
						RrefundOrderDtl = delegator.makeValue("RefundOrderDtl",
								RefundOrderDtlMap);
						RrefundOrderDtl.create();

					} catch (Exception e) {
						e.printStackTrace();

					}
					return RrefundOrderDtl;
				}
		
		
		// 根据参数获得 RefundOrderHeader
		public static RefundOrderHeader getRefundOrderHeader(GenericValue refundOrderHeader) {
			RefundOrderHeader RrefundOrderHeader = null;
		
			try {
				RrefundOrderHeader = new RefundOrderHeader();
				RrefundOrderHeader.setDocId((String)refundOrderHeader.get("docId"));
				RrefundOrderHeader.setDocNo((Long)refundOrderHeader.get("docNo"));
				RrefundOrderHeader.setPartyId((String)refundOrderHeader.get("partyId"));
				RrefundOrderHeader.setStoreId((String)refundOrderHeader.get("storeId"));
				RrefundOrderHeader.setBaseEntry((String)refundOrderHeader.get("baseEntry"));
				RrefundOrderHeader.setExtSystemNo((String)refundOrderHeader.get("extSystemNo"));
				RrefundOrderHeader.setExtDocNo((String)refundOrderHeader.get("extDocNo"));
				RrefundOrderHeader.setPostingDate((Timestamp)refundOrderHeader.get("postingDate"));
				RrefundOrderHeader.setDocStatus((String)refundOrderHeader.get("docStatus"));
				RrefundOrderHeader.setMemo((String)refundOrderHeader.get("memo"));
				RrefundOrderHeader.setCreateUserId((String)refundOrderHeader.get("createUserId"));
				RrefundOrderHeader.setCreateDocDate((Timestamp)refundOrderHeader.get("createDocDate"));
				RrefundOrderHeader.setLastUpdateUserId((String)refundOrderHeader.get("lastUpdateUserId"));
				RrefundOrderHeader.setLastUpdateDocDate((Timestamp)refundOrderHeader.get("lastUpdateDocDate"));
				RrefundOrderHeader.setAmount((BigDecimal)refundOrderHeader.get("amount"));
				RrefundOrderHeader.setApprovalUserId((String)refundOrderHeader.get("approvalUserId"));
				RrefundOrderHeader.setApprovalDate((Timestamp)refundOrderHeader.get("approvalDate"));
				RrefundOrderHeader.setIsSync((String)refundOrderHeader.get("isSync"));
				RrefundOrderHeader.setPrintNum((Long)refundOrderHeader.get("printNum"));
				RrefundOrderHeader.setAttrName1((String)refundOrderHeader.get("attrName1"));
				RrefundOrderHeader.setAttrName2((String)refundOrderHeader.get("attrName2"));
				RrefundOrderHeader.setAttrName3((String)refundOrderHeader.get("attrName3"));
				RrefundOrderHeader.setAttrName4((String)refundOrderHeader.get("attrName4"));
				RrefundOrderHeader.setAttrName5((String)refundOrderHeader.get("attrName5"));
				RrefundOrderHeader.setAttrName6((String)refundOrderHeader.get("attrName6"));
				RrefundOrderHeader.setAttrName7((String)refundOrderHeader.get("attrName7"));
				RrefundOrderHeader.setAttrName8((String)refundOrderHeader.get("attrName8"));
				RrefundOrderHeader.setAttrName9((String)refundOrderHeader.get("attrName9"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return RrefundOrderHeader;
		}
		
		// 根据参数得到 RefundOrderDtl
			public static RefundOrderDtl getRefundOrderDtl(GenericValue refundOrderDtl) {
				RefundOrderDtl RrefundOrderDtl = null;
				try {
					RrefundOrderDtl = new RefundOrderDtl();
					RrefundOrderDtl.setDocId((String)refundOrderDtl.get("docId"));
					RrefundOrderDtl.setLineNo((Long)refundOrderDtl.get("lineNo"));
					RrefundOrderDtl.setLineNoBaseEntry((Long)refundOrderDtl.get("lineNoBaseEntry"));
					RrefundOrderDtl.setStyle((String)refundOrderDtl.get("style"));
					RrefundOrderDtl.setType((String)refundOrderDtl.get("type"));
					RrefundOrderDtl.setExtNo((String)refundOrderDtl.get("extNo"));
					RrefundOrderDtl.setAmount((BigDecimal)refundOrderDtl.get("amount"));
					RrefundOrderDtl.setCardCode((String)refundOrderDtl.get("cardCode"));
					RrefundOrderDtl.setCode((String)refundOrderDtl.get("code"));
					RrefundOrderDtl.setSerialNo((String)refundOrderDtl.get("serialNo"));
					RrefundOrderDtl.setCardName((String)refundOrderDtl.get("cardName"));
					RrefundOrderDtl.setMemo((String)refundOrderDtl.get("memo"));
					RrefundOrderDtl.setApprovalUserId((String)refundOrderDtl.get("approvalUserId"));
					RrefundOrderDtl.setAttrName1((String)refundOrderDtl.get("attrName1"));
					RrefundOrderDtl.setAttrName2((String)refundOrderDtl.get("attrName2"));
					RrefundOrderDtl.setAttrName3((String)refundOrderDtl.get("attrName3"));
					RrefundOrderDtl.setAttrName4((String)refundOrderDtl.get("attrName4"));
					RrefundOrderDtl.setAttrName5((String)refundOrderDtl.get("attrName5"));
					RrefundOrderDtl.setAttrName6((String)refundOrderDtl.get("attrName6"));
					RrefundOrderDtl.setAttrName7((String)refundOrderDtl.get("attrName7"));
					RrefundOrderDtl.setAttrName8((String)refundOrderDtl.get("attrName8"));
					RrefundOrderDtl.setAttrName9((String)refundOrderDtl.get("attrName9"));
				} catch (Exception e) {
					e.printStackTrace();

				}
				return RrefundOrderDtl;
			}

	  //更新销售订单头
		public static GenericValue updateRefundOrderHeader(GenericValue Rrefundorder,RefundOrderHeader refundOrderHeader){
		
			try{
				Date date = new Date();
				Timestamp timestamp = new Timestamp(date.getTime());
				Rrefundorder.set("docId",
						refundOrderHeader.getDocId());
				Rrefundorder.set("docNo",
						refundOrderHeader.getDocNo());
				Rrefundorder.set("partyId",
						refundOrderHeader.getPartyId());
				Rrefundorder.set("storeId",
						refundOrderHeader.getStoreId());
				Rrefundorder.set("baseEntry",
						refundOrderHeader.getBaseEntry());
				Rrefundorder.set("extSystemNo",
						refundOrderHeader.getExtSystemNo());
				Rrefundorder.set("extDocNo",
						refundOrderHeader.getExtDocNo());
				Rrefundorder.set("postingDate",
						refundOrderHeader.getPostingDate());
				Rrefundorder.set("docStatus",
						refundOrderHeader.getDocStatus());
				Rrefundorder.set("memo",
						refundOrderHeader.getMemo());
				Rrefundorder.set("createUserId",
						refundOrderHeader.getCreateUserId());
				Rrefundorder.set("createDocDate",timestamp);
				Rrefundorder.set("lastUpdateUserId",
						refundOrderHeader.getLastUpdateUserId());
				Rrefundorder.set("lastUpdateDocDate",timestamp);
				Rrefundorder.set("amount",
						refundOrderHeader.getAmount());
				Rrefundorder.set("approvalUserId",
						refundOrderHeader.getApprovalUserId());
				Rrefundorder.set("approvalDate",
						refundOrderHeader.getApprovalDate());
				Rrefundorder.set("isSync",
						refundOrderHeader.getIsSync());
				Rrefundorder.set("printNum",
						refundOrderHeader.getPrintNum());
				Rrefundorder.set("attrName1",
						refundOrderHeader.getAttrName1());
				Rrefundorder.set("attrName2",
						refundOrderHeader.getAttrName2());
				Rrefundorder.set("attrName3",
						refundOrderHeader.getAttrName3());
				Rrefundorder.set("attrName4",
						refundOrderHeader.getAttrName4());
				Rrefundorder.set("attrName5",
						refundOrderHeader.getAttrName5());
				Rrefundorder.set("attrName6",
						refundOrderHeader.getAttrName6());
				Rrefundorder.set("attrName7",
						refundOrderHeader.getAttrName7());
				Rrefundorder.set("attrName8",
						refundOrderHeader.getAttrName8());
				Rrefundorder.set("attrName9",
						refundOrderHeader.getAttrName9());
				Rrefundorder.store();
			}catch (Exception e) {
				e.printStackTrace();
			}
			return Rrefundorder;
		}
		
}
