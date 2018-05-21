package com.springrain.erp.modules.psi.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.cxf.endpoint.Client;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
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
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.excel.ExcelUtil;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.entity.SessionMonitor;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.psi.dao.PsiBarcodeDao;
import com.springrain.erp.modules.psi.dao.PsiProductDao;
import com.springrain.erp.modules.psi.dao.PsiProductHsCodeDetailDao;
import com.springrain.erp.modules.psi.dao.PsiProductTieredPriceDao;
import com.springrain.erp.modules.psi.dao.PsiSkuDao;
import com.springrain.erp.modules.psi.dao.PsiSupplierDao;
import com.springrain.erp.modules.psi.dao.PurchasePlanDao;
import com.springrain.erp.modules.psi.dao.parts.PsiPartsDao;
import com.springrain.erp.modules.psi.entity.ProductSupplier;
import com.springrain.erp.modules.psi.entity.PsiAttrDto;
import com.springrain.erp.modules.psi.entity.PsiBarcode;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductAttribute;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.entity.PsiProductHsCodeDetail;
import com.springrain.erp.modules.psi.entity.PsiProductTieredPrice;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PurchasePlan;
import com.springrain.erp.modules.psi.entity.PurchasePlanItem;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 *	供应商产品Service
 */
@Component
@Transactional(readOnly = true)
public class PsiProductService extends BaseService {
	@Autowired
	private 	PurchasePlanDao 				planDao;
	@Autowired
	private 	PsiProductDao 					psiProductDao;
	@Autowired
	private 	PsiPartsDao 					psiPartsDao;
	@Autowired
	private 	PsiSupplierDao 					supplierDao;
	@Autowired
	private 	PsiBarcodeDao 					barcodeDao;
	@Autowired
	private 	PsiSkuDao 						psiSkuDao;
	@Autowired
	private 	PsiProductPartsService  		productPartsService;
	@Autowired
	private 	PsiProductAttributeService  	psiProductAttributeService;
	@Autowired
	private 	PsiProductTieredPriceDao   		productTieredDao;
	@Autowired
	private 	PsiProductTieredPriceService    productTieredService;
	@Autowired
	private 	PsiProductEliminateService 		productEliminateService;
	@Autowired
	private 	MailManager              		mailManager;
	@Autowired
	private 	PsiProductHsCodeDetailDao 		psiProductHsCodeDetailDao;
	@Autowired
	private 	SaleReportService				saleReportService;
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	private final static Logger logger = LoggerFactory.getLogger(PsiProductService.class);
	
	private static final Integer [] priceTiereds={500,1000,2000,3000,5000,10000,15000};
	private static final String []  currencyType={"CNY","USD"};
	private static Map<String, String>  forecastMap= Maps.newHashMap();
	static{
		forecastMap.put("Express card", "3");
		forecastMap.put("Kindle cover", "3");
		forecastMap.put("Speaker", "3");
		forecastMap.put("Tablet PC bag", "3");
		forecastMap.put("HDD Adapter", "2");
		forecastMap.put("Keyboard", "2");
		forecastMap.put("Scanner", "2");
		forecastMap.put("Bluetooth Adaptor", "1");
		forecastMap.put("Cable", "1");
		forecastMap.put("Docking station", "1");
		forecastMap.put("HDD case", "1");
		forecastMap.put("HDD enclosures", "1");
		forecastMap.put("Hub", "1");
		forecastMap.put("Wireless presenter", "1");
	}
	
	//带old和other的
	public List<PsiProduct> find() {
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		dc.addOrder(Order.desc("id"));
		dc.add(Restrictions.eq("delFlag", "0"));
		List<PsiProduct> tempList=psiProductDao.find(dc);
		return tempList;
	}  
	
	/**
	 * 带old和other的
	 * @param isNew 新上架产品筛选标记,(上架时间不为空)
	 * @return
	 */
	public List<PsiProduct> findForList(String isNewAdd) {
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(isNewAdd) && "1".equals(isNewAdd)) {
			dc.add(Restrictions.isNotNull("addedMonth"));
			dc.addOrder(Order.desc("addedMonth"));
		} else {
			dc.addOrder(Order.desc("id"));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		List<PsiProduct> tempList=psiProductDao.find(dc);
		return tempList;
	} 
	
	
	public Map<String,PsiProduct> getProductMap() {
		Map<String,PsiProduct> rs = Maps.newHashMap();
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		List<PsiProduct> tempList=psiProductDao.find(dc);
		for(PsiProduct pro :tempList){
			rs.put(pro.getName(), pro);
		}
		return rs;
	} 
	
	public Map<String,String> getModelAndSupplierMap(){
		String sql = "SELECT  CONCAT(a.`brand`,' ',a.`model`) , GROUP_CONCAT(c.`nikename`) FROM psi_product a ,psi_product_supplier b ,psi_supplier c  WHERE a.`id` = b.`product_id` AND b.`supplier_id` = c.`id` AND a.`del_flag` = '0' GROUP BY a.`model`,a.`brand`";
		List<Object[]> list = this.psiProductDao.findBySql(sql);
		Map<String,String> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(), objects[1].toString());
		}
		return rs;
	}
	
	
	/**
	 * 查出预留库存值
	 * 
	 */
	public Integer getResidueById(Integer productId){
		String sql ="select CASE  WHEN a.`pack_quantity`<=30 THEN a.`pack_quantity`*3 WHEN 30<=a.`pack_quantity`AND a.`pack_quantity`<100 THEN a.`pack_quantity`*2  ELSE a.`pack_quantity` END AS aa FROM psi_product AS a  WHERE a.id=:p1 ";
		List<BigInteger> list = this.psiProductDao.findBySql(sql,new Parameter(productId));
		return list.get(0).intValue();
	}
	
	/**
	 * 查出sku对应的预留库存值
	 * 
	 */
	public Map<String,Object[]> getResidueMap(){
		 Map<String,Object[]> residueMap = Maps.newHashMap();
		String sql ="SELECT CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END ,'_',b.`country`) AS productName,b.sku,CASE  WHEN a.`pack_quantity`<=30 THEN a.`pack_quantity`*3 WHEN 30<=a.`pack_quantity`AND a.`pack_quantity`<100 THEN a.`pack_quantity`*2  ELSE a.`pack_quantity` END AS aa FROM psi_product AS a,psi_sku AS b  WHERE a.id=b.`product_id` AND b.`country`='de' AND b.`use_barcode`='1'";
		List<Object[]> list = this.psiProductDao.findBySql(sql);
		for(Object[] objs:list){
			residueMap.put(objs[0].toString(), objs);
		}
		return residueMap;
	}
	
	public Map<String,Integer> getResidueMap(Set<String> skus){
		 Map<String,Integer> residueMap = Maps.newHashMap();
		String sql ="SELECT b.sku,CASE  WHEN a.`pack_quantity`<=30 THEN a.`pack_quantity`*3 WHEN 30<=a.`pack_quantity`AND a.`pack_quantity`<100 THEN a.`pack_quantity`*2  ELSE a.`pack_quantity` END AS aa FROM psi_product AS a,psi_sku AS b  WHERE a.id=b.`product_id` AND b.`country`='de' AND b.del_flag='0' AND a.del_flag='0' and b.sku in :p1";
		List<Object[]> list = this.psiProductDao.findBySql(sql,new Parameter(skus));
		for(Object[] objs:list){
			residueMap.put(objs[0].toString(), Integer.parseInt(objs[1].toString()));
		}
		return residueMap;   
	}
	
	public List<PsiProduct> findAllOnSale(){
		//产品淘汰分平台、颜色
		List<Integer> saleIdList = productEliminateService.findOnSaleList(null);
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		//dc.add(Restrictions.eq("isSale","1"));
		if (saleIdList != null && saleIdList.size() > 0) {
			dc.add(Restrictions.in("id",saleIdList));
		} else {
			return Lists.newArrayList();
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.desc("createTime"));
		return psiProductDao.find(dc);
	}
	
	
	public List<PsiProduct> findAllOnSaleNotNew(String country,String productName){
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		dc.add(Restrictions.like("platform","%"+country+"%"));
		if(StringUtils.isNotBlank(productName)){
			dc.add(Restrictions.like("model","%"+productName+"%"));
		}
		dc.add(Restrictions.eq("isSale","1"));
		dc.add(Restrictions.or(Restrictions.eq("isNew","0"),Restrictions.isNotNull("addedMonth")));
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiProductDao.find(dc);
	}
	
	public Page<PsiProduct> findAllOnSaleNotNewByPage(Page<PsiProduct> page ,String country,String productName){
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		dc.add(Restrictions.like("platform","%"+country+"%"));
		if(StringUtils.isNotBlank(productName)){
			dc.add(Restrictions.like("model","%"+productName+"%"));
		}
		dc.add(Restrictions.eq("isSale","1"));
		dc.add(Restrictions.or(Restrictions.eq("isNew","0"),Restrictions.isNotNull("addedMonth")));
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiProductDao.find(page,dc);
	}
	
   public Page<PsiProduct> findAllProduct(Page<PsiProduct> page ,String productName){
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		if(StringUtils.isNotBlank(productName)){
			dc.add(Restrictions.like("model","%"+productName+"%"));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiProductDao.find(page,dc);
	}
	
	public Page<Object[]> findOrderBySaleProduct(Page<Object[]> page,String productName){
//		String sql="SELECT a.name,a.platform,a.pId,SUM(b.day31_sales) volume   FROM ( "+
//          " SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,a.`platform`,a.id pId "+
//          " FROM psi_product a JOIN mysql.help_topic b "+
//          " ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) "+
//          " WHERE a.`is_sale`='1' AND a.`del_flag`='0' AND (a.`is_new`='0' OR a.`added_month` IS NOT NULL) "+
//          " ) a left JOIN  psi_product_variance b ON a.name=b.`product_name`  where 1=1 ";
		//产品淘汰分平台、颜色

		//查询非所有平台都为新品的产品
		List<String> nameList = productEliminateService.findForecastName();
		if (nameList == null || nameList.size() == 0) {
			return page;
		}
		String sql="SELECT a.name,a.platform,a.pId,SUM(b.day31_sales) volume  FROM ( "+
				" SELECT CASE WHEN e.`color`='' THEN e.`product_name` ELSE CONCAT(e.`product_name`,'_',e.`color`) END AS NAME ,  "+
				" GROUP_CONCAT(e.`country`) AS platform,e.product_id pId  "+
				" FROM `psi_product_eliminate` e  "+
				" WHERE e.`del_flag`='0' AND CASE WHEN e.`color`='' THEN e.`product_name` ELSE CONCAT(e.`product_name`,'_',e.`color`) END IN(:p1)  "+
				" GROUP BY e.`product_name`,e.color "+
				" ) a LEFT JOIN  psi_product_variance b ON a.name=b.`product_name` WHERE 1=1  ";
		if(StringUtils.isNotBlank(productName)){
			sql+=" and a.name like '%"+productName+"%'";
		}
		sql+=" GROUP BY a.name,a.platform,a.pId ORDER BY volume DESC ";
		return psiProductDao.findBySql2(page, sql,new Parameter(nameList),null);
	}
	
	public List<PsiProduct> findAll(){
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		dc.add(Restrictions.ne("model", "other"));
		dc.add(Restrictions.ne("model", "Old"));
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.desc("createTime"));
		return psiProductDao.find(dc);
	}
	
	public List<PsiProduct> findIsComponents(String components){
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		if(StringUtils.isNotBlank(components)){
			if("0".equals(components)){
				dc.add(Restrictions.or(Restrictions.isNull("components"),Restrictions.eq("components", "0")));
			}else if("1".equals(components)){
				dc.add(Restrictions.eq("components", "1"));
			}
		}
		dc.add(Restrictions.ne("model", "other"));
		dc.add(Restrictions.ne("model", "Old"));
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.desc("createTime"));
		return psiProductDao.find(dc);
	}
	
	public List<PsiProduct> findAll(String country){
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		dc.add(Restrictions.ne("model", "other"));
		dc.add(Restrictions.ne("model", "Old"));
		dc.add(Restrictions.eq("delFlag", "0"));
		if(StringUtils.isNotEmpty(country)){
			dc.add(Restrictions.like("platform", "%"+country+"%"));
		}
		dc.addOrder(Order.desc("createTime"));
		return psiProductDao.find(dc);
	}
	
	public Map<Integer,PsiProduct> findProductsMap(Set<Integer> productIds){
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		dc.add(Restrictions.ne("model", "other"));
		dc.add(Restrictions.ne("model", "Old"));
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.desc("createTime"));
		if(productIds!=null&&productIds.size()>0){
			dc.add(Restrictions.in("id", productIds));
		}
		List<PsiProduct> products= psiProductDao.find(dc);
		Map<Integer,PsiProduct>  tempMap = Maps.newHashMap();
		for(PsiProduct product:products){
			tempMap.put(product.getId(), product);
		}
		return tempMap;
	}
	
	public Page<PsiProduct> find(Page<PsiProduct> page,PsiProduct psiProduct) {
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(psiProduct.getModel())) {
			dc.add(Restrictions.like("model", "%" + psiProduct.getModel()+ "%"));
		}
		
		if (StringUtils.isNotEmpty(psiProduct.getType())) {
			if("other".equals(psiProduct.getType())){
				dc.add(Restrictions.eq("type", ""));
			}else{
				dc.add(Restrictions.eq("type", psiProduct.getType()));
			}
		}
		//barcode  模糊查询
		if(StringUtils.isNotEmpty(psiProduct.getBrand())){
			dc.createAlias("this.barcodes", "barcode");
			dc.add(Restrictions.like("barcode.barcode","%"+psiProduct.getBrand()+"%"));
		}
		
		dc.addOrder(Order.desc("id"));
		dc.add(Restrictions.eq("delFlag", "0"));
		
		
		return psiProductDao.find(page,dc);
	}
	
	public Page<PsiProduct> findWithSku(Page<PsiProduct> page, PsiProduct psiProduct) {
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(psiProduct.getModel())) {
			dc.add(Restrictions.like("model", "%" + psiProduct.getModel()+ "%"));
		}
		if (StringUtils.isNotEmpty(psiProduct.getType())) {
			if("other".equals(psiProduct.getType())){
				dc.add(Restrictions.eq("type", ""));
			}else{
				dc.add(Restrictions.eq("type", psiProduct.getType()));
			}
		}
		if(StringUtils.isEmpty(psiProduct.getPlatform())){
			psiProduct.setPlatform("de");
		}
		String country = psiProduct.getPlatform();
		if("ebay".equals(psiProduct.getPlatform())){
			country = "de";
		}else if("ebay_com".equals(psiProduct.getPlatform())|| psiProduct.getPlatform().startsWith("com")){
			country = "com";
		}
		if("1".equals(psiProduct.getDelFlag())){
			String sql = "SELECT DISTINCT(b.`psi_product`) FROM psi_barcode b WHERE  CONCAT(b.`psi_product`,',',b.`product_color`) NOT IN (SELECT CONCAT(a.`product_id`,',',a.`color`) FROM psi_sku a WHERE a.`country`=:p1 AND a.`del_flag`='0' ) AND b.`product_platform` =:p1 AND b.`del_flag`='0' ";
			List<Object> params = psiProductDao.findBySql(sql,new Parameter(psiProduct.getPlatform()));
			if(params.size()>0){
				dc.add(Restrictions.in("id",params));
			}
		}
		dc.add(Restrictions.like("platform", "%"+country+"%"));
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiProductDao.find(page, dc);
	}
	
	public PsiProduct findProductByName(String productName) {
		String sql ="SELECT DISTINCT a.`psi_product` FROM psi_barcode a  WHERE a.`del_flag` ='0' AND CONCAT(a.`product_name`,CASE  WHEN a.`product_color`='' THEN '' ELSE CONCAT('_',a.`product_color`) END) = :p1";
		List<Object> list =  psiProductDao.findBySql(sql, new Parameter(productName));
		if(list.size()==1){
			return psiProductDao.get((Integer)list.get(0));
		}
		return null;
	}
	
	public Map<String,List<PsiAttrDto>> findBarcodeAndSku(String productName){
		Map<String,List<PsiAttrDto>> map=Maps.newHashMap();
		String sql="SELECT s.`country`,b.`barcode_type`,b.`barcode`,GROUP_CONCAT(DISTINCT s.`asin`), "+
				" GROUP_CONCAT(d.`sku`),GROUP_CONCAT(IFNULL(d.`ean`,'-')),GROUP_CONCAT(IFNULL(d.`sale_price`,'-')),MIN(d.`open_date`),MAX(IFNULL(d.`open_cycle`,'-')),s.account_name "+
				" FROM psi_product p  "+
				" JOIN psi_barcode b ON p.id=b.`psi_product` AND b.`del_flag`='0'  "+
				" JOIN psi_sku s ON b.id=s.`barcode` AND s.`del_flag`='0'  "+
				" JOIN amazoninfo_product2 d ON d.`account_name`=s.`account_name` AND d.`sku`=s.sku   "+
				" WHERE  CONCAT(s.`product_name`,CASE WHEN s.`color`!='' THEN '_' ELSE '' END,s.`color`)=:p1 AND d.`active`='1'  "+  //--
				" GROUP BY b.`product_color`,b.`psi_product`,s.`country`,b.`barcode_type`,b.`barcode`,s.account_name order by FIELD(s.country,'de','uk','fr','it','es','com','com1','com2','com3','ca','jp','mx') ";
		List<Object[]> list=psiProductDao.findBySql(sql,new Parameter(productName));
		//List<PsiAttrDto> skuLit=Lists.newArrayList();
		Map<String,PsiProductEliminate> eliminateMap=productEliminateService.findAllInfoByNameWithColor(productName);
		if(list!=null&&list.size()>0){
		   for (Object[] obj: list) {
			    String country=obj[0].toString();
			    String barcodeType=(obj[1]==null?"":obj[1].toString());
			    String barcode=(obj[2]==null?"":obj[2].toString());
			    String asin=(obj[3]==null?"":obj[3].toString());
			    String sku=(obj[4]==null?"":obj[4].toString());
			    String ean=(obj[5]==null?"":obj[5].toString());
			    String price=(obj[6]==null?"":obj[6].toString());
			    PsiAttrDto psiSku=new PsiAttrDto();
			    psiSku.setCountry(country);
			    psiSku.setSku(sku);
			    psiSku.setAsin(asin);
			    psiSku.setBarcode(barcode);
			    psiSku.setEan(ean);
			    psiSku.setPrice(price);
			    psiSku.setBarcodeType(barcodeType);
			    String openDates=(obj[7]==null?"":obj[7].toString());
			    String openC=(obj[8]==null?"":obj[8].toString());
			    String accountName=(obj[9]==null?"":obj[9].toString());
			    psiSku.setAccountName(accountName);
			    psiSku.setPeriod(openC);
			    psiSku.setAddedMonth(openDates);
			    if(eliminateMap!=null&&eliminateMap.get(country)!=null){
			    	PsiProductEliminate e=eliminateMap.get(country);
			    	psiSku.setSalesAttr(e.getIsSale());
			    	psiSku.setMainAttr(e.getIsMain());
			    	psiSku.setNewAttr(e.getIsNew());
			    	psiSku.setSalesDate(e.getAddedMonth());
			    }
			    //skuLit.add(psiSku);
			    List<PsiAttrDto> temp=map.get(country.startsWith("com")?"com":country);
			    if(temp==null){
			    	temp=Lists.newArrayList();
			    	map.put(country.startsWith("com")?"com":country, temp);
			    }
			    temp.add(psiSku);
		   }
		}
		return map;
	}
	
	
	
	
	public Page<PsiProduct> findWithSkuAllCountry(Page<PsiProduct> page, PsiProduct psiProduct) {
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(psiProduct.getModel())) {
			dc.add(Restrictions.like("model", "%" + psiProduct.getModel()+ "%"));
		}
		if (StringUtils.isNotEmpty(psiProduct.getType())) {
			if("other".equals(psiProduct.getType())){
				dc.add(Restrictions.eq("type", ""));
			}else{
				dc.add(Restrictions.eq("type", psiProduct.getType()));
			}
		}
		dc.add(Restrictions.sqlRestriction(" model not like '%other%' and model not like '%Old%' "));
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiProductDao.find(page, dc);
	}
	
	public List<String> findCountryProduct(PsiProduct psiProduct) {
		String sql="";
		String country = psiProduct.getPlatform();
		if(StringUtils.isBlank(country)){
			sql="select distinct CONCAT(a.`product_name`,CASE WHEN a.`product_color`!='' THEN CONCAT ('_',a.`product_color`) ELSE '' END) AS NAME " +
					" from (select distinct product_name,product_color from psi_barcode where del_flag='0' and product_name not like '%other%' and product_name not like '%Old%') a ";
		}else if("eu".equals(country)) {
			sql="select distinct CONCAT(a.`product_name`,CASE WHEN a.`product_color`!='' THEN CONCAT ('_',a.`product_color`) ELSE '' END) AS NAME from psi_barcode a " +
				" where a.del_flag='0' and a.product_platform in ('de','uk','es','fr','it') and  a.product_name not like '%other%' and a.product_name not like '%Old%' ";
		}else if("unEn".equals(country)) {
			sql="select distinct CONCAT(a.`product_name`,CASE WHEN a.`product_color`!='' THEN CONCAT ('_',a.`product_color`) ELSE '' END) AS NAME from psi_barcode a " +
				" where a.del_flag='0' and a.product_platform in ('de','jp','es','fr','it') and  a.product_name not like '%other%' and a.product_name not like '%Old%' ";
		}else if("en".equals(psiProduct.getPlatform())){
			sql="select distinct CONCAT(a.`product_name`,CASE WHEN a.`product_color`!='' THEN CONCAT ('_',a.`product_color`) ELSE '' END) AS NAME from psi_barcode a " +
					" where a.del_flag='0' and a.product_platform in ('com','ca','uk') ";
		}else{
			sql="select distinct CONCAT(a.`product_name`,CASE WHEN a.`product_color`!='' THEN CONCAT ('_',a.`product_color`) ELSE '' END) AS NAME from psi_barcode a " +
					" where a.del_flag='0' and a.product_platform='"+psiProduct.getPlatform()+"' and  a.product_name not like '%other%' and a.product_name not like '%Old%' ";
			}
		return psiProductDao.findBySql(sql);
	}
	
	public List<String> findCountryProduct2(PsiProduct psiProduct) {
		String sql="";
		if(StringUtils.isBlank(psiProduct.getPlatform())){
			sql="select distinct CONCAT(a.`product_name`,CASE WHEN a.`product_color`!='' THEN CONCAT ('_',a.`product_color`) ELSE '' END) AS NAME " +
					" from (select distinct product_name,product_color from psi_barcode where del_flag='0') a ";
		}else if("eu".equals(psiProduct.getPlatform())){
			sql="select distinct CONCAT(a.`product_name`,CASE WHEN a.`product_color`!='' THEN CONCAT ('_',a.`product_color`) ELSE '' END) AS NAME from psi_barcode a " +
					" where a.del_flag='0' and a.product_platform in ('de','fr','it','es','uk') ";
		}else if("unEn".equals(psiProduct.getPlatform())){
			sql="select distinct CONCAT(a.`product_name`,CASE WHEN a.`product_color`!='' THEN CONCAT ('_',a.`product_color`) ELSE '' END) AS NAME from psi_barcode a " +
					" where a.del_flag='0' and a.product_platform in ('de','fr','it','es','jp') ";
		}else if("en".equals(psiProduct.getPlatform())){
			sql="select distinct CONCAT(a.`product_name`,CASE WHEN a.`product_color`!='' THEN CONCAT ('_',a.`product_color`) ELSE '' END) AS NAME from psi_barcode a " +
					" where a.del_flag='0' and a.product_platform in ('com','ca','uk') ";
		}else{
			sql="select distinct CONCAT(a.`product_name`,CASE WHEN a.`product_color`!='' THEN CONCAT ('_',a.`product_color`) ELSE '' END) AS NAME from psi_barcode a " +
				" where a.del_flag='0' and a.product_platform='"+psiProduct.getPlatform()+"'";
		}
		return psiProductDao.findBySql(sql);
	}
	
	public Map<String,String> findSupplierByProductName() {
		String 	sql="select distinct a.name,s.nikename " +
				" from (SELECT DISTINCT psi_product,CONCAT(`product_name`,CASE WHEN `product_color`!='' THEN CONCAT ('_',`product_color`) ELSE '' END) NAME FROM psi_barcode WHERE del_flag='0' AND product_name NOT LIKE '%other%' AND product_name NOT LIKE '%Old%') a "+
				" join psi_product_supplier p on p.product_id=a.psi_product "+
				" join psi_supplier s on s.id=p.supplier_id";
		Map<String,String> map=new HashMap<String,String>();
		List<Object[]> list=psiProductDao.findBySql(sql);
	    for (Object[] obj : list) {
			if(map.get(obj[0].toString())==null){
				map.put(obj[0].toString(),obj[1]==null?"":obj[1].toString());
			}else{
				String name=map.get(obj[0].toString()).concat(obj[1]==null?"":","+obj[1].toString());
				map.remove(obj[0].toString());
				map.put(obj[0].toString(),name);
			}
		}
		return map;
	}
	
	public Map<Integer,String> findSupplierByProducId() {
		String 	sql="select distinct a.psi_product,s.nikename " +
				" from (SELECT DISTINCT psi_product,CONCAT(`product_name`,CASE WHEN `product_color`!='' THEN CONCAT ('_',`product_color`) ELSE '' END) NAME FROM psi_barcode WHERE del_flag='0' AND product_name NOT LIKE '%other%' AND product_name NOT LIKE '%Old%') a "+
				" join psi_product_supplier p on p.product_id=a.psi_product "+
				" join psi_supplier s on s.id=p.supplier_id";
		Map<Integer,String> map=Maps.newHashMap();
		List<Object[]> list=psiProductDao.findBySql(sql);
	    for (Object[] obj : list) {
	    	Integer id=Integer.parseInt(obj[0].toString());
			if(map.get(id)==null){
				map.put(id,obj[1]==null?"":obj[1].toString());
			}else{
				String name=map.get(id).concat(obj[1]==null?"":","+obj[1].toString());
				map.remove(obj[0].toString());
				map.put(id,name);
			}
		}
		return map;
	}
	
	public Page<PsiProduct> findWithSessionsMonitor(Page<PsiProduct> page, SessionMonitor sessionMonitor) {
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(sessionMonitor.getProductName())) {
			dc.add(Restrictions.like("model", "%" + sessionMonitor.getProductName()+ "%"));
		}
		if (StringUtils.isNotEmpty(sessionMonitor.getColor())) {
			if("other".equals(sessionMonitor.getColor())){
				dc.add(Restrictions.eq("type", ""));
			}else{
				dc.add(Restrictions.eq("type", sessionMonitor.getColor()));
			}
		}
		String country = sessionMonitor.getCountry();
		if(StringUtils.isEmpty(country)){
			country ="de";
		}
		String sql = "SELECT b.`id` FROM psi_sku a, psi_product b WHERE a.`product_id` = b.`id` AND a.`country`=:p1 AND a.`del_flag`='0' GROUP BY b.`id`";
		List<Object> params = psiProductDao.findBySql(sql,new Parameter(country));
		if(params.size()>0){
			dc.add(Restrictions.in("id",params));
		}else{
			return null;
		}
		dc.add(Restrictions.like("platform", "%"+country+"%"));
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiProductDao.find(page, dc);
	}
	
	public PsiProduct get(Integer id){
		return psiProductDao.get(id);
	}
	
	public Integer getId(String productName){
		String sql="select id from psi_product where CONCAT(brand,' ',model)='"+productName+"'";
		List<Integer> list=psiProductDao.findBySql(sql);
		return list.get(0);
	}
	
	@Transactional(readOnly = false)
	public Integer updateIsNew(){
		String sql="UPDATE psi_product SET is_new='0' WHERE added_month IS NOT NULL AND is_new='1' AND TO_DAYS(NOW()) - TO_DAYS(added_month)>=180 ";
		return psiProductDao.updateBySql(sql, null);
	}
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		String sql ="UPDATE  psi_barcode  SET del_flag='1' WHERE psi_product =:p1";
		psiProductDao.updateBySql(sql, new Parameter(id));
		sql ="UPDATE psi_sku  SET del_flag='1' WHERE product_id =:p1";
		psiProductDao.updateBySql(sql, new Parameter(id));
		sql ="delete from psi_product_supplier  WHERE product_id =:p1";
		psiProductDao.updateBySql(sql, new Parameter(id));
		psiProductDao.deleteById(id);
		productPartsService.deleteByProductId(id);
		//删除淘汰明细
		sql ="UPDATE psi_product_eliminate SET del_flag='1' WHERE product_id =:p1";
		psiProductDao.updateBySql(sql, new Parameter(id));
		sql="UPDATE psi_product_attribute SET del_flag='1' WHERE product_id=:p1 ";
		psiProductDao.updateBySql(sql,new Parameter(id));
		
		//删除阶级价格
		sql="UPDATE psi_product_tiered_price AS b SET b.`del_flag`='1' WHERE b.`product_id`=:p1 ";
		psiProductDao.updateBySql(sql,new Parameter(id));
	}
	
	@Transactional(readOnly = false)
	public void updateProductState(PsiProduct psiProduct){
		psiProductDao.save(psiProduct);
	}
	
	@Transactional(readOnly = false)
	public void updateProductAddedMontn(Integer pid,String addedMonth){
		String sql = "update psi_product set added_month = :p1 where id = :p2";
		psiProductDao.updateBySql(sql, new Parameter(addedMonth,pid));
	}
	
	@Transactional(readOnly = false)
	public void updateChineseName(String chineseName,Integer productId){
		String sql = "update psi_product set chinese_name = :p1 where id = :p2";
		psiProductDao.updateBySql(sql, new Parameter(chineseName,productId));
	}
	
	@Transactional(readOnly = false)
	public void deleteFilePath(String newPath,Integer productId){
        if(StringUtils.isBlank(newPath)){
        	String sql = "update psi_product set file_path = null where id = :p1";
    		psiProductDao.updateBySql(sql, new Parameter(productId));
		}else{
			String sql = "update psi_product set file_path = :p1 where id = :p2";
			psiProductDao.updateBySql(sql, new Parameter(newPath,productId));
		}
	}
	@Transactional(readOnly = false)
	public void save(PsiProduct psiProduct){
		this.psiProductDao.save(psiProduct);
	}
	
	public Object[] findHscode(String name){
		String sql="SELECT t.`eu_hscode`,t.`jp_hscode`,t.`ca_hscode`,t.`us_hscode`,t.`hk_hscode`,t.`cn_hscode`,t.`mx_hscode`, "+
				" t.`eu_custom_duty`,t.`jp_custom_duty`,t.`ca_custom_duty`,t.`us_custom_duty`,t.`mx_custom_duty`,t.`tax_refund`  "+
				" FROM psi_product t WHERE t.chinese_name=:p1 AND t.`del_flag`='0'   "+
				" AND (t.`eu_hscode` IS NOT NULL OR t.`us_hscode` IS NOT NULL OR t.`jp_hscode` IS NOT NULL OR t.`ca_hscode` IS NOT NULL) ORDER BY t.`create_time` DESC LIMIT 1 ";
		List<Object[]> list=psiProductDao.findBySql(sql,new Parameter(name));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public void save(PsiProduct psiProduct,String[] suppliers,Integer oldPackQuantity){
		psiProductDao.getSession().clear();
		String newColor=psiProduct.getColor();
		String oldColor="";
		Map<String,List<String>> accountMap=amazonAccountConfigService.findAccountByCountryNoServer();
		if(psiProduct.getId()==null){
			psiProduct.setReviewSta("0");//未审核0
			if (psiProduct.getTransportType() == null) {
				psiProduct.setTransportType(2);	//新品
			}
			String[] platforms =  psiProduct.getPlatform().split(",");
			List<PsiBarcode> bars = Lists.newArrayList();
			List<PsiProductEliminate> eliminates = Lists.newArrayList();
			for (String platform : platforms) {
				String[] colors = psiProduct.getColor().split(",");
				if("com.unitek".equals(platform)){
					continue;
				}
				for (String color : colors) {
					if(accountMap!=null&&accountMap.get(platform)!=null){
						for(String account:accountMap.get(platform)){
							PsiBarcode barcode = new PsiBarcode();
							barcode.setLastUpdateTime(new Date());
							barcode.setProductColor(color);
							barcode.setProductPlatform(platform);
							barcode.setProductName(psiProduct.getName());
							barcode.setPsiProduct(psiProduct);
							barcode.setAccountName(account);
							bars.add(barcode);
						}
					}
					
					//新增时增加产品明细
					PsiProductEliminate eliminate = new PsiProductEliminate();
					eliminate.setProduct(psiProduct);
					eliminate.setProductName(psiProduct.getName());
					eliminate.setCountry(platform);
					eliminate.setColor(StringUtils.isEmpty(color)?"":color);
					eliminate.setIsSale("1"); // 是否在售
					eliminate.setDelFlag("0");
					
					eliminate.setIsNew("1");//是否新品
					eliminate.setTransportType(psiProduct.getTransportType());
					eliminate.setSalesForecastScheme(forecastMap.get(psiProduct.getType()));
					eliminates.add(eliminate);
				}
			}
			psiProduct.setBarcodes(bars);
			if(suppliers.length>0){
				//处理供应商
				List<ProductSupplier> supplierList = Lists.newArrayList();
				for (String supplierId : suppliers) {
					supplierList.add(new ProductSupplier(psiProduct,supplierDao.get(Integer.parseInt(supplierId))));
				}
				psiProduct.setPsiSuppliers(supplierList);
			}
			if(psiProduct.getModel().endsWith("EU")||psiProduct.getModel().endsWith("US")||psiProduct.getModel().endsWith("DE")||psiProduct.getModel().endsWith("UK")||psiProduct.getModel().endsWith("JP")){
				psiProduct.setModelShort(psiProduct.getModel().substring(0,psiProduct.getModel().length()-2));
			}else{
				psiProduct.setModelShort(psiProduct.getModel());
			}
			
			psiProductDao.save(psiProduct);
			productEliminateService.save(eliminates);//新增时保存淘汰明细
			
			for(String color : psiProduct.getColor().split(",")){
				//新增时增加产品分颜色不分平台属性明细
				PsiProductAttribute attribute = new PsiProductAttribute();
				attribute.setProduct(psiProduct);
				attribute.setProductName(psiProduct.getName());
				attribute.setColor(color);
				attribute.setCreateUser(UserUtils.getUser());
				attribute.setCreateDate(psiProduct.getCreateTime());
				attribute.setDelFlag("0");
				psiProductAttributeService.save(attribute);
				
				for(String supplierId:suppliers){
					PsiSupplier sup = new PsiSupplier();
					sup.setId(Integer.parseInt(supplierId));
					for(Integer level:priceTiereds){
						for(String currency:currencyType){
							PsiProductTieredPrice tieredPrice = new PsiProductTieredPrice();
							tieredPrice.setProduct(psiProduct);
							tieredPrice.setColor(color);
							tieredPrice.setLevel(level);
							tieredPrice.setCurrencyType(currency);
							tieredPrice.setSupplier(sup);
							tieredPrice.setDelFlag("0");
							tieredPrice.setCreateUser(UserUtils.getUser());
							tieredPrice.setCreateTime(new Date());
							tieredPrice.setUpdateUser(UserUtils.getUser());
							tieredPrice.setUpdateTime(new Date());
							this.productTieredDao.save(tieredPrice);
						}
					}
				}
			}
			try{
				 if(StringUtils.isNotBlank(psiProduct.getChineseName())){
					 Object[] temp=findHscode(psiProduct.getChineseName());
					 if(temp!=null){
						    psiProduct.setEuHscode(temp[0]==null?null:temp[0].toString());
							psiProduct.setJpHscode(temp[1]==null?null:temp[1].toString());
							psiProduct.setCaHscode(temp[2]==null?null:temp[2].toString());
							psiProduct.setUsHscode(temp[3]==null?null:temp[3].toString());
							psiProduct.setHkHscode(temp[4]==null?null:temp[4].toString());
							psiProduct.setCnHscode(temp[5]==null?null:temp[5].toString());
							psiProduct.setMxHscode(temp[6]==null?null:temp[6].toString());
							psiProduct.setEuCustomDuty(temp[7]==null?null:Float.parseFloat(temp[7].toString()));
							psiProduct.setJpCustomDuty(temp[8]==null?null:Float.parseFloat(temp[8].toString()));
							psiProduct.setCaCustomDuty(temp[9]==null?null:Float.parseFloat(temp[9].toString()));
							psiProduct.setUsCustomDuty(temp[10]==null?null:Float.parseFloat(temp[10].toString()));
							psiProduct.setMxCustomDuty(temp[11]==null?null:Float.parseFloat(temp[11].toString()));
							psiProduct.setTaxRefund(temp[12]==null?null:Integer.parseInt(temp[12].toString()));
							logger.info(psiProduct.getChineseName()+"=="+psiProduct.getName()+"=="+psiProduct.getEuHscode());
					 }
				 }
			}catch(Exception e){
				   logger.info("hscode:"+psiProduct.getType(),e);
			}
			psiProduct.setEuImportDuty(19f);
			psiProduct.setJpImportDuty(8f);
			psiProduct.setCaImportDuty(14.975f);
			psiProduct.setUsImportDuty(0.47f);
			psiProduct.setMxImportDuty(16f);
			psiProductDao.save(psiProduct);
		}else{
			String  sendContent="";
			boolean  packQuantityFlag =false;
			//对供应商变化进行处理
			Set<Integer> oldSupplierIds=Sets.newHashSet();
			Set<Integer> newSupplierIds=Sets.newHashSet();
			Set<Integer> addSupplierIds=Sets.newHashSet();
			Set<Integer> delSupplierIds=Sets.newHashSet();
			
			
			PsiProduct updateProduct = findProductById(psiProduct.getId());
			oldColor = updateProduct.getColor();
			updateProduct.setBoxVolume(psiProduct.getBoxVolume());
			updateProduct.setBrand(psiProduct.getBrand());
			updateProduct.setCombination(psiProduct.getCombination());
			updateProduct.setDescription(psiProduct.getDescription());
			updateProduct.setGw(psiProduct.getGw());
			updateProduct.setHeight(psiProduct.getHeight());
			updateProduct.setImage(psiProduct.getImage());
			updateProduct.setIsMain(psiProduct.getIsMain());
			updateProduct.setIsNew(psiProduct.getIsNew());
			updateProduct.setIsSale(psiProduct.getIsSale());
			updateProduct.setLength(psiProduct.getLength());
			updateProduct.setModel(psiProduct.getModel());
			updateProduct.setPackQuantity(psiProduct.getPackQuantity());
			updateProduct.setType(psiProduct.getType());
			updateProduct.setUpdateTime(new Date());
			updateProduct.setUpdateUser(UserUtils.getUser());
			updateProduct.setVolumeRatio(psiProduct.getVolumeRatio());
			updateProduct.setWeight(psiProduct.getWeight());
			updateProduct.setWidth(psiProduct.getWidth());
			
			updateProduct.setPackHeight(psiProduct.getPackHeight());
			updateProduct.setPackWidth(psiProduct.getPackWidth());
			updateProduct.setPackLength(psiProduct.getPackLength());
			updateProduct.setTransportType(psiProduct.getTransportType());
			updateProduct.setProducePeriod(psiProduct.getProducePeriod());
			updateProduct.setMinOrderPlaced(psiProduct.getMinOrderPlaced());
			updateProduct.setProductPackLength(psiProduct.getProductPackLength());
			updateProduct.setProductPackWidth(psiProduct.getProductPackWidth());
			updateProduct.setProductPackHeight(psiProduct.getProductPackHeight());
			updateProduct.setProductPackWeight(psiProduct.getProductPackWeight());
			updateProduct.setRemark(psiProduct.getRemark());
			updateProduct.setProductList(psiProduct.getProductList());
			updateProduct.setFilePath(psiProduct.getFilePath());
			
			updateProduct.setChineseName(psiProduct.getChineseName());
			String platformStr = psiProduct.getPlatform();
			String colorStr = psiProduct.getColor();
			updateProduct.setPlatform(platformStr);
			updateProduct.setColor(colorStr);
			
			updateProduct.setCertification(psiProduct.getCertification());
			updateProduct.setFactoryCertification(psiProduct.getFactoryCertification());
			updateProduct.setInateckCertification(psiProduct.getInateckCertification());
			updateProduct.setCertificationFile(psiProduct.getCertificationFile());
			updateProduct.setHasElectric(psiProduct.getHasElectric());
			updateProduct.setHasPower(psiProduct.getHasPower());
			updateProduct.setTranReportFile(psiProduct.getTranReportFile());
			updateProduct.setPriceChangeLog(psiProduct.getPriceChangeLog());
			
			updateProduct.setContractNo(psiProduct.getContractNo());
			updateProduct.setDeclarePoint(psiProduct.getDeclarePoint());
			updateProduct.setCreateUser(psiProduct.getCreateUser());//跟单员
			updateProduct.setMaterial(psiProduct.getMaterial());//材质
			updateProduct.setTaxRefund(psiProduct.getTaxRefund());//退税率
			updateProduct.setImproveRemark(psiProduct.getImproveRemark());
			updateProduct.setHasMagnetic(psiProduct.getHasMagnetic());
			updateProduct.setReviewSta(psiProduct.getReviewSta());
			
			updateProduct.setComponents(psiProduct.getComponents());
			//如果装箱数不同发送邮件     老装箱数不为1 
			if(psiProduct.getId()!=null && oldPackQuantity.intValue()!=1&&oldPackQuantity.intValue()!=psiProduct.getPackQuantity().intValue()){
				packQuantityFlag=true;
				sendContent = updateProduct.getName()+"'s Packing Number Change From:("+oldPackQuantity+"),To:("+psiProduct.getPackQuantity()+")";
			}
			
			Map<String,Map<String, PsiBarcode>> barcodeMap = updateProduct.getBarcodeMapByAccount();
			String[] platforms = platformStr.split(",");
			List<PsiBarcode> list = updateProduct.getBarcodes();
			for (String platform : platforms) {
				String[] colors = colorStr.split(",");
				if("com.unitek".equals(platform)){
					continue;
				}
				for (String color : colors) {
					if(accountMap!=null&&accountMap.get(platform)!=null){
						for(String account:accountMap.get(platform)){
							if(barcodeMap.get(account)!=null){
								PsiBarcode code = barcodeMap.get(account).get(color);
								if(code!=null){
									code.setDelFlag("0");
									List<PsiSku> skus = code.getSkus();
									if(skus!=null){
										for (PsiSku psiSku : skus) {
											psiSku.setDelFlag("0");
										}
									}
								}else{
									PsiBarcode barcode = new PsiBarcode();
									barcode.setLastUpdateTime(new Date());
									barcode.setProductColor(color);
									barcode.setProductPlatform(platform);
									barcode.setProductName(psiProduct.getName());
									barcode.setPsiProduct(psiProduct);
									barcode.setAccountName(account);
									list.add(barcode);
								}
							}else{
								PsiBarcode barcode = new PsiBarcode();
								barcode.setLastUpdateTime(new Date());
								barcode.setProductColor(color);
								barcode.setProductPlatform(platform);
								barcode.setProductName(psiProduct.getName());
								barcode.setPsiProduct(psiProduct);
								barcode.setAccountName(account);
								list.add(barcode);
							}
						}
					}
				}	
			}
			List<ProductSupplier> supplierList = updateProduct.getPsiSuppliers();
			if(supplierList==null){
				supplierList = Lists.newArrayList();
			}
			if(suppliers==null){
				suppliers = new String[0];
			}
			List<Integer> supplierIds = Lists.newArrayList();
			for (String idStr : suppliers) {
				Integer id = Integer.parseInt(idStr);
				supplierIds.add(id);
				newSupplierIds.add(id);
			}
			Set<Integer> tempSet = Sets.newHashSet();
			String sql = "delete from psi_product_supplier where id =:p1";
			for (Iterator<ProductSupplier> iterator = supplierList.iterator(); iterator.hasNext();) {
				ProductSupplier productSupplier = iterator.next();
				Integer id =  productSupplier.getSupplier().getId();
				if(!supplierIds.contains(id)){
					psiProductDao.updateBySql(sql, new Parameter(productSupplier.getId()));
					delSupplierIds.add(id);
				}else{
					tempSet.add(id);
				}
				oldSupplierIds.add(id);
			}
			for (Integer id : supplierIds) {
				if(!tempSet.contains(id)){
					supplierList.add(new ProductSupplier(updateProduct, supplierDao.get(id)));
					addSupplierIds.add(id);
				}
			}
			
			psiProductDao.save(updateProduct);
			//如果装箱数变化    发送邮件 
			if(packQuantityFlag){
				//查询对应的产品经理
				String productMannagerEmail=this.findMangerMailByProductName(updateProduct.getName());
				this.sendEmail(sendContent,productMannagerEmail);
			}
			
			//阶梯价格处理 star
			Integer productId=updateProduct.getId();
			//编辑时对原来颜色进行比较，如果颜色有变动，删除相应的产品阶梯价格
			Set<String> addPriceSet = Sets.newHashSet();
			Set<String> delPriceSet = Sets.newHashSet();
			Set<String> delColorSet = Sets.newHashSet();
			Set<String> addColorSet = Sets.newHashSet();
			Set<String> oldColorSet = Sets.newHashSet();
			Set<String> newColorSet = Sets.newHashSet();
			for(String oldColorStr:oldColor.split(",")){
				oldColorSet.add(oldColorStr);
			}
			for(String newColorStr:newColor.split(",")){
				newColorSet.add(newColorStr);
			}
			for(String color:newColor.split(",")){
				//老的里面没有新的
				if(!oldColorSet.contains(color)){
					addColorSet.add(color);
				}
			}
			for(String color:oldColor.split(",")){
				//新的里面没有老的
				if(!newColorSet.contains(color)){
					delColorSet.add(color);
				}
			}
					
			//如果有删除、增加产品     删除、增加供应商
			if(delColorSet.size()!=0||addColorSet.size()!=0||addSupplierIds.size()!=0||delSupplierIds.size()!=0){
				//进行删除和新增操作
				if(delColorSet.size()>0){
					for(String delColor:delColorSet){
						//原供应商id的全删除
						for(Integer supplierId:oldSupplierIds){
							String addTieredStr = productId+","+delColor+","+supplierId;
							delPriceSet.add(addTieredStr);
						}
					}
				}
				if(addColorSet.size()>0){
					for(String addColor:addColorSet){
						if(suppliers.length>0){
							for(String supplierId:suppliers){
								String addTieredStr = productId+","+addColor+","+supplierId;
								addPriceSet.add(addTieredStr);
							}
						}
					}
				}
				if(delSupplierIds.size()>0){
					for(Integer supplierId:delSupplierIds){
						//删除所有旧的颜色
						for(String color:oldColor.split(",")){
							String delTieredStr = productId+","+color+","+supplierId;
							delPriceSet.add(delTieredStr);
						}
					}
				}
				if(addSupplierIds.size()>0){
					for(Integer supplierId:addSupplierIds){
						//增加所有新的颜色
						for(String color:newColor.split(",")){
							String addTieredStr = productId+","+color+","+supplierId;
							addPriceSet.add(addTieredStr);
						}
					}
				}
				if(addPriceSet.size()>0){
					for(String addPriceStr :addPriceSet){
						String arr[] = addPriceStr.split(",");
						String addColor = arr[1];
						String addSupplierId =arr[2];
						PsiSupplier sup = new PsiSupplier();
						sup.setId(Integer.parseInt(addSupplierId));
						//对档位进行设置
						for(Integer leval:priceTiereds){
							for(String currency:currencyType){
								PsiProductTieredPrice tieredPrice = new PsiProductTieredPrice();
								tieredPrice.setProduct(psiProduct);
								tieredPrice.setColor(addColor);
								tieredPrice.setLevel(leval);
								tieredPrice.setCurrencyType(currency);
								tieredPrice.setSupplier(sup);
								tieredPrice.setCreateUser(UserUtils.getUser());
								tieredPrice.setCreateTime(new Date());
								tieredPrice.setUpdateUser(UserUtils.getUser());
								tieredPrice.setUpdateTime(new Date());
								tieredPrice.setDelFlag("0");
								this.productTieredDao.save(tieredPrice);
							}
						}
					}
				}
				
				if(delPriceSet.size()>0){
					for(String delPriceStr :delPriceSet){
						String arr[] = delPriceStr.split(",");
						String delColor = arr[1];
						String delSupplierId =arr[2];
						this.productTieredDao.deleteByProductColorSupplier(productId, delColor, delSupplierId);
					}
				}
			 }
			
			//如果是新品、已审核并且添加了颜色，生成新品采购计划
			if(addColorSet.size()>0&&!"1".equals(updateProduct.getComponents())){
				try {
					planDao.genPlan(updateProduct, addColorSet);
				} catch (Exception e) {}
				
			}
			productEliminateService.updateProductEliminate(updateProduct);//更新淘汰明细
			psiProductAttributeService.updateProductAttr(updateProduct);//更新产品分颜色属性
		 }
		//阶梯价格处理end
	}	
	
	@Transactional(readOnly = false)
	public void delSkus(){
		String sql="SELECT DISTINCT p.`id` FROM psi_sku AS p ,psi_barcode AS b WHERE p.`barcode` = b.`id` AND  p.`product_id`= b.`psi_product`  AND p.color=b.`product_color` AND p.country=b.`product_platform` AND b.`del_flag`='1' AND p.`del_flag`='0' ";
		List<Integer> aa=psiProductDao.findBySql(sql);
		if(aa.size()>0){
			sql="UPDATE psi_sku AS a SET a.del_flag='1' WHERE a.id IN :p1";
			psiProductDao.updateBySql(sql, new Parameter(aa));
		}
	}
	
	public PsiProduct findProductByIdLazy(Integer id){
		return psiProductDao.get(id);
	}
	
	public PsiProduct findProductById(Integer id){
		PsiProduct psiProduct =  psiProductDao.get(id);
		for (PsiBarcode barcode : psiProduct.getBarcodes()) {
			Hibernate.initialize(barcode);
		}
		return psiProduct;
	}
	
	
	public boolean isExistName(PsiProduct psiProduct) {
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		dc.add(Restrictions.eq("model",psiProduct.getModel()));
		dc.add(Restrictions.eq("brand",psiProduct.getBrand()));
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiProductDao.count(dc)>0;
	}
	
	public List<PsiProduct> findAllByCountry(String country,String type){
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(country)){
			if("eu".equals(country)){
				dc.add(Restrictions.or(Restrictions.like("platform", "%fr%"),Restrictions.like("platform", "%de%"), 
										Restrictions.like("platform", "%es%"),  Restrictions.like("platform", "%it%"),  Restrictions.like("platform", "%uk%")));
			}else if("en".equals(country)){
				dc.add(Restrictions.or(Restrictions.like("platform", "%uk%"),Restrictions.like("platform", "%ca%"), 
						Restrictions.like("platform", "%com%")));
			}else if("am".equals(country)){	//美洲
				dc.add(Restrictions.or(Restrictions.like("platform", "%ca%"), Restrictions.like("platform", "%com%")));
			}else{
				dc.add(Restrictions.like("platform", "%"+country+"%"));
			}
		}
		if(StringUtils.isNotEmpty(type)){
			dc.add(Restrictions.eq("type", type));
		}
		dc.add(Restrictions.or(Restrictions.isNull("components"),Restrictions.eq("components", "0")));
		dc.add(Restrictions.ne("model", "other"));
		dc.add(Restrictions.ne("model", "Old"));
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.asc("createTime"));
		return psiProductDao.find(dc);
	}
	
	public List<PsiProduct> findAllByCountry2(String country,String type){
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(country)){
			if("eu".equals(country)){
				dc.add(Restrictions.or(Restrictions.like("platform", "%fr%"),Restrictions.like("platform", "%de%"), 
										Restrictions.like("platform", "%es%"),  Restrictions.like("platform", "%it%"),  Restrictions.like("platform", "%uk%")));
			}else if("en".equals(country)){
				dc.add(Restrictions.or(Restrictions.like("platform", "%uk%"),Restrictions.like("platform", "%ca%"), 
						Restrictions.like("platform", "%com%")));

			}else{
				dc.add(Restrictions.like("platform", "%"+country+"%"));
			}
		}
		if(StringUtils.isNotEmpty(type)){
			dc.add(Restrictions.eq("type", type));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.asc("createTime"));
		return psiProductDao.find(dc);
	}
	
	public List<PsiProduct> findAllByCountryByType(String country,Set<String> type){
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(country)){
			if("eu".equals(country)){
				dc.add(Restrictions.or(Restrictions.like("platform", "%fr%"),Restrictions.like("platform", "%de%"), 
										Restrictions.like("platform", "%es%"),  Restrictions.like("platform", "%it%"),  Restrictions.like("platform", "%uk%")));
			}else if("en".equals(country)){
				dc.add(Restrictions.or(Restrictions.like("platform", "%uk%"),Restrictions.like("platform", "%ca%"), 
						Restrictions.like("platform", "%com%")));

			}else if("unEn".equals(country)){
				dc.add(Restrictions.or(Restrictions.like("platform", "%de%"),Restrictions.like("platform", "%fr%"), 
						Restrictions.like("platform", "%it%"),Restrictions.like("platform", "%es%"),Restrictions.like("platform", "%jp%")));

			}else{
				dc.add(Restrictions.like("platform", "%"+country+"%"));
			}
		}
		if(type!=null&&type.size()>0){
			dc.add(Restrictions.in("type", type));
		}
		dc.add(Restrictions.ne("model", "other"));
		dc.add(Restrictions.ne("model", "Old"));
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.asc("createTime"));
		return psiProductDao.find(dc);
	}
	
	public List<PsiProduct> findAllByCountryByType2(String country,Set<String> type){
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(country)){
			if("eu".equals(country)){
				dc.add(Restrictions.or(Restrictions.like("platform", "%fr%"),Restrictions.like("platform", "%de%"), 
										Restrictions.like("platform", "%es%"),  Restrictions.like("platform", "%it%"),  Restrictions.like("platform", "%uk%")));
			}else if("en".equals(country)){
				dc.add(Restrictions.or(Restrictions.like("platform", "%uk%"),Restrictions.like("platform", "%ca%"), 
						Restrictions.like("platform", "%com%")));

			}else if("unEn".equals(country)){
				dc.add(Restrictions.or(Restrictions.like("platform", "%de%"),Restrictions.like("platform", "%fr%"), 
						Restrictions.like("platform", "%it%"),Restrictions.like("platform", "%es%"),Restrictions.like("platform", "%jp%")));

			}else{
				dc.add(Restrictions.like("platform", "%"+country+"%"));
			}
		}
		if(type!=null&&type.size()>0){
			dc.add(Restrictions.in("type", type));
		}
		
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.asc("createTime"));
		return psiProductDao.find(dc);
	}
	
	public PsiBarcode getBarcodeById(Integer barcodeId) {
		return barcodeDao.get(barcodeId);
	}
	
	public PsiSku getSkuById(Integer skuId) {
		return psiSkuDao.get(skuId);
	}
	
	@Transactional(readOnly = false)
	public void save(PsiSku psiSku) {
		psiSkuDao.save(psiSku);
	}
	
	
	@Transactional(readOnly = false)
	public void save(PsiBarcode barcode) {
		barcodeDao.save(barcode);
	}
	
	@Transactional(readOnly = false)
	public void save(List<PsiBarcode> barcodes) {
		barcodeDao.save(barcodes);
	}
	
	@Transactional(readOnly = false)
	public void deleteSku(Integer id) {
		psiSkuDao.deleteById(id);
	}
	
	/**
	 * 单颜色产品 key不加入颜色
	 * @return
	 */
	public Map<String,PsiBarcode> findBarcode() {
		DetachedCriteria dc = barcodeDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		List<PsiBarcode> rs =  barcodeDao.find(dc);
		Map<String, PsiBarcode> rsMap = Maps.newHashMap();
		for (PsiBarcode psiBarcode : rs) {
			String pn = psiBarcode.getProductName();
			if(StringUtils.isNotEmpty(psiBarcode.getProductColor())){
				String color = psiBarcode.getPsiProduct().getColor();
				if(color.contains(",")){
					pn=pn+"_"+psiBarcode.getProductColor();
				}
			}
			for (PsiSku sku :psiBarcode.getSkus()) {
				Hibernate.initialize(sku);
			}
			rsMap.put(pn+"_"+psiBarcode.getProductPlatform(), psiBarcode);
		}
		return rsMap;
	}
	
	public Map<String,PsiBarcode> findBarcodeByAccount() {
		DetachedCriteria dc = barcodeDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		List<PsiBarcode> rs =  barcodeDao.find(dc);
		Map<String, PsiBarcode> rsMap = Maps.newHashMap();
		for (PsiBarcode psiBarcode : rs) {
			String pn = psiBarcode.getProductName();
			if(StringUtils.isNotEmpty(psiBarcode.getProductColor())){
				String color = psiBarcode.getPsiProduct().getColor();
				if(color.contains(",")){
					pn=pn+"_"+psiBarcode.getProductColor();
				}
			}
			for (PsiSku sku :psiBarcode.getSkus()) {
				Hibernate.initialize(sku);
			}
			rsMap.put(pn+"_"+(psiBarcode.getAccountName()==null?"":psiBarcode.getAccountName()), psiBarcode);
		}
		return rsMap;
	}
	
	@Transactional(readOnly = false)
	public void updateProductAsin(){
		String sql = "SELECT b.`id`,a.`asin` FROM amazoninfo_product2 a , psi_sku b WHERE b.`del_flag` = '0' AND a.`sku` = b.`sku` AND a.`country` = b.`country` AND (b.`asin` IS NULL OR a.`asin` != b.`asin`) and a.asin is not null";
		List<Object[]> list = psiSkuDao.findBySql(sql);
		sql = "UPDATE psi_sku a SET ASIN = :p2 WHERE a.`id`= :p1";
		for (Object[] objects : list) {
			psiSkuDao.updateBySql(sql, new Parameter(objects[0],objects[1].toString()));
		}
	}
		
	public PsiSku getSku(String name,String country,String color){
		DetachedCriteria dc = psiSkuDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.add(Restrictions.eq("color", color));
		dc.add(Restrictions.eq("country", country));
		dc.add(Restrictions.eq("productName", name));
		List<PsiSku> rs = psiSkuDao.find(dc);
		for (PsiSku psiSku : rs) {
			if("1".equals(psiSku.getUseBarcode())){
				return psiSku;
			}
		}	
		return null;
	}
	
	public PsiSku getPsiSku(String name,String country,String color){
		DetachedCriteria dc = psiSkuDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		if(StringUtils.isNotBlank(color)){
			dc.add(Restrictions.eq("color", color));
		}
		dc.add(Restrictions.eq("country", country));
		dc.add(Restrictions.eq("productName", name));
		List<PsiSku> rs = psiSkuDao.find(dc);
		if(rs!=null&&rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	
	public PsiSku getPsiSku2(String name,String country,String color){
		String sql="select p.product_name, p.color, p.asin, p.sku from psi_sku p JOIN psi_inventory_fba AS a  ON a.`country`=p.`country` AND a.`sku`=p.sku AND p.`del_flag`='0'  "+
					" WHERE  a.`data_date`= :p1 AND  p.country IN :p2 and product_name=:p3 and a.`fulfillable_quantity`>0 ";
		
		if(StringUtils.isNotBlank(color)){
			sql+=" and color=:p4 ";
		}
		String sql1 = "SELECT a.`data_date` FROM psi_inventory_fba AS a where a.country in :p1 ORDER BY a.`data_date` DESC LIMIT 1";
		List<Date> dates=Lists.newArrayList();
		if("de,fr,it,es,uk".contains(country)){
			dates=this.psiSkuDao.findBySql(sql1,new Parameter(Sets.newHashSet("de","fr","it","es","uk")));
		}else{
			dates=this.psiSkuDao.findBySql(sql1,new Parameter(Sets.newHashSet(country)));
		}
		if(dates!=null&&dates.size()>0){
			Date date  = dates.get(0);
			List<Object[]> list=Lists.newArrayList();
			if("de,fr,it,es,uk".contains(country)){
				if(StringUtils.isNotBlank(color)){
					list=psiSkuDao.findBySql(sql,new Parameter(date,Sets.newHashSet("de","fr","it","es","uk"),name,color));
				}else{
					list=psiSkuDao.findBySql(sql,new Parameter(date,Sets.newHashSet("de","fr","it","es","uk"),name));
				}
			}else{
				if(StringUtils.isNotBlank(color)){
					list=this.psiSkuDao.findBySql(sql,new Parameter(date,Sets.newHashSet(country),name,color));
				}else{
					list=this.psiSkuDao.findBySql(sql,new Parameter(date,Sets.newHashSet(country),name));
				}
			}
			if(list!=null&&list.size()>0){
				PsiSku psisku=new PsiSku();// product_name,color,asin,sku 
				for (Object[] obj: list) {
					psisku.setProductName(obj[0]==null?"":obj[0].toString());
					psisku.setColor(obj[1]==null?"":obj[1].toString());
					psisku.setAsin(obj[2]==null?"":obj[2].toString());
					psisku.setSku(obj[3]==null?"":obj[3].toString());
					return psisku;
				}
			}
		}	
		return null;
	}
	
	public Map<String,Set<String>> getSkuMapByProduct(Set<Integer> productIds,Set<String> refSkus){
		DetachedCriteria dc = psiSkuDao.createDetachedCriteria();
		Map<String,Set<String>> tempMap = Maps.newHashMap();
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.add(Restrictions.in("productId", productIds));
		dc.add(Restrictions.not(Restrictions.like("sku", "%local")));
		dc.add(Restrictions.not(Restrictions.like("sku", "%\\_old%")));
		dc.add(Restrictions.not(Restrictions.like("sku", "%-old%")));
		List<PsiSku> rs = psiSkuDao.find(dc);
		for (PsiSku psiSku : rs) {
			String key =psiSku.getProductName()+"|"+(psiSku.getCountry().equals("com")?"us":psiSku.getCountry());
			if(StringUtils.isNotEmpty(psiSku.getColor())){
				key = key+"|"+psiSku.getColor();
			}	
			String sku = psiSku.getSku();
			String country = psiSku.getCountry();
			
			//如果是欧洲的看结尾是否包含国家信息  
			if(sku.contains("-")){
				String tempSku = sku.substring(sku.lastIndexOf("-")+1);
				if(tempSku.contains("UK")&&!"uk".equals(country)){
					continue;
				}else if(tempSku.contains("FR")&&!"fr".equals(country)){
					continue;
				}else if(tempSku.contains("ES")&&!"es".equals(country)){
					continue;
				}else if(tempSku.contains("IT")&&!"it".equals(country)){
					continue;
				}else if(tempSku.contains("DE")&&!"de".equals(country)){
					continue;
				}
			}else{
				if(!"de".equals(country)){
					continue;
				}
			}
			
			String value=psiSku.getUseBarcode()+"|"+psiSku.getSku();
			Set<String> skuSet = null;
			if(tempMap.get(key)==null){
				skuSet = Sets.newHashSet();
			}else{
				skuSet = tempMap.get(key);
			}
			tempMap.put(key, skuSet);
			refSkus.add(psiSku.getSku());
			skuSet.add(value);
		}	
		return tempMap;
	}
	
	public boolean getProductIsHasSku(Integer productId,String country,String color){
		DetachedCriteria dc = psiSkuDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.add(Restrictions.eq("color", color));
		dc.add(Restrictions.eq("country", country));
		dc.add(Restrictions.eq("productId", productId));
		return psiSkuDao.count(dc)>0;
	}
	
	public String validateSku(String sku,String country){
		DetachedCriteria dc = psiSkuDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag","0"));
		dc.add(Restrictions.eq("country",country));
		dc.add(Restrictions.eq("sku", sku));
		List<PsiSku> rs = psiSkuDao.find(dc);
		if(rs.size()>0){
			return sku+"已经被"+rs.get(0).getProductName()+"_"+rs.get(0).getColor()+"绑定,不能重复绑定！";
		}
		return "";
	}
	
	public String validateFnSku(String sku){
		DetachedCriteria dc = psiSkuDao.createDetachedCriteria();
		dc.add(Restrictions.eq("useBarcode","1"));
		dc.add(Restrictions.eq("delFlag","0"));
		dc.add(Restrictions.eq("sku", sku));
		List<PsiSku> rs = psiSkuDao.find(dc);
		if(rs.size()>0){
			return sku+"的Fnsku已经被"+DictUtils.getDictLabel(rs.get(0).getCountry(),"platform","")+"的"+rs.get(0).getProductName()+"_"+rs.get(0).getColor()+"使用！";
		}
		return "";
	}
	
	/**
	 * 查询所有产品的sku，包括一个产品多个sku
	 */
	public List<PsiSku> getSkus(Set<String> countrySet){
		DetachedCriteria dc = psiSkuDao.createDetachedCriteria();
		if(countrySet!=null&&countrySet.size()>0){
			dc.add(Restrictions.in("country", countrySet));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		List<PsiSku> rs = psiSkuDao.find(dc);
		return rs;
	}
	
	public Map<String,Integer> findNewQuantityByCn(Integer warehouseId){
    	Map<String,Integer> map=Maps.newHashMap();
    	String sql="SELECT p.`country_code`,CONCAT(p.`product_name`,CASE WHEN p.`color_code`='' THEN '' ELSE  CONCAT('_',p.`color_code`) END) NAME,SUM(p.`new_quantity`) FROM psi_inventory p "+
          " WHERE p.`warehouse_id`=:p1  GROUP BY p.`country_code`,NAME ";
    	List<Object[]> list=psiProductDao.findBySql(sql,new Parameter(warehouseId));
    	for (Object[] obj: list) {
			map.put(obj[1].toString()+"_"+obj[0].toString(), Integer.parseInt(obj[2].toString()));
		}
    	return map;
    }

	
	public List<PsiSku> getProduct(String selCountry,String accountName){
		List<PsiSku> skuList=Lists.newArrayList();
	/*	String sql="SELECT a.country,a.asin,a.sku,p.`product_name`,p.`color`,SUM(a.`fulfillable_quantity`) quantity  "+
				" FROM psi_sku p left JOIN psi_inventory_fba AS a  ON a.`country`=p.`country` AND a.`sku`=p.sku AND p.`del_flag`='0' "+
				" WHERE  a.`data_date`= (SELECT MAX(a.`data_date`) FROM psi_inventory_fba AS a ) "+
				" AND a.`fulfillable_quantity`>0  AND  a.country IN :p1 and a.asin is not null and a.sku is not null GROUP BY a.country,a.asin,a.sku,p.`product_name`,p.`color` HAVING quantity>0 ";
		*/
		String sql1 = "SELECT a.`data_date` FROM psi_inventory_fba AS a where a.country in :p1 and a.account_name like :p2 ORDER BY a.`data_date` DESC LIMIT 1";
		List<Date> dates=Lists.newArrayList();
		if("de,fr,it,es,uk".contains(selCountry)){
			dates=this.psiSkuDao.findBySql(sql1,new Parameter(Sets.newHashSet("de","fr","it","es","uk"),accountName.split("_")[0]+"%"));
		}else{
			dates=this.psiSkuDao.findBySql(sql1,new Parameter(Sets.newHashSet(selCountry),accountName+"%"));
		}
		if(dates!=null&&dates.size()>0){
			Date date  = dates.get(0);
			String sql=" SELECT p.country,p.asin,p.sku,p.`product_name`,p.`color`,SUM(a.`fulfillable_quantity`) quantity  "+
					" FROM psi_sku p LEFT JOIN psi_inventory_fba AS a  ON a.`account_name`=p.`account_name` AND a.`sku`=p.sku AND p.`del_flag`='0'  "+
					" WHERE  a.`data_date`= :p1 and p.`product_name`!='Inateck other' "+
					" AND  a.country IN :p2 and a.account_name like :p3 AND p.asin IS NOT NULL AND p.sku IS NOT NULL AND p.sku NOT LIKE '%local%'  GROUP BY p.country,p.asin,p.sku,p.`product_name`,p.`color` ";
					if("de,fr,it,es,uk".contains(selCountry)){
						Map<String, String> powerMap=getHasPowerByName();
						Set<String>   asinList=saleReportService.getPanEuProductAsin();
						List<Object[]> list=psiSkuDao.findBySql(sql,new Parameter(date,Sets.newHashSet("de","fr","it","es","uk"),accountName.split("_")[0]+"%"));
						Map<String,Integer> tempMap=Maps.newHashMap();
						Map<String,Integer> deStock=findNewQuantityByCn(19);
						for (Object[] obj: list) {
							String country=obj[0].toString();
							String asin=obj[1].toString();
							String sku=obj[2].toString();
							String name=obj[3].toString();
							String color=(obj[4]==null?"":obj[4].toString());
							Integer quantity=Integer.parseInt(obj[5].toString());
							String pname=name;
							if(StringUtils.isNotBlank(color)){
								pname=name+"_"+color;
							}
							
							if(asinList.contains(asin)){
								Integer tempQuantity=(tempMap.get(asin+","+sku+","+name+","+color)==null?0:tempMap.get(asin+","+sku+","+name+","+color));
								tempMap.put(asin+","+sku+","+name+","+color, quantity+tempQuantity);
							}else{
								PsiSku psiSku=new PsiSku(sku, asin,country,color,name,quantity);
								Integer localStock=0;
								
                                if("0".equals(powerMap.get(pname))){//0:不带电
                                	if(deStock.get(pname+"_de")!=null){
                                		localStock+=deStock.get(pname+"_de");
    								}
                                	if(deStock.get(pname+"_fr")!=null){
                                		localStock+=deStock.get(pname+"_fr");
    								}
                                	if(deStock.get(pname+"_it")!=null){
                                		localStock+=deStock.get(pname+"_it");
    								}
                                	if(deStock.get(pname+"_es")!=null){
                                		localStock+=deStock.get(pname+"_es");
    								}
                                	if(deStock.get(pname+"_uk")!=null){
                                		localStock+=deStock.get(pname+"_uk");
    								}
								}else{
									if("uk".equals(country)){
										localStock=deStock.get(pname+"_uk");
									}else{
										if(deStock.get(pname+"_de")!=null){
	                                		localStock+=deStock.get(pname+"_de");
	    								}
	                                	if(deStock.get(pname+"_fr")!=null){
	                                		localStock+=deStock.get(pname+"_fr");
	    								}
	                                	if(deStock.get(pname+"_it")!=null){
	                                		localStock+=deStock.get(pname+"_it");
	    								}
	                                	if(deStock.get(pname+"_es")!=null){
	                                		localStock+=deStock.get(pname+"_es");
	    								}
									}
								}
                                psiSku.setProductId(localStock);
								skuList.add(psiSku);
							}
						}
						if(tempMap!=null&&tempMap.size()>0){
							for (Map.Entry<String, Integer> entry: tempMap.entrySet()) {
								String key = entry.getKey();
								String[] arr=key.split(",");
								String asin=arr[0];
								String sku=arr[1];
								String name=arr[2];
								String color="";
								try{
									color=arr[3];
								}catch(Exception e){}
								Integer localStock=0;
								String pname=name;
								if(StringUtils.isNotBlank(color)){
									pname=name+"_"+color;
								}
									if(deStock.get(pname+"_de")!=null){
                                		localStock+=deStock.get(pname+"_de");
    								}
                                	if(deStock.get(pname+"_fr")!=null){
                                		localStock+=deStock.get(pname+"_fr");
    								}
                                	if(deStock.get(pname+"_it")!=null){
                                		localStock+=deStock.get(pname+"_it");
    								}
                                	if(deStock.get(pname+"_es")!=null){
                                		localStock+=deStock.get(pname+"_es");
    								}
                                	if(deStock.get(pname+"_uk")!=null){
                                		localStock+=deStock.get(pname+"_uk");
    								}
								PsiSku psiSku=new PsiSku(sku, asin,"eu",color,name,entry.getValue());
								psiSku.setProductId(localStock);
								skuList.add(psiSku);
							}
						}
					}else{
						List<Object[]> list=psiSkuDao.findBySql(sql,new Parameter(date,Sets.newHashSet(selCountry),accountName.split("_")[0]+"%"));
						Map<String,Integer> stockMap=Maps.newHashMap();
						if(selCountry.contains("com")){
							stockMap=findNewQuantityByCn(120);
						}else if(selCountry.contains("jp")){
							stockMap=findNewQuantityByCn(147);
						}
						for (Object[] obj: list) {
							String country=obj[0].toString();
							String asin=obj[1].toString();
							String sku=obj[2].toString();
							String name=obj[3].toString();
							String color=(obj[4]==null?"":obj[4].toString());
							Integer quantity=Integer.parseInt(obj[5].toString());
							PsiSku psiSku=new PsiSku(sku, asin,country,color,name,quantity);
							if(selCountry.contains("com")||selCountry.contains("jp")){
								Integer localStock=0;
								String pname=name;
								if(StringUtils.isNotBlank(color)){
									pname=name+"_"+color;
								}
								if(stockMap!=null&&stockMap.get(pname+"_"+selCountry)!=null){
									localStock=stockMap.get(pname+"_"+selCountry);
								}
								psiSku.setProductId(localStock);
							}
							skuList.add(psiSku);
						}
					}
			
		}
		
		return skuList;
	}
	
	
	/**
	 * 查询所有产品的sku，包括一个产品多个sku
	 */
	@Transactional(readOnly = false)
	public void clearDirtySku(){
		String sql = "SELECT CONCAT(a.country,'_',a.sku) FROM psi_sku a WHERE a.`del_flag` = '0' GROUP BY a.`country`,a.`sku` HAVING COUNT(1)>1";
		List<Object> list = psiSkuDao.findBySql(sql);
		if(list.size()>0){
			sql = "SELECT CONCAT(a.country,'_',a.sku),a.`id`,a.`use_barcode` FROM psi_sku a WHERE CONCAT(a.country,'_',a.sku) IN :p1";
			List<Object[]> list1 = psiSkuDao.findBySql(sql,new Parameter(list));
			Map<String,List<Object[]>> map = Maps.newHashMap();
			List<Object> dels = Lists.newArrayList();
			for (Object[] objects : list1) {
				String key = objects[0].toString();
				dels.add(objects[1]);
				List<Object[]> data = map.get(key);
				if(data==null){
					data = Lists.newArrayList();
					map.put(key, data);
				}
				data.add(objects);
			}
			for (List<Object[]> lists : map.values()) {
				boolean flag = false;
				for (Object[] objects : lists) {
					if("1".equals(objects[2].toString())){
						dels.remove(objects[1]);
						flag = true;
						break;
					}
				}
				if(!flag){
					dels.remove(lists.get(0)[1]);
				}
			}
			if(dels.size()>0){
				sql = "UPDATE psi_sku a SET a.`del_flag` = '1' WHERE a.`id` IN :p1";
				psiSkuDao.updateBySql(sql, new Parameter(dels));
			}
		}
		sql = "UPDATE amazoninfo_product2 a , psi_sku b SET b.`asin` = a.`asin` WHERE BINARY(a.`sku`) = b.`sku` AND a.`country` = b.`country` AND b.`del_flag` = '0' AND a.`asin` != b.`asin` AND a.`asin` IS NOT NULL AND a.`asin`!='' ";
		psiSkuDao.updateBySql(sql,null);
	}
	
	
	//查询所有产品的sku，包括一个产品多个sku 包含为匹配的sku
	public Map<String,String> findSkusByFbaUpdate(String country){
		/*String hid = "";
		if("CN".equals(hourseCode)){
			hid = "21";
		}else if("DE".equals(hourseCode)){
			hid = "19";
		}else if("US".equals(hourseCode)){
			hid = "120";
		}*/
		String sql = "SELECT DISTINCT a.sku ,CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END,'[',a.`sku`,']') AS NAME  FROM psi_sku AS a WHERE   a.`country` = :p1 AND a.`del_flag` = '0' AND (a.`use_barcode`='1' OR a.`sku` IN (SELECT DISTINCT b.sku FROM psi_inventory AS b  WHERE  b.`country_code` = :p1 ))";
		//String sql = "SELECT a.`sku`,CONCAT(a.`product_name`,CASE WHEN a.`color_code`!='' THEN CONCAT ('_',a.`color_code`) ELSE '' END,'[',a.`sku`,']') AS NAME FROM psi_inventory a WHERE a.`country_code` = :p1 AND a.`warehouse_id` = :p2 ";
		List<Object> list = psiSkuDao.findBySql(sql,new Parameter(country));
		Map<String,String> rs = Maps.newLinkedHashMap();
		for (Object object : list) {
			Object[]objs = (Object[])object;
			rs.put(objs[0].toString(),objs[1].toString());
		}
		return rs;
	}
	
	//查询所有产品的sku，包括一个产品多个sku 包含为匹配的sku
		public Map<String,String> findSkus(String country){
			String sql = "SELECT a.`sku`,CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END,'[',a.`sku`,']') AS NAME FROM psi_sku a WHERE  a.`sku` !='' AND a.`country` =:p1 AND a.`del_flag`='0'";
			List<Object> list = psiSkuDao.findBySql(sql,new Parameter(country));
			Map<String,String> rs = Maps.newLinkedHashMap();
			for (Object object : list) {
				Object[]objs = (Object[])object;
				rs.put(objs[0].toString(),objs[1].toString());
			}
			if(rs.keySet().size()>0){
				sql = "SELECT DISTINCT a.`sku` FROM amazoninfo_product2 a WHERE a.`sku` !='' AND a.`country` =:p1 and a.`active`='1' and a.`sku` not in :p2";
				list = psiSkuDao.findBySql(sql,new Parameter(country,rs.keySet()));
			}else{
				sql = "SELECT DISTINCT a.`sku` FROM amazoninfo_product2 a WHERE a.`sku` !='' AND a.`country` =:p1 and a.`active`='1'";
				list = psiSkuDao.findBySql(sql,new Parameter(country));
			}
			for (Object object : list) {
				rs.put(object.toString(),"Unknown["+object.toString()+"]");
			}
			return rs;
		}
	
	//查询所有产品的sku，包括一个产品多个sku 包含为匹配的sku
	public Map<String,String> findProductNameWithSku(String country){
		String temp = "";
		List<String> countrys = Lists.newArrayList();
		if(StringUtils.isNotEmpty(country)){
			temp = " AND a.`country` in :p1 ";
			if("eu".equals(country)){
				countrys.add("de");
				countrys.add("it");
				countrys.add("es");
				countrys.add("fr");
				countrys.add("uk");
			}else{
				countrys.add(country);
			}
		}
		String sql = "SELECT DISTINCT a.`sku`,CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) AS NAME FROM psi_sku a WHERE  a.`sku` !='' AND a.`product_name` !='inateck other' AND a.`product_name` !='inateck old' AND a.`del_flag`='0' "+temp;
		List<Object> list = null;
		if(countrys.size()>0){
			list = psiSkuDao.findBySql(sql,new Parameter(countrys));
		}else{
			list = psiSkuDao.findBySql(sql);
		}
		Map<String,String> rs = Maps.newLinkedHashMap();
		for (Object object : list) {
			Object[]objs = (Object[])object;
			rs.put(objs[0].toString(),objs[1].toString());
		}
		return rs;
	}
	
	public Map<String,Object[]> findProductNameWithSku(){
		String sql = "SELECT DISTINCT a.`sku`,CONCAT(a.`product_name`,CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) AS NAME,p.brand FROM psi_sku a " +
				" join  psi_product p ON a.`product_id` = p.id WHERE  a.`sku` !=''  AND a.`del_flag`='0' ";
		List<Object[]> list = psiSkuDao.findBySql(sql);
		Map<String,Object[]> rs = Maps.newLinkedHashMap();
		for (Object[] object : list) {
			Object[] ojt={object[1],object[2]};
			rs.put(object[0].toString(),ojt);
		}
		return rs;
	}
	
	//查询所有产品的sku，包括一个产品多个sku 包含为匹配的sku
	public List<String> findProductSkusByName(String country,String productName,String color){
		String sql = "SELECT DISTINCT	a.`sku` FROM psi_sku a WHERE  a.`sku` !='' AND a.`country` in :p1 AND a.`del_flag`='0' AND a.`product_name` = :p2 AND a.`color` = :p3";
		List<String> countrys = Lists.newArrayList();
		if("eu".equals(country)){
			countrys.add("de");
			countrys.add("it");
			countrys.add("es");
			countrys.add("fr");
			countrys.add("uk");
		}else{
			countrys.add(country);
		}
		List<String> list = psiSkuDao.findBySql(sql,new Parameter(countrys,productName,color==null?"":color));
		return list;
	}
	
	//查询所有产品的sku，包括一个产品多个sku 包含为匹配的sku
	public List<String> findProductSkusByName(String productName){
		String sql = "SELECT DISTINCT	a.`sku` FROM psi_sku a WHERE  a.`sku` !='' AND a.`del_flag`='0' AND a.`product_name` = :p1 ";
		List<String> list = psiSkuDao.findBySql(sql,new Parameter(productName));
		return list;
	}
	
	//查询所有产品的sku，包括一个产品多个sku 包含为匹配的sku
	public List<String> findProductSkusByName(String productName,String country){
		String temp ="";
		if(StringUtils.isNotEmpty(country)){
			temp = "and a.`country`='"+country+"'";
		}
		productName = productName.replace("_", "\\_");
		productName = "%"+productName+"%";
		String sql = "SELECT DISTINCT a.`sku` FROM psi_sku a WHERE  a.`sku` !='' AND a.`del_flag`='0'  AND CONCAT(a.`product_name`,CASE WHEN a.`color`='' THEN '' ELSE '_' END,a.`color`) like :p1 "+temp;
		List<String> list = psiSkuDao.findBySql(sql,new Parameter(productName));
		return list;
	}
		
	/**
	 * 查询同一产品、同一颜色、部分平台所有的sku
	 */
	public List<PsiSku> getSkus(String name,String color){
		DetachedCriteria dc = psiSkuDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.add(Restrictions.eq("color", color));
		dc.add(Restrictions.eq("productName", name));
		List<PsiSku> rs = psiSkuDao.find(dc);
		return rs;
	}
	
	/**
	 * 查询单一产品的多个sku
	 */
	public List<PsiSku> getSkus(String name,String country,String color){
		DetachedCriteria dc = psiSkuDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.add(Restrictions.eq("color", color));
		dc.add(Restrictions.eq("country", country));
		dc.add(Restrictions.eq("productName", name));
		List<PsiSku> rs = psiSkuDao.find(dc);
		return rs;
	}
	
	
	public PsiSku getSkuBySku(String sku,String useBarcode){
		DetachedCriteria dc = psiSkuDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.add(Restrictions.eq("sku", sku));
		dc.add(Restrictions.eq("useBarcode", useBarcode));
		List<PsiSku> rs = psiSkuDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}else{
			return null;
		}
		
	}
	
	//对条码没要求，只要求获得sku
	public PsiSku getSkuBySku(String sku){
		DetachedCriteria dc = psiSkuDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.add(Restrictions.eq("sku", sku));
		List<PsiSku> rs = psiSkuDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}else{
			return null;
		}
		
	}
	
	
		public PsiSku getProductByAsin(String asin){
			DetachedCriteria dc = psiSkuDao.createDetachedCriteria();
			dc.add(Restrictions.eq("delFlag", "0"));
			dc.add(Restrictions.eq("asin", asin));
			List<PsiSku> rs = psiSkuDao.find(dc);
			if(rs.size()>0){
				return rs.get(0);
			}else{
				return null;
			}
			
		}
		
		public PsiSku getProductByAsin2(String asin){
			DetachedCriteria dc = psiSkuDao.createDetachedCriteria();
			dc.add(Restrictions.eq("delFlag", "0"));
			dc.add(Restrictions.eq("asin", asin));
			dc.add(Restrictions.ne("productName","Inateck Old"));
			List<PsiSku> rs = psiSkuDao.find(dc);
			if(rs.size()>0){
				return rs.get(0);
			}else{
				return null;
			}
		}
		
		
		public PsiSku getProductByAsin(String asin,String country){
			DetachedCriteria dc = psiSkuDao.createDetachedCriteria();
			dc.add(Restrictions.eq("delFlag", "0"));
			dc.add(Restrictions.eq("asin", asin));
			dc.add(Restrictions.ne("productName","Inateck Old"));
			if("de".equals(country)){
				dc.add(Restrictions.in("country",Sets.newHashSet("de","fr","it","es","uk")));
			}else{
				dc.add(Restrictions.eq("country",country));
			}
			List<PsiSku> rs = psiSkuDao.find(dc);
			if(rs.size()>0){
				return rs.get(0);
			}else{
				return null;
			}
		}
		
		public String getAccountByAsin(String asin,String country){
			String sql="select distinct account_name from psi_sku p where country=:p1 and asin=:p2 and del_flag='0'";
			List<String> rs = psiSkuDao.findBySql(sql,new Parameter(country,asin));
			if(rs!=null&&rs.size()==1){
				return rs.get(0);
			}
			return null;
		}
	
	public PsiSku getProductBySku(String sku,String country){
		DetachedCriteria dc = psiSkuDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.add(Restrictions.eq("sku", sku));
		Set<String> countrys = Sets.newHashSet(country);
		if(StringUtils.isNotEmpty(country)){
			if("de,fr,it,es,uk".contains(country)){
				countrys.add("de");
				countrys.add("fr");
				countrys.add("it");
				countrys.add("es");
				countrys.add("uk");
			}
			dc.add(Restrictions.in("country",countrys));
		}
		List<PsiSku> rs = psiSkuDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}else{
			return null;
		}
	}
	
	@Transactional(readOnly = false)
	public void clearItemProductInfo(String sku){
		String sql = "UPDATE  amazoninfo_orderitem a  SET a.`product_name`= NULL,a.`color` = NULL  WHERE a.`sellersku` = :p1";
		psiSkuDao.updateBySql(sql, new Parameter(sku));
	}
	
	@Transactional(readOnly = false)
	public void setItemProductInfo(String sku,String productName,String color){
		String sql = "UPDATE  amazoninfo_orderitem a  SET a.`product_name`= :p1 ,a.`color` = :p2  WHERE a.`sellersku` = :p3";
		psiSkuDao.updateBySql(sql, new Parameter(productName,color,sku));
		
		String sql2 = "UPDATE  amazoninfo_ebay_orderitem a  SET a.`title`= :p1  WHERE a.`sku` = :p2 and a.id like '%_amazon' ";
		psiSkuDao.updateBySql(sql2, new Parameter(productName+(StringUtils.isNotBlank(color)?("_"+color):""),sku));
	}
	
	@Transactional(readOnly = false)
	public void updateEbayItem(String sku,String productName){
		String sql = "UPDATE  amazoninfo_ebay_orderitem a  SET a.`title`= :p1  WHERE a.`sku` = :p2 and a.id like '%_ebay' ";
		psiSkuDao.updateBySql(sql, new Parameter(productName,sku));
	}
	
	/**
	 *查询装箱数 
	 * 
	 */
	public Integer findPackQuantity(Integer productId){
		String sql ="SELECT pack_quantity FROM psi_product WHERE id=:p1";
		List<Integer> list=this.psiProductDao.findBySql(sql, new Parameter(productId));
		if(list!=null&&list.size()==1){
			return list.get(0).intValue();
		}
		return null;
	}
	
	public PsiProduct findProductByProductName(String productName){
		String temp = productName;
		if(productName.indexOf("_")>0){
			temp = productName.substring(0,productName.lastIndexOf("_"));
		}
		DetachedCriteria dc = psiProductDao.createDetachedCriteria();
		dc.add(Restrictions.sqlRestriction("CONCAT(brand,' ',model) = '"+temp+"'"));
		dc.add(Restrictions.eq("delFlag", "0"));
		List<PsiProduct> list = psiProductDao.find(dc);
		if(list.size()==1){
			return list.get(0);
		}
		return null;
	}
	
	public PsiBarcode getBarcodeByProCouCol(Integer productId,String country,String color,String account) {
		DetachedCriteria dc = barcodeDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.add(Restrictions.eq("psiProduct.id", productId));
		dc.add(Restrictions.eq("productPlatform", country));
		dc.add(Restrictions.eq("productColor", color));
		if(StringUtils.isNotBlank(account)){
			dc.add(Restrictions.eq("accountName",account));
		}
		List<PsiBarcode> barcodes=barcodeDao.find(dc);
		if(barcodes.size()>=1){
			 return barcodes.get(0);
	    }else{
			 return null;
		}
	}
	
	public PsiBarcode getBarcodeByProCouCol(String productName,String country,String color,String account) {
		DetachedCriteria dc = barcodeDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.add(Restrictions.eq("productName", productName));
		dc.add(Restrictions.eq("productPlatform", country));
		dc.add(Restrictions.eq("productColor", color));
		if(StringUtils.isNotBlank(account)){
			dc.add(Restrictions.eq("accountName",account));
		}
		List<PsiBarcode> barcodes=barcodeDao.find(dc);
		if(barcodes.size()>=1){
			 return barcodes.get(0);
	    }else{
			 return null;
		}
	}
   

	public Map<String,Integer> findPackQuantityMap(){
		Map<String,Integer> map = Maps.newHashMap();
		String sql ="SELECT CONCAT(p.`brand`,' ',p.model),pack_quantity FROM psi_product  AS p";
		List<Object[]> list=this.psiProductDao.findBySql(sql);
		for(Object[] object:list){
			map.put(object[0].toString(), Integer.parseInt(object[1].toString()));
		}
		
		return map;
	}
	
	
	public Map<String,String> findProductTypeMap(){
		Map<String,String> map = Maps.newHashMap();
		String sql ="SELECT CONCAT(p.`brand`,' ',p.model),p.`color`,p.`TYPE` FROM psi_product  AS p  WHERE p.`del_flag`='0'  ";
		List<Object[]> list=this.psiProductDao.findBySql(sql);
		for(Object[] object:list){
			String color =  object[1].toString();
			if(StringUtils.isNotEmpty(color)){
				for(String colorStr:color.split(",")){
					map.put(object[0].toString()+"_"+colorStr, object[2].toString());
				}
			}else{
				map.put(object[0].toString(), object[2].toString());
			}
		}
		return map;
	}
	
	
	public String findProductTypeByProductName(String productName){
		if (StringUtils.isEmpty(productName)) {
			return null;
		}
		String sql ="SELECT p.`TYPE` FROM psi_product  AS p  WHERE CONCAT(p.`brand`,' ',p.model)=:p1 ORDER BY id DESC LIMIT 1 ";
		List<Object> list=this.psiProductDao.findBySql(sql, new Parameter(productName.split("_")[0]));
		if (list != null && list.size()>0 && list.get(0)!=null) {
			return list.get(0).toString();
		}
		return null;
	}
	
	/**
	 * 获取指定月份的产品和类型对应关系
	 * @param month
	 * @return
	 */
	public Map<String,String> findProductTypeMap(String month){
		//当月按实时关系处理
		if (StringUtils.isEmpty(month) || month.equals(new SimpleDateFormat("yyyyMM").format(new Date()))) {
			return findProductTypeMap();
		}
		Map<String,String> map = Maps.newHashMap();
		String sql ="SELECT t.`product_name`,t.`type` FROM `amazoninfo_report_month_type` t WHERE t.`month`=:p1 GROUP BY t.`product_name` ";
		List<Object[]> list=this.psiProductDao.findBySql(sql, new Parameter(month));
		for(Object[] object: list){
			map.put(object[0].toString(), object[1].toString());
		}
		return map;
	}
	
	/**
	 *产品经理，产品类型关系 
	 */
	public Map<String,String> findManagerProductTypeMap(){
		Map<String,String> map = Maps.newHashMap();
		//String sql ="SELECT b.`value`,a.`login_name` FROM sys_user AS a,sys_dict AS b, psi_product_manage_group AS c WHERE a.`id`=c.`user_id` AND b.id=c.`dict_id` AND a.`del_flag`='0' AND b.`del_flag`='0'";
		
		String sql ="SELECT  t.value,GROUP_CONCAT(distinct r.`login_name`) FROM ( "+
			"	SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(a.user_id,',',b.help_topic_id+1),',',-1) userId,dict_id  "+
			"	FROM psi_product_manage_group a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.user_id) - LENGTH(REPLACE(a.user_id,',',''))+1)  "+
			"	) d JOIN psi_product_type_group g ON d.dict_id=g.id  "+
			"	JOIN sys_dict t ON g.`dict_id`=t.id AND t.`del_flag`='0' AND  t.`type`='product_type'  "+
			"	JOIN sys_user r ON r.id=d.userId AND r.del_flag='0' GROUP BY t.value ";
		List<Object[]> list=this.psiProductDao.findBySql(sql);
		for(Object[] object:list){
			map.put(object[0].toString(), object[1].toString());
		}
		return map;
	}
	
	/**
	 *根据产品id  获得产品经理的邮箱地址
	 */
	public Set<String>  findMangerByProductIds(Set<Integer> productIds){
		Set<String> set = Sets.newHashSet();
		//String sql="SELECT DISTINCT a.`email` FROM sys_user AS a,sys_dict AS b, psi_product_manage_group AS c,psi_product AS d WHERE d.`TYPE`=b.`value` AND a.`id`=c.`user_id` AND b.id=c.`dict_id` AND a.`del_flag`='0' AND b.`del_flag`='0' AND d.id IN :p1";
		
		String sql="SELECT  DISTINCT r.email FROM ( "+
			"	SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(a.user_id,',',b.help_topic_id+1),',',-1) userId,dict_id "+
			"	FROM psi_product_manage_group a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.user_id) - LENGTH(REPLACE(a.user_id,',',''))+1) "+
			"	) d JOIN psi_product_type_group g ON d.dict_id=g.id "+
			"	JOIN sys_dict t ON g.`dict_id`=t.id AND t.`del_flag`='0' AND  t.`type`='product_type' "+
			"	JOIN psi_product p ON p.type=t.value AND p.del_flag='0' "+
			"	JOIN sys_user r ON r.id=d.userId AND r.del_flag='0' WHERE p.id IN :p1 ";
				
		List<String> list=this.psiProductDao.findBySql(sql,new Parameter(productIds));
		for(String email:list){
			set.add(email);
		}
		return set;
	}
	
	/**
	 *根据产品名  获得产品经理的邮箱地址
	 */
	public Map<String,Set<String>>  findMangerEmailByProductNames(){
		Map<String,Set<String>> rs = Maps.newHashMap();
		//String sql="SELECT DISTINCT a.`email`,CONCAT(d.`brand`,' ',d.`model`) FROM sys_user AS a,sys_dict AS b, psi_product_manage_group AS c,psi_product AS d WHERE d.`TYPE`=b.`value` AND a.`id`=c.`user_id` AND b.id=c.`dict_id` AND a.`del_flag`='0' AND b.`del_flag`='0' ";
		String sql="SELECT DISTINCT r.email,CONCAT(p.`brand`,' ',p.`model`) FROM ( "+
			"	SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(a.user_id,',',b.help_topic_id+1),',',-1) userId,dict_id  "+
			"	FROM psi_product_manage_group a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.user_id) - LENGTH(REPLACE(a.user_id,',',''))+1)  "+
			"	) d JOIN psi_product_type_group g ON d.dict_id=g.id "+
			"	JOIN sys_dict t ON g.`dict_id`=t.id AND t.`del_flag`='0' AND  t.`type`='product_type' "+
			"	JOIN psi_product p ON p.type=t.value AND p.del_flag='0' "+
			"	JOIN sys_user r ON r.id=d.userId AND r.del_flag='0' ";
		
		List<Object[]> list=this.psiProductDao.findBySql(sql);
		for(Object[] obj:list){
			String email = obj[0].toString();
			String name = obj[1].toString();
			Set<String> set = null;
			if(rs.get(name)==null){
				set=Sets.newHashSet();
			}else{
				set=rs.get(name);
			}
			set.add(email);
			rs.put(name, set);
		}
		return rs;
	}
	
	/**
	 *根据产品名  获得采购经理的邮箱地址
	 */
	public Map<String,Set<String>>  findPurchaseEmailByProductNames(){
		Map<String,Set<String>> rs = Maps.newHashMap();
		String sql="SELECT DISTINCT a.`email`,CONCAT(d.`brand`,' ',d.`model`) FROM sys_user AS a,sys_dict AS b, psi_product_purchase_group AS c,psi_product AS d WHERE d.`TYPE`=b.`value` AND a.`id`=c.`user_id` AND b.id=c.`dict_id` AND a.`del_flag`='0' AND b.`del_flag`='0' ";
		List<Object[]> list=this.psiProductDao.findBySql(sql);
		for(Object[] obj:list){
			String email = obj[0].toString();
			String name = obj[1].toString();
			Set<String> set = null;
			if(rs.get(name)==null){
				set=Sets.newHashSet();
			}else{
				set=rs.get(name);
			}
			set.add(email);
			rs.put(name, set);
		}
		return rs;
	}
	
	/**
	 *根据产品名  获得产品经理的id
	 */
	public Map<String,Set<String>>  findMangerByProductNames(){
		Map<String,Set<String>>  rs = Maps.newHashMap();
		//String sql="SELECT DISTINCT a.`id`,CONCAT(d.`brand`,' ',d.`model`) FROM sys_user AS a,sys_dict AS b, psi_product_manage_group AS c,psi_product AS d WHERE d.`TYPE`=b.`value` AND a.`id`=c.`user_id` AND b.id=c.`dict_id` AND a.`del_flag`='0' AND b.`del_flag`='0' ";
		
		String sql="SELECT DISTINCT r.id,CONCAT(p.`brand`,' ',p.`model`) FROM ( "+
				"	SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(a.user_id,',',b.help_topic_id+1),',',-1) userId,dict_id  "+
				"	FROM psi_product_manage_group a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.user_id) - LENGTH(REPLACE(a.user_id,',',''))+1)  "+
				"	) d JOIN psi_product_type_group g ON d.dict_id=g.id "+
				"	JOIN sys_dict t ON g.`dict_id`=t.id AND t.`del_flag`='0' AND  t.`type`='product_type' "+
				"	JOIN psi_product p ON p.type=t.value AND p.del_flag='0' "+
				"	JOIN sys_user r ON r.id=d.userId AND r.del_flag='0' ";
			
		List<Object[]> list=this.psiProductDao.findBySql(sql);
		for(Object[] obj:list){
			String userId = obj[0].toString();
			String name = obj[1].toString();
			Set<String> set=rs.get(name);
			if(set==null){
				set=Sets.newHashSet();
				rs.put(name, set);
			}
			set.add(userId);
		}
		return rs;
	}
	
	/**
	 *根据产品名  获得产品经理的id
	 */
	public List<String>   findMangerByProductName(String productName){
		String userId="";
		//String sql="SELECT DISTINCT a.`id` FROM sys_user AS a,sys_dict AS b, psi_product_manage_group AS c,psi_product AS d WHERE d.`TYPE`=b.`value` AND a.`id`=c.`user_id` AND b.id=c.`dict_id` AND a.`del_flag`='0' AND b.`del_flag`='0' AND CONCAT(d.`brand`,' ',d.`model`)=:p1 ";
		
		String sql="SELECT DISTINCT r.id FROM ( "+
				"	SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(a.user_id,',',b.help_topic_id+1),',',-1) userId,dict_id  "+
				"	FROM psi_product_manage_group a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.user_id) - LENGTH(REPLACE(a.user_id,',',''))+1)  "+
				"	) d JOIN psi_product_type_group g ON d.dict_id=g.id "+
				"	JOIN sys_dict t ON g.`dict_id`=t.id AND t.`del_flag`='0' AND  t.`type`='product_type' "+
				"	JOIN psi_product p ON p.type=t.value AND p.del_flag='0' "+
				"	JOIN sys_user r ON r.id=d.userId AND r.del_flag='0' where CONCAT(p.`brand`,' ',p.`model`)=:p1 ";
			
		return this.psiProductDao.findBySql(sql,new Parameter(productName));
	}
	
	/**
	 *根据产品名  获得产品经理的id
	 */
	public  String findMangerMailByProductName(String productName){
		//String sql="SELECT DISTINCT  a.`email` FROM sys_user AS a,sys_dict AS b, psi_product_manage_group AS c,psi_product AS d WHERE d.`TYPE`=b.`value` AND a.`id`=c.`user_id` AND b.id=c.`dict_id` AND a.`del_flag`='0' AND b.`del_flag`='0' AND CONCAT(d.`brand`,' ',d.`model`)=:p1 ";
		
		String sql="SELECT  GROUP_CONCAT(DISTINCT r.email) FROM ( "+
				"	SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(a.user_id,',',b.help_topic_id+1),',',-1) userId,dict_id  "+
				"	FROM psi_product_manage_group a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.user_id) - LENGTH(REPLACE(a.user_id,',',''))+1)  "+
				"	) d JOIN psi_product_type_group g ON d.dict_id=g.id "+
				"	JOIN sys_dict t ON g.`dict_id`=t.id AND t.`del_flag`='0' AND  t.`type`='product_type' "+
				"	JOIN psi_product p ON p.type=t.value AND p.del_flag='0' "+
				"	JOIN sys_user r ON r.id=d.userId AND r.del_flag='0' where CONCAT(p.`brand`,' ',p.`model`)=:p1 ";
		
		List<String> list=this.psiProductDao.findBySql(sql,new Parameter(productName));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	
	/**
	 *采购经理，产品类型关系 
	 */
	public Map<String,String> findPurchaseProductTypeMap(){
		Map<String,String> map = Maps.newHashMap();
		String sql ="SELECT b.`value`,a.`login_name` FROM sys_user AS a,sys_dict AS b, psi_product_purchase_group AS c WHERE a.`id`=c.`user_id` AND b.id=c.`dict_id` AND a.`del_flag`='0' AND b.`del_flag`='0'";
		List<Object[]> list=this.psiProductDao.findBySql(sql);
		for(Object[] object:list){
			map.put(object[0].toString(), object[1].toString());
		}
		return map;
	}
	
	/**
	 *产品id，采购经理关系 
	 */
	public Set<String>  findPurchaseByProductIds(Set<Integer> productIds){
		Set<String> set = Sets.newHashSet();
		String sql="SELECT DISTINCT a.`email` FROM sys_user AS a,sys_dict AS b, psi_product_purchase_group AS c,psi_product AS d WHERE d.`TYPE`=b.`value` AND a.`id`=c.`user_id` AND b.id=c.`dict_id` AND a.`del_flag`='0' AND b.`del_flag`='0' AND d.id IN :p1";
		List<String> list=this.psiProductDao.findBySql(sql,new Parameter(productIds));
		for(String email:list){
			set.add(email);
		}
		return set;
	}
	
	/**
	 *查询产品对应的采购经理 
	 */
	public Map<String,String>  findPurchaseEmailProductName(){
		Map<String,String> rs = Maps.newHashMap();
		String sql="SELECT DISTINCT CONCAT(d.`brand`,' ',d.`model`),a.`email` FROM sys_user AS a,sys_dict AS b, psi_product_purchase_group AS c,psi_product AS d WHERE d.`TYPE`=b.`value` AND a.`id`=c.`user_id` AND b.id=c.`dict_id` AND a.`del_flag`='0' AND b.`del_flag`='0' ";
		List<Object[]> list=this.psiProductDao.findBySql(sql);
		for(Object[] obj:list){
			rs.put(obj[0].toString(), obj[1].toString());
		}
		return rs;
	}
	
	
	/**
	 * 
	 *  根据产品经理的id   获取产品名字
	 */
	public List<String>  findProductByManagerId(String userId){
		//String sql="SELECT CONCAT(d.`brand`,' ',d.`model`) FROM sys_user AS a,sys_dict AS b, psi_product_manage_group AS c,psi_product AS d WHERE d.`TYPE`=b.`value` AND a.`id`=c.`user_id` AND b.id=c.`dict_id` AND a.`del_flag`='0' AND b.`del_flag`='0'  AND a.`id`=:p1 ";
		
		String sql="SELECT  distinct CONCAT(p.`brand`,' ',p.`model`) FROM ( "+
			"	SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(a.user_id,',',b.help_topic_id+1),',',-1) userId,dict_id  "+
			"	FROM psi_product_manage_group a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.user_id) - LENGTH(REPLACE(a.user_id,',',''))+1)   "+
			"	) d JOIN psi_product_type_group g ON d.dict_id=g.id  "+
			"	JOIN sys_dict t ON g.`dict_id`=t.id AND t.`del_flag`='0' AND  t.`type`='product_type'   "+
			"	JOIN psi_product p ON p.type=t.value AND p.del_flag='0'  "+
			"	JOIN sys_user r ON r.id=d.userId AND r.del_flag='0'  WHERE r.id=:p1  ";
		List<String> list=this.psiProductDao.findBySql(sql,new Parameter(userId));
		return list;
	}
	 
	
	
	/**
	 *查询装箱数 
	 * 
	 */
	public Map<Integer,Integer> findAllPackQuantity(){
		String sql ="SELECT a.`id`,a.`pack_quantity` FROM psi_product AS a  WHERE del_flag='0' ";
		List<Object[]> list=this.psiProductDao.findBySql(sql);
		Map<Integer,Integer> map = Maps.newHashMap();
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				Integer productId = Integer.parseInt(obj[0].toString());
				Integer packQuantity=Integer.parseInt(obj[1].toString());
				map.put(productId, packQuantity);
			}
		}
		return map;
	}
	
	
	
	public Map<String,String> findIsNewMap(){
		Map<String,String> map = Maps.newHashMap();
		String sql ="SELECT CONCAT(p.`brand`,' ',p.model),p.`is_new` FROM psi_product  AS p";
		List<Object[]> list=this.psiProductDao.findBySql(sql);
		for(Object[] object:list){
			map.put(object[0].toString(), object[1].toString().equals("0")?"普通品":"新品");
		}
		return map;
	}
	
	
	/**
	 *查询所有新品 
	 */
	 public Map<Integer,String> findNewMap(){
			Map<Integer,String> map = Maps.newHashMap();
			String sql ="SELECT p.`id`,CONCAT(p.`brand`,' ',p.model) FROM psi_product  AS p where p.`is_new`='1' and p.`del_flag`='0'";
			List<Object[]> list=this.psiProductDao.findBySql(sql);
			for(Object[] object:list){
				map.put(Integer.parseInt(object[0].toString()),object[1].toString());
			}
			return map;
		}
	 
	 
	 
	 /**
	  *查询所有新品 
	  */
	 public List<String> findNewProducts(){
			String sql ="SELECT (CASE WHEN a.`color`='' THEN CONCAT(a.`product_name`,'_',a.`country`) ELSE CONCAT(a.`product_name`,'_',a.`color`,'_',a.`country`) END) FROM psi_product_eliminate AS a WHERE a.`del_flag`='0' AND a.`is_new`='1'";
			List<String> list=this.psiProductDao.findBySql(sql);
			return list;
		}
	 
	 /**
	  *该产品是否为新品
	  */
	 public boolean isNewProduct(String proName,String color,String country){
			String sql ="SELECT a.`is_new` FROM psi_product_eliminate AS a WHERE a.`del_flag`='0' AND a.`product_name`=:p1 AND a.`color`=:p2 AND a.`country`=:p3 ";
			List<Object> list=this.psiProductDao.findBySql(sql,new Parameter(proName,color,country));
			return list!=null&&list.size()>0&&"1".equals(list.get(0)+"");
			
		}
	 
	 
	public List<String> findNoProductNameOrderItems(){
		String sql ="SELECT DISTINCT a.`sellersku` FROM amazoninfo_orderitem a WHERE (a.`product_name` IS NULL OR a.`product_name` ='') AND a.`sellersku` != ''";
		return this.psiProductDao.findBySql(sql);
	}
	
	
	public Map<String,String> getAllBandingSku(){
		String skuSql ="SELECT b.`product_name`,b.`country`,b.`color`,b.`sku` FROM psi_sku AS b WHERE b.`del_flag`='0' AND b.use_barcode='1'";
		List<Object[]> psiSkus=this.psiProductDao.findBySql(skuSql); 
		Map<String,String> skuMap = Maps.newHashMap();
		for(Object[] psiSku:psiSkus){
			String productName = psiSku[0].toString();
			String country = psiSku[1].toString();
			String color   = psiSku[2].toString();
			String sku     = psiSku[3].toString();
			String key=productName+","+country+","+color;
			skuMap.put(key, sku);
		}
		return skuMap;
	}
	
	public Map<String,String> getAllBandingSku2(){
		String skuSql ="SELECT b.`product_name`,b.`country`,b.`color`,b.`sku` FROM psi_sku AS b WHERE b.`del_flag`='0' AND b.use_barcode='1'";
		List<Object[]> psiSkus=this.psiProductDao.findBySql(skuSql); 
		Map<String,String> skuMap = Maps.newHashMap();
		for(Object[] psiSku:psiSkus){
			String productName = psiSku[0].toString();
			String country = psiSku[1].toString();
			String color   = psiSku[2].toString();
			String sku     = psiSku[3].toString();
			if(StringUtils.isNotEmpty(color)){
				productName=productName+"_"+color;
			}
			String key=productName+","+country;
			skuMap.put(key, sku);
		}
		return skuMap;
	}
	  
	/**
	 *key:sku   产品名字 
	 */
	public Map<String,String> getBandingSkuProduct(){
		String skuSql ="SELECT b.`product_name`,b.`country`,b.`color`,b.`sku` FROM psi_sku AS b WHERE b.`del_flag`='0' AND b.use_barcode='1'";
		List<Object[]> psiSkus=this.psiProductDao.findBySql(skuSql); 
		Map<String,String> skuMap = Maps.newHashMap();
		for(Object[] psiSku:psiSkus){
			String productName = psiSku[0].toString();
			String country = psiSku[1].toString();
			String color   = psiSku[2].toString();
			if(!"".equals(color)){
				productName+="_"+color;
			}
			String sku     = psiSku[3].toString();
			String value=productName+","+country;
			skuMap.put(sku,value);
		}
		return skuMap;
	}
	
	/**
	 *查询sku对应的产品名 
	 * 
	 */
	
	public Map<String,String> findAllProductNamesWithSku(){
		String sql = "SELECT DISTINCT a.`sku`,CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END) AS productName FROM psi_sku a WHERE  NOT(a.`product_name` LIKE '%other%' or a.product_name like '%Old%') AND a.`del_flag`='0'";
		List<Object[]> list = this.psiPartsDao.findBySql(sql);
		Map<String, String> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(),objects[1].toString());
		}
		return rs;
	}
	
	
	
	public Map<String,String> getAllBandingProductSku(){
		String skuSql ="SELECT b.`product_id`,b.`country`,b.`color`,b.`sku` FROM psi_sku AS b WHERE b.`del_flag`='0' AND b.use_barcode='1'";
		List<Object[]> psiSkus=this.psiProductDao.findBySql(skuSql); 
		Map<String,String> skuMap = Maps.newHashMap();
		for(Object[] psiSku:psiSkus){
			String productId = psiSku[0].toString();
			String country = psiSku[1].toString();
			String color   = psiSku[2].toString();
			String sku     = psiSku[3].toString();
			String key=productId+","+country+","+color;
			skuMap.put(key, sku);
		}
		
		return skuMap;
	}
	
	public Map<String,String> getBandingProductInfoSku(){
		String skuSql ="SELECT b.`product_id`,b.`country`,b.`color`,b.`sku`,b.`product_name` FROM psi_sku AS b WHERE b.`del_flag`='0' AND b.use_barcode='1'";
		List<Object[]> psiSkus=this.psiProductDao.findBySql(skuSql); 
		Map<String,String> skuMap = Maps.newHashMap();
		for(Object[] psiSku:psiSkus){
			String productId = psiSku[0].toString();
			String country = psiSku[1].toString();
			String color   = psiSku[2].toString();
			String sku     = psiSku[3].toString();
			String name    = psiSku[4].toString();
			String value   = productId+","+name+","+country+","+color;
			skuMap.put(sku, value);
		}
		return skuMap;
	}
	
	/**
	 *查询产品价格 
	 */
//	public Map<Integer,Float> getAllPrice(Integer supplierId,String currencyType){
//		String skuSql ="";
//		if("CNY".equals(currencyType)){
//			skuSql ="SELECT a.`product_id`,a.`rmb_price` FROM psi_product_supplier AS a,psi_product AS b WHERE a.`product_id`=b.`id` AND b.`del_flag`='0' AND a.`supplier_id`=:p1";
//		}else{
//			skuSql ="SELECT a.`product_id`,a.`price` FROM psi_product_supplier AS a,psi_product AS b WHERE a.`product_id`=b.`id` AND b.`del_flag`='0' AND a.`supplier_id`=:p1";
//		}
//		
//		List<Object[]> objects=this.psiProductDao.findBySql(skuSql,new Parameter(supplierId)); 
//		Map<Integer,Float> skuMap = Maps.newHashMap();
//		for(Object[] object:objects){
//			Integer id = Integer.parseInt(object[0].toString());
//			Float price =object[1]!=null?Float.parseFloat(object[1].toString()):null;
//			skuMap.put(id, price);
//		}
//		return skuMap;
//	}
	
	/**
	 *查询产品交货日期 
	 */
	public Map<Integer,String> getAllReceivedDate(Date date){
		String skuSql ="SELECT a.id,a.`produce_period` FROM psi_product AS a WHERE a.`del_flag`='0'";
		List<Object[]> periods=this.psiProductDao.findBySql(skuSql); 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<Integer,String> periodMap = Maps.newHashMap();
		for(Object[] period:periods){
			Integer id = Integer.parseInt(period[0].toString());
			Integer p =period[1]!=null?Integer.parseInt(period[1].toString()):0;
			
			periodMap.put(id, sdf.format(DateUtils.addDays(date, p)));
		}
		return periodMap;
	}
	
	

	
	/**
	 *查询产品id，产品名map
	 * 
	 */
	public Map<Integer,String> findProductNamesMap(){
		String sql ="SELECT a.`id`,CONCAT(a.`brand`,' ',a.`model`) FROM psi_product AS a  WHERE del_flag='0'";
		List<Object[]> list=this.psiProductDao.findBySql(sql);
		Map<Integer,String> map = Maps.newLinkedHashMap();  
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				Integer productId = Integer.parseInt(obj[0].toString());
				String productName=obj[1].toString();
				map.put(productId, productName);
			}
		}
		return map;
	}
	

	/**
	 *查询产品id，产品名map
	 * 
	 */
	public List<String> findProductNameAndColorList(){
		String sql ="SELECT distinct CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME "+
          " FROM psi_product a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1)  WHERE  a.`del_flag`='0' ";
		return this.psiProductDao.findBySql(sql);
	}
	
	
	/**
	 *查询分颜色产品信息
	 * 
	 */
	public List<Object[]> findProducColorInfo(){
		String sql ="SELECT distinct CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME "+
          " ,a.`chinese_name`,a.`description` FROM psi_product a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1)  WHERE  a.`del_flag`='0' ";
		return this.psiProductDao.findBySql(sql);
	}
	
	
	/***
	 *查询sku匹配的条码 
	 * 
	 */
	
	public String getFnskuBySku(String sku){
		String sql ="SELECT p.`barcode` FROM psi_sku AS c , psi_barcode AS p WHERE p.`psi_product`=c.`product_id`AND p.`product_platform`=c.`country` AND p.`product_color`=c.`color` AND c.`del_flag`='0' AND c.`use_barcode`='1' AND p.`barcode` IS NOT NULL  AND c.sku=:p1 ";
		List<String> objects=this.psiProductDao.findBySql(sql,new Parameter(sku));
		if(objects.size()==1){
			return objects.get(0);
		}
		return null;
	}
	
	/**
	 * fnsku和sku转化
	 * 
	 */
	public Map<String,String> getFnskuMap(Set<String> fnskuSet){
		Map<String,String> fnSkuMap = Maps.newHashMap();
		String sql ="SELECT DISTINCT a.`fnsku`, a.`sku` FROM amazoninfo_product2 AS a where a.`fnsku` IS NOT NULL ";
		List<Object[]> list=null;
		if(fnskuSet!=null&&fnskuSet.size()>0){
			sql=sql+" and a.fnsku IN :p1 OR a.ean IN :p1 ";
			list =this.psiProductDao.findBySql(sql,new Parameter(fnskuSet));
		}else{
			list =this.psiProductDao.findBySql(sql);
		}
		for(Object[] object :list){
			if(object[0]!=null&&object[1]!=null){
				fnSkuMap.put(object[0].toString(), object[1].toString());
			}
		}
		return fnSkuMap;
	}
	
	
	public Map<String,String> getSkuAndFnskuMap(Set<String> skuSet){
		Map<String,String> fnSkuMap = Maps.newHashMap();
		Parameter para = null;
		String sql ="SELECT DISTINCT a.`sku`, (CASE WHEN a.`fnsku` = a.`asin` THEN a.`ean` ELSE a.`fnsku` END)  FROM amazoninfo_product2 AS a  WHERE 1=1 ";
		if(skuSet!=null&&skuSet.size()>0){
			sql+="and a.sku in :p1";
			para=new Parameter(skuSet);
		}else{
			para=new Parameter();
		}
		List<Object[]> list =this.psiProductDao.findBySql(sql,para);
		for(Object[] object :list){
			if(object[0]!=null&&object[1]!=null){
				fnSkuMap.put(object[0].toString(), object[1].toString());
			}
		}
		return fnSkuMap;
	}
	
	public Map<String,String> findOneProductHasManySkus(Set<String> countrys,String warehouse){
		String warehouseId = "21";
		if("US".equals(warehouse)){
			warehouseId = "120";
		}else if("DE".equals(warehouse)){
			warehouseId = "19";
		}
		Map<String,String> rs = Maps.newHashMap();
		String sql ="SELECT CONCAT(a.`product_name`,CASE WHEN a.`color_code`!='' THEN '_' ELSE '' END ,a.`color_code`) AS NAMES ,GROUP_CONCAT(a.`sku` ORDER BY a.`id`) FROM psi_inventory a WHERE a.`warehouse_id` = :p1 AND a.`new_quantity` >0 AND a.`country_code` in :p2 GROUP BY NAMES HAVING COUNT(1) >1";
		List<Object[]> list =this.psiProductDao.findBySql(sql,new Parameter(warehouseId,countrys));
		for(Object[] object :list){
			String skus = object[1].toString();
			for (String sku : skus.split(",")) {
				rs.put(sku, skus);
			}
		}
		return rs;
	}
	
//	@Transactional(readOnly = false)
//	public void updateProductPrice(Map<Integer, Float> prices,Map<Integer, Float> rmbPrices,Map<Integer, String> remarks){
//		String sql = "UPDATE psi_product_supplier a SET a.`price` = :p1,a.remarks = :p2,a.`rmb_price` = :p4 WHERE a.`id` = :p3";
//		for (Entry<Integer, Float> entry : prices.entrySet()) {
//			String remark= remarks.get(entry.getKey())==null?"":remarks.get(entry.getKey());
//			psiProductDao.updateBySql(sql, new Parameter(entry.getValue(),remark,entry.getKey(),rmbPrices.get(entry.getKey())));
//		}
//	}
	
	@Transactional(readOnly = false)
	public void updateHscode(String eu_hscode,String us_hscode,String jp_hscode,String ca_hscode,String hk_hscode,String cn_hscode,String euImportDuty,String usImportDuty,String caImportDuty,String jpImportDuty,String euCustomDuty,String usCustomDuty,String caCustomDuty,String jpCustomDuty,Integer product_id,String mx_hscode,String mxImportDuty,String mxCustomDuty){
		String sql = "UPDATE psi_product a SET a.eu_hscode=:p1,a.us_hscode=:p2,a.jp_hscode=:p3,a.ca_hscode=:p4,a.hk_hscode=:p5,a.cn_hscode=:p6,  " +
		    " a.eu_import_duty=:p7,a.us_import_duty=:p8,a.ca_import_duty=:p9,a.jp_import_duty=:p10,a.eu_custom_duty=:p11,a.us_custom_duty=:p12,a.ca_custom_duty=:p13,a.jp_custom_duty=:p14,a.mx_hscode=:p15,a.mx_import_duty=:p16,a.mx_custom_duty=:p17 "+
			" WHERE a.`id` = :p18 ";
		psiProductDao.updateBySql(sql, new Parameter(eu_hscode,us_hscode,jp_hscode,ca_hscode,hk_hscode,cn_hscode,StringUtils.isBlank(euImportDuty)?null:euImportDuty,StringUtils.isBlank(usImportDuty)?null:usImportDuty,StringUtils.isBlank(caImportDuty)?null:caImportDuty,StringUtils.isBlank(jpImportDuty)?null:jpImportDuty,StringUtils.isBlank(euCustomDuty)?null:euCustomDuty,StringUtils.isBlank(usCustomDuty)?null:usCustomDuty,StringUtils.isBlank(caCustomDuty)?null:caCustomDuty,StringUtils.isBlank(jpCustomDuty)?null:jpCustomDuty,mx_hscode,StringUtils.isBlank(mxImportDuty)?null:mxImportDuty,StringUtils.isBlank(mxCustomDuty)?null:mxCustomDuty,product_id));
	    
		String historySql="select eu_hscode,ca_hscode,jp_hscode,us_hscode,hk_hscode,cn_hscode from psi_product_hscode_detail where product_id=:p1 order by update_date desc";
		List<Object[]> obj=psiProductDao.findBySql(historySql,new Parameter(product_id));
		String addSql="insert into psi_product_hscode_detail(product_id,update_date,eu_hscode,ca_hscode,jp_hscode,us_hscode,hk_hscode,cn_hscode) values(:p1,:p2,:p3,:p4,:p5,:p6,:p7,:p8)";
		if(obj!=null&&obj.size()>0){
			Object[] temp=obj.get(0);
			String euHscode=(temp[0]==null?"":temp[0].toString());
			String caHscode=(temp[1]==null?"":temp[1].toString());
			String jpHscode=(temp[2]==null?"":temp[2].toString());
			String usHscode=(temp[3]==null?"":temp[3].toString());
			String hkHscode=(temp[4]==null?"":temp[4].toString());
			String cnHscode=(temp[5]==null?"":temp[5].toString());
			boolean flag=false;
			if((StringUtils.isBlank(eu_hscode)&&StringUtils.isNotBlank(euHscode))||(StringUtils.isNotBlank(eu_hscode)&&!eu_hscode.equals(euHscode))||
				(StringUtils.isBlank(ca_hscode)&&StringUtils.isNotBlank(caHscode))||(StringUtils.isNotBlank(ca_hscode)&&!ca_hscode.equals(caHscode))||
				(StringUtils.isBlank(ca_hscode)&&StringUtils.isNotBlank(jpHscode))||(StringUtils.isNotBlank(jp_hscode)&&!jp_hscode.equals(jpHscode))||
				(StringUtils.isBlank(us_hscode)&&StringUtils.isNotBlank(usHscode))||(StringUtils.isNotBlank(us_hscode)&&!us_hscode.equals(usHscode))||
				(StringUtils.isBlank(us_hscode)&&StringUtils.isNotBlank(hkHscode))||(StringUtils.isNotBlank(hk_hscode)&&!hk_hscode.equals(hkHscode))||
				(StringUtils.isBlank(us_hscode)&&StringUtils.isNotBlank(cnHscode))||(StringUtils.isNotBlank(cn_hscode)&&!cn_hscode.equals(cnHscode))
			){
				flag=true;
			}
			if(flag){
				psiProductHsCodeDetailDao.updateBySql(addSql,new Parameter(product_id,new Date(),eu_hscode,ca_hscode,jp_hscode,us_hscode,hk_hscode,cn_hscode));
			}
		}else{
			psiProductHsCodeDetailDao.updateBySql(addSql,new Parameter(product_id,new Date(),eu_hscode,ca_hscode,jp_hscode,us_hscode,hk_hscode,cn_hscode));
		}
	}
	@Transactional(readOnly = false)
	public void updateTaxRefund(Integer taxRefund,Integer prductId){
		String sql="UPDATE psi_product AS a SET a.`tax_refund`=:p1 WHERE a.`id`=:p2";
		this.psiProductDao.updateBySql(sql,new Parameter(taxRefund,prductId));
	}
	
	public List<PsiProductHsCodeDetail> getHistoryHscodeById(Integer product_id){
		List<PsiProductHsCodeDetail> hscodeList=Lists.newArrayList();
		String sql="select eu_hscode,ca_hscode,jp_hscode,us_hscode,hk_hscode,cn_hscode,update_date from psi_product_hscode_detail where product_id=:p1 limit 0,5 ";
		List<Object[]> objList=psiProductDao.findBySql(sql,new Parameter(product_id));
		for (Object[] temp : objList) {
			String euHscode=(temp[0]==null?"":temp[0].toString());
			String caHscode=(temp[1]==null?"":temp[1].toString());
			String jpHscode=(temp[2]==null?"":temp[2].toString());
			String usHscode=(temp[3]==null?"":temp[3].toString());
			String hkHscode=(temp[4]==null?"":temp[4].toString());
			String cnHscode=(temp[5]==null?"":temp[5].toString());
			
			PsiProductHsCodeDetail code=new PsiProductHsCodeDetail();
			code.setEuHscode(euHscode);
			code.setCaHscode(caHscode);
			code.setJpHscode(jpHscode);
			code.setUsHscode(usHscode);
			code.setHkHscode(hkHscode);
			code.setCnHscode(cnHscode);
			code.setUpdateDate((Timestamp)temp[6]);
			code.setFormatDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(code.getUpdateDate()));
			hscodeList.add(code);
		}
		return hscodeList;
	}
	
	
	/**
	 * 查询产品里的颜色和订单里的颜色不同的产品信息
	 * 
	 */ 
	
	public String getColorChangeInfo(){
		String sql="SELECT a.`id`,CONCAT(a.`brand`,' ',a.`model`),a.`color`,b.`order_no`,b.`id` AS orderId ,b.`order_sta`,c.`product_name`,c.`color_code` FROM psi_product AS a ,lc_psi_purchase_order AS b ,lc_psi_purchase_order_item AS c WHERE b.`id`=c.`purchase_order_id` AND b.`order_sta` <>'6' AND b.`order_sta` !='5' AND c.`del_flag`='0' AND a.`id`=c.`product_id` AND NOT(CONCAT(',',a.`color`,',') LIKE CONCAT('%,',c.`color_code`,',%')) GROUP BY b.`order_no`";
		List<Object[]> objects=this.psiProductDao.findBySql(sql);
		StringBuilder res=new StringBuilder();
		if(objects.size()>0){
			for(Object[] object:objects){
				res.append((String)object[1]).append(",").append((String)object[3]).append("</br>");
			}
		}
		return res.toString();
	}
	
	public Map<Integer, String>  getVomueAndWeight(Set<Integer> productIdSet){
		Map<Integer,String> map=Maps.newHashMap();
		List<Object[]> list =null;
		String sql="SELECT a.id,a.`box_volume`,a.`gw`,a.`pack_quantity` FROM psi_product AS a  ";
		if(productIdSet!=null&&productIdSet.size()>0){
			sql+=" WHERE a.id IN :p1";
			list=this.psiProductDao.findBySql(sql,new Parameter(productIdSet));
		}else{
			list=this.psiProductDao.findBySql(sql);
		}
		
		for(Object[] obj:list){
			map.put((Integer)obj[0], obj[1].toString()+","+obj[2].toString()+","+obj[3].toString());
		}
		return map;
	}
	
	public Map<Integer, String>  getVomueAndWeight(){
		Map<Integer,String> map=Maps.newHashMap();
		String sql="SELECT a.id,a.`box_volume`,a.`gw`,a.`pack_quantity` FROM psi_product AS a";
		List<Object[]> list =this.psiProductDao.findBySql(sql);
		for(Object[] obj:list){
			map.put((Integer)obj[0], obj[1].toString()+","+obj[2].toString()+","+obj[3].toString());
		}
		return map;
	}
	
	public Map<String, String>  getVomueAndWeightByName(){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT DISTINCT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME, "+
           " a.`box_volume`,a.`gw`,a.`pack_quantity` "+
		  " FROM psi_product a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1)  WHERE  a.`del_flag`='0' ";
		List<Object[]> list =this.psiProductDao.findBySql(sql);
		for(Object[] obj:list){
			map.put(obj[0].toString(), obj[1].toString()+","+obj[2].toString()+","+obj[3].toString());
		}
		return map;
	}
	
	public static Map<String,String> endPromotions(AmazonAccountConfig config,Set<String> promotionIds) {
		Map<String,String> rs = Maps.newHashMap();
		try {
			String interfaceUrl = BaseService.AMAZONLOGIN_WEBPATH.replace("host", config.getServerIp()+":8080");
			Client client = BaseService.getCxfClient(interfaceUrl);
			Object[] str = new Object[]{Global.getConfig("ws.key"), config.getAccountName(),Lists.newArrayList(promotionIds)};
			Object[] res = client.invoke("endPromotions", str);
			List<String> list = (List<String>)res[0];
			for(String  idAndRs : list){
				 String[] arr=idAndRs.split("_");
				 rs.put(arr[0],arr[1]);
			}
		} catch (Exception e) {
			logger.error(config.getAccountName()+"关闭折扣错误："+e.getMessage(), e);
		}
		return rs;
	}
	
	@Transactional(readOnly=false)
	public void updateSuffixName(Integer id,String suffixName,String flag) {
		String sql ="";
		if("0".equals(flag)){
			sql ="UPDATE psi_product  SET certification_file=:p1 WHERE id=:p2 ";
		}else if("1".equals(flag)){
			sql="UPDATE psi_product  SET tran_report_file=:p1 WHERE id=:p2 ";
		}else if("2".equals(flag)){
			sql="UPDATE psi_product  SET check_List=:p1,check_state='0',check_list_user=:p2 WHERE id=:p3 ";
		}else if("3".equals(flag)){
			sql="UPDATE psi_product  SET tech_file=:p1 WHERE id=:p2 ";
		}else if("4".equals(flag)){
			sql="UPDATE psi_product  SET bom_list=:p1 WHERE id=:p2 ";
		}
		if("2".equals(flag)){
			this.psiProductDao.updateBySql(sql, new Parameter(suffixName,UserUtils.getUser().getId(),id));
		}else{
			this.psiProductDao.updateBySql(sql, new Parameter(suffixName,id));
		}
	}
	
	@Transactional(readOnly=false)
	public void updateCheckListState(Integer id,String state) {
		String sql="UPDATE psi_product  SET check_state=:p1,check_user=:p2 WHERE id=:p3 ";
		this.psiProductDao.updateBySql(sql, new Parameter(state,UserUtils.getUser().getId(),id));
	}
	
	
	//查询产品体积、重量
	
	public Map<String,String> getVolumeWeightBySku(Set<String> skus){
		String sql="SELECT a.`sku`,b.`box_volume`,b.`gw` FROM psi_sku AS a,psi_product AS b WHERE  a.`product_id`=b.`id` AND a.`sku` IN :p1 GROUP BY a.`sku`";
		List<Object[]> objs= this.psiProductDao.findBySql(sql, new Parameter(skus));
		Map<String,String> tempMap = Maps.newHashMap();
		for(Object[] obj:objs){
			String sku = obj[0].toString();
			if(obj[1]!=null&&obj[2]!=null){
				tempMap.put(sku, obj[1].toString()+","+obj[2].toString());
			}
		}
		return tempMap;
	}
	
	
	//michael 获取产品第一次下单时间
	public Map<String,String> getFirstPurchaseDate(){
		String sql ="SELECT b.`product_id`,a.`purchase_date` FROM psi_purchase_order AS a ,psi_purchase_order_item AS b WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` !='8' GROUP BY b.`product_id`";
		//增加理诚采购数据
		sql = "SELECT aa.product_id,MIN(aa.purchase_date) FROM ("+
				" SELECT b.`product_id`,a.`purchase_date` FROM psi_purchase_order AS a ,psi_purchase_order_item AS b WHERE a.`id`=b.`purchase_order_id` "+
				" AND a.`order_sta` !='8' GROUP BY b.`product_id`"+
				" UNION ALL"+
				" SELECT b.`product_id`,a.`purchase_date` FROM lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b WHERE a.`id`=b.`purchase_order_id`"+ 
				" AND a.`order_sta` !='8' GROUP BY b.`product_id`) aa GROUP BY aa.product_id";
		Map<String,String> tempMap = Maps.newHashMap();
		List<Object[]> list = this.psiProductDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				tempMap.put(obj[0].toString(), obj[1].toString());
			}
		}
	
	return tempMap;
	}
	
	//查询不使用的sku
	public Set<String> getNotFbaSku(){
		Set<String> set = Sets.newHashSet();
		String sql="SELECT a.`sku` FROM amazoninfo_product2 AS a  WHERE a.`is_fba`='0'";
		List<String> list = this.psiProductDao.findBySql(sql);
		set.addAll(list);
		return set;
	}
	
	
	public List<Object[]> getErrorFnSku(){
		String sql="SELECT aa.sku,aa.barcode,a.`fnsku`,pname FROM psi_inventory_fba a ,(SELECT a.product_platform,a.`barcode`,b.`sku`, CONCAT(b.`product_name`,(CASE WHEN b.`color`!='' THEN '_' ELSE '' END),b.`color`)  AS pname,b.`color`,b.`product_name` FROM psi_barcode a ,psi_sku b WHERE a.`id` = b.`barcode` AND a.`del_flag` = '0' AND b.`del_flag` = '0' AND b.`use_barcode` = '1' AND a.`barcode_type` = 'fnsku') aa,psi_product_eliminate c  WHERE a.`country` = aa.product_platform AND  a.`sku` =  BINARY(aa.sku) AND a.`data_date` = CURDATE() AND a.`fnsku` != aa.barcode  AND c.`product_name` = aa.product_name AND c.`color` = aa.color AND a.`country` = c.`country` AND c.`del_flag` = '0'";
		List<Object[]> list = this.psiProductDao.findBySql(sql);
		return list;
	}
	
	public List<Object[]> findErrorProductAsin(){
		String sql="SELECT p.`asin`,p.`country`,GROUP_CONCAT(DISTINCT CONCAT(p.`product_name`,CASE WHEN p.`color`!='' THEN CONCAT('_',p.`color`) ELSE '' END)) pname FROM psi_sku p "+
			    " JOIN amazoninfo_product2 d ON p.`country`=d.`country` AND d.`sku`=p.`sku`  "+
				" JOIN psi_product_eliminate a  ON a.`country`=d.`country` AND p.`product_name`=a.`product_name` AND p.`color`=a.`color` AND a.`del_flag`='0' AND a.`is_sale`!='4'  "+
				" WHERE p.`del_flag`='0'  AND d.`active`='1'  AND p.asin IS NOT NULL AND p.`product_name` NOT LIKE '%other%' AND p.`product_name` NOT LIKE '%old%'  "+
				" GROUP BY p.`asin`,p.`country`  HAVING LOCATE(',',pname)>0 ";
		List<Object[]> list = this.psiProductDao.findBySql(sql);
		return list;
	}
	
	
	
	/**
	 *找出主力产品的上架时间
	 */
	public Map<String,Date> getMasterProductAddedTime(){
		Map<String,Date> resMap =Maps.newHashMap();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String sql="SELECT (CASE WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color`)  END) AS proName ,MAX(a.`added_month`) FROM psi_product_eliminate AS a WHERE a.`del_flag`='0' AND a.`is_new`='0' AND a.`added_month` IS NOT NULL GROUP BY a.`product_name`,a.`color`";
		List<Object[]> list = this.psiProductDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				String name=obj[0].toString();
				String addDate = obj[1].toString();
				try {
					Date date=sdf.parse(addDate);
					resMap.put(name, date);
				} catch (ParseException e) {
				}
			}
		}
		return resMap;
	}
	
	/**
	 *找出淘汰品 和新品
	 */
	public List<String> getNoSaleAndNewPros(){
		String sql=" SELECT CONCAT(a.`brand`,' ',a.`model`) FROM psi_product AS a  WHERE a.`del_flag`='0' AND (a.`is_sale`='4' OR a.`is_new`='1')" ;
//		String sql="SELECT DISTINCT CASE  WHEN  b.`color`='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color`) END AS proColor FROM psi_product AS a , psi_sku AS b WHERE a.`del_flag`='0' AND b.`del_flag`='0'  AND a.`id`=b.`product_id` AND (a.`is_sale`='0' OR a.`is_new`='1')";
		//产品淘汰分平台、颜色 sku表中country字段ebay按照de处理
//		String sql ="SELECT DISTINCT CASE WHEN  b.`color`='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color`) END AS proColor FROM "+
//				" `psi_product_eliminate` e, psi_sku AS b WHERE  b.`del_flag`='0' AND  e.`product_name`=b.`product_name` AND "+
//				" e.`country`=CASE WHEN b.`country` ='ebay' THEN 'de' ELSE b.`country` END AND e.`color`=b.`color`"+
//				" AND e.`del_flag`='0' AND (e.`is_sale`='0' OR e.`is_new`='1') ";
		return  this.psiProductDao.findBySql(sql);
	}
	
	
	/**
	 * 查出产品90天 ，180天，360天 销量     (往前抛3天)
	 * key:proName    value:color,90,180
	 */
	public Map<String,Set<String>> get90_180Sales(List<String> newOrNoSales){
		Map<String,Set<String>> resMap = Maps.newHashMap();
			String sql="SELECT a.`product_name`,a.`color` " +
					" ,SUM( CASE WHEN a.`date` BETWEEN DATE_ADD(CURDATE(),INTERVAL -93 DAY ) AND DATE_ADD(CURDATE(),INTERVAL -3 DAY)  THEN a.`sales_volume` ELSE 0 END ) AS 90DaySales "+
					" ,SUM( CASE WHEN a.`date` BETWEEN DATE_ADD(CURDATE(),INTERVAL -183 DAY ) AND DATE_ADD(CURDATE(),INTERVAL -3 DAY) THEN a.`sales_volume` ELSE 0 END ) AS 180DaySales "+
					" FROM amazoninfo_sale_report AS a WHERE a.`product_name` <> '' AND  a.`order_type`='1' AND  a.`product_name`  NOT IN :p1 AND a.`date`>= DATE_ADD(CURDATE(),INTERVAL -183 DAY ) GROUP BY a.`product_name`,a.`color` ";
			List<Object[]> list = this.psiProductDao.findBySql(sql,new Parameter(newOrNoSales));
			if(list!=null&&list.size()>0){
				for(Object[] obj:list){
					String proName=obj[0].toString();
					String color=obj[1].toString();
					String sale90=obj[2].toString();
					String sale180=obj[3].toString();
					Set<String> set =null;
					if(resMap.get(proName)==null){
						set=Sets.newHashSet();
					}else{
						set=resMap.get(proName);
					}
					set.add(color+","+sale90+","+sale180);
					resMap.put(proName, set);
				}
			}
		return resMap;
	}

	private  boolean sendEmail(String content,String productManagerEmail){
		String toAddress ="fbamitteilung@inateck.com,supply-chain@inateck.com,"+UserUtils.logistics1+","+UserUtils.logistics2;
		if(StringUtils.isNotEmpty(productManagerEmail)){
			toAddress=toAddress+","+productManagerEmail;
		}
		
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();   
			final MailInfo mailInfo = new MailInfo(toAddress,"Product packing number change notification："+content+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(content);
			new Thread(){
				public void run(){   
					 mailManager.send(mailInfo);
				}
			}.start();
		}
		return true;
	}
	
	public Map<String,Integer> getProductNameAndPidMap(){
		String sql = "SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) proName "+
					" , a.`id` FROM psi_product a JOIN mysql.help_topic b "+
					" ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1)";
		List<Object[]> list = this.psiProductDao.findBySql(sql);
		Map<String,Integer>  rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(), Integer.parseInt(objects[1].toString()));
		}
		return rs;
	}
	
	public List<String> findProductByType(String type){
		String sql = "SELECT DISTINCT CONCAT(a.`product_name`,CASE WHEN a.`color`!= '' THEN '_' ELSE '' END ,a.`color`) FROM psi_product_eliminate a,psi_product b WHERE a.`is_new` = '0' AND a.`is_sale` != '4' AND a.`del_flag` = '0' AND b.`id` = a.`product_id` AND b.`TYPE` = :p1";
		List<String> list = this.psiProductDao.findBySql(sql,new Parameter(type));
		return list;
	}
	
	//由key：fnsku   value：sku
	public Map<String,String> getFnskuAndSkuMap(){
		Map<String,String> fnSkuMap = Maps.newHashMap();
		String sql ="SELECT DISTINCT (CASE WHEN a.`fnsku` = a.`asin` THEN a.`ean` ELSE a.`fnsku` END) ,a.`sku` FROM amazoninfo_product2 AS a";
		List<Object[]> list =this.psiProductDao.findBySql(sql);
		for(Object[] object :list){
			if(object[0]!=null&&object[1]!=null){
				fnSkuMap.put(object[0].toString(), object[1].toString());
			}
		}
		return fnSkuMap;
	}
	
	public Map<String,String> getFnskuAndSkuVersionMap(){
		Map<String,String> fnSkuMap = Maps.newHashMap();
		String sql ="SELECT DISTINCT (CASE WHEN a.`fnsku` = a.`asin` THEN a.`ean` ELSE a.`fnsku` END) ,a.`sku` FROM amazoninfo_product2 AS a";
		List<Object[]> list =this.psiProductDao.findBySql(sql);
		for(Object[] object :list){
			if(object[0]!=null&&object[1]!=null){
				String sku = object[1].toString();
				String version = getSkuVersion(sku);
				fnSkuMap.put(object[0].toString(), version);
			}
		}
		return fnSkuMap;
	}
	
	public boolean getHasSameAsin(String newSku,String oldSku){
		boolean flag=false;
		Set<String> setSku= Sets.newHashSet();
		setSku.add(newSku);
		setSku.add(oldSku);
		String sql ="SELECT a.id FROM amazoninfo_product2 a WHERE a.`sku` IN :p1 AND a.`active` = '1'  GROUP BY a.`asin`";
		List<Integer> list =this.psiProductDao.findBySql(sql,new Parameter(setSku));
		if(list!=null&&list.size()>1){
			flag=true;
		}
		return flag;
	}
	
	public Map<Integer, String> getHasPower(){
		Map<Integer, String> rs = Maps.newHashMap();
		String sql ="SELECT a.id,a.has_power FROM psi_product a WHERE a.del_flag='0'";
		List<Object[]> list =this.psiProductDao.findBySql(sql);
		for (Object[] obj : list) {
			rs.put(Integer.parseInt(obj[0].toString()), obj[1].toString());
		}
		return rs;
	}
	
	public Map<String, String> getHasPowerByName(){
		Map<String, String> rs = Maps.newHashMap();
		String sql =" SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,a.`has_power` "+
           " FROM psi_product a JOIN mysql.help_topic b  ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.`del_flag`='0' ";
		List<Object[]> list =this.psiProductDao.findBySql(sql);
		for (Object[] obj : list) {
			rs.put(obj[0].toString(), obj[1].toString());
		}
		return rs;
	}
	
	public Map<String, String> getHasChargedByName(){
		Map<String, String> rs = Maps.newHashMap();
		String sql =" SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,a.`has_electric` "+
           " FROM psi_product a JOIN mysql.help_topic b  ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.`del_flag`='0' ";
		List<Object[]> list =this.psiProductDao.findBySql(sql);
		for (Object[] obj : list) {
			rs.put(obj[0].toString(), obj[1].toString());
		}
		return rs;
	}
	
	public Map<String, String> getPowerOrKeyboardByName(){
		Map<String, String> rs = Maps.newHashMap();
		String sql =" SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,CASE WHEN (a.type='Keyboard' and a.model!='KB02001') THEN '1' ELSE a.`has_power` END  "+
           " FROM psi_product a JOIN mysql.help_topic b  ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.`del_flag`='0' ";
		List<Object[]> list =this.psiProductDao.findBySql(sql);
		for (Object[] obj : list) {
			rs.put(obj[0].toString(), obj[1].toString());
		}
		return rs;
	}
	
	public Map<String,Object[]> getHasPowerAndCode(String country){
		Map<String, Object[]> rs = Maps.newHashMap();
		String sql="";
		if("uk".equals(country)){
			sql="SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,(case when a.type='Keyboard' then '1' else a.`has_power` end),c.code, "+
		            " round(a.product_pack_length*0.3937008,2),round(a.product_pack_width*0.3937008,2),round(a.product_pack_height*0.3937008,2),round(a.product_pack_weight/1000*2.2046226,2),round(a.length*0.3937008,2),round(a.width*0.3937008,2),round(a.height*0.3937008,2),round(a.weight/1000*2.2046226,2)  "+
					" FROM psi_product a JOIN mysql.help_topic b  ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1)   "+
					" LEFT JOIN amazoninfo_product_type_code c ON a.`TYPE`=c.`product_type`  "+
					" WHERE a.`del_flag`='0' and (a.`has_power` = '1' or a.`TYPE` = 'Keyboard') ";
		}else{
			sql="SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,(case when a.type='Keyboard' then '1' else a.`has_power` end),c.code, "+
					" round(a.product_pack_length*0.3937008,2),round(a.product_pack_width*0.3937008,2),round(a.product_pack_height*0.3937008,2),round(a.product_pack_weight/1000*2.2046226,2),round(a.length*0.3937008,2),round(a.width*0.3937008,2),round(a.height*0.3937008,2),round(a.weight/1000*2.2046226,2)  "+
					" FROM psi_product a JOIN mysql.help_topic b  ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1)   "+
					" LEFT JOIN amazoninfo_product_type_code c ON a.`TYPE`=c.`product_type`  "+
					" WHERE a.`del_flag`='0' ";
		}
		List<Object[]> list =this.psiProductDao.findBySql(sql);
		for (Object[] obj : list) {
			rs.put(obj[0].toString(), obj);
		}
		return rs;
	}
	
	public Map<String,Integer> getPackQuantity(){
		Map<String, Integer> rs = Maps.newHashMap();
		String sql =" SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,a.pack_quantity "+
           " FROM psi_product a JOIN mysql.help_topic b  ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.`del_flag`='0' ";
		List<Object[]> list =this.psiProductDao.findBySql(sql);
		for (Object[] obj : list) {
			rs.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
		}
		return rs;
	}
	
	public Map<String,Map<String,Object>> getProductTypeAndWeight(){
		Map<String, Map<String,Object>> rs = Maps.newHashMap();
		String sql =" SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,a.type,a.gw,a.pack_quantity,a.eu_custom_duty,a.us_custom_duty "+
           " FROM psi_product a JOIN mysql.help_topic b  ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.`del_flag`='0' ";
		List<Object[]> list =this.psiProductDao.findBySql(sql);
		for (Object[] obj : list) {
			String name=obj[0].toString();
			String type=obj[1].toString();
			BigDecimal gw=(BigDecimal)obj[2];
			Integer packQuantity=Integer.parseInt(obj[3].toString());
			float euDuty=Float.parseFloat(obj[4]==null?"0":obj[4].toString());
			float usDuty=Float.parseFloat(obj[5]==null?"0":obj[5].toString());
			Map<String,Object> temp=rs.get(name);
			if(temp==null){
				temp=Maps.newHashMap();
				rs.put(name, temp);
			}
			temp.put("type",type);
			temp.put("tranGw",gw.divide(new BigDecimal(packQuantity),2, BigDecimal.ROUND_UP).floatValue());
			temp.put("euDuty",euDuty);
			temp.put("usDuty",usDuty);
		}
		return rs;
	}
	
	public  String getSkuVersion(String sku){
		 Pattern pattern = Pattern.compile("new[0-9]*", Pattern.CASE_INSENSITIVE);   
		 Matcher matcher = pattern.matcher(sku);                            
		 if(matcher.find(0)) {  
			 String res =matcher.group();
		     if("NEW".equals(res.toUpperCase())){
		    	return "NEW1"; 
		     }else{
		    	return  res.toUpperCase();
		     }                       
		 }
		 return "无";
	}
	
	public Map<String,String> getSkuByProduct(){
		Map<String, String> rs = Maps.newHashMap();
		String sql="SELECT CONCAT(s.`product_name`,CASE WHEN color='' THEN '' ELSE CONCAT('_',s.`color`) END) NAME,country,sku FROM psi_sku s WHERE s.`del_flag`='0' AND s.`use_barcode`='1' ";
		List<Object[]> list =this.psiProductDao.findBySql(sql);
		for (Object[] obj : list) {
			rs.put(obj[0].toString()+"_"+obj[1].toString(), obj[2].toString());
		}
		return rs;
	}
	
	public List<String> getSkuByCountryProduct(String country,String productName){
		String sql="";
		if("eu".equals(country)){
			sql="SELECT DISTINCT sku FROM psi_sku p WHERE p.`del_flag`='0' AND country in ('de') and sku not like '%local%' AND CONCAT(p.`product_name`,CASE WHEN color='' THEN '' ELSE CONCAT('_',p.`color`) END)=:p1 ";
			return psiProductDao.findBySql(sql,new Parameter(productName));
		}else{
			sql="SELECT DISTINCT sku FROM psi_sku p WHERE p.`del_flag`='0' AND country=:p1 and sku not like '%local%' AND CONCAT(p.`product_name`,CASE WHEN color='' THEN '' ELSE CONCAT('_',p.`color`) END)=:p2 ";
			return psiProductDao.findBySql(sql,new Parameter(country,productName));
		}
	}
	
	
	
	/**
	 *查找某sku的绑定sku 
	 */
	public String getBangSkuBySku(String sku){
		String sql="SELECT a.`sku` FROM psi_sku AS a ,(SELECT a.`product_name`,a.`color_code`,a.`country_code` FROM psi_inventory AS a WHERE a.sku=:p1 GROUP BY a.`sku`) AS b" +
				"	WHERE a.`product_name`=b.product_name AND a.`country`=b.country_code AND a.`color`=b.color_code AND a.`use_barcode`='1' AND a.`del_flag`='0' ";
		List<String> skus= psiProductDao.findBySql(sql,new Parameter(sku));
		if(skus!=null&&skus.size()>0){
			return skus.get(0);
		}else{
			return null;
		}
	}
	
	
	
	/**
	 * 获得产品装箱数，产品id
	 * key:product_color
	 * value:id+,+packNumber
	 */
	public Map<String,String> getPackNumberByColor(){
		String sql = "SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) proName" +
				",a.`id`,a.`pack_quantity` FROM psi_product a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) AND a.`del_flag`='0'	";
		List<Object[]> list = this.psiProductDao.findBySql(sql);
		Map<String,String> rs = Maps.newHashMap();
		for(Object[] obj:list){
			String productName=obj[0].toString();
			rs.put(productName, obj[1]+","+obj[2]);
		}
		return rs;
	}
	
	
	/**
	 * 查找某产品类型的产品经理负责人
	 */
	public String getManagerByProductType(String type){
		//String sql = "	SELECT DISTINCT a.name FROM  sys_user AS a,sys_dict AS b, psi_product_manage_group AS c WHERE a.id=c.`user_id` AND c.`dict_id`=b.`id` AND a.`del_flag`='0' AND b.`value`=:p1";
		String sql="SELECT  group_concat(distinct r.name) FROM ( "+
			"	SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(a.user_id,',',b.help_topic_id+1),',',-1) userId,dict_id  "+
			"	FROM psi_product_manage_group a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.user_id) - LENGTH(REPLACE(a.user_id,',',''))+1)   "+
			"	) d JOIN psi_product_type_group g ON d.dict_id=g.id  "+
			"	JOIN sys_dict t ON g.`dict_id`=t.id AND t.`del_flag`='0' AND  t.`type`='product_type'   "+
			"	JOIN psi_product p ON p.type=t.value AND p.del_flag='0'  "+
			"	JOIN sys_user r ON r.id=d.userId AND r.del_flag='0'  WHERE p.type=:p1  ";
		
		List<String> list = this.psiProductDao.findBySql(sql,new Parameter(type));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 查找某产品类型的采购经理负责人
	 */
	public Map<String,String> getManagerByProductType(){
		Map<String,String> rs = Maps.newHashMap();
		//String sql = "	SELECT b.`value`,a.name FROM  sys_user AS a,sys_dict AS b, psi_product_manage_group AS c WHERE a.id=c.`user_id` AND c.`dict_id`=b.`id` AND a.`del_flag`='0' AND b.`del_flag`='0' ";
		String sql ="SELECT  t.value,GROUP_CONCAT(distinct r.`name`) FROM ( "+
				"	SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(a.user_id,',',b.help_topic_id+1),',',-1) userId,dict_id  "+
				"	FROM psi_product_manage_group a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.user_id) - LENGTH(REPLACE(a.user_id,',',''))+1)  "+
				"	) d JOIN psi_product_type_group g ON d.dict_id=g.id  "+
				"	JOIN sys_dict t ON g.`dict_id`=t.id AND t.`del_flag`='0' AND  t.`type`='product_type'  "+
				"	JOIN sys_user r ON r.id=d.userId AND r.del_flag='0' GROUP BY t.value ";
		List<Object[]> list = this.psiProductDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				rs.put(obj[0].toString(), obj[1].toString());
			}
		}
		return rs;
	}
	
	/**
	 *查询所有跟单 
	 */
	
	public List<Integer> getFollowMan(){
		String sql = "SELECT a.`create_user` FROM psi_product AS a Where a.del_flag='0' and  a.`create_user`!='1' GROUP BY a.`create_user`";
		List<Integer> list = this.psiProductDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			return list;
		}
		return null;
	}
	/**
	 * 查找某产品类型的采购经理负责人
	 */
	public String getPurchaseByProductType(String type){
		String sql = "	SELECT DISTINCT a.name FROM  sys_user AS a,sys_dict AS b, psi_product_purchase_group AS c WHERE a.id=c.`user_id` AND c.`dict_id`=b.`id` AND a.`del_flag`='0' AND b.`value`=:p1";
		List<String> list = this.psiProductDao.findBySql(sql,new Parameter(type));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 查找某产品类型的采购经理负责人
	 */
	public Map<String,String> getPurchaseByProductType(){
		Map<String,String> rs = Maps.newHashMap();
		String sql = "	SELECT b.`value`,a.name FROM  sys_user AS a,sys_dict AS b, psi_product_purchase_group AS c WHERE a.id=c.`user_id` AND c.`dict_id`=b.`id` AND a.`del_flag`='0' AND b.`del_flag`='0' ";
		List<Object[]> list = this.psiProductDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				rs.put(obj[0].toString(), obj[1].toString());
			}
		}
		return rs;
	}
	
	
	/**
	 * 查询摄影师
	 */
	/*public String getPhotoByProductType(String type){
		String sql = "SELECT c.`user_id` FROM sys_dict AS b,  psi_product_group_photo AS c,psi_product_type_group AS d WHERE c.`line_id`=d.`id` AND d.`dict_id`=b.`id`  AND b.`del_flag`='0' AND c.`del_flag`='0' AND b.`value`=:p1";
		List<String> list = this.psiProductDao.findBySql(sql,new Parameter(type));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}*/
	
	/*public Map<String,String> getPhotoByProductType(){
		Map<String,String> map=Maps.newHashMap();
		String sql=" SELECT a.value,GROUP_CONCAT(DISTINCT s.name) FROM ( "+
				" SELECT b.`value`,c.`user_id` FROM sys_dict AS b,  psi_product_group_photo AS c,psi_product_type_group AS d WHERE c.`line_id`=d.`id` AND d.`dict_id`=b.`id`  AND b.`del_flag`='0' AND c.`del_flag`='0' "+
				" ) a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.user_id) - LENGTH(REPLACE(a.user_id,',',''))+1) "+
				" JOIN sys_user s ON SUBSTRING_INDEX(SUBSTRING_INDEX(a.user_id,',',b.help_topic_id+1),',',-1)=s.id GROUP BY a.value ";
		List<Object[]> list = this.psiProductDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				map.put(obj[0].toString(), obj[1].toString());
			}
		}
		return map;			
	}
	*/
	
	/**
	 *根据产品名称查询产品装箱数、体积、重量 信息 
	 */
	public Map<String,String> getProductVoGw(Set<String> productNames){
		Map<String,String> rs = Maps.newHashMap();
		String sql="SELECT DISTINCT (CASE WHEN b.product_color='' THEN b.`product_name` ELSE CONCAT(b.product_name,'_',b.product_color) END),CONCAT(a.`pack_quantity`,',',a.`box_volume`,',',a.`gw`) " +
				" FROM psi_product AS a,psi_barcode AS b  WHERE a.`id`=b.`psi_product` AND b.`del_flag`='0' AND " +
				" (CASE WHEN b.product_color='' THEN b.`product_name` ELSE CONCAT(b.product_name,'_',b.product_color) END) IN :p1 ;";
		List<Object[]> list=this.psiProductDao.findBySql(sql,new Parameter(productNames));
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				String proColor = obj[0].toString();
				rs.put(proColor, obj[1].toString());
			}
		}
		return rs;
	}
	
	public Map<String,String> findEbayName(){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT DISTINCT country,sku,CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END )  FROM psi_sku p WHERE p.`del_flag`='0' AND country IN ('ebay','ebay_com') AND p.`product_name`!='Inateck other' AND p.`product_name`!='Inateck Old'";
		List<Object[]> list=this.psiProductDao.findBySql(sql);
		for (Object[] obj :list) {
			String country=obj[0].toString();
			String sku=obj[1].toString();
			String name=obj[2].toString();
			map.put(country+"_"+sku, name);
		}
		return map;
	}
	
	public Map<String,Map<String,String>> findSkuByCountryAsin(){
		Map<String,Map<String,String>>  map=Maps.newHashMap();
		String sql="SELECT p.sku,p.`country`,p.`asin` FROM psi_sku p WHERE p.`del_flag`='0' AND ASIN IS NOT NULL AND sku IS NOT NULL";
		List<Object[]> list=this.psiProductDao.findBySql(sql);
		for (Object[] obj :list) {
			Map<String,String> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[0].toString(),temp);
			}
			temp.put(obj[1].toString(),obj[2].toString());
		}
		return map;
	}
	  
	public Map<String, Map<String, String>> getSkusByCountry(Set<String> countrySet){
		Map<String, Map<String, String>> rs = Maps.newHashMap();
		String skuSql ="SELECT t.`sku`,t.`country`,CONCAT(t.`product_name`,CASE  WHEN t.`color`='' THEN '' ELSE CONCAT('_',t.`color`) END ) AS pName FROM `psi_sku` t WHERE t.`country` IN :p1 AND t.`del_flag`='0' AND t.use_barcode='1'";
		List<Object[]> psiSkus = this.psiProductDao.findBySql(skuSql, new Parameter(countrySet));
		for (Object[] obj : psiSkus) {
			String sku = obj[0].toString();
			String country = obj[1].toString();
			String productName = obj[2].toString();
			Map<String, String> map = rs.get(sku);
			if (map == null) {
				map = Maps.newHashMap();
				rs.put(sku, map);
			}
			map.put("country", country);
			map.put("productName", productName);
		}
		return rs;
	}
	
	
	/**
	 *查询是新品，并且没下过订单的产品 
	 *product_color,country
	 */
	public Set<String> getNewProductAndNoOrder(){
		Set<String> resSet = Sets.newHashSet();
		//查出所有已下过单的产品
		String sql="SELECT  DISTINCT CONCAT(b.`product_name`,',',b.`color_code`,',',b.`country_code`) AS productName  FROM psi_purchase_order AS a ,psi_purchase_order_item AS b WHERE a.id=b.`purchase_order_id` AND a.`order_sta`!='6' " +
				"UNION(SELECT  DISTINCT CONCAT(b.`product_name`,',',b.`color_code`,',',b.`country_code`) AS productName  FROM lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b WHERE a.id=b.`purchase_order_id` AND a.`order_sta`!='6') ";
        List<String> productNames = this.psiProductDao.findBySql(sql);
		//排除已下单的产品,得出新品未下单的
		sql="SELECT (CASE  WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color`) END) AS productName ,a.`country` FROM psi_product_eliminate AS a WHERE  a.`is_new`='1' AND a.`del_flag`='0' AND CONCAT(a.`product_name`,',',a.`color`,',',a.`country`) NOT IN :p1 ";	
		List<Object[]> noOrderProducts= this.psiProductDao.findBySql(sql,new Parameter(productNames));
		for(Object[] obj:noOrderProducts){
			resSet.add(obj[0]+","+obj[1]);
		}
		return resSet;
	}
		
	public Map<String,Float> findCustomDuty(){
		Map<String,Float> map=Maps.newHashMap();
		String sql="SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME, "+
		" eu_custom_duty,ca_custom_duty,jp_custom_duty,us_custom_duty,tax_refund "+
		" FROM psi_product a JOIN mysql.help_topic b "+
		" ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.`del_flag`='0' ";
		List<Object[]> list= this.psiProductDao.findBySql(sql);
		for (Object[] obj: list) {
			String name=obj[0].toString();
			Float euCustomDuty=Float.parseFloat(obj[1]==null?"0":obj[1].toString());
			Float caCustomDuty=Float.parseFloat(obj[2]==null?"0":obj[2].toString());
			Float jpCustomDuty=Float.parseFloat(obj[3]==null?"0":obj[3].toString());
			Float usCustomDuty=Float.parseFloat(obj[4]==null?"0":obj[4].toString());
			Float taxRefund=Float.parseFloat(obj[5]==null?"17":obj[5].toString());
			map.put(name+"_eu", euCustomDuty);
			map.put(name+"_ca", caCustomDuty);
			map.put(name+"_jp", jpCustomDuty);
			map.put(name+"_us", usCustomDuty);
			map.put(name, taxRefund);
		}
		return map;
	}
	
	public Map<String,Float> findSupplier(Set<String> nameSet){
		Map<String,Float> map=Maps.newHashMap();
		String sql="SELECT p.name,t.tax_rate FROM "+
		" (SELECT id,CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME "+ 
		" FROM psi_product a JOIN mysql.help_topic b "+
		" ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.`del_flag`='0') p "+ 
		" JOIN psi_product_supplier s ON s.product_id=p.id "+
		" JOIN psi_supplier t ON s.supplier_id=t.id AND del_flag='0' ";
		List<Object[]> list= this.psiProductDao.findBySql(sql);
		for (Object[] obj: list) {
			String name=obj[0].toString();
			Float tax=Float.parseFloat(obj[1]==null?"0":obj[1].toString());
			map.put(name,tax);
		}
		return map;
	}
	
	public Map<String,Float> findTransportAvgPrice(Set<String> nameSet,String country){
		Map<String,Float> map=Maps.newHashMap();
		String tempCountry="";
		if("de,fr,it,es,uk".contains(country)){
			tempCountry="EU";
		}else if("jp".equals(country)){
			tempCountry="JP";
		}else{
			tempCountry="US";
		}
		String sql="SELECT product_name,GROUP_CONCAT(avg_price ORDER BY update_date DESC) FROM psi_product_avg_price p where product_name in :p1 and country=:p2 "+
           " GROUP BY product_name ORDER BY update_date DESC ";
		List<Object[]> list=psiProductDao.findBySql(sql,new Parameter(nameSet,tempCountry));
		for (Object[] obj: list) {
			String productName=obj[0].toString();
			Float price=Float.parseFloat(obj[1].toString().split(",")[0])/AmazonProduct2Service.getRateConfig().get("USD/CNY");
			if("de,fr,it,es".contains(country)){
				map.put(productName,price/AmazonProduct2Service.getRateConfig().get("EUR/USD"));
			}else if("ca".equals(country)){
				map.put(productName,price/AmazonProduct2Service.getRateConfig().get("CAD/USD"));
			}else if("uk".equals(country)){
				map.put(productName,price/AmazonProduct2Service.getRateConfig().get("GBP/USD"));
			}else if("jp".equals(country)){
				map.put(productName,price*AmazonProduct2Service.getRateConfig().get("USD/JPY"));
			}else if("mx".equals(country)){
				map.put(productName,price/AmazonProduct2Service.getRateConfig().get("MXN/USD"));
			}else{
				map.put(productName,price);
			}
		}
		return map;
	}
	
	
	public Map<String,Float> findTransportAvgPrice(String name){
		Map<String,Float> map=Maps.newHashMap();
	
		String sql="SELECT country,GROUP_CONCAT(avg_price ORDER BY update_date DESC) FROM psi_product_avg_price p where product_name = :p1 "+
           " GROUP BY country ORDER BY update_date DESC ";
		List<Object[]> list=psiProductDao.findBySql(sql,new Parameter(name));
		for (Object[] obj: list) {
		   Float price=Float.parseFloat(obj[1].toString().split(",")[0])/AmazonProduct2Service.getRateConfig().get("USD/CNY");
		   map.put(obj[0].toString(),price);
		}
		return map;
	}
	
	
	public Map<String,Float> findCustomDutyById(){
		Map<String,Float> map=Maps.newHashMap();
		String sql="SELECT id,eu_custom_duty,ca_custom_duty,jp_custom_duty,us_custom_duty,mx_custom_duty FROM psi_product a  WHERE a.`del_flag`='0' ";
		List<Object[]> list= this.psiProductDao.findBySql(sql);
		for (Object[] obj: list) {
			String name=obj[0].toString();
			Float euCustomDuty=Float.parseFloat(obj[1]==null?"0":obj[1].toString());
			Float caCustomDuty=Float.parseFloat(obj[2]==null?"0":obj[2].toString());
			Float jpCustomDuty=Float.parseFloat(obj[3]==null?"0":obj[3].toString());
			Float usCustomDuty=Float.parseFloat(obj[4]==null?"0":obj[4].toString());
			Float mxCustomDuty=Float.parseFloat(obj[5]==null?"0":obj[5].toString());
			map.put(name+"_eu", euCustomDuty);
			map.put(name+"_ca", caCustomDuty);
			map.put(name+"_jp", jpCustomDuty);
			map.put(name+"_us", usCustomDuty);
			map.put(name+"_mx", mxCustomDuty);
		}
		return map;
	}
	
	public Map<String, Map<String,String>> findEvent(String productName ,String startDate,String endDate){
		 Map<String, Map<String,String>> rs = Maps.newHashMap();
		 String sql ="SELECT a.country,a.event_type,DATE_FORMAT(a.`event_data`,'%Y%m%d') FROM amazoninfo_session_event a WHERE a.`event_data`>=:p1 AND a.`event_data`<=:p2 AND a.`product_name` = :p3 "; 
		 List<Object[]> list= this.psiProductDao.findBySql(sql,new Parameter(startDate,endDate,productName));
		 if(list!=null&&list.size()>0){
			 for (Object[] objs : list) {
				 String country = objs[0].toString();
				 String type = objs[1].toString();
				 String date = objs[2].toString();
				 Map<String,String> data = rs.get(country);
				 if(data==null){
					 data = Maps.newHashMap();
					 rs.put(country, data);
				 }
				 data.put(date, type);
			 }
		 }
		return rs;
	}
	
	@Transactional(readOnly = false)
	public void updateSessionEvents(){
		String sql = "INSERT INTO amazoninfo_session_event (product_name,country,event_type,event_data) "+
					 "	SELECT  CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN '_' ELSE '' END ,b.`color`) product_name,a.`country`,'1',MIN(a.`data_date`) AS event_data FROM amazoninfo_advertising a ,psi_sku b WHERE b.`del_flag` = '0' AND a.`sku` = b.`sku` AND a.`country` = b.`country` AND b.product_name IS NOT NULL AND b.product_name!='' AND b.product_name !='inateck old' AND b.product_name !='inateck other' GROUP BY product_name,a.`country` "+
					 "	ON DUPLICATE KEY UPDATE `product_name` = VALUES(product_name)";	
		psiProductDao.updateBySql(sql, null);
		
		sql ="INSERT INTO amazoninfo_session_event (product_name,country,event_type,event_data)  "+
				 "	SELECT CONCAT(p.`product_name`,CASE WHEN p.`color`!='' THEN CONCAT ('_',p.`color`) ELSE '' END) AS  product_name,d.`country`,(CASE WHEN d.`operat`=1 THEN 3 ELSE d.operat END) AS event_type ,DATE_FORMAT(d.`create_date`,'%Y-%m-%d') t_date  FROM amazoninfo_posts_relationship_feed d  "+
				 "	JOIN amazoninfo_posts_relationship_change c ON d.id=c.`feed_id` "+
				 "	JOIN psi_sku p ON d.`country`=p.`country` AND c.sku=p.`sku`  "+
				 "	WHERE  d.`operat` IN ('1','2') AND p.`del_flag`='0' AND d.create_date > DATE_ADD(CURDATE(),INTERVAL -7 DAY) "+
				 "	GROUP BY product_name,d.`country`,t_date "+
				 "	ON DUPLICATE KEY UPDATE `product_name` = VALUES(product_name)";
		
		psiProductDao.updateBySql(sql, null);
		
		sql ="INSERT INTO amazoninfo_session_event (product_name,country,event_type,event_data) "+
			"		SELECT DISTINCT CONCAT(s.`product_name`,CASE WHEN s.`color`!='' THEN CONCAT ('_',s.`color`) ELSE '' END) AS product_name,f.`country`,CASE WHEN f.reason = '断货升价' THEN '4'  WHEN f.reason = '积压降价' THEN '5'  WHEN f.reason LIKE '%促销%' THEN '6' WHEN f.reason LIKE '%汇率%' THEN '7' WHEN f.reason LIKE '%防御%' THEN '8' ELSE '9' END  ,DATE_FORMAT(f.`request_date`,'%Y-%m-%d')   FROM amazoninfo_price_feed f ,amazoninfo_price p,psi_sku s WHERE f.`id` = p.`feed_price_feed_id` AND f.`state` = '3' AND s.`del_flag` = '0' AND f.`country` = s.`country` AND s.`sku` = p.`sku` AND f.`reason` IS NOT NULL AND f.`reason` !='' AND f.reason NOT LIKE '%包邮%' AND f.reason NOT LIKE '%自动%' "+
			"		AND f.`request_date` > DATE_ADD(CURDATE(),INTERVAL -7 DAY) "+
			"		ON DUPLICATE KEY UPDATE `product_name` = VALUES(product_name)     ";
		
		psiProductDao.updateBySql(sql, null);
		
		sql = "INSERT INTO amazoninfo_session_event (product_name,country,event_type,event_data)  "+
				"			SELECT CONCAT(t.`product_name`,CASE WHEN t.`color`!='' THEN CONCAT ('_',t.`color`) ELSE '' END) AS product_name,t.`country`,'10',DATE_FORMAT(t.`date`,'%Y-%m-%d') FROM amazoninfo_sale_report t  "+
				"			WHERE   t.`order_type`='1' AND t.`flash_sales_order`>0 AND t.product_name IS NOT NULL AND t.`date` > DATE_ADD(CURDATE(),INTERVAL -7 DAY)  "+
				"		GROUP BY t.`country`,product_name,t.`date`  "+
				"		ON DUPLICATE KEY UPDATE `product_name` = VALUES(product_name) ";
		
		psiProductDao.updateBySql(sql, null);
		
		
		sql = "INSERT INTO amazoninfo_session_event (product_name,country,event_type,event_data)   "+
				"			SELECT SUBSTRING_INDEX(b.`sku`,',',-1) AS pname ,b.`country`,'11',DATE_FORMAT(b.`request_date`,'%Y-%m-%d') AS rdate FROM amazoninfo_image a, amazoninfo_image_feed b WHERE a.`feed_image_feed_id`= b.`id` AND b.`state` = '3' AND a.`type` = 'Main' AND b.`sku` LIKE '%,%' AND b.request_date > DATE_ADD(CURDATE(),INTERVAL -7 DAY)  "+
				"			GROUP BY b.`country`,rdate,pname  "+
				"			ON DUPLICATE KEY UPDATE `product_name` = VALUES(product_name)";
		psiProductDao.updateBySql(sql, null);
		
		
		sql = "INSERT INTO amazoninfo_session_event (product_name,country,event_type,event_data)   "+
			"	SELECT b.`product_name`,b.`country`,'12',DATE_FORMAT(a.`create_date`,'%Y-%m-%d') AS rdate "+
			"	FROM amazoninfo_posts_feed a, amazoninfo_posts_change b WHERE a.id= b.`feed_id` AND a.`state` = '3' "+
			"	AND a.`operate_type`='0'  AND a.`create_date`> DATE_ADD(CURDATE(),INTERVAL -7 DAY) AND b.`studio`='0' AND b.`product_name` IS NOT NULL "+
			"	GROUP BY a.`country`,rdate,b.`product_name` "+  
			"	ON DUPLICATE KEY UPDATE `product_name` = VALUES(product_name) ";
		psiProductDao.updateBySql(sql, null);
	}
	
	
	/**
	 *根据最新绑定的sku，更新 :提单、运单、入库单、库存、库存日志 
	 */
	@Transactional(readOnly = false)
	public void updateAllSku(String productColorCountry,String sku){
		//提单
		String sql="UPDATE psi_lading_bill_item AS a SET a.`sku`=:p1 WHERE a.`sku`=:p2 ";
		psiProductDao.updateBySql(sql, new Parameter(sku,productColorCountry));
		sql="UPDATE lc_psi_lading_bill_item AS a SET a.`sku`=:p1 WHERE a.`sku`=:p2 ";
		psiProductDao.updateBySql(sql, new Parameter(sku,productColorCountry));
		
		//运单
		sql="UPDATE psi_transport_order_item AS a SET a.`sku`=:p1 WHERE a.`sku`=:p2 ";
		psiProductDao.updateBySql(sql, new Parameter(sku,productColorCountry));
		sql="UPDATE lc_psi_transport_order_item AS a SET a.`sku`=:p1 WHERE a.`sku`=:p2 ";
		psiProductDao.updateBySql(sql, new Parameter(sku,productColorCountry));
		
		//入库单
		sql="UPDATE psi_inventory_in_item AS a SET a.`sku`=:p1 WHERE a.`sku`=:p2 ";
		psiProductDao.updateBySql(sql, new Parameter(sku,productColorCountry));
		
		//库存
		sql="UPDATE psi_inventory AS a SET a.`sku`=:p1 WHERE a.`sku`=:p2 ";
		psiProductDao.updateBySql(sql, new Parameter(sku,productColorCountry));
		
		//库存日志
		sql="UPDATE psi_inventory_revision_log AS a SET a.`sku`=:p1,a.`remark`=CONCAT(a.`remark`,'新品Sku') WHERE a.`sku`=:p2 ";
		psiProductDao.updateBySql(sql, new Parameter(sku,productColorCountry));
		
		//2017-02-28以后修改start----
		//出库单
		sql="UPDATE psi_inventory_out_item AS a SET a.`sku`=:p1 WHERE a.`sku`=:p2 ";
		psiProductDao.updateBySql(sql, new Parameter(sku,productColorCountry));
		
		//2017-02-28以后修改end----
		//预测物流单
		sql="update psi_forecast_transport_order_item set sku=:p1 where sku=:p2";
		psiProductDao.updateBySql(sql, new Parameter(sku,productColorCountry));
	}
	
	
	@Transactional(readOnly = false)
	public void signedSample(Integer productId,String signedSimple){
		String sql = " UPDATE psi_product AS a SET a.`signed_sample`=:p2 WHERE a.`id`=:p1 ";
		psiProductDao.updateBySql(sql, new Parameter(productId,signedSimple));
	}
	
	public Map<String,List<String>> findNewProduct(){
		Map<String,List<String>> map=Maps.newHashMap();
		String sql=" SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,platform "+ 
		" FROM psi_product a JOIN mysql.help_topic b "+
		" ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.`del_flag`='0' and a.is_new='1' ";
		List<Object[]> list= this.psiProductDao.findBySql(sql);
		for (Object[] obj: list) {
			String name=obj[0].toString();
			String country=obj[1].toString();
			if(StringUtils.isNotBlank(country)){
				String[] arrCountry=country.split(",");
				for (String arr : arrCountry) {
					List<String> temp=map.get(arr);
					if(temp==null){
						temp=Lists.newArrayList();
						map.put(arr, temp);
					}
					temp.add(name);
				}
			}
		}
		return map;
	}
	
	
	public Map<String,String> findAddMonth(){
		Map<String,String> map=Maps.newHashMap();
		String sql=" SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,added_month "+ 
		" FROM psi_product a JOIN mysql.help_topic b "+
		" ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.`del_flag`='0' ";
		List<Object[]> list= this.psiProductDao.findBySql(sql);
		for (Object[] obj: list) {
			String name=obj[0].toString();
			if(obj[1]==null){
				map.put(name, "");
			}else{
				String addedMonth=obj[1].toString();
				if(addedMonth.compareTo("2017-01-01")>=0){
					map.put(name, "新品");
				}else{
					map.put(name, "普通品");
				}
			}
		}
		return map;
	}
	
	/**
	 *根据产品类型，找出产品线名称 
	 */
	public String getLineName(String type){
		String sql="SELECT a.`name`,a.`id` FROM psi_product_type_dict AS a,	psi_product_type_group AS b,sys_dict AS c WHERE a.`id`=b.`id` AND b.`dict_id`=c.`id` AND c.`value`=:p1 AND c.`del_flag`='0' ";
		List<Object[]> list = this.psiProductDao.findBySql(sql, new Parameter(type));
		if(list!=null&&list.size()>0){
			return list.get(0)[0]+","+list.get(0)[1];
		}
		return null;
	}
	
	/**
	 *根据产品线id，获取负责人
	 */
	public String getResponseName(String lineId){
		String sql=" SELECT GROUP_CONCAT(aa.name) FROM (SELECT DISTINCT(b.`name`) FROM psi_product_group_user AS a,sys_user AS b WHERE a.`responsible` LIKE CONCAT('%',b.`id`,'%') AND LENGTH(b.`id`)>10 AND a.`product_group_id`=:p1 AND a.`del_flag`='0' GROUP BY b.`name`) AS aa";
		List<String> list = this.psiProductDao.findBySql(sql, new Parameter(lineId));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}

	public Map<String,String> findAllCheckList(String line){
		String sql="select d.check_list,concat(d.brand,' ',d.model) from psi_product d  ";
		if(StringUtils.isNotBlank(line)){
			sql+=" JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type' "+
			    " JOIN psi_product_type_group g ON t.id=g.`dict_id` ";
		}
		sql+="where d.check_state='1' and d.del_flag='0' and d.check_list  is not null and d.check_list !='' ";
		if(StringUtils.isNotBlank(line)){
			sql+=" and g.id='"+line+"' ";
		}
		Map<String,String>  map=Maps.newHashMap();
		List<Object[]> list=psiProductDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				map.put(obj[1].toString(),obj[0].toString());
			}
		}
		return map;
	}
	
	
	public Map<String,String> findAllTechFileList(String line){
		String sql="select d.tech_file,concat(d.brand,' ',d.model) from psi_product d  ";
		if(StringUtils.isNotBlank(line)){
			sql+=" JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type' "+
			    " JOIN psi_product_type_group g ON t.id=g.`dict_id` ";
		}
		sql+="where d.del_flag='0' and d.tech_file is not null and d.tech_file!='' ";
		if(StringUtils.isNotBlank(line)){
			sql+=" and g.id='"+line+"' ";
		}
		Map<String,String>  map=Maps.newHashMap();
		List<Object[]> list=psiProductDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				map.put(obj[1].toString(),obj[0].toString());
			}
		}
		return map;
	}
	
	public Map<String,String> findAllBomList(String line){
		String sql="select d.bom_list,concat(d.brand,' ',d.model) from psi_product d  ";
		if(StringUtils.isNotBlank(line)){
			sql+=" JOIN sys_dict t ON d.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type' "+
			    " JOIN psi_product_type_group g ON t.id=g.`dict_id` ";
		}
		sql+="where d.del_flag='0' and d.bom_list is not null and d.bom_list!='' ";
		if(StringUtils.isNotBlank(line)){
			sql+=" and g.id='"+line+"' ";
		}
		Map<String,String>  map=Maps.newHashMap();
		List<Object[]> list=psiProductDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				map.put(obj[1].toString(),obj[0].toString());
			}
		}
		return map;
	}
	
	public Map<String,String> findCreateUserMap(){
		Map<String,String> map = Maps.newHashMap();
		String sql ="SELECT CONCAT(p.`brand`,' ',p.model),r.`name` FROM psi_product  AS p  "+
		" join sys_user r on r.id=p.`create_user` where p.del_flag='0' ";
		List<Object[]> list=this.psiProductDao.findBySql(sql);
		for(Object[] object:list){
			map.put(object[0]==null?"":object[0].toString(),object[1]==null?"":object[1].toString());
		}
		return map;
	}
	
	
	/**
	 * 根据sku获取产品名
	 */
	public Map<String,String> getProductNameBySku(){
		Map<String,String> rsMap = Maps.newHashMap();
		String sql="SELECT  DISTINCT a.`sku`, CASE WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color`) END AS proName FROM psi_sku AS a WHERE a.`del_flag`='0' AND a.`product_name` != 'Inateck other' AND a.`product_name` != 'Inateck Old' ";
		List<Object[]> list=psiProductDao.findBySql(sql);
		for (Object[] obj : list) {
			String sku = obj[0].toString();
			String name = obj[1].toString();
			rsMap.put(sku, name);
		}
		
		return rsMap;
	}
	

	
	/**
	 *根据asin获取产品名 
	 */
	public Map<String,String> getProductNameByAsin(){
		Map<String,String> rsMap = Maps.newHashMap();
		String sql="SELECT  DISTINCT a.asin, CASE WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color`) END AS proName FROM psi_sku AS a WHERE a.`del_flag`='0' AND a.`product_name` != 'Inateck other' AND a.`product_name` != 'Inateck Old' AND a.asin IS NOT NULL";
		List<Object[]> list=psiProductDao.findBySql(sql);
		for (Object[] obj : list) {
			String asin = obj[0].toString();
			String name = obj[1].toString();
			rsMap.put(asin, name);
		}
		
		return rsMap;
	} 
	
	/**
	 *根据用户id获取所管辖的供应商id
	 */
	public List<Integer> getSupplierIdByUser(String userId){
		String sql="	SELECT DISTINCT b.id FROM psi_product AS a,psi_supplier AS b,psi_product_supplier AS c WHERE a.`id`=c.`product_id` AND b.id=c.`supplier_id` AND a.`del_flag`='0' AND b.`del_flag`='0' AND a.`create_user`=:p1";
		List<Integer> suppliers=psiProductDao.findBySql(sql,new Parameter(userId));
		return suppliers;
	}
	
	/**
	 * 查询上周录入的还未填写hashcode或者退税率的产品
	 * @return [型号  明细]
	 */
	public Map<String, String> findNoHashCodeOrTaxMap(){
		String sql = "select t.`model`,t.`platform`,t.`eu_hscode`,t.`us_hscode`,t.`ca_hscode`,t.`jp_hscode`,t.`tax_refund` from `psi_product` t "+
				" where t.`del_flag`='0' and t.`components`!='1' and TO_DAYS(NOW()) - TO_DAYS(t.`create_time`)<=7";
		List<Object[]> list = this.psiProductDao.findBySql(sql);
		Map<String, String> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			String model = objects[0].toString();
			String platform = objects[1].toString();
			StringBuffer stringBuffer = new StringBuffer();
			if (objects[2]==null && (platform.contains("de") || platform.contains("uk") || platform.contains("fr") || platform.contains("it") || platform.contains("es"))) {
				stringBuffer.append("未录入EU hashcode,");
			}
			if (objects[3]==null && platform.contains("com")) {
				stringBuffer.append("未录入US hashcode,");
			}
			if (objects[4]==null && platform.contains("ca")) {
				stringBuffer.append("未录入CA hashcode,");
			}
			if (objects[5]==null && platform.contains("jp")) {
				stringBuffer.append("未录入JP hashcode,");
			}
			if (objects[6]==null) {
				stringBuffer.append("未录入退税率,");
			}
			if (StringUtils.isNotEmpty(stringBuffer.toString())) {
				rs.put(model, stringBuffer.toString().substring(0, stringBuffer.toString().length()-1));
			}
		}
		return rs;
	}
	
	public Map<String,String> findChineseName(){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT brand,model,chinese_name FROM psi_product a  WHERE a.`del_flag`='0' ";
		List<Object[]> list= this.psiProductDao.findBySql(sql);
		for (Object[] obj: list) {
			String pname=obj[0].toString()+" "+obj[1].toString();
			if(pname.endsWith("US")||pname.endsWith("JP")||pname.endsWith("UK")||pname.endsWith("EU")||pname.endsWith("DE")){
				pname=pname.replace("US","").replace("JP","").replace("UK","").replace("EU","").replace("DE","");
			}
			String cname=(obj[2]==null?"":obj[2].toString()).split(";")[0];
			map.put(pname, cname);
		}
		return map;
	}
	
	
	public Map<String,String> getProductNameByCountrySku(){
		Map<String,String> rsMap = Maps.newHashMap();
		String sql="SELECT  DISTINCT a.`sku`, country,CASE WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color`) END AS proName FROM psi_sku AS a WHERE a.`del_flag`='0' AND a.`product_name` != 'Inateck other' AND a.`product_name` != 'Inateck Old' ";
		List<Object[]> list=psiProductDao.findBySql(sql);
		for (Object[] obj : list) {
			String sku = obj[0].toString();
			String  country = obj[1].toString();
			String name = obj[2].toString();
			rsMap.put(country+"_"+sku, name);
		}
		return rsMap;
	}
	
	/**
	 *查询产品moq 
	 */
	public Map<Integer,Integer> getMoq(){
		Map<Integer,Integer> rsMap = Maps.newHashMap();
		String sql="SELECT a.id,a.`min_order_placed` FROM psi_product AS  a";
		List<Object[]> list=psiProductDao.findBySql(sql);
		for (Object[] obj : list) {
			Integer id = Integer.parseInt(obj[0].toString());
			Integer moq = Integer.parseInt(obj[1].toString());
			rsMap.put(id, moq);
		}
		return rsMap;
	}
	
	public List<Integer> findProductIdByType(String type){
		String sql="SELECT a.id FROM psi_product AS  a where type=:p1 and del_flag='0' ";
		return psiProductDao.findBySql(sql,new Parameter(type));
	}
	
	public Map<String,Float> findTranGw(){
	   Map<String,Float> map=Maps.newHashMap();
	   String sql="SELECT CONCAT(b.`brand`, ' ', b.`model`),b.color,TRUNCATE(CASE WHEN b.volume_ratio <= 167  THEN 167 * b.box_volume / b.pack_quantity  ELSE b.gw / b.pack_quantity  END,2) AS tranGw FROM psi_product AS b WHERE  b.`del_flag` = '0' ";
	   List<Object[]> list=psiProductDao.findBySql(sql);
	   for(Object[] obj:list){
		   String color=(obj[1]==null?"":obj[1].toString());
		   String name="";
		   if(StringUtils.isBlank(color)){
			   name=obj[0].toString();
			   map.put(name,obj[1]==null?0:Float.parseFloat(obj[2].toString()));
		   }else{
			   if(color.contains(",")){
				   String[] arr=color.split(",");
				   for(String c:arr){
					   name=obj[0].toString()+"_"+c;
					   map.put(name,obj[1]==null?0:Float.parseFloat(obj[2].toString()));
				   }
			   }else{
				   name=obj[0].toString()+"_"+color;
				   map.put(name,obj[1]==null?0:Float.parseFloat(obj[2].toString()));
			   }
		   }
	   }
	   return map;
	}
	
	public Map<String,Set<String>> findGroupName(String model,String color){
		String sql="";
		Map<String,Set<String>> map=Maps.newHashMap();
		List<Object[]> nameList=Lists.newArrayList();
		if(StringUtils.isBlank(color)){
			sql="SELECT DISTINCT CONCAT(brand,' ',model),CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME "+
					" FROM psi_product a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) "+
					" WHERE  CONCAT(brand,' ',a.`model_short`)=:p1 AND a.`del_flag`='0' order by name desc";
			nameList=psiProductDao.findBySql(sql,new Parameter(model));
		}else{
			sql="SELECT DISTINCT CONCAT(brand,' ',model),CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME "+
					" FROM psi_product a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) "+
					" WHERE   CONCAT(brand,' ',a.`model_short`)=:p1 AND a.`del_flag`='0' AND SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)=:p2 order by name desc ";
			nameList=psiProductDao.findBySql(sql,new Parameter(model,color));
		}
		Set<String> nameSets=Sets.newLinkedHashSet();
		Set<String> nameWithColorSets=Sets.newLinkedHashSet();
		for(Object[] obj:nameList){
			String name=obj[0].toString();
			String nameWithColor=obj[1].toString();
			nameSets.add(name);
			nameWithColorSets.add(nameWithColor);
		}
		map.put("0",nameSets);
		map.put("1",nameWithColorSets);
		return map;
	}
	
	/**
	 * 用于删除总库存为0且淘汰的产品相关联信息
	 * @param id	产品id
	 * @param color	颜色
	 */
	@Transactional(readOnly = false)
	public void delProductByColor(Integer id, String color){
		Parameter parameter = new Parameter(id);
		//删除psi_sku
		String sql = "UPDATE `psi_sku` t SET t.`del_flag`='1' WHERE t.`product_id`=:p1 ";
		if (StringUtils.isNotEmpty(color)) {
			sql +=  " AND t.`color`=:p2";
			parameter = new Parameter(id, color);
		}
		psiProductDao.updateBySql(sql, parameter);
		//删除psi_barcode
		sql = "UPDATE `psi_barcode` t SET t.`del_flag`='1' WHERE t.`psi_product`=:p1 ";
		if (StringUtils.isNotEmpty(color)) {
			sql +=  " AND t.`product_color`=:p2";
			parameter = new Parameter(id, color);
		}
		psiProductDao.updateBySql(sql, parameter);
		//删除psi_product_eliminate
		sql = "UPDATE `psi_product_eliminate` t SET t.`del_flag`='1' WHERE t.`product_id`=:p1 ";
		if (StringUtils.isNotEmpty(color)) {
			sql +=  " AND t.`color`=:p2";
			parameter = new Parameter(id, color);
		}
		psiProductDao.updateBySql(sql, parameter);
		//删除psi_product_attribute
		sql = "UPDATE `psi_product_attribute` t SET t.`del_flag`='1' WHERE t.`product_id`=:p1 ";
		if (StringUtils.isNotEmpty(color)) {
			sql +=  " AND t.`color`=:p2";
			parameter = new Parameter(id, color);
		}
		psiProductDao.updateBySql(sql, parameter);
		//删除psi_product_tiered_price
		sql = "UPDATE `psi_product_tiered_price` t SET t.`del_flag`='1' WHERE t.`product_id`=:p1 ";
		if (StringUtils.isNotEmpty(color)) {
			sql +=  " AND t.`color`=:p2";
			parameter = new Parameter(id, color);
		}
		psiProductDao.updateBySql(sql, parameter);
	}
	
	
	/**
	 *获取带颜色的产品id，MOQ 
	 */
	public Map<String,String>  getProductColorPackInfo(){
		Map<String,String> rs = Maps.newHashMap();
		String sql="SELECT DISTINCT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME,a.id,a.`pack_quantity`,a.`min_order_placed`  FROM psi_product a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1)  WHERE  a.`del_flag`='0'  ";
		List<Object[]> list=psiProductDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				String name = obj[0].toString();
				String productId = obj[1].toString();
				String pack = obj[2].toString();
				rs.put(name, productId+","+pack+","+(obj[1]==null?"未设置":obj[1].toString()));
			}
		}
		return rs;
	}
	
	public List<Object[]> findExcepitonSize(String country){
		List<Object[]> list=Lists.newArrayList();
		if(StringUtils.isNotBlank(country)){
			String sql="SELECT d.country,d.`product_name`,d.asin,d.sku,ROUND(d.`package_length`*2.54,2) LENGTH,ROUND(d.`package_width`*2.54,2) width,ROUND(d.`package_height`*2.54,2) height, "+
		            " p.`product_pack_length`,p.`product_pack_width`,p.`product_pack_height` "+
					" FROM amazoninfo_posts_detail d   "+
					" JOIN psi_product p ON CONCAT(p.`brand`,' ',p.`model`)=SUBSTRING_INDEX(d.`product_name`,'_',1)  "+
					"  WHERE d.`query_time`>DATE_SUB(CURDATE(),INTERVAL 1 DAY) and d.country=:p1 AND d.`product_name` IS NOT NULL  "+
					"  AND (ROUND(d.`package_length`*2.54,2)-p.`product_pack_length`>0.5 OR   "+
					"  ROUND(d.`package_width`*2.54,2)-p.`product_pack_width`>0.5 OR  "+
					"  ROUND(d.`package_height`*2.54,2)-p.`product_pack_height`>0.5  "+
					" ) AND p.`del_flag`='0' ";
			list=psiProductDao.findBySql(sql,new Parameter(country));
		}else{
			String sql="SELECT d.country,d.`product_name`,d.asin,d.sku,ROUND(d.`package_length`*2.54,2) LENGTH,ROUND(d.`package_width`*2.54,2) width,ROUND(d.`package_height`*2.54,2) height, "+
		            " p.`product_pack_length`,p.`product_pack_width`,p.`product_pack_height` "+
					" FROM amazoninfo_posts_detail d   "+
					" JOIN psi_product p ON CONCAT(p.`brand`,' ',p.`model`)=SUBSTRING_INDEX(d.`product_name`,'_',1)  "+
					"  WHERE d.`query_time`>DATE_SUB(CURDATE(),INTERVAL 1 DAY)  AND d.`product_name` IS NOT NULL  "+
					"  AND (ROUND(d.`package_length`*2.54,2)-p.`product_pack_length`>0.5 OR   "+
					"  ROUND(d.`package_width`*2.54,2)-p.`product_pack_width`>0.5 OR  "+
					"  ROUND(d.`package_height`*2.54,2)-p.`product_pack_height`>0.5  "+
					" ) AND p.`del_flag`='0' ";
			list=psiProductDao.findBySql(sql);
		}
		return list;
	}
	
	
	@Transactional(readOnly=false)
	public void reviewSave(PsiProduct product){
		product.setReviewSta("1");
		if(!"1".equals(product.getComponents())){
			this.genPlan(product);
		}
		this.psiProductDao.save(product);
	}
	
	@Transactional(readOnly=false)
	public void genPlan(PsiProduct product){
		String colors = product.getColor();
		String countrys ="de,com,jp";
		if("keyboard".equals(product.getType().toLowerCase())||"1".equals(product.getHasPower())) {
			countrys="de,com,jp,uk";
		}
		PurchasePlan plan = new PurchasePlan();
		plan.setCreateDate(new Date());
		plan.setCreateUser(UserUtils.getUser());
		plan.setPlanSta("1");
		List<PurchasePlanItem> items = Lists.newArrayList();
		for(String color:colors.split(",") ){
			for(String country:countrys.split(",")){
				items.add(new PurchasePlanItem(plan, product, product.getName(), color, country, 0, "0"));
			}
		}
		if(items.size()>0){
			plan.setItems(items);
			planDao.save(plan);
		}
	}

	public List<String> findAllHidden() {
		String sql = "SELECT DISTINCT t.`product_name` FROM `psi_product_hidden` t";
		return psiProductDao.findBySql(sql);
	}

	@Transactional(readOnly = false)
	public void saveHidden(List<String> productNameList) {
		if (productNameList == null) {
			return;
		}
		Date date = new Date();
		String sql = "INSERT INTO `psi_product_hidden`(product_name,data_date) VALUES(:p1, :p2)";
		for (String productName : productNameList) {
			psiProductDao.updateBySql(sql, new Parameter(productName, date));
		}
	}
	
	public List<String> findComponents(){
		String sql="SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME "+ 
			" FROM psi_product a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) "+
			" WHERE  a.`components`='1' AND a.`del_flag`='0' ";
		return psiProductDao.findBySql(sql);
	}
	
	/**
	 * 临时初始哈产品定位(excel格式要求，第一列产品名，第二列定位)
	 * @param flag eu,jp
	 */
	@Transactional(readOnly = false)
	public void importPosition(String flag) {
		try {
			String path = this.getClass().getResource("").getPath();
			if (path.contains(":")) {
				path = path.substring(1, path.lastIndexOf("classes"));
			} else {
				path = path.substring(0, path.lastIndexOf("classes"));
			}
			path = path.replace("%20", " ");
			path = path + "classes/"+flag+".xlsx";
			File file = new File(path);
			//File file = new File("e:/"+flag+".xlsx");
			if (!file.exists()) {
				logger.warn("产品定位文件不存在" + file.getAbsolutePath());
			}
			InputStream inputStream = new FileInputStream(file);
			List<String[]> dataList = ExcelUtil.read(inputStream);
			List<PsiProduct> list = findAll();
			Map<String, PsiProduct> proMap = Maps.newHashMap();
			for (PsiProduct psiProduct : list) {
				proMap.put(psiProduct.getModel(), psiProduct);
			}
			String sql = "UPDATE `psi_product_eliminate` t SET t.`is_sale`=:p1 WHERE t.`product_id`=:p2 AND t.`color` like :p3 and t.is_sale !='4'";
			if ("eu".equals(flag)) {
				sql += " AND t.`country` !='jp'";
			}
			if ("jp".equals(flag)) {
				sql += " AND t.`country` ='jp'";
			}
			String planSql = "UPDATE `psi_purchase_plan` p, `psi_purchase_plan_item` i SET p.`product_position`=:p1"+
							" WHERE p.`id`=i.`plan_id` AND i.`product_id`=:p2 AND i.`color_code` like :p3 ";
			for (String[] str : dataList) {
				String name = str[0].trim();
				String position = str[1];
				position = changePosition(position);
				String color = "";
				if (name.contains("_")) {
					color = name.split("_")[1];
					name = name.split("_")[0];
				}
				name = name.trim();
				name = name.replace("Inateck ", "");
				PsiProduct product = null;
				boolean ukFlag = false;
				if ("eu".equals(flag)) {
					product = proMap.get(name);
					if (product == null) {
						ukFlag = true;
						product = proMap.get(name+"EU");
					}
					if (product == null) {
						product = proMap.get(name+"UK");
					}
					if (product == null) {
						logger.info(flag + "初始化产品定位未解析正确的产品，请手动处理" + str[0]);
						continue;
					}
				}
				if ("jp".equals(flag)) {
					product = proMap.get(name);
					if (product == null) {
						product = proMap.get(name+"JP");
					}
					if (product == null) {
						logger.info(flag + "初始化产品定位未解析正确的产品，请手动处理" + str[0]);
						continue;
					}
				}
				if (StringUtils.isEmpty(color) && StringUtils.isNotEmpty(product.getColor())) {
					color = "%%";
				}
				psiProductDao.updateBySql(sql, new Parameter(position, product.getId(), color));
				if ("eu".equals(flag)) {
					//更新产品表is_sale
					String positionSql = "SELECT t.`product_id`,t.`is_sale` FROM `psi_product_eliminate` t WHERE t.`product_name` =:p1 AND t.`del_flag`='0' ORDER BY FIELD(t.`country`,'de','uk','fr','jp','it','es','ca','mx','com','com2','com3')";
					List<Object[]> positionList = psiProductDao.findBySql(positionSql, new Parameter(product.getName()));
					String productIsSale = "0";	//是否在售标记
					for (Object[] obj : positionList) {
						productIsSale = obj[1].toString();
						break;
					}
					if (product != null && !productIsSale.equals(product.getIsSale())) {
						product.setIsSale(productIsSale);
						save(product);
					}
					psiProductDao.updateBySql(planSql, new Parameter(position, product.getId(), color));
					if (ukFlag) {
						product = proMap.get(name+"UK");
						if (product != null) {
							//更新产品表is_sale
							positionList = psiProductDao.findBySql(positionSql, new Parameter(product.getName()));
							productIsSale = "0";	//是否在售标记
							for (Object[] obj : positionList) {
								productIsSale = obj[1].toString();
								break;
							}
							if (product != null && !productIsSale.equals(product.getIsSale())) {
								product.setIsSale(productIsSale);
								save(product);
							}
							psiProductDao.updateBySql(planSql, new Parameter(position, product.getId(), color));
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("导入产品定位失败", e);
		}
	}

	private String changePosition(String position){
		if (position.contains("爆")) {
			position = "1";
		} else if(position.contains("利润")) {
			position = "2";
		} else if(position.contains("主力")) {
			position = "3";
		} else if(position.contains("淘汰")) {
			position = "4";
		} else {
			logger.info("异常定位标识，修正后重试" + position);
		}
		return position;
	}
	
	public Map<String,String> findAccountByCountry(){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT DISTINCT p.sku,p.`country`,p.`account_name` FROM psi_sku p WHERE p.`del_flag`='0' and p.account_name is not null";
		List<Object[]> list=psiProductDao.findBySql(sql);
		for(Object[] obj:list){
			String sku=obj[0].toString();
			String country=obj[1].toString();
			String account=obj[2].toString();
			map.put(sku+"_"+country,account);
		}
		return map;
	}
	
	public  PsiProduct findSize(String model){
		String sql="SELECT p.`product_pack_length`*0.3937008,p.`product_pack_width`*0.3937008,p.`product_pack_height`*0.3937008,ifnull(p.`product_pack_weight`,p.weight)*0.035274 "+
		           " FROM psi_product p WHERE p.`model`=:p1 AND p.`del_flag`='0' ";
		List<Object[]> list=psiProductDao.findBySql(sql,new Parameter(model));
		if(list!=null&&list.size()>0){
			Object[] obj=list.get(0);
			PsiProduct p=new PsiProduct();
			if(obj[0]!=null){
				p.setProductPackLength((BigDecimal)obj[0]);
			}
			if(obj[1]!=null){
				p.setProductPackWidth((BigDecimal)obj[1]);
			}
			if(obj[2]!=null){
				p.setProductPackHeight((BigDecimal)obj[2]);
			}
			if(obj[3]!=null){
				p.setProductPackWeight((BigDecimal)obj[3]);
			}
			return p;
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public void updateUseBarcode(String country,Integer productId,String color){
		 String sql= "UPDATE psi_barcode SET barcode='' WHERE product_platform=:p1 and psi_product=:p2 and product_color=:p3 ";
		 psiProductDao.updateBySql(sql, new Parameter(country,productId,color));
				sql= "UPDATE psi_sku  SET use_barcode='0' WHERE country=:p1 and product_id=:p2 and color=:p3";
		 psiProductDao.updateBySql(sql, new Parameter(country,productId,color));
	}
	
	public  List<PsiProduct> findTempSize(){
		String sql="SELECT concat(brand,' ',model),ifnull(p.`product_pack_length`,p.length)*0.3937008,ifnull(p.`product_pack_width`,width)*0.3937008,ifnull(p.`product_pack_height`,height)*0.3937008,ifnull(p.`product_pack_weight`,p.weight)*0.0022046 "+
		           " FROM psi_product p WHERE p.`del_flag`='0' ";
		List<Object[]> list=psiProductDao.findBySql(sql);
		List<PsiProduct> rs=Lists.newArrayList();
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				PsiProduct p=new PsiProduct();
				p.setBrand(obj[0].toString());
				if(obj[1]!=null){
					p.setProductPackLength((BigDecimal)obj[1]);
				}
				if(obj[2]!=null){
					p.setProductPackWidth((BigDecimal)obj[2]);
				}
				if(obj[3]!=null){
					p.setProductPackHeight((BigDecimal)obj[3]);
				}
				if(obj[4]!=null){
					p.setProductPackWeight((BigDecimal)obj[4]);
				}
				rs.add(p);
			}
			
		}
		return rs;
	}
	
}
