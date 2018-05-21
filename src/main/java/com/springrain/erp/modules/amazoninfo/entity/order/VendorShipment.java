package com.springrain.erp.modules.amazoninfo.entity.order;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.google.common.collect.Lists;
import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "amazoninfo_vendor_shipment")
public class VendorShipment implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private String asn;
	
	private String country;
	
	private String shipmentStatus;
	
	private String type;
	
	private String status;//ERP状态
	
	private Date shipDate;
	
	private Date shippedDate;
	
	private Date deliveryDate;
	
	private String freightTerms;//货运方面
	
	private String carrierSCAC;//承运人
	
	private String carrierTracking;
	
	private String shipAddress;
	
	private Integer packages;
	
	private Integer stackedPallets;
	
	private Integer unstackedPallets;
	
	private Float fee;
	
	
	private User checkUser;
	
	private User deliveryUser;
	
	private String billStatu;
	
	private String checkStatu;
	
	private List<VendorOrder> orders = Lists.newArrayList();
	
	public String getCheckStatu() {
		return checkStatu;
	}

	public void setCheckStatu(String checkStatu) {
		this.checkStatu = checkStatu;
	}

	public String getBillStatu() {
		return billStatu;
	}

	public void setBillStatu(String billStatu) {
		this.billStatu = billStatu;
	}

	@ManyToOne()
	@JoinColumn(name = "check_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCheckUser() {
		return checkUser;
	}

	public void setCheckUser(User checkUser) {
		this.checkUser = checkUser;
	}

	@ManyToOne()
	@JoinColumn(name = "delivery_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getDeliveryUser() {
		return deliveryUser;
	}

	public void setDeliveryUser(User deliveryUser) {
		this.deliveryUser = deliveryUser;
	}

	public Float getFee() {
		return fee;
	}

	public void setFee(Float fee) {
		this.fee = fee;
	}

	public String getAsn() {
		return asn;
	}

	public void setAsn(String asn) {
		this.asn = asn;
	}

	public String getShipmentStatus() {
		return shipmentStatus;
	}

	public void setShipmentStatus(String shipmentStatus) {
		this.shipmentStatus = shipmentStatus;
	}
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Date getShippedDate() {
		return shippedDate;
	}

	public void setShippedDate(Date shippedDate) {
		this.shippedDate = shippedDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getShipDate() {
		return shipDate;
	}

	public void setShipDate(Date shipDate) {
		this.shipDate = shipDate;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getFreightTerms() {
		return freightTerms;
	}

	public void setFreightTerms(String freightTerms) {
		this.freightTerms = freightTerms;
	}

	@Column(name="carrierSCAC")
	public String getCarrierSCAC() {
		return carrierSCAC;
	}

	public void setCarrierSCAC(String carrierSCAC) {
		this.carrierSCAC = carrierSCAC;
	}

	public String getCarrierTracking() {
		return carrierTracking;
	}

	public void setCarrierTracking(String carrierTracking) {
		this.carrierTracking = carrierTracking;
	}

	public String getShipAddress() {
		return shipAddress;
	}

	public void setShipAddress(String shipAddress) {
		this.shipAddress = shipAddress;
	}

	public Integer getPackages() {
		return packages;
	}

	public void setPackages(Integer packages) {
		this.packages = packages;
	}

	public Integer getStackedPallets() {
		return stackedPallets;
	}

	public void setStackedPallets(Integer stackedPallets) {
		this.stackedPallets = stackedPallets;
	}

	public Integer getUnstackedPallets() {
		return unstackedPallets;
	}

	public void setUnstackedPallets(Integer unstackedPallets) {
		this.unstackedPallets = unstackedPallets;
	}

	@OneToMany(mappedBy = "shipment",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<VendorOrder> getOrders() {
		return orders;
	}

	
	public void setOrders(List<VendorOrder> orders) {
		this.orders = orders;
	}

	public VendorShipment() {}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}
