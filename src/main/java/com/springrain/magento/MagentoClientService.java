package com.springrain.magento;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.entity.Image;

/**
 * inateck官网magento接口服务类
 * @author lee
 * @date 2016-9-14
 */
public class MagentoClientService {
	
	private static Logger logger = LoggerFactory.getLogger(MagentoClientService.class);
	
	//"http://www.inatecktest.com/api/v2_soap"
	private static String interfaceUrl = Global.getConfig("inateck.magento.serviceurl");
	private static String username = Global.getConfig("inateck.magento.username");//"gone"
	private static String apiKey = Global.getConfig("inateck.magento.apiKey");//"v7h07E9a5J3z96#Q"
	//"C:/Program Files/Apache Software Foundation/Tomcat 6.0/webapps"
	private static String basePath = Global.getConfig("erp.basePath");

	public static BindingStub getBindingStub() {
		BindingStub bindingStub = null;
		int errorNum = 0;
		while(bindingStub==null){
			try {
				bindingStub = (BindingStub) new MagentoServiceLocator().getPort(new URL(interfaceUrl));
				bindingStub.setTimeout(3 * 60 * 1000);// 设置接口超时时间
				break;
			} catch (Exception e){
				errorNum++;
				if (errorNum > 5) {
					logger.error("获取接口服务异常", e);
					break;
				}
			}
		}
		return bindingStub;
	}
	
	public static String getSessionId(BindingStub stub) {
		int num = 0;
		String sessioonId = login(stub);
		while (StringUtils.isEmpty(sessioonId)) {
			sessioonId = login(stub);
			if (StringUtils.isNotEmpty(sessioonId)) {
				break;
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {}
			num++;
			if (num > 5) {
				logger.warn("累计5次获取sessionId失败!");
				break;
			}
		}
		return sessioonId;
	} 
	
	private static String login(BindingStub stub) {
		try {
			return stub.login(username, apiKey);
		} catch (Exception e) {
			logger.error("获取sessionId异常", e);
		}
		return null;
	}
	
	/**
	 * 产品下架
	 * @param country 国家(支持de,com,fr,it,jp.其它国家不修改)
	 * @param asinSet 产品的asin集合(ps:同一产品有多个asin时以逗号连接)
	 * @return Map<String, String> asin rs 1:成功下架  2：未找到对应的产品  3：下架失败(此情况不返回)
	 */
	public static Map<String, String> catalogProductDelete(String country, Set<String> asinSet){
		Map<String, String> rsMap = Maps.newHashMap();
		if (StringUtils.isEmpty(country) || !"de,com,us,fr,it,jp".contains(country)) {
			logger.warn("不支持的country:" + country);
			//不支持的国家直接返回
			for (String asin : asinSet) {
				rsMap.put(asin, "2");
			}
			return rsMap;
		}
		logger.info(country + "同步下架官网产品:" + asinSet.toString());
		try {
			BindingStub stub = MagentoClientService.getBindingStub();
			String sessionId = MagentoClientService.getSessionId(stub);
			if (StringUtils.isEmpty(sessionId)) {
				logger.info("获取sessionId失败");
			}
			for (String asinStr : asinSet) {
				String asins = "";
				//同一产品可能有多个asin,以逗号连接传参
				for (String asin : asinStr.split(",")) {
					if (StringUtils.isNotEmpty(asin)) {
						asins = asins + asin + ",";
					}
				}
				String result = "1";	//默认成功
				if (StringUtils.isEmpty(asins)) {
					continue;
				}
				asins = asins.substring(0, asins.length() - 1);
	
				String key = getAsinKeyByCountry(country);//"asin_germany"
				String storeCode = country.toLowerCase();
				if ("com".equals(country) || "us".equals(country)) {
					storeCode = "default";
				}
				Map<String, String> asinIdMap =	getAsinRelation(stub, asins, sessionId, key, storeCode);
				if (asinIdMap.size() == 0) {	//没有匹配到产品
					result = "2";
				}
				for (String asin : asinIdMap.keySet()) {
					String product = asinIdMap.get(asin);
					//更新产品信息
					CatalogProductCreateEntity productData = new CatalogProductCreateEntity();
					productData.setStatus("2");
					productData.setVisibility("1");
					boolean rs = catalogProductUpdate(stub, sessionId, product, productData, storeCode);
					if (!rs) {
						logger.warn("下架失败,asin:" + asin);
						result = "3";	//有失败的记录为失败
					}
				}
				if (!"3".equals(result)) {	//失败的不返回,下次继续发送下架请求
					rsMap.put(asinStr, result);
				}
			}
		} catch (Exception e) {
			logger.error("修改官网价格失败！", e);
		}
		return rsMap;
	}
	
	/**
	 * 修改产品价格
	 * @param country 国家(支持de,com,fr,it,jp.其它国家不修改)
	 * @param asinDesMap key：asin  value:asin对应的价格
	 */
	public static void catalogProductPriceUpdate(String country, Map<String, Float> asinPriceMap){
		if (StringUtils.isEmpty(country) || !"de,com,us,fr,it,jp".contains(country)) {
			logger.warn("不支持的country:" + country);
			return;
		}
		logger.info(country + "同步修改官网价格:" + asinPriceMap.toString());
		try {
			BindingStub stub = MagentoClientService.getBindingStub();
			String sessionId = MagentoClientService.getSessionId(stub);
			if (StringUtils.isEmpty(sessionId)) {
				logger.info("获取sessionId失败");
			}
			String asins = "";
			for (String asin : asinPriceMap.keySet()) {
				asins = asins + asin + ",";
			}
			if (StringUtils.isNotEmpty(asins)) {
				asins = asins.substring(0, asins.length() - 1);
	
				String key = getAsinKeyByCountry(country);//"asin_germany"
				String storeCode = country.toLowerCase();
				if ("com".equals(country) || "us".equals(country)) {
					storeCode = "default";
				}
				Map<String, String> asinIdMap =	getAsinRelation(stub, asins, sessionId, key, storeCode);
				for (String asin : asinIdMap.keySet()) {
					Float price = asinPriceMap.get(asin);
					if (price != null) {
						String product = asinIdMap.get(asin);
						CatalogProductCreateEntity productData = new CatalogProductCreateEntity();
						productData.setPrice(price.toString());
						//更新产品信息
						catalogProductUpdate(stub, sessionId, product, productData, storeCode);
					}
				}
			}
		} catch (Exception e) {
			logger.error("修改官网价格失败！", e);
		}
	}
	
	/**
	 * 修改产品描述(支持de,com,fr,it,jp)
	 * @param country 国家(支持de,com,fr,it,jp。其它国家不修改)
	 * @param asinDesMap key：asin  value:asin对应的description
	 */
	public static boolean catalogProductDescriptionUpdate(String country, Map<String, String> asinDesMap){
		if (StringUtils.isEmpty(country) || !"de,com,us,fr,it,jp".contains(country)) {
			logger.warn("不支持的country:" + country);
			return false;
		}
		try {
			BindingStub stub = MagentoClientService.getBindingStub();
			String sessionId = MagentoClientService.getSessionId(stub);
			if (StringUtils.isEmpty(sessionId)) {
				logger.info("获取sessionId失败");
			}
			String asins = "";
			for (String asin : asinDesMap.keySet()) {
				
				asins = asins + asin + ",";
			}
			if (StringUtils.isNotEmpty(asins)) {
				asins = asins.substring(0, asins.length() - 1);
	
				String key = getAsinKeyByCountry(country);
				String storeCode = country.toLowerCase();
				if ("com".equals(country) || "us".equals(country)) {
					storeCode = "default";
				}
				Map<String, String> asinIdMap =	getAsinRelation(stub, asins, sessionId, key, storeCode);
				boolean flag = false;
				for (String asin : asinIdMap.keySet()) {
					String description = asinDesMap.get(asin);
					if (StringUtils.isNotEmpty(description)) {
						String product = asinIdMap.get(asin);
						CatalogProductCreateEntity productData = new CatalogProductCreateEntity();
						productData.setShort_description(description);
						//更新产品信息
						flag = catalogProductUpdate(stub, sessionId, product, productData, storeCode);
					}
				}
				return flag;
			}
		} catch (Exception e) {
			logger.error("修改官网产品描述失败！", e);
		}
		return false;
	}
	
	/**
	 *	图片修改(支持de,com,fr,it,jp)
	 *  @param asin 改图片产品对应国家的asin
	 *  @param images 图片列表信息
	 */
	public static void catalogProductAttributeMediaUpdate(String country, String asin, List<Image> images){
		if (StringUtils.isEmpty(country) || !"de,com,us,fr,it,jp".contains(country)) {
			logger.warn("不支持的country:" + country);
			return;
		}
		try {
			BindingStub stub = MagentoClientService.getBindingStub();
			String sessionId = MagentoClientService.getSessionId(stub);
			if (StringUtils.isEmpty(sessionId)) {
				logger.info("获取sessionId失败");
			}
			String key = getAsinKeyByCountry(country);
			String storeCode = country.toLowerCase();
			if ("com".equals(country) || "us".equals(country)) {
				storeCode = "default";
			}
			Map<String, String> asinIdMap =	getAsinRelation(stub, asin, sessionId, key, storeCode);
			if (StringUtils.isNotEmpty(asinIdMap.get(asin))) {
				String product = asinIdMap.get(asin);
				int errorNum = 0;
				CatalogProductImageEntity[] entitys = null;
				while (true) {
					if (errorNum > 20) {
						break;
					}
					try {
						entitys = stub.catalogProductAttributeMediaList(sessionId, product, storeCode, "0");
						break;
					} catch (Exception e) {
						errorNum ++ ;
						if (errorNum > 20) {
							logger.error("获取产品图片信息失败", e);
						}
					}
				}
				if (entitys == null || entitys.length == 0) {
					return;
				}
				//官网产品图片位置和名称对应关系
				Map<String, String> positionNameMap = Maps.newHashMap();
				for (CatalogProductImageEntity entity : entitys) {
					positionNameMap.put(entity.getPosition(), entity.getFile());
				}
				for (Image image : images) {
					String type = image.getType();
					String position = "1";	//对应官网的图片位置,main图片放第一张
					if (!"Main".equals(type)) {
						Integer p = Integer.parseInt(type.substring(2)) + 1;
						position = p.toString();
					}
					String name = positionNameMap.get(position);
					String isDelete = image.getIsDelete();
					//根据路径查找图片base64编码后传递
					CatalogProductAttributeMediaCreateEntity data = new CatalogProductAttributeMediaCreateEntity();
					CatalogProductImageFileEntity file = new CatalogProductImageFileEntity();
					file.setMime("image/jpeg");
					if (StringUtils.isNotEmpty(name)) {		//有name,说明已经存在图片,可update
						file.setName(name);
						if (StringUtils.isNotEmpty(isDelete) && "1".equals(isDelete)) {
							data.setRemove("1");	//删除
							data.setExclude("1");	//显示标记
						} else {
							String path = basePath+image.getLocation();
							try {
								path = URLDecoder.decode(path,"UTF-8");
							} catch (UnsupportedEncodingException e) {}
							logger.info("update图片路径：" + path);
							File imgFile = new File(path);
							FileInputStream in;
							try {
								in = new FileInputStream(imgFile);
								String content = Encodes.getBASE64String(Encodes.transInputstreamToBytes(in));
								file.setContent(content);
							} catch (FileNotFoundException e1) {
								logger.error("File not found", e1);
							}
							data.setFile(file);
							data.setRemove("0");	//删除标记
							data.setExclude("0");	//显示标记
						}
						data.setPosition(position);	//图片位置
						errorNum = 0;
						while (true) {
							if (errorNum > 20) {
								break;
							}
							try {
								stub.catalogProductAttributeMediaUpdate(sessionId, product, name, data, storeCode, "0");
								break;
							} catch (Exception e2) {
								errorNum ++ ;
								if (errorNum > 20) {
									logger.error("更新产品图片信息失败", e2);
								}
							}
						}
					} else {	//create图片,删除的忽略
						if (StringUtils.isEmpty(isDelete) || !"1".equals(isDelete)) {
							data.setExclude("0");	//显示标记
							data.setPosition(position);	//图片位置
							data.setRemove("0");	//删除
							String path = basePath+image.getLocation();
							try {
								path = URLDecoder.decode(path,"UTF-8");
							} catch (UnsupportedEncodingException e) {}
							logger.info("create图片路径：" + path);
							File imgFile = new File(path);
							FileInputStream in;
							try {
								in = new FileInputStream(imgFile);
								String content = Encodes.getBASE64String(Encodes.transInputstreamToBytes(in));
								file.setContent(content);
							} catch (FileNotFoundException e1) {
								logger.error("File not found", e1);
							}
							data.setFile(file);
							data.setExclude("0");	//显示标记
							data.setPosition(position);	//图片位置
							errorNum = 0;
							while (true) {
								if (errorNum > 20) {
									break;
								}
								try {
									stub.catalogProductAttributeMediaCreate(sessionId, product, data, storeCode, "0");
									break;
								} catch (Exception e2) {
									errorNum ++ ;
									if (errorNum > 20) {
										logger.error("创建产品图片信息失败", e2);
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error(country + "修改图片失败, asin:" + asin, e);
		}
	}
	
	/**
	 * 同步美国产品重量到官网,换算成磅
	 * @param country 国家com
	 * @param asinWeightMap key：asin  value:asin对应的产品重量
	 * @throws Exception 
	 */
	public static void catalogProductWeightUpdate(String country, Map<String, String> asinWeightMap, 
			String sessionId, BindingStub stub) throws Exception{
		if (StringUtils.isEmpty(country) || !"de,com,us,fr,it,jp".contains(country)) {
			logger.warn("不支持的country:" + country);
			return;
		}
		String asins = "";
		for (String asin : asinWeightMap.keySet()) {
			asins = asins + asin + ",";
		}
		if (StringUtils.isNotEmpty(asins)) {
			asins = asins.substring(0, asins.length() - 1);

			String key = getAsinKeyByCountry(country);//"asin_germany"
			String storeCode = country.toLowerCase();
			if ("com".equals(country) || "us".equals(country)) {
				storeCode = "default";
			}
			Map<String, String> asinIdMap =	getAsinRelation(stub, asins, sessionId, key, storeCode);
			for (String asin : asinIdMap.keySet()) {
				String weight = asinWeightMap.get(asin);
				if (StringUtils.isNotEmpty(weight)) {
					String product = asinIdMap.get(asin);
					CatalogProductCreateEntity productData = new CatalogProductCreateEntity();
					productData.setWeight(weight);
					//更新产品信息
					catalogProductUpdate(stub, sessionId, product, productData, storeCode);
				}
			}
		}
	}
	
	/**
	 * 校验官网和亚马逊价格
	 * @param country 国家com,de
	 * @param asinPriceMap key：asin  value:asin对应的亚马逊价格
	 * @return Map<String, List<Float>> 价格不一致的asin  list  亚马逊价格、官网价格、是否自动改价（0：未改 1：已改 2：失败）
	 * @throws Exception 
	 */
	public static Map<String, List<Object>> catalogProductPriceCheck(String country, Map<String, Float> asinPriceMap, 
			String sessionId, BindingStub stub) throws Exception{
		Map<String, List<Object>> rsMap = Maps.newHashMap();
		if (StringUtils.isEmpty(country) || !"de,com,us,fr,it,jp".contains(country)) {
			logger.warn("不支持的country:" + country);
			return rsMap;
		}
		if (asinPriceMap == null || asinPriceMap.size() == 0) {
			return rsMap;
		}
		String asins = "";
		for (String asin : asinPriceMap.keySet()) {
			asins = asins + asin + ",";
		}
		if (StringUtils.isNotEmpty(asins)) {
			asins = asins.substring(0, asins.length() - 1);

			String key = getAsinKeyByCountry(country);//"asin_germany"
			String storeCode = country.toLowerCase();
			if ("com".equals(country) || "us".equals(country)) {
				storeCode = "default";
			}
			Map<String, String> asinIdMap =	getAsinRelation(stub, asins, sessionId, key, storeCode);
			for (String asin : asinIdMap.keySet()) {
				Float price = asinPriceMap.get(asin);
				if (price != null) {
					String product = asinIdMap.get(asin);
					CatalogProductReturnEntity entity = stub.catalogProductInfo(sessionId, product, storeCode, null, null);
					if (entity != null) {
						Float priceInateck = Float.parseFloat(entity.getPrice());
						if (!price.equals(priceInateck)) {	//不相等
							List<Object> list = Lists.newArrayList();
							list.add(price);
							list.add(priceInateck);
							String flag = "0";	//不修改价格只提醒
							/*主动修改不一致的价格*/
							CatalogProductCreateEntity productData = new CatalogProductCreateEntity();
							productData.setPrice(price.toString());
							try {
								boolean rs = catalogProductUpdate(stub, sessionId, product, productData, storeCode);
								if (rs) {
									flag = "1";
								} else {
									flag = "2";
								}
							} catch (Exception e) {
								flag = "2";
							}
							list.add(flag);
							rsMap.put(asin, list);
						}
					}
				}
			}
		}
		return rsMap;
	}
	
	/**
	 * 产品修改
	 * @param product 产品id或sku
	 */
	private static boolean catalogProductUpdate(BindingStub stub, String sessionId, String product, 
			CatalogProductCreateEntity productData, String storeCode){
		try {
			boolean flag = false;
			int errorNum = 0;
			while (!flag) {
				try {
					//调用接口同步库存
					flag = stub.catalogProductUpdate(sessionId, product, productData, storeCode, "0");
					break;
				} catch (Exception e) {
					Thread.sleep(10000);
					errorNum++;
					if (errorNum > 20) {
						logger.error("累计20次修改官网产品异常", e);
						throw new Exception("累计20次修改官网产品异常");
					}
				}
			}
			return flag;
		} catch (Exception e) {
			logger.error("修改官网产品失败！", e);
		}
		return false;
	}
	
	/**
	 * 根据asin获取asin与官网产品(id)的对应关系
	 * @param stub
	 * @param asins
	 * @param sessionId
	 * @return
	 */
	private static Map<String, String> getAsinRelation(BindingStub stub, String asins, 
			String sessionId, String key, String storeCode) throws Exception {
		//组合筛选条件查询产品,根据库存对应的所有asin得到官网asin与产品的对应关系
		Filters filters = new Filters();
		AssociativeEntity assFilter = new AssociativeEntity();
		assFilter.setKey("in");
		assFilter.setValue(asins);//"B00N1KXE9K,B00N1LHFEY"
		ComplexFilter[] complexFilters = new ComplexFilter[1];
		filters.setComplex_filter(complexFilters);
		ComplexFilter complexFilter = new ComplexFilter();
		complexFilter.setKey(key);	//德国:asin_germany，美国：asin_us
		complexFilter.setValue(assFilter);
		complexFilters[0] = complexFilter;
		CatalogProductEntity[] arr = null;
		int errorNum = 0;
		while (arr == null) {
			try {
				arr = stub.catalogProductList(sessionId, filters, storeCode);
				break;
			} catch (Exception e) {
				Thread.sleep(5000);
				errorNum++;
				if (errorNum > 20) {
					logger.error("累计20次获取产品id与asin对应关系异常", e);
					throw new Exception("累计5次获取产品id与asin对应关系异常");
				}
			}
		}
		//官网中asin与产品ID对应关系map
		Map<String, String> inateckAsinIdMap = Maps.newHashMap();
		for (CatalogProductEntity entity : arr) {
			String asin = "";
			if (key.equals("asin_germany")) {
				asin = entity.getAsin_germany();
			} else if (key.equals("asin_us")) {
				asin = entity.getAsin_us();
			} else if (key.equals("asin_japan")) {
				asin = entity.getAsin_japan();
			} else if (key.equals("asin_canada")) {
				asin = entity.getAsin_canada();
			} else if (key.equals("asin_uk")) {
				asin = entity.getAsin_uk();
			} else if (key.equals("asin_italy")) {
				asin = entity.getAsin_italy();
			} else if (key.equals("asin_spain")) {
				asin = entity.getAsin_spain();
			} else if (key.equals("asin_france")) {
				asin = entity.getAsin_france();
			}
			String id = entity.getProduct_id();
			if (StringUtils.isNotEmpty(asin) && StringUtils.isNotEmpty(id)) {
				inateckAsinIdMap.put(asin.trim(), id);
			}
		}
		return inateckAsinIdMap;
	}
	
	/**
	 * 根据国家获取官网asin对应的字段名称
	 * @return
	 */
	private static String getAsinKeyByCountry(String country) {
		if (StringUtils.isEmpty(country)) {
			logger.warn("country is null");
			return null;
		}
		country = country.toLowerCase();
		if ("de".equals(country)) {
			return "asin_germany";
		} else if ("com".equals(country) || "us".equals(country)) {
			return "asin_us";
		} else if ("jp".equals(country)) {
			return "asin_japan";
		} else if ("ca".equals(country)) {
			return "asin_canada";
		} else if ("uk".equals(country)) {
			return "asin_uk";
		} else if ("it".equals(country)) {
			return "asin_italy";
		} else if ("es".equals(country)) {
			return "asin_spain";
		} else if ("fr".equals(country)) {
			return "asin_france";
		} else {
			logger.warn("invalid country:" + country);
			return null;
		}
	}

	public static void main(String[] args) {
		/*Map<String, String> asinDesMap = Maps.newHashMap();
		String deString = "<ul>"+
				"<li>High quality PP material test de</li>"+
				"<li>Simple design, easy to open, rich texture surface design, smooth curve treatment in both ends</li>"+
				"<li>Internally and externally ribbed housing design, reduce pressure and prevent from temperature distortion</li>"+
				"</ul>";
		asinDesMap.put("B01GFCHVCQ", deString);
		boolean rs = catalogProductDescriptionUpdate("com", asinDesMap);
		System.out.println(rs);*/
		
		/*Map<String, Float> asinPriceMap = Maps.newHashMap();
		Float priceFloat = Float.parseFloat("26.99");
		asinPriceMap.put("B01GFMQ4X8", priceFloat);
		catalogProductPriceUpdate(asinPriceMap);*/
		

		/*List<Image> images = Lists.newArrayList();
		Image image = new Image();
		image.setLocation("/inateck-erp/userfiles/fb502a8f2de944fa9737bd1891e5512c/../images/productImages/SP1003/001.jpg");
		image.setType("PT5");
		image.setIsDelete("1");
		images.add(image);
		
		catalogProductAttributeMediaUpdate("de", "B01GFMQ4X8", images);*/
		
		/*String country = "fr";
		Map<String, String> rs = catalogProductDelete(country, Sets.newHashSet("B01GFMQ,ddwfaf","B01GFMQSDFS","B01GFMQ4X8,B01GFMQSXX"));
		for (String string : rs.keySet()) {
			System.out.println(string + "\t" + rs.get(string));
		}*/
		
		BindingStub stub = getBindingStub();
		Map<String, Float> asinPriceMap = Maps.newHashMap();
		asinPriceMap.put("B01N593EJY", 35.99f);
		asinPriceMap.put("B00DW374W4", 11.99f);
		asinPriceMap.put("B00DZFOH4W", 57.99f);
		asinPriceMap.put("B00FCLG65U", 13.99f);
		asinPriceMap.put("B00FPIMICA", 25.99f);
		asinPriceMap.put("B00IJU0K2Q", 19.99f);
		try {
			MagentoClientService.catalogProductPriceCheck("com", asinPriceMap, getSessionId(stub), stub);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
