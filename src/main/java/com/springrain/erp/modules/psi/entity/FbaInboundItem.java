package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.StringUtils;

/**
 * FBA帖子项Entity
 * @author Tim
 * @version 2015-01-29
 */
@Entity
@Table(name = "psi_fba_inbound_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class FbaInboundItem implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	
	private String sku;

    private String fnSku;
    
    private Integer sellerShipped;
    
    private String remark;

    private Integer quantityShipped;

    private Integer quantityReceived;

    private Integer quantityInCase;
    
    private String pageDelFlag;
	
    
    private FbaInbound fbaInbound;
    
    
    private String stockCode;
    
    private Integer cartonNo;
    private String description;
    private String problem;	//错误记录
    
    private String flag;//0:未统计
    private Integer packQuantity;
    
    private Integer oldPackQuantity;
    
    
    
    @Transient
    public Integer getOldPackQuantity() {
		return oldPackQuantity;
	}
	public void setOldPackQuantity(Integer oldPackQuantity) {
		this.oldPackQuantity = oldPackQuantity;
	}
    
	@Transient
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public Integer getCartonNo() {
		return cartonNo;
	}

	public void setCartonNo(Integer cartonNo) {
		this.cartonNo = cartonNo;
	}

	
	public FbaInboundItem() {
		super();
	}

	public FbaInboundItem(Integer id){
		this();
		this.id = id;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}
	
	public String getFnSku() {
		return fnSku;
	}

	public void setFnSku(String fnSku) {
		this.fnSku = fnSku;
	}

	public Integer getQuantityShipped() {
		return quantityShipped;
	}

	public void setQuantityShipped(Integer quantityShipped) {
		this.quantityShipped = quantityShipped;
	}

	public Integer getQuantityReceived() {
		return quantityReceived;
	}

	public void setQuantityReceived(Integer quantityReceived) {
		this.quantityReceived = quantityReceived;
	}

	public Integer getQuantityInCase() {
		return quantityInCase;
	}

	public void setQuantityInCase(Integer quantityInCase) {
		this.quantityInCase = quantityInCase;
	}
	
	
	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getSellerShipped() {
		return sellerShipped;
	}

	public void setSellerShipped(Integer sellerShipped) {
		this.sellerShipped = sellerShipped;
	}

	@Transient
	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	@ManyToOne()
	@JoinColumn(name="fba_inbound_id")
	@NotFound(action = NotFoundAction.IGNORE)
	public FbaInbound getFbaInbound() {
		return fbaInbound;
	}

	public void setFbaInbound(FbaInbound fbaInbound) {
		this.fbaInbound = fbaInbound;
	}
	
	public String getProblem() {
		return problem;
	}
	
	public void setProblem(String problem) {
		this.problem = problem;
	}
	/*public FbaInboundItem(InboundShipmentItem item){
		this();
		this.fnSku  = item.getFulfillmentNetworkSKU();
		this.sku = item.getSellerSKU();
		this.quantityInCase = item.getQuantityInCase();
		this.quantityReceived = item.getQuantityReceived();
		this.quantityShipped = item.getQuantityShipped();
	}*/
	
	public FbaInboundItem(String sku, Integer sellerShipped,
			Integer quantityShipped, FbaInbound fbaInbound) {
		super();
		this.sku = sku;
		this.sellerShipped = sellerShipped;
		this.quantityShipped = quantityShipped;
		this.fbaInbound = fbaInbound;
	}

	@Transient
	public String getPageDelFlag() {
		return pageDelFlag;
	}

	public void setPageDelFlag(String pageDelFlag) {
		this.pageDelFlag = pageDelFlag;
	}

	@Override
	public String toString() {
		return "{\"sku\":\""+this.sku+"\",\"quantity\":\""+this.quantityShipped+"\"}";
	}

	//错误记录
	@Transient
	public Map<String, String> getProblemMap() {
		Map<String, String> rs = Maps.newHashMap();
		if (StringUtils.isNotEmpty(problem)) {
			String[] strings = problem.split("<br/>");
			for (String string : strings) {
				rs.put(string.split(":")[0], string.split(":")[1]);
			}
		}
		return rs;
	}
	
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	public Integer getPackQuantity() {
		return packQuantity;
	}
	public void setPackQuantity(Integer packQuantity) {
		this.packQuantity = packQuantity;
	}
	
}


