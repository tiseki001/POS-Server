package org.ofbiz.service.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilGenerics;

import javolution.util.FastMap;

public class ReflectUtil {
	/**
	 * 由反射 取得 model属性 ，组装成对应的map
	 * 注：model属性与所要get("field") 条件一一对应
	 * @param model
	 * @param partyList
	 * @return
	 */
	public static void getMapByFeflection(Object modelFrom, Object modelTo){
		Field[] field = modelFrom.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
        try {
            for (int j = 0; j < field.length; j++) { // 遍历所有属性
                String name = field[j].getName(); // 获取属性的名字
                name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
                String type = field[j].getGenericType().toString(); // 获取属性的类型
                //if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
                    Method m = modelFrom.getClass().getMethod("get" + name);
                    String value = (String) m.invoke(modelFrom); // 调用getter方法获取属性值
                    if (null != value) {
                        m = modelTo.getClass().getMethod("set"+name,field[j].getGenericType().getClass());
                        if (null != m)
                        	m.invoke(modelTo, value);
                    }
                //}

            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
	}	
	
	public static Map<String, String> getData2Map(Object modelFrom){
		
		Map<String, String> mapResult = FastMap.newInstance();;
		Field[] field = modelFrom.getClass().getDeclaredFields(); // 获取实体类的所有属性，返回Field数组
        try {
            for (int j = 0; j < field.length; j++) { // 遍历所有属性
                String name = field[j].getName(); // 获取属性的名字
                name = name.substring(0, 1).toUpperCase() + name.substring(1); // 将属性的首字符大写，方便构造get，set方法
                String type = field[j].getGenericType().toString(); // 获取属性的类型
                //if (type.equals("class java.lang.String")) { // 如果type是类类型，则前面包含"class "，后面跟类名
                    Method m = modelFrom.getClass().getMethod("get" + name);
                    String value = (String) m.invoke(modelFrom); // 调用getter方法获取属性值
                    if (null != value) {
                    	mapResult.put(name, value);
                    }
                //}

            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return mapResult;
	}	
}