/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
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
import com.springrain.erp.modules.sys.entity.User;


@Entity
@Table(name = "psi_forecast_transport_order")
public class PsiTransportForecastOrder implements Serializable{
	private static final long serialVersionUID = 1L;
	private		 Integer   		 id; 		          	 // id
	private 	 String	         orderSta;            	 // 订单状态  1:草稿 3：已生成运单 5：已审批；8：已取消
	private      String          remark;                 // 备注
	private		 Date     		 createDate;             // 创建日期
	private		 User     		 createUser;             // 创建人
	private		 Date     		 updateDate;             // 最后更新日期
	private		 User     		 updateUser;             // 最后更新人
	private		 Date     		 reviewDate;             // 审核日期
	private		 User     		 reviewUser;             // 审核人
	private		 Date      		 cancelDate;             // 取消日期
	private		 User            cancelUser;             // 取消人
	
	private 	 List<PsiTransportForecastOrderItem> items = Lists.newArrayList();
	
	public PsiTransportForecastOrder() {
		super();
	}

	public PsiTransportForecastOrder(Integer id){
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
	
	
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getOrderSta() {
		return orderSta;
	}

	public void setOrderSta(String orderSta) {
		this.orderSta = orderSta;
	}


	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
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

	public Date getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
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

	
	@OneToMany(mappedBy = "psiTransportForecastOrder")
	@Where(clause="display_sta!='1'")  
	@OrderBy(value="productName,colorCode")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public List<PsiTransportForecastOrderItem> getItems() {
		return items;
	}

	public void setItems(List<PsiTransportForecastOrderItem> items) {
		this.items = items;
	}

	

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
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
	
	
}


