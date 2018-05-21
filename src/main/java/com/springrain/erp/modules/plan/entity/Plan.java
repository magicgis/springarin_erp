package com.springrain.erp.modules.plan.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import com.springrain.erp.common.persistence.IdEntity;

/**
 * 日常工作计划Entity
 * @author tim
 * @version 2014-03-25
 */
@Entity
@DynamicUpdate @DynamicInsert
@Table(name = "plan_plan")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Plan extends IdEntity<Plan> {
	
	private static final long serialVersionUID = 1L;
	private String content; 	// 内容
	private String type; //类型   0: 日志   1:周计划  2:个人月计划   3:部门月计划
	private String performance;  //完成情况
	
	private String flag;//记录标记 y/m/d ; y/m/w ; y/m ; y/m/dep
	
	public Plan() {
		super();
	}

	public Plan(String id){
		this();
		this.id = id;
	}

	public String getContent() {
		/*if(StringUtils.isNotEmpty(content)){
			content = HtmlUtils.htmlEscape(content);
		}*/
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	@Length(min=1, max=1)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPerformance() {
		/*if(StringUtils.isNotEmpty(performance)){
			performance = HtmlUtils.htmlEscape(performance);
		}*/
		return performance;
	}

	public void setPerformance(String performance) {
		this.performance = performance;
	}
	
	@Length(min=0, max=10)
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
}


