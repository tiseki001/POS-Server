package org.ofbiz.order.backorder;

import java.math.BigDecimal;

public class BackOrderDtl {
	private String docId;   			//退订单ID：主键
	private Long lineNo;			    //明细行号：主键
	private Long lineNoBaseEntry;       //NULL，无意义	
	private String productId;			//商品ID
	private String productName;			//商品 名称
	private String barCodes;			//商品条形码
	private String extNo;				//外部编码
	private String serialNo;			//序列号
	private BigDecimal unitPrice;		//原价
	private BigDecimal rebatePrice;		//折扣价（销售单价）
	private Long quantity;			    //退订数量
	private BigDecimal rebateAmount;    //金额
	private BigDecimal bomAmount;       //套包金额
	private String memo;				//备注
	private String salesId;				//销售员ID
	private Long memberPoint;			//会员积分
	private String isGift;				//是否赠品
	private String isSequence;			//是否序列化管理
	private String isMainProduct;       //是否是主商品
	private String bomId;               //套包Id
	private String bomName;             //套包Name
	private String productSalesPolicyId;//销售政策Id
	private String productSalesPolicyNo;//销售政策No
	private String approvalUserId;		//审批人ID
	private String attrName1;           //自定义字段1
	private String attrName2;		    //自定义字段2
	private String attrName3;		    //自定义字段3			
	private String attrName4;			//自定义字段4		
	private String attrName5;			//自定义字段5
	private String attrName6;			//自定义字段6
	private String attrName7;		 	//自定义字段7
	private String attrName8;           //自定义字段8
	private String attrName9;  			//自定义字段9
	
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
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getBarCodes() {
		return barCodes;
	}
	public void setBarCodes(String barCodes) {
		this.barCodes = barCodes;
	}
	public String getExtNo() {
		return extNo;
	}
	public void setExtNo(String extNo) {
		this.extNo = extNo;
	}
	public String getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	public BigDecimal getRebatePrice() {
		return rebatePrice;
	}
	public void setRebatePrice(BigDecimal rebatePrice) {
		this.rebatePrice = rebatePrice;
	}
	public Long getQuantity() {
		return quantity;
	}
	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}
	
	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	public BigDecimal getBomAmount() {
		return bomAmount;
	}
	public void setBomAmount(BigDecimal bomAmount) {
		this.bomAmount = bomAmount;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getSalesId() {
		return salesId;
	}
	public void setSalesId(String salesId) {
		this.salesId = salesId;
	}
	public Long getMemberPoint() {
		return memberPoint;
	}
	public void setMemberPoint(Long memberPoint) {
		this.memberPoint = memberPoint;
	}
	public String getIsGift() {
		return isGift;
	}
	public void setIsGift(String isGift) {
		this.isGift = isGift;
	}
	
	public String getIsSequence() {
		return isSequence;
	}
	public void setIsSequence(String isSequence) {
		this.isSequence = isSequence;
	}
	
	public String getIsMainProduct() {
		return isMainProduct;
	}
	public void setIsMainProduct(String isMainProduct) {
		this.isMainProduct = isMainProduct;
	}
	public String getBomId() {
		return bomId;
	}
	public void setBomId(String bomId) {
		this.bomId = bomId;
	}
	public String getBomName() {
		return bomName;
	}
	public void setBomName(String bomName) {
		this.bomName = bomName;
	}
	public String getProductSalesPolicyId() {
		return productSalesPolicyId;
	}
	public void setProductSalesPolicyId(String productSalesPolicyId) {
		this.productSalesPolicyId = productSalesPolicyId;
	}
	public String getProductSalesPolicyNo() {
		return productSalesPolicyNo;
	}
	public void setProductSalesPolicyNo(String productSalesPolicyNo) {
		this.productSalesPolicyNo = productSalesPolicyNo;
	}
	public String getApprovalUserId() {
		return approvalUserId;
	}
	public void setApprovalUserId(String approvalUserId) {
		this.approvalUserId = approvalUserId;
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
	
}
