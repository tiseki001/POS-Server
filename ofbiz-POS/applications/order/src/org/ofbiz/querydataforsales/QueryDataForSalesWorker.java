package org.ofbiz.querydataforsales;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.common.BasePosObject;
import org.ofbiz.common.Constant;
import org.ofbiz.delivery.webService.DeliveryWebWork;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.inventory.webService.InventoryWebWork;
import org.ofbiz.order.parameter.Parameter;
import org.ofbiz.product.salesprice.SalesPriceWorker;
import org.ofbiz.service.DispatchContext;

public class QueryDataForSalesWorker {
	//根据partyId,sotreId查询List<productPriceTypeId>
	public static BasePosObject getProductPriceTypeList(DispatchContext dctx,Parameter parameter) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<GenericValue> productPriceTypeIdList = null;
		EntityCondition mainCond = null;// 条件
    	List<EntityCondition> andExprs = FastList.newInstance();
    	EntityFindOptions findOptions =  new EntityFindOptions() ;
    	Set<String> fieldsToSelect = FastSet.newInstance();
		try{
			andExprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, parameter.getPartyId()));
			andExprs.add(EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, parameter.getProductStoreId()));
			mainCond = EntityCondition.makeCondition(andExprs,EntityOperator.AND);
			findOptions.setDistinct(true);
        	fieldsToSelect.add("productPriceTypeId");
			productPriceTypeIdList = delegator.findList("ProductStoreRolePriceType", mainCond,fieldsToSelect, null,findOptions, false);
			if(UtilValidate.isNotEmpty(productPriceTypeIdList)){
				pObject.setData(productPriceTypeIdList);
			}
			pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
        	pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//根据条件查询商品
	@SuppressWarnings("unchecked")
	public static BasePosObject getProductByCond(DispatchContext dctx,Product pt) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<Object> list = null;
		List<ProductPriceType> productPriceTypes = null;
		ProductSalesPolicy productSalesPolicy = null;
		Map<String,Object> g = null;
		ProductPriceType productPriceType = null;
		List<GenericValue> productSalesPolicys = null;
		List<ProductSalesPolicy> productSalesPolicyList = FastList.newInstance();
		List<GenericValue> productPriceList = null;
		try{
			String productId = pt.getProductId();
			String productName = pt.getProductName();
			String idValue = pt.getIdValue();
			String sequenceId = pt.getSequenceId();
			String productStoreId = pt.getProductStoreId();
			String movementTypeId = pt.getMovementTypeId();
			Map<String, Object> viewMap = FastMap.newInstance();
			//根据商品Id查  
			if(UtilValidate.isNotEmpty(productId)){
				viewMap.put("productId",productId);
			}
			//根据商品Name查
			if(UtilValidate.isNotEmpty(productName)){
				viewMap.put("productName",productName);
			}
			//根据条码查
			if(UtilValidate.isNotEmpty(idValue)){
				viewMap.put("idValue",idValue);
			}
			//根据串号查
			if(UtilValidate.isNotEmpty(sequenceId)){
				viewMap.put("sequenceId",sequenceId);
			}
			viewMap.put("productStoreId",productStoreId);
			viewMap.put("movementTypeId",movementTypeId);//移动类型选仓库
			viewMap.put("isStock","N");
			BasePosObject  pObjectResult = InventoryWebWork.findInventoreItem(dctx,viewMap);
			List<GenericValue> j = (List<GenericValue>)pObjectResult.getData();
			//查询商品唯一时，返回此商品
			if(UtilValidate.isNotEmpty(j) && j.size()==1){
				//根据查询结果的productId，productPriceTypeId查询基础价格
				productPriceList = delegator.findByAnd("ProductPrice", UtilMisc.toMap(
        				"productId", j.get(0).getString("productId"),
        				"productPriceTypeId","SUGGESTED_PRICE"));
				
				Product product = new Product();
				product.setProductId(j.get(0).getString("productId"));
				product.setProductName(j.get(0).getString("productName"));
				product.setIdValue(j.get(0).getString("idValue"));
				if(UtilValidate.isNotEmpty(j.get(0).getString("onhand"))){
					product.setOnhand(Long.valueOf(j.get(0).getString("onhand")));//库存数量
				}
				if(UtilValidate.isNotEmpty(j.get(0).getString("promise"))){
					product.setPromise(Long.valueOf(j.get(0).getString("promise")));//占用库存
				}
				if(UtilValidate.isNotEmpty(j.get(0).get("requireInventory"))&&j.get(0).getString("requireInventory").equals("N")){
					product.setFacilityId("");
				}else{
					product.setFacilityId(j.get(0).getString("facilityId"));//仓库ID
				}
				product.setIsSequence(j.get(0).getString("isSequence"));//是否串号管理
				if(UtilValidate.isNotEmpty(sequenceId)){
					product.setSequenceId((String)j.get(0).getString("sequenceId"));//串号
					product.setIsOccupied(j.get(0).getString("isOccupied"));//是否占用库存
				}
				if(UtilValidate.isEmpty(j.get(0).getString("productStoreId"))){
					product.setProductStoreId(productStoreId);
				}else{
					product.setProductStoreId((String)j.get(0).getString("productStoreId"));
				}
				if(UtilValidate.isNotEmpty(productPriceList)){
					product.setSuggestedPrice((BigDecimal)productPriceList.get(0).get("price"));
				}
		        //根据商品Id,店面Id查询销售政策
		        productSalesPolicys = SalesPriceWorker.getProductSalesPolicyById(product.getProductId(), product.getProductStoreId(), delegator);
	
		        if(UtilValidate.isNotEmpty(productSalesPolicys)){
			        for(GenericValue p : productSalesPolicys){
			        	String productSalesPolicyId = (String)p.get("productSalesPolicyId");
			        	String policyName = (String)p.get("policyName");
			        	Long mainRatio = (Long)p.get("mainRatio");
			            productPriceTypes = FastList.newInstance();
			        	productSalesPolicy = new ProductSalesPolicy();
			        	productSalesPolicy.setProductSalesPolicyId(productSalesPolicyId);
			        	productSalesPolicy.setPolicyName(policyName);
			        	productSalesPolicy.setMainRatio(mainRatio);
			        	//根据商品Id,商品销售政策Id,门店Id查询价格类型List
			        	list =SalesPriceWorker.getProductPriceType(product.getProductId(),productStoreId, productSalesPolicyId, delegator);
			        	for(Object o : list){
			        	    g = (Map<String,Object>)o;
			        	    productPriceType = new ProductPriceType();
			        		productPriceType.setProductPriceRuleId((String)g.get("productPriceRuleId"));
			        		productPriceType.setProductPriceTypeId((String)g.get("productPriceTypeId"));
			        		productPriceType.setDescription((String)g.get("description"));
			        		productPriceType.setPrice((String)g.get("price"));
			        		productPriceTypes.add(productPriceType);
			        	}
			        	
//---------------------增加按串号调价的逻辑
			        	List<GenericValue> seqPriceList = delegator.findByAnd("ProductPromoSpecialserial", UtilMisc.toMap(
		        				"productId", product.getProductId(),
		        				"sequenceId",product.getSequenceId()));
			        	if(seqPriceList!=null&&seqPriceList.size()>0){
			        		for(ProductPriceType ppt : productPriceTypes){
			        			if(ppt.getProductPriceTypeId().equals("GUIDE_PRICE")){
			        				ppt.setPrice((String)seqPriceList.get(0).get("guidePrice"));
			        			}
			        			if(ppt.getProductPriceTypeId().equals("CKECK_PRICE")){
			        				ppt.setPrice((String)seqPriceList.get(0).get("checkPrice"));
			        			}
			        			if(ppt.getProductPriceTypeId().equals("SALES_PRICE")){
			        				ppt.setPrice((String)seqPriceList.get(0).get("salesPrice"));
			        			}
			        		}
			        	}
			        	
			        	
//------------------------------------------------------------------			        	
			        	productSalesPolicy.setProductPriceTypes(productPriceTypes);
			        	productSalesPolicyList.add(productSalesPolicy);
				    }
			        product.setProductSalesPolicys(productSalesPolicyList);
		        }
			    pObject.setData(product);
			}
		    pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
        	pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	
	//根据条件查询预订商品
	@SuppressWarnings("unchecked")
	public static BasePosObject getPreOrderProductByCond(DispatchContext dctx,Product pt) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<GenericValue> productPriceList = null;
		try{
			String productId = pt.getProductId();
			String productName = pt.getProductName();
			String idValue = pt.getIdValue();
			String sequenceId = pt.getSequenceId();
			String productStoreId = pt.getProductStoreId();
			String movementTypeId = pt.getMovementTypeId();
			Map<String, Object> viewMap = FastMap.newInstance();
			//根据商品Id查  
			if(UtilValidate.isNotEmpty(productId)){
				viewMap.put("productId",productId);
			}
			//根据商品Name查
			if(UtilValidate.isNotEmpty(productName)){
				viewMap.put("productName",productName);
			}
			//根据条码查
			if(UtilValidate.isNotEmpty(idValue)){
				viewMap.put("idValue",idValue);
			}
			//根据串号查
			if(UtilValidate.isNotEmpty(sequenceId)){
				viewMap.put("sequenceId",sequenceId);
			}
			viewMap.put("productStoreId",productStoreId);
			viewMap.put("movementTypeId",movementTypeId);//移动类型选仓库
			viewMap.put("isStock","N");
			BasePosObject  pObjectResult=InventoryWebWork.findInventoreItem(dctx,viewMap);
			List<GenericValue> j = (List<GenericValue>)pObjectResult.getData();
			//查询商品唯一时，返回此商品
			if(UtilValidate.isNotEmpty(j) && j.size()==1){
				//根据查询结果的productId，productPriceTypeId查询基础价格
				productPriceList = delegator.findByAnd("ProductPrice", UtilMisc.toMap(
        				"productId", j.get(0).getString("productId"),
        				"productPriceTypeId","SUGGESTED_PRICE"));
				
				Product product = new Product();
				product.setProductId(j.get(0).getString("productId"));
				product.setProductName(j.get(0).getString("productName"));
				product.setIdValue(j.get(0).getString("idValue"));
				if(UtilValidate.isNotEmpty(j.get(0).getString("onhand"))){
					product.setOnhand(Long.valueOf(j.get(0).getString("onhand")));//库存数量
				}
				if(UtilValidate.isNotEmpty(j.get(0).getString("promise"))){
					product.setPromise(Long.valueOf(j.get(0).getString("promise")));//占用库存
				}
				product.setFacilityId(j.get(0).getString("facilityId"));//仓库ID
				product.setIsSequence(j.get(0).getString("isSequence"));//是否串号管理
				if(UtilValidate.isNotEmpty(sequenceId)){
					product.setSequenceId((String)j.get(0).getString("sequenceId"));//串号
					product.setIsOccupied(j.get(0).getString("isOccupied"));//是否占用库存
				}
				if(UtilValidate.isEmpty(j.get(0).getString("productStoreId"))){
					product.setProductStoreId(productStoreId);
				}else{
					product.setProductStoreId((String)j.get(0).getString("productStoreId"));
				}
				if(UtilValidate.isNotEmpty(productPriceList)){
					product.setSuggestedPrice((BigDecimal)productPriceList.get(0).get("price"));
				}
			    pObject.setData(product);
			}
		    pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
        	pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	
	// 根据销售政策id,门店id,获得套包集合
	@SuppressWarnings("unchecked")
	public static  BasePosObject getBomByCond(DispatchContext dctx,SubProduct subProduct) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		ProductSalesPolicyBom productSalesPolicyBom = null;
		List<GenericValue> gvList = null;
		Map<String,Object> bomMap = null;
		List<ProductSalesPolicyBom> productSalesPolicyBoms = FastList.newInstance();
		try{
			String productStoreId = subProduct.getProductStoreId();
			String productSalesPolicyId = subProduct.getProductSalesPolicyId();
	    	//根据销售政策Id查询套包商品套包集合
	    	gvList = SalesPriceWorker.getProductSalesPolicyBoms(productStoreId,productSalesPolicyId, delegator);
	    	if(UtilValidate.isNotEmpty(gvList)){//根据销售政策，门店Id查询出BomList
		    	for(Object obj : gvList){
		    		bomMap = (Map<String,Object>)obj;
		    		String bomId = (String)bomMap.get("productId");
		    		String bomName = (String)bomMap.get("productName");
		    	    productSalesPolicyBom = new ProductSalesPolicyBom(bomId,bomName);
			   		productSalesPolicyBoms.add(productSalesPolicyBom);
		    	}
		    	pObject.setData(productSalesPolicyBoms);
	    	}
	    	pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
        	pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	
	//根据套包Id,门店Id查询商品
	@SuppressWarnings("unchecked")
	public static  BasePosObject getProductByBomCond(DispatchContext dctx,SubProduct subProduct) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<Product> products =  FastList.newInstance();
		Product pt = null;
		BasePosObject productObject = null;
		Map<String,Object> map = null;
		List<GenericValue> gvproducts = null;
		try{
			String bomId = subProduct.getBomId();
			String productStoreId = subProduct.getProductStoreId();
			String movementTypeId = subProduct.getMovementTypeId();
			List<EntityCondition> andExprs = FastList.newInstance();
			andExprs.add(EntityCondition.makeConditionDate("fromDate", "thruDate"));
			andExprs.add(EntityCondition.makeCondition("productId",bomId));
			EntityCondition mainCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
	        EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, -1, true);

			gvproducts = delegator.findList("ProductAssoc", mainCond, UtilMisc.toSet("productId","productIdTo","productAssocTypeId"), null, findOpts, true);
    		//商品有套包时返回套包下商品，没有套包时返回商品本身
    		if(UtilValidate.isEmpty(gvproducts)){
    			pt = new Product();
	    		pt.setProductId(bomId);
	    		pt.setProductStoreId(productStoreId);
	    		pt.setMovementTypeId(movementTypeId);
	    		productObject =  QueryDataForSalesWorker.getProductByCond(dctx,pt);
    			products.add((Product)productObject.getData());
	    	}else{
				for(Object obj : gvproducts){
					map = (Map<String,Object>)obj;
					pt = new Product();
		    		pt.setProductId((String)map.get("productIdTo"));
		    		pt.setProductStoreId(productStoreId);
		    		pt.setMovementTypeId(movementTypeId);
		    		productObject =  QueryDataForSalesWorker.getProductByCond(dctx,pt);
		    		products.add((Product)productObject.getData());
				}
	    	}
	    	pObject.setFlag(Constant.OK);
	    	pObject.setData(products);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
        	pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	
	

	//根据条件查询付款方式
	public static BasePosObject getPaymentMethod(DispatchContext dctx,Map<String, Object> view) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		List<Object> paymentMethod = FastList.newInstance();
		List<GenericValue> list = null;
		Map<String,Object> map = null;
		//根据storeId 查询PaymentMethod
		try {
			String storeId = (String)view.get("storeId");
		    list = delegator.findByAnd("PaymentMethodMessage",UtilMisc.toMap("storeId",storeId));
		    //如果门店支付方式为空，返回全部支付方式
			if(UtilValidate.isEmpty(list)){
				list = delegator.findList("PaymentMethodType", null,null,null,null,false);
			}
			for(GenericValue gv : list){
				String paymentMethodTypeId = (String)gv.get("paymentMethodTypeId");
				String  description =(String)gv.get("description");
				map = FastMap.newInstance();
				map.put("paymentMethodTypeId",paymentMethodTypeId);
				map.put("description",description);
				paymentMethod.add(map);
			}
			pObject.setData(paymentMethod);
			pObject.setFlag(Constant.OK);
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG);
        	pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	//根据条件获取缴款额
	@SuppressWarnings("unchecked")
	public static BasePosObject getPaymentAmount(DispatchContext dctx,Parameter parameter) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		BasePosObject pObjectResult = null;
		EntityCondition mainCond = null;// 条件
		//EntityCondition inOrderAmountMainCond = null;// 条件
		List<String> orderBy = FastList.newInstance();// 排序条件
		List<EntityCondition> andExprs = FastList.newInstance();
		List<Map<String,Object>> listData = null;
		List<Object> listObject = FastList.newInstance();
		Map<String, Object> view = FastMap.newInstance();
		Map<String, BigDecimal> targetAmountMap = FastMap.newInstance();
		Map<String, BigDecimal> PreAmountMap = FastMap.newInstance();
		try{
			//创建Map（TargetAmount）Map（PreAmount）
			String storeId = parameter.getStoreId();
			view.put("storeId", storeId);
			pObjectResult = getPaymentMethod(dctx,view);
			listData = (List<Map<String,Object>>)pObjectResult.getData();
			for(int i=0;i<listData.size();i++){
				targetAmountMap.put((String)listData.get(i).get("paymentMethodTypeId"),new BigDecimal(0));
				PreAmountMap.put((String)listData.get(i).get("paymentMethodTypeId"),new BigDecimal(0));
			}
			String postingDate = parameter.getPostingDate();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");   
			Calendar cd = Calendar.getInstance();   
            cd.setTime(sdf.parse(postingDate));   
            cd.add(Calendar.DATE, 1);
            String endDate =sdf.format(cd.getTime());
            Date date1 = new Date(sdf.parse(postingDate).getTime());
            Date date2 = new Date(sdf.parse(endDate).getTime());
			if(Constant.isNotNull(postingDate)){
				andExprs.add(EntityCondition.makeCondition("postingDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(date1.getTime())));
				andExprs.add(EntityCondition.makeCondition("postingDate", EntityOperator.LESS_THAN, new Timestamp(date2.getTime())));
			}
			if (UtilValidate.isNotEmpty(parameter.getStoreId())) {
				andExprs.add(EntityCondition.makeCondition("storeId", EntityOperator.EQUALS,parameter.getStoreId()));
			}
			if (andExprs.size() > 0) {
				mainCond = EntityCondition.makeCondition(andExprs,EntityOperator.AND);
				//inOrderAmountMainCond = EntityCondition.makeCondition("storeId", EntityOperator.EQUALS,parameter.getStoreId());
			}
			 orderBy.add("type");
			//根据条件处理订金预订单、收款单、订金返还单、退款单、缴款单 中的 amount
			addOrderAmount(delegator,"PreCollectionOrderAmount",mainCond,orderBy,targetAmountMap);
			addOrderAmount(delegator,"CollectionOrderAmount",mainCond,orderBy,targetAmountMap);
			subOrderAmount(delegator,"PreRefundOrderAmount",mainCond,orderBy,targetAmountMap);
			subOrderAmount(delegator,"RefundOrderAmount",mainCond,orderBy,targetAmountMap);
			//addOrderAmount(delegator,"PaymentInOrderAmount",inOrderAmountMainCond,orderBy,targetAmountMap);//2014.12.25缴款额中取消前日最后一次缴款的差异金额
			addOrderAmount(delegator,"PaymentInOrderPreAmount",mainCond,orderBy,PreAmountMap);		
		    //返回门店中各种支付方式的TargetAmount、PreAmount
			for(Map<String, Object> gv : listData){
				String paymentMethodTypeId = (String)gv.get("paymentMethodTypeId");
				PaymentMethodAmount p = new PaymentMethodAmount();
				p.setType((String)gv.get("paymentMethodTypeId"));
				p.setTypeName((String)gv.get("description"));
				if(targetAmountMap.containsKey(paymentMethodTypeId)){
					p.setTargetAmount(targetAmountMap.get(paymentMethodTypeId));
				}
				if(PreAmountMap.containsKey(paymentMethodTypeId)){
					p.setPreAmount(PreAmountMap.get(paymentMethodTypeId));
				}
				listObject.add(p);
		    }
			pObject.setData(listObject);
			pObject.setFlag(Constant.OK);
		}catch(Exception e){
			pObject.setFlag(Constant.NG);
        	pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
	public static void addOrderAmount(Delegator delegator,String viewName,EntityCondition mainCond,List<String> orderBy,Map<String, BigDecimal> keys){
		List<GenericValue> list=null;
//		String docId = null;
		try {
//			if(viewName.equals("PaymentInOrderAmount")){
//				orderBy.remove("type");
//				orderBy.add("docId DESC");//首先按docId倒序选出最大的一个
//				orderBy.add("type");
//				list = delegator.findList(viewName,mainCond, null, orderBy, null, false );
//				docId = (String)list.get(0).get("docId");
//				for(GenericValue gv : list){//获取docId最大的所有支付方式
//					if(docId.equals((String) gv.get("docId"))){
//						String type = (String) gv.get("type");
//						BigDecimal  amount =(BigDecimal) gv.get("amount");
//						if(keys.containsKey(type)){
//							keys.put(type, keys.get(type).add(amount));
//						}
//					}
//				}
//				orderBy.remove("docId DESC");
//			}else{
				list = delegator.findList(viewName,mainCond, null, orderBy, null, false );
				for(GenericValue gv : list){
					String type = (String)gv.get("type");
					BigDecimal  amount =(BigDecimal)gv.get("amount");
					if(keys.containsKey(type)){
						keys.put(type, keys.get(type).add(amount));
					}
				}
//			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
	}
	public static void subOrderAmount(Delegator delegator,String viewName,EntityCondition mainCond,List<String> orderBy,Map<String, BigDecimal> keys){
		List<GenericValue> list=null;
		try {
			list = delegator.findList(viewName,mainCond, null, orderBy, null, false );
			for(GenericValue gv : list){
				String type = (String)gv.get("type");
				BigDecimal  amount =(BigDecimal)gv.get("amount");
				if(keys.containsKey(type)){
					keys.put(type, keys.get(type).subtract(amount));
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
	}
	 //根据会员手机号获得会员ID和会员姓名。
		public static BasePosObject getMemberByPhoneNo(DispatchContext dctx,Map<String,Object>view){
			Delegator delegator = dctx.getDelegator();
			BasePosObject pObject = new BasePosObject ();
			Person person = null;
			 List<GenericValue> resultList=null;
			List<Person> persons = FastList.newInstance();
		
			try{
				if (UtilValidate.isNotEmpty(view)){
					String phoneMobile = (String)view.get("phoneMobile");
					resultList = CustomerAndSalesPerson.getCustomer(delegator, phoneMobile, null);
				 for(GenericValue g : resultList){
					Person pson=getPersons(g);	//根据参数获得person
					persons.add(pson); //放入persons的list中	
					}	
				 //如果多条数据返回错误信息

				 if(UtilValidate.isEmpty(persons)&&persons.size()!=1){
					 pObject.setFlag(Constant.OK);
					 return pObject;
				 }

				 //如果为一条数据返回id和姓名
					if(persons.size()==1){
						for(int i =0; i<persons.size();i++){
					 person=  persons.get(i);
					 //销售人员
					 Customer customer = new Customer();
					 customer.setPartyId(person.getPartyId());
					 customer.setFirstName(person.getFirstName());
					 customer.setMiddleName(person.getMiddleName());
					 customer.setLastName(person.getLastName());
					 pObject.setFlag(Constant.OK);
					pObject.setData(customer);
						}
					}
				}
					}catch (Exception e) {
						 pObject.setFlag(Constant.NG);
						 pObject.setMsg(e.getMessage());
						e.printStackTrace();
					}			
					return pObject;
		}
		
		/*
		 * 根据销售人员id查找下销售人员姓名
		 */
		public static BasePosObject getSalesPersonBySalesPersonId(DispatchContext dctx,Map<String,Object>view){

			Delegator delegator = dctx.getDelegator();
			BasePosObject pObject = new BasePosObject();
			List<GenericValue> Personinfo = null;
			EntityFindOptions findOptions =  new EntityFindOptions() ;	     
			try {
				if (UtilValidate.isNotEmpty(view)) {
					String partyId = (String) view.get("partyId");
					List<EntityCondition> condList = FastList.newInstance();
					List<EntityCondition> condList1 = FastList.newInstance();
					List<EntityCondition> condListtime = FastList.newInstance();
					Timestamp fromDate = UtilDateTime.nowTimestamp();
					condListtime.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate));
			        EntityCondition thruCond = EntityCondition.makeCondition(UtilMisc.toList(
			                EntityCondition.makeCondition("thruDate", null),
			                EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, fromDate)),
			                EntityOperator.OR);
			        condListtime.add(thruCond);
			        EntityCondition conditiontime = EntityCondition.makeCondition(condListtime);
			        
			        condList1.add(conditiontime);
		
					condList.add(EntityCondition.makeCondition("roleTypeId","EMPLOYEE"));
					condList.add(EntityCondition.makeCondition("parentTypeId","EMPLOYEE"));
					EntityCondition condition = EntityCondition.makeCondition(condList, EntityOperator.OR);
					condList1.add(condition);
					if (UtilValidate.isNotEmpty(partyId)){
						condList1.add(EntityCondition.makeCondition("partyId", partyId));
					}
					EntityCondition condition1 = EntityCondition.makeCondition(condList1, EntityOperator.AND);
                    findOptions.setDistinct(true);
					Personinfo = delegator.findList("CustomerAndpersonView",
							condition1, UtilMisc.toSet("partyId","firstName","middleName","lastName","phoneMobile"), null, findOptions, true);
					if(UtilValidate.isNotEmpty(Personinfo) && Personinfo.size() == 1){
						pObject.setData(Personinfo.get(0));
					}
					pObject.setFlag(Constant.OK);
				}
			} catch (Exception e) {
				e.printStackTrace();
				pObject.setMsg(e.getMessage());
				pObject.setFlag(Constant.NG);
			}
			return pObject;
			
			
			//			Delegator delegator = dctx.getDelegator();
//			BasePosObject pObject = new BasePosObject ();
//			Person person = null;
//			 List<GenericValue> resultList=null;
//			List<Person> persons = FastList.newInstance();
//		
//			try{
//				if (UtilValidate.isNotEmpty(view)){
//					String partyId = (String)view.get("partyId");
//					resultList = CustomerAndSalesPerson.getEmployee(delegator, partyId);
//				 for(GenericValue g : resultList){
//					Person pson=getPersons(g);	//根据参数获得person
//					persons.add(pson); //放入persons的list中	
//					}	
//				 //如果多条数据返回错误信息
//
//				 if(UtilValidate.isEmpty(persons)&&persons.size()!=1){
//					 pObject.setFlag(Constant.OK);
//					 return pObject;
//				 }
//
//				 //如果为一条数据返回id和姓名
//					if(persons.size()==1){
//						for(int i =0; i<persons.size();i++){
//					 person=  persons.get(i);
//					 //销售人员
//					 Sales sales = new Sales();
//					 sales.setFirstName(person.getFirstName());
//					 sales.setMiddleName(person.getMiddleName());
//					 sales.setLastName(person.getLastName());
//					 pObject.setFlag(Constant.OK);
//					pObject.setData(sales);
//						}
//					}
//				}
//					}catch (Exception e) {
//						 pObject.setFlag(Constant.NG);
//						 pObject.setMsg(e.getMessage());
//						e.printStackTrace();
//					}			
//					return pObject;
		}
			//根据参数得到 
			public static Person getPersons(GenericValue person) {
				Person Pperson = null;
				try {
				Pperson = new Person();
				Pperson.setPartyId((String)person.get("partyId"));
				Pperson.setFirstName((String)person.get("firstName"));
				Pperson.setMiddleName((String)person.get("middleName"));
				Pperson.setLastName((String)person.get("lastName"));
				Pperson.setPhoneMobile((String)person.get("phoneMobile"));
			}catch (Exception e) {
				e.printStackTrace();
			}
				return Pperson;

			}
		/*
		 * 根据门店Id和移动类型查询仓库
		 */
		@SuppressWarnings("unchecked")
		public static BasePosObject getFacilityByCond(DispatchContext dctx,Product product) {
			Map<String,Object> mapIn = FastMap.newInstance();
			BasePosObject pObject = new BasePosObject ();
			List<GenericValue> facilityList = null;
			try{
				String productStoreId = product.getProductStoreId();
				String movementTypeId = product.getMovementTypeId();
				mapIn.put("productStoreId",productStoreId);
				mapIn.put("movementTypeId",movementTypeId);
				BasePosObject pObjectDelivery = DeliveryWebWork.findFacility(dctx, mapIn);
				if(UtilValidate.isEmpty(pObjectDelivery.getData())){
					pObject.setFlag(Constant.OK);
					return pObject;
				}
				facilityList = (List<GenericValue>)pObjectDelivery.getData();
				String facilityId = (String)facilityList.get(0).get("facilityId");
				String facilityName = (String)facilityList.get(0).get("facilityName");
				Facility facility = new Facility();
				facility.setFacilityId(facilityId);
				facility.setFacilityName(facilityName);
				pObject.setFlag(Constant.OK);
				pObject.setData(facility);
			}catch(Exception e){
				pObject.setFlag(Constant.NG);
			    pObject.setMsg(e.getMessage());
				e.printStackTrace();
			}
			return pObject;
		}

		//修改订单头备注信息
		@SuppressWarnings("unused")
		public static BasePosObject updateHeaderMemo(DispatchContext dctx,Parameter parameter) {
			Delegator delegator = dctx.getDelegator();
			BasePosObject pObject = new BasePosObject();
			try {
				String docId = parameter.getDocId();
				String header = parameter.getHeader();
				String memo = parameter.getMemo();
				String lastUpdateUserId = parameter.getLastUpdateUserId();
				Timestamp lastUpdateDocDate = parameter.getLastUpdateDocDate();//待用
				GenericValue orderHeader= delegator.findByPrimaryKey(header,UtilMisc.toMap("docId",docId));
				if(UtilValidate.isEmpty(orderHeader)){
					pObject.setFlag(Constant.NG);
		        	pObject.setMsg("无此订单");
		        	return pObject;
				}
				orderHeader.set("memo",memo);
				orderHeader.set("lastUpdateUserId",lastUpdateUserId);
				orderHeader.set("lastUpdateDocDate",UtilDateTime.nowTimestamp());
				orderHeader.store();
				pObject.setFlag(Constant.OK);
			} catch (GenericEntityException e) {
				pObject.setFlag(Constant.NG);
	        	pObject.setMsg(e.getMessage());
				e.printStackTrace();
			}
			return pObject;
		}

		//修改订单明细备注信息
		public static BasePosObject updateDetailMemo(DispatchContext dctx,Parameter parameter) {
			Delegator delegator = dctx.getDelegator();
			BasePosObject pObject = new BasePosObject();
			try {
				String docId = parameter.getDocId();
				Long lineNo = parameter.getLineNo();
				String detail = parameter.getDetail();
				String memo = parameter.getMemo();
				GenericValue orderDetail= delegator.findByPrimaryKey(detail,UtilMisc.toMap("docId",docId,"lineNo",lineNo));
				if(UtilValidate.isEmpty(orderDetail)){
					pObject.setFlag(Constant.NG);
		        	pObject.setMsg("无此订单");
		        	return pObject;
				}
				orderDetail.set("memo",memo);
				orderDetail.store();
				pObject.setFlag(Constant.OK);
			} catch (GenericEntityException e) {
				pObject.setFlag(Constant.NG);
	        	pObject.setMsg(e.getMessage());
				e.printStackTrace();
			}
			return pObject;
		}
		
		
		/*
		 * getPersonByProductStoreId
		 */
		public static BasePosObject getPersonByProductStoreId(DispatchContext dctx,
				Map<String, Object> view) {
			Delegator delegator = dctx.getDelegator();
			BasePosObject pObject = new BasePosObject();
			List<GenericValue> Personinfo = null;
			try {
				if (UtilValidate.isNotEmpty(view)) {
					String productStoreId = (String) view.get("productStoreId");
					String roleTypeId = (String) view.get("roleTypeId");
					List<EntityCondition> condList = FastList.newInstance();
					List<EntityCondition> condList1 = FastList.newInstance();
					List<EntityCondition> condListtime = FastList.newInstance();
					Timestamp fromDate = UtilDateTime.nowTimestamp();
					condListtime.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate));
			        EntityCondition thruCond = EntityCondition.makeCondition(UtilMisc.toList(
			                EntityCondition.makeCondition("thruDate", null),
			                EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, fromDate)),
			                EntityOperator.OR);
			        condListtime.add(thruCond);
			        EntityCondition conditiontime = EntityCondition.makeCondition(condListtime);
			        
			        condList1.add(conditiontime);
					if (!UtilValidate.isNotEmpty(roleTypeId)) {
						condList.add(EntityCondition.makeCondition("roleTypeId",
								"EMPLOYEE"));
						condList.add(EntityCondition.makeCondition("parentTypeId",
								"EMPLOYEE"));
						EntityCondition condition = EntityCondition.makeCondition(
								condList, EntityOperator.OR);

						condList1.add(condition);
					}else{
						condList1.add(EntityCondition.makeCondition("roleTypeId",
								roleTypeId));
					}
					if (UtilValidate.isNotEmpty(productStoreId))
						condList1.add(EntityCondition.makeCondition(
								"productStoreId", productStoreId));

					EntityCondition condition1 = EntityCondition.makeCondition(
							condList1, EntityOperator.AND);

					Personinfo = delegator.findList("CustomerAndpersonView",
							condition1, UtilMisc.toSet("partyId","firstName","middleName","lastName","phoneMobile","productStoreId","userLoginId","roleTypeId"), null, null, true);
					pObject.setData(Personinfo);
					pObject.setFlag(Constant.OK);
				}
			} catch (Exception e) {
				e.printStackTrace();
				pObject.setMsg(e.getMessage());
				pObject.setFlag(Constant.NG);
			}
			return pObject;
		}
		/*
		 * getPersonByPriceType 
		 */
		public static BasePosObject getPersonByPriceType(DispatchContext dctx,Map<String,Object>view){
			Delegator delegator = dctx.getDelegator();
			BasePosObject pObject = new BasePosObject ();
		    List<GenericValue> Personinfo = null;
		    EntityFindOptions findOptions =  new EntityFindOptions() ;	  
			 try{
				if (UtilValidate.isNotEmpty(view)){
					String productStoreId = (String)view.get("productStoreId");
					String priceTypeId = (String)view.get("productPriceTypeId");
					 List<EntityCondition> condList = FastList.newInstance();     
					 List<EntityCondition> condListtime = FastList.newInstance();
					 Timestamp fromDate = UtilDateTime.nowTimestamp();
						condListtime.add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, fromDate));
				        EntityCondition thruCond = EntityCondition.makeCondition(UtilMisc.toList(
				                EntityCondition.makeCondition("thruDate", null),
				                EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, fromDate)),
				                EntityOperator.OR);
				        condListtime.add(thruCond);
				        EntityCondition conditiontime = EntityCondition.makeCondition(condListtime);
				        
				        condList.add(conditiontime);
				     if(UtilValidate.isNotEmpty(productStoreId))
				    	 condList.add(EntityCondition.makeCondition("productStoreId", productStoreId));
				     if(UtilValidate.isNotEmpty(priceTypeId))
				    	 condList.add(EntityCondition.makeCondition("productPriceTypeId", priceTypeId));
					     
				        EntityCondition condition = EntityCondition.makeCondition(condList,EntityOperator.AND);
				        findOptions.setDistinct(true);
				       Personinfo = delegator.findList("PriceRuleUser", condition, UtilMisc.toSet("partyId","firstName","middleName","lastName","userLoginId"),null, findOptions, true);
				       pObject.setData(Personinfo);
				       pObject.setFlag(Constant.OK);
				}
				}catch (Exception e) {
					e.printStackTrace();
					pObject.setMsg(e.getMessage());
					pObject.setFlag(Constant.NG);
					}
			 return pObject;
		}

	
}