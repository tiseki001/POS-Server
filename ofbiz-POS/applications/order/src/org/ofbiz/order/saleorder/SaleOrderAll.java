package org.ofbiz.order.saleorder;

import java.util.List;

import org.ofbiz.order.collectionorder.CollectionOrderDtl;
import org.ofbiz.order.collectionorder.CollectionOrderHeader;
import org.ofbiz.order.salesoutwhsorder.SalesOutWhsOrderDtl;
import org.ofbiz.order.salesoutwhsorder.SalesOutWhsOrderHeader;

public class SaleOrderAll {
	private SaleOrderHeader salesOrderHeader; //销售表头
	private List<SaleOrderDtl>  salesOrderDetail; //销售明细表	
	private List<SaleOrderDtlPrmtn> detailPrmtn;//商品和促销对应关系
	private List<SaleOrderPrmtn>    prmtn; //促销信息
	private SyncDoc syncdoc;
	private CollectionOrderHeader collectionOrderHeader;
	private List<CollectionOrderDtl>  collectionOrderDetail;
	private SalesOutWhsOrderHeader deliveryDoc;
	private List<SalesOutWhsOrderDtl> deliveryItem;
	public SaleOrderHeader getSalesOrderHeader() {
		return salesOrderHeader;
	}
	public void setSalesOrderHeader(SaleOrderHeader salesOrderHeader) {
		this.salesOrderHeader = salesOrderHeader;
	}
	public List<SaleOrderDtl> getSalesOrderDetail() {
		return salesOrderDetail;
	}
	public void setSalesOrderDetail(List<SaleOrderDtl> salesOrderDetail) {
		this.salesOrderDetail = salesOrderDetail;
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
	public SyncDoc getSyncdoc() {
		return syncdoc;
	}
	public void setSyncdoc(SyncDoc syncdoc) {
		this.syncdoc = syncdoc;
	}
	public CollectionOrderHeader getCollectionOrderHeader() {
		return collectionOrderHeader;
	}
	public void setCollectionOrderHeader(CollectionOrderHeader collectionOrderHeader) {
		this.collectionOrderHeader = collectionOrderHeader;
	}
	public List<CollectionOrderDtl> getCollectionOrderDetail() {
		return collectionOrderDetail;
	}
	public void setCollectionOrderDetail(
			List<CollectionOrderDtl> collectionOrderDetail) {
		this.collectionOrderDetail = collectionOrderDetail;
	}
	public SalesOutWhsOrderHeader getDeliveryDoc() {
		return deliveryDoc;
	}
	public void setDeliveryDoc(SalesOutWhsOrderHeader deliveryDoc) {
		this.deliveryDoc = deliveryDoc;
	}
	public List<SalesOutWhsOrderDtl> getDeliveryItem() {
		return deliveryItem;
	}
	public void setDeliveryItem(List<SalesOutWhsOrderDtl> deliveryItem) {
		this.deliveryItem = deliveryItem;
	}
	
	
}
