package org.ofbiz.service.util;

import java.util.ArrayList;

import java.util.HashMap;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;

import net.sf.json.JSONObject;

import net.sf.json.JSONSerializer;
import net.sf.json.JsonConfig;

import net.sf.json.util.JSONUtils;
import net.sf.json.xml.XMLSerializer;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;


public class JSONUtil {

	public static String listToJson(List<?> list){
		String json ="";
		if(null == list || list.size() == 0){
			 return "";
		}
		
		json = JSONSerializer.toJSON(list).toString();
		
		return json;
	}
	
	public static Object json2Bean(String jsonStr,Class cls){
		Map<String, Class> classMap = new HashMap<String, Class>();
		get2ClassMap(cls,classMap);
		JSONObject jsonObject=json2Jsonbj(jsonStr);
		return JSONObject.toBean(jsonObject,cls,classMap);
	}

	public static Map<String, Object> json2Map(String jsonStr){
        Map<String, Object> map = new HashMap<String, Object>();
        //最外层解析
        JSONObject json = JSONObject.fromObject(jsonStr);
        for(Object k : json.keySet()){
            Object v = json.get(k);
            if (null != v && !v.toString().equals("null") && !v.toString().isEmpty()) {
            	map.put(k.toString(), v);
            }
        }
        return map;
    }

	public static JSONObject json2Jsonbj(String jsonStr){
		String[] formats={"yyyy-MM-dd HH:mm:ss.SSS","yyyy-MM-dd"};
		JSONUtils.getMorpherRegistry().registerMorpher(new TimestampMorpher(formats));
		
		return JSONObject.fromObject(jsonStr);
	}
	
	public static JSONObject bean2Json(Object obj){
		JsonConfig config = new JsonConfig();
		//config.registerJsonBeanProcessor(java.sql.Timestamp.class,  
	    //        new JsDateJsonBeanProcessor());
		config.registerJsonValueProcessor(java.sql.Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss.SSS"));
		config.registerJsonValueProcessor(java.sql.Date.class, new DateJsonValueProcessor("yyyy-MM-dd"));
		return JSONObject.fromObject(obj, config); 

	}
	/*@SuppressWarnings("rawtypes")
	public static Object json2Bean(JSONObject jsonObj, Class clazz, Map<String, String> nameTransMap, Map<String, Class> identityListMap){

		JsonConfig jsonConfig = new JsonConfig();
		jsonConfig.setIgnoreDefaultExcludes(false);
		if (null != nameTransMap)
			jsonConfig.setJavaIdentifierTransformer(new JavamyTransformer(nameTransMap,false));
	    jsonConfig.setRootClass(clazz);
		if (null != identityListMap)
			jsonConfig.setClassMap(identityListMap);
		return JSONObject.toBean(jsonObj, jsonConfig);
	}

	public static class JavamyTransformer extends JavaIdentifierTransformer {
		private static HashMap<String, String> replaceMap;
		
		JavamyTransformer(Map<String, String> map,boolean bFormTo) {
			replaceMap = new HashMap<String, String>();
			for(Map.Entry<String, String> entry: map.entrySet()) {
				if (bFormTo)
					replaceMap.put(entry.getKey(), entry.getValue());
				else
					replaceMap.put(entry.getValue(), entry.getKey());
			}			
		}
		@Override
		public String transformToJavaIdentifier(String str) {
			for(Map.Entry<String, String> entry: replaceMap.entrySet()) {
				if (str.equals(entry.getKey()))
					return entry.getValue();
			}
			return str;

		}
	}*/

	public static void main(String []args){

		/*String v1 = "2014-08-01 12:37:07";
		java.sql.Timestamp ob1 = java.sql.Timestamp.valueOf(v1);
		System.out.print(ob1.toString());
		java.sql.Timestamp objResult = ConvertUtil.convertStringToTimeStamp(v1);
		System.out.print(objResult);
		String s1 = new String("AUTOMATED_AGENT");
		String[] strFields = s1.split("\\|");
		System.out.println(strFields.length);
		JSONObject retJsonObj = new JSONObject();
        retJsonObj.put("syncTime", ConvertUtil.convertDateToString(new Date()));
        String ss = retJsonObj.toString();
        JSONObject jsonRoot = JSONObject.fromObject(ss);
        String jsonTime = jsonRoot.getString("syncTime");
        System.out.print(jsonTime);*/
		/*Customer map = new Customer();
		map.setAge("12,3"); 
		map.setAu1(BigDecimal.valueOf(4.566));
		map.setSp1(1234);
		map.setName("张,三");
		//List<StrategyObj> list1 = new ArrayList<StrategyObj>();
		StrategyObj st1 = new StrategyObj();
		st1.setAudiogain(BigDecimal.valueOf(234.45));
		st1.setoTime(java.sql.Timestamp.valueOf("2014-09-01 12:37:07.567"));
		
		List<HeaderObj> headerList = new ArrayList<HeaderObj>();
		HeaderObj header1 = new HeaderObj();
		header1.setAudiogains(BigDecimal.valueOf(23433.45));
		header1.setOoTime(java.sql.Timestamp.valueOf("2014-09-05 12:37:07.567"));
		headerList.add(header1);
		st1.setHeaderList(headerList);
		//map.setHd(header1);
		map.setSt1(st1);
		StrategyNObj st2 = new StrategyNObj();
		st2.setAudiogain(BigDecimal.valueOf(234.45));
		st2.setoTime(java.sql.Timestamp.valueOf("2014-09-03 12:37:07.567"));
		
		List<HeaderNObj> headerList2 = new ArrayList<HeaderNObj>();
		HeaderNObj header2 = new HeaderNObj();
		header2.setAudiogain(BigDecimal.valueOf(9999.45));
		header2.setoTime(java.sql.Timestamp.valueOf("2014-09-07 12:37:07.567"));
		headerList2.add(header2);
		st2.setHeaderList(headerList2);
		map.setSt2(st2);
		
		//Map<String,Object> mp1 = new HashMap<String,Object>();
		//mp1.put("11", "1,\"11");
		
		//st1.settMap(mp1);
		//list1.add(st1);
		//map.setDate1(java.sql.Timestamp.valueOf("2014-08-01 12:37:07.678"));
		//map.setList1(list1);
		JSONObject jsonObject = bean2Json(map);

		System.out.println(jsonObject);
		
		Object sss = json2Bean(jsonObject.toString(),Customer.class);
		System.out.println(sss.toString());*/
		
		/*Map<String,Object> mp1 = new HashMap<String,Object>();
		mp1.put("11", "2014-08-01 12:37:07.678");
		mp1.put("12", null);
		JSONObject jsonObject = bean2Json(mp1);
		System.out.println(mp1.toString());
		Map<String,Object> sss = json2Map(jsonObject.toString());
		System.out.println(sss.toString());*/
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("start", new Date().toString());
		System.out.println(jsonObj.toString());
		List<Object> list =new ArrayList<Object>();

		/*list.add("123");
		list.add("qaz");
		list.add("wsx");*/

		list.add(UtilMisc.toMap("partyId", "partyId1", 
                "firstName", "firstName",
                "lastName", "lastName"));
		//System.out.println(list);
		jsonObj.put("data",JSONSerializer.toJSON(list).toString());
		
		String xml =  new XMLSerializer().write(jsonObj);
		System.out.println(xml);
		//System.out.print(listToJson(list));
	}
	
	@SuppressWarnings({ "rawtypes" })
	private static void get2ClassMap(Class clazz,Map<String, Class> classMap) {
		Map<String, Class> clsBeansMap = getClassMap(clazz,classMap);
		//bean里面的子bean类型
		Map<String, Class> clsBeanMap = new HashMap<String, Class>();

		//bean里面集合的bean类型
		Map<String, Class> classMapsub = new HashMap<String, Class>();
		while(clsBeansMap.size()!=0){
			for(Map.Entry<String, Class> entry: clsBeansMap.entrySet()) {
				clsBeanMap.put(entry.getKey(), entry.getValue());
			}
			clsBeansMap.clear();
			for(Map.Entry<String, Class> entry: clsBeanMap.entrySet()) {
				Map<String, Class> clsTmpBeanMap = getClassMap(entry.getValue(),classMapsub);
				for(Map.Entry<String, Class> entryTmp: clsTmpBeanMap.entrySet()) {
					clsBeansMap.put(entryTmp.getKey(), entryTmp.getValue());
				}
			}
			clsBeanMap.clear();
		}
		for(Map.Entry<String, Class> entry: classMapsub.entrySet()) {
			classMap.put(entry.getKey(), entry.getValue());
		}
	}
	
	public static boolean isJavaClass(Class<?> clz) {  
	    return clz != null && clz.getClassLoader() == null;  
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static Map<String, Class> getClassMap(Class clazz,Map<String, Class> classMap) {
		Map<String, Class> classRetMap = new HashMap<String, Class>();
		Field[] fs = clazz.getDeclaredFields(); // 得到所有的fields  
		for(Field f : fs)   
		{   
			Class fieldClazz = f.getType(); // 得到field的class及类型全路径  
			if(fieldClazz.isPrimitive())  continue;  //【1】 //判断是否为基本类型  
			if(fieldClazz.getName().startsWith("java.lang")) continue; //getName()返回field的类型全路径；  
			if(fieldClazz.isAssignableFrom(List.class)) //【2】  
			{   
				Type fc = f.getGenericType(); // 关键的地方，如果是List类型，得到其Generic的类型  
				if(fc == null) continue;  
				if(fc instanceof ParameterizedType) // 【3】如果是泛型参数的类型   
				{   
					ParameterizedType pt = (ParameterizedType) fc;  
					Class genericClazz = (Class)pt.getActualTypeArguments()[0]; //【4】 得到泛型里的class类型对象。  
					classMap.put(f.getName(), genericClazz);
					classRetMap.put(f.getName(), genericClazz);
				}   
			}
			if (!isJavaClass(fieldClazz)){
				classRetMap.put(f.getName(), fieldClazz);  
			}
		}
		return classRetMap;
	}

}