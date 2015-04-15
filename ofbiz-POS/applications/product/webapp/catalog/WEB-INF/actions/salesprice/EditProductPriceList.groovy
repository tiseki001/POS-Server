/*
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
 */

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

module = "EditProductPriceList.groovy";

context.hasPermission = security.hasEntityPermission("CATALOG", "_VIEW", session);

// context.nowTimestampString = UtilDateTime.nowTimestamp().toString();

productSalesPolicyId = request.getParameter("productSalesPolicyId");
context.productSalesPolicyId = productSalesPolicyId;

productPriceRuleId = request.getParameter("productPriceRuleId");
context.productPriceRuleId = productPriceRuleId;

productId = request.getParameter("productId");
context.productId = productId;

productName = request.getParameter("productName");
context.productName = productName;

//----------------------------------
    	 DynamicViewEntity dynamicView = new DynamicViewEntity();

         // define the main condition & expression list
         List<EntityCondition> andExprs = FastList.newInstance();
         EntityCondition mainCond = null;

         List<String> orderBy = FastList.newInstance();
         List<String> fieldsToSelect = FastList.newInstance();
         // default view settings
         
         dynamicView.addMemberEntity("PPL", "ProductPriceList");
         dynamicView.addMemberEntity("PRO", "Product");
         dynamicView.addMemberEntity("PPT", "ProductPriceType");
         dynamicView.addMemberEntity("PSP", "ProductSalesPolicy");
         dynamicView.addMemberEntity("PPR", "ProductPriceRule");
         dynamicView.addAlias("PPL", "productPriceTypeId");
         dynamicView.addAlias("PPL", "productPriceRuleId");
         dynamicView.addAlias("PPL", "productSalesPolicyId");
         dynamicView.addAlias("PPL", "productId");
         dynamicView.addAlias("PPL", "price");
         dynamicView.addAlias("PPL", "isManual");
         dynamicView.addAlias("PRO", "productName");
         dynamicView.addAlias("PRO", "brandName");
         dynamicView.addAlias("PPT", "description");
         dynamicView.addAlias("PSP", "policyName");
         dynamicView.addAlias("PPR", "ruleName");
         
         // 一对一关系 内，左外 关联都可行 true：左外关联；false：内关联
         dynamicView.addViewLink("PRO", "PPL", Boolean.FALSE, ModelKeyMap.makeKeyMapList("productId"));
         dynamicView.addViewLink("PPL", "PPT", Boolean.FALSE, ModelKeyMap.makeKeyMapList("productPriceTypeId"));
         dynamicView.addViewLink("PPL", "PSP", Boolean.FALSE, ModelKeyMap.makeKeyMapList("productSalesPolicyId"));
         dynamicView.addViewLink("PPL", "PPR", Boolean.FALSE, ModelKeyMap.makeKeyMapList("productPriceRuleId"));

     	 fieldsToSelect.add("productPriceRuleId");
         fieldsToSelect.add("productPriceTypeId");
         fieldsToSelect.add("productSalesPolicyId");
         fieldsToSelect.add("productId");
         fieldsToSelect.add("price"); 
         fieldsToSelect.add("isManual");
         fieldsToSelect.add("productName"); 
         fieldsToSelect.add("brandName"); 
         fieldsToSelect.add("description");
         fieldsToSelect.add("policyName");
         fieldsToSelect.add("ruleName");   
         
         if (UtilValidate.isNotEmpty(productSalesPolicyId)) {
         	andExprs.add(EntityCondition.makeCondition("productSalesPolicyId", EntityOperator.EQUALS, productSalesPolicyId));
		 }
		 if (UtilValidate.isNotEmpty(productPriceRuleId)) {
         	andExprs.add(EntityCondition.makeCondition("ruleName", EntityOperator.LIKE, "%"+productPriceRuleId+"%"));
		 }
		 if (UtilValidate.isNotEmpty(productId)) {
         	andExprs.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, productId));
		 }
		 if (UtilValidate.isNotEmpty(productName)) {
         	andExprs.add(EntityCondition.makeCondition("productName", EntityOperator.LIKE, "%"+productName+"%"));
		 }
         
         if (andExprs.size() > 0) {
	         mainCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
         }else {
         	andExprs.add(EntityCondition.makeCondition("productId", EntityOperator.EQUALS, "0000000000"));
         	mainCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
         }
         
         EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, -1, false);
         // using list iterator
         EntityListIterator listIt = null;
         List<GenericValue> lists = null;
        
 		listSize = 1;
 		try {
 			listIt = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, findOpts);
 			lists = listIt.getCompleteList();
 			listSize = lists.size();
 			listIt.close();
 		} catch (GenericEntityException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 		
 		highIndex = 0;
		lowIndex = 0;
		
		viewIndex = (viewIndex) ?: 0;
		
		lowIndex = viewIndex * viewSize;
		highIndex = (viewIndex + 1) * viewSize;
		if (listSize < highIndex) {
		    highIndex = listSize;
		}
		
		try {
 			listIt = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, findOpts);
 			lists = listIt.getPartialList(lowIndex + 1, highIndex - lowIndex);
 			listIt.close();
 		} catch (GenericEntityException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
 		
 		
context.lists = lists;
 		
context.viewIndex = viewIndex;
context.viewSize = viewSize;
context.listSize = listSize;
context.lowIndex = lowIndex;
context.highIndex = highIndex;
//----------------------------------

