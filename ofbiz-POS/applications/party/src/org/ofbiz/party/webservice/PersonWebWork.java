package org.ofbiz.party.webservice;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

public class PersonWebWork {

	public static BasePosObject findPersonByProductStoreId(Map<String, Object> mapIn, DispatchContext dctx) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			List<GenericValue> list = delegator.findByAnd("PersonAndProductStore", UtilMisc.toMap("productStoreId", mapIn.get("productStoreId")));
			pObject.setFlag(Constant.OK);
			if(list!=null && list.size()>0){
				pObject.setData(list);
			}
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}

}
