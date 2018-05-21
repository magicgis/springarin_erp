package com.springrain.erp.modules.amazoninfo.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.SaleProfitDao;
import com.springrain.erp.modules.amazoninfo.entity.SaleProfit;
import com.springrain.erp.modules.amazoninfo.entity.SaleReport;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;

/**
 * 产品按天统计销量销售额利润Service
 */
@Component
@Transactional(readOnly = true)
public class SaleAnalysisReportService extends BaseService {
	
	@Autowired
	private SaleProfitDao saleProfitDao;

	
	@Autowired
	private PsiProductTypeGroupDictService groupDictService;
	
	/**
	 * 总销售额情况分析
	 * @param saleProfit(排除B2B销量、销售额、利润)
	 * @param flag	1:按月  3：按年
	 * @return map key 1:结果集合列表  2：总计
	 */
	public List<SaleProfit> getSalesDataList(SaleProfit saleProfit, String groupType, String countryGroup, List<String> monthList){
		String temp = "";
		if (StringUtils.isNotEmpty(saleProfit.getCountry()) && !"total".equals(saleProfit.getCountry()) 
				&& !"en".equals(saleProfit.getCountry()) && !"eu".equals(saleProfit.getCountry()) 
				&& !"nonEn".equals(saleProfit.getCountry()) && !"noUs".equals(saleProfit.getCountry())) {
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
		
		String temp1 = "";
		if ("1".equals(groupType)) {
			temp1 = " t.`product_name`, ";
		} else if ("2".equals(groupType)) {
			temp1 = " t.`type`, ";
		}
		
		if ("1".equals(countryGroup)) {
			temp1 += " t.`country`, ";
		}
		
		String temp2 = "";
		if (StringUtils.isNotEmpty(saleProfit.getLine())) {
			if ("A".equals(saleProfit.getLine())) {
				temp2 = " AND t.`line` IN ('A','F') ";	//A/F线已合并
			} else {
				temp2 = " AND t.`line`='"+saleProfit.getLine()+"' ";
			}
		}
		Parameter parameter = new Parameter(monthList);
		String sql = "SELECT t.`product_name`,t.`type`,t.`line`,SUM(t.`sales`-IFNULL(t.`market_sales`,0)),SUM(t.`sales_volume`-IFNULL(t.`market_num`,0)),SUM(t.`profits`-IFNULL(t.`market_profit`,0)),SUM(t.`sales_no_tax`), "+ 
				" SUM(t.`amazon_fee`),SUM(t.`refund`),SUM(t.`buy_cost`),SUM(t.`other_fee`),SUM(t.`transport_fee`),t.`month`, " +
				" SUM(t.`ad_in_event_fee`), SUM(t.`ad_in_profit_fee`),SUM(t.`ad_out_event_fee`),SUM(t.`ad_out_profit_fee`),"+
				" SUM(t.`ad_in_event_sales`), SUM(t.`ad_in_profit_sales`),SUM(t.`ad_out_event_sales`),SUM(t.`ad_out_profit_sales`),"+
				" SUM(t.`ad_in_event_sales_volume`), SUM(t.`ad_in_profit_sales_volume`),SUM(t.`ad_out_event_sales_volume`),SUM(t.`ad_out_profit_sales_volume`),"+
				" SUM(t.`support_num`),SUM(t.`support_amazon_fee`),SUM(t.`support_cost`),SUM(t.`review_num`),SUM(t.`review_amazon_fee`),SUM(t.`review_cost`)," +
				" SUM(t.`recall_num`),SUM(t.`recall_cost`),SUM(t.`recall_fee`),SUM(IFNULL(t.`tariff`,0)),SUM(IFNULL(t.`storage_fee`,0)),SUM(IFNULL(t.`fee_quantity`,0)),"+
				" SUM(IFNULL(t.`ad_ams_sales`,0)) ,SUM(IFNULL(t.`ad_ams_sales_volume`,0)),SUM(IFNULL(t.`ad_ams_fee`,0)),"+
				" SUM(IFNULL(t.`long_storage_fee`,0)) ,SUM(IFNULL(t.`deal_fee`,0)),SUM(IFNULL(t.`deal_sales_volume`,0)),SUM(IFNULL(t.`deal_profit`,0)),SUBSTRING_INDEX(group_concat(t.product_attr order by FIELD(t.country,'de','uk','fr','jp','it','es','com','ca','mx'),FIELD(t.product_attr,'淘汰','新品','爆款','利润款','主力','普通')),',',1),"+
				" SUM(IFNULL(t.`mold_fee`,0)),t.`country` "+
				" FROM `amazoninfo_report_month_type` t WHERE t.`month` IN :p1 "+temp+temp2+"  AND t.`type` is not null "+
				" GROUP BY "+temp1+"t.`month` ORDER BY t.`month`,SUM(t.`sales`) DESC ";
		List<Object[]> list = saleProfitDao.findBySql(sql, parameter);
		List<SaleProfit> rs = Lists.newArrayList();
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
			String country = objs[46].toString();
			buyCost = buyCost + moldFee;	//模具费算到采购成本
			profits = profits - moldFee;
			//利润减去仓储费和闪促费
			profits = profits + storageFee + longStorageFee + dealFee;
			
			//杂费里面统计仓储费(仓储费为负数,直接相加)
			//otherFee = otherFee + storageFee;
			//利润去除评测单(替代货在统计时已剔除)
			profits = profits + reviewAmazonFee - reviewCost - tariff;
			//利润去除召回单
			profits = profits - recallFee - recallCost;
			/*if ("1".equals(groupType)) {
				productName = type;
			} else if ("2".equals(groupType)) {
				productName = line;
			}*/

			SaleProfit profit = new SaleProfit();
			profit.setProductName(productName);
			profit.setCountry(country);
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
			rs.add(profit);

		}
		return rs;
	}
	
	/**
	 * 
	 * @param saleProfit(排除B2B销量、销售额、利润)
	 * @param flag	1:按月  3：按年
	 * @return map key 1:结果集合列表  2：总计
	 */
	public List<SaleProfit> getSalesProfitDataList(SaleProfit saleProfit, String groupType, List<String> monthList){
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
		Parameter parameter = new Parameter(saleProfit.getDay(), saleProfit.getEnd());
		String sql ="SELECT t.`product_name`,t.`type`,t.`line`,SUM(t.`sales`-IFNULL(t.`market_sales`,0)),SUM(t.`sales_volume`-IFNULL(t.`market_num`,0)),SUM(t.`profits`-IFNULL(t.`market_profit`,0)),SUM(t.`sales_no_tax`), "+ 
					" SUM(t.`amazon_fee`),SUM(t.`refund`),SUM(t.`buy_cost`),SUM(t.`other_fee`),SUM(t.`transport_fee`),t.`month`, " +
					" SUM(t.`ad_in_event_fee`), SUM(t.`ad_in_profit_fee`),SUM(t.`ad_out_event_fee`),SUM(t.`ad_out_profit_fee`),"+
					" SUM(t.`ad_in_event_sales`), SUM(t.`ad_in_profit_sales`),SUM(t.`ad_out_event_sales`),SUM(t.`ad_out_profit_sales`),"+
					" SUM(t.`ad_in_event_sales_volume`), SUM(t.`ad_in_profit_sales_volume`),SUM(t.`ad_out_event_sales_volume`),SUM(t.`ad_out_profit_sales_volume`),"+
					" SUM(t.`support_num`),SUM(t.`support_amazon_fee`),SUM(t.`support_cost`),SUM(t.`review_num`),SUM(t.`review_amazon_fee`),SUM(t.`review_cost`)," +
					" SUM(t.`recall_num`),SUM(t.`recall_cost`),SUM(t.`recall_fee`),SUM(IFNULL(t.`tariff`,0)),SUM(IFNULL(t.`storage_fee`,0)),SUM(IFNULL(t.`fee_quantity`,0)),"+
					" SUM(IFNULL(t.`ad_ams_sales`,0)) ,SUM(IFNULL(t.`ad_ams_sales_volume`,0)),SUM(IFNULL(t.`ad_ams_fee`,0)),"+
					" SUM(IFNULL(t.`long_storage_fee`,0)) ,SUM(IFNULL(t.`deal_fee`,0)),SUM(IFNULL(t.`deal_sales_volume`,0)),SUM(IFNULL(t.`deal_profit`,0)),SUBSTRING_INDEX(group_concat(t.product_attr order by FIELD(t.country,'de','uk','fr','jp','it','es','com','ca','mx'),FIELD(t.product_attr,'淘汰','新品','爆款','利润款','主力','普通')),',',1),"+
					" SUM(IFNULL(t.`mold_fee`,0)) "+
					" FROM `amazoninfo_report_month_type` t WHERE t.`month` >=:p1 AND t.`month` <=:p2 "+temp+temp2+"  AND t.`type` is not null GROUP BY "+temp1+",t.`month` ORDER BY t.`month`,SUM(t.`sales_volume`) DESC ";
		
		List<Object[]> list = saleProfitDao.findBySql(sql, parameter);
		List<SaleProfit> rs = Lists.newArrayList();
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
			//利润减去仓储费和闪促费
			profits = profits + storageFee + longStorageFee + dealFee;
			
			//利润去除评测单(替代货在统计时已剔除)
			profits = profits + reviewAmazonFee - reviewCost - tariff;
			//利润去除召回单
			profits = profits - recallFee - recallCost;
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

		}
		return rs;
	}

	public List<String> findNewProduct(String currMonth, SaleProfit saleProfit) {
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

		String temp2 = "";
		if (StringUtils.isNotEmpty(saleProfit.getLine())) {
			temp2 = " AND t.`line`='"+saleProfit.getLine()+"' ";
		}
		String sql = "SELECT DISTINCT t.`product_name` FROM `amazoninfo_report_month_type` t WHERE t.`month`=:p1 "+temp+temp2+" AND t.`sales`>0 AND t.`product_attr`='新品'";
		return saleProfitDao.findBySql(sql, new Parameter(currMonth));
	}

    //month - type -sales
	public Map<String,SaleReport> findMarketingQuantity(SaleProfit saleProfit,Map<String, String> lineMap,Date start,Date end){
		Map<String,SaleReport> map = Maps.newLinkedHashMap();
		String temp = "";
		if (StringUtils.isNotEmpty(saleProfit.getCountry()) && !"total".equals(saleProfit.getCountry()) 
				&& !"en".equals(saleProfit.getCountry()) && !"eu".equals(saleProfit.getCountry()) 
				&& !"nonEn".equals(saleProfit.getCountry()) && !"noUs".equals(saleProfit.getCountry())) {
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
		
		String temp2 = "";
		if (StringUtils.isNotEmpty(saleProfit.getLine())) {
			temp2 = " AND t.`product_name` in :p3 ";
		}	
		List<Object[]> list= null;
		String sql="SELECT DATE_FORMAT(t.`date`,'%Y%m') dates,SUM(t.`sure_sales_volume`*IFNULL(t.`pack_num`,1)) volume,SUM(IFNULL(ads_order,0)+IFNULL(ams_order,0)) ads, "+
			    " SUM(IFNULL(free_order,0)) free,SUM(IFNULL(flash_sales_order,0)) flash,SUM(IFNULL(promotions_order,0)) promotions,SUM(IFNULL(support_volume,0)) support,SUM(IFNULL(review_volume,0)) review "+
				" FROM amazoninfo_sale_report t WHERE t.order_type='1' and t.date>=:p1 and t.date<:p2 "+temp+temp2+" "+
				" GROUP BY dates order by dates asc ";
		if (StringUtils.isNotEmpty(saleProfit.getLine())) {
			String line= lineMap.get(saleProfit.getLine());
			List<String> nameList=groupDictService.getProductByLineId(line);
			list = saleProfitDao.findBySql(sql,new Parameter(DateUtils.getFirstDayOfMonth(start),DateUtils.addDays(DateUtils.getLastDayOfMonth(end), 1),nameList));
		}else{
			list = saleProfitDao.findBySql(sql,new Parameter(DateUtils.getFirstDayOfMonth(start),DateUtils.addDays(DateUtils.getLastDayOfMonth(end), 1)));
		}
		for (Object[] obj: list) {
			String  month = obj[0].toString();
			Integer total = Integer.parseInt(obj[1].toString());
			Integer ads =  Integer.parseInt(obj[2].toString());
			Integer free = Integer.parseInt(obj[3].toString());
			Integer flash = Integer.parseInt(obj[4].toString());
			Integer promotions = Integer.parseInt(obj[5].toString());
			Integer support = Integer.parseInt(obj[6].toString());
			Integer review = Integer.parseInt(obj[7].toString());
			
			SaleReport report = new SaleReport();
			report.setSalesVolume(total);
			report.setAdsOrder(ads);
			report.setFreeOrder(free);
			report.setFlashSalesOrder(flash);
			report.setPromotionsOrder(promotions);
			report.setSupportVolume(support);
			report.setReviewVolume(review);
			
			report.setRealOrder(total-ads-free-flash-promotions-support-review);//自然
			report.setAmsOrder(ads+free+flash+promotions+support+review);//营销
			
			map.put(month, report);
			
			
		}
		return map;
	}
	
	
	public Map<String,Map<String,Integer>> find(SaleProfit saleProfit,Map<String, String> lineMap,Date start,Date end){
		Map<String,Map<String,Integer>> map = Maps.newLinkedHashMap();
		
		String temp = "";
		if (StringUtils.isNotEmpty(saleProfit.getCountry()) && !"total".equals(saleProfit.getCountry()) 
				&& !"en".equals(saleProfit.getCountry()) && !"eu".equals(saleProfit.getCountry()) 
				&& !"nonEn".equals(saleProfit.getCountry()) && !"noUs".equals(saleProfit.getCountry())) {
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
		
		String temp2 = "";
		if (StringUtils.isNotEmpty(saleProfit.getLine())) {
			temp2 = " AND t.`product_name` in :p3 ";
		}	
		List<Object[]> list= null;
		
		String sql= "SELECT DATE_FORMAT(t.`purchase_date`,'%Y%m') dates,t.`product_name`,COUNT(*) FROM amazoninfo_promotions_report t "+
		   " WHERE  t.`purchase_date`>=:p1 AND t.`purchase_date`<:p2  "+temp+temp2+" AND t.`promotion_ids`='闪购' "+
		   " GROUP BY dates,t.`product_name` order by dates asc ";
		if (StringUtils.isNotEmpty(saleProfit.getLine())) {
			String line= lineMap.get(saleProfit.getLine());
			List<String> nameList=groupDictService.getProductNameByLineId(line);
			list = saleProfitDao.findBySql(sql,new Parameter(DateUtils.getFirstDayOfMonth(start),DateUtils.addDays(DateUtils.getLastDayOfMonth(end), 1),nameList));
		}else{
			list = saleProfitDao.findBySql(sql,new Parameter(DateUtils.getFirstDayOfMonth(start),DateUtils.addDays(DateUtils.getLastDayOfMonth(end), 1)));
		}
		for (Object[] obj: list) {
			String date= obj[0].toString();
			String name = (obj[1]==null?"":obj[1].toString());
			Integer quantity = Integer.parseInt(obj[2].toString());
			
			Map<String,Integer> tempMap = map.get(date);
			if(tempMap==null){
				tempMap = Maps.newLinkedHashMap();
				map.put(date, tempMap);
			}
			tempMap.put(name, quantity);
			
			Integer totalQty= (tempMap.get("total")==null?0:tempMap.get("total"));
			tempMap.put("total", totalQty+quantity);
		}
		return map;
	}
	
	
	public Map<String,Map<String,Object[]>> findAdsDate(SaleProfit saleProfit,Map<String, String> lineMap,Date start,Date end){
        Map<String,Map<String,Object[]>> map = Maps.newLinkedHashMap();
		
		String temp = "";
		if (StringUtils.isNotEmpty(saleProfit.getCountry()) && !"total".equals(saleProfit.getCountry()) 
				&& !"en".equals(saleProfit.getCountry()) && !"eu".equals(saleProfit.getCountry()) 
				&& !"nonEn".equals(saleProfit.getCountry()) && !"noUs".equals(saleProfit.getCountry())) {
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
		
		String temp2 = "";
		if (StringUtils.isNotEmpty(saleProfit.getLine())) {
			temp2 = " AND t.`product_name` in :p3 ";
		}	
		List<Object[]> list= null;
		String sql="SELECT month,product_name,ifnull(ams_flag,'1') amsFlag,ifnull(spa_flag,'1') spaFlag,avg(spa_avg_click),avg(ams_avg_click) "+
                   " FROM amazoninfo_ads_month_report t WHERE t.`month`>=:p1 and t.month<=:p2 "+temp+temp2+" group by month,product_name,amsFlag,spaFlag order by month asc " ;
		
		if (StringUtils.isNotEmpty(saleProfit.getLine())) {
			String line= lineMap.get(saleProfit.getLine());
			List<String> nameList=groupDictService.getProductNameByLineId(line);
			list = saleProfitDao.findBySql(sql,new Parameter(DateUtils.getDate(start, "yyyyMM"),DateUtils.getDate(end, "yyyyMM"),nameList));
		}else{
			list = saleProfitDao.findBySql(sql,new Parameter(DateUtils.getDate(start, "yyyyMM"),DateUtils.getDate(end, "yyyyMM")));
		}
		
		if(list!=null&&list.size()>0){
			   for (Object[] obj: list) {
				    Map<String,Object[]> tempMap = map.get(obj[0].toString());
					if(tempMap==null){
						tempMap = Maps.newLinkedHashMap();
						map.put(obj[0].toString(), tempMap);
					}
					tempMap.put(obj[1].toString(), obj);
			   }
		}
		return map;
	}
	
	
	public Map<String,Map<Integer,Integer>> findRankDate(SaleProfit saleProfit,Map<String, String> lineMap,Date start,Date end){
		 Map<String,Map<Integer,Integer>> map = Maps.newLinkedHashMap();
		
		String temp = "";
		if (StringUtils.isNotEmpty(saleProfit.getCountry()) && !"total".equals(saleProfit.getCountry()) 
				&& !"en".equals(saleProfit.getCountry()) && !"eu".equals(saleProfit.getCountry()) 
				&& !"nonEn".equals(saleProfit.getCountry()) && !"noUs".equals(saleProfit.getCountry())) {
			temp = " AND t.`country`='"+saleProfit.getCountry()+"' ";
		} else{
			return map;
		}
		
		String temp2 = "";
		if (StringUtils.isNotEmpty(saleProfit.getLine())) {
			temp2 = " AND t.line = :p3 ";
		}	
		List<Object[]> list= null;
		String sql="SELECT rank,sales_volume,month "+
                   " FROM amazoninfo_rank_sales_month_report t WHERE t.`month`>=:p1 and t.month<=:p2 "+temp+temp2+" order by month asc " ;
		
		if (StringUtils.isNotEmpty(saleProfit.getLine())) {
			list = saleProfitDao.findBySql(sql,new Parameter(DateUtils.getDate(start, "yyyyMM"),DateUtils.getDate(end, "yyyyMM"),saleProfit.getLine()));
		}else{
			list = saleProfitDao.findBySql(sql,new Parameter(DateUtils.getDate(start, "yyyyMM"),DateUtils.getDate(end, "yyyyMM")));
		}
		
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				Map<Integer,Integer> tempMap = map.get(obj[2].toString());
				if(tempMap==null){
					tempMap = Maps.newLinkedHashMap();
					map.put(obj[2].toString(), tempMap);
				}
				tempMap.put(Integer.parseInt(obj[0].toString()), Integer.parseInt(obj[1].toString()));
			}
		}
		return map;
	}
	
	
	 public Map<String,Map<String,Object[]>> findBestSeller(SaleProfit saleProfit,Map<String, String> lineMap,Date start,Date end){
		 Map<String,Map<String,Object[]>> map=Maps.newLinkedHashMap();

			String temp = "";
			if (StringUtils.isNotEmpty(saleProfit.getCountry()) && !"total".equals(saleProfit.getCountry()) 
					&& !"en".equals(saleProfit.getCountry()) && !"eu".equals(saleProfit.getCountry()) 
					&& !"nonEn".equals(saleProfit.getCountry()) && !"noUs".equals(saleProfit.getCountry())) {
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
			
			String temp2 = "";
			if (StringUtils.isNotEmpty(saleProfit.getLine())) {
				temp2 = " AND t.`line` = :p3 ";
			}	
			List<Object[]> list= null;
			String sql="SELECT month,country,count(*),SUM(CASE WHEN t.`bestseller` LIKE '%1%' THEN 1 ELSE 0 END) num  "+
	                   " FROM amazoninfo_catalog_month_report t WHERE t.`month`>=:p1 and t.month<=:p2 "+temp+temp2+" group by month,country  order by month asc " ;
			
			if (StringUtils.isNotEmpty(saleProfit.getLine())) {
				list = saleProfitDao.findBySql(sql,new Parameter(DateUtils.getDate(start, "yyyyMM"),DateUtils.getDate(end, "yyyyMM"),saleProfit.getLine()));
			}else{
				list = saleProfitDao.findBySql(sql,new Parameter(DateUtils.getDate(start, "yyyyMM"),DateUtils.getDate(end, "yyyyMM")));
			}
			
			if(list!=null&&list.size()>0){
				   for (Object[] obj: list) {
					    Map<String,Object[]> tempMap = map.get(obj[0].toString());
						if(tempMap==null){
							tempMap = Maps.newLinkedHashMap();
							map.put(obj[0].toString(), tempMap);
						}
						tempMap.put(obj[1].toString(), obj);
				   }
			}
		 return map;
	 }
	 
	 
	 public Map<String,Map<String,Map<String,Object[]>>> findAfterSales(SaleProfit saleProfit,Map<String, String> lineMap,Date start,Date end){
		 Map<String,Map<String,Map<String,Object[]>>> map=Maps.newLinkedHashMap();

			String temp = "";
			if (StringUtils.isNotEmpty(saleProfit.getCountry()) && !"total".equals(saleProfit.getCountry()) 
					&& !"en".equals(saleProfit.getCountry()) && !"eu".equals(saleProfit.getCountry()) 
					&& !"nonEn".equals(saleProfit.getCountry()) && !"noUs".equals(saleProfit.getCountry())) {
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
			
			String temp2 = "";
			if (StringUtils.isNotEmpty(saleProfit.getLine())) {
				temp2 = " AND t.`product_name` in :p3 ";
			}	
			List<Object[]> list= null;
			String sql="SELECT month,product_name,country,review_asin "+
	                   " FROM amazoninfo_review_month_report t WHERE t.`month`>=:p1 and t.month<=:p2 "+temp+temp2+" order by month asc " ;
			
			if (StringUtils.isNotEmpty(saleProfit.getLine())) {
				String line= lineMap.get(saleProfit.getLine());
				List<String> nameList=groupDictService.getProductNameByLineId(line);
				nameList.add(saleProfit.getLine());
				list = saleProfitDao.findBySql(sql,new Parameter(DateUtils.getDate(start, "yyyyMM"),DateUtils.getDate(end, "yyyyMM"),nameList));
			}else{
				list = saleProfitDao.findBySql(sql,new Parameter(DateUtils.getDate(start, "yyyyMM"),DateUtils.getDate(end, "yyyyMM")));
			}
			
			if(list!=null&&list.size()>0){
				   for (Object[] obj: list) {
					    Map<String,Map<String,Object[]>> monthMap = map.get(obj[0].toString());
						if(monthMap==null){
							monthMap = Maps.newLinkedHashMap();
							map.put(obj[0].toString(), monthMap);
						}
						
						Map<String,Object[]> tempMap = monthMap.get(obj[2].toString());
						if(tempMap==null){
							tempMap = Maps.newLinkedHashMap();
							monthMap.put(obj[2].toString(), tempMap);
						}
						
						Object[] tempObj = new Object[3];
						String[] star=obj[3].toString().split(",");//5_R2Q5WBGUL318AD,5_RJYHLGH2W6FYQ,5_R397ZQOCJ02NLV,5_R1ZBYMB7SCX9UE
						String[] starAndReview = star[0].split("_");
						if(Integer.parseInt(starAndReview[0])<3){
							tempObj[0]="Y";
						}else{
							tempObj[0]="N";
						}
						Integer num=0;
						
						String starLink = "";
						String country = obj[2].toString();
						for (String arr: star) {
							String[] tempArr = arr.split("_");
							if(Integer.parseInt(tempArr[0])<3){
								num++;
							}
							
							String suffix=country;
        					if("jp,uk".contains(country)){
        						suffix="co."+country;
        					}else if("mx".equals(country)){
        						suffix="com."+country;
        					}
        					String link="https://www.amazon."+suffix+"/review/"+tempArr[1];
							starLink += "<a target='blank' href='"+link+"'>"+tempArr[0]+"</a> ";
						}
						tempObj[1] = num*100f/star.length;
						
						tempObj[2] = starLink;
						tempMap.put(obj[1].toString(), tempObj);
				   }
			}
		 return map;
	 }
	
	 
	 @Transactional(readOnly = false)
	 public void updateAndSaveReportData(){
		   String sql="INSERT INTO `amazoninfo_ads_month_report` (product_name,country,month,spa_avg_click,spa_flag)  "+ 
					" SELECT CONCAT(p.`product_name`,CASE WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END) product_name,t.country,DATE_FORMAT(t.`data_date`,'%Y%m') dates,AVG(t.`clicks`) spa_avg_click,'0' spa_flag "+
					"	FROM amazoninfo_advertising t "+
					"	JOIN psi_sku p ON t.`country`=p.`country` AND t.sku=p.sku  AND p.`del_flag`='0' "+
					"	WHERE t.`data_date`>=:p1 AND t.`data_date`<:p2 "+ 
					"	GROUP BY dates,NAME,country "+
					" ON DUPLICATE KEY UPDATE `spa_avg_click` = VALUES(spa_avg_click),`spa_flag` = VALUES(spa_flag) ";
		  Date today = DateUtils.getDateStart(new Date());
		  saleProfitDao.updateBySql(sql,new Parameter(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(today,-1)),DateUtils.addDays(today, 1)));
		
		  String sql1="INSERT INTO `amazoninfo_ads_month_report` (product_name,country,month,ams_avg_click,ams_flag)  "+ 
					" SELECT t.`product_name`,t.country,DATE_FORMAT(t.`data_date`,'%Y%m') dates,AVG(t.`clicks`) ams_avg_click,'0' ams_flag "+
					"	FROM amazoninfo_aws_adversting t "+
					"	WHERE t.`data_date`>=:p1 AND t.`data_date`<:p2 and product_name is not null "+ 
					"	GROUP BY dates,product_name,country "+
					" ON DUPLICATE KEY UPDATE `ams_avg_click` = VALUES(ams_avg_click),`ams_flag` = VALUES(ams_flag) ";
		  saleProfitDao.updateBySql(sql1,new Parameter(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(today,-1)),DateUtils.addDays(today, 1)));
		  
		 
		  String sql2="INSERT INTO `amazoninfo_rank_sales_month_report` (MONTH,country,rank,line,sales_volume)  "+
					  "	SELECT DATE_FORMAT(DATE_ADD(r.`query_time`, INTERVAL -1 DAY),'%Y%m') dates,r.`country`,r.rank,SUBSTRING(b.lineName,'1','1') lineName ,CEIL(AVG(t.`sales_volume`)) sales_volume    "+
					  " FROM amazoninfo_catalog_rank r     "+
					  " JOIN amazoninfo_sale_report t  ON t.`date`=DATE_ADD(r.`query_time`, INTERVAL -1 DAY) AND r.`country`=t.`country`     "+
					  "  AND r.product_name=CONCAT(t.`product_name`,CASE WHEN t.`color`='' THEN '' ELSE CONCAT('_',t.`color`) END) 			   "+		
					  " JOIN    (SELECT d.name,p.name lineName FROM (     "+
					  " SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,a.type   "+  
					  " FROM psi_product a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.del_flag='0' ) d     "+
					  " JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type'     "+
					  " JOIN psi_product_type_group g ON t.id=g.`dict_id`     "+
					  " JOIN psi_product_type_dict p ON p.id=g.id  AND p.`del_flag`='0') b ON r.product_name=b.name    "+
					  " WHERE r.`query_time`>=:p1 AND r.`query_time`<:p2  AND r.rank<=100  AND r.`path_name` IS NOT NULL AND r.`product_name` IS NOT NULL    "+
					  " GROUP BY dates,r.`country`,r.rank,b.lineName   ON DUPLICATE KEY UPDATE `sales_volume` = VALUES(sales_volume)  ";
		  saleProfitDao.updateBySql(sql2,new Parameter(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(today,-1)),DateUtils.addDays(today, 1)));
		  
		  
		  String sql3="INSERT INTO `amazoninfo_catalog_month_report` (MONTH,country,line,path_name,bestseller)  "+
		          " SELECT DATE_FORMAT(DATE_ADD(r.`query_time`, INTERVAL -1 DAY),'%Y%m') dates,r.`country`,  "+
				  " SUBSTRING(b.lineName,'1','1') lineName,r.path_name,GROUP_CONCAT(DISTINCT (CASE WHEN r.rank=1 THEN 1 ELSE 0 END) ) rank   "+
				  "  FROM amazoninfo_catalog_rank r  JOIN     "+
				  "  (SELECT d.name,p.name lineName FROM (     "+
				  "  SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,a.type    "+
				  "   FROM psi_product a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.del_flag='0' ) d    "+
				  "   JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type'    "+
				  "   JOIN psi_product_type_group g ON t.id=g.`dict_id`     "+
				  " JOIN psi_product_type_dict p ON p.id=g.id  AND p.`del_flag`='0') b ON r.product_name=b.name    "+
				  "  WHERE r.`query_time`>=:p1 AND r.`query_time`<:p2  AND r.`path_name` IS NOT NULL AND r.`product_name` IS NOT NULL   "+ 
				  "  GROUP BY dates,r.`country`,b.lineName,r.path_name  "+
				  "   ON DUPLICATE KEY UPDATE `bestseller` = VALUES(bestseller)   ";
		  saleProfitDao.updateBySql(sql3,new Parameter(DateUtils.getFirstDayOfMonth(today),DateUtils.addDays(today, 1)));
		  
		  
		  
		  String sql6 ="SELECT SUBSTRING_INDEX(GROUP_CONCAT(t.`id`),',',1-COUNT(1)) "+
				" FROM amazoninfo_review_comment t "+
				" GROUP BY t.`star`,t.`review_asin` HAVING COUNT(1)>1";
		List<Object> list = saleProfitDao.findBySql(sql6, null);
		if(list!=null && list.size()>0){
			sql = "DELETE FROM amazoninfo_review_comment WHERE id IN :p1 ";
			for (Object object : list) {
				saleProfitDao.updateBySql(sql, new Parameter(Lists.newArrayList(object.toString().split(","))));
			}
		}
		  
		  String sql4="INSERT INTO `amazoninfo_review_month_report` (MONTH,country,product_name,star,review_asin)  SELECT DATE_FORMAT(c.`review_date`,'%Y%m') dates,p.`country`,p.name NAME, "+
				 " SUBSTRING_INDEX(GROUP_CONCAT(c.`star` ORDER BY c.`review_date`,c.`id`),',',5) star, SUBSTRING_INDEX(GROUP_CONCAT(distinct concat(c.star,'_',c.review_asin) ORDER BY c.`review_date`,c.`id`),',',5) review_asin  "+
				 " FROM (select distinct p.country,p.asin,CONCAT(t.`product_name`,CASE WHEN t.`color`='' THEN '' ELSE CONCAT('_',t.`color`) END) NAME from psi_product_eliminate t "+
				 " JOIN psi_sku p  ON t.`product_name`=p.`product_name` AND t.`color`=p.`color` AND t.`country`=p.`country` AND p.`del_flag`='0' where  t.`is_new`='1' AND t.`del_flag`='0') p "+
				 " JOIN amazoninfo_review_comment c ON p.`country`=c.`country` AND p.`asin`=c.`asin` "+
				 " WHERE  c.`review_date`>=:p1 and  c.`review_date`<:p2  "+
				 " GROUP BY dates,NAME,p.`country` ON DUPLICATE KEY UPDATE `star` = VALUES(star),`review_asin` = VALUES(review_asin)   ";
		  saleProfitDao.updateBySql(sql4,new Parameter(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(today,-1)),DateUtils.addDays(today, 1)));
	 
	      
		  String sql5 ="INSERT INTO `amazoninfo_review_month_report` (MONTH,country,product_name,star,review_asin)  "+
				  " SELECT DATE_FORMAT(c.`review_date`,'%Y%m') dates,p.`country`,SUBSTRING(b.lineName,'1','1') NAME,   "+
				  " SUBSTRING_INDEX(GROUP_CONCAT(c.`star` ORDER BY c.`review_date`,c.`id`),',',5) star, SUBSTRING_INDEX(GROUP_CONCAT(distinct concat(c.star,'_',c.review_asin) ORDER BY c.`review_date`,c.`id`),',',5) review_asin    "+
				  " FROM (select distinct p.country,p.asin,t.`product_name` product_name from psi_product_eliminate t    "+
				  " JOIN psi_sku p  ON t.`product_name`=p.`product_name` AND t.`color`=p.`color` AND t.`country`=p.`country` AND p.`del_flag`='0' where t.`is_new`='1' AND t.`del_flag`='0'  ) p   "+ 
				  "  JOIN amazoninfo_review_comment c ON p.`country`=c.`country` AND p.`asin`=c.`asin`    "+
				  
				  "  join (SELECT d.name,p.name lineName FROM (     "+
				  "  SELECT CONCAT(brand,' ',model) NAME,a.type    "+
				  "   FROM psi_product a  WHERE a.del_flag='0' ) d    "+
				  "   JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type'    "+
				  "   JOIN psi_product_type_group g ON t.id=g.`dict_id`     "+
				  " JOIN psi_product_type_dict p ON p.id=g.id  AND p.`del_flag`='0') b ON p.product_name=b.name    "+
				  
				  "  WHERE  c.`review_date`>=:p1 and  c.`review_date`<:p2    "+
				  "  GROUP BY dates,NAME,p.`country` ON DUPLICATE KEY UPDATE `star` = VALUES(star),`review_asin` = VALUES(review_asin)   ";
		  saleProfitDao.updateBySql(sql5,new Parameter(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(today,-1)),DateUtils.addDays(today, 1)));
	 }
}
