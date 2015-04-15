package org.ofbiz.querydataforsales;

import java.math.BigDecimal;
import java.util.List;

public class Product {
	private String productId;//商品Id
	private String productName;//商品Name
	private String productStoreId;//门店Id
	private String isSequence;//是否序列化管理
	private String idValue;//条码
	private Long   onhand;//库存数量
	private Long   promise;//占用库存
	private String sequenceId;//串号
	private String movementTypeId;//移动类型
	private String facilityId;//仓库ID
	private String isOccupied;//是否占用库存
	private BigDecimal suggestedPrice;//厂家指导价
	private List<ProductSalesPolicy> productSalesPolicys;//销售政策
	
	public Product() {}
	
	public Product(String productId, String productName, String productStoreId,
			String idValue, String sequenceId, String movementTypeId) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.productStoreId = productStoreId;
		this.idValue = idValue;
		this.sequenceId = sequenceId;
		this.movementTypeId = movementTypeId;
	}

	public Product(String productId, String productName, String productStoreId,
			String isSequence, String idValue, Long onhand, List<ProductSalesPolicy> productSalesPolicys) {
		super();
		this.productId = productId;
		this.productName = productName;
		this.productStoreId = productStoreId;
		this.isSequence = isSequence;
		this.idValue = idValue;
		this.onhand = onhand;
		this.productSalesPolicys = productSalesPolicys;
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

	public String getProductStoreId() {
		return productStoreId;
	}

	public void setProductStoreId(String productStoreId) {
		this.productStoreId = productStoreId;
	}

	public String getIsSequence() {
		return isSequence;
	}

	public void setIsSequence(String isSequence) {
		this.isSequence = isSequence;
	}

	public String getIdValue() {
		return idValue;
	}

	public void setIdValue(String idValue) {
		this.idValue = idValue;
	}

	public Long getOnhand() {
		return onhand;
	}

	public void setOnhand(Long onhand) {
		this.onhand = onhand;
	}

	public Long getPromise() {
		return promise;
	}

	public void setPromise(Long promise) {
		this.promise = promise;
	}

	public String getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(String sequenceId) {
		this.sequenceId = sequenceId;
	}

	
	public String getMovementTypeId() {
		return movementTypeId;
	}

	public void setMovementTypeId(String movementTypeId) {
		this.movementTypeId = movementTypeId;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}
	
	public String getIsOccupied() {
		return isOccupied;
	}

	public void setIsOccupied(String isOccupied) {
		this.isOccupied = isOccupied;
	}

	public List<ProductSalesPolicy> getProductSalesPolicys() {
		return productSalesPolicys;
	}

	public void setProductSalesPolicys(
			List<ProductSalesPolicy> productSalesPolicys) {
		this.productSalesPolicys = productSalesPolicys;
	}

	public BigDecimal getSuggestedPrice() {
		return suggestedPrice;
	}

	public void setSuggestedPrice(BigDecimal suggestedPrice) {
		this.suggestedPrice = suggestedPrice;
	}


}
