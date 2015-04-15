package testClientSoap;

import java.util.*;
import java.net.*;
import java.rmi.*;
import javax.xml.namespace.*;
import javax.xml.rpc.*;
import javax.xml.rpc.encoding.TypeMapping;
import javax.xml.rpc.encoding.TypeMappingRegistry;
//import javax.wsdl.OperationType;
import org.apache.axis.Message;

import org.apache.axis.encoding.XMLType;
import org.apache.axis.message.RPCElement;
import org.apache.axis.message.RPCParam;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.utils.*;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.ofbiz.service.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

public class TestCreateCatalog {

	private static Map getResponseParams(Message respMessage) {
        Map mRet = new Hashtable();
        try {
            SOAPEnvelope resEnv = respMessage.getSOAPEnvelope();
            List bodies = resEnv.getBodyElements();
            Iterator i = bodies.iterator();
            while (i.hasNext()) {
                Object o = i.next();
                if (o instanceof RPCElement) {
                    RPCElement body = (RPCElement) o;
                    List params = null;
                    params = body.getParams();
                    Iterator p = params.iterator();
                    while (p.hasNext()) {
                        RPCParam param = (RPCParam) p.next();
                        mRet.put(param.getName(), param.getValue());
                       
                        System.out.println("SOAP Client Param - " + param.getName() + "=" + param.getValue());
                    }
                }
            }
        } catch (org.apache.axis.AxisFault e) {
         System.out.println("AxisFault");
        } catch (org.xml.sax.SAXException e) {
         System.out.println("SAXException");
        }
        return mRet;
	}
	
	private static void callRPC(String targetName,String methodName){
        String message = "";
        Map output;
        ArrayList outputList;
        String responseMessage,firstName,lastName,endpoint;
   
	    try {

	        endpoint = targetName;
	        XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
	        config.setServerURL(new URL(endpoint));
	        config.setEnabledForExceptions(true);

	        XmlRpcClient client = new XmlRpcClient(config);
	        client.setConfig(config);

	        //Map paramMap = new HashMap();

	        //paramMap.put("catalogName", "11");                     //参数
	        //paramMap.put("login.username", "admin");          //用户名
	        //paramMap.put("login.password", "ofbiz");            //密码

	        //Object[] params = new Object[]{paramMap};
	        
	        //Object[] params = new Object[]{"image"};
	        Map paramMap = new HashMap();

	        paramMap.put("firstName", "image");                     //参数
	        Object[] params = new Object[]{paramMap};
	        
	        Map result = (Map) client.execute(methodName, params);

	        Object[] oz = (Object [])result.get("personList");
	        for(int i=0;i<oz.length;i++){
	            HashMap person = (HashMap)oz[i];
	            System.out.println(person.get("partyId")+"  "+person.get("firstName")+" "+person.get("lastName"));
	        }
	        System.out.println(result.toString());
	    } catch (MalformedURLException ex) {
	        message = "error: wrong url";
	    }  catch (XmlRpcException ex) {
	        message = "error: failed to create the call";
	    } finally {
	      System.out.println("");
	      System.out.println(message);
	    }		
	}
	
	private static void callAXIS(String targetName,String methodName){
        String message = "";
        Map output;
        ArrayList outputList;
        String responseMessage,firstName,lastName,endpoint;
   
	    try {
	        endpoint = targetName;
	        Service service = new Service();
	        Call call = (Call) service.createCall();
	        call.setTargetEndpointAddress(new URL(endpoint));
	        call.setOperationName(new QName( methodName,methodName));
	        /*call.addParameter("login.username",
	                org.apache.axis.Constants.XSD_STRING,
	                javax.xml.rpc.ParameterMode.IN);
	        call.addParameter("login.password",
	                org.apache.axis.Constants.XSD_STRING,
	                javax.xml.rpc.ParameterMode.IN);*/

	        call.setReturnClass(org.w3c.dom.Element.class);

	        call.addParameter("firstName",
	        		XMLType.XSD_STRING,ParameterMode.IN);
	        
	        call.setUseSOAPAction(true); 
	        call.setReturnType(org.apache.axis.Constants.XSD_STRING);
	        Object responseWS = call.invoke(new Object[]{"Image"});
	        System.out.println( "Receiving response: " +  (String) responseWS);
	        output = call.getOutputParams();
	        getResponseParams(call.getMessageContext().getResponseMessage());

	    } catch (MalformedURLException ex) {
	        message = "error: wrong url";
	    } catch (ServiceException ex) {
	        message = "error: failed to create the call";
	    } catch (RemoteException ex) {
	        message = "error: failed to invoke WS";
	    }/* catch (XmlRpcException ex) {
	        message = "error: failed to create the call";
	    }*/ finally {
	      System.out.println("");
	      System.out.println(message);
	    }
	}
	
	public static void main(String[] args) throws Exception {

		//String address = "http://localhost:8080/catalog/control/SOAPService/";
		//String address = "http://localhost:8080/catalog/control/xmlrpc/";
		//String address = "http://localhost:8080/webtools/control/xmlrpc/";
		String address = "http://localhost:8080/webtools/control/SOAPService/";
		//String methods = "createProdCatalog";
		String methods = "findPersonByfirstName";
        //callRPC(address,methods);
		callAXIS(address,methods);
   

    }
}   