package org.ofbiz.party.webservice;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
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
import org.ofbiz.common.QueryCommonWorker;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;

public class PartyWebWork {
	/*
	 * 查询会员信息
	 */
	public static BasePosObject findPartyInfo(Map<String, Object> mapIn,DispatchContext dctx) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		// 方法参数
		EntityCondition whereEntityCondition = null;// 条件
		Set<String> fieldsToSelect = FastSet.newInstance();// 要查询的列
		List<EntityCondition> andExpre = FastList.newInstance();// 添加条件值
		List<String> orderby = FastList.newInstance();// 排序
		orderby.add("createdDate DESC");
		
		//根据年龄查询
//		List<Object> listDate = FastList.newInstance();
//		if (Constant.isNotNull(mapIn.get("startAge"))) {
//			Timestamp startDate = UtilDateTime.addDaysToTimestamp(sysDate, -Integer.parseInt(mapIn.get("startAge")+"")*365);
//			andExpre.add(EntityCondition.makeCondition("birthDate",EntityOperator.GREATER_THAN_EQUAL_TO,startDate));
//		}else if(Constant.isNotNull(mapIn.get("endAge"))){
//			Timestamp endDate = UtilDateTime.addDaysToTimestamp(sysDate, -Integer.parseInt(mapIn.get("endAge")+"")*365);
//			listDate.add(endDate);
//			listDate.add(sysDate);
//			andExpre.add(EntityCondition.makeCondition("birthDate",EntityOperator.BETWEEN, listDate));
//		}else if(Constant.isNotNull(mapIn.get("startAge")) && Constant.isNotNull(mapIn.get("endAge"))){
//			Timestamp startDate = UtilDateTime.addDaysToTimestamp(sysDate, -Integer.parseInt(mapIn.get("startAge")+"")*365);
//			Timestamp endDate = UtilDateTime.addDaysToTimestamp(sysDate, -Integer.parseInt(mapIn.get("endAge")+"")*365);
//			listDate.add(endDate);
//			listDate.add(startDate);
//			andExpre.add(EntityCondition.makeCondition("birthDate",EntityOperator.BETWEEN, listDate));
//		}
		if (Constant.isNotNull(mapIn.get("name"))) {
			andExpre.add(EntityCondition.makeCondition("firstName",EntityOperator.LIKE, "%" + mapIn.get("name") + "%" + ""));
		}
		if (Constant.isNotNull(mapIn.get("gender"))) {
			andExpre.add(EntityCondition.makeCondition("gender",EntityOperator.EQUALS, mapIn.get("gender") + ""));
		}
		if (Constant.isNotNull(mapIn.get("phoneMobile"))) {
			andExpre.add(EntityCondition.makeCondition("phoneMobile",EntityOperator.LIKE, "%" + mapIn.get("phoneMobile") + "%" + ""));
		}
//		if (Constant.isNotNull(mapIn.get("stateProvinceGeoId"))) {
//			andExpre.add(EntityCondition.makeCondition("stateProvinceGeoId",EntityOperator.LIKE, mapIn.get("stateProvinceGeoId") + ""));
//		}
//		if (Constant.isNotNull(mapIn.get("city"))) {
//			andExpre.add(EntityCondition.makeCondition("city",EntityOperator.LIKE, mapIn.get("city") + ""));
//		}
		if (Constant.isNotNull(mapIn.get("address1"))) {
			andExpre.add(EntityCondition.makeCondition("address1",EntityOperator.LIKE,"%" + mapIn.get("address1") + "%"));
		}
		if (Constant.isNotNull(mapIn.get("productStoreId"))) {
			andExpre.add(EntityCondition.makeCondition("productStoreId",EntityOperator.EQUALS, mapIn.get("productStoreId") ));
		}
		if (Constant.isNotNull(mapIn.get("cardId"))) {
			andExpre.add(EntityCondition.makeCondition("cardId",EntityOperator.LIKE, "%" + mapIn.get("cardId") + "%"));
		}
		//根据会员等级查询
		if (Constant.isNotNull(mapIn.get("partyClassificationGroupId"))) {
			andExpre.add(EntityCondition.makeCondition("partyClassificationGroupId",EntityOperator.EQUALS, mapIn.get("partyClassificationGroupId")));
		}
		//根据创建日期查询
		if(Constant.isNotNull(mapIn.get("startCreatedDate"))){
			andExpre.add(EntityCondition.makeConditionWhere("PT.CREATED_DATE >= '"+mapIn.get("startCreatedDate")+"'"));
		}
		if(Constant.isNotNull(mapIn.get("endCreatedDate"))){
			andExpre.add(EntityCondition.makeConditionWhere("PT.CREATED_DATE < '"+mapIn.get("endCreatedDate")+"'"));
		}
//		if (Constant.isNotNull(mapIn.get("startCreatedDate"))) {
//			andExpre.add(EntityCondition.makeCondition("createdDate",EntityOperator.GREATER_THAN_EQUAL_TO, ConvertUtil.convertStringToTimeStamp(mapIn.get("startCreatedDate")+"")));
//		}else if(Constant.isNotNull(mapIn.get("endCreatedDate"))) {
//			listCreatedDate.add(ConvertUtil.convertStringToTimeStamp(mapIn.get("endCreatedDate")+""));
//			listCreatedDate.add(sysDate);
//			andExpre.add(EntityCondition.makeCondition("createdDate",EntityOperator.BETWEEN, listCreatedDate));
//		}else if (Constant.isNotNull(mapIn.get("startCreatedDate")) && Constant.isNotNull(mapIn.get("endCreatedDate"))) {
//			listCreatedDate.add(ConvertUtil.convertStringToTimeStamp(mapIn.get("startCreatedDate")+""));
//			listCreatedDate.add(ConvertUtil.convertStringToTimeStamp(mapIn.get("endCreatedDate")+""));
//			andExpre.add(EntityCondition.makeCondition("createdDate",EntityOperator.BETWEEN, listCreatedDate));
//		}
		
		if (andExpre.size() > 0) {
			whereEntityCondition = EntityCondition.makeCondition(andExpre,EntityOperator.AND);
		}

		try {
			List<GenericValue> list = delegator.findList("PartyAndPersonAndContact",whereEntityCondition, fieldsToSelect, orderby, null, false);
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

	/*
	 * 添加会员信息
	 */
	public static BasePosObject createParty(DispatchContext dctx, Map<String,Object> customer,
			String username, String password) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> createCustomerMap = new HashMap<String, Object>();
		Map<String, Object> partyClassificationMap = new HashMap<String, Object>();

		Timestamp fromDate = UtilDateTime.nowTimestamp();
		String partyId =(String) getId(dctx, customer.get("productStoreId"));
		createCustomerMap.put("emailContactMechId", getId(dctx, customer.get("productStoreId")));
		createCustomerMap.put("partyId",partyId );
		createCustomerMap.put("firstName", customer.get("firstName"));
		createCustomerMap.put("lastName", customer.get("lastName"));
		createCustomerMap.put("emailAddress", customer.get("emailAddress"));
		createCustomerMap.put("integral", customer.get("integral"));
		createCustomerMap.put("gender", customer.get("gender"));
		createCustomerMap.put("phoneMobile", customer.get("phoneMobile"));
		if(Constant.isNotNull(customer.get("birthDate"))){
			createCustomerMap.put("birthDate",convertStringToDate(customer.get("birthDate")+""));
		}
		
		createCustomerMap.put("productStoreId",customer.get("productStoreId"));
		createCustomerMap.put("socialSecurityNumber",customer.get("socialSecurityNumber"));
		createCustomerMap.put("cardId", customer.get("cardId"));

		GenericValue productStoreRole = null;
		Map<String, Object> productStoreRoleMap = new HashMap<String, Object>();
		productStoreRoleMap.put("roleTypeId", "CUSTOMER");
		productStoreRoleMap.put("productStoreId", customer.get("productStoreId"));
		productStoreRoleMap.put("fromDate", fromDate);

		try { 
			Map<String, Object> cResult = dispatcher.runSync("quickCreateCustomer", createCustomerMap);
			partyId = (String) cResult.get("partyId");

			productStoreRoleMap.put("partyId", partyId);
			productStoreRole = delegator.makeValue("ProductStoreRole",productStoreRoleMap);
			productStoreRole.set("isSync", "1");
			productStoreRole.create();
			
			partyClassificationMap.put("productStoreId",customer.get("productStoreId"));
			partyClassificationMap.put("partyId", partyId);
			partyClassificationMap.put("partyClassificationGroupId",customer.get("partyClassificationGroupId"));
			partyClassificationMap.put("fromDate", fromDate);
			partyClassificationMap.put("login.username", username);
			partyClassificationMap.put("login.password", password);
			dispatcher.runSync("createPartyClassification",partyClassificationMap); // 分类
			// 添加通信方法
			if (Constant.isNotNull(customer.get("phoneNumber"))) {
				addTelecomNumber(dctx,customer.get("productStoreId"), partyId,customer.get("phoneNumber"), "PHONE_MOBILE", username, password);
			}
			if (Constant.isNotNull(customer.get("phoneHome"))) {
				addTelecomNumber(dctx,customer.get("productStoreId"), partyId, customer.get("phoneHome"),"PHONE_HOME", username, password);
			}
			if (Constant.isNotNull(customer.get("phoneWork"))) {
				addTelecomNumber(dctx,customer.get("productStoreId"), partyId, customer.get("phoneWork"),"PHONE_WORK", username, password);
			}
			if (Constant.isNotNull(customer.get("faxNumber"))) {
				addTelecomNumber(dctx,customer.get("productStoreId"), partyId, customer.get("faxNumber"),"FAX_NUMBER", username, password);
			}
			
			// 添加postalAddress
			if (Constant.isNotNull(customer.get("address1"))) {
				addPostalAddress(dctx,customer.get("productStoreId"), partyId, customer.get("address1"),customer.get("SPGId"), customer.get("city"),customer.get("postalCode"), "PRIMARY_LOCATION", username, password);
			}
			if (Constant.isNotNull(customer.get("baddress1"))) {
				addPostalAddress(dctx,customer.get("productStoreId"), partyId, customer.get("baddress1"),customer.get("BSPGId"), customer.get("bcity"),customer.get("bpostalCode"), "BILLING_LOCATION"
						, username, password);
			}
			if (Constant.isNotNull(customer.get("saddress1"))) {
				addPostalAddress(dctx,customer.get("productStoreId"), partyId, customer.get("saddress1"),customer.get("SSPGId"), customer.get("scity"),customer.get("spostalCode"), "SHIPPING_LOCATION"
						, username, password);
			}
			pObject.setFlag(Constant.OK);
			pObject.setMsg("添加会员成功");
		} catch (Exception e1) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg("添加会员失败");
			return pObject;
		}
		return pObject;
	}

	/*
	 * 添加通信
	 */
	public static void addTelecomNumber(DispatchContext dctx,Object productStoreId,Object partyId, Object phone, String phoneType,
			String username, String password)throws Exception{
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> partyTelecomNumberMap = new HashMap<String, Object>();
		Map<String, Object> partyContactMechPurposeMap = new HashMap<String, Object>();
		partyTelecomNumberMap.put("contactMechId", getId(dctx, productStoreId));
		partyTelecomNumberMap.put("productStoreId", productStoreId);
		partyTelecomNumberMap.put("partyId", partyId);
		partyTelecomNumberMap.put("countryCode", "");
		partyTelecomNumberMap.put("areaCode", "");
		partyTelecomNumberMap.put("contactNumber", phone);
		partyTelecomNumberMap.put("extension", "");
		partyTelecomNumberMap.put("allowSolicitation", "");
		partyTelecomNumberMap.put("login.username", username);
		partyTelecomNumberMap.put("login.password", password);
		// 电话
		Map<String,Object> map = dispatcher.runSync("createPartyTelecomNumber", partyTelecomNumberMap);
		partyContactMechPurposeMap.put("productStoreId", productStoreId);
		partyContactMechPurposeMap.put("partyId", partyId);
		partyContactMechPurposeMap.put("contactMechId", map.get("contactMechId"));
		partyContactMechPurposeMap.put("contactMechPurposeTypeId", phoneType); // 电话类型
		partyContactMechPurposeMap.put("login.username", username);
		partyContactMechPurposeMap.put("login.password", password);
		dispatcher.runSync("createPartyContactMechPurpose", partyContactMechPurposeMap);
		
	}

	/*
	 * 添加通信地址
	 */
	public static void addPostalAddress(DispatchContext dctx,Object productStoreId,Object partyId, Object address, Object SPGId, Object city,Object postalCode, String postalType,
			String username, String password)throws Exception {
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> partyContactMechPurposeMap1 = new HashMap<String, Object>();
		Map<String, Object> partyPostalAddressMap = new HashMap<String, Object>();
		partyPostalAddressMap.put("contactMechId", getId(dctx,productStoreId));
		partyPostalAddressMap.put("productStoreId", productStoreId);
		partyPostalAddressMap.put("partyId", partyId);
		partyPostalAddressMap.put("toName", "");
		partyPostalAddressMap.put("address1", address); 
		partyPostalAddressMap.put("address2", "");
		partyPostalAddressMap.put("city", city);
		partyPostalAddressMap.put("stateProvinceGeoId", SPGId); // 城市
		partyPostalAddressMap.put("postalCode", postalCode);
		partyPostalAddressMap.put("countryGeoId", "CHN"); // 国家
		partyPostalAddressMap.put("login.username", username);
		partyPostalAddressMap.put("login.password", password);
		Map<String, Object> ppaResult = dispatcher.runSync("createPartyPostalAddress", partyPostalAddressMap); // 邮政地址
		String ppaContactMechId = (String) ppaResult.get("contactMechId");

		partyContactMechPurposeMap1.put("productStoreId", productStoreId);
		partyContactMechPurposeMap1.put("partyId", partyId); 
		partyContactMechPurposeMap1.put("contactMechId", ppaContactMechId);
		partyContactMechPurposeMap1.put("contactMechPurposeTypeId", postalType); // 通信地址
		partyContactMechPurposeMap1.put("login.username", username);
		partyContactMechPurposeMap1.put("login.password", password);
		dispatcher.runSync("createPartyContactMechPurpose", partyContactMechPurposeMap1);
	}

	/*
	 * 日期转换
	 */
	public static Date convertStringToDate(String dateStr) {
		Date date = null;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		format.setLenient(false);
		// 要转换字符串
		try {
			date = new Date(format.parse(dateStr).getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/*
	 * 修改会员信息
	 */
	public static BasePosObject updateParty(DispatchContext dctx,Map<String, ? extends Object> customer,
			String username, String password) {
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Map<String, Object> contactMechMap = FastMap.newInstance();
		Map<String, Object> partyPostalAddressMap = new HashMap<String, Object>();

		Timestamp fromDate = UtilDateTime.nowTimestamp();

		// String partyId = jsonObj.get("partyId").toString(); // partyId;
		try {
			Map<String, Object> productStoreRoleMap = new HashMap<String, Object>();
			productStoreRoleMap.put("partyId", customer.get("partyId"));
			productStoreRoleMap.put("roleTypeId", "CUSTOMER");
			productStoreRoleMap.put("productStoreId", customer.get("productStoreId"));
			productStoreRoleMap.put("fromDate", fromDate);
			//店面权限
			/*List<GenericValue> productStoreRoles = delegator.findByAnd("ProductStoreRole",UtilMisc.toMap("partyId", customer.get("partyId")));
			if (UtilValidate.isEmpty(productStoreRoles)) {
				GenericValue productStoreRole = delegator.makeValue("ProductStoreRole",productStoreRoleMap);
				productStoreRole.create();
			} else {
				for (GenericValue gv : productStoreRoles) {
					gv.remove();
					gv = delegator.makeValue("ProductStoreRole",productStoreRoleMap);
					gv.create();
				}
			}*/
			//修改人员信息
			Map<String, Object> personMap = FastMap.newInstance();
			personMap.put("partyId", customer.get("partyId"));
			personMap.put("firstName", customer.get("firstName"));
			personMap.put("lastName", "");
			personMap.put("gender", customer.get("gender"));
			personMap.put("integral", customer.get("integral"));
			personMap.put("phoneMobile", customer.get("phoneMobile"));
			if(Constant.isNotNull(customer.get("birthDate"))){
				personMap.put("birthDate", convertStringToDate(customer.get("birthDate").toString()));
			}
			personMap.put("cardId", customer.get("cardId"));
			personMap.put("socialSecurityNumber",customer.get("socialSecurityNumber"));
			personMap.put("login.username", username);
			personMap.put("login.password", password);
			dispatcher.runSync("updatePerson", personMap);
			//修改会员等级
			Map<String, Object> partyClassificationMap = new HashMap<String, Object>();
			partyClassificationMap.put("partyId", customer.get("partyId"));
			partyClassificationMap.put("partyClassificationGroupId",customer.get("partyClassificationGroupId"));
			partyClassificationMap.put("fromDate", fromDate);
			EntityConditionList<EntityExpr> ecl = EntityCondition.makeCondition(EntityCondition.makeCondition("partyId",EntityOperator.EQUALS,customer.get("partyId")));
			List<GenericValue> c = delegator.findList("PartyClassification",ecl, null, UtilMisc.toList("partyId", "fromDate"), null,false);
			if (c != null) {
				for (GenericValue pcf : c) {
					pcf.remove();
					GenericValue partyClassification = delegator.makeValue("PartyClassification", partyClassificationMap);
					partyClassification.create();
				}
			}
			//修改联系方式
			List<GenericValue> partyContactMechPurposesList = delegator.findByAnd("PartyContactMechPurpose",UtilMisc.toMap("partyId", (String)customer.get("partyId")));
			String cmptId =null;
			String contactMechId =null;
			String phContactMechId =null;
			String pmContactMechId =null;
			String faxContactMechId =null;
			String plContactMechId =null;
			for (GenericValue partyContactMechPurposes : partyContactMechPurposesList) {
				cmptId = (String) partyContactMechPurposes.get("contactMechPurposeTypeId");
				if (cmptId.equals("PRIMARY_EMAIL")) {// 邮箱
					contactMechId = (String) partyContactMechPurposes.get("contactMechId");
				}else if (cmptId.equals("PHONE_HOME")) {// 家庭电话
					phContactMechId = (String) partyContactMechPurposes.get("contactMechId");
				}else if (cmptId.equals("PHONE_WORK")) {// 工作电话
					pmContactMechId = (String) partyContactMechPurposes.get("contactMechId");
				}else if (cmptId.equals("FAX_NUMBER")) {// 传真
					faxContactMechId = (String) partyContactMechPurposes.get("contactMechId");
				}else if (cmptId.equals("PRIMARY_LOCATION")) {// 详细地址
					plContactMechId = (String) partyContactMechPurposes.get("contactMechId");
				}
			}
			// 邮箱
			if(Constant.isNotNull(contactMechId)){
				contactMechMap.put("contactMechId", contactMechId);
				contactMechMap.put("contactMechTypeId", "EMAIL_ADDRESS");
				contactMechMap.put("infoString", customer.get("emailAddress"));
				GenericValue contactMech = delegator.makeValue("ContactMech", contactMechMap);
				contactMech.store();
			}else {
				if (UtilValidate.isNotEmpty(customer.get("emailAddress"))) {
					contactMechMap.put("contactMechId", getId(dctx, customer.get("productStoreId")));
					contactMechMap.put("contactMechTypeId", "EMAIL_ADDRESS");
					contactMechMap.put("infoString", customer.get("emailAddress"));
					contactMechMap.put("isSync", "1");
					GenericValue contactMech = delegator.makeValue("ContactMech", contactMechMap);
					contactMech.create();
					
					GenericValue partyContactMech = delegator.makeValue("PartyContactMech", 
							UtilMisc.toMap("partyId", customer.get("partyId"),
									"contactMechId", contactMech.get("contactMechId"),
									"fromDate", UtilDateTime.nowTimestamp(),
									"isSync", "1"));
					partyContactMech.create();
					
					GenericValue partyContactMechPurpose = delegator.makeValue("PartyContactMechPurpose", 
							UtilMisc.toMap("partyId", customer.get("partyId"),
									"contactMechId", contactMech.get("contactMechId"),
									"contactMechPurposeTypeId", "PRIMARY_EMAIL",
									"fromDate", UtilDateTime.nowTimestamp(),
									"isSync", "1"));
					partyContactMechPurpose.create();
				}
			}
			
			// 家庭电话
			if(Constant.isNull(phContactMechId) && Constant.isNotNull(customer.get("phoneHome"))){
				addTelecomNumber(dctx,customer.get("productStoreId"), customer.get("partyId"),customer.get("phoneHome"), "PHONE_HOME",
						username, password);
			}else if(Constant.isNotNull(phContactMechId) && Constant.isNotNull(customer.get("phoneHome"))){
				storeTelecomNumber(delegator, phContactMechId,customer.get("phoneHome"));
			}
			// 工作电话
			if(Constant.isNull(pmContactMechId) && Constant.isNotNull(customer.get("phoneWork"))){
				addTelecomNumber(dctx,customer.get("productStoreId"), customer.get("partyId"),customer.get("phoneWork"), "PHONE_WORK",
						username, password);
			}else if(Constant.isNotNull(pmContactMechId) && Constant.isNotNull(customer.get("phoneWork"))){
				storeTelecomNumber(delegator, pmContactMechId,customer.get("phoneWork"));
			}
			// 传真
			if(Constant.isNull(faxContactMechId) && Constant.isNotNull(customer.get("faxNumber"))){
				addTelecomNumber(dctx,customer.get("productStoreId"), customer.get("partyId"),customer.get("faxNumber"), "FAX_NUMBER",
						username, password);
			}else if(Constant.isNotNull(faxContactMechId) && Constant.isNotNull(customer.get("faxNumber"))){
				storeTelecomNumber(delegator, faxContactMechId,customer.get("faxNumber"));
			}
			// 详细地址
			if(Constant.isNull(plContactMechId)){
				if (UtilValidate.isNotEmpty(customer.get("address1"))) {
					addPostalAddress(dctx,customer.get("productStoreId") ,customer.get("partyId") , customer.get("address1") , customer.get("SPGId"), customer.get("city"),customer.get("postalCode"), "PRIMARY_LOCATION",
							username, password);
				}
			}else if(Constant.isNotNull(plContactMechId) && Constant.isNotNull(customer.get("address1"))){
				partyPostalAddressMap.put("contactMechId", plContactMechId);
				partyPostalAddressMap.put("toName", customer.get("firstName"));
				partyPostalAddressMap.put("address1", customer.get("address1"));
				partyPostalAddressMap.put("address2", "");
				partyPostalAddressMap.put("city", customer.get("city"));
				partyPostalAddressMap.put("stateProvinceGeoId", customer.get("SPGId")); // 城市
				partyPostalAddressMap.put("countryGeoId", "CHN"); // 国家
				GenericValue postalAddress = delegator.makeValue("PostalAddress",partyPostalAddressMap);
				postalAddress.store();
			}
			pObject.setFlag(Constant.OK);
			pObject.setMsg("修改会员成功");
		} catch (Exception e1) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg("修改会员失败");
			e1.printStackTrace();
		}
		return pObject;
	}

	/*
	 * 修改通信方法
	 */
	public static void storeTelecomNumber(Delegator delegator,Object contactMechId, Object phoneNumber) {
		Map<String, Object> telecomNumberMap = new HashMap<String, Object>();
		if (UtilValidate.isNotEmpty(phoneNumber)) {
			telecomNumberMap.put("contactMechId", contactMechId);
			telecomNumberMap.put("countryCode", "011");
			telecomNumberMap.put("areaCode", "011");
			telecomNumberMap.put("contactNumber", phoneNumber);

			GenericValue gv = null;
			try {
				gv = delegator.makeValue("TelecomNumber", telecomNumberMap);
				gv.store();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/*
	 * 获取id
	 */
	public static Object getId(DispatchContext dctx,Object productStoreId){
		Map<String,Object> mapIds = FastMap.newInstance();
		mapIds.clear();
		mapIds.put("type", "V");
		mapIds.put("storeId",productStoreId);
		BasePosObject idObject =QueryCommonWorker.getOrderId(dctx,mapIds);
		String contactMechId = (String) idObject.getData();
		String strId =contactMechId.substring(contactMechId.length()-8, contactMechId.length());
		String contactMech1=Integer.toHexString(Integer.parseInt(strId));
		contactMech1 =contactMechId.substring(0, contactMechId.length()-8)+contactMech1;
		return contactMech1;
	}
	/*
	 * 获取会员等级
	 */
	public static BasePosObject findPartyPc(DispatchContext dctx){
		Delegator delegator = dctx.getDelegator();
		BasePosObject pObject = new BasePosObject();
		try {
			List<GenericValue> list=delegator.findList("PartyClassificationGroup", null, null, null, null, false);
			pObject.setFlag(Constant.OK);
			pObject.setData(list);
		} catch (GenericEntityException e) {
			pObject.setFlag(Constant.NG);
			pObject.setMsg(e.getMessage());
			e.printStackTrace();
		}
		return pObject;
	}
}
