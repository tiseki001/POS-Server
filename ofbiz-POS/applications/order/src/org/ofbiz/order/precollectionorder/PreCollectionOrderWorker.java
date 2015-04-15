package org.ofbiz.order.precollectionorder;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.order.parameter.Parameter;
import org.ofbiz.order.preorder.OrderGenericValue;
import org.ofbiz.order.preorder.PreOrderWorker;
import org.ofbiz.service.DispatchContext;
/**
 * Worker methods for PreCollectionOrderWorker Information
 */
public class PreCollectionOrderWorker {
	//创建收订金预收单
	 public static BasePosObject createPreCollectionOrder(DispatchContext dctx ,PreCollectionOrder preCollectionOrder ){
		 BasePosObject pObject = new BasePosObject ();
		 try{
			 //写入header
			getGVPreCollectionOrderHeader(dctx,preCollectionOrder.getHeader());
			int len= preCollectionOrder.getDetail().size();
			//循环写入明细
			 for(int i=0;i<len;i++){
				 getGVPreCollectionOrderDtl(dctx,preCollectionOrder.getDetail().get(i));
			 }
			 //调用接口更新预订单表头的付款金额(PreCollectionAmount)
			 Map<String, Object> view = FastMap.newInstance();
			 String docId = preCollectionOrder.getHeader().getBaseEntry();
		     String lastUpdateUserId = preCollectionOrder.getHeader().getLastUpdateUserId();
		     Timestamp lastUpdateDocDate = preCollectionOrder.getHeader().getLastUpdateDocDate();
		     BigDecimal amount = preCollectionOrder.getHeader().getAmount();
		     view.put("docId",docId);
		     view.put("lastUpdateUserId",lastUpdateUserId);
		     view.put("lastUpdateDocDate",lastUpdateDocDate);
		     view.put("amount",amount);
		     BasePosObject pObjectResult = PreOrderWorker.addPreOrderCollectionAmount(dctx,view);
		     if(pObjectResult.getFlag().equals(Constant.NG)){
		    	 pObject.setFlag(Constant.NG);
				 pObject.setMsg(pObjectResult.getMsg());
				 return pObject;
		     }
		     pObject.setFlag(Constant.OK);
		 }catch (Exception e) {
			 pObject.setFlag(Constant.NG);
			 pObject.setMsg(e.getMessage());
			  e.printStackTrace();
			}
		return pObject;
	 }		

	 //更新订金预收单状态
	 @SuppressWarnings("unused")
	public static BasePosObject updatePreCollectionOrderStatus(DispatchContext dctx,Parameter parameter) {
			Delegator delegator = dctx.getDelegator();
			BasePosObject pObject = new BasePosObject ();
			GenericValue preCollectionOrderHeader = null;
			// 根据主键docId 查询PreCollectionOrderHeader的非空的且不是部分已清的Status状态
			try{
				String docId = parameter.getDocId();
				String docStatus = parameter.getDocStatus();
				String lastupdateuserId = parameter.getLastUpdateUserId();
				Timestamp lastUpdateDocDate =parameter.getLastUpdateDocDate();//待用
			    preCollectionOrderHeader = delegator.findByPrimaryKey("PreCollectionOrderHeader", UtilMisc.toMap("docId", docId));
				if(UtilValidate.isEmpty(preCollectionOrderHeader)){
					pObject.setFlag(Constant.NG);
					pObject.setMsg("根据docId查询结果为空");
					return pObject;
				}
			    String preCollectionOrderDocStatus = (String) preCollectionOrderHeader.get("docStatus"); 
			    //如订单状态为已清或作废，则不能再改变状态
			    if(preCollectionOrderDocStatus.equals(Constant.CLEARED)||preCollectionOrderDocStatus.equals(Constant.INVALID)){
			    	pObject.setFlag(Constant.NG);
					pObject.setMsg("订单状态为已清或作废时不能再改变其状态");
					return pObject;
			    }
			   //更新docStatus
			   if(UtilValidate.isNotEmpty(docStatus)){
				   preCollectionOrderHeader.set("docStatus",docStatus);
			   }
			   preCollectionOrderHeader.set("lastUpdateUserId",lastupdateuserId);
			   Date date = new Date();
			   Timestamp timestamp = new Timestamp(date.getTime());
			   preCollectionOrderHeader.set("lastUpdateDocDate",timestamp);
			   preCollectionOrderHeader.store();
			pObject.setFlag(Constant.OK);
		} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
		}
	
	// 更新订金预收单
	public static BasePosObject updatePreCollectionOrder(DispatchContext dctx,PreCollectionOrder preCollectionOrder) {
		Delegator delegator = dctx.getDelegator();
		GenericValue preCollectionOrderHeader = null;
		BasePosObject pObject = new BasePosObject();
		try {
			String docId = preCollectionOrder.getHeader().getDocId();
			 if(UtilValidate.isEmpty(docId)){
				 pObject.setFlag(Constant.NG);
			     pObject.setMsg("订单ID错误");
			     return pObject;
			  }
	    	// 根据ID 查询 订金预收单头
			preCollectionOrderHeader = delegator.findByPrimaryKey("PreCollectionOrderHeader", UtilMisc.toMap("docId", docId));
			 //订单状态为草稿,才能修改
			 String docStatus = (String)preCollectionOrderHeader.get("docStatus");
			 if(!docStatus.equals(Constant.DRAFT)){
				 pObject.setFlag(Constant.NG);
	    		 pObject.setMsg("订单状态不是草稿");
				 return pObject;
			  }
			
			// 更新订金预收单头
			updateGVPreCollectionOrderHeader(preCollectionOrderHeader,preCollectionOrder.getHeader());
			//删除原有明细
		    delegator.removeByAnd("PreCollectionOrderDtl",UtilMisc.toMap("docId",docId));
			//写入明细
			int len = preCollectionOrder.getDetail().size();
			for (int i = 0; i < len; i++) {
				 getGVPreCollectionOrderDtl(dctx,preCollectionOrder.getDetail().get(i));
			}
			pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
	    	pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
	     return pObject;
	}
		
	
	// 根据docId查找PreCollectionOrder
	@SuppressWarnings("unchecked")
	public static BasePosObject getPreCollectionOrderById(DispatchContext dctx,Map<String, Object>view) {
		BasePosObject pObject = new BasePosObject();
		List<GenericValue> values = null;
		OrderGenericValue order = new OrderGenericValue();
		 BasePosObject result = null;
		try {
			String docId = (String)view.get("docId");
			String where = "DOC_ID='" + docId + "'";
			Map<String, Object> viewMap = FastMap.newInstance();
			viewMap.put("where", where);
			 //查询Headers
			 result =  getPreCollectionOrderHeaderByCondition(dctx,viewMap);
			 values = (List<GenericValue>) result.getData();
			 if (UtilValidate.isNotEmpty(result.getData())) {
				 order.setHeader(values.get(0));
			 }
			// 查询Details
			result = getPreCollectionOrderDtlByCondition(dctx,viewMap);
			values = (List<GenericValue>) result.getData();
			if (UtilValidate.isEmpty(result.getData())) {
				order.setDetail(values);
			}
			pObject.setData(order);
			pObject.setFlag(Constant.OK);
		} catch (Exception e) {
			 pObject.setFlag(Constant.NG);
			 pObject.setMsg(e.getMessage());
			 e.printStackTrace();
		}
		return pObject;
	}
	
	// 根据条件查询PreCollectionOrderHeader
	public static BasePosObject getPreCollectionOrderHeaderByCondition(DispatchContext dctx, Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<GenericValue> gvpcoHeaders = null;
		List<String> orderBy = FastList.newInstance();
		try{
			String where =(String)view.get("where");
			EntityCondition mainCond = EntityCondition.makeConditionWhere(where);
			orderBy.add("lastUpdateDocDate DESC");//更新时间倒序排列
			gvpcoHeaders = delegator.findList("PreCollectionOrderHeader",mainCond, null,orderBy,null, false);
			pObject.setFlag(Constant.OK);
			if(UtilValidate.isNotEmpty(gvpcoHeaders)){
				pObject.setData(gvpcoHeaders);
			}
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
		
	//根据条件查询PreCollectionOrderDtl
	public static BasePosObject getPreCollectionOrderDtlByCondition(DispatchContext dctx, Map<String, Object>view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<GenericValue> gvpcoDtls = null;
		try{
			String where = (String)view.get("where");
			EntityCondition mainCond = EntityCondition.makeConditionWhere(where);
			//根据条件查找 List<GenericValue>
			gvpcoDtls = delegator.findList("PreCollectionOrderDtl",mainCond, null, UtilMisc.toList("docId"),null, false);
			pObject.setFlag(Constant.OK);
			if(UtilValidate.isNotEmpty(gvpcoDtls)){
				pObject.setData(gvpcoDtls);
			}
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//添加预订单收款额
	public static BasePosObject addPreCollectionAmount(DispatchContext dctx,Map<String, Object> view) {
		BasePosObject pObject =  new BasePosObject();
		try {
			//调用PreOrderWorker中的添加预收款方法。
			BasePosObject pObjectResult = PreOrderWorker.addPreOrderCollectionAmount(dctx, view);
			if(pObjectResult.getFlag().equals(Constant.NG)){
				pObject.setData(Constant.NG);
				pObject.setMsg(pObjectResult.getMsg());
				return pObject;
			}
			pObject.setFlag(Constant.OK);
	    }catch(Exception e){
	    	pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
	    	e.printStackTrace();
	    }   
		return pObject;
	}
						
		//根据参数得到GenericValue preCollectionOrderHeader(put)
		public static GenericValue getGVPreCollectionOrderHeader(DispatchContext dctx,PreCollectionOrderHeader pcoh){
			GenericValue gpreCollectionOrderHeader = null;
			Map<String,Object> pcohMap = FastMap.newInstance();
			Delegator delegator = dctx.getDelegator();
			try{
				pcohMap.put("docId",pcoh.getDocId());
				pcohMap.put("docNo",pcoh.getDocNo());
		        pcohMap.put("partyId",pcoh.getPartyId());
		        pcohMap.put("storeId",pcoh.getStoreId());
		        pcohMap.put("baseEntry",pcoh.getBaseEntry());
		        pcohMap.put("extSystemNo",pcoh.getExtSystemNo());
		        pcohMap.put("extDocNo",pcoh.getExtDocNo());
		        pcohMap.put("postingDate",pcoh.getPostingDate());
		        pcohMap.put("docStatus",pcoh.getDocStatus());
		        pcohMap.put("memo",pcoh.getMemo());
		        pcohMap.put("createUserId",pcoh.getCreateUserId());
		        Date date = new Date();
				Timestamp timestamp = new Timestamp(date.getTime());
				pcohMap.put("createDocDate",timestamp);
		        pcohMap.put("lastUpdateUserId",pcoh.getLastUpdateUserId());
		        pcohMap.put("lastUpdateDocDate",timestamp);
		        pcohMap.put("amount",pcoh.getAmount());
		        pcohMap.put("approvalUserId",pcoh.getApprovalUserId());
		        pcohMap.put("approvalDate",pcoh.getApprovalDate());
		        //订单状态非草稿状态置"1"
		        if(!pcoh.getDocStatus().equals(Constant.DRAFT)){
		        	pcohMap.put("isSync",Constant.ONE);
		        }else{
		        	pcohMap.put("isSync",pcoh.getIsSync());
		        }
		        pcohMap.put("printNum",pcoh.getPrintNum());
		        pcohMap.put("attrName1",pcoh.getAttrName1());
		        pcohMap.put("attrName2",pcoh.getAttrName2());
		        pcohMap.put("attrName3",pcoh.getAttrName3());
		        pcohMap.put("attrName4",pcoh.getAttrName4());
		        pcohMap.put("attrName5",pcoh.getAttrName5());
		        pcohMap.put("attrName6",pcoh.getAttrName6());
		        pcohMap.put("attrName7",pcoh.getAttrName7());
		        pcohMap.put("attrName8",pcoh.getAttrName8());
		        pcohMap.put("attrName9",pcoh.getAttrName9());
		        gpreCollectionOrderHeader = delegator.makeValue("PreCollectionOrderHeader", pcohMap);
		        gpreCollectionOrderHeader.create();
			}catch(Exception e){
				e.printStackTrace();
			}
			return gpreCollectionOrderHeader;
		}
		//根据参数得到 GenericValue preCollectionOrderDtl(put)
		public static GenericValue getGVPreCollectionOrderDtl(DispatchContext dctx,PreCollectionOrderDtl pcod){
			Delegator delegator = dctx.getDelegator();
			Map<String,Object> pcodMap = FastMap.newInstance();
			GenericValue gpreCollectionOrderDtl = null;
			 try{
				 pcodMap.put("docId",pcod.getDocId());
				 pcodMap.put("lineNo",pcod.getLineNo());
				 pcodMap.put("type", pcod.getType());
				 pcodMap.put("extNo", pcod.getExtNo());
				 pcodMap.put("amount",pcod.getAmount());
				 pcodMap.put("preRefundAmount",pcod.getPreRefundAmount());
				 pcodMap.put("cardCode", pcod.getCardCode());
				 pcodMap.put("cardName", pcod.getCardName());
				 pcodMap.put("code", pcod.getCode());
				 pcodMap.put("serialNo", pcod.getSerialNo());
				 pcodMap.put("memo", pcod.getMemo());
				 pcodMap.put("approvalUserId", pcod.getApprovalUserId());
				 pcodMap.put("lineNoBaseEntry",pcod.getLineNoBaseEntry());
				 pcodMap.put("style",pcod.getStyle());
		         pcodMap.put("attrName1",pcod.getAttrName1());
		         pcodMap.put("attrName2",pcod.getAttrName2());
		         pcodMap.put("attrName3",pcod.getAttrName3());
		         pcodMap.put("attrName4",pcod.getAttrName4());
		         pcodMap.put("attrName5",pcod.getAttrName5());
		         pcodMap.put("attrName6",pcod.getAttrName6());
		         pcodMap.put("attrName7",pcod.getAttrName7());
		         pcodMap.put("attrName8",pcod.getAttrName8());
		         pcodMap.put("attrName9",pcod.getAttrName9());
		         gpreCollectionOrderDtl = delegator.makeValue("PreCollectionOrderDtl", pcodMap);
		         gpreCollectionOrderDtl.create();
			 }catch(Exception e){
				 e.printStackTrace();
			 }
			return gpreCollectionOrderDtl;
		}
		
		//根据PreCollectionOrderHeader参数值修改GenericValue preCollectionOrderHeader(set)
		public static GenericValue updateGVPreCollectionOrderHeader(GenericValue gvpcoh,PreCollectionOrderHeader pcoh){
			try{
				gvpcoh.put("docId",pcoh.getDocId());
		        gvpcoh.put("docNo",pcoh.getDocNo());
		        gvpcoh.put("partyId",pcoh.getPartyId());
		        gvpcoh.put("storeId",pcoh.getStoreId());
		        gvpcoh.put("baseEntry",pcoh.getBaseEntry());
		        gvpcoh.put("extSystemNo",pcoh.getExtSystemNo());
		        gvpcoh.put("extDocNo",pcoh.getExtDocNo());
		        gvpcoh.put("postingDate",pcoh.getPostingDate());
		        gvpcoh.put("docStatus",pcoh.getDocStatus());
		        gvpcoh.put("memo",pcoh.getMemo());
		        gvpcoh.put("createUserId",pcoh.getCreateUserId());
		        Date date = new Date();
				Timestamp timestamp = new Timestamp(date.getTime());
				gvpcoh.put("createDocDate",timestamp);
		        gvpcoh.put("lastUpdateUserId",pcoh.getLastUpdateUserId());
		        gvpcoh.put("lastUpdateDocDate",timestamp);
		        gvpcoh.put("amount",pcoh.getAmount());
		        gvpcoh.put("approvalUserId",pcoh.getApprovalUserId());
		        gvpcoh.put("approvalDate",pcoh.getApprovalDate());
		        gvpcoh.put("isSync",pcoh.getIsSync());
		        gvpcoh.put("printNum",pcoh.getPrintNum());
		        gvpcoh.put("attrName1",pcoh.getAttrName1());
		        gvpcoh.put("attrName2",pcoh.getAttrName2());
		        gvpcoh.put("attrName3",pcoh.getAttrName3());
		        gvpcoh.put("attrName4",pcoh.getAttrName4());
		        gvpcoh.put("attrName5",pcoh.getAttrName5());
		        gvpcoh.put("attrName6",pcoh.getAttrName6());
		        gvpcoh.put("attrName7",pcoh.getAttrName7());
		        gvpcoh.put("attrName8",pcoh.getAttrName8());
		        gvpcoh.put("attrName9",pcoh.getAttrName9());
		        gvpcoh.store();
			}catch(Exception e){
				e.printStackTrace();
			}
			return gvpcoh;
		}
		
}
