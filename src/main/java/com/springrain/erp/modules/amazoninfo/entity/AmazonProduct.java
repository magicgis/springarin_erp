package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.excel.annotation.ExcelField;

/**
 * 亚马逊产品Entity
 * 
 * @author tim
 * @version 2014-06-04
 */
@Entity
@Table(name = "amazoninfo_product")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AmazonProduct implements Serializable,Comparable<AmazonProduct>{

	private static final long serialVersionUID = 1L;
	private Integer id; // 编号
	private String name; // 名称
	private String delFlag = "0"; // 删除标记（0：正常；1：删除）
	private String active = "1"; // 删除标记（1：正常；0：不激活）
	private Date createDate;
	
	private String ean;
	private String asin;
	private String sku;
	private String country;
	
	private AmazonProduct parentProduct;
	
	private List<AmazonProduct> children;
	
	private boolean is_parent;

	
	@Transient
	public boolean isIs_parent() {
		return is_parent;
	}
	
	@Transient
	public void setIs_parent(boolean is_parent) {
		this.is_parent = is_parent;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@ExcelField(title="productName", align=2, sort=20)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	@Column(updatable=false)
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
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
	@Column(updatable=false)
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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@Cascade({CascadeType.SAVE_UPDATE,CascadeType.PERSIST})
	@JoinColumn(name="parent")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonProduct getParentProduct() {
		return parentProduct;
	}

	public void setParentProduct(AmazonProduct parentProduct) {
		this.parentProduct = parentProduct;
	}
	
	@OneToMany(mappedBy = "parentProduct", fetch=FetchType.LAZY)
	@Cascade({CascadeType.SAVE_UPDATE,CascadeType.PERSIST})
	@OrderBy(value="createDate") @Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	public List<AmazonProduct> getChildren() {
		return children;
	}

	public void setChildren(List<AmazonProduct> children) {
		this.children = children;
	}

	@PrePersist
	public void prePersist(){
		if(this.ean!=null&&this.ean.length()>0){
			BigDecimal bd = new BigDecimal(this.ean);;
			this.ean = bd.toPlainString();
		}
		this.country = this.country.toLowerCase();
		this.createDate = new Date();
		if(StringUtils.isNotEmpty(this.name)){
			this.name = this.name.trim(); 
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((asin == null) ? 0 : asin.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((ean == null) ? 0 : ean.hashCode());
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
		AmazonProduct other = (AmazonProduct) obj;
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
		if (ean == null) {
			if (other.ean != null)
				return false;
		} else if (!ean.equals(other.ean))
			return false;
		return true;
	}

	@Override
	public int compareTo(AmazonProduct o) {
		if((parentProduct!=null && o.getParentProduct()!=null)||(parentProduct ==null && o.getParentProduct() ==null)){
			return asin.compareTo(o.getAsin());
		}
		return parentProduct!=null?1:-1;
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
}
