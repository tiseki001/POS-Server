package org.ofbiz.face.erpface.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ErpXmlConverUtil {

	/**
	 * xml to map xml <node><key label="key1">value1</key><key
	 * label="key2">value2</key>......</node>
	 * 
	 * @param xml
	 * @return
	 * @throws DocumentException
	 */
	public static Map xmltoMap(String xml) throws DocumentException {

		Map map = new HashMap();
		Document document = DocumentHelper.parseText(xml);
		Element nodeElement = document.getRootElement();
		List node = nodeElement.elements();
		for (Iterator it = node.iterator(); it.hasNext();) {
			Element elm = (Element) it.next();
			map.put(elm.getName(), elm.getText());
			elm = null;
		}
		node = null;
		nodeElement = null;
		document = null;
		return map;

	}  
	
	/**
	 * xml to list xml <nodes><node><key label="key1">value1</key><key
	 * label="key2">value2</key>......</node><node><key
	 * label="key1">value1</key><key
	 * label="key2">value2</key>......</node></nodes>
	 * 
	 * @param xml
	 * @return
	 * @throws DocumentException
	 */
	public static List xmltoList(String xml, String rootName)
			throws DocumentException {

		List<Map> list = new ArrayList<Map>();
		Document document = DocumentHelper.parseText(xml);
		Element nodesElement = document.getRootElement();
		Element lElement = nodesElement.element(rootName);
		List nodes = lElement.elements();
		for (Iterator its = nodes.iterator(); its.hasNext();) {
			Element nodeElement = (Element) its.next();
			Map map = xmltoMap(nodeElement.asXML());
			list.add(map);
			map = null;
		}
		nodes = null;
		nodesElement = null;
		document = null;
		return list;

		// return null;
	}  

	/**
	 * 头行结构，将头，行节点作为key
	 * @param xml
	 * @return
	 * @throws DocumentException 
	 */
	public static Map xmltoMapObj(String xml) throws DocumentException {  
        Map map = new HashMap();  
        Map<String, Map<String, String>> mapObj = new HashMap();  
        Document document = DocumentHelper.parseText(xml);  
        Element nodeElement = document.getRootElement(); 
        List node = nodeElement.elements();  
        for (Iterator it = node.iterator(); it.hasNext();) {  
            Element elm = (Element) it.next();  
            map.put(elm.getName(), elm.getText());  
            elm = null;  
        }  
        //头行结构，将头，行节点作为key
        mapObj.put(nodeElement.getName(), map);
        node = null;  
        nodeElement = null;  
        document = null;  
        return mapObj;
    }  
	/**
	 * xml to list xml <root><nodes><node><key label="key1">value1</key><key
	 * label="key2">value2</key>......</node><node><key
	 * label="key1">value1</key><key
	 * label="key2">value2</key>......</node></nodes></root>
	 * 
	 * @param xml
	 * @return
	 */
	public static List xmltoList(String xml) {
		try {
			List<Map> list = new ArrayList<Map>();
			Document document = DocumentHelper.parseText(xml);
			Element nodesElement = document.getRootElement();
			
			// 循环根节点下的所有子节点
			Iterator iter =  nodesElement.elementIterator();
			while (iter.hasNext()) {
				Element ele = (Element) iter.next();
				// 获取单个子节点
				for (Iterator iters = ele.elementIterator(); iters.hasNext();) {
					Element itemEle = (Element) iters.next();
					Map map = xmltoMap(itemEle.asXML());
					list.add(map);
					map = null;
				}
			}
			nodesElement = null;
			document = null;
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 头行xml解析
	 * xml <OutputParameters> <GET_TXN_CMD_LINE> <TXNS>
	 *          <Header><txn_header_id>10401</txn_header_id>...  
		 *			<Lines><Line><mo_line_id>10562</mo_line_id> ...</Line>
		 *					...
		 *			</Lines>
		 *		</Header>
		 *</TXNS></GET_TXN_CMD_LINE> </OutputParameters> 
	 * @param xml
	 * @return
	 */
	public static List headersAndLinesxmltoList(String xml) {
		xml = xml.replaceAll("&lt;", "<");
		try {
			List<Map> list = new ArrayList<Map>();
			Document document = DocumentHelper.parseText(xml);
			Element nodesElement = document.getRootElement();
			
			// 循环根节点下的所有子节点
			Iterator iter =  nodesElement.elementIterator();
			while (iter.hasNext()) {
				Element ele = (Element) iter.next();
				// 获取单个子节点
				for (Iterator TXNSIters = ele.elementIterator(); TXNSIters.hasNext();) {
					Element headerEle = (Element) TXNSIters.next();
					Map map = headerXmltoMap(headerEle.asXML());
					list.add(map);
					map = null;
				}
			}
			nodesElement = null;
			document = null;
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 头行xml解析
	 * xml <OutputParameters> <GET_TXN_CMD_LINE> <TXNS>
	 *          <Header><txn_header_id>10401</txn_header_id>...  
		 *			<Lines><Line><mo_line_id>10562</mo_line_id> ...</Line>
		 *					...
		 *			</Lines>
		 *		</Header>
		 *</TXNS></GET_TXN_CMD_LINE> </OutputParameters> 
	 * @param xml
	 * @return
	 */
	public static List headersAndLinesTXNSxmltoList(String xml) {
		xml = xml.replace("<?xml version = '1.0' encoding = 'UTF-8'?>", "").replaceAll("&lt;", "<");
		try {
			List<Map> list = new ArrayList<Map>();
			Document document = DocumentHelper.parseText(xml);
			Element nodesElement = document.getRootElement();
			
			// 循环根节点下的所有子节点
			Iterator iter =  nodesElement.elementIterator();
			while (iter.hasNext()) {
				Element ele = (Element) iter.next();
				// 获取单个子节点
				for (Iterator iters = ele.elementIterator(); iters.hasNext();) {
					Element TXNSEle = (Element) iters.next();
					for (Iterator TXNSIters = TXNSEle.elementIterator(); TXNSIters.hasNext();) {
						Element headerEle = (Element) TXNSIters.next();
						Map map = headerXmltoMap(headerEle.asXML());
						list.add(map);
						map = null;
					}
				}
			}
			nodesElement = null;
			document = null;
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <Header><txn_header_id>10401</txn_header_id>...  
		 *			<Lines><Line><mo_line_id>10562</mo_line_id> ...</Line>
		 *					...
		 *			</Lines>
		 *		</Header>
	 * @param xml
	 * @return
	 */
	public static Map headerXmltoMap(String xml) {
		try {
			Map map = new HashMap();
			Document document = DocumentHelper.parseText(xml);
			Element nodeElement = document.getRootElement();
			List node = nodeElement.elements();
			for (Iterator it = node.iterator(); it.hasNext();) {
				Element elm = (Element) it.next();
				if (elm.getName().equals("LINES")) {
					List list = lineXmltoList(elm.asXML());
					map.put("LINES", list);
				}else {
					map.put(elm.getName(), elm.getText());
				}
				elm = null;
			}
			node = null;
			nodeElement = null;
			document = null;
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * xml to list xml <nodes><node><key label="key1">value1</key><key
	 * label="key2">value2</key>......</node><node><key
	 * label="key1">value1</key><key
	 * label="key2">value2</key>......</node></nodes>
	 * 
	 * @param xml
	 * @return
	 */
	public static List lineXmltoList(String xml) {
		try {
			List<Map> list = new ArrayList<Map>();
			Document document = DocumentHelper.parseText(xml);
			Element nodesElement = document.getRootElement();
			
			// 获取单个子节点
			for (Iterator iters = nodesElement.elementIterator(); iters.hasNext();) {
				Element itemEle = (Element) iters.next();
				Map map = xmltoMap(itemEle.asXML());
				list.add(map);
				map = null;
			}
			nodesElement = null;
			document = null;
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	
}
