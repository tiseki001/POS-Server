package org.ofbiz.order.saleorder;

import java.math.BigDecimal;
import java.sql.Timestamp;


public class SaleOrderHeader {

	private String docId;                //销售订单
	private Long  docNo; 				 //销售订单号码
	private String partyId;				 //公司ID		
	private String storeId;				 //门店ID
	private String baseEntry;            //上级订单号
	private String extSystemNo;          //外部系统编码
	private String extDocNo;             //外部单据编码
	private Timestamp   postingDate;          //过账日期
	private String docStatus;            //单据状态（草稿/确定/批准/已清/作废）
	private String memo;				 //备注信息
	private String createUserId;         //创建用户ID
	private String createUserNameF;       //创建用户名称first
	private String createUserNameM;       //创建用户名称middle
	private String createUserNameL;       //创建用户名称lastname
	private String salesPersonNameF;
	private String salesPersonNameM;
	private String salesPersonNameL;
	private Timestamp   createDocDate;        //创建日期
	private String lastUpdateUserId;   	 //最后更新用户ID
	private Timestamp lastUpdateDocDate;  	 //最后更新日期 
	private String memberId;             //会员ID 
	private String memberNameF;          //会员名字F
	private String memberNameM;           //会员名字M
	private String memberNameL;            //会员名字L
	private BigDecimal primeAmount;          //订单原价
	private BigDecimal rebateAmount;         //订单折扣价（订单销售总价）
	private BigDecimal preCollectionAmount;  //订金额
	private BigDecimal collectionAmount;	 //收款额
	private String fundStatus;           //收款状态（未清/部分已清/已清）
	private String warehouseStatus;        //出库状态（未清/部分已清/已清）
	private String returnStatus;         //退货状态（未清/部分已清/已清）
	private Long memberPointAmount;    //会员总积分
	private String salesId;              //销售人员
	private String logisticsAddress;
	private String collectionStoreId; 	 //收款门店ID
	private String deliveryStoreId;     //出库门店ID
	private String   approvalUserId;		 //审批人ID
	private Timestamp   approvalDate;         //审批时间 
	private String isSync;               //单据同步状态
	private Long printNum;      //打印次数
	private String movementTypeId;
	private String memberPhoneMobile;
	private String attrName1;            //自定义字段1
	private String attrName2;		     //自定义字段2
	private String attrName3;		     //自定义字段3			
	private String attrName4;			 //自定义字段4		
	private String attrName5;			 //自定义字段5
	private String attrName6;			 //自定义字段6
	private String attrName7;		 	 //自定义字段7
	private String attrName8;            //自定义字段8
	private String attrName9;  			 //自定义字段9
	
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
	public String getMemberId() {
		return memberId;
	}
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	public BigDecimal getPrimeAmount() {
		return primeAmount;
	}
	public void setPrimeAmount(BigDecimal primeAmount) {
		this.primeAmount = primeAmount;
	}
	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	public BigDecimal getPreCollectionAmount() {
		return preCollectionAmount;
	}
	public void setPreCollectionAmount(BigDecimal preCollectionAmount) {
		this.preCollectionAmount = preCollectionAmount;
	}
	public BigDecimal getCollectionAmount() {
		return collectionAmount;
	}
	public void setCollectionAmount(BigDecimal collectionAmount) {
		this.collectionAmount = collectionAmount;
	}
	public String getFundStatus() {
		return fundStatus;
	}
	public void setFundStatus(String fundStatus) {
		this.fundStatus = fundStatus;
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
	public Long getMemberPointAmount() {
		return memberPointAmount;
	}
	public void setMemberPointAmount(Long memberPointAmount) {
		this.memberPointAmount = memberPointAmount;
	}
	public String getSalesId() {
		return salesId;
	}
	public void setSalesId(String salesId) {
		this.salesId = salesId;
	}
	public String getCollectionStoreId() {
		return collectionStoreId;
	}
	public void setCollectionStoreId(String collectionStoreId) {
		this.collectionStoreId = collectionStoreId;
	}
	public String getDeliveryStoreId() {
		return deliveryStoreId;
	}
	public void setDeliveryStoreId(String deliveryStoreId) {
		this.deliveryStoreId = deliveryStoreId;
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
	
	public String getCreateUserNameF() {
		return createUserNameF;
	}
	public void setCreateUserNameF(String createUserNameF) {
		this.createUserNameF = createUserNameF;
	}
	public String getCreateUserNameM() {
		return createUserNameM;
	}
	public void setCreateUserNameM(String createUserNameM) {
		this.createUserNameM = createUserNameM;
	}
	public String getCreateUserNameL() {
		return createUserNameL;
	}
	public void setCreateUserNameL(String createUserNameL) {
		this.createUserNameL = createUserNameL;
	}
	
	
	public String getMemberNameF() {
		return memberNameF;
	}
	public void setMemberNameF(String memberNameF) {
		this.memberNameF = memberNameF;
	}
	public String getMemberNameM() {
		return memberNameM;
	}
	public void setMemberNameM(String memberNameM) {
		this.memberNameM = memberNameM;
	}
	public String getMemberNameL() {
		return memberNameL;
	}
	public void setMemberNameL(String memberNameL) {
		this.memberNameL = memberNameL;
	}
	
	public String getLogisticsAddress() {
		return logisticsAddress;
	}
	public void setLogisticsAddress(String logisticsAddress) {
		this.logisticsAddress = logisticsAddress;
	}
	
	public Long getPrintNum() {
		return printNum;
	}
	public void setPrintNum(Long printNum) {
		this.printNum = printNum;
	}
	
	
	public String getMovementTypeId() {
		return movementTypeId;
	}
	public void setMovementTypeId(String movementTypeId) {
		this.movementTypeId = movementTypeId;
	}
	
	public String getSalesPersonNameF() {
		return salesPersonNameF;
	}
	public void setSalesPersonNameF(String salesPersonNameF) {
		this.salesPersonNameF = salesPersonNameF;
	}
	public String getSalesPersonNameM() {
		return salesPersonNameM;
	}
	public void setSalesPersonNameM(String salesPersonNameM) {
		this.salesPersonNameM = salesPersonNameM;
	}
	public String getSalesPersonNameL() {
		return salesPersonNameL;
	}
	public void setSalesPersonNameL(String salesPersonNameL) {
		this.salesPersonNameL = salesPersonNameL;
	}
	public String getMemberPhoneMobile() {
		return memberPhoneMobile;
	}
	public void setMemberPhoneMobile(String memberPhoneMobile) {
		this.memberPhoneMobile = memberPhoneMobile;
	}
	public  SaleOrderHeader(){
		
	}
	@Override
	public String toString() {
		return "SaleOrderHeader [docId=" + docId + ", docNo=" + docNo
				+ ", partyId=" + partyId + ", storeId=" + storeId
				+ ", baseEntry=" + baseEntry + ", extSystemNo=" + extSystemNo
				+ ", extDocNo=" + extDocNo + ", postingDate=" + postingDate
				+ ", docStatus=" + docStatus + ", memo=" + memo
				+ ", createUserId=" + createUserId + ", createUserNameF="
				+ createUserNameF + ", createUserNameM=" + createUserNameM
				+ ", createUserNameL=" + createUserNameL
				+ ", salesPersonNameF=" + salesPersonNameF
				+ ", salesPersonNameM=" + salesPersonNameM
				+ ", salesPersonNameL=" + salesPersonNameL + ", createDocDate="
				+ createDocDate + ", lastUpdateUserId=" + lastUpdateUserId
				+ ", lastUpdateDocDate=" + lastUpdateDocDate + ", memberId="
				+ memberId + ", memberNameF=" + memberNameF + ", memberNameM="
				+ memberNameM + ", memberNameL=" + memberNameL
				+ ", primeAmount=" + primeAmount + ", rebateAmount="
				+ rebateAmount + ", preCollectionAmount=" + preCollectionAmount
				+ ", collectionAmount=" + collectionAmount + ", fundStatus="
				+ fundStatus + ", warehouseStatus=" + warehouseStatus
				+ ", returnStatus=" + returnStatus + ", memberPointAmount="
				+ memberPointAmount + ", salesId=" + salesId
				+ ", logisticsAddress=" + logisticsAddress
				+ ", collectionStoreId=" + collectionStoreId
				+ ", deliveryStoreId=" + deliveryStoreId + ", approvalUserId="
				+ approvalUserId + ", approvalDate=" + approvalDate
				+ ", isSync=" + isSync + ", printNum=" + printNum
				+ ", movementTypeId=" + movementTypeId + ", memberPhoneMobile="
				+ memberPhoneMobile + ", attrName1=" + attrName1
				+ ", attrName2=" + attrName2 + ", attrName3=" + attrName3
				+ ", attrName4=" + attrName4 + ", attrName5=" + attrName5
				+ ", attrName6=" + attrName6 + ", attrName7=" + attrName7
				+ ", attrName8=" + attrName8 + ", attrName9=" + attrName9 + "]";
	}

}
