/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 营销计划Entity
 * @author Michael
 * @version 2017-06-12
 */
@Entity
@Table(name = "psi_marketing_plan")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiMarketingPlan implements Serializable {
	private static final long serialVersionUID = 1016455201323176330L;
	private 	Integer 	id; 				// 编号
	private 	String      countryCode;        // 国家编号
	private     String      startWeek;          // 开始周
	private     String      endWeek;	        // 结束周
	private     String      remark;             // 备注
	private     String      sta;                // 状态     0:新建   3：已审核   8：取消  5：广告暂停
	private     String      type;               // 类型     0:促销   1：广告
	private		Date      	updateDate;         // 最后更新日期
	private		User     	updateUser;         // 最后更新人
	private		Date     	reviewDate;         // 确认日期
	private		User     	reviewUser;         // 确认人
	private		Date      	cancelDate;         // 取消日期
	private		User        cancelUser;         // 取消人
	private		User      	createUser;         // 创建人
	private		Date     	createDate;         // 创建日期
	
	private     List<PsiMarketingPlanItem>  items=Lists.newArrayList() ;
	
	private     String      oldItemIds;         // (非数据库字段)
	private     String      productNameColor;   // (非数据库字段)
	private     String      warn;   			// (非数据库字段)
	private     Integer     promoQuantity;   	// (非数据库字段)
	private     Integer     realQuantity;   	// (非数据库字段)
	private     Integer     readyQuantity;   	// (非数据库字段)
	private     boolean     canCancel;			// (非数据库字段)
	
	public PsiMarketingPlan() {
		super();
	}

	public PsiMarketingPlan(Integer id){
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


	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getStartWeek() {
		return startWeek;
	}

	public void setStartWeek(String startWeek) {
		this.startWeek = startWeek;
	}

	public String getEndWeek() {
		return endWeek;
	}

	public void setEndWeek(String endWeek) {
		this.endWeek = endWeek;
	}


    @Transient
	public String getOldItemIds() {
		return oldItemIds;
	}

	public void setOldItemIds(String oldItemIds) {
		this.oldItemIds = oldItemIds;
	}

	@Transient
	public List<PsiMarketingPlan>  getTempPlans(){
		List<PsiMarketingPlan> tempItems = Lists.newArrayList();
		Map<String,PsiMarketingPlan>  map = Maps.newHashMap();
		for(PsiMarketingPlanItem item:items){
			String nameColor = item.getNameWithColor();
			PsiMarketingPlan plan = null;
			if(map.get(nameColor)==null){
				plan = new PsiMarketingPlan();
				plan.setId(id);
				plan.setSta(sta);
				plan.setType(type);
				plan.setRemark(remark);
				plan.setCreateDate(createDate);
				plan.setCreateUser(createUser);
				plan.setCountryCode(countryCode);
				plan.setStartWeek(startWeek);
				plan.setEndWeek(endWeek);
				plan.setWarn(item.getWarn());
				plan.setPromoQuantity(item.getPromoQuantity());
				plan.setRealQuantity(item.getRealQuantity());
				plan.setReadyQuantity(item.getReadyQuantity());
				plan.setProductNameColor(nameColor);
			}else{
				plan = map.get(nameColor);
			}
			map.put(nameColor, plan);
			plan.getItems().add(item);
		}
		
		tempItems.addAll(map.values());
		
		return tempItems;
	}
	
	@OneToMany(mappedBy = "marketingPlan", fetch = FetchType.EAGER)
	@Where(clause = "del_flag=0")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	@Cascade(CascadeType.ALL)
	public List<PsiMarketingPlanItem> getItems() {
		return items;
	}

	public void setItems(List<PsiMarketingPlanItem> items) {
		this.items = items;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
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

	public String getSta() {
		return sta;
	}

	public void setSta(String sta) {
		this.sta = sta;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

//	@Transient
//	public String getWarns(){
//		StringBuffer sb = new StringBuffer();
//		for(PsiMarketingPlanItem item :items){
//			if(StringUtils.isNotEmpty(item.getWarn())){
//				sb.append(item.getProductName()+":"+item.getWarn()+"<br/>");
//			}
//		}
//		return sb.toString();
//	}

	public PsiMarketingPlan(String countryCode, String remark, String sta,String type) {
		super();
		this.countryCode = countryCode;
		this.remark = remark;
		this.sta = sta;
		this.type = type;
	}

	
	@Transient
	public String getProductNameColor() {
		return productNameColor;
	}

	public void setProductNameColor(String productNameColor) {
		this.productNameColor = productNameColor;
	}

	@Transient
	public String getWarn() {
		return warn;
	}

	public void setWarn(String warn) {
		this.warn = warn;
	}

	@Transient
	public Integer getPromoQuantity() {
		return promoQuantity;
	}

	public void setPromoQuantity(Integer promoQuantity) {
		this.promoQuantity = promoQuantity;
	}

	@Transient
	public Integer getRealQuantity() {
		return realQuantity;
	}

	public void setRealQuantity(Integer realQuantity) {
		this.realQuantity = realQuantity;
	}
	
	
	
	@Transient
	public boolean getCanCancel() {
		boolean cancelFlag = true;
		//没备货之前都可以取消
		for(PsiMarketingPlanItem item:items){
			if(item.getReadyQuantity()!=null){
				cancelFlag=false;
				break;
			}
		}
		return cancelFlag;
	}

	public void setCanCancel(boolean canCancel) {
		this.canCancel = canCancel;
	}

	@Transient
	public Integer getReadyQuantity() {
		return readyQuantity;
	}

	public void setReadyQuantity(Integer readyQuantity) {
		this.readyQuantity = readyQuantity;
	}

		
	
}


