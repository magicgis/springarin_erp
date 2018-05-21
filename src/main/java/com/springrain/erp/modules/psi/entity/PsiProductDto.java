package com.springrain.erp.modules.psi.entity;
import java.math.BigDecimal;
public class PsiProductDto {
	private			 Integer 			productId;
	private			 String				productName;
	private 		 Integer            packQuantity;	//装箱个数
	private 		 BigDecimal 		boxVolume;  	//大箱体积
	private 		 BigDecimal 		gw;         	//毛重
	
	public PsiProductDto() {
	}
	
	public PsiProductDto(Integer productId,String productName,Integer packQuantity,BigDecimal boxVolume,BigDecimal gw) {
		super();
		this.productId = productId;
		this.productName=productName;
		this.packQuantity=packQuantity;
		this.boxVolume=boxVolume;  
		this.gw=gw;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getPackQuantity() {
		return packQuantity;
	}

	public void setPackQuantity(Integer packQuantity) {
		this.packQuantity = packQuantity;
	}

	public BigDecimal getBoxVolume() {
		return boxVolume;
	}

	public void setBoxVolume(BigDecimal boxVolume) {
		this.boxVolume = boxVolume;
	}

	public BigDecimal getGw() {
		return gw;
	}

	public void setGw(BigDecimal gw) {
		this.gw = gw;
	}
	
	
	
}
