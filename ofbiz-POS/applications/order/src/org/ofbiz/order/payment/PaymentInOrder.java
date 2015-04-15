package org.ofbiz.order.payment;

import java.util.List;

public class PaymentInOrder {
private PaymentInOrderHeader header;
private List<PaymentInOrderDtl> detail;
public PaymentInOrderHeader getHeader() {
	return header;
}
public void setHeader(PaymentInOrderHeader header) {
	this.header = header;
}
public List<PaymentInOrderDtl> getDetail() {
	return detail;
}
public void setDetail(List<PaymentInOrderDtl> detail) {
	this.detail = detail;
}
@Override
public String toString() {
	return "PaymentInOrder [header=" + header + ", detail=" + detail + "]";
}

}
