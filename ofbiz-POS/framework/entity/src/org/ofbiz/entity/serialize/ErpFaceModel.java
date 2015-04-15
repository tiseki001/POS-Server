package org.ofbiz.entity.serialize;

import java.util.List;

import org.ofbiz.entity.GenericValue;

public class ErpFaceModel {
	private GenericValue header;
	
	private List<GenericValue> item;
	
	public GenericValue getHeader() {
		return header;
	}
	public void setHeader(GenericValue header) {
		this.header = header;
	}
	public List<GenericValue> getItem() {
		return item;
	}
	public void setItem(List<GenericValue> item) {
		this.item = item;
	}
	
	
}
