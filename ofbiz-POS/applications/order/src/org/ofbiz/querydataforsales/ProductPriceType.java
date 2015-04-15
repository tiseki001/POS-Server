package org.ofbiz.querydataforsales;

public class ProductPriceType {
	private String productPriceRuleId;
	private String productPriceTypeId;
	private String description;
	private String price;
	public String getProductPriceTypeId() {
		return productPriceTypeId;
	}
	public void setProductPriceTypeId(String productPriceTypeId) {
		this.productPriceTypeId = productPriceTypeId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	
	public String getProductPriceRuleId() {
		return productPriceRuleId;
	}
	public void setProductPriceRuleId(String productPriceRuleId) {
		this.productPriceRuleId = productPriceRuleId;
	}
	@Override
	public String toString() {
		return "ProductPriceType [productPriceRuleId=" + productPriceRuleId
				+ ", productPriceTypeId=" + productPriceTypeId
				+ ", description=" + description + ", price=" + price + "]";
	}
	
	
}
