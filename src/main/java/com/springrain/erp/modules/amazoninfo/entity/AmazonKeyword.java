package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;


@Entity
@Table(name = "amazoninfo_keyword")
public class AmazonKeyword implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private Integer id; 		// 编号
	private String asin;
	private String title;
	private AmazonKeywordSearch search;
	private String delFlag;
	private Integer rank;
	
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

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	@ManyToOne()
	@JoinColumn(name="search_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public AmazonKeywordSearch getSearch() {
		return search;
	}

	public void setSearch(AmazonKeywordSearch search) {
		this.search = search;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}
	
}


