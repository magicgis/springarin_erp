package com.springrain.erp.modules.custom.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class CustomProductProblemKey implements Serializable {

	private static final long serialVersionUID = 1L;
	private String dataType;
	private String dataId;
	
	public String getDataType() {
		return dataType;
	}
	
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public String getDataId() {
		return dataId;
	}
	
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CustomProductProblemKey) {
			CustomProductProblemKey key = (CustomProductProblemKey) obj;
			if (this.dataId == key.getDataId()
					&& this.dataType.equals(key.getDataType())) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.dataType.hashCode();
	}

	public CustomProductProblemKey(){};
	public CustomProductProblemKey(String dataType, String dataId) {
		super();
		this.dataType = dataType;
		this.dataId = dataId;
	}
	
}
