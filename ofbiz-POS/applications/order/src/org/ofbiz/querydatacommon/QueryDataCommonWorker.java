package org.ofbiz.querydatacommon;
import java.util.Map;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.common.QueryCommonWorker;
import org.ofbiz.service.DispatchContext;

public class QueryDataCommonWorker {
	/**
	 * @param dctx
	 * @param Map
	 * @return
	 */
	public static BasePosObject getOrderId(DispatchContext dctx,Map<String,Object> view) {
		BasePosObject pObject = new BasePosObject();
		try {
			pObject = QueryCommonWorker.getOrderId(dctx, view);
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
}
