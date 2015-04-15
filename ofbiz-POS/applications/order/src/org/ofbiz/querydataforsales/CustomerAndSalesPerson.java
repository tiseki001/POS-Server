package org.ofbiz.querydataforsales;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityOperator;
//员工和会员获取
public class CustomerAndSalesPerson {
	//通过销售人员id获取销售人员姓名
	 public static List<GenericValue> getEmployee(Delegator delegator, String partyId) {
	       
	        List<GenericValue> PersonAndCustomer = null;
	        
	        List<EntityCondition> condList = FastList.newInstance();
	        
	        condList.add(EntityCondition.makeCondition("roleTypeId", "EMPLOYEE"));
	     
	        if(UtilValidate.isNotEmpty(partyId))
	        	condList.add(EntityCondition.makeCondition("partyId", partyId));
	        
	        PersonAndCustomer = getPersonAndCustomer(delegator,condList);
	        return PersonAndCustomer;
	    }
	 //通过手机号码获取会员ID和会员姓名
	    public static List<GenericValue> getCustomer(Delegator delegator,String phoneMobile,String memberId) {
	    
	        List<GenericValue> PersonAndCustomer = null;
	        
	        List<EntityCondition> condList = FastList.newInstance();
	        condList.add(EntityCondition.makeCondition("roleTypeId", "CUSTOMER"));
	 
	        if(UtilValidate.isNotEmpty(phoneMobile))
	        	condList.add(EntityCondition.makeCondition("phoneMobile", phoneMobile));
	        	String partyId = memberId;
	        if(UtilValidate.isNotEmpty(partyId)){
	        	condList.add(EntityCondition.makeCondition("partyId", partyId));
	        }
	        PersonAndCustomer = getPersonAndCustomer(delegator,condList);
	        return PersonAndCustomer;
	    }
	    public static List<GenericValue> getPersonAndCustomer(Delegator delegator, List<EntityCondition> condList) {
	    	List<GenericValue> PersonAndCustomer = null;
	    		     
	        EntityCondition condition = EntityCondition.makeCondition(condList);
	      
	        try {
	        	PersonAndCustomer = delegator.findList("PersonAndCustomer", condition, null,UtilMisc.toList("partyId"), null, false);
	        } catch (Exception e) {
	            Debug.logError(e, "Unable to get PersonAndCustomer shipping methods");
	            return null;
	        }
	    	return PersonAndCustomer;
	    }

}
