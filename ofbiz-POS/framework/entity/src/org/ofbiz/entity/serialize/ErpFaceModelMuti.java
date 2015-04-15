package org.ofbiz.entity.serialize;

import java.util.Map;

import org.ofbiz.entity.GenericValue;

public class ErpFaceModelMuti {
private GenericValue header;
	
	
	
	public GenericValue getHeader() {
		return header;
	}
	public void setHeader(GenericValue header) {
		this.header = header;
	}
	
	
	private Map<String, Object> items;

	public Map<String, Object> getItems() {
		return items;
	}
	public void setItems(Map<String, Object> items) {
		this.items = items;
	}
}
