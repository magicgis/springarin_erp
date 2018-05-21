package com.springrain.erp.modules.amazoninfo.entity.customer;

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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.custom.entity.CustomEmailTemplate;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 售后邮件发送客户过滤条件Entity
 * @author lee
 * @date 2016-4-11
 */
@Entity
@Table(name = "amazoninfo_custom_filter")
public class AmazonCustomFilter {

	private Integer id ;
    private Date startDate;		//购买起始日期
    private Date endDate;		//购买截止日期
    private AmazonCustomer customer;
	private String email;		//邮箱(亚马逊或自有邮箱)
	private String name;		//客户名称
	private String country;		//平台
	private String buyTimes;	//购买次数 1：一次 2：多次
	private String returnFlag;	//退货情况(为空时忽略)0:未退货 1：退过货

	//购买产品信息
	private String pn1;		//购买1
	private String pn2;		//购买2
	private String pn3;		//购买3
	private String pnAnd;	//购买复合关系 1：且 0：或

	//未购买产品信息
	private String pn11;		//未购买1
	private String pn22;		//未购买2
	private String pn33;		//未购买3
	private String pn1And;		//未购买复合关系 1：且  0：或
	
	private String good;		//留页面好评情况 1:留过好评 0：未留过好评
	private String error;		//留页面差评情况 1:留过差评 0：未留过差评
	private String pl;			//购买频率 30、90、182分别表示一个月、三个月、半年
	
	private User createBy;
    private Date createDate;
	private String delFlag;		//删除标记 0：正常 1：删除
    private CustomEmailTemplate template;	//邮件模板
    private Integer totalCustomer = 0;	//满足任务条件的客户总数
    private String state;	//0:未开始  1：进行中  2：已完成
    private String bigOrder;	// 0:普通单  1：大订单
    private Integer sendDelay = 0;	//延迟发送天数,默认为0不延迟(针对发货时间)
    
    private List<AmazonComment> comments;
    
    private Integer sendNum = 0;	//已发送数
    private Integer notSendNum = 0;	//未发送数
    private Integer replyNum = 0;	//回复数
    private Integer reviewNum = 0;	//评论数
    private String reason;	// 发送原因
    private String taskType;	// 任务类型  1：售后询问	2：邀请评测
    private String auditState;	// 审核状态  0：待审 	1：通过 	2：否决
    private String fileName;	// 格式 uuid/fileName 用于支持邮件附件功能
    private String filePath;	// file AbsolutePath 用于支持邮件附件功能
    
    public AmazonCustomFilter() {}

    @Id
   	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne()
	@JoinColumn(name="customer_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonCustomer getCustomer() {
		return customer;
	}

	public void setCustomer(AmazonCustomer customer) {
		this.customer = customer;
	}
	
	@ManyToOne()
	@JoinColumn(name="template_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public CustomEmailTemplate getTemplate() {
		return template;
	}

	public void setTemplate(CustomEmailTemplate template) {
		this.template = template;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getBuyTimes() {
		return buyTimes;
	}

	public void setBuyTimes(String buyTimes) {
		this.buyTimes = buyTimes;
	}

	public String getReturnFlag() {
		return returnFlag;
	}

	public void setReturnFlag(String returnFlag) {
		this.returnFlag = returnFlag;
	}

	public String getPn1() {
		return pn1;
	}

	public void setPn1(String pn1) {
		this.pn1 = pn1;
	}

	public String getPn2() {
		return pn2;
	}

	public void setPn2(String pn2) {
		this.pn2 = pn2;
	}

	public String getPn3() {
		return pn3;
	}

	public void setPn3(String pn3) {
		this.pn3 = pn3;
	}

	public String getPnAnd() {
		return pnAnd;
	}

	public void setPnAnd(String pnAnd) {
		this.pnAnd = pnAnd;
	}

	public String getPn11() {
		return pn11;
	}

	public void setPn11(String pn11) {
		this.pn11 = pn11;
	}

	public String getPn22() {
		return pn22;
	}

	public void setPn22(String pn22) {
		this.pn22 = pn22;
	}

	public String getPn33() {
		return pn33;
	}

	public void setPn33(String pn33) {
		this.pn33 = pn33;
	}

	public String getPn1And() {
		return pn1And;
	}

	public void setPn1And(String pn1And) {
		this.pn1And = pn1And;
	}

	public String getGood() {
		return good;
	}

	public void setGood(String good) {
		this.good = good;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getPl() {
		return pl;
	}

	public void setPl(String pl) {
		this.pl = pl;
	}
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateBy() {
		return createBy;
	}

	public void setCreateBy(User createBy) {
		this.createBy = createBy;
	}

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

	public Integer getTotalCustomer() {
		return totalCustomer;
	}

	public void setTotalCustomer(Integer totalCustomer) {
		this.totalCustomer = totalCustomer;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	@Transient
	public String getStateStr() {
		String rs = "";
		if("0".equals(state)){
			rs = "未开始";
		}else if("1".equals(state)){
			rs = "进行中";
		}else if("2".equals(state)){
			rs = "已完成";
		}else if("3".equals(state)){
			rs = "已取消";
		}else if("4".equals(state)){
			rs = "已暂停";
		}
		return rs;
	}

	@OneToMany(mappedBy = "task",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<AmazonComment> getComments() {
		return comments;
	}

	public void setComments(List<AmazonComment> comments) {
		this.comments = comments;
	}

	public String getBigOrder() {
		return bigOrder;
	}

	public void setBigOrder(String bigOrder) {
		this.bigOrder = bigOrder;
	}

	public Integer getSendDelay() {
		return sendDelay;
	}

	public void setSendDelay(Integer sendDelay) {
		this.sendDelay = sendDelay;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Integer getSendNum() {
		return sendNum;
	}

	public void setSendNum(Integer sendNum) {
		this.sendNum = sendNum;
	}

	public Integer getNotSendNum() {
		return notSendNum;
	}

	public void setNotSendNum(Integer notSendNum) {
		this.notSendNum = notSendNum;
	}

	public Integer getReplyNum() {
		return replyNum;
	}

	public void setReplyNum(Integer replyNum) {
		this.replyNum = replyNum;
	}

	public Integer getReviewNum() {
		return reviewNum;
	}

	public void setReviewNum(Integer reviewNum) {
		this.reviewNum = reviewNum;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}

	public String getAuditState() {
		return auditState;
	}

	public void setAuditState(String auditState) {
		this.auditState = auditState;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Transient
	public String getRemark() {
		String mark = "";
		String and1 = "1".equals(pnAnd)?"和":"或";
		String and2 = "1".equals(pn1And)?"和":"或";
		if (StringUtils.isNotEmpty(pn1)) {
			mark += ("购买过：" + pn1);
		}
		if (StringUtils.isNotEmpty(pn2)) {
			mark += (and1 + pn2);
		}
		if (StringUtils.isNotEmpty(pn3)) {
			mark += (and1 + pn3);
		}
		if (StringUtils.isNotEmpty(pn11)) {
			mark += ("<br/>未购买过：" + pn11);
		}
		if (StringUtils.isNotEmpty(pn22)) {
			mark += (and2 + pn22);
		}
		if (StringUtils.isNotEmpty(pn33)) {
			mark += (and2 + pn33);
		}
		return mark;
	}

	//回复率
	@Transient
	public Float getReplyPct() {
		if (sendNum > 0) {
			return replyNum/(float)sendNum;
		}
		return 0f;
	}

	//评论率
	@Transient
	public Float getReviewPct() {
		if (sendNum > 0) {
			return reviewNum/(float)sendNum;
		}
		return 0f;
	}

}
