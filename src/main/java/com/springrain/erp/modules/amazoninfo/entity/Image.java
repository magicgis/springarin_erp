/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * 图片修改Entity
 * @author tim
 * @version 2014-11-11
 */
@Entity
@Table(name = "amazoninfo_image")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Image implements Serializable{
	
private static final long serialVersionUID = 1L;
	
	private Integer id; // 编号
	
	private ImageFeed imageFeed;
	
	private String type;
	
	private String location;
	
	private String isDelete;
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
	@JoinColumn(name="feed_image_feed_id")
	public ImageFeed getImageFeed() {
		return imageFeed;
	}

	public void setImageFeed(ImageFeed imageFeed) {
		this.imageFeed = imageFeed;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getIsDelete() {
		return isDelete;
	}

	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}
}


