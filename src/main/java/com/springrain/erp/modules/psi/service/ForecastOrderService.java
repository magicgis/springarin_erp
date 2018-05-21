/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.entity.SalesForecastByMonth;
import com.springrain.erp.modules.amazoninfo.service.SaleProfitService;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.amazoninfo.service.SalesForecastServiceByMonth;
import com.springrain.erp.modules.psi.dao.ForecastOrderDao;
import com.springrain.erp.modules.psi.dao.ForecastOrderItemDao;
import com.springrain.erp.modules.psi.entity.ForecastOrder;
import com.springrain.erp.modules.psi.entity.ForecastOrderItem;
import com.springrain.erp.modules.psi.entity.ProductInventoryTotalDto;
import com.springrain.erp.modules.psi.entity.ProductSalesInfo;
import com.springrain.erp.modules.psi.entity.PsiInventoryFba;
import com.springrain.erp.modules.psi.entity.PsiInventoryTotalDto;
import com.springrain.erp.modules.psi.entity.PsiInventoryTotalDtoByInStock;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductTieredPriceDto;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrderItem;
import com.springrain.erp.modules.psi.service.lc.LcPurchaseOrderService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;
/**
 * 预测订单Service
 * @author Michael  
 * @version 2016-2-26
 */
@Component
@Transactional(readOnly = true)
public class ForecastOrderService extends BaseService {
	@Autowired
	private ForecastOrderDao    			forecastOrderDao;
	@Autowired
	private ForecastOrderItemDao    		forecastOrderItemDao;
	@Autowired
	private PsiSupplierService    			supplierService;
	@Autowired
	private LcPurchaseOrderService        	purchaseOrderService;
	@Autowired
	private PsiProductService           	productService;
	@Autowired
	private MailManager						mailManager;  
	@Autowired
	private SystemService					systemService;  
	@Autowired
	private PsiInventoryService           	psiInventoryService;
	@Autowired
	private PsiProductEliminateService    	psiProductEliminateService;
	@Autowired
	private PsiProductAttributeService    	psiProductAttributeService;
	@Autowired
	private PsiSupplierService 			  	psiSupplierService;
	@Autowired
	private SalesForecastServiceByMonth   	salesForecastService;
	@Autowired
	private ProductSalesInfoService       	productSalesInfoService;
	@Autowired
	private PsiProductService             	psiProductService;
	@Autowired
	private PsiProductTieredPriceService 	productTieredPriceService;
	@Autowired
	private PsiProductTypeGroupDictService  typeLineService;
	@Autowired
	private PsiProductGroupUserService 		psiProductGroupUserService;
	@Autowired
	private PsiMarketingPlanService   		planService;
	@Autowired
	private SaleReportService   		saleReportService;
	@Autowired
	private SaleProfitService saleProfitService;
	
	public ForecastOrder get(Integer id) {
		return forecastOrderDao.get(id);
	}

	@Transactional(readOnly = false)
	public void saveItemList(List<ForecastOrderItem> itemList) {
		forecastOrderItemDao.save(itemList);
	}
	
	public Page<ForecastOrder> find(Page<ForecastOrder> page, ForecastOrder forecastOrder) {
		DetachedCriteria dc = forecastOrderDao.createDetachedCriteria();
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
		return forecastOrderDao.find(page,dc);
	}
	
	
	@Transactional(readOnly = false)
	public void save(ForecastOrder forecastOrder){
		this.forecastOrderDao.save(forecastOrder);
	}
	
	@Transactional(readOnly = false)
	public void cancel(ForecastOrder forecastOrder){
		forecastOrder.setCancelDate(new Date());
		forecastOrder.setCancelUser(UserUtils.getUser());
		forecastOrder.setOrderSta("8");
		this.forecastOrderDao.save(forecastOrder);
	}
	
	
	@Transactional(readOnly = false)
	public String reviewSave(ForecastOrder forecastOrder,Map<String,List<ForecastOrderItem>> overMap) throws ParseException{
		//通知进行超标审核
		StringBuffer contents= new StringBuffer("Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;<br/>预测订单编号：<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/forecastOrder/overReview?id="+forecastOrder.getId()+"'>"+forecastOrder.getId()+"</a>已创建，请尽快登陆erp系统进行(超标审核),超标产品如下：<br/>");
		contents.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>" +
		"<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '>" +
		"<th>产品名</th><th>MOQ</th><th>运输方式</th><th>生产运输缓冲周期</th><th>下单量</th><th>国家</th>" +
		"<th>下单依据</th><th>总库存</th><th>31日销</th><th>库存上限</th><th>超出库存上限数量</th></tr>");
		Map<String,String> stockMap = this.getStockTotal(null);
		Map<Integer,Integer> moqMap =this.psiProductService.getMoq();
		Map<String,String> tranMap=psiProductEliminateService.findTransportType();
		for(Map.Entry<String,List<ForecastOrderItem>> entry:overMap.entrySet()){
			String proName = entry.getKey();
			Integer total  =0;
			for(ForecastOrderItem item :entry.getValue()){
				total+=item.getQuantity();
			}
			Integer sale31 = 0;
			if(stockMap.get(proName)!=null){
				String[] arr= stockMap.get(proName).split(",");
				total+=Integer.parseInt(arr[0]);
				sale31 = Integer.parseInt(arr[1]);
			}
			int i=0;
			for(ForecastOrderItem item :entry.getValue()){
				String country = item.getCountryCode();
				Integer period = item.getPeriod();
				Integer quantity = item.getQuantity();
				if (quantity == 0) {
					continue;	//未下单的不需要提醒
				}
				Integer max =item.getMaxStock()==null?0:item.getMaxStock();
				String by ="手动增加";
				if("0".equals(item.getBy31sales())||"2".equals(item.getBy31sales())){
					by="预销";
				}else if("1".equals(item.getBy31sales())||"3".equals(item.getBy31sales())){
					by="31销";
				}
				String color="#f5fafe";
				if(i==0){
					color="#99CCFF";
				}
				i++;
				contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:"+color+"; '>" +
						"<td>"+proName+"</td><td>"+moqMap.get(item.getProduct().getId())+"</td><td>"+("2".equals(tranMap.get(item.getConKey()))?"空运":"海运")+"</td><td>"+period+"</td><td>"+quantity+"</td><td>"+("com".equals(country)?"us":country)+"</td>"+
						"<td>"+by+"</td><td>"+total+"</td><td>"+sale31+"</td><td>"+max+"</td><td>"+(total-max)+"</td></tr>");
			}
		}
		contents.append("</table>");
		Date date = new Date();
		List<String> emails =systemService.findUserByMenuName("psi:forecastOrder:overReview");
		if(emails!=null&&emails.size()>0){
			StringBuilder address= new StringBuilder();
			for(String email:emails){
				address.append(email).append(",");
			}
			final MailInfo mailInfo = new MailInfo(address.substring(0, address.length()-1),"预测采购订单(待超标审核)"+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(contents.toString());
			mailInfo.setCcToAddress(UserUtils.getUser().getEmail());
			//发送成功不成功都能保存
			new Thread(){
				@Override
				public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
		}
		return "";
	}
	
	/**
	 *是否超标 
	 */
	@Transactional(readOnly = false)
	public String isOver(Integer itemId,Integer quantity){
		ForecastOrderItem item =this.forecastOrderItemDao.get(itemId);
		String productName = item.getProductName();
		String color = item.getColorCode();
		String nameWithColor = item.getProductNameColor();
		Integer forecastId = item.getForecastOrder().getId();
		//查询最大库容值
		Integer totalStock=0;
		Integer sale31 =0;
		String sql="SELECT SUM(a.`quantity`) FROM  `psi_forecast_order_item` AS a  WHERE a.`display_sta`!='1' AND a.`product_name`=:p1 AND a.`color_code`=:p2 AND a.`forecast_order_id`=:p3  ";
		List<Object> objs = this.forecastOrderDao.findBySql(sql, new Parameter(productName,color,forecastId));
		if(objs!=null&&objs.size()>0){
			totalStock=objs.get(0)==null?0:((int)Float.parseFloat(objs.get(0).toString()));
		}
		
//		if(quantity.intValue()==0&&totalStock.intValue()==0){
		if(totalStock.intValue()==0){
			this.updateMaxQuantity(productName, color, forecastId, null);
			return "";
		}else{
			boolean over5=false;
			Map<String,String> stockMap = this.getStockTotal(nameWithColor);
			if(stockMap!=null&&stockMap.size()>0&&stockMap.get(nameWithColor)!=null){
				String arr[]=stockMap.get(nameWithColor).split(",");
				totalStock+=Integer.parseInt(arr[0]);
				sale31=Integer.parseInt(arr[1]);
				if(sale31.intValue()==0||totalStock/sale31>5){
					over5=true;
				}
			}
			
			Integer max = psiProductAttributeService.getMaxInventoryByName(nameWithColor);
			//总库存/31日销>5  或者大于最大库存值，标记最大尺寸
			if(over5||(max!=null&&max.intValue()>0&&totalStock.intValue()>0&&totalStock>max)){
				//更新最大库存
				this.updateMaxQuantity(productName, color, forecastId, max);
				if(over5){
					return "下单后总库存变为："+totalStock+",大于31日销("+sale31+")5倍";
				}else{
					return "下单后总库存变为："+totalStock+",最大库存限制为："+max+",超出库存限制"+"("+(totalStock-max)+")";
				}
				
			}else{
				//更新最大库存
				updateMaxQuantity(productName, color, forecastId, null);
				return "";
			}
		}
		
	}
	
	@Transactional(readOnly = false)
	public void updateMaxQuantity(String productName,String color,Integer forecastId,Integer maxStock){
		String sql ="UPDATE `psi_forecast_order_item` AS a SET a.`max_stock`=:p4  WHERE a.`product_name`=:p1 AND a.`color_code`=:p2 AND a.`forecast_order_id`=:p3  ";
		this.forecastOrderDao.updateBySql(sql, new Parameter(productName,color,forecastId,maxStock));
	}
	
	
	
	@Transactional(readOnly = false)
	public String reviewOverSave(ForecastOrder forecastOrder,boolean isOver) throws ParseException{
		//通知进行终极审核
		String content = "预测订单编号：<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/forecastOrder/bossReview?id="+forecastOrder.getId()+"'>"+forecastOrder.getId()+"</a>已创建，请尽快登陆erp系统进行(终极审核)";
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();
			List<String> emails =systemService.findUserByMenuName("psi:forecastOrder:bossReview");
			if(emails!=null&&emails.size()>0){
				StringBuilder address= new StringBuilder();
				for(String email:emails){
					address.append(email).append(",");
				}
				final MailInfo mailInfo = new MailInfo(address.substring(0, address.length()-1),"预测采购订单("+(isOver?"":"未")+"超标)待终极审核"+DateUtils.getDate("-yyyy/M/dd"),date);
				mailInfo.setContent(content);
				mailInfo.setCcToAddress(UserUtils.getUser().getEmail());
				//发送成功不成功都能保存
				new Thread(){
					@Override
					public void run(){
						mailManager.send(mailInfo);
					}
				}.start();
			}
		}
		return "";
	}
	
	@Transactional(readOnly = false)
	public String bossSave(ForecastOrder forecastOrder) throws ParseException{
		if("4".equals(forecastOrder.getOrderSta())){
			forecastOrder.setOrderSta("5");			
			forecastOrder.setReviewUser(UserUtils.getUser());
			forecastOrder.setReviewDate(new Date());
			this.forecastOrderDao.save(forecastOrder);
			String res= "审核成功";
			List<ForecastOrder> list = findForSplit(forecastOrder);
			if (list != null && list.size() == 2) {
				res = this.splitForecastOrder(list);
				if(!"审核失败".equals(res)){
					//给销售发邮件
					this.sendEmail(list);
				}
			}
			return res;
		}
		return "";
	}
	
	@Transactional(readOnly = false)
	public void salesSave(ForecastOrder forecastOrder) throws ParseException{
		save(forecastOrder);
		//判断是否分单
		List<ForecastOrder> list = findForSplit(forecastOrder);
		if (list != null && list.size() == 2) {
			String res = this.splitForecastOrder(list);
			if(!"审核失败".equals(res)){
				//给销售发邮件
				this.sendEmail(list);
			}
		}
	}
	
	/**
	 * 更新计划数据
	 * 
	 */
	@Transactional(readOnly = false)
	public void updatePlanData(List<ForecastOrder> forecastOrders){
		//当前周
		Date targetDate = forecastOrders.get(0).getTargetDate();
		Date today = new Date();
		String targetWeek = "";
		DateFormat formatWeek = new SimpleDateFormat("yyyyww");
		String oneWeek = DateUtils.getWeekStr(today,formatWeek, 4, "");
		String twoWeek = DateUtils.getWeekStr(DateUtils.addDays(today, 7),formatWeek, 4, "");
		String threeWeek = DateUtils.getWeekStr(DateUtils.addDays(today, 14),formatWeek, 4, "");
		String fourWeek = DateUtils.getWeekStr(DateUtils.addDays(today, 21),formatWeek, 4, "");
		//第四周周数,用于比较当前周下单产品
		boolean delay = false;
		if (targetDate != null && targetDate.after(DateUtils.addMonths(new Date(), 1))) {
			targetWeek =  DateUtils.getWeekStr(targetDate,formatWeek, 4, "");
			delay = true;
		}
		
		Map<String, String> fanInfo=this.productService.getPowerOrKeyboardByName();
		for (ForecastOrder forecastOrder : forecastOrders) {
			for(ForecastOrderItem  item :forecastOrder.getItems()){
				Integer quantity = item.getQuantity();
				if(!"0".equals(item.getDisplaySta())||quantity==null||quantity.intValue()==0){
					continue;
				}
				
				String productName = item.getProductName();
				String color = item.getColorCode();
				String country = item.getCountryCode();
				boolean isFanEu=false;
				if(fanInfo.get(item.getProductNameColor())!=null&&"1".equals(fanInfo.get(item.getProductNameColor()))){
					isFanEu=true;
				}
				Integer forecastId = forecastOrder.getId();
				Integer queQ = 0;
				String endWeek =fourWeek;
				//缺口数
				String byWeek = item.getByWeek();
				if(delay){
					endWeek=targetWeek;
				}else{
					if("0".equals(byWeek)){
						queQ=item.getForecast1week();
						endWeek=fourWeek;
					}else if("1".equals(byWeek)){
						queQ=item.getForecast2week();
						endWeek=oneWeek;
					}else if("2".equals(byWeek)){
						queQ=item.getForecast3week();
						endWeek=twoWeek;
					}else if("3".equals(byWeek)){
						queQ=item.getForecast4week();
						endWeek=threeWeek;
					}
				}
				Integer balanceQ = quantity-queQ;
				Date endDate = DateUtils.getLastDayOfWeek(Integer.parseInt(endWeek.substring(0, 4)), Integer.parseInt(endWeek.substring(4, 6)));
				long spaceDay = DateUtils.spaceDays(forecastOrder.getCreateDate(), endDate);
				//没备货的不做处理
				if(balanceQ.intValue()>0){
					//促销、广告  都不为空
					if(item.getPromotionQuantity()!=null&&item.getPromotionQuantity().intValue()>0&&item.getPromotionBossQuantity()!=null&&item.getPromotionBossQuantity().intValue()>0){
						Integer proQ =0;
						balanceQ-=item.getPromotionQuantity();
						if(balanceQ.intValue()>0){
							proQ =item.getPromotionQuantity();
							//促销用完了，剩下的是广告的
							
							//更新广告累加广告数、备注
							int adQ =0;
							if(item.getPromotionBossQuantity().intValue()>balanceQ){
								adQ = balanceQ;
							}else{
								adQ =item.getPromotionBossQuantity();
							}
							//更新广告累加广告数、备注
							this.updatePlanAdInfo(productName, color, country, adQ, isFanEu,forecastId,spaceDay);
						}else{
							//促销都没用完
							proQ=balanceQ+item.getPromotionQuantity();
						}
						//根据促销数量     对营销计划进行更新
						this.updatePlanPromoInfo(productName, color, country, proQ,endWeek,isFanEu);
					}else if(item.getPromotionQuantity()!=null&&item.getPromotionQuantity().intValue()>0){
						//促销
						this.updatePlanPromoInfo(productName, color, country, balanceQ,endWeek,isFanEu);
					}else if(item.getPromotionBossQuantity()!=null&&item.getPromotionBossQuantity().intValue()>0){
						//更新广告累加广告数、备注      (广告数大于剩余数的，用剩余数)
						int adQ =0;
						if(item.getPromotionBossQuantity().intValue()>balanceQ){
							adQ = balanceQ;
						}else{
							adQ =item.getPromotionBossQuantity();
						}
						this.updatePlanAdInfo(productName, color, country, adQ, isFanEu,forecastId,spaceDay);
					}
				}
			}
			
		}
		
	}
	
	
	//更新广告信息
	@Transactional(readOnly = false)
	public void updatePlanAdInfo(String productName,String color,String country,Integer quantity,boolean fanEu,Integer forecastId,long spaceDay){
		if(fanEu){
			//泛欧产品根据日期算备货数
			Set<String> countrySet = Sets.newHashSet();
			countrySet.add("de");
			countrySet.add("fr");
			countrySet.add("uk");
			countrySet.add("it");
			countrySet.add("es");
			String sql = "SELECT i.id,i.`promo_quantity` FROM `psi_marketing_plan` t ,`psi_marketing_plan_item` i WHERE t.`id`=i.`marketing_plan_id` " +
					" AND t.`sta`='3'  AND i.`product_name`=:p1 AND i.`color_code`=:p2 AND t.`country_code` IN :p3 AND i.`del_flag`='0' " +
					"  AND t.`type`='1'  ORDER BY FIELD(t.`country_code`,'de','fr','uk','it','es') ";
			List<Object[]> list = this.forecastOrderDao.findBySql(sql, new Parameter(productName,color,countrySet));
			for (Object[] obj : list) {
				Integer itemId = Integer.parseInt(obj[0].toString());
				Integer proQuantity = Integer.parseInt(obj[1]!=null?obj[1].toString():"0");
				Integer balanceQ = proQuantity*(int)spaceDay;
				quantity-=balanceQ;
				Integer tempQ =0;
				if(quantity>=0){
					tempQ=balanceQ;
					String remark="["+forecastId+"],备货数"+tempQ+";";;
					String updateSql=" UPDATE psi_marketing_plan_item AS a SET a.`ready_quantity`=(IFNULL(a.`ready_quantity`,0)+ :p2),a.`ready_remark`=CONCAT(IFNULL(a.`ready_remark`,''),:p3) WHERE a.`id`=:p1 ";
					this.forecastOrderDao.updateBySql(updateSql, new Parameter(itemId,tempQ,remark));
				}else{
					tempQ=quantity+balanceQ;
					String remark="["+forecastId+"],备货数"+tempQ+";";;
					String updateSql=" UPDATE psi_marketing_plan_item AS a SET a.`ready_quantity`=(IFNULL(a.`ready_quantity`,0)+ :p2),a.`ready_remark`=CONCAT(IFNULL(a.`ready_remark`,''),:p3) WHERE a.`id`=:p1 ";
					this.forecastOrderDao.updateBySql(updateSql, new Parameter(itemId,tempQ,remark));
					break;
				}
			}
		}else{
			String sql="UPDATE psi_marketing_plan AS a ,psi_marketing_plan_item AS b SET b.`ready_quantity`=(IFNULL(b.`ready_quantity`,0)+:p4)," +
					"b.`ready_remark`=CONCAT(IFNULL(b.`ready_remark`,''),:p5) WHERE a.`id`=b.`marketing_plan_id` AND a.`sta`='3' AND a.`type`='1' AND b.`del_flag`='0'" +
					" AND a.`country_code`=:p3 AND b.`product_name`=:p1 AND b.`color_code`=:p2";
			String remark="["+forecastId+"],备货数"+quantity+";";
			this.forecastOrderDao.updateBySql(sql, new Parameter(productName,color,country,quantity,remark));
		}
		
	}
	
	
	
	
	/**
	 * 更新促销信息
	 * 从最近的更新备货数
	 */
	@Transactional(readOnly = false)
	public void updatePlanPromoInfo(String productName,String color,String country,Integer quantity,String endWeek,boolean fanEu){
		Set<String> countrySet = Sets.newHashSet();
		if(fanEu){
			countrySet.add("de");
			countrySet.add("fr");
			countrySet.add("uk");
			countrySet.add("it");
			countrySet.add("es");
		}else{
			countrySet.add(country);
		}
		String sql = "SELECT i.id,i.`promo_quantity`,i.`ready_quantity` FROM `psi_marketing_plan` t ,`psi_marketing_plan_item` i WHERE t.`id`=i.`marketing_plan_id` " +
				" AND t.`sta`='3'  AND i.`product_name`=:p1 AND i.`color_code`=:p2 AND t.`country_code` IN :p3 AND i.`del_flag`='0' AND t.`start_week`<=:p4 " +
				"  AND (i.`promo_quantity`-IFNULL(i.`ready_quantity`,0))>0  AND t.`type`='0'  ORDER BY FIELD(t.`country_code`,'de','fr','uk','it','es'),t.`start_week` ASC ";
		List<Object[]> list = this.forecastOrderDao.findBySql(sql, new Parameter(productName,color,countrySet,endWeek));
		for (Object[] obj : list) {
			Integer itemId = Integer.parseInt(obj[0].toString());
			Integer proQuantity = Integer.parseInt(obj[1]!=null?obj[1].toString():"0");
			Integer readyQuantity = Integer.parseInt(obj[2]!=null?obj[2].toString():"0");
			Integer balanceQ = proQuantity-readyQuantity;
			quantity-=balanceQ;
			Integer tempQ =0;
			if(quantity>=0){
				tempQ=balanceQ;
				String updateSql=" UPDATE psi_marketing_plan_item AS a SET a.`ready_quantity`=(IFNULL(a.`ready_quantity`,0)+ :p2) WHERE a.`id`=:p1 ";
				this.forecastOrderDao.updateBySql(updateSql, new Parameter(itemId,tempQ));
			}else{
				tempQ=quantity+balanceQ;
				String updateSql=" UPDATE psi_marketing_plan_item AS a SET a.`ready_quantity`=(IFNULL(a.`ready_quantity`,0)+ :p2) WHERE a.`id`=:p1 ";
				this.forecastOrderDao.updateBySql(updateSql, new Parameter(itemId,tempQ));
				break;
			}
		}
	}
	
	
	/**
	 * 拆分订单
	 * @throws ParseException 
	 */
	public String  splitForecastOrder(List<ForecastOrder> forecastOrders) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//生成新品下单通知数据
		Set<String> unOrderProducts=this.productService.getNewProductAndNoOrder();
		//查询最近下单
		Map<String,String> lastDateMap = this.getLastOrderDate();
		Integer resI=0;
		List<PsiSupplier> suppliers=supplierService.findAll();
		Map<Integer,PsiSupplier> supMap = Maps.newHashMap();
		for(PsiSupplier sup:suppliers){
			supMap.put(sup.getId(), sup);
		}
		Map<Integer,List<ForecastOrderItem>> forecastMap = Maps.newHashMap();
		Map<Integer,Map<Integer,Integer>> supplierProductMap = Maps.newHashMap(); //供应商，产品，数量    
		PsiSupplier  supplier51 = new PsiSupplier(51);
		PsiSupplier  supplier106 = new PsiSupplier(106);
		for (ForecastOrder forecastOrder : forecastOrders) {
			for(ForecastOrderItem item:forecastOrder.getItems()){
				Integer supplierId = item.getSupplier().getId();
				Integer productId = item.getProduct().getId();
				int proId =productId.intValue();
				if(proId==265||proId==266||proId==267||proId==268){
					supplierId=51;
					item.setSupplier(supplier51);
				}
				if(proId==235){
					supplierId=106;
					item.setSupplier(supplier106);
				}
				//如果生成数为0，终极促销数为0或者为空，都不算数量
				if(item.getQuantity().intValue()==0){
					continue;
				}
				//生成供应商产品数量map
				Map<Integer,Integer> productQuantityMap =null;
				if(supplierProductMap.get(supplierId)==null){
					productQuantityMap = Maps.newHashMap();
				}else{
					productQuantityMap = supplierProductMap.get(supplierId);
				}
				
				Integer productQuantity = item.getQuantity();
				if(productQuantityMap.get(productId)!=null){
					productQuantity+=productQuantityMap.get(productId);
				}
				productQuantityMap.put(productId, productQuantity);
				supplierProductMap.put(supplierId, productQuantityMap);
				//保存最近下单周
				String proColor = item.getProductNameColor();
				if(lastDateMap.get(proColor)!=null){
					item.setLastOrderWeek(lastDateMap.get(proColor));
				}
				List<ForecastOrderItem> tempList = null;
				if(forecastMap.get(supplierId)==null){
					tempList = Lists.newArrayList();
				}else{
					tempList = forecastMap.get(supplierId);
				}
				tempList.add(item);
				forecastMap.put(supplierId, tempList);
			}
		}
		resI=forecastMap.size();
		Map<Integer,String> receivedMap = this.productService.getAllReceivedDate(new Date());
		 Map<Integer,Map<String,Set<Integer>>> followMap = getFollowMap(forecastMap.keySet());
		 
		//根据供应商生成采购订单
		 List<LcPurchaseOrder>  purchaseOrders = Lists.newArrayList();
		for(Map.Entry<Integer,List<ForecastOrderItem>> supplierIdEntry:forecastMap.entrySet()){
			Integer supplierId = supplierIdEntry.getKey();
			PsiSupplier supplier = supMap.get(supplierId);
			 //查询每个供应商产品的价格
			 Map<String,PsiProductTieredPriceDto> dtoMap = productTieredPriceService.findPrices(supplierProductMap.get(supplierId).keySet(), supplierId, supplier.getCurrencyType());
			 //查询每个供应商的跟单员信息
			Map<String,Set<Integer>> followProductMap = followMap.get(supplierId);
			//生成采购数据
			purchaseOrders.addAll(this.createPurchaseOrder(supplier, followProductMap, forecastMap.get(supplierId),receivedMap,dtoMap,supplierProductMap.get(supplierId)));
		}
		this.purchaseOrderService.saveAll(purchaseOrders);
		
		//新品首单发信
		StringBuffer sb = new StringBuffer("");
		for(LcPurchaseOrder order : purchaseOrders){
			for(LcPurchaseOrderItem orderItem:order.getItems()){
				if(unOrderProducts.contains(orderItem.getProductNameColorCountry())){
					if(StringUtils.isEmpty(sb)){
						sb.append("Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;新品首单产品明细如下：<br/><table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
						sb.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#f2f4f6;color:#666;'>");
						sb.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>产品名称</th>");
						sb.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>国家</th>");
						sb.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>数量</th>");
						sb.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>预计交期</th>");
						sb.append("</tr>");
					}
					sb.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
					String productUrl="<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/psiInventory/productInfoDetail?productName="+orderItem.getProductNameColor()+"'>"+orderItem.getProductNameColor()+"</a>";
					sb.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+productUrl+"</td>");
					sb.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+("com".equals(orderItem.getCountryCode())?"us":orderItem.getCountryCode())+"</td>");
					sb.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+orderItem.getQuantityOrdered()+"</td>");
					sb.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(orderItem.getDeliveryDate()!=null?sdf.format(orderItem.getDeliveryDate()):"")+"</td>");
					sb.append("</tr>");
				}
			}
		}
		
		if(StringUtils.isNotEmpty(sb)){
			//新品首单      发信给：销售、产品经理、市场推广、图片设计
			String email="maik@inateck.com,amazon-sales@inateck.com,pmg@inateck.com,marketing_dept@inateck.com,design@inateck.com";
			this.purchaseOrderService.sendNoticeEmail(email, sb.toString(), "新品首单提醒", "", "");
		}
		try {
			//对营销计划进行备货更新
			updatePlanData(forecastOrders);
		} catch (Exception e) {
			logger.error("营销计划进行备货更新失败", e);
		}
		return "生成"+resI+"个订单";
	}
	
	
	/**
	 *获取跟单员信息       供应商：用户：产品
	 */
	public Map<Integer,Map<String,Set<Integer>>> getFollowMap(Set<Integer> supplierIds){
		Map<Integer,Map<String,Set<Integer>>> resMap = Maps.newHashMap();
		String sql="SELECT DISTINCT c.`supplier_id`,a.`create_user`,a.id FROM psi_product AS a,sys_user AS b,psi_product_supplier AS c WHERE a.id=c.`product_id`AND  a.`create_user`=b.`id` AND a.`del_flag`='0' AND b.`del_flag`='0' AND c.`supplier_id` IN :p1";
		List<Object[]>  list=  this.forecastOrderDao.findBySql(sql, new Parameter(supplierIds));
		for(Object[] obj:list){
			Integer supplierId = (Integer)obj[0];
			String userId = (String)obj[1];
			Integer productId = (Integer)obj[2];
			Map<String,Set<Integer>> followUserMap=null;
			if(resMap.get(supplierId)==null){
				followUserMap=Maps.newHashMap();
			}else{
				followUserMap=resMap.get(supplierId);
			}
			Set<Integer> productIds = null;
			if(followUserMap.get(userId)==null){
				productIds=Sets.newHashSet();
			}else{
				productIds=followUserMap.get(userId);
			}
			productIds.add(productId);
			followUserMap.put(userId, productIds);
			resMap.put(supplierId, followUserMap);
		}
		return resMap;
	}
	
	/**
	 *获取跟单员信息 
	 */
	public Map<Integer,String> getSupplierMap(){
		Map<Integer,String> resMap = Maps.newHashMap();
		String sql="SELECT b.`product_id`,a.id,a.`nikename` FROM psi_supplier AS a,psi_product_supplier AS b WHERE a.`id`=b.`supplier_id`";
		List<Object[]>  list=  this.forecastOrderDao.findBySql(sql);
		for(Object[] obj:list){
			Integer productId = (Integer)obj[0];
			String supplierInfo = obj[1]+","+obj[2];
			resMap.put(productId, supplierInfo);
		}
		return resMap;
	}
	
     /**
      *最近下单时间 
      */
	public Map<String,String> getLastOrderDate(){
		return this.purchaseOrderService.getLastOrderDateByColor();
	}
	
	
	/**
     *最近周有无订单
     */
	public boolean hasLastWeekOrder(Date startDate){
		String sql="SELECT COUNT(*) FROM psi_forecast_order AS a WHERE a.`order_sta`!='8' AND a.`create_date`>=:p1";
		List<BigInteger>  list=  this.forecastOrderDao.findBySql(sql,new Parameter(startDate));
		if(list.get(0).intValue()>0){
			return false;
		}else{
			return true;
		}
	}
	
	/**
	 *获取可添加产品信息
	 */
	public List<Object[]> getCanAddInfos(Integer orderId,String country,String nameColor){
		String sql="SELECT a.product_id,a.`product_name`,a.`color_code`,a.`country_code`,a.id FROM psi_forecast_order_item AS a WHERE a.`forecast_order_id`=:p1 AND a.`display_sta`='1' AND a.`by31sales` IN ('0','2')";
		Parameter para =null;
		if(StringUtils.isEmpty(country)&&StringUtils.isEmpty(nameColor)){
			para=new Parameter(orderId);
		}else if(StringUtils.isNotEmpty(country)&&StringUtils.isEmpty(nameColor)){
			sql=sql+" AND a.`country_code`=:p2 ";	
			para=new Parameter(orderId,country);
		}else if(StringUtils.isEmpty(country)&&StringUtils.isNotEmpty(nameColor)){
			sql=sql+" AND  (CASE WHEN a.`color_code`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END)=:p2";	
			para=new Parameter(orderId,nameColor);
		}else{
			sql=sql+" AND a.`country_code`=:p2  AND  (CASE WHEN a.`color_code`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END)=:p3";	
			para=new Parameter(orderId,country,nameColor);
		}
		return  this.forecastOrderDao.findBySql(sql,para);
	}
	
	
	public List<LcPurchaseOrder> createPurchaseOrder(PsiSupplier supplier,Map<String,Set<Integer>> followProductMap,List<ForecastOrderItem> foreItems,
			Map<Integer,String> receivedMap, Map<String,PsiProductTieredPriceDto> dtoMap,Map<Integer,Integer> productQuantityMap) throws ParseException {

		SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd");
		Integer tax=psiSupplierService.get(supplier.getId()).getTaxRate();
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
				for(ForecastOrderItem forecastItem :foreItems){
					if(!followProductMap.get(userId).contains(forecastItem.getProduct().getId())){
						continue;
					}
					LcPurchaseOrderItem orderItem = new LcPurchaseOrderItem();
					String name=forecastItem.getProductNameColor();
					if(StringUtils.isNotBlank(name)){
						String lineId=nameAndLineIdMap.get(name);
						if(StringUtils.isNotBlank(lineId)&&saleUserMap!=null&&saleUserMap.get(lineId)!=null&&saleUserMap.get(lineId).get(forecastItem.getCountryCode())!=null){
							orderItem.setSalesUser(saleUserMap.get(lineId).get(forecastItem.getCountryCode()).split(",")[1]);
						}
					}
					orderItem.setForecastRemark(forecastItem.getRemark());
					orderItem.setForecastItemId(forecastItem.getId());
					orderItem.setProduct(forecastItem.getProduct());
					orderItem.setColorCode(forecastItem.getColorCode());
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
					String productColor = forecastItem.getProduct().getId()+"_"+forecastItem.getColorCode();
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
		
	@Transactional(readOnly = false)
	public String updateQuantity(Integer itemId,Integer quantity,String flag){
		try{
			String colunmStr="";
			if("0".equals(flag)){
				colunmStr=",a.`sale_quantity`";
			}else if("1".equals(flag)){
				colunmStr=",a.`review_quantity`";
			}else if("2".equals(flag)){
				colunmStr=",a.`boss_quantity`";
			}
			String sql ="UPDATE psi_forecast_order_item AS a SET a.`quantity`=:p2 "+colunmStr+"=:p2 WHERE a.`id`=:p1";
			this.forecastOrderDao.updateBySql(sql, new Parameter(itemId,quantity));
			return "true";
		}catch (Exception ex){
			return "false";
		}
	}
	
	
	@Transactional(readOnly = false)
	public String batchUpdate(List<Integer> itemIds,Integer batchQuantity,String batchRemark,Integer forecastOrderId){
		try{
			String sql ="UPDATE psi_forecast_order_item AS a SET a.`quantity`=:p2,a.`sale_quantity`=:p2 ,a.`remark`=:p3 WHERE a.`id`in :p1 and forecast_order_id=:p4";
			this.forecastOrderDao.updateBySql(sql, new Parameter(itemIds,batchQuantity,batchRemark,forecastOrderId));
			return "true";
		}catch (Exception ex){
			return "false";
		}
	}
	
	
	
	@Transactional(readOnly = false)
	public String updateQuantityAdd(Integer itemId,Integer quantity,String remark,String flag){
			String colunmStr="";
			if("0".equals(flag)){
				colunmStr="SET a.`quantity`=:p2,a.`sale_quantity`=:p2,a.`remark`=:p3,a.display_sta='2'";
			}else if("1".equals(flag)){
				colunmStr="SET a.`quantity`=:p2,a.`review_quantity`=:p2,a.`review_remark`=:p3,a.display_sta='2'";
			}else if("2".equals(flag)){
				colunmStr="SET a.`quantity`=:p2,a.`boss_quantity`=:p2,a.`boss_remark`=:p3,a.display_sta='2'";
			}
			String sql ="UPDATE psi_forecast_order_item AS a "+colunmStr+" WHERE a.`id`=:p1";
			this.forecastOrderDao.updateBySql(sql, new Parameter(itemId,quantity,remark));
			return "";
	}
	
	@Transactional(readOnly = false)
	public String updateQuantityDel(Integer itemId){
		try{
			String sql ="UPDATE psi_forecast_order_item AS a Set a.`quantity`=0,a.display_sta=1 WHERE a.`id`=:p1";
			this.forecastOrderDao.updateBySql(sql, new Parameter(itemId));
			return "true";
		}catch (Exception ex){
			return "false";
		}
	}
	
	/**
	 *取消所有未完成的预测单
	 * 
	 */
	@Transactional(readOnly = false)
	public void updateCancelSta(){
		String sql ="UPDATE psi_forecast_order  AS a SET a.`order_sta`='8' WHERE a.`order_sta` IN('1','3') ";
		this.forecastOrderDao.updateBySql(sql, null);
	}
	
	@Transactional(readOnly = false)
	public String updateRemark(Integer itemId,String remark,String flag){
		try{
			String colunmStr="";
			if("0".equals(flag)){
				colunmStr="a.`remark`";
			}else if("1".equals(flag)){
				colunmStr="a.`review_remark`";
			}else if("2".equals(flag)){
				colunmStr="a.`boss_remark`";
			}
			String sql ="UPDATE psi_forecast_order_item AS a SET "+colunmStr+"=:p2 WHERE a.`id`=:p1";
			this.forecastOrderDao.updateBySql(sql, new Parameter(itemId,remark));
			return "true";
		}catch (Exception ex){
			return "false";
		}
	}

	/**
	 *查询是否有未完成订单 
	 * 
	 */
	public boolean isExistOrder(){
		String sql ="SELECT COUNT(*) FROM psi_forecast_order  WHERE order_sta IN ('1','3')";
		List<BigInteger> list=this.forecastOrderDao.findBySql(sql);
		if(list!=null&&list.get(0).intValue()>0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 
	 * @param targetDate 备货目标日期(备货到选定日期所在周的周日,选定日期最少大于当前时间一个月),
	 * 延期备货模式统一默认下选定备货周缺口,为空时与当前计算法一致
	 * @throws ParseException 
	 */
	@Transactional(readOnly = false)
	public void generateOrder(Date targetDate) throws ParseException{
		boolean delay = false;
		Date date = null;
		if (targetDate != null && targetDate.after(DateUtils.addMonths(new Date(), 1))) {
			//时间换算到周日,即备货到周日
			Calendar c = Calendar.getInstance();
			c.setTime(targetDate);
			c.setFirstDayOfWeek(Calendar.MONDAY);
			c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
			date = c.getTime();
			delay = true;
		}
		//动态计算预测日均销
		Map<String, Double> forecastAvgMap = Maps.newHashMap();
		if (delay) {
			//[name_country [data]]
			Map<String, Map<String, Float>>  forecastDatas = psiInventoryService.getForecastByMonthSalesData();
			forecastAvgMap = getAvg(date, forecastDatas);
		}
		
		List<PsiProduct> list = psiProductService.findAll();
		//产品名,在产数据String
		Map<String, PsiInventoryTotalDto> producting = psiInventoryService.getProducingQuantity();
		Map<String, PsiInventoryTotalDtoByInStock> inventorys = psiInventoryService.getInventoryQuantity();
		Map<String,Map<String, PsiInventoryTotalDto>> rs = psiInventoryService.getTransporttingQuantity();
		Map<String, PsiInventoryTotalDto> transportting = rs.get("1");
		//产品名_国家
		Map<String,ProductSalesInfo> fancha = productSalesInfoService.findAll();
		//产品名_国家 fba
		Map<String, PsiInventoryFba>  fbas = psiInventoryService.getAllProductFbaInfo();
		//查询所有产品在售或淘汰,区分平台和颜色 map<产品名_颜色_国家, isSale>
		Map<String, String> produPositionMap = psiProductEliminateService.findAllProductPosition();
		//查询所有产品是否新品,区分平台和颜色 map<产品名_颜色_国家, isNew>
		Map<String, String> isNewMap = psiProductEliminateService.findIsNewMap();
		//查询定义当前周需要下单的产品(产品名_颜色)
		List<String> orderProducts = psiProductAttributeService.findOrderProducts();
		//查询产品的下单周(产品名_颜色)
		Map<String, Integer> purchaseWeekMap = psiProductAttributeService.findPurchaseWeekMap();
		//获取产品缓冲周期([产品名_颜色, [国家 缓冲周期]])
		Map<String, Map<String, Integer>> productBufferPeriod = psiProductEliminateService.findBufferPeriod();
		//[产品名称_颜色_国家  上架天数]
		Map<String, Integer> onSaleDays = psiProductEliminateService.findOnSaleDays();
		//泛欧标记
		Map<String, String> fanOuMap = psiProductEliminateService.findProductFanOuFlag();
		//产品  国家 类型 计划数（类型 0：促销 1：广告）计划的促销和广告数
		Map<String, Map<String, Map<String, Integer>>> planVolume = planService.findPlanInventory(purchaseWeekMap, targetDate, productBufferPeriod, fancha, fanOuMap);
		//产品  国家 类型 计划数（类型 0：日销 1：有效天数）计划的促销和广告数
		Map<String, Map<String, Map<String, Integer>>> adDaySalesMap = planService.findPlanAdDaySales(targetDate);
		//最近一周的销量[productName_country(含eu)  sales_volume]
		Map<String, Integer> lastWeekSaleMap = psiInventoryService.findLastWeekSale();
		//最近一个月调价超过20%的产品
		Map<String, String> priceChangeMap = findPriceChangeMap();
		//新品
		List<String> isNewList = psiProductEliminateService.findIsNewProductName();
		
		List<PsiSupplier> suppliers = psiSupplierService.findAll();
		Map<Integer, PsiSupplier> supplierMap = Maps.newHashMap();
		for (PsiSupplier psiSupplier : suppliers) {
			supplierMap.put(psiSupplier.getId(), psiSupplier);
		}
		Map<Integer, Integer> productSupplier = psiSupplierService.findProductSupplier();
		
		Date today = DateUtils.getLastDayOfMonth(DateUtils.getDateStart(new Date()));
		Date start = DateUtils.addMonths(today, -1);
		Date end = DateUtils.addMonths(today, 5);
		
		//产品 [国家[月  数]]
		Map<String,Map<String,Map<String,SalesForecastByMonth>>>  data = salesForecastService.findAllWithType(start,end);
		DateFormat monthFormat = new SimpleDateFormat("yyyy-MM");
		List<String> monthList = Lists.newArrayList();	//当前月开始4个月
		for (int i = 1; i < 5; i++) {
			monthList.add(monthFormat.format(DateUtils.addMonths(start,i)));
		}
		
		List<ForecastOrderItem> itemList = Lists.newArrayList();
		List<ForecastOrderItem> newItemList = Lists.newArrayList();
		ForecastOrder order = new ForecastOrder();	//非新品订单(供应链负责)
		ForecastOrder newOrder = new ForecastOrder();	//新品订单(运营负责)
		order.setOrderSta("1");
		order.setCreateDate(new Date());
		User user = UserUtils.getUser();
		if(StringUtils.isEmpty(UserUtils.getUser().getId())){
			user = new User("1");
		}
		
		order.setCreateUser(user);
		order.setTargetDate(targetDate);	//提前备货时的备货时间
		order.setType("0");	//是否新品单标记

		newOrder.setOrderSta("1");
		newOrder.setCreateDate(order.getCreateDate());
		newOrder.setCreateUser(user);
		newOrder.setTargetDate(targetDate);	//提前备货时的备货时间
		newOrder.setType("1");	//是否新品单标记
		for (PsiProduct psiProduct : list) {
			if (!"1".equals(psiProduct.getReviewSta())) {
				continue;
			}
			if ("1".equals(psiProduct.getComponents())) {//配件
				continue;
			}
			for (String country : psiProduct.getPlatform().split(",")) {
				//分产品国家判断装箱数
				Integer packQuantity = psiProduct.getPackQuantity();
				
				if (!"com.unitek".equals(country)) {
					String keyStock = "";
					if ("fr,de,uk,it,es".contains(country)) {
						keyStock = "DE";
					} else if ("com,ca,com2,com3".contains(country)) {
						keyStock = "US";
					} else if ("jp".equals(country)) {//日本新增海外仓
						keyStock = "JP";
					}
					List<String> productNameWithColor = psiProduct.getProductNameWithColor();
					for (int i = 0; i < productNameWithColor.size(); i++) {
						String productName = productNameWithColor.get(i);
						//fanOu 0：完全泛欧  1：uk以外4国泛欧  2：不能泛欧
						String fanOu = fanOuMap.get(productName);
						//筛选,完全泛欧产品欧洲市场只计算德国数据
						if ("0".equals(fanOu) && "fr,uk,es,it".contains(country)) {
							continue;
						}
						//筛选,uk以外4国泛欧产品欧洲市场只计算德国和英国数据
						if ("1".equals(fanOu) && "fr,es,it".contains(country)) {
							continue;
						}
						String key = productName + "_" + country;
						if ("4".equals(produPositionMap.get(key))) {	//淘汰品不下单
							continue;
						}
						String isNew = isNewMap.get(key);
						//产品定位
						String productPosition = produPositionMap.get(key);
						String priceChange = priceChangeMap.get(key);
						String fachaKey = key;
						if ("0".equals(fanOu) && "de".equals(country)) {
							fachaKey = productName + "_eu";
						}
						//fanOu为1时后面需要单独处理UK数据
						if ("1".equals(fanOu) && "de".equals(country)) {
							fachaKey = productName + "_eunouk";
						}
						Integer total = 0;//总计,需要计算
						//在产
						Integer productingNum = 0;
						try {
							productingNum = producting.get(productName).getInventorys().get(country).getQuantity();
						} catch (Exception e) {}
						if ("0,1".contains(fanOu) && "de".equals(country)) {
							try {
								productingNum += producting.get(productName).getInventorys().get("fr").getQuantity();
							} catch (Exception e) {}
							if ("0".equals(fanOu)) {//完全泛欧时才包含uk
								try {
									productingNum += producting.get(productName).getInventorys().get("uk").getQuantity();
								} catch (Exception e) {}
							}
							try {
								productingNum += producting.get(productName).getInventorys().get("it").getQuantity();
							} catch (Exception e) {}
							try {
								productingNum += producting.get(productName).getInventorys().get("es").getQuantity();
							} catch (Exception e) {}
						}
						total += productingNum;
						//中国仓
						Integer cn = 0;
						try {
							cn = inventorys.get(productName).getInventorys().get(country).getQuantityInventory().get("CN").getNewQuantity();
						} catch (Exception e) {}
						if ("0,1".contains(fanOu) && "de".equals(country)) {
							try {
								cn += inventorys.get(productName).getInventorys().get("fr").getQuantityInventory().get("CN").getNewQuantity();
							} catch (Exception e) {}
							if ("0".equals(fanOu)) {//完全泛欧时才包含uk
								try {
									cn += inventorys.get(productName).getInventorys().get("uk").getQuantityInventory().get("CN").getNewQuantity();
								} catch (Exception e) {}
							}
							try {
								cn += inventorys.get(productName).getInventorys().get("it").getQuantityInventory().get("CN").getNewQuantity();
							} catch (Exception e) {}
							try {
								cn += inventorys.get(productName).getInventorys().get("es").getQuantityInventory().get("CN").getNewQuantity();
							} catch (Exception e) {}
						}
						total += cn;
						//在途
						Integer transit = 0;
						try {
							transit = transportting.get(productName).getInventorys().get(country).getQuantity();
						} catch (Exception e) {}
						if ("0,1".contains(fanOu) && "de".equals(country)) {
							try {
								transit += transportting.get(productName).getInventorys().get("fr").getQuantity();
							} catch (Exception e) {}
							if ("0".equals(fanOu)) {
								try {
									transit += transportting.get(productName).getInventorys().get("uk").getQuantity();
								} catch (Exception e) {}
							}
							try {
								transit += transportting.get(productName).getInventorys().get("it").getQuantity();
							} catch (Exception e) {}
							try {
								transit += transportting.get(productName).getInventorys().get("es").getQuantity();
							} catch (Exception e) {}
						}
						total += transit;
						
						//海外仓(实)
						Integer deNew = 0;
						try {
							deNew = inventorys.get(productName).getInventorys().get(country).getQuantityInventory().get(keyStock).getNewQuantity();
						} catch (Exception e) {}
						if ("0,1".contains(fanOu) && "de".equals(country)) {
							try {
								deNew += inventorys.get(productName).getInventorys().get("fr").getQuantityInventory().get(keyStock).getNewQuantity();
							} catch (Exception e) {}
							if ("0".equals(fanOu)) {
								try {
									deNew += inventorys.get(productName).getInventorys().get("uk").getQuantityInventory().get(keyStock).getNewQuantity();
								} catch (Exception e) {}
							}
							try {
								deNew += inventorys.get(productName).getInventorys().get("it").getQuantityInventory().get(keyStock).getNewQuantity();
							} catch (Exception e) {}
							try {
								deNew += inventorys.get(productName).getInventorys().get("es").getQuantityInventory().get(keyStock).getNewQuantity();
							} catch (Exception e) {}
							//海外仓算上美国运输过去的库存
							try {
								deNew += inventorys.get(productName).getInventorys().get("com").getQuantityInventory().get(keyStock).getNewQuantity();
							} catch (Exception e) {}
						}
						if ("com2".equals(country)) {
							//海外仓算上老账号美国库存
							try {
								deNew += inventorys.get(productName).getInventorys().get("com").getQuantityInventory().get(keyStock).getNewQuantity();
							} catch (Exception e) {}
							
						}
						total += deNew;
						//加上fba库存
						int fba = 0;
						try {
							fba = fbas.get(key).getTotal();
						} catch (Exception e) {}
						if ("0,1".contains(fanOu) && "de".equals(country)) {
							try {
								fba += fbas.get(productName + "_fr").getTotal();
							} catch (Exception e) {}
							if ("0".equals(fanOu)) {
								try {
									fba += fbas.get(productName + "_uk").getTotal();
								} catch (Exception e) {}
							}
							try {
								fba += fbas.get(productName + "_it").getTotal();
							} catch (Exception e) {}
							try {
								fba += fbas.get(productName + "_es").getTotal();
							} catch (Exception e) {}
						}
						total += fba;
						//2017-06-12新增,计划的促销和广告预留数量,即总库存需要排除的库存数
						int promotion = 0;//预测周期内的促销数
						try {
							promotion = planVolume.get(productName).get(country).get("0");
						} catch (Exception e) {}
						if ("0,1".contains(fanOu) && "de".equals(country)) {
							try {
								promotion += planVolume.get(productName).get("fr").get("0");
							} catch (Exception e) {}
							if ("0".equals(fanOu)) {
								try {
									promotion += planVolume.get(productName).get("uk").get("0");
								} catch (Exception e) {}
							}
							try {
								promotion += planVolume.get(productName).get("it").get("0");
							} catch (Exception e) {}
							try {
								promotion += planVolume.get(productName).get("es").get("0");
							} catch (Exception e) {}
						}
						total = total - promotion; //总库存减去目前计划中的促销和广告数量
						int ad = 0;
						try {
							ad = planVolume.get(productName).get(country).get("1");
						} catch (Exception e) {}
						if ("0,1".contains(fanOu) && "de".equals(country)) {
							try {
								ad += planVolume.get(productName).get("fr").get("1");
							} catch (Exception e) {}
							if ("0".equals(fanOu)) {
								try {
									ad += planVolume.get(productName).get("uk").get("1");
								} catch (Exception e) {}
							}
							try {
								ad += planVolume.get(productName).get("it").get("1");
							} catch (Exception e) {}
							try {
								ad += planVolume.get(productName).get("es").get("1");
							} catch (Exception e) {}
						}
						total = total - ad; //总库存减去目前计划中的促销和广告数量
						//产品总库存计算完毕
						//正常情况下按照实际的31日销计算
						int fancha31Sales = 0;
						if (fancha.get(fachaKey) != null) {
							fancha31Sales = fancha.get(fachaKey).getDay31Sales();
							if (ad > 0) {
								fancha31Sales = fancha.get(fachaKey).getRealDay31Sales();
							}
						}
						
						//安全库存量&可销天&下单点
						double safe = 0; //安全库存量
						try {
							safe = fancha.get(fachaKey).getPeriodSqrt()*fancha.get(fachaKey).getVariance()*2.33;
						} catch (Exception e) {
						}
						//预测&方差31日销量排除了广告和促销数据，下单点计算时加入计划广告数(促销为单周一次性计划，不需要纳入计划)
						double pointForecast = 0;	//预测数据下单点
						double pointDay31Sales = 0;	//31日销下单点
						double daySale31 = 0;	//31日内日均销量
						if (fancha.get(fachaKey) != null && fancha31Sales > 0) {
							int days = 31;
							if (onSaleDays.get(key) != null) {
								days = onSaleDays.get(key);
							}
							daySale31 = fancha31Sales/(double)days;
							if ("1".equals(isNewMap.get(key)) && lastWeekSaleMap.get(fachaKey) != null) {	//新品按最近一周销量推算
								daySale31 = lastWeekSaleMap.get(fachaKey)/(double)7;
							}
						}
						int bufferPeriod = 0;	//缓冲周期
						if (productBufferPeriod.get(productName)!=null && productBufferPeriod.get(productName).get(country) != null) {
							bufferPeriod = productBufferPeriod.get(productName).get(country);	//缓冲周期
						}
						if ("jp".equals(country) && bufferPeriod > 0) {
							bufferPeriod += 7;	//日本国家延长7天
						}
						
						double daySaleForecast = 0;
						try {
							// 采用指定周期计算预测日均销
							if (delay) {
								daySaleForecast = forecastAvgMap.get(fachaKey);
							} else {
								daySaleForecast = fancha.get(fachaKey).getForecastPreiodAvg();
							}
						} catch (NullPointerException e) {}
						//TODO 广告销量计入下单点(考虑截止时间是否在下单点计算周期内(生产+运输+缓冲fancha.get(fachaKey).getPeriod() + bufferPeriod))
						int adDaySales = 0;
						try {
							adDaySales = adDaySalesMap.get(productName).get(country).get("0");
						} catch (Exception e) {}
						if ("0,1".contains(fanOu) && "de".equals(country)) {
							try {
								adDaySales += adDaySalesMap.get(productName).get("fr").get("0");
							} catch (Exception e) {}
							if ("0".equals(fanOu)) {
								try {
									adDaySales += adDaySalesMap.get(productName).get("uk").get("0");
								} catch (Exception e) {}
							}
							try {
								adDaySales += adDaySalesMap.get(productName).get("it").get("0");
							} catch (Exception e) {}
							try {
								adDaySales += adDaySalesMap.get(productName).get("es").get("0");
							} catch (Exception e) {}
						}
						Calendar cal = Calendar.getInstance();
						int day = 8 - cal.get(Calendar.DAY_OF_WEEK);	//当前天到周日的天数
						try{
							if (fancha.get(fachaKey).getForecastPreiodAvg() != null && fancha.get(fachaKey).getForecastPreiodAvg() > 0) {
								pointForecast = daySaleForecast * (fancha.get(fachaKey).getPeriod() + bufferPeriod) + safe;
								pointForecast += daySaleForecast * day;	//推算到周日
								if (adDaySales > 0) {
									int days = adDaySalesMap.get(productName).get(country).get("1");
									if (days == -1 || days > (fancha.get(fachaKey).getPeriod() + bufferPeriod)) {
										pointForecast += adDaySales * (fancha.get(fachaKey).getPeriod() + bufferPeriod);
									} else {
										pointForecast += adDaySales * days;
									}
								}
							}
						}catch (Exception e) {}
						try{
							if (fancha.get(fachaKey) != null && fancha31Sales > 0) {
								pointDay31Sales = daySale31 * (fancha.get(fachaKey).getPeriod() + bufferPeriod) + safe;
								pointDay31Sales += daySale31 * day;	//推算到周日
								if (adDaySales > 0) {
									int days = adDaySalesMap.get(productName).get(country).get("1");
									if (days == -1 || days > (fancha.get(fachaKey).getPeriod() + bufferPeriod)) {
										pointDay31Sales += adDaySales * (fancha.get(fachaKey).getPeriod() + bufferPeriod);
									} else {
										pointDay31Sales += adDaySales * days;
									}
								}
							}
						}catch (Exception e) {}
						//以下分两种方式的下单点分别计算下单量,只要有一种下单量大于0则需要下单,两种方式都需要下单的情况下以预测方式下单
						//结余
						double balanceForecast = total - pointForecast;	//预测数据计算结余
						double balanceDay31Sales = total - pointDay31Sales;	//31日销计算结余
						if (delay) {
							int days = (int)((date.getTime() - new Date().getTime())/86400000);
							balanceForecast = balanceForecast - daySaleForecast * (days - day - 21);	//减去21算出第一周,后面加21天销量回到第四周缺口
							balanceDay31Sales = balanceDay31Sales - daySale31 * (days - day - 21);
						}
						if (delay) {	//备货模式下忽略下单点,但是考虑安全库存
							balanceForecast = balanceForecast + pointForecast - safe;
							balanceDay31Sales = balanceDay31Sales + pointDay31Sales - safe;
						}
						//预测下单量
						String color = "";
						if (productName.split("_").length == 2) {
							color = productName.split("_")[1];
						}

						int day31Sales = 0; //31日销
						int realDay31Sales = 0; //去营销数后31日销
						if (fancha.get(fachaKey) != null) {
							day31Sales = fancha.get(fachaKey).getDay31Sales();
							realDay31Sales = fancha.get(fachaKey).getRealDay31Sales();
						}
						boolean flag = false;	//标记是否已下单,默认：否
						int period = fancha.get(fachaKey) != null ?fancha.get(fachaKey).getPeriod():0;
						period += bufferPeriod;
						if (balanceForecast < 0) {
							flag = true;
							ForecastOrderItem item = new ForecastOrderItem();
							//设置预留的促销数和广告数
							item.setPromotionQuantity(promotion);	//促销数量
							item.setPromotionBossQuantity(ad);	//广告数量
							double base = -balanceForecast;
							int orderQuantity = getOrderQuantity(psiProduct, base,packQuantity);
							item.setForecast1week(orderQuantity);
							//采购期预日销
//							double daySaleForecast = 0;
//							if(fancha.get(fachaKey) !=null && fancha.get(fachaKey).getForecastPreiodAvg() != null){
//								daySaleForecast = fancha.get(fachaKey).getForecastPreiodAvg();
//							}
							item.setForecast2week(getOrderQuantity(psiProduct, base + daySaleForecast*7,packQuantity));
							item.setForecast3week(getOrderQuantity(psiProduct, base + daySaleForecast*14,packQuantity));
							item.setForecast4week(getOrderQuantity(psiProduct, base + daySaleForecast*21,packQuantity));
							if (delay) { //采购周下单产品依据预测销量推测下单,延迟补单一律补到第四周
								item.setBy31sales("2");
								item.setQuantity(item.getForecast4week());
								item.setByWeek("3");
							} else if(purchaseWeekMap.get(productName) != null){
								item.setBy31sales("2"); //采购周下单产品依据预测销量推测下单
								if(purchaseWeekMap.get(productName)==0){
									item.setQuantity(item.getForecast4week());	//当前周下单产品
									item.setByWeek("3");
								} else if (purchaseWeekMap.get(productName)==1) {
									item.setQuantity(item.getForecast1week());	//下周下单产品,补全到下周缺口
									item.setByWeek("0");
								} else if (purchaseWeekMap.get(productName)==2) {
									item.setQuantity(item.getForecast2week());
									item.setByWeek("1");
									//item.setPromotionBossQuantity(ad * 2);	//广告数量
								} else if (purchaseWeekMap.get(productName)==3) {
									item.setQuantity(item.getForecast3week());
									item.setByWeek("2");
									//item.setPromotionBossQuantity(ad * 3);	//广告数量
								}
							} else {
								item.setBy31sales("0"); //依据预测销量推测下单
								item.setQuantity(item.getForecast1week());
								item.setByWeek("0");
							}
							item.setCountryCode(country);
							item.setColorCode(color);
							item.setProduct(psiProduct);
							item.setProductName(productName.split("_")[0]);
							try {
								item.setSupplier(supplierMap.get(productSupplier.get(psiProduct.getId())));
							} catch (Exception e) {}
							
							item.setTotalStock(total);
							item.setSafeStock(MathUtils.roundUp(safe));
							item.setPeriod(period);
							//保存四个月的预测销量
							if (data.get(productName) != null && data.get(productName).get(country) != null) {
								item.setForecast1month(data.get(productName).get(country).get(monthList.get(0)).getQuantityForecast());
								item.setForecast2month(data.get(productName).get(country).get(monthList.get(1)).getQuantityForecast());
								item.setForecast3month(data.get(productName).get(country).get(monthList.get(2)).getQuantityForecast());
								try {
									item.setForecast4month(data.get(productName).get(country).get(monthList.get(3)).getQuantityForecast());
								} catch (NullPointerException e) {
									item.setForecast4month(0);
									WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, productName + "在" + country + "没有预测销量,是否未及时更新淘汰属性！");
								}
							}
							item.setDay31sales(day31Sales);
							item.setRealDay31sales(realDay31Sales);
							item.setIsNew(isNew);
							item.setIsMain(productPosition);
							item.setPriceChange(priceChange);
							item.setDisplaySta("0");
							item.setPeriodBuffer(bufferPeriod);
							if (isNewList.contains(productName)) {	//新品由运营负责
								item.setForecastOrder(newOrder);
								newItemList.add(item);
							} else {
								item.setForecastOrder(order);
								itemList.add(item);
							}
						} 
						//如果当前周需要下单的产品没有达到下单点,计算出第四周的下单量,如果第四周达到下单条件也要下单(延期补单一律计算到第四周)
						else if((orderProducts.contains(productName) || delay) && !flag){
							//采购期预日销
//							double daySaleForecast = 0;
//							if(fancha.get(fachaKey) != null && fancha.get(fachaKey).getForecastPreiodAvg() != null){
//								daySaleForecast = fancha.get(fachaKey).getForecastPreiodAvg();
//							}
							double base = -balanceForecast;
							double forecast4week = base + daySaleForecast*21;
							if (forecast4week > 0) {	//计算出第四周需要下单
								flag = true;	//标记已经下单
								int orderQuantity = getOrderQuantity(psiProduct, forecast4week,packQuantity);
								ForecastOrderItem item = new ForecastOrderItem();
								//设置预留的促销数和广告数
								item.setPromotionQuantity(promotion);	//促销数量
								item.setPromotionBossQuantity(ad);	//广告数量
								item.setBy31sales("2"); //当前周下单产品依据预测销量推测下单
								item.setQuantity(orderQuantity);
								item.setForecast4week(orderQuantity);
								item.setByWeek("3");
								item.setForecast1week(getOrderQuantity(psiProduct, base,packQuantity));
								item.setForecast2week(getOrderQuantity(psiProduct, base + daySaleForecast*7,packQuantity));
								item.setForecast3week(getOrderQuantity(psiProduct, base + daySaleForecast*14,packQuantity));
								item.setCountryCode(country);
								item.setColorCode(color);
								item.setProduct(psiProduct);
								item.setProductName(productName.split("_")[0]);
								try {
									item.setSupplier(supplierMap.get(productSupplier.get(psiProduct.getId())));
								} catch (Exception e) {}
								
								item.setTotalStock(total);
								item.setSafeStock(MathUtils.roundUp(safe));
								item.setPeriod(period);
								//保存四个月的预测销量
								if (data.get(productName) != null && data.get(productName).get(country) != null) {
									item.setForecast1month(data.get(productName).get(country).get(monthList.get(0)).getQuantityForecast());
									item.setForecast2month(data.get(productName).get(country).get(monthList.get(1)).getQuantityForecast());
									item.setForecast3month(data.get(productName).get(country).get(monthList.get(2)).getQuantityForecast());
									item.setForecast4month(data.get(productName).get(country).get(monthList.get(3)).getQuantityForecast());
								}
								item.setDay31sales(day31Sales);
								item.setRealDay31sales(realDay31Sales);
								item.setIsNew(isNew);
								item.setIsMain(productPosition);
								item.setPriceChange(priceChange);
								item.setDisplaySta("0");
								item.setPeriodBuffer(bufferPeriod);
								if (isNewList.contains(productName)) {	//新品由运营负责
									item.setForecastOrder(newOrder);
									newItemList.add(item);
								} else {
									item.setForecastOrder(order);
									itemList.add(item);
								}
							}
							
						}
						if (!flag) {	//不需要下单的产品,分两种方式计算出缺口保存
							//预测方式
							ForecastOrderItem item = new ForecastOrderItem();
							//设置预留的促销数和广告数
							item.setPromotionQuantity(promotion);	//促销数量
							item.setPromotionBossQuantity(ad);	//广告数量
							double base = -balanceForecast;
							int orderQuantity = getOrderQuantity(psiProduct, base,packQuantity);
							item.setForecast1week(orderQuantity);
							//采购期预日销
//							double daySaleForecast = 0;
//							if(fancha.get(fachaKey) !=null && fancha.get(fachaKey).getForecastPreiodAvg() != null){
//								daySaleForecast = fancha.get(fachaKey).getForecastPreiodAvg();
//							}
							item.setForecast2week(getOrderQuantity(psiProduct, base + daySaleForecast*7,packQuantity));
							item.setForecast3week(getOrderQuantity(psiProduct, base + daySaleForecast*14,packQuantity));
							item.setForecast4week(getOrderQuantity(psiProduct, base + daySaleForecast*21,packQuantity));
							if(purchaseWeekMap.get(productName) != null){
								item.setBy31sales("2"); //采购周下单产品依据预测销量推测下单
							} else {
								item.setBy31sales("0"); //依据预测销量推测下单
							}
							//item.setQuantity(item.getForecast1week());
							item.setQuantity(0);
							if(purchaseWeekMap.get(productName) != null){
								if(purchaseWeekMap.get(productName)==0){
									item.setByWeek("3");
								} else if (purchaseWeekMap.get(productName)==1) {
									item.setByWeek("0");
								} else if (purchaseWeekMap.get(productName)==2) {
									item.setByWeek("1");
									//item.setPromotionBossQuantity(ad * 2);	//广告数量
								} else if (purchaseWeekMap.get(productName)==3) {
									item.setByWeek("2");
									//item.setPromotionBossQuantity(ad * 3);	//广告数量
								}
							}
							item.setCountryCode(country);
							item.setColorCode(color);
							item.setProduct(psiProduct);
							item.setProductName(productName.split("_")[0]);
							try {
								item.setSupplier(supplierMap.get(productSupplier.get(psiProduct.getId())));
							} catch (Exception e) {}
							
							item.setTotalStock(total);
							item.setSafeStock(MathUtils.roundUp(safe));
							item.setPeriod(period);
							//保存四个月的预测销量
							if (data.get(productName) != null && data.get(productName).get(country) != null) {
								item.setForecast1month(data.get(productName).get(country).get(monthList.get(0)).getQuantityForecast());
								item.setForecast2month(data.get(productName).get(country).get(monthList.get(1)).getQuantityForecast());
								item.setForecast3month(data.get(productName).get(country).get(monthList.get(2)).getQuantityForecast());
								try {
									item.setForecast4month(data.get(productName).get(country).get(monthList.get(3)).getQuantityForecast());
								} catch (NullPointerException e) {
									item.setForecast4month(0);
									WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, productName + "在" + country + "没有预测销量,是否未及时更新淘汰属性！");
								}
							}
							item.setDay31sales(day31Sales);
							item.setRealDay31sales(realDay31Sales);
							item.setIsNew(isNew);
							item.setIsMain(productPosition);
							item.setPriceChange(priceChange);
							item.setDisplaySta("1");	//标记系统计算不需要下单
							item.setPeriodBuffer(bufferPeriod);
							if (isNewList.contains(productName)) {	//新品由运营负责
								item.setForecastOrder(newOrder);
								newItemList.add(item);
							} else {
								item.setForecastOrder(order);
								itemList.add(item);
							}
						}
					}
				}
			}
		}
		//添加备注
		Map<String, Map<String, ProductInventoryTotalDto>> dtoMap = 
				getProductGap(list, producting, inventorys, transportting, fancha, fbas, 
				orderProducts, productBufferPeriod, onSaleDays, forecastAvgMap, date, planVolume, adDaySalesMap, isNewMap, lastWeekSaleMap,fanOuMap);
		for (ForecastOrderItem forecastOrderItem : itemList) {
			if (forecastOrderItem.getQuantity() < 0) {
				continue;
			}
			String colorName = forecastOrderItem.getProductName();
			if (StringUtils.isNotEmpty(forecastOrderItem.getColorCode())) {
				colorName = colorName + "_" + forecastOrderItem.getColorCode();
			}
			Map<String, ProductInventoryTotalDto> proMap = dtoMap.get(colorName);
			//if (proMap != null && "1".equals(proMap.get("total").getAdjust())) { //即使总数据有缺口也提示具体国家的可调剂数量
			if (proMap != null) {
				String fanOu = fanOuMap.get(colorName);
				String countryCode = forecastOrderItem.getCountryCode();
				//四国泛欧产品只在fr,de,es,it四国可以调剂,不能泛欧产品不能调剂
				if ("mx".equals(countryCode) || ("1".equals(fanOu) && !"fr,de,es,it".contains(countryCode)) || "2".equals(fanOu)) {
					continue;
				}
				
				StringBuilder tips = new StringBuilder("");
				for (Map.Entry<String, ProductInventoryTotalDto> countryEntry : proMap.entrySet()) {
					String country = countryEntry.getKey();
					if ("mx".equals(country) || ("1".equals(fanOu) && !"fr,de,es,it".contains(country)) || "2".equals(fanOu)) {
						continue;
					}
					ProductInventoryTotalDto dto = proMap.get(country);
					if (!"total".equals(country) && "1".equals(dto.getAdjust()) && dto.getAdjustNum() > 0) {
						tips.append("<br/>").append( ("com".equals(country)?"us":country) ).append(":").append(dto.getAdjustNum());
						//tips = tips + "<br/>" + country + ":" + dto.getAdjustNum() + "  在产：" + dto.getProducting() + "  中国仓：" + dto.getCn();
					}
				}
				if (StringUtils.isNotEmpty(tips)) {
					forecastOrderItem.setTips( "可调剂数量："+tips.toString());
				}
			}
		}
		this.save(order);
		this.saveItemList(itemList);
		this.save(newOrder);
		this.saveItemList(newItemList);
	}

	/**
	 * 根据库存和销量等信息计算产品缺口相关信息
	 * @param list	所有产品列表
	 * @param producting	在产数据,	产品名,在产数据String
	 * @param inventorys	中国仓数据	产品名,数据String
	 * @param transportting 在途数据	产品名,数据String
	 * @param fancha		方差 产品名_国家
	 * @param fbas			fba库存 产品名_国家 fba
	 * @param isSaleMap		在售/淘汰,区分平台和颜色 map<产品名_颜色_国家, isSale>
	 * @param orderProducts 当前周需要下单的产品(产品名_颜色)
	 * @param productBufferPeriod	产品缓冲周期([产品名_颜色[国家 缓冲周期]])
	 * @param planVolume 计划促销和广告预留库存
	 * @return Map<产品名_颜色, Map<国家(总计:total), ProductInventoryTotalDto>>
	 */
	private Map<String, Map<String, ProductInventoryTotalDto>> getProductGap(List<PsiProduct> list, Map<String, PsiInventoryTotalDto> producting, Map<String, 
			PsiInventoryTotalDtoByInStock> inventorys, Map<String, PsiInventoryTotalDto> transportting, Map<String,ProductSalesInfo> fancha, 
			Map<String, PsiInventoryFba>  fbas, List<String> orderProducts, Map<String, Map<String, Integer>> productBufferPeriod, Map<String, Integer> onSaleDays, 
			Map<String, Double> forecastAvgMap, Date date, Map<String, Map<String, Map<String, Integer>>> planVolume, Map<String, Map<String, Map<String, Integer>>> adDaySalesMap,
			Map<String, String> isNewMap, Map<String, Integer> lastWeekSaleMap, Map<String, String> fanOuMap){ 

		Map<String, Map<String, ProductInventoryTotalDto>> rs = Maps.newHashMap();
		for (PsiProduct psiProduct : list) {
			if ("0".equals(psiProduct.getIsSale())) {
				continue;	//淘汰品跳过
			}
			if ("1".equals(psiProduct.getComponents())) {//配件
				continue;
			}
			List<String> productNameWithColor = psiProduct.getProductNameWithColor();
			for (int i = 0; i < productNameWithColor.size(); i++) {
				String productName = productNameWithColor.get(i);
				String fanOu = fanOuMap.get(productName);
				//产品库存map
				Map<String, ProductInventoryTotalDto> productMap = rs.get(productName);
				if (productMap == null) {
					productMap = Maps.newHashMap();
					rs.put(productName, productMap);
				}
				ProductInventoryTotalDto totalDto = new ProductInventoryTotalDto();
				productMap.put("total", totalDto);	//单品各平台总计
				for (String country : psiProduct.getPlatform().split(",")) {
					//四国泛欧产品只在fr,de,es,it四国可以调剂,不能泛欧产品不能调剂
					if ("mx".equals(country) || ("1".equals(fanOu) && !"fr,de,es,it".contains(country)) || "2".equals(fanOu)) {
						continue;
					}
					if (!"com.unitek".equals(country)) {
						String keyStock = "";
						if ("fr,de,uk,it,es".contains(country)) {
							keyStock = "DE";
						} else if ("com,ca".contains(country)) {
							keyStock = "US";
						} else if ("jp".equals(country)) {//日本新增海外仓
							keyStock = "JP";
						}
						String key = productName + "_" + country;
						String fachaKey = key;
						if ("0".equals(fanOu) && "de".equals(country)) {
							fachaKey = productName + "_eu";
						}
						//fanOu为1时后面需要单独处理UK数据
						if ("1".equals(fanOu) && "de".equals(country)) {
							fachaKey = productName + "_eunouk";
						}
						ProductInventoryTotalDto dto = productMap.get(country);
						if (dto == null) {
							dto = new ProductInventoryTotalDto();
							dto.setAdjust("0");
							productMap.put(country, dto);
						}
						Integer total = 0;//总计,需要计算
						//在产
						Integer productingNum = 0;
						try {
							productingNum = producting.get(productName).getInventorys().get(country).getQuantity();
						} catch (NullPointerException e) {}
						dto.setProducting(productingNum);
						totalDto.setProducting(totalDto.getProducting() + productingNum);
						total += productingNum;
						//中国仓
						Integer cn = 0;
						try {
							cn = inventorys.get(productName).getInventorys().get(country).getQuantityInventory().get("CN").getNewQuantity();
						} catch (NullPointerException e) {}
						dto.setCn(cn);
						totalDto.setCn(totalDto.getCn() + cn);
						total += cn;
						//在途
						Integer transit = 0;
						try {
							transit = transportting.get(productName).getInventorys().get(country).getQuantity();
						} catch (NullPointerException e) {}
						dto.setTransportting(transit);
						totalDto.setTransportting(totalDto.getTransportting() + transit);
						total += transit;
						
						//海外仓(实)
						Integer deNew = 0;
						try {
							deNew = inventorys.get(productName).getInventorys().get(country).getQuantityInventory().get(keyStock).getNewQuantity();
						} catch (NullPointerException e) {}
						dto.setOverseas(deNew);
						totalDto.setOverseas(totalDto.getOverseas() + deNew);
						total += deNew;
						//加上fba库存
						int fba = 0;
						try {
							fba = fbas.get(key).getTotal();
						} catch (NullPointerException e) {}
						dto.setFbas(fba);
						totalDto.setFbas(totalDto.getFbas() + fba);
						total += fba;
						//2017-06-12新增,计划的促销和广告预留数量,即总库存需要排除的库存数
						int promotion = 0;//预测周期内的促销数
						try {
							promotion = planVolume.get(productName).get(country).get("0");
						} catch (Exception e) {}
						if ("0,1".contains(fanOu) && "de".equals(country)) {
							try {
								promotion += planVolume.get(productName).get("fr").get("0");
							} catch (Exception e) {}
							if ("0".equals(fanOu)) {
								try {
									promotion += planVolume.get(productName).get("uk").get("0");
								} catch (Exception e) {}
							}
							try {
								promotion += planVolume.get(productName).get("it").get("0");
							} catch (Exception e) {}
							try {
								promotion += planVolume.get(productName).get("es").get("0");
							} catch (Exception e) {}
						}
						total = total - promotion; //总库存减去目前计划中的促销和广告预留数量
						int ad = 0;//预测周期内的广告数
						try {
							ad = planVolume.get(productName).get(country).get("1");
						} catch (Exception e) {}
						if ("0,1".contains(fanOu) && "de".equals(country)) {
							try {
								ad += planVolume.get(productName).get("fr").get("1");
							} catch (Exception e) {}
							if ("0".equals(fanOu)) {
								try {
									ad += planVolume.get(productName).get("uk").get("1");
								} catch (Exception e) {}
							}
							try {
								ad += planVolume.get(productName).get("it").get("1");
							} catch (Exception e) {}
							try {
								ad += planVolume.get(productName).get("es").get("1");
							} catch (Exception e) {}
						}
						total = total - ad; //总库存减去目前计划中的促销和广告预留数量
						dto.setTotal(total);
						totalDto.setTotal(totalDto.getTotal() + total);
						//产品总库存计算完毕
						//正常情况下按照实际的31日销计算
						int fancha31Sales = 0;
						if (fancha.get(fachaKey) != null) {
							fancha31Sales = fancha.get(fachaKey).getDay31Sales();
							if (ad > 0) {
								fancha31Sales = fancha.get(fachaKey).getRealDay31Sales();
							}
						}
						//安全库存量&可销天&下单点
						double safe = 0; //安全库存量
						try {
							safe = fancha.get(fachaKey).getPeriodSqrt()*fancha.get(fachaKey).getVariance()*2.33;
						} catch (NullPointerException e) {}
						double pointForecast = 0;	//预测数据下单点
						double pointDay31Sales = 0;	//31日销下单点
						double daySale31 = 0;	//31日内日均销量
						if (fancha.get(fachaKey) != null && fancha31Sales > 0) {
							int days = 31;
							if (onSaleDays.get(key) != null) {
								days = onSaleDays.get(key) - 1;
							}
							daySale31 = fancha31Sales/(double)days;
							if ("1".equals(isNewMap.get(key)) && lastWeekSaleMap.get(fachaKey) != null) {	//新品按最近一周销量推算
								daySale31 = lastWeekSaleMap.get(fachaKey)/(double)7;
							}
						}
						int bufferPeriod = 0;	//缓冲周期
						if (productBufferPeriod.get(productName)!=null && productBufferPeriod.get(productName).get(country) != null) {
							bufferPeriod = productBufferPeriod.get(productName).get(country);	//缓冲周期
						}
						if ("jp".equals(country) && bufferPeriod > 0) {
							bufferPeriod += 7;	//日本国家延长7天
						}
						
						double daySaleForecast = 0;
						try {
							if (forecastAvgMap.size() > 0) {	//延期备货时动态计算预日销
								daySaleForecast = forecastAvgMap.get(fachaKey);
							} else {
								daySaleForecast = fancha.get(fachaKey).getForecastPreiodAvg();
							}
						} catch (NullPointerException e) {}
						//TODO 广告销量计入下单点(考虑截止时间是否在下单点计算周期内(生产+运输+缓冲fancha.get(fachaKey).getPeriod() + bufferPeriod))
						int adDaySales = 0;
						try {
							adDaySales = adDaySalesMap.get(productName).get(country).get("0");
						} catch (Exception e) {}
						if ("0,1".contains(fanOu) && "de".equals(country)) {
							try {
								adDaySales += adDaySalesMap.get(productName).get("fr").get("0");
							} catch (Exception e) {}
							if ("0".equals(fanOu)) {
								try {
									adDaySales += adDaySalesMap.get(productName).get("uk").get("0");
								} catch (Exception e) {}
							}
							try {
								adDaySales += adDaySalesMap.get(productName).get("it").get("0");
							} catch (Exception e) {}
							try {
								adDaySales += adDaySalesMap.get(productName).get("es").get("0");
							} catch (Exception e) {}
						}
						
						Calendar cal = Calendar.getInstance();
						int day = 8 - cal.get(Calendar.DAY_OF_WEEK);	//当前天到周日的天数
						try{
							if (fancha.get(fachaKey).getForecastPreiodAvg() != null && fancha.get(fachaKey).getForecastPreiodAvg() > 0) {
								pointForecast = daySaleForecast * (fancha.get(fachaKey).getPeriod() + bufferPeriod) + safe;
								pointForecast += daySaleForecast * day;	//推算到周日
								if (adDaySales > 0) {
									int days = adDaySalesMap.get(productName).get(country).get("1");
									if (days == -1 || days > (fancha.get(fachaKey).getPeriod() + bufferPeriod)) {
										pointForecast += adDaySales * (fancha.get(fachaKey).getPeriod() + bufferPeriod);
									} else {
										pointForecast += adDaySales * days;
									}
								}
							}
						}catch (Exception e) {}
						try{
							if (fancha.get(fachaKey) != null && fancha31Sales > 0) {
								pointDay31Sales = daySale31 * (fancha.get(fachaKey).getPeriod() + bufferPeriod) + safe;
								pointDay31Sales += daySale31 * day;	//推算到周日
								if (adDaySales > 0) {
									int days = adDaySalesMap.get(productName).get(country).get("1");
									if (days == -1 || days > (fancha.get(fachaKey).getPeriod() + bufferPeriod)) {
										pointDay31Sales += adDaySales * (fancha.get(fachaKey).getPeriod() + bufferPeriod);
									} else {
										pointDay31Sales += adDaySales * days;
									}
								}
							}
						}catch (Exception e) {}
						//以下分两种方式的下单点分别计算下单量,只要有一种下单量大于0则需要下单,两种方式都需要下单的情况下以预测方式下单
						//结余
						double balanceForecast = total - pointForecast;	//预测数据计算结余
						double balanceDay31Sales = total - pointDay31Sales;	//31日销计算结余
						if (forecastAvgMap.size() > 0) {
							int days = (int)((date.getTime() - new Date().getTime())/86400000);
							balanceForecast = balanceForecast - daySaleForecast * (days - day);	//第四周缺口
							balanceDay31Sales = balanceDay31Sales - daySale31 * (days - day);
						}
						dto.setBalanceForecast(balanceForecast);
						totalDto.setBalanceForecast(totalDto.getBalanceForecast() + balanceForecast);
						dto.setBalanceDay31Sales(balanceDay31Sales);
						totalDto.setBalanceDay31Sales(totalDto.getBalanceDay31Sales() + balanceDay31Sales);

						//采购期预日销
//						double daySaleForecast = 0;
//						if(fancha.get(fachaKey) != null && fancha.get(fachaKey).getForecastPreiodAvg() != null){
//							daySaleForecast = fancha.get(fachaKey).getForecastPreiodAvg();
//						}
						dto.setDaySaleForecast(daySaleForecast);
						totalDto.setDaySaleForecast(totalDto.getDaySaleForecast() + daySaleForecast);
						dto.setDaySale31(daySale31);
						totalDto.setDaySale31(totalDto.getDaySale31() + daySale31);
						
						double saleForecast = 0;//预测数据计算销售期预月销
						double saleDay31Sales = 0;//31日销计算销售期预月销
						double xiadanForecast = 0;	//判断是否下单的数量
						double xiadanDay31Sales = 0;	//判断是否下单的数量
						try{
							if (fancha.get(fachaKey).getForecastAfterPreiodSalesByMonth() != null && fancha.get(fachaKey).getForecastAfterPreiodSalesByMonth() > 0) {
								saleForecast = fancha.get(fachaKey).getForecastAfterPreiodSalesByMonth();
							}
							if (saleForecast > 0 && productBufferPeriod.get(productName) != null && productBufferPeriod.get(productName).get(country) != null) { //除以31*缓冲周期
								saleForecast = saleForecast/31*productBufferPeriod.get(productName).get(country);
								int period = productBufferPeriod.get(productName).get(country)>28?(productBufferPeriod.get(productName).get(country) - 28) : 0;
								xiadanForecast = saleForecast/31*period;
							}
						} catch (Exception e) {}
						try{
							if (fancha.get(fachaKey) != null && fancha31Sales > 0) {
								saleDay31Sales = fancha31Sales;
							}
							if (saleDay31Sales > 0 && productBufferPeriod.get(productName) != null && productBufferPeriod.get(productName).get(country) != null) { //除以31*缓冲周期
								saleDay31Sales = saleDay31Sales/31*productBufferPeriod.get(productName).get(country);
								int period = productBufferPeriod.get(productName).get(country)>28?(productBufferPeriod.get(productName).get(country) - 28) : 0;
								xiadanDay31Sales = saleDay31Sales/31*period;
							}
						} catch (Exception e) {}
						dto.setSaleForecast(saleForecast);
						dto.setSaleDay31Sales(saleDay31Sales);
						dto.setXiadanForecast(xiadanForecast);
						dto.setXiadanDay31Sales(xiadanDay31Sales);
						
						
						//分产品国家判断装箱数
						Integer packQuantity = psiProduct.getPackQuantity();
						
						dto.setPackQuantity(packQuantity);	//装箱数
						totalDto.setSaleForecast(totalDto.getSaleForecast() + saleForecast);
						totalDto.setSaleDay31Sales(totalDto.getSaleDay31Sales() + saleDay31Sales);
						totalDto.setXiadanForecast(totalDto.getXiadanForecast() + xiadanForecast);
						totalDto.setXiadanDay31Sales(totalDto.getXiadanDay31Sales() + xiadanDay31Sales);
						totalDto.setPackQuantity(packQuantity);	//装箱数
						
					}
				}
			}
		}
		for (Map.Entry<String, Map<String, ProductInventoryTotalDto>>colorNameEntry : rs.entrySet()) {
			String colorName = colorNameEntry.getKey();
			Map<String, ProductInventoryTotalDto> productMap = rs.get(colorName);
			for (Map.Entry<String, ProductInventoryTotalDto>countryEntry : productMap.entrySet()) {
				String country = countryEntry.getKey();
				ProductInventoryTotalDto dto = productMap.get(country);
				ProductInventoryTotalDto totalDto = productMap.get("total");
				//预测下单量
				boolean flag = false;	//标记是否需要下单
				if (dto.getBalanceForecast() < 0) {
					flag = true;	//标记需要下单
				} 
				//如果当前周需要下单的产品没有达到下单点,计算出第四周的下单量,如果第四周达到下单条件也要下单
				else if(orderProducts.contains(colorName)){
					if (dto.getDaySaleForecast()*21 - dto.getBalanceForecast() > 0) {	//计算出第四周需要下单
						flag = true;	//标记需要下单
					}
				}
				if (dto.getBalanceDay31Sales() < 0 && !flag) {//31日销达到下单量,并且没有下单
					flag = true;	//标记需要下单
				} else if(!flag && orderProducts.contains(colorName)){	//前三步都没有下单并且在本周下单产品列表中,按31日销计算第四周是否需要下单
					if (dto.getDaySale31()*21 - dto.getBalanceDay31Sales() > 0) {	//计算出第四周需要下单
						flag = true;	//标记需要下单
					}
				}
				if (!flag && (dto.getProducting() > 0 || dto.getCn() > 0)) {	//不需要下单并且在产或中国仓有货才可调剂
					dto.setAdjust("1");//设置是否可以调剂(包含总数)
					if (!"total".equals(country)) {	//计算各国家可以调剂的数量
						//可调剂数选择两种方式中较小的一种
						double adjustNum = Math.abs(dto.getXiadanForecast() - dto.getBalanceForecast());
						double adjustNum1 = Math.abs(dto.getXiadanDay31Sales() - dto.getBalanceDay31Sales());
						if(orderProducts.contains(colorName)){
							adjustNum = Math.abs((dto.getXiadanForecast() + dto.getDaySaleForecast()*21) - dto.getBalanceForecast());
							adjustNum1 = Math.abs((dto.getXiadanDay31Sales() + dto.getDaySale31()*21) - dto.getBalanceDay31Sales());
						}
						if (adjustNum > adjustNum1) {
							adjustNum = adjustNum1;
						}
						if (adjustNum > (dto.getProducting() + dto.getCn())) {
							adjustNum = dto.getProducting() + dto.getCn();
						}
						dto.setAdjustNum(MathUtils.roundDown(adjustNum/dto.getPackQuantity())*dto.getPackQuantity());
						totalDto.setAdjustNum(totalDto.getAdjustNum() + dto.getAdjustNum());
					}
				}
			}
		}
    	return rs;
	}
	
	private int getOrderQuantity(PsiProduct psiProduct, double quantity,Integer packQuantity){
		return MathUtils.roundUp((quantity)/packQuantity)*packQuantity;
	}
	
	/**
	 * 最近两个月的促销数量
	 */
	public Map<String,String> getLast2MonthPromotionQ(Date createDate){
		String sql="SELECT b.`product_id`,b.`color_code`,b.`country_code`,DATE_FORMAT(a.`create_date`,'%Y-%u'),(CASE WHEN b.promotion_boss_quantity IS NOT NULL THEN b.promotion_boss_quantity ELSE b.`promotion_quantity` END ) AS Qty ,b.`remark`  " +
				" FROM psi_forecast_order AS  a ,psi_forecast_order_item AS b WHERE a.id=b.`forecast_order_id` AND b.`promotion_quantity`>0 AND b.promotion_boss_quantity!=0" +
				" AND DATE_ADD(:p1, INTERVAL -3 MONTH)<a.`create_date`  ORDER BY a.`create_date`";
		 Map<String,String>  resMap = Maps.newHashMap();
		 List<Object[]> list = this.forecastOrderDao.findBySql(sql,new Parameter(createDate));
		 for(Object[] obj:list){
			 String key = obj[0]+","+obj[1]+","+obj[2];
			 String value="";
			 String year =obj[3].toString().split("-")[0];
			 String week =obj[3].toString().split("-")[1];
			 if("2016".equals(year)){
				 value= year+"-"+(Integer.parseInt(week)+1)+"周,"+obj[5]+"["+obj[4]+"]<br/>";
			 }else{
				 value= obj[3]+"周,"+obj[5]+"["+obj[4]+"]<br/>";
			 }
			
			 if(resMap.get(key)!=null){
				 value+=resMap.get(key);
			 }
			 resMap.put(key, value);
		 }
		 return resMap;
	}
	
	//发送邮件给销售
	public boolean sendEmail(List<ForecastOrder> forecastOrders){
		StringBuffer contents= new StringBuffer("");
		contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;width:100%' cellpadding='0' cellspacing='0' >");
		contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
		contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:20%'>产品名</th>");
		contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:5%'>国家</th>");
		contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:5%'>下单周</th>");
		contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:8%'>预测下单量</th>");
		contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:8%'>运营下单量</th>");
		contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:8%'>最终下单量</th>");
		contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:15%'>销售原因</th>");
		contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:15%'>审核原因</th>");
		contents.append("</tr>");
		for (ForecastOrder forecastOrder : forecastOrders) {
			for(ForecastOrderItem item:forecastOrder.getItems()){
				Integer quantity=item.getQuantity();
				if(quantity.intValue()==0&&(item.getSaleQuantity()!=null&&item.getSaleQuantity().intValue()==0)){//如果最后数量为0但销售数量不为0的，不排除掉
					continue;
				}
				
				Integer forecastQ = 0;
				if("0".equals(item.getByWeek())){
					forecastQ=item.getForecast1week();
				}else if("1".equals(item.getByWeek())){
					forecastQ=item.getForecast2week();
				}else if("2".equals(item.getByWeek())){
					forecastQ=item.getForecast3week();
				}else if("3".equals(item.getByWeek())){
					forecastQ=item.getForecast4week();
				}
				
				Integer saleQuantity=0;
				if(item.getBossQuantity()!=null){
					if(item.getSaleQuantity()!=null){
						saleQuantity=item.getSaleQuantity();
					}else{
						if("0".equals(item.getByWeek())){
							saleQuantity=item.getForecast1week();
						}else if("1".equals(item.getByWeek())){
							saleQuantity=item.getForecast2week();
						}else if("2".equals(item.getByWeek())){
							saleQuantity=item.getForecast3week();
						}else if("3".equals(item.getByWeek())){
							saleQuantity=item.getForecast4week();
						}else{
							saleQuantity=0;
						}
					}
				}else{
					saleQuantity=(item.getSaleQuantity()!=null?item.getSaleQuantity():item.getQuantity());
				}
				
				//判断有变动没有，有变动就标红
				String color="#666";
				if(!saleQuantity.equals(quantity)){
					color="red";
				}
				
				contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:"+color+"; '>");
				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:"+color+";'>"+item.getProductNameColor()+"</td>");
				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:"+color+";'>"+(("com".equals(item.getCountryCode())?"us":item.getCountryCode()).toUpperCase())+"</td>");
				String week="";
				if("2".equals(item.getBy31sales())||"3".equals(item.getBy31sales())){
					if("3".equals(item.getByWeek())){
						week="WK"+DateUtils.getDate(forecastOrder.getCreateDate(), "w");
					}else{
						if(!"9".equals(item.getByWeek())){
							week="WK"+DateUtils.getDate(DateUtils.addDays(forecastOrder.getCreateDate(), 7*(Integer.parseInt(item.getByWeek())+1)),"w");
						}
					}
				}
				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:"+color+";'>"+week+"</td>");
				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:"+color+";'>"+forecastQ+"</td>");
				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:"+color+";'>"+saleQuantity+"</td>");
				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:"+color+";'>"+item.getQuantity()+"</td>");
				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:"+color+";'>"+(StringUtils.isEmpty(item.getRemark())?"":item.getRemark())+"</td>");
				contents.append("<td style='border-left:1px solid;border-top:1px solid;color:"+color+";'>"+(StringUtils.isNotEmpty(item.getBossRemark())?item.getBossRemark():(StringUtils.isNotEmpty(item.getReviewRemark())?item.getReviewRemark():""))+"</td>");
				contents.append("</tr>"); 
			}
		}
		contents.append("</table><br/>");
		 
		String toAddress ="tim@inateck.com,amazon-sales@inateck.com,supply-chain@inateck.com";
		Date date = new Date();   
		final MailInfo mailInfo = new MailInfo(toAddress,"预测采购订单已审核"+DateUtils.getDate("-yyyy/M/dd")+"第"+DateUtils.getDate(forecastOrders.get(0).getCreateDate(), "w")+"周",date);
		mailInfo.setContent(contents.toString());
		System.out.println(contents.toString());
		new Thread(){
			public void run(){
				 mailManager.send(mailInfo);
			}
		}.start();
		
		return true;
	}
	
	
	public ForecastOrderItem getItemInfo(Integer itemId){
		return this.forecastOrderItemDao.get(itemId);
	}
	
	/**
	 * 根据截至时间和预测数据计算该期间内的预测日均销
	 * @param date	截至时间
	 * @param forecastDatas	预测销量
	 * @return
	 */
	public Map<String, Double> getAvg(Date date, Map<String, Map<String, Float>> forecastDatas) {
		Map<String, Double> rs = Maps.newHashMap();
		if (date == null) {
			return rs;
		}
		//计算目标日期距离当前时间的天数
		Date now = new Date();
		int days = (int)((date.getTime() - now.getTime())/86400000);
		for (Map.Entry<String, Map<String, Float>> nameAndCountryEntry : forecastDatas.entrySet()) {
			String nameAndCountry = nameAndCountryEntry.getKey();
			Map<String, Float> forecastData = forecastDatas.get(nameAndCountry);
			Double forecastPreiodAvg = 0d;
			for (int k = 1; k <= days; k++) {
				//按月
				Float temp = forecastData.get(DateUtils.getDate(DateUtils.getLastDayOfMonth(DateUtils.addDays(now, k)),"yyyyMMdd"));
				int ii = k-30;
				while(temp==null){
					Date tempDate = DateUtils.addDays(now, ii);
					if(tempDate.before(DateUtils.getFirstDayOfMonth(now))){
						break;
					}
					temp = forecastData.get(DateUtils.getDate(DateUtils.getLastDayOfMonth(tempDate),"yyyyMMdd"));
					ii=ii-30;
				}
				temp = (temp==null?0f:temp);
				forecastPreiodAvg +=temp;
			}
			forecastPreiodAvg = forecastPreiodAvg/days;
			BigDecimal tempd = new BigDecimal(forecastPreiodAvg);
			tempd = tempd.setScale(2, BigDecimal.ROUND_HALF_UP);
			forecastPreiodAvg = tempd.doubleValue();
			rs.put(nameAndCountry, forecastPreiodAvg);
		}
		return rs;
	}
	
	/**
	 *获取有价格差的产品 
	 */
	public Map<String,String> getDiffPriceMap(){
		DecimalFormat df = new DecimalFormat("0.##");
		Map<String,String>  rs = Maps.newHashMap();
		String sql="SELECT (CASE WHEN a.color !='' THEN CONCAT(a.product_id,'_',a.color) ELSE a.product_id END) as proIdColor ," +
				" GROUP_CONCAT(a.`price` ORDER BY a.`price` DESC) FROM psi_product_tiered_price AS a  " +
				"WHERE a.`del_flag`='0' AND a.`currency_type`='CNY' AND a.`price` IS NOT NULL " +
				"GROUP BY a.`product_id`,a.`color`,a.`supplier_id` HAVING MIN(a.`price`)!=MAX(a.`price`)";
		List<Object[]> list = this.forecastOrderDao.findBySql(sql);
		String[] levalArr={"500","1000","2000","3000","5000","8000","10000","15000"};
		for(Object[] obj:list){
			String productIdColor = obj[0].toString();
			String priceCon = obj[1].toString();
			String priceArr[] =priceCon.split(",");
			Float  firstPrice = 0f; 
			Set<String> same= Sets.newHashSet();
			StringBuilder priceAlert =new StringBuilder();
			for(int i=0;i<priceArr.length;i++){   
				Float tempPrice=Float.parseFloat(priceArr[i]);   
				if(i==0){
					firstPrice=Float.parseFloat(priceArr[i]);
				}else{
					String reduce = df.format(firstPrice-tempPrice);
					if((firstPrice-tempPrice)>0&&!same.contains(reduce)){ 
						priceAlert.append(levalArr[i]).append("档便宜").append(reduce).append("元<br/>");
						same.add(reduce);
					}
				}
			}
			rs.put(productIdColor,priceAlert.toString());
		}
		
		return rs;
	}
	
	/**
	 *获取昨天的库销比 
	 */
	public Map<String,Float> getSaleMonth(){
		Map<String,Float> rs = Maps.newHashMap();
		String sql1 ="SELECT MAX(a.`data_date`) FROM psi_product_in_stock AS a ";
		List<Date> dates = this.forecastOrderDao.findBySql(sql1);
		if(dates!=null&&dates.size()>0){
			String sql="SELECT a.`product_name`,ROUND(SUM(a.`total_stock`)/SUM(a.`day31_sales`),2) FROM psi_product_in_stock AS a  WHERE a.`country`='total' AND a.`data_date`=:p1 GROUP BY a.`product_name` ";
			List<Object[]> objs=this.forecastOrderDao.findBySql(sql,new Parameter(dates.get(0)));
			if(objs!=null&&objs.size()>0){
				for(Object[] obj:objs){
					if(obj[1]!=null){
						rs.put(obj[0].toString(), Float.parseFloat(obj[1].toString()));
					}
				}
			}
		}
		return rs;
	}
	
	/**
	 *获取昨天库存
	 *产品名：总数,31日销 
	 */
	public Map<String,String> getStockTotal(String productName){
		Map<String,String> rs = Maps.newHashMap();
		String sql1 ="SELECT MAX(a.`data_date`) FROM psi_product_in_stock AS a ";
		List<Date> dates = this.forecastOrderDao.findBySql(sql1);
		if(dates!=null&&dates.size()>0){
			List<Object[]> objs=null;
			String sql=" SELECT a.`product_name`,a.`total_stock`,a.`day31_sales` FROM psi_product_in_stock AS a  WHERE a.`country`='total' AND a.`data_date`=:p1 ";
			if(StringUtils.isNotEmpty(productName)){
				sql+=" AND a.`product_name`=:p2";
				objs=this.forecastOrderDao.findBySql(sql,new Parameter(dates.get(0),productName));
			}else{
				objs=this.forecastOrderDao.findBySql(sql,new Parameter(dates.get(0)));
			}
			
			if(objs!=null&&objs.size()>0){
				for(Object[] obj:objs){
					if(obj[1]!=null){
						rs.put(obj[0].toString(), (obj[1].toString()+","+(obj[2]==null?0:obj[2].toString())));
					}
				}
			}
		}
		return rs;
	}

	
	/**
	 * 最近一个月价格浮动超过20%产品,折扣对价格影响太复杂,暂不考虑
	 * @return Map<colorName, remark>
	 */
	public Map<String, String> findPriceChangeMap() {
		String sql = "SELECT c.sellersku,c.sales_channel,c.pricestr,d.hisprice FROM (SELECT  b.sellersku,a.`sales_channel`, "+
				" SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT(( "+
				//" b.`item_price` - IFNULL(b.`promotion_discount`,0) "+
				" b.`item_price` "+
				" ) / b.`quantity_shipped`)ORDER BY a.`purchase_date` DESC),',',1) AS pricestr "+
				" FROM "+
				" amazoninfo_order a, "+
				" amazoninfo_orderitem b  "+
				" WHERE a.`order_status` = 'shipped'  "+
				" AND a.`id` = b.`order_id`  "+
				" AND a.`purchase_date` > :p1  "+
				" AND b.`quantity_shipped` >0 "+
				" AND b.`sellersku` NOT LIKE '%old%' AND b.`sellersku` NOT LIKE '%local%' "+
				" GROUP BY  b.sellersku ,a.`sales_channel`)c,(SELECT sellersku,`sales_channel`,SUBSTRING_INDEX(GROUP_CONCAT(aa.price ORDER BY aa.qua DESC),',',1)AS hisprice FROM (SELECT  b.sellersku,a.`sales_channel`, "+
				//" (( b.`item_price` - IFNULL(b.`promotion_discount`,0)) / b.`quantity_shipped`) AS  price , "+
				" (b.`item_price` / b.`quantity_shipped`) AS  price , "+
				" SUM(b.`quantity_ordered`) AS  qua "+
				" FROM amazoninfo_order a, amazoninfo_orderitem b  "+
				" WHERE a.`order_status` = 'shipped'  "+
				" AND a.`id` = b.`order_id` "+
				" AND a.`purchase_date` > :p1  "+
				" AND b.`quantity_shipped` >0 "+
				" AND b.`sellersku` NOT LIKE '%old%' AND b.`sellersku` NOT LIKE '%local%' "+
				" GROUP BY  b.sellersku,a.`sales_channel`,( "+
				//" b.`item_price` - b.`promotion_discount` "+
				" b.`item_price` "+
				" ) / b.`quantity_shipped`) aa  GROUP BY sellersku,`sales_channel` ) d WHERE c.sellersku = d.sellersku AND c.sales_channel = d.sales_channel  AND ABS((c.pricestr- d.hisprice)/d.hisprice)>0.2 ";
		List<Object[]> list = forecastOrderDao.findBySql(sql, new Parameter(DateUtils.addMonths(new Date(), -1)));
		Map<String, String> skuNameMap = saleProfitService.findSkuNames();
		Map<String, String> rs = Maps.newHashMap();
		for (Object[] obj : list) {
			String sku = obj[0].toString();
			String productName = skuNameMap.get(sku);
			if (StringUtils.isEmpty(productName)) {
				continue;
			}
			String country = obj[1].toString();
			country = country.substring(country.lastIndexOf(".")+1);
			Float currPrice = Float.parseFloat(obj[2].toString());
			Float hisPrice = Float.parseFloat(obj[3].toString());
			double percent = (currPrice - hisPrice)*100/hisPrice.doubleValue();
			String color = "red";
			if (currPrice < hisPrice) {
				color = "green";
			}
			String remark = "<font style='font-size:16px;color:"+color+"'>" +String.format("%.2f", percent)+"%</font>";
			remark = "当前价：" + currPrice +",历史价：" + hisPrice + "<br/>价格浮动" + remark;
			rs.put(productName+"_"+country, remark);
		}
		return rs;
	}
	
	public List<ForecastOrder> findForSplit(ForecastOrder forecastOrder) {
		DetachedCriteria dc = forecastOrderDao.createDetachedCriteria();
		if (forecastOrder.getCreateDate()!=null){
			dc.add(Restrictions.eq("createDate", forecastOrder.getCreateDate()));
		}
		dc.add(Restrictions.eq("orderSta","5"));
		return forecastOrderDao.find(dc);
	}
	
	public static void main(String[] args) {
		Date d1 = new Date();
		Date d2 = new Date(116,6,28,23,59,59);
		int days = (int)((d2.getTime() - d1.getTime())/86400000);
		System.out.println(days);
//		System.out.println(DateUtils.getWeekOfYear(d2));
//		System.out.println(DateUtils.getDate(d2, "w"));
//		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
//		PsiInventoryService  psiInventoryService= applicationContext.getBean(PsiInventoryService.class);
//		Map<String, Map<String, Float>> forecastDatas = psiInventoryService.getForecastByMonthSalesData();
//		new ForecastOrderService().getAvg(d2, forecastDatas);
//		applicationContext.close();
	}
	
	
}
