/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.dao.PsiTransportForecastAnalyseOrderDao;
import com.springrain.erp.modules.psi.dao.PsiTransportForecastOrderDao;
import com.springrain.erp.modules.psi.dao.PsiTransportForecastOrderItemDao;
import com.springrain.erp.modules.psi.dao.PsiTransportForecastPlanOrderDao;
import com.springrain.erp.modules.psi.entity.ForecastOrderItem;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.entity.PsiProductTieredPriceDto;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PsiTransportForecastAnalyseOrder;
import com.springrain.erp.modules.psi.entity.PsiTransportForecastOrder;
import com.springrain.erp.modules.psi.entity.PsiTransportForecastOrderItem;
import com.springrain.erp.modules.psi.entity.PsiTransportForecastPlanOrder;
import com.springrain.erp.modules.psi.entity.PsiTransportOrder;
import com.springrain.erp.modules.psi.entity.PsiTransportOrderItem;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrderItem;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrderItem;
import com.springrain.erp.modules.psi.scheduler.PsiConfig;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportOrderService;
import com.springrain.erp.modules.psi.service.lc.LcPurchaseOrderService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;


@Component
@Transactional(readOnly = true)
public class PsiTransportForecastOrderService extends BaseService {
	@Autowired
	private PsiTransportForecastOrderDao    	psiTransportForecastOrderDao;
	@Autowired
	private PsiTransportForecastOrderItemDao    psiTransportForecastOrderItemDao;
	@Autowired
	private PsiProductService    	            psiProductService;
	@Autowired
	private PsiTransportOrderService            psiTransportOrderService;
	@Autowired
	private StockService 					    stockService;
	@Autowired
	private PurchaseOrderService    	        purchaseOrderService;
	@Autowired
	private LcPsiTransportOrderService lcPsiTransportOrderService;
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
	@Autowired
	private PsiTransportForecastPlanOrderDao    	psiTransportForecastPlanOrderDao;
	
	@Autowired
	private PsiTransportForecastAnalyseOrderDao  psiTransportForecastAnalyseOrderDao;
	@Autowired
	private ForecastOrderService    	        forecastOrderService;
	@Autowired
	private PsiSupplierService  supplierService;
	@Autowired
	private PsiProductTieredPriceService productTieredPriceService;
	@Autowired
	private LcPurchaseOrderService  lcPurchaseOrderService;
	@Autowired
	private PsiProductTypeGroupDictService  typeLineService;
	@Autowired
	private PsiProductGroupUserService 		psiProductGroupUserService;
	
	@Transactional(readOnly = false)
	public void save(List<PsiTransportForecastAnalyseOrder> order) {
		psiTransportForecastAnalyseOrderDao.save(order);
	}
	
	@Transactional(readOnly = false)
	public void savePlanOrder(PsiTransportForecastPlanOrder order) {
		psiTransportForecastPlanOrderDao.save(order);
	}
	
	public PsiTransportForecastAnalyseOrder getAnalyseOrder(Integer id){
          return psiTransportForecastAnalyseOrderDao.get(id);		
	}
	
	
	@Transactional(readOnly = false)
	public void deletePlanOrder(Integer id) {
		String sql="update psi_forecast_transport_plan_order set del_flag='1' where id=:p1 ";
		psiTransportForecastPlanOrderDao.updateBySql(sql,new Parameter(id));
	}
	
	
	public PsiTransportForecastPlanOrder findOrder(String productName,String country,String type,String sku,String transportType,String model){
		DetachedCriteria dc = psiTransportForecastPlanOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag","0"));
		dc.add(Restrictions.eq("state","0"));
		if("0".equals(type)){
			dc.add(Restrictions.eq("sku",sku));
			dc.add(Restrictions.eq("transportType",transportType));
			dc.add(Restrictions.eq("model",model));
		}
		dc.add(Restrictions.eq("type",type));
		dc.add(Restrictions.eq("productName",productName));
		dc.add(Restrictions.eq("countryCode",country));
		
		List<PsiTransportForecastPlanOrder> list=psiTransportForecastPlanOrderDao.find(dc);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public Map<String,Integer> findPlanOrder(){
		Map<String,Integer> map=Maps.newHashMap();
		DetachedCriteria dc = psiTransportForecastPlanOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag","0"));
		dc.add(Restrictions.eq("state","0"));
		dc.add(Restrictions.gt("quantity",0));
		List<PsiTransportForecastPlanOrder> list=psiTransportForecastPlanOrderDao.find(dc);
		if(list!=null&&list.size()>0){
			//name,country,order.getType(),order.getTransportType(),order.getModel()
			for (PsiTransportForecastPlanOrder order: list) {
				String name=order.getProductName();
				String country=order.getCountryCode();
				String type=order.getType();
				String model=order.getModel();
				String key="";
				if("0".equals(type)){
					key=name+"_"+country+"_"+model;
				}else{
					key=name+"_"+country;
				}
				Integer qty=map.get(key);
				map.put(key, order.getQuantity()+(qty==null?0:qty));
			}
		}
		return map;
	}
	
	public Map<String,Map<String,List<PsiTransportForecastPlanOrder>>> findPlanOrder(Date startDate,Date endDate,String state,String type,String lineId,String name,String country) {
	    Date today=new Date();
	    today.setHours(0);
	    today.setMinutes(0);
	    today.setSeconds(0);
	    Map<String,Map<String,List<PsiTransportForecastPlanOrder>>> map=Maps.newHashMap();
	    DetachedCriteria dc = psiTransportForecastPlanOrderDao.createDetachedCriteria();
	    if (startDate!=null){
			dc.add(Restrictions.ge("updateDate",startDate));
		}
		if (endDate!=null){
			dc.add(Restrictions.le("updateDate",DateUtils.addDays(endDate,1)));
		}
	    
		if(StringUtils.isNotEmpty(country)){
			if("eu".equals(country)){
				dc.add(Restrictions.in("countryCode",Lists.newArrayList("de","fr","it","es","uk")));
			}else{
				dc.add(Restrictions.eq("countryCode",country));
			}
		}
		if(StringUtils.isNotBlank(type)){
			dc.add(Restrictions.eq("type",type));
		}
		if(StringUtils.isNotEmpty(lineId)){
			String sql="SELECT d.name FROM ( "+
					" SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,a.type "+ 
					" FROM psi_product a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.del_flag='0' ) d "+
					" JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type' "+
					" JOIN psi_product_type_group g ON t.id=g.`dict_id`  "+
					" JOIN psi_product_type_dict p ON p.id=g.id  AND p.`del_flag`='0' WHERE p.id=:p1 ";
			List<String> lists = psiTransportForecastAnalyseOrderDao.findBySql(sql, new Parameter(lineId));
			if(lists!=null && lists.size()>0){
				dc.add(Restrictions.in("productName",lists));
			}else{
				dc.add(Restrictions.eq("productName",lineId));
			}
		}
		
		if(StringUtils.isNotEmpty(name)){
			dc.add(Restrictions.eq("productName",name));
		}
		dc.add(Restrictions.eq("delFlag","0"));
		if(StringUtils.isNotBlank(state)){
			dc.add(Restrictions.eq("state",state));
		}
		
		List<PsiTransportForecastPlanOrder> list=psiTransportForecastPlanOrderDao.find(dc);
		if(list!=null&&list.size()>0){
			for (PsiTransportForecastPlanOrder order : list) {
				Map<String,List<PsiTransportForecastPlanOrder>> temp=map.get(order.getProductName());
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(order.getProductName(),temp);
				}
				List<PsiTransportForecastPlanOrder> tempList=temp.get(order.getCountryCode());
				if(tempList==null){
					tempList=Lists.newArrayList();
					temp.put(order.getCountryCode(),tempList);
				}
				tempList.add(order);
			}
		}
	   return map;
}

	
	public Map<String,Map<String,List<PsiTransportForecastAnalyseOrder>>> findAnalyse(String gap,String lineId,String name,String country) {
		    Date today=new Date();
		    today.setHours(0);
		    today.setMinutes(0);
		    today.setSeconds(0);
		    Map<String,Map<String,List<PsiTransportForecastAnalyseOrder>>> map=Maps.newHashMap();
		    DetachedCriteria dc = psiTransportForecastAnalyseOrderDao.createDetachedCriteria();
			dc.add(Restrictions.ge("updateDate",today));
			if(StringUtils.isNotEmpty(country)){
				if("eu".equals(country)){
					dc.add(Restrictions.in("countryCode",Lists.newArrayList("de","fr","it","es","uk")));
				}else{
					dc.add(Restrictions.eq("countryCode",country));
				}
			}
			if(StringUtils.isNotBlank(gap)&&"1".equals(gap)){
				dc.add(Restrictions.or(Restrictions.isNotNull("airGap"),Restrictions.isNotNull("seaGap"),Restrictions.isNotNull("poGap")));
			}
			if(StringUtils.isNotEmpty(lineId)){
				String sql="SELECT d.name FROM ( "+
						" SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,a.type "+ 
						" FROM psi_product a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.del_flag='0' ) d "+
						" JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type' "+
						" JOIN psi_product_type_group g ON t.id=g.`dict_id`  "+
						" JOIN psi_product_type_dict p ON p.id=g.id  AND p.`del_flag`='0' WHERE p.id=:p1 ";
				List<String> lists = psiTransportForecastAnalyseOrderDao.findBySql(sql, new Parameter(lineId));
				if(lists!=null && lists.size()>0){
					dc.add(Restrictions.in("productName",lists));
				}else{
					dc.add(Restrictions.eq("productName",lineId));
				}
			}
			
			if(StringUtils.isNotEmpty(name)){
				dc.add(Restrictions.eq("productName",name));
			}
			
			List<PsiTransportForecastAnalyseOrder> list=psiTransportForecastAnalyseOrderDao.find(dc);
			if(list!=null&&list.size()>0){
				for (PsiTransportForecastAnalyseOrder order : list) {
					Map<String,List<PsiTransportForecastAnalyseOrder>> temp=map.get(order.getProductName());
					if(temp==null){
						temp=Maps.newHashMap();
						map.put(order.getProductName(),temp);
					}
					List<PsiTransportForecastAnalyseOrder> tempList=temp.get(order.getCountryCode());
					if(tempList==null){
						tempList=Lists.newArrayList();
						temp.put(order.getCountryCode(),tempList);
					}
					tempList.add(order);
				}
			}
		   return map;
	}
	
	
	public PsiTransportForecastOrder get(Integer id) {
		return psiTransportForecastOrderDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void saveItem(PsiTransportForecastOrderItem psiTransportForecastOrderItem) {
		psiTransportForecastOrderItemDao.save(psiTransportForecastOrderItem);
	}
	
	@Transactional(readOnly = false)
	public void updateEuModel(Integer id) {
		String sql="UPDATE psi_forecast_transport_order_item  t SET t.`model`='3' WHERE t.`forecast_order_id`=:p1 AND t.`model`='1' AND t.`country_code` IN ('de','fr','it','es','uk') ";
		psiTransportForecastOrderItemDao.updateBySql(sql,new Parameter(id));
	}
	
	@Transactional(readOnly = false)
	public String updateInfo(Integer itemId,String flag,String content) {
		if("0".equals(flag)){//数量
			String sql="update psi_forecast_transport_order_item set check_quantity=:p1 where id=:p2 ";
			psiTransportForecastOrderDao.updateBySql(sql, new Parameter(content,itemId));
		}else if("1".equals(flag)){//备注
			try {
				content=URLDecoder.decode(content, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String sql="update psi_forecast_transport_order_item set remark=:p1 where id=:p2 ";
			psiTransportForecastOrderDao.updateBySql(sql, new Parameter(content,itemId));
		}else if("2".equals(flag)){//审批备注
			try {
				content=URLDecoder.decode(content, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String sql="update psi_forecast_transport_order_item set review_remark=:p1 where id=:p2 ";
			psiTransportForecastOrderDao.updateBySql(sql, new Parameter(content,itemId));
		}
		return "1";
	}
	
	@Transactional(readOnly = false)
	public String updateInfo2(Integer itemId,String flag,String content) {
		if("0".equals(flag)){//数量
			String sql="";
			if("0".equals(content)){
				sql="update psi_forecast_transport_plan_order set quantity=:p1,del_flag='1' where id=:p2 ";
			}else{
				sql="update psi_forecast_transport_plan_order set quantity=:p1 where id=:p2 ";
			}
			psiTransportForecastPlanOrderDao.updateBySql(sql, new Parameter(content,itemId));
		}else if("1".equals(flag)){//备注
			try {
				content=URLDecoder.decode(content, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String sql="update psi_forecast_transport_plan_order set remark=:p1 where id=:p2 ";
			psiTransportForecastPlanOrderDao.updateBySql(sql, new Parameter(content,itemId));
		}
		return "1";
	}
	
	@Transactional(readOnly = false)
	public String deleteItem(Integer itemId) {
		String sql="update psi_forecast_transport_order_item set display_sta='1' where id=:p1 ";
		psiTransportForecastOrderDao.updateBySql(sql, new Parameter(itemId));
		return "1";
	}
	
	public Boolean countOrder(Date startDate){
		String sql="SELECT COUNT(*) FROM psi_forecast_transport_order AS a WHERE a.`order_sta`!='8' AND a.`create_date`>=:p1";
		List<BigInteger>  list=  this.psiTransportForecastOrderDao.findBySql(sql,new Parameter(startDate));
		if(list.get(0).intValue()>0){
			return false;
		}else{
			return true;
		}
	}
	
	@Transactional(readOnly = false)
	public String cancel(Integer id) {
		String sql="update psi_forecast_transport_order set order_sta='8',cancel_date=now(),cancel_user=:p1 where id=:p2 ";
		psiTransportForecastOrderDao.updateBySql(sql, new Parameter(UserUtils.getUser().getId(),id));
		return "1";
	}
	
	@Transactional(readOnly = false)
	public String checkOrder(Integer id) {
		String sql="update psi_forecast_transport_order set order_sta='5',review_date=now(),review_user=:p1 where id=:p2 ";
		psiTransportForecastOrderDao.updateBySql(sql, new Parameter(UserUtils.getUser().getId(),id));
		return "1";
	}
	
	//国家   运输类型 模式
	public Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>> getById(Integer id) {
		PsiTransportForecastOrder order=psiTransportForecastOrderDao.get(id);
		Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>> map=Maps.newHashMap();
		for (PsiTransportForecastOrderItem item: order.getItems()) {
			Map<String,Map<String,List<PsiTransportForecastOrderItem>>> temp=map.get(item.getCountryCode());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(item.getCountryCode(), temp);
			}
			Map<String,List<PsiTransportForecastOrderItem>> countryMap=temp.get(item.getTransportType());
			if(countryMap==null){
				countryMap=Maps.newHashMap();
				temp.put(item.getTransportType(), countryMap);
			}
			List<PsiTransportForecastOrderItem> list=countryMap.get(item.getModel());
			if(list==null){
				list=Lists.newArrayList();
				countryMap.put(item.getModel(),list);
			}
			list.add(item);
		}
		return map;
	}
	
	
	public void genTrans(List<PsiTransportOrder> psiTransportOrderList,Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>> map,Map<Integer, String>  productMap,Map<String,Integer> packQuantityMap) throws IOException{
		List<Stock> toStock = this.stockService.findStocks("2");
		Set<String> countrySet=map.keySet();
		Set<String> euCountrySet=Sets.newHashSet();
		for (String country: countrySet) {
			if("de,fr,it,es,uk".contains(country)){
				euCountrySet.add(country);
			}
		}
		if(euCountrySet!=null&&euCountrySet.size()>0){
			Set<String> typeSet=Sets.newHashSet("0","1");
			Set<String> modelSet=Sets.newHashSet("0","1","2","3");
			for (String type: typeSet) {
				for (String model: modelSet) {
					List<PsiTransportForecastOrderItem> itemList=Lists.newArrayList();
					for(String country:euCountrySet){
						if(map.get(country)!=null&&map.get(country).get(type)!=null&&map.get(country).get(type).get(model)!=null){
							itemList.addAll(map.get(country).get(type).get(model));
						}
					}
					if(itemList!=null&&itemList.size()>0){
						PsiTransportOrder order=new PsiTransportOrder();
						order.setFromStore(stockService.get(21));
						for (Stock stock : toStock) {
							if("0".equals(type)){//local
								order.setToStore(stockService.get(19));
							}else{
								if("1".equals(stock.getType())&&"de".equals(stock.getPlatform())){
									order.setToStore(stock);
									break;
								}
							}
						}
						order.setOrgin("SZX");
						if("0".equals(type)){//"本地运输" 德国空运本地运输：LEJ,德国空运FBA运输：FRA,德国海运：HAM
							if("0".equals(model)){//"空运"
								order.setDestination("LEJ");
							}else{//"海运"
								order.setDestination("HAM");
							}
						}else{
							if("0".equals(model)){//"空运"
								order.setDestination("FRA");
							}else{//"海运"
								order.setDestination("HAM");
							}
						}
						//order.setDestination(DictUtils.getDictList("transport_pod").get(0).getValue());
						order.setModel(model);
						order.setTransportType(type);
						order.setTransportSta("0");//草稿状态
						order.setPaymentSta("0");//未付款状态
						order.setCreateDate(new Date());
						order.setCreateUser(UserUtils.getUser());
						order.setToCountry("DE");
						order.setDestinationDetail(null);
						order.setPickUpDate(DateUtils.addDays(itemList.get(0).getPsiTransportForecastOrder().getCreateDate(), 7));
						Float volume =0f;
						Float weight=0f;
						Integer boxNum=0;
						List<PsiTransportOrderItem>	orderItems=Lists.newArrayList();
						for (PsiTransportForecastOrderItem item : itemList) {
							PsiTransportOrderItem tranOrderItem=new PsiTransportOrderItem();
							tranOrderItem.setProductName(item.getProductName());
							tranOrderItem.setColorCode(item.getColorCode());
							tranOrderItem.setCountryCode(item.getCountryCode());
							tranOrderItem.setSku(item.getSku());
							tranOrderItem.setQuantity(item.getActualQuantity());
							tranOrderItem.setOfflineSta("0");
							//tranOrderItem.setPackQuantity(item.getBoxNum());
							String name=item.getProductName();
							if(StringUtils.isNotBlank(item.getColorCode())){
								name+="_"+item.getColorCode();
							}
							Integer pack=packQuantityMap.get(name);
							if(name.contains("Inateck DB1001")){
								if("com,uk,jp,ca,mx,".contains(item.getCountryCode())){
									pack=60;
								}else{
									pack=44;
								}
							}else if(name.contains("Inateck DB2001")){
								if("com,jp,ca,mx,".contains(item.getCountryCode())){
									pack=32;
								}else{
									pack=24;
								}
							}
							tranOrderItem.setPackQuantity(pack);
							PsiProduct product=psiProductService.findProductByProductName(item.getProductName());
							tranOrderItem.setProduct(product);
							String res =psiTransportOrderService.getProPriceByProductId(product.getId());
							Float   partsPrice =this.psiTransportOrderService.getPartsPriceByProductId(product.getId(), item.getColorCode());
							Float 	price =0f;
							String  currency="";
							if(StringUtils.isNotEmpty(res)){
								String[] arr=res.split("_");
								price=Float.parseFloat(arr[0].toString())/1.17f;
								price=price*2f;
								//查找配件单价
								if("CNY".equals(arr[1])){//如果是人民币的换成美元
									currency="USD";
									price=partsPrice+(price/AmazonProduct2Service.getRateConfig().get("USD/CNY"));
								}else{
									price=partsPrice+price;
								}
								
							}
//							Float 	price =0f;
//							String  currency="";
//							if(StringUtils.isNotEmpty(res)){
//								String[] arr=res.split("_");
//								price=Float.parseFloat(arr[0])*1.5f;
//								currency=arr[1];
//							}
							tranOrderItem.setProductPrice(price);
							tranOrderItem.setItemPrice(price);
							tranOrderItem.setCurrency(currency);
							tranOrderItem.setTransportOrder(order);
							if(StringUtils.isNotBlank(item.getRemark())&&!"ok".equals(item.getRemark().trim())&&!"OK".equals(item.getRemark().trim())&&!"Ok".equals(item.getRemark().trim())){
								tranOrderItem.setRemark(item.getRemark());
							}
							
							orderItems.add(tranOrderItem);
							volume+=item.getActualQuantity()*1.0f/pack*(Float.parseFloat(productMap.get(product.getId()).split(",")[0]));
							weight+=item.getActualQuantity()*1.0f/pack*(Float.parseFloat(productMap.get(product.getId()).split(",")[1]));
							boxNum+=MathUtils.roundUp(item.getActualQuantity()*1.0d/pack);
						}
						order.setVolume(volume);
						order.setWeight(weight);
						order.setBoxNumber(boxNum);
						order.setItems(orderItems);
						String transportNo =psiTransportOrderService.createFlowNo();
						order.setTransportNo(transportNo);
						psiTransportOrderList.add(order);
					}
					
				}
			}
			countrySet.removeAll(euCountrySet);
		}
			
		if(countrySet!=null&&countrySet.size()>0){//com ca jp
			for (String country: countrySet) {
				Map<String,Map<String,List<PsiTransportForecastOrderItem>>> countryMap=map.get(country);
				for(Map.Entry<String,Map<String,List<PsiTransportForecastOrderItem>>> entry1:countryMap.entrySet()){
					String transportType = entry1.getKey();
					Map<String,List<PsiTransportForecastOrderItem>> typeMap=entry1.getValue();
					for(Map.Entry<String,List<PsiTransportForecastOrderItem>> entry:typeMap.entrySet()){
						String model =entry.getKey();
						List<PsiTransportForecastOrderItem> itemList=entry.getValue();
						PsiTransportOrder order=new PsiTransportOrder();
						order.setFromStore(stockService.get(21));
						for (Stock stock : toStock) {
							if("0".equals(transportType)){//local
								if("jp".equals(country)){
									order.setToStore(stockService.get(147));
								}else{
									order.setToStore(stockService.get(120));
								}
							}else{
								if("1".equals(stock.getType())&&country.equals(stock.getPlatform())){
									order.setToStore(stock);
									break;
								}
							}
						}
						order.setOrgin("SZX");
						//order.setDestination(DictUtils.getDictList("transport_pod").get(0).getValue());
						if("0".equals(transportType)){//"本地运输" 德国空运本地运输：LEJ,德国空运FBA运输：FRA,德国海运：HAM
							if("0".equals(model)){//"空运"
								if("jp".equals(country)){
									order.setDestination("NRT");
								}else if("ca".equals(country)){
									order.setDestination("YYZ");
								}else{
									order.setDestination("DFW");
								}
							}else{//"海运"
                                if("jp".equals(country)){
                                	order.setDestination("NGO");
								}else{
									order.setDestination("LAX");
								}
							}
						}else{
							if("0".equals(model)){//"空运"
								if("jp".equals(country)){
									order.setDestination("NRT");
								}else if("ca".equals(country)){
									order.setDestination("YYZ");
								}else{
									order.setDestination("DFW");
								}
							}else{//"海运"
                                if("jp".equals(country)){
                                	order.setDestination("NGO");
								}else{
									order.setDestination("LAX");
								}
							}
						}
						order.setModel(model);
						order.setTransportType(transportType);
						order.setTransportSta("0");//草稿状态
						order.setPaymentSta("0");//未付款状态
						order.setCreateDate(new Date());
						order.setCreateUser(UserUtils.getUser());
						order.setToCountry(country);
						order.setDestinationDetail(null);
						order.setPickUpDate(DateUtils.addDays(itemList.get(0).getPsiTransportForecastOrder().getCreateDate(), 7));
						Float volume =0f;
						Float weight=0f;
						Integer boxNum=0;
						List<PsiTransportOrderItem>	orderItems=Lists.newArrayList();
						for (PsiTransportForecastOrderItem item : itemList) {
							PsiTransportOrderItem tranOrderItem=new PsiTransportOrderItem();
							tranOrderItem.setProductName(item.getProductName());
							tranOrderItem.setColorCode(item.getColorCode());
							tranOrderItem.setCountryCode(item.getCountryCode());
							tranOrderItem.setSku(item.getSku());
							tranOrderItem.setQuantity(item.getActualQuantity());
							tranOrderItem.setOfflineSta("0");
							//tranOrderItem.setPackQuantity(item.getBoxNum());
							String name=item.getProductName();
							if(StringUtils.isNotBlank(item.getColorCode())){
								name+="_"+item.getColorCode();
							}
							Integer pack=packQuantityMap.get(name);
							if(name.contains("Inateck DB1001")){
								if("com,uk,jp,ca,mx,".contains(item.getCountryCode())){
									pack=60;
								}else{
									pack=44;
								}
							}else if(name.contains("Inateck DB2001")){
								if("com,jp,ca,mx,".contains(item.getCountryCode())){
									pack=32;
								}else{
									pack=24;
								}
							}
							tranOrderItem.setPackQuantity(pack);
							PsiProduct product=psiProductService.findProductByProductName(item.getProductName());
							tranOrderItem.setProduct(product);
							String res =psiTransportOrderService.getProPriceByProductId(product.getId());
							Float   partsPrice =this.psiTransportOrderService.getPartsPriceByProductId(product.getId(), item.getColorCode());
							Float 	price =0f;
							String  currency="";
							if(StringUtils.isNotEmpty(res)){
								String[] arr=res.split("_");
								price=Float.parseFloat(arr[0].toString())/1.17f;
								price=price*2f;
								//查找配件单价
								if("CNY".equals(arr[1])){//如果是人民币的换成美元
									currency="USD";
									price=partsPrice+(price/AmazonProduct2Service.getRateConfig().get("USD/CNY"));
								}else{
									price=partsPrice+price;
								}
								
							}
							tranOrderItem.setProductPrice(price);
							tranOrderItem.setItemPrice(price);
							
							tranOrderItem.setCurrency(currency);
							tranOrderItem.setTransportOrder(order);
							//tranOrderItem.setRemark(item.getRemark());
							if(StringUtils.isNotBlank(item.getRemark())&&!"ok".equals(item.getRemark().trim())&&!"OK".equals(item.getRemark().trim())&&!"Ok".equals(item.getRemark().trim())){
								tranOrderItem.setRemark(item.getRemark());
							}
							orderItems.add(tranOrderItem);
							volume+=item.getActualQuantity()*1.0f/(float)pack*(Float.parseFloat(productMap.get(product.getId()).split(",")[0]));
							weight+=item.getActualQuantity()*1.0f/(float)pack*(Float.parseFloat(productMap.get(product.getId()).split(",")[1]));
							boxNum+=MathUtils.roundUp(item.getActualQuantity()*1.0d/pack);
						}
						order.setVolume(volume);
						order.setWeight(weight);
						order.setBoxNumber(boxNum);
						order.setItems(orderItems);
						String transportNo =psiTransportOrderService.createFlowNo();
						order.setTransportNo(transportNo);
						psiTransportOrderList.add(order);
					}
				}
				
			}
		}
		
	}
	
	public void genTrans2(List<LcPsiTransportOrder> psiTransportOrderList,Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>> map,Map<Integer, String>  productMap,Map<String,Integer> packQuantityMap) throws IOException{
		List<Stock> toStock = this.stockService.findStocks("2");
		Set<String> countrySet=map.keySet();
		Set<String> euCountrySet=Sets.newHashSet();
		Map<String,Float> dutyMap=psiProductService.findCustomDutyById();
		Map<String,Map<String,PsiProductEliminate>> eliminateMap = psiProductEliminateService.findAllByNameAndCountry();
		for (String country: countrySet) {
			if("de,fr,it,es,uk".contains(country)){
				euCountrySet.add(country);
			}
		}
		if(euCountrySet!=null&&euCountrySet.size()>0){
			Set<String> typeSet=Sets.newHashSet("0","1");
			Set<String> modelSet=Sets.newHashSet("0","1","2","3");
			for (String type: typeSet) {
				for (String model: modelSet) {
					List<PsiTransportForecastOrderItem> itemList=Lists.newArrayList();
					for(String country:euCountrySet){
						if(map.get(country)!=null&&map.get(country).get(type)!=null&&map.get(country).get(type).get(model)!=null){
							itemList.addAll(map.get(country).get(type).get(model));
						}
					}
					if(itemList!=null&&itemList.size()>0){
						LcPsiTransportOrder order=new LcPsiTransportOrder();
						order.setFromStore(stockService.get(130));
						for (Stock stock : toStock) {
							if("0".equals(type)){//local
								order.setToStore(stockService.get(19));
							}else{
								if("1".equals(stock.getType())&&"de".equals(stock.getPlatform())){
									order.setToStore(stock);
									break;
								}
							}
						}
						order.setOrgin("SZX");
						if("0".equals(type)){//"本地运输" 德国空运本地运输：LEJ,德国空运FBA运输：FRA,德国海运：HAM
							if("0".equals(model)){//"空运"
								order.setDestination("LEJ");
							}else{//"海运"
								order.setDestination("HAM");
							}
						}else{
							if("0".equals(model)){//"空运"
								order.setDestination("FRA");
							}else{//"海运"
								order.setDestination("HAM");
							}
						}
						//order.setDestination(DictUtils.getDictList("transport_pod").get(0).getValue());
						order.setModel(model);
						order.setTransportType(type);
						order.setTransportSta("0");//草稿状态
						order.setPaymentSta("0");//未付款状态
						order.setCreateDate(new Date());
						order.setCreateUser(UserUtils.getUser());
						order.setToCountry("DE");
						order.setDestinationDetail(null);
						order.setPickUpDate(DateUtils.addDays(itemList.get(0).getPsiTransportForecastOrder().getCreateDate(), 7));
						Float volume =0f;
						Float weight=0f;
						Integer boxNum=0;
						List<LcPsiTransportOrderItem>	orderItems=Lists.newArrayList();
						for (PsiTransportForecastOrderItem item : itemList) {
							LcPsiTransportOrderItem tranOrderItem=new LcPsiTransportOrderItem();
							tranOrderItem.setProductName(item.getProductName());
							tranOrderItem.setColorCode(item.getColorCode());
							tranOrderItem.setCountryCode(item.getCountryCode());
							tranOrderItem.setSku(item.getSku());
							tranOrderItem.setQuantity(item.getActualQuantity());
							tranOrderItem.setOfflineSta("0");
							String name=item.getProductName();
							if(StringUtils.isNotBlank(item.getColorCode())){
								name+="_"+item.getColorCode();
							}
							Integer pack=packQuantityMap.get(name);
							if(name.contains("Inateck DB1001")){
								if("com,uk,jp,ca,mx,".contains(item.getCountryCode())){
									pack=60;
								}else{
									pack=44;
								}
							}else if(name.contains("Inateck DB2001")){
								if("com,jp,ca,mx,".contains(item.getCountryCode())){
									pack=32;
								}else{
									pack=24;
								}
							}
							
							tranOrderItem.setPackQuantity(pack);
							PsiProduct product=psiProductService.findProductByProductName(item.getProductName());
							tranOrderItem.setProduct(product);
							String res =lcPsiTransportOrderService.getProPriceByProductId(product.getId());
//							Float   partsPrice =this.lcPsiTransportOrderService.getPartsPriceByProductId(product.getId(), item.getColorCode());
							Float 	price =0f;
							Float   productPrice=0f;
							Float   lowerPrice=0f;
							String  currency="";
							String countryCode = tranOrderItem.getCountryCode();
							Float priceRate=0f;
							if(StringUtils.isNotEmpty(res)){
								String[] arr=res.split("_");
								price=Float.parseFloat(arr[0].toString());
								Integer taxRefund=(product.getTaxRefund()==null?17:product.getTaxRefund());
								productPrice =Float.parseFloat(arr[0].toString())/(1+(taxRefund/100));
								lowerPrice=0f;
								if("CNY".equals(arr[1])){
									productPrice=productPrice/AmazonProduct2Service.getRateConfig().get("USD/CNY");
									price=price/AmazonProduct2Service.getRateConfig().get("USD/CNY");
								}

								//理诚全部是人民币
								if("it,de,es,fr,uk".contains(countryCode)){
									currency="EUR";
									lowerPrice=productPrice*1.15f;
									price=price/AmazonProduct2Service.getRateConfig().get("EUR/USD");
									productPrice=productPrice/AmazonProduct2Service.getRateConfig().get("EUR/USD");
									if(dutyMap.get(product.getId()+"_eu")!=null&&dutyMap.get(product.getId()+"_eu")>0){
										priceRate=1f;
									}else{
										priceRate=2.2f;
									}
								}else if("com,mx,ca".contains(countryCode)){
									currency="USD";
									lowerPrice=productPrice*1.15f;
									String suf="us";
									if("ca".equals(item.getCountryCode())){
										suf="ca";
									}
									if(dutyMap.get(product.getId()+"_"+suf)!=null&&dutyMap.get(product.getId()+"_"+suf)>0){
										if(productPrice<=0.057*price){
											priceRate=1.2f;
										}else if(productPrice>=0.15*price){
											priceRate=0.8f;
										}else{
											priceRate=1f;
										}
									}else{
										priceRate=2.5f;
									}
								}else if("jp".contains(countryCode)){
									currency="JPY";
									lowerPrice=productPrice*1.15f;
									price=price*AmazonProduct2Service.getRateConfig().get("USD/JPY");
									productPrice=productPrice*AmazonProduct2Service.getRateConfig().get("USD/JPY");
									if(dutyMap.get(product.getId()+"_jp")!=null&&dutyMap.get(product.getId()+"_jp")>0){
										priceRate=0.5f; 
									}else{
										priceRate=2f;
									}
								}
								
							}

							tranOrderItem.setProductPrice(productPrice);
							tranOrderItem.setItemPrice(price);
							//tranOrderItem.setLowerPrice(lowerPrice);
							//tranOrderItem.setImportPrice(productPrice*priceRate);
							if(eliminateMap.get(item.getProductNameColor())!=null&&eliminateMap.get(item.getProductNameColor()).get(item.getCountryCode())!=null){
								tranOrderItem.setLowerPrice(eliminateMap.get(item.getProductNameColor()).get(item.getCountryCode()).getCnpiPrice());
								tranOrderItem.setImportPrice(eliminateMap.get(item.getProductNameColor()).get(item.getCountryCode()).getPiPrice());
							}
							
							tranOrderItem.setCurrency(currency);
							tranOrderItem.setTransportOrder(order);
							if(StringUtils.isNotBlank(item.getRemark())&&!"ok".equals(item.getRemark().trim())&&!"OK".equals(item.getRemark().trim())&&!"Ok".equals(item.getRemark().trim())){
								tranOrderItem.setRemark(item.getRemark());
							}
							
							orderItems.add(tranOrderItem);
							if(!"1".equals(product.getComponents())){
								volume+=item.getActualQuantity()*1.0f/pack*(Float.parseFloat(productMap.get(product.getId()).split(",")[0]));
								weight+=item.getActualQuantity()*1.0f/pack*(Float.parseFloat(productMap.get(product.getId()).split(",")[1]));
								boxNum+=MathUtils.roundUp(item.getActualQuantity()*1.0d/pack);
							}
						}
						order.setVolume(volume);
						order.setWeight(weight);
						order.setBoxNumber(boxNum);
						order.setItems(orderItems);
						String transportNo =lcPsiTransportOrderService.createFlowNo();
						order.setTransportNo(transportNo);
						psiTransportOrderList.add(order);
					}
					
				}
			}
			countrySet.removeAll(euCountrySet);
		}
			
		if(countrySet!=null&&countrySet.size()>0){//com ca jp
			for (String country: countrySet) {
				Map<String,Map<String,List<PsiTransportForecastOrderItem>>> countryMap=map.get(country);
//				for(String transportType:countryMap.keySet()){
				for(Map.Entry<String,Map<String,List<PsiTransportForecastOrderItem>>> entry1:countryMap.entrySet()){
					String transportType = entry1.getKey();
					Map<String,List<PsiTransportForecastOrderItem>> typeMap=entry1.getValue();
					for(Map.Entry<String,List<PsiTransportForecastOrderItem>> entry:typeMap.entrySet()){
						String model =entry.getKey();
						List<PsiTransportForecastOrderItem> itemList=entry.getValue();
						LcPsiTransportOrder order=new LcPsiTransportOrder();
						order.setFromStore(stockService.get(130));
						for (Stock stock : toStock) {
							if("0".equals(transportType)){//local
								if("jp".equals(country)){
									order.setToStore(stockService.get(147));
								}else{
									order.setToStore(stockService.get(120));
								}
								
							}else{
								if("1".equals(stock.getType())&&country.equals(stock.getPlatform())){
									order.setToStore(stock);
									break;
								}
							}
						}
						order.setOrgin("SZX");
						//order.setDestination(DictUtils.getDictList("transport_pod").get(0).getValue());
						if("0".equals(transportType)){//"本地运输" 德国空运本地运输：LEJ,德国空运FBA运输：FRA,德国海运：HAM
							if("0".equals(model)){//"空运"
								if("jp".equals(country)){
									order.setDestination("NRT");
								}else if("ca".equals(country)){
									order.setDestination("YYZ");
								}else{
									order.setDestination("DFW");
								}
							}else{//"海运"
                                if("jp".equals(country)){
                                	order.setDestination("NGO");
								}else{
									order.setDestination("LAX");
								}
							}
						}else{
							if("0".equals(model)){//"空运"
								if("jp".equals(country)){
									order.setDestination("NRT");
								}else if("ca".equals(country)){
									order.setDestination("YYZ");
								}else{
									order.setDestination("DFW");
								}
							}else{//"海运"
                                if("jp".equals(country)){
                                	order.setDestination("NGO");
								}else{
									order.setDestination("LAX");
								}
							}
						}
						order.setModel(model);
						order.setTransportType(transportType);
						order.setTransportSta("0");//草稿状态
						order.setPaymentSta("0");//未付款状态
						order.setCreateDate(new Date());
						order.setCreateUser(UserUtils.getUser());
						order.setToCountry(country);
						order.setDestinationDetail(null);
						order.setPickUpDate(DateUtils.addDays(itemList.get(0).getPsiTransportForecastOrder().getCreateDate(), 7));
						Float volume =0f;
						Float weight=0f;
						Integer boxNum=0;
						List<LcPsiTransportOrderItem>	orderItems=Lists.newArrayList();
						for (PsiTransportForecastOrderItem item : itemList) {
							LcPsiTransportOrderItem tranOrderItem=new LcPsiTransportOrderItem();
							tranOrderItem.setProductName(item.getProductName());
							tranOrderItem.setColorCode(item.getColorCode());
							tranOrderItem.setCountryCode(item.getCountryCode());
							tranOrderItem.setSku(item.getSku());
							tranOrderItem.setQuantity(item.getActualQuantity());
							tranOrderItem.setOfflineSta("0");
							String name=item.getProductName();
							if(StringUtils.isNotBlank(item.getColorCode())){
								name+="_"+item.getColorCode();
							}
							Integer pack=packQuantityMap.get(name);
							if(name.contains("Inateck DB1001")){
								if("com,uk,jp,ca,mx,".contains(item.getCountryCode())){
									pack=60;
								}else{
									pack=44;
								}
							}else if(name.contains("Inateck DB2001")){
								if("com,jp,ca,mx,".contains(item.getCountryCode())){
									pack=32;
								}else{
									pack=24;
								}
							}
							tranOrderItem.setPackQuantity(pack);
							PsiProduct product=psiProductService.findProductByProductName(item.getProductName());
							tranOrderItem.setProduct(product);
							String res =lcPsiTransportOrderService.getProPriceByProductId(product.getId());
//							Float   partsPrice =this.lcPsiTransportOrderService.getPartsPriceByProductId(product.getId(), item.getColorCode());
							Float 	price =0f;
							Float   productPrice=0f;
							Float   lowerPrice=0f;
							String  currency="";
							String countryCode = tranOrderItem.getCountryCode();
							Float priceRate=0f;
							if(StringUtils.isNotEmpty(res)){
								String[] arr=res.split("_");
								price=Float.parseFloat(arr[0].toString());
								Integer taxRefund=(product.getTaxRefund()==null?17:product.getTaxRefund());
								productPrice =Float.parseFloat(arr[0].toString())/(1+(taxRefund/100));
								lowerPrice=0f;
								if("CNY".equals(arr[1])){
									productPrice=productPrice/AmazonProduct2Service.getRateConfig().get("USD/CNY");
									price=price/AmazonProduct2Service.getRateConfig().get("USD/CNY");
								}

								//理诚全部是人民币
								if("it,de,es,fr,uk".contains(countryCode)){
									currency="EUR";
									lowerPrice=productPrice*1.15f;
									price=price/AmazonProduct2Service.getRateConfig().get("EUR/USD");
									productPrice=productPrice/AmazonProduct2Service.getRateConfig().get("EUR/USD");
									if(dutyMap.get(product.getId()+"_eu")!=null&&dutyMap.get(product.getId()+"_eu")>0){
										 priceRate=1f;
									}else{
										priceRate=2.2f;
									}
								}else if("com,mx,ca".contains(countryCode)){
									currency="USD";
									lowerPrice=productPrice*1.15f;
									String suf="us";
									if("ca".equals(item.getCountryCode())){
										suf="ca";
									}
									if(dutyMap.get(product.getId()+"_"+suf)!=null&&dutyMap.get(product.getId()+"_"+suf)>0){
										if(productPrice<=0.057*price){
											priceRate=1.2f;
										}else if(productPrice>=0.15*price){
											priceRate=0.8f;
										}else{
											priceRate=1f;
										}
									}else{
										priceRate=2.5f;
									}
								}else if("jp".contains(countryCode)){
									currency="JPY";
									lowerPrice=productPrice*1.15f;
									price=price*AmazonProduct2Service.getRateConfig().get("USD/JPY");
									productPrice=productPrice*AmazonProduct2Service.getRateConfig().get("USD/JPY");
									if(dutyMap.get(product.getId()+"_jp")!=null&&dutyMap.get(product.getId()+"_jp")>0){
										priceRate=0.5f; 
									}else{
										priceRate=2f;
									}
								}
								
							}
							
							tranOrderItem.setProductPrice(productPrice);
							tranOrderItem.setItemPrice(price);
							//tranOrderItem.setLowerPrice(lowerPrice);
							//tranOrderItem.setImportPrice(productPrice*priceRate);
							String cty = (item.getCountryCode().contains("com")?"com":item.getCountryCode());
							if(eliminateMap.get(item.getProductNameColor())!=null&&eliminateMap.get(item.getProductNameColor()).get(cty)!=null){
								tranOrderItem.setLowerPrice(eliminateMap.get(item.getProductNameColor()).get(cty).getCnpiPrice());
								tranOrderItem.setImportPrice(eliminateMap.get(item.getProductNameColor()).get(cty).getPiPrice());
							}
							
							tranOrderItem.setCurrency(currency);
							tranOrderItem.setTransportOrder(order);
							if(StringUtils.isNotBlank(item.getRemark())&&!"ok".equals(item.getRemark().trim())&&!"OK".equals(item.getRemark().trim())&&!"Ok".equals(item.getRemark().trim())){
								tranOrderItem.setRemark(item.getRemark());
							}
							orderItems.add(tranOrderItem);
							if(!"1".equals(product.getComponents())){
								volume+=item.getActualQuantity()*1.0f/pack*(Float.parseFloat(productMap.get(product.getId()).split(",")[0]));
								weight+=item.getActualQuantity()*1.0f/pack*(Float.parseFloat(productMap.get(product.getId()).split(",")[1]));
								boxNum+=MathUtils.roundUp(item.getActualQuantity()*1.0d/pack);
							}
						}
						order.setVolume(volume);
						order.setWeight(weight);
						order.setBoxNumber(boxNum);
						order.setItems(orderItems);
						String transportNo =lcPsiTransportOrderService.createFlowNo();
						order.setTransportNo(transportNo);
						psiTransportOrderList.add(order);
					}
				}
				
			}
		}
		
	}
	
	@Transactional(readOnly = false)
	public void addSaveData(Integer id) throws IOException{
		List<PsiTransportOrder> psiTransportOrderList=Lists.newArrayList();
		List<LcPsiTransportOrder> lcPsiTransportOrderList=Lists.newArrayList();
		Map<Integer, String>  productMap=psiProductService.getVomueAndWeight();
		//国家-运输类型-空海运
		Map<String,Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>>> map= getTransportItem(id); 
		
		Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>> spMap=map.get("0");
		Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>> lcMap=map.get("1");
		Map<String,Integer> packQuantityMap=psiProductService.getPackQuantity();
		
		if(spMap!=null&&spMap.size()>0){
			genTrans(psiTransportOrderList,spMap,productMap,packQuantityMap);
		}
		
		if(lcMap!=null&&lcMap.size()>0){
			genTrans2(lcPsiTransportOrderList,lcMap,productMap,packQuantityMap);
		}
		
		if(psiTransportOrderList!=null&&psiTransportOrderList.size()>0){
			psiTransportOrderService.saveList(psiTransportOrderList);
		}
		
		if(lcPsiTransportOrderList!=null&&lcPsiTransportOrderList.size()>0){
			lcPsiTransportOrderService.saveList(lcPsiTransportOrderList);
		}
		
		String sql="update psi_forecast_transport_order set order_sta='5',review_date=now(),review_user=:p1 where id=:p2 ";
		psiTransportForecastOrderDao.updateBySql(sql, new Parameter(UserUtils.getUser().getId(),id));
	}
	
	//国家 运输类型 空海运
		public Map<String,Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>>> getTransportItem(Integer id) {
			Map<String,Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>>> map=Maps.newHashMap();
			 DetachedCriteria dc = psiTransportForecastOrderItemDao.createDetachedCriteria();
			    dc.createAlias("psiTransportForecastOrder", "order");
				dc.add(Restrictions.eq("order.id",id));
				dc.add(Restrictions.ne("displaySta","1"));
				dc.add(Restrictions.gt("checkQuantity",0));
				List<PsiTransportForecastOrderItem> tempList=psiTransportForecastOrderItemDao.find(dc);
				if(tempList!=null&&tempList.size()>0){
					
					for (PsiTransportForecastOrderItem item : tempList) {
						if(StringUtils.isBlank(item.getTransSta())){
							item.setTransSta("0");
						}
						Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>> tempMap=map.get(item.getTransSta());
						if(tempMap==null){
							tempMap=Maps.newHashMap();
							map.put(item.getTransSta(), tempMap);
						}
						
						Map<String,Map<String,List<PsiTransportForecastOrderItem>>> temp=tempMap.get(item.getCountryCode());
						if(temp==null){
							temp=Maps.newHashMap();
							tempMap.put(item.getCountryCode(), temp);
						}
						Map<String,List<PsiTransportForecastOrderItem>> countryMap=temp.get(item.getTransportType());
						if(countryMap==null){
							countryMap=Maps.newHashMap();
							temp.put(item.getTransportType(), countryMap);
						}
						List<PsiTransportForecastOrderItem> list=countryMap.get(item.getModel());
						if(list==null){
							list=Lists.newArrayList();
							countryMap.put(item.getModel(),list);
						}
						if(list.size()>0){
							boolean flag=true;
							for (PsiTransportForecastOrderItem orderItem : list) {
								if(orderItem.getProductNameColor().equals(item.getProductNameColor())&&orderItem.getSku().equals(item.getSku())){
									orderItem.setActualQuantity(orderItem.getActualQuantity()+item.getCheckQuantity());
									flag=false;
								}
							}
							if(flag){
								item.setActualQuantity(item.getCheckQuantity());
								list.add(item);
							}
						}else{
							item.setActualQuantity(item.getCheckQuantity());
							list.add(item);
						}
					}
				}
				
			 return map;
		}
	
		
		//国家 运输类型  产品名
		public Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>> getByName(Integer id,String name,String country,String transModel) {
			Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>> map=Maps.newHashMap();
			 DetachedCriteria dc = psiTransportForecastOrderItemDao.createDetachedCriteria();
			    dc.createAlias("psiTransportForecastOrder", "order");
				dc.add(Restrictions.eq("order.id",id));
				dc.add(Restrictions.ne("displaySta","1"));
				if(StringUtils.isNotEmpty(name)){
					if(name.contains("_")){
						String[] arr=name.split("_");
						dc.add(Restrictions.eq("productName",arr[0]));
						dc.add(Restrictions.eq("colorCode",arr[1]));
					}else{
						dc.add(Restrictions.eq("productName",name));
					}
				}
				if(StringUtils.isNotEmpty(country)){
					dc.add(Restrictions.eq("countryCode",country));
				}
				if(StringUtils.isNotEmpty(transModel)){
					dc.add(Restrictions.eq("model",transModel));
				}
				List<PsiTransportForecastOrderItem> tempList=psiTransportForecastOrderItemDao.find(dc);
				if(tempList!=null&&tempList.size()>0){
					
					for (PsiTransportForecastOrderItem item : tempList) {
						Map<String,Map<String,List<PsiTransportForecastOrderItem>>> temp=map.get(item.getCountryCode());
						if(temp==null){
							temp=Maps.newHashMap();
							map.put(item.getCountryCode(), temp);
						}
						Map<String,List<PsiTransportForecastOrderItem>> countryMap=temp.get(item.getTransportType());
						if(countryMap==null){
							countryMap=Maps.newHashMap();
							temp.put(item.getTransportType(), countryMap);
						}
						List<PsiTransportForecastOrderItem> list=countryMap.get(item.getProductNameColor());
						if(list==null){
							list=Lists.newArrayList();
							countryMap.put(item.getProductNameColor(),list);
						}
						list.add(item);
					}
						
				
				}
				
			 return map;
		}
		
		
		//产品名 运输类型   国家 
				public Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>> getByCountryName(Integer id,String name,String country,String transModel,String transSta) {
					Map<Integer, String>  productMap=psiProductService.getVomueAndWeight();
					Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>> map=Maps.newLinkedHashMap();
					 DetachedCriteria dc = psiTransportForecastOrderItemDao.createDetachedCriteria();
					    dc.createAlias("psiTransportForecastOrder", "order");
						dc.add(Restrictions.eq("order.id",id));
						dc.add(Restrictions.ne("displaySta","1"));
						if(StringUtils.isNotEmpty(name)){
							if(name.contains("_")){
								String[] arr=name.split("_");
								dc.add(Restrictions.eq("productName",arr[0]));
								dc.add(Restrictions.eq("colorCode",arr[1]));
							}else{
								dc.add(Restrictions.eq("productName",name));
							}
						}
						if(StringUtils.isNotEmpty(country)){
							if("eu".equals(country)){
								dc.add(Restrictions.in("countryCode",Lists.newArrayList("de","fr","it","es","uk")));
							}else{
								dc.add(Restrictions.eq("countryCode",country));
							}
						}
						if(StringUtils.isNotEmpty(transModel)){
							dc.add(Restrictions.eq("model",transModel));
						}
						
						if(StringUtils.isNotBlank(transSta)){
							dc.add(Restrictions.eq("transSta",transSta));
						}
						
						dc.addOrder(Order.asc("productName"));
						dc.addOrder(Order.asc("transSta"));
						List<PsiTransportForecastOrderItem> tempList=psiTransportForecastOrderItemDao.find(dc);
						if(tempList!=null&&tempList.size()>0){
							
							for (PsiTransportForecastOrderItem item : tempList) {
								Map<String,Map<String,List<PsiTransportForecastOrderItem>>> temp=map.get(item.getProductNameColor());
								if(temp==null){
									temp=Maps.newLinkedHashMap();
									map.put(item.getProductNameColor(), temp);
								}
								Map<String,List<PsiTransportForecastOrderItem>> countryMap=temp.get(item.getTransportType());
								if(countryMap==null){
									countryMap=Maps.newLinkedHashMap();
									temp.put(item.getTransportType(), countryMap);
								}
								List<PsiTransportForecastOrderItem> list=countryMap.get(item.getCountryCode());
								if(list==null){
									list=Lists.newArrayList();
									countryMap.put(item.getCountryCode(),list);
								}
								 Integer box=MathUtils.roundUp(item.getCheckQuantity()*1.0d/item.getBoxNum());
								 item.setBoxQuantity(box);
								  PsiProduct product=psiProductService.findProductByProductName(item.getProductName());
								  float volume=item.getCheckQuantity()*1.0f/item.getBoxNum()*(Float.parseFloat(productMap.get(product.getId()).split(",")[0]));
					              item.setVolume(volume);
								  float weight=item.getCheckQuantity()*1.0f/item.getBoxNum()*(Float.parseFloat(productMap.get(product.getId()).split(",")[1]));
								  item.setWeight(weight);
								  list.add(item);
							}
						}
						
					 return map;
				}	
		
	//国家 运输类型 空海运
	public Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>> get(Integer id,String name,String country,String transModel) {
		Map<String,Map<String,Map<String,List<PsiTransportForecastOrderItem>>>> map=Maps.newHashMap();
		 DetachedCriteria dc = psiTransportForecastOrderItemDao.createDetachedCriteria();
		    dc.createAlias("psiTransportForecastOrder", "order");
			dc.add(Restrictions.eq("order.id",id));
			dc.add(Restrictions.ne("displaySta","1"));
			if(StringUtils.isNotEmpty(name)){
				if(name.contains("_")){
					String[] arr=name.split("_");
					dc.add(Restrictions.eq("productName",arr[0]));
					dc.add(Restrictions.eq("colorCode",arr[1]));
				}else{
					dc.add(Restrictions.eq("productName",name));
				}
			}
			if(StringUtils.isNotEmpty(country)){
				if("eu".equals(country)){
					dc.add(Restrictions.in("countryCode",Lists.newArrayList("de","fr","it","es","uk")));
				}else{
					dc.add(Restrictions.eq("countryCode",country));
				}
				
			}
			if(StringUtils.isNotEmpty(transModel)){
				dc.add(Restrictions.eq("model",transModel));
			}
			List<PsiTransportForecastOrderItem> tempList=psiTransportForecastOrderItemDao.find(dc);
			if(tempList!=null&&tempList.size()>0){
				
				for (PsiTransportForecastOrderItem item : tempList) {
					Map<String,Map<String,List<PsiTransportForecastOrderItem>>> temp=map.get(item.getCountryCode());
					if(temp==null){
						temp=Maps.newHashMap();
						map.put(item.getCountryCode(), temp);
					}
					Map<String,List<PsiTransportForecastOrderItem>> countryMap=temp.get(item.getTransportType());
					if(countryMap==null){
						countryMap=Maps.newHashMap();
						temp.put(item.getTransportType(), countryMap);
					}
					List<PsiTransportForecastOrderItem> list=countryMap.get(item.getModel());
					if(list==null){
						list=Lists.newArrayList();
						countryMap.put(item.getModel(),list);
					}
					list.add(item);
				}
					
			
			}
			
		 return map;
	}
	
	//运输类型 空海运 产品名称  国家 
		public Map<String,Map<String,Map<String,Map<String,Map<String,PsiTransportForecastOrderItem>>>>> getByCountry(Integer id,String name,String country,String transModel) {
			Map<String,Map<String,Map<String,Map<String,Map<String,PsiTransportForecastOrderItem>>>>> map=Maps.newHashMap();
			 DetachedCriteria dc = psiTransportForecastOrderItemDao.createDetachedCriteria();
			    dc.createAlias("psiTransportForecastOrder", "order");
				dc.add(Restrictions.eq("order.id",id));
				dc.add(Restrictions.ne("displaySta","1"));
				if(StringUtils.isNotEmpty(name)){
					if(name.contains("_")){
						String[] arr=name.split("_");
						dc.add(Restrictions.eq("productName",arr[0]));
						dc.add(Restrictions.eq("colorCode",arr[1]));
					}else{
						dc.add(Restrictions.eq("productName",name));
					}
				}
				if(StringUtils.isNotEmpty(country)){
					if("eu".equals(country)){
						dc.add(Restrictions.in("countryCode",Lists.newArrayList("de","fr","it","es","uk")));
					}else{
						dc.add(Restrictions.eq("countryCode",country));
					}
					
				}
				if(StringUtils.isNotEmpty(transModel)){
					dc.add(Restrictions.eq("model",transModel));
				}
				List<PsiTransportForecastOrderItem> tempList=psiTransportForecastOrderItemDao.find(dc);
				if(tempList!=null&&tempList.size()>0){
					
					for (PsiTransportForecastOrderItem item : tempList) {
						Map<String,Map<String,Map<String,Map<String,PsiTransportForecastOrderItem>>>> temp=map.get(item.getTransportType());
						if(temp==null){
							temp=Maps.newHashMap();
							map.put(item.getTransportType(), temp);
						}
						Map<String,Map<String,Map<String,PsiTransportForecastOrderItem>>> countryMap=temp.get(item.getModel());
						if(countryMap==null){
							countryMap=Maps.newHashMap();
							temp.put(item.getModel(), countryMap);
						}
						Map<String,Map<String,PsiTransportForecastOrderItem>> nameMap=countryMap.get(item.getProductNameColor());
						if(nameMap==null){
							nameMap=Maps.newHashMap();
							countryMap.put(item.getProductNameColor(),nameMap);
						}
						Map<String,PsiTransportForecastOrderItem> skuMap=nameMap.get(item.getSku());
						if(skuMap==null){
							skuMap=Maps.newHashMap();
							nameMap.put(item.getSku(),skuMap);
						}
						skuMap.put(item.getCountryCode(),item);
					}
				
				}
				
			 return map;
		}
		
		
		//运输类型 空海运 产品名称  国家 
				public Map<String,Map<String,Map<String,Map<String,Map<String,PsiTransportForecastOrderItem>>>>> getAllCountry(Integer id,String name,String transModel) {
					Map<String,Map<String,Map<String,Map<String,Map<String,PsiTransportForecastOrderItem>>>>> map=Maps.newHashMap();
					 DetachedCriteria dc = psiTransportForecastOrderItemDao.createDetachedCriteria();
					    dc.createAlias("psiTransportForecastOrder", "order");
						dc.add(Restrictions.eq("order.id",id));
						dc.add(Restrictions.ne("displaySta","1"));
						if(StringUtils.isNotEmpty(name)){
							if(name.contains("_")){
								String[] arr=name.split("_");
								dc.add(Restrictions.eq("productName",arr[0]));
								dc.add(Restrictions.eq("colorCode",arr[1]));
							}else{
								dc.add(Restrictions.eq("productName",name));
							}
						}
						
						if(StringUtils.isNotEmpty(transModel)){
							dc.add(Restrictions.eq("model",transModel));
						}
						List<PsiTransportForecastOrderItem> tempList=psiTransportForecastOrderItemDao.find(dc);
						if(tempList!=null&&tempList.size()>0){
							
							for (PsiTransportForecastOrderItem item : tempList) {
								Map<String,Map<String,Map<String,Map<String,PsiTransportForecastOrderItem>>>> temp=map.get(item.getTransportType());
								if(temp==null){
									temp=Maps.newHashMap();
									map.put(item.getTransportType(), temp);
								}
								Map<String,Map<String,Map<String,PsiTransportForecastOrderItem>>> countryMap=temp.get(item.getModel());
								if(countryMap==null){
									countryMap=Maps.newHashMap();
									temp.put(item.getModel(), countryMap);
								}
								Map<String,Map<String,PsiTransportForecastOrderItem>> nameMap=countryMap.get(item.getProductNameColor());
								if(nameMap==null){
									nameMap=Maps.newHashMap();
									countryMap.put(item.getProductNameColor(),nameMap);
								}
								Map<String,PsiTransportForecastOrderItem> skuMap=nameMap.get(item.getSku());
								if(skuMap==null){
									skuMap=Maps.newHashMap();
									nameMap.put(item.getSku(),skuMap);
								}
								skuMap.put(item.getCountryCode(),item);
							}
						
						}
						
					 return map;
				}	

	@Transactional(readOnly = false)
	public void save(PsiTransportForecastOrder psiTransportForecastOrder) {
		psiTransportForecastOrderDao.save(psiTransportForecastOrder);
	}
	
	
	@Transactional(readOnly = false)
	public void updateRemark(Map<String,Map<String,String>> map,Integer id) {
		String sql="update psi_forecast_transport_order_item  set remark=CONCAT(ifnull(remark,''),' ',:p1) where forecast_order_id=:p2 and country_code=:p3 and  CONCAT(`product_name`,CASE WHEN `color_code`='' THEN '' ELSE CONCAT('_',`color_code`) END)=:p4 ";
	    for ( Map.Entry<String,Map<String,String>> entry: map.entrySet()) {
			String name=entry.getKey();
			Map<String,String> countryEntry=entry.getValue();
            for (Map.Entry<String,String> temp: countryEntry.entrySet()) {
				String country=temp.getKey();
				String remark=temp.getValue();
				psiTransportForecastOrderDao.updateBySql(sql, new Parameter(remark,id,country,name));
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void updateUsDelFlag(Integer id) {
		String sql="update psi_forecast_transport_order_item  set display_sta='1' where forecast_order_id=:p1 and country_code='com' ";
		psiTransportForecastOrderDao.updateBySql(sql, new Parameter(id));
	}
	
	@Transactional(readOnly = false)
	public void updateQuantity(List<String> nameList,Integer id) {
		String sql="update psi_forecast_transport_order_item  set check_quantity=0 where forecast_order_id=:p1 and CONCAT(`product_name`,CASE  WHEN color_code='' THEN '' ELSE CONCAT('_',color_code) END ) in :p2 ";
		psiTransportForecastOrderDao.updateBySql(sql, new Parameter(id,nameList));
	}
	
	
	public Page<PsiTransportForecastOrder> find(Page<PsiTransportForecastOrder> page, PsiTransportForecastOrder forecastOrder) {
		DetachedCriteria dc = psiTransportForecastOrderDao.createDetachedCriteria();
		if (forecastOrder.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",forecastOrder.getCreateDate()));
		}
		if (forecastOrder.getUpdateDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(forecastOrder.getUpdateDate(),1)));
		}
		if(StringUtils.isNotEmpty(forecastOrder.getOrderSta())){
			dc.add(Restrictions.eq("orderSta", forecastOrder.getOrderSta()));
		}else{
			dc.add(Restrictions.ne("orderSta","8"));
		}
		page.setOrderBy("id desc");
		return psiTransportForecastOrderDao.find(page,dc);
	}
	
	public Map<String,Map<String,Integer>> getCnInventoryProduct(String productName,String productCountry){
		String sql="SELECT p.sku,CONCAT(p.`product_name`,CASE  WHEN p.color_code='' THEN '' ELSE CONCAT('_',p.color_code) END ) NAME,p.`country_code`,SUM(new_quantity) FROM psi_inventory p WHERE p.`warehouse_id` in ('21','130') "+
	         " and CONCAT(p.`product_name`,CASE  WHEN p.color_code='' THEN '' ELSE CONCAT('_',p.color_code) END )=:p1 and p.`country_code`=:p2 "+
            " GROUP BY p.sku,name,p.`country_code` HAVING SUM(new_quantity)>0 ";
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(productName,productCountry));
		Map<String,Map<String,Integer>> map=Maps.newLinkedHashMap();
		for (Object[] obj: list) {
			String sku=obj[0].toString();
			String name=obj[1].toString();
			//String country=obj[2].toString();
			Integer quantity=Integer.parseInt(obj[3].toString());
			Map<String,Integer> temp=map.get(name);
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(name, temp);
			}
			temp.put(sku, quantity);
			Integer tempQuantity=temp.get("total");
			temp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		}
		getPOInventoryProduct(map, productName, productCountry);
		getPOInventoryProduct2(map, productName, productCountry);
		getNewProduct(map,productName,productCountry);
		getNewProduct2(map,productName,productCountry);
		return map;
	}
	
	public Map<String,Map<String,Integer>> getCnInventoryProduct1(String productName,String productCountry){
		String sql="SELECT p.sku,CONCAT(p.`product_name`,CASE  WHEN p.color_code='' THEN '' ELSE CONCAT('_',p.color_code) END ) NAME,p.`country_code`,SUM(new_quantity) FROM psi_inventory p WHERE p.`warehouse_id`='21' "+
	         " and CONCAT(p.`product_name`,CASE  WHEN p.color_code='' THEN '' ELSE CONCAT('_',p.color_code) END )=:p1 and p.`country_code`=:p2 "+
            " GROUP BY p.sku,name,p.`country_code` HAVING SUM(new_quantity)>0 ";
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(productName,productCountry));
		Map<String,Map<String,Integer>> map=Maps.newLinkedHashMap();
		for (Object[] obj: list) {
			String sku=obj[0].toString();
			String name=obj[1].toString();
			//String country=obj[2].toString();
			Integer quantity=Integer.parseInt(obj[3].toString());
			Map<String,Integer> temp=map.get(name);
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(name, temp);
			}
			temp.put(sku, quantity);
			Integer tempQuantity=temp.get("total");
			temp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		}
		getPOInventoryProduct(map, productName, productCountry);
		getNewProduct(map,productName,productCountry);
		return map;
	}
	
	public Map<String,Map<String,Integer>> getCnInventoryProduct2(String productName,String productCountry){
		String sql="SELECT p.sku,CONCAT(p.`product_name`,CASE  WHEN p.color_code='' THEN '' ELSE CONCAT('_',p.color_code) END ) NAME,p.`country_code`,SUM(new_quantity) FROM psi_inventory p WHERE p.`warehouse_id`='130' "+
	         " and CONCAT(p.`product_name`,CASE  WHEN p.color_code='' THEN '' ELSE CONCAT('_',p.color_code) END )=:p1 and p.`country_code`=:p2 "+
            " GROUP BY p.sku,name,p.`country_code` HAVING SUM(new_quantity)>0 ";
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(productName,productCountry));
		Map<String,Map<String,Integer>> map=Maps.newLinkedHashMap();
		for (Object[] obj: list) {
			String sku=obj[0].toString();
			String name=obj[1].toString();
			//String country=obj[2].toString();
			Integer quantity=Integer.parseInt(obj[3].toString());
			Map<String,Integer> temp=map.get(name);
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(name, temp);
			}
			temp.put(sku, quantity);
			Integer tempQuantity=temp.get("total");
			temp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		}
		getPOInventoryProduct(map, productName, productCountry);
		getNewProduct(map,productName,productCountry);
		return map;
	}
	
	
	
	/***
	 *   查询未收货订单， 
	 */
	public Map<String,Map<String,Integer>> getPOInventoryProduct(Map<String,Map<String,Integer>> map,String productName,String productCountry){
		//查询分批收货的订单item
		List<Integer> orderItemIds = this.purchaseOrderService.getOrderItemFromDelivery(productName,productCountry);
		Map<String,String> skuMap=psiProductService.getSkuByProduct();
		Parameter para = null;
		String sql="";
		Date date=new Date();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
		Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
		if(orderItemIds!=null&&orderItemIds.size()>0){
			sql=" SELECT t.`country_code`,CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) NAME, "+
				" SUM(t.`quantity_ordered`-t.`quantity_off_ordered`-(t.`quantity_received`-t.`quantity_off_received`)) quantity "+ 
				" FROM psi_purchase_order r JOIN psi_purchase_order_item t ON r.id=t.`purchase_order_id` AND t.`del_flag`='0' AND t.id NOT IN :p3 "+
				" WHERE r.`order_sta` IN ('2','3') AND t.`actual_delivery_date`>=:p4 "+
				" AND t.`actual_delivery_date`<=:p5 "+
				" and CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END )=:p1 and t.`country_code`=:p2 "+
				" GROUP BY t.`country_code`,NAME HAVING quantity>0 ";
			para=new Parameter(productName,productCountry,orderItemIds,format.format(start),format.format(end));
		}else{
			sql=" SELECT t.`country_code`,CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) NAME, "+
				" SUM(t.`quantity_ordered`-t.`quantity_off_ordered`-(t.`quantity_received`-t.`quantity_off_received`)) quantity "+ 
				" FROM psi_purchase_order r JOIN psi_purchase_order_item t ON r.id=t.`purchase_order_id` AND t.`del_flag`='0' "+
				" WHERE r.`order_sta` IN ('2','3') AND t.`actual_delivery_date`>=:p3 "+
				" AND t.`actual_delivery_date`<=:p4 "+
				" and CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END )=:p1 and t.`country_code`=:p2 "+
				" GROUP BY t.`country_code`,NAME HAVING quantity>0 ";
			para=new Parameter(productName,productCountry,format.format(start),format.format(end));
		}
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql,para);
		for (Object[] obj: list) {
				String name=obj[1].toString();
				String country=obj[0].toString();
				Integer quantity=Integer.parseInt(obj[2].toString());
				String sku=skuMap.get(name+"_"+country);
				Map<String,Integer> temp=map.get(name);
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					map.put(name, temp);
				}
				
				Integer skuQuantity=temp.get(sku);
				temp.put(sku, quantity+(skuQuantity==null?0:skuQuantity));
				//countryTemp.put(sku, quantity);
				Integer tempQuantity=temp.get("total");
				temp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		}
		//获得分批收货的产品数量
		if(orderItemIds!=null&&orderItemIds.size()>0){
			this.purchaseOrderService.getDeliveryInfos(map, skuMap,productName,productCountry);
		}
		return map;
	}
	
	public Map<String,Map<String,Integer>> getPOInventoryProduct2(Map<String,Map<String,Integer>> map,String productName,String productCountry){
		//查询分批收货的订单item
		List<Integer> orderItemIds = this.purchaseOrderService.getOrderItemFromDelivery2(productName,productCountry);
		Map<String,String> skuMap=psiProductService.getSkuByProduct();
		Parameter para = null;
		String sql="";
		Date date=new Date();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
		Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
		if(orderItemIds!=null&&orderItemIds.size()>0){
			sql=" SELECT t.`country_code`,CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) NAME, "+
				" SUM(t.`quantity_ordered`-t.`quantity_off_ordered`-(t.`quantity_received`-t.`quantity_off_received`)) quantity "+ 
				" FROM lc_psi_purchase_order r JOIN lc_psi_purchase_order_item t ON r.id=t.`purchase_order_id` AND t.`del_flag`='0' AND t.id NOT IN :p3 "+
				" WHERE r.`order_sta` IN ('2','3') AND t.`actual_delivery_date`>=:p4 "+
				" AND t.`actual_delivery_date`<=:p5 "+
				" and CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END )=:p1 and t.`country_code`=:p2 "+
				" GROUP BY t.`country_code`,NAME HAVING quantity>0 ";
			para=new Parameter(productName,productCountry,orderItemIds,format.format(start),format.format(end));
		}else{
			sql=" SELECT t.`country_code`,CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) NAME, "+
				" SUM(t.`quantity_ordered`-t.`quantity_off_ordered`-(t.`quantity_received`-t.`quantity_off_received`)) quantity "+ 
				" FROM lc_psi_purchase_order r JOIN lc_psi_purchase_order_item t ON r.id=t.`purchase_order_id` AND t.`del_flag`='0' "+
				" WHERE r.`order_sta` IN ('2','3') AND t.`actual_delivery_date`>=:p3 "+
				" AND t.`actual_delivery_date`<=:p4 "+
				" and CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END )=:p1 and t.`country_code`=:p2 "+
				" GROUP BY t.`country_code`,NAME HAVING quantity>0 ";
			para=new Parameter(productName,productCountry,format.format(start),format.format(end));
		}
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql,para);
		for (Object[] obj: list) {
				String name=obj[1].toString();
				String country=obj[0].toString();
				Integer quantity=Integer.parseInt(obj[2].toString());
				String sku=skuMap.get(name+"_"+country);
				Map<String,Integer> temp=map.get(name);
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					map.put(name, temp);
				}
				
				Integer skuQuantity=temp.get(sku);
				temp.put(sku, quantity+(skuQuantity==null?0:skuQuantity));
				//countryTemp.put(sku, quantity);
				Integer tempQuantity=temp.get("total");
				temp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		}
		//获得分批收货的产品数量
		if(orderItemIds!=null&&orderItemIds.size()>0){
			this.purchaseOrderService.getDeliveryInfos2(map, skuMap,productName,productCountry);
		}
		return map;
	}
	
	public Map<String,Map<String,Integer>> getNewProduct(Map<String,Map<String,Integer>> map,String productName,String productCountry){
		Date date=new Date();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
		Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
		
		String sql="SELECT t.`country_code`,CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) NAME,t.sku,SUM(t.`quantity`) "+
				" FROM psi_transport_order r JOIN psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' and t.offline_sta='0' "+
				" WHERE r.transport_sta IN ('0') AND r.`transport_type` IN ('0','1') "+
				" and r.`pick_up_date`>=:p3 "+
				" and r.`pick_up_date`<=:p4 "+
				" and CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END)=:p1 and t.`country_code`=:p2 "+
				" AND r.`orgin` IN ('SZX','HKG','CSX') GROUP BY  t.`country_code`,name,t.sku ";
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(productName,productCountry,format.format(start),format.format(end)));
		for (Object[] obj: list) {
				String name=obj[1].toString();
				Integer quantity=Integer.parseInt(obj[3].toString());
				String sku=obj[2].toString();
				if(map!=null&&map.get(name)!=null&&map.get(name).get(sku)!=null){
					Integer skuQuantity=map.get(name).get(sku);
					Map<String,Integer> countryTemp=map.get(name);
					if(skuQuantity-quantity>0){
						countryTemp.put(sku,skuQuantity-quantity);
					}else{
						countryTemp.put(sku,0);
					}
					Integer tempQuantity=countryTemp.get("total");
					if(tempQuantity-quantity>0){
						countryTemp.put("total",tempQuantity-quantity);
					}else{
						countryTemp.put("total",0);
					}
				}
		}
		return map;
	}
	
	public Map<String,Map<String,Integer>> getNewProduct2(Map<String,Map<String,Integer>> map,String productName,String productCountry){
		Date date=new Date();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
		Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
		
		String sql="SELECT t.`country_code`,CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) NAME,t.sku,SUM(t.`quantity`) "+
				" FROM lc_psi_transport_order r JOIN lc_psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' and t.offline_sta='0' "+
				" WHERE r.transport_sta IN ('0') AND r.`transport_type` IN ('0','1') "+
				" and r.`pick_up_date`>=:p3 "+
				" and r.`pick_up_date`<=:p4 "+
				" and CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END)=:p1 and t.`country_code`=:p2 "+
				" AND r.`orgin` IN ('SZX','HKG','CSX') GROUP BY  t.`country_code`,name,t.sku ";
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(productName,productCountry,format.format(start),format.format(end)));
		for (Object[] obj: list) {
				String name=obj[1].toString();
				Integer quantity=Integer.parseInt(obj[3].toString());
				String sku=obj[2].toString();
				if(map!=null&&map.get(name)!=null&&map.get(name).get(sku)!=null){
					Integer skuQuantity=map.get(name).get(sku);
					Map<String,Integer> countryTemp=map.get(name);
					if(skuQuantity-quantity>0){
						countryTemp.put(sku,skuQuantity-quantity);
					}else{
						countryTemp.put(sku,0);
					}
					Integer tempQuantity=countryTemp.get("total");
					if(tempQuantity-quantity>0){
						countryTemp.put("total",tempQuantity-quantity);
					}else{
						countryTemp.put("total",0);
					}
				}
		}
		return map;
	}
	
	//name-country-sku
	public Map<String,Map<String,Map<String,Integer>>> getCnInventoryProduct(){
		String sql="SELECT p.sku,CONCAT(p.`product_name`,CASE  WHEN p.color_code='' THEN '' ELSE CONCAT('_',p.color_code) END ) NAME,p.`country_code`,SUM(new_quantity) FROM psi_inventory p WHERE p.`warehouse_id` in ('21','130') "+
            " GROUP BY p.sku,name,p.`country_code` HAVING SUM(new_quantity)>0 ";
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql);
		Map<String,Map<String,Map<String,Integer>>> map=Maps.newLinkedHashMap();
		for (Object[] obj: list) {
			String sku=obj[0].toString();
			String name=obj[1].toString();
			String country=obj[2].toString();
			Integer quantity=Integer.parseInt(obj[3].toString());
			Map<String,Map<String,Integer>> temp=map.get(name);
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(name, temp);
			}
			Map<String,Integer> countryTemp=temp.get(country);
			if(countryTemp==null){
				countryTemp=Maps.newLinkedHashMap();
				temp.put(country, countryTemp);
			}
			countryTemp.put(sku, quantity);
			Integer tempQuantity=countryTemp.get("total");
			countryTemp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		}
		getPOInventoryProduct(map);
		getPOInventoryProduct2(map);
		getNewProduct(map);
		getNewProduct2(map);
		return map;
	}
	
	public Map<String,Map<String,Map<String,Integer>>> getCnInventoryProduct1(){
		String sql="SELECT p.sku,CONCAT(p.`product_name`,CASE  WHEN p.color_code='' THEN '' ELSE CONCAT('_',p.color_code) END ) NAME,p.`country_code`,SUM(new_quantity) FROM psi_inventory p WHERE p.`warehouse_id`='21' "+
            " GROUP BY p.sku,name,p.`country_code` HAVING SUM(new_quantity)>0 ";
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql);
		Map<String,Map<String,Map<String,Integer>>> map=Maps.newLinkedHashMap();
		for (Object[] obj: list) {
			String sku=obj[0].toString();
			String name=obj[1].toString();
			String country=obj[2].toString();
			Integer quantity=Integer.parseInt(obj[3].toString());
			Map<String,Map<String,Integer>> temp=map.get(name);
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(name, temp);
			}
			Map<String,Integer> countryTemp=temp.get(country);
			if(countryTemp==null){
				countryTemp=Maps.newLinkedHashMap();
				temp.put(country, countryTemp);
			}
			countryTemp.put(sku, quantity);
			Integer tempQuantity=countryTemp.get("total");
			countryTemp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		}
		getPOInventoryProduct(map);
		getNewProduct(map);
		return map;
	}
	
	public Map<String,Map<String,Map<String,Integer>>> getCnInventory(){
		String sql="SELECT p.sku,CONCAT(p.`product_name`,CASE  WHEN p.color_code='' THEN '' ELSE CONCAT('_',p.color_code) END ) NAME,p.`country_code`,SUM(new_quantity) FROM psi_inventory p WHERE p.`warehouse_id`='21' "+
            " GROUP BY p.sku,name,p.`country_code` HAVING SUM(new_quantity)>0 ";
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql);
		Map<String,Map<String,Map<String,Integer>>> map=Maps.newLinkedHashMap();
		for (Object[] obj: list) {
			String sku=obj[0].toString();
			String name=obj[1].toString();
			String country=obj[2].toString();
			Integer quantity=Integer.parseInt(obj[3].toString());
			Map<String,Map<String,Integer>> temp=map.get(name);
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(name, temp);
			}
			Map<String,Integer> countryTemp=temp.get(country);
			if(countryTemp==null){
				countryTemp=Maps.newLinkedHashMap();
				temp.put(country, countryTemp);
			}
			countryTemp.put(sku, quantity);
			Integer tempQuantity=countryTemp.get("total");
			countryTemp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		}
		return map;
	}
	
	public Map<String,Map<String,Map<String,Integer>>> getCnInventoryProduct2(){
		String sql="SELECT p.sku,CONCAT(p.`product_name`,CASE  WHEN p.color_code='' THEN '' ELSE CONCAT('_',p.color_code) END ) NAME,p.`country_code`,SUM(new_quantity) FROM psi_inventory p WHERE p.`warehouse_id`='130' "+
            " GROUP BY p.sku,name,p.`country_code` HAVING SUM(new_quantity)>0 ";
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql);
		Map<String,Map<String,Map<String,Integer>>> map=Maps.newLinkedHashMap();
		for (Object[] obj: list) {
			String sku=obj[0].toString();
			String name=obj[1].toString();
			String country=obj[2].toString();
			Integer quantity=Integer.parseInt(obj[3].toString());
			Map<String,Map<String,Integer>> temp=map.get(name);
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(name, temp);
			}
			Map<String,Integer> countryTemp=temp.get(country);
			if(countryTemp==null){
				countryTemp=Maps.newLinkedHashMap();
				temp.put(country, countryTemp);
			}
			countryTemp.put(sku, quantity);
			Integer tempQuantity=countryTemp.get("total");
			countryTemp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		}
		getPOInventoryProduct2(map);
		getNewProduct2(map);
		return map;
	}
	
	public Map<String,Map<String,Map<String,Integer>>> getLCCnInventory(){
		String sql="SELECT p.sku,CONCAT(p.`product_name`,CASE  WHEN p.color_code='' THEN '' ELSE CONCAT('_',p.color_code) END ) NAME,p.`country_code`,SUM(new_quantity) FROM psi_inventory p WHERE p.`warehouse_id`='130' "+
            " GROUP BY p.sku,name,p.`country_code` HAVING SUM(new_quantity)>0 ";
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql);
		Map<String,Map<String,Map<String,Integer>>> map=Maps.newLinkedHashMap();
		for (Object[] obj: list) {
			String sku=obj[0].toString();
			String name=obj[1].toString();
			String country=obj[2].toString();
			Integer quantity=Integer.parseInt(obj[3].toString());
			Map<String,Map<String,Integer>> temp=map.get(name);
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(name, temp);
			}
			Map<String,Integer> countryTemp=temp.get(country);
			if(countryTemp==null){
				countryTemp=Maps.newLinkedHashMap();
				temp.put(country, countryTemp);
			}
			countryTemp.put(sku, quantity);
			Integer tempQuantity=countryTemp.get("total");
			countryTemp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		}
		return map;
	}
	
	/**
	 * {productName_color,国家{sku,数量}}
	 * 
	 */
	public Map<String,Map<String,Map<String,Integer>>> getPOInventoryProduct(Map<String,Map<String,Map<String,Integer>>> map){
		//查询分批收货的订单item
		List<Integer> orderItemIds = this.purchaseOrderService.getOrderItemFromDelivery(null,null);
		Map<String,String> skuMap=psiProductService.getSkuByProduct();
		Map<String, String> newMap=psiProductEliminateService.findIsNewMap();//产品名_颜色_国   1新品
		String sql="";
		List<Object[]> list=null;
		Date date=new Date();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
		Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
		if(orderItemIds!=null&&orderItemIds.size()>0){
			sql=" SELECT t.`country_code`,CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) NAME, "+
					" SUM(t.`quantity_ordered`-t.`quantity_off_ordered`-(t.`quantity_received`-t.`quantity_off_received`)) quantity "+ 
					" FROM psi_purchase_order r JOIN psi_purchase_order_item t ON r.id=t.`purchase_order_id` AND t.`del_flag`='0' AND t.id NOT IN :p1 "+
					" WHERE r.`order_sta` IN ('2','3') AND t.`actual_delivery_date`>=:p2 "+
					" AND t.`actual_delivery_date`<=:p3 "+
					" GROUP BY t.`country_code`,NAME HAVING quantity>0 ";
			list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(orderItemIds,format.format(start),format.format(end)));
		}else{
			sql=" SELECT t.`country_code`,CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) NAME, "+
					" SUM(t.`quantity_ordered`-t.`quantity_off_ordered`-(t.`quantity_received`-t.`quantity_off_received`)) quantity "+ 
					" FROM psi_purchase_order r JOIN psi_purchase_order_item t ON r.id=t.`purchase_order_id` AND t.`del_flag`='0' "+
					" WHERE r.`order_sta` IN ('2','3') AND t.`actual_delivery_date`>=:p1 "+
					" AND t.`actual_delivery_date`<=:p2 "+
					" GROUP BY t.`country_code`,NAME HAVING quantity>0 ";
			list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(format.format(start),format.format(end)));
		}
		for (Object[] obj: list) {
				String name=obj[1].toString();
				String country=obj[0].toString();
				Integer quantity=Integer.parseInt(obj[2].toString());
				String sku=skuMap.get(name+"_"+country);
				//没绑定sku的不算
				if(StringUtils.isEmpty(sku)&&"1".equals(newMap.get(name+"_"+country))){//产品名_颜色_国   1新品
					sku=name+"_"+country;
				}
				
				if(StringUtils.isEmpty(sku)){
					continue;
				}
				Map<String,Map<String,Integer>> temp=map.get(name);
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					map.put(name, temp);
				}
				Map<String,Integer> countryTemp=temp.get(country);
				if(countryTemp==null){
					countryTemp=Maps.newLinkedHashMap();
					temp.put(country, countryTemp);
				}
				Integer skuQuantity=countryTemp.get(sku);
				countryTemp.put(sku, quantity+(skuQuantity==null?0:skuQuantity));
				//countryTemp.put(sku, quantity);
				Integer tempQuantity=countryTemp.get("total");
				countryTemp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		}
		
		//获得分批收货的产品数量
		if(orderItemIds!=null&&orderItemIds.size()>0){
			this.purchaseOrderService.getDeliveryInfos(map, skuMap);
		}
		return map;
	}
	
	
	/**
	 * {productName_color,国家{sku,数量}}
	 * 
	 */
	public Map<String,Map<String,Map<String,Integer>>> getPOInventoryProduct2(Map<String,Map<String,Map<String,Integer>>> map){
		//查询分批收货的订单item
		List<Integer> orderItemIds = this.purchaseOrderService.getOrderItemFromDelivery2(null,null);
		Map<String,String> skuMap=psiProductService.getSkuByProduct();
		Map<String, String> newMap=psiProductEliminateService.findIsNewMap();//产品名_颜色_国   1新品
		String sql="";
		List<Object[]> list=null;
		Date date=new Date();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
		Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
		if(orderItemIds!=null&&orderItemIds.size()>0){
			sql=" SELECT t.`country_code`,CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) NAME, "+
					" SUM(t.`quantity_ordered`-t.`quantity_off_ordered`-(t.`quantity_received`-t.`quantity_off_received`)) quantity "+ 
					" FROM lc_psi_purchase_order r JOIN lc_psi_purchase_order_item t ON r.id=t.`purchase_order_id` AND t.`del_flag`='0' AND t.id NOT IN :p1 "+
					" WHERE r.`order_sta` IN ('2','3') AND t.`actual_delivery_date`>=:p2 "+
					" AND t.`actual_delivery_date`<=:p3 "+
					" GROUP BY t.`country_code`,NAME HAVING quantity>0 ";
			list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(orderItemIds,format.format(start),format.format(end)));
		}else{
			sql=" SELECT t.`country_code`,CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) NAME, "+
					" SUM(t.`quantity_ordered`-t.`quantity_off_ordered`-(t.`quantity_received`-t.`quantity_off_received`)) quantity "+ 
					" FROM lc_psi_purchase_order r JOIN lc_psi_purchase_order_item t ON r.id=t.`purchase_order_id` AND t.`del_flag`='0' "+
					" WHERE r.`order_sta` IN ('2','3') AND t.`actual_delivery_date`>=:p1 "+
					" AND t.`actual_delivery_date`<=:p2 "+
					" GROUP BY t.`country_code`,NAME HAVING quantity>0 ";
			list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(format.format(start),format.format(end)));
		}
		for (Object[] obj: list) {
				String name=obj[1].toString();
				String country=obj[0].toString();
				Integer quantity=Integer.parseInt(obj[2].toString());
				String sku=skuMap.get(name+"_"+country);
				//没绑定sku的不算
				if(StringUtils.isEmpty(sku)&&"1".equals(newMap.get(name+"_"+country))){//产品名_颜色_国   1新品
					sku=name+"_"+country;
				}
				if(StringUtils.isEmpty(sku)){
					continue;
				}
				Map<String,Map<String,Integer>> temp=map.get(name);
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					map.put(name, temp);
				}
				Map<String,Integer> countryTemp=temp.get(country);
				if(countryTemp==null){
					countryTemp=Maps.newLinkedHashMap();
					temp.put(country, countryTemp);
				}
				Integer skuQuantity=countryTemp.get(sku);
				countryTemp.put(sku, quantity+(skuQuantity==null?0:skuQuantity));
				//countryTemp.put(sku, quantity);
				Integer tempQuantity=countryTemp.get("total");
				countryTemp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		}
		
		//获得分批收货的产品数量
		if(orderItemIds!=null&&orderItemIds.size()>0){
			this.purchaseOrderService.getDeliveryInfos2(map, skuMap);
		}
		return map;
	}
	
	public Map<String,Map<String,Map<String,Integer>>> getNewProduct(Map<String,Map<String,Map<String,Integer>>> map){
		Date date=new Date();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
		Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
		
		String sql="SELECT t.`country_code`,CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) NAME,t.sku,SUM(t.`quantity`) "+
				" FROM psi_transport_order r JOIN psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' and t.offline_sta='0' "+
				" WHERE r.transport_sta IN ('0') AND r.`transport_type` IN ('0','1') "+
				" and r.`pick_up_date`>=:p1 "+
				" and r.`pick_up_date`<=:p2 "+
				" AND r.`orgin` IN ('SZX','HKG','CSX') GROUP BY  t.`country_code`,name,t.sku ";
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(format.format(start),format.format(end)));
		for (Object[] obj: list) {
				String name=obj[1].toString();
				String country=obj[0].toString();
				Integer quantity=Integer.parseInt(obj[3].toString());
				String sku=obj[2]==null?"":obj[2].toString();
				if(map!=null&&map.get(name)!=null&&map.get(name).get(country)!=null&&map.get(name).get(country).get(sku)!=null){
					Map<String,Integer> countryTemp=map.get(name).get(country);
					Integer skuQuantity=countryTemp.get(sku);
					if(skuQuantity-quantity>0){
						countryTemp.put(sku,skuQuantity-quantity);
					}else{
						countryTemp.put(sku,0);
					}
					Integer tempQuantity=countryTemp.get("total");
					if(tempQuantity-quantity>0){
						countryTemp.put("total",tempQuantity-quantity);
					}else{
						countryTemp.put("total",0);
					}
				}
		}
		return map;
	}
	
	public Map<String,Map<String,Map<String,Integer>>> getNewProduct2(Map<String,Map<String,Map<String,Integer>>> map){
		Date date=new Date();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
		Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
		
		String sql="SELECT t.`country_code`,CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) NAME,t.sku,SUM(t.`quantity`) "+
				" FROM lc_psi_transport_order r JOIN lc_psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' and t.offline_sta='0' "+
				" WHERE r.transport_sta IN ('0') AND r.`transport_type` IN ('0','1') "+
				" and r.`pick_up_date`>=:p1 "+
				" and r.`pick_up_date`<=:p2 "+
				" AND r.`orgin` IN ('SZX','HKG','CSX') GROUP BY  t.`country_code`,name,t.sku ";
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(format.format(start),format.format(end)));
		for (Object[] obj: list) {
				String name=obj[1].toString();
				String country=obj[0].toString();
				Integer quantity=Integer.parseInt(obj[3].toString());
				String sku=obj[2].toString();
				if(map!=null&&map.get(name)!=null&&map.get(name).get(country)!=null&&map.get(name).get(country).get(sku)!=null){
					Map<String,Integer> countryTemp=map.get(name).get(country);
					Integer skuQuantity=countryTemp.get(sku);
					if(skuQuantity-quantity>0){
						countryTemp.put(sku,skuQuantity-quantity);
					}else{
						countryTemp.put(sku,0);
					}
					Integer tempQuantity=countryTemp.get("total");
					if(tempQuantity-quantity>0){
						countryTemp.put("total",tempQuantity-quantity);
					}else{
						countryTemp.put("total",0);
					}
				}
		}
		return map;
	}
	
	/*public Map<String,Map<String,Map<String,Integer>>> getNewProduct(){
		Map<String,Map<String,Map<String,Integer>>> map=Maps.newHashMap();
		String sql="SELECT t.`country_code`,CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) NAME,t.sku,SUM(t.`quantity`) "+
				" FROM psi_transport_order r JOIN psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' "+
				" WHERE r.transport_sta IN ('0') AND r.`transport_type` IN ('0','1') "+
				" and r.`pick_up_date`>SUBDATE(DATE_ADD(CURDATE(),INTERVAL -7 DAY),DATE_FORMAT(DATE_ADD(CURDATE(),INTERVAL -7 DAY),'%w')-7) "+
				" and r.`pick_up_date`<SUBDATE(DATE_ADD(CURDATE(),INTERVAL 7 DAY),DATE_FORMAT(DATE_ADD(CURDATE(),INTERVAL 7 DAY),'%w')-1) "+
				" AND r.`orgin` IN ('SZX','HKG') GROUP BY  t.`country_code`,name,t.sku ";
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql);
		for (Object[] obj: list) {
				String name=obj[1].toString();
				String country=obj[0].toString();
				Integer quantity=Integer.parseInt(obj[3].toString());
				String sku=obj[2].toString();
				Map<String,Map<String,Integer>> temp=map.get(name);
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					map.put(name, temp);
				}
				Map<String,Integer> countryTemp=temp.get(country);
				if(countryTemp==null){
					countryTemp=Maps.newLinkedHashMap();
					temp.put(country, countryTemp);
				}
				countryTemp.put(sku, quantity);
				Integer tempQuantity=countryTemp.get("total");
				countryTemp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		}
		return map;
	}*/
	
	public Map<String,Map<String,Map<String,Integer>>> getNewProduct(){
		Map<String,Map<String,Map<String,Integer>>> map=Maps.newHashMap();
		Date date=new Date();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
		Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
		
		String sql="select t.`country_code`,t.name,t.sku,sum(t.quantity),t.model from " +
				" (SELECT t.`country_code`,CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) NAME,t.sku,SUM(t.`quantity`) quantity,r.model "+
				" FROM psi_transport_order r JOIN psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' and t.offline_sta='0' "+
				" WHERE r.transport_sta IN ('0') AND r.`transport_type` IN ('0','1') "+
				" and r.`pick_up_date`>=:p1 "+
				" and r.`pick_up_date`<=:p2 "+
				" AND r.`orgin` IN ('SZX','HKG','CSX') GROUP BY  t.`country_code`,name,t.sku,r.model "+
				" union all SELECT t.`country_code`,CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) NAME,t.sku,SUM(t.`quantity`) quantity,r.model "+
				" FROM lc_psi_transport_order r JOIN lc_psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' and t.offline_sta='0' "+
				" WHERE r.transport_sta IN ('0') AND r.`transport_type` IN ('0','1') "+
				" and r.`pick_up_date`>=:p1 "+
				" and r.`pick_up_date`<=:p2 "+
				" AND r.`orgin` IN ('SZX','HKG','CSX') GROUP BY  t.`country_code`,name,t.sku,r.model) t group by  t.`country_code`,t.name,t.sku,t.model ";
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(format.format(start),format.format(end)));
		for (Object[] obj: list) {
				String name=obj[1].toString();
				String country=obj[0].toString();
				Integer quantity=Integer.parseInt(obj[3].toString());
				String sku=obj[2]==null?"":obj[2].toString();
				String model=obj[4].toString();
				Map<String,Map<String,Integer>> temp=map.get(name+"_"+country);
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					map.put(name+"_"+country, temp);
				}
				Map<String,Integer> totalTemp=temp.get("total");
				if(totalTemp==null){
					totalTemp=Maps.newLinkedHashMap();
					temp.put("total", totalTemp);
				}
				totalTemp.put(sku, quantity+(totalTemp.get(sku)==null?0:totalTemp.get(sku)));
				
				Map<String,Integer> countryTemp=temp.get(model);
				if(countryTemp==null){
					countryTemp=Maps.newLinkedHashMap();
					temp.put(model, countryTemp);
				}
				countryTemp.put(sku, quantity);
				Integer tempQuantity=countryTemp.get("total");
				countryTemp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		}
		return map;
	}
	
	public Map<String,Map<String,Map<String,Integer>>> getNewProduct1(){
		Map<String,Map<String,Map<String,Integer>>> map=Maps.newHashMap();
		Date date=new Date();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
		Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
		
		String sql=" SELECT t.`country_code`,CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) NAME,t.sku,SUM(t.`quantity`) quantity,r.model "+
				" FROM psi_transport_order r JOIN psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' and t.offline_sta='0' "+
				" WHERE r.transport_sta IN ('0') AND r.`transport_type` IN ('0','1') "+
				" and r.`pick_up_date`>=:p1 "+
				" and r.`pick_up_date`<=:p2 "+
				" AND r.`orgin` IN ('SZX','HKG','CSX') GROUP BY  t.`country_code`,name,t.sku,r.model ";
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(format.format(start),format.format(end)));
		for (Object[] obj: list) {
				String name=obj[1].toString();
				String country=obj[0].toString();
				Integer quantity=Integer.parseInt(obj[3].toString());
				String sku=obj[2]==null?"":obj[2].toString();
				String model=obj[4].toString();
				Map<String,Map<String,Integer>> temp=map.get(name+"_"+country);
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					map.put(name+"_"+country, temp);
				}
				Map<String,Integer> totalTemp=temp.get("total");
				if(totalTemp==null){
					totalTemp=Maps.newLinkedHashMap();
					temp.put("total", totalTemp);
				}
				totalTemp.put(sku, quantity+(totalTemp.get(sku)==null?0:totalTemp.get(sku)));
				
				Map<String,Integer> countryTemp=temp.get(model);
				if(countryTemp==null){
					countryTemp=Maps.newLinkedHashMap();
					temp.put(model, countryTemp);
				}
				countryTemp.put(sku, quantity);
				Integer tempQuantity=countryTemp.get("total");
				countryTemp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		}
		return map;
	}
	
	public Map<String,Map<String,Map<String,Integer>>> getNewProduct2(){
		Map<String,Map<String,Map<String,Integer>>> map=Maps.newHashMap();
		Date date=new Date();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
		Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
		
		String sql=" SELECT t.`country_code`,CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) NAME,t.sku,SUM(t.`quantity`) quantity,r.model "+
				" FROM lc_psi_transport_order r JOIN lc_psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' and t.offline_sta='0' "+
				" WHERE r.transport_sta IN ('0') AND r.`transport_type` IN ('0','1') "+
				" and r.`pick_up_date`>=:p1 "+
				" and r.`pick_up_date`<=:p2 "+
				" AND r.`orgin` IN ('SZX','HKG','CSX') GROUP BY  t.`country_code`,name,t.sku,r.model";
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(format.format(start),format.format(end)));
		for (Object[] obj: list) {
				String name=obj[1].toString();
				String country=obj[0].toString();
				Integer quantity=Integer.parseInt(obj[3].toString());
				String sku=obj[2]==null?"":obj[2].toString();
				String model=obj[4].toString();
				Map<String,Map<String,Integer>> temp=map.get(name+"_"+country);
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					map.put(name+"_"+country, temp);
				}
				Map<String,Integer> totalTemp=temp.get("total");
				if(totalTemp==null){
					totalTemp=Maps.newLinkedHashMap();
					temp.put("total", totalTemp);
				}
				totalTemp.put(sku, quantity+(totalTemp.get(sku)==null?0:totalTemp.get(sku)));
				
				Map<String,Integer> countryTemp=temp.get(model);
				if(countryTemp==null){
					countryTemp=Maps.newLinkedHashMap();
					temp.put(model, countryTemp);
				}
				countryTemp.put(sku, quantity);
				Integer tempQuantity=countryTemp.get("total");
				countryTemp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		}
		return map;
	}
	
	public Map<String,Map<String,Map<String,Integer>>> getNewProduct2(String createDate){
		Map<String,Map<String,Map<String,Integer>>> map=Maps.newHashMap();
	
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		Date date=new Date();
		try {
			date = format.parse(createDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
		Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
		
		String sql=" SELECT t.`country_code`,CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) NAME,t.sku,SUM(t.`quantity`) quantity,r.model "+
				" FROM lc_psi_transport_order r JOIN lc_psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' and t.offline_sta='0' "+
				" WHERE r.transport_sta IN ('0') AND r.`transport_type` IN ('0','1') "+
				" and r.`pick_up_date`>=:p1 "+
				" and r.`pick_up_date`<=:p2 "+
				" AND r.`orgin` IN ('SZX','HKG','CSX') GROUP BY  t.`country_code`,name,t.sku,r.model";
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(format.format(start),format.format(end)));
		for (Object[] obj: list) {
				String name=obj[1].toString();
				String country=obj[0].toString();
				Integer quantity=Integer.parseInt(obj[3].toString());
				String sku=obj[2]==null?"":obj[2].toString();
				String model=obj[4].toString();
				Map<String,Map<String,Integer>> temp=map.get(name+"_"+country);
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					map.put(name+"_"+country, temp);
				}
				Map<String,Integer> totalTemp=temp.get("total");
				if(totalTemp==null){
					totalTemp=Maps.newLinkedHashMap();
					temp.put("total", totalTemp);
				}
				totalTemp.put(sku, quantity+(totalTemp.get(sku)==null?0:totalTemp.get(sku)));
				Integer totalQuantity=totalTemp.get("total");
				totalTemp.put("total", quantity+(totalQuantity==null?0:totalQuantity));
				
				Map<String,Integer> countryTemp=temp.get(model);
				if(countryTemp==null){
					countryTemp=Maps.newLinkedHashMap();
					temp.put(model, countryTemp);
				}
				countryTemp.put(sku, quantity);
				Integer tempQuantity=countryTemp.get("total");
				countryTemp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
		}
		return map;
	}
	
	
	public Map<String,Integer> getFbaTansportQuantity(){
		Map<String,Integer> map=Maps.newHashMap();
		String fbaTran="SELECT c.productName,c.country,SUM(b.`quantity_shipped`-b.`quantity_received`) AS tranFba FROM psi_fba_inbound a ,psi_fba_inbound_item b,(SELECT CONCAT(e.`product_name`,CASE  WHEN e.`color`='' THEN '' ELSE CONCAT('_',e.color) END) AS productName,e.`sku`,e.`country` FROM psi_sku e WHERE e.`del_flag`='0')c "+  
				  " WHERE a.`id` = b.`fba_inbound_id` AND ship_from_address IN ('US','DE') AND a.shipped_date IS NOT NULL  AND a.`country` = c.country AND b.`sku` = c.sku AND a.`shipment_status` IN ('SHIPPED','IN_TRANSIT','DELIVERED','CHECKED_IN','RECEIVING') AND b.`quantity_shipped`>b.`quantity_received` GROUP BY c.productName,c.country  HAVING tranFba >0  ";
		
		List<Object[]> fabList=psiTransportForecastOrderDao.findBySql(fbaTran);
		for (Object[] obj: fabList) {
			String name=obj[0].toString();
			String country=obj[1].toString();
			Integer quantity=Integer.parseInt(obj[2].toString());
			map.put(name+"_"+country, quantity);
			if("fr,uk,es,it,de".contains(obj[1].toString())){
				Integer euTemp=map.get(name+"_eu");
				map.put(name+"_eu", quantity+(euTemp==null?0:euTemp));
			}
		}		
		return map;
	}
	
	//name_country,到达日期 ,'1'
	public Map<String,Map<Date,Integer>> getTransportQuantity(){
		Map<String,Map<Date,Integer>> map=Maps.newHashMap();
		String sql="select r.`transport_type`,r.`pick_up_date`,r.`country_code`,r.NAME,sum(r.quantity),r.model,r.dates from "+
			" (SELECT r.`transport_type`,r.`pick_up_date`,t.`country_code`,CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) NAME,SUM(t.`quantity`) quantity,r.model,r.oper_arrival_date dates "+
			" FROM psi_transport_order r JOIN psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' and t.offline_sta='0' "+
			" WHERE r.transport_sta IN ('0','1','2','3') AND r.`transport_type` IN ('0') "+
			" GROUP BY  r.`transport_type`,r.`pick_up_date`,t.`country_code`,NAME,r.model,dates "+
			" union all SELECT r.`transport_type`,r.`pick_up_date`,t.`country_code`,CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) NAME,SUM(t.`quantity`) quantity,r.model,r.oper_arrival_date dates "+
			" FROM lc_psi_transport_order r JOIN lc_psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' and t.offline_sta='0' "+
			" WHERE r.transport_sta IN ('0','1','2','3') AND r.`transport_type` IN ('0') "+
			" GROUP BY  r.`transport_type`,r.`pick_up_date`,t.`country_code`,NAME,r.model,dates) r  GROUP BY  r.`transport_type`,r.`pick_up_date`,r.`country_code`,r.NAME,r.model,r.dates ";
		List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql);
		for (Object[] obj: list) {
		//	String type=obj[0].toString();
			Date pickUpDate=(Date)obj[1];
			String country=obj[2].toString();
			String name=obj[3].toString();
			Integer quantity=Integer.parseInt(obj[4].toString());
			String model=obj[5].toString();
			Date deliveryDate=(Date)obj[6];
			if(pickUpDate==null&&deliveryDate==null){
				continue;
			}
			if(deliveryDate!=null){
				pickUpDate=deliveryDate;
			}else{
				if("0".equals(model)){//air
					pickUpDate=DateUtils.addDays(pickUpDate,PsiConfig.get(country).getFbaBySky());
				}else if("1".equals(model)){//sea
					pickUpDate=DateUtils.addDays(pickUpDate,PsiConfig.get(country).getFbaBySea());
				}else{
					pickUpDate=DateUtils.addDays(pickUpDate,PsiConfig.get(country).getFbaByExpress());
				}
			}
			
			Map<Date,Integer> temp=map.get(name+"_"+country);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(name+"_"+country, temp);
			}
			
			Integer totalQuantity=temp.get(pickUpDate);
			temp.put(pickUpDate, quantity+(totalQuantity==null?0:totalQuantity));
			
			if("de,uk,fr,it,es".contains(country)){
				Map<Date,Integer> euTemp=map.get(name+"_eu");
				if(euTemp==null){
					euTemp=Maps.newHashMap();
					map.put(name+"_eu", euTemp);
				}
				Integer totalQuantityEu=euTemp.get(pickUpDate);
				euTemp.put(pickUpDate, quantity+(totalQuantityEu==null?0:totalQuantityEu));
			}
			if("de,fr,it,es".contains(country)){
				Map<Date,Integer> euTemp=map.get(name+"_four");
				if(euTemp==null){
					euTemp=Maps.newHashMap();
					map.put(name+"_four", euTemp);
				}
				Integer totalQuantityEu=euTemp.get(pickUpDate);
				euTemp.put(pickUpDate, quantity+(totalQuantityEu==null?0:totalQuantityEu));
			}
		}
		return map;
	}
	
	//name_country,到达日期 ,'1'
		public Map<String,Map<Date,Integer>> getFBATransportQuantity(){
			Map<String,Map<Date,Integer>> map=Maps.newHashMap();
			String sql="select r.`transport_type`,r.`pick_up_date`,r.`country_code`,r.NAME,SUM(r.`quantity`),r.model,r.dates  from "+
				" (SELECT r.`transport_type`,r.`pick_up_date`,t.`country_code`,CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) NAME,SUM(t.`quantity`) quantity,r.model,r.oper_arrival_date dates "+
				" FROM psi_transport_order r JOIN psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' and t.offline_sta='0' "+
				" WHERE r.transport_sta IN ('0','1','2','3') AND r.`transport_type` IN ('1') and r.from_store in ('130','21') "+
				" GROUP BY  r.`transport_type`,r.`pick_up_date`,t.`country_code`,NAME,r.model,dates "+
				" union all SELECT r.`transport_type`,r.`pick_up_date`,t.`country_code`,CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) NAME,SUM(t.`quantity`) quantity,r.model,r.oper_arrival_date dates "+
				" FROM lc_psi_transport_order r JOIN lc_psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' and t.offline_sta='0' "+
				" WHERE r.transport_sta IN ('0','1','2','3') AND r.`transport_type` IN ('1')  and r.from_store in ('130','21') "+
				" GROUP BY  r.`transport_type`,r.`pick_up_date`,t.`country_code`,NAME,r.model,dates) r GROUP BY  r.`transport_type`,r.`pick_up_date`,r.`country_code`,r.NAME,r.model,r.dates ";
			List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql);
			for (Object[] obj: list) {
			//	String type=obj[0].toString();
				Date pickUpDate=(Date)obj[1];
				
				String country=obj[2].toString();
				String name=obj[3].toString();
				Integer quantity=Integer.parseInt(obj[4].toString());
				String model=obj[5].toString();
				Map<Date,Integer> temp=map.get(name+"_"+country);
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(name+"_"+country, temp);
				}
				
				Date deliveryDate=(Date)obj[6];
				if(pickUpDate==null&&deliveryDate==null){
					continue;
				}
				if(deliveryDate!=null){
					pickUpDate=deliveryDate;
				}else{
					if("0".equals(model)){//air
						pickUpDate=DateUtils.addDays(pickUpDate,PsiConfig.get(country).getFbaBySky());
					}else if("1".equals(model)){//sea
						pickUpDate=DateUtils.addDays(pickUpDate,PsiConfig.get(country).getFbaBySea());
					}else{
						pickUpDate=DateUtils.addDays(pickUpDate,PsiConfig.get(country).getFbaByExpress());
					}
				}
				Integer totalQuantity=temp.get(pickUpDate);
				temp.put(pickUpDate, quantity+(totalQuantity==null?0:totalQuantity));
				
				if("de,uk,fr,it,es".contains(country)){
					Map<Date,Integer> euTemp=map.get(name+"_eu");
					if(euTemp==null){
						euTemp=Maps.newHashMap();
						map.put(name+"_eu", euTemp);
					}
					Integer totalQuantityEu=euTemp.get(pickUpDate);
					euTemp.put(pickUpDate, quantity+(totalQuantityEu==null?0:totalQuantityEu));
				}
				if("de,fr,it,es".contains(country)){
					Map<Date,Integer> euTemp=map.get(name+"_four");
					if(euTemp==null){
						euTemp=Maps.newHashMap();
						map.put(name+"_four", euTemp);
					}
					Integer totalQuantityEu=euTemp.get(pickUpDate);
					euTemp.put(pickUpDate, quantity+(totalQuantityEu==null?0:totalQuantityEu));
				}
			}
			return map;
		}
		
		//国家-model-产品-数量
		public Map<String,Map<String,Map<String,Object[]>>> findPackQuantityByModelCountry(Integer id,String type){
			Map<String,Map<String,Map<String,Object[]>>> map=Maps.newHashMap();
			DetachedCriteria dc = psiTransportForecastOrderItemDao.createDetachedCriteria();
		    dc.createAlias("psiTransportForecastOrder", "order");
			dc.add(Restrictions.eq("order.id",id));
			dc.add(Restrictions.ne("displaySta","1"));
			dc.add(Restrictions.eq("transSta",type));
			dc.add(Restrictions.gt("checkQuantity",0));
			List<PsiTransportForecastOrderItem> tempList=psiTransportForecastOrderItemDao.find(dc);
			if(tempList!=null&&tempList.size()>0){
				for (PsiTransportForecastOrderItem item : tempList) {
					Map<String,Map<String,Object[]>> modelMap=map.get(item.getCountryCode());
					if(modelMap==null){
						modelMap=Maps.newHashMap();
						map.put(item.getCountryCode(),modelMap);
					}
					Map<String,Object[]> countryMap=modelMap.get(item.getModel());
					if(countryMap==null){
						countryMap=Maps.newHashMap();
						modelMap.put(item.getModel(),countryMap);
					}
					Object[] obj=countryMap.get(item.getProductNameColor());
					Integer quantity=0;
					String remark="";
					if(obj!=null){
						quantity=Integer.parseInt(obj[0]==null?"0":obj[0].toString());
						remark=(obj[1]==null?"":obj[1].toString());
					}
					
					Object[] newObj=new Object[2];
					Integer newQuantity=quantity+item.getCheckQuantity();
					String newRemark=(StringUtils.isNotBlank(item.getRemark())?(item.getRemark()+";"):"")+remark;
					newObj[0]=newQuantity;
					newObj[1]=newRemark;
					countryMap.put(item.getProductNameColor(),newObj);
				}
			}
			return map;
		}
		
		public String findErrorData(Integer id){
			StringBuilder returnStr=new StringBuilder();
			String sql="SELECT CONCAT(t.`product_name`,CASE  WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END) NAME, "+
			" SUM(quantity) quantity,SUM(check_quantity) check_quantity FROM psi_forecast_transport_order_item t WHERE t.`forecast_order_id`=:p1  AND display_sta!='1' "+
			"  GROUP BY NAME HAVING check_quantity > quantity ";
			List<Object[]> list=psiTransportForecastOrderItemDao.findBySql(sql,new Parameter(id));
			if(list!=null&&list.size()>0){
				for (Object[] obj: list) {
					returnStr.append(obj[0].toString()).append(",系统数量：").append(obj[1].toString()).append(",审核数量：").append(obj[2].toString()).append("\n");
				}
			}
			return returnStr.toString();
		}
		
		public Map<String,Map<String,Integer>> findCheckQuantity(Integer id){
			Map<String,Map<String,Integer>> map=Maps.newHashMap();
			String sql="SELECT CONCAT(t.`product_name`,CASE  WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END) NAME, "+
					" SUM(check_quantity) check_quantity,t.trans_sta FROM psi_forecast_transport_order_item t WHERE t.`forecast_order_id`=:p1  AND display_sta!='1' "+
					"  GROUP BY NAME,t.trans_sta HAVING check_quantity > 0 ";
			List<Object[]> list=psiTransportForecastOrderItemDao.findBySql(sql,new Parameter(id));
			if(list!=null&&list.size()>0){
				for (Object[] obj: list) {
					Map<String,Integer> temp=map.get(obj[2].toString());
					if(temp==null){
						temp=Maps.newHashMap();
						map.put(obj[2].toString(),temp);
					}
					temp.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
				}
			}
			return map;
		}
		
		public Map<String,Integer> findCheckQuantity(Set<String> idSet){
			Map<String,Integer> map=Maps.newHashMap();
			String sql="SELECT t.`product_name` NAME,SUM(quantity) quantity FROM psi_forecast_transport_plan_order t WHERE t.`id` in :p1 and quantity>0 "+
					" GROUP BY NAME ";
			List<Object[]> list=psiTransportForecastPlanOrderDao.findBySql(sql,new Parameter(idSet));
			if(list!=null&&list.size()>0){
				for (Object[] obj: list) {
					map.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
				}
			}
			return map;
		}
		
		public Map<String,Integer> findCnStockByName(Set<String> name,Date date){
			Map<String,Integer> map=Maps.newHashMap();
			String sql="SELECT CONCAT(p.`product_name`,CASE  WHEN p.color_code='' THEN '' ELSE CONCAT('_',p.color_code) END ) NAME,SUM(new_quantity) FROM psi_inventory p WHERE p.`warehouse_id` ='21' "+
			        " and CONCAT(p.`product_name`,CASE  WHEN p.color_code='' THEN '' ELSE CONCAT('_',p.color_code) END ) in :p1 "+
		            " GROUP BY name HAVING SUM(new_quantity)>0 ";
			List<Object[]> list=psiTransportForecastOrderItemDao.findBySql(sql,new Parameter(name));
			if(list!=null&&list.size()>0){
				for (Object[] obj: list) {
					map.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
				}
			}
			findPOInventoryProduct(map,name,date);
			findNewProductByName(map,name,date);
			return map;
		}
		
		public Map<String,Integer> findCnStockByName2(Set<String> name,Date date){
			Map<String,Integer> map=Maps.newHashMap();
			String sql="SELECT CONCAT(p.`product_name`,CASE  WHEN p.color_code='' THEN '' ELSE CONCAT('_',p.color_code) END ) NAME,SUM(new_quantity) FROM psi_inventory p WHERE p.`warehouse_id` ='130' "+
			        " and CONCAT(p.`product_name`,CASE  WHEN p.color_code='' THEN '' ELSE CONCAT('_',p.color_code) END ) in :p1 "+
		            " GROUP BY name HAVING SUM(new_quantity)>0 ";
			List<Object[]> list=psiTransportForecastOrderItemDao.findBySql(sql,new Parameter(name));
			if(list!=null&&list.size()>0){
				for (Object[] obj: list) {
					map.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
				}
			}
			findPOInventoryProduct2(map,name,date);
			findNewProductByName2(map,name,date);
			return map;
		}
		
		
		public Map<String,Integer> findPOInventoryProduct(Map<String,Integer>  map,Set<String> name,Date date){
			//查询分批收货的订单item
			List<Integer> orderItemIds = this.purchaseOrderService.getOrderItemFromDelivery(null,null);
			String sql="";
			List<Object[]> list=null;
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
			Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
			if(orderItemIds!=null&&orderItemIds.size()>0){
				sql=" SELECT CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) NAME, "+
						" SUM(t.`quantity_ordered`-t.`quantity_off_ordered`-(t.`quantity_received`-t.`quantity_off_received`)) quantity "+ 
						" FROM psi_purchase_order r JOIN psi_purchase_order_item t ON r.id=t.`purchase_order_id` AND t.`del_flag`='0' AND t.id NOT IN :p1 "+
						" WHERE r.`order_sta` IN ('2','3') AND t.`actual_delivery_date`>=:p2 "+
						" AND t.`actual_delivery_date`<=:p3 and  CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) in :p4 "+
						" GROUP BY NAME HAVING quantity>0 ";
				list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(orderItemIds,format.format(start),format.format(end),name));
			}else{
				sql=" SELECT CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) NAME, "+
						" SUM(t.`quantity_ordered`-t.`quantity_off_ordered`-(t.`quantity_received`-t.`quantity_off_received`)) quantity "+ 
						" FROM psi_purchase_order r JOIN psi_purchase_order_item t ON r.id=t.`purchase_order_id` AND t.`del_flag`='0' "+
						" WHERE r.`order_sta` IN ('2','3') AND t.`actual_delivery_date`>=:p1 "+
						" AND t.`actual_delivery_date`<=:p2 and  CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) in :p3  "+
						" GROUP BY NAME HAVING quantity>0 ";
				list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(format.format(start),format.format(end),name));
			}
			if(list!=null&&list.size()>0){
				for (Object[] obj: list) {
					String pname=obj[0].toString();
					Integer quantity=Integer.parseInt(obj[1].toString());
					if(map.get(pname)==null){
						map.put(pname, quantity);
					}else{
						Integer tempQuantity=map.get(pname);
						map.put(pname, tempQuantity+quantity);
					}
				}
			}	
			//获得分批收货的产品数量
			if(orderItemIds!=null&&orderItemIds.size()>0){
				this.purchaseOrderService.getDeliveryInfosByName(map,name,date);
			}
			return map;
		}
		
		public Map<String,Integer> findPOInventoryProduct2(Map<String,Integer>  map,Set<String> name,Date date){
			//查询分批收货的订单item
			List<Integer> orderItemIds = this.purchaseOrderService.getOrderItemFromDelivery2(null,null);
			String sql="";
			List<Object[]> list=null;
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
			Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
			if(orderItemIds!=null&&orderItemIds.size()>0){
				sql=" SELECT CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) NAME, "+
						" SUM(t.`quantity_ordered`-t.`quantity_off_ordered`-(t.`quantity_received`-t.`quantity_off_received`)) quantity "+ 
						" FROM lc_psi_purchase_order r JOIN lc_psi_purchase_order_item t ON r.id=t.`purchase_order_id` AND t.`del_flag`='0' AND t.id NOT IN :p1 "+
						" WHERE r.`order_sta` IN ('2','3') AND t.`actual_delivery_date`>=:p2 "+
						" AND t.`actual_delivery_date`<=:p3 and  CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) in :p4 "+
						" GROUP BY NAME HAVING quantity>0 ";
				list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(orderItemIds,format.format(start),format.format(end),name));
			}else{
				sql=" SELECT CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) NAME, "+
						" SUM(t.`quantity_ordered`-t.`quantity_off_ordered`-(t.`quantity_received`-t.`quantity_off_received`)) quantity "+ 
						" FROM lc_psi_purchase_order r JOIN lc_psi_purchase_order_item t ON r.id=t.`purchase_order_id` AND t.`del_flag`='0' "+
						" WHERE r.`order_sta` IN ('2','3') AND t.`actual_delivery_date`>=:p1 "+
						" AND t.`actual_delivery_date`<=:p2 and  CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) in :p3  "+
						" GROUP BY NAME HAVING quantity>0 ";
				list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(format.format(start),format.format(end),name));
			}
			if(list!=null&&list.size()>0){
				for (Object[] obj: list) {
					String pname=obj[0].toString();
					Integer quantity=Integer.parseInt(obj[1].toString());
					if(map.get(pname)==null){
						map.put(pname, quantity);
					}else{
						Integer tempQuantity=map.get(pname);
						map.put(pname, tempQuantity+quantity);
					}
				}
			}	
			//获得分批收货的产品数量
			if(orderItemIds!=null&&orderItemIds.size()>0){
				this.purchaseOrderService.getDeliveryInfosByName2(map,name,date);
			}
			return map;
		}
		
		public Map<String,Integer> findNewProductByName(Map<String,Integer> map,Set<String> name,Date date){
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
			Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
			
			String sql="SELECT CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) NAME,SUM(t.`quantity`) "+
					" FROM psi_transport_order r JOIN psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' and t.offline_sta='0' "+
					" WHERE r.transport_sta IN ('0') AND r.`transport_type` IN ('0','1') "+
					" and r.`pick_up_date`>=:p1 "+
					" and r.`pick_up_date`<=:p2 and CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) in :p3 "+
					" AND r.`orgin` IN ('SZX','HKG','CSX') GROUP BY name ";
			List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(format.format(start),format.format(end),name));
			if(list!=null&&list.size()>0){
				for (Object[] obj: list) {
					String pname=obj[0].toString();
					Integer quantity=Integer.parseInt(obj[1].toString());
					Integer temp=map.get(pname)-quantity;
					map.put(obj[0].toString(),temp>0?temp:0);
				}
			}	
			return map;
		}
		
		
		
		public Map<String,Integer> findNewProductByName2(Map<String,Integer> map,Set<String> name,Date date){
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
			Date start=com.springrain.erp.common.utils.DateUtils.getMonday(date);
			Date end=com.springrain.erp.common.utils.DateUtils.getSunday(date);
			
			String sql="SELECT CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) NAME,SUM(t.`quantity`) "+
					" FROM lc_psi_transport_order r JOIN lc_psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' and t.offline_sta='0' "+
					" WHERE r.transport_sta IN ('0') AND r.`transport_type` IN ('0','1') "+
					" and r.`pick_up_date`>=:p1 "+
					" and r.`pick_up_date`<=:p2 and CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) in :p3 "+
					" AND r.`orgin` IN ('SZX','HKG','CSX') GROUP BY name ";
			List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(format.format(start),format.format(end),name));
			if(list!=null&&list.size()>0){
				for (Object[] obj: list) {
					String pname=obj[0].toString();
					Integer quantity=Integer.parseInt(obj[1].toString());
					Integer temp=map.get(pname)-quantity;
					map.put(obj[0].toString(),temp>0?temp:0);
				}
			}	
			return map;
		}
		
		//name country sku quantity
		public Map<String,Map<Date,Map<String,Integer>>> getPurchaseOrder(Map<String, PsiProductEliminate> attrMap,Map<String,String> skuMap){
			Map<String,Map<Date,Map<String,Integer>>> map=Maps.newLinkedHashMap();
			Date start=com.springrain.erp.common.utils.DateUtils.getMonday(new Date());
			//查询分批收货的订单item
			List<Integer> orderItemIds = this.purchaseOrderService.getOrderItemFromDelivery(null,null,start);
			
			String sql="";
			List<Object[]> list=null;
			
			if(orderItemIds!=null&&orderItemIds.size()>0){
				sql=" SELECT t.`country_code`,CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) NAME, "+
						" SUM(t.`quantity_ordered`-t.`quantity_off_ordered`-(t.`quantity_received`-t.`quantity_off_received`)) quantity,t.`actual_delivery_date` "+ 
						" FROM lc_psi_purchase_order r JOIN lc_psi_purchase_order_item t ON r.id=t.`purchase_order_id` AND t.`del_flag`='0' AND t.id NOT IN :p1 "+
						" WHERE r.`order_sta` IN ('0','1','2','3') AND t.`actual_delivery_date`>=:p2 "+
						" GROUP BY t.`country_code`,NAME HAVING quantity>0 ";
				list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(orderItemIds,start));
			}else{
				sql=" SELECT t.`country_code`,CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) NAME, "+
						" SUM(t.`quantity_ordered`-t.`quantity_off_ordered`-(t.`quantity_received`-t.`quantity_off_received`)) quantity,t.`actual_delivery_date` "+ 
						" FROM lc_psi_purchase_order r JOIN lc_psi_purchase_order_item t ON r.id=t.`purchase_order_id` AND t.`del_flag`='0' "+
						" WHERE r.`order_sta` IN ('0','1','2','3') AND t.`actual_delivery_date`>=:p1 "+
						" GROUP BY t.`country_code`,NAME HAVING quantity>0 ";
				list=psiTransportForecastOrderDao.findBySql(sql,new Parameter(start));
			}
			for (Object[] obj: list) {
					String name=obj[1].toString();
					String country=obj[0].toString();
					Integer quantity=Integer.parseInt(obj[2].toString());
					Date date=(Date)obj[3];
					System.out.println(name+country);
					String sku=skuMap.get(name+"_"+country);
					//没绑定sku的不算
					if(StringUtils.isEmpty(sku)&&"新品".equals(attrMap.get(name+"_"+country).getIsNew())){//产品名_颜色_国   1新品
						sku=name+"_"+country;
					}
					if(StringUtils.isEmpty(sku)){
						continue;
					}
					Map<Date,Map<String,Integer>> temp=map.get(name+"_"+country);
					if(temp==null){
						temp=Maps.newLinkedHashMap();
						map.put(name+"_"+country, temp);
					}
					Map<String,Integer> countryTemp=temp.get(date);
					if(countryTemp==null){
						countryTemp=Maps.newLinkedHashMap();
						temp.put(date, countryTemp);
					}
					Integer skuQuantity=countryTemp.get(sku);
					countryTemp.put(sku, quantity+(skuQuantity==null?0:skuQuantity));
					Integer tempQuantity=countryTemp.get("total");
					countryTemp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
			}
			//获得分批收货的产品数量
			if(orderItemIds!=null&&orderItemIds.size()>0){
				this.purchaseOrderService.getPODeliveryInfos(map, skuMap,attrMap);
			}
			return map;
		}
		
		
		
		public Map<String,Map<Date,Map<String,Integer>>> getAllTransportQuantity(){
			Map<String,Map<Date,Map<String,Integer>>> resultMap=Maps.newHashMap();
			String sql="select r.`transport_type`,r.`pick_up_date`,r.`country_code`,r.NAME,SUM(r.`quantity`),r.model,r.dates  from "+
				" (SELECT r.`transport_type`,r.`pick_up_date`,t.`country_code`,CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) NAME,SUM(t.`quantity`) quantity,r.model,r.oper_arrival_date dates "+
				" FROM psi_transport_order r JOIN psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' and t.offline_sta='0' "+
				" WHERE r.transport_sta IN ('0','1','2','3') AND (r.`transport_type`='0' or (r.`transport_type`='1' and r.from_store in ('130','21')) ) "+
				" GROUP BY  r.`transport_type`,r.`pick_up_date`,t.`country_code`,NAME,r.model,dates "+
				" union all SELECT r.`transport_type`,r.`pick_up_date`,t.`country_code`,CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE  CONCAT('_',t.`color_code`) END) NAME,SUM(t.`quantity`) quantity,r.model,r.oper_arrival_date dates "+
				" FROM lc_psi_transport_order r JOIN lc_psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' and t.offline_sta='0' "+
				" WHERE r.transport_sta IN ('0','1','2','3') AND (r.`transport_type`='0' or (r.`transport_type`='1' and r.from_store in ('130','21')) ) "+
				" GROUP BY  r.`transport_type`,r.`pick_up_date`,t.`country_code`,NAME,r.model,dates) r GROUP BY  r.`transport_type`,r.`pick_up_date`,r.`country_code`,r.NAME,r.model,r.dates ";
			List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql);
			for (Object[] obj: list) {
				String type=obj[0].toString();
				Date pickUpDate=(Date)obj[1];
				Date deliveryDate=(Date)obj[6];
				if(pickUpDate==null&&deliveryDate==null){
					continue;
				}
				String country=obj[2].toString();
				String name=obj[3].toString();
				Integer quantity=Integer.parseInt(obj[4].toString());
				String model=obj[5].toString();
				Map<Date,Map<String,Integer>> map=resultMap.get(name+"_"+country);
				if(map==null){
					map=Maps.newHashMap();
					resultMap.put(name+"_"+country, map);
				}
				
				if(deliveryDate!=null){
					pickUpDate=deliveryDate;
				}else{
					if("0".equals(model)){//air
						pickUpDate=DateUtils.addDays(pickUpDate,PsiConfig.get(country).getFbaBySky());
					}else if("1".equals(model)){//sea
						pickUpDate=DateUtils.addDays(pickUpDate,PsiConfig.get(country).getFbaBySea());
					}else{
						pickUpDate=DateUtils.addDays(pickUpDate,PsiConfig.get(country).getFbaByExpress());
					}
				}
				
				Map<String,Integer> temp=map.get(pickUpDate);
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(pickUpDate, temp);
				}
				
				Integer totalQuantity=temp.get(type);
				temp.put(type, quantity+(totalQuantity==null?0:totalQuantity));
				
				if("de,uk,fr,it,es".contains(country)){
					Map<Date,Map<String,Integer>> euMap=resultMap.get(name+"_eu");
					if(euMap==null){
						euMap=Maps.newHashMap();
						resultMap.put(name+"_eu", euMap);
					}
					Map<String,Integer> euTemp=euMap.get(pickUpDate);
					if(euTemp==null){
						euTemp=Maps.newHashMap();
						euMap.put(pickUpDate, euTemp);
					}
					Integer totalQuantityEu=euTemp.get(type);
					euTemp.put(type, quantity+(totalQuantityEu==null?0:totalQuantityEu));
				}
				if("de,fr,it,es".contains(country)){
					Map<Date,Map<String,Integer>> euMap=resultMap.get(name+"_euNoUk");
					if(euMap==null){
						euMap=Maps.newHashMap();
						resultMap.put(name+"_euNoUk", euMap);
					}
					Map<String,Integer> euTemp=euMap.get(pickUpDate);
					if(euTemp==null){
						euTemp=Maps.newHashMap();
						euMap.put(pickUpDate, euTemp);
					}
					Integer totalQuantityEu=euTemp.get(type);
					euTemp.put(type, quantity+(totalQuantityEu==null?0:totalQuantityEu));
				}
			}
			
			//type:1
			String fbaTranSql="SELECT c.productName,a.`shipped_date`,a.ship_from_address,a.arrival_date,SUM(CASE  WHEN a.country IN ('de','fr','uk') AND a.`arrival_date` IS NOT NULL AND a.`arrival_date` <= DATE_ADD(CURDATE(),INTERVAL -30 DAY) THEN 0 ELSE (b.`quantity_shipped`-b.`quantity_received`) END ) AS tranFba FROM psi_fba_inbound a ,psi_fba_inbound_item b,(SELECT CONCAT(e.`product_name`,CASE  WHEN e.`color`='' THEN '' ELSE CONCAT('_',e.color) END ,'_',e.`country`) AS productName,e.`sku`,e.`country` FROM psi_sku e WHERE e.`del_flag`='0' )c " +
					" WHERE a.`id` = b.`fba_inbound_id` and a.shipped_date is not null AND a.`country` = c.country AND b.`sku` = c.sku AND ( ( a.`shipment_status` IN ('SHIPPED','IN_TRANSIT','DELIVERED','CHECKED_IN','RECEIVING')  AND a.`ship_from_address`!='CN') or (a.`shipment_status` ='RECEIVING'  AND a.`ship_from_address`='CN')    ) AND b.`quantity_shipped`>b.`quantity_received`  GROUP BY c.productName,a.`shipped_date`,a.ship_from_address,a.arrival_date  HAVING tranFba >0 ";
			
			List<Object[]> fbaList=psiTransportForecastOrderDao.findBySql(fbaTranSql);
			for (Object[] obj: fbaList) {
				String nameWithCountry=obj[0].toString();
				String name=nameWithCountry.substring(0,nameWithCountry.lastIndexOf("_"));
				String country=nameWithCountry.substring(nameWithCountry.lastIndexOf("_")+1);
				
				Date pickUpDate=(Date)obj[1];
				String fromAddr=obj[2].toString();
				Date arrivalDate=(Date)obj[3];
				Integer quantity=Integer.parseInt(obj[4].toString());
				if("CN".equals(fromAddr)&&arrivalDate==null){
					continue;
				}
				if(arrivalDate!= null){
					pickUpDate=arrivalDate;
				}else{
					pickUpDate=DateUtils.addDays(pickUpDate,3);
				}
				
				Map<Date,Map<String,Integer>> map=resultMap.get(nameWithCountry);
				if(map==null){
					map=Maps.newHashMap();
					resultMap.put(nameWithCountry, map);
				}
				Map<String,Integer> temp=map.get(pickUpDate);
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(pickUpDate, temp);
				}
				
				Integer totalQuantity=temp.get("1");
				temp.put("1", quantity+(totalQuantity==null?0:totalQuantity));
				
				
				if("de,uk,fr,it,es".contains(country)){
					Map<Date,Map<String,Integer>> euMap=resultMap.get(name+"_eu");
					if(euMap==null){
						euMap=Maps.newHashMap();
						resultMap.put(name+"_eu", euMap);
					}
					Map<String,Integer> euTemp=euMap.get(pickUpDate);
					if(euTemp==null){
						euTemp=Maps.newHashMap();
						euMap.put(pickUpDate, euTemp);
					}
					Integer totalQuantityEu=euTemp.get("1");
					euTemp.put("1", quantity+(totalQuantityEu==null?0:totalQuantityEu));
				}
				
				if("de,fr,it,es".contains(country)){
					Map<Date,Map<String,Integer>> euMap=resultMap.get(name+"_euNoUk");
					if(euMap==null){
						euMap=Maps.newHashMap();
						resultMap.put(name+"_euNoUk", euMap);
					}
					Map<String,Integer> euTemp=euMap.get(pickUpDate);
					if(euTemp==null){
						euTemp=Maps.newHashMap();
						euMap.put(pickUpDate, euTemp);
					}
					Integer totalQuantityEu=euTemp.get("1");
					euTemp.put("1", quantity+(totalQuantityEu==null?0:totalQuantityEu));
				}
			}	
			return resultMap;
		}
		
		
		public Map<String,Map<String,Integer>> getCnStockProduct(){
			String sql="SELECT p.sku,CONCAT(p.`product_name`,CASE  WHEN p.color_code='' THEN '' ELSE CONCAT('_',p.color_code) END ) NAME,p.`country_code`,SUM(new_quantity) FROM psi_inventory p WHERE p.`warehouse_id`='130' "+
	            " GROUP BY p.sku,name,p.`country_code` HAVING SUM(new_quantity)>0 ";
			List<Object[]> list=psiTransportForecastOrderDao.findBySql(sql);
			Map<String,Map<String,Integer>> map=Maps.newLinkedHashMap();
			for (Object[] obj: list) {
				String sku=obj[0].toString();
				String name=obj[1].toString();
				String country=obj[2].toString();
				Integer quantity=Integer.parseInt(obj[3].toString());
				Map<String,Integer> temp=map.get(name+"_"+country);
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					map.put(name+"_"+country, temp);
				}
				temp.put(sku, quantity);
				Integer tempQuantity=temp.get("total");
				temp.put("total", quantity+(tempQuantity==null?0:tempQuantity));
			}
			return map;
		}
		
		
		@Transactional(readOnly = false)
		public void saveTransData(Set<Integer> ids) throws IOException{
			List<LcPsiTransportOrder> lcPsiTransportOrderList=Lists.newArrayList();
			Map<Integer, String>  productMap=psiProductService.getVomueAndWeight();
			//国家-运输类型-空海运
			 Map<String,Map<String,Map<String,List<PsiTransportForecastPlanOrder>>>>  map= getTransportItem(ids); 
			
			Map<String,Integer> packQuantityMap=psiProductService.getPackQuantity();
			
			if(map!=null&&map.size()>0){
				genLcTrans(lcPsiTransportOrderList,map,productMap,packQuantityMap);
			}
			
			if(lcPsiTransportOrderList!=null&&lcPsiTransportOrderList.size()>0){
				lcPsiTransportOrderService.saveList(lcPsiTransportOrderList);
			}
			
			String sql="update psi_forecast_transport_plan_order set state='1' where id in :p1 ";
			psiTransportForecastPlanOrderDao.updateBySql(sql, new Parameter(ids));
		}
		
		public List<PsiTransportForecastPlanOrder> findPlanOrder(Set<Integer> ids){
			    DetachedCriteria dc = psiTransportForecastPlanOrderDao.createDetachedCriteria();
				dc.add(Restrictions.in("id",ids));
				dc.add(Restrictions.eq("state","0"));
				dc.add(Restrictions.eq("delFlag","0"));
				dc.add(Restrictions.gt("quantity",0));
				return psiTransportForecastPlanOrderDao.find(dc);
		}
		
		public  Map<String,Map<String,Map<String,List<PsiTransportForecastPlanOrder>>>> getTransportItem(Set<Integer> ids) {
			 Map<String,Map<String,Map<String,List<PsiTransportForecastPlanOrder>>>> map=Maps.newHashMap();
			 DetachedCriteria dc = psiTransportForecastPlanOrderDao.createDetachedCriteria();
				dc.add(Restrictions.in("id",ids));
				dc.add(Restrictions.eq("state","0"));
				dc.add(Restrictions.eq("delFlag","0"));
				dc.add(Restrictions.gt("quantity",0));
				List<PsiTransportForecastPlanOrder> tempList=psiTransportForecastPlanOrderDao.find(dc);
				if(tempList!=null&&tempList.size()>0){
					
					for (PsiTransportForecastPlanOrder item : tempList) {
						
						Map<String,Map<String,List<PsiTransportForecastPlanOrder>>> temp=map.get(item.getCountryCode());
						if(temp==null){
							temp=Maps.newHashMap();
							map.put(item.getCountryCode(), temp);
						}
						Map<String,List<PsiTransportForecastPlanOrder>> countryMap=temp.get(item.getTransportType());
						if(countryMap==null){
							countryMap=Maps.newHashMap();
							temp.put(item.getTransportType(), countryMap);
						}
						List<PsiTransportForecastPlanOrder> list=countryMap.get(item.getModel());
						if(list==null){
							list=Lists.newArrayList();
							countryMap.put(item.getModel(),list);
						}
						if(list.size()>0){
							boolean flag=true;
							for (PsiTransportForecastPlanOrder orderItem : list) {
								if(orderItem.getProductName().equals(item.getProductName())&&orderItem.getSku().equals(item.getSku())){
									orderItem.setQuantity(orderItem.getQuantity()+item.getQuantity());
									flag=false;
								}
							}
							if(flag){
								item.setQuantity(item.getQuantity());
								list.add(item);
							}
						}else{
							item.setQuantity(item.getQuantity());
							list.add(item);
						}
					}
				}
				
			 return map;
		}
	
		
		public void genLcTrans(List<LcPsiTransportOrder> psiTransportOrderList, Map<String,Map<String,Map<String,List<PsiTransportForecastPlanOrder>>>>  map,Map<Integer, String>  productMap,Map<String,Integer> packQuantityMap) throws IOException{
			List<Stock> toStock = this.stockService.findStocks("2");
			Set<String> countrySet=map.keySet();
			Set<String> euCountrySet=Sets.newHashSet();
			Map<String,Map<String,PsiProductEliminate>> eliminateMap = psiProductEliminateService.findAllByNameAndCountry();
			for (String country: countrySet) {
				if("de,fr,it,es,uk".contains(country)){
					euCountrySet.add(country);
				}
			}
			Date today=new Date();
			if(euCountrySet!=null&&euCountrySet.size()>0){
				Set<String> typeSet=Sets.newHashSet("0","1");
				Set<String> modelSet=Sets.newHashSet("0","1","2","3");
				for (String type: typeSet) {
					for (String model: modelSet) {
						List<PsiTransportForecastPlanOrder> itemList=Lists.newArrayList();
						for(String country:euCountrySet){
							if(map.get(country)!=null&&map.get(country).get(type)!=null&&map.get(country).get(type).get(model)!=null){
								itemList.addAll(map.get(country).get(type).get(model));
							}
						}
						if(itemList!=null&&itemList.size()>0){
							LcPsiTransportOrder order=new LcPsiTransportOrder();
							order.setFromStore(stockService.get(130));
							for (Stock stock : toStock) {
								if("0".equals(type)){//local
									order.setToStore(stockService.get(19));
								}else{
									if("1".equals(stock.getType())&&"de".equals(stock.getPlatform())){
										order.setToStore(stock);
										break;
									}
								}
							}
							order.setOrgin("SZX");
							if("0".equals(type)){//"本地运输" 德国空运本地运输：LEJ,德国空运FBA运输：FRA,德国海运：HAM
								if("0".equals(model)){//"空运"
									order.setDestination("LEJ");
								}else{//"海运"
									order.setDestination("HAM");
								}
							}else{
								if("0".equals(model)){//"空运"
									order.setDestination("FRA");
								}else{//"海运"
									order.setDestination("HAM");
								}
							}
							//order.setDestination(DictUtils.getDictList("transport_pod").get(0).getValue());
							order.setModel(model);
							order.setTransportType(type);
							order.setTransportSta("0");//草稿状态
							order.setPaymentSta("0");//未付款状态
							order.setCreateDate(new Date());
							order.setCreateUser(UserUtils.getUser());
							order.setToCountry("DE");
							order.setDestinationDetail(null);
							order.setPickUpDate(today);
							Float volume =0f;
							Float weight=0f;
							Integer boxNum=0;
							List<LcPsiTransportOrderItem>	orderItems=Lists.newArrayList();
							for (PsiTransportForecastPlanOrder item : itemList) {
								LcPsiTransportOrderItem tranOrderItem=new LcPsiTransportOrderItem();
								tranOrderItem.setProductName(item.getProductName());
								
								if(item.getProductName().indexOf("_")>0){
									String color =item.getProductName().substring(item.getProductName().lastIndexOf("_")+1);
									tranOrderItem.setColorCode(color);
								}else{
									tranOrderItem.setColorCode("");
								}
								
								tranOrderItem.setCountryCode(item.getCountryCode());
								tranOrderItem.setSku(item.getSku());
								tranOrderItem.setQuantity(item.getQuantity());
								tranOrderItem.setOfflineSta("0");
								String name=item.getProductName();
								
								Integer pack=packQuantityMap.get(name);
								if(name.contains("Inateck DB1001")){
									if("com,uk,jp,ca,mx,".contains(item.getCountryCode())){
										pack=60;
									}else{
										pack=44;
									}
								}else if(name.contains("Inateck DB2001")){
									if("com,jp,ca,mx,".contains(item.getCountryCode())){
										pack=32;
									}else{
										pack=24;
									}
								}
								
								tranOrderItem.setPackQuantity(pack);
								PsiProduct product=psiProductService.findProductByProductName(item.getProductName());
								tranOrderItem.setProduct(product);
								String res =lcPsiTransportOrderService.getProPriceByProductId(product.getId());
//								Float   partsPrice =this.lcPsiTransportOrderService.getPartsPriceByProductId(product.getId(), item.getColorCode());
								Float 	price =0f;
								Float   productPrice=0f;
								String  currency="";
								String countryCode = tranOrderItem.getCountryCode();
								if(StringUtils.isNotEmpty(res)){
									String[] arr=res.split("_");
									price=Float.parseFloat(arr[0].toString());
									Integer taxRefund=(product.getTaxRefund()==null?17:product.getTaxRefund());
									productPrice =Float.parseFloat(arr[0].toString())/(1+(taxRefund/100));
									if("CNY".equals(arr[1])){
										productPrice=productPrice/AmazonProduct2Service.getRateConfig().get("USD/CNY");
										price=price/AmazonProduct2Service.getRateConfig().get("USD/CNY");
									}

									//理诚全部是人民币
									if("it,de,es,fr,uk".contains(countryCode)){
										currency="EUR";
										price=price/AmazonProduct2Service.getRateConfig().get("EUR/USD");
										productPrice=productPrice/AmazonProduct2Service.getRateConfig().get("EUR/USD");
									}else if("com,mx,ca".contains(countryCode)){
										currency="USD";
									}else if("jp".contains(countryCode)){
										currency="JPY";
										price=price*AmazonProduct2Service.getRateConfig().get("USD/JPY");
										productPrice=productPrice*AmazonProduct2Service.getRateConfig().get("USD/JPY");
									}
									
								}

								tranOrderItem.setProductPrice(productPrice);
								tranOrderItem.setItemPrice(price);
								//tranOrderItem.setLowerPrice(lowerPrice);
								//tranOrderItem.setImportPrice(productPrice*priceRate);
								if(eliminateMap.get(item.getProductName())!=null&&eliminateMap.get(item.getProductName()).get(item.getCountryCode())!=null){
									tranOrderItem.setLowerPrice(eliminateMap.get(item.getProductName()).get(item.getCountryCode()).getCnpiPrice());
									tranOrderItem.setImportPrice(eliminateMap.get(item.getProductName()).get(item.getCountryCode()).getPiPrice());
								}
								
								tranOrderItem.setCurrency(currency);
								tranOrderItem.setTransportOrder(order);
								if(StringUtils.isNotBlank(item.getRemark())&&!"ok".equals(item.getRemark().trim())&&!"OK".equals(item.getRemark().trim())&&!"Ok".equals(item.getRemark().trim())){
									tranOrderItem.setRemark(item.getRemark());
								}
								
								orderItems.add(tranOrderItem);
								if(!"1".equals(product.getComponents())){
									volume+=item.getQuantity()*1.0f/pack*(Float.parseFloat(productMap.get(product.getId()).split(",")[0]));
									weight+=item.getQuantity()*1.0f/pack*(Float.parseFloat(productMap.get(product.getId()).split(",")[1]));
									boxNum+=MathUtils.roundUp(item.getQuantity()*1.0d/pack);
								}
							}
							order.setVolume(volume);
							order.setWeight(weight);
							order.setBoxNumber(boxNum);
							order.setItems(orderItems);
							String transportNo =lcPsiTransportOrderService.createFlowNo();
							order.setTransportNo(transportNo);
							psiTransportOrderList.add(order);
						}
						
					}
				}
				countrySet.removeAll(euCountrySet);
			}
				
			if(countrySet!=null&&countrySet.size()>0){//com ca jp
				for (String country: countrySet) {
					Map<String,Map<String,List<PsiTransportForecastPlanOrder>>> countryMap=map.get(country);
//					for(String transportType:countryMap.keySet()){
					for(Map.Entry<String,Map<String,List<PsiTransportForecastPlanOrder>>> entry1:countryMap.entrySet()){
						String transportType = entry1.getKey();
						Map<String,List<PsiTransportForecastPlanOrder>> typeMap=entry1.getValue();
						for(Map.Entry<String,List<PsiTransportForecastPlanOrder>> entry:typeMap.entrySet()){
							String model =entry.getKey();
							List<PsiTransportForecastPlanOrder> itemList=entry.getValue();
							LcPsiTransportOrder order=new LcPsiTransportOrder();
							order.setFromStore(stockService.get(130));
							for (Stock stock : toStock) {
								if("0".equals(transportType)){//local
									order.setToStore(stockService.get(120));
								}else{
									if("1".equals(stock.getType())&&country.equals(stock.getPlatform())){
										order.setToStore(stock);
										break;
									}
								}
							}
							order.setOrgin("SZX");
							//order.setDestination(DictUtils.getDictList("transport_pod").get(0).getValue());
							if("0".equals(transportType)){//"本地运输" 德国空运本地运输：LEJ,德国空运FBA运输：FRA,德国海运：HAM
								if("0".equals(model)){//"空运"
									if("jp".equals(country)){
										order.setDestination("NRT");
									}else if("ca".equals(country)){
										order.setDestination("YYZ");
									}else{
										order.setDestination("DFW");
									}
								}else{//"海运"
	                                if("jp".equals(country)){
	                                	order.setDestination("NGO");
									}else{
										order.setDestination("LAX");
									}
								}
							}else{
								if("0".equals(model)){//"空运"
									if("jp".equals(country)){
										order.setDestination("NRT");
									}else if("ca".equals(country)){
										order.setDestination("YYZ");
									}else{
										order.setDestination("DFW");
									}
								}else{//"海运"
	                                if("jp".equals(country)){
	                                	order.setDestination("NGO");
									}else{
										order.setDestination("LAX");
									}
								}
							}
							order.setModel(model);
							order.setTransportType(transportType);
							order.setTransportSta("0");//草稿状态
							order.setPaymentSta("0");//未付款状态
							order.setCreateDate(new Date());
							order.setCreateUser(UserUtils.getUser());
							order.setToCountry(country);
							order.setDestinationDetail(null);
							order.setPickUpDate(today);
							Float volume =0f;
							Float weight=0f;
							Integer boxNum=0;
							List<LcPsiTransportOrderItem>	orderItems=Lists.newArrayList();
							for (PsiTransportForecastPlanOrder item : itemList) {
								LcPsiTransportOrderItem tranOrderItem=new LcPsiTransportOrderItem();
								tranOrderItem.setProductName(item.getProductName());
								if(item.getProductName().indexOf("_")>0){
									String color =item.getProductName().substring(item.getProductName().lastIndexOf("_")+1);
									tranOrderItem.setColorCode(color);
								}else{
									tranOrderItem.setColorCode("");
								}
								tranOrderItem.setCountryCode(item.getCountryCode());
								tranOrderItem.setSku(item.getSku());
								tranOrderItem.setQuantity(item.getQuantity());
								tranOrderItem.setOfflineSta("0");
								String name=item.getProductName();
								
								Integer pack=packQuantityMap.get(name);
								if(name.contains("Inateck DB1001")){
									if("com,uk,jp,ca,mx,".contains(item.getCountryCode())){
										pack=60;
									}else{
										pack=44;
									}
								}else if(name.contains("Inateck DB2001")){
									if("com,jp,ca,mx,".contains(item.getCountryCode())){
										pack=32;
									}else{
										pack=24;
									}
								}
								tranOrderItem.setPackQuantity(pack);
								PsiProduct product=psiProductService.findProductByProductName(item.getProductName());
								tranOrderItem.setProduct(product);
								String res =lcPsiTransportOrderService.getProPriceByProductId(product.getId());
//								Float   partsPrice =this.lcPsiTransportOrderService.getPartsPriceByProductId(product.getId(), item.getColorCode());
								Float 	price =0f;
								Float   productPrice=0f;
								String  currency="";
								String countryCode = tranOrderItem.getCountryCode();
								if(StringUtils.isNotEmpty(res)){
									String[] arr=res.split("_");
									price=Float.parseFloat(arr[0].toString());
									Integer taxRefund=(product.getTaxRefund()==null?17:product.getTaxRefund());
									productPrice =Float.parseFloat(arr[0].toString())/(1+(taxRefund/100));
									if("CNY".equals(arr[1])){
										productPrice=productPrice/AmazonProduct2Service.getRateConfig().get("USD/CNY");
										price=price/AmazonProduct2Service.getRateConfig().get("USD/CNY");
									}

									//理诚全部是人民币
									if("it,de,es,fr,uk".contains(countryCode)){
										currency="EUR";
										price=price/AmazonProduct2Service.getRateConfig().get("EUR/USD");
										productPrice=productPrice/AmazonProduct2Service.getRateConfig().get("EUR/USD");
									}else if("com,mx,ca".contains(countryCode)){
										currency="USD";
									}else if("jp".contains(countryCode)){
										currency="JPY";
										price=price*AmazonProduct2Service.getRateConfig().get("USD/JPY");
										productPrice=productPrice*AmazonProduct2Service.getRateConfig().get("USD/JPY");
									}
									
								}
								
								tranOrderItem.setProductPrice(productPrice);
								tranOrderItem.setItemPrice(price);
								//tranOrderItem.setLowerPrice(lowerPrice);
								//tranOrderItem.setImportPrice(productPrice*priceRate);
								if(eliminateMap.get(item.getProductName())!=null&&eliminateMap.get(item.getProductName()).get(item.getCountryCode())!=null){
									tranOrderItem.setLowerPrice(eliminateMap.get(item.getProductName()).get(item.getCountryCode()).getCnpiPrice());
									tranOrderItem.setImportPrice(eliminateMap.get(item.getProductName()).get(item.getCountryCode()).getPiPrice());
								}
								
								tranOrderItem.setCurrency(currency);
								tranOrderItem.setTransportOrder(order);
								if(StringUtils.isNotBlank(item.getRemark())&&!"ok".equals(item.getRemark().trim())&&!"OK".equals(item.getRemark().trim())&&!"Ok".equals(item.getRemark().trim())){
									tranOrderItem.setRemark(item.getRemark());
								}
								orderItems.add(tranOrderItem);
								if(!"1".equals(product.getComponents())){
									volume+=item.getQuantity()*1.0f/pack*(Float.parseFloat(productMap.get(product.getId()).split(",")[0]));
									weight+=item.getQuantity()*1.0f/pack*(Float.parseFloat(productMap.get(product.getId()).split(",")[1]));
									boxNum+=MathUtils.roundUp(item.getQuantity()*1.0d/pack);
								}
							}
							order.setVolume(volume);
							order.setWeight(weight);
							order.setBoxNumber(boxNum);
							order.setItems(orderItems);
							String transportNo =lcPsiTransportOrderService.createFlowNo();
							order.setTransportNo(transportNo);
							psiTransportOrderList.add(order);
						}
					}
					
				}
			}
			
		}
		
	
	@Transactional(readOnly = false)
	public void savePurchase(Set<Integer> idSet) throws ParseException{	
		
		List<PsiTransportForecastPlanOrder> planList=findPlanOrder(idSet);
		Map<Integer,List<PsiTransportForecastPlanOrder>> forecastMap = Maps.newHashMap();
		Map<Integer,Map<Integer,Integer>> supplierProductMap = Maps.newHashMap(); //供应商，产品，数量    
		PsiSupplier  supplier51 = new PsiSupplier(51);
		PsiSupplier  supplier106 = new PsiSupplier(106);
		Map<String,PsiProduct> productMap=psiProductService.getProductMap();
		List<PsiSupplier> suppliers=supplierService.findAll();
		Map<Integer,PsiSupplier> supMap = Maps.newHashMap();
		for(PsiSupplier sup:suppliers){
			supMap.put(sup.getId(), sup);
		}
		
		for(PsiTransportForecastPlanOrder planOrder:planList){
			String name=planOrder.getProductName();
			if(name.indexOf("_")>0){
				name = name.substring(0,name.indexOf("_"));
			}
			PsiProduct product=productMap.get(name);
			
			Integer supplierId = product.getPsiSuppliers().get(0).getSupplier().getId();
			Integer productId = product.getId();
			planOrder.setProduct(product);
			int proId =productId.intValue();
			if(proId==265||proId==266||proId==267||proId==268){
				supplierId=51;
				planOrder.setSupplier(supplier51);
			}else if(proId==235){
				supplierId=106;
				planOrder.setSupplier(supplier106);
			}else{
				planOrder.setSupplier(product.getPsiSuppliers().get(0).getSupplier());
			}
			//如果生成数为0，终极促销数为0或者为空，都不算数量
			if(planOrder.getQuantity().intValue()==0){
				continue;
			}
			//生成供应商产品数量map
			Map<Integer,Integer> productQuantityMap =null;
			if(supplierProductMap.get(supplierId)==null){
				productQuantityMap = Maps.newHashMap();
			}else{
				productQuantityMap = supplierProductMap.get(supplierId);
			}
			
			Integer productQuantity = planOrder.getQuantity();
			if(productQuantityMap.get(productId)!=null){
				productQuantity+=productQuantityMap.get(productId);
			}
			productQuantityMap.put(productId, productQuantity);
			supplierProductMap.put(supplierId, productQuantityMap);
			
			List<PsiTransportForecastPlanOrder> tempList = null;
			if(forecastMap.get(supplierId)==null){
				tempList = Lists.newArrayList();
			}else{
				tempList = forecastMap.get(supplierId);
			}
			tempList.add(planOrder);
			forecastMap.put(supplierId, tempList);
		}
		
		
		Map<Integer,String> receivedMap = psiProductService.getAllReceivedDate(new Date());
		 Map<Integer,Map<String,Set<Integer>>> followMap = forecastOrderService.getFollowMap(forecastMap.keySet());
		 
		//根据供应商生成采购订单
		 List<LcPurchaseOrder>  purchaseOrders = Lists.newArrayList();
		for(Map.Entry<Integer,List<PsiTransportForecastPlanOrder>> supplierIdEntry:forecastMap.entrySet()){
			Integer supplierId = supplierIdEntry.getKey();
			PsiSupplier supplier = supMap.get(supplierId);
			 //查询每个供应商产品的价格
			Map<String,PsiProductTieredPriceDto> dtoMap = productTieredPriceService.findPrices(supplierProductMap.get(supplierId).keySet(), supplierId, supplier.getCurrencyType());
			 //查询每个供应商的跟单员信息
			Map<String,Set<Integer>> followProductMap = followMap.get(supplierId);
			//生成采购数据
			purchaseOrders.addAll(createPurchaseOrder(supplier, followProductMap, forecastMap.get(supplierId),receivedMap,dtoMap,supplierProductMap.get(supplierId)));
		}
		this.lcPurchaseOrderService.saveAll(purchaseOrders);
		
		String sql="update psi_forecast_transport_plan_order set state='1' where id in :p1 ";
		psiTransportForecastPlanOrderDao.updateBySql(sql, new Parameter(idSet));
     }	
	
	
	public List<LcPurchaseOrder> createPurchaseOrder(PsiSupplier supplier,Map<String,Set<Integer>> followProductMap,List<PsiTransportForecastPlanOrder> foreItems,
			Map<Integer,String> receivedMap, Map<String,PsiProductTieredPriceDto> dtoMap,Map<Integer,Integer> productQuantityMap) throws ParseException {

		SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd");
		Integer tax=supplierService.get(supplier.getId()).getTaxRate();
		Float  taxRate= (tax+100)/100f;
		//获取产品价格 start
		Map<String,BigDecimal> productPrices = Maps.newHashMap();
		for(Map.Entry<String,PsiProductTieredPriceDto> entry:dtoMap.entrySet()){
			String key = entry.getKey();
			String [] arr=key.split(",");
			String productIdStr=arr[0];
			String color =arr[1];
			PsiProductTieredPriceDto dto = dtoMap.get(key);
			Integer orderQuantity = productQuantityMap.get(Integer.parseInt(productIdStr));
			Float price = null;
			String proColorKey = productIdStr+"_"+color;
			if(productPrices.get(proColorKey)==null){
				if("USD".equals(supplier.getCurrencyType())){
					if(orderQuantity<1000){ 		//小于1000用500的价
						price=dto.getLeval500usd(); 
					}else if(orderQuantity<2000){   //小于2000用1000的价
						price=dto.getLeval1000usd();
					}else if(orderQuantity<3000){   //小于3000用2000的价
						price=dto.getLeval2000usd();
					}else if(orderQuantity<5000){   //小于5000用3000的价
						price=dto.getLeval3000usd();
					}else if(orderQuantity<10000){  //小于10000用5000的价
						price=dto.getLeval5000usd();
					}else if(orderQuantity<15000){  //小于15000用10000的价
						price=dto.getLeval10000usd();
					}else{                          //大于15000用15000的价
						price=dto.getLeval15000usd();
					}
				}else{
					if(orderQuantity<1000){ 		//小于1000用500的价
						price=dto.getLeval500cny(); 
					}else if(orderQuantity<2000){   //小于2000用1000的价
						price=dto.getLeval1000cny();
					}else if(orderQuantity<3000){   //小于3000用2000的价
						price=dto.getLeval2000cny();
					}else if(orderQuantity<5000){   //小于5000用3000的价
						price=dto.getLeval3000cny();
					}else if(orderQuantity<10000){  //小于10000用5000的价
						price=dto.getLeval5000cny();
					}else if(orderQuantity<15000){  //小于15000用10000的价
						price=dto.getLeval10000cny();
					}else{                          //大于15000用15000的价
						price=dto.getLeval15000cny();
					}
				}
				if(price==null){
					productPrices.put(proColorKey, null);
				}else{
					productPrices.put(proColorKey, new BigDecimal(price+"").multiply(new BigDecimal(taxRate+"")).setScale(2, BigDecimal.ROUND_HALF_UP));
				}
			}
		}
		//获取产品价格 end
		
		//产品名称-产品线ID
		Map<String,String> nameAndLineIdMap=typeLineService.getLineByName();
		//产品线-国家-人id+","+name
		Map<String,Map<String,String>> saleUserMap=psiProductGroupUserService.getSingleGroupUser();
		
		List<LcPurchaseOrder> purchaseOrders = Lists.newArrayList();
			for(Map.Entry<String,Set<Integer>> userEntry:followProductMap.entrySet()){
				String userId =userEntry.getKey();
				LcPurchaseOrder  purchaseOrder = new LcPurchaseOrder();
				purchaseOrder.setSupplier(supplier);
				List<LcPurchaseOrderItem> itemList = new ArrayList<LcPurchaseOrderItem>();
				String shortName  = supplier.getNikename();
				BigDecimal  totalAmount =BigDecimal.ZERO;
				for(PsiTransportForecastPlanOrder forecastItem :foreItems){
					if(!followProductMap.get(userId).contains(forecastItem.getProduct().getId())){
						continue;
					}
					LcPurchaseOrderItem orderItem = new LcPurchaseOrderItem();
					String name=forecastItem.getProductName();
					if(StringUtils.isNotBlank(name)){
						String lineId=nameAndLineIdMap.get(name);
						if(StringUtils.isNotBlank(lineId)&&saleUserMap!=null&&saleUserMap.get(lineId)!=null&&saleUserMap.get(lineId).get(forecastItem.getCountryCode())!=null){
							orderItem.setSalesUser(saleUserMap.get(lineId).get(forecastItem.getCountryCode()).split(",")[1]);
						}
					}
					orderItem.setForecastRemark(forecastItem.getRemark());
					orderItem.setForecastItemId(forecastItem.getId());
					String color = "";
					if(name.indexOf("_")>0){
						//name = name.substring(0,productName.indexOf("_"));
						color = name.substring(name.indexOf("_")+1);
					}
					
					orderItem.setProduct(forecastItem.getProduct());
					orderItem.setColorCode(color);
					orderItem.setCountryCode(forecastItem.getCountryCode());
					Integer orderQuantity =0;
					if(forecastItem.getQuantity()!=0){
						orderQuantity+=forecastItem.getQuantity();
					}
					
//					if(forecastItem.getPromotionQuantity()!=null){//添加促销数量
//						orderQuantity+=forecastItem.getPromotionQuantity();
//					}
//					if(forecastItem.getPromotionBossQuantity()!=null){//添加广告数量
//						orderQuantity+=forecastItem.getPromotionBossQuantity();
//					}
					orderItem.setQuantityOrdered(orderQuantity);
					orderItem.setQuantityPreReceived(0);   //预收货数量为0
					orderItem.setQuantityReceived(0);      //已收货数量为0
					//线下数量
					orderItem.setQuantityOffPreReceived(0);   //线下预收货数量为0
					orderItem.setQuantityOffReceived(0);      //线下已收货数量为0
					orderItem.setQuantityOffOrdered(0);       //线下订单数为0
					
					orderItem.setQuantityPayment(0);       //已付款数量为0
					orderItem.setPaymentAmount(BigDecimal.ZERO);        //已支付金额    0
					orderItem.setProductName(forecastItem.getProductName());
					orderItem.setDeliveryDate(sdf.parse(receivedMap.get(orderItem.getProduct().getId())));
					orderItem.setActualDeliveryDate(orderItem.getDeliveryDate());
					orderItem.setPurchaseOrder(purchaseOrder);  
					String productColor = forecastItem.getProduct().getId()+"_"+color;
					orderItem.setItemPrice(productPrices.get(productColor));
					if(orderItem.getItemPrice()!=null){
						totalAmount=totalAmount.add(new BigDecimal(orderItem.getQuantityOrdered()).multiply(orderItem.getItemPrice()));
					}
					itemList.add(orderItem);
				}
				if(itemList.size()>0){
					User user = UserUtils.getUserById(userId);
					Date curDate = new Date();
					String orderNo = this.purchaseOrderService.createSequenceNumber(shortName+"_LC");
					purchaseOrder.setOrderNo(orderNo);
					purchaseOrder.setIsOverInventory("0");//这种拆分的应该不超标吧？
					purchaseOrder.setPurchaseDate(curDate);
					purchaseOrder.setCreateDate(curDate);
					purchaseOrder.setCreateUser(user);
					purchaseOrder.setUpdateDate(curDate);
					purchaseOrder.setUpdateUser(user);
					purchaseOrder.setTotalAmount(totalAmount);    //订单总金额
					purchaseOrder.setDepositAmount(BigDecimal.ZERO);           //已支付定金金额 0f
					purchaseOrder.setDepositPreAmount(BigDecimal.ZERO);        //已申请定金金额0f
					purchaseOrder.setOrderSta("0");  		      //草稿状态
					purchaseOrder.setDelFlag("0");                //删除状态
					purchaseOrder.setPaySta("0");                 //是否付款
					purchaseOrder.setPaymentAmount(BigDecimal.ZERO);           //支付尾款金额0f
					purchaseOrder.setCurrencyType(supplier.getCurrencyType());
					purchaseOrder.setDeposit(supplier.getDeposit());
					purchaseOrder.setMerchandiser(user);
					purchaseOrder.setItems(itemList);
					purchaseOrders.add(purchaseOrder);
				}
				
			}
		    return purchaseOrders;
		}
}
