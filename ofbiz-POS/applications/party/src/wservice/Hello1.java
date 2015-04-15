package wservice;  
  

import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.base.util.*;

/** 
 *  
 * @author why 
 * 
 */  

public class Hello1 {  
  
    public Person sayHello(String name) {
    	String result="hhhhhhhhhhhh";
    	GenericDelegator delegator = GenericDelegator.getGenericDelegator("default");
    	LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher("default",delegator);
    	GenericValue admin = null;
    	try {
    	    admin = delegator.findByPrimaryKey("UserLogin", UtilMisc.toMap("userLoginId", "admin"));
    	    result = admin.toString();
    	} catch (GenericEntityException e1) {
    	    e1.printStackTrace();
    	}
    	Person p = new Person("zhangsan", "女", 18);
    	
        return p;  
    }  

}  


class Person {
	public Person(String name, String sex, int age){
		this.name = name;
		this.sex = sex;
		this.age = age;
	}
	String name;
	String sex;
	int age;
}

