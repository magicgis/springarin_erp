/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.springrain.erp.common.persistence.DataEntity;
import com.springrain.erp.common.utils.StringUtils;

/**
 * 仓库Entity
 * @author tim
 * @version 2014-11-17
 */
@Entity
@Table(name = "psi_stock")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class Stock extends DataEntity<Stock> {
	
	private static final long serialVersionUID = 1L;
	private Integer id; 		// 编号
	private String addressLine1;
	private String addressLine2;
	private String city;
	private String districtorcounty;
	private String stateorprovincecode;
	private String countrycode;
	private String postalcode;
	private String platform;
	
	private String stockSign;
	private String stockName;
	private String type;
	private String name; 	// 名称
	private Float  capacity; //仓库容量(m³)

	
	public Stock(String addressLine1, String addressLine2, String city,
			String districtorcounty, String stateorprovincecode,
			String countrycode, String postalcode, String platform,
			String stockSign, String stockName, String type, String name) {
		super();
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.districtorcounty = districtorcounty;
		this.stateorprovincecode = stateorprovincecode;
		this.countrycode = countrycode;
		this.postalcode = postalcode;
		this.platform = platform;
		this.stockSign = stockSign;
		this.stockName = stockName;
		this.type = type;
		this.name = name;
	}

	public Stock() {
		super();
	}
	
	public Stock(Integer id) {
		this.id=id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrictorcounty() {
		return districtorcounty;
	}

	public void setDistrictorcounty(String districtorcounty) {
		this.districtorcounty = districtorcounty;
	}

	public String getStateorprovincecode() {
		return stateorprovincecode;
	}

	public void setStateorprovincecode(String stateorprovincecode) {
		this.stateorprovincecode = stateorprovincecode;
	}

	public String getCountrycode() {
		return countrycode;
	}

	public void setCountrycode(String countrycode) {
		this.countrycode = countrycode;
	}

	public String getPostalcode() {
		return postalcode;
	}

	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getStockSign() {
		return stockSign;
	}

	public void setStockSign(String stockSign) {
		this.stockSign = stockSign;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Float getCapacity() {
		return capacity;
	}

	public void setCapacity(Float capacity) {
		this.capacity = capacity;
	}

	
	@Transient
	public String getAddress() {
		String rs = "";
		rs = rs +(StringUtils.isEmpty(countrycode)?"":countrycode+" ");
		rs = rs +(StringUtils.isEmpty(districtorcounty)?"":districtorcounty+" ");
		rs = rs +(StringUtils.isEmpty(stateorprovincecode)?"":stateorprovincecode+" ");
		rs = rs +(StringUtils.isEmpty(city)?"":city+" ");
		rs = rs +(StringUtils.isEmpty(addressLine1)?"":addressLine1+" ");
		rs = rs +(StringUtils.isEmpty(addressLine2)?"":addressLine2+" ");
		rs = rs +(StringUtils.isEmpty(name)?"":(" ,"+name));
		return rs;
	}
}


