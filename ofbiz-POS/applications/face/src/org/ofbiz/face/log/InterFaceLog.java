package org.ofbiz.face.log;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;

public class InterFaceLog {
	public static void saveLog(ErpFaceLogModel model, Delegator delegator) {
		
		String logId = delegator.getNextSeqId("ErpFaceLog");
		List<EntityCondition> andExprs = FastList.newInstance();
		andExprs.add(EntityCondition.makeCondition("model",
				EntityOperator.EQUALS, model.getModel()));
		andExprs.add(EntityCondition.makeCondition("event",
				EntityOperator.EQUALS, model.getEvent()));
		andExprs.add(EntityCondition.makeCondition("recordId",
				EntityOperator.EQUALS, model.getRecordId()));
		andExprs.add(EntityCondition.makeCondition("state",
				EntityOperator.EQUALS, "Y"));
		andExprs.add(EntityCondition.makeCondition("result",
				EntityOperator.EQUALS, "N"));
		EntityCondition mainCond = EntityCondition.makeCondition(andExprs,
				EntityOperator.AND);
		try {
			TransactionUtil.begin();
			delegator.storeByCondition("ErpFaceLog",
					UtilMisc.toMap("state", "N"), mainCond);

			GenericValue faceLog = null;

			Map<String, String> newMap = UtilMisc.toMap("logId", logId,
					"model", model.getModel(), "event", model.getEvent(),
					"result", model.getResult(), "state", model.getState(),
					"resultInfo", model.getResultInfo().length() > 1000
							? model.getResultInfo().substring(0, 800) : model.getResultInfo(), 
					"recordId", model.getRecordId());
			faceLog = delegator.makeValue("ErpFaceLog", newMap);
			faceLog.create();
			TransactionUtil.commit();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static List<String> getFailData(ErpFaceLogModel model, Delegator delegator) {
		List<EntityCondition> andExprs = FastList.newInstance();
		List<String> idList = FastList.newInstance();
		andExprs.add(EntityCondition.makeCondition("model",
				EntityOperator.EQUALS, model.getModel()));
		andExprs.add(EntityCondition.makeCondition("event",
				EntityOperator.EQUALS, model.getEvent()));		
		andExprs.add(EntityCondition.makeCondition("result",
				EntityOperator.EQUALS, "N"));
		andExprs.add(EntityCondition.makeCondition("state",
				EntityOperator.EQUALS, "Y"));
		
		EntityCondition mainCond = EntityCondition.makeCondition(andExprs,
				EntityOperator.AND);
		Set<String> feilds = new HashSet<String>();
		feilds.add("recordId");
		try {
			List<GenericValue> result = delegator.findList("ErpFaceLog", mainCond, feilds, null, null, true);
			for(GenericValue gv : result){
				if (UtilValidate.isNotEmpty(gv)) {
					if (UtilValidate.isNotEmpty(gv.get("recordId"))) {
						idList.add(gv.get("recordId").toString());
					}
				}
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return idList;
	}
}

