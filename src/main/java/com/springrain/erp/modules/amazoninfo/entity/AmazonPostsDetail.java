package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.springrain.erp.modules.psi.entity.FbaInboundItem;


@Entity
@Table(name = "amazoninfo_posts_detail")
public class AmazonPostsDetail implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id;
	private AmazonPostsDetail parentPortsDetail;
	private List<AmazonPostsDetail> children;
	
	private String asin;
	private String country;
	private String productName;
	private Date queryTime;
	private String  binding;
	private String  brand;
	private String label;
	private String manufacturer;
	private String publisher;
	private String studio;
	private String title;
	private Integer  packageQuantity;
	private Float  packageHeight;
	private Float  packageLength;
	private Float  packageWidth;
	private Float  packageWeight;
	private String    productGroup;
	private String    productTypeName;
    private String feature1;
    private String feature2;
    private String feature3;
    private String feature4;
    private String feature5;
	private String bySize;
	private String byColor;
	private String size;
	private String color;
	private Date createTime;
	private String partNumber;
	private List<AmazonCatalogRank> rankItems = Lists.newArrayList();
	 
	//补全字段 
	private String ean;
	private String sku;
	private String description;
	private String keyword1;
	private String keyword2;
	private String keyword3;
	private String keyword4;
	private String keyword5;
	
	private Integer star1 = 0;
	private Integer star2 = 0;
	private Integer star3 = 0;
	private Integer star4 = 0;
	private Integer star5 = 0;
	private Float star = 0f;
	
	private String compareStar;
	
	private String catalog1;
	private String catalog2;
	private String parentage;
	private String parentSku;
	private String relationshipType;
	private String variationTheme;
	
	private String productIdType;
	private String currency;
	private String conditionType;
	private String productTaxCode;
	private String fulfillmentCenterId;
	private String unit;
	
	private Float price;
	private Float salePrice;
	private Integer quantity;
	
	
	private String picture1;
	private String picture2;
	private String picture3;
	private String picture4;
	private String picture5;
	private String picture6;
	private String picture7;
	private String picture8;
	private String picture9;
	private String initPath;
	
	private String accountName;
	
	private List<FbaInboundItem> tempItems = Lists.newArrayList();
	
	
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getPicture9() {
		return picture9;
	}

	public void setPicture9(String picture9) {
		this.picture9 = picture9;
	}

	public String getPicture1() {
		return picture1;
	}

	public void setPicture1(String picture1) {
		this.picture1 = picture1;
	}

	public String getPicture2() {
		return picture2;
	}

	public void setPicture2(String picture2) {
		this.picture2 = picture2;
	}

	public String getPicture3() {
		return picture3;
	}

	public void setPicture3(String picture3) {
		this.picture3 = picture3;
	}

	public String getPicture4() {
		return picture4;
	}

	public void setPicture4(String picture4) {
		this.picture4 = picture4;
	}

	public String getPicture5() {
		return picture5;
	}

	public void setPicture5(String picture5) {
		this.picture5 = picture5;
	}

	public String getPicture6() {
		return picture6;
	}

	public void setPicture6(String picture6) {
		this.picture6 = picture6;
	}

	public String getPicture7() {
		return picture7;
	}

	public void setPicture7(String picture7) {
		this.picture7 = picture7;
	}

	public String getPicture8() {
		return picture8;
	}

	public void setPicture8(String picture8) {
		this.picture8 = picture8;
	}

	public String getInitPath() {
		return initPath;
	}

	public void setInitPath(String initPath) {
		this.initPath = initPath;
	}

	@OneToMany(mappedBy = "portsDetail",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@OrderBy(value="rank")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	public List<AmazonCatalogRank> getRankItems() {
		return rankItems;
	}

	public void setRankItems(List<AmazonCatalogRank> rankItems) {
		this.rankItems = rankItems;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKeyword1() {
		return keyword1;
	}

	public void setKeyword1(String keyword1) {
		this.keyword1 = keyword1;
	}

	public String getKeyword2() {
		return keyword2;
	}

	public void setKeyword2(String keyword2) {
		this.keyword2 = keyword2;
	}

	public String getKeyword3() {
		return keyword3;
	}

	public void setKeyword3(String keyword3) {
		this.keyword3 = keyword3;
	}

	public String getKeyword4() {
		return keyword4;
	}

	public void setKeyword4(String keyword4) {
		this.keyword4 = keyword4;
	}

	public String getKeyword5() {
		return keyword5;
	}

	public void setKeyword5(String keyword5) {
		this.keyword5 = keyword5;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
	public Date getQueryTime() {
		return queryTime;
	}

	public void setQueryTime(Date queryTime) {
		this.queryTime = queryTime;
	}

	public String getBinding() {
		return binding;
	}

	public void setBinding(String binding) {
		this.binding = binding;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getStudio() {
		return studio;
	}

	public void setStudio(String studio) {
		this.studio = studio;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public Integer getPackageQuantity() {
		return packageQuantity;
	}

	public void setPackageQuantity(Integer packageQuantity) {
		this.packageQuantity = packageQuantity;
	}

	public Float getPackageHeight() {
		return packageHeight;
	}

	public void setPackageHeight(Float packageHeight) {
		this.packageHeight = packageHeight;
	}

	public Float getPackageLength() {
		return packageLength;
	}

	public void setPackageLength(Float packageLength) {
		this.packageLength = packageLength;
	}

	public Float getPackageWidth() {
		return packageWidth;
	}

	public void setPackageWidth(Float packageWidth) {
		this.packageWidth = packageWidth;
	}

	public Float getPackageWeight() {
		return packageWeight;
	}

	public void setPackageWeight(Float packageWeight) {
		this.packageWeight = packageWeight;
	}

	public String getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}

	public String getProductTypeName() {
		return productTypeName;
	}

	public void setProductTypeName(String productTypeName) {
		this.productTypeName = productTypeName;
	}
	
	
	public String getPartNumber() {
		return partNumber;
	}

	public void setPartNumber(String partNumber) {
		this.partNumber = partNumber;
	}

	public String getFeature1() {
		return feature1;
	}

	public void setFeature1(String feature1) {
		this.feature1 = feature1;
	}

	public String getFeature2() {
		return feature2;
	}

	public void setFeature2(String feature2) {
		this.feature2 = feature2;
	}

	public String getFeature3() {
		return feature3;
	}

	public void setFeature3(String feature3) {
		this.feature3 = feature3;
	}

	public String getFeature4() {
		return feature4;
	}

	public void setFeature4(String feature4) {
		this.feature4 = feature4;
	}

	public String getFeature5() {
		return feature5;
	}

	public void setFeature5(String feature5) {
		this.feature5 = feature5;
	}

	public String getBySize() {
		return bySize;
	}

	public void setBySize(String bySize) {
		this.bySize = bySize;
	}

	public String getByColor() {
		return byColor;
	}

	public void setByColor(String byColor) {
		this.byColor = byColor;
	}
	
	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade({CascadeType.SAVE_UPDATE,CascadeType.PERSIST})
	@JoinColumn(name="parent_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonPostsDetail getParentPortsDetail() {
		return parentPortsDetail;
	}

	public void setParentPortsDetail(AmazonPostsDetail parentPortsDetail) {
		this.parentPortsDetail = parentPortsDetail;
	}

	@OneToMany(mappedBy = "parentPortsDetail", fetch=FetchType.LAZY)
	@Cascade({CascadeType.SAVE_UPDATE,CascadeType.PERSIST})
	@OrderBy(value="queryTime")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	public List<AmazonPostsDetail> getChildren() {
		return children;
	}

	public void setChildren(List<AmazonPostsDetail> children) {
		this.children = children;
	}

	

	public Integer getStar1() {
		return star1;
	}

	public void setStar1(Integer star1) {
		this.star1 = star1;
	}

	public Integer getStar2() {
		return star2;
	}

	public void setStar2(Integer star2) {
		this.star2 = star2;
	}

	public Integer getStar3() {
		return star3;
	}

	public void setStar3(Integer star3) {
		this.star3 = star3;
	}

	public Integer getStar4() {
		return star4;
	}

	public void setStar4(Integer star4) {
		this.star4 = star4;
	}

	public Integer getStar5() {
		return star5;
	}

	public void setStar5(Integer star5) {
		this.star5 = star5;
	}

	public Float getStar() {
		return star;
	}

	public void setStar(Float star) {
		this.star = star;
	}

	public AmazonPostsDetail() {}
	
	
	@Transient
	public void countStar(){
		int count = (star1+star2+star3+star4+star5);
		if(count>0){
			float star =(star1*1+star2*2+star3*3+star4*4+star5*5)/((float)count);
			BigDecimal bd = new BigDecimal(star);
			bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
			this.star = bd.floatValue();
		}
	}
	
	@Transient
	public String reviewLink(){
		String suff = country;
		if(suff!=null){
			if("uk,jp".contains(country)){
				 suff = "co."+suff;
			}else if("mx".equals(country)){
				suff = "com."+suff;
			}
			return "http://www.amazon."+suff+"/product-reviews/"+asin;
		}
		return "";
	}
	
	@Transient
	public String getStar1Link(){
		return reviewLink()+"?formatType=current_format&filterByStar=one_star&pageNumber=1";
	}
	
	@Transient
	public String getStar2Link(){
		return reviewLink()+"?formatType=current_format&filterByStar=two_star&pageNumber=1";
	}
	
	@Transient
	public String getStar3Link(){
		return reviewLink()+"?formatType=current_format&filterByStar=three_star&pageNumber=1";
	}
	
	@Transient
	public String getStar4Link(){
		return reviewLink()+"?formatType=current_format&filterByStar=four_star&pageNumber=1";
	}
	
	@Transient
	public String getStar5Link(){
		return reviewLink()+"?formatType=current_format&filterByStar=five_star&pageNumber=1";
	}
	
	@Transient
	public String getCompareStar() {
		return compareStar;
	}

	public void setCompareStar(String compareStar) {
		this.compareStar = compareStar;
	}
	
	
	@Transient
	public String getCatalog1() {
		return catalog1;
	}

	public void setCatalog1(String catalog1) {
		this.catalog1 = catalog1;
	}
	
	@Transient
	public String getCatalog2() {
		return catalog2;
	}

	public void setCatalog2(String catalog2) {
		this.catalog2 = catalog2;
	}
	
	@Transient
	public List<FbaInboundItem> getTempItems() {
		return tempItems;
	}

	public void setTempItems(List<FbaInboundItem> tempItems) {
		this.tempItems = tempItems;
	}
	@Transient
	public String getParentage() {
		return parentage;
	}

	public void setParentage(String parentage) {
		this.parentage = parentage;
	}
	@Transient
	public String getParentSku() {
		return parentSku;
	}

	public void setParentSku(String parentSku) {
		this.parentSku = parentSku;
	}
	@Transient
	public String getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}
	@Transient
	public String getVariationTheme() {
		return variationTheme;
	}

	public void setVariationTheme(String variationTheme) {
		this.variationTheme = variationTheme;
	}
	@Transient
	public String getProductIdType() {
		return productIdType;
	}

	public void setProductIdType(String productIdType) {
		this.productIdType = productIdType;
	}
	@Transient
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
	@Transient
	public String getConditionType() {
		return conditionType;
	}

	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}
	@Transient
	public String getProductTaxCode() {
		return productTaxCode;
	}

	public void setProductTaxCode(String productTaxCode) {
		this.productTaxCode = productTaxCode;
	}
	@Transient
	public String getFulfillmentCenterId() {
		return fulfillmentCenterId;
	}

	public void setFulfillmentCenterId(String fulfillmentCenterId) {
		this.fulfillmentCenterId = fulfillmentCenterId;
	}
	@Transient
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
	
	
	@Transient
	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}
	@Transient
	public Float getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Float salePrice) {
		this.salePrice = salePrice;
	}
	@Transient
	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public AmazonPostsDetail(AmazonPostsDetail parentPortsDetail, String asin,
			String country, String productName, String binding, String brand,
			String label, String manufacturer, String publisher, String studio,
			String title, Integer packageQuantity, Float packageHeight,
			Float packageLength, Float packageWidth, Float packageWeight,
			String productGroup, String productTypeName, String feature1,
			String feature2, String feature3, String feature4, String feature5,
			String size, String color, String partNumber,
			String ean, String sku,
			String description, String keyword1, String keyword2,
			String keyword3, String keyword4, String keyword5, String catalog1,
			String catalog2, String parentage, String parentSku,
			String relationshipType, String variationTheme,
			String productIdType, String currency, String conditionType,
			String productTaxCode, String fulfillmentCenterId, String unit) {
		super();
		this.parentPortsDetail = parentPortsDetail;
		this.asin = asin;
		this.country = country;
		this.productName = productName;
		this.binding = binding;
		this.brand = brand;
		this.label = label;
		this.manufacturer = manufacturer;
		this.publisher = publisher;
		this.studio = studio;
		this.title = title;
		this.packageQuantity = packageQuantity;
		this.packageHeight = packageHeight;
		this.packageLength = packageLength;
		this.packageWidth = packageWidth;
		this.packageWeight = packageWeight;
		this.productGroup = productGroup;
		this.productTypeName = productTypeName;
		this.feature1 = feature1;
		this.feature2 = feature2;
		this.feature3 = feature3;
		this.feature4 = feature4;
		this.feature5 = feature5;
		this.size = size;
		this.color = color;
		this.partNumber = partNumber;
		this.ean = ean;
		this.sku = sku;
		this.description = description;
		this.keyword1 = keyword1;
		this.keyword2 = keyword2;
		this.keyword3 = keyword3;
		this.keyword4 = keyword4;
		this.keyword5 = keyword5;
		this.catalog1 = catalog1;
		this.catalog2 = catalog2;
		this.parentage = parentage;
		this.parentSku = parentSku;
		this.relationshipType = relationshipType;
		this.variationTheme = variationTheme;
		this.productIdType = productIdType;
		this.currency = currency;
		this.conditionType = conditionType;
		this.productTaxCode = productTaxCode;
		this.fulfillmentCenterId = fulfillmentCenterId;
		this.unit = unit;
	}
}
