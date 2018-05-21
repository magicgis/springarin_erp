/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrderItem;
import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "psi_vat_invoice_use_info")
public class PsiVatInvoiceUseInfo {  
	
	private	    Integer      		id; 		  	 // id
	private     User         		createUser;      // 创建人
	private     Date         		createDate;      // 创建时间
	private     Integer             quantity;
	private     String              productName;
	private     String              countryCode;
	private     PsiVatInvoiceInfo   invoice;
	private     LcPsiTransportOrderItem   item;
	private     String              invoiceNo;
	private     String              delFlag;         // 删除标记
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	

	@ManyToOne()
	@JoinColumn(name="create_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	
	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	@ManyToOne()
	@JoinColumn(name="invoice_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public PsiVatInvoiceInfo getInvoice() {
		return invoice;
	}

	public void setInvoice(PsiVatInvoiceInfo invoice) {
		this.invoice = invoice;
	}

	@ManyToOne()
	@JoinColumn(name="order_item_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public LcPsiTransportOrderItem getItem() {
		return item;
	}

	public void setItem(LcPsiTransportOrderItem item) {
		this.item = item;
	}

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

}


