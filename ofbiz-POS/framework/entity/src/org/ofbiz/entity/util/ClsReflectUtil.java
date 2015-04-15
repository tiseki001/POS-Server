package org.ofbiz.entity.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClsReflectUtil {
	
	@SuppressWarnings({ "rawtypes"})
	public static Class[] get2ClassMap(Class clazz,Map<String, Class> classMap) {
		getClassMap(clazz,classMap);
		if (classMap.size()!=0){
			Map<String, Class> classMapsub = new HashMap<String, Class>();
			for(Map.Entry<String, Class> entry: classMap.entrySet()) {
				classMapsub.put(entry.getKey(), entry.getValue());
			}
			Map<String, Class> classMapsub2 = new HashMap<String, Class>();
			while(true) {
				classMapsub2.clear();
				for(Map.Entry<String, Class> entry: classMapsub.entrySet()) {
					getClassMap(entry.getValue(),classMapsub2);
				}
				if (classMapsub2.size()==0)
					break;
				classMapsub.clear();
				for(Map.Entry<String, Class> entry: classMapsub2.entrySet()) {
					classMapsub.put(entry.getKey(), entry.getValue());
					classMap.put(entry.getKey(), entry.getValue());
				}
			}
		}

		Class[] bindClsArray = new Class[classMap.size()+1];
		bindClsArray[0]=clazz;
    	int i=1;
    	for (Class value : classMap.values()) {
    		bindClsArray[i] = value;
    		i++;
    	}
		return bindClsArray;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void getClassMap(Class clazz,Map<String, Class> classMap) {

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
				}   
			}   
			if(fieldClazz.isAssignableFrom(Map.class)) //【2】  
			{   
				Type fc = f.getGenericType(); // 关键的地方，如果是Map类型，得到其Generic的类型  
				if(fc == null) continue;  
				if(fc instanceof ParameterizedType) // 【3】如果是泛型参数的类型   
				{   
					ParameterizedType pt = (ParameterizedType) fc;  
					Class genericClazz = (Class)pt.getActualTypeArguments()[1]; //【4】 得到泛型里的class类型对象。  
					classMap.put(f.getName(), genericClazz);  
				}   
			}   
		}
	}
}