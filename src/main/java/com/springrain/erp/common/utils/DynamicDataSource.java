package com.springrain.erp.common.utils;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DynamicDataSource extends AbstractRoutingDataSource {
	
	/**
	 * spring erp dataSource
	 */
    public static final String DATA_SOURCE_1 = "dataSource1";
    
    /**
	 * licheng erp dataSource
	 */
    public static final String DATA_SOURCE_2 = "dataSource2";
	
	private static final ThreadLocal<String> contextHolder = new ThreadLocal<String>();

	@Override
	protected Object determineCurrentLookupKey() {
		return getCustomerType();
	}

	/**
	 * 设置指定的数据源
	 * @param customerType	数据源别名
	 */
	public static void setCustomerType(String customerType) {
		contextHolder.set(customerType);
	}

	public static String getCustomerType() {
		return contextHolder.get();
	}

	public static void clearCustomerType() {
		contextHolder.remove();
	}
	
}