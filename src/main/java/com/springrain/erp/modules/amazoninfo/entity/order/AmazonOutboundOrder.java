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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
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
import com.google.common.collect.Maps;
import com.springrain.erp.modules.custom.entity.Event;
import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "amazoninfo_outbound_order")
public class AmazonOutboundOrder{
	
	
	public static Map<String,String> displayableOrderCommentMap;
	
	static{
		displayableOrderCommentMap = Maps.newHashMap();
		displayableOrderCommentMap.put("de", "Danke für Ihren Inateck Einkauf.");
		displayableOrderCommentMap.put("fr", "Merci de votre achat chez Inateck.");
		displayableOrderCommentMap.put("it", "Grazie per aver acquistato i prodotti Inateck.");
		displayableOrderCommentMap.put("es", "Gracias por comprar los productos Inateck.");
		displayableOrderCommentMap.put("uk", "Thank you for purchasing Inateck products!");
		displayableOrderCommentMap.put("com", "Thank you for purchasing Inateck products!");
		displayableOrderCommentMap.put("jp", "Inateckをご愛顧いただいて、ありがとうございました。");
		displayableOrderCommentMap.put("ca", "Thank you for purchasing Inateck products!");
	}
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	
    private String amazonOrderId;  //创建的时候 如果是替代为原订单号；生成后放置亚马逊生成的订单号

    private String sellerOrderId;

    private Date lastUpdateDate;

    private String orderStatus;//Planning Shipping Delivery Complete Canceled

    private String shippingSpeedCategory;//Standard(普通) Expedited(加急) 
    
    private String orderType;//Review(评测) Support(替代)
    
    private String fulfillmentAction;//Ship - 立即配送   Hold - 暂缓配送。 默认为Ship;Hold可以暂缓发出，2周还不Ship订单自动取消
    
    private String displayableOrderComment;

    private   String  country;//

    private AmazonOutboundAddress shippingAddress;
    
    private List<AmazonOutboundShipment> shipmentItems = Lists.newArrayList();
    
    private String buyerEmail;

    private String buyerName;

    private Date earliestShipDate;//您承诺的订单发货时间范围的第一天

    private Date latestShipDate;//您承诺的订单最晚发货时间范围的第一天

    private Date earliestDeliveryDate;//最早到达时间

    private Date latestDeliveryDate;//最晚到达时间
    
    private Date createDate;
    private User createUser;
    
    private Date checkDate;
    private User checkUser;
    
    private String oldOrderId;
    
    private List<AmazonOutboundOrderItem> items = Lists.newArrayList();
    
    private Date cancelDate;
    private User cancelUser;
    
    private String weight;
    
    private Float fbaPerUnitFulfillmentFee;
    
    private Float fbaTransportationFee;
    
    private Float fbaPerOrderFulfillmentFee;
    
    private String remark;
    
	private String customId;
	
    private  String  eventId;
    
    private  String eventType;
    
    private Float amazonFee;	//结算报表中统计的亚马逊费用
	private Integer supportNum;	//替代货数量(非数据库字段)
	private Integer reviewNum;	//评测数量(非数据库字段)
    
	private String amazonOrEbay;
	
	private String flag;
	
	private String accountName;
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	@Transient
	public String getAmazonOrEbay() {
		return amazonOrEbay;
	}
	public void setAmazonOrEbay(String amazonOrEbay) {
		this.amazonOrEbay = amazonOrEbay;
	}
	
	@Transient
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
    public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}

	public String getOldOrderId() {
		return oldOrderId;
	}

	public void setOldOrderId(String oldOrderId) {
		this.oldOrderId = oldOrderId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}

	@ManyToOne()
	@JoinColumn(name = "cancel_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCancelUser() {
		return cancelUser;
	}

	public void setCancelUser(User cancelUser) {
		this.cancelUser = cancelUser;
	}

	public String getAmazonOrderId() {
		return amazonOrderId;
	}
	public void setAmazonOrderId(String amazonOrderId) {
		this.amazonOrderId = amazonOrderId;
	}

	public String getSellerOrderId() {
		return sellerOrderId;
	}
	
	public void setSellerOrderId(String sellerOrderId) {
		this.sellerOrderId = sellerOrderId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
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

	public String getBuyerEmail() {
		return buyerEmail;
	}

	public void setBuyerEmail(String buyerEmail) {
		this.buyerEmail = buyerEmail;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	public Date getEarliestShipDate() {
		return earliestShipDate;
	}

	public void setEarliestShipDate(Date earliestShipDate) {
		this.earliestShipDate = earliestShipDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	public Date getLatestShipDate() {
		return latestShipDate;
	}

	public void setLatestShipDate(Date latestShipDate) {
		this.latestShipDate = latestShipDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	public Date getEarliestDeliveryDate() {
		return earliestDeliveryDate;
	}

	public void setEarliestDeliveryDate(Date earliestDeliveryDate) {
		this.earliestDeliveryDate = earliestDeliveryDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	public Date getLatestDeliveryDate() {
		return latestDeliveryDate;
	}

	public void setLatestDeliveryDate(Date latestDeliveryDate) {
		this.latestDeliveryDate = latestDeliveryDate;
	}

	@OneToOne
	@JoinColumn(name="shipping_address")
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public AmazonOutboundAddress getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(AmazonOutboundAddress shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	
	
	@OneToMany(mappedBy = "order",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@OrderBy(value="id")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<AmazonOutboundOrderItem> getItems() {
		return items;
	}

	public void setItems(List<AmazonOutboundOrderItem> items) {
		this.items = items;
	}
	
	
	
	@OneToMany(mappedBy = "order",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@OrderBy(value="id")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<AmazonOutboundShipment> getShipmentItems() {
		return shipmentItems;
	}
	public void setShipmentItems(List<AmazonOutboundShipment> shipmentItems) {
		this.shipmentItems = shipmentItems;
	}
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
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
	
	
	public String getShippingSpeedCategory() {
		return shippingSpeedCategory;
	}

	public void setShippingSpeedCategory(String shippingSpeedCategory) {
		this.shippingSpeedCategory = shippingSpeedCategory;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getFulfillmentAction() {
		return fulfillmentAction;
	}

	public void setFulfillmentAction(String fulfillmentAction) {
		this.fulfillmentAction = fulfillmentAction;
	}

	public String getDisplayableOrderComment() {
		return displayableOrderComment;
	}

	public void setDisplayableOrderComment(String displayableOrderComment) {
		this.displayableOrderComment = displayableOrderComment;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public Float getFbaPerUnitFulfillmentFee() {
		return fbaPerUnitFulfillmentFee;
	}

	public void setFbaPerUnitFulfillmentFee(Float fbaPerUnitFulfillmentFee) {
		this.fbaPerUnitFulfillmentFee = fbaPerUnitFulfillmentFee;
	}

	public Float getFbaTransportationFee() {
		return fbaTransportationFee;
	}

	public void setFbaTransportationFee(Float fbaTransportationFee) {
		this.fbaTransportationFee = fbaTransportationFee;
	}
	
	
	public Float getFbaPerOrderFulfillmentFee() {
		return fbaPerOrderFulfillmentFee;
	}
	public void setFbaPerOrderFulfillmentFee(Float fbaPerOrderFulfillmentFee) {
		this.fbaPerOrderFulfillmentFee = fbaPerOrderFulfillmentFee;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne()
	@JoinColumn(name = "create_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
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

	public Float getAmazonFee() {
		return amazonFee;
	}
	public void setAmazonFee(Float amazonFee) {
		this.amazonFee = amazonFee;
	}
	
	@Transient
	public Integer getSupportNum() {
		return supportNum;
	}
	
	public void setSupportNum(Integer supportNum) {
		this.supportNum = supportNum;
	}
	
	@Transient
	public Integer getReviewNum() {
		return reviewNum;
	}
	
	public void setReviewNum(Integer reviewNum) {
		this.reviewNum = reviewNum;
	}
	
	@Transient
	public String getUrlLink(){
		String suf = country;
		if(StringUtils.isNotBlank(suf)){
			if("jp,uk".contains(suf)){
				suf = "co."+suf;
			}else if("mx".contains(suf)){
				suf = "com."+suf;
			}
		}
		return "https://sellercentral.amazon."+suf+"/gp/orders/fba-order-details.html?ie=UTF8&orderID="+amazonOrderId;
	}
	
	@Transient
	public Map<String,List<AmazonOutboundShipment>> getTrackingList(){
		Map<String,List<AmazonOutboundShipment>> map=Maps.newHashMap();
		if(shipmentItems!=null&&shipmentItems.size()>0){
			for (AmazonOutboundShipment item : shipmentItems) {
				List<AmazonOutboundShipment> list=map.get(item.getTrackSupplier()+","+item.getTrackNumber());
				if(list==null){
					list=Lists.newArrayList();
					map.put(item.getTrackSupplier()+","+item.getTrackNumber(),list);
				}
				list.add(item);
			}
		}
		return map;
	}
	public AmazonOutboundOrder() {}

}
