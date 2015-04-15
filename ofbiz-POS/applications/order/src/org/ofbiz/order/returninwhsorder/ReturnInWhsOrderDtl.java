package org.ofbiz.order.returninwhsorder;

public class ReturnInWhsOrderDtl {
	private String docId;				
	private String baseEntry;	
	private String baseLineNo; 
	private String lineNo;   
	private String memo;
	private Long   quantity;	//数量
	private String isSequence;
	private String idValue;
	private String productId;
	private String sequenceId;
	private String facilityId;
	private String facilityIdFrom;
	private String productName;
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

	public String getBaseLineNo() {
		return baseLineNo;
	}
	public void setBaseLineNo(String baseLineNo) {
		this.baseLineNo = baseLineNo;
	}
	public String getLineNo() {
		return lineNo;
	}
	public void setLineNo(String lineNo) {
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
	public String getFacilityIdFrom() {
		return facilityIdFrom;
	}
	public void setFacilityIdFrom(String facilityIdFrom) {
		this.facilityIdFrom = facilityIdFrom;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}

}
