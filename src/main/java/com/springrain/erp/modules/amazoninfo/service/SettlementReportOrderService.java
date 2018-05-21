package com.springrain.erp.modules.amazoninfo.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.SettlementReportOrderDao;
import com.springrain.erp.modules.amazoninfo.entity.SettlementReportItem;
import com.springrain.erp.modules.amazoninfo.entity.SettlementReportOrder;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;

/**
 * @author Sam
 * 
 */
@Component
@Transactional(readOnly = true)
public class SettlementReportOrderService extends BaseService {

	@Autowired
	private SettlementReportOrderDao settlementReportOrderDao;

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * @param xmlPath
	 *            解析xml
	 */
	@Transactional(readOnly = false)
	public boolean parseXml(String xmlPath, String country) {
		File xmlFile = new File(xmlPath);
		if (xmlFile.exists()) {
			// 进行处理
			try {
				String content = FileUtils.readFileToString(xmlFile);
				content = content.replaceAll(
						"<\\?xml version=\"1.0\" encoding=\"UTF-8\"\\?>", "");
				content = "<data>" + content + "</data>";
				SAXReader reader = new SAXReader();
				Document document = reader.read(new ByteArrayInputStream(
						content.getBytes("utf-8")));
				// 文档根节点
				Element node = document.getRootElement();
				// 开始解析order节点
				List<Element> orders = (List<Element>) node
						.selectNodes("//Order");
				SettlementReportOrder order = null;
				for (Element element : orders) {
					order = new SettlementReportOrder();
					order.setCountry(country);
					Element amazonOrderID = element.element("AmazonOrderID");
					if (amazonOrderID != null) {
						order.setAmazonOrderId(amazonOrderID.getText());
					}
					order.setAdjustmentId("");

					Element merchantOrderID = element
							.element("MerchantOrderID");
					if (merchantOrderID != null) {
						order.setMerchantOrderId(merchantOrderID.getText());
					}

					Element shipmentID = element.element("ShipmentID");
					if (shipmentID != null) {
						order.setShipmentId(shipmentID.getText());
					}

					Element marketplaceName = element
							.element("MarketplaceName");
					if (marketplaceName != null) {
						order.setMarketplaceName(marketplaceName.getText());
					}

					Element fulfillment = element.element("Fulfillment");
					List<SettlementReportItem> items2 = Lists.newArrayList();
					if (fulfillment != null) {
						Element merchantFulfillmentID = fulfillment
								.element("MerchantFulfillmentID");
						if (merchantFulfillmentID != null) {
							order.setMerchantFulfillmentId(merchantFulfillmentID
									.getText());
						}
						Element postedDate = fulfillment.element("PostedDate");
						if (postedDate != null) {
							String posted = postedDate.getText();
							posted = posted.replace("T", " ");
							posted = posted.substring(0, posted.indexOf("+"));
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd hh:mm:ss");
							try {
								order.setPostedDate(sdf.parse(posted));
							} catch (ParseException e) {
							}
						}
						List<Element> items = fulfillment.elements("Item");
						SettlementReportItem item = null;

						for (Element element2 : items) {
							item = new SettlementReportItem();
							Element amazonOrderItemCode = element2
									.element("AmazonOrderItemCode");
							if (amazonOrderItemCode != null) {
								item.setAmazonOrderItemCode(amazonOrderItemCode
										.getText());
							}

							Element sku = element2.element("SKU");
							if (sku != null) {
								item.setSku(sku.getText());
							}

							Element quantity = element2.element("Quantity");
							if (quantity != null) {
								item.setQuantity(Integer.parseInt(quantity
										.getText()));
							}

							Element itemPrice = element2.element("ItemPrice");
							if (itemPrice != null) {
								List<Element> components = itemPrice
										.elements("Component");
								for (Element element3 : components) {
									Element type = element3.element("Type");
									Element amount = element3.element("Amount");
									if (type != null && amount != null) {
										if ("Principal".equals(type.getText())) {
											item.setPrincipal(new BigDecimal(
													amount.getText()));
										} else if ("Shipping".equals(type
												.getText())) {
											item.setShipping(new BigDecimal(
													amount.getText()));
										}
									}
								}
							}

							Element itemFees = element2.element("ItemFees");
							if (itemFees != null) {
								List<Element> fees = itemFees.elements("Fee");
								for (Element element3 : fees) {
									Element type1 = element3.element("Type");
									Element amount1 = element3
											.element("Amount");
									if (type1 != null && amount1 != null) {
										String typeTxt = type1.getText();
										if ("Cross-Border Fulfillment Fee"
												.equals(typeTxt)) {
											item.setCrossBorderFulfillmentFee(new BigDecimal(
													amount1.getText()));
										} else if ("FBAPerUnitFulfillmentFee"
												.equals(typeTxt)) {
											item.setFbaPerUnitFulfillmentFee(new BigDecimal(
													amount1.getText()));
										} else if ("FBAWeightBasedFee"
												.equals(typeTxt)) {
											item.setFbaWeightBasedFee(new BigDecimal(
													amount1.getText()));
										} else if ("Commission".equals(typeTxt)) {
											item.setCommission(new BigDecimal(
													amount1.getText()));
										} else if ("ShippingChargeback"
												.equals(typeTxt)) {
											item.setShippingChargeback(new BigDecimal(
													amount1.getText()));
										} else if ("GiftwrapChargeback"
												.equals(typeTxt)) {
											item.setGiftwrapChargeback(new BigDecimal(
													amount1.getText()));
										}
									}
								}
							}
							item.setAddTime(new Date());
							item.setOrder(order);
							items2.add(item);
						}
					}
					order.setItems(items2);
					// 如果不存在相同记录则增加
					if (isNotExist(order)) {
						order.setAddTime(new Date());
						settlementReportOrderDao.save(order);
					}
				}

				// 开始解析adjustment节点
				List<Element> adjustments = (List<Element>) node
						.selectNodes("//Adjustment");
				for (Element element : adjustments) {
					order = new SettlementReportOrder();
					order.setCountry(country);
					Element amazonOrderID = element.element("AmazonOrderID");
					if (amazonOrderID != null) {
						order.setAmazonOrderId(amazonOrderID.getText());
					}

					Element merchantOrderID = element
							.element("MerchantOrderID");
					if (merchantOrderID != null) {
						order.setMerchantOrderId(merchantOrderID.getText());
					}

					Element adjustmentID = element.element("AdjustmentID");
					if (adjustmentID != null) {
						order.setAdjustmentId(adjustmentID.getText());
					}

					Element marketplaceName = element
							.element("MarketplaceName");
					if (marketplaceName != null) {
						order.setMarketplaceName(marketplaceName.getText());
					}

					Element fulfillment = element.element("Fulfillment");
					List<SettlementReportItem> items2 = Lists.newArrayList();
					if (fulfillment != null) {
						Element merchantFulfillmentID = fulfillment
								.element("MerchantFulfillmentID");
						if (merchantFulfillmentID != null) {
							order.setMerchantFulfillmentId(merchantFulfillmentID
									.getText());
						}
						Element postedDate = fulfillment.element("PostedDate");
						if (postedDate != null) {
							String posted = postedDate.getText();
							posted = posted.replace("T", " ");
							posted = posted.substring(0, posted.indexOf("+"));
							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd hh:mm:ss");
							try {
								order.setPostedDate(sdf.parse(posted));
							} catch (ParseException e) {
							}
						}
						List<Element> items = fulfillment
								.elements("AdjustedItem");
						SettlementReportItem item = null;
						for (Element element2 : items) {
							item = new SettlementReportItem();
							Element amazonOrderItemCode = element2
									.element("AmazonOrderItemCode");
							if (amazonOrderItemCode != null) {
								item.setAmazonOrderItemCode(amazonOrderItemCode
										.getText());
							}

							Element merchantAdjustmentItemID = element2
									.element("MerchantAdjustmentItemID");
							if (merchantAdjustmentItemID != null) {
								item.setMerchantAdjustmentItemId(merchantAdjustmentItemID
										.getText());
							}

							Element sku = element2.element("SKU");
							if (sku != null) {
								item.setSku(sku.getText());
							}

							Element itemPrice = element2
									.element("ItemPriceAdjustments");
							if (itemPrice != null) {
								List<Element> components = itemPrice
										.elements("Component");
								for (Element element3 : components) {
									Element type = element3.element("Type");
									Element amount = element3.element("Amount");
									if (type != null && amount != null) {
										if ("Principal".equals(type.getText())) {
											item.setPrincipal(new BigDecimal(
													amount.getText()));
										} else if ("Shipping".equals(type
												.getText())) {
											item.setShipping(new BigDecimal(
													amount.getText()));
										}
									}
								}
							}

							Element itemFees = element2
									.element("ItemFeeAdjustments");
							if (itemFees != null) {
								List<Element> fees = itemFees.elements("Fee");
								for (Element element3 : fees) {
									Element type1 = element3.element("Type");
									Element amount1 = element3
											.element("Amount");
									if (type1 != null && amount1 != null) {
										String typeTxt = type1.getText();
										if ("Cross-Border Fulfillment Fee"
												.equals(typeTxt)) {
											item.setCrossBorderFulfillmentFee(new BigDecimal(
													amount1.getText()));
										} else if ("FBAPerUnitFulfillmentFee"
												.equals(typeTxt)) {
											item.setFbaPerUnitFulfillmentFee(new BigDecimal(
													amount1.getText()));
										} else if ("FBAWeightBasedFee"
												.equals(typeTxt)) {
											item.setFbaWeightBasedFee(new BigDecimal(
													amount1.getText()));
										} else if ("Commission".equals(typeTxt)) {
											item.setCommission(new BigDecimal(
													amount1.getText()));
										} else if ("ShippingChargeback"
												.equals(typeTxt)) {
											item.setShippingChargeback(new BigDecimal(
													amount1.getText()));
										} else if ("GiftwrapChargeback"
												.equals(typeTxt)) {
											item.setGiftwrapChargeback(new BigDecimal(
													amount1.getText()));
										} else if ("RefundCommission"
												.equals(typeTxt)) {
											item.setRefundCommission(new BigDecimal(
													amount1.getText()));
										}
									}
								}
							}
							item.setAddTime(new Date());
							item.setOrder(order);
							items2.add(item);
						}
					}
					order.setItems(items2);
					// 如果不存在相同记录则增加
					if (isNotExist(order)) {
						order.setAddTime(new Date());
						settlementReportOrderDao.save(order);
					}
				}
				return true;
			} catch (Exception e) {
				logger.warn("解析xml出错了！", e);
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		SettlementReportOrderService  service = applicationContext.getBean(SettlementReportOrderService.class);
		service.parseReportTxt("E:/9742645806017593_finished_inserted.txt", "de", "Inateck_DE");
		applicationContext.close();
	}
	
	
	private static DateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss");
	/**
	 * @param xmlPath
	 *            解析xml
	 */
	@Transactional(readOnly = false)
	public boolean parseReportTxt(String txtPath, String country, String accountName) {
		File txtFile = new File(txtPath);
		if (txtFile.exists()) {
			// 进行处理
			try {
				List<String> lines = FileUtils.readLines(txtFile);
				int i = 0;
				Map<String,SettlementReportOrder> orderMap = Maps.newHashMap();
				Map<String,SettlementReportOrder> order1Map = Maps.newHashMap();
				Date now = new Date();
				SettlementReportItem item = null;
				
				for (String line : lines) {
					if(i>=2&&line.trim().length()>0){
						String[]cells = line.split("\t",36);
						String setId = cells[0];
						String sql = "SELECT COUNT(1) FROM settlementreport_order a WHERE a.`settlement_id` = :p1";
						if(i==2){
							if(((BigInteger)settlementReportOrderDao.findBySql(sql, new Parameter(setId)).get(0)).intValue()>0){
								break;
							}
						}
						if ("settlement-id".equals(cells[0])) {
							break;
						}
						String type = cells[6];
						String orderId = cells[7];
						if("Order".equals(type)){
							SettlementReportOrder order = orderMap.get(orderId);
							if(order==null){
								order = new SettlementReportOrder();
								order.setSettlementId(setId);
								String merchantOrderId =  cells[8];
								String adjustmentId = cells[9];
								String shipmentId = cells[10];
								String posted = cells[17];
								posted = posted.replace("T", " ");
								posted = posted.substring(0, posted.indexOf("+"));
								order.setAddTime(now);
								order.setAdjustmentId(adjustmentId);
								order.setAmazonOrderId(orderId);
								String temp = cells[11];
								if(temp!=null&&temp.contains(".")&&!temp.contains(country)){
									temp = temp.substring(temp.lastIndexOf(".")+1);
									if(!temp.equals(country)){
										country = temp;
										accountName = accountName.split("_")[0] + "_" + ("com".equals(country)?"US":country.toUpperCase());
									}
								}
								order.setCountry(country);
								order.setAccountName(accountName);
								order.setShipmentId(shipmentId);
								order.setMarketplaceName(cells[11]);
								order.setMerchantFulfillmentId(cells[16]);
								order.setPostedDate(sdf.parse(posted));
								order.setMerchantOrderId(merchantOrderId);
								orderMap.put(orderId, order);
								order.setType(type);
							}	
							String quantityStr =cells[22];
							String priceType = cells[23];
							String feeType = cells[25];
							String shipmentFee = cells[12];
							if(StringUtils.isNotEmpty(quantityStr)){
								String amazonOrderItemCode = cells[18];
								String sku =cells[21];
								item = new SettlementReportItem();
								item.setOrder(order);
								order.getItems().add(item);
								item.setQuantity(Integer.parseInt(quantityStr));
								item.setSku(sku);
								item.setAmazonOrderItemCode(amazonOrderItemCode);
							}else{
								if(StringUtils.isNotEmpty(priceType)){
									String priceAmount = replacePrice(cells[24]);
									if ("Principal".equals(priceType)) {
										if(item.getPrincipal()!=null){
											item.setPrincipal(item.getPrincipal().add(new BigDecimal(replacePrice(priceAmount))));
										}else{
											item.setPrincipal(new BigDecimal(replacePrice(priceAmount)));
										}
									} else if ("Shipping".equals(priceType)) {
										if(item.getShipping()!=null){
											item.setShipping(item.getShipping().add(new BigDecimal(replacePrice(priceAmount))));
										}else{
											item.setShipping(new BigDecimal(replacePrice(priceAmount)));
										}
									}else if ("COD".equals(priceType)) {
										if(item.getCod()!=null){
											item.setCod(item.getCod().add(new BigDecimal(replacePrice(priceAmount))));
										}else{
											item.setCod(new BigDecimal(replacePrice(priceAmount)));
										}
									}else if ("GiftWrap".equals(priceType)) {
										if(item.getGiftWrap()!=null){
											item.setGiftWrap(item.getGiftWrap().add(new BigDecimal(replacePrice(priceAmount))));
										}else{
											item.setGiftWrap(new BigDecimal(replacePrice(priceAmount)));
										}
									}else if ("Goodwill".equals(priceType)) {
										if(item.getGoodwill()!=null){
											item.setGoodwill(item.getGoodwill().add(new BigDecimal(replacePrice(priceAmount))));
										}else{
											item.setGoodwill(new BigDecimal(replacePrice(priceAmount)));
										}
									}else if ("Tax".equals(priceType)) {
										if(item.getTax()!=null){
											item.setTax(item.getTax().add(new BigDecimal(replacePrice(priceAmount))));
										}else{
											item.setTax(new BigDecimal(replacePrice(priceAmount)));
										}
									}else if ("SalesTaxServiceFee".equals(priceType)) {
										if(item.getSalesTaxServiceFee()!=null){
											item.setSalesTaxServiceFee(item.getSalesTaxServiceFee().add(new BigDecimal(replacePrice(priceAmount))));
										}else{
											item.setSalesTaxServiceFee(new BigDecimal(replacePrice(priceAmount)));
										}
									}else if ("ShippingTax".equals(priceType)) {
										if(item.getShippingTax()!=null){
											item.setShippingTax(item.getShippingTax().add(new BigDecimal(replacePrice(priceAmount))));
										}else{
											item.setShippingTax(new BigDecimal(replacePrice(priceAmount)));
										}
									} else{
										if(item.getOtherFee()!=null){
											item.setOtherFee(item.getOtherFee().add(new BigDecimal(replacePrice(priceAmount))));
										}else{
											item.setOtherFee(new BigDecimal(replacePrice(priceAmount)));
										}
										if (!"PointsGranted".equals(priceType)) {
											logger.warn("Order：" + country+":"+orderId+"出现新的费用类型:"+priceType +",已统计到OtherFee类型中" + txtPath);
										}
									}
								}else if(StringUtils.isNotEmpty(feeType)){
									String feeAmount = replacePrice(cells[26]);
									if ("Cross-Border Fulfillment Fee".equals(feeType)) {
										if(item.getCrossBorderFulfillmentFee()!=null){
											item.setCrossBorderFulfillmentFee(item.getCrossBorderFulfillmentFee().add(new BigDecimal(replacePrice(feeAmount))));
										}else{
											item.setCrossBorderFulfillmentFee(new BigDecimal(replacePrice(feeAmount)));
										}
									} else if ("FBAPerUnitFulfillmentFee".equals(feeType)) {
										if(item.getFbaPerUnitFulfillmentFee()!=null){
											item.setFbaPerUnitFulfillmentFee(item.getFbaPerUnitFulfillmentFee().add(new BigDecimal(replacePrice(feeAmount))));
										}else{
											item.setFbaPerUnitFulfillmentFee(new BigDecimal(replacePrice(feeAmount)));
										}
									} else if ("FBAPerOrderFulfillmentFee".equals(feeType)) {
										if(item.getFbaPerOrderFulfillmentFee()!=null){
											item.setFbaPerOrderFulfillmentFee(item.getFbaPerOrderFulfillmentFee().add(new BigDecimal(replacePrice(feeAmount))));
										}else{
											item.setFbaPerOrderFulfillmentFee(new BigDecimal(replacePrice(feeAmount)));
										}
									} else if ("FBAWeightBasedFee".equals(feeType)) {
										if(item.getFbaWeightBasedFee()!=null){
											item.setFbaWeightBasedFee(item.getFbaWeightBasedFee().add(new BigDecimal(replacePrice(feeAmount))));
										}else{
											item.setFbaWeightBasedFee(new BigDecimal(replacePrice(feeAmount)));
										}
									} else if ("Commission".equals(feeType)) {
										if(item.getCommission()!=null){
											item.setCommission(item.getCommission().add(new BigDecimal(replacePrice(feeAmount))));
										}else{
											item.setCommission(new BigDecimal(replacePrice(feeAmount)));
										}
									} else if ("ShippingChargeback".equals(feeType)) {
										if(item.getShippingChargeback()!=null){
											item.setShippingChargeback(item.getShippingChargeback().add(new BigDecimal(replacePrice(feeAmount))));
										}else{
											item.setShippingChargeback(new BigDecimal(replacePrice(feeAmount)));
										}
									} else if ("GiftwrapChargeback".equals(feeType)) {
										if(item.getGiftwrapChargeback()!=null){
											item.setGiftwrapChargeback(item.getGiftwrapChargeback().add(new BigDecimal(replacePrice(feeAmount))));
										}else{
											item.setGiftwrapChargeback(new BigDecimal(replacePrice(feeAmount)));
										}
									} else if ("RefundCommission".equals(feeType)) {
										if(item.getRefundCommission()!=null){
											item.setRefundCommission(item.getRefundCommission().add(new BigDecimal(replacePrice(feeAmount))));
										}else{
											item.setRefundCommission(new BigDecimal(replacePrice(feeAmount)));
										}
									}else if ("CODFee".equals(feeType)) {
										if(item.getCodFee()!=null){
											item.setCodFee(item.getCodFee().add(new BigDecimal(replacePrice(feeAmount))));
										}else{
											item.setCodFee(new BigDecimal(replacePrice(feeAmount)));
										}
									}else if ("ShippingHB".equals(feeType)) {
										if(item.getShippingHb()!=null){
											item.setShippingHb(item.getShippingHb().add(new BigDecimal(replacePrice(feeAmount))));
										}else{
											item.setShippingHb(new BigDecimal(replacePrice(feeAmount)));
										}
									}else if ("Tax".equals(feeType)) {
										if(item.getTax()!=null){
											item.setTax(item.getTax().add(new BigDecimal(replacePrice(feeAmount))));
										}else{
											item.setTax(new BigDecimal(replacePrice(feeAmount)));
										}
									}else if ("SalesTaxServiceFee".equals(feeType)) {
										if(item.getSalesTaxServiceFee()!=null){
											item.setSalesTaxServiceFee(item.getSalesTaxServiceFee().add(new BigDecimal(replacePrice(feeAmount))));
										}else{
											item.setSalesTaxServiceFee(new BigDecimal(replacePrice(feeAmount)));
										}
									}else if ("ShippingTax".equals(feeType)) {
										if(item.getShippingTax()!=null){
											item.setShippingTax(item.getShippingTax().add(new BigDecimal(replacePrice(feeAmount))));
										}else{
											item.setShippingTax(new BigDecimal(replacePrice(feeAmount)));
										}
									}else{
										if(item.getOtherFee()!=null){
											item.setOtherFee(item.getOtherFee().add(new BigDecimal(replacePrice(feeAmount))));
										}else{
											item.setOtherFee(new BigDecimal(replacePrice(feeAmount)));
										}
										if (!"PointsGranted".equals(feeType)) {
											logger.warn("Order：" + country+":"+orderId+"出现新的费用类型:"+feeType +",已统计到OtherFee类型中" + txtPath);
										}
									}
								}else if (StringUtils.isNotEmpty(shipmentFee)){
									if(item.getShipmentFee()==null){
										item.setShipmentFee(new BigDecimal(replacePrice(cells[13])));
									} else {
										item.setShipmentFee(item.getShipmentFee().add(new BigDecimal(replacePrice(cells[13]))));
									}
								}else if(StringUtils.isEmpty(quantityStr)&&StringUtils.isEmpty(priceType)&&StringUtils.isEmpty(feeType)){
									if(StringUtils.isNotEmpty(cells[32])){
										if(item.getPromotion()==null){
											item.setPromotion(new BigDecimal(cells[32]));
										}else{
											item.setPromotion(item.getPromotion().add(new BigDecimal(cells[32])));
										}
									}else{
										logger.warn(type+country+":"+orderId+"读取有误!!!" + txtPath);
									}
								}
							}
						}else if(type != null && type.toLowerCase().contains("refund") && StringUtils.isNotEmpty(cells[16])){
							SettlementReportOrder order = order1Map.get(orderId);
							String merchantAdjustmentItemId = cells[20];
							if(order==null){
								order = new SettlementReportOrder();
								order.setSettlementId(setId);
								String merchantOrderId =  cells[8];
								String adjustmentId = cells[9];
								String shipmentId = cells[10];
								String posted = cells[17];
								posted = posted.replace("T", " ");
								posted = posted.substring(0, posted.indexOf("+"));
								order.setAddTime(now);
								order.setAdjustmentId(adjustmentId);
								order.setAmazonOrderId(orderId);
								order.setCountry(country);
								order.setAccountName(accountName);
								order.setShipmentId(shipmentId);
								order.setMarketplaceName(cells[11]);
								order.setMerchantFulfillmentId(cells[16]);
								order.setPostedDate(sdf.parse(posted));
								order.setMerchantOrderId(merchantOrderId);
								order1Map.put(orderId, order);
								order.setType(type);
								String amazonOrderItemCode = cells[18];
								String sku =cells[21];
								item = new SettlementReportItem();
								item.setOrder(order);
								order.getItems().add(item);
								item.setSku(sku);
								item.setAmazonOrderItemCode(amazonOrderItemCode);
								item.setMerchantAdjustmentItemId(merchantAdjustmentItemId);
							}	
							String amazonOrderItemCode = cells[18];
							int flag = 0;
							for (SettlementReportItem itemTemp : order.getItems()) {
								if(itemTemp.getAmazonOrderItemCode().equals(amazonOrderItemCode)){
									item = itemTemp;
									flag = 1;
									break;
								}
							}
							if(flag==0){
								String sku =cells[21];
								item = new SettlementReportItem();
								item.setOrder(order);
								order.getItems().add(item);
								item.setSku(sku);
								item.setAmazonOrderItemCode(amazonOrderItemCode);
								item.setMerchantAdjustmentItemId(merchantAdjustmentItemId);
							}
							String priceType = cells[23];
							String feeType = cells[25];
							if(StringUtils.isNotEmpty(priceType)){
								String priceAmount = cells[24];
								if ("Principal".equals(priceType)) {
									if(item.getPrincipal()!=null){
										item.setPrincipal(item.getPrincipal().add(new BigDecimal(replacePrice(priceAmount))));
									}else{
										item.setPrincipal(new BigDecimal(replacePrice(priceAmount)));
									}
								} else if ("Shipping".equals(priceType) || "ReturnShipping".equals(priceType)) {
									if(item.getShipping()!=null){
										item.setShipping(item.getShipping().add(new BigDecimal(replacePrice(priceAmount))));
									}else{
										item.setShipping(new BigDecimal(replacePrice(priceAmount)));
									}
								}else if ("COD".equals(priceType)) {
									if(item.getCod()!=null){
										item.setCod(item.getCod().add(new BigDecimal(replacePrice(priceAmount))));
									}else{
										item.setCod(new BigDecimal(replacePrice(priceAmount)));
									}
								}else if ("GiftWrap".equals(priceType)) {
									if(item.getGiftWrap()!=null){
										item.setGiftWrap(item.getGiftWrap().add(new BigDecimal(replacePrice(priceAmount))));
									}else{
										item.setGiftWrap(new BigDecimal(replacePrice(priceAmount)));
									}
								}else if ("Goodwill".equals(priceType)) {
									if(item.getGoodwill()!=null){
										item.setGoodwill(item.getGoodwill().add(new BigDecimal(replacePrice(priceAmount))));
									}else{
										item.setGoodwill(new BigDecimal(replacePrice(priceAmount)));
									}
								}else if ("RestockingFee".equals(priceType)){
									if(item.getRestockingFee()!=null){
										item.setRestockingFee(item.getRestockingFee().add(new BigDecimal(replacePrice(priceAmount))));
									}else{
										item.setRestockingFee(new BigDecimal(replacePrice(priceAmount)));
									}
								}else if ("Tax".equals(priceType)) {
									if(item.getTax()!=null){
										item.setTax(item.getTax().add(new BigDecimal(replacePrice(priceAmount))));
									}else{
										item.setTax(new BigDecimal(replacePrice(priceAmount)));
									}
								}else if ("SalesTaxServiceFee".equals(feeType)) {
									if(item.getSalesTaxServiceFee()!=null){
										item.setSalesTaxServiceFee(item.getSalesTaxServiceFee().add(new BigDecimal(replacePrice(priceAmount))));
									}else{
										item.setSalesTaxServiceFee(new BigDecimal(replacePrice(priceAmount)));
									}
								}else if ("ShippingTax".equals(feeType)) {
									if(item.getShippingTax()!=null){
										item.setShippingTax(item.getShippingTax().add(new BigDecimal(replacePrice(priceAmount))));
									}else{
										item.setShippingTax(new BigDecimal(replacePrice(priceAmount)));
									}
								} else {
									if(item.getOtherFee()!=null){
										item.setOtherFee(item.getOtherFee().add(new BigDecimal(replacePrice(priceAmount))));
									}else{
										item.setOtherFee(new BigDecimal(replacePrice(priceAmount)));
									}
									if (!"PointsGranted".equals(priceType)) {
										logger.warn("Refund：" + country+":"+orderId+"出现新的费用类型:"+priceType +",已统计到OtherFee类型中" + txtPath);
									}
								}
							}else if(StringUtils.isNotEmpty(feeType)){
								String feeAmount = cells[26];
								if ("Cross-Border Fulfillment Fee"
										.equals(feeType)) {
									if(item.getCrossBorderFulfillmentFee()==null){
										item.setCrossBorderFulfillmentFee(new BigDecimal(replacePrice(feeAmount)));
									}else{
										item.setCrossBorderFulfillmentFee(item.getCrossBorderFulfillmentFee().add(new BigDecimal(replacePrice(feeAmount))));
									}
								} else if ("FBAPerUnitFulfillmentFee"
										.equals(feeType)) {
									if(item.getFbaPerUnitFulfillmentFee()==null){
										item.setFbaPerUnitFulfillmentFee(new BigDecimal(replacePrice(feeAmount)));
									}else{
										item.setFbaPerUnitFulfillmentFee(item.getFbaPerUnitFulfillmentFee().add(new BigDecimal(replacePrice(feeAmount))));
									}
								}else if ("FBAPerOrderFulfillmentFee"
										.equals(feeType)) {
									if(item.getFbaPerOrderFulfillmentFee()==null){
										item.setFbaPerOrderFulfillmentFee(new BigDecimal(replacePrice(feeAmount)));
									}else{
										item.setFbaPerOrderFulfillmentFee(item.getFbaPerOrderFulfillmentFee().add(new BigDecimal(replacePrice(feeAmount))));
									}
								} else if ("FBAWeightBasedFee"
										.equals(feeType)) {
									if(item.getFbaWeightBasedFee()==null){
										item.setFbaWeightBasedFee(new BigDecimal(replacePrice(feeAmount)));
									}else{
										item.setFbaWeightBasedFee(item.getFbaWeightBasedFee().add(new BigDecimal(replacePrice(feeAmount))));
									}
								} else if ("Commission".equals(feeType)) {
									if(item.getCommission()==null){
										item.setCommission(new BigDecimal(replacePrice(feeAmount)));
									}else{
										item.setCommission(item.getCommission().add(new BigDecimal(replacePrice(feeAmount))));
									}
								} else if ("ShippingChargeback"
										.equals(feeType)) {
									if(item.getShippingChargeback()==null){
										item.setShippingChargeback(new BigDecimal(replacePrice(feeAmount)));
									}else{
										item.setShippingChargeback(item.getShippingChargeback().add(new BigDecimal(replacePrice(feeAmount))));
									}
								} else if ("GiftwrapChargeback"
										.equals(feeType)) {
									if(item.getGiftwrapChargeback()==null){
										item.setGiftwrapChargeback(new BigDecimal(replacePrice(feeAmount)));
									}else{
										item.setGiftwrapChargeback(item.getGiftwrapChargeback().add(new BigDecimal(replacePrice(feeAmount))));
									}
								} else if ("RefundCommission"
										.equals(feeType)) {
									if(item.getRefundCommission()==null){
										item.setRefundCommission(new BigDecimal(replacePrice(feeAmount)));
									}else{
										item.setRefundCommission(item.getRefundCommission().add(new BigDecimal(replacePrice(feeAmount))));
									}
								}else if ("CODFee"
										.equals(feeType)) {
									if(item.getCodFee()==null){
										item.setCodFee(new BigDecimal(replacePrice(feeAmount)));
									}else{
										item.setCodFee(item.getCodFee().add(new BigDecimal(replacePrice(feeAmount))));
									}
								}else if ("ShippingHB".equals(feeType)) {
									if(item.getShippingHb()==null){
										item.setShippingHb(new BigDecimal(replacePrice(feeAmount)));
									}else{
										item.setShippingHb(item.getShippingHb().add(new BigDecimal(replacePrice(feeAmount))));
									}
								}else if ("Tax".equals(feeType)) {
									if(item.getTax()==null){
										item.setTax(new BigDecimal(replacePrice(feeAmount)));
									}else{
										item.setTax(item.getTax().add(new BigDecimal(replacePrice(feeAmount))));
									}
								}else if ("SalesTaxServiceFee".equals(feeType)) {
									if(item.getSalesTaxServiceFee()==null){
										item.setSalesTaxServiceFee(new BigDecimal(replacePrice(feeAmount)));
									}else{
										item.setSalesTaxServiceFee(item.getSalesTaxServiceFee().add(new BigDecimal(replacePrice(feeAmount))));
									}
								}else if ("ShippingTax".equals(feeType)) {
									if(item.getShippingTax()==null){
										item.setShippingTax(new BigDecimal(replacePrice(feeAmount)));
									}else{
										item.setShippingTax(item.getShippingTax().add(new BigDecimal(replacePrice(feeAmount))));
									}
								}else{
									if(item.getOtherFee()!=null){
										item.setOtherFee(item.getOtherFee().add(new BigDecimal(replacePrice(feeAmount))));
									}else{
										item.setOtherFee(new BigDecimal(replacePrice(feeAmount)));
									}
									if (!"PointsGranted".equals(feeType)) {
										logger.warn(country+":"+orderId+"出现新的费用类型:"+feeType +",已统计到OtherFee类型中" + txtPath);
									}
								}
							}else if(StringUtils.isEmpty(priceType)&&StringUtils.isEmpty(feeType)){
								if(StringUtils.isNotEmpty(cells[32])){
									if(item.getPromotion()==null){
										item.setPromotion(new BigDecimal(replacePrice(cells[32])));
									}else{
										item.setPromotion(item.getPromotion().add(new BigDecimal(replacePrice(cells[32]))));
									}
								}else{
									logger.warn(type+country+":"+orderId+"读取有误!!!" + txtPath);
								}
							}
						}else if ("Subscription Fee".equals(type)){
							SettlementReportOrder order  = new SettlementReportOrder();
							order.setCountry(country);
							order.setAccountName(accountName);
							order.setSettlementId(setId);
							order.setType(type);
							order.setAdjustmentId(cells[9]);
							String posted = cells[17];
							posted = posted.replace("T", " ");
							posted = posted.substring(0, posted.indexOf("+"));
							order.setAddTime(now);
							order.setPostedDate(sdf.parse(posted));
							item = new SettlementReportItem();
							item.setOrder(order);
							order.getItems().add(item);
							item.setOtherFee(new BigDecimal(replacePrice(cells[cells.length-1]).trim()));
							settlementReportOrderDao.save(order);
						}else if ("ServiceFee".equals(type)){
							String detail = "";	//标记ServiceFee具体内容(如广告费)
							if (StringUtils.isNotEmpty(cells[25])) {
								detail = "_" + cells[25];
							}
							SettlementReportOrder order  = new SettlementReportOrder();
							order.setCountry(country);
							order.setAccountName(accountName);
							order.setSettlementId(setId);
							order.setType(type + detail);
							order.setAdjustmentId(cells[9]);
							String posted = cells[17];
							posted = posted.replace("T", " ");
							posted = posted.substring(0, posted.indexOf("+"));
							order.setAddTime(now);
							order.setPostedDate(sdf.parse(posted));
							item = new SettlementReportItem();
							item.setOrder(order);
							order.getItems().add(item);
							if (StringUtils.isNotEmpty(cells[26])) {
								item.setOtherFee(new BigDecimal(replacePrice(cells[26]).trim()));
							}
							if (StringUtils.isNotEmpty(cells[28])) {
								item.setOtherFee(item.getOtherFee().add(new BigDecimal(replacePrice(cells[28]).trim())));
							}
							settlementReportOrderDao.save(order);
						}else if ("Lightning Deal Fee".equals(type)){
							SettlementReportOrder order  = new SettlementReportOrder();
							order.setCountry(country);
							order.setAccountName(accountName);
							order.setSettlementId(setId);
							order.setType(type);
							order.setAdjustmentId(cells[9]);
							String posted = cells[17];
							posted = posted.replace("T", " ");
							posted = posted.substring(0, posted.indexOf("+"));
							order.setAddTime(now);
							order.setPostedDate(sdf.parse(posted));
							item = new SettlementReportItem();
							item.setOrder(order);
							order.getItems().add(item);
							if (StringUtils.isNotEmpty(cells[26])) {
								item.setOtherFee(new BigDecimal(replacePrice(cells[26]).trim()));
							}
							if (StringUtils.isNotEmpty(cells[28])) {
								item.setOtherFee(item.getOtherFee().add(new BigDecimal(replacePrice(cells[28]).trim())));
							}
							settlementReportOrderDao.save(order);
						}else{
							SettlementReportOrder order  = new SettlementReportOrder();
							order.setCountry(country);
							order.setAccountName(accountName);
							order.setSettlementId(setId);
							order.setAmazonOrderId(orderId);
							order.setType(type);
							order.setAdjustmentId(cells[9]);
							String posted = cells[17];
							posted = posted.replace("T", " ");
							posted = posted.substring(0, posted.indexOf("+"));
							order.setAddTime(now);
							order.setPostedDate(sdf.parse(posted));
							item = new SettlementReportItem();
							item.setOrder(order);
							order.getItems().add(item);
							String sku =cells[21];
							item.setSku(sku);
							String quantityStr =cells[22];
							if(StringUtils.isNotEmpty(quantityStr)){
								item.setQuantity(Integer.parseInt(quantityStr));
							}
							if (StringUtils.isNotEmpty(cells[cells.length-1])) {
								item.setOtherFee(new BigDecimal(replacePrice(cells[cells.length-1]).trim()));
							}
							settlementReportOrderDao.save(order);
						}
					}
					i++;
				}
				if(orderMap.size()>0){
					settlementReportOrderDao.save(Lists.newArrayList(orderMap.values()));
				}
				if(order1Map.size()>0){
					settlementReportOrderDao.save(Lists.newArrayList(order1Map.values()));
				}
				return true;
			} catch (Exception e) {
				WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, "解析"+country+"结算报表txt出错了！" + txtPath);
				logger.warn("解析txt出错了！" + txtPath,e);
			}
		}
		return false;
	}
	
	@Transactional(readOnly = false)
	public void saveAndUpdateFee(){
		//更新订单费用
		String sql = "INSERT INTO `amazoninfo_sale_report` (sku,sales_no_tax,fee,fee_other,DATE,country,fee_quantity,order_type,account_name)  SELECT o1.sku,SUM(o1.sales) AS sales_no_tax,SUM(o1.amazon_fee) AS fee,SUM(o1.order_other_fee) AS fee_other,STR_TO_DATE(DATE_FORMAT(o.`purchase_date`,'%Y%m%d'),'%Y%m%d') AS DATE,REPLACE(REPLACE(REPLACE(o.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','') AS country,SUM(quantity) AS fee_quantity,1,o.`account_name` FROM amazoninfo_order o ,(SELECT o.`amazon_order_id`,o.`account_name`,t.`sku`,SUM(t.quantity) AS quantity,SUM(IFNULL(t.`shipping`,0) + IFNULL(t.principal,0)+IFNULL(t.`promotion`,0) + IFNULL(t.`shipping_chargeback`,0)) AS sales,SUM(IFNULL(t.`fba_per_unit_fulfillment_fee`,0) + IFNULL(t.`fba_weight_based_fee`,0) + IFNULL(t.`commission`,0)) AS amazon_fee, "+
			" SUM(IFNULL(t.`cod`,0) + IFNULL(t.`gift_wrap`,0) + IFNULL(t.`goodwill`,0)+ "+
			" IFNULL(t.`cross_border_fulfillment_fee`,0) + IFNULL(t.`giftwrap_chargeback`,0) + IFNULL(t.`refund_commission`,0)+ "+
			" IFNULL(t.`restocking_fee`,0)  + IFNULL(t.`cod_fee`,0) + IFNULL(t.`other_fee`,0) + IFNULL(t.`shipping_hb`,0)+ "+
			" IFNULL(t.`shipment_fee`,0) + IFNULL(t.`fba_per_order_fulfillment_fee`,0) + IFNULL(t.`tax`,0) + IFNULL(t.`sales_tax_service_fee`,0) + IFNULL(t.`shipping_tax`,0) ) AS order_other_fee  FROM amazoninfo_financial o,amazoninfo_financial_item t WHERE o.id = t.`order_id`  AND o.`type` = 'Order' AND o.posted_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -60 DAY) GROUP BY o.`amazon_order_id`,t.`sku`) o1 "+
			" WHERE o.`amazon_order_id` = o1.amazon_order_id AND o1.account_name=o.`account_name` AND o.purchase_date>=DATE_ADD(CURRENT_DATE(),INTERVAL -60 DAY) and o1.sku is not null and o1.sku!='' GROUP BY o1.sku,DATE,o.`sales_channel`,o.`account_name` "+
			" ON DUPLICATE KEY UPDATE `sales_no_tax` = VALUES(sales_no_tax),`fee` = VALUES(fee),`fee_other` = VALUES(fee_other),fee_quantity = VALUES(fee_quantity)";
		settlementReportOrderDao.updateBySql(sql,null);
		
		//更新退款费用
		sql = "INSERT INTO `amazoninfo_sale_report` (sku,DATE,country,refund,order_type,account_name) SELECT o1.sku,STR_TO_DATE(DATE_FORMAT(o.`purchase_date`,'%Y%m%d'),'%Y%m%d') AS DATE,REPLACE(REPLACE(REPLACE(o.`sales_channel`,'Amazon.com.',''),'Amazon.',''),'co.','') AS country,refund,1,o.`account_name` FROM amazoninfo_order o ,(SELECT o.`amazon_order_id`,o.`account_name`,t.`sku`,SUM(IFNULL(t.`principal`,0) + IFNULL(t.`shipping`,0) + IFNULL(t.`cod`,0) + IFNULL(t.`gift_wrap`,0) + IFNULL(t.`goodwill`,0) "+
				" + IFNULL(t.`cross_border_fulfillment_fee`,0) + IFNULL(t.`fba_per_unit_fulfillment_fee`,0) + IFNULL(t.`fba_weight_based_fee`,0) "+
				" + IFNULL(t.`commission`,0) + IFNULL(t.`shipping_chargeback`,0) + IFNULL(t.`giftwrap_chargeback`,0) + IFNULL(t.`refund_commission`,0) "+
				" + IFNULL(t.`restocking_fee`,0) + IFNULL(t.`promotion`,0) + IFNULL(t.`cod_fee`,0) + IFNULL(t.`other_fee`,0) + IFNULL(t.`shipping_hb`,0) "+
				" + IFNULL(t.`shipment_fee`,0) + IFNULL(t.`fba_per_order_fulfillment_fee`,0) + IFNULL(t.`tax`,0) + IFNULL(t.`sales_tax_service_fee`,0) + IFNULL(t.`shipping_tax`,0)) AS refund  FROM amazoninfo_financial o,amazoninfo_financial_item t WHERE o.id = t.`order_id`  AND o.`type` = 'Refund' GROUP BY o.`amazon_order_id`,t.`sku`) o1  "+
				" WHERE o.`amazon_order_id` = o1.amazon_order_id AND o1.account_name=o.`account_name` and o1.sku is not null and o1.sku!=''  GROUP BY o1.sku,DATE,o.`sales_channel`,o.`account_name` "+
				" ON DUPLICATE KEY UPDATE `refund` = VALUES(refund)";
		settlementReportOrderDao.updateBySql(sql,null);
	}
	

	public boolean isNotExist(SettlementReportOrder order) {
		DetachedCriteria dc = settlementReportOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("amazonOrderId", order.getAmazonOrderId()));
		dc.add(Restrictions.eq("adjustmentId", order.getAdjustmentId()));
		return settlementReportOrderDao.count(dc) == 0;
	}
	
	public List<Object[]> find(List<String> settlementIds) {
		List<Object[]> list = Lists.newArrayList();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT o.`settlement_id`,o.`country`,DATE_FORMAT(MIN(o.`posted_date`),'%Y-%m-%d') AS START,DATE_FORMAT(MAX(o.`posted_date`),'%Y-%m-%d') AS END," +
				" SUM(CASE WHEN o.`type`='Order' THEN IFNULL(t.`principal`,0) END) AS order_principal_total," +
	
				" SUM(CASE WHEN o.`type`='Order' THEN IFNULL(t.`fba_per_unit_fulfillment_fee`,0) + IFNULL(t.`fba_weight_based_fee`,0) + IFNULL(t.`commission`,0) END) AS amazon_fee," +
	
				" SUM(CASE WHEN o.`type`='Order' THEN IFNULL(t.`shipping`,0) + IFNULL(t.`cod`,0) + IFNULL(t.`gift_wrap`,0) + IFNULL(t.`goodwill`,0)" +
				" + IFNULL(t.`cross_border_fulfillment_fee`,0) + IFNULL(t.`shipping_chargeback`,0) + IFNULL(t.`giftwrap_chargeback`,0) + IFNULL(t.`refund_commission`,0)" +
				" + IFNULL(t.`restocking_fee`,0) + IFNULL(t.`promotion`,0) + IFNULL(t.`cod_fee`,0) + IFNULL(t.`other_fee`,0) + IFNULL(t.`shipping_hb`,0)" +
				" + IFNULL(t.`shipment_fee`,0) + IFNULL(t.`fba_per_order_fulfillment_fee`,0) + IFNULL(t.`tax`,0) + IFNULL(t.`sales_tax_service_fee`,0) + IFNULL(t.`shipping_tax`,0) END) AS order_other_fee," +
		
				" SUM(CASE WHEN o.`type`='Refund' THEN IFNULL(t.`principal`,0) + IFNULL(t.`shipping`,0) + IFNULL(t.`cod`,0) + IFNULL(t.`gift_wrap`,0) + IFNULL(t.`goodwill`,0)" +
				" + IFNULL(t.`cross_border_fulfillment_fee`,0) + IFNULL(t.`fba_per_unit_fulfillment_fee`,0) + IFNULL(t.`fba_weight_based_fee`,0)" +
				" + IFNULL(t.`commission`,0) + IFNULL(t.`shipping_chargeback`,0) + IFNULL(t.`giftwrap_chargeback`,0) + IFNULL(t.`refund_commission`,0)" +
				" + IFNULL(t.`restocking_fee`,0) + IFNULL(t.`promotion`,0) + IFNULL(t.`cod_fee`,0) + IFNULL(t.`other_fee`,0) + IFNULL(t.`shipping_hb`,0)" +
				" + IFNULL(t.`shipment_fee`,0) + IFNULL(t.`fba_per_order_fulfillment_fee`,0) END) AS refund_total," +
	
				" SUM(CASE WHEN o.`type`='Storage Fee' THEN IFNULL(t.`other_fee`,0) END) AS storage_fee," +
	
				" SUM(CASE WHEN o.`type`!='Order' AND o.`type`!='Refund' AND o.`type`!='Storage Fee' THEN " +
				" IFNULL(t.`principal`,0) + IFNULL(t.`shipping`,0) + IFNULL(t.`cross_border_fulfillment_fee`,0) + IFNULL(t.`fba_per_unit_fulfillment_fee`,0) " +
				" + IFNULL(t.`fba_weight_based_fee`,0) " +
				" + IFNULL(t.`commission`,0) + IFNULL(t.`shipping_chargeback`,0) + IFNULL(t.`giftwrap_chargeback`,0) + IFNULL(t.`refund_commission`,0) " +
				" + IFNULL(t.`restocking_fee`,0) " +
				" + IFNULL(t.`promotion`,0) + IFNULL(t.`cod`,0) + IFNULL(t.`cod_fee`,0) + IFNULL(t.`other_fee`,0) + IFNULL(t.`shipping_hb`,0) " +
				" + IFNULL(t.`shipment_fee`,0) + IFNULL(t.`fba_per_order_fulfillment_fee`,0) + IFNULL(t.`gift_wrap`,0) + IFNULL(t.`goodwill`,0)  END) AS other_total" +
	
				" FROM settlementreport_item t JOIN settlementreport_order o ON t.`order_id` = o.`id` " +
				" WHERE o.`settlement_id` IS NOT NULL  ");
		if (settlementIds != null && settlementIds.size() > 0) {
			sql.append(" and o.`settlement_id` in :p1");
		} else {
			return list;
		}
		sql.append(" GROUP BY o.`settlement_id` order by o.`posted_date` DESC ");
		if(settlementIds != null && settlementIds.size() > 0){
			list = settlementReportOrderDao.findBySql(sql.toString(), new Parameter(settlementIds));
		} else {
			list = settlementReportOrderDao.findBySql(sql.toString());
		}
		return list;
	}
	
	public List<Object[]> find(String country, List<String> settlementIds) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT o.`settlement_id`,o.`country`,date_format(MIN(o.`posted_date`),'%Y-%m-%d'),date_format(MAX(o.`posted_date`),'%Y-%m-%d')," +
			" SUM(IFNULL(t.`principal`,0) + IFNULL(t.`shipping`,0) + IFNULL(t.`cod`,0) + IFNULL(t.`gift_wrap`,0) + IFNULL(t.`goodwill`,0)) AS a," +
			" SUM(IFNULL(t.`cross_border_fulfillment_fee`,0) + IFNULL(t.`fba_per_unit_fulfillment_fee`,0)" +
			" + IFNULL(t.`fba_weight_based_fee`,0)" +
			" + IFNULL(t.`commission`,0) + IFNULL(t.`shipping_chargeback`,0) + IFNULL(t.`giftwrap_chargeback`,0) + IFNULL(t.`refund_commission`,0)" +
			" + IFNULL(t.`restocking_fee`,0) + IFNULL(t.`promotion`,0) + IFNULL(t.`cod_fee`,0) + IFNULL(t.`other_fee`,0) + IFNULL(t.`shipping_hb`,0)" +
			" + IFNULL(t.`shipment_fee`,0) + IFNULL(t.`fba_per_order_fulfillment_fee`,0) ) AS b" +
			" FROM settlementreport_item t JOIN settlementreport_order o ON t.`order_id` = o.`id` " +
			"WHERE o.`settlement_id` IS NOT NULL ");
		if (StringUtils.isNotBlank(country) && settlementIds.size() == 0) {
			sql.append(" and o.`country` = :p1");
		} else if (settlementIds.size() > 0) {
			sql.append(" and o.`settlement_id` in :p1");
		}
		sql.append(" GROUP BY o.`settlement_id` order by o.`posted_date` DESC ");
		List<Object[]> list = Lists.newArrayList();
		if (StringUtils.isNotBlank(country) && settlementIds.size() == 0) {
			list = settlementReportOrderDao.findBySql(sql.toString(), new Parameter(country));
		} else if(settlementIds.size() > 0){
			list = settlementReportOrderDao.findBySql(sql.toString(), new Parameter(settlementIds));
		} else {
			list = settlementReportOrderDao.findBySql(sql.toString());
		}
		return list;
	}
	
	public List<Object[]> findDetail(String settlementId) {
		String sql = "SELECT o.`amazon_order_id`,o.`type`,SUM(IFNULL(t.`principal`,0) + IFNULL(t.`shipping`,0) + IFNULL(t.`cod`,0) + IFNULL(t.`gift_wrap`,0) + IFNULL(t.`goodwill`,0)) AS sales,"+
				"SUM(IFNULL(t.`cross_border_fulfillment_fee`,0) + IFNULL(t.`fba_per_unit_fulfillment_fee`,0)"+
				"+ IFNULL(t.`fba_weight_based_fee`,0)"+
			    "+ IFNULL(t.`commission`,0) + IFNULL(t.`shipping_chargeback`,0) + IFNULL(t.`giftwrap_chargeback`,0) + IFNULL(t.`refund_commission`,0)"+
			    "+ IFNULL(t.`restocking_fee`,0) + IFNULL(t.`promotion`,0) + IFNULL(t.`cod_fee`,0) + IFNULL(t.`other_fee`,0) + IFNULL(t.`shipping_hb`,0)"+
			    "+ IFNULL(t.`shipment_fee`,0) + IFNULL(t.`fba_per_order_fulfillment_fee`,0) ) AS fee"+
			    " FROM settlementreport_item t JOIN settlementreport_order o ON t.`order_id`=o.`id` "+
			    " WHERE o.`settlement_id`=:p1 GROUP BY o.`amazon_order_id`,o.`type` ORDER BY o.`type`,o.`amazon_order_id`";
		List<Object[]> list = settlementReportOrderDao.findBySql(sql, new Parameter(settlementId));
		return list;
	}
	
	public List<Object[]> findOrderDetail(String settlementId) {
		String sql = "SELECT o.`amazon_order_id`,o.`type`,"+
				"SUM(IFNULL(t.`principal`,0)), SUM(IFNULL(t.`shipping`,0)),SUM(IFNULL(t.`cod`,0)),"+
				"SUM(IFNULL(t.`cross_border_fulfillment_fee`,0)),SUM(IFNULL(t.`fba_per_unit_fulfillment_fee`,0)),"+
				"SUM(IFNULL(t.`fba_weight_based_fee`,0)), SUM(IFNULL(t.`commission`,0)),SUM(IFNULL(t.`shipping_chargeback`,0)),"+
				"SUM(IFNULL(t.`giftwrap_chargeback`,0)),SUM(IFNULL(t.`refund_commission`,0)),"+
				"SUM(IFNULL(t.`restocking_fee`,0)),SUM(IFNULL(t.`promotion`,0)),SUM(IFNULL(t.`cod_fee`,0)),SUM(IFNULL(t.`other_fee`,0)),"+
				"SUM(IFNULL(t.`shipping_hb`,0)), SUM(IFNULL(t.`shipment_fee`,0)),SUM(IFNULL(t.`fba_per_order_fulfillment_fee`,0)),"+
				"SUM(IFNULL(t.`gift_wrap`,0)), SUM(IFNULL(t.`goodwill`,0))"+
				"FROM settlementreport_item t JOIN settlementreport_order o ON t.`order_id`=o.`id` "+
				"WHERE o.`settlement_id`=:p1 GROUP BY o.`amazon_order_id`,o.`type` ORDER BY o.`type`,o.`amazon_order_id`";
		List<Object[]> list = settlementReportOrderDao.findBySql(sql, new Parameter(settlementId));
		return list;
	}
	
	public List<Object[]> findTitle(String settlementId) {
		String sql = "SELECT o.`country`,o.`marketplace_name`,date_format(MIN(o.`posted_date`),'%Y%m%d'),date_format(MAX(o.`posted_date`),'%Y%m%d') "+
				" FROM settlementreport_order o WHERE o.`settlement_id`=:p1 AND o.`country` IS NOT NULL";
		List<Object[]> list = settlementReportOrderDao.findBySql(sql, new Parameter(settlementId));
		return list;
	}
	
	/**
	 * 根据country找到对应的settlementId
	 * @return
	 */
	public Map<String, List<String>> coverSettlementId(Date startTime, Date endTime) {
		Map<String, List<String>> resultMap = Maps.newHashMap();
		String sql = "SELECT o.`settlement_id`,o.`country` FROM settlementreport_order o "+
				" WHERE o.`country` IS NOT NULL  AND o.`settlement_id` IS NOT NULL " +
				" AND o.`posted_date`>:p1 AND o.`posted_date`<:p2 GROUP BY o.`settlement_id`";
		List<Object[]> list = settlementReportOrderDao.findBySql(sql, new Parameter(startTime, endTime));
		List<String> totalSettlementIds = Lists.newArrayList();
		for (Object[] objects : list) {
			totalSettlementIds.add(objects[0].toString());
		}
		resultMap.put("total", totalSettlementIds);
		return resultMap;
	}
	
	/**
	 * 根据settlementId找到对应的country
	 * @return
	 */
	public Map<String, String> coverCountry(Date startTime, Date endTime) {
		Map<String, String> resultMap = Maps.newHashMap();
		String sql = "SELECT o.`settlement_id`,o.`country` FROM settlementreport_order o "+
				" WHERE o.`settlement_id` IS NOT NULL   AND o.`country` IS NOT NULL " +
				" AND o.`posted_date`>:p1 AND o.`posted_date`<:p2 GROUP BY o.`settlement_id`";
		List<Object[]> list = settlementReportOrderDao.findBySql(sql, new Parameter(startTime, endTime));
		for (Object[] objects : list) {
			resultMap.put(objects[0].toString(), objects[1].toString());
		}
		return resultMap;
	}
	
	public List<SettlementReportOrder> findStorageFee(String day) {
		List<SettlementReportOrder> rs = Lists.newArrayList();
		String sql = "SELECT o.`country`,DATE_FORMAT(MIN(o.`posted_date`),'%Y%m%d'),DATE_FORMAT(MAX(o.`posted_date`),'%Y%m%d') "+
				" ,SUM(CASE WHEN o.`type`='Storage Fee' THEN IFNULL(t.`other_fee`,0) END) AS storage_fee"+
				" FROM settlementreport_item t JOIN settlementreport_order o ON t.`order_id` = o.`id`"+
				" WHERE DATE_FORMAT(o.`add_time`,'%Y%m%d')='"+day+"'  GROUP BY o.`country`";
		List<Object[]> list = settlementReportOrderDao.findBySql(sql);
		for (Object[] obj : list) {
			String country = obj[0].toString();
			String minDay = obj[1].toString();
			String maxDay = obj[2].toString();
			String total = obj[3]==null?"0":obj[3].toString();
			SettlementReportOrder order = new SettlementReportOrder();
			order.setCountry(country);
			//借用字段临时存储信息
			order.setShipmentId(minDay);
			order.setAdjustmentId(maxDay);
			order.setAmazonOrderId(total);
			rs.add(order);
		}
		return rs;
	}
	
	private static String replacePrice(String str){
		if(str.contains(".")){
			return str.replace(",", "");
		} else if(str.contains(",")){
			String[] arr = str.split(",");
			if (arr[arr.length-1] != null && arr[arr.length-1].length()==2) {
				str = "";
				StringBuilder buf=new StringBuilder();
				for (int i = 0; i < arr.length; i++) {
					if (i == arr.length - 1) {
						buf.append("." + arr[i]);
					} else {
						buf.append(arr[i]);
					}
				}
				str=buf.toString();
			} else {
				str = str.replace(",", "");
			}
		}
		return str;
	}
	
	
	public Map<String,List<Object[]>>  compareCommission(Date start,Date end){
		Map<String,List<Object[]>> map=Maps.newHashMap();
		String sql="  SELECT r.`amazon_order_id`,t.sku,r.`country`,ROUND(SUM(-IFNULL(t.`commission`,0))*100/SUM(t.`principal`+IFNULL(t.promotion,0))) commission,(CASE WHEN r.`country`='jp' THEN ROUND(e.commission_pcent*1.08) ELSE e.commission_pcent END) pcent,DATE_FORMAT(r.`posted_date`,'%Y%m%d') posted_date,SUM(t.quantity) quantity,SUM(t.`principal`) principal,SUM(IFNULL(t.promotion,0)) promotion "+ 
				" FROM settlementreport_order r FORCE INDEX(posted_date) JOIN settlementreport_item t ON r.id=t.`order_id`  "+
				" JOIN  psi_sku p ON r.country=p.country AND t.sku=p.`sku` AND p.del_flag='0' "+
				" JOIN psi_product_eliminate e ON e.product_id=p.product_id AND e.product_name=p.product_name AND e.country=p.country  AND e.color=p.color AND e.del_flag='0' "+
				" WHERE r.`posted_date`>=:p1 AND r.`posted_date`<=:p2 AND  r.TYPE='Order' AND  e.commission_pcent IS NOT NULL "+
				" GROUP BY r.`amazon_order_id`,t.sku,r.`country`,r.`posted_date`,e.commission_pcent "+
				"  HAVING commission<>pcent AND commission>0 ";
		
		List<Object[]> list=settlementReportOrderDao.findBySql(sql,new Parameter(start,end));
		for (Object[] obj: list) {
			List<Object[]> temp=map.get(obj[2].toString());
			if(temp==null){
				temp=Lists.newArrayList();
				map.put(obj[2].toString(),temp);
			}
			temp.add(obj);
		}
		return map;
	}
	
	public Map<String,List<Object[]>>  compareCommission2(Date start,Date end){
		Map<String,List<Object[]>> map=Maps.newHashMap();
		String sql=" SELECT r.sku,r.`country`,r.commission,r.pcent,COUNT(*) FROM ( SELECT distinct t.sku,r.`country`,ROUND(SUM(-IFNULL(t.`commission`,0))*100/SUM(t.`principal`+IFNULL(t.shipping,0)+IFNULL(t.promotion,0)+IFNULL(t.shipping_chargeback,0)),2) commission,(CASE WHEN r.`country`='jp' THEN ROUND(e.commission_pcent*1.08) ELSE e.commission_pcent END) pcent "+ 
				" FROM settlementreport_order r FORCE INDEX(posted_date) JOIN settlementreport_item t ON r.id=t.`order_id`  "+
				" JOIN  psi_sku p ON r.country=p.country AND t.sku=p.`sku` AND p.del_flag='0' "+
				" JOIN psi_product_eliminate e ON e.product_id=p.product_id AND e.product_name=p.product_name AND e.country=p.country  AND e.color=p.color AND e.del_flag='0' "+
				" WHERE r.`posted_date`>=:p1 AND r.`posted_date`<=:p2 AND  r.TYPE='Order' AND  e.commission_pcent IS NOT NULL "+
				" GROUP BY r.`amazon_order_id`,t.sku,r.`country`,r.`posted_date`,e.commission_pcent "+
				"  HAVING commission-pcent>1 AND commission>0 ) r GROUP BY r.sku,r.`country`,r.commission,r.pcent ";
		
		List<Object[]> list=settlementReportOrderDao.findBySql(sql,new Parameter(start,end));
		for (Object[] obj: list) {
			List<Object[]> temp=map.get(obj[1].toString());
			if(temp==null){
				temp=Lists.newArrayList();
				map.put(obj[1].toString(),temp);
			}
			temp.add(obj);
		}
		return map;
	}
	
	//当天各平台是否有新增仓储费信息list<country>
	public List<String> findStorageFeeCountryList(){
		String sql = "SELECT t.`country` FROM `settlementreport_order` t WHERE t.`type`='Storage Fee' AND DATE_FORMAT(t.`add_time`,'%Y-%m-%d') = DATE_FORMAT(NOW(),'%Y-%m-%d') ";
		List<String> list = settlementReportOrderDao.findBySql(sql);
		return list;
	}
}
