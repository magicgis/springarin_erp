/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.dao.PurchaseForecastDao;
import com.springrain.erp.modules.psi.entity.PurchaseForecastDto;
import com.springrain.erp.modules.psi.entity.PurchaseOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrder;

/**
 * 采购资金计划ervice
 * @author Michael
 * @version 2015-08-24
 */
@Component
@Transactional(readOnly = true)
public class PurchaseForecastService extends BaseService {

	@Autowired
	private PurchaseForecastDao  purchaseForecastDao;
	
	public List<PurchaseForecastDto> findAll(){
		return purchaseForecastDao.findAll();
	}
	
	@Transactional(readOnly = false)
	public void save(PurchaseForecastDto purchaseForecastDto) {
		purchaseForecastDao.save(purchaseForecastDto);
	}

	
	
	
	/**
	 *生成采购资金报表 
	 * @throws ParseException 
	 * 
	 */
	
	@Transactional(readOnly=false)
	public void createForecastData() throws ParseException{
		//每天重新算数据
		String sql="DELETE FROM psi_purchase_forecast_report ";
		this.purchaseForecastDao.updateBySql(sql,null);
		
		
		String monStr="";
		Date forecastData =null;
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf1  = new SimpleDateFormat("yyyy-MM-dd");
		String dayStr =sdf.format(new Date());
		if(Integer.parseInt(dayStr.substring(6))>25){
			//取下月
			monStr=sdf.format(DateUtils.addMonths(new Date(), 1)).substring(0,6);
			forecastData=sdf1.parse(dayStr.substring(0, 4)+"-"+dayStr.substring(4, 6)+"-25");
		}else{
			monStr=dayStr.substring(0,6);
			String aa=sdf.format(DateUtils.addMonths(new Date(), -1));
			forecastData=sdf1.parse(aa.substring(0, 4)+"-"+aa.substring(4, 6)+"-25");
		}
		
		Float currencyRate = AmazonProduct2Service.getRateConfig().get("USD/CNY");
		Set<Integer> productIdSet = this.getPeriodIn30Days();
		Map<String,String> productMap = getProductOrderPlaced();
		Set<String> proColors  = Sets.newHashSet();
		//获得单产品下单量  key:产品名颜色       value:产品id，下单数
		Map<String,String> orderInfoMap =this.getOrderData(productMap,forecastData);
		//收货未付款
		Map<String,Float>  unPayLadingMap = this.getUnPayLading(currencyRate,proColors);
		//未收货未付款
		Map<String,Float>  unPayOrderMap = this.getUnPayOrderAmont(currencyRate,proColors);
		//预测要下单数据
		Map<String,String> foreCastMap  =Maps.newHashMap();
		//获取产品供应商信息   key:产品id  value：供应商信息     供应商id   ： 货币类型、定金、美元价格、人民币价格
		Map<Integer,Map<Integer,String>> proSupplierMap = this.getProSupplier();
		for(Map.Entry<String,String> entry1:orderInfoMap.entrySet()){
			String proNameColor = entry1.getKey();
			String orderInfo=entry1.getValue();
			Integer productId  = Integer.parseInt(orderInfo.split(",")[0]);
			Integer orderQuantity = Float.valueOf(orderInfo.split(",")[1]).intValue();
			Map<Integer,String>  innerMap = proSupplierMap.get(productId);
			if(innerMap!=null){
				//有几个供应商生成几条数据，只把数量限制
				for(Map.Entry<Integer,String> entry:innerMap.entrySet()){
					Integer supplierId = entry.getKey();
					String[] dataArr = entry.getValue().split(",");
					String  currency = dataArr[0];
					Integer  deposit = Integer.parseInt(dataArr[1]);
					Float    price =Float.parseFloat(dataArr[2]);
					Float    rmbPrice =Float.parseFloat(dataArr[3]);
					Float    orderAmount =0f;
					Float    depositAmount =0f;
					Float    ladingAmount =0f;
					if("USD".equals(currency)){
						orderAmount = price*orderQuantity/innerMap.size();
					}else{
						orderAmount = rmbPrice*orderQuantity/(innerMap.size()*currencyRate);
					}
					depositAmount=orderAmount*deposit/100;
					//交期一个月之内算提单金额
					if(productIdSet.contains(productId)){
						ladingAmount =orderAmount-depositAmount;
					}
					String key=supplierId+","+proNameColor;
					proColors.add(key);
					foreCastMap.put(key, orderAmount+","+ladingAmount+","+depositAmount);
				}
			}
		}
		
		//遍历set
		for(String key:proColors){
			PurchaseForecastDto   forecastDto = new PurchaseForecastDto();
			forecastDto.setBalanceLadingAmount(0f);
			forecastDto.setDepositAmount(0f);
			forecastDto.setOrderAmount(0f);
			forecastDto.setLadingAmount(0f);
			if(foreCastMap.get(key)!=null){
				String[] value=foreCastMap.get(key).split(",");
				forecastDto.setOrderAmount(Float.parseFloat(value[0]));
				forecastDto.setLadingAmount(forecastDto.getLadingAmount()+Float.parseFloat(value[1]));
				forecastDto.setDepositAmount(Float.parseFloat(value[2]));
			}
			
			if(unPayLadingMap.get(key)!=null){
				forecastDto.setBalanceLadingAmount(forecastDto.getBalanceLadingAmount()+unPayLadingMap.get(key));
			}
			
			if(unPayOrderMap.get(key)!=null){
				forecastDto.setLadingAmount(forecastDto.getLadingAmount()+unPayOrderMap.get(key));
			}
			forecastDto.setMonth(monStr);
			forecastDto.setSupplierId(Integer.parseInt(key.split(",")[0]));
			forecastDto.setProductNameColor(key.split(",")[1]);
			forecastDto.setDataDate(new Date());
			this.purchaseForecastDao.save(forecastDto);
		}
	}
	
	
	/**
	 *获取产品id，产品名，产品数
	 */
	public Map<String,String> getOrderData(Map<String,String> proMap,Date date){
		Map<String,String> map  = Maps.newHashMap();
		String sql="SELECT a.`product_name`,SUM(a.`order_point`) AS ww,a.`price`,a.`rmb_price` FROM psi_product_in_stock AS a WHERE  a.`data_date`=:p2 AND a.`product_name` IN (:p1) GROUP BY a.`product_name` HAVING ww>0";
		List<Object[]> list = this.purchaseForecastDao.findBySql(sql,new Parameter(proMap.keySet(),date));
		for(int i=0;i<list.size();i++){
			Object[] obj = list.get(i);
			String productName =obj[0].toString();
			Integer orderPoint=Float.valueOf(obj[1].toString()).intValue();
			if(proMap.get(productName)!=null){
				//如果下单量
				if(Integer.parseInt(proMap.get(productName).split(",")[1])/2>orderPoint){
					Integer productId  = Integer.parseInt(proMap.get(productName).split(",")[0]);
					//如果大于最小下单量就取原值，
					if(orderPoint>=Integer.parseInt(proMap.get(productName).split(",")[1])){
						map.put(obj[0].toString(),productId+","+ orderPoint);
					}else{
						map.put(obj[0].toString(),productId+","+ obj[1].toString());
					}
				}
			}
		}
		return map;
	}
	
	public Map<String,String> getProductOrderPlaced(){
		Map<String,String> map  = Maps.newHashMap();
		//产品淘汰分平台、颜色
		String sql = "SELECT DISTINCT CASE WHEN a.color ='' THEN a.product_name ELSE CONCAT(a.product_name,'_',a.color) END AS productName,b.id,b.min_order_placed FROM ( "+
					" SELECT p.id,p.min_order_placed FROM `psi_product_eliminate` e,`psi_product` p "+
					" WHERE e.`product_id`=p.`id` AND e.`del_flag`='0' AND p.`del_flag`='0' AND p.min_order_placed IS NOT NULL AND e.is_sale!='4'"+
					" ) AS b, psi_sku AS a WHERE  a.`del_flag`='0' AND a.`product_id`=b.`id` ";
		List<Object[]> list = this.purchaseForecastDao.findBySql(sql);
		for(int i=0;i<list.size();i++){
			Object[] obj = list.get(i);
			map.put(obj[0].toString(), obj[1].toString()+","+obj[2].toString());
		}
		return map;
	}
	
	
	public Map<Integer,Map<Integer,String>> getProSupplier(){
		Map<Integer,Map<Integer,String>> map  = Maps.newHashMap();
		String sql="SELECT a.`product_id`,a.`supplier_id`,a.`currency_type`,c.`deposit`,ROUND(MIN(a.`price`)*((100+c.`tax_rate`)/100),2) FROM psi_product_tiered_price AS a,psi_product AS b,psi_supplier AS c WHERE a.`supplier_id`=c.id AND  a.`product_id`=b.`id` AND a.`currency_type`=c.`currency_type` AND a.`del_flag`='0' AND b.`del_flag`='0'AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END )  GROUP BY a.`supplier_id`,a.`product_id`,a.`color`,a.`currency_type`";
		List<Object[]> list = this.purchaseForecastDao.findBySql(sql);
		for(Object[] obj:list){
			Integer productId = Integer.parseInt(obj[0].toString());
			Integer supplierId = Integer.parseInt(obj[1].toString());
			String currency = obj[2].toString();
			Map<Integer,String>  innerMap=null;
			if(map.get(productId)==null){
				innerMap = Maps.newHashMap();
			}else{
				innerMap = map.get(productId);
			}
			//供应商id    货币类型、定金、美元价格、人民币价格
			String value="";
			if("USD".equals(currency)){
				value=obj[2]+","+obj[3]+","+(obj[4]==null?"0":obj[4].toString())+",0";
			}else{
				value=obj[2]+","+obj[3]+",0,"+(obj[4]==null?"0":obj[4].toString());
			}
			
			innerMap.put(supplierId,value);
			map.put(productId, innerMap);
		}
		return map;
	}
	
	
	
	public List<PurchaseForecastDto> getForecastReport(PurchaseOrder order,String monStr){
		String whereSql="";
		Parameter para = null;
		List<Object[]> list = null;
		List<PurchaseForecastDto> rsList = Lists.newArrayList();
		if(order.getSupplier()!=null&&order.getSupplier().getId()!=null&&StringUtils.isNotEmpty(order.getOrderNo())){
			whereSql+="AND a.`product_name_color`=:p1 AND a.`supplier_id`=:p2";
			para = new Parameter(order.getOrderNo(),order.getSupplier().getId());
		}else{
			if(StringUtils.isNotEmpty(order.getOrderNo())){
				whereSql+="AND a.`product_name_color`=:p1 ";
				para = new Parameter(order.getOrderNo());
			}
			if(order.getSupplier()!=null&&order.getSupplier().getId()!=null){
				whereSql+="AND a.`supplier_id`=:p1";
				para = new Parameter(order.getSupplier().getId());
			}
		}
		
		String sql="SELECT SUM(a.`order_amount`) AS orderAmont,SUM(a.`deposit_amount`) AS depositAmount,SUM(a.`lading_amount`) AS ladingAmount ,SUM(a.`balance_lading_amount`) AS blanceLadingAmount FROM psi_purchase_forecast_report AS a WHERE 1=1  "+whereSql+" AND a.`month`='"+monStr+"' GROUP BY a.`month`";
		if(para!=null){
			list = this.purchaseForecastDao.findBySql(sql,para);
		}else{
			list = this.purchaseForecastDao.findBySql(sql);
		}
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				PurchaseForecastDto dto = new PurchaseForecastDto(null, null, monStr, Float.parseFloat(obj[0].toString()), Float.parseFloat(obj[1].toString()), Float.parseFloat(obj[2].toString()), Float.parseFloat(obj[3].toString()), null);
				rsList.add(dto);
			}
		}
		return rsList;
	}
	
	
	public List<PurchaseForecastDto> getForecastReport(LcPurchaseOrder order,String monStr){
		String whereSql="";
		Parameter para = null;
		List<Object[]> list = null;
		List<PurchaseForecastDto> rsList = Lists.newArrayList();
		if(order.getSupplier()!=null&&order.getSupplier().getId()!=null&&StringUtils.isNotEmpty(order.getOrderNo())){
			whereSql+="AND a.`product_name_color`=:p1 AND a.`supplier_id`=:p2";
			para = new Parameter(order.getOrderNo(),order.getSupplier().getId());
		}else{
			if(StringUtils.isNotEmpty(order.getOrderNo())){
				whereSql+="AND a.`product_name_color`=:p1 ";
				para = new Parameter(order.getOrderNo());
			}
			if(order.getSupplier()!=null&&order.getSupplier().getId()!=null){
				whereSql+="AND a.`supplier_id`=:p1";
				para = new Parameter(order.getSupplier().getId());
			}
		}
		
		String sql="SELECT SUM(a.`order_amount`) AS orderAmont,SUM(a.`deposit_amount`) AS depositAmount,SUM(a.`lading_amount`) AS ladingAmount ,SUM(a.`balance_lading_amount`) AS blanceLadingAmount FROM lc_psi_purchase_forecast_report AS a WHERE 1=1  "+whereSql+" AND a.`month`='"+monStr+"' GROUP BY a.`month`";
		if(para!=null){
			list = this.purchaseForecastDao.findBySql(sql,para);
		}else{
			list = this.purchaseForecastDao.findBySql(sql);
		}
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				PurchaseForecastDto dto = new PurchaseForecastDto(null, null, monStr, Float.parseFloat(obj[0].toString()), Float.parseFloat(obj[1].toString()), Float.parseFloat(obj[2].toString()), Float.parseFloat(obj[3].toString()), null);
				rsList.add(dto);
			}
		}
		return rsList;
	}
	
	
	/**
	 *未收货订单总共多少钱
	 */
	public Map<String,Float>  getUnPayOrderAmont(Float rate,Set<String> proColors){
		Map<String,Float>  map =Maps.newHashMap();
		String sql="SELECT cc.proColor,cc.supplier_id, SUM(cc.ladingAmount) FROM (SELECT a.supplier_id,(CASE WHEN b.color_code='' THEN  b.`product_name` ELSE CONCAT( b.`product_name`,'_',b.`color_code`) END)" +
				" AS proColor, CASE WHEN  a.currency_type='USD' THEN SUM((b.quantity_ordered-b.quantity_received)*(100-a.`deposit`)/100) ELSE " +
				"SUM((b.quantity_ordered-b.quantity_received)*(100-a.`deposit`)/100)/:p1 END  AS ladingAmount FROM psi_purchase_order AS a,psi_purchase_order_item AS b WHERE " +
				"a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('2','3') AND b.`del_flag`='0'  AND b.quantity_ordered<>b.quantity_received GROUP BY a.id,b.`product_name`,b.`color_code`) AS cc GROUP BY cc.proColor";
		List<Object[]> list=this.purchaseForecastDao.findBySql(sql,new Parameter(rate));
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				String key=obj[1].toString()+","+obj[0].toString();
				map.put(key, Float.parseFloat(obj[2].toString()));
				proColors.add(key);
			}
		}
		return map;
	}
	
	

	/**
	 *有尾款单未支付
	 * 
	 */
		public Map<String,Float> getUnPayLading(Float currentyRate,Set<String> keySet){
			Map<String,Float> rs = Maps.newHashMap();
//			String sql ="SELECT cc.supplier_id ,CASE WHEN cc.color_code ='' THEN cc.product_name ELSE CONCAT(cc.product_name,' ',cc.color_code) END,SUM(unPayAmount) FROM ( SELECT a.id,a.supplier_id,b.`product_name`,b.`color_code`," +
//					"CASE WHEN a.currency_type ='USD' THEN SUM(b.`quantity_lading`*b.`item_price`)*a.total_amount/(SELECT ee.total FROM (SELECT SUM(b.`quantity_lading`*b.`item_price`) AS total,a.`id` FROM psi_lading_bill AS a ,psi_lading_bill_item AS b WHERE a.`id`=b.`lading_bill_id` GROUP BY a.`id`) AS ee WHERE ee.id= a.id) ELSE(SUM(b.`quantity_lading`*b.`item_price`)*a.total_amount/(SELECT ee.total FROM (SELECT SUM(b.`quantity_lading`*b.`item_price`) AS total,a.`id` FROM psi_lading_bill AS a ,psi_lading_bill_item AS b WHERE a.`id`=b.`lading_bill_id` GROUP BY a.`id`) AS ee WHERE ee.id= a.id))/:p1 END " +
//					"AS unPayAmount FROM psi_lading_bill AS a ,psi_lading_bill_item AS b WHERE a.`id`=b.`lading_bill_id` AND b.`del_flag`='0' AND a.`bill_sta`<>'2' AND a.`total_payment_amount`='0' GROUP BY  a.id,a.supplier_id," +
//					"b.`product_name`,b.`color_code` ) AS cc GROUP BY cc.supplier_id,cc.product_name,cc.color_code";
			String sql="SELECT a.supplier_id,CASE WHEN b.color_code ='' THEN b.product_name ELSE CONCAT(b.product_name,'_',b.color_code) END AS proName ," +
					" CASE WHEN a.currency_type ='USD' THEN (b.`total_amount`-b.`total_payment_amount`-b.`total_payment_pre_amount`) ELSE ((b.`total_amount`-b.`total_payment_amount`-b.`total_payment_pre_amount`)/:p1) END  AS unPay " +
					" FROM psi_lading_bill AS a ,psi_lading_bill_item AS b WHERE a.`id`=b.`lading_bill_id` " +
					" AND a.`bill_sta`!='3' AND (b.`total_amount`-b.`total_payment_amount`-b.`total_payment_pre_amount`)>0 " +
					" GROUP BY a.supplier_id,b.product_name,b.color_code";
			List<Object[]> list = this.purchaseForecastDao.findBySql(sql,new Parameter(currentyRate));
			if(list!=null&&list.size()>0){
				for(Object[] obj:list){
					Integer supplierId = Integer.parseInt(obj[0].toString());
					String productNameColor = obj[1].toString();
					Float  payAmount = Float.parseFloat(obj[2].toString());
					String key=supplierId+","+productNameColor;
					keySet.add(key);
					rs.put(key, payAmount);
				}
			}
			return rs;
		}
	
	/**
	 *查询交期在一个月内的产品id 
	 */
	public Set<Integer>  getPeriodIn30Days(){
		Set<Integer> set  =Sets.newHashSet();
		String sql="SELECT a.`id` FROM psi_product AS a WHERE a.`produce_period`<30";
		List<Integer> list = this.purchaseForecastDao.findBySql(sql);
		set.addAll(list);
		return set;
	}
	
}
