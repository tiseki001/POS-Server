package org.ofbiz.face.erpface;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.entity.serialize.XmlConverUtil;
import org.ofbiz.face.erpface.xml.ErpXmlConverUtil;
import org.ofbiz.face.log.SaveXml;

import com.sun.jersey.core.util.Base64;

public class RestInvocationBasicAuthWithHeader {
	// xml payload with REST header for invoking the service
	
	
	
	private static final String xmlRequest4 = "<ns:GET_Input xmlns:ns=\"http://xmlns.oracle.com/apps/fnd/soaprovider/plsql/rest/fnd_profile/get/\""
			+ "     xmlns:ns1=\"http://xmlns.oracle.com/apps/fnd/soaprovider/plsql/rest/fnd_profile/header\">"
			+ "    <ns1:RESTHeader>"
			+ "			<ns1:Responsibility>FND_REP_STANDALONE_APP</ns1:Responsibility>"
			+ "			<ns1:RespApplication>FND</ns1:RespApplication>"
			+ "			<ns1:SecurityGroup>STANDARD</ns1:SecurityGroup>"
			+ "			<ns1:NLSLanguage>AMERICAN</ns1:NLSLanguage>"
			+ "			<ns1:Org_Id>81</ns1:Org_Id>"
			+ "    </ns1:RESTHeader>"
			+ "    <ns:InputParameters>"
			+ "    {0}"
			+ "    </ns:InputParameters>"
			+ "</ns:GET_Input>";

	private static final String xmlRequest = "<InputData >"
		+ "    <RESTHeader>"
		+ "			<Responsibility>FND_REP_STANDALONE_APP</Responsibility>"
		+ "			<RespApplication>FND</RespApplication>"
		+ "			<SecurityGroup>STANDARD</SecurityGroup>"
		+ "			<NLSLanguage>AMERICAN</NLSLanguage>"
		+ "			<Org_Id>81</Org_Id>"
		+ "    </RESTHeader>"
		+ "    <InputParameters>"
		+ "    {0}"
		+ "    </InputParameters>"
		+ "</InputData>";

	/**
	 * This method invokes a REST service using basic Authentication and xml payload with REST headers.
	 */

		

	public static String postXml_BasicAuth(String model,String svcUrlStr, Map<String,Object> params,String username,String passwd) throws IOException {
		String xmlRequset= xmlRequest4;
		String paramsStr = "";
		if(params==null||params.size()<1){
			xmlRequset = xmlRequset.replace("{0}", "");
		}else{
			
			Set<String> keys = params.keySet();
			for(String key : keys){
				// paramsStr+="<"+key+">"+params.get(key).toString()+"</"+key+">";
				paramsStr+="<ns:"+key+">"+params.get(key).toString()+"</ns:"+key+">";
			}
			xmlRequset = xmlRequset.replace("{0}", paramsStr);
		}

		URL url = new URL(svcUrlStr);
		// Obtaining connection to invoke the service
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// Setting Http header values
		conn.setConnectTimeout(1800000);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/xml");
		String auth = username + ":" + passwd;
		byte[] bytes = Base64.encode(auth);
		String authStr = new String(bytes);
		conn.setRequestProperty("Authorization", "Basic " + authStr);
		conn.setRequestProperty("Accept", "application/xml");
		conn.setRequestProperty("Content-Language", "en-US");
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		// Send request
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

		wr.write(xmlRequset.toCharArray());

		

		wr.flush();
		wr.close();
		conn.connect();
		System.out.println("Response code - " + conn.getResponseCode());
		// Get Response
		String response = null;
		try {
			response = readHttpResponse(conn);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			if (conn != null)
				conn.disconnect();
		}
		// Show response
		SaveXml.saveXmlByStr("ebsdata/"+model, model, response);
		return response;
	}
	/**
	 * This method invokes a REST service using basic Authentication and xml payload with REST headers.
	 */
	public static void postXml_BasicAuth(String svcUrlStr, String username,String passwd) throws IOException {

		URL url = new URL(svcUrlStr);
		// Obtaining connection to invoke the service
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// Setting Http header values
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/xml");
		String auth = username + ":" + passwd;
		byte[] bytes = Base64.encode(auth);
		String authStr = new String(bytes);
		conn.setRequestProperty("Authorization", "Basic " + authStr);
		conn.setRequestProperty("Accept", "application/xml");
		conn.setRequestProperty("Content-Language", "en-US");
		conn.setUseCaches(false);
		conn.setDoInput(true);
		conn.setDoOutput(true);
		// Send request
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(xmlRequest4.toCharArray());
		wr.flush();
		wr.close();
		conn.connect();
		System.out.println("Response code - " + conn.getResponseCode());
		// Get Response
		String response = null;
		try {
			response = readHttpResponse(conn);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		finally {
			if (conn != null)
				conn.disconnect();
		}
		System.out.println(response);
		// Show response
	}

	/**
	 * This method reads response from server and returns it in a string representation.
	 */
	private static String readHttpResponse(HttpURLConnection conn) {

		InputStream is = null;
		BufferedReader rd = null;
		StringBuffer response = new StringBuffer();
		try {

			if (conn.getResponseCode() >= 400) {
				is = conn.getErrorStream();
			} else {
				is = conn.getInputStream();
			}
			rd = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\n');
			}
		} catch (IOException ioe) {
			response.append(ioe.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
				}
			}
			if (rd != null) {
				try {
					rd.close();
				} catch (Exception e) {
				}
			}
		}
		return (response.toString());
	}


}

