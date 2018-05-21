package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.modules.sys.entity.User;

/**
 * 进销存产品
 */
@Entity
@Table(name = "psi_product_tiered_price_review")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiProductTieredPriceDto implements Serializable{
	private static final long serialVersionUID = -733315768876841859L;
	private			 Integer 			id;                 //id
	private			 Integer 			productId;          //产品id
	private			 Integer 			supplierId;         //供应商id
	private          String             proNameColor;       //产品名&颜色
	private			 String   		    nikeName;           //供应商名字
	private			 String   		    color;              //颜色
	private			 String   		    currencyType;       //货币类型
	private          Integer            moq;                //最小下单量
	
	private          Float              leval500usd;         //档次
	private          Float              leval1000usd;        //档次
	private          Float              leval2000usd;        //档次
	private          Float              leval3000usd;        //档次
	private          Float              leval5000usd;        //档次
	private          Float              leval10000usd;       //档次
	private          Float              leval15000usd;       //档次
	
	private          Float              leval500cny;         //档次
	private          Float              leval1000cny;        //档次
	private          Float              leval2000cny;        //档次
	private          Float              leval3000cny;        //档次
	private          Float              leval5000cny;        //档次
	private          Float              leval10000cny;       //档次
	private          Float              leval15000cny;       //档次
	
	
	private          Float              before500usd;         //改前档次价
	private          Float              before1000usd;        //改前档次价
	private          Float              before2000usd;        //改前档次价
	private          Float              before3000usd;        //改前档次价
	private          Float              before5000usd;        //改前档次价
	private          Float              before10000usd;       //改前档次价
	private          Float              before15000usd;       //改前档次价
	
	private          Float              before500cny;         //改前档次价
	private          Float              before1000cny;        //改前档次价
	private          Float              before2000cny;        //改前档次价
	private          Float              before3000cny;        //改前档次价
	private          Float              before5000cny;        //改前档次价
	private          Float              before10000cny;       //改前档次价
	private          Float              before15000cny;       //改前档次价
	
	
	private          String              remark;                 //备注
	private          String              content;                //原因
	private          boolean             hasMulColor;            //有多个颜色
	private          String              hasColor;               //有多个颜色，状态
	private          Integer             taxRate;                //税率(显示用，非字段)
	private          String              reviewSta;              //审核状态：0：未审核，1：已审核   3：已取消
	
	private 	     User             	 reviewUser;
	private 		 Date                reviewDate;
	private 		 User                cancelUser;
	private 		 Date                cancelDate;
	private 		 Date 			     createDate;
	private 		 User 			     createUser;
	
	private          String              filePath;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getProductId() {
		return productId;
	}
	public void setProductId(Integer productId) {
		this.productId = productId;
	}
	public Integer getSupplierId() {
		return supplierId;
	}
	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}
	public String getProNameColor() {
		return proNameColor;
	}
	public void setProNameColor(String proNameColor) {
		this.proNameColor = proNameColor;
	}
	public String getNikeName() {
		return nikeName;
	}
	public void setNikeName(String nikeName) {
		this.nikeName = nikeName;
	}
	
	public String getHasColor() {
		return hasColor;
	}
	public void setHasColor(String hasColor) {
		this.hasColor = hasColor;
	}
	public Float getLeval500usd() {
		return leval500usd;
	}
	public void setLeval500usd(Float leval500usd) {
		this.leval500usd = leval500usd;
	}
	public Float getLeval1000usd() {
		return leval1000usd;
	}
	public void setLeval1000usd(Float leval1000usd) {
		this.leval1000usd = leval1000usd;
	}
	public Float getLeval2000usd() {
		return leval2000usd;
	}
	public void setLeval2000usd(Float leval2000usd) {
		this.leval2000usd = leval2000usd;
	}
	public Float getLeval3000usd() {
		return leval3000usd;
	}
	public void setLeval3000usd(Float leval3000usd) {
		this.leval3000usd = leval3000usd;
	}
	public Float getLeval5000usd() {
		return leval5000usd;
	}
	public void setLeval5000usd(Float leval5000usd) {
		this.leval5000usd = leval5000usd;
	}
	public Float getLeval10000usd() {
		return leval10000usd;
	}
	public void setLeval10000usd(Float leval10000usd) {
		this.leval10000usd = leval10000usd;
	}
	public Float getLeval15000usd() {
		return leval15000usd;
	}
	public void setLeval15000usd(Float leval15000usd) {
		this.leval15000usd = leval15000usd;
	}
	public Float getLeval500cny() {
		return leval500cny;
	}
	public void setLeval500cny(Float leval500cny) {
		this.leval500cny = leval500cny;
	}
	public Float getLeval1000cny() {
		return leval1000cny;
	}
	public void setLeval1000cny(Float leval1000cny) {
		this.leval1000cny = leval1000cny;
	}
	public Float getLeval2000cny() {
		return leval2000cny;
	}
	public void setLeval2000cny(Float leval2000cny) {
		this.leval2000cny = leval2000cny;
	}
	public Float getLeval3000cny() {
		return leval3000cny;
	}
	public void setLeval3000cny(Float leval3000cny) {
		this.leval3000cny = leval3000cny;
	}
	public Float getLeval5000cny() {
		return leval5000cny;
	}
	public void setLeval5000cny(Float leval5000cny) {
		this.leval5000cny = leval5000cny;
	}
	public Float getLeval10000cny() {
		return leval10000cny;
	}
	public void setLeval10000cny(Float leval10000cny) {
		this.leval10000cny = leval10000cny;
	}
	public Float getLeval15000cny() {
		return leval15000cny;
	}
	public void setLeval15000cny(Float leval15000cny) {
		this.leval15000cny = leval15000cny;
	}
	
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	@Transient
	public Integer getMoq() {
		return moq;
	}
	public void setMoq(Integer moq) {
		this.moq = moq;
	}
	public String getCurrencyType() {
		return currencyType;
	}
	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	@Transient
	public boolean getHasMulColor() {
		return hasMulColor;
	}
	public void setHasMulColor(boolean hasMulColor) {
		this.hasMulColor = hasMulColor;
	}
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	@Transient
	public Integer getTaxRate() {
		return taxRate;
	}
	public void setTaxRate(Integer taxRate) {
		this.taxRate = taxRate;
	}
	
	@ManyToOne()
	@JoinColumn(name="review_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getReviewUser() {
		return reviewUser;
	}
	public void setReviewUser(User reviewUser) {
		this.reviewUser = reviewUser;
	}
	public Date getReviewDate() {
		return reviewDate;
	}
	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}
	
	@ManyToOne()
	@JoinColumn(name="cancel_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCancelUser() {
		return cancelUser;
	}
	public void setCancelUser(User cancelUser) {
		this.cancelUser = cancelUser;
	}
	public Date getCancelDate() {
		return cancelDate;
	}
	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
	public String getReviewSta() {
		return reviewSta;
	}
	public void setReviewSta(String reviewSta) {
		this.reviewSta = reviewSta;
	}
	
	public Float getBefore500usd() {
		return before500usd;
	}
	public void setBefore500usd(Float before500usd) {
		this.before500usd = before500usd;
	}
	public Float getBefore1000usd() {
		return before1000usd;
	}
	public void setBefore1000usd(Float before1000usd) {
		this.before1000usd = before1000usd;
	}
	public Float getBefore2000usd() {
		return before2000usd;
	}
	public void setBefore2000usd(Float before2000usd) {
		this.before2000usd = before2000usd;
	}
	public Float getBefore3000usd() {
		return before3000usd;
	}
	public void setBefore3000usd(Float before3000usd) {
		this.before3000usd = before3000usd;
	}
	public Float getBefore5000usd() {
		return before5000usd;
	}
	public void setBefore5000usd(Float before5000usd) {
		this.before5000usd = before5000usd;
	}
	public Float getBefore10000usd() {
		return before10000usd;
	}
	public void setBefore10000usd(Float before10000usd) {
		this.before10000usd = before10000usd;
	}
	public Float getBefore15000usd() {
		return before15000usd;
	}
	public void setBefore15000usd(Float before15000usd) {
		this.before15000usd = before15000usd;
	}
	public Float getBefore500cny() {
		return before500cny;
	}
	public void setBefore500cny(Float before500cny) {
		this.before500cny = before500cny;
	}
	public Float getBefore1000cny() {
		return before1000cny;
	}
	public void setBefore1000cny(Float before1000cny) {
		this.before1000cny = before1000cny;
	}
	public Float getBefore2000cny() {
		return before2000cny;
	}
	public void setBefore2000cny(Float before2000cny) {
		this.before2000cny = before2000cny;
	}
	public Float getBefore3000cny() {
		return before3000cny;
	}
	public void setBefore3000cny(Float before3000cny) {
		this.before3000cny = before3000cny;
	}
	public Float getBefore5000cny() {
		return before5000cny;
	}
	public void setBefore5000cny(Float before5000cny) {
		this.before5000cny = before5000cny;
	}
	public Float getBefore10000cny() {
		return before10000cny;
	}
	public void setBefore10000cny(Float before10000cny) {
		this.before10000cny = before10000cny;
	}
	public Float getBefore15000cny() {
		return before15000cny;
	}
	public void setBefore15000cny(Float before15000cny) {
		this.before15000cny = before15000cny;
	}
	@ManyToOne()
	@JoinColumn(name="create_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}
	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public PsiProductTieredPriceDto(){}
	
	
}
