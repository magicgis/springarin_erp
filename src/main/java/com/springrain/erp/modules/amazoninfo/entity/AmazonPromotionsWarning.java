/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.scheduler.AmazonWSConfig;
import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "amazoninfo_promotions_warning")
public class AmazonPromotionsWarning implements Serializable {
	private      static final long serialVersionUID = -2433140471122314283L;
	private 	Integer 	id; 			// id
	private 	String 		promotionId; 	// 促销码
	private     String      remark;         // 备注
	private 	String 		country;        // 平台
	private     Date        startDate;     // 创建日期
	private     User        updateUser;     // 编辑人
	private     Date        updateDate;     // 编辑日期
	private     Date        createDate;     // 编辑日期
	private     String      warningSta; // 折扣预警状态
	private     Date        endDate;        // 结束时间
	
	private     List<AmazonPromotionsWarningItem>        items;

	private     String      buyerPurchases;
	private     String      buyerGets;
	private 	String     	purchasedItems; 
	private     String      promotion;
	private     String      oneRedemption;
	
	private     String      isActive;//0:有效
	
	private     String      claimCode;
	
	private     String      promotionCode;
	
	
	private      User       createUser;
	
	private      User       checkUser;
	
	private      Date       checkDate;
	
	private      String     claimCodeCombinability;
	
	private      String     conditionType;
	
	
	private      String     mainAsin;
	
	
	private      Float      amountOffPrice;
	
	private      Float    purchaseQuantity;
	
	private      String   proType;
	
	private      String   reason;
	
    private      String     lastCheckUser;
	
	private      Date       lastCheckDate;
	
	private      Float      checkRate;
	
	private      String     checkFlag;
	
	private      String     specialCheckUser;
	
	private      String     qualifyingItem;
	
	private      Integer    tempQuantity;
	private      Integer    quantity;
	
	private      String     accountName;
	
	
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	@Transient
	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Transient
	public Integer getTempQuantity() {
		return tempQuantity;
	}

	public void setTempQuantity(Integer tempQuantity) {
		this.tempQuantity = tempQuantity;
	}

	public String getQualifyingItem() {
		return qualifyingItem;
	}

	public void setQualifyingItem(String qualifyingItem) {
		this.qualifyingItem = qualifyingItem;
	}

	public String getSpecialCheckUser() {
		return specialCheckUser;
	}

	public void setSpecialCheckUser(String specialCheckUser) {
		this.specialCheckUser = specialCheckUser;
	}

	public String getCheckFlag() {
		return checkFlag;
	}

	public void setCheckFlag(String checkFlag) {
		this.checkFlag = checkFlag;
	}

	public String getLastCheckUser() {
		return lastCheckUser;
	}

	public void setLastCheckUser(String lastCheckUser) {
		this.lastCheckUser = lastCheckUser;
	}

	public Date getLastCheckDate() {
		return lastCheckDate;
	}

	public void setLastCheckDate(Date lastCheckDate) {
		this.lastCheckDate = lastCheckDate;
	}

	public Float getCheckRate() {
		return checkRate;
	}

	public void setCheckRate(Float checkRate) {
		this.checkRate = checkRate;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	@Transient
	public Float getPurchaseQuantity() {
		return purchaseQuantity;
	}

	public void setPurchaseQuantity(Float purchaseQuantity) {
		this.purchaseQuantity = purchaseQuantity;
	}

	@Transient
	public Float getAmountOffPrice() {
		return amountOffPrice;
	}

	public void setAmountOffPrice(Float amountOffPrice) {
		this.amountOffPrice = amountOffPrice;
	}

	@Transient
	public String getMainAsin() {
		return mainAsin;
	}

	public void setMainAsin(String mainAsin) {
		this.mainAsin = mainAsin;
	}

	@Transient
	public String getConditionType() {
		return conditionType;
	}

	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}

	@Transient
	public String getClaimCodeCombinability() {
		return claimCodeCombinability;
	}

	public void setClaimCodeCombinability(String claimCodeCombinability) {
		this.claimCodeCombinability = claimCodeCombinability;
	}

	@ManyToOne()
	@JoinColumn(name="create_user")
	 public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	@ManyToOne()
	@JoinColumn(name="check_user")
	public User getCheckUser() {
		return checkUser;
	}

	public void setCheckUser(User checkUser) {
		this.checkUser = checkUser;
	}
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}




	DateFormat sdf =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	@Transient
	public String getClaimCode() {
		return claimCode;
	}

	public void setClaimCode(String claimCode) {
		this.claimCode = claimCode;
	}

	public AmazonPromotionsWarning() {
		super();
	}

	public AmazonPromotionsWarning(Integer id){
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
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public String getPromotionId() {
		return promotionId;
	}

	public void setPromotionId(String promotionId) {
		this.promotionId = promotionId;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	
	@OneToMany(mappedBy = "warning",fetch=FetchType.LAZY)
	@Where(clause="del_flag=0")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	@Cascade(CascadeType.ALL)
	public List<AmazonPromotionsWarningItem> getItems() {
		return items;
	}

	public void setItems(List<AmazonPromotionsWarningItem> items) {
		this.items = items;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@ManyToOne()
	@JoinColumn(name="update_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(User updateUser) {
		this.updateUser = updateUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	
	public String getPurchasedItems() {
		return purchasedItems;
	}
	
	public void setPurchasedItems(String purchasedItems) {
		this.purchasedItems = purchasedItems;
	}

	public void setWarningSta(String warningSta) {
		this.warningSta = warningSta;
	}
	

	public String getWarningSta() {
		return warningSta;
	}

	public String getBuyerPurchases() {
		return buyerPurchases;
	}

	public void setBuyerPurchases(String buyerPurchases) {
		this.buyerPurchases = buyerPurchases;
	}

	public String getBuyerGets() {
		return buyerGets;
	}

	public void setBuyerGets(String buyerGets) {
		this.buyerGets = buyerGets;
	}

	public String getPromotion() {
		return promotion;
	}

	public void setPromotion(String promotion) {
		this.promotion = promotion;
	}

	public String getOneRedemption() {
		return oneRedemption;
	}

	public void setOneRedemption(String oneRedemption) {
		this.oneRedemption = oneRedemption;
	}
	
	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	
	
	
	
	

	@Transient
	@JsonIgnore
	public Date xmlGregorianToLocalDate(String country) {
			Date date = new Date();
			if("de,it,es,fr".contains(country)){
				sdf.setTimeZone(AmazonWSConfig.get("de").getTimeZone());
			}else{
				sdf.setTimeZone(AmazonWSConfig.get(country).getTimeZone());
			}
			String time = sdf.format(date);
			sdf.setTimeZone(TimeZone.getDefault());
			try {
				return sdf.parse(time);
			} catch (ParseException e) {}
		    return null;
	}
	
	
	@Transient
	public String getPromotionLink(){
		String suff = country;
		if(suff!=null){
			if("uk,fr,it,es".contains(country)){
				suff="de";
			}
			if("jp".equals(country)){
				 suff = "co."+suff;
			}else if("mx".equals(country)){
				suff = "com."+suff;
			}
			try {
				return "https://sellercentral.amazon."+suff+"/promotions/view?trackingId="+URLEncoder.encode(promotionId,"utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
	
	
	public String toJson(){
		String productStr="";
		String asinStr="";
		StringBuffer buf1 = new StringBuffer();
		StringBuffer buf2 = new StringBuffer();
		for(AmazonPromotionsWarningItem item :items){
			buf1.append(item.getProductNameColor()+",");
			buf2.append(item.getAsin()+",");
		}
		if(StringUtils.isNotBlank(buf1.toString())){
			productStr=buf1.toString();
			asinStr=buf2.toString();
		}
	
		if(StringUtils.isNotEmpty(productStr)&&productStr.length()>0){
			productStr=productStr.substring(0,productStr.length()-1);
			asinStr=asinStr.substring(0,asinStr.length()-1);
		}
		return "{\"id\":\""+this.id+"\",\"asin\":\""+asinStr+"\",\"buyerGets\":\""+this.getBuyerGets()+"\",\"productName\":\""+productStr+"\",\"promotionId\":\""+this.promotionId+"\",\"promotionCode\":\""+this.promotionCode+"\",\"startDate\":\""+sdf.format(this.startDate)+"\",\"endDate\":\""+sdf.format(this.endDate)+"\"}";
	}
}


