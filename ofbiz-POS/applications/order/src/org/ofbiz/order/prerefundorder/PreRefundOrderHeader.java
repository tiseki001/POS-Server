package org.ofbiz.order.prerefundorder;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class PreRefundOrderHeader {
	private String    docId;				 //收款单ID
	private Long      docNo;				 //收款单号码（纯数字）
	private String    partyId;				 //公司ID
	private String    storeId;				 //门店ID	
	private String    baseEntry;		     //上级订单号(退订单)
	private String    extSystemNo;			 //外部系统编码
	private String    extDocNo;			     //外部单据编码
	private Timestamp postingDate;			 //过账日期
	private String    docStatus;			 //单据状态(草稿/确定/批准/已清/作废)
	private String    memo;				      //备注信息
	private String    createUserId;		      //创建用户ID		
	private Timestamp createDocDate;		 //创建日期
	private String    lastUpdateUserId;	     //最后更新用户ID
	private Timestamp lastUpdateDocDate;	 //最后更新日期
	private BigDecimal amount;				 //退款额
	private String    approvalUserId;		 //审批人
	private Timestamp approvalDate;		     //审批时间
	private String    isSync;			     //单据同步状态	
	private Long      printNum;
	private String    attrName1;             //自定义字段1
	private String    attrName2;		     //自定义字段2
	private String    attrName3;		     //自定义字段3			
	private String    attrName4;			 //自定义字段4		
	private String    attrName5;			 //自定义字段5
	private String    attrName6;			 //自定义字段6
	private String    attrName7;		 	 //自定义字段7
	private String    attrName8;            //自定义字段8
	private String    attrName9;  			 //自定义字段9
	public String getDocId() {
		return docId;
	}
	public void setDocId(String docId) {
		this.docId = docId;
	}
	
	public Long getDocNo() {
		return docNo;
	}
	public void setDocNo(Long docNo) {
		this.docNo = docNo;
	}
	public String getPartyId() {
		return partyId;
	}
	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getBaseEntry() {
		return baseEntry;
	}
	public void setBaseEntry(String baseEntry) {
		this.baseEntry = baseEntry;
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
	public String getCreateUserId() {
		return createUserId;
	}
	public void setCreateUserId(String createUserId) {
		this.createUserId = createUserId;
	}
	public Timestamp getCreateDocDate() {
		return createDocDate;
	}
	public void setCreateDocDate(Timestamp createDocDate) {
		this.createDocDate = createDocDate;
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
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getApprovalUserId() {
		return approvalUserId;
	}
	public void setApprovalUserId(String approvalUserId) {
		this.approvalUserId = approvalUserId;
	}
	public Timestamp getApprovalDate() {
		return approvalDate;
	}
	public void setApprovalDate(Timestamp approvalDate) {
		this.approvalDate = approvalDate;
	}
	public String getIsSync() {
		return isSync;
	}
	public void setIsSync(String isSync) {
		this.isSync = isSync;
	}
	public String getAttrName1() {
		return attrName1;
	}
	public void setAttrName1(String attrName1) {
		this.attrName1 = attrName1;
	}
	public String getAttrName2() {
		return attrName2;
	}
	public void setAttrName2(String attrName2) {
		this.attrName2 = attrName2;
	}
	public String getAttrName3() {
		return attrName3;
	}
	public void setAttrName3(String attrName3) {
		this.attrName3 = attrName3;
	}
	public String getAttrName4() {
		return attrName4;
	}
	public void setAttrName4(String attrName4) {
		this.attrName4 = attrName4;
	}
	public String getAttrName5() {
		return attrName5;
	}
	public void setAttrName5(String attrName5) {
		this.attrName5 = attrName5;
	}
	public String getAttrName6() {
		return attrName6;
	}
	public void setAttrName6(String attrName6) {
		this.attrName6 = attrName6;
	}
	public String getAttrName7() {
		return attrName7;
	}
	public void setAttrName7(String attrName7) {
		this.attrName7 = attrName7;
	}
	public String getAttrName8() {
		return attrName8;
	}
	public void setAttrName8(String attrName8) {
		this.attrName8 = attrName8;
	}
	public String getAttrName9() {
		return attrName9;
	}
	public void setAttrName9(String attrName9) {
		this.attrName9 = attrName9;
	}
	public Long getPrintNum() {
		return printNum;
	}
	public void setPrintNum(Long printNum) {
		this.printNum = printNum;
	}

}
