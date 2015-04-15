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
package org.ofbiz.product.store;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

/**
 * ProductStoreWorker - Worker class for store related functionality
 */
public class ProductStoreAttribute {

	/**
     * create createProductStoreAttribute
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> createProductStoreAttribute(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String productStoreId = (String)context.get("productStoreId");
    	String partyId = (String)context.get("partyId");
    	String description = (String)context.get("description");
    	String attrValue = (String)context.get("attrValue");
    	String productStoreAttrTypeId = "";
    	
    	List<GenericValue> gvs = null;
    	try {
    		gvs = delegator.findByAnd("ProductStoreAttributeType", UtilMisc.toMap("description", description));
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (UtilValidate.isNotEmpty(gvs)) {
			productStoreAttrTypeId = (String)gvs.get(0).get("productStoreAttrTypeId");
		}else {
			productStoreAttrTypeId = delegator.getNextSeqId("ProductStoreAttributeType");
			
			GenericValue productStoreAttributeType = null;
			
			Map<String, String> newAttrTypeMap = UtilMisc.toMap("productStoreAttrTypeId", productStoreAttrTypeId, 
					"partyId", partyId, "description", description);
			productStoreAttributeType = delegator.makeValue("ProductStoreAttributeType", newAttrTypeMap);
			
			try {
				productStoreAttributeType.create();
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	
		GenericValue productStoreAttribute = null;
        
        Map<String, String> newAttrMap = UtilMisc.toMap("productStoreId", productStoreId, 
        											"productStoreAttrTypeId", productStoreAttrTypeId, 
        											"attrValue", attrValue);
        productStoreAttribute = delegator.makeValue("ProductStoreAttribute", newAttrMap);
        
        try {
        	productStoreAttribute.create();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess();
    }
    
    /**
     * delete ProductStoreAttribute
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> deleteProductStoreAttribute(DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	String productStoreId = (String)context.get("productStoreId");
    	String productStoreAttrTypeId = (String)context.get("productStoreAttrTypeId");
    	
    	GenericValue gv = null;
    	List<GenericValue> lists = null;
    	try {
			gv = delegator.findByPrimaryKey("ProductStoreAttributeType", UtilMisc.toMap("productStoreAttrTypeId", productStoreAttrTypeId));
			lists = delegator.findByAnd("ProductStoreAttribute", UtilMisc.toMap("productStoreId", productStoreId, "productStoreAttrTypeId", productStoreAttrTypeId));
			gv.remove();
    		delegator.removeAll(lists);
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	return ServiceUtil.returnSuccess();
    }
}
