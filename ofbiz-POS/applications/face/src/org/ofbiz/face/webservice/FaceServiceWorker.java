package org.ofbiz.face.webservice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastSet;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityListIterator;

public class FaceServiceWorker {
	
	public static List<GenericValue> getProductList() {
		GenericDelegator delegator = GenericDelegator.getGenericDelegator("default");
		Set<String> fieldsToSelect = FastSet.newInstance();
		fieldsToSelect.add("productId");
		fieldsToSelect.add("productName");
		
		List<GenericValue>  products = null;
		EntityListIterator listI = null;
		try {
			listI = delegator.find("Product", null, null, fieldsToSelect, null, null);
			products = listI.getCompleteList();
			listI.close();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return products;
	}
	public static List<GenericValue> getProductFeatureApplList() {
		GenericDelegator delegator = GenericDelegator.getGenericDelegator("default");
		
		List<GenericValue>  lists = null;
		EntityListIterator listI = null;
		try {
			listI = delegator.find("ProductFeatureAppl", null, null, null, null, null);
			lists = listI.getCompleteList();
			listI.close();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lists;
	}
	public static List<GenericValue> getProductCategoryMemberList() {
		GenericDelegator delegator = GenericDelegator.getGenericDelegator("default");
		
		List<GenericValue>  lists = null;
		EntityListIterator listI = null;
		try {
			listI = delegator.find("ProductCategoryMember", null, null, null, null, null);
			lists = listI.getCompleteList();
			listI.close();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return lists;
	}

	/**
	 * @see EBS上行接口通用实体取值函数
	 * @param entityName 实体（视图）名称
	 * @param Condition 取值条件
	 * @param orderBy 排序
	 * @return 实体内容
	 */
	public static List<GenericValue> getEntities(String entityName, String whereCondition, List<String> orderBy) {
		GenericDelegator delegator = GenericDelegator.getGenericDelegator("default");
		EntityCondition whereEntityCondition = null;
		List<GenericValue>  Entities = null;
		EntityListIterator listI = null;
		try {
			//TransactionUtil.begin();
			if(UtilValidate.isNotEmpty(whereCondition)){
				whereEntityCondition = EntityCondition.makeConditionWhere(whereCondition);
			}			
			Entities = delegator.findList(entityName, whereEntityCondition, null, orderBy, null, false);
			// listI = delegator.find(entityName, whereEntityCondition, null, null, orderBy, null);
			// Entities = listI.getCompleteList();
			// TransactionUtil.commit();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			/*try {
				TransactionUtil.rollback();
			} catch (GenericTransactionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}*/
		}
		return Entities;
	}
	public static List<GenericValue> changeDocLine(List<GenericValue> DocLine) {
		List<GenericValue> lists =FastList.newInstance(); 
		GenericValue Y = null;
		GenericValue N = null;
		for (int i = 0; i < DocLine.size(); i++) {
			Y = DocLine.get(i);
			System.out.println(Y.get("docId") +":"
					+Y.get("erpPolicyId") +":"
					+ Y.get("isMainProduct"));
			if (Y.get("erpPolicyId") != null && Y.get("erpPolicyId").equals("10") 
					&& Y.get("isMainProduct").equals("Y")) {
				N = DocLine.get(i+1);
				if (N.get("isMainProduct").equals("N")) {
					BigDecimal r = new BigDecimal(Y.get("rebateAmount").toString());
					BigDecimal add = r.add(new BigDecimal(N.get("rebateAmount").toString()));
					Y.set("rebateAmount", add);
					BigDecimal s = new BigDecimal(Y.get("saleAmount").toString());
					BigDecimal subtract = s.subtract(add);
					Y.set("depreciation", subtract);
					lists.add(Y);
					N.set("rebateAmount", new BigDecimal(0));
					N.set("depreciation", new BigDecimal(0));
					lists.add(N);
				}
			}else if(Y.get("erpPolicyId") != null && Y.get("erpPolicyId").equals("10") 
					&& Y.get("isMainProduct").equals("N")) {
			}else {
				lists.add(Y);
			}
		}
		return lists;
	}
	public static List<GenericValue> rebatePrice(List<GenericValue> DocLine) {
		List<GenericValue> lists =FastList.newInstance(); 
		for (GenericValue gv : DocLine) {
			Double rebateAmount = Double.parseDouble(gv.get("rebateAmount").toString());
			Double quantity = Double.parseDouble(gv.get("quantity").toString());
			String str = new BigDecimal(rebateAmount/quantity).toString();
			String _str = "";
			if (str.indexOf(".") != -1) {
				_str = str.substring(str.indexOf("."));
				_str =_str.length()>7?_str.substring(0, 7):_str;
				str = str.substring(0, str.indexOf(".")) + _str;
			}
			gv.set("rebatePrice", new BigDecimal(str));
			lists.add(gv);
		}
		return lists;
	}
}
