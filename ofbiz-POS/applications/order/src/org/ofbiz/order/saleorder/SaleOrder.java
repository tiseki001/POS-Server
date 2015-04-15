package org.ofbiz.order.saleorder;

import java.util.List;
//销售订单
public class SaleOrder {
	private SaleOrderHeader header; //销售表头
	private List<SaleOrderDtl>  detail; //销售明细表	
	private List<SaleOrderDtlPrmtn> detailPrmtn;//商品和促销对应关系
	private List<SaleOrderPrmtn>    prmtn; //促销信息
	private List<SalesOrderPriceDtl> priceDtl;
	private SyncDoc syncdoc;
	public SaleOrderHeader getHeader() {
		return header;
	}
	public void setHeader(SaleOrderHeader header) {
		this.header = header;
	}
	public List<SaleOrderDtl> getDetail() {
		return detail;
	}
	public void setDetail(List<SaleOrderDtl> detail) {
		this.detail = detail;
	}
	public List<SaleOrderDtlPrmtn> getDetailPrmtn() {
		return detailPrmtn;
	}
	public void setDetailPrmtn(List<SaleOrderDtlPrmtn> detailPrmtn) {
		this.detailPrmtn = detailPrmtn;
	}
	public List<SaleOrderPrmtn> getPrmtn() {
		return prmtn;
	}
	public void setPrmtn(List<SaleOrderPrmtn> prmtn) {
		this.prmtn = prmtn;
	}
	public List<SalesOrderPriceDtl> getPriceDtl() {
		return priceDtl;
	}
	public void setPriceDtl(List<SalesOrderPriceDtl> priceDtl) {
		this.priceDtl = priceDtl;
	}
	public SyncDoc getSyncdoc() {
		return syncdoc;
	}
	public void setSyncdoc(SyncDoc syncdoc) {
		this.syncdoc = syncdoc;
	}
	@Override
	public String toString() {
		return "SaleOrder [header=" + header + ", detail=" + detail
				+ ", detailPrmtn=" + detailPrmtn + ", prmtn=" + prmtn
				+ ", priceDtl=" + priceDtl + ", syncdoc=" + syncdoc + "]";
	}
	
	}

	