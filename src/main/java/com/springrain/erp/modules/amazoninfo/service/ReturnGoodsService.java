/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.ReturnGoodsDao;
import com.springrain.erp.modules.amazoninfo.entity.ReturnGoods;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;

/**
 * 退货信息Service
 * @author Tim
 * @version 2014-12-29
 */
@Component
@Transactional(readOnly = true)
public class ReturnGoodsService extends BaseService {

	@Autowired
	private ReturnGoodsDao returnGoodsDao;
	
	@Autowired
	private AmazonOrderService orderService;
	
	@Autowired
	private AmazonProductService amazonProductService;
	
	@Autowired
	private SaleProfitService saleProfitService;
	
	public ReturnGoods get(String id) {
		return returnGoodsDao.get(id);
	}
	
	public List<ReturnGoods> getReturnGoods(String orderId){
		List<ReturnGoods> goodsList=Lists.newArrayList();
		String sql="SELECT sku,ASIN,country,quantity,reason FROM amazoninfo_return_goods WHERE order_id=:p1 ";
		List<Object[]> list=returnGoodsDao.findBySql(sql,new Parameter(orderId));
		for (Object[] obj : list) {
			ReturnGoods goods=new ReturnGoods();
			goods.setSku(obj[0]==null?"":obj[0].toString());
			goods.setAsin(obj[1]==null?"":obj[1].toString());
			goods.setCountry(obj[2]==null?"":obj[2].toString());
			goods.setQuantity(Integer.parseInt(obj[3].toString()));
			goods.setReason(obj[4]==null?"":obj[4].toString());
			if(StringUtils.isNotBlank(goods.getAsin())&&StringUtils.isNotBlank(goods.getCountry())){
				goods.setProductName(amazonProductService.findProductName(goods.getAsin(),goods.getCountry()));
			}
			goodsList.add(goods);
		}
		return goodsList;
	}
	
	public Map<String,ReturnGoods> getReturnGoodsByOrderId(String orderId){
		Map<String,ReturnGoods> map=Maps.newHashMap();
		String sql="SELECT sku,ASIN,country,sum(quantity),reason FROM amazoninfo_return_goods WHERE order_id=:p1 group by sku,ASIN,country,reason ";
		List<Object[]> list=returnGoodsDao.findBySql(sql,new Parameter(orderId));
		for (Object[] obj : list) {
			ReturnGoods goods=new ReturnGoods();
			goods.setSku(obj[0]==null?"":obj[0].toString());
			goods.setAsin(obj[1]==null?"":obj[1].toString());
			goods.setCountry(obj[2]==null?"":obj[2].toString());
			goods.setQuantity(Integer.parseInt(obj[3].toString()));
			goods.setReason(obj[4]==null?"":obj[4].toString());
			if(StringUtils.isNotBlank(goods.getAsin())&&StringUtils.isNotBlank(goods.getCountry())){
				goods.setProductName(amazonProductService.findProductName(goods.getAsin(),goods.getCountry()));
			}
			map.put(goods.getSku(), goods);
		}
		return map;
	}
	
	public Page<ReturnGoods> find(Page<ReturnGoods> page, ReturnGoods returnGoods) {
		DetachedCriteria dc = returnGoodsDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(returnGoods.getSku())){
			dc.add(Restrictions.or(Restrictions.like("orderId", "%"+returnGoods.getSku()+"%"),Restrictions.like("sku", "%"+returnGoods.getSku()+"%"),Restrictions.like("asin", "%"+returnGoods.getSku()+"%")));
		}
		Date endDate = returnGoods.getReturnDate();
		if (endDate==null){
			endDate = new Date();
			endDate.setHours(0);
			endDate.setMinutes(0);
			endDate.setSeconds(0);
			returnGoods.setReturnDate(endDate);
		}
		dc.add(Restrictions.le("returnDate",DateUtils.addDays(endDate,1)));
		
		if (returnGoods.getStartDate()==null){
			returnGoods.setStartDate(DateUtils.addDays(endDate,-7));
		}
		dc.add(Restrictions.ge("returnDate",returnGoods.getStartDate()));
		
		if(StringUtils.isNotEmpty(returnGoods.getReason())){
			dc.add(Restrictions.eq("reason",returnGoods.getReason()));
		}
		
		if(StringUtils.isNotEmpty(returnGoods.getCustomerComment())){
			dc.add(Restrictions.neOrIsNotNull("customerComment", ""));
		}
		
		if(StringUtils.isNotEmpty(returnGoods.getDisposition())){
			dc.add(Restrictions.eq("disposition",returnGoods.getDisposition()));
		}
		
		if (StringUtils.isNotEmpty(returnGoods.getCountry())){
			dc.add(Restrictions.eq("country",returnGoods.getCountry()));
		}else{
			returnGoods.setCountry("de");
			dc.add(Restrictions.eq("country","de"));
		}
		return returnGoodsDao.find(page, dc);
	}
	//国家-确认销量-退货量-差评数量-订单数量-退货率-差评率
	public  Map<String,Object[]> findReturnCommentInfo(Date start,Date end,String productNameWithColor){
		/*String sqlString=" select c.country,sum(sure_sales_volume),sum(return_amount) return_amount,sum(comment_amount) comment_amount,sum(order_amount), " +
				" TRUNCATE(sum(return_amount)*100/sum(sure_sales_volume),2),TRUNCATE(sum(comment_amount)*100/sum(order_amount),2)  from ("+
				"select c.`country` country,0 sure_sales_volume,sum(return_amount) return_amount,sum(comment_amount) comment_amount,sum(order_amount) order_amount from amazoninfo_return_comment c"+
				" WHERE c.purchase_date>=:p1 AND c.purchase_date<=:p2 " +
				" and CONCAT(c.`product_name`,CASE WHEN c.`color`!='' THEN CONCAT ('_',c.`color`) ELSE '' END)=:p3 "+
				" GROUP BY c.`country` union  "+
				"select o.country country,sum(sure_sales_volume) sure_sales_volume,0 return_amount,0 comment_amount,0 order_amount from amazoninfo_sale_report o "+
				" WHERE  o.order_type='1' and o.date>=:p1 AND o.date<=:p2 " +
				" and CONCAT(o.`product_name`,CASE WHEN o.`color`!='' THEN CONCAT ('_',o.`color`) ELSE '' END)=:p3 "+
				" GROUP by o.country ) c group by c.country";*/
		String sql="select o.country country,sum(sure_sales_volume) sure_sales_volume from amazoninfo_sale_report o "+
				" WHERE  o.order_type='1' and o.date>=:p1 AND o.date<=:p2 " +
				" and CONCAT(o.`product_name`,CASE WHEN o.`color`!='' THEN CONCAT ('_',o.`color`) ELSE '' END)=:p3 "+
				" GROUP by o.country ";
		List<Object[]> salesList=returnGoodsDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyy-MM-dd").format(start),new SimpleDateFormat("yyyy-MM-dd").format(end),productNameWithColor));
		Map<String,Integer> salesMap=Maps.newHashMap();
		for (Object[] objects: salesList) {
			String country=objects[0].toString();
			Integer sure_sales_volume=Integer.parseInt(objects[1]==null?"0":objects[1].toString());
			salesMap.put(country,sure_sales_volume);
			/*if("uk,it,es,de,fr".contains(country)){
				Integer quantity=(salesMap.get("eu")==null?0:salesMap.get("eu"));
				salesMap.put("eu",sure_sales_volume+quantity);
			}
			Integer totalQuantity=(salesMap.get("total")==null?0:salesMap.get("total"));
			salesMap.put("total",sure_sales_volume+totalQuantity);*/
		}
		
		
		String sqlString="select c.`country` country,0 sure_sales_volume,sum(return_amount) return_amount,sum(comment_amount) comment_amount,sum(order_amount) order_amount,0 r,0 s from amazoninfo_return_comment c"+
				" WHERE c.purchase_date>=:p1 AND c.purchase_date<=:p2 " +
				" and CONCAT(c.`product_name`,CASE WHEN c.`color`!='' THEN CONCAT ('_',c.`color`) ELSE '' END)=:p3 "+
				" GROUP BY c.`country` ";
		List<Object[]> list=returnGoodsDao.findBySql(sqlString,new Parameter(new SimpleDateFormat("yyyy-MM-dd").format(start),new SimpleDateFormat("yyyy-MM-dd").format(end),productNameWithColor));
		Map<String,Object[]> map=new HashMap<String,Object[]>();
		
		for (Object[] objects : list) {
			String country=objects[0].toString();
			//Integer sure_sales_volume=Integer.parseInt(objects[1]==null?"0":objects[1].toString());
			Integer sure_sales_volume=(salesMap.get(country)==null?0:salesMap.get(country));
			Integer return_amount=Integer.parseInt(objects[2]==null?"0":objects[2].toString());
			Integer comment_amount=Integer.parseInt(objects[3]==null?"0":objects[3].toString());
			Integer order_amount=Integer.parseInt(objects[4]==null?"0":objects[4].toString());
			//TRUNCATE(sum(return_amount)*100/sum(sure_sales_volume),2),
			//TRUNCATE(sum(comment_amount)*100/sum(order_amount),2)
			
			if(sure_sales_volume!=0){
				objects[5]=new BigDecimal(return_amount*100f/sure_sales_volume).setScale(2,4).floatValue();
			}
			
			if(order_amount!=0){
				objects[6]=new BigDecimal(comment_amount*100f/order_amount).setScale(2,4).floatValue();
			}
			
			
			objects[1]=sure_sales_volume;
			map.put(country, objects);
			if("uk,it,es,de,fr".contains(country)){
				Object[] obj=map.get("eu");
				if(obj==null){
					obj=new Object[7];
					map.put("eu", obj);
				}
				obj[0]="eu";
				obj[1]=Integer.parseInt(obj[1]==null?"0":obj[1].toString())+sure_sales_volume;
				obj[2]=Integer.parseInt(obj[2]==null?"0":obj[2].toString())+return_amount;
				obj[3]=Integer.parseInt(obj[3]==null?"0":obj[3].toString())+comment_amount;
				obj[4]=Integer.parseInt(obj[4]==null?"0":obj[4].toString())+order_amount;
			}
			Object[] total=map.get("total");
			if(total==null){
				total=new Object[7];
				map.put("total", total);
			}
			total[0]="total";
			total[1]=Integer.parseInt(total[1]==null?"0":total[1].toString())+sure_sales_volume;
			total[2]=Integer.parseInt(total[2]==null?"0":total[2].toString())+return_amount;
			total[3]=Integer.parseInt(total[3]==null?"0":total[3].toString())+comment_amount;
			total[4]=Integer.parseInt(total[4]==null?"0":total[4].toString())+order_amount;
		}
		
		
		
		
		if(map.size()>0){
			Object[] returnRateEu=map.get("eu");
			if(returnRateEu!=null){
				if(returnRateEu[1]!=null&&returnRateEu[2]!=null&&Integer.parseInt(returnRateEu[1].toString())!=0){
				   returnRateEu[5]=new BigDecimal(Float.parseFloat(returnRateEu[2].toString())*100/Integer.parseInt(returnRateEu[1].toString())).setScale(2,4).floatValue();
				}else{
				   returnRateEu[5]=0;
				}
				if(returnRateEu[3]!=null&&returnRateEu[4]!=null&&Integer.parseInt(returnRateEu[4].toString())!=0){
					returnRateEu[6]=new BigDecimal(Float.parseFloat(returnRateEu[3].toString())*100/Integer.parseInt(returnRateEu[4].toString())).setScale(2,4).floatValue();
				}else{
				   returnRateEu[6]=0;
				}
			}
			Object[] returnRateTotal=map.get("total");
			if(returnRateTotal!=null){
				if(returnRateTotal[1]!=null&&returnRateTotal[2]!=null&&Integer.parseInt(returnRateTotal[1].toString())!=0){
					returnRateTotal[5]=new BigDecimal(Float.parseFloat(returnRateTotal[2].toString())*100/Integer.parseInt(returnRateTotal[1].toString())).setScale(2,4).floatValue();
				}else{
					returnRateTotal[5]=0;
				}
				if(returnRateTotal[3]!=null&&returnRateTotal[4]!=null&&Integer.parseInt(returnRateTotal[4].toString())!=0){
					returnRateTotal[6]=new BigDecimal(Float.parseFloat(returnRateTotal[3].toString())*100/Integer.parseInt(returnRateTotal[4].toString())).setScale(2,4).floatValue();
				}else{
					returnRateTotal[6]=0;
				}
			}
		}
		return map;
	}
	
	public  Map<String,Object[]> findAllReturnCommentInfo(ReturnGoods returnGoods){
		String sqlString="";
		List<Object[]> list=Lists.newArrayList();
		if(StringUtils.isBlank(returnGoods.getCountry())){
			sqlString="select CONCAT(c.`product_name`,CASE WHEN c.`color`!='' THEN CONCAT ('_',c.`color`) ELSE '' END) name,sum(return_amount),sum(comment_amount),sum(order_amount) from amazoninfo_return_comment c"+
					" WHERE c.purchase_date>=:p1 AND c.purchase_date<:p2 GROUP BY name ";
			list=returnGoodsDao.findBySql(sqlString,new Parameter(returnGoods.getStartDate(),DateUtils.addDays(returnGoods.getReturnDate(),1)));
		}else{
			sqlString="select CONCAT(c.`product_name`,CASE WHEN c.`color`!='' THEN CONCAT ('_',c.`color`) ELSE '' END) name,sum(return_amount),sum(comment_amount),sum(order_amount),c.country from amazoninfo_return_comment c"+
				" WHERE c.purchase_date>=:p1  AND c.purchase_date<:p2 and c.`country`=:p3 "+
				" GROUP BY c.`country`,name ";
			list=returnGoodsDao.findBySql(sqlString,new Parameter(returnGoods.getStartDate(),DateUtils.addDays(returnGoods.getReturnDate(),1),returnGoods.getCountry()));
		}
		Map<String,Object[]> map=new HashMap<String,Object[]>();
		for (Object[] objects : list) {
			map.put((String) objects[0], objects);
		}
    	return map;
    }
	
	
	public  Map<String,Map<String,Object[]>> findAllReturnCommentInfo(ReturnGoods returnGoods,String type){
		String sqlString="";
		String typeSql="";
		if("1".equals(type)){
			typeSql="'%Y%m'";
		}else{
			typeSql="'%x%v'";
		}
		List<Object[]> list=Lists.newArrayList();
		if(StringUtils.isBlank(returnGoods.getCountry())){
			sqlString="select CONCAT(c.`product_name`,CASE WHEN c.`color`!='' THEN CONCAT ('_',c.`color`) ELSE '' END) name,DATE_FORMAT(c.purchase_date,"+typeSql+") dates,sum(return_amount),sum(comment_amount),sum(order_amount) from amazoninfo_return_comment c"+
					" WHERE c.purchase_date>=:p1 AND c.purchase_date<:p2 " +
		 			" GROUP BY dates,name ";
			list=returnGoodsDao.findBySql(sqlString,new Parameter(returnGoods.getStartDate(),DateUtils.addDays(returnGoods.getReturnDate(),1)));
		}else{
			sqlString="select CONCAT(c.`product_name`,CASE WHEN c.`color`!='' THEN CONCAT ('_',c.`color`) ELSE '' END) name,DATE_FORMAT(c.purchase_date,"+typeSql+") dates,sum(return_amount),sum(comment_amount),sum(order_amount),c.country from amazoninfo_return_comment c"+
				" WHERE c.purchase_date>=:p1 AND c.purchase_date<:p2 and c.`country`=:p3 "+
				" GROUP BY dates,c.`country`,name ";
			list=returnGoodsDao.findBySql(sqlString,new Parameter(returnGoods.getStartDate(),DateUtils.addDays(returnGoods.getReturnDate(),1),returnGoods.getCountry()));
		}
		Map<String,Map<String,Object[]>> map=new HashMap<String,Map<String,Object[]>>();
		for (Object[] objects : list) {
			Map<String,Object[]> dates=map.get(objects[0].toString());
			if(dates==null){
				dates=Maps.newLinkedHashMap();
				map.put(objects[0].toString(),dates);
			}
			String date = objects[1].toString(); 
			if(!"1".equals(type)){
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
			}
			dates.put(date,objects);
		}
    	return map;
    }
	
	public  Map<String,Map<String,Object[]>> findAllOrderInfo(ReturnGoods returnGoods,String type){
		String sqlString="";
		String typeSql="";
		if("1".equals(type)){
			typeSql="'%Y%m'";
		}else{
			typeSql="'%x%v'";
		}
		List<Object[]> list=Lists.newArrayList();
		if(StringUtils.isBlank(returnGoods.getCountry())){
			 sqlString="select CONCAT(o.`product_name`,CASE WHEN o.`color`!='' THEN CONCAT ('_',o.`color`) ELSE '' END) name,DATE_FORMAT(o.date,"+typeSql+") dates,sum(sure_sales_volume) from amazoninfo_sale_report o "+
						" WHERE  o.order_type='1' and o.date>=:p1 AND o.date<:p2 " +
						" GROUP by dates,name  ";
			 list=returnGoodsDao.findBySql(sqlString,new Parameter(returnGoods.getStartDate(),DateUtils.addDays(returnGoods.getReturnDate(),1)));
		}else{
		    sqlString="select CONCAT(o.`product_name`,CASE WHEN o.`color`!='' THEN CONCAT ('_',o.`color`) ELSE '' END) name,DATE_FORMAT(o.date,"+typeSql+") dates,sum(sure_sales_volume),o.country from amazoninfo_sale_report o "+
				" WHERE o.order_type='1' and o.date>=:p1 AND o.date<:p2 and o.country =:p3 "+
				" GROUP by dates,o.country,name  ";
		    list=returnGoodsDao.findBySql(sqlString,new Parameter(returnGoods.getStartDate(),DateUtils.addDays(returnGoods.getReturnDate(),1),returnGoods.getCountry()));
		} 
		
		Map<String,Map<String,Object[]>> map=new HashMap<String,Map<String,Object[]>>();
		for (Object[] objects : list) {
			if(objects[0]!=null){
				Map<String,Object[]> dates=map.get(objects[0].toString());
				if(dates==null){
					dates=Maps.newLinkedHashMap();
					map.put(objects[0].toString(),dates);
				}
				String date = objects[1].toString(); 
				if(!"1".equals(type)){
					Integer i = Integer.parseInt(date.substring(4));
					if(i==53){
						Integer year = Integer.parseInt(date.substring(0,4));
						date =  (year+1)+"01";
					}else if (date.contains("2016")){
						i = i+1;
						date = "2016"+(i<10?("0"+i):i);
					}
				}
				dates.put(date,objects);
			}
		}
    	return map;
    }
	
	public  Map<String,Object[]> findAllOrderInfo(ReturnGoods returnGoods){
		String sqlString="";
		List<Object[]> list=Lists.newArrayList();
		if(StringUtils.isBlank(returnGoods.getCountry())){
			 sqlString="select CONCAT(o.`product_name`,CASE WHEN o.`color`!='' THEN CONCAT ('_',o.`color`) ELSE '' END) name,sum(sure_sales_volume) from amazoninfo_sale_report o "+
						" WHERE o.order_type='1' and o.date>=:p1 AND o.date<:p2 GROUP by name  ";
			 list=returnGoodsDao.findBySql(sqlString,new Parameter(returnGoods.getStartDate(),DateUtils.addDays(returnGoods.getReturnDate(),1)));
		}else{
		    sqlString="select CONCAT(o.`product_name`,CASE WHEN o.`color`!='' THEN CONCAT ('_',o.`color`) ELSE '' END) name,sum(sure_sales_volume),o.country from amazoninfo_sale_report o "+
				" WHERE o.order_type='1' and o.date>=:p1 AND o.date<:p2 and o.country =:p3 "+
				" GROUP by o.country,name  ";
		    list=returnGoodsDao.findBySql(sqlString,new Parameter(returnGoods.getStartDate(),DateUtils.addDays(returnGoods.getReturnDate(),1),returnGoods.getCountry()));
		} 
		Map<String,Object[]> map=new HashMap<String,Object[]>();
		for (Object[] objects : list) {
			map.put((String) objects[0], objects);
		}
    	return map;
    }
	
	public  List<Object[]> findReturnInfoByProduct(ReturnGoods returnGoods){
		if(StringUtils.isBlank(returnGoods.getCountry())){
			String sqlString="SELECT o.`orderId` orderId,o.productName productName,SUM(o.`saleQuantity`) saleQuantity,SUM(g.`quantity`) quantity,g.reason " +
					" FROM  (SELECT o.`amazon_order_id` orderId,CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END) productName,SUM(t.`quantity_ordered`) saleQuantity " +
					" FROM amazoninfo_order o JOIN  amazoninfo_orderitem t ON o.id=t.`order_id` " +
					" WHERE o.`order_status` in ('Shipped','UnShipped') AND DATE_FORMAT(o.`purchase_date`,'%Y-%m-%d')>=:p1  AND DATE_FORMAT(o.`purchase_date`,'%Y-%m-%d')<:p2 "+
					" AND CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END)=:p3 " +
					" GROUP BY o.`amazon_order_id`,CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END)) o" +
					" LEFT JOIN (SELECT order_id,g.`quantity`,reason FROM amazoninfo_return_goods g JOIN (SELECT DISTINCT product_name,color,country,ASIN FROM psi_sku WHERE `product_name` NOT LIKE '%other%' AND product_name NOT LIKE '%Old%') s " +
					" ON g.`asin`=s.asin AND g.`country`=s.country AND CONCAT(s.`product_name`,CASE WHEN s.`color`!='' THEN CONCAT ('_',s.`color`) ELSE '' END)=:p4) g ON g.`order_id`=o.`orderId` " +
					" GROUP BY o.`orderId`,o.productName,g.reason ";
			return returnGoodsDao.findBySql(sqlString,new Parameter(returnGoods.getStartDate(),DateUtils.addDays(returnGoods.getReturnDate(), 1),returnGoods.getProductName(),returnGoods.getProductName()));
		}else{
			String sqlString="SELECT o.`orderId` orderId,o.productName productName,SUM(o.`saleQuantity`) saleQuantity,SUM(g.`quantity`) quantity,g.reason " +
					" FROM  (SELECT o.`amazon_order_id` orderId,CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END) productName,SUM(t.`quantity_ordered`) saleQuantity " +
					" FROM amazoninfo_order o JOIN  amazoninfo_orderitem t ON o.id=t.`order_id` " +
					" WHERE o.`order_status` in ('Shipped','UnShipped') AND DATE_FORMAT(o.`purchase_date`,'%Y-%m-%d')>=:p1 AND DATE_FORMAT(o.`purchase_date`,'%Y-%m-%d')<:p2 " +
					" AND SUBSTRING_INDEX(o.`sales_channel`,'.',-1)=:p3 AND CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END)=:p4 " +
					" GROUP BY o.`amazon_order_id`,CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END)) o" +
					" LEFT JOIN (SELECT order_id,g.`quantity`,reason FROM amazoninfo_return_goods g JOIN (SELECT DISTINCT product_name,color,country,ASIN FROM psi_sku WHERE `product_name` NOT LIKE '%other%' AND product_name NOT LIKE '%Old%') s " +
					" ON g.`asin`=s.asin AND g.`country`=s.country AND CONCAT(s.`product_name`,CASE WHEN s.`color`!='' THEN CONCAT ('_',s.`color`) ELSE '' END)=:p5 ) g ON g.`order_id`=o.`orderId` " +
					" GROUP BY o.`orderId`,o.productName,g.reason ";
			return returnGoodsDao.findBySql(sqlString,new Parameter(returnGoods.getStartDate(),DateUtils.addDays(returnGoods.getReturnDate(), 1),returnGoods.getCountry(),returnGoods.getProductName(),returnGoods.getProductName()));
		}
		
		
    }
	
	public  Map<String,String> findCommentAmount(ReturnGoods returnGoods){
		List<String> list=null;
		if(StringUtils.isBlank(returnGoods.getCountry())){
			   String sqlString="SELECT o.`amazon_order_id` FROM amazoninfo_order o  "+
					    		" JOIN amazoninfo_orderitem t ON o.id=t.`order_id`    "+
					    		" WHERE  o.`purchase_date`>=:p1 AND o.`purchase_date`<:p2 AND  o.`order_status`='Shipped'    "+
					    		" AND CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END)=:p3  ";
			   list=returnGoodsDao.findBySql(sqlString,new Parameter(returnGoods.getStartDate(),DateUtils.addDays(returnGoods.getReturnDate(),1),returnGoods.getProductName()));
		}else{
			   String sqlString="SELECT o.`amazon_order_id` FROM amazoninfo_order o  "+
			    		" JOIN amazoninfo_orderitem t ON o.id=t.`order_id`    "+
			    		" WHERE  o.`purchase_date`>=:p1 AND o.`purchase_date`<:p2 AND  o.`order_status`='Shipped'    "+
			    		" AND CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END)=:p3 and o.`sales_channel` like :p4  ";
	           list=returnGoodsDao.findBySql(sqlString,new Parameter(returnGoods.getStartDate(),DateUtils.addDays(returnGoods.getReturnDate(),1),returnGoods.getProductName(),"%"+returnGoods.getCountry()));
		}
	    
		String orderId="";
		StringBuffer buf= new StringBuffer();
		for (String id : list) {
			buf.append("<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazoninfo/order/form?amazonOrderId="+id+"'>"+id+"</a>, ");
		}
		orderId=buf.toString();
		Map<String,String> map=new HashMap<String,String>();
		map.put("orderId", orderId);
		map.put("size", list.size()+"");
		return map;
		
    }
	
	public  Map<String,String> findCommentEventAmount(ReturnGoods returnGoods){
		List<Object[]> list=null;
		if(StringUtils.isBlank(returnGoods.getCountry())){
			String sqlString="SELECT c.id,c.order_id FROM (SELECT a.ID,a.`remarks`,a.country,SUBSTRING_INDEX(a.`invoice_number`,',',1)  order_id" +
					" FROM custom_event_manager a  " +
					" WHERE a.type='1' AND a.`invoice_number`!='not find oderID' AND a.`invoice_number` IS NOT NULL) c " +
					" JOIN  (SELECT DISTINCT product_name,color,country,ASIN FROM psi_sku WHERE `product_name` NOT LIKE '%other%' AND product_name NOT LIKE '%Old%') t ON t.`asin`=c.`remarks` AND t.`country`=c.country" +
					" JOIN amazoninfo_order o ON o.`amazon_order_id` = c.`order_id`" +
					" WHERE o.`order_status`='Shipped' and o.`purchase_date`>=:p1 AND o.`purchase_date`<:p2 "+
					"  AND CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END)=:p3";
			 list=returnGoodsDao.findBySql(sqlString,new Parameter(returnGoods.getStartDate(),DateUtils.addDays(returnGoods.getReturnDate(),1),returnGoods.getProductName()));
		}else{
			String sqlString="SELECT c.id,c.order_id FROM (SELECT a.ID,a.`remarks`,a.country,SUBSTRING_INDEX(a.`invoice_number`,',',1)  order_id" +
					" FROM custom_event_manager a  " +
					" WHERE a.type='1' AND a.`invoice_number`!='not find oderID' AND a.`invoice_number` IS NOT NULL) c " +
					" JOIN  (SELECT DISTINCT product_name,color,country,ASIN FROM psi_sku WHERE `product_name` NOT LIKE '%other%' AND product_name NOT LIKE '%Old%') t ON t.`asin`=c.`remarks` AND t.`country`=c.country" +
					" JOIN amazoninfo_order o ON o.`amazon_order_id` = c.`order_id`" +
					" WHERE o.`order_status`='Shipped' and o.`purchase_date`>=:p1 AND o.`purchase_date`<:p2 "+
					"  AND CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END)=:p3 and o.`sales_channel` like :p4 ";
			 list=returnGoodsDao.findBySql(sqlString,new Parameter(returnGoods.getStartDate(),DateUtils.addDays(returnGoods.getReturnDate(),1),returnGoods.getProductName(),"%"+returnGoods.getCountry()));
		}
		String orderId="";
		StringBuffer buf= new StringBuffer();
		for (Object[] obj : list) {
			//orderId +=("<a href=/inateck-erp/a/custom/event/form?id="+Integer.parseInt(obj[0].toString())+"'>"+obj[1]+"</a>, ");
			buf.append("<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazoninfo/order/form?amazonOrderId="+obj[1]+"'>"+obj[1]+"</a>, ");
		}
		orderId=buf.toString();
		Map<String,String> map=new HashMap<String,String>();
		map.put("orderId", orderId);
		map.put("size", list.size()+"");
		return map;
		
    }
	
	public Page<ReturnGoods> findAllCountry(Page<ReturnGoods> page, ReturnGoods returnGoods) {
		DetachedCriteria dc = returnGoodsDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(returnGoods.getSku())){
			dc.add(Restrictions.or(Restrictions.like("orderId", "%"+returnGoods.getSku()+"%"),Restrictions.like("sku", "%"+returnGoods.getSku()+"%"),Restrictions.like("asin", "%"+returnGoods.getSku()+"%")));
		}
		Date endDate = returnGoods.getReturnDate();
		if (endDate==null){
			endDate = new Date();
			endDate.setHours(0);
			endDate.setMinutes(0);
			endDate.setSeconds(0);
			returnGoods.setReturnDate(endDate);
		}
		dc.add(Restrictions.le("returnDate",DateUtils.addDays(endDate,1)));
		
		if (returnGoods.getStartDate()==null){
			returnGoods.setStartDate(DateUtils.addDays(endDate,-7));
		}
		dc.add(Restrictions.ge("returnDate",returnGoods.getStartDate()));
		
		if(StringUtils.isNotEmpty(returnGoods.getReason())){
			dc.add(Restrictions.eq("reason",returnGoods.getReason()));
		}
		return returnGoodsDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(ReturnGoods returnGoods) {
		returnGoodsDao.save(returnGoods);
	}
	
	public boolean isExist(ReturnGoods returnGoods){
		//过滤
		DetachedCriteria dc = returnGoodsDao.createDetachedCriteria();
		dc.add(Restrictions.eq("sku",returnGoods.getSku()));
		dc.add(Restrictions.eq("orderId",returnGoods.getOrderId()));
		dc.add(Restrictions.eq("returnDate",returnGoods.getReturnDate()));
		return returnGoodsDao.count(dc)>0;
	}
	
	public Timestamp getMaxDate(Set<String> countrys,String accountName){
		String sql = "select max(return_date) from amazoninfo_return_goods where country in :p1";
		List<Object> rs = null;
		if(StringUtils.isNotBlank(accountName)&&accountName.contains("_")){
			sql+=" and account_name like :p2 ";
			rs = returnGoodsDao.findBySql(sql, new Parameter(countrys,accountName.split("_")[0]+"%"));
		}else{
			rs = returnGoodsDao.findBySql(sql, new Parameter(countrys));
		}
		if(rs.size()==1){
			return (Timestamp)rs.get(0);
		}else{
			return null;
		}
	}
	
	public List<String> getReturnGoodsReasons(ReturnGoods returnGoods){
		String sql = "SELECT DISTINCT a.`reason` FROM amazoninfo_return_goods  a WHERE a.`country` = :p1 AND a.`return_date`< :p2 AND a.`return_date` >:p3";
		return returnGoodsDao.findBySql(sql, new Parameter(returnGoods.getCountry(),DateUtils.addDays(returnGoods.getReturnDate(),1),returnGoods.getStartDate()));
	}
	
	public List<String> getReturnGoodsDispositions(ReturnGoods returnGoods){
		String sql = "SELECT DISTINCT a.`disposition` FROM amazoninfo_return_goods  a WHERE a.`country` = :p1 AND a.`return_date`< :p2 AND a.`return_date` >:p3";
		return returnGoodsDao.findBySql(sql, new Parameter(returnGoods.getCountry(),DateUtils.addDays(returnGoods.getReturnDate(),1),returnGoods.getStartDate()));
	}
	
	@Transactional(readOnly = false)
	public void save(List<ReturnGoods> returnGoods) {
		returnGoodsDao.save(returnGoods);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		returnGoodsDao.deleteById(id);
	}
	
	public Map<String,Object[]> getRetrunGoodsOrder(Date start , Date end , String country){
		String sql = "SELECT a.`order_id`,a.`sku`,SUM(a.`quantity`),b.`sales_channel` FROM amazoninfo_return_goods a ,amazoninfo_order b WHERE a.`order_id`= b.`amazon_order_id` AND b.`order_status` IN ('Shipped') AND b.`purchase_date`>:p1 AND b.`purchase_date` < :p2 AND b.`sales_channel` LIKE :p3  GROUP BY a.`order_id`,a.`sku`,b.`sales_channel` order by b.`purchase_date`";
		List<Object[]> list = returnGoodsDao.findBySql(sql, new Parameter(start,DateUtils.addDays(end, 1),"%"+country+"%"));
		Map<String,Object[]> rs = Maps.newLinkedHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(), objects);
		}
		return rs;
	}
	
	
	/**
	 * 统计时间段内的销量
	 * @param start	yyyy-MM-dd格式日期
	 * @param end	yyyy-MM-dd格式日期
	 * @return map [国家[产品 销量]]]
	 */
	public Map<String, Map<String, Integer>> getSalesVolume(String start , String end){
		start = start + " 00:00:00";
		end = end + " 23:59:59";
		Map<String, Map<String, Integer>> rs = Maps.newHashMap();
		String sql = "SELECT o.country,o.`product_name`,o.`color`,SUM(sure_sales_volume) FROM amazoninfo_sale_report o "+
				" WHERE o.order_type='1' AND o.date>=:p1 AND o.date<=:p2 AND o.`product_name` IS NOT NULL "+
				" GROUP BY o.country,o.`product_name`,o.`color`";
		List<Object[]> list = returnGoodsDao.findBySql(sql, new Parameter(start, end));
		for (Object[] objects : list) {
			String country = objects[0].toString();
			String productName = objects[1].toString();
			String color = objects[2]==null?"":objects[2].toString();
			if (StringUtils.isNotEmpty(color)) {
				productName = productName + "_" + color;
			}
			Integer salesVolume = objects[3]==null?0:Integer.parseInt(objects[3].toString());
			Map<String, Integer> salesVolumeMap = rs.get(country);
			if (salesVolumeMap == null) {
				salesVolumeMap = Maps.newHashMap();
				rs.put(country, salesVolumeMap);
			}
			salesVolumeMap.put(productName, salesVolume);
		}
		return rs;
	}
	
	/**
	 * 统计时间段内的退货量
	 * @param start	yyyy-MM-dd格式日期
	 * @param end	yyyy-MM-dd格式日期
	 * @return map [国家[产品[原因  退货数]]] 退货原因中total指总退货数
	 */
	public Map<String, Map<String, Map<String, Integer>>> getRetrunGoods(String start , String end){
		Map<String, Map<String, Map<String, Integer>>> rs = Maps.newHashMap();
		Map<String, String> skuNameMap = saleProfitService.findSkuNames();
		//按退货时间算退货量
		String sql = "SELECT t.`country`,SUM(t.`quantity`),t.`sku`,t.`reason` FROM `amazoninfo_return_goods` t WHERE "+
				" DATE_FORMAT(t.`return_date`,'%Y-%m-%d')>=:p1 AND DATE_FORMAT(t.`return_date`,'%Y-%m-%d')<=:p2 "+
				" GROUP BY t.`sku`,t.`reason`,t.`country`";
		//按订单时间算退货量
		/*String sql = "SELECT t.`country`,SUM(t.`quantity`),t.`sku`,t.`reason` FROM `amazoninfo_return_goods` t,amazoninfo_order o " +
				" WHERE t.`order_id`=o.`amazon_order_id` AND "+
				" DATE_FORMAT(o.`purchase_date`,'%Y%m%d')>=:p1 AND DATE_FORMAT(o.`purchase_date`,'%Y%m%d')<=:p2 "+ 
				" GROUP BY t.`sku`,t.`reason`,t.`country`";*/
		List<Object[]> list = returnGoodsDao.findBySql(sql, new Parameter(start, end));
		for (Object[] objects : list) {
			String country = objects[0].toString();
			Integer quantity = objects[1]==null?0:Integer.parseInt(objects[1].toString());
			String sku = objects[2].toString();
			String reason = objects[3].toString();
			String productName = skuNameMap.get(sku);
			if (StringUtils.isEmpty(productName)) {
				continue;
			}
			Map<String, Map<String, Integer>> countryMap = rs.get(country);
			if (countryMap == null) {
				countryMap = Maps.newHashMap();
				rs.put(country, countryMap);
			}
			Map<String, Integer> productMap = countryMap.get(productName);
			if (productMap == null) {
				productMap = Maps.newHashMap();
				countryMap.put(productName, productMap);
			}
			productMap.put(reason, quantity);
			Integer totalQuantity = productMap.get("total");
			if (totalQuantity == null) {
				productMap.put("total", quantity);
			} else {
				productMap.put("total", totalQuantity + quantity);
			}
		}
		//退款统计(评测、退税以外的情况),数量太少统计到退货的质量问题中QUALITY_UNACCEPTABLE
		sql = "SELECT t.`country`,i.`product_name`,COUNT(i.`id`) FROM `amazoninfo_refund` t, `amazoninfo_refund_item` i"+ 
				" WHERE t.`id`=i.`refund_id` AND t.`refund_state`='1' AND"+
				" i.`remark` NOT LIKE '%测评%' AND i.`remark` NOT LIKE '%review%' AND i.`remark` NOT LIKE '%退税%' AND i.`remark` NOT LIKE '%tax%' AND"+
				" DATE_FORMAT(t.`create_date`,'%Y-%m-%d')>=:p1 AND DATE_FORMAT(t.`create_date`,'%Y-%m-%d')<=:p2"+
				" GROUP BY i.`product_name`,t.`country`";
		list = returnGoodsDao.findBySql(sql, new Parameter(start, end));
		for (Object[] obj : list) {
			String country = obj[0].toString();
			String productName = obj[1].toString();
			Integer quantity = obj[2]==null?0:Integer.parseInt(obj[2].toString());
			String reason = "QUALITY_UNACCEPTABLE";	//退款统计到质量问题中
			if (quantity > 0) {
				Map<String, Map<String, Integer>> countryMap = rs.get(country);
				if (countryMap == null) {
					countryMap = Maps.newHashMap();
					rs.put(country, countryMap);
				}
				Map<String, Integer> productMap = countryMap.get(productName);
				if (productMap == null) {
					productMap = Maps.newHashMap();
					countryMap.put(productName, productMap);
				}
				Integer totalQuantity = productMap.get(reason);
				if (totalQuantity == null) {
					productMap.put(reason, quantity);
				} else {
					productMap.put(reason, totalQuantity + quantity);
				}
			}
		}
		return rs;
	}
	
	/**
	 * 统计时间段内的退货率
	 * @param start	yyyyMMdd格式日期
	 * @param end	yyyyMMdd格式日期
	 * @return map [国家[产品[原因  退货率]]] 退货原因中total指总退货率
	 */
	public Map<String, Map<String, Map<String, Float>>> getRetrunGoodsRate(Map<String, Map<String, Map<String, Integer>>> retrunGoods, 
			Map<String, Map<String, Integer>> salesVolume){
		Map<String, Map<String, Map<String, Float>>> rs = Maps.newHashMap();
		for (Map.Entry<String, Map<String, Map<String, Integer>>> entry : retrunGoods.entrySet()) { 
		    String country =entry.getKey();
			Map<String, Map<String, Integer>> countryReturnGoods = entry.getValue();
			Map<String, Map<String, Float>> countryMap = rs.get(country);
			if (countryMap == null) {
				countryMap = Maps.newHashMap();
				rs.put(country, countryMap);
			}
			for (Map.Entry<String, Map<String, Integer>> entryRs : countryReturnGoods.entrySet()) {
				String productName=entryRs.getKey();
				Map<String, Float> productMap = countryMap.get(productName);
				if (productMap == null) {
					productMap = Maps.newHashMap();
					countryMap.put(productName, productMap);
				}
				Map<String, Integer> productGoods = entryRs.getValue();
				
				for (Map.Entry<String, Integer> entryGoods: productGoods.entrySet()) {
					String reason=entryGoods.getKey();
					Integer quantity = entryGoods.getValue(); //产品对应国家的退货数
					if (quantity == null || quantity == 0) {
						continue;
					}
					Integer totalVolume = 0;	//产品对应国家的确认销量
					try {
						totalVolume = salesVolume.get(country).get(productName);
					} catch (NullPointerException e) {}
					//过滤条件为滚动31日销量
					if (totalVolume != null && totalVolume > 30) {
						Float rate = quantity/(float)totalVolume;
						productMap.put(reason, rate);
					}
				}
				
			}
		}
		return rs;
	}
	
	@Transactional(readOnly = false)
	public void updatePurchaseDate() {
		String sql = "UPDATE `amazoninfo_return_goods` t INNER JOIN `amazoninfo_order` o " +
				" ON t.`order_id`= o.`amazon_order_id` SET t.`purchase_date`=o.`purchase_date` WHERE DATE_FORMAT(t.`return_date`,'%Y%m%d')>:p1";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		//更新5天以内的退货单
		returnGoodsDao.updateBySql(sql, new Parameter(format.format(DateUtils.addDays(new Date(), -5))));
	}
	
	public Map<String,Integer> findReturnQuantity(Date start,Date end,String country){
		String sql="SELECT r.order_id,CONCAT(p.`product_name`,CASE WHEN p.`color`!='' THEN CONCAT ('_',p.`color`) ELSE '' END) NAME,SUM(r.quantity) FROM amazoninfo_return_goods r "+
			    " JOIN psi_sku p ON r.country=p.country AND r.sku=p.sku AND p.del_flag='0' "+
				" WHERE r.`purchase_date`>=:p1 AND r.`purchase_date`<=:p2 ";
		List<Object> list=Lists.newArrayList();
		Map<String,Integer> map=Maps.newHashMap();
		list.add(start);
		list.add(end);
		if(StringUtils.isNotBlank(country)&&!"total".equals(country)){
			 if("eu".equals(country)){
				 sql+=" and p.country  in  ('de','fr','it','es','uk') ";
			 }else{
				 sql+=" and p.country=:p3 ";
				 list.add(country); 
			 }
			
		}
		sql+=" GROUP BY r.order_id,NAME ";
		List<Object[]> objList=returnGoodsDao.findBySql(sql, new Parameter(list.toArray(new Object[list.size()])));
		if(objList!=null&&objList.size()>0){
			for (Object[] obj: objList) {
				String orderId=obj[0].toString();
				String name=(obj[1]==null?"":obj[1].toString());
				Integer quantity=Integer.parseInt(obj[2].toString());
				map.put(orderId+","+name,quantity);
			}
		}
		return map;
	}
	
	@Transactional(readOnly = false)
	public void deleteReturnGoodsErrorData(){
		String sql ="SELECT GROUP_CONCAT(cc.ids) FROM (SELECT SUBSTRING_INDEX(c.ids,',',(CASE WHEN d.quantity_shipped = 1 THEN (c.qua-d.quantity_shipped) ELSE ROUND(c.qua/2,0) END)) ids , 1 AS cl FROM (SELECT  e.`sku`,e.`order_id`,SUM(e.`quantity`) AS qua ,GROUP_CONCAT(e.`id`) ids FROM amazoninfo_return_goods e WHERE e.`return_date`>DATE_ADD(CURDATE(),INTERVAL -7 DAY) GROUP BY e.`sku`,e.`order_id` HAVING qua >1) c , "+
					" (SELECT a.`amazon_order_id`,b.`sellersku`,SUM(b.`quantity_shipped`) quantity_shipped FROM amazoninfo_order a ,amazoninfo_orderitem b WHERE a.`id` = b.`order_id` AND a.`purchase_date` >DATE_ADD(CURDATE(),INTERVAL -7 DAY) AND b.`quantity_shipped`>0  GROUP BY a.`amazon_order_id`,b.`sellersku` ) d "+
					" WHERE c.sku = d.sellersku AND c.order_id = d.amazon_order_id AND c.qua > d.quantity_shipped) cc GROUP BY cc.cl";
		List<Object> list = returnGoodsDao.findBySql(sql, null);
		if(list!=null && list.size()>0){
			Object ids = list.get(0);
			if(ids!=null){
				sql = "DELETE FROM amazoninfo_return_goods WHERE id IN :p1 ";
				returnGoodsDao.updateBySql(sql, new Parameter(Lists.newArrayList(ids.toString().split(","))));
			}
		}
	}
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		ReturnGoodsService service = applicationContext.getBean(ReturnGoodsService.class);
		service.updatePurchaseDate();
		/*SaleProfitService saleProfitService = applicationContext.getBean(SaleProfitService.class);
		service.setSaleProfitService(saleProfitService);
		Map<String, Map<String, Map<String, Float>>> rs = service.getRetrunGoodsRate("20160529", "20160629");
		for (String country : rs.keySet()) {
			System.out.println(country);
			for (String productName : rs.get(country).keySet()) {
				System.out.println(productName);
				for (String reason : rs.get(country).get(productName).keySet()) {
					System.out.println(reason + "\t" + rs.get(country).get(productName).get(reason));
				}
			}
			System.out.println();
		}*/
		applicationContext.close();
	}

	public SaleProfitService getSaleProfitService() {
		return saleProfitService;
	}

	public void setSaleProfitService(SaleProfitService saleProfitService) {
		this.saleProfitService = saleProfitService;
	}
	
}
