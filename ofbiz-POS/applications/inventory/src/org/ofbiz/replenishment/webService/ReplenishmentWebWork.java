package org.ofbiz.replenishment.webService;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastSet;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.util.ConvertUtil;

public class ReplenishmentWebWork {
	/*
	 * 查询补货单头
	 */
	public static BasePosObject findReplenishment(Map<String,Object> mapIn,DispatchContext dctx) {
		Delegator delegator = dctx.getDelegator();
		// 方法参数
		EntityCondition whereEntityCondition = null;//条件
		Set<String> fieldsToSelect = FastSet.newInstance();// 要查询的列
		List<EntityCondition> andExprs = FastList.newInstance();// 添加条件值
		List<String> orderBy = FastList.newInstance();
		orderBy.add("updateDate DESC");
		//5个条件
		if (Constant.isNotNull(mapIn.get("docId"))) {
			andExprs.add(EntityCondition.makeCondition("docId", EntityOperator.LIKE, "%"+(String)mapIn.get("docId")+"%"));
		}
		if (Constant.isNotNull(mapIn.get("docStatus"))) {
			andExprs.add(EntityCondition.makeCondition("docStatus", EntityOperator.EQUALS, (String)mapIn.get("docStatus")));
		}
		if (Constant.isNotNull(mapIn.get("productStoreId"))) {
			andExprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, (String)mapIn.get("productStoreId")));
		}
		if (Constant.isNotNull(mapIn.get("updateUserId"))) {
			andExprs.add(EntityCondition.makeCondition("updateUserId", EntityOperator.EQUALS, (String)mapIn.get("updateUserId")));
		}
		List<Object> listDate = FastList.newInstance();
		if(Constant.isNotNull(mapIn.get("startDate"))){
			listDate.add(ConvertUtil.convertStringToTimeStamp(mapIn.get("startDate")+""));
		}
		if (Constant.isNotNull(mapIn.get("endDate"))) {
			listDate.add(ConvertUtil.convertStringToTimeStamp(mapIn.get("endDate")+""));
		}
		if(listDate.size()>0){
			andExprs.add(EntityCondition.makeCondition("docDate", EntityOperator.BETWEEN, listDate));
		}
		if (andExprs.size() > 0){
        	//mainCond = EntityCondition.makeConditionWhere(jsonStr);
			whereEntityCondition = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
        }
		BasePosObject pObject  = new BasePosObject();
		try {
			List<GenericValue> list = delegator.findList("ReplenishmentAndPerson",whereEntityCondition, fieldsToSelect, orderBy, null, false );
			pObject.setFlag("S");
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
	/*
	 * 查询明细
	 */
	public static BasePosObject findReplenishmentItem(Map<String,Object> mapIn,DispatchContext dctx) {
		Delegator delegator = dctx.getDelegator();
		// 方法参数
		EntityCondition whereEntityCondition = null;//条件
		Set<String> fieldsToSelect = FastSet.newInstance();// 要查询的列
		List<EntityCondition> andExprs = FastList.newInstance();// 添加条件值
		andExprs.add(EntityCondition.makeCondition("docId", EntityOperator.EQUALS, (String)mapIn.get("docId")));
		if (andExprs.size() > 0){
			whereEntityCondition = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
        }
		List<String> orderBy = FastList.newInstance();
		orderBy.add("lineNo");
		BasePosObject pObject  = new BasePosObject();
		try {
			List<GenericValue> list = delegator.findList("ReplenishmentItemAndProduct",whereEntityCondition, fieldsToSelect, orderBy, null, false );
			pObject.setFlag("S");
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
	/*
	 * 添加补货单
	 */
	public static BasePosObject saveReplenishment(Map<String,Object> mapHeader,List<Map<String,Object>>listItem,DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject= new BasePosObject();
		try {
			//添加头数据
			if(mapHeader.get("postingDate")!=null){
				mapHeader.put("postingDate", ConvertUtil.convertStringToTimeStamp(mapHeader.get("postingDate").toString()));
			}
			if(mapHeader.get("saleDay")!=null){
				mapHeader.put("saleDay", Long.parseLong(String.valueOf(mapHeader.get("saleDay"))));
			}
			if(mapHeader.get("receiveDate")!=null){
				mapHeader.put("receiveDate", ConvertUtil.convertStringToTimeStamp(mapHeader.get("receiveDate").toString()));
			}
			//获取系统时间
			mapHeader.put("docDate",UtilDateTime.nowTimestamp() );
			mapHeader.put("updateDate", UtilDateTime.nowTimestamp());
			GenericValue replenishment = delegator.makeValidValue("Replenishment", mapHeader);
			GenericValue replenishment1 = delegator.findByPrimaryKey("Replenishment", UtilMisc.toMap("docId", mapHeader.get("docId")));
			if(!replenishment.get("docStatus").equals(Constant.DRAFT)){
				replenishment.set("isSync", Constant.ONE);
			}else{
				replenishment.set("isSync", "0");
			}
			if(replenishment1==null){
				replenishment.create();
			}
			//添加明细
			for (int i = 0; i < listItem.size(); i++) {
				Map<String,Object> itemMap = listItem.get(i);
				Object availableQuantity=null;	//可用库存
				Object receiveQuantity=null;	//待收货数
				Object saftyQuantity=null;		//安全库存
				Object preQuantity=null;		//预订数
				Object quantity=null;			//补货数
				Object promise=null;			//承诺数
				Object onhand=null;				//库存数
				Object baseLineNo=null;			//指令行
				Object lineNo=null;				//行
				//可用库存
				if(itemMap.get("availableQuantity")!=null){
					availableQuantity = Long.parseLong(String.valueOf(itemMap.get("availableQuantity")));
				}
				//待收货数
				if(itemMap.get("receiveQuantity")!=null){
					receiveQuantity = Long.parseLong(String.valueOf(itemMap.get("receiveQuantity")));
				}
				//安全库存
				if(itemMap.get("saftyQuantity")!=null){
					saftyQuantity=Long.parseLong(String.valueOf(itemMap.get("saftyQuantity")));
				}
				//预订数
				if(itemMap.get("preQuantity")!=null){
					preQuantity=Long.parseLong(String.valueOf(itemMap.get("preQuantity")));
				}
				//补货数
				if(itemMap.get("quantity")!=null){
					quantity=Long.parseLong(String.valueOf(itemMap.get("quantity")));
				}
				//承诺数
				if(itemMap.get("promise")!=null){
					promise=Long.parseLong(String.valueOf(itemMap.get("promise")));
				}
				//库存数
				if(itemMap.get("onhand")!=null){
					onhand=Long.parseLong(String.valueOf(itemMap.get("onhand")));
				}
				//指令行
				if(itemMap.get("baseLineNo")!=null){
					baseLineNo=Long.parseLong(String.valueOf(itemMap.get("baseLineNo")));
				}
				//行
				if(itemMap.get("lineNo")!=null){
					lineNo=Long.parseLong(String.valueOf(itemMap.get("lineNo")));
				}
				
				itemMap.put("availableQuantity", availableQuantity);//可用库存
				itemMap.put("receiveQuantity", receiveQuantity);	//待收货数
				itemMap.put("saftyQuantity", saftyQuantity);		//安全库存
				itemMap.put("preQuantity", preQuantity);			//预订数
				itemMap.put("quantity", quantity);					//补货数
				itemMap.put("promise", promise);					//承诺数
				itemMap.put("onhand", onhand);						//库存数
				itemMap.put("baseLineNo", baseLineNo);				//指令行
				itemMap.put("lineNo", lineNo);						//行
				GenericValue replenishmentItem = delegator.makeValidValue("ReplenishmentItem", itemMap);
				replenishmentItem.create();
			}
			pObject.setFlag(Constant.OK);
			pObject.setData("保存成功");
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	/*
	 * 补货申请
	 */
	public static BasePosObject saveReplenishmentBefor(Map<String, Object> mapIn, DispatchContext dctx) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject= new BasePosObject();
		try {
			// 方法参数
//			Set<String> fieldsToSelect = FastSet.newInstance();// 要查询的列
//			List<EntityCondition> andExprs = FastList.newInstance();// 添加条件值 and
////			List<EntityCondition> andExprs1 = FastList.newInstance();// 添加条件值 or
//			EntityCondition whereEntityCondition = null;//条件
////			EntityCondition whereEntityCondition1 = null;//条件
//			if (Constant.isNotNull(mapIn.get("productStoreId"))) {
//				andExprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, (String)mapIn.get("productStoreId")));
//			}
//			//仓库类型
//			if(Constant.isNotNull(mapIn.get("facilityTypeId"))){
//				andExprs.add(EntityCondition.makeConditionWhere("FA.FACILITY_TYPE_ID = '"+mapIn.get("facilityTypeId")+"'"));
//			}else{
//				pObject.setFlag(Constant.NG);
//				pObject.setMsg("未选择仓库类型");
//				return pObject;
//			}
//			DynamicViewEntity modelViewEvtity =new DynamicViewEntity();//视图
//			ModelReader model = ModelReader.getModelReader("ReplenishmentAndOther");
//			model.set
//			if(Constant.isNotNull(mapIn.get("saleDay"))){
//				Timestamp time = UtilDateTime.nowTimestamp();
//				Timestamp startTime =UtilDateTime.addDaysToTimestamp(time, -Integer.parseInt(mapIn.get("saleDay").toString()));
//				Timestamp endTime =UtilDateTime.addDaysToTimestamp(time, -1);
//				ModelEntity
//				model.setVersion("SOH.LAST_UPDATE_DOC_DATE >= '"+startTime+"' AND SOH.LAST_UPDATE_DOC_DATE < '"+endTime+"'");
//			}
//			modelViewEvtity.addMemberEntity("SC", model.toString());
//			whereEntityCondition = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
//			//查询冲红的 ReturnOrderHeader movementType = 'TC'
//			List<GenericValue> returnOrderHeaderList = delegator.findByAnd("ReturnOrderHeader", UtilMisc.toMap("movementType", Constant.MOVEMENTYPE));
//			List<Object> idList = new ArrayList<Object>();
//			for (int i = 0; i < returnOrderHeaderList.size(); i++) {
//				idList.add(returnOrderHeaderList.get(i).get("docId"));
//			}
//			//去除冲红退货
//			andExprs1.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("docId"),EntityOperator.NOT_IN,idList));
//			//销售状态---确定，批准，已清
//			List<String> docStatusList = new ArrayList<String>();
//			docStatusList.add(Constant.VALID);
//			docStatusList.add(Constant.APPROVED);
//			docStatusList.add(Constant.CLEARED);
//			String starts =Constant.VALID+"','"+Constant.APPROVED+"','"+Constant.CLEARED;
//			andExprs1.add(EntityCondition.makeConditionWhere("SC.SOH_DOC_STATUS IN ('"+starts+"')"));
//			
//			//本店销售出库
//			andExprs1.add(EntityCondition.makeConditionWhere("SC.SOH_DELIVERY_STORE_ID = SC.SOH_STORE_ID"));
//			
//			andExprs1.add(whereEntityCondition1);
//			andExprs1.add(EntityCondition.makeConditionWhere("OU.RC_DOC_STATUS IN ('"+starts+"')"));
//			
//			whereEntityCondition = EntityCondition.makeCondition(andExprs1, EntityOperator.OR);
			StringBuffer str = new StringBuffer();
			String sql= "SELECT II.PRODUCT_ID AS productId, PR.PRODUCT_NAME AS productName,SUM(II.ONHAND) AS onhand,SUM(II.PROMISE) AS promise,sum(SC.SOD_WAREHOUSE_QUANTITY) AS saftyQuantity,sum(OU.OUTSTANDING) AS receiveQuantity,sum(SC.POD_QUANTITY) AS preQuantity,(SUM(SC.SOD_WAREHOUSE_QUANTITY) - SUM(II.ONHAND)- SUM(OU.OUTSTANDING)) + SUM(SC.POD_QUANTITY)+ SUM(II.PROMISE) AS QUANTITY FROM INVENTORYN_ITEM II LEFT OUTER JOIN PRODUCT PR ON II.PRODUCT_ID = PR.PRODUCT_ID LEFT OUTER JOIN (SELECT SUM(POD.QUANTITY) AS POD_QUANTITY, SUM(SOD.WAREHOUSE_QUANTITY) AS SOD_WAREHOUSE_QUANTITY, SOD.PRODUCT_ID AS SOD_PRODUCT_ID FROM SALE_ORDER_DTL SOD LEFT OUTER JOIN SALE_ORDER_HEADER SOH ON SOD.DOC_ID = SOH.DOC_ID LEFT OUTER JOIN PRE_ORDER_HEADER POH ON SOH.DOC_ID = POH.BASE_ENTRY LEFT OUTER JOIN PRE_ORDER_DTL POD ON SOD.LINE_NO_BASE_ENTRY = POD.LINE_NO_BASE_ENTRY WHERE (SOH.DOC_STATUS IN ('1', '2', '3')";
			//去除冲红
			sql+=" AND SOH.DOC_ID NOT IN (SELECT ROH.BASE_ENTRY FROM return_order_header ROH WHERE MOVEMENT_TYPE_ID='"+Constant.MOVEMENTYPE+"')";
			if (Constant.isNotNull(mapIn.get("productStoreId"))) {
				sql+=" AND SOH.STORE_ID='"+mapIn.get("productStoreId")+"'";
			}
			if(Constant.isNotNull(mapIn.get("saleDay"))){
				Timestamp time = UtilDateTime.nowTimestamp();
				Calendar  newDate = Calendar.getInstance();
				time.setHours(0);
				time.setMinutes(0);
				time.setSeconds(0);
				newDate.setTime(time);
				newDate.add(Calendar.DAY_OF_YEAR, -Integer.parseInt(mapIn.get("saleDay")+""));
				String dateStr=ConvertUtil.convertDateToString(newDate.getTime());
				Timestamp startTime =ConvertUtil.convertStringToTimeStamp(dateStr);
				sql+=" AND SOH.LAST_UPDATE_DOC_DATE >= '"+startTime+"' AND SOH.LAST_UPDATE_DOC_DATE < '"+time+"'";
				if (Constant.isNotNull(mapIn.get("productStoreId"))) {
					sql+=" AND SOH.DELIVERY_STORE_ID ='"+mapIn.get("productStoreId")+"'";
				}
			}
			sql+=") GROUP BY SOD.PRODUCT_ID )SC ON II.PRODUCT_ID = SC.SOD_PRODUCT_ID LEFT OUTER JOIN(SELECT RI.PRODUCT_ID AS RI_PRODUCT_ID,(SUM(COALESCE(RI.QUANTITY,0)) - SUM(COALESCE(RI.RECEIVE_QUANTITY,0))) AS OUTSTANDING FROM RECEIVE_ITEMCOMMAND RI LEFT OUTER JOIN RECEIVE_COMMAND RC ON RI.DOC_ID = RC.DOC_ID WHERE RC.DOC_STATUS IN ('1', '2', '3')  ";
			if (Constant.isNotNull(mapIn.get("productStoreId"))) {
				sql+=" AND RC.PRODUCT_STORE_ID='"+mapIn.get("productStoreId")+"'";
			}
			sql+=" GROUP BY RI.PRODUCT_ID) OU ON II.PRODUCT_ID = OU.RI_PRODUCT_ID LEFT OUTER JOIN FACILITY FA ON II.FACILITY_ID = FA.FACILITY_ID";
			//仓库类型
			if(Constant.isNotNull(mapIn.get("facilityTypeId"))){
				sql+=" WHERE FA.FACILITY_TYPE_ID = '"+mapIn.get("facilityTypeId")+"'";
			}else{
				pObject.setFlag(Constant.NG);
				pObject.setMsg("未选择仓库类型");
				return pObject;
			}
			if (Constant.isNotNull(mapIn.get("productStoreId"))) {
				sql+=" AND II.PRODUCT_STORE_ID = '"+mapIn.get("productStoreId")+"'";
			}
			
			sql+=" AND PR.REQUIRE_INVENTORY='"+Constant.ISSEQ+"' GROUP BY II.PRODUCT_ID,PR.PRODUCT_NAME";
			String groupHelperName = delegator.getGroupHelperName("org.ofbiz");
			Connection conn = ConnectionFactory.getConnection(groupHelperName);  
			System.out.println(sql);
			Statement stmt =  conn.createStatement(); 
			ResultSet rs = stmt.executeQuery(sql);
//			System.out.println(sql);
			ResultSetMetaData md = rs.getMetaData(); 
		    int columnCount = md.getColumnCount();
		    List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		    while (rs.next()) {
		    	Map<String,Object> map = new HashMap<String,Object>(); 
		    	for (int i = 1; i <= columnCount; i++) {
		    		map.put(md.getColumnLabel(i), rs.getObject(i));
		    		if(map.get("QUANTITY")!= null && Long.parseLong(String.valueOf(map.get("QUANTITY")))<0){
		    			map.put("QUANTITY", Long.parseLong(String.valueOf(0)));
		    		}
		    		
		    	}
		    	list.add(map);
		    }
		    
//			List<GenericValue> list = delegator.findList("ReplenishmentAndOther",whereEntityCondition, fieldsToSelect, null, null, false );
			pObject.setFlag(Constant.OK);
			if(list!=null && list.size()>0){
//				for (GenericValue genericValue : list) {
//					//需配数  安全库存-当前库存-待收货数+预定数+承诺数
//					if (genericValue.getLong("quantity")<0) {
//						genericValue.set("quantity", Long.parseLong(String.valueOf(0)));
//					}
//				}
				pObject.setData(list);
			}
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	
	
	public static BasePosObject saveReplenishmentBefor1(
			Map<String, Object> mapIn, DispatchContext dctx) {

		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		EntityCondition mainCond = null;
		List<GenericValue> InventorynSum = null;
		List<GenericValue> SaleSum = null;
		List<GenericValue> PreSum = null;
		List<GenericValue> reSum = null;
		try {
			String storeId = mapIn.get("productStoreId").toString();
			String ftype = "";
			if (Constant.isNotNull(mapIn.get("facilityTypeId"))) {
				ftype = mapIn.get("facilityTypeId").toString();
			}

			Timestamp time = UtilDateTime.nowTimestamp();
			Calendar newDate = Calendar.getInstance();
			time.setHours(23);
			time.setMinutes(59);
			time.setSeconds(59);
			newDate.setTime(time);
			newDate.add(Calendar.DAY_OF_YEAR, -Integer.parseInt(mapIn.get("saleDay") + ""));
			String dateStr = ConvertUtil.convertDateToString(newDate.getTime());
			Timestamp startTime = ConvertUtil.convertStringToTimeStamp(dateStr);

			String whereStr = "INI.product_Store_Id = '" + storeId + "'";
			if (!ftype.equals("")) {
				whereStr += " AND FA.facility_Type_Id = '" + ftype + "'";
			}
			mainCond = EntityCondition.makeConditionWhere(whereStr);

			InventorynSum = delegator.findList("InventorynItemSum", mainCond,null, null, null, true);

			whereStr = "SOD.SOH_DELIVERY_STORE_ID = '" + storeId + "'";
			whereStr += " AND SOD.SOH_LAST_UPDATE_DOC_DATE > '" + startTime+ "' AND SOD.SOH_LAST_UPDATE_DOC_DATE < '" + time + "'";
			mainCond = EntityCondition.makeConditionWhere(whereStr);
			SaleSum = delegator.findList("SaleOrderDtlSum", mainCond, null,null, null, true);

			whereStr = "POH.store_Id = '" + storeId + "'";
			mainCond = EntityCondition.makeConditionWhere(whereStr);
			PreSum = delegator.findList("PreOrderDtlSum", mainCond, null, null,
					null, true);
			whereStr = "POH.product_Store_Id = '" + storeId + "'";
			if (!ftype.equals("")) {
				whereStr += " AND FA.facility_Type_Id = '" + ftype + "'";
			}
			mainCond = EntityCondition.makeConditionWhere(whereStr);
			reSum = delegator.findList("ReceiveItemcommandSum", mainCond, null,null, null, true);

			Map<String, ReplenishmentModel> result = new HashMap<String, ReplenishmentModel>();
			for (GenericValue gv : SaleSum) {
				ReplenishmentModel model = new ReplenishmentModel();
				model.setProductId(gv.get("productId").toString());
				model.setProductName(gv.get("productName").toString());
				model.setSaftyQuantity(Long.parseLong(gv.get("warehouseQuantity").toString()));
				result.put(model.getProductId(), model);
			}

			for (GenericValue gv : reSum) {
				ReplenishmentModel model = null;
				if (result.get(gv.get("productId").toString()) == null) {
					model = new ReplenishmentModel();
					model.setProductId(gv.get("productId").toString());
					model.setProductName(gv.get("productName").toString());
					model.setReceiveQuantity(Long.parseLong(gv.get("quantity").toString()));
					result.put(model.getProductId(), model);
					// result.
				} else {
					model = result.get(gv.get("productId").toString());
					model.setReceiveQuantity(Long.parseLong(gv.get("quantity").toString()));
				}
			}

			for (GenericValue gv : PreSum) {
				ReplenishmentModel model = null;
				if (result.get(gv.get("productId").toString()) == null) {
					model = new ReplenishmentModel();
					model.setProductId(gv.get("productId").toString());
					model.setProductName(gv.get("productName").toString());
					model.setPreQuantity(Long.parseLong(gv.get("quantity").toString()));
					result.put(model.getProductId(), model);
					// result.
				} else {
					model = result.get(gv.get("productId").toString());
					model.setPreQuantity(Long.parseLong(gv.get("quantity").toString()));
				}
			}
			long quantity = 0;
			for (GenericValue gv : InventorynSum) {
				ReplenishmentModel model = null;
				if (result.get(gv.get("productId").toString()) == null) {
					model = new ReplenishmentModel();
					model.setProductId(gv.get("productId").toString());
					model.setProductName(gv.get("productName").toString());
					model.setOnhand(Long.parseLong(gv.get("onhand").toString()));
					model.setPromise(Long.parseLong(gv.get("promise").toString()));
					quantity = model.getSaftyQuantity() - model.getOnhand()
							- model.getReceiveQuantity()
							- model.getPreQuantity() + model.getPromise();
					if (quantity < 0)
						quantity = 0;
					model.setQuantity(quantity);
					result.put(model.getProductId(), model);
					// result.
				} else {
					model = result.get(gv.get("productId").toString());
					
					model.setOnhand(Long.parseLong(gv.get("onhand").toString()));
					model.setPromise(Long.parseLong(gv.get("promise")
							.toString()));
					quantity = model.getSaftyQuantity() - model.getOnhand()
							- model.getReceiveQuantity()
							- model.getPreQuantity() + model.getPromise();
					if (quantity < 0)
						quantity = 0;
					model.setQuantity(quantity);
				}

			}
			List<ReplenishmentModel> resultlist = FastList.newInstance();
			Iterator<String> it = result.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next().toString();

				resultlist.add(result.get(key));
			}

			pObject.setFlag(Constant.OK);
			if (resultlist != null && resultlist.size() > 0) {
				// for (GenericValue genericValue : list) {
				// //需配数 安全库存-当前库存-待收货数+预定数+承诺数
				// if (genericValue.getLong("quantity")<0) {
				// genericValue.set("quantity",
				// Long.parseLong(String.valueOf(0)));
				// }
				// }
				pObject.setData(resultlist);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}

}


