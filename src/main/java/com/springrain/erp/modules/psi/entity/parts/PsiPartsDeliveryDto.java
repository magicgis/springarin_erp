package com.springrain.erp.modules.psi.entity.parts;

/**
 * 配件订单详情Entity
 * @author Michael
 * @version 2015-06-02
 */
public class PsiPartsDeliveryDto {
	  
	private     Integer    		partsId;         	// 配件id
	private     String     		partsName;       	// 配件名字
	private     Integer         canLadingQuantity; 	// 订单数量
	private     Integer         partsOrderId;       // 配件订单id
	private     String          partsOrderNo;       // 配件订单No
	private     Integer         partsOrderItemId;   // 配件订单项id
	private     Float           price;              // 价格
	
	public PsiPartsDeliveryDto(){
		
	}
	
	public PsiPartsDeliveryDto(Integer partsId, String partsName,	Integer canLadingQuantity, Integer partsOrderId,String partsOrderNo, Integer partsOrderItemId,Float price) {
		super();
		this.partsId = partsId;
		this.partsName = partsName;
		this.canLadingQuantity = canLadingQuantity;
		this.partsOrderId = partsOrderId;
		this.partsOrderNo = partsOrderNo;
		this.partsOrderItemId = partsOrderItemId;
		this.price=price;
	}
	public Integer getPartsId() {
		return partsId;
	}
	public void setPartsId(Integer partsId) {
		this.partsId = partsId;
	}
	public String getPartsName() {
		return partsName;
	}
	public void setPartsName(String partsName) {
		this.partsName = partsName;
	}
	public Integer getCanLadingQuantity() {
		return canLadingQuantity;
	}
	public void setCanLadingQuantity(Integer canLadingQuantity) {
		this.canLadingQuantity = canLadingQuantity;
	}
	public Integer getPartsOrderId() {
		return partsOrderId;
	}
	public void setPartsOrderId(Integer partsOrderId) {
		this.partsOrderId = partsOrderId;
	}
	public String getPartsOrderNo() {
		return partsOrderNo;
	}
	public void setPartsOrderNo(String partsOrderNo) {
		this.partsOrderNo = partsOrderNo;
	}
	public Float getPrice() {
		return price;
	}


	public void setPrice(Float price) {
		this.price = price;
	}


	public Integer getPartsOrderItemId() {
		return partsOrderItemId;
	}
	public void setPartsOrderItemId(Integer partsOrderItemId) {
		this.partsOrderItemId = partsOrderItemId;

	}
}


