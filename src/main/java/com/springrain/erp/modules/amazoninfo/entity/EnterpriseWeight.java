package com.springrain.erp.modules.amazoninfo.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "amazoninfo_enterprise_weight")
public class EnterpriseWeight {
   private Integer id;
   private String country;
   private Float monday;
   private Float tuesday;
   private Float wednesday;
   private Float thursday;
   private Float friday;
   private Float saturday;
   private Float sunday;
   private String flag;
   
	
   public String getFlag() {
	   return flag;
   }
   public void setFlag(String flag) {
	this.flag = flag;
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
	public Float getMonday() {
		return monday;
	}
	public void setMonday(Float monday) {
		this.monday = monday;
	}
	public Float getTuesday() {
		return tuesday;
	}
	public void setTuesday(Float tuesday) {
		this.tuesday = tuesday;
	}
	public Float getWednesday() {
		return wednesday;
	}
	public void setWednesday(Float wednesday) {
		this.wednesday = wednesday;
	}
	public Float getThursday() {
		return thursday;
	}
	public void setThursday(Float thursday) {
		this.thursday = thursday;
	}
	public Float getFriday() {
		return friday;
	}
	public void setFriday(Float friday) {
		this.friday = friday;
	}
	public Float getSaturday() {
		return saturday;
	}
	public void setSaturday(Float saturday) {
		this.saturday = saturday;
	}
	public Float getSunday() {
		return sunday;
	}
	public void setSunday(Float sunday) {
		this.sunday = sunday;
	}
	   
   
   
}
