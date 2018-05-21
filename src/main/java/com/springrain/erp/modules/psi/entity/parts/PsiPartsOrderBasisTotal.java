/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.parts;

import java.util.Date;
import java.util.List;


public class PsiPartsOrderBasisTotal {
	
	private 	String     purchaseOrderNo; 	// 配件名称
	private     Date       purchaseDate;        // 采购日期
	private     List<PsiPartsOrderBasis> items;
	
	
	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}
	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}
	
	public List<PsiPartsOrderBasis> getItems() {
		return items;
	}
	public void setItems(List<PsiPartsOrderBasis> items) {
		this.items = items;
	}
	
	public Date getPurchaseDate() {
		return purchaseDate;
	}
	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	} 
	
	
	
	
}


