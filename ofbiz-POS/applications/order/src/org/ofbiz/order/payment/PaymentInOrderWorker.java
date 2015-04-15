package org.ofbiz.order.payment;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.order.parameter.Parameter;
import org.ofbiz.order.payment.PaymentInOrder;
import org.ofbiz.order.payment.PaymentInOrderDtl;
import org.ofbiz.order.payment.PaymentInOrderHeader;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.util.ConvertUtil;

import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;

public class PaymentInOrderWorker {

	public static final String module = PaymentInOrderWorker.class.getName();

	private PaymentInOrderWorker() {
	}

	/*
	 * findPostingDate
	 */
	@SuppressWarnings("deprecation")
	public static BasePosObject findPostingDate(DispatchContext dctx,
			Map<String, Object> mapIn) {
		BasePosObject pObject = new BasePosObject();
		Delegator delegator = dctx.getDelegator();
    	List<String> orderBy = FastList.newInstance();// 排序条件
    	EntityFindOptions findOptions =  new EntityFindOptions() ;
    	Set<String> fieldsToSelect = FastSet.newInstance();
    	Timestamp time = null;
		try {
			if (mapIn.get("productStoreId") == null) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg("没有选择店面");
				return pObject;
			}
			GenericValue postDate = delegator.findByPrimaryKey("PostingDate",
					UtilMisc.toMap("productStoreId",mapIn.get("productStoreId")));
			pObject.setFlag(Constant.OK);
			if (postDate != null) {
				pObject.setData(postDate);
			}else {
				String where1 = "STORE_ID='"+mapIn.get("productStoreId")+"' AND DOC_TYPE='"+Constant.PAYDAY+"'";
				String where0 = "STORE_ID='"+mapIn.get("productStoreId")+"' AND DOC_TYPE='"+Constant.PAYMENT+"'";
				EntityCondition mainCond1 = EntityCondition.makeConditionWhere(where1);
				EntityCondition mainCond0 = EntityCondition.makeConditionWhere(where0);
				fieldsToSelect.add("postingDate");
				orderBy.add("postingDate DESC");
				findOptions.setDistinct(true);
				//日结账查询posDates1，中途缴款查询posDates0  
	        	List<GenericValue> posDates1 = delegator.findList("PaymentInOrderHeader", mainCond1, fieldsToSelect, orderBy, findOptions, false);
	        	List<GenericValue> posDates0 = delegator.findList("PaymentInOrderHeader", mainCond0, fieldsToSelect, orderBy, findOptions, false);
	        	if(UtilValidate.isEmpty(posDates0) && UtilValidate.isEmpty(posDates1)){
					Timestamp newTime = UtilDateTime.nowTimestamp();
					String str = ConvertUtil.convertDateToString(newTime);
					time = ConvertUtil.convertStringToTimeStamp(str);
					time.setHours(0);
					time.setMinutes(0);
					time.setSeconds(0);
				}else if(UtilValidate.isNotEmpty(posDates0) && UtilValidate.isEmpty(posDates1)){
					time = (Timestamp) posDates0.get(0).get("postingDate");//缴款单头中最大中途缴款时间
				}else if(UtilValidate.isNotEmpty(posDates1) && UtilValidate.isEmpty(posDates0)){
					time = (Timestamp) posDates1.get(0).get("postingDate");//缴款单头中最大日结账时间
					time = addOneDay(time);
				}else if(UtilValidate.isNotEmpty(posDates0) && UtilValidate.isNotEmpty(posDates1)){
					Timestamp time1 = (Timestamp) posDates1.get(0).get("postingDate");
					Timestamp time0 = (Timestamp) posDates0.get(0).get("postingDate");
					if((time1.getTime()-time0.getTime())<0){
						time = time0;
					}else{
						time = time1;
						time = addOneDay(time);
					}
				}
	        	Map<String, Object> map = new HashMap<String, Object>();
				map.put("productStoreId", mapIn.get("productStoreId"));
				map.put("isSync", "0");
				map.put("postingDate", time);
				GenericValue postingDate = delegator.makeValidValue("PostingDate", map);
				postingDate.create();
				pObject.setData(postingDate);
			}
		}catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//时间戳时间加一天方法
	public static Timestamp addOneDay(Timestamp time){
		Timestamp timestamp = null;
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
			Calendar cd = Calendar.getInstance(); 
			cd.setTime(time);
			cd.add(Calendar.DATE, 1);
			String timeString =sdf.format(cd.getTime());
			timestamp =  new Timestamp(sdf.parse(timeString).getTime());
		}catch(Exception e){
			e.printStackTrace();
		}
		return timestamp;
	}
	
	//过账日期时间加一天方法
	
	public static BasePosObject addPostingDateOneDay(DispatchContext dctx, Parameter parameter){
		BasePosObject pObject = new BasePosObject();
		Delegator delegator = dctx.getDelegator();
		Timestamp now = null;
		try{
			String storeId = parameter.getStoreId();
			Timestamp newTime = UtilDateTime.nowTimestamp();
			String str = ConvertUtil.convertDateToString(newTime);
			now = ConvertUtil.convertStringToTimeStamp(str);
			now.setHours(0);
			now.setMinutes(0);
			now.setSeconds(0);
			GenericValue gv = delegator.findOne("PostingDate",UtilMisc.toMap("productStoreId", storeId), false);
			Timestamp datePostingdate = (Timestamp) gv.get("postingDate");
			if(datePostingdate.getTime() - now.getTime() <= 0){
				datePostingdate = addOneDay(datePostingdate);
				gv.set("postingDate", datePostingdate);
				gv.store();
				pObject.setFlag(Constant.OK);
			}else{
				pObject.setFlag(Constant.NG);
				pObject.setMsg("时间大于今天");
			}
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
		
	}
	
	@SuppressWarnings("deprecation")
	public static BasePosObject createPaymentInOrder(DispatchContext dctx,
			PaymentInOrder paymentInOrder) {
		BasePosObject pObject = new BasePosObject();
		Delegator delegator = dctx.getDelegator();
		try {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 定义格式，不显示毫秒
			// 当前Orderpostingdate日期
			Timestamp orderPostingdate = (Timestamp) paymentInOrder.getHeader()
					.getPostingDate();
			String storeId = paymentInOrder.getHeader().getStoreId();
			GenericValue postInfo = delegator.findOne("PostingDate",
					UtilMisc.toMap("productStoreId", storeId), false);
			if (UtilValidate.isEmpty(postInfo)) {
				pObject.setFlag(Constant.NG);
				pObject.setData(postInfo);
				pObject.setMsg("StoreId不同");
				return pObject;
			}
			// 数据库Postingdate日期
			Timestamp datePostingdate = (Timestamp) postInfo.get("postingDate");
			// 获取当前系统时间
			Timestamp times = UtilDateTime.nowTimestamp();
			String str = ConvertUtil.convertDateToString(times);
			Timestamp systime = ConvertUtil.convertStringToTimeStamp(str);
			systime.setHours(0);
			systime.setMinutes(0);
			systime.setSeconds(0);
			if (orderPostingdate.equals(datePostingdate)
					&& (systime.getTime() - orderPostingdate.getTime() >= 0)) {

				int lenth = paymentInOrder.getDetail().size();
				// 写入数据
				getGVPaymentInOrderHeader(dctx, paymentInOrder.getHeader());
				for (int i = 0; i < lenth; i++) {
					getGVPaymentOrderDtl(dctx, paymentInOrder.getDetail()
							.get(i));
				}
			} else {
				pObject.setFlag(Constant.NG);
				pObject.setMsg("不符合时间");
				return pObject;
			}
			// 日结账的时候，且状态为确定/批准/已清时候，
			String doctype = paymentInOrder.getHeader().getDocType();
			String docstatus = paymentInOrder.getHeader().getDocStatus();
			if (doctype.equals(Constant.PAYDAY)
					&& (docstatus.equals(Constant.VALID)
							|| docstatus.equals(Constant.APPROVED) || docstatus
								.equals(Constant.CLEARED))) {
				@SuppressWarnings("unused")
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 定义格式，不显示毫秒
				GenericValue postInfos = delegator.findOne("PostingDate",
						UtilMisc.toMap("productStoreId", storeId), false);
				Timestamp dataPostdate = (Timestamp) postInfos
						.get("postingDate");
				String datetime = df.format(dataPostdate);
				// 创建成功日期+1
				Calendar cd = Calendar.getInstance();
				cd.setTime(df.parse(datetime));
				cd.add(Calendar.DATE, 1);
				String addtime = df.format(cd.getTime());
				Timestamp stamp = Timestamp.valueOf(addtime);
				postInfos.set("postingDate", stamp);
				postInfos.store();
			}
			pObject.setFlag(Constant.OK);

		} catch (Exception e) {
			e.printStackTrace();
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
		}
		return pObject;
	}
	

	
	/**
	 * GetPaymentInOrderHeaderById
	 */
	@SuppressWarnings("unchecked")
	public static BasePosObject getPaymentInOrderHeaderById(DispatchContext dctx, JSONObject jsonObj) {
		BasePosObject pObject = new BasePosObject ();
		List<GenericValue> list = null;
		BasePosObject result = null;
		try {
			String docId = (String)jsonObj.get("docId");
			String where = "DOC_ID='"+docId+"'";
			Map<String, Object> viewMap = FastMap.newInstance();
			viewMap.put("where",where);
			result = getPaymentInOrderHeaderByCondition(dctx,viewMap);
			list = (List<GenericValue>)result.getData();
			pObject.setFlag(Constant.OK);
			if(UtilValidate.isNotEmpty(list)){
				pObject.setData(list.get(0));
			}
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	/**
	 * GetPaymentInOrderHeaderByCondition
	 */
	public static BasePosObject getPaymentInOrderHeaderByCondition(
			DispatchContext dctx, Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		EntityCondition mainCond = null;
		List<String> orderBy = FastList.newInstance();
		orderBy.add("lastUpdateDocDate DESC");// 倒序
		try {
			String where = (String) view.get("where");
			mainCond = EntityCondition.makeConditionWhere(where);
			List<GenericValue> Header = delegator.findList(
					"PaymentInOrderHeaderMessage", mainCond, null, orderBy, null,
					false);
			pObject.setFlag(Constant.OK);
			if (UtilValidate.isNotEmpty(Header)) {
				pObject.setData(Header);
			}
		} catch (Exception e) {
			e.printStackTrace();
			pObject.setMsg(e.getMessage());
			pObject.setFlag(Constant.NG);
		}
		return pObject;
	}
	/**
	 * getPaymentInOrderDtlById
	 */
	@SuppressWarnings("unchecked")
	public static BasePosObject getPaymentInOrderDtlById(DispatchContext dctx,JSONObject jsonObj) {
		BasePosObject pObject = new BasePosObject ();
		List<GenericValue> list = null;
		BasePosObject result = null;
		try {
			String docId = (String)jsonObj.get("docId");
			String where = "DOC_ID='"+docId+"'";
			Map<String, Object> viewMap = FastMap.newInstance();
			viewMap.put("where",where);
			result = getPaymentInOrderDtlByCondition(dctx,viewMap);
			list = (List<GenericValue>)result.getData();
			pObject.setFlag(Constant.OK);
			if(UtilValidate.isNotEmpty(list)){
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
	 * 根据条件查询PaymentInOrderDtl
	 */
	public static BasePosObject getPaymentInOrderDtlByCondition(
			DispatchContext dctx, Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		EntityCondition mainCond = null;
		try {
			String where = (String) view.get("where");
			mainCond = EntityCondition.makeConditionWhere(where);
			// 根据条件查找订单明细
			List<GenericValue> Dtl = delegator.findList("PaymentInOrderDtlMessage",
					mainCond, null, UtilMisc.toList("docId"), null, false);
			pObject.setFlag(Constant.OK);

			if (UtilValidate.isNotEmpty(Dtl)) {
				pObject.setData(Dtl);
			}
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}

	/**
	 * getPaymentInOrderById
	 * 
	 * @param dctx
	 * @param paymentinorderheader
	 * @return
	 */
	public static BasePosObject getPaymentInOrderById(DispatchContext dctx,
			Map<String, Object> view) {
		BasePosObject pObject = new BasePosObject();
		Delegator delegator = dctx.getDelegator();
		GenericValue paymentInOrderHeader = null;
		List<GenericValue> paymentInOrderDtls = null;
		ObjectOrder objectOrder = new ObjectOrder();
		try {
			String docId = (String) view.get("docId");
			// 根据ID查询RefundOrderHeader数值对象(map形式)
			paymentInOrderHeader = delegator.findByPrimaryKey(
					"PaymentInOrderHeaderMessage", UtilMisc.toMap("docId", docId));
			if (UtilValidate.isEmpty(paymentInOrderHeader)) {
				pObject.setFlag(Constant.OK);
				return pObject;
			}

			// 根据ID查询List<GenericValue> paymentOrderDtls
			paymentInOrderDtls = delegator.findByAnd("PaymentInOrderDtlMessage",
					UtilMisc.toMap("docId", docId));
			if (UtilValidate.isEmpty(paymentInOrderDtls)) {
				pObject.setFlag(Constant.OK);
				return pObject;
			}
			// 设置paymentOrder的头和行
			objectOrder.setHeader(paymentInOrderHeader);
			objectOrder.setDetail(paymentInOrderDtls);
			pObject.setFlag(Constant.OK);
			pObject.setData(objectOrder);
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}

	/**
	 * 根据条件查询PaymentOrder
	 */
	public static BasePosObject getPaymentInOrderByCondition(
			DispatchContext dctx, Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<GenericValue> paymentHeaders1 = null;
		EntityCondition mainCond = null;
		List<GenericValue> paymentDtls1 = null;
		List<String> orderBy = FastList.newInstance();
		orderBy.add("lastUpdateDocDate DESC");// 倒序
		try {
			String where = (String) view.get("where");
			mainCond = EntityCondition.makeConditionWhere(where);
			paymentHeaders1 = delegator.findList("PaymentInOrderHeaderMessage",
					mainCond, null, orderBy, null, false);
			if (UtilValidate.isEmpty(paymentHeaders1)) {
				pObject.setFlag(Constant.OK);
				return pObject;
			}

			// 根据条件获取明细z
			mainCond = EntityCondition.makeConditionWhere(where);
			paymentDtls1 = delegator.findList("PaymentInOrderDtlMessage", mainCond,
					null, UtilMisc.toList("docId"), null, false);
			if (UtilValidate.isEmpty(paymentDtls1)) {
				pObject.setFlag(Constant.OK);
				return pObject;
			}

			ObjectOrder objectOrder = new ObjectOrder();
			objectOrder.setHeader(paymentHeaders1);
			objectOrder.setDetail(paymentDtls1);
			pObject.setFlag(Constant.OK);
			pObject.setData(objectOrder);
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}

	// 根据参数的到GenericValue paymentOrderheader,放入map
	public static GenericValue getGVPaymentInOrderHeader(DispatchContext dctx,
			PaymentInOrderHeader paymentinorderheader) {
		GenericValue paymentInOrderHeader = null;
		Map<String, Object> paymentinorderheaderMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		try {
			paymentinorderheaderMap.put("docId",
					paymentinorderheader.getDocId());
			paymentinorderheaderMap.put("docNo",
					paymentinorderheader.getDocNo());
			paymentinorderheaderMap.put("partyId",
					paymentinorderheader.getPartyId());
			paymentinorderheaderMap.put("storeId",
					paymentinorderheader.getStoreId());
			paymentinorderheaderMap.put("baseEntry",
					paymentinorderheader.getBaseEntry());
			paymentinorderheaderMap.put("extSystemNo",
					paymentinorderheader.getExtSystemNo());
			paymentinorderheaderMap.put("extDocNo",
					paymentinorderheader.getExtDocNo());
			paymentinorderheaderMap.put("postingDate",
					paymentinorderheader.getPostingDate());
			paymentinorderheaderMap.put("docStatus",
					paymentinorderheader.getDocStatus());
			paymentinorderheaderMap.put("memo", paymentinorderheader.getMemo());
			paymentinorderheaderMap.put("createUserId",
					paymentinorderheader.getCreateUserId());
			paymentinorderheaderMap.put("createDocDate",
					UtilDateTime.nowTimestamp());
			paymentinorderheaderMap.put("lastUpdateUserId",
					paymentinorderheader.getLastUpdateUserId());
			paymentinorderheaderMap.put("lastUpdateDocDate",
					UtilDateTime.nowTimestamp());
			paymentinorderheaderMap.put("amount",
					paymentinorderheader.getAmount());
			paymentinorderheaderMap.put("approvalUserId",
					paymentinorderheader.getApprovalUserId());
			paymentinorderheaderMap.put("approvalDate",
					paymentinorderheader.getApprovalDate());
			if (!paymentinorderheader.getDocStatus().equals(Constant.DRAFT)) {
				paymentinorderheaderMap.put("isSync", Constant.ONE);
			} else {
				paymentinorderheaderMap.put("isSync",
						paymentinorderheader.getIsSync());
			}
			paymentinorderheaderMap.put("printNum",
					paymentinorderheader.getPrintNum());
			paymentinorderheaderMap.put("docType",
					paymentinorderheader.getDocType());
			paymentinorderheaderMap.put("attrName1",
					paymentinorderheader.getAttrName1());
			paymentinorderheaderMap.put("attrName2",
					paymentinorderheader.getAttrName2());
			paymentinorderheaderMap.put("attrName3",
					paymentinorderheader.getAttrName3());
			paymentinorderheaderMap.put("attrName4",
					paymentinorderheader.getAttrName4());
			paymentinorderheaderMap.put("attrName5",
					paymentinorderheader.getAttrName5());
			paymentinorderheaderMap.put("attrName6",
					paymentinorderheader.getAttrName6());
			paymentinorderheaderMap.put("attrName7",
					paymentinorderheader.getAttrName7());
			paymentinorderheaderMap.put("attrName8",
					paymentinorderheader.getAttrName8());
			paymentinorderheaderMap.put("attrName9",
					paymentinorderheader.getAttrName9());
			paymentInOrderHeader = delegator.makeValue("PaymentInOrderHeader",
					paymentinorderheaderMap);
			paymentInOrderHeader.create();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return paymentInOrderHeader;
	}

	// 根据参数得到 GenericValue paymentOrderDtl 放入map
	public static GenericValue getGVPaymentOrderDtl(DispatchContext dctx,
			PaymentInOrderDtl paymentInOrderDtl) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> PaymentInOrderDtlMap = FastMap.newInstance();
		GenericValue PaymentinOrderDtl = null;
		try {
			PaymentInOrderDtlMap.put("docId", paymentInOrderDtl.getDocId());
			PaymentInOrderDtlMap.put("lineNo", paymentInOrderDtl.getLineNo());
			PaymentInOrderDtlMap.put("lineNoBaseEntry",
					paymentInOrderDtl.getLineNoBaseEntry());
			PaymentInOrderDtlMap.put("type", paymentInOrderDtl.getType());
			PaymentInOrderDtlMap.put("extNo", paymentInOrderDtl.getExtNo());
			PaymentInOrderDtlMap.put("amount", paymentInOrderDtl.getAmount());
			PaymentInOrderDtlMap.put("memo", paymentInOrderDtl.getMemo());
			PaymentInOrderDtlMap.put("approvalUserId",
					paymentInOrderDtl.getApprovalUserId());
			PaymentInOrderDtlMap.put("targetAmount",
					paymentInOrderDtl.getTargetAmount());
			PaymentInOrderDtlMap.put("preAmount",
					paymentInOrderDtl.getPreAmount());
			PaymentInOrderDtlMap.put("erpCheckAmount",
					paymentInOrderDtl.getErpCheckAmount());
			PaymentInOrderDtlMap.put("diffAmount",
					paymentInOrderDtl.getDiffAmount());
			PaymentInOrderDtlMap.put("erpMemo", paymentInOrderDtl.getErpMemo());
			PaymentInOrderDtlMap.put("style", paymentInOrderDtl.getStyle());
			PaymentInOrderDtlMap.put("attrName1",
					paymentInOrderDtl.getAttrName1());
			PaymentInOrderDtlMap.put("attrName2",
					paymentInOrderDtl.getAttrName2());
			PaymentInOrderDtlMap.put("attrName3",
					paymentInOrderDtl.getAttrName3());
			PaymentInOrderDtlMap.put("attrName4",
					paymentInOrderDtl.getAttrName4());
			PaymentInOrderDtlMap.put("attrName5",
					paymentInOrderDtl.getAttrName5());
			PaymentInOrderDtlMap.put("attrName6",
					paymentInOrderDtl.getAttrName6());
			PaymentInOrderDtlMap.put("attrName7",
					paymentInOrderDtl.getAttrName7());
			PaymentInOrderDtlMap.put("attrName8",
					paymentInOrderDtl.getAttrName8());
			PaymentInOrderDtlMap.put("attrName9",
					paymentInOrderDtl.getAttrName9());
			PaymentinOrderDtl = delegator.makeValue("PaymentInOrderDtl",
					PaymentInOrderDtlMap);
			PaymentinOrderDtl.create();

		} catch (Exception e) {
			e.printStackTrace();

		}
		return PaymentinOrderDtl;
	}

	

	
}
