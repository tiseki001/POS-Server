package org.ofbiz.order.prerefundorder;

import java.util.List;

public class PreRefundOrder {
	private PreRefundOrderHeader header;
	private List<PreRefundOrderDtl> detail;
	public PreRefundOrderHeader getHeader() {
		return header;
	}
	public void setHeader(PreRefundOrderHeader header) {
		this.header = header;
	}
	public List<PreRefundOrderDtl> getDetail() {
		return detail;
	}
	public void setDetail(List<PreRefundOrderDtl> detail) {
		this.detail = detail;
	}
	
}
