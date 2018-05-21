/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
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
import com.springrain.erp.modules.psi.dao.HisPsiMarketingPlanDao;
import com.springrain.erp.modules.psi.dao.HisPsiMarketingPlanItemDao;
import com.springrain.erp.modules.psi.dao.PsiMarketingPlanDao;
import com.springrain.erp.modules.psi.dao.PsiMarketingPlanItemDao;
import com.springrain.erp.modules.psi.entity.HisPsiMarketingPlan;
import com.springrain.erp.modules.psi.entity.HisPsiMarketingPlanItem;
import com.springrain.erp.modules.psi.entity.ProductSalesInfo;
import com.springrain.erp.modules.psi.entity.PsiInventoryFba;
import com.springrain.erp.modules.psi.entity.PsiMarketingPlan;
import com.springrain.erp.modules.psi.entity.PsiMarketingPlanItem;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;


/**
 * 营销计划Service
 * @author Michael
 * @version 2017-06-12
 */
@Component
@Transactional(readOnly = true)
public class PsiMarketingPlanService extends BaseService {
	private final static Logger logger = LoggerFactory.getLogger(PsiMarketingPlanService.class);

	@Autowired
	private 	PsiMarketingPlanDao 		psiMarketingPlanDao;
	@Autowired
	private 	HisPsiMarketingPlanDao 		hisPsiMarketingPlanDao;
	@Autowired
	private 	PsiMarketingPlanItemDao     psiMarketingPlanItemDao;
	@Autowired
	private 	HisPsiMarketingPlanItemDao 	hisPsiMarketingPlanItemDao;
	@Autowired
	private 	MailManager					mailManager;
	@Autowired
	private 	PsiProductTypeGroupDictService  groupDictService;
	@Autowired
	private 	SystemService               systemService;
	@Autowired
	private PsiInventoryFbaService psiInventoryFbaService;
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
	@Autowired
	private PsiInventoryService psiInventoryService ;
	
	public PsiMarketingPlan get(Integer id) {
		return psiMarketingPlanDao.get(id);
	}
	
	public Page<PsiMarketingPlan> find(Page<PsiMarketingPlan> page, PsiMarketingPlan psiMarketingPlan,String isCheck,String nameColor,String lineId) {
		DetachedCriteria dc = psiMarketingPlanDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(nameColor)||StringUtils.isNotEmpty(lineId)){
			dc.createAlias("this.items", "item");
			if(StringUtils.isNotEmpty(nameColor)){
				String arr[] = nameColor.split("_");
				String color="";
				String proName = arr[0];
				if(arr.length>1){
					color=arr[1];
				}
				dc.add(Restrictions.eq("item.productName",proName));
				dc.add(Restrictions.eq("item.colorCode",color));
			}
			
			if(StringUtils.isNotEmpty(lineId)){
				 Map<String,Set<Integer>> lineMap =groupDictService.getLineProductIds();
				 Set<Integer> proIds = lineMap.get(lineId);
				 if(proIds!=null&&proIds.size()>0){
					 dc.add(Restrictions.in("item.product.id",proIds));
				 }
			}
		}
		
		
		if(StringUtils.isNotEmpty(psiMarketingPlan.getCountryCode())){
			dc.add(Restrictions.eq("countryCode", psiMarketingPlan.getCountryCode()));
		}
		
		if(StringUtils.isNotEmpty(psiMarketingPlan.getType())){
			dc.add(Restrictions.eq("type", psiMarketingPlan.getType()));
		}
		
		if(StringUtils.isNotEmpty(psiMarketingPlan.getSta())){
			dc.add(Restrictions.eq("sta", psiMarketingPlan.getSta()));
		} else {
			dc.add(Restrictions.ne("sta", "8"));	//取消状态默认不显示
		}
		
		if(psiMarketingPlan.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",psiMarketingPlan.getCreateDate()));
		}
		
		if(psiMarketingPlan.getUpdateDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(psiMarketingPlan.getUpdateDate(),1)));
		}
		if("1".equals(isCheck)){
			dc.add(Restrictions.or(Restrictions.eq("createUser.id",UserUtils.getUser().getId()),Restrictions.eq("reviewUser.id",UserUtils.getUser().getId())));
		}
		dc.addOrder(Order.desc("id"));
		return psiMarketingPlanDao.find(page, dc);
	}
	
	
	public List<HisPsiMarketingPlan> findHis(Integer planId) {
		DetachedCriteria dc = hisPsiMarketingPlanDao.createDetachedCriteria();
		dc.add(Restrictions.eq("marketingPlanId", planId));
		return hisPsiMarketingPlanDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void editSave(PsiMarketingPlan psiMarketingPlan) {
		Map<String,Integer> oldMap = Maps.newHashMap();
		if(psiMarketingPlan.getId()!=null){
			PsiMarketingPlan  oldPlan = this.psiMarketingPlanDao.get(psiMarketingPlan.getId());
			for(PsiMarketingPlanItem item:oldPlan.getItems()){
				if("0".equals(item.getDelFlag())){
					oldMap.put(item.getNameWithColor(), item.getPromoQuantity());
				}
			}
		}
		
		
		Set<Integer>  delItemSet = Sets.newHashSet();
		Set<String> setNewIds = new HashSet<String>();
		boolean flag =false;
		for(PsiMarketingPlanItem item:psiMarketingPlan.getItems()){
			if(!flag&&psiMarketingPlan.getId()!=null){
				Integer oldQ =oldMap.get(item.getNameWithColor());
				if(oldQ==null||oldMap.get(item.getNameWithColor())==null||(item.getPromoQuantity().intValue()==oldQ.intValue())){
					flag=true;
				}
			}
			if(item.getId()!=null){
				setNewIds.add(item.getId().toString());
			}
			item.setMarketingPlan(psiMarketingPlan);
		}
		
		//促销开始周和结束周一样
		if("0".equals(psiMarketingPlan.getType())){
			psiMarketingPlan.setEndWeek(psiMarketingPlan.getStartWeek());
		}
		
		if(psiMarketingPlan.getId()==null){
			if(StringUtils.isEmpty(psiMarketingPlan.getSta())){
				psiMarketingPlan.setSta("0");
			}
			
			psiMarketingPlan.setCreateDate(new Date());
			psiMarketingPlan.setCreateUser(UserUtils.getUser());
			psiMarketingPlanDao.save(psiMarketingPlan);
		}else{
			psiMarketingPlan.setUpdateDate(new Date());
			psiMarketingPlan.setUpdateUser(UserUtils.getUser());
			//对编辑的产品进行处理
			String oldItemIds=psiMarketingPlan.getOldItemIds();
			String [] oldIds = oldItemIds.split(",");
			if(setNewIds!=null&&setNewIds.size()>0){
				for(int j=0;j<oldIds.length;j++){
					if(!setNewIds.contains(oldIds[j])){
						//不包含就干掉
						delItemSet.add(Integer.valueOf(oldIds[j]));
					};
				}
			}else{
				//说明原来的都删除了
				for(int j=0;j<oldIds.length;j++){
					delItemSet.add(Integer.valueOf(oldIds[j]));
				}
			}
			
			if(delItemSet.size()>0){
				for(PsiMarketingPlanItem item:this.getItemsByIds(delItemSet)){
					item.setDelFlag("1");
					item.setMarketingPlan(psiMarketingPlan);
					psiMarketingPlan.getItems().add(item);
				};
			}
			
			//如果为已审核状态编辑，       就变为申请状态   重新审核
			if("3".equals(psiMarketingPlan.getSta())){
				psiMarketingPlan.setSta("1");
			}
			psiMarketingPlanDao.getSession().merge(psiMarketingPlan);
		}
		
		
		
		//如果为申请审核状态就发信
		if("1".equals(psiMarketingPlan.getSta())){
			List<User> users=systemService.findUserByPermission("psi:psiMarketingPlan:review");
			StringBuilder emailAddress= new StringBuilder();
			for(User user:users){
				emailAddress.append(user.getEmail()).append(",");
			}
			if(emailAddress.length()>0){
				emailAddress=new StringBuilder(emailAddress.substring(0, emailAddress.length()-1));
			}else{
				emailAddress.append(UserUtils.getUser().getEmail());
			}
			String content = ("0".equals(psiMarketingPlan.getSta())?"促销":"广告")+"计划已建立,【<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/psiMarketingPlan/review?id="+psiMarketingPlan.getId()+"'>点击审核</a>】";
			this.sendEmail(content, ("0".equals(psiMarketingPlan.getSta())?"促销":"广告")+"计划申请审核", emailAddress.toString(), UserUtils.getUser().getEmail());
		}
		
		//审核后修改，保存版本快照
		if(!"0".equals(psiMarketingPlan.getSta())){
			 //保存版本快照
			HisPsiMarketingPlan his= new HisPsiMarketingPlan(psiMarketingPlan.getCountryCode(),
					psiMarketingPlan.getStartWeek(),psiMarketingPlan.getEndWeek(),psiMarketingPlan.getRemark(), 
					psiMarketingPlan.getSta(), psiMarketingPlan.getType(), new Date(),UserUtils.getUser(),psiMarketingPlan.getId());
			
			List<HisPsiMarketingPlanItem>  items = Lists.newArrayList();
			for(PsiMarketingPlanItem item :psiMarketingPlan.getItems()){
				items.add(new HisPsiMarketingPlanItem(item.getProductName(), item.getProduct(), item.getColorCode(), item.getPromoQuantity(),"",his));
			}
			his.setItems(items);
			this.hisPsiMarketingPlanDao.save(his);
		}
	}
	
	@Transactional(readOnly = false)
	public void reviewSave(PsiMarketingPlan psiMarketingPlan) {
		psiMarketingPlan = this.get(psiMarketingPlan.getId());
		psiMarketingPlan.setSta("3");//已审核
		psiMarketingPlan.setReviewDate(new Date());
		psiMarketingPlan.setReviewUser(UserUtils.getUser());
		psiMarketingPlanDao.save(psiMarketingPlan);
		String content = ("0".equals(psiMarketingPlan.getSta())?"促销":"广告")+"已审批,【<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/psiMarketingPlan/view?id="+psiMarketingPlan.getId()+"'>点击查看</a>】";
		this.sendEmail(content, ("0".equals(psiMarketingPlan.getSta())?"促销":"广告")+"计划已审批", psiMarketingPlan.getCreateUser().getEmail(), UserUtils.getUser().getEmail());
	 //保存版本快照
		HisPsiMarketingPlan his= new HisPsiMarketingPlan(psiMarketingPlan.getCountryCode(),
				psiMarketingPlan.getStartWeek(),psiMarketingPlan.getEndWeek(),psiMarketingPlan.getRemark(), 
				psiMarketingPlan.getSta(), psiMarketingPlan.getType(), new Date(),UserUtils.getUser(),psiMarketingPlan.getId());
		
		List<HisPsiMarketingPlanItem>  items = Lists.newArrayList();
		for(PsiMarketingPlanItem item :psiMarketingPlan.getItems()){
			items.add(new HisPsiMarketingPlanItem(item.getProductName(), item.getProduct(), item.getColorCode(), item.getPromoQuantity(),"",his));
		}
		his.setItems(items);
		this.hisPsiMarketingPlanDao.save(his);
	}
	
	@Transactional(readOnly = false)
	public void cancel(PsiMarketingPlan psiMarketingPlan) {
		psiMarketingPlan= this.get(psiMarketingPlan.getId());
		psiMarketingPlan.setCancelDate(new Date());
		psiMarketingPlan.setCancelUser(UserUtils.getUser());
		psiMarketingPlan.setSta("8");//取消
		psiMarketingPlanDao.save(psiMarketingPlan);
	}
	
 
	@Transactional(readOnly = false)
	public void save(PsiMarketingPlan psiMarketingPlan) {
		psiMarketingPlanDao.save(psiMarketingPlan);
	}
	
	public List<String> getAllProductColors(){
		String sql="SELECT DISTINCT CONCAT(a.`product_name`,CASE  WHEN a.`product_color`='' THEN '' ELSE CONCAT('_',a.`product_color`) END) AS productName FROM psi_barcode a WHERE a.`del_flag`='0'";
		List<String> objs=this.psiMarketingPlanDao.findBySql(sql);
		return objs;
	}


	
	public void sendEmail(String content,String title,String sendEmail,String ccEmail){
		Date date = new Date();
		final MailInfo mailInfo1 = new MailInfo(sendEmail,title,date);
		mailInfo1.setContent(content);
		mailInfo1.setCcToAddress(ccEmail);
		//发送成功不成功都能保存
		new Thread(){
			@Override
			public void run(){
				mailManager.send(mailInfo1);
			}
		}.start();
	}
	
	
	/***
	 * 
	 *根据方差表查出31销
	 * 
	 */
	public Integer getSales31Days(String productName ,String country){
		String sql="SELECT a.`day31_sales` FROM psi_product_variance AS a WHERE a.`product_name`=:p1 AND a.`country`=:p2";
		List<Integer> objs=this.psiMarketingPlanDao.findBySql(sql,new Parameter(productName,country));
		if(objs!=null&&objs.size()>0){
			return objs.get(0);
		}else{
			return 0;
		}
	}
	
   /**
	 * 找出需要更新销售数的产品
	 *已审核
	 *当前日期大于开始周
	 *结束周为空或者大于上周
	 *DateUtils.addDays(new Date(),-37) 
	 */
	public List<PsiMarketingPlan> findLastWeek() {
		String week=DateUtils.getWeekStr(new Date(),new SimpleDateFormat("yyyyww"), 4, "");
		String weekNext=DateUtils.getWeekStr(DateUtils.addDays(new Date(),-37),new SimpleDateFormat("yyyyww"), 4, "");
		DetachedCriteria dc = psiMarketingPlanDao.createDetachedCriteria();
		Restrictions.le("startWeek", week);
		dc.add(Restrictions.sqlRestriction(" (end_week='' or end_week is NULL or end_week >='"+weekNext+"' ) "));
		dc.add(Restrictions.eq("sta", "3"));
		return this.psiMarketingPlanDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void updateRealQuantity(){
		List<PsiMarketingPlan> list = this.findLastWeek();
		Date today = new Date();
		if(list!=null&&list.size()>0){
			for(PsiMarketingPlan plan :list){
				String startWeek = plan.getStartWeek();
				String endWeek = plan.getEndWeek();
				Date startDay = DateUtils.getFirstDayOfWeek(Integer.parseInt(startWeek.substring(0, 4)), Integer.parseInt(startWeek.substring(4, 6)));
				Date endDay = null;
				if(StringUtils.isNotEmpty(endWeek)){
					endDay = DateUtils.getLastDayOfWeek(Integer.parseInt(endWeek.substring(0, 4)), Integer.parseInt(endWeek.substring(4, 6)));
				}else{
					endDay = today;
				}
				String country = plan.getCountryCode();
				String type = plan.getType();
				for(PsiMarketingPlanItem item :plan.getItems()){
					String productName = item.getProductName();
					String color = item.getColorCode();
					Integer realQuantity=this.getPromoSales(productName, color, country, startDay, endDay, type);
					if(realQuantity!=null&&realQuantity.intValue()>0){
						//更新促销是总数，
						if("0".equals(type)){
							this.updateRealPromo(item.getId(), realQuantity);
						}else if ("1".equals(type)){
						//广告球平均数
						Integer avg=(int)(realQuantity*1f/DateUtils.spaceDays(startDay, endDay));
						this.updateRealPromo(item.getId(), avg);
						}
					}
				}
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void updateRealPromo(Integer id,Integer realPromoQuantity){
		String  sql = "UPDATE psi_marketing_plan_item AS a SET a.`real_quantity`=:p2 WHERE a.`id`=:p1";
		this.psiMarketingPlanDao.updateBySql(sql, new Parameter(id,realPromoQuantity));
	}
	
	/***
	 * 
	 *统计销量和广告
	 * 
	 */
	public Integer getPromoSales(String productName,String color,String country,Date startDay,Date endDay,String type){
		String sql="SELECT SUM(a.`flash_sales_order`+a.`outside_order`),SUM(a.`ads_order`) FROM amazoninfo_sale_report AS a WHERE" +
				" a.`product_name`=:p1 AND a.`color`=:p2 AND a.`country`=:p3 AND a.`date`>=:p4 AND a.`date`<=:p5 ";
		List<Object[]> objs=this.psiMarketingPlanDao.findBySql(sql,new Parameter(productName,color,country,startDay,endDay));
		if(objs!=null&&objs.size()>0){
			if("0".equals(type)){
				return (objs.get(0)[0]!=null?Integer.parseInt(objs.get(0)[0].toString()):0);
			}else{
				return (objs.get(0)[1]!=null?Integer.parseInt(objs.get(0)[1].toString()):0);
			}
		}else{
			return 0;
		}
	}
	
	
	
	/***
	 * 
	 *是否两个计划日期向抵触
	 * 
	 */
	public String isExist(String productName ,String color,String country,Integer id,String type,String startWeek){
		List<BigInteger> obj=null;
		if("0".equals(type)){
			String sql="SELECT COUNT(*) FROM psi_marketing_plan AS  a,psi_marketing_plan_item AS  b WHERE a.`id`=b.`marketing_plan_id` AND b.`del_flag`='0' AND " +
					" a.id<>:p1 AND b.`product_name`=:p2 AND a.`country_code`=:p3 AND b.`color_code`=:p4  AND a.type ='0' AND a.`sta`<>'8'  AND  a.`start_week` =:p5 ";
			obj=this.psiMarketingPlanDao.findBySql(sql,new Parameter(id,productName,country,color,startWeek));
		}else if("1".equals(type)){//广告，无结束期限
			String sql="SELECT COUNT(*) FROM psi_marketing_plan AS  a,psi_marketing_plan_item AS  b WHERE a.id = b.`marketing_plan_id` AND b.`del_flag`='0' AND" +
					" a.id<>:p1 AND b.`product_name`=:p2 AND a.`country_code`=:p3 AND b.`color_code`=:p4  AND a.type ='1' AND a.`sta`<>'8'   ";
			obj=this.psiMarketingPlanDao.findBySql(sql,new Parameter(id,productName,country,color));
		}
		if(obj.get(0).intValue()>0){
			return "true";
		}else{
			return "false";
		}
	}
	
	/***
	 * 
	 *查出分国家库存情况
	 * 
	 */
	public Map<String,Integer> getInventoryInfo(String productName,String color){
		Map<String,Integer>  rs = Maps.newHashMap();
		String sql="SELECT a.`country_code`,SUM(a.`new_quantity`) FROM psi_inventory AS a WHERE a.`warehouse_id`='130' " +
				" AND a.`product_name`=:p1 AND a.`color_code`=:p2 GROUP BY a.`product_name`,a.`color_code`,a.`country_code`";
		List<Object[]> objs=this.psiMarketingPlanDao.findBySql(sql,new Parameter(productName,color));
		if(objs!=null&&objs.size()>0){
			for(Object[] obj:objs){
				String countryCode = obj[0].toString();
				if(obj[1]!=null&&Integer.parseInt(obj[1].toString())>0){
					rs.put(countryCode, Integer.parseInt(obj[1].toString()));
				}
			}
		}
		return rs;
	}
	
	
	public List<PsiMarketingPlanItem>  getItemsByIds(Set<Integer> ids){
		DetachedCriteria dc = this.psiMarketingPlanItemDao.createDetachedCriteria();
		dc.add(Restrictions.in("id", ids));
		return this.psiMarketingPlanItemDao.find(dc);
	}

	/**
	 * 查询当前周之后计划的促销和广告数量
	 * @param week 周数(ps:当前周下单产品需要预算四周)
	 * @param orderProducts 当前下单周内的产品(下四周缺口)
	 * @param targetDate 备货时间
	 * @param fancha 
	 * @param productBufferPeriod 
	 * @return Map<productName, Map<country, Map<type, 数量>>> type 0:促销  1：广告
	 * @throws ParseException 
	 */
	public Map<String, Map<String, Map<String, Integer>>> findPlanInventory(Map<String, Integer> purchaseWeekMap, Date targetDate, 
			Map<String, Map<String, Integer>> productBufferPeriod, Map<String, ProductSalesInfo> fancha, Map<String, String> fanOuMap) throws ParseException {
		Map<String, Map<String, Map<String, Integer>>> rsMap = Maps.newHashMap();
		DateFormat formatWeek = new SimpleDateFormat("yyyyww");
		String week = DateUtils.getWeekStr(new Date(),formatWeek, 4, "");
		Date weekDate = formatWeek.parse(week);
		//第四周周数,用于比较当前周下单产品
		Date lastWeekDate = DateUtils.addDays(weekDate, 21);
		boolean delay = false;
		if (targetDate != null && targetDate.after(DateUtils.addMonths(new Date(), 1))) {
			lastWeekDate = targetDate;
			delay = true;
		}
		Integer lastWeek = Integer.parseInt(DateUtils.getWeekStr(lastWeekDate,formatWeek, 4, ""));
		String sql = "SELECT i.`product_name`,i.`color_code`,t.`country_code`,i.`promo_quantity`,"+
				" t.`start_week`,t.`end_week`,t.`type`"+
				" FROM `psi_marketing_plan` t ,`psi_marketing_plan_item` i"+
				" WHERE t.`id`=i.`marketing_plan_id` AND t.`sta`='3' AND (t.`end_week`>=:p1 or t.`end_week`='' OR t.`end_week` IS NULL)";
		List<Object[]> list = psiMarketingPlanDao.findBySql(sql, new Parameter(week));
		for (Object[] obj : list) {
			if (obj[0] == null) {
				continue;
			}
			String productName = obj[0].toString();
			String color = obj[1] == null ? "" : obj[1].toString();
			if (StringUtils.isNotEmpty(color)) {
				productName = productName + "_" + color;
			}
			String country = obj[2].toString();
			int period = 0;	//缓冲周期
			if (productBufferPeriod.get(productName)!=null && productBufferPeriod.get(productName).get(country) != null) {
				period = productBufferPeriod.get(productName).get(country);	//缓冲周期
			}
			if ("jp".equals(country) && period > 0) {
				period += 7;	//日本国家延长7天
			}
			String fachaKey = productName + "_" + country;
			String fanOu = fanOuMap.get(productName);
			if ("0".equals(fanOu) && "de".equals(country)) {
				fachaKey = productName + "_eu";
			}
			//fanOu为1时后面需要单独处理UK数据
			if ("1".equals(fanOu) && "de".equals(country)) {
				fachaKey = productName + "_eunouk";
			}
			if (fancha.get(fachaKey) !=null && fancha.get(fachaKey).getPeriod() > 0) {
				period += fancha.get(fachaKey).getPeriod();
			}
			Integer promoQuantity = obj[3]==null?0:Integer.parseInt(obj[3].toString());
			Integer startWeek = obj[4]==null?0:Integer.parseInt(obj[4].toString());
			//结束周为空表示不限期
			String endWeekStr = obj[5]==null?"":obj[5].toString();
			Integer endWeek = 0;
			if (StringUtils.isNotEmpty(endWeekStr)) {
				endWeek = Integer.parseInt(endWeekStr);
			}
			String type = obj[6].toString();
			Map<String, Map<String, Integer>> productMap = rsMap.get(productName);
			if (productMap == null) {
				productMap = Maps.newHashMap();
				rsMap.put(productName, productMap);
			}
			Map<String, Integer> countryMap = productMap.get(country);
			if (countryMap == null) {
				countryMap = Maps.newHashMap();
				productMap.put(country, countryMap);
			}
			Integer quantity = 0;
			if (countryMap.get(type) != null) {
				quantity = countryMap.get(type);
			}
			/*
			 * 1、下单覆盖周期在开始时间之后并且在结束时间之前
			 * 2、下单覆盖周期在结束时间之后,但是当前周在结束周之前
			 */
			//计算预留的库存,即需要在下单时要从总库存中剔除的部分
			int currWeek = Integer.parseInt(week);	//当前周
			int coverWeek = currWeek;	//下单覆盖周
			if (delay) {
				coverWeek = lastWeek;	//备货模式,覆盖到lastWeek
			} else if (purchaseWeekMap.get(productName) != null) {//下单周产品按下单时间覆盖
				if (purchaseWeekMap.get(productName) == 0) {
					coverWeek = lastWeek;	//当前下单周,覆盖到lastWeek
				} else if (purchaseWeekMap.get(productName) == 2) {
					Date coverWeekDate = DateUtils.addDays(weekDate, 7);
					coverWeek = Integer.parseInt(DateUtils.getWeekStr(coverWeekDate, formatWeek, 4, ""));
				} else if (purchaseWeekMap.get(productName) == 3) {
					Date coverWeekDate = DateUtils.addDays(weekDate, 14);
					coverWeek = Integer.parseInt(DateUtils.getWeekStr(coverWeekDate, formatWeek, 4, ""));
				}
				//purchaseWeekMap.get(productName) == 1下周下单跟默认值currWeek一致
				//算上缓冲周期和采购运输周期等
				Date coverDate = formatWeek.parse(coverWeek+"");
				coverDate = DateUtils.addDays(coverDate, period);
				coverWeek = Integer.parseInt(DateUtils.getWeekStr(coverDate, formatWeek, 4, ""));
			}
			
			//计算下单覆盖周促销&广告数量
			if (coverWeek >= startWeek && (coverWeek<=endWeek || endWeek == 0)) {	//生效了未开始,算所有数量
				if ("0".equals(type)) {	//促销,不夸周算全部
					quantity += promoQuantity;
				} else {	//广告,为日均销
					int totalWeek = week(currWeek, coverWeek) + 1;
					if (currWeek < startWeek) {
						totalWeek = week(startWeek, coverWeek) + 1;
					}
					quantity += totalWeek * promoQuantity * 7;	//广告为日均均销量
				}
			} else if (currWeek <= endWeek && (coverWeek>=endWeek && endWeek > 0)) {
				if ("0".equals(type)) {	//促销,不夸周算全部
					quantity += promoQuantity;
				} else {
					int totalWeek = week(currWeek, endWeek) + 1;
					quantity += totalWeek * promoQuantity * 7;	//广告为日均均销量
				}
			}
			countryMap.put(type, quantity);
		}
		return rsMap;
	}

	/**
	 * 查询当前周之后计划的单日广告数量和广告到期天数
	 * @return Map<productName, Map<country, Map<type, 数量>>> type 0:日销  1：到期天数
	 * @throws ParseException 
	 */
	public Map<String, Map<String, Map<String, Integer>>> findPlanAdDaySales(Date targetDate) throws ParseException {
		Map<String, Map<String, Map<String, Integer>>> rsMap = Maps.newHashMap();
		DateFormat formatWeek = new SimpleDateFormat("yyyyww");
		String week = DateUtils.getWeekStr(new Date(),formatWeek, 4, "");
		Date weekDate = formatWeek.parse(week);
		//第四周周数,用于比较当前周下单产品
		Date lastWeekDate = DateUtils.addDays(weekDate, 21);
		if (targetDate != null && targetDate.after(DateUtils.addMonths(new Date(), 1))) {
			lastWeekDate = targetDate;
		}
		Integer lastWeek = Integer.parseInt(DateUtils.getWeekStr(lastWeekDate,formatWeek, 4, ""));
		String sql = "SELECT i.`product_name`,i.`color_code`,t.`country_code`,i.`promo_quantity`,"+
				" t.`start_week`,t.`end_week`"+
				" FROM `psi_marketing_plan` t ,`psi_marketing_plan_item` i"+
				" WHERE t.`id`=i.`marketing_plan_id` AND t.`sta`='3' AND t.`type`='1' AND i.`promo_quantity`>0 " +
				" AND t.`start_week`<=:p1 AND (t.`end_week`>=:p2 or t.`end_week`='' OR t.`end_week` IS NULL)";
		List<Object[]> list = psiMarketingPlanDao.findBySql(sql, new Parameter(lastWeek+"", week));
		for (Object[] obj : list) {
			if (obj[0] == null) {
				continue;
			}
			String productName = obj[0].toString();
			String color = obj[1] == null ? "" : obj[1].toString();
			if (StringUtils.isNotEmpty(color)) {
				productName = productName + "_" + color;
			}
			String country = obj[2].toString();
			Integer promoQuantity = obj[3]==null?0:Integer.parseInt(obj[3].toString());
			//结束周为空表示不限期
			String endWeekStr = obj[5]==null?"":obj[5].toString();
			Integer endWeek = 0;
			if (StringUtils.isNotEmpty(endWeekStr)) {
				endWeek = Integer.parseInt(endWeekStr);
			}
			Map<String, Map<String, Integer>> productMap = rsMap.get(productName);
			if (productMap == null) {
				productMap = Maps.newHashMap();
				rsMap.put(productName, productMap);
			}
			Map<String, Integer> countryMap = productMap.get(country);
			if (countryMap == null) {
				countryMap = Maps.newHashMap();
				productMap.put(country, countryMap);
			}
			countryMap.put("0", promoQuantity); //计划日销量
			if (endWeek == 0) {
				countryMap.put("1", -1);	//结束日期,-1表示下单覆盖周期内都有效
			} else {
				Date endDate = formatWeek.parse(endWeekStr);
				endDate = DateUtils.addDays(endDate, 7);
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(new Date());
			    int day1 = calendar.get(Calendar.DAY_OF_YEAR);
			    calendar.setTime(endDate);
			    int day2 = calendar.get(Calendar.DAY_OF_YEAR);
			    int days = day2 - day1;
				countryMap.put("1", days);	//距离结束的天数
			}
		}
		return rsMap;
	}
	
	public static void main(String[] args) {
		DateFormat formatWeek = new SimpleDateFormat("yyyyww");
		try {
			Date endDate = formatWeek.parse("201730");
			endDate = DateUtils.addDays(endDate, 7);
			System.out.println(endDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 计算两个周之间相差几周
	 * @param start 开始周(yyyyWW)
	 * @param end	结束周(大于开始周)
	 * @return
	 * @throws ParseException 
	 */
	private static int week(Integer start, Integer end){
		if (start == null || end == null || end < start) {
			return 0;
		}
		SimpleDateFormat format = new SimpleDateFormat("yyyyww");
		try {
			Date t1 = format.parse(start+"");
			Date t2 = format.parse(end+"");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(t1);
		    int day1 = calendar.get(Calendar.DAY_OF_YEAR);
		    calendar.setTime(t2);
		    int day2 = calendar.get(Calendar.DAY_OF_YEAR);
		    int days = day2 - day1;
			return days/7;
		} catch (ParseException e) {
			logger.error("计算周数差异常：" + start + "\t" + end , e);
			return 0;
		}
	}
	
	
	/**
	 *获取上周做了广告的 
	 */
	public Map<String,Set<String>> findAdByDate(Date startDate,Date endDate){
		Map<String,Set<String>> rs = Maps.newHashMap();
		String sql=" SELECT DISTINCT (CASE WHEN b.`color`='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color`) END ) AS proName,b.`country` FROM psi_sku AS b ," +
				" (SELECT a.`country`,a.`sku` FROM amazoninfo_advertising AS a WHERE a.`data_date` BETWEEN :p1 AND :p2 GROUP BY a.`country`,a.`sku`) AS aa" +
				" WHERE b.`sku`=BINARY(aa.sku) AND b.`country`=aa.country AND b.`del_flag`='0' " +
				" UNION " +
				" SELECT a.`product_name` AS proName ,a.`country` FROM amazoninfo_aws_adversting AS a WHERE a.`start_date`<=:p2 AND (a.`end_date` IS NULL OR a.`end_date`>=:p1) GROUP BY a.`product_name`,a.`country`" ;
		List<Object[]> list = psiMarketingPlanDao.findBySql(sql, new Parameter(startDate,endDate));
		for (Object[] obj : list) {
			Set<String> set = null;
			if (obj[0] == null) {
				continue;
			}
			String proName = obj[0].toString();
			String country = obj[1].toString();
			if(rs.get(country)==null){
				set=Sets.newHashSet();
			}else{
				set = rs.get(country);
			}
			set.add(proName);
			rs.put(country, set);
		}
		return rs;
	}
	
	
	/**
	 *目前做广告的
	 */
	public Map<String,Set<String>> findAdingByDate(String date){
		Map<String,Set<String>> rs = Maps.newHashMap();
		String sql=" (SELECT DISTINCT (CASE WHEN b.`color`='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color`) END ) AS proName,b.`country` FROM psi_sku AS b ," +
				" (SELECT a.`country`,a.`sku` FROM amazoninfo_advertising AS a WHERE DATE_FORMAT(a.`create_date`,'%Y-%m-%d') >'"+date+"'  GROUP BY a.`country`,a.`sku`) AS aa	" +
				"			 WHERE b.`sku`=BINARY(aa.sku) AND b.`country`=aa.country AND b.`del_flag`='0' )" +
				"				 UNION" +
				"				 (SELECT a.`product_name` AS proName ,a.`country` FROM amazoninfo_aws_adversting AS a WHERE a.`data_date` >'"+date+"' GROUP BY a.`product_name`,a.`country`);" ;
		List<Object[]> list = psiMarketingPlanDao.findBySql(sql);
		for (Object[] obj : list) {
			Set<String> set = null;
			String proName = obj[0].toString();
			String country = obj[1].toString();
			if(rs.get(country)==null){
				set=Sets.newHashSet();
			}else{
				set = rs.get(country);
			}
			set.add(proName);
			rs.put(country, set);
		}
		return rs;
	}
	
	
	/**
	 *上周做了促销的 
	 */
	public  Map<String,Set<String>> findPromotionsByDate(Date startDate,Date endDate){
		Map<String,Set<String>> rs = Maps.newHashMap();
		 Map<String,Map<String,Integer>>  sale30Map = psiInventoryFbaService.get31SalesMap(null);
		 Map<String,Map<String,PsiProductEliminate>> isNewMap=psiProductEliminateService.findAllByNameAndCountry();
		String sql="SELECT distinct product_name,country,t.`promotion_ids`,t.quantity FROM amazoninfo_promotions_report t "+
				" WHERE (t.`purchase_date` BETWEEN :p1 AND :p2) and t.`promotion_ids` not like '%F-FE and FD Series off 10% 12.13-%' "+
				" AND (t.`promotion_ids`='闪购' OR "+
				" ( (t.`promotion_ids` LIKE 'F-%' OR t.`promotion_ids` LIKE '%,F-%') AND (t.`promotion_ids` NOT LIKE 'F-%A-Page%' AND t.`promotion_ids` NOT LIKE '%,F-%A-Page%' "+
				" AND t.`promotion_ids` NOT LIKE 'F-%AMZ%' AND t.`promotion_ids` NOT LIKE '%,F-%AMZ%') )) ";
		List<Object[]> list= psiMarketingPlanDao.findBySql(sql,new Parameter(startDate,endDate));
		for (Object[] obj : list) {
			Set<String> set = null;
			String proName = obj[0].toString();
			String country = obj[1].toString();
			String promotionId=(obj[2]==null?"":obj[2].toString());
			if("闪购".equals(promotionId)){//单次闪促超过1/4*(该产品该平台31日销) 如果单次闪促销量超过该产品一周的销量且闪促销量超过了当前fba库存的20%还是会预警
				Integer quantity=(Integer.parseInt(obj[3]==null?"0":obj[3].toString()));
				if(sale30Map.get(country)!=null&&sale30Map.get(country).get(proName)!=null&&quantity>sale30Map.get(country).get(proName)/4f){
					if(isNewMap.get(proName)!=null&&isNewMap.get(proName).get(country)!=null&&!"4".equals(isNewMap.get(proName).get(country).getIsSale())){
						Map<String,PsiInventoryFba>  amazonInventory=psiInventoryService.getProductFbaInfo(proName);
						if(amazonInventory.get(proName+"_"+country)!=null){
							Integer totalQuantity=amazonInventory.get(proName+"_"+country).getRealTotal();
							if(quantity>=totalQuantity*0.2){
								if(rs.get(country)==null){
									set=Sets.newHashSet();
								}else{
									set = rs.get(country);
								}
								set.add(proName);
								rs.put(country, set);
							}
						}
					}
				}
			}else{
				if(rs.get(country)==null){
					set=Sets.newHashSet();
				}else{
					set = rs.get(country);
				}
				set.add(proName);
				rs.put(country, set);
			}
		}
		return rs;
	}
	
	/**
	 *获得上周的计划 
	 */
	public List<PsiMarketingPlan> findByWeek(String startWeek) {
		DetachedCriteria dc = psiMarketingPlanDao.createDetachedCriteria();
		dc.add(Restrictions.eq("sta", "3"));
		dc.add(Restrictions.le("startWeek",startWeek));
		dc.add(Restrictions.or(Restrictions.ge("endWeek",startWeek),Restrictions.isNull("endWeek"),Restrictions.eq("endWeek","")));
//		dc.add(Restrictions.sqlRestriction(" (end_week >='"+startWeek+"' or (end_week='' or end_week is null))"));
		return psiMarketingPlanDao.find(dc);
	}
	
	
	
	/**
	 * 检查上周没开始的计划
	 * 1:做了促销没做促销计划
	 * 2：做了促销计划没做促销
	 * 3：做了广告没做广告计划
	 * 4：做了广告计划没做广告
	 * @throws ParseException 
	 */
	@Transactional(readOnly = false)
	public Map<String,Map<String,Set<String>>> getWarnInfos() throws ParseException{
		Map<String,Map<String,Set<String>>> rs = Maps.newHashMap();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date today = sdf.parse(sdf.format(new Date()));
		Date startDay = DateUtils.getMonday(DateUtils.addDays(today, -7));
		Date endDay = DateUtils.getSunday(startDay);
		Map<String,Set<String>> promos = this.findPromotionsByDate(startDay, endDay);
		Map<String,Set<String>> ads = this.findAdByDate(startDay, endDay);
		 
		String week = DateUtils.getWeekStr(startDay,new SimpleDateFormat("yyyyww"), 4, "");
		List<PsiMarketingPlan> plans = this.findByWeek(week);
		Map<String,Set<String>> promoPlans = Maps.newHashMap();
		Map<String,Set<String>> adPlans = Maps.newHashMap();
		
		if(plans!=null&&plans.size()>0){
			for(PsiMarketingPlan plan:plans){
				String country = plan.getCountryCode();
				String type =plan.getType();
				List<PsiMarketingPlanItem>  items = plan.getItems();
				Set<String> promoSet = null;
				Set<String> adSet = null;
				if(promoPlans.get(country)==null){
					promoSet = Sets.newHashSet();
				}else{
					promoSet=promoPlans.get(country);
				}
				if(adPlans.get(country)==null){
					adSet = Sets.newHashSet();
				}else{
					adSet=adPlans.get(country);
				}
				
				if("0".equals(type)){//促销
					Map<String,Set<String>> warnMap2= null;
					if(rs.get(country)==null){
						warnMap2=Maps.newTreeMap();
					}else{
						warnMap2 = rs.get(country);
					}
					Set<String> warn2 = null;
					if(warnMap2.get("2")==null){
						warn2 = Sets.newHashSet();
					}else{
						warn2=warnMap2.get("2");
					}
					Set<String> countryPros = promos.get(country);
					for(PsiMarketingPlanItem item:items){
						String proName = item.getNameWithColor();
						promoSet.add(proName);
						promoPlans.put(country, promoSet);
						//做了促销计划没促销
						if(countryPros==null||!countryPros.contains(proName)){
							if(StringUtils.isEmpty(item.getWarn())){
								item.setWarn(week+"周,没做促销;");
							}else{
								item.setWarn(item.getWarn()+week+"周,没做促销;");
							}
							warn2.add(proName);
							warnMap2.put("2", warn2);
							rs.put(country, warnMap2);
						}
					}
				}else if("1".equals(type)){
					Map<String,Set<String>> warnMap4= null;
					if(rs.get(country)==null){
						warnMap4=Maps.newHashMap();
					}else{
						warnMap4 = rs.get(country);
					}
					Set<String> warn4 = null;
					if(warnMap4.get("4")==null){
						warn4 = Sets.newHashSet();
					}else{
						warn4=warnMap4.get("4");
					}
					Set<String> countryAds = ads.get(country);
					adPlans.put(country, adSet);
					for(PsiMarketingPlanItem item:items){
						String proName = item.getNameWithColor();
						adSet.add(proName);
						if(countryAds==null||!countryAds.contains(proName)){
							if(StringUtils.isEmpty(item.getWarn())){
								item.setWarn(week+"周,没做广告;");
							}else{
								item.setWarn(item.getWarn()+week+"周,没做广告;");
							}
							warn4.add(proName);
							warnMap4.put("4", warn4);
							rs.put(country, warnMap4);
						}
					}
				}
				this.psiMarketingPlanDao.save(plan);
			}
		
		}
		
		if(promoPlans!=null&&promoPlans.size()>0){
			//做了促销没有做促销计划
			for(Map.Entry<String,Set<String>>  proEntry:promos.entrySet()){
				String country = proEntry.getKey();
				Set<String> pros = promoPlans.get(country);
				
				Map<String,Set<String>> warnMap1= null;
				if(rs.get(country)==null){
					warnMap1=Maps.newHashMap();
				}else{
					warnMap1 = rs.get(country);
				}
				Set<String> warn1 = null;
				if(warnMap1.get("1")==null){
					warn1 = Sets.newHashSet();
				}else{
					warn1=warnMap1.get("1");
				}
				for(String proColor:proEntry.getValue()){
					if(pros==null||!pros.contains(proColor)){
						warn1.add(proColor);
						warnMap1.put("1", warn1);
						rs.put(country, warnMap1);
					}
				}
			}
		}else{
			for(Map.Entry<String,Set<String>>  proEntry:promos.entrySet()){
				//做了促销没有做促销计划
				String country = proEntry.getKey();
				Map<String,Set<String>> warnMap1= null;
				if(rs.get(country)==null){
					warnMap1=Maps.newHashMap();
				}else{
					warnMap1 = rs.get(country);
				}
				Set<String> warn1 = null;
				if(warnMap1.get("1")==null){
					warn1 = Sets.newHashSet();
				}else{
					warn1=warnMap1.get("1");
				}
				for(String proColor:proEntry.getValue()){
					warn1.add(proColor);
					warnMap1.put("1", warn1);
					rs.put(country, warnMap1);
				}
			}
		}
		
		
		if(adPlans!=null&&adPlans.size()>0){
			//做了广告没有做广告计划
			for(Map.Entry<String,Set<String>>  adEntry:ads.entrySet()){
				String country = adEntry.getKey();
				Set<String> adPlanSet = adPlans.get(country);
				Map<String,Set<String>> warnMap3= null;
				if(rs.get(country)==null){
					warnMap3=Maps.newHashMap();
				}else{
					warnMap3 = rs.get(country);
				}
				Set<String> warn3 = null;
				if(warnMap3.get("3")==null){
					warn3 = Sets.newHashSet();
				}else{
					warn3=warnMap3.get("3");
				}
				for(String proColor:adEntry.getValue()){//这个国家所有做了广告的
					if(adPlanSet==null||!adPlanSet.contains(proColor)){
						warn3.add(proColor);
						warnMap3.put("3", warn3);
						rs.put(country, warnMap3);
					}
				}
			}
		}else{
			for(Map.Entry<String,Set<String>>  adEntry:ads.entrySet()){
				//做了广告没有做广告计划
				String country = adEntry.getKey();
				Map<String,Set<String>> warnMap3= null;
				if(rs.get(country)==null){
					warnMap3=Maps.newHashMap();
				}else{
					warnMap3 = rs.get(country);
				}
				Set<String> warn3 = null;
				if(warnMap3.get("3")==null){
					warn3 = Sets.newHashSet();
				}else{
					warn3=warnMap3.get("3");
				}
				for(String proColor:adEntry.getValue()){
					warn3.add(proColor);
					warnMap3.put("3", warn3);
					rs.put(country, warnMap3);
				}
			}
		}
		
		return rs;
	}
	
	
	/**
	 *通知发信
	 *  * 检查上周没开始的计划
	 * 1:做了促销没做促销计划
	 * 2：做了促销计划没做促销
	 * 3：做了广告没做广告计划
	 * 4：做了广告计划没做广告
	 */
	public void exeWarn(){
		try{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDay = DateUtils.getMonday(DateUtils.addDays(new Date(), -7));
		Date endDay = DateUtils.getSunday(startDay);
		Map<String,Map<String,Set<String>>> warnMap=this.getWarnInfos();
		if(warnMap!=null&&warnMap.size()>0){
			StringBuffer contents=new StringBuffer();
			contents.append("Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;<br/>上周("+sdf.format(startDay)+"~"+sdf.format(endDay)+")营销计划与实际信息预警如下：<br/>");
			contents.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '><tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '>" +
			"<th>国家</th><th>产品名</th><th>情况</th></tr>");
			for(Map.Entry<String,Map<String,Set<String>>> typeEntry : warnMap.entrySet()){
				String country = typeEntry.getKey();
				Map<String,Set<String>> typeMap = typeEntry.getValue();
				int i=0;
				for(Map.Entry<String,Set<String>> entry:typeMap.entrySet()){
					String type=entry.getKey();
					String typeName="";
					if("1".equals(type)){
						typeName="有促销没促销计划";
					}else if("2".equals(type)){
						typeName="有促销计划没促销";
					}else if("3".equals(type)){
						typeName="有广告没广告计划";
					}else if("4".equals(type)){
						typeName="有广告计划没广告";
					}
					for(String proName:entry.getValue()){
						String color="#f5fafe";
						if(i==0){
							color="#99CCFF";
						}
						i++;
						contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:"+color+"; '>" +
								"<td>"+("com".equals(country)?"us":country)+"</td><td>"+proName+"</td><td>"+typeName+"</td></tr>");
					}
				}
			}
			sendNoticeEmail("amazon-sales@inateck.com,erp_development@inateck.com", contents.toString(), "营销计划预警", "", "");
		}
		}catch(Exception ex ){
			logger.error("营销计划预警异常"+ex.getMessage(), ex);
		}
	}
	
	
	/**
	 * sendemail
	 */
	public void sendNoticeEmail(String email,String content,String subject,String ccEmail,String bccEmail){
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();
			final MailInfo mailInfo = new MailInfo(email,subject+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(content);
			if(StringUtils.isNotEmpty(bccEmail)){
				mailInfo.setBccToAddress(bccEmail);
			}
			if(StringUtils.isNotEmpty(ccEmail)){
				mailInfo.setCcToAddress(ccEmail);
			}
			//发送成功不成功都能保存
			new Thread(){
				@Override
				public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
		}
	}    
	
	
	/**
	 *初始化广告数据 
	 */
	@Transactional(readOnly=false)
	public void initAds(){
		Map<String,Set<String>> rs = this.findAdingByDate("2017-07-06");
		List<PsiMarketingPlan> list = Lists.newArrayList();
		String week = DateUtils.getWeekStr(new Date(),new SimpleDateFormat("yyyyww"), 4, "");
		for(Map.Entry<String,Set<String>> entry :rs.entrySet()){
			String country = entry.getKey();
			Set<String> products = entry.getValue();
			for(String proColor:products){
				String arr []=proColor.split("_");
				String proName = arr[0];
				String color="";
				if(arr.length>1){
					color=arr[1];
				}
				PsiMarketingPlan plan = new PsiMarketingPlan(country, "2017-07-20系统初始化", "3", "1");
					plan.setCreateDate(new Date());
					plan.setStartWeek(week);
					PsiMarketingPlanItem item = new PsiMarketingPlanItem();
					item.setProductName(proName);
					item.setColorCode(color);
					item.setDelFlag("0");
					item.setMarketingPlan(plan);
					item.setPromoQuantity(1);//默认写广告日均数为1
				plan.getItems().add(item);
				list.add(plan);
			}
		}
		this.psiMarketingPlanDao.save(list);
	}
	
	
//	public static void main(String[] args) throws ParseException {
//		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
//		PsiMarketingPlanService  s= applicationContext.getBean(PsiMarketingPlanService.class);
//		s.exeWarn();
//	}
	
}
