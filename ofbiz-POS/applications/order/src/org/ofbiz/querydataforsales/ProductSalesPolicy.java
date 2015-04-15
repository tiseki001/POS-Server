package org.ofbiz.querydataforsales;

import java.util.List;

public class ProductSalesPolicy {
	private String productSalesPolicyId;
	private String policyName;
	private Long mainRatio;
	private List<ProductPriceType> productPriceTypes;
	
	public ProductSalesPolicy() {}
	
	public ProductSalesPolicy(String productSalesPolicyId, String policyName) {
		super();
		this.productSalesPolicyId = productSalesPolicyId;
		this.policyName = policyName;
	}


	public String getProductSalesPolicyId() {
		return productSalesPolicyId;
	}

	public void setProductSalesPolicyId(String productSalesPolicyId) {
		this.productSalesPolicyId = productSalesPolicyId;
	}

	public String getPolicyName() {
		return policyName;
	}

	public void setPolicyName(String policyName) {
		this.policyName = policyName;
	}

	public Long getMainRatio() {
		return mainRatio;
	}

	public void setMainRatio(Long mainRatio) {
		this.mainRatio = mainRatio;
	}

	public List<ProductPriceType> getProductPriceTypes() {
		return productPriceTypes;
	}

	public void setProductPriceTypes(List<ProductPriceType> productPriceTypes) {
		this.productPriceTypes = productPriceTypes;
	}

	@Override
	public String toString() {
		return "ProductSalesPolicy [productSalesPolicyId="
				+ productSalesPolicyId + ", policyName=" + policyName
				+ ", productPriceTypes=" + productPriceTypes + "]";
	}

	
}
