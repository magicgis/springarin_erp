/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity.lc;

import java.util.Date;
import java.util.List;


public class LcPsiPartsOrderBasisTotal {
	
	private 	String     purchaseOrderNo; 	// 配件名称
	private     Date       purchaseDate;        // 采购日期
	private     List<LcPsiPartsOrderBasis> items;
	
	public String getPurchaseOrderNo() {
		return purchaseOrderNo;
	}
	public void setPurchaseOrderNo(String purchaseOrderNo) {
		this.purchaseOrderNo = purchaseOrderNo;
	}
	
	public List<LcPsiPartsOrderBasis> getItems() {
		return items;
	}
	public void setItems(List<LcPsiPartsOrderBasis> items) {
		this.items = items;
	}
	
	public Date getPurchaseDate() {
		return purchaseDate;
	}
	public void setPurchaseDate(Date purchaseDate) {
		this.purchaseDate = purchaseDate;
	} 
	
	
	
	
}


