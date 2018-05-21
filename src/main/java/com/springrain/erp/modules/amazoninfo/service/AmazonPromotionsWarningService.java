/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
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
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonLightningDealsDao;
import com.springrain.erp.modules.amazoninfo.dao.AmazonPromotionsWarningDao;
import com.springrain.erp.modules.amazoninfo.dao.AmazonPromotionsWarningItemDao;
import com.springrain.erp.modules.amazoninfo.dao.AmazonSysPromotionsDao;
import com.springrain.erp.modules.amazoninfo.dao.AmazonSysPromotionsInventoryDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonLightningDeals;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPromotionsWarning;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPromotionsWarningItem;
import com.springrain.erp.modules.amazoninfo.entity.AmazonSysPromotions;
import com.springrain.erp.modules.amazoninfo.entity.AmazonSysPromotionsInventory;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.utils.UserUtils;


@Component
@Transactional(readOnly = true)
public class AmazonPromotionsWarningService extends BaseService {

	@Autowired
	private AmazonPromotionsWarningDao amazonPromotionsWarningDao;
	
	@Autowired
	private AmazonPromotionsWarningItemDao amazonPromotionsWarningItemDao;
	
	@Autowired
	private AmazonSysPromotionsDao amazonSysPromotionsDao;
	
	@Autowired
	private AmazonOrderService amazonOrderService;
	
	@Autowired
	private AmazonLightningDealsDao amazonLightningDealsDao;
	
	@Autowired
	private AmazonSysPromotionsInventoryDao amazonSysPromotionsInventoryDao;
	
	@Autowired
	private PsiProductTypeGroupDictService psiTypeGroupService;
	
	@Autowired
	private PsiProductGroupUserService  psiProductGroupUserService;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AmazonPromotionsWarningService.class);
	@Transactional(readOnly = false)
	public void savePromotionsCode(AmazonSysPromotions amazonSysPromotions){
		amazonSysPromotionsDao.save(amazonSysPromotions);
	}
	
	public AmazonSysPromotions getPromotionsCodeById(Integer id){
		return amazonSysPromotionsDao.get(id);
	}
	
	public Map<String,String> getAsinMap(String country,String accountName){
		String sql ="SELECT DISTINCT CONCAT(a.`product_name`,IF(a.`color`=' ','',CONCAT('_',a.`color`))),a.`asin` FROM psi_sku AS a WHERE a.`del_flag`='0' AND  a.`country`= :p1 and a.account_name=:p2 and a.asin is not null "+
	    "and a.product_name not like '%other%' and a.product_name not like '%old%' ";
		List<Object[]> list=this.amazonPromotionsWarningDao.findBySql(sql,new Parameter(country,accountName));
		Map<String,String> asinMap = Maps.newHashMap();
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				asinMap.put(obj[1].toString(), obj[0].toString());
			}
		}
		return asinMap;
	}
	
	
	public Map<String,String> getNoEmail(){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT t.`country`,group_concat(t.`promotions_code`) FROM amazoninfo_sys_promotions s JOIN amazoninfo_sys_promotions_item t ON s.id=t.`track_id` "+
                   " WHERE s.type='0' AND t.`email` IS NULL group by t.country ";
		List<Object[]> list=amazonSysPromotionsDao.findBySql(sql);
		for (Object[] obj: list) {
			map.put(obj[0].toString(), obj[1].toString());
		}
		return map;
	}
	
	public Map<String,Set<String>> getNoEmail2(){
		Map<String,Set<String>> map=Maps.newHashMap();
		String sql="SELECT t.`country`,t.`promotions_code` FROM amazoninfo_sys_promotions s JOIN amazoninfo_sys_promotions_item t ON s.id=t.`track_id` "+
                   " WHERE s.type='0' AND t.`email` IS NULL ";
		List<Object[]> list=amazonSysPromotionsDao.findBySql(sql);
		for (Object[] obj: list) {
			Set<String> set=map.get(obj[0].toString());
			if(set==null){
				set=Sets.newHashSet();
				map.put(obj[0].toString(),set);
			}
			set.add(obj[1].toString());
		}
		return map;
	}
	
	@Transactional(readOnly = false)
	public void cancelPromtionsCode(Integer id){
		String sql="update amazoninfo_sys_promotions set status='2' where id=:p1";
		amazonSysPromotionsDao.updateBySql(sql, new Parameter(id));
	}
	
	@Transactional(readOnly = false)
	public void updateEmail(Map<String,Map<String,String>> codeMap){
		String updateSql="update amazoninfo_sys_promotions_item set email=:p1 where promotions_code=:p2 and country=:p3 ";
		for (Map.Entry<String,Map<String,String>> entry : codeMap.entrySet()) {  
		    String country=entry.getKey();
			Map<String,String> countryMap=entry.getValue();
			for (Map.Entry<String,String> entryCountry : countryMap.entrySet()) {
			    String code=entryCountry.getKey();
				String email=entryCountry.getValue();
				amazonSysPromotionsDao.updateBySql(updateSql, new Parameter(email,code,country));
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void updateCustomId(){
		Map<String,Map<String,Object[]>> customMap=amazonOrderService.findCustomId();
		String updateSql="update amazoninfo_sys_promotions_item set custom_id=:p1,product_name=:p2,amazon_order_id=:p3 where promotions_id=:p4 and country=:p5 ";
		for(Map.Entry<String,Map<String,Object[]>> entry :customMap.entrySet()){
		    String country=entry.getKey();
			Map<String,Object[]> countryMap=entry.getValue();
			for (Map.Entry<String,Object[]> trackEntry: countryMap.entrySet()) {
				String trackId=trackEntry.getKey();
				Object[] obj=trackEntry.getValue();
				String customId=(obj[2]==null?null:obj[2].toString());//2
				String amazonOrderId=(obj[3]==null?null:obj[3].toString());//3
				String name=(obj[4]==null?null:obj[4].toString());//4
				amazonSysPromotionsDao.updateBySql(updateSql, new Parameter(customId,name,amazonOrderId,trackId,country));
			}
		}
	}
	
	public Map<String,Set<String>> getNotAmazonOrderId(){
		Map<String,Set<String>> map=Maps.newHashMap();
		String sql="select country,promotions_id from amazoninfo_sys_promotions_item where amazon_order_id is null group by country";
		List<Object[]> list=amazonSysPromotionsInventoryDao.findBySql(sql);
		for (Object[] obj: list) {
			Set<String> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Sets.newHashSet();
				map.put(obj[0].toString(), temp);
			}
			temp.add(obj[1].toString());
		}
		return map;
	}
	
	//0:不关闭
	public Map<String,String> isClosePromotionsId(String country,Set<String> promotionsId){
		Map<String,String>  map=Maps.newHashMap();
		String sql="select promotions_id,amazon_order_id from amazoninfo_sys_promotions_item where country=:p1 and promotions_id in :p2 ";
		List<Object[]> list=amazonSysPromotionsInventoryDao.findBySql(sql,new Parameter(country,promotionsId));
		for (Object[] obj: list) {
			map.put(obj[0].toString(),obj[1]==null?"0":"1");
		}
		return map;
	}
	
	public List<AmazonPromotionsWarning> find() {
		DetachedCriteria dc = amazonPromotionsWarningDao.createDetachedCriteria();
		dc.add(Restrictions.eq("isActive","0"));
		dc.createAlias("this.items", "item");
		dc.add(Restrictions.eq("item.delFlag","0"));
		return amazonPromotionsWarningDao.find(dc);
	}
	
	public Page<AmazonPromotionsWarning> find(Page<AmazonPromotionsWarning> page, AmazonPromotionsWarning amazonPromotionsWarning) {
		DetachedCriteria dc = amazonPromotionsWarningDao.createDetachedCriteria();
		
		if(StringUtils.isNotEmpty(amazonPromotionsWarning.getCountry())){
			dc.add(Restrictions.eq("country", amazonPromotionsWarning.getCountry()));
		}
		
		if(StringUtils.isNotBlank(amazonPromotionsWarning.getOneRedemption())){
			dc.add(Restrictions.like("promotionId","%F-"+amazonPromotionsWarning.getPromotionId()+"%"));
		}
		if(StringUtils.isNotEmpty(amazonPromotionsWarning.getPromotionId())){
			//dc.add(Restrictions.like("promotionId","%"+amazonPromotionsWarning.getPromotionId()+"%"));
			dc.add(Restrictions.or(Restrictions.like("promotionId", "%"+amazonPromotionsWarning.getPromotionId()+"%"),
						Restrictions.eq("promotionCode",amazonPromotionsWarning.getPromotionId())));
		}
	
		//dc.add(Restrictions.eq("item.delFlag","0"));
		dc.createAlias("this.items", "item");
		if(StringUtils.isNotEmpty(amazonPromotionsWarning.getRemark())){
			dc.add(Restrictions.like("item.productNameColor", "%"+amazonPromotionsWarning.getRemark()+"%"));
		}
	
		if(StringUtils.isNotBlank(amazonPromotionsWarning.getClaimCode())){
			List<String> nameList=psiTypeGroupService.getProductNameByLineId(amazonPromotionsWarning.getClaimCode());
			if(nameList!=null&&nameList.size()>0){
				dc.add(Restrictions.in("item.productNameColor",nameList));
			}else{
				dc.add(Restrictions.eq("item.productNameColor",amazonPromotionsWarning.getClaimCode()));
			}
		}
		
		if(amazonPromotionsWarning.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate", amazonPromotionsWarning.getCreateDate()));
		}
		
		if (amazonPromotionsWarning.getUpdateDate()!=null){
			dc.add(Restrictions.lt("createDate",DateUtils.addDays(amazonPromotionsWarning.getUpdateDate(),1)));
		}
		if (amazonPromotionsWarning.getCreateUser()!=null && StringUtils.isNotEmpty(amazonPromotionsWarning.getCreateUser().getId())){
			dc.add(Restrictions.eq("createUser", amazonPromotionsWarning.getCreateUser()));
		}
		
		if(StringUtils.isNotBlank(amazonPromotionsWarning.getProType())){
			dc.add(Restrictions.eq("proType",amazonPromotionsWarning.getProType()));
		}
		
		if(StringUtils.isEmpty(amazonPromotionsWarning.getWarningSta())){
			dc.add(Restrictions.in("warningSta",Sets.newHashSet("0","1")));
		}else if(!"8".equals(amazonPromotionsWarning.getWarningSta())){
			dc.add(Restrictions.eq("warningSta",amazonPromotionsWarning.getWarningSta()));
		}
		
		if(StringUtils.isNotBlank(amazonPromotionsWarning.getAccountName())){
			dc.add(Restrictions.eq("accountName",amazonPromotionsWarning.getAccountName()));
		}
		return amazonPromotionsWarningDao.find2(page, dc);
	}
	
	public Page<AmazonSysPromotions> findPromotions(Page<AmazonSysPromotions> page,AmazonSysPromotions amazonSysPromotions) {
		DetachedCriteria dc = amazonSysPromotionsDao.createDetachedCriteria();
		
		if(StringUtils.isNotEmpty(amazonSysPromotions.getCountry())){
			dc.add(Restrictions.eq("country", amazonSysPromotions.getCountry()));
		}
	
		if(amazonSysPromotions.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate", amazonSysPromotions.getCreateDate()));
		}
		
		if (amazonSysPromotions.getReviewDate()!=null){
			dc.add(Restrictions.lt("createDate",DateUtils.addDays(amazonSysPromotions.getReviewDate(),1)));
		}
		
		if(StringUtils.isNotBlank(amazonSysPromotions.getType())){
			dc.add(Restrictions.eq("type",amazonSysPromotions.getType()));
		}
		
		if(StringUtils.isEmpty(amazonSysPromotions.getStatus())){
			dc.add(Restrictions.ne("status","2"));
		}else{
			dc.add(Restrictions.eq("status",amazonSysPromotions.getStatus()));
		}
		return amazonSysPromotionsDao.find2(page, dc);
	}
	
	
	public AmazonPromotionsWarning get(Integer id){
		DetachedCriteria dc = amazonPromotionsWarningDao.createDetachedCriteria();
		dc.add(Restrictions.eq("id", id));
		dc.createAlias("this.items", "item");
		dc.add(Restrictions.eq("item.delFlag","0"));
		List<AmazonPromotionsWarning> list =amazonPromotionsWarningDao.find(dc);
		if(list!=null&&list.size()>0){
			AmazonPromotionsWarning warn =list.get(0);
			Hibernate.initialize(warn.getItems());
			return warn;
		}
		return null;
	}
	
	public AmazonPromotionsWarning getById(Integer id){
		AmazonPromotionsWarning warn=amazonPromotionsWarningDao.get(id);
		Hibernate.initialize(warn.getItems());
		return warn;
	}
	
	@Transactional(readOnly = false)
	public String deleteItem(Integer id){
		String sql="update amazoninfo_promotions_warning_item set del_flag='1' where id=:p1";
		amazonPromotionsWarningItemDao.updateBySql(sql, new Parameter(id));
		return "1";
	}
	
	
	@Transactional(readOnly = false)
	public void saveItem(AmazonPromotionsWarningItem item) {
		if(item.getId()!=null){
			amazonPromotionsWarningItemDao.getSession().merge(item);
		}else{
			amazonPromotionsWarningItemDao.save(item);
		}
	}
	
	@Transactional(readOnly = false)
	public void save(List<AmazonPromotionsWarning> promotionsWarnings) {
		//amazonPromotionsWarningDao.save(promotionsWarnings);
		for (AmazonPromotionsWarning promotionsWarning : promotionsWarnings) {
			amazonPromotionsWarningDao.getSession().merge(promotionsWarning);
		}
	}
	
	@Transactional(readOnly = false)
	public void save2(AmazonPromotionsWarning promotionsWarning) {
		
	}
	
	
	@Transactional(readOnly = false)
	public void save(AmazonPromotionsWarning promotionsWarning) {
		amazonPromotionsWarningDao.save(promotionsWarning);
	}
	
	public Integer isNotExist(String promotionsId,String country){
		String sql="select id from amazoninfo_promotions_warning where binary(promotion_id)=:p1 and country=:p2 ";
		List<Integer> list=amazonPromotionsWarningDao.findBySql(sql,new Parameter(promotionsId,country));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public String getPromotionCode(String promotionsId,String country){
		String sql="select promotion_code from amazoninfo_promotions_warning where binary(promotion_id)=:p1 and country=:p2 ";
		List<String> list=amazonPromotionsWarningDao.findBySql(sql,new Parameter(promotionsId,country));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public void deleteByWarningId(Integer warningId,String asin){
		String sql="update amazoninfo_promotions_warning_item set del_flag='1' where warning_id=:p1 and asin=:p2 ";
		amazonPromotionsWarningItemDao.updateBySql(sql, new Parameter(warningId,asin));
	}
	
	@Transactional(readOnly = false)
	public void updateHalfQuantity(Integer quantity,Integer id){
		String sql="update amazoninfo_promotions_warning_item set half_hour_quantity=:p1 where id=:p2";
		amazonPromotionsWarningItemDao.updateBySql(sql, new Parameter(quantity,id));
		String sql1="update amazoninfo_promotions_warning set update_user=:p1,update_date=:p2 where id=(select warning_id from amazoninfo_promotions_warning_item where id=:p3)";
		amazonPromotionsWarningDao.updateBySql(sql1, new Parameter(UserUtils.getUser().getId(),new Date(),id));
	}
	
	@Transactional(readOnly = false)
	public void updateCumulativeQuantity(Integer quantity,Integer id){
		String sql="update amazoninfo_promotions_warning_item set cumulative_quantity=:p1 where id=:p2";
		amazonPromotionsWarningItemDao.updateBySql(sql, new Parameter(quantity,id));
		String sql1="update amazoninfo_promotions_warning set update_user=:p1,update_date=:p2 where id=(select warning_id from amazoninfo_promotions_warning_item where id=:p3)";
		amazonPromotionsWarningDao.updateBySql(sql1, new Parameter(UserUtils.getUser().getId(),new Date(),id));
	}
	
	@Transactional(readOnly = false)
	public void updateCumulativeQuantity(Set<String> asinQuantity){
		String sql="update amazoninfo_promotions_warning_item set cumulative_quantity=:p1 where warning_id=:p2 and asin=:p3";
		String sql1="update amazoninfo_promotions_warning set warning_sta='1',update_user=:p1,update_date=:p2 where id=:p3 ";
		for (String asinStr: asinQuantity) {
			String[] arr=asinStr.split(",");
			Integer id=Integer.parseInt(arr[0]);
			String asin=arr[1];
			Integer quantity=Integer.parseInt(arr[2]);
			amazonPromotionsWarningItemDao.updateBySql(sql, new Parameter(quantity,id,asin));
			amazonPromotionsWarningDao.updateBySql(sql1, new Parameter(UserUtils.getUser().getId(),new Date(),id));
		}
	}
	
	@Transactional(readOnly = false)
	public void  updateRemark(String remark,Integer id){
		String sql="update amazoninfo_promotions_warning set remark=:p1 where id=:p2";
		amazonPromotionsWarningDao.updateBySql(sql, new Parameter(remark,id));
	}
	
	@Transactional(readOnly = false)
	public void  updateState(String state,Integer id){
		String sql="update amazoninfo_promotions_warning set warning_sta=:p1,update_user=:p2,update_date=:p3 where id=:p4";
		amazonPromotionsWarningDao.updateBySql(sql, new Parameter(state,UserUtils.getUser().getId(),new Date(),id));
	}
	
	@Transactional(readOnly = false)
	public void  updateStateAndRemark(String state,Integer id){
		String sql="update amazoninfo_promotions_warning set warning_sta=:p1,update_user=:p2,update_date=:p3,remark=NULL where id=:p4";
		amazonPromotionsWarningDao.updateBySql(sql, new Parameter(state,UserUtils.getUser().getId(),new Date(),id));
	}
	
	
	@Transactional(readOnly = false)
	public void  checkState(String state,Integer id){
		String sql="update amazoninfo_promotions_warning set warning_sta=:p1,check_user=:p2,check_date=:p3 where id=:p4";
		amazonPromotionsWarningDao.updateBySql(sql, new Parameter(state,UserUtils.getUser().getId(),new Date(),id));
	}
	
	
	@Transactional(readOnly = false)
	public void specialCheck(Integer id,String checeUser){
		String sql="update amazoninfo_promotions_warning set special_check_user=:p1 where id=:p2";
		amazonPromotionsWarningDao.updateBySql(sql, new Parameter(checeUser,id));
	}
	
	
	public List<AmazonPromotionsWarning> findByEage() {
		DetachedCriteria dc = amazonPromotionsWarningDao.createDetachedCriteria();
		dc.add(Restrictions.eq("warningSta","1"));
		dc.createAlias("this.items", "item");
		dc.add(Restrictions.eq("item.delFlag","0"));
		List<AmazonPromotionsWarning> list =amazonPromotionsWarningDao.find(dc);
		for(AmazonPromotionsWarning warn:list){
			Hibernate.initialize(warn.getItems());
		}
		return list;
	}
	
	
	//待改
	public Map<String,List<AmazonPromotionsWarning>> findFPromtonsByCountry() {
		Map<String,List<AmazonPromotionsWarning>> map=Maps.newHashMap();
		DetachedCriteria dc = amazonPromotionsWarningDao.createDetachedCriteria();
		dc.add(Restrictions.eq("warningSta","1"));
		 List<AmazonPromotionsWarning> list =amazonPromotionsWarningDao.find(dc);
		for(AmazonPromotionsWarning warn:list){
			Hibernate.initialize(warn.getItems());
		}
		
		return map;
	}
	
	public List<AmazonPromotionsWarning> findFreePromotions() {
		DetachedCriteria dc = amazonPromotionsWarningDao.createDetachedCriteria();
		dc.add(Restrictions.eq("isActive","0"));
		dc.add(Restrictions.like("promotionId","Free-",MatchMode.START));
		 List<AmazonPromotionsWarning> list =amazonPromotionsWarningDao.find(dc);
		for(AmazonPromotionsWarning warn:list){
			Hibernate.initialize(warn.getItems());
		}
		return list;
	}
	
	
	@Transactional(readOnly = false)
	public void  updateStaAndResById(Integer id,String remark){
		String sql="update amazoninfo_promotions_warning set remark=:p1,warning_sta='2' where id=:p2";
		amazonPromotionsWarningDao.updateBySql(sql, new Parameter(remark,id));
	}
	
	/**
	 *更新结束状态及原因 
	 */
	@Transactional(readOnly = false)
	public void updateStaAndRes(String tranId,String country,String res) {
		if(StringUtils.isNotBlank(res)&&res.contains("成功结束折扣")){
			String sql ="UPDATE amazoninfo_promotions_warning  AS a SET a.`warning_sta`='2',remark=:p3 WHERE binary(a.`promotion_id`) =:p1 AND a.`country`=:p2 ";
			amazonPromotionsWarningDao.updateBySql(sql, new Parameter(tranId,country,res));
		}else{
			String sql ="UPDATE amazoninfo_promotions_warning  AS a SET remark=:p3 WHERE binary(a.`promotion_id`) =:p1 AND a.`country`=:p2 ";
			amazonPromotionsWarningDao.updateBySql(sql, new Parameter(tranId,country,res));
		}
		
	}
	
	@Transactional(readOnly = false)
	public void updateCheckFlag(String tranId,String country) {
		String sql ="UPDATE amazoninfo_promotions_warning  AS a SET check_flag='0' WHERE binary(a.`promotion_id`) =:p1 AND a.`country`=:p2 ";
		amazonPromotionsWarningDao.updateBySql(sql, new Parameter(tranId,country));
	}
	
	@Transactional(readOnly = false)
	public void updateIsActive(List<String> promotionsId,String country) {
	    String sql="UPDATE amazoninfo_promotions_warning  AS a SET a.`is_active`='0' WHERE binary(a.`promotion_id`) in :p1 AND a.`country`=:p2 ";
	    amazonPromotionsWarningDao.updateBySql(sql, new Parameter(promotionsId,country));
	    
	    String sql1="UPDATE amazoninfo_promotions_warning  AS a SET a.`is_active`='1' WHERE a.`is_active`='0' and binary(a.`promotion_id`) not in :p1 AND a.`country`=:p2 ";
	    amazonPromotionsWarningDao.updateBySql(sql1, new Parameter(promotionsId,country));
	    
	    String sql2="UPDATE amazoninfo_promotions_warning   AS a SET a.`warning_sta`='2'  where a.`is_active`='1' AND a.`country`=:p1 ";
	    amazonPromotionsWarningDao.updateBySql(sql2, new Parameter(country));
	}
	
	
	
	public Map<String,List<AmazonPromotionsWarning>> getUnPromotions(){
		String sql="SELECT DISTINCT w.`promotion_id`,w.`start_date`,w.`end_date`,w.`country`,w.id,GROUP_CONCAT(t.asin),w.`warning_sta`  FROM amazoninfo_promotions_warning w "+
		" JOIN amazoninfo_promotions_warning_item t ON w.id=t.`warning_id` AND t.`del_flag`='0' "+
		" JOIN psi_product_eliminate s ON t.`product_name_color`=CONCAT(s.`product_name`,CASE WHEN s.`color`!='' THEN CONCAT ('_',s.`color`) ELSE '' END) "+ 
		" AND s.del_flag='0'  AND s.`country`=w.`country` AND s.`is_sale`!='4'  "+
		" WHERE  ((w.`promotion_id` LIKE 'F-%' AND  w.`warning_sta`='0') or (w.`warning_sta`='1' and t.half_hour_quantity is null and t.cumulative_quantity is null)) and w.`is_active`='0'  GROUP BY w.`promotion_id`,w.`start_date`,w.`end_date`,w.`country`,w.id ";
		Map<String,List<AmazonPromotionsWarning>>  map=Maps.newHashMap();
		List<Object[]> list=amazonPromotionsWarningDao.findBySql(sql);
		
		for (Object[] obj: list) {
			AmazonPromotionsWarning warn=new AmazonPromotionsWarning();
			String promotionId=obj[0].toString();
			Date start=new Date(((Timestamp)obj[1]).getTime());
			Date end=new Date(((Timestamp)obj[2]).getTime());
			String country=obj[3].toString();
			Date date=warn.xmlGregorianToLocalDate(country);
			Integer id=Integer.parseInt(obj[4].toString());
			String claimCode=obj[5].toString();
			String statu=obj[6].toString();
			if("0".equals(statu)){
				if((start.before(date)||(start.equals(date)))&&(end.after(date)||(end.equals(date)))){
					List<AmazonPromotionsWarning> temp=map.get(country);
					if(temp==null){
						temp=Lists.newArrayList();
						map.put(country,temp);
					}
					warn.setPromotionId(promotionId);
					warn.setCountry(country);
					warn.setId(id);
					warn.setClaimCode(claimCode);
					temp.add(warn);
				}
			}else{
				List<AmazonPromotionsWarning> temp=map.get(country);
				if(temp==null){
					temp=Lists.newArrayList();
					map.put(country,temp);
				}
				warn.setPromotionId(promotionId);
				warn.setCountry(country);
				warn.setId(id);
				warn.setClaimCode(claimCode);
				temp.add(warn);
			}
		}
		return map;
	}
	
	
	public Map<String,Set<String>> findFreePromotionsByCountry() {
		Map<String,Set<String>> map=Maps.newHashMap();
		DetachedCriteria dc = amazonPromotionsWarningDao.createDetachedCriteria();
		dc.add(Restrictions.eq("isActive","0"));
		dc.add(Restrictions.like("promotionId","Free",MatchMode.START));
		dc.add(Restrictions.ge("updateDate",DateUtils.addDays(new Date(),-7)));
		List<AmazonPromotionsWarning> list =amazonPromotionsWarningDao.find(dc);
		for(AmazonPromotionsWarning warn:list){
			Hibernate.initialize(warn.getItems());
		}
		for (AmazonPromotionsWarning amazonPromotionsWarning : list) {
			
			StringBuffer buf= new StringBuffer(amazonPromotionsWarning.getPromotionId());
			Set<String> temp=map.get(amazonPromotionsWarning.getCountry());
			if(temp==null){
				temp=Sets.newHashSet();
				map.put(amazonPromotionsWarning.getCountry(), temp);
			}
			if(amazonPromotionsWarning.getItems()!=null&&amazonPromotionsWarning.getItems().size()>0){
				for(AmazonPromotionsWarningItem item:amazonPromotionsWarning.getItems()){
					if("0".equals(item.getDelFlag())&&StringUtils.isNotBlank(item.getProductNameColor())&&!buf.toString().contains(item.getProductNameColor())){
						buf.append(";"+item.getProductNameColor());
					}
				}
			}
			temp.add(buf.toString());
		}
		return map;
	}
	
	
	public Map<String,Map<String,String>> findPromotionsCode(){
		Map<String,Map<String,String>> map=Maps.newHashMap();
		String sql="SELECT w.`country`,w.`promotion_id`,w.`promotion_code` FROM amazoninfo_promotions_warning w WHERE w.`promotion_code` IS NOT NULL and w.update_date>=:p1 ";
		List<Object[]> list=amazonPromotionsWarningDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(),-7)));
		for (Object[] obj: list) {
			Map<String,String> countryMap=map.get(obj[0].toString());
			if(countryMap==null){
				countryMap=Maps.newHashMap();
				map.put(obj[0].toString(), countryMap);
			}
			countryMap.put(obj[1].toString(), obj[2].toString());
		}
		return map;
	}
	
	public Map<String,Map<String,String>> findPromotionsCode2(){
		Map<String,Map<String,String>> map=Maps.newHashMap();
		String sql="SELECT w.`country`,w.`promotion_id`,w.`promotion_code` FROM amazoninfo_promotions_warning w WHERE w.`promotion_code` IS NOT NULL and w.is_active='0' ";
		List<Object[]> list=amazonPromotionsWarningDao.findBySql(sql);
		for (Object[] obj: list) {
			Map<String,String> countryMap=map.get(obj[0].toString());
			if(countryMap==null){
				countryMap=Maps.newHashMap();
				map.put(obj[0].toString(), countryMap);
			}
			countryMap.put(obj[1].toString(), obj[2].toString());
		}
		return map;
	}
	
	public Map<String,String> getPromotionsCodeInventory(String country,Integer num){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT p.`promotions_id`,p.`promotions_code` FROM amazoninfo_sys_promotions_inventory p WHERE country=:p1 and is_active='0' and p.`promotions_code` is not null order by create_date asc limit :p2 ";
		List<Object[]> list=amazonSysPromotionsInventoryDao.findBySql(sql, new Parameter(country,num));
		for (Object[] obj: list) {
			map.put(obj[0].toString(),obj[1].toString());
		}
		return map;
	}
	
	public Map<String,Object[]> getPromotionsCodeInventoryDetail(String country,Integer num){
		Map<String,Object[]> map=Maps.newHashMap();
		String sql="SELECT p.`promotions_id`,p.`promotions_code`,p.create_date FROM amazoninfo_sys_promotions_inventory p WHERE country=:p1 and is_active='0' and p.`promotions_code` is not null order by create_date asc limit :p2 ";
		List<Object[]> list=amazonSysPromotionsInventoryDao.findBySql(sql, new Parameter(country,num));
		for (Object[] obj: list) {
			map.put(obj[0].toString(),obj);
		}
		return map;
	}
	
	@Transactional(readOnly = false)
	public void updateCodeIsActive(String country,Set<String> promotionsId){
		String sql="update amazoninfo_sys_promotions_inventory set is_active='1',use_date=now() where country=:p1 and promotions_id in (:p2) ";
		amazonSysPromotionsInventoryDao.updateBySql(sql, new Parameter(country,promotionsId));
	}
	
	
	@Transactional(readOnly = false)
	public void updateSpecialFlag(String country,Set<String> promotionsId){
		String sql="update amazoninfo_promotions_warning set check_rate='1' where country=:p1 and promotion_id in (:p2) ";
		amazonSysPromotionsInventoryDao.updateBySql(sql, new Parameter(country,promotionsId));
	}
	
	@Transactional(readOnly = false)
	public void updateSpecialNoFlag(String country,Set<String> promotionsId){
		String sql="update amazoninfo_promotions_warning set check_rate=NULL where country=:p1 and promotion_id in (:p2) ";
		amazonSysPromotionsInventoryDao.updateBySql(sql, new Parameter(country,promotionsId));
	}
	
	
	@Transactional(readOnly = false)
	public void updateCodeIsActive(String country){
		String sql="update amazoninfo_sys_promotions_inventory set is_active='3' where country=:p1 and is_active in ('0','2') and create_date<=:p2 ";
		amazonSysPromotionsInventoryDao.updateBySql(sql, new Parameter(country,DateUtils.addMonths(new Date(),-3)));
	}
	
	public Integer getUnUsePromotionsCode(String country){
		String sql="SELECT count(1) FROM amazoninfo_sys_promotions_inventory p WHERE country=:p1 and is_active='0' ";
		List<Object> list=amazonSysPromotionsInventoryDao.findBySql(sql, new Parameter(country));
		if(list!=null&&list.size()>0&&list.get(0)!=null){
			return ((BigInteger)list.get(0)).intValue();
		}
		return 0;
	}
	
	@Transactional(readOnly = false)
	public void saveCodeInventoryList(List<AmazonSysPromotionsInventory> smazonSysPromotionsInventorys){
		amazonSysPromotionsInventoryDao.save(smazonSysPromotionsInventorys);
	}
	
	public Map<String,Set<String>> getInitPromotionsCode(){
		Map<String,Set<String>> map=Maps.newHashMap();
		String sql="select country,promotions_id from amazoninfo_sys_promotions_inventory where is_active='2' ";
		List<Object[]> list=amazonSysPromotionsInventoryDao.findBySql(sql);
		for (Object[] obj: list) {
			Set<String> set=map.get(obj[0].toString());
			if(set==null){
				set=Sets.newHashSet();
				map.put(obj[0].toString(), set);
			}
			set.add(obj[1].toString());
		}
		return map;
	}
	
	@Transactional(readOnly = false)
	public void updateProCode(Map<String,String> codeMap,String country){
		String sql="update amazoninfo_sys_promotions_inventory set is_active='0',promotions_code=:p1 where promotions_id=:p2 and  country=:p3 ";
		for (Map.Entry<String,String> entry : codeMap.entrySet()) {  
		    String trackId=entry.getKey();
			String code=entry.getValue();
			if(StringUtils.isNotBlank(code)){
				amazonSysPromotionsInventoryDao.updateBySql(sql, new Parameter(code,trackId,country));
			}
		}
	}
	
	public Map<String,Set<String>> getInitPromotionsCodeByErp(){
		Map<String,Set<String>> map=Maps.newHashMap();
		String sql="select country,promotions_id from amazoninfo_sys_promotions_item where promotions_code is null ";
		List<Object[]> list=amazonSysPromotionsInventoryDao.findBySql(sql);
		for (Object[] obj: list) {
			Set<String> set=map.get(obj[0].toString());
			if(set==null){
				set=Sets.newHashSet();
				map.put(obj[0].toString(), set);
			}
			set.add(obj[1].toString());
		}
		return map;
	}
	
	@Transactional(readOnly = false)
	public void updateProCodeByErp(Map<String,String> codeMap,String country){
		String sql="update amazoninfo_sys_promotions_item set promotions_code=:p1 where promotions_id=:p2 and  country=:p3 ";
		for (Map.Entry<String,String> entry : codeMap.entrySet()) {  
		    String trackId=entry.getKey();
			String code=entry.getValue();
			if(StringUtils.isNotBlank(code)){
				amazonSysPromotionsDao.updateBySql(sql, new Parameter(code,trackId,country));
			}
		}
	}
	
	//获得最新100个code
	public Map<Integer,String>  getAtcivePromotions(String country){
		Map<Integer,String> proMap = Maps.newHashMap();
		String sql="SELECT a.`id`,a.`promotion_id`,a.`promotion_code` FROM amazoninfo_promotions_warning AS a WHERE a.`promotion_code` IS NOT NULL AND a.`country`=:p1  ORDER BY  a.id DESC LIMIT 100 ";
		List<Object[]> list = this.amazonPromotionsWarningDao.findBySql(sql,new Parameter(country));
		for(Object[] obj:list){
			proMap.put(Integer.parseInt(obj[0].toString()), obj[2]+"["+obj[1]+"]");
		}
		return proMap;
	}
	
	public Map<String,Set<String>> findEntireCatalogByCountry(){
		Map<String,Set<String>> map=Maps.newHashMap();
		String sql="SELECT w.`country`,w.`promotion_id` FROM amazoninfo_promotions_warning  w "+
         " WHERE w.`is_active`='0' AND w.`purchased_items` IN ('Entire catalog','Entire catalogue')";
		List<Object[]> list = this.amazonPromotionsWarningDao.findBySql(sql);
		for (Object[] obj: list) {
			Set<String> proSet=map.get(obj[0].toString());
			if(proSet==null){
				proSet=Sets.newHashSet();
				map.put(obj[0].toString(), proSet);
			}
			proSet.add(obj[1].toString());
		}
		return map;
	}
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		AmazonPromotionsWarningService  service= applicationContext.getBean(AmazonPromotionsWarningService.class);
	    System.out.println(service.promotionPrice("com","B00VWLN6WU","F-KPL OFF 44% TO 8.9 11.25-12.31",14.99f));
		applicationContext.close();
	}
	
	
	
	public Float promotionPrice(String country,String asin,String promotionsId,Float salePrice){
		try{

			if(StringUtils.isBlank(asin)){
				LOGGER.info(country+"="+promotionsId+"asin为空");
				return null;
			}
			if(salePrice==null){
				LOGGER.info(country+"="+promotionsId+"="+asin+"salePrice为空");
				return null;
			}
			Set<String> proSet=Sets.newHashSet();
			for (String proId: promotionsId.split(",")) {
				if(proId.startsWith("F-")||proId.startsWith("C-")||proId.startsWith("R-")){
					proSet.add(proId);
				}
			}
			if(proSet.size()==0){
				return 0f;
			}
			
			String sql="SELECT w.`promotion_id`,w.`buyer_purchases`,w.`buyer_gets`,w.`purchased_items`,GROUP_CONCAT(CONCAT(t.asin,'_',(CASE WHEN t.`is_main`='0' THEN '0' ELSE '1' END))) ASIN "+
				"	 FROM amazoninfo_promotions_warning w JOIN amazoninfo_promotions_warning_item t ON w.id=t.`warning_id`  "+
				"	 WHERE w.`country`=:p1  and w.`promotion_id` in :p2 and t.product_name_color is not null and w.`buyer_gets` is not null GROUP BY w.`promotion_id`,w.`buyer_purchases`,w.`buyer_gets`,w.`purchased_items` ";
			List<Object[]> list = this.amazonPromotionsWarningDao.findBySql(sql,new Parameter(country,proSet));
			Float promotionsPrice=null; 
			if(list!=null&&list.size()>0){
				promotionsPrice = 0f;
				for (Object[] obj: list) {
					String proId=obj[0].toString();
					proSet.remove(proId);
					String buyerPurchase=obj[1].toString();
					String buyerGets=obj[2].toString();
					String purchaseItems=obj[3].toString();
					String asinGroup=obj[4].toString();
					Float proBuyerGets=Float.parseFloat(buyerGets.substring(buyerGets.lastIndexOf(" ")).trim().replace(",", "."));
					Float proBuyerPurchase=Float.parseFloat(buyerPurchase.substring(buyerPurchase.lastIndexOf(" ")).trim().replace(",", "."));
					if("Entire catalogue".equals(purchaseItems)||"Entire catalog".equals(purchaseItems)){//所有asin
						if(buyerGets.contains("Amount off")){
							if(buyerPurchase.contains("At least this quantity of items")){
								promotionsPrice+=proBuyerGets/proBuyerPurchase;
							}else{
								promotionsPrice+=proBuyerGets;
							}
						}else{
							promotionsPrice+=salePrice*proBuyerGets/100;
						}
					}else{
						if((asinGroup.contains("_0")&&asinGroup.contains(asin+"_0"))||(!asinGroup.contains("_0")&&asinGroup.contains(asin+"_1"))){
							if(buyerGets.contains("Amount off")){
								if(buyerPurchase.contains("At least this quantity of items")){
									promotionsPrice+=proBuyerGets/proBuyerPurchase;
								}else{
									promotionsPrice+=proBuyerGets;
								}
							}else{
									promotionsPrice+=salePrice*proBuyerGets/100;
							}
						}
					}
				}
				
			 }
				//避免只要包邮查询不到都补刀
				if(proSet.size()>0){
					String sql2="SELECT distinct t.`promotion_ids`,t.`promotion_discount` FROM amazoninfo_order r JOIN amazoninfo_orderitem t ON r.`id`=t.`order_id` "+
	                       " WHERE r.`order_status`='Shipped'  AND t.`asin`=:p1  AND t.`item_price`=:p2 AND t.`promotion_ids` in :p3 and t.`promotion_discount`>0 and t.`promotion_ids` not like '%Core Free Shipping%' and t.`promotion_ids` not like 'Free Delivery%' ";
					List<Object[]> proList= this.amazonPromotionsWarningDao.findBySql(sql2,new Parameter(asin,salePrice,proSet));
					if(proList!=null&&proList.size()>0){
						Set<String> countProSet=Sets.newHashSet();
						if(promotionsPrice==null){
							promotionsPrice = 0f;
						}
						for (Object[] obj: proList) {
							String proId=obj[0].toString();
							Float discount=Float.parseFloat(obj[1]==null?"0":obj[1].toString());
		                    if(countProSet.contains(proId)){
								continue;
							}
		                    promotionsPrice+=discount;
							countProSet.add(proId);
						}
					}
					return promotionsPrice>salePrice?salePrice:promotionsPrice;
				}
			if(promotionsPrice==null){
				LOGGER.info(country+"="+promotionsId+"="+asin+"promotion price为空");
			}
			return promotionsPrice;
		}catch(Exception e){
			return null;
		}
	}
	
	//country+"_"+proId+"_"+asin
	public Map<String,Float> findProPrice(Map<String,Float> priceMap){//priceMap=amazonProduct2Service.findAllProductPrice
		 Map<String,Float>  map=Maps.newHashMap();
		 String sql="SELECT w.`promotion_id`,w.`buyer_purchases`,w.`buyer_gets`,w.`purchased_items`,GROUP_CONCAT(CONCAT(t.asin,'_',(CASE WHEN t.`is_main`='0' THEN '0' ELSE '1' END))) ASIN,w.country "+
					" FROM amazoninfo_promotions_warning w JOIN amazoninfo_promotions_warning_item t ON w.id=t.`warning_id`  "+
					" WHERE t.product_name_color is not null and w.`buyer_gets` is not null GROUP BY w.country,w.`promotion_id`,w.`buyer_purchases`,w.`buyer_gets`,w.`purchased_items` ";
		 List<Object[]> list = this.amazonPromotionsWarningDao.findBySql(sql);
		 if(list!=null&&list.size()>0){
				for (Object[] obj: list) {
					String proId=obj[0].toString();
					String buyerPurchase=obj[1].toString();
					String buyerGets=obj[2].toString();
					String purchaseItems=obj[3].toString();
					String asinGroup=obj[4].toString();
					String  country=obj[5].toString();
					Float proBuyerGets=Float.parseFloat(buyerGets.substring(buyerGets.lastIndexOf(" ")).trim().replace(",", "."));
					Float proBuyerPurchase=Float.parseFloat(buyerPurchase.substring(buyerPurchase.lastIndexOf(" ")).trim().replace(",", "."));
					
					String[] asinArr=asinGroup.replaceAll("_1", "").replaceAll("_0", "").split(",");
					for (String asin: asinArr) {
						if(priceMap.get(country+"_"+asin)!=null){
							Float salePrice=priceMap.get(country+"_"+asin);
							Float promotionsPrice=0f;
							if("Entire catalogue".equals(purchaseItems)||"Entire catalog".equals(purchaseItems)){//所有asin
								if(buyerGets.contains("Amount off")&&buyerPurchase.contains("At least this quantity of items")){
									promotionsPrice=proBuyerGets/proBuyerPurchase;
								}else if(buyerGets.contains("Amount off")&&buyerPurchase.contains("At least amount")){
									promotionsPrice=salePrice*(proBuyerGets/proBuyerPurchase);
								}else{//Percent off
									promotionsPrice=salePrice*proBuyerGets/100;
								}
							}else{
								if((asinGroup.contains("_0")&&asinGroup.contains(asin+"_0"))||(!asinGroup.contains("_0")&&asinGroup.contains(asin+"_1"))){
										if(buyerGets.contains("Amount off")&&buyerPurchase.contains("At least this quantity of items")){
											 promotionsPrice=proBuyerGets/proBuyerPurchase;
										}else if(buyerGets.contains("Amount off")&&buyerPurchase.contains("At least amount")){
											 promotionsPrice=salePrice*(proBuyerGets/proBuyerPurchase);
										}else{
											promotionsPrice=salePrice*proBuyerGets/100;
										}
								}
							}
							map.put(country+"_"+proId+"_"+asin, promotionsPrice>salePrice?salePrice:promotionsPrice);
						}
					}
				}
		 }		
		 return map;
	}
	
	public Map<String,List<String>> findPromotions(List<String> country,String productName){
		Map<String,List<String>> map=Maps.newHashMap(); 
		String sql="SELECT w.`country`,CONCAT(w.`promotion_id`,',',w.`buyer_gets`) FROM amazoninfo_promotions_warning w JOIN amazoninfo_promotions_warning_item t ON w.id=t.`warning_id` "+
                " WHERE w.`country` IN :p1 AND t.`product_name_color`=:p2 AND w.`is_active`='0'";
		List<Object[]> list = this.amazonPromotionsWarningDao.findBySql(sql,new Parameter(country,productName));
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
			    String proCountry=obj[0].toString();
			    String proId=obj[1].toString();
			    List<String> temp=map.get(proCountry);
			    if(temp==null){
			    	temp=Lists.newArrayList();
			    	map.put(proCountry, temp);
			    }
			    temp.add(proId);
			}
		}		
		return map;
	}
	
	public List<AmazonLightningDeals> findLightningDeal(String country,String internalDesc) {
		DetachedCriteria dc = amazonLightningDealsDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country",country));
		dc.add(Restrictions.eq("internalDesc",internalDesc));
		return amazonLightningDealsDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void saveDeal(List<AmazonLightningDeals> amazonLightningDeals) {
		/*for (AmazonLightningDeals deal: amazonLightningDeals) {
			if(deal.getId()!=null&&deal.getId()>0){
				amazonLightningDealsDao.getSession().merge(deal); 
			}else{
				amazonLightningDealsDao.save(deal);
			}
		}*/
		amazonLightningDealsDao.save(amazonLightningDeals);
	}
	
	
	public Map<String,Map<String,AmazonLightningDeals>> findLightningDeal(AmazonLightningDeals amazonLightningDeals,String[] productsName) {
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Map<String,Map<String,AmazonLightningDeals>> map=Maps.newLinkedHashMap();
		DetachedCriteria dc = amazonLightningDealsDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country",amazonLightningDeals.getCountry()));
		dc.add(Restrictions.eq("status","Ended"));
		dc.add(Restrictions.in("productName",productsName));
		if(amazonLightningDeals.getStart()!=null){
			dc.add(Restrictions.ge("start",amazonLightningDeals.getStart()));
		}
		if(amazonLightningDeals.getEnd()!=null){
			dc.add(Restrictions.le("end",amazonLightningDeals.getEnd()));
		}
		dc.addOrder(Order.desc("start"));
		List<AmazonLightningDeals> list=amazonLightningDealsDao.find(dc);
		if(list!=null&&list.size()>0){
			for (AmazonLightningDeals deal: list) {
				Map<String,AmazonLightningDeals> temp=map.get(deal.getProductName());
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					map.put(deal.getProductName(),temp);
				}
				String key=dateFormat.format(deal.getStart())+"---"+dateFormat.format(deal.getEnd());
				temp.put(key,deal);
			}
		}
		return map;
	}
	
	public Integer findDealQuantity(String name,Date start,Date end,String country){
		String sql="SELECT sum(quantity) FROM amazoninfo_promotions_report r WHERE r.`product_name`=:p1 and r.promotion_ids='闪购' and country=:p2 AND r.`purchase_date`>=:p3 and r.`purchase_date`<=:p4  ";
		List<Object> list=amazonLightningDealsDao.findBySql(sql,new Parameter(name,country,start,end));
		if(list!=null&&list.size()>0&&list.get(0)!=null){
			return Integer.parseInt(list.get(0).toString());
		}
		return 0;
	}
	
	
	public List<String> findAllProductName(String country,Date start,Date end){
		String sql = "SELECT DISTINCT product_name FROM amazoninfo_lightning_deals  WHERE country=:p1 and start>=:p2 and end<:p3 ";
		List<String> list = amazonLightningDealsDao.findBySql(sql,new Parameter(country,start,DateUtils.addDays(end, 1)));
		return  list;
	}
	
	public List<String> findAllProductName(){
		String sql = "SELECT DISTINCT product_name FROM amazoninfo_lightning_deals";
		List<String> list = amazonLightningDealsDao.findBySql(sql);
		return  list;
	}
	
	public Map<String,List<AmazonLightningDeals>> findLightningDealList(AmazonLightningDeals amazonLightningDeals) {
		Map<String,List<AmazonLightningDeals>> map=Maps.newLinkedHashMap();
		DetachedCriteria dc = amazonLightningDealsDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country",amazonLightningDeals.getCountry()));
		if(StringUtils.isNotBlank(amazonLightningDeals.getStatus())){
			dc.add(Restrictions.eq("status",amazonLightningDeals.getStatus()));
		}
		if(amazonLightningDeals.getStart()!=null){
			dc.add(Restrictions.ge("start",amazonLightningDeals.getStart()));
		}
		if(amazonLightningDeals.getEnd()!=null){
			dc.add(Restrictions.le("end",amazonLightningDeals.getEnd()));
		}
		if(StringUtils.isNotBlank(amazonLightningDeals.getProductName())){
			List<String> nameList=psiTypeGroupService.getProductNameByLineId(amazonLightningDeals.getProductName());
			if(nameList==null||nameList.size()==0){
				return map;
			}
			dc.add(Restrictions.in("productName",nameList));
		}
		if(!UserUtils.hasPermission("amazoninfo:productSalePrice:all")){
			List<String> temp = psiProductGroupUserService.getProductByGroupUser();
			Set<String> productSet=Sets.newHashSet();
			for (String product: temp) {
				if(product.endsWith("_"+amazonLightningDeals.getCountry())){
					productSet.add(product.substring(0,product.lastIndexOf("_")));
				}
			}
			if(productSet==null||productSet.size()==0){
				return map;
			}
			dc.add(Restrictions.in("productName",productSet));
		}
		dc.addOrder(Order.desc("end"));
		List<AmazonLightningDeals> list=amazonLightningDealsDao.find(dc);
		if(list!=null&&list.size()>0){
			for (AmazonLightningDeals deal: list) {
				List<AmazonLightningDeals> temp=map.get(deal.getInternalDesc()+"-"+deal.getStart()+"-"+deal.getEnd());
				if(temp==null){
					temp=Lists.newArrayList();
					map.put(deal.getInternalDesc()+"-"+deal.getStart()+"-"+deal.getEnd(),temp);
				}
				temp.add(deal);
			}
		}
		return map;
	}
	
	
	@Transactional(readOnly = false)
	public void updateAvgDealFee(){
		String sql="SELECT country,d.`internal_desc`,d.`deal_fee`/count(*),COUNT(*) num FROM amazoninfo_lightning_deals d WHERE d.`deal_fee`>0 "+
                   " GROUP BY country,d.`internal_desc`,d.`deal_fee` HAVING num>1 ";
		List<Object[]> list=amazonLightningDealsDao.findBySql(sql);
		String updateSql="update amazoninfo_lightning_deals set deal_fee=:p1  where country=:p2 and internal_desc=:p3";
		for (Object[] obj : list) {
			String country=obj[0].toString();
			String internalDesc=obj[1].toString();
			Float fee=Float.parseFloat(obj[2].toString());
			amazonLightningDealsDao.updateBySql(updateSql, new Parameter(fee,country,internalDesc));
		}
	}
	
	/**
	 * 
	 * @param date	日期字符串（格式：yyyyMMdd）
	 * @param rateMap	日期当天对应的汇率
	 * @return Map<productName, Map<accountName, AmazonLightningDeals>> (AmazonLightningDeals 含销量、促销费用、盈亏,货币单位：欧元)
	 */
	public Map<String, Map<String, AmazonLightningDeals>> findDealDetailByDay(String date, Map<String, Float> rateMap){//*d.`actual_quantity`
		Map<String, Map<String, AmazonLightningDeals>> map=Maps.newHashMap();
		String sql="SELECT d.`country`,d.`product_name`,sum(d.`actual_quantity`),sum(d.`deal_fee`*(case when d.country='com' then :p1 when d.country='ca' then :p2 when d.country='jp' then :p3 when d.country='mx' then :p4 when d.country='uk' then :p5 else 1 end))"+
		           " ,sum((d.`deal_price`*(case when d.country='com' then :p1 when d.country='ca' then :p2 when d.country='jp' then :p3 when d.country='mx' then :p4 when d.country='uk' then :p5 else 1 end)-d.`safe_price`)*d.`actual_quantity`-d.`deal_fee`*(case when d.country='com' then :p1 when d.country='ca' then :p2 when d.country='jp' then :p3 when d.country='mx' then :p4 when d.country='uk' then :p5 else 1 end) ),d.`account_name` "+
                   " FROM amazoninfo_lightning_deals d WHERE DATE_FORMAT(d.`start`,'%Y%m%d')=:p6 and d.`product_name` is not null  group by d.`country`,d.`account_name`,d.`product_name` ";
		List<Object[]> list=amazonLightningDealsDao.findBySql(sql,new Parameter(MathUtils.getRate("USD", "EUR", rateMap),MathUtils.getRate("CAD", "EUR", rateMap),
				MathUtils.getRate("JPY", "EUR", rateMap),MathUtils.getRate("MXN", "EUR", rateMap),MathUtils.getRate("GBP", "EUR", rateMap),date));
		for (Object[] obj: list) {
			String country=obj[0].toString();
			String name=obj[1].toString();
			Integer quantity=Integer.parseInt(obj[2].toString());
			Float dealFee=Float.parseFloat(obj[3].toString());
			Float profit=obj[4]==null?0:Float.parseFloat(obj[4].toString());
			String accountName=obj[5]==null?SaleProfitService.accountMap.get(country):obj[5].toString();
			Map<String, AmazonLightningDeals> temp=map.get(name);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(name, temp);
			}
			AmazonLightningDeals deals=new AmazonLightningDeals();
			deals.setCountry(country);
			deals.setAccountName(accountName);
			deals.setActualQuantity(quantity);
			deals.setDealFee(dealFee>0?-dealFee:dealFee);
			deals.setConv1(profit);
			temp.put(accountName, deals);
		}
		return map;
	}
	
	public Map<String,Set<String>> findPromtions(String country){
		 Map<String,Set<String>> map=Maps.newHashMap();
		 String sql="SELECT a.`promotion_id`,a.`buyer_gets`,t.`product_name_color` FROM  amazoninfo_promotions_warning a "+
		   " JOIN amazoninfo_promotions_warning_item t ON a.id=t.`warning_id` "+
		   " WHERE a.`warning_sta`='1' AND a.`country`=:p1 and t.`product_name_color` is not null and a.`buyer_gets` is not null ";
		 
		 List<Object[]> list=amazonLightningDealsDao.findBySql(sql,new Parameter(country));
		 for (Object[] obj: list) {
			  String proId=obj[0].toString();
			  String buyerGets=obj[1].toString();
			  String name=obj[2].toString();
			  Set<String> temp=map.get(name);
			  if(temp==null){
				  temp=Sets.newHashSet();
				  map.put(name, temp);
			  }
			  temp.add(proId+","+buyerGets);
		 }
		 return map;
	}

}
