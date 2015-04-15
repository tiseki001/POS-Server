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
package org.ofbiz.product.promo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.lang.RandomStringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.product.condUtil.condSqlString;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

/**
 * Promotions Services
 */
public class PromoServices {
	
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

    public final static String module = PromoServices.class.getName();
    public static final String resource = "ProductUiLabels";
    protected final static char[] smartChars = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
            'Z', '2', '3', '4', '5', '6', '7', '8', '9' };

    public static Map<String, Object> createProductPromoCodeSet(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Long quantity = (Long) context.get("quantity");
        int codeLength = (Integer) context.get("codeLength");
        String promoCodeLayout = (String) context.get("promoCodeLayout");

        // For PromoCodes we give the option not to use chars that are easy to mix up like 0<>O, 1<>I, ...
        boolean useSmartLayout = false;
        boolean useNormalLayout = false;
        if ("smart".equals(promoCodeLayout)) {
            useSmartLayout = true;
        } else if ("normal".equals(promoCodeLayout)) {
            useNormalLayout = true;
        }

        String newPromoCodeId = "";
        StringBuilder bankOfNumbers = new StringBuilder();
        bankOfNumbers.append("Following PromoCodes have been created: ");
        for (long i = 0; i < quantity; i++) {
            Map<String, Object> createProductPromoCodeMap = null;
            boolean foundUniqueNewCode = false;
            long count = 0;

            while (!foundUniqueNewCode) {
                if (useSmartLayout) {
                    newPromoCodeId = RandomStringUtils.random(codeLength, smartChars);
                } else if (useNormalLayout) {
                    newPromoCodeId = RandomStringUtils.randomAlphanumeric(codeLength);
                }
                GenericValue existingPromoCode = null;
                try {
                    existingPromoCode = delegator.findByPrimaryKeyCache("ProductPromoCode", "productPromoCodeId", newPromoCodeId);
                }
                catch (GenericEntityException e) {
                    Debug.logWarning("Could not find ProductPromoCode for just generated ID: " + newPromoCodeId, module);
                }
                if (existingPromoCode == null) {
                    foundUniqueNewCode = true;
                }

                count++;
                if (count > 999999) {
                    return ServiceUtil.returnError("Unable to locate unique PromoCode! Length [" + codeLength + "]");
                }
            }
            try {
                Map<String, Object> newContext = dctx.makeValidContext("createProductPromoCode", "IN", context);
                newContext.put("productPromoCodeId", newPromoCodeId);
                createProductPromoCodeMap = dispatcher.runSync("createProductPromoCode", newContext);
            } catch (GenericServiceException err) {
                return ServiceUtil.returnError("Could not create a bank of promo codes", null, null, createProductPromoCodeMap);
            }
            if (ServiceUtil.isError(createProductPromoCodeMap)) {
                // what to do here? try again?
                return ServiceUtil.returnError("Could not create a bank of promo codes", null, null, createProductPromoCodeMap);
            }
            bankOfNumbers.append((String) createProductPromoCodeMap.get("productPromoCodeId"));
            bankOfNumbers.append(",");
        }

        return ServiceUtil.returnSuccess(bankOfNumbers.toString());
    }

    public static Map<String, Object> purgeOldStoreAutoPromos(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String productStoreId = (String) context.get("productStoreId");
        Locale locale = (Locale) context.get("locale");
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();

        List<EntityCondition> condList = FastList.newInstance();
        if (UtilValidate.isEmpty(productStoreId)) {
            condList.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, productStoreId));
        }
        condList.add(EntityCondition.makeCondition("userEntered", EntityOperator.EQUALS, "Y"));
        condList.add(EntityCondition.makeCondition("thruDate", EntityOperator.NOT_EQUAL, null));
        condList.add(EntityCondition.makeCondition("thruDate", EntityOperator.LESS_THAN, nowTimestamp));
        EntityCondition cond = EntityCondition.makeCondition(condList, EntityOperator.AND);

        try {
            EntityListIterator eli = delegator.find("ProductStorePromoAndAppl", cond, null, null, null, null);
            GenericValue productStorePromoAndAppl = null;
            while ((productStorePromoAndAppl = eli.next()) != null) {
                GenericValue productStorePromo = delegator.makeValue("ProductStorePromoAppl");
                productStorePromo.setAllFields(productStorePromoAndAppl, true, null, null);
                productStorePromo.remove();
            }
            eli.close();
        } catch (GenericEntityException e) {
            Debug.logError(e, "Error removing expired ProductStorePromo records: " + e.toString(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "ProductPromoCodeCannotBeRemoved", UtilMisc.toMap("errorString", e.toString()), locale));
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> importPromoCodesFromFile(DispatchContext dctx, Map<String, ? extends Object> context) {
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Locale locale = (Locale) context.get("locale");

        // check the uploaded file
        ByteBuffer fileBytes = (ByteBuffer) context.get("uploadedFile");
        if (fileBytes == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "ProductPromoCodeImportUploadedFileNotValid", locale));
        }

        String encoding = System.getProperty("file.encoding");
        String file = Charset.forName(encoding).decode(fileBytes).toString();
        // get the createProductPromoCode Model
        ModelService promoModel;
        try {
            promoModel = dispatcher.getDispatchContext().getModelService("createProductPromoCode");
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        }

        // make a temp context for invocations
        Map<String, Object> invokeCtx = promoModel.makeValid(context, ModelService.IN_PARAM);

        // read the bytes into a reader
        BufferedReader reader = new BufferedReader(new StringReader(file));
        List<Object> errors = FastList.newInstance();
        int lines = 0;
        String line;

        // read the uploaded file and process each line
        try {
            while ((line = reader.readLine()) != null) {
                // check to see if we should ignore this line
                if (line.length() > 0 && !line.startsWith("#")) {
                    if (line.length() > 0 && line.length() <= 20) {
                        // valid promo code
                        Map<String, Object> inContext = FastMap.newInstance();
                        inContext.putAll(invokeCtx);
                        inContext.put("productPromoCodeId", line);
                        Map<String, Object> result = dispatcher.runSync("createProductPromoCode", inContext);
                        if (result != null && ServiceUtil.isError(result)) {
                            errors.add(line + ": " + ServiceUtil.getErrorMessage(result));
                        }
                    } else {
                        // not valid ignore and notify
                        errors.add(line + UtilProperties.getMessage(resource, "ProductPromoCodeInvalidCode", locale));
                    }
                    ++lines;
                }
            }
        } catch (IOException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                Debug.logError(e, module);
            }
        }

        // return errors or success
        if (errors.size() > 0) {
            return ServiceUtil.returnError(errors);
        } else if (lines == 0) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "ProductPromoCodeImportEmptyFile", locale));
        }

        return ServiceUtil.returnSuccess();
    }

    public static Map<String, Object> importPromoCodeEmailsFromFile(DispatchContext dctx, Map<String, ? extends Object> context) {
       LocalDispatcher dispatcher = dctx.getDispatcher();
        String productPromoCodeId = (String) context.get("productPromoCodeId");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
       
        ByteBuffer bytebufferwrapper = (ByteBuffer) context.get("uploadedFile");
    
        if (bytebufferwrapper == null) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "ProductPromoCodeImportUploadedFileNotValid", locale));
        }

        byte[] wrapper =  bytebufferwrapper.array();
       
      // read the bytes into a reader
        BufferedReader reader = new BufferedReader(new StringReader(new String(wrapper)));
        List<Object> errors = FastList.newInstance();
        int lines = 0;
        String line;

        // read the uploaded file and process each line
        try {
            while ((line = reader.readLine()) != null) {
                if (line.length() > 0 && !line.startsWith("#")) {
                    if (UtilValidate.isEmail(line)) {
                        // valid email address
                        Map<String, Object> result = dispatcher.runSync("createProductPromoCodeEmail", UtilMisc.<String, Object>toMap("productPromoCodeId",
                                productPromoCodeId, "emailAddress", line, "userLogin", userLogin));
                        if (result != null && ServiceUtil.isError(result)) {
                            errors.add(line + ": " + ServiceUtil.getErrorMessage(result));
                        }
                    } else {
                        // not valid ignore and notify
                        errors.add(line + ": is not a valid email address");
                    }
                    ++lines;
                }
            }
        } catch (IOException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } catch (GenericServiceException e) {
            Debug.logError(e, module);
            return ServiceUtil.returnError(e.getMessage());
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                Debug.logError(e, module);
            }
        }

        // return errors or success
        if (errors.size() > 0) {
            return ServiceUtil.returnError(errors);
        } else if (lines == 0) {
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, 
                    "ProductPromoCodeImportEmptyFile", locale));
        }

        return ServiceUtil.returnSuccess();
    }
    /**
     * createPromotionBasicInformation
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> createPromotionBasicInformation(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String productPromoId = (String)context.get("productPromoId");
        GenericValue productPromo ;
        
        try {
			productPromo = delegator.findByPrimaryKey("ProductPromo", UtilMisc.toMap("productPromoId", productPromoId));
			if (UtilValidate.isNotEmpty(productPromo)) {
				productPromo.setNonPKFields(context);
				productPromo.store();
			}else {
				productPromo = delegator.makeValue("ProductPromo", UtilMisc.toMap("productPromoId", productPromoId));
				productPromo.setNonPKFields(context);
				productPromo.create();
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        Map<String, Object> results = ServiceUtil.returnSuccess();
        results.put("productPromoId", productPromoId);
        return results;
    }
    
    
    /**
     * 添加促销商品条件
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> createProductPromoProductCond(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String rowCount = (String) context.get("rowCount");
    	String fieldName = (String)context.get("fieldName");
    	String seqnum = "";
    	String productPromoId = (String)context.get("productPromoId");
    	Map<String, String> productCondMap = UtilMisc.toMap("productPromoId", productPromoId);
    	
    	List<GenericValue> dels = null;
    	
    	try {
    		if (UtilValidate.isNotEmpty(rowCount) && rowCount.equals("delete")) {
    			dels = delegator.findByAnd("ProductPromoProductCond", productCondMap);
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
    				gvs = delegator.findByAnd("ProductPromoProductCond", productCondMap);
    				delegator.removeAll(gvs);
    			} catch (GenericEntityException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			}
    		}
    		seqnum = delegator.getNextSeqId("ProductPromoProductCond");
			gv = delegator.makeValue("ProductPromoProductCond", productCondMap);
    		gv.setNonPKFields(context);
    		gv.set("seqnum", seqnum);
    		
    		String formula = "";
    		if(fieldName.equals("CATEGORY") || fieldName.equals("productCategoryId")){
    			gv.set("tableName", "product_category_member");
    			gv.set("fieldName", "productCategoryId");
    			formula = "PCM.product_category_id"+entityOperators.get(gv.get("operate"))+"'"+context.get("value1")+"'";
    			gv.set("formula", formula);
    		}else if(fieldName.equals("productId") || fieldName.equals("productName")) {
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
     * findProductList
     * 	condition from table productPromoProductCond
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> findProductList(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String productPromoId = (String)context.get("productPromoId");
    	//	配套礼品condition 主键ID
    	String productPromoRangeId = (String)context.get("productPromoRangeId");
    	
    	Map<String,Object> result = FastMap.newInstance();
    	
        DynamicViewEntity dynamicView = new DynamicViewEntity();
        
        EntityCondition mainCond = null;

        List<String> orderBy = FastList.newInstance();
        List<String> fieldsToSelect = FastList.newInstance();
        // default view settings
        
        dynamicView.addMemberEntity("PRO", "Product");
        dynamicView.addMemberEntity("PCM", "ProductCategoryMember");
        dynamicView.addMemberEntity("PC", "ProductCategory");
        dynamicView.addAlias("PRO", "productId");
        dynamicView.addAlias("PRO", "productName");
        dynamicView.addAlias("PRO", "brandName");
        dynamicView.addAlias("PRO", "featureAll");
        dynamicView.addAlias("PRO", "categoryAll");
        
        //  true：左外关联；false：内关联
        dynamicView.addViewLink("PRO", "PCM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"));
        dynamicView.addViewLink("PCM", "PC", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productCategoryId"));

        fieldsToSelect.add("productId");
        fieldsToSelect.add("productName"); 
        fieldsToSelect.add("brandName");
        fieldsToSelect.add("featureAll");
        fieldsToSelect.add("categoryAll");
        
        String sqlString = "";
    	if (UtilValidate.isNotEmpty(productPromoId)) {
    		sqlString = condSqlString.getSqlString(delegator, "ProductPromoProductCond", "productPromoId", productPromoId);
    	}
    	if(UtilValidate.isNotEmpty(productPromoRangeId)){
    		sqlString = condSqlString.getSqlString(delegator, "ProductPromoGiftProduct", "productPromoRangeId", productPromoRangeId);
    	}
        mainCond =  EntityCondition.makeConditionWhere(sqlString); 
        
        // using list iterator
        EntityListIterator listIt = null;
        int listSize = 0;
        
        Integer viewSize = (Integer) context.get("viewSize");
        Integer viewIndex = (Integer) context.get("viewIndex");
        Integer maxRows = null;
        if (viewSize != null && viewIndex != null) {
            maxRows = viewSize * (viewIndex + 1);
        }
        maxRows = maxRows != null ? maxRows : -1;
        
        EntityConditionList<EntityCondition> entityConditionList = null;
		try {
			listIt = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, 
					new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, maxRows, true));
			listSize = listIt.getResultsSizeAfterPartialList();
			int listSize1 = listIt.getCompleteList().size();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	result.put("listIt", listIt);
    	result.put("listSize", listSize);
    	return result;
    }
    /**
     * findSpecialProductList 特价商品
     * 	condition from table productPromoProductCond
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> findSpecialProductList(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String productPromoId = (String)context.get("productPromoId");
    	//特价商品
    	String findType = (String)context.get("findType");
    	
    	Map<String,Object> result = FastMap.newInstance();
    	
        DynamicViewEntity dynamicView = new DynamicViewEntity();
        
        EntityCondition mainCond = null;

        List<String> orderBy = FastList.newInstance();
        List<String> fieldsToSelect = FastList.newInstance();
        // default view settings
        
        dynamicView.addMemberEntity("PRO", "Product");
        dynamicView.addMemberEntity("PCM", "ProductCategoryMember");
        dynamicView.addMemberEntity("PC", "ProductCategory");
        dynamicView.addMemberEntity("PPSP", "ProductPromoSpecialProduct");
        dynamicView.addAlias("PRO", "productId");
        dynamicView.addAlias("PRO", "productName");
        dynamicView.addAlias("PRO", "brandName");
        dynamicView.addAlias("PRO", "featureAll");
        dynamicView.addAlias("PRO", "categoryAll");
        dynamicView.addAlias("PPSP", "checkPrice");
        dynamicView.addAlias("PPSP", "guidePrice");
        dynamicView.addAlias("PPSP", "salesPrice");
        
        //  true：左外关联；false：内关联
        dynamicView.addViewLink("PRO", "PCM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"));
        dynamicView.addViewLink("PRO", "PPSP", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"));
        dynamicView.addViewLink("PCM", "PC", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productCategoryId"));

        fieldsToSelect.add("productId");
        fieldsToSelect.add("productName"); 
        fieldsToSelect.add("brandName");
        fieldsToSelect.add("checkPrice");
        fieldsToSelect.add("guidePrice");
        fieldsToSelect.add("salesPrice");
        fieldsToSelect.add("featureAll");
        fieldsToSelect.add("categoryAll");
        
        String sqlString = "";
    	if (UtilValidate.isNotEmpty(productPromoId)) {
    		sqlString = condSqlString.getSqlString(delegator, "ProductPromoSPCond", "productPromoId", productPromoId);
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
        
        // String productPrice = "";
        
		try {
			listIt = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, 
					new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, maxRows, true));
			listSize = listIt.getResultsSizeAfterPartialList();
			lists = listIt.getCompleteList();
			/*if (UtilValidate.isNotEmpty(findType)) {
				for (GenericValue gv : listIt.getCompleteList()) {
					List<GenericValue> gvs = delegator.findByAnd("ProductPromoSpecialProduct", UtilMisc.toMap("productPromoId", productPromoId, "productId", gv.get("productId")));
					if (UtilValidate.isNotEmpty(gvs)) {
						productPrice = (String)gvs.get(0).get("price");
						gv.set("priceDetailText", productPrice);
					}
				}
			}*/
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	result.put("lists", listIt);
    	result.put("listSize", listSize);
    	return result;
    }
    /**
     * 添加促销店铺条件
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> createProductPromoStore(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String rowCount = (String) context.get("rowCount");
    	String fieldName = (String)context.get("fieldName");
    	String productPromoId = (String)context.get("productPromoId");
    	String seqnum = "";
    	List<GenericValue> dels = null;
    	Map<String, String> productCondMap = UtilMisc.toMap("productPromoId", productPromoId);
    	
    	try {
    		if (UtilValidate.isNotEmpty(rowCount) && rowCount.equals("delete")) {
    			dels = delegator.findByAnd("ProductPromoStore", productCondMap);
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
    				gvs = delegator.findByAnd("ProductPromoStore", productCondMap);
    				delegator.removeAll(gvs);
    			} catch (GenericEntityException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			}
    		}
    		
    		seqnum = delegator.getNextSeqId("ProductPromoStore");
			gv = delegator.makeValue("ProductPromoStore", productCondMap);
    		gv.setNonPKFields(context);
    		gv.set("seqnum", seqnum);
    		gv.set("tableName", "product_store_attribute");
    		
    		StringBuffer formula = null;
    		if (context.get("fieldName").equals("storeName")) {
    			formula = new StringBuffer("");
        		if (context.get("operate").equals("LIKE")) {
        			formula.append(" PS.store_name like '%");
            		formula.append(context.get("value1"));
            		formula.append("%' ");
    			}else {
    				formula.append(" PS.store_name ='");
    				formula.append(context.get("value1"));
    				formula.append("' ");
    			}
			}else {
				formula = new StringBuffer(" (PSA.product_store_attr_type_id = '");
				formula.append(context.get("fieldName"));
				formula.append("' and PSA.attr_value "+entityOperators.get(gv.get("operate"))+" '");
				formula.append(context.get("value1"));
				formula.append("') ");
			}
    		
			gv.set("formula", formula.toString());
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
     * findStoreList
     * condition from table productPromoStore
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> findStoreList(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String productPromoId = (String)context.get("productPromoId");
    	Map<String,Object> result = FastMap.newInstance();
    	
        DynamicViewEntity dynamicView = new DynamicViewEntity();
        // define the main condition list
        EntityCondition mainCond = null;

        List<String> orderBy = FastList.newInstance();
        List<String> fieldsToSelect = FastList.newInstance();
        // default view settings
        
        dynamicView.addMemberEntity("PS", "ProductStore");
        dynamicView.addMemberEntity("PSA", "ProductStoreAttribute");
        dynamicView.addAlias("PS", "productStoreId");
        dynamicView.addAlias("PS", "storeName");
        dynamicView.addAlias("PS", "allAttribute");
        
        dynamicView.addViewLink("PS", "PSA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productStoreId"));

        fieldsToSelect.add("productStoreId");
        fieldsToSelect.add("storeName");
        fieldsToSelect.add("allAttribute");
        
        String sqlString = "";
    	if (UtilValidate.isNotEmpty(productPromoId)) {
    		sqlString = condSqlString.getSqlString(delegator, "ProductPromoStore", "productPromoId", productPromoId);
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
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	result.put("listIt", listIt);
    	result.put("listSize", listSize);
    	return result;
    }
    /**
     * 添加促销顾客条件
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> createProductPromoParty(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String rowCount = (String) context.get("rowCount");
    	String fieldName = (String)context.get("fieldName");
    	String productPromoId = (String)context.get("productPromoId");
    	Map<String, String> productCondMap = UtilMisc.toMap("productPromoId", productPromoId);
    	String seqnum = "";
    	GenericValue gv = null;
    	List<GenericValue> gvs = null;
    	List<GenericValue> dels = null;
    	
    	try {
    		if (UtilValidate.isNotEmpty(rowCount) && rowCount.equals("delete")) {
    			dels = delegator.findByAnd("ProductPromoParty", productCondMap);
    			delegator.removeAll(dels);
			}
		} catch (GenericEntityException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
    	
    	if(UtilValidate.isNotEmpty(fieldName)){
    		if(rowCount.equals("2")){
    			try {
    				gvs = delegator.findByAnd("ProductPromoParty", productCondMap);
    				delegator.removeAll(gvs);
    			} catch (GenericEntityException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			}
    		}
    		seqnum = delegator.getNextSeqId("ProductPromoParty");
			gv = delegator.makeValue("ProductPromoParty", productCondMap);
    		gv.setNonPKFields(context);
    		gv.set("seqnum", seqnum);
    		gv.set("tableName", "Person");
    		gv.set("fieldName", context.get("fieldName"));
    		
    		String formula = "";
    		if (context.get("operate").equals("LIKE")) {
    			formula = "PE."+context.get("fieldName")+entityOperators.get(gv.get("operate"))+"'%"+context.get("value1")+"%'";
			}else {
				formula = "PE."+context.get("fieldName")+entityOperators.get(gv.get("operate"))+"'"+context.get("value1")+"'";
			}
			
			gv.set("formula", formula);
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
     * findStoreList
     * condition from table productPromoStore
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> findCustomerList(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
        
        String productPromoId = (String)context.get("productPromoId");
        List<GenericValue> partyList = null;
        DynamicViewEntity dynamicView = new DynamicViewEntity();
        Map<String,Object> result = FastMap.newInstance();

        // define the main condition list
        EntityCondition mainCond = null;

        List<String> orderBy = FastList.newInstance();
        List<String> fieldsToSelect = FastList.newInstance();
        // default view settings
        
        dynamicView.addMemberEntity("PE", "Person");
        dynamicView.addMemberEntity("PR", "PartyRole");
        dynamicView.addMemberEntity("PCF", "PartyClassification");
        dynamicView.addMemberEntity("PCG", "PartyClassificationGroup");
        dynamicView.addAlias("PE", "partyId");
        dynamicView.addAlias("PE", "lastName");
        dynamicView.addAlias("PE", "firstName");
        dynamicView.addAlias("PE", "cardId");
        dynamicView.addAlias("PE", "phoneMobile");
        dynamicView.addAlias("PE", "gender");
        dynamicView.addAlias("PCF", "partyClassificationGroupId");
        dynamicView.addAlias("PCG", "description");
        
        dynamicView.addViewLink("PE", "PR", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
        dynamicView.addViewLink("PE", "PCF", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
        dynamicView.addViewLink("PCF", "PCG", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyClassificationGroupId"));

        fieldsToSelect.add("partyId");
        fieldsToSelect.add("cardId");
        fieldsToSelect.add("firstName");
        fieldsToSelect.add("lastName");
        fieldsToSelect.add("phoneMobile");
        fieldsToSelect.add("gender");
        fieldsToSelect.add("description");
        
        orderBy.add("partyId");
        
        dynamicView.addMemberEntity("PCMP2", "PartyContactMechPurpose");
        dynamicView.addMemberEntity("PA", "PostalAddress");
        dynamicView.addAlias("PA", "address1");
        dynamicView.addAlias("PCMP2", "contactMechPurposeTypeId2", "contactMechPurposeTypeId", null, null, null, null);
        dynamicView.addViewLink("PE", "PCMP2", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
        dynamicView.addViewLink("PCMP2", "PA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("contactMechId"));
        fieldsToSelect.add("address1");
        
        
        //StringBuffer sb = new StringBuffer("(PCMP2.contact_mech_purpose_type_id = 'GENERAL_LOCATION') ");
        StringBuffer sb = new StringBuffer("(PR.role_type_id = 'CUSTOMER') ");
        
        String sqlString = "";
    	if (UtilValidate.isNotEmpty(productPromoId)) {
    		sqlString = condSqlString.getSqlString(delegator, "ProductPromoParty", "productPromoId", productPromoId);
    	}
    	
    	if (UtilValidate.isNotEmpty(sqlString)) {
    		sqlString = sb.toString() + " and " + sqlString;
		}else {
			sqlString = sb.toString();
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
					new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, maxRows, false));
			listSize = listIt.getResultsSizeAfterPartialList();;
			//lists = listIt.getCompleteList();
			//listIt.close();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*List<Object> list = FastList.newInstance();
		for (GenericValue pl : lists) {
			list.add(UtilMisc.toMap("partyId", (String)pl.get("partyId"), 
					"name", (String)(pl.get("lastName") == null ? "" : pl.get("lastName")) 
							+ (pl.get("firstName") == null ? "" : pl.get("firstName")),
					"sex", pl.get("gender"),
					"phoneMobile", pl.get("phoneMobile"),
					"cardId", pl.get("cardId"), 
					"description", pl.get("description"), 
					"address1", pl.get("address1")
                    ));
		} */
		result.put("listSize", listSize);
		result.put("listIt", listIt);
    	return result;
    }
    /**
     * 创建特价商品 与礼品套包条件
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> createProductPromoCond(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String rowCount = (String) context.get("rowCount");
    	String fieldName = (String)context.get("fieldName");
    	String seqnum = "";
    	String productPromoId = (String)context.get("productPromoId");
    	String productPromoRangeId = (String)context.get("productPromoRangeId");
    	String productType = (String)context.get("productType");
    	
    	Map<String, String> productCondMap = UtilMisc.toMap("productPromoId", productPromoId);
    	List<GenericValue> dels = null;
    	
    	try {
    		if (UtilValidate.isNotEmpty(rowCount) && rowCount.equals("delete")) {
    			if (productType.equals("CreateProductPromoSpecialProduct")) {	//特价商品
    				dels = delegator.findByAnd("ProductPromoSPCond", productCondMap);
				}else if (productType.equals("CreateProductPromoGiftProduct")) {	// 配套礼品
					dels = delegator.findByAnd("ProductPromoGiftProduct", productCondMap);
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
    				if (productType.equals("CreateProductPromoSpecialProduct")) {	//特价商品
    					gvs = delegator.findByAnd("ProductPromoSPCond", productCondMap);
					}else if (productType.equals("CreateProductPromoGiftProduct")) {	// 配套礼品
						gvs = delegator.findByAnd("ProductPromoGiftProduct", UtilMisc.toMap("productPromoRangeId", productPromoRangeId));
					}
    				delegator.removeAll(gvs);
    			} catch (GenericEntityException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			}
    		}
    		if (productType.equals("CreateProductPromoSpecialProduct")) {
    			seqnum = delegator.getNextSeqId("ProductPromoSPCond");
    			gv = delegator.makeValue("ProductPromoSPCond", productCondMap);
			}else if (productType.equals("CreateProductPromoGiftProduct")) {
				seqnum = delegator.getNextSeqId("ProductPromoGiftProduct");
    			gv = delegator.makeValue("ProductPromoGiftProduct", UtilMisc.toMap("productPromoRangeId", productPromoRangeId));
			}
    		gv.setNonPKFields(context);
    		gv.set("seqnum", seqnum);
    		
    		String formula = "";
    		if(fieldName.equals("CATEGORY") || fieldName.equals("productCategoryId")){
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
     * 更新特价商品
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> updateProductPromoSpecialProduct(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        String productPromoId = (String)context.get("productPromoId");
        String productId = (String)context.get("productId");
        String checkPrice = (String)context.get("checkPrice");
        String guidePrice = (String)context.get("guidePrice");
        String salesPrice = (String)context.get("salesPrice");
        String productPromoSPCondId = delegator.getNextSeqId("ProductPromoSpecialProduct");
    	String seqnum = delegator.getNextSeqId("ProductPromoSpecialProduct");
    	
    	Map<String, String> productCondMap = UtilMisc.toMap("productPromoId", productPromoId, "productId", productId);
    	
    	GenericValue gv = null;
    	List<GenericValue> gvs = null;
    	
    	if (UtilValidate.isNotEmpty(productId)) {
    		try {
    			gvs = delegator.findByAnd("ProductPromoSpecialProduct", productCondMap);
				//当前条件记录不为null,&& productPrice != null
				if (UtilValidate.isNotEmpty(gvs)) {
					gv = gvs.get(0);
					gv.set("checkPrice", checkPrice);
					gv.set("guidePrice", guidePrice);
					gv.set("salesPrice", salesPrice);
					gv.store();
				}else if(UtilValidate.isNotEmpty(gvs) && !UtilValidate.isNotEmpty(checkPrice) &&
						!UtilValidate.isNotEmpty(guidePrice) && !UtilValidate.isNotEmpty(salesPrice)){
					gv = gvs.get(0);
					gv.remove();
				}else if(!UtilValidate.isNotEmpty(gvs)){
					gv = delegator.makeValue("ProductPromoSpecialProduct", UtilMisc.toMap("productPromoId", productPromoId, 
							"productId", productId,
							"productPromoSPCondId", productPromoSPCondId,
							"seqnum", seqnum,
							"checkPrice", checkPrice,
							"guidePrice", guidePrice,
							"salesPrice", salesPrice));
					gv.create();
				}
				
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        return ServiceUtil.returnSuccess();
    }
    /**
     * getProductCategorysByProductId
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> getProductCategorysByProductId(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	
    	String productId = (String)context.get("fieldName");
    	Map<String,Object> result = FastMap.newInstance();
    	List<String> lists = FastList.newInstance();
    	List<GenericValue> gvs = null;
    	String categoryName = "";
    	if (UtilValidate.isNotEmpty(productId)) {
    		try {
    			gvs = delegator.findByAnd("ProductCategoryAndMember", UtilMisc.toMap("productId", productId));
				for (GenericValue gv : gvs) {
					categoryName = (String)gv.get("categoryName") == null ? "" : (String)gv.get("categoryName");
					lists.add("(" + categoryName + ")" + gv.get("productCategoryId") + 
							": " + gv.get("productCategoryId"));
				}
    		} catch (GenericEntityException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
		}
		
    	result.put("productCategoryList", lists);
    	return result;
    }
    /**
     * getProductFeaturesByProductId
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> getProductFeaturesByProductId(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	
    	String productId = (String)context.get("fieldName");
    	Map<String,Object> result = FastMap.newInstance();
    	List<String> lists = FastList.newInstance();
    	List<GenericValue> gvs = null;
    	String description = "";
    	if (UtilValidate.isNotEmpty(productId)) {
    		try {
    			gvs = delegator.findByAnd("ProductFeatureAndAppl", UtilMisc.toMap("productId", productId));
				for (GenericValue gv : gvs) {
					description = (String)gv.get("description") == null ? "" : (String)gv.get("description");
					lists.add("(" + description + ")" + gv.get("productFeatureCategoryId") + 
							": " + gv.get("productFeatureId"));
				}
    		} catch (GenericEntityException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
		}
		
    	result.put("productFeatureList", lists);
    	return result;
    }
    /**
     * findSerialList
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> findSerialList(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String productPromoId = (String)context.get("productPromoId");
    	Map<String,Object> result = FastMap.newInstance();
    	
        DynamicViewEntity dynamicView = new DynamicViewEntity();
        // define the main condition list
        EntityCondition mainCond = null;

        List<String> orderBy = FastList.newInstance();
        List<String> fieldsToSelect = FastList.newInstance();
        // default view settings
        
        dynamicView.addMemberEntity("PPSS", "ProductPromoSpecialserial");
        dynamicView.addAlias("PPSS", "productPromoSPCondId");
        dynamicView.addAlias("PPSS", "productPromoId");
        dynamicView.addAlias("PPSS", "SequenceId");
        dynamicView.addAlias("PPSS", "productId");
        dynamicView.addAlias("PPSS", "seqnum");
        dynamicView.addAlias("PPSS", "price");

        fieldsToSelect.add("productPromoSPCondId");
        fieldsToSelect.add("productPromoId"); 
        fieldsToSelect.add("SequenceId");
        fieldsToSelect.add("productId"); 
        fieldsToSelect.add("seqnum");
        fieldsToSelect.add("price"); 
        
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
					new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, maxRows, false));
			lists = listIt.getCompleteList();
			listSize = lists.size();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	result.put("listIt", listIt);
    	result.put("listSize", listSize);
    	return result;
    }
    /**
     * createProductPromoSpecialserial
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> createProductPromoSpecialserial(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String productPromoId = (String)context.get("productPromoId");
    	String sequenceId = (String)context.get("sequenceId");
    	String checkPrice = (String)context.get("checkPrice");
        String guidePrice = (String)context.get("guidePrice");
        String salesPrice = (String)context.get("salesPrice");
    	String productPromoSPCondId = delegator.getNextSeqId("ProductPromoSpecialserial");
    	String seqnum = delegator.getNextSeqId("ProductPromoSpecialserial");
    	String productId = "";
    	
    	List<GenericValue> productSequences = null;
    	try {
    		productSequences = delegator.findByAnd("ProductSequence", UtilMisc.toMap("sequenceId", sequenceId));
    		productId = (String)productSequences.get(0).get("productId");
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	Map<String, String> map = UtilMisc.toMap("productPromoSPCondId", productPromoSPCondId,
    			"productPromoId", productPromoId, 
    			"sequenceId", sequenceId,
    			"productId", productId,
    			"seqnum", seqnum,
    			"checkPrice", checkPrice,
				"guidePrice", guidePrice,
				"salesPrice", salesPrice);
    	GenericValue gv = delegator.makeValue("ProductPromoSpecialserial", map);
    	try {
			gv.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess();
    }
    /**
     * deleteProductPromoSpecialserial
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> deleteProductPromoSpecialserial(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String productPromoSPCondId = (String)context.get("productPromoSPCondId");
    	
    	Map<String, String> map = UtilMisc.toMap("productPromoSPCondId", productPromoSPCondId);
    			
    	List<GenericValue> gvs;
		try {
			gvs = delegator.findByAnd("ProductPromoSpecialserial", map);
			gvs.get(0).remove();
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	return ServiceUtil.returnSuccess();
    }
    /**
     * findNoPrameList
     * condition from table productPromoStore
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> findNotPrameList(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String productPromoId = (String) context.get("productPromoId");
    	String entityName = (String) context.get("entityName");
    	String orderBy = (String) context.get("orderBy");
    	Map<String,Object> result = FastMap.newInstance();
    	List<String> orderByList = FastList.newInstance();
    	orderByList.add(orderBy);
    	

        // using list iterator
        EntityListIterator listIt = null;
        int listSize = 0;
        
        Integer viewSize = (Integer) context.get("viewSize");
        Integer viewIndex = (Integer) context.get("viewIndex");
        Integer maxRows = null;
        if (viewSize != null && viewIndex != null) {
            maxRows = viewSize * (viewIndex + 1);
        }
        maxRows = maxRows != null ? maxRows : -1;
        
        EntityCondition cond = EntityCondition.makeCondition("productPromoId", EntityOperator.EQUALS, productPromoId);
		try {
			listIt = delegator.find(entityName, cond, null, null, orderByList, 
					new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, maxRows, false));
            listSize = listIt.getResultsSizeAfterPartialList();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	result.put("listIt", listIt);
    	result.put("listSize", listSize);
    	return result;
    }
}
