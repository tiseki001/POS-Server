package org.ofbiz.product.salesprice;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
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


/**
 * 
 * @author Totyu-001
 */
public class SalesPriceWorker {
	
	public final static String module = SalesPriceWorker.class.getName();
	
	/**
     * 判别给定的productPriceTypeId是否存在于productprice中
     * @param delegator
     * @param productId
     * @param productPriceTypeId
     * @return
     */
    public static boolean chackProductPrice(Delegator delegator, String productId, String productPriceTypeId) {
    	List<GenericValue> list = null;
    	try {
			list = delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", productId, "productPriceTypeId", productPriceTypeId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(list)) {
			return true;
		}else {
			return false;
		}
    }
    /**
     * 判别给定的productId是否有价格
     * @param delegator
     * @param productId
     * @param productPriceTypeId
     * @return
     */
    public static boolean chackProductPrice(Delegator delegator, String productId) {
    	List<GenericValue> list = null;
    	try {
			list = delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", productId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(list)) {
			return true;
		}else {
			return false;
		}
    }
    /**
     * 添加价格规则时，生成所有门店对应规则表
     * 备注：现测试数据productPriceRule已经生成；
     * 故：在生成productPriceList时，生成ProductPriceRuleStoreList
     * enable = "N"
     * @param delegator
     * @param productPriceRuleId
     */
    public static void createProductPriceRuleStoreList(Delegator delegator, String productPriceRuleId) {
    	GenericValue gv = null;
    	List<GenericValue> productStores = null;
    	try {
    		productStores = delegator.findList("ProductStore", null, null, null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (UtilValidate.isNotEmpty(productPriceRuleId)) {
			for (GenericValue genericValue : productStores) {
				gv = delegator.makeValue("ProductPriceRuleStoreList", UtilMisc.toMap("productPriceRuleId", productPriceRuleId, 
						"storeId", genericValue.get("productStoreId"),
						"enable", "N"));
				try {
					gv.create();
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
    }
    /**
     * 门店条件改变，会重新生成productPriceList，
     * 故：再改变所有门店对应规则表
     * enable = "Y"
     * @param delegator
     * @param productPriceRuleId
     */
    public static void updateProductPriceRuleStoreList(Delegator delegator, String productPriceRuleId, List<GenericValue> listStore) {
    	GenericValue gv = null;
    	List<GenericValue> productStores = null;
    	try {
    		productStores = delegator.findByAnd("ProductPriceRuleStoreList", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
    		//  此操作目的：更新新增的门店到storeList
    		delegator.removeAll(productStores);
    		createProductPriceRuleStoreList(delegator, productPriceRuleId);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//将条件查询出来的productPriceRuleId对应的记录全设为"enable" = "Y"
		for (GenericValue genericValue : listStore) {
			try {
				gv = delegator.findByPrimaryKey("ProductPriceRuleStoreList", UtilMisc.toMap("productPriceRuleId", productPriceRuleId, 
						"storeId", genericValue.get("productStoreId")));
				gv.set("enable", "Y");
				gv.store();
			} catch (GenericEntityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
    }
    /**
     *新增门店，将对应价格规则下的门店添加到ProductPriceRuleStoreList
     * 
     * @param delegator
     * @param productPriceRuleId
     */
    public static void setProductPriceRuleStoreList(DispatchContext dctx, String storeId) {
    	Delegator delegator = dctx.getDelegator();
    	Map<String, String> map = FastMap.newInstance();
    	
    	GenericValue gv = null;
    	GenericValue entity = null;
    	List<GenericValue> stores = null;
    	List<GenericValue> priceRules = null;
    	List<GenericValue> salesPolicyRules = null;
    	String productPriceRuleId = null;
    	try {
    		priceRules = delegator.findList("ProductPriceRule", null, null, null, null, false);
    		for (GenericValue priceRule : priceRules) {
    			productPriceRuleId = priceRule.get("productPriceRuleId").toString();
    			Map<String, Object> storeMap = SalesPriceWorker.findStoreList(dctx, productPriceRuleId);
    			stores = (List<GenericValue>) storeMap.get("lists");
    			//2种方式添加ProductPriceRuleStoreList
    			updateProductPriceRuleStoreList(delegator, productPriceRuleId, stores);
    			/*for (GenericValue store : stores) {
    				map = UtilMisc.toMap("productPriceRuleId", productPriceRuleId, 
							"storeId", storeId);
    				gv = delegator.findByPrimaryKey("ProductPriceRuleStoreList", map);
					if (store.get("productStoreId").equals(storeId)) {
						if (UtilValidate.isEmpty(gv)) {
							gv = delegator.makeValue("ProductPriceRuleStoreList", map);
							gv.set("enable", "Y");
							gv.create();
						}else {
							gv.set("enable", "Y");
							gv.store();
						}
					}else {
						if (UtilValidate.isEmpty(gv)) {
							gv = delegator.makeValue("ProductPriceRuleStoreList", map);
							gv.set("enable", "N");
							gv.create();
						}
					}
				}*/
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public static String round(String percent, String digit, String diff, String p){
    	Double d = Double.parseDouble(p) * Double.parseDouble(percent) * 0.01;
    	Double pr = null;
    	if (digit.equals("-1") || digit == "-1") {
    		pr = (Math.round(d / 10) * Math.pow(10, 1)) + Double.valueOf(diff);
    		return pr.toString();
		}else if (digit.equals("-2") || digit == "-2") {
			pr = (Math.round(d / 100) * Math.pow(10, 2)) + Double.valueOf(diff);
			return pr.toString();
		}
    	BigDecimal k = new BigDecimal(d.toString());
        BigDecimal price = k.setScale(Integer.parseInt(digit), BigDecimal.ROUND_HALF_UP);
        BigDecimal price1 = price.add(new BigDecimal(diff));
    	return price1.toString();
    }
    /**
     * create ProductPriceList
     * @param dctx
     * @param priceRuleId
     * @param salesPolicyId
     */
    public static void createProductPriceList(Delegator delegator, List<GenericValue> products, 
    		String priceRuleId, String salesPolicyId){
    	//Map<String, Object> productMap = proMap;
    	Map<String, Object> productPriceListMap = FastMap.newInstance();
    	List<GenericValue> listProduct = products;
    	
    	String productPriceTypeId = "";
		String baseTypeId = "";
		GenericValue productPriceRule = null;
		try {
			//条件清除ProductPriceList中isManual = ‘N’的记录
			List<GenericValue> gvs = delegator.findByAnd("ProductPriceList", 
					UtilMisc.toMap("productPriceRuleId", priceRuleId,
							"productSalesPolicyId", salesPolicyId,
							"isManual", "N"));
			delegator.removeAll(gvs);
			
			productPriceRule = delegator.findByPrimaryKey("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", priceRuleId));
			
			productPriceTypeId = (String)productPriceRule.get("productPriceTypeId");
			baseTypeId = (String) productPriceRule.get("baseTypeId");
			
			//EntityListIterator listP = (EntityListIterator)productMap.get("listIt");
			//listProduct = listP.getCompleteList();
			//listProduct = (List<GenericValue>) productMap.get("lists");
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
		GenericValue gv = null;
		String price = "";
		
		if(UtilValidate.isNotEmpty(listProduct)){
			for (GenericValue product : listProduct) {
				boolean b = SalesPriceWorker.chackProductPrice(delegator, product.get("productId").toString(), baseTypeId);
				if (b) {
					price = getProductPriceById(delegator, priceRuleId, (String)product.get("productId"));
					productPriceListMap = UtilMisc.toMap("productId", product.get("productId"), 
							"productSalesPolicyId", salesPolicyId,
							"productPriceRuleId", priceRuleId,
							"productPriceTypeId", productPriceTypeId);
					try {
						gv = delegator.findByPrimaryKey("ProductPriceList", productPriceListMap);
						// 已存在数据，则更新 ,反之 创建
						if (UtilValidate.isNotEmpty(gv)) {
							gv.store();
						}else{
							productPriceListMap.put("isManual", "N");
							productPriceListMap.put("price", price);
							gv = delegator.makeValue("ProductPriceList", productPriceListMap);
							gv.create();
						}
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
    }
    public static String getProductPriceById(Delegator delegator, String productPriceRuleId, String productId){
    	GenericValue productPriceRule = null;
    	List<GenericValue> productPrices = null;
    	String price = "";
    	String basePrice = "";
    	
    	try {
    		productPriceRule = delegator.findByPrimaryKey("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String baseTypeId = (String)productPriceRule.get("baseTypeId");
		String percent = (String)productPriceRule.get("percent");
		String digit = (String)productPriceRule.get("digit");
		String diff = (String)productPriceRule.get("diff");
		
		try {
			productPrices = delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", productId,
    				"productPriceTypeId", baseTypeId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (UtilValidate.isNotEmpty(productPrices)) {
			basePrice = productPrices.get(0).get("price").toString();
			//basePrice = (String)productPrices.get(0).get("price");
		}
		if (UtilValidate.isNotEmpty(basePrice)) {
			price = round(percent, digit, diff, basePrice);
		}
		return price;
    }
    public static String getProductPriceByTypeId(DispatchContext dctx, String productPriceRuleId, String productPriceTypeId, String productId){
    	Delegator delegator = dctx.getDelegator();
    	
    	GenericValue productPriceRule = null;
    	List<GenericValue> productPrices = null;
    	String price = "";
    	String basePrice = "";
    	
    	try {
    		productPriceRule = delegator.findByPrimaryKey("ProductPriceRule", UtilMisc.toMap("productPriceRuleId", productPriceRuleId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String baseTypeId = (String)productPriceRule.get("baseTypeId");
		String percent = (String)productPriceRule.get("percent");
		String digit = (String)productPriceRule.get("digit");
		String diff = (String)productPriceRule.get("diff");
		
		try {
			productPrices = delegator.findByAnd("ProductPrice", UtilMisc.toMap("productId", productId,
    				"productPriceTypeId", productPriceTypeId));
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (UtilValidate.isNotEmpty(productPrices)) {
			basePrice = productPrices.get(0).get("price").toString();
			//basePrice = (String)productPrices.get(0).get("price");
		}
		if (UtilValidate.isNotEmpty(basePrice)) {
			price = round(percent, digit, diff, basePrice);
		}
		return price;
    }
	
	/**
	 *getProductPriceList()
     * 
     */
    public static GenericValue getProductPriceList(String productId, String storeId, Delegator delegator) {
    	List<GenericValue> productPriceLists = null;
    	
        try {
        	productPriceLists = delegator.findByAnd("ProductPriceList", UtilMisc.toMap("productId", productId, "storeId", storeId));
        	if (UtilValidate.isEmpty(productPriceLists)) {
        		return null;
			}else {
				return productPriceLists.get(0);
			}
        } catch (GenericEntityException ex) {
            Debug.logError("Unable to find outstanding purchase orders for product [" + productId + "] and storeId [" + storeId + "] due to " + ex.getMessage() + " - returning null", module);
            return null;
        }
    }
    /**
	 *getProductSalesPolicy()
     * 
     */
    public static List<GenericValue> getProductSalesPolicyById(String productId, String storeId, Delegator delegator) {
    	GenericValue productSalesPolicy = null;
    	List<GenericValue> productSalesPolicys = FastList.newInstance();
    	List<GenericValue> productPriceLists = null;
    	String productSalesPolicyId = "";
    	EntityFindOptions findOptions =  new EntityFindOptions() ;
    	Set<String> fieldsToSelect = FastSet.newInstance();
    	EntityCondition mainCond = null;// 条件
    	List<EntityCondition> andExprs = FastList.newInstance();
        try {
        	andExprs.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
			andExprs.add(EntityCondition.makeCondition("storeId", EntityOperator.EQUALS, storeId));
			mainCond = EntityCondition.makeCondition(andExprs,EntityOperator.AND);
        	findOptions.setDistinct(true);
        	fieldsToSelect.add("productSalesPolicyId");
        	productPriceLists = delegator.findList("ProductPriceListView", mainCond, fieldsToSelect, null, findOptions, false);
        	if (UtilValidate.isNotEmpty(productPriceLists)) {
        		for (GenericValue genericValue : productPriceLists) {
        			productSalesPolicyId = (String)genericValue.get("productSalesPolicyId");
        			productSalesPolicy = delegator.findByPrimaryKey("ProductSalesPolicy", UtilMisc.toMap("productSalesPolicyId", productSalesPolicyId));
        			productSalesPolicys.add(productSalesPolicy);
				}
        		
			}
        	
        	if (UtilValidate.isEmpty(productSalesPolicys)) {
        		return null;
			}else {
				return productSalesPolicys;
			}
        } catch (GenericEntityException ex) {
            Debug.logError("Unable to find outstanding purchase orders for product [" + productSalesPolicy + "] due to " + ex.getMessage() + " - returning null", module);
            return null;
        }
    }
    /**
     * 
     * getProductSalesPolicyBoms
     * @param productId
     * @param storeId
     * @param delegator
     * @return
     */
    public static List<GenericValue> getProductSalesPolicyBoms(String productStoreId, String productSalesPolicyId, Delegator delegator) {
    	
        DynamicViewEntity dynamicView = new DynamicViewEntity();

        // define the main condition  list
        EntityCondition mainCond = null;

        List<EntityCondition> andExprs = FastList.newInstance();
        List<String> orderBy = FastList.newInstance();
        List<String> fieldsToSelect = FastList.newInstance();
        // default view settings
        
        dynamicView.addMemberEntity("PRO", "Product");

        dynamicView.addMemberEntity("PCM", "ProductCategoryMember");
        dynamicView.addMemberEntity("PFA", "ProductFeatureAppl");
        dynamicView.addMemberEntity("PF", "ProductFeature");
        dynamicView.addMemberEntity("PC", "ProductCategory");
        
        dynamicView.addAlias("PRO", "productId");
        dynamicView.addAlias("PRO", "productName");
        dynamicView.addAlias("PRO", "brandName");
        dynamicView.addAlias("PRO", "config");
        dynamicView.addAlias("PRO", "isSequence");
        dynamicView.addAlias("PRO", "sequenceLength");

        
   //     dynamicView.addViewLink("PRO", "II", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"));
        dynamicView.addViewLink("PRO", "PCM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"));
        dynamicView.addViewLink("PRO", "PFA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"));
        dynamicView.addViewLink("PFA", "PF", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productFeatureId"));
        dynamicView.addViewLink("PCM", "PC", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productCategoryId"));

        fieldsToSelect.add("productId");
        fieldsToSelect.add("productName"); 
        fieldsToSelect.add("brandName");
        fieldsToSelect.add("config"); 
        fieldsToSelect.add("isSequence"); 
        fieldsToSelect.add("sequenceLength"); 
        
        String sqlString = condSqlString.getSqlString(delegator, "ProductSalesPolicyBom", "productSalesPolicyId", productSalesPolicyId);
        if (UtilValidate.isNotEmpty(sqlString)) {
        	andExprs.add(EntityCondition.makeConditionWhere(sqlString));
		}
//        if (UtilValidate.isNotEmpty(productStoreId)) {
//        	andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productStoreId"), EntityOperator.EQUALS, EntityFunction.UPPER(productStoreId)));
//		}
       
        if (andExprs.size() > 0) mainCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
        
        // using list iterator
        EntityListIterator listIt = null;
        List<GenericValue> lists = null;
        
		try {
			//(新增判断)
			lists = delegator.findByAnd("ProductSalesPolicyBom",UtilMisc.toMap("productSalesPolicyId",productSalesPolicyId));
			if(UtilValidate.isEmpty(lists)){
				return null;
			}
			
			listIt = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, 
					new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, -1, true));
			lists = listIt.getCompleteList();
			// listIt.close();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return lists;
    }
    /**
     * getProductPriceType
     * @param productId
     * @param storeId
     * @param delegator
     * @return
     */
    public static List<Object> getProductPriceType(String productId, String storeId, String productSalesPolicyId, Delegator delegator) {
    	List<GenericValue> productPriceLists = null;
    	GenericValue productPriceType = null;
    	String productPriceTypeId = "";
    	String productPriceRuleId = "";
    	
    	List<Object> list = FastList.newInstance();
        try {
        	productPriceLists = delegator.findByAnd("ProductPriceListView", UtilMisc.toMap("productId", productId, "storeId", storeId, "productSalesPolicyId", productSalesPolicyId));
        	for (GenericValue genericValue : productPriceLists) {
        		productPriceRuleId = (String)genericValue.get("productPriceRuleId");
        		productPriceTypeId = (String)genericValue.get("productPriceTypeId");
        		productPriceType = delegator.findByPrimaryKey("ProductPriceType", UtilMisc.toMap("productPriceTypeId", productPriceTypeId));
        		list.add(UtilMisc.toMap(
        				"productPriceRuleId", productPriceRuleId,
        				"productPriceTypeId", productPriceTypeId,
        				"description", (String)productPriceType.get("description"), 
        				"price", (String)genericValue.get("price")));
			}
        	
        	if (UtilValidate.isEmpty(productPriceLists)) {
        		return null;
			}else {
				return list;
			}
        } catch (GenericEntityException ex) {
            Debug.logError("Unable to find outstanding purchase orders for product [" + productId + "] and productSalesPolicyId [" + productSalesPolicyId + "] due to " + ex.getMessage() + " - returning null", module);
            return null;
        }
    }
    public static void  setPriceListChange(Delegator delegator, Map<String, ? extends Object> context) throws GenericEntityException {
    	Map<String, Object> pkMap = FastMap.newInstance();
    	pkMap.put("productId", context.get("productId"));

    	pkMap.put("productSalesPolicyId", context.get("productSalesPolicyId"));
    	pkMap.put("productPriceRuleId", context.get("productPriceRuleId"));
    	pkMap.put("productPriceTypeId", context.get("productPriceTypeId"));
    	GenericValue entity;

		entity = delegator.findByPrimaryKey("ProductPriceList", pkMap);
		
		GenericValue entityc;

		entityc = delegator.findByPrimaryKey("PriceListChange", pkMap);
		GenericValue entityn;
		
		if (UtilValidate.isNotEmpty(entity)) {
			if(!entity.get("price").toString().equals(context.get("price"))){
				//entity.set(name, value)
				entityn = GenericValue.create(delegator.getModelEntity("PriceListChange"));
				entityn.set("productId", context.get("productId"));

				entityn.set("productSalesPolicyId", context.get("productSalesPolicyId"));
				entityn.set("productPriceRuleId", context.get("productPriceRuleId"));
				entityn.set("productPriceTypeId", context.get("productPriceTypeId"));
				entityn.set("price", context.get("price"));
				entityn.set("oldPrice", entity.get("price"));
				entityn.set("isSee", "N");
				entityn = delegator.makeValue("PriceListChange", entityn);
				if (!UtilValidate.isNotEmpty(entityc)) {
					
					entityn.create();
				}else{
					entityn.store();
				}
			}
			
		} else {
			entity = GenericValue.create(delegator.getModelEntity("PriceListChange"));
			entity.set("productId", context.get("productId"));

			entity.set("productSalesPolicyId", context.get("productSalesPolicyId"));
			entity.set("productPriceRuleId", context.get("productPriceRuleId"));
			entity.set("productPriceTypeId", context.get("productPriceTypeId"));
			entity.set("price", context.get("price"));
			//entity.set("oldPrice", entity.get("price"));
			entity.set("isSee", "N");
			entity = delegator.makeValue("PriceListChange", entity);
			if (!UtilValidate.isNotEmpty(entityc)) {
				
				entity.create();
			}else{
				entity.store();
			}
		}
    }
    
    public static Map<String, Object> findBomList(DispatchContext dctx, String productPriceRuleId) {
    	Delegator delegator = dctx.getDelegator();
    	
    	//String productPriceRuleId = (String)context.get("productPriceRuleId");
    	Map<String,Object> result = FastMap.newInstance();
    	
        DynamicViewEntity dynamicView = new DynamicViewEntity();

        // define the main condition  list
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
        
        dynamicView.addViewLink("PRO", "PCM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"));
        dynamicView.addViewLink("PCM", "PC", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productCategoryId"));

        fieldsToSelect.add("productId");
        fieldsToSelect.add("productName"); 
        fieldsToSelect.add("brandName"); 
        fieldsToSelect.add("featureAll"); 
        fieldsToSelect.add("categoryAll"); 
        
        String sqlString = "";
        if (UtilValidate.isNotEmpty(productPriceRuleId) ) {
        	sqlString = condSqlString.getSqlString(delegator, "ProductPriceRuleProduct", "productPriceRuleId", productPriceRuleId);
		}
    	
        mainCond =  EntityCondition.makeConditionWhere(sqlString); 
        
        // using list iterator
        EntityListIterator listIt = null;
        List<GenericValue> lists = null;
        
		try {
			listIt = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, 
					new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, -1, true));
			lists = listIt.getCompleteList();
			listIt.close();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	result.put("lists", lists);
    	return result;
    }
    
    public static Map<String, Object> findStoreList(DispatchContext dctx, String productPriceRuleId) {
    	Delegator delegator = dctx.getDelegator();
    	Map<String,Object> result = FastMap.newInstance();
    	
        DynamicViewEntity dynamicView = new DynamicViewEntity();
        // define the main condition  list
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
    	if (UtilValidate.isNotEmpty(productPriceRuleId)) {
    		sqlString = condSqlString.getSqlString(delegator, "ProductPriceRuleStore", "productPriceRuleId", productPriceRuleId);
    	}
        mainCond =  EntityCondition.makeConditionWhere(sqlString); 
        
        // using list iterator
        EntityListIterator listIt = null;
        List<GenericValue> lists = null;
        
		try {
			listIt = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, 
					new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, -1, true));
			lists = listIt.getCompleteList();
			listIt.close();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	result.put("lists", lists);
    	return result;
    }
    
    public static void saveProductPriceList(String path, String userLoginId) throws IOException {
		Map<String, Object> map = FastMap.newInstance();
		String filePath = path;
		String fileType = filePath.substring(filePath.lastIndexOf(".") + 1,
				filePath.length());
		System.out.println(fileType);
		// XSSFWorkbook xssfWorkbook = new XSSFWorkbook(filePath);
		InputStream stream = new FileInputStream(filePath);
		Workbook xssfWorkbook = null;
		if (fileType.equals("xls")) {
			xssfWorkbook = new HSSFWorkbook(stream);
		} else if (fileType.equals("xlsx")) {
			xssfWorkbook = new XSSFWorkbook(filePath);
		} else {
			System.out.println("您输入的excel格式不正确");
		}

		// 循环工作表Sheet
		for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
			Sheet xssfSheet = xssfWorkbook.getSheetAt(0);
			// XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
			if (xssfSheet == null) {
				continue;
			}
			// 循环行Row
			for (int rowNum = 2; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
				Row xssfRow = xssfSheet.getRow(rowNum);
				if (xssfRow == null) {
					continue;
				}
				// 循环列Cell
				for (int cellNum = 0; cellNum <= xssfRow.getLastCellNum(); cellNum++) {
					Cell xssfCell = xssfRow.getCell(cellNum);
					if (xssfCell == null) {
						continue;
					}
					switch (cellNum) {
					case 0:
						map.put("productId", getValue(xssfCell));
						break;
					case 1:
						map.put("productSalesPolicyId", getValue(xssfCell));
						break;
					case 2:
						map.put("productPriceRuleId", getValue(xssfCell));
						break;
					case 3:
						map.put("productPriceTypeId", getValue(xssfCell));
						break;
					case 4:
						map.put("price", getValue(xssfCell));
						break;
					case 5:
						map.put("isManual", getValue(xssfCell));
						break;
					}
				}
				saveEntity("ProductPriceList", map, userLoginId);
			}
		}
	}
    @SuppressWarnings("static-access")
	public static String getValue(Cell xssfCell) {
		if (xssfCell.getCellType() == xssfCell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(xssfCell.getBooleanCellValue());
		} else if (xssfCell.getCellType() == xssfCell.CELL_TYPE_NUMERIC) {
			if (HSSFDateUtil.isCellDateFormatted(xssfCell)) {
				Date date = HSSFDateUtil.getJavaDate(xssfCell
						.getNumericCellValue());
				SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				return dformat.format(date);
			} else {
				DecimalFormat df = new DecimalFormat("0");
				return df.format(xssfCell.getNumericCellValue());
			}

		} else {
			return String.valueOf(xssfCell.getStringCellValue());
		}
	}
    public static void saveEntity(String entityName, Map<String, Object> context, String userLoginId) {
		GenericDelegator delegator = GenericDelegator
				.getGenericDelegator("default");
		String logId = delegator.getNextSeqId("ProductPriceListLog");

		String productId = (String) context.get("productId");
		String productSalesPolicyId = (String) context.get("productSalesPolicyId");
		String productPriceRuleId = (String) context.get("productPriceRuleId");
		String productPriceTypeId = (String) context.get("productPriceTypeId");

		if (UtilValidate.isNotEmpty(productId) &&
				UtilValidate.isNotEmpty(productSalesPolicyId) &&
					UtilValidate.isNotEmpty(productPriceRuleId) &&
						UtilValidate.isNotEmpty(productPriceTypeId)) {
			Map<String, String> pkMap = UtilMisc.toMap("productId",productId,
					 "productSalesPolicyId", productSalesPolicyId, 
					 "productPriceRuleId",productPriceRuleId,
					 "productPriceTypeId", productPriceTypeId);
			GenericValue productPriceListLog = null;
			GenericValue gv = null;
			try {
				gv = delegator.findByPrimaryKey(entityName, pkMap);
				if (UtilValidate.isNotEmpty(gv)) {
					gv.setNonPKFields(context);
					gv.store();
				} else {
					gv = delegator.makeValue(entityName, pkMap);
					gv.setNonPKFields(context);
					gv.create();
				}
				pkMap.put("logId", logId);
				pkMap.put("userLoginId", userLoginId);
				productPriceListLog = delegator.makeValue("ProductPriceListLog", pkMap);
				productPriceListLog.setNonPKFields(context);
				productPriceListLog.create();
			} catch (GenericEntityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
    
    public static void saveProductPriceListLog(Delegator delegator, Map<String, ? extends Object> context) {
    	String logId = delegator.getNextSeqId("ProductPriceListLog");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String partyId = (String)userLogin.get("userLoginId");
    	String isManual = (String)userLogin.get("isManual");
    	
    	Map<String, String> pkMap = UtilMisc.toMap("logId",logId,
				 "userLoginId", partyId,
				 "isManual", isManual);
    	
    	GenericValue productPriceListLog = null;
    	productPriceListLog = delegator.makeValue("ProductPriceListLog", pkMap);
		productPriceListLog.setNonPKFields(context);
		try {
			productPriceListLog.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}
