package com.springrain.erp.modules.amazoninfo.entity.order;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.web.util.HtmlUtils;
import com.amazonservices.mws.orders._2013_09_01.model.Address;

@Entity
@Table(name = "amazoninfo_unline_address")
public class AmazonUnlineAddress{

	private Integer id;
	
    private String name;

    private String addressLine1;

    private String addressLine2;

    private String addressLine3;

    private String city;

    private String county;//县

    private String district;//地方

    private String stateOrRegion;//省份

    private String postalCode;

    private String countryCode;

    private String phone;
    
    private AmazonUnlineOrder order;

	public String getName() {
		return HtmlUtils.htmlUnescape(name);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddressLine1() {
		return HtmlUtils.htmlUnescape(addressLine1);
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return HtmlUtils.htmlUnescape(addressLine2);
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getAddressLine3() {
		return HtmlUtils.htmlUnescape(addressLine3);
	}

	public void setAddressLine3(String addressLine3) {
		this.addressLine3 = addressLine3;
	}

	public String getCity() {
		return HtmlUtils.htmlUnescape(city);
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCounty() {
		return HtmlUtils.htmlUnescape(county);
	}

	public void setCounty(String county) {
		this.county = county;
	}

	public String getDistrict() {
		return HtmlUtils.htmlUnescape(district);
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getStateOrRegion() {
		return HtmlUtils.htmlUnescape(stateOrRegion);
	}

	public void setStateOrRegion(String stateOrRegion) {
		this.stateOrRegion = stateOrRegion;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCountryCode() {
		return HtmlUtils.htmlUnescape(countryCode);
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@OneToOne(fetch=FetchType.LAZY,optional=false)
	@JoinColumn(name="order_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonUnlineOrder getOrder() {
		return order;
	}

	public void setOrder(AmazonUnlineOrder order) {
		this.order = order;
	}
	
	public AmazonUnlineAddress() {}
	
	public AmazonUnlineAddress(Address address, AmazonUnlineOrder order) {
		super();
		this.name = address.getName();
		this.addressLine1 = address.getAddressLine1();
		this.addressLine2 = address.getAddressLine2();
		this.addressLine3 = address.getAddressLine3();
		this.city = address.getCity();
		this.county = address.getCounty();
		this.district = address.getDistrict();
		this.stateOrRegion = address.getStateOrRegion();
		this.postalCode = address.getPostalCode();
		this.countryCode = address.getCountryCode();
		this.phone = address.getPhone();
		this.order = order;
	}
}
