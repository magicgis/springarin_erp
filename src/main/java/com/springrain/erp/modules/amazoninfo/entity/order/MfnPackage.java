package com.springrain.erp.modules.amazoninfo.entity.order;

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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "amazoninfo_ebay_package")
public class MfnPackage {
	private Integer id;
	private String packageNo;
	private Date printTime;
	private String status;
	private String remark;
	private List<MfnOrder> orders = Lists.newArrayList();
	private User printUser;
	
	private Date start;
	private  String country;
	

	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	@ManyToOne()
	@JoinColumn(name = "print_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getPrintUser() {
		return printUser;
	}
	public void setPrintUser(User printUser) {
		this.printUser = printUser;
	}
	@Transient
    public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@OneToMany(mappedBy = "mfnPackage",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL})
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.NONE)
	public List<MfnOrder> getOrders() {
		return orders;
	}
	public void setOrders(List<MfnOrder> orders) {
		this.orders = orders;
	}
	public String getPackageNo() {
		return packageNo;
	}
	public void setPackageNo(String packageNo) {
		this.packageNo = packageNo;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getPrintTime() {
		return printTime;
	}
	public void setPrintTime(Date printTime) {
		this.printTime = printTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Transient
	public String getStateStr() {
		if("1".equals(status)){
			return "waiting for the feedback results...";
		}else if("2".equals(status)){
			return "order status change success";
		}else if("3".equals(status)){
			return "order status change failed,maybe amazon or ebay service is not available or the network reason, please try again later";
		}
		return status;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
   
}
