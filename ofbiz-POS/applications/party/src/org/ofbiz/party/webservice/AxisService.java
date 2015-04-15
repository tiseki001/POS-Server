package org.ofbiz.party.webservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javolution.util.FastList;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.DynamicViewEntity;
import org.ofbiz.entity.model.ModelKeyMap;
import org.ofbiz.entity.serialize.XmlConverUtil;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.util.JSONUtil;


import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * 
 * @author why
 * 
 */

public class AxisService {

	public static Customer[] selectParty() {
		List<GenericValue> list = selectPartyList();
		Customer[] eis = new Customer[list.size()];
		int i = 0;
		for (GenericValue pl : list) {
			
			eis[i].setPartyId((String) pl.get("partyId"));
			eis[i].setContactMechId((String) pl.get("contactMechId"));
			eis[i].setContactMechPurposeTypeId((String) pl
					.get("contactMechPurposeTypeId"));
			eis[i].setName((String) pl.get("lastName")
					+ (pl.get("firstName") == null ? "" : pl.get("firstName")));
			eis[i].setSex((String) pl.get("personalTitle"));
			eis[i].setAge(getAge(pl.get("birthDate")));
			eis[i].setBirthDate(pl.get("birthDate") + "");
			eis[i].setPhoneMobile((String) pl.get("contactNumber"));
			eis[i].setIdentityCard((String) pl.get("socialSecurityNumber"));
			eis[i].setStore((String) pl.get("storeName"));
			eis[i].setIntegral((String) pl.get("integral"));
			eis[i].setCardId((String) pl.get("cardId"));
			eis[i].setTotelIngetral("");
			eis[i].setClassification((String) pl.get("description"));
			eis[i].setCreateDate(pl.get("createdStamp") + "");
			eis[i].setPhoneHome((String) pl.get("contactNumber1"));
			eis[i].setEmailAddress((String) pl.get("infoString"));
			eis[i].setSPGId((String)pl.get("stateProvinceGeoId"));
			eis[i].setCity((String) pl.get("city"));
			eis[i].setAddress1((String) pl.get("address1"));
			i++;
		}
		// XmlConverUtil.listtoXml(list);
		return eis;
	}

	public static String selectPartyXml() {
		System.out.println("selectPartyXml");
		List<GenericValue> list = selectPartyList();
		String xml = "";
		try {
			xml = XmlConverUtil.listtoXml(list);
			System.out.println(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xml;
	}
	public static String selectPartyByName(String partyId) {
		System.out.println("selectPartyByName");
		List<GenericValue> list = selectPartyList();
		List<GenericValue> list1 = FastList.newInstance();
		
		for (GenericValue pl : list) {
			if(pl.get("partyId").toString().equals(partyId))
				list1.add(pl);
			
		}
		String xml = "";
		try {
			if(partyId!=null&&!partyId.equals(""))
				xml = XmlConverUtil.listtoXml(list1);
			else
				xml = XmlConverUtil.listtoXml(list);
			System.out.println(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xml;
	}

	private static List<GenericValue> selectPartyList() {
		GenericDelegator delegator = GenericDelegator
				.getGenericDelegator("default");
		// LocalDispatcher dispatcher =
		// GenericDispatcher.getLocalDispatcher("default",delegator);
		
		List<GenericValue> partyList = null;
		DynamicViewEntity dynamicView = new DynamicViewEntity();

		// define the main condition & expression list
		List<EntityCondition> andExprs = FastList.newInstance();
		EntityCondition mainCond = null;

		List<String> orderBy = FastList.newInstance();
		List<String> fieldsToSelect = FastList.newInstance();
		// default view settings
//		dynamicView.addMemberEntity("PAR", "Party");
//		dynamicView.addAlias("PAR", "statusId");
		
		dynamicView.addMemberEntity("PE", "Person");
//		dynamicView.addViewLink("PAR", "PE", Boolean.TRUE,
//				ModelKeyMap.makeKeyMapList("partyId"));
		dynamicView.addAlias("PE", "partyId");
		dynamicView.addAlias("PE", "lastName");
		dynamicView.addAlias("PE", "firstName");
		dynamicView.addAlias("PE", "cardId");
		dynamicView.addAlias("PE", "integral");
		dynamicView.addAlias("PE", "createdStamp");
		dynamicView.addAlias("PE", "birthDate");
		dynamicView.addAlias("PE", "personalTitle");
		dynamicView.addAlias("PE", "socialSecurityNumber");
		
		// dynamicView.addViewLink("PT", "PE", Boolean.FALSE,
		// ModelKeyMap.makeKeyMapList("partyId"));
		dynamicView.addMemberEntity("PCF", "PartyClassification");
		dynamicView.addMemberEntity("PCG", "PartyClassificationGroup");

		dynamicView.addAlias("PCF", "partyClassificationGroupId");
		dynamicView.addAlias("PCG", "description");
		// 一对一关系 内，左外 关联都可行 true：左外关联；false：内关联
		// 一对多；先true,后false
		dynamicView.addViewLink("PE", "PCF", Boolean.TRUE,
				ModelKeyMap.makeKeyMapList("partyId"));
		dynamicView.addViewLink("PCF", "PCG", Boolean.TRUE,
				ModelKeyMap.makeKeyMapList("partyClassificationGroupId"));

		fieldsToSelect.add("partyId");
		fieldsToSelect.add("integral");
		fieldsToSelect.add("cardId");
		fieldsToSelect.add("firstName");
		fieldsToSelect.add("lastName");
		fieldsToSelect.add("personalTitle");
		fieldsToSelect.add("socialSecurityNumber");
		fieldsToSelect.add("createdStamp");
		fieldsToSelect.add("birthDate");
		fieldsToSelect.add("description");

		orderBy.add("lastName");

		

		if (andExprs.size() > 0)
			mainCond = EntityCondition.makeCondition(andExprs,
					EntityOperator.AND);
		// set distinct on so we only get one row per order
		EntityFindOptions findOpts = new EntityFindOptions(true,
				EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
				EntityFindOptions.CONCUR_READ_ONLY, -1, -1, true);
		// using list iterator
		EntityListIterator pli;
		try {
			TransactionUtil.begin();
			pli = delegator.findListIteratorByCondition(dynamicView, mainCond,
					null, fieldsToSelect, orderBy, findOpts);
			// partyList = pli.getPartialList(1, -1);
			if(pli.first())
				partyList = pli.getCompleteList();
			pli.close();
			TransactionUtil.commit();
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return partyList;
//		List<Customer> list = FastList.newInstance();
		
//		for (GenericValue pl : partyList) {
//			Customer customer = new Customer();
//			
//			customer.setPartyId((String) pl.get("partyId"));
//			customer.setContactMechId((String) pl.get("contactMechId"));
//			customer.setContactMechPurposeTypeId((String) pl
//					.get("contactMechPurposeTypeId"));
//			customer.setName((String) pl.get("lastName")
//					+ (pl.get("firstName") == null ? "" : pl.get("firstName")));
//			customer.setSex((String) pl.get("personalTitle"));
//			customer.setAge(getAge(pl.get("birthDate")));
//			customer.setBirthDate(pl.get("birthDate") + "");
//			customer.setPhoneMobile((String) pl.get("contactNumber"));
//			customer.setIdentityCard((String) pl.get("socialSecurityNumber"));
//			customer.setStore((String) pl.get("storeName"));
//			customer.setIntegral((String) pl.get("integral"));
//			customer.setCardId((String) pl.get("cardId"));
//			customer.setTotelIngetral("");
//			customer.setClassification((String) pl.get("description"));
//			customer.setCreateDate(pl.get("createdStamp") + "");
//			customer.setPhoneHome((String) pl.get("contactNumber1"));
//			customer.setEmailAddress((String) pl.get("infoString"));
//			customer.setSPGId((String) getSPG(delegator,
//					pl.get("stateProvinceGeoId")));
//			customer.setCity((String) pl.get("city"));
//			customer.setAddress1((String) pl.get("address1"));
//
//			list.add(customer);
//		}

	}

	public static Object getSPG(Delegator delegator, Object obj) {
		if (obj == null) {
			return obj;
		} else {
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
		if (null != birthDate && "" != birthDate) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				date = sdf.parse(birthDate.toString());
			} catch (ParseException e) {
				System.out.println(e.getMessage());
			}
			long day = (currDate.getTime() - date.getTime()) / 1000;

			return String.valueOf(day / (60 * 60 * 24 * 365));
		} else {
			return "";
		}
	}

}
