/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.lc;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.dao.lc.LcPsiTransportOrderDao;
import com.springrain.erp.modules.psi.entity.FbaInbound;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiTransportForecastDto;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrderContainer;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrderItem;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportPayment;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportPaymentItem;
import com.springrain.erp.modules.psi.scheduler.PsiConfig;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTieredPriceService;
import com.springrain.erp.modules.psi.service.StockService;
import com.springrain.erp.modules.sys.dao.GenerateSequenceDao;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 运单表Service
 * @author Michael
 * @version 2015-01-15
 */
@Component
@Transactional(readOnly = true)
public class LcPsiTransportOrderService extends BaseService {
	@Autowired
	private LcPsiTransportOrderDao 			    psiTransportOrderDao;
	@Autowired
	private GenerateSequenceDao 			    genDao;
	@Autowired
	private PsiProductService 				    psiProductService;
	@Autowired
	private LcPsiTransportOrderItemService 	    psiTransportOrderItemService;
	@Autowired
	private LcPsiTransportOrderContainerService   psiTransportContainerService;
	@Autowired
	private StockService 						stockService;
	@Autowired
	private PsiProductTieredPriceService		productTieredPriceService;
	@Autowired
	private PsiInventoryService	       		    inventoryService;
	
	
	public LcPsiTransportOrder get(Integer id) {
		return psiTransportOrderDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void updateDeclareAmount(Integer id,Float declare_amount){
		String sql="update lc_psi_transport_order set declare_amount=:p1 where id=:p2";
		psiTransportOrderDao.updateBySql(sql,new Parameter(declare_amount,id));
	}
	
	public LcPsiTransportOrder getById(Integer id) {
		String sql="SELECT p.id,p.`suffix_name`,p.`transport_no`,p.`create_date`,p.`to_country`,t.`product_id`,t.`currency`,t.`product_price`,t.`quantity`,t.`pack_quantity`,d.`chinese_name`, d.`gw`, "+
            " d.`brand`,d.`model` pmodel,d.type,d.`pack_length`,d.`pack_width`,d.`pack_height`,d.`box_volume`, "+
			" eu_hscode,ca_hscode,jp_hscode,us_hscode,hk_hscode,p.weight,p.box_number,p.model tmodel,cn_hscode,t.country_code,d.material,t.id itemId,t.lower_price,t.item_price,t.import_price,mx_hscode,p.volume,d.tax_refund,t.product_name,t.color_code  "+
			" FROM lc_psi_transport_order p JOIN lc_psi_transport_order_item t ON p.id=t.`transport_order_id` AND t.`del_flag`='0' "+
            " JOIN psi_product d ON d.id=t.`product_id` AND d.del_flag='0' where p.id=:p1 and (d.components IS NULL OR d.components='0') order by t.`product_id` ";
		List<Object[]> list=this.psiTransportOrderDao.findBySql(sql, new Parameter(id));
		LcPsiTransportOrder order=new LcPsiTransportOrder();
		List<LcPsiTransportOrderItem>	 items=Lists.newArrayList();
		for (int i=0;i<list.size();i++) {
			Object[] obj=list.get(i);
			if(i==0){
				order.setId(Integer.parseInt(obj[0].toString()));
				order.setSuffixName(obj[1]==null?null:obj[1].toString());
				order.setTransportNo(obj[2].toString());
				order.setCreateDate((Timestamp)obj[3]);
				order.setToCountry(obj[4]==null?null:obj[4].toString());
				order.setVolume(Float.parseFloat(obj[35].toString()));
			}
			LcPsiTransportOrderItem item=new LcPsiTransportOrderItem();
			PsiProduct product=new PsiProduct();
			product.setId(Integer.parseInt(obj[5].toString()));
			item.setCurrency(obj[6]==null?null:obj[6].toString());
			item.setProductPrice(obj[7]==null?0f:Float.parseFloat(obj[7].toString()));
			item.setLowerPrice(obj[31]==null?0f:Float.parseFloat(obj[31].toString()));
			item.setItemPrice(obj[32]==null?0f:Float.parseFloat(obj[32].toString()));
			item.setImportPrice(obj[33]==null?0f:Float.parseFloat(obj[33].toString()));
			item.setQuantity(Integer.parseInt(obj[8].toString()));
			item.setPackQuantity(Integer.parseInt(obj[9].toString()));
			item.setCountryCode(obj[28]==null?null:obj[28].toString());
			item.setProductName(obj[37]==null?"":obj[37].toString());
		    item.setColorCode(obj[38]==null?"":obj[38].toString());
			product.setChineseName(obj[10]==null?null:obj[10].toString());
			product.setGw((BigDecimal)obj[11]);
			product.setBrand(obj[12].toString());
			product.setModel(obj[13].toString());
			if(product.getModel().endsWith("US")||product.getModel().endsWith("JP")||product.getModel().endsWith("UK")||product.getModel().endsWith("EU")||product.getModel().endsWith("DE")){
				product.setModel(obj[13].toString().replace("US","").replace("JP","").replace("UK","").replace("EU","").replace("DE",""));
			}else{
				product.setModel(obj[13].toString());
			}
			
			product.setType(obj[14].toString());
			product.setPackLength((BigDecimal)obj[15]);
			product.setPackWidth((BigDecimal)obj[16]);
			product.setPackHeight((BigDecimal)obj[17]);
			product.setBoxVolume((BigDecimal)obj[18]);
			product.setEuHscode(obj[19]==null?null:obj[19].toString());
			product.setCaHscode(obj[20]==null?null:obj[20].toString());
			product.setJpHscode(obj[21]==null?null:obj[21].toString());
			product.setUsHscode(obj[22]==null?null:obj[22].toString());
			product.setHkHscode(obj[23]==null?null:obj[23].toString());
			product.setTaxRefund(obj[36]==null?0:Integer.parseInt(obj[36].toString()));
			order.setWeight(Float.parseFloat(obj[24].toString()));
			order.setBoxNumber(Integer.parseInt(obj[25]==null?"0":obj[25].toString()));
			order.setModel(obj[26].toString());
			product.setCnHscode(obj[27]==null?null:obj[27].toString());
			product.setMaterial(obj[29]==null?null:obj[29].toString());
			product.setMxHscode(obj[34]==null?null:obj[34].toString());
			item.setId(Integer.parseInt(obj[30].toString()));
			item.setProduct(product);
			items.add(item);
		}
		order.setItems(items);
		return order;
	}
	
	public List<Object[]> getCountBySingleProduct(String productId,String startDate,String endDate){
		String sql ="SELECT a.`transport_no`,a.`model`,a.`to_country`, b.`quantity` FROM lc_psi_transport_order AS a,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND a.`transport_sta`<>'8' AND b.`del_flag`='0' AND a.`pick_up_date` BETWEEN :p2 AND :p3 AND b.`product_id`=:p1 ";
		return  this.psiTransportOrderDao.findBySql(sql, new Parameter(productId,startDate,endDate));
	}
	
	/**
	 *报关单号 
	 */
	public Map<String,String> getTransportDeclareNo(List<String> tranNos){
		Map<String,String> rs = Maps.newHashMap();
		String sql ="SELECT a.`transport_no`,a.`declare_no`,a.export_date FROM lc_psi_transport_order AS a  WHERE a.`declare_no` <>'' AND a.`transport_no` IN :p1 ";
		List<Object[]>  list=  this.psiTransportOrderDao.findBySql(sql, new Parameter(tranNos));
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				rs.put(obj[0].toString(), (obj[1]!=null?obj[1].toString():" ")+",,"+(obj[2]!=null?obj[2].toString():" "));
			}
		}
		return rs;
	}
	
	
	public List<Object[]> getCountByAllProduct(String startDate,String endDate){
		String sql ="SELECT a.`transport_no`,a.`model`,a.`to_country`, SUM(b.`quantity`),b.`product_id`,b.`product_name` FROM lc_psi_transport_order AS a,lc_psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND a.`transport_sta`<>'8' AND b.`del_flag`='0'  AND a.`pick_up_date` BETWEEN :p1 AND :p2  GROUP BY b.`transport_order_id`,b.`product_id` ";
		return  this.psiTransportOrderDao.findBySql(sql, new Parameter(startDate,endDate));
	}
	
	public LcPsiTransportOrder get(String transportNo) {
		DetachedCriteria dc = this.psiTransportOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("transportNo", transportNo));  
		List<LcPsiTransportOrder> rs = this.psiTransportOrderDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	
	
	public LcPsiTransportOrder getByFbaShipmentNo(String shipmentNo) {
		DetachedCriteria dc = this.psiTransportOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("shipmentId", shipmentNo));
		dc.add(Restrictions.ne("transportSta", "8"));
		List<LcPsiTransportOrder> rs = this.psiTransportOrderDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	
	
	public LcPsiTransportOrder getLikeFbaShipmentNo(String shipmentId,Integer fbaId,String orderSta) {
		DetachedCriteria dc = this.psiTransportOrderDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(shipmentId)){
			dc.add(Restrictions.like("shipmentId", "%"+shipmentId+"%"));
		}else if(fbaId!=null){
			dc.add(Restrictions.sqlRestriction(" FIND_IN_SET("+fbaId+",fba_inbound_id)"));
		}else{
			return null;
		}
		if(StringUtils.isNotEmpty(orderSta)){
			dc.add(Restrictions.eq("transportSta", orderSta));
		}
		List<LcPsiTransportOrder> rs = this.psiTransportOrderDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	
	public Page<LcPsiTransportOrder> find(Page<LcPsiTransportOrder> page, LcPsiTransportOrder psiTransportOrder) {
		DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(psiTransportOrder.getTransportNo())){
			String content=psiTransportOrder.getTransportNo();
			dc.createAlias("this.items", "item");
			dc.add(Restrictions.or(Restrictions.like("transportNo","%"+content+"%"),
					Restrictions.like("item.productName", "%"+content+"%") ,Restrictions.like("item.sku", "%"+content+"%") ,
					Restrictions.like("shipmentId", "%"+content+"%"),Restrictions.like("ladingBillNo", "%"+content+"%")));
		}
		if(StringUtils.isNotEmpty(psiTransportOrder.getTransportSta())){
			dc.add(Restrictions.eq("transportSta", psiTransportOrder.getTransportSta()));
		}else{
			dc.add(Restrictions.ne("transportSta", "8"));
		}
		
		if(StringUtils.isNotEmpty(psiTransportOrder.getModel())){
			dc.add(Restrictions.eq("model", psiTransportOrder.getModel()));
		}else{
			dc.add(Restrictions.ne("model","4"));
		}
		
		if(psiTransportOrder.getBoxNumber()!=null&&psiTransportOrder.getBoxNumber()>0){
			dc.add(Restrictions.eq("boxNumber", psiTransportOrder.getBoxNumber()));
		}
		
		if(StringUtils.isNotEmpty(psiTransportOrder.getToCountry())){
			if("eu".equals(psiTransportOrder.getToCountry())){
				dc.add(Restrictions.in("toCountry", new String[]{"DE","de","uk","fr","it","es"}));
			}else if("com".equals(psiTransportOrder.getToCountry())){
				dc.add(Restrictions.in("toCountry", new String[]{"US","ca","com","mx"}));
			}else{
				dc.add(Restrictions.eq("toCountry", "jp"));
			}
			
		}
		
		if(psiTransportOrder.getVendor1()!=null&&psiTransportOrder.getVendor1().getId()!=null){
			Integer vendorId=psiTransportOrder.getVendor1().getId();
			dc.add(Restrictions.or(Restrictions.eq("vendor1.id", vendorId),Restrictions.eq("vendor2.id", vendorId),Restrictions.eq("vendor3.id", vendorId),Restrictions.eq("vendor4.id", vendorId)));
		}
		
		if(psiTransportOrder.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",psiTransportOrder.getCreateDate()));
		}
		
		if(psiTransportOrder.getEtdDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(psiTransportOrder.getEtdDate(),1)));
		}
		
		if(StringUtils.isNotEmpty(psiTransportOrder.getTransportType())){
			dc.add(Restrictions.eq("transportType",psiTransportOrder.getTransportType())); 
		}
		
		return psiTransportOrderDao.find2(page, dc);
	}
	
	
	public List<LcPsiTransportOrder> findByProductId(Integer productId) {
			DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
			dc.createAlias("this.items", "item");
			dc.add(Restrictions.eq("item.product.id", productId));
			dc.add(Restrictions.eq("transportSta", "0"));
		return psiTransportOrderDao.find(dc);
	}
	
	public List<LcPsiTransportOrder> findOutboundOrder() {
		DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
		dc.add(Restrictions.or(Restrictions.ne("declareAmount",null), Property.forName("declareAmount").isNotNull()));
		dc.add(Restrictions.eq("transportSta", "1"));
		//dc.add(Restrictions.ge("createDate","2016-10-20"));
	    List<LcPsiTransportOrder> list = psiTransportOrderDao.find(dc);
		if(list!=null&&list.size()>0){
			for(LcPsiTransportOrder e:list){
				Hibernate.initialize(e.getItems());
			}
		}
		return list;
   }
	
	public List<Integer> findOutboundOrderId(){
		String sql="SELECT id FROM lc_psi_transport_order r WHERE r.`transport_sta`='1' AND r.`declare_amount` IS NULL";
		return psiTransportOrderDao.findBySql(sql);
	}
	
	public List<LcPsiTransportOrder> findByUnlineOrderId(Integer unlineOrderId) {
		DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("unlineOrder", unlineOrderId));
		dc.add(Restrictions.eq("transportSta", "0"));
		return psiTransportOrderDao.find(dc);
	}
	
	public List<LcPsiTransportOrder> exp(LcPsiTransportOrder psiTransportOrder) {
		DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(psiTransportOrder.getTransportNo())){
			dc.add(Restrictions.or(Restrictions.like("transportNo","%"+psiTransportOrder.getTransportNo()+"%"),Restrictions.like("shipmentId", "%"+psiTransportOrder.getShipmentId()+"%")));
		}
		if(StringUtils.isNotEmpty(psiTransportOrder.getTransportSta())){
			dc.add(Restrictions.eq("transportSta", psiTransportOrder.getTransportSta()));
		}else{
			dc.add(Restrictions.ne("transportSta", "8"));
		}
		
		if(StringUtils.isNotEmpty(psiTransportOrder.getModel())){
			dc.add(Restrictions.eq("model", psiTransportOrder.getModel()));
		}
		
		if(psiTransportOrder.getVendor1()!=null&&psiTransportOrder.getVendor1().getId()!=null){
			Integer vendorId=psiTransportOrder.getVendor1().getId();
			dc.add(Restrictions.or(Restrictions.eq("vendor1.id", vendorId),Restrictions.eq("vendor2.id", vendorId),Restrictions.eq("vendor3.id", vendorId),Restrictions.eq("vendor4.id", vendorId)));
		}
		
		if(psiTransportOrder.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",psiTransportOrder.getCreateDate()));
		}
		if(psiTransportOrder.getEtdDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(psiTransportOrder.getEtdDate(),1)));
		}
		if(psiTransportOrder.getFromStore()!=null&&psiTransportOrder.getFromStore().getId()!=null){
			dc.createAlias("this.fromStore", "fromStore");
			dc.add(Restrictions.eq("fromStore.id",psiTransportOrder.getFromStore().getId()));
		}
		return psiTransportOrderDao.find(dc);
	}
	
	public List<LcPsiTransportOrder> expNew(LcPsiTransportOrder psiTransportOrder) {
		DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(psiTransportOrder.getTransportNo())){
			dc.add(Restrictions.or(Restrictions.like("transportNo","%"+psiTransportOrder.getTransportNo()+"%"),Restrictions.like("shipmentId", "%"+psiTransportOrder.getShipmentId()+"%")));
		}
		if(StringUtils.isNotEmpty(psiTransportOrder.getTransportSta())){
			dc.add(Restrictions.eq("transportSta", psiTransportOrder.getTransportSta()));
		}else{
			dc.add(Restrictions.ne("transportSta", "8"));
		}
		
//		dc.add(Restrictions.ne("model", "1"));
		
		if(psiTransportOrder.getVendor1()!=null&&psiTransportOrder.getVendor1().getId()!=null){
			Integer vendorId=psiTransportOrder.getVendor1().getId();
			dc.add(Restrictions.or(Restrictions.eq("vendor1.id", vendorId),Restrictions.eq("vendor2.id", vendorId),Restrictions.eq("vendor3.id", vendorId),Restrictions.eq("vendor4.id", vendorId)));
		}
		
		if(psiTransportOrder.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",psiTransportOrder.getCreateDate()));
		}
		
		if(psiTransportOrder.getEtdDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(psiTransportOrder.getEtdDate(),1)));
		}
		
		dc.addOrder(Order.desc("id"));
		return psiTransportOrderDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void addSaveData(LcPsiTransportOrder psiTransportOrder) throws IOException {
		this.clearSupplierData(psiTransportOrder);
		//查询新品没绑定条码的
		Set<String> newProducts = Sets.newHashSet();
		List<String> list=psiProductService.findNewProducts();
		if(list!=null&&list.size()>0){
			newProducts.addAll(list);
		}
		
		Float volume =0f;
		Float weight=0f;
		Set<Integer> productIds = Sets.newHashSet();
		for(LcPsiTransportOrderItem item:psiTransportOrder.getItems()){
			productIds.add(item.getProduct().getId());
		}
		
		//算出产品重量体积
		Map<Integer,String>	volumeWeightMap=this.psiProductService.getVomueAndWeight(productIds);
				
		for(LcPsiTransportOrderItem item:psiTransportOrder.getItems()){
			item.setTransportOrder(psiTransportOrder);
			
			//如果为新品且没有sku,组合sku
			if(newProducts.contains(item.getProductColorCountry())&&StringUtils.isEmpty(item.getSku())){//如果sku为空并且为新品无条码的产品，设置sku
				item.setSku(item.getProductColorCountry());
			}
			Integer productId = item.getProduct().getId();
			if(!"1".equals(item.getProduct().getComponents())){
				volume+=item.getQuantity()/(float)item.getPackQuantity()*(Float.parseFloat(volumeWeightMap.get(productId).split(",")[0]));
				weight+=item.getQuantity()/(float)item.getPackQuantity()*(Float.parseFloat(volumeWeightMap.get(productId).split(",")[1]));
			}
		}
		psiTransportOrder.setVolume(volume);
		psiTransportOrder.setWeight(weight);
		psiTransportOrder.setTransportSta("0");//草稿状态
		psiTransportOrder.setPaymentSta("0");//未付款状态
		psiTransportOrder.setCreateDate(new Date());
		psiTransportOrder.setCreateUser(UserUtils.getUser());
		
		//由收货仓库查询去向国
		//如果运输类型不为批量发货
		if(!"2".equals(psiTransportOrder.getTransportType())&&!"3".equals(psiTransportOrder.getTransportType())){
			Stock stock = stockService.get(psiTransportOrder.getToStore().getId());
			String country="";
			if(StringUtils.isEmpty(stock.getPlatform())){
				country=stock.getCountrycode();
			}else{
				country=stock.getPlatform();
			};
			psiTransportOrder.setToCountry(country);
			psiTransportOrder.setDestinationDetail(null);
		}else{
			psiTransportOrder.setToStore(null);
			if(StringUtils.isBlank(psiTransportOrder.getDestinationDetail())){
				psiTransportOrder.setToCountry(psiTransportOrder.getItems().get(0).getCountryCode());
			}else{
				psiTransportOrder.setToCountry(psiTransportOrder.getDestinationDetail());
			}
		}
		psiTransportOrderDao.save(psiTransportOrder);
	}
	
	@Transactional(readOnly = false)
	public String createFlowNo() throws IOException {
		synchronized (LcPsiTransportOrderService.class){
			return this.genDao.genSequenceByMonth("_LC_YD",3);
		}
	}
	
	
	@Transactional(readOnly = false)
	public void editSaveData(LcPsiTransportOrder psiTransportOrder,String filePath,MultipartFile[] localFile,MultipartFile[] tranFile,MultipartFile[] dapFile,MultipartFile[] otherFile,MultipartFile[] otherFile1,MultipartFile[] insuranceFile,MultipartFile[] taxFile) throws IOException {
		Set<Integer>  delItemSet = Sets.newHashSet();
		Set<Integer>  delConertainItemSet =Sets.newHashSet();
		this.clearSupplierData(psiTransportOrder);
		//查询新品没绑定条码的
		Set<String> newProducts = Sets.newHashSet();
		List<String> list=this.psiProductService.findNewProducts();
		if(list!=null&&list.size()>0){
			newProducts.addAll(list);
		}
				
		//保存附件
		this.saveAttachment(psiTransportOrder, filePath, localFile, tranFile, dapFile, otherFile,otherFile1, insuranceFile, taxFile);
		
		Set<String> setNewIds = new HashSet<String>();
		Set<String> setConertainIds = new HashSet<String>();
		for(LcPsiTransportOrderItem item:psiTransportOrder.getItems()){
			if(item.getId()!=null){
				setNewIds.add(item.getId().toString());
			}
			item.setTransportOrder(psiTransportOrder);
			//如果为新品且没有sku,组合sku
			if(newProducts.contains(item.getProductColorCountry())&&StringUtils.isEmpty(item.getSku())){//如果sku为空并且为新品无条码的产品，设置sku
				item.setSku(item.getProductColorCountry());
			}
			
		}
		
		
		
		
		String oldItemIds=psiTransportOrder.getOldItemIds();
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
		
		if(!StringUtils.isEmpty(psiTransportOrder.getOldContainerIds())&&psiTransportOrder.getContainerItems()==null){
			String oldConertainIds=psiTransportOrder.getOldContainerIds();
			String [] oldConnertainIdArr = oldConertainIds.split(",");
			for(int j=0;j<oldConnertainIdArr.length;j++){
				if(!StringUtils.isEmpty(oldConnertainIdArr[j])){
					this.psiTransportOrderDao.deleteConertainItem(Integer.valueOf(oldConnertainIdArr[j]));
				}
			}
		}
		
		//保存集装箱信息   
		if(psiTransportOrder.getContainerItems()!=null&&psiTransportOrder.getContainerItems().size()>0){
			String oldConertainIds=psiTransportOrder.getOldContainerIds();
			String [] oldConnertainIdArr = oldConertainIds.split(",");
			if(psiTransportOrder.getOceanModel().equals("FCL")){
				Integer teu = 0;
				for (Iterator<LcPsiTransportOrderContainer> iterator = psiTransportOrder.getContainerItems().iterator(); iterator.hasNext();) {
					LcPsiTransportOrderContainer item = (LcPsiTransportOrderContainer) iterator.next();
					if(item.getId()!=null&&item.getQuantity()!=null){
						setConertainIds.add(item.getId().toString());
						Integer cargoNums=item.getQuantity();
						if(cargoNums!=null&&!cargoNums.equals(0)){
							if(item.getContainerType().equals("20GP")){
								teu+=1*cargoNums;
							}else if(item.getContainerType().equals("40GP")){
								teu+=2*cargoNums;
							}else if(item.getContainerType().equals("40HQ")){
								teu+=2*cargoNums;
							}else if(item.getContainerType().equals("45HQ")){
								teu+=2*cargoNums;
							}
						}
						item.setTransportOrder(psiTransportOrder);
					}else{
						iterator.remove();
						continue;
					}
				}
			//保存装箱记数
			if(teu>0){
				psiTransportOrder.setTeu(teu);
			}
			
			if(setConertainIds!=null&&setConertainIds.size()>0){
				for(int j=0;j<oldConnertainIdArr.length;j++){
					if(!setConertainIds.contains(oldConnertainIdArr[j])){
						delConertainItemSet.add(Integer.valueOf(oldConnertainIdArr[j]));
					};
				}
			}else{
				//说明原来的都删除了
				for(int j=0;j<oldConnertainIdArr.length;j++){
					if(!StringUtils.isEmpty(oldConnertainIdArr[j])){
						delConertainItemSet.add(Integer.valueOf(oldConnertainIdArr[j]));
					}
				}
			}
		}else if(psiTransportOrder.getOceanModel().equals("LCL")){
			//有可能是FCL  转LCL 这样的也要删掉
			for(int j=0;j<oldConnertainIdArr.length;j++){
				if(!StringUtils.isEmpty(oldConnertainIdArr[j])){
					delConertainItemSet.add(Integer.valueOf(oldConnertainIdArr[j]));
				}
			}
		}
		
	}
		
		//由收货仓库查询去向国
		//如果运输类型不为批量发货
		if(!"2".equals(psiTransportOrder.getTransportType())&&!"3".equals(psiTransportOrder.getTransportType())){
			Stock stock = stockService.get(psiTransportOrder.getToStore().getId());
			String country="";
			if(StringUtils.isEmpty(stock.getPlatform())){
				country=stock.getCountrycode();
			}else{
				country=stock.getPlatform();
			};
			psiTransportOrder.setToCountry(country);
			psiTransportOrder.setDestinationDetail(null);
		}else{
			psiTransportOrder.setToStore(null);
			if(StringUtils.isBlank(psiTransportOrder.getDestinationDetail())){
				psiTransportOrder.setToCountry(psiTransportOrder.getItems().get(0).getCountryCode());
			}else{
				psiTransportOrder.setToCountry(psiTransportOrder.getDestinationDetail());
			}
		}
				
		
		if(delItemSet.size()>0){
			for(LcPsiTransportOrderItem item:this.psiTransportOrderItemService.getTransportOrderItems(delItemSet)){
				item.setDelFlag("1");
				item.setTransportOrder(psiTransportOrder);
				psiTransportOrder.getItems().add(item);
			};
		}
		
		if(delConertainItemSet.size()>0){
			for(LcPsiTransportOrderContainer item:this.psiTransportContainerService.getTransportContainerItems(delConertainItemSet)){
				item.setDelFlag("1");
				item.setTransportOrder(psiTransportOrder);
				psiTransportOrder.getContainerItems().add(item);
			}
		}
		
		//放入第一次eta时间
		if(psiTransportOrder.getFirstEtaDate()==null&&psiTransportOrder.getPreEtaDate()!=null){
			psiTransportOrder.setFirstEtaDate(psiTransportOrder.getPreEtaDate());
		}
		this.psiTransportOrderDao.getSession().merge(psiTransportOrder);
		
	}
	
	
	@Transactional(readOnly = false)
	public void editSaveData(LcPsiTransportOrder psiTransportOrder) throws IOException {
		Set<Integer>  delItemSet = Sets.newHashSet();
		Set<Integer>  delConertainItemSet =Sets.newHashSet();
		//查询新品没绑定条码的
		Set<String> newProducts = Sets.newHashSet();
		List<String> list=this.psiProductService.findNewProducts();
		if(list!=null&&list.size()>0){
			newProducts.addAll(list);
		}
		this.clearSupplierData(psiTransportOrder);
		
		//如果不是fba运输，清空fbaId和shipmentId
		if(!"1".equals(psiTransportOrder.getTransportType())){
			psiTransportOrder.setFbaInboundId(null);
			psiTransportOrder.setShipmentId(null);
		}
		
		Set<String> setNewIds = new HashSet<String>();
		Set<String> setConertainIds = new HashSet<String>();
		Float  volume=0f;
		Float  weight=0f;
		Integer boxNumber=0;
		Set<Integer> productIdSet =Sets.newHashSet();
		//保存货物清单信息
		for(LcPsiTransportOrderItem item:psiTransportOrder.getItems()){
			if(item.getId()!=null){
				setNewIds.add(item.getId().toString());
			}
			productIdSet.add(item.getProduct().getId());
			item.setTransportOrder(psiTransportOrder);
			//如果为新品且没有sku,组合sku
			if(newProducts.contains(item.getProductColorCountry())&&StringUtils.isEmpty(item.getSku())){//如果sku为空并且为新品无条码的产品，设置sku
				item.setSku(item.getProductColorCountry());
			}
		}
		
		
		if("0".equals(psiTransportOrder.getTransportSta())){
			//如果已出库之前都可以改
			Map<Integer,String> volumeWeightMap=this.psiProductService.getVomueAndWeight(productIdSet);
			for(LcPsiTransportOrderItem item:psiTransportOrder.getItems()){
				String volumeWeight=volumeWeightMap.get(item.getProduct().getId());
				Float rate= item.getQuantity()/Float.parseFloat(volumeWeight.split(",")[2]);
				if(!"1".equals(item.getProduct().getComponents())){
					Float itemVolue= Float.parseFloat(volumeWeight.split(",")[0])*rate;
					Float itemWeight= Float.parseFloat(volumeWeight.split(",")[1])*rate;
					volume+=itemVolue;
					weight+=itemWeight;
					boxNumber+=MathUtils.roundUp(item.getQuantity()*1.0d/item.getPackQuantity());
				}
			}
			psiTransportOrder.setWeight(weight);
			psiTransportOrder.setVolume(volume);
			psiTransportOrder.setBoxNumber(boxNumber);
		}
		
		String oldItemIds=psiTransportOrder.getOldItemIds();
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
		
		if(!StringUtils.isEmpty(psiTransportOrder.getOldContainerIds())&&psiTransportOrder.getContainerItems()==null){
			String oldConertainIds=psiTransportOrder.getOldContainerIds();
			String [] oldConnertainIdArr = oldConertainIds.split(",");
			for(int j=0;j<oldConnertainIdArr.length;j++){
				if(!StringUtils.isEmpty(oldConnertainIdArr[j])){
					delConertainItemSet.add(Integer.valueOf(oldConnertainIdArr[j]));
				}
			}
		}
		
		//保存集装箱信息   
		if(psiTransportOrder.getContainerItems()!=null&&psiTransportOrder.getContainerItems().size()>0){
			String oldConertainIds=psiTransportOrder.getOldContainerIds();
			String [] oldConnertainIdArr = oldConertainIds.split(",");
			
			if(psiTransportOrder.getOceanModel().equals("FCL")){
				Integer teu = 0;
				for(LcPsiTransportOrderContainer item:psiTransportOrder.getContainerItems()){
					if(item.getId()!=null&&item.getQuantity()!=null){
						setConertainIds.add(item.getId().toString());
					}
					Integer cargoNums=item.getQuantity();
					if(cargoNums!=null&&!cargoNums.equals(0)){
						if(item.getContainerType().equals("20GP")){
							teu+=1*cargoNums;
						}else if(item.getContainerType().equals("40GP")){
							teu+=2*cargoNums;
						}else if(item.getContainerType().equals("40HQ")){
							teu+=2*cargoNums;
						}
				}
				item.setTransportOrder(psiTransportOrder);
				
			}
			
			//保存装箱记数
			if(teu>0){
				psiTransportOrder.setTeu(teu);
			}
			
			if(setConertainIds!=null&&setConertainIds.size()>0){
				for(int j=0;j<oldConnertainIdArr.length;j++){
					if(!setConertainIds.contains(oldConnertainIdArr[j])){
						delConertainItemSet.add(Integer.valueOf(oldConnertainIdArr[j]));
					};
				}
			}else{
				//说明原来的都删除了
				for(int j=0;j<oldConnertainIdArr.length;j++){
					if(!StringUtils.isEmpty(oldConnertainIdArr[j])){
						delConertainItemSet.add(Integer.valueOf(oldConnertainIdArr[j]));
					}
				}
			}
		}else if(psiTransportOrder.getOceanModel().equals("LCL")){
			//有可能是FCL  转LCL 这样的也要删掉
			for(int j=0;j<oldConnertainIdArr.length;j++){
				if(!StringUtils.isEmpty(oldConnertainIdArr[j])){
					delConertainItemSet.add(Integer.valueOf(oldConnertainIdArr[j]));
				}
			}
		}
		
		}
		
		//由收货仓库查询去向国
		//如果运输类型不为批量发货
		if(!"2".equals(psiTransportOrder.getTransportType())&&!"3".equals(psiTransportOrder.getTransportType())){
			Stock stock = stockService.get(psiTransportOrder.getToStore().getId());
			String country="";
			if(StringUtils.isEmpty(stock.getPlatform())){
				country=stock.getCountrycode();
			}else{
				country=stock.getPlatform();
			};
			psiTransportOrder.setToCountry(country);
			psiTransportOrder.setDestinationDetail(null);
		}else{
			psiTransportOrder.setToStore(null);
			psiTransportOrder.setToCountry(psiTransportOrder.getDestinationDetail());
		}
				
		
		//把删除的查出来放到items上
		if(delItemSet.size()>0){
			for(LcPsiTransportOrderItem item:this.psiTransportOrderItemService.getTransportOrderItems(delItemSet)){
				item.setDelFlag("1");
				item.setTransportOrder(psiTransportOrder);
				psiTransportOrder.getItems().add(item);
			};
		}
		
		if(delConertainItemSet.size()>0){
			for(LcPsiTransportOrderContainer item:this.psiTransportContainerService.getTransportContainerItems(delConertainItemSet)){
				item.setDelFlag("1");
				item.setTransportOrder(psiTransportOrder);
				psiTransportOrder.getContainerItems().add(item);
			};
		}
		
		//如果是fba运输，新建状态  依靠shipmentId，获取fbaId     前台shipmentId是多选
		if("1".equals(psiTransportOrder.getTransportType())&&"0".equals(psiTransportOrder.getTransportSta())&&StringUtils.isNotEmpty(psiTransportOrder.getShipmentId())){
			String fbaIds = this.findFbaIdByShipmentIds(Arrays.asList(psiTransportOrder.getShipmentId().split(",")));
			psiTransportOrder.setFbaInboundId(fbaIds);
		}
		this.psiTransportOrderDao.getSession().merge(psiTransportOrder);
	}
	
	
	 public String findFbaIdByShipmentIds(List<String> shipmentIds){
		  String sql="SELECT GROUP_CONCAT(a.`id`) FROM psi_fba_inbound AS a WHERE a.`shipment_id` IN :p1";
		  List<Object> list = psiTransportOrderDao.findBySql(sql,new Parameter(shipmentIds));
		  if(list!=null&&list.size()>0&&list.get(0)!=null){
				return list.get(0).toString();
		  }
		  return null;
	  }
	
	@Transactional(readOnly = false)
	public void updateSta(String cancelSta,Integer orderId) {
		String updateSql ="UPDATE lc_psi_transport_order  SET transport_sta=:p1 WHERE id=:p2";
		psiTransportOrderDao.updateBySql(updateSql, new Parameter(cancelSta,orderId));
	}

	/**
	 *查找要付款的运单   草稿和申请
	 */
	public List<LcPsiTransportOrder> findUnDonePayment(Integer supplierId) {
		DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
		
		dc.add(Restrictions.ne("transportSta", "8"));
		dc.add(Restrictions.in("paymentSta", new String[]{"0","1"}));
		dc.add(Restrictions.or(Restrictions.eq("vendor1.id", supplierId),Restrictions.eq("vendor2.id", supplierId),Restrictions.eq("vendor3.id", supplierId),
				Restrictions.eq("vendor4.id", supplierId),Restrictions.eq("vendor5.id", supplierId),Restrictions.eq("vendor6.id", supplierId),
				Restrictions.eq("vendor7.id", supplierId)));
		dc.addOrder(Order.desc("id"));
		return this.psiTransportOrderDao.find(dc);
	}
	
	/**
	 *查找所有新建状态的运单
	 */
	public List<LcPsiTransportOrder> findTranOrderBySta(Integer fromStoreId,String orderSta) {
		DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("fromStore.id", fromStoreId));
		dc.add(Restrictions.eq("transportSta", orderSta));
		dc.addOrder(Order.asc("id"));
		return this.psiTransportOrderDao.find(dc);
	}
	
	/**
	 *根据某产品  查询在途的  产品数量
	 *@param sku
	 */
	public Integer findOnWayTranQuantity(String sku){
		DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
		if(!StringUtils.isEmpty(sku)){
			dc.add(Restrictions.eq("item.sku", sku));
		}
		dc.addOrder(Order.asc("id"));
		List<LcPsiTransportOrder> orders =this.psiTransportOrderDao.find(dc);
		Integer quantity = 0;
		for(LcPsiTransportOrder order :orders){
			for(LcPsiTransportOrderItem item:order.getItems()){
				quantity+=item.getShippedQuantity();
			}
		}
		
		return quantity;
	}
	
	/**
	 *根据某产品  查询在途的  产品数量
	 *@param sku
	 */
	public Map<String,Integer> findOnWayTran(){
		DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
		dc.addOrder(Order.asc("id"));
		List<LcPsiTransportOrder> orders =this.psiTransportOrderDao.find(dc);
		Map<String,Integer>  map = Maps.newHashMap();
		for(LcPsiTransportOrder order :orders){
			for(LcPsiTransportOrderItem item:order.getItems()){
				Integer quantity =item.getShippedQuantity();
				String sku ="";
				if(map.get(sku)!=null){
					quantity+=map.get(sku);
				}
				map.put(sku, quantity);
			}
		}
		
		return map;
	}
	
	
	/**
	 *根据某产品  查询在途的  产品数量
	 *@param sku
	 */
	public List<PsiInventory> findOnWayTran(Integer storeId){
		String sql="SELECT i.`sku`,SUM(i.`quantity`) AS quantity FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS i WHERE a.`id`=i.`transport_order_id` AND a.transport_sta in ('1','2','3') AND a.to_store=:p1 GROUP BY i.sku ";
		List<Object> objs=this.psiTransportOrderDao.findBySql(sql, new Parameter(storeId));
		List<PsiInventory> rs = Lists.newArrayList();
		for (Object object : objs) {
			Object[]obs = (Object[])object;
			rs.add(new PsiInventory(Integer.parseInt(obs[1].toString()),obs[0].toString()));
		}
		return rs;
	}
	
	/**
	 *查找所有新建状态的、并且入库仓库为    本地仓的    运单
	 */
	public List<LcPsiTransportOrder> findInventoryInTranOrder(String[] orderStas,Integer storeId,String[] transportTypeStas) {
		DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("toStore.id",storeId));
		dc.add(Restrictions.in("transportSta", orderStas));
		dc.add(Restrictions.in("transportType", transportTypeStas));
		dc.addOrder(Order.asc("id"));
		return this.psiTransportOrderDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void save(LcPsiTransportOrder psiTransportOrder) {
		this.psiTransportOrderDao.save(psiTransportOrder);
	}
	
	
	@Transactional(readOnly = false)
	public void saveList(List<LcPsiTransportOrder> psiTransportOrders) {
		this.psiTransportOrderDao.save(psiTransportOrders);
	}
	
	@Transactional(readOnly = false)
	public void merge(LcPsiTransportOrder psiTransportOrder) {
		this.psiTransportOrderDao.getSession().merge(psiTransportOrder);
	}
	
	@Transactional(readOnly = false)
	public String updateLadingBillNo(Integer tranId,String ladingBillNo){
		String sql ="update  lc_psi_transport_order set lading_bill_no=:p1 where id=:p2";
		int i =this.psiTransportOrderDao.updateBySql(sql, new Parameter(ladingBillNo,tranId));
		if(i>0){
			return "true";
		}else{
			return "false";
		}
	}
	
	@Transactional(readOnly = false)
	public String updateEtaDate(Integer tranId,Date etaDate){
		String sql ="update  lc_psi_transport_order set eta_date=:p1 where id=:p2";
		int i =this.psiTransportOrderDao.updateBySql(sql, new Parameter(etaDate,tranId));
		if(i>0){
			return "true";
		}else{
			return "false";
		}
	}
	
	@Transactional(readOnly = false)
	public String updateOperArrivalDate(Integer tranId,Date operArrivalDate){
		String sql ="update  lc_psi_transport_order set oper_arrival_date=:p1 where id=:p2";
		int i =this.psiTransportOrderDao.updateBySql(sql, new Parameter(operArrivalDate,tranId));
		if(i>0){
			return "true";
		}else{
			return "false";
		}
	}
	
	
	/**
	 *根据运单No,跟新fbaId 
	 */
	@Transactional(readOnly = false)
	public void updateFbaId(String tranNo,String fbaId){
		String sql ="UPDATE  lc_psi_transport_order AS a SET a.`fba_inbound_id`=:p1 WHERE a.`transport_no`=:p2";
		this.psiTransportOrderDao.updateBySql(sql, new Parameter(fbaId,tranNo));
	}
	
	@Transactional(readOnly = false)
	public String updateModel(Integer tranId,String model){
		String sql ="update  lc_psi_transport_order set model=:p1 where id=:p2";
		int i =this.psiTransportOrderDao.updateBySql(sql, new Parameter(model,tranId));
		if(i>0){
			return "true";
		}else{
			return "false";
		}
	}
	
	
	//查询目的港list
	public List<String> getPOD(){
		String sql ="SELECT a.`destination` FROM lc_psi_transport_order AS a WHERE a.`transport_sta`<>'8' GROUP BY a.`destination`";
		List<String> list=this.psiTransportOrderDao.findBySql(sql);
		return list;
	}

	//更新费用附件
	
	public void saveAttachment(LcPsiTransportOrder psiTransportOrder,String filePath,MultipartFile[] localFile,MultipartFile[] tranFile,MultipartFile[] dapFile,MultipartFile[] otherFile,MultipartFile[] otherFile1,MultipartFile[] insuranceFile,MultipartFile[] taxFile){
		//判断附件
		if(localFile[0].getSize()>0){
			psiTransportOrder.setLocalPath(null);//如果编辑上传了附件就把原来的清空
			for (MultipartFile attchmentFile : localFile) {
				if(attchmentFile.getSize()!=0){
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/lcPsiTransport";
					File baseDir = new File(baseDirStr+"/"+psiTransportOrder.getTransportNo()); 
					if(!baseDir.isDirectory())
						baseDir.mkdirs();
					String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
					String name=UUID.randomUUID().toString()+suffix;
					File dest = new File(baseDir,name);
					try {
						FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
						psiTransportOrder.setLocalPathAppend(psiTransportOrder.getTransportNo()+"/"+name);
					} catch (IOException e) {
						logger.warn(name+"文件保存失败",e);
					}
				}
			}
		}
		
		if(tranFile[0].getSize()>0){
			psiTransportOrder.setTranPath(null);//如果编辑上传了附件就把原来的清空
			for (MultipartFile attchmentFile : tranFile) {
				if(attchmentFile.getSize()!=0){
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/lcPsiTransport";
					File baseDir = new File(baseDirStr+"/"+psiTransportOrder.getTransportNo()); 
					if(!baseDir.isDirectory())
						baseDir.mkdirs();
					String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
					String name=UUID.randomUUID().toString()+suffix;
					File dest = new File(baseDir,name);
					try {
						FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
						psiTransportOrder.setTranPathAppend(psiTransportOrder.getTransportNo()+"/"+name);
					} catch (IOException e) {
						logger.warn(name+"文件保存失败",e);
					}
				}
			}
		}
		
		if(dapFile[0].getSize()>0){
			psiTransportOrder.setDapPath(null);//如果编辑上传了附件就把原来的清空
			for (MultipartFile attchmentFile : dapFile) {
				if(attchmentFile.getSize()!=0){
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/lcPsiTransport";
					File baseDir = new File(baseDirStr+"/"+psiTransportOrder.getTransportNo()); 
					if(!baseDir.isDirectory())
						baseDir.mkdirs();
					String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
					String name=UUID.randomUUID().toString()+suffix;
					File dest = new File(baseDir,name);
					try {
						FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
						psiTransportOrder.setDapPathAppend(psiTransportOrder.getTransportNo()+"/"+name);
					} catch (IOException e) {
						logger.warn(name+"文件保存失败",e);
					}
				}
			}
		}
		
		
		if(otherFile[0].getSize()>0){
			psiTransportOrder.setOtherPath(null);//如果编辑上传了附件就把原来的清空
			for (MultipartFile attchmentFile : otherFile) {
				if(attchmentFile.getSize()!=0){
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/lcPsiTransport";
					File baseDir = new File(baseDirStr+"/"+psiTransportOrder.getTransportNo()); 
					if(!baseDir.isDirectory())
						baseDir.mkdirs();
					String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
					String name=UUID.randomUUID().toString()+suffix;
					File dest = new File(baseDir,name);
					try {
						FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
						psiTransportOrder.setOtherPathAppend(psiTransportOrder.getTransportNo()+"/"+name);
					} catch (IOException e) {
						logger.warn(name+"文件保存失败",e);
					}
				}
			}
		}
		
		
		if(otherFile1[0].getSize()>0){
			psiTransportOrder.setOtherPath1(null);//如果编辑上传了附件就把原来的清空
			for (MultipartFile attchmentFile : otherFile1) {
				if(attchmentFile.getSize()!=0){
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/lcPsiTransport";
					File baseDir = new File(baseDirStr+"/"+psiTransportOrder.getTransportNo()); 
					if(!baseDir.isDirectory())
						baseDir.mkdirs();
					String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
					String name=UUID.randomUUID().toString()+suffix;
					File dest = new File(baseDir,name);
					try {
						FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
						psiTransportOrder.setOtherPath1Append(psiTransportOrder.getTransportNo()+"/"+name);
					} catch (IOException e) {
						logger.warn(name+"文件保存失败",e);
					}
				}
			}
		}
		
		if(insuranceFile[0].getSize()>0){
			psiTransportOrder.setInsurancePath(null);//如果编辑上传了附件就把原来的清空
			for (MultipartFile attchmentFile : insuranceFile) {
				if(attchmentFile.getSize()!=0){
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/lcPsiTransport";
					File baseDir = new File(baseDirStr+"/"+psiTransportOrder.getTransportNo()); 
					if(!baseDir.isDirectory())
						baseDir.mkdirs();
					String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
					String name=UUID.randomUUID().toString()+suffix;
					File dest = new File(baseDir,name);
					try {
						FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
						psiTransportOrder.setInsurancePathAppend(psiTransportOrder.getTransportNo()+"/"+name);
					} catch (IOException e) {
						logger.warn(name+"文件保存失败",e);
					}
				}
			}
		}
		
		
		if(taxFile[0].getSize()>0){
			psiTransportOrder.setTaxPath(null);//如果编辑上传了附件就把原来的清空
			for (MultipartFile attchmentFile : taxFile) {
				if(attchmentFile.getSize()!=0){
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/lcPsiTransport";
					File baseDir = new File(baseDirStr+"/"+psiTransportOrder.getTransportNo()); 
					if(!baseDir.isDirectory())
						baseDir.mkdirs();
					String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
					String name=UUID.randomUUID().toString()+suffix;
					File dest = new File(baseDir,name);
					try {
						FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
						psiTransportOrder.setTaxPathAppend(psiTransportOrder.getTransportNo()+"/"+name);
					} catch (IOException e) {
						logger.warn(name+"文件保存失败",e);
					}
				}
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void updateMixFile(Integer id,String suffixName){
		String sql ="update  lc_psi_transport_order set mix_file=:p1 where id=:p2";
		psiTransportOrderDao.updateBySql(sql, new Parameter(suffixName,id));
	}
	
	@Transactional(readOnly = false)
	public void updateSuffixName(Integer id,String suffixName){
		this.psiTransportOrderDao.updateSuffixName(id,suffixName);
		
	}

	@Transactional(readOnly = false)
	public void updateElsePath(Integer id,String elsePath){
		this.psiTransportOrderDao.updateElsePath(id,elsePath);
	}

	@Transactional(readOnly = false)
	public void updateExportPath(Integer id,String elsePath){
		this.psiTransportOrderDao.updateExportPath(id,elsePath);
	}
	
	public void clearSupplierData(LcPsiTransportOrder psiTransportOrder){
		if(psiTransportOrder.getVendor1()!=null&&psiTransportOrder.getVendor1().getId()==null){
			psiTransportOrder.setVendor1(null);
		}
		if(psiTransportOrder.getVendor2()!=null&&psiTransportOrder.getVendor2().getId()==null){
			psiTransportOrder.setVendor2(null);
		}
		if(psiTransportOrder.getVendor3()!=null&&psiTransportOrder.getVendor3().getId()==null){
			psiTransportOrder.setVendor3(null);
		}
		if(psiTransportOrder.getVendor4()!=null&&psiTransportOrder.getVendor4().getId()==null){
			psiTransportOrder.setVendor4(null);
		}
		if(psiTransportOrder.getVendor5()!=null&&psiTransportOrder.getVendor5().getId()==null){
			psiTransportOrder.setVendor5(null);
		}
		if(psiTransportOrder.getVendor6()!=null&&psiTransportOrder.getVendor6().getId()==null){
			psiTransportOrder.setVendor6(null);
		}
		if(psiTransportOrder.getVendor7()!=null&&psiTransportOrder.getVendor7().getId()==null){
			psiTransportOrder.setVendor7(null);
		}
	}
	
	
	/**
	 *查询运单附件信息 
	 * 
	 */
	
	public Map<String,String> getSupplierCostPath(Map<String,List<LcPsiTransportPaymentItem>> costMap,LcPsiTransportPayment psiTransportPayment){
		Map<String,String> fileMap =Maps.newLinkedHashMap();
		StringBuffer sbPath= new StringBuffer("");
			String sql="SELECT a.`local_path`,a.`tran_path`,a.`dap_path`,a.`other_path`,a.`insurance_path`,a.`tax_path`,a.`other_path1`,a.`transport_no` FROM lc_psi_transport_order AS a WHERE a.`transport_no` in :p1";
			List<Object[]> tranInfos=this.psiTransportOrderDao.findBySql(sql,new Parameter(costMap.keySet()));
			if(tranInfos.size()>0){
				for(Object[] objs:tranInfos){
					String tranNo=objs[7].toString();
					List<LcPsiTransportPaymentItem> list =costMap.get(tranNo);
					for(LcPsiTransportPaymentItem item:list){
						if("LocalAmount".equals(item.getPaymentType())){
							if(objs[0]!=null&&!"".equals(objs[0].toString())){
								fileMap.put(tranNo+"_LocalAmount", objs[0].toString());
								sbPath.append(objs[0].toString()).append(",");
							}
						}else if("TranAmount".equals(item.getPaymentType())){
							if(objs[1]!=null&&!"".equals(objs[1].toString())){
								fileMap.put(tranNo+"_TranAmount", objs[1].toString());
								sbPath.append(objs[1].toString()).append(",");
							}
						}else if("DapAmount".equals(item.getPaymentType())){
							if(objs[2]!=null&&!"".equals(objs[2].toString())){
								fileMap.put(tranNo+"_DapAmount", objs[2].toString());
								sbPath.append(objs[2].toString()).append(",");
							}
						}else if("OtherAmount".equals(item.getPaymentType())){
							if(objs[3]!=null&&!"".equals(objs[3].toString())){
								fileMap.put(tranNo+"_OtherAmount", objs[3].toString());
								sbPath.append(objs[3].toString()).append(",");
							}
						}else if("InsuranceAmount".equals(item.getPaymentType())){
							if(objs[4]!=null&&!"".equals(objs[4].toString())){
								fileMap.put(tranNo+"_InsuranceAmount", objs[4].toString());
								sbPath.append(objs[4].toString()).append(",");
							}
						}else if("TaxAmount".equals(item.getPaymentType())){
							if(objs[5]!=null&&!"".equals(objs[5].toString())){
								fileMap.put(tranNo+"_TaxAmount", objs[5].toString());
								sbPath.append(objs[5].toString()).append(",");
							}
						}else if("OtherAmount1".equals(item.getPaymentType())){
							if(objs[6]!=null&&!"".equals(objs[6].toString())){
								fileMap.put(tranNo+"_OtherAmount1", objs[6].toString());
								sbPath.append(objs[6].toString()).append(",");
							}
						}
					}
				}
			}
		
		if(sbPath.length()>1){
			psiTransportPayment.setSupplierCostPath(sbPath.toString().substring(0,sbPath.length()-1));
		}
		return fileMap;
	}
	
	
	/**
	 *查询产品价格 
	 * 
	 */
	
	public String getProPriceByProductId(Integer productId){
		return productTieredPriceService.getPriceBaseMoqNoColorNoSupplier(productId);
	}
	
	public Map<String,String> getProPriceByProductId(){
		return productTieredPriceService.getPriceBaseMoqNoColorNoSupplier();
	}
	
	public Map<String,String> getProPriceByProductId2(){
		return productTieredPriceService.getPriceBaseMoqNoColorNoSupplier2();
	}
	
	public Map<String,String> getProPriceByProductId3(){
		return productTieredPriceService.getPriceBaseMoqNoColorNoSupplier3();
	}
	
	/**
	 *获取未发货的，已匹配的shippmentId 
	 */
	public Float getPartsPriceByProductId(Integer productId,String color){
		Float price=0f;
		String sql="SELECT SUM(CASE WHEN b.`price` IS NULL THEN (b.`rmb_price`/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+") ELSE b.`price` END) FROM psi_product_parts AS a,psi_parts AS b " +
				"	WHERE a.`parts_id`=b.`id` AND a.`product_id`=:p1 AND a.`color`=:p2  GROUP BY a.`product_id`,a.`color` ";
		List<Object> objs=  this.psiTransportOrderDao.findBySql(sql,new Parameter(productId,color));
		if(objs!=null&&objs.size()>0){
			for(Object obj:objs){
				price=Float.parseFloat(obj==null?"0":obj.toString());
			}
		}
		return price;
	}
	
	
	/**
	 * 删除运单items
	 * 
	 */
	@Transactional(readOnly = false)
	public void deleteOrderItems(Set<Integer> itemIds) {
		Parameter parameter =new Parameter(itemIds);
		this.psiTransportOrderDao.updateBySql("update lc_psi_transport_order_item set del_flag='1' where id in :p1", parameter);
	}
	
	/**
	 * 删除海运集装箱items
	 * 
	 */
	@Transactional(readOnly = false)
	public void deleteConertainItems(Set<Integer> itemIds) {
		Parameter parameter =new Parameter(itemIds);
		this.psiTransportOrderDao.updateBySql("update lc_psi_transport_order_container set del_flag='1' where id in :p1", parameter);
	}
	

	/**
	 *获取未发货的，已匹配的shippmentId 
	 */
	public List<String> getMutiShippmentIds(){
		String sql="SELECT a.`shipment_id` FROM lc_psi_transport_order AS a WHERE a.`transport_type`='1' AND a.`transport_sta`='0' AND a.`shipment_id` LIKE '%,%'";
		return  this.psiTransportOrderDao.findBySql(sql);
	}
	
	
	/**
	 *根据fba贴更新关联的运单的到货时间，运单状态，操作到达时间 
	 */
	
	@Transactional(readOnly = false)
	public void updateArrviedDateByFba(FbaInbound fbaInbound){
		String shipmentId =fbaInbound.getShipmentId();
		//查询该shipmentId是否存在   
		String sql ="SELECT a.`id` FROM lc_psi_transport_order AS a WHERE a.`shipment_id` like :p1 AND a.`transport_type`<>'0'";
		List<Integer> ids= this.psiTransportOrderDao.findBySql(sql,new Parameter("%"+shipmentId+"%"));
		if(ids.size()==1){
			Integer transportId=ids.get(0);
			//更新到达时间    运单状态  
			String updateSql="UPDATE lc_psi_transport_order AS a SET a.`arrival_date`=:p1,a.`transport_sta`=:p2,a.`oper_arrival_date`=:p3,a.`oper_arrival_fixed_date`=:p3 WHERE a.`id`=:p4 AND a.`transport_type`<>'0'";
			this.psiTransportOrderDao.updateBySql(updateSql, new Parameter(fbaInbound.getArrivalDate(),"5",new Date(),transportId));
		}
	}
	
	/**
	 * 根据fbaId，更新shipmentId
	 * 
	 */
	@Transactional(readOnly = false)
	public void updateShipmentIdByFbaId(String shipmentId,Integer fbaId) {
		String sql="SELECT a.`id`,a.`shipment_id`  FROM lc_psi_transport_order AS a WHERE FIND_IN_SET(:p1,a.`fba_inbound_id`) ";
		List<Object[]> list = this.psiTransportOrderDao.findBySql(sql,new Parameter(fbaId));
		if(list!=null&&list.size()>0){
			Object[] obj= list.get(0);
			String tempShipmentId = shipmentId;
			if(obj[1]!=null&&StringUtils.isNotEmpty(obj[1].toString())){
				if(!obj[1].toString().contains(shipmentId)){
					tempShipmentId=obj[1].toString()+","+tempShipmentId;
					this.psiTransportOrderDao.updateBySql("UPDATE lc_psi_transport_order AS a SET a.`shipment_id`=:p1 WHERE a.`id`=:p2", new Parameter(tempShipmentId,obj[0]));
				}
			}else{
				this.psiTransportOrderDao.updateBySql("UPDATE lc_psi_transport_order AS a SET a.`shipment_id`=:p1 WHERE a.`id`=:p2", new Parameter(tempShipmentId,obj[0]));
			}
			
		}
	}
	
	/**
	 *查找所有新建状态的 运单
	 */
	public List<LcPsiTransportOrder> findNewTranOrder() {
		DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("transportSta", "0"));
		dc.addOrder(Order.asc("id"));
		return this.psiTransportOrderDao.find(dc);
	}
	
	/**
	 * sku-类型-
	 * (0,在途；1,生产;2:CN仓；3：海外仓)
	 * @return
	 */
	public Map<String,Map<String,List<PsiTransportForecastDto>>> findTransportForecast(){
		Map<String,Map<String,List<PsiTransportForecastDto>>> map=Maps.newHashMap();
		//1.在途  本地运输0
		String sql1="SELECT i.`sku`,i.country_code,a.model,SUM(i.`quantity`) AS quantity,IFNULL(a.`pre_eta_date`,a.`pick_up_date`),(case when a.`pre_eta_date` is null then '0' else '1' end) "+
			" FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS i "+
			" WHERE a.`id`=i.`transport_order_id` and i.del_flag='0' AND a.transport_sta IN ('1','2','3') AND a.`transport_type`='0' AND i.sku IS NOT NULL AND i.sku !='' GROUP BY i.sku,i.country_code,a.model,IFNULL(a.`pre_eta_date`,a.`pick_up_date`),(case when a.`pre_eta_date` is null then '0' else '1' end) ";
		List<Object[]> list1=psiTransportOrderDao.findBySql(sql1);
		for (Object[] obj : list1) {
			String sku=obj[0].toString();
			String country=obj[1].toString();
			String model=obj[2].toString();
			Integer quantity=Integer.parseInt(obj[3].toString());
			String type=obj[5].toString();
			Date forecast=null;
			if("0".equals(type)){//预计到达时间为空
				if("0".equals(model)){//Air
					forecast=DateUtils.addDays((Date)obj[4],PsiConfig.get(country).getTransportBySky());
				}else if("1".equals(model)){//Sea
					forecast=DateUtils.addDays((Date)obj[4],PsiConfig.get(country).getTransportBySea());
				}else{
					forecast=DateUtils.addDays((Date)obj[4],7);
				}
			}else{
				if("0".equals(model)){//Air
					forecast=DateUtils.addDays((Timestamp)obj[4],PsiConfig.get(country).getWareHouseBySky());
				}else if("1".equals(model)){//Sea
					forecast=DateUtils.addDays((Timestamp)obj[4],PsiConfig.get(country).getWareHouseBySea());
				}else{
					forecast=DateUtils.addDays((Timestamp)obj[4],3);
				}
			}
			Map<String,List<PsiTransportForecastDto>> temp=map.get(sku);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(sku,temp);
			}
			List<PsiTransportForecastDto> list=temp.get("0");
			if(list==null){
				list=Lists.newArrayList();
				temp.put("0",list);
			}
			Long day=DateUtils.spaceDays(new Date(),forecast)+1;
			list.add(new PsiTransportForecastDto(sku,forecast,quantity,day));
		}
		//1.在途  FBA运输1	
		String sql2="SELECT i.`sku`,i.country_code,a.model,SUM(i.`quantity`) AS quantity,IFNULL(a.`pre_eta_date`,a.`pick_up_date`),(case when a.`pre_eta_date` is null then '0' else '1' end) "+
				" FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS i "+
				" WHERE a.`id`=i.`transport_order_id` and i.del_flag='0' AND a.transport_sta IN ('1','2','3') AND a.`transport_type`='1' AND i.sku IS NOT NULL AND i.sku !='' GROUP BY i.sku,i.country_code,a.model,IFNULL(a.`pre_eta_date`,a.`pick_up_date`),(case when a.`pre_eta_date` is null then '0' else '1' end) ";
			List<Object[]> list2=psiTransportOrderDao.findBySql(sql2);
			for (Object[] obj : list2) {
				String sku=obj[0].toString();
				String country=obj[1].toString();
				String model=obj[2].toString();
				Integer quantity=Integer.parseInt(obj[3].toString());
				String type=obj[5].toString();
				Date forecast=null;
				if("0".equals(type)){//预计到达时间为空
					if("0".equals(model)){//Air
						forecast=DateUtils.addDays((Date)obj[4],PsiConfig.get(country).getTransportBySky());
					}else if("1".equals(model)){//Sea
						forecast=DateUtils.addDays((Date)obj[4],PsiConfig.get(country).getTransportBySea());
					}else{
						forecast=DateUtils.addDays((Date)obj[4],7);
					}
				}else{
					forecast=(Timestamp)obj[4];
				}
				Map<String,List<PsiTransportForecastDto>> temp=map.get(sku);
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(sku,temp);
				}
				List<PsiTransportForecastDto> list=temp.get("0");
				if(list==null){
					list=Lists.newArrayList();
					temp.put("0",list);
				}
				Long day=DateUtils.spaceDays(new Date(),forecast)+1;
				list.add(new PsiTransportForecastDto(sku,forecast,quantity,day));
			}
		//2.生产
	/*	String sql3="SELECT s.sku,t.`country_code`,SUM(t.quantity_ordered-t.quantity_received),t.`delivery_date`,TO_DAYS(NOW())-TO_DAYS(t.`delivery_date`),p.`transport_type` "+
           " FROM psi_purchase_order r JOIN psi_purchase_order_item t  ON r.id=t.`purchase_order_id` and t.del_flag='0' "+
           " JOIN psi_sku s ON s.`country`=t.`country_code` AND s.`del_flag`='0' AND s.`use_barcode`='1' "+
           " join psi_product p on concat(p.brand,' ',p.model)=s.product_name and p.del_flag='0' "+
           " AND  CONCAT(s.product_name,CASE WHEN s.color!='' THEN CONCAT ('_',s.color) ELSE '' END)=CONCAT(t.product_name,CASE WHEN t.`color_code`!='' THEN CONCAT ('_',t.color_code) ELSE '' END)  "+    
           " WHERE r.order_sta='2' OR (r.order_sta='3' AND t.quantity_ordered!=t.quantity_received) GROUP BY s.sku,t.`country_code`,t.`delivery_date` ";
		*/
		 String sql3 ="select sku,country_code,sum(orderNum),spareday,transport_type FROM (SELECT s.sku,t.`country_code`,SUM(t.`quantity_ordered`-t.`quantity_off_ordered`-(t.`quantity_received`-t.`quantity_off_received`)) AS orderNum,t.`delivery_date`,TO_DAYS(NOW())-TO_DAYS(t.`delivery_date`) spareday,p.`transport_type` "+
           " FROM psi_purchase_order r JOIN psi_purchase_order_item t  ON r.id=t.`purchase_order_id` and t.del_flag='0' "+
           " JOIN psi_sku s ON s.`country`=t.`country_code` AND s.`del_flag`='0' AND s.`use_barcode`='1' "+
           " join psi_product p on concat(p.brand,' ',p.model)=s.product_name and p.del_flag='0' "+
           " AND  CONCAT(s.product_name,CASE WHEN s.color!='' THEN CONCAT ('_',s.color) ELSE '' END)=CONCAT(t.product_name,CASE WHEN t.`color_code`!='' THEN CONCAT ('_',t.color_code) ELSE '' END)  "+    
           " WHERE r.`order_sta` IN ('2','3')  GROUP BY s.sku,t.`country_code`,t.`delivery_date` "+
           " UNION ALL "+
           " SELECT s.sku,t.`country_code`,SUM(t.`quantity_ordered`-t.`quantity_off_ordered`-(t.`quantity_received`-t.`quantity_off_received`)) AS orderNum,t.`delivery_date`,TO_DAYS(NOW())-TO_DAYS(t.`delivery_date`) spareday,p.`transport_type` "+
           " FROM psi_purchase_order r JOIN psi_purchase_order_item t  ON r.id=t.`purchase_order_id` and t.del_flag='0' "+
           " JOIN psi_sku s ON s.`country`=t.`country_code` AND s.`del_flag`='0' AND s.`use_barcode`='1' "+
           " join psi_product p on concat(p.brand,' ',p.model)=s.product_name and p.del_flag='0' "+
           " AND  CONCAT(s.product_name,CASE WHEN s.color!='' THEN CONCAT ('_',s.color) ELSE '' END)=CONCAT(t.product_name,CASE WHEN t.`color_code`!='' THEN CONCAT ('_',t.color_code) ELSE '' END)  "+    
           " WHERE r.`order_sta` IN ('2','3')  GROUP BY s.sku,t.`country_code`,t.`delivery_date`) t group by sku,country_code,spareday,transport_type ";	

		 
		List<Object[]> list3=psiTransportOrderDao.findBySql(sql3);   
		for (Object[] obj : list3) {
			String sku=obj[0].toString();
			String country=obj[1].toString();
			Integer quantity=Integer.parseInt(obj[2].toString());
		    //Integer day=Integer.parseInt(obj[4].toString());
		    String type=obj[5].toString();
		    Date forecast=null;
		    //1海运  其他空运
		    if("1".equals(type)){
		    	forecast=DateUtils.addDays((Date)obj[3],PsiConfig.get(country).getTransportBySea());
		    }else{
		    	forecast=DateUtils.addDays((Date)obj[3],PsiConfig.get(country).getTransportBySky());
		    }
		    Map<String,List<PsiTransportForecastDto>> temp=map.get(sku);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(sku,temp);
			}
			List<PsiTransportForecastDto> list=temp.get("1");
			if(list==null){
				list=Lists.newArrayList();
				temp.put("1",list);
			}
			Long day=DateUtils.spaceDays(new Date(),forecast)+1;
			list.add(new PsiTransportForecastDto(sku,forecast,quantity,day)); 
		}   
		
		//3.在仓 CN
		String sql4="SELECT y.sku,y.country_code,y.new_quantity,y.warehouse_id,p.`transport_type` FROM psi_inventory y "+
				" join psi_product p on concat(p.brand,' ',p.model)=y.product_name and p.del_flag='0' "+
				" WHERE new_quantity>0 and y.warehouse_id=21 ";
		List<Object[]> list4=psiTransportOrderDao.findBySql(sql4);   
		for (Object[] obj : list4) {
			String sku=obj[0].toString();
			String country=obj[1].toString();
			Integer quantity=Integer.parseInt(obj[2].toString());
		    String type=obj[4].toString();
		    Date forecast=new Date();
		    if("1".equals(type)){
		    	forecast=DateUtils.addDays((Date)forecast,PsiConfig.get(country).getTransportBySea());
		    }else{
		    	forecast=DateUtils.addDays((Date)forecast,PsiConfig.get(country).getTransportBySky());
		    }
		    Map<String,List<PsiTransportForecastDto>> temp=map.get(sku);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(sku,temp);
			}
			List<PsiTransportForecastDto> list=temp.get("2");
			if(list==null){
				list=Lists.newArrayList();
				temp.put("2",list);
			}
			Long day=DateUtils.spaceDays(new Date(),forecast)+1;
			list.add(new PsiTransportForecastDto(sku,forecast,quantity,day));
		}
		
		
		//海外仓
		String sql5="SELECT y.sku,y.country_code,y.new_quantity,y.warehouse_id,p.`transport_type` FROM psi_inventory y "+
				" join psi_product p on concat(p.brand,' ',p.model)=y.product_name and p.del_flag='0' "+
				" WHERE new_quantity>0 and y.warehouse_id in (19,120) ";
		List<Object[]> list5=psiTransportOrderDao.findBySql(sql5);   
		for (Object[] obj : list5) {
			String sku=obj[0].toString();
			String country=obj[1].toString();
			Integer quantity=Integer.parseInt(obj[2].toString());
		    Integer warehouse=Integer.parseInt(obj[3].toString());
		    String type=obj[4].toString();
		    Date forecast=new Date();
		    if("1".equals(type)){
		    	if(warehouse==19){//DE
		    		forecast=DateUtils.addDays((Date)forecast,8);
		    	}else if(warehouse==120){//US
		    		forecast=DateUtils.addDays((Date)forecast,8);
		    	}
		    }else{
		    	if(warehouse==19){//DE
		    		forecast=DateUtils.addDays((Date)forecast,8);
		    	}else if(warehouse==120){//US
		    		forecast=DateUtils.addDays((Date)forecast,8);
		    	}
		    }
		    Map<String,List<PsiTransportForecastDto>> temp=map.get(sku);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(sku,temp);
			}
			List<PsiTransportForecastDto> list=temp.get("3");
			if(list==null){
				list=Lists.newArrayList();
				temp.put("3",list);
			}
			Long day=DateUtils.spaceDays(new Date(),forecast)+1;
			list.add(new PsiTransportForecastDto(sku,forecast,quantity,day));
		}
		return map;
	}
	
	
	//获取运单的模式：海、空、快
	public Map<Integer,String> getTranModel(Set<Integer> tranIds){
		Map<Integer,String> resMap = Maps.newHashMap();
		String sql="SELECT a.id,a.`model` FROM lc_psi_transport_order AS a WHERE a.`id` IN :p1"   ;   
		List<Object[]> objs = this.psiTransportOrderDao.findBySql(sql, new Parameter(tranIds));
		for(Object[] obj:objs){
			String model ="";
			if("0".equals(obj[1])){
				model="Air";
			}else if("1".equals(obj[1])){
				model="Ocean";
			}else if("2".equals(obj[1])){
				model="Express";
			}
			resMap.put(Integer.parseInt(obj[0].toString()), model);
		}
		return resMap;
	}
	
	
	public List<Object[]> getSingleTran(String productNameColor,String model,String toCountry,Integer fromStoreId,String tranType,Date startDate,Date endDate){
		String sql="SELECT a.`transport_no`,a.`from_store`,a.`to_country`,a.`model`,a.`transport_type`, b.`sku`,b.`country_code`,b.`quantity`,b.`remark`,b.`pack_quantity`,a.declare_no,a.export_date,(CASE WHEN b.`color_code`='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END) as proNameColor FROM lc_psi_transport_order AS a ,lc_psi_transport_order_item AS b WHERE a.id=b.`transport_order_id` " +
				" AND a.`transport_sta`!='8' AND b.`del_flag`='0' AND  (CASE WHEN b.`color_code`='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END) " +
				"like '%"+productNameColor+"%' " ;
		int i=0;
		int j =0;
		if(StringUtils.isNotEmpty(model)){
			j++;
		}
		if(StringUtils.isNotEmpty(toCountry)){
			j++;
		}
		if(fromStoreId!=null){
			j++;
		}
		if(StringUtils.isNotEmpty(tranType)){
			j++;
		}
		if(startDate!=null){
			j++;
		}
		if(endDate!=null){
			j++;
		}
		Object[]  objs = new Object[j];
		if(StringUtils.isNotEmpty(model)){
			objs[i++]=model;
			sql+=" AND a.`model`=:p"+i;
		}
		
		if(StringUtils.isNotEmpty(toCountry)){
			String []  countryStr=null;
			if(toCountry.equals("com")||toCountry.equals("US")||toCountry.equals("mx")){
				countryStr=new String[]{"com","US","mx"};
			}else if(toCountry.equals("DE")||toCountry.equals("de")||toCountry.equals("uk")||toCountry.equals("fr")||toCountry.equals("it")||toCountry.equals("es")){
				countryStr=new String[]{"DE","de","uk","fr","it","es"};
			}else if(toCountry.equals("jp")){
				countryStr=new String[]{"jp"};
			}else if(toCountry.equals("ca")){
				countryStr=new String[]{"ca"};
			}else{
				countryStr=new String[]{"DE","de","uk","fr","it","es"};
			}
			objs[i++]=countryStr;
			sql+=" AND a.`to_country` in :p"+i;
		}
		
		if(fromStoreId!=null){
			objs[i++]=fromStoreId;
			sql+=" AND a.`from_store` = :p"+i;
		}
		
		if(StringUtils.isNotEmpty(tranType)){
			objs[i++]=tranType;
			sql+=" AND a.`transport_type`=:p"+i;
		}
		if(startDate!=null){
			objs[i++]=startDate;
			sql+=" AND a.`create_date`>=:p"+i;
		}
		if(endDate!=null){
			objs[i++]=endDate;
			sql+=" AND a.`create_date`<=:p"+i;
		}
		sql+=" ORDER BY a.`create_date` DESC ";
		
		return this.psiTransportOrderDao.findBySql(sql, new Parameter(objs));
	}
	

	public Map<String,Map<String,Integer>> findTotalTranQuantity(LcPsiTransportOrder psiTransportOrder){
		Map<String,Map<String,Integer>>  map=Maps.newHashMap();
		String sql="SELECT CONCAT(t.`product_name`,CASE WHEN t.`color_code`!='' THEN CONCAT ('_',t.`color_code`) ELSE '' END) NAME,LOWER(o.to_country),SUM(t.`quantity`) FROM lc_psi_transport_order o "+
        " JOIN lc_psi_transport_order_item t ON o.id=t.`transport_order_id` AND t.`del_flag`='0' "+
        " WHERE o.`transport_sta`!='0' AND o.`transport_sta`!='8' AND o.`create_date`>=:p1 AND o.`create_date`<=:p2 "+
        " GROUP BY  NAME,LOWER(o.to_country) ";
		List<Object[]> list=psiTransportOrderDao.findBySql(sql,new Parameter(psiTransportOrder.getCreateDate(),DateUtils.addDays(psiTransportOrder.getEtdDate(),1)));
		for (Object[] obj: list) {
			String name=obj[0].toString();
			String country=obj[1].toString();
			Integer quantity=Integer.parseInt(obj[2].toString());
			
			Map<String,Integer> temp=map.get(country);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(country,temp);
			}
			temp.put(name,quantity);
			
			if("de,fr,it,es,uk".contains(country)){
				Map<String,Integer> euTemp=map.get("EU");
				if(euTemp==null){
					euTemp=Maps.newHashMap();
					map.put("EU", euTemp);
				}
				Integer euQuantity=euTemp.get(name);
				euTemp.put(name, quantity+(euQuantity==null?0:euQuantity));
			}
			
			if("com,us,ca".contains(country)){
				Map<String,Integer> euTemp=map.get("US");
				if(euTemp==null){
					euTemp=Maps.newHashMap();
					map.put("US", euTemp);
				}
				Integer euQuantity=euTemp.get(name);
				euTemp.put(name, quantity+(euQuantity==null?0:euQuantity));
			}
			
			if("jp".contains(country)){
				Map<String,Integer> euTemp=map.get("JP");
				if(euTemp==null){
					euTemp=Maps.newHashMap();
					map.put("JP", euTemp);
				}
				Integer euQuantity=euTemp.get(name);
				euTemp.put(name, quantity+(euQuantity==null?0:euQuantity));
			}
			
		}
		return map;
	}

	public List<LcPsiTransportOrder> findInStockChanges() {
		DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
		dc.add(Restrictions.isNotNull("changeRecord"));
		List<LcPsiTransportOrder> list= psiTransportOrderDao.find(dc);
		for (LcPsiTransportOrder psiTransportOrder : list) {
			Hibernate.initialize(psiTransportOrder.getItems());
		}
		return list;
	}
	
	@Transactional(readOnly = false)
	public void clearChangeRecord() {
		String sql = "UPDATE `lc_psi_transport_order` t SET t.`change_record`=NULL WHERE t.`change_record` IS NOT NULL";
		psiTransportOrderDao.updateBySql(sql, null);
	}
	
	
	/**
	 * 更新confirmPay
	 */
	@Transactional(readOnly = false)
	public void updateConfirmPay(String confirmPay,Integer id) {
		Parameter parameter =new Parameter(id,confirmPay);
		this.psiTransportOrderDao.updateBySql(" UPDATE lc_psi_transport_order AS a SET a.`confirm_pay`=:p2 WHERE a.`id`=:p1 ", parameter);
	}
	
	//拆分运单
	@Transactional(readOnly = false)
	public String splitSaveData(LcPsiTransportOrder psiTransportOrder) throws IOException{
		this.clearSupplierData(psiTransportOrder);
		List<LcPsiTransportOrderItem> itemList = psiTransportOrder.getItems();
		List<LcPsiTransportOrderItem> newItems = Lists.newArrayList();
		LcPsiTransportOrder order = new LcPsiTransportOrder();	//拆分出的新运单
		int totalBoxNum = 0;	//总箱数
		for (LcPsiTransportOrderItem item : itemList) {
			if (item.getChdQuantity() > 0) {	//拆单数大于0表示需要拆单
				LcPsiTransportOrderItem tranOrderItem = new LcPsiTransportOrderItem();
				tranOrderItem.setProductName(item.getProductName());
				tranOrderItem.setColorCode(item.getColorCode());
				tranOrderItem.setCountryCode(item.getCountryCode());
				tranOrderItem.setSku(item.getSku());
				tranOrderItem.setQuantity(item.getChdQuantity());	//新item数量即页面填写的拆单数
				tranOrderItem.setOfflineSta(item.getOfflineSta());
				tranOrderItem.setPackQuantity(item.getPackQuantity());	//装箱数
				//PsiProduct product=psiProductService.findProductByProductName(item.getProductName());
				tranOrderItem.setProduct(item.getProduct());
				tranOrderItem.setProductPrice(item.getProductPrice());
				tranOrderItem.setItemPrice(item.getItemPrice());
				tranOrderItem.setLowerPrice(item.getLowerPrice());
				tranOrderItem.setImportPrice(item.getImportPrice());
				tranOrderItem.setCurrency(item.getCurrency());
				tranOrderItem.setTransportOrder(order);
				tranOrderItem.setRemark(item.getRemark());
				tranOrderItem.setCnPrice(item.getCnPrice());
				tranOrderItem.setDelFlag("0");
				newItems.add(tranOrderItem);
				if(!"1".equals(item.getProduct().getComponents())){
					totalBoxNum += tranOrderItem.getQuantity()/tranOrderItem.getPackQuantity();
				}
				tranOrderItem.setTransportOrder(order);	//关联新的运单
				//设置新的数量
				item.setQuantity(item.getQuantity() - item.getChdQuantity());
				if (item.getQuantity() == 0) {
					item.setDelFlag("1");	//拆完了直接标记删除
				}
			}
		}
		if (newItems.size() == 0) {
			return "0";	//没有拆分
		}
		//生成新的运单号
		String transportNo = this.createFlowNo();
		order.setTransportNo(transportNo);
		order.setItems(newItems);
		order.setBoxNumber(totalBoxNum);
		psiTransportOrder.setBoxNumber(psiTransportOrder.getBoxNumber()-totalBoxNum);	//拆分后的箱数
		//复制运单信息
		copyOrderInfo(order, psiTransportOrder);
		
		Float volume=0f;
		Float weight=0f;
		Set<Integer> productIdSet =Sets.newHashSet();
		//保存货物清单信息
		for(LcPsiTransportOrderItem item:psiTransportOrder.getItems()){
			productIdSet.add(item.getProduct().getId());
		}
		Map<Integer,String> volumeWeightMap = this.psiProductService.getVomueAndWeight(productIdSet);
		
		//修改主运单数量和重量信息
		for(LcPsiTransportOrderItem item:psiTransportOrder.getItems()){
			if(!"1".equals(item.getProduct().getComponents())){
				String volumeWeight=volumeWeightMap.get(item.getProduct().getId());
				Float rate= item.getQuantity()/Float.parseFloat(volumeWeight.split(",")[2]);
				Float itemVolue= Float.parseFloat(volumeWeight.split(",")[0])*rate;
				Float itemWeight= Float.parseFloat(volumeWeight.split(",")[1])*rate;
				volume+=itemVolue;
				weight+=itemWeight;
			}
			
			item.setTransportOrder(psiTransportOrder);
		}
		psiTransportOrder.setWeight(weight);
		psiTransportOrder.setVolume(volume);
		
		//计算拆分出的运单数量和重量信息
		volume=0f;	//数据清0重算
		weight=0f;
		for(LcPsiTransportOrderItem item : newItems){
			if(!"1".equals(item.getProduct().getComponents())){
				String volumeWeight=volumeWeightMap.get(item.getProduct().getId());
				Float rate= item.getQuantity()/Float.parseFloat(volumeWeight.split(",")[2]);
				Float itemVolue= Float.parseFloat(volumeWeight.split(",")[0])*rate;
				Float itemWeight= Float.parseFloat(volumeWeight.split(",")[1])*rate;
				volume+=itemVolue;
				weight+=itemWeight;
			}	
		}
		order.setWeight(weight);
		order.setVolume(volume);
		psiTransportOrderDao.save(order);
		this.psiTransportOrderDao.getSession().merge(psiTransportOrder);
		return order.getTransportNo();
	}
	
	//合并运单
	@Transactional(readOnly = false)
	public void merge(String ids) throws IOException {
		LcPsiTransportOrder order =null;
		String[] idArr=ids.split(",");
		String shipmentIds ="";
		String fbaIds ="";
		Map<String,Integer> skuAndOffMap=Maps.newHashMap(); 
		for(int i=0;i<idArr.length;i++){
			if(i==0){
				order = this.psiTransportOrderDao.get(Integer.parseInt(idArr[i]));
				if(StringUtils.isNotEmpty(order.getFbaInboundId())){
					fbaIds=order.getFbaInboundId();
				}
				if(StringUtils.isNotEmpty(order.getShipmentId())){
					shipmentIds=order.getShipmentId();
				}
				for(LcPsiTransportOrderItem item:order.getItems()){
					skuAndOffMap.put(item.getSku()+","+item.getOfflineSta(),item.getQuantity());
				}
			}else{
				Set<String> skuAndOff = skuAndOffMap.keySet();
				LcPsiTransportOrder tempOrder = this.psiTransportOrderDao.get(Integer.parseInt(idArr[i]));
				for(LcPsiTransportOrderItem item:tempOrder.getItems()){
					String key=item.getSku()+","+item.getOfflineSta();
					if(skuAndOff.contains(key)){
						skuAndOffMap.put(key, skuAndOffMap.get(key)+item.getQuantity());
					}else{
						LcPsiTransportOrderItem tempItem = new LcPsiTransportOrderItem(order, item.getProduct(), item.getProductName(), item.getColorCode(),
								item.getCountryCode(),item.getQuantity(),item.getShippedQuantity(),item.getReceiveQuantity(),item.getItemPrice(),
								item.getCurrency(),item.getDelFlag(),item.getRemark(),item.getPackQuantity(),item.getSku(),item.getOfflineSta(),
								item.getProductPrice(),item.getCnPrice(),item.getFbaFlag(),item.getFbaInboundId());
						tempItem.setLowerPrice(item.getLowerPrice());
						tempItem.setImportPrice(item.getImportPrice());
						order.getItems().add(tempItem);
					}
					
				}
				
				if(StringUtils.isNotEmpty(tempOrder.getFbaInboundId())){
					if(StringUtils.isNotEmpty(fbaIds)){
						fbaIds=fbaIds+","+tempOrder.getFbaInboundId();
					}else{
						fbaIds=tempOrder.getFbaInboundId();
					}
				}
				
				if(StringUtils.isNotEmpty(tempOrder.getShipmentId())){
					if(StringUtils.isNotEmpty(shipmentIds)){
						shipmentIds=shipmentIds+","+tempOrder.getShipmentId();
					}else{
						shipmentIds=tempOrder.getShipmentId();
					}
				}
				
				tempOrder.setTransportSta("8");
				tempOrder.setCancelDate(new Date());
				tempOrder.setCancelUser(UserUtils.getUser());
				this.psiTransportOrderDao.save(tempOrder);//其余的都变为取消状态
			}
		}
		
		//算出产品重量体积
		Map<Integer,String>	volumeWeightMap=this.psiProductService.getVomueAndWeight(null);
		//重新计算重量体积
		Float volume =0f;
		Float weight=0f;
		Integer boxNum = 0;
		for(LcPsiTransportOrderItem item:order.getItems()){
			if("1".equals(item.getDelFlag())){
				continue;
			}   
			String key=item.getSku()+","+item.getOfflineSta();
			if(skuAndOffMap.containsKey(key)){
				item.setQuantity(skuAndOffMap.get(key));
			}
			
			Integer productId = item.getProduct().getId();
			if(!"1".equals(item.getProduct().getComponents())){
				volume+=item.getQuantity()*1.0f/item.getPackQuantity()*(Float.parseFloat(volumeWeightMap.get(productId).split(",")[0]));
				weight+=item.getQuantity()*1.0f/item.getPackQuantity()*(Float.parseFloat(volumeWeightMap.get(productId).split(",")[1]));
				boxNum+=item.getQuantity()%item.getPackQuantity()==0?(item.getQuantity()/item.getPackQuantity()):(item.getQuantity()/item.getPackQuantity()+1);
			}
			
		}
		order.setVolume(volume);
		order.setWeight(weight);
		order.setBoxNumber(boxNum);
		this.psiTransportOrderDao.save(order);
	}
	/**
	 * 
	 * @param order	目标运单
	 * @param psiTransportOrder	复制源
	 */
	private void copyOrderInfo(LcPsiTransportOrder order, LcPsiTransportOrder psiTransportOrder){
		order.setFromStore(psiTransportOrder.getFromStore());
		order.setToStore(psiTransportOrder.getToStore());
		order.setOrgin(psiTransportOrder.getOrgin());
		order.setDestination(psiTransportOrder.getDestination());
		order.setModel(psiTransportOrder.getModel());
		order.setTransportType(psiTransportOrder.getTransportType());
		order.setTransportSta(psiTransportOrder.getTransportSta());
		order.setPaymentSta(psiTransportOrder.getPaymentSta());
		order.setCreateDate(new Date());
		order.setCreateUser(UserUtils.getUser());
		order.setToCountry(psiTransportOrder.getToCountry());
		order.setDestinationDetail(psiTransportOrder.getDestinationDetail());
		order.setPickUpDate(psiTransportOrder.getPickUpDate());
		order.setArrivalDate(psiTransportOrder.getArrivalDate());
		order.setEtaDate(psiTransportOrder.getEtaDate());
		order.setEtdDate(psiTransportOrder.getEtdDate());
		order.setOperArrivalDate(psiTransportOrder.getOperArrivalDate());
		order.setFirstEtaDate(psiTransportOrder.getFirstEtaDate());
		order.setPreEtaDate(psiTransportOrder.getPreEtaDate());
		order.setDeliveryDate(psiTransportOrder.getDeliveryDate());
	}
	
	
	public List<LcPsiTransportOrder> findOrderFileByMonth(String month,Set<String> countrySet,String model){
		List<LcPsiTransportOrder> orderList=Lists.newArrayList();
		String sql="SELECT r.`transport_no`,r.`dap_path`,r.`tran_path`,r.`other_path`,r.`suffix_name`,r.`export_invoice_path`,r.`tax_path`,r.mix_file FROM lc_psi_transport_order r WHERE r.`transport_sta`!='8' "+
				" AND DATE_FORMAT(r.`create_date`,'%Y-%m')=:p1 ";

		int i=2;
		List<Object> paramList=Lists.newArrayList();
		paramList.add(month);
		if(countrySet!=null&&countrySet.size()>0){
			sql+=" and r.to_country in :p"+(i++)+" ";
			paramList.add(countrySet);
		}
		if(StringUtils.isNotBlank(model)){
			sql+=" and r.model=:p"+(i++)+" ";
			paramList.add(model);
		}
		List<Object[]> list=psiTransportOrderDao.findBySql(sql, new Parameter(paramList.toArray(new Object[paramList.size()])));
		
		
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				LcPsiTransportOrder order=new LcPsiTransportOrder();
				String tranNo=obj[0].toString();
				String dapPath=(obj[1]==null?"":obj[1].toString());
				String tranPath=(obj[2]==null?"":obj[2].toString());
				String otherPath=(obj[3]==null?"":obj[3].toString());
				String suffixName=(obj[4]==null?"":obj[4].toString());
				String exportInvoicePath=(obj[5]==null?"":obj[5].toString());
				String taxPath=(obj[6]==null?"":obj[6].toString());
				String mixFile=(obj[7]==null?"":obj[7].toString());
				order.setTransportNo(tranNo);
				order.setDapPath(dapPath);
				order.setTranPath(tranPath);
				order.setOtherPath(otherPath);
				order.setSuffixName(suffixName);
				order.setExportInvoicePath(exportInvoicePath);
				order.setTaxPath(taxPath);
				order.setMixFile(mixFile);
				orderList.add(order);
			}
		}
		return orderList;
	}
	
	//country/_product/month
	public Map<String,Map<String,Map<String,Integer>>> findAllTransportOrder(String country,Date start,Date end){
		start = DateUtils.getMonday(start);
		end = DateUtils.getSunday(end);
		String temp="";
		if(StringUtils.isNotBlank(country)){
			if("eu".equals(country)){
				temp=" and t.`country_code` in ('de','fr','it','es','uk') ";
			}else{
				temp=" and t.`country_code`="+country ;
			}
		}
		Map<String,Map<String,Map<String,Integer>>> map=Maps.newHashMap();
		String sql="SELECT t.name,t.`country_code`,DATE_FORMAT(t.`oper_arrival_date`,'%x%v') dates,SUM(t.quantity),t.model FROM "+
				" (SELECT CONCAT(t.`product_name`,CASE WHEN t.`color_code`!='' THEN CONCAT ('_',t.`color_code`) ELSE '' END) NAME,t.`country_code`,t.`quantity`,r.`oper_arrival_date`,r.model FROM lc_psi_transport_order r "+ 
				" JOIN lc_psi_transport_order_item t ON r.id=t.`transport_order_id` and t.del_flag='0' "+
				" WHERE r.`oper_arrival_date`>=:p1 and r.`oper_arrival_date`<=:p2 AND r.`transport_sta`!='8' AND r.`transport_sta`!='0' "+temp+" "+
				" UNION "+
				" SELECT CONCAT(t.`product_name`,CASE WHEN t.`color_code`!='' THEN CONCAT ('_',t.`color_code`) ELSE '' END) NAME,t.`country_code`,t.`quantity`,r.`oper_arrival_date`,r.model FROM  psi_transport_order r  "+
				" JOIN psi_transport_order_item t ON r.id=t.`transport_order_id` and t.del_flag='0'  "+
				" WHERE r.`oper_arrival_date`>=:p1 and  r.`oper_arrival_date`<=:p2 AND r.`transport_sta`!='8' AND r.`transport_sta`!='0' "+temp+" ) t "+
				" GROUP BY t.name,t.`country_code`,dates,t.model ";
		List<Object[]> list=psiTransportOrderDao.findBySql(sql, new Parameter(start,end));
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				String name=obj[0].toString();
				String tranCountry=obj[1].toString();
				String date=obj[2].toString();
				
				Integer i = Integer.parseInt(date.substring(4));
				if(i==53){
					Integer year = Integer.parseInt(date.substring(0,4));
					date =  (year+1)+"01";
				}else if (date.contains("2016")){
					i = i+1;
					date = "2016"+(i<10?("0"+i):i);
				}
				
				Integer quantity=Integer.parseInt(obj[3].toString());
				String model=obj[4].toString();
				Map<String,Map<String,Integer>> countryTemp=map.get(tranCountry+"_"+name);
				if(countryTemp==null){
					countryTemp=Maps.newHashMap();
					map.put(tranCountry+"_"+name, countryTemp);
				}
				Map<String,Integer> dateTemp=countryTemp.get(model);
				if(dateTemp==null){
					dateTemp=Maps.newHashMap();
					countryTemp.put(model, dateTemp);
				}
				dateTemp.put(date, quantity);
			}
		}	
		
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyww");
		//生产 部门收货
		String temp2="";
		if(StringUtils.isNotBlank(country)){
			if("eu".equals(country)){
				temp2=" and b.`country_code` in ('de','fr','it','es','uk') ";
			}else{
				temp2=" and b.`country_code`="+country ;
			}
		}
		String sql2 ="SELECT name,country_code,SUM(orderNum),`delivery_date`,`transport_type` FROM (" +
				"SELECT  CONCAT(b.`product_name`,CASE  WHEN b.`color_code`='' THEN '' ELSE CONCAT('_',b.`color_code`) END) name,b.`country_code`," +
				" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum,b.`delivery_date`,p.`transport_type` " +
				" FROM psi_purchase_order AS a ,psi_purchase_order_item AS b,psi_product_eliminate p " +
				" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('2','3')  and b.`del_flag`='0' and b.`country_code`=p.`country` " +
				" and CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END)=CONCAT(b.product_name,CASE WHEN b.`color_code`!='' THEN CONCAT ('_',b.color_code) ELSE '' END)  AND p.del_flag='0' "+
				"  "+temp2+" GROUP BY name,b.country_code,b.`delivery_date`,p.`transport_type`  "+
				" UNION ALL SELECT CONCAT(b.`product_name`,CASE  WHEN b.`color_code`='' THEN '' ELSE CONCAT('_',b.`color_code`) END) name,b.`country_code`," +
				" SUM(b.`quantity_ordered`-b.`quantity_off_ordered`-(b.`quantity_received`-b.`quantity_off_received`)) AS orderNum,b.`delivery_date`,p.`transport_type` " +
				" FROM lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b,psi_product_eliminate p " +
				" WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` IN ('2','3')  and b.`del_flag`='0' and b.`country_code`=p.`country` " +
				" and CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END)=CONCAT(b.product_name,CASE WHEN b.`color_code`!='' THEN CONCAT ('_',b.color_code) ELSE '' END)  AND p.del_flag='0' "+
				"  "+temp2+" GROUP BY name,b.country_code,b.`delivery_date`,p.`transport_type`) AS t GROUP BY name,country_code,`delivery_date`,`transport_type` ";	
				List<Object[]> list2=psiTransportOrderDao.findBySql(sql2);   
				for (Object[] obj : list2) {
				    String name=obj[0].toString();
					String tranCountry=obj[1].toString();
					Integer quantity=Integer.parseInt(obj[2].toString());
				    String type=(obj[4]==null?"1":obj[4].toString());
				    Date forecast=null;
				    //1海运  其他空运
				    String model="1";
				    if("1".equals(type)){
				    	forecast=DateUtils.addDays((Date)obj[3],PsiConfig.get(tranCountry).getTransportBySea());
				    }else{
				    	model="0";
				    	forecast=DateUtils.addDays((Date)obj[3],PsiConfig.get(tranCountry).getTransportBySky());
				    }	
				    String date=dateFormat.format(forecast);
					Map<String,Map<String,Integer>> countryTemp=map.get(tranCountry+"_"+name);
					if(countryTemp==null){
						countryTemp=Maps.newHashMap();
						map.put(tranCountry+"_"+name, countryTemp);
					}
					Map<String,Integer> dateTemp=countryTemp.get(model);
					if(dateTemp==null){
						dateTemp=Maps.newHashMap();
						countryTemp.put(model, dateTemp);
					}
					Integer beforeQuantity=dateTemp.get(date);
					dateTemp.put(date, quantity+(beforeQuantity==null?0:beforeQuantity));
		       }
		//仓库
				String temp3="";
				if(StringUtils.isNotBlank(country)){
					if("eu".equals(country)){
						temp3=" and p.`country_code` in ('de','fr','it','es','uk') ";
					}else{
						temp3=" and p.`country_code`="+country ;
					}
				}
			String sql3="SELECT CONCAT(p.`product_name`,CASE WHEN p.`color_code`!='' THEN CONCAT ('_',p.`color_code`) ELSE '' END) NAME,p.`country_code`,SUM(p.`new_quantity`+p.`offline_quantity`) quantity,now(),a.`transport_type` "+
				" FROM psi_inventory p JOIN psi_product_eliminate a ON p.`product_name`=a.`product_name` AND a.`color`=p.`color_code` AND a.`del_flag`='0' AND a.`country`=p.`country_code` "+
				" WHERE p.`warehouse_id` IN (21,130)  "+temp3+" AND (p.`new_quantity`>0 OR p.`offline_quantity`>0)  GROUP BY NAME,country_code ";
			List<Object[]> list3=psiTransportOrderDao.findBySql(sql3);   
			for (Object[] obj : list3) {
			    String name=obj[0].toString();
				String tranCountry=obj[1].toString();
				Integer quantity=Integer.parseInt(obj[2].toString());
			    String type=(obj[4]==null?"1":obj[4].toString());
			    Date forecast=null;
			    //1海运  其他空运
			    String model="1";
			    if("1".equals(type)){
			    	forecast=DateUtils.addDays((Date)obj[3],PsiConfig.get(tranCountry).getTransportBySea());
			    }else{
			    	model="0";
			    	forecast=DateUtils.addDays((Date)obj[3],PsiConfig.get(tranCountry).getTransportBySky());
			    }	
			    String date=dateFormat.format(forecast);
				Map<String,Map<String,Integer>> countryTemp=map.get(tranCountry+"_"+name);
				if(countryTemp==null){
					countryTemp=Maps.newHashMap();
					map.put(tranCountry+"_"+name, countryTemp);
				}
				Map<String,Integer> dateTemp=countryTemp.get(model);
				if(dateTemp==null){
					dateTemp=Maps.newHashMap();
					countryTemp.put(model, dateTemp);
				}
				Integer beforeQuantity=dateTemp.get(date);
				dateTemp.put(date, quantity+(beforeQuantity==null?0:beforeQuantity));
	       }
		    return map;
	}
	
	@Transactional(readOnly = false)
	public void updateInvoiceFlag(Integer id){
		String sql="update lc_psi_transport_order set invoice_flag='0' where id=:p1 ";
		psiTransportOrderDao.updateBySql(sql, new Parameter(id));
	}
	
	public List<Object[]> findArrvalNextWeek(){
		String sql="SELECT r.`transport_no`,DATE_FORMAT(r.oper_arrival_date,'%Y-%m-%d') arrivalDate,r.`model`,CONCAT(t.`product_name`,CASE WHEN t.`color_code`!='' THEN CONCAT('_',t.`color_code`) ELSE '' END) NAME,t.`country_code`,t.`quantity` "+
		" FROM lc_psi_transport_order r  "+
		" JOIN lc_psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0'  "+
		" WHERE r.oper_arrival_date>DATE_SUB(CURDATE(),INTERVAL -2 DAY) AND r.oper_arrival_date<DATE_SUB(CURDATE(),INTERVAL -10 DAY) AND  "+
		" r.to_country='de' AND r.transport_sta!='8' AND t.sku=CONCAT(CONCAT(t.`product_name`,CASE WHEN t.`color_code`!='' THEN CONCAT('_',t.`color_code`) ELSE '' END),'_',t.`country_code`) ";
		return psiTransportOrderDao.findBySql(sql);
	}
	
	
	public List<Object[]> findArrvalNextWeekUS(){
		String sql="SELECT r.`transport_no`,DATE_FORMAT(r.oper_arrival_date,'%Y-%m-%d') arrivalDate,r.`model`,CONCAT(t.`product_name`,CASE WHEN t.`color_code`!='' THEN CONCAT('_',t.`color_code`) ELSE '' END) NAME,t.`country_code`,t.`quantity` "+
		" FROM lc_psi_transport_order r  "+
		" JOIN lc_psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0'  "+
		" WHERE r.oper_arrival_date>DATE_SUB(CURDATE(),INTERVAL -2 DAY) AND r.oper_arrival_date<DATE_SUB(CURDATE(),INTERVAL -10 DAY) AND  "+
		" r.to_store='120' AND r.transport_sta!='8' AND t.sku=CONCAT(CONCAT(t.`product_name`,CASE WHEN t.`color_code`!='' THEN CONCAT('_',t.`color_code`) ELSE '' END),'_',t.`country_code`) ";
		return psiTransportOrderDao.findBySql(sql);
	}
	
	
   //line model 
	public Map<String,Map<String,Map<String,LcPsiTransportOrder>>> findTransRate(String year) throws ParseException{
		Map<String,Map<String,Map<String,LcPsiTransportOrder>>> map=Maps.newLinkedHashMap();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy");
		Date start=dateFormat.parse(year+"-01-01");
		Date end=dateFormat.parse(Integer.parseInt(year)+1+"-01-01");
		String sql="SELECT r.model,d.name,	"+
				" ROUND(SUM( (IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0))*a.`quantity`* 	"+ 
				" (CASE WHEN r.`model`='2' THEN ( CASE WHEN p.`volume_ratio`>167 THEN  p.gw/p.`pack_quantity`  ELSE  ((CEIL(p.`pack_length`*p.`pack_width`*p.`pack_height`/5000)+0.5)/p.`pack_quantity`) END ) 	"+
				" WHEN r.`model`='0' THEN  ( CASE WHEN p.`pack_length`*p.`pack_width`*p.`pack_height`/6000/p.`pack_quantity`<p.gw/p.`pack_quantity`   THEN p.gw/p.`pack_quantity`  ELSE ROUND(p.`pack_length`*p.`pack_width`*p.`pack_height`/6000/p.`pack_quantity`,2)  END) 	"+
				" ELSE p.gw/p.`pack_quantity` END)/r.`weight`),2) money, 	"+
				" ROUND(SUM(a.`quantity`*(CASE WHEN r.`model`='2' THEN ( CASE WHEN p.`volume_ratio`>167 THEN  p.gw/p.`pack_quantity`  ELSE  ((CEIL(p.`pack_length`*p.`pack_width`*p.`pack_height`/5000)+0.5)/p.`pack_quantity`) END ) 	"+
				" WHEN r.`model`='0' THEN  ( CASE WHEN p.`pack_length`*p.`pack_width`*p.`pack_height`/6000/p.`pack_quantity`<p.gw/p.`pack_quantity`   THEN p.gw/p.`pack_quantity`  ELSE ROUND(p.`pack_length`*p.`pack_width`*p.`pack_height`/6000/p.`pack_quantity`,2)  END) 	"+
				" ELSE p.gw/p.`pack_quantity` END)),2) weight, 	"+
				" SUM(a.quantity) quantity,ROUND(SUM((a.`quantity`/a.`pack_quantity`)/p.`box_volume`),2) volume,DATE_FORMAT(r.`pick_up_date`,'%Y%m') dates 	"+
				" FROM lc_psi_transport_order r  	"+
				" JOIN lc_psi_transport_order_item a ON r.id=a.`transport_order_id` AND a.`del_flag`='0' 	"+
				" JOIN psi_product p ON a.`product_id`=p.id  AND p.`del_flag`='0' 	"+
				" JOIN sys_dict t ON p.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type'  	"+
				" JOIN psi_product_type_group g ON t.id=g.`dict_id`  	"+
				" JOIN psi_product_type_dict d ON d.id=g.id  AND d.`del_flag`='0' 	"+
				" WHERE r.`pick_up_date`>=:p1 AND r.`pick_up_date`<:p2 and r.from_store='130' AND r.`model` IN ('0','1','2')  AND  r.transport_sta='5'  	"+
				" GROUP BY d.name,r.model,dates order by dates,d.name";	
		List<Object[]> list=psiTransportOrderDao.findBySql(sql,new Parameter(start,end));
		for (Object[] obj: list) {
			String model=obj[0].toString();
			String line=obj[1].toString();
			Float money=Float.parseFloat(obj[2].toString());
			Float weight=Float.parseFloat(obj[3].toString());
			Integer quantity=Integer.parseInt(obj[4].toString());
			Float volume=Float.parseFloat(obj[5].toString());
			String month=obj[6].toString();
			Map<String,Map<String,LcPsiTransportOrder>> monthMap=map.get(month);
			if(monthMap==null){
				monthMap=Maps.newLinkedHashMap();
				map.put(month, monthMap);
			}
			
			Map<String,Map<String,LcPsiTransportOrder>> monthTotalMap=map.get("total");
			if(monthTotalMap==null){
				monthTotalMap=Maps.newLinkedHashMap();
				map.put("total", monthTotalMap);
			}
			
			Map<String,LcPsiTransportOrder> temp=monthMap.get(line);
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				monthMap.put(line, temp);
			}
			LcPsiTransportOrder order=new LcPsiTransportOrder();
			order.setVolume(volume);
			order.setWeight(weight);
			order.setPayAmount1(money);
			order.setPlaneNum(quantity);
			temp.put(model, order);
			
			Map<String,LcPsiTransportOrder> temp2=monthTotalMap.get(line);
			if(temp2==null){
				temp2=Maps.newLinkedHashMap();
				monthTotalMap.put(line, temp2);
			}
			LcPsiTransportOrder order2=temp2.get(model);
			if(order2==null){
				order2=new LcPsiTransportOrder();
				order2.setVolume(0f);
				order2.setWeight(0f);
				order2.setPayAmount1(0f);
				order2.setPlaneNum(0);
			}
			order2.setVolume(volume+order2.getVolume());
			order2.setWeight(weight+order2.getWeight());
			order2.setPayAmount1(money+order2.getPayAmount1());
			order2.setPlaneNum(quantity+order2.getPlaneNum());
			temp2.put(model, order2);
			
			
			
			LcPsiTransportOrder modelOrder=temp.get("total");
			if(modelOrder==null){
				modelOrder=new LcPsiTransportOrder();
				modelOrder.setVolume(0f);
				modelOrder.setWeight(0f);
				modelOrder.setPayAmount1(0f);
				modelOrder.setPlaneNum(0);
			}
			modelOrder.setVolume(volume+modelOrder.getVolume());
			modelOrder.setWeight(weight+modelOrder.getWeight());
			modelOrder.setPayAmount1(money+modelOrder.getPayAmount1());
			modelOrder.setPlaneNum(quantity+modelOrder.getPlaneNum());
			temp.put("total", modelOrder);
			
			
			LcPsiTransportOrder modelOrder2=temp2.get("total");
			if(modelOrder2==null){
				modelOrder2=new LcPsiTransportOrder();
				modelOrder2.setVolume(0f);
				modelOrder2.setWeight(0f);
				modelOrder2.setPayAmount1(0f);
				modelOrder2.setPlaneNum(0);
			}
			modelOrder2.setVolume(volume+modelOrder2.getVolume());
			modelOrder2.setWeight(weight+modelOrder2.getWeight());
			modelOrder2.setPayAmount1(money+modelOrder2.getPayAmount1());
			modelOrder2.setPlaneNum(quantity+modelOrder2.getPlaneNum());
			temp2.put("total", modelOrder2);
			
			Map<String,LcPsiTransportOrder> totalTemp=monthMap.get("total");
			if(totalTemp==null){
				totalTemp=Maps.newLinkedHashMap();
				monthMap.put("total", totalTemp);
			}
			LcPsiTransportOrder totalOrder=totalTemp.get(model);
			if(totalOrder==null){
				totalOrder=new LcPsiTransportOrder();
				totalOrder.setVolume(0f);
				totalOrder.setWeight(0f);
				totalOrder.setPayAmount1(0f);
				totalOrder.setPlaneNum(0);
			}
			totalOrder.setVolume(volume+totalOrder.getVolume());
			totalOrder.setWeight(weight+totalOrder.getWeight());
			totalOrder.setPayAmount1(money+totalOrder.getPayAmount1());
			totalOrder.setPlaneNum(quantity+totalOrder.getPlaneNum());
			totalTemp.put(model, totalOrder);
			
			
			Map<String,LcPsiTransportOrder> totalTemp2=monthTotalMap.get("total");
			if(totalTemp2==null){
				totalTemp2=Maps.newLinkedHashMap();
				monthTotalMap.put("total", totalTemp2);
			}
			LcPsiTransportOrder totalOrder2=totalTemp2.get(model);
			if(totalOrder2==null){
				totalOrder2=new LcPsiTransportOrder();
				totalOrder2.setVolume(0f);
				totalOrder2.setWeight(0f);
				totalOrder2.setPayAmount1(0f);
				totalOrder2.setPlaneNum(0);
			}
			totalOrder2.setVolume(volume+totalOrder2.getVolume());
			totalOrder2.setWeight(weight+totalOrder2.getWeight());
			totalOrder2.setPayAmount1(money+totalOrder2.getPayAmount1());
			totalOrder2.setPlaneNum(quantity+totalOrder2.getPlaneNum());
			totalTemp2.put(model, totalOrder2);
			
			
			LcPsiTransportOrder totalModelOrder=totalTemp.get("total");
			if(totalModelOrder==null){
				totalModelOrder=new LcPsiTransportOrder();
				totalModelOrder.setVolume(0f);
				totalModelOrder.setWeight(0f);
				totalModelOrder.setPayAmount1(0f);
				totalModelOrder.setPlaneNum(0);
			}
			totalModelOrder.setVolume(volume+totalModelOrder.getVolume());
			totalModelOrder.setWeight(weight+totalModelOrder.getWeight());
			totalModelOrder.setPayAmount1(money+totalModelOrder.getPayAmount1());
			totalModelOrder.setPlaneNum(quantity+totalModelOrder.getPlaneNum());
			totalTemp.put("total", totalModelOrder);
			
			LcPsiTransportOrder totalModelOrder2=totalTemp2.get("total");
			if(totalModelOrder2==null){
				totalModelOrder2=new LcPsiTransportOrder();
				totalModelOrder2.setVolume(0f);
				totalModelOrder2.setWeight(0f);
				totalModelOrder2.setPayAmount1(0f);
				totalModelOrder2.setPlaneNum(0);
			}
			totalModelOrder2.setVolume(volume+totalModelOrder2.getVolume());
			totalModelOrder2.setWeight(weight+totalModelOrder2.getWeight());
			totalModelOrder2.setPayAmount1(money+totalModelOrder2.getPayAmount1());
			totalModelOrder2.setPlaneNum(quantity+totalModelOrder2.getPlaneNum());
			totalTemp2.put("total", totalModelOrder2);
		}
		
		return map;
	}
	
	public  Map<String,Map<String,Float>> findLocal(String year) throws ParseException{
		Map<String,Map<String,Float>> map=Maps.newHashMap();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy");
		Date start=dateFormat.parse(year+"-01-01");
		Date end=dateFormat.parse(Integer.parseInt(year)+1+"-01-01");
	    String sql="SELECT d1.name, " +
		    		" ROUND(SUM(d.fee*(case when d.country like 'jp%' then 0.05809 when d.country like 'com%' then 6.4292 else 7.8873 end )*t.`quantity_shipped`*(p.gw/p.`pack_quantity`)/d.`weight`),2) money,DATE_FORMAT(d.`shipped_date`,'%Y%m') dates 	"+
	    		    " FROM psi_fba_inbound d "+
					" JOIN psi_fba_inbound_item t ON d.id=t.`fba_inbound_id`  "+
	    		    " join psi_sku s on s.sku=t.sku and s.country=d.country and s.del_flag='0' "+
					" JOIN psi_product p ON s.`product_id`=p.id  AND p.`del_flag`='0' 	"+
					" JOIN sys_dict t1 ON p.type=t1.`value` AND t1.`del_flag`='0' AND  t1.`type`='product_type'  	"+
					" JOIN psi_product_type_group g ON t1.id=g.`dict_id`  	"+
					" JOIN psi_product_type_dict d1 ON d1.id=g.id  AND d1.`del_flag`='0' 	"+
					" WHERE d.`shipped_date`>=:p1 AND d.`shipped_date`<:p2 and d.`ship_from_address` IN ('DE','US','JP') and d.fee is not null and d.fee>0 AND d.`shipment_status`='CLOSED' group by d1.name,dates ";
		
	    List<Object[]> list=psiTransportOrderDao.findBySql(sql,new Parameter(start,end));
		for (Object[] obj: list) {
			 String line=obj[0].toString();
			 Float sales=Float.parseFloat(obj[1].toString());
			 Map<String,Float> temp=map.get(obj[2].toString());
			 if(temp==null){
				 temp=Maps.newHashMap();
				 map.put(obj[2].toString(),temp);
			 }
			 temp.put(line, sales);
			 temp.put("total", sales+(temp.get("total")==null?0:temp.get("total")));
			 
			 Map<String,Float> totalTemp=map.get("total");
			 if(totalTemp==null){
				 totalTemp=Maps.newHashMap();
				 map.put("total",totalTemp);
			 }
			 totalTemp.put(line, sales+(totalTemp.get(line)==null?0:totalTemp.get(line)));
			 totalTemp.put("total", sales+(totalTemp.get("total")==null?0:totalTemp.get("total")));
		}
		return map;
	}
	
	public Map<String,Map<String,Float>> findSalesByLine(String year) throws ParseException{
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy");
		Date start=dateFormat.parse(year+"-01-01");
		Date end=dateFormat.parse(Integer.parseInt(year)+1+"-01-01");
		Map<String,Map<String,Float>>  map=Maps.newHashMap();
		String sql="SELECT p.NAME,SUM(CASE WHEN r.`country` IN ('de','fr','it','es') THEN r.`sure_sales`*7.8501  "+
				" WHEN r.`country`='uk' THEN r.`sure_sales`*8.8336  WHEN r.`country`='jp' THEN r.`sure_sales`*0.05799     "+
				" WHEN r.`country`='mx' THEN r.`sure_sales`*0.3398  ELSE r.`sure_sales`*6.424  END) sales,DATE_FORMAT(r.`date`,'%Y%m') dates   "+
				" FROM amazoninfo_sale_report r   "+
				" JOIN  psi_product a ON r.`product_name`=CONCAT(a.`brand`,' ',a.`model`) AND a.`del_flag`='0'   "+
				" JOIN sys_dict t ON a.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type'    "+
				" JOIN psi_product_type_group g ON t.id=g.`dict_id`    "+
				" JOIN psi_product_type_dict p ON p.id=g.id  AND p.`del_flag`='0'   "+
				" WHERE r.`order_type`='1' AND r.date>=:p1 AND r.`date`<:p2  GROUP BY p.name,dates";
		List<Object[]> list=psiTransportOrderDao.findBySql(sql,new Parameter(start,end));
		for (Object[] obj: list) {
			 String line=obj[0].toString();
			 Float sales=Float.parseFloat(obj[1].toString());
			 Map<String,Float> temp=map.get(obj[2].toString());
			 if(temp==null){
				 temp=Maps.newHashMap();
				 map.put(obj[2].toString(),temp);
			 }
			 temp.put(line, sales);
			 temp.put("total", sales+(temp.get("total")==null?0:temp.get("total")));
			 
			 Map<String,Float> totalTemp=map.get("total");
			 if(totalTemp==null){
				 totalTemp=Maps.newHashMap();
				 map.put("total",totalTemp);
			 }
			 totalTemp.put(line, sales+(totalTemp.get(line)==null?0:totalTemp.get(line)));
			 totalTemp.put("total", sales+(totalTemp.get("total")==null?0:totalTemp.get("total")));
		}
		 return map;       
	}
	
	
	public Map<String,Map<String,Integer>> findChangeBillByLine(String year) throws ParseException{
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy");
		Date start=dateFormat.parse(year+"-01-01");
		Date end=dateFormat.parse(Integer.parseInt(year)+1+"-01-01");
		Map<String,Map<String,Integer>> map=Maps.newHashMap();
		String sql="SELECT d.name,SUM(b.`quantity`),DATE_FORMAT(b.`apply_date`,'%Y%m') dates FROM psi_sku_change_bill b  "+
				" JOIN psi_product p ON b.`product_id`=p.id  AND p.`del_flag`='0' 	"+
				" JOIN sys_dict t ON p.type=t.`value` AND t.`del_flag`='0' AND  t.`type`='product_type'  "+
				" JOIN psi_product_type_group g ON t.id=g.`dict_id`  	 "+
				" JOIN psi_product_type_dict d ON d.id=g.id  AND d.`del_flag`='0' "+
				" WHERE b.`warehouse_id`='130' AND b.`apply_date`>=:p1 AND b.`apply_date`<:p2 "+
				" AND b.`cancel_date` IS NULL GROUP BY d.name,dates ";
		List<Object[]> list=psiTransportOrderDao.findBySql(sql,new Parameter(start,end));
		for (Object[] obj: list) {
			 String line=obj[0].toString();
			 Integer sales=Integer.parseInt(obj[1].toString());
			 Map<String,Integer> temp=map.get(obj[2].toString());
			 if(temp==null){
				 temp=Maps.newHashMap();
				 map.put(obj[2].toString(),temp);
			 }
			 temp.put(line, sales);
			 temp.put("total", sales+(temp.get("total")==null?0:temp.get("total")));
			 
			 Map<String,Integer> totalTemp=map.get("total");
			 if(totalTemp==null){
				 totalTemp=Maps.newHashMap();
				 map.put("total",totalTemp);
			 }
			 totalTemp.put(line, sales+(totalTemp.get(line)==null?0:totalTemp.get(line)));
			 totalTemp.put("total", sales+(totalTemp.get("total")==null?0:totalTemp.get("total")));
		}
		 return map;       
	}
	
	
	

	public Map<String,Integer>  findFbaTranDays(){
		Map<String,List<Integer>> map=Maps.newHashMap();
		String sql="SELECT a.to_country,a.model,TIMESTAMPDIFF(DAY,a.shippedDate,b.finish_date) days,DATE_FORMAT(a.`shippedDate`,'%m') FROM  "+
		 " (SELECT  a.to_country,a.`transport_no`,a.model,IFNULL(a.`pick_up_date`,a.`etd_date`) shippedDate,SUBSTRING_INDEX(SUBSTRING_INDEX(a.shipment_id,',',b.help_topic_id+1),',',-1) shipment_id   "+
		 " FROM lc_psi_transport_order a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.shipment_id) - LENGTH(REPLACE(a.shipment_id,',',''))+1)   "+
		 " WHERE a.`from_store`=130 AND a.`transport_type`='1' AND a.`transport_sta`='5' AND a.`model` IN ('0','1') AND a.`shipment_id`!=''  "+
		 " ) a JOIN psi_fba_inbound b ON a.shipment_id=b.shipment_id   "+
		 " WHERE b.shipment_status='CLOSED' AND b.finish_date IS NOT NULL ";	
		List<Object[]> list=psiTransportOrderDao.findBySql(sql);
		for (Object[] obj: list) {
			 String country=obj[0].toString();
			 String model=obj[1].toString();
			 Integer days=Integer.parseInt(obj[2].toString());
			 String month=obj[3].toString();
			 
			 String key=country+"_"+model+"_"+month;
			 List<Integer> tmp=map.get(key);
			 if(tmp==null){
				 tmp=Lists.newArrayList();
				 map.put(key, tmp);
			 }
			 tmp.add(days);
		}	
		
		Map<String,Integer> fbaDatesMap=Maps.newHashMap();
		
		Map<String,Float> avgDatesMap=Maps.newHashMap();
		for (Map.Entry<String,List<Integer>> entry: map.entrySet()) {
			String key=entry.getKey();
			List<Integer> temp=entry.getValue();
			Integer total=0;
			for (Integer num: temp) {
				total+=num;
			}
			avgDatesMap.put(key,total*1f/temp.size());
		}
		
		for (Map.Entry<String,List<Integer>> entry: map.entrySet()) {
			Float avgDays=avgDatesMap.get(entry.getKey());//(x1-m)(x1-m)+..(xn-m)(xn-m) /n
			List<Integer> temp=entry.getValue();
			Float total=0f;
			for (Integer num: temp) {
				total+=(num-avgDays)*(num-avgDays);
			}
			fbaDatesMap.put(entry.getKey(), MathUtils.roundUp(avgDays+Math.sqrt(total*1d/temp.size())));
		}
		
		return fbaDatesMap;
	}
	
	
	public Map<String,Integer>  findFbaTranDays2(){
		Map<String,List<Integer>> map=Maps.newHashMap();
		String sql="SELECT a.to_country,a.model,TIMESTAMPDIFF(DAY,a.shippedDate,b.finish_date) days,(case when DATE_FORMAT(a.`shippedDate`,'%m') in ('01','02','03') then '1' when DATE_FORMAT(a.`shippedDate`,'%m') in ('04','05','06') then '2' when DATE_FORMAT(a.`shippedDate`,'%m') in ('07','08','09') then '3' else '4' end) month FROM  "+
		 " (SELECT  a.to_country,a.`transport_no`,a.model,IFNULL(a.`pick_up_date`,a.`etd_date`) shippedDate,SUBSTRING_INDEX(SUBSTRING_INDEX(a.shipment_id,',',b.help_topic_id+1),',',-1) shipment_id   "+
		 " FROM lc_psi_transport_order a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.shipment_id) - LENGTH(REPLACE(a.shipment_id,',',''))+1)   "+
		 " WHERE a.`from_store`=130 AND a.`transport_type`='1' AND a.`transport_sta`='5' AND a.`model` IN ('0','1') AND a.`shipment_id`!=''  "+
		 " ) a JOIN psi_fba_inbound b ON a.shipment_id=b.shipment_id   "+
		 " WHERE b.shipment_status='CLOSED' AND b.finish_date IS NOT NULL ";	
		List<Object[]> list=psiTransportOrderDao.findBySql(sql);
		for (Object[] obj: list) {
			 String country=obj[0].toString();
			 String model=obj[1].toString();
			 Integer days=Integer.parseInt(obj[2].toString());
			 String month=obj[3].toString();
			 
			 String key=country+"_"+model+"_"+month;
			 List<Integer> tmp=map.get(key);
			 if(tmp==null){
				 tmp=Lists.newArrayList();
				 map.put(key, tmp);
			 }
			 tmp.add(days);
			 
			 String totalKey=country+"_"+model+"_total";
			 List<Integer> totalTemp=map.get(totalKey);
			 if(totalTemp==null){
				 totalTemp=Lists.newArrayList();
				 map.put(totalKey, totalTemp);
			 }
			 totalTemp.add(days);
		}	
		
		Map<String,Integer> fbaDatesMap=Maps.newHashMap();
		
		Map<String,Float> avgDatesMap=Maps.newHashMap();
		for (Map.Entry<String,List<Integer>> entry: map.entrySet()) {
			String key=entry.getKey();
            if(key.contains("total")){
				continue;
			}
			List<Integer> temp=entry.getValue();
			Integer total=0;
			for (Integer num: temp) {
				total+=num;
			}
			avgDatesMap.put(key,total*1f/temp.size());
		}
		
		for (Map.Entry<String,List<Integer>> entry: map.entrySet()) {
			String key=entry.getKey();
		    if(key.contains("total")){
					continue;
			}
			Float avgDays=avgDatesMap.get(key);//(x1-m)(x1-m)+..(xn-m)(xn-m) /n
			List<Integer> temp=entry.getValue();
			Float total=0f;
			for (Integer num: temp) {
				total+=(num-avgDays)*(num-avgDays);
			}
			
			String totalKey=key.substring(0,key.lastIndexOf("_"))+"_total";
			List<Integer> totalList=map.get(totalKey);
			Integer[] array = new Integer[totalList.size()];
			for (int i=0;i<array.length;i++) {
				array[i]=totalList.get(i);
			}		
			List<Integer> modalNums = getModalNums(array);
			fbaDatesMap.put(entry.getKey(), MathUtils.roundUp(modalNums.get(modalNums.size()-1)+Math.sqrt(total*1d/temp.size())));
			fbaDatesMap.put(totalKey,modalNums.get(modalNums.size()-1));
			
		}
		
		return fbaDatesMap;
	}
	
	
	public Map<String,Integer>  findLocalTranDays2(){
		Map<String,List<Integer>> map=Maps.newHashMap();
		String sql="SELECT LOWER(a.to_country),a.model,TIMESTAMPDIFF(DAY,IFNULL(a.`pick_up_date`,a.`etd_date`),a.`oper_arrival_date`) days, "+
				   " (case when DATE_FORMAT(IFNULL(a.`pick_up_date`,a.`etd_date`),'%m') in ('01','02','03') then '1' when DATE_FORMAT(IFNULL(a.`pick_up_date`,a.`etd_date`),'%m') in ('04','05','06') then '2' when DATE_FORMAT(IFNULL(a.`pick_up_date`,a.`etd_date`),'%m') in ('07','08','09') then '3' else '4' end) pickUpDate,DATE_FORMAT(a.`oper_arrival_date`,'%m') "+
				   " FROM lc_psi_transport_order a "+
				   " WHERE a.`from_store`=130 AND a.`transport_type`='0' AND a.`transport_sta`='5' AND a.`model` IN ('0','1') ";
		List<Object[]> list=psiTransportOrderDao.findBySql(sql);
		for (Object[] obj: list) {
			 String country=obj[0].toString();
			 String model=obj[1].toString();
			 Integer days=Integer.parseInt(obj[2].toString());
			 String month=obj[3].toString();
			 String arrivalMonth=obj[4].toString();
			 if("fr,it,es,uk".contains(obj[1].toString())){
				 country="de";
			 }else if("us".contains(obj[1].toString())){
				 country="com";
			 }
			 String key=country+"_"+model+"_"+month+"_"+arrivalMonth;
			 List<Integer> tmp=map.get(key);
			 if(tmp==null){
				 tmp=Lists.newArrayList();
				 map.put(key, tmp);
			 }
			 tmp.add(days);
			 
			 String totalKey=country+"_"+model+"_total";
			 List<Integer> totalTemp=map.get(totalKey);
			 if(totalTemp==null){
				 totalTemp=Lists.newArrayList();
				 map.put(totalKey, totalTemp);
			 }
			 totalTemp.add(days);
		}		 
		
		
		Map<String,Integer> localDatesMap=Maps.newHashMap();
		Map<String,Float> avgDatesMap=Maps.newHashMap();
		
		for (Map.Entry<String,List<Integer>> entry: map.entrySet()) {
			String key=entry.getKey();
            if(key.contains("total")){
				continue;
			}
			List<Integer> temp=entry.getValue();
			Integer total=0;
			for (Integer num: temp) {
				total+=num;
			}
			avgDatesMap.put(key,total*1f/temp.size());
		}
		

		for (Map.Entry<String,List<Integer>> entry: map.entrySet()) {
			String key=entry.getKey();
			if(key.contains("total")){
				continue;
			}
			Float avgDays=avgDatesMap.get(key);//(x1-m)(x1-m)+..(xn-m)(xn-m) /n
			List<Integer> temp=entry.getValue();
			Float total=0f;
			for (Integer num: temp) {
				total+=(num-avgDays)*(num-avgDays);
			}
			
			String[] arr=key.split("_");
			String totalKey=arr[0]+"_"+arr[1]+"_total";
			List<Integer> totalList=map.get(totalKey);
			Integer[] array = new Integer[totalList.size()];
			for (int i=0;i<array.length;i++) {
				array[i]=totalList.get(i);
			}		
			List<Integer> modalNums = getModalNums(array);
			
			localDatesMap.put(key, MathUtils.roundUp(modalNums.get(modalNums.size()-1)+Math.sqrt(total*1d/temp.size())));
			localDatesMap.put(totalKey,modalNums.get(modalNums.size()-1));
		}
		
		Map<String,List<Integer>> map2=Maps.newHashMap();
		String sql2=" SELECT b.country,DATE_FORMAT(b.shipped_date,'%m') shippedMonth,TIMESTAMPDIFF(DAY,b.shipped_date,b.finish_date) days FROM psi_fba_inbound b "+
					" WHERE b.shipment_status='CLOSED' AND b.ship_from_address IN ('DE','US','JP')  "+
					" AND b.finish_date IS NOT NULL AND b.shipped_date>='2016-01-01' AND b.finish_date>=b.shipped_date AND TIMESTAMPDIFF(DAY,b.shipped_date,b.finish_date)<40 ";
		
		List<Object[]> list2=psiTransportOrderDao.findBySql(sql2);
		for (Object[] obj: list2) {
			 String country=obj[0].toString();
			 String month=obj[1].toString();
			 Integer days=Integer.parseInt(obj[2].toString());
			 if(country.startsWith("com")){
				 country="com";
			 }
			 String key=country+"_"+month;
			 List<Integer> tmp=map2.get(key);
			 if(tmp==null){
				 tmp=Lists.newArrayList();
				 map2.put(key, tmp);
			 }
			 tmp.add(days);
			 
			 List<Integer> totalTemp=map2.get("total");
			 if(totalTemp==null){
				 totalTemp=Lists.newArrayList();
				 map2.put("total", totalTemp);
			 }
			 totalTemp.add(days);
		}		 
		//m=(x1+...+xn)/n  s*s=(x1-m)(x1-m)+..(xn-m)(xn-m) /n
		
		Map<String,Integer> daysMap=Maps.newHashMap();
		Map<String,Float> avgDaysMap=Maps.newHashMap();
		for (Map.Entry<String,List<Integer>> entry: map2.entrySet()) {
			String key=entry.getKey();
			if(key.contains("total")){
				continue;
			}
			List<Integer> temp=entry.getValue();
			Integer total=0;
			for (Integer num: temp) {
				total+=num;
			}
			avgDaysMap.put(key,total*1f/temp.size());
		}
		
		
		for (Map.Entry<String,List<Integer>> entry: map2.entrySet()) {
			String key=entry.getKey();
			if(key.contains("total")){
				continue;
			}
			Float avgDays=avgDaysMap.get(key);//(x1-m)(x1-m)+..(xn-m)(xn-m) /n
			List<Integer> temp=entry.getValue();
			Float total=0f;
			for (Integer num: temp) {
				total+=(num-avgDays)*(num-avgDays);
			}

			List<Integer> totalList=map2.get("total");
			Integer[] array = new Integer[totalList.size()];
			for (int i=0;i<array.length;i++) {
				array[i]=totalList.get(i);
			}		
			List<Integer> modalNums = getModalNums(array);
			
			daysMap.put(key, MathUtils.roundUp(modalNums.get(modalNums.size()-1)+Math.sqrt(total*1d/temp.size())));
			daysMap.put("total",modalNums.get(modalNums.size()-1));
			
		}
		
		
		Map<String,Integer> finalMap=Maps.newHashMap();
		for (Map.Entry<String,Integer> entry: localDatesMap.entrySet()) {
			 String key=entry.getKey();//country+"_"+model+"_"+month+"_"+arrivalMonth;
			 String[] arr=key.split("_");
			 if(key.contains("total")){
					finalMap.put(arr[0]+"_"+arr[1]+"_total",entry.getValue()+daysMap.get("total"));
			 }else{
				 if(daysMap.get(arr[0]+"_"+arr[3])!=null){
					 finalMap.put(arr[0]+"_"+arr[1]+"_"+arr[2],entry.getValue()+daysMap.get(arr[0]+"_"+arr[3]));
				 } 
			 }
			 
		}
		
		return finalMap;
	}
	 
	 public static List<Integer> getModalNums(Integer[] arr) {
	        int n = arr.length;

	        if (n == 0) {
	            return Collections.EMPTY_LIST;
	        }

	        if (n == 1) {
	            return Arrays.asList(arr[0]);
	        }

	        Map<Integer, Integer> freqMap = Maps.newHashMap();
	        for (int i = 0; i < n; i++) { // 统计数组中每个数出现的频率
	            Integer v = freqMap.get(arr[i]);
	            // v == null 说明 freqMap 中还没有这个 arr[i] 这个键
	            freqMap.put(arr[i], v == null ? 1 : v + 1);
	        }

	        // 将 freqMap 中所有的键值对（键为数，值为数出现的频率）放入一个 ArrayList
	        List<Map.Entry<Integer, Integer>> entries = Lists.newArrayList(freqMap.entrySet());
	        // 对 entries 按出现频率从大到小排序
	        Collections.sort(entries, new Comparator<Map.Entry<Integer, Integer>>() {
	            @Override
	            public int compare(Map.Entry<Integer, Integer> e1, Map.Entry<Integer, Integer> e2) {
	                return e2.getValue() - e1.getValue();
	            }
	        });

	        List<Integer> modalNums = Lists.newArrayList();
	        modalNums.add(entries.get(0).getKey()); // 排序后第一个 entry 的键肯定是一个众数

	        int size = entries.size();
	        for (int i = 1; i < size; i++) {
	            // 如果之后的 entry 与第一个 entry 的 value 相等，那么这个 entry 的键也是众数
	            if (entries.get(i).getValue().equals(entries.get(0).getValue())) {
	                modalNums.add(entries.get(i).getKey());
	            } else {
	                break;
	            }
	        }

	        return modalNums;
	    }
	
	public Map<String,Integer>  findLocalTranDays(){
		Map<String,List<Integer>> map=Maps.newHashMap();
		String sql="SELECT LOWER(a.to_country),a.model,TIMESTAMPDIFF(DAY,IFNULL(a.`pick_up_date`,a.`etd_date`),a.`oper_arrival_date`) days, "+
				   " DATE_FORMAT(IFNULL(a.`pick_up_date`,a.`etd_date`),'%m') pickUpDate,DATE_FORMAT(a.`oper_arrival_date`,'%m') "+
				   " FROM lc_psi_transport_order a "+
				   " WHERE a.`from_store`=130 AND a.`transport_type`='0' AND a.`transport_sta`='5' AND a.`model` IN ('0','1') ";
		List<Object[]> list=psiTransportOrderDao.findBySql(sql);
		for (Object[] obj: list) {
			 String country=obj[0].toString();
			 String model=obj[1].toString();
			 Integer days=Integer.parseInt(obj[2].toString());
			 String month=obj[3].toString();
			 String arrivalMonth=obj[4].toString();
			 if("fr,it,es,uk".contains(obj[1].toString())){
				 country="de";
			 }else if("us".contains(obj[1].toString())){
				 country="com";
			 }
			 String key=country+"_"+model+"_"+month+"_"+arrivalMonth;
			 List<Integer> tmp=map.get(key);
			 if(tmp==null){
				 tmp=Lists.newArrayList();
				 map.put(key, tmp);
			 }
			 tmp.add(days);
		}		 
		
		
		Map<String,Integer> localDatesMap=Maps.newHashMap();
		Map<String,Float> avgDatesMap=Maps.newHashMap();
		
		for (Map.Entry<String,List<Integer>> entry: map.entrySet()) {
			String key=entry.getKey();
			List<Integer> temp=entry.getValue();
			Integer total=0;
			for (Integer num: temp) {
				total+=num;
			}
			avgDatesMap.put(key,total*1f/temp.size());
		}
		

		for (Map.Entry<String,List<Integer>> entry: map.entrySet()) {
			Float avgDays=avgDatesMap.get(entry.getKey());//(x1-m)(x1-m)+..(xn-m)(xn-m) /n
			List<Integer> temp=entry.getValue();
			Float total=0f;
			for (Integer num: temp) {
				total+=(num-avgDays)*(num-avgDays);
			}
			localDatesMap.put(entry.getKey(), MathUtils.roundUp(avgDays+Math.sqrt(total*1d/temp.size())));
		}
		
		
		
		Map<String,List<Integer>> map2=Maps.newHashMap();
		String sql2=" SELECT b.country,DATE_FORMAT(b.shipped_date,'%m') shippedMonth,TIMESTAMPDIFF(DAY,b.shipped_date,b.finish_date) days FROM psi_fba_inbound b "+
					" WHERE b.shipment_status='CLOSED' AND b.ship_from_address IN ('DE','US','JP')  "+
					" AND b.finish_date IS NOT NULL AND b.shipped_date>='2016-01-01' AND b.finish_date>=b.shipped_date AND TIMESTAMPDIFF(DAY,b.shipped_date,b.finish_date)<40 ";
		
		List<Object[]> list2=psiTransportOrderDao.findBySql(sql2);
		for (Object[] obj: list2) {
			 String country=obj[0].toString();
			 String month=obj[1].toString();
			 Integer days=Integer.parseInt(obj[2].toString());
			 if(country.startsWith("com")){
				 country="com";
			 }
			 String key=country+"_"+month;
			 List<Integer> tmp=map2.get(key);
			 if(tmp==null){
				 tmp=Lists.newArrayList();
				 map2.put(key, tmp);
			 }
			 tmp.add(days);
		}		 
		//m=(x1+...+xn)/n  s*s=(x1-m)(x1-m)+..(xn-m)(xn-m) /n
		
		Map<String,Integer> daysMap=Maps.newHashMap();
		Map<String,Float> avgDaysMap=Maps.newHashMap();
		for (Map.Entry<String,List<Integer>> entry: map2.entrySet()) {
			String key=entry.getKey();
			List<Integer> temp=entry.getValue();
			Integer total=0;
			for (Integer num: temp) {
				total+=num;
			}
			avgDaysMap.put(key,total*1f/temp.size());
		}
		
		
		for (Map.Entry<String,List<Integer>> entry: map2.entrySet()) {
			Float avgDays=avgDaysMap.get(entry.getKey());//(x1-m)(x1-m)+..(xn-m)(xn-m) /n
			List<Integer> temp=entry.getValue();
			Float total=0f;
			for (Integer num: temp) {
				total+=(num-avgDays)*(num-avgDays);
			}
			daysMap.put(entry.getKey(), MathUtils.roundUp(avgDays+Math.sqrt(total*1d/temp.size())));
		}
		
		
		Map<String,Integer> finalMap=Maps.newHashMap();
		for (Map.Entry<String,Integer> entry: localDatesMap.entrySet()) {
			 String key=entry.getKey();//country+"_"+model+"_"+month+"_"+arrivalMonth;
			 String[] arr=key.split("_");
			 if(daysMap.get(arr[0]+"_"+arr[3])!=null){
				 finalMap.put(arr[0]+"_"+arr[1]+"_"+arr[2],entry.getValue()+daysMap.get(arr[0]+"_"+arr[3]));
			 }
		}
		
		return finalMap;
	}
	
	@Transactional(readOnly = false)
	public void countTransDays(){//String month,String model,String country
		Map<String,Integer> fbaMap=findFbaTranDays();
		Map<String,Integer> localMap=findLocalTranDays();
				
		String sql="select id from psi_transport_days where month=:p1 and model=:p2 and country=:p3 ";
		String updateFbaSql="update psi_transport_days set fba=:p1 where id=:p2 ";
		String addFbaSql="insert into psi_transport_days(month,model,country,fba) values(:p1,:p2,:p3,:p4)";
		String updateLocalSql="update psi_transport_days set local=:p1 where id=:p2 ";
		String addLocalSql="insert into psi_transport_days(month,model,country,local) values(:p1,:p2,:p3,:p4)";
		
		if(fbaMap!=null&&fbaMap.size()>0){
			for (Map.Entry<String,Integer> entry: fbaMap.entrySet()) {
				 String[] arr=entry.getKey().split("_");//country+"_"+model+"_"+month
				 String month=arr[2];
				 String model=arr[1];
				 String country=arr[0];
				 List<Object> list=psiTransportOrderDao.findBySql(sql,new Parameter(month,model,country));
				 if(list!=null&&list.size()>0){
					 psiTransportOrderDao.updateBySql(updateFbaSql, new Parameter(entry.getValue(),list.get(0)));
				 }else{
					 psiTransportOrderDao.updateBySql(addFbaSql, new Parameter(month,model,country,entry.getValue()));
				 }
			}
		}
		
		if(localMap!=null&&localMap.size()>0){
			for (Map.Entry<String,Integer> entry: localMap.entrySet()) {
				 String[] arr=entry.getKey().split("_");//country+"_"+model+"_"+month
				 String month=arr[2];
				 String model=arr[1];
				 String country=arr[0];
				 List<Object> list=psiTransportOrderDao.findBySql(sql,new Parameter(month,model,country));
				 if(list!=null&&list.size()>0){
					 psiTransportOrderDao.updateBySql(updateLocalSql, new Parameter(entry.getValue(),list.get(0)));
				 }else{
					 psiTransportOrderDao.updateBySql(addLocalSql, new Parameter(month,model,country,entry.getValue()));
				 }
			}
		}
	}
	
	@Transactional(readOnly = false)
	public void countTransDays2(){//String month,String model,String country
		Map<String,Integer> fbaMap=findFbaTranDays2();
		Map<String,Integer> localMap=findLocalTranDays2();
				
		String sql="select id from psi_transport_days where month=:p1 and model=:p2 and country=:p3 ";
		String updateFbaSql="update psi_transport_days set fba=:p1 where id=:p2 ";
		String addFbaSql="insert into psi_transport_days(month,model,country,fba) values(:p1,:p2,:p3,:p4)";
		String updateLocalSql="update psi_transport_days set local=:p1 where id=:p2 ";
		String addLocalSql="insert into psi_transport_days(month,model,country,local) values(:p1,:p2,:p3,:p4)";
		
		if(fbaMap!=null&&fbaMap.size()>0){
			for (Map.Entry<String,Integer> entry: fbaMap.entrySet()) {
				 String[] arr=entry.getKey().split("_");//country+"_"+model+"_"+month
				 String month=arr[2];
				 String model=arr[1];
				 String country=arr[0];
				 List<Object> list=psiTransportOrderDao.findBySql(sql,new Parameter(month,model,country));
				 if(list!=null&&list.size()>0){
					 psiTransportOrderDao.updateBySql(updateFbaSql, new Parameter(entry.getValue(),list.get(0)));
				 }else{
					 psiTransportOrderDao.updateBySql(addFbaSql, new Parameter(month,model,country,entry.getValue()));
				 }
			}
		}
		
		if(localMap!=null&&localMap.size()>0){
			for (Map.Entry<String,Integer> entry: localMap.entrySet()) {
				 String[] arr=entry.getKey().split("_");//country+"_"+model+"_"+month
				 String month=arr[2];
				 String model=arr[1];
				 String country=arr[0];
				 List<Object> list=psiTransportOrderDao.findBySql(sql,new Parameter(month,model,country));
				 if(list!=null&&list.size()>0){
					 psiTransportOrderDao.updateBySql(updateLocalSql, new Parameter(entry.getValue(),list.get(0)));
				 }else{
					 psiTransportOrderDao.updateBySql(addLocalSql, new Parameter(month,model,country,entry.getValue()));
				 }
			}
		}
	}
	
	
	public Map<String,Integer> findTranDays(String month){
		Map<String,Integer> map=Maps.newHashMap();
		String sql="SELECT country,IFNULL(LOCAL,fba) FROM psi_transport_days s "+
		    " WHERE s.`model`='1' AND MONTH=:p1 AND country IN ('de','com','jp') AND (fba IS NOT NULL OR LOCAL IS NOT NULL) ";
		List<Object[]> list=psiTransportOrderDao.findBySql(sql,new Parameter(month));
		for (Object[] obj: list) {
			map.put(obj[0].toString(),Integer.parseInt(obj[1].toString()));
		}
		return map;
	}
	
	
	public Map<String,Map<String,Map<String,Map<String,Float>>>> findTransportFee(String year) throws ParseException{
		Map<String,Map<String,Map<String,Map<String,Float>>>>  map = Maps.newHashMap();
		String sql="SELECT '0',DATE_FORMAT(r.`export_date`,'%Y%m') DATE,(SELECT p.nikename FROM psi_supplier p WHERE p.id=r.`vendor1`) vendor, "+
				" (CASE WHEN r.`to_country` IN ('de','uk') THEN 'DE' WHEN r.`to_country` IN ('com','us','com2','com3') THEN 'US' WHEN r.`to_country`='ca' THEN 'CA' WHEN r.`to_country`='mx' THEN 'MX' WHEN r.`to_country`='jp' THEN 'JP' ELSE '' END) country,  "+
				" SUM(r.`local_amount`) local_amount,r.currency1  "+
				" FROM lc_psi_transport_order r   "+
				" WHERE r.`export_date`>=:p1 AND r.`export_date`<=:p2  and r.from_store in ('21','130')  and r.transport_type in ('0','1')  AND r.`model`!='4' AND r.local_amount IS NOT NULL AND r.`to_country` IS NOT NULL  AND r.`to_country`!='' AND r.`transport_sta`!='0' AND r.`transport_sta`!='8' "+
				" GROUP BY  DATE,country,r.`vendor1`,r.currency1 "+
				" UNION  ALL "+
				" SELECT '0',DATE_FORMAT(r.`export_date`,'%Y%m') DATE,(SELECT p.nikename FROM psi_supplier p WHERE p.id=r.`vendor2`) vendor, "+
				" (CASE WHEN r.`to_country` IN ('de','uk') THEN 'DE' WHEN r.`to_country` IN ('com','us','com2','com3') THEN 'US' WHEN r.`to_country`='ca' THEN 'CA' WHEN r.`to_country`='mx' THEN 'MX' WHEN r.`to_country`='jp' THEN 'JP' ELSE '' END) country, "+
				" SUM(r.`tran_amount`) tran_amount,r.currency2 "+
				" FROM lc_psi_transport_order r  "+
				" 	WHERE r.`export_date`>=:p1 AND r.`export_date`<=:p2   and r.from_store in ('21','130') and r.transport_type in ('0','1') AND r.`model`!='4' AND r.tran_amount IS NOT NULL AND r.`to_country` IS NOT NULL  AND r.`to_country`!='' AND r.`transport_sta`!='0' AND r.`transport_sta`!='8' "+
				" 	GROUP BY  DATE,country,r.`vendor2`,r.currency2 "+
				" UNION  ALL "+
				" SELECT '1',DATE_FORMAT(r.`export_date`,'%Y%m') DATE,(SELECT p.nikename FROM psi_supplier p WHERE p.id=r.`vendor3`) vendor, "+
				" 	(CASE WHEN r.`to_country` IN ('de','uk') THEN 'DE' WHEN r.`to_country` IN ('com','us','com2','com3') THEN 'US' WHEN r.`to_country`='ca' THEN 'CA' WHEN r.`to_country`='mx' THEN 'MX' WHEN r.`to_country`='jp' THEN 'JP' ELSE '' END) country, "+
				" 	SUM(r.`dap_amount`) dap_amount,r.currency3 "+
				" 	FROM lc_psi_transport_order r  "+
				" 	WHERE r.`export_date`>=:p1 AND r.`export_date`<=:p2   and r.from_store in ('21','130')  and r.transport_type in ('0','1') AND r.`model`!='4' AND r.dap_amount IS NOT NULL AND r.`to_country` IS NOT NULL  AND r.`to_country`!='' AND r.`transport_sta`!='0' AND r.`transport_sta`!='8' "+
				" GROUP BY  DATE,country,r.`vendor3`,r.currency3 "+
				" UNION  ALL "+
				" SELECT '0',DATE_FORMAT(r.`export_date`,'%Y%m') DATE,(SELECT p.nikename FROM psi_supplier p WHERE p.id=r.`vendor4`) vendor, "+
				" (CASE WHEN r.`to_country` IN ('de','uk') THEN 'DE' WHEN r.`to_country` IN ('com','us','com2','com3') THEN 'US' WHEN r.`to_country`='ca' THEN 'CA' WHEN r.`to_country`='mx' THEN 'MX' WHEN r.`to_country`='jp' THEN 'JP' ELSE '' END) country, "+
				" SUM(r.`other_amount`) other_amount,r.currency4 "+
				" FROM lc_psi_transport_order r  "+
				" WHERE r.`export_date`>=:p1 AND r.`export_date`<=:p2   and r.from_store in ('21','130')  and r.transport_type in ('0','1')  AND r.`model`!='4' AND r.other_amount IS NOT NULL AND r.`to_country` IS NOT NULL  AND r.`to_country`!='' AND r.`transport_sta`!='0' AND r.`transport_sta`!='8' "+
				" GROUP BY  DATE,country,r.`vendor4`,r.currency4 "+
				" UNION  ALL "+
				" SELECT '1',DATE_FORMAT(r.`export_date`,'%Y%m') DATE,(SELECT p.nikename FROM psi_supplier p WHERE p.id=r.`vendor7`) vendor, "+
				" (CASE WHEN r.`to_country` IN ('de','uk') THEN 'DE' WHEN r.`to_country` IN ('com','us','com2','com3') THEN 'US' WHEN r.`to_country`='ca' THEN 'CA' WHEN r.`to_country`='mx' THEN 'MX' WHEN r.`to_country`='jp' THEN 'JP' ELSE '' END) country, "+
				" SUM(r.`other_amount1`) other_amount1,r.currency7 "+
				" FROM lc_psi_transport_order r  "+
				" WHERE r.`export_date`>=:p1 AND r.`export_date`<=:p2    and r.from_store in ('21','130')  and r.transport_type in ('0','1') AND r.`model`!='4' AND r.other_amount1 IS NOT NULL AND r.`to_country` IS NOT NULL  AND r.`to_country`!='' AND r.`transport_sta`!='0' AND r.`transport_sta`!='8' "+
				" GROUP BY  DATE,country,r.`vendor7`,r.currency7 "+
				" UNION  ALL "+
				" SELECT '2',DATE_FORMAT(r.`export_date`,'%Y%m') DATE,(SELECT p.nikename FROM psi_supplier p WHERE p.id=r.`vendor6`) vendor, "+
				" (CASE WHEN r.`to_country` IN ('de','uk') THEN 'DE' WHEN r.`to_country` IN ('com','us','com2','com3') THEN 'US' WHEN r.`to_country`='ca' THEN 'CA' WHEN r.`to_country`='mx' THEN 'MX' WHEN r.`to_country`='jp' THEN 'JP' ELSE '' END) country, "+
				" SUM(IFNULL(r.`tax_taxes`,0)+IFNULL(r.`duty_taxes`,0)+IFNULL(r.`other_taxes`,0)) tax,r.currency6 "+
				"  FROM lc_psi_transport_order r  "+
				" WHERE r.`export_date`>=:p1 AND r.`export_date`<=:p2  and r.from_store in ('21','130')  and r.transport_type in ('0','1') AND r.`model`!='4' AND (r.tax_taxes IS NOT NULL or r.duty_taxes IS NOT NULL or  r.other_taxes IS NOT NULL ) AND r.`to_country` IS NOT NULL  AND r.`to_country`!='' AND r.`transport_sta`!='0' AND r.`transport_sta`!='8' "+
				" GROUP BY  DATE,country,r.`vendor6`,r.currency6 ";
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		List<Object[]> list=psiTransportOrderDao.findBySql(sql,new Parameter(df.parse(year+"-01-01"),df.parse(year+"-12-31")));
		for (Object[] obj : list) {
			Map<String,Map<String,Map<String,Float>>>  temp = map.get(obj[3].toString());
			if(temp==null){
				temp = Maps.newHashMap();
				map.put(obj[3].toString(),temp);
			}
			String key = ((obj[2]==null?"No":obj[2].toString())+"-"+(obj[5]==null?"No":obj[5].toString()));
			Map<String,Map<String,Float>> countryTemp = temp.get(key); 
			if(countryTemp==null){
				countryTemp = Maps.newHashMap();
				temp.put(key, countryTemp);
			}
			Map<String,Float> supplierMap = countryTemp.get(obj[0].toString());
			if(supplierMap==null){
				supplierMap = Maps.newHashMap();
				countryTemp.put(obj[0].toString(),supplierMap);
			}
			float fee = (supplierMap.get(obj[1].toString())==null?0:supplierMap.get(obj[1].toString()));
			supplierMap.put(obj[1].toString(),fee+Float.parseFloat(obj[4].toString()));
		}
		return map;
	}
	
	public Map<String,List<Object[]>> findDeclare(String year) throws ParseException{
		Map<String,List<Object[]>> map=Maps.newHashMap();
		String sql="SELECT (CASE WHEN r.`transport_type`='3' THEN ((CASE WHEN r.`to_country` IN ('de','uk') THEN 'Offline_DE' WHEN r.`to_country` IN ('com','us','com2','com3','ca','mx') THEN 'Offline_US'  WHEN r.`to_country`='jp' THEN 'Offline_JP' ELSE 'Offline_Other' END)) ELSE (CASE WHEN r.`to_country` IN ('de','uk') THEN 'DE' WHEN r.`to_country` IN ('com','us','com2','com3','ca','mx') THEN 'US'  WHEN r.`to_country`='jp' THEN 'JP' ELSE 'Other' END) END) TYPE,DATE_FORMAT(r.`export_date`,'%Y%m') dates,DATE_FORMAT(r.`export_date`,'%Y%m%d'),r.`declare_no`,r.`transport_no`,r.`box_number`,r.`weight`,r.`volume`,(case when r.`model`='0' then 'AE' when r.`model`='1' then 'OE'  when r.`model`='2' then 'EX'  else 'TR' end) model,(CASE WHEN r.`to_country` IN ('de','uk') THEN 'DE' WHEN r.`to_country` IN ('com','us','com2','com3') THEN 'US' WHEN r.`to_country`='ca' THEN 'CA' WHEN r.`to_country`='mx' THEN 'MX' WHEN r.`to_country`='jp' THEN 'JP' ELSE '' END) country,SUM(ifnull(t.`lower_price`,0)*t.`quantity`),SUM(ifnull(t.`import_price`,0)*t.`quantity`) "+
                   " FROM lc_psi_transport_order r JOIN lc_psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' "+
                   " WHERE r.`export_date`>=:p1 AND r.`export_date`<=:p2  AND r.from_store='130'  AND r.`transport_sta`!='8' AND r.`model`!='4' group by TYPE,dates,r.`declare_no`,r.`transport_no`,r.`box_number`,r.`weight`,r.`volume`,country";
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		List<Object[]> list=psiTransportOrderDao.findBySql(sql,new Parameter(df.parse(year+"-01-01"),df.parse(year+"-12-31")));
		for (Object[] obj : list) {
			String type = obj[0].toString();
			List<Object[]> temp = map.get(type);
			if(temp==null){
				temp = Lists.newArrayList();
				map.put(type,temp);
			}
			temp.add(obj);
		}
		return map;
	}
	
	
	public Map<String,Map<String,Float>> findImportPrice(String year) throws ParseException{
		Map<String,Map<String,Float>> map = Maps.newHashMap();
		String sql="SELECT (CASE WHEN r.`transport_type`='3' THEN ((CASE WHEN r.`to_country` IN ('de','uk') THEN 'Offline_DE' WHEN r.`to_country` IN ('com','us','com2','com3','ca','mx') THEN 'Offline_US'  WHEN r.`to_country`='jp' THEN 'Offline_JP' ELSE 'Offline_Other' END)) ELSE (CASE WHEN r.`to_country` IN ('de','uk') THEN 'DE' WHEN r.`to_country` IN ('com','us','com2','com3','ca','mx') THEN 'US'  WHEN r.`to_country`='jp' THEN 'JP' ELSE 'Other' END) END) TYPE,DATE_FORMAT(r.`export_date`,'%Y%m') dates,SUM(ifnull(t.`import_price`,0)*t.`quantity`) "+
				"	FROM lc_psi_transport_order r  "+
				"	JOIN lc_psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' "+
				"	WHERE r.`export_date`>=:p1 AND r.`export_date`<=:p2  AND r.from_store='130'  AND r.`transport_sta`!='8' AND r.`model`!='4' "+
				"	GROUP BY TYPE,dates ";
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		List<Object[]> list=psiTransportOrderDao.findBySql(sql,new Parameter(df.parse(year+"-01-01"),df.parse(year+"-12-31")));
		for (Object[] obj : list) {
			String type = obj[0].toString();
			Map<String,Float> temp = map.get(type);
			if(temp==null){
				temp = Maps.newHashMap();
				map.put(type,temp);
			}
			temp.put(obj[1].toString(), Float.parseFloat(obj[2].toString()));
		}
		return map;				
	}
	
	public Map<String,Map<String,Float>> findCnPrice(String year) throws ParseException{
		Map<String,Map<String,Float>> map = Maps.newHashMap();
		String sql="SELECT (CASE WHEN r.`transport_type`='3' THEN ((CASE WHEN r.`to_country` IN ('de','uk') THEN 'Offline_DE' WHEN r.`to_country` IN ('com','us','com2','com3','ca','mx') THEN 'Offline_US'  WHEN r.`to_country`='jp' THEN 'Offline_JP' ELSE 'Offline_Other' END)) ELSE (CASE WHEN r.`to_country` IN ('de','uk') THEN 'DE' WHEN r.`to_country` IN ('com','us','com2','com3','ca','mx') THEN 'US'  WHEN r.`to_country`='jp' THEN 'JP' ELSE 'Other' END) END) TYPE,DATE_FORMAT(r.`export_date`,'%Y%m') dates,SUM(ifnull((CASE WHEN r.`transport_no` IN ('20180323_LC_YD033','20180323_LC_YD031','20180323_LC_YD030','20180323_LC_YD031','20180323_LC_YD028') THEN 6.2764  ELSE 1 END)*t.`lower_price`,0)*t.`quantity`)  "+
				"	FROM lc_psi_transport_order r JOIN lc_psi_transport_order_item t ON r.id=t.`transport_order_id` AND t.`del_flag`='0' "+
				"	WHERE r.`export_date`>=:p1 AND r.`export_date`<=:p2  AND r.from_store='130'  AND r.`transport_sta`!='8' AND r.`model`!='4' "+
				"	GROUP BY TYPE,dates ";
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		List<Object[]> list=psiTransportOrderDao.findBySql(sql,new Parameter(df.parse(year+"-01-01"),df.parse(year+"-12-31")));
		for (Object[] obj : list) {
			String type = obj[0].toString();
			Map<String,Float> temp = map.get(type);
			if(temp==null){
				temp = Maps.newHashMap();
				map.put(type,temp);
			}
			temp.put(obj[1].toString(), Float.parseFloat(obj[2].toString()));
		}
		return map;				
	}
	
	
	@Transactional(readOnly = false)
	public void saveRateData(String month,double usdCny,double eurCny,double jpyCny){
		String sql="insert into lc_psi_transport_month_rate(month,usdCny,eurCny,jpyCny) values(:p1,:p2,:p3,:p4)";
		psiTransportOrderDao.updateBySql(sql, new Parameter(month,usdCny,eurCny,jpyCny));
	}
	
	
	public Map<String,Object[]> findRate(String year){
		Map<String,Object[]> map = Maps.newHashMap();
		String sql="select month,usdCny,eurCny,jpyCny from lc_psi_transport_month_rate where month like :p1 ";
		List<Object[]> list=psiTransportOrderDao.findBySql(sql,new Parameter(year+"%"));
		for (Object[] obj : list) {
			map.put(obj[0].toString(), obj);
		}
		return map;
	}
} 
