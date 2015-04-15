package org.ofbiz.order.saleorder;
//销售订单商品销售价格明细
public class SalesOrderPriceDtl {
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
	public String getProductPriceTypeId() {
		return productPriceTypeId;
	}
	public void setProductPriceTypeId(String productPriceTypeId) {
		this.productPriceTypeId = productPriceTypeId;
	}
	public String getProductPriceRuleId() {
		return productPriceRuleId;
	}
	public void setProductPriceRuleId(String productPriceRuleId) {
		this.productPriceRuleId = productPriceRuleId;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
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

private	String docId;		//销售订单ID：主键	
 private	Long lineNo;		//明细行号：主键
 private	String productPriceTypeId;  //销售政策ID：主键		
 private	String productPriceRuleId;  //销售规则ID：主键	 
 private	String price; 	//价格						
 private String attrName1;            //自定义字段1
	private String attrName2;		     //自定义字段2
	private String attrName3;		     //自定义字段3			
	private String attrName4;			 //自定义字段4		
	private String attrName5;			 //自定义字段5
	private String attrName6;			 //自定义字段6
	private String attrName7;		 	 //自定义字段7
	private String attrName8;            //自定义字段8
	private String attrName9;  			 //自定义字段9
}
