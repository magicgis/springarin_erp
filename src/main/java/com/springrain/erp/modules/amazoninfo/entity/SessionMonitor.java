package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.modules.sys.entity.User;

/**
 * session和转化率监控Entity
 * @author Tim
 * @version 2015-02-09
 */
@Entity
@Table(name = "amazoninfo_session_monitor")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SessionMonitor implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private String country; 
	private Date month;
	
	private Integer productId; 
	private String productName;
	private String color;
	
	private Integer sessions;
	private Integer sessionsByDate;
	
	private Float conver;
	
	private User createUser;
	private Date createDate;
	private User lastUpdateUser;
	private Date lastUpdateDate; 
	
	private String searchFlag ="0";          //0：按日期       1： 按星期       2： 按月份统计
	
	public SessionMonitor() {
		super();
	}

	public SessionMonitor(Integer sessions, Integer sessionsByDate,
			Float conver) {
		super();
		this.sessions = sessions;
		this.sessionsByDate = sessionsByDate;
		this.conver = conver;
	}
	
	public SessionMonitor(SessionMonitor sessionMonitor) {
		super();
		this.sessions = sessionMonitor.getSessions();
		this.sessionsByDate = sessionMonitor.getSessionsByDate();
		this.conver = sessionMonitor.getConver();
		this.color = sessionMonitor.getColor();
		this.country = sessionMonitor.getCountry();
		this.productName = sessionMonitor.getProductName();
		this.productId = sessionMonitor.getProductId();
	}
	
	//sessionsByDate 值记录的orderPlace
	public SessionMonitor(Integer sessions,Integer sessionsByDate) {
		super();
		this.sessions = sessions;
		this.sessionsByDate = sessionsByDate;
	}

	public SessionMonitor(Integer id){
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

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}
	
	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getMonth() {
		return month;
	}

	public void setMonth(Date month) {
		this.month = month;
	}
	
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getSessions() {
		return sessions;
	}

	public void setSessions(Integer sessions) {
		this.sessions = sessions;
	}

	public Float getConver() {
		return conver;
	}

	public void setConver(Float conver) {
		this.conver = conver;
	}

	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}
	
	@Column(updatable=false)
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getLastUpdateUser() {
		return lastUpdateUser;
	}

	public void setLastUpdateUser(User lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	public Integer getSessionsByDate() {
		return sessionsByDate;
	}

	public void setSessionsByDate(Integer sessionsByDate) {
		this.sessionsByDate = sessionsByDate;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
	
	@Transient
	public String getSearchFlag() {
		return searchFlag;
	}
	public void setSearchFlag(String searchFlag) {
		this.searchFlag = searchFlag;
	}
	
	@Transient
	public String getTip(){
		double conver = 0d;
		if(sessions==null){
			sessions = 0 ;
		}
		if(sessions>0){
			conver = sessionsByDate*100/(double)sessions;
			BigDecimal  temp =   new  BigDecimal(conver);
			conver =  temp.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
		}
		return "Sessions:"+sessions+";Conversion:"+conver+"%";
	}
	
	@Override
	public String toString() {
		String flag = "";
		if(sessions!=null){
			flag = "session";
		}else{
			flag = "conversion";
		}
		return productName+","+flag;
	}
}


