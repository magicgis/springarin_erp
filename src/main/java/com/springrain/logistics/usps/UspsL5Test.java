package com.springrain.logistics.usps;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;

/**
 * 调用示例类
 *
 */
public class UspsL5Test {
	
	private static Logger logger = LoggerFactory.getLogger(UspsL5Test.class);
	
	/**
	 * 创建标签调用示例
	 */
	public void testCreateShipment(){
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		UspsL5 uspsL5 = new UspsL5();
		uspsL5.setDate(format.format(DateUtils.addDays(new Date(), 1)));	//范围为7天内
		uspsL5.setMailClass("PM");
		uspsL5.setPackageType("PACKAGE");
		uspsL5.setWeight(12);
		uspsL5.setLength(5f);
		uspsL5.setWidth(2f);
		uspsL5.setHeight(1.5f);
		
		uspsL5.setSenderName("Adam Boyle");
		uspsL5.setSenderCompany("US Parcel Technologies");
		uspsL5.setSenderAddress1("770 East Main St. #132");
		uspsL5.setSenderCity("Lehi");
		uspsL5.setSenderState("UT");
		uspsL5.setSenderZip("84043");
		uspsL5.setSenderZip4("2293");
		
		uspsL5.setReceiverName("Tim Diego");
		uspsL5.setReceiverCompany("L5");
		uspsL5.setReceiverAddress1("204 37TH AVE NORTH");
		uspsL5.setReceiverAddress2("MAIL STOP 341");
		uspsL5.setReceiverCity("Saint Petersburg");
		uspsL5.setReceiverState("FL");
		uspsL5.setReceiverZip("33704");
		uspsL5.setReceiverZip4("1416");
		
		uspsL5.setInsurance(800);
		
		String result = UspsL5Service.createShipment(uspsL5);
		logger.info(result);
		JSONObject object = (JSONObject) JSON.parse(result);
		if (StringUtils.isNotEmpty(object.getString("error"))) {
			logger.error(object.getString("error"));
			return;
		}
		String trackingNumber = object.getString("trackingNumber");
		logger.info("trackingNumber:" + trackingNumber);
		String labelImage = object.getString("labelImage");	//标签图片(png)base64编码后的字符串
		logger.info("labelImage:" + labelImage);
		try {
			//保存图片方法
			FileUtils.writeByteArrayToFile(new File("e:/"+trackingNumber+".png"), Encodes.getUnBASE64Byte(labelImage.getBytes()));
		} catch (Exception e) {
			logger.error("写入标签失败", e);
		}
		Integer total = object.getJSONObject("quote").getInteger("total");	//总费用美分(基础费用+保险)
		logger.info("total:" + total);
	}
	
	/**
	 * 查询价格
	 * @param uspsL5
	 * @return json字符串
	 */
	public static void quote() {
		try {
			DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			UspsL5 uspsL5 = new UspsL5();
			uspsL5.setDate(format.format(DateUtils.addDays(new Date(), 1)));
			uspsL5.setMailClass("PM");
			uspsL5.setPackageType("PACKAGE");
			uspsL5.setWeight(12);
			uspsL5.setLength(5f);
			uspsL5.setWidth(2f);
			uspsL5.setHeight(1.5f);
			
			uspsL5.setSenderZip("84043");
			uspsL5.setSenderZip4("2293");
			
			uspsL5.setReceiverZip("33704");
			uspsL5.setReceiverZip4("1416");
			
			uspsL5.setInsurance(800);
			
			String result = UspsL5Service.quote(uspsL5);
			logger.info(result);
			JSONObject object = (JSONObject) JSON.parse(result);
			if (StringUtils.isNotEmpty(object.getString("error"))) {
				logger.error(object.getString("error"));
				return;
			}
			Integer total = object.getInteger("total");//总费用美分(基础费用+保险)
			logger.info("total:" + total);
		} catch (Exception e) {
			logger.error("查询价格失败", e);
		}
	}
}
