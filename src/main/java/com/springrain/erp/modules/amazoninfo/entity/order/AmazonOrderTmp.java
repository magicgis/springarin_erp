package com.springrain.erp.modules.amazoninfo.entity.order;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="Orders")
public class AmazonOrderTmp {
	
	private Long count;
	
	private List<AmazonOrder> amazonOrders;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public List<AmazonOrder> getAmazonOrders() {
		return amazonOrders;
	}

	public void setAmazonOrders(List<AmazonOrder> amazonOrders) {
		this.amazonOrders = amazonOrders;
	}

	public AmazonOrderTmp(Long count, List<AmazonOrder> amazonOrders) {
		super();
		this.count = count;
		this.amazonOrders = amazonOrders;
	}
	
	public AmazonOrderTmp() {}
}
