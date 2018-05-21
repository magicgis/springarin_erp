package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.excel.annotation.ExcelField;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.utils.DictUtils;

/**
 * 亚马逊商业报表Entity
 * @author tim
 * @version 2014-05-28
 */
@Entity
@Table(name = "amazoninfo_business_report")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class BusinessReport implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private String country;
	
	private Date dataDate;
	
	private Date createDate;
	
	private String pAsin ;//(Parent) ASIN
	
	private String childAsin; //(Child) ASIN
	
	private String title;//Title	
	
	private Integer sessions; //Sessions
	
	private Float sessionPercentage;//Session Percentage	
	
	private Integer pageViews; //Page Views
	
	private Float pageViewsPercentage;//Page Views Percentage
	
	private Integer buyBoxPercentage;//Buy Box Percentage	
	
	
	private Integer unitsOrdered ;//Units Ordered	
	
	private Integer b2bUnitsOrdered ;//b2b Units Ordered	
	
	
	private Float unitSessionPercentage ;//Unit Session Percentage	
	
	private Float b2bUnitSessionPercentage ;//b2b Unit Session Percentage	
	
	
	
	private Float grossProductSales ;//Gross Product Sales	
	
	private Float b2bOrderedProductSales ;//b2bOrderedProductSales
	
	
	private Float conversion;//conversion
	
	
	private Integer ordersPlaced;//Orders Placed
	
	private Integer b2bOrdersPlaced;//Orders Placed
	
	
	private String delFlag = "0"; // 删除标记（0：正常；1：删除）

	private String searchFlag ="0";          //0：按日期       1： 按星期       2： 按月份统计
	
	private String dateSpan ="";
	
	private Float aveSalesPerItem;
	
	private String accountName;
	
	private static Map<String, String> countryKey = Maps.newHashMap();
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	
	@ExcelField(title="国家", align=2, sort=20,dictType="platform")
	public String getCountry() {
		return country;
	}
	
	@Transient
	public String getCountryStr() {
		countryKey.clear();
		List<Dict> dicts = DictUtils.getDictList("platform");
		for (Dict dict : dicts) {
			countryKey.put(dict.getValue(), dict.getLabel());
		}
		return countryKey.get(country);
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd")
	@ExcelField(title="dataDate", align=2, sort=25)
	public Date getDataDate() {
		return dataDate;
	}

	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}

	public String getpAsin() {
		return pAsin;
	}

	public void setpAsin(String pAsin) {
		this.pAsin = pAsin;
	}
	
	@ExcelField(title="Asin", align=2, sort=30)
	public String getChildAsin() {
		return childAsin;
	}

	public void setChildAsin(String childAsin) {
		this.childAsin = childAsin;
	}
	
	@ExcelField(title="产品名称", align=2, sort=0)
	public String getTitle() {
		return HtmlUtils.htmlEscape(title);
	}
	
	@Transient
	public String getTitleStr() {
		if(title!=null && title.length()>40){
			return title.substring(0,40);
		}
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@ExcelField(title="Sessions", align=2, sort=40)
	public Integer getSessions() {
		return sessions;
	}

	public void setSessions(Integer sessions) {
		this.sessions = sessions;
	}
	
	@ExcelField(title="Page Views", align=2, sort=60)
	public Integer getPageViews() {
		return pageViews;
	}

	@Transient
	@ExcelField(title="Average Sales per Order Item", align=2, sort=110)
	public Float getAveSalesPerItem() {
		return aveSalesPerItem;
	}

	public void setAveSalesPerItem(Float aveSalesPerItem) {
		this.aveSalesPerItem = aveSalesPerItem;
	}

	public void setPageViews(Integer pageViews) {
		this.pageViews = pageViews;
	}
	
	@ExcelField(title="UnitsOrdered", align=2, sort=80)
	public Integer getUnitsOrdered() {
		return unitsOrdered;
	}

	public void setUnitsOrdered(Integer unitsOrdered) {
		this.unitsOrdered = unitsOrdered;
	}
	
	@ExcelField(title="OrdersPlaced(%)", align=2, sort=100,fieldType=PercentType.class)
	public Integer getOrdersPlaced() {
		return ordersPlaced;
	}

	public void setOrdersPlaced(Integer ordersPlaced) {
		this.ordersPlaced = ordersPlaced;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	@ExcelField(title="Session(%)", align=2, sort=50,fieldType=PercentType.class)
	public Float getSessionPercentage() {
		return sessionPercentage;
	}

	public void setSessionPercentage(Float sessionPercentage) {
		this.sessionPercentage = sessionPercentage;
	}
	
	
	public Float getPageViewsPercentage() {
		return pageViewsPercentage;
	}

	public void setPageViewsPercentage(Float pageViewsPercentage) {
		this.pageViewsPercentage = pageViewsPercentage;
	}
	
	@ExcelField(title="BuyBox(%)", align=2, sort=70)
	public Integer getBuyBoxPercentage() {
		return buyBoxPercentage;
	}

	public void setBuyBoxPercentage(Integer buyBoxPercentage) {
		this.buyBoxPercentage = buyBoxPercentage;
	}
	
	@ExcelField(title="UnitSession(%)", align=2, sort=90,fieldType=PercentType.class)
	public Float getUnitSessionPercentage() {
		return unitSessionPercentage;
	}

	public void setUnitSessionPercentage(Float unitSessionPercentage) {
		this.unitSessionPercentage = unitSessionPercentage;
	}

	public Float getGrossProductSales() {
		return grossProductSales;
	}

	public void setGrossProductSales(Float grossProductSales) {
		this.grossProductSales = grossProductSales;
	}
	
	
	public Integer getB2bUnitsOrdered() {
		return b2bUnitsOrdered;
	}

	public void setB2bUnitsOrdered(Integer b2bUnitsOrdered) {
		this.b2bUnitsOrdered = b2bUnitsOrdered;
	}

	public Float getB2bUnitSessionPercentage() {
		return b2bUnitSessionPercentage;
	}

	public void setB2bUnitSessionPercentage(Float b2bUnitSessionPercentage) {
		this.b2bUnitSessionPercentage = b2bUnitSessionPercentage;
	}

	public Float getB2bOrderedProductSales() {
		return b2bOrderedProductSales;
	}

	public void setB2bOrderedProductSales(Float b2bOrderedProductSales) {
		this.b2bOrderedProductSales = b2bOrderedProductSales;
	}

	public Integer getB2bOrdersPlaced() {
		return b2bOrdersPlaced;
	}

	public void setB2bOrdersPlaced(Integer b2bOrdersPlaced) {
		this.b2bOrdersPlaced = b2bOrdersPlaced;
	}

	@ExcelField(title="转化率(%)", align=2, sort=110,fieldType=PercentType.class)
	public Float getConversion() {
		return conversion;
	}

	public void setConversion(Float conversion) {
		this.conversion = conversion;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Transient
	public String getSearchFlag() {
		return searchFlag;
	}
	public void setSearchFlag(String searchFlag) {
		this.searchFlag = searchFlag;
	}
	
	
	@Transient
	public String getDateSpan() {
		return dateSpan;
	}

	public void setDateSpan(String dateSpan) {
		this.dateSpan = dateSpan;
	}
	
	@Transient
	public String getConversionStr() {
		if(sessions!=null&&ordersPlaced!=null&&sessions>0){
			float rs = ((float)ordersPlaced)*100/sessions;
			BigDecimal temp = new BigDecimal(rs);
			temp = temp.setScale(2,BigDecimal.ROUND_HALF_UP);
			return temp.floatValue()+"";
		}
		return "0";
	}

	@Transient
	public String getHref(){
		String suff = this.country;
		if("jp".equals(suff)||"uk".equals(suff)){
			suff = "co."+suff;
		}else if("mx".equals(suff)){
			suff="com.mx";
		}
		return "http://www.amazon."+suff+"/dp/"+this.getChildAsin();
	}
	
	public BusinessReport() {}

	public BusinessReport(String country, Date dataDate, Date createDate,
			String pAsin, String childAsin, String title, Integer sessions,
			Float sessionPercentage, Integer pageViews,
			Float pageViewsPercentage, Integer buyBoxPercentage,
			Integer unitsOrdered, Float unitSessionPercentage,
			Float grossProductSales, Integer ordersPlaced,Float conversion,Float aveSalesPerItem,Integer b2bUnitsOrdered,
			Float b2bUnitSessionPercentage, Float b2bOrderedProductSales,
			Integer b2bOrdersPlaced) {
		super();
		this.country = country;
		this.dataDate = dataDate;
		this.createDate = createDate;
		this.pAsin = pAsin;
		this.childAsin = childAsin;
		this.title = title;
		this.sessions = sessions;
		this.sessionPercentage = sessionPercentage;
		this.pageViews = pageViews;
		this.pageViewsPercentage = pageViewsPercentage;
		this.buyBoxPercentage = buyBoxPercentage;
		this.unitsOrdered = unitsOrdered;
		this.unitSessionPercentage = unitSessionPercentage;
		this.grossProductSales = grossProductSales;
		this.ordersPlaced = ordersPlaced;
		this.conversion = conversion;
		this.aveSalesPerItem=aveSalesPerItem;
		
		this.b2bUnitsOrdered = b2bUnitsOrdered;
		this.b2bUnitSessionPercentage = b2bUnitSessionPercentage;
		this.b2bOrderedProductSales = b2bOrderedProductSales;
		this.b2bOrdersPlaced = b2bOrdersPlaced;
	}
}


