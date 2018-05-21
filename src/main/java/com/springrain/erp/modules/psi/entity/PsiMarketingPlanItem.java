/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;

/**
 * 营销计划Entity
 * @author Michael
 * @version 2017-06-12
 */
@Entity
@Table(name = "psi_marketing_plan_item")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class PsiMarketingPlanItem implements Serializable {
	private static final long serialVersionUID = -7776851132195786606L;
	private 	Integer 			id; 				// 编号
	private 	String 				productName; 		// 产品名称
	private 	PsiProduct			product;            // 产品
	private 	String      		colorCode;          // 颜色编号
	private     Integer     		promoQuantity;      // 促销数(促销里面是总数，广告里面是日均数)
	private     Integer     		realQuantity;       // 实际数
	private     String      		warn;               // 预警信息记录(有促销实际没促销，有广告实际没广告)
	
	private     String      		delFlag="0";           
	private     Integer     		readyQuantity;  	//备货数 (如果是广告每次累加)
	private     String      		readyRemark;     	//预测备注 (如果是广告每次累加)
	private     PsiMarketingPlan 	marketingPlan ;
	
	public PsiMarketingPlanItem() {
		super();
	}

	public PsiMarketingPlanItem(Integer id){
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


	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	
	public Integer getReadyQuantity() {
		return readyQuantity;
	}

	public void setReadyQuantity(Integer readyQuantity) {
		this.readyQuantity = readyQuantity;
	}

	public String getReadyRemark() {
		return readyRemark;
	}

	public void setReadyRemark(String readyRemark) {
		this.readyRemark = readyRemark;
	}

	@ManyToOne()
	@JoinColumn(name="marketing_plan_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiMarketingPlan getMarketingPlan() {
		return marketingPlan;
	}

	public void setMarketingPlan(PsiMarketingPlan marketingPlan) {
		this.marketingPlan = marketingPlan;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getRealQuantity() {
		return realQuantity;
	}

	public void setRealQuantity(Integer realQuantity) {
		this.realQuantity = realQuantity;
	}

	@ManyToOne()
	@JoinColumn(name="product_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiProduct getProduct() {
		return product;
	}

	
	public void setProduct(PsiProduct product) {
		this.product = product;
	}

	public String getColorCode() {
		return colorCode;
	}

	public void setColorCode(String colorCode) {
		this.colorCode = colorCode;
	}

	public Integer getPromoQuantity() {
		return promoQuantity;
	}

	public void setPromoQuantity(Integer promoQuantity) {
		this.promoQuantity = promoQuantity;
	}

	@Transient
	public List<String> getColorList() {
		if(product!=null&&StringUtils.isNotBlank(product.getColor())){
			return Arrays.asList(product.getColor().split(","));
		}else{
			return Lists.newArrayList();
		}
	}
	
	
	@Transient
	public String getNameWithColor() {
		if(StringUtils.isNotBlank(colorCode)){
			return productName+"_"+colorCode;
		}
		return productName;
	}

	public String getWarn() {
		return warn;
	}

	public void setWarn(String warn) {
		this.warn = warn;
	}
	
	
	@Transient
	public String getReadyRemark2() {
		String remark = this.readyRemark;
		String regex = "(?<=\\[)(\\d+)(?=\\])";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(remark); // 获取 matcher 对象
        Set<String> set = Sets.newHashSet();
        while(m.find()){
        	set.add(m.group());
        }
        if(set.size()>0){
        	for(String ss :set){
        		remark=remark.replace("["+ss+"]", "<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/forecastOrder/view?id="+ss+"'>点击查看预测单</a>");
        	}
        }
		return remark;
	}
}


