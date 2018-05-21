/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.lc;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 折扣预警Entity
 * @author Michael
 * @version 2015-08-24
 */
@Entity
@Table(name = "lc_psi_quality_test")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class LcPsiQualityTest implements Serializable {
	
	private static final long serialVersionUID = -6536754047653937704L;
	private 		Integer 		id; 			// id
	private     	String      	aql;            // AQL
	private         Integer         testQuantity;   // 抽样数 
	private     	String      	inView;         // 内观
	private     	String      	outView;        // 外观
	private     	String      	packing;        // 包装
	private     	String      	function;       // 功能
	private     	String      	isOk;           // 1：合格0：不合格
	private     	String      	dealWay;        // 1：让步接收 0：特采  2：返工
	private         String          reason;         // 原因
	private         String          reportFile;     // 品检报告文件
	private         String          giveInFile;     // 特采申请单、让步联络函
	
    private         Integer         ladingId;       // 提货单id

    private         String          ladingBillNo;   // 提单号
    private         Integer         productId;      // 产品id
    private         String          productName;    // 产品名
    private         String          color;          // 颜色
	
    private     	User        	createUser;     // 创建人
	private     	Date        	createDate;     // 创建日期
    private     	User        	sureUser;       // 确认人
	private     	Date        	sureDate;       // 确认日期
	private         Integer         okQuantity;     // 合格数
	private         Integer       	receivedQuantity; // 收货数
	private         Integer         totalQuantity;  // 质检数
	
	private         String          testSta;        // 质检状态  0：草稿   3：申请   5：已审核  8:取消
	private     	User        	cancelUser;     // 创建人
	private     	Date        	cancelDate;     // 创建日期
	
    private         Integer         supplierId;     // 供应商id
    
    private         String          reviewRemark;   // 审核备注
    private         String          reviewRemark1;  // 采购备注
    private         String          reviewRemark2;  // 产品备注
    private         String          reviewRemark3;  // 质检备注
    
    private         User            reviewUser;     // 备注人
    private         User            reviewUser1;    // 采购备注人
    private         User            reviewUser2;    // 产品备注人
    private         User            reviewUser3;    // 质检备注人
    
    private         Date            reviewDate;     // 录入日期
    private         Date            reviewDate1;    // 录入日期
    private         Date            reviewDate2;    // 录入日期
    private         Date            reviewDate3;    // 录入日期
    
	public LcPsiQualityTest() {
		super();
	}
	
	public LcPsiQualityTest(Integer id){
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

	public String getAql() {
		return aql;
	}

	public void setAql(String aql) {
		this.aql = aql;
	}

	public Integer getTestQuantity() {
		return testQuantity;
	}

	public void setTestQuantity(Integer testQuantity) {
		this.testQuantity = testQuantity;
	}

	public String getInView() {
		return inView;
	}

	public void setInView(String inView) {
		this.inView = inView;
	}

	public String getOutView() {
		return outView;
	}

	public void setOutView(String outView) {
		this.outView = outView;
	}

	public String getPacking() {
		return packing;
	}

	public void setPacking(String packing) {
		this.packing = packing;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getIsOk() {
		return isOk;
	}

	public void setIsOk(String isOk) {
		this.isOk = isOk;
	}

	public String getDealWay() {
		return dealWay;
	}

	public void setDealWay(String dealWay) {
		this.dealWay = dealWay;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getReportFile() {
		return reportFile;
	}

	public void setReportFile(String reportFile) {
		this.reportFile = reportFile;
	}

	public String getGiveInFile() {
		return giveInFile;
	}

	public void setGiveInFile(String giveInFile) {
		this.giveInFile = giveInFile;
	}

	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "create_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Integer getLadingId() {
		return ladingId;
	}

	public void setLadingId(Integer ladingId) {
		this.ladingId = ladingId;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
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

	public String getLadingBillNo() {
		return ladingBillNo;
	}

	public void setLadingBillNo(String ladingBillNo) {
		this.ladingBillNo = ladingBillNo;
	}
	
	@Transient
	public String getProductNameColor(){
		String productName=this.productName;
		if(StringUtils.isNotEmpty(color)){
			productName=productName+"_"+color;
		}
		return productName;
	}
	
	public void setReportPathAppend(String reportPath) {
		if(StringUtils.isBlank(this.reportFile)){
			this.reportFile = reportPath;
		}else{
			this.reportFile = this.reportFile+","+reportPath;
		}
	}
	
	
	public void setGiveInPathAppend(String giveInPath) {
		if(StringUtils.isBlank(this.giveInFile)){
			this.giveInFile = giveInPath;
		}else{
			this.giveInFile = this.giveInFile+","+giveInPath;
		}
	}

	public Integer getOkQuantity() {
		return okQuantity;
	}

	public void setOkQuantity(Integer okQuantity) {
		this.okQuantity = okQuantity;
	}

	public Integer getReceivedQuantity() {
		return receivedQuantity;
	}

	public void setReceivedQuantity(Integer receivedQuantity) {
		this.receivedQuantity = receivedQuantity;
	}

	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sure_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getSureUser() {
		return sureUser;
	}

	public void setSureUser(User sureUser) {
		this.sureUser = sureUser;
	}

	public Date getSureDate() {
		return sureDate;
	}

	public void setSureDate(Date sureDate) {
		this.sureDate = sureDate;
	}
	
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cancel_user")
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

	public Integer getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(Integer totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public String getTestSta() {
		return testSta;
	}

	public void setTestSta(String testSta) {
		this.testSta = testSta;
	}

	public Integer getSupplierId() {
		return supplierId;
	}

	public void setSupplierId(Integer supplierId) {
		this.supplierId = supplierId;
	}

	public String getReviewRemark() {
		return reviewRemark;
	}

	public void setReviewRemark(String reviewRemark) {
		this.reviewRemark = reviewRemark;
	}

	public String getReviewRemark1() {
		return reviewRemark1;
	}

	public void setReviewRemark1(String reviewRemark1) {
		this.reviewRemark1 = reviewRemark1;
	}

	public String getReviewRemark2() {
		return reviewRemark2;
	}

	public void setReviewRemark2(String reviewRemark2) {
		this.reviewRemark2 = reviewRemark2;
	}

	public String getReviewRemark3() {
		return reviewRemark3;
	}

	public void setReviewRemark3(String reviewRemark3) {
		this.reviewRemark3 = reviewRemark3;
	}

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getReviewUser() {
		return reviewUser;
	}

	public void setReviewUser(User reviewUser) {
		this.reviewUser = reviewUser;
	}

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_user1")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getReviewUser1() {
		return reviewUser1;
	}

	public void setReviewUser1(User reviewUser1) {
		this.reviewUser1 = reviewUser1;
	}

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_user2")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getReviewUser2() {
		return reviewUser2;
	}

	public void setReviewUser2(User reviewUser2) {
		this.reviewUser2 = reviewUser2;
	}

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "review_user3")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getReviewUser3() {
		return reviewUser3;
	}

	public void setReviewUser3(User reviewUser3) {
		this.reviewUser3 = reviewUser3;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	public Date getReviewDate1() {
		return reviewDate1;
	}

	public void setReviewDate1(Date reviewDate1) {
		this.reviewDate1 = reviewDate1;
	}

	public Date getReviewDate2() {
		return reviewDate2;
	}

	public void setReviewDate2(Date reviewDate2) {
		this.reviewDate2 = reviewDate2;
	}

	public Date getReviewDate3() {
		return reviewDate3;
	}

	public void setReviewDate3(Date reviewDate3) {
		this.reviewDate3 = reviewDate3;
	}

	public LcPsiQualityTest(String aql, Integer testQuantity, String inView,
			String outView, String packing, String function, String isOk,
			String dealWay, String reason, String reportFile,
			String giveInFile, Integer ladingId, String ladingBillNo,
			Integer productId, String productName, String color,
			User createUser, Date createDate, User sureUser, Date sureDate,
			Integer okQuantity, Integer receivedQuantity,
			Integer totalQuantity, String testSta, User cancelUser,
			Date cancelDate, Integer supplierId) {
		super();
		this.aql = aql;
		this.testQuantity = testQuantity;
		this.inView = inView;
		this.outView = outView;
		this.packing = packing;
		this.function = function;
		this.isOk = isOk;
		this.dealWay = dealWay;
		this.reason = reason;
		this.reportFile = reportFile;
		this.giveInFile = giveInFile;
		this.ladingId = ladingId;
		this.ladingBillNo = ladingBillNo;
		this.productId = productId;
		this.productName = productName;
		this.color = color;
		this.createUser = createUser;
		this.createDate = createDate;
		this.sureUser = sureUser;
		this.sureDate = sureDate;
		this.okQuantity = okQuantity;
		this.receivedQuantity = receivedQuantity;
		this.totalQuantity = totalQuantity;
		this.testSta = testSta;
		this.cancelUser = cancelUser;
		this.cancelDate = cancelDate;
		this.supplierId = supplierId;
	}
	
	
	
}


