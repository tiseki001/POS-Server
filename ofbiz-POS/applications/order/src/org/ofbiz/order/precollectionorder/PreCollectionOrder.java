package org.ofbiz.order.precollectionorder;

import java.util.List;

public class PreCollectionOrder {
	private PreCollectionOrderHeader header;
	private List<PreCollectionOrderDtl> detail;
	public PreCollectionOrderHeader getHeader() {
		return header;
	}
	public void setHeader(PreCollectionOrderHeader header) {
		this.header = header;
	}
	public List<PreCollectionOrderDtl> getDetail() {
		return detail;
	}
	public void setDetail(List<PreCollectionOrderDtl> detail) {
		this.detail = detail;
	}

}
