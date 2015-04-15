package org.ofbiz.replenishment.webService;

public class ReplenishmentModel {
	private String productId;
	private String productName;
	private long onhand;
	private long promise;
	private long saftyQuantity;
	private long receiveQuantity;
	private long preQuantity;
	private long quantity;
	public ReplenishmentModel(){
		onhand = 0L;
		promise = 0L;
		saftyQuantity = 0L;
		receiveQuantity = 0L;
		preQuantity = 0L;
		quantity = 0L;
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
	public long getOnhand() {
		return onhand;
	}
	public void setOnhand(long onhand) {
		this.onhand = onhand;
	}
	public long getPromise() {
		return promise;
	}
	public void setPromise(long promise) {
		this.promise = promise;
	}
	public long getSaftyQuantity() {
		return saftyQuantity;
	}
	public void setSaftyQuantity(long saftyQuantity) {
		this.saftyQuantity = saftyQuantity;
	}
	public long getReceiveQuantity() {
		return receiveQuantity;
	}
	public void setReceiveQuantity(long receiveQuantity) {
		this.receiveQuantity = receiveQuantity;
	}
	public long getPreQuantity() {
		return preQuantity;
	}
	public void setPreQuantity(long preQuantity) {
		this.preQuantity = preQuantity;
	}
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}	
}
