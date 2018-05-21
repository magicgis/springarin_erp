package com.springrain.erp.modules.psi.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.cxf.endpoint.Client;
import org.hibernate.Hibernate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
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
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.dao.FbaInboundDao;
import com.springrain.erp.modules.psi.dao.FbaInboundItemDao;
import com.springrain.erp.modules.psi.dao.StockDao;
import com.springrain.erp.modules.psi.entity.FbaInbound;
import com.springrain.erp.modules.psi.entity.FbaInboundItem;
import com.springrain.erp.modules.psi.entity.ProductSalesInfo;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiInventoryOut;
import com.springrain.erp.modules.psi.entity.PsiInventoryOutItem;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiTransportOrder;
import com.springrain.erp.modules.psi.entity.PsiTransportOrderItem;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrderItem;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportOrderService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;

@Component
@Transactional(readOnly = true)
public class FbaInboundService extends BaseService {
	
	
	@Autowired
	private FbaInboundDao 			fbaInboundDao;
	@Autowired
	private StockService 			stockService;
	@Autowired
	private StockDao 				stockDao;
	@Autowired
	private PsiProductService 		productService;
	@Autowired
	private AmazonProduct2Service 	amazonProduct2Service;
	@Autowired
	private PsiInventoryFbaService 	inventoryFbaService;
	@Autowired
	private PsiInventoryService 	inventoryService;
	@Autowired
	private FbaInboundItemDao       fbaInboundItemDao;
	@Autowired
	private PsiTransportOrderService   tranService;
	@Autowired
	private LcPsiTransportOrderService lcTranService;
	@Autowired
	private MailManager			     mailManager;  
	@Autowired
	private ProductSalesInfoService  vanceService;
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	
	public FbaInbound get(Integer id) {
		return (FbaInbound) this.fbaInboundDao.get(id);
	}  
	
	public FbaInbound get(String country, String shipmentId) {
		DetachedCriteria dc = this.fbaInboundDao.createDetachedCriteria(new Criterion[0]);
		dc.add(Restrictions.eq("shipmentId", shipmentId));
		if ("uk".equals(country))
			dc.add(Restrictions.in("country",Lists.newArrayList(new String[] { "fr", "es", "it", "uk","de" })));
		else {
			dc.add(Restrictions.eq("country", country));
		}
		List<FbaInbound> list = this.fbaInboundDao.find(dc);
		if (list.size() == 1) {
			FbaInbound fbaInbound = (FbaInbound) list.get(0);
			for (FbaInboundItem item : fbaInbound.getItems()) {
				Hibernate.initialize(item);
			}
			return fbaInbound;
		}
		return null;
	}
	
	public FbaInbound getByShipmentId(String shipmentId) {
		DetachedCriteria dc = this.fbaInboundDao.createDetachedCriteria();
		dc.add(Restrictions.eq("shipmentId", shipmentId));
		dc.add(Restrictions.ne("shipmentStatus", "DELETED"));
		dc.add(Restrictions.ne("shipmentStatus", "CANCELLED"));
		List<FbaInbound> list = this.fbaInboundDao.find(dc);
		if (list.size() == 1) {
			return list.get(0);
		}
		return null;
	}
	
	public boolean isExistSku(String shipmentId, String sku) {
		if (StringUtils.isEmpty(shipmentId) || StringUtils.isEmpty(sku)) {
			return false;
		}
		String sql="SELECT COUNT(1) FROM `psi_fba_inbound_item` t,`psi_fba_inbound` f WHERE t.`fba_inbound_id`=f.`id` AND f.`shipment_id`=:p1 AND t.`sku`=:p2";
		int num = ((BigInteger)fbaInboundDao.findBySql(sql, new Parameter(shipmentId, sku)).get(0)).intValue();
		if (num > 0) {
			return true;
		}
		return false;
	}
	
	public boolean isExistSku(Integer id, String sku) {
		if (id==null || StringUtils.isEmpty(sku)) {
			return false;
		}
		String sql="SELECT COUNT(1) FROM `psi_fba_inbound_item` t,`psi_fba_inbound` f WHERE t.`fba_inbound_id`=f.`id` AND f.`id`=:p1 AND t.`sku`=:p2";
		int num = ((BigInteger)fbaInboundDao.findBySql(sql, new Parameter(id, sku)).get(0)).intValue();
		if (num > 0) {
			return true;
		}
		return false;
	}

	public Page<FbaInbound> find(Page<FbaInbound> page, FbaInbound fbaInbound) {
		DetachedCriteria dc = this.fbaInboundDao
				.createDetachedCriteria(new Criterion[0]);
		if (StringUtils.isNotEmpty(fbaInbound.getShipmentId())) {
			dc.add(Restrictions.or(Restrictions.like("shipmentId",
					"%" + fbaInbound.getShipmentId() + "%"),Restrictions.like("shipmentName",
							"%" + fbaInbound.getShipmentId() + "%")));
		}
		if (StringUtils.isNotEmpty(fbaInbound.getShipmentName())) {
			List<String> skus = productService.findProductSkusByName(fbaInbound.getShipmentName(), fbaInbound.getCountry());
			if(skus.size()>0){
				dc.createAlias("this.items", "item");
				dc.add(Restrictions.in("item.sku",skus));
			}else{
				return page;
			}
		}
		if(fbaInbound.getTray()!=null&&fbaInbound.getTray().intValue()==0){
			dc.add(Restrictions.or(Restrictions.isNull("fee"),Restrictions.eq("fee",0f)));
		}
		if("0".equals(fbaInbound.getResponseLevel())){
			dc.add(Restrictions.eq("responseLevel", fbaInbound.getResponseLevel()));
		}
		
		if(StringUtils.isNotEmpty(fbaInbound.getCountry())){
			dc.add(Restrictions.eq("country", fbaInbound.getCountry()));
		}
		if ("-1".equals(fbaInbound.getShipmentStatus()))
			dc.add(Restrictions.in(
					"shipmentStatus",
					Lists.newArrayList(new String[] { "WORKING", "SHIPPED",
							"IN_TRANSIT", "DELIVERED", "CHECKED_IN",
							"RECEIVING", "CLOSED", "ERROR", "" })));
		else{
			dc.add(Restrictions.eq("shipmentStatus",
					fbaInbound.getShipmentStatus()));
		}
		dc.add(Restrictions.ge("createDate", fbaInbound.getCreateDate()));
		dc.add(Restrictions.le("createDate", fbaInbound.getLastUpdateDate()));
        if(StringUtils.isNotBlank(fbaInbound.getAccountName())){
        	dc.add(Restrictions.eq("accountName", fbaInbound.getAccountName()));
        }
		return this.fbaInboundDao.find(page, dc);
	}

	public List<FbaInbound> findFba(List<String> countrys,String stockCode,boolean isNotShipped) {
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		DetachedCriteria dc = this.fbaInboundDao.createDetachedCriteria(new Criterion[0]);
		if (countrys!=null&&countrys.size()>0) {
			dc.add(Restrictions.in("country", countrys));
		}
		
		if(isNotShipped){
			dc.add(Restrictions.isNull("shippedDate"));
		}
		
		try {
			dc.add(Restrictions.ge("createDate", sdf.parse("2015-08-01")));
		} catch (ParseException e) {}
		
		dc.add(Restrictions.ne("shipmentStatus", "DELETED"));
		dc.add(Restrictions.isNotNull("shipmentId"));
//		if(StringUtils.isNotEmpty(stockCode)){
//			dc.add(Restrictions.eq("shipFromAddress", stockCode));
//		}
		
		return this.fbaInboundDao.find(dc);
	}
	
	public List<FbaInbound> findNoClosed(String country) {
		DetachedCriteria dc = this.fbaInboundDao.createDetachedCriteria(new Criterion[0]);
		List<String> countrys = Lists.newArrayList(country);
		if("uk,it,es,de,fr".contains(country)){
			countrys.add("de");
			countrys.add("es");
			countrys.add("fr");
			countrys.add("it");
			countrys.add("uk");
		}
		dc.add(Restrictions.in("country", countrys));
		dc.add(Restrictions.ne("shipmentStatus", "DELETED"));
		dc.add(Restrictions.ne("shipmentStatus", "CLOSED"));
		dc.add(Restrictions.ne("shipmentStatus", "CANCELLED"));
		dc.add(Restrictions.isNotNull("shipmentStatus"));
		List<FbaInbound> rs =  this.fbaInboundDao.find(dc);
		for (FbaInbound fbaInbound : rs) {
			Hibernate.initialize(fbaInbound.getItems());
		}
		return rs;
	}
	
	/**
	 * FBA贴P0级响应监控
	 * @param shipFromAddress DE
	 * @param responseLevel 0:p0级  1:p1级
	 * @return
	 */
	public List<FbaInbound> findResponseMonitor(String shipFromAddress, String responseLevel) {
		DetachedCriteria dc = this.fbaInboundDao.createDetachedCriteria(new Criterion[0]);
		dc.add(Restrictions.eq("shipmentStatus", "WORKING"));
		dc.add(Restrictions.eq("shipFromAddress", shipFromAddress));
		dc.add(Restrictions.eq("responseLevel", responseLevel));
		dc.add(Restrictions.isNull("responseTime"));
		List<FbaInbound> rs =  this.fbaInboundDao.find(dc);
		for (FbaInbound fbaInbound : rs) {
			Hibernate.initialize(fbaInbound.getItems());
		}
		return rs;
	}
	
	/**
	 * FBA贴P0级出库监控
	 * @param shipFromAddress DE
	 * @param responseLevel 0:p0级  1:p1级
	 * @return
	 */
	public List<FbaInbound> findShipMonitor(String shipFromAddress, String responseLevel) {
		DetachedCriteria dc = this.fbaInboundDao.createDetachedCriteria(new Criterion[0]);
		dc.add(Restrictions.eq("shipmentStatus", "WORKING"));
		dc.add(Restrictions.eq("shipFromAddress", shipFromAddress));
		dc.add(Restrictions.eq("responseLevel", responseLevel));
		dc.add(Restrictions.isNotNull("responseTime"));
		dc.add(Restrictions.isNull("shippedDate"));
		List<FbaInbound> rs =  this.fbaInboundDao.find(dc);
		for (FbaInbound fbaInbound : rs) {
			Hibernate.initialize(fbaInbound.getItems());
		}
		return rs;
	}
	
	public List<FbaInbound> find(List<Integer> ids) {
		DetachedCriteria dc = this.fbaInboundDao.createDetachedCriteria(new Criterion[0]);
		dc.add(Restrictions.in("id", ids));
		dc.addOrder(Order.asc("id"));
		List<FbaInbound> rs =  this.fbaInboundDao.find(dc);
		for (FbaInbound fbaInbound : rs) {
			Hibernate.initialize(fbaInbound.getItems());
		}
		return rs;
	}
	
	
	public List<FbaInbound> findFbaNoCancel(String country,String[] fbaStas,String shipAddress,boolean isNotShipped,Set<String> shipmentIds) {
		DetachedCriteria dc = this.fbaInboundDao.createDetachedCriteria(new Criterion[0]);
		if (StringUtils.isNotEmpty(country)) {
			dc.add(Restrictions.eq("country", country));
		}
		
		if(fbaStas.length>0){
			dc.add(Restrictions.not(Restrictions.in("shipmentStatus", fbaStas)));
		}
		
		if(isNotShipped){
			dc.add(Restrictions.isNull("shippedDate"));
		}
		
		SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
		try {
			dc.add(Restrictions.ge("createDate",sdf.parse("2015-08-01")));
		} catch (ParseException e) {
		}
		
		if(StringUtils.isNotEmpty(shipAddress)){
			dc.add(Restrictions.eq("shipFromAddress", shipAddress));
		}
		
		dc.add(Restrictions.isNotNull("shipmentId"));
		if(shipmentIds!=null&&shipmentIds.size()>0){
			dc.add(Restrictions.in("shipmentId", shipmentIds));
		}
		return this.fbaInboundDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void save(FbaInbound fbaInbound) {
		this.fbaInboundDao.save(fbaInbound);
	}
	
	@Transactional(readOnly = false)
	public void save(List<FbaInbound> fbaInbounds) {
		this.fbaInboundDao.save(fbaInbounds);
	}
	
	@Transactional(readOnly = false)
	public void saveItemList(List<FbaInboundItem> itemList) {
		this.fbaInboundItemDao.save(itemList);
	}

	public Timestamp findLastUpdateTime(String country,String accountName) {
		String sql = "SELECT MAX(a.`last_update_date`) FROM psi_fba_inbound a  WHERE a.country = :p1 and a.account_name=:p2 AND a.`last_update_by` = '1'";
		List<Object> rs = this.fbaInboundDao.findBySql(sql, new Parameter(country,accountName));
		if (rs.size() == 1) {
			return (Timestamp) rs.get(0);
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public void savePlan(FbaInbound fbaInbound) {
		String country = fbaInbound.getCountry();
		AmazonAccountConfig config=amazonAccountConfigService.getByName(fbaInbound.getAccountName());
		if(config==null){
			logger.error("无法找到对应发货平台配置:"+country);
		}
		String shipToCountryCode = config.getCountryCode();
		String stockCode = "";
		for (FbaInboundItem item : fbaInbound.getItems()) {
			if(StringUtils.isEmpty(stockCode)){
				stockCode = item.getStockCode();
			}
			item.setFbaInbound(fbaInbound);
		}
		if(stockCode==null){
			stockCode = fbaInbound.getDocAddress();
		}
		if(StringUtils.isNotEmpty(fbaInbound.getShipFromAddress())){
			stockCode=fbaInbound.getShipFromAddress();
		}else{
			fbaInbound.setShipFromAddress(stockCode);
		}
		String sql = "SELECT COUNT(1) FROM psi_fba_inbound a WHERE a.`create_date` > CURDATE() AND a.`ship_from_address` = :p1 AND a.`country` = :p2";
		List<Object> list = fbaInboundDao.findBySql(sql,new Parameter(stockCode,config.getCountry()));
		int num = ((BigInteger)list.get(0)).intValue()+1 ;
		String name  = fbaInbound.getShipmentName();
		if(country.startsWith("com")&&!country.equals("com")){
			if(StringUtils.isEmpty(fbaInbound.getShipmentName())){
				String index="[US"+(Integer.parseInt(country.replace("com",""))-1)+"] ";
				name = index+" FBA ("+DateUtils.getDate("dd.M.yyyy")+") New_"+(num)+" From "+stockCode;
				fbaInbound.setShipmentName(name);
			}
		}else{
			if(StringUtils.isEmpty(fbaInbound.getShipmentName())){
				name = "["+shipToCountryCode+"] FBA ("+DateUtils.getDate("dd.M.yyyy")+") New_"+(num)+" From "+stockCode;
				fbaInbound.setShipmentName(name);
			}
		}
		
		fbaInbound.setCreateBy(UserUtils.getUser());
		fbaInbound.setCreateDate(new Date());
		fbaInbound.setLabelPrepType("SELLER_LABEL");
		fbaInboundDao.save(fbaInbound);
	}
	
	
	
	
	@Transactional(readOnly = false)
	public String shippedFbaInBound(PsiInventoryOut inventoryOut) {
		String shipMentIds = inventoryOut.getTranFbaNo();
		String[]ids =  shipMentIds.split(",");
		String rsStr = "";
		List<FbaInbound> fbas = Lists.newArrayList();
		boolean flag = true;
		for (String id : ids) {
			FbaInbound fbaInbound = getByShipmentId(id);
			String oldStatu = fbaInbound.getShipmentStatus();
			//保存pdf凭证
			if(inventoryOut.getPdfFile()!=null){
				fbaInbound.setPdfFile(inventoryOut.getPdfFile());
			}
			if("WORKING".equals(oldStatu)){
				fbaInbound.setShipmentStatus("SHIPPED");
			}
			AmazonAccountConfig config=amazonAccountConfigService.getByName(fbaInbound.getAccountName(),false);
			//进行数据修改
			if(inventoryOut.getWarehouseId().intValue()!=21&&inventoryOut.getWarehouseId().intValue()!=130){
				fbaInbound.setTray(inventoryOut.getPalletQuantity());
				fbaInbound.setDhlTracking(inventoryOut.getTrackBarcode());
				fbaInbound.setDeliveryDate(inventoryOut.getLadingDate());
			    fbaInbound.setSupplier(inventoryOut.getSupplier());
			    if("de".equals(inventoryOut.getWhereabouts())){
			    	fbaInbound.setQuantity1(inventoryOut.getQuantity1());//dpd de 15kg箱数  或者其他物流箱数  
				    fbaInbound.setQuantity2(inventoryOut.getQuantity2());
			    }else{
			    	fbaInbound.setQuantity1(inventoryOut.getQuantity3());
			    }
			    
			    fbaInbound.setFee(inventoryOut.getFee());
			    if(inventoryOut.getTranWeight()==null){
			    	inventoryOut.setTranWeight(0f);
			    }
			    if(inventoryOut.getTranVolume()==null){
			    	inventoryOut.setTranWeight(0f);
			    }
			    fbaInbound.setWeight(new BigDecimal(inventoryOut.getTranWeight()));
			    fbaInbound.setVolume(new BigDecimal(inventoryOut.getTranVolume()));
			}
		    if(ids.length==1){
				Map<String,FbaInboundItem> itemMap = Maps.newHashMap();
				for (FbaInboundItem item : fbaInbound.getItems()) {
					itemMap.put(item.getSku(), item);
				}
				for (PsiInventoryOutItem outItem : inventoryOut.getItems()) {
					String sku = outItem.getSku();
					FbaInboundItem item = itemMap.get(sku);
					if(item ==null){
						fbaInbound.getItems().add(new FbaInboundItem(sku,0,outItem.getQuantity(),fbaInbound));
					}else{
						item.setSellerShipped(item.getQuantityShipped());
						item.setQuantityShipped(outItem.getQuantity());
					}
				}
			}else{
				for (FbaInboundItem item : fbaInbound.getItems()) {
					item.setSellerShipped(item.getQuantityShipped());
					item.setQuantityShipped(item.getQuantityShipped());
				}
			}
			if("WORKING".equals(oldStatu)){
				String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
				Client client = BaseService.getCxfClient(interfaceUrl);
				Object[] str = new Object[]{Global.getConfig("ws.key"),fbaInbound.getId()};
				String rs = "";
				try {
					Object[] res = client.invoke("shippedFbaInBound", str);
					rs = (String)res[0];
				} catch (Exception e) {
					logger.error("shippedFbaInBound",e);
				}
				
				if("Upload To Amz Successful".equals(rs)){
					fbaInbound.setShippedDate(new Date());
					fbaInbound.setProessStatus("出货成功！");
				}else{
					fbaInbound.setShipmentStatus(oldStatu);
					fbaInbound.setProessStatus("出货失败！原因:亚马逊服务不可用或网络异常"+rs);
					flag = false;
					rsStr = "ShippmentId:["+id+"] outBound fail!Reason:Amazon Service is not available or web exception,"+rs;
					break;
				}
			}else{
				fbaInbound.setShippedDate(new Date());
				fbaInbound.setProessStatus("库存出货成功！警告：但因为帖子状态不是WORKING，未同步亚马逊！");
				//rsStr = "帖子["+id+"]出货成功！警告：但因为帖子状态不是WORKING，未同步亚马逊！";
				rsStr = "ShippmentId:["+id+"] outBound success!warning:beause fba shippment status is not WORKING,not synchronous width Amazon!";
			}
			if("DPD".equals(fbaInbound.getSupplier())&&fbaInbound.getTray()!=null){
				/*if("de".equals(fbaInbound.getCountry())){
					fbaInbound.setFee(fbaInbound.getTray()*4.11f);
				}else */
				if("fr".equals(fbaInbound.getCountry())){
					fbaInbound.setFee(fbaInbound.getTray()*9.35f);
				}else if("uk".equals(fbaInbound.getCountry())){
					fbaInbound.setFee(fbaInbound.getTray()*9.35f);
				}else if("it".equals(fbaInbound.getCountry())){
					fbaInbound.setFee(fbaInbound.getTray()*10.39f);
				}else if("es".equals(fbaInbound.getCountry())){
					fbaInbound.setFee(fbaInbound.getTray()*13.51f);
				}
			}
			if("DHL-FREE".equals(fbaInbound.getSupplier())){
				 fbaInbound.setFee(0f);
			}
			fbas.add(fbaInbound);
		}
		if(flag){
			fbaInboundDao.save(fbas);
			if(rsStr.length()==0){
				rsStr = "outBound success!";
			}
		}else{
			for (FbaInbound fbaInbound : fbas) {
				fbaInbound.setShippedDate(null);
				logger.error("查看出库失败，清空ShipDate====shipmentId:"+fbaInbound.getShipmentId());
			}
		}
		return rsStr;
	}

	@Transactional(readOnly = false)
	public void upLoadEmailFlag(String sid, String level) {
		Set<Integer> sids = Sets.newHashSet(); 
		if(sid.contains(",")){
			for (String str : sid.split(",")) {
				sids.add(Integer.parseInt(str));
			}
		}else{
			sids.add(Integer.parseInt(sid));
		}
		String temp = "";
		if(StringUtils.isNotEmpty(level)){
			temp = ",a.response_level='"+level+"' ";
		}
		String sql = "UPDATE psi_fba_inbound a SET a.are_cases_required=(CASE WHEN a.`shipment_status`='WORKING' THEN  '1' when  a.`shipment_status`=''  THEN  '3' ELSE '2' END)"+temp+"  WHERE a.id in :p1";
		fbaInboundDao.updateBySql(sql, new Parameter(sids));
	}
	
	
	
//	@Transactional(readOnly = false)
//	public void upLoadSupplier(String shippmentId,String billNo,String supplier) {
//		String sql = "UPDATE psi_fba_inbound a SET a.dhl_tracking=:p1,supplier=:p2  WHERE a.shipment_id = :p3";
//		fbaInboundDao.updateBySql(sql, new Parameter(billNo,supplier,shippmentId));
//	}


	public Map<String,List<Object[]>> exportFbaInbound(FbaInbound fbaInbound){
		String sql = "SELECT a.`ship_from_address`,a.`shipment_id`,a.`shipment_name`,SUM(c.weight*b.`quantity_shipped`),a.`fee`,a.`shipped_date`,a.`arrival_date`,a.`delivery_date`,a.`volume`,a.`weight`,a.country "+
				"FROM psi_fba_inbound a ,psi_fba_inbound_item b,(SELECT DISTINCT a.`sku`,TRUNCATE((CASE WHEN  b.`volume_ratio`<167  THEN 167*b.`box_volume`/b.`pack_quantity` ELSE b.`gw`/b.`pack_quantity` END),2) AS weight FROM psi_sku a ,psi_product b WHERE a.`product_id` = b.`id` AND a.`del_flag` = '0'"+
				") c WHERE a.`id` = b.`fba_inbound_id` and a.country like :p3 AND b.`sku` = c.sku AND  NOT(a.`shipment_status` IN ('DELETED','CANCELLED')) AND a.`create_date`>=:p1 AND a.`create_date`<=:p2 GROUP BY a.`country`,a.`shipment_id` order by a.`create_date` desc";
		String countryStr = "";
		if(StringUtils.isNotEmpty(fbaInbound.getCountry())){
			countryStr = "%"+fbaInbound.getCountry()+"%";
		}else{
			countryStr = "%%";
		}
		List<Object[]> list = fbaInboundDao.findBySql(sql, new Parameter(fbaInbound.getCreateDate(),fbaInbound.getLastUpdateDate(),countryStr));
		Map<String,List<Object[]>> rs = Maps.newLinkedHashMap();
		for (Object[] objects : list) {
			String from = objects[0].toString();
			String country = objects[10].toString();
			if(!"CN".equals(from)){
				if("com".equals(country)){
					country = "US";
				}else{
					country = country.toUpperCase();
				}
			}else{
				country = from;
			}
			List<Object[]> cList = rs.get(country);
			if(cList==null){
				cList = Lists.newArrayList();
				rs.put(country, cList);
			}
			cList.add(objects);
		}
		return rs;
	}    
	
	public List<Object> hasReceivingProducts(){
		String sql = "SELECT DISTINCT b.`sku` FROM psi_fba_inbound a ,psi_fba_inbound_item b WHERE a.`id` = b.`fba_inbound_id` AND  a.`shipment_status` NOT IN ('DELETED','CANCELLED','WORKING','ERROR','SHIPPED','CLOSED') and a.`shipment_status`!='' AND a.`country` IN ('de','fr','uk','com2','jp')";
		List<Object> list = fbaInboundDao.findBySql(sql,null);
		return list;
	}
	
	public Integer findInventory(String sku){
		String sql = "SELECT  MIN(a.`data_date`) FROM psi_inventory_fba a WHERE a.`sku` = BINARY(:p1)";
		List<Object> minDateList = fbaInboundDao.findBySql(sql,new Parameter(sku));
		boolean isNew = false;
		if(minDateList.size()==1){
			Date minDate = (Date)minDateList.get(0);
			if(minDate!=null){
				isNew = minDate.after(new Date(115, 7, 1));
			}
		}
		if(!isNew){
			sql = "SELECT a.`data_date`,a.`fulfillable_quantity` FROM psi_inventory_fba a WHERE a.`orrect_quantity` IS NULL AND a.`sku` = BINARY(:p1) AND a.`country` IN ('de','fr','uk','com','jp') and (a.`transit_quantity`=0 OR a.`transit_quantity` IS NULL) ORDER BY a.`data_date` DESC";
			List<Object[]> list = fbaInboundDao.findBySql(sql,new Parameter(sku));
			if(list.size()>0){
				sql = "SELECT SUM(CASE WHEN a.`shipment_status` = 'CLOSED' || b.`quantity_received`>b.`quantity_shipped` || (a.country IN ('de','fr','uk','jp') AND a.`arrival_date` IS NOT NULL AND a.`arrival_date` <= DATE_ADD(CURDATE(),INTERVAL -15 DAY)) THEN b.`quantity_received` ELSE b.`quantity_shipped` END) FROM psi_fba_inbound a ,psi_fba_inbound_item b WHERE a.`id` = b.`fba_inbound_id` AND  a.`shipment_status` NOT IN ('DELETED','CANCELLED','WORKING','ERROR') and a.`shipment_status`!='' AND a.`shipment_status` IS NOT NULL AND b.`sku` = :p1  AND (a.`shipped_date` is null or a.`shipped_date` >= :p2 or a.arrival_date>=:p2) and a.`create_date`>'2015-08-01'  AND (b.`quantity_received` >0 OR b.`quantity_shipped`)";
				Date date  = (Date)list.get(0)[0];
				if(sku.toLowerCase().contains("-us")){
					date = DateUtils.addHours(date, -16);
				}else{
					date = DateUtils.addHours(date, -6);
				}
				List<Object> list1 = fbaInboundDao.findBySql(sql,new Parameter(sku,date));
				sql = "SELECT SUM(b.`quantity_ordered`) FROM amazoninfo_order a ,amazoninfo_orderitem b WHERE a.`id` = b.`order_id` AND a.`order_status` IN ('Shipped','Pending') AND a.`purchase_date` > :p1 AND b.`sellersku` = :p2";
				List<Object> list2 = fbaInboundDao.findBySql(sql,new Parameter(date,sku));
				Integer rs = 0 ;
				Integer sales = 0 ;
				if(list2.get(0)!=null){
					sales = Integer.parseInt(list2.get(0).toString());
				}
				int duoqudao = findSkuQuantity(date, sku);
				int recall = findRecallQuantity(date, sku);
				
				rs = Integer.parseInt(list.get(0)[1]==null?"0":list.get(0)[1].toString())+ Integer.parseInt(list1.get(0)==null?"0":list1.get(0).toString())-sales-duoqudao-recall;
				return rs;
			}	
		}	
		//取8/1日
		sql = "SELECT SUM(CASE WHEN a.`shipment_status` = 'CLOSED' || b.`quantity_received`>b.`quantity_shipped` || (a.country IN ('de','fr','uk','jp') AND a.`arrival_date` IS NOT NULL AND a.`arrival_date` <= DATE_ADD(CURDATE(),INTERVAL -15 DAY)) THEN b.`quantity_received` ELSE b.`quantity_shipped` END) FROM psi_fba_inbound a ,psi_fba_inbound_item b WHERE a.`id` = b.`fba_inbound_id` AND  a.`shipment_status` NOT IN ('DELETED','CANCELLED','WORKING','ERROR') and a.`shipment_status`!='' AND a.`shipment_status` IS NOT NULL AND b.`sku` = :p1  AND (a.`shipped_date` is null or a.`shipped_date` >= '2015-08-01' or a.arrival_date>='2015-08-01') and a.`create_date`>'2015-08-01'  AND (b.`quantity_received` >0 OR b.`quantity_shipped`)";
		List<Object> list1 = fbaInboundDao.findBySql(sql,new Parameter(sku));
		sql = "SELECT SUM(b.`quantity_ordered`) FROM amazoninfo_order a ,amazoninfo_orderitem b WHERE a.`id` = b.`order_id` AND a.`order_status` IN ('Shipped','Pending') AND a.`purchase_date` > '2015-08-01' AND b.`sellersku` = :p1";
		List<Object> list2 = fbaInboundDao.findBySql(sql,new Parameter(sku));
		int duoqudao = findSkuQuantity(new Date(115, 7, 1), sku);
		int recall = findRecallQuantity(new Date(115, 7, 1), sku);
		Integer rs = 0 ;
		Integer sales = 0 ;
		if(list2.get(0)!=null){
			sales = Integer.parseInt(list2.get(0).toString());
		}
		rs = Integer.parseInt(list1.get(0)==null?"0":list1.get(0).toString())-sales-duoqudao-recall;
		return rs;
	}
	
	//查询某sku关联的产品在ERP系统有多个SKU
	public Set<String> findMultipleSku(String country){
		Set<String> rs = Sets.newHashSet();
		String sql = "SELECT DISTINCT CONCAT(t.`product_name`,CASE WHEN t.`color`='' THEN '' ELSE CONCAT('_',t.`color`) END,'_',t.`country`),t.`sku` FROM `psi_sku` t WHERE t.`del_flag`='0'";
		if (StringUtils.isNotEmpty(country)) {
			sql += " AND t.`country`='"+country+"'";
		}
		List<Object[]> list = fbaInboundDao.findBySql(sql);
		Map<String, String> map = Maps.newHashMap();
		for (Object[] obj : list) {
			String str = map.get(obj[0].toString());
			if (StringUtils.isNotEmpty(str)) {
				rs.add(str);
				rs.add(obj[1].toString());
			}
			map.put(obj[0].toString(), obj[1].toString());
		}
		return rs;
	}

	 public Integer findRecallQuantity(Date date,String sku){
		  String sql="SELECT SUM((b.`requested_qty`-b.`cancelled_qty`)) AS num  FROM amazoninfo_removal_order a ,amazoninfo_removal_orderitem b WHERE a.`id` = b.`order_id` AND a.`purchase_date`>=:p2 AND b.`sellersku` = :p1  AND a.`order_status` != 'Cancelled' GROUP BY b.`sellersku`";
		  List<Object> list = fbaInboundDao.findBySql(sql,new Parameter(sku,date));
		  if(list!=null&&list.size()>0&&list.get(0)!=null){
				return Integer.parseInt(list.get(0).toString());
		  }
		  return 0;
	 }
	
	
	//查询完成时间为空的记录
	public Map<Integer,List<FbaInboundItem>> findNotFinish() {
		Date date = new Date(116, 2, 10);	//只处理2016.3.10之后的数据
		Map<Integer,List<FbaInboundItem>> rs = Maps.newHashMap();
		DetachedCriteria dc = fbaInboundItemDao.createDetachedCriteria();
		dc.createAlias("fbaInbound", "fbaInbound");
		dc.add(Restrictions.isNull("fbaInbound.finishDate"));
		dc.add(Restrictions.gt("fbaInbound.createDate", date));
		List<FbaInboundItem> itemList = fbaInboundItemDao.find(dc);
		for (FbaInboundItem fbaInboundItem : itemList) {
			List<FbaInboundItem> list = rs.get(fbaInboundItem.getFbaInbound().getId());
			if (list == null) {
				list = Lists.newArrayList();
				rs.put(fbaInboundItem.getFbaInbound().getId(), list);
			}
			list.add(fbaInboundItem);
		}
		return rs;
	}

	//查询需要从亚马孙抓取错误记录以及预计收货时间的对象[fbaInboundId[item.sku	FbaInboundItem]]
	public Map<Integer,List<FbaInboundItem>> findForCatch(String country) {
		Map<Integer, List<FbaInboundItem>> rs = Maps.newHashMap();
		//只处理2016.3.10开始的数据
		String sql = " SELECT id FROM psi_fba_inbound t WHERE DATE_FORMAT(t.`create_date`,'%Y%m%d')>='20160310' AND "+
				" (t.`target_date` IS NULL OR t.`catch_flag` IS NULL) AND t.`shipment_id` IS NOT NULL AND t.`country`=:p1 AND t.`shipment_status`!='DELETED' AND t.`shipment_status`!='CANCELLED'";
		List<Integer> idList = fbaInboundDao.findBySql(sql, new Parameter(country));
		if (idList != null && idList.size() > 0) {
			DetachedCriteria dc = fbaInboundItemDao.createDetachedCriteria();
			dc.createAlias("fbaInbound", "fbaInbound");
			dc.add(Restrictions.in("fbaInbound.id", idList));
			List<FbaInboundItem> itemList = fbaInboundItemDao.find(dc);
			for (FbaInboundItem fbaInboundItem : itemList) {
				List<FbaInboundItem> list = rs.get(fbaInboundItem.getFbaInbound().getId());
				if (list == null) {
					list = Lists.newArrayList();
					rs.put(fbaInboundItem.getFbaInbound().getId(), list);
				}
				list.add(fbaInboundItem);
			}
		}
		return rs;
	}
	
	//国家-产品-发货数
	public Map<String,Map<String,Integer>> findWorkingFBA(){
		Map<String,Map<String,Integer>> map=Maps.newHashMap();
		String sql="SELECT d.`country`,CONCAT(s.`product_name`,CASE  WHEN s.color='' THEN '' ELSE CONCAT('_',s.color) END ),SUM(t.`quantity_shipped`) "+
			" FROM psi_fba_inbound d JOIN psi_fba_inbound_item t  ON d.id=t.`fba_inbound_id` "+
			" JOIN psi_sku s ON t.`sku`=s.`sku` AND d.`country`=s.`country` AND s.`del_flag`='0' "+ 
			" WHERE (d.`shipment_status`='' OR d.`shipment_status`='WORKING') AND d.`ship_from_address`='DE' "+
			" GROUP BY d.`country`,CONCAT(s.`product_name`,CASE  WHEN s.color='' THEN '' ELSE CONCAT('_',s.color) END ) ";
        List<Object[]> list=fbaInboundDao.findBySql(sql);
        for (Object[] obj: list) {
        	Map<String,Integer> temp=map.get(obj[0].toString());
        	if(temp==null){
        		temp=Maps.newHashMap();
        		map.put(obj[0].toString(),temp);
        	}
        	temp.put(obj[1].toString(),Integer.parseInt(obj[2].toString()));
		}
		return map;
	}
	
	
	
	/**
	 *德国仓未发出的fba贴信息 (总)
	 * key:productNameColor  value:quantity
	 */
	public Map<String,Integer> getUnShippedFbaInbound(){
		Map<String,Integer> map=Maps.newHashMap();
		String sql="SELECT CASE WHEN c.`color` ='' THEN c.`product_name` ELSE CONCAT(c.`product_name`,'_',c.`color`) END nameCode," +
				" SUM(b.`quantity_shipped`) FROM psi_fba_inbound AS a ,psi_fba_inbound_item AS b," +
				"(SELECT DISTINCT a.`sku`,a.`product_name`,a.`color` FROM psi_sku AS a WHERE a.`del_flag`='0' AND a.`product_name` !='Inateck other')  AS c" +
				" WHERE a.id=b.`fba_inbound_id` AND b.sku=c.`sku`  AND a.`ship_from_address`='DE' AND a.`doc_address`='DE' " +
				" AND a.`shipment_status` IN ('','WORKING','SHIPPED') AND a.shipped_date IS NULL AND b.`quantity_shipped`>0  GROUP BY c.`product_name`,c.`color` ";
        List<Object[]> list=fbaInboundDao.findBySql(sql);
        for (Object[] obj: list) {
        	map.put(obj[0].toString(),Integer.parseInt(obj[1].toString()));
		}
		return map;
	}
	
	
	/**
	 *德国仓未发出的fba贴信息 （德国仓库存）
	 * key:productNameColor  value:quantity
	 */
	public Map<String,Integer> getUnShippedFbaByLocal(){
		//String sql="SELECT a.`fba_inbound_id` FROM psi_transport_order AS a  WHERE a.`fba_inbound_id` <> ''  AND a.`create_date`>=SUBDATE(DATE_FORMAT(CURDATE(),'%Y-%m-%d'),INTERVAL 15 DAY)";
		String sql="(SELECT a.`fba_inbound_id` FROM psi_transport_order AS a  WHERE a.`fba_inbound_id` <> '' AND a.`transport_type`='0' AND a.`create_date`>=SUBDATE(DATE_FORMAT(CURDATE(),'%Y-%m-%d'),INTERVAL 115 DAY)) " +
				" UNION (SELECT a.`fba_inbound_id` FROM lc_psi_transport_order AS a  WHERE a.`fba_inbound_id` <> '' AND a.`transport_type`='0' AND a.`create_date`>=SUBDATE(DATE_FORMAT(CURDATE(),'%Y-%m-%d'),INTERVAL 115 DAY))";
		List<String> tempFbaIds = this.fbaInboundDao.findBySql(sql);
		List<Integer> fbaIds = Lists.newArrayList();
		for(String fbaIdStr:tempFbaIds){
			for(String id:fbaIdStr.split(",")){
				fbaIds.add(Integer.parseInt(id));
			}
		}
		Map<String,Integer> map=Maps.newHashMap();
		List<Object[]> list=Lists.newArrayList();
		if(fbaIds!=null&&fbaIds.size()>0){
			sql="SELECT CASE WHEN c.`color` ='' THEN c.`product_name` ELSE CONCAT(c.`product_name`,'_',c.`color`) END nameCode," +
					" SUM(b.`quantity_shipped`) FROM psi_fba_inbound AS a ,psi_fba_inbound_item AS b," +
					" (SELECT DISTINCT a.`sku`,a.`product_name`,a.`color` FROM psi_sku AS a WHERE a.`del_flag`='0' AND a.`product_name` !='Inateck other')  AS c" +
					" WHERE a.id=b.`fba_inbound_id` AND a.id NOT IN :p1 AND b.sku=c.`sku`  AND a.`ship_from_address`='DE' AND a.`doc_address`='DE' " +
					" AND a.`shipment_status` IN ('','WORKING','SHIPPED') AND a.shipped_date IS NULL AND b.`quantity_shipped`>0  GROUP BY c.`product_name`,c.`color` ";
	        list=fbaInboundDao.findBySql(sql,new Parameter(fbaIds));
		}else{
			sql="SELECT CASE WHEN c.`color` ='' THEN c.`product_name` ELSE CONCAT(c.`product_name`,'_',c.`color`) END nameCode," +
					" SUM(b.`quantity_shipped`) FROM psi_fba_inbound AS a ,psi_fba_inbound_item AS b," +
					" (SELECT DISTINCT a.`sku`,a.`product_name`,a.`color` FROM psi_sku AS a WHERE a.`del_flag`='0' AND a.`product_name` !='Inateck other')  AS c" +
					" WHERE a.id=b.`fba_inbound_id`  AND b.sku=c.`sku`  AND a.`ship_from_address`='DE' AND a.`doc_address`='DE' " +
					" AND a.`shipment_status` IN ('','WORKING','SHIPPED') AND a.shipped_date IS NULL AND b.`quantity_shipped`>0  GROUP BY c.`product_name`,c.`color` ";
	        list=fbaInboundDao.findBySql(sql);
		}
		for (Object[] obj: list) {
        	map.put(obj[0].toString(),Integer.parseInt(obj[1].toString()));
		}
		return map;
	}
	
	
	
	/**
	 * 获得fba后端信息
	 */
	public Map<String,List<String>> getTrackInfos(){
		Map<String,List<String>> rs = Maps.newHashMap();
		String sql="SELECT a.id,a.`supplier`,a.`dhl_tracking` FROM psi_fba_inbound AS a  WHERE a.`to_dhl` IS NULL AND a.`country` in ('de','fr','es','it','uk')  " +
				" AND a.`shipment_status` NOT IN ('DELETED','CANCELLED') AND a.`supplier` IN ('DPD','DHL-FREE') AND a.`dhl_tracking` !='' ";
			List<Object[]> list = this.fbaInboundDao.findBySql(sql);
			for(Object[] obj:list){
				String supplierType=obj[1].toString();
				String trackNo = obj[0]+",,"+obj[2];
				List<String> tracks =null;
				if(rs.get(supplierType)==null){
					tracks = Lists.newArrayList();
				}else{
					tracks=rs.get(supplierType);
				}
				tracks.add(trackNo);
				rs.put(supplierType, tracks);
			}
			return rs;
	}
	
	
	/**
	 *跟新到亚马逊的时间 
	 */
	@Transactional(readOnly = false)
	public void upToAmaDate(Integer id,Date date) {
		String sql = "UPDATE psi_fba_inbound a SET a.`to_dhl`=:p1 WHERE a.id=:p2";
		fbaInboundDao.updateBySql(sql, new Parameter(date,id));
	}
	
	/**
	 * 查询最近一周到货的产品 
	 * 去德国本地仓的未收货运单     &  未创建过fba贴
	 * key:运单号       value:产品名，数量
	 * inPara  :transportNo:productNameColor:de/unde:sku:数量
	 * 排除新品（sku=product_color_country）
	 */
	public Map<String,Map<String,Integer>> getLastWeekTransportOrder(Map<String,Map<String,Map<String,Map<String,Integer>>>> deTransportMap){
		Map<String,Map<String,Integer>>  rs=Maps.newHashMap();
		String sql="SELECT a.transport_no,CASE WHEN b.`color_code`='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END AS productNameColor," +
				" (IFNULL(b.`shipped_quantity`,b.`quantity`)-IFNULL(b.`receive_quantity`,0)) AS totalNums,b.sku,b.country_code FROM psi_transport_order AS a," +
				" psi_transport_order_item AS b WHERE a.id=b.`transport_order_id` AND a.`transport_sta` NOT IN('5','8')  AND a.`transport_type`='0' AND b.`del_flag`='0' AND a.`to_store`='19' AND (a.`fba_inbound_id` ='' OR a.`fba_inbound_id` IS NULL) " +
				" AND b.`offline_sta`='0' AND a.`oper_arrival_date`  BETWEEN SUBDATE(CURDATE(),DATE_FORMAT(CURDATE(),'%w')-6) AND ADDDATE(SUBDATE(CURDATE(),DATE_FORMAT(CURDATE(),'%w')-6),7)" +
				" AND (CASE WHEN b.`color_code`='' THEN CONCAT(b.`product_name`,'_',b.country_code) ELSE CONCAT(b.`product_name`,'_',b.`color_code`,'_',b.country_code) END)<>b.sku " ;
			List<Object[]> list = this.fbaInboundDao.findBySql(sql);
			for(Object[] obj:list){
				String tranNo = obj[0].toString();
				String proColor= obj[1].toString();
				Integer tranQ = Integer.parseInt(obj[2].toString());
				Integer singleTranQ=tranQ;
				String sku = obj[3]!=null?obj[3].toString():"";
				String country=obj[4].toString();
				Map<String,Integer> tranMap = null;
				if(rs.get(tranNo)==null){
					tranMap =Maps.newHashMap();
				}else{
					tranMap =rs.get(tranNo);
				}
				
				if(tranMap.get(proColor)!=null){
					tranQ+=tranMap.get(proColor);
				}
				tranMap.put(proColor, tranQ);
				rs.put(tranNo, tranMap);
				
				if(StringUtils.isNotEmpty(sku)){
					Map<String,Map<String,Map<String,Integer>>> proMap = null;
					if(deTransportMap.get(tranNo)==null){
						proMap = Maps.newHashMap();
					}else{
						proMap = deTransportMap.get(tranNo);
					}
					Map<String,Integer> skuMap = null;
					String countryFlag="de";
					
					if(!"de".equals(country)){
						countryFlag="unDe";
					}
					
					Map<String,Map<String,Integer>> countryMap = null;
					if(proMap.get(proColor)==null){
						countryMap = Maps.newHashMap();
					}else{
						countryMap=proMap.get(proColor);
					}
					
					if(countryMap.get(countryFlag)==null){
						skuMap =Maps.newHashMap();
					}else{
						skuMap = countryMap.get(countryFlag);
					}
					skuMap.put(sku, singleTranQ);
					countryMap.put(countryFlag, skuMap);
					proMap.put(proColor, countryMap);
					
					deTransportMap.put(tranNo, proMap);
				}
			}
			
			
			//查询理诚运单到货信息
			sql="SELECT a.transport_no,CASE WHEN b.`color_code`='' THEN b.`product_name` ELSE CONCAT(b.`product_name`,'_',b.`color_code`) END AS productNameColor, " +
					" (IFNULL(b.`shipped_quantity`,b.`quantity`)-IFNULL(b.`receive_quantity`,0)) AS totalNums,b.sku,b.country_code FROM lc_psi_transport_order AS a, " +
					" lc_psi_transport_order_item AS b WHERE a.id=b.`transport_order_id` AND a.`transport_sta` NOT IN('5','8')  AND a.`transport_type`='0' AND b.`del_flag`='0' AND a.`to_store`='19' AND (a.`fba_inbound_id` ='' OR a.`fba_inbound_id` IS NULL) " +
					" AND b.`offline_sta`='0' AND a.`oper_arrival_date`  BETWEEN SUBDATE(CURDATE(),DATE_FORMAT(CURDATE(),'%w')-6) AND ADDDATE(SUBDATE(CURDATE(),DATE_FORMAT(CURDATE(),'%w')-6),7) " +
					" AND (CASE WHEN b.`color_code`='' THEN CONCAT(b.`product_name`,'_',b.country_code) ELSE CONCAT(b.`product_name`,'_',b.`color_code`,'_',b.country_code) END)<>b.sku " ;
				list = this.fbaInboundDao.findBySql(sql);
				for(Object[] obj:list){
					String tranNo = obj[0].toString();
					String proColor= obj[1].toString();
					Integer tranQ = Integer.parseInt(obj[2].toString());
					Integer singleTranQ=tranQ;
					String sku = obj[3]!=null?obj[3].toString():"";
					String country=obj[4].toString();
					Map<String,Integer> tranMap = null;
					if(rs.get(tranNo)==null){
						tranMap =Maps.newHashMap();
					}else{
						tranMap =rs.get(tranNo);
					}
					
					if(tranMap.get(proColor)!=null){
						tranQ+=tranMap.get(proColor);
					}
					tranMap.put(proColor, tranQ);
					rs.put(tranNo, tranMap);
					
					if(StringUtils.isNotEmpty(sku)){
						Map<String,Map<String,Map<String,Integer>>> proMap = null;
						if(deTransportMap.get(tranNo)==null){
							proMap = Maps.newHashMap();
						}else{
							proMap = deTransportMap.get(tranNo);
						}
						Map<String,Integer> skuMap = null;
						String countryFlag="de";
						
						if(!"de".equals(country)){
							countryFlag="unDe";
						}
						
						Map<String,Map<String,Integer>> countryMap = null;
						if(proMap.get(proColor)==null){
							countryMap = Maps.newHashMap();
						}else{
							countryMap=proMap.get(proColor);
						}
						
						if(countryMap.get(countryFlag)==null){
							skuMap =Maps.newHashMap();
						}else{
							skuMap = countryMap.get(countryFlag);
						}
						skuMap.put(sku, singleTranQ);
						countryMap.put(countryFlag, skuMap);
						proMap.put(proColor, countryMap);
						
						deTransportMap.put(tranNo, proMap);
					}
				}
			
		return rs;
	}
	
	
	/**
	 *不足35天补足45天的量
	 *根据本地库存和未来7天（本周六到下周五）即将到货的运单，结合需要的数，建成需要的fba贴
	 *仅德国仓发完DE FBA
	 */
	@Transactional(readOnly = false)
	public void genFbaInbound(){
		//算出全欧洲fba总库存        和全欧洲日均销   再算出缺口
		Map<String, Integer>  fbaMap        = inventoryFbaService.getEuFbaInventory();
		Map<String,String>    packMap       = productService.getPackNumberByColor();
		Map<String,Set<String>> deInventorySkuMap = Maps.newHashMap();
		Map<String,Map<String,Integer>>   inventoryMap  = inventoryService.getEuInventory(deInventorySkuMap);
		Map<String,Integer>   unShippedMap  = this.getUnShippedFbaInbound();
		Map<String,Integer>   unShippedLocalMap =this.getUnShippedFbaByLocal();
		//根据可售天算出缺口
		Map<String,Integer> gapMap = Maps.newHashMap();
		Map<String,Integer>   safeMap = Maps.newHashMap();
		Map<String,Integer>   sale31Map = Maps.newHashMap();
		List<ProductSalesInfo> vanList=vanceService.findByCountry("eu");
		
		List<String> componentsList=productService.findComponents();
		
		for(ProductSalesInfo vanInfo:vanList){
			if(vanInfo.getVariance()>0){
				Integer safeQ = (int)(vanInfo.getVariance()*vanInfo.getPeriodSqrt()*2.33);
				safeMap.put(vanInfo.getProductName(), safeQ);	
				sale31Map.put(vanInfo.getProductName(), vanInfo.getDay31Sales());
			}
		}
		
//		for(String productName:fbaMap.keySet()){
		for(Map.Entry<String, Integer> entry:fbaMap.entrySet()){// 有可能fba里有，但产品里面删除了
			String productName = entry.getKey();
			if(packMap.get(productName)==null){
				continue;
			}
			if(componentsList.contains(productName)){
				continue;
			}
			Integer packNum = Integer.parseInt(packMap.get(productName).split(",")[1]);
			Integer total = entry.getValue();
			if(safeMap.get(productName)!=null&&safeMap.get(productName)>0){
				total-=safeMap.get(productName);
			}
			Integer sale31 = sale31Map.get(productName);
			if(sale31!=null&&sale31!=0){
				//如果有计划状态的fba贴，total+
				if(unShippedMap.get(productName)!=null){
					total+=unShippedMap.get(productName);
				}
				//根据总库存和31日销，算出可售天
				Integer canSaleDay = new BigDecimal(total*31f/sale31).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
				if(total>0&&canSaleDay<35){
					//如果可售天小于35，补充到45天的量
					Integer gap=(45-canSaleDay)*sale31/31;
					//配合装箱数,不满一箱算一箱
					if(gap%packNum!=0){
						gap=(gap/packNum+1)*packNum;
					}
					gapMap.put(productName, gap);
				}else{
					gapMap.put(productName, 0);
				}
			}
		}
		
		//库存生成fba贴信息
		Map<String,Integer>  byInventoryData = Maps.newHashMap(); 
		//运单生成fba贴信息 [transport[productName (总数,使用数)]]
		Map<String,Map<String,String>>  byTransportData = Maps.newHashMap(); 
		
		//查询德国仓数据（eu）
		for(Map.Entry<String, Integer> entry:gapMap.entrySet()){
			String productName = entry.getKey();
			Integer gapQuantity=entry.getValue();
			if(inventoryMap.get(productName)!=null){
				Integer inventoryQ = inventoryMap.get(productName).get("euTotal");
				Integer unShippedQ = unShippedLocalMap.get(productName)!=null?unShippedLocalMap.get(productName):0; //未运走的，依照库存数建的单，库存数减去待发数量为当前可用库存
				if(inventoryQ!=null){
					inventoryQ-=unShippedQ;
					if(inventoryQ>0&&gapQuantity>0){
						//库存够用发所有缺口数量
						Integer deliveryQ =gapQuantity;
						if(inventoryQ<gapQuantity){
							//库存不够用，发走凑整箱的数
							Integer packNum = Integer.parseInt(packMap.get(productName).split(",")[1]);
							deliveryQ=inventoryQ/packNum*packNum;
						}
						gapQuantity=gapQuantity-deliveryQ;
						if(deliveryQ.intValue()>0){
							byInventoryData.put(productName, deliveryQ);
						}
						gapMap.put(productName, gapQuantity);
					}
				}
			}
		}
		
		Map<String,Map<String,Map<String,Map<String,Integer>>>> deTransportMap=Maps.newHashMap();
		//查询一周到货的运单（这周六0点到下周六0点）  理诚、春雨都要
		Map<String,Map<String,Integer>>  transportMap= getLastWeekTransportOrder(deTransportMap);
		for(Map.Entry<String,Map<String,Integer>> entry:transportMap.entrySet()){
			String transportNo = entry.getKey();
			Map<String,String> fbaTranMap = Maps.newHashMap();
			Map<String,Integer> tranMap = entry.getValue();
			for(Map.Entry<String,Integer> entry1:tranMap.entrySet()){
				String productName = entry1.getKey();
				Integer tranQ =entry1.getValue();
				Integer gapQ =gapMap.get(productName);
				Integer fbaQ=0;
				if(gapQ!=null&&gapQ>0){
					//有缺口并且缺口剩余数>0
					Integer surplusQ=tranQ-gapQ;
					if(surplusQ>=0){
						//本票可以满足缺口
						fbaQ=gapQ;
					}else{
						//本票不够用,凑个装箱数
						Integer packNum = Integer.parseInt(packMap.get(productName).split(",")[1]);
						fbaQ=tranQ/packNum*packNum;
					}
					gapMap.put(productName, gapQ-fbaQ);
				}
				fbaTranMap.put(productName, tranQ+",,"+fbaQ);
			}
			byTransportData.put(transportNo, fbaTranMap);
		}
		
		//根据库存和下周运单数据生成fba贴
//		Map<String,String> skuInfoMap=this.productService.getAllBandingSku2();
		List<FbaInbound>  transportInbounds = this.createByTransport(byTransportData,deTransportMap,"de","DE");
		FbaInbound inventoryInbound =this.createByInventory(byInventoryData,deInventorySkuMap,inventoryMap,"de","DE");
		if(inventoryInbound.getItems()!=null&&inventoryInbound.getItems().size()>0){
			transportInbounds.add(inventoryInbound);
		}
		//校验单贴总箱数(大于200的需要拆分)
		Map<String, String> skuMap = findSkuNames();
		transportInbounds = validateBoxNum(transportInbounds, packMap, skuMap, 200);
		
		//整理邮件发信通知销售，自动建Fba贴成功
		StringBuffer contents= new StringBuffer("");
		List<BigInteger> list = getOrderIndex("de", "DE");
		int num = list.get(0).intValue() ;
		String fbaName="";
		for(FbaInbound inbound:transportInbounds){
			try{
				String name= "["+"DE"+"] FBA ("+DateUtils.getDate("dd.M.yyyy")+") New_"+(++num)+" From "+"DE";
				fbaName+="("+name+")";
				inbound.setShipmentName(name);
				inbound.setAccountName("Inateck_DE");
				//更新运单信息
				this.save(inbound);
				//把fba帖id放入运单里面
				String tranNo="";
				if(StringUtils.isNotEmpty(inbound.getTransportNo())){
					tranNo="&nbsp;&nbsp;&nbsp;&nbsp;运单号："+inbound.getTransportNo()+"";
					//根据运单号包含(_LC_)判断是春雨还是理诚,(关联运单,限制箱数拆分后可能多个FBA贴对应同一个运单)
					String ids = tranService.getFbaId(inbound.getTransportNo());
					if (StringUtils.isEmpty(ids)) {
						ids = inbound.getId()+"";
					} else {
						ids = ids + "," + inbound.getId();
					}
					tranService.updateFbaId(inbound.getTransportNo(), ids);
					contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
					contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'><td colspan='3' style='text-align:center'>"+inbound.getShipmentName()+tranNo+"</td></tr>");
					contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#f2f4f6;color:#666;'>");
					contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>SKU</th>");
					contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>数量</th>");
					contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>运输转码提示信息</th></tr>");
				}else{
					contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
					contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'><td colspan='3' style='text-align:center'>"+inbound.getShipmentName()+tranNo+"</td></tr>");
					contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#f2f4f6;color:#666;'>");
					contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>SKU</th>");
					contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>数量</th>");
					contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>库存转码提示信息</th></tr>");
				}
				for (FbaInboundItem item: inbound.getItems()) {
					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getSku()+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getQuantityShipped()+"</td>");
				    contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getDescription()+"</td></tr>");
			    }
			    contents.append("</table><br/>");
			}catch(Exception ex){
				logger.error("自动建贴异常", ex);
			}
		}
		
		if(StringUtils.isNotEmpty(contents)){
			//运单未生成fba贴的数量提示：
			for(Map.Entry<String,Map<String,String>> entry1:byTransportData.entrySet()){
				String transportNo = entry1.getKey();
				contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
				contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'><td colspan='4' style='text-align:center'>这周六到下周五预计收货的运单["+transportNo+"]信息</td></tr>");
				contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#f2f4f6;color:#666;'>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>产品名称</th>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>运单数量</th>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>生成fba数</th>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>未生成fba数</th>");
				contents.append("</tr>");
				Map<String,String> tranMap = entry1.getValue();
			    for (Map.Entry<String,String> entry: tranMap.entrySet()) {
			    	String productName  =entry.getKey();
			    	contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+productName+"</td>");
					String[] arr=entry.getValue().split(",,");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+arr[0]+"</td>");
					if(arr[1].contains("(")){
						contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+0+"<span style='color:red'>"+arr[1]+"</span></td>");
						contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+Integer.parseInt(arr[0])+"</td>");
					}else{
						contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+arr[1]+"</td>");
						contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+(Integer.parseInt(arr[0])-Integer.parseInt(arr[1]))+"</td>");
					}
					contents.append("</tr>"); 
			    }
			    contents.append("</table><br/>");
			}
			String toAddress="amazon-sales@inateck.com,leehong@inateck.com";
			this.sendEmail(contents.toString(), "每周四自动生成fba贴"+fbaName+DateUtils.getDate("-yyyy/M/dd"), toAddress);
		}
		
		
	}
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		FbaInboundService fbaInboundService= applicationContext.getBean(FbaInboundService.class);
		PsiProductService productService= applicationContext.getBean(PsiProductService.class);
		Map<String, String> skuMap = fbaInboundService.findSkuNames();
		Map<String, String> packMap = productService.getPackNumberByColor();
		List<FbaInbound> list = fbaInboundService.find(Lists.newArrayList(2580,2581));
		FbaInboundService service = new FbaInboundService();
		list = service.validateBoxNum(list, packMap, skuMap, 200);
		for (FbaInbound fbaInbound : list) {
			System.out.println(fbaInbound.getTotalCarton());
			for (FbaInboundItem item : fbaInbound.getItems()) {
				System.out.println(item.getSku() + "\t" + item.getQuantityShipped());
			}
			System.out.println();
		}
		applicationContext.close();
	}

	//sku对应的产品名称[sku  productname]]]
	public Map<String, String> findSkuNames() {
		Map<String, String> rs = Maps.newHashMap();
		String sql = "SELECT DISTINCT t.`sku`,t.`product_name`,t.`color` FROM psi_sku t WHERE  t.`del_flag`='0'";
        List<Object[]>  list = fbaInboundDao.findBySql(sql, null);
        for (Object[] obj : list) {
			String sku = obj[0].toString();
			String productName = obj[1].toString();
			String color = obj[2]==null?"":obj[2].toString();
			if (StringUtils.isNotEmpty(color)) {
				productName = productName + "_" + color;
			}
			rs.put(sku, productName);
		}
        return rs;
	}
	
	/**
	 * 校验帖子总箱数不能超过maxBox,超过maxBox的自动拆分
	 * @param transportInbounds	校验的帖子集合
	 * @param packMap	产品装箱数
	 * @param skuMap	sku产品映射关系
	 * @param maxBox	单贴总箱数
	 * @return
	 */
	private List<FbaInbound> validateBoxNum(List<FbaInbound> transportInbounds, 
			Map<String,String> packMap, Map<String, String> skuMap, int maxBox) {
		List<FbaInbound> rs = Lists.newArrayList();
		for (FbaInbound fbaInbound : transportInbounds) {
			int totalBox = 0;
			for (FbaInboundItem item : fbaInbound.getItems()) {
				String productName = skuMap.get(item.getSku());
				Integer packNum = 0;
				try {
					packNum = Integer.parseInt(packMap.get(productName).split(",")[1]);
				} catch (Exception e) {
					logger.warn(productName + "获取装箱数失败");
				}
				Integer shippenQty = item.getQuantityShipped();
				if(packNum > 0){
					totalBox += (shippenQty/packNum);
				}
			}
			if (totalBox > maxBox) {	//需要拆分
				rs.addAll(splitFbaInbound(fbaInbound, packMap, skuMap, maxBox));
			} else {
				rs.add(fbaInbound); //没超过maxBox箱不处理
			}
		}
		return rs;
	}

	
	/**
	 * //对超过maxBox箱数的fba贴拆分成多个帖子,同时每个帖子总箱数不能超过maxBox
	 * @param fbaInbound	需要拆分的帖子
	 * @param packMap	产品装箱数
	 * @param skuMap	sku产品映射关系
	 * @param maxBox	单贴总箱数
	 * @return
	 */
	private List<FbaInbound> splitFbaInbound(FbaInbound fbaInbound, Map<String,String> packMap, 
			Map<String, String> skuMap, int maxBox) {
		List<FbaInbound> rs = Lists.newArrayList();
		int i = 0;	//拆分单集合序号标记
		for (FbaInboundItem item : fbaInbound.getItems()) {
			String productName = skuMap.get(item.getSku());
			Integer packNum = 0;
			try {
				packNum = Integer.parseInt(packMap.get(productName).split(",")[1]);
			} catch (Exception e) {
				logger.warn(productName + "获取装箱数失败");
			}
			Integer shippenQty = item.getQuantityShipped();
			int itemBox = 1;
			if (packNum > 0) {
				itemBox = shippenQty/packNum;
			}
			if (itemBox > maxBox) {
				int max = itemBox/maxBox;
				i++; //大于200的话直接生成下一个新帖,剩余部分再处理
				for (int j = 0; j < max; j++) {
					FbaInbound inbound = getSplitFbaInbound(rs, fbaInbound, i);
					FbaInboundItem newItem = new FbaInboundItem();
					newItem.setSku(item.getSku());
					newItem.setQuantityShipped(maxBox * packNum);
					newItem.setDescription(item.getDescription());
					newItem.setFbaInbound(inbound);
					inbound.getItems().add(newItem);
					inbound.setTotalCarton(maxBox);//达到最大箱了
					if(j < max-1){
						i++;	//最后一次不需要移动标记了,下个产品再判断
					}
				}
				itemBox = itemBox - max * maxBox;
				if (itemBox == 0) { //说明刚好是最大箱的倍数,已经处理完毕了
					continue;
				}
			}
			//经过上面处理,剩余箱数itemBox必然小于maxBox
			FbaInbound inbound = getSplitFbaInbound(rs, fbaInbound, i);
			if (inbound.getTotalCarton() + itemBox > maxBox) {	//超标了
				i++;
				FbaInbound newInbound = getSplitFbaInbound(rs, fbaInbound, i);
				FbaInboundItem newItem = new FbaInboundItem();
				newItem.setSku(item.getSku());
				newItem.setQuantityShipped(itemBox * packNum);
				newItem.setDescription(item.getDescription());
				newItem.setFbaInbound(newInbound);
				newInbound.getItems().add(newItem);
				//更新箱数
				newInbound.setTotalCarton(itemBox);
			} else {
				//未超标,可以直接添加进去
				FbaInboundItem newItem = new FbaInboundItem();
				newItem.setSku(item.getSku());
				newItem.setQuantityShipped(itemBox * packNum);
				newItem.setDescription(item.getDescription());
				newItem.setFbaInbound(inbound);
				inbound.getItems().add(newItem);
				//更新箱数
				inbound.setTotalCarton(inbound.getTotalCarton()+itemBox);
			}
		}
		return rs;
	}
	
	private FbaInbound getSplitFbaInbound(List<FbaInbound> rs, FbaInbound fbaInbound, int i){
		FbaInbound inbound = null;
		if (rs.size() == i) {	//需要增加帖子了
			inbound = new FbaInbound();
			inbound.setShipFromAddress(fbaInbound.getShipFromAddress());
			inbound.setShipmentStatus(fbaInbound.getShipmentStatus());
			inbound.setDocAddress(fbaInbound.getDocAddress());
			inbound.setAreCasesRequired(fbaInbound.getAreCasesRequired());
			inbound.setCreateBy(fbaInbound.getCreateBy());
			inbound.setCreateDate(fbaInbound.getCreateDate());
			inbound.setCountry(fbaInbound.getCountry());
			inbound.setHasGenLabel(fbaInbound.getHasGenLabel());
			inbound.setTransportNo(fbaInbound.getTransportNo());
			inbound.setTotalCarton(0); //临时放置总箱数,便于后面比较
			rs.add(inbound);
		} else {
			inbound = rs.get(i);
		}
		return inbound;
	}


	//查找目前一天有几个单
	public List<BigInteger> getOrderIndex(String stockCode,String shipToCountryCode){
		String sql = "SELECT COUNT(1) FROM psi_fba_inbound a WHERE a.`create_date` > CURDATE() AND a.`ship_from_address` = :p1 AND a.`country` = :p2";
		return  fbaInboundDao.findBySql(sql,new Parameter(stockCode,shipToCountryCode));
	}
	
	
	/**
	 *根据库存数据生成fba信息 
	 */
	public FbaInbound createByInventory(Map<String,Integer>  byInventoryData,Map<String,Set<String>> deInventorySkuMap,Map<String,Map<String,Integer>> inventoryMap,String country,String shipFromAddress){
			FbaInbound fbaInbound = new FbaInbound();
			fbaInbound.setCreateBy(new User("1"));
			fbaInbound.setCreateDate(new Date());
			fbaInbound.setCountry(country);
			fbaInbound.setShipFromAddress(shipFromAddress);
			fbaInbound.setDocAddress(shipFromAddress);
			fbaInbound.setShipmentStatus("");
			fbaInbound.setAreCasesRequired("");
			
			for(Map.Entry<String,Integer>productNameColorEntry:byInventoryData.entrySet()){
				String productNameColor = productNameColorEntry.getKey();
				Integer gapQ =productNameColorEntry.getValue();
				String sku="";
				Map<String,Integer> skuMap=inventoryMap.get(productNameColor);
				//如果库存有德码记录，取库存最大的sku
				Set<String> deSkus = deInventorySkuMap.get(productNameColor);
				if(deSkus==null||deSkus.size()==0||skuMap==null){
					//德国库存没记录的不要
					continue;
				}
				
				for(Map.Entry<String, Integer> entry:skuMap.entrySet()){
					String skuKey = entry.getKey();
					if(!"euTotal".equals(skuKey)&&deSkus.contains(skuKey)){
						sku=skuKey;
						break;
					}
				}
				
				StringBuilder tips =new StringBuilder("");
				Integer euTotal=skuMap.get("euTotal");
				Integer skuQ = skuMap.get(sku);
				if(euTotal!=null&&skuQ!=null&&(euTotal.intValue()!=skuQ.intValue()&&skuQ<gapQ)){
					Integer tempQ = gapQ-skuQ;
					//如果欧洲库存和当前sku的库存不同，说明有其他sku，提示转码
//					for(String skuKey:skuMap.keySet()){
					for(Map.Entry<String, Integer> entry:skuMap.entrySet()){
						String skuKey = entry.getKey();
						if(!"euTotal".equals(skuKey)&&!sku.equals(skuKey)){
							tempQ-=entry.getValue();
							if(tempQ.intValue()>0){
								tips.append(skuKey).append("数量：").append(entry.getValue()).append(",    ");
							}else{
								tips.append(skuKey).append("数量：").append(tempQ+entry.getValue()).append(",    ");
								break;
							}
						}
					}
				}
				
				FbaInboundItem item = new FbaInboundItem();
				item.setFbaInbound(fbaInbound);
				item.setQuantityShipped(gapQ);
				item.setSku(sku);
				item.setDescription(tips.toString());
				fbaInbound.getItems().add(item);
			}
		return fbaInbound;
	}
	
	
	/**
	 *根据运单数据生成fba信息 
	 */
	public List<FbaInbound> createByTransport(Map<String,Map<String,String>>  byTransportData,Map<String,Map<String,Map<String,Map<String,Integer>>>> deTransportMap,String country,String shipFromAddress){
			List<FbaInbound> list = Lists.newArrayList();
			Map<String,FbaInbound> accountFba=Maps.newHashMap();
			for(Map.Entry<String,Map<String,String>>transportEntry:byTransportData.entrySet()){
				String transportNo = transportEntry.getKey();
				FbaInbound fbaInbound = new FbaInbound();
				fbaInbound.setCreateBy(new User("1"));
				fbaInbound.setCreateDate(new Date());
				fbaInbound.setCountry(country);
				fbaInbound.setShipFromAddress(shipFromAddress);
				fbaInbound.setDocAddress(shipFromAddress);
				fbaInbound.setShipmentStatus("");
				fbaInbound.setTransportNo(transportNo);
				Map<String,String> transportMap = transportEntry.getValue();
				for(Map.Entry<String,String> productNameColorEntry:transportMap.entrySet()){
					String productNameColor = productNameColorEntry.getKey();
					String sku="";
					//用运单里的德国sku
					if(deTransportMap.get(transportNo)!=null&&deTransportMap.get(transportNo).get(productNameColor)!=null&&deTransportMap.get(transportNo).get(productNameColor).get("de")!=null){
						for(String skuStr :deTransportMap.get(transportNo).get(productNameColor).get("de").keySet()){
							sku=skuStr;//随便拿个德码的sku
							break;
						}
					}
					if(StringUtils.isNotEmpty(sku)){
						StringBuilder desc= new StringBuilder("");
						Integer fbaQ = Integer.parseInt(productNameColorEntry.getValue().split(",,")[1]);
						if(deTransportMap.get(transportNo).get(productNameColor).get("de").get(sku)<fbaQ){
							//如果德国的数量没有不满足缺口
//							for(String sku1 :deTransportMap.get(transportNo).get(productNameColor).get("de").keySet()){
							for(Map.Entry<String,Integer> entry :deTransportMap.get(transportNo).get(productNameColor).get("de").entrySet()){
								String sku1 = entry.getKey();
								desc.append(sku1).append("数量：").append(entry.getValue()).append(",");
							}
							
							if(deTransportMap.get(transportNo).get(productNameColor).get("unDe")!=null){
//								for(String sku1 :deTransportMap.get(transportNo).get(productNameColor).get("unDe").keySet()){
								for(Map.Entry<String,Integer> entry :deTransportMap.get(transportNo).get(productNameColor).get("unDe").entrySet()){
									String sku1 = entry.getKey();
									desc.append(sku1).append("数量：").append(entry.getValue()).append(",");
								}
							}
						}
						if(fbaQ>0){
							FbaInboundItem item = new FbaInboundItem();
							item.setFbaInbound(fbaInbound);
							item.setQuantityShipped(fbaQ);
							item.setSku(sku);
							item.setDescription(desc.toString());
							fbaInbound.getItems().add(item);
						}
					}else{
						//如果运的不是德码的，把生成fba贴数量还原
						String oldQ = transportMap.get(productNameColor).split(",")[0];
						StringBuilder skuInfo= new StringBuilder("");
						if(deTransportMap.get(transportNo).get(productNameColor).get("unDe")!=null){
							for(String sku1 :deTransportMap.get(transportNo).get(productNameColor).get("unDe").keySet()){
								skuInfo.append(sku1).append(";");
							}
						}
						transportMap.put(productNameColor, oldQ+",,("+skuInfo+")");     
					}
					
				}
				if(fbaInbound.getItems().size()>0){
					list.add(fbaInbound);
				}
			}
		return list;
	}
	
	
	
	/**
	 *根据运单生成fba帖子
	 */
	@Transactional(readOnly = false)
	public FbaInbound createByTransportCn(PsiTransportOrder tranOrder,String accountName){
			FbaInbound fbaInbound = new FbaInbound();
			fbaInbound.setAccountName(accountName);
			fbaInbound.setCreateBy(new User("1"));
			fbaInbound.setCreateDate(new Date());
			String cou = tranOrder.getToCountry();
			if("US".equals(cou)){
				cou="com";
			}else if("DE".equals(cou)){
				cou="de";
			}
			fbaInbound.setCountry(cou);
			String docAddress="DE";
			if("jp,com,ca,mx,".contains(fbaInbound.getCountry()+",")){
				docAddress="CN";
			}
			fbaInbound.setShipFromAddress("CN"); //发货地址
			fbaInbound.setDocAddress(docAddress);//账单地址
			fbaInbound.setShipmentStatus("");
			List<BigInteger> list = getOrderIndex(fbaInbound.getDocAddress(),fbaInbound.getCountry());
			int num = list.get(0).intValue() ;
			AmazonAccountConfig config=amazonAccountConfigService.getByName(accountName);
			String name= "["+config.getCountryCode()+"] FBA ("+DateUtils.getDate("dd.M.yyyy")+") New_"+(++num)+" From "+fbaInbound.getShipFromAddress();
			fbaInbound.setShipmentName(name);
			this.save(fbaInbound);
			for(PsiTransportOrderItem tranItem:tranOrder.getItems()){
				if(tranItem.getQuantity()>0&&"0".equals(tranItem.getOfflineSta()) 
						&& "0".equals(tranItem.getFbaFlag())){	//只算未建贴的
					FbaInboundItem item = new FbaInboundItem();
					item.setFbaInbound(fbaInbound);
					item.setQuantityShipped(tranItem.getQuantity());
					item.setSku(tranItem.getSku());
					fbaInbound.getItems().add(item);
					tranItem.setFbaFlag("1");	//标记已建FBA贴
					tranItem.setTransportOrder(tranOrder);
					tranItem.setFbaInboundId(fbaInbound.getId());
				} else {
					tranItem.setTransportOrder(tranOrder);
				}
			}	
			this.save(fbaInbound);
			tranService.merge(tranOrder);
			StringBuffer contents= new StringBuffer("");
			if(fbaInbound.getItems()!=null&&fbaInbound.getItems().size()>0){
				try{
					contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
					contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
					contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>SKU</th>");
					contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>数量</th>");
					contents.append("</tr>");
				    for (FbaInboundItem item: fbaInbound.getItems()) {
						contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
						contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getSku()+"</td>");
						contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getQuantityShipped()+"</td>");
						contents.append("</tr>"); 
				    }
				    contents.append("</table><br/>");
				    if(StringUtils.isNotEmpty(contents)){
						String  toAddress="amazon-sales@inateck.com,tim@inateck.com,"+UserUtils.logistics1;
						this.sendEmail(contents.toString(), "运营部：运单自动生成fba贴,运单"+tranOrder.getTransportNo()+"fba贴["+fbaInbound.getShipmentName()+"]"+DateUtils.getDate("-yyyy/M/dd"), toAddress);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		return fbaInbound;
	}
	
	
	/**
	 *根据运单生成fba帖子
	 */
	@Transactional(readOnly = false)
	public FbaInbound createByTransportCn(LcPsiTransportOrder tranOrder,String accountName){
			FbaInbound fbaInbound = new FbaInbound();
			fbaInbound.setAccountName(accountName);
			fbaInbound.setCreateBy(new User("1"));
			fbaInbound.setCreateDate(new Date());
			String cou = tranOrder.getToCountry();
			if("US".equals(cou)){
				cou="com";
			}else if("DE".equals(cou)){
				cou="de";
			}
			fbaInbound.setCountry(cou);
			String docAddress="DE";
			if("jp,com,ca,mx,".contains(fbaInbound.getCountry()+",")){
				docAddress="CN";
			}
			fbaInbound.setShipFromAddress("CN"); //发货地址
			fbaInbound.setDocAddress(docAddress);//账单地址
			fbaInbound.setShipmentStatus("");
			List<BigInteger> list = getOrderIndex(fbaInbound.getCountry(), fbaInbound.getDocAddress());
			int num = list.get(0).intValue() ;
			AmazonAccountConfig config=amazonAccountConfigService.getByName(accountName,false);
			String name= "["+config.getCountryCode()+"] FBA ("+DateUtils.getDate("dd.M.yyyy")+") New_"+(++num)+" From "+fbaInbound.getShipFromAddress();
			fbaInbound.setShipmentName(name);
			this.save(fbaInbound);
			for(LcPsiTransportOrderItem tranItem:tranOrder.getItems()){
				if(tranItem.getQuantity()>0&&"0".equals(tranItem.getOfflineSta()) 
						&& "0".equals(tranItem.getFbaFlag())){	//只算未建贴的
					FbaInboundItem item = new FbaInboundItem();
					item.setFbaInbound(fbaInbound);
					item.setQuantityShipped(tranItem.getQuantity());
					item.setSku(tranItem.getSku());
					item.setPackQuantity(tranItem.getPackQuantity());
					fbaInbound.getItems().add(item);
					tranItem.setFbaFlag("1");	//标记已建FBA贴
					tranItem.setTransportOrder(tranOrder);
					tranItem.setFbaInboundId(fbaInbound.getId());
				} else {
					tranItem.setTransportOrder(tranOrder);
				}
			}	
			this.save(fbaInbound);
			lcTranService.merge(tranOrder);
			StringBuffer contents= new StringBuffer("");
			if(fbaInbound.getItems()!=null&&fbaInbound.getItems().size()>0){
				try{
					contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
					contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
					contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>SKU</th>");
					contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>数量</th>");
					contents.append("</tr>");
				    for (FbaInboundItem item: fbaInbound.getItems()) {
						contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
						contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getSku()+"</td>");
						contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getQuantityShipped()+"</td>");
						contents.append("</tr>"); 
				    }
				    contents.append("</table><br/>");
				    if(StringUtils.isNotEmpty(contents)){
						String  toAddress="amazon-sales@inateck.com";
						this.sendEmail(contents.toString(), "运单自动生成fba贴,运单"+tranOrder.getTransportNo()+"fba贴["+fbaInbound.getShipmentName()+"]"+DateUtils.getDate("-yyyy/M/dd"), toAddress);
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		return fbaInbound;
	}
	
	public void sendEmail(String contents,String title,String toAddress){
		Date date = new Date();
		final MailInfo mailInfo = new MailInfo(toAddress,title,date);
		mailInfo.setContent(contents);
		new Thread(){
			public void run(){
				mailManager.send(mailInfo);
			}
		}.start();
	}
	
	@Transactional(readOnly = false)
	public void asyTransportFba(FbaInbound inbound){
		try{
//		inbound = this.fbaInboundDao.get(inbound.getId());
		Set<String> tranSkuMap = Sets.newHashSet();
		Set<Integer> productIds = Sets.newHashSet();
		//根据shipmentId或者id，查出运单
		String shipmentId = inbound.getShipmentId();
		PsiTransportOrder order =tranService.getLikeFbaShipmentNo(shipmentId,inbound.getId(),"0");
		LcPsiTransportOrder lcOrder=null;
		if(order==null){
			lcOrder = lcTranService.getLikeFbaShipmentNo(shipmentId,inbound.getId(),"0");
			if(lcOrder!=null){
				for(LcPsiTransportOrderItem item :lcOrder.getItems()){
					tranSkuMap.add(item.getSku()+","+item.getPackQuantity());
					productIds.add(item.getProduct().getId());
				}
			}else{
				return; //都为空就返回
			}
		}else{
			for(PsiTransportOrderItem item :order.getItems()){
				tranSkuMap.add(item.getSku()+","+item.getPackQuantity());
				productIds.add(item.getProduct().getId());
			}
		}
		
		Map<String,Integer> skuMap = Maps.newHashMap();
		Map<String,Integer> skuFbaIdMap = Maps.newHashMap();
		Map<String,Integer> skuPackMap = Maps.newHashMap();
		if(StringUtils.isNotEmpty(inbound.getShipmentId())){
			String shippmentId = "";
			if(order!=null){
				shippmentId=order.getShipmentId();
			}else if(lcOrder!=null){
				shippmentId=lcOrder.getShipmentId();
			}
			//如果没绑定
			if("".equals(shipmentId)){
				return ;
			}
			//遍历多个帖子，组成skumap
			for(String shipId:shippmentId.split(",")){
				if(!shipId.equals(inbound.getShipmentId())){
					inbound=null;
					inbound=this.getByShipmentId(shipId);
				}
				for(FbaInboundItem item:inbound.getItems()){
					String  sku = item.getSku();
					Integer quantity = item.getQuantityShipped();
					if(quantity==null||quantity.intValue()==0){
						continue;
					}
					/*if(skuMap.get(sku)!=null){
						quantity+=skuMap.get(sku);
					}*/
					Integer tempPack=0;
					if(item.getOldPackQuantity()!=null&&item.getOldPackQuantity()>0){
						tempPack=item.getOldPackQuantity();
					}else{
						tempPack=item.getPackQuantity();
					}
					skuMap.put(sku+","+tempPack, quantity);
					skuPackMap.put(sku+","+tempPack, item.getPackQuantity());
					skuFbaIdMap.put(sku+","+tempPack, inbound.getId());
				}
			}
		}else{
			for(FbaInboundItem item:inbound.getItems()){
				String  sku = item.getSku();
				Integer quantity = item.getQuantityShipped();
				if(quantity==null||quantity.intValue()==0){
					continue;
				}
				/*if(skuMap.get(sku)!=null){
					quantity+=skuMap.get(sku);
				}*/
				Integer tempPack=0;
				if(item.getOldPackQuantity()!=null&&item.getOldPackQuantity()>0){
					tempPack=item.getOldPackQuantity();
				}else{
					tempPack=item.getPackQuantity();
				}
				skuMap.put(sku+","+tempPack, quantity);
				skuPackMap.put(sku+","+tempPack, item.getPackQuantity());
				skuFbaIdMap.put(sku+","+tempPack, inbound.getId());
			}
		}
		
		
		
		if(order!=null){
			
			List<PsiInventory> inventorys=this.inventoryService.findByStock(21);
			Map<String,Float> priceMap = Maps.newHashMap();
			Map<String,String> skuInfoMap=Maps.newHashMap();
			for(PsiInventory inventory:inventorys){
				String values=inventory.getProductId()+","+inventory.getProductName()+","+inventory.getCountryCode()+","+inventory.getColorCode();
				String sku = inventory.getSku();
				priceMap.put(sku, inventory.getAvgPrice());
				if(skuInfoMap.get(sku)!=null){
					continue;
				}
				skuInfoMap.put(inventory.getSku(),values);
			}
			Map<String,String>  bangdingSkuMap =this.productService.getBandingProductInfoSku();
			for(String sku:bangdingSkuMap.keySet()){
				if(skuInfoMap.get(sku)!=null){
					continue;
				}
				skuInfoMap.put(sku,skuInfoMap.get(sku));
			}
			
			List<PsiTransportOrderItem> items = Lists.newArrayList();
			for (Iterator<PsiTransportOrderItem> iterator = order.getItems().iterator(); iterator.hasNext();) {
				PsiTransportOrderItem item = (PsiTransportOrderItem) iterator.next();
				String sku =item.getSku();
				Integer packQuantity=item.getPackQuantity();
				String key=sku+","+packQuantity;
				//1如果运单里的sku,fba里面没有则不变，
				if(skuMap.keySet().contains(key)){
					//如果fba里面的sku，运单里有，则更新为
					item.setFbaFlag("1");
					if(item.getFbaInboundId()==null){
						item.setFbaInboundId(skuFbaIdMap.get(key));
					}
					item.setQuantity(skuMap.get(key));
					item.setPackQuantity(skuPackMap.get(key));
				}else{
					item.setFbaFlag("0");
				}
			}
			
			
			for(Map.Entry<String, Integer> entry:skuMap.entrySet()){
				String[] skuAndPack=entry.getKey().split(",");
				String sku = skuAndPack[0];
				if(!tranSkuMap.contains(entry.getKey())&&skuInfoMap.get(sku)!=null){
					Integer quantity = entry.getValue();
					String arr[] = skuInfoMap.get(sku).split(",");
					Integer productId =Integer.parseInt(arr[0]);
					String productName = arr[1];
					String country     = arr[2];
					String color ="";
					if(arr.length>3){
						color=arr[3];
					}
					
					String res =this.tranService.getProPriceByProductId(productId);
					Float   partsPrice =this.tranService.getPartsPriceByProductId(productId, color);
					Float 	price =0f;
					Float 	avgPrice =0f;
					String  currency="";
					Integer packQuantity=0;
					if(priceMap.get(sku)!=null){
						avgPrice=priceMap.get(sku);
					}
					if(StringUtils.isNotEmpty(res)){
						String[] arrr=res.split("_");
						price=Float.parseFloat(arrr[0].toString());
						//查找配件单价
						if("CNY".equals(arrr[1])){//如果是人民币的换成美元
							currency="USD";
							price=partsPrice+(price/AmazonProduct2Service.getRateConfig().get("USD/CNY"));
						}else{
							price=partsPrice+price;
						}
						price=(price/1.17f)*2;
						packQuantity =Integer.parseInt(arrr[2]);
					}
					productIds.add(productId);
					items.add(new PsiTransportOrderItem(order, new PsiProduct(productId), productName, color, country,quantity, null, null, price, currency, "0", null, packQuantity, sku,"0", price, avgPrice,"1",skuFbaIdMap.get(entry.getKey())));
				}
			}
			
			
			if(items.size()>0){
				order.getItems().addAll(items);
			}
			
			//算出产品重量体积
			Map<Integer,String>	volumeWeightMap=this.productService.getVomueAndWeight(productIds);
			//重新计算重量体积
			Float volume =0f;
			Float weight=0f;
			Integer boxNum = 0;
			for(PsiTransportOrderItem item:order.getItems()){
				if("1".equals(item.getDelFlag())){
					continue;
				}   
				
				//如果sku在该运单里面，fba状态更新为1;
				if(skuMap.containsKey(item.getSku())&&"0".equals(item.getFbaFlag())){
					item.setFbaFlag("1");
				}
				
				Integer productId = item.getProduct().getId();
				volume+=item.getQuantity()/(float)item.getPackQuantity()*(Float.parseFloat(volumeWeightMap.get(productId).split(",")[0]));
				weight+=item.getQuantity()/(float)item.getPackQuantity()*(Float.parseFloat(volumeWeightMap.get(productId).split(",")[1]));
				boxNum+=item.getQuantity()%item.getPackQuantity()==0?(item.getQuantity()/item.getPackQuantity()):(item.getQuantity()/item.getPackQuantity()+1);
			}
			order.setVolume(volume);
			order.setWeight(weight);
			order.setBoxNumber(boxNum);
			this.tranService.save(order);
			
		}else if (lcOrder!=null){
			
			List<PsiInventory> inventorys=this.inventoryService.findByStock(130);
			Map<String,Float> priceMap = Maps.newHashMap();
			Map<String,String> skuInfoMap=Maps.newHashMap();
			for(PsiInventory inventory:inventorys){
				String values=inventory.getProductId()+","+inventory.getProductName()+","+inventory.getCountryCode()+","+inventory.getColorCode();
				String sku = inventory.getSku();
				priceMap.put(sku, inventory.getAvgPrice());
				if(skuInfoMap.get(sku)!=null){
					continue;
				}
				skuInfoMap.put(inventory.getSku(),values);
			}
			Map<String,String>  bangdingSkuMap =this.productService.getBandingProductInfoSku();
			for(String sku:bangdingSkuMap.keySet()){
				if(skuInfoMap.get(sku)!=null){
					continue;
				}
				skuInfoMap.put(sku,skuInfoMap.get(sku));
			}
			
			
			List<LcPsiTransportOrderItem> items = Lists.newArrayList();
			for (Iterator<LcPsiTransportOrderItem> iterator = lcOrder.getItems().iterator(); iterator.hasNext();) {
				LcPsiTransportOrderItem item = (LcPsiTransportOrderItem) iterator.next();
				String sku =item.getSku();
				Integer packQuantity=item.getPackQuantity();
				String key=sku+","+packQuantity;
				//1如果运单里的sku,fba里面没有则不变，  
				if(skuMap.keySet().contains(key)){
					item.setFbaFlag("1");
					if(item.getFbaInboundId()==null){
						item.setFbaInboundId(skuFbaIdMap.get(key));
					}
					item.setQuantity(skuMap.get(key));
					item.setPackQuantity(skuPackMap.get(key));
				}else{
					item.setFbaFlag("0");
				}
			}
			
			for(Map.Entry<String, Integer> entry:skuMap.entrySet()){
				String[] skuAndPack=entry.getKey().split(",");
				String sku = skuAndPack[0];
				if(!tranSkuMap.contains(entry.getKey())&&skuInfoMap.get(sku)!=null){
					Integer quantity = entry.getValue();
					String arr[] = skuInfoMap.get(sku).split(",");
					Integer productId =Integer.parseInt(arr[0]);
					String productName = arr[1];
					String country     = arr[2];
					String color ="";
					if(arr.length>3){
						color=arr[3];
					}
					String res =this.lcTranService.getProPriceByProductId(productId);
					Float 	price =0f;
					Float 	avgPrice =0f;
					String  currency="";
					Integer packQuantity=0;
					if(priceMap.get(sku)!=null){
						avgPrice=priceMap.get(sku);
					}
					if(StringUtils.isNotEmpty(res)){
						String[] arrr=res.split("_");
						price=Float.parseFloat(arrr[0].toString());
						price =(price/1.17f)*2;
						
						if(!"CNY".equals(arrr[1])){
							price=price*AmazonProduct2Service.getRateConfig().get("USD/CNY");
						}
						//理诚全部是人民币
						if("it,de,es,fr,uk".contains(country)){
							currency="EUR";
							price=price/AmazonProduct2Service.getRateConfig().get("USD/CNY")/AmazonProduct2Service.getRateConfig().get("EUR/USD");
						}else if("com,mx,ca".contains(country)){
							currency="USD";
							price=price/AmazonProduct2Service.getRateConfig().get("USD/CNY");
						}else if("jp".contains(country)){
							currency="JPY";
							price=price/AmazonProduct2Service.getRateConfig().get("USD/CNY")/AmazonProduct2Service.getRateConfig().get("JPY/USD");
						}
						packQuantity =Integer.parseInt(arrr[2]);
						productIds.add(productId);
					items.add(new LcPsiTransportOrderItem(lcOrder, new PsiProduct(productId), productName, color, country,quantity, null, null, price, currency, "0", null, packQuantity, sku,"0", price, avgPrice,"1",skuFbaIdMap.get(entry.getKey())));
				}
			}
			}
			
			if(items.size()>0){
				lcOrder.getItems().addAll(items);
			}
			
			//算出产品重量体积
			Map<Integer,String>	volumeWeightMap=this.productService.getVomueAndWeight(productIds);
			
			//重新计算重量体积
			Float volume =0f;
			Float weight=0f;
			Integer boxNum = 0;
			for(LcPsiTransportOrderItem item:lcOrder.getItems()){
				if("1".equals(item.getDelFlag())){
					continue;
				}
				
				//如果sku在该运单里面，fba状态更新为1;
				if(skuMap.containsKey(item.getSku())&&"0".equals(item.getFbaFlag())){
					item.setFbaFlag("1");
				}
				
				Integer productId = item.getProduct().getId();
				volume+=item.getQuantity()/(float)item.getPackQuantity()*(Float.parseFloat(volumeWeightMap.get(productId).split(",")[0]));
				weight+=item.getQuantity()/(float)item.getPackQuantity()*(Float.parseFloat(volumeWeightMap.get(productId).split(",")[1]));
				boxNum+=item.getQuantity()%item.getPackQuantity()==0?(item.getQuantity()/item.getPackQuantity()):(item.getQuantity()/item.getPackQuantity()+1);
			}
			lcOrder.setVolume(volume);
			lcOrder.setWeight(weight);
			lcOrder.setBoxNumber(boxNum);
			this.lcTranService.save(lcOrder);
		}
		}catch(Exception ex){
		}
	}
		
	public List<FbaInbound> findNoAmzReferenceId(String country) {
		DetachedCriteria dc = this.fbaInboundDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country", country));
		dc.add(Restrictions.isNull("amzReferenceId"));
		//有效的
		dc.add(Restrictions.in("shipmentStatus",
				Lists.newArrayList(new String[] { "WORKING", "SHIPPED",
						"IN_TRANSIT", "DELIVERED", "CHECKED_IN",
						"RECEIVING", "CLOSED", "ERROR", "" })));
		return fbaInboundDao.find(dc);
	}
	
	public List<FbaInbound> findNoFee(String country) {
		DetachedCriteria dc = this.fbaInboundDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country", country));
		dc.add(Restrictions.isNull("fee"));
		dc.add(Restrictions.eq("supplier", "DHL"));
		//有效的
		dc.add(Restrictions.in("shipmentStatus",
				Lists.newArrayList(new String[] { "WORKING", "SHIPPED",
						"IN_TRANSIT", "DELIVERED", "CHECKED_IN",
						"RECEIVING", "CLOSED", "ERROR", "" })));
		return fbaInboundDao.find(dc);
	}
	
	//理诚拆分创建FBA贴
	@Transactional(readOnly = false)
	public String splitCreateFba(LcPsiTransportOrder psiTransportOrder,String country,String accountName) throws IOException {
		lcTranService.clearSupplierData(psiTransportOrder);
		List<LcPsiTransportOrderItem> itemList = psiTransportOrder.getItems();
		FbaInbound fbaInbound = new FbaInbound();
		fbaInbound.setAccountName(accountName);
		fbaInbound.setCreateBy(new User("1"));
		fbaInbound.setCreateDate(new Date());
		String cou = psiTransportOrder.getToCountry();
		if(StringUtils.isEmpty(country)){
			country=cou;
		}
		fbaInbound.setCountry(country);
		String docAddress="DE";
		if("jp,com,ca,mx,com2".contains(fbaInbound.getCountry()+",")){
			docAddress="CN";
		}
		fbaInbound.setShipFromAddress("CN"); //发货地址
		fbaInbound.setDocAddress(docAddress);//账单地址
		fbaInbound.setShipmentStatus("");
		List<BigInteger> list = getOrderIndex(fbaInbound.getCountry(), fbaInbound.getDocAddress());
		int num = list.get(0).intValue() ;
		AmazonAccountConfig config=amazonAccountConfigService.getByName(accountName,false);
		String name= "["+config.getCountryCode()+"] FBA ("+DateUtils.getDate("dd.M.yyyy")+") New_"+(++num)+" From "+fbaInbound.getShipFromAddress();
		fbaInbound.setShipmentName(name);
		boolean flag = true;	//标记是否选择了产品
		for (LcPsiTransportOrderItem item : itemList) {
			if ("1".equals(item.getIsFba())) {	//本次选择建贴的item
				if (item.getQuantity() > 0 && "0".equals(item.getOfflineSta())) {
					if (flag) {
						this.save(fbaInbound);	//第一个保存数据生成ID
					}
					flag = false;
					FbaInboundItem fbaItem = new FbaInboundItem();
					fbaItem.setFbaInbound(fbaInbound);
					fbaItem.setQuantityShipped(item.getQuantity());
					fbaItem.setSku(item.getSku());
					fbaItem.setPackQuantity(item.getPackQuantity());
					fbaInbound.getItems().add(fbaItem);
					item.setTransportOrder(psiTransportOrder);
					item.setFbaFlag("1");
					item.setFbaInboundId(fbaInbound.getId());
				}
			} else {
				item.setTransportOrder(psiTransportOrder);
			}
		}
		if (flag) {
			return "0";	//没有选择产品
		}
		this.save(fbaInbound);
		if("1".equals(psiTransportOrder.getTransportType())){
			psiTransportOrder.appendFbaId(fbaInbound.getId()+"");
		}
		this.lcTranService.merge(psiTransportOrder);
		//发送邮件通知
		StringBuffer contents= new StringBuffer("");
		if (fbaInbound.getItems() != null && fbaInbound.getItems().size() > 0) {
			try{
				contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
				contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>SKU</th>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>数量</th>");
				contents.append("</tr>");
			    for (FbaInboundItem item: fbaInbound.getItems()) {
					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getSku()+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getQuantityShipped()+"</td>");
					contents.append("</tr>"); 
			    }
			    contents.append("</table><br/>");
			    if(StringUtils.isNotEmpty(contents)){
					String toAddress="amazon-sales@inateck.com";
					sendEmail(contents.toString(), "运单自动生成fba贴,运单"+psiTransportOrder.getTransportNo()+"fba贴["+fbaInbound.getShipmentName()+"]"+DateUtils.getDate("-yyyy/M/dd"), toAddress);
				}
			}catch(Exception ex){
				logger.error("创建FBA贴发送邮件失败", ex);
			}
		}
		return fbaInbound.getShipmentName();
	}
	
	//春雨拆分创建FBA贴
	@Transactional(readOnly = false)
	public String splitCreateFba(PsiTransportOrder psiTransportOrder,String country,String accountName) throws IOException {
		tranService.clearSupplierData(psiTransportOrder);
		List<PsiTransportOrderItem> itemList = psiTransportOrder.getItems();
		FbaInbound fbaInbound = new FbaInbound();
		fbaInbound.setAccountName(accountName);
		fbaInbound.setCreateBy(new User("1"));
		fbaInbound.setCreateDate(new Date());
		String cou = psiTransportOrder.getToCountry();
		if(StringUtils.isEmpty(country)){
			country=cou;
		}
		fbaInbound.setCountry(country);
		String docAddress="DE";
		if("jp,com,ca,mx,".contains(fbaInbound.getCountry()+",")){
			docAddress="CN";
		}
		fbaInbound.setShipFromAddress("CN"); //发货地址
		fbaInbound.setDocAddress(docAddress);//账单地址
		fbaInbound.setShipmentStatus("");
		List<BigInteger> list = getOrderIndex(fbaInbound.getCountry(), fbaInbound.getDocAddress());
		int num = list.get(0).intValue() ;
		AmazonAccountConfig config=amazonAccountConfigService.getByName(accountName);
		String name= "["+config.getCountryCode()+"] FBA ("+DateUtils.getDate("dd.M.yyyy")+") New_"+(++num)+" From "+fbaInbound.getShipFromAddress();
		fbaInbound.setShipmentName(name);
		boolean flag = true;	//标记是否选择了产品
		for (PsiTransportOrderItem item : itemList) {
			if ("1".equals(item.getIsFba())) {	//本次选择建贴的item
				if (item.getQuantity() > 0 && "0".equals(item.getOfflineSta())) {
					if (flag) {
						this.save(fbaInbound);	//第一个保存数据生成ID
					}
					flag = false;
					FbaInboundItem fbaItem = new FbaInboundItem();
					fbaItem.setFbaInbound(fbaInbound);
					fbaItem.setQuantityShipped(item.getQuantity());
					fbaItem.setSku(item.getSku());
					fbaItem.setPackQuantity(item.getPackQuantity());
					fbaInbound.getItems().add(fbaItem);
					item.setTransportOrder(psiTransportOrder);
					item.setFbaFlag("1");
					item.setFbaInboundId(fbaInbound.getId());
				}
			} else {
				item.setTransportOrder(psiTransportOrder);
			}
		}
		if (flag) {
			return "0";	//没有选择产品
		}
		this.save(fbaInbound);
		if("1".equals(psiTransportOrder.getTransportType())){
			psiTransportOrder.appendFbaId(fbaInbound.getId()+"");
		}
		this.tranService.merge(psiTransportOrder);
		//发送邮件通知
		StringBuffer contents= new StringBuffer("");
		if (fbaInbound.getItems() != null && fbaInbound.getItems().size() > 0) {
			try{
				contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
				contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>SKU</th>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>数量</th>");
				contents.append("</tr>");
			    for (FbaInboundItem item: fbaInbound.getItems()) {
					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getSku()+"</td>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getQuantityShipped()+"</td>");
					contents.append("</tr>"); 
			    }
			    contents.append("</table><br/>");
			    if(StringUtils.isNotEmpty(contents)){
					String toAddress="amazon-sales@inateck.com";
					sendEmail(contents.toString(), "运单自动生成fba贴,运单"+psiTransportOrder.getTransportNo()+"fba贴["+fbaInbound.getShipmentName()+"]"+DateUtils.getDate("-yyyy/M/dd"), toAddress);
				}
			}catch(Exception ex){
				logger.error("创建FBA贴发送邮件失败", ex);
			}
		}
		return fbaInbound.getShipmentName();
	}
	
	
	 public Integer findSkuQuantity(Date date,String sku){
		  String sql="SELECT SUM(t.`quantity_ordered`) FROM amazoninfo_outbound_order r JOIN amazoninfo_outbound_orderitem t ON r.`id`=t.`order_id` "+
              " WHERE (seller_order_id NOT LIKE 'MFN-%' OR (r.`create_user`=1)) AND r.order_status ='COMPLETE' AND t.`sellersku`=:p1 AND r.`create_date`>=:p2 ";
		  List<Object> list = fbaInboundDao.findBySql(sql,new Parameter(sku,date));
		  if(list!=null&&list.size()>0&&list.get(0)!=null){
				return Integer.parseInt(list.get(0).toString());
		  }
		  return 0;
	  }
	
	 /**
	  * SKU的重量、体积(大箱体积和重量)
	  * @return
	  */
	 public Map<String, Map<String, Float>> findSkuTranInfo(){
		 Map<String, Map<String, Float>> rs = Maps.newHashMap();
		 String sql="SELECT DISTINCT a.`sku`,b.`gw`,b.`box_volume` "+
				  " FROM psi_sku a ,psi_product b WHERE a.`product_id` = b.`id` AND a.`del_flag` = '0' AND b.`del_flag`='0'";
		 List<Object[]> list = fbaInboundDao.findBySql(sql);
		 for (Object[] obj : list) {
			String sku = obj[0].toString();
			Float gw = Float.parseFloat(obj[1].toString());
			Float boxVolume = Float.parseFloat(obj[2].toString());
			Map<String, Float> map = Maps.newHashMap();
			rs.put(sku, map);
			map.put("gw", gw);
			map.put("volume", boxVolume);
		 }
		 return rs;
	 }
	 
	 
	 /**
	  *查出fba 状态以为shiped。。。但出库时间为空 
	  * "WORKING", "SHIPPED","IN_TRANSIT", "DELIVERED", "CHECKED_IN","RECEIVING", "CLOSED", "ERROR", "DELETED","CLOSED","CANCELLED" })));
	  */
	public Map<String,List<Object[]>> getFbaInboundWarning() {
		Map<String,List<Object[]>> rs = Maps.newHashMap();
		String sql=" SELECT  a.`shipment_id`,a.`shipment_name`,a.`country`,a.`shipment_status` FROM psi_fba_inbound AS a WHERE a.`shipment_status` NOT IN :p1 AND a.`shipped_date` IS  NULL  ";
		List<Object[]> list=this.fbaInboundDao.findBySql(sql,new Parameter(Lists.newArrayList(new String[] { "WORKING", "DELETED", "CANCELLED", "ERROR", "" })));
		Set<String> shipmentIds = Sets.newHashSet(); 
		for(Object[] obj:list){
			shipmentIds.add(obj[0].toString());
		}
		
		if(shipmentIds.size()>0){
			sql="SELECT a.`tran_fba_no` FROM psi_inventory_out AS a WHERE a.`tran_fba_no` IN :p1";
			List<String> shipmenIds =this.fbaInboundDao.findBySql(sql,new Parameter(shipmentIds));
			if(shipmenIds==null||shipmentIds.size()>0){
				rs.put("0", list);//时间为空，也没出库单     发销售
			}else{
				rs.put("1", list);
			}
			return rs;
		}
		
		return null;
	}
	
	
	@Transactional(readOnly = false)
	public void  findErrorFba(){
		String sql="SELECT p.shipment_id FROM psi_fba_inbound p "+
				" WHERE  p.`create_date`>=:p1 and p.`create_date`>='2017-07-25' AND p.`shipment_status` in ('RECEIVING','CLOSED')   "+ 
				" AND p.`ship_from_address` IN ('DE','JP','US')  "+
				" AND (p.`count_flag` IS NULL or p.count_flag='') AND p.fee>0 "+
				" and p.`fee`*(CASE WHEN p.`ship_from_address`='DE' THEN :p2 WHEN p.`ship_from_address`='US' THEN :p3 ELSE :p4 END)/p.`weight`>30 "; 
		List<String>  errorList=fbaInboundDao.findBySql(sql,new Parameter(DateUtils.addMonths(new Date(),-6),AmazonProduct2Service.getRateConfig().get("EUR/USD")*AmazonProduct2Service.getRateConfig().get("USD/CNY"),AmazonProduct2Service.getRateConfig().get("USD/CNY"),AmazonProduct2Service.getRateConfig().get("JPY/CNY")));
		if(errorList!=null&&errorList.size()>0){
			updateErrorCount(errorList);
			String toEmail="eileen";
			StringBuilder cnt=new StringBuilder("FBA运单fee异常：\n\n");
			for (String id: errorList) {
				cnt.append(id).append("\n");
			}
			WeixinSendMsgUtil.sendTextMsgToUser(toEmail,cnt.toString());
		}
		
	}
	
	public List<Object[]>  findFbaTran(){
		String sql="SELECT p.id,p.`ship_from_address`,CONCAT(s.`product_name`,CASE  WHEN s.`color`='' THEN '' ELSE CONCAT('_',s.`color`) END ) NAME,SUM(t.`quantity_shipped`), "+
				"	SUM(t.`quantity_shipped`*d.`gw`/  "+
				"	(CASE WHEN (d.id=217 AND   p.`country` IN ('com','uk','jp','ca','mx')) THEN 60   "+
				"	 WHEN (d.id=217 AND  p.`country` IN ('de','fr','it','es')) THEN 44 WHEN (d.id=218 AND   p.`country` IN ('com','jp','ca','mx')) THEN 32    "+
				"	WHEN (d.id=218 AND   p.`country` IN ('de','fr','it','es','uk')) THEN 24 ELSE d.pack_quantity END)   "+
				"	 )/p.`weight`*p.fee*(case when p.`ship_from_address`='DE' then :p1 when p.`ship_from_address`='US' then :p2 else :p3 end)  pfee,SUM(t.quantity_received),group_concat(t.`sku`)  "+
				"	FROM psi_fba_inbound p  "+
				"	JOIN psi_fba_inbound_item t ON p.id=t.`fba_inbound_id`   "+
				"	JOIN psi_sku s ON p.`country`=s.`country` AND t.`sku`=s.`sku` AND s.`del_flag`='0'  "+
				"	JOIN psi_product d ON s.`product_name`=CONCAT(d.`brand`,' ',d.`model`) AND d.`del_flag`='0'  "+
				"	WHERE p.`create_date`>=:p4 and p.`create_date`>='2017-07-25' AND p.`shipment_status` in ('RECEIVING','CLOSED')   "+
				"	AND p.`ship_from_address` IN ('DE','JP','US') "+
				"	AND (p.`count_flag` IS NULL or p.`count_flag`='') GROUP BY p.`id`,p.`ship_from_address`,NAME";
		Float p1=AmazonProduct2Service.getRateConfig().get("EUR/USD")*AmazonProduct2Service.getRateConfig().get("USD/CNY");
		Float p2=AmazonProduct2Service.getRateConfig().get("USD/CNY");
		Float p3=AmazonProduct2Service.getRateConfig().get("JPY/CNY");
		return fbaInboundDao.findBySql(sql,new Parameter(p1,p2,p3,DateUtils.addMonths(new Date(),-3)));
	}
	
	
	
	public List<Object[]>  findFbaTranItemNoFlag(){
		String sql="SELECT p.id,p.`ship_from_address`,CONCAT(s.`product_name`,CASE  WHEN s.`color`='' THEN '' ELSE CONCAT('_',s.`color`) END ) NAME,SUM(t.`quantity_shipped`), "+
				"	SUM(t.`quantity_shipped`*d.`gw`/  "+
				"	(CASE WHEN (d.id=217 AND   p.`country` IN ('com','uk','jp','ca','mx')) THEN 60   "+
				"	 WHEN (d.id=217 AND  p.`country` IN ('de','fr','it','es')) THEN 44 WHEN (d.id=218 AND   p.`country` IN ('com','jp','ca','mx')) THEN 32    "+
				"	WHEN (d.id=218 AND   p.`country` IN ('de','fr','it','es','uk')) THEN 24 ELSE d.pack_quantity END)   "+
				"	 )/p.`weight`*p.fee*(case when p.`ship_from_address`='DE' then :p1 when p.`ship_from_address`='US' then :p2 else :p3 end)  pfee,SUM(t.quantity_received),group_concat(t.`sku`)  "+
				"	FROM psi_fba_inbound p  "+
				"	JOIN psi_fba_inbound_item t ON p.id=t.`fba_inbound_id`   "+
				"	JOIN psi_sku s ON p.`country`=s.`country` AND t.`sku`=s.`sku` AND s.`del_flag`='0'  "+
				"	JOIN psi_product d ON s.`product_name`=CONCAT(d.`brand`,' ',d.`model`) AND d.`del_flag`='0'  "+
				"	WHERE p.`create_date`>=:p4 and p.`create_date`>='2017-07-25' AND p.`shipment_status` in ('RECEIVING','CLOSED')   "+
				"	AND p.`ship_from_address` IN ('DE','JP','US') "+
				"	AND p.`count_flag`='0' and t.flag='0' GROUP BY p.`id`,p.`ship_from_address`,NAME";
		Float p1=AmazonProduct2Service.getRateConfig().get("EUR/USD")*AmazonProduct2Service.getRateConfig().get("USD/CNY");
		Float p2=AmazonProduct2Service.getRateConfig().get("USD/CNY");
		Float p3=AmazonProduct2Service.getRateConfig().get("JPY/CNY");
		return fbaInboundDao.findBySql(sql,new Parameter(p1,p2,p3,DateUtils.addMonths(new Date(),-3)));
	}
	
	@Transactional(readOnly = false)
	public void  updateCount(Set<String> idSet){
		String sql="update psi_fba_inbound set count_flag='0' where id in :p1 ";
		fbaInboundDao.updateBySql(sql, new Parameter(idSet));
	}
	
	@Transactional(readOnly = false)
	public void  updateItemCount(Map<String,Set<String>> map){
		String sql="update psi_fba_inbound_item set flag='0' where fba_inbound_id = :p1 and sku in :p2 ";
		for (Map.Entry<String,Set<String>> entry: map.entrySet()) {
			fbaInboundDao.updateBySql(sql, new Parameter(entry.getKey(),entry.getValue()));
		}
	}
	
	@Transactional(readOnly = false)
	public void  updateErrorCount(List<String> idSet){
		String sql="update psi_fba_inbound set count_flag='2' where shipment_id in :p1 ";
		fbaInboundDao.updateBySql(sql, new Parameter(idSet));
	}

	//FBA到货异常记录(到货5天收货未达到80%)
	public List<FbaInbound> getFbaReceiveWarning() {
		List<FbaInbound> rs = Lists.newArrayList();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
		String sql = "SELECT t.`shipment_id`,SUM(IFNULL(i.`quantity_shipped`,0)),SUM(IFNULL(i.`quantity_received`,0)) " +
				" FROM `psi_fba_inbound` t, `psi_fba_inbound_item` i "+
				" WHERE t.`id`=i.`fba_inbound_id` AND t.`arrival_date`=:p1 AND t.`shipment_status`='RECEIVING'"+
				" GROUP BY t.`id`";
		List<String> shipmentIds = Lists.newArrayList();
		List<Object[]> list = fbaInboundDao.findBySql(sql, new Parameter(format.format(DateUtils.addDays(new Date(), -5))));
		for (Object[] obj : list) {
			String shipmentId = obj[0].toString();
			Integer shipped = Integer.parseInt(obj[1].toString());
			Integer received = Integer.parseInt(obj[2].toString());
			if (shipped > 0 && (double)received/shipped < 0.8) {
				shipmentIds.add(shipmentId);
			}
		}
		if (shipmentIds.size() > 0) {
			DetachedCriteria dc = this.fbaInboundDao.createDetachedCriteria();
			dc.add(Restrictions.in("shipmentId", shipmentIds));
			rs = this.fbaInboundDao.find(dc);
			for (FbaInbound fbaInbound : rs) {
				Hibernate.initialize(fbaInbound.getItems());
			}
		}
		return rs;
	}

	public List<String> getOtherSkus(String proName, Integer warehouseId, String sku) {
		String sql = "SELECT t.`sku` FROM `psi_inventory` t WHERE t.`warehouse_id`=:p1 "+
				" AND CASE WHEN t.`color_code`='' THEN t.`product_name` ELSE CONCAT(t.`product_name`,'_',t.`color_code`) END =:p2 "+
				" AND t.`new_quantity`>0 AND t.`sku` !=:p3";
		return fbaInboundDao.findBySql(sql, new Parameter(warehouseId, proName, sku));
	}
	
//	public static void main(String[] args) {
//		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
//		FbaInboundSynMonitor inboundService = applicationContext.getBean(FbaInboundSynMonitor.class);
//		inboundService.synFbaInbound();
//	}
	
}