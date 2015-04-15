package org.ofbiz.face.erpface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.face.EntityImport;
import org.ofbiz.face.FaceConfig.FaceConfigInfo;
import org.ofbiz.face.erpface.xml.ErpXmlConverUtil;
import org.ofbiz.face.log.ErpFaceLogModel;
import org.ofbiz.face.log.InterFaceLog;
import org.ofbiz.face.webservice.ErpServiceUtil;



/**
 * 数据下载Worker类
 * @author Totyu-001
 *
 */
public class FaceDownloadWorker {
	
	/**
	 * getXmltoList
	 * @param delegator
	 * @param info
	 * @param syncTime
	 * @param failedIdName
	 * @return List
	 * @throws IOException
	 */
	public static List getXmltoList(Delegator delegator, FaceConfigInfo info, String syncTime, String failedIdName) throws IOException {
		List xmltoList = FastList.newInstance();
		ErpFaceLogModel Model = new ErpFaceLogModel(info.model,info.event,"","","","");
		//获取所有同步失败的记录ID
		List<String> failIds = InterFaceLog.getFailData(Model, delegator);
		//根据最后更新时间获取记录
		String rusultXml = "";
		if (UtilValidate.isNotEmpty(syncTime)) {
			rusultXml = RestInvocationBasicAuthWithHeader.postXml_BasicAuth(info.event, info.ebsUrl,UtilMisc.toMap("P_TIMESTAMP", (Object)syncTime), info.userName, info.password);
		}else{
			rusultXml = RestInvocationBasicAuthWithHeader.postXml_BasicAuth(info.event, info.ebsUrl,UtilMisc.toMap("P_TIMESTAMP", (Object)""), info.userName, info.password);
		}
		xmltoList = ErpXmlConverUtil.xmltoList(rusultXml);
		//根据ID获取记录
		if (UtilValidate.isNotEmpty(failIds)) {
			for(String id : failIds){
				rusultXml = RestInvocationBasicAuthWithHeader.postXml_BasicAuth(info.event, info.ebsUrl,UtilMisc.toMap(failedIdName, (Object)id), info.userName, info.password);
				List failList = ErpXmlConverUtil.xmltoList(rusultXml);
				xmltoList.addAll(failList);
			}
		}
		return xmltoList;
	}
	
	/**
	 * getCommdXmltoList
	 * @param delegator
	 * @param info
	 * @param syncTime
	 * @param failedIdName
	 * @return List
	 * @throws IOException
	 */
	public static List getCommdXmltoList(Delegator delegator, FaceConfigInfo info, String syncTime, String failedIdName) throws IOException {
		List xmltoList = FastList.newInstance();
		ErpFaceLogModel Model = new ErpFaceLogModel(info.model,info.event,"","","","");
		//获取所有同步失败的记录ID
		List<String> failIds = InterFaceLog.getFailData(Model, delegator);
		//根据最后更新时间获取记录
		String rusultXml = "";
		if (UtilValidate.isNotEmpty(syncTime)) {
			rusultXml = RestInvocationBasicAuthWithHeader.postXml_BasicAuth(info.event, info.ebsUrl,UtilMisc.toMap("P_TIMESTAMP", (Object)syncTime), info.userName, info.password);
		}else{
			rusultXml = RestInvocationBasicAuthWithHeader.postXml_BasicAuth(info.event, info.ebsUrl,UtilMisc.toMap("P_TIMESTAMP", (Object)""), info.userName, info.password);
		}
		xmltoList = ErpXmlConverUtil.headersAndLinesxmltoList(rusultXml);
		//根据ID获取记录
		if (UtilValidate.isNotEmpty(failIds)) {
			for(String id : failIds){
				rusultXml = RestInvocationBasicAuthWithHeader.postXml_BasicAuth(info.event, info.ebsUrl,UtilMisc.toMap(failedIdName, (Object)id), info.userName, info.password);
				List failList = ErpXmlConverUtil.headersAndLinesxmltoList(rusultXml);
				xmltoList.addAll(failList);
			}
		}
		return xmltoList;
	}
	
	/**
	 * 调拨指令收货行数据下载
	 * 串号管理由触发器完成
	 * @param delegator
	 * @param lines
	 * @param docId
	 */
	public static void saveReceiveItemcommand(Delegator delegator, List lines, String docId) {
		Map<String, Object> entityValue = FastMap.newInstance();
		int lineNo = 0;
		for(Object o : lines){
			lineNo = lineNo + 1;
			Map<String,String> map = (Map<String, String>) o;
			entityValue.clear();
			entityValue.put("docId", docId);
			entityValue.put("baseLineNo", map.get("MO_LINE_ID"));
			entityValue.put("lineNo", Long.valueOf(lineNo));
			entityValue.put("quantity", map.get("QUANTITY"));
			entityValue.put("receiveQuantity", "0");
			entityValue.put("productId", map.get("ITEM_CODE"));
			entityValue.put("extDocDemo", map.get("LINE_SOURCE_TYPE"));
			entityValue.put("isSequence", getSequence(delegator, map.get("ITEM_CODE")));
			entityValue.put("sequenceId", map.get("SERIAL_NUMBER"));
			entityValue.put("facilityId", ErpServiceUtil.getFacilityId(delegator, map.get("TO_ENTREPOT_DESC")));
			entityValue.put("memo", map.get("COMMENTS") == null ? "" : map.get("COMMENTS"));
			
			try {
				EntityImport.importEntityOne("ReceiveItemcommand", entityValue, delegator);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//添加串号入pos库中
			saveProductSeq(delegator, map.get("ITEM_CODE"), map.get("SERIAL_NUMBER"));
		}
	}
	/**
	 * 调拨指令发货行数据下载
	 * @param delegator
	 * @param lines
	 * @param docId
	 */
	public static List<Map<String, Object>> saveDeliveryItemcommand(Delegator delegator, List lines, String docId) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> entityValue = null;
		
		int lineNo = 0;
		for(Object o : lines){
			lineNo = lineNo + 1;
			//Long lineNo = delegator.getNextSeqIdLong("DeliveryItemcommand");
			Map<String,String> map = (Map<String, String>) o;
			entityValue = FastMap.newInstance();
			entityValue.put("docId", docId);
			entityValue.put("baseLineNo", map.get("MO_LINE_ID"));
			entityValue.put("lineNo", Long.valueOf(lineNo));
			entityValue.put("quantity", map.get("QUANTITY"));
			entityValue.put("deliveryQuantity", "0");
			entityValue.put("productId", map.get("ITEM_CODE")); 
			entityValue.put("extDocDemo", map.get("LINE_SOURCE_TYPE"));
			entityValue.put("isSequence", getSequence(delegator, map.get("ITEM_CODE")));
			entityValue.put("sequenceId", map.get("SERIAL_NUMBER"));
			entityValue.put("facilityId", ErpServiceUtil.getFacilityId(delegator, map.get("FROM_ENTREPOT_DESC")));
			entityValue.put("facilityIdTo", ErpServiceUtil.getFacilityId(delegator, map.get("TO_ENTREPOT_DESC")));
			entityValue.put("memo", map.get("COMMENTS")== null ? "" : map.get("COMMENTS"));
			try {
				EntityImport.importEntityOne("DeliveryItemcommand", entityValue, delegator);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//添加串号入pos库中
			//saveProductSeq(delegator, map.get("ITEM_CODE"), map.get("SERIAL_NUMBER"));
			
			list.add(entityValue);
		}
		return list;
	}
	
	/**
	 * 杂出指令行数据下载
	 * @param delegator
	 * @param lines
	 * @param docId
	 */
	public static List<Map<String, Object>> saveOutDeliveryItemcommand(Delegator delegator, List lines, String docId) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> entityValue = null;
		int lineNo = 0;
		for(Object o : lines){
			lineNo = lineNo + 1;
			Map<String,String> map = (Map<String, String>) o;
			entityValue = FastMap.newInstance();
			entityValue.put("docId", docId);
			entityValue.put("baseLineNo", map.get("MO_LINE_ID"));
			entityValue.put("lineNo", Long.valueOf(lineNo));
			entityValue.put("quantity", map.get("QUANTITY"));
			entityValue.put("deliveryQuantity", "0");
			entityValue.put("productId", map.get("ITEM_CODE"));
			entityValue.put("isSequence", getSequence(delegator, map.get("ITEM_CODE")));
			entityValue.put("sequenceId", map.get("SERIAL_NUMBER"));
			entityValue.put("facilityId", ErpServiceUtil.getFacilityId(delegator, map.get("FACILITY_DESC")));
			entityValue.put("memo", map.get("COMMENTS") == null ? "" : map.get("COMMENTS"));
			
			try {
				EntityImport.importEntityOne("DeliveryItemcommand", entityValue, delegator);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//添加串号入pos库中
			//saveProductSeq(delegator, map.get("ITEM_CODE"), map.get("SERIAL_NUMBER"));
			
			list.add(entityValue);
		}
		return list;
	}
	
	/**
	 * 杂入指令行数据下载
	 * 串号管理由触发器完成
	 * @param delegator
	 * @param lines
	 * @param docId
	 */
	public static void saveinReceiveItemcommand(Delegator delegator, List lines, String docId) {
		Map<String, Object> entityValue = FastMap.newInstance();
		int lineNo = 0;
		for(Object o : lines){
			lineNo = lineNo + 1;
			Map<String,String> map = (Map<String, String>) o;
			entityValue.clear();
			entityValue.put("docId", docId);
			entityValue.put("baseLineNo", map.get("MO_LINE_ID"));
			entityValue.put("lineNo", Long.valueOf(lineNo));
			entityValue.put("quantity", map.get("QUANTITY"));
			entityValue.put("receiveQuantity", "0");
			entityValue.put("productId", map.get("ITEM_CODE"));
			entityValue.put("isSequence", getSequence(delegator, map.get("ITEM_CODE")));
			entityValue.put("sequenceId", map.get("SERIAL_NUMBER"));
			entityValue.put("facilityId", ErpServiceUtil.getFacilityId(delegator, map.get("FACILITY_DESC")));
			entityValue.put("memo", map.get("COMMENTS") == null ? "" : map.get("COMMENTS"));
			
			try {
				EntityImport.importEntityOne("ReceiveItemcommand", entityValue, delegator);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//添加串号入pos库中
			saveProductSeq(delegator, map.get("ITEM_CODE"), map.get("SERIAL_NUMBER"));
		}
	}
	
	/**
	 * create deliverCommend save ProductSeq
	 * @param delegator
	 * @param productId
	 * @param seq
	 */
	public static void saveProductSeq(Delegator delegator, String productId, String seq) {
		GenericValue productSeq = null;
		if (UtilValidate.isNotEmpty(productId) && UtilValidate.isNotEmpty(seq)) {
			try {
				productSeq = delegator.findByPrimaryKey("ProductSequence", UtilMisc.toMap("productId", productId, "sequenceId", seq));
				if (UtilValidate.isEmpty(productSeq)) {
					productSeq = delegator.makeValue("ProductSequence", UtilMisc.toMap("productId", productId, 
							"sequenceId", seq,
							"partyId", "10000",
							"isOuts", "N",
							"isOccupied", "N"));
					productSeq.create();
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 运营商套餐值下载(取行中相关信息)
	 * @param delegator
	 * @param lines
	 * @param docId
	 */
	public static void saveOperation(Delegator delegator, List lines) {
		Map<String, Object> entityValue = FastMap.newInstance();
		for(Object o : lines){
			Map<String,String> map = (Map<String, String>) o;
			entityValue.clear();
			entityValue.put("erpOptSetValueId", map.get("POLICY_CODE"));
			entityValue.put("erpOptSetValueName", map.get("BUSINESSS_TYPE_DSP"));
			try {
				EntityImport.importEntityOne("ErpOptSetValue", entityValue, delegator);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 是否串号占用
	 * @param delegator
	 * @param productId
	 * @param seq
	 */
	public static Boolean isOccupiedProductSeq(Delegator delegator, List lines) {
		GenericValue productSeq = null;
		for(Object o : lines){
			Map<String,String> map = (Map<String, String>) o;
			try {
				productSeq = delegator.findByPrimaryKey("ProductSequence", 
						UtilMisc.toMap("productId",  map.get("ITEM_CODE"), 
								"sequenceId", map.get("SERIAL_NUMBER")));
				if (UtilValidate.isNotEmpty(productSeq) && productSeq.get("isOccupied").equals("Y")) {
					return true;
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * update ProductPrice
	 * @param delegator
	 * @param productId
	 * @param seq
	 */
	public static GenericValue updateProductPrice(Delegator delegator, 
			String productId, String productPriceTypeId) {
		List<GenericValue> gvs = null;
		try {
			gvs = delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", productId, 
					"productPriceTypeId", productPriceTypeId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(gvs)) {
			return gvs.get(0);
		}else {
			return null;
		}
	}
	/**
	 * is sequence
	 * @param delegator
	 * @param productId
	 * @param seq
	 */
	public static String getSequence(Delegator delegator, String productId) {
		GenericValue gv = null;
		try {
			gv = delegator.findByPrimaryKey("Product", 
					UtilMisc.toMap("productId", productId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(gv)) {
			if (UtilValidate.isNotEmpty(gv.get("isSequence"))) {
				return gv.get("isSequence").toString();
			}else {
				return "N";
			}
		}else {
			return "";
		}
	}
	/**
	 * 指令存在，则更新itemCommand
	 * @param delegator
	 * @param command
	 * @param extDocNo
	 * @return
	 */
	public static GenericValue findCommand(Delegator delegator, String command, String extDocNo) {
		List<GenericValue> gvs = null;
		try {
			gvs = delegator.findByAnd(command, UtilMisc.toMap("extDocNo", extDocNo));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(gvs)) {
			return gvs.get(0);
		}else {
			return null;
		}
	}
	
	/**
	 * 无关门店的指令不做下载
	 * @param delegator
	 * @param productStoreId
	 * @param productStoreIdTo
	 * @return
	 */
	public static boolean primaryStore(Delegator delegator, String productStoreId, String productStoreIdTo) {
		GenericValue gv = null;
		GenericValue gvTo = null;
		try {
			gv = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
			gvTo = delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", productStoreIdTo));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if ((UtilValidate.isNotEmpty(gv) && gv.get("primaryStoreGroupId").equals("N")) || 
				(UtilValidate.isNotEmpty(gvTo) && gvTo.get("primaryStoreGroupId").equals("N"))) {
			return false;
		}else {
			return true;
		}
	}
	
	/**
	 * command(杂入，杂出)
	 * @param delegator
	 * @param productId
	 * @param seq
	 */
	public static Boolean isEmptyCommandFacility(Delegator delegator, String productStoreId, List lines) {
		boolean facilityId;
		for(Object o : lines){
			Map<String,String> map = (Map<String, String>) o;
			facilityId = isEmptyFacility(delegator, productStoreId, map.get("FACILITY_DESC"));
			if (facilityId) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * command(调拨收货)
	 * 门店 仓库id为null
	 * @param delegator
	 * @param productId
	 * @param seq
	 */
	public static Boolean isEmptyROCommandFacility(Delegator delegator, String productStoreId, List lines) {
		boolean facilityId = false;
		for(Object o : lines){
			Map<String,String> map = (Map<String, String>) o;
			if (!FaceDownloadWorker.primaryStore(delegator, productStoreId, null)) {
				facilityId = isEmptyFacility(delegator, productStoreId, map.get("TO_ENTREPOT_DESC"));
			}
			if (facilityId) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * command(调拨发货)
	 * 门店 仓库id为null
	 * @param delegator
	 * @param productId
	 * @param seq
	 */
	public static Boolean isEmptyDOCommandFacility(Delegator delegator, 
			String productStoreId, String productStoreIdTo, List lines) {
		boolean facilityId = false;
		boolean facilityIdTo = false;
		
		for(Object o : lines){
			Map<String,String> map = (Map<String, String>) o;
			if (!FaceDownloadWorker.primaryStore(delegator, productStoreId, null)) {
				facilityId = isEmptyFacility(delegator, productStoreId, map.get("FROM_ENTREPOT_DESC"));
			}
			if (!FaceDownloadWorker.primaryStore(delegator, productStoreIdTo, null)) {
				facilityIdTo = isEmptyFacility(delegator, productStoreIdTo, map.get("TO_ENTREPOT_DESC"));
			}
			if (facilityId || facilityIdTo) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 仓库mappId ！= null
	 * and
	 * 仓库mappId.startsWith(store_code) == true
	 * @param delegator
	 * @param productStoreId
	 * @param facilityId
	 * @return
	 */
	public static Boolean isEmptyFacility(Delegator delegator, String productStoreId, String entrepotDesc) {
		String facilityId = "";
		facilityId = ErpServiceUtil.getFacilityId(delegator, entrepotDesc);
		if (UtilValidate.isNotEmpty(facilityId)
				&& facilityId.startsWith(productStoreId)) {
			return false;
		}else {
			return true;
		}
	}
	/**
	 * 商品特征对应ProductFeatureTypeid重复下载，
	 * thruDate前一个为sysdate
	 * @param delegator
	 * @param map
	 * @param entityValue
	 * @throws GenericEntityException
	 */
	public static void setFeatureTypeThruDate(Delegator delegator, Map<String,String> map,
			Map<String, Object> entityValue) throws GenericEntityException {
		
		List<GenericValue> productFeatureTypes = delegator.findList("ProductFeatureType", 
				EntityCondition.makeCondition("hasTable", EntityOperator.EQUALS, null), null, null, null, false);
		for (GenericValue featureType : productFeatureTypes) {
			if (map.get("FEATURE_ID").startsWith(featureType.getString("productFeatureTypeId"))) {
				List<GenericValue> gvs = delegator.findByAnd("ProductFeatureAppl", UtilMisc.toMap("productId", map.get("ITEM_CODE"),
						"productFeatureId", map.get("FEATURE_ID")));
				
				List<EntityCondition> andExprs = FastList.newInstance();
				andExprs.add(EntityCondition.makeCondition("productId", 
						EntityOperator.EQUALS, map.get("ITEM_CODE")));
				andExprs.add(EntityCondition.makeCondition("productFeatureId", 
						EntityOperator.LIKE, "%" + featureType.get("productFeatureTypeId") + "%"));
				EntityCondition mainCond = EntityCondition.makeCondition(andExprs,
						EntityOperator.AND);
				
				delegator.storeByCondition("ProductFeatureAppl",
						UtilMisc.toMap("thruDate", UtilDateTime.nowTimestamp()), mainCond);
			}
		}
	}
	
	/**
	 * delete 所需要update门店所有属性
	 * @param delegator
	 * @param productStoreId
	 * @return
	 */
	public static void deleteProductStoreAttr(Delegator delegator, String productStoreId) {
		List<GenericValue> gvs = null;
		try {
			gvs = delegator.findByAnd("ProductStoreAttribute", UtilMisc.toMap("productStoreId", productStoreId));
			delegator.removeAll(gvs);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
