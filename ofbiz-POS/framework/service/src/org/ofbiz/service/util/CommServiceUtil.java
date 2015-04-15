package org.ofbiz.service.util;


import java.util.Iterator;

import java.util.Map;

import javax.xml.namespace.QName;


import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

import org.apache.axis2.Constants;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.axis2.transport.http.HttpTransportProperties;
import org.ofbiz.base.util.UtilProperties;


public class CommServiceUtil {


	
	public static String sendRectWebService(String url, String strServiceName, Map<String,String> mParam) {
		String strJson="";
		String systemDir = System.getProperty("ofbiz.home")+"\\framework\\base\\config\\";
		String keyPass = UtilProperties.getPropertyValue("service", "clientKeyPass", "");
		String trustPass = UtilProperties.getPropertyValue("service", "clientTrustPass", "");
		try {
			System.setProperty("javax.net.ssl.keyStore", systemDir+"client.keystore"); 
			System.setProperty("javax.net.ssl.keyStorePassword", keyPass); 
			System.setProperty("javax.net.ssl.trustStore", systemDir+"client.truststore"); 
			System.setProperty("javax.net.ssl.trustStorePassword", trustPass); 
		    ServiceClient sc = new ServiceClient();

		    Options opts = new Options();
		    opts.setTo(new EndpointReference(url));
		    opts.setTimeOutInMilliSeconds(900000L);
		    opts.setAction(strServiceName);
		    //opts.setTransportInProtocol(Constants.TRANSPORT_HTTPS);
		    
		    //用户密码认证	
//		    HttpTransportProperties.Authenticator basicAuth = new HttpTransportProperties.Authenticator();  
//            basicAuth.setUsername("username");  
//            basicAuth.setPassword("pass");  
//            opts.setProperty(org.apache.axis2.transport.http.HTTPConstants.AUTHENTICATE, basicAuth);
		    //call dot net
//		    opts.setProperty(org.apache.axis2.transport.http.HTTPConstants.CHUNKED, Boolean.FALSE);
		    
		    //call rest service
		    //opts.setProperty(Constants.Configuration.ENABLE_REST, Constants.VALUE_TRUE);
		    //opts.setProperty(Constants.Configuration.HTTP_METHOD,Constants.Configuration.HTTP_METHOD_POST);
		    sc.setOptions(opts);
		    OMElement res = sc.sendReceive(createSendEntity(strServiceName,mParam));
	        OMElement res1 = res.getFirstElement().getFirstElement();
	        Iterator<OMElement> value = res1.getChildrenWithLocalName("map-Value");
	        if (value.hasNext()) {
	        	OMElement param = (OMElement)value.next();

	        	OMElement param2 = param.getFirstElement();
	        	strJson = param2.getAttributeValue(new QName("value"));
	        }
	        sc.cleanupTransport(); 
		} catch(Exception e){
			System.out.print(e.getMessage());
			strJson = "error";
		}
		return strJson;
	}
	
    private static OMElement createSendEntity(String serviceName,Map<String,String> mParam) {
    	OMFactory fac=OMAbstractFactory.getOMFactory();


        OMElement SendMsg = null;
        try {
        	
        	//OMNamespace omNs = fac.createOMNamespace("http://ofbiz.apache.org/service/", "");

        	SendMsg = fac.createOMElement(serviceName, null);
            OMElement mapMap = fac.createOMElement("map-Map", null);

            SendMsg.addChild(mapMap);
            if (null != mParam) {
	            for(Map.Entry<String,String> entry : mParam.entrySet()){  
	            	mapMap.addChild(createMapEntry(fac,entry.getKey(),entry.getValue()));
	            }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return SendMsg;
     }

     private static OMElement createMapEntry(OMFactory fac,String key, String val) {

        OMElement mapEntry = fac.createOMElement("map-Entry", null);

        // create the key
        OMElement mapKey = fac.createOMElement("map-Key", null);
        OMElement keyElement = fac.createOMElement("std-String", null);
        OMAttribute keyAttribute = fac.createOMAttribute("value", null, key);

        mapKey.addChild(keyElement);
        keyElement.addAttribute(keyAttribute);

        // create the value
        OMElement mapValue = fac.createOMElement("map-Value", null);
        OMElement valElement = fac.createOMElement("std-String", null);
        OMAttribute valAttribute = fac.createOMAttribute("value", null, val);

        mapValue.addChild(valElement);
        valElement.addAttribute(valAttribute);

        // attach to map-Entry
        mapEntry.addChild(mapKey);
        mapEntry.addChild(mapValue);

        return mapEntry;
     }
}