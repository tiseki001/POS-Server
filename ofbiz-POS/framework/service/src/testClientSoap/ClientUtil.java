package testClientSoap;

 

import java.util.List;

 

import org.apache.axiom.om.OMAbstractFactory;

import org.apache.axiom.om.OMElement;

import org.apache.axiom.om.OMFactory;

import org.apache.axiom.om.OMNamespace;

import org.apache.axis2.AxisFault;

import org.apache.axis2.addressing.EndpointReference;

import org.apache.axis2.client.Options;

import org.apache.axis2.client.ServiceClient;

 

public class ClientUtil {

 

private static OMFactory factory = OMAbstractFactory.getOMFactory();

private String endPoint;

private List<ClientGenericValue> valueList;

private String om;

private String serviceName;

 

public ClientUtil(String endPoint, String serviceName , String om,

List<ClientGenericValue> valueList) {

this.endPoint = endPoint;

this.serviceName = serviceName;

this.om = om;

this.valueList = valueList;

}

 

 

 

public ServiceClient createClient() throws AxisFault {

ServiceClient client = new ServiceClient();

Options options = new Options();

 

EndpointReference targetERP = new EndpointReference(endPoint); //定义目的EndpointReference，就是服务地址

options.setTo(targetERP);

options.setTimeOutInMilliSeconds(400000); //定义超时，这里可以不定义

 

client.setOptions(options);

return client;

}

 

public OMElement send() throws AxisFault{

OMNamespace ns = factory.createOMNamespace("http://ofbiz.apache.org/service/", serviceName);

OMElement root = factory.createOMElement(serviceName, ns);

 

OMElement data = factory.createOMElement("map-HashMap", null);

root.addChild(data);

for(ClientGenericValue value : valueList){

OMElement mapKey = factory.createOMElement("map-Entry", null);

 

OMElement keyElement = factory.createOMElement("map-Key", null);

OMElement keyValue = factory.createOMElement("std-String", null);

keyValue.addAttribute(factory.createOMAttribute("value", null, value.getKey()));

keyElement.addChild(keyValue);

 

 

OMElement valueElement = factory.createOMElement("map-Value", null);

OMElement valueValue = factory.createOMElement(value.getType(), null);

valueValue.addAttribute(factory.createOMAttribute("value", null, value.getValue()));

valueElement.addChild(valueValue);

 

mapKey.addChild(keyElement);

mapKey.addChild(valueElement);

 

data.addChild(mapKey);

}

System.out.println(root);

OMElement result = createClient().sendReceive(root);

return result;

}

 

 

 

public String getEndPoint() {

return endPoint;

}

 

public void setEndPoint(String endPoint) {

this.endPoint = endPoint;

}

 

}