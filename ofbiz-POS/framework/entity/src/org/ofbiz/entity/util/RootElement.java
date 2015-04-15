package org.ofbiz.entity.util;

import java.util.List;  
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;  

  
/** 
 * @author      
 * @param <T>
 * @param <T>
 * @create
 */  

@XmlRootElement  
public class RootElement {
	
    private List<Object> arrayList; 
    private Map<String,Object> hashMap; 
 

    public List<Object> getArrayList() {  
        return arrayList;  
    }  
  

    public void setArrayList(List<Object> arrayList) {  
        this.arrayList = arrayList;  
    }  

    public Map<String,Object> getHashMap() {  
        return hashMap;  
    }  
  

    public void setHashMap(Map<String,Object> hashMap) {  
        this.hashMap = hashMap;  
    }  

    @Override  
    public String toString() {  
        return "Root [arrayList=" + arrayList + "hashMap=" + hashMap + "]";  
    }    
}  
