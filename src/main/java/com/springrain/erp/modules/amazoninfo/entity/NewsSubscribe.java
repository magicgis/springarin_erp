package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.web.NewsSubscribeController;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 邮件订阅
 * 
 */
@Entity
@Table(name = "amazoninfo_news_subscribe")
public class NewsSubscribe implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id; // 编号
	private String platform;	//格式：逗号分隔
	private String productName;
	private String email;
	private String emailType;	//订阅的邮件类型,多选
	private String state;	//生效标记 0:未生效 1：生效
	private String delFlag;
	private User createBy;
	private Date createDate;
	private String type;	//筛选条件分类 1：按产品  2：按产品类型  3：按产品线  4：按产品属性(1:新品、2:主力、3:淘汰)
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmailType() {
		return emailType;
	}

	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Transient
	public String getEmailTypeStr() {
		String rs = "";
		if (StringUtils.isNotEmpty(emailType)) {
			for (String key : emailType.split(",")) {
				rs += (NewsSubscribeController.emailTypeMap.get(key)==null?"":NewsSubscribeController.emailTypeMap.get(key) + ",");
			}
			if (StringUtils.isNotEmpty(rs)) {
				rs = rs.substring(0, rs.length()-1);
			}
		}
		return rs;
	}

	@Transient
	public String getSubscribeStr() {
		String rs = "";
		StringBuffer buf= new StringBuffer();
		if ("4".equals(type)) {
			for (String key : productName.split(",")) {
				buf.append(NewsSubscribeController.attrMap.get(key) + ",");
			}
			rs=buf.toString();
			if(StringUtils.isNotEmpty(rs)){
				rs = rs.substring(0, rs.length()-1);
			}
		} else {
			rs = productName;
		}
		return rs;
	}
	
}
