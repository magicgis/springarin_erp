package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
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
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 亚马逊帖子上架Entity
 * @author tim
 * @version 2014-08-06
 */
@Entity
@Table(name = "amazoninfo_feed_submission")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FeedSubmission implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private Integer id; 		// 编号

	private String feedSubmissionReqId;
	
	private String state = "0";//0:发送中 1:正在发送 2:发送成功  3:结果返回
	
	private String excelFile;
	
	private String resultFile;
	
	private String result;
	
	private Date createDate;
	
	private User createBy;
	
	private String country;
	
	private List<Feed> feeds;
	
	private Date endDate;
	
	private String delFlag;
	
	private String accountName;
	
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	@Transient
	public Date getEndDate() {
		return endDate;
	}
	@Transient
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public FeedSubmission() {
		super();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFeedSubmissionReqId() {
		return feedSubmissionReqId;
	}

	public void setFeedSubmissionReqId(String feedSubmissionReqId) {
		this.feedSubmissionReqId = feedSubmissionReqId;
	}

	public String getState() {
		return state;
	}

	@Transient
	public String getStateStr() {
		if("0".equals(state)){
			return "已存档";
		}else if("1".equals(state)){
			return "正在发送到服务器...";
		}else if("2".equals(state)){
			return "正在等待反馈结果...";
		}else if("3".equals(state)){
			return "帖子上传完成";
		}else if("4".equals(state)){
			if(StringUtils.isEmpty(feedSubmissionReqId)){
				return "上传失败,可能亚马逊服务不可用或网络原因,请稍后重试！";
			}else{
				return "累计10次获取结果失败,请自行观察上传结果;可能亚马逊服务不稳定或网络原因！";
			}
		}
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}
	
	public String getExcelFile() {
		return excelFile;
	}

	public void setExcelFile(String excelFile) {
		this.excelFile = excelFile;
	}

	public String getResultFile() {
		return resultFile;
	}

	public void setResultFile(String resultFile) {
		this.resultFile = resultFile;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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
	
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	@OneToMany(mappedBy = "feedSubmission", fetch=FetchType.LAZY)
	@OrderBy(value="id")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	@Cascade(CascadeType.SAVE_UPDATE)
	public List<Feed> getFeeds() {
		return feeds;
	}

	public void setFeeds(List<Feed> feeds) {
		this.feeds = feeds;
	}
	
}


