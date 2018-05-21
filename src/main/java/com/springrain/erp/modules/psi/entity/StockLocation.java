/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;

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

import com.springrain.erp.common.persistence.DataEntity;
import com.springrain.erp.common.utils.StringUtils;

/**
 * 仓库库位
 */
@Entity
@Table(name = "psi_stock_location")
@Cache(usage = CacheConcurrencyStrategy.NONE)
public class StockLocation    extends DataEntity<StockLocation>  {
	
	private static final long serialVersionUID = 1L;
	private Integer id; 		// 编号
	private String name;
	private StockArea stockArea;
	public StockLocation() {
		super();
	}
	
	public StockLocation(Integer id) {
		this.id=id;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


    @ManyToOne()
    @JoinColumn(name="area_id")
    @NotFound(action = NotFoundAction.IGNORE)
    public StockArea getStockArea() {
        return stockArea;
    }

    public void setStockArea(StockArea stockArea) {
        this.stockArea = stockArea;
    }


}


