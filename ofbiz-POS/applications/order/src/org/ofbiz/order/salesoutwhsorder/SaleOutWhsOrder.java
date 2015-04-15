package org.ofbiz.order.salesoutwhsorder;

import java.util.List;

public class SaleOutWhsOrder {
	private  Object header;
	private List<SalesOutWhsOrderDtl> item;
	public Object getHeader() {
		return header;
	}
	public void setHeader(Object header) {
		this.header = header;
	}
	public List<SalesOutWhsOrderDtl> getItem() {
		return item;
	}
	public void setItem(List<SalesOutWhsOrderDtl> item) {
		this.item = item;
	}

}
