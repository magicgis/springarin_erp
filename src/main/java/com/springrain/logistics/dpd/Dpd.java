package com.springrain.logistics.dpd;

/**
 * Dpd物流参数封装类
 * 
 */
public class Dpd {

	/**
	 * 	Selection of product, exactly one per shipment,
		mandatory for consignment data. Possible values are:
		CL = DPD CLASSIC
		E830 = DPD 8:30
		E10 = DPD 10:00
		E12 = DPD 12:00
		E18 = DPD 18:00
		IE2 = DPD EXPRESS
		PL = DPD PARCELLetter
		PL+ = DPD PARCELLetterPlus
		MAIL = DPD International Mail
	 */
	private String product="CL";
	private String sendingDepot="0104";
	private String mpsCompleteDelivery="0";
	private String customerNumber="2406002211";
	// sender信息
	private String senderName="F&amp;M Technology GmbH";
	private String senderAddress1="Montgolfierstraße 6";
	private String senderCity="Wiedemar";
	private String senderZip="04509";
	private String senderCountry="DE";;

	// receiver信息
	private String receiverName;
	private String receiverAddress1;
	private String receiverCity;
	private String receiverState;
	private String receiverZip;
	private String receiverCountry;
	
	public String getSendingDepot() {
		return sendingDepot;
	}

	public void setSendingDepot(String sendingDepot) {
		this.sendingDepot = sendingDepot;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getMpsCompleteDelivery() {
		return mpsCompleteDelivery;
	}

	public void setMpsCompleteDelivery(String mpsCompleteDelivery) {
		this.mpsCompleteDelivery = mpsCompleteDelivery;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderAddress1() {
		return senderAddress1;
	}

	public void setSenderAddress1(String senderAddress1) {
		this.senderAddress1 = senderAddress1;
	}

	public String getSenderCity() {
		return senderCity;
	}

	public void setSenderCity(String senderCity) {
		this.senderCity = senderCity;
	}

	public String getSenderZip() {
		return senderZip;
	}

	public void setSenderZip(String senderZip) {
		this.senderZip = senderZip;
	}

	public String getSenderCountry() {
		return senderCountry;
	}

	public void setSenderCountry(String senderCountry) {
		this.senderCountry = senderCountry;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiverAddress1() {
		return receiverAddress1;
	}

	public void setReceiverAddress1(String receiverAddress1) {
		this.receiverAddress1 = receiverAddress1;
	}

	public String getReceiverCity() {
		return receiverCity;
	}

	public void setReceiverCity(String receiverCity) {
		this.receiverCity = receiverCity;
	}

	public String getReceiverState() {
		return receiverState;
	}

	public void setReceiverState(String receiverState) {
		this.receiverState = receiverState;
	}

	public String getReceiverZip() {
		return receiverZip;
	}

	public void setReceiverZip(String receiverZip) {
		this.receiverZip = receiverZip;
	}

	public String getReceiverCountry() {
		return receiverCountry;
	}

	public void setReceiverCountry(String receiverCountry) {
		this.receiverCountry = receiverCountry;
	}

}
