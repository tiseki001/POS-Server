/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.product.salesprice;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.product.condUtil.condSqlString;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;


/**
 * SalesPriceServices - Workers and Services class for product price related functionality
 */
public class SalesPriceServices {
	
	public static Map<String, String> entityOperators;
	public static Map<String, String> entityFieldName;

    static {
        entityOperators = FastMap.newInstance();
        entityOperators.put("EQ", " = ");
        entityOperators.put("GT", " > ");
        entityOperators.put("GTE", " >= ");
        entityOperators.put("LT", " < ");
        entityOperators.put("LTE", " <= ");
        entityOperators.put("NEQ", " <> ");
        entityOperators.put("LIKE", " LIKE ");
        entityOperators.put("IN", " in ");
        
        entityFieldName = FastMap.newInstance();
        entityFieldName.put("productId", " PRO.product_id ");
        entityFieldName.put("productName", " PRO.product_name ");
        entityFieldName.put("brandName", " PRO.brand_name ");
    }

    public static final String module = SalesPriceServices.class.getName();
    public static final String resource = "ProductUiLabels";
    public static final String resourceError = "ProductErrorUiLabels";
    public static final BigDecimal ONE_BASE = BigDecimal.ONE;
    public static final BigDecimal PERCENT_SCALE = new BigDecimal("100.000");

    public static final int taxCalcScale = UtilNumber.getBigDecimalScale("salestax.calc.decimals");
    public static final int taxFinalScale = UtilNumber.getBigDecimalScale("salestax.final.decimals");
    public static final int taxRounding = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");
    
    /**
     * 创建价格类型（IS_BASE = "Y"）
     * 创建productPrice
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> createProductPriceTypeRule(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String partyId = (String)userLogin.get("userLoginId");
    	
    	String productPriceRuleId = (String)context.get("productPriceRuleId");
    	GenericValue productPriceRule = null;
    	
    	try {
			productPriceRule = delegator.findByPrimaryKey("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String productPriceTypeId = "";
		List<GenericValue> productPriceType_ = null;
		
		try {
			productPriceType_ = delegator.findByAnd("ProductPriceType", UtilMisc.toMap("description", productPriceRule.get("ruleName"), "isBase","Y"));
		} catch (GenericEntityException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		// 不存在productPriceRuleId对应的ProductPriceType记录，则创建一条新记录
		if (UtilValidate.isEmpty(productPriceType_)) {
			productPriceTypeId = delegator.getNextSeqId("ProductPriceType");
			GenericValue productPriceType = delegator.makeValue("ProductPriceType", UtilMisc.toMap("productPriceTypeId", productPriceTypeId,
					"description", productPriceRule.get("ruleName"),
					"isBase","Y"));
			try {
				productPriceType.create();
			} catch (GenericEntityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else {
			productPriceTypeId = (String)productPriceType_.get(0).get("productPriceTypeId");
		}
		
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		List<EntityCondition> condList = FastList.newInstance();
        
        condList.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null));
        condList.add(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp));
        EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.OR);
		
        List<GenericValue> productPrices = null;
        try {
        	productPrices = delegator.findList("ProductPrice", cond, null, null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (GenericValue productPrice : productPrices) {
			if (UtilValidate.isNotEmpty(productPrice.get("price"))) {
				String price1 = SalesPriceWorker.getProductPriceByTypeId(dctx, productPriceRuleId, 
						(String)productPrice.get("productPriceTypeId"), 
						(String)productPrice.get("productId"));
				
				BigDecimal price = new BigDecimal(price1.toString());
				
				Map<String, Object> pkMap = UtilMisc.toMap("productId", productPrice.get("productId"),
						"productPriceTypeId", productPriceTypeId,
						"productPricePurposeId", productPrice.get("productPricePurposeId"),
						"currencyUomId", productPrice.get("currencyUomId"),
						"productStoreGroupId", productPrice.get("productStoreGroupId"),
						"fromDate", productPrice.get("fromDate"));
				// productprice 存在基础价格，则store；否则create。
				try {
					GenericValue entity ;
					
					entity = delegator.findByPrimaryKey("ProductPrice", pkMap);	
					if (UtilValidate.isNotEmpty(entity)) {
						entity.set("price", price);
						entity.store();
					}else {
						entity = delegator.makeValue("ProductPrice", pkMap);
						entity.set("taxInPrice", productPrice.get("taxInPrice"));
						entity.set("price", price);
						entity.set("createdByUserLogin", partyId);
						entity.set("lastModifiedByUserLogin", partyId);
						
						entity.create();
					}
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    	return ServiceUtil.returnSuccess();
    }
    
    /**
     * create ProductSalesPolicy
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> createProductSalesPolicy(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Locale locale = (Locale) context.get("locale");
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String partyId = (String)userLogin.get("userLoginId");
    	String productSalesPolicyId = (String)context.get("productSalesPolicyId");
    	
    	 // partyId might be empty, so check it and get next seq party id if empty
        if (UtilValidate.isEmpty(productSalesPolicyId)) {
            try {
            	productSalesPolicyId = delegator.getNextSeqId("ProductSalesPolicy");
            } catch (IllegalArgumentException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "party.id_generation_failure", locale));
            }
        }
    	
        GenericValue salesPolicy = null;
        
        Map<String, String> newSalesPolicyMap = UtilMisc.toMap("productSalesPolicyId", productSalesPolicyId, "partyId", partyId);
        salesPolicy = delegator.makeValue("ProductSalesPolicy", newSalesPolicyMap);
        salesPolicy.setNonPKFields(context);
        try {
			salesPolicy.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String, Object> returnMap = FastMap.newInstance();
		returnMap.put("productSalesPolicyId", productSalesPolicyId);
    	return returnMap;
    }
    
    /**
     * update SalesPolicy
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateSalesPolicy(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String partyId = (String)userLogin.get("userLoginId");
    	String productSalesPolicyId = (String)context.get("productSalesPolicyId");
    	String policyName = (String) context.get("policyName");
    	String description = (String) context.get("description");
    	Long mainRatio = (Long) context.get("mainRatio");
    	
        GenericValue salesPolicy = null;
        
        Map<String, Object> newSalesPolicyMap = UtilMisc.toMap("productSalesPolicyId", productSalesPolicyId, 
        													"partyId", partyId,
        													"policyName", policyName,
        													"description", description,
        													"mainRatio", mainRatio);
        salesPolicy = delegator.makeValue("ProductSalesPolicy", newSalesPolicyMap);
        try {
			salesPolicy.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess();
    }
    
    /**
     * create ProductPriceRule
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> createProductPriceRules(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Locale locale = (Locale) context.get("locale");
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String partyId = (String)userLogin.get("userLoginId");
    	String productPriceRuleId = (String)context.get("productPriceRuleId");
    	
    	GenericValue priceRule = null;
    	
        if (!UtilValidate.isEmpty(productPriceRuleId)) {
            
            Map<String, String> newPriceRuleMap = UtilMisc.toMap("productPriceRuleId", productPriceRuleId, "partyId", partyId);
            priceRule = delegator.makeValue("ProductPriceRule", newPriceRuleMap);
            priceRule.setNonPKFields(context);
            try {
            	priceRule.store();
    		} catch (GenericEntityException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }else{
        	try {
            	productPriceRuleId = delegator.getNextSeqId("ProductPriceRule");
            } catch (IllegalArgumentException e) {
                return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
                        "party.id_generation_failure", locale));
            }
        	
            Map<String, String> newPriceRuleMap = UtilMisc.toMap("productPriceRuleId", productPriceRuleId, "partyId", partyId);
            priceRule = delegator.makeValue("ProductPriceRule", newPriceRuleMap);
            priceRule.setNonPKFields(context);
            try {
            	priceRule.create();
    		} catch (GenericEntityException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		//添加ProductPriceRuleStoreList
    		Map<String, Object> storeMap = SalesPriceWorker.findStoreList(dctx, productPriceRuleId);
    		SalesPriceWorker.updateProductPriceRuleStoreList(delegator, productPriceRuleId, (List<GenericValue>) storeMap.get("lists"));
        }
        Map<String, Object> returnMap = FastMap.newInstance();
		returnMap.put("productPriceRuleId", productPriceRuleId);
    	return returnMap;
    }
    
    
    /**
     * remove SalesPolicy
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> removeSalesPrice(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String productSalesPolicyId = (String)context.get("productSalesPolicyId");
    	String productPriceRuleId = (String)context.get("productPriceRuleId");
    	
    	GenericValue gv = null;
    	List<GenericValue> lists = null;
    	List<GenericValue> productPriceConds = null;
    	List<GenericValue> gvs = null;
    	List<GenericValue> gvs1 = null;
    	List<GenericValue> gvs2 = null;
    	List<GenericValue> productPriceLists = null;
    	try {
    		if(UtilValidate.isNotEmpty(productSalesPolicyId)){
    			gv = delegator.findByPrimaryKey("ProductSalesPolicy", UtilMisc.toMap("productSalesPolicyId", productSalesPolicyId));
    			lists = delegator.findByAnd("ProductSalesPolicyRule", UtilMisc.toMap("productSalesPolicyId", productSalesPolicyId));
    			gvs = delegator.findByAnd("ProductSalesPolicyBom", UtilMisc.toMap("productSalesPolicyId", productSalesPolicyId));
    			productPriceLists = delegator.findByAnd("ProductPriceList", UtilMisc.toMap("productSalesPolicyId", productSalesPolicyId));
    			
    		}else if(UtilValidate.isNotEmpty(productPriceRuleId)){
    			gv = delegator.findByPrimaryKey("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
    			lists = delegator.findByAnd("ProductSalesPolicyRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
    			gvs = delegator.findByAnd("ProductPriceRuleStore", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
    			gvs2 = delegator.findByAnd("ProductPriceRuleStoreList", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
    			gvs1 = delegator.findByAnd("ProductPriceRuleProduct", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
    			productPriceLists = delegator.findByAnd("ProductPriceList", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
    			productPriceConds = delegator.findByAnd("ProductPriceCond", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
    		}
    		delegator.removeAll(gvs);
    		delegator.removeAll(gvs1);
    		delegator.removeAll(gvs2);
    		delegator.removeAll(lists);
    		delegator.removeAll(productPriceLists);
    		delegator.removeAll(productPriceConds);
			gv.remove();
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	return ServiceUtil.returnSuccess();
    }
    
    /**
     * create 销售政策与规则对应关系
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> createSalesPolicyPriceRule(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String productPriceRuleId = (String)context.get("productPriceRuleId");
    	String productSalesPolicyId = (String)context.get("productSalesPolicyId");
    	
        GenericValue priceRule = null;
        GenericValue productPriceRule = null;
        	
        try {
			productPriceRule = delegator.findByPrimaryKey("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        Map<String, String> newPriceRuleMap = UtilMisc.toMap("productPriceRuleId", productPriceRuleId, "productSalesPolicyId", productSalesPolicyId);
        priceRule = delegator.makeValue("ProductSalesPolicyRule", newPriceRuleMap);
        priceRule.setNonPKFields(context);
        priceRule.set("ruleName", productPriceRule.get("ruleName"));
        priceRule.set("description", productPriceRule.get("description"));
        priceRule.set("productPriceTypeId", productPriceRule.get("productPriceTypeId"));

        try {
        	priceRule.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, Object> productMap = SalesPriceWorker.findBomList(dctx, productPriceRuleId);
			//findBomList(dctx, UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
		// 添加价格清单; 全连接价格规则id下的商品与门店
		SalesPriceWorker.createProductPriceList(delegator, (List<GenericValue>) productMap.get("lists"), productPriceRuleId, productSalesPolicyId);
    	return ServiceUtil.returnSuccess();
    }
    
    /**
     * delete SalesPolicyPriceSule
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> deleteSalesPolicyPriceSule(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String productSalesPolicyId = (String)context.get("productSalesPolicyId");
    	String productPriceRuleId = (String)context.get("productPriceRuleId");
    	
    	List<GenericValue> gv = null;
    	List<GenericValue> list = null;
    	
    	try {
    		gv = delegator.findByAnd("ProductSalesPolicyRule", UtilMisc.toMap("productSalesPolicyId", productSalesPolicyId, "productPriceRuleId", productPriceRuleId));
    		list = delegator.findByAnd("ProductPriceList", UtilMisc.toMap("productSalesPolicyId", productSalesPolicyId, "productPriceRuleId", productPriceRuleId));
    		delegator.removeAll(list);
    		delegator.removeAll(gv);
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	return ServiceUtil.returnSuccess();
    }
    
    /**
     * save SalesPolicyPackage
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> saveSalesPolicyPackage(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String productPriceRuleId = (String)context.get("productPriceRuleId");
    	String productSalesPolicyId = (String)context.get("productSalesPolicyId");
    	
    	if (UtilValidate.isNotEmpty(productPriceRuleId) && productPriceRuleId.equals("000000")) {
    		productPriceRuleId = null;
		}
    	if (UtilValidate.isNotEmpty(productSalesPolicyId) && productSalesPolicyId.equals("000000")) {
    		productSalesPolicyId = null;
		}
    	
    	String fieldName = (String)context.get("fieldName");
    	String seqnum = "";
    	String rowCount = (String)context.get("rowCount");
    	String expreType = (String)context.get("expreType");
    	String rowTotal = (String)context.get("rowTotal");
    	List<GenericValue> dels = null;
    	Map<String, String> newPriceRuleMap = UtilMisc.toMap("productPriceRuleId", productPriceRuleId);
    	Map<String, String> newSalesPolicyMap = UtilMisc.toMap("productSalesPolicyId", productSalesPolicyId);
    	
    	try {
    		if (UtilValidate.isNotEmpty(rowCount) && rowCount.equals("delete")) {
    			if(expreType.equals("product")){
    				dels = delegator.findByAnd("ProductPriceRuleProduct", newPriceRuleMap);
				}else if(expreType.equals("store")){
					dels = delegator.findByAnd("ProductPriceRuleStore", newPriceRuleMap);
				}else{
					dels = delegator.findByAnd("ProductSalesPolicyBom", newSalesPolicyMap);
				}
    			delegator.removeAll(dels);
			}
		} catch (GenericEntityException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	
    	
    	GenericValue gv = null;
    	List<GenericValue> gvs = null;
    	
    	if(UtilValidate.isNotEmpty(fieldName)){
    		if(rowCount.equals("2")){
    			try {
    				if(expreType.equals("product")){
    					gvs = delegator.findByAnd("ProductPriceRuleProduct", newPriceRuleMap);
    				}else if(expreType.equals("store")){
    					gvs = delegator.findByAnd("ProductPriceRuleStore", newPriceRuleMap);
    				}else{
    					gvs = delegator.findByAnd("ProductSalesPolicyBom", newSalesPolicyMap);
    				}
    				delegator.removeAll(gvs);
    			} catch (GenericEntityException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			}
    		}
    		if(expreType.equals("product")){
    			seqnum = delegator.getNextSeqId("ProductPriceRuleProduct");
    			gv = delegator.makeValue("ProductPriceRuleProduct", newPriceRuleMap);
    			
			}else if(expreType.equals("store")){
				seqnum = delegator.getNextSeqId("ProductPriceRuleStore");
				gv = delegator.makeValue("ProductPriceRuleStore", newPriceRuleMap);
			}else{
				seqnum = delegator.getNextSeqId("ProductSalesPolicyBom");
				gv = delegator.makeValue("ProductSalesPolicyBom", newSalesPolicyMap);
			}
    		gv.setNonPKFields(context);
    		gv.set("seqnum", seqnum);
    		
    		String formula = "";
    		
    		if(fieldName.equals("CATEGORY") || fieldName.equals("productCategoryId")) {
    			gv.set("tableName", "product_category_member");
    			gv.set("fieldName", "productCategoryId");
    			formula = "PCM.product_category_id"+entityOperators.get(gv.get("operate"))+"'"+context.get("value1")+"'";
    			gv.set("formula", formula);
    		}else if(fieldName.equals("productId") || fieldName.equals("productName") ) {
    			gv.set("tableName", "product");
    			gv.set("fieldName", fieldName);
    			if (context.get("operate").equals("LIKE")) {
    				formula = entityFieldName.get(fieldName) + entityOperators.get(gv.get("operate")) + "'%"+context.get("value1") + "%'";
				}else {
					formula = entityFieldName.get(fieldName) + entityOperators.get(gv.get("operate")) + "'"+context.get("value1") + "'";
				}
    			gv.set("formula", formula);
    		}else{
    			gv.set("tableName", "product_feature_appl");
    			gv.set("fieldName", "productFeatureId");
    			if (context.get("operate").equals("LIKE")) {
    				formula = "exists (select product_id from PRODUCT_FEATURE_APPL where PRO.product_id = product_id and "+
    						"product_feature_id " + entityOperators.get(gv.get("operate")) + "'%"+context.get("value1") + "%')";
				}else if (context.get("operate").equals("IN")) {
					formula = "exists (select product_id from PRODUCT_FEATURE_APPL where PRO.product_id = product_id and " +
					"product_feature_id " + entityOperators.get(gv.get("operate")) + 
					context.get("value1").toString().replace("(", "('").replace(")", "')").replaceAll(",", "','") + ")";
				}else {
					formula = "exists (select product_id from PRODUCT_FEATURE_APPL where PRO.product_id = product_id and " +
							"product_feature_id " + entityOperators.get(gv.get("operate")) + "'"+context.get("value1") + "')";
				}
    			gv.set("formula", formula);
    		}
    		
    		if(expreType.equals("store")){
    			gv.set("fieldName", fieldName);
    			gv.set("tableName", "product_store_attribute");
    			StringBuffer sbFormula = null;
        		if (context.get("fieldName").equals("storeName")) {
        			sbFormula = new StringBuffer("");
            		if (context.get("operate").equals("LIKE")) {
            			sbFormula.append(" PS.store_name like '%");
            			sbFormula.append(context.get("value1"));
            			sbFormula.append("%' ");
        			}else {
        				sbFormula.append(" PS.store_name "+entityOperators.get(gv.get("operate"))+" '");
        				sbFormula.append(context.get("value1"));
        				sbFormula.append("' ");
        			}
            		gv.set("formula", sbFormula.toString());
    			}else {
    				/*sbFormula = new StringBuffer(" (PSA.product_store_attr_type_id = '");
    				sbFormula.append(context.get("fieldName"));
    				sbFormula.append("' and PSA.attr_value "+entityOperators.get(gv.get("operate"))+" '");
    				sbFormula.append(context.get("value1"));
    				sbFormula.append("') ");*/
    				if (context.get("operate").equals("LIKE")) {
        				formula = "exists (select PRODUCT_STORE_ID from PRODUCT_STORE_ATTRIBUTE where PS.PRODUCT_STORE_ID = PRODUCT_STORE_ID and "+
	        				"(product_store_attr_type_id = '"+context.get("fieldName")+"' and attr_value " 
	    					+ entityOperators.get(gv.get("operate")) + "'%"+context.get("value1") + "%'))";
    				}else {
    					formula = "exists (select PRODUCT_STORE_ID from PRODUCT_STORE_ATTRIBUTE where PS.PRODUCT_STORE_ID = PRODUCT_STORE_ID and " +
	    					"(product_store_attr_type_id = '"+context.get("fieldName")+"' and attr_value " 
	    					+ entityOperators.get(gv.get("operate")) + "'"+context.get("value1") + "'))";
    				}
    				gv.set("formula", formula);
    			}
    		}
    		
    		try {
    			gv.create();
    		} catch (GenericEntityException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
    	//价格规则下商品，门店改变时，更新productPriceList 清单
    	//Map<String, Object> productMap = findBomList(dctx, UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
    	//Map<String, Object> storeMap = findStoreList(dctx, UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
    	Map<String, Object> productMap = null;
    	Map<String, Object> storeMap = null;
    	List<GenericValue> lists = null;
    	String salesPolicyId = "";
    	
    	if (UtilValidate.isNotEmpty(rowCount) && (rowCount.equals(rowTotal) || rowCount.equals("delete"))) {
    		// 生成门店清单表
        	if (expreType.equals("store")) {
        		storeMap = SalesPriceWorker.findStoreList(dctx, productPriceRuleId);
        		SalesPriceWorker.updateProductPriceRuleStoreList(delegator, productPriceRuleId, (List<GenericValue>) storeMap.get("lists"));
    		}else {
    			productMap = SalesPriceWorker.findBomList(dctx, productPriceRuleId);
    	    	try {
    				lists = delegator.findByAnd("ProductSalesPolicyRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
    				for (GenericValue productSalesPolicyRule : lists) {
    					salesPolicyId = (String)productSalesPolicyRule.get("productSalesPolicyId");
    					//生成新价格清单数据
    					SalesPriceWorker.createProductPriceList(delegator, (List<GenericValue>) productMap.get("lists"), productPriceRuleId, salesPolicyId);
    				}
    			} catch (GenericEntityException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}
		}
    	
    	return ServiceUtil.returnSuccess();
    }
    /**
     * save ProductPriceListStore
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> saveProductPriceListStore(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	
    	String fieldName = (String)context.get("fieldName");
    	String rowCount = (String)context.get("rowCount");
    	String expreType = (String)context.get("expreType");
    	List<GenericValue> dels = null;
    	
    	try {
    		if (UtilValidate.isNotEmpty(rowCount) && rowCount.equals("delete")) {
    			dels = delegator.findList("ProductPriceListStore", null, null, null, null, true);
    			delegator.removeAll(dels);
			}
		} catch (GenericEntityException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	
    	
    	GenericValue gv = null;
    	List<GenericValue> gvs = null;
    	
    	if(UtilValidate.isNotEmpty(fieldName)){
    		if(rowCount.equals("2")){
    			try {
    				gvs = delegator.findList("ProductPriceListStore", null, null, null, null, true);
    				delegator.removeAll(gvs);
    			} catch (GenericEntityException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			}
    		}
    		
    		String productPriceListStoreId = delegator.getNextSeqId("ProductPriceListStore");
    		
    		gv = delegator.makeValue("ProductPriceListStore", UtilMisc.toMap("productPriceListStoreId", productPriceListStoreId));
    		gv.setNonPKFields(context);
    		
    		StringBuffer sbFormula = null;
    		
    		gv.set("fieldName", fieldName);
			gv.set("tableName", "product_store_attribute");
    		if (context.get("fieldName").equals("storeName")) {
    			sbFormula = new StringBuffer("");
        		if (context.get("operate").equals("LIKE")) {
        			sbFormula.append(" PS.store_name like '%");
        			sbFormula.append(context.get("value1"));
        			sbFormula.append("%' ");
    			}else {
    				sbFormula.append(" PS.store_name "+entityOperators.get(gv.get("operate"))+" '");
    				sbFormula.append(context.get("value1"));
    				sbFormula.append("' ");
    			}
			}else {
				sbFormula = new StringBuffer(" (PSA.product_store_attr_type_id = '");
				sbFormula.append(context.get("fieldName"));
				sbFormula.append("' and PSA.attr_value "+entityOperators.get(gv.get("operate"))+" '");
				sbFormula.append(context.get("value1"));
				sbFormula.append("') ");
			}
			gv.set("formula", sbFormula.toString());
    		
    		try {
    			gv.create();
    		} catch (GenericEntityException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	return ServiceUtil.returnSuccess();
    }
    
    /**
     * getProductFeatureCategoryList
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> getProductFeatureCategoryList(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	
    	String fieldName = (String)context.get("fieldName");
    	Map<String,Object> result = FastMap.newInstance();
    	List<String> lists = FastList.newInstance();
    	List<GenericValue> gvs = null;
    	String categoryName = "";
    	
    	if (UtilValidate.isNotEmpty(fieldName)) {
    		try {
    			if(fieldName.equals("CATEGORY")){
    				gvs = delegator.findList("ProductCategory", null, null, null, null, true);
    				for (GenericValue gv : gvs) {
    					categoryName = (String)gv.get("categoryName") == null ? "" : (String)gv.get("categoryName");
    					lists.add("(" + categoryName + ")" +
    							gv.get("productCategoryId") + ": " + gv.get("productCategoryId"));
    				}
    			}else if (fieldName.equals("productId") || fieldName.equals("productName") || fieldName.equals("brandName")) {
    				lists.add("PRODUCT: PRODUCT");
    			}else{
    				gvs = delegator.findByAnd("ProductFeature", UtilMisc.toMap("productFeatureCategoryId", fieldName));
    				for (GenericValue gv : gvs) {
    					lists.add(gv.get("description")+": "+gv.get("productFeatureId"));
    					//lists.add(UtilMisc.toMap("option", gv.get("description")+":"+gv.get("productFeatureId")));
    				}
    			}
    		} catch (GenericEntityException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
		}
    	result.put("productFeatureCategoryList", lists);
    	return result;
    }
    
    /**
     * getProductPriceRuleStoreList
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> getProductPriceRuleStoreList(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	
    	String productStoreAttrTypeId = (String)context.get("fieldName");
    	Map<String,Object> result = FastMap.newInstance();
    	List<String> lists = FastList.newInstance();
    	List<GenericValue> gvs = null;
    	
    	try {
    		gvs = delegator.findByAnd("ProductStoreAttribute", UtilMisc.toMap("productStoreAttrTypeId", productStoreAttrTypeId));
			for (GenericValue gv : gvs) {
				lists.add(gv.get("attrValue")+": "+gv.get("productStoreId"));
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	result.put("productPriceRuleStoreList", lists);
    	return result;
    }
    
    /**
     * getProductFeatureCategoryList
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> findBomList(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	
    	String productPriceRuleId = (String)context.get("productPriceRuleId");
    	String productSalesPolicyId = (String)context.get("productSalesPolicyId");
    	Map<String,Object> result = FastMap.newInstance();
    	
        DynamicViewEntity dynamicView = new DynamicViewEntity();

        // define the main condition  list
        EntityCondition mainCond = null;

        List<String> orderBy = FastList.newInstance();
        List<String> fieldsToSelect = FastList.newInstance();
        // default view settings
        
        dynamicView.addMemberEntity("PRO", "Product");
        dynamicView.addMemberEntity("PCM", "ProductCategoryMember");
        // dynamicView.addMemberEntity("PFA", "ProductFeatureAppl");
        // dynamicView.addMemberEntity("PF", "ProductFeature");
        dynamicView.addMemberEntity("PC", "ProductCategory");
        dynamicView.addAlias("PRO", "productId");
        dynamicView.addAlias("PRO", "productName");
        dynamicView.addAlias("PRO", "brandName");
        dynamicView.addAlias("PRO", "featureAll");
        dynamicView.addAlias("PRO", "categoryAll");
        
        dynamicView.addViewLink("PRO", "PCM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"));
        dynamicView.addViewLink("PCM", "PC", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productCategoryId"));
        // dynamicView.addViewLink("PRO", "PFA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"));
        // dynamicView.addViewLink("PFA", "PF", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productFeatureId"));

        fieldsToSelect.add("productId");
        fieldsToSelect.add("productName"); 
        fieldsToSelect.add("brandName"); 
        fieldsToSelect.add("featureAll"); 
        fieldsToSelect.add("categoryAll"); 
        
        String sqlString = "";
        if (UtilValidate.isNotEmpty(productPriceRuleId) && !productPriceRuleId.equals("000000")) {
        	sqlString = condSqlString.getSqlString(delegator, "ProductPriceRuleProduct", "productPriceRuleId", productPriceRuleId);
		}
    	if (UtilValidate.isNotEmpty(productSalesPolicyId) && !productSalesPolicyId.equals("000000")) {
    		sqlString = condSqlString.getSqlString(delegator, "ProductSalesPolicyBom", "productSalesPolicyId", productSalesPolicyId);
		}
    	
        mainCond =  EntityCondition.makeConditionWhere(sqlString); 
        
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
			listIt = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, 
					new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, maxRows, true));
			lists = listIt.getCompleteList();
			listSize = lists.size();
			// listIt.close();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	result.put("listIt", listIt);
    	result.put("listSize", listSize);
    	return result;
    }
    /**
     * findStoreList
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> findStoreList(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String productPriceRuleId = (String)context.get("productPriceRuleId");
    	Map<String,Object> result = FastMap.newInstance();
    	
        DynamicViewEntity dynamicView = new DynamicViewEntity();
        // define the main condition  list
        EntityCondition mainCond = null;

        List<String> orderBy = FastList.newInstance();
        List<String> fieldsToSelect = FastList.newInstance();
        // default view settings
        
        dynamicView.addMemberEntity("PS", "ProductStore");
        dynamicView.addMemberEntity("PSA", "ProductStoreAttribute");
        // dynamicView.addMemberEntity("PSAT", "ProductStoreAttributeType");
        dynamicView.addAlias("PS", "productStoreId");
        dynamicView.addAlias("PS", "storeName");
        dynamicView.addAlias("PS", "allAttribute");
        // dynamicView.addAlias("PSA", "attrValue");
        // dynamicView.addAlias("PSAT", "productStoreAttrTypeId");
        
        dynamicView.addViewLink("PS", "PSA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productStoreId"));
        // dynamicView.addViewLink("PSA", "PSAT", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productStoreAttrTypeId"));

        fieldsToSelect.add("productStoreId");
        fieldsToSelect.add("storeName");
        fieldsToSelect.add("allAttribute"); 
        // fieldsToSelect.add("attrValue"); 
        // fieldsToSelect.add("productStoreAttrTypeId"); 
        
        String sqlString = "";
    	if (UtilValidate.isNotEmpty(productPriceRuleId)) {
    		sqlString = condSqlString.getSqlString(delegator, "ProductPriceRuleStore", "productPriceRuleId", productPriceRuleId);
    	}
        mainCond =  EntityCondition.makeConditionWhere(sqlString); 
        
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
			listIt = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, 
					new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, maxRows, true));
			lists = listIt.getCompleteList();
			listSize = lists.size();
			// listIt.close();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	result.put("listIt", listIt);
    	result.put("listSize", listSize);
    	return result;
    }
    /**
     * findRuleList
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> findRuleList(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String productSalesPolicyId = (String)context.get("productSalesPolicyId");
    	
    	List<GenericValue> gvs = FastList.newInstance();
    	List<GenericValue> list = FastList.newInstance();
    	GenericValue productPriceRule = null;
    	try {
			gvs = delegator.findByAnd("ProductSalesPolicyRule", UtilMisc.toMap("productSalesPolicyId", productSalesPolicyId));
			for (GenericValue gv : gvs) {
				productPriceRule = delegator.findByPrimaryKey("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", gv.get("productPriceRuleId")));
				list.add(productPriceRule);
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String,Object> results = FastMap.newInstance();
    	results.put("listIt", list);
    	return results;
    }
    
    
    /**
     * updateProductPriceList
     * @param dctx
     * @param context
     * @return
     */
   public static Map<String, Object> updateProductPriceList(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
    	String productId = (String)context.get("productId");
    	String productSalesPolicyId = (String)context.get("productSalesPolicyId");
    	String productPriceRuleId = (String)context.get("productPriceRuleId");
    	String productPriceTypeId = (String)context.get("productPriceTypeId");
    	String price = (String)context.get("price");
    	String isManual = (String)context.get("isManual");
    	String isManualFlag = "Y";
    	
    	Map<String, String> map = UtilMisc.toMap("productId", productId,
    			"productSalesPolicyId", productSalesPolicyId,
    			"productPriceRuleId", productPriceRuleId, 
    			"productPriceTypeId", productPriceTypeId);
    	
    	if (UtilValidate.isEmpty(isManual)) {
    		price = SalesPriceWorker.getProductPriceById(delegator, productPriceRuleId, productId);
    		isManualFlag = "N";
		}else if(UtilValidate.isNotEmpty(isManual) && UtilValidate.isEmpty(price)) {
			GenericValue gv = null;
			try {
				gv = delegator.findByPrimaryKey("ProductPriceList", map);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			price = (String)gv.get("price");
		}
    	
    	map.put("price", price);
    	map.put("isManual", isManualFlag);
        
    	GenericValue gv = delegator.makeValue("ProductPriceList", map);
    	try {
			gv.store();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess();
    }
    
    /**
     * updateProductPriceList
     * @param dctx
     * @param context
     * @return
     */
   /* public static Map<String, Object> updateProductPriceList(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String productId = (String)context.get("productId");
    	String storeId = (String)context.get("storeId");
    	String productSalesPolicyId = (String)context.get("productSalesPolicyId");
    	String productPriceRuleId = (String)context.get("productPriceRuleId");
    	String productPriceTypeId = (String)context.get("productPriceTypeId");
    	String price = (String)context.get("price");
    	String isManual = (String)context.get("isManual");
    	String flag = (String)context.get("flag");
    	//清除查询cond
    	List<GenericValue> gvs = null;
    	try {
			gvs = delegator.findList("ProductPriceListStore", null, null, null, null, false);
			delegator.removeAll(gvs);
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	Map<String, String> map = UtilMisc.toMap("productId", productId,
    			"productSalesPolicyId", productSalesPolicyId,
    			"productPriceRuleId", productPriceRuleId, 
    			"productPriceTypeId", productPriceTypeId);
    	List<GenericValue> list = null;
		try {
			list = delegator.findByAnd("ProductPriceList", map);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	if (UtilValidate.isEmpty(isManual)) {
    		price = getProductPriceById(dctx, productPriceRuleId, productId);
    		//z更新单条价格清单记录
    		if (flag.equals("Y")) {
    			map.put("storeId", storeId);
            	map.put("price", price);
            	map.put("isManual", "N");
            	GenericValue gv = delegator.makeValue("ProductPriceList", map);
            	try {
            		gv.store();
            	} catch (GenericEntityException e) {
            		e.printStackTrace();
            	}
			}else {
				for (GenericValue genericValue : list) {
					map.put("storeId", (String)genericValue.get("storeId"));
					map.put("price", price);
					map.put("isManual", "N");
					GenericValue gv = delegator.makeValue("ProductPriceList", map);
					try {
						gv.store();
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
				}
			}
		}else if(UtilValidate.isNotEmpty(isManual) && UtilValidate.isNotEmpty(price)) {
			//z更新单条价格清单记录
    		if (flag.equals("Y")) {
    			map.put("storeId", storeId);
            	map.put("price", price);
            	map.put("isManual", "Y");
            	GenericValue gv = delegator.makeValue("ProductPriceList", map);
            	try {
            		gv.store();
            	} catch (GenericEntityException e) {
            		e.printStackTrace();
            	}
			}else {
				//更新所有查询出的门店价格
				for (GenericValue genericValue : list) {
					map.put("storeId", (String)genericValue.get("storeId"));
					map.put("price", price);
		        	map.put("isManual", "Y");
		        	GenericValue productPriceList = delegator.makeValue("ProductPriceList", map);
		        	try {
		        		productPriceList.store();
		    		} catch (GenericEntityException e) {
		    			e.printStackTrace();
		    		}
				}
			}
		}else {
			return ServiceUtil.returnSuccess();
		}
    	return ServiceUtil.returnSuccess();
    }*/
    
   
    /**
     * getProductPriceRules
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> getProductPriceRules(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String partyId = (String)context.get("partyId");
    	
    	List<GenericValue> list = FastList.newInstance();
		try {
			list = delegator.findByAnd("ProductPriceRule", UtilMisc.toMap("partyId", partyId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	Map<String,Object> results = FastMap.newInstance();
    	results.put("ProductPriceRuleList", list);
    	return results;
    }
    /**
     * getProductSalesPolicys
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> getProductSalesPolicys(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String partyId = (String)context.get("partyId");
    	
    	List<GenericValue> list = FastList.newInstance();
		try {
			list = delegator.findByAnd("ProductSalesPolicy", UtilMisc.toMap("partyId", partyId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	Map<String,Object> results = FastMap.newInstance();
    	results.put("ProductSalesPolicyList", list);
    	return results;
    }
    
    public static Map<String, Object>  setPriceListChange(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> resultJson = FastMap.newInstance();
		//GenericValue priceInstance = (GenericValue) context.get("productPriceListInstance");
		//Map<String, Object> priceInstance = FastMap.newInstance();
		//priceInstance.put("", arg1)
		//ProductStoreWorker.setAllProdcutStoreAttribute(delegator, productStoreId);
		try {
			SalesPriceWorker.saveProductPriceListLog(delegator, context);
			SalesPriceWorker.setPriceListChange(delegator, context);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
    	//return null;
    }
    /**
	 * 新增门店，将对应价格规则下的门店添加到ProductPriceRuleStoreList
	 * eecas setProductPriceRuleStoreList
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> setProductPriceRuleStoreList(DispatchContext dctx,
			Map<String, ? extends Object> context) {
		GenericValue productStore = (GenericValue) context.get("productStore");
		// 总店触发
		String strServerType = System.getProperty("posstore.type");
    	if (strServerType.equals("0")){
    		SalesPriceWorker.setProductPriceRuleStoreList(dctx, productStore.get("productStoreId").toString());
    	}
		return ServiceUtil.returnSuccess();
	}
	
	/**
	 * ImportProductPriceList
	 * @param dctx
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> ImportProductPriceList(DispatchContext dctx,
			Map<String, ? extends Object> context) throws Exception {
		GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String userLoginId = (String)userLogin.get("userLoginId");

		ByteBuffer fileData = (ByteBuffer) context.get("uploadFile");
		String fileName = (String) context.get("_uploadFile_fileName");

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
		SalesPriceWorker.saveProductPriceList(path, userLoginId);

		return ServiceUtil.returnSuccess();
	}
}
