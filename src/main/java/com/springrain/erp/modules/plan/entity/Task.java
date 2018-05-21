package com.springrain.erp.modules.plan.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.common.persistence.IdEntity;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 任务管理Entity
 * @author tim
 * @version 2014-04-21
 */
@Entity
@Table(name = "plan_task")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Task extends IdEntity<Task> {
	
	private static final long serialVersionUID = 1L;
	
	private String subject; 	// 任务主题
	
	private String attchmentPath; 	// 附件路径
	
	private String flag;//是否成功发送信息 0：没有；1：有
	
	private Date startDate;// 开始日期
	
	private Date endDate;// 结束日期
	
	private String state;// 0:进行中 1：完成
	
	private List<User> performers; //执行人列表
	
	public Task() {
		super();
	}

	public Task(String id){
		this();
		this.id = id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getAttchmentPath() {
		return attchmentPath;
	}
	
	@Transient
	public String getRealName(){
		if(StringUtils.isNotEmpty(this.attchmentPath)){
			return attchmentPath.substring(attchmentPath.lastIndexOf("/")+1,attchmentPath.lastIndexOf("_"));
		}
		return attchmentPath;
	}
	
	@Transient
	public String getStateStr(){
		String rs = "进行中";
		if("0".equals(state)){
			if(endDate!=null && !new Date().before(endDate))
				rs = "已逾期";
		}else{
			rs = "已完成";
		}
		return rs;
	}
	
	
	@Transient
	public String getPerformer(){
		String rs = "";
		StringBuffer stringBuffer = new StringBuffer("");
		if(performers!=null){
			for (User performer : this.performers) {
				stringBuffer.append(performer.getName()+",");
			}
			if(performers.size()>0){
				rs = stringBuffer.toString();
				rs = rs.substring(0,rs.length()-1);
			}
		}
		return rs;
	}
	
	
	public void setAttchmentPath(String attchmentPath) {
		this.attchmentPath = attchmentPath;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "plan_task_user", joinColumns = {@JoinColumn(name = "task_id") }, inverseJoinColumns = { @JoinColumn(name = "user_id") })
	@Where(clause="del_flag='"+DEL_FLAG_NORMAL+"'")
	@OrderBy("id") @Fetch(FetchMode.SUBSELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JsonIgnore
	public List<User> getPerformers() {
		return performers;
	}

	public void setPerformers(List<User> performers) {
		this.performers = performers;
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


	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
}


