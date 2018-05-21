package com.springrain.erp.modules.amazoninfo.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "amazoninfo_new_releases_rank")
public class AmazonNewReleasesRank{
	
	private String id; 	
	private String asin;
	private String country;
	private String productName;
	private Date queryTime;
	private String catalog;
	private String catalogName;
	private Integer rank;
	private String path;
	private String pathName;
	private Date endTime;
	
	@Transient
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Date getQueryTime() {
		return queryTime;
	}

	public void setQueryTime(Date queryTime) {
		this.queryTime = queryTime;
	}

	public String getCatalog() {
		return catalog;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}

	public String getCatalogName() {
		return catalogName;
	}

	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
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
			return "http://www.amazon."+suff+"/gp/new-releases/pc/"+catalog;
		}else if("it,ca".contains(country)){
			return "http://www.amazon."+suff+"/gp/new-releases/electronics/"+catalog;
		}else{
			return "http://www.amazon."+suff+"/gp/new-releases/computers/"+catalog;
		}
	}

	
}


