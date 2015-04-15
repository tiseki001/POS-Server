package org.ofbiz.face.erpface;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONSerializer;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.delivery.webService.DeliveryWebWork;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.face.EntityImport;
import org.ofbiz.face.FaceConfig;
import org.ofbiz.face.FaceConfig.FaceConfigInfo;
import org.ofbiz.face.log.ErpFaceLogModel;
import org.ofbiz.face.log.InterFaceLog;
import org.ofbiz.face.webservice.ErpServiceUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.util.ConvertUtil;


/**
 * 数据下载服务类
 * @author Totyu-001
 *
 */
public class FaceDownloadServer {
	
	public static Map<String, Object> storeToPos(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> entityValue = FastMap.newInstance();
		List xmltoList = FastList.newInstance();
		
		FaceConfigInfo info = FaceConfig.getFaceConfigInfo("ebs-store-attr");
		ErpFaceLogModel Model = new ErpFaceLogModel(info.model,info.event,"","","","");
		//获取上次同步时间
		String syncTime= SyncTime.getLastTime(Model, delegator);
		try {
			xmltoList = FaceDownloadWorker.getXmltoList(delegator, info, syncTime, "P_STORE_CODE");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Model.setResult("N");
			Model.setState("Y");
			Model.setResultInfo(e.getMessage());
			InterFaceLog.saveLog(Model, delegator);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Timestamp timestamp = ConvertUtil.convertStringToTimeStamp(
				syncTime.replace("T", " "));
		Timestamp timestampt = null;
		for(Object o : xmltoList){
			Map<String,String> map = (Map<String, String>) o;
			Model.setRecordId(map.get("FLEX_VALUE").toString());
			//更新最近时间
			timestampt = ErpServiceUtil.convertStringToTimeStamp(map.get("LAST_UPDATE_DATE"));
			if (timestampt.getTime() > timestamp.getTime()) {
				timestamp = timestampt;
			}
			entityValue.clear();
			FaceDownloadWorker.deleteProductStoreAttr(delegator, map.get("FLEX_VALUE"));
			try {
				TransactionUtil.begin();
				if (UtilValidate.isNotEmpty(map.get("STORE_TYPE"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "TYPE");
					entityValue.put("attrValue", map.get("STORE_TYPE"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("ETYPE"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "ETYPE");
					entityValue.put("attrValue", map.get("ETYPE"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("STORE_LEVEL"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "LEVEL");
					entityValue.put("attrValue", map.get("STORE_LEVEL"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("CLASS1"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "CLASS1");
					entityValue.put("attrValue", map.get("CLASS1"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("CLASS2"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "CLASS2");
					entityValue.put("attrValue", map.get("CLASS2"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("MANGEMENT"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "MANGEMENT");
					entityValue.put("attrValue", map.get("MANGEMENT"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("ATTRIBUTE1"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "COMPANY");
					entityValue.put("attrValue", map.get("ATTRIBUTE1"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("COMPANY_ACCOUNT"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "COMACCOUNT");
					entityValue.put("attrValue", map.get("COMPANY_ACCOUNT"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("ACCOUNT"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "ACCOUNT");
					entityValue.put("attrValue", map.get("ACCOUNT"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("BIG_AREA"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "BIGAREA");
					entityValue.put("attrValue", map.get("BIG_AREA"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("SMALL_AREA"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "SMALLAREA");
					entityValue.put("attrValue", map.get("SMALL_AREA"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("COUNTY"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "COUNTY");
					entityValue.put("attrValue", map.get("COUNTY"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("PROVINCE"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "PROVINCE");
					entityValue.put("attrValue", map.get("PROVINCE"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("CITY"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "CITY");
					entityValue.put("attrValue", map.get("CITY"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("CT_AREA"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "CTAREA");
					entityValue.put("attrValue", map.get("CT_AREA"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("CT_LEVEL"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "CTLEVEL");
					entityValue.put("attrValue", map.get("CT_LEVEL"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("CU_AREA"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "CUAREA");
					entityValue.put("attrValue", map.get("CU_AREA"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("CU_LEVEL"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "CULEVEL");
					entityValue.put("attrValue", map.get("CU_LEVEL"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("CM_AREA"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "CMAREA");
					entityValue.put("attrValue", map.get("CM_AREA"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				if (UtilValidate.isNotEmpty(map.get("CM_LEVEL"))) {
					entityValue.put("productStoreId", map.get("FLEX_VALUE"));
					entityValue.put("productStoreAttrTypeId", "CMLEVEL");
					entityValue.put("attrValue", map.get("CM_LEVEL"));
					EntityImport.importEntityOne("ProductStoreAttribute", entityValue, delegator);
				}
				TransactionUtil.commit();
				
				Model.setResult("Y");
				Model.setState("Y");
				Model.setResultInfo("success");
				InterFaceLog.saveLog(Model, delegator);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					TransactionUtil.rollback();
				} catch (GenericTransactionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Model.setResult("N");
				Model.setState("Y");
				Model.setResultInfo(e.getMessage());
				InterFaceLog.saveLog(Model, delegator);
			}
		}
		SyncTime.saveLastTime(Model, timestamp, delegator);
		return ServiceUtil.returnSuccess();
	}
	
	/**
	 * 物料下载
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> productToPos(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> entityValue = FastMap.newInstance();
		List xmltoList = FastList.newInstance();
		
		FaceConfigInfo info = FaceConfig.getFaceConfigInfo("ebs-product");
		ErpFaceLogModel Model = new ErpFaceLogModel(info.model,info.event,"","","","");
		//获取上次同步时间
		String syncTime= SyncTime.getLastTime(Model, delegator);
		try {
			xmltoList = FaceDownloadWorker.getXmltoList(delegator, info, syncTime, "P_ITEM_ID");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Model.setResult("N");
			Model.setState("Y");
			Model.setResultInfo(e.getMessage());
			InterFaceLog.saveLog(Model, delegator);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Timestamp timestamp = ConvertUtil.convertStringToTimeStamp(
				syncTime.replace("T", " "));
		Timestamp timestampt = null;
		for(Object o : xmltoList){
			Map<String,String> map = (Map<String, String>) o;
			Model.setRecordId(map.get("ITEM_ID").toString());
			//更新最近时间
			timestampt = ErpServiceUtil.convertStringToTimeStamp(map.get("LAST_UPDATE_DATE"));
			if (timestampt.getTime() > timestamp.getTime()) {
				timestamp = timestampt;
			}
			entityValue.clear();
			try {
				TransactionUtil.begin();
				entityValue.put("productId", map.get("ITEM_CODE"));
				entityValue.put("productTypeId", "FINISHED_GOOD");
				entityValue.put("internalName", map.get("DESCRIPTION"));
				entityValue.put("productName", map.get("DESCRIPTION"));
				entityValue.put("config", map.get("LONG_DESCRIPTION"));
				entityValue.put("requireInventory", map.get("STOCK_ENABLED_FLAG"));
				entityValue.put("isSequence", map.get("SN_CONTROL_FLAG"));
				entityValue.put("sequenceLength", map.get("SN_LENGTH"));
				EntityImport.importEntityOne("Product", entityValue, delegator);
				TransactionUtil.commit();
				
				Model.setResult("Y");
				Model.setState("Y");
				Model.setResultInfo("success");
				InterFaceLog.saveLog(Model, delegator);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					TransactionUtil.rollback();
				} catch (GenericTransactionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Model.setResult("N");
				Model.setState("Y");
				Model.setResultInfo(e.getMessage());
				InterFaceLog.saveLog(Model, delegator);
			}
		}
		SyncTime.saveLastTime(Model, timestamp, delegator);
		return ServiceUtil.returnSuccess();
	}
	/**
	 * 物料特征下载
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> featureToPos(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> entityValue = FastMap.newInstance();
		List xmltoList = FastList.newInstance();
		
		FaceConfigInfo info = FaceConfig.getFaceConfigInfo("ebs-feature");
		ErpFaceLogModel Model = new ErpFaceLogModel(info.model,info.event,"","","","");
		//获取上次同步时间
		String syncTime= SyncTime.getLastTime(Model, delegator);
		try {
			xmltoList = FaceDownloadWorker.getXmltoList(delegator, info, syncTime, "P_FEATURE_ID");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Model.setResult("N");
			Model.setState("Y");
			Model.setResultInfo(e.getMessage());
			InterFaceLog.saveLog(Model, delegator);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Timestamp timestamp = ConvertUtil.convertStringToTimeStamp(
				syncTime.replace("T", " "));
		Timestamp timestampt = null;
		for(Object o : xmltoList){
			Map<String,String> map = (Map<String, String>) o;
			Model.setRecordId(map.get("FEATURE_ID").toString());
			//更新最近时间
			timestampt = ErpServiceUtil.convertStringToTimeStamp(map.get("LAST_UPDATE_DATE"));
			if (timestampt.getTime() > timestamp.getTime()) {
				timestamp = timestampt;
			}
			entityValue.clear();
			try {
				TransactionUtil.begin();
				entityValue.put("productFeatureId", map.get("FEATURE_ID"));
				entityValue.put("productFeatureTypeId", map.get("FEATURE_TYPE"));
				entityValue.put("productFeatureCategoryId", map.get("FEATURE_TYPE"));
				entityValue.put("description", map.get("FEATURE_DESCRIPTION"));
				EntityImport.importEntityOne("ProductFeature", entityValue, delegator);
				TransactionUtil.commit();
				
				Model.setResult("Y");
				Model.setState("Y");
				Model.setResultInfo("success");
				InterFaceLog.saveLog(Model, delegator);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					TransactionUtil.rollback();
				} catch (GenericTransactionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Model.setResult("N");
				Model.setState("Y");
				Model.setResultInfo(e.getMessage());
				InterFaceLog.saveLog(Model, delegator);
			}
		}
		SyncTime.saveLastTime(Model, timestamp, delegator);
		return ServiceUtil.returnSuccess();
	}
	/**
	 * 物料特征appl下载
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> productFeatureToPos(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> entityValue = FastMap.newInstance();
		List xmltoList = FastList.newInstance();
		
		FaceConfigInfo info = FaceConfig.getFaceConfigInfo("ebs-product-feature");
		ErpFaceLogModel Model = new ErpFaceLogModel(info.model,info.event,"","","","");
		//获取上次同步时间
		String syncTime= SyncTime.getLastTime(Model, delegator);
		try {
			xmltoList = FaceDownloadWorker.getXmltoList(delegator, info, syncTime, "P_ITEM_FEATURE_ID");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Model.setResult("N");
			Model.setState("Y");
			Model.setResultInfo(e.getMessage());
			InterFaceLog.saveLog(Model, delegator);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Set<String> key = FastSet.newInstance();
		key.addAll(xmltoList);
		Timestamp timestamp = ConvertUtil.convertStringToTimeStamp(
				syncTime.replace("T", " "));
		Timestamp timestampt = null;
		for(Object o : xmltoList){
			Map<String,String> map = (Map<String, String>) o;
			Model.setRecordId(map.get("ITEM_FEATURE_ID").toString());
			//更新最近时间
			timestampt = ErpServiceUtil.convertStringToTimeStamp(map.get("LAST_UPDATE_DATE"));
			if (timestampt.getTime() > timestamp.getTime()) {
				timestamp = timestampt;
			}
			entityValue.clear();
			boolean b ;
			try {
				b = TransactionUtil.begin();
				List<GenericValue> gvs = delegator.findByAnd("ProductFeatureAppl", UtilMisc.toMap("productId", map.get("ITEM_CODE"),
						"productFeatureId", map.get("FEATURE_ID"),
						"productFeatureApplTypeId", "STANDARD_FEATURE"));
				// 不做重复录入
				if (UtilValidate.isEmpty(gvs)) {
					FaceDownloadWorker.setFeatureTypeThruDate(delegator, map, entityValue);
					entityValue.put("productId", map.get("ITEM_CODE"));
					entityValue.put("productFeatureId", map.get("FEATURE_ID"));
					entityValue.put("productFeatureApplTypeId", "STANDARD_FEATURE");
					entityValue.put("fromDate", 
							ErpServiceUtil.convertStringToTimeStamp(map.get("CREATION_DATE")));
					EntityImport.importEntityOne("ProductFeatureAppl", entityValue, delegator);
				}
				TransactionUtil.commit();
				
				Model.setResult("Y");
				Model.setState("Y");
				Model.setResultInfo("success");
				InterFaceLog.saveLog(Model, delegator);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					TransactionUtil.rollback();
				} catch (GenericTransactionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Model.setResult("N");
				Model.setState("Y");
				Model.setResultInfo(e.getMessage());
				InterFaceLog.saveLog(Model, delegator);
			}
		}
		SyncTime.saveLastTime(Model, timestamp, delegator);
		return ServiceUtil.returnSuccess();
	}
	/**
	 * 基础价格下载
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> priceToPos(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> entityValue = FastMap.newInstance();
		List xmltoList = FastList.newInstance();
		
		FaceConfigInfo info = FaceConfig.getFaceConfigInfo("ebs-price-type");
		ErpFaceLogModel Model = new ErpFaceLogModel(info.model,info.event,"","","","");
		//获取上次同步时间
		String syncTime= SyncTime.getLastTime(Model, delegator);
		try {
			xmltoList = FaceDownloadWorker.getXmltoList(delegator, info, syncTime, "P_PRODUCTID");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Model.setResult("N");
			Model.setState("Y");
			Model.setResultInfo(e.getMessage());
			InterFaceLog.saveLog(Model, delegator);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Timestamp timestamp = ConvertUtil.convertStringToTimeStamp(
				syncTime.replace("T", " "));
		Timestamp timestampt = null;
		for(Object o : xmltoList){
			Map<String,String> map = (Map<String, String>) o;
			Model.setRecordId(map.get("PRODUCTID").toString());
			//更新最近时间
			timestampt = ErpServiceUtil.convertStringToTimeStamp(map.get("LAST_UPDATE_DATE"));
			if (timestampt.getTime() > timestamp.getTime()) {
				timestamp = timestampt;
			}
			entityValue.clear();
			boolean b ;
			try {
				b = TransactionUtil.begin();
				entityValue.put("productId", map.get("PRODUCTID"));
				entityValue.put("productPriceTypeId", map.get("PRODUCTPRICETYPEID"));
				entityValue.put("productPricePurposeId", "PURCHASE");
				entityValue.put("currencyUomId", map.get("CURRENCYUOMID"));
				entityValue.put("productStoreGroupId", "_NA_");
				entityValue.put("price", map.get("PRICE"));
				//更新价格；目的无价格产品生成的price='0'的记录；
				//注：此时所有商品必定会有价格记录
				GenericValue gv = FaceDownloadWorker.updateProductPrice(delegator, map.get("PRODUCTID"), 
						map.get("PRODUCTPRICETYPEID"));
				if (UtilValidate.isNotEmpty(gv)) {
					entityValue.put("fromDate", (Timestamp) gv.get("fromDate"));
				}else {
					entityValue.put("fromDate", 
							ErpServiceUtil.convertStringToTimeStamp(map.get("CREATION_DATE")));
				}
				EntityImport.importEntityOne("ProductPrice", entityValue, delegator);
				TransactionUtil.commit();
				
				Model.setResult("Y");
				Model.setState("Y");
				Model.setResultInfo("success");
				InterFaceLog.saveLog(Model, delegator);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					TransactionUtil.rollback();
				} catch (GenericTransactionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Model.setResult("N");
				Model.setState("Y");
				Model.setResultInfo(e.getMessage());
				InterFaceLog.saveLog(Model, delegator);
			}
		}
		SyncTime.saveLastTime(Model, timestamp, delegator);
		return ServiceUtil.returnSuccess();
	}
	/**
	 * 付款方式下载
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> paymentToPos(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> entityValue = FastMap.newInstance();
		List xmltoList = FastList.newInstance();
		
		FaceConfigInfo info = FaceConfig.getFaceConfigInfo("ebs-payment-method");
		ErpFaceLogModel Model = new ErpFaceLogModel(info.model,info.event,"","","","");
		//获取上次同步时间
		String syncTime= SyncTime.getLastTime(Model, delegator);
		try {
			xmltoList = FaceDownloadWorker.getXmltoList(delegator, info, syncTime, "P_RECEIPT_METHOD_ID");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Model.setResult("N");
			Model.setState("Y");
			Model.setResultInfo(e.getMessage());
			InterFaceLog.saveLog(Model, delegator);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Timestamp timestamp = ConvertUtil.convertStringToTimeStamp(
				syncTime.replace("T", " "));
		Timestamp timestampt = null;
		for(Object o : xmltoList){
			Map<String,String> map = (Map<String, String>) o;
			Model.setRecordId(map.get("ATTRIBUTE8").toString());
			//更新最近时间
			timestampt = ErpServiceUtil.convertStringToTimeStamp(map.get("LAST_UPDATE_DATE"));
			if (timestampt.getTime() > timestamp.getTime()) {
				timestamp = timestampt;
			}
			entityValue.clear();
			boolean b ;
			try {
				b = TransactionUtil.begin();
				entityValue.put("paymentMethodTypeId", map.get("ATTRIBUTE8"));
				entityValue.put("description", map.get("RECEIPT_METHOD_NAME"));
				EntityImport.importEntityOne("PaymentMethodType", entityValue, delegator);
				TransactionUtil.commit();
				
				Model.setResult("Y");
				Model.setState("Y");
				Model.setResultInfo("success");
				InterFaceLog.saveLog(Model, delegator);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					TransactionUtil.rollback();
				} catch (GenericTransactionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Model.setResult("N");
				Model.setState("Y");
				Model.setResultInfo(e.getMessage());
				InterFaceLog.saveLog(Model, delegator);
			}
		}
		SyncTime.saveLastTime(Model, timestamp, delegator);
		return ServiceUtil.returnSuccess();
	}
	/**
	 * 调拨指令下载DeliveryCommand 对占用库存操作
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> deliveryToPos(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		List<Map<String, Object>> listItem = FastList.newInstance();
		Map<String, Object> entityValue = FastMap.newInstance();
		List xmltoList = FastList.newInstance();
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String partyId = (String)userLogin.get("userLoginId");
		FaceConfigInfo info = FaceConfig.getFaceConfigInfo("ebs-delivery-command");
		ErpFaceLogModel Model = new ErpFaceLogModel(info.model,info.event,"","","","");
		//获取上次同步时间
		String syncTime= SyncTime.getLastTime(Model, delegator);
		try {
			xmltoList = FaceDownloadWorker.getCommdXmltoList(delegator, info, syncTime, "P_DOCUMENT_NUM");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Model.setResult("N");
			Model.setState("Y");
			Model.setResultInfo(e.getMessage());
			InterFaceLog.saveLog(Model, delegator);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Timestamp timestamp = ConvertUtil.convertStringToTimeStamp(
				syncTime.replace("T", " "));
		Timestamp timestampt = null;
		for(Object o : xmltoList){
			Map<String,String> map = (Map<String, String>) o;
			Object obj = map.get("LINES");
			List lines = (List<Map>)obj;
			Model.setRecordId(map.get("DOCUMENT_NUM").toString());
			Model.setResult("Y");
			//更新最近时间
			timestampt = ErpServiceUtil.convertStringToTimeStamp(map.get("LAST_UPDATE_DATE"));
			if (timestampt.getTime() > timestamp.getTime()) {
				timestamp = timestampt;
			}
			entityValue.clear();
			listItem.clear();
			String docId = "";
			GenericValue command = null;
			boolean isEmpty = true;
			try {
				if (!FaceDownloadWorker.primaryStore(delegator, 
						map.get("TO_STORE_CODE"), map.get("FROM_STORE_CODE"))) {
					TransactionUtil.begin();
					GenericValue productStore = delegator.findByPrimaryKey("ProductStore", 
							UtilMisc.toMap("productStoreId", map.get("FROM_STORE_CODE")));
					
					entityValue.put("docDate", nowTimestamp);
					entityValue.put("postingDate", nowTimestamp);
					entityValue.put("updateDate", nowTimestamp);
					entityValue.put("extDocNo", map.get("DOCUMENT_NUM"));
					entityValue.put("docStatus", "1");
					entityValue.put("status", "0");
					entityValue.put("memo", map.get("DESCRIPTION") == null ? "" : map.get("DESCRIPTION"));
					
					if ( UtilValidate.isEmpty(productStore) || 
							(UtilValidate.isNotEmpty(productStore) && 
								productStore.get("primaryStoreGroupId").equals("Y"))) {	//调拨收货
						isEmpty =FaceDownloadWorker.isEmptyROCommandFacility(delegator, 
								map.get("FROM_STORE_CODE"), lines);
						if (!isEmpty) {
							docId = ErpServiceUtil.getOrderId(dispatcher, "R", map.get("FROM_STORE_CODE"));
							entityValue.put("docId", docId);
							entityValue.put("productStoreId", map.get("TO_STORE_CODE"));
							entityValue.put("productStoreIdFrom", map.get("FROM_STORE_CODE"));
							entityValue.put("userId", partyId);
							entityValue.put("updateUserId", partyId);
							entityValue.put("movementTypeId", "RO");
							command = FaceDownloadWorker.findCommand(delegator, "ReceiveCommand", map.get("DOCUMENT_NUM"));
							if (UtilValidate.isEmpty(command)) {
								EntityImport.importEntityOne("ReceiveCommand", entityValue, delegator);
								// 行下载
								FaceDownloadWorker.saveReceiveItemcommand(delegator, lines, docId);
								Model.setResultInfo("调拨收货...successfully");
							}else {
								Model.setResultInfo("调拨收货...extDocNo已存在");								
							}
						}else {
							Model.setResult("N");
						}
					}else if (UtilValidate.isNotEmpty(productStore)) {	// 调拨发货
						isEmpty =FaceDownloadWorker.isEmptyDOCommandFacility(delegator, 
								map.get("FROM_STORE_CODE"), map.get("TO_STORE_CODE"), lines);
						if (!isEmpty) {
							boolean isOccupied = FaceDownloadWorker.isOccupiedProductSeq(delegator, lines);
							if (isOccupied) {
								Model.setResultInfo("串号已被占用");
								Model.setResult("N");
							}else {
								docId = ErpServiceUtil.getOrderId(dispatcher, "D", map.get("FROM_STORE_CODE"));
								entityValue.put("docId", docId);
								entityValue.put("productStoreId", map.get("FROM_STORE_CODE"));
								entityValue.put("productStoreIdTo", map.get("TO_STORE_CODE"));
								if (map.get("FROM_STORE_CODE").equals(map.get("TO_STORE_CODE"))) {
									entityValue.put("movementTypeId", "ZO");
								}else {
									entityValue.put("movementTypeId", "DO");
								}
								command = FaceDownloadWorker.findCommand(delegator, "DeliveryCommand", map.get("DOCUMENT_NUM"));
								if (UtilValidate.isEmpty(command)) {
									EntityImport.importEntityOne("DeliveryCommand", entityValue, delegator);
									// 行下载
									listItem = FaceDownloadWorker.saveDeliveryItemcommand(delegator, lines, docId);
									// 占用库存
									DeliveryWebWork.commadDelivery(entityValue, listItem, dctx);
									Model.setResultInfo("调拨发货...successfully");
								}else {
									Model.setResultInfo("调拨发货...extDocNo已存在");
								}
							}
						}else {
							Model.setResultInfo("门店facilityId为null");
							Model.setResult("N");
						}
					}
					TransactionUtil.commit();
				}else {
					Model.setResultInfo("无关门店指令，不做下载");
				}
				Model.setState("Y");
				InterFaceLog.saveLog(Model, delegator);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					TransactionUtil.rollback();
				} catch (GenericTransactionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Model.setResult("N");
				Model.setState("Y");
				Model.setResultInfo(e.getMessage());
				InterFaceLog.saveLog(Model, delegator);
			}
		}
		SyncTime.saveLastTime(Model, timestamp, delegator);
		return ServiceUtil.returnSuccess();
	}
	
	/**
	 * 杂出指令下载
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> outDeliveryToPos(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		List<Map<String, Object>> listItem = FastList.newInstance();
		Map<String, Object> entityValue = FastMap.newInstance();
		List xmltoList = FastList.newInstance();
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String partyId = (String)userLogin.get("userLoginId");
		FaceConfigInfo info = FaceConfig.getFaceConfigInfo("ebs-outDelivery-command");
		ErpFaceLogModel Model = new ErpFaceLogModel(info.model,info.event,"","","","");
		//获取上次同步时间
		String syncTime= SyncTime.getLastTime(Model, delegator);
		try {
			xmltoList = FaceDownloadWorker.getCommdXmltoList(delegator, info, syncTime, "P_DOCUMENT_NUM");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Model.setResult("N");
			Model.setState("Y");
			Model.setResultInfo(e.getMessage());
			InterFaceLog.saveLog(Model, delegator);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Timestamp timestamp = ConvertUtil.convertStringToTimeStamp(
				syncTime.replace("T", " "));
		Timestamp timestampt = null;
		for(Object o : xmltoList){
			Map<String,String> map = (Map<String, String>) o;
			Object obj = map.get("LINES");
			List lines = (List<Map>)obj;
			Model.setRecordId(map.get("DOCUMENT_NUM").toString());
			Model.setResult("Y");
			//更新最近时间
			timestampt = ErpServiceUtil.convertStringToTimeStamp(map.get("LAST_UPDATE_DATE"));
			if (timestampt.getTime() > timestamp.getTime()) {
				timestamp = timestampt;
			}
			entityValue.clear();
			listItem.clear();
			String docId = "";
			GenericValue command = null;
			boolean isEmpty = true;
			try {
				if (!FaceDownloadWorker.primaryStore(delegator, 
						map.get("STORE_CODE"), null)) {
					isEmpty =FaceDownloadWorker.isEmptyCommandFacility(delegator, 
							map.get("STORE_CODE"), lines);
					if (!isEmpty) {
						boolean isOccupied = FaceDownloadWorker.isOccupiedProductSeq(delegator, lines);
						if (isOccupied) {
							Model.setResultInfo("串号已被占用");
							Model.setResult("N");
						}else {
							TransactionUtil.begin();
							docId = ErpServiceUtil.getOrderId(dispatcher, "O", map.get("STORE_CODE"));
							entityValue.put("docId", docId);
							entityValue.put("extDocNo", map.get("DOCUMENT_NUM"));
							entityValue.put("productStoreId", map.get("STORE_CODE"));
							entityValue.put("docDate", nowTimestamp);
							entityValue.put("postingDate", nowTimestamp);
							entityValue.put("updateDate", nowTimestamp);
							entityValue.put("userId", partyId);
							entityValue.put("updateUserId", partyId);
							entityValue.put("docStatus", "1");
							entityValue.put("status", "0");
							entityValue.put("movementTypeId", "OO");
							entityValue.put("memo", map.get("DESCRIPTION") == null ? "" : map.get("DESCRIPTION"));
							command = FaceDownloadWorker.findCommand(delegator, "DeliveryCommand", map.get("DOCUMENT_NUM"));
							if (UtilValidate.isEmpty(command)) {
								EntityImport.importEntityOne("DeliveryCommand", entityValue, delegator);
								// 行下载
								listItem = FaceDownloadWorker.saveOutDeliveryItemcommand(delegator, lines, docId);
								// 占用库存
								DeliveryWebWork.commadDelivery(entityValue, listItem, dctx);
								Model.setResultInfo("杂出指令...successfully");
							}else {
								Model.setResultInfo("杂出指令...extDocNo已存在");
							}
							TransactionUtil.commit();
						}
					}else {
						Model.setResultInfo("门店facilityId为null");
						Model.setResult("N");
					}
				}else {
					Model.setResultInfo("无关门店指令，不做下载");
				}
				Model.setState("Y");
				InterFaceLog.saveLog(Model, delegator);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					TransactionUtil.rollback();
				} catch (GenericTransactionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				Model.setResult("N");
				Model.setState("Y");
				Model.setResultInfo(e.getMessage());
				InterFaceLog.saveLog(Model, delegator);
			}
		}
		SyncTime.saveLastTime(Model, timestamp, delegator);
		return ServiceUtil.returnSuccess();
	}
	
	/**
	 * 杂入指令下载
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> inReceiveToPos(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		Map<String, Object> entity = FastMap.newInstance();
		List headers = FastList.newInstance();
		
		GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String partyId = (String)userLogin.get("userLoginId");
		FaceConfigInfo config = FaceConfig.getFaceConfigInfo("ebs-receive-command");
		ErpFaceLogModel log = new ErpFaceLogModel(config.model,config.event,"","","","");
		//获取上次同步时间
		String lastSyncTime= SyncTime.getLastTime(log, delegator);
		try {
			headers = FaceDownloadWorker.getCommdXmltoList(delegator, config, lastSyncTime, "P_DOCUMENT_NUM");
		} catch (Exception e) {
			e.printStackTrace();
			log.setResult("N");
			log.setState("Y");
			log.setResultInfo(e.getMessage());
			InterFaceLog.saveLog(log, delegator);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Timestamp timestamp = ConvertUtil.convertStringToTimeStamp(
				lastSyncTime.replace("T", " "));
		Timestamp timestampt = null;
		for(Object header : headers){
			Map<String,String> map = (Map<String, String>) header;
			Object obj = map.get("LINES");
			List lines = (List<Map>)obj;
			log.setRecordId(map.get("DOCUMENT_NUM").toString());
			log.setResult("Y");
			//更新最近时间
			timestampt = ErpServiceUtil.convertStringToTimeStamp(map.get("LAST_UPDATE_DATE"));
			if (timestampt.getTime() > timestamp.getTime()) {
				timestamp = timestampt;
			}
			entity.clear();
			String docId = "";
			GenericValue command = null;
			boolean isEmpty = true;
			try {
				if (!FaceDownloadWorker.primaryStore(delegator, 
						map.get("STORE_CODE"), null)) {
					isEmpty =FaceDownloadWorker.isEmptyCommandFacility(delegator, 
							map.get("STORE_CODE"), lines);
					if (!isEmpty) {
						TransactionUtil.begin();
						docId = ErpServiceUtil.getOrderId(dispatcher, "I", map.get("STORE_CODE"));
						entity.put("docId", docId);
						entity.put("extDocNo", map.get("DOCUMENT_NUM"));
						entity.put("productStoreId", map.get("STORE_CODE"));
						entity.put("docDate", nowTimestamp);
						entity.put("postingDate", nowTimestamp);
						entity.put("updateDate", nowTimestamp);
						entity.put("userId", partyId);
						entity.put("updateUserId", partyId);
						entity.put("docStatus", "1");
						entity.put("status", "0");
						entity.put("movementTypeId", "IO");
						entity.put("memo", map.get("DESCRIPTION") == null ? "" : map.get("DESCRIPTION"));
						command = FaceDownloadWorker.findCommand(delegator, "ReceiveCommand", map.get("DOCUMENT_NUM"));
						if (UtilValidate.isEmpty(command)) {
							EntityImport.importEntityOne("ReceiveCommand", entity, delegator);
							// 行下载
							FaceDownloadWorker.saveinReceiveItemcommand(delegator, lines, docId);
							log.setResultInfo("杂入...successfully");
						}else {
							log.setResultInfo("杂入...extDocNo已存在");
						}
						TransactionUtil.commit();
					}else {
						log.setResultInfo("门店facilityId为null");
						log.setResult("N");
					}
				}else {
					log.setResultInfo("无关门店指令，不做下载");
				}
				log.setState("Y");
				InterFaceLog.saveLog(log, delegator);
			} catch (GenericEntityException e) {
				e.printStackTrace();
				try {
					TransactionUtil.rollback();
				} catch (GenericTransactionException e1) {
					e1.printStackTrace();
				}
				log.setResult("N");
				log.setState("Y");
				log.setResultInfo(e.getMessage());
				InterFaceLog.saveLog(log, delegator);
			}
		}
		SyncTime.saveLastTime(log, timestamp, delegator);
		return ServiceUtil.returnSuccess();
	}
	
	/**
	 * 售后受理维护数据下载
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> acceptToPos(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> entityValue = FastMap.newInstance();
		List xmltoList = FastList.newInstance();
		
		FaceConfigInfo info = FaceConfig.getFaceConfigInfo("ebs-accept");
		ErpFaceLogModel Model = new ErpFaceLogModel(info.model,info.event,"","","","");
		//获取上次同步时间
		String syncTime= SyncTime.getLastTime(Model, delegator);
		try {
			xmltoList = FaceDownloadWorker.getCommdXmltoList(delegator, info, syncTime, "P_FLEX_CODE");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Model.setResult("N");
			Model.setState("Y");
			Model.setResultInfo(e.getMessage());
			InterFaceLog.saveLog(Model, delegator);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Timestamp timestamp = ConvertUtil.convertStringToTimeStamp(
				syncTime.replace("T", " "));
		Timestamp timestampt = null;
		for(Object o : xmltoList){
			Map<String,String> map = (Map<String, String>) o;
			Model.setRecordId(map.get("FLEX_CODE").toString());
			//更新最近时间
			timestampt = ErpServiceUtil.convertStringToTimeStamp(map.get("LAST_UPDATE_DATE"));
			if (timestampt.getTime() > timestamp.getTime()) {
				timestamp = timestampt;
			}
			entityValue.clear();
			
			try {
				TransactionUtil.begin();
				entityValue.put("type", map.get("TYPE"));
				entityValue.put("codeId", map.get("FLEX_CODE"));
				entityValue.put("description", map.get("DESCRIPTION"));
				EntityImport.importEntityOne("AfterSaleCode", entityValue, delegator);
				TransactionUtil.commit();
				
				Model.setResult("Y");
				Model.setState("Y");
				Model.setResultInfo("success");
				InterFaceLog.saveLog(Model, delegator);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					TransactionUtil.rollback();
				} catch (GenericTransactionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Model.setResult("N");
				Model.setState("Y");
				Model.setResultInfo(e.getMessage());
				InterFaceLog.saveLog(Model, delegator);
			}
		}
		SyncTime.saveLastTime(Model, timestamp, delegator);
		return ServiceUtil.returnSuccess();
	}
	
	/**
	 * 运营商套餐值下载(取行中相关信息)
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> operationToPos(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> entityValue = FastMap.newInstance();
		List headers = FastList.newInstance();
    	
		FaceConfigInfo config = FaceConfig.getFaceConfigInfo("ebs-operation-business");
		ErpFaceLogModel log = new ErpFaceLogModel(config.model,config.event,"","","","");
		//获取上次同步时间
		String lastSyncTime= SyncTime.getLastTime(log, delegator);
		try {
			headers = FaceDownloadWorker.getCommdXmltoList(delegator, config, lastSyncTime, "P_POLICY_NUMBER");
		} catch (Exception e) {
			e.printStackTrace();
			log.setResult("N");
			log.setState("Y");
			log.setResultInfo(e.getMessage());
			InterFaceLog.saveLog(log, delegator);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Timestamp timestamp = ConvertUtil.convertStringToTimeStamp(
				lastSyncTime.replace("T", " "));
		Timestamp timestampt = null;
		for(Object header : headers){
			Map<String,String> map = (Map<String, String>) header;
			String attrValue = map.get("OPERATING_UNIT");
			Object obj = map.get("LINES");
			List lines = (List<Map>)obj;
			log.setRecordId(map.get("POLICY_NUMBER").toString());
			//更新最近时间
			timestampt = ErpServiceUtil.convertStringToTimeStamp(map.get("LAST_UPDATE_DATE"));
			if (timestampt.getTime() > timestamp.getTime()) {
				timestamp = timestampt;
			}
			entityValue.clear();
			try {
				TransactionUtil.begin();
				for(Object o : lines){
					Map<String,String> mapLines = (Map<String, String>) o;
					entityValue.clear();
					entityValue.put("erpOptSetValueId", mapLines.get("POLICY_CODE"));
					entityValue.put("erpOptSetValueName", mapLines.get("BUSINESSS_TYPE_DSP"));
					entityValue.put("attrValue", attrValue);
					try {
						EntityImport.importEntityOne("ErpOptSetValue", entityValue, delegator);
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				TransactionUtil.commit();
				
				log.setResult("Y");
				log.setState("Y");
				log.setResultInfo("success");
				InterFaceLog.saveLog(log, delegator);
			} catch (GenericEntityException e) {
				e.printStackTrace();
				try {
					TransactionUtil.rollback();
				} catch (GenericTransactionException e1) {
					e1.printStackTrace();
				}
				log.setResult("N");
				log.setState("Y");
				log.setResultInfo(e.getMessage());
				InterFaceLog.saveLog(log, delegator);
			}
		}
		SyncTime.saveLastTime(log, timestamp, delegator);
		return ServiceUtil.returnSuccess();
	}
	
	/**
	 * 门店要货单下载
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> replenishmentItemToPos(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
		Map<String, Object> entityValue = FastMap.newInstance();
		List xmltoList = FastList.newInstance();
		
		FaceConfigInfo info = FaceConfig.getFaceConfigInfo("ebs-replenishment-item");
		ErpFaceLogModel Model = new ErpFaceLogModel(info.model,info.event,"","","","");
		//获取上次同步时间
		String syncTime= SyncTime.getLastTime(Model, delegator);
		try {
			xmltoList = FaceDownloadWorker.getXmltoList(delegator, info, syncTime, "P_DOCUMENT_NUM");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Model.setResult("N");
			Model.setState("Y");
			Model.setResultInfo(e.getMessage());
			InterFaceLog.saveLog(Model, delegator);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Timestamp timestamp = ConvertUtil.convertStringToTimeStamp(
				syncTime.replace("T", " "));
		Timestamp timestampt = null;
		for(Object o : xmltoList){
			Map<String,String> map = (Map<String, String>) o;
			Model.setRecordId(map.get("DOCUMENT_NUM").toString());
			//更新最近时间
			timestampt = ErpServiceUtil.convertStringToTimeStamp(map.get("LAST_UPDATE_DATE"));
			if (timestampt.getTime() > timestamp.getTime()) {
				timestamp = timestampt;
			}
			entityValue.clear();
			boolean b ;
			try {
				b = TransactionUtil.begin();
				entityValue.put("docId", map.get("DOCUMENT_NUM"));
				entityValue.put("lineNo", map.get("LINE_NUM"));
				entityValue.put("erpMemo", map.get("COMMENTS"));
				entityValue.put("attrName1", map.get("QUANTITY"));
				EntityImport.importEntityOne("ReplenishmentItem", entityValue, delegator);
				TransactionUtil.commit();
				
				Model.setResult("Y");
				Model.setState("Y");
				Model.setResultInfo("success");
				InterFaceLog.saveLog(Model, delegator);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					TransactionUtil.rollback();
				} catch (GenericTransactionException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Model.setResult("N");
				Model.setState("Y");
				Model.setResultInfo(e.getMessage());
				InterFaceLog.saveLog(Model, delegator);
			}
		}
		SyncTime.saveLastTime(Model, timestamp, delegator);
		return ServiceUtil.returnSuccess();
	}
	
	/**
	 * 取服务器当前时间给client
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> getNowTime(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		BasePosObject pObject = new BasePosObject();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        format.setLenient(false);  
        String strTime = format.format(UtilDateTime.nowTimestamp());
        pObject.setData(strTime);
        pObject.setFlag("S");
        String strResult = JSONSerializer.toJSON(pObject).toString();
        result.put("jsonOut", strResult);
		return result;
	}
	
}
