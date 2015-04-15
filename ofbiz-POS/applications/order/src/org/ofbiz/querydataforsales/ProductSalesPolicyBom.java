package org.ofbiz.querydataforsales;

public class ProductSalesPolicyBom {
	private String bomId;
	private String bomName;
	
	public ProductSalesPolicyBom() {
		super();
	}
	public ProductSalesPolicyBom(String bomId, String bomName) {
		super();
		this.bomId = bomId;
		this.bomName = bomName;
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
	
}
