/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
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
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.dao.PsiTransportOrderDao;
import com.springrain.erp.modules.psi.entity.FbaInbound;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiTransportForecastDto;
import com.springrain.erp.modules.psi.entity.PsiTransportOrder;
import com.springrain.erp.modules.psi.entity.PsiTransportOrderContainer;
import com.springrain.erp.modules.psi.entity.PsiTransportOrderItem;
import com.springrain.erp.modules.psi.entity.PsiTransportPayment;
import com.springrain.erp.modules.psi.entity.PsiTransportPaymentItem;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrder;
import com.springrain.erp.modules.psi.scheduler.PsiConfig;
import com.springrain.erp.modules.sys.dao.GenerateSequenceDao;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 运单表Service
 * @author Michael
 * @version 2015-01-15
 */
@Component
@Transactional(readOnly = true)
public class PsiTransportOrderService extends BaseService {
	@Autowired
	private PsiTransportOrderDao 			    psiTransportOrderDao;
	@Autowired
	private GenerateSequenceDao 			    genDao;
	@Autowired
	private PsiProductService 				    psiProductService;
	@Autowired
	private PsiTransportOrderItemService 	    psiTransportOrderItemService;
	@Autowired
	private PsiTransportOrderContainerService   psiTransportContainerService;
	@Autowired
	private StockService 						stockService;
	@Autowired
	private PsiProductTieredPriceService		productTieredPriceService;
	
	
	public PsiTransportOrder get(Integer id) {
		return psiTransportOrderDao.get(id);
	}
	
	
	public PsiTransportOrder getById(Integer id) {
		String sql="SELECT p.id,p.`suffix_name`,p.`transport_no`,p.`create_date`,p.`to_country`,t.`product_id`,t.`currency`,t.`item_price`,t.`quantity`,t.`pack_quantity`,d.`chinese_name`, d.`gw`, "+
            " d.`brand`,d.`model` pmodel,d.type,d.`pack_length`,d.`pack_width`,d.`pack_height`,d.`box_volume`, "+
			" eu_hscode,ca_hscode,jp_hscode,us_hscode,hk_hscode,p.weight,p.box_number,p.model tmodel "+
			" FROM psi_transport_order p JOIN psi_transport_order_item t ON p.id=t.`transport_order_id` AND t.`del_flag`='0' "+
            " JOIN psi_product d ON d.id=t.`product_id` AND d.del_flag='0' where p.id=:p1 ";
		List<Object[]> list=this.psiTransportOrderDao.findBySql(sql, new Parameter(id));
		PsiTransportOrder order=new PsiTransportOrder();
		List<PsiTransportOrderItem>	 items=Lists.newArrayList();
		for (int i=0;i<list.size();i++) {
			Object[] obj=list.get(i);
			if(i==0){
				order.setId(Integer.parseInt(obj[0].toString()));
				order.setSuffixName(obj[1]==null?null:obj[1].toString());
				order.setTransportNo(obj[2].toString());
				order.setCreateDate((Timestamp)obj[3]);
				order.setToCountry(obj[4]==null?null:obj[4].toString());
			}
			PsiTransportOrderItem item=new PsiTransportOrderItem();
			PsiProduct product=new PsiProduct();
			product.setId(Integer.parseInt(obj[5].toString()));
			item.setCurrency(obj[6]==null?null:obj[6].toString());
			item.setItemPrice(obj[7]==null?0f:Float.parseFloat(obj[7].toString()));
			item.setQuantity(Integer.parseInt(obj[8].toString()));
			item.setPackQuantity(Integer.parseInt(obj[9].toString()));
			product.setChineseName(obj[10]==null?null:obj[10].toString());
			product.setGw((BigDecimal)obj[11]);
			product.setBrand(obj[12].toString());
			product.setModel(obj[13].toString());
			if(product.getModel().startsWith("DL1001")||product.getModel().startsWith("DL1002")||product.getModel().startsWith("DS1001")){
				product.setModel(obj[13].toString().replace("US","").replace("JP","").replace("UK","").replace("EU",""));
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
			order.setWeight(Float.parseFloat(obj[24].toString()));
			order.setBoxNumber(Integer.parseInt(obj[25]==null?"0":obj[25].toString()));
			order.setModel(obj[26].toString());
			item.setProduct(product);
			items.add(item);
		}
		order.setItems(items);
		return order;
	}
	
	public List<Object[]> getCountBySingleProduct(String productId,String startDate,String endDate){
		String sql ="SELECT a.`transport_no`,a.`model`,a.`to_country`, b.`quantity` FROM psi_transport_order AS a,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND a.`transport_sta`<>'8' AND b.`del_flag`='0' AND a.`pick_up_date` BETWEEN :p2 AND :p3 AND b.`product_id`=:p1 ";
		return  this.psiTransportOrderDao.findBySql(sql, new Parameter(productId,startDate,endDate));
	}
	
	public List<Object[]> getCountByAllProduct(String startDate,String endDate){
		String sql ="SELECT a.`transport_no`,a.`model`,a.`to_country`, SUM(b.`quantity`),b.`product_id`,b.`product_name` FROM psi_transport_order AS a,psi_transport_order_item AS b WHERE a.`id`=b.`transport_order_id` AND a.`transport_sta`<>'8' AND b.`del_flag`='0'  AND a.`pick_up_date` BETWEEN :p1 AND :p2  GROUP BY b.`transport_order_id`,b.`product_id` ";
		return  this.psiTransportOrderDao.findBySql(sql, new Parameter(startDate,endDate));
	}
	
	public PsiTransportOrder get(String transportNo) {
		DetachedCriteria dc = this.psiTransportOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("transportNo", transportNo));  
		List<PsiTransportOrder> rs = this.psiTransportOrderDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	
	public PsiTransportOrder getByFbaShipmentNo(String shipmentNo) {
		DetachedCriteria dc = this.psiTransportOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("shipmentId", shipmentNo));
		List<PsiTransportOrder> rs = this.psiTransportOrderDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	
	public PsiTransportOrder getLikeFbaShipmentNo(String shipmentId,Integer fbaId,String orderSta) {
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
		
		List<PsiTransportOrder> rs = this.psiTransportOrderDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	
	public Page<PsiTransportOrder> find(Page<PsiTransportOrder> page, PsiTransportOrder psiTransportOrder) {
		DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(psiTransportOrder.getTransportNo())){
			String content=psiTransportOrder.getTransportNo();
			dc.createAlias("this.items", "item");
			dc.add(Restrictions.or(Restrictions.like("transportNo","%"+content+"%"),
					Restrictions.like("item.productName", "%"+content+"%"),Restrictions.like("item.sku", "%"+content+"%")
					,Restrictions.like("shipmentId", "%"+content+"%"),Restrictions.like("ladingBillNo", "%"+content+"%")));
		}
		if(StringUtils.isNotEmpty(psiTransportOrder.getTransportSta())){
			dc.add(Restrictions.eq("transportSta", psiTransportOrder.getTransportSta()));
		}else{
			dc.add(Restrictions.ne("transportSta", "8"));
		}
		
		if(StringUtils.isNotEmpty(psiTransportOrder.getModel())){
			dc.add(Restrictions.eq("model", psiTransportOrder.getModel()));
		}
		
		if(psiTransportOrder.getBoxNumber()!=null&&psiTransportOrder.getBoxNumber()>0){
			dc.add(Restrictions.eq("boxNumber", psiTransportOrder.getBoxNumber()));
		}
		
		
		if(StringUtils.isNotEmpty(psiTransportOrder.getToCountry())){
			if("eu".equals(psiTransportOrder.getToCountry())){
				dc.add(Restrictions.in("toCountry", new String[]{"DE","de"}));
			}else if("com".equals(psiTransportOrder.getToCountry())){
				dc.add(Restrictions.in("toCountry", new String[]{"US","ca","com"}));
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
	
	
	public List<PsiTransportOrder> findByProductId(Integer productId) {
			DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
			dc.createAlias("this.items", "item");
			dc.add(Restrictions.eq("item.product.id", productId));
			dc.add(Restrictions.eq("transportSta", "0"));
		return psiTransportOrderDao.find(dc);
	}
	
	public List<PsiTransportOrder> findByUnlineOrderId(Integer unlineOrderId) {
		DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("unlineOrder", unlineOrderId));
		dc.add(Restrictions.eq("transportSta", "0"));
		return psiTransportOrderDao.find(dc);
	}
	
	public List<PsiTransportOrder> exp(PsiTransportOrder psiTransportOrder) {
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
		
		return psiTransportOrderDao.find(dc);
	}
	
	public List<PsiTransportOrder> expNew(PsiTransportOrder psiTransportOrder) {
		DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(psiTransportOrder.getTransportNo())){
			dc.add(Restrictions.or(Restrictions.like("transportNo","%"+psiTransportOrder.getTransportNo()+"%"),Restrictions.like("shipmentId", "%"+psiTransportOrder.getShipmentId()+"%")));
		}
		if(StringUtils.isNotEmpty(psiTransportOrder.getTransportSta())){
			dc.add(Restrictions.eq("transportSta", psiTransportOrder.getTransportSta()));
		}else{
			dc.add(Restrictions.ne("transportSta", "8"));
		}
		
		dc.add(Restrictions.ne("model", "1"));
		
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
		
		return psiTransportOrderDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void addSaveData(PsiTransportOrder psiTransportOrder) throws IOException {
		this.clearSupplierData(psiTransportOrder);
		
		Float volume =0f;
		Float weight=0f;
		Set<Integer> productIds = Sets.newHashSet();
		for(PsiTransportOrderItem item:psiTransportOrder.getItems()){
			productIds.add(item.getProduct().getId());
		}
		
		//算出产品重量体积
		Map<Integer,String>	volumeWeightMap=this.psiProductService.getVomueAndWeight(productIds);
				
		for(PsiTransportOrderItem item:psiTransportOrder.getItems()){
			item.setTransportOrder(psiTransportOrder);
			Integer productId = item.getProduct().getId();
			volume+=item.getQuantity()/(float)item.getPackQuantity()*(Float.parseFloat(volumeWeightMap.get(productId).split(",")[0]));
			weight+=item.getQuantity()/(float)item.getPackQuantity()*(Float.parseFloat(volumeWeightMap.get(productId).split(",")[1]));
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
			psiTransportOrder.setToCountry(psiTransportOrder.getDestinationDetail());
		}
		
		psiTransportOrderDao.save(psiTransportOrder);
	}
	
	@Transactional(readOnly = false)
	public String createFlowNo() throws IOException {
		return this.genDao.genSequenceByMonth("_YD",3);
	}
	
	@Transactional(readOnly = false)
	public void editSaveData(PsiTransportOrder psiTransportOrder,String filePath,MultipartFile[] localFile,MultipartFile[] tranFile,MultipartFile[] dapFile,MultipartFile[] otherFile,MultipartFile[] otherFile1,MultipartFile[] insuranceFile,MultipartFile[] taxFile) throws IOException {
		Set<Integer>  delItemSet = Sets.newHashSet();
		Set<Integer>  delConertainItemSet =Sets.newHashSet();
		this.clearSupplierData(psiTransportOrder);
		//保存附件
		this.saveAttachment(psiTransportOrder, filePath, localFile, tranFile, dapFile, otherFile,otherFile1, insuranceFile, taxFile);
		
		Set<String> setNewIds = new HashSet<String>();
		Set<String> setConertainIds = new HashSet<String>();
		for(PsiTransportOrderItem item:psiTransportOrder.getItems()){
			if(item.getId()!=null){
				setNewIds.add(item.getId().toString());
			}
			item.setTransportOrder(psiTransportOrder);
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
				for (Iterator<PsiTransportOrderContainer> iterator = psiTransportOrder.getContainerItems().iterator(); iterator.hasNext();) {
					PsiTransportOrderContainer item = (PsiTransportOrderContainer) iterator.next();
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
			psiTransportOrder.setToCountry(psiTransportOrder.getDestinationDetail());
		}
				
		
		if(delItemSet.size()>0){
			for(PsiTransportOrderItem item:this.psiTransportOrderItemService.getTransportOrderItems(delItemSet)){
				item.setDelFlag("1");
				item.setTransportOrder(psiTransportOrder);
				psiTransportOrder.getItems().add(item);
			};
		}
		
		if(delConertainItemSet.size()>0){
			for(PsiTransportOrderContainer item:this.psiTransportContainerService.getTransportContainerItems(delConertainItemSet)){
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
	public void editSaveData(PsiTransportOrder psiTransportOrder) throws IOException {
		Set<Integer>  delItemSet = Sets.newHashSet();
		Set<Integer>  delConertainItemSet =Sets.newHashSet();
		
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
		Set<Integer> productIdSet =Sets.newHashSet();
		//保存货物清单信息
		for(PsiTransportOrderItem item:psiTransportOrder.getItems()){
			if(item.getId()!=null){
				setNewIds.add(item.getId().toString());
			}
			productIdSet.add(item.getProduct().getId());
			item.setTransportOrder(psiTransportOrder);
		}
		
		
		if("0".equals(psiTransportOrder.getTransportSta())){
			//如果已出库之前都可以改
			Map<Integer,String> volumeWeightMap=this.psiProductService.getVomueAndWeight(productIdSet);
			for(PsiTransportOrderItem item:psiTransportOrder.getItems()){
				String volumeWeight=volumeWeightMap.get(item.getProduct().getId());
				Float rate= item.getQuantity()/Float.parseFloat(volumeWeight.split(",")[2]);
				Float itemVolue= Float.parseFloat(volumeWeight.split(",")[0])*rate;
				Float itemWeight= Float.parseFloat(volumeWeight.split(",")[1])*rate;
				volume+=itemVolue;
				weight+=itemWeight;
			}
			psiTransportOrder.setWeight(weight);
			psiTransportOrder.setVolume(volume);
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
				for(PsiTransportOrderContainer item:psiTransportOrder.getContainerItems()){
					if(item.getId()!=null){
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
			for(PsiTransportOrderItem item:this.psiTransportOrderItemService.getTransportOrderItems(delItemSet)){
				//删除fba相应的sku
				item.setDelFlag("1");
				item.setTransportOrder(psiTransportOrder);
				psiTransportOrder.getItems().add(item);
			};
		}
		
		if(delConertainItemSet.size()>0){
			for(PsiTransportOrderContainer item:this.psiTransportContainerService.getTransportContainerItems(delConertainItemSet)){
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
		String updateSql ="UPDATE psi_transport_order  SET transport_sta=:p1 WHERE id=:p2";
		psiTransportOrderDao.updateBySql(updateSql, new Parameter(cancelSta,orderId));
	}

	/**
	 *查找要付款的运单   草稿和申请
	 */
	public List<PsiTransportOrder> findUnDonePayment(Integer supplierId) {
		DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
		
		dc.add(Restrictions.ne("transportSta", "8"));
		dc.add(Restrictions.in("paymentSta", new String[]{"0","1"}));
		dc.add(Restrictions.or(Restrictions.eq("vendor1.id", supplierId),Restrictions.eq("vendor2.id", supplierId),Restrictions.eq("vendor3.id", supplierId),Restrictions.eq("vendor4.id", supplierId),Restrictions.eq("vendor5.id", supplierId),Restrictions.eq("vendor6.id", supplierId)));
		dc.addOrder(Order.desc("id"));
		return this.psiTransportOrderDao.find(dc);
	}
	
	/**
	 *查找所有新建状态的运单
	 */
	public List<PsiTransportOrder> findTranOrderBySta(Integer fromStoreId,String orderSta) {
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
		List<PsiTransportOrder> orders =this.psiTransportOrderDao.find(dc);
		Integer quantity = 0;
		for(PsiTransportOrder order :orders){
			for(PsiTransportOrderItem item:order.getItems()){
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
		List<PsiTransportOrder> orders =this.psiTransportOrderDao.find(dc);
		Map<String,Integer>  map = Maps.newHashMap();
		for(PsiTransportOrder order :orders){
			for(PsiTransportOrderItem item:order.getItems()){
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
		String sql="SELECT i.`sku`,SUM(i.`quantity`) AS quantity FROM psi_transport_order AS a ,psi_transport_order_item AS i WHERE a.`id`=i.`transport_order_id` AND a.transport_sta in ('1','2','3') AND a.to_store=:p1 GROUP BY i.sku ";
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
	public List<PsiTransportOrder> findInventoryInTranOrder(String[] orderStas,Integer storeId,String[] transportTypeStas) {
		DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
		dc.add(Restrictions.eq("toStore.id",storeId));
		dc.add(Restrictions.in("transportSta", orderStas));
		dc.add(Restrictions.in("transportType", transportTypeStas));
		dc.addOrder(Order.asc("id"));
		return this.psiTransportOrderDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void save(PsiTransportOrder psiTransportOrder) {
		this.psiTransportOrderDao.save(psiTransportOrder);
	}
	
	
	@Transactional(readOnly = false)
	public void saveList(List<PsiTransportOrder> psiTransportOrders) {
		this.psiTransportOrderDao.save(psiTransportOrders);
	}
	
	@Transactional(readOnly = false)
	public void merge(PsiTransportOrder psiTransportOrder) {
		this.psiTransportOrderDao.getSession().merge(psiTransportOrder);
	}
	
	@Transactional(readOnly = false)
	public String updateLadingBillNo(Integer tranId,String ladingBillNo){
		String sql ="update  psi_transport_order set lading_bill_no=:p1 where id=:p2";
		int i =this.psiTransportOrderDao.updateBySql(sql, new Parameter(ladingBillNo,tranId));
		if(i>0){
			return "true";
		}else{
			return "false";
		}
	}
	
	@Transactional(readOnly = false)
	public String updateEtaDate(Integer tranId,Date etaDate){
		String sql ="update  psi_transport_order set eta_date=:p1 where id=:p2";
		int i =this.psiTransportOrderDao.updateBySql(sql, new Parameter(etaDate,tranId));
		if(i>0){
			return "true";
		}else{
			return "false";
		}
	}
	
	@Transactional(readOnly = false)
	public String updateOperArrivalDate(Integer tranId,Date operArrivalDate){
		String sql ="update  psi_transport_order set oper_arrival_date=:p1 where id=:p2";
		int i =this.psiTransportOrderDao.updateBySql(sql, new Parameter(operArrivalDate,tranId));
		if(i>0){
			return "true";
		}else{
			return "false";
		}
	}
	
	/**
	 *根据运单No,获取fbaId 
	 */
	public String getFbaId(String tranNo){
		String sql ="SELECT fba_inbound_id FROM "+(tranNo.contains("_LC_")?"lc_psi_transport_order":"psi_transport_order")+" AS a WHERE a.`transport_no`=:p1 ";
		List<Object> list = psiTransportOrderDao.findBySql(sql, new Parameter(tranNo));
		if (list != null && list.size()>0 && list.get(0)!=null) {
			return list.get(0).toString();
		}
		return null;
	}
	
	
	/**
	 *根据运单No,跟新fbaId 
	 */
	@Transactional(readOnly = false)
	public void updateFbaId(String tranNo,String fbaId){
		String sql ="UPDATE  "+(tranNo.contains("_LC_")?"lc_psi_transport_order":"psi_transport_order")+" AS a SET a.`fba_inbound_id`=:p1 WHERE a.`transport_no`=:p2  ";
		this.psiTransportOrderDao.updateBySql(sql, new Parameter(fbaId,tranNo));
	}
	
	@Transactional(readOnly = false)
	public String updateModel(Integer tranId,String model){
		String sql ="update  psi_transport_order set model=:p1 where id=:p2";
		int i =this.psiTransportOrderDao.updateBySql(sql, new Parameter(model,tranId));
		if(i>0){
			return "true";
		}else{
			return "false";
		}
	}
	
	
	//查询目的港list
	public List<String> getPOD(){
		String sql ="SELECT a.`destination` FROM psi_transport_order AS a WHERE a.`transport_sta`<>'8' GROUP BY a.`destination`";
		List<String> list=this.psiTransportOrderDao.findBySql(sql);
		return list;
	}

	//更新费用附件
	
	public void saveAttachment(PsiTransportOrder psiTransportOrder,String filePath,MultipartFile[] localFile,MultipartFile[] tranFile,MultipartFile[] dapFile,MultipartFile[] otherFile,MultipartFile[] otherFile1,MultipartFile[] insuranceFile,MultipartFile[] taxFile){
		//判断附件
		if(localFile[0].getSize()>0){
			psiTransportOrder.setLocalPath(null);//如果编辑上传了附件就把原来的清空
			for (MultipartFile attchmentFile : localFile) {
				if(attchmentFile.getSize()!=0){
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport";
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
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport";
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
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport";
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
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport";
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
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport";
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
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport";
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
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiTransport";
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
	public void updateSuffixName(Integer id,String suffixName){
		this.psiTransportOrderDao.updateSuffixName(id,suffixName);
		
	}

	@Transactional(readOnly = false)
	public void updateElsePath(Integer id,String elsePath){
		this.psiTransportOrderDao.updateElsePath(id,elsePath);
	}


	public void clearSupplierData(PsiTransportOrder psiTransportOrder){
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
	
	public Map<String,String> getSupplierCostPath(Map<String,List<PsiTransportPaymentItem>> costMap,PsiTransportPayment psiTransportPayment){
		Map<String,String> fileMap =Maps.newLinkedHashMap();
		StringBuffer sbPath= new StringBuffer("");
			String sql="SELECT a.`local_path`,a.`tran_path`,a.`dap_path`,a.`other_path`,a.`insurance_path`,a.`tax_path`,a.`other_path1`,a.`transport_no` FROM psi_transport_order AS a WHERE a.`transport_no` in :p1";
			List<Object[]> tranInfos=this.psiTransportOrderDao.findBySql(sql,new Parameter(costMap.keySet()));
			if(tranInfos.size()>0){
				for(Object[] objs:tranInfos){
					String tranNo=objs[7].toString();
					List<PsiTransportPaymentItem> list =costMap.get(tranNo);
					for(PsiTransportPaymentItem item:list){
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
		this.psiTransportOrderDao.updateBySql("update psi_transport_order_item set del_flag='1' where id in :p1", parameter);
	}
	
	/**
	 * 删除海运集装箱items
	 * 
	 */
	@Transactional(readOnly = false)
	public void deleteConertainItems(Set<Integer> itemIds) {
		Parameter parameter =new Parameter(itemIds);
		this.psiTransportOrderDao.updateBySql("update psi_transport_order_container set del_flag='1' where id in :p1", parameter);
	}
	

	/**
	 *获取未发货的，已匹配的shippmentId 
	 */
	public List<String> getMutiShippmentIds(){
		String sql="SELECT a.`shipment_id` FROM psi_transport_order AS a WHERE a.`transport_type`='1' AND a.`transport_sta`='0' AND a.`shipment_id` LIKE '%,%'";
		return  this.psiTransportOrderDao.findBySql(sql);
	}
	
	
	
	/**
	 * 根据fbaId，更新shipmentId
	 */
	@Transactional(readOnly = false)
	public void updateShipmentIdByFbaId(String shipmentId,Integer fbaId) {
		String sql="SELECT a.`id`,a.`shipment_id`  FROM psi_transport_order AS a WHERE FIND_IN_SET(:p1,a.`fba_inbound_id`) ";
		List<Object[]> list = this.psiTransportOrderDao.findBySql(sql,new Parameter(fbaId));
		if(list!=null&&list.size()>0){
			Object[] obj= list.get(0);
			Integer transportOrderId = Integer.parseInt(obj[0].toString());
			String tempShipmentId = shipmentId;
			if(obj[1]!=null&&StringUtils.isNotEmpty(obj[1].toString())){
				if(!obj[1].toString().contains(shipmentId)){
					tempShipmentId=obj[1].toString()+","+tempShipmentId;
					this.psiTransportOrderDao.updateBySql("UPDATE psi_transport_order AS a SET a.`shipment_id`=:p1 WHERE a.`id`=:p2", new Parameter(tempShipmentId,transportOrderId));
				}
			}else{
				this.psiTransportOrderDao.updateBySql("UPDATE psi_transport_order AS a SET a.`shipment_id`=:p1 WHERE a.`id`=:p2", new Parameter(tempShipmentId,transportOrderId));
			}
			
		}
		
	}
	
	/**
	 * 更新confirmPay
	 */
	@Transactional(readOnly = false)
	public void updateConfirmPay(String confirmPay,Integer id) {
		Parameter parameter =new Parameter(id,confirmPay);
		this.psiTransportOrderDao.updateBySql(" UPDATE psi_transport_order AS a SET a.`confirm_pay`=:p2 WHERE a.`id`=:p1 ", parameter);
	}
	
	/**
	 *根据fba贴更新关联的运单的到货时间，运单状态，操作到达时间 
	 */
	@Transactional(readOnly = false)
	public void updateArrviedDateByFba(FbaInbound fbaInbound){
		String shipmentId =fbaInbound.getShipmentId();
		//查询该shipmentId是否存在   
		String sql ="SELECT a.`id` FROM psi_transport_order AS a WHERE a.`shipment_id` like :p1 AND a.`transport_type`<>'0'";
		List<Integer> ids= this.psiTransportOrderDao.findBySql(sql,new Parameter("%"+shipmentId+"%"));
		if(ids.size()==1){
			Integer transportId=ids.get(0);
			//更新到达时间    运单状态  
			String updateSql="UPDATE psi_transport_order AS a SET a.`arrival_date`=:p1,a.`transport_sta`=:p2,a.`oper_arrival_date`=:p3,a.`oper_arrival_fixed_date`=:p3 WHERE a.`id`=:p4 AND a.`transport_type`<>'0'";
			this.psiTransportOrderDao.updateBySql(updateSql, new Parameter(fbaInbound.getArrivalDate(),"5",new Date(),transportId));
		}else{
			//如果春雨运单没有，查找理诚的运单
			sql ="SELECT a.`id` FROM lc_psi_transport_order AS a WHERE a.`shipment_id` like :p1 AND a.`transport_type`<>'0'";
			ids= this.psiTransportOrderDao.findBySql(sql,new Parameter("%"+shipmentId+"%"));
			if(ids.size()==1){
				Integer transportId=ids.get(0);
				//更新到达时间    运单状态  
				String updateSql="UPDATE lc_psi_transport_order AS a SET a.`arrival_date`=:p1,a.`transport_sta`=:p2,a.`oper_arrival_date`=:p3,a.`oper_arrival_fixed_date`=:p3 WHERE a.`id`=:p4 AND a.`transport_type`<>'0'";
				this.psiTransportOrderDao.updateBySql(updateSql, new Parameter(fbaInbound.getArrivalDate(),"5",new Date(),transportId));
			}
			
		}
	}
	
	
	/**
	 *查找所有新建状态的 运单
	 */
	public List<PsiTransportOrder> findNewTranOrder() {
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
			" FROM psi_transport_order AS a ,psi_transport_order_item AS i "+
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
				" FROM psi_transport_order AS a ,psi_transport_order_item AS i "+
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
		 String sql3 ="select sku,country_code,sum(orderNum),delivery_date,spareday,transport_type FROM (SELECT s.sku,t.`country_code`,SUM(t.`quantity_ordered`-t.`quantity_off_ordered`-(t.`quantity_received`-t.`quantity_off_received`)) AS orderNum,t.`delivery_date`,TO_DAYS(NOW())-TO_DAYS(t.`delivery_date`) spareday,p.`transport_type` "+
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
           " WHERE r.`order_sta` IN ('2','3')  GROUP BY s.sku,t.`country_code`,t.`delivery_date`) t group by sku,country_code,delivery_date,spareday,transport_type ";	

		 
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
		    String type=(obj[4]==null?"1":obj[4].toString());
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
		    if (obj[4] == null) {
				continue;
			}
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
	public Map<String,String> getTranModel(Set<String> tranNos){
		Map<String,String> resMap = Maps.newHashMap();
		String sql="SELECT a.transport_no,a.`model` FROM psi_transport_order AS a WHERE a.`transport_no` IN :p1"   ;   
		List<Object[]> objs = this.psiTransportOrderDao.findBySql(sql, new Parameter(tranNos));
		for(Object[] obj:objs){
			String model ="";
			if("0".equals(obj[1])){
				model="Air";
			}else if("1".equals(obj[1])){
				model="Ocean";
			}else if("2".equals(obj[1])){
				model="Express";
			}
			resMap.put(obj[0].toString(), model);
		}
		
		
		sql="SELECT a.transport_no,a.`model` FROM lc_psi_transport_order AS a WHERE a.`transport_no` IN :p1"   ;   
		List<Object[]> lcOobjs = this.psiTransportOrderDao.findBySql(sql, new Parameter(tranNos));
		for(Object[] obj:lcOobjs){
			String model ="";
			if("0".equals(obj[1])){
				model="Air";
			}else if("1".equals(obj[1])){
				model="Ocean";
			}else if("2".equals(obj[1])){
				model="Express";
			}
			resMap.put(obj[0].toString(), model);
		}
		return resMap;
	}
	
	
	
	/**
	 * 根据fbaid，shippmentId   取消运单绑定
	 */
	@Transactional(readOnly = false)
	public  void cancelTransportBading(Integer fbaInboundId,String shipmentId){
		logger.info("开始取消运单绑定,fbaInboundId:" +fbaInboundId + "\tshipmentId:" + shipmentId);
		if(StringUtils.isNotEmpty(shipmentId)){
			String sql="UPDATE psi_transport_order AS a " +
					" SET a.`shipment_id`=REPLACE(a.`shipment_id`, ',"+shipmentId+"',''), a.`shipment_id`=REPLACE(a.`shipment_id`, '"+shipmentId+"','')," +
					" a.`fba_inbound_id`=REPLACE(a.`fba_inbound_id`,',"+fbaInboundId+"',''), a.`fba_inbound_id`=REPLACE(a.`fba_inbound_id`,'"+fbaInboundId+"','') " +
					" WHERE a.`shipment_id` LIKE '%"+shipmentId+"%'";
			this.psiTransportOrderDao.updateBySql(sql, null);
			sql="UPDATE lc_psi_transport_order AS a SET a.`shipment_id`=REPLACE(a.`shipment_id`, ',"+shipmentId+"', ''),a.`shipment_id`=REPLACE(a.`shipment_id`, '"+shipmentId+"', '')," +
					" a.`fba_inbound_id`=REPLACE(a.`fba_inbound_id`,',"+fbaInboundId+"',''), a.`fba_inbound_id`=REPLACE(a.`fba_inbound_id`,'"+fbaInboundId+"','') "+
					"  WHERE a.`shipment_id` LIKE '%"+shipmentId+"%'";
			this.psiTransportOrderDao.updateBySql(sql, null);
		}else{
			//如果shipmentId为空，也有可能运单绑定了多个帖子。
			String sql=" UPDATE psi_transport_order AS a SET" +
					"  a.`fba_inbound_id`=REPLACE(a.`fba_inbound_id`,',"+fbaInboundId+"',''), a.`fba_inbound_id`=REPLACE(a.`fba_inbound_id`,'"+fbaInboundId+"','') " +
					"  WHERE FIND_IN_SET(:p1,a.`fba_inbound_id`) ";
			this.psiTransportOrderDao.updateBySql(sql, new Parameter(fbaInboundId));
			sql=" UPDATE lc_psi_transport_order AS a SET " +
				" a.`fba_inbound_id`=REPLACE(a.`fba_inbound_id`,',"+fbaInboundId+"',''), a.`fba_inbound_id`=REPLACE(a.`fba_inbound_id`,'"+fbaInboundId+"','') " +
				" WHERE FIND_IN_SET(:p1,a.`fba_inbound_id`) ";
			this.psiTransportOrderDao.updateBySql(sql, new Parameter(fbaInboundId));
		}
		//解绑对应的item,使之可以重新建贴
		String sql = "UPDATE `psi_transport_order_item` AS a SET a.`fba_flag`='0',a.`fba_inbound_id`=NULL WHERE a.`fba_inbound_id`='"+fbaInboundId+"'";
		this.psiTransportOrderDao.updateBySql(sql, null);
		sql = "UPDATE `lc_psi_transport_order_item` AS a SET a.`fba_flag`='0',a.`fba_inbound_id`=NULL WHERE a.`fba_inbound_id`='"+fbaInboundId+"'";
		this.psiTransportOrderDao.updateBySql(sql, null);
		logger.info("取消运单绑定结束,fbaInboundId:" +fbaInboundId + "\tshipmentId:" + shipmentId);
	}
	
	
	public List<Object[]> getSingleTran(String productNameColor,String model,String toCountry,Integer fromStoreId,String tranType,Date startDate,Date endDate){
		String sql="SELECT a.`transport_no`,a.`from_store`,a.`to_country`,a.`model`,a.`transport_type`, b.`sku`,b.`country_code`,b.`quantity`,b.`remark`,b.`pack_quantity` FROM psi_transport_order AS a ,psi_transport_order_item AS b WHERE a.id=b.`transport_order_id`" +
				" AND a.`transport_sta`!='8' AND b.`del_flag`='0' AND " +
				" (CASE WHEN b.`color_code`='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END)=:p1 "   ;   
		int i=0;
		int j =1;
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
		objs[i]=productNameColor;
		if(StringUtils.isNotEmpty(model)){
			objs[++i]=model;
			sql+=" AND a.`model`=:p"+(i+1);
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
			objs[++i]=countryStr;
			sql+=" AND a.`to_country` in :p"+(i+1);
		}
		
		if(fromStoreId!=null){
			objs[++i]=fromStoreId;
			sql+=" AND a.`from_store` = :p"+(i+1);
		}
		
		if(StringUtils.isNotEmpty(tranType)){
			objs[++i]=tranType;
			sql+=" AND a.`transport_type`=:p"+(i+1);
		}
		if(startDate!=null){
			objs[++i]=startDate;
			sql+=" AND a.`create_date`>=:p"+(i+1);
		}
		if(endDate!=null){
			objs[++i]=endDate;
			sql+=" AND a.`create_date`<=:p"+(i+1);
		}
		
		sql+=" ORDER BY a.`create_date` DESC ";
		
		return this.psiTransportOrderDao.findBySql(sql, new Parameter(objs));
	}
	

	public Map<String,Map<String,Integer>> findTotalTranQuantity(PsiTransportOrder psiTransportOrder){
		Map<String,Map<String,Integer>>  map=Maps.newHashMap();
		String sql="SELECT CONCAT(t.`product_name`,CASE WHEN t.`color_code`!='' THEN CONCAT ('_',t.`color_code`) ELSE '' END) NAME,LOWER(o.to_country),SUM(t.`quantity`) FROM psi_transport_order o "+
        " JOIN psi_transport_order_item t ON o.id=t.`transport_order_id` AND t.`del_flag`='0' "+
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
	
	public List<PsiTransportOrder> findInStockChanges() {
		DetachedCriteria dc = psiTransportOrderDao.createDetachedCriteria();
		dc.add(Restrictions.isNotNull("changeRecord"));
		List<PsiTransportOrder> list= psiTransportOrderDao.find(dc);
		for (PsiTransportOrder psiTransportOrder : list) {
			Hibernate.initialize(psiTransportOrder.getItems());
		}
		return list;
	}
	
	@Transactional(readOnly = false)
	public void clearChangeRecord() {
		String sql = "UPDATE `psi_transport_order` t SET t.`change_record`=NULL WHERE t.`change_record` IS NOT NULL";
		psiTransportOrderDao.updateBySql(sql, null);
	}
	
	//拆分运单
	@Transactional(readOnly = false)
	public String splitSaveData(PsiTransportOrder psiTransportOrder) throws IOException {
		this.clearSupplierData(psiTransportOrder);
		List<PsiTransportOrderItem> itemList = psiTransportOrder.getItems();
		List<PsiTransportOrderItem> newItems = Lists.newArrayList();
		PsiTransportOrder order = new PsiTransportOrder();	//拆分出的新运单
		int totalBoxNum = 0;	//总箱数
		for (PsiTransportOrderItem item : itemList) {
			if (item.getChdQuantity() > 0) {	//拆单数大于0表示需要拆单
				PsiTransportOrderItem tranOrderItem = new PsiTransportOrderItem();
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
				tranOrderItem.setCurrency(item.getCurrency());
				tranOrderItem.setTransportOrder(order);
				tranOrderItem.setRemark(item.getRemark());
				tranOrderItem.setCnPrice(item.getCnPrice());
				tranOrderItem.setDelFlag("0");
				newItems.add(tranOrderItem);
				totalBoxNum += tranOrderItem.getQuantity()/tranOrderItem.getPackQuantity();
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
		for(PsiTransportOrderItem item:psiTransportOrder.getItems()){
			productIdSet.add(item.getProduct().getId());
		}
		Map<Integer,String> volumeWeightMap = this.psiProductService.getVomueAndWeight(productIdSet);
		
		//修改主运单数量和重量信息
		for(PsiTransportOrderItem item:psiTransportOrder.getItems()){
			String volumeWeight=volumeWeightMap.get(item.getProduct().getId());
			Float rate= item.getQuantity()/Float.parseFloat(volumeWeight.split(",")[2]);
			Float itemVolue= Float.parseFloat(volumeWeight.split(",")[0])*rate;
			Float itemWeight= Float.parseFloat(volumeWeight.split(",")[1])*rate;
			volume+=itemVolue;
			weight+=itemWeight;
			item.setTransportOrder(psiTransportOrder);
		}
		psiTransportOrder.setWeight(weight);
		psiTransportOrder.setVolume(volume);
		
		//计算拆分出的运单数量和重量信息
		volume=0f;	//数据清0重算
		weight=0f;
		for(PsiTransportOrderItem item : newItems){
			String volumeWeight=volumeWeightMap.get(item.getProduct().getId());
			Float rate= item.getQuantity()/Float.parseFloat(volumeWeight.split(",")[2]);
			Float itemVolue= Float.parseFloat(volumeWeight.split(",")[0])*rate;
			Float itemWeight= Float.parseFloat(volumeWeight.split(",")[1])*rate;
			volume+=itemVolue;
			weight+=itemWeight;
		}
		order.setWeight(weight);
		order.setVolume(volume);
		psiTransportOrderDao.save(order);
		this.psiTransportOrderDao.getSession().merge(psiTransportOrder);
		return order.getTransportNo();
	}
	
	/**
	 * 
	 * @param order	目标运单
	 * @param psiTransportOrder	复制源
	 */
	private void copyOrderInfo(PsiTransportOrder order, PsiTransportOrder psiTransportOrder){
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
		String sql="SELECT r.`transport_no`,r.`dap_path`,r.`tran_path`,r.`other_path`,r.`suffix_name`,r.`tax_path` FROM psi_transport_order r WHERE r.`transport_sta`!='8' "+
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
				String taxPath=(obj[5]==null?"":obj[5].toString());
				order.setTransportNo(tranNo);
				order.setDapPath(dapPath);
				order.setTranPath(tranPath);
				order.setOtherPath(otherPath);
				order.setSuffixName(suffixName);
				order.setTaxPath(taxPath);
				orderList.add(order);
			}
		}
		return orderList;
	}
	
	//合并运单
	@Transactional(readOnly = false)
	public void merge(String ids) throws IOException {
		PsiTransportOrder order =null;
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
				for(PsiTransportOrderItem item:order.getItems()){
					skuAndOffMap.put(item.getSku()+","+item.getOfflineSta(),item.getQuantity());
				}
			}else{
				Set<String> skuAndOff = skuAndOffMap.keySet();
				PsiTransportOrder tempOrder = this.psiTransportOrderDao.get(Integer.parseInt(idArr[i]));
				for(PsiTransportOrderItem item:tempOrder.getItems()){
					String key=item.getSku()+","+item.getOfflineSta();
					if(skuAndOff.contains(key)){
						skuAndOffMap.put(key, skuAndOffMap.get(key)+item.getQuantity());
					}else{
						PsiTransportOrderItem tempItem = new PsiTransportOrderItem(order, item.getProduct(), item.getProductName(), item.getColorCode(),
								item.getCountryCode(),item.getQuantity(),item.getShippedQuantity(),item.getReceiveQuantity(),item.getItemPrice(),
								item.getCurrency(),item.getDelFlag(),item.getRemark(),item.getPackQuantity(),item.getSku(),item.getOfflineSta(),
								item.getProductPrice(),item.getCnPrice(),item.getFbaFlag(),item.getFbaInboundId());
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
		for(PsiTransportOrderItem item:order.getItems()){
			if("1".equals(item.getDelFlag())){
				continue;
			}   
			String key=item.getSku()+","+item.getOfflineSta();
			if(skuAndOffMap.containsKey(key)){
				item.setQuantity(skuAndOffMap.get(key));
			}
			
			Integer productId = item.getProduct().getId();
			volume+=item.getQuantity()/(float)item.getPackQuantity()*(Float.parseFloat(volumeWeightMap.get(productId).split(",")[0]));
			weight+=item.getQuantity()/(float)item.getPackQuantity()*(Float.parseFloat(volumeWeightMap.get(productId).split(",")[1]));
			boxNum+=item.getQuantity()%item.getPackQuantity()==0?(item.getQuantity()/item.getPackQuantity()):(item.getQuantity()/item.getPackQuantity()+1);
		}
		order.setVolume(volume);
		order.setWeight(weight);
		order.setBoxNumber(boxNum);
		this.psiTransportOrderDao.save(order);
	}
} 
