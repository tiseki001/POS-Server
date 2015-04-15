package org.ofbiz.order.returninwhsorder;

import java.sql.Timestamp;

public class ReturnInWhsOrderHeader {
	private String    docId;				 //退货入库单ID
	private String    extSystemNo;			 //外部系统编码
	private String    extDocNo;			 //外部单据编码
	private Timestamp docDate;             
	private Timestamp postingDate;	      //过账日期
	private String    docStatus;		  //单据状态(草稿/确定/批准/已清/作废)
	private String    memo;				 //备注信息
	private Timestamp updateDate;
	private String    status;            
	private String    isSync;			     //单据同步状态	
	private String    baseEntry;	
	private String    movementTypeId;      //移动类型
	private String    userId;
	private String    updateUserId;	    //最后更新用户ID
	private String    productStoreId;      //门店ID
	private String    productStoreIdFrom;  //源门面ID
	private String    partyId;
	private String    partyIdFrom;
	private String    partyIdTo;
	private Long      printNum;        //打印次数
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	public String getExtSystemNo() {
		return extSystemNo;
	}
	public void setExtSystemNo(String extSystemNo) {
		this.extSystemNo = extSystemNo;
	}
	public String getExtDocNo() {
		return extDocNo;
	}
	public void setExtDocNo(String extDocNo) {
		this.extDocNo = extDocNo;
	}
	public Timestamp getDocDate() {
		return docDate;
	}
	public void setDocDate(Timestamp docDate) {
		this.docDate = docDate;
	}
	public Timestamp getPostingDate() {
		return postingDate;
	}
	public void setPostingDate(Timestamp postingDate) {
		this.postingDate = postingDate;
	}
	public String getDocStatus() {
		return docStatus;
	}
	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public Timestamp getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getIsSync() {
		return isSync;
	}
	public void setIsSync(String isSync) {
		this.isSync = isSync;
	}
	public String getBaseEntry() {
		return baseEntry;
	}
	public void setBaseEntry(String baseEntry) {
		this.baseEntry = baseEntry;
	}
	public String getMovementTypeId() {
		return movementTypeId;
	}
	public void setMovementTypeId(String movementTypeId) {
		this.movementTypeId = movementTypeId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUpdateUserId() {
		return updateUserId;
	}
	public void setUpdateUserId(String updateUserId) {
		this.updateUserId = updateUserId;
	}
	public String getProductStoreId() {
		return productStoreId;
	}
	public void setProductStoreId(String productStoreId) {
		this.productStoreId = productStoreId;
	}
	public String getProductStoreIdFrom() {
		return productStoreIdFrom;
	}
	public void setProductStoreIdFrom(String productStoreIdFrom) {
		this.productStoreIdFrom = productStoreIdFrom;
	}
	public String getPartyId() {
		return partyId;
	}
	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
	public String getPartyIdFrom() {
		return partyIdFrom;
	}
	public void setPartyIdFrom(String partyIdFrom) {
		this.partyIdFrom = partyIdFrom;
	}
	public Long getPrintNum() {
		return printNum;
	}
	public void setPrintNum(Long printNum) {
		this.printNum = printNum;
	}
	public String getPartyIdTo() {
		return partyIdTo;
	}
	public void setPartyIdTo(String partyIdTo) {
		this.partyIdTo = partyIdTo;
	}
	
}
