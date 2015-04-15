package org.ofbiz.product.productinfo;

import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;

public class ProductInfoWork {
	public static BasePosObject productInfo(DispatchContext dctx,Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			List<EntityCondition> condList = FastList.newInstance();
			if (Constant.isNotNull(view.get("productId"))) {
				condList.add(EntityCondition.makeCondition("productId",EntityOperator.LIKE,("%" + view.get("productId") + "%")));
			}
			if (Constant.isNotNull(view.get("idValue"))) {
				condList.add(EntityCondition.makeCondition("idValue",EntityOperator.LIKE,("%" + view.get("idValue") + "%")));
			}
			if (Constant.isNotNull(view.get("productName"))) {
				condList.add(EntityCondition.makeCondition("productName",EntityOperator.LIKE,("%" + view.get("productName") + "%")));
			}
			EntityCondition condition = EntityCondition.makeCondition(condList);

			List<GenericValue> list = delegator.findList("ProductInfoView",condition, null, null, null, true);
			pObject.setFlag(Constant.OK);
			if (list != null && list.size() > 0) {
				pObject.setData(list);
			}
		} catch (Exception e) {
			e.printStackTrace();
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}

	public static BasePosObject productSequenceInfo(DispatchContext dctx,Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<EntityCondition> condList = FastList.newInstance();
		try {
			if (Constant.isNotNull(view.get("sequenceId"))) {
				condList.add(EntityCondition.makeCondition("sequenceId",EntityOperator.LIKE,("%" + view.get("sequenceId") + "")));
			}
			if (Constant.isNotNull(view.get("productStoreId"))) {
				condList.add(EntityCondition.makeCondition("productStoreId",EntityOperator.EQUALS, view.get("productStoreId")));
			}
			if (Constant.isNotNull(view.get("facilityId"))) {
				condList.add(EntityCondition.makeCondition("facilityId",EntityOperator.NOT_EQUAL, null));
			}
			EntityCondition condition = EntityCondition.makeCondition(condList);

			List<GenericValue> list = delegator.findList("ProductSequenceInfoView", condition, null, null, null,true);
			pObject.setFlag(Constant.OK);
			if (list != null && list.size() > 0) {
				pObject.setData(list);
			}
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
}
