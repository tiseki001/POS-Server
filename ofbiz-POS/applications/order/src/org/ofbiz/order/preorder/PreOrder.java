package org.ofbiz.order.preorder;

import java.util.List;

public class PreOrder {
	private PreOrderHeader header;
	private List<PreOrderDtl> detail;
	public PreOrderHeader getHeader() {
		return header;
	}
	public void setHeader(PreOrderHeader header) {
		this.header = header;
	}
	public List<PreOrderDtl> getDetail() {
		return detail;
	}
	public void setDetail(List<PreOrderDtl> detail) {
		this.detail = detail;
	}

}
