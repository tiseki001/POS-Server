package org.ofbiz.face.webservice;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.serialize.ErpFaceModelMuti;
import org.ofbiz.entity.serialize.XmlConverUtil;
import org.ofbiz.face.Test;
import org.ofbiz.face.erpface.ErpFaceModelUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * @author Administrator
 *
 */
public class FaceService {
	
	

	/**
	 * @see 输出前处理
	 * @param xml
	 * @return
	 */
	private static String facePreOutput(String xml){
		return xml;
	}
	
	/**
	 * @see 头行结构[单键关联]出口
	 * @param Header
	 * @param Line
	 * @param whereCondition
	 * @return
	 */
	private static String selectEntitiesList(String Header, String Line, String whereCondition) {
		Set<String> key = FastSet.newInstance();
		key.add("entityID");
		List<String> orderBy = FastList.newInstance();
		orderBy.addAll(key);

		List<GenericValue> DocHeader = FaceServiceWorker.getEntities(Header, whereCondition, orderBy);
		orderBy.add("lineNo");
		List<GenericValue> DocLine = FaceServiceWorker.getEntities(Line, whereCondition, orderBy);
		
		Map<String, Object> itemMaps = FastMap.newInstance();
		itemMaps.put("DocLine", DocLine);

		List<ErpFaceModelMuti> modelMutis = ErpFaceModelUtil.ErpFaceMap2Model(DocHeader, itemMaps, key);
		
		String xmlMuti = "";
		try {
			xmlMuti = XmlConverUtil.erpMutiReusultToXml(modelMutis);
			//System.out.println(xmlMuti);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return facePreOutput(xmlMuti);
	}
	
	/**
	 * @see [销售订单+收款]&[退货订单+付款]出口
	 * @see 接口条件：
	 * @see 单据日时（起始条件指定）；
	 * @see 最后更新时间（起始条件指定）；
	 * @see 门店ID；
	 * @see 单据编号
	 * @param whereCondition
	 * @return
	 */
	public static String selectSalesOrderIncomingPaymentList(String orderType,String whereCondition) {
		System.out.println("selectOrderPaymentList");
		Set<String> key = null;
		List<String> orderBy = null;
		List<GenericValue> DocHeader = null;
		List<GenericValue> DocLine = null;
		Map<String, Object> itemMaps = null;
		List<ErpFaceModelMuti> modelMutis = null;
		
		if(UtilValidate.isEmpty(orderType)) orderType = "SALES AND RETURNS";
		if(orderType.toUpperCase().contains("SALES")){
			// 销售订单+收款
			key = FastSet.newInstance();
			key.add("entityID");
			orderBy = FastList.newInstance();
			orderBy.addAll(key);
			DocHeader = FaceServiceWorker.getEntities("ErpIFSalesOrderHeader", whereCondition, orderBy);
			for (GenericValue Entity : DocHeader) {
				Entity.put("orderType", "Sales");
			}
			orderBy.add("lineNo");
			DocLine = FaceServiceWorker.getEntities("ErpIFIncomingPaymentLine", whereCondition, orderBy);
			itemMaps = FastMap.newInstance();
			itemMaps.put("DocLine", DocLine);
			modelMutis = ErpFaceModelUtil.ErpFaceMap2Model(DocHeader, itemMaps, key);

		}
		if(orderType.toUpperCase().contains("RETURNS")){
			// 退货订单+付款
			key = FastSet.newInstance();
			key.add("entityID");
			orderBy = FastList.newInstance();
			orderBy.addAll(key);
			DocHeader = FaceServiceWorker.getEntities("ErpIFReturnOrderHeader", whereCondition, orderBy);
			for (GenericValue Entity : DocHeader) {
				Entity.put("orderType", "Returns");
			}
			orderBy.add("lineNo");
			DocLine = FaceServiceWorker.getEntities("ErpIFOutgoingPaymentLine", whereCondition, orderBy);
			itemMaps = FastMap.newInstance();
			itemMaps.put("DocLine", DocLine);
			if(modelMutis == null){
				modelMutis = ErpFaceModelUtil.ErpFaceMap2Model(DocHeader, itemMaps, key);
			}else{
				modelMutis.addAll(ErpFaceModelUtil.ErpFaceMap2Model(DocHeader, itemMaps, key));
			}
		}
		String xmlMuti = "";
		try {
			xmlMuti = XmlConverUtil.erpMutiReusultToXml(modelMutis);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return facePreOutput(xmlMuti);
	}

	/**
	 * @see [销售订单+出库]&[退货订单+入库]出口
	 * @see 接口条件：
	 * @see 单据日时（起始条件指定）；
	 * @see 最后更新时间（起始条件指定）；
	 * @see 门店ID；
	 * @see 单据编号
	 * @param whereCondition
	 * @return
	 */
	public static String selectSalesOrdeDeliveryList(String orderType,String whereCondition) {
		System.out.println("selectOrdeDeliveryList");
		Set<String> key = null;
		List<String> orderBy = null;
		List<GenericValue> DocHeader = null;
		List<GenericValue> DocLine = null;
		Map<String, Object> itemMaps = null;
		List<ErpFaceModelMuti> modelMutis = null;
		
		if(UtilValidate.isEmpty(orderType)) orderType = "SALES AND RETURNS";
		if(orderType.toUpperCase().contains("SALES")){
			// 销售订单+出库
			key = FastSet.newInstance();
			key.add("entityID");
			orderBy = FastList.newInstance();
			orderBy.addAll(key);
			DocHeader = FaceServiceWorker.getEntities("ErpIFSalesOrderHeader", whereCondition, orderBy);
			for (GenericValue Entity : DocHeader) {
				Entity.put("orderType", "Sales");
			}
			orderBy.add("lineNo");
			DocLine = FaceServiceWorker.getEntities("ErpIFDeliveryLine", whereCondition, orderBy);
			itemMaps = FastMap.newInstance();
			itemMaps.put("DocLine", DocLine);
			modelMutis = ErpFaceModelUtil.ErpFaceMap2Model(DocHeader, itemMaps, key);
		}
		if(orderType.toUpperCase().contains("RETURNS")){
			// 退货订单+入库
			key = FastSet.newInstance();
			key.add("entityID");
			orderBy = FastList.newInstance();
			orderBy.addAll(key);
			DocHeader = FaceServiceWorker.getEntities("ErpIFReturnOrderHeader", whereCondition, orderBy);
			for (GenericValue Entity : DocHeader) {
				Entity.put("orderType", "Returns");
			}
			orderBy.add("lineNo");
			DocLine = FaceServiceWorker.getEntities("ErpIFRetrnGoodsReceiptLine", whereCondition, orderBy);
			DocLine = FaceServiceWorker.changeDocLine(DocLine);
			itemMaps = FastMap.newInstance();
			itemMaps.put("DocLine", DocLine);
			if(modelMutis == null){
				modelMutis = ErpFaceModelUtil.ErpFaceMap2Model(DocHeader, itemMaps, key);
			}else{
				modelMutis.addAll(ErpFaceModelUtil.ErpFaceMap2Model(DocHeader, itemMaps, key));
			}
		}
		String xmlMuti = "";
		try {
			xmlMuti = XmlConverUtil.erpMutiReusultToXml(modelMutis);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return facePreOutput(xmlMuti);		
	}

	/**
	 * @see 入库出口
	 * @see 接口条件：
	 * @see 单据日时（起始条件指定）；
	 * @see 最后更新时间（起始条件指定）；
	 * @see 门店ID；
	 * @see 单据编号；
	 * @see 指令单编号
	 * @param whereCondition
	 * @return
	 */
	public static String selectGoodsReceiptList(String whereCondition) {
		System.out.println("selectGoodsReceiptList");
		return selectEntitiesList("ErpIFGoodsReceiptByCommandHeader",
				"ErpIFGoodsReceiptByCommandLine",whereCondition);
	}
	
	/**
	 * @see 出库出口
	 * @see 接口条件：
	 * @see 单据日时（起始条件指定）；
	 * @see 最后更新时间（起始条件指定）；
	 * @see 门店ID；
	 * @see 单据编号；
	 * @see 指令单编号
	 * @param whereCondition
	 * @return
	 */
	/*public static Map<String,Object> selectGoodsIssueList(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		System.out.println("selectGoodsIssueList");
		Map<String, Object> resultMap=FastMap.newInstance();
		String xmlString= selectEntitiesList("ErpIFGoodsIssueByCommandHeader",
					"ErpIFGoodsIssueByCommandLine",context.get("whereCondition").toString());
		resultMap.put("xmlString", xmlString);
	 return	resultMap;

	}*/
	public static String selectGoodsIssueList(String whereCondition) {
		System.out.println("selectGoodsIssueList");
		return selectEntitiesList("ErpIFGoodsIssueByCommandHeader",
				"ErpIFGoodsIssueByCommandLine",whereCondition);

	}

	/**
	 * 预订单
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	/*
	public static String selectPreOrder(String whereCondition) {
		System.out.println("selectPreOrder");
		return selectEntitiesList("ERPPreOrderHeaderView", 
				"ERPPreOrderDtlView", whereCondition);

	}*/
	/**
	 * 预订单 + 退订单
	 * 
	 * @param orderType
	 * @param whereCondition
	 * @return xmlString
	 */
	public static String selectPreOrder(String orderType,String whereCondition) {
		System.out.println("selectPreOrder");
		Set<String> key = null;
		List<String> orderBy = null;
		List<GenericValue> DocHeader = null;
		List<GenericValue> DocLine = null;
		Map<String, Object> itemMaps = null;
		List<ErpFaceModelMuti> modelMutis = null;
		
		if(UtilValidate.isEmpty(orderType)) orderType = "SALES AND RETURNS";
		if(orderType.toUpperCase().contains("SALES")){
			// 预订单
			key = FastSet.newInstance();
			key.add("entityID");
			orderBy = FastList.newInstance();
			orderBy.addAll(key);
			DocHeader = FaceServiceWorker.getEntities("ERPPreOrderHeaderView", whereCondition, orderBy);
			for (GenericValue Entity : DocHeader) {
				Entity.put("orderType", "Sales");
			}
			orderBy.add("lineNo");
			DocLine = FaceServiceWorker.getEntities("ERPPreOrderDtlView", whereCondition, orderBy);
			DocLine = FaceServiceWorker.rebatePrice(DocLine);
			itemMaps = FastMap.newInstance();
			itemMaps.put("DocLine", DocLine);
			modelMutis = ErpFaceModelUtil.ErpFaceMap2Model(DocHeader, itemMaps, key);
		}
		if(orderType.toUpperCase().contains("RETURNS")){
			// 退订单
			key = FastSet.newInstance();
			key.add("entityID");
			orderBy = FastList.newInstance();
			orderBy.addAll(key);
			DocHeader = FaceServiceWorker.getEntities("ERPBackOrderHeaderView", whereCondition, orderBy);
			for (GenericValue Entity : DocHeader) {
				Entity.put("orderType", "Returns");
			}
			orderBy.add("lineNo");
			DocLine = FaceServiceWorker.getEntities("ERPBackOrderDtlView", whereCondition, orderBy);
			DocLine = FaceServiceWorker.rebatePrice(DocLine);
			itemMaps = FastMap.newInstance();
			itemMaps.put("DocLine", DocLine);
			if(modelMutis == null){
				modelMutis = ErpFaceModelUtil.ErpFaceMap2Model(DocHeader, itemMaps, key);
			}else{
				modelMutis.addAll(ErpFaceModelUtil.ErpFaceMap2Model(DocHeader, itemMaps, key));
			}
		}
		String xmlMuti = "";
		try {
			xmlMuti = XmlConverUtil.erpMutiReusultToXml(modelMutis);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return facePreOutput(xmlMuti);		
	}
	
	/*
	public static String selectPreCollectionOrder(String whereCondition) {
		System.out.println("selectPreCollectionOrder");
		return selectEntitiesList("ERPPreCollectionOrderHeaderView", 
				"ERPPreCollectionOrderDtlView", whereCondition);

	}*/
	/**
	 * 预定金 + 退定金
	 * 
	 * @param orderType
	 * @param whereCondition
	 * @return xmlString
	 */
	public static String selectPreCollectionOrder(String orderType,String whereCondition) {
		System.out.println("selectPreCollectionOrder");
		Set<String> key = null;
		List<String> orderBy = null;
		List<GenericValue> DocHeader = null;
		List<GenericValue> DocLine = null;
		Map<String, Object> itemMaps = null;
		List<ErpFaceModelMuti> modelMutis = null;
		
		if(UtilValidate.isEmpty(orderType)) orderType = "SALES AND RETURNS";
		if(orderType.toUpperCase().contains("SALES")){
			// 预定金
			key = FastSet.newInstance();
			key.add("entityID");
			orderBy = FastList.newInstance();
			orderBy.addAll(key);
			DocHeader = FaceServiceWorker.getEntities("ERPPreCollectionOrderHeaderView", whereCondition, orderBy);
			for (GenericValue Entity : DocHeader) {
				Entity.put("orderType", "Sales");
			}
			orderBy.add("lineNo");
			DocLine = FaceServiceWorker.getEntities("ERPPreCollectionOrderDtlView", whereCondition, orderBy);
			itemMaps = FastMap.newInstance();
			itemMaps.put("DocLine", DocLine);
			modelMutis = ErpFaceModelUtil.ErpFaceMap2Model(DocHeader, itemMaps, key);
		}
		if(orderType.toUpperCase().contains("RETURNS")){
			// 退定金
			key = FastSet.newInstance();
			key.add("entityID");
			orderBy = FastList.newInstance();
			orderBy.addAll(key);
			DocHeader = FaceServiceWorker.getEntities("ERPPPreRefundOrderHeaderView", whereCondition, orderBy);
			for (GenericValue Entity : DocHeader) {
				Entity.put("orderType", "Returns");
			}
			orderBy.add("lineNo");
			DocLine = FaceServiceWorker.getEntities("ERPPreRefundOrderDtlView", whereCondition, orderBy);
			itemMaps = FastMap.newInstance();
			itemMaps.put("DocLine", DocLine);
			if(modelMutis == null){
				modelMutis = ErpFaceModelUtil.ErpFaceMap2Model(DocHeader, itemMaps, key);
			}else{
				modelMutis.addAll(ErpFaceModelUtil.ErpFaceMap2Model(DocHeader, itemMaps, key));
			}
		}
		String xmlMuti = "";
		try {
			xmlMuti = XmlConverUtil.erpMutiReusultToXml(modelMutis);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return facePreOutput(xmlMuti);		
	}
	
	/**
	 * 调拨出库
	 * 
	 * @param dctx
	 * @param context
	 * @return xmlString
	 */
	public static String selectDelivery(String whereCondition) {
		return selectEntitiesList("ERPDeliveryDocHeaderView",
				"ERPDeliveryItemDtlView", whereCondition);
	}
	/**
	 * 调拨入库
	 * 
	 * @param dctx
	 * @param context
	 * @return xmlString
	 */
	public static String selectDeliveryReceiveList(String whereCondition) {
		return selectEntitiesList("ERPReceiveDocHeaderView",
				"ERPReceiveItemDtlView", whereCondition);
	}
	/**
	 * 调拨指令
	 * 
	 * @param dctx
	 * @param context
	 * @return xmlString
	 */
	public static String selectReceiveCommandList(String whereCondition) {
		return selectEntitiesList("ERPReceiveCommandHeaderView",
				"ERPReceiveItemCommandDtlView", whereCondition);
	}

	/**
	 * 门店要货
	 * 
	 * @param dctx
	 * @param context
	 * @return xmlString
	 */
	public static String selectReplenishment(String whereCondition) {
		return selectEntitiesList("ERPReplenishmentHeaderView",
				"ERPReplenishmentItemDtlView", whereCondition);
	}
	
	/**
	 * @see 运营商业务出口
	 * @param whereCondition
	 * @return
	 */
	public static String selectOperatorsBusinessList(String whereCondition) {
		System.out.println("selectOperatorsBusinessList");
		Set<String> key = null;
		List<String> orderBy = null;
		List<GenericValue> DocHeader = null;
		String xml = "";
		
		try {
			key = FastSet.newInstance();
			key.add("openingDate");
			orderBy = FastList.newInstance();
			orderBy.addAll(key);
			DocHeader = FaceServiceWorker.getEntities("ErpIFOptBusiness", whereCondition, orderBy);
			DocHeader.addAll(FaceServiceWorker.getEntities("ErpIFSalesOptBnss", whereCondition, orderBy));
			xml = XmlConverUtil.listtoXml(DocHeader);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return facePreOutput(xml);
	}
	
	public static String selectPaymentInOrder(String whereCondition) {
		return selectEntitiesList("PaymentInOrderHeaderView",
				"PaymentInOrderDtlView", whereCondition);
	}
	/**
	 * 本地soap测试服务
	 * @param dctx
	 * @param context
	 * @return
	 * @throws GenericEntityException 
	 */
	public static Map<String, Object> testERPServer(DispatchContext dctx,
			Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		//selectSalesOrderIncomingPaymentList("", ""); //收款退款
		System.out.println(selectDelivery(""));	// 调拨出库
		Test.fileWriter(selectDelivery(""));
		//selectDeliveryReceiveList("");	// 调拨入库
		//selectGoodsReceiptList("");	//杂入
		//selectGoodsIssueList(""); 	// 杂出
		//return ServiceUtil.returnSuccess();
//		String whereCondition = (String)context.get("whereCondition");
//		String xml=selectEntitiesList("PaymentInOrderHeaderView",
//				"PaymentInOrderDtlView", whereCondition);
//		Map<String, Object> result= FastMap.newInstance();
//		result.put("xml", xml);
//		return result;
		//System.out.println(selectSalesOrdeDeliveryList("sales", "header.doc_id = 'S1412241755012000000012'"));
	    
		return ServiceUtil.returnSuccess();
		
	}

}
