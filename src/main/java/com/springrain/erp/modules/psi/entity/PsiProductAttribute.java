package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

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
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 产品分颜色不分平台属性Entity
 */
@Entity
@Table(name = "psi_product_attribute")
public class PsiProductAttribute implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private PsiProduct product; // 产品
	private String productName;
	private String color;
	private Integer quantity;	//最大库存
    private Date createDate;
    private User createUser;
    
	private String delFlag; // 删除标记
	private Integer purchaseWeek; // 采购周 (0:当前周,1:当前周+1周,2:当前周+2周,3:当前周+3周)
	private double inventorySaleMonth;	//库销比(库存可销月数)
	private Date unshelveDate;	//下架时间
	
	private Float moqPrice;
	private String currencyType;
	private String cameraman;
	
	public String getCameraman() {
		return cameraman;
	}

	public void setCameraman(String cameraman) {
		this.cameraman = cameraman;
	}

	public String getCurrencyType() {
		return currencyType;
	}

	public void setCurrencyType(String currencyType) {
		this.currencyType = currencyType;
	}

	public Float getMoqPrice() {
		return moqPrice;
	}

	public void setMoqPrice(Float moqPrice) {
		this.moqPrice = moqPrice;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne()
	@JoinColumn(name = "product_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiProduct getProduct() {
		return product;
	}

	public void setProduct(PsiProduct product) {
		this.product = product;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@ManyToOne()
	@JoinColumn(name = "create_user")
	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public PsiProductAttribute() {
	}

	public PsiProductAttribute(String productName, String color,Integer quantity, Date createDate) {
		super();
		this.productName = productName;
		this.color = color;
		this.quantity = quantity;
		this.createDate = createDate;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	
	@Transient
	public String getColorName() {
		String name = getProductName();
		if (StringUtils.isNotEmpty(color)) {
			name = name + "_" + color;
		}
		return name;
	}

	public Integer getPurchaseWeek() {
		return purchaseWeek;
	}

	public void setPurchaseWeek(Integer purchaseWeek) {
		this.purchaseWeek = purchaseWeek;
	}
	
	public double getInventorySaleMonth() {
		return inventorySaleMonth;
	}

	public void setInventorySaleMonth(double inventorySaleMonth) {
		this.inventorySaleMonth = inventorySaleMonth;
	}
	
	public Date getUnshelveDate() {
		return unshelveDate;
	}

	public void setUnshelveDate(Date unshelveDate) {
		this.unshelveDate = unshelveDate;
	}
	
	
	//运输周
	@Transient
	public String getTransportWeekStr() {
		if (purchaseWeek == null) {
			return "";
		} else {
			int w1 = new BigDecimal(product.getProducePeriod()/7d).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
			int w = purchaseWeek + w1 + 1;
			Calendar c = Calendar.getInstance();
			c.setFirstDayOfWeek(Calendar.MONDAY);
			c.setMinimalDaysInFirstWeek(1);
			c.setTime(DateUtils.addWeeks(new Date(), w));
			return c.get(Calendar.YEAR) + "年第" + c.get(Calendar.WEEK_OF_YEAR) + "周";
		}
	}
	
}
