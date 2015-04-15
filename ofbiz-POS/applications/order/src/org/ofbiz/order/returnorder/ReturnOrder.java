package org.ofbiz.order.returnorder;

import java.util.List;

public class ReturnOrder {
	private ReturnOrderHeader header;
	private List<ReturnOrderDtl> detail;
	public ReturnOrderHeader getHeader() {
		return header;
	}
	public void setHeader(ReturnOrderHeader header) {
		this.header = header;
	}
	public List<ReturnOrderDtl> getDetail() {
		return detail;
	}
	public void setDetail(List<ReturnOrderDtl> detail) {
		this.detail = detail;
	}
}
