package com.springrain.erp.modules.psi.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonPostsDetailService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.SaleProfitService;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductInStock;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductInStockService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;
import com.springrain.magento.AssociativeEntity;
import com.springrain.magento.BindingStub;
import com.springrain.magento.CatalogInventoryStockItemUpdateEntity;
import com.springrain.magento.CatalogProductEntity;
import com.springrain.magento.ComplexFilter;
import com.springrain.magento.Filters;
import com.springrain.magento.MagentoClientService;

public class InateckInventorySynMonitor {

	private final static Logger logger = LoggerFactory.getLogger(InateckInventorySynMonitor.class);

	@Autowired
	private PsiInventoryService inventoryService;
	
	@Autowired
	private PsiProductService psiProductService;
	
	@Autowired
	private AmazonPostsDetailService amazonPostsDetailService;
	
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	
	@Autowired
	private MailManager mailManager;
	
	@Autowired
	private SaleProfitService saleProfitService;
	
	@Autowired
	private PsiProductInStockService inStockService;
	
	/**
	 * 异常库存监控(库存数和出入库记录中的库存不一致的产品)
	 */
	public void abnormalInventory() {
		List<PsiInventory> list = inventoryService.findAbnormalInventory();
		if (list != null && list.size() > 0) {
			StringBuffer sb = new StringBuffer();
			for (PsiInventory psiInventory : list) {
				sb.append("sku:" + psiInventory.getSku()+ ",库存数:" +psiInventory.getNewQuantity()+ ",出入库记录数:" +psiInventory.getOldQuantity()+ "\n");
			}
			String head = "系统监控到库存异常，明细如下，请核实\n";
			WeixinSendMsgUtil.sendTextMsgToUser("tim|leehong|eileen", head+sb.toString());
		}
	}
	
	/**
	 * 库存通知邮件(库存top20和库销比top20),每月最后一个周五发送
	 */
	public void inventoryNotice() {
		try {
			Date today = new Date();
			int month = today.getMonth();
			Date nextWeek = DateUtils.addDays(today, 7);
			int nextWeekMonth = nextWeek.getMonth();
			if (month == nextWeekMonth) {	//下周月份一致,说明当前不是当月最后一周
				return;
			}
			List<PsiProductInStock> topInventory = inStockService.findTopInventory();
			List<PsiProductInStock> topRatio = inStockService.findTopRatio();
			StringBuffer contents = new StringBuffer("");
			contents.append("Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;以下是产品库存量及31日库销比Top20明细,请知悉.<br/>");
			
	    	String title = "库存量前20产品明细";
			contents.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
			contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='7'><span style='font-weight: bold;font-size:25px'>"+title+"</span></td></tr>");
			contents.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>产品名称</th><th>总库存</th><th>31日销</th><th>31日库销比</th></tr>");
			for (PsiProductInStock psiProductInStock : topInventory) {
	    		int totalStock = psiProductInStock.getTotalStock();
	    		int day31Sales = psiProductInStock.getDay31Sales();
	    		String ratio = "";
	    		if (day31Sales > 0) {
	    			ratio = String.format("%.2f", totalStock/(double)day31Sales);
				}
				contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
	    		contents.append("<td>"+(psiProductInStock.getProductName())+"</td>");
	    		contents.append("<td>"+(totalStock)+"</td>");
	    		contents.append("<td>"+(day31Sales)+"</td>");
	    		contents.append("<td>"+(ratio)+"</td>");
	    		contents.append("</tr>");
			}
			contents.append("</table><br/><br/>");
			
			title = "31日销库销比前20产品明细";
			contents.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
			contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='7'><span style='font-weight: bold;font-size:25px'>"+title+"</span></td></tr>");
			contents.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>产品名称</th><th>总库存</th><th>31日销</th><th>31日库销比</th></tr>");
			for (PsiProductInStock psiProductInStock : topRatio) {
	    		int day31Sales = psiProductInStock.getDay31Sales();
	    		String ratio = "";
	    		if (day31Sales > 0) {
	    			ratio = String.format("%.2f", psiProductInStock.getInventorySaleMonth());
				}
				contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
	    		contents.append("<td>"+(psiProductInStock.getProductName())+"</td>");
	    		contents.append("<td>"+(psiProductInStock.getTotalStock())+"</td>");
	    		contents.append("<td>"+(day31Sales)+"</td>");
	    		contents.append("<td>"+(ratio)+"</td>");
	    		contents.append("</tr>");
			}
			contents.append("</table><br/><br/>");
        	final MailInfo mailInfo = new MailInfo("manager@inateck.com", "Top20产品库存量及31日库销比提醒("+DateUtils.getDate("yyyy/MM/dd")+")", new Date());
			mailInfo.setContent(contents.toString());
			mailInfo.setBccToAddress("leehong@inateck.com");
			new Thread(){
			    public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
		} catch (Exception e) {
			logger.error("Note:TOP20库存量及库销比邮件失败", e);
		}
	}
	
	public void synInventory() {
		try {
			logger.info("开始同步德国本地仓和美国FBA库存到官网");
			BindingStub stub = MagentoClientService.getBindingStub();
			String sessionId = MagentoClientService.getSessionId(stub);
			if (StringUtils.isEmpty(sessionId)) {
				throw new Exception("获取sessionId失败");
			}
			//asin对应的库存数
			Map<String, Integer> data = inventoryService.findDeLocalInventory(psiProductService.getHasPower());
			StringBuilder asins = new StringBuilder("");	//库存对应的所有asin,
			for (String asin : data.keySet()) {
				asins.append(asin).append(",");
			}
			if (StringUtils.isNotEmpty(asins)) {
				asins = new StringBuilder(asins.substring(0, asins.length()-1));
			}
			String rs = "";
			String syncCountry = "de";
			try {
				int num = syncInventory(syncCountry, data, stub, sessionId, asins);
				rs = "同步德国本地仓库存到官网完毕,同步产品数:" + num;
			} catch (Exception e) {
				logger.error("同步德国库存到官网失败", e);
				rs = "同步德国库存到官网失败";
			}
			syncCountry = "com";
			//data = inventoryService.findFbaInventory(syncCountry);
			//改成同步本地仓库存,小于等于10的取0，大于10的取一半库存
			data = inventoryService.findUsLocalInventory();
			asins = new StringBuilder("");	//库存对应的所有asin,
			for (String asin : data.keySet()) {
				asins.append(asin).append(",");
			}
			if (StringUtils.isNotEmpty(asins)) {
				asins = new StringBuilder(asins.substring(0, asins.length()-1));
			}
			try {
				int num = syncInventory(syncCountry, data, stub, sessionId, asins);
				rs += "\n同步美国FBA库存到官网完毕,同步产品数:" + num;
			} catch (Exception e) {
				logger.error("同步美国FBA库存到官网失败", e);
				rs += "\n同步美国FBA库存到官网失败";
			}
			logger.info("同步库存完毕,开始同步美国产品重量");
			try {
				Map<String, String> asinWeightMap = Maps.newHashMap();
				List<PsiProduct> list = psiProductService.find();
				Map<String, Float> weightMap = Maps.newHashMap();
				Map<String, String> asinNameMap = amazonPostsDetailService.findNewAsinNameMap("com");
				for (PsiProduct psiProduct : list) {
					//单品快递计费重量
					weightMap.put(psiProduct.getName(), psiProduct.getExpressGw());
				}
				for (String asin : asinNameMap.keySet()) {
					Float weight = weightMap.get(asinNameMap.get(asin));
					if (weight != null) {
						//进一法换算成磅
						asinWeightMap.put(asin, Math.ceil(weight*2.2205f)+"");
					}
				}
				MagentoClientService.catalogProductWeightUpdate("com", asinWeightMap, sessionId, stub);
				logger.info("同步产品重量完毕");
				rs += "\n同步产品重量完毕";
			} catch (Exception e) {
				logger.error("Note:同步产品重量失败", e);
				rs += "\n同步产品重量失败!!!";
			}
			try {
				logger.info("开始比对德美产品价格");
				List<String> countryList = Lists.newArrayList("de", "com");
				Map<String,Map<String,Float>> asinPriceMap = amazonProduct2Service.findCountryAsinPrice(countryList);
				Map<String, Map<String, List<Object>>> checkRs = Maps.newHashMap();
				for (String country : countryList) {
					Map<String, List<Object>> rsMap = MagentoClientService.catalogProductPriceCheck(country, asinPriceMap.get(country), sessionId, stub);
					if (rsMap != null && rsMap.size() > 0) {
						checkRs.put(country, rsMap);
					}
				}
				if (checkRs.size() > 0) {
					Map<String,String> asinNameMap = saleProfitService.getProductNameByAsin();
					String toAddress = "marketing_dept@inateck.com,kathy@inateck.com";	//发送邮件给市场推广部
		        	final MailInfo mailInfo = new MailInfo(toAddress, "官网产品价格与亚马逊不一致提醒("+DateUtils.getDate("yyyy/MM/dd")+")", new Date());
					String contents = initMesssage(checkRs, asinNameMap);
					mailInfo.setContent(contents);
					mailInfo.setCcToAddress("leehong@inateck.com");
					new Thread(){
					    public void run(){
							mailManager.send(mailInfo);
						}
					}.start();
				}
				logger.info("比对德美产品价格完毕");
				rs += "\n比对德美产品价格完毕";
			} catch (Exception e) {
				logger.error("Note:比对德美产品价格失败", e);
				rs += "\n比对德美产品价格失败!!!";
			}
			WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, rs);
		} catch (Exception e) {
			logger.error("同步库存到官网失败:" + e.getMessage(), e);
			WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, "同步库存到官网失败:"+e.getMessage());
		}
	}
	
	private static int syncInventory(String country, Map<String, Integer> data, BindingStub stub, 
			String sessionId, StringBuilder asins) throws Exception{
		//组合筛选条件查询产品,根据库存对应的所有asin得到官网asin与产品的对应关系
		Filters filters = new Filters();
		AssociativeEntity assFilter = new AssociativeEntity();
		assFilter.setKey("in");
		assFilter.setValue(asins.toString());//"B00N1KXE9K,B00N1LHFEY"
		ComplexFilter[] complexFilters = new ComplexFilter[1];
		filters.setComplex_filter(complexFilters);
		ComplexFilter complexFilter = new ComplexFilter();
		if ("de".equals(country)) {
			complexFilter.setKey("asin_germany");	//德国，美国：asin_us
		} else if ("com".equals(country)) {
			complexFilter.setKey("asin_us");	//德国，美国：asin_us
		} else {
			return 0;
		}
		
		complexFilter.setValue(assFilter);
		complexFilters[0] = complexFilter;
		CatalogProductEntity[] arr = null;
		int errorNum = 0;
		while (arr == null) {
			try {
				arr = stub.catalogProductList(sessionId, filters, null);
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
			String[] strs = entity.getWebsite_ids();
			if (strs == null || strs.length == 0) {
				continue;
			}
			boolean flag = true;
			for (int i = 0; i < strs.length; i++) {
				if ("de".equals(country) && "2".equals(strs[i])) {
					flag = false;	//德国站点编号为2,产品有德国站点才进行同步
					break;
				}
				if ("com".equals(country) && "1".equals(strs[i])) {
					flag = false;	//美国站点编号为1,产品有美国站点才进行同步
					break;
				}
			}
			if (flag) {
				continue;
			}
			String asin = entity.getAsin_germany();	//默认为德国
			if ("com".equals(country)) {
				asin = entity.getAsin_us();
			}
			String id = entity.getProduct_id();
			if (StringUtils.isNotEmpty(asin) && StringUtils.isNotEmpty(id)) {
				inateckAsinIdMap.put(asin.trim(), id);
			}
		}
		//ERP中asin对应的库存数转为官网中产品(id)对应的库存数map
		Map<String, Integer> idQuantityMap = Maps.newHashMap();
		for (Entry<String, Integer> entry : data.entrySet()) {
			String asin = entry.getKey();
			Integer quantytiy = entry.getValue();
			String id = inateckAsinIdMap.get(asin);
			if (StringUtils.isEmpty(id)) {
				continue;
			}
			Integer total = idQuantityMap.get(id);
			if (total == null) {
				idQuantityMap.put(id, quantytiy);
			} else {
				idQuantityMap.put(id, total + quantytiy);
			}
		}
		//组合webservice批量更新产品库存接口参数
		String[] productIds = new String[idQuantityMap.keySet().size()];
		CatalogInventoryStockItemUpdateEntity[] productData = new CatalogInventoryStockItemUpdateEntity[idQuantityMap.keySet().size()];
		int index = 0;
		for (Entry<String, Integer> entry : idQuantityMap.entrySet()) {
			String id = entry.getKey();
			//System.out.println("id:" + id + "\tqantity:" + idQuantityMap.get(id));
			productIds[index] = id;
			CatalogInventoryStockItemUpdateEntity entity = new CatalogInventoryStockItemUpdateEntity();
			entity.setQty(idQuantityMap.get(id)+"");
			entity.setManage_stock(1);
			if (idQuantityMap.get(id) > 0) {
				entity.setIs_in_stock(1);
			} else {
				entity.setIs_in_stock(0);
			}
			productData[index] = entity;
			index++;
		}
		
		boolean flag = false;
		errorNum = 0;
		while (!flag) {
			try {
				//调用接口同步库存
				flag = stub.catalogInventoryStockItemMultiUpdate(sessionId, productIds, productData);
				break;
			} catch (Exception e) {
				Thread.sleep(10000);
				errorNum++;
				if (errorNum > 20) {
					logger.error("累计20次同步库存异常", e);
					throw new Exception("累计20次同步库存异常");
				}
			}
		}
		return productIds.length;
	}
	
	/**
	 * 组装邮件内容
	 * @param checkRs
	 * @return
	 */
	private static String initMesssage(Map<String, Map<String, List<Object>>> checkRs, Map<String,String> asinNameMap) {
		StringBuffer contents = new StringBuffer("");
		contents.append("Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;以下是官网价格与亚马逊不一致产品,请知悉.<br/><table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' >");
//		for(String country : checkRs.keySet()){
		for(Map.Entry<String, Map<String, List<Object>>> entry1:checkRs.entrySet()){
			String country = entry1.getKey();
			String suff = country;
			if("jp,uk".contains(country)){
				suff = "co."+country;
			}
			String countryStr = SystemService.countryNameMap.get(country);
	    	String title = countryStr+"官网价格与亚马逊不一致产品";
			contents.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
			contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='7'><span style='font-weight: bold;font-size:25px'>"+title+"</span></td></tr>");
			contents.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>国家</th><th>Asin</th><th>产品</th><th>亚马逊价格</th><th>官网价格</th><th>调整结果</th></tr>");
			for (Entry<String, List<Object>> entry : entry1.getValue().entrySet()) {
	    		List<Object> list = entry.getValue();
	    		String asin = entry.getKey();
	    		String productName = asinNameMap.get(asin);
	    		String rs = "0".equals(list.get(2).toString())?"未调整":"1".equals(list.get(2).toString())?"已自动改价":"自动改价失败";
				contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
	    		contents.append("<td>"+(countryStr)+"</td><td><a href = \"http://www.amazon."+suff+"/dp/"+asin+"\">"+asin+"</a></td>");
	    		contents.append("<td><a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/psiInventory/productInfoDetail?productName="+productName+"'>"+productName+"</a></td>");
	    		contents.append("<td>"+(String.format("%.2f", list.get(0)))+"</td>");
	    		contents.append("<td>"+(String.format("%.2f", list.get(1)))+"</td>");
	    		contents.append("<td style=\"color:"+("2".equals(list.get(2).toString())?"red":"")+"\">"+(rs)+"</td>");
	    		contents.append("</tr>");
			}
			contents.append("</table><br/><br/>");
		}
		return contents.toString();
	}

	public static void main(String[] args) {
		//new InateckInventorySynMonitor().synInventory();

		BindingStub stub = MagentoClientService.getBindingStub();
		String sessionId = MagentoClientService.getSessionId(stub);
		if (StringUtils.isEmpty(sessionId)) {
			logger.info("获取sessionId失败");
		}
		/*String product = "436";
		CatalogInventoryStockItemUpdateEntity entity = new CatalogInventoryStockItemUpdateEntity();
		entity.setQty("224");
		entity.setIs_in_stock(1);
		entity.setUse_config_min_qty(1);
		entity.setUse_config_max_sale_qty(1);
		entity.setManage_stock(1);
		entity.setUse_config_manage_stock(0);
		entity.setIs_qty_decimal(0);
		entity.setUse_config_backorders(1);
		entity.setNotify_stock_qty(10);
		entity.setUse_config_notify_stock_qty(0);
		int num = 0;
		while (num < 10) {
			try {	//更新单个产品
				int rs = stub.catalogInventoryStockItemUpdate(sessionId, product, entity);
				System.out.println("更新" + rs + "行");
				break;
			} catch (Exception e) {
				num++;
				if (num==10) {
					logger.error("更新库存异常", e);
				}
			}
		}

		String[] productIds = "256,257,258".split(",");
		CatalogInventoryStockItemUpdateEntity[] productData = new CatalogInventoryStockItemUpdateEntity[3];
		productData[0] = entity;
		CatalogInventoryStockItemUpdateEntity entity1 = new CatalogInventoryStockItemUpdateEntity();
		entity1.setQty("286");
		productData[1] = entity1;
		CatalogInventoryStockItemUpdateEntity entity2 = new CatalogInventoryStockItemUpdateEntity();
		entity2.setQty("386");
		productData[2] = entity2;
		try {	//批量更新
			boolean flag = stub.catalogInventoryStockItemMultiUpdate(sessionId, productIds, productData);
			System.out.println(flag);
		} catch (Exception e) {
			logger.error("更新库存异常", e);
		}*/
		
		Map<String, Float> asinPriceMap = Maps.newHashMap();
		asinPriceMap.put("B01N593EJY", 35.99f);
		asinPriceMap.put("B00DW374W4", 11.99f);
		asinPriceMap.put("B00DZFOH4W", 57.99f);
		asinPriceMap.put("B00FCLG65U", 13.99f);
		asinPriceMap.put("B00FPIMICA", 25.99f);
		asinPriceMap.put("B00IJU0K2Q", 19.99f);
		Map<String, Map<String, List<Object>>> checkRs = Maps.newHashMap();
		String country = "com";
		Map<String, List<Object>> rsMap = Maps.newHashMap();
		try {
			rsMap = MagentoClientService.catalogProductPriceCheck(country, asinPriceMap, sessionId, stub);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (rsMap != null && rsMap.size() > 0) {
			checkRs.put(country, rsMap);
		}
		if (checkRs.size() > 0) {
			//TODO 发送邮件通知
			Map<String, String> asinNameMap = Maps.newHashMap();
			String contents = initMesssage(checkRs, asinNameMap);
			System.out.println(contents);
			
		}
		logger.info("比对德美产品价格完毕");
	}
}
