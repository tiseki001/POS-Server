package org.ofbiz.order.collectionorder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
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
import org.ofbiz.order.saleorder.SaleOrderWork;
import org.ofbiz.service.DispatchContext;

public class CollectionOrderWork {
	/**
	 * Worker methods for collectionOrder Information DispatchContext用于取得对象
	 * 一个名叫context的影射Map包含了你的输入参数并返回一个结果映射。
	 * 
	 */

	public static final String module = CollectionOrderWork.class.getName();
	private CollectionOrderWork() {}
 /*
  * create  CollectionOrder
  */
 public static BasePosObject createCollectionOrder(DispatchContext dctx ,CollectionOrder CO){
		 BasePosObject pObject = new BasePosObject ();

		int len= CO.getDetail().size();
	 try{
		 //写入header
		getGVCollectionOrderHeader(dctx,CO.getHeader());
		
		//循环明细
		 for(int i=0;i<len;i++){
	  getGVCollectionOrderDtl(dctx,CO.getDetail().get(i));
		 }
		
		 pObject.setFlag(Constant.OK);
	 }catch (Exception e) {
		e.printStackTrace();
			 pObject.setMsg(e.getMessage());
	 }
		//判断是不是草稿状态
     String docStatus =(String)CO.getHeader().getDocStatus();
     if(docStatus.equals(Constant.DRAFT)){
  	   pObject.setFlag(Constant.NG);
  	   pObject.setMsg("草稿状态不需要回写");
  	    return pObject;
     }
	 String baseEntry = CO.getHeader().getBaseEntry();
	 BigDecimal collectionAmount=CO.getHeader().getCollectionAmount();
	 String lastUpdateUserId = CO.getHeader().getLastUpdateUserId();
	 Timestamp lastUpdateDocDate = (Timestamp)CO.getHeader().getLastUpdateDocDate();
	 //更新销售订单头累加付款额
	 Map<String,Object> views= new HashMap<String,Object>();
	 views.put("where", "DOC_ID='"+baseEntry+"'");
	 views.put("docId", baseEntry);
	 views.put("collectionAmount",collectionAmount);
	 views.put("lastUpdateUserId",lastUpdateUserId);
	 views.put("lastUpdateDocDate",lastUpdateDocDate);
	 pObject= SaleOrderWork.addSalesOrderCollectionAmount(dctx,views);
	if(pObject.getFlag().equals(Constant.NG)){
		pObject.setFlag(Constant.NG);
		pObject.setMsg("更新SalesOrderCollectionAmount未成功");
	}
	return pObject;
 }		
 	//updateCollectionOrerStatus
	public static BasePosObject updateCollectionOrderStatus(DispatchContext dctx,Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject ();
		GenericValue collectionOrderHeader = null;// 数据库中的数值对象 销售表头
		// 根据主键docId 查询collectionOrderHeader的非空的且不是部分已清的Status状态
		try{
			String docId = view.get("docId").toString();
			String docStatus=view.get("docStatus").toString();
			String lastupdateuserid = (String)view.get("lastUpdateUserId");
			@SuppressWarnings("unused")
			Timestamp lastupdatedocdate =(Timestamp)view.get("lastUpdateDocDate");
		collectionOrderHeader = delegator.findByPrimaryKey("CollectionOrderHeader", UtilMisc.toMap("docId", docId));
			if(UtilValidate.isEmpty(collectionOrderHeader)){
				pObject.setFlag(Constant.NG);
				pObject.setMsg("不可以为空");
			}
		String CdocStatus = (String) collectionOrderHeader.get("docStatus"); 
		if(CdocStatus.equals(Constant.CLEARED)|| CdocStatus.equals(Constant.INVALID)){
		     pObject.setFlag(Constant.NG);
		     pObject.setMsg("订单状态为已清或作废时不能再改变其状态");
		     return pObject;
		}
		if (CdocStatus != null && CdocStatus != ""&& !CdocStatus.equals(Constant.PARTCLEARED))
				if(CdocStatus.equals(docStatus)){
					pObject.setFlag(Constant.NG);
					pObject.setMsg("所有状态都一致");
					return pObject;
			}
		//更新docStatus
		if(docStatus!=null && !docStatus.equals(CdocStatus)){
			collectionOrderHeader.set("docStatus",docStatus);
		}
		if(!docStatus.equals(Constant.DRAFT)){
			collectionOrderHeader.set("isSync",Constant.ONE);	
		}
		collectionOrderHeader.set("lastUpdateUserId",lastupdateuserid);
		collectionOrderHeader.set("lastUpdateDocDate",UtilDateTime.nowTimestamp());
		collectionOrderHeader.store();
		pObject.setFlag(Constant.OK);
	} catch (Exception e) {
		pObject.setFlag(Constant.NG);
		pObject.setMsg(e.getMessage());
		e.printStackTrace();
	}

	return pObject;
	}
	/*
	 *  updateCollectionOrder
	 */
		public static BasePosObject updateCollectionOrder(DispatchContext dctx,CollectionOrder CO) {
			Delegator delegator = dctx.getDelegator();
			GenericValue collectionOrderHeader = null;
			List<GenericValue> collectionOrderDtls = null;
			BasePosObject pObject = new BasePosObject();
			try {
				String docId = CO.getHeader().getDocId();
				 if(UtilValidate.isEmpty(docId)){
					 pObject.setFlag(Constant.NG);
				    	pObject.setMsg("订单ID错误");
				    	return pObject;
				    }
		    	// 根据ID 查询 销售单头
				collectionOrderHeader = delegator.findByPrimaryKey("CollectionOrderHeader", UtilMisc.toMap("docId", docId)); 
				// 草稿才能修改
				String docStatus = (String) collectionOrderHeader.get("docStatus");
				if (UtilValidate.isEmpty(docStatus) &&!docStatus.equals(Constant.DRAFT)) {
						pObject.setFlag(Constant.NG);
						pObject.setMsg("订单状态不是草稿");
						return pObject;
						}
				
				// 更新销售单头
				updateGVCollectionOrderHeader(collectionOrderHeader,CO.getHeader());
				// 根据id查询订单明细
				collectionOrderDtls = delegator.findByAnd("CollectionOrderDtl",
						UtilMisc.toMap("docId", docId));
				// 删除原有明细
				delegator.removeAll(collectionOrderDtls);
				//写入明细
				int lengs = CO.getDetail().size();
				for (int i = 0; i < lengs; i++) {
					 getGVCollectionOrderDtl(dctx,CO.getDetail().get(i));
				}
				pObject.setFlag(Constant.OK);
			}catch(Exception e){
				pObject.setFlag(Constant.NG);
		    	pObject.setMsg(e.getMessage());
				e.printStackTrace();
			}

		        return pObject;

			}
		/*
		 * 根据docId查找CollectionOrder
		 */
		public static BasePosObject getCollectionOrderById(DispatchContext dctx,Map<String, Object>view) {
			Delegator delegator = dctx.getDelegator();
			BasePosObject pObject = new BasePosObject();
			GenericValue collectionOrderHeader = null;
			List<GenericValue> collectionOrderDtls = null;
			 CollectionOrderDtl CcollectionOrderDtl = null;
			CollectionOrder collectionorder = new CollectionOrder();
			try {
				String docId = (String)view.get("docId");
				collectionOrderHeader = delegator.findByPrimaryKey("CollectionOrderHeader",UtilMisc.toMap("docId", docId));
				 if(UtilValidate.isEmpty(collectionOrderHeader)){
					 pObject.setFlag(Constant.OK);
				     return pObject;	
				 }
				// 获取java对象header
				CollectionOrderHeader CcollectionOrderHeader = getCollectionOrderHeader(collectionOrderHeader);
				// 根据ID查询CollectionOrderDtls
				collectionOrderDtls = delegator.findByAnd("CollectionOrderDtl",UtilMisc.toMap("docId", docId));
				List<CollectionOrderDtl> CcollectionOrderDtls = FastList.newInstance();
				for(GenericValue collectionOrderDtl : collectionOrderDtls){
					//根据参数获取 CollectionOrderDtl 
				CcollectionOrderDtl = getCollectionOrderDtl(collectionOrderDtl);
				//CollectionOrderDtl 
				CcollectionOrderDtls.add(CcollectionOrderDtl);
				}
			//设置collectionOrder的头和行
				collectionorder.setHeader(CcollectionOrderHeader);
				collectionorder.setDetail(CcollectionOrderDtls);
				 pObject.setFlag(Constant.OK);
				 pObject.setData(collectionorder);
		} catch (GenericEntityException e) {
			 pObject.setFlag(Constant.NG);
			 pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
	     return pObject;
	}
		/*
		*根据条件查询CollectionOrder
		*/
		public static BasePosObject getCollectionOrderByCondition(DispatchContext dctx,Map<String, Object>view) {
			Delegator delegator = dctx.getDelegator();
			BasePosObject pObject= new BasePosObject();
			List<GenericValue> LGcollectionOrderHeader = null;
			EntityCondition mainCond = null;
			List<CollectionOrderHeader> collectionOrderHeaders = new ArrayList<CollectionOrderHeader>();
			List<CollectionOrderDtl> collectionOrderDtls = new ArrayList<CollectionOrderDtl>();
			List<GenericValue> LGcollectionOrderDtl = null;
			List<String> orderBy = FastList.newInstance();
			orderBy.add("lastUpdateDocDate DESC");//倒序
			try {
				String where = (String) view.get("where");
				mainCond = EntityCondition.makeConditionWhere(where);
				LGcollectionOrderHeader = delegator.findList("CollectionOrderHeader",
						mainCond, null, orderBy, null, false);
				if (UtilValidate.isEmpty(LGcollectionOrderHeader)) {
					pObject.setFlag(Constant.OK);
					return pObject;
				}
				for (GenericValue gv : LGcollectionOrderHeader) {
					// 获取创建用户的id和name
					//Customer Ccustomer = getCustomerandCreateUser(gv);
					// 根据参数获得CollectionOrderHeader
					CollectionOrderHeader CcollectionOrderHeader = getCollectionOrderHeader(gv);
					//CcollectionOrderHeader.setCreateUserNameF(Ccustomer.getFirstName());
					//CcollectionOrderHeader.setCreateUserNameM(Ccustomer.getMiddleName());
					//CcollectionOrderHeader.setCreateUserNameL(Ccustomer.getLastName());
					// 放入 CollectionOrderHeaders List
					collectionOrderHeaders.add(CcollectionOrderHeader);
				}
				//String where = (String) view.get("where");
				mainCond = EntityCondition.makeConditionWhere(where);
				// 根据条件查找
				LGcollectionOrderDtl = delegator.findList("CollectionOrderDtl", mainCond, null,
						UtilMisc.toList("docId"), null, false);
				if (UtilValidate.isEmpty(LGcollectionOrderDtl)) {
					pObject.setFlag(Constant.OK);
					return pObject;
				}
				for (GenericValue collectionOrderDtl : LGcollectionOrderDtl) {
					// 根据参数获得CollectionOrderDtl
					CollectionOrderDtl CcollectionOrderDtl = getCollectionOrderDtl(collectionOrderDtl);
					// 放入List<SaleOrderDtl> 中
					collectionOrderDtls.add(CcollectionOrderDtl);
				}
				List<Object> collectionOrders = new ArrayList<Object>();
				collectionOrders.addAll(collectionOrderDtls);
				collectionOrders.addAll(collectionOrderHeaders);
				pObject.setFlag(Constant.OK);
				pObject.setData(collectionOrders);
			} catch (Exception e) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg(e.getMessage());
				e.printStackTrace();
			}
			return pObject;
		}

		/*
		 * 根据条件查询CollectionOrderHeader
		 */
		
		public static BasePosObject getCollectionOrderHeaderByCondition(DispatchContext dctx, Map<String, Object> view) {
			Delegator delegator = dctx.getDelegator();
			BasePosObject pObject = new BasePosObject();
			List<CollectionOrderHeader> collectionOrderHeaders = new ArrayList<CollectionOrderHeader>();
			List<GenericValue> collectionOrderHeaders1 = null;
			EntityCondition mainCond = null;
			List<String> orderBy = FastList.newInstance();
			orderBy.add("lastUpdateDocDate DESC");//倒序
			try{
				String where =(String)view.get("where");
				mainCond = EntityCondition.makeConditionWhere(where);
				collectionOrderHeaders1 = delegator.findList("CollectionOrderHeader",mainCond, null,orderBy,null, false);
				if(UtilValidate.isEmpty(collectionOrderHeaders1)){
					pObject.setFlag(Constant.OK);
			    	return pObject;
				}
				for(GenericValue collectionOrderHeader : collectionOrderHeaders1){
					//根据参数获得CollctionOrderHeader
					CollectionOrderHeader CcollectionOrderHeader = getCollectionOrderHeader( collectionOrderHeader);
					//放入 CollectionOrderHeaders List
					collectionOrderHeaders.add(CcollectionOrderHeader);
				}
				pObject.setData(collectionOrderHeaders);
				pObject.setFlag(Constant.OK);
			}catch(Exception e){
				pObject.setFlag(Constant.NG);
				pObject.setMsg(e.getMessage());
				e.printStackTrace();
			}
			return pObject;
		}
		/*
		 * //根据条件查询CollectionOrderDtl
		 */
		
		public static BasePosObject getCollectionOrderDtlByCondition(DispatchContext dctx, Map<String, Object>view) {
			Delegator delegator = dctx.getDelegator();
			BasePosObject pObject = new BasePosObject();
			List<CollectionOrderDtl> collectionOrderDtls = new ArrayList<CollectionOrderDtl>();
			List<GenericValue>collectionOrderDtls1 = null;
			EntityCondition mainCond = null;
			try{
				String where = (String)view.get("where");
				mainCond = EntityCondition.makeConditionWhere(where);
				//根据条件查找 List<GenericValue>
				collectionOrderDtls1 = delegator.findList("CollectionOrderDtl",mainCond, null, UtilMisc.toList("docId"),null, false);
				if(UtilValidate.isEmpty(collectionOrderDtls1)){
					pObject.setFlag(Constant.OK);
			    	return pObject;
				}
				for(GenericValue collectionOrderDtl : collectionOrderDtls1){
					//根据参数获得CollectionOrderDtl
					CollectionOrderDtl CcollectionOrderDtl = getCollectionOrderDtl(collectionOrderDtl);
					//放入List<CollectionOrderDtl> 中
					collectionOrderDtls.add(CcollectionOrderDtl);
				}
				pObject.setData(collectionOrderDtls);
				pObject.setFlag(Constant.OK);
			}catch(Exception e){
				pObject.setFlag(Constant.NG);
				pObject.setMsg(e.getMessage());
				e.printStackTrace();
			}
			return pObject;
		}
		
	// 根据参数的到GenericValue Collectionorderheader,放入map
	public static GenericValue getGVCollectionOrderHeader(DispatchContext dctx,
			CollectionOrderHeader collectionOrderHeader) {
		GenericValue CcollectionOrderHeader = null;
		Map<String, Object> CollectionOrderHeaderMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		try {
			CollectionOrderHeaderMap.put("docId",
					collectionOrderHeader.getDocId());
			CollectionOrderHeaderMap.put("docNo",
					collectionOrderHeader.getDocNo());
			CollectionOrderHeaderMap.put("partyId",
					collectionOrderHeader.getPartyId());
			CollectionOrderHeaderMap.put("storeId",
					collectionOrderHeader.getStoreId());
			CollectionOrderHeaderMap.put("baseEntry",
					collectionOrderHeader.getBaseEntry());
			CollectionOrderHeaderMap.put("extSystemNo",
					collectionOrderHeader.getExtSystemNo());
			CollectionOrderHeaderMap.put("extDocNo",
					collectionOrderHeader.getExtDocNo());
			CollectionOrderHeaderMap.put("postingDate",
					collectionOrderHeader.getPostingDate());
			CollectionOrderHeaderMap.put("docStatus",
					collectionOrderHeader.getDocStatus());
			CollectionOrderHeaderMap.put("memo",
					collectionOrderHeader.getMemo());
			CollectionOrderHeaderMap.put("createUserId",
					collectionOrderHeader.getCreateUserId());
			CollectionOrderHeaderMap.put("createDocDate", UtilDateTime.nowTimestamp());
			CollectionOrderHeaderMap.put("lastUpdateUserId",
					collectionOrderHeader.getLastUpdateUserId());
			CollectionOrderHeaderMap.put("lastUpdateDocDate", UtilDateTime.nowTimestamp());
			CollectionOrderHeaderMap.put("amount",
					collectionOrderHeader.getAmount());
			CollectionOrderHeaderMap.put("preCollectionAmount",
					collectionOrderHeader.getPreCollectionAmount());
			CollectionOrderHeaderMap.put("collectionAmount",
					collectionOrderHeader.getCollectionAmount());
			CollectionOrderHeaderMap.put("approvalUserId",
					collectionOrderHeader.getApprovalUserId());
			CollectionOrderHeaderMap.put("approvalDate",
					collectionOrderHeader.getApprovalDate());
			if(!collectionOrderHeader.getDocStatus().equals(Constant.DRAFT)){
				CollectionOrderHeaderMap.put("isSync",
						Constant.ONE);
			}else{
			CollectionOrderHeaderMap.put("isSync",
					collectionOrderHeader.getIsSync());}
			CollectionOrderHeaderMap.put("printNum",
					collectionOrderHeader.getPrintNum());
			CollectionOrderHeaderMap.put("attrName1",
					collectionOrderHeader.getAttrName1());
			CollectionOrderHeaderMap.put("attrName2",
					collectionOrderHeader.getAttrName2());
			CollectionOrderHeaderMap.put("attrName3",
					collectionOrderHeader.getAttrName3());
			CollectionOrderHeaderMap.put("attrName4",
					collectionOrderHeader.getAttrName4());
			CollectionOrderHeaderMap.put("attrName5",
					collectionOrderHeader.getAttrName5());
			CollectionOrderHeaderMap.put("attrName6",
					collectionOrderHeader.getAttrName6());
			CollectionOrderHeaderMap.put("attrName7",
					collectionOrderHeader.getAttrName7());
			CollectionOrderHeaderMap.put("attrName8",
					collectionOrderHeader.getAttrName8());
			CollectionOrderHeaderMap.put("attrName9",
					collectionOrderHeader.getAttrName9());
			CcollectionOrderHeader = delegator.makeValue(
					"CollectionOrderHeader", CollectionOrderHeaderMap);
			CcollectionOrderHeader.create();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CcollectionOrderHeader;
	}
	
	// 根据参数获得 CollectionOrderHeader
	public static CollectionOrderHeader getCollectionOrderHeader(GenericValue collectionOrderHeader) {
		CollectionOrderHeader CcollectionOrderHeader = null;
		try {
			CcollectionOrderHeader = new CollectionOrderHeader();
			CcollectionOrderHeader.setDocId((String)collectionOrderHeader.get("docId"));
			CcollectionOrderHeader.setDocNo((Long)collectionOrderHeader.get("docNo"));
			CcollectionOrderHeader.setPartyId((String)collectionOrderHeader.get("partyId"));
			CcollectionOrderHeader.setStoreId((String)collectionOrderHeader.get("storeId"));
			CcollectionOrderHeader.setBaseEntry((String)collectionOrderHeader.get("baseEntry"));
			CcollectionOrderHeader.setExtSystemNo((String)collectionOrderHeader.get("extSystemNo"));
			CcollectionOrderHeader.setExtDocNo((String)collectionOrderHeader.get("extDocNo"));
			CcollectionOrderHeader.setPostingDate((Timestamp)collectionOrderHeader.get("postingDate"));
			CcollectionOrderHeader.setDocStatus((String)collectionOrderHeader.get("docStatus"));
			CcollectionOrderHeader.setMemo((String)collectionOrderHeader.get("memo"));
			CcollectionOrderHeader.setCreateUserId((String)collectionOrderHeader.get("createUserId"));
			CcollectionOrderHeader.setCreateDocDate((Timestamp)collectionOrderHeader.get("createDocDate"));
			CcollectionOrderHeader.setLastUpdateUserId((String)collectionOrderHeader.get("lastUpdateUserId"));
			CcollectionOrderHeader.setLastUpdateDocDate((Timestamp)collectionOrderHeader.get("lastUpdateDocDate"));
			CcollectionOrderHeader.setAmount((BigDecimal)collectionOrderHeader.get("amount"));
			CcollectionOrderHeader.setPreCollectionAmount((BigDecimal)collectionOrderHeader.get("preCollectionAmount"));
			CcollectionOrderHeader.setCollectionAmount((BigDecimal)collectionOrderHeader.get("collectionAmount"));
			CcollectionOrderHeader.setApprovalUserId((String)collectionOrderHeader.get("approvalUserId"));
			CcollectionOrderHeader.setApprovalDate((Timestamp)collectionOrderHeader.get("approvalDate"));
			CcollectionOrderHeader.setIsSync((String)collectionOrderHeader.get("isSync"));
			CcollectionOrderHeader.setPrintNum((Long)collectionOrderHeader.get("printNum"));
			CcollectionOrderHeader.setAttrName1((String)collectionOrderHeader.get("attrName1"));
			CcollectionOrderHeader.setAttrName2((String)collectionOrderHeader.get("attrName2"));
			CcollectionOrderHeader.setAttrName3((String)collectionOrderHeader.get("attrName3"));
			CcollectionOrderHeader.setAttrName4((String)collectionOrderHeader.get("attrName4"));
			CcollectionOrderHeader.setAttrName5((String)collectionOrderHeader.get("attrName5"));
			CcollectionOrderHeader.setAttrName6((String)collectionOrderHeader.get("attrName6"));
			CcollectionOrderHeader.setAttrName7((String)collectionOrderHeader.get("attrName7"));
			CcollectionOrderHeader.setAttrName8((String)collectionOrderHeader.get("attrName8"));
			CcollectionOrderHeader.setAttrName9((String)collectionOrderHeader.get("attrName9"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return CcollectionOrderHeader;
	}
	
	// 根据参数得到 CollectionOrderDtl
		public static CollectionOrderDtl getCollectionOrderDtl(GenericValue collectionOrderDtl) {
			CollectionOrderDtl CcollectionOrderDtl = null;
			try {
				CcollectionOrderDtl = new CollectionOrderDtl();
				CcollectionOrderDtl.setDocId((String)collectionOrderDtl.get("docId"));
				CcollectionOrderDtl.setLineNo((Long)collectionOrderDtl.get("lineNo"));
				CcollectionOrderDtl.setStyle((String)collectionOrderDtl.get("style"));
				CcollectionOrderDtl.setType((String)collectionOrderDtl.get("type"));
				CcollectionOrderDtl.setExtNo((String)collectionOrderDtl.get("extNo"));
				CcollectionOrderDtl.setAmount((BigDecimal)collectionOrderDtl.get("amount"));
				CcollectionOrderDtl.setLineNoBaseEntry((Long)collectionOrderDtl.get("lineNoBaseEntry"));
				CcollectionOrderDtl.setCardCode((String)collectionOrderDtl.get("cardCode"));
				CcollectionOrderDtl.setCardName((String)collectionOrderDtl.get("cardName"));
				CcollectionOrderDtl.setMemo((String)collectionOrderDtl.get("memo"));
				CcollectionOrderDtl.setApprovalUserId((String)collectionOrderDtl.get("approvalUserId"));
				CcollectionOrderDtl.setAttrName1((String)collectionOrderDtl.get("attrName1"));
				CcollectionOrderDtl.setAttrName2((String)collectionOrderDtl.get("attrName2"));
				CcollectionOrderDtl.setAttrName3((String)collectionOrderDtl.get("attrName3"));
				CcollectionOrderDtl.setAttrName4((String)collectionOrderDtl.get("attrName4"));
				CcollectionOrderDtl.setAttrName5((String)collectionOrderDtl.get("attrName5"));
				CcollectionOrderDtl.setAttrName6((String)collectionOrderDtl.get("attrName6"));
				CcollectionOrderDtl.setAttrName7((String)collectionOrderDtl.get("attrName7"));
				CcollectionOrderDtl.setAttrName8((String)collectionOrderDtl.get("attrName8"));
				CcollectionOrderDtl.setAttrName9((String)collectionOrderDtl.get("attrName9"));
			} catch (Exception e) {
				e.printStackTrace();

			}
			return CcollectionOrderDtl;
		}

	
	// 根据参数得到 GenericValue CollectionOrderDtl 放入map
	public static GenericValue getGVCollectionOrderDtl(DispatchContext dctx,
			CollectionOrderDtl collectionOrderDtl) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> CollectionOrderDtlMap = FastMap.newInstance();
		GenericValue CcollectionOrderDtl = null;
		try {
			CollectionOrderDtlMap.put("docId", collectionOrderDtl.getDocId());
			CollectionOrderDtlMap.put("lineNo", collectionOrderDtl.getLineNo());
			CollectionOrderDtlMap.put("style", collectionOrderDtl.getStyle());
			CollectionOrderDtlMap.put("type", collectionOrderDtl.getType());
			CollectionOrderDtlMap.put("extNo", collectionOrderDtl.getExtNo());
			CollectionOrderDtlMap.put("amount", collectionOrderDtl.getAmount());
			CollectionOrderDtlMap.put("lineNoBaseEntry",
					collectionOrderDtl.getLineNoBaseEntry());
			CollectionOrderDtlMap.put("code",
					collectionOrderDtl.getCode());
			CollectionOrderDtlMap.put("cardCode",
					collectionOrderDtl.getCardCode());
			CollectionOrderDtlMap.put("cardName",
					collectionOrderDtl.getCardName());
			CollectionOrderDtlMap.put("serialNo",
					collectionOrderDtl.getSerialNo());
			CollectionOrderDtlMap.put("memo", collectionOrderDtl.getMemo());
			CollectionOrderDtlMap.put("approvalUserId",
					collectionOrderDtl.getApprovalUserId());
			CollectionOrderDtlMap.put("attrName1",
					collectionOrderDtl.getAttrName1());
			CollectionOrderDtlMap.put("attrName2",
					collectionOrderDtl.getAttrName2());
			CollectionOrderDtlMap.put("attrName3",
					collectionOrderDtl.getAttrName3());
			CollectionOrderDtlMap.put("attrName4",
					collectionOrderDtl.getAttrName4());
			CollectionOrderDtlMap.put("attrName5",
					collectionOrderDtl.getAttrName5());
			CollectionOrderDtlMap.put("attrName6",
					collectionOrderDtl.getAttrName6());
			CollectionOrderDtlMap.put("attrName7",
					collectionOrderDtl.getAttrName7());
			CollectionOrderDtlMap.put("attrName8",
					collectionOrderDtl.getAttrName8());
			CollectionOrderDtlMap.put("attrName9",
					collectionOrderDtl.getAttrName9());
			CcollectionOrderDtl = delegator.makeValue("CollectionOrderDtl",
					CollectionOrderDtlMap);
			CcollectionOrderDtl.create();

		} catch (Exception e) {
			e.printStackTrace();

		}
		return CcollectionOrderDtl;
	}

  //更新销售订单头
	public static GenericValue updateGVCollectionOrderHeader(GenericValue Ccollectionorder,CollectionOrderHeader collectionOrderHeader){
	
		try{
			Ccollectionorder.set("docId",
					collectionOrderHeader.getDocId());
			Ccollectionorder.set("docNo",
					collectionOrderHeader.getDocNo());
			Ccollectionorder.set("partyId",
					collectionOrderHeader.getPartyId());
			Ccollectionorder.set("storeId",
					collectionOrderHeader.getStoreId());
			Ccollectionorder.set("baseEntry",
					collectionOrderHeader.getBaseEntry());
			Ccollectionorder.set("extSystemNo",
					collectionOrderHeader.getExtSystemNo());
			Ccollectionorder.set("extDocNo",
					collectionOrderHeader.getExtDocNo());
			Ccollectionorder.set("postingDate",
					collectionOrderHeader.getPostingDate());
			Ccollectionorder.set("docStatus",
					collectionOrderHeader.getDocStatus());
			Ccollectionorder.set("memo",
					collectionOrderHeader.getMemo());
			Ccollectionorder.set("createUserId",
					collectionOrderHeader.getCreateUserId());
			Ccollectionorder.set("createDocDate",UtilDateTime.nowTimestamp());
			Ccollectionorder.set("lastUpdateUserId",
					collectionOrderHeader.getLastUpdateUserId());
			Ccollectionorder.set("lastUpdateDocDate",UtilDateTime.nowTimestamp());
			Ccollectionorder.set("amount",
					collectionOrderHeader.getAmount());
			Ccollectionorder.set("preCollectionAmount",
					collectionOrderHeader.getPreCollectionAmount());
			Ccollectionorder.set("collectionAmount",
					collectionOrderHeader.getCollectionAmount());
			Ccollectionorder.set("approvalUserId",
					collectionOrderHeader.getApprovalUserId());
			Ccollectionorder.set("approvalDate",
					collectionOrderHeader.getApprovalDate());
			Ccollectionorder.set("isSync",
					collectionOrderHeader.getIsSync());
			Ccollectionorder.set("printNum",
					collectionOrderHeader.getPrintNum());
			Ccollectionorder.set("attrName1",
					collectionOrderHeader.getAttrName1());
			Ccollectionorder.set("attrName2",
					collectionOrderHeader.getAttrName2());
			Ccollectionorder.set("attrName3",
					collectionOrderHeader.getAttrName3());
			Ccollectionorder.set("attrName4",
					collectionOrderHeader.getAttrName4());
			Ccollectionorder.set("attrName5",
					collectionOrderHeader.getAttrName5());
			Ccollectionorder.set("attrName6",
					collectionOrderHeader.getAttrName6());
			Ccollectionorder.set("attrName7",
					collectionOrderHeader.getAttrName7());
			Ccollectionorder.set("attrName8",
					collectionOrderHeader.getAttrName8());
			Ccollectionorder.set("attrName9",
					collectionOrderHeader.getAttrName9());
			Ccollectionorder.store();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return Ccollectionorder;
	}
	
	/*
	 * 根据参数获取person人员
	 
	public static Customer getCustomerandCreateUser(GenericValue customer) {
		Customer Ccustomer = null;
		try {
			Ccustomer = new Customer();
			Ccustomer.setPartyId((String) customer.get("partyId"));
			Ccustomer.setFirstName((String) customer.get("firstName"));
			Ccustomer.setMiddleName((String) customer.get("middleName"));
			Ccustomer.setLastName((String) customer.get("lastName"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Ccustomer;
	}*/
	
	
}