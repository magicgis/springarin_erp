/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.entity;

import java.math.BigDecimal;
import java.util.Collections;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.StringUtils;

/**
 * 亚马逊产品目录
 * @author tim
 * @version 2016-03-29
 */

@Entity
@Table(name = "amazoninfo_type_catelog")
public class ProductCatelog {
	
	private 		Integer 		id; 				// id
	private         String          country;            // country
	private     	Date        	createDate;     	// 创建日期
	private     	Date        	queryDate;     	// 查询日期
	private         String    type;
	private         String    catalogName;
	private         String    catalogLinkId;
	private         Float     sales;
	private         Integer   salesVolume;
	private         Float     marketShare;
	private         Float 	  avg30MarketShare;
	private         Float 	  avgPrice;
	private         Float     yestdayAvgPrice;
	private         List<ProductCatelogItem> items = Lists.newArrayList();
	
	public ProductCatelog() {
		super();
	}
	
	public ProductCatelog(Integer id){
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

	public Date getCreateDate() {
		return createDate;
	}


	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country= country;
	}

	public Date getQueryDate() {
		return queryDate;
	}

	public void setQueryDate(Date queryDate) {
		this.queryDate = queryDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}

	public Float getSales() {
		return sales;
	}

	public void setSales(Float sales) {
		this.sales = sales;
	}

	public Integer getSalesVolume() {
		return salesVolume;
	}

	public void setSalesVolume(Integer salesVolume) {
		this.salesVolume = salesVolume;
	}

	public Float getMarketShare() {
		return marketShare;
	}

	public void setMarketShare(Float marketShare) {
		this.marketShare = marketShare;
	}

	@Column(name="avg30_market_share")
	public Float getAvg30MarketShare() {
		return avg30MarketShare;
	}

	public void setAvg30MarketShare(Float avg30MarketShare) {
		this.avg30MarketShare = avg30MarketShare;
	}

	public Float getAvgPrice() {
		return avgPrice;
	}

	public void setAvgPrice(Float avgPrice) {
		this.avgPrice = avgPrice;
	}

	public Float getYestdayAvgPrice() {
		return yestdayAvgPrice;
	}

	public void setYestdayAvgPrice(Float yestdayAvgPrice) {
		this.yestdayAvgPrice = yestdayAvgPrice;
	}
	

	public String getCatalogLinkId() {
		return catalogLinkId;
	}

	public void setCatalogLinkId(String catalogLinkId) {
		this.catalogLinkId = catalogLinkId;
	}
	
	@OneToMany(mappedBy = "productCatelog",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@OrderBy(value="rank ")
	@NotFound(action = NotFoundAction.IGNORE)
	public List<ProductCatelogItem> getItems() {
		return items;
	}

	public void setItems(List<ProductCatelogItem> items) {
		this.items = items;
	}
	
	@Transient
	public Map<String, ProductCatelogItem> getAsinMap(){
		Map<String, ProductCatelogItem> rs = Maps.newHashMap();
		for (ProductCatelogItem item : items) {
			rs.put(item.getAsin(), item);
		}
		return rs;
	}
	
	@Transient
	public String getLink(){
		String suff = country;
		if("uk,jp".contains(country)){
			 suff = "co."+suff;
		}else if("mx".equals(country)){
			suff = "com."+suff;
		}
		if("com".equals(country)){
			return "http://www.amazon.com/gp/bestsellers/pc/"+catalogLinkId;
		}else if("it,ca".contains(country)){
			return "http://www.amazon."+suff+"/gp/bestsellers/electronics/"+catalogLinkId;
		}else{
			return "http://www.amazon."+suff+"/gp/bestsellers/computers/"+catalogLinkId;
		}
	}
	
	@Transient
	public void countAvgPrice(){
		float avgPrice = 0f;	
		int i = 0;
		for (ProductCatelogItem item : items) {
			if("1".equals(item.getMe())&&item.getPrice()!=null){
				i++;
				avgPrice+=item.getPrice();
			}
		}
		if(i>0){
			this.avgPrice = avgPrice/i;
			BigDecimal bd = new BigDecimal(this.avgPrice);
			bd.setScale(2, BigDecimal.ROUND_HALF_UP);
			this.avgPrice = bd.floatValue();
		}else{
			this.avgPrice = 0f ;
		}
	}
	
	
	@Transient
	public String getStylePirce(){
		if(avgPrice!=null && yestdayAvgPrice!=null&&!avgPrice.equals(yestdayAvgPrice)){
			return avgPrice>yestdayAvgPrice?"green":"red";
		}
		return "";
	}
	
	@Transient
	public String getStyleMs(){
		if(marketShare!=null && avg30MarketShare!=null&&!marketShare.equals(avg30MarketShare)){
			return marketShare>avg30MarketShare?"green":"red";
		}
		return "";
	}
	
	
	
	
	@Transient
	public String getOutMs(){ 
		String rs = "";
		Map<String,MsEntry> map = Maps.newHashMap();
		for (ProductCatelogItem item : items) {
			String brand = item.getBrand();
			if(brand!=null&&"0".equals(item.getMe())&&!"inateck".equals(brand.toLowerCase())){
				MsEntry ms = map.get(brand);
				if(ms==null){
					map.put(brand,new MsEntry(item.getBrand(),(21-item.getRank())*100/210f));
				}else{
					ms.setMs((ms.getMs()+(21-item.getRank())*100/210f));
				}
			}
		}
		List<MsEntry> list = Lists.newArrayList(map.values());
		Collections.sort(list);
		StringBuffer buf= new StringBuffer();
		for (MsEntry msEntry : list) {
			buf.append(msEntry.toString());
		}
		rs=buf.toString();
		return rs;
	}
	
	@Transient
	public String getTip(){ 
		StringBuffer buf= new StringBuffer("<table class='table table-striped table-bordered table-condensed'><tr>");
		int i = 0;
		for (ProductCatelogItem item : items) {
			if(!StringUtils.isEmpty(item.getBrand())){
				
				String price = "";
				if(item.getPrice()!=null){
					price = ":"+item.getPrice();
				}
				buf.append("<td>"+item.getRank()+".<a target='_blank' href='"+item.getLink()+"'><img src='"+item.getImageUrl()+"' style='width: 20px;height: 20px'/>"+(item.getMe().equals("1")?item.getProductName():item.getBrand())+"</a>"+price+"</td>");
				i++;
				if(i%3==0){
					buf.append("<tr/>");
				}
			}
		}
		buf.append("</table>");
		return buf.toString();
	}
	
	@Transient
	public String getFistTo20(){ 
		String rs = "";
		StringBuffer buf= new StringBuffer();
		for (ProductCatelogItem item : items) {
			if("1".equals(item.getFirstTo20())&& !StringUtils.isEmpty(item.getBrand())){
				String price = "";
				if(item.getPrice()!=null){
					price = ":"+item.getPrice();
				}
			    buf.append(+item.getRank()+".<a target='_blank' href='"+item.getLink()+"'><img src='"+item.getImageUrl()+"' style='width: 20px;height: 20px'/>"+(item.getMe().equals("1")?item.getProductName():item.getBrand())+"</a>"+price+"<br/>");
			}
		}
		if(StringUtils.isNotBlank(buf.toString())){
			rs=buf.toString();
		}
		return rs;
	}
	
	
	static class MsEntry implements Comparable<MsEntry>{
		
		private String name;
		
		private Float ms;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Float getMs() {
			return ms;
		}

		public void setMs(Float ms) {
			this.ms = ms;
		}
		
		public MsEntry(String name, Float ms) {
			super();
			this.name = name;
			this.ms = ms;
		}

		@Override
		public int compareTo(MsEntry o) {
			if(ms!=null && o.getMs()!=null){
				return ms-o.getMs()>0?-1:1;
			}else if(ms==null){
				return -1;
			}else  if(o.getMs()==null){
				return 1;
			}else{
				return 0;
			}
		}
		
		@Override
		public String toString() {
			BigDecimal bd = new BigDecimal(ms);
			bd = bd.setScale(0,BigDecimal.ROUND_HALF_UP);
			return StringUtils.isEmpty(name)?"No Brand":name+":"+bd.intValue()+"%<br/>";
		}
	} 
	
}


