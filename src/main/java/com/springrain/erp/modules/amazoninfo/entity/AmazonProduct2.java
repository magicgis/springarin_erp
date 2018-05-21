package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.common.utils.excel.annotation.ExcelField;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 亚马逊产品Entity
 * 
 * @author tim
 * @version 2014-06-04
 */
@Entity
@Table(name = "amazoninfo_product2")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AmazonProduct2 implements Serializable{

	private static final long serialVersionUID = 1L;
	private Integer id; // 编号
	private String active = "1"; // 删除标记（1：正常；0：不激活）
	
	private Date updateDate;
	private Date openDate;
	private Integer openCycle;
	
	private String ean;
	private String asin;
	private String sku;
	private Float price; 
	private Float salePrice; 
	private String country;
	
	private Float warnPrice; 
	private Float highWarnPrice;
	
	private User warnPriceByUser;
	private Date lastWarnPriceUpdate;
	
	private String fnsku;
	
	private String isFba = "1";
	
	private Integer quantity; 
	
	private Float businessPrice;	//B2B价格
	//5档阶梯价格和数量
	private Float price1;
	private Float price2;
	private Float price3;
	private Float price4;
	private Float price5;
	private Integer quantity1;
	private Integer quantity2;
	private Integer quantity3;
	private Integer quantity4;
	private Integer quantity5;
	
	private String accountName;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}
	

	public Date getOpenDate() {
		return openDate;
	}

	public void setOpenDate(Date openDate) {
		this.openDate = openDate;
	}
	
	public Integer getOpenCycle() {
		return openCycle;
	}

	public void setOpenCycle(Integer openCycle) {
		this.openCycle = openCycle;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	@ExcelField(title="ean", align=2, sort=30)
	public String getEan() {
		return ean;
	}

	public void setEan(String ean) {
		this.ean = ean;
	}
	
	@ExcelField(title="asin", align=2, sort=40)
	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}
	
	@ExcelField(title="country", align=2, sort=50)
	@Column(updatable=false)
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	public String getFnsku() {
		return fnsku;
	}

	public void setFnsku(String fnsku) {
		this.fnsku = fnsku;
	}
	
	public Float getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Float salePrice) {
		this.salePrice = salePrice;
	}
	
	public String getIsFba() {
		return isFba;
	}

	public void setIsFba(String isFba) {
		this.isFba = isFba;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public AmazonProduct2() {}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((asin == null) ? 0 : asin.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((sku == null) ? 0 : sku.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AmazonProduct2 other = (AmazonProduct2) obj;
		if (asin == null) {
			if (other.asin != null)
				return false;
		} else if (!asin.equals(other.asin))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (sku == null) {
			if (other.sku != null)
				return false;
		} else if (!sku.equals(other.sku))
			return false;
		return true;
	}
	
	
	public Float getWarnPrice() {
		return warnPrice;
	}

	public void setWarnPrice(Float warnPrice) {
		this.warnPrice = warnPrice;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getWarnPriceByUser() {
		return warnPriceByUser;
	}

	public void setWarnPriceByUser(User warnPriceByUser) {
		this.warnPriceByUser = warnPriceByUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getLastWarnPriceUpdate() {
		return lastWarnPriceUpdate;
	}
	
	public void setLastWarnPriceUpdate(Date lastWarnPriceUpdate) {
		this.lastWarnPriceUpdate = lastWarnPriceUpdate;
	}
	
	public Float getHighWarnPrice() {
		return highWarnPrice;
	}

	public void setHighWarnPrice(Float highWarnPrice) {
		this.highWarnPrice = highWarnPrice;
	}

	public Float getBusinessPrice() {
		return businessPrice;
	}

	public void setBusinessPrice(Float businessPrice) {
		this.businessPrice = businessPrice;
	}

	public Float getPrice1() {
		return price1;
	}

	public void setPrice1(Float price1) {
		this.price1 = price1;
	}

	public Float getPrice2() {
		return price2;
	}

	public void setPrice2(Float price2) {
		this.price2 = price2;
	}

	public Float getPrice3() {
		return price3;
	}

	public void setPrice3(Float price3) {
		this.price3 = price3;
	}

	public Float getPrice4() {
		return price4;
	}

	public void setPrice4(Float price4) {
		this.price4 = price4;
	}

	public Float getPrice5() {
		return price5;
	}

	public void setPrice5(Float price5) {
		this.price5 = price5;
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

	public Integer getQuantity3() {
		return quantity3;
	}

	public void setQuantity3(Integer quantity3) {
		this.quantity3 = quantity3;
	}

	public Integer getQuantity4() {
		return quantity4;
	}

	public void setQuantity4(Integer quantity4) {
		this.quantity4 = quantity4;
	}

	public Integer getQuantity5() {
		return quantity5;
	}

	public void setQuantity5(Integer quantity5) {
		this.quantity5 = quantity5;
	}

	public AmazonProduct2(String sku, String asin,Float price,Integer quantity,String isFba,Date openDate) {
		super();
		this.sku = sku;
		this.asin = asin;
		this.price = price;
		this.quantity = quantity;
		this.isFba = isFba;
		this.openDate = openDate;
	}
	
	public AmazonProduct2(String sku, Float salePrice, Float warnPrice,String country,String accountName) {
		super();
		this.sku = sku;
		this.salePrice = salePrice;
		this.warnPrice = warnPrice;
		this.country = country;
		this.accountName = accountName;
	}
	
	public String render(boolean isOdd){
		String even = "";
		if(!isOdd){
			even = "background-color:#e8f3fd;";
		}
		String temp = country;
		if("com".equals(country)){
			temp = "us";
		}
		float tempPrice = salePrice;
		Float lowPrice = getLowPrice();
		if (lowPrice != null && lowPrice>0 && lowPrice<salePrice) {
			tempPrice = lowPrice;
		}
		
		String color = warnPrice-salePrice>0?"red":"green";
		String result = "<tr style='font-size:10px;"+even+"'>"+
				 "<td style=\"border:1px dotted #cad9ea;padding:0 2px 0\">"+temp.toUpperCase()+"</td>"+
				 "<td style=\" border:1px dotted #cad9ea;padding:0 2px 0\">"+sku+"</td>"+
				 "<td style=\" border:1px dotted #cad9ea;padding:0 2px 0\">"+tempPrice+"</td>"+
				 "<td style=\" border:1px dotted #cad9ea;padding:0 2px 0;color:"+color+"\">"+warnPrice+"</td>"+
				 "</tr>";
		return result;
	}
	
	public String render(boolean isOdd,int type){
		String even = "";
		if(!isOdd){
			even = "background-color:#e8f3fd;";
		}
		String temp = country;
		if("com".equals(country)){
			temp = "us";
		}
		String suff = country;
		if("jp,uk".contains(country)){
			suff = "co."+country;
		}
		
		String error = type==1?"帖子被禁或删除":"价格不可读";
		String result = "<tr style='font-size:10px;"+even+"'>"+
				 "<td style=\"border:1px dotted #cad9ea;padding:0 2px 0\">"+temp.toUpperCase()+"</td>"+
				 "<td style=\" border:1px dotted #cad9ea;padding:0 2px 0\"><a href = \"http://www.amazon."+suff+"/dp/"+asin+"\">"+sku+"</a></td>"+
				 "<td style=\" border:1px dotted #cad9ea;padding:0 2px 0;color:red\">"+error+"</td>"+
				 "</tr>";
		return result;
	}
	
	@Transient
	public String getLink(){
		String suff = country;
		if("uk,jp".contains(country)){
			 suff = "co."+suff;
		}else if("mx".equals(country)){
			suff = "com."+suff;
		}
		return "http://www.amazon."+suff+"/dp/"+asin;
	}
	
	/**
	 * 获取最高一级的阶梯价(即B2B最低价)
	 * @return
	 */
	@Transient
	public Float getLowPrice(){
		if (price5 != null) {
			return price5;
		} else if (price4 != null) {
			return price4;
		} else if (price3 != null) {
			return price3;
		} else if (price2 != null) {
			return price2;
		} else if (price1 != null) {
			return price1;
		}else {
			return businessPrice;
		}
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	
}
