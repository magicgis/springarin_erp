/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.lc;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.dao.lc.LcPurchaseFinancialDao;
import com.springrain.erp.modules.psi.entity.PurchaseOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseFinancialDto;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrder;

/**
 * 折扣预警Service
 * @author Michael
 * @version 2015-08-24
 */
@Component
@Transactional(readOnly = true)
public class LcPurchaseFinancialService extends BaseService {

	@Autowired
	private LcPurchaseFinancialDao  purchaseFinancialDao;
	
	public List<LcPurchaseFinancialDto> findAll(){
		return purchaseFinancialDao.findAll();
	}
	
	
	@Transactional(readOnly = false)
	public void save(LcPurchaseFinancialDto purchaseFinancialDto) {
		purchaseFinancialDao.save(purchaseFinancialDto);
	}

	
	
	//支付尾款金额
	public Map<String,Float> getLadingFinancialReport(Float currentyRate,Set<String> keySet){
		Map<String,Float> rs = Maps.newHashMap();
		String sql ="SELECT DATE_FORMAT(dd.sure_date,'%Y%m'),dd.supplier_id,dd.productName,SUM(total) FROM (" +
		" SELECT a.`sure_date`,TRUNCATE((CASE  WHEN a.currency_type ='CNY' THEN  b.`payment_amount`/:p1 ELSE  b.`payment_amount` END) ,2) AS total ,"+
		" CASE WHEN c.color_code!='' THEN CONCAT(c.product_name,'_',c.color_code) ELSE c.product_name	 END AS productName,a.supplier_id FROM lc_psi_purchase_payment AS a ,"+
		" lc_psi_purchase_payment_item AS b,lc_psi_lading_bill_item AS c WHERE a.`id`=b.`payment_id` AND b.lading_item_bill_id=c.id AND a.`payment_sta`='2' AND b.`del_flag`='0' AND "+
		" b.`payment_type`='1'"+
		"  )dd GROUP BY dd.productName,DATE_FORMAT(dd.sure_date,'%Y%m'),dd.supplier_id";
		
		List<Object[]> list = this.purchaseFinancialDao.findBySql(sql,new Parameter(currentyRate));
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				String month = obj[0].toString();
				Integer supplierId = Integer.parseInt(obj[1].toString());
				String productNameColor = obj[2].toString();
				Float  payAmount = Float.parseFloat(obj[3].toString());
				String key=month+","+supplierId+","+productNameColor;
				keySet.add(key);
				rs.put(key, payAmount);
			}
		}
		return rs;
	}
	
	/**
	 *部分确认支付订单 
	 */
		public Map<String,Float> getPartsLadingFinancialReport(Float currentyRate,Set<String> keySet){
			Map<String,Float> rs = Maps.newHashMap();
			String sql ="SELECT DATE_FORMAT(dd.create_date,'%Y%m'),dd.supplier_id,dd.productName,SUM(total) FROM  (SELECT a.`create_date`,TRUNCATE((CASE  WHEN a.currency_type ='CNY' THEN  b.`payment_amount`/:p1 ELSE  b.`payment_amount` END) * c.pcent*(a.`real_payment_amount`/a.`payment_amount_total`),2) AS total ," +
			"CASE WHEN c.color_code!='' THEN CONCAT(c.product_name,'_',c.color_code) ELSE c.product_name	 END AS productName,a.supplier_id FROM lc_psi_purchase_payment AS a ," +
			"lc_psi_purchase_payment_item AS b,(SELECT a.id,a.supplier_id,b.`product_name`,b.`color_code`,SUM(b.`quantity_lading`*b.`item_price`)/a.`no_deposit_amount`  AS pcent FROM" +
			" lc_psi_lading_bill AS a ,lc_psi_lading_bill_item AS b WHERE a.`id`=b.`lading_bill_id` AND b.`del_flag`='0' AND a.`bill_sta`<>'2' GROUP BY  a.id,a.supplier_id," +
			"b.`product_name`,b.`color_code` ) AS c WHERE a.`id`=b.`payment_id` AND c.id=b.`lading_bill_id` AND c.supplier_id=a.supplier_id AND a.`payment_sta`='1' AND a.`real_payment_amount`>0 AND " +
			"b.`payment_type`='1')dd GROUP BY dd.productName,DATE_FORMAT(dd.create_date,'%Y%m'),dd.supplier_id";
			List<Object[]> list = this.purchaseFinancialDao.findBySql(sql,new Parameter(currentyRate));
			if(list!=null&&list.size()>0){
				for(Object[] obj:list){
					String month = obj[0].toString();
					Integer supplierId = Integer.parseInt(obj[1].toString());
					String productNameColor = obj[2].toString();
					Float  payAmount = Float.parseFloat(obj[3].toString());
					String key=month+","+supplierId+","+productNameColor;
					keySet.add(key);
					rs.put(key, payAmount);
				}
			}
			return rs;
		}
		
		/**
		 *部分确认支付提单
		 */
		public Map<String,Float> getPartsOrderFinancialReport(Float currentyRate,Set<String> keySet){
			Map<String,Float> rs = Maps.newHashMap();
			String sql ="SELECT DATE_FORMAT(dd.create_date,'%Y%m'),dd.supplier_id,dd.productName,SUM(total) FROM  (SELECT a.`create_date`,TRUNCATE((CASE  WHEN a.currency_type ='CNY' THEN  b.`payment_amount`/:p1 ELSE  b.`payment_amount` END) * c.pcent *(a.`real_payment_amount`/a.`payment_amount_total`),2) AS total ," +
			"CASE WHEN c.color_code!='' THEN CONCAT(c.product_name,'_',c.color_code) ELSE c.product_name	 END AS productName,a.supplier_id FROM lc_psi_purchase_payment AS a ," +
			"lc_psi_purchase_payment_item AS b,(SELECT a.id,a.`supplier_id`,b.`product_name`,b.`color_code`,SUM(b.`quantity_ordered`*b.`item_price`)/a.`order_total` AS pcent FROM " +
			"lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b WHERE a.`id`= b.`purchase_order_id` AND b.`del_flag`='0' AND a.order_sta in ('2','3','4','5') GROUP BY a.id,a.supplier_id," +
			"b.product_name,b.color_code) AS c WHERE a.`id`=b.`payment_id` AND c.id=b.`purchase_order_id` AND c.supplier_id=a.supplier_id AND b.del_flag='0' AND a.`payment_sta`='1' AND a.`real_payment_amount`>0 AND " +
			"b.`payment_type`='0')dd GROUP BY dd.productName,DATE_FORMAT(dd.create_date,'%Y%m'),dd.supplier_id";
			List<Object[]> list = this.purchaseFinancialDao.findBySql(sql,new Parameter(currentyRate));
			if(list!=null&&list.size()>0){
				for(Object[] obj:list){
					String month = obj[0].toString();
					Integer supplierId = Integer.parseInt(obj[1].toString());
					
					String productNameColor = obj[2].toString();
					Float  payAmount = Float.parseFloat(obj[3].toString());
					String key=month+","+supplierId+","+productNameColor;
					keySet.add(key);
					
					rs.put(key, payAmount);
				}
			}
			return rs;
		}
	
	//支付定金金额
	public Map<String,Float> getOrderFinancialReport(Float currentyRate,Set<String> keySet){
		Map<String,Float> rs = Maps.newHashMap();
		String sql ="SELECT DATE_FORMAT(dd.sure_date,'%Y%m'),dd.supplier_id,dd.productName,SUM(total) FROM  (SELECT a.`sure_date`,TRUNCATE((CASE  WHEN a.currency_type ='CNY' THEN  b.`payment_amount`/:p1 ELSE  b.`payment_amount` END) * c.pcent,2) AS total ," +
		"CASE WHEN c.color_code!='' THEN CONCAT(c.product_name,'_',c.color_code) ELSE c.product_name	 END AS productName,a.supplier_id FROM lc_psi_purchase_payment AS a ," +
		"lc_psi_purchase_payment_item AS b,(SELECT a.id,a.`supplier_id`,b.`product_name`,b.`color_code`,SUM(b.`quantity_ordered`*b.`item_price`)/a.`order_total` AS pcent FROM " +
		"lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b WHERE a.`id`= b.`purchase_order_id` AND b.`del_flag`='0' AND a.order_sta in ('2','3','4','5') GROUP BY a.id,a.supplier_id," +
		"b.product_name,b.color_code) AS c WHERE a.`id`=b.`payment_id` AND c.id=b.`purchase_order_id` AND c.supplier_id=a.supplier_id AND b.del_flag='0' AND a.`payment_sta`='2' AND " +
		"b.`payment_type`='0')dd GROUP BY dd.supplier_id,dd.productName,DATE_FORMAT(dd.sure_date,'%Y%m')";
		List<Object[]> list = this.purchaseFinancialDao.findBySql(sql,new Parameter(currentyRate));
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				String month = obj[0].toString();
				Integer supplierId = Integer.parseInt(obj[1].toString());
				String productNameColor = obj[2].toString();
				Float  payAmount = Float.parseFloat(obj[3].toString());
				String key=month+","+supplierId+","+productNameColor;
				keySet.add(key);
				rs.put(key, payAmount);
			}
		}
		return rs;
	}

	//订单金额        注意itemPrice如果为空，取及时的单价
	public Map<String,Float> getPurchaseReport(Float currentyRate,Set<String> keySet){
		Map<String,Float> rs = Maps.newHashMap();
		String sql ="SELECT DATE_FORMAT(a.create_date,'%Y%m'),a.supplier_id,CASE WHEN b.color_code!='' THEN CONCAT(b.product_name,'_',b.color_code) ELSE b.product_name END AS productName,TRUNCATE((CASE  WHEN a.currency_type ='CNY' THEN  SUM(b.`quantity_ordered`*(CASE WHEN b.`item_price` IS NULL THEN (" +
				" SELECT ROUND(MIN(e.`price`)*((100+c.`tax_rate`)/100),2) FROM psi_product_tiered_price AS e,psi_product AS f,psi_supplier AS c WHERE e.`supplier_id`=c.id AND e.`supplier_id`=a.supplier_id AND  e.`product_id`=f.`id` AND  e.`level`<=(CASE WHEN f.`min_order_placed`<500 THEN 500 ELSE f.`min_order_placed`END ) AND e.`product_id`=b.product_id AND e.`color`=b.color_code AND e.`currency_type`=a.currency_type" +
				") ELSE b.`item_price` END))/:p1 ELSE " +
		" SUM(b.quantity_ordered*(CASE WHEN b.`item_price` IS NULL THEN (" +
		" SELECT ROUND(MIN(e.`price`)*((100+c.`tax_rate`)/100),2) FROM psi_product_tiered_price AS e,psi_product AS f,psi_supplier AS c WHERE e.`supplier_id`=c.id AND e.`supplier_id`=a.supplier_id AND  e.`product_id`=f.`id` AND  e.`level`<=(CASE WHEN f.`min_order_placed`<500 THEN 500 ELSE f.`min_order_placed`END ) AND e.`product_id`=b.product_id AND e.`color`=b.color_code AND e.`currency_type`=a.currency_type "+
		" ) ELSE b.`item_price` END) ) END ),2) AS total  " +
		" FROM lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b WHERE a.`id`= b.`purchase_order_id` AND b.`del_flag`='0' " +
		" AND a.order_sta in ('2','3','4','5')  GROUP BY DATE_FORMAT(a.create_date,'%Y%m'),a.supplier_id,b.product_name,b.color_code";
		List<Object[]> list = this.purchaseFinancialDao.findBySql(sql,new Parameter(currentyRate));
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				if(obj[3]!=null){
					String month = obj[0].toString();
					Integer supplierId = Integer.parseInt(obj[1].toString());
					String productNameColor = obj[2].toString();
					Float  payAmount = Float.parseFloat(obj[3].toString());
					String key=month+","+supplierId+","+productNameColor;
					keySet.add(key);
					rs.put(key, payAmount);
				}
			}
		}
		return rs;
	}

	
	
	
	//整合数据组成上面的表
	@Transactional(readOnly = false)
	public void createFinancicalReprotData(){
		//清空原有数据
		String sql="DELETE FROM lc_psi_purchase_financial_report ";
		this.purchaseFinancialDao.updateBySql(sql,null);
		
		
		Float currentyRate = 1f;//理诚的都是CNY
		Set<String> keySet = Sets.newHashSet();
		Map<String,Float> payLading = this.getLadingFinancialReport(currentyRate,keySet);
		Map<String,Float> payOrder = this.getOrderFinancialReport(currentyRate,keySet);
		Map<String,Float> order = this.getPurchaseReport(currentyRate,keySet);
		//部分支付
		Map<String,Float> payLadingParts = this.getPartsLadingFinancialReport(currentyRate,keySet);
		Map<String,Float> payOrderParts = this.getPartsOrderFinancialReport(currentyRate,keySet);
		for(String key:keySet){
			String []  arr = key.split(",");
			LcPurchaseFinancialDto dto = new LcPurchaseFinancialDto();
			if(payLading.containsKey(key)){
				dto.setPayLadingAmount(payLading.get(key));
			}else{
				dto.setPayLadingAmount(0f);
			}
			
			if(payOrder.containsKey(key)){
				dto.setPayOrderAmount(payOrder.get(key));
			}else{
				dto.setPayOrderAmount(0f);
			}
			
			
			if(order.containsKey(key)){
				dto.setOrderAmount(order.get(key));
			}else{
				dto.setOrderAmount(0f);
			}
			
			//部分支付定金(部分确认)
			if(payOrderParts.containsKey(key)){
				dto.setPayOrderAmount(dto.getPayOrderAmount()+payOrderParts.get(key));
			}
			//部分支付尾款(部分确认)
			if(payLadingParts.containsKey(key)){
				dto.setPayLadingAmount(dto.getPayLadingAmount()+payLadingParts.get(key));
			}

			dto.setMonth(arr[0]);
			dto.setSupplierId(Integer.parseInt(arr[1]));
			dto.setProductNameColor(arr[2]);
			dto.setDataDate(new Date());
			purchaseFinancialDao.save(dto);
		}
		
	}
	
	//查询产品供应商
	public Map<String,Set<Integer>>  getProductSupplierMap(){
		Map<String,Set<Integer>>  map = Maps.newHashMap();
		String  sql= "SELECT a.`product_name_color`,a.`supplier_id` FROM lc_psi_purchase_financial_report AS a  GROUP BY a.`supplier_id`,a.`product_name_color`";
		List<Object[]> list  = this.purchaseFinancialDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				Integer supplierId=Integer.parseInt(obj[1].toString());
				String proColor =obj[0].toString();
				Set<Integer>  supplierSet=null;
				if(map.get(proColor)==null){
					supplierSet  = Sets.newHashSet();
				}else{
					supplierSet = map.get(proColor);
				}
				supplierSet.add(supplierId);
				map.put(obj[0].toString(), supplierSet);
			}
		}
		return map;
	}
	
	public List<LcPurchaseFinancialDto> getFinancicalReport(PurchaseOrder order,String startMonthStr,String endMonthStr){
		String whereSql="";
		Parameter para = null;
		List<Object[]> list = null;
		List<LcPurchaseFinancialDto> rsList = Lists.newArrayList();
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
		
		String sql="SELECT a.`month`,SUM(a.`order_amount`) as orderAmont,SUM(a.`pay_order_amount`) as payOrderAmount,SUM(a.`pay_lading_amount`) as payLadingAmount FROM lc_psi_purchase_financial_report AS a WHERE a.month<='"+endMonthStr+"' AND a.month>='"+startMonthStr+"' "+whereSql+" GROUP BY a.month ORDER BY a.`month` DESC ";
		if(para!=null){
			list = this.purchaseFinancialDao.findBySql(sql,para);
		}else{
			list = this.purchaseFinancialDao.findBySql(sql);
		}
		
		String starMonth="";
		String endMonth="";
		for(int i=0;i<list.size();i++){
			Object[] obj = list.get(i);
			if(i==0){
				endMonth=obj[0].toString();
			}
			if(i==(list.size()-1)){
				starMonth=obj[0].toString();
			}
			LcPurchaseFinancialDto dto = new LcPurchaseFinancialDto(obj[0].toString(), null, null, Float.parseFloat(obj[1].toString()), Float.parseFloat(obj[2].toString()), Float.parseFloat(obj[3].toString()), null);
			rsList.add(dto);
		}
		if(!starMonth.equals(endMonth)){
			order.setDelFlag(starMonth+","+endMonth);
		}else{
			order.setDelFlag(starMonth);
		}
		
		return rsList;
	}
	
	public List<LcPurchaseFinancialDto> getFinancicalReport(LcPurchaseOrder order,String startMonthStr,String endMonthStr){
		String whereSql="";
		Parameter para = null;
		List<Object[]> list = null;
		List<LcPurchaseFinancialDto> rsList = Lists.newArrayList();
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
		
		String sql="SELECT a.`month`,SUM(a.`order_amount`) as orderAmont,SUM(a.`pay_order_amount`) as payOrderAmount,SUM(a.`pay_lading_amount`) as payLadingAmount FROM lc_psi_purchase_financial_report AS a WHERE a.month<='"+endMonthStr+"' AND a.month>='"+startMonthStr+"' "+whereSql+" GROUP BY a.month ORDER BY a.`month` DESC ";
		if(para!=null){
			list = this.purchaseFinancialDao.findBySql(sql,para);
		}else{
			list = this.purchaseFinancialDao.findBySql(sql);
		}
		
		String starMonth="";
		String endMonth="";
		for(int i=0;i<list.size();i++){
			Object[] obj = list.get(i);
			if(i==0){
				endMonth=obj[0].toString();
			}
			if(i==(list.size()-1)){
				starMonth=obj[0].toString();
			}
			LcPurchaseFinancialDto dto = new LcPurchaseFinancialDto(obj[0].toString(), null, null, Float.parseFloat(obj[1].toString()), Float.parseFloat(obj[2].toString()), Float.parseFloat(obj[3].toString()), null);
			rsList.add(dto);
		}
		if(!starMonth.equals(endMonth)){
			order.setDelFlag(starMonth+","+endMonth);
		}else{
			order.setDelFlag(starMonth);
		}
		
		return rsList;
	}
	
	public Map<Integer,Map<String,Float>> getFinancicalReportExport(PurchaseOrder order,String startMonthStr,String endMonthStr){
		Map<Integer,Map<String,Float>> resMap = Maps.newHashMap();
		String whereSql="";
		Parameter para = null;
		List<Object[]> list = null;
		if(StringUtils.isNotEmpty(order.getOrderNo())){
			whereSql+="AND a.`product_name_color`=:p1 ";
			para = new Parameter(order.getOrderNo());
		}
		String sql="SELECT supplier_id,a.`month`,SUM(a.`order_amount`) as orderAmont FROM lc_psi_purchase_financial_report AS a WHERE a.month<='"+endMonthStr+"' AND a.month>='"+startMonthStr+"' "+whereSql+" GROUP BY a.month,supplier_id DESC ";
		if(para!=null){
			list = this.purchaseFinancialDao.findBySql(sql,para);
		}else{
			list = this.purchaseFinancialDao.findBySql(sql);
		}
		Map<String,Float> innerMap = null;
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				Integer supplierId=Integer.parseInt(obj[0].toString());
				if(resMap.get(supplierId)==null){
					innerMap=Maps.newHashMap();
				}else{
					innerMap=resMap.get(supplierId);
				}
				String month=obj[1].toString();
				Float  orderAmount =Float.parseFloat(obj[2].toString());
				innerMap.put(month, orderAmount);
				resMap.put(supplierId, innerMap);
			}
		}
		return resMap;
	}
	
	
	public Map<Integer,Map<String,Float>> getFinancicalReportExport(LcPurchaseOrder order,String startMonthStr,String endMonthStr){
		Map<Integer,Map<String,Float>> resMap = Maps.newHashMap();
		String whereSql="";
		Parameter para = null;
		List<Object[]> list = null;
		if(StringUtils.isNotEmpty(order.getOrderNo())){
			whereSql+="AND a.`product_name_color`=:p1 ";
			para = new Parameter(order.getOrderNo());
		}
		String sql="SELECT supplier_id,a.`month`,SUM(a.`order_amount`) as orderAmont FROM lc_psi_purchase_financial_report AS a WHERE a.month<='"+endMonthStr+"' AND a.month>='"+startMonthStr+"' "+whereSql+" GROUP BY a.month,supplier_id DESC ";
		if(para!=null){
			list = this.purchaseFinancialDao.findBySql(sql,para);
		}else{
			list = this.purchaseFinancialDao.findBySql(sql);
		}
		Map<String,Float> innerMap = null;
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				Integer supplierId=Integer.parseInt(obj[0].toString());
				if(resMap.get(supplierId)==null){
					innerMap=Maps.newHashMap();
				}else{
					innerMap=resMap.get(supplierId);
				}
				String month=obj[1].toString();
				Float  orderAmount =Float.parseFloat(obj[2].toString());
				innerMap.put(month, orderAmount);
				resMap.put(supplierId, innerMap);
			}
		}
		return resMap;
	}
	
}
