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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.springrain.erp.modules.sys.entity.User;

/**
 * 产品变更子项Entity
 */
@Entity
@Table(name = "psi_product_improvement_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiProductImprovementItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id; // 编号
	private Integer sort; // 处理序号 1:销售  2：采购  3：质量
	private String department;//提出处理意见的部门
	private String permission; // 处理需要的权限(结合sort便于调整顺序)
	private String content; // 处理意见
	private String delFlag;
	private User createBy; // 创建人
	private Date createDate; // 创建时间
	private PsiProductImprovement productImprovement;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@ManyToOne()
	@JoinColumn(name = "improvement_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiProductImprovement getProductImprovement() {
		return productImprovement;
	}

	public void setProductImprovement(PsiProductImprovement productImprovement) {
		this.productImprovement = productImprovement;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	@ManyToOne()
	@JoinColumn(name="create_by")
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

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

}
