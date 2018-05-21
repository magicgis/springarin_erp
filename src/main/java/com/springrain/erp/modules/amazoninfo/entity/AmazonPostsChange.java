package com.springrain.erp.modules.amazoninfo.entity;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;


@Entity
@Table(name = "amazoninfo_posts_change")
public class AmazonPostsChange{

	private String title;
	private String  brand;
	private String description;
	private String feature1;
    private String feature2;
    private String feature3;
    private String feature4;
    private String feature5;
    private Float  packageHeight;
	private Float  packageLength;
	private Float  packageWidth;
	private Float  packageWeight;
	
	//产品尺寸
	private Float  productHeight;
	private Float  productLength;
	private Float  productWidth;
	private Float  productWeight;
	
	
	private String manufacturer;
	private String partNumber;
	
	private String keyword1;
	private String keyword2;
	private String keyword3;
	private String keyword4;
	private String keyword5;
	private String catalog1;
	private String catalog2;
	private Float  price;
	private Float  salePrice;
	
	private Integer id;
	private String asin;
	private String country;
	private String productName;
	

	private String label;
	private String publisher;
	private String studio;
	private String  binding;
	private Integer packageQuantity;
	private String  productGroup;
	private String  productTypeName;
	private String size;
	private String color;
	private String variationTheme;
	private AmazonPostsFeed amazonPostsFeed;
    private String ean;
    private String sku;
    private String crossSku;
    private String crossCountry;
    private String isFba;//1:转 0：不转
    private Integer quantity;
    private String reason;
    
    
    private  String comAsin;
    private  String caAsin;
    private  String ukAsin;
    
    private  String comSku;
    private  String caSku;
    private  String ukSku;
    
    private  String flag;
    
    private  String merchantShippingGroupName;
    
    public static Map<String,List<String>> catalogTypeMap;
    
    static{
    	catalogTypeMap = Maps.newHashMap();
    	catalogTypeMap.put("CE", Lists.newArrayList("ConsumerElectronics","KindleAccessories","KindleEReaderAccessories","KindleFireAccessories"));//颜色
    	catalogTypeMap.put("Home", Lists.newArrayList("Kitchen","OutdoorLiving"));
    	catalogTypeMap.put("HomeImprovement", Lists.newArrayList("Tools","Electrical"));
    	catalogTypeMap.put("Lighting", Lists.newArrayList("LightsAndFixtures")); //颜色
    	catalogTypeMap.put("Office", Lists.newArrayList("OfficeElectronics"));//颜色
    	catalogTypeMap.put("Wireless", Lists.newArrayList("WirelessAccessories"));//颜色
    } 
    
    
    private String catalogType1;
    
    private String catalogType2;
    
    
    @Transient
    public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}


    public String getCatalogType1() {
		return catalogType1;
	}

	public void setCatalogType1(String catalogType1) {
		this.catalogType1 = catalogType1;
	}

	
	public String getCatalogType2() {
		return catalogType2;
	}

	public void setCatalogType2(String catalogType2) {
		this.catalogType2 = catalogType2;
	}

	@Transient
	public String getComAsin() {
		return comAsin;
	}

	public void setComAsin(String comAsin) {
		this.comAsin = comAsin;
	}
	@Transient
	public String getCaAsin() {
		return caAsin;
	}

	public void setCaAsin(String caAsin) {
		this.caAsin = caAsin;
	}
	@Transient
	public String getUkAsin() {
		return ukAsin;
	}

	public void setUkAsin(String ukAsin) {
		this.ukAsin = ukAsin;
	}
	@Transient
	public String getComSku() {
		return comSku;
	}

	public void setComSku(String comSku) {
		this.comSku = comSku;
	}
	@Transient
	public String getCaSku() {
		return caSku;
	}

	public void setCaSku(String caSku) {
		this.caSku = caSku;
	}
	@Transient
	public String getUkSku() {
		return ukSku;
	}

	public void setUkSku(String ukSku) {
		this.ukSku = ukSku;
	}

	public Float getProductHeight() {
		return productHeight;
	}

	public void setProductHeight(Float productHeight) {
		this.productHeight = productHeight;
	}

	public Float getProductLength() {
		return productLength;
	}

	public void setProductLength(Float productLength) {
		this.productLength = productLength;
	}

	public Float getProductWidth() {
		return productWidth;
	}

	public void setProductWidth(Float productWidth) {
		this.productWidth = productWidth;
	}

	public Float getProductWeight() {
		return productWeight;
	}

	public void setProductWeight(Float productWeight) {
		this.productWeight = productWeight;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getCrossCountry() {
		return crossCountry;
	}

	public void setCrossCountry(String crossCountry) {
		this.crossCountry = crossCountry;
	}


	public String getVariationTheme() {
		return variationTheme;
	}

	public void setVariationTheme(String variationTheme) {
		this.variationTheme = variationTheme;
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


	@ManyToOne()
	@JoinColumn(name="feed_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonPostsFeed getAmazonPostsFeed() {
		return amazonPostsFeed;
	}

	public void setAmazonPostsFeed(AmazonPostsFeed amazonPostsFeed) {
		this.amazonPostsFeed = amazonPostsFeed;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
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

	public String getCatalog1() {
		return catalog1;
	}

	public void setCatalog1(String catalog1) {
		this.catalog1 = catalog1;
	}

	public String getCatalog2() {
		return catalog2;
	}

	public void setCatalog2(String catalog2) {
		this.catalog2 = catalog2;
	}
	
	
	
	
	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public Float getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Float salePrice) {
		this.salePrice = salePrice;
	}

	public String getCrossSku() {
		return crossSku;
	}

	public void setCrossSku(String crossSku) {
		this.crossSku = crossSku;
	}

	public String getIsFba() {
		return isFba;
	}

	public void setIsFba(String isFba) {
		this.isFba = isFba;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	@Transient
	public String getMerchantShippingGroupName() {
		return merchantShippingGroupName;
	}

	public void setMerchantShippingGroupName(String merchantShippingGroupName) {
		this.merchantShippingGroupName = merchantShippingGroupName;
	}

	public void genProductEl(Element product,String sku){
		product.addElement("SKU").addText(sku);
		String operateType = amazonPostsFeed.getOperateType();
		if("1,6,7,8".contains(operateType) || "-5".equals(operateType)){
			Element pid = product.addElement("StandardProductID");
			if(StringUtils.isNotEmpty(ean)){
				pid.addElement("Type").addText("EAN");
				pid.addElement("Value").addText(ean);
			}else{
				pid.addElement("Type").addText("ASIN");
				pid.addElement("Value").addText(asin);
			}
		}else if("3,5".contains(operateType)){
			return;
		}
		//cross不需要设置属性
		if(!"7".equals(operateType)){
			Element desc =product.addElement("DescriptionData");
			desc.addElement("Title").addCDATA(HtmlUtils.htmlUnescape(title));
			
			if("8".equals(operateType)&&"com".equals(country)&&StringUtils.isNotBlank(merchantShippingGroupName)){
				desc.addElement("MerchantShippingGroupName").addCDATA(merchantShippingGroupName);
			}
			
			if(brand!=null){
				if("de,fr,it,es,uk".contains(country)&&"Tomons".equals(brand)){
					brand="tomons";
				}
				desc.addElement("Brand").addCDATA(brand);
			}
			if(StringUtils.isNotBlank(description)){
				desc.addElement("Description").addCDATA(HtmlUtils.htmlUnescape(description));
			}
			if(StringUtils.isNotBlank(feature1)&&StringUtils.isNotBlank(feature2)&&StringUtils.isNotBlank(feature3)&&StringUtils.isNotBlank(feature4)&&StringUtils.isNotBlank(feature5)){
				desc.addElement("BulletPoint").addCDATA(feature1);
				desc.addElement("BulletPoint").addCDATA(feature2);
				desc.addElement("BulletPoint").addCDATA(feature3);
				desc.addElement("BulletPoint").addCDATA(feature4);
				desc.addElement("BulletPoint").addCDATA(feature5);
			}
			
			if((productHeight!=null&&productHeight>0)||(productLength!=null&&productLength>0)||(productWidth!=null&&productWidth>0)){
				Element dimens = desc.addElement("ItemDimensions");
				if((productLength!=null&&productLength>0)){
					dimens.addElement("Length").addAttribute("unitOfMeasure", "IN").addText(productLength+"");
				}
				if((productWidth!=null&&productWidth>0)){
					dimens.addElement("Width").addAttribute("unitOfMeasure", "IN").addText(productWidth+"");
				}
				if((productHeight!=null&&productHeight>0)){
					dimens.addElement("Height").addAttribute("unitOfMeasure", "IN").addText(productHeight+"");
				}
			}
			
			if((packageHeight!=null&&packageHeight>0)||(packageLength!=null&&packageLength>0)||(packageWidth!=null&&packageWidth>0)){
				Element dimens = desc.addElement("PackageDimensions");
				if((packageLength!=null&&packageLength>0)){
					dimens.addElement("Length").addAttribute("unitOfMeasure", "IN").addText(packageLength+"");
				}
				if((packageWidth!=null&&packageWidth>0)){
					dimens.addElement("Width").addAttribute("unitOfMeasure", "IN").addText(packageWidth+"");
				}
				if((packageHeight!=null&&packageHeight>0)){
					dimens.addElement("Height").addAttribute("unitOfMeasure", "IN").addText(packageHeight+"");
				}
			}
			
			if((packageWeight!=null&&packageWeight>0)){
				desc.addElement("PackageWeight").addAttribute("unitOfMeasure", "LB").addText(packageWeight+"");
			}
			
			if((productWeight!=null&&productWeight>0)){
				desc.addElement("ShippingWeight").addAttribute("unitOfMeasure", "LB").addText(productWeight+"");
			}
			
			if(StringUtils.isNotBlank(manufacturer)){
				desc.addElement("Manufacturer").addCDATA(manufacturer);
			}
			if(StringUtils.isNotBlank(partNumber)){
				desc.addElement("MfrPartNumber").addCDATA(partNumber);
			}
			if(StringUtils.isNotBlank(keyword1)&&StringUtils.isNotBlank(keyword2)&&StringUtils.isNotBlank(keyword3)&&StringUtils.isNotBlank(keyword4)&&StringUtils.isNotBlank(keyword5)){
				desc.addElement("SearchTerms").addCDATA(HtmlUtils.htmlUnescape(keyword1));
				desc.addElement("SearchTerms").addCDATA(HtmlUtils.htmlUnescape(keyword2));
				desc.addElement("SearchTerms").addCDATA(HtmlUtils.htmlUnescape(keyword3));
				desc.addElement("SearchTerms").addCDATA(HtmlUtils.htmlUnescape(keyword4));
				desc.addElement("SearchTerms").addCDATA(HtmlUtils.htmlUnescape(keyword5));
			}
			if(!"com".equals(country)){
				if(StringUtils.isNotBlank(catalog1)){
					desc.addElement("RecommendedBrowseNode").addCDATA(catalog1);
				}
				if(StringUtils.isNotBlank(catalog2)){
					desc.addElement("RecommendedBrowseNode").addCDATA(catalog2);
				}
			}else{
				if(StringUtils.isNotBlank(catalog1)){
					desc.addElement("ItemType").addCDATA(catalog1);
				}
			}
		}
		if("1,5,-5,6,7".contains(operateType)){
			//建普通帖
			if("com".equals(country)){
				 if(!"Home".equals(catalogType1)){
					  Element computer =  product.addElement("ProductData").addElement(catalogType1).addElement("ProductType")
					 .addElement(catalogType2);
						  computer.addElement("VariationData").addElement("Parentage").addText("child");
				 }else if("Home".equals(catalogType1)){
					  product.addElement("ProductData").addElement(catalogType1)
								.addElement("VariationData").addElement("Parentage").addText("child");
				 }
			}else{
				//建普通帖
				  Element computer =  product.addElement("ProductData").addElement("Computers").addElement("ProductType")
				 .addElement("Computer");
				  computer.addElement("VariationData").addElement("Parentage").addText("child");
			}
			  
		}else if("2".equals(operateType)){
			if("com".equals(country)){
				//建母帖
				Element variationData = null;
				 if(!"Home".equals(catalogType1)){
					if("OfficeElectronics".equals(catalogType2)&&"Size-Color".equals(variationTheme)){
						variationTheme = "SizeColor";
					} 
					variationData =  product.addElement("ProductData").addElement(catalogType1).addElement("ProductType")
					 .addElement(catalogType2).addElement("VariationData");
				 }else if("Home".equals(catalogType1)){
					 variationData =  product.addElement("ProductData").addElement(catalogType1)
							.addElement("VariationData");
				 }
				 if(variationData!=null){
					 variationData.addElement("Parentage").addText("parent");
					 variationData.addElement("VariationTheme").addText(variationTheme);
				 }
				
			}else{
				//建母帖
				Element variationData =  product.addElement("ProductData").addElement("Computers").addElement("ProductType")
				 .addElement("Computer").addElement("VariationData");
				variationData.addElement("Parentage").addText("parent");
				variationData.addElement("VariationTheme").addText(variationTheme);
			}
		}
	}
	
	@Transient
	public String getLink(){
		String suf = country;
		if(StringUtils.isNotBlank(suf)){
			if("jp,uk".contains(suf)){
				suf = "co."+suf;
			}else if("mx".contains(suf)){
				suf = "com."+suf;
			}
		}
		return "http://www.amazon."+suf+"/dp/"+asin;
	}
}
