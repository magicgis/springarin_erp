package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "psi_barcode")
public class PsiBarcode implements Serializable{
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String productColor;
	private String productPlatform;
	private String barcode;
	private String barcodeType;
	
	private PsiProduct psiProduct;
	private User lastUpdateBy;
	
	private Date lastUpdateTime;
	
	private String delFlag="0";
	private String productName;
	private String accountName;
	
	private List<PsiSku> skus;
	
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@OneToMany(mappedBy = "barcode", fetch=FetchType.LAZY,cascade=CascadeType.ALL)
	@Where(clause="del_flag='0'")
	@NotFound(action = NotFoundAction.IGNORE)
	public List<PsiSku> getSkus() {
		return skus;
	}
	
	@Transient
	public Map<String,List<PsiSku>> getAccountSkus() {
		Map<String,List<PsiSku>> map=Maps.newHashMap();
		for (PsiSku sku  : skus) {
			String name=(sku.getAccountName()==null?"":sku.getAccountName());
			List<PsiSku> rs = map.get(name);
			if(rs==null){
				rs=Lists.newArrayList();
				map.put(name, rs);
			}
			rs.add(sku);
		}
		return map;
	}
	
	@Transient
	public List<PsiSku> getDeSkus() {
		List<PsiSku> rs = Lists.newArrayList();
		for (PsiSku sku  : skus) {
			if("de".equals(sku.getCountry())){
				rs.add(sku);
			}
		}
		return rs;
	}
	
	@Transient
	public List<PsiSku> getEbaySkus() {
		List<PsiSku> rs = Lists.newArrayList();
		for (PsiSku sku  : skus) {
			if("ebay".equals(sku.getCountry())){
				rs.add(sku);
			}
		}
		return rs;
	}
	
	@Transient
	public List<PsiSku> getComSkus() {
		List<PsiSku> rs = Lists.newArrayList();
		for (PsiSku sku  : skus) {
			if("com".equals(sku.getCountry())){
				rs.add(sku);
			}
		}
		return rs;
	}
	
	@Transient
	public List<PsiSku> getComEbaySkus() {
		List<PsiSku> rs = Lists.newArrayList();
		for (PsiSku sku  : skus) {
			if("ebay_com".equals(sku.getCountry())){
				rs.add(sku);
			}
		}
		return rs;
	}
	
	public void setSkus(List<PsiSku> skus) {
		this.skus = skus;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(updatable=false)
	public String getProductColor() {
		return productColor;
	}
	public void setProductColor(String productColor) {
		this.productColor = productColor;
	}
	
	@Transient
	public String getBarcodeProductName() {
		String color = "";
		if(productColor!=null){
			color = " "+productColor.toUpperCase();
		}
		return getProductName()+color;
	}
	
	@Column(updatable=false)
	public String getProductPlatform() {
		return productPlatform;
	}
	public void setProductPlatform(String productPlatform) {
		this.productPlatform = productPlatform;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	
	public String getBarcodeType() {
		return barcodeType;
	}
	public void setBarcodeType(String barcodeType) {
		this.barcodeType = barcodeType;
	}
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiProduct getPsiProduct() {
		return psiProduct;
	}
	
	public void setPsiProduct(PsiProduct psiProduct) {
		this.psiProduct = psiProduct;
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
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	@Column(updatable=false)
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	
	@Transient
	public int getIndex() {
		return getPsiProduct().getBarcodes().indexOf(this);
	}
	
	@Transient
	public Set<String> getAsin() {
		Set<String> asins = Sets.newLinkedHashSet();
		for (PsiSku sku : skus) {
			if(StringUtils.isNotEmpty(sku.getAsin())){
				asins.add(sku.getAsin());
			}
		}
		return asins;
	}

	@Transient
	public String getName() {
		String color = "";
		if(StringUtils.isNotBlank(productColor)){
			color = "_"+productColor;
		}
		return getProductName()+color;
	}
	
	
}
