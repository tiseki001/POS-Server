package org.ofbiz.order.refundorder;

import java.util.List;

public class RefundOrder {
private RefundOrderHeader header;
private List<RefundOrderDtl> detail;
public RefundOrderHeader getHeader() {
	return header;
}
public void setHeader(RefundOrderHeader header) {
	this.header = header;
}
public List<RefundOrderDtl> getDetail() {
	return detail;
}
public void setDetail(List<RefundOrderDtl> detail) {
	this.detail = detail;
}
@Override
public String toString() {
	return "RefundOrder [header=" + header + ", detail=" + detail + "]";
}

}
