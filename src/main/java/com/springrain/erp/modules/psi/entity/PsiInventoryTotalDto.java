package com.springrain.erp.modules.psi.entity;

import java.util.Map;

import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.StringUtils;

public class PsiInventoryTotalDto {
	
	private String productName;
	
	private String color;
	/**
	 *如果是在途、在产，主键是国家code 
	 */
	private Map<String,PsiInventoryDto> inventorys =Maps.newHashMap();

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Integer getQuantity() {
		int rs = 0 ;
		if(inventorys!=null){
			for(PsiInventoryDto dto:inventorys.values()){
					rs+=dto.getQuantity();
			}
			
		}
		return rs;
	}
	
	public Integer getQuantityEuro() {
		int rs = 0 ;
		if(inventorys!=null){
			for(PsiInventoryDto dto : inventorys.values()) {
				if(dto.getCountry()!=null&&"fr,es,it,de,uk".contains(dto.getCountry())){
					rs+=dto.getQuantity();
				}
			}
		}
		return rs;
	}

	public String getProductNameWithColor() {
		if(StringUtils.isNotBlank(color)){
			return productName+"_"+color;
		}
		return productName;
	}

	public Map<String, PsiInventoryDto> getInventorys() {
		return inventorys;
	}

	public void setInventorys(Map<String, PsiInventoryDto> inventorys) {
		this.inventorys = inventorys;
	}

	public PsiInventoryTotalDto(String productName, String color,	Map<String, PsiInventoryDto> inventorys) {
		super();
		this.productName = productName;
		this.color = color;
		this.inventorys = inventorys;
	}

}
