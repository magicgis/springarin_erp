package com.springrain.erp.modules.amazoninfo.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;

@Entity
@Table(name = "settlementreport_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SettlementReportOrder {
	private Integer id;
	private String amazonOrderId;
	private String merchantOrderId;
	private String shipmentId;
	private String adjustmentId;
	private String marketplaceName;
	private String merchantFulfillmentId;
	private Date postedDate;
	private String country;
	
	private String type;
	private String settlementId;
	private String accountName;	//账号名称
	
	private Date addTime;
	
	private List<SettlementReportItem> items = Lists.newArrayList();

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSettlementId() {
		return settlementId;
	}

	public void setSettlementId(String settlementId) {
		this.settlementId = settlementId;
	}

	public String getAmazonOrderId() {
		return amazonOrderId;
	}

	public void setAmazonOrderId(String amazonOrderId) {
		this.amazonOrderId = amazonOrderId;
	}

	public String getMerchantOrderId() {
		return merchantOrderId;
	}

	public void setMerchantOrderId(String merchantOrderId) {
		this.merchantOrderId = merchantOrderId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(String shipmentId) {
		this.shipmentId = shipmentId;
	}

	public String getAdjustmentId() {
		return adjustmentId;
	}

	public void setAdjustmentId(String adjustmentId) {
		this.adjustmentId = adjustmentId;
	}

	public String getMarketplaceName() {
		return marketplaceName;
	}

	public void setMarketplaceName(String marketplaceName) {
		this.marketplaceName = marketplaceName;
	}

	public String getMerchantFulfillmentId() {
		return merchantFulfillmentId;
	}

	public void setMerchantFulfillmentId(String merchantFulfillmentId) {
		this.merchantFulfillmentId = merchantFulfillmentId;
	}

	public Date getPostedDate() {
		return postedDate;
	}

	public void setPostedDate(Date postedDate) {
		this.postedDate = postedDate;
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	@OneToMany(mappedBy = "order", fetch = FetchType.EAGER)
	@OrderBy(value = "id")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	@Cascade(CascadeType.SAVE_UPDATE)
	public List<SettlementReportItem> getItems() {
		return items;
	}

	public void setItems(List<SettlementReportItem> items) {
		this.items = items;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
}
