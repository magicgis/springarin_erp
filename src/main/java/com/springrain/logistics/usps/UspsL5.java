package com.springrain.logistics.usps;

/**
 * L5 USPS物流参数封装类 部分字段说明见文档 http://docs.shipl5.com/
 * 
 */
public class UspsL5 {

	private String date; //The ship date should be in the format of YYYY-MM-DD.
	private String mailClass = "PM"; //
	private String packageType = "PACKAGE"; // PACKAGE(默认)
	private Integer weight; // This is the weight of the package in ounces. Max
							// weight for First Class mail is 16 oz.
	// 长宽高(单位：inches)
	private Float length;
	private Float width;
	private Float height;

	// sender信息
	private String senderName;
	private String senderCompany;
	private String senderAddress1;
	private String senderAddress2;	//可为空
	private String senderCity;
	private String senderState;
	private String senderZip;
	private String senderZip4;

	// receiver信息
	private String receiverName;
	private String receiverCompany;
	private String receiverAddress1;
	private String receiverAddress2;
	private String receiverCity;
	private String receiverState;
	private String receiverZip;
	private String receiverZip4;
	private String receiverCountry = "US";	//两位国家编码 e.g. US. Only required for receiver country. Currently, only domestic shipments are supported.

	private String signature = "A";
	private Integer insurance; // 包裹的价值，单位：美分

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getMailClass() {
		return mailClass;
	}

	public void setMailClass(String mailClass) {
		this.mailClass = mailClass;
	}

	public String getPackageType() {
		return packageType;
	}

	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Float getLength() {
		return length;
	}

	public void setLength(Float length) {
		this.length = length;
	}

	public Float getWidth() {
		return width;
	}

	public void setWidth(Float width) {
		this.width = width;
	}

	public Float getHeight() {
		return height;
	}

	public void setHeight(Float height) {
		this.height = height;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderCompany() {
		return senderCompany;
	}

	public void setSenderCompany(String senderCompany) {
		this.senderCompany = senderCompany;
	}

	public String getSenderAddress1() {
		return senderAddress1;
	}

	public void setSenderAddress1(String senderAddress1) {
		this.senderAddress1 = senderAddress1;
	}

	public String getSenderAddress2() {
		return senderAddress2;
	}

	public void setSenderAddress2(String senderAddress2) {
		this.senderAddress2 = senderAddress2;
	}

	public String getSenderCity() {
		return senderCity;
	}

	public void setSenderCity(String senderCity) {
		this.senderCity = senderCity;
	}

	public String getSenderState() {
		return senderState;
	}

	public void setSenderState(String senderState) {
		this.senderState = senderState;
	}

	public String getSenderZip() {
		return senderZip;
	}

	public void setSenderZip(String senderZip) {
		this.senderZip = senderZip;
	}

	public String getSenderZip4() {
		return senderZip4;
	}

	public void setSenderZip4(String senderZip4) {
		this.senderZip4 = senderZip4;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiverCompany() {
		return receiverCompany;
	}

	public void setReceiverCompany(String receiverCompany) {
		this.receiverCompany = receiverCompany;
	}

	public String getReceiverAddress1() {
		return receiverAddress1;
	}

	public void setReceiverAddress1(String receiverAddress1) {
		this.receiverAddress1 = receiverAddress1;
	}

	public String getReceiverAddress2() {
		return receiverAddress2;
	}

	public void setReceiverAddress2(String receiverAddress2) {
		this.receiverAddress2 = receiverAddress2;
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

	public String getReceiverZip4() {
		return receiverZip4;
	}

	public void setReceiverZip4(String receiverZip4) {
		this.receiverZip4 = receiverZip4;
	}

	public String getReceiverCountry() {
		return receiverCountry;
	}

	public void setReceiverCountry(String receiverCountry) {
		this.receiverCountry = receiverCountry;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Integer getInsurance() {
		return insurance;
	}

	public void setInsurance(Integer insurance) {
		this.insurance = insurance;
	}

}
