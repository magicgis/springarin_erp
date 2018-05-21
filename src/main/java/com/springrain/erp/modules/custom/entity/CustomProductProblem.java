package com.springrain.erp.modules.custom.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springrain.erp.common.persistence.IdEntity;
import com.springrain.erp.common.utils.MessageUtils;
import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "custom_product_problem")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class CustomProductProblem  implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	private CustomProductProblemKey pKey;	//联合主键
	
	private 	String 		country;
	private     String      productName;
	private     String      problemType;
	private     String      problem;
	private     String      orderNos;
	private     Date        createDate;
	private     Date        dataDate;
	private String revertEmail;

	
	public CustomProductProblemKey getpKey() {
		return pKey;
	}

	public void setpKey(CustomProductProblemKey pKey) {
		this.pKey = pKey;
	}
	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProblemType() {
		return problemType;
	}

	public void setProblemType(String problemType) {
		this.problemType = problemType;
	}

	public String getProblem() {
		return problem;
	}

	public void setProblem(String problem) {
		this.problem = problem;
	}

	public String getOrderNos() {
		return orderNos;
	}

	public void setOrderNos(String orderNos) {
		this.orderNos = orderNos;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getDataDate() {
		return dataDate;
	}

	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}
	
	public String getRevertEmail() {
		return revertEmail;
	}
	public void setRevertEmail(String revertEmail) {
		this.revertEmail = revertEmail;
	}
}