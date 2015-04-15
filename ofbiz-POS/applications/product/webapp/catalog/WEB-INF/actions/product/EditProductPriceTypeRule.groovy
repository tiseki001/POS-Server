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

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityTypeUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.product.product.ProductWorker;

// context.nowTimestampString = UtilDateTime.nowTimestamp().toString();

productPriceTypeId = request.getParameter("productPriceTypeId");
context.productPriceTypeId = productPriceTypeId;

ProductWorker.setProductPriceTypeRule(delegator, productPriceTypeId);
		
 DynamicViewEntity dynamicView = new DynamicViewEntity();

 // define the main condition & expression list
 List<EntityCondition> andExprs = FastList.newInstance();
 EntityCondition mainCond = null;

 List<String> orderBy = FastList.newInstance();
 List<String> fieldsToSelect = FastList.newInstance();
 // default view settings
 
 dynamicView.addMemberEntity("RT", "RoleType");
 dynamicView.addMemberEntity("PPTR", "ProductPriceTypeRule");
 dynamicView.addAlias("RT", "roleTypeId");
 dynamicView.addAlias("RT", "parentTypeId");
 dynamicView.addAlias("RT", "description");
 dynamicView.addAlias("PPTR", "productPriceTypeId");
 dynamicView.addAlias("PPTR", "isVisble");
 
 dynamicView.addViewLink("RT", "PPTR", Boolean.TRUE, ModelKeyMap.makeKeyMapList("roleTypeId"));

 fieldsToSelect.add("roleTypeId"); 
 fieldsToSelect.add("parentTypeId");
 fieldsToSelect.add("description"); 
 fieldsToSelect.add("productPriceTypeId"); 
 fieldsToSelect.add("isVisble");
 
 EntityCondition oCond = EntityCondition.makeCondition(
 		EntityCondition.makeCondition("roleTypeId", 
		EntityOperator.EQUALS, "EMPLOYEE"), 
		EntityOperator.OR, 
		EntityCondition.makeCondition("parentTypeId", 
		EntityOperator.EQUALS, "EMPLOYEE"));
 andExprs.add(oCond);	
 EntityCondition cCond = EntityCondition.makeCondition("productPriceTypeId", 
		EntityOperator.EQUALS, productPriceTypeId);
 //EntityCondition.makeConditionWhere(" PPTR.PRODUCT_PRICE_TYPE_ID = '" + productPriceTypeId +"'");
 andExprs.add(cCond);
 mainCond =  EntityCondition.makeCondition(andExprs, EntityOperator.AND);;
 
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


