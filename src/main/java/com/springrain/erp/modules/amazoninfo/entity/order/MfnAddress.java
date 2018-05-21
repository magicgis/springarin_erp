package com.springrain.erp.modules.amazoninfo.entity.order;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.web.util.HtmlUtils;

import com.springrain.erp.common.utils.IdGen;

@Entity
@Table(name = "amazoninfo_ebay_address")
public class MfnAddress{

	private String id;
	private String name;
	private String street;
	private String street1;
	private String street2;
	private String cityName;
	private String country;
	private String stateOrProvince;
	private String countryCode;
	private String postalCode;
	private String phone;
	private MfnOrder orderId;
	
	@PrePersist
	public void prePersist(){
		this.id = IdGen.uuid();
	}
	
	@Id
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return  HtmlUtils.htmlUnescape(name);
	}
	

	public void setName(String name) {
		this.name = name;
	}
	
	public String getStreet() {
		return HtmlUtils.htmlUnescape(street);
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getStreet1() {
		return HtmlUtils.htmlUnescape(street1);
	}
	public void setStreet1(String street1) {
		this.street1 = street1;
	}
	public String getStreet2() {
		return HtmlUtils.htmlUnescape(street2);
	}
	public void setStreet2(String street2) {
		this.street2 = street2;
	}
	public String getCityName() {
		return HtmlUtils.htmlUnescape(cityName);
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	public String getCountry() {
		return HtmlUtils.htmlUnescape(country);
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getStateOrProvince() {
		return HtmlUtils.htmlUnescape(stateOrProvince);
	}
	public void setStateOrProvince(String stateOrProvince) {
		this.stateOrProvince = stateOrProvince;
	}
	public String getCountryCode() {
		return HtmlUtils.htmlUnescape(countryCode);
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public String getPostalCode() {
		return HtmlUtils.htmlUnescape(postalCode);
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
	@OneToOne(fetch=FetchType.LAZY,optional=false)
	@JoinColumn(name="order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public MfnOrder getOrderId() {
		return orderId;
	}
	public void setOrderId(MfnOrder orderId) {
		this.orderId = orderId;
	}
	

}
