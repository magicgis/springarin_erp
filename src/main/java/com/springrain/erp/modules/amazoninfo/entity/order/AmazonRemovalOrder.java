package com.springrain.erp.modules.amazoninfo.entity.order;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Entity
@Table(name = "amazoninfo_removal_order")
@DynamicInsert
@DynamicUpdate
public class AmazonRemovalOrder{
	
    private String amazonOrderId; 

    private Date purchaseDate;//创建订单的日期。

    private Date lastUpdateDate;
    
    private String serviceSpeed;

    private String orderStatus;

    private String country;

    private String orderType;
    
    private Integer id;
    
    private Date createDate;
    
    private List<AmazonRemovalOrderItem> items = Lists.newArrayList();
    

    private Integer requestedQty;
    
    private Integer completedQty;
    
    private Integer cancelledQty;
    
    private Integer inProcessQty;
    
    private String sku;
    
    private String accountName;
    
    private String source;//非数据库字段,仅做前台数据传输
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getServiceSpeed() {
		return serviceSpeed;
	}

	public void setServiceSpeed(String serviceSpeed) {
		this.serviceSpeed = serviceSpeed;
	}

	public String getAmazonOrderId() {
		return amazonOrderId;
	}
	public void setAmazonOrderId(String amazonOrderId) {
		this.amazonOrderId = amazonOrderId;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getPurchaseDate() {
		return purchaseDate;
	}
	
	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}


	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	@OneToMany(mappedBy = "order",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<AmazonRemovalOrderItem> getItems() {
		return items;
	}

	public void setItems(List<AmazonRemovalOrderItem> items) {
		this.items = items;
	}
	
	@Transient
	public Map<String,AmazonRemovalOrderItem> getItemsMap() {
		Map<String,AmazonRemovalOrderItem> rs = Maps.newHashMap();
		for (AmazonRemovalOrderItem item : items) {
			String sku = item.getSellersku();
			String type = item.getDisposition();
			String request = item.getRequestedQty()+"";
			String currency = item.getCurrency();
			String key = sku+"_"+type+"_"+request+"_"+currency;
			rs.put(key,item);
		}
		return rs;
	}
	
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@Transient
	public Integer getRequestedQty() {
		return requestedQty;
	}

	public void setRequestedQty(Integer requestedQty) {
		this.requestedQty = requestedQty;
	}

	@Transient
	public Integer getCompletedQty() {
		return completedQty;
	}

	public void setCompletedQty(Integer completedQty) {
		this.completedQty = completedQty;
	}

	@Transient
	public Integer getCancelledQty() {
		return cancelledQty;
	}

	public void setCancelledQty(Integer cancelledQty) {
		this.cancelledQty = cancelledQty;
	}

	@Transient
	public Integer getInProcessQty() {
		return inProcessQty;
	}

	public void setInProcessQty(Integer inProcessQty) {
		this.inProcessQty = inProcessQty;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	@Column(updatable=false)
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	@PrePersist
	public void prePersist(){
		this.createDate = new Date();
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Transient
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * 判断是否有未入库的
	 * @return 0:没有  1：有
	 */
	@Transient
	public String getCanStore() {
		String rs = "0";
		for (AmazonRemovalOrderItem item : items) {
			if (item.getStoredQty() < item.getCompletedQty()) {
				rs = "1";
				break;
			}
		}
		return rs;
	}

	
	@Transient
	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public AmazonRemovalOrder(){
		
	}
	
	public AmazonRemovalOrder(String amazonOrderId, String country,	String orderType, String sku, Integer inProcessQty,Integer requestedQty,String orderStatus) {
		super();
		this.amazonOrderId = amazonOrderId;
		this.country = country;
		this.orderType = orderType;
		this.inProcessQty = inProcessQty;
		this.sku = sku;
		this.requestedQty=requestedQty;
		this.orderStatus =orderStatus;
	}
	
	
	
	
	
}
