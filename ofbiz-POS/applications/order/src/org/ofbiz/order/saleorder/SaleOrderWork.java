package org.ofbiz.order.saleorder;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
import org.ofbiz.delivery.webService.DeliveryWebWork;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.order.collectionorder.CollectionOrder;
import org.ofbiz.order.collectionorder.CollectionOrderWork;
import org.ofbiz.order.parameter.Parameter;
import org.ofbiz.order.preorder.PreOrderWorker;
import org.ofbiz.order.returnorder.ReturnOrder;
import org.ofbiz.order.salesoutwhsorder.SalesOutWhsOrder;
import org.ofbiz.order.salesoutwhsorder.SalesOutWhsOrderWork;
import org.ofbiz.receive.webService.ReceiveWebWork;
import org.ofbiz.service.DispatchContext;

public class SaleOrderWork {
	/**
	 * Worker methods for SalerOrder Information DispatchContext用于取得对象
	 * 一个名叫context的影射Map包含了你的输入参数并返回一个结果映射。
	 * 
	 */
	public static final String module = SaleOrderWork.class.getName();

	private SaleOrderWork() {
	}

	/**
	 * 创建销售， 收款， 出库
	 */
	public static BasePosObject createSalesOrderAll(DispatchContext dctx,
			SaleOrder saleOrder, CollectionOrder collectionOrder,
			SalesOutWhsOrder salesOutWhsOrder) {
		BasePosObject pObject = new BasePosObject();
		BasePosObject pObjectResult = null;
		try {
			// 销售单不为空写入销售单
			if (UtilValidate.isNotEmpty(saleOrder)) {
				pObjectResult = createSalesOrder(dctx, saleOrder);
				if (pObjectResult.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectResult.getMsg());
					return pObject;
				}
			}
			// 收款单不为空写入收款单
			if (UtilValidate.isNotEmpty(collectionOrder)) {
				pObjectResult = CollectionOrderWork.createCollectionOrder(dctx,
						collectionOrder);
				if (pObjectResult.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectResult.getMsg());
					return pObject;
				}
			}
			// 退货单不为空写入退货单
			if (UtilValidate.isNotEmpty(salesOutWhsOrder)) {
				pObjectResult = SalesOutWhsOrderWork.createSalesOutWhsOrder(
						dctx, salesOutWhsOrder);
				if (pObjectResult.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectResult.getMsg());
					return pObject;
				}
			}
			if (UtilValidate.isEmpty(saleOrder)
					&& UtilValidate.isEmpty(collectionOrder)
					&& UtilValidate.isEmpty(salesOutWhsOrder)) {
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

	/**
	 * update SalesOrderALl
	 */
	public static BasePosObject updateSalesOrderAll(DispatchContext dctx,
			SaleOrder saleOrder, CollectionOrder collectionOrder,
			SalesOutWhsOrder salesOutWhsOrder) {
		BasePosObject pObject = new BasePosObject();
		BasePosObject pObjectResult = null;
		try {
			// 销售单不为空更新
			if (UtilValidate.isNotEmpty(saleOrder)) {
				pObjectResult = updateSalesOrder(dctx, saleOrder);
				if (pObjectResult.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectResult.getMsg());
					return pObject;
				}
			}
			// 收款单不为空写入收款单
			if (UtilValidate.isNotEmpty(collectionOrder)) {
				pObjectResult = CollectionOrderWork.createCollectionOrder(dctx,
						collectionOrder);
				if (pObjectResult.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectResult.getMsg());
					return pObject;
				}
			}
			// 退货单不为空写入退货单
			if (UtilValidate.isNotEmpty(salesOutWhsOrder)) {
				pObjectResult = SalesOutWhsOrderWork.createSalesOutWhsOrder(
						dctx, salesOutWhsOrder);
				if (pObjectResult.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg(pObjectResult.getMsg());
					return pObject;
				}
			}
			pObject.setFlag(Constant.OK);
			/*
			 * if(UtilValidate.isEmpty(saleOrder)&&UtilValidate.isEmpty(
			 * collectionOrder)&&UtilValidate.isEmpty(salesOutWhsOrder)){
			 * pObject.setFlag(Constant.NG);
			 * 
			 * } else {pObject.setFlag(Constant.OK);}
			 */
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}

	/**
	 * create SalesOrder
	 */
	public static BasePosObject createSalesOrder(DispatchContext dctx,
			SaleOrder saleorder) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> SyncDocMap = new HashMap<String, Object>();
		GenericValue syncDoc = null;// 其他门店同步
		BasePosObject pObject = new BasePosObject();
		Parameter parameter = null;
		int leng = saleorder.getDetail().size();
		int len = saleorder.getDetailPrmtn().size();
		int lens = saleorder.getPrmtn().size();
		int lent = saleorder.getPriceDtl().size();
		try {
			// 写入销售订单Header
			getGVSaleOrderHeader(dctx, saleorder.getHeader());
			for (int i = 0; i < leng; i++) {
				// 写入销售明细订单
				getGVSaleOrderDtl(dctx, saleorder.getDetail().get(i));
			}
			if (saleorder.getDetailPrmtn() != null)
				for (int i = 0; i < len; i++) {
					// 商品和促销关系表
					getGVSaleOrderDtlPrmtn(dctx, saleorder.getDetailPrmtn()
							.get(i));
				}
			if (saleorder.getPrmtn() != null)
				for (int i = 0; i < lens; i++) {
					// 写入促销信息表
					getGVSaleOrderPrmtn(dctx, saleorder.getPrmtn().get(i));
				}
			if (saleorder.getPriceDtl() != null)
				for (int i = 0; i < lent; i++) {
					// 写入销售订单商品销售价格明细
					getGVSaleOrderPriceDtl(dctx, saleorder.getPriceDtl().get(i));
				}
			// 判断门店，收款店，出库店的关系
			String StoreId = saleorder.getHeader().getStoreId();
			String CollectionStoreId = saleorder.getHeader()
					.getCollectionStoreId();
			String DeliveryStoreId = saleorder.getHeader().getDeliveryStoreId();
			// 如果收款门店ID ！=门店ID
			if (UtilValidate.isNotEmpty(CollectionStoreId)
					&& !CollectionStoreId.equals(StoreId)) {
				// SyncDoc表中插入销售订单storeId=collectionStoreId
				SyncDocMap.put("docId", saleorder.getHeader().getDocId());
				SyncDocMap.put("docType", Constant.SALES);
				SyncDocMap.put("storeId", CollectionStoreId);
				syncDoc = delegator.makeValue("SyncDoc", SyncDocMap);
				syncDoc.create();
			} // 如果出库门店ID ！=门店ID 或出库门店！=收款门店
			if (UtilValidate.isNotEmpty(DeliveryStoreId)
					&& !DeliveryStoreId.equals(StoreId)
					&& !DeliveryStoreId.equals(CollectionStoreId)) {
				SyncDocMap.put("docType", Constant.SALES);
				SyncDocMap.put("storeId", DeliveryStoreId);
				SyncDocMap.put("docId", saleorder.getHeader().getDocId());
				syncDoc = delegator.makeValue("SyncDoc", SyncDocMap);
				syncDoc.create();
			}
			// 如果销售订单头BaseEntry不为空,调用更新预订单状态方法(更新销售状态为已清)
			if (UtilValidate.isNotEmpty(saleorder.getHeader().getBaseEntry())) {
				parameter = new Parameter();
				parameter.setDocId(saleorder.getHeader().getBaseEntry());
				parameter.setSalesStatus(Constant.CLEAR);
				parameter.setLastUpdateUserId(saleorder.getHeader()
						.getLastUpdateUserId());
				parameter.setLastUpdateDocDate(saleorder.getHeader()
						.getLastUpdateDocDate());
				pObject = PreOrderWorker.updatePreOrderStatus(dctx, parameter);
				if (pObject.getFlag().equals(Constant.NG)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg("预订单状态为已清或作废");
					return pObject;
				}
			} // 判断是否锁库存
			String docStatus = saleorder.getHeader().getDocStatus();
			List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
			// 如果不是草稿状态锁库存
			if (!docStatus.equals(Constant.DRAFT)) {
				// 如果lockedquantity数量为0，不锁库存
				Map<String, Object> mapHeader = getSaleOrderHeaderMap(dctx,
						saleorder.getHeader());
				int lenth = saleorder.getDetail().size();
				for (int i = 0; i < lenth; i++) {
					Long lockquantity = saleorder.getDetail().get(i)
							.getLockedQuantity();
					if (UtilValidate.isNotEmpty(lockquantity)
							&& lockquantity != 0) {
						// 获取出库明细，把锁库数量lockedquantity 赋值给quantity
						Map<String, Object> saleorderdtl = getSaleOrderDtlMap(
								dctx, saleorder.getDetail().get(i));
						listItem.add(saleorderdtl);
					}
				}
				if (listItem.size() > 0) {
					BasePosObject commdelivery = DeliveryWebWork
							.commadDelivery(mapHeader, listItem, dctx);// commad
																		// 中转库
					if (commdelivery.getFlag().equals(Constant.NG)) {
						pObject.setFlag(Constant.NG);
						pObject.setMsg(commdelivery.getMsg());
						return pObject;
					}
				}
			} // deliveryWork 441 没有仓库id 为什么状态干掉变成N

			pObject.setFlag(Constant.OK);
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}

	/**
	 * updateSalesOrerStatus
	 */
	public static BasePosObject updateSalesOrderStatus(DispatchContext dctx,
			Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		GenericValue saleOrderHeader = null;// 数据库中的数值对象 销售表头
		BasePosObject pObject = new BasePosObject();
		List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
		String docStatus = null;
		String fundStatus = null;
		String warehouseStatus = null;
		String returnStatus = null;
		// 根据主键docId 查询SaleOrderHeader的非空的且不是部分已清的Status状态
		try {
			if (UtilValidate.isNotEmpty(view)) {
				String docId = (String) view.get("docId");
				if(UtilValidate.isNotEmpty(view.get("docStatus"))){
					 docStatus = view.get("docStatus").equals(null) ? null : (String) view.get("docStatus"); // 销售订单状态
				}
                if(UtilValidate.isNotEmpty(view.get("fundStatus"))){
                	 fundStatus =  view.get("fundStatus").equals(null) ? null : (String) view.get("fundStatus") ; // 收款状态
				}
                if(UtilValidate.isNotEmpty(view.get("warehouseStatus"))){
                	 warehouseStatus =  view.get("warehouseStatus").equals(null) ? null :(String) view.get("warehouseStatus") ;// 出库状态
				}
                if(UtilValidate.isNotEmpty(view.get("returnStatus"))){
                	returnStatus =  view.get("returnStatus").equals(null)? null : (String) view.get("returnStatus");// 退货状态
				}
				String lastUpdateUserId = (String) view.get("lastUpdateUserId");// 更新用户 
				//Timestamp lastUpdateDocDate = (Timestamp) view.get("lastUpdateDocDate");
				// 如果所有的状态都一样返回错误心
				saleOrderHeader = delegator.findByPrimaryKey("SaleOrderHeader",
						UtilMisc.toMap("docId", docId));
				String SdocStatus = (String) saleOrderHeader.get("docStatus");
				String SfundStatus = (String) saleOrderHeader.get("fundStatus");
				String SwarehouseStatus = (String) saleOrderHeader.get("warehouseStatus");
				String SreturnStatus = (String) saleOrderHeader.get("returnStatus");
				if (SdocStatus.equals(Constant.CLEARED)
						|| SdocStatus.equals(Constant.INVALID)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg("订单状态为已清或作废时不能再改变其状态");
					return pObject;
				}
				if (UtilValidate.isNotEmpty(SfundStatus)
						&& !SfundStatus.equals(Constant.PARTCLEARED)
						&& UtilValidate.isNotEmpty(SwarehouseStatus)
						&& !SwarehouseStatus.equals(Constant.PARTCLEARED)
						&& UtilValidate.isNotEmpty(SreturnStatus)
						&& !SreturnStatus.equals(Constant.PARTCLEARED)) {
					if (SfundStatus.equals(fundStatus)
							&& SwarehouseStatus.equals(warehouseStatus)
							&& SreturnStatus.equals(returnStatus)) {
						pObject.setFlag(Constant.NG);
						pObject.setMsg("查询Status与目前Status一致");
						return pObject;
					}
				}

				if (UtilValidate.isNotEmpty(docStatus)) {
					// 更新docStatus
					saleOrderHeader.set("docStatus", docStatus);
				}
				if ( UtilValidate.isNotEmpty(fundStatus)) {
					// 更新fundStatus
					saleOrderHeader.set("fundStatus", fundStatus);
				}

				if ( UtilValidate.isNotEmpty(warehouseStatus)) {
					// 更新warehouseStatus
					saleOrderHeader.set("warehouseStatus", warehouseStatus);
				}
				if ( UtilValidate.isNotEmpty(returnStatus)) {
					// 更新returnStatus
					saleOrderHeader.set("returnStatus", returnStatus);
				}
				saleOrderHeader.store();
				// 更新修改时间
				saleOrderHeader.set("lastUpdateDocDate",
						UtilDateTime.nowTimestamp());
				saleOrderHeader.set("lastUpdateUserId", lastUpdateUserId);
				saleOrderHeader.set("isSync", Constant.ONE);
				saleOrderHeader.store();
				GenericValue header = delegator.findByPrimaryKey(
						"SaleOrderHeader", UtilMisc.toMap("docId", docId));
				String sdocStatus = (String) header.get("docStatus");
				String sfundStatus = (String) header.get("fundStatus");
				// String swarehouseStatus =
				// (String)header.get("warehouseStatus");
				String sreturnStatus = (String) header.get("returnStatus");
				if (sfundStatus.equals(Constant.CLEAR)
				// && swarehouseStatus.equals(
				// Constant.CLEAR)
						&& sreturnStatus.equals(Constant.CLEAR)) {
					saleOrderHeader.set("docStatus", Constant.CLEARED);
					saleOrderHeader.store();
				}
				// 如果更新的状态为作废时，解锁库存
				if (sdocStatus.equals(Constant.INVALID)) {
					// 获取解锁参数Header
					Map<String, Object> mapHeader = getHeaderMap(dctx,
							saleOrderHeader);
					// 获取解锁参数Item
					String where = "DOC_ID='" + docId + "'";
					EntityCondition mainCond = EntityCondition
							.makeConditionWhere(where);
					List<GenericValue> SaleDtl = delegator.findList(
							"SaleOrderDtl", mainCond, null,
							UtilMisc.toList("docId"), null, false);
					for (GenericValue saleOrderDtl : SaleDtl) {
						SaleOrderDtl OrderDtl = getSaleOrderDtl(saleOrderDtl);
						Map<String, Object> saleorderdtl = getSaleOrderDtlMap(
								dctx, OrderDtl);
						listItem.add(saleorderdtl);
					}
					BasePosObject SaleReceive = ReceiveWebWork.commadReceive(
							mapHeader, listItem, dctx);
					if (SaleReceive.getFlag().equals(Constant.NG)) {
						pObject.setFlag(Constant.NG);
						pObject.setMsg(SaleReceive.getMsg());
						return pObject;
					}
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

	/**
	 * update SaleOrder
	 */
	public static BasePosObject updateSalesOrder(DispatchContext dctx,
			SaleOrder saleorder) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		GenericValue saleOrderHeader = null;
		List<GenericValue> saleOrderDtls = null;
		List<GenericValue> saleOrderPriceDtls = null;
		List<Map<String, Object>> listItem = new ArrayList<Map<String, Object>>();
		try {
			String docId = saleorder.getHeader().getDocId();
			if (UtilValidate.isEmpty(docId)) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg("销售单ID错误");
				return pObject;
			}
			/*
			 * // 如果出库门店！=本门店，解锁原有的订单 GenericValue
			 * header=delegator.findByPrimaryKey("SaleOrderHeader",
			 * UtilMisc.toMap("docId", docId)); SaleOrderHeader saleheader
			 * =getSaleOrderHeader(header); String deliveryId =
			 * saleheader.getDeliveryStoreId(); String
			 * storeid=saleheader.getStoreId();
			 * if(UtilValidate.isNotEmpty(deliveryId
			 * )&&!deliveryId.equals(storeid)){ Map<String, Object> mapHeader =
			 * getHeaderMap(dctx,header); // 获取解锁参数Item String where =
			 * "DOC_ID='" + docId + "'"; EntityCondition mainCond =
			 * EntityCondition .makeConditionWhere(where); List<GenericValue>
			 * SaleDtl = delegator.findList( "SaleOrderDtl", mainCond, null,
			 * UtilMisc.toList("docId"), null, false); for (GenericValue gv :
			 * SaleDtl) { SaleOrderDtl OrderDtl = getSaleOrderDtl(gv);
			 * Map<String, Object> saleorderdtl = getSaleOrderDtlMap( dctx,
			 * OrderDtl); listItem.add(saleorderdtl); }//调方法解锁 BasePosObject
			 * SaleReceive = ReceiveWebWork.commadReceive( mapHeader, listItem,
			 * dctx); if (SaleReceive.getFlag().equals(Constant.NG)) {
			 * pObject.setFlag(Constant.NG);
			 * pObject.setMsg(SaleReceive.getMsg()); return pObject; } }
			 */
			// 根据ID 查询 销售单头
			saleOrderHeader = delegator.findByPrimaryKey("SaleOrderHeader",
					UtilMisc.toMap("docId", docId));
			boolean valid = true;
			// 查询订单状态是否为草稿，草稿就更新
			String docStatus = (String) saleOrderHeader.get("docStatus");
			if (UtilValidate.isEmpty(docStatus)
					|| !docStatus.equals(Constant.DRAFT)) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg("此订单不是草稿");
				return pObject;
			}// 判断是不是确定状态是确定锁库存
			String docStatuss = (String) saleorder.getHeader().getDocStatus();
			if (!docStatuss.equals(Constant.VALID)) {
				valid = false;
			}
			// 更新销售单头
			updateGVSaleOrderHeader(saleOrderHeader, saleorder.getHeader());
			// 根据id查询订单明细
			saleOrderDtls = delegator.findByAnd("SaleOrderDtl",
					UtilMisc.toMap("docId", docId));
			saleOrderPriceDtls = delegator.findByAnd("SalesOrderPriceDtl",
					UtilMisc.toMap("docId", docId));
			// 删除原有明细
			delegator.removeAll(saleOrderDtls);
			// 写入明细
			int lengs = saleorder.getDetail().size();
			for (int i = 0; i < lengs; i++) {
				getGVSaleOrderDtl(dctx, saleorder.getDetail().get(i));
			}
			delegator.removeAll(saleOrderPriceDtls);
			for (int i = 0; i < saleorder.getPriceDtl().size(); i++) {
				getGVSaleOrderPriceDtl(dctx, saleorder.getPriceDtl().get(i));
			}
			if (valid) {
				String docStatusS = saleorder.getHeader().getDocStatus();
				String deliverystoreId = saleorder.getHeader()
						.getDeliveryStoreId();
				String storeId = saleorder.getHeader().getStoreId();
				if (UtilValidate.isNotEmpty(deliverystoreId)
						&& deliverystoreId.equals(storeId)
						&& !docStatusS.equals(Constant.DRAFT)) {
					// 锁库存
					Map<String, Object> mapHeader = getSaleOrderHeaderMap(dctx,
							saleorder.getHeader());
					int lenth = saleorder.getDetail().size();
					for (int i = 0; i < lenth; i++) {
						Long lockquantity = saleorder.getDetail().get(i).getLockedQuantity();
						if (UtilValidate.isNotEmpty(lockquantity) && lockquantity != 0) {
							// 获取出库明细
							Map<String, Object> saleorderdtl = getSaleOrderDtlMap(
									dctx, saleorder.getDetail().get(i));
							listItem.add(saleorderdtl);
						}
						
					}
					// 调方法锁库存
					if (listItem.size() > 0) {
						BasePosObject commDelivery = DeliveryWebWork
								.commadDelivery(mapHeader, listItem, dctx);// commad
																			// 中转库
						if (commDelivery.getFlag().equals(Constant.NG)) {
							pObject.setFlag(Constant.NG);
							pObject.setMsg(commDelivery.getMsg());
							return pObject;
						}
					}
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

	/**
	 * getSalesOrderById
	 * 
	 * @param dctx
	 * @param view
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static BasePosObject getSalesOrderById(DispatchContext dctx,
			Map<String, Object> view) {
		BasePosObject pObject = new BasePosObject();
		List<GenericValue> values = null;
		SaleOrders saleOrder = new SaleOrders();
		try {
			// 根据ID查询SaleOrderHeader数值对象(map形式)
			String docId = (String) view.get("docId");
			String where = "DOC_ID='" + docId + "'";
			Map<String, Object> viewMap = FastMap.newInstance();
			viewMap.put("where", where);
			BasePosObject pObjectHeader = getSalesOrderHeaderByCondition(dctx,
					viewMap);
			values = (List<GenericValue>) pObjectHeader.getData();
			if (UtilValidate.isNotEmpty(values)) {
				saleOrder.setHeader(values.get(0));
			}
			// 根据ID查询SaleOrderDtls
			BasePosObject pObjectDetail = getSalesOrderDtlByCondition(dctx,
					viewMap);
			values = (List<GenericValue>) pObjectDetail.getData();
			if (UtilValidate.isNotEmpty(values)) {
				saleOrder.setDetail(values);
			}
			pObject.setData(saleOrder);
			pObject.setFlag(Constant.OK);
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}

	/**
	 * 根据docId删除SaleOrder
	 */

	public static BasePosObject deleteSalesOrderById(DispatchContext dctx,
			Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		GenericValue saleOrderHeader = null;
		List<GenericValue> saleOrderDtls = null;
		try {
			String docId = (String) view.get("docId");
			if (UtilValidate.isEmpty(docId)) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg("ID错误");
				return pObject;
			}
			// 根据ID查询SaleOrderHeader数值对象(map形式)
			saleOrderHeader = delegator.findByPrimaryKey("SaleOrderHeader",
					UtilMisc.toMap("docId", docId));
			// 查询订单状态是否为草稿，是 删除预订单
			String docStatus = (String) saleOrderHeader.get("docStatus");
			if (docStatus.equals("") || !docStatus.equals(Constant.DRAFT)) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg("订单状态不是草稿");
				return pObject;
			}
			if (docStatus.equals(Constant.DRAFT)) {
				// 删除原有头
				delegator.removeValue(saleOrderHeader);
				// 根据ID查询List<GenericValue> PreOrderDtls
				saleOrderDtls = delegator.findByAnd("SaleOrderDtl",
						UtilMisc.toMap("docId", docId));
				// 删除原有明细
				delegator.removeAll(saleOrderDtls);
			}
			pObject.setFlag(Constant.OK);
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}

		return pObject;

	}

	// 根据ID查询SaleOrderHeader
	@SuppressWarnings("unchecked")
	public static BasePosObject getSalesOrderHeaderById(DispatchContext dctx,
			Map<String, Object> view) {
		BasePosObject pObject = new BasePosObject();
		List<GenericValue> saleOrderHeaders = null;
		BasePosObject result = null;
		try {
			String docId = (String) view.get("docId");
			String where = "DOC_ID='" + docId + "'";
			Map<String, Object> viewMap = FastMap.newInstance();
			viewMap.put("where", where);
			result = getSalesOrderHeaderByCondition(dctx, viewMap);
			saleOrderHeaders = (List<GenericValue>) result.getData();
			pObject.setFlag(Constant.OK);
			if (UtilValidate.isNotEmpty(saleOrderHeaders)) {
				pObject.setData(saleOrderHeaders.get(0));
			}
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}

	/**
	 * 根据条件查询SaleOrderHeader
	 */

	public static BasePosObject getSalesOrderHeaderByCondition(
			DispatchContext dctx, Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<GenericValue> saleOrderHeaders1 = null;
		EntityCondition mainCond = null;
		List<String> orderBy = FastList.newInstance();
		orderBy.add("lastUpdateDocDate DESC");// 倒序
		try {
			String where = (String) view.get("where");
			mainCond = EntityCondition.makeConditionWhere(where);
			saleOrderHeaders1 = delegator.findList("SaleOrderHeaderAndperson",
					mainCond, null, orderBy, null, false);
			pObject.setFlag(Constant.OK);
			if (UtilValidate.isNotEmpty(saleOrderHeaders1)) {
				pObject.setData(saleOrderHeaders1);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}

	/**
	 * 根据条件查询SaleOrderDtl
	 */
	public static BasePosObject getSalesOrderDtlByCondition(
			DispatchContext dctx, Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<GenericValue> saleOrderDtls1 = null;
		EntityCondition mainCond = null;
		try {
			String where = (String) view.get("where");
			mainCond = EntityCondition.makeConditionWhere(where);
			// 根据条件查找订单明细
			saleOrderDtls1 = delegator.findList(
					"SaleOrderDtlandBomAndPolicyView", mainCond, null,
					UtilMisc.toList("docId"), null, false);
			pObject.setFlag(Constant.OK);
			/*
			 * // 取得BOM的名称 String boname = (String)
			 * saleOrderDtls1.get(0).get("productNames"); String policyname =
			 * (String) saleOrderDtls1.get(0) .get("policyName"); for
			 * (GenericValue saleOrderDtl : saleOrderDtls1) { //
			 * 根据参数获得SaleOrderDtl SaleOrderDtl SsaleOrderDtl =
			 * getSaleOrderDtl(saleOrderDtl); SsaleOrderDtl.setBomName(boname);
			 * SsaleOrderDtl.setProductSalesPolicyName(policyname); //
			 * 放入List<SaleOrderDtl> 中 saleOrderDtls.add(SsaleOrderDtl); }
			 */
			if (UtilValidate.isNotEmpty(saleOrderDtls1)) {
				pObject.setData(saleOrderDtls1);
			}
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}

	/**
	 * 根据条件查询SaleOrder
	 */
	public static BasePosObject getSalesOrderByCondition(DispatchContext dctx,
			Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<GenericValue> saleOrderHeaders1 = null;
		EntityCondition mainCond = null;
		List<SaleOrderDtl> saleOrderDtls = new ArrayList<SaleOrderDtl>();
		List<GenericValue> saleOrderDtls1 = null;
		List<String> orderBy = FastList.newInstance();
		orderBy.add("lastUpdateDocDate DESC");// 倒序
		try {
			String where = (String) view.get("where");
			mainCond = EntityCondition.makeConditionWhere(where);
			saleOrderHeaders1 = delegator.findList("SaleOrderHeaderAndperson",
					mainCond, null, orderBy, null, false);
			if (UtilValidate.isEmpty(saleOrderHeaders1)) {
				pObject.setFlag(Constant.OK);
				return pObject;
			}

			// 根据条件获取明细z
			mainCond = EntityCondition.makeConditionWhere(where);
			saleOrderDtls1 = delegator.findList(
					"SaleOrderDtlandBomAndPolicyView", mainCond, null,
					UtilMisc.toList("docId"), null, false);
			if (UtilValidate.isEmpty(saleOrderDtls1)) {
				pObject.setFlag(Constant.OK);
				return pObject;
			}
			// 获取Bomname
			String boname = (String) saleOrderDtls1.get(0).get("productNames");
			String policyname = (String) saleOrderDtls1.get(0)
					.get("policyName");
			for (GenericValue saleOrderDtl : saleOrderDtls1) {
				// 根据参数获得SaleOrderDtl
				SaleOrderDtl SsaleOrderDtl = getSaleOrderDtl(saleOrderDtl);
				SsaleOrderDtl.setProductSalesPolicyName(policyname);
				SsaleOrderDtl.setBomName(boname);
				// 放入List<SaleOrderDtl> 中
				saleOrderDtls.add(SsaleOrderDtl);
			}
			SaleOrders saleorders = new SaleOrders();
			saleorders.setHeader(saleOrderHeaders1);
			saleorders.setDetail(saleOrderDtls);
			pObject.setFlag(Constant.OK);
			pObject.setData(saleorders);
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}

	/**
	 * 添加出库数量（SalesOutWhsOrder）
	 */
	public static BasePosObject addSalesOrderWhsQty(DispatchContext dctx,
			SalesOutWhsOrder salesoutwhsorder) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		// 根据SQL查询行list 中的 库存数量 和 预订数量
		try {
			String docId = salesoutwhsorder.getHeader().getBaseEntry();
			String lastUpdateUserId = salesoutwhsorder.getHeader()
					.getUpdateUserId();
			Timestamp lastupdateDocDate = (Timestamp) salesoutwhsorder
					.getHeader().getUpdateDate();
			int i = 0;
			Map<String, Object> view = FastMap.newInstance();
			Long salesOutWhsorderQuantity = null;
			Long salesOutWhsorderLineNo = null;
			Long saleOrderDtlQuantity = null;
			boolean WarehouseStatusCleared = true;
			for (i = 0; i < salesoutwhsorder.getItem().size(); i++) {
				salesOutWhsorderQuantity = salesoutwhsorder.getItem().get(i)
						.getQuantity();
				salesOutWhsorderLineNo = salesoutwhsorder.getItem().get(i)
						.getBaseLineNo();

				GenericValue saleOrderDtl = delegator.findByPrimaryKey(
						"SaleOrderDtl", UtilMisc.toMap("docId", docId,
								"lineNo", salesOutWhsorderLineNo));
				saleOrderDtlQuantity = (Long) saleOrderDtl
						.get("warehouseQuantity") + salesOutWhsorderQuantity;

				if (saleOrderDtlQuantity > (Long) saleOrderDtl.get("quantity")) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg("出库数量大于销售数量");
					return pObject;
				}
				saleOrderDtl.set("warehouseQuantity", saleOrderDtlQuantity);
				saleOrderDtl.store();

			}
			List<GenericValue> SsaleOrderDtl = delegator.findByAnd(
					"SaleOrderDtl", UtilMisc.toMap("docId", docId));
			for (i = 0; i < SsaleOrderDtl.size(); i++) {
				salesOutWhsorderQuantity = (Long) SsaleOrderDtl.get(i).get(
						"warehouseQuantity");
				saleOrderDtlQuantity = (Long) SsaleOrderDtl.get(i).get(
						"quantity");
				if (salesOutWhsorderQuantity < saleOrderDtlQuantity) {
					WarehouseStatusCleared = false;
					break;
				}
			}
			// 如果明细出库数量=销售数量
			if (WarehouseStatusCleared) {
				view.put("docId", docId);
				view.put("warehouseStatus", Constant.CLEAR);
				view.put("lastUpdateUserId", lastUpdateUserId);
				view.put("lastUpdateDocDate", lastupdateDocDate);
			} else {
				view.put("docId", docId);
				view.put("returnStatus", Constant.PARTCLEARED);
				view.put("lastUpdateUserId", lastUpdateUserId);
				view.put("lastUpdateDocDate", lastupdateDocDate);

			}
			updateSalesOrderStatus(dctx, view);
			pObject.setFlag(Constant.OK);
		} catch (Exception e) {
			e.printStackTrace();
			pObject.setFlag(Constant.NG);
		}

		return pObject;
	}

	/**
	 * 添加addSalesOrderRtnQty（returnOrder）
	 */
	@SuppressWarnings("unused")
	public static BasePosObject addSalesOrderRtnQty(DispatchContext dctx,
			ReturnOrder returnOrder) {
		Delegator delegator = dctx.getDelegator();
		List<SaleOrderDtl> saleOrderDtls = null;
		BasePosObject pObject = new BasePosObject();
		// 根据SQL查询行list 中的 订货数量 和 退订数量
		try {
			String docId = returnOrder.getHeader().getBaseEntry();
			String lastUpdateUserId = (String) returnOrder.getHeader()
					.getLastUpdateUserId();
			Timestamp lastUpdateDocDate = (Timestamp) returnOrder.getHeader()
					.getLastUpdateDocDate();
			int i = 0;
			Long returnOrderQantity = null; // 退货数量
			Long returnOrderLineNoBaseEntry = null;
			Long saleorderdtlQuantity = null;// 设置后的数量
			boolean ReturnStatusCleared = true;
			GenericValue Header = delegator.findByPrimaryKey("SaleOrderHeader",
					UtilMisc.toMap("docId", docId));
			for (i = 0; i < returnOrder.getDetail().size(); i++) {
				returnOrderQantity = returnOrder.getDetail().get(i)
						.getQuantity();
				returnOrderLineNoBaseEntry = returnOrder.getDetail().get(i)
						.getLineNoBaseEntry();
				GenericValue SsaleOrderDtl = delegator.findByPrimaryKey(
						"SaleOrderDtl", UtilMisc.toMap("docId", docId,
								"lineNo", returnOrderLineNoBaseEntry));
				saleorderdtlQuantity = (Long) SsaleOrderDtl
						.get("returnQuantity") + returnOrderQantity;
				if (saleorderdtlQuantity > (Long) SsaleOrderDtl.get("quantity")) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg("退货数量大于销售数量");
					return pObject;
				}
				SsaleOrderDtl.set("returnQuantity", saleorderdtlQuantity);
				SsaleOrderDtl.store();
			}
			List<GenericValue> saleOrderDtl = delegator.findByAnd(
					"SaleOrderDtl", UtilMisc.toMap("docId", docId));
			for (i = 0; i < saleOrderDtl.size(); i++) {
				returnOrderQantity = (Long) saleOrderDtl.get(i).get(
						"returnQuantity");
				saleorderdtlQuantity = (Long) saleOrderDtl.get(i).get(
						"quantity");
				if (returnOrderQantity < saleorderdtlQuantity) {
					ReturnStatusCleared = false;
					break;
				}

			}
			Map<String, Object> view = FastMap.newInstance();
			// 如果明细中的所有退货数量=销售数量 
			if (ReturnStatusCleared) {
				view.put("docId", docId);
				view.put("returnStatus", Constant.CLEAR);
				view.put("lastUpdateUserId", lastUpdateUserId);
				view.put("lastUpdateDocDate", lastUpdateDocDate);
			} else {// 更新为部分已清
				view.put("docId", docId);
				view.put("returnStatus", Constant.PARTCLEARED);
				view.put("lastUpdateUserId", lastUpdateUserId);
				view.put("lastUpdateDocDate", lastUpdateDocDate);
			}
			updateSalesOrderStatus(dctx, view);
			pObject.setFlag(Constant.OK);
		} catch (Exception e) {
			e.printStackTrace();
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
		}
		return pObject;
	}

	/**
	 * 添加销售付款(CollectionOrder)
	 */
	@SuppressWarnings("unused")
	public static BasePosObject addSalesOrderCollectionAmount(
			DispatchContext dctx, Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		// List<String> orderBy = FastList.newInstance();
		// orderBy.add("lastUpdateDocDate DESC");//倒序
		try {
			String lastUpdateUserId = (String) view.get("lastUpdateUserId");
			Timestamp lastUpdateDocDate = (Timestamp) view
					.get("lastUpdateDocDate");
			BigDecimal amounts = (BigDecimal) view.get("collectionAmount");// 收款单传过来的付款额
			String docId = (String) view.get("docId");
			GenericValue SaleOrderHeaders = delegator.findByPrimaryKey(
					"SaleOrderHeader", UtilMisc.toMap("docId", docId));

			BigDecimal preCollectionAmount = (BigDecimal) SaleOrderHeaders
					.get("preCollectionAmount");// 订金额
			BigDecimal CcollectionAmount = (BigDecimal) SaleOrderHeaders
					.get("collectionAmount");// 收款额
			BigDecimal rebateAmount = (BigDecimal) SaleOrderHeaders
					.get("rebateAmount");// 订单总金额
			double rAmount = rebateAmount.doubleValue();
			BigDecimal collectionAmounts = CcollectionAmount.add(amounts);// 累加付款额
			BigDecimal newPerCollectionAmount = collectionAmounts
					.add(preCollectionAmount); // 付款额+订金额

			double nPca = newPerCollectionAmount.doubleValue();
			String fundStatus = (String) SaleOrderHeaders.get("fundStatus");// 付款状态
			String docIds = (String) SaleOrderHeaders.get("docId");
			boolean fundStatusClear = true;
			Map<String, Object> viewMap = FastMap.newInstance();
			// 如果付款额+订金额>订单总金额 报错
			if (nPca > rAmount) {
				pObject.setFlag(Constant.NG);
				pObject.setMsg("付款额大于订单总额");
				return pObject;
			} else if (nPca < rAmount) {
				fundStatusClear = false;
			}
			SaleOrderHeaders.set("collectionAmount", collectionAmounts);
			SaleOrderHeaders.store();

			// 如果付款额+订金额=订金总额
			if (fundStatusClear) {
				// 查询预订单表头的付款状态为已清返回错误信息
				if (fundStatus.equals(Constant.CLEAR)) {
					pObject.setFlag(Constant.NG);
					pObject.setMsg("订单表头的付款状态为已清");
					return pObject;
				}
				view.put("docId", docId);
				view.put("fundStatus", Constant.CLEAR);
				view.put("lastUpdateUserId", lastUpdateUserId);
				view.put("lastUpdateDocDate", lastUpdateDocDate);
				// 更新订单表头的付款状态为已清
			} else {
				// 如果付款额+订金额<订金总额
				view.put("docId", docId);
				view.put("fundStatus", Constant.PARTCLEARED);
				view.put("lastUpdateUserId", lastUpdateUserId);
				view.put("lastUpdateDocDate", lastUpdateDocDate);
				// 更新销售订单付款状态为部分已清
			}
			updateSalesOrderStatus(dctx, view);
			// 更新销售订单表头的付款额

			pObject.setFlag(Constant.OK);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pObject;
	}
	
	public static BasePosObject updateSalesOrderDtlSN(DispatchContext dctx, Object docId, Object lineNo, Object sequenceId){
		Delegator delegator = dctx.getDelegator();
		GenericValue saleOrderDtl = null;
		BasePosObject pObject = new BasePosObject();
		try {
			//查询销售订单明细
			saleOrderDtl = delegator.findByPrimaryKey("SaleOrderDtl",UtilMisc.toMap("docId", docId,"lineNo",lineNo));
			if (UtilValidate.isNotEmpty(saleOrderDtl)) {
				if(UtilValidate.isEmpty(saleOrderDtl.get("serialNo"))){
					// 更新serialNo
					saleOrderDtl.set("serialNo", sequenceId);
					saleOrderDtl.store();
					pObject.setData(true);
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
	public static BasePosObject updateSalesOrderHeaderUserId(DispatchContext dctx, Object docId, Object userId){
		Delegator delegator = dctx.getDelegator();
		GenericValue saleOrderHeader = null;
		BasePosObject pObject = new BasePosObject();
		try {
			//查询销售订单头
			saleOrderHeader = delegator.findByPrimaryKey("SaleOrderHeader",UtilMisc.toMap("docId", docId));
			if (UtilValidate.isNotEmpty(saleOrderHeader)) {
				// 更新lastUpdateUserId
				saleOrderHeader.set("lastUpdateUserId", userId);
				saleOrderHeader.set("lastUpdateDocDate", UtilDateTime.nowTimestamp());
				saleOrderHeader.set("isSync",Constant.ONE);
				saleOrderHeader.store();
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
	 * 根据参数获取销售订单Header
	 */

	public static SaleOrderHeader getSaleOrderHeader(
			GenericValue saleOrderHeader) {
		SaleOrderHeader SsaleOrderHeader = null;
		try {
			SsaleOrderHeader = new SaleOrderHeader();
			SsaleOrderHeader.setDocId((String) saleOrderHeader.get("docId"));
			SsaleOrderHeader.setDocNo((Long) saleOrderHeader.get("docNo"));
			SsaleOrderHeader
					.setPartyId((String) saleOrderHeader.get("partyId"));
			SsaleOrderHeader
					.setStoreId((String) saleOrderHeader.get("storeId"));
			SsaleOrderHeader.setBaseEntry((String) saleOrderHeader
					.get("baseEntry"));
			SsaleOrderHeader.setExtSystemNo((String) saleOrderHeader
					.get("extSystemNo"));
			SsaleOrderHeader.setExtDocNo((String) saleOrderHeader
					.get("extDocNo"));
			SsaleOrderHeader.setPostingDate((Timestamp) saleOrderHeader
					.get("postingDate"));
			SsaleOrderHeader.setDocStatus((String) saleOrderHeader
					.get("docStatus"));
			SsaleOrderHeader.setMemo((String) saleOrderHeader.get("memo"));
			SsaleOrderHeader.setCreateUserId((String) saleOrderHeader
					.get("createUserId"));
			SsaleOrderHeader.setCreateDocDate((Timestamp) saleOrderHeader
					.get("createDocDate"));
			SsaleOrderHeader.setLastUpdateUserId((String) saleOrderHeader
					.get("lastUpdateUserId"));
			SsaleOrderHeader.setLastUpdateDocDate((Timestamp) saleOrderHeader
					.get("lastUpdateDocDate"));
			SsaleOrderHeader.setMemberId((String) saleOrderHeader
					.get("memberId"));
			SsaleOrderHeader.setPrimeAmount((BigDecimal) saleOrderHeader
					.get("primeAmount"));
			SsaleOrderHeader.setRebateAmount((BigDecimal) saleOrderHeader
					.get("rebateAmount"));
			SsaleOrderHeader
					.setPreCollectionAmount((BigDecimal) saleOrderHeader
							.get("preCollectionAmount"));
			SsaleOrderHeader.setCollectionAmount((BigDecimal) saleOrderHeader
					.get("collectionAmount"));
			SsaleOrderHeader.setFundStatus((String) saleOrderHeader
					.get("fundStatus"));
			SsaleOrderHeader.setWarehouseStatus((String) saleOrderHeader
					.get("warehouseStatus"));
			SsaleOrderHeader.setReturnStatus((String) saleOrderHeader
					.get("returnStatus"));
			SsaleOrderHeader.setMemberPointAmount((Long) saleOrderHeader
					.get("memberPointAmount"));
			SsaleOrderHeader
					.setSalesId((String) saleOrderHeader.get("salesId"));
			SsaleOrderHeader.setCollectionStoreId((String) saleOrderHeader
					.get("collectionStoreId"));
			SsaleOrderHeader.setDeliveryStoreId((String) saleOrderHeader
					.get("deliveryStoreId"));
			SsaleOrderHeader.setApprovalUserId((String) saleOrderHeader
					.get("approvalUserId"));
			SsaleOrderHeader.setApprovalDate((Timestamp) saleOrderHeader
					.get("approvalDate"));
			SsaleOrderHeader.setMovementTypeId((String) saleOrderHeader
					.get("movementTypeId"));
			SsaleOrderHeader.setIsSync((String) saleOrderHeader.get("isSync"));
			SsaleOrderHeader
					.setPrintNum((Long) saleOrderHeader.get("printNum"));
			SsaleOrderHeader.setLogisticsAddress((String) saleOrderHeader
					.get("logisticsAddress"));
			SsaleOrderHeader.setAttrName1((String) saleOrderHeader
					.get("attrName1"));
			SsaleOrderHeader.setAttrName2((String) saleOrderHeader
					.get("attrName2"));
			SsaleOrderHeader.setAttrName3((String) saleOrderHeader
					.get("attrName3"));
			SsaleOrderHeader.setAttrName4((String) saleOrderHeader
					.get("attrName4"));
			SsaleOrderHeader.setAttrName5((String) saleOrderHeader
					.get("attrName5"));
			SsaleOrderHeader.setAttrName6((String) saleOrderHeader
					.get("attrName6"));
			SsaleOrderHeader.setAttrName7((String) saleOrderHeader
					.get("attrName7"));
			SsaleOrderHeader.setAttrName8((String) saleOrderHeader
					.get("attrName8"));
			SsaleOrderHeader.setAttrName9((String) saleOrderHeader
					.get("attrName9"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SsaleOrderHeader;
	}

	/*
	 * 根据参数获取Dtl
	 */

	public static SaleOrderDtl getSaleOrderDtl(GenericValue saleOrderDtl) {
		SaleOrderDtl SsaleOrderDtl = null;
		try {
			SsaleOrderDtl = new SaleOrderDtl();
			SsaleOrderDtl.setDocId((String) saleOrderDtl.get("docId"));
			SsaleOrderDtl.setLineNo((Long) saleOrderDtl.get("lineNo"));
			SsaleOrderDtl.setLineNoBaseEntry((Long) saleOrderDtl
					.get("lineNoBaseEntry"));
			SsaleOrderDtl.setProductId((String) saleOrderDtl.get("productId"));
			SsaleOrderDtl.setProductName((String) saleOrderDtl
					.get("productName"));
			SsaleOrderDtl.setBarCodes((String) saleOrderDtl.get("barCodes"));
			SsaleOrderDtl.setExtNo((String) saleOrderDtl.get("extNo"));
			SsaleOrderDtl.setSerialNo((String) saleOrderDtl.get("serialNo"));
			SsaleOrderDtl.setUnitPrice((BigDecimal) saleOrderDtl
					.get("unitPrice"));
			SsaleOrderDtl.setRebatePrice((BigDecimal) saleOrderDtl
					.get("rebatePrice"));
			SsaleOrderDtl.setQuantity((Long) saleOrderDtl.get("quantity"));
			SsaleOrderDtl.setWarehouseQuantity((Long) saleOrderDtl
					.get("warehouseQuantity"));
			SsaleOrderDtl.setLockedQuantity((Long) saleOrderDtl
					.get("lockedQuantity"));
			SsaleOrderDtl.setReturnQuantity((Long) saleOrderDtl
					.get("returnQuantity"));
			SsaleOrderDtl.setRebateAmount((BigDecimal) saleOrderDtl
					.get("rebateAmount"));
			SsaleOrderDtl.setBomAmount((BigDecimal) saleOrderDtl
					.get("bomAmount"));
			SsaleOrderDtl.setMemo((String) saleOrderDtl.get("memo"));
			SsaleOrderDtl
					.setFacilityId((String) saleOrderDtl.get("facilityId"));
			SsaleOrderDtl.setProductSalesPolicyId((String) saleOrderDtl
					.get("productSalesPolicyId"));
			SsaleOrderDtl.setProductSalesPolicyNo((Long) saleOrderDtl
					.get("productSalesPolicyNo"));
			SsaleOrderDtl.setSalesId((String) saleOrderDtl.get("salesId"));
			SsaleOrderDtl
					.setMemberPoint((Long) saleOrderDtl.get("memberPoint"));
			SsaleOrderDtl.setIsGift((String) saleOrderDtl.get("isGift"));
			SsaleOrderDtl.setIsMainProduct((String) saleOrderDtl
					.get("isMainProduct"));
			SsaleOrderDtl.setBomId((String) saleOrderDtl.get("bomId"));
			SsaleOrderDtl
					.setIsSequence((String) saleOrderDtl.get("isSequence"));
			SsaleOrderDtl.setInvoiceNo((String) saleOrderDtl.get("invoiceNo"));
			SsaleOrderDtl.setApprovalUserId((String) saleOrderDtl
					.get("approvalUserId"));
			SsaleOrderDtl.setBaseEntry((String) saleOrderDtl.get("baseEntry"));
			SsaleOrderDtl.setAttrName1((String) saleOrderDtl.get("attrName1"));
			SsaleOrderDtl.setAttrName2((String) saleOrderDtl.get("attrName2"));
			SsaleOrderDtl.setAttrName3((String) saleOrderDtl.get("attrName3"));
			SsaleOrderDtl.setAttrName4((String) saleOrderDtl.get("attrName4"));
			SsaleOrderDtl.setAttrName5((String) saleOrderDtl.get("attrName5"));
			SsaleOrderDtl.setAttrName6((String) saleOrderDtl.get("attrName6"));
			SsaleOrderDtl.setAttrName7((String) saleOrderDtl.get("attrName7"));
			SsaleOrderDtl.setAttrName8((String) saleOrderDtl.get("attrName8"));
			SsaleOrderDtl.setAttrName9((String) saleOrderDtl.get("attrName9"));

		} catch (Exception e) {
			e.printStackTrace();
		}
		return SsaleOrderDtl;
	}

	/*
	 * 根据参数的到GenericValue Saleorderheader,放入map
	 */
	public static GenericValue getGVSaleOrderHeader(DispatchContext dctx,
			SaleOrderHeader saleOrderHeader) {
		GenericValue SsaleOrderHeader = null;
		Map<String, Object> SaleOrderHeaderMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		try {
			SaleOrderHeaderMap.put("docId", saleOrderHeader.getDocId());
			SaleOrderHeaderMap.put("docNo", saleOrderHeader.getDocNo());
			SaleOrderHeaderMap.put("partyId", saleOrderHeader.getPartyId());
			SaleOrderHeaderMap.put("storeId", saleOrderHeader.getStoreId());
			SaleOrderHeaderMap.put("baseEntry", saleOrderHeader.getBaseEntry());
			SaleOrderHeaderMap.put("extSystemNo",
					saleOrderHeader.getExtSystemNo());
			SaleOrderHeaderMap.put("extDocNo", saleOrderHeader.getExtDocNo());
			SaleOrderHeaderMap.put("postingDate",
					saleOrderHeader.getPostingDate());
			SaleOrderHeaderMap.put("docStatus", saleOrderHeader.getDocStatus());
			SaleOrderHeaderMap.put("memo", saleOrderHeader.getMemo());
			SaleOrderHeaderMap.put("createUserId",
					saleOrderHeader.getCreateUserId());
			SaleOrderHeaderMap.put("createDocDate", timestamp);
			SaleOrderHeaderMap.put("lastUpdateUserId",
					saleOrderHeader.getLastUpdateUserId());
			SaleOrderHeaderMap.put("lastUpdateDocDate", timestamp);
			SaleOrderHeaderMap.put("memberId", saleOrderHeader.getMemberId());
			SaleOrderHeaderMap.put("primeAmount",
					saleOrderHeader.getPrimeAmount());
			SaleOrderHeaderMap.put("rebateAmount",
					saleOrderHeader.getRebateAmount());
			SaleOrderHeaderMap.put("preCollectionAmount",
					saleOrderHeader.getPreCollectionAmount());
			SaleOrderHeaderMap.put("collectionAmount",
					saleOrderHeader.getCollectionAmount());
			SaleOrderHeaderMap.put("fundStatus",
					saleOrderHeader.getFundStatus());
			SaleOrderHeaderMap.put("warehouseStatus",
					saleOrderHeader.getWarehouseStatus());
			SaleOrderHeaderMap.put("returnStatus",
					saleOrderHeader.getReturnStatus());
			SaleOrderHeaderMap.put("memberPointAmount",
					saleOrderHeader.getMemberPointAmount());
			SaleOrderHeaderMap.put("salesId", saleOrderHeader.getSalesId());

			SaleOrderHeaderMap.put("logisticsAddress",
					saleOrderHeader.getLogisticsAddress());

			SaleOrderHeaderMap.put("collectionStoreId",
					saleOrderHeader.getCollectionStoreId());
			SaleOrderHeaderMap.put("deliveryStoreId",
					saleOrderHeader.getDeliveryStoreId());
			SaleOrderHeaderMap.put("approvalUserId",
					saleOrderHeader.getApprovalUserId());
			SaleOrderHeaderMap.put("approvalDate",
					saleOrderHeader.getApprovalDate());
			SaleOrderHeaderMap.put("movementTypeId",
					saleOrderHeader.getMovementTypeId());
			if (!saleOrderHeader.getDocStatus().equals(Constant.DRAFT)) {
				SaleOrderHeaderMap.put("isSync", Constant.ONE);
			} else {
				SaleOrderHeaderMap.put("isSync", saleOrderHeader.getIsSync());
			}
			SaleOrderHeaderMap.put("printNum", saleOrderHeader.getPrintNum());
			SaleOrderHeaderMap.put("attrName1", saleOrderHeader.getAttrName1());
			SaleOrderHeaderMap.put("attrName2", saleOrderHeader.getAttrName2());
			SaleOrderHeaderMap.put("attrName3", saleOrderHeader.getAttrName3());
			SaleOrderHeaderMap.put("attrName4", saleOrderHeader.getAttrName4());
			SaleOrderHeaderMap.put("attrName5", saleOrderHeader.getAttrName5());
			SaleOrderHeaderMap.put("attrName6", saleOrderHeader.getAttrName6());
			SaleOrderHeaderMap.put("attrName7", saleOrderHeader.getAttrName7());
			SaleOrderHeaderMap.put("attrName8", saleOrderHeader.getAttrName8());
			SaleOrderHeaderMap.put("attrName9", saleOrderHeader.getAttrName9());
			SsaleOrderHeader = delegator.makeValue("SaleOrderHeader",
					SaleOrderHeaderMap);
			SsaleOrderHeader.create();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SsaleOrderHeader;
	}

	/*
	 * 根据参数得到 GenericValue saleOrderDtl 放入map
	 */
	public static GenericValue getGVSaleOrderDtl(DispatchContext dctx,
			SaleOrderDtl saleOrderDtl) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> SaleOrderDtlMap = FastMap.newInstance();
		GenericValue SsaleOrderDtl = null;
		try {
			SaleOrderDtlMap.put("docId", saleOrderDtl.getDocId());
			SaleOrderDtlMap.put("lineNo", saleOrderDtl.getLineNo());
			SaleOrderDtlMap.put("lineNoBaseEntry",
					saleOrderDtl.getLineNoBaseEntry());
			SaleOrderDtlMap.put("productId", saleOrderDtl.getProductId());
			SaleOrderDtlMap.put("productName", saleOrderDtl.getProductName());
			SaleOrderDtlMap.put("barCodes", saleOrderDtl.getBarCodes());
			SaleOrderDtlMap.put("extNo", saleOrderDtl.getExtNo());
			SaleOrderDtlMap.put("serialNo", saleOrderDtl.getSerialNo());
			SaleOrderDtlMap.put("unitPrice", saleOrderDtl.getUnitPrice());
			SaleOrderDtlMap.put("rebatePrice", saleOrderDtl.getRebatePrice());
			SaleOrderDtlMap.put("quantity", saleOrderDtl.getQuantity());
			SaleOrderDtlMap.put("warehouseQuantity",
					saleOrderDtl.getWarehouseQuantity());
			SaleOrderDtlMap.put("lockedQuantity",
					saleOrderDtl.getLockedQuantity());
			SaleOrderDtlMap.put("returnQuantity",
					saleOrderDtl.getReturnQuantity());
			SaleOrderDtlMap.put("rebateAmount", saleOrderDtl.getRebateAmount());
			SaleOrderDtlMap.put("bomAmount", saleOrderDtl.getBomAmount());
			SaleOrderDtlMap.put("memo", saleOrderDtl.getMemo());
			SaleOrderDtlMap.put("productSalesPolicyId",
					saleOrderDtl.getProductSalesPolicyId());
			SaleOrderDtlMap.put("productSalesPolicyNo",
					saleOrderDtl.getProductSalesPolicyNo());
			SaleOrderDtlMap.put("salesId", saleOrderDtl.getSalesId());
			SaleOrderDtlMap.put("memberPoint", saleOrderDtl.getMemberPoint());
			SaleOrderDtlMap.put("isMainProduct",
					saleOrderDtl.getIsMainProduct());
			SaleOrderDtlMap.put("bomId", saleOrderDtl.getBomId());
			SaleOrderDtlMap.put("facilityId", saleOrderDtl.getFacilityId());
			SaleOrderDtlMap.put("isGift", saleOrderDtl.getIsGift());
			SaleOrderDtlMap.put("isSequence", saleOrderDtl.getIsSequence());
			SaleOrderDtlMap.put("invoiceNo", saleOrderDtl.getInvoiceNo());
			SaleOrderDtlMap.put("approvalUserId",
					saleOrderDtl.getApprovalUserId());
			SaleOrderDtlMap.put("baseEntry", saleOrderDtl.getBaseEntry());
			SaleOrderDtlMap.put("attrName1", saleOrderDtl.getAttrName1());
			SaleOrderDtlMap.put("attrName2", saleOrderDtl.getAttrName2());
			SaleOrderDtlMap.put("attrName3", saleOrderDtl.getAttrName3());
			SaleOrderDtlMap.put("attrName4", saleOrderDtl.getAttrName4());
			SaleOrderDtlMap.put("attrName5", saleOrderDtl.getAttrName5());
			SaleOrderDtlMap.put("attrName6", saleOrderDtl.getAttrName6());
			SaleOrderDtlMap.put("attrName7", saleOrderDtl.getAttrName7());
			SaleOrderDtlMap.put("attrName8", saleOrderDtl.getAttrName8());
			SaleOrderDtlMap.put("attrName9", saleOrderDtl.getAttrName9());
			SsaleOrderDtl = delegator
					.makeValue("SaleOrderDtl", SaleOrderDtlMap);
			SsaleOrderDtl.create();

		} catch (Exception e) {
			e.printStackTrace();

		}
		return SsaleOrderDtl;
	}

	/*
	 * 根据参数得到Generic Value saleorderdtlprmtn
	 */
	public static GenericValue getGVSaleOrderDtlPrmtn(DispatchContext dctx,
			SaleOrderDtlPrmtn saleOrderDtlPrmtn) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> SaleOrderDtlPrmtnMap = FastMap.newInstance();
		GenericValue SsaleOrderDtlPrmtn = null;
		try {
			SaleOrderDtlPrmtnMap.put("docId", saleOrderDtlPrmtn.getDocId());
			SaleOrderDtlPrmtnMap.put("lineNo", saleOrderDtlPrmtn.getLineNo());
			SaleOrderDtlPrmtnMap.put("promotionId",
					saleOrderDtlPrmtn.getPromotionId());
			SaleOrderDtlPrmtnMap.put("orderPrmtnId",
					saleOrderDtlPrmtn.getOrderPrmtnId());
			SaleOrderDtlPrmtnMap.put("attrName1",
					saleOrderDtlPrmtn.getAttrName1());
			SaleOrderDtlPrmtnMap.put("attrName2",
					saleOrderDtlPrmtn.getAttrName2());
			SaleOrderDtlPrmtnMap.put("attrName3",
					saleOrderDtlPrmtn.getAttrName3());
			SaleOrderDtlPrmtnMap.put("attrName4",
					saleOrderDtlPrmtn.getAttrName4());
			SaleOrderDtlPrmtnMap.put("attrName5",
					saleOrderDtlPrmtn.getAttrName5());
			SaleOrderDtlPrmtnMap.put("attrName6",
					saleOrderDtlPrmtn.getAttrName6());
			SaleOrderDtlPrmtnMap.put("attrName7",
					saleOrderDtlPrmtn.getAttrName7());
			SaleOrderDtlPrmtnMap.put("attrName8",
					saleOrderDtlPrmtn.getAttrName8());
			SaleOrderDtlPrmtnMap.put("attrName9",
					saleOrderDtlPrmtn.getAttrName9());
			SsaleOrderDtlPrmtn = delegator.makeValue("SaleOrderDtlPrmtn",
					SaleOrderDtlPrmtnMap);
			SsaleOrderDtlPrmtn.create();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SsaleOrderDtlPrmtn;
	}

	/*
	 * 根据参数得到GenericValue SaleOrderPrmtn
	 */
	public static GenericValue getGVSaleOrderPrmtn(DispatchContext dctx,
			SaleOrderPrmtn saleOrderPrmtn) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> SaleOrderPrmtnMap = FastMap.newInstance();
		GenericValue SsaleOrderPrmtn = null;
		try {
			SaleOrderPrmtnMap.put("orderPrmtnId",
					saleOrderPrmtn.getOrderPrmtnId());
			SaleOrderPrmtnMap.put("promotionId",
					saleOrderPrmtn.getPromotionId());
			SaleOrderPrmtnMap.put("attrName1", saleOrderPrmtn.getAttrName1());
			SaleOrderPrmtnMap.put("attrName2", saleOrderPrmtn.getAttrName2());
			SaleOrderPrmtnMap.put("attrName3", saleOrderPrmtn.getAttrName3());
			SaleOrderPrmtnMap.put("attrName4", saleOrderPrmtn.getAttrName4());
			SaleOrderPrmtnMap.put("attrName5", saleOrderPrmtn.getAttrName5());
			SaleOrderPrmtnMap.put("attrName6", saleOrderPrmtn.getAttrName6());
			SaleOrderPrmtnMap.put("attrName7", saleOrderPrmtn.getAttrName7());
			SaleOrderPrmtnMap.put("attrName8", saleOrderPrmtn.getAttrName8());
			SaleOrderPrmtnMap.put("attrName9", saleOrderPrmtn.getAttrName9());
			SsaleOrderPrmtn = delegator.makeValue("SaleOrderPrmtn",
					SaleOrderPrmtnMap);
			SsaleOrderPrmtn.create();

		} catch (Exception e) {

			e.printStackTrace();

		}
		return SsaleOrderPrmtn;
	}

	/*
	 * 根据参数得到GenericValue SaleOrderPriceDtl
	 */
	public static GenericValue getGVSaleOrderPriceDtl(DispatchContext dctx,
			SalesOrderPriceDtl salesOrderPriceDtl) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> SaleOrderPriceDtlMap = FastMap.newInstance();
		GenericValue SsaleOrderPriceDtl = null;
		try {
			SaleOrderPriceDtlMap.put("docId", salesOrderPriceDtl.getDocId());
			SaleOrderPriceDtlMap.put("lineNo", salesOrderPriceDtl.getLineNo());
			SaleOrderPriceDtlMap.put("productPriceTypeId",
					salesOrderPriceDtl.getProductPriceTypeId());
			SaleOrderPriceDtlMap.put("productPriceRuleId",
					salesOrderPriceDtl.getProductPriceRuleId());
			SaleOrderPriceDtlMap.put("price", salesOrderPriceDtl.getPrice());
			SaleOrderPriceDtlMap.put("attrName1",
					salesOrderPriceDtl.getAttrName1());
			SaleOrderPriceDtlMap.put("attrName2",
					salesOrderPriceDtl.getAttrName2());
			SaleOrderPriceDtlMap.put("attrName3",
					salesOrderPriceDtl.getAttrName3());
			SaleOrderPriceDtlMap.put("attrName4",
					salesOrderPriceDtl.getAttrName4());
			SaleOrderPriceDtlMap.put("attrName5",
					salesOrderPriceDtl.getAttrName5());
			SaleOrderPriceDtlMap.put("attrName6",
					salesOrderPriceDtl.getAttrName6());
			SaleOrderPriceDtlMap.put("attrName7",
					salesOrderPriceDtl.getAttrName7());
			SaleOrderPriceDtlMap.put("attrName8",
					salesOrderPriceDtl.getAttrName8());
			SaleOrderPriceDtlMap.put("attrName9",
					salesOrderPriceDtl.getAttrName9());
			SsaleOrderPriceDtl = delegator.makeValue("SalesOrderPriceDtl",
					SaleOrderPriceDtlMap);
			SsaleOrderPriceDtl.create();

		} catch (Exception e) {

			e.printStackTrace();

		}
		return SsaleOrderPriceDtl;
	}

	/*
	 * 更新销售订单头(set)
	 */
	public static GenericValue updateGVSaleOrderHeader(
			GenericValue SsaleOrderHeader, SaleOrderHeader saleOrderHeader) {
		try {
			Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			SsaleOrderHeader.set("docId", saleOrderHeader.getDocId());
			SsaleOrderHeader.set("docNo", saleOrderHeader.getDocNo());
			SsaleOrderHeader.set("partyId", saleOrderHeader.getPartyId());
			SsaleOrderHeader.set("storeId", saleOrderHeader.getStoreId());
			SsaleOrderHeader.set("baseEntry", saleOrderHeader.getBaseEntry());
			SsaleOrderHeader.set("extSystemNo",
					saleOrderHeader.getExtSystemNo());
			SsaleOrderHeader.set("extDocNo", saleOrderHeader.getExtDocNo());
			SsaleOrderHeader.set("postingDate",
					saleOrderHeader.getPostingDate());
			SsaleOrderHeader.set("docStatus", saleOrderHeader.getDocStatus());
			SsaleOrderHeader.set("memo", saleOrderHeader.getMemo());
			SsaleOrderHeader.set("createUserId",
					saleOrderHeader.getCreateUserId());
			SsaleOrderHeader.set("createDocDate", timestamp);
			SsaleOrderHeader.set("lastUpdateUserId",
					saleOrderHeader.getLastUpdateUserId());
			SsaleOrderHeader.set("lastUpdateDocDate", timestamp);
			SsaleOrderHeader.set("memberId", saleOrderHeader.getMemberId());
			SsaleOrderHeader.set("primeAmount",
					saleOrderHeader.getPrimeAmount());
			SsaleOrderHeader.set("rebateAmount",
					saleOrderHeader.getRebateAmount());
			SsaleOrderHeader.set("preCollectionAmount",
					saleOrderHeader.getPreCollectionAmount());
			SsaleOrderHeader.set("collectionAmount",
					saleOrderHeader.getCollectionAmount());
			SsaleOrderHeader.set("fundStatus", saleOrderHeader.getFundStatus());
			SsaleOrderHeader.set("warehouseStatus",
					saleOrderHeader.getWarehouseStatus());
			SsaleOrderHeader.set("returnStatus",
					saleOrderHeader.getReturnStatus());
			SsaleOrderHeader.set("memberPointAmount",
					saleOrderHeader.getMemberPointAmount());
			SsaleOrderHeader.set("salesId", saleOrderHeader.getSalesId());
			SsaleOrderHeader.set("collectionStoreId",
					saleOrderHeader.getCollectionStoreId());
			SsaleOrderHeader.set("deliveryStoreId",
					saleOrderHeader.getDeliveryStoreId());
			SsaleOrderHeader.set("logisticsAddress",
					saleOrderHeader.getLogisticsAddress());
			SsaleOrderHeader.set("approvalUserId",
					saleOrderHeader.getApprovalUserId());
			SsaleOrderHeader.set("approvalDate",
					saleOrderHeader.getApprovalDate());
			SsaleOrderHeader.set("movementTypeId",
					saleOrderHeader.getMovementTypeId());
			SsaleOrderHeader.set("isSync", saleOrderHeader.getIsSync());
			SsaleOrderHeader.set("printNum", saleOrderHeader.getPrintNum());
			SsaleOrderHeader.set("attrName1", saleOrderHeader.getAttrName1());
			SsaleOrderHeader.set("attrName2", saleOrderHeader.getAttrName2());
			SsaleOrderHeader.set("attrName3", saleOrderHeader.getAttrName3());
			SsaleOrderHeader.set("attrName4", saleOrderHeader.getAttrName4());
			SsaleOrderHeader.set("attrName5", saleOrderHeader.getAttrName5());
			SsaleOrderHeader.set("attrName6", saleOrderHeader.getAttrName6());
			SsaleOrderHeader.set("attrName7", saleOrderHeader.getAttrName7());
			SsaleOrderHeader.set("attrName8", saleOrderHeader.getAttrName8());
			SsaleOrderHeader.set("attrName9", saleOrderHeader.getAttrName9());
			SsaleOrderHeader.store();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SsaleOrderHeader;
	}

	/*
	 * 中转库传数据
	 */
	public static Map<String, Object> getSaleOrderHeaderMap(
			DispatchContext dctx, SaleOrderHeader saleOrderHeader) {
		Map<String, Object> SaleOrderHeaderMap = FastMap.newInstance();
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		try {
			SaleOrderHeaderMap.put("baseEntry", saleOrderHeader.getDocId());//
			SaleOrderHeaderMap.put("docNo", saleOrderHeader.getDocNo());
			SaleOrderHeaderMap.put("partyId", saleOrderHeader.getPartyId());
			SaleOrderHeaderMap.put("productStoreId",
					saleOrderHeader.getStoreId());
			//SaleOrderHeaderMap.put("baseEntry", saleOrderHeader.getBaseEntry());
			SaleOrderHeaderMap.put("extSystemNo",
					saleOrderHeader.getExtSystemNo());
			SaleOrderHeaderMap.put("extDocNo", saleOrderHeader.getExtDocNo());
			SaleOrderHeaderMap.put("postingDate",
					saleOrderHeader.getPostingDate());
			//SaleOrderHeaderMap.put("statusId", saleOrderHeader.getDocStatus());//
			SaleOrderHeaderMap.put("memo", saleOrderHeader.getMemo());
			SaleOrderHeaderMap.put("createUserId",
					saleOrderHeader.getCreateUserId());
			SaleOrderHeaderMap.put("createDocDate", timestamp);
			SaleOrderHeaderMap.put("updateUserId",
					saleOrderHeader.getLastUpdateUserId());
			SaleOrderHeaderMap.put("updateDate", timestamp);
			SaleOrderHeaderMap.put("memberId", saleOrderHeader.getMemberId());
			SaleOrderHeaderMap.put("primeAmount",
					saleOrderHeader.getPrimeAmount());
			SaleOrderHeaderMap.put("rebateAmount",
					saleOrderHeader.getRebateAmount());
			SaleOrderHeaderMap.put("preCollectionAmount",
					saleOrderHeader.getPreCollectionAmount());
			SaleOrderHeaderMap.put("collectionAmount",
					saleOrderHeader.getCollectionAmount());
			SaleOrderHeaderMap.put("fundStatus",
					saleOrderHeader.getFundStatus());
			SaleOrderHeaderMap.put("warehouseStatus",
					saleOrderHeader.getWarehouseStatus());
			SaleOrderHeaderMap.put("returnStatus",
					saleOrderHeader.getReturnStatus());
			SaleOrderHeaderMap.put("memberPointAmount",
					saleOrderHeader.getMemberPointAmount());
			SaleOrderHeaderMap.put("salesId", saleOrderHeader.getSalesId());
			SaleOrderHeaderMap.put("logisticsAddress",
					saleOrderHeader.getLogisticsAddress());
			SaleOrderHeaderMap.put("collectionStoreId",
					saleOrderHeader.getCollectionStoreId());
			SaleOrderHeaderMap.put("deliveryStoreId",
					saleOrderHeader.getDeliveryStoreId());
			SaleOrderHeaderMap.put("approvalUserId",
					saleOrderHeader.getApprovalUserId());
			SaleOrderHeaderMap.put("approvalDate",
					saleOrderHeader.getApprovalDate());
			SaleOrderHeaderMap.put("isSync", saleOrderHeader.getIsSync());
			SaleOrderHeaderMap.put("movementTypeId",
					saleOrderHeader.getMovementTypeId());//
			SaleOrderHeaderMap.put("printNum", saleOrderHeader.getPrintNum());
			SaleOrderHeaderMap.put("attrName1", saleOrderHeader.getAttrName1());
			SaleOrderHeaderMap.put("attrName2", saleOrderHeader.getAttrName2());
			SaleOrderHeaderMap.put("attrName3", saleOrderHeader.getAttrName3());
			SaleOrderHeaderMap.put("attrName4", saleOrderHeader.getAttrName4());
			SaleOrderHeaderMap.put("attrName5", saleOrderHeader.getAttrName5());
			SaleOrderHeaderMap.put("attrName6", saleOrderHeader.getAttrName6());
			SaleOrderHeaderMap.put("attrName7", saleOrderHeader.getAttrName7());
			SaleOrderHeaderMap.put("attrName8", saleOrderHeader.getAttrName8());
			SaleOrderHeaderMap.put("attrName9", saleOrderHeader.getAttrName9());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return SaleOrderHeaderMap;
	}

	/*
	 * 中转库传数据
	 */
	public static Map<String, Object> getSaleOrderDtlMap(DispatchContext dctx,
			SaleOrderDtl saleOrderDtl) {
		Map<String, Object> SaleOrderDtlMap = FastMap.newInstance();
		// GenericValue SsaleOrderDtl = null;
		try {
			SaleOrderDtlMap.put("docId", saleOrderDtl.getDocId());// baseentry
			SaleOrderDtlMap.put("lineNo", saleOrderDtl.getLineNo());//
			SaleOrderDtlMap.put("lineNoBaseEntry",
					saleOrderDtl.getLineNoBaseEntry());
			SaleOrderDtlMap.put("productId", saleOrderDtl.getProductId());
			SaleOrderDtlMap.put("productName", saleOrderDtl.getProductName());
			SaleOrderDtlMap.put("barCodes", saleOrderDtl.getBarCodes());
			SaleOrderDtlMap.put("extNo", saleOrderDtl.getExtNo());
			SaleOrderDtlMap.put("sequenceId", saleOrderDtl.getSerialNo());//
			SaleOrderDtlMap.put("unitPrice", saleOrderDtl.getUnitPrice());
			SaleOrderDtlMap.put("rebatePrice", saleOrderDtl.getRebatePrice());
			SaleOrderDtlMap.put("quantity", saleOrderDtl.getLockedQuantity());// lock
																				// gei
																				// quantity
			SaleOrderDtlMap.put("warehouseQuantity",
					saleOrderDtl.getWarehouseQuantity());
			SaleOrderDtlMap.put("lockedQuantity",
					saleOrderDtl.getLockedQuantity());
			SaleOrderDtlMap.put("returnQuantity",
					saleOrderDtl.getReturnQuantity());
			SaleOrderDtlMap.put("rebateAmount", saleOrderDtl.getRebateAmount());
			SaleOrderDtlMap.put("bomAmount", saleOrderDtl.getBomAmount());
			SaleOrderDtlMap.put("memo", saleOrderDtl.getMemo());
			SaleOrderDtlMap.put("facilityId", saleOrderDtl.getFacilityId());
			SaleOrderDtlMap.put("productSalesPolicyId",
					saleOrderDtl.getProductSalesPolicyId());
			SaleOrderDtlMap.put("productSalesPolicyNo",
					saleOrderDtl.getProductSalesPolicyNo());
			SaleOrderDtlMap.put("salesId", saleOrderDtl.getSalesId());
			SaleOrderDtlMap.put("memberPoint", saleOrderDtl.getMemberPoint());
			SaleOrderDtlMap.put("isMainProduct",
					saleOrderDtl.getIsMainProduct());
			SaleOrderDtlMap.put("bomId", saleOrderDtl.getBomId());
			SaleOrderDtlMap.put("isGift", saleOrderDtl.getIsGift());
			SaleOrderDtlMap.put("isSequence", saleOrderDtl.getIsSequence());
			SaleOrderDtlMap.put("invoiceNo", saleOrderDtl.getInvoiceNo());
			SaleOrderDtlMap.put("approvalUserId",
					saleOrderDtl.getApprovalUserId());
			SaleOrderDtlMap.put("baseEntry", saleOrderDtl.getBaseEntry());
			SaleOrderDtlMap.put("attrName1", saleOrderDtl.getAttrName1());
			SaleOrderDtlMap.put("attrName2", saleOrderDtl.getAttrName2());
			SaleOrderDtlMap.put("attrName3", saleOrderDtl.getAttrName3());
			SaleOrderDtlMap.put("attrName4", saleOrderDtl.getAttrName4());
			SaleOrderDtlMap.put("attrName5", saleOrderDtl.getAttrName5());
			SaleOrderDtlMap.put("attrName6", saleOrderDtl.getAttrName6());
			SaleOrderDtlMap.put("attrName7", saleOrderDtl.getAttrName7());
			SaleOrderDtlMap.put("attrName8", saleOrderDtl.getAttrName8());
			SaleOrderDtlMap.put("attrName9", saleOrderDtl.getAttrName9());
		} catch (Exception e) {
			e.printStackTrace();

		}
		return SaleOrderDtlMap;
	}

	/*
	 * 解锁传的数据
	 */
	public static Map<String, Object> getHeaderMap(DispatchContext dctx,
			GenericValue saleOrderHeader) {
		Map<String, Object> HeaderMap = FastMap.newInstance();
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		try {
			HeaderMap.put("docId", saleOrderHeader.get("docId"));//
			HeaderMap.put("docNo", saleOrderHeader.get("docNo"));
			HeaderMap.put("partyId", saleOrderHeader.get("partyId"));
			HeaderMap.put("productStoreId", saleOrderHeader.get("storeId"));
			HeaderMap.put("baseEntry", saleOrderHeader.get("baseEntry"));
			HeaderMap.put("extSystemNo", saleOrderHeader.get("extSystemNo"));
			HeaderMap.put("extDocNo", saleOrderHeader.get("extDocNo"));
			HeaderMap.put("postingDate", saleOrderHeader.get("postingDate"));
			HeaderMap.put("statusId", saleOrderHeader.get("docStatus"));//
			HeaderMap.put("memo", saleOrderHeader.get("memo"));
			HeaderMap.put("createUserId", saleOrderHeader.get("createUserId"));
			HeaderMap.put("createDocDate", timestamp);
			HeaderMap.put("lastUpdateUserId",
					saleOrderHeader.get("lastUpdateUserId"));
			HeaderMap.put("lastUpdateDocDate", timestamp);
			HeaderMap.put("memberId", saleOrderHeader.get("memberId"));
			HeaderMap.put("primeAmount", saleOrderHeader.get("primeAmount"));
			HeaderMap.put("rebateAmount", saleOrderHeader.get("rebateAmount"));
			HeaderMap.put("preCollectionAmount",
					saleOrderHeader.get("preCollectionAmount"));
			HeaderMap.put("collectionAmount",
					saleOrderHeader.get("collectionAmount"));
			HeaderMap.put("fundStatus", saleOrderHeader.get("fundStatus"));
			HeaderMap.put("warehouseStatus",
					saleOrderHeader.get("warehouseStatus"));
			HeaderMap.put("returnStatus", saleOrderHeader.get("returnStatus"));
			HeaderMap.put("memberPointAmount",
					saleOrderHeader.get("memberPointAmount"));
			HeaderMap.put("salesId", saleOrderHeader.get("salesId"));
			HeaderMap.put("logisticsAddress",
					saleOrderHeader.get("logisticsAddress"));
			HeaderMap.put("collectionStoreId",
					saleOrderHeader.get("collectionStoreId"));
			HeaderMap.put("deliveryStoreId",
					saleOrderHeader.get("deliveryStoreId"));
			HeaderMap.put("approvalUserId",
					saleOrderHeader.get("approvalUserId"));
			HeaderMap.put("approvalDate", saleOrderHeader.get("approvalDate"));
			HeaderMap.put("isSync", saleOrderHeader.get("isSync"));
			HeaderMap.put("movementTypeId",
					saleOrderHeader.get("movementTypeId"));//
			HeaderMap.put("printNum", saleOrderHeader.get("printNum"));
			HeaderMap.put("attrName1", saleOrderHeader.get("attrName1"));
			HeaderMap.put("attrName2", saleOrderHeader.get("attrName2"));
			HeaderMap.put("attrName3", saleOrderHeader.get("attrName3"));
			HeaderMap.put("attrName4", saleOrderHeader.get("attrName4"));
			HeaderMap.put("attrName5", saleOrderHeader.get("attrName5"));
			HeaderMap.put("attrName6", saleOrderHeader.get("attrName6"));
			HeaderMap.put("attrName7", saleOrderHeader.get("attrName7"));
			HeaderMap.put("attrName8", saleOrderHeader.get("attrName8"));
			HeaderMap.put("attrName9", saleOrderHeader.get("attrName9"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return HeaderMap;
	}

	/*
	 * 解锁传数据
	 */
	public static Map<String, Object> getDtlMap(DispatchContext dctx,
			GenericValue saleOrderDtl) {
		Map<String, Object> SaleOrderDtlMap = FastMap.newInstance();
		// GenericValue SsaleOrderDtl = null;
		try {
			SaleOrderDtlMap.put("docId", saleOrderDtl.get("docId"));// baseentry
			SaleOrderDtlMap.put("lineNo", saleOrderDtl.get("lineNo"));//
			SaleOrderDtlMap.put("lineNoBaseEntry",
					saleOrderDtl.get("lineNoBaseEntry"));
			SaleOrderDtlMap.put("productId", saleOrderDtl.get("productId"));
			SaleOrderDtlMap.put("productName", saleOrderDtl.get("productName"));
			SaleOrderDtlMap.put("barCodes", saleOrderDtl.get("barCodes"));
			SaleOrderDtlMap.put("extNo", saleOrderDtl.get("extNo"));
			SaleOrderDtlMap.put("sequenceId", saleOrderDtl.get("serialNo"));//
			SaleOrderDtlMap.put("unitPrice", saleOrderDtl.get("unitPrice"));
			SaleOrderDtlMap.put("rebatePrice", saleOrderDtl.get("rebatePrice"));
			SaleOrderDtlMap.put("quantity", saleOrderDtl.get("quantity"));
			SaleOrderDtlMap.put("warehouseQuantity",
					saleOrderDtl.get("warehouseQuantity"));
			SaleOrderDtlMap.put("lockedQuantity",
					saleOrderDtl.get("lockedQuantity"));
			SaleOrderDtlMap.put("returnQuantity",
					saleOrderDtl.get("returnQuantity"));
			SaleOrderDtlMap.put("rebateAmount",
					saleOrderDtl.get("rebateAmount"));
			SaleOrderDtlMap.put("bomAmount", saleOrderDtl.get("bomAmount"));
			SaleOrderDtlMap.put("memo", saleOrderDtl.get("memo"));
			SaleOrderDtlMap.put("facilityId", saleOrderDtl.get("facilityId"));
			SaleOrderDtlMap.put("productSalesPolicyId",
					saleOrderDtl.get("productSalesPolicyId"));
			SaleOrderDtlMap.put("productSalesPolicyNo",
					saleOrderDtl.get("productSalesPolicyNo"));
			SaleOrderDtlMap.put("salesId", saleOrderDtl.get("salesId"));
			SaleOrderDtlMap.put("memberPoint", saleOrderDtl.get("memberPoint"));
			SaleOrderDtlMap.put("isMainProduct",
					saleOrderDtl.get("isMainProduct"));
			SaleOrderDtlMap.put("bomId", saleOrderDtl.get("bomId"));
			SaleOrderDtlMap.put("isGift", saleOrderDtl.get("isGift"));
			SaleOrderDtlMap.put("isSequence", saleOrderDtl.get("isSequence"));
			SaleOrderDtlMap.put("invoiceNo", saleOrderDtl.get("invoiceNo"));
			SaleOrderDtlMap.put("approvalUserId",
					saleOrderDtl.get("approvalUserId"));
			SaleOrderDtlMap.put("baseEntry", saleOrderDtl.get("baseEntry"));
			SaleOrderDtlMap.put("attrName1", saleOrderDtl.get("attrName1"));
			SaleOrderDtlMap.put("attrName2", saleOrderDtl.get("attrName2"));
			SaleOrderDtlMap.put("attrName3", saleOrderDtl.get("attrName3"));
			SaleOrderDtlMap.put("attrName4", saleOrderDtl.get("attrName4"));
			SaleOrderDtlMap.put("attrName5", saleOrderDtl.get("attrName5"));
			SaleOrderDtlMap.put("attrName6", saleOrderDtl.get("attrName6"));
			SaleOrderDtlMap.put("attrName7", saleOrderDtl.get("attrName7"));
			SaleOrderDtlMap.put("attrName8", saleOrderDtl.get("attrName8"));
			SaleOrderDtlMap.put("attrName9", saleOrderDtl.get("attrName9"));
		} catch (Exception e) {
			e.printStackTrace();

		}
		return SaleOrderDtlMap;
	}

}