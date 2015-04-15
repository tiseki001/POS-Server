package org.ofbiz.order.prerefundorder;
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
import org.ofbiz.order.backorder.BackOrderWorker;
import org.ofbiz.order.parameter.Parameter;
import org.ofbiz.order.preorder.OrderGenericValue;
import org.ofbiz.service.DispatchContext;
/**
 * Worker methods for PreRefundOrderWorker Information
 */
public class PreRefundOrderWorker {
	//创建收订金返还单
	 public static BasePosObject createPreRefundOrder(DispatchContext dctx ,PreRefundOrder preRefundOrder ){
		 BasePosObject pObject = new BasePosObject ();
		 try{
			 //写入header
			getGVPreRefundOrderHeader(dctx,preRefundOrder.getHeader());
			int len= preRefundOrder.getDetail().size();
			//循环写入明细
			 for(int i=0;i<len;i++){
				 getGVPreRefundOrderDtl(dctx,preRefundOrder.getDetail().get(i));
			 }
			 //调用接口更新退订单表头的退款金额(PreRefundAmount)
		     String docId = preRefundOrder.getHeader().getBaseEntry();
		     String lastUpdateUserId = preRefundOrder.getHeader().getLastUpdateUserId();
		     Timestamp lastUpdateDocDate = preRefundOrder.getHeader().getLastUpdateDocDate();
		     BigDecimal amount=preRefundOrder.getHeader().getAmount();
		     Map<String, Object> viewMap = FastMap.newInstance();
		     viewMap.put("amount",amount);
		     viewMap.put("lastUpdateUserId",lastUpdateUserId);
		     viewMap.put("lastUpdateDocDate",lastUpdateDocDate);
		     viewMap.put("docId",docId);
		     BasePosObject pObjectResult = BackOrderWorker.addBackOrderPreRefundAmount(dctx, viewMap);
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

	 //更新订金返还单状态
	 public static BasePosObject updatePreRefundOrderStatus(DispatchContext dctx,Parameter parameter) {
			Delegator delegator = dctx.getDelegator();
			BasePosObject pObject = new BasePosObject ();
			GenericValue preRefundOrderHeader = null;// 数据库中的数值对象 销售表头
			// 根据主键docId 查询PreRefundOrderHeader的非空的且不是部分已清的Status状态
			try{
				String docId = parameter.getDocId();
				String docStatus = parameter.getDocStatus();
				String lastUpdateUserId = parameter.getLastUpdateUserId();
				//Timestamp lastupdatedocdate = parameter.getLastUpdateDocDate();//待用
			    preRefundOrderHeader = delegator.findByPrimaryKey("PreRefundOrderHeader", UtilMisc.toMap("docId", docId));
				if(UtilValidate.isEmpty(preRefundOrderHeader)){
					pObject.setFlag(Constant.NG);
					pObject.setMsg("根据docId查询结果为空");
					return pObject;
				}
			    String preRefundOrderDocStatus = (String) preRefundOrderHeader.get("docStatus");
			    //如订单状态为已清或作废，则不能再改变状态
			    if(preRefundOrderDocStatus.equals(Constant.CLEARED)||preRefundOrderDocStatus.equals(Constant.INVALID)){
			    	pObject.setFlag(Constant.NG);
					pObject.setMsg("订单状态为已清或作废时不能再改变其状态");
					return pObject;
			    }
				//更新docStatus
				if(UtilValidate.isNotEmpty(docStatus)){
					preRefundOrderHeader.set("docStatus",docStatus);
				}
				preRefundOrderHeader.set("lastUpdateUserId",lastUpdateUserId);
				Date date = new Date();
				Timestamp timestamp = new Timestamp(date.getTime());
				preRefundOrderHeader.set("lastUpdateDocDate",timestamp);
				preRefundOrderHeader.set("isSync", Constant.ONE);
				preRefundOrderHeader.store();
				pObject.setFlag(Constant.OK);
			} catch (Exception e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
			}
			return pObject;
		}
	
    // 更新订金返还单
	public static BasePosObject updatePreRefundOrder(DispatchContext dctx,PreRefundOrder preRefundOrder) {
		Delegator delegator = dctx.getDelegator();
		GenericValue preRefundOrderHeader = null;
		BasePosObject pObject = new BasePosObject();
		try {
			String docId = preRefundOrder.getHeader().getDocId();
			 if(UtilValidate.isEmpty(docId)){
				 pObject.setFlag(Constant.NG);
			     pObject.setMsg("订单ID错误");
			     return pObject;
			  }
	    	// 根据ID 查询 订金返还单头
			preRefundOrderHeader = delegator.findByPrimaryKey("PreRefundOrderHeader", UtilMisc.toMap("docId", docId)); 
			 //订单状态为草稿,才能修改
			 String docStatus = (String)preRefundOrderHeader.get("docStatus");
			 if(!docStatus.equals(Constant.DRAFT)){
				 pObject.setFlag(Constant.NG);
	    		 pObject.setMsg("订单状态不是草稿");
				 return pObject;
			  }
			// 更新订金返还单头
			updateGVPreRefundOrderHeader(preRefundOrderHeader,preRefundOrder.getHeader());
			//删除原有明细
		    delegator.removeByAnd("PreRefundOrderDtl",UtilMisc.toMap("docId",docId));
			//写入新明细
			int len = preRefundOrder.getDetail().size();
			for (int i = 0; i < len; i++) {
				 getGVPreRefundOrderDtl(dctx,preRefundOrder.getDetail().get(i));
			}
			pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
	    	pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
	     return pObject;
	}
		
	
	//根据docId查找PreRefundOrder
	 
	@SuppressWarnings("unchecked")
	public static BasePosObject getPreRefundOrderById(DispatchContext dctx,Map<String, Object>view) {
		BasePosObject pObject = new BasePosObject();
		List<GenericValue> values = null;
		OrderGenericValue order = new OrderGenericValue();
		BasePosObject result = null;
		try {
			String docId = (String)view.get("docId");
			String where = "DOC_ID='" + docId + "'";
			 Map<String, Object> viewMap = FastMap.newInstance();
			 viewMap.put("where", where);
			 //查询preHeaders
			 result =  getPreRefundOrderHeaderByCondition(dctx,viewMap);
			 values = (List<GenericValue>) result.getData();
			 if (UtilValidate.isNotEmpty(result.getData())) {
				 order.setHeader(values.get(0));
			 }
			// 查询preOrderDtls
			result = getPreRefundOrderDtlByCondition(dctx,viewMap);
			values = (List<GenericValue>) result.getData();
			if (UtilValidate.isNotEmpty(result.getData())) {
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
		
	
	// 根据条件查询PreRefundOrderHeader
	public static BasePosObject getPreRefundOrderHeaderByCondition(DispatchContext dctx, Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<GenericValue> gvpcoHeaders = null;
		EntityCondition mainCond = null;
		List<String> orderBy = FastList.newInstance();
		try{
			String where =(String)view.get("where");
			mainCond = EntityCondition.makeConditionWhere(where);
			orderBy.add("lastUpdateDocDate DESC");//更新时间倒序排列
			gvpcoHeaders = delegator.findList("PreRefundOrderHeader",mainCond, null,orderBy,null, false);
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
		
	//根据条件查询PreRefundOrderDtl
	public static BasePosObject getPreRefundOrderDtlByCondition(DispatchContext dctx, Map<String, Object>view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<GenericValue> gvpcoDtls = null;
		try{
			String where = (String)view.get("where");
			EntityCondition mainCond = EntityCondition.makeConditionWhere(where);
			//根据条件查找 List<GenericValue>
			gvpcoDtls = delegator.findList("PreRefundOrderDtl",mainCond, null, UtilMisc.toList("docId"),null, false);
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
							
	//根据PreRefundOrderHeader参数得到GenericValue preRefundOrderHeader(put)
	public static GenericValue getGVPreRefundOrderHeader(DispatchContext dctx,PreRefundOrderHeader proh){
		GenericValue gpreRefundOrderHeader = null;
		Map<String,Object> pcohMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		try{
			pcohMap.put("docId",proh.getDocId());
			pcohMap.put("docNo",proh.getDocNo());
	        pcohMap.put("partyId",proh.getPartyId());
	        pcohMap.put("storeId",proh.getStoreId());
	        pcohMap.put("baseEntry",proh.getBaseEntry());
	        pcohMap.put("extSystemNo",proh.getExtSystemNo());
	        pcohMap.put("extDocNo",proh.getExtDocNo());
	        pcohMap.put("postingDate",proh.getPostingDate());
	        pcohMap.put("docStatus",proh.getDocStatus());
	        pcohMap.put("memo",proh.getMemo());
	        pcohMap.put("createUserId",proh.getCreateUserId());
	        Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());
			pcohMap.put("createDocDate",timestamp);
	        pcohMap.put("lastUpdateUserId",proh.getLastUpdateUserId());
	        pcohMap.put("lastUpdateDocDate",timestamp);
	        pcohMap.put("amount",proh.getAmount());
	        pcohMap.put("approvalUserId",proh.getApprovalUserId());
	        pcohMap.put("approvalDate",proh.getApprovalDate());
	        //订单状态非草稿状态置"1"
	        if(!proh.getDocStatus().equals(Constant.DRAFT)){
	        	pcohMap.put("isSync",Constant.ONE);
	        }else{
	        	pcohMap.put("isSync",proh.getIsSync());
	        }
	        pcohMap.put("printNum",proh.getPrintNum());
	        pcohMap.put("attrName1",proh.getAttrName1());
	        pcohMap.put("attrName2",proh.getAttrName2());
	        pcohMap.put("attrName3",proh.getAttrName3());
	        pcohMap.put("attrName4",proh.getAttrName4());
	        pcohMap.put("attrName5",proh.getAttrName5());
	        pcohMap.put("attrName6",proh.getAttrName6());
	        pcohMap.put("attrName7",proh.getAttrName7());
	        pcohMap.put("attrName8",proh.getAttrName8());
	        pcohMap.put("attrName9",proh.getAttrName9());
	        gpreRefundOrderHeader = delegator.makeValue("PreRefundOrderHeader", pcohMap);
	        gpreRefundOrderHeader.create();
		}catch(Exception e){
			e.printStackTrace();
		}
		return gpreRefundOrderHeader;
	}
	//根据PreRefundOrderHeader参数得到 GenericValue preRefundOrderDtl(put)
	public static GenericValue getGVPreRefundOrderDtl(DispatchContext dctx,PreRefundOrderDtl prod){
		Delegator delegator = dctx.getDelegator();
		Map<String,Object> pcodMap = FastMap.newInstance();
		GenericValue gpreRefundOrderDtl = null;
		 try{
			 pcodMap.put("docId",prod.getDocId());
			 pcodMap.put("lineNo",prod.getLineNo());
			 pcodMap.put("lineNoBaseEntry",prod.getLineNoBaseEntry());
			 pcodMap.put("type", prod.getType());
			 pcodMap.put("extNo", prod.getExtNo());
			 pcodMap.put("amount",prod.getAmount());
			 pcodMap.put("cardCode", prod.getCardCode());
			 pcodMap.put("cardName", prod.getCardName());
			 pcodMap.put("code", prod.getCode());
			 pcodMap.put("serialNo", prod.getSerialNo());
			 pcodMap.put("memo", prod.getMemo());
			 pcodMap.put("approvalUserId", prod.getApprovalUserId());
			 pcodMap.put("style", prod.getStyle());
	         pcodMap.put("attrName1",prod.getAttrName1());
	         pcodMap.put("attrName2",prod.getAttrName2());
	         pcodMap.put("attrName3",prod.getAttrName3());
	         pcodMap.put("attrName4",prod.getAttrName4());
	         pcodMap.put("attrName5",prod.getAttrName5());
	         pcodMap.put("attrName6",prod.getAttrName6());
	         pcodMap.put("attrName7",prod.getAttrName7());
	         pcodMap.put("attrName8",prod.getAttrName8());
	         pcodMap.put("attrName9",prod.getAttrName9());
	         gpreRefundOrderDtl = delegator.makeValue("PreRefundOrderDtl", pcodMap);
	         gpreRefundOrderDtl.create();
		 }catch(Exception e){
			 e.printStackTrace();
		 }
		return gpreRefundOrderDtl;
	}
	
	//根据PreRefundOrderHeader参数值修改GenericValue preRefundOrderHeader(set)
	public static GenericValue updateGVPreRefundOrderHeader(GenericValue gvpcoh,PreRefundOrderHeader pcoh){
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
