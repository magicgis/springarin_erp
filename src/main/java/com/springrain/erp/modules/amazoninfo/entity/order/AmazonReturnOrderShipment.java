package com.springrain.erp.modules.amazoninfo.entity.order;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.springrain.erp.common.utils.HttpRequest;

@Entity
@Table(name = "amazoninfo_return_order_shipment")
public class AmazonReturnOrderShipment{
	
    private Integer id;
    
    private String orderId;
    
    private String shipmentId;
    
    private String shippedDate;
    
    private String trackingNumber;
    
    private String trackingState;
  
  
    private List<AmazonReturnOrderShipmentItem> items = Lists.newArrayList();
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(String shipmentId) {
		this.shipmentId = shipmentId;
	}

	public String getShippedDate() {
		return shippedDate;
	}

	public void setShippedDate(String shippedDate) {
		this.shippedDate = shippedDate;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public String getTrackingState() {
		return trackingState;
	}

	public void setTrackingState(String trackingState) {
		this.trackingState = trackingState;
	}

	@OneToMany(mappedBy = "order",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<AmazonReturnOrderShipmentItem> getItems() {
		return items;
	}

	public void setItems(List<AmazonReturnOrderShipmentItem> items) {
		this.items = items;
	}
	
	@Transient
	public String getTracking(){
		String url=trackingNumber;
		if(url.contains("tools.usps.com")){
			url="http://wwwapps.ups.com/WebTracking/track?track=yes&trackNums="+url.split(" ")[1];
		}else if(url.contains("www.abfs")){
			url="https://arcb.com/tools/tracking.html#/"+url.split(" ")[1];
		}else if(url.toUpperCase().contains("(EXLA)")||url.toUpperCase().contains("(ESTES EXPRESS)")){
			url="http://www.estes-express.com/WebApp/ShipmentTracking/MainServlet?submitFromForm=yes&search_type=PRO&search_criteria="+url.toUpperCase().replace("(ESTES EXPRESS)","").replace("(EXLA)","").trim()+"&searchedType=PRO&searchedCriteria=%5BNUMBER%5D";
		}else if(url.contains("http://www.estes-express.com")){
			url="http://www.estes-express.com/WebApp/ShipmentTracking/MainServlet?submitFromForm=yes&search_type=PRO&search_criteria="+url.split(" ")[1]+"&searchedType=PRO&searchedCriteria=%5BNUMBER%5D";
		}else if(url.contains("www.fedex.com")){
			url="https://www.fedex.com/apps/fedextrack/?action=track&action=track&tracknumbers="+url.split(" ")[1];
		}else if(url.toUpperCase().contains("(UPS FREIGHT)")||url.toUpperCase().contains("(UPGF)")||url.toUpperCase().contains("(UPS GROUND)")){
			url="http://wwwapps.ups.com/WebTracking/track?track=yes&trackNums="+url.toUpperCase().replace("(UPS FREIGHT)","").replace("(UPGF)","").replace("(UPS GROUND)","").trim();
		}
		return url;
	}
}
