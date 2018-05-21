package com.springrain.erp.common.utils;

import java.math.BigDecimal;
import java.util.Map;

import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;

public class MathUtils {
	/**
	 * 求给定双精度数组中值的最大值
	 * 
	 * @param inputData
	 *            输入数据数组
	 * @return 运算结果,如果输入值不合法，返回为-1
	 */
	public static Double getMax(Double[] inputData) {
		if (inputData == null || inputData.length == 0)
			return -1d;
		int len = inputData.length;
		Double max = inputData[0];
		for (int i = 0; i < len; i++) {
			if (max < inputData[i])
				max = inputData[i];
		}
		return max;
	}

	/**
	 * 求求给定双精度数组中值的最小值
	 * 
	 * @param inputData
	 *            输入数据数组
	 * @return 运算结果,如果输入值不合法，返回为-1
	 */
	public static Double getMin(Double[] inputData) {
		if (inputData == null || inputData.length == 0)
			return -1d;
		int len = inputData.length;
		Double min = inputData[0];
		for (int i = 0; i < len; i++) {
			if (min > inputData[i])
				min = inputData[i];
		}
		return min;
	}

	/**
	 * 求给定双精度数组中值的和
	 * 
	 * @param inputData
	 *            输入数据数组
	 * @return 运算结果
	 */
	public static Double getSum(Double[] inputData) {
		if (inputData == null || inputData.length == 0)
			return -1d;
		int len = inputData.length;
		Double sum = 0d;
		for (int i = 0; i < len; i++) {
			sum = sum + inputData[i];
		}

		return sum;

	}
	
	/**
	 * 求给定双精度数组中值的和
	 * 
	 * @param inputData
	 *            输入数据数组
	 * @return 运算结果
	 */
	public static Integer getSum(Integer[] inputData) {
		if (inputData == null || inputData.length == 0)
			return -1;
		int len = inputData.length;
		Integer sum = 0;
		for (int i = 0; i < len; i++) {
			sum = sum + inputData[i];
		}

		return sum;

	}
	
	public static Float getAverage(Integer[] inputData) {
		if (inputData == null || inputData.length == 0)
			return -1f;
		int len = inputData.length;
		Float result;
		result = (float)getSum(inputData) / len;
		return result;
	}

	/**
	 * 求给定双精度数组中值的数目
	 * 
	 * @param input
	 *            Data 输入数据数组
	 * @return 运算结果
	 */
	public static int getCount(Double[] inputData) {
		if (inputData == null)
			return -1;

		return inputData.length;
	}

	/**
	 * 求给定双精度数组中值的平均值
	 * 
	 * @param inputData
	 *            输入数据数组
	 * @return 运算结果
	 */
	public static Double getAverage(Double[] inputData) {
		if (inputData == null || inputData.length == 0)
			return -1d;
		int len = inputData.length;
		Double result;
		result = getSum(inputData) / len;

		return result;
	}

	/**
	 * 求给定双精度数组中值的平方和
	 * 
	 * @param inputData
	 *            输入数据数组
	 * @return 运算结果
	 */
	public static Double getSquareSum(Double[] inputData) {
		if (inputData == null || inputData.length == 0)
			return -1d;
		int len = inputData.length;
		Double sqrsum = 0.0;
		for (int i = 0; i < len; i++) {
			sqrsum = sqrsum + inputData[i] * inputData[i];
		}

		return sqrsum;
	}

	/**
	 * 求给定双精度数组中值的方差
	 * 
	 * @param inputData
	 *            输入数据数组
	 * @return 运算结果
	 */
	public static Double getVariance(Double[] inputData) {
		int count = getCount(inputData);
		Double sqrsum = getSquareSum(inputData);
		Double average = getAverage(inputData);
		Double result;
		result = (sqrsum - count * average * average) / count;

		return result;
	}

	/**
	 * 求给定双精度数组中值的标准差
	 * 
	 * @param inputData
	 *            输入数据数组
	 * @return 运算结果
	 */
	public static Double getStandardDiviation(Double[] inputData) {
		Double result;
		// 绝对值化很重要
		result = Math.sqrt(Math.abs(getVariance(inputData)));
		return result;
	}
	
	/**
	 * 向上取整
	 */
	public static Integer  roundUp(Double number) {
		if(number==null){
			return null;
		}
		BigDecimal temp = new BigDecimal(number);
		temp = temp.setScale(0, BigDecimal.ROUND_UP);
		return temp.intValue();
	}

	/**
	 * 向下取整
	 */
	public static Integer roundDown(Double number){
		if(number==null){
			return null;
		}
		BigDecimal temp = new BigDecimal(number);
		temp = temp.setScale(0, BigDecimal.ROUND_DOWN);
		return temp.intValue();
	}
	
	
	/**
	 * 货币汇率换算
	 * @param source 原始货币单位
	 * @param target 目标货币单位(暂时支持转换为欧元和美元两种货币单位，可扩展)
	 * @return
	 */
	public static Float getRate(String source, String target, Map<String, Float> rate) {
		if (source != null && source.equals(target)) {
			return 1f;
		}
		if (rate == null || rate.size() < 19) { //如果为空或者信息不够完整则用实时汇率计算
			rate = AmazonProduct2Service.getRateConfig();
		}
		if ("EUR".equals(target)) {	//转换为欧元
			if ("CNY".equals(source)) {
				return rate.get("CNY/USD")/ rate.get("EUR/USD");
			} else {
				return rate.get(source + "/EUR");
			}
		} else {	//转换为美元
			return rate.get(source + "/USD");
		}
	}
}