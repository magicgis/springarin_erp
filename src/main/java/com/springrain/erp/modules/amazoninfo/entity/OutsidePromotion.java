package com.springrain.erp.modules.amazoninfo.entity;

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
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.modules.sys.entity.User;


/**
 * 站外促销分析
 */

@Entity
@Table(name = "amazoninfo_outside_promotion")
public class OutsidePromotion {
	private 	Integer 	id;
	private 	String 		country;
	private 	String 		promotionCode;               //折扣码
	private 	Float 		platformFunds;               //平台经费
	private 	String 		sampleProvided;              //样品提供
	private 	String 		trackId;                     //跟踪id
	private 	String 		productName;                 //产品型号(带颜色)
	private 	String 		asin;                        //asin
	private 	String 		delFlag;                     //删除状态
	private 	Date        startDate;                   //开始时间
	private 	Date        endDate;                     //结束时间
	private 	Date        endRealDate;                 //促销end时间
	private     String      buyerGets;                   //促销类型
	private 	User 		createUser;                  //创建人
	private 	Date        createDate;                  //创建时间
	
	
	private 	AmazonPromotionsWarning 	   promoWarning;//折扣预警id
	
	private     List<OutsidePromotionWebsite>  promoWebsites; //站外促销站点
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "createUser")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "promo_warning_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonPromotionsWarning getPromoWarning() {
		return promoWarning;
	}

	public void setPromoWarning(AmazonPromotionsWarning promoWarning) {
		this.promoWarning = promoWarning;
	}

	
	@Transient
	public List<OutsidePromotionWebsite> getPromoWebsites() {
		return promoWebsites;
	}

	public void setPromoWebsites(List<OutsidePromotionWebsite> promoWebsites) {
		this.promoWebsites = promoWebsites;
	}


	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	public Float getPlatformFunds() {
		return platformFunds;
	}

	public void setPlatformFunds(Float platformFunds) {
		this.platformFunds = platformFunds;
	}

	public String getSampleProvided() {
		return sampleProvided;
	}

	public void setSampleProvided(String sampleProvided) {
		this.sampleProvided = sampleProvided;
	}

	public String getTrackId() {
		return trackId;
	}

	public void setTrackId(String trackId) {
		this.trackId = trackId;
	}

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

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}


	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public String getBuyerGets() {
		return buyerGets;
	}

	public void setBuyerGets(String buyerGets) {
		this.buyerGets = buyerGets;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public Date getEndRealDate() {
		return endRealDate;
	}

	public void setEndRealDate(Date endRealDate) {
		this.endRealDate = endRealDate;
	}

	@Transient
	public String getTrackIdPromoCode(){
		return promotionCode+ "["+trackId+"]";
	}

}
