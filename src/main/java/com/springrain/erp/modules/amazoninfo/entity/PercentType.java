package com.springrain.erp.modules.amazoninfo.entity;

import java.math.BigDecimal;

public class PercentType {

	/**
	 * 获取对象值（导入）
	 */
	public static Object getValue(String val) {
		if(val!=null){
			BigDecimal   bd   =   new   BigDecimal(Float.parseFloat(val));
			return  bd.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue()+"%";
		}
		return "";
	}

	/**
	 * 设置对象值（导出）
	 */
	public static String setValue(Object val) {
		if(val!=null){
			BigDecimal   bd   =   new   BigDecimal((Float)val);
			return  bd.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue()+"%";
		}
		return "";
	}
	
}
