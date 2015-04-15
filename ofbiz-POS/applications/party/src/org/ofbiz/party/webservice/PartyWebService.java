package org.ofbiz.party.webservice;


import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.util.JSONUtil;

public class PartyWebService {
	
	public static final String module = PartyWebService.class.getName();
    public static final String resource = "PartyUiLabels";
    public static final String resourceError = "PartyErrorUiLabels";

    
	public static Map<String,Object> addCustomer(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String,Object> result = new HashMap<String,Object>();
        Map<String,Object> createCustomerMap = new HashMap<String,Object>();
        Map<String,Object> partyClassificationMap = new HashMap<String,Object>();
        
        String jsonStr = (String)context.get("jsonStr");
        JSONObject jsonObj =  JSONObject.fromObject(jsonStr);
        Customer customer=null;
 
        customer = (Customer)JSONObject.toBean(jsonObj,Customer.class);

        Timestamp fromDate = UtilDateTime.nowTimestamp();
        String partyId = "";
        
        createCustomerMap.put("firstName", customer.getName().substring(1));
        createCustomerMap.put("lastName", customer.getName().substring(0, 1));
        createCustomerMap.put("emailAddress", customer.getEmailAddress());
        createCustomerMap.put("integral", customer.getIntegral());
        createCustomerMap.put("gender", customer.getSex());
        createCustomerMap.put("phoneMobile", customer.getPhoneNumber());
        // createCustomerMap.put("personalTitle", customer.getSex());
        createCustomerMap.put("birthDate", convertStringToDate(customer.getBirthDate()));
        createCustomerMap.put("socialSecurityNumber", customer.getSocialSecurityNumber());
        createCustomerMap.put("cardId", customer.getCardId());
        // createCustomerMap.put("firstName", store);
        // createCustomerMap.put("createDate", createDate);

        GenericValue productStoreRole = null;
        Map<String,Object> productStoreRoleMap = new HashMap<String,Object>();
        productStoreRoleMap.put("roleTypeId", "CUSTOMER");
        productStoreRoleMap.put("productStoreId", customer.getStore());
        productStoreRoleMap.put("fromDate", fromDate);
        
        try {
        	Map<String, Object> cResult = dispatcher.runSync("quickCreateCustomer", createCustomerMap);
        	partyId = (String)cResult.get("partyId");
        	
        	productStoreRoleMap.put("partyId", partyId);
        	productStoreRole = delegator.makeValue("ProductStoreRole", productStoreRoleMap);
        	productStoreRole.create();
        	
        	partyClassificationMap.put("partyId", partyId);
        	partyClassificationMap.put("partyClassificationGroupId", customer.getPartyClassificationGroupId());
        	partyClassificationMap.put("fromDate", fromDate);
        	partyClassificationMap.put("login.username", "admin");
        	partyClassificationMap.put("login.password", "ofbiz");
        	dispatcher.runSync("createPartyClassification", partyClassificationMap);	// 分类
        	
        	//添加通信方法
        	if(UtilValidate.isNotEmpty(customer.getPhoneNumber())) {
        		addTelecomNumber(dispatcher, partyId, customer.getPhoneNumber(), "PHONE_MOBILE");
        	}
			if(UtilValidate.isNotEmpty(customer.getPhoneHome())) {
				addTelecomNumber(dispatcher, partyId, customer.getPhoneHome(), "PHONE_HOME");
			}
			if(UtilValidate.isNotEmpty(customer.getPhoneWork())) {
				addTelecomNumber(dispatcher, partyId, customer.getPhoneWork(), "PHONE_WORK");
			}
			if(UtilValidate.isNotEmpty(customer.getFaxNumber())) {
				addTelecomNumber(dispatcher, partyId, customer.getFaxNumber(), "FAX_NUMBER");
			}
			
			//添加postalAddress
        	if(UtilValidate.isNotEmpty(customer.getAddress1())) {
        		addPostalAddress(dispatcher, partyId, customer.getAddress1(), customer.getSPGId(), customer.getCity(), customer.getPostalCode(), "PRIMARY_LOCATION");
        	}
			if(UtilValidate.isNotEmpty(customer.getBaddress1())) {
				addPostalAddress(dispatcher, partyId, customer.getBaddress1(), customer.getBSPGId(), customer.getBcity(), customer.getBpostalCode(), "BILLING_LOCATION");
			}
			if(UtilValidate.isNotEmpty(customer.getSaddress1())) {
				addPostalAddress(dispatcher, partyId, customer.getSaddress1(), customer.getSSPGId(), customer.getScity(), customer.getSpostalCode(), "SHIPPING_LOCATION");
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		result.put("partyId", partyId);
		return result;
    } 
	
	public static void addTelecomNumber(LocalDispatcher dispatcher, String partyId, String phone, String phoneType ) throws GenericServiceException {
		
		Map<String,Object> partyTelecomNumberMap = new HashMap<String,Object>();
        Map<String,Object> partyContactMechPurposeMap = new HashMap<String,Object>();
        
		partyTelecomNumberMap.put("partyId", partyId);
    	partyTelecomNumberMap.put("countryCode", "");
    	partyTelecomNumberMap.put("areaCode", "");
    	partyTelecomNumberMap.put("contactNumber", phone);
    	partyTelecomNumberMap.put("extension", "");
    	partyTelecomNumberMap.put("allowSolicitation", "");
    	partyTelecomNumberMap.put("login.username", "admin");
    	partyTelecomNumberMap.put("login.password", "ofbiz");
    	// 电话
    	Map<String, Object> ptnResult = dispatcher.runSync("createPartyTelecomNumber", partyTelecomNumberMap);
    	String contactMechId = (String)ptnResult.get("contactMechId");
    	
    	partyContactMechPurposeMap.put("partyId", partyId);
    	partyContactMechPurposeMap.put("contactMechId", contactMechId);
    	partyContactMechPurposeMap.put("contactMechPurposeTypeId", phoneType);	// 电话类型
    	partyContactMechPurposeMap.put("login.username", "admin");
    	partyContactMechPurposeMap.put("login.password", "ofbiz");
    	dispatcher.runSync("createPartyContactMechPurpose", partyContactMechPurposeMap);
	}
	
	public static void addPostalAddress(LocalDispatcher dispatcher, String partyId, String address, String SPGId, String city, 
										String postalCode, String postalType ) throws GenericServiceException {
		Map<String,Object> partyContactMechPurposeMap1 = new HashMap<String,Object>();
        Map<String,Object> partyPostalAddressMap = new HashMap<String,Object>();
		
		partyPostalAddressMap.put("partyId", partyId);
    	// partyPostalAddressMap.put("contactMechId", contactMechId);
    	partyPostalAddressMap.put("toName", "");
    	partyPostalAddressMap.put("address1", address);
    	partyPostalAddressMap.put("address2", "");
    	partyPostalAddressMap.put("city", city);
    	partyPostalAddressMap.put("stateProvinceGeoId", SPGId);	// 城市
    	partyPostalAddressMap.put("postalCode", postalCode);
    	partyPostalAddressMap.put("countryGeoId", "CHN");	// 国家
    	partyPostalAddressMap.put("login.username", "admin");
    	partyPostalAddressMap.put("login.password", "ofbiz");
    	Map<String, Object> ppaResult = dispatcher.runSync("createPartyPostalAddress", partyPostalAddressMap);	// 邮政地址
    	String ppaContactMechId = (String)ppaResult.get("contactMechId");
    	
    	partyContactMechPurposeMap1.put("partyId", partyId);
    	partyContactMechPurposeMap1.put("contactMechId", ppaContactMechId);
    	partyContactMechPurposeMap1.put("contactMechPurposeTypeId", postalType);	// 通信地址
    	partyContactMechPurposeMap1.put("login.username", "admin");
    	partyContactMechPurposeMap1.put("login.password", "ofbiz");
    	dispatcher.runSync("createPartyContactMechPurpose", partyContactMechPurposeMap1);
	}
	
	public static Map<String, Object> selectParty(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String,Object> resultJson = FastMap.newInstance();
        
        String personName = "";
    	String phoneMobile = "";
    	String cardId = "";
    	String store = "";
    	String sex = "";
    	String partyClassificationGroupId = "";
    	String SPGId = "";
    	String city = "";
    	
    	String date1 = "";
    	String date2 = "";
    	
    	Customer customer=null;
        String jsonStr = (String)context.get("jsonStr");
        if (UtilValidate.isNotEmpty(jsonStr)) {
        	JSONObject jsonObj = JSONObject.fromObject(jsonStr);
            customer = (Customer)JSONObject.toBean(jsonObj,Customer.class);
        	personName = customer.getName();	// jsonObj.get("name").toString();
        	phoneMobile = customer.getPhoneMobile();	// jsonObj.get("phoneMobile").toString();
        	cardId = customer.getCardId();	// jsonObj.get("cardId").toString();
        	store = customer.getStore();	// jsonObj.get("store").toString();
        	sex = customer.getSex();	// jsonObj.get("sex").toString();
        	partyClassificationGroupId = customer.getPartyClassificationGroupId();	// jsonObj.get("partyClassificationGroupId").toString();
        	SPGId = customer.getSPGId();  // jsonObj.get("SPGId").toString();
        	city = customer.getCity();	 // jsonObj.get("city").toString();
        	date1 = customer.getDate1();	 // jsonObj.get("date1").toString();
        	date2 = customer.getDate2();	 // jsonObj.get("date2").toString();
		}
        
      //-----------------------------------------------
       // List<String> partyIds = getPartyIds(delegator, phoneMobile, city, SPGId, partyClassificationGroupId);
        List<GenericValue> partyList = null;
        DynamicViewEntity dynamicView = new DynamicViewEntity();

        // define the main condition & expression list
        List<EntityCondition> andExprs = FastList.newInstance();
        EntityCondition mainCond = null;

        List<String> orderBy = FastList.newInstance();
        List<String> fieldsToSelect = FastList.newInstance();
        // default view settings
        
        dynamicView.addMemberEntity("PE", "Person");
        dynamicView.addAlias("PE", "partyId");
        dynamicView.addAlias("PE", "lastName");
        dynamicView.addAlias("PE", "firstName");
        dynamicView.addAlias("PE", "cardId");
        dynamicView.addAlias("PE", "integral");
        dynamicView.addAlias("PE", "createdStamp");
        dynamicView.addAlias("PE", "birthDate");
        dynamicView.addAlias("PE", "gender");
        dynamicView.addAlias("PE", "personalTitle");
        dynamicView.addAlias("PE", "socialSecurityNumber");
        // dynamicView.addViewLink("PT", "PE", Boolean.FALSE, ModelKeyMap.makeKeyMapList("partyId"));
        dynamicView.addMemberEntity("PCF", "PartyClassification");
        dynamicView.addMemberEntity("PCG", "PartyClassificationGroup");
        
        dynamicView.addAlias("PCF", "partyClassificationGroupId");
        dynamicView.addAlias("PCG", "description");
        // 一对一关系 内，左外 关联都可行 true：左外关联；false：内关联
        // 一对多；先true,后false
        dynamicView.addViewLink("PE", "PCF", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
        dynamicView.addViewLink("PCF", "PCG", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyClassificationGroupId"));

        fieldsToSelect.add("partyId");
        fieldsToSelect.add("integral");
        fieldsToSelect.add("cardId");
        fieldsToSelect.add("firstName");
        fieldsToSelect.add("lastName");
        fieldsToSelect.add("gender");
        fieldsToSelect.add("personalTitle");
        fieldsToSelect.add("socialSecurityNumber");
        fieldsToSelect.add("createdStamp");
        fieldsToSelect.add("birthDate");
        fieldsToSelect.add("description");
        
        orderBy.add("lastName");
        if(!UtilValidate.isNotEmpty(jsonStr) && !"".equals(jsonStr)){
        	andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+personName+"%")));
        	if(!sex.equals("")){
        		andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("gender"), EntityOperator.EQUALS, EntityFunction.UPPER(sex)));
        	}
        	if(!cardId.equals("")){
        		andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("cardId"), EntityOperator.EQUALS, EntityFunction.UPPER(cardId)));
        	}
        }
        
        dynamicView.addMemberEntity("PCMP", "PartyContactMechPurpose");
        dynamicView.addMemberEntity("PSR", "ProductStoreRole");
        dynamicView.addMemberEntity("PS", "ProductStore");
        dynamicView.addMemberEntity("TN", "TelecomNumber");
        dynamicView.addAlias("PSR", "productStoreId");
        dynamicView.addAlias("PS", "storeName");
        dynamicView.addAlias("TN", "contactNumber", "contactNumber", null, null, null, null);
        dynamicView.addAlias("PCMP", "contactMechId");
        dynamicView.addAlias("PCMP", "contactMechPurposeTypeId", "contactMechPurposeTypeId", null, null, null, null);
        dynamicView.addViewLink("PE", "PCMP", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
        dynamicView.addViewLink("PCMP", "TN", Boolean.TRUE, ModelKeyMap.makeKeyMapList("contactMechId"));
        dynamicView.addViewLink("PCMP", "PSR", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
        dynamicView.addViewLink("PSR", "PS", Boolean.TRUE, ModelKeyMap.makeKeyMapList("productStoreId"));
        fieldsToSelect.add("contactMechId");
        fieldsToSelect.add("contactNumber");
        fieldsToSelect.add("storeName");
        fieldsToSelect.add("productStoreId");
        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("contactMechPurposeTypeId"), EntityOperator.EQUALS, EntityFunction.UPPER("PHONE_MOBILE")));
        
        dynamicView.addMemberEntity("PCMP1", "PartyContactMechPurpose");
        dynamicView.addMemberEntity("TN1", "TelecomNumber");
        dynamicView.addAlias("TN1", "contactNumber1", "contactNumber", null, null, null, null);
        dynamicView.addAlias("PCMP1", "contactMechPurposeTypeId1", "contactMechPurposeTypeId", null, null, null, null);
        dynamicView.addViewLink("PE", "PCMP1", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
        dynamicView.addViewLink("PCMP1", "TN1", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
        fieldsToSelect.add("contactNumber1");
        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("contactMechPurposeTypeId1"), EntityOperator.EQUALS, EntityFunction.UPPER("PHONE_HOME")));
        
        dynamicView.addMemberEntity("PCMP2", "PartyContactMechPurpose");
        dynamicView.addMemberEntity("PA", "PostalAddress");
        dynamicView.addAlias("PA", "stateProvinceGeoId", "stateProvinceGeoId", null, null, null, null);
        dynamicView.addAlias("PA", "city", "city", null, null, null, null);
        dynamicView.addAlias("PA", "address1", "address1", null, null, null, null);
        dynamicView.addAlias("PCMP2", "contactMechPurposeTypeId2", "contactMechPurposeTypeId", null, null, null, null);
        dynamicView.addViewLink("PE", "PCMP2", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
        dynamicView.addViewLink("PCMP2", "PA", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
        fieldsToSelect.add("stateProvinceGeoId");
        fieldsToSelect.add("city");
        fieldsToSelect.add("address1");
        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("contactMechPurposeTypeId2"), EntityOperator.EQUALS, EntityFunction.UPPER("PRIMARY_LOCATION")));
        
        dynamicView.addMemberEntity("PCMP3", "PartyContactMechPurpose");
        dynamicView.addMemberEntity("CM", "ContactMech");
        dynamicView.addAlias("CM", "infoString");
        dynamicView.addAlias("PCMP3", "contactMechPurposeTypeId3", "contactMechPurposeTypeId", null, null, null, null);
        dynamicView.addViewLink("PE", "PCMP3", Boolean.TRUE, ModelKeyMap.makeKeyMapList("partyId"));
        dynamicView.addViewLink("PCMP3", "CM", Boolean.FALSE, ModelKeyMap.makeKeyMapList("contactMechId"));
        fieldsToSelect.add("infoString");
        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("contactMechPurposeTypeId3"), EntityOperator.EQUALS, EntityFunction.UPPER("PRIMARY_EMAIL")));

        if(!UtilValidate.isNotEmpty(jsonStr) && !"".equals(jsonStr)){
        	if(!phoneMobile.equals("")){
        		andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("contactNumber"), EntityOperator.LIKE, EntityFunction.UPPER("%"+phoneMobile+"%")));
        	}
        	if(!partyClassificationGroupId.equals("")){
        		andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyClassificationGroupId"), EntityOperator.EQUALS, EntityFunction.UPPER(partyClassificationGroupId)));
        	}
        	if(!SPGId.equals("")){
        		andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("stateProvinceGeoId"), EntityOperator.EQUALS, EntityFunction.UPPER(SPGId)));
        	}
        	if(!city.equals("")){
        		andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("city"), EntityOperator.EQUALS, EntityFunction.UPPER(city)));
        	}
        	
        	List<Object> listDate = FastList.newInstance();
        	listDate.add(convertStringToTimeStamp(date1));
        	Timestamp ts = new Timestamp(new java.util.Date().getTime());
        	if(!date2.equals("")){
        		ts = convertStringToTimeStamp(date2);
        	}
        	listDate.add(ts);
        	andExprs.add(EntityCondition.makeCondition("createdStamp", EntityOperator.BETWEEN, listDate));
        }
        
        if (andExprs.size() > 0) mainCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
        // set distinct on so we only get one row per order
        EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, -1, true);
        // using list iterator
        EntityListIterator pli;
		try {
			pli = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, findOpts);
			// partyList = pli.getPartialList(1, -1);
			partyList = pli.getCompleteList();
			pli.close();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Object> list = FastList.newInstance();
		
		for (GenericValue pl : partyList) {
			
			list.add(UtilMisc.toMap("partyId", (String)pl.get("partyId"), 
					"contactMechId", pl.get("contactMechId"),
					"contactMechPurposeTypeId", pl.get("contactMechPurposeTypeId"),
                    "name", (String)pl.get("lastName") + (pl.get("firstName") == null ? "" : pl.get("firstName")),
                    "sex", pl.get("gender"),
                    "age", getAge(pl.get("birthDate")),
                    "birth", pl.get("birthDate")+"",
                    "phoneMobile", pl.get("contactNumber"),
                    "identityCard", pl.get("socialSecurityNumber"),
                    "store", pl.get("storeName"), 	// 会员详细
                    "ingetral", pl.get("integral"),	
                    "cardId", pl.get("cardId"), 
                    "totelIngetral", "",
                    "classification", pl.get("description"), 
                    "createDate", pl.get("createdStamp")+"",
                    "phoneHome", pl.get("contactNumber1"), 	// 联系方式
                    "emailAddress", pl.get("infoString"),
                    "SPGId",  getSPG(delegator, pl.get("stateProvinceGeoId")),	// 	联系地址
                    "city", pl.get("city"),
                    "address1", pl.get("address1")
                    ));
		} 
		String strResult = "" ;
		try {
			strResult = JSONUtil.listToJson(list);
			// strResult = JSONUtil.listToJson(jsonStr(list));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		String s1 = "";
		try {
			XMLSerializer serializer = new XMLSerializer();  
			JSON jsonObject = JSONSerializer.toJSON(strResult); 
			s1 = serializer.write(jsonObject);
			//s1 = serializer.write(JSONObject.fromObject(strResult));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
        
        resultJson.put("jsonOut", s1);
		return resultJson;
	}
	

	
	
	public static List<String> getPartyIds(Delegator delegator, String telNumber, String city, String stateProvinceGeoId, String partyClassificationGroupId ) {
		List<EntityCondition> andExprs = FastList.newInstance();
        EntityCondition mainCond = null;
        List<String> orderBy = FastList.newInstance();
        List<String> fieldsToSelect = FastList.newInstance();
		List<GenericValue> partyList = null;
        DynamicViewEntity dynamicView = new DynamicViewEntity();
        List<String> partyClassificationPartyIds = FastList.newInstance();
        Map<String, String> partyClassificationMap = FastMap.newInstance();
        
        if(!partyClassificationGroupId.equals("")){
        	partyClassificationMap.put("partyClassificationGroupId", partyClassificationGroupId);
        }
        
        try {
        	partyList = delegator.findByAnd("PartyClassification", partyClassificationMap);
        	for (GenericValue gv : partyList) {
        		if(!partyClassificationPartyIds.contains(gv.get("partyId"))){
        			partyClassificationPartyIds.add((String)gv.get("partyId"));
        		}
			}
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
		dynamicView.addMemberEntity("TN", "TelecomNumber");
        dynamicView.addMemberEntity("PCMP", "PartyContactMechPurpose");
        dynamicView.addAlias("PCMP", "partyId");
        dynamicView.addAlias("PCMP", "contactMechPurposeTypeId");
        dynamicView.addAlias("TN", "contactNumber");
        dynamicView.addViewLink("PCMP", "TN", Boolean.TRUE, ModelKeyMap.makeKeyMapList("contactMechId"));
        fieldsToSelect.add("partyId");
        
        if(!telNumber.equals("")){
        	andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("contactMechPurposeTypeId"), EntityOperator.EQUALS, EntityFunction.UPPER("PHONE_MOBILE")));
        	andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("contactNumber"), EntityOperator.LIKE, EntityFunction.UPPER("%"+telNumber+"%")));
        }
        
        if (andExprs.size() > 0) mainCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
        // set distinct on so we only get one row per order
        EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, 200, true);
        // using list iterator
        EntityListIterator pli;
		try {
			pli = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, findOpts);
			partyList = pli.getPartialList(1, 200);
			
			pli.close();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<String> postalAddressPartyIds = getPostalAddressIds(delegator, city, stateProvinceGeoId);
		List<String> partyIds = FastList.newInstance();
		for (GenericValue gv : partyList) {
			partyIds.add((String)gv.get("partyId"));
		}
		// 构建满足条件的 partyIds
		for (String id : partyIds) {
			if( !postalAddressPartyIds.contains(id) || !partyClassificationPartyIds.contains(id)){
				partyIds.remove(id);
			}
		}
		return partyIds;
	}
	
	public static List<String> getPostalAddressIds(Delegator delegator, String city, String stateProvinceGeoId ) {
		List<EntityCondition> andExprs = FastList.newInstance();
        EntityCondition mainCond = null;
        List<String> orderBy = FastList.newInstance();
        List<String> fieldsToSelect = FastList.newInstance();
		List<GenericValue> partyList = null;
        DynamicViewEntity dynamicView = new DynamicViewEntity();
        
		dynamicView.addMemberEntity("PA", "PostalAddress");
        dynamicView.addMemberEntity("PCM", "PartyContactMech");
        dynamicView.addAlias("PA", "city");
        dynamicView.addAlias("PA", "stateProvinceGeoId");
        dynamicView.addAlias("PCM", "contactNumber");
        dynamicView.addViewLink("PCM", "PA", Boolean.TRUE, ModelKeyMap.makeKeyMapList("contactMechId"));
        fieldsToSelect.add("partyId");
        
        if(!city.equals("")){
        	andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("city"), EntityOperator.EQUALS, EntityFunction.UPPER(city)));
        }
        if(!stateProvinceGeoId.equals("")){
        	andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("stateProvinceGeoId"), EntityOperator.EQUALS, EntityFunction.UPPER(stateProvinceGeoId)));
        }
        
        if (andExprs.size() > 0) mainCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
        // set distinct on so we only get one row per order
        EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, 200, true);
        // using list iterator
        EntityListIterator pli;
		try {
			pli = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, findOpts);
			partyList = pli.getPartialList(1, 200);
			
			pli.close();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> partyIds = FastList.newInstance();
		for (GenericValue gv : partyList) {
			partyIds.add((String)gv.get("partyId"));
		}
		if(city.equals("") && stateProvinceGeoId.equals("")){
			return null;
		}
		return partyIds;
	}
	
	public static Object getSPG(Delegator delegator, Object obj) {
		if(obj == null ){
			return obj;
		}else{
			List<GenericValue> gvs = null;
			try {
				gvs = delegator.findByAnd("Geo", UtilMisc.toMap("geoId", obj));
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return gvs.get(0).get("geoName");
		}
	}
	
	public static String getAge(Object birthDate) {
		java.util.Date currDate = new java.util.Date();
		java.util.Date date = null;
		if(null != birthDate &&"" != birthDate){
			try
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				date = sdf.parse(birthDate.toString());
			}
			catch (ParseException e)
			{
				System.out.println(e.getMessage());
			}
			long day = (currDate.getTime() - date.getTime())/1000;

			return String.valueOf(day / (60 * 60 * 24 * 365));
		}else{
			return "";
		}
	}
	

	/**
	 * update Party
	 * @param dctx
	 * @param context
	 * @return
	 */
	public static Map<String, Object> updateParty(DispatchContext dctx, Map<String, ? extends Object> context) {
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        Map<String,Object> personMap = FastMap.newInstance();
        Map<String,Object> contactMechMap = FastMap.newInstance();
        Map<String,Object> partyClassificationMap = new HashMap<String,Object>();
        Map<String,Object> partyPostalAddressMap = new HashMap<String,Object>();
        
        String jsonStr = (String)context.get("jsonStr");
        JSONObject jsonObj = new JSONObject();
        jsonObj = JSONObject.fromObject(jsonStr);
        Customer customer = (Customer)JSONObject.toBean(jsonObj,Customer.class);
        
        Timestamp fromDate = UtilDateTime.nowTimestamp();
        
        //String partyId = jsonObj.get("partyId").toString();	// partyId;
        String contactMechId = "";	// 邮箱;
        String phContactMechId = "";	// 家庭电话;
        String pmContactMechId = "";	// 手机
        String plContactMechId = "";	// 联系方式
        String cmptId = "";
        List<GenericValue> partyContactMechPurposes = null;
        try {
			partyContactMechPurposes = delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", customer.getPartyId()));
			for (GenericValue genericValue : partyContactMechPurposes) {
				cmptId = (String) genericValue.get("contactMechPurposeTypeId");
				if(cmptId.equals("PRIMARY_EMAIL")){
					contactMechId = (String) genericValue.get("contactMechId");
				}else if(cmptId.equals("PHONE_HOME")){
					phContactMechId = (String) genericValue.get("contactMechId");
				}else if(cmptId.equals("PHONE_MOBILE")){
					pmContactMechId = (String) genericValue.get("contactMechId");
				}else if(cmptId.equals("PRIMARY_LOCATION")){
					plContactMechId = (String) genericValue.get("contactMechId");
				}
			}
        } catch (GenericEntityException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        
        Map<String,Object> productStoreRoleMap = new HashMap<String,Object>();
        GenericValue productStoreRole = null;
        productStoreRoleMap.put("partyId", customer.getPartyId());
        productStoreRoleMap.put("roleTypeId", "CUSTOMER");
        productStoreRoleMap.put("productStoreId", customer.getStore());
        productStoreRoleMap.put("fromDate", fromDate);
        
        try {
			List<GenericValue> productStoreRoles = delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("partyId", customer.getPartyId()));
			if(!UtilValidate.isNotEmpty(productStoreRoles)){
				productStoreRole = delegator.makeValue("ProductStoreRole", productStoreRoleMap);
				productStoreRole.create();
			}else{
				for (GenericValue gv : productStoreRoles) {
					gv.remove();
					gv = delegator.makeValue("ProductStoreRole", productStoreRoleMap);
					gv.create();
				}
			}
		} catch (GenericEntityException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        
        personMap.put("partyId", customer.getPartyId());
        personMap.put("firstName", customer.getName());
        personMap.put("lastName", "");
        personMap.put("gender", customer.getSex());
        personMap.put("integral", customer.getIntegral());
        personMap.put("birthDate", convertStringToDate(customer.getBirthDate()));
        personMap.put("cardId", customer.getCardId());
        personMap.put("socialSecurityNumber", customer.getSocialSecurityNumber());
        personMap.put("login.username", "admin");
        personMap.put("login.password", "ofbiz");

        
    	try {
			dispatcher.runSync("updatePerson", personMap);
//    		person = delegator.makeValue("Person", personMap);
//    		person.store();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
	                    "person.update.write_failure", new Object[] { e.getMessage() }, locale));
		}
		
		contactMechMap.put("contactMechId", contactMechId);
		contactMechMap.put("contactMechTypeId", "EMAIL_ADDRESS");
		contactMechMap.put("infoString", customer.getEmailAddress());
		// contactMechMap.put("login.username", "admin");
		// contactMechMap.put("login.password", "ofbiz");
		
		GenericValue contactMech = null;
        
    	try {
			// dispatcher.runSync("updateContactMech", contactMechMap);
    		contactMech = delegator.makeValue("ContactMech", contactMechMap);
    		contactMech.store();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
	                    "person.update.write_failure", new Object[] { e.getMessage() }, locale));
		}

		partyClassificationMap.put("partyId", customer.getPartyId());
		partyClassificationMap.put("partyClassificationGroupId", customer.getPartyClassificationGroupId());
		partyClassificationMap.put("fromDate", fromDate);
		// partyClassificationMap.put("login.username", "admin");
		// partyClassificationMap.put("login.password", "ofbiz");
		
		GenericValue partyClassification = null;
		
		try {
			// dispatcher.runSync("updatePartyClassification", partyClassificationMap);	// 分类
			 EntityConditionList<EntityExpr> ecl = EntityCondition.makeCondition(
	                    EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), 
	                    EntityOperator.EQUALS, EntityFunction.UPPER(customer.getPartyId()))
	                    );
			List<GenericValue> c = delegator.findList("PartyClassification", ecl, null, UtilMisc.toList("partyId", "fromDate"), null, false);
			if (c != null) {
                for (GenericValue pcf: c) {
                	pcf.remove();
                    // pcf.set("partyClassificationGroupId", partyClassificationGroupId);	// 不能更新主键
                	// pcf.store();
                	partyClassification = delegator.makeValue("PartyClassification", partyClassificationMap);
                	partyClassification.create();
                }
            }
			// partyClassification.set("partyClassificationGroupId", partyClassificationGroupId);
			// partyClassification.store();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
	                    "PartyClassification.update.write_failure", new Object[] { e.getMessage() }, locale));
		}
		
		try {
			if(!UtilValidate.isNotEmpty(pmContactMechId) && UtilValidate.isNotEmpty(customer.getPhoneNumber())){
				addTelecomNumber(dispatcher, customer.getPartyId(), customer.getPhoneNumber(), "PHONE_MOBILE");
			}else{
				storeTelecomNumber(delegator, pmContactMechId, customer.getPhoneNumber());
			}
			
			if(!UtilValidate.isNotEmpty(phContactMechId) && UtilValidate.isNotEmpty(customer.getPhoneHome())){
				addTelecomNumber(dispatcher, customer.getPartyId(), customer.getPhoneHome(), "PHONE_HOME");
			}else{
				storeTelecomNumber(delegator, phContactMechId, customer.getPhoneHome());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
	                    "PartyTelecomNumber.update.write_failure", new Object[] { e.getMessage() }, locale));
		}
		
		if(!UtilValidate.isNotEmpty(plContactMechId) && UtilValidate.isNotEmpty(customer.getAddress1())){
			try {
				addPostalAddress(dispatcher, customer.getPartyId(), customer.getAddress1(), customer.getSPGId(), customer.getCity(), "", "PRIMARY_LOCATION");
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			partyPostalAddressMap.put("contactMechId", plContactMechId);
			partyPostalAddressMap.put("toName", customer.getFirstName()+customer.getLastName());
			partyPostalAddressMap.put("address1", customer.getAddress1());
			partyPostalAddressMap.put("address2", "");
			partyPostalAddressMap.put("city", customer.getCity());
			partyPostalAddressMap.put("stateProvinceGeoId", customer.getSPGId());	// 城市
			// partyPostalAddressMap.put("postalCode", "111000");
			partyPostalAddressMap.put("countryGeoId", "CHN");	// 国家
			
			GenericValue postalAddress = null;
			
			try {
				// Map<String, Object> ppaResult = dispatcher.runSync("updatePostalAddress", partyPostalAddressMap);	// 邮政地址
				postalAddress = delegator.makeValue("PostalAddress", partyPostalAddressMap);
				postalAddress.store();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return ServiceUtil.returnError(UtilProperties.getMessage(resourceError, 
						"PartyPostalAddress.create.db_error", new Object[] { e1.getMessage() }, locale)); 
			}
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static void storeTelecomNumber(Delegator delegator, String contactMechId, String phoneNumber){
		Map<String,Object> telecomNumberMap = new HashMap<String,Object>();
		if(UtilValidate.isNotEmpty(phoneNumber)){
			telecomNumberMap.put("contactMechId", contactMechId);
			telecomNumberMap.put("countryCode", "011");
			telecomNumberMap.put("areaCode", "011");
			telecomNumberMap.put("contactNumber", phoneNumber);
			
			GenericValue gv = null;
			
			try {
				//	Map<String, Object> tResult = dispatcher.runSync("updateTelecomNumber", TelecomNumberMap);
				gv = delegator.makeValue("TelecomNumber", telecomNumberMap);
				gv.store();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static Map<String,Object> deleteParty(DispatchContext dctx, Map<String, ? extends Object> context) {
		Locale locale = (Locale) context.get("locale");
		Delegator delegator = dctx.getDelegator();
        LocalDispatcher dispatcher = dctx.getDispatcher();
        
        String jsonStr = (String)context.get("jsonStr");
        JSONObject jsonObj = new JSONObject();
        jsonObj = JSONObject.fromObject(jsonStr);
        String partyId = jsonObj.get("partyId").toString();
        
       // String partyId = "10150";	
        
        List<GenericValue> dummyPKs = new FastList<GenericValue>();
        List<GenericValue>  GenericValues= null;
        GenericValue party = null;
        GenericValue person = null;
        GenericValue contactMech = null;
        GenericValue telecomNumber = null;
        GenericValue postalAddress = null;

        try {
        	GenericValues= delegator.findByAnd("PartyContactMechPurpose", UtilMisc.toMap("partyId", partyId));
        	delegator.removeAll(GenericValues);
        	
        	GenericValues= delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId));
        	for (GenericValue partyContactMech : GenericValues) {
        		
        		contactMech = delegator.findByPrimaryKey("ContactMech", UtilMisc.toMap("contactMechId", partyContactMech.get("contactMechId")));
        		System.out.println(contactMech.get("contactMechTypeId"));
        		if(contactMech.get("contactMechTypeId").equals("TELECOM_NUMBER")){
        			telecomNumber = delegator.findByPrimaryKey("TelecomNumber", UtilMisc.toMap("contactMechId", partyContactMech.get("contactMechId")));
        			telecomNumber.remove();
        		}else if(contactMech.get("contactMechTypeId").equals("POSTAL_ADDRESS")){
        			postalAddress = delegator.findByPrimaryKey("PostalAddress", UtilMisc.toMap("contactMechId", partyContactMech.get("contactMechId")));
        			postalAddress.remove();
        		}
        		
        		partyContactMech.remove();
        		contactMech.remove();
			}
        	
            person = delegator.findByPrimaryKey("Person", UtilMisc.toMap("partyId", partyId));
            dummyPKs.add(person);
            // person.remove();
            
            GenericValues = delegator.findByAnd("ProductStoreRole", UtilMisc.toMap("partyId", partyId));
            delegator.removeAll(GenericValues);
            
            GenericValues = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", partyId));
            delegator.removeAll(GenericValues);
            
            GenericValues = delegator.findByAnd("PartyDataSource", UtilMisc.toMap("partyId", partyId));
            delegator.removeAll(GenericValues);
            
            GenericValues = delegator.findByAnd("PartyStatus", UtilMisc.toMap("partyId", partyId));
            delegator.removeAll(GenericValues);
            
            GenericValues = delegator.findByAnd("PartyClassification", UtilMisc.toMap("partyId", partyId));
            delegator.removeAll(GenericValues);
            
            GenericValues = delegator.findByAnd("PartyNameHistory", UtilMisc.toMap("partyId", partyId));
            delegator.removeAll(GenericValues);
            
            party = delegator.findByPrimaryKey("Party", UtilMisc.toMap("partyId", partyId));
            dummyPKs.add(party);
            //party.remove();
            
            delegator.removeAll(dummyPKs);
        } catch (GenericEntityException e) {
            Debug.logWarning(e.getMessage(), module);
        }
        
		return ServiceUtil.returnSuccess();
	}
	
	public static Date convertStringToDate(String dateStr) {
        Date  date = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
        format.setLenient(false);  
        //要转换字符串 
        try {
        	date = new Date(format.parse(dateStr).getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		return date;
	}
	
	public static Timestamp convertStringToTimeStamp(String dateStr) {
        Date  date = null;
        if(dateStr.equals("")){
        	dateStr = "1975-1-1";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");  
        format.setLenient(false);  
        //要转换字符串 
        try {
        	date = new Date(format.parse(dateStr).getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		return new Timestamp(date.getTime());
	}
	
	public static Map<String,Object> initParty(DispatchContext dctx, Map<String, ? extends Object> context) {
		Delegator delegator = dctx.getDelegator();
        List<String> fieldsToSelect = FastList.newInstance();
        Map<String,Object> resultJson = FastMap.newInstance();
		DynamicViewEntity dynamicView = new DynamicViewEntity();

        // default view settings
        dynamicView.addMemberEntity("G", "Geo");
        dynamicView.addAlias("G", "geoId");
        dynamicView.addAlias("G", "geoName");

        // define the main condition & expression list
        List<EntityCondition> andExprs = FastList.newInstance();
        EntityCondition mainCond = null;
        
        List<String> orderBy = FastList.newInstance();
        // fields we need to select; will be used to set distinct
        fieldsToSelect.add("geoId");
        fieldsToSelect.add("geoName");
        
        dynamicView.addMemberEntity("GA", "GeoAssoc");
        dynamicView.addAlias("GA", "geoId");
        dynamicView.addAlias("GA", "geoIdTo");
        dynamicView.addViewLink("GA", "G", Boolean.FALSE, ModelKeyMap.makeKeyMapList("geoIdTo", "geoId"));

        fieldsToSelect.add("geoId");
        fieldsToSelect.add("geoIdTo");
        
        andExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("geoId"), EntityOperator.EQUALS, EntityFunction.UPPER("CHN")));
		
        if (andExprs.size() > 0) mainCond = EntityCondition.makeCondition(andExprs, EntityOperator.AND);
        // set distinct on so we only get one row per order
        EntityFindOptions findOpts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, -1, 200, true);
        // using list iterator
        EntityListIterator pli;
        List<GenericValue> genericValue = FastList.newInstance();
        
		try {
			pli = delegator.findListIteratorByCondition(dynamicView, mainCond, null, fieldsToSelect, orderBy, findOpts);
			genericValue = pli.getPartialList(1, 200);
			
			pli.close();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String, String> map = FastMap.newInstance();
		Map<String, String> mapa = FastMap.newInstance();
		Map<String, String> mapb = FastMap.newInstance();
		Map<String, Map<String, String>> map1 = FastMap.newInstance();
		
		for (GenericValue gv : genericValue) {
			map.put((String)gv.get("geoIdTo"), (String)gv.get("geoName"));
		}
		map1.put("geo", map);
		
		List<GenericValue> partyClassificationGroup = FastList.newInstance();
		
        try {
        	 partyClassificationGroup = delegator.findList("PartyClassificationGroup", null, null, null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (GenericValue gv : partyClassificationGroup) {
			mapa.put((String)gv.get("partyClassificationGroupId"), (String)gv.get("description"));
		}
		map1.put("partyClassificationGroup", mapa);

		List<GenericValue> productStore = FastList.newInstance();
		
        try {
        	productStore = delegator.findList("ProductStore", null, null, null, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (GenericValue gv : productStore) {
			mapb.put((String)gv.get("productStoreId"), (String)gv.get("storeName"));
		}
		map1.put("productStore", mapb);
		
		String strResult = JSONSerializer.toJSON(map1).toString(); ;
        resultJson.put("jsonOut", strResult);
        
		return resultJson;
	}
    
    public static Map<String,Object> findPersonByfirstName(DispatchContext dctx, Map context) {

        Delegator delegator = dctx.getDelegator();
        String firstName = (String)context.get("firstName");
        Map<String,Object> result = new HashMap<String,Object>();

        List<GenericValue> toBeStored = null;
        List<Object> idList = FastList.newInstance();

        
        try {

            EntityConditionList<EntityExpr> ecl = EntityCondition.makeCondition(
                    EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + firstName.toUpperCase() + "%"))
                    );
            toBeStored = delegator.findList("Person", ecl, null, UtilMisc.toList("lastName", "firstName", "partyId"), null, false);


            //toBeStored = delegator.findByAnd("Person", UtilMisc.toMap("firstName", firstName));

            Iterator<GenericValue> it = toBeStored.iterator();
            while(it.hasNext()){
                GenericValue p = (GenericValue)it.next();
                idList.add(UtilMisc.toMap("partyId", (String)p.get("partyId"), 
                                          "firstName", (String)p.get("firstName"),
                                          "lastName", (String)p.get("lastName")));
            }
            result.put("personList", idList);
        }catch(GenericEntityException e){
            return ServiceUtil.returnError("firstName #" + firstName + "not found!");
        }
                
        return result;
    }    

    public static Map<String,Object> findPersonByfirstNameJson(DispatchContext dctx, Map context) {

        Delegator delegator = dctx.getDelegator();
        String firstNameJson = (String)context.get("firstName");
        Map<String,Object> result = new HashMap<String,Object>();
        try {
        	
	        JSONObject jsonObj = new JSONObject();
	        jsonObj = JSONObject.fromObject(firstNameJson);
	        String firstName = jsonObj.get("firstName").toString();
	        String lastName = jsonObj.get("lastName").toString();
	        System.out.println(lastName+":------------------");
	
	        List<GenericValue> toBeStored = null;
	        List<Object> idList = FastList.newInstance();

       
        //try{
            EntityConditionList<EntityExpr> ecl = EntityCondition.makeCondition(
                    EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + firstName.toUpperCase() + "%"))
                    );
            toBeStored = delegator.findList("Person", ecl, null, UtilMisc.toList("lastName", "firstName", "partyId"), null, false);


            //toBeStored = delegator.findByAnd("Person", UtilMisc.toMap("firstName", firstName));

            Iterator<GenericValue> it = toBeStored.iterator();
            while(it.hasNext()){
                GenericValue p = (GenericValue)it.next();
                idList.add(UtilMisc.toMap("partyId", (String)p.get("partyId"), 
                                          "firstName", (String)p.get("firstName"),
                                          "lastName", (String)p.get("lastName")));
            }
            for (int i=0;i<10000;i++){
            	idList.add(UtilMisc.toMap("partyId", "partyId1", 
                        "firstName", "firstName1",
                        "lastName", "lastName1"));
            }
            String strResult = JSONUtil.listToJson(idList);
            
            result.put("personList", strResult);
        }catch(GenericEntityException e){
            return ServiceUtil.returnError("firstName #" + firstNameJson + "not found!");
        }
        catch(Exception e){
	    	e.printStackTrace();
	    }    
        return result;
    }    

    public static Map<String,Object> findSoap(DispatchContext dctx, Map context) {

		/**介绍如何结合soapui测试这个服务*/

		//return ServiceUtil.returnSuccess("成功运行TestSoap服务");
		Map<String, Object> result = new HashMap<String,Object>();//ServiceUtil.returnSuccess("成功运行TestSoap服务");
		result.putAll(context);
		return result;
	}
    
}