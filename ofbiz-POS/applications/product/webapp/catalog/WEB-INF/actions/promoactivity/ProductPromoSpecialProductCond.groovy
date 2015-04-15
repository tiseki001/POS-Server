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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

module = "ProductPromoSpecialProductCond.groovy";

context.hasPermission = security.hasEntityPermission("CATALOG", "_VIEW", session);

context.nowTimestampString = UtilDateTime.nowTimestamp().toString();

productPromoId = request.getParameter("productPromoId");
context.productPromoId = productPromoId;

    	Map<String,Object> result = FastMap.newInstance();
    	
        DynamicViewEntity dynamicView = new DynamicViewEntity();
        
        try {
			List<GenericValue> gvs = delegator.findByAnd("ProductCategoryAndMember", UtilMisc.toMap("productId", "1111"));
			for (GenericValue gv : gvs) {
				// System.out.println((String)gv.get("productCategoryId")+(String)gv.get("categoryName"));
			}
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        
        // define the main condition & expression list
        List<EntityCondition> andExprs = FastList.newInstance();
        EntityCondition mainCond = null;

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
        dynamicView.addAlias("PRO", "priceDetailText");
        
        // 一对一关系 内，左外 关联都可行 true：左外关联；false：内关联
        // 一对多；先true,后false
        dynamicView.addViewLink("PRO", "PCM", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"));
        dynamicView.addViewLink("PRO", "PFA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productId"));
        dynamicView.addViewLink("PFA", "PF", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productFeatureId"));
        dynamicView.addViewLink("PCM", "PC", Boolean.FALSE, ModelKeyMap.makeKeyMapList("productCategoryId"));

        fieldsToSelect.add("productId");
        fieldsToSelect.add("productName"); 
        fieldsToSelect.add("brandName"); 
        fieldsToSelect.add("priceDetailText"); 
        
        String sqlString = "";
    	if (UtilValidate.isNotEmpty(productPromoId)) {
    		sqlString = condSqlString.getSqlString(delegator, "ProductPromoProductCond", "productPromoId", productPromoId);
    	}else if(UtilValidate.isNotEmpty(productPromoRangeId)){
    		sqlString = condSqlString.getSqlString(delegator, "ProductPromoGiftProduct", "productPromoRangeId", productPromoRangeId);
    	}
        mainCond =  EntityCondition.makeConditionWhere(sqlString); 
        
        // using list iterator
        EntityListIterator listIt = null;
        List<GenericValue> lists = null;
        String productPrice = "";
        int listSize = 0;
		try {
			listIt = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, 
					new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, -1, false));
			lists = listIt.getCompleteList();
			for (GenericValue gv : lists) {
				List<GenericValue> gvs = delegator.findByAnd("ProductPromoSpecialProduct", UtilMisc.toMap("productPromoId", productPromoId, "productId", gv.get("productId")));
				if (UtilValidate.isNotEmpty(gvs)) {
					productPrice = (String)gvs.get(0).get("price");
					gv.set("priceDetailText", productPrice);
				}
			}
			listIt.close();
			listSize = lists.size();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	context.listSize = listSize;
 		context.lists = lists;
