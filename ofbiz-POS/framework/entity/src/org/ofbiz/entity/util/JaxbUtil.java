package org.ofbiz.entity.util;
  
import java.io.StringReader;  
import java.io.StringWriter;  

import java.util.HashMap;
import java.util.List;
import java.util.Map;
  
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;  
import javax.xml.bind.Marshaller;  
import javax.xml.bind.Unmarshaller;  

 
/** 
 * Jaxb2工具类 
 * @create      2013-3-29 下午2:40:14 
 */  
public class JaxbUtil {  
  
    /** 
     * JavaBean转换成xml 
     * 默认编码UTF-8 
     * @param obj 
     * @param writer 
     * @return  
     */  
    public static String convertToXml(Object obj) {  
        return convertToXml(obj, "UTF-8");  
    }  

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static String convertListToXml(Object obj) {
    	Class[] bindClsArray={};
    	Class objCls = obj.getClass().getSuperclass();
    	RootElement root = new RootElement();
    	Map<String, Class> classMap = new HashMap<String, Class>();
    	if (objCls.equals(java.util.AbstractList.class)){
    		List objLst = (List)obj;
    		if (objLst.size()!=0) {
	    		bindClsArray = ClsReflectUtil.get2ClassMap(objLst.get(0).getClass(),classMap);
	    		root.setArrayList((List)obj);
    		}
    	} else if (objCls.equals(java.util.AbstractMap.class)){

    		for (Object value : ((Map)obj).values()) {
	    		bindClsArray = ClsReflectUtil.get2ClassMap(value.getClass(),classMap);
	    		root.setHashMap((Map)obj);
	    		break;
			}
    	}

        return convertToXml(root, "UTF-8",bindClsArray);  
    }  


    /** 
     * JavaBean转换成xml 
     * @param obj 
     * @param encoding  
     * @return  
     */  

    @SuppressWarnings({ "rawtypes" })
    public static String convertToXml(Object obj, String encoding, Class... bindCls) {  
        String result = null;  
        try {

        	int nSize = bindCls.length;

        	Class[] bindClsArray = new Class[nSize+1];
        	bindClsArray[0]=obj.getClass();
        	for(int i=0;i<bindCls.length;i++){
        		bindClsArray[i+1]=bindCls[i];
        	}

            //JAXBContext context = JAXBContext.newInstance(obj.getClass(),Province.class);  
            JAXBContext context = JAXBContext.newInstance(bindClsArray);
            Marshaller marshaller = context.createMarshaller();  
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);  
            marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);  

            StringWriter writer = new StringWriter();
        	if (nSize==0) {
            	JAXB.marshal(obj, writer);
        	} else {
        		marshaller.marshal(obj, writer);  
        	}
            result = writer.toString();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return result;  
    }  
  
    /** 
     * xml转换成JavaBean 
     * @param xml 
     * @param c 
     * @return 
     */  
    @SuppressWarnings("unchecked")  
    public static <T> T convertToJavaBean(String xml, Class<T> c) {  
        T t = null;  
        try {  
            JAXBContext context = JAXBContext.newInstance(c);  
            Unmarshaller unmarshaller = context.createUnmarshaller();  
            t = (T) unmarshaller.unmarshal(new StringReader(xml));  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return t;  
    }

    @SuppressWarnings({ "rawtypes" })
    public static List<Object> convertToJavaBeanList(String xml,Class ... bindCls) {  
    	List<Object> t = null;  
        try {  
        	int nSize = bindCls.length;
        	Class[] bindClsArray = new Class[nSize+1];
        	bindClsArray[0]=RootElement.class;
        	for(int i=0;i<bindCls.length;i++){
        		bindClsArray[i+1]=bindCls[i];
        	}
            JAXBContext context = JAXBContext.newInstance(bindClsArray);  
            Unmarshaller unmarshaller = context.createUnmarshaller();  
            RootElement result = (RootElement) unmarshaller.unmarshal(new StringReader(xml));
            t = result.getArrayList();
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return t;  
    }

    @SuppressWarnings({ "rawtypes" })
    public static Map<String,Object> convertToJavaBeanMap(String xml,Class ... bindCls) {  
    	Map<String,Object> t = null;  
        try {  
        	int nSize = bindCls.length;
        	Class[] bindClsArray = new Class[nSize+1];
        	bindClsArray[0]=RootElement.class;
        	for(int i=0;i<bindCls.length;i++){
        		bindClsArray[i+1]=bindCls[i];
        	}
            JAXBContext context = JAXBContext.newInstance(bindClsArray);  
            Unmarshaller unmarshaller = context.createUnmarshaller();  
            RootElement result = (RootElement) unmarshaller.unmarshal(new StringReader(xml));
            t = result.getHashMap();
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
        return t;  
    }
}  