package org.ofbiz.common;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transaction;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.DispatchContext;
public class QueryCommonWorker {
	public final static  Map<String,String> map = FastMap.newInstance();
	static {
			map.put("A","warehouseReplenishmentNo");//补货单
			map.put("B","backOrderNo");//退订单
			map.put("C","collectionOrderNo");//收款单
			map.put("D","deliveryNo");//调拨发货单
			map.put("E","warehouseInNo");//收货单
			map.put("F","refundOrderNo");//退款单
			map.put("G","warehouseOutNo");//发货单
			map.put("H","inventoryNo");//盘点单
			map.put("I","returnInWhsOrderNo");//退货入库单
			map.put("J","receiveNo");//调拨收货单
			map.put("L","preCollectionOrderNo");//预收单
			map.put("M","inventorynItemNo");//库存单
			map.put("N","paymentInOrderNo");//缴款单
			map.put("O","salesOutWhsOrderNo");//销售出库单
			map.put("P","preOrderNo");//预订单
			map.put("R","returnOrderNo");//退货单
			map.put("S","salesOrderNo");//销售订单
			map.put("T","inventorynTransferNo");//库存转移
			map.put("U","preRefundOrderNo");//订金返还单
			map.put("V","partyNo");//会员ID
			map.put("Y","warehouseTransferNo");//移库单
	}
	public static BasePosObject getOrderId(DispatchContext dctx,Map<String,Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		GenericValue docNo = null;
		List<GenericValue> posStroeInfos= null;
		SimpleDateFormat sdf = null;
		String storeId = null;
		//主键为当前日期,每日一条数据,以当前日期查询，如果有值，更新;否，创建一条新数据
		try {
			Transaction front  = TransactionUtil.suspend();
			boolean result = TransactionUtil.begin();
			String type = (String)view.get("type");
        	String strServerType = System.getProperty("posstore.type");
        	if (strServerType.equals("0")){
        		storeId = "99999999";//总店设置门店ID
        	}else{
        		posStroeInfos = delegator.findList("PosStoreInfo", null, null, null, null, false);
        		storeId = (String)posStroeInfos.get(0).get("storeId");//本机为门店服务器
        	}
			if(map.containsKey(type)){
				String docNoId = map.get(type);
				Date date = new Date();
			    sdf = new SimpleDateFormat("yyMMdd");
				String timeNow = sdf.format(date);
				docNo = delegator.findByPrimaryKey("DocNo",UtilMisc.toMap("id", new java.sql.Date(date.getTime())));
				if(UtilValidate.isEmpty(docNo)){  
					try {
						sdf = new SimpleDateFormat("yyyy-MM-dd"); 
						Date dateDefault = sdf.parse("2014-10-01");//初始值
						docNo = delegator.findByPrimaryKey("DocNo",UtilMisc.toMap("id",new java.sql.Date(dateDefault.getTime())));
						docNo.set("id", new java.sql.Date(date.getTime()));
						docNo.setNonPKFields(docNo);
						docNo.create();
					 } catch (ParseException e) {
						e.printStackTrace();
					}                
				}
				if(UtilValidate.isEmpty((Long)docNo.get(docNoId))){
					docNo.set(docNoId,Long.parseLong("1"));
				}
				//返回No,数据库中该类型No+1保存，更新LastUpdateDate
				String noIdStr =  String.format("%08d", (Long)docNo.get(docNoId));
				String noStr = type+timeNow+storeId+noIdStr;
				docNo.set(docNoId,(Long)docNo.get(docNoId)+1);
				docNo.set("lastUpdateDocDate",new Timestamp(date.getTime()));
				docNo.store();
				pObject.setData(noStr);
				TransactionUtil.commit(result);
				TransactionUtil.resume(front);
			}
			pObject.setFlag(Constant.OK);
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
}
