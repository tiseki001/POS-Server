package org.ofbiz.order.returninwhsorder;

import java.util.List;

public class ReturnInWhsOrder {
	private ReturnInWhsOrderHeader header;
	private List<ReturnInWhsOrderDtl> item;
	public ReturnInWhsOrderHeader getHeader() {
		return header;
	}
	public void setHeader(ReturnInWhsOrderHeader header) {
		this.header = header;
	}
	public List<ReturnInWhsOrderDtl> getItem() {
		return item;
	}
	public void setItem(List<ReturnInWhsOrderDtl> item) {
		this.item = item;
	}
	
	
}
