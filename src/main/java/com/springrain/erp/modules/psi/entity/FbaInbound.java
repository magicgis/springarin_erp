package com.springrain.erp.modules.psi.entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.utils.SkuComparator;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

/**
 * FBA帖子Entity
 * @author Tim
 * @version 2015-01-29
 */
@Entity
@Table(name = "psi_fba_inbound")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FbaInbound implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id; 
	
	private String shipmentId;

    private String shipmentName;

    private String shipFromAddress;
    
    private String docAddress;//用于伪造发货地址

    private String destinationFulfillmentCenterId;

    private String shipmentStatus;

    private String labelPrepType;

    private String areCasesRequired = "0";
	
	private User createBy;
	
	private User lastUpdateBy;
	
	private Date createDate;
	
	private Date lastUpdateDate;
	
	private Date arrivalDate;

	private String proessStatus;
	
	private Date shippedDate;
	
	private String country;
	
	private Float fee;
	
	private Integer tray;
	
	private Date deliveryDate;
	
	private String  supplier;        // 供应商
	
	private String dhlTracking;
	
	private String hasGenLabel="0";
	
	private String amazonZnr;//Amazon-Referenznr
	
	private Date toDhl;	//到达DHL的时间
	
	private Date finishDate;	//收货完成时间(收货量达到90%)
	
	private Date targetDate;	//预计收货时间（从亚马逊抓取）
	private String catchFlag;	//抓取标记 1：已抓取,针对closed状态
	
	//欧洲本地增加
	private Date responseTime;
	private User responseUser;
	private String responseLevel;
	
	private List<FbaInboundItem> items = Lists.newArrayList();
	
	public final static  Map<String,List<String>> labelPageTypeMap = Maps.newHashMap(); 
	
	
	private int totalCarton;
	private float totalWeight;
	private int totalQuantity;
	private String shippingAddress;
	private List<FbaInboundItem> tempItems = Lists.newArrayList();
	private String amazonWareHouse;
	
	
	//DPD 德国 包裹数量
	private Integer quantity1;//15kg (其他包裹数也放这个字段)
	private Integer quantity2;//30kg
	
	private Integer quantity3;//15kg (其他包裹数也放这个字段)(非数据库字段)
	private Integer quantity4;//30kg(非数据库字段)
	
	private BigDecimal weight;
	private BigDecimal volume;
	
	private      String          pdfFile;	//pdf凭证
	
	private      String          transportNo; //临时记录运单号
	private      String          amzReferenceId; //fba贴(页面抓取)对应的Amazon Reference ID
	
	private      String          countFlag;
	
	private      String          accountName;

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getCountFlag() {
		return countFlag;
	}

	public void setCountFlag(String countFlag) {
		this.countFlag = countFlag;
	}

	public Integer getQuantity1() {
		return quantity1;
	}

	public void setQuantity1(Integer quantity1) {
		this.quantity1 = quantity1;
	}
	
	public Integer getQuantity2() {
		return quantity2;
	}

	public void setQuantity2(Integer quantity2) {
		this.quantity2 = quantity2;
	}

	@Transient
	public String getAmazonWareHouse() {
		return amazonWareHouse;
	}

	public void setAmazonWareHouse(String amazonWareHouse) {
		this.amazonWareHouse = amazonWareHouse;
	}

	
	@Transient
	public String getTransportNo() {
		return transportNo;
	}

	public void setTransportNo(String transportNo) {
		this.transportNo = transportNo;
	}

	@Transient
	public String getAmazonZnr() {
		return amazonZnr;
	}

	public void setAmazonZnr(String amazonZnr) {
		this.amazonZnr = amazonZnr;
	}

	@Transient
	public List<FbaInboundItem> getTempItems() {
		return tempItems;
	}

	public void setTempItems(List<FbaInboundItem> tempItems) {
		this.tempItems = tempItems;
	}

	@Transient
	public String getShippingAddress() {
		return shippingAddress;
	}

	public void setShippingAddress(String shippingAddress) {
		this.shippingAddress = shippingAddress;
	}

	@Transient
	public int getTotalCarton() {
		return totalCarton;
	}

	@Column(updatable=false)
	public String getDocAddress() {
		return docAddress;
	}

	public void setDocAddress(String docAddress) {
		this.docAddress = docAddress;
	}

	public void setTotalCarton(int totalCarton) {
		this.totalCarton = totalCarton;
	}
	@Transient
	public float getTotalWeight() {
		return totalWeight;
	}

	public void setTotalWeight(float totalWeight) {
		this.totalWeight = totalWeight;
	}
	@Transient
	public int getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public FbaInbound() {
		super();
	}
	
	public FbaInbound(FbaInbound copy) {
		this.shipFromAddress = copy.getShipFromAddress();
		this.docAddress = copy.getDocAddress();
		this.shipmentStatus = copy.getShipmentStatus();
		this.shipmentName = copy.getShipmentName();
		this.labelPrepType = copy.getLabelPrepType();
		this.createBy = copy.getCreateBy();
		this.lastUpdateBy = copy.getLastUpdateBy();
		this.createDate = copy.getCreateDate();
		this.lastUpdateDate = copy.getLastUpdateDate();
		this.country = copy.getCountry();
	}

	public FbaInbound(Integer id){
		this();
		this.id = id;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
	
	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getShipmentId() {
		return shipmentId;
	}

	public void setShipmentId(String shipmentId) {
		this.shipmentId = shipmentId;
	}

	public String getShipmentName() {
		return shipmentName;
	}

	public void setShipmentName(String shipmentName) {
		this.shipmentName = shipmentName;
	}
	
	@Column(updatable=false)
	public String getShipFromAddress() {
		return shipFromAddress;
	}

	public void setShipFromAddress(String shipFromAddress) {
		this.shipFromAddress = shipFromAddress;
	}

	public String getDestinationFulfillmentCenterId() {
		return destinationFulfillmentCenterId;
	}

	public void setDestinationFulfillmentCenterId(
			String destinationFulfillmentCenterId) {
		this.destinationFulfillmentCenterId = destinationFulfillmentCenterId;
	}

	public String getShipmentStatus() {
		return shipmentStatus;
	}

	public void setShipmentStatus(String shipmentStatus) {
		this.shipmentStatus = shipmentStatus;
	}

	public String getLabelPrepType() {
		return labelPrepType;
	}

	public void setLabelPrepType(String labelPrepType) {
		this.labelPrepType = labelPrepType;
	}

	
	public String getAreCasesRequired() {
		return areCasesRequired;
	}

	public void setAreCasesRequired(String areCasesRequired) {
		this.areCasesRequired = areCasesRequired;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateBy() {
		return createBy;
	}

	public void setCreateBy(User createBy) {
		this.createBy = createBy;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getLastUpdateBy() {
		return lastUpdateBy;
	}

	public void setLastUpdateBy(User lastUpdateBy) {
		this.lastUpdateBy = lastUpdateBy;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public String getProessStatus() {
		return proessStatus;
	}

	public void setProessStatus(String proessStatus) {
		this.proessStatus = proessStatus;
	}
	
	@Column(updatable=false)
	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getShippedDate() {
		return shippedDate;
	}

	public void setShippedDate(Date shippedDate) {
		this.shippedDate = shippedDate;
	}
	
	@OneToMany(mappedBy = "fbaInbound",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@OrderBy(value="sku,quantityInCase DESC")
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<FbaInboundItem> getItems() {
		return items;
	}
	
	@Transient
	public List<FbaInboundItem> getItemsByOrder() {
		Collections.sort(items,new SkuComparator()); 
		return items;
	}
	
	public void setItems(List<FbaInboundItem> items) {
		this.items = items;
	}
	
	@Transient
	public Map<String,FbaInboundItem> getItemsMap() {
		Map<String,FbaInboundItem> rs = Maps.newHashMap();
		//排除重复
		for (Iterator<FbaInboundItem> iterator = this.items.iterator(); iterator.hasNext();) {
			FbaInboundItem item = iterator.next();
			String sku = item.getSku();
			if(rs.get(sku)!=null){
				iterator.remove();
			}else{
				rs.put(sku,item);
			}
		}
		return rs;
	}
	
	
	@Transient
	public Integer getQuantityShipped() {
		int rs = 0;
		for (FbaInboundItem item : this.items) {
			rs+=(item.getQuantityShipped()==null?0:item.getQuantityShipped());
		}
		return rs;
	}
	
	@Transient
	public Integer getQuantityReceived() {
		int rs = 0;
		for (FbaInboundItem item : this.items) {
			rs+=(item.getQuantityReceived()==null?0:item.getQuantityReceived());
		}
		return rs;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getHasGenLabel() {
		return hasGenLabel;
	}

	public void setHasGenLabel(String hasGenLabel) {
		this.hasGenLabel = hasGenLabel;
	}
	
	
	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public Float getFee() {
		return fee;
	}

	public void setFee(Float fee) {
		this.fee = fee;
	}

	public Integer getTray() {
		return tray;
	}

	public void setTray(Integer tray) {
		this.tray = tray;
	}

	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	
	public String getDhlTracking() {
		return dhlTracking;
	}

	public void setDhlTracking(String dhlTracking) {
		this.dhlTracking = dhlTracking;
	}
	
	public Date getToDhl() {
		return toDhl;
	}

	public void setToDhl(Date toDhl) {
		this.toDhl = toDhl;
	}

	public Date getFinishDate() {
		return finishDate;
	}

	public void setFinishDate(Date finishDate) {
		this.finishDate = finishDate;
	}

	public Date getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(Date targetDate) {
		this.targetDate = targetDate;
	}

	public String getCatchFlag() {
		return catchFlag;
	}

	public void setCatchFlag(String catchFlag) {
		this.catchFlag = catchFlag;
	}

	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getArrivalDate() {
		return arrivalDate;
	}
	
	public String getPdfFile() {
		return pdfFile;
	}

	public void setPdfFile(String pdfFile) {
		this.pdfFile = pdfFile;
	}

	@Transient
	public Set<String> getSkus() {
		Set<String> rs = Sets.newHashSet();
		for (FbaInboundItem item : this.items) {
			rs.add(item.getSku());
		}
		return rs;
	}

	public void setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
	}
	
	@Transient
	public String getError() {
		if("RECEIVING,CLOSED".contains(shipmentStatus)){
			for (FbaInboundItem item : this.items) {
				if(!item.getQuantityShipped().equals(item.getQuantityReceived())){
					return "1";
				}
			}
		}
		return "0";
	}
	
	//判断是否抓取到错误记录
	@Transient
	public String getHasProblem() {
		for (FbaInboundItem item : this.items) {
			if(StringUtils.isNotEmpty(item.getProblem())){
				return "1";
			}
		}
		return "0";
	}
	
	//错误记录链接地址
	@Transient
	public String getProblemUrl() {
		if("1".equals(getHasProblem())){
			String suffix = country;
			if("jp,uk".contains(suffix)){
				suffix = "co."+suffix;
			}else if ("mx".equals(suffix)){
				suffix = "com."+suffix;
			}
			String link = "https://sellercentral.amazon."+suffix+"/gp/fba/inbound-shipment-workflow/index.html/ref=ag_fbaisw_name_fbasqs#"+shipmentId+"/summary/problems";
			return link;
		}
		return "";
	}
	
	
/*
	public FbaInbound(InboundShipmentInfo inboundShipmentInfo,String country){
		this.shipmentName = inboundShipmentInfo.getShipmentName();
		if("uk".equals(country)){
			if(shipmentName.contains("]")){
				this.country = shipmentName.split("]")[0].replace("[", "").trim().toLowerCase();
				if("gb".equals(this.country)||"uk".equals(this.country)){
					this.country = "uk";
				}
			}else{
				this.country = "de";
			}
		}else if("ca".equals(country)){
			if(shipmentName.contains("]")){
				this.country = shipmentName.split("]")[0].replace("[", "").trim().toLowerCase();
				if("us".equals(this.country)){
					this.country = "com1";
				}
			}
		}else{
			this.country = country;
		}
		this.areCasesRequired = inboundShipmentInfo.getAreCasesRequired()?"1":"0";
		this.createDate = new Date();
		this.destinationFulfillmentCenterId = inboundShipmentInfo.getDestinationFulfillmentCenterId();
		this.labelPrepType = inboundShipmentInfo.getLabelPrepType();
		if(shipmentName.contains("From")){
			this.shipFromAddress = shipmentName.split("From")[1].trim();
			if(!"CN,DE,JP,US".contains(shipFromAddress)){
				this.shipFromAddress = inboundShipmentInfo.getShipFromAddress().getCountryCode();
			}
		}else{
			this.shipFromAddress = inboundShipmentInfo.getShipFromAddress().getCountryCode();
		}
		
		this.docAddress = this.shipFromAddress;
		this.shipmentId = inboundShipmentInfo.getShipmentId();
		this.shipmentStatus = inboundShipmentInfo.getShipmentStatus();
	}*/
	
	public String toJson() {
		StringBuilder rs = new StringBuilder("[");
		for (FbaInboundItem item : items) {
			rs.append(item.toString()).append(",");
		}
		String str =rs.toString();
		if(str.length()>1){
			str = str.substring(0,str.length()-1);
		}
		str+="]";
		return str;
	}

	public String getAmzReferenceId() {
		return amzReferenceId;
	}

	public void setAmzReferenceId(String amzReferenceId) {
		this.amzReferenceId = amzReferenceId;
	}
	
	public List<String> genCartonsFile(File dir,Map<String,Integer> skuBybagMap,String sellerId) {
		Document doc = DocumentHelper.createDocument();
		Element rootElmt = doc.addElement("AmazonEnvelope");
		Element headerEl = rootElmt.addElement("Header");
		headerEl.addElement("DocumentVersion").addText("1.01");
		headerEl.addElement("MerchantIdentifier").addText(sellerId);
		rootElmt.addElement("MessageType").addText("CartonContentsRequest");
		Element msg = rootElmt.addElement("Message");
		msg.addElement("MessageID").addText("1");
		Element request = msg.addElement("CartonContentsRequest");
		request.addElement("ShipmentId").addText(shipmentId);
		
		int totalPackage = 1;
		List<String> packageList = Lists.newArrayList();
		List<Element> cartonsEl = Lists.newArrayList();
		for (FbaInboundItem item : getItemsByOrder()) {
			String sku = item.getSku();
			Integer pack = item.getPackQuantity();
			if(pack==null){
				pack = skuBybagMap.get(sku);
			}
			if(sku.contains("-DB1001")){
				if("com,uk,jp,ca,mx,".contains(country+",")){
					pack=60;
				}else{
					pack=44;
				}
			}else if(sku.contains("-DB2001")){
				if("com,jp,ca,mx,".contains(country+",")){
					pack=32;
				}else{
					pack=24;
				}
			}
			int lastQ = item.getQuantityShipped()%pack;
			int bags = lastQ==0?(item.getQuantityShipped()/pack):((item.getQuantityShipped()/pack)+1);
			for (int i = 0; i < bags; i++) {
				Element carton = DocumentHelper.createElement("Carton");
				cartonsEl.add(carton);
				String cartonId = totalPackage+"";
				cartonId = cartonId.length()+cartonId;
				carton.addElement("CartonId").addText(cartonId);
				packageList.add(cartonId);
				totalPackage++;
				Element itemEl = carton.addElement("Item");
				itemEl.addElement("SKU").addText(sku);
				if(lastQ==0){
					itemEl.addElement("QuantityShipped").addText(pack+"");
				}else{
					if(i == (bags-1)){
						itemEl.addElement("QuantityShipped").addText(lastQ+"");
					}else{
						itemEl.addElement("QuantityShipped").addText(pack+"");
					}
				}
				itemEl.addElement("QuantityInCase").addText(pack+"");
			}
		}
		request.addElement("NumCartons").addText((totalPackage-1)+"");
		
		for (Element element : cartonsEl) {
			 request.add(element);
		}
		File rs = new File(dir ,"data.xml");
		try {
			// 定义输出流的目的地
			FileWriter fw = new FileWriter(rs);
			// 定义输出格式和字符集
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");
			// 定义用于输出xml文件的XMLWriter对象
			XMLWriter xmlWriter = new XMLWriter(fw, format);
			xmlWriter.write(doc);
			xmlWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return packageList;
	}

	@Transient
	public Integer getQuantity3() {
		return quantity3;
	}

	public void setQuantity3(Integer quantity3) {
		this.quantity3 = quantity3;
	}

	@Transient
	public Integer getQuantity4() {
		return quantity4;
	}

	public void setQuantity4(Integer quantity4) {
		this.quantity4 = quantity4;
	}

	public Date getResponseTime() {
		return responseTime;
	}

	public void setResponseTime(Date responseTime) {
		this.responseTime = responseTime;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getResponseUser() {
		return responseUser;
	}

	public void setResponseUser(User responseUser) {
		this.responseUser = responseUser;
	}

	public String getResponseLevel() {
		return responseLevel;
	}

	public void setResponseLevel(String responseLevel) {
		this.responseLevel = responseLevel;
	}
	
}


