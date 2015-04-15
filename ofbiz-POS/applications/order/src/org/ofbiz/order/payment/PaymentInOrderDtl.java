package org.ofbiz.order.payment;

import java.math.BigDecimal;

public class PaymentInOrderDtl {
	private String docId;				 //缴款单ID	
	private Long lineNo;				 //明细行号
	private Long lineNoBaseEntry;       //上级订单明细行号
	private String type;				 //支付类型
	private String typeName;            //类型名字
	private String extNo;				 //外部编码
	private String approvalUserId;		 //审批人ID
	private String memo;				 //备注
	private BigDecimal targetAmount;     //应缴金额
	private BigDecimal amount;				//缴款金额
	private BigDecimal preAmount;				 //预缴金额
	private BigDecimal erpCheckAmount;			//总部核对金额
	private String erpMemo;			 //总部处理原因
	private BigDecimal diffAmount;    //差异金额
	private String style;                    //类型（订金收款）
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
	public Long getLineNo() {
		return lineNo;
	}
	public void setLineNo(Long lineNo) {
		this.lineNo = lineNo;
	}
	public Long getLineNoBaseEntry() {
		return lineNoBaseEntry;
	}
	public void setLineNoBaseEntry(Long lineNoBaseEntry) {
		this.lineNoBaseEntry = lineNoBaseEntry;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getExtNo() {
		return extNo;
	}
	public void setExtNo(String extNo) {
		this.extNo = extNo;
	}
	public String getApprovalUserId() {
		return approvalUserId;
	}
	public void setApprovalUserId(String approvalUserId) {
		this.approvalUserId = approvalUserId;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public BigDecimal getTargetAmount() {
		return targetAmount;
	}
	public void setTargetAmount(BigDecimal targetAmount) {
		this.targetAmount = targetAmount;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getPreAmount() {
		return preAmount;
	}
	public void setPreAmount(BigDecimal preAmount) {
		this.preAmount = preAmount;
	}
	public BigDecimal getErpCheckAmount() {
		return erpCheckAmount;
	}
	public void setErpCheckAmount(BigDecimal erpCheckAmount) {
		this.erpCheckAmount = erpCheckAmount;
	}
	public String getErpMemo() {
		return erpMemo;
	}
	public void setErpMemo(String erpMemo) {
		this.erpMemo = erpMemo;
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
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	
	public BigDecimal getDiffAmount() {
		return diffAmount;
	}
	public void setDiffAmount(BigDecimal diffAmount) {
		this.diffAmount = diffAmount;
	}
	@Override
	public String toString() {
		return "PaymentInOrderDtl [docId=" + docId + ", lineNo=" + lineNo
				+ ", lineNoBaseEntry=" + lineNoBaseEntry + ", type=" + type
				+ ", typeName=" + typeName + ", extNo=" + extNo
				+ ", approvalUserId=" + approvalUserId + ", memo=" + memo
				+ ", targetAmount=" + targetAmount + ", amount=" + amount
				+ ", preAmount=" + preAmount + ", erpCheckAmount="
				+ erpCheckAmount + ", erpMemo=" + erpMemo + ", diffAmount="
				+ diffAmount + ", style=" + style + ", attrName1=" + attrName1
				+ ", attrName2=" + attrName2 + ", attrName3=" + attrName3
				+ ", attrName4=" + attrName4 + ", attrName5=" + attrName5
				+ ", attrName6=" + attrName6 + ", attrName7=" + attrName7
				+ ", attrName8=" + attrName8 + ", attrName9=" + attrName9 + "]";
	}

}
