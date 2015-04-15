package testClientSoap;

 

import java.util.ArrayList;

import java.util.List;

 

import org.apache.axiom.om.OMElement;

import org.apache.axis2.AxisFault;

 

public class ClientTest {

 

/**

* @param args

* @throws AxisFault

*/

public static void main(String[] args) {

String endPoint = "http://localhost:8080/webtools/control/SOAPService/";

String serviceName = "findPersonByfirstName";

String om = "http://ofbiz.apache.org/service/";

List<ClientGenericValue> valueList = getTestData();

 

 

ClientUtil cu = new ClientUtil(endPoint, serviceName, om, valueList);

 

try {

OMElement result = cu.send();

System.out.println("Sent Hello, got : " + result.toString());

} catch (AxisFault e) {

// TODO Auto-generated catch block

e.printStackTrace();

}

 

}

 

public static List<ClientGenericValue> getTestData(){

List<ClientGenericValue> valueList = new ArrayList<ClientGenericValue>();

ClientGenericValue value1 = new ClientGenericValue();

value1.setType("std-String");

value1.setKey("firstName");

value1.setValue("Ima");

 

/*ClientGenericValue value2 = new ClientGenericValue();

value2.setType("std-String");

value2.setKey("salt");

value2.setValue("The power of OFBiz");

 

// ClientGenericValue value3 = new ClientGenericValue();

// value3.setType("eeval-OrderItemTypeAttr");

// value3.setKey("testing");

// value3.setValue("org.ofbiz.entity.GenericValue"); //这个可以不用填写的*/

 

valueList.add(value1);

//valueList.add(value2);

// valueList.add(value3);

return valueList;

}

}