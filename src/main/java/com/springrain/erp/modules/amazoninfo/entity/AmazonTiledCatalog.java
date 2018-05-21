package com.springrain.erp.modules.amazoninfo.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "amazoninfo_tiled_catalog")
public class AmazonTiledCatalog {
	private Integer id;
	private String catalogName;
    private String country;
    private String pathId1;
    private String pathId2;
    private String pathId3;
    private String pathId4;
    private String pathId5;
    private String pathId6;
    private String pathId7;
    private String pathId8;
    private String isUse;
    
    
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	
	
	public String getCatalogName() {
		return catalogName;
	}
	public void setCatalogName(String catalogName) {
		this.catalogName = catalogName;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPathId1() {
		return pathId1;
	}
	public void setPathId1(String pathId1) {
		this.pathId1 = pathId1;
	}
	public String getPathId2() {
		return pathId2;
	}
	public void setPathId2(String pathId2) {
		this.pathId2 = pathId2;
	}
	public String getPathId3() {
		return pathId3;
	}
	public void setPathId3(String pathId3) {
		this.pathId3 = pathId3;
	}
	public String getPathId4() {
		return pathId4;
	}
	public void setPathId4(String pathId4) {
		this.pathId4 = pathId4;
	}
	public String getPathId5() {
		return pathId5;
	}
	public void setPathId5(String pathId5) {
		this.pathId5 = pathId5;
	}
	public String getPathId6() {
		return pathId6;
	}
	public void setPathId6(String pathId6) {
		this.pathId6 = pathId6;
	}
	public String getPathId7() {
		return pathId7;
	}
	public void setPathId7(String pathId7) {
		this.pathId7 = pathId7;
	}
	public String getPathId8() {
		return pathId8;
	}
	public void setPathId8(String pathId8) {
		this.pathId8 = pathId8;
	}
	
	public String getIsUse() {
		return isUse;
	}
	public void setIsUse(String isUse) {
		this.isUse = isUse;
	}
	@Transient
	public void setPathId(int size,String pathId) {
		if(size==1){
			setPathId1(pathId);
		}else if(size==2){
			setPathId2(pathId);
		}else if(size==3){
			setPathId3(pathId);
		}else if(size==4){
			setPathId4(pathId);
		}else if(size==5){
			setPathId5(pathId);
		}else if(size==6){
			setPathId6(pathId);
		}else if(size==7){
			setPathId7(pathId);
		}else if(size==8){
			setPathId8(pathId);
		}
	}
	
	
}


