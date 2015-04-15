package org.ofbiz.order.salesoutwhsorder;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.delivery.webService.DeliveryWebWork;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.order.parameter.Parameter;
import org.ofbiz.order.saleorder.SaleOrderWork;
import org.ofbiz.receive.webService.ReceiveWebWork;
import org.ofbiz.service.DispatchContext;

public class SalesOutWhsOrderWork {
	public static final String module = SalesOutWhsOrderWork.class.getName();

	/*
	 * 创建销售出库
	 */
	public static BasePosObject createSalesOutWhsOrder(DispatchContext dctx,
			SalesOutWhsOrder salesoutwhsorder) {
		BasePosObject pObject = new BasePosObject();
		List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
		int lent = salesoutwhsorder.getItem().size();
		Map<String,Object> itemMap = null;
		BasePosObject result = null;
		boolean isSequenceNull = false;
		try{
			
			// 获取销售出库header
			Map<String, Object> mapHeader = getGVSalesOutWhsOrderHeaders(dctx,
					salesoutwhsorder.getHeader());


			// 获取出库明细
			for (int i = 0; i < lent; i++) {
				Map<String, Object> salesoutwhsorderdtl = getGVSalesOutWhsOrderDtls(
						dctx, salesoutwhsorder.getItem().get(i));
				listItem.add(salesoutwhsorderdtl);
			}
			
			// 解锁库存
			List<Map<String, Object>> listItema = new ArrayList<Map<String, Object>>();

			int lens = salesoutwhsorder.getItem().size();
			for (int i = 0; i < lens; i++) {
				// 获取解锁数量如果数量<0 不等于null 解锁库存
				Long ulockedquantity = salesoutwhsorder.getItem().get(i)
						.getUnLockedQuantity();
				if (UtilValidate.isNotEmpty(ulockedquantity)
						&& ulockedquantity != 0) {
					Map<String, Object> receiveOrderdtl = getSalesOutWhsOrderDtls(
							dctx, salesoutwhsorder.getItem().get(i));
					listItema.add(receiveOrderdtl);
				}
			}

			if (listItema.size() > 0) {
				BasePosObject SaleReceive = ReceiveWebWork.commadReceive(
						mapHeader, listItema, dctx);
				if (SaleReceive.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(SaleReceive.getMsg());
					return pObject;
				}
			}
			
			// 添加出库
			BasePosObject saveDeli = DeliveryWebWork.szDelivery(mapHeader,
					listItem, dctx);
			if (saveDeli.getFlag().equals(Constant.NG)) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg(saveDeli.getMsg());
				//pObject.setData("创建失败");
				return pObject;
			}
			//如果出库单明细是串号管理且串号为空时，回写销售订单明细中的串号
			for (int i = 0; i < listItem.size(); i++) {
					itemMap = listItem.get(i);
					if("Y".equals(itemMap.get("isSequence"))){
					            result =	SaleOrderWork.updateSalesOrderDtlSN(dctx,itemMap.get("baseEntry"),
								itemMap.get("baseLineNo"),itemMap.get("sequenceId"));
						if (result.getFlag().equals(Constant.NG)) {
							pObject.setFlag(Constant.NG);
							pObject.setMsg("回写销售订单明细串号失败");
							return pObject;
						}
						if (UtilValidate.isNotEmpty(result.getData())) {
							isSequenceNull = true;
						}
					  }
				}
			//回写销售订单明细中的串号同时，回写销售订单头中的lastUpdateUserId
			if(isSequenceNull){
				 result =	SaleOrderWork.updateSalesOrderHeaderUserId(dctx,itemMap.get("baseEntry"),mapHeader.get("userId"));
				if (result.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg("回写销售订单头中最后更新人失败");
					return pObject;
				}
			}
			
			// 更新销售订单明细出库数量
			pObject = SaleOrderWork.addSalesOrderWhsQty(dctx, salesoutwhsorder);
			if (pObject.getFlag().equals(Constant.NG)) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg("更新未成功");
				return pObject;
			}
		
			pObject.setFlag(Constant.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pObject;
	}

	/*
	 * 更新订单状态
	 */
	@SuppressWarnings("unused")
	public static BasePosObject updateSalesOutWhsOrderStatus(
			DispatchContext dctx, Parameter parameter) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		GenericValue salesOutWhsOrderHeader = null;// 数据库中的数值对象
		// 根据主键docId 查询SalesoutwhsOrderHeader的非空的且不是部分已清的Status状态
		try {
			String updateUserId = parameter.getUpdateUserId();
			Timestamp updateDate = parameter.getUpdateDate();// 待用
			String docId = parameter.getDocId();
			String docStatus = parameter.getDocStatus();
			salesOutWhsOrderHeader = delegator.findByPrimaryKey("DeliveryDoc",
					UtilMisc.toMap("docId", docId));
			// 获取销售出库header的docStatus 如果为已经或者作废就不更新
			String SdocStatus = (String) salesOutWhsOrderHeader
					.get("docStatus");
			if (SdocStatus.equals(Constant.CLEARED)
					&& SdocStatus.equals(Constant.INVALID)) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg("Docstatus的状态为已清或者作废");
				return pObject;
			}
			if (UtilValidate.isNotEmpty(docStatus)
					&& !docStatus.equals(SdocStatus)) {
				// 更新docStatus
				salesOutWhsOrderHeader.set("docStatus", docStatus);
			}
			salesOutWhsOrderHeader.store();
			if(!docStatus.equals(Constant.DRAFT)){
				salesOutWhsOrderHeader.put("isSync",Constant.ONE);
				}
			// 更新修改时间
			salesOutWhsOrderHeader.set("updateDate",
					UtilDateTime.nowTimestamp());
			salesOutWhsOrderHeader.set("updateUserId", updateUserId);
			salesOutWhsOrderHeader.store();
			pObject.setFlag(Constant.OK);

		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}

		return pObject;
	}

	/**
	 * update SalesoutwhsOrder
	 */
	public static BasePosObject updateSalesOutWhsOrder(DispatchContext dctx,
			SalesOutWhsOrder salesoutwhsorder) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		GenericValue salesOutWhsOrderHeader = null;
		List<GenericValue> salesOutWhsOrderDtls = null;
		try {
			String docId =((SalesOutWhsOrderHeader)salesoutwhsorder.getHeader()).getDocId();
			if (UtilValidate.isEmpty(docId)) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg("销售出库单ID错误");
				return pObject;
			}

			// 根据ID 查询 销售单头
			salesOutWhsOrderHeader = delegator.findByPrimaryKey("DeliveryDoc",
					UtilMisc.toMap("docId", docId));
			// 草稿修改
			String docStatus = (String) salesOutWhsOrderHeader.get("docStatus");
			if (UtilValidate.isEmpty(docStatus)
					|| !docStatus.equals(Constant.DRAFT)) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg("订单状态不是草稿");
				return pObject;
			}
			// 更新销售单头
			updateSalesOutWhsOrderHeader(salesOutWhsOrderHeader,
					(SalesOutWhsOrderHeader)salesoutwhsorder.getHeader());
			// 根据id查询订单明细
			salesOutWhsOrderDtls = delegator.findByAnd("DeliveryItem",
					UtilMisc.toMap("docId", docId));
			// 删除原有明细
			delegator.removeAll(salesOutWhsOrderDtls);
			// 写入明细
			int lengs = salesoutwhsorder.getItem().size();
			for (int i = 0; i < lengs; i++) {
				getGVSalesOutWhsOrderDtl(dctx, salesoutwhsorder.getItem()
						.get(i));
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
	 * 根基条件查询销售出库单头
	 */

	public static BasePosObject getSalesOutWhsOrderHeaderByCondition(
			DispatchContext dctx, Parameter parameter) {
		BasePosObject pObject = new BasePosObject();
		try {
			Map<String, Object> mapIn = FastMap.newInstance();
			String docId = parameter.getDocId();
			String baseEntry = parameter.getBaseEntry();
			String docStatus = parameter.getDocStatus();
			String orderType = parameter.getOrderType();
			String startDate = parameter.getStartDate();
			String endDate = parameter.getEndDate();
			mapIn.put("docId", docId);
			mapIn.put("baseEntry", baseEntry);
			mapIn.put("docStatus", docStatus);
			mapIn.put("orderType", orderType);
			mapIn.put("startDate", startDate);
			mapIn.put("endDate", endDate);
			BasePosObject pObjectResult = DeliveryWebWork.findDeliveryDoc(
					mapIn, dctx);
			if (UtilValidate.isEmpty(pObjectResult.getData())) {
				pObject.setFlag(Constant.OK);
				return pObject;
			}
		
		
			pObject.setFlag(Constant.OK);
			pObject.setData(pObjectResult.getData());
			return pObject;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pObject;
	}
	/*
	 * getSalesOutWhsOrder By Id
	 */

	@SuppressWarnings("unchecked")
	public static BasePosObject getSalesOutWhsOrderById(DispatchContext dctx,
			 Parameter parameter) {
		BasePosObject pObject = new BasePosObject();
		List<SalesOutWhsOrderDtl> salesoutOrderDtls = null;
		List<SalesOutWhsOrderHeader> salesoutOrderHeaders = null;
		try {
			//String docId = parameter.getDocId();
			//String where = "DOC_ID='" + docId + "'";
			BasePosObject pObjectHeader =getSalesOutWhsOrderHeaderByCondition(dctx, parameter);
			if (UtilValidate.isEmpty(pObjectHeader.getData())) {
				pObject.setFlag(Constant.OK);
				return pObject;
			}

			// 根据ID查询SalesOutWhsOrderDtls
			BasePosObject pObjectItem = getSalesOutWhsOrderDtlByCondition(dctx,
					parameter);
			if (UtilValidate.isEmpty(pObjectItem.getData())) {
				pObject.setFlag(Constant.OK);
					return pObject;
			}
			// 设置SaleOrder的头和行
			salesoutOrderHeaders = (List<SalesOutWhsOrderHeader>) pObjectHeader.getData();
			salesoutOrderDtls = (List<SalesOutWhsOrderDtl>) pObjectItem.getData();
			SaleOutWhsOrder saleordes= new SaleOutWhsOrder();
			saleordes.setHeader(salesoutOrderHeaders);
			saleordes.setItem(salesoutOrderDtls);
			pObject.setFlag(Constant.OK);
			pObject.setData(saleordes);
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}	

	/**
	 * 根据条件查询SalesOutWhsOrder
	 */
	@SuppressWarnings("unchecked")
	public static BasePosObject getSalesOutWhsOrderByCondition(DispatchContext dctx,
			 Parameter parameter) {
		BasePosObject pObject = new BasePosObject();
		List<SalesOutWhsOrderDtl> salesoutOrderDtls = null;
		List<SalesOutWhsOrderHeader> salesoutOrderHeaders = null;
		try {
			BasePosObject pObjectHeader =getSalesOutWhsOrderHeaderByCondition(dctx, parameter);
			if (UtilValidate.isEmpty(pObjectHeader.getData())) {
				pObject.setFlag(Constant.OK);
				return pObject;
			}

			// 根据ID查询SalesOutWhsOrderDtls
			BasePosObject pObjectItem = getSalesOutWhsOrderDtlByCondition(dctx,
					parameter);
			if (UtilValidate.isEmpty(pObjectItem.getData())) {
				pObject.setFlag(Constant.OK);
					return pObject;
			}
			salesoutOrderHeaders = (List<SalesOutWhsOrderHeader>) pObjectHeader.getData();
			salesoutOrderDtls = (List<SalesOutWhsOrderDtl>) pObjectItem.getData();
			SaleOutWhsOrder saleordes= new SaleOutWhsOrder();
			saleordes.setHeader(salesoutOrderHeaders);
			saleordes.setItem(salesoutOrderDtls);
			pObject.setFlag(Constant.OK);
			pObject.setData(saleordes);
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}

	/*
	 * 根基条件查询销售出库单头
	 */
	@SuppressWarnings("unchecked")
	public static BasePosObject getSalesOutWhsOrderDtlByCondition(
			DispatchContext dctx, Parameter parameter) {
		BasePosObject pObject = new BasePosObject();
		Delegator delegator = dctx.getDelegator();
		try {
			Map<String, Object> mapIn = FastMap.newInstance();
			String docId = parameter.getDocId();
			String baseEntry = parameter.getBaseEntry();
			String docStatus = parameter.getDocStatus();
			String orderType = parameter.getOrderType();
			String startDate = parameter.getStartDate();
			String endDate = parameter.getEndDate();
			mapIn.put("docId", docId);
			mapIn.put("baseEntry", baseEntry);
			mapIn.put("docStatus", docStatus);
			mapIn.put("orderType", orderType);
			mapIn.put("startDate", startDate);
			mapIn.put("endDate", endDate);
			BasePosObject pObjectResult = DeliveryWebWork.findDeliveryItem(
					mapIn, dctx);
			if (UtilValidate.isEmpty(pObjectResult.getData())) {
				pObject.setFlag(Constant.OK);
				return pObject;
			}
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> result = (List<Map<String, Object>>) pObjectResult
					.getData();
			for (int i = 0; i < result.size(); i++) {
				Map<String, Object> map = result.get(i);
				Map<String, Object> maps = new HashMap<String, Object>();
				maps.putAll(map);
				list.add(maps);
			}
			List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = list.get(i);
				String basEntry = map.get("baseEntry").toString();
				Long line = (Long) map.get("baseLineNo");
				GenericValue SaleQuantity = delegator.findByPrimaryKey(
						"SaleOrderDtl",
						UtilMisc.toMap("docId", basEntry, "lineNo", line));
				map.put("saleQuantity", SaleQuantity.get("quantity"));
				lists.add(map);
			}
			pObject.setFlag(Constant.OK);
			pObject.setData(lists);
			return pObject;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pObject;
	}

	// 根据参数得到SalesOutWhsOrderheader
	public static SalesOutWhsOrderHeader getSalesOutWhsOrderHeader(
			GenericValue salesOutWhsOrderHeader) {
		SalesOutWhsOrderHeader SsalesOutWhsOrderHeader = null;
		try {
			SsalesOutWhsOrderHeader = new SalesOutWhsOrderHeader();
			SsalesOutWhsOrderHeader.setDocId((String) salesOutWhsOrderHeader
					.get("docId"));
			SsalesOutWhsOrderHeader
					.setExtSystemNo((String) salesOutWhsOrderHeader
							.get("extSystemNo"));
			SsalesOutWhsOrderHeader.setExtDocNo((String) salesOutWhsOrderHeader
					.get("extDocNo"));
			SsalesOutWhsOrderHeader.setPartyId((String) salesOutWhsOrderHeader
					.get("partyId"));
			SsalesOutWhsOrderHeader
					.setPartyIdTo((String) salesOutWhsOrderHeader
							.get("partyIdTo"));
			SsalesOutWhsOrderHeader.setPrintNum((Long) salesOutWhsOrderHeader
					.get("printNum"));
			SsalesOutWhsOrderHeader
					.setDocDate((Timestamp) salesOutWhsOrderHeader
							.get("docDate"));
			SsalesOutWhsOrderHeader
					.setPostingDate((Timestamp) salesOutWhsOrderHeader
							.get("postingDate"));
			SsalesOutWhsOrderHeader
					.setDocStatus((String) salesOutWhsOrderHeader
							.get("docStatus"));
			SsalesOutWhsOrderHeader.setMemo((String) salesOutWhsOrderHeader
					.get("memo"));
			SsalesOutWhsOrderHeader
					.setUpdateDate((Timestamp) salesOutWhsOrderHeader
							.get("updateDate"));
			SsalesOutWhsOrderHeader.setStatus((String) salesOutWhsOrderHeader
					.get("status"));
			SsalesOutWhsOrderHeader.setIsSync((String) salesOutWhsOrderHeader
					.get("isSync"));
			SsalesOutWhsOrderHeader
					.setBaseEntry((String) salesOutWhsOrderHeader
							.get("baseEntry"));
			SsalesOutWhsOrderHeader
					.setMovementTypeId((String) salesOutWhsOrderHeader
							.get("movementTypeId"));
			SsalesOutWhsOrderHeader.setUserId((String) salesOutWhsOrderHeader
					.get("UserId"));
			SsalesOutWhsOrderHeader
					.setUpdateUserId((String) salesOutWhsOrderHeader
							.get("updateUserId"));
			SsalesOutWhsOrderHeader
					.setProductStoreId((String) salesOutWhsOrderHeader
							.get("productStoreId"));
			SsalesOutWhsOrderHeader
					.setProductStoreIdTo((String) salesOutWhsOrderHeader
							.get("productStoreIdTo"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return SsalesOutWhsOrderHeader;
	}

	// 根据参数得到SalesoutwhsOrderDtl salesouwhsOrderDtl
	public static SalesOutWhsOrderDtl getSalesOutWhsOrderDtl(
			GenericValue salesOutWhsOrderDtl) {
		SalesOutWhsOrderDtl SsalesOutWhsOrderDtl = null;
		try {
			SsalesOutWhsOrderDtl = new SalesOutWhsOrderDtl();
			SsalesOutWhsOrderDtl.setDocId((String) salesOutWhsOrderDtl
					.get("docId"));
			SsalesOutWhsOrderDtl.setBaseEntry((String) salesOutWhsOrderDtl
					.get("baseEntry"));
			SsalesOutWhsOrderDtl.setLineNo((Long) salesOutWhsOrderDtl
					.get("lineNo"));
			SsalesOutWhsOrderDtl.setMemo((String) salesOutWhsOrderDtl
					.get("memo"));
			SsalesOutWhsOrderDtl.setIdValue((String) salesOutWhsOrderDtl
					.get("idValue"));
			SsalesOutWhsOrderDtl.setIsSequence((String) salesOutWhsOrderDtl
					.get("isSequence"));
			SsalesOutWhsOrderDtl.setQuantity((Long) salesOutWhsOrderDtl
					.get("quantity"));
			SsalesOutWhsOrderDtl.setProductId((String) salesOutWhsOrderDtl
					.get("productId"));
			SsalesOutWhsOrderDtl.setSequenceId((String) salesOutWhsOrderDtl
					.get("sequenceId"));
			SsalesOutWhsOrderDtl.setProductName((String) salesOutWhsOrderDtl.get("productName"));
			SsalesOutWhsOrderDtl.setSaleQuantity((Long)salesOutWhsOrderDtl.get("saleQuantity"));
			SsalesOutWhsOrderDtl.setFacilityId((String) salesOutWhsOrderDtl
					.get("facilityId"));
			SsalesOutWhsOrderDtl.setFacilityIdTo((String) salesOutWhsOrderDtl
					.get("facilityIdTo"));
			SsalesOutWhsOrderDtl.setBaseLineNo((Long) salesOutWhsOrderDtl
					.get("baseLineNo"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SsalesOutWhsOrderDtl;
	}

	// 根据参数的到GenericValue SalesOutWhsprderheader,放入map
	public static GenericValue getGVSalesOutWhsOrderHeader(
			DispatchContext dctx, SalesOutWhsOrderHeader Sooh) {
		GenericValue Ssooh = null;
		Map<String, Object> SalesOutWHsOrderHeaderMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		try {
			SalesOutWHsOrderHeaderMap.put("docId", Sooh.getDocId());
			SalesOutWHsOrderHeaderMap.put("extSystemNo", Sooh.getExtSystemNo());
			SalesOutWHsOrderHeaderMap.put("partyId", Sooh.getPartyId());
			SalesOutWHsOrderHeaderMap.put("extDocNo", Sooh.getExtDocNo());
			SalesOutWHsOrderHeaderMap.put("docDate", Sooh.getDocDate());
			SalesOutWHsOrderHeaderMap.put("postingDate", Sooh.getPostingDate());
			SalesOutWHsOrderHeaderMap.put("docStatus", Sooh.getDocStatus());
			SalesOutWHsOrderHeaderMap.put("printNum", Sooh.getPrintNum());
			SalesOutWHsOrderHeaderMap.put("memo", Sooh.getMemo());
			SalesOutWHsOrderHeaderMap.put("updateDate", Sooh.getUpdateDate());
			SalesOutWHsOrderHeaderMap.put("status", Sooh.getStatus());
			SalesOutWHsOrderHeaderMap.put("isSync", Sooh.getIsSync());
			SalesOutWHsOrderHeaderMap.put("baseEntry", Sooh.getBaseEntry());
			SalesOutWHsOrderHeaderMap.put("movementTypeId",
					Sooh.getMovementTypeId());
			SalesOutWHsOrderHeaderMap.put("userId", Sooh.getUserId());
			SalesOutWHsOrderHeaderMap.put("updateUserId",
					Sooh.getUpdateUserId());
			SalesOutWHsOrderHeaderMap.put("productStoreId",
					Sooh.getProductStoreId());
			SalesOutWHsOrderHeaderMap.put("productStoreIdTo",
					Sooh.getProductStoreIdTo());
			SalesOutWHsOrderHeaderMap.put("partyIdTo", Sooh.getPartyIdTo());
			Ssooh = delegator.makeValue("SalesOutWhsOrderHeader",
					SalesOutWHsOrderHeaderMap);
			Ssooh.create();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Ssooh;
	}

	// 根据参数得到 GenericValue salesOutWhsOrderDtl 放入map
	public static GenericValue getGVSalesOutWhsOrderDtl(DispatchContext dctx,
			SalesOutWhsOrderDtl Sood) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> SalesOutWhsOrderDtlMap = FastMap.newInstance();
		GenericValue Ssood = null;
		try {
			SalesOutWhsOrderDtlMap.put("docId", Sood.getDocId());
			SalesOutWhsOrderDtlMap.put("baseEntry", Sood.getBaseEntry());
			SalesOutWhsOrderDtlMap.put("lineNo", Sood.getLineNo());
			SalesOutWhsOrderDtlMap.put("memo", Sood.getMemo());
			SalesOutWhsOrderDtlMap.put("isSequence", Sood.getIsSequence());
			SalesOutWhsOrderDtlMap.put("productName", Sood.getProductName());
			SalesOutWhsOrderDtlMap.put("saleQuantity", Sood.getSaleQuantity());
			SalesOutWhsOrderDtlMap.put("idValue", Sood.getIdValue());
			SalesOutWhsOrderDtlMap.put("quantity", Sood.getQuantity());
			SalesOutWhsOrderDtlMap.put("productId", Sood.getProductId());
			SalesOutWhsOrderDtlMap.put("sequenceId", Sood.getSequenceId());
			SalesOutWhsOrderDtlMap.put("facilityId", Sood.getFacilityId());
			SalesOutWhsOrderDtlMap.put("facilityIdTo", Sood.getFacilityIdTo());
			SalesOutWhsOrderDtlMap.put("baseLineNo", Sood.getBaseLineNo());
			Ssood = delegator.makeValue("SalesOutWhsOrderDtl",
					SalesOutWhsOrderDtlMap);
			Ssood.create();

		} catch (Exception e) {
			e.printStackTrace();

		}
		return Ssood;
	}

	// 写入库存所需要的item 根据参数得到 GenericValue salesOutWhsOrderDtl 放入map
	public static Map<String, Object> getGVSalesOutWhsOrderDtls(
			DispatchContext dctx, SalesOutWhsOrderDtl Soods) {
		Map<String, Object> SalesOutWhsOrderDtlMap = FastMap.newInstance();
		try {
			SalesOutWhsOrderDtlMap.put("docId", Soods.getDocId());
			SalesOutWhsOrderDtlMap.put("baseEntry", Soods.getBaseEntry());
			SalesOutWhsOrderDtlMap.put("lineNo", Soods.getLineNo());
			SalesOutWhsOrderDtlMap.put("memo", Soods.getMemo());
			SalesOutWhsOrderDtlMap.put("isSequence", Soods.getIsSequence());
			SalesOutWhsOrderDtlMap.put("idValue", Soods.getIdValue());
			SalesOutWhsOrderDtlMap.put("productId", Soods.getProductId());
			SalesOutWhsOrderDtlMap.put("sequenceId", Soods.getSequenceId());
			SalesOutWhsOrderDtlMap.put("facilityId", Soods.getFacilityId());
			SalesOutWhsOrderDtlMap.put("facilityIdTo", Soods.getFacilityIdTo());
			SalesOutWhsOrderDtlMap.put("quantity", Soods.getQuantity());
			SalesOutWhsOrderDtlMap.put("baseLineNo", Soods.getBaseLineNo());
			// SSoods.create();

		} catch (Exception e) {
			e.printStackTrace();

		}
		return SalesOutWhsOrderDtlMap;
	}

	// 写入库存header
	public static Map<String, Object> getGVSalesOutWhsOrderHeaders(
			DispatchContext dctx, SalesOutWhsOrderHeader Soohs) {

		Map<String, Object> SalesOutWHsOrderHeaderMap = FastMap.newInstance();
		try {
			SalesOutWHsOrderHeaderMap.put("docId", Soohs.getDocId());
			SalesOutWHsOrderHeaderMap
					.put("extSystemNo", Soohs.getExtSystemNo());
			SalesOutWHsOrderHeaderMap.put("partyId", Soohs.getPartyId());
			SalesOutWHsOrderHeaderMap.put("extDocNo", Soohs.getExtDocNo());
			SalesOutWHsOrderHeaderMap.put("docDate", Soohs.getDocDate());
			SalesOutWHsOrderHeaderMap
					.put("postingDate", Soohs.getPostingDate());
			SalesOutWHsOrderHeaderMap.put("docStatus", Soohs.getDocStatus());
			SalesOutWHsOrderHeaderMap.put("printNum", Soohs.getPrintNum());
			SalesOutWHsOrderHeaderMap.put("memo", Soohs.getMemo());
			SalesOutWHsOrderHeaderMap.put("updateDate", Soohs.getUpdateDate());
			SalesOutWHsOrderHeaderMap.put("status", Soohs.getStatus());
			if(!Soohs.getDocStatus().equals(Constant.DRAFT)){
				SalesOutWHsOrderHeaderMap.put("isSync",Constant.ONE);
				}else{
				SalesOutWHsOrderHeaderMap.put("isSync", Soohs.getIsSync());
				}
		
			SalesOutWHsOrderHeaderMap.put("baseEntry", Soohs.getBaseEntry());
			SalesOutWHsOrderHeaderMap.put("movementTypeId",
					Soohs.getMovementTypeId());
			SalesOutWHsOrderHeaderMap.put("userId", Soohs.getUserId());
			SalesOutWHsOrderHeaderMap.put("updateUserId",
					Soohs.getUpdateUserId());
			SalesOutWHsOrderHeaderMap.put("productStoreId",
					Soohs.getProductStoreId());
			SalesOutWHsOrderHeaderMap.put("productStoreIdTo",
					Soohs.getProductStoreIdTo());
			SalesOutWHsOrderHeaderMap.put("partyIdTo", Soohs.getPartyIdTo());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SalesOutWHsOrderHeaderMap;
	}

	// 更新销售出库订单头(set)
	public static GenericValue updateSalesOutWhsOrderHeader(
			GenericValue salesOutWhsOrderHeader, SalesOutWhsOrderHeader Sooh) {
		try {
			salesOutWhsOrderHeader.set("docId", Sooh.getDocId());
			salesOutWhsOrderHeader.set("extSystemNo", Sooh.getExtSystemNo());
			salesOutWhsOrderHeader.set("extDocNo", Sooh.getExtDocNo());
			salesOutWhsOrderHeader.set("partyId", Sooh.getPartyId());
			salesOutWhsOrderHeader.set("printNum", Sooh.getPrintNum());

			salesOutWhsOrderHeader.set("partyIdTo", Sooh.getPartyIdTo());

			salesOutWhsOrderHeader.set("docDate", Sooh.getDocDate());
			salesOutWhsOrderHeader.set("postingDate", Sooh.getPostingDate());
			salesOutWhsOrderHeader.set("docStatus", Sooh.getDocStatus());
			salesOutWhsOrderHeader.set("memo", Sooh.getMemo());
			salesOutWhsOrderHeader.set("updateDate", Sooh.getUpdateDate());
			salesOutWhsOrderHeader.set("status", Sooh.getStatus());
			salesOutWhsOrderHeader.set("isSync", Sooh.getIsSync());
			salesOutWhsOrderHeader.set("baseEntry", Sooh.getBaseEntry());
			salesOutWhsOrderHeader.set("movementTypeId",
					Sooh.getMovementTypeId());
			salesOutWhsOrderHeader.set("userId", Sooh.getUserId());
			salesOutWhsOrderHeader.set("updateUserId", Sooh.getUpdateUserId());
			salesOutWhsOrderHeader.set("productStoreId",
					Sooh.getProductStoreId());
			salesOutWhsOrderHeader.set("productStoreIdTo",
					Sooh.getProductStoreIdTo());

			salesOutWhsOrderHeader.store();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return salesOutWhsOrderHeader;
	}

	// 解锁库存所需要的item 根据参数得到 GenericValue salesOutWhsOrderDtl 放入map
	public static Map<String, Object> getSalesOutWhsOrderDtls(
			DispatchContext dctx, SalesOutWhsOrderDtl Soods) {
		Map<String, Object> SalesOutWhsOrderDtlMap = FastMap.newInstance();
		try {
			SalesOutWhsOrderDtlMap.put("docId", Soods.getBaseEntry());
			SalesOutWhsOrderDtlMap.put("baseEntry", Soods.getBaseEntry());
			SalesOutWhsOrderDtlMap.put("lineNo", Soods.getLineNo());
			SalesOutWhsOrderDtlMap.put("memo", Soods.getMemo());
			SalesOutWhsOrderDtlMap.put("isSequence", Soods.getIsSequence());
			SalesOutWhsOrderDtlMap.put("idValue", Soods.getIdValue());
			SalesOutWhsOrderDtlMap.put("productId", Soods.getProductId());
			SalesOutWhsOrderDtlMap.put("sequenceId", Soods.getSequenceId());
			SalesOutWhsOrderDtlMap.put("facilityId", Soods.getFacilityId());
			SalesOutWhsOrderDtlMap.put("facilityIdTo", Soods.getFacilityIdTo());
			SalesOutWhsOrderDtlMap.put("quantity", Soods.getUnLockedQuantity());
			SalesOutWhsOrderDtlMap.put("baseLineNo", Soods.getBaseLineNo());
			// SSoods.create();

		} catch (Exception e) {
			e.printStackTrace();

		}
		return SalesOutWhsOrderDtlMap;
	}
}
