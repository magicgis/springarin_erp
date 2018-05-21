package com.springrain.erp.modules.custom.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.common.persistence.DataEntity;
import com.springrain.erp.common.utils.MessageUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.excel.annotation.ExcelField;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 事件Entity
 * 
 * @author tim
 * @version 2014-05-21
 */
@Entity
@Table(name = "custom_event_manager")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class Event extends DataEntity<Event> {
	private static final long serialVersionUID = 1L;
	private Integer id;// '编号',
	private Date endDate;// '处理结束时间',
	private Date answerDate;// '响应时间',
	private String state;// '状态：0:未响应 1：已响应 2：已解决  4,已关闭
	private User masterBy;// '处理人',
	private String transmit;// '转发记录',
	private String subject;// COMMENT '主题',
	private String type;// COMMENT '类型', 1:中差评
	private String priority;// COMMENT '优先级',
	private String attchmentPath;// COMMENT '附件路径',
	private String description;// COMMENT '事件描述',
	private String result;//处理结果
	private String reason;//事件原由
	private List<Comment> comments;
	
	private String customId;// COMMENT '客户id',
	private String invoiceNumber;// COMMENT '订单号',
	private String reviewLink;// COMMENT '评论链接',
	private Date reviewDate;// '评论时间',
	private String customEmail;
	private String customName; 
	private String country;
	private String taxId;//税号
	
	
	private String name;
	private String street;
	private String street1;
	private String street2;
	private String cityName;
	private String county;
	private String stateOrProvince;
	private String countryCode;
	private String postalCode;
	private String phone;
	private String problemType;
	private String productName;
	
	//针对召回事件
	private String emailNotice;			//标记是否已邮件通知客户 0：未通知  1：已通知
	private Integer productQuantity;	// 产品数量
	private String shipToChina;			//召回产品发回中国 0：不发回  1 发回(美国默认发回)
	
	//针对Review Refund事件
	private String refundType;	//返款方式 1:Gift Card 2:Paypal Card 3:Credit Card
	private String cardNumber;	//卡号,类型对应返款方式
	private Float totalPrice;	//返款总价格
	
	private String isEvil;   //0:恶意
	
	private Float reviewPrice;
	private Integer reviewQuantity;
	
	private String  productAttribute;
	
	private String productLine;
	
	private String accountName;
	
	@Transient
	public String getProductLine() {
		return productLine;
	}

	public void setProductLine(String productLine) {
		this.productLine = productLine;
	}

	@ExcelField(title="属性", align=2, sort=21)
	public String getProductAttribute() {
		return productAttribute;
	}

	public void setProductAttribute(String productAttribute) {
		this.productAttribute = productAttribute;
	}

	@ExcelField(title="价格", align=2, sort=20)
	public Float getReviewPrice() {
		return reviewPrice;
	}

	public void setReviewPrice(Float reviewPrice) {
		this.reviewPrice = reviewPrice;
	}

	@ExcelField(title="数量", align=2, sort=19)
	public Integer getReviewQuantity() {
		return reviewQuantity;
	}

	public void setReviewQuantity(Integer reviewQuantity) {
		this.reviewQuantity = reviewQuantity;
	}

	public String getIsEvil() {
		return isEvil;
	}

	public void setIsEvil(String isEvil) {
		this.isEvil = isEvil;
	}

	public String getTaxId() {
		return taxId;
	}

	public void setTaxId(String taxId) {
		this.taxId = taxId;
	}

	@Transient
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	@Transient
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}
	@Transient
	public String getStreet1() {
		return street1;
	}

	public void setStreet1(String street1) {
		this.street1 = street1;
	}
	@Transient
	public String getStreet2() {
		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;
	}
	@Transient
	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	@Transient
	public String getCounty() {
		return county;
	}

	public void setCounty(String county) {
		this.county = county;
	}
	@Transient
	public String getStateOrProvince() {
		return stateOrProvince;
	}

	public void setStateOrProvince(String stateOrProvince) {
		this.stateOrProvince = stateOrProvince;
	}
	@Transient
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	@Transient
	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	@Transient
	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@ExcelField(title="编号", align=2, sort=3,fieldType=IdType.class)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ExcelField(title="结束时间", align=2, sort=11,fieldType=Date.class)
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@ExcelField(title="创建时间", align=2, sort=10,fieldType=Date.class)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getAnswerDate() {
		return answerDate;
	}

	public void setAnswerDate(Date answerDate) {
		this.answerDate = answerDate;
	}
	
	@ExcelField(title="状态", align=2, sort=12)
	public String getState() {
		return state;
	}

	@Transient
	public String getStateStr() {
		String rs = "";
		if("0".equals(state)){
			rs = MessageUtils.format("custom_event_noResponse");
		}else if("1".equals(state)){
			rs = MessageUtils.format("custom_event_processing");
		}else if("2".equals(state)){
			rs = MessageUtils.format("custom_event_completed");
		}else if("4".equals(state)){
			rs = MessageUtils.format("custom_event_closed");
		}
		return rs;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	@ExcelField(title="负责人", align=2, sort=2)
	public User getMasterBy() {
		return masterBy;
	}

	public void setMasterBy(User masterBy) {
		this.masterBy = masterBy;
	}
	
	public String getTransmit() {
		return transmit;
	}

	public void setTransmit(String transmit) {
		if(StringUtils.isBlank(this.transmit))
			this.transmit = transmit;
		else
			this.transmit = this.transmit+","+transmit;
	}
	
	@ExcelField(title="主题", align=2, sort=13)
	public String getSubject() {
		return HtmlUtils.htmlUnescape(subject);
	}
	
	@Transient
	public String getSubjectStr() {
		if(subject!=null && subject.replaceAll("<br/>","").length()>30){
			return subject.replaceAll("<br/>","").substring(0,30)+"...";
		}
		return subject;
	}
	
	@Transient
	public String getSubjectStr2() {
		if(subject!=null && subject.length()>0){
			return HtmlUtils.htmlEscape(subject.replace("<br/>",""));
		}
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	@ExcelField(title="类型", align=2, sort=14)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Transient
	public String getTypeStr() {
		String rs = "";
		if("1".equals(type)){
			rs = "Rating";
		}else if("2".equals(type)){
			rs = "Account Rating";
		}else if("3".equals(type)){
			rs = "FAQ_Email";
		}else if("4".equals(type)){
			rs = "Tax_Refund";
		}else if("5".equals(type)){
			rs = "Support";
		}else if("6".equals(type)){
			rs = "FAQ";
		}else if("7".equals(type)){
			rs = "Support_Voucher";
		}else if("8".equals(type)){
			rs = "Marketing Order";
		}else if("9".equals(type)){
			rs = "Product Improvement";
		}else if("10".equals(type)){
			rs = "Product Recall";
		}else if("11".equals(type)){
			rs = "Review Refund";
		}else if("12".equals(type)){
			rs = "Ebay Order";
		}else if("13".equals(type)){
			rs = "Website SupportOrder";
		}else if("14".equals(type)){
			rs = "Offline SupportOrder";
		}else if("15".equals(type)){
			rs ="MFN Order";
		}
		return rs;
	}
	
	@ExcelField(title="优先级", align=2, sort=7)
	public String getPriority() {
		return priority;
	}
	@Transient
	public String getPriorityStr() {
		String rs = "";
		if("1".equals(priority)){
			rs = "L1";
		}else if("2".equals(priority)){
			rs = "L2";
		}else if("3".equals(priority)){
			rs = "L3";
		}
		return rs;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	@ExcelField(title="客户信息(除Review Order 外，其他为客户id)", align=2, sort=120)
	public String getCustomId() {
		return customId;
	}

	public void setCustomId(String customId) {
		this.customId = customId;
	}
	
	@ExcelField(title="订单号", align=2, sort=15)
	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	
	@ExcelField(title="评论链接", align=2, sort=16)
	public String getReviewLink() {
		return reviewLink;
	}

	public void setReviewLink(String reviewLink) {
		this.reviewLink = reviewLink;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}
	
	@ExcelField(title="解决方法", align=2, sort=6)
	public String getAttchmentPath() {
		return attchmentPath;
	}

	public void setAttchmentPath(String attchmentPath) {
		if(StringUtils.isBlank(this.attchmentPath)){
			this.attchmentPath = attchmentPath;
		}else{
			this.attchmentPath = this.attchmentPath+","+attchmentPath;
		}	
	}
	
	@OneToMany(mappedBy = "event", fetch=FetchType.LAZY)
	@Where(clause="del_flag='"+DEL_FLAG_NORMAL+"'")
	@Fetch(FetchMode.SUBSELECT)
	@OrderBy("createDate")
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	@ExcelField(title="产品型号", align=2, sort=4)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = HtmlUtils.htmlUnescape(description);
	}

	@Transient
	public int getMasterTime(){
		if(endDate!=null && answerDate!=null){
			long time = endDate.getTime() - answerDate.getTime();
			return Math.round(time/60000f);
		}
		return -1;
	}
	
	@Transient
	public String getSuff(){
		if(StringUtils.isNotEmpty(country)){
			if("com.unitek".equals(country)){
				return "com";
			}else if("uk".equals(country)||"jp".equals(country)){
				return "co."+country;
			}else{
				return country;
			}
		}
		return "";
	}
	
	@ExcelField(title="处理结果", align=2, sort=8)
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	@ExcelField(title="客户邮箱", align=2, sort=17)
	public String getCustomEmail() {
		return customEmail;
	}

	public void setCustomEmail(String customEmail) {
		this.customEmail = customEmail;
	}
	
	@ExcelField(title="客户名", align=2, sort=18)
	public String getCustomName() {
		return customName;
	}

	public void setCustomName(String customName) {
		this.customName = customName;
	}
	
	@ExcelField(title="平台", align=2, sort=1,dictType="platform")
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	@ExcelField(title="事件原由", align=2, sort=5)
	public String getReason() {
		return reason;
	}

	@Transient
	public String getReasonStr() {
		if(reason!=null && reason.length()>30){
			return reason.substring(0,30)+"...";
		}
		return reason;
	}
	
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	@Transient
	@ExcelField(title="创建人", align=2, sort=9)
	public User getCreateBy() {
		return super.getCreateBy();
	}
	
	public String getProblemType() {
		return problemType;
	}

	public void setProblemType(String problemType) {
		this.problemType = problemType;
	}

	@ExcelField(title="产品名", align=0, sort=5)
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getEmailNotice() {
		return emailNotice;
	}

	public void setEmailNotice(String emailNotice) {
		this.emailNotice = emailNotice;
	}

	public Integer getProductQuantity() {
		return productQuantity;
	}

	public void setProductQuantity(Integer productQuantity) {
		this.productQuantity = productQuantity;
	}

	public String getShipToChina() {
		return shipToChina;
	}

	public void setShipToChina(String shipToChina) {
		this.shipToChina = shipToChina;
	}

	public String getRefundType() {
		return refundType;
	}

	public void setRefundType(String refundType) {
		this.refundType = refundType;
	}

	public String getCardNumber() {
		return cardNumber;
	}

	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}

	public Float getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Float totalPrice) {
		this.totalPrice = totalPrice;
	}

	public Event() {}
	
	public Event(String state, User masterBy, String subject,
			String type, String priority, String customId, String reviewLink,
			Date reviewDate, String description, String customName, String customEmail,String country,String reason) {
		super();
		this.state = state;
		this.masterBy = masterBy;
		this.subject = subject;
		this.type = type;
		this.priority = priority;
		this.customId = customId;
		this.reviewLink = reviewLink;
		this.reviewDate = reviewDate;
		this.description = description;
		this.customName = customName;
		this.customEmail = customEmail;
		this.country = country;
		this.reason = reason;
	}

	@Override
	public String toString() {
		return "[" + subject + "]";
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
}
