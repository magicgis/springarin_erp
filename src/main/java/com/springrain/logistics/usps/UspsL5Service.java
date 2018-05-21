package com.springrain.logistics.usps;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.SpringContextHolder;
import com.springrain.server.webservice.UspsL5WebRequest;

public class UspsL5Service {
	
	private static Logger logger = LoggerFactory.getLogger(UspsL5Service.class);
	
	//API Test Key
	/*private static String key = "f43e8f30-096a-4ff1-9688-6b3e95be26ce";
	private static String password = "20Inateck18";
	private static String serviceUrl = "https://test.usparceltech.com/api/";*/
	
	//API Pro Key
	private static String key = "aab523c7-ef19-43db-8930-c88e98843685";
	private static String password = "20Inateck18";
	private static String serviceUrl = "https://prod.usparceltech.com/api/";
	
	/**
	 * 创建发货标签
	 * @param uspsL5
	 * @return json字符串	JSONObject object = (JSONObject) JSON.parse(result);
	 */
	public static String createShipment(UspsL5 uspsL5) {
		try {
			JSONObject object = new JSONObject();
			object.put("header", getHeader());
			
			JSONObject shipmentJson = new JSONObject();
			object.put("shipment", shipmentJson);
			shipmentJson.put("date", uspsL5.getDate());
			shipmentJson.put("mailClass", uspsL5.getMailClass());
			shipmentJson.put("packageType", uspsL5.getPackageType());
			shipmentJson.put("weight", uspsL5.getWeight());
			
			JSONObject dimensionsJson = new JSONObject();
			shipmentJson.put("dimensions", dimensionsJson);
			dimensionsJson.put("length", uspsL5.getLength());
			dimensionsJson.put("width", uspsL5.getWidth());
			dimensionsJson.put("height", uspsL5.getHeight());
			
			JSONObject senderJson = new JSONObject();
			shipmentJson.put("sender", senderJson);
			senderJson.put("name", uspsL5.getSenderName());
			senderJson.put("company", uspsL5.getSenderCompany());
			senderJson.put("address1", uspsL5.getSenderAddress1());
			senderJson.put("address2", uspsL5.getSenderAddress2());
			senderJson.put("city", uspsL5.getSenderCity());
			senderJson.put("state", uspsL5.getSenderState());
			senderJson.put("zip", uspsL5.getSenderZip());
			senderJson.put("zip4", uspsL5.getSenderZip4());
			
			JSONObject receiverJson = new JSONObject();
			shipmentJson.put("receiver", receiverJson);
			receiverJson.put("name", uspsL5.getReceiverName());
			receiverJson.put("company", uspsL5.getReceiverCompany());
			receiverJson.put("address1", uspsL5.getReceiverAddress1());
			receiverJson.put("address2", uspsL5.getReceiverAddress2());
			receiverJson.put("city", uspsL5.getReceiverCity());
			receiverJson.put("state", uspsL5.getReceiverState());
			receiverJson.put("zip", uspsL5.getReceiverZip());
			receiverJson.put("zip4", uspsL5.getReceiverZip4());
			receiverJson.put("country", uspsL5.getReceiverCountry());

			JSONObject optionsJson = new JSONObject();
			shipmentJson.put("options", optionsJson);
			optionsJson.put("signature", "N");
			optionsJson.put("insurance", uspsL5.getInsurance());
			
			String sendContent = object.toJSONString();
			String result = postService("CreateShipment", sendContent);
	        return result;
		} catch (Exception e) {
			logger.error("创建标签异常", e);
		}
		return null;
	}
	
	/**
	 * 查询价格
	 * @param uspsL5
	 * @return json字符串
	 */
	public static String quote(UspsL5 uspsL5) {
		try {
			JSONObject object = new JSONObject();
			object.put("header", getHeader());
			
			JSONObject shipmentJson = new JSONObject();
			object.put("shipment", shipmentJson);
			shipmentJson.put("mailClass", uspsL5.getMailClass());
			shipmentJson.put("packageType", uspsL5.getPackageType());
			shipmentJson.put("weight", uspsL5.getWeight());
			
			JSONObject dimensionsJson = new JSONObject();
			shipmentJson.put("dimensions", dimensionsJson);
			dimensionsJson.put("length", uspsL5.getLength());
			dimensionsJson.put("width", uspsL5.getWeight());
			dimensionsJson.put("height", uspsL5.getHeight());
			
			JSONObject senderJson = new JSONObject();
			shipmentJson.put("sender", senderJson);
			senderJson.put("zip", uspsL5.getSenderZip());
			senderJson.put("zip4", uspsL5.getSenderZip4());
			
			JSONObject receiverJson = new JSONObject();
			shipmentJson.put("receiver", receiverJson);
			receiverJson.put("zip", uspsL5.getReceiverZip());
			receiverJson.put("zip4", uspsL5.getReceiverZip4());
			receiverJson.put("country", uspsL5.getReceiverCountry());

			JSONObject optionsJson = new JSONObject();
			shipmentJson.put("options", optionsJson);
			optionsJson.put("signature", "N");
			optionsJson.put("insurance", uspsL5.getInsurance());	//包裹价值,单位便士
			
			String sendContent = object.toJSONString();
			String result = postService("Quote", sendContent);
	        return result;
		} catch (Exception e) {
			logger.error("查询价格失败", e);
		}
		return null;
	}
	
	/**
	 * 废弃
	 */
	public static String voidByTrackingNumber(String trackingNumber) {
		try {
			JSONObject object = new JSONObject();
			object.put("header", getHeader());
			
			JSONObject shipmentJson = new JSONObject();
			object.put("shipment", shipmentJson);
			shipmentJson.put("trackingNumber", trackingNumber);
			
			String sendContent = object.toJSONString();
		    String result = postService("Void", sendContent);
		    return result;
		} catch (Exception e) {
			logger.error("取消TrackingNumber异常：" + trackingNumber, e);
		}
		return null;
	}
	
	/**
	 * 查询邮资余额(单位：美分)
	 */
	public static String postageBalance() {
		try {
			JSONObject object = new JSONObject();
			object.put("header", getHeader());
			String sendContent = object.toJSONString();
		    String result = postService("PostageBalance", sendContent);
	        return result;
		} catch (Exception e) {
			logger.error("查询邮资余额异常", e);
		}
		return null;
	}
	
	/**
	 * 购买邮资
	 * @param money 美分
	 */
	public static String purchasePostage(Integer money) {
		try {
			JSONObject object = new JSONObject();
			object.put("header", getHeader());
			object.put("amount", money);
			String sendContent = object.toJSONString();
		    String result = postService("PurchasePostage", sendContent);
	        return result;
		} catch (Exception e) {
			logger.error("邮资充值异常", e);
		}
		return null;
	}
	
	public static JSONObject getHeader() {
		JSONObject headerJson = new JSONObject();
		headerJson.put("key", key);
		headerJson.put("password", password);
		headerJson.put("version", "1.0");
		return headerJson;
	}
	
	/**
	 * 调用远程服务并返回结果
	 */
	private static String postService(String serviceName, String sendContent) {
		try {
			String url = serviceUrl + serviceName;
			UspsL5WebRequest request = SpringContextHolder.getBean(UspsL5WebRequest.class);
			//调用备份服务器提供的接口推送
			return request.postService(url, Encodes.getBASE64String(sendContent.getBytes("UTF-8")), "Hip6k8wOkQ2qb2*Bb");
		} catch (Exception e) {
			logger.error("USPS服务请求异常", e);
		}
        return null;
	}
}
