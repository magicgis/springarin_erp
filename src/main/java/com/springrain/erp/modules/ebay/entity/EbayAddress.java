package com.springrain.erp.modules.ebay.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.web.util.HtmlUtils;

import com.ebay.soap.eBLBaseComponents.AddressType;

@Entity
@Table(name = "ebay_address")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class EbayAddress {

	private Integer id;
	private String name;
	private String street;
	private String street1;
	private String street2;
	private String cityName;
	private String county;
	private String stateOrProvince;
	private String countryCode;
	private String postalCode;
	private String phone;
	private EbayOrder order;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
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
	public String getCounty() {
		return HtmlUtils.htmlUnescape(county);
	}
	public void setCounty(String county) {
		this.county = county;
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
	public EbayOrder getOrder() {
		return order;
	}
	public void setOrder(EbayOrder order) {
		this.order = order;
	}
	
	public EbayAddress(){};
	
	public EbayAddress(AddressType address, EbayOrder order) {
		this.name = address.getName();
		this.street = address.getStreet();
		this.street1= address.getStreet1();
		this.street2 = address.getStreet2();
		this.cityName = address.getCityName();
		this.county = address.getCounty();
		this.stateOrProvince = address.getStateOrProvince();
		this.postalCode = address.getPostalCode();
		
		if(address.getCountry()!=null){
			this.countryCode = address.getCountry().value();
		}
		this.phone = address.getPhone();
		this.order = order;
	}
}
