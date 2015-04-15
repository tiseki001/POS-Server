package org.ofbiz.querydataforsales;

import java.math.BigDecimal;

public class PaymentMethodAmount {
	private String type;
	private String typeName;
	private BigDecimal  targetAmount;
	private BigDecimal  PreAmount;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public BigDecimal getTargetAmount() {
		return targetAmount;
	}
	public void setTargetAmount(BigDecimal targetAmount) {
		this.targetAmount = targetAmount;
	}
	public BigDecimal getPreAmount() {
		return PreAmount;
	}
	public void setPreAmount(BigDecimal preAmount) {
		PreAmount = preAmount;
	}
	
}
