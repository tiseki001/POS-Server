package org.ofbiz.order.salesoutwhsorder;

import java.math.BigDecimal;
import java.sql.Timestamp;


public class SalesOutWhsOrderDtl {
	private String docId;				
	private String baseEntry;				
	private Long lineNo;     
	private String memo;
	private Long quantity;	//数量出库的
	private String isSequence;
	private String idValue;
	private String productId;
	private String sequenceId;
	private String facilityId;
	private String facilityIdTo;
	private Long baseLineNo;    //baseentry明细中的LineNo
	private Long unLockedQuantity;
	private String productName;			//商品 名称
	private Long  saleQuantity;     
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public String getBaseEntry() {
		return baseEntry;
	}
	public void setBaseEntry(String baseEntry) {
		this.baseEntry = baseEntry;
	}
	public Long getLineNo() {
		return lineNo;
	}
	public void setLineNo(Long lineNo) {
		this.lineNo = lineNo;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Long getQuantity() {
		return quantity;
	}
	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
	public String getIsSequence() {
		return isSequence;
	}
	public void setIsSequence(String isSequence) {
		this.isSequence = isSequence;
	}
	public String getIdValue() {
		return idValue;
	}
	public void setIdValue(String idValue) {
		this.idValue = idValue;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getSequenceId() {
		return sequenceId;
	}
	public void setSequenceId(String sequenceId) {
		this.sequenceId = sequenceId;
	}
	public String getFacilityId() {
		return facilityId;
	}
	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}
	public String getFacilityIdTo() {
		return facilityIdTo;
	}
	public void setFacilityIdTo(String facilityIdTo) {
		this.facilityIdTo = facilityIdTo;
	}
	public Long getBaseLineNo() {
		return baseLineNo;
	}
	public void setBaseLineNo(Long baseLineNo) {
		this.baseLineNo = baseLineNo;
	}
	public Long getUnLockedQuantity() {
		return unLockedQuantity;
	}
	public void setUnLockedQuantity(Long unLockedQuantity) {
		this.unLockedQuantity = unLockedQuantity;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public Long getSaleQuantity() {
		return saleQuantity;
	}
	public void setSaleQuantity(Long saleQuantity) {
		this.saleQuantity = saleQuantity;
	}

	
	
}
