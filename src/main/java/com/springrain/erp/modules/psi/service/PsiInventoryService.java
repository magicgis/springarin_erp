/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MapValueComparator;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonRemovalOrder;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.psi.dao.PsiInventoryDao;
import com.springrain.erp.modules.psi.dao.PsiInventoryRevisionLogDao;
import com.springrain.erp.modules.psi.dao.PsiQualityChangeBillDao;
import com.springrain.erp.modules.psi.dao.PsiSkuChangeBillDao;
import com.springrain.erp.modules.psi.entity.FbaInboundDto;
import com.springrain.erp.modules.psi.entity.ProductSalesInfo;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiInventoryDto;
import com.springrain.erp.modules.psi.entity.PsiInventoryDtoByInStock;
import com.springrain.erp.modules.psi.entity.PsiInventoryDtoByInStockWithWarehouseCode;
import com.springrain.erp.modules.psi.entity.PsiInventoryFba;
import com.springrain.erp.modules.psi.entity.PsiInventoryInnerDto;
import com.springrain.erp.modules.psi.entity.PsiInventoryRevisionLog;
import com.springrain.erp.modules.psi.entity.PsiInventoryTotalDto;
import com.springrain.erp.modules.psi.entity.PsiInventoryTotalDtoByInStock;
import com.springrain.erp.modules.psi.entity.PsiInventoryWarn;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiQualityChangeBill;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.entity.PsiSkuChangeBill;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.scheduler.PsiConfig;
import com.springrain.erp.modules.sys.dao.GenerateSequenceDao;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 采购付款明细Service
 * @author Michael
 * @version 2014-12-24
 */
@Component
@Transactional(readOnly = true)
public class PsiInventoryService extends BaseService {
	
	
	@Autowired
	private PsiInventoryDao 				psiInventoryDao;
	@Autowired
	private PsiInventoryRevisionLogDao 		inventoryLogDao;
	@Autowired
	private StockService 					stockService;
	@Autowired
	private PsiProductService 				psiProductService;
	@Autowired
	private PsiInventoryRevisionLogService  psiInventoryLogService;
	@Autowired   
	private PsiSkuChangeBillDao 			psiSkuChangeBillDao;
	@Autowired
	private GenerateSequenceDao 			genSequenceDao;
	@Autowired
	private ProductSalesInfoService 		productSalesInfoService;
	@Autowired
	private AmazonProduct2Service 			amazonProduct2Service;
	@Autowired
	private PsiProductTieredPriceService 	tieredPriceService;
	@Autowired
	private SaleReportService 				saleReportService;
	@Autowired
	private PsiQualityChangeBillDao     	qualityChangeBillDao;
	@Autowired
	private MailManager 					mailManager;
	@Autowired
	private PsiProductEliminateService 		psiProductEliminateService;
	
	
	public PsiInventory get(Integer id) {
		return psiInventoryDao.get(id);
	}
	
	
	public Page<PsiInventory> find(Page<PsiInventory> page, PsiInventory psiInventory) {
		DetachedCriteria dc = psiInventoryDao.createDetachedCriteria();
		if(psiInventory.getWarehouse()!=null){
			dc.add(Restrictions.eq("warehouse",psiInventory.getWarehouse()));
			
			if(130==psiInventory.getWarehouse().getId().intValue()&&"1".equals(psiInventory.getRemark())){
				dc.add(Restrictions.and(Restrictions.ne("remark",""),Property.forName("remark").isNotNull()));
			}
		}
		
		if(psiInventory.getProductName()!=null&&!psiInventory.getProductName().equals("")){
			dc.add(Restrictions.like("productName","%"+psiInventory.getProductName()+"%"));
		}
		
		if(psiInventory.getCountryCode()!=null&&!psiInventory.getCountryCode().equals("")){
			dc.add(Restrictions.eq("countryCode",psiInventory.getCountryCode()));
		}
		
		if(StringUtils.isEmpty(page.getOrderBy())){
			dc.addOrder(Order.desc("updateDate"));
		}
		
		return psiInventoryDao.findGroupByProduct(page, dc);
	}
	
	public List<PsiInventory> find(Set<String> productNames,Integer warehouseId) {
		DetachedCriteria dc = psiInventoryDao.createDetachedCriteria();
		if(warehouseId!=null){
			dc.add(Restrictions.eq("warehouse.id",warehouseId));
		}
		
		if(productNames!=null&&productNames.size()>0){
			dc.add(Restrictions.in("productName",productNames));
		}
		return psiInventoryDao.find(dc);
	}
	
	
	
	
	public List<PsiInventory> findByProductAndStock(Integer productId,String colorCode, PsiInventory inventory) {
		DetachedCriteria dc = psiInventoryDao.createDetachedCriteria();
		dc.add(Restrictions.eq("productId", productId));
		dc.add(Restrictions.eq("colorCode", colorCode));
		if(inventory.getWarehouse()!=null){
			dc.add(Restrictions.eq("warehouse",inventory.getWarehouse()));
		}
		
		if(inventory.getProductName()!=null&&!inventory.getProductName().equals("")){
			dc.add(Restrictions.like("productName","%"+inventory.getProductName()+"%"));
		}
		
		if(inventory.getCountryCode()!=null&&!inventory.getCountryCode().equals("")){
			dc.add(Restrictions.eq("countryCode",inventory.getCountryCode()));
		}
		Map<String,PsiInventory> temMap =Maps.newHashMap();
		List<PsiInventory> temList= psiInventoryDao.findGroupByCountryAndColor(dc);
		for (PsiInventory in:temList) {
			temMap.put(in.getCountryCode(), in);
		}
		List<PsiInventory> resList = Lists.newArrayList();
		if(temMap.size()>0){
			if(temMap.get("de")!=null){
				resList.add(temMap.get("de"));
			}
			if(temMap.get("fr")!=null){
				resList.add(temMap.get("fr"));
			}
			if(temMap.get("it")!=null){
				resList.add(temMap.get("it"));
			}
			if(temMap.get("es")!=null){
				resList.add(temMap.get("es"));
			}
			if(temMap.get("uk")!=null){
				resList.add(temMap.get("uk"));
			}
			if(temMap.get("com")!=null){
				resList.add(temMap.get("com"));
			}
			if(temMap.get("com2")!=null){
				resList.add(temMap.get("com2"));
			}
			if(temMap.get("com3")!=null){
				resList.add(temMap.get("com3"));
			}
			if(temMap.get("ca")!=null){
				resList.add(temMap.get("ca"));
			}
			if(temMap.get("jp")!=null){
				resList.add(temMap.get("jp"));
			}
			if(temMap.get("mx")!=null){
				resList.add(temMap.get("mx"));
			}
		}
		return resList;
	}
	
	
	
	public List<PsiInventory> findInventorySum(Integer productId, Integer warehouseId,String countryCode,String colorCode) {
		DetachedCriteria dc = psiInventoryDao.createDetachedCriteria();
		dc.add(Restrictions.eq("productId", productId));
		if(warehouseId!=null){
			dc.add(Restrictions.eq("warehouse.id",warehouseId));
		}
		if(countryCode!=null&&!countryCode.equals("")){
			dc.add(Restrictions.eq("countryCode",countryCode));
		}
		if(colorCode!=null&&!colorCode.equals("All")){
			dc.add(Restrictions.eq("colorCode",colorCode));
		}
		//return psiInventoryDao.findInventorySum(dc);
		return psiInventoryDao.find(dc);
	}
	
	//查询采购订单的历史备注
	public String findHisPurchaseRemark(String orderNo, String productNameColor,String country){
		StringBuffer sb = new StringBuffer();
		String sql=" SELECT MAX(aa.createDate),aa.`remark` FROM (SELECT DISTINCT DATE_FORMAT(b.`create_date`,'%Y-%m-%d') AS createDate,a.`remark`,a.`product_name`, a.`country_code`, a.`color_code` " +
				" FROM lc_psi_his_purchase_order_item AS a,lc_psi_his_purchase_order AS b WHERE  a.`purchase_order_id`=b.`id` AND a.`del_flag`='0' AND " +
				" b.`order_no`=:p1 AND (CASE WHEN  a.`color_code`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END) =:p2 AND a.`country_code`=:p3  AND a.`remark` <>'') AS aa GROUP BY aa.`remark`";
		List<Object[]>  list = this.psiInventoryDao.findBySql(sql, new Parameter(orderNo,productNameColor,country));
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				if(obj[0]!=null&&obj[1]!=null){
					sb.append(obj[0].toString()).append(","+obj[1].toString()+"</b><br/>");
				}
			}
		}
		return sb.toString();
	}
	
	/***
	 *根据sku获得产品id和国家信息（装箱数）
	 */
	public Map<String,String> findProductInfosBySku(Set<String> skus) {
		Map<String,String> map = Maps.newHashMap();
		String sql="SELECT a.sku,a.`product_id`,a.`country_code` FROM psi_inventory AS a WHERE a.`sku` IN :p1  GROUP BY a.`sku`";
		List<Object[]>  list = this.psiInventoryDao.findBySql(sql, new Parameter(skus));
		for(Object[] obj:list){
			map.put(obj[0].toString(), obj[1].toString()+","+obj[2].toString());
		}
		return map;
	}
	
	public String getFbaTip(String country,String name){
		String sql = "SELECT a.`sku`,a.`fulfillable_quantity` FROM psi_inventory_fba a,(SELECT DISTINCT a.`sku` FROM psi_inventory a WHERE CONCAT(a.`product_name`,CASE WHEN a.`color_code`!='' THEN '_' ELSE '' END,a.`color_code`) = :p1 AND a.`country_code` = :p2) b WHERE BINARY(a.`sku`) = b.sku and a.country = :p2 ORDER BY a.`data_date` DESC ";
		List<Object[]> objects=this.psiInventoryDao.findBySql(sql,new Parameter(name,country));
		StringBuilder rs = new StringBuilder();
		String sku = "";
		Set<String> skus = Sets.newHashSet(); 
		for (Object[] objs : objects) {
			sku = objs[0].toString();
			if(skus.contains(sku)){
				break;
			}
			rs.append(sku).append(":").append(objs[1].toString()).append("<br/>") ;
			skus.add(sku);
		}
		return rs.toString();
	}
	
	
	
	//通过库存日志，直接更新库存数据
	@Transactional(readOnly = false)
	public String getAdjustInventoryDataBylog(Integer warehouseId){
		String tips="";
		String sql="SELECT b.sku,SUM(b.`quantity`),a.`new_quantity` FROM psi_inventory_revision_log AS b,psi_inventory AS a WHERE b.`warehouse_id`=:p1 AND a.`warehouse_id`=b.`warehouse_id` AND a.`sku`=b.`sku` AND b.data_type='new' GROUP BY b.`sku` HAVING  a.`new_quantity`<>SUM(b.`quantity`)";
		List<Object[]> objects=this.psiInventoryDao.findBySql(sql,new Parameter(warehouseId));
		if(objects.size()>0){
			StringBuffer sb =new StringBuffer("");
			for(Object[] object:objects){
				String sku =object[0].toString();
				BigDecimal logQuantity = (BigDecimal)object[1];
				Integer    newQuantity = (Integer)object[2];
				String updateSql="UPDATE psi_inventory AS a SET a.`new_quantity`=:p1 WHERE a.`sku`=:p2 AND a.`warehouse_id`=:p3 AND a.`new_quantity`=:p4";
				this.inventoryLogDao.updateBySql(updateSql, new Parameter(logQuantity,sku,warehouseId,newQuantity));
				sb.append("sku："+sku+"日志数量："+logQuantity+"库存原数量："+newQuantity);
			}
			tips=sb.toString();
		}else{
			return null;
		}
		return tips;
	}
	
	public List<PsiInventory> findProductQuantity(){
	//	String sqlString="SELECT sku,CONCAT(`product_name`,CASE WHEN `color_code`!='' THEN CONCAT ('_',`color_code`) ELSE '' END) product,country_code,new_quantity FROM psi_inventory WHERE new_quantity<15 and new_quantity>0 AND warehouse_id=19 ";
	/*	String sql="SELECT s.`sku`,CONCAT(p.`product_name`,CASE WHEN p.`color_code`!='' THEN CONCAT ('_',p.`color_code`) ELSE '' END) product,s.`country`,t.new_quantity,s.`asin` " +
				" FROM (SELECT DISTINCT p.`product_name`,p.`color_code`,p.`country_code` FROM psi_inventory p WHERE p.new_quantity<15 AND p.new_quantity>0 AND p.warehouse_id=19) p  " +
				" JOIN psi_sku s ON p.`product_name`=s.`product_name` AND p.`color_code`=s.`color` AND s.`country`=p.`country_code`  " +
				" JOIN psi_inventory t ON s.`sku`=t.sku AND s.`country`=t.country_code AND t.warehouse_id=19 AND t.new_quantity>0    " +
				" JOIN amazoninfo_product2 d ON d.`sku`=s.`sku` AND d.`country`=s.`country` AND d.`country`='de' AND d.fnsku IS NULL ";*/
		String sql=" SELECT DISTINCT t.`sku`,t.productName product,t.`country`,t.`quantity`,t.`asin`,s.new_quantity FROM( " +
				" SELECT CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) AS productName,a.`sku`,a.asin,a.`quantity` ,a.country  " +
				" FROM amazoninfo_product2 a,psi_sku b WHERE a.`sku`= b.`sku` AND a.`active` = '1' AND a.`is_fba` = '0' AND NOT(a.sku LIKE '%-old%') AND a.`country` IN('de','com')) t   " +
				" JOIN (SELECT b.`country_code`,CONCAT(b.`product_name`,CASE  WHEN b.`color_code`='' THEN '' ELSE CONCAT('_',b.`color_code`) END) AS productName , SUM(b.`new_quantity`) new_quantity FROM psi_inventory b    " +
				" WHERE b.`warehouse_id` IN(19,120) GROUP BY productName,b.`country_code`) s ON t.productName=s.productName AND s.country_code=t.country  WHERE s.new_quantity<15 AND s.new_quantity<t.quantity  ";

		List<Object[]> arr=psiInventoryDao.findBySql(sql);
		List<PsiInventory> list=new ArrayList<PsiInventory>();
		for (Object[] obj : arr) {
			PsiInventory psiInventory=new PsiInventory();
			psiInventory.setSku(obj[0]==null?"":obj[0].toString());
			psiInventory.setProductName(obj[1]==null?"":obj[1].toString());
			psiInventory.setCountryCode(obj[2]==null?"":obj[2].toString());
			psiInventory.setOldQuantity(Integer.parseInt(obj[3].toString()));
			psiInventory.setAsin(obj[4]==null?"":obj[4].toString());
			psiInventory.setNewQuantity(obj[5]==null?0:Integer.parseInt(obj[5].toString()));
			list.add(psiInventory);
		}
		return list;
	}
	public PsiInventory findBySku(String sku, Integer warehouseId) {
		DetachedCriteria dc = psiInventoryDao.createDetachedCriteria();
		dc.add(Restrictions.eq("sku", sku));
		dc.add(Restrictions.eq("warehouse.id",warehouseId));
		List<PsiInventory> list =  psiInventoryDao.find(dc);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	public List<PsiInventory> findInventoryByPCCW(Integer productId,String countryCode,String colorCode, Integer warehouseId) {
		DetachedCriteria dc = psiInventoryDao.createDetachedCriteria();
		dc.add(Restrictions.eq("productId", productId));
		if(warehouseId!=null){
			dc.add(Restrictions.eq("warehouse.id",warehouseId));
		}
		if(countryCode!=null&&!countryCode.equals("")){
			dc.add(Restrictions.eq("countryCode",countryCode));
		}
		
		if(colorCode!=null){
			dc.add(Restrictions.eq("colorCode",colorCode));
		}
		List<PsiInventory> list =  psiInventoryDao.find(dc);
		return list;
	}
	
	public List<PsiInventory> findByStock(Integer warehouseId) {
		DetachedCriteria dc = psiInventoryDao.createDetachedCriteria();
		if(warehouseId!=null){
			dc.add(Restrictions.eq("warehouse.id",warehouseId));
		}
		return psiInventoryDao.find(dc);
	}
	
	
	public Map<String,Integer> findByStockAndCountry(Integer warehouseId,String country,Integer supplierId) {
		Map<String,Integer> proMap = Maps.newHashMap();
		String [] countrys=null;
		if("eu".equals(country)){
			countrys=new String[]{"de","fr","it","es","uk"};
		}else{
			countrys=new String []{country};
		}
		String sql ="";
		List<Integer> productIds=null;
		if(supplierId!=null){
			sql ="SELECT a.`product_id` FROM psi_product_supplier AS a WHERE a.`supplier_id`=:p1";
			productIds=this.psiInventoryDao.findBySql(sql,new Parameter(supplierId));
		}
		
		List<Object[]> list =null;
		if(productIds!=null&&productIds.size()>0){
			sql="SELECT a.`product_name`,SUM(a.`new_quantity`) FROM psi_inventory AS a WHERE a.`new_quantity`>0 AND a.`country_code` IN :p1 AND a.`warehouse_id`=:p2 AND a.`product_id` IN :p3 GROUP BY a.`product_name`";
			list=psiInventoryDao.findBySql(sql, new Parameter(countrys,warehouseId,productIds));
		}else{
			sql+="SELECT a.`product_name`,SUM(a.`new_quantity`) FROM psi_inventory AS a WHERE a.`new_quantity`>0 AND a.`country_code` IN :p1 AND a.`warehouse_id`=:p2  GROUP BY a.`product_name` ";
			list=psiInventoryDao.findBySql(sql, new Parameter(countrys,warehouseId));
		}
		 
		for(Object[] obj:list){
			String productName=obj[0].toString();
			Integer newQ =Integer.parseInt(obj[1].toString());
			proMap.put(productName, newQ);
		}
		return proMap;
	}
	
	/**
	 * 加入sku后此方法摒弃
	 * 
	 */
//	public  Integer getInventoryIdByPCC(Integer productId,String color,String country,Integer wareHouseId){
//		String sql ="SELECT a.id FROM psi_inventory AS a WHERE a.product_id=:p1 AND a.color_code=:p2 AND a.country_code=:p3 AND a.warehouse_id=:p4";
//		Parameter parameter =new Parameter(productId,color,country,wareHouseId);
//		List<Integer> list=this.psiInventoryDao.findBySql(sql, parameter);
//		if(list==null||list.size()==0){
//			return 0;
//		}else{
//			return list.get(0).intValue();
//		}
//		
//	}
	
	public  Integer getInventoryIdBySkuAndHouseId(String sku,Integer wareHouseId){
		String sql ="SELECT a.id FROM psi_inventory AS a WHERE a.sku=:p1 AND a.warehouse_id=:p2";
		Parameter parameter =new Parameter(sku,wareHouseId);
		List<Integer> list=this.psiInventoryDao.findBySql(sql, parameter);
		if(list==null||list.size()==0){
			return 0;
		}else{
			return list.get(0).intValue();
		}
		
	}
	
	/**
	 *获取fba运输方式 ,,本地运输也有可能有fbaId,为德国本地运输自动生成fba贴
	 */
	public Map<String,String> getFbaTranModel(){
		String sql="SELECT a.`shipment_id`,a.`model`,a.`transport_no`,a.`fba_inbound_id` FROM psi_transport_order AS a WHERE a.`transport_sta`<>'8' AND a.`transport_type`='1'" +
				" AND (a.`shipment_id` <>'' OR a.`fba_inbound_id` <>'') " +
				"UNION  (SELECT a.`shipment_id`,a.`model`,a.`transport_no`,a.`fba_inbound_id` FROM lc_psi_transport_order AS a WHERE a.`transport_sta`<>'8' AND a.`transport_type`='1'" +
				" AND (a.`shipment_id` <>'' OR a.`fba_inbound_id` <>''))";
		Map<String,String> modelMap = Maps.newHashMap();
		List<Object[]> list = this.psiInventoryDao.findBySql(sql);
		for(Object[] obj:list){
			String model ="";
			if("0".equals(obj[1].toString())){
				model="by air";
			}else if("1".equals(obj[1].toString())){
				model="by sea";
			}else if("2".equals(obj[1].toString())){
				model="by express";
			}else if("3".equals(obj[1].toString())){
				model="by train";
			}
			if(obj[3]!=null){
				String fbaIds = obj[3].toString();
				for(String fbaId:fbaIds.split(",")){
					if(StringUtils.isNotEmpty(fbaId)){
						modelMap.put(fbaId, model+","+obj[2]);
					}
				}
				
			}
			if(obj[0]!=null){ {
				String[] shipArr=obj[0].toString().split(",");
					for (String sid : shipArr) {
						if(StringUtils.isNotEmpty(sid)){
							modelMap.put(sid, model+","+obj[2]);
						}
					}
				}
			}
		}
		return modelMap;
	}
	
	public  Map<String,Integer> getProductSkuByHouseId(Integer wareHouseId){
		Map<String,Integer> skuMap = Maps.newHashMap();
		String sql ="SELECT a.sku,a.new_quantity FROM psi_inventory as a WHERE a.warehouse_id=:p1";
		List<Object[]> list=this.psiInventoryDao.findBySql(sql, new Parameter(wareHouseId));
		for(Object[] object:list){
			skuMap.put(object[0].toString(), Integer.parseInt(object[1].toString()));
		}
		return skuMap;
	}
	
	
	public  Map<String,Integer> getInventorySkuMap(String warehouseCode,Set<String> skuSet){
		Map<String,Integer> skuMap = Maps.newHashMap();
		String sql ="SELECT a.sku,a.new_quantity FROM psi_inventory as a ,psi_stock AS b WHERE a.warehouse_id=b.`id` AND b.`countrycode`=:p1 and a.sku in :p2";
		List<Object[]> list=this.psiInventoryDao.findBySql(sql, new Parameter(warehouseCode,skuSet));
		for(Object[] object:list){
			skuMap.put(object[0].toString(), Integer.parseInt(object[1].toString()));
		}
		return skuMap;
	}
	
	@Transactional(readOnly = false)
	public void changeCountrySave(PsiInventory inventory){
		
		String fromSku=inventory.getSku();
		Stock warehouse=inventory.getWarehouse();
		Integer productId = inventory.getProductId();
		String  colorCode = inventory.getColorCode();
		String  productName = inventory.getProductName();
		String  warehouseName = inventory.getWarehouseName();
		Integer warehouseId   =inventory.getWarehouse().getId();
		Integer newTotal    = 0;
		Integer oldTotal    = 0;
		Integer brokenTotal = 0;
		Integer renewTotal  = 0;
		
		String batchNumber="";
		String warehouseCode=warehouse.getCountrycode();
		if("DE".equals(warehouseCode)){
			//生成批次流水号
			//batchNumber = this.genSequenceDao.genSequenceNoCode("skuBatchNo",3);
			SimpleDateFormat sdf =new SimpleDateFormat("yyMMddHHmmsss");
			batchNumber=sdf.format(new Date());
		}
		
		String content="";
		
		Map<String,PsiInventory> skuMap = Maps.newHashMap();
		Map<String,PsiInventory> inventoryMap = Maps.newHashMap();//key：sku value:po
		for(PsiInventoryRevisionLog item:inventory.getChangeItems()){
			PsiInventory tempInventory = new PsiInventory();
			//key:国家  整合new old broken renew 数据
			String key = item.getCountryCode();//这里面实际放的是sku
			item.setSku(key);
			//查出country
			PsiSku psiSku =this.psiProductService.getSkuBySku(key,"1");
			String countryCode="";
			if(psiSku!=null){
				countryCode = psiSku.getCountry();
			}else{
				//如果被转化的sku当前没被绑定，则从库里面查（新条码转老条码）
				PsiInventory temp=this.findBySku(key, warehouseId);
				if(temp==null){
					countryCode=key.substring(key.lastIndexOf("_")+1);
					PsiInventory toInventory =new PsiInventory();
					//由sku查出国家 
					toInventory.setNewQuantity(0);
					toInventory.setOldQuantity(0);
					toInventory.setBrokenQuantity(0);
					toInventory.setRenewQuantity(0);
					toInventory.setSparesQuantity(0);
					toInventory.setOfflineQuantity(0);
					toInventory.setProductId(productId);
					toInventory.setProductName(productName);
					toInventory.setColorCode(colorCode);
					toInventory.setCountryCode(countryCode);
					toInventory.setWarehouse(warehouse);
					toInventory.setWarehouseName(warehouseName);
					toInventory.setUpdateDate(new Date());
					toInventory.setSku(key);
					this.psiInventoryDao.save(toInventory);
				}else{
					countryCode=this.findBySku(key, warehouseId).getCountryCode();
				}
			}
			
			item.setCountryCode(countryCode);
			
			if(inventoryMap.get(key)==null){
				inventoryMap.put(key, this.findBySku(key, warehouseId));//获取sku,库存po
			}
			
			PsiInventory  toInventory = inventoryMap.get(key);
			if(skuMap.containsKey(key)){
				tempInventory = skuMap.get(key);
				this.setTypeDataAndLog(item,inventory, tempInventory,toInventory);
			}else{
				this.setTypeDataAndLog(item,inventory,tempInventory,toInventory);
				skuMap.put(key, tempInventory);
			}
			
		}
		
		for(Map.Entry<String,PsiInventory> entry:skuMap.entrySet()){
			String key = entry.getKey();
			PsiInventory temp = entry.getValue();
			//PsiInventory toInventory = this.findInventory(productId, warehouse, temp.getCountryCode(), colorCode);
			PsiInventory toInventory = inventoryMap.get(key);
			//如果这个对象为null 重新生成一个数据
			if(toInventory==null){
				toInventory =new PsiInventory();
				//由sku查出国家 
				String countryCode=this.psiProductService.getSkuBySku(key,"1").getCountry();
				toInventory.setNewQuantity(0);
				toInventory.setOldQuantity(0);
				toInventory.setBrokenQuantity(0);
				toInventory.setRenewQuantity(0);
				toInventory.setSparesQuantity(0);
				toInventory.setOfflineQuantity(0);
				toInventory.setProductId(productId);
				toInventory.setProductName(productName);
				toInventory.setColorCode(colorCode);
				toInventory.setCountryCode(countryCode);
				toInventory.setWarehouse(warehouse);
				toInventory.setWarehouseName(warehouseName);
				toInventory.setUpdateDate(new Date());
				toInventory.setSku(key);
			}  
			
			if(temp.getNewQuantity()!=null&&!temp.getNewQuantity().equals(0)){
				//往条码转换清单表插入数据
				String evenName="From "+fromSku+" To "+key;
				PsiSkuChangeBill skuChangeBill=new PsiSkuChangeBill(evenName, warehouseId, warehouseName, productId, productName, toInventory.getCountryCode(), toInventory.getColorCode(), fromSku, key, batchNumber, temp.getNewQuantity(), new Date(), UserUtils.getUser(), "0");
				this.psiSkuChangeBillDao.save(skuChangeBill);
			
				if("CN".equals(warehouseCode)&&temp.getNewQuantity()>=200){
					content+=evenName+",数量:"+temp.getNewQuantity()+"<br/>";
				}
			}
			
			/**
			 *如果用inventory_reversion_log表统计数算出及时库存数据，开启下面注释
			 * 
			 */
			//this.psiInventoryLogService.getSumByInventory(warehouseId,toInventory);
			if(temp.getNewQuantity()!=null){
				toInventory.setNewQuantity(temp.getNewQuantity()+toInventory.getNewQuantity());
				newTotal+=temp.getNewQuantity();
				if(toInventory.getNewQuantity()<0){
					throw new RuntimeException("SKU调换后有new、old、broken、renew有负值，同时刻有其他人操作同一条数据，操作已取消");
				}
			}
			if(temp.getOldQuantity()!=null){
				toInventory.setOldQuantity(temp.getOldQuantity()+toInventory.getOldQuantity());
				oldTotal +=temp.getOldQuantity();
				if(toInventory.getOldQuantity()<0){
					throw new RuntimeException("SKU调换后有new、old、broken、renew有负值，同时刻有其他人操作同一条数据，操作已取消");
				}
			}
			if(temp.getBrokenQuantity()!=null){
				toInventory.setBrokenQuantity(temp.getBrokenQuantity()+toInventory.getBrokenQuantity());
				brokenTotal+=temp.getBrokenQuantity();
				if(toInventory.getBrokenQuantity()<0){
					throw new RuntimeException("SKU调换后有new、old、broken、renew有负值，同时刻有其他人操作同一条数据，操作已取消");
				}
			}
			if(temp.getRenewQuantity()!=null){
				toInventory.setRenewQuantity(temp.getRenewQuantity()+toInventory.getRenewQuantity());
				renewTotal +=temp.getRenewQuantity();
				if(toInventory.getRenewQuantity()<0){
					throw new RuntimeException("SKU调换后有new、old、broken、renew有负值，同时刻有其他人操作同一条数据，操作已取消");
				}
			}
			toInventory.setUpdateDate(new Date());
			//调整到的对象更新
			this.psiInventoryDao.save(toInventory);
		}
		
		/**
		 *如果用inventory_reversion_log表统计数算出及时库存数据，开启下面注释
		 * 
		 */
		//this.psiInventoryLogService.getSumByInventory(warehouseId,inventory);
		//被调整对象更新
		/*inventory.setNewQuantity(inventory.getNewQuantity()-newTotal);
		inventory.setOldQuantity(inventory.getOldQuantity()-oldTotal);
		inventory.setBrokenQuantity(inventory.getBrokenQuantity()-brokenTotal);
		inventory.setRenewQuantity(inventory.getRenewQuantity()-renewTotal);*/
		
		if(inventory.getNewQuantity()<0||inventory.getOldQuantity()<0||inventory.getBrokenQuantity()<0||inventory.getRenewQuantity()<0){
			throw new RuntimeException("SKU调换后有new、old、broken、renew有负值，同时刻有其他人操作同一条数据，操作已取消");
		}
		inventory.setUpdateDate(new Date());
		this.psiInventoryDao.save(inventory);
		
		//如果有中国仓大于200的转码
		if("CN".equals(warehouseCode)&&StringUtils.isNotEmpty(content)){
			sendNoticeEmail("supply-chain@inateck.com,"+UserUtils.logistics1, "Hi,All<br/><br/>中国仓Sku转换超过200片，明细如下：<br/>"+content, "中国仓Sku转换(超过200片)通知", UserUtils.getUser().getEmail(), "");
		}
	}
	
	@Transactional(readOnly = false)
	public String newOldChangeSave(PsiInventory inventory){
		//如果是New_To_Offline 不生效，进行审批
		String rs="";
		boolean flag=true;
		Integer warehouseId = inventory.getWarehouse().getId();
		for(PsiInventoryRevisionLog log:inventory.getChangeItems()){
			//只要是线上转线下就通知销售确认转码      中国仓转码 或 不是中国仓装码数量<10直接生效 
			if(log.getOperationType().equals("14")&&(warehouseId.intValue()!=21&&warehouseId.intValue()!=130)&&log.getQuantity()>10){
				flag=false;
				break;
			}
			//如果是offline_to_new也需要发邮件
			if("15".equals(log.getOperationType())&&(warehouseId.intValue()!=21&&warehouseId.intValue()!=130)){
				String content="Hi,All<br/><br/>&nbsp;&nbsp;产品["+inventory.getProductNameColor()+"]线下转线上("+log.getQuantity()+")个，请知悉 <br/><br/><br/>best regards<br/>Erp System";
				String title="库存线下转线上已生成,请知悉,"+inventory.getProductNameColor()+"("+log.getQuantity()+")个";
				String email="george@inateck.com,amazon-sales@inateck.com,tim@inateck.com,supply-chain@inateck.com";
				sendNoticeEmail(email, content, title, "", "");
			}
		}
		if(flag){
			this.newOldDataSet(inventory);
			this.psiInventoryDao.save(inventory);
		}else{
			//记录转码审批表
			for(PsiInventoryRevisionLog log:inventory.getChangeItems()){
				if(log.getOperationType().equals("14")){
					Integer offId=this.createNewToOffline(inventory.getWarehouse().getId(), inventory.getSku(), log.getQuantity(), log.getRemark(),inventory.getProductId(), inventory.getProductName(), inventory.getCountryCode(), inventory.getColorCode(),null,null);
					return offId+","+log.getQuantity();
				}
			}
		}
		return rs;
	}
	
	@Transactional(readOnly = false)
	public  Integer  createNewToOffline(Integer warehouseId,String sku,Integer quantity,String remark,Integer productId,String productName,String country,String color,Integer unlineId,String unlineNo){
		PsiQualityChangeBill   changeBill = new PsiQualityChangeBill(warehouseId, "", "New_To_Offline", sku,quantity, remark, "0", new Date(),
				UserUtils.getUser(),unlineNo,unlineId,productId,productName,country,color);
		this.qualityChangeBillDao.save(changeBill);
		return changeBill.getId();
	}
	
	@Transactional(readOnly = false)
	public  Integer  createSureNewToOffline(Integer warehouseId,String sku,Integer quantity,String remark,Integer productId,String productName,String country,String color,Integer unlineId,String unlineNo){
		PsiQualityChangeBill   changeBill = new PsiQualityChangeBill(warehouseId, "", "New_To_Offline", sku,quantity, remark, "3", new Date(),
				UserUtils.getUser(),unlineNo,unlineId,productId,productName,country,color);
		this.qualityChangeBillDao.save(changeBill);
		return changeBill.getId();
	}

	public void savelog(String operationType,String dataType,Integer quantity,String remark,String colorCode,String countryCode,Integer productId,
			String productName,Integer warehouseId,String relativeNo,String sku,Integer relativeWarehouseId,String relativeWarehouseName,Integer timelyQuantity){
		PsiInventoryRevisionLog log = new PsiInventoryRevisionLog();
		log.setOperationType(operationType);
		log.setDataType(dataType);
		log.setQuantity(quantity);
		log.setColorCode(colorCode);
		log.setCountryCode(countryCode);
		log.setProductId(productId);
		log.setProductName(productName);
		log.setOperatinDate(new Date());
		log.setOperationUser(UserUtils.getUser());
		log.setRemark(remark);
		log.setWarehouseId(warehouseId);
		log.setRelativeNumber(relativeNo);
		log.setSku(sku);
		log.setTerminiWarehouseId(relativeWarehouseId);
		log.setTerminiWarehouseName(relativeWarehouseName);
		log.setTimelyQuantity(timelyQuantity);
		this.inventoryLogDao.save(log);
	}
	
	
	public void setTypeDataAndLog(PsiInventoryRevisionLog log,PsiInventory fromInventory,PsiInventory tempInventory,PsiInventory toInventory){
		String type= log.getOperationType();
		Integer quantity = log.getQuantity();
		String formS = fromInventory.getSku();
		String toS =log.getSku();
		String  oprationType = "From_"+formS+"_To_"+toS;
		Integer fromQ =null;
		Integer toQ=null;
		Integer newQ =0;
		Integer oldQ = 0;
		Integer brokenQ = 0;
		Integer renewQ = 0;
		if(toInventory!=null){
			newQ = toInventory.getNewQuantity();
			oldQ = toInventory.getOldQuantity();
			brokenQ = toInventory.getBrokenQuantity();
			renewQ = toInventory.getRenewQuantity();
		}
		if(type.equals("1")){
			tempInventory.setNewQuantity(quantity);
			//添加两条日志记录
			fromQ=fromInventory.getNewQuantity()-quantity;
			fromInventory.setNewQuantity(fromQ);
			toQ = newQ+quantity;
			this.savelog("New_"+oprationType, "new", -quantity, log.getRemark(), fromInventory.getColorCode(), fromInventory.getCountryCode(), fromInventory.getProductId(), fromInventory.getProductName(), fromInventory.getWarehouse().getId(),null,formS,null,null,fromQ);
			this.savelog("New_"+oprationType, "new", quantity, log.getRemark(), fromInventory.getColorCode(), log.getCountryCode(), fromInventory.getProductId(), fromInventory.getProductName(), fromInventory.getWarehouse().getId(),null,toS,null,null,toQ);
		}else if(type.equals("2")){
			tempInventory.setOldQuantity(quantity);
			fromQ=fromInventory.getOldQuantity()-quantity;
			fromInventory.setOldQuantity(fromQ);
			toQ = oldQ+quantity;
			this.savelog("Old_"+oprationType, "old", -quantity, log.getRemark(), fromInventory.getColorCode(), fromInventory.getCountryCode(), fromInventory.getProductId(), fromInventory.getProductName(), fromInventory.getWarehouse().getId(),null,formS,null,null,fromQ);
			this.savelog("Old_"+oprationType, "old", quantity, log.getRemark(), fromInventory.getColorCode(), log.getCountryCode(), fromInventory.getProductId(), fromInventory.getProductName(), fromInventory.getWarehouse().getId(),null,toS,null,null,toQ);
		}else if(type.equals("3")){
			tempInventory.setBrokenQuantity(quantity);
			fromQ=fromInventory.getBrokenQuantity()-quantity;
			fromInventory.setBrokenQuantity(fromQ);
			toQ = brokenQ+quantity;
			this.savelog("Broken"+oprationType, "broken", -quantity, log.getRemark(), fromInventory.getColorCode(), fromInventory.getCountryCode(), fromInventory.getProductId(), fromInventory.getProductName(), fromInventory.getWarehouse().getId(),null,formS,null,null,fromQ);
			this.savelog("Broken"+oprationType, "broken", quantity, log.getRemark(), fromInventory.getColorCode(), log.getCountryCode(), fromInventory.getProductId(), fromInventory.getProductName(), fromInventory.getWarehouse().getId(),null,toS,null,null,toQ);
		}else if(type.equals("4")){
			tempInventory.setRenewQuantity(quantity);
			fromQ=fromInventory.getRenewQuantity()-quantity;
			fromInventory.setRenewQuantity(fromQ);
			toQ = renewQ+quantity;
			this.savelog("Renew"+oprationType, "renew", -quantity, log.getRemark(), fromInventory.getColorCode(), fromInventory.getCountryCode(), fromInventory.getProductId(), fromInventory.getProductName(), fromInventory.getWarehouse().getId(),null,formS,null,null,fromQ);
			this.savelog("Renew"+oprationType, "renew", quantity, log.getRemark(), fromInventory.getColorCode(), log.getCountryCode(), fromInventory.getProductId(), fromInventory.getProductName(), fromInventory.getWarehouse().getId(),null,toS,null,null,toQ);
		}
	}
	
	/***
	 * 
	 *如果 
	 */
	public void newOldDataSet(PsiInventory inventory){
		Integer newTotal = inventory.getNewQuantity();
		Integer oldTotal = inventory.getOldQuantity();
		Integer brokenTotal  = inventory.getBrokenQuantity();
		Integer renewTotal = inventory.getRenewQuantity();
		Integer sparesTotal  = inventory.getSparesQuantity();
		Integer offlineTotal  = inventory.getOfflineQuantity();
		for(PsiInventoryRevisionLog log:inventory.getChangeItems()){
			Integer quantity = log.getQuantity();
			if(log.getOperationType().equals("1")){
				newTotal-=quantity;
				oldTotal+=quantity;
				this.savelog("New_To_Old", "new", -quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,newTotal);
				this.savelog("New_To_Old", "old", quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,oldTotal);
			}else if(log.getOperationType().equals("2")){
				newTotal-=quantity;
				brokenTotal+=quantity;
				this.savelog("New_To_Broken", "new", -quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,newTotal);
				this.savelog("New_To_Broken", "broken", quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,brokenTotal);
			}else if(log.getOperationType().equals("3")){
				newTotal-=quantity;
				renewTotal+=quantity;
				this.savelog("New_To_Renew", "new", -quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,newTotal);
				this.savelog("New_To_Renew", "renew", quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,renewTotal);
			}else if(log.getOperationType().equals("4")){
				oldTotal-=quantity;
				newTotal+=quantity;	
				this.savelog("Old_To_New", "old", -quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,oldTotal);
				this.savelog("Old_To_New", "new", quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,newTotal);
			}else if(log.getOperationType().equals("5")){
				oldTotal-=quantity;
				brokenTotal+=quantity;
				this.savelog("Old_To_Broken", "old", -quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,oldTotal);
				this.savelog("Old_To_Broken", "broken", quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,brokenTotal);
			}else if(log.getOperationType().equals("6")){
				oldTotal-=quantity;
				renewTotal+=quantity;
				this.savelog("Old_To_Renew", "old", -quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,oldTotal);
				this.savelog("Old_To_Renew", "renew", quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,renewTotal);
			}else if(log.getOperationType().equals("7")){
				brokenTotal-=quantity;
				newTotal+=quantity;
				this.savelog("Broken_To_New", "broken", -quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,brokenTotal);
				this.savelog("Broken_To_New", "new", quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,newTotal);
			}else if(log.getOperationType().equals("8")){
				brokenTotal-=quantity;
				oldTotal+=quantity;
				this.savelog("Broken_To_Old", "broken", -quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,brokenTotal);
				this.savelog("Broken_To_Old", "old", quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,oldTotal);
			}else if(log.getOperationType().equals("9")){
				brokenTotal-=quantity;
				renewTotal+=quantity;
				this.savelog("Broken_To_Renew", "broken", -quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,brokenTotal);
				this.savelog("Broken_To_Renew", "renew", quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,renewTotal);
			}else if(log.getOperationType().equals("10")){
				renewTotal-=quantity;
				newTotal+=quantity;
				this.savelog("Renew_To_New", "renew", -quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,renewTotal);
				this.savelog("Renew_To_New", "new", quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,newTotal);
			}else if(log.getOperationType().equals("11")){
				renewTotal-=quantity;
				oldTotal+=quantity;
				this.savelog("Renew_To_Old", "renew", -quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,renewTotal);
				this.savelog("Renew_To_Old", "old", quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,oldTotal);
			}else if(log.getOperationType().equals("12")){
				renewTotal-=quantity;
				brokenTotal+=quantity;
				this.savelog("Renew_To_Broken", "renew", -quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,renewTotal);
				this.savelog("Renew_To_Broken", "broken", quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,brokenTotal);
			}else if(log.getOperationType().equals("13")){
				sparesTotal-=quantity;
				newTotal+=quantity;
				this.savelog("Spares_To_New", "spares", -quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,sparesTotal);
				this.savelog("Spares_To_New", "new", quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,newTotal);
			}else if(log.getOperationType().equals("14")){
				newTotal-=quantity;
				offlineTotal+=quantity;
				this.savelog("New_To_Offline", "new", -quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,newTotal);
				this.savelog("New_To_Offline", "offline", quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,offlineTotal);
			}else if(log.getOperationType().equals("15")){
				offlineTotal-=quantity;
				newTotal+=quantity;
				this.savelog("Offline_To_New", "offline", -quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,offlineTotal);
				this.savelog("Offline_To_New", "new", quantity, log.getRemark(), inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), inventory.getWarehouse().getId(),null,inventory.getSku(),null,null,newTotal);
		
			}
		}
		
		if(newTotal<0||oldTotal<0||brokenTotal<0||renewTotal<0||sparesTotal<0||offlineTotal<0){
			throw new RuntimeException("数据类型调整后有new、old、broken、renew、spares、offline有负值，同时刻有其他人操作同一条数据，操作已取消");
		}
		
		inventory.setNewQuantity(newTotal);
		inventory.setOldQuantity(oldTotal);
		inventory.setBrokenQuantity(brokenTotal);
		inventory.setRenewQuantity(renewTotal);
		inventory.setSparesQuantity(sparesTotal);
		inventory.setOfflineQuantity(offlineTotal);
	}
	
	
	@Transactional(readOnly = false)
	public void save(PsiInventory psiInventory) {
		psiInventoryDao.save(psiInventory);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		psiInventoryDao.deleteById(id);
	}
	
	
	
	/**
	 * 查询所有产品(在产)信息
	 */
	
	public Map<String,PsiInventoryTotalDto> getProducingQuantity(){
		Map<String,String> skuMap=this.psiProductService.getAllBandingSku();
		
//		String sql ="SELECT b.`product_name`,b.`country_code`,b.`color_code`," +
//				" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum" +
//				" FROM psi_purchase_order AS a ,psi_purchase_order_item AS b " +
//				" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3')  and b.`del_flag`='0' " +
//				" GROUP BY b.product_name,b.country_code,b.color_code  ";
		
		String sql ="SELECT product_name,country_code,color_code,SUM(orderNum),SUM(offNum) FROM (" +
				"SELECT b.`product_name`,b.`country_code`,b.`color_code`," +
				" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum,SUM(b.`quantity_off_ordered`-b.`quantity_off_received`) AS offNum " +
				" FROM psi_purchase_order AS a ,psi_purchase_order_item AS b " +
				" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3')  and b.`del_flag`='0' " +
				" GROUP BY b.product_name,b.country_code,b.color_code  "+
				" UNION ALL SELECT b.`product_name`,b.`country_code`,b.`color_code`," +
				" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum,SUM(b.`quantity_off_ordered`-b.`quantity_off_received`) AS offNum " +
				" FROM lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b " +
				" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3')  and b.`del_flag`='0' " +
				" GROUP BY b.product_name,b.country_code,b.color_code ) AS t GROUP BY product_name,country_code,color_code ";
		List<Object[]> 	objects=this.psiInventoryDao.findBySql(sql); 
		
		Map<String,PsiInventoryTotalDto> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDto>> innerDtoMap = Maps.newHashMap();
	
		for(Object[] object:objects){
			List<PsiInventoryDto> psiInventoryDtos = Lists.newArrayList();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			Integer quantity   = Integer.parseInt(object[3].toString());
			Integer offlineQuanity = Integer.parseInt(object[4].toString());
			//如果线上在产为0，线下在产也为0    跳过
			if(quantity.intValue()==0&&offlineQuanity.intValue()==0){
				continue;
			}
			String productColorKey=productName+","+color;
			String key=productName+","+country+","+color;
			Map<String, Integer> inSkuMap = Maps.newHashMap();
			String sku =skuMap.get(key);
			if(!StringUtils.isEmpty(sku)){
				inSkuMap.put(sku, quantity);
			}else{
				inSkuMap.put("noSku", quantity);
			}
			
			if(innerDtoMap.get(productColorKey)!=null){
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			psiInventoryDtos.add(new PsiInventoryDto(productName, country, color,inSkuMap));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
			
		}
		
		for(Map.Entry<String, List<PsiInventoryDto>> entry:innerDtoMap.entrySet()){
			String productColorKey = entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			
			Map<String,PsiInventoryDto> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDto psiInventoryDto:entry.getValue()){
				countryDtoMap.put(psiInventoryDto.getCountry(), psiInventoryDto);
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDto(proArr[0], color, countryDtoMap));
			
		}
		
		return psiInventoryTotalDtos;
	}
	
	/**
	 * 查询（欧洲）所有产品(在产)信息
	 */
	public Map<String,PsiInventoryTotalDto> getProducingQuantityEu(){
		Map<String,String> skuMap=this.psiProductService.getAllBandingSku();
//		String	sql ="SELECT b.`product_name`,b.`country_code`,b.`color_code`," +
//				" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`))  AS orderNum " +
//				" FROM psi_purchase_order AS a ,psi_purchase_order_item AS b " +
//				" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3')  AND b.`country_code` in :p1 and b.`del_flag`='0' GROUP BY b.product_id,b.country_code,b.color_code";

		String sql ="SELECT product_name,country_code,color_code,SUM(orderNum) FROM (" +
				"SELECT b.`product_name`,b.`country_code`,b.`color_code`," +
				" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum" +
				" FROM psi_purchase_order AS a ,psi_purchase_order_item AS b " +
				" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3')  AND b.`country_code` in :p1 and b.`del_flag`='0' " +
				" GROUP BY b.product_name,b.country_code,b.color_code  "+
				" UNION ALL SELECT b.`product_name`,b.`country_code`,b.`color_code`," +
				" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum" +
				" FROM lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b " +
				" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3') AND b.`country_code` in :p1  and b.`del_flag`='0' " +
				" GROUP BY b.product_name,b.country_code,b.color_code ) AS t GROUP BY product_name,country_code,color_code ";
		List<Object[]> objects=this.psiInventoryDao.findBySql(sql,new Parameter(Lists.newArrayList("de","fr","it","es","uk"))); 
		
		Map<String,PsiInventoryTotalDto> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDto>> innerDtoMap = Maps.newHashMap();
	
		for(Object[] object:objects){
			List<PsiInventoryDto> psiInventoryDtos = Lists.newArrayList();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			Integer quantity   = Integer.parseInt(object[3].toString());
			String productColorKey=productName+","+color;
			String key=productName+","+country+","+color;
			Map<String, Integer> inSkuMap = Maps.newHashMap();
			String sku =skuMap.get(key);
			if(!StringUtils.isEmpty(sku)){
				inSkuMap.put(sku, quantity);
			}else{
				inSkuMap.put("noSku", quantity);
			}
			
			if(innerDtoMap.get(productColorKey)!=null){
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			psiInventoryDtos.add(new PsiInventoryDto(productName, country, color,inSkuMap));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
			
		}
		
		for(Map.Entry<String, List<PsiInventoryDto>> entry:innerDtoMap.entrySet()){
			String productColorKey = entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			Map<String,PsiInventoryDto> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDto psiInventoryDto:entry.getValue()){
				countryDtoMap.put(psiInventoryDto.getCountry(), psiInventoryDto);
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDto(proArr[0], color, countryDtoMap));
			
		}
		return psiInventoryTotalDtos;
	}

	/**
	 * 查询所有产品(在产)信息
	 */
	
	public Map<String,PsiInventoryTotalDto> getProducingQuantity(String countryFlag){
		Map<String,String> skuMap=this.psiProductService.getAllBandingSku();
//		String	sql ="SELECT b.`product_name`,b.`country_code`,b.`color_code`, " +
//				" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`))  AS orderNum " +
//				" FROM psi_purchase_order AS a ,psi_purchase_order_item AS b " +
//				" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3')  AND b.`country_code` = :p1 and b.`del_flag`='0' GROUP BY b.product_id,b.country_code,b.color_code";

		String sql ="SELECT product_name,country_code,color_code,SUM(orderNum) FROM (" +
				"SELECT b.`product_name`,b.`country_code`,b.`color_code`," +
				" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum" +
				" FROM psi_purchase_order AS a ,psi_purchase_order_item AS b " +
				" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3')  AND b.`country_code` = :p1 and b.`del_flag`='0' " +
				" GROUP BY b.product_name,b.country_code,b.color_code  "+
				" UNION ALL SELECT b.`product_name`,b.`country_code`,b.`color_code`," +
				" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum" +
				" FROM lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b " +
				" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3') AND b.`country_code` = :p1  and b.`del_flag`='0' " +
				" GROUP BY b.product_name,b.country_code,b.color_code ) AS t GROUP BY product_name,country_code,color_code ";
		List<Object[]> objects=this.psiInventoryDao.findBySql(sql,new Parameter(countryFlag)); 
		
		Map<String,PsiInventoryTotalDto> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDto>> innerDtoMap = Maps.newHashMap();
	
		for(Object[] object:objects){
			List<PsiInventoryDto> psiInventoryDtos = Lists.newArrayList();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			Integer quantity   = Integer.parseInt(object[3].toString());
			String productColorKey=productName+","+color;
			String key=productName+","+country+","+color;
			Map<String, Integer> inSkuMap = Maps.newHashMap();
			String sku =skuMap.get(key);
			if(!StringUtils.isEmpty(sku)){
				inSkuMap.put(sku, quantity);
			}else{
				inSkuMap.put("noSku", quantity);
			}
			
			if(innerDtoMap.get(productColorKey)!=null){
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			psiInventoryDtos.add(new PsiInventoryDto(productName, country, color,inSkuMap));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
			
		}
		
		for(Map.Entry<String, List<PsiInventoryDto>> entry:innerDtoMap.entrySet()){
			String productColorKey = entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			Map<String,PsiInventoryDto> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDto psiInventoryDto:entry.getValue()){
				countryDtoMap.put(psiInventoryDto.getCountry(), psiInventoryDto);
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDto(proArr[0], color, countryDtoMap));
			
		}
		return psiInventoryTotalDtos;
	}
	/***
	 * 单个产品
	 * 
	 */
	public PsiInventoryTotalDto getProducingQuantity(String productName_color,String countryCode){
		Parameter parameter  = null;
		String sql ="";
		if(StringUtils.isNotEmpty(countryCode)){
//			sql ="SELECT b.`product_name`, b.color_code,b.`country_code`," +
//					" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`))  AS orderNum " +
//					" FROM psi_purchase_order AS a ,psi_purchase_order_item AS b " +
//					" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3') and b.`del_flag`='0' " +
//					"  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END )=:p1 AND b.`country_code` =:p2 " +
//					" GROUP BY b.product_name,b.country_code,b.color_code ";

			sql ="SELECT product_name,color_code,country_code,SUM(orderNum) FROM (" +
					"SELECT b.`product_name`,b.`color_code`,b.`country_code`," +
					" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum" +
					" FROM psi_purchase_order AS a ,psi_purchase_order_item AS b " +
					" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3') and b.`del_flag`='0' " +
					" AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END )=:p1 AND b.`country_code` = :p2 " +
					" GROUP BY b.product_name,b.country_code,b.color_code  "+
					" UNION ALL SELECT b.`product_name`,b.`color_code`,b.`country_code`," +
					" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum" +
					" FROM lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b " +
					" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3') and b.`del_flag`='0'" +
					" AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END )=:p1 AND b.`country_code` = :p2 " +
					" GROUP BY b.product_name,b.country_code,b.color_code ) AS t GROUP BY product_name,country_code,color_code ";
			parameter=new Parameter(productName_color,countryCode);
		}else{
//			sql ="SELECT b.`product_name`, b.color_code,b.`country_code`," +
//					" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`))  AS orderNum " +
//					" FROM psi_purchase_order AS a ,psi_purchase_order_item AS b " +
//					" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3') and b.`del_flag`='0' " +
//					" AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END )=:p1 " +
//					" GROUP BY b.product_name,b.country_code,b.color_code ";

			sql ="SELECT product_name,color_code,country_code,SUM(orderNum) FROM (" +
					"SELECT b.`product_name`,b.`color_code`,b.`country_code`," +
					" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum" +
					" FROM psi_purchase_order AS a ,psi_purchase_order_item AS b " +
					" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3') and b.`del_flag`='0' " +
					" AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END )=:p1 " +
					" GROUP BY b.product_name,b.country_code,b.color_code  "+
					" UNION ALL SELECT b.`product_name`,b.`color_code`,b.`country_code`," +
					" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum" +
					" FROM lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b " +
					" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3') and b.`del_flag`='0'" +
					" AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END )=:p1 " +
					" GROUP BY b.product_name,b.country_code,b.color_code ) AS t GROUP BY product_name,country_code,color_code ";
			parameter=new Parameter(productName_color);
		}
		
		List<Object[]> objects=this.psiInventoryDao.findBySql(sql,parameter); 
		List<PsiInventoryDto> psiInventoryDtos = Lists.newArrayList();   
		String productName="";
		String color="";	
		for(Object[] object:objects){
			Map<String, Integer> inSkuMap = Maps.newHashMap();
			if("".equals(productName)){
				productName = object[0].toString();
				color = object[1].toString();
			}
			String country = object[2].toString();
			Integer quantity   =  ((BigDecimal)object[3]).intValue();;
			inSkuMap.put("noSku", quantity);
			psiInventoryDtos.add(new PsiInventoryDto(productName, country, color, inSkuMap));
		}
		
		Map<String,PsiInventoryDto> countryMap =Maps.newHashMap();
		for(PsiInventoryDto inventoryDto:psiInventoryDtos){
			String country=inventoryDto.getCountry();
			countryMap.put(country, inventoryDto);
		}
		
		return new PsiInventoryTotalDto(productName, color, countryMap);
	}
	
	/**
	 * 查询所有产品(在途)信息
	 */
	public Map<String,Map<String,PsiInventoryTotalDto>> getTransporttingQuantity(){
		 
		//String sql ="SELECT b.`product_name`,b.`country_code`,b.`color_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END),(CASE WHEN a.`transport_sta` IN('1','2','3','4') THEN 1 ELSE 0 END) as tType ,sum(b.quantity) FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' and b.offline_sta='0' AND a.`transport_sta` IN ('0','1','2','3','4')   GROUP BY b.`product_name`,b.`country_code`,b.`color_code`,tType";
		
		String sql=" SELECT b.`product_name`,b.country_code,b.`color_code`,b.sku,SUM(tempQuantity),tType,SUM(quantity) FROM ( "+
				"	SELECT b.`product_name`, b.color_code,(case when (a.to_store='19'&&b.country_code not like 'de%'&&b.country_code not like 'uk%'&&b.country_code not like 'fr%'&&b.country_code not like 'it%'&&b.country_code not like 'es%'&&a.`transport_sta` IN('1','2','3','4')) then 'de' when (a.to_store='120'&&b.country_code not like 'com%'&&a.`transport_sta` IN('1','2','3','4')) then 'com' when (a.to_store='147'&&b.country_code not like 'jp%'&&a.`transport_sta` IN('1','2','3','4')) then 'jp' else  b.country_code end) country_code,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END) tempQuantity,(CASE WHEN a.`transport_sta` IN('1','2','3','4') THEN 1 ELSE 0 END) AS tType ,SUM(b.quantity) quantity "+
				"	FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('0','1','2','3','4')  "+
				"	GROUP BY b.`product_name`,b.`country_code`,b.`color_code`,tType "+
				"	UNION ALL "+
				"	SELECT b.`product_name`, b.color_code,(case when (a.to_store='19'&&b.country_code not like 'de%'&&b.country_code not like 'uk%'&&b.country_code not like 'fr%'&&b.country_code not like 'it%'&&b.country_code not like 'es%'&&a.`transport_sta` IN('1','2','3','4')) then 'de' when (a.to_store='120'&&b.country_code not like 'com%'&&a.`transport_sta` IN('1','2','3','4')) then 'com' when (a.to_store='147'&&b.country_code not like 'jp%'&&a.`transport_sta` IN('1','2','3','4')) then 'jp' else  b.country_code end) country_code,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END),(CASE WHEN a.`transport_sta` IN('1','2','3','4') THEN 1 ELSE 0 END) AS tType ,SUM(b.quantity) "+
				"	FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('0','1','2','3','4')  "+
				"	GROUP BY b.`product_name`,country_code,b.`color_code`,tType) b GROUP BY b.`product_name`,b.`country_code`,b.`color_code`,tType ";
			
		List<Object[]>	objects=this.psiInventoryDao.findBySql(sql); 
		Map<String,PsiInventoryTotalDto> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDto>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, Integer>> producSkuMap =Maps.newHashMap();
		
		Map<String,PsiInventoryTotalDto> psiInventoryTotalDtos1 = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDto>> innerDtoMap1 = Maps.newHashMap();
		Map<String,Map<String, Integer>> producSkuMap1 =Maps.newHashMap();
		
		for(Object[] object:objects){
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			String type = object[5].toString();
			String sku = "noSku";
			Integer quantity = 0;
			if("1".equals(type)){
				if(object[3]!=null){
					sku  = object[3].toString();
				}
				 if(object[4]==null){  
					 continue;
				 }
				 quantity   = ((BigDecimal)object[4]).intValue();
			}else{
				 if(object[6]==null){
					 continue;
				 }
				 quantity   = ((BigDecimal)object[6]).intValue();
			}
			
			String key=productName+","+country+","+color;
			
			if("1".equals(type)){
				Map<String,Integer>  inMap = producSkuMap.get(key);
				if(inMap==null){
					inMap = Maps.newHashMap();
					producSkuMap.put(key, inMap);
				}
				inMap.put(sku, quantity);
			}else{
				Map<String,Integer>  inMap = producSkuMap1.get(key);
				if(inMap==null){
					inMap = Maps.newHashMap();
					producSkuMap1.put(key, inMap);
				}
				inMap.put(sku, quantity);
			}
		}
		
		//在途的
		for(Map.Entry<String,Map<String,Integer>> entry1:producSkuMap.entrySet()){
			String key=entry1.getKey();
			Map<String,Integer> skuMap = entry1.getValue();
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			String productColorKey=productName+","+color;
			List<PsiInventoryDto> psiInventoryDtos = innerDtoMap.get(productColorKey);
			if(psiInventoryDtos==null){
				psiInventoryDtos = Lists.newArrayList();
				innerDtoMap.put(productColorKey, psiInventoryDtos);
			}
			psiInventoryDtos.add(new PsiInventoryDto(productName, country, color,skuMap));
		 }
		
		for(Map.Entry<String, List<PsiInventoryDto>> entry:innerDtoMap.entrySet()){
			String productColorKey = entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			Map<String,PsiInventoryDto> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDto psiInventoryDto:entry.getValue()){
				countryDtoMap.put(psiInventoryDto.getCountry(), psiInventoryDto);
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDto(proArr[0], color, countryDtoMap));
		}
		
		//待发货的
		for(Map.Entry<String, Map<String, Integer>> entry:producSkuMap1.entrySet()){
			String key = entry.getKey();
			Map<String,Integer> skuMap = entry.getValue();
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			String productColorKey=productName+","+color;
			List<PsiInventoryDto> psiInventoryDtos = innerDtoMap1.get(productColorKey);
			if(psiInventoryDtos==null){
				psiInventoryDtos = Lists.newArrayList();
				innerDtoMap1.put(productColorKey, psiInventoryDtos);
			}
			psiInventoryDtos.add(new PsiInventoryDto(productName, country, color,skuMap));
		 }
		
		for(Map.Entry<String, List<PsiInventoryDto>> entry:innerDtoMap1.entrySet()){
			String productColorKey = entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			Map<String,PsiInventoryDto> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDto psiInventoryDto:entry.getValue()){
				countryDtoMap.put(psiInventoryDto.getCountry(), psiInventoryDto);
			}
			psiInventoryTotalDtos1.put(name,new PsiInventoryTotalDto(proArr[0], color, countryDtoMap));
		}
		Map<String,Map<String,PsiInventoryTotalDto>> rs = Maps.newHashMap();
		rs.put("1", psiInventoryTotalDtos);
		rs.put("0", psiInventoryTotalDtos1);
		return rs;
	}
	
	public Map<String,Map<String,PsiInventoryTotalDto>> getTransporttingQuantity(Set<String> nameSet){
		 
		//String sql ="SELECT b.`product_name`,b.`country_code`,b.`color_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END),(CASE WHEN a.`transport_sta` IN('1','2','3','4') THEN 1 ELSE 0 END) as tType ,sum(b.quantity) FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' and b.offline_sta='0' AND a.`transport_sta` IN ('0','1','2','3','4')   GROUP BY b.`product_name`,b.`country_code`,b.`color_code`,tType";
		
		String sql=" SELECT b.`product_name`,b.`country_code`,b.`color_code`,b.sku,SUM(tempQuantity),tType,SUM(quantity) FROM ( "+
				"	SELECT b.`product_name`, b.color_code,b.`country_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END) tempQuantity,(CASE WHEN a.`transport_sta` IN('1','2','3','4') THEN 1 ELSE 0 END) AS tType ,SUM(b.quantity) quantity "+
				"	FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('0','1','2','3','4')  "+
				"  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) in :p1 "+
				"	GROUP BY b.`product_name`,b.`country_code`,b.`color_code`,tType "+
				"	UNION ALL "+
				"	SELECT b.`product_name`, b.color_code,b.`country_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END),(CASE WHEN a.`transport_sta` IN('1','2','3','4') THEN 1 ELSE 0 END) AS tType ,SUM(b.quantity) "+
				"	FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('0','1','2','3','4')  "+
				"  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) in :p1 "+
				"	GROUP BY b.`product_name`,b.`country_code`,b.`color_code`,tType) b GROUP BY b.`product_name`,b.`country_code`,b.`color_code`,tType ";
			
		List<Object[]>	objects=this.psiInventoryDao.findBySql(sql,new Parameter(nameSet)); 
		Map<String,PsiInventoryTotalDto> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDto>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, Integer>> producSkuMap =Maps.newHashMap();
		
		Map<String,PsiInventoryTotalDto> psiInventoryTotalDtos1 = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDto>> innerDtoMap1 = Maps.newHashMap();
		Map<String,Map<String, Integer>> producSkuMap1 =Maps.newHashMap();
		
		for(Object[] object:objects){
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			String type = object[5].toString();
			String sku = "noSku";
			Integer quantity = 0;
			if("1".equals(type)){
				if(object[3]!=null){
					sku  = object[3].toString();
				}
				 if(object[4]==null){  
					 continue;
				 }
				 quantity   = ((BigDecimal)object[4]).intValue();
			}else{
				 if(object[6]==null){
					 continue;
				 }
				 quantity   = ((BigDecimal)object[6]).intValue();
			}
			
			String key=productName+","+country+","+color;
			
			if("1".equals(type)){
				Map<String,Integer>  inMap = producSkuMap.get(key);
				if(inMap==null){
					inMap = Maps.newHashMap();
					producSkuMap.put(key, inMap);
				}
				inMap.put(sku, quantity);
			}else{
				Map<String,Integer>  inMap = producSkuMap1.get(key);
				if(inMap==null){
					inMap = Maps.newHashMap();
					producSkuMap1.put(key, inMap);
				}
				inMap.put(sku, quantity);
			}
		}
		
		//在途的
		for(Map.Entry<String,Map<String,Integer>> entry1:producSkuMap.entrySet()){
			String key=entry1.getKey();
			Map<String,Integer> skuMap = entry1.getValue();
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			String productColorKey=productName+","+color;
			List<PsiInventoryDto> psiInventoryDtos = innerDtoMap.get(productColorKey);
			if(psiInventoryDtos==null){
				psiInventoryDtos = Lists.newArrayList();
				innerDtoMap.put(productColorKey, psiInventoryDtos);
			}
			psiInventoryDtos.add(new PsiInventoryDto(productName, country, color,skuMap));
		 }
		
		for(Map.Entry<String, List<PsiInventoryDto>> entry:innerDtoMap.entrySet()){
			String productColorKey = entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			Map<String,PsiInventoryDto> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDto psiInventoryDto:entry.getValue()){
				countryDtoMap.put(psiInventoryDto.getCountry(), psiInventoryDto);
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDto(proArr[0], color, countryDtoMap));
		}
		
		//待发货的
		for(Map.Entry<String, Map<String, Integer>> entry:producSkuMap1.entrySet()){
			String key = entry.getKey();
			Map<String,Integer> skuMap = entry.getValue();
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			String productColorKey=productName+","+color;
			List<PsiInventoryDto> psiInventoryDtos = innerDtoMap1.get(productColorKey);
			if(psiInventoryDtos==null){
				psiInventoryDtos = Lists.newArrayList();
				innerDtoMap1.put(productColorKey, psiInventoryDtos);
			}
			psiInventoryDtos.add(new PsiInventoryDto(productName, country, color,skuMap));
		 }
		
		for(Map.Entry<String, List<PsiInventoryDto>> entry:innerDtoMap1.entrySet()){
			String productColorKey = entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			Map<String,PsiInventoryDto> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDto psiInventoryDto:entry.getValue()){
				countryDtoMap.put(psiInventoryDto.getCountry(), psiInventoryDto);
			}
			psiInventoryTotalDtos1.put(name,new PsiInventoryTotalDto(proArr[0], color, countryDtoMap));
		}
		Map<String,Map<String,PsiInventoryTotalDto>> rs = Maps.newHashMap();
		rs.put("1", psiInventoryTotalDtos);
		rs.put("0", psiInventoryTotalDtos1);
		return rs;
	}
	
	/**
	 * 查询所有产品(在途)信息(排除中国仓发货数据)
	 */
	public Map<String,Map<String,PsiInventoryTotalDto>> getTransporttingWithoutCn(){
		String sql=" SELECT b.`product_name`,b.`country_code`,b.`color_code`,b.sku,SUM(tempQuantity),tType,SUM(quantity) FROM ( "+
				"	SELECT b.`product_name`, b.color_code,b.`country_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END) tempQuantity,(CASE WHEN a.`transport_sta` IN('1','2','3','4') THEN 1 ELSE 0 END) AS tType ,SUM(b.quantity) quantity "+
				"	FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('0','1','2','3','4') AND a.from_store NOT IN(SELECT id FROM `psi_stock` t WHERE t.`stock_name` LIKE '中国%') "+
				"	GROUP BY b.`product_name`,b.`country_code`,b.`color_code`,tType "+
				"	UNION ALL "+
				"	SELECT b.`product_name`, b.color_code,b.`country_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END),(CASE WHEN a.`transport_sta` IN('1','2','3','4') THEN 1 ELSE 0 END) AS tType ,SUM(b.quantity) "+
				"	FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('0','1','2','3','4')  AND a.from_store NOT IN(SELECT id FROM `psi_stock` t WHERE t.`stock_name` LIKE '中国%') "+
				"	GROUP BY b.`product_name`,b.`country_code`,b.`color_code`,tType) b GROUP BY b.`product_name`,b.`country_code`,b.`color_code`,tType ";
			
		List<Object[]>	objects=this.psiInventoryDao.findBySql(sql); 
		Map<String,PsiInventoryTotalDto> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDto>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, Integer>> producSkuMap =Maps.newHashMap();
		
		Map<String,PsiInventoryTotalDto> psiInventoryTotalDtos1 = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDto>> innerDtoMap1 = Maps.newHashMap();
		Map<String,Map<String, Integer>> producSkuMap1 =Maps.newHashMap();
		
		for(Object[] object:objects){
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			String type = object[5].toString();
			String sku = "noSku";
			Integer quantity = 0;
			if("1".equals(type)){
				if(object[3]!=null){
					sku  = object[3].toString();
				}
				 if(object[4]==null){  
					 continue;
				 }
				 quantity   = ((BigDecimal)object[4]).intValue();
			}else{
				 if(object[6]==null){
					 continue;
				 }
				 quantity   = ((BigDecimal)object[6]).intValue();
			}
			
			String key=productName+","+country+","+color;
			
			if("1".equals(type)){
				Map<String,Integer>  inMap = producSkuMap.get(key);
				if(inMap==null){
					inMap = Maps.newHashMap();
					producSkuMap.put(key, inMap);
				}
				inMap.put(sku, quantity);
			}else{
				Map<String,Integer>  inMap = producSkuMap1.get(key);
				if(inMap==null){
					inMap = Maps.newHashMap();
					producSkuMap1.put(key, inMap);
				}
				inMap.put(sku, quantity);
			}
		}
		
		//在途的
		for(Map.Entry<String, Map<String, Integer>> entry1:producSkuMap.entrySet()){
			String key=entry1.getKey();
			Map<String,Integer> skuMap = entry1.getValue();
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			String productColorKey=productName+","+color;
			List<PsiInventoryDto> psiInventoryDtos = innerDtoMap.get(productColorKey);
			if(psiInventoryDtos==null){
				psiInventoryDtos = Lists.newArrayList();
				innerDtoMap.put(productColorKey, psiInventoryDtos);
			}
			psiInventoryDtos.add(new PsiInventoryDto(productName, country, color,skuMap));
		 }
		
		for(Map.Entry<String, List<PsiInventoryDto>> entry:innerDtoMap.entrySet()){
			String productColorKey = entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			Map<String,PsiInventoryDto> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDto psiInventoryDto:entry.getValue()){
				countryDtoMap.put(psiInventoryDto.getCountry(), psiInventoryDto);
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDto(proArr[0], color, countryDtoMap));
		}
		
		//待发货的
		for(Map.Entry<String, Map<String, Integer>> entry:producSkuMap1.entrySet()){
			String key = entry.getKey();
			Map<String,Integer> skuMap = entry.getValue();
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			String productColorKey=productName+","+color;
			List<PsiInventoryDto> psiInventoryDtos = innerDtoMap1.get(productColorKey);
			if(psiInventoryDtos==null){
				psiInventoryDtos = Lists.newArrayList();
				innerDtoMap1.put(productColorKey, psiInventoryDtos);
			}
			psiInventoryDtos.add(new PsiInventoryDto(productName, country, color,skuMap));
		 }
		
		for(Map.Entry<String, List<PsiInventoryDto>> entry:innerDtoMap1.entrySet()){
			String productColorKey = entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			Map<String,PsiInventoryDto> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDto psiInventoryDto:entry.getValue()){
				countryDtoMap.put(psiInventoryDto.getCountry(), psiInventoryDto);
			}
			psiInventoryTotalDtos1.put(name,new PsiInventoryTotalDto(proArr[0], color, countryDtoMap));
		}
		Map<String,Map<String,PsiInventoryTotalDto>> rs = Maps.newHashMap();
		rs.put("1", psiInventoryTotalDtos);
		rs.put("0", psiInventoryTotalDtos1);
		return rs;
	}
	
	
	/**
	 * 查询单产品(在途)信息
	 */
	public Map<String,PsiInventoryTotalDto> getTransporttingQuantity(String productName_color,String countryCode){
		
		String	sql ="";
		Parameter parameter=null;
		if(StringUtils.isNotEmpty(countryCode)){
			//sql ="SELECT b.`product_name`, b.color_code,b.`country_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END),(CASE WHEN a.`transport_sta` IN('1','2','3','4') THEN 1 ELSE 0 END) as tType ,sum(b.quantity) FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('0','1','2','3','4')  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END )=:p1 AND b.`country_code` =:p2 GROUP BY b.`product_name`,b.`country_code`,b.`color_code`,tType";
			sql=" SELECT b.`product_name`,b.`color_code`,b.country_code,b.sku,SUM(tempQuantity),tType,SUM(quantity) FROM ( "+
				"	SELECT b.`product_name`, b.color_code,(case when (a.to_store='19'&&b.country_code not like 'de%'&&b.country_code not like 'uk%'&&b.country_code not like 'fr%'&&b.country_code not like 'it%'&&b.country_code not like 'es%'&&a.`transport_sta` IN('1','2','3','4')) then 'de' when (a.to_store='120'&&b.country_code not like 'com%'&&a.`transport_sta` IN('1','2','3','4')) then 'com' when (a.to_store='147'&&b.country_code not like 'jp%'&&a.`transport_sta` IN('1','2','3','4')) then 'jp' else  b.country_code end) country_code,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END) tempQuantity,(CASE WHEN a.`transport_sta` IN('1','2','3','4') THEN 1 ELSE 0 END) AS tType ,SUM(b.quantity) quantity "+
				"	FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('0','1','2','3','4')  "+
				"    AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END )=:p1 AND b.`country_code` =:p2  "+
				"	GROUP BY b.`product_name`,b.`country_code`,b.`color_code`,tType "+
				"	UNION ALL "+
				"	SELECT b.`product_name`, b.color_code,(case when (a.to_store='19'&&b.country_code not like 'de%'&&b.country_code not like 'uk%'&&b.country_code not like 'fr%'&&b.country_code not like 'it%'&&b.country_code not like 'es%'&&a.`transport_sta` IN('1','2','3','4')) then 'de' when (a.to_store='120'&&b.country_code not like 'com%'&&a.`transport_sta` IN('1','2','3','4')) then 'com' when (a.to_store='147'&&b.country_code not like 'jp%'&&a.`transport_sta` IN('1','2','3','4')) then 'jp' else  b.country_code end) country_code,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END),(CASE WHEN a.`transport_sta` IN('1','2','3','4') THEN 1 ELSE 0 END) AS tType ,SUM(b.quantity) "+
				"	FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('0','1','2','3','4')  "+
				"	AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END )=:p1 AND b.`country_code` =:p2 "+
				"	GROUP BY b.`product_name`,country_code,b.`color_code`,tType) b GROUP BY b.`product_name`,b.`country_code`,b.`color_code`,tType ";
			
			parameter =new Parameter(productName_color,countryCode);
		}else{
			//sql ="SELECT b.`product_name`, b.color_code,b.`country_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END),(CASE WHEN a.`transport_sta` IN('1','2','3','4') THEN 1 ELSE 0 END) as tType ,sum(b.quantity) FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('0','1','2','3','4') AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END )=:p1 GROUP BY b.`product_name`,b.`country_code`,b.`color_code`,tType";
			sql=" SELECT b.`product_name`,b.`color_code`,b.country_code,b.sku,SUM(tempQuantity),tType,SUM(quantity) FROM ( "+
					"	SELECT b.`product_name`, b.color_code,(case when (a.to_store='19'&&b.country_code not like 'de%'&&b.country_code not like 'uk%'&&b.country_code not like 'fr%'&&b.country_code not like 'it%'&&b.country_code not like 'es%'&&a.`transport_sta` IN('1','2','3','4')) then 'de' when (a.to_store='120'&&b.country_code not like 'com%'&&a.`transport_sta` IN('1','2','3','4')) then 'com' when (a.to_store='147'&&b.country_code not like 'jp%'&&a.`transport_sta` IN('1','2','3','4')) then 'jp' else  b.country_code end) country_code,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END) tempQuantity,(CASE WHEN a.`transport_sta` IN('1','2','3','4') THEN 1 ELSE 0 END) AS tType ,SUM(b.quantity) quantity "+
					"	FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('0','1','2','3','4')  "+
					"    AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END )=:p1   "+
					"	GROUP BY b.`product_name`,country_code,b.`color_code`,tType "+
					"	UNION ALL "+
					"	SELECT b.`product_name`, b.color_code,(case when (a.to_store='19'&&b.country_code not like 'de%'&&b.country_code not like 'uk%'&&b.country_code not like 'fr%'&&b.country_code not like 'it%'&&b.country_code not like 'es%'&&a.`transport_sta` IN('1','2','3','4')) then 'de' when (a.to_store='120'&&b.country_code not like 'com%'&&a.`transport_sta` IN('1','2','3','4')) then 'com' when (a.to_store='147'&&b.country_code not like 'jp%'&&a.`transport_sta` IN('1','2','3','4')) then 'jp' else  b.country_code end) country_code,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END),(CASE WHEN a.`transport_sta` IN('1','2','3','4') THEN 1 ELSE 0 END) AS tType ,SUM(b.quantity) "+
					"	FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('0','1','2','3','4')  "+
					"	AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END )=:p1  "+
					"	GROUP BY b.`product_name`,country_code,b.`color_code`,tType) b GROUP BY b.`product_name`,b.`country_code`,b.`color_code`,tType ";
				
			parameter =new Parameter(productName_color);
		}
		List<Object[]> objects=this.psiInventoryDao.findBySql(sql,parameter); 
		Map<String,Map<String, Integer>> producSkuMap =Maps.newHashMap();  
		Map<String,Map<String, Integer>> producSkuMap1 =Maps.newHashMap();  
		String productName="";
		String color="";	
		for(Object[] object:objects){
			if(!"".equals(productName)){
				productName = object[0].toString();
				color = object[1].toString();
			}
			String country = object[2].toString();
			
			String type = object[5].toString();
			String sku = "noSku";
			Integer quantity = 0;
			
			if("1".equals(type)){
				 sku  = object[3].toString();
				 if(object[4]==null){
					 continue;
				 }
				 quantity   = ((BigDecimal)object[4]).intValue();
			}else{
				if(object[6]==null){
					 continue;
				 }
				 quantity   = ((BigDecimal)object[6]).intValue();
			}
			
			String key=productName+","+country+","+color;
			if("1".equals(type)){
				Map<String,Integer>  inMap = producSkuMap.get(key);
				if(inMap==null){
					inMap = Maps.newHashMap();
					producSkuMap.put(key, inMap);
				}
				inMap.put(sku, quantity);
			}else{
				Map<String,Integer>  inMap = producSkuMap1.get(key);
				if(inMap==null){
					inMap = Maps.newHashMap();
					producSkuMap1.put(key, inMap);
				}
				inMap.put(sku, quantity);
			}
		}
		
		//在途的
		List<PsiInventoryDto> psiInventoryDtos = Lists.newArrayList();
		for(Map.Entry<String, Map<String, Integer>> entry1:producSkuMap.entrySet()){
			String key=entry1.getKey();
			Map<String,Integer> skuMap = entry1.getValue();
			String[] arr=key.split(",");
			productName = arr[0].toString();
			String country = arr[1].toString();
			color = "";
			if(arr.length>2){
				color=arr[2];
			}
			psiInventoryDtos.add(new PsiInventoryDto(productName, country, color,skuMap));
		 }
		
		Map<String,PsiInventoryDto> countryMap =Maps.newHashMap();
		for(PsiInventoryDto inventoryDto:psiInventoryDtos){
			String country=inventoryDto.getCountry();
			countryMap.put(country, inventoryDto);
		}
		PsiInventoryTotalDto psiInventoryTotalDto = new PsiInventoryTotalDto(productName, color, countryMap);
		//待发货的
		List<PsiInventoryDto> psiInventoryDtos1 = Lists.newArrayList();
		for(Map.Entry<String, Map<String, Integer>> entry:producSkuMap1.entrySet()){
			String key = entry.getKey();
			Map<String,Integer> skuMap = entry.getValue();
			String[] arr=key.split(",");
			productName = arr[0].toString();
			String country = arr[1].toString();
			color = "";
			if(arr.length>2){
				color=arr[2];
			}
			psiInventoryDtos1.add(new PsiInventoryDto(productName, country, color,skuMap));
		 }
		
		Map<String,PsiInventoryDto> countryMap1 =Maps.newHashMap();
		for(PsiInventoryDto inventoryDto:psiInventoryDtos1){
			String country=inventoryDto.getCountry();
			countryMap1.put(country, inventoryDto);
		}
		PsiInventoryTotalDto psiInventoryTotalDto1 = new PsiInventoryTotalDto(productName, color, countryMap1);
		Map<String,PsiInventoryTotalDto> rs = Maps.newHashMap();
		rs.put("1", psiInventoryTotalDto);
		rs.put("0", psiInventoryTotalDto1);
		return rs;
	}
	
	
	
	
	/**
	 * 查询(欧洲)所有产品(在途)信息
	 */
	
	public Map<String,PsiInventoryTotalDto> getTransporttingQuantityEu(){
		//String sql ="SELECT b.`product_name`,b.`country_code`,b.`color_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END) FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('1','2','3','4')  AND b.`country_code` in :p1 GROUP BY b.`product_name`,b.`country_code`,b.`color_code`";
		
		String sql ="SELECT b.`product_name`,b.`country_code`,b.`color_code`,b.sku,sum(quantity) from (SELECT b.`product_name`,b.`country_code`,b.`color_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END) quantity FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('1','2','3','4')  AND b.`country_code` in :p1 GROUP BY b.`product_name`,b.`country_code`,b.`color_code` "+
				" union all SELECT b.`product_name`,b.`country_code`,b.`color_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END) FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('1','2','3','4')  AND b.`country_code` in :p1 GROUP BY b.`product_name`,b.`country_code`,b.`color_code` ) b GROUP BY b.`product_name`,b.`country_code`,b.`color_code` ";
		
		List<Object[]> objects=this.psiInventoryDao.findBySql(sql,new Parameter(Lists.newArrayList("de","fr","it","es","uk"))); 
		Map<String,PsiInventoryTotalDto> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDto>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, Integer>> producSkuMap =Maps.newHashMap();
		
		for(Object[] object:objects){
			Map<String,Integer>  inMap = Maps.newHashMap();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			String sku     = object[3].toString();
			Integer quantity   = Integer.parseInt(object[4].toString());
			String key=productName+","+country+","+color;
			if(producSkuMap.get(key)!=null){
				inMap = producSkuMap.get(key);
			}
			inMap.put(sku, quantity);
			producSkuMap.put(key, inMap);
		}
		
		for(Map.Entry<String, Map<String, Integer>> entry1:producSkuMap.entrySet()){
			String key=entry1.getKey();
			Map<String,Integer> skuMap = entry1.getValue();
			List<PsiInventoryDto> psiInventoryDtos = Lists.newArrayList();
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			String productColorKey=productName+","+color;
			if(innerDtoMap.get(productColorKey)!=null){
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			
			psiInventoryDtos.add(new PsiInventoryDto(productName, country, color,skuMap));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
		    }
		
		for(Map.Entry<String, List<PsiInventoryDto>> entry:innerDtoMap.entrySet()){
			String productColorKey = entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			Map<String,PsiInventoryDto> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDto psiInventoryDto:entry.getValue()){
				countryDtoMap.put(psiInventoryDto.getCountry(), psiInventoryDto);
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDto(proArr[0], color, countryDtoMap));
		}
		return psiInventoryTotalDtos;
	}
	
	public Map<String,PsiInventoryTotalDto> getTransporttingQuantity(String countryFlag){
		//String sql ="SELECT b.`product_name`,b.`country_code`,b.`color_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END)) FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('1','2','3','4') AND b.`country_code` = :p1 GROUP BY b.`product_name`,b.`country_code`,b.`color_code`";
		String sql ="select b.`product_name`,b.`country_code`,b.`color_code`,b.sku,sum(quantity) from (SELECT b.`product_name`,b.`country_code`,b.`color_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END)) quantity FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('1','2','3','4') AND b.`country_code` = :p1 GROUP BY b.`product_name`,b.`country_code`,b.`color_code`"+
			" union all SELECT b.`product_name`,b.`country_code`,b.`color_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END)) FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('1','2','3','4') AND b.`country_code` = :p1 GROUP BY b.`product_name`,b.`country_code`,b.`color_code`) b group by b.`product_name`,b.`country_code`,b.`color_code` ";
		
		List<Object[]> objects=this.psiInventoryDao.findBySql(sql,new Parameter(countryFlag)); 
		Map<String,PsiInventoryTotalDto> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDto>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, Integer>> producSkuMap =Maps.newHashMap();
		
		for(Object[] object:objects){
			Map<String,Integer>  inMap = Maps.newHashMap();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			String sku     = object[3].toString();
			Integer quantity   = Integer.parseInt(object[4].toString());
			String key=productName+","+country+","+color;
			if(producSkuMap.get(key)!=null){
				inMap = producSkuMap.get(key);
			}
			inMap.put(sku, quantity);
			producSkuMap.put(key, inMap);
		}
		
		for(Map.Entry<String, Map<String, Integer>> entry1:producSkuMap.entrySet()){
			String key=entry1.getKey();
			Map<String,Integer> skuMap = entry1.getValue();
			List<PsiInventoryDto> psiInventoryDtos = Lists.newArrayList();
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			String productColorKey=productName+","+color;
			if(innerDtoMap.get(productColorKey)!=null){
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			
			psiInventoryDtos.add(new PsiInventoryDto(productName, country, color,skuMap));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
		    }
		
		for(Map.Entry<String, List<PsiInventoryDto>> entry:innerDtoMap.entrySet()){
			String productColorKey = entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			Map<String,PsiInventoryDto> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDto psiInventoryDto:entry.getValue()){
				countryDtoMap.put(psiInventoryDto.getCountry(), psiInventoryDto);
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDto(proArr[0], color, countryDtoMap));
		}
		return psiInventoryTotalDtos;
	}



	
	
	/**
	 * 查询某天所有产品(在途)信息
	 */
	
	public Map<String,PsiInventoryTotalDto> getTransporttingQuantity(Date date){
		//String sql ="SELECT b.`product_name`,b.`country_code`,b.`color_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END) FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' AND b.offline_sta='0' AND a.`oper_delivery_date`>:p1  AND (a.`arrival_date`>:p1 OR a.`arrival_date` IS NULL) AND a.`transport_sta`<>'8'  GROUP BY b.`product_name`,b.`country_code`,b.`color_code`";
		
		String sql ="select b.`product_name`,b.`country_code`,b.`color_code`,b.sku,sum(quantity) from  (SELECT b.`product_name`,b.`country_code`,b.`color_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END) quantity FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' AND b.offline_sta='0' AND a.`oper_delivery_date`>:p1  AND (a.`arrival_date`>:p1 OR a.`arrival_date` IS NULL) AND a.`transport_sta`<>'8'  GROUP BY b.`product_name`,b.`country_code`,b.`color_code` "+
				" union all SELECT b.`product_name`,b.`country_code`,b.`color_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END) quantity FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' AND b.offline_sta='0' AND a.`oper_delivery_date`>:p1  AND (a.`arrival_date`>:p1 OR a.`arrival_date` IS NULL) AND a.`transport_sta`<>'8'  GROUP BY b.`product_name`,b.`country_code`,b.`color_code`) b GROUP BY b.`product_name`,b.`country_code`,b.`color_code` ";
		
		
		List<Object[]>	objects=this.psiInventoryDao.findBySql(sql,new Parameter(date)); 
		Map<String,PsiInventoryTotalDto> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDto>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, Integer>> producSkuMap =Maps.newHashMap();
		
		for(Object[] object:objects){
			Map<String,Integer>  inMap = Maps.newHashMap();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			String sku     = object[3].toString();
			Integer quantity   = Integer.parseInt(object[4].toString());
			String key=productName+","+country+","+color;
			if(producSkuMap.get(key)!=null){
				inMap = producSkuMap.get(key);
			}
			inMap.put(sku, quantity);
			producSkuMap.put(key, inMap);
		}
		
		for(Map.Entry<String, Map<String, Integer>> entry1:producSkuMap.entrySet()){
			String key=entry1.getKey();
			Map<String,Integer> skuMap = entry1.getValue();
			List<PsiInventoryDto> psiInventoryDtos = Lists.newArrayList();
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			String productColorKey=productName+","+color;
			if(innerDtoMap.get(productColorKey)!=null){
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			
			psiInventoryDtos.add(new PsiInventoryDto(productName, country, color, skuMap));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
		    }
		
		for(Map.Entry<String, List<PsiInventoryDto>> entry:innerDtoMap.entrySet()){
			String productColorKey = entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			Map<String,PsiInventoryDto> countryDtoMap = Maps.newHashMap();
			for(PsiInventoryDto psiInventoryDto:entry.getValue()){
				countryDtoMap.put(psiInventoryDto.getCountry(), psiInventoryDto);
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDto(proArr[0], color, countryDtoMap));
		}
		return psiInventoryTotalDtos;
	}
	
	
	/**
	 * 查询某天(欧洲)所有产品(在途)信息
	 */
	
	public Map<String,PsiInventoryTotalDto> getTransporttingQuantityEu(Date date){
		//String 	sql ="SELECT b.`product_name`,b.`country_code`,b.`color_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END) FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' AND b.offline_sta='0' AND a.`oper_delivery_date`>:p1  AND (a.`arrival_date`>:p1 OR a.`arrival_date` IS NULL) AND a.`transport_sta`<>'8'  AND b.`country_code` in :p2 GROUP BY b.`product_name`,b.`country_code`,b.`color_code`";
		
		String 	sql ="select b.`product_name`,b.`country_code`,b.`color_code`,b.sku,sum(quantity) from  (SELECT b.`product_name`,b.`country_code`,b.`color_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END) quantity FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' AND b.offline_sta='0' AND a.`oper_delivery_date`>:p1  AND (a.`arrival_date`>:p1 OR a.`arrival_date` IS NULL) AND a.`transport_sta`<>'8'  AND b.`country_code` in :p2 GROUP BY b.`product_name`,b.`country_code`,b.`color_code` "+
		 " union all SELECT b.`product_name`,b.`country_code`,b.`color_code`,b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END) FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' AND b.offline_sta='0' AND a.`oper_delivery_date`>:p1  AND (a.`arrival_date`>:p1 OR a.`arrival_date` IS NULL) AND a.`transport_sta`<>'8'  AND b.`country_code` in :p2 GROUP BY b.`product_name`,b.`country_code`,b.`color_code`) b GROUP BY b.`product_name`,b.`country_code`,b.`color_code` ";
		
		
		List<Object[]> 	objects=this.psiInventoryDao.findBySql(sql,new Parameter(date,new String[]{"de","fr","it","es","uk"})); 
		Map<String,PsiInventoryTotalDto> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDto>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, Integer>> producSkuMap =Maps.newHashMap();
		
		for(Object[] object:objects){
			Map<String,Integer>  inMap = Maps.newHashMap();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			String sku     = object[3].toString();
			Integer quantity   = Integer.parseInt(object[4].toString());
			String key=productName+","+country+","+color;
			if(producSkuMap.get(key)!=null){
				inMap = producSkuMap.get(key);
			}
			inMap.put(sku, quantity);
			producSkuMap.put(key, inMap);
		}
		
		for(Map.Entry<String, Map<String, Integer>> entry1:producSkuMap.entrySet()){
			String key=entry1.getKey();
			Map<String,Integer> skuMap = entry1.getValue();
			List<PsiInventoryDto> psiInventoryDtos = Lists.newArrayList();
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			String productColorKey=productName+","+color;
			if(innerDtoMap.get(productColorKey)!=null){
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			
			psiInventoryDtos.add(new PsiInventoryDto(productName, country, color, skuMap));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
		    }
		
		for(Map.Entry<String, List<PsiInventoryDto>> entry:innerDtoMap.entrySet()){
			String productColorKey = entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			Map<String,PsiInventoryDto> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDto psiInventoryDto:entry.getValue()){
				countryDtoMap.put(psiInventoryDto.getCountry(), psiInventoryDto);
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDto(proArr[0], color, countryDtoMap));
		}
		return psiInventoryTotalDtos;
	}
	
	
	/**
	 * 查询某天单产品(在途)信息
	 */
	public PsiInventoryDto getTransporttingQuantity(Date date,String productName,String color,String countryCode){
		
		Parameter parameter = new Parameter(date,productName,color,countryCode);
		//String sql ="SELECT b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END)) -SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END) FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' AND b.offline_sta='0' AND a.`oper_delivery_date`>:p1  AND (a.`arrival_date`>:p1 OR a.`arrival_date` IS NULL) AND b.`product_name`=:p2 AND b.`color_code`=:p3 AND b.`country_code` =:p4 AND a.`transport_sta`<>'8'  GROUP BY b.`product_name`,b.`country_code`,b.`color_code`";
		
		String sql ="select b.sku,sum(quantity) from  (SELECT b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END)) -SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END) quantity FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' AND b.offline_sta='0' AND a.`oper_delivery_date`>:p1  AND (a.`arrival_date`>:p1 OR a.`arrival_date` IS NULL) AND b.`product_name`=:p2 AND b.`color_code`=:p3 AND b.`country_code` =:p4 AND a.`transport_sta`<>'8'  GROUP BY b.`product_name`,b.`country_code`,b.`color_code` "+
				" union all SELECT b.sku,SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END)) -SUM(CASE WHEN a.`transport_type` ='1' THEN  b.`shipped_quantity` ELSE 0 END) FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` and b.`del_flag`='0' AND b.offline_sta='0' AND a.`oper_delivery_date`>:p1  AND (a.`arrival_date`>:p1 OR a.`arrival_date` IS NULL) AND b.`product_name`=:p2 AND b.`color_code`=:p3 AND b.`country_code` =:p4 AND a.`transport_sta`<>'8'  GROUP BY b.`product_name`,b.`country_code`,b.`color_code`) b group by b.sku" ;
		
		List<Object[]> objects=this.psiInventoryDao.findBySql(sql,parameter); 
		Map<String,Integer>  inMap = Maps.newHashMap();
		for(Object[] object:objects){
			String   sku    	= object[0].toString();
			Integer  quantity   = Integer.parseInt(object[1].toString());
			inMap.put(sku, quantity);
		}
		return new PsiInventoryDto(productName, countryCode, color, inMap);
	}
	
	/**
	 * 查询所有产品(在库)信息
	 */
	
	public Map<String,PsiInventoryTotalDtoByInStock> getInventoryQuantity(Integer warehouseId){
		if(warehouseId==null){
			return null;
		}
		//获取仓库国家简码
		Map<Integer,String> stockMap=this.stockService.findSelfStockCountryCode();
		String houseCode=stockMap.get(warehouseId);
		
		
		String sql ="SELECT a.`product_name`,a.`country_code`,a.`color_code`,a.sku,a.`new_quantity`,a.`old_quantity`,a.`broken_quantity`,a.`renew_quantity`,a.`offline_quantity` FROM psi_inventory AS a WHERE a.warehouse_id=:p1 ";
		List<Object[]>	objects=this.psiInventoryDao.findBySql(sql,new Parameter(warehouseId));
		Map<String,PsiInventoryTotalDtoByInStock> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDtoByInStockWithWarehouseCode>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, String>> producSkuMap =Maps.newHashMap();
		
		for(Object[] object:objects){
			Map<String,String>  inMap = Maps.newHashMap();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			String sku     = object[3].toString();
			Integer  newQuantity   = Integer.parseInt(object[4].toString());
			Integer  oldQuantity   = Integer.parseInt(object[5].toString());
			Integer  brokenQuantity   = Integer.parseInt(object[6].toString());
			Integer  renewQuantity   = Integer.parseInt(object[7].toString());
			Integer  offlineQuantity   = Integer.parseInt(object[8].toString());
			String key=productName+","+country+","+color;
			if(producSkuMap.get(key)!=null){
				inMap = producSkuMap.get(key);
			}
			String value=newQuantity+","+oldQuantity+","+brokenQuantity+","+renewQuantity+","+offlineQuantity;
			if(inMap.get(sku)!=null){
				String skuValue=inMap.get(sku);
				Integer skuNewQuantity =Integer.parseInt(skuValue.split(",")[0]);
				Integer skuOldQuantity =Integer.parseInt(skuValue.split(",")[1]);
				Integer skuBrokenQuantity =Integer.parseInt(skuValue.split(",")[2]);
				Integer skuRenewQuantity =Integer.parseInt(skuValue.split(",")[3]);
				Integer skuOfflineQuantity =Integer.parseInt(skuValue.split(",")[4]);
				value=newQuantity+skuNewQuantity+","+oldQuantity+skuOldQuantity+","+brokenQuantity+skuBrokenQuantity+","+renewQuantity+skuRenewQuantity+","+offlineQuantity+skuOfflineQuantity;
			}
			inMap.put(sku, value);
			producSkuMap.put(key, inMap);
		}
		
		for(Map.Entry<String,Map<String,String>> entry1:producSkuMap.entrySet()){
			String key=entry1.getKey();
			List<PsiInventoryDtoByInStockWithWarehouseCode> houseDtos = Lists.newArrayList();
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			String productColorKey=productName+","+color;
			
			Map<String,String> skuMap = entry1.getValue();
			Map<String,Integer> skuNewMap    =Maps.newHashMap(); 
			Map<String,Integer> skuOldMap    =Maps.newHashMap(); 
			Map<String,Integer> skuBrokenMap =Maps.newHashMap();
			Map<String,Integer> skuRenewMap  =Maps.newHashMap();
			Map<String,Integer> skuOfflineMap  =Maps.newHashMap();
			for(Map.Entry<String,String> entry:skuMap.entrySet()){
				String sku = entry.getKey();
				String value=entry.getValue();
				Integer newQuantity =Integer.parseInt(value.split(",")[0]);
				Integer oldQuantity =Integer.parseInt(value.split(",")[1]);
				Integer brokenQuantity =Integer.parseInt(value.split(",")[2]);
				Integer renewQuantity =Integer.parseInt(value.split(",")[3]);
				Integer offlineQuantity =Integer.parseInt(value.split(",")[4]);
				if(skuNewMap.get(sku)!=null){
					newQuantity+=skuNewMap.get(sku);
					oldQuantity+=skuOldMap.get(sku);
					brokenQuantity+=skuBrokenMap.get(sku);
					renewQuantity+=skuRenewMap.get(sku);
					offlineQuantity+=skuOfflineMap.get(sku);
				}
				skuNewMap.put(sku, newQuantity);
				skuOldMap.put(sku, oldQuantity);
				skuBrokenMap.put(sku, brokenQuantity);
				skuRenewMap.put(sku, renewQuantity);
				skuOfflineMap.put(sku, offlineQuantity);
			}
			
			
			if(innerDtoMap.get(productColorKey)!=null){
				houseDtos=innerDtoMap.get(productColorKey);
			}
			PsiInventoryDtoByInStockWithWarehouseCode  houseDto=new PsiInventoryDtoByInStockWithWarehouseCode(productName, country, color, skuNewMap, skuOldMap, skuBrokenMap, skuRenewMap,skuOfflineMap, houseCode);
			houseDtos.add(houseDto);
			innerDtoMap.put(productColorKey, houseDtos);
		    }
		
		for(Map.Entry<String, List<PsiInventoryDtoByInStockWithWarehouseCode>> entry:innerDtoMap.entrySet()){
			String productColorKey =entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name = proArr[0];
			String color = "";
			if(proArr.length>1){
				color = proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name = name+"_"+color;
			}
			
			Map<String,PsiInventoryDtoByInStock> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDtoByInStockWithWarehouseCode houseDto:entry.getValue()){
				String counrty = houseDto.getCountry();
				PsiInventoryDtoByInStock temp = countryDtoMap.get(counrty);
				if(temp==null){
					Map<String,PsiInventoryDtoByInStockWithWarehouseCode> houseMap=Maps.newHashMap();
					houseMap.put(houseCode, houseDto);
					countryDtoMap.put(houseDto.getCountry(), new PsiInventoryDtoByInStock(houseDto.getProductName(), houseDto.getCountry(), color, houseMap));
				}else{
					temp.getQuantityInventory().put(houseCode, houseDto);
				}
			
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDtoByInStock(proArr[0], color, countryDtoMap));
		}
		
		return psiInventoryTotalDtos;
	}
	
	/**
	 * 查询所有产品(在库)信息
	 */
	
	public Map<String,PsiInventoryTotalDtoByInStock> getInventoryQuantity(){
		//获取仓库国家简码
		Map<Integer,String> stockMap=this.stockService.findSelfStockCountryCode();
		String	sql ="SELECT a.`product_name`,a.`country_code`,a.`color_code`,a.`sku`,a.`warehouse_id`,a.`new_quantity`,a.`old_quantity`,a.`broken_quantity`,a.`renew_quantity`,a.`offline_quantity` FROM psi_inventory AS a  ";
		List<Object[]> objects=this.psiInventoryDao.findBySql(sql); 
		Map<String,PsiInventoryTotalDtoByInStock> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDtoByInStockWithWarehouseCode>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, String>> producSkuMap =Maps.newHashMap();
		
		for(Object[] object:objects){
			Map<String,String>  inMap = Maps.newHashMap();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			String sku     = object[3].toString();
			String warehouseId=object[4].toString();
			Integer  newQuantity   = Integer.parseInt(object[5].toString());
			Integer  oldQuantity   = Integer.parseInt(object[6].toString());
			Integer  brokenQuantity   = Integer.parseInt(object[7].toString());
			Integer  renewQuantity   = Integer.parseInt(object[8].toString());
			Integer  offlineQuantity   = Integer.parseInt(object[9].toString());
			String key=productName+","+country+","+color+","+warehouseId;
			if(producSkuMap.get(key)!=null){
				inMap = producSkuMap.get(key);
			}
			String value=newQuantity+","+oldQuantity+","+brokenQuantity+","+renewQuantity+","+offlineQuantity;
			if(inMap.get(sku)!=null){
				String skuValue=inMap.get(sku);
				Integer skuNewQuantity =Integer.parseInt(skuValue.split(",")[0]);
				Integer skuOldQuantity =Integer.parseInt(skuValue.split(",")[1]);
				Integer skuBrokenQuantity =Integer.parseInt(skuValue.split(",")[2]);
				Integer skuRenewQuantity =Integer.parseInt(skuValue.split(",")[3]);
				Integer skuOfflineQuantity =Integer.parseInt(skuValue.split(",")[4]);
				value=newQuantity+skuNewQuantity+","+oldQuantity+skuOldQuantity+","+brokenQuantity+skuBrokenQuantity+","+renewQuantity+skuRenewQuantity+","+offlineQuantity+skuOfflineQuantity;
			}
			inMap.put(sku, value);
			producSkuMap.put(key, inMap);
		}
		
		for(Map.Entry<String,Map<String,String>> entry1:producSkuMap.entrySet()){
			String key=entry1.getKey();
			List<PsiInventoryDtoByInStockWithWarehouseCode> psiInventoryDtos =null;
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			Integer warehouseId=Integer.parseInt(arr[3].toString());
			String warehouseCode=stockMap.get(warehouseId);
			
			String productColorKey=productName+","+color;
			Map<String,String> inSkuMap = entry1.getValue();
			Map<String,Integer> skuNewMap    =Maps.newHashMap(); 
			Map<String,Integer> skuOldMap    =Maps.newHashMap(); 
			Map<String,Integer> skuBrokenMap =Maps.newHashMap();
			Map<String,Integer> skuRenewMap  =Maps.newHashMap();
			Map<String,Integer> skuOfflineMap  =Maps.newHashMap();
			for(Map.Entry<String,String> entry:inSkuMap.entrySet()){
				String sku = entry.getKey();
				String value=entry.getValue();
				Integer newQuantity =Integer.parseInt(value.split(",")[0]);
				Integer oldQuantity =Integer.parseInt(value.split(",")[1]);
				Integer brokenQuantity =Integer.parseInt(value.split(",")[2]);
				Integer renewQuantity =Integer.parseInt(value.split(",")[3]);
				Integer offlineQuantity =Integer.parseInt(value.split(",")[4]);
				if(skuNewMap.get(sku)!=null){
					newQuantity+=skuNewMap.get(sku);
					oldQuantity+=skuOldMap.get(sku);
					brokenQuantity+=skuBrokenMap.get(sku);
					renewQuantity+=skuRenewMap.get(sku);
					offlineQuantity+=skuOfflineMap.get(sku);
				}
				skuNewMap.put(sku, newQuantity);
				skuOldMap.put(sku, oldQuantity);
				skuBrokenMap.put(sku, brokenQuantity);
				skuRenewMap.put(sku, renewQuantity);
				skuOfflineMap.put(sku, offlineQuantity);
			}
			
			
			if(innerDtoMap.get(productColorKey)==null){
				psiInventoryDtos=Lists.newArrayList();
			}else{
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			
			psiInventoryDtos.add(new PsiInventoryDtoByInStockWithWarehouseCode(productName, country, color,skuNewMap,skuOldMap,skuBrokenMap,skuRenewMap,skuOfflineMap,warehouseCode));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
			
			
		}
	
		
		for(Map.Entry<String, List<PsiInventoryDtoByInStockWithWarehouseCode>> entry:innerDtoMap.entrySet()){
			String productColorKey =entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			Map<String,PsiInventoryDtoByInStock> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDtoByInStockWithWarehouseCode houseDto:entry.getValue()){
				String houseCode=houseDto.getWarehouseCode();
				String counrty = houseDto.getCountry();
				PsiInventoryDtoByInStock temp = countryDtoMap.get(counrty);
				if(temp==null){
					Map<String,PsiInventoryDtoByInStockWithWarehouseCode> houseMap=Maps.newHashMap();
					houseMap.put(houseCode, houseDto);
					countryDtoMap.put(houseDto.getCountry(), new PsiInventoryDtoByInStock(houseDto.getProductName(), houseDto.getCountry(), color, houseMap));
				}else{
					//temp.getQuantityInventory().put(houseCode, houseDto);

					PsiInventoryDtoByInStockWithWarehouseCode dto=temp.getQuantityInventory().get(houseCode);
					if(dto==null){
						temp.getQuantityInventory().put(houseCode, houseDto);
					}else{
						Map<String,Integer> skuNewMap=houseDto.getSkusNewQuantity(); 
						Map<String,Integer> skuOldMap=houseDto.getSkusOldQuantity(); 
						Map<String,Integer> skuBrokenMap =houseDto.getSkusBrokenQuantity();
						Map<String,Integer> skuRenewMap  =houseDto.getSkusRenewQuantity();
						Map<String,Integer> skuOfflineMap  =houseDto.getSkusOfflineQuantity();
						if(dto.getSkusNewQuantity()!=null&&dto.getSkusNewQuantity().size()>0){
							for (Map.Entry<String, Integer> entryNew: dto.getSkusNewQuantity().entrySet()) {
								String sku = entryNew.getKey();
								skuNewMap.put(sku,entryNew.getValue()+(skuNewMap.get(sku)==null?0:skuNewMap.get(sku)));
							}
						}
						if(dto.getSkusOldQuantity()!=null&&dto.getSkusOldQuantity().size()>0){
							for (Map.Entry<String, Integer> entryOld: dto.getSkusOldQuantity().entrySet()) {
								String sku = entryOld.getKey();
								skuOldMap.put(sku,entryOld.getValue()+(skuOldMap.get(sku)==null?0:skuOldMap.get(sku)));
							}
						}
						if(dto.getSkusBrokenQuantity()!=null&&dto.getSkusBrokenQuantity().size()>0){
							for (Map.Entry<String, Integer> entryBroken: dto.getSkusBrokenQuantity().entrySet()) {
								String sku = entryBroken.getKey();
								skuBrokenMap.put(sku,entryBroken.getValue()+(skuBrokenMap.get(sku)==null?0:skuBrokenMap.get(sku)));
							}
						}
						if(dto.getSkusRenewQuantity()!=null&&dto.getSkusRenewQuantity().size()>0){
							for (Map.Entry<String, Integer> entryRenew: dto.getSkusRenewQuantity().entrySet()) {
								String sku = entryRenew.getKey();
								skuRenewMap.put(sku,entryRenew.getValue()+(skuRenewMap.get(sku)==null?0:skuRenewMap.get(sku)));
							}
						}
						if(dto.getSkusOfflineQuantity()!=null&&dto.getSkusOfflineQuantity().size()>0){
							for (Map.Entry<String, Integer> entryOffline: dto.getSkusOfflineQuantity().entrySet()) {
								String sku = entryOffline.getKey();
								skuOfflineMap.put(sku,entryOffline.getValue()+(skuOfflineMap.get(sku)==null?0:skuOfflineMap.get(sku)));
							}
						}
						
						temp.getQuantityInventory().put(houseCode, houseDto);
					}
				}
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDtoByInStock(proArr[0], color, countryDtoMap));
			
		}
		return psiInventoryTotalDtos;
	}
	
	public Map<String,PsiInventoryTotalDtoByInStock> getInventoryQuantity(Set<String> nameSet){
		//获取仓库国家简码
		Map<Integer,String> stockMap=this.stockService.findSelfStockCountryCode();
		String	sql ="SELECT a.`product_name`,a.`country_code`,a.`color_code`,a.`sku`,a.`warehouse_id`,a.`new_quantity`,a.`old_quantity`,a.`broken_quantity`,a.`renew_quantity`,a.`offline_quantity` FROM psi_inventory AS a  "+
				"  where CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END ) in :p1 ";
		List<Object[]> objects=this.psiInventoryDao.findBySql(sql,new Parameter(nameSet)); 
		Map<String,PsiInventoryTotalDtoByInStock> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDtoByInStockWithWarehouseCode>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, String>> producSkuMap =Maps.newHashMap();
		
		for(Object[] object:objects){
			Map<String,String>  inMap = Maps.newHashMap();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			String sku     = object[3].toString();
			String warehouseId=object[4].toString();
			Integer  newQuantity   = Integer.parseInt(object[5].toString());
			Integer  oldQuantity   = Integer.parseInt(object[6].toString());
			Integer  brokenQuantity   = Integer.parseInt(object[7].toString());
			Integer  renewQuantity   = Integer.parseInt(object[8].toString());
			Integer  offlineQuantity   = Integer.parseInt(object[9].toString());
			String key=productName+","+country+","+color+","+warehouseId;
			if(producSkuMap.get(key)!=null){
				inMap = producSkuMap.get(key);
			}
			String value=newQuantity+","+oldQuantity+","+brokenQuantity+","+renewQuantity+","+offlineQuantity;
			if(inMap.get(sku)!=null){
				String skuValue=inMap.get(sku);
				Integer skuNewQuantity =Integer.parseInt(skuValue.split(",")[0]);
				Integer skuOldQuantity =Integer.parseInt(skuValue.split(",")[1]);
				Integer skuBrokenQuantity =Integer.parseInt(skuValue.split(",")[2]);
				Integer skuRenewQuantity =Integer.parseInt(skuValue.split(",")[3]);
				Integer skuOfflineQuantity =Integer.parseInt(skuValue.split(",")[4]);
				value=newQuantity+skuNewQuantity+","+oldQuantity+skuOldQuantity+","+brokenQuantity+skuBrokenQuantity+","+renewQuantity+skuRenewQuantity+","+offlineQuantity+skuOfflineQuantity;
			}
			inMap.put(sku, value);
			producSkuMap.put(key, inMap);
		}
		
		for(Map.Entry<String,Map<String,String>> entry1:producSkuMap.entrySet()){
			String key=entry1.getKey();
			List<PsiInventoryDtoByInStockWithWarehouseCode> psiInventoryDtos =null;
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			Integer warehouseId=Integer.parseInt(arr[3].toString());
			String warehouseCode=stockMap.get(warehouseId);
			
			String productColorKey=productName+","+color;
			Map<String,String> inSkuMap = entry1.getValue();
			Map<String,Integer> skuNewMap    =Maps.newHashMap(); 
			Map<String,Integer> skuOldMap    =Maps.newHashMap(); 
			Map<String,Integer> skuBrokenMap =Maps.newHashMap();
			Map<String,Integer> skuRenewMap  =Maps.newHashMap();
			Map<String,Integer> skuOfflineMap  =Maps.newHashMap();
			for(Map.Entry<String,String> entry:inSkuMap.entrySet()){
				String sku = entry.getKey();
				String value=entry.getValue();
				Integer newQuantity =Integer.parseInt(value.split(",")[0]);
				Integer oldQuantity =Integer.parseInt(value.split(",")[1]);
				Integer brokenQuantity =Integer.parseInt(value.split(",")[2]);
				Integer renewQuantity =Integer.parseInt(value.split(",")[3]);
				Integer offlineQuantity =Integer.parseInt(value.split(",")[4]);
				if(skuNewMap.get(sku)!=null){
					newQuantity+=skuNewMap.get(sku);
					oldQuantity+=skuOldMap.get(sku);
					brokenQuantity+=skuBrokenMap.get(sku);
					renewQuantity+=skuRenewMap.get(sku);
					offlineQuantity+=skuOfflineMap.get(sku);
				}
				skuNewMap.put(sku, newQuantity);
				skuOldMap.put(sku, oldQuantity);
				skuBrokenMap.put(sku, brokenQuantity);
				skuRenewMap.put(sku, renewQuantity);
				skuOfflineMap.put(sku, offlineQuantity);
			}
			
			
			if(innerDtoMap.get(productColorKey)==null){
				psiInventoryDtos=Lists.newArrayList();
			}else{
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			
			psiInventoryDtos.add(new PsiInventoryDtoByInStockWithWarehouseCode(productName, country, color,skuNewMap,skuOldMap,skuBrokenMap,skuRenewMap,skuOfflineMap,warehouseCode));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
			
			
		}
	
		
		for(Map.Entry<String, List<PsiInventoryDtoByInStockWithWarehouseCode>> entry:innerDtoMap.entrySet()){
			String productColorKey =entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			Map<String,PsiInventoryDtoByInStock> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDtoByInStockWithWarehouseCode houseDto:entry.getValue()){
				String houseCode=houseDto.getWarehouseCode();
				String counrty = houseDto.getCountry();
				PsiInventoryDtoByInStock temp = countryDtoMap.get(counrty);
				if(temp==null){
					Map<String,PsiInventoryDtoByInStockWithWarehouseCode> houseMap=Maps.newHashMap();
					houseMap.put(houseCode, houseDto);
					countryDtoMap.put(houseDto.getCountry(), new PsiInventoryDtoByInStock(houseDto.getProductName(), houseDto.getCountry(), color, houseMap));
				}else{
					//temp.getQuantityInventory().put(houseCode, houseDto);

					PsiInventoryDtoByInStockWithWarehouseCode dto=temp.getQuantityInventory().get(houseCode);
					if(dto==null){
						temp.getQuantityInventory().put(houseCode, houseDto);
					}else{
						Map<String,Integer> skuNewMap=houseDto.getSkusNewQuantity(); 
						Map<String,Integer> skuOldMap=houseDto.getSkusOldQuantity(); 
						Map<String,Integer> skuBrokenMap =houseDto.getSkusBrokenQuantity();
						Map<String,Integer> skuRenewMap  =houseDto.getSkusRenewQuantity();
						Map<String,Integer> skuOfflineMap  =houseDto.getSkusOfflineQuantity();
						if(dto.getSkusNewQuantity()!=null&&dto.getSkusNewQuantity().size()>0){
							for (Map.Entry<String, Integer> entryNew: dto.getSkusNewQuantity().entrySet()) {
								String sku = entryNew.getKey();
								skuNewMap.put(sku,entryNew.getValue()+(skuNewMap.get(sku)==null?0:skuNewMap.get(sku)));
							}
						}
						if(dto.getSkusOldQuantity()!=null&&dto.getSkusOldQuantity().size()>0){
							for (Map.Entry<String, Integer> entryOld: dto.getSkusOldQuantity().entrySet()) {
								String sku = entryOld.getKey();
								skuOldMap.put(sku,entryOld.getValue()+(skuOldMap.get(sku)==null?0:skuOldMap.get(sku)));
							}
						}
						if(dto.getSkusBrokenQuantity()!=null&&dto.getSkusBrokenQuantity().size()>0){
							for (Map.Entry<String, Integer> entryBroken: dto.getSkusBrokenQuantity().entrySet()) {
								String sku = entryBroken.getKey();
								skuBrokenMap.put(sku,entryBroken.getValue()+(skuBrokenMap.get(sku)==null?0:skuBrokenMap.get(sku)));
							}
						}
						if(dto.getSkusRenewQuantity()!=null&&dto.getSkusRenewQuantity().size()>0){
							for (Map.Entry<String, Integer> entryRenew: dto.getSkusRenewQuantity().entrySet()) {
								String sku = entryRenew.getKey();
								skuRenewMap.put(sku,entryRenew.getValue()+(skuRenewMap.get(sku)==null?0:skuRenewMap.get(sku)));
							}
						}
						if(dto.getSkusOfflineQuantity()!=null&&dto.getSkusOfflineQuantity().size()>0){
							for (Map.Entry<String, Integer> entryOffline: dto.getSkusOfflineQuantity().entrySet()) {
								String sku = entryOffline.getKey();
								skuOfflineMap.put(sku,entryOffline.getValue()+(skuOfflineMap.get(sku)==null?0:skuOfflineMap.get(sku)));
							}
						}
						
						temp.getQuantityInventory().put(houseCode, houseDto);
					}
				}
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDtoByInStock(proArr[0], color, countryDtoMap));
			
		}
		return psiInventoryTotalDtos;
	}
	
	/**
	 * 查询所有产品(在库)信息      这里面理诚库存为CnLc
	 */
	
	public Map<String,PsiInventoryTotalDtoByInStock> getInventoryQuantity2(){
		//获取仓库国家简码
		Map<Integer,String> stockMap=this.stockService.findSelfStockCountryCode();
		String	sql ="SELECT a.`product_name`,a.`country_code`,a.`color_code`,a.`sku`,a.`warehouse_id`,a.`new_quantity`,a.`old_quantity`,a.`broken_quantity`,a.`renew_quantity`,a.`offline_quantity` FROM psi_inventory AS a  ";
		List<Object[]> objects=this.psiInventoryDao.findBySql(sql); 
		Map<String,PsiInventoryTotalDtoByInStock> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDtoByInStockWithWarehouseCode>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, String>> producSkuMap =Maps.newHashMap();
		
		for(Object[] object:objects){
			Map<String,String>  inMap = Maps.newHashMap();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			String sku     = object[3].toString();
			String warehouseId=object[4].toString();
			Integer  newQuantity   = Integer.parseInt(object[5].toString());
			Integer  oldQuantity   = Integer.parseInt(object[6].toString());
			Integer  brokenQuantity   = Integer.parseInt(object[7].toString());
			Integer  renewQuantity   = Integer.parseInt(object[8].toString());
			Integer  offlineQuantity   = Integer.parseInt(object[9].toString());
			String key=productName+","+country+","+color+","+warehouseId;
			if(producSkuMap.get(key)!=null){
				inMap = producSkuMap.get(key);
			}
			String value=newQuantity+","+oldQuantity+","+brokenQuantity+","+renewQuantity+","+offlineQuantity;
			if(inMap.get(sku)!=null){
				String skuValue=inMap.get(sku);
				Integer skuNewQuantity =Integer.parseInt(skuValue.split(",")[0]);
				Integer skuOldQuantity =Integer.parseInt(skuValue.split(",")[1]);
				Integer skuBrokenQuantity =Integer.parseInt(skuValue.split(",")[2]);
				Integer skuRenewQuantity =Integer.parseInt(skuValue.split(",")[3]);
				Integer skuOfflineQuantity =Integer.parseInt(skuValue.split(",")[4]);
				value=newQuantity+skuNewQuantity+","+oldQuantity+skuOldQuantity+","+brokenQuantity+skuBrokenQuantity+","+renewQuantity+skuRenewQuantity+","+offlineQuantity+skuOfflineQuantity;
			}
			inMap.put(sku, value);
			producSkuMap.put(key, inMap);
		}
		
		for(Map.Entry<String,Map<String,String>> entry1:producSkuMap.entrySet()){
			String key=entry1.getKey();
			List<PsiInventoryDtoByInStockWithWarehouseCode> psiInventoryDtos =null;
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			Integer warehouseId=Integer.parseInt(arr[3].toString());
			String warehouseCode=stockMap.get(warehouseId);
			
			String productColorKey=productName+","+color;
			Map<String,String> inSkuMap = producSkuMap.get(key);
			Map<String,Integer> skuNewMap    =Maps.newHashMap(); 
			Map<String,Integer> skuOldMap    =Maps.newHashMap(); 
			Map<String,Integer> skuBrokenMap =Maps.newHashMap();
			Map<String,Integer> skuRenewMap  =Maps.newHashMap();
			Map<String,Integer> skuOfflineMap  =Maps.newHashMap();
			for(Map.Entry<String,String> entry:inSkuMap.entrySet()){
				String sku = entry.getKey();
				String value=entry.getValue();
				Integer newQuantity =Integer.parseInt(value.split(",")[0]);
				Integer oldQuantity =Integer.parseInt(value.split(",")[1]);
				Integer brokenQuantity =Integer.parseInt(value.split(",")[2]);
				Integer renewQuantity =Integer.parseInt(value.split(",")[3]);
				Integer offlineQuantity =Integer.parseInt(value.split(",")[4]);
				if(skuNewMap.get(sku)!=null){
					newQuantity+=skuNewMap.get(sku);
					oldQuantity+=skuOldMap.get(sku);
					brokenQuantity+=skuBrokenMap.get(sku);
					renewQuantity+=skuRenewMap.get(sku);
					offlineQuantity+=skuOfflineMap.get(sku);
				}
				skuNewMap.put(sku, newQuantity);
				skuOldMap.put(sku, oldQuantity);
				skuBrokenMap.put(sku, brokenQuantity);
				skuRenewMap.put(sku, renewQuantity);
				skuOfflineMap.put(sku, offlineQuantity);
			}
			
			
			if(innerDtoMap.get(productColorKey)==null){
				psiInventoryDtos=Lists.newArrayList();
			}else{
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			
			psiInventoryDtos.add(new PsiInventoryDtoByInStockWithWarehouseCode(productName, country, color,skuNewMap,skuOldMap,skuBrokenMap,skuRenewMap,skuOfflineMap,warehouseCode));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
			
			
			
			//上面中国仓都是cn，下面把理诚仓库变成cnLc  start
			if(warehouseId.intValue()==130){
				warehouseCode="CnLc";
				Map<String,String> inSkuMap1 = producSkuMap.get(key);
				Map<String,Integer> skuNewMap1    =Maps.newHashMap(); 
				Map<String,Integer> skuOldMap1    =Maps.newHashMap(); 
				Map<String,Integer> skuBrokenMap1 =Maps.newHashMap();
				Map<String,Integer> skuRenewMap1  =Maps.newHashMap();
				Map<String,Integer> skuOfflineMap1  =Maps.newHashMap();
				for(Map.Entry<String,String> entry:inSkuMap1.entrySet()){
					String sku= entry.getKey();
					String value=entry.getValue();
					Integer newQuantity1 =Integer.parseInt(value.split(",")[0]);
					Integer oldQuantity1 =Integer.parseInt(value.split(",")[1]);
					Integer brokenQuantity1 =Integer.parseInt(value.split(",")[2]);
					Integer renewQuantity1 =Integer.parseInt(value.split(",")[3]);
					Integer offlineQuantity1 =Integer.parseInt(value.split(",")[4]);
					if(skuNewMap1.get(sku)!=null){
						newQuantity1+=skuNewMap1.get(sku);
						oldQuantity1+=skuOldMap1.get(sku);
						brokenQuantity1+=skuBrokenMap1.get(sku);
						renewQuantity1+=skuRenewMap1.get(sku);
						offlineQuantity1+=skuOfflineMap1.get(sku);
					}
					skuNewMap1.put(sku, newQuantity1);
					skuOldMap1.put(sku, oldQuantity1);
					skuBrokenMap1.put(sku, brokenQuantity1);
					skuRenewMap1.put(sku, renewQuantity1);
					skuOfflineMap1.put(sku, offlineQuantity1);
				}
				
				
				if(innerDtoMap.get(productColorKey)==null){
					psiInventoryDtos=Lists.newArrayList();
				}else{
					psiInventoryDtos=innerDtoMap.get(productColorKey);
				}
				
				psiInventoryDtos.add(new PsiInventoryDtoByInStockWithWarehouseCode(productName, country, color,skuNewMap,skuOldMap,skuBrokenMap,skuRenewMap,skuOfflineMap,warehouseCode));
				innerDtoMap.put(productColorKey, psiInventoryDtos);
			}
			//上面中国仓都是cn，下面把理诚仓库变成cnLc  end 20161030
		}
	
		
		for(Map.Entry<String, List<PsiInventoryDtoByInStockWithWarehouseCode>> entry:innerDtoMap.entrySet()){
			String productColorKey =entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			Map<String,PsiInventoryDtoByInStock> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDtoByInStockWithWarehouseCode houseDto:entry.getValue()){
				String houseCode=houseDto.getWarehouseCode();
				String counrty = houseDto.getCountry();
				PsiInventoryDtoByInStock temp = countryDtoMap.get(counrty);
				if(temp==null){
					Map<String,PsiInventoryDtoByInStockWithWarehouseCode> houseMap=Maps.newHashMap();
					houseMap.put(houseCode, houseDto);
					countryDtoMap.put(houseDto.getCountry(), new PsiInventoryDtoByInStock(houseDto.getProductName(), houseDto.getCountry(), color, houseMap));
				}else{
					//temp.getQuantityInventory().put(houseCode, houseDto);

					PsiInventoryDtoByInStockWithWarehouseCode dto=temp.getQuantityInventory().get(houseCode);
					if(dto==null){
						temp.getQuantityInventory().put(houseCode, houseDto);
					}else{
						Map<String,Integer> skuNewMap=houseDto.getSkusNewQuantity(); 
						Map<String,Integer> skuOldMap=houseDto.getSkusOldQuantity(); 
						Map<String,Integer> skuBrokenMap =houseDto.getSkusBrokenQuantity();
						Map<String,Integer> skuRenewMap  =houseDto.getSkusRenewQuantity();
						Map<String,Integer> skuOfflineMap  =houseDto.getSkusOfflineQuantity();
						if(dto.getSkusNewQuantity()!=null&&dto.getSkusNewQuantity().size()>0){
							for (Map.Entry<String, Integer> entryNew: dto.getSkusNewQuantity().entrySet()) {
								String sku = entryNew.getKey();
								skuNewMap.put(sku,entryNew.getValue()+(skuNewMap.get(sku)==null?0:skuNewMap.get(sku)));
							}
						}
						if(dto.getSkusOldQuantity()!=null&&dto.getSkusOldQuantity().size()>0){
							for (Map.Entry<String, Integer> entryOld: dto.getSkusOldQuantity().entrySet()) {
								String sku = entryOld.getKey();
								skuOldMap.put(sku,entryOld.getValue()+(skuOldMap.get(sku)==null?0:skuOldMap.get(sku)));
							}
						}
						if(dto.getSkusBrokenQuantity()!=null&&dto.getSkusBrokenQuantity().size()>0){
							for (Map.Entry<String, Integer> entryBroken: dto.getSkusBrokenQuantity().entrySet()) {
								String sku = entryBroken.getKey();
								skuBrokenMap.put(sku,entryBroken.getValue()+(skuBrokenMap.get(sku)==null?0:skuBrokenMap.get(sku)));
							}
						}
						if(dto.getSkusRenewQuantity()!=null&&dto.getSkusRenewQuantity().size()>0){
							for (Map.Entry<String, Integer> entryRenew: dto.getSkusRenewQuantity().entrySet()) {
								String sku = entryRenew.getKey();
								skuRenewMap.put(sku,entryRenew.getValue()+(skuRenewMap.get(sku)==null?0:skuRenewMap.get(sku)));
							}
						}
						if(dto.getSkusOfflineQuantity()!=null&&dto.getSkusOfflineQuantity().size()>0){
							for (Map.Entry<String, Integer> entryOffline: dto.getSkusOfflineQuantity().entrySet()) {
								String sku = entryOffline.getKey();
								skuOfflineMap.put(sku,entryOffline.getValue()+(skuOfflineMap.get(sku)==null?0:skuOfflineMap.get(sku)));
							}
						}
						
						temp.getQuantityInventory().put(houseCode, houseDto);
					}
				}
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDtoByInStock(proArr[0], color, countryDtoMap));
			
		}
		return psiInventoryTotalDtos;
	}
	
	/**
	 * 查询单产品(在库)信息
	 */
	
	public PsiInventoryTotalDtoByInStock getInventoryQuantity(String productName_color,String country){
		
		//获取仓库国家简码
		Map<Integer,String> stockMap=this.stockService.findSelfStockCountryCode();
		
		String sql="";
		Parameter parameter = null;
		if(StringUtils.isNotEmpty(country)){
			sql ="SELECT a.`product_name`,a.`country_code`,a.`color_code`,a.sku,a.`warehouse_id`,a.`new_quantity`,a.`old_quantity`,a.`broken_quantity`,a.`renew_quantity`,a.`offline_quantity` FROM psi_inventory AS a WHERE CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END )=:p1 AND a.`country_code` = :p2 ";
			parameter=new Parameter(productName_color,country);
		}else{
			sql ="SELECT a.`product_name`,a.`country_code`,a.`color_code`,a.sku,a.`warehouse_id`,a.`new_quantity`,a.`old_quantity`,a.`broken_quantity`,a.`renew_quantity`,a.`offline_quantity` FROM psi_inventory AS a WHERE CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END )=:p1 ";
			parameter=new Parameter(productName_color);   
		}
		List<Object[]> objects = this.psiInventoryDao.findBySql(sql,parameter); 
		
		String productName="";
		String color="";   
		
		Map<String,List<PsiInventoryDtoByInStockWithWarehouseCode>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, String>> producSkuMap =Maps.newHashMap();
		
		for(Object[] object:objects){
			Map<String,String>  inMap = Maps.newHashMap();
			if(StringUtils.isEmpty(productName)){
				productName = object[0].toString();
				color   = object[2].toString();
			}
			String country1 = object[1].toString();
			String sku     = object[3].toString();
			String warehouseId=object[4].toString();
			Integer  newQuantity   = Integer.parseInt(object[5].toString());
			Integer  oldQuantity   = Integer.parseInt(object[6].toString());
			Integer  brokenQuantity   = Integer.parseInt(object[7].toString());
			Integer  renewQuantity   = Integer.parseInt(object[8].toString());
			Integer  offlineQuantity   = Integer.parseInt(object[9].toString());
			String key=productName+","+country1+","+color+","+warehouseId;
			if(producSkuMap.get(key)!=null){
				inMap = producSkuMap.get(key);
			}
			String value=newQuantity+","+oldQuantity+","+brokenQuantity+","+renewQuantity+","+offlineQuantity;
			if(inMap.get(sku)!=null){
				String skuValue=inMap.get(sku);
				Integer skuNewQuantity =Integer.parseInt(skuValue.split(",")[0]);
				Integer skuOldQuantity =Integer.parseInt(skuValue.split(",")[1]);
				Integer skuBrokenQuantity =Integer.parseInt(skuValue.split(",")[2]);
				Integer skuRenewQuantity =Integer.parseInt(skuValue.split(",")[3]);
				Integer skuOfflineQuantity =Integer.parseInt(skuValue.split(",")[4]);
				value=newQuantity+skuNewQuantity+","+oldQuantity+skuOldQuantity+","+brokenQuantity+skuBrokenQuantity+","+renewQuantity+skuRenewQuantity+","+offlineQuantity+skuOfflineQuantity;
			}
			inMap.put(sku, value);
			producSkuMap.put(key, inMap);
		}
		
		for(Map.Entry<String,Map<String,String>> entry1:producSkuMap.entrySet()){
			String key=entry1.getKey();
			List<PsiInventoryDtoByInStockWithWarehouseCode> psiInventoryDtos = Lists.newArrayList();
			String[] arr=key.split(",");
			String productName1 = arr[0].toString();
			String country1 = arr[1].toString();
			String color1 = "";
			if(arr.length>2){
				color1=arr[2];
			}
			Integer warehouseId=Integer.parseInt(arr[3].toString());
			String warehouseCode=stockMap.get(warehouseId);
			
			String productColorKey=productName1+","+color1;
			Map<String,String> inSkuMap = producSkuMap.get(key);
			Map<String,Integer> skuNewMap    =Maps.newHashMap(); 
			Map<String,Integer> skuOldMap    =Maps.newHashMap(); 
			Map<String,Integer> skuBrokenMap =Maps.newHashMap();
			Map<String,Integer> skuRenewMap  =Maps.newHashMap();
			Map<String,Integer> skuOfflineMap  =Maps.newHashMap();
			for(Map.Entry<String,String> entry:inSkuMap.entrySet()){
				String sku = entry.getKey();
				String value=entry.getValue();
				Integer newQuantity =Integer.parseInt(value.split(",")[0]);
				Integer oldQuantity =Integer.parseInt(value.split(",")[1]);
				Integer brokenQuantity =Integer.parseInt(value.split(",")[2]);
				Integer renewQuantity =Integer.parseInt(value.split(",")[3]);
				Integer offlineQuantity =Integer.parseInt(value.split(",")[4]);
				if(skuNewMap.get(sku)!=null){
					newQuantity+=skuNewMap.get(sku);
					oldQuantity+=skuOldMap.get(sku);
					brokenQuantity+=skuBrokenMap.get(sku);
					renewQuantity+=skuRenewMap.get(sku);
					offlineQuantity+=skuOfflineMap.get(sku);
				}
				skuNewMap.put(sku, newQuantity);
				skuOldMap.put(sku, oldQuantity);
				skuBrokenMap.put(sku, brokenQuantity);
				skuRenewMap.put(sku, renewQuantity);
				skuOfflineMap.put(sku, offlineQuantity);
			}
			
			
			if(innerDtoMap.get(productColorKey)!=null){
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			
			psiInventoryDtos.add(new PsiInventoryDtoByInStockWithWarehouseCode(productName1, country1, color1,skuNewMap,skuOldMap,skuBrokenMap,skuRenewMap,skuOfflineMap,warehouseCode));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
		}
		
		PsiInventoryTotalDtoByInStock inventoryTotalDtoByInStock = null;
		for(Map.Entry<String, List<PsiInventoryDtoByInStockWithWarehouseCode>> entry:innerDtoMap.entrySet()){
			String productColorKey =entry.getKey();
			String[] proArr = productColorKey.split(",");
			String color2 = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			
			Map<String,PsiInventoryDtoByInStock> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDtoByInStockWithWarehouseCode houseDto:entry.getValue()){
				String houseCode=houseDto.getWarehouseCode();
				String counrty = houseDto.getCountry();
				PsiInventoryDtoByInStock temp = countryDtoMap.get(counrty);
				if(temp==null){
					Map<String,PsiInventoryDtoByInStockWithWarehouseCode> houseMap=Maps.newHashMap();
					houseMap.put(houseCode, houseDto);
					countryDtoMap.put(houseDto.getCountry(), new PsiInventoryDtoByInStock(houseDto.getProductName(), houseDto.getCountry(), color2, houseMap));
				}else{
					PsiInventoryDtoByInStockWithWarehouseCode dto=temp.getQuantityInventory().get(houseCode);
					if(dto==null){
						temp.getQuantityInventory().put(houseCode, houseDto);
					}else{
						Map<String,Integer> skuNewMap=houseDto.getSkusNewQuantity(); 
						Map<String,Integer> skuOldMap=houseDto.getSkusOldQuantity(); 
						Map<String,Integer> skuBrokenMap =houseDto.getSkusBrokenQuantity();
						Map<String,Integer> skuRenewMap  =houseDto.getSkusRenewQuantity();
						Map<String,Integer> skuOfflineMap  =houseDto.getSkusOfflineQuantity();
						if(dto.getSkusNewQuantity()!=null&&dto.getSkusNewQuantity().size()>0){
							for (Map.Entry<String, Integer> entryNew: dto.getSkusNewQuantity().entrySet()) {
								String sku = entryNew.getKey();
								skuNewMap.put(sku,entryNew.getValue()+(skuNewMap.get(sku)==null?0:skuNewMap.get(sku)));
							}
						}
						if(dto.getSkusOldQuantity()!=null&&dto.getSkusOldQuantity().size()>0){
							for (Map.Entry<String, Integer> entryOld: dto.getSkusOldQuantity().entrySet()) {
								String sku = entryOld.getKey();
								skuOldMap.put(sku,entryOld.getValue()+(skuOldMap.get(sku)==null?0:skuOldMap.get(sku)));
							}
						}
						if(dto.getSkusBrokenQuantity()!=null&&dto.getSkusBrokenQuantity().size()>0){
							for (Map.Entry<String, Integer> entryBroken: dto.getSkusBrokenQuantity().entrySet()) {
								String sku = entryBroken.getKey();
								skuBrokenMap.put(sku,entryBroken.getValue()+(skuBrokenMap.get(sku)==null?0:skuBrokenMap.get(sku)));
							}
						}
						if(dto.getSkusRenewQuantity()!=null&&dto.getSkusRenewQuantity().size()>0){
							for (Map.Entry<String, Integer> entryRenew: dto.getSkusRenewQuantity().entrySet()) {
								String sku = entryRenew.getKey();
								skuRenewMap.put(sku,entryRenew.getValue()+(skuRenewMap.get(sku)==null?0:skuRenewMap.get(sku)));
							}
						}
						if(dto.getSkusOfflineQuantity()!=null&&dto.getSkusOfflineQuantity().size()>0){
							for (Map.Entry<String, Integer> entryOffline: dto.getSkusOfflineQuantity().entrySet()) {
								String sku = entryOffline.getKey();
								skuOfflineMap.put(sku,entryOffline.getValue()+(skuOfflineMap.get(sku)==null?0:skuOfflineMap.get(sku)));
							}
						}
						
						temp.getQuantityInventory().put(houseCode, houseDto);
					}
				}
				
			}
			inventoryTotalDtoByInStock=new PsiInventoryTotalDtoByInStock(proArr[0], color2, countryDtoMap);
		}
		
		
		return inventoryTotalDtoByInStock;
	}
	
	
	/**
	 * 查询(欧洲)所有产品(在库)信息
	 */
	
	public Map<String,PsiInventoryTotalDtoByInStock> getInventoryQuantityEu(Integer warehouseId){
		if(warehouseId==null){
			return null;
		}
		//获取仓库国家简码
		Map<Integer,String> stockMap=this.stockService.findSelfStockCountryCode();
		String houseCode=stockMap.get(warehouseId);
		String	sql ="SELECT a.`product_name`,a.`country_code`,a.`color_code`,a.sku,a.`new_quantity`,a.`old_quantity`,a.`broken_quantity`,a.`renew_quantity`,a.`offline_quantity` FROM psi_inventory AS a where a.`country_code` in :p1 AND a.warehouse_id=:p2 ";
		List<Object[]>	objects=this.psiInventoryDao.findBySql(sql,new Parameter(Lists.newArrayList("de","fr","it","es","uk"),warehouseId)); 
		Map<String,PsiInventoryTotalDtoByInStock> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDtoByInStockWithWarehouseCode>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, String>> producSkuMap =Maps.newHashMap();
		
		for(Object[] object:objects){
			Map<String,String>  inMap = Maps.newHashMap();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			String sku     = object[3].toString();
			Integer  newQuantity   = Integer.parseInt(object[4].toString());
			Integer  oldQuantity   = Integer.parseInt(object[5].toString());
			Integer  brokenQuantity   = Integer.parseInt(object[6].toString());
			Integer  renewQuantity   = Integer.parseInt(object[7].toString());
			Integer  offlineQuantity   = Integer.parseInt(object[8].toString());
			String key=productName+","+country+","+color;
			if(producSkuMap.get(key)!=null){
				inMap = producSkuMap.get(key);
			}
			String value=newQuantity+","+oldQuantity+","+brokenQuantity+","+renewQuantity+","+offlineQuantity;
			if(inMap.get(sku)!=null){
				String skuValue=inMap.get(sku);
				Integer skuNewQuantity =Integer.parseInt(skuValue.split(",")[0]);
				Integer skuOldQuantity =Integer.parseInt(skuValue.split(",")[1]);
				Integer skuBrokenQuantity =Integer.parseInt(skuValue.split(",")[2]);
				Integer skuRenewQuantity =Integer.parseInt(skuValue.split(",")[3]);
				Integer skuOfflineQuantity =Integer.parseInt(skuValue.split(",")[4]);
				value=newQuantity+skuNewQuantity+","+oldQuantity+skuOldQuantity+","+brokenQuantity+skuBrokenQuantity+","+renewQuantity+skuRenewQuantity+","+offlineQuantity+skuOfflineQuantity;
			}
			inMap.put(sku, value);
			producSkuMap.put(key, inMap);
		}
		
		for(Map.Entry<String,Map<String,String>> entry1:producSkuMap.entrySet()){
			String key=entry1.getKey();
			List<PsiInventoryDtoByInStockWithWarehouseCode> psiInventoryDtos = Lists.newArrayList();
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			String productColorKey=productName+","+color;
			
			Map<String,String> skuMap = producSkuMap.get(key);
			Map<String,Integer> skuNewMap    =Maps.newHashMap(); 
			Map<String,Integer> skuOldMap    =Maps.newHashMap(); 
			Map<String,Integer> skuBrokenMap =Maps.newHashMap();
			Map<String,Integer> skuRenewMap  =Maps.newHashMap();
			Map<String,Integer> skuOfflineMap  =Maps.newHashMap();
			for(Map.Entry<String,String> entry:skuMap.entrySet()){
				String sku = entry.getKey();
				String value=entry.getValue();
				Integer newQuantity =Integer.parseInt(value.split(",")[0]);
				Integer oldQuantity =Integer.parseInt(value.split(",")[1]);
				Integer brokenQuantity =Integer.parseInt(value.split(",")[2]);
				Integer renewQuantity =Integer.parseInt(value.split(",")[3]);
				Integer offlineQuantity =Integer.parseInt(value.split(",")[4]);
				if(skuNewMap.get(sku)!=null){
					newQuantity+=skuNewMap.get(sku);
					oldQuantity+=skuOldMap.get(sku);
					brokenQuantity+=skuBrokenMap.get(sku);
					renewQuantity+=skuRenewMap.get(sku);
					offlineQuantity+=skuOfflineMap.get(sku);
				}
				skuNewMap.put(sku, newQuantity);
				skuOldMap.put(sku, oldQuantity);
				skuBrokenMap.put(sku, brokenQuantity);
				skuRenewMap.put(sku, renewQuantity);
				skuOfflineMap.put(sku, offlineQuantity);
			}
			
			
			if(innerDtoMap.get(productColorKey)!=null){
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			
			psiInventoryDtos.add(new PsiInventoryDtoByInStockWithWarehouseCode(productName, country, color,skuNewMap,skuOldMap,skuBrokenMap,skuRenewMap,skuOfflineMap,houseCode));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
		    }
		
		for(Map.Entry<String, List<PsiInventoryDtoByInStockWithWarehouseCode>> entry:innerDtoMap.entrySet()){
			String productColorKey =entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			Map<String,PsiInventoryDtoByInStock> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDtoByInStockWithWarehouseCode houseDto:entry.getValue()){
				String counrty = houseDto.getCountry();
				PsiInventoryDtoByInStock temp = countryDtoMap.get(counrty);
				if(temp==null){
					Map<String,PsiInventoryDtoByInStockWithWarehouseCode> houseMap=Maps.newHashMap();
					houseMap.put(houseCode, houseDto);
					countryDtoMap.put(houseDto.getCountry(), new PsiInventoryDtoByInStock(houseDto.getProductName(), houseDto.getCountry(), color, houseMap));
				}else{
					temp.getQuantityInventory().put(houseCode, houseDto);
				}
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDtoByInStock(proArr[0], color, countryDtoMap));
		}
		
		return psiInventoryTotalDtos;
	}
	
	/**
	 * 查询(欧洲)所有产品(在库)信息
	 */
	
	public Map<String,PsiInventoryTotalDtoByInStock> getInventoryQuantityEu(){
		//获取仓库国家简码
		Map<Integer,String> stockMap=this.stockService.findSelfStockCountryCode();
		String	sql ="SELECT a.`product_name`,a.`country_code`,a.`color_code`,a.sku,a.warehouse_id,a.`new_quantity`,a.`old_quantity`,a.`broken_quantity`,a.`renew_quantity`,a.`offline_quantity` FROM psi_inventory AS a where a.`country_code` in :p1 ";
		List<Object[]>	objects=this.psiInventoryDao.findBySql(sql,new Parameter(Lists.newArrayList("de","fr","it","es","uk"))); 
		Map<String,PsiInventoryTotalDtoByInStock> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDtoByInStockWithWarehouseCode>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, String>> producSkuMap =Maps.newHashMap();
		
		for(Object[] object:objects){
			Map<String,String>  inMap = Maps.newHashMap();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			String sku     = object[3].toString();
			String warehouseId=object[4].toString();
			Integer  newQuantity   = Integer.parseInt(object[5].toString());
			Integer  oldQuantity   = Integer.parseInt(object[6].toString());
			Integer  brokenQuantity   = Integer.parseInt(object[7].toString());
			Integer  renewQuantity   = Integer.parseInt(object[8].toString());
			Integer  offlineQuantity   = Integer.parseInt(object[9].toString());
			String key=productName+","+country+","+color+","+warehouseId;
			if(producSkuMap.get(key)!=null){
				inMap = producSkuMap.get(key);
			}
			String value=newQuantity+","+oldQuantity+","+brokenQuantity+","+renewQuantity+","+offlineQuantity;
			if(inMap.get(sku)!=null){
				String skuValue=inMap.get(sku);
				Integer skuNewQuantity =Integer.parseInt(skuValue.split(",")[0]);
				Integer skuOldQuantity =Integer.parseInt(skuValue.split(",")[1]);
				Integer skuBrokenQuantity =Integer.parseInt(skuValue.split(",")[2]);
				Integer skuRenewQuantity =Integer.parseInt(skuValue.split(",")[3]);
				Integer skuOfflineQuantity =Integer.parseInt(skuValue.split(",")[4]);
				value=newQuantity+skuNewQuantity+","+oldQuantity+skuOldQuantity+","+brokenQuantity+skuBrokenQuantity+","+renewQuantity+skuRenewQuantity+","+offlineQuantity+skuOfflineQuantity;
			}
			inMap.put(sku, value);
			producSkuMap.put(key, inMap);
		}
		
		for(Map.Entry<String,Map<String,String>> entry1:producSkuMap.entrySet()){
			String key=entry1.getKey();
			List<PsiInventoryDtoByInStockWithWarehouseCode> psiInventoryDtos = Lists.newArrayList();
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			Integer warehouseId=Integer.parseInt(arr[3].toString());
			String warehouseCode=stockMap.get(warehouseId);
			String productColorKey=productName+","+color;
			
			Map<String,String> skuMap = producSkuMap.get(key);
			Map<String,Integer> skuNewMap    =Maps.newHashMap(); 
			Map<String,Integer> skuOldMap    =Maps.newHashMap(); 
			Map<String,Integer> skuBrokenMap =Maps.newHashMap();
			Map<String,Integer> skuRenewMap  =Maps.newHashMap();
			Map<String,Integer> skuOfflineMap  =Maps.newHashMap();
			for(Map.Entry<String,String> entry:skuMap.entrySet()){
				String sku = entry.getKey();
				String value=entry.getValue();
				Integer newQuantity =Integer.parseInt(value.split(",")[0]);
				Integer oldQuantity =Integer.parseInt(value.split(",")[1]);
				Integer brokenQuantity =Integer.parseInt(value.split(",")[2]);
				Integer renewQuantity =Integer.parseInt(value.split(",")[3]);
				Integer offlineQuantity =Integer.parseInt(value.split(",")[4]);
				if(skuNewMap.get(sku)!=null){
					newQuantity+=skuNewMap.get(sku);
					oldQuantity+=skuOldMap.get(sku);
					brokenQuantity+=skuBrokenMap.get(sku);
					renewQuantity+=skuRenewMap.get(sku);
					offlineQuantity+=skuOfflineMap.get(sku);
				}
				skuNewMap.put(sku, newQuantity);
				skuOldMap.put(sku, oldQuantity);
				skuBrokenMap.put(sku, brokenQuantity);
				skuRenewMap.put(sku, renewQuantity);
				skuOfflineMap.put(sku, offlineQuantity);
			}
			
			
			if(innerDtoMap.get(productColorKey)!=null){
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			
			psiInventoryDtos.add(new PsiInventoryDtoByInStockWithWarehouseCode(productName, country, color,skuNewMap,skuOldMap,skuBrokenMap,skuRenewMap,skuOfflineMap,warehouseCode));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
		    }
		
		for(Map.Entry<String, List<PsiInventoryDtoByInStockWithWarehouseCode>> entry:innerDtoMap.entrySet()){
			String productColorKey =entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			Map<String,PsiInventoryDtoByInStock> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDtoByInStockWithWarehouseCode houseDto:entry.getValue()){
				String houseCode=houseDto.getWarehouseCode();
				String counrty = houseDto.getCountry();
				PsiInventoryDtoByInStock temp = countryDtoMap.get(counrty);
				if(temp==null){
					Map<String,PsiInventoryDtoByInStockWithWarehouseCode> houseMap=Maps.newHashMap();
					houseMap.put(houseCode, houseDto);
					countryDtoMap.put(houseDto.getCountry(), new PsiInventoryDtoByInStock(houseDto.getProductName(), houseDto.getCountry(), color, houseMap));
				}else{
					//temp.getQuantityInventory().put(houseCode, houseDto);

					PsiInventoryDtoByInStockWithWarehouseCode dto=temp.getQuantityInventory().get(houseCode);
					if(dto==null){
						temp.getQuantityInventory().put(houseCode, houseDto);
					}else{
						Map<String,Integer> skuNewMap=houseDto.getSkusNewQuantity(); 
						Map<String,Integer> skuOldMap=houseDto.getSkusOldQuantity(); 
						Map<String,Integer> skuBrokenMap =houseDto.getSkusBrokenQuantity();
						Map<String,Integer> skuRenewMap  =houseDto.getSkusRenewQuantity();
						Map<String,Integer> skuOfflineMap  =houseDto.getSkusOfflineQuantity();
						if(dto.getSkusNewQuantity()!=null&&dto.getSkusNewQuantity().size()>0){
							for (Map.Entry<String, Integer> entryNew: dto.getSkusNewQuantity().entrySet()) {
								String sku = entryNew.getKey();
								skuNewMap.put(sku,entryNew.getValue()+(skuNewMap.get(sku)==null?0:skuNewMap.get(sku)));
							}
						}
						if(dto.getSkusOldQuantity()!=null&&dto.getSkusOldQuantity().size()>0){
							for (Map.Entry<String, Integer> entryOld: dto.getSkusOldQuantity().entrySet()) {
								String sku = entryOld.getKey();
								skuOldMap.put(sku,entryOld.getValue()+(skuOldMap.get(sku)==null?0:skuOldMap.get(sku)));
							}
						}
						if(dto.getSkusBrokenQuantity()!=null&&dto.getSkusBrokenQuantity().size()>0){
							for (Map.Entry<String, Integer> entryBroken: dto.getSkusBrokenQuantity().entrySet()) {
								String sku = entryBroken.getKey();
								skuBrokenMap.put(sku,entryBroken.getValue()+(skuBrokenMap.get(sku)==null?0:skuBrokenMap.get(sku)));
							}
						}
						if(dto.getSkusRenewQuantity()!=null&&dto.getSkusRenewQuantity().size()>0){
							for (Map.Entry<String, Integer> entryRenew: dto.getSkusRenewQuantity().entrySet()) {
								String sku = entryRenew.getKey();
								skuRenewMap.put(sku,entryRenew.getValue()+(skuRenewMap.get(sku)==null?0:skuRenewMap.get(sku)));
							}
						}
						if(dto.getSkusOfflineQuantity()!=null&&dto.getSkusOfflineQuantity().size()>0){
							for (Map.Entry<String, Integer> entryOffline: dto.getSkusOfflineQuantity().entrySet()) {
								String sku = entryOffline.getKey();
								skuOfflineMap.put(sku,entryOffline.getValue()+(skuOfflineMap.get(sku)==null?0:skuOfflineMap.get(sku)));
							}
						}
						
						temp.getQuantityInventory().put(houseCode, houseDto);
					}
				}
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDtoByInStock(proArr[0], color, countryDtoMap));
			
		}
		return psiInventoryTotalDtos;
	}
	
	public Map<String,PsiInventoryTotalDtoByInStock> getInventoryQuantity(String countryFlag){
		//获取仓库国家简码
		Map<Integer,String> stockMap=this.stockService.findSelfStockCountryCode();
		String	sql ="SELECT a.`product_name`,a.`country_code`,a.`color_code`,a.sku,a.warehouse_id,a.`new_quantity`,a.`old_quantity`,a.`broken_quantity`,a.`renew_quantity`,a.`offline_quantity` FROM psi_inventory AS a where a.`country_code` = :p1 ";
		List<Object[]>	objects=this.psiInventoryDao.findBySql(sql,new Parameter(countryFlag)); 
		Map<String,PsiInventoryTotalDtoByInStock> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDtoByInStockWithWarehouseCode>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, String>> producSkuMap =Maps.newHashMap();
		
		for(Object[] object:objects){
			Map<String,String>  inMap = Maps.newHashMap();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			String sku     = object[3].toString();
			String warehouseId=object[4].toString();
			Integer  newQuantity   = Integer.parseInt(object[5].toString());
			Integer  oldQuantity   = Integer.parseInt(object[6].toString());
			Integer  brokenQuantity   = Integer.parseInt(object[7].toString());
			Integer  renewQuantity   = Integer.parseInt(object[8].toString());
			Integer  offlineQuantity   = Integer.parseInt(object[9].toString());
			String key=productName+","+country+","+color+","+warehouseId;
			if(producSkuMap.get(key)!=null){
				inMap = producSkuMap.get(key);
			}
			String value=newQuantity+","+oldQuantity+","+brokenQuantity+","+renewQuantity+","+offlineQuantity;
			if(inMap.get(sku)!=null){
				String skuValue=inMap.get(sku);
				Integer skuNewQuantity =Integer.parseInt(skuValue.split(",")[0]);
				Integer skuOldQuantity =Integer.parseInt(skuValue.split(",")[1]);
				Integer skuBrokenQuantity =Integer.parseInt(skuValue.split(",")[2]);
				Integer skuRenewQuantity =Integer.parseInt(skuValue.split(",")[3]);
				Integer skuOfflineQuantity =Integer.parseInt(skuValue.split(",")[4]);
				value=newQuantity+skuNewQuantity+","+oldQuantity+skuOldQuantity+","+brokenQuantity+skuBrokenQuantity+","+renewQuantity+skuRenewQuantity+","+offlineQuantity+skuOfflineQuantity;
			}
			inMap.put(sku, value);
			producSkuMap.put(key, inMap);
		}
		
		for(Map.Entry<String, Map<String, String>> entry1:producSkuMap.entrySet()){
			String key = entry1.getKey();
			List<PsiInventoryDtoByInStockWithWarehouseCode> psiInventoryDtos = Lists.newArrayList();
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			Integer warehouseId=Integer.parseInt(arr[3].toString());
			String warehouseCode=stockMap.get(warehouseId);
			String productColorKey=productName+","+color;
			
			Map<String,String> skuMap = producSkuMap.get(key);
			Map<String,Integer> skuNewMap    =Maps.newHashMap(); 
			Map<String,Integer> skuOldMap    =Maps.newHashMap(); 
			Map<String,Integer> skuBrokenMap =Maps.newHashMap();
			Map<String,Integer> skuRenewMap  =Maps.newHashMap();
			Map<String,Integer> skuOfflineMap  =Maps.newHashMap();
			for(Map.Entry<String,String> entry:skuMap.entrySet()){
				String sku = entry.getKey();
				String value=entry.getValue();
				Integer newQuantity =Integer.parseInt(value.split(",")[0]);
				Integer oldQuantity =Integer.parseInt(value.split(",")[1]);
				Integer brokenQuantity =Integer.parseInt(value.split(",")[2]);
				Integer renewQuantity =Integer.parseInt(value.split(",")[3]);
				Integer offlineQuantity =Integer.parseInt(value.split(",")[4]);
				if(skuNewMap.get(sku)!=null){
					newQuantity+=skuNewMap.get(sku);
					oldQuantity+=skuOldMap.get(sku);
					brokenQuantity+=skuBrokenMap.get(sku);
					renewQuantity+=skuRenewMap.get(sku);
					offlineQuantity+=skuOfflineMap.get(sku);
				}
				skuNewMap.put(sku, newQuantity);
				skuOldMap.put(sku, oldQuantity);
				skuBrokenMap.put(sku, brokenQuantity);
				skuRenewMap.put(sku, renewQuantity);
				skuOfflineMap.put(sku, offlineQuantity);
			}
			
			
			if(innerDtoMap.get(productColorKey)!=null){
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			
			psiInventoryDtos.add(new PsiInventoryDtoByInStockWithWarehouseCode(productName, country, color,skuNewMap,skuOldMap,skuBrokenMap,skuRenewMap,skuOfflineMap,warehouseCode));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
		    }
		
		for(Map.Entry<String, List<PsiInventoryDtoByInStockWithWarehouseCode>> entry:innerDtoMap.entrySet()){
			String productColorKey =entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			Map<String,PsiInventoryDtoByInStock> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDtoByInStockWithWarehouseCode houseDto:entry.getValue()){
				String houseCode=houseDto.getWarehouseCode();
				String counrty = houseDto.getCountry();
				PsiInventoryDtoByInStock temp = countryDtoMap.get(counrty);
				if(temp==null){
					Map<String,PsiInventoryDtoByInStockWithWarehouseCode> houseMap=Maps.newHashMap();
					houseMap.put(houseCode, houseDto);
					countryDtoMap.put(houseDto.getCountry(), new PsiInventoryDtoByInStock(houseDto.getProductName(), houseDto.getCountry(), color, houseMap));
				}else{
					//temp.getQuantityInventory().put(houseCode, houseDto);
					PsiInventoryDtoByInStockWithWarehouseCode dto=temp.getQuantityInventory().get(houseCode);
					if(dto==null){
						temp.getQuantityInventory().put(houseCode, houseDto);
					}else{
						Map<String,Integer> skuNewMap=houseDto.getSkusNewQuantity(); 
						Map<String,Integer> skuOldMap=houseDto.getSkusOldQuantity(); 
						Map<String,Integer> skuBrokenMap =houseDto.getSkusBrokenQuantity();
						Map<String,Integer> skuRenewMap  =houseDto.getSkusRenewQuantity();
						Map<String,Integer> skuOfflineMap  =houseDto.getSkusOfflineQuantity();
						if(dto.getSkusNewQuantity()!=null&&dto.getSkusNewQuantity().size()>0){
							for (Map.Entry<String, Integer> entryNew: dto.getSkusNewQuantity().entrySet()) {
								String sku = entryNew.getKey();
								skuNewMap.put(sku,entryNew.getValue()+(skuNewMap.get(sku)==null?0:skuNewMap.get(sku)));
							}
						}
						if(dto.getSkusOldQuantity()!=null&&dto.getSkusOldQuantity().size()>0){
							for (Map.Entry<String, Integer> entryOld: dto.getSkusOldQuantity().entrySet()) {
								String sku = entryOld.getKey();
								skuOldMap.put(sku,entryOld.getValue()+(skuOldMap.get(sku)==null?0:skuOldMap.get(sku)));
							}
						}
						if(dto.getSkusBrokenQuantity()!=null&&dto.getSkusBrokenQuantity().size()>0){
							for (Map.Entry<String, Integer> entryBroken: dto.getSkusBrokenQuantity().entrySet()) {
								String sku = entryBroken.getKey();
								skuBrokenMap.put(sku,entryBroken.getValue()+(skuBrokenMap.get(sku)==null?0:skuBrokenMap.get(sku)));
							}
						}
						if(dto.getSkusRenewQuantity()!=null&&dto.getSkusRenewQuantity().size()>0){
							for (Map.Entry<String, Integer> entryRenew: dto.getSkusRenewQuantity().entrySet()) {
								String sku = entryRenew.getKey();
								skuRenewMap.put(sku,entryRenew.getValue()+(skuRenewMap.get(sku)==null?0:skuRenewMap.get(sku)));
							}
						}
						if(dto.getSkusOfflineQuantity()!=null&&dto.getSkusOfflineQuantity().size()>0){
							for (Map.Entry<String, Integer> entryOffline: dto.getSkusOfflineQuantity().entrySet()) {
								String sku = entryOffline.getKey();
								skuOfflineMap.put(sku,entryOffline.getValue()+(skuOfflineMap.get(sku)==null?0:skuOfflineMap.get(sku)));
							}
						}
						
						temp.getQuantityInventory().put(houseCode, houseDto);
					}
				}
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDtoByInStock(proArr[0], color, countryDtoMap));
			
		}
		return psiInventoryTotalDtos;
	}
	/**
	 * 查询某天所有产品(在库)信息
	 */
	public Map<String,PsiInventoryTotalDtoByInStock> getInventoryQuantity(Date date,Integer warehouseId){
		//获取仓库国家简码
		Map<Integer,String> stockMap=this.stockService.findSelfStockCountryCode();
		String houseCode=stockMap.get(warehouseId);
		String sql ="SELECT a.`product_name`,a.`country_code`,a.`color_code`,a.`sku`,SUM(a.`new_quantity`),SUM(a.`old_quantity`),SUM(a.`broken_quantity`),SUM(a.`renew_quantity`),SUM(a.`offline_quantity`) FROM psi_inventory_revision_log AS a WHERE a.`data_type`='new'  AND a.`operatin_date` <=:p1 AND a.warehouse_id=:p2 GROUP BY a.`sku`";
		List<Object[]> objects=null;
		if(warehouseId==null){
			return null;
		}else{
			objects=this.psiInventoryDao.findBySql(sql,new Parameter(date,warehouseId)); 
		}
		
		Map<String,PsiInventoryTotalDtoByInStock> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDtoByInStockWithWarehouseCode>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, String>> producSkuMap =Maps.newHashMap();
		
		for(Object[] object:objects){
			Map<String,String>  inMap = Maps.newHashMap();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			String sku     = object[3].toString();
			Integer  newQuantity   = Integer.parseInt(object[4].toString());
			Integer  oldQuantity   = Integer.parseInt(object[5].toString());
			Integer  brokenQuantity   = Integer.parseInt(object[6].toString());
			Integer  renewQuantity   = Integer.parseInt(object[7].toString());
			Integer  offlineQuantity   = Integer.parseInt(object[8].toString());
			String key=productName+","+country+","+color;
			if(producSkuMap.get(key)!=null){
				inMap = producSkuMap.get(key);
			}
			String value=newQuantity+","+oldQuantity+","+brokenQuantity+","+renewQuantity+","+offlineQuantity;
			if(inMap.get(sku)!=null){
				String skuValue=inMap.get(sku);
				Integer skuNewQuantity =Integer.parseInt(skuValue.split(",")[0]);
				Integer skuOldQuantity =Integer.parseInt(skuValue.split(",")[1]);
				Integer skuBrokenQuantity =Integer.parseInt(skuValue.split(",")[2]);
				Integer skuRenewQuantity =Integer.parseInt(skuValue.split(",")[3]);
				Integer skuOfflineQuantity =Integer.parseInt(skuValue.split(",")[4]);
				value=newQuantity+skuNewQuantity+","+oldQuantity+skuOldQuantity+","+brokenQuantity+skuBrokenQuantity+","+renewQuantity+skuRenewQuantity+","+offlineQuantity+skuOfflineQuantity;
			}
			inMap.put(sku, value);
			producSkuMap.put(key, inMap);
		}
		
		for(Map.Entry<String, Map<String, String>> entry1:producSkuMap.entrySet()){
			String key = entry1.getKey();
			List<PsiInventoryDtoByInStockWithWarehouseCode> psiInventoryDtos = Lists.newArrayList();
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			String productColorKey=productName+","+color;
			if(innerDtoMap.get(productColorKey)!=null){
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			
			Map<String,String> skuMap = producSkuMap.get(key);
			Map<String,Integer> skuNewMap    =Maps.newHashMap(); 
			Map<String,Integer> skuOldMap    =Maps.newHashMap(); 
			Map<String,Integer> skuBrokenMap =Maps.newHashMap();
			Map<String,Integer> skuRenewMap  =Maps.newHashMap();
			Map<String,Integer> skuOfflineMap  =Maps.newHashMap();
			for(Map.Entry<String,String> entry:skuMap.entrySet()){
				String sku = entry.getKey();
				String value=entry.getValue();
				Integer newQuantity =Integer.parseInt(value.split(",")[0]);
				Integer oldQuantity =Integer.parseInt(value.split(",")[1]);
				Integer brokenQuantity =Integer.parseInt(value.split(",")[2]);
				Integer renewQuantity =Integer.parseInt(value.split(",")[3]);
				Integer offlineQuantity =Integer.parseInt(value.split(",")[4]);
				if(skuNewMap.get(sku)!=null){
					newQuantity+=skuNewMap.get(sku);
					oldQuantity+=skuOldMap.get(sku);
					brokenQuantity+=skuBrokenMap.get(sku);
					renewQuantity+=skuRenewMap.get(sku);
					offlineQuantity+=skuOfflineMap.get(sku);
				}
				skuNewMap.put(sku, newQuantity);
				skuOldMap.put(sku, oldQuantity);
				skuBrokenMap.put(sku, brokenQuantity);
				skuRenewMap.put(sku, renewQuantity);
			}
			psiInventoryDtos.add(new PsiInventoryDtoByInStockWithWarehouseCode(productName, country, color,skuNewMap,skuOldMap,skuBrokenMap,skuRenewMap,skuOfflineMap,houseCode));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
		    }
		
		for(Map.Entry<String, List<PsiInventoryDtoByInStockWithWarehouseCode>> entry:innerDtoMap.entrySet()){
			String productColorKey =entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			
			Map<String,PsiInventoryDtoByInStock> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDtoByInStockWithWarehouseCode houseDto:entry.getValue()){
				String counrty = houseDto.getCountry();
				PsiInventoryDtoByInStock temp = countryDtoMap.get(counrty);
				if(temp==null){
					Map<String,PsiInventoryDtoByInStockWithWarehouseCode> houseMap=Maps.newHashMap();
					houseMap.put(houseCode, houseDto);
					countryDtoMap.put(houseDto.getCountry(), new PsiInventoryDtoByInStock(houseDto.getProductName(), houseDto.getCountry(), color, houseMap));
				}else{
					temp.getQuantityInventory().put(houseCode, houseDto);
				}
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDtoByInStock(proArr[0], color, countryDtoMap));
		}
		return psiInventoryTotalDtos;
	}
	
	
	/**
	 * 查询某天所有产品(在库)信息
	 */
	public Map<String,PsiInventoryTotalDtoByInStock> getInventoryQuantity(Date date){
		//获取仓库国家简码
		Map<Integer,String> stockMap=this.stockService.findSelfStockCountryCode();
		String	sql ="SELECT a.`product_name`,a.`country_code`,a.`color_code`,a.`sku`,a.`warehouse_id`,SUM(a.`new_quantity`),SUM(a.`old_quantity`),SUM(a.`broken_quantity`),SUM(a.`renew_quantity`),SUM(a.`offline_quantity`) FROM psi_inventory_revision_log AS a WHERE a.`data_type`='new'  AND a.`operatin_date` <=:p1  GROUP BY a.`sku`";
		List<Object[]>	objects=this.psiInventoryDao.findBySql(sql,new Parameter(date)); 
		
		Map<String,PsiInventoryTotalDtoByInStock> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDtoByInStockWithWarehouseCode>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, String>> producSkuMap =Maps.newHashMap();
		
		for(Object[] object:objects){
			Map<String,String>  inMap = Maps.newHashMap();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			String sku     = object[3].toString();
			String warehouseId =object[4].toString();
			Integer  newQuantity   = Integer.parseInt(object[5].toString());
			Integer  oldQuantity   = Integer.parseInt(object[6].toString());
			Integer  brokenQuantity   = Integer.parseInt(object[7].toString());
			Integer  renewQuantity   = Integer.parseInt(object[8].toString());
			Integer  offlineQuantity   = Integer.parseInt(object[9].toString());
			String key=productName+","+country+","+color+","+warehouseId;
			if(producSkuMap.get(key)!=null){
				inMap = producSkuMap.get(key);
			}
			String value=newQuantity+","+oldQuantity+","+brokenQuantity+","+renewQuantity;
			if(inMap.get(sku)!=null){
				String skuValue=inMap.get(sku);
				Integer skuNewQuantity =Integer.parseInt(skuValue.split(",")[0]);
				Integer skuOldQuantity =Integer.parseInt(skuValue.split(",")[1]);
				Integer skuBrokenQuantity =Integer.parseInt(skuValue.split(",")[2]);
				Integer skuRenewQuantity =Integer.parseInt(skuValue.split(",")[3]);
				Integer skuOfflineQuantity =Integer.parseInt(skuValue.split(",")[4]);
				value=newQuantity+skuNewQuantity+","+oldQuantity+skuOldQuantity+","+brokenQuantity+skuBrokenQuantity+","+renewQuantity+skuRenewQuantity+","+offlineQuantity+skuOfflineQuantity;
			}
			inMap.put(sku, value);
			producSkuMap.put(key, inMap);
		}
		
		for(Map.Entry<String, Map<String, String>> entry1:producSkuMap.entrySet()){
			String key = entry1.getKey();
			List<PsiInventoryDtoByInStockWithWarehouseCode> psiInventoryDtos = Lists.newArrayList();
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			Integer warehouesId=Integer.parseInt(arr[3].toString());
			String  warehosueCode=stockMap.get(warehouesId);
			String productColorKey=productName+","+color;
			if(innerDtoMap.get(productColorKey)!=null){
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			
			Map<String,String> skuMap = producSkuMap.get(key);
			Map<String,Integer> skuNewMap    =Maps.newHashMap(); 
			Map<String,Integer> skuOldMap    =Maps.newHashMap(); 
			Map<String,Integer> skuBrokenMap =Maps.newHashMap();
			Map<String,Integer> skuRenewMap  =Maps.newHashMap();
			Map<String,Integer> skuOfflineMap  =Maps.newHashMap();
			for(Map.Entry<String,String> entry:skuMap.entrySet()){
				String sku = entry.getKey();
				String value=entry.getValue();
				Integer newQuantity =Integer.parseInt(value.split(",")[0]);
				Integer oldQuantity =Integer.parseInt(value.split(",")[1]);
				Integer brokenQuantity =Integer.parseInt(value.split(",")[2]);
				Integer renewQuantity =Integer.parseInt(value.split(",")[3]);
				Integer offlineQuantity =Integer.parseInt(value.split(",")[4]);
				if(skuNewMap.get(sku)!=null){
					newQuantity+=skuNewMap.get(sku);
					oldQuantity+=skuOldMap.get(sku);
					brokenQuantity+=skuBrokenMap.get(sku);
					renewQuantity+=skuRenewMap.get(sku);
					offlineQuantity+=skuOfflineMap.get(sku);
				}
				skuNewMap.put(sku, newQuantity);
				skuOldMap.put(sku, oldQuantity);
				skuBrokenMap.put(sku, brokenQuantity);
				skuRenewMap.put(sku, renewQuantity);
			}
			psiInventoryDtos.add(new PsiInventoryDtoByInStockWithWarehouseCode(productName, country, color,skuNewMap,skuOldMap,skuBrokenMap,skuRenewMap,skuOfflineMap,warehosueCode));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
		    }
		
		for(Map.Entry<String, List<PsiInventoryDtoByInStockWithWarehouseCode>> entry:innerDtoMap.entrySet()){
			String productColorKey =entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			Map<String,PsiInventoryDtoByInStock> countryDtoMap =Maps.newHashMap();
			
			
			for(PsiInventoryDtoByInStockWithWarehouseCode houseDto:entry.getValue()){
				String houseCode=houseDto.getWarehouseCode();
				String counrty = houseDto.getCountry();
				PsiInventoryDtoByInStock temp = countryDtoMap.get(counrty);
				if(temp==null){
					Map<String,PsiInventoryDtoByInStockWithWarehouseCode> houseMap=Maps.newHashMap();
					houseMap.put(houseCode, houseDto);
					countryDtoMap.put(houseDto.getCountry(), new PsiInventoryDtoByInStock(houseDto.getProductName(), houseDto.getCountry(), color, houseMap));
				}else{
					//temp.getQuantityInventory().put(houseCode, houseDto);
					PsiInventoryDtoByInStockWithWarehouseCode dto=temp.getQuantityInventory().get(houseCode);
					if(dto==null){
						temp.getQuantityInventory().put(houseCode, houseDto);
					}else{
						Map<String,Integer> skuNewMap=houseDto.getSkusNewQuantity(); 
						Map<String,Integer> skuOldMap=houseDto.getSkusOldQuantity(); 
						Map<String,Integer> skuBrokenMap =houseDto.getSkusBrokenQuantity();
						Map<String,Integer> skuRenewMap  =houseDto.getSkusRenewQuantity();
						Map<String,Integer> skuOfflineMap  =houseDto.getSkusOfflineQuantity();
						if(dto.getSkusNewQuantity()!=null&&dto.getSkusNewQuantity().size()>0){
							for (Map.Entry<String, Integer> entryNew: dto.getSkusNewQuantity().entrySet()) {
								String sku = entryNew.getKey();
								skuNewMap.put(sku,entryNew.getValue()+(skuNewMap.get(sku)==null?0:skuNewMap.get(sku)));
							}
						}
						if(dto.getSkusOldQuantity()!=null&&dto.getSkusOldQuantity().size()>0){
							for (Map.Entry<String, Integer> entryOld: dto.getSkusOldQuantity().entrySet()) {
								String sku = entryOld.getKey();
								skuOldMap.put(sku,entryOld.getValue()+(skuOldMap.get(sku)==null?0:skuOldMap.get(sku)));
							}
						}
						if(dto.getSkusBrokenQuantity()!=null&&dto.getSkusBrokenQuantity().size()>0){
							for (Map.Entry<String, Integer> entryBroken: dto.getSkusBrokenQuantity().entrySet()) {
								String sku = entryBroken.getKey();
								skuBrokenMap.put(sku,entryBroken.getValue()+(skuBrokenMap.get(sku)==null?0:skuBrokenMap.get(sku)));
							}
						}
						if(dto.getSkusRenewQuantity()!=null&&dto.getSkusRenewQuantity().size()>0){
							for (Map.Entry<String, Integer> entryRenew: dto.getSkusRenewQuantity().entrySet()) {
								String sku = entryRenew.getKey();
								skuRenewMap.put(sku,entryRenew.getValue()+(skuRenewMap.get(sku)==null?0:skuRenewMap.get(sku)));
							}
						}
						if(dto.getSkusOfflineQuantity()!=null&&dto.getSkusOfflineQuantity().size()>0){
							for (Map.Entry<String, Integer> entryOffline: dto.getSkusOfflineQuantity().entrySet()) {
								String sku = entryOffline.getKey();
								skuOfflineMap.put(sku,entryOffline.getValue()+(skuOfflineMap.get(sku)==null?0:skuOfflineMap.get(sku)));
							}
						}
						
						temp.getQuantityInventory().put(houseCode, houseDto);
					}
				}
			}
			
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDtoByInStock(proArr[0], color, countryDtoMap));
		}
		return psiInventoryTotalDtos;
	}
	
	/**
	 * 查询某天(欧洲)所有产品(在库)信息
	 */
	public Map<String,PsiInventoryTotalDtoByInStock> getInventoryQuantityEu(Date date,Integer warehouseId){
		//获取仓库国家简码
		Map<Integer,String> stockMap=this.stockService.findSelfStockCountryCode();
		String houseCode=stockMap.get(warehouseId);
		String	sql ="SELECT a.`product_name`,a.`country_code`,a.`color_code`,a.`sku`,SUM(a.`new_quantity`),SUM(a.`old_quantity`),SUM(a.`broken_quantity`),SUM(a.`renew_quantity`),SUM(a.`offline_quantity`) FROM psi_inventory_revision_log AS a WHERE a.`data_type`='new' AND a.`country_code` in :p2 AND a.`operatin_date` <=:p1 AND a.warehouse_id=:p3 GROUP BY a.`sku`";
		List<Object[]> objects=null;
		if(warehouseId==null){
			return null;
		}else{
			objects=this.psiInventoryDao.findBySql(sql,new Parameter(date,new String[]{"de","fr","it","es","uk"},warehouseId)); 
		}
		Map<String,PsiInventoryTotalDtoByInStock> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDtoByInStockWithWarehouseCode>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, String>> producSkuMap =Maps.newHashMap();
		
		for(Object[] object:objects){
			Map<String,String>  inMap = Maps.newHashMap();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			String sku     = object[3].toString();
			Integer  newQuantity   = Integer.parseInt(object[4].toString());
			Integer  oldQuantity   = Integer.parseInt(object[5].toString());
			Integer  brokenQuantity   = Integer.parseInt(object[6].toString());
			Integer  renewQuantity   = Integer.parseInt(object[7].toString());
			Integer  offlineQuantity   = Integer.parseInt(object[8].toString());
			String key=productName+","+country+","+color;
			if(producSkuMap.get(key)!=null){
				inMap = producSkuMap.get(key);
			}
			String value=newQuantity+","+oldQuantity+","+brokenQuantity+","+renewQuantity;
			if(inMap.get(sku)!=null){
				String skuValue=inMap.get(sku);
				Integer skuNewQuantity =Integer.parseInt(skuValue.split(",")[0]);
				Integer skuOldQuantity =Integer.parseInt(skuValue.split(",")[1]);
				Integer skuBrokenQuantity =Integer.parseInt(skuValue.split(",")[2]);
				Integer skuRenewQuantity =Integer.parseInt(skuValue.split(",")[3]);
				Integer skuOfflineQuantity =Integer.parseInt(skuValue.split(",")[4]);
				value=newQuantity+skuNewQuantity+","+oldQuantity+skuOldQuantity+","+brokenQuantity+skuBrokenQuantity+","+renewQuantity+skuRenewQuantity+","+offlineQuantity+skuOfflineQuantity;
			}
			inMap.put(sku, value);
			producSkuMap.put(key, inMap);
		}
		
		for(Map.Entry<String, Map<String, String>> entry1:producSkuMap.entrySet()){
			String key = entry1.getKey();
			List<PsiInventoryDtoByInStockWithWarehouseCode> psiInventoryDtos = Lists.newArrayList();
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			String productColorKey=productName+","+color;
			if(innerDtoMap.get(productColorKey)!=null){
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			
			Map<String,String> skuMap = producSkuMap.get(key);
			Map<String,Integer> skuNewMap    =Maps.newHashMap(); 
			Map<String,Integer> skuOldMap    =Maps.newHashMap(); 
			Map<String,Integer> skuBrokenMap =Maps.newHashMap();
			Map<String,Integer> skuRenewMap  =Maps.newHashMap();
			Map<String,Integer> skuOfflineMap  =Maps.newHashMap();
			for(Map.Entry<String,String> entry:skuMap.entrySet()){
				String sku = entry.getKey();
				String value=entry.getValue();
				Integer newQuantity =Integer.parseInt(value.split(",")[0]);
				Integer oldQuantity =Integer.parseInt(value.split(",")[1]);
				Integer brokenQuantity =Integer.parseInt(value.split(",")[2]);
				Integer renewQuantity =Integer.parseInt(value.split(",")[3]);
				Integer offlineQuantity =Integer.parseInt(value.split(",")[4]);
				if(skuNewMap.get(sku)!=null){
					newQuantity+=skuNewMap.get(sku);
					oldQuantity+=skuOldMap.get(sku);
					brokenQuantity+=skuBrokenMap.get(sku);
					renewQuantity+=skuRenewMap.get(sku);
					offlineQuantity+=skuOfflineMap.get(sku);
				}
				skuNewMap.put(sku, newQuantity);
				skuOldMap.put(sku, oldQuantity);
				skuBrokenMap.put(sku, brokenQuantity);
				skuRenewMap.put(sku, renewQuantity);
				skuOfflineMap.put(sku, offlineQuantity);
			}
			psiInventoryDtos.add(new PsiInventoryDtoByInStockWithWarehouseCode(productName, country, color,skuNewMap,skuOldMap,skuBrokenMap,skuRenewMap,skuOfflineMap,houseCode));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
		    }
		
		for(Map.Entry<String, List<PsiInventoryDtoByInStockWithWarehouseCode>> entry:innerDtoMap.entrySet()){
			String productColorKey =entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			Map<String,PsiInventoryDtoByInStock> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDtoByInStockWithWarehouseCode houseDto:entry.getValue()){
				String counrty = houseDto.getCountry();
				PsiInventoryDtoByInStock temp = countryDtoMap.get(counrty);
				if(temp==null){
					Map<String,PsiInventoryDtoByInStockWithWarehouseCode> houseMap=Maps.newHashMap();
					houseMap.put(houseCode, houseDto);
					countryDtoMap.put(houseDto.getCountry(), new PsiInventoryDtoByInStock(houseDto.getProductName(), houseDto.getCountry(), color, houseMap));
				}else{
					temp.getQuantityInventory().put(houseCode, houseDto);
				}
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDtoByInStock(proArr[0], color, countryDtoMap));
		}
		return psiInventoryTotalDtos;
	}
	
	/**
	 * 查询某天(欧洲)所有产品(在库)信息
	 */
	public Map<String,PsiInventoryTotalDtoByInStock> getInventoryQuantityEu(Date date){
		//获取仓库国家简码
		Map<Integer,String> stockMap=this.stockService.findSelfStockCountryCode();
		String sql ="SELECT a.`product_name`,a.`country_code`,a.`color_code`,a.`sku`,a.`warehouse_id`,SUM(a.`new_quantity`),SUM(a.`old_quantity`),SUM(a.`broken_quantity`),SUM(a.`renew_quantity`),SUM(a.`offline_quantity`) FROM psi_inventory_revision_log AS a WHERE a.`data_type`='new' AND a.`country_code` in :p2 AND a.`operatin_date` <=:p1 GROUP BY a.`sku`";
		List<Object[]>	objects=this.psiInventoryDao.findBySql(sql,new Parameter(date,new String[]{"de","fr","it","es","uk"})); 
		Map<String,PsiInventoryTotalDtoByInStock> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDtoByInStockWithWarehouseCode>> innerDtoMap = Maps.newHashMap();
		Map<String,Map<String, String>> producSkuMap =Maps.newHashMap();
		
		for(Object[] object:objects){
			Map<String,String>  inMap = Maps.newHashMap();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			String sku     = object[3].toString();
			String warehouseId=object[4].toString();
			Integer  newQuantity   = Integer.parseInt(object[5].toString());
			Integer  oldQuantity   = Integer.parseInt(object[6].toString());
			Integer  brokenQuantity   = Integer.parseInt(object[7].toString());
			Integer  renewQuantity   = Integer.parseInt(object[8].toString());
			Integer  offlineQuantity   = Integer.parseInt(object[9].toString());
			String key=productName+","+country+","+color+","+warehouseId;
			if(producSkuMap.get(key)!=null){
				inMap = producSkuMap.get(key);
			}
			String value=newQuantity+","+oldQuantity+","+brokenQuantity+","+renewQuantity;
			if(inMap.get(sku)!=null){
				String skuValue=inMap.get(sku);
				Integer skuNewQuantity =Integer.parseInt(skuValue.split(",")[0]);
				Integer skuOldQuantity =Integer.parseInt(skuValue.split(",")[1]);
				Integer skuBrokenQuantity =Integer.parseInt(skuValue.split(",")[2]);
				Integer skuRenewQuantity =Integer.parseInt(skuValue.split(",")[3]);
				Integer skuOfflineQuantity =Integer.parseInt(skuValue.split(",")[4]);
				value=newQuantity+skuNewQuantity+","+oldQuantity+skuOldQuantity+","+brokenQuantity+skuBrokenQuantity+","+renewQuantity+skuRenewQuantity+","+offlineQuantity+skuOfflineQuantity;
			}
			inMap.put(sku, value);
			producSkuMap.put(key, inMap);
		}
		
		for(Map.Entry<String, Map<String, String>> entry1:producSkuMap.entrySet()){
			String key = entry1.getKey();
			List<PsiInventoryDtoByInStockWithWarehouseCode> psiInventoryDtos = Lists.newArrayList();
			String[] arr=key.split(",");
			String productName = arr[0].toString();
			String country = arr[1].toString();
			String color = "";
			if(arr.length>2){
				color=arr[2];
			}
			Integer warehouseId=Integer.parseInt(arr[3].toString());
			String warehouesCode=stockMap.get(warehouseId);
			String productColorKey=productName+","+color;
			if(innerDtoMap.get(productColorKey)!=null){
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			
			Map<String,String> skuMap = producSkuMap.get(key);
			Map<String,Integer> skuNewMap    =Maps.newHashMap(); 
			Map<String,Integer> skuOldMap    =Maps.newHashMap(); 
			Map<String,Integer> skuBrokenMap =Maps.newHashMap();
			Map<String,Integer> skuRenewMap  =Maps.newHashMap();
			Map<String,Integer> skuOfflineMap  =Maps.newHashMap();
			for(Map.Entry<String,String> entry:skuMap.entrySet()){
				String sku = entry.getKey();
				String value=entry.getValue();
				Integer newQuantity =Integer.parseInt(value.split(",")[0]);
				Integer oldQuantity =Integer.parseInt(value.split(",")[1]);
				Integer brokenQuantity =Integer.parseInt(value.split(",")[2]);
				Integer renewQuantity =Integer.parseInt(value.split(",")[3]);
				Integer offlineQuantity =Integer.parseInt(value.split(",")[4]);
				if(skuNewMap.get(sku)!=null){
					newQuantity+=skuNewMap.get(sku);
					oldQuantity+=skuOldMap.get(sku);
					brokenQuantity+=skuBrokenMap.get(sku);
					renewQuantity+=skuRenewMap.get(sku);
					offlineQuantity+=skuOfflineMap.get(sku);
				}
				skuNewMap.put(sku, newQuantity);
				skuOldMap.put(sku, oldQuantity);
				skuBrokenMap.put(sku, brokenQuantity);
				skuRenewMap.put(sku, renewQuantity);
				skuOfflineMap.put(sku, offlineQuantity);
			}
			psiInventoryDtos.add(new PsiInventoryDtoByInStockWithWarehouseCode(productName, country, color,skuNewMap,skuOldMap,skuBrokenMap,skuRenewMap,skuOfflineMap,warehouesCode));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
		    }
		
		for(Map.Entry<String, List<PsiInventoryDtoByInStockWithWarehouseCode>> entry:innerDtoMap.entrySet()){
			String productColorKey =entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			Map<String,PsiInventoryDtoByInStock> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDtoByInStockWithWarehouseCode houseDto:entry.getValue()){
				String houseCode=houseDto.getWarehouseCode();
				String counrty = houseDto.getCountry();
				PsiInventoryDtoByInStock temp = countryDtoMap.get(counrty);
				if(temp==null){
					Map<String,PsiInventoryDtoByInStockWithWarehouseCode> houseMap=Maps.newHashMap();
					houseMap.put(houseCode, houseDto);
					countryDtoMap.put(houseDto.getCountry(), new PsiInventoryDtoByInStock(houseDto.getProductName(), houseDto.getCountry(), color, houseMap));
				}else{
					//temp.getQuantityInventory().put(houseCode, houseDto);
					PsiInventoryDtoByInStockWithWarehouseCode dto=temp.getQuantityInventory().get(houseCode);
					if(dto==null){
						temp.getQuantityInventory().put(houseCode, houseDto);
					}else{
						Map<String,Integer> skuNewMap=houseDto.getSkusNewQuantity(); 
						Map<String,Integer> skuOldMap=houseDto.getSkusOldQuantity(); 
						Map<String,Integer> skuBrokenMap =houseDto.getSkusBrokenQuantity();
						Map<String,Integer> skuRenewMap  =houseDto.getSkusRenewQuantity();
						Map<String,Integer> skuOfflineMap  =houseDto.getSkusOfflineQuantity();
						if(dto.getSkusNewQuantity()!=null&&dto.getSkusNewQuantity().size()>0){
							for (Map.Entry<String, Integer> entryNew: dto.getSkusNewQuantity().entrySet()) {
								String sku = entryNew.getKey();
								skuNewMap.put(sku,entryNew.getValue()+(skuNewMap.get(sku)==null?0:skuNewMap.get(sku)));
							}
						}
						if(dto.getSkusOldQuantity()!=null&&dto.getSkusOldQuantity().size()>0){
							for (Map.Entry<String, Integer> entryOld: dto.getSkusOldQuantity().entrySet()) {
								String sku = entryOld.getKey();
								skuOldMap.put(sku,entryOld.getValue()+(skuOldMap.get(sku)==null?0:skuOldMap.get(sku)));
							}
						}
						if(dto.getSkusBrokenQuantity()!=null&&dto.getSkusBrokenQuantity().size()>0){
							for (Map.Entry<String, Integer> entryBroken: dto.getSkusBrokenQuantity().entrySet()) {
								String sku = entryBroken.getKey();
								skuBrokenMap.put(sku,entryBroken.getValue()+(skuBrokenMap.get(sku)==null?0:skuBrokenMap.get(sku)));
							}
						}
						if(dto.getSkusRenewQuantity()!=null&&dto.getSkusRenewQuantity().size()>0){
							for (Map.Entry<String, Integer> entryRenew: dto.getSkusRenewQuantity().entrySet()) {
								String sku = entryRenew.getKey();
								skuRenewMap.put(sku,entryRenew.getValue()+(skuRenewMap.get(sku)==null?0:skuRenewMap.get(sku)));
							}
						}
						if(dto.getSkusOfflineQuantity()!=null&&dto.getSkusOfflineQuantity().size()>0){
							for (Map.Entry<String, Integer> entryOffline: dto.getSkusOfflineQuantity().entrySet()) {
								String sku = entryOffline.getKey();
								skuOfflineMap.put(sku,entryOffline.getValue()+(skuOfflineMap.get(sku)==null?0:skuOfflineMap.get(sku)));
							}
						}
						
						temp.getQuantityInventory().put(houseCode, houseDto);
					}
				}
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDtoByInStock(proArr[0], color, countryDtoMap));
		}
		return psiInventoryTotalDtos;
	}
	
	/**
	 * 查询某天单产品(在库)信息
	 */
	
	public PsiInventoryDtoByInStockWithWarehouseCode getInventoryQuantity(Date date,String productName,String color,String countryCode,Integer warehouseId){
		//获取仓库国家简码
				Map<Integer,String> stockMap=this.stockService.findSelfStockCountryCode();
		String	sql ="SELECT a.`sku`,SUM(a.`new_quantity`),SUM(a.`old_quantity`),SUM(a.`broken_quantity`),SUM(a.`renew_quantity`),SUM(a.`offline_quantity`) FROM psi_inventory_revision_log AS a WHERE a.`data_type`='new'  AND a.`operatin_date` <=:p1 AND a.`product_name`=:p2 AND a.`color_code`=:p3 AND a.`country_code` =:p4 AND a.warehouse_id=:p5 GROUP BY a.`sku`  ";
		List<Object[]> objects=null;
		if(warehouseId==null){
			return null;
		}else{
			 objects=this.psiInventoryDao.findBySql(sql,new Parameter(date,productName,color,countryCode,warehouseId)); 
		}
		
		Map<String,String>  inMap = Maps.newHashMap();
		for(Object[] object:objects){
			String   sku    	= object[0].toString();
			Integer  newQuantity   = Integer.parseInt(object[1].toString());
			Integer  oldQuantity   = Integer.parseInt(object[2].toString());
			Integer  brokenQuantity   = Integer.parseInt(object[3].toString());
			Integer  renewQuantity   = Integer.parseInt(object[4].toString());
			Integer  offlineQuantity   = Integer.parseInt(object[5].toString());
			String value=newQuantity+","+oldQuantity+","+brokenQuantity+","+renewQuantity+","+offlineQuantity;
			if(inMap.get(sku)!=null){
				String skuValue=inMap.get(sku);
				Integer skuNewQuantity =Integer.parseInt(skuValue.split(",")[0]);
				Integer skuOldQuantity =Integer.parseInt(skuValue.split(",")[1]);
				Integer skuBrokenQuantity =Integer.parseInt(skuValue.split(",")[2]);
				Integer skuRenewQuantity =Integer.parseInt(skuValue.split(",")[3]);
				Integer skuOfflineQuantity =Integer.parseInt(skuValue.split(",")[4]);
				value=newQuantity+skuNewQuantity+","+oldQuantity+skuOldQuantity+","+brokenQuantity+skuBrokenQuantity+","+renewQuantity+skuRenewQuantity+","+offlineQuantity+skuOfflineQuantity;
			}
			
			inMap.put(sku, value);
		}
		
		Map<String,Integer> skuNewMap    =Maps.newHashMap(); 
		Map<String,Integer> skuOldMap    =Maps.newHashMap(); 
		Map<String,Integer> skuBrokenMap =Maps.newHashMap();
		Map<String,Integer> skuRenewMap  =Maps.newHashMap();
		Map<String,Integer> skuOfflineMap  =Maps.newHashMap();
		for(Map.Entry<String,String> entry:inMap.entrySet()){
			String sku= entry.getKey();
			String value=entry.getValue();
			Integer newQuantity =Integer.parseInt(value.split(",")[0]);
			Integer oldQuantity =Integer.parseInt(value.split(",")[1]);
			Integer brokenQuantity =Integer.parseInt(value.split(",")[2]);
			Integer renewQuantity =Integer.parseInt(value.split(",")[3]);
			Integer offlineQuantity =Integer.parseInt(value.split(",")[4]);
			if(skuNewMap.get(sku)!=null){
				newQuantity+=skuNewMap.get(sku);
				oldQuantity+=skuOldMap.get(sku);
				brokenQuantity+=skuBrokenMap.get(sku);
				renewQuantity+=skuRenewMap.get(sku);
				offlineQuantity+=skuRenewMap.get(sku);
			}
			skuNewMap.put(sku, newQuantity);
			skuOldMap.put(sku, oldQuantity);
			skuBrokenMap.put(sku, brokenQuantity);
			skuRenewMap.put(sku, renewQuantity);
			skuOfflineMap.put(sku, offlineQuantity);
		}
	
		return new PsiInventoryDtoByInStockWithWarehouseCode(productName, countryCode, color, skuNewMap,skuOldMap,skuBrokenMap,skuRenewMap,skuOfflineMap,stockMap.get(warehouseId));
	}
	
	
	public List<Object[]> getAllProductSalesInfo(){
		String sql = "SELECT CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) AS productName,b.`country`,b.`date`,SUM(b.`sales_volume`),SUM(IFNULL(b.`real_order`,0)) FROM amazoninfo_sale_report b "+
				 " WHERE b.`sales`>b.`sales_volume` AND b.order_type='1' and NOT(b.`product_name` LIKE '%other%' or b.`product_name` LIKE '%Old%' ) AND b.`date`>DATE_ADD(NOW(),INTERVAL -161 DAY)   GROUP BY  productName,b.`date`,b.`country`  ORDER BY productName,b.`country`,b.`date` DESC ";
		List<Object[]> rs = psiInventoryDao.findBySql(sql);
		return rs;
			 /*  sql = "SELECT CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) AS productName,'de',b.`date`,SUM(b.`sales_volume`) FROM amazoninfo_sale_report b "+
				" WHERE  b.order_type='1' and b.`country` IN ('fr','de','es','it','uk') AND  NOT(b.`product_name` LIKE '%other%' or b.`product_name` LIKE '%Old%' ) AND b.`date`>DATE_ADD(NOW(),INTERVAL -161 DAY)   GROUP BY  productName,b.`date`  ORDER BY productName,b.`date` DESC";
		List<Object[]> eu = psiInventoryDao.findBySql(sql);
		List<Object> hasPower = saleReportService.hasPowerProducts();
		for (Iterator<Object[]> iterator = rs.iterator(); iterator.hasNext();) {
			Object[] objs = iterator.next();
			String name = objs[0].toString();
			String country = objs[1].toString();
			if(!hasPower.contains(name)&&("de").equals(country)){
				iterator.remove();
			}
		}
		for (Object[] objs : eu) {
			String name = objs[0].toString();
			if(!hasPower.contains(name)){
				rs.add(objs);
			}
		}
		return rs;*/
	}
	
	
	/**
	 * 最近一周的销量(排除大单,用于新品预测下单,新品下单按最近一周销量推算31日销)
	 * @return [productName_country(含eu)  sales_volume]
	 */
	public Map<String, Integer> findLastWeekSale(){
		Map<String, Integer> rs = Maps.newHashMap();
		String sql = "SELECT CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) AS productName,b.`country`,SUM(b.`sales_volume`),SUM(b.`real_order`-IFNULL(b.`max_order`,0)) "+
				" FROM amazoninfo_sale_report b  "+
				" WHERE b.`sales`>b.`sales_volume` AND b.`product_name` IS NOT NULL AND b.order_type='1' AND  b.`date`>DATE_ADD(NOW(),INTERVAL -9 DAY) AND  b.`date`<DATE_ADD(NOW(),INTERVAL -2 DAY) "+   
				" GROUP BY  productName,b.`country`  ORDER BY productName,b.`country` ";
		List<Object[]> list = psiInventoryDao.findBySql(sql);
		for (Object[] obj : list) {
			String productName = obj[0].toString();
			String country = obj[1].toString();
			Integer qty = Integer.parseInt(obj[3].toString());
			rs.put(productName+"_"+country, qty);
			if ("de,fr,uk,it,es".contains(country)) {
				String key = productName+"_eu";
				if (rs.get(key) == null) {
					rs.put(key, qty);
				} else {
					rs.put(key, rs.get(key) + qty);
				}
			}
		}
		return rs;
	}
	
	//欧洲产品销量统计,计算方差调用,b.`sales`>b.`sales_volume`条件剔除类似8.7事件大促销单
	public List<Object[]> getAllProductSalesInfoByEuro(){
		String sql = "SELECT CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) AS productName,b.`date`,SUM(b.`sales_volume`),SUM(IFNULL(b.`real_order`,0)) FROM amazoninfo_sale_report b "+
				" WHERE b.`sales`>b.`sales_volume` AND b.order_type='1' and b.`country` IN ('fr','de','es','it','uk') AND  NOT(b.`product_name` LIKE '%other%' or b.`product_name` LIKE '%Old%' ) AND b.`date`>DATE_ADD(NOW(),INTERVAL -161 DAY)   GROUP BY  productName,b.`date`  ORDER BY productName,b.`date` DESC";
		return  psiInventoryDao.findBySql(sql);
	}
	
	//欧洲uk以外国家产品销量统计,计算方差调用,b.`sales`>b.`sales_volume`条件剔除类似8.7事件大促销单
	public List<Object[]> getAllProductSalesInfoByEuroNoUk(){
		String sql = "SELECT CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) AS productName,b.`date`,SUM(b.`sales_volume`),SUM(IFNULL(b.`real_order`,0)) FROM amazoninfo_sale_report b "+
				" WHERE b.`sales`>b.`sales_volume` AND b.order_type='1' and b.`country` IN ('fr','de','es','it') AND  NOT(b.`product_name` LIKE '%other%' or b.`product_name` LIKE '%Old%' ) AND b.`date`>DATE_ADD(NOW(),INTERVAL -161 DAY) and b.`real_order` is not null  GROUP BY  productName,b.`date`  ORDER BY productName,b.`date` DESC";
		return  psiInventoryDao.findBySql(sql);
	}
	
	public Map<String,PsiInventoryFba>   getAllProductFbaInfo(){
		String sql = "SELECT MAX(data_date) FROM psi_inventory_fba";
		List<Object> date = psiInventoryDao.findBySql(sql);
		sql = "SELECT DISTINCT b.`sku`,CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END) AS productName,a.`country`,b.`fulfillable_quantity`,b.`reserved_quantity`,b.`transit_quantity`,b.orrect_quantity FROM psi_sku a ,psi_inventory_fba b" +
				" WHERE a.`sku` = b.`sku` AND a.`country` = b.`country` AND a.`del_flag` = '0' AND NOT(a.`product_name` LIKE '%other' or a.`product_name` LIKE '%Old%' ) AND b.`data_date` = :p1 ORDER BY a.`country`";
		List<Object[]> list = psiInventoryDao.findBySql(sql,new Parameter(date.get(0)));
		Map<String,PsiInventoryFba> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			//String sku = objs[0].toString();
			String productName = objs[1].toString(); 
			String country = objs[2].toString();
			int fba = (Integer)objs[3];
			int reserved = (Integer)objs[4];
			int transit = (Integer)objs[5];
			Integer orrectQuantity =   (Integer)objs[6]==null?0:(Integer)objs[6];
			String key = productName+"_"+country;
			PsiInventoryFba psiFba = rs.get(key);
			if(psiFba==null){
				rs.put(key, new PsiInventoryFba(null,null,null, fba, 0, reserved, 0, transit, 0, country, null,null,orrectQuantity));
			}else{
				psiFba.setFulfillableQuantity(psiFba.getFulfillableQuantity()+fba);
				psiFba.setReservedQuantity(psiFba.getReservedQuantity()+reserved);
				psiFba.setTransitQuantity(psiFba.getTransitQuantity()+transit);
				if(orrectQuantity!=null){
					psiFba.setOrrectQuantity(psiFba.getOrrectQuantity()==null?orrectQuantity:psiFba.getOrrectQuantity()+orrectQuantity);
				}
			}
			if("de,fr,it,es,uk".contains(country)){
				String euKey=productName+"_eu";
				PsiInventoryFba euPsiFba = rs.get(euKey);
				if(euPsiFba==null){
					rs.put(euKey, new PsiInventoryFba(null,null,null, fba, 0, reserved, 0, transit, 0, country, null,null,orrectQuantity));
				}else{
					euPsiFba.setFulfillableQuantity(euPsiFba.getFulfillableQuantity()+fba);
					euPsiFba.setReservedQuantity(euPsiFba.getReservedQuantity()+reserved);
					euPsiFba.setTransitQuantity(euPsiFba.getTransitQuantity()+transit);
					if(orrectQuantity!=null){
						euPsiFba.setOrrectQuantity(euPsiFba.getOrrectQuantity()==null?orrectQuantity:euPsiFba.getOrrectQuantity()+orrectQuantity);
					}
				}
			}
			if("de,fr,it,es".contains(country)){
				String euKey=productName+"_four";
				PsiInventoryFba euPsiFba = rs.get(euKey);
				if(euPsiFba==null){
					rs.put(euKey, new PsiInventoryFba(null,null,null, fba, 0, reserved, 0, transit, 0, country, null,null,orrectQuantity));
				}else{
					euPsiFba.setFulfillableQuantity(euPsiFba.getFulfillableQuantity()+fba);
					euPsiFba.setReservedQuantity(euPsiFba.getReservedQuantity()+reserved);
					euPsiFba.setTransitQuantity(euPsiFba.getTransitQuantity()+transit);
					if(orrectQuantity!=null){
						euPsiFba.setOrrectQuantity(euPsiFba.getOrrectQuantity()==null?orrectQuantity:euPsiFba.getOrrectQuantity()+orrectQuantity);
					}
				}
			}
			psiFba = rs.get(productName);
			if(psiFba==null){
				rs.put(productName, new PsiInventoryFba(null,null,null, fba, 0, reserved, 0, transit, 0, null, null,null,orrectQuantity));
			}else{
				psiFba.setFulfillableQuantity(psiFba.getFulfillableQuantity()+fba);
				psiFba.setReservedQuantity(psiFba.getReservedQuantity()+reserved);
				psiFba.setTransitQuantity(psiFba.getTransitQuantity()+transit);
				if(orrectQuantity!=null){
					psiFba.setOrrectQuantity((psiFba.getOrrectQuantity()==null?orrectQuantity:psiFba.getOrrectQuantity())+orrectQuantity);
				}
			}
		}
		return  rs;
	}
	
	public Map<String,PsiInventoryFba>  getProductFbaInfo(String productName){
		String sql = "SELECT MAX(data_date) FROM psi_inventory_fba";
		List<Object> date = psiInventoryDao.findBySql(sql);
		sql = "SELECT DISTINCT b.`sku`,CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END) AS productName,a.`country`,b.`fulfillable_quantity`,b.`reserved_quantity`,b.`transit_quantity`,b.orrect_quantity FROM psi_sku a ,psi_inventory_fba b" +
				" WHERE a.`sku` = b.`sku` AND a.`country` = b.`country` AND a.`del_flag` = '0' AND NOT(a.`product_name` LIKE '%other' or a.`product_name` LIKE '%Old%' ) AND b.`data_date` = :p1 and CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END)=:p2  ORDER BY a.`country`";
		List<Object[]> list = psiInventoryDao.findBySql(sql,new Parameter(date.get(0),productName));
		Map<String,PsiInventoryFba> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			//String sku = objs[0].toString();
			String country = objs[2].toString();
			int fba = (Integer)objs[3];
			int reserved = (Integer)objs[4];
			int transit = (Integer)objs[5];
			Integer orrectQuantity =   (Integer)objs[6]==null?0:(Integer)objs[6];
			String key = productName+"_"+country;
			PsiInventoryFba psiFba = rs.get(key);
			if(psiFba==null){
				rs.put(key, new PsiInventoryFba(null,null,null, fba, 0, reserved, 0, transit, 0, country, null,null,orrectQuantity));
			}else{
				psiFba.setFulfillableQuantity(psiFba.getFulfillableQuantity()+fba);
				psiFba.setReservedQuantity(psiFba.getReservedQuantity()+reserved);
				psiFba.setTransitQuantity(psiFba.getTransitQuantity()+transit);
				if(orrectQuantity!=null){
					psiFba.setOrrectQuantity(psiFba.getOrrectQuantity()==null?orrectQuantity:psiFba.getOrrectQuantity()+orrectQuantity);
				}
			}
			if("fr,uk,es,it,de".contains(country)){
				psiFba = rs.get(productName+"_eu");
				if(psiFba==null){
					rs.put(productName+"_eu", new PsiInventoryFba(null,null,null, fba, 0, reserved, 0, transit, 0, country, null,null,orrectQuantity));
				}else{
					psiFba.setFulfillableQuantity(psiFba.getFulfillableQuantity()+fba);
					psiFba.setReservedQuantity(psiFba.getReservedQuantity()+reserved);
					psiFba.setTransitQuantity(psiFba.getTransitQuantity()+transit);
					if(orrectQuantity!=null){
						psiFba.setOrrectQuantity(psiFba.getOrrectQuantity()==null?orrectQuantity:psiFba.getOrrectQuantity()+orrectQuantity);
					}
				}
			}
			
			psiFba = rs.get(productName);
			if(psiFba==null){
				rs.put(productName, new PsiInventoryFba(null,null,null, fba, 0, reserved, 0, transit, 0, country, null,null,orrectQuantity));
			}else{
				psiFba.setFulfillableQuantity(psiFba.getFulfillableQuantity()+fba);
				psiFba.setReservedQuantity(psiFba.getReservedQuantity()+reserved);
				psiFba.setTransitQuantity(psiFba.getTransitQuantity()+transit);
				if(orrectQuantity!=null){
					psiFba.setOrrectQuantity(psiFba.getOrrectQuantity()==null?orrectQuantity:psiFba.getOrrectQuantity()+orrectQuantity);
				}
			}
		}
		return  rs;
	}
	
	
	public Map<String,PsiInventoryFba>  getProductFbaInfo(Set<String> nameSet){
		String sql = "SELECT MAX(data_date) FROM psi_inventory_fba";
		List<Object> date = psiInventoryDao.findBySql(sql);
		sql = "SELECT DISTINCT b.`sku`,CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END) AS productName,a.`country`,b.`fulfillable_quantity`,b.`reserved_quantity`,b.`transit_quantity`,b.orrect_quantity FROM psi_sku a ,psi_inventory_fba b" +
				" WHERE a.`sku` = b.`sku` AND a.`country` = b.`country` AND a.`del_flag` = '0' AND NOT(a.`product_name` LIKE '%other' or a.`product_name` LIKE '%Old%' ) AND b.`data_date` = :p1 and CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END) in :p2  ORDER BY a.`country`";
		List<Object[]> list = psiInventoryDao.findBySql(sql,new Parameter(date.get(0),nameSet));
		Map<String,PsiInventoryFba> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			//String sku = objs[0].toString();
			String productName=objs[1].toString();
			String country = objs[2].toString();
			int fba = (Integer)objs[3];
			int reserved = (Integer)objs[4];
			int transit = (Integer)objs[5];
			Integer orrectQuantity =   (Integer)objs[6]==null?0:(Integer)objs[6];
			String key = productName+"_"+country;
			PsiInventoryFba psiFba = rs.get(key);
			if(psiFba==null){
				rs.put(key, new PsiInventoryFba(null,null,null, fba, 0, reserved, 0, transit, 0, country, null,null,orrectQuantity));
			}else{
				psiFba.setFulfillableQuantity(psiFba.getFulfillableQuantity()+fba);
				psiFba.setReservedQuantity(psiFba.getReservedQuantity()+reserved);
				psiFba.setTransitQuantity(psiFba.getTransitQuantity()+transit);
				if(orrectQuantity!=null){
					psiFba.setOrrectQuantity(psiFba.getOrrectQuantity()==null?orrectQuantity:psiFba.getOrrectQuantity()+orrectQuantity);
				}
			}
			if("fr,uk,es,it,de".contains(country)){
				psiFba = rs.get(productName+"_eu");
				if(psiFba==null){
					rs.put(productName+"_eu", new PsiInventoryFba(null,null,null, fba, 0, reserved, 0, transit, 0, country, null,null,orrectQuantity));
				}else{
					psiFba.setFulfillableQuantity(psiFba.getFulfillableQuantity()+fba);
					psiFba.setReservedQuantity(psiFba.getReservedQuantity()+reserved);
					psiFba.setTransitQuantity(psiFba.getTransitQuantity()+transit);
					if(orrectQuantity!=null){
						psiFba.setOrrectQuantity(psiFba.getOrrectQuantity()==null?orrectQuantity:psiFba.getOrrectQuantity()+orrectQuantity);
					}
				}
			}
			
			psiFba = rs.get(productName);
			if(psiFba==null){
				rs.put(productName, new PsiInventoryFba(null,null,null, fba, 0, reserved, 0, transit, 0, country, null,null,orrectQuantity));
			}else{
				psiFba.setFulfillableQuantity(psiFba.getFulfillableQuantity()+fba);
				psiFba.setReservedQuantity(psiFba.getReservedQuantity()+reserved);
				psiFba.setTransitQuantity(psiFba.getTransitQuantity()+transit);
				if(orrectQuantity!=null){
					psiFba.setOrrectQuantity(psiFba.getOrrectQuantity()==null?orrectQuantity:psiFba.getOrrectQuantity()+orrectQuantity);
				}
			}
		}
		return  rs;
	}
	
	
	public Map<String,PsiInventoryFba>   getAllProductFbaInfo(String countryFlag){
	    String sql = "SELECT MAX(data_date) FROM psi_inventory_fba";
		List<Object> date = psiInventoryDao.findBySql(sql);
		sql = "SELECT DISTINCT b.`sku`,CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END) AS productName,a.`country`,b.`fulfillable_quantity`,b.`reserved_quantity`,b.`transit_quantity`,b.orrect_quantity FROM psi_sku a ,psi_inventory_fba b" +
				" WHERE a.`sku` = b.`sku` AND a.`country` = b.`country` and a.`country` in :p1 AND a.`del_flag` = '0' AND NOT(a.`product_name` LIKE '%other' or a.`product_name` LIKE '%Old%' ) AND b.`data_date` = :p2 ORDER BY a.`country`";
		List<Object[]> list = null;
		if("eu".equals(countryFlag)){
			list=psiInventoryDao.findBySql(sql,new Parameter(Lists.newArrayList("de","fr","it","es","uk"),date.get(0))); 
		}else{
			 sql = "SELECT DISTINCT b.`sku`,CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END) AS productName,a.`country`,b.`fulfillable_quantity`,b.`reserved_quantity`,b.`transit_quantity`,b.orrect_quantity FROM psi_sku a ,psi_inventory_fba b" +
						" WHERE a.`sku` = b.`sku` and a.`country` = :p1 AND a.`country` = b.`country` AND a.`del_flag` = '0' AND NOT(a.`product_name` LIKE '%other' or a.`product_name` LIKE '%Old%' ) AND b.`data_date` = :p2 ORDER BY a.`country`";
			list=psiInventoryDao.findBySql(sql,new Parameter(countryFlag,date.get(0))); 
		}
		Map<String,PsiInventoryFba> rs = Maps.newHashMap();
		for (Object[] objs : list) {
			//String sku = objs[0].toString();
			String productName = objs[1].toString(); 
			String country = objs[2].toString();
			int fba = (Integer)objs[3];
			int reserved = (Integer)objs[4];
			int transit = (Integer)objs[5];
			Integer orrectQuantity =  (Integer)objs[6]==null?0:(Integer)objs[6];
			String key = productName+"_"+country;
			PsiInventoryFba psiFba = rs.get(key);
			if(psiFba==null){
				rs.put(key, new PsiInventoryFba(null,null,null, fba, 0, reserved, 0, transit, 0, country, null,null,orrectQuantity));
			}else{
				psiFba.setFulfillableQuantity(psiFba.getFulfillableQuantity()+fba);
				psiFba.setReservedQuantity(psiFba.getReservedQuantity()+reserved);
				psiFba.setTransitQuantity(psiFba.getTransitQuantity()+transit);
				if(orrectQuantity!=null){
					psiFba.setOrrectQuantity(psiFba.getOrrectQuantity()==null?orrectQuantity:psiFba.getOrrectQuantity()+orrectQuantity);
				}
			}
			psiFba = rs.get(productName);
			if(psiFba==null){
				rs.put(productName, new PsiInventoryFba(null,null,null, fba, 0, reserved, 0, transit, 0, country, null,null,orrectQuantity));
			}else{
				psiFba.setFulfillableQuantity(psiFba.getFulfillableQuantity()+fba);
				psiFba.setReservedQuantity(psiFba.getReservedQuantity()+reserved);
				psiFba.setTransitQuantity(psiFba.getTransitQuantity()+transit);
				if(orrectQuantity!=null){
					psiFba.setOrrectQuantity(psiFba.getOrrectQuantity()==null?orrectQuantity:psiFba.getOrrectQuantity()+orrectQuantity);
				}
			}
		}
		return  rs;
	}
	
	public Map<String, Map<String, Float>> getForecastSalesData(){
		String sql = "SELECT CONCAT(a.`product_name`,'_',a.`country`) AS NAME,a.`data_date`,TRUNCATE(a.`quantity_forecast`/7,2) FROM amazoninfo_sales_forecast a " +
				"			WHERE a.`del_flag` = '0' AND a.`data_date`> DATE_ADD(NOW(),INTERVAL -1 WEEK)  GROUP BY  a.`product_name`,a.`data_date`,a.`country` ORDER BY NAME,a.`country`";
		Map<String, Map<String, Float>> rs = Maps.newHashMap();
		List<Object[]> list = psiInventoryDao.findBySql(sql);
		for (Object[] objects : list) {
			String name = objects[0].toString();
			Date date = (Date)objects[1];
			
			BigDecimal temp = ((BigDecimal)objects[2]);
			Float data = 0f;
			if(temp!=null){
				data = temp.floatValue();
			}
			Map<String, Float> tempMap = rs.get(name);
			if(tempMap==null){
				tempMap = Maps.newHashMap();
				rs.put(name, tempMap);
			}
			tempMap.put(DateUtils.getDate(date,"yyyyMMdd"), data);
		}
		sql = "SELECT CONCAT(a.`product_name`,'_eu') AS NAME,a.`data_date`,TRUNCATE(SUM(a.`quantity_forecast`)/7,2) FROM amazoninfo_sales_forecast a " +
				"	WHERE a.`del_flag` = '0' AND a.`data_date`> DATE_ADD(NOW(),INTERVAL -1 WEEK)  AND a.`country` IN ('uk','it','es','fr','de') GROUP BY  a.`product_name`,a.`data_date` ORDER BY NAME";
		list = psiInventoryDao.findBySql(sql);
		for (Object[] objects : list) {
			String name = objects[0].toString();
			Date date = (Date)objects[1];
			Float data = ((BigDecimal)objects[2]).floatValue();
			Map<String, Float> tempMap = rs.get(name);
			if(tempMap==null){
				tempMap = Maps.newHashMap();
				rs.put(name, tempMap);
			}
			tempMap.put(DateUtils.getDate(date,"yyyyMMdd"), data);
		}
		return rs;
	}
	
	
	public Map<String, Map<String, Float>> getForecastByMonthSalesData(){
		String sql = "SELECT CONCAT(a.`product_name`,'_',a.`country`) AS NAME,a.`data_date`,TRUNCATE(IFNULL((case when quantity_authentication >0  then quantity_authentication else a.`quantity_forecast` end),0)/DAYOFMONTH(LAST_DAY(a.`data_date`)),2) FROM amazoninfo_sales_forecast_month a "+
					" ,psi_product_eliminate b WHERE a.`country` = b.`country` AND a.`product_name` = CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN '_' ELSE '' END,b.`color`) AND a.`type` = b.`sales_forecast_scheme` AND b.`del_flag`='0' AND  a.`data_date`>= CURDATE()  GROUP BY  a.`product_name`,a.`data_date`,a.`country` ORDER BY NAME,a.`country`";
		Map<String, Map<String, Float>> rs = Maps.newHashMap();
		List<Object[]> list = psiInventoryDao.findBySql(sql);
		//List<Object> hasPower = saleReportService.hasPowerProducts();
		Map<String, String> fanOuMap = psiProductEliminateService.findProductFanOuFlag();
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
			Object[] objects = iterator.next();
			String name = objects[0].toString();
			String tempStr = name.substring(0,name.lastIndexOf("_"));
			if(("0".equals(fanOuMap.get(tempStr)) || "1".equals(fanOuMap.get(tempStr)))&&name.contains("_de")){
				iterator.remove();
				continue;
			}
			Date date = (Date)objects[1];
			
			BigDecimal temp = ((BigDecimal)objects[2]);
			Float data = 0f;
			if(temp!=null){
				data = temp.floatValue();
			}
			Map<String, Float> tempMap = rs.get(name);
			if(tempMap==null){
				tempMap = Maps.newHashMap();
				rs.put(name, tempMap);
			}
			tempMap.put(DateUtils.getDate(date,"yyyyMMdd"), data);
		}
		sql = "SELECT CONCAT(a.`product_name`,'_eu') AS NAME,a.`data_date`,TRUNCATE(SUM(ifnull((case when quantity_authentication >0  then a.quantity_authentication else a.`quantity_forecast` end),0))/DAYOFMONTH(LAST_DAY(a.`data_date`)),2) FROM amazoninfo_sales_forecast_month a "+
				",psi_product_eliminate b WHERE a.`country` = b.`country` AND a.`product_name` = CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN '_' ELSE '' END,b.`color`) AND a.`type` = b.`sales_forecast_scheme` AND b.`del_flag`='0' AND a.`data_date`>= CURDATE() AND a.`country` IN ('uk','it','es','fr','de')  GROUP BY  a.`product_name`,a.`data_date` ORDER BY NAME";
		list = psiInventoryDao.findBySql(sql);
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
			Object[] objects = iterator.next();
			String name = objects[0].toString();
			String tempStr = name.substring(0,name.lastIndexOf("_"));
			if("1".equals(fanOuMap.get(tempStr)) || "2".equals(fanOuMap.get(tempStr))){
				iterator.remove();
				continue;
			}
			Date date = (Date)objects[1];
			Float data = ((BigDecimal)objects[2]).floatValue();
			Map<String, Float> tempMap = rs.get(name);
			if(tempMap==null){
				tempMap = Maps.newHashMap();
				rs.put(name, tempMap);
			}
			tempMap.put(DateUtils.getDate(date,"yyyyMMdd"), data);
		}
		sql = "SELECT CONCAT(a.`product_name`,'_eunouk') AS NAME,a.`data_date`,TRUNCATE(SUM(ifnull((case when quantity_authentication >0  then a.quantity_authentication else a.`quantity_forecast` end),0))/DAYOFMONTH(LAST_DAY(a.`data_date`)),2) FROM amazoninfo_sales_forecast_month a "+
				",psi_product_eliminate b WHERE a.`country` = b.`country` AND a.`product_name` = CONCAT(b.`product_name`,CASE WHEN b.`color`!='' THEN '_' ELSE '' END,b.`color`) AND a.`type` = b.`sales_forecast_scheme` AND b.`del_flag`='0' AND a.`data_date`>= CURDATE() AND a.`country` IN ('it','es','fr','de')  GROUP BY  a.`product_name`,a.`data_date` ORDER BY NAME";
		list = psiInventoryDao.findBySql(sql);
		for (Iterator<Object[]> iterator = list.iterator(); iterator.hasNext();) {
			Object[] objects = iterator.next();
			String name = objects[0].toString();
			String tempStr = name.substring(0,name.lastIndexOf("_"));
			if("0".equals(fanOuMap.get(tempStr)) || "2".equals(fanOuMap.get(tempStr))){
				iterator.remove();
				continue;
			}
			Date date = (Date)objects[1];
			Float data = ((BigDecimal)objects[2]).floatValue();
			Map<String, Float> tempMap = rs.get(name);
			if(tempMap==null){
				tempMap = Maps.newHashMap();
				rs.put(name, tempMap);
			}
			tempMap.put(DateUtils.getDate(date,"yyyyMMdd"), data);
		}
		return rs;
	}
	
	
	public Map<String, Integer> getProducTotalSalesBy31Days(){
		String sql = "SELECT CONCAT(b.`product_name`,CASE  WHEN b.`color`='' THEN '' ELSE CONCAT('_',b.`color`) END) AS productName ,SUM(b.`sales_volume`) FROM  amazoninfo_sale_report b"+
			"WHERE  b.order_type='1' and b.`product_name` IS NOT NULL AND NOT(b.`product_name` LIKE '%other%' or b.`product_name` LIKE '%Old%' ) AND b.`date` < DATE_ADD(CURDATE(),INTERVAL -1 DAY) AND b.`date` >= DATE_ADD(CURDATE(),INTERVAL -32 DAY)   GROUP BY productName ";
		 List<Object[]> list = psiInventoryDao.findBySql(sql);
		 Map<String, Integer> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(),((BigDecimal)objects[1]).intValue());
		}
		return rs;
	}
	
	
	//非中国仓发的才是被分配的
	public Map<String, Object[]> getFbaWorking(){
		String sql = "SELECT CONCAT(c.`product_name`,CASE  WHEN c.`color`='' THEN '' ELSE CONCAT('_',c.`color`) END ,'_',a.`country`) AS productName,  GROUP_CONCAT(DISTINCT b.`sku`),SUM(b.`quantity_shipped`) FROM psi_fba_inbound a , psi_fba_inbound_item b , psi_sku c WHERE  a.`country` = c.`country` AND c.`sku` = b.`sku` and c.`del_flag`='0' AND  a.`id` = b.`fba_inbound_id` and  NOT(a.`ship_from_address`='CN')  and  a.`shipment_status` NOT IN ('DELETED','CANCELLED','ERROR') and a.`shipment_id` IS NOT NULL and a.`shipment_status`!='' AND a.`shipped_date` is null and a.create_date>'2015-08-01' GROUP BY  productName";
		List<Object[]> list = psiInventoryDao.findBySql(sql);
		Map<String, Object[]> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(),objects);
		}
		return rs;
	}
	
	public Map<String, Integer> getFbaWorkingByEuro(){
		String sql = "SELECT CONCAT(c.`product_name`,CASE  WHEN c.`color`='' THEN '' ELSE CONCAT('_',c.`color`) END ) AS productName,SUM(b.`quantity_shipped`) FROM psi_fba_inbound a , psi_fba_inbound_item b , psi_sku c WHERE a.`country` = c.`country` and c.`del_flag`='0' AND  c.`sku` = b.`sku` AND  a.`id` = b.`fba_inbound_id` AND a.`shipment_status` NOT IN ('DELETED','CANCELLED','ERROR') and a.`shipment_id` IS NOT NULL  AND a.`shipment_status`!='' and a.`shipped_date` is null and a.create_date>'2015-08-01'  AND a.`country` IN ('de','fr','es','it','uk') and   NOT(a.`ship_from_address`='CN') GROUP BY productName";
		List<Object[]> list = psiInventoryDao.findBySql(sql);
		Map<String, Integer> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(),((BigDecimal)objects[1]).intValue());
		}
		return rs;
	}
	
	//非中国仓发的才是被分配的 sku/数量
	public Map<String, Integer> getFbaWorkingMap(String stockCode ){
		stockCode = "%From "+stockCode+"%";
		String sql = "SELECT  b.`sku`,SUM(b.`quantity_shipped`) FROM psi_fba_inbound a , psi_fba_inbound_item b , psi_sku c WHERE  a.`country` = c.`country` AND c.`sku` = b.`sku` and c.`del_flag`='0' AND  a.`id` = b.`fba_inbound_id`    AND a.`create_date`>'2015-08-01' AND a.`shipped_date` IS NULL AND a.`shipment_name` LIKE :p1 AND a.`shipment_status`!='' AND a.`shipment_status` not in :p2 GROUP BY b.`sku`";
		List<Object[]> list = psiInventoryDao.findBySql(sql,new Parameter(stockCode,new String[]{"CANCELLED","DELETED","ERROR"}));
		Map<String, Integer> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(),((BigDecimal)objects[1]).intValue());
		}   
		return rs;
	}
	
	//非当前shipmentId被占用的库存
	public Map<String, Integer> getFbaWorkingMap(String stockCode,Set<String> skus,Integer inboundId ){
		stockCode = "%From "+stockCode+"%";
		String sql = "SELECT  b.`sku`,SUM(b.`quantity_shipped`) FROM psi_fba_inbound a , psi_fba_inbound_item b , psi_sku c WHERE  a.`country` = c.`country` AND c.`sku` = b.`sku` AND c.`del_flag`='0' AND  a.`id` = b.`fba_inbound_id`  AND b.`sku` IN :p2 AND a.`id`!=:p3 AND a.`shipment_status` NOT IN ('DELETED','CANCELLED','ERROR') AND a.`shipment_status`!='' AND a.`shipped_date` is null and a.create_date>'2015-08-01' AND a.`shipment_name` LIKE :p1 GROUP BY b.`sku`";
		List<Object[]> list = psiInventoryDao.findBySql(sql,new Parameter(stockCode,skus,inboundId));
		Map<String, Integer> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(),((BigDecimal)objects[1]).intValue());
		}
		return rs;
	}
	
	
	public int getProductFbaWorking(String sku,String stockCode ){
		stockCode = "%From "+stockCode+"%";
		String sql = "SELECT SUM(b.`quantity_shipped`) FROM psi_fba_inbound a , psi_fba_inbound_item b WHERE   a.`id` = b.`fba_inbound_id` AND a.`shipment_status` NOT IN ('DELETED','CANCELLED','ERROR') AND a.`shipment_status`!='' AND a.`shipped_date` is null and a.create_date>'2015-08-01' and b.`sku` = :p1 and a.`shipment_name` LIKE :p2 ";
		List<Object> list = psiInventoryDao.findBySql(sql,new Parameter(sku,stockCode));
		BigDecimal rs = (BigDecimal)list.get(0);
		if(rs!=null){
			return rs.intValue();
		}
		return 0;
	}
	
	//非当前shipmentId被占用的库存
	public int getProductFbaWorking(String sku,String stockCode,Integer inboundId){
		stockCode = "%From "+stockCode+"%";
		String sql = "SELECT SUM(b.`quantity_shipped`) FROM psi_fba_inbound a , psi_fba_inbound_item b WHERE   a.`id` = b.`fba_inbound_id` AND a.`shipment_status` NOT IN ('DELETED','CANCELLED','ERROR') AND a.`shipment_status`!='' AND a.`shipped_date` is null and a.create_date>'2015-08-01' and b.`sku` = :p1 and a.`shipment_name` LIKE :p2 and a.`id`!=:p3";
		List<Object> list = psiInventoryDao.findBySql(sql,new Parameter(sku,stockCode,inboundId));
		BigDecimal rs = (BigDecimal)list.get(0);
		if(rs!=null){
			return rs.intValue();
		}
		return 0;
	}
	
	/**
	 * 获取在产产品信息           20160516 添加分批收货表deliveryDate
	 * @param  productName
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	public List<PsiInventoryInnerDto> getProducingByNameAndCountry(String productName_color,String country) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<PsiInventoryInnerDto> innerDto=Lists.newArrayList();
		String sql="";
		Parameter para=null;
		if(StringUtils.isEmpty(country)){
			sql="SELECT a.`order_no`,b.`country_code`,CASE WHEN  c.`delivery_date` IS NOT NULL THEN (c.quantity-c.quantity_received-(c.quantity_off-c.quantity_off_received)) ELSE (b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) END  AS orderNum " +
					" ,a.`purchase_date`,b.`delivery_date`,CASE WHEN c.`remark` IS NOT NULL THEN c.`remark` ELSE  b.`remark` END AS remark,CASE WHEN  c.`delivery_date` IS NOT NULL THEN c.`delivery_date` ELSE  b.`actual_delivery_date`  END  AS deliveryDate ," +
					" CASE WHEN  c.`delivery_date` IS NOT NULL THEN c.`quantity_off`-c.`quantity_off_received` ELSE (b.`quantity_off_ordered`-b.`quantity_off_received`) END as offlineNum " +
					" FROM psi_purchase_order AS a ,psi_purchase_order_item AS b LEFT JOIN psi_purchase_order_delivery_date AS c ON  b.`id`=c.`purchase_order_item_id` AND c.`del_flag`='0' AND (c.quantity-c.quantity_received) >0 " +
					" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3')   AND b.`del_flag`='0' AND (b.`quantity_ordered`-b.`quantity_received`)>0 AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1 " +
					" UNION ALL SELECT a.`order_no`,b.`country_code`,CASE WHEN  c.`delivery_date` IS NOT NULL THEN (c.quantity-c.quantity_received-(c.quantity_off-c.quantity_off_received)) ELSE (b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) END  AS orderNum " +
					" ,a.`purchase_date`,b.`delivery_date`,CASE WHEN c.`remark` IS NOT NULL THEN c.`remark` ELSE  b.`remark` END AS remark,CASE WHEN  c.`delivery_date` IS NOT NULL THEN c.`delivery_date` ELSE  b.`actual_delivery_date`  END  AS deliveryDate ," +
					" CASE WHEN  c.`delivery_date` IS NOT NULL THEN c.`quantity_off`-c.`quantity_off_received` ELSE (b.`quantity_off_ordered`-b.`quantity_off_received`) END as offlineNum " +
					" FROM lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b LEFT JOIN lc_psi_purchase_order_delivery_date AS c ON  b.`id`=c.`purchase_order_item_id` AND c.`del_flag`='0' AND (c.quantity-c.quantity_received) >0 " +
					" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3')   AND b.`del_flag`='0' AND (b.`quantity_ordered`-b.`quantity_received`)>0 AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1 ";
			para=new Parameter(productName_color);
		}else{
			sql="SELECT a.`order_no`,b.`country_code`,CASE WHEN  c.`delivery_date` IS NOT NULL THEN (c.quantity-c.quantity_received-(c.quantity_off-c.quantity_off_received)) ELSE (b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) END  AS orderNum" +
					" ,a.`purchase_date`,b.`delivery_date`,CASE WHEN c.`remark` IS NOT NULL THEN c.`remark` ELSE  b.`remark` END AS remark,CASE WHEN  c.`delivery_date` IS NOT NULL THEN c.`delivery_date` ELSE  b.`actual_delivery_date`  END  AS deliveryDate," +
					" CASE WHEN  c.`delivery_date` IS NOT NULL THEN c.`quantity_off`-c.`quantity_off_received` ELSE (b.`quantity_off_ordered`-b.`quantity_off_received`) END as offlineNum " +
					" FROM psi_purchase_order AS a ,psi_purchase_order_item AS b LEFT JOIN psi_purchase_order_delivery_date AS c ON  b.`id`=c.`purchase_order_item_id` AND c.`del_flag`='0' AND (c.quantity-c.quantity_received) >0 " +
					" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3')  AND b.`del_flag`='0' AND (b.`quantity_ordered`-b.`quantity_received`)>0 AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1 AND b.`country_code` in :p2"+
					" UNION ALL SELECT a.`order_no`,b.`country_code`,CASE WHEN  c.`delivery_date` IS NOT NULL THEN (c.quantity-c.quantity_received-(c.quantity_off-c.quantity_off_received)) ELSE (b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) END  AS orderNum" +
					" ,a.`purchase_date`,b.`delivery_date`,CASE WHEN c.`remark` IS NOT NULL THEN c.`remark` ELSE  b.`remark` END AS remark,CASE WHEN  c.`delivery_date` IS NOT NULL THEN c.`delivery_date` ELSE  b.`actual_delivery_date`  END  AS deliveryDate," +
					" CASE WHEN  c.`delivery_date` IS NOT NULL THEN c.`quantity_off`-c.`quantity_off_received` ELSE (b.`quantity_off_ordered`-b.`quantity_off_received`) END as offlineNum " +
					" FROM lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b LEFT JOIN lc_psi_purchase_order_delivery_date AS c ON  b.`id`=c.`purchase_order_item_id` AND c.`del_flag`='0' AND (c.quantity-c.quantity_received) >0 " +
					" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3')  AND b.`del_flag`='0' AND (b.`quantity_ordered`-b.`quantity_received`)>0 AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1 AND b.`country_code` in :p2";
			if("eu".equals(country)){
				para=new Parameter(productName_color,Lists.newArrayList("de","fr","it","es","uk"));
			}else{
				para=new Parameter(productName_color,Lists.newArrayList(country));
			}
		}    
		List<Object[]> list=this.psiInventoryDao.findBySql(sql, para);
		for(Object[] object:list){
			String tranWeek = "";
			if(object[6]!=null){
				tranWeek ="WK"+DateUtils.getWeekOfYear(DateUtils.addDays((Date)object[6],7));
			}else if(object[4]!=null){
				tranWeek ="WK"+DateUtils.getWeekOfYear(DateUtils.addDays((Date)object[4],7))+"";
			}
			String remark=object[5]==null?"":object[5].toString();
			String orderNo=object[0].toString();
			String countryCode=object[1].toString();
			if(StringUtils.isNotEmpty(remark)){
				remark= remark + "<br/>" + this.findHisPurchaseRemark(orderNo, productName_color, countryCode);
			}
			
			innerDto.add(new PsiInventoryInnerDto(orderNo,countryCode, ((BigInteger)object[2]).intValue(),
					sdf.format((Date)object[3])+"(WK"+DateUtils.getWeekOfYear((Date)object[3])+")"
					,object[6]==null?"":(sdf.format((Date)object[6])+"(WK"+DateUtils.getWeekOfYear((Date)object[6])+")")
					,null,null,null,null,null,null,
					remark,object[4]==null?"":(sdf.format((Date)object[4])+"(WK"+DateUtils.getWeekOfYear((Date)object[4])+")"),tranWeek,((BigInteger)object[7]).intValue()));
		}
		
		return innerDto;
	}
	
	
	/**
	 * 获取在产产品信息
	 * @param  productName
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	public List<PsiInventoryInnerDto> getTransporttingByNameAndCountry(String productName_color,String country,String warehouseCode){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<PsiInventoryInnerDto> innerDto=Lists.newArrayList();
		String sql="";
		Parameter para=null;
		if(StringUtils.isEmpty(country)){
			//sql="SELECT a.`transport_no`,b.`country_code`,b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END),a.`pick_up_date`,a.`oper_arrival_date`,b.sku,a.model,a.to_country,b.barcode,a.remark,b.offline_sta FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0'  AND a.`transport_sta` IN('1','2','3','4') AND a.`to_store` in (SELECT p.id FROM psi_stock AS p WHERE p.`type`='0' )  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1  ";
			
			sql="SELECT a.`transport_no`,b.`country_code`,b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END),a.`pick_up_date`,a.`oper_arrival_date`,b.sku,a.model,a.to_country,b.barcode,a.remark,b.offline_sta,(case when a.from_store='19' then 'DE' when a.from_store='120' then 'US' when a.from_store='147' then 'JP' else 'CN' end) FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0'  AND a.`transport_sta` IN('1','2','3','4') AND a.`to_store` in (SELECT p.id FROM psi_stock AS p WHERE p.`type`='0' )  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1  "+
				" union all SELECT a.`transport_no`,b.`country_code`,b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END),a.`pick_up_date`,a.`oper_arrival_date`,b.sku,a.model,a.to_country,b.barcode,a.remark,b.offline_sta,(case when a.from_store='19' then 'DE' when a.from_store='120' then 'US' when a.from_store='147' then 'JP' else 'CN' end) FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0'  AND a.`transport_sta` IN('1','2','3','4') AND a.`to_store` in (SELECT p.id FROM psi_stock AS p WHERE p.`type`='0' )  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1  ";
			
			para=new Parameter(productName_color);
		}else{
			//sql="SELECT a.`transport_no`,b.`country_code`,b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END),a.`pick_up_date`,a.`oper_arrival_date`,b.sku,a.model,a.to_country,b.barcode ,a.remark,b.offline_sta FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND a.`transport_sta` IN('1','2','3','4') AND a.`to_store` IN (SELECT p.id FROM psi_stock AS p WHERE p.`type`='0' AND  p.`countrycode`=:p2)  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1 AND b.`country_code` in :p3";
			
			sql="SELECT a.`transport_no`,b.`country_code`,b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END),a.`pick_up_date`,a.`oper_arrival_date`,b.sku,a.model,a.to_country,b.barcode ,a.remark,b.offline_sta,(case when a.from_store='19' then 'DE' when a.from_store='120' then 'US' when a.from_store='147' then 'JP' else 'CN' end) FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND a.`transport_sta` IN('1','2','3','4') AND a.`to_store` IN (SELECT p.id FROM psi_stock AS p WHERE p.`type`='0' AND  p.`countrycode`=:p2)  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1 AND (case when (a.to_store='19'&&b.country_code not like 'de%'&&b.country_code not like 'uk%'&&b.country_code not like 'fr%'&&b.country_code not like 'it%'&&b.country_code not like 'es%') then 'de' when (a.to_store='120'&&b.country_code not like 'com%') then 'com' when (a.to_store='147'&&b.country_code not like 'jp%') then 'jp' else  b.country_code end)  in :p3 "+
			" union all SELECT a.`transport_no`,b.`country_code`,b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END),a.`pick_up_date`,a.`oper_arrival_date`,b.sku,a.model,a.to_country,b.barcode ,a.remark,b.offline_sta,(case when a.from_store='19' then 'DE' when a.from_store='120' then 'US' when a.from_store='147' then 'JP' else 'CN' end) FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND a.`transport_sta` IN('1','2','3','4') AND a.`to_store` IN (SELECT p.id FROM psi_stock AS p WHERE p.`type`='0' AND  p.`countrycode`=:p2)  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1 AND (case when (a.to_store='19'&&b.country_code not like 'de%'&&b.country_code not like 'uk%'&&b.country_code not like 'fr%'&&b.country_code not like 'it%'&&b.country_code not like 'es%') then 'de' when (a.to_store='120'&&b.country_code not like 'com%') then 'com' when (a.to_store='147'&&b.country_code not like 'jp%') then 'jp' else  b.country_code end)  in :p3 ";
			
			if("eu".equals(country)){
				para=new Parameter(productName_color,warehouseCode,Lists.newArrayList("de","fr","it","es","uk"));
			}else{
				para=new Parameter(productName_color,warehouseCode,Lists.newArrayList(country));
			}   
		}    
		
		List<Object[]> list=this.psiInventoryDao.findBySql(sql, para);
		for(Object[] object:list){
			String model=object[6].toString();
			if("0".equals(model)){
				model="by air";
			}else if("1".equals(model)){
				model="by sea";
			}else if("2".equals(model)){
				model="by express";
			}else if("3".equals(model)){
				model="by train";
			}
			PsiInventoryInnerDto dto =  new PsiInventoryInnerDto(object[0].toString(), object[1].toString(), ((BigInteger)object[2]).intValue(),object[3]==null?"":sdf.format((Date)object[3]), null,object[5].toString(),null,null,null,object[4]==null?"":sdf.format((Date)object[4]),model,(String)object[7],null,(object[8]==null?"":object[8].toString()),(object[9]==null?"":object[9].toString()),object[11].toString(),object[10].toString());
			innerDto.add(dto);
		}
		return innerDto;
	}
	
	
	/**
	 * 获取在途运单信息
	 * @param  productName
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	public List<PsiInventoryInnerDto> getTranByNameAndCountry(String productName_color,String country,String warehouseCode){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<PsiInventoryInnerDto> innerDto=Lists.newArrayList();
		String sql="";
		Parameter para=null;
		if(StringUtils.isEmpty(country)){
			sql="SELECT a.`transport_no`,b.`country_code`,b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END),a.`oper_delivery_date`,a.`oper_arrival_date`,b.sku,a.model,a.to_country FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('1','2','3','4') AND a.`to_store` in (SELECT p.id FROM psi_stock AS p WHERE p.`type`='0' )  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1  "+
				" union all SELECT a.`transport_no`,b.`country_code`,b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END),a.`oper_delivery_date`,a.`oper_arrival_date`,b.sku,a.model,a.to_country FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('1','2','3','4') AND a.`to_store` in (SELECT p.id FROM psi_stock AS p WHERE p.`type`='0' )  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1  ";
			para=new Parameter(productName_color);
		}else{
			sql="SELECT a.`transport_no`,b.`country_code`,b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END),a.`oper_delivery_date`,a.`oper_arrival_date`,b.sku,a.model,a.to_country FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('1','2','3','4') AND a.`to_store` IN (SELECT p.id FROM psi_stock AS p WHERE p.`type`='0' AND  p.`countrycode`=:p2)  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1 AND b.`country_code` in :p3 "+
				" union all SELECT a.`transport_no`,b.`country_code`,b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END),a.`oper_delivery_date`,a.`oper_arrival_date`,b.sku,a.model,a.to_country FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('1','2','3','4') AND a.`to_store` IN (SELECT p.id FROM psi_stock AS p WHERE p.`type`='0' AND  p.`countrycode`=:p2)  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1 AND b.`country_code` in :p3 ";
			if("eu".equals(country)){
				para=new Parameter(productName_color,warehouseCode,Lists.newArrayList("de","fr","it","es","uk"));
			}else if("am".equals(country)){
				para=new Parameter(productName_color,warehouseCode,Lists.newArrayList("com","ca","mx"));
			}else{
				para=new Parameter(productName_color,warehouseCode,Lists.newArrayList(country));
			}   
		}
		
		List<Object[]> list=this.psiInventoryDao.findBySql(sql, para);
		for(Object[] object:list){
			String model=object[6].toString();
			if("0".equals(model)){
				model="by air";
			}else if("1".equals(model)){
				model="by sea";
			}else if("2".equals(model)){
				model="by express";
			}else if("3".equals(model)){
				model="by train";
			}
			innerDto.add(new PsiInventoryInnerDto(object[0].toString(), object[1].toString(), ((BigInteger)object[2]).intValue(),object[3]==null?"":sdf.format((Date)object[3]), null,object[5].toString(),null,null,null,object[4]==null?"":sdf.format((Date)object[4]),model,(String)object[7],null,null,null));
		}
		return innerDto;
	}
	
	
	/**
	 * 获取待发货运单信息
	 * @param  productName
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	public List<PsiInventoryInnerDto> getWaitTranByNameAndCountry(String productName_color,String country,String warehouseCode){
		List<PsiInventoryInnerDto> innerDto=Lists.newArrayList();
		String sql="";
		Parameter para=null;
		if("CN".equals(country)){
			sql="SELECT a.`transport_no`,b.`country_code`,b.`quantity`,a.`oper_delivery_date`,a.`oper_arrival_date`,b.sku,a.model,a.to_country FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` ='0'  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1  "+
				" union all SELECT a.`transport_no`,b.`country_code`,b.`quantity`,a.`oper_delivery_date`,a.`oper_arrival_date`,b.sku,a.model,a.to_country FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` ='0'  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1  ";
			para=new Parameter(productName_color);
		}else{
			sql="SELECT a.`transport_no`,b.`country_code`,b.`quantity`,a.`oper_delivery_date`,a.`oper_arrival_date`,b.sku,a.model,a.to_country FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta`='0' AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1 AND b.`country_code` =:p2 "+
				" union all SELECT a.`transport_no`,b.`country_code`,b.`quantity`,a.`oper_delivery_date`,a.`oper_arrival_date`,b.sku,a.model,a.to_country FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta`='0' AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1 AND b.`country_code` =:p2 ";
			para=new Parameter(productName_color,country);
		}
		
		List<Object[]> list=this.psiInventoryDao.findBySql(sql, para);
		for(Object[] object:list){
			String model=object[6].toString();
			if("0".equals(model)){
				model="by air";
			}else if("1".equals(model)){
				model="by sea";
			}else if("2".equals(model)){
				model="by express";
			}else if("3".equals(model)){
				model="by train";
			}
			innerDto.add(new PsiInventoryInnerDto(object[0].toString(), object[1].toString(), (Integer)object[2],null, null,object[5].toString(),null,null,null,null,model,(String)object[7],null,null,null));
		}
		return innerDto;
	}
	/**
	 * 获取在产产品信息
	 * @param  productName
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	public List<PsiInventoryInnerDto> getPreTransporttingByNameAndCountry(String productName_color,String country,String warehouseCode){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<PsiInventoryInnerDto> innerDto=Lists.newArrayList();
		String sql="";
		Parameter para=null;
		if(StringUtils.isEmpty(country)){
			sql="SELECT a.`transport_no`,b.`country_code`,b.quantity,a.`pick_up_date`,a.`oper_arrival_date`,b.sku,a.model,a.to_country,(case when a.from_store='19' then 'DE' when a.from_store='120' then 'US' when a.from_store='147' then 'JP' else 'CN' end) FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` ='0'  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1  "+
		     " union all SELECT a.`transport_no`,b.`country_code`,b.quantity,a.`pick_up_date`,a.`oper_arrival_date`,b.sku,a.model,a.to_country,(case when a.from_store='19' then 'DE' when a.from_store='120' then 'US' when a.from_store='147' then 'JP' else 'CN' end) FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` ='0'  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1 ";
			para=new Parameter(productName_color);
		}else{
			sql="SELECT a.`transport_no`,b.`country_code`,b.quantity,a.`pick_up_date`,a.`oper_arrival_date`,b.sku,a.model,a.to_country,(case when a.from_store='19' then 'DE' when a.from_store='120' then 'US' when a.from_store='147' then 'JP' else 'CN' end) FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta`  ='0'   AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1 AND b.`country_code` in :p2  "+
				" union all SELECT a.`transport_no`,b.`country_code`,b.quantity,a.`pick_up_date`,a.`oper_arrival_date`,b.sku,a.model,a.to_country,(case when a.from_store='19' then 'DE' when a.from_store='120' then 'US' when a.from_store='147' then 'JP' else 'CN' end) FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta`  ='0'   AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1 AND b.`country_code` in :p2 ";
			if("eu".equals(country)){
				para=new Parameter(productName_color,Lists.newArrayList("de","fr","it","es","uk"));
			}else{
				para=new Parameter(productName_color,Lists.newArrayList(country));
			}   
		}
		
		List<Object[]> list=this.psiInventoryDao.findBySql(sql, para);
		for(Object[] object:list){
			String model=object[6].toString();
			if("0".equals(model)){
				model="by air";
			}else if("1".equals(model)){
				model="by sea";
			}else if("2".equals(model)){
				model="by express";
			}else if("3".equals(model)){
				model="by train";
			}
			String toCountry = (String)object[7];
			if("com".equals(toCountry)){
				toCountry = "us";
			}
			if(toCountry!=null){
				toCountry = toCountry.toUpperCase();
			}
			innerDto.add(new PsiInventoryInnerDto(object[0].toString(), object[1].toString(), ((Integer)object[2]).intValue(),object[3]==null?"":sdf.format((Date)object[3]), null,null,null,null,null,object[4]==null?"":sdf.format((Date)object[4]),model,toCountry,null,(String)object[8],null));
		}
		return innerDto;
	}
	
	
	/**
	 * 获取在库产品信息
	 * @param  productName
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	public List<PsiInventoryInnerDto> getInventoryByNameCountryWarehouseCode(String productName_color,String country,String warehouseCode){
		List<PsiInventoryInnerDto> innerDto=Lists.newArrayList();
		String sql="";
		Parameter para=null;
		if(StringUtils.isEmpty(country)){
			if(StringUtils.isEmpty(warehouseCode)){
				sql="SELECT a.`country_code`,a.sku,a.`new_quantity`,a.`old_quantity`,a.`broken_quantity`,a.`renew_quantity` FROM psi_inventory AS a where CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END ) =:p1 ";
				para=new Parameter(productName_color);
			}else{
				sql="SELECT a.`country_code`,a.sku,a.`new_quantity`,a.`old_quantity`,a.`broken_quantity`,a.`renew_quantity` FROM psi_inventory AS a ,psi_stock AS b WHERE a.`warehouse_id`=b.`id` AND CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END )=:p1 AND b.`countrycode`=:p2 ";
				para=new Parameter(productName_color,warehouseCode);
			}
		}else{
			String [] countryArr=null;
			if("eu".equals(country)){
				countryArr=new String[]{"de","fr","it","es","uk"};
			}else{
				countryArr=new String[]{country};
			}
			if(StringUtils.isEmpty(warehouseCode)){
				sql="SELECT a.`country_code`,a.sku,a.`new_quantity`,a.`old_quantity`,a.`broken_quantity`,a.`renew_quantity` FROM psi_inventory AS a where CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END )=:p1 AND a.`country_code` in :p2  ";
				para=new Parameter(productName_color,countryArr);
			}else{
				sql="SELECT a.`country_code`,a.sku,a.`new_quantity`,a.`old_quantity`,a.`broken_quantity`,a.`renew_quantity` FROM psi_inventory AS a ,psi_stock AS b WHERE a.`warehouse_id`=b.`id` AND CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END )=:p1 AND a.`country_code` in :p2 AND b.`countrycode` =:p3";
				para=new Parameter(productName_color,countryArr,warehouseCode);
			}
		}
		
		List<Object[]> list=this.psiInventoryDao.findBySql(sql, para);
		for(Object[] object:list){
			innerDto.add(new PsiInventoryInnerDto(null, object[0].toString(), (Integer)object[2], null,null,object[1].toString(),(Integer)object[3],(Integer)object[4],(Integer)object[5],null,null,null,null,null,null));
		}
		return innerDto;
	}
	
	
	/**
	 * 获取FBA在途信息
	 * @param  productName
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	public List<FbaInboundDto> getFbaTransporttingByNameAndCountry(String productName_color,String country){
		List<Object[]> list = Lists.newArrayList();
		if(StringUtils.isNotEmpty(country)){
			Set<String> countrys = Sets.newHashSet(country);
			if("eu".equals(country)){
				countrys.add("de");
				countrys.add("uk");
				countrys.add("fr");
				countrys.add("es");
				countrys.add("it");
			}
			String sql = "SELECT DISTINCT sku FROM psi_sku c WHERE c.`del_flag`='0' and  c.`country` in :p1 AND  CONCAT(c.`product_name`,CASE  WHEN c.`color`='' THEN '' ELSE CONCAT('_',c.color) END )=:p2";
			List<Object> temp = psiInventoryDao.findBySql(sql, new Parameter(countrys,productName_color));
			if(temp.size()>0){
				sql="SELECT a.`shipment_id`,a.`shipment_name`,b.`quantity_shipped`,b.`quantity_received`,b.`sku`,a.`shipment_status`,DATE_FORMAT(a.`delivery_date`,'%Y-%m-%d') AS deliveryStr FROM psi_fba_inbound a ,psi_fba_inbound_item b" +
						"	 WHERE a.`id` = b.`fba_inbound_id` and a.shipped_date is not null AND a.`shipment_status` IN ('SHIPPED','IN_TRANSIT','DELIVERED','CHECKED_IN','RECEIVING') AND b.`quantity_shipped`>b.`quantity_received` AND a.`country` in :p1 AND b.`sku` IN :p2 ";
				list = psiInventoryDao.findBySql(sql, new Parameter(countrys,temp));
			}
		}else{
			String sql = "SELECT DISTINCT sku FROM psi_sku c WHERE  c.`del_flag`='0' and CONCAT(c.`product_name`,CASE  WHEN c.`color`='' THEN '' ELSE CONCAT('_',c.color) END )=:p1";
			List<Object> temp = psiInventoryDao.findBySql(sql, new Parameter(productName_color));
			if(temp.size()>0){
				sql="SELECT a.`shipment_id`,a.`shipment_name`,b.`quantity_shipped`,b.`quantity_received`,b.`sku`,a.`shipment_status`,DATE_FORMAT(a.`delivery_date`,'%Y-%m-%d') AS deliveryStr FROM psi_fba_inbound a ,psi_fba_inbound_item b" +
					"	 WHERE a.`id` = b.`fba_inbound_id` and a.shipped_date is not null AND a.`shipment_status` IN ('SHIPPED','IN_TRANSIT','DELIVERED','CHECKED_IN','RECEIVING') AND b.`quantity_shipped`>b.`quantity_received` AND b.`sku` IN :p1 order by  a.`shipment_name`";
				list = psiInventoryDao.findBySql(sql, new Parameter(temp));
			}	
		}
		Set<String> shipments = Sets.newHashSet();
		List<FbaInboundDto> rs = Lists.newArrayList();
		for(Object[] objs:list){
			shipments.add(objs[0].toString());
			rs.add(new FbaInboundDto(objs[0].toString(),objs[1].toString(),(Integer)objs[2],(Integer)objs[3],objs[4].toString(),objs[5].toString(),objs[6]==null?"":objs[6].toString()));
		}
		String sql = "SELECT a.`shipment_id`,date_format(a.`oper_arrival_date`,'%Y-%c-%d') ,a.remark FROM psi_transport_order AS a WHERE a.`shipment_id` REGEXP  :p1 AND a.`oper_arrival_date` IS NOT NULL and a.`shipment_id` IS NOT NULL AND a.`transport_type`='1' "+
				" union all SELECT a.`shipment_id`,date_format(a.`oper_arrival_date`,'%Y-%c-%d') ,a.remark FROM lc_psi_transport_order AS a WHERE a.`shipment_id` REGEXP  :p1 AND a.`oper_arrival_date` IS NOT NULL and a.`shipment_id` IS NOT NULL  AND a.`transport_type`='1' ";
		if(shipments.size()>0){
			StringBuilder like = new StringBuilder("");
			for (String sid : shipments) {
				like.append(sid).append(",.*|.*,").append(sid).append("|").append(sid).append("|");
			}
			if(like.length()>0){
				like = new StringBuilder(like.substring(0,like.length()-1));
			}
			List<Object[]> temp = psiInventoryDao.findBySql(sql, new Parameter(like.toString()));
			for (Object[] objects : temp) {
				String sid = objects[0].toString();
				for (FbaInboundDto dto : rs) {
					String id = dto.getShipmentId();
					if(id.equals(sid)||sid.contains(id+",") || sid.contains(","+id)){
						dto.setToDate(objects[1].toString());
						dto.setRemark(objects[2].toString());
					}
				}
			}
		}
		return rs;
	}
	
	
	/**
	 * 获取FBA为发货帖子信息
	 * @param  productName
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	public List<FbaInboundDto> getPreWkFbaTransporttingByNameAndCountry(String productName_color,String country){
		List<Object[]> list = Lists.newArrayList();
		Set<String> countrys = Sets.newHashSet(country);
		if("eu".equals(country)){
			countrys.add("de");
			countrys.add("uk");
			countrys.add("fr");
			countrys.add("es");
			countrys.add("it");
		}
		String sql = "SELECT DISTINCT sku FROM psi_sku c WHERE c.`del_flag`='0' and  c.`country` in :p1 AND  CONCAT(c.`product_name`,CASE  WHEN c.`color`='' THEN '' ELSE CONCAT('_',c.color) END )=:p2";
		List<Object> temp = psiInventoryDao.findBySql(sql, new Parameter(countrys,productName_color));
		if(temp.size()>0){
			sql="SELECT a.`shipment_id`,a.`shipment_name`,b.`quantity_shipped`,b.`sku`,a.`shipment_status` FROM psi_fba_inbound a ,psi_fba_inbound_item b" +
					"	 WHERE a.`id` = b.`fba_inbound_id` and a.shipped_date is  null AND a.`shipment_status` IN ('WORKING','SHIPPED','IN_TRANSIT','DELIVERED','CHECKED_IN','RECEIVING') AND a.`country` in :p1 AND b.`sku` IN :p2 ";
			list = psiInventoryDao.findBySql(sql, new Parameter(countrys,temp));
		}
		List<FbaInboundDto> rs = Lists.newArrayList();
		for(Object[] objs:list){
			rs.add(new FbaInboundDto(objs[0].toString(),objs[1].toString(),(Integer)objs[2],0,objs[3].toString(),objs[4].toString(),null));
		}
		return rs;
	}
	

	
	/**
	 * 获取退货订单里的信息
	 * @param  productName
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	public List<AmazonRemovalOrder> getRecallingOrder(String productName_color,String country){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			List<AmazonRemovalOrder> rs = Lists.newArrayList();
			Parameter para = null;
			String sql="";
			if("total".equals(country)){
				sql="SELECT a.`amazon_order_id`,a.`country`,a.`create_date`,b.`sellersku`,b.`in_process_qty`,b.`requested_qty`,b.`disposition` FROM amazoninfo_removal_order AS a ,amazoninfo_removal_orderitem AS b WHERE a.id = b.`order_id` AND a.`order_status`='Pending' AND a.`order_type`='Return' AND b.`in_process_qty`>0 AND b.`product_name`=:p1 ORDER BY FIELD(b.`disposition`,'Sellable','Unsellable') , a.`create_date` DESC ";
				para = new Parameter(productName_color);
			}else if("eu".equals(country)){
				Set<String> countrys = Sets.newHashSet();
				countrys.add("de");
				countrys.add("uk");
				countrys.add("fr");
				countrys.add("es");
				countrys.add("it");
				sql="SELECT a.`amazon_order_id`,a.`country`,a.`create_date`,b.`sellersku`,b.`in_process_qty`,b.`requested_qty`,b.`disposition` FROM amazoninfo_removal_order AS a ,amazoninfo_removal_orderitem AS b WHERE a.id = b.`order_id` AND a.`order_status`='Pending' AND a.`order_type`='Return' AND b.`in_process_qty`>0 AND b.`product_name`=:p1 AND a.`country` IN :p2 ORDER BY FIELD(b.`disposition`,'Sellable','Unsellable') , a.`create_date` DESC";
				para = new Parameter(productName_color,countrys);
			}else{
				sql="SELECT a.`amazon_order_id`,a.`country`,a.`create_date`,b.`sellersku`,b.`in_process_qty`,b.`requested_qty`,b.`disposition` FROM amazoninfo_removal_order AS a ,amazoninfo_removal_orderitem AS b WHERE a.id = b.`order_id` AND a.`order_status`='Pending' AND a.`order_type`='Return' AND b.`in_process_qty`>0 AND b.`product_name`=:p1 AND a.`country` = :p2 ORDER BY FIELD(b.`disposition`,'Sellable','Unsellable') , a.`create_date` DESC";
				para = new Parameter(productName_color,country);
			}
			
			List<Object[]> list = psiInventoryDao.findBySql(sql, para);
			for(Object[] objs:list){
				rs.add(new AmazonRemovalOrder(objs[0].toString(),objs[1].toString() , sdf.format((Date)objs[2]),objs[3].toString(),Integer.parseInt(objs[4].toString()),Integer.parseInt(objs[5].toString()),objs[6].toString()));
			}  
		return rs;
	}
	
	
	
	/**
	 * 获取所有FBA在途信息
	 * @param  productName
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	public Map<String, Integer> getAllFbaTransporttingByNameAndCountry(){
		String sql="SELECT c.productName,SUM(CASE  WHEN a.country IN ('de','fr','uk') AND a.`arrival_date` IS NOT NULL AND a.`arrival_date` <= DATE_ADD(CURDATE(),INTERVAL -30 DAY) THEN 0 ELSE (b.`quantity_shipped`-b.`quantity_received`) END ) AS tranFba FROM psi_fba_inbound a ,psi_fba_inbound_item b,(SELECT CONCAT(e.`product_name`,CASE  WHEN e.`color`='' THEN '' ELSE CONCAT('_',e.color) END ,'_',e.`country`) AS productName,e.`sku`,e.`country` FROM psi_sku e WHERE e.`del_flag`='0')c "+ 
				" WHERE a.`id` = b.`fba_inbound_id` AND a.shipped_date IS NOT NULL  AND a.`country` = c.country AND b.`sku` = c.sku AND a.`shipment_status` IN ('SHIPPED','IN_TRANSIT','DELIVERED','CHECKED_IN','RECEIVING') AND b.`quantity_shipped`>b.`quantity_received` GROUP BY c.productName HAVING tranFba >0  ";
		List<Object[]> list = psiInventoryDao.findBySql(sql);
		Map<String, Integer> map = Maps.newHashMap(); 
		for (Object[] objects : list) {
			map.put(objects[0].toString(),((BigDecimal)objects[1]).intValue());
		}
		return map;
	}
	
	/**
	 * 获取所有FBA在途信息
	 * @param  productName
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	public Map<String, Integer> getFbaTransporttingByName(String productName){
		String sql="SELECT c.productName,SUM(CASE  WHEN a.country IN ('de','fr','uk') AND a.`arrival_date` IS NOT NULL AND a.`arrival_date` <= DATE_ADD(CURDATE(),INTERVAL -30 DAY) THEN 0 ELSE (b.`quantity_shipped`-b.`quantity_received`) END ) AS tranFba FROM psi_fba_inbound a ,psi_fba_inbound_item b,(SELECT CONCAT(e.`product_name`,CASE  WHEN e.`color`='' THEN '' ELSE CONCAT('_',e.color) END ,'_',e.`country`) AS productName,e.`sku`,e.`country` FROM psi_sku e WHERE e.`del_flag`='0' and CONCAT(e.`product_name`,CASE  WHEN e.`color`='' THEN '' ELSE CONCAT('_',e.color) END) = :p1 )c " +
				"WHERE a.`id` = b.`fba_inbound_id` and a.shipped_date is not null AND a.`country` = c.country AND b.`sku` = c.sku AND a.`shipment_status` IN ('SHIPPED','IN_TRANSIT','DELIVERED','CHECKED_IN','RECEIVING') AND b.`quantity_shipped`>b.`quantity_received` GROUP BY c.productName  HAVING tranFba >0 " ;
		List<Object[]> list = psiInventoryDao.findBySql(sql,new Parameter(productName));
		Map<String, Integer> map = Maps.newHashMap(); 
		for (Object[] objects : list) {
			map.put(objects[0].toString(),((BigDecimal)objects[1]).intValue());
		}
		return map;
	}
	
	public Map<String, Integer> getFbaTransporttingByName(Set<String> productName){
		String sql="SELECT c.productName,SUM(CASE  WHEN a.country IN ('de','fr','uk') AND a.`arrival_date` IS NOT NULL AND a.`arrival_date` <= DATE_ADD(CURDATE(),INTERVAL -30 DAY) THEN 0 ELSE (b.`quantity_shipped`-b.`quantity_received`) END ) AS tranFba FROM psi_fba_inbound a ,psi_fba_inbound_item b,(SELECT CONCAT(e.`product_name`,CASE  WHEN e.`color`='' THEN '' ELSE CONCAT('_',e.color) END ,'_',e.`country`) AS productName,e.`sku`,e.`country` FROM psi_sku e WHERE e.`del_flag`='0' and CONCAT(e.`product_name`,CASE  WHEN e.`color`='' THEN '' ELSE CONCAT('_',e.color) END) in (:p1) )c " +
				" WHERE a.`id` = b.`fba_inbound_id` and a.shipped_date is not null AND a.`country` = c.country AND b.`sku` = c.sku AND a.`shipment_status` IN ('SHIPPED','IN_TRANSIT','DELIVERED','CHECKED_IN','RECEIVING') AND b.`quantity_shipped`>b.`quantity_received` GROUP BY c.productName  HAVING tranFba >0 " ;
		List<Object[]> list = psiInventoryDao.findBySql(sql,new Parameter(productName));
		Map<String, Integer> map = Maps.newHashMap(); 
		for (Object[] objects : list) {
			map.put(objects[0].toString(),((BigDecimal)objects[1]).intValue());
		}
		return map;
	}
	
	public Map<String, Integer> getFbaTransporttingByName2(String productName){
		String sql="SELECT c.productName,SUM(CASE  WHEN a.country IN ('de','fr','uk') AND a.`arrival_date` IS NOT NULL AND a.`arrival_date` <= DATE_ADD(CURDATE(),INTERVAL -30 DAY) THEN 0 ELSE (b.`quantity_shipped`-b.`quantity_received`) END ) AS tranFba FROM psi_fba_inbound a ,psi_fba_inbound_item b,(SELECT CONCAT(e.`product_name`,CASE  WHEN e.`color`='' THEN '' ELSE CONCAT('_',e.color) END ,'_',e.`country`) AS productName,e.`sku`,e.`country` FROM psi_sku e WHERE e.`del_flag`='0' and CONCAT(e.`product_name`,CASE  WHEN e.`color`='' THEN '' ELSE CONCAT('_',e.color) END) = :p1 )c " +
				"WHERE a.`id` = b.`fba_inbound_id` and a.shipped_date is not null AND a.`country` = c.country AND b.`sku` = c.sku AND a.`shipment_status` IN ('SHIPPED','IN_TRANSIT','DELIVERED','CHECKED_IN','RECEIVING') AND b.`quantity_shipped`>b.`quantity_received` AND a.`ship_from_address`='CN' GROUP BY c.productName  HAVING tranFba >0 " ;
		List<Object[]> list = psiInventoryDao.findBySql(sql,new Parameter(productName));
		Map<String, Integer> map = Maps.newHashMap(); 
		for (Object[] objects : list) {
			map.put(objects[0].toString(),((BigDecimal)objects[1]).intValue());
		}
		return map;
	}
	
	public Map<String,Map<Date,Integer>> getFbaTransporttingByName(){
		Map<String,Map<Date,Integer>> map=Maps.newHashMap();
		String sql="SELECT c.productName,a.`shipped_date`,SUM(CASE  WHEN a.country IN ('de','fr','uk') AND a.`arrival_date` IS NOT NULL AND a.`arrival_date` <= DATE_ADD(CURDATE(),INTERVAL -30 DAY) THEN 0 ELSE (b.`quantity_shipped`-b.`quantity_received`) END ) AS tranFba FROM psi_fba_inbound a ,psi_fba_inbound_item b,(SELECT CONCAT(e.`product_name`,CASE  WHEN e.`color`='' THEN '' ELSE CONCAT('_',e.color) END ,'_',e.`country`) AS productName,e.`sku`,e.`country` FROM psi_sku e WHERE e.`del_flag`='0' )c " +
					"WHERE a.`id` = b.`fba_inbound_id` and a.shipped_date is not null AND a.`country` = c.country AND b.`sku` = c.sku AND a.`shipment_status` IN ('SHIPPED','IN_TRANSIT','DELIVERED','CHECKED_IN','RECEIVING') AND b.`quantity_shipped`>b.`quantity_received` AND a.`ship_from_address`!='CN'  GROUP BY c.productName,a.`shipped_date`  HAVING tranFba >0 " ;
		List<Object[]> list = psiInventoryDao.findBySql(sql);
		for (Object[] obj: list) {
			Map<Date,Integer> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[0].toString(), temp);
			}
			temp.put(DateUtils.addDays((Date)obj[1],5), ((BigDecimal)obj[2]).intValue());
		}
		return map;
	}
	
	
	public Map<String,Map<Date,Integer>> getFbaTransportting(){
		Map<String,Map<Date,Integer>> map=Maps.newHashMap();
		String sql="SELECT c.productName,a.`shipped_date`,SUM(CASE  WHEN a.country IN ('de','fr','uk') AND a.`arrival_date` IS NOT NULL AND a.`arrival_date` <= DATE_ADD(CURDATE(),INTERVAL -30 DAY) THEN 0 ELSE (b.`quantity_shipped`-b.`quantity_received`) END ) AS tranFba FROM psi_fba_inbound a ,psi_fba_inbound_item b,(SELECT CONCAT(e.`product_name`,CASE  WHEN e.`color`='' THEN '' ELSE CONCAT('_',e.color) END ,'_',e.`country`) AS productName,e.`sku`,e.`country` FROM psi_sku e WHERE e.`del_flag`='0' )c " +
					"WHERE a.`id` = b.`fba_inbound_id` and a.shipped_date is not null AND a.`country` = c.country AND b.`sku` = c.sku AND a.`shipment_status` IN ('SHIPPED','IN_TRANSIT','DELIVERED','CHECKED_IN','RECEIVING') AND b.`quantity_shipped`>b.`quantity_received` AND a.`ship_from_address`!='CN'  GROUP BY c.productName,a.`shipped_date`  HAVING tranFba >0 " ;
		List<Object[]> list = psiInventoryDao.findBySql(sql);
		for (Object[] obj: list) {
			Map<Date,Integer> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(obj[0].toString(), temp);
			}
			temp.put(DateUtils.addDays((Date)obj[1],5), ((BigDecimal)obj[2]).intValue());
		}
		return map;
	}
	
	
	public Map<String,Map<Date,Integer>> getFBATransportQuantity(){
		Map<String,Map<Date,Integer>> map=Maps.newHashMap();

		String sql="SELECT r.`transport_type`,r.`pick_up_date`,a.country,c.productName,SUM(CASE  WHEN a.country IN ('de','fr','uk') AND a.`arrival_date` IS NOT NULL AND a.`arrival_date` <= DATE_ADD(CURDATE(),INTERVAL -30 DAY) THEN 0 ELSE (b.`quantity_shipped`-b.`quantity_received`) END ) AS tranFba,r.model FROM psi_fba_inbound a ,psi_fba_inbound_item b,(SELECT CONCAT(e.`product_name`,CASE  WHEN e.`color`='' THEN '' ELSE CONCAT('_',e.color) END ,'_',e.`country`) AS productName,e.`sku`,e.`country` FROM psi_sku e WHERE e.`del_flag`='0')c ,psi_transport_order r " +
			" WHERE r.shipment_id=a.shipment_id and a.`id` = b.`fba_inbound_id` and a.shipped_date is not null AND a.`country` = c.country AND b.`sku` = c.sku AND a.`shipment_status` IN ('SHIPPED','IN_TRANSIT','DELIVERED','CHECKED_IN','RECEIVING') AND b.`quantity_shipped`>b.`quantity_received` AND a.`ship_from_address`='CN' GROUP BY c.productName, a.`country`  HAVING tranFba >0";
		
		
		
		List<Object[]> list=psiInventoryDao.findBySql(sql);
		for (Object[] obj: list) {
		//	String type=obj[0].toString();
			Date pickUpDate=(Date)obj[1];
			if(pickUpDate==null){
				continue;
			}
			String country=obj[2].toString();
			String name=obj[3].toString();

					Integer quantity=Integer.parseInt(obj[4].toString());
					String model=obj[5].toString();
					Map<Date,Integer> temp=map.get(name+"_"+country);
					if(temp==null){
						temp=Maps.newHashMap();
						map.put(name+"_"+country, temp);
					}
					if("0".equals(model)){//air
						pickUpDate=DateUtils.addDays(pickUpDate,PsiConfig.get(country).getFbaBySky());
					}else if("1".equals(model)){//sea
						pickUpDate=DateUtils.addDays(pickUpDate,PsiConfig.get(country).getFbaBySea());
					}else{
						pickUpDate=DateUtils.addDays(pickUpDate,PsiConfig.get(country).getFbaByExpress());
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

		}
		return map;
	}
	
	public Map<String, Integer> getFbaTransporttingReceiveByName(String productName){
		String sql="SELECT c.productName,SUM(b.`quantity_shipped`-b.`quantity_received`) AS tranFba FROM psi_fba_inbound a ,psi_fba_inbound_item b,(SELECT CONCAT(e.`product_name`,CASE  WHEN e.`color`='' THEN '' ELSE CONCAT('_',e.color) END ,'_',e.`country`) AS productName,e.`sku`,e.`country` FROM psi_sku e WHERE e.`del_flag`='0' and CONCAT(e.`product_name`,CASE  WHEN e.`color`='' THEN '' ELSE CONCAT('_',e.color) END) = :p1 )c " +
				"WHERE a.`id` = b.`fba_inbound_id` and a.shipped_date is not null AND a.`country` = c.country AND b.`sku` = c.sku AND a.`shipment_status`='RECEIVING' AND b.`quantity_shipped`>b.`quantity_received` GROUP BY c.productName  HAVING tranFba >0 " ;
		List<Object[]> list = psiInventoryDao.findBySql(sql,new Parameter(productName));
		Map<String, Integer> map = Maps.newHashMap(); 
		for (Object[] objects : list) {
			map.put(objects[0].toString(),((BigDecimal)objects[1]).intValue());
		}
		return map;
	}
	
	public Map<String, Integer> getFbaTransporttingReceiveByName(){
		String sql="SELECT c.productName,SUM(b.`quantity_shipped`-b.`quantity_received`) AS tranFba FROM psi_fba_inbound a ,psi_fba_inbound_item b,(SELECT CONCAT(e.`product_name`,CASE  WHEN e.`color`='' THEN '' ELSE CONCAT('_',e.color) END ,'_',e.`country`) AS productName,e.`sku`,e.`country` FROM psi_sku e WHERE e.`del_flag`='0' )c " +
				"WHERE a.`id` = b.`fba_inbound_id` and a.shipped_date is not null AND a.`country` = c.country AND b.`sku` = c.sku AND a.`shipment_status`='RECEIVING' AND b.`quantity_shipped`>b.`quantity_received` GROUP BY c.productName  HAVING tranFba >0 " ;
		List<Object[]> list = psiInventoryDao.findBySql(sql);
		Map<String, Integer> map = Maps.newHashMap(); 
		for (Object[] objects : list) {
			map.put(objects[0].toString(),((BigDecimal)objects[1]).intValue());
		}
		return map;
	}
	
	
	public Map<String, Integer> getAllFbaTransporttingByNameAndCountry(String country){
		String sql="SELECT c.productName,SUM(CASE  WHEN a.country IN ('de','fr','uk') AND a.`arrival_date` IS NOT NULL AND a.`arrival_date` <= DATE_ADD(CURDATE(),INTERVAL -30 DAY) THEN 0 ELSE (b.`quantity_shipped`-b.`quantity_received`) END ) AS tranFba  FROM psi_fba_inbound a ,psi_fba_inbound_item b,(SELECT CONCAT(e.`product_name`,CASE  WHEN e.`color`='' THEN '' ELSE CONCAT('_',e.color) END ,'_',e.`country`) AS productName,e.`sku`,e.`country` FROM psi_sku e WHERE e.`del_flag`='0')c " +
				"WHERE a.`id` = b.`fba_inbound_id` and a.shipped_date is not null and a.`country` in :p1 AND a.`country` = c.country AND b.`sku` = c.sku AND a.`shipment_status` IN ('SHIPPED','IN_TRANSIT','DELIVERED','CHECKED_IN','RECEIVING') AND b.`quantity_shipped`>b.`quantity_received` GROUP BY c.productName  HAVING tranFba >0 ";
		List<Object[]> list = null;
		if("eu".equals(country)){
			list=psiInventoryDao.findBySql(sql,new Parameter(Lists.newArrayList("de","fr","it","es","uk"))); 
		}else{
			sql="SELECT c.productName,SUM(CASE  WHEN a.country IN ('de','fr','uk') AND a.`arrival_date` IS NOT NULL AND a.`arrival_date` <= DATE_ADD(CURDATE(),INTERVAL -30 DAY) THEN 0 ELSE (b.`quantity_shipped`-b.`quantity_received`) END ) AS tranFba  FROM psi_fba_inbound a ,psi_fba_inbound_item b,(SELECT CONCAT(e.`product_name`,CASE  WHEN e.`color`='' THEN '' ELSE CONCAT('_',e.color) END ,'_',e.`country`) AS productName,e.`sku`,e.`country` FROM psi_sku e WHERE e.`del_flag`='0')c " +
					"WHERE a.`id` = b.`fba_inbound_id` and a.shipped_date is not null and a.`country` = :p1 AND a.`country` = c.country AND b.`sku` = c.sku AND a.`shipment_status` IN ('SHIPPED','IN_TRANSIT','DELIVERED','CHECKED_IN','RECEIVING') AND b.`quantity_shipped`>b.`quantity_received` GROUP BY c.productName HAVING tranFba >0";
			list=psiInventoryDao.findBySql(sql,new Parameter(country)); 
		}
		Map<String, Integer> map = Maps.newHashMap(); 
		for (Object[] objects : list) {
			map.put(objects[0].toString(),((BigDecimal)objects[1]).intValue());
		}
		return map;
	}

	/**
	 * 获取在产产品信息
	 * @param  productName
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	public Map<String,PsiInventoryInnerDto> getTransporttingByNameAndCountrySum(String productName_color,String country,String warehouseCode){
		Map<String,PsiInventoryInnerDto> innerDto=Maps.newLinkedHashMap();
		//String sql="SELECT b.sku, SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END)) FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND a.`transport_sta` IN('1','2','3','4') AND a.`to_store` = (SELECT p.id FROM psi_stock AS p WHERE p.`type`='0' AND  p.`countrycode`=:p2)  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1 AND b.`country_code` = :p3 GROUP BY b.sku ";
		
		String sql="select b.sku,sum(quantity) from (SELECT b.sku, SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END)) quantity FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND a.`transport_sta` IN('1','2','3','4') AND a.`to_store` = (SELECT p.id FROM psi_stock AS p WHERE p.`type`='0' AND  p.`countrycode`=:p2)  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1 AND b.`country_code` = :p3 GROUP BY b.sku "+
				" union all SELECT b.sku, SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END)) FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND a.`transport_sta` IN('1','2','3','4') AND a.`to_store` = (SELECT p.id FROM psi_stock AS p WHERE p.`type`='0' AND  p.`countrycode`=:p2)  AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) =:p1 AND b.`country_code` = :p3 GROUP BY b.sku ) b group by b.sku";
		
		List<Object[]> list=this.psiInventoryDao.findBySql(sql, new Parameter(productName_color,warehouseCode,country));
		for(Object[] object:list){  
			String sku = object[0].toString();
			innerDto.put(sku,new PsiInventoryInnerDto(null, null, ((BigDecimal)object[1]).intValue(), null,null,sku,null,null,null,null,null,null,null,null,null));
		}
		return innerDto;
	}
	
	/**
	 * 获取在产产品信息
	 * @param  productName
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	public int getTransporttingBySku(String sku){
		//String sql="SELECT SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END)) FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND a.`transport_sta` IN('1','2','3','4')  and b.sku = :p1 ";
		
		String sql="select sum(quantity) from  (SELECT SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END)) quantity FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND a.`transport_sta` IN('1','2','3','4')  and b.sku = :p1 "+
				" union all SELECT SUM(b.`shipped_quantity`- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END)) FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND a.`transport_sta` IN('1','2','3','4')  and b.sku = :p1 ) b ";
		
		
		List<Object> list=this.psiInventoryDao.findBySql(sql, new Parameter(sku));
		BigDecimal rs = (BigDecimal)list.get(0);
		if(rs!=null){
			return rs.intValue();
		}
		return 0;
	}
	
	
	/**
	 * 获取在产产品信息
	 * @param  productName
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	public Map<String,PsiInventoryInnerDto> getInventoryByNameCountryWarehouseCodeSum(String productName_color,String country,String warehouseCode){
		Map<String,PsiInventoryInnerDto> innerDto=Maps.newLinkedHashMap();
		String sql="SELECT a.sku,a.`new_quantity` FROM psi_inventory AS a ,psi_stock AS b WHERE a.`warehouse_id`=b.`id` AND CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END )=:p1 AND a.`country_code` = :p2 AND b.`countrycode` =:p3 ";
		List<Object[]> list=this.psiInventoryDao.findBySql(sql, new Parameter(productName_color,country,warehouseCode));
		for(Object[] object:list){
			String sku = object[0].toString();
			innerDto.put(sku,new PsiInventoryInnerDto(null, null, (Integer)object[1], null,null,object[0].toString(),null,null,null,null,null,null,null,null,null));
		}
		return innerDto;
	}
	
	/**
	 * 获取在产产品信息
	 * @param  productName
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	public List<Object> getSkusInStock(String productName_color,String country){
		String sql="SELECT DISTINCT a.sku FROM psi_sku AS a WHERE  CONCAT(a.`product_name`,CASE  WHEN a.color='' THEN '' ELSE CONCAT('_',a.color) END )=:p1 AND a.`country` = :p2 AND a.`del_flag` = '0' AND (a.`use_barcode`='1' OR a.`sku` IN (SELECT DISTINCT b.sku FROM psi_inventory AS b  WHERE  CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END )=:p1 AND b.`country_code` = :p2 ))";
		List<Object> list=this.psiInventoryDao.findBySql(sql, new Parameter(productName_color,country));
		return list;
	}
	
	/**
	 * 获取在产产品信息
	 * @param  productName
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	public int getInventoryBySku(String sku,String warehouseCode){
		String sql="SELECT a.`new_quantity` FROM psi_inventory AS a ,psi_stock AS b WHERE a.`warehouse_id`=b.`id` AND a.sku =:p1 AND b.`countrycode` =:p2 ";
		List<Object> list=this.psiInventoryDao.findBySql(sql, new Parameter(sku,warehouseCode));
		if(list.size()>0){
			Integer rs = (Integer)list.get(0);
			if(rs!=null){
				return rs;
			}
		}
		return 0;
	}
	
	/**
	 *获取产品MOQ价格 
	 */
	public Map<String,Map<String,Object>> getProductsMoqAndPrice(){
		Map<String,Map<String,Object>> productsMoqAndPrice = this.tieredPriceService.getMoqPriceBaseMoqNoSupplier();
		return productsMoqAndPrice;
	}
	
	/**
	 * 获取pi价格，月份格式为yyyy-MM,同时支持eu、de
	 * @return
	 */
	public Map<String, Float> getPiPrice(String country,Date month){
		String montnStr = "";
		Date d2017 = new Date(117, 0,1);
		if(month.before(d2017)){
			montnStr = "2016-12";
		}else{
			montnStr = DateUtils.getDate(month, "yyyy-MM");
		}
		String sql = "SELECT CONCAT(CONCAT(brand,' ',a.model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) proName "+
				" ,p.`price` FROM psi_product a JOIN mysql.help_topic b  "+
				"	ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) JOIN amazoninfo_pi_price p ON p.`model` = a.`model` WHERE a.`del_flag` = '0' AND p.`country` = :p1 AND p.`datadate` = :p2   AND p.`price` IS NOT NULL ";
		if("eu".equals(country)){
			country = "de";
		}
		if("us".equals(country)){
			country = "com";
		}
		List<Object[]> list=psiInventoryDao.findBySql(sql,new Parameter(country, montnStr));
		Map<String, Float> rs = Maps.newHashMap();
		for (Object[] objects : list) {
			rs.put(objects[0].toString(), Float.parseFloat(objects[1].toString()));
		}
		return rs;
	}
	
	
	
	
	public List<PsiInventoryWarn> getInventoryWarnList(){
		List<PsiProduct> list =psiProductService.findAllByCountry(null,null);
		Map<String, PsiInventoryTotalDto> producting = getProducingQuantity();
		Map<String, PsiInventoryTotalDto> transportting = getTransporttingQuantity().get("1");
		Map<String, PsiInventoryTotalDtoByInStock> inventorys =getInventoryQuantity();
		Map<String, PsiInventoryFba>  fbas = getAllProductFbaInfo();
		Map<String,Map<String,Object>> productsMoqAndPrice = getProductsMoqAndPrice();
		Map<String,ProductSalesInfo> fancha = productSalesInfoService.findAll();

        List<PsiInventoryWarn> warnList=new ArrayList<PsiInventoryWarn>();
            String keyStock="";
		    List<String> sortCountry=Lists.newArrayList("de","fr","it","es","uk","com","ca","jp");
        	for (PsiProduct psiProduct : list) {
        		List<String> nameWithColorList=psiProduct.getProductNameWithColor();
        		List<String> countryList = Arrays.asList(psiProduct.getPlatform().split(","));
        		for (String nameWithColor : nameWithColorList) {
        				for (String saleCountry : sortCountry) {
    		                if(countryList.contains(saleCountry)){
    		                	PsiInventoryWarn warn=new PsiInventoryWarn();
    	        				if("fr,de,uk,es,it".contains(saleCountry)){
    	        					keyStock="DE";
    	        				}else if("com,ca".contains(saleCountry)){
    	        					keyStock="US";
    	        				}
    	        				String key=nameWithColor+"_"+saleCountry;
    		        			int total=0;
    		        			warn.setProductName(nameWithColor);//产品
    		        			warn.setCreateUserEmail(psiProduct.getCreateUser().getEmail());// 邮箱
    		        			if(productsMoqAndPrice.get(nameWithColor)!=null&&productsMoqAndPrice.get(nameWithColor).get("moq")!=null){//MOQ
    				        		warn.setMoq((Integer) productsMoqAndPrice.get(nameWithColor).get("moq"));
    				        	}else{
    				        		warn.setMoq(0);
    				        	}
    		        			warn.setCountry(saleCountry);//国家
    		        			

    							if(producting.get(nameWithColor)==null||producting.get(nameWithColor).getInventorys()==null||producting.get(nameWithColor).getInventorys().get(saleCountry)==null||producting.get(nameWithColor).getInventorys().get(saleCountry).getQuantity()==null||producting.get(nameWithColor).getInventorys().get(saleCountry).getQuantity()<=0){//产
    								warn.setProducting(0);
    							}else{
    								warn.setProducting(producting.get(nameWithColor).getInventorys().get(saleCountry).getQuantity());
    							    total+=producting.get(nameWithColor).getInventorys().get(saleCountry).getQuantity();
    							}  
    							if(inventorys.get(nameWithColor)==null||inventorys.get(nameWithColor).getInventorys()==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry)==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory()==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get("CN")==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get("CN").getNewQuantity()==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get("CN").getNewQuantity()<=0){
    								warn.setChinaQuantity(0);//中国仓
    							}else{
    								warn.setChinaQuantity(inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get("CN").getNewQuantity());
    								total+=inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get("CN").getNewQuantity();
    							}
    							if(transportting.get(nameWithColor)==null||transportting.get(nameWithColor).getInventorys()==null||transportting.get(nameWithColor).getInventorys().get(saleCountry)==null||transportting.get(nameWithColor).getInventorys().get(saleCountry).getQuantity()==null||transportting.get(nameWithColor).getInventorys().get(saleCountry).getQuantity()<=0){
    								 warn.setTransportting(0);//途
    							}else{
    								 warn.setTransportting(transportting.get(nameWithColor).getInventorys().get(saleCountry).getQuantity());
    								 total+=transportting.get(nameWithColor).getInventorys().get(saleCountry).getQuantity();
    							}
                                int seaInventory=0;
                                int allTotal=total;
    							if(inventorys.get(nameWithColor)==null||inventorys.get(nameWithColor).getInventorys()==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry)==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory()==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get("DE")==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get("DE").getNewQuantity()==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get("DE").getNewQuantity()<=0){
    							}else{
    								seaInventory+=inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get("DE").getNewQuantity();
    								total+=inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get("DE").getNewQuantity();
    							}
    							if(inventorys.get(nameWithColor)==null||inventorys.get(nameWithColor).getInventorys()==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry)==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory()==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get("US")==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get("US").getNewQuantity()==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get("US").getNewQuantity()<=0){
    							}else{
    								seaInventory+=inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get("US").getNewQuantity();
    								total+=inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get("US").getNewQuantity();
    							}
    							int deNew=0;
    							if(inventorys.get(nameWithColor)==null||inventorys.get(nameWithColor).getInventorys()==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry)==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory()==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get(keyStock)==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get(keyStock).getNewQuantity()==null||inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get(keyStock).getNewQuantity()<=0){
    							}else{
    							     deNew=inventorys.get(nameWithColor).getInventorys().get(saleCountry).getQuantityInventory().get(keyStock).getNewQuantity();
    							}
    							allTotal=allTotal+deNew;
    							if(seaInventory>0){//海外仓
    								warn.setSeaQuantity(seaInventory);
    							}else{
    								warn.setSeaQuantity(0);
    							}
    			        		
    			                if(fbas.get(key)==null||fbas.get(key).getTotal()<=0){
    								warn.setFbaTotal(0);//FBA总
    							}else{
    								warn.setFbaTotal(fbas.get(key).getTotal());
    								total+=fbas.get(key).getTotal();
    							}
    			         
    			            	if(total>0){//总库存
    			        			warn.setTotal(total);
    			        		}else{
    			        			warn.setTotal(0);
    			        		}
    			            	
    			          
    			            	   double safeDay=0;
    								if(fancha.get(key)!=null&&fancha.get(key).getForecastPreiodAvg()!=null&&fancha.get(key).getForecastPreiodAvg()>0&&fancha.get(key).getPeriodSqrt()!=null&&fancha.get(key).getVariance()!=null){
    									safeDay=fancha.get(key).getPeriodSqrt()*fancha.get(key).getVariance()*2.33/fancha.get(key).getForecastPreiodAvg();
    								}else if(fancha.get(key)!=null&&fancha.get(key).getDay31Sales()!=null&&fancha.get(key).getDay31Sales()>0&&fancha.get(key).getPeriodSqrt()!=null&&fancha.get(key).getVariance()!=null&&fancha.get(key).getDay31Sales()/31.0>0){
    									safeDay=fancha.get(key).getPeriodSqrt()*fancha.get(key).getVariance()*2.33/(fancha.get(key).getDay31Sales()/31.0);
    								}
    								
    			            	   
    			            	
    				            	if(fancha.get(key)!=null&&fancha.get(key).getPeriod()>0){//周期
    									warn.setPreiod(fancha.get(key).getPeriod());
    								}else{
    									warn.setPreiod(0);
    								}
    							
    			            	   if(fancha.get(key)!=null&&fancha.get(key).getDay31Sales()!=null&&fancha.get(key).getDay31Sales()>0){//31日销
    			                    	warn.setDay31Sales(fancha.get(key).getDay31Sales());
    								}else{
    									warn.setDay31Sales(0);
    								}
    			            	   
    								if(fancha.get(key)==null||fancha.get(key).getForecastAfterPreiodSalesByMonth()==null||fancha.get(key).getForecastAfterPreiodSalesByMonth()<=0){
    									warn.setForecastAfterPreiodSales(0);//销售期预月销
    								}else{
    									warn.setForecastAfterPreiodSales((int)Math.round(fancha.get(key).getForecastAfterPreiodSalesByMonth()));
    								}
    								
    								double safe=0;//量(安全库存)
    								if(fancha.get(key)==null||MathUtils.roundUp(fancha.get(key).getPeriodSqrt()*fancha.get(key).getVariance()*2.33)<=0){
    									warn.setSafeSales(0);
    								}else{
    									warn.setSafeSales(MathUtils.roundUp(fancha.get(key).getPeriodSqrt()*fancha.get(key).getVariance()*2.33));
    								    safe=MathUtils.roundUp(fancha.get(key).getPeriodSqrt()*fancha.get(key).getVariance()*2.33);
    								}
    								double point=0;
    								
    								if(safeDay>0){//天(安全库存)
    									warn.setSafeDay(MathUtils.roundUp(safeDay));
    								}else{
    									warn.setSafeDay(0);
    								}
    								
    								if(fancha.get(key)!=null&&fancha.get(key).getForecastPreiodAvg()!=null&&fancha.get(key).getForecastPreiodAvg()>0){
    									point=fancha.get(key).getForecastPreiodAvg()*fancha.get(key).getPeriod()+safe;
    								}else if(fancha.get(key)!=null&&fancha.get(key).getDay31Sales()!=null&&fancha.get(key).getDay31Sales()>0){
    									point=(fancha.get(key).getDay31Sales()/31.0)*fancha.get(key).getPeriod()+safe;
    								}
    								if(point>0){//下单点
    									warn.setPoint((int)Math.round(point));
    								}else{
    									warn.setPoint(0);
    								}
    								
    								double jy=allTotal+(fbas.get(key)==null?0:fbas.get(key).getTotal())-point;
    							    if(jy!=0){//结余
    									warn.setBalance((int)Math.round(jy));
    								}else{
    									warn.setBalance(0);
    								}
    								
    							    double sale=0;
    								if(fancha.get(key)!=null&&fancha.get(key).getForecastAfterPreiodSalesByMonth()!=null&&fancha.get(key).getForecastAfterPreiodSalesByMonth()>0){
    									   sale=jy/(fancha.get(key).getForecastAfterPreiodSalesByMonth()/31);
    								}else if(fancha.get(key)!=null&&fancha.get(key).getDay31Sales()!=null&&fancha.get(key).getDay31Sales()>0){
    									   sale=jy/(fancha.get(key).getDay31Sales()/31.0);
    								}
    								warn.setSaleDay((int)Math.round(sale));
    								
    								double saleJy=0;
    								if(fancha.get(key)!=null&&fancha.get(key).getForecastAfterPreiodSalesByMonth()!=null){//下单量
    									saleJy=fancha.get(key).getForecastAfterPreiodSalesByMonth();
    									if(MathUtils.roundUp((saleJy-jy)/psiProduct.getPackQuantity())*psiProduct.getPackQuantity()>0){
    										warn.setOrderSales(MathUtils.roundUp((saleJy-jy)/psiProduct.getPackQuantity())*psiProduct.getPackQuantity());
    									}else{
    										warn.setOrderSales(0);
    									}
    								}else if(fancha.get(key)!=null){
    									saleJy=fancha.get(key).getDay31Sales();
    									if(MathUtils.roundUp((saleJy-jy)/psiProduct.getPackQuantity())*psiProduct.getPackQuantity()>0){
    										warn.setOrderSales(MathUtils.roundUp((saleJy-jy)/psiProduct.getPackQuantity())*psiProduct.getPackQuantity());
    									}else{
    										warn.setOrderSales(0);
    									}
    								}else{
    									warn.setOrderSales(0);
    								}
    							warnList.add(warn);	
    		                }
    					
    					}
        			
        		}
			}
        	
			return warnList;
	}
	
	/**
	 *查询在途的sku数量 
	 * 
	 */
	public 	Map<String,Integer> getTranSkuQuantity(boolean isShip,Integer warehouseId){
		Map<String,Integer> tranMap = Maps.newHashMap();
		String sql="";
		if(isShip){
			sql ="SELECT CASE WHEN b.`color_code`='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END AS aa,b.`country_code`,SUM(b.`quantity`) FROM "+(warehouseId.intValue()==21?" psi_transport_order AS a,psi_transport_order_item AS b ":" lc_psi_transport_order AS a,lc_psi_transport_order_item AS b ")+"  WHERE a.`id`=b.`transport_order_id` AND a.`transport_sta` IN ('1','2','3','4') AND b.`del_flag`='0' AND a.`transport_type`='0' GROUP BY b.`product_name`,b.`color_code`,b.`country_code`";
//			sql ="select aa,country_code,sum(quantity) from  (SELECT CASE WHEN b.`color_code`='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END AS aa,b.`country_code`,SUM(b.`quantity`) quantity FROM psi_transport_order AS a,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND a.`transport_sta` IN ('1','2','3','4') AND b.`del_flag`='0' AND a.`transport_type`='0' GROUP BY b.`product_name`,b.`color_code`,b.`country_code` "+
//			" union all SELECT CASE WHEN b.`color_code`='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END AS aa,b.`country_code`,SUM(b.`quantity`) FROM lc_psi_transport_order AS a,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND a.`transport_sta` IN ('1','2','3','4') AND b.`del_flag`='0' AND a.`transport_type`='0' GROUP BY b.`product_name`,b.`color_code`,b.`country_code` ) b GROUP BY aa,b.`country_code` ";
		}else{
			sql ="SELECT CASE WHEN b.`color_code`='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END AS aa,b.`country_code`,SUM(b.`quantity`) FROM "+(warehouseId.intValue()==21?" psi_transport_order AS a,psi_transport_order_item AS b ":" lc_psi_transport_order AS a,lc_psi_transport_order_item AS b ")+"  WHERE a.`id`=b.`transport_order_id` AND a.`transport_sta` ='0' AND b.`del_flag`='0'  GROUP BY b.`product_name`,b.`color_code`,b.`country_code`";
//			sql ="select aa,country_code,sum(quantity) from  (SELECT CASE WHEN b.`color_code`='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END AS aa,b.`country_code`,SUM(b.`quantity`) quantity FROM psi_transport_order AS a,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND a.`transport_sta` ='0' AND b.`del_flag`='0'  GROUP BY b.`product_name`,b.`color_code`,b.`country_code` " +
//			" union all SELECT CASE WHEN b.`color_code`='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END AS aa,b.`country_code`,SUM(b.`quantity`) FROM lc_psi_transport_order AS a,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND a.`transport_sta` ='0' AND b.`del_flag`='0'  GROUP BY b.`product_name`,b.`color_code`,b.`country_code` ) b GROUP BY aa,b.`country_code";
			
		
		}
		List<Object[]> list = this.psiInventoryDao.findBySql(sql);
		for(Object[] objs:list){
			tranMap.put(objs[0]+",,"+objs[1], Integer.parseInt(objs[2].toString()));
		}
		return tranMap;
	}
	
	

	
	public Object[] getForecastInfo(String country,String sku){
		String sql="SELECT ifnull(d.delivery_date,d.shipped_date),t.`quantity_shipped`,(CASE WHEN d.`delivery_date` IS NOT NULL THEN '0' ELSE '1' END),d.`ship_from_address`,d.shipment_status,d.shipment_id FROM psi_fba_inbound d  JOIN psi_fba_inbound_item t ON d.id=t.`fba_inbound_id` "+
                   " WHERE d.`country`=:p1 AND t.sku=:p2 ORDER BY d.`create_date` DESC ";
		List<Object[]> list=psiInventoryDao.findBySql(sql,new Parameter(country,sku));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public Integer getNewQuantity(String country,String sku,Integer wareHouseId){
		String sql="SELECT new_quantity FROM psi_inventory WHERE country_code=:p1 AND sku=:p2 AND warehouse_id=:p3 order by update_date desc";
		List<Integer> list=psiInventoryDao.findBySql(sql,new Parameter(country,sku,wareHouseId));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public Integer getNewQuantityByName(String country,String productName,Integer wareHouseId){
		String sql="SELECT sum(new_quantity) FROM psi_inventory b WHERE country_code=:p1 AND CONCAT(b.`product_name`,CASE WHEN b.`color_code`!='' THEN CONCAT ('_',b.`color_code`) ELSE '' END)=:p2 "+
              " AND warehouse_id=:p3 and new_quantity>0 GROUP BY CONCAT(b.`product_name`,CASE WHEN b.`color_code`!='' THEN CONCAT ('_',b.`color_code`) ELSE '' END) ";
		List<Object> list=psiInventoryDao.findBySql(sql,new Parameter(country,productName,wareHouseId));
		if(list!=null&&list.size()>0){
			return ((BigDecimal)list.get(0)).intValue();
		}
		return null;
	}
	
	
	/**
	 * 查询sku在各仓库数量
	 * @param  productName
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	public Map<Integer,Integer> getInventoryBySku(String sku){
		Map<Integer,Integer> resMap = Maps.newHashMap();
		String sql="SELECT a.`warehouse_id`,a.`new_quantity` FROM psi_inventory AS a WHERE  a.sku =:p1 ";
		List<Object[]> list=this.psiInventoryDao.findBySql(sql, new Parameter(sku));
		if(list.size()>0){
			for(Object[] obj:list){
				Integer warehouseId = (Integer)obj[0];
				Integer quantity = (Integer)obj[1];
				resMap.put(warehouseId, quantity);
			}
		}
		return resMap;
	}
	
	public Map<String, String> isPanEuMap (){
		String sql = "SELECT DISTINCT a.`product_name` FROM amazoninfo_pan_eu a WHERE a.`is_pan_eu` = '1' and a.`product_name` is not null";
		Map<String, String> rs = Maps.newHashMap();
		List<Object> list = psiInventoryDao.findBySql(sql);
		for (Object object : list) {
			rs.put(object.toString(), "1");
		}
		return rs;
	}
	
	/**
	 *实时算库存容量 
	 */
	public Float getTimelyCapacity(Integer warehouseId){
		String sql="SELECT SUM(aa.cap) FROM (SELECT a.`warehouse_id`,CEILING(SUM(a.`new_quantity`+a.`offline_quantity`)/b.`pack_quantity`)*b.`box_volume` AS cap FROM psi_inventory AS a,psi_product AS b WHERE a.`product_id`=b.`id` AND a.`warehouse_id`=:p1  GROUP BY a.`warehouse_id`,a.`product_id`) AS aa ";
		List<Object> list = psiInventoryDao.findBySql(sql,new Parameter(warehouseId));
		if (list != null && list.size() > 0 && list.get(0) != null) {
			return Float.parseFloat(list.get(0).toString());
		}
		return 0f;
	}
	
	/**
	 *实时算库存容量 
	 */
	public Map<Integer,Float> getTimelyCapacity(){
		String sql="SELECT aa.warehouse_id,SUM(aa.cap) FROM (SELECT a.`warehouse_id`,CEILING(SUM(a.`new_quantity`+a.`offline_quantity`)/b.`pack_quantity`)*b.`box_volume` AS cap FROM psi_inventory AS a,psi_product AS b WHERE a.`product_id`=b.`id`  GROUP BY a.`warehouse_id`,a.`product_id`) AS aa GROUP BY aa.warehouse_id";
		Map<Integer,Float> rs = Maps.newHashMap();
		List<Object[]> list = psiInventoryDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				rs.put(Integer.parseInt(obj[0].toString()), Float.parseFloat(obj[1].toString()));
			}
		}
		return rs;
	}
	
	
	
	public Map<String,Map<String,Integer>> getLocalInventory(){
		Map<String,Map<String,Integer>> map=Maps.newHashMap();
		String sql="SELECT t.warehouse_id,CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) NAME,SUM(t.new_quantity) FROM psi_inventory t "+
                    " WHERE t.warehouse_id IN (120,19,147) GROUP BY t.warehouse_id,NAME ";
		List<Object[]> list=this.psiInventoryDao.findBySql(sql);
		if(list.size()>0){
			for(Object[] obj:list){
				Map<String,Integer> temp=map.get(obj[0].toString());
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(obj[0].toString(),temp);
				}
				temp.put(obj[1].toString(),Integer.parseInt(obj[2].toString()));
			}
		}	
		return map;
	}
	
	public Map<String,Integer> getDeOffLineInventoryByProduct(){
		Map<String,Integer> map=Maps.newHashMap();
		String sql="SELECT CONCAT(t.`product_name`,CASE  WHEN t.color_code='' THEN '' ELSE CONCAT('_',t.color_code) END ) NAME,SUM(t.offline_quantity) FROM psi_inventory t "+
                " WHERE t.warehouse_id=19 GROUP BY NAME ";
		List<Object[]> list=this.psiInventoryDao.findBySql(sql);
		for (Object[] obj: list) {
			map.put(obj[0].toString(),Integer.parseInt(obj[1].toString()));
		}
		return map;
	}
	
	/**
	 * 获得欧洲的总库存数
	 *key:productNameColor
	 * value:{sku:数量}
	 * 总数sku为：euTotal
	 */
	public Map<String,Map<String,Integer>> getEuInventory(Map<String,Set<String>> deSkuMap){
		String fbaSql="SELECT t.`sku`,SUM(t.`quantity_shipped`) qty "+ 
			" FROM psi_fba_inbound d JOIN psi_fba_inbound_item t ON d.id=t.`fba_inbound_id` "+
			" WHERE  d.`create_date`>=:p1 AND d.`country` IN('de','fr','it','es','uk') AND (d.`shipment_status` IS NULL OR d.`shipment_status`='WORKING') "+
			" GROUP BY t.sku";
		List<Object[]> fbaList=this.psiInventoryDao.findBySql(fbaSql,new Parameter(DateUtils.addDays(new Date(),-15)));
		Map<String,Integer> fbaMap=Maps.newHashMap();
		if(fbaList!=null&&fbaList.size()>0){
			for (Object[] obj: fbaList) {
				fbaMap.put(obj[0].toString(),Integer.parseInt(obj[1].toString()));
			}
		}
		
		Map<String,Map<String,Integer>> rs=Maps.newHashMap();
		String sql="SELECT CONCAT(a.`product_name`,CASE  WHEN a.`color_code`='' THEN '' ELSE CONCAT('_',a.`color_code`) END) AS productName,a.`sku`,a.`new_quantity`,a.`country_code` " +
				" FROM psi_inventory AS a WHERE a.`warehouse_id`='19' AND a.`country_code` IN ('de','fr','it','es','uk')" +
				" AND (CASE WHEN a.`color_code`='' THEN CONCAT(a.`product_name`,'_',a.country_code) ELSE CONCAT(a.`product_name`,'_',a.`color_code`,'_',a.country_code) END)<>a.sku ";
		List<Object[]> list=this.psiInventoryDao.findBySql(sql);
		for (Object[] obj: list) {
			String productNameColor=obj[0].toString();
			String sku =obj[1].toString();
			Integer quantity =Integer.parseInt(obj[2].toString());
			if(fbaMap!=null&&fbaMap.get(sku)!=null){
				Integer fbaQty=fbaMap.get(sku);
				if(quantity-fbaQty>0){
					quantity=quantity-fbaQty;
				}else{
					continue;
				}
			}
			Integer euTotal =quantity;
			Map<String,Integer> skuMap = null;
			if(rs.get(productNameColor)==null){
				skuMap = Maps.newHashMap();
			}else{
				skuMap = rs.get(productNameColor);
			}
			
			skuMap.put(sku, quantity);
			if(skuMap.get("euTotal")!=null){
				euTotal+=skuMap.get("euTotal");
			}

			skuMap.put("euTotal", euTotal);
			rs.put(productNameColor, skuMap);
			
			
			//整理de的sku
			if("de".equals(obj[3].toString())){
				Set<String> deSkus = null;
				if(deSkuMap.get(productNameColor)==null){
					deSkus=Sets.newHashSet();
				}else{
					deSkus=deSkuMap.get(productNameColor);
				}
				deSkus.add(sku);
				deSkuMap.put(productNameColor, deSkus);
			}
		}
		
		Map<String,Map<String,Integer>> rsSortMap=Maps.newHashMap();
		//加入值排序
		for(Map.Entry<String, Map<String, Integer>> entry:rs.entrySet()){
			String proName = entry.getKey();
			Map<String,Integer> sortMap = Maps.newLinkedHashMap();
			Map<String,Integer> noSortKeyMap=entry.getValue();
			MapValueComparator bvc =  new MapValueComparator(noSortKeyMap,false);  
			TreeMap<String,Integer> sortKeyMap = new TreeMap<String,Integer>(bvc);  
			sortKeyMap.putAll(noSortKeyMap); 
	        for(Map.Entry<String,Integer> entry1:sortKeyMap.entrySet()){
	        	String sortKey=entry1.getKey();
	        	sortMap.put(sortKey, noSortKeyMap.get(sortKey));
	        }
	        rsSortMap.put(proName, sortMap);
		}
		return rsSortMap;
	}
	
    public Map<String,Map<String,Object>> findInventoryAndPrice(Integer warehouseId){
    	Map<String,Map<String,Object>> map=Maps.newHashMap();
    	String sql="SELECT CONCAT(p.`product_name`,CASE  WHEN p.`color_code`='' THEN '' ELSE CONCAT('_', p.`color_code`) END )  NAME,SUM(p.`new_quantity`+p.`offline_quantity`) quantity,MAX(p.`avg_price`)  "+
    			" FROM psi_inventory p WHERE p.`warehouse_id`=:p1 "+
    			" GROUP BY NAME HAVING quantity>0 ";
    	List<Object[]> list=this.psiInventoryDao.findBySql(sql,new Parameter(warehouseId));
    	for (Object[] obj: list) {
    		Map<String,Object> temp=map.get(obj[0].toString());
    		if(temp==null){
    			temp=Maps.newHashMap();
    			map.put(obj[0].toString(),temp);
    		}
    		temp.put("quantity", Integer.parseInt(obj[1].toString()));
    		temp.put("price",Float.parseFloat(obj[2]==null?"0":(obj[2].toString()))*Integer.parseInt(obj[1].toString()));
		}
    	return  map;
    }
    
	public void updateInventoryAvgPrice(Integer warehouseId,String productName,Float price){
		String sql="UPDATE psi_inventory p set avg_price=:p1 where p.`warehouse_id`=:p2 and CONCAT(p.`product_name`,CASE  WHEN p.`color_code`='' THEN '' ELSE CONCAT('_', p.`color_code`) END )=:p3 ";
		psiInventoryDao.updateBySql(sql, new Parameter(price,warehouseId,productName));
	}
	
    //CNY
    public Map<String,Float> getCnPrice(){
    	Map<String,Float> map=Maps.newHashMap();
    	String sql="SELECT DISTINCT CONCAT(p.`product_name`,CASE  WHEN p.`color_code`='' THEN '' ELSE CONCAT('_', p.`color_code`) END )  NAME,avg_price "+
    			 " FROM psi_inventory p WHERE p.`avg_price` IS NOT NULL ";
    	List<Object[]> list=psiInventoryDao.findBySql(sql);
    	for (Object[] obj: list) {
			map.put(obj[0].toString(),Float.parseFloat(obj[1].toString()));
		}
    	return map;
    }
    
    
  /**
   *获得上周出库数量 
   */
    public Map<String,Map<String,Integer>> getLastWeekOutInventoryQuantity(Integer warehouseId,Date lastWeekStart,Date lastWeekEnd){
    	Map<String,Map<String,Integer>> map=Maps.newHashMap();
    	String sql="SELECT  CASE WHEN  a.`color_code` ='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END AS productNameColor ,a.`country_code` ,SUM(a.`quantity`) FROM psi_inventory_revision_log AS a " +
    			"WHERE  a.warehouse_id=:p1 AND  a.`operatin_date` between :p2 and :p3 " +
    			" AND a.`quantity`<0 GROUP BY a.`product_name`,a.`color_code`,a.`country_code`";
    	List<Object[]> list=psiInventoryDao.findBySql(sql,new Parameter(warehouseId,lastWeekStart,lastWeekEnd));
    	for (Object[] obj: list) {
    		String productNameColor =obj[0].toString();
    		String country =obj[1].toString();
    		Integer quantity = Integer.parseInt(obj[2].toString());
    		Integer totalQ   = quantity;
    		Map<String,Integer> inMap = null;
			if(map.get(productNameColor)==null){
				inMap=Maps.newHashMap();
			}else{
				inMap= map.get(productNameColor);
			}
			
			if(inMap.get(country)!=null){
				quantity+=inMap.get(country);
			}
			
			inMap.put(country, quantity);
			
			if(inMap.get("total")!=null){
				totalQ+=inMap.get("total");
			}
			inMap.put("total", totalQ);
			
			map.put(productNameColor, inMap);
		}
    	return map;
    }
    
    
    
	//sendemail
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
    

    public Map<String,Integer> findNewQuantityByCn(Integer warehouseId){
    	Map<String,Integer> map=Maps.newHashMap();
    	String sql="SELECT p.`country_code`,CONCAT(p.`product_name`,CASE WHEN p.`color_code`='' THEN '' ELSE  CONCAT('_',p.`color_code`) END) NAME,SUM(p.`new_quantity`) FROM psi_inventory p "+
          " WHERE p.`warehouse_id`=:p1  GROUP BY p.`country_code`,NAME ";
    	List<Object[]> list=psiInventoryDao.findBySql(sql,new Parameter(warehouseId));
    	for (Object[] obj: list) {
			map.put(obj[1].toString()+"_"+obj[0].toString(), Integer.parseInt(obj[2].toString()));
		}
    	return map;
    }
    

    public Map<String,Integer> findSkuNewQuantity(Integer warehouseId){
    	Map<String,Integer> map=Maps.newHashMap();
    	String sql="SELECT t.`sku`,SUM(t.`new_quantity`) FROM `psi_inventory` t WHERE t.`warehouse_id`=:p1 AND t.`new_quantity`>0 GROUP BY t.`sku`";
    	List<Object[]> list=psiInventoryDao.findBySql(sql,new Parameter(warehouseId));
    	for (Object[] obj: list) {
			map.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
		}
    	return map;
    }

	/**
     *获得某个仓库的sku动态平均价
    */
    public Map<String,Float> getAvgPriceByWarehouseId(Integer warehouseId){
    	Map<String,Float> map=Maps.newHashMap();
    	String sql="SELECT a.`sku`,a.`avg_price` FROM psi_inventory AS a WHERE a.`warehouse_id`=:p1";
    	List<Object[]> list=psiInventoryDao.findBySql(sql,new Parameter(warehouseId));
    	for(Object[] obj:list){
    		if(obj[1]!=null){
    			map.put(obj[0].toString(), Float.parseFloat(obj[1].toString()));
    		}
    	}
    	return map;
    }

    //德国本地库存数(带电源产品忽略英国)[asin quantity]
	public Map<String, Integer> findDeLocalInventory(Map<Integer, String> hasPower) {
		Map<String, Integer> rs = Maps.newHashMap();
		Map<String, String> skuAsin = getSkuAsinMap();
		String sql = "SELECT t.`sku`,t.`country_code`,t.`product_id`,SUM(t.`new_quantity`+t.`offline_quantity`) FROM `psi_inventory` t WHERE t.`warehouse_id`='19' GROUP BY t.`sku`,t.`country_code`";
		List<Object[]> list=psiInventoryDao.findBySql(sql);
    	for(Object[] obj:list){
    		String sku = obj[0].toString();
    		String country = obj[1].toString();
    		Integer productId = Integer.parseInt(obj[2].toString());
    		if ("uk".equals(country) && "1".equals(hasPower.get(productId))) {
				continue;
			}
    		Integer quantity = Integer.parseInt(obj[3].toString());
    		String asin = skuAsin.get(sku);
    		if (StringUtils.isNotEmpty(asin)) {
    			Integer total = rs.get(asin);
    			if (total == null) {
    				rs.put(asin, quantity);
				} else {
					rs.put(asin, total + quantity);
				}
			}
    	}
		return rs;
	}

    //美国本地库存数(用于同步至官网,库存小于等于10算0个,大于10取一半库存)[asin quantity]
	public Map<String, Integer> findUsLocalInventory() {
		Map<String, Integer> rs = Maps.newHashMap();
		Map<String, String> skuAsin = getSkuAsinMap();
		String sql = "SELECT t.`sku`,t.`country_code`,t.`product_id`,SUM(t.`new_quantity`+t.`offline_quantity`) FROM `psi_inventory` t WHERE t.`warehouse_id`='120' GROUP BY t.`sku`,t.`country_code`";
		List<Object[]> list=psiInventoryDao.findBySql(sql);
    	for(Object[] obj:list){
    		String sku = obj[0].toString();
    		String country = obj[1].toString();
    		if (!"com".equals(country)) {
				continue;
			}
    		Integer quantity = Integer.parseInt(obj[3].toString());
    		String asin = skuAsin.get(sku);
    		if (StringUtils.isNotEmpty(asin)) {
    			Integer total = rs.get(asin);
    			if (total == null) {
    				rs.put(asin, quantity);
				} else {
					rs.put(asin, total + quantity);
				}
			}
    	}
    	for (Entry<String, Integer> entry : rs.entrySet()) {
			if (entry.getValue() <= 10) {
				rs.put(entry.getKey(), 0);
			} else {
				rs.put(entry.getKey(), entry.getValue()/2);
			}
		}
		return rs;
	}

    //指定国家当天fba库存数[asin quantity]
	public Map<String, Integer> findFbaInventory(String country) {
		Map<String, Integer> rs = Maps.newHashMap();
		String sql = "SELECT t.`asin`,SUM(t.`fulfillable_quantity`) FROM `psi_inventory_fba` t "+
				" WHERE t.`country`=:p1 AND  t.`data_date`=(SELECT MAX(data_date) FROM psi_inventory_fba) GROUP BY t.`asin`";
		List<Object[]> list=psiInventoryDao.findBySql(sql, new Parameter(country));
    	for(Object[] obj:list){
    		String asin = obj[0].toString();
    		Integer quantity = Integer.parseInt(obj[1].toString());
    		rs.put(asin, quantity);
    	}
		return rs;
	}

    //[sku asin]
	public Map<String, String> getSkuAsinMap() {
		Map<String, String> rs = Maps.newHashMap();
		String sql = "SELECT DISTINCT t.`sku`,t.`asin` FROM `psi_sku` t WHERE t.`del_flag`='0' AND t.`sku` IS NOT NULL AND t.`asin` IS NOT NULL";
		List<Object[]> list=psiInventoryDao.findBySql(sql);
    	for(Object[] obj:list){
    		rs.put(obj[0].toString(), obj[1].toString());
    	}
		return rs;
	}

	/**
	 * 获取中国仓两天的数据
	 * 产品:日期：数量
	 */
	public Map<String,Map<String,Integer>> getInventoryByDate(List<String> bothDate) {
		Map<String,Map<String,Integer>> rs = Maps.newHashMap();
		String sql = "SELECT DATE_FORMAT(a.`data_date`,'%Y-%m-%d'),a.`product_name`,a.`cn` FROM psi_product_in_stock AS a WHERE  a.`data_date` in :p1 AND a.`country`='total'";
		List<Object[]> list=psiInventoryDao.findBySql(sql,new Parameter(bothDate));
    	for(Object[] obj:list){
    		String dataDate = obj[0].toString();
    		String productName = obj[1].toString();
    		Integer quantity  =Integer.parseInt(obj[2].toString());
    		Map<String,Integer> inMap = null;
    		if(rs.get(productName)==null){
    			inMap = Maps.newHashMap();
    		}else{
    			inMap =rs.get(productName);
    		}
    		inMap.put(dataDate, quantity);
    		rs.put(productName, inMap);
    	}
		return rs;
	}


	


    /***
     *查询一段时间内的出库数，product_color:数量,price 
    */
	public Map<String,String> inInventoryDataByDate(String startDate,String endDate){
		 Map<String,String> rs = Maps.newHashMap();
		String sql="SELECT CASE WHEN a.`color_code`='' THEN  a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END AS productName,SUM(a.`quantity`),ROUND(SUM(a.`quantity`*a.price)/SUM(a.quantity),2) FROM psi_inventory_in_item AS a,psi_inventory_in AS b " +
				//" WHERE a.`inventory_in_id`=b.`id` AND b.`warehouse_id`='130' AND a.`bill_item_id` IS NOT NULL AND b.`data_date` BETWEEN :p1 AND :p2 " +
				" WHERE a.`inventory_in_id`=b.`id` AND b.`warehouse_id`='130'  AND b.`add_date` BETWEEN :p1 AND :p2 " +
				"  GROUP BY a.`product_name`,a.`color_code` ";
		List<Object[]> list =this.psiInventoryDao.findBySql(sql,new Parameter(startDate,endDate));
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				if(obj[2]==null){
					rs.put(obj[0].toString(), obj[1].toString()+",0");
				}else{
					rs.put(obj[0].toString(), obj[1].toString()+","+obj[2].toString());
				}
			}
		}
		return rs;
	}
	
	
	/***
     *查询一段时间内的出库数，product_color:数量 
    */
	public Map<String,Integer> outInventoryDataByDate(String startDate,String endDate){
		 Map<String,Integer> rs = Maps.newHashMap();
		String sql=" SELECT  CASE WHEN a.`color_code`='' THEN  a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END AS productName, SUM(a.`quantity`)  "+
					"  FROM psi_inventory_out_item AS a,psi_inventory_out AS b  WHERE a.`inventory_out_id`=b.`id` AND b.`warehouse_id`='130' AND b.`add_date` BETWEEN :p1 AND :p2  "+ 
					//" AND (b.`tran_fba_id` IS NOT NULL OR b.`tran_local_id` IS NOT NULL)  GROUP BY a.`product_name`,a.`color_code` ";
					"  GROUP BY a.`product_name`,a.`color_code` ";
		List<Object[]> list =this.psiInventoryDao.findBySql(sql,new Parameter(startDate,endDate));
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				rs.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
			}
		}
		return rs;
	}
    
	
	/***
     *查询小于期初时间的产品价格 ，product_color:数量,price 
    */
	public Map<String,Float> startDateyPrice(String startDate){
		 Map<String,Float> rs = Maps.newHashMap();
		 String sql=" SELECT MAX(a.id) FROM psi_inventory_in_item AS a,psi_inventory_in AS b "+
				" WHERE a.`inventory_in_id`=b.`id` AND b.`warehouse_id`='130' AND a.`bill_item_id` IS NOT NULL AND a.price IS NOT NULL AND b.`data_date`<=:p1 "+
				 " GROUP BY a.`product_name`,a.`color_code` ";
		 List<Integer> maxIds =this.psiInventoryDao.findBySql(sql,new Parameter(startDate));
		 if(maxIds!=null&&maxIds.size()>0){
			 sql="SELECT CASE WHEN a.`color_code`='' THEN  a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END AS productName , a.price FROM psi_inventory_in_item AS a WHERE a.`id` IN :p1 ";
				List<Object[]> list =this.psiInventoryDao.findBySql(sql,new Parameter(maxIds));
				if(list!=null&&list.size()>0){
					for(Object[] obj:list){
						rs.put(obj[0].toString(), Float.parseFloat(obj[1].toString()));
					}
				}
		 }
		return rs;
	}
	
	
	/***
     *从库存日志推算当天的库存数
    */
	public Map<String,String> dateInventoryMap(String startDate,String endDate){
		 Map<String,String> rs = Maps.newHashMap();
		 String sql=" SELECT CASE WHEN a.`color_code`='' THEN  a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END AS productName," +
		 		"SUM(CASE WHEN DATE_FORMAT(a.`operatin_date`,'%Y-%m-%d')<=:p1 THEN a.quantity ELSE 0 END) AS xxx,SUM(a.quantity) AS xx FROM psi_inventory_revision_log AS a" +
		 		" WHERE a.`warehouse_id`='130' AND a.operatin_date<=:p2 GROUP BY a.`product_name`,a.`color_code`";
		List<Object[]> list =this.psiInventoryDao.findBySql(sql,new Parameter(startDate,endDate));
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				rs.put(obj[0].toString(), obj[1]+","+obj[2]);
			}
		}
		return rs;
	}
	

	
	/***
     *查询没有sku的新品
    */
	public List<String> getNoSkus(Integer warehouseId){
		 String sql=" SELECT DISTINCT a.`sku` FROM psi_inventory AS a WHERE (CASE WHEN a.`color_code`='' THEN CONCAT(a.`product_name`,'_',a.`country_code`)" +
		 		" ELSE CONCAT(a.`product_name`,'_',a.`color_code`,'_',a.`country_code`) END)=a.`sku`";
		 Parameter parameter = null;
		 if (warehouseId != null) {
			 sql += " AND a.warehouse_id=:p1";
			 parameter = new Parameter(warehouseId);
		}
		 List<String> list =this.psiInventoryDao.findBySql(sql, parameter);
		return list;
	}
	
	
	/**
	 * 运单管理中国仓在库库存数据
	 * 查询sku的中国仓在库库存(排除当前运单)[country[sku quantity]]
	 * @param id 需要排除的运单编号
	 * @param type 1:春雨  2：理诚(默认)
	 * @return
	 */
	public Map<String, Map<String, Integer>> getSkuInventoryMap(Integer id, String type) {
		Map<String, Map<String, Integer>> rs = Maps.newHashMap();
		Integer warehouseId = 130;
		if ("1".equals(type)) {
			warehouseId = 21;
		}
		String inventorySql = "SELECT a.`country_code`,a.`sku`,SUM(a.`new_quantity`+a.`offline_quantity`) AS quantity "+
				" FROM psi_inventory AS a WHERE a.`warehouse_id`="+warehouseId+" GROUP BY a.`country_code`,a.`sku`";
		List<Object[]> list = psiInventoryDao.findBySql(inventorySql);
    	for(Object[] obj : list){
    		String country = obj[0].toString();
    		String sku = obj[1].toString();
    		Integer quantity = obj[2]==null?0:Integer.parseInt(obj[2].toString());
    		Map<String, Integer> countryMap = rs.get(country);
    		if (countryMap == null) {
    			countryMap = Maps.newHashMap();
    			rs.put(country, countryMap);
			}
    		countryMap.put(sku, quantity);
    	}

		String sql = "SELECT t.`country_code`,t.`sku`,SUM(t.`quantity`) FROM `lc_psi_transport_order_item` t ,`lc_psi_transport_order` o "+
				" WHERE t.`transport_order_id`=o.`id` AND o.`transport_sta`='0' AND o.`id`!=:p1 AND t.del_flag='0' GROUP BY t.`sku`,t.`country_code`";
		if ("1".equals(type)) {	//春雨
			sql = "SELECT t.`country_code`,t.`sku`,SUM(t.`quantity`) FROM `psi_transport_order_item` t ,`psi_transport_order` o "+
					" WHERE t.`transport_order_id`=o.`id` AND o.`transport_sta`='0' AND o.`id`!=:p1 AND t.del_flag='0' GROUP BY t.`sku`,t.`country_code`";
		}
		list = psiInventoryDao.findBySql(sql, new Parameter(id));
    	for(Object[] obj : list){
    		String country = obj[0].toString();
    		if (obj[1] == null) {
				continue;
			}
    		String sku = obj[1].toString();
    		Integer quantity = obj[2]==null?0:Integer.parseInt(obj[2].toString());
    		Map<String, Integer> countryMap = rs.get(country);
    		if (countryMap == null) {
    			countryMap = Maps.newHashMap();
    			rs.put(country, countryMap);
			}
    		Integer totalQuantity = countryMap.get(sku);
    		if (totalQuantity == null || totalQuantity < quantity) {
    			countryMap.put(sku, 0);
			} else {
				countryMap.put(sku, totalQuantity-quantity);
			}
    	}
		return rs;
	}
	
	/**
	 * 统计所有产品的在库数量(不区分仓库、国家)
	 * @return Map<String,Integer> [productName qty]
	 */
	public Map<String,Integer> getAllInventory(){
		Map<String,Integer> rs = Maps.newHashMap();
		String sql="SELECT a.`product_name`,a.`color_code`,SUM(a.`new_quantity`+a.`old_quantity`+a.`broken_quantity`+a.`renew_quantity`+a.`offline_quantity`) AS qty"+
				" FROM psi_inventory AS a GROUP BY a.`product_name`,a.`color_code`";
		List<Object[]> list = psiInventoryDao.findBySql(sql);
		for(Object[] obj:list){
			String productName = obj[0].toString();
			String color = obj[1]==null?"":obj[1].toString();
			if (StringUtils.isNotEmpty(color)) {
				productName = productName + "_" + color;
			}
			Integer quantity = obj[2]==null?0:Integer.parseInt(obj[2].toString());
			rs.put(productName, quantity);
	    }
		return rs;
	}
	
	/**
	 * 统计所有产品的在途数量(不区分仓库、国家)
	 * @return Map<String,Integer> [productName qty]
	 */
	public Map<String,Integer> getAllTransportting(){
		Map<String,Integer> rs = Maps.newHashMap();
		String sql="SELECT b.`product_name`,b.`color_code`,SUM(tempQuantity) AS qty FROM ( "+
				" SELECT b.`product_name`, b.color_code, "+
				" SUM(IFNULL(b.`shipped_quantity`,0)- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  IFNULL(b.`shipped_quantity`,0) ELSE 0 END) tempQuantity "+
				" FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('1','2','3','4')   "+
				" GROUP BY b.`product_name`,b.`color_code` "+
				" UNION ALL  "+
				" SELECT b.`product_name`, b.color_code, "+
				" SUM(IFNULL(b.`shipped_quantity`,0)- (CASE WHEN b.`receive_quantity` IS NULL THEN 0 ELSE b.`receive_quantity` END))-SUM(CASE WHEN a.`transport_type` ='1' THEN  IFNULL(b.`shipped_quantity`,0) ELSE 0 END) tempQuantity "+
				" FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND b.`del_flag`='0' AND b.offline_sta='0' AND a.`transport_sta` IN('1','2','3','4')   "+
				" GROUP BY b.`product_name`,b.`color_code`) b GROUP BY b.`product_name`,b.`color_code` HAVING qty>0";
		List<Object[]> list = psiInventoryDao.findBySql(sql);
		for (Object[] obj : list) {
			String productName = obj[0].toString();
			String color = obj[1]==null?"":obj[1].toString();
			if (StringUtils.isNotEmpty(color)) {
				productName = productName + "_" + color;
			}
			Integer quantity = obj[2]==null?0:Integer.parseInt(obj[2].toString());
			rs.put(productName, quantity);
	    }
		return rs;
	}
	
	/**
	 * 统计所有产品的FBA数量(不区分仓库、国家)
	 * @return Map<String,Integer> [productName qty]
	 */
	public Map<String,Integer> getAllFba(){
		Map<String,Integer> rs = Maps.newHashMap();
		String sql = "SELECT MAX(data_date) FROM psi_inventory_fba";
		List<Object> date = psiInventoryDao.findBySql(sql);
		sql = "SELECT productName,SUM(fulfillable_quantity),SUM(transit_quantity),SUM(orrect_quantity) FROM("+
				" SELECT DISTINCT b.`sku`,CONCAT(a.`product_name`,CASE  WHEN a.`color`='' THEN '' ELSE CONCAT('_',a.`color`) END) AS productName,"+
				" a.`country`,b.`fulfillable_quantity`,b.`reserved_quantity`,b.`transit_quantity`,IFNULL(b.orrect_quantity,0) AS orrect_quantity FROM psi_sku a ,psi_inventory_fba b"+
				" WHERE a.`sku` = b.`sku` AND a.`country` = b.`country` AND a.`del_flag` = '0' AND NOT(a.`product_name` LIKE '%other' OR a.`product_name` LIKE '%Old%' ) "+
				" AND b.`data_date` = :p1 ORDER BY a.`country`)AS aa GROUP BY productName";
		List<Object[]> list = psiInventoryDao.findBySql(sql,new Parameter(date.get(0)));		
		for (Object[] objs : list) {
			String productName = objs[0].toString();
			int fulfillable = objs[1]==null?0:Integer.parseInt(objs[1].toString());
			int transit = objs[2]==null?0:Integer.parseInt(objs[2].toString());
			int orrect = objs[3]==null?0:Integer.parseInt(objs[3].toString());
			rs.put(productName, fulfillable+transit+orrect);
		}
		return  rs;
	}
	
	public Map<String,Map<String,Integer>> findOfflineQuantity(){
		Map<String,Map<String,Integer>> map=Maps.newHashMap();
		String sql="SELECT p.`country_code`,CONCAT(p.`product_name`,(CASE WHEN p.`color_code`!='' THEN CONCAT('_',p.`color_code`)  ELSE '' END)) NAME,p.`warehouse_id`,SUM(p.`offline_quantity`) FROM psi_inventory p "+
                   " WHERE p.`offline_quantity`>0 and p.`warehouse_id` in (21,130) GROUP BY p.`country_code`,NAME,p.`warehouse_id` ";
		List<Object[]> list = psiInventoryDao.findBySql(sql);		
		for (Object[] obj: list) {
			String country=obj[0].toString();
			String name=obj[1].toString();
			String wareHouseId=obj[2].toString();//lc 1 sp 0
			Integer quantity=Integer.parseInt(obj[3].toString());
			Map<String,Integer> temp=map.get(name+"_"+country);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(name+"_"+country,temp);
			}
			temp.put("21".equals(wareHouseId)?"0":"1", quantity);
		}
		return map;
	}
	
	
	/**
	 * 获取中国仓两天的数据
	 * 产品:日期：数量
	 */
	public Map<String,Integer> getInventoryByDate(Integer warehouseId,String dateDate) {
		Map<String,Integer> rs = Maps.newHashMap();
		int houseId = warehouseId.intValue();
		String sql="";
		if(houseId==120){
			sql="SELECT a.`product_name`,SUM(IFNULL(a.`overseas`,0)) FROM psi_product_in_stock AS a WHERE a.`data_date` ='"+dateDate+"' AND a.`country` IN ('com','ca') GROUP BY a.`product_name`";
		}else if(houseId==19){
			sql="SELECT a.`product_name`,SUM(IFNULL(a.`overseas`,0)) FROM psi_product_in_stock AS a WHERE a.`data_date` ='"+dateDate+"' AND a.`country` IN ('de','fr','uk','it','es') GROUP BY a.`product_name`";
		}else if(houseId==21){
			sql="SELECT a.`product_name`,SUM(IFNULL(a.`cn`,0)-IFNULL(a.`cn_lc`,0)) FROM psi_product_in_stock AS a WHERE a.`data_date` ='"+dateDate+"' AND a.`country` IN ('de','fr','uk','it','es','jp','com','ca') GROUP BY a.`product_name`";
		}else if(houseId==130){
			sql="SELECT a.`product_name`,SUM(IFNULL(a.`cn_lc`,0)) FROM psi_product_in_stock AS a WHERE  a.`data_date` ='"+dateDate+"' AND a.`country` IN ('de','fr','uk','it','es','jp','com','ca') GROUP BY a.`product_name`";
		}

		List<Object[]> list=psiInventoryDao.findBySql(sql);
    	for(Object[] obj:list){
    		String productName = obj[0].toString();
    		Integer quantity  =Integer.parseInt(obj[1].toString());
    		rs.put(productName, quantity);
    	}
		return rs;
	}
	
	/**
	 * 
	 * 每天监控备品数满整箱，和大货的零散货+备品数满一箱提醒
	 */
	public  List<Object[]> getOverPackQuantity(){
			String sql="SELECT (CASE WHEN a.`color_code`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END) AS proName,a.`sku`, a.`new_quantity`,a.`spares_quantity`,b.`pack_quantity`" +
				",TRUNCATE((a.`new_quantity`%b.`pack_quantity`+a.`spares_quantity`)/b.`pack_quantity`,0) AS box  " +
				"FROM psi_inventory AS a ,psi_product AS b WHERE a.`product_id`=b.`id` AND b.`pack_quantity` <>1 AND (( a.`new_quantity`%b.`pack_quantity`>0 " +
				"AND  (a.`new_quantity`%b.`pack_quantity`+a.`spares_quantity`)/b.`pack_quantity`>1) OR a.`spares_quantity`/b.`pack_quantity`>1)  AND a.`warehouse_id`=:p1";
			List<Object[]> list = this.psiInventoryDao.findBySql(sql,new Parameter(130));
		return list;
	}
	
	
	
	
	/**
	 *查询产品预计收货情况
	 */
	public  Map<String,Map<Integer,Integer>> getUnReceivedData(){
		 Map<String,Map<Integer,Integer>> rs = Maps.newHashMap();
		String sql="SELECT CASE WHEN  c.`delivery_date` IS NOT NULL THEN c.`delivery_date` ELSE IFNULL(b.`actual_delivery_date`,b.`delivery_date`) END AS delieryDate, " +
				"b.product_id,(CASE WHEN  c.`delivery_date` IS NOT NULL THEN c.`quantity`-c.`quantity_received`ELSE b.`quantity_ordered`-b.`quantity_received` END) AS unReceived  FROM lc_psi_purchase_order AS a," +
				"lc_psi_purchase_order_item AS b LEFT JOIN lc_psi_purchase_order_delivery_date AS c ON  b.`id`=c.`purchase_order_item_id` AND c.`del_flag`='0' AND (c.quantity-c.quantity_received) >0  WHERE a.id=b.`purchase_order_id` AND a.`del_flag`='0'  AND a.`order_sta`IN ('1','2','3') AND b.`del_flag`='0' ";
		List<Object[]> list = this.psiInventoryDao.findBySql(sql);
		
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				String date = obj[0].toString();
				Integer productId = Integer.parseInt(obj[1].toString());
				Integer unReveived = Integer.parseInt(obj[2].toString());
				if(unReveived>0){
					Map<Integer,Integer> inMap = null;
					if(rs.get(date)==null){
						inMap = Maps.newHashMap();
					}else{
						inMap = rs.get(date);
					}
					
					if(inMap.get(productId)!=null){
						unReveived+=inMap.get(productId);
					}
					inMap.put(productId, unReveived);
					rs.put(date, inMap);
				}
			}
		}
		return rs;
	}
	
	/**
	 *运单情况，中国仓是发出多少，其他仓是即将入多少 
	 *仓库id，日期，体积
	 */
	
	public  Map<Integer,Map<String,Integer>> getTransportData(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String toDayStr=sdf.format(new Date());
		Map<Integer,Map<String,Integer>> rs = Maps.newHashMap();
		String sql="SELECT a.`transport_sta`,a.`transport_type`,DATE_FORMAT(a.`oper_arrival_date`,'%Y-%m-%d'),a.`volume`,a.`from_store`,a.`to_store` FROM lc_psi_transport_order AS a  WHERE a.`transport_sta` NOT IN ('4','5','8') AND a.`transport_type`!='3'";
		List<Object[]> list = this.psiInventoryDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				String sta = obj[0].toString();
				String type = obj[1].toString();
				String date = obj[2]==null?toDayStr:obj[2].toString();
				if(obj[3]==null){
					continue;
				}
				Float volume = Float.parseFloat(obj[3].toString());
				Integer fromId =Integer.parseInt(obj[4].toString()); 
				Integer toId =Integer.parseInt(obj[5].toString()); 
				
				//如果是没发货，算发货仓库  
				if("0".equals(sta)){
					Map<String,Integer> inMap = null;
					if(rs.get(fromId)==null){
						inMap = Maps.newHashMap();
					}else{
						inMap = rs.get(fromId);
					}
					if(inMap.get(date)!=null){
						volume+=inMap.get(date);
					}
					rs.put(fromId, inMap);
				}else{
					//出了库的只算 本地运输的
					if("0".equals(type)){
						Map<String,Integer> inMap = null;
						if(rs.get(toId)==null){
							inMap = Maps.newHashMap();
						}else{
							inMap = rs.get(toId);
						}
						if(inMap.get(date)!=null){
							volume+=inMap.get(date);
						}
						rs.put(toId, inMap);
					}
				}
			}
		}
		return rs;
	}
	 
	/**
	 *德国仓/美国仓  减去所有fba working 的体积
	 * 
	 */
	public  Map<String,Float> getWorkingFbaVolumeData(){
		Map<String,Float> rs = Maps.newHashMap();
		String sql="SELECT aa.ship_from_address,SUM(aa.cap) FROM (SELECT a.`ship_from_address`,CEILING(SUM(b.`quantity_shipped`/d.`pack_quantity`))*d.`box_volume` AS cap FROM psi_fba_inbound AS a,psi_fba_inbound_item AS b ," +
				"	psi_sku AS c,psi_product AS d WHERE a.id  = b.`fba_inbound_id` AND c.`product_id`=d.id AND BINARY(b.`sku`)=c.`sku` AND a.`shipment_status`='WORKING' " +
				"AND a.`shipped_date` IS NULL AND a.`ship_from_address` IN ('DE','US') GROUP BY a.`ship_from_address`,c.`product_id`) AS aa";
		List<Object[]> list = this.psiInventoryDao.findBySql(sql);
		
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				if(obj[0]!=null&&obj[1]!=null){
					rs.put(obj[0].toString(), Float.parseFloat(obj[1].toString()));
				}
			}
		}
		return rs;
	}
	
	/**
	 *获得德国仓打了包箱单没发货的产品 
	 *shipped状态   跟踪号为空  10天内
	 */
	public  Map<String,Integer> getUnShippedQuantity(int stockId){
		Map<String,Integer> rs = Maps.newHashMap();
		String sql="SELECT b.`sellersku`,SUM(b.`quantity_ordered`) FROM amazoninfo_unline_order AS a ,amazoninfo_unline_orderitem AS b " +
				" WHERE a.`id`=b.`order_id` AND a.`sales_channel`=:p1 AND (a.`bill_no` ='' OR a.`bill_no` IS NULL) AND a.`order_status`='Shipped' and a.order_channel!='管理员' and a.order_channel!='check24' " +
				" AND a.`purchase_date`>=DATE_ADD(CURDATE(),INTERVAL -10 DAY) GROUP BY b.`sellersku` ";
		List<Object[]> list = this.psiInventoryDao.findBySql(sql,new Parameter(stockId));
		
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
					rs.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
			}
		}
		return rs;
	}
	
	
    
    /**
     *推算仓库那天会爆仓 
     * @throws ParseException 
     */ 
	public void  getOverCapacityInfo() throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDay = sdf.parse(sdf.format(new Date()));
		Date endDate = DateUtils.addDays(startDay, 30);
		 Map<String,Float> fbaMap =getWorkingFbaVolumeData();
		 Map<Integer,Map<String,Integer>> tranMap =getTransportData();
		 Map<String,Map<Integer,Integer>> orderMap=getUnReceivedData();
		 Map<Integer,Stock> stockMap=this.stockService.findStock();
		 Map<Integer,Float> inventoryMap = getTimelyCapacity();
		 Map<Integer,PsiProduct> productMap=this.psiProductService.findProductsMap(null);
		 Float cn = inventoryMap.get(130)==null?0f:inventoryMap.get(130);
		 Float de = inventoryMap.get(19)==null?0f:inventoryMap.get(19);
		 Float maxCn=stockMap.get(130).getCapacity();
		 Float maxDe=stockMap.get(19).getCapacity();
		//德国仓减去fba的
		 de-=fbaMap.get("DE")!=null?fbaMap.get("DE"):0f;
		 StringBuffer sbDe = new StringBuffer();
		 StringBuffer sbCn = new StringBuffer();
		while(startDay.before(endDate)){
			String dateStr = sdf.format(startDay);
			//中国仓加上预计收货的
			if(orderMap.get(dateStr)!=null){
				Map<Integer,Integer> unReceivedMap =orderMap.get(dateStr);
				for(Map.Entry<Integer,Integer> entry:unReceivedMap.entrySet()){
					Integer productId = entry.getKey();
					Integer quantity = entry.getValue();
					PsiProduct product = productMap.get(productId);
					Float volume = 0f;
					if(product!=null){
						if(product.getPackLength().intValue()==1&&product.getPackWidth().intValue()==1&&product.getPackHeight().intValue()==1){//如果包装都为长宽高都为1
							volume+=quantity/100;
						}else{
							volume+=(float)Math.ceil(quantity/product.getPackQuantity().doubleValue())*product.getBoxVolume().floatValue();
						}
					}
					cn+=volume;
				}
			//中国仓减去本地运输的没发货的运单
				if(tranMap.get(130)!=null&&tranMap.get(130).get(dateStr)!=null){
					cn-=tranMap.get(130).get(dateStr);
				}
		
			//德国仓加上预计到仓的运单
				if(tranMap.get(19)!=null&&tranMap.get(19).get(dateStr)!=null){
					de+=tranMap.get(19).get(dateStr);
				}
			
				
			}
			
			if(maxCn!=null){
				if(cn>maxCn*(0.8f)){
					sbCn.append(dateStr+"预计库容："+(cn.intValue())+"(m³)<br/>");
				}
			}
			
			if(maxDe!=null){
				if(de>maxDe*(0.8f)){
					sbDe.append(dateStr+"预计库容："+(de.intValue())+"(m³)<br/>");
				}
			}
			
			startDay=DateUtils.addDays(startDay,1);
		}
		//对德国仓和中国仓预警数据输出
		if(sbCn.length()>0||sbDe.length()>0){
			//整理数据发信：
			sendNoticeEmail(UserUtils.logistics1+","+UserUtils.logistics2+",tim@inateck.com","仓库‘预计容量’大于仓库‘最大库存容量’的80%预警信息如下:<br/>"+(sbCn.length()>0?("中国仓库预计库容情况如下：<br/>"+sbCn):"") +(sbDe.length()>0?("德国仓预计库容情况如下：<br/>"+sbDe):""), "本地仓库容预警", UserUtils.getUser().getEmail(), "");
		}
	}
	
	
	public Map<String,Integer> findQuantityByWarehouseId(Integer id){
		Map<String,Integer> map=Maps.newHashMap();
		String sql="SELECT (CASE WHEN  a.`color_code`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END ) AS proName,SUM(a.`new_quantity`+a.`offline_quantity`) FROM psi_inventory AS a "+
		   " WHERE a.`warehouse_id`=:p1 GROUP BY a.`product_name`,a.`color_code` ";
		List<Object[]> list=psiInventoryDao.findBySql(sql,new Parameter(id));
		for(Object[] obj:list){
			map.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
		}
		return map;
	}
	
	public Map<String,Integer> findQuantityByWarehouseId2(Integer id){
		Map<String,Integer> map=Maps.newHashMap();
		String sql="SELECT (CASE WHEN  a.`color_code`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END ) AS proName,SUM(a.`new_quantity`+a.`offline_quantity`) FROM psi_inventory AS a join psi_product d on a.`product_id`=d.`id` "+
		   " WHERE a.`warehouse_id`=:p1 and (d.has_power!='1' or (d.has_power='1' and a.country_code!='uk')) GROUP BY a.`product_name`,a.`color_code` ";
		List<Object[]> list=psiInventoryDao.findBySql(sql,new Parameter(id));
		for(Object[] obj:list){
			map.put(obj[0].toString(), Integer.parseInt(obj[1].toString()));
		}
		return map;
	}
	
	public Map<String,PsiInventoryTotalDto> getProducingQuantity(Set<String> nameSet){
		Map<String,String> skuMap=this.psiProductService.getAllBandingSku();

		
		String sql ="SELECT product_name,country_code,color_code,SUM(orderNum),SUM(offNum) FROM (" +
				"SELECT b.`product_name`,b.`country_code`,b.`color_code`," +
				" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum,SUM(b.`quantity_off_ordered`-b.`quantity_off_received`) AS offNum " +
				" FROM psi_purchase_order AS a ,psi_purchase_order_item AS b " +
				" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3')  and b.`del_flag`='0' " +
				" AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) in :p1 "+
				" GROUP BY b.product_name,b.country_code,b.color_code  "+
				" UNION ALL SELECT b.`product_name`,b.`country_code`,b.`color_code`," +
				" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum,SUM(b.`quantity_off_ordered`-b.`quantity_off_received`) AS offNum " +
				" FROM lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b " +
				" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('1','2','3')  and b.`del_flag`='0' " +
				" AND CONCAT(b.`product_name`,CASE  WHEN b.color_code='' THEN '' ELSE CONCAT('_',b.color_code) END ) in :p1 "+
				" GROUP BY b.product_name,b.country_code,b.color_code ) AS t GROUP BY product_name,country_code,color_code ";
		List<Object[]> 	objects=this.psiInventoryDao.findBySql(sql,new Parameter(nameSet)); 
		
		Map<String,PsiInventoryTotalDto> psiInventoryTotalDtos = Maps.newLinkedHashMap();
		Map<String,List<PsiInventoryDto>> innerDtoMap = Maps.newHashMap();
	
		for(Object[] object:objects){
			List<PsiInventoryDto> psiInventoryDtos = Lists.newArrayList();
			String productName = object[0].toString();
			String country = object[1].toString();
			String color   = object[2].toString();
			Integer quantity   = Integer.parseInt(object[3].toString());
			Integer offlineQuanity = Integer.parseInt(object[4].toString());
			//如果线上在产为0，线下在产也为0    跳过
			if(quantity.intValue()==0&&offlineQuanity.intValue()==0){
				continue;
			}
			String productColorKey=productName+","+color;
			String key=productName+","+country+","+color;
			Map<String, Integer> inSkuMap = Maps.newHashMap();
			String sku =skuMap.get(key);
			if(!StringUtils.isEmpty(sku)){
				inSkuMap.put(sku, quantity);
			}else{
				inSkuMap.put("noSku", quantity);
			}
			
			if(innerDtoMap.get(productColorKey)!=null){
				psiInventoryDtos=innerDtoMap.get(productColorKey);
			}
			psiInventoryDtos.add(new PsiInventoryDto(productName, country, color,inSkuMap));
			innerDtoMap.put(productColorKey, psiInventoryDtos);
			
		}
		
		for(Map.Entry<String, List<PsiInventoryDto>> entry:innerDtoMap.entrySet()){
			String productColorKey = entry.getKey();
			String[] proArr = productColorKey.split(",");
			String name=proArr[0];
			String color = "";
			if(proArr.length>1){
				color=proArr[1];
			}
			if(StringUtils.isNotEmpty(color)){
				name=name+"_"+color;
			}
			
			
			Map<String,PsiInventoryDto> countryDtoMap =Maps.newHashMap();
			for(PsiInventoryDto psiInventoryDto:entry.getValue()){
				countryDtoMap.put(psiInventoryDto.getCountry(), psiInventoryDto);
			}
			psiInventoryTotalDtos.put(name,new PsiInventoryTotalDto(proArr[0], color, countryDtoMap));
			
		}
		
		return psiInventoryTotalDtos;
	}
	
	
	@Transactional(readOnly = false)
	public void updateRemark(Integer productId,String countryCode,String colorCode,Integer warehouseId,String remark){
		String sql="update psi_inventory set remark=:p1 where product_id=:p2 and country_code=:p3 and color_code=:p4 and warehouse_id=:p5";
		psiInventoryDao.updateBySql(sql,new Parameter(remark,productId,countryCode,colorCode,warehouseId)); 
	}
	
	
	public Map<String,Map<String,String>> find(){
		Map<String,Map<String,Integer>> quantityMap=Maps.newHashMap();
		Map<String,Map<String,String>> remarkMap=Maps.newHashMap();
		Map<String,Map<String,String>> finalRemarkMap=Maps.newHashMap();
		String sql="SELECT concat(p.`product_name`,case when p.`color_code`='' then '' else concat('_',p.`color_code`) end) name,p.`country_code`,COUNT(*) num,sum(t.`quantity_ordered`) quantity,GROUP_CONCAT(DISTINCT p.`remark`) remark "+ 
				" FROM lc_psi_purchase_order r   "+
				" JOIN lc_psi_purchase_order_item t ON r.id=t.`purchase_order_id` AND t.`del_flag`='0' "+
				" JOIN ( "+
				" SELECT p.`product_name`,p.`color_code`,p.`country_code`,GROUP_CONCAT(DISTINCT p.`remark`) remark FROM psi_inventory p  "+
				//" JOIN psi_product_eliminate e ON e.`product_name`=p.`product_name` AND e.`color`=p.`color_code` AND e.`country`=p.`country_code` "+
				" WHERE  p.`warehouse_id`=130 AND  p.`remark` IS NOT NULL AND p.`remark`!='' AND p.`new_quantity`>=0 "+
				" GROUP BY p.`product_name`,p.`color_code`,p.`country_code` "+
				" ) p ON p.`product_name`=t.`product_name` AND p.`color_code`=t.`color_code` AND p.`country_code`=t.`country_code` "+
				" WHERE r.`order_sta` IN ('3','4','5')  "+
				" GROUP BY p.`product_name`,p.`color_code`,p.`country_code` HAVING num<=2 ORDER BY r.`create_date` ASC ";
		List<Object[]> list=psiInventoryDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				String name=obj[0].toString();
				String country=obj[1].toString();
				String quantityArr=obj[3].toString();
				Integer quantity=0;
				if(quantityArr.contains(",")){
					quantity=Integer.parseInt(quantityArr.split(",")[0]);
				}else{
					quantity=Integer.parseInt(quantityArr);
				}
				String remark=obj[4].toString();
				Map<String,Integer> temp=quantityMap.get(name);
				if(temp==null){
					temp=Maps.newHashMap();
					quantityMap.put(name, temp);
				}
				temp.put(country, quantity);
				
				Map<String,String> temp1=remarkMap.get(name);
				if(temp1==null){
					temp1=Maps.newHashMap();
					remarkMap.put(name, temp1);
				}
				temp1.put(country, remark);
			}
			
			String skuChangeSql="SELECT concat(c.`product_name`,case when c.`product_color`='' then '' else concat('_',c.`product_color`) end) name, t.`country_code` fromCountry,c.`product_country` toCountry,SUM(c.`quantity`) "+
					"	FROM psi_sku_change_bill c  "+
					"	JOIN psi_inventory t ON c.`from_sku`=t.`sku`  "+
					"	WHERE  concat(c.`product_name`,case when c.`product_color`='' then '' else concat('_',c.`product_color`) end) in :p1 and c.`warehouse_id`=130 AND t.`warehouse_id`='130' AND c.`change_sta`='3' and c.`remark` like '%新品需贴码%'  "+
					"	GROUP BY c.`product_name`,c.`product_color`, t.`country_code`,fromCountry,toCountry ";
			
			
			List<Object[]> skuList=psiInventoryDao.findBySql(skuChangeSql,new Parameter(quantityMap.keySet()));
			if(skuList!=null&&skuList.size()>0){
				for (Object[] obj: skuList) {
					String name=obj[0].toString();
					String fromCountry=obj[1].toString();
					String toCountry=obj[2].toString();
					Integer quantity=Integer.parseInt(obj[3].toString());
					Map<String, Integer>  temp=quantityMap.get(name);
					if(temp!=null){
						Integer oldFromCountry=temp.get(fromCountry)==null?0:temp.get(fromCountry);
						Integer oldToCountry=temp.get(toCountry)==null?0:temp.get(toCountry);
						temp.put(fromCountry,oldFromCountry-quantity);
                        temp.put(toCountry,oldToCountry+quantity);
					}
				}
			}
			
			
			String psiSql="SELECT CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END) NAME,t.`country_code`,SUM(t.`quantity`)  "+
						" FROM lc_psi_transport_order r  "+
					    " JOIN lc_psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0'  "+
					    " WHERE CONCAT(t.`product_name`,CASE WHEN t.`color_code`='' THEN '' ELSE CONCAT('_',t.`color_code`) END)  in :p1 and r.`transport_sta`!='8' GROUP BY NAME,t.`country_code`";
			
			List<Object[]> psiList=psiInventoryDao.findBySql(psiSql,new Parameter(quantityMap.keySet()));
		
			if(psiList!=null&&psiList.size()>0){
				for (Object[] obj: psiList) {
					String name=obj[0].toString();
					String country=obj[1].toString();
					Integer quantity=Integer.parseInt(obj[2].toString());
					if(quantityMap.get(name)!=null&&quantityMap.get(name).get(country)!=null&&quantityMap.get(name).get(country)>quantity){
						Map<String,String> temp=finalRemarkMap.get(name);
						if(temp==null){
							temp=Maps.newHashMap();
							finalRemarkMap.put(name, temp);
						}
						temp.put(country, remarkMap.get(name).get(country));
					}
				}
			}	
		}
		return finalRemarkMap;
	}
	
	
	/**
	 * 查询库存数和出入库记录中的库存不一致的产品
	 * @return 
	 */
	public List<PsiInventory> findAbnormalInventory(){
		List<PsiInventory> rs = Lists.newArrayList();
		String sql = "SELECT t.`sku`,t.`product_name`,t.`color_code`,t.`new_quantity`,a.qty,t.warehouse_id FROM `psi_inventory` t, "+
				" (SELECT t.`sku`,t.`warehouse_id`,t.`operatin_date`,t.`product_name`,SUBSTRING_INDEX(GROUP_CONCAT(t.`timely_quantity` ORDER BY id DESC),',',1) AS qty "+
				" FROM `psi_inventory_revision_log` t WHERE t.`timely_quantity` IS NOT NULL AND t.data_type='new'  "+
				" GROUP BY t.`sku`,t.`warehouse_id`) a WHERE t.`sku` = a.sku AND t.`warehouse_id`=a.warehouse_id AND t.`new_quantity`!=a.qty "+
				" AND t.`update_date`>'2017-06-01' ";
		List<Object[]> list = psiInventoryDao.findBySql(sql);
		for (Object[] obj : list) {
			String sku = obj[0].toString();
			Integer newQty = obj[3]==null?0:Integer.parseInt(obj[3].toString());
			Integer qty = obj[4]==null?0:Integer.parseInt(obj[4].toString());
			PsiInventory psiInventory = new PsiInventory();
			psiInventory.setSku(sku);
			psiInventory.setNewQuantity(newQty);
			psiInventory.setOldQuantity(qty);
			rs.add(psiInventory);
		}
		return rs;
	}
	
//	public static void main(String[] args) throws ParseException {
//		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
//		PsiInventoryService service = applicationContext.getBean(PsiInventoryService.class);
//		service.getOverCapacityInfo();
//	}
}
