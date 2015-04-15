package org.ofbiz.order.backorder;

import java.util.List;

public class BackOrder {
	private BackOrderHeader header;
	private List<BackOrderDtl> detail;
	public BackOrderHeader getHeader() {
		return header;
	}
	public void setHeader(BackOrderHeader header) {
		this.header = header;
	}
	public List<BackOrderDtl> getDetail() {
		return detail;
	}
	public void setDetail(List<BackOrderDtl> detail) {
		this.detail = detail;
	}

}
