package com.springrain.erp.modules.psi.entity;

public class FbaInboundDto {
	
	private String shipmentId;
	
	private String shipmentName;

	private int quantityShipped;
	
	private int quantityReceived;
	
	private String sku;
	
	private String shipmentStatus;
	
	private String toDate="";
	
	private String remark = "";
	
	private String pickUpDate;

	public String getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(String shipmentId) {
		this.shipmentId = shipmentId;
	}

	public String getShipmentName() {
		return shipmentName;
	}

	public void setShipmentName(String shipmentName) {
		this.shipmentName = shipmentName;
	}

	public int getQuantityShipped() {
		return quantityShipped;
	}

	public void setQuantityShipped(int quantityShipped) {
		this.quantityShipped = quantityShipped;
	}

	public int getQuantityReceived() {
		return quantityReceived;
	}

	public void setQuantityReceived(int quantityReceived) {
		this.quantityReceived = quantityReceived;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getPickUpDate() {
		return pickUpDate;
	}

	public void setPickUpDate(String pickUpDate) {
		this.pickUpDate = pickUpDate;
	}

	public String getShipmentStatus() {
		return shipmentStatus;
	}

	public void setShipmentStatus(String shipmentStatus) {
		this.shipmentStatus = shipmentStatus;
	}
	
	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public FbaInboundDto(String shipmentId, String shipmentName,
			int quantityShipped, int quantityReceived, String sku,
			String shipmentStatus,String pickUpDate) {
		super();
		this.shipmentId = shipmentId;
		this.shipmentName = shipmentName;
		this.quantityShipped = quantityShipped;
		this.quantityReceived = quantityReceived;
		this.sku = sku;
		this.shipmentStatus = shipmentStatus;
		this.pickUpDate=pickUpDate;
	}
}
