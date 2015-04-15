package org.ofbiz.face.erpface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;

import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.serialize.ErpFaceModel;
import org.ofbiz.entity.serialize.ErpFaceModelMuti;



public class ErpFaceModelUtil {
	
	public static List<ErpFaceModelMuti> ErpFaceMap2Model(List<GenericValue> headers, Map<String, Object> itemMaps, Set<String> key) {
		List<ErpFaceModelMuti> ErpFaceModel = new ArrayList<ErpFaceModelMuti>();
		boolean flag = true;
		int startIndex = 0;
		
		for (GenericValue header : headers) {
			ErpFaceModelMuti model = new ErpFaceModelMuti();
			Map<String, Object> itemMapModels = FastMap.newInstance();
			model.setHeader(header);
			for (String string : itemMaps.keySet()) {
				List<GenericValue> itemModels = new ArrayList<GenericValue>();
				List<GenericValue> items = (List<GenericValue>)itemMaps.get(string);
				for (GenericValue item : items) {
					for (String str : key) {
						if (!header.get(str).equals(item.get(str))) {
							flag = false;
						}
					}
					if (flag) {
						itemModels.add(item);
						items.remove(item);
					}
					flag = true;
				}
				itemMapModels.put(string, itemModels);
			}
			model.setItems(itemMapModels);
			ErpFaceModel.add(model);
		}
		
		return ErpFaceModel;
		
	}
	
	public static List<ErpFaceModel> ErpFaceList2Model(List<GenericValue> headers, List<GenericValue> items, Set<String> key) {
		List<ErpFaceModel> ErpFaceModel = new ArrayList<ErpFaceModel>();
		boolean flag = true;
		int startIndex = 0;
		
		for (GenericValue header : headers) {
			List<GenericValue> itemModels = new ArrayList<GenericValue>();
			ErpFaceModel model = new ErpFaceModel();
			// for (int i = 0; i < items.size(); i++) {
				// GenericValue item = items.get(i);
			for (GenericValue item : items) {
				for (String str : key) {
					if (!header.get(str).equals(item.get(str))) {
						flag = false;
					}
				}
				if (flag) {
					itemModels.add(item);
					items.remove(item);
				}
				flag = true;
				// startIndex ++;
				// break;
			}
			model.setHeader(header);
			model.setItem(itemModels);
			ErpFaceModel.add(model);
			// itemModels.clear();
		}
		
		return ErpFaceModel;
		
	}
	
	


}
