package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.CountryCode;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.SaleProfitDao;
import com.springrain.erp.modules.amazoninfo.entity.Advertising;
import com.springrain.erp.modules.amazoninfo.entity.AmazonLightningDeals;
import com.springrain.erp.modules.amazoninfo.entity.ProductPrice;
import com.springrain.erp.modules.amazoninfo.entity.SaleProfit;
import com.springrain.erp.modules.amazoninfo.entity.SaleReport;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOutboundOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonRemovalOrderItem;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductMoldFeeService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.utils.DictUtils;

/**
 * 产品按天统计销量销售额利润Service
 */
@Component
@Transactional(readOnly = true)
public class SaleProfitService extends BaseService {
	
	@Autowired
	private SaleProfitDao saleProfitDao;
	
	@Autowired
	private PsiProductService psiProductService;
	
	@Autowired
	private SaleReportService saleReportService;
	
	@Autowired
	private PsiProductTypeGroupDictService dictService;

	@Autowired
	private AmazonPromotionsWarningService amazonPromotionsWarningService;
	
	@Autowired
	private PsiProductMoldFeeService psiProductMoldFeeService;
	
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
	
	public static Map<String, Float> rs = Maps.newConcurrentMap();
	//广告数据国家账号临时对应关系
	public static Map<String, String> accountMap = Maps.newHashMap();
	static{
		// 2015年估算平均汇率(用于统计数据转换欧元)
		rs.put("USD/EUR", 0.9117f);
		rs.put("GBP/EUR", 1.38f);
		rs.put("CAD/EUR", 0.72f);
		rs.put("JPY/EUR", 0.0074f);
		rs.put("CNY/USD", 0.161f);
		rs.put("EUR/USD", 1.1f);
		//以下数据未使用
		rs.put("USD/CNY",6.31f);
		rs.put("EUR/CNY", 7.06f);
		rs.put("JPY/CNY", 0.05f);
		rs.put("CAD/CNY", 5.02f);
		rs.put("GBP/CNY", 9.33f);
		rs.put("JPY/USD", 0.0084f);
		rs.put("CAD/USD", 0.81f);
		rs.put("GBP/USD", 1.56f);
		rs.put("JPY/CAD", 0.01f);
		rs.put("JPY/GBP", 0.0053f);
		rs.put("USD/CAD", 1.3065f);
		rs.put("USD/GBP", 0.6446f);
		rs.put("USD/JPY", 123.738f);
		
		accountMap.put("de", "Inateck_DE");
		accountMap.put("fr", "Inateck_FR");
		accountMap.put("it", "Inateck_IT");
		accountMap.put("es", "Inateck_ES");
		accountMap.put("uk", "Inateck_UK");
		accountMap.put("com", "Inateck_US");
		accountMap.put("ca", "Inateck_CA");
		accountMap.put("jp", "Inateck_JP");
		accountMap.put("mx", "Inateck_MX");
		accountMap.put("com2", "TDKRFSEB_US");
		accountMap.put("com3", "Tomons_US");
	}
		
	@Transactional(readOnly = false)
	public void save(SaleProfit saleProfit) {
		saleProfitDao.save(saleProfit);
	}

	@Transactional(readOnly = false)
	public void saveList(List<SaleProfit> saleProfits) {
		saleProfitDao.save(saleProfits);
	}
	
	/**
	 * 根据日期分平台、产品类型统计销售额、销量以及费用利润等信息保存到中间表
	 * @param date	日期(yyyyMMdd)
	 * @param rateRs 当天汇率
	 * @return
	 * 
	 */
	@Transactional(readOnly=false)
	public void saveOrUpdate(String date, Map<String, Float> rateRs){
		if (rateRs == null || rateRs.size() < 19) {
			rateRs = rs;
		}
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		String currencyType = "EUR";
		String typeSql = "'%Y%m%d'";
		//税后销售额排除增值税(新增加美国： 6.47%、加拿大：15%、日本：8%、墨西哥：16%)
		String temp1 = " ,TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.sales_no_tax*:p1/1.15 WHEN a.country='jp' THEN a.sales_no_tax*:p2/1.08 WHEN a.country='uk' THEN a.sales_no_tax*:p3/1.2 WHEN a.country='com' THEN a.sales_no_tax*:p4/1.0647 WHEN a.country='mx' THEN a.sales_no_tax*:p5/1.16 WHEN a.country='cn' THEN a.sales_no_tax*:p6 WHEN a.country='es' THEN a.sales_no_tax*:p7/1.21  WHEN a.country='fr' THEN a.sales_no_tax*:p7/1.2 WHEN a.country='it' THEN a.sales_no_tax*:p7/1.22 ELSE a.sales_no_tax*:p7/1.19  END),2)" +
	        		" ,TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.fee*:p1 WHEN a.country='jp' THEN a.fee*:p2 WHEN a.country='uk' THEN a.fee*:p3 WHEN a.country='com' THEN a.fee*:p4 WHEN a.country='mx' THEN a.fee*:p5 WHEN a.country='cn' THEN a.fee*:p6 ELSE a.fee*:p7 END),2)" +
	        		" ,TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.fee_other*:p1 WHEN a.country='jp' THEN a.fee_other*:p2 WHEN a.country='uk' THEN a.fee_other*:p3 WHEN a.country='com' THEN a.fee_other*:p4 WHEN a.country='mx' THEN a.fee_other*:p5 WHEN a.country='cn' THEN a.fee_other*:p6 ELSE a.fee_other*:p7 END),2)" +
	        		" ,TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.fee_quantity*IFNULL(a.`pack_num`,1)*:p9*"+ProductPrice.tranFee.get("ca")+" WHEN a.country='jp' THEN a.fee_quantity*IFNULL(a.`pack_num`,1) * :p9 * "+ProductPrice.tranFee.get("jp")+" WHEN a.country='de' THEN a.fee_quantity*IFNULL(a.`pack_num`,1) * :p9 * "+ProductPrice.tranFee.get("de")+" WHEN a.country='uk' THEN a.fee_quantity*IFNULL(a.`pack_num`,1) * :p9 * "+ProductPrice.tranFee.get("uk")+" WHEN a.country='com' THEN a.fee_quantity*IFNULL(a.`pack_num`,1) * :p9 *"+ProductPrice.tranFee.get("com")+" WHEN a.country='mx' THEN a.fee_quantity*IFNULL(a.`pack_num`,1) * :p9 * "+ProductPrice.tranFee.get("mx")+" WHEN a.country='es' THEN a.fee_quantity*IFNULL(a.`pack_num`,1) * :p9 * "+ProductPrice.tranFee.get("es")+" WHEN a.country='it' THEN a.fee_quantity*IFNULL(a.`pack_num`,1) * :p9 * "+ProductPrice.tranFee.get("it")+" WHEN a.country='fr' THEN a.fee_quantity*IFNULL(a.`pack_num`,1) * :p9 * "+ProductPrice.tranFee.get("fr")+" ELSE a.fee_quantity*IFNULL(a.`pack_num`,1) * :p9 * 14 END),2)" +
	        		" ,sum(IFNULL(fee_quantity,0)*IFNULL(a.`pack_num`,1)),TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.refund*:p1 WHEN a.country='jp' THEN a.refund*:p2 WHEN a.country='uk' THEN a.refund*:p3 WHEN a.country='com' THEN a.refund*:p4 WHEN a.country='mx' THEN a.refund*:p5 WHEN a.country='cn' THEN a.refund*:p6 ELSE a.refund*:p7 END),2)" ;
		Parameter parameter = new Parameter(MathUtils.getRate("CAD", currencyType, rateRs),MathUtils.getRate("JPY", currencyType, rateRs),MathUtils.getRate("GBP", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),MathUtils.getRate("MXN", currencyType, rateRs),MathUtils.getRate("USD", currencyType, rateRs),MathUtils.getRate("EUR", currencyType, rateRs),date,MathUtils.getRate("CNY", currencyType, rateRs));
		
        String sql = "SELECT CONCAT((case when a.`product_name` is null then CONCAT(a.sku,'(未匹配)') else a.`product_name` end ),CASE  WHEN a.`color`='' || a.`color` is null THEN '' ELSE CONCAT('_',a.`color`) END) AS productName,SUM(a.`sales_volume`*IFNULL(a.`pack_num`,1)) as sales,TRUNCATE(SUM(CASE WHEN a.country='ca' THEN a.sales*:p1 WHEN a.country='jp' THEN a.sales*:p2 WHEN a.country='uk' THEN a.sales*:p3 WHEN a.country='com' THEN a.sales*:p4 WHEN a.country='mx' THEN a.sales*:p5 WHEN a.country='cn' THEN a.sales*:p6 ELSE a.sales*:p7 END),2)  " +//,sum(a.promotions_order),sum(a.flash_sales_order),max(max_order)  " +
        		temp1+",a.`country`,a.`avg_freight`,a.`price`,SUM(IFNULL(a.`return_num`,0)),a.`account_name`  FROM amazoninfo_sale_report a  ";
        sql+=" WHERE DATE_FORMAT(a.`date`,"+typeSql+")=:p8 and a.order_type='1'";
        sql+=" GROUP BY productName,a.account_name order by sales desc";
        //obj 0:产品名称 1:销量  2：销售额  3 ：税后收入  4：亚马逊佣金  5：杂费 6：运输费计算数(已减去退货数)  7：已确认数据 8：退款
        List<Object[]>  list = saleProfitDao.findBySql(sql,parameter);
		Map<String, String> productTypeMap = psiProductService.findProductTypeMap(date.substring(0, 6));
		//单价和运费信息(只使用运费数据,8月份以后单价按下面方式计算)
		Map<String, Map<String,Float>> gwMap = saleReportService.getProductPriceAndTranGwNoTax(currencyType, rateRs);
		//采购单价(已减去退税额)
		Map<String,Float> buyCostMap = getBuyCost(rateRs);
		//产品类型对应的产品线关系
		Map<String, String> typeLine = dictService.getTypeLine(date.substring(0, 6));
		//替代货和评测单
		Map<String, Map<String, AmazonOutboundOrder>> supportAndReview = findSupportAndReviewByDay(date, rateRs);
		//站内广成本[国家[产品  Advertising]]
		Map<String, Map<String, Map<String, Advertising>>> insideAdFeeMap = findInsideAdFeeByDay(date, rateRs);
		//站外广成本[国家[产品  Advertising]]
		//Map<String, Map<String, Map<String, Advertising>>> outsideAdFeeMap = findFacebookAdByDay(date, rateRs);
		//AMS广告[国家[产品  Advertising]]
		Map<String, Map<String, Advertising>> amsAdFeeMap = findAmsAdFeeByDay(date, rateRs);
		//促销明细[产品[国家  AmazonLightningDeals]]
		Map<String, Map<String, AmazonLightningDeals>> dealsFeeMap = amazonPromotionsWarningService.findDealDetailByDay(date, rateRs);
		//需要计算模具费的产品
		Map<String, Float> moldFeeMap = psiProductMoldFeeService.findMoldFeeForBuyCost();
		//唯一键
		Map<String, SaleProfit> saleProfits = findSaleProfits(date);
		//[国家[类型  对象]]
		Map<String, Map<String, SaleProfit>> rs = Maps.newHashMap();
		List<SaleProfit> lists = Lists.newArrayList();
		for (Object[] obj : list) {
			if (obj[0] == null) {
				continue;
			}
			String productName = obj[0].toString();
			String type = productTypeMap.get(productName);
			if (StringUtils.isEmpty(type)) {
				continue;
			}
			Integer salesVolume = obj[1]==null?0:Integer.parseInt(obj[1].toString());
			Float sales = obj[2]==null?0:Float.parseFloat(obj[2].toString());
			Float salesNoTax = obj[3]==null?0:Float.parseFloat(obj[3].toString());
			Float amazonFee = obj[4]==null?0:Float.parseFloat(obj[4].toString());
			Float otherFee = obj[5]==null?0:Float.parseFloat(obj[5].toString());
			Float transport = obj[6]==null?0:Float.parseFloat(obj[6].toString());
			Integer quantity = obj[7]==null?0:Integer.parseInt(obj[7].toString());
			Float refund = obj[8]==null?0:Float.parseFloat(obj[8].toString());
			String country = obj[9].toString();
			Float avgFreight = obj[10]==null?0:Float.parseFloat(obj[10].toString());	//运费单价
			Integer returnNum = obj[12]==null?0:Integer.parseInt(obj[12].toString());	//退货数
			String accountName = obj[13].toString();	//账号标记
			//计算替代货和评测单成本及广告(event)成本(评测单另外单独统计)
			Integer supportNum = 0;
			Float supportAmazonFee = 0f;	//替代货亚马逊总费用
			Float supportCost = 0f;	//替代货成本
			try {
				supportNum = supportAndReview.get(accountName).get(productName).getSupportNum();
				supportAmazonFee = supportAndReview.get(accountName).get(productName).getAmazonFee();
			} catch (NullPointerException e) {}
			Float transportFee = 0f;//运输费用(含替代货和评测单)
			try {
				if (avgFreight > 0) {
					transportFee = quantity * avgFreight;
					supportCost = supportNum * avgFreight;
				} else {	//没有运费记录按原来的算法计算
					transportFee = transport * gwMap.get(productName).get("gw");
					supportCost = supportNum * getEurRate(country, rateRs) * ProductPrice.tranFee.get(country) * gwMap.get(productName).get("gw");
				}
			} catch (NullPointerException e) {}
			//站内广告成本
			Float adInEventSales = 0f; //站内event广告销售额
			Integer adInEventSalesVolume = 0; //站内event广告销量
			Float adInEventFee = 0f; //站内event广告费用
			Float adInProfitSales = 0f; //站内Profit广告销售额
			Integer adInProfitSalesVolume = 0; //站内Profit广告销量
			Float adInProfitFee = 0f; //站内Profit广告费用
			try {//站内event广告
				adInEventFee = insideAdFeeMap.get(accountName).get(productName).get("event").getTotalSpend();
				adInEventSales = insideAdFeeMap.get(accountName).get(productName).get("event").getWeekSameSkuUnitsSales();
				adInEventSalesVolume = insideAdFeeMap.get(accountName).get(productName).get("event").getWeekSameSkuUnitsOrdered();
				adInEventFee = getVatFee(adInEventFee, country);
			} catch (NullPointerException e) {}
			try {//站内profit广告
				adInProfitFee = insideAdFeeMap.get(accountName).get(productName).get("profit").getTotalSpend();
				adInProfitSales = insideAdFeeMap.get(accountName).get(productName).get("profit").getWeekSameSkuUnitsSales();
				adInProfitSalesVolume = insideAdFeeMap.get(accountName).get(productName).get("profit").getWeekSameSkuUnitsOrdered();
				adInProfitFee = getVatFee(adInProfitFee, country);
			} catch (NullPointerException e) {}
			//站外广告成本
			Float adOutEventSales = 0f; //站外event广告销售额
			Integer adOutEventSalesVolume = 0; //站外event广告销量
			Float adOutEventFee = 0f; //站外event广告费用
			Float adOutProfitSales = 0f; //站外Profit广告销售额
			Integer adOutProfitSalesVolume = 0; //站外Profit广告销量
			Float adOutProfitFee = 0f; //站外Profit广告费用
			/*try {//站外event广告
				adOutEventFee = outsideAdFeeMap.get(country).get(productName).get("event").getTotalSpend();
				adOutEventSales = outsideAdFeeMap.get(country).get(productName).get("event").getWeekSameSkuUnitsSales();
				adOutEventSalesVolume = outsideAdFeeMap.get(country).get(productName).get("event").getWeekSameSkuUnitsOrdered();
				adOutEventFee = getVatFee(adOutEventFee, country);
			} catch (NullPointerException e) {}
			try {//站外profit广告
				adOutProfitFee = outsideAdFeeMap.get(country).get(productName).get("profit").getTotalSpend();
				adOutProfitSales = outsideAdFeeMap.get(country).get(productName).get("profit").getWeekSameSkuUnitsSales();
				adOutProfitSalesVolume = outsideAdFeeMap.get(country).get(productName).get("profit").getWeekSameSkuUnitsOrdered();
				adOutProfitFee = getVatFee(adOutProfitFee, country);
			} catch (NullPointerException e) {}*/

			//AMS广告成本
			Float adAmsSales = 0f; //站外event广告销售额
			Integer adAmsSalesVolume = 0; //站外event广告销量
			Float adAmsFee = 0f; //站外event广告费用
			try {//AMS广告
				adAmsFee = amsAdFeeMap.get(accountName).get(productName).getTotalSpend();
				adAmsSales = amsAdFeeMap.get(accountName).get(productName).getWeekSameSkuUnitsSales();
				adAmsSalesVolume = amsAdFeeMap.get(accountName).get(productName).getWeekSameSkuUnitsOrdered();
				adAmsFee = getVatFee(adAmsFee, country);
			} catch (NullPointerException e) {}//AMS广告成本
			Float dealFee = 0f; //闪促费用
			Integer dealSalesVolume = 0; //闪促销量
			Float dealProfit = 0f; //闪促盈亏
			try {//闪促费用
				dealFee = dealsFeeMap.get(productName).get(accountName).getDealFee();
				dealProfit = dealsFeeMap.get(productName).get(accountName).getConv1();
				dealSalesVolume = dealsFeeMap.get(productName).get(accountName).getActualQuantity();
				dealFee = getVatFee(dealFee, country);
			} catch (NullPointerException e) {}
			//税后收入-亚马逊费用-其它费用-退款-运费-采购成本
			float profits = salesNoTax + amazonFee + otherFee + refund - transportFee;
			String line = typeLine.get(type.toLowerCase());
			Map<String, SaleProfit> countryMap = rs.get(country);
			if (countryMap == null) {
				countryMap = Maps.newHashMap();
				rs.put(country, countryMap);
			}
			SaleProfit saleProfit = saleProfits.get(date+productName+country+accountName);
			if (saleProfit == null) {
				saleProfit = new SaleProfit();
			}
			//统计模具费用
			if (moldFeeMap.get(productName) != null) {
				saleProfit.setMoldFee(salesVolume * MathUtils.getRate("CNY", "EUR", rateRs));
			}
			saleProfit.setSales(sales);
			saleProfit.setSalesVolume(salesVolume);
			saleProfit.setFeeQuantity(quantity);	//费用数量
			//销量为0或者结算数量大于0并且结算数量与销量相差小于5个时覆盖预估数据
			boolean flag = false;	//是否重新计算利润标记(不计算即使用估算的数据)
			if (salesVolume==0 || (quantity >0 && salesVolume > 0 && salesVolume-quantity<5)) {
				saleProfit.setSalesNoTax(salesNoTax);
				saleProfit.setAmazonFee(amazonFee);
				saleProfit.setOtherFee(otherFee);
				saleProfit.setTransportFee(transportFee);
				flag = true;
			}
			
			if (saleProfit.getBuyCost() == null || saleProfit.getBuyCost() == 0) {
				//采购成本记录单价
				float buyCost = 0f;//采购成本
				try {
					buyCost = buyCostMap.get(productName);
				} catch (NullPointerException e) {
					buyCost = 0f;
				}
				saleProfit.setBuyCost(buyCost);	//采购单价
			}
			saleProfit.setRefund(refund);
			saleProfit.setCountry(country);
			saleProfit.setAccountName(accountName);
			saleProfit.setType(type);
			saleProfit.setDay(date);
			saleProfit.setLine(line);
			saleProfit.setProductName(productName);
			saleProfit.setReturnNum(returnNum);
			saleProfit.setSupportNum(supportNum);
			saleProfit.setSupportCost(supportCost + saleProfit.getBuyCost() * supportNum);
			saleProfit.setSupportAmazonFee(supportAmazonFee);
			/*updateReviewOrder单独处理，此处不再更新
			saleProfit.setReviewNum(reviewNum);
			saleProfit.setReviewCost(reviewCost + saleProfit.getBuyCost() * reviewNum);
			saleProfit.setReviewAmazonFee(reviewAmazonFee);*/
			//减去替代货成本(暂时减去替代货的成本,广告费用暂不考虑)
			//profits = profits + supportAmazonFee - supportCost - buyCost;
			if (flag) {	//按照实际利润计算
				saleProfit.setProfits(profits + supportAmazonFee - saleProfit.getBuyCost()*(saleProfit.getFeeQuantity() + supportNum));
			} else{	//估算时利润已去除亚马逊费用、运费、采购费(此处只去除退款和替代成本)
				profits = saleProfit.getProfits()==null?0f:saleProfit.getProfits();
				profits = profits + refund - saleProfit.getBuyCost() * supportNum;
				saleProfit.setProfits(profits);
			}
			
			saleProfit.setAdInEventSales(adInEventSales);
			saleProfit.setAdInEventSalesVolume(adInEventSalesVolume);
			saleProfit.setAdInEventFee(adInEventFee);
			saleProfit.setAdInProfitSales(adInProfitSales);
			saleProfit.setAdInProfitSalesVolume(adInProfitSalesVolume);
			saleProfit.setAdInProfitFee(adInProfitFee);
			saleProfit.setAdOutEventSales(adOutEventSales);
			saleProfit.setAdOutEventSalesVolume(adOutEventSalesVolume);
			saleProfit.setAdOutEventFee(adOutEventFee);
			saleProfit.setAdOutProfitSales(adOutProfitSales);
			saleProfit.setAdOutProfitSalesVolume(adOutProfitSalesVolume);
			saleProfit.setAdOutProfitFee(adOutProfitFee);
			saleProfit.setAdAmsSales(adAmsSales);
			saleProfit.setAdAmsSalesVolume(adAmsSalesVolume);
			saleProfit.setAdAmsFee(adAmsFee);
			//TODO 仓储费另算以及加入长期仓储费
			saleProfit.setDealFee(dealFee);
			saleProfit.setDealProfit(dealProfit);
			saleProfit.setDealSalesVolume(dealSalesVolume);
			lists.add(saleProfit);
			countryMap.put(productName, saleProfit);
		}
		if (lists.size() > 0) {
			saveList(lists);
		}
	}
	
	private Map<String, SaleProfit> findSaleProfits(String day) {
		Map<String, SaleProfit> rs = Maps.newHashMap();
		DetachedCriteria dc = saleProfitDao.createDetachedCriteria();
		dc.add(Restrictions.eq("day", day));
		List<SaleProfit> list = saleProfitDao.find(dc);
		for (SaleProfit saleProfit : list) {
			rs.put(saleProfit.getDay()+saleProfit.getProductName()+saleProfit.getCountry()+saleProfit.getAccountName(), saleProfit);
		}
		return rs;
	}

	/**
	 * 换算增加增值税后的费用
	 * @param adInEventFee
	 * @param country
	 * @return
	 */
	private Float getVatFee(Float adInEventFee, String country) {
		float vat = 0;
		if ("jp".equals(country)) {
			vat = adInEventFee * 0.08f;	//当前只有日本收取
		}
		return adInEventFee + vat;
	}

	//根据条件查询唯一记录
	public SaleProfit getByUnique(String day, String country, String productName, String accountName) {
		DetachedCriteria dc = saleProfitDao.createDetachedCriteria();
		dc.add(Restrictions.eq("day", day));
		dc.add(Restrictions.eq("country", country));
		dc.add(Restrictions.eq("productName", productName));
		dc.add(Restrictions.eq("accountName", accountName));
		List<SaleProfit> list = saleProfitDao.find(dc);
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 
	 * @param saleProfit(排除B2B销量、销售额、利润 2017-4-10)
	 * @param flag	1:按月  2：按天 2：按年
	 * @return map key 1:结果集合列表  2：总计
	 */
	public Map<String, List<SaleProfit>> getSalesProfitList(SaleProfit saleProfit, String flag, String groupType){
		Map<String, List<SaleProfit>> map = Maps.newHashMap();
		String temp = "";
		if (StringUtils.isNotEmpty(saleProfit.getCountry()) && !"total".equals(saleProfit.getCountry()) 
				&& !"en".equals(saleProfit.getCountry()) && !"eu".equals(saleProfit.getCountry()) && !"nonEn".equals(saleProfit.getCountry()) && !"noUs".equals(saleProfit.getCountry())) {
			temp = " AND t.`country`='"+saleProfit.getCountry()+"' ";
		} else if ("en".equals(saleProfit.getCountry())) {
			temp = " AND t.`country` in ('com','uk','ca') ";
		} else if ("eu".equals(saleProfit.getCountry())) {
			temp = " AND t.`country` in ('de','uk','fr','it','es') ";
		} else if ("nonEn".equals(saleProfit.getCountry())) {	//非英语国家
			temp = " AND t.`country` in ('de','fr','it','es','jp') ";
		} else if ("noUs".equals(saleProfit.getCountry())) {	//非美国
			temp = " AND t.`country` in ('de','uk','fr','it','es','jp','ca','mx') ";
		} 
		String temp1 = " t.`product_name` ";
		if ("1".equals(groupType)) {
			temp1 = " t.`type` ";
		} else if ("2".equals(groupType)) {
			temp1 = " t.`line` ";
		}
		
		String temp2 = "";
		if (StringUtils.isNotEmpty(saleProfit.getLine())) {
			temp2 = " AND t.`line`='"+saleProfit.getLine()+"' ";
		}
		if (saleProfit.getReturnNum() != null) {
			temp2 = " AND t.`type` is not null ";
		}
		String sql = "";
		Parameter parameter = new Parameter(saleProfit.getDay(), saleProfit.getEnd());
		if (StringUtils.isNotEmpty(flag) && "1".equals(flag)) {	//按月
			sql ="SELECT t.`product_name`,t.`type`,t.`line`,SUM(t.`sales`-IFNULL(t.`market_sales`,0)),SUM(t.`sales_volume`-IFNULL(t.`market_num`,0)),SUM(t.`profits`-IFNULL(t.`market_profit`,0)),SUM(t.`sales_no_tax`), "+ 
					" SUM(t.`amazon_fee`),SUM(t.`refund`),SUM(t.`buy_cost`),SUM(t.`other_fee`),SUM(t.`transport_fee`),t.`month`, " +
					" SUM(t.`ad_in_event_fee`), SUM(t.`ad_in_profit_fee`),SUM(t.`ad_out_event_fee`),SUM(t.`ad_out_profit_fee`),"+
					" SUM(t.`ad_in_event_sales`), SUM(t.`ad_in_profit_sales`),SUM(t.`ad_out_event_sales`),SUM(t.`ad_out_profit_sales`),"+
					" SUM(t.`ad_in_event_sales_volume`), SUM(t.`ad_in_profit_sales_volume`),SUM(t.`ad_out_event_sales_volume`),SUM(t.`ad_out_profit_sales_volume`),"+
					" SUM(t.`support_num`),SUM(t.`support_amazon_fee`),SUM(t.`support_cost`),SUM(t.`review_num`),SUM(t.`review_amazon_fee`),SUM(t.`review_cost`)," +
					" SUM(t.`recall_num`),SUM(t.`recall_cost`),SUM(t.`recall_fee`),SUM(IFNULL(t.`tariff`,0)),SUM(IFNULL(t.`storage_fee`,0)),SUM(IFNULL(t.`fee_quantity`,0)),"+
					" SUM(IFNULL(t.`ad_ams_sales`,0)) ,SUM(IFNULL(t.`ad_ams_sales_volume`,0)),SUM(IFNULL(t.`ad_ams_fee`,0)),"+
					" SUM(IFNULL(t.`long_storage_fee`,0)) ,SUM(IFNULL(t.`deal_fee`,0)),SUM(IFNULL(t.`deal_sales_volume`,0)),SUM(IFNULL(t.`deal_profit`,0)),SUBSTRING_INDEX(group_concat(distinct t.product_attr order by FIELD(t.country,'com','de','uk','fr','jp','it','es','ca','mx'),FIELD(t.product_attr,'淘汰','新品','主力','普通')),',',1),"+
					" SUM(IFNULL(t.`mold_fee`,0)),SUM(IFNULL(t.`express_fee`,0)),SUM(IFNULL(t.`vine_fee`,0)),SUM(IFNULL(t.`vine_num`,0)),SUM(IFNULL(t.`vine_cost`,0)) "+
					" FROM `amazoninfo_report_month_type` t WHERE t.`month` >=:p1 AND t.`month` <=:p2 "+temp+temp2+" GROUP BY "+temp1+",t.`month` ORDER BY t.`month`,SUM(t.`sales_volume`) DESC ";
		} else if (StringUtils.isNotEmpty(flag) && "3".equals(flag)) {//按年
			parameter = new Parameter(saleProfit.getDay()+"01", saleProfit.getEnd()+"12");
			sql ="SELECT t.`product_name`,t.`type`,t.`line`,SUM(t.`sales`-IFNULL(t.`market_sales`,0)),SUM(t.`sales_volume`-IFNULL(t.`market_num`,0)),SUM(t.`profits`-IFNULL(t.`market_profit`,0)),SUM(t.`sales_no_tax`), "+ 
					" SUM(t.`amazon_fee`),SUM(t.`refund`),SUM(t.`buy_cost`),SUM(t.`other_fee`),SUM(t.`transport_fee`),SUBSTRING(t.`month`,1,4), " +
					" SUM(t.`ad_in_event_fee`), SUM(t.`ad_in_profit_fee`),SUM(t.`ad_out_event_fee`),SUM(t.`ad_out_profit_fee`),"+
					" SUM(t.`ad_in_event_sales`), SUM(t.`ad_in_profit_sales`),SUM(t.`ad_out_event_sales`),SUM(t.`ad_out_profit_sales`),"+
					" SUM(t.`ad_in_event_sales_volume`), SUM(t.`ad_in_profit_sales_volume`),SUM(t.`ad_out_event_sales_volume`),SUM(t.`ad_out_profit_sales_volume`),"+
					" SUM(t.`support_num`),SUM(t.`support_amazon_fee`),SUM(t.`support_cost`),SUM(t.`review_num`),SUM(t.`review_amazon_fee`),SUM(t.`review_cost`)," +
					" SUM(t.`recall_num`),SUM(t.`recall_cost`),SUM(t.`recall_fee`),SUM(IFNULL(t.`tariff`,0)),SUM(IFNULL(t.`storage_fee`,0)),SUM(IFNULL(t.`fee_quantity`,0)),"+
					" SUM(IFNULL(t.`ad_ams_sales`,0)) ,SUM(IFNULL(t.`ad_ams_sales_volume`,0)),SUM(IFNULL(t.`ad_ams_fee`,0)),"+
					" SUM(IFNULL(t.`long_storage_fee`,0)) ,SUM(IFNULL(t.`deal_fee`,0)),SUM(IFNULL(t.`deal_sales_volume`,0)),SUM(IFNULL(t.`deal_profit`,0)),SUBSTRING_INDEX(group_concat(distinct t.product_attr order by FIELD(t.country,'com','de','uk','fr','jp','it','es','ca','mx'),FIELD(t.product_attr,'淘汰','新品','主力','普通')),',',1),"+
					" SUM(IFNULL(t.`mold_fee`,0)),SUM(IFNULL(t.`express_fee`,0)),SUM(IFNULL(t.`vine_fee`,0)),SUM(IFNULL(t.`vine_num`,0)),SUM(IFNULL(t.`vine_cost`,0)) "+
					" FROM `amazoninfo_report_month_type` t WHERE t.`month` >=:p1 AND t.`month` <=:p2 "+temp+temp2+" GROUP BY "+temp1+",SUBSTRING(t.`month`,1,4) ORDER BY t.`month`,SUM(t.`sales_volume`) DESC ";
		} else {
			sql ="SELECT t.`product_name`,t.`type`,t.`line`,SUM(t.`sales`-IFNULL(t.`market_sales`,0)),SUM(t.`sales_volume`-IFNULL(t.`market_num`,0)),SUM(t.`profits`-IFNULL(t.`market_profit`,0)),SUM(t.`sales_no_tax`), "+ 
					" SUM(t.`amazon_fee`),SUM(t.`refund`),SUM(t.`buy_cost`* (case when t.`fee_quantity` >0 then t.`fee_quantity`  else t.`sales_volume` end) ),SUM(t.`other_fee`),SUM(t.`transport_fee`),t.`day`, " +
					" SUM(t.`ad_in_event_fee`), SUM(t.`ad_in_profit_fee`),SUM(t.`ad_out_event_fee`),SUM(t.`ad_out_profit_fee`),"+
					" SUM(t.`ad_in_event_sales`), SUM(t.`ad_in_profit_sales`),SUM(t.`ad_out_event_sales`),SUM(t.`ad_out_profit_sales`),"+
					" SUM(t.`ad_in_event_sales_volume`), SUM(t.`ad_in_profit_sales_volume`),SUM(t.`ad_out_event_sales_volume`),SUM(t.`ad_out_profit_sales_volume`),"+
					" SUM(t.`support_num`),SUM(t.`support_amazon_fee`),SUM(t.`support_cost`),SUM(t.`review_num`),SUM(t.`review_amazon_fee`),SUM(t.`review_cost`)," +
					" SUM(t.`recall_num`),SUM(t.`recall_cost`),SUM(t.`recall_fee`),SUM(IFNULL(t.`tariff`,0)*(case when t.`fee_quantity` is null then t.`sales_volume` else t.`fee_quantity` end)),SUM(IFNULL(t.`storage_fee`,0)),SUM(IFNULL(t.`fee_quantity`,0)),"+
					" SUM(IFNULL(t.`ad_ams_sales`,0)) ,SUM(IFNULL(t.`ad_ams_sales_volume`,0)),SUM(IFNULL(t.`ad_ams_fee`,0)),"+
					" 0 ,SUM(IFNULL(t.`deal_fee`,0)),SUM(IFNULL(t.`deal_sales_volume`,0)),SUM(IFNULL(t.`deal_profit`,0)),SUBSTRING_INDEX(group_concat(distinct t.product_attr order by FIELD(t.country,'com','de','uk','fr','jp','it','es','ca','mx'),FIELD(t.product_attr,'淘汰','新品','主力','普通')),',',1),"+
					" SUM(IFNULL(t.`mold_fee`,0)),SUM(IFNULL(t.`express_fee`,0)),SUM(IFNULL(t.`vine_fee`,0)),SUM(IFNULL(t.`vine_num`,0)),SUM(IFNULL(t.`vine_cost`,0)) "+
					" FROM `amazoninfo_sale_profit` t WHERE t.`day` >=:p1 AND t.`day` <=:p2 "+temp+temp2+" GROUP BY "+temp1+" ORDER BY SUM(t.`sales_volume`) DESC";
		}
		
		List<Object[]> list = saleProfitDao.findBySql(sql, parameter);
		
		List<SaleProfit> rs = Lists.newArrayList();
		map.put("1", rs);
		SaleProfit totalProfit = new SaleProfit();
		totalProfit.setSales(0f);
		totalProfit.setSalesVolume(0);
		totalProfit.setProfits(0f);
		totalProfit.setSalesNoTax(0f);
		totalProfit.setAmazonFee(0f);
		totalProfit.setRefund(0f);
		totalProfit.setBuyCost(0f);
		totalProfit.setOtherFee(0f);
		totalProfit.setTransportFee(0f);
		totalProfit.setAdInEventFee(0f);
		totalProfit.setAdInProfitFee(0f);
		totalProfit.setAdOutEventFee(0f);
		totalProfit.setAdOutProfitFee(0f);
		totalProfit.setAdInEventSales(0f);
		totalProfit.setAdInProfitSales(0f);
		totalProfit.setAdOutEventSales(0f);
		totalProfit.setAdOutProfitSales(0f);
		totalProfit.setAdInEventSalesVolume(0);
		totalProfit.setAdInProfitSalesVolume(0);
		totalProfit.setAdOutEventSalesVolume(0);
		totalProfit.setAdOutProfitSalesVolume(0);
		totalProfit.setSupportNum(0);
		totalProfit.setSupportAmazonFee(0f);
		totalProfit.setSupportCost(0f);
		totalProfit.setReviewNum(0);
		totalProfit.setReviewAmazonFee(0f);
		totalProfit.setReviewCost(0f);
		totalProfit.setRecallNum(0);
		totalProfit.setRecallCost(0f);
		totalProfit.setRecallFee(0f);
		totalProfit.setTariff(0f);
		totalProfit.setFeeQuantity(0);
		totalProfit.setAdAmsFee(0f);
		totalProfit.setAdAmsSales(0f);
		totalProfit.setAdAmsSalesVolume(0);
		totalProfit.setStorageFee(0f);
		totalProfit.setLongStorageFee(0f);
		totalProfit.setDealFee(0f);
		totalProfit.setDealProfit(0f);
		totalProfit.setDealSalesVolume(0);
		map.put("2", Lists.newArrayList(totalProfit));
		for (Object[] objs : list) {
			String productName = objs[0].toString();
			String type = objs[1]==null?null: objs[1].toString();
			String line =  objs[2]==null?null: objs[2].toString();
			Float sales = objs[3]==null?0:Float.parseFloat(objs[3].toString());
			Integer salesVolume = objs[4]==null?0:Integer.parseInt(objs[4].toString());
			Float profits = objs[5]==null?0:Float.parseFloat(objs[5].toString());
			Float salesNoTax = objs[6]==null?0:Float.parseFloat(objs[6].toString());	//税后销售额
			Float amazonFee = objs[7]==null?0:Float.parseFloat(objs[7].toString());
			Float refund = objs[8]==null?0:Float.parseFloat(objs[8].toString());
			Float buyCost = objs[9]==null?0:Float.parseFloat(objs[9].toString());
			Float otherFee = objs[10]==null?0:Float.parseFloat(objs[10].toString());
			Float transportFee = objs[11]==null?0:Float.parseFloat(objs[11].toString());
			String day = objs[12].toString();
			//站内站外广告费用(站外广告计算时为返点钱减去总花费,故按负数计算)
			Float adInEventFee = objs[13]==null?0:-Float.parseFloat(objs[13].toString());
			Float adInProfitFee = objs[14]==null?0:-Float.parseFloat(objs[14].toString());
			Float adOutEventFee = objs[15]==null?0:Float.parseFloat(objs[15].toString());
			Float adOutProfitFee = objs[16]==null?0:Float.parseFloat(objs[16].toString());
			//站内站外广告销售额
			Float adInEventSales = objs[17]==null?0:Float.parseFloat(objs[17].toString());
			Float adInProfitSales = objs[18]==null?0:Float.parseFloat(objs[18].toString());
			Float adOutEventSales = objs[19]==null?0:Float.parseFloat(objs[19].toString());
			Float adOutProfitSales = objs[20]==null?0:Float.parseFloat(objs[20].toString());
			//站内站外广告销量
			Integer adInEventSalesVolume = objs[21]==null?0:Integer.parseInt(objs[21].toString());
			Integer adInProfitSalesVolume = objs[22]==null?0:Integer.parseInt(objs[22].toString());
			Integer adOutEventSalesVolume = objs[23]==null?0:Integer.parseInt(objs[23].toString());
			Integer adOutProfitSalesVolume = objs[24]==null?0:Integer.parseInt(objs[24].toString());
			//利润去除广告费用
			profits = profits + adInEventFee + adInProfitFee + adOutEventFee + adOutProfitFee;
			//替代货和评测单
			Integer supportNum = objs[25]==null?0:Integer.parseInt(objs[25].toString());
			Float supportAmazonFee = objs[26]==null?0:Float.parseFloat(objs[26].toString());
			Float supportCost = objs[27]==null?0:Float.parseFloat(objs[27].toString());
			Integer reviewNum = objs[28]==null?0:Integer.parseInt(objs[28].toString());
			Float reviewAmazonFee = objs[29]==null?0:Float.parseFloat(objs[29].toString());
			Float reviewCost = objs[30]==null?0:Float.parseFloat(objs[30].toString());
			//召回数量和成本
			Integer recallNum = objs[31]==null?0:Integer.parseInt(objs[31].toString());
			Float recallCost = objs[32]==null?0:Float.parseFloat(objs[32].toString());
			Float recallFee = objs[33]==null?0:Float.parseFloat(objs[33].toString());
			//关税
			Float tariff = objs[34]==null?0:Float.parseFloat(objs[34].toString());
			//仓储费
			Float storageFee = objs[35]==null?0:Float.parseFloat(objs[35].toString());
			//已出结算报告费用数量
			Integer feeQuantity = objs[36]==null?0:Integer.parseInt(objs[36].toString());
			//AMS广告
			Float adAmsSales = objs[37]==null?0:Float.parseFloat(objs[37].toString());
			Integer adAmsSalesVolume = objs[38]==null?0:Integer.parseInt(objs[38].toString());
			Float adAmsFee = objs[39]==null?0:-Float.parseFloat(objs[39].toString());
			profits = profits + adAmsFee;
			

			//" SUM(IFNULL(t.`deal_fee`,0)),SUM(IFNULL(t.`deal_sales_volume`,0)),SUM(IFNULL(t.`deal_profit`,0))"+
			//长期仓储费
			Float longStorageFee = objs[40]==null?0:Float.parseFloat(objs[40].toString());
			//闪促费用、销量、盈亏
			Float dealFee = objs[41]==null?0:Float.parseFloat(objs[41].toString());
			Integer dealSalesVolume = objs[42]==null?0:Integer.parseInt(objs[42].toString());
			Float dealProfit = objs[43]==null?0:Float.parseFloat(objs[43].toString());
			Float moldFee = objs[45]==null?0:Float.parseFloat(objs[45].toString());
			buyCost = buyCost + moldFee;	//模具费算到采购成本
			profits = profits - moldFee;
			//TODO 利润减去仓储费和闪促费
			profits = profits + storageFee + longStorageFee + dealFee;
			
			//利润去除评测单(替代货在统计时已剔除)
			profits = profits + reviewAmazonFee - reviewCost - tariff;
			//利润去除召回单
			profits = profits - recallFee - recallCost;
			
			Float expressFee = objs[46]==null?0:Float.parseFloat(objs[46].toString());
			Float vineFee = objs[47]==null?0:Float.parseFloat(objs[47].toString());
			Integer vineNum = objs[48]==null?0:Integer.parseInt(objs[48].toString());
			Float vineCost = objs[49]==null?0:Float.parseFloat(objs[49].toString());
			//利润去除召回单
			profits = profits - expressFee - vineFee - vineCost;
			if ("1".equals(groupType)) {
				productName = type;
			} else if ("2".equals(groupType)) {
				productName = line;
			}

			SaleProfit profit = new SaleProfit();
			profit.setProductName(productName);
			profit.setType(type);
			profit.setLine(line);
			profit.setSales(sales);
			profit.setSalesVolume(salesVolume);
			profit.setProfits(profits);
			profit.setSalesNoTax(salesNoTax);
			profit.setAmazonFee(amazonFee);
			profit.setRefund(refund);
			profit.setBuyCost(buyCost);
			profit.setOtherFee(otherFee);
			profit.setTransportFee(transportFee);
			profit.setDay(day);
			profit.setAdInEventFee(adInEventFee);
			profit.setAdInProfitFee(adInProfitFee);
			profit.setAdOutEventFee(adOutEventFee);
			profit.setAdOutProfitFee(adOutProfitFee);
			profit.setAdInEventSales(adInEventSales);
			profit.setAdInProfitSales(adInProfitSales);
			profit.setAdOutEventSales(adOutEventSales);
			profit.setAdOutProfitSales(adOutProfitSales);
			profit.setAdInEventSalesVolume(adInEventSalesVolume);
			profit.setAdInProfitSalesVolume(adInProfitSalesVolume);
			profit.setAdOutEventSalesVolume(adOutEventSalesVolume);
			profit.setAdOutProfitSalesVolume(adOutProfitSalesVolume);
			profit.setSupportNum(supportNum);
			profit.setSupportAmazonFee(supportAmazonFee);
			profit.setSupportCost(supportCost);
			profit.setReviewNum(reviewNum);
			profit.setReviewAmazonFee(reviewAmazonFee);
			profit.setReviewCost(reviewCost);
			profit.setRecallNum(recallNum);
			profit.setRecallCost(recallCost);
			profit.setRecallFee(recallFee);
			profit.setTariff(tariff);
			profit.setFeeQuantity(feeQuantity);
			profit.setAdAmsSales(adAmsSales);
			profit.setAdAmsSalesVolume(adAmsSalesVolume);
			profit.setAdAmsFee(adAmsFee);
			profit.setStorageFee(storageFee);
			profit.setLongStorageFee(longStorageFee);
			profit.setDealFee(dealFee);
			profit.setDealProfit(dealProfit);
			profit.setDealSalesVolume(dealSalesVolume);
			String productAttr=(objs[44]==null?"":objs[44].toString());
			profit.setProductAttr(productAttr);
			profit.setMoldFee(moldFee);
			profit.setExpressFee(expressFee);
			profit.setVineFee(vineFee);
			profit.setVineCost(vineCost);
			profit.setVineNum(vineNum);
			rs.add(profit);

			totalProfit.setSales(totalProfit.getSales() + sales);
			totalProfit.setSalesVolume(totalProfit.getSalesVolume() + salesVolume);
			totalProfit.setProfits(totalProfit.getProfits() + profits);
			totalProfit.setSalesNoTax(totalProfit.getSalesNoTax() + salesNoTax);
			totalProfit.setAmazonFee(totalProfit.getAmazonFee() + amazonFee);
			totalProfit.setRefund(totalProfit.getRefund() + refund);
			totalProfit.setBuyCost(totalProfit.getBuyCost() + buyCost);
			totalProfit.setOtherFee(totalProfit.getOtherFee() + otherFee);
			totalProfit.setTransportFee(totalProfit.getTransportFee() + transportFee);
			totalProfit.setAdInEventFee(totalProfit.getAdInEventFee() + adInEventFee);
			totalProfit.setAdInProfitFee(totalProfit.getAdInProfitFee() + adInProfitFee);
			totalProfit.setAdOutEventFee(totalProfit.getAdOutEventFee() + adOutEventFee);
			totalProfit.setAdOutProfitFee(totalProfit.getAdOutProfitFee() + adOutProfitFee);
			totalProfit.setAdInEventSales(totalProfit.getAdInEventSales() + adInEventSales);
			totalProfit.setAdInProfitSales(totalProfit.getAdInProfitSales() + adInProfitSales);
			totalProfit.setAdOutEventSales(totalProfit.getAdOutEventSales() + adOutEventSales);
			totalProfit.setAdOutProfitSales(totalProfit.getAdOutProfitSales() + adOutProfitSales);
			totalProfit.setAdInEventSalesVolume(totalProfit.getAdInEventSalesVolume() + adInEventSalesVolume);
			totalProfit.setAdInProfitSalesVolume(totalProfit.getAdInProfitSalesVolume() + adInProfitSalesVolume);
			totalProfit.setAdOutEventSalesVolume(totalProfit.getAdOutEventSalesVolume() + adOutEventSalesVolume);
			totalProfit.setAdOutProfitSalesVolume(totalProfit.getAdOutProfitSalesVolume() + adOutProfitSalesVolume);
			totalProfit.setSupportNum(totalProfit.getSupportNum() + supportNum);
			totalProfit.setSupportAmazonFee(totalProfit.getSupportAmazonFee() + supportAmazonFee);
			totalProfit.setSupportCost(totalProfit.getSupportCost() + supportCost);
			totalProfit.setReviewNum(totalProfit.getReviewNum() + reviewNum);
			totalProfit.setReviewAmazonFee(totalProfit.getReviewAmazonFee() + reviewAmazonFee);
			totalProfit.setReviewCost(totalProfit.getReviewCost() + reviewCost);
			totalProfit.setRecallNum(totalProfit.getRecallNum() + recallNum);
			totalProfit.setRecallCost(totalProfit.getRecallCost() + recallCost);
			totalProfit.setRecallFee(totalProfit.getRecallFee() + recallFee);
			totalProfit.setTariff(totalProfit.getTariff() + tariff);
			totalProfit.setFeeQuantity(totalProfit.getFeeQuantity() + feeQuantity);
			totalProfit.setAdAmsSales(totalProfit.getAdAmsSales() + adAmsSales);
			totalProfit.setAdAmsSalesVolume(totalProfit.getAdAmsSalesVolume() + adAmsSalesVolume);
			totalProfit.setAdAmsFee(totalProfit.getAdAmsFee() + adAmsFee);
			totalProfit.setStorageFee(totalProfit.getStorageFee() + storageFee);
			totalProfit.setLongStorageFee(totalProfit.getLongStorageFee() + longStorageFee);
			totalProfit.setDealFee(totalProfit.getDealFee() + dealFee);
			totalProfit.setDealProfit(totalProfit.getDealProfit() + dealProfit);
			totalProfit.setDealSalesVolume(totalProfit.getDealSalesVolume() + dealSalesVolume);
			totalProfit.setMoldFee(totalProfit.getMoldFee() + moldFee);
			totalProfit.setExpressFee(totalProfit.getExpressFee() + expressFee);
			totalProfit.setVineCost(totalProfit.getVineCost() + vineCost);
			totalProfit.setVineFee(totalProfit.getVineFee() + vineFee);
			totalProfit.setVineNum(totalProfit.getVineNum() + vineNum);
		}
		return map;
	}
	
	/**
	 * 
	 * @param B2B
	 * @param flag	1:按月  2：按天 2：按年
	 */
	public List<SaleProfit> getMarketSalesList(SaleProfit saleProfit, String flag){
		List<SaleProfit> rs = Lists.newArrayList();
		String sql = "";
		Parameter parameter = new Parameter(saleProfit.getDay(), saleProfit.getEnd());
		if (StringUtils.isNotEmpty(flag) && "1".equals(flag)) {	//按月
			sql ="SELECT t.`product_name`,t.`line`,t.`month`,SUM(t.`market_sales`),SUM(t.`market_num`),SUM(IFNULL(t.`market_profit`,0)),t.`country`"+
					" FROM `amazoninfo_report_month_type` t WHERE t.`market_sales`>0 AND t.`month` >=:p1 AND t.`month` <=:p2  GROUP BY t.`product_name`,t.`month`,t.`country` ORDER BY t.`month`";
		} else if (StringUtils.isNotEmpty(flag) && "3".equals(flag)) {//按年
			parameter = new Parameter(saleProfit.getDay()+"01", saleProfit.getEnd()+"12");
			sql ="SELECT t.`product_name`,t.`line`,SUBSTRING(t.`month`,1,4),SUM(t.`market_sales`),SUM(t.`market_num`),SUM(IFNULL(t.`market_profit`,0)),t.`country`"+
					" FROM `amazoninfo_report_month_type` t WHERE t.`market_sales`>0 AND t.`month` >=:p1 AND t.`month` <=:p2 GROUP BY t.`product_name`,SUBSTRING(t.`month`,1,4),t.`country` ORDER BY t.`month`";
		} else {
			sql ="SELECT t.`product_name`,t.`line`,t.`day`,SUM(t.`market_sales`),SUM(t.`market_num`),SUM(IFNULL(t.`market_profit`,0)),t.`country`"+
					" FROM `amazoninfo_sale_profit` t WHERE t.`market_sales`>0 AND t.`day` >=:p1 AND t.`day` <=:p2 GROUP BY t.`product_name`,t.`country`,t.`day` ORDER BY t.`day`";
		}
		
		List<Object[]> list = saleProfitDao.findBySql(sql, parameter);
		
		for (Object[] objs : list) {
			String productName = objs[0].toString();
			String line = objs[1]==null?null: objs[1].toString();
			String day = objs[2].toString();
			Float sales = objs[3]==null?0:Float.parseFloat(objs[3].toString());
			Integer salesVolume = objs[4]==null?0:Integer.parseInt(objs[4].toString());
			Float profits = objs[5]==null?0:Float.parseFloat(objs[5].toString());
			String country = objs[6].toString();

			SaleProfit profit = new SaleProfit();
			profit.setProductName(productName);
			profit.setLine(line);
			profit.setSales(sales);
			profit.setSalesVolume(salesVolume);
			profit.setProfits(profits);
			profit.setDay(day);
			profit.setCountry(country);
			rs.add(profit);
		}
		return rs;
	}
	
	public Float getSalesProfit(SaleProfit saleProfit, String flag){
		String sql = "";
		Parameter parameter = null;
		if (StringUtils.isNotEmpty(flag) && "1".equals(flag)) {
			parameter = new Parameter(saleProfit.getDay(), saleProfit.getEnd());
			sql ="SELECT SUM(t.`sales`) "+
					" FROM `amazoninfo_report_month_type` t WHERE t.`month` >=:p1 AND t.`month` <=:p2";
		} else if (StringUtils.isNotEmpty(flag) && "3".equals(flag)) {
			parameter = new Parameter(saleProfit.getDay()+"01", saleProfit.getEnd()+"12");
			sql ="SELECT SUM(t.`sales`)"+ 
					" FROM `amazoninfo_report_month_type` t WHERE t.`month` >=:p1 AND t.`month` <=:p2";
		} else {
			parameter = new Parameter(saleProfit.getDay(), saleProfit.getEnd());
			sql ="SELECT SUM(t.`sales`)"+ 
					" FROM `amazoninfo_sale_profit` t WHERE t.`day` >=:p1 AND t.`day` <=:p2 ";
		}
		List<Object> list = saleProfitDao.findBySql(sql, parameter);
		if (list.size() > 0 && list.get(0) !=null) {
			return ((BigDecimal)list.get(0)).floatValue();
		}
		return 0f;
	}

	public String getTips(String flag, SaleProfit saleProfit) throws ParseException {
		String rs = "";
		String sql = "";
		List<Object[]> list = Lists.newArrayList();
		String temp = "";
		if (StringUtils.isNotEmpty(saleProfit.getCountry()) && !"total".equals(saleProfit.getCountry()) 
				&& !"en".equals(saleProfit.getCountry()) && !"eu".equals(saleProfit.getCountry())) {
			temp = " AND t.`country`='"+saleProfit.getCountry()+"' ";
		} else if ("en".equals(saleProfit.getCountry())) {
			temp = " AND t.`country` in ('com','uk','ca') ";
		} else if ("eu".equals(saleProfit.getCountry())) {
			temp = " AND t.`country` in ('de','uk','fr','it','es') ";
		}
		String start = saleProfit.getDay();
		String end = saleProfit.getEnd();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		if ("2".equals(flag)) {
			sql = "SELECT SUM(t.`sales_volume`),SUM(IFNULL(t.`fee_quantity`,0)) FROM `amazoninfo_sale_report` t WHERE t.`order_type`='1' "+temp+" AND t.`date`>=:p1 AND t.`date`<=:p2";
			list = saleProfitDao.findBySql(sql, new Parameter(format.parse(start), DateUtils.getLastDayOfMonth(format.parse(end))));
		} else if ("3".equals(flag)) {
			start = start + "0101";
			end = end + "0101";
			sql = "SELECT SUM(t.`sales_volume`),SUM(IFNULL(t.`fee_quantity`,0)) FROM `amazoninfo_sale_report` t WHERE t.`order_type`='1' "+temp+"  AND t.`date`>=:p1 AND t.`date`<=:p2";
			list = saleProfitDao.findBySql(sql, new Parameter(format.parse(start), DateUtils.getLastDayOfMonth(format.parse(end))));
		} else {
			start = start + "01";
			end = end + "01";
			sql = "SELECT SUM(t.`sales_volume`),SUM(IFNULL(t.`fee_quantity`,0)) FROM `amazoninfo_sale_report` t WHERE t.`order_type`='1' "+temp+"  AND t.`date`>=:p1 AND t.`date`<=:p2";
			list = saleProfitDao.findBySql(sql, new Parameter(format.parse(start), DateUtils.getLastDayOfMonth(format.parse(end))));
		}
		float ratio = 0.01f;	//偏差大于1%显示提示信息
		for (Object[] obj : list) {
			int saleVolume = obj[0]==null?0:Integer.parseInt(obj[0].toString());
			int feeQuantity = obj[1]==null?0:Integer.parseInt(obj[1].toString());
			if (saleVolume > 0) {
				float cha = saleVolume - feeQuantity;
				if (cha/saleVolume > ratio) {
					rs = "提示：所选时间范围内部分亚马逊结算报告还未生成,相关利润数据偏差较大,请知悉！";
				}
			}
		}
		return rs;
	}
	

	//获取最近的订单结算时间
	public Date getSettlementDate(SaleProfit saleProfit) throws ParseException {
		String temp = "";
		if (StringUtils.isNotEmpty(saleProfit.getCountry()) && !"total".equals(saleProfit.getCountry()) 
				&& !"en".equals(saleProfit.getCountry()) && !"eu".equals(saleProfit.getCountry())) {
			temp = " AND t.`country`='"+saleProfit.getCountry()+"' ";
		} else if ("en".equals(saleProfit.getCountry())) {
			temp = " AND t.`country` in ('com','uk','ca') ";
		} else if ("eu".equals(saleProfit.getCountry())) {
			temp = " AND t.`country` in ('de','uk','fr','it','es') ";
		}
		String sql = "SELECT MAX(DATE_FORMAT(t.`posted_date`,'%Y%m%d')) AS dat FROM `amazoninfo_financial` t " +
				" WHERE t.`posted_date` >= DATE_ADD(CURDATE(),INTERVAL -15 DAY) "+temp+" GROUP BY country";
		List<Object> list = saleProfitDao.findBySql(sql);
		int min = 21000101;	//无实际作用仅用于比对
		for (Object obj : list) {
			int dat = obj==null?0:Integer.parseInt(obj.toString());
			if (min == 0 || min > dat) {
				min = dat;
			}
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.parse(min+"");
	}
	
	//获取没有填入运费的数据
	public List<SaleReport> findNoFreight() {
		List<SaleReport> rs = Lists.newArrayList();
		String sql = "SELECT t.`id`,t.`product_name`,t.`color`,t.`country`,DATE_FORMAT(t.`date`,'%Y%m%d') "+
				" FROM `amazoninfo_sale_report` t WHERE DATE_FORMAT(t.`date`,'%Y')>='2016' AND t.`product_name` IS NOT NULL AND t.`avg_freight` IS NULL LIMIT 10000";
        List<Object[]>  list = saleProfitDao.findBySql(sql, null);
        for (Object[] obj : list) {
			Integer id = Integer.parseInt(obj[0].toString());
			String productName = obj[1].toString();
			String color = obj[2]==null?"":obj[2].toString();
			if (StringUtils.isNotEmpty(color)) {
				productName = productName + "_" + color;
			}
			String country = obj[3].toString();
			String dateStr = obj[4].toString();
			SaleReport saleReport = new SaleReport();
			saleReport.setId(id);
			saleReport.setProductName(productName);	//包含颜色
			saleReport.setCountry(country);
			saleReport.setClassType(dateStr);	//存入日期字符串
			rs.add(saleReport);
		}
        return rs;
	}
	
	//获取所有的运费数据[Map<productName_country, Map<dateStr, avgPrice>>]
	public Map<String, Map<String, Float>> findAllFreight(Map<String,Map<String,Float>> allRate) {
		Map<String, Map<String, Float>> rs = Maps.newHashMap();
		String sql = "SELECT t.`product_name`,t.`country`, t.`avg_price`,DATE_FORMAT(t.`update_date`,'%Y%m%d') FROM `psi_product_avg_price` t";
        List<Object[]>  list = saleProfitDao.findBySql(sql, null);
        for (Object[] obj : list) {
			String productName = obj[0].toString();
			String country = obj[1].toString();	// EU,US,JP
			String key = productName + "_" + country;
			float avgPrice = Float.parseFloat(obj[2].toString());
			String dateStr = obj[3].toString();
			avgPrice = avgPrice * MathUtils.getRate("CNY", "EUR", allRate.get(dateStr));	//按当时的汇率转换成欧元
			Map<String, Float> priceMap = rs.get(key);
			if (priceMap == null) {
				priceMap = Maps.newHashMap();
				rs.put(key, priceMap);
			}
			priceMap.put(dateStr, avgPrice);
		}
        return rs;
	}
	
	//获取所有的运费数据的最小时间[Map<productName_country, dateStr>]
	public Map<String, String> findAllFreightMinDate() {
		Map<String, String> rs = Maps.newHashMap();
		String sql = "SELECT t.`product_name`,t.`country`,DATE_FORMAT(MIN(t.`update_date`),'%Y%m%d') FROM `psi_product_avg_price` t GROUP BY t.`product_name`,t.`country`";
        List<Object[]>  list = saleProfitDao.findBySql(sql, null);
        for (Object[] obj : list) {
			String productName = obj[0].toString();
			String country = obj[1].toString();	// EU,US,JP
			String key = productName + "_" + country;
			String dateStr = obj[2].toString();
			rs.put(key, dateStr);
		}
        return rs;
	}
	
	@Transactional(readOnly = false)
	public void updateFreight(List<SaleReport> list, Map<String, String> minDateMap, Map<String, Map<String, Float>> allFreight) {
		String sql = "UPDATE `amazoninfo_sale_report` t SET t.`avg_freight`=:p1 WHERE t.`id`=:p2";
		for (SaleReport saleReport : list) {
			String productName = saleReport.getProductName();
			String country = saleReport.getCountry();
			String key = productName + "_" + country.toUpperCase();
			if ("de,uk,fr,it,es".contains(country)) {
				key = productName + "_EU";
			} else if ("com,ca".contains(country)) {
				key = productName + "_US";
			}
			float avgFreight = getFreight(key,saleReport.getClassType(), minDateMap, allFreight);
			saleProfitDao.updateBySql(sql, new Parameter(avgFreight, saleReport.getId()));
		}
	}
	
	/**
	 * 计算产品运输费
	 * @param key			产品名称_国家
	 * @param dateStr		订单时间
	 * @param minDateMap	最小记录时间[Map<productName_country(EU,US,JP), dateStr>]
	 * @param allFreight	所有运费数据[Map<productName_country(EU,US,JP), Map<dateStr, avgPrice>>]
	 */
	public float getFreight(String key, String dateStr, Map<String, String> minDateMap, Map<String, Map<String, Float>> allFreight) {
		if (minDateMap.get(key) == null) {	//没有运费记录
			return -1f;
		}
		//有运费记录：订单时间小于最小记录时间,统一按最小记录时间的运费计算,否则按最近的一天计算
		if (Integer.parseInt(dateStr) <= Integer.parseInt(minDateMap.get(key))) {
			return allFreight.get(key).get(minDateMap.get(key));
		} else {
			Map<String, Float> priceMap = allFreight.get(key);
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			float avgFreight = -1f;
			try {
				Date date = format.parse(dateStr);
				while (true) {
					if (priceMap.get(format.format(date)) != null) {
						avgFreight = priceMap.get(format.format(date));
						break;
					}
					date = DateUtils.addDays(date, -1);
				}
			} catch (ParseException e) {}
			return avgFreight;
		}
	}
			
	//获取指定日期的SaleReport
	public List<SaleReport> findSaleReportListByDay(String day) {
		List<SaleReport> rs = Lists.newArrayList();
		String sql = "SELECT t.`id`,t.`product_name`,t.`color`,t.`country`,t.`price`,t.`sku`"+
				" FROM `amazoninfo_sale_report` t WHERE DATE_FORMAT(t.`date`,'%Y%m%d')=:p1 AND t.`product_name` IS NOT NULL AND t.`sku` IS NOT NULL";
        List<Object[]>  list = saleProfitDao.findBySql(sql, new Parameter(day));
        for (Object[] obj : list) {
			Integer id = Integer.parseInt(obj[0].toString());
			String productName = obj[1].toString();
			String color = obj[2]==null?"":obj[2].toString();
			if (StringUtils.isNotEmpty(color)) {
				productName = productName + "_" + color;
			}
			String country = obj[3].toString();
			String sku = obj[5].toString();
			SaleReport saleReport = new SaleReport();
			saleReport.setId(id);
			saleReport.setProductName(productName);	//包含颜色
			saleReport.setCountry(country);
			if (obj[4] != null) {
				saleReport.setPrice(Float.parseFloat(obj[4].toString()));
			}
			saleReport.setSku(sku);
			rs.add(saleReport);
		}
        return rs;
	}

	//sku对应的产品名称[sku  productname]]]
	public Map<String, String> findSkuNames() {
		Map<String, String> rs = Maps.newHashMap();
		String sql = "SELECT DISTINCT t.`sku`,t.`product_name`,t.`color` FROM psi_sku t WHERE  t.`del_flag`='0'";
        List<Object[]>  list = saleProfitDao.findBySql(sql, null);
        for (Object[] obj : list) {
			String sku = obj[0].toString();
			String productName = obj[1].toString();
			String color = obj[2]==null?"":obj[2].toString();
			if (StringUtils.isNotEmpty(color)) {
				productName = productName + "_" + color;
			}
			rs.put(sku, productName);
		}
        return rs;
	}

	//获取产品退货数[日期[国家[sku  退货数]]]
	public Map<String, Map<String, Map<String, Integer>>> findReturnNums(String day, Map<String, String> skuNameMap) {
		Map<String, Map<String, Map<String, Integer>>> rs = Maps.newHashMap();
		String sql = "SELECT aa.country,aa.sku,SUM(aa.num),DATE_FORMAT(b.`purchase_date`,'%Y%m%d') AS days FROM " +
				" (SELECT a.`sku`,a.`country`,SUM(a.`quantity`) AS num ,a.`order_id` FROM amazoninfo_return_goods a GROUP BY a.`order_id`,a.`sku`,a.`country`) aa ," +
				"amazoninfo_order b WHERE aa.`order_id` = b.`amazon_order_id` AND DATE_FORMAT(b.`purchase_date`,'%Y%m%d')>=:p1 GROUP BY days,aa.country,aa.sku";
        List<Object[]>  list = saleProfitDao.findBySql(sql, new Parameter(day));
        for (Object[] obj : list) {
			String country = obj[0].toString();
			String sku = obj[1].toString();
			Integer num = Integer.parseInt(obj[2].toString());
			String date = obj[3].toString();
			Map<String, Map<String, Integer>> dateMap = rs.get(date);
			if (dateMap == null) {
				dateMap = Maps.newHashMap();
				rs.put(date, dateMap);
			}
			Map<String, Integer> countryMap = dateMap.get(country);
			if (countryMap == null) {
				countryMap = Maps.newHashMap();
				dateMap.put(country, countryMap);
			}
			countryMap.put(sku, num);
		}
        return rs;
	}

	//获取产品保本价[sku_国家 保本价]
	public Map<String, Float> findPriceMapByDay(String day, Map<String,Float> rate) {
		if (Integer.parseInt(day) < 20160106) {	//最早的时间为20160106
			day = "20160106";
		}
		Map<String, Float> rs = Maps.newHashMap();
		String sql = "SELECT t.`product_name`,t.`country`,t.`amz_price`,t.`sku` FROM `amazoninfo_product_price` t WHERE DATE_FORMAT(t.date,'%Y%m%d')=:p1";
        List<Object[]>  list = saleProfitDao.findBySql(sql, new Parameter(day));
        if(list == null || list.size() == 0){
        	sql = "SELECT MAX(a.`date`) FROM amazoninfo_product_price a ";
    		List<Object> dateList =  saleProfitDao.findBySql(sql);
    		if(dateList.size() > 0){
    			sql = "SELECT t.`product_name`,t.`country`,t.`amz_price`,t.`sku` FROM `amazoninfo_product_price` t  WHERE t.`date`=:p1 ";
    			list =  saleProfitDao.findBySql(sql,new Parameter(dateList.get(0)));
    		}
        }
        float crate = MathUtils.getRate("USD", "EUR", rate);
        for (Object[] obj : list) {
			String country = obj[1].toString();
			float amzPrice = Float.parseFloat(obj[2].toString());
			String sku = obj[3].toString();
			rs.put(sku + "_" + country, amzPrice * crate);
		}
        return rs;
	}

	@Transactional(readOnly = false)
	public void updatePrice(List<SaleReport> reports, String dateStr, 
			Map<String, Map<String, Map<String, Integer>>> returns, Map<String, Float> priceMap) {
		String sql = "UPDATE `amazoninfo_sale_report` t SET t.`price`=:p1,t.return_num=:p2 WHERE t.`id`=:p3";
		String sqlPrice = "UPDATE `amazoninfo_sale_report` t SET t.`price`=:p1 WHERE t.`id`=:p2";
		String sqlReturn = "UPDATE `amazoninfo_sale_report` t SET t.return_num=:p1 WHERE t.`id`=:p2";
		for (SaleReport saleReport : reports) {
			String sku = saleReport.getSku();
			String country = saleReport.getCountry();
			String key = sku + "_" + country;
			int num = 0;
			try {
				num = returns.get(dateStr).get(country).get(sku);
			} catch (NullPointerException e) {}
			if (saleReport.getPrice() == null && priceMap.get(key) != null) {
				if (num > 0) {
					saleProfitDao.updateBySql(sql, new Parameter(priceMap.get(key), num, saleReport.getId()));
				} else {
					saleProfitDao.updateBySql(sqlPrice, new Parameter(priceMap.get(key), saleReport.getId()));
				}
			} else if (num > 0) {
				saleProfitDao.updateBySql(sqlReturn, new Parameter(num, saleReport.getId()));
			}
		}
	}

	//获取产品替代货数量和评测数量[国家[产品 [类型   数量]]] ReviewOrSupport状态算替代,Paypal_Refund为线下退款，推广人员统计后导入系统
	public Map<String, Map<String, AmazonOutboundOrder>> findSupportAndReviewByDay(String day, Map<String, Float> rate) {
		Map<String, Map<String, AmazonOutboundOrder>> rs = Maps.newHashMap();
		String sql = "SELECT t.`country`,SUM(i.`quantity_ordered`),i.`product_name`,i.`color`,t.`order_type`,SUM(IFNULL(t.`amazon_fee`,0)),t.`account_name`"+
				" FROM `amazoninfo_outbound_order` t,`amazoninfo_outbound_orderitem` i "+
				" WHERE i.`order_id`=t.`id` AND DATE_FORMAT(t.`create_date`,'%Y%m%d')=:p1 AND t.`order_status`='COMPLETE' AND t.`order_type` IN('Review','ReviewOrSupport','Support','Paypal_Refund')"+
				" GROUP BY i.`product_name`,i.`color`,t.`account_name`,t.`order_type` ORDER BY t.`country`";
        List<Object[]>  list = saleProfitDao.findBySql(sql, new Parameter(day));
        for (Object[] obj : list) {
			String country = obj[0].toString();
			Integer quantity = obj[1]==null?0:Integer.parseInt(obj[1].toString());
			String productName = obj[2].toString();
			String color = obj[3]==null?"":obj[3].toString();
			if (StringUtils.isNotEmpty(color)) {
				productName = productName + "_" + color;
			}
			String orderType = obj[4].toString();
			Float fee = obj[5]==null?0:Float.parseFloat(obj[5].toString());
			String accountName = obj[6].toString();
			//欧元区以外的按当天汇率转换
			fee = fee * getEurRate(country, rate);
			Map<String, AmazonOutboundOrder> accountMap = rs.get(accountName);
			if (accountMap == null) {
				accountMap = Maps.newHashMap();
				rs.put(accountName, accountMap);
			}
			AmazonOutboundOrder outboundOrder = accountMap.get(productName);
			if (outboundOrder == null) {
				outboundOrder = new AmazonOutboundOrder();
				outboundOrder.setAmazonFee(0f);
				outboundOrder.setSupportNum(0);
				outboundOrder.setReviewNum(0);
				outboundOrder.setFbaTransportationFee(0f);
				accountMap.put(productName, outboundOrder);
			}
			if ("Review".equals(orderType) || "Paypal_Refund".equals(orderType)) {
				outboundOrder.setReviewNum(outboundOrder.getReviewNum() + quantity);
				//FbaTransportationFee存储评测单费用
				outboundOrder.setFbaTransportationFee(outboundOrder.getFbaTransportationFee() + fee);
			} else {//ReviewOrSupport状态也算替代
				outboundOrder.setSupportNum(outboundOrder.getSupportNum() + quantity);
				outboundOrder.setAmazonFee(outboundOrder.getAmazonFee() + fee);	//amazonfee存储替代货费用
			}
			
		}
        return rs;
	}

	/**
	 * 评测订单数据统计到利润表(数据不完整可能对应不到当天的销售额，必须单独统计，没有销量就新增)
	 * @param startDay yyyyMMdd
	 */
	@Transactional(readOnly=false)
	public void updateReviewOrder(String startDay, Map<String,Map<String,Float>> allRateRs) {
		String sql = "SELECT t.`country`,SUM(i.`quantity_ordered`),i.`product_name`,i.`color`,t.`order_type`,SUM(IFNULL(t.`amazon_fee`,0)),DATE_FORMAT(t.`create_date`,'%Y%m%d'),t.`account_name`"+
				" FROM `amazoninfo_outbound_order` t,`amazoninfo_outbound_orderitem` i "+
				" WHERE i.`order_id`=t.`id` AND DATE_FORMAT(t.`create_date`,'%Y%m%d')>=:p1 AND t.`order_status`='COMPLETE' AND t.`order_type` IN('Review','Paypal_Refund')"+
				" GROUP BY i.`product_name`,i.`color`,t.`account_name`,DATE_FORMAT(t.`create_date`,'%Y%m%d') ORDER BY t.`country`";
		List<Object[]> list = saleProfitDao.findBySql(sql, new Parameter(startDay));
		//待更新
		List<AmazonOutboundOrder> updateList = Lists.newArrayList();
		for (Object[] obj : list) {
			String country = obj[0].toString();
			Integer quantity = obj[1]==null?0:Integer.parseInt(obj[1].toString());
			String productName = obj[2].toString();
			String color = obj[3]==null?"":obj[3].toString();
			if (StringUtils.isNotEmpty(color)) {
				productName = productName + "_" + color;
			}
			//String orderType = obj[4].toString();
			Float fee = obj[5]==null?0:Float.parseFloat(obj[5].toString());
			String day = obj[6].toString();
			String accountName = obj[7].toString();
			//欧元区以外的按当天汇率转换
			fee = fee * getEurRate(country, allRateRs.get(day));
			AmazonOutboundOrder outboundOrder = new AmazonOutboundOrder();
			outboundOrder.setAmazonFee(fee);
			outboundOrder.setReviewNum(quantity);
			//计算成本
			Float buyCost = findRecentBuyCost(productName, country, allRateRs.get(day));
			outboundOrder.setFbaTransportationFee(quantity*buyCost);
			outboundOrder.setCountry(country);
			outboundOrder.setBuyerName(productName);
			outboundOrder.setAmazonOrderId(day);
			outboundOrder.setAccountName(accountName);
			updateList.add(outboundOrder);
		}
		//更新数据
		String updateSql = "UPDATE `amazoninfo_sale_profit` t SET t.`review_num`=:p1,t.`review_cost`=:p2,t.`review_amazon_fee`=:p6 WHERE t.`product_name`=:p3 AND t.`country`=:p4 AND t.`day`=:p5 AND t.`account_name`=:p7";
		String insertSql = "INSERT INTO `amazoninfo_sale_profit`(DAY,country,product_name,TYPE,line,sales,sales_volume,sales_no_tax,refund,"+
				" amazon_fee,other_fee,transport_fee,buy_cost,profits,return_num,support_num,support_cost,support_amazon_fee,"+
				" review_num,review_cost,review_amazon_fee,ad_in_event_sales,ad_in_event_sales_volume,ad_in_event_fee,"+
				" ad_in_profit_sales,ad_in_profit_sales_volume,ad_in_profit_fee,ad_out_event_sales,ad_out_event_sales_volume,ad_out_event_fee,"+
				" ad_out_profit_sales,ad_out_profit_sales_volume,ad_out_profit_fee,recall_num,recall_cost,recall_fee,account_name)"+
				" VALUES(:p1,:p2,:p3,:p4,:p5,0,0,0,0,0,0,0,0,0,0,0,0,0,:p6,:p7,:p8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,:p9)";
		Map<String, String> allProductTypeMap = psiProductService.findProductTypeMap();
		for (AmazonOutboundOrder outboundOrder : updateList) {
			String country = outboundOrder.getCountry();
			String day = outboundOrder.getAmazonOrderId();
			String productName = outboundOrder.getBuyerName();
			String accountName = outboundOrder.getAccountName();
			if (getByUnique(day, country, productName, accountName) != null) {	//存在则更新
				saleProfitDao.updateBySql(updateSql, new Parameter(outboundOrder.getReviewNum(),outboundOrder.getFbaTransportationFee(),productName,country,day,outboundOrder.getAmazonFee(),accountName));
			} else {
				//产品类型对应的产品线关系
				String month = day.substring(0, 6);
				if (Integer.parseInt(month) < 201601) {
					month = "201601";
				}
				Map<String, String> typeLine = dictService.getTypeLine(month);
				Map<String, String> productTypeMap = psiProductService.findProductTypeMap(month);
				String type = productTypeMap.get(productName);
				if(StringUtils.isEmpty(type)){
					type = allProductTypeMap.get(productName);
				}
				if(StringUtils.isEmpty(type)){
					type = psiProductService.findProductTypeByProductName(productName);
				}
				if (StringUtils.isEmpty(type)) {
					continue;
				}
				String line = typeLine.get(type.toLowerCase());
				if (StringUtils.isEmpty(line)) {
					logger.info("召回产品产品线匹配失败：" +productName);
					continue;
				}
				saleProfitDao.updateBySql(insertSql, new Parameter(day,country,productName,type,line,outboundOrder.getReviewNum(),outboundOrder.getFbaTransportationFee(),outboundOrder.getAmazonFee(),accountName));
			}
		}
	}

	/**
	 * 自发货快递费统计到利润表
	 * @param startDay yyyyMMdd
	 */
	@Transactional(readOnly=false)
	public void updateExpressFee(String startDay, Map<String,Map<String,Float>> allRateRs) {
		String sql = "SELECT t.`account_name`,t.`country`,DATE_FORMAT(t.`buy_time`,'%Y%m%d'),t.`fee`,i.`title` " +
				" FROM `amazoninfo_ebay_order` t, `amazoninfo_ebay_orderitem` i "+
				" WHERE t.`id`=i.`order_id` AND DATE_FORMAT(t.`buy_time`,'%Y%m%d')>:p1 AND t.`fee` IS NOT NULL AND t.`fee`>0 " +
				" AND t.`account_name` IN (SELECT a.`account_name` FROM amazoninfo_account_config a WHERE a.`del_flag`='0') GROUP BY t.`order_id`";
		List<Object[]> list = saleProfitDao.findBySql(sql, new Parameter(startDay));
		//待更新
		Map<String, AmazonOutboundOrder> updateMap = Maps.newHashMap();
		for (Object[] obj : list) {
			String accountName = obj[0].toString();
			String country = obj[1].toString();
			String day = obj[2].toString();
			float expressFee = Float.parseFloat(obj[3].toString());
			String productName = obj[4].toString();
			
			expressFee = expressFee * getEurRate(country, allRateRs.get(day));
			String key = accountName+day+productName;
			AmazonOutboundOrder outboundOrder = updateMap.get(key);
			if (outboundOrder == null) {
				outboundOrder = new AmazonOutboundOrder();
				outboundOrder.setAmazonFee(expressFee);
				outboundOrder.setCountry(country);
				outboundOrder.setBuyerName(productName);
				outboundOrder.setAmazonOrderId(day);
				outboundOrder.setAccountName(accountName);
				updateMap.put(key, outboundOrder);
			} else {
				outboundOrder.setAmazonFee(outboundOrder.getAmazonFee() + expressFee);
			}
		}
		//更新数据
		Map<String, String> allProductTypeMap = psiProductService.findProductTypeMap();
		List<SaleProfit> list2 = Lists.newArrayList();
		for (AmazonOutboundOrder outboundOrder : updateMap.values()) {
			String country = outboundOrder.getCountry();
			String day = outboundOrder.getAmazonOrderId();
			String productName = outboundOrder.getBuyerName();
			String accountName = outboundOrder.getAccountName();
			SaleProfit saleProfit = getByUnique(day, country, productName, accountName);
			if (saleProfit == null) {	//存在则更新
				//产品类型对应的产品线关系
				String month = day.substring(0, 6);
				if (Integer.parseInt(month) < 201601) {
					month = "201601";
				}
				Map<String, String> typeLine = dictService.getTypeLine(month);
				Map<String, String> productTypeMap = psiProductService.findProductTypeMap(month);
				String type = productTypeMap.get(productName);
				if(StringUtils.isEmpty(type)){
					type = allProductTypeMap.get(productName);
				}
				if(StringUtils.isEmpty(type)){
					type = psiProductService.findProductTypeByProductName(productName);
				}
				if (StringUtils.isEmpty(type)) {
					continue;
				}
				String line = typeLine.get(type.toLowerCase());
				if (StringUtils.isEmpty(line)) {
					logger.info("产品线匹配失败：" +productName);
					continue;
				}
				saleProfit = new SaleProfit();
				saleProfit.setDay(day);
				saleProfit.setProductName(productName);
				saleProfit.setCountry(country);
				saleProfit.setAccountName(accountName);
				saleProfit.setType(type);
				saleProfit.setLine(line);
			}
			saleProfit.setExpressFee(outboundOrder.getAmazonFee());
			list2.add(saleProfit);
		}
		if (list2.size()>0) {
			saveList(list2);
		}
	}

	/**
	 * Vine数据统计到利润表
	 * @param startDay yyyyMMdd
	 */
	@Transactional(readOnly=false)
	public void updateVineFee(String startDay, Map<String,Map<String,Float>> allRateRs) {
		String sql = "SELECT t.`country`,t.`asin`,t.`account_name`,t.`fee`,t.`quantity`,DATE_FORMAT(t.`date_date`,'%Y%m%d') "+
				" FROM `amazoninfo_vine_fee` t WHERE DATE_FORMAT(t.`date_date`,'%Y%m%d')>:p1";
		List<Object[]> list = saleProfitDao.findBySql(sql, new Parameter(startDay));

		Map<String, String> asinNameMap = getProductNameByAsin();
		//待更新
		Map<String, AmazonOutboundOrder> updateMap = Maps.newHashMap();
		for (Object[] obj : list) {
			String country = obj[0].toString();
			String asin = obj[1].toString();
			String accountName = obj[2].toString();
			Float fee = Float.parseFloat(obj[3].toString());
			Integer quantity = Integer.parseInt(obj[4].toString());
			String day = obj[5].toString();
			
			String productName = asinNameMap.get(asin);
			if (StringUtils.isEmpty(productName)) {
				continue;
			}
			//欧元区以外的按当天汇率转换
			fee = fee * getEurRate(country, allRateRs.get(day));
			Float buyCost = findRecentBuyCost(productName, country, allRateRs.get(day));
			String key = accountName+day+productName;
			AmazonOutboundOrder outboundOrder = updateMap.get(key);
			if (outboundOrder == null) {
				outboundOrder = new AmazonOutboundOrder();
				outboundOrder.setAmazonFee(fee);
				outboundOrder.setReviewNum(quantity);
				//计算成本
				outboundOrder.setFbaTransportationFee(quantity*buyCost);
				outboundOrder.setCountry(country);
				outboundOrder.setBuyerName(productName);
				outboundOrder.setAmazonOrderId(day);
				outboundOrder.setAccountName(accountName);
				updateMap.put(key, outboundOrder);
			} else {
				outboundOrder.setAmazonFee(outboundOrder.getAmazonFee() + fee);
				outboundOrder.setReviewNum(outboundOrder.getReviewNum() + quantity);
				//计算成本
				outboundOrder.setFbaTransportationFee(outboundOrder.getFbaTransportationFee() + quantity*buyCost);
			}
		}
		//更新数据
		Map<String, String> allProductTypeMap = psiProductService.findProductTypeMap();
		List<SaleProfit> list2 = Lists.newArrayList();
		for (AmazonOutboundOrder outboundOrder : updateMap.values()) {
			String country = outboundOrder.getCountry();
			String day = outboundOrder.getAmazonOrderId();
			String productName = outboundOrder.getBuyerName();
			String accountName = outboundOrder.getAccountName();
			SaleProfit saleProfit = getByUnique(day, country, productName, accountName);
			if (saleProfit == null) {	//存在则更新
				//产品类型对应的产品线关系
				String month = day.substring(0, 6);
				if (Integer.parseInt(month) < 201601) {
					month = "201601";
				}
				Map<String, String> typeLine = dictService.getTypeLine(month);
				Map<String, String> productTypeMap = psiProductService.findProductTypeMap(month);
				String type = productTypeMap.get(productName);
				if(StringUtils.isEmpty(type)){
					type = allProductTypeMap.get(productName);
				}
				if(StringUtils.isEmpty(type)){
					type = psiProductService.findProductTypeByProductName(productName);
				}
				if (StringUtils.isEmpty(type)) {
					continue;
				}
				String line = typeLine.get(type.toLowerCase());
				if (StringUtils.isEmpty(line)) {
					logger.info("产品线匹配失败：" +productName);
					continue;
				}
				saleProfit = new SaleProfit();
				saleProfit.setDay(day);
				saleProfit.setProductName(productName);
				saleProfit.setCountry(country);
				saleProfit.setAccountName(accountName);
				saleProfit.setType(type);
				saleProfit.setLine(line);
			}
			saleProfit.setVineFee(outboundOrder.getAmazonFee());
			saleProfit.setVineNum(outboundOrder.getReviewNum());
			saleProfit.setVineCost(outboundOrder.getFbaTransportationFee());
			list2.add(saleProfit);
		}
		if (list2.size()>0) {
			saveList(list2);
		}
		
	}

	
	/**
	 * 站外广告销售额、销量、成本(欧元)
	 * @param date yyyyMMdd
	 * @param rate 汇率
	 * @return[国家[产品[类型  广告费用对象]]]类型为：event(销售投放) or profit(广告策划)
	 */
	public Map<String, Map<String, Map<String, Advertising>>> findFacebookAdByDay(String date, Map<String, Float> rate) {
		Map<String, Map<String, Map<String, Advertising>>> rs = Maps.newHashMap();
		String target = "EUR";
		Map<String, String> asinNameMap = getProductNameByAsin();
		String sql="SELECT f.`tracking_id`,  "+
				" (CASE WHEN f.`market`='com' THEN t.`amount_spent`*:p1 "+
				" WHEN f.`market`='ca' THEN t.`amount_spent`*:p2  "+
				" WHEN f.`market`='jp' THEN t.`amount_spent`*:p3 "+
				" WHEN f.`market`='uk' THEN t.`amount_spent`*:p4 "+
				" ELSE t.`amount_spent` END) AS spend "+
				" FROM facebook_report t JOIN amazoninfo_facebook_relationship f ON t.`ad_id`=f.`ad_id`  "+
				" WHERE t.`del_flag`='0' AND f.`del_flag`='0' AND DATE_FORMAT(t.`start`,'%Y%m%d')=:p5 ";
		List<Object[]> spendList = saleProfitDao.findBySql(sql, new Parameter(
        		MathUtils.getRate("USD", target, rate),
        		MathUtils.getRate("CAD", target, rate),
        		MathUtils.getRate("JPY", target, rate),
        		MathUtils.getRate("GBP", target, rate), date));
		//trackId对应的总花费
		Map<String, Float> trackSpend = Maps.newHashMap();
		for (Object[] obj : spendList) {
			trackSpend.put(obj[0].toString(), Float.parseFloat(obj[1].toString()));
		}
		//所有trackid对应的投放广告asin，trackid对应的广告类型，event or profit
		Map<String, Map<String, String>> relationMap = Maps.newHashMap();
		sql = "SELECT f.`tracking_id`,t.`campaign_name`, f.`asin_on_ad`,f.`market`"+
			" FROM facebook_report t JOIN amazoninfo_facebook_relationship f ON t.`ad_id`=f.`ad_id` "+
			" WHERE t.`del_flag`='0' AND f.`del_flag`='0' GROUP BY f.`tracking_id`";
		List<Object[]> relation = saleProfitDao.findBySql(sql);
		for (Object[] obj : relation) {
			String trackId = obj[0].toString();
			String name = obj[1].toString();
			String asin = obj[2].toString();
			String country = obj[3].toString();
			String type = "profit";
			if (name.contains("sales request")) {
				type = "event";
			}
			Map<String, String> map = relationMap.get(trackId);
			if (map == null) {
				map = Maps.newHashMap();
				relationMap.put(trackId, map);
			}
			map.put("type", type);
			map.put("asin", asin);
			map.put("country", country);
		}
		
		//当天的trackId对应的总花费
		sql = "SELECT t.`market`,t.`asin`,t.`tracking_id`,t.`items_shipped`,t.`revenue`,t.`advertising_fees` " +
				" FROM `amazoninfo_facebook_report` t WHERE DATE_FORMAT(t.`date_shipped`,'%Y%m%d')=:p1 AND t.`del_flag`='0'";
		List<Object[]> salesList = saleProfitDao.findBySql(sql, new Parameter(date));
		//计算每个trackid的inateck产品销量和非inateck产品的返点总额(用于均摊及补贴广告花费)
		Map<String, Float> returnMap = Maps.newHashMap();	//非inateck产品返款
		Map<String, Integer> trackIdSalesVolume = Maps.newHashMap();//inateck产品销量
		for (Object[] obj : salesList) {
			String country = obj[0].toString();
			float eurRate = getEurRate(country, rate);
			String asin = obj[1].toString();
			String trackId = obj[2].toString();
			Integer salesVolume = obj[3]==null?0:Integer.parseInt(obj[3].toString());
			Float fee = obj[5]==null?0:Float.parseFloat(obj[5].toString());
			fee = fee * eurRate;
			String productName = asinNameMap.get(asin);
			if (StringUtils.isEmpty(productName)) {
				//不是本公司产品,累加返点金额
				Float returnFee = returnMap.get(trackId);
				if (returnFee == null) {
					returnMap.put(trackId, fee);
				} else {
					returnMap.put(trackId, returnFee + fee);
				}
			} else {
				Integer totalSalesVolume = trackIdSalesVolume.get(trackId);
				if (totalSalesVolume == null) {
					trackIdSalesVolume.put(trackId, salesVolume);
				} else {
					trackIdSalesVolume.put(trackId, totalSalesVolume + salesVolume);
				}
			}
		}
		//分国家、产品统计当天的广告数据
		Set<String> trackIdSet = Sets.newHashSet();
		for (Object[] obj : salesList) {
			String country = obj[0].toString();
			float eurRate = getEurRate(country, rate);
			String asin = obj[1].toString();
			String trackId = obj[2].toString();
			Integer salesVolume = obj[3]==null?0:Integer.parseInt(obj[3].toString());
			Float sales = obj[4]==null?0:Float.parseFloat(obj[4].toString());
			sales = sales * eurRate;
			//返款费用
			Float fee = obj[5]==null?0:Float.parseFloat(obj[5].toString());
			fee = fee * eurRate;
			String productName = asinNameMap.get(asin);
			if (StringUtils.isNotEmpty(productName)) {//本公司产品,计算广告数据
				trackIdSet.add(trackId);
				//返款费用加上分摊的成本和非inateck产品的返款费用
				Integer totalVolume = trackIdSalesVolume.get(trackId);
				Float totalReturn = returnMap.get(trackId);
				Float totalSpend = trackSpend.get(trackId);
				if (totalVolume != null && totalVolume > 0 && totalSpend != null) {
					fee = fee - totalSpend/totalVolume*salesVolume;	//单个产品花费乘以销量
					if (totalReturn != null && totalReturn > 0) {
						fee = fee + totalReturn/totalVolume*salesVolume;
					}
				}
				String key = "profit";
				try {
					key = relationMap.get(trackId).get("type");
				} catch (Exception e) {}
				
				Map<String, Map<String, Advertising>> countryMap = rs.get(country);
				if (countryMap == null) {
					countryMap = Maps.newHashMap();
					rs.put(country, countryMap);
				}
				Map<String, Advertising> productMap = countryMap.get(productName);
				if (productMap == null) {
					productMap = Maps.newHashMap();
					countryMap.put(productName, productMap);
				}
				Advertising advertising = productMap.get(key);
				if (advertising == null) {
					advertising = new Advertising();
					advertising.setTotalSpend(0f);	//广告花费
					advertising.setWeekSameSkuUnitsOrdered(0);	//广告销量
					advertising.setWeekSameSkuUnitsSales(0f);	//广告销售额
					productMap.put(key, advertising);
				}
				advertising.setTotalSpend(advertising.getTotalSpend() + fee);
				advertising.setWeekSameSkuUnitsOrdered(advertising.getWeekSameSkuUnitsOrdered() + salesVolume);
				advertising.setWeekSameSkuUnitsSales(advertising.getWeekSameSkuUnitsSales() + sales);
			}
		}
		//如果当天花费了广告费用但是对应的trackId没有产生订单(inateck产品订单),即没有分摊,则全部算在投放的指定asin对应的产品上
		 for (Map.Entry<String,Float> entry : trackSpend.entrySet()) { 
		   String spendId=entry.getKey();
			if (!trackIdSet.contains(spendId)) {
				try {
					String trackIdCountry = relationMap.get(spendId).get("country");
					String asinName = relationMap.get(spendId).get("asin");
					String key = relationMap.get(spendId).get("type");
					Map<String, Map<String, Advertising>> countryMap = rs.get(trackIdCountry);
					if (countryMap == null) {
						countryMap = Maps.newHashMap();
						rs.put(trackIdCountry, countryMap);
					}
					Map<String, Advertising> productMap = countryMap.get(asinName);
					if (productMap == null) {
						productMap = Maps.newHashMap();
						countryMap.put(asinName, productMap);
					}
					Advertising advertising = productMap.get(key);
					if (advertising == null) {
						advertising = new Advertising();
						advertising.setTotalSpend(0f);	//广告花费
						advertising.setWeekSameSkuUnitsOrdered(0);	//广告销量
						advertising.setWeekSameSkuUnitsSales(0f);	//广告销售额
						productMap.put(key, advertising);
					}
					//所有的花费都算在投放ASIN对应的产品上
					Float totalSpend = entry.getValue();
					Float totalReturn = returnMap.get(spendId);
					//其他品牌产品有返点也算在投放ASIN对应的产品上
					if (totalReturn != null) {
						totalSpend = totalSpend + totalReturn;
					}
					advertising.setTotalSpend(advertising.getTotalSpend() + totalSpend);
				} catch (Exception e) {
					logger.error("获取trackId对应的产品异常", e);
				}
			}
		}
		return rs;
	}	
	
	
	/**
	 * 站内广告销售额、销量、成本(欧元)
	 * @param date yyyyMMdd
	 * @param rate 汇率
	 * @return[国家[产品[类型  广告费用对象]]]类型为：event(销售投放) or profit(广告策划)
	 */
	public Map<String, Map<String, Map<String, Advertising>>> findInsideAdFeeByDay(String date, Map<String, Float> rate) {
		Map<String, Map<String, Map<String, Advertising>>> rs = Maps.newHashMap();
		String target = "EUR";
		//sku对应的产品名称[sku productname]]]
		Map<String, String> skuMap = findSkuNames();
		String sql = "";
		if(date.compareTo("20180506")>=0){
			sql="SELECT t.`country`,t.`advertised_sku`,SUM(CASE WHEN t.`country`='com' THEN t.`spend`*:p1 "+
				"	WHEN t.`country`='ca' THEN t.`spend`*:p2  "+
				"	WHEN t.`country`='jp' THEN t.`spend`*:p3 "+
				"	WHEN t.`country`='uk' THEN t.`spend`*:p4 "+
				"	ELSE t.`spend` END) AS spend,t.`campaign_name`,SUM(IFNULL(t.`day_advertised_sku_units`,0)),SUM(IFNULL(t.`day_advertised_sku_sales`,0)),t.`account_name` "+
				"	FROM `amazoninfo_advertising_report` t WHERE DATE_FORMAT(t.`data_date`,'%Y%m%d')=:p5 AND t.`advertised_sku` IS NOT NULL AND t.`advertised_sku`!='' "+
				"	GROUP BY t.`advertised_sku`,t.`country`,t.`account_name`,t.`campaign_name` ";
		}else{
			sql = "SELECT t.`country`,t.`sku`,SUM(CASE WHEN t.`country`='com' THEN t.`total_spend`*:p1 "+
					" WHEN t.`country`='ca' THEN t.`total_spend`*:p2  "+
					" WHEN t.`country`='jp' THEN t.`total_spend`*:p3 "+
					" WHEN t.`country`='uk' THEN t.`total_spend`*:p4 "+
					" ELSE t.`total_spend` END) AS spend,t.`name`,SUM(IFNULL(t.`week_same_sku_units_ordered`,0)),SUM(IFNULL(t.`week_same_sku_units_sales`,0)),t.`account_name` "+
					" FROM `amazoninfo_advertising` t WHERE DATE_FORMAT(t.`data_date`,'%Y%m%d')=:p5 "+
					" GROUP BY t.`sku`,t.`country`,t.`account_name`,t.`name`";
		}
        List<Object[]>  list = saleProfitDao.findBySql(sql, new Parameter(
        		MathUtils.getRate("USD", target, rate),
        		MathUtils.getRate("CAD", target, rate),
        		MathUtils.getRate("JPY", target, rate),
        		MathUtils.getRate("GBP", target, rate), date));
        for (Object[] obj : list) {
			String country = obj[0].toString();
			float eurRate = getEurRate(country, rate);
			String sku = obj[1] == null ? "" : obj[1].toString();
			Float spend = obj[2] == null ? 0f : Float.parseFloat(obj[2].toString());
			String name = obj[3]==null?"":obj[3].toString();
			Integer salesVolume = obj[4] == null ? 0 : Integer.parseInt(obj[4].toString());
			Float sales = obj[5] == null ? 0f : Float.parseFloat(obj[5].toString());
			String accountName = obj[6]==null?accountMap.get(country):obj[6].toString();
			sales = sales * eurRate;
			//name不包含profit的统一算作event
			String key = "event";
			if (name.toLowerCase().contains("profit")) {
				key = "profit";
			}
			String productName = skuMap.get(sku);
			if (StringUtils.isEmpty(productName)) {
				if (StringUtils.isNotEmpty(sku)) {
					logger.warn("广告sku未匹配到产品:" + sku);
				}
				continue;
			}
			Map<String, Map<String, Advertising>> accountMap = rs.get(accountName);
			if (accountMap == null) {
				accountMap = Maps.newHashMap();
				rs.put(accountName, accountMap);
			}
			Map<String, Advertising> productMap = accountMap.get(productName);
			if (productMap == null) {
				productMap = Maps.newHashMap();
				accountMap.put(productName, productMap);
			}
			Advertising advertising = productMap.get(key);
			if (advertising == null) {
				advertising = new Advertising();
				advertising.setTotalSpend(0f);	//广告花费
				advertising.setWeekSameSkuUnitsOrdered(0);	//广告销量
				advertising.setWeekSameSkuUnitsSales(0f);	//广告销售额
				productMap.put(key, advertising);
			}
			advertising.setTotalSpend(advertising.getTotalSpend() + spend);
			advertising.setWeekSameSkuUnitsOrdered(advertising.getWeekSameSkuUnitsOrdered() + salesVolume);
			advertising.setWeekSameSkuUnitsSales(advertising.getWeekSameSkuUnitsSales() + sales);
		}
        return rs;
	}	
	
	
	/**
	 * AMS广告销售额、销量、成本(欧元)
	 * @param date yyyyMMdd
	 * @param rate 汇率
	 * @return[国家[产品  广告费用对象]]
	 */
	public Map<String, Map<String, Advertising>> findAmsAdFeeByDay(String date, Map<String, Float> rate) {
		Map<String, Map<String, Advertising>> rs = Maps.newHashMap();
		String sql = "SELECT t.`country`,t.`product_name`,SUM(t.`spend`),SUM(t.`units_sold`),SUM(t.`total_sales`),t.`account_name` "+
				" FROM `amazoninfo_aws_adversting` t WHERE t.`campaign_type`='DisplayAds' AND DATE_FORMAT(t.`data_date`,'%Y%m%d')=:p1 "+
				" AND t.`product_name` IS NOT NULL GROUP BY t.`country`,t.`account_name`,t.`product_name`";
        List<Object[]>  list = saleProfitDao.findBySql(sql, new Parameter(date));
        for (Object[] obj : list) {
			String country = obj[0].toString();
			float eurRate = getEurRate(country, rate);
			String productName = obj[1].toString();
			Float spend = obj[2] == null ? 0f : Float.parseFloat(obj[2].toString());
			spend = spend * eurRate;
			Integer salesVolume = obj[3] == null ? 0 : Integer.parseInt(obj[3].toString());
			Float sales = obj[4] == null ? 0f : Float.parseFloat(obj[4].toString());
			sales = sales * eurRate;
			String accountName = obj[5]==null?accountMap.get(country):obj[5].toString();
		
			Map<String, Advertising> accountMap = rs.get(accountName);
			if (accountMap == null) {
				accountMap = Maps.newHashMap();
				rs.put(accountName, accountMap);
			}
			Advertising advertising = accountMap.get(productName);
			if (advertising == null) {
				advertising = new Advertising();
				advertising.setTotalSpend(0f);	//广告花费
				advertising.setWeekSameSkuUnitsOrdered(0);	//广告销量
				advertising.setWeekSameSkuUnitsSales(0f);	//广告销售额
				accountMap.put(productName, advertising);
			}
			advertising.setTotalSpend(advertising.getTotalSpend() + spend);
			advertising.setWeekSameSkuUnitsOrdered(advertising.getWeekSameSkuUnitsOrdered() + salesVolume);
			advertising.setWeekSameSkuUnitsSales(advertising.getWeekSameSkuUnitsSales() + sales);
		}
        return rs;
	}
	
	@Transactional(readOnly = false)
	public void updateOutboundFee(){
		String sql = "SELECT DISTINCT t.`amazon_order_id` FROM `amazoninfo_outbound_order` t WHERE t.`order_status`='COMPLETE' " +
				" AND t.`amazon_order_id` IS NOT NULL AND t.`amazon_fee` IS NULL";
		List<Object> list = saleProfitDao.findBySql(sql);
		if (list != null && list.size() > 0) {
			sql = "SELECT o.`amazon_order_id`," +
					" SUM(IFNULL(t.`principal`,0) + IFNULL(t.`shipping`,0) + IFNULL(t.`cod`,0) + IFNULL(t.`gift_wrap`,0) + IFNULL(t.`goodwill`,0) + "+
					" IFNULL(t.`cross_border_fulfillment_fee`,0) + IFNULL(t.`fba_per_unit_fulfillment_fee`,0) + IFNULL(t.`fba_weight_based_fee`,0) + "+
					" IFNULL(t.`commission`,0) + IFNULL(t.`shipping_chargeback`,0) + IFNULL(t.`giftwrap_chargeback`,0) + IFNULL(t.`refund_commission`,0) + "+
					" IFNULL(t.`restocking_fee`,0) + IFNULL(t.`promotion`,0) + IFNULL(t.`cod_fee`,0) + IFNULL(t.`other_fee`,0) + IFNULL(t.`shipping_hb`,0) + "+
					" IFNULL(t.`shipment_fee`,0) + IFNULL(t.`fba_per_order_fulfillment_fee`,0)) AS fee "+
					" FROM amazoninfo_financial o,amazoninfo_financial_item t WHERE o.id = t.`order_id` AND o.`amazon_order_id` IN (:p1) GROUP BY o.`amazon_order_id`";
			List<Object[]> objects = saleProfitDao.findBySql(sql, new Parameter(list));
			String updateSql = "UPDATE `amazoninfo_outbound_order` t SET t.`amazon_fee`=:p1 WHERE t.`amazon_order_id`=:p2";
			for (Object[] obj : objects) {
				String amazonOrderId = obj[0].toString();
				Float fee = obj[1]==null?0:Float.parseFloat(obj[1].toString());
				saleProfitDao.updateBySql(updateSql, new Parameter(fee, amazonOrderId));
			}
		}
	}
	
	private float getEurRate(String country, Map<String, Float> rate){
		String target = "EUR";
		if ("com".equals(country) || "us".equals(country)) {
			return MathUtils.getRate("USD", target, rate);
		} else if ("uk".equals(country)) {
			return MathUtils.getRate("GBP", target, rate);
		} else if ("ca".equals(country)) {
			return MathUtils.getRate("CAD", target, rate);
		} else if ("jp".equals(country)) {
			return MathUtils.getRate("JPY", target, rate);
		} else if ("mx".equals(country)) {
			return MathUtils.getRate("MXN", target, rate);
		} else {
			return 1f; //其他四国为欧元
		}
	}
	
	//asin对应的产品名
	public Map<String,String>  getProductNameByAsin(){
		Map<String, String> rs = Maps.newHashMap();
		String sql = "SELECT a.`asin`,a.`product_name`,a.color FROM psi_sku AS a  WHERE a.`del_flag`='0'  AND a.`asin` is not null AND a.`product_name` not in('Inateck other','Inateck Old') GROUP BY a.`asin`,a.`product_name`,a.`color`";
		List<Object[]> list = saleProfitDao.findBySql(sql);
		for (Object[] obj : list) {
			if (obj[1] != null) {
				String productName = obj[1].toString();
				String color = obj[2]==null?"":obj[2].toString();
				if (StringUtils.isNotEmpty(color)) {
					productName = productName + "_" + color;
				}
				rs.put(obj[0].toString(), productName);
			}
		}
	    return rs;
	}

	/**
	 * 召回订单数据统计到利润表
	 * @param startDay yyyyMMdd
	 */
	@Transactional(readOnly=false)
	public void updateRecallOrder(String startDay, Map<String,Map<String,Float>> allRateRs) {
		String sql = "SELECT o.`country`,t.`sellersku`,DATE_FORMAT(o.`purchase_date`,'%Y%m%d') AS dates,t.`product_name`, "+
				" SUM(CASE WHEN o.`order_type`='Liquidate' THEN t.`completed_qty` ELSE t.`requested_qty` END) AS qty, "+
				" SUM((CASE WHEN o.`order_type`='Liquidate' THEN t.`completed_qty` ELSE t.`requested_qty` END)*(IFNULL(t.`buy_cost`,0))) AS cost, "+
				" SUM(IFNULL(t.`removal_fee`,0)) AS fee,t.`currency`,t.`disposition`,o.`order_type`,o.`account_name` "+
				" FROM `amazoninfo_removal_orderitem` t,`amazoninfo_removal_order` o "+
				" WHERE t.`order_id`=o.`id` AND o.`order_status`!='Cancelled' AND DATE_FORMAT(o.`purchase_date`,'%Y%m%d') "+
				" IN(SELECT DISTINCT DATE_FORMAT(a.`purchase_date`,'%Y%m%d') FROM `amazoninfo_removal_order` a WHERE   DATE_FORMAT(a.`last_update_date`,'%Y%m%d')>=:p1) "+
				" GROUP BY t.`sellersku`,dates,o.`country`,t.`disposition`,o.`order_type` HAVING qty>0 OR fee>0 OR cost>0";
		List<Object[]> list = saleProfitDao.findBySql(sql, new Parameter(startDay));
		//key:country_productName_date
		Map<String, AmazonRemovalOrderItem> updateMap = Maps.newHashMap();
		for (Object[] obj : list) {
			String country = obj[0].toString();
			String sku = obj[1].toString();
			String dates = obj[2].toString();
			if (Integer.parseInt(dates) < 20150101) {
				continue; //利润表最早数据为20150101,之前的数据跳过
			}
			String productName = obj[3]==null?"Inateck other":obj[3].toString();
			Integer qty = obj[4]==null?0:Integer.parseInt(obj[4].toString());
			Float cost = obj[5]==null?0f:Float.parseFloat(obj[5].toString());
			Float fee = obj[6]==null?0f:Float.parseFloat(obj[6].toString());
			String currencyType = obj[7]==null?"":obj[7].toString();
			String disposition = obj[8]==null?"":obj[8].toString();
			String orderType = obj[9]==null?"":obj[9].toString();
			String accountName = obj[10].toString();
			//退回且可售的不计入亏损成本(亏损成本从利润中扣除)
			if ("Sellable".equals(disposition) && "Return".equals(orderType)) {
				cost = 0f;
			}
			
			String temp = country;
			if ("de".equals(country)) {
				if (sku.toLowerCase().contains("uk")) {
					temp = "uk";
				} else if (sku.toLowerCase().contains("fr")) {
					temp = "fr";
				} else if (sku.toLowerCase().contains("it")) {
					temp = "it";
				} else if (sku.toLowerCase().contains("es")) {
					temp = "es";
				}
				accountName = accountName.split("_")[0] + "_" + temp.toUpperCase();
			}
			if (StringUtils.isEmpty(currencyType)) {
				currencyType = "EUR";
				if ("com".equals(country)) {
					currencyType = "USD";
				} else if ("jp".equals(country)) {
					currencyType = "JPY";
				} else if ("ca".equals(country)) {
					currencyType = "CAD";
				} else if ("de".equals(country)) {
					if (sku.toLowerCase().contains("uk")) {
						currencyType = "GBP";
					}
				}
			}

			Map<String,Float> rate = allRateRs.get(dates);
			fee = fee * MathUtils.getRate(currencyType, "EUR", rate);
			String key = temp + "_" + productName + "_" + dates;
			AmazonRemovalOrderItem item = updateMap.get(key);
			if (item == null) {
				item = new AmazonRemovalOrderItem();
				item.setCountryCode(temp);
				item.setAccountName(accountName);
				item.setColorCode(dates);	//color属性临时存储日期
				item.setProductName(productName);
				item.setCompletedQty(0);
				item.setBuyCost(0f);	//采购成本存储总成本
				item.setRemovalFee(new BigDecimal(0));	//召回费用
				updateMap.put(key, item);
			}
			item.setCompletedQty(item.getCompletedQty() + qty);
			item.setBuyCost(item.getBuyCost() + cost);
			item.setRemovalFee(item.getRemovalFee().add(new BigDecimal(fee)));
		}
		//更新数据
		String updateSql = "UPDATE `amazoninfo_sale_profit` t SET t.`recall_num`=:p1,t.`recall_cost`=:p2,t.`recall_fee`=:p6 WHERE t.`product_name`=:p3 AND t.`country`=:p4 AND t.`day`=:p5 AND t.`account_name`=:p7";
		String insertSql = "INSERT INTO `amazoninfo_sale_profit`(DAY,country,product_name,TYPE,line,sales,sales_volume,sales_no_tax,refund,"+
				" amazon_fee,other_fee,transport_fee,buy_cost,profits,return_num,support_num,support_cost,support_amazon_fee,"+
				" review_num,review_cost,review_amazon_fee,ad_in_event_sales,ad_in_event_sales_volume,ad_in_event_fee,"+
				" ad_in_profit_sales,ad_in_profit_sales_volume,ad_in_profit_fee,ad_out_event_sales,ad_out_event_sales_volume,ad_out_event_fee,"+
				" ad_out_profit_sales,ad_out_profit_sales_volume,ad_out_profit_fee,recall_num,recall_cost,recall_fee,account_name)"+
				" VALUES(:p1,:p2,:p3,:p4,:p5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,:p6,:p7,:p8,:p9)";
		Map<String, String> allProductTypeMap = psiProductService.findProductTypeMap();
		for (Map.Entry<String,AmazonRemovalOrderItem> entry : updateMap.entrySet()) { 
			AmazonRemovalOrderItem item = entry.getValue();
			String country = item.getCountryCode();
			String day = item.getColorCode();
			String productName = item.getProductName();
			String accountName = item.getAccountName();
			if (getByUnique(day, country, productName, accountName) != null) {	//存在则更新
				saleProfitDao.updateBySql(updateSql, new Parameter(item.getCompletedQty(),item.getBuyCost(),productName,country,day,item.getRemovalFee(),accountName));
			} else {
				//产品类型对应的产品线关系
				String month = day.substring(0, 6);
				if (Integer.parseInt(month) < 201601) {
					month = "201601";
				}
				Map<String, String> typeLine = dictService.getTypeLine(month);
				Map<String, String> productTypeMap = psiProductService.findProductTypeMap(month);
				String type = productTypeMap.get(productName);
				if(StringUtils.isEmpty(type)){
					type = allProductTypeMap.get(productName);
				}
				if(StringUtils.isEmpty(type)){
					type = psiProductService.findProductTypeByProductName(productName);
				}
				if (StringUtils.isEmpty(type)) {
					continue;
				}
				String line = typeLine.get(type.toLowerCase());
				if (StringUtils.isEmpty(line)) {
					logger.info("召回产品产品线匹配失败：" +productName);
					continue;
				}
				saleProfitDao.updateBySql(insertSql, new Parameter(day,country,productName,type,line,item.getCompletedQty(),item.getBuyCost(),item.getRemovalFee(),accountName));
			}
		}
	}
	
	/**
	 * 计算采购价格（减去退税后的价格 单位：欧元）
	 * @param rateRs 汇率
	 * @return 【产品名   价格】
	 */
	public Map<String, Float> getBuyCost(Map<String, Float> rateRs){
		Map<String, Float> rs = Maps.newHashMap();
		String sql = "SELECT t.`product_name`,t.`color`,t.`moq_price`,t.`currency_type`,p.`tax_refund` FROM `psi_product_attribute` t ,psi_product p "+
				" WHERE t.`product_id`=p.`id` AND t.`del_flag`='0' AND p.`del_flag`='0' AND t.`moq_price` IS NOT NULL";
		List<Object[]> list = saleProfitDao.findBySql(sql);
		for (Object[] obj : list) {
			String productName = obj[0].toString();
			String color = obj[1]==null?"":obj[1].toString();
			if (StringUtils.isNotEmpty(color)) {
				productName = productName + "_" + color;
			}
			Float moqPrice = obj[2]==null?0f:Float.parseFloat(obj[2].toString());
			String currencyType = obj[3]==null?"":obj[3].toString();
			Integer taxRefund = obj[4]==null?17:Integer.parseInt(obj[4].toString());	//退税率为空时默认为17个点
			moqPrice = moqPrice * 100/(100 + taxRefund) * MathUtils.getRate(currencyType, "EUR", rateRs);	//算出退税后的价格再转换成欧元
			rs.put(productName, moqPrice);
		}
		return rs;
	}
	
	/**
	 * 计算产品关税（不含税价*关税税率,其中不含税价由含税价和税率推算）
	 * @param rateRs 汇率
	 * @return [产品名   [国家 关税价格]]
	 */
	public Map<String, Map<String, Float>> getTariffs(Map<String, Float> rateRs){
		Map<String, Map<String, Float>> rs = Maps.newHashMap();
		//查询出产品的税率（供应商表）
		String rateSql = "SELECT CONCAT(p.`brand`,' ',p.`model`),MAX(s.`tax_rate`) FROM psi_product_supplier t ,psi_product p, psi_supplier s"+
				" WHERE t.`product_id`=p.`id` AND t.`supplier_id`=s.`id` AND p.`del_flag`='0' AND s.`del_flag`='0' GROUP BY p.`id`";
		List<Object[]> ratelist = saleProfitDao.findBySql(rateSql);
		Map<String, Float> rateMap = Maps.newHashMap();
		for (Object[] obj : ratelist) {
			String productName = obj[0].toString();
			Float rate= obj[1]==null?0f:Float.parseFloat(obj[1].toString());
			rateMap.put(productName, rate);
		}
		//查询产品含税价和关税税率
		String sql = "SELECT a.`product_name`,a.`color`,a.`moq_price`,a.`currency_type`,p.`eu_custom_duty`,p.`ca_custom_duty`,p.`us_custom_duty`,p.`jp_custom_duty` "+
				" FROM psi_product_attribute a, psi_product p WHERE a.`product_id`=p.`id` AND a.`del_flag`='0' AND p.`del_flag`='0'";
		List<Object[]> list = saleProfitDao.findBySql(sql);
		for (Object[] obj : list) {
			String productName = obj[0].toString();
			String color = obj[1]==null?"":obj[1].toString();
			String productColorName = productName;
			if (StringUtils.isNotEmpty(color)) {
				productColorName = productName + "_" + color;
			}
			Float price = obj[2]==null?0f:Float.parseFloat(obj[2].toString());
			String currencyType = obj[3]==null?"":obj[3].toString();
			Float euRate= obj[4]==null?0f:Float.parseFloat(obj[4].toString());
			Float caRate= obj[5]==null?0f:Float.parseFloat(obj[5].toString());
			Float usRate= obj[6]==null?0f:Float.parseFloat(obj[6].toString());
			Float jpRate= obj[7]==null?0f:Float.parseFloat(obj[7].toString());
			//欧洲关税为统一价
			Map<String, Float> productMap = Maps.newHashMap();
			rs.put(productColorName, productMap);
			Float rate = rateMap.get(productName)==null?0f:rateMap.get(productName);	//供应商税率
			//含税价推送不含税价并换算汇率
			if (price==0 || StringUtils.isEmpty(currencyType)) {
				productMap.put("eu", 0f);
				productMap.put("ca", 0f);
				productMap.put("com", 0f);
				productMap.put("jp", 0f);
			} else {
				Float exchangeRate = MathUtils.getRate(currencyType, "EUR", rateRs);	//当天汇率
				Float preTaxPrice = price*100/(100+rate) * exchangeRate;
				//计算实际关税
				Float euTariffs = preTaxPrice * euRate / 100;
				Float caTariffs = preTaxPrice * caRate / 100;
				Float usTariffs = preTaxPrice * usRate / 100;
				Float jpTariffs = preTaxPrice * jpRate / 100;
				productMap.put("eu", euTariffs);
				productMap.put("ca", caTariffs);
				productMap.put("com", usTariffs);
				productMap.put("jp", jpTariffs);
			}
		}
		return rs;
	}

	/**
	 * 更新关税
	 * @param rateRs 汇率
	 */
	@Transactional(readOnly=false)
	public void updateTariffs(Map<String, Float> rateRs, String dateStr) {
		DetachedCriteria dc = saleProfitDao.createDetachedCriteria();
		dc.add(Restrictions.eq("day", dateStr));
		dc.add(Restrictions.isNull("tariff"));
		List<SaleProfit> list = saleProfitDao.find(dc);
		Map<String, Map<String, Float>> tariffsMap = getTariffs(rateRs);
		for (SaleProfit saleProfit : list) {
			String country = saleProfit.getCountry();
			String productName = saleProfit.getProductName();
			if ("de,uk,fr,it,es".contains(country)) {
				country = "eu";
			}
			Float tariff = 0f;
			try {
				tariff = tariffsMap.get(productName).get(country);
				if (tariff == null) {
					tariff = 0f;
				}
			} catch (NullPointerException e) {}
			saleProfit.setTariff(tariff);
		}
		saveList(list);
	}
	
	/**
	 * 统计市场部大单数据（B2B）
	 * @param rateRs 汇率
	 */
	@Transactional(readOnly=false)
	public void updateMarketOrder(Map<String,Map<String,Float>> allRate){
		//日期  国家  产品
		Map<String, Map<String, Map<String, SaleProfit>>> map = Maps.newHashMap();
		String sql = "SELECT t.`order_item_id`,t.`product_name`,t.`money`,r.`country`,t.`refund_type`,r.`account_name`  FROM amazoninfo_refund_item t, `amazoninfo_refund` r " +
				" WHERE t.`refund_id`=r.`id` AND r.`state`='3' AND r.`create_date`>DATE_ADD(CURDATE(), INTERVAL -3 MONTH) AND t.`remark` LIKE '%B2B%' AND t.`refund_type`='Principal'";
		List<Object[]> list = saleProfitDao.findBySql(sql);
		String orderSql = "SELECT DATE_FORMAT(o.`purchase_date`,'%Y%m%d'),t.`quantity_shipped` ,t.`item_price`"+
				" FROM `amazoninfo_orderitem` t,`amazoninfo_order` o WHERE t.`order_id`=o.`id` AND  t.`order_item_id`=:p1";
		//结算报告的信息,统计佣金等费用(销售额和退款以外的总计),用于计算利润
		String settmentSql = "SELECT SUM(IFNULL(t.`shipping`,0) + IFNULL(t.`cod`,0) + IFNULL(t.`gift_wrap`,0) + IFNULL(t.`goodwill`,0) "+
				" + IFNULL(t.`cross_border_fulfillment_fee`,0) + IFNULL(t.`fba_per_unit_fulfillment_fee`,0) + IFNULL(t.`fba_weight_based_fee`,0) "+
				" + IFNULL(t.`commission`,0) + IFNULL(t.`shipping_chargeback`,0) + IFNULL(t.`giftwrap_chargeback`,0) + IFNULL(t.`refund_commission`,0) "+
				" + IFNULL(t.`restocking_fee`,0) + IFNULL(t.`promotion`,0) + IFNULL(t.`cod_fee`,0) + IFNULL(t.`other_fee`,0) + IFNULL(t.`shipping_hb`,0) "+
				" + IFNULL(t.`shipment_fee`,0) + IFNULL(t.`fba_per_order_fulfillment_fee`,0) + IFNULL(t.`tax`,0) + IFNULL(t.`sales_tax_service_fee`,0) + IFNULL(t.`shipping_tax`,0)) AS fee "+
				" FROM `settlementreport_item` t WHERE t.`amazon_order_item_code`=:p1";
		for (Object[] obj : list) {
			String orderItemId = obj[0].toString();
			String productName = obj[1].toString();
			Float money = obj[2]==null?0f:Float.parseFloat(obj[2].toString());
			String country = obj[3].toString();
			String accountName = obj[5].toString();
			List<Object[]> orderList = saleProfitDao.findBySql(orderSql, new Parameter(orderItemId));
			List<Object> feeList = saleProfitDao.findBySql(settmentSql, new Parameter(orderItemId));
			if (orderList==null || orderList.size()==0) {
				continue;
			}
			Object[] objects = orderList.get(0);
			String day = objects[0].toString();
			Integer quantity = Integer.parseInt(objects[1].toString());
			Float total = Float.parseFloat(objects[2].toString());
			Float fee = 0f;
			if (feeList!=null && feeList.size() > 0 && feeList.get(0) != null) {
				fee = Float.parseFloat(feeList.get(0).toString());
			}

			Map<String, Map<String, SaleProfit>> dateMap = map.get(day);
			if (dateMap == null) {
				dateMap = Maps.newHashMap();
				map.put(day, dateMap);
			}
			Map<String, SaleProfit> countryMap = dateMap.get(accountName);
			if (countryMap == null) {
				countryMap = Maps.newHashMap();
				dateMap.put(accountName, countryMap);
			}
			SaleProfit saleProfit = countryMap.get(productName);
			if (saleProfit == null) {
				saleProfit = new SaleProfit();
				saleProfit.setMarketNum(0);
				saleProfit.setMarketSales(0f);
				saleProfit.setMarketProfit(0f);
				saleProfit.setCountry(country);
				saleProfit.setAccountName(accountName);
				saleProfit.setProductName(productName);
				saleProfit.setDay(day);
				countryMap.put(productName, saleProfit);
			}
			saleProfit.setMarketNum(saleProfit.getMarketNum() + quantity);
			String source = "EUR";
			if ("com".equals(country)) {
				source = "USD";
			} else if ("uk".equals(country)) {
				source = "GBP";
			} else if ("jp".equals(country)) {
				source = "JPY";
			} else if ("ca".equals(country)) {
				source = "CAD";
			}
			//单价和运费信息(只使用运费数据,8月份以后单价按下面方式计算)
			Map<String, Map<String,Float>> gwMap = saleReportService.getProductPriceAndTranGwNoTax("EUR", allRate.get(day));
			float orderTotal = (total-money) * MathUtils.getRate(source, "EUR", allRate.get(day));
			saleProfit.setMarketSales(saleProfit.getMarketSales() + orderTotal);

			SaleProfit profit = getByUnique(day, country, productName, accountName);
			if (profit != null) {
				float avgFreight = gwMap.get(productName).get("gw");
				if (profit.getFeeQuantity()!=null && profit.getFeeQuantity()>0) {
					avgFreight = profit.getTransportFee()/profit.getFeeQuantity();
				}
				if (profit.getTariff() == null) {
					profit.setTariff(0f);
				}
				float vat = 0f;
				String temp = country.toUpperCase();
				if("UK".equals(temp)){
					temp = "GB";
				}
				if("COM".equals(temp) || country.contains("com")){
					temp = "US";
				}
				CountryCode vatCode = CountryCode.valueOf(temp);
				if (vatCode != null) {
					vat = vatCode.getVat()/100f;
				}
				//计算B2B利润,税后销售额-退款-运输-关税-采购成本-亚马逊结算报告费用
				float marketProfit = (total/(1+vat)- money + fee) * MathUtils.getRate(source, "EUR", allRate.get(day)) - (profit.getBuyCost()+avgFreight+profit.getTariff())*quantity;
				saleProfit.setMarketProfit(saleProfit.getMarketProfit() + marketProfit);
			}
		}
		String updateSql = "UPDATE `amazoninfo_sale_profit` t SET t.`market_num`=:p1,t.`market_sales`=:p2,t.`market_profit`=:p6 WHERE t.`product_name`=:p3 AND t.`country`=:p4 AND t.`day`=:p5 AND t.`account_name`=:p7";
		for (Map.Entry<String, Map<String, Map<String, SaleProfit>>> entry : map.entrySet()) { 
			Map<String, Map<String, SaleProfit>> dateMap = entry.getValue();
			 for (Map.Entry<String, Map<String, SaleProfit>> entryDate : dateMap.entrySet()) { 
				Map<String, SaleProfit> countryMap =entryDate.getValue();
				for (Map.Entry<String, SaleProfit> entryRs : countryMap.entrySet()) { 
					SaleProfit saleProfit = entryRs.getValue();
					saleProfitDao.updateBySql(updateSql, new Parameter(saleProfit.getMarketNum(),saleProfit.getMarketSales(),saleProfit.getProductName(),saleProfit.getCountry(),saleProfit.getDay(),saleProfit.getMarketProfit(),saleProfit.getAccountName()));
				}
			}
		}
	}

	//根据productName和country找最近的本地贴价格(含采购价、关税、运费)
	public float findRecentBuyCost(String productName, String temp, Map<String,Float> rate) {
		String sql = "SELECT t.`local_price` FROM `amazoninfo_product_price` t  WHERE t.`product_name`=:p1 AND t.`country`=:p2 ORDER BY t.`date` DESC LIMIT 1";
        List<Object> list = saleProfitDao.findBySql(sql, new Parameter(productName, temp));
        float crate = MathUtils.getRate("USD", "EUR", rate);
        for (Object obj : list) {
        	if(obj == null){
        		return 0f;
        	}
        	float amzPrice = Float.parseFloat(obj.toString());
			return amzPrice * crate;
		}
        return 0f;
	}
	
	/**
	 * 补齐产品分类属性
	 */
	@Transactional(readOnly=false)
	public void updateProductAttr(){
		DetachedCriteria dc = saleProfitDao.createDetachedCriteria();
		dc.add(Restrictions.ge("day", "20180101"));
		dc.add(Restrictions.isNull("productAttr"));
		List<SaleProfit> list = saleProfitDao.find(dc);
		//总的定位
		Map<String, String> positionMap = psiProductEliminateService.findProductPositionByCountry(null);
		Map<String, PsiProductEliminate> countryPositionMap = psiProductEliminateService.findProductCountryAttr();
		for (SaleProfit saleProfit : list) {
			String country = saleProfit.getCountry();
			String productName = saleProfit.getProductName();
			String key = productName+ "_" + country;
			PsiProductEliminate eliminate = countryPositionMap.get(key);
			if (eliminate != null ) {
				if ("新品".equals(eliminate.getIsNew())) {
					saleProfit.setProductAttr("新品");
				} else if (StringUtils.isNotEmpty(eliminate.getIsSale())) {
					saleProfit.setProductAttr(eliminate.getIsSale());
				}
			}
			//上一步未匹配国家产品定位,按整个产品规则算定位
			if (StringUtils.isEmpty(saleProfit.getProductAttr())) {
				String isSale = positionMap.get(productName);
				if (StringUtils.isNotEmpty(isSale)) {
					String attr = DictUtils.getDictLabel(isSale, "product_position", "");
					if (StringUtils.isNotEmpty(attr)) {
						saleProfit.setProductAttr(attr);
					}
				}
			}
		}
		saveList(list);
	}

	/**
	 * 查询指定时间区间内的销量,用来均摊仓储费用
	 * @param country
	 * @param minDay
	 * @param maxDay
	 * @return
	 * @throws ParseException 
	@Transactional(readOnly = false)
	public void updateStorageFee(String country, String minDay, String maxDay, Float total) throws ParseException {
		String sql = "SELECT MAX(t.`day`) FROM `amazoninfo_sale_profit` t WHERE t.`storage_fee` IS NOT NULL AND t.`country`='"+country+"'";
		List<Object> list = saleProfitDao.findBySql(sql);
		if (list != null && list.size() > 0 && list.get(0) != null) {
			String dayStr = list.get(0).toString();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			minDay = format.format(DateUtils.addDays(format.parse(dayStr), 1));
		}
		sql = "SELECT SUM(t.`fee_quantity`) FROM `amazoninfo_sale_profit` t WHERE t.`country`=:p1 AND t.`day`>=:p2 AND t.`day`<=:p3";
		int salesVolume = ((BigDecimal)saleProfitDao.findBySql(sql, new Parameter(country,minDay,maxDay)).get(0)).intValue();
		if (salesVolume > 0) {
			float avg = total/salesVolume;	//单个仓储费用
			sql = "UPDATE amazoninfo_sale_profit t SET t.`storage_fee`=t.`fee_quantity`*:p1 WHERE t.`country`=:p2 AND t.`day`>=:p3 AND t.`day`<=:p4";
			try {
				saleProfitDao.updateBySql(sql, new Parameter(avg,country,minDay,maxDay));
			} catch (Exception e) {	//锁表时休眠30秒重试
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e1) {}
				saleProfitDao.updateBySql(sql, new Parameter(avg,country,minDay,maxDay));
			}
		}
	}
	 */

}
