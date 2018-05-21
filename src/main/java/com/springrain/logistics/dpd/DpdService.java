package com.springrain.logistics.dpd;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.SpringContextHolder;
import com.springrain.server.webservice.UspsL5WebRequest;

public class DpdService {
	
	private static Logger logger = LoggerFactory.getLogger(DpdService.class);
	
	//DPD账号信息
	private static String domain = "public-ws-stage.dpd.com";	//测试环境域名
	//private static String domain = "public-ws.dpd.com";	//生产环境域名
	private static String delisId = "fmtech104";
	private static String password = "ejbs1357";
	
	/**
	 * 创建发货标签(用inateckDpdToken字符串代替token,webservice接口中会自动替换)
	 * @param dpd
	 * @return JSONObject 正常情况返回parcelLabelNumber(多个以逗号','分隔)和parcellabelsPDF
	 */
	public static JSONObject storeOrders(Dpd dpd) {
		List<Dpd> list = Lists.newArrayList();
		list.add(dpd);
		return storeOrders(list);
	}
	/**
	 * 创建发货标签(用inateckDpdToken字符串代替token,webservice接口中会自动替换)
	 * @param dpd
	 * @return JSONObject 正常情况返回parcelLabelNumber(多个以逗号','分隔)和parcellabelsPDF
	 */
	public static JSONObject storeOrders(List<Dpd> dpdList) {
		JSONObject resultJson = new JSONObject();
		String serviceUrl = "https://"+domain+"/services/ShipmentService/V3_2/storeOrders";
		try {
			String head = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" "+
				" xmlns:ns=\"http://dpd.com/common/service/types/Authentication/2.0\"  "+
				" xmlns:ns1=\"http://dpd.com/common/service/types/ShipmentService/3.2\"> "+
				" <soapenv:Header> "+
				" <ns:authentication> "+
				" <delisId>"+delisId+"</delisId> "+
				" <authToken>inateckDpdToken</authToken> "+
				" <messageLanguage>de_DE</messageLanguage> "+
				" </ns:authentication> "+
				" </soapenv:Header> "+
				" <soapenv:Body> "+
				" <ns1:storeOrders> "+
				" <printOptions> "+
				" <printerLanguage>PDF</printerLanguage> "+
				" <paperFormat>A6</paperFormat> "+
				" </printOptions>";
			StringBuilder content = new StringBuilder();
			for (Dpd dpd : dpdList) {
				content.append("<order>");
				content.append("<generalShipmentData>");
				content.append("<sendingDepot>"+dpd.getSendingDepot()+"</sendingDepot>");
				content.append("<product>"+dpd.getProduct()+"</product>");
				content.append("<mpsCompleteDelivery>"+dpd.getMpsCompleteDelivery()+"</mpsCompleteDelivery>");
				content.append("<sender>");
				content.append("<name1>"+dpd.getSenderName()+"</name1>");
				content.append("<street>"+dpd.getSenderAddress1()+"</street>");
				content.append("<country>"+dpd.getSenderCountry()+"</country>");
				content.append("<zipCode>"+dpd.getSenderZip()+"</zipCode>");
				content.append("<city>"+dpd.getSenderCity()+"</city>");
				content.append("<customerNumber>"+dpd.getCustomerNumber()+"</customerNumber>");
				content.append("</sender>");
				content.append("<recipient>");
				content.append("<name1>"+dpd.getReceiverName()+"</name1>");
				content.append("<street>"+dpd.getReceiverAddress1()+"</street>");
				content.append("<state>"+dpd.getReceiverState()+"</state>");
				content.append("<country>"+dpd.getReceiverCountry()+"</country>");
				content.append("<zipCode>"+dpd.getReceiverZip()+"</zipCode>");
				content.append("<city>"+dpd.getReceiverCity()+"</city>");
				content.append("</recipient>");
				content.append("</generalShipmentData>");
				content.append("<productAndServiceData>");
				content.append("<orderType>consignment</orderType>");
				content.append("</productAndServiceData>");
				content.append("</order>");
			}
			String tail =" </ns1:storeOrders> "+
				" </soapenv:Body> "+
				" </soapenv:Envelope> ";
			String result = postService(serviceUrl, head+content.toString()+tail);
			if (result != null) {
				try {
					Document document = DocumentHelper.parseText(result);
					Element root = document.getRootElement();
					List<Node> list = root.selectNodes("//parcelLabelNumber");
					if (list != null && list.size() > 0) {
						StringBuilder sb = new StringBuilder("");
						for (int i = 0; i < list.size(); i++) {
							Node node = list.get(i);
							if (i < list.size() - 1) {
								sb.append(node.getText()+",");
							} else {
								sb.append(node.getText());
							}
						}
						resultJson.put("parcelLabelNumber", sb.toString());
					}
			        /*Node parcelLabelNumberNode = root.selectSingleNode("//parcelLabelNumber");
			        if (parcelLabelNumberNode != null) {
			        	resultJson.put("parcelLabelNumber", parcelLabelNumberNode.getText());
					}*/
			        //base64编码的PDF格式的label
			        Node pdfNode = root.selectSingleNode("//parcellabelsPDF");
			        if (pdfNode != null) {
			        	resultJson.put("parcellabelsPDF", pdfNode.getText());
					} else {
						Node messageNode = root.selectSingleNode("//errorMessage");
				        if (messageNode != null) {
				        	resultJson.put("error", messageNode.getText());
				        }
						messageNode = root.selectSingleNode("//message");
				        if (messageNode != null) {
				        	resultJson.put("error", messageNode.getText());
						}
						messageNode = root.selectSingleNode("//faultstring");
				        if (messageNode != null) {
				        	resultJson.put("error", messageNode.getText());
						}
					}
				} catch (Exception e) {
					resultJson.put("error", result);
				}
			}
		} catch (Exception e) {
			logger.error("创建shiplabel失败", e);
			resultJson.put("error", "创建shiplabel失败");
		}
		return resultJson;
	}
	
	/**
	 * 调用远程服务并返回结果
	 */
	private static String postService(String serviceUrl, String sendContent) {
		try {
			UspsL5WebRequest request = SpringContextHolder.getBean(UspsL5WebRequest.class);
			//调用备份服务器提供的接口推送
			return request.dpdService(serviceUrl, Encodes.getBASE64String(sendContent.getBytes("UTF-8")), "Hip6k8wOkQ2qb2*Bb");
		} catch (Exception e) {
			logger.error("USPS服务请求异常", e);
		}
        return null;
	}
}
