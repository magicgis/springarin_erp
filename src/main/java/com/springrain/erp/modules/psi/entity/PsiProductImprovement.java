package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;
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

import com.google.common.collect.Lists;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 产品变更Entity
 */
@Entity
@Table(name = "psi_product_improvement")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiProductImprovement implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id; // 编号
	private String line;	//产品线
	private String productName;	//产品名
	private String reason;	//变更原因
	private String perRemark;	//变更前说明
	private String afterRemark;	//变更后说明
	private String orderNo;	//变更起始订单号
	private Date improveDate;	//变更时间
	private String filePath;	//变更涉及附件(多附件压缩zip)
	private String type;	//紧急程度 1：普通 2：紧急 3：特急
	private String status;	//状态   0:新建  1：采集意见中 2：待审批  3：审批通过 4：取消
	private Date updateDate; // 最后更新日期
	private User updateBy; // 最后更新人
	private User createBy; // 创建人
	private Date createDate; // 创建日期
	private Date approvalDate; // 审批日期
	private User approvalBy; // 审批人
	private String approvalContent; //审批意见
	private String permission;//当前阶段编辑处理意见需要的权限
	private String delFlag;
	
	private List<PsiProductImprovementItem>  items=Lists.newArrayList() ;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@OneToMany(mappedBy = "productImprovement", fetch = FetchType.EAGER)
	@Where(clause = "del_flag=0")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	@Cascade(CascadeType.ALL)
	@OrderBy(value="sort")
	public List<PsiProductImprovementItem> getItems() {
		return items;
	}

	public void setItems(List<PsiProductImprovementItem> items) {
		this.items = items;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getPerRemark() {
		return perRemark;
	}

	public void setPerRemark(String perRemark) {
		this.perRemark = perRemark;
	}

	public String getAfterRemark() {
		return afterRemark;
	}

	public void setAfterRemark(String afterRemark) {
		this.afterRemark = afterRemark;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Date getImproveDate() {
		return improveDate;
	}

	public void setImproveDate(Date improveDate) {
		this.improveDate = improveDate;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	@ManyToOne()
	@JoinColumn(name="update_by")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(User updateBy) {
		this.updateBy = updateBy;
	}

	public Date getApprovalDate() {
		return approvalDate;
	}

	public void setApprovalDate(Date approvalDate) {
		this.approvalDate = approvalDate;
	}


	@ManyToOne()
	@JoinColumn(name="approval_by")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getApprovalBy() {
		return approvalBy;
	}

	public void setApprovalBy(User approvalBy) {
		this.approvalBy = approvalBy;
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

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	public String getApprovalContent() {
		return approvalContent;
	}

	public void setApprovalContent(String approvalContent) {
		this.approvalContent = approvalContent;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	@Transient
	public List<String> getProductNames() {
		List<String> list = Lists.newArrayList();
		if (StringUtils.isNotEmpty(productName)) {
			for (String name : productName.split(",")) {
				list.add(name);
			}
		}
		return list;
	}
	
}


