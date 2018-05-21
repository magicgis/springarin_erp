package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;


@Entity
@Table(name = "amazoninfo_catalog_rank")
public class AmazonCatalogRank implements Serializable,Comparable<AmazonCatalogRank>{
	
	private static final long serialVersionUID = 1L;
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
	private  AmazonPostsDetail portsDetail;
	private String rankStr;
	private Integer maxRank;
	private Integer minRank;
	private String startDate;
	private String endDate;
	private List<String> rankXAxis  = Lists.newArrayList();
	private String accountName;
	
	
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	
	@Transient
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	@Transient
	public List<String> getRankXAxis() {
		return rankXAxis;
	}

	public void setRankXAxis(List<String> rankXAxis) {
		this.rankXAxis = rankXAxis;
	}

	@Transient
	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	@Transient
	public Integer getMaxRank() {
		return maxRank;
	}

	public void setMaxRank(Integer maxRank) {
		this.maxRank = maxRank;
	}
	@Transient
	public Integer getMinRank() {
		return minRank;
	}

	public void setMinRank(Integer minRank) {
		this.minRank = minRank;
	}

	@Transient
	public String getRankStr() {
		return rankStr;
	}

	public void setRankStr(String rankStr) {
		this.rankStr = rankStr;
	}

	@ManyToOne()
	@JoinColumn(name="ports_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonPostsDetail getPortsDetail() {
		return portsDetail;
	}

	public void setPortsDetail(AmazonPostsDetail portsDetail) {
		this.portsDetail = portsDetail;
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


	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss")
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
	
	public AmazonCatalogRank() {
		super();
	}
	
	public AmazonCatalogRank(String catalog,Integer rank,
			AmazonPostsDetail portsDetail) {
		super();
		this.portsDetail = portsDetail;
		this.asin = portsDetail.getAsin();
		this.country = portsDetail.getCountry();
		this.productName = portsDetail.getProductName();
		this.queryTime = portsDetail.getQueryTime();
		this.catalog = catalog;
		this.rank = rank;
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
			return "http://www.amazon."+suff+"/gp/bestsellers/pc/"+catalog;
		}else if("it,ca".contains(country)){
			return "http://www.amazon."+suff+"/gp/bestsellers/electronics/"+catalog;
		}else{
			return "http://www.amazon."+suff+"/gp/bestsellers/computers/"+catalog;
		}
	}

	@Override
	public int compareTo(AmazonCatalogRank o) {
	   return this.rank.compareTo(o.rank);
	}
}


