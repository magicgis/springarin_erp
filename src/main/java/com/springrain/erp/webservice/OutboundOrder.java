package com.springrain.erp.webservice;

import java.util.Date;

public class OutboundOrder {
	  
    private String weight;
    
    private Float fbaPerUnitFulfillmentFee;
    
    private Float fbaTransportationFee;
    
    private Float fbaPerOrderFulfillmentFee;
    
    private Date earliestShipDate;//您承诺的订单发货时间范围的第一天

    private Date latestShipDate;//您承诺的订单最晚发货时间范围的第一天

    private Date earliestDeliveryDate;//最早到达时间

    private Date latestDeliveryDate;//最晚到达时间
    
    private String errorMsg;
    
	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public Float getFbaPerUnitFulfillmentFee() {
		return fbaPerUnitFulfillmentFee;
	}

	public void setFbaPerUnitFulfillmentFee(Float fbaPerUnitFulfillmentFee) {
		this.fbaPerUnitFulfillmentFee = fbaPerUnitFulfillmentFee;
	}

	public Float getFbaTransportationFee() {
		return fbaTransportationFee;
	}

	public void setFbaTransportationFee(Float fbaTransportationFee) {
		this.fbaTransportationFee = fbaTransportationFee;
	}

	public Float getFbaPerOrderFulfillmentFee() {
		return fbaPerOrderFulfillmentFee;
	}

	public void setFbaPerOrderFulfillmentFee(Float fbaPerOrderFulfillmentFee) {
		this.fbaPerOrderFulfillmentFee = fbaPerOrderFulfillmentFee;
	}

	public Date getEarliestShipDate() {
		return earliestShipDate;
	}

	public void setEarliestShipDate(Date earliestShipDate) {
		this.earliestShipDate = earliestShipDate;
	}

	public Date getLatestShipDate() {
		return latestShipDate;
	}

	public void setLatestShipDate(Date latestShipDate) {
		this.latestShipDate = latestShipDate;
	}

	public Date getEarliestDeliveryDate() {
		return earliestDeliveryDate;
	}

	public void setEarliestDeliveryDate(Date earliestDeliveryDate) {
		this.earliestDeliveryDate = earliestDeliveryDate;
	}

	public Date getLatestDeliveryDate() {
		return latestDeliveryDate;
	}

	public void setLatestDeliveryDate(Date latestDeliveryDate) {
		this.latestDeliveryDate = latestDeliveryDate;
	}
    
}
