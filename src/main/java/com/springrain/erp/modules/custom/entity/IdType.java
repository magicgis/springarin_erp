package com.springrain.erp.modules.custom.entity;


public class IdType {

	/**
	 * 获取对象值（导入）
	 */
	public static Object getValue(String val) {
		return "SPR-"+val;
	}

	/**
	 * 设置对象值（导出）
	 */
	public static String setValue(Object val) {
		return "SPR-"+val;
	}
	
}
