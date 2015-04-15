package org.ofbiz.order.parameter;

import java.sql.Timestamp;

public class Parameter {
	private String   docId ;
	private String   docStatus; // 订单状态
	private String   fundStatus;// 收款状态
	private String   backStatus;//退订状态
	private String   warehouseStatus;// 出库状态 、入库状态
	private String   returnStatus ;// 退货状态
	private String   refundStatus ;//退款状态
	private String   status;//退货入库状态
	private String   salesStatus;//预订单销售状态（未清/部分已清/已清）
	private String   lastUpdateUserId ;//更新用户
	private Timestamp lastUpdateDocDate;//更新时间
	private String   baseEntry;
	private String   orderType;//出库单类型
	private String   startDate;
	private String   endDate;
	private String   productStoreIdFrom;
	private String   productStoreId;
	private Timestamp  docDate;
	private String   updateUserId;
	private Timestamp updateDate;
	private String   header;
	private String   detail;
	private Long     lineNo;
	private String   memo;
	private String   exchangeStatus;
	private String   storeId;
	private String   postingDate;
	private String   partyId;
	private String   productId;
	private String   sequenceId;
	private Long     quantity;
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public String getDocStatus() {
		return docStatus;
	}
	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}
	public String getFundStatus() {
		return fundStatus;
	}
	public void setFundStatus(String fundStatus) {
		this.fundStatus = fundStatus;
	}
	public String getBackStatus() {
		return backStatus;
	}
	public void setBackStatus(String backStatus) {
		this.backStatus = backStatus;
	}
	public String getWarehouseStatus() {
		return warehouseStatus;
	}
	public void setWarehouseStatus(String warehouseStatus) {
		this.warehouseStatus = warehouseStatus;
	}
	public String getReturnStatus() {
		return returnStatus;
	}
	public void setReturnStatus(String returnStatus) {
		this.returnStatus = returnStatus;
	}
	
	public String getRefundStatus() {
		return refundStatus;
	}
	public void setRefundStatus(String refundStatus) {
		this.refundStatus = refundStatus;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getLastUpdateUserId() {
		return lastUpdateUserId;
	}
	public void setLastUpdateUserId(String lastUpdateUserId) {
		this.lastUpdateUserId = lastUpdateUserId;
	}
	public Timestamp getLastUpdateDocDate() {
		return lastUpdateDocDate;
	}
	public void setLastUpdateDocDate(Timestamp lastUpdateDocDate) {
		this.lastUpdateDocDate = lastUpdateDocDate;
	}
	public String getBaseEntry() {
		return baseEntry;
	}
	public void setBaseEntry(String baseEntry) {
		this.baseEntry = baseEntry;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getProductStoreIdFrom() {
		return productStoreIdFrom;
	}
	public void setProductStoreIdFrom(String productStoreIdFrom) {
		this.productStoreIdFrom = productStoreIdFrom;
	}
	public String getProductStoreId() {
		return productStoreId;
	}
	public void setProductStoreId(String productStoreId) {
		this.productStoreId = productStoreId;
	}
	public Timestamp getDocDate() {
		return docDate;
	}
	public void setDocDate(Timestamp docDate) {
		this.docDate = docDate;
	}
	public String getUpdateUserId() {
		return updateUserId;
	}
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}
	public Timestamp getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
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
	public String getExchangeStatus() {
		return exchangeStatus;
	}
	public void setExchangeStatus(String exchangeStatus) {
		this.exchangeStatus = exchangeStatus;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getPostingDate() {
		return postingDate;
	}
	public void setPostingDate(String postingDate) {
		this.postingDate = postingDate;
	}
	public String getPartyId() {
		return partyId;
	}
	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
	public String getSalesStatus() {
		return salesStatus;
	}
	public void setSalesStatus(String salesStatus) {
		this.salesStatus = salesStatus;
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
	public Long getQuantity() {
		return quantity;
	}
	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	
}
