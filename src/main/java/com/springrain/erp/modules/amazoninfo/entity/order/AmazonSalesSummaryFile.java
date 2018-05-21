package com.springrain.erp.modules.amazoninfo.entity.order;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 订单按月报表Entity
 */
@Entity
@Table(name = "amazoninfo_sales_summary_file")
public class AmazonSalesSummaryFile {

	private Integer id; // 编号
	private String month; // 月份
	private String platform; // 平台
	private String type; // 文件类型 1：xls(预留字段,可能需要增加其他格式文件)
	private String filePath; // 文件类型 1：xls(预留字段,可能需要增加其他格式文件)

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

}
