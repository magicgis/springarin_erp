/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.oa.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.modules.sys.entity.Office;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 招聘Entity
 * 2016-11-21 michael
 */
@Entity
@Table(name = "oa_recruit")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Recruit  implements Serializable {
	private static final long serialVersionUID = -5175984604370087706L;
	private		 Integer   		 id; 		          	 // id
	private      String          name;                   // 名字
	private      String          sex;                    // 男、女
	private      String          phone;                  // 电话
	private      String          email;                  // 邮箱
	private      String          origin;                 // 简历来源
	private      String          position;               // 职位
	
	private      Date            noticeDate;             // 通知日期
	private      Date            interviewDate;          // 面试日期
	private      String          interviewReview1;       // 初试评价
	private      String          interviewReview2;       // 复试评价
	private      String          resumeUrl;              // 简历链接
	private      String          resumeFile;             // 简历
	private      String          remark;                 // 备注
	
	private       User           createUser;             // 创建人
	private       Date           createDate;             // 创建时间
	private       User           updateUser;             // 编辑人
	private       Date           updateDate;             // 编辑时间
	
	private      String          delFlag;                // 删除状态
	
	private      Office          office;                 // 部门
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	
	public Recruit() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	@ManyToOne()
	@JoinColumn(name="office")
	@NotFound(action = NotFoundAction.IGNORE)
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Date getNoticeDate() {
		return noticeDate;
	}

	public void setNoticeDate(Date noticeDate) {
		this.noticeDate = noticeDate;
	}

	public Date getInterviewDate() {
		return interviewDate;
	}

	public void setInterviewDate(Date interviewDate) {
		this.interviewDate = interviewDate;
	}

	public String getInterviewReview1() {
		return interviewReview1;
	}

	public void setInterviewReview1(String interviewReview1) {
		this.interviewReview1 = interviewReview1;
	}

	public String getInterviewReview2() {
		return interviewReview2;
	}

	public void setInterviewReview2(String interviewReview2) {
		this.interviewReview2 = interviewReview2;
	}

	public String getResumeFile() {
		return resumeFile;
	}

	public void setResumeFile(String resumeFile) {
		this.resumeFile = resumeFile;
	}

	public String getResumeUrl() {
		return resumeUrl;
	}

	public void setResumeUrl(String resumeUrl) {
		this.resumeUrl = resumeUrl;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	
	
	
	
	
	
	
	
	
	
}


