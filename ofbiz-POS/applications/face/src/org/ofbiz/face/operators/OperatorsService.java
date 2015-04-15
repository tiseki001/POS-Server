package org.ofbiz.face.operators;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.common.Constant;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.face.erpface.SyncTime;
import org.ofbiz.face.log.ErpFaceLogModel;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.util.ConvertUtil;


/**
 * 运营商接入信息server
 * 
 * @author Totyu-001
 * 
 */
public class OperatorsService {

	/**
	 * getOperatorList
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> getOperatorList(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();

		String productSalesPolicyId = (String) context
				.get("productSalesPolicyId");
		String valueStr[] = productSalesPolicyId.split("-");
		Map<String, Object> result = FastMap.newInstance();
		List<String> lists = FastList.newInstance();
		List<GenericValue> gvs = null;

		try {
			gvs = delegator.findByAnd("ErpPolicySetValueRelationView", UtilMisc
					.toMap("productSalesPolicyId", valueStr[0],
							"productStoreId",valueStr[1]));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (GenericValue gv : gvs) {
			lists.add(gv.get("erpOptSetValueName") + ": "
					+ gv.get("erpOptSetValueId"));
		}
		result.put("operatorList", lists);
		return result;
	}

	/**
	 * getOperatorListBySalesId
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> getOperatorListBySalesId(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();

		String productSalesPolicyId = (String) context
				.get("productSalesPolicyId");
		Map<String, Object> result = FastMap.newInstance();
		List<String> lists = FastList.newInstance();
		List<GenericValue> gvs = null;

		try {
			gvs = delegator.findByAnd("ErpPolicyRelationView", UtilMisc.toMap(
					"productSalesPolicyId", productSalesPolicyId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (GenericValue gv : gvs) {
			lists.add(gv.get("erpOptSetValueName") + ": "
					+ gv.get("erpOptSetValueId"));
		}
		result.put("operatorList", lists);
		return result;
	}

	/**
	 * upload 运营商与校验信息
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> upload(DispatchContext dctx,
			Map<String, ? extends Object> context) throws Exception {
		Delegator delegator = dctx.getDelegator();

		ByteBuffer fileData = (ByteBuffer) context.get("uploadFile");
		String fileName = (String) context.get("_uploadFile_fileName");
		String target = (String) context.get("target");

		String fileServerPath = FlexibleStringExpander.expandString(
				UtilProperties.getPropertyValue("operator",
						"upload.server.path"), context);
		String path = fileServerPath
				+ fileName.replace(".", UtilDateTime.nowDate().getTime() + ".");
		File file = new File(path);
		Debug.logInfo("upload file to " + file.getAbsolutePath(), "");

		try {
			RandomAccessFile out = new RandomAccessFile(file, "rw");
			out.write(fileData.array());
			out.close();
		} catch (FileNotFoundException e) {
			Debug.logError(e, "");
			return ServiceUtil.returnError("file can't write, fileName:"
					+ file.getAbsolutePath());
		} catch (IOException e) {
			Debug.logError(e, "");
			return ServiceUtil.returnError("file can't write, fileName:"
					+ file.getAbsolutePath());
		}

		// 解析xlsx文件，并将数据写入对应表中
		if (target.equals("operator")) {
			OperatorWorker.saveOperator(path);
		} else {
			OperatorWorker.saveCheck(path);
		}

		return ServiceUtil.returnSuccess();
	}

	/**
	 * ERPCheck
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> ERPCheck(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();

		String userLoginId = (String) context.get("partyId");
        if (userLoginId == null) {
        	GenericValue userLogin = (GenericValue) context.get("userLogin");
        	userLoginId = userLogin.get("userLoginId").toString();
        }
		Set<String> storeIdSet = OperatorWorker.getStoreIdsByPartyId(delegator, userLoginId);
    	
		List<EntityCondition> andExprs = FastList.newInstance();
		EntityCondition mainCond = null; 
		
		andExprs.add(EntityCondition.makeCondition("storeId",
				EntityOperator.IN, storeIdSet));
		
		if (andExprs.size() > 0) {
			mainCond = EntityCondition.makeCondition(andExprs,EntityOperator.AND);
		}
		
		List<GenericValue> checks = null;
		List<GenericValue> salesOpts = null;
		List<GenericValue> operators = null;
		try {
			checks = delegator.findList("ErpOptBusinessCheck", null, null,
					null, null, false);
			salesOpts = delegator.findList("ErpSalesOptBusiness", mainCond, null,
					null, null, false);
			operators = delegator.findList("ErpOperatorsBusiness", mainCond, null,
					null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			// 校验 接入号+业务类型;若匹配,则:isChecked = "Y"
			for (GenericValue check : checks) {
				String accessNumber = check.get("accessNumber").toString();
				String erpOptSetValueId = check.get("erpOptSetValueId")
						.toString();
				String keyDate = check.get("keyDate").toString();

				for (GenericValue sale : salesOpts) {
					String saleAccessNumber = (String) sale.get("accessNumber");
					String saleErpOptSetValueId = (String) sale
							.get("erpOptSetValueId");
					String saleKeyDate = (String) sale.get("keyDate");
					boolean b = OperatorWorker.isCheck(delegator, "ErpSalesOptBusiness", saleAccessNumber, saleErpOptSetValueId, saleKeyDate);
					if (!b) {
						if (accessNumber.equals(saleAccessNumber)
								&& erpOptSetValueId.equals(saleErpOptSetValueId)
								&& keyDate.equals(saleKeyDate)) {
							sale.set("isChecked", "Y");
							sale.store();
							check.set("isChecked", "Y");
							check.store();
						}
					}
				}
				for (GenericValue operator : operators) {
					String operatorAccessNumber = operator.get("accessNumber")
							.toString();
					String operatorErpOptSetValueId = operator.get(
							"erpOptSetValueId").toString();
					String operatorKeyDate = operator.get("keyDate").toString();
					boolean sob = OperatorWorker.isCheck(delegator, "ErpSalesOptBusiness", operatorAccessNumber, operatorErpOptSetValueId, operatorKeyDate);
					boolean ob = OperatorWorker.isCheck(delegator, "ErpOperatorsBusiness", operatorAccessNumber, operatorErpOptSetValueId, operatorKeyDate);
					if (!sob && !ob) {
						if (accessNumber.equals(operatorAccessNumber)
								&& erpOptSetValueId.equals(operatorErpOptSetValueId)
								&& keyDate.equals(operatorKeyDate)) {
							operator.set("isChecked", "Y");
							operator.store();
							check.set("isChecked", "Y");
							check.store();
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}

	/**
	 * createOrUpdateOperators
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> createOrUpdateOperators(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();

		String accessNumber = (String) context.get("accessNumber");
		String erpOptSetValueId = (String) context.get("erpOptSetValueId");
		String isChecked = (String) context.get("isChecked");
		Timestamp openingDate = (Timestamp) context.get("openingDate");
		String strData = ConvertUtil.convertDateToStringD(openingDate);

		Map<String, String> pkMap = UtilMisc.toMap("accessNumber",
				accessNumber, "erpOptSetValueId", erpOptSetValueId, "keyDate",
				strData);

		GenericValue gv = null;
		try {
			gv = delegator.findByPrimaryKey("ErpOperatorsBusiness", pkMap);
			if (UtilValidate.isNotEmpty(gv)) {
				gv.setNonPKFields(context);
				if (UtilValidate.isEmpty(isChecked)) {
					gv.set("isChecked", "N");
				}
				gv.store();
			} else {
				gv = delegator.makeValue("ErpOperatorsBusiness", pkMap);
				gv.setNonPKFields(context);
				if (UtilValidate.isEmpty(isChecked)) {
					gv.set("isChecked", "N");
				}
				gv.create();
			}
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}

	/**
	 * createOrUpdateCheck
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> createOrUpdateCheck(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();

		String accessNumber = (String) context.get("accessNumber");
		String erpOptSetValueId = (String) context.get("erpOptSetValueId");
		String isChecked = (String) context.get("isChecked");
		Timestamp openingDate = (Timestamp) context.get("openingDate");
		String strData = ConvertUtil.convertDateToStringD(openingDate);

		Map<String, String> pkMap = UtilMisc.toMap("accessNumber",
				accessNumber, "erpOptSetValueId", erpOptSetValueId, "keyDate",
				strData);

		GenericValue gv = null;
		try {
			gv = delegator.findByPrimaryKey("ErpOptBusinessCheck", pkMap);
			if (UtilValidate.isNotEmpty(gv)) {
				gv.setNonPKFields(context);
				if (UtilValidate.isEmpty(isChecked)) {
					gv.set("isChecked", "N");
				}
				gv.store();
			} else {
				gv = delegator.makeValue("ErpOptBusinessCheck", pkMap);
				gv.setNonPKFields(context);
				if (UtilValidate.isEmpty(isChecked)) {
					gv.set("isChecked", "N");
				}
				gv.create();
			}
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}

	/**
	 * createSalesOptBusiness
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> createSalesOptBusiness(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();

		EntityCondition cond = null;
		ErpFaceLogModel Model = new ErpFaceLogModel("saveSalesOptBusines",
				"getSalesDate", "", "", "", "");
		Timestamp lastTime = SyncTime.getLastTimestamp(Model, delegator);
		if (UtilValidate.isNotEmpty(lastTime)) {
			cond = EntityCondition.makeCondition("lastUpdatedStamp",
					EntityOperator.GREATER_THAN, lastTime);
		}

		List<GenericValue> gvs = null;
		try {
			gvs = delegator.findList("SaleOrderHeaderAndDtl", cond, null, null,
					null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GenericValue erpSalesOptBusiness = null;
		try {
			for (GenericValue gv : gvs) {
				//获取销售单据的主数据
				if (gv.get("isMainProduct").equals("Y")) {
					Map<String, Object> map = UtilMisc.toMap("storeId", gv
							.get("storeId"), "docId", gv.get("docId"), "lineNo", gv
							.get("lineNo"), "docDate", gv.get("postingDate"),
							"sequenceId", gv.get("serialNo"),
							"productSalesPolicyId", gv.get("productSalesPolicyId"),
							"isChecked", "N", "productId", gv.get("productId"),
							"productName", gv.get("productName"));

					erpSalesOptBusiness = delegator.findByPrimaryKey(
							"ErpSalesOptBusiness", UtilMisc.toMap("docId", gv
									.get("docId"), "lineNo", gv.get("lineNo")));

					if (UtilValidate.isEmpty(erpSalesOptBusiness)) {
						erpSalesOptBusiness = delegator.makeValidValue(
								"ErpSalesOptBusiness", map);
						erpSalesOptBusiness.create();
					} else {
						erpSalesOptBusiness.set("storeId", gv.get("storeId"));
						erpSalesOptBusiness.set("docDate", gv.get("postingDate"));
						erpSalesOptBusiness.set("sequenceId", gv.get("serialNo"));
						erpSalesOptBusiness.set("productSalesPolicyId", gv
								.get("productSalesPolicyId"));
						erpSalesOptBusiness.set("productId", gv.get("productId"));
						erpSalesOptBusiness.set("productName", gv
								.get("productName"));
						erpSalesOptBusiness.store();
					}
				}
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SyncTime.saveLastTime(Model, UtilDateTime.nowTimestamp(), delegator);
		return ServiceUtil.returnSuccess();
	}

	/**
	 * updateSalesOptBusiness
	 * 
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> updateSalesOptBusiness(
			DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();

		String docId = (String) context.get("docId");
		Long lineNo = (Long) context.get("lineNo");
		String isChecked = (String) context.get("isChecked");
		Timestamp openingDate = (Timestamp) context.get("openingDate");
		String strData = ConvertUtil.convertDateToStringD(openingDate);

		Map<String, Object> pkMap = UtilMisc.toMap("docId", docId, "lineNo",
				lineNo);

		GenericValue gv = null;
		try {
			gv = delegator.findByPrimaryKey("ErpSalesOptBusiness", pkMap);
			if (UtilValidate.isNotEmpty(gv)) {
				gv.setNonPKFields(context);
				gv.set("keyDate", strData);
				if (UtilValidate.isEmpty(isChecked)) {
					gv.set("isChecked", "N");
				}
				gv.store();
			} else {
				gv = delegator.makeValue("ErpSalesOptBusiness", pkMap);
				gv.setNonPKFields(context);
				gv.set("keyDate", strData);
				if (UtilValidate.isEmpty(isChecked)) {
					gv.set("isChecked", "N");
				}
				gv.create();
			}
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	/**
     * findSalesOptBusinessList
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> findOperatorsList(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	Map<String,Object> result = FastMap.newInstance();
    	String entityName = (String) context.get("entityName");
    	String userLoginId = (String) context.get("partyId");
        if (userLoginId == null) {
        	GenericValue userLogin = (GenericValue) context.get("userLogin");
        	userLoginId = userLogin.get("userLoginId").toString();
        }
        Map<String,Object> inputFields = (Map<String,Object>) context.get("inputFields");
        
        String isChecked = (String) inputFields.get("isChecked");
        String openingDate = (String) inputFields.get("openingDate");
        String openingDateEnd = (String) inputFields.get("openingDateEnd");
        
        Set<String> storeIdSet = OperatorWorker.getStoreIdsByPartyId(delegator, userLoginId);
    	
        List<String> orderByList = FastList.newInstance();
    	orderByList.add("-openingDate");
    	if (entityName.equals("ErpSalesOptBusiness")) {
    		orderByList.add("-docDate");
		}
		List<EntityCondition> andExprs = FastList.newInstance();
		EntityCondition mainCond = null; 
		
		andExprs.add(EntityCondition.makeCondition("storeId",
				EntityOperator.IN, storeIdSet));
		
		List<Object> listDate = FastList.newInstance();
		if(Constant.isNotNull(openingDate)){
			listDate.add(ConvertUtil.convertStringToTimeStamp(openingDate));
		}
		if (Constant.isNotNull(openingDateEnd)) {
			listDate.add(ConvertUtil.convertStringToTimeStamp(openingDateEnd));
		}
		if(Constant.isNotNull(openingDate) && Constant.isNotNull(openingDateEnd)){
			andExprs.add(EntityCondition.makeCondition("openingDate", EntityOperator.BETWEEN, listDate));
		}else if (Constant.isNotNull(openingDate) && Constant.isNull(openingDateEnd)) {
			andExprs.add(EntityCondition.makeCondition("openingDate", EntityOperator.GREATER_THAN, 
					ConvertUtil.convertStringToTimeStamp(openingDate)));
		}
		if(Constant.isNotNull(isChecked)){
    		andExprs.add(EntityCondition.makeCondition("isChecked", EntityOperator.EQUALS, isChecked));
    	}
		
        if (andExprs.size() > 0) {
			mainCond = EntityCondition.makeCondition(andExprs,EntityOperator.AND);
		}
        
        // using list iterator
        EntityListIterator listIt = null;
        List<GenericValue> lists = null;
        int listSize = 0;
        
        Integer viewSize = (Integer) context.get("viewSize");
        Integer viewIndex = (Integer) context.get("viewIndex");
        Integer maxRows = null;
        if (viewSize != null && viewIndex != null) {
            maxRows = viewSize * (viewIndex + 1);
        }
        maxRows = maxRows != null ? maxRows : -1;
        
		try {
			listIt = delegator.find(entityName, mainCond, null, null, orderByList, 
					new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, maxRows, false));
            listSize = listIt.getResultsSizeAfterPartialList();
//			lists = delegator.findList(entityName, mainCond, null, null, 
//					new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, maxRows, false), false);
//			listSize = lists.size();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	result.put("listIt", listIt);
    	result.put("listSize", listSize);
    	return result;
    }
	

}
