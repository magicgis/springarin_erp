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
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 新品采购计划明细
 * @author Michael
 */
@Entity
@Table(name = "psi_purchase_plan")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PurchasePlan implements Serializable{
	private static final long serialVersionUID = -1865080117676585411L;
	private		 Integer   		 id; 		          	 // id
	private 	 String	         planSta;            	 // 订单状态  1:草稿；2：已申请审核；3：已初级审核；4：已终极审核 5：部分生成订单；6：已完成；8：已取消
	private		 User      		 createUser;             // 创建人
	private		 Date     		 createDate;             // 创建日期
	
	private 	 String	         remark; 
	private		 Date     		 reviewDate;            
	private		 User     		 reviewUser;            
	private		 Date     		 bossReviewDate;         
	private		 User     		 bossReviewUser;          
	private		 Date      		 cancelDate;            
	private		 User            cancelUser;    
	private      String          productPosition;   	  //爆款、走量款、利润款、品类补充款  产品定位
	private		 String   		 attFilePath;             // 文件路径
	private      MultipartFile   attFile;                 //（非数据库字段）
	private		 String    		 oldItemIds ;             // 老items
	
	private 	 List<PurchasePlanItem> items = Lists.newArrayList();
	
	public PurchasePlan() {
		super();
	}

	public PurchasePlan(Integer id){
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

	public String getPlanSta() {
		return planSta;
	}

	public void setPlanSta(String planSta) {
		this.planSta = planSta;
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

	public Date getBossReviewDate() {
		return bossReviewDate;
	}

	public void setBossReviewDate(Date bossReviewDate) {
		this.bossReviewDate = bossReviewDate;
	}

	@ManyToOne()
	@JoinColumn(name="boss_review_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getBossReviewUser() {
		return bossReviewUser;
	}

	public void setBossReviewUser(User bossReviewUser) {
		this.bossReviewUser = bossReviewUser;
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

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	
	@OneToMany(mappedBy = "plan",fetch=FetchType.EAGER)
//	@OrderBy(value="product,colorCode,countryCode")
	@Where(clause="del_flag=0")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public List<PurchasePlanItem> getItems() {
		return items;
	}

	public void setItems(List<PurchasePlanItem> items) {
		this.items = items;
	}

	@Transient
	public String getOldItemIds() {
		return oldItemIds;
	}

	public void setOldItemIds(String oldItemIds) {
		this.oldItemIds = oldItemIds;
	}
	
	
	@Transient
	public Integer getItemsQuantity() {
		int rs = 0;
		for (PurchasePlanItem item : items) {
			rs+=item.getQuantity();
		}
		return rs;
	}
	
	@Transient
	public List<PurchasePlan> getTempPlans(){
		Map<String,PurchasePlan> map = Maps.newLinkedHashMap();
		for (PurchasePlanItem item : items) {
			PurchasePlan temp=map.get(item.getProductNameColor());
			if(temp==null){
				temp=new PurchasePlan();
				temp.setId(id);
				temp.setCreateDate(createDate);
				temp.setCreateUser(createUser);
				temp.setRemark(remark);
				temp.setPlanSta(planSta);
				map.put(item.getProductNameColor(), temp);
			}
			temp.getItems().add(item);
		}
		List<PurchasePlan> plans = Lists.newArrayList();
		plans.addAll(map.values());
		return plans;
	}
	
	@Transient
	public String getTempProductName(){
		String rs ="";
		for(PurchasePlanItem item :items){
			rs=item.getProductNameColor();
			break;
		}
		return rs;
	}

	@Transient
	public String getProductName(){
		String rs ="";
		for(PurchasePlanItem item :items){
			rs=item.getProductName();
			break;
		}
		return rs;
	}
	
	@Transient
	public String getSupplierName(){
		List<ProductSupplier> productSuppliers =items.get(0).getProduct().getPsiSuppliers();
		String supplierName="";
		for(ProductSupplier proSupplier: productSuppliers){
			supplierName=proSupplier.getSupplier().getNikename();
			break;
		}
		return supplierName;
	}

	public String getAttFilePath() {
		return attFilePath;
	}

	public void setAttFilePath(String attFilePath) {
		this.attFilePath = attFilePath;
	}

	@Transient
	public MultipartFile getAttFile() {
		return attFile;
	}

	public void setAttFile(MultipartFile attFile) {
		this.attFile = attFile;
	}

	public String getProductPosition() {
		return productPosition;
	}

	public void setProductPosition(String productPosition) {
		this.productPosition = productPosition;
	}
	
}


