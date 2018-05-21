package com.springrain.erp.modules.plan.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.IdEntity;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 产品管理Entity
 * @author tim
 * @version 2014-04-02
 */
@Entity
@Table(name = "plan_product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Product extends IdEntity<Product> {
	
	private static final long serialVersionUID = 1L;
	private String name; 	// 新品型号
	private String model;
	
	private User masterBy; 	// 负责人
	private String imgPath; 	// 预览图路径
	private String supplier; 	// 供应商
	private String finish;//代表进行到了第几步
	private String period;
	
	private Date startDate;// 开始日期
	private Date endDate;// 结束日期
	private static Map<String,String> stepName;  
	
	static{
		stepName = Maps.newHashMap();
		stepName.put("0", "未开始");
		stepName.put("1", "正在\"意向产品确定\"");
		stepName.put("2", "正在\"产品功能优化\"");
		stepName.put("3", "正在\"产品外形优化\"");
		stepName.put("4", "正在\"供应商确定\"");
		stepName.put("5", "正在\"最终样品确定\"");
		stepName.put("6", "正在\"申请下单\"");
		stepName.put("7", "已完成");
	}	
	
	private List<ProductFlow> listFlow;//产品发布流程

	public Product() {
		super();
	}

	public Product(String id){
		this();
		this.id = id;
	}
	
	@Transient
	public String getFinishStr() {
		return stepName.get(finish);
	}
	
	@Length(min=1, max=200)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
	
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getMasterBy() {
		return masterBy;
	}

	public void setMasterBy(User masterBy) {
		this.masterBy = masterBy;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	@OneToMany(mappedBy = "product", fetch=FetchType.LAZY)
	@Where(clause="del_flag='"+DEL_FLAG_NORMAL+"'")
	@OrderBy(value="step") @Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	public List<ProductFlow> getListFlow() {
		return listFlow;
	}

	public void setListFlow(List<ProductFlow> listFlow) {
		this.listFlow = listFlow;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Field(index=Index.YES, analyze=Analyze.NO, store=Store.YES)
	@DateBridge(resolution = Resolution.DAY)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd")
	@Field(index=Index.YES, analyze=Analyze.NO, store=Store.YES)
	@DateBridge(resolution = Resolution.DAY)
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getFinish() {
		return finish;
	}

	public void setFinish(String finish) {
		this.finish = finish;
	}
}


