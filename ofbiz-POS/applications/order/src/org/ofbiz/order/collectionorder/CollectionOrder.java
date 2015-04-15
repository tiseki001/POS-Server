package org.ofbiz.order.collectionorder;

import java.util.Arrays;
import java.util.List;

public class CollectionOrder {
private CollectionOrderHeader header;
private List<CollectionOrderDtl>  detail;
public CollectionOrderHeader getHeader() {
	return header;
}
public void setHeader(CollectionOrderHeader header) {
	this.header = header;
}
public List<CollectionOrderDtl> getDetail() {
	return detail;
}
public void setDetail(List<CollectionOrderDtl> detail) {
	this.detail = detail;
}
@Override
public String toString() {
	return "CollectionOrder [header=" + header + ", detail=" + detail + "]";
}

}
