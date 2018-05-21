/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
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
import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 预测订单Entity
 * @author Michael
 * @version 2016-02-26
 */
@Entity
@Table(name = "psi_forecast_order")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class ForecastOrder implements Serializable{
	private static final long serialVersionUID = 1L;
	private		 Integer   		 id; 		          	 // id
	private 	 String	         orderSta;            	 // 订单状态  1:草稿    3：已初级审批 4：已高级审批  5：已审批；8：已取消
	private      String          remark;                 // 备注
	private		 Date     		 createDate;             // 创建日期
	private		 User     		 createUser;             // 创建人
	private		 Date     		 updateDate;             // 最后更新日期
	private		 User     		 updateUser;             // 最后更新人
	private		 Date     		 reviewDate;             // 审核日期
	private		 User     		 reviewUser;             // 审核人
	private		 Date      		 cancelDate;             // 取消日期
	private		 User            cancelUser;             // 取消人
	private		 Date     		 targetDate;             // 备货目标日期(特殊情况需提前备货)
	private		 String     	 type;             		 // 1:新品订单 0:非新品订单(新品由运营负责,非新品由供应链负责)
	private 	 List<ForecastOrderItem> items = Lists.newArrayList();
	
	public ForecastOrder() {
		super();
	}

	public ForecastOrder(Integer id){
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

	
	@OneToMany(mappedBy = "forecastOrder")
	//@Where(clause="by_week!='8'||by_week IS NULL") 
	@Where(clause="display_sta!='1'")  
	@OrderBy(value="productName,colorCode")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public List<ForecastOrderItem> getItems() {
		return items;
	}

	public void setItems(List<ForecastOrderItem> items) {
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
	
	public Date getTargetDate() {
		return targetDate;
	}

	public void setTargetDate(Date targetDate) {
		this.targetDate = targetDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Transient
	public Map<String,List<ForecastOrderItem>> getProductMap(){
		Map<String,List<ForecastOrderItem>> resMap = Maps.newHashMap();
		Map<String,String>  temMap =Maps.newTreeMap();
		for(ForecastOrderItem item:items){
			String productName = item.getProductNameColor();
			List<ForecastOrderItem> tempItems = null;
			if(resMap.get(productName)==null){
				tempItems = Lists.newArrayList();
			}else{
				tempItems = resMap.get(productName);
			}
			tempItems.add(item);
			resMap.put(productName, tempItems);
			if(temMap.get(productName)==null){
				String type="2";
				if("2".equals(item.getBy31sales())||"3".equals(item.getBy31sales())){
					if("3".equals(item.getByWeek())){
					//2，3状态并且用了第四周的数据
						type="0";
					}else{
						type="1";
					}
				}
				temMap.put(productName, type);
			}
		}
		List<String>  oneList = Lists.newArrayList();
		List<String>  twoList = Lists.newArrayList();
		List<String>  threeList = Lists.newArrayList();
		for(Map.Entry<String, String> entry:temMap.entrySet()){
			String productName = entry.getKey();
			if("0".equals(temMap.get(productName))){
				oneList.add(productName);
			}else if("1".equals(temMap.get(productName))){
				twoList.add(productName);
			}else if("2".equals(temMap.get(productName))){
				threeList.add(productName);
			}
		}
		//添加排序
		Map<String,List<ForecastOrderItem>> resMap1 = Maps.newLinkedHashMap();
		if(oneList.size()>0){
			for(String productName:oneList){
				resMap1.put(productName, resMap.get(productName));
			}
		}
		if(twoList.size()>0){
			for(String productName:twoList){
				resMap1.put(productName, resMap.get(productName));
			}		
		}
		if(threeList.size()>0){
			for(String productName:threeList){
				resMap1.put(productName, resMap.get(productName));
			}
		}
		
		return resMap1;
	}
	
	/**
	 * 
	 *不分国家的下单总量 
	 * 
	 */
	@Transient
	public Map<String,Integer> getProductTotalMap(){
		Map<String,Integer> resMap = Maps.newTreeMap();
		for(ForecastOrderItem item:items){
			String productName = item.getProductNameColor();
			Integer total =item.getQuantity();
			if(resMap.get(productName)!=null){
				total+=resMap.get(productName);
			}
			resMap.put(productName, total);
		}
		return resMap;
	}
	
	/**
	 * 
	 *不分国家的下单总量 
	 * 
	 */
	@Transient
	public Map<String,Integer> getProductNoPromoTotalMap(){
		Map<String,Integer> resMap = Maps.newTreeMap();
		for(ForecastOrderItem item:items){
			String productName = item.getProductNameColor();
			Integer total =item.getQuantity()+item.getTotalStock();//本次下单(排除促销)+当时的库存总量
			if(resMap.get(productName)!=null){
				total+=resMap.get(productName);
			}
			resMap.put(productName, total);
		}
		return resMap;
	}
	/**
	 * 
	 *不分国家的31日销
	 * 
	 */
	@Transient
	public Map<String,Integer> getProduct31SaleMap(){
		Map<String,Integer> resMap = Maps.newTreeMap();
		for(ForecastOrderItem item:items){
			String productName = item.getProductNameColor();
			Integer total31 =item.getDay31sales();
			if(resMap.get(productName)!=null){
				total31+=resMap.get(productName);
			}
			resMap.put(productName, total31);
		}
		return resMap;
	}
	

	@Transient
	public Map<String,List<ForecastOrderItem>> getBossProductMap(){
		Map<String,List<ForecastOrderItem>> resMap = Maps.newTreeMap();
		Map<String,String>  temMap =Maps.newHashMap();
		for(ForecastOrderItem item:items){
			Integer compInteger =null;//新增的为空
			if("0".equals(item.getByWeek())){
				compInteger=item.getForecast1week();
			}else if("1".equals(item.getByWeek())){
				compInteger=item.getForecast2week();
			}else if("2".equals(item.getByWeek())){
				compInteger=item.getForecast3week();
			}else if("3".equals(item.getByWeek())){
				compInteger=item.getForecast4week();
			}
			if(!item.getQuantity().equals(compInteger)){
				String productName = item.getProduct().getName();
				List<ForecastOrderItem> tempItems = null;
				if(resMap.get(productName)==null){
					tempItems = Lists.newArrayList();
				}else{
					tempItems = resMap.get(productName);
				}
				tempItems.add(item);
				resMap.put(productName, tempItems);
				
				if(temMap.get(productName)==null){
					String type="2";
					if("2".equals(item.getBy31sales())||"3".equals(item.getBy31sales())){
						if("3".equals(item.getByWeek())){
						//2，3状态并且用了第四周的数据
							type="0";
						}else{
							type="1";
						}
					}
					temMap.put(productName, type);
				}
			}
		}
		List<String>  oneList = Lists.newArrayList();
		List<String>  twoList = Lists.newArrayList();
		List<String>  threeList = Lists.newArrayList();
		for(Map.Entry<String, String> entry:temMap.entrySet()){
			String productName = entry.getKey();
			if("0".equals(temMap.get(productName))){
				oneList.add(productName);
			}else if("1".equals(temMap.get(productName))){
				twoList.add(productName);
			}else if("2".equals(temMap.get(productName))){
				threeList.add(productName);
			}
		}
		//添加排序
		Map<String,List<ForecastOrderItem>> resMap1 = Maps.newLinkedHashMap();
		if(oneList.size()>0){
			for(String productName:oneList){
				resMap1.put(productName, resMap.get(productName));
			}
		}
		if(twoList.size()>0){
			for(String productName:twoList){
				resMap1.put(productName, resMap.get(productName));
			}		
		}
		if(threeList.size()>0){
			for(String productName:threeList){
				resMap1.put(productName, resMap.get(productName));
			}
		}
		
		return resMap1;
	}
	
	@Transient
	public Map<String,Integer> getBossProductTotalMap(){
		Map<String,Integer> resMap = Maps.newTreeMap();
		for(ForecastOrderItem item:items){
			String productName = item.getProduct().getName();
			Integer compInteger =null;
			if("0".equals(item.getByWeek())){
				compInteger=item.getForecast1week();
			}else if("1".equals(item.getByWeek())){
				compInteger=item.getForecast2week();
			}else if("2".equals(item.getByWeek())){
				compInteger=item.getForecast3week();
			}else if("3".equals(item.getByWeek())){
				compInteger=item.getForecast4week();
			}
			if(!item.getQuantity().equals(compInteger)){
				Integer total =item.getQuantity();
				if(item.getPromotionQuantity()!=null){
					total+=item.getPromotionQuantity();
				}
				if(resMap.get(productName)!=null){
					total+=resMap.get(productName);
				}
				resMap.put(productName, total);
			}
		}
		return resMap;
	}
	
	@Transient
	public List<ForecastOrderItem> getBossItems(){
		List<ForecastOrderItem> resList = Lists.newArrayList();
		for(ForecastOrderItem item:items){
			Integer compInteger =null;
			if("0".equals(item.getByWeek())){
				compInteger=item.getForecast1week();
			}else if("1".equals(item.getByWeek())){
				compInteger=item.getForecast2week();
			}else if("2".equals(item.getByWeek())){
				compInteger=item.getForecast3week();
			}else if("3".equals(item.getByWeek())){
				compInteger=item.getForecast4week();
			}
			if(item.getQuantity().equals(compInteger)){
				resList.add(item);
			}
		}
		return resList;
	}
	
	
	@Transient
	public Integer getForecastTotal(){
		Integer forecastTotal =0;
		for(ForecastOrderItem item:items){//不显示新增的
			if(StringUtils.isNotEmpty(item.getByWeek())&&!"2".equals(item.getDisplaySta())){
				Integer quantity=0;
				if("0".equals(item.getByWeek())){
					quantity=item.getForecast1week();
				}else if("1".equals(item.getByWeek())){
					quantity=item.getForecast2week();
				}else if("2".equals(item.getByWeek())){
					quantity=item.getForecast3week();
				}else if("3".equals(item.getByWeek())){
					quantity=item.getForecast4week();
				}
				forecastTotal+=quantity;
			}
		}
		return forecastTotal;
	}
	
	@Transient
	public Integer getOrderTotal(){
		Integer orderTotal =0;
		for(ForecastOrderItem item:items){
			orderTotal+=item.getQuantity();
		}
		return orderTotal;
	}
	
}


