package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.List;

import com.google.common.collect.Lists;

public class ProductPriceApprovalTemp implements Serializable {

	private static final long serialVersionUID = 1L;

	private String country;
	private String reason;
	private String accountName;
	
	private List<ProductPriceApproval> prices = Lists.newArrayList();
	
	public List<ProductPriceApproval> getPrices() {
		return prices;
	}

	public void setPrices(List<ProductPriceApproval> prices) {
		this.prices = prices;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	
}
