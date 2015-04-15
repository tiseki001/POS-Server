package org.ofbiz.order.salesoutwhsorder;

import java.util.List;

public class SalesOutWhsOrder {
private  SalesOutWhsOrderHeader header;
private List<SalesOutWhsOrderDtl> item;


public List<SalesOutWhsOrderDtl> getItem() {
	return item;
}
public void setItem(List<SalesOutWhsOrderDtl> item) {
	this.item = item;
}
public SalesOutWhsOrderHeader getHeader() {
	return header;
}
public void setHeader(SalesOutWhsOrderHeader header) {
	this.header = header;
}




}
