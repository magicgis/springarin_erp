package com.springrain.erp.modules.amazoninfo.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@Table(name = "amazoninfo_directory_comment")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ProductDirectoryComment {
	 private  	Integer 	id;           // 编号
	 private  	String  	title ;       // 产品title 
	 private  	String  	asin;      	  // 产品asin
	 private  	String  	country;      // 产品country 
	 private  	Integer 	star1 = 0;    // 1星
	 private 	Integer 	star2 = 0;    // 2星
	 private 	Integer 	star3 = 0;    // 3星
	 private 	Integer 	star4 = 0;    // 4星
	 private 	Integer 	star5 = 0;    // 5星
	 private 	Float 		star = 0f;    // 平均得分
	 private  	Date    	dataDate;     // 扫描时间
	 private    Integer     ranking;      // 排名
	 private    Float       salePrice;    // 售价
	 private    String      url;          // 目录url
	 private    Integer     directoryId;  // 目录id
	 private    String      image;        // 图片url
	 private    Date        dataDateEnd;  // 结束日期（搜索用）
	 private    String      brand;        // 品牌
	 private    Date        updateDate;   // 更新日期（top100，最新的更新日期应该相同）
	 private    Date        shelvesDate;   // 上架日期(评论最小日期);
	 private    String      isShield;      // 是否屏蔽  1：是
	 private    String      weekCompare;   // 星期比较
	 private    Float       saleCommRate; // 销售评论比(非字段)
	 private    Integer     comm30Days;   // 30天评论数(非字段)
	 private    String      displayColor;   // 百分比价格  颜色显示（非字段）
	 
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAsin() {
		return asin;
	}

	public void setAsin(String asin) {
		this.asin = asin;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Integer getStar1() {
		return star1;
	}

	public void setStar1(Integer star1) {
		this.star1 = star1;
	}

	public Integer getStar2() {
		return star2;
	}

	public void setStar2(Integer star2) {
		this.star2 = star2;
	}

	public Integer getStar3() {
		return star3;
	}

	public void setStar3(Integer star3) {
		this.star3 = star3;
	}

	public Integer getStar4() {
		return star4;
	}

	public void setStar4(Integer star4) {
		this.star4 = star4;
	}

	public Integer getStar5() {
		return star5;
	}

	public void setStar5(Integer star5) {
		this.star5 = star5;
	}

	public Float getStar() {
		return star;
	}

	public void setStar(Float star) {
		this.star = star;
	}


	public Date getDataDate() {
		return dataDate;
	}

	public void setDataDate(Date dataDate) {
		this.dataDate = dataDate;
	}

	public Integer getRanking() {
		return ranking;
	}

	public void setRanking(Integer ranking) {
		this.ranking = ranking;
	}

	public Float getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(Float salePrice) {
		this.salePrice = salePrice;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getDirectoryId() {
		return directoryId;
	}

	public void setDirectoryId(Integer directoryId) {
		this.directoryId = directoryId;
	}

	
	@Transient
	public Integer getComm30Days() {
		return comm30Days;
	}

	public void setComm30Days(Integer comm30Days) {
		this.comm30Days = comm30Days;
	}

	@Transient
	public Date getDataDateEnd() {
		return dataDateEnd;
	}

	public void setDataDateEnd(Date dataDateEnd) {
		this.dataDateEnd = dataDateEnd;
	}
	
	@Transient
	public Integer getAllStar(){
			return star1+star2+star3+star4+star5;
	}


	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	@Transient
	public Float getSaleCommRate() {
		return saleCommRate;
	}

	public void setSaleCommRate(Float saleCommRate) {
		this.saleCommRate = saleCommRate;
	}
	
	@Transient
	public String reviewLink(){
		String suff = country;
		if(suff!=null){
			if("uk,jp".contains(country)){
				 suff = "co."+suff;
			}else if("mx".equals(country)){
				suff = "com."+suff;
			}
			return "https://www.amazon."+suff+"/product-reviews/"+asin.replace("\n", "");
		}
		return "";
	}
	
	@Transient
	public String getReviewsLinkByStar(Integer num,String starType){
		String suffix = country;
		if("jp,uk".contains(suffix)){
			suffix = "co."+suffix;
		}else if ("mx".equals(suffix)){
			suffix = "com."+suffix;
		}
		String link = "https://www.amazon."
				+ suffix
				+ "/ss/customer-reviews/ajax/reviews/get/ref=cm_cr_pr_viewopt_sr?sortBy=recent&reviewerType=all_reviews&formatType=current_format&filterByStar="+starType+"&pageNumber="
				+num+"&pageSize=20&asin=" + asin;
		return link;
	}
	
	@Transient
	public String getProductLink(){
		String suff = country;
		if(suff!=null){
			if("uk,jp".contains(country)){
				 suff = "co."+suff;
			}else if("mx".equals(country)){
				suff = "com."+suff;
			}
			return "https://www.amazon."+suff+"/dp/"+asin.replace("\n", "");
		}
		return "";
	}
	
	@Transient
	public void countStar(){
		int count = (star1+star2+star3+star4+star5);
		if(count>0){
			float star =(star1*1+star2*2+star3*3+star4*4+star5*5)/((float)count);
			BigDecimal bd = new BigDecimal(star);
			bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
			this.star = bd.floatValue();
		}
	}
	
	@Transient
	public Integer getGoodComments(){
		return this.star4+this.star5;
	}
	
	
	@Transient
	public Float getGoodCommentsRate(){
		if(getAllStar()>0){
			float star =(star4+star5)*100/((float)getAllStar());
			BigDecimal bd = new BigDecimal(star);
			bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
			return  bd.floatValue();
		}else{   
			return 0f;
		}
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public Date getShelvesDate() {
		return shelvesDate;
	}

	public void setShelvesDate(Date shelvesDate) {
		this.shelvesDate = shelvesDate;
	}

	public String getIsShield() {
		return isShield;
	}

	public void setIsShield(String isShield) {
		this.isShield = isShield;
	}

	@Transient
	public String getWeekCompare() {
		return weekCompare;
	}

	public void setWeekCompare(String weekCompare) {
		this.weekCompare = weekCompare;
	}

	@Transient
	public String getDisplayColor() {
		return displayColor;
	}

	public void setDisplayColor(String displayColor) {
		this.displayColor = displayColor;
	}
	
	
	
	
	
}
