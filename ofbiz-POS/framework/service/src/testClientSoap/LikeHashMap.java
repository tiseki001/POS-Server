package testClientSoap;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;

public class LikeHashMap extends HashMap {
	public Set keySet() {
		Set set = super.keySet();
		TreeSet tSet = null;
		if (set != null) {
			// 对已存在的key进行排序
			tSet = new TreeSet(set);
		}
		return tSet;
	}
	public List<Object> get(String key,boolean like) {
		List<Object> value = new ArrayList<Object>();
		//是否为模糊搜索
		if(like){
			List<String> keyList = new ArrayList<String>();
			TreeSet<String> treeSet = (TreeSet) this.keySet();
			for (String string : treeSet) {
				//通过排序后,key是有序的.
				if (key.indexOf(string) != -1) {
					keyList.add(string);
					value.add(this.get(string));
				} else if (key.indexOf(string) == -1 && keyList.size() == 0) {
					//当不包含这个key时而且key.size()等于0时,说明还没找到对应的key的开始
					continue;
				} else {
					//当不包含这个key时而且key.size()大于0时,说明对应的key到当前这个key已经结束.不必要在往下找
					break;
				}
			}
			keyList.clear();
			keyList=null;
		}else{
			value.add(this.get(key));
		}
		return value;
	}
 public static void main(String[] args) {
  LikeHashMap hMap = new LikeHashMap();
  /*for (int i = 0; i < 100000; i++) {
   hMap.put("A_"+i, "AAAAAA"+i);
  }*/
  hMap.put("TH_01dd", "11111111");
  hMap.put("TH_0d", "2222222");
  hMap.put("JG1dd", "333333");
  hMap.put("GHdd", "44444");
  hMap.put("SS11dd", "55555");
  long time=System.currentTimeMillis();
  System.out.println(hMap.get("GHdd11",true));
  System.out.println(hMap.get("TH_0d78",true));
  //System.out.println(System.currentTimeMillis()-time);
 }
}