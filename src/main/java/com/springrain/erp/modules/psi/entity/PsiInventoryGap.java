package com.springrain.erp.modules.psi.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;


@Entity
@Table(name = "psi_inventory_gap")
public class PsiInventoryGap {
	private		Integer  	 id;
	private     Date         createDate;
	private     String       nameColor;
	private     String       forecastType;
	private     String       type;
	private     String       country;
	private     Integer       week1;
	private     Integer       week2;
	private     Integer       week3;
	private     Integer       week4;
	private     Integer       week5;
	private     Integer       week6;
	private     Integer       week7;
	private     Integer       week8;
	private     Integer       week9;
	private     Integer       week10;
	private     Integer       week11;
	private     Integer       week12;
	private     Integer       week13;
	private     Integer       week14;
	private     Integer       week15;
	private     Integer       week16;
	
	private     String        time;
	private     Integer        day;
	private     Integer        gap;
	
	private     String        desc;
	
	
	@Transient
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	@Transient
	public Integer getDay() {
		return day;
	}
	public void setDay(Integer day) {
		this.day = day;
	}
	@Transient
	public Integer getGap() {
		return gap;
	}
	public void setGap(Integer gap) {
		this.gap = gap;
	}
	
	@Transient
	public String getGapForecastType() {
		if("0".equals(forecastType)){
			return "销售预测";
		}else if("1".equals(forecastType)){
			return "周日销";
		}else if("2".equals(forecastType)){
			return "周日销(安)";
		}else if("3".equals(forecastType)){
			return "销售预测(安)";
		}
		return "";
	}
	
	
	@Transient
	public String getGapType() {//2.亚马逊仓 3.FBA在途 4.本地仓 5.本地运输6.CN 7.PO
		if("8".equals(type)){
			return "亚马逊仓balance";
		}else if("9".equals(type)){
			return "FBA在途balance";
		}else if("10".equals(type)){
			return "本地仓balance";
		}else if("11".equals(type)){
			return "在途balance";
		}else if("12".equals(type)){
			return "中国仓balance";
		}else if("13".equals(type)){
			return "PO balance";
		}
		return "";
	}
	
	@Transient
	public String getNextGapType() {
		if("8".equals(type)){//3
			return "FBA在途:";
		}else if("9".equals(type)){//4
			return "本地仓:";
		}else if("10".equals(type)){//5
			return "在途:";
		}else if("11".equals(type)){//6
			return "中国仓:";
		}else if("12".equals(type)){//7
			return "PO:";
		}else if("13".equals(type)){
			return "";
		}
		return "";
	}
	
	@Transient
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNameColor() {
		return nameColor;
	}

	public void setNameColor(String nameColor) {
		this.nameColor = nameColor;
	}

	public String getForecastType() {
		return forecastType;
	}

	public void setForecastType(String forecastType) {
		this.forecastType = forecastType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Integer getWeek1() {
		return week1;
	}

	public void setWeek1(Integer week1) {
		this.week1 = week1;
	}

	public Integer getWeek2() {
		return week2;
	}

	public void setWeek2(Integer week2) {
		this.week2 = week2;
	}

	public Integer getWeek3() {
		return week3;
	}

	public void setWeek3(Integer week3) {
		this.week3 = week3;
	}

	public Integer getWeek4() {
		return week4;
	}

	public void setWeek4(Integer week4) {
		this.week4 = week4;
	}

	public Integer getWeek5() {
		return week5;
	}

	public void setWeek5(Integer week5) {
		this.week5 = week5;
	}

	public Integer getWeek6() {
		return week6;
	}

	public void setWeek6(Integer week6) {
		this.week6 = week6;
	}

	public Integer getWeek7() {
		return week7;
	}

	public void setWeek7(Integer week7) {
		this.week7 = week7;
	}

	public Integer getWeek8() {
		return week8;
	}

	public void setWeek8(Integer week8) {
		this.week8 = week8;
	}

	public Integer getWeek9() {
		return week9;
	}

	public void setWeek9(Integer week9) {
		this.week9 = week9;
	}

	public Integer getWeek10() {
		return week10;
	}

	public void setWeek10(Integer week10) {
		this.week10 = week10;
	}

	public Integer getWeek11() {
		return week11;
	}

	public void setWeek11(Integer week11) {
		this.week11 = week11;
	}

	public Integer getWeek12() {
		return week12;
	}

	public void setWeek12(Integer week12) {
		this.week12 = week12;
	}

	public Integer getWeek13() {
		return week13;
	}

	public void setWeek13(Integer week13) {
		this.week13 = week13;
	}

	public Integer getWeek14() {
		return week14;
	}

	public void setWeek14(Integer week14) {
		this.week14 = week14;
	}

	public Integer getWeek15() {
		return week15;
	}

	public void setWeek15(Integer week15) {
		this.week15 = week15;
	}

	public Integer getWeek16() {
		return week16;
	}

	public void setWeek16(Integer week16) {
		this.week16 = week16;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	
}
