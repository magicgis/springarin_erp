package com.springrain.erp.modules.psi.entity;


public class PsiInventoryInnerDto {
	private		 String 	billNo;
	private		 String 	country;
	private		 String 	createDate;
	private		 String 	orderDate;   //PO预计收货时间  
	private		 String 	deliveryDate;   //PO预计收货时间  
	
	private      String     arriveDate;     //Transport预计到港时间
	private      Integer    quantity;
	private      String     sku;
	private      Integer    oldQuantity;
	private      Integer    brokenQuantity;
	private      Integer    renewQuantity;
	
	private      String     remark;
	private      String     tranModel;
	private      String     toCountry;
	private      String     tranWeek;  //运输周
	private      String     barcode;
	
	private      Integer    quantityOffline;
	private      String    offlineSta;      //线下状态
	
	
	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getTranWeek() {
		return tranWeek;
	}

	public void setTranWeek(String tranWeek) {
		this.tranWeek = tranWeek;
	}

	public Integer getQuantityOffline() {
		return quantityOffline;
	}

	public void setQuantityOffline(Integer quantityOffline) {
		this.quantityOffline = quantityOffline;
	}

	public String getToCountry() {
		return toCountry;
	}

	public String getOfflineSta() {
		return offlineSta;
	}

	public void setOfflineSta(String offlineSta) {
		this.offlineSta = offlineSta;
	}

	public void setToCountry(String toCountry) {
		this.toCountry = toCountry;
	}

	public String getArriveDate() {
		return arriveDate;
	}

	public void setArriveDate(String arriveDate) {
		this.arriveDate = arriveDate;
	}

	public String getTranModel() {
		return tranModel;
	}

	public void setTranModel(String tranModel) {
		this.tranModel = tranModel;
	}

	public String getCountry() {
		return country;
	}
	
	public void setCountry(String country) {
		this.country = country;
	}

	public String getBillNo() {
		return billNo;
	}

	public void setBillNo(String billNo) {
		this.billNo = billNo;
	}


	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Integer getOldQuantity() {
		return oldQuantity;
	}

	public void setOldQuantity(Integer oldQuantity) {
		this.oldQuantity = oldQuantity;
	}

	public Integer getBrokenQuantity() {
		return brokenQuantity;
	}

	public void setBrokenQuantity(Integer brokenQuantity) {
		this.brokenQuantity = brokenQuantity;
	}

	public Integer getRenewQuantity() {
		return renewQuantity;
	}

	public void setRenewQuantity(Integer renewQuantity) {
		this.renewQuantity = renewQuantity;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}

	public PsiInventoryInnerDto(String billNo,String country,Integer quantity, String createDate,String deliveryDate,String sku,Integer oldQuantity,Integer brokenQuantity,Integer renewQuantity,String arriveDate,String tranModel,String remark,String orderDate,String tranWeek,Integer quantityOffline) {
		super();
		this.billNo = billNo;
		this.country = country;
		this.quantity=quantity;
		this.createDate = createDate;
		this.deliveryDate = deliveryDate;
		this.orderDate=orderDate;
		this.sku=sku;
		this.oldQuantity=oldQuantity;
		this.brokenQuantity=brokenQuantity;
		this.renewQuantity=renewQuantity;
		this.arriveDate=arriveDate;
		this.tranModel=tranModel;
		this.remark=remark;
		this.tranWeek=tranWeek;
		this.quantityOffline=quantityOffline;
	}
	public PsiInventoryInnerDto(String billNo,String country,Integer quantity, String createDate,String deliveryDate,String sku,Integer oldQuantity,Integer brokenQuantity,Integer renewQuantity,String arriveDate,String tranModel,String toCountry,String orderDate,String barcode,String remark,String tranWeek,String offlineSta) {
		super();
		this.billNo = billNo;
		this.country = country;
		this.quantity=quantity;
		this.createDate = createDate;
		this.deliveryDate = deliveryDate;
		this.orderDate=orderDate;
		this.sku=sku;
		this.oldQuantity=oldQuantity;
		this.brokenQuantity=brokenQuantity;
		this.renewQuantity=renewQuantity;
		this.arriveDate=arriveDate;
		this.tranModel=tranModel;
		this.remark=remark;
		this.barcode=barcode;
		this.toCountry = toCountry;
		this.tranWeek=tranWeek;
		this.offlineSta=offlineSta;
	}
	public PsiInventoryInnerDto() {}
	
}
