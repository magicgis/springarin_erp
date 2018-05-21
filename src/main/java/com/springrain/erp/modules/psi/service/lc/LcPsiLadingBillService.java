/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.lc;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;

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
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.custom.entity.SendEmail;
import com.springrain.erp.modules.custom.service.SendEmailService;
import com.springrain.erp.modules.psi.dao.PsiInventoryDao;
import com.springrain.erp.modules.psi.dao.PsiInventoryInDao;
import com.springrain.erp.modules.psi.dao.lc.LcPsiLadingBillDao;
import com.springrain.erp.modules.psi.dao.lc.LcPsiLadingBillItemDao;
import com.springrain.erp.modules.psi.dao.lc.LcPsiPartsInventoryOutDao;
import com.springrain.erp.modules.psi.dao.lc.LcPurchaseOrderDao;
import com.springrain.erp.modules.psi.dao.lc.LcPurchaseOrderDeliveryDateDao;
import com.springrain.erp.modules.psi.dao.lc.LcPurchaseOrderItemDao;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiInventoryIn;
import com.springrain.erp.modules.psi.entity.PsiInventoryInItem;
import com.springrain.erp.modules.psi.entity.PsiProductInStock;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.entity.lc.LcPsiLadingBill;
import com.springrain.erp.modules.psi.entity.lc.LcPsiLadingBillItem;
import com.springrain.erp.modules.psi.entity.lc.LcPsiQualityTest;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrderDeliveryDate;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrderItem;
import com.springrain.erp.modules.psi.service.PsiInventoryRevisionLogService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductInStockService;
import com.springrain.erp.modules.psi.service.PsiProductPartsService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.StockService;
import com.springrain.erp.modules.sys.dao.GenerateSequenceDao;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;

/**
 * 提单Service
 * @author Michael
 * @version 2014-11-11
 */
@Component
@Transactional(readOnly = true)
public class LcPsiLadingBillService extends BaseService {
	
	@Autowired  
	private LcPsiLadingBillDao 				psiLadingBillDao;
	@Autowired
	private LcPsiLadingBillItemDao 			psiLadingBillItemDao;
	@Autowired
	private GenerateSequenceDao 			genSequenceDao;
	@Autowired
	private LcPurchaseOrderItemDao 			orderItemDao;
	@Autowired  
	private LcPurchaseOrderItemService 		orderItemService;
	@Autowired
	private LcPurchaseOrderDao 				orderDao;
	@Autowired
	private LcPurchaseOrderService 			purchaseOrderService;
	@Autowired
	private PsiSupplierService 				psiSupplierService;
	@Autowired
	private PsiInventoryService 			psiInventoryService;
	@Autowired
	private PsiInventoryDao 				psiInventoryDao;
	@Autowired
	private StockService 					stockService;
	@Autowired
	private PsiInventoryRevisionLogService  psiInventoryLogService;
	@Autowired
	private PsiProductService 			    psiProductService;
	@Autowired
	private LcPsiPartsInventoryOutDao 	    psiPartsInventoryOutDao;
	@Autowired
	private PsiProductPartsService          productPartsService;
	@Autowired
	private LcPurchaseOrderDeliveryDateDao  deliveryDateDao;
	@Autowired
	private MailManager 					mailManager;
	@Autowired
	private PsiInventoryInDao 				psiInventoryInDao;
	@Autowired
	private LcPsiQualityTestService 	    testService;
	@Autowired
	private SendEmailService 				sendEmailService;
	
	private static String 				    filePath;
	
	@Autowired
	private PsiProductAttributeService psiProductAttributeService;
	
	@Autowired
	private PsiProductInStockService	psiProductInStockService;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(LcPsiLadingBillService.class);
	
	public LcPsiLadingBill get(Integer id) {
		return psiLadingBillDao.get(id);
	}
	
	
	
	public List<LcPurchaseOrder> getOrderInfo(String billId) {
		DetachedCriteria dc = psiLadingBillDao.createDetachedCriteria();
		dc.add(Restrictions.eq("billNo", billId));
		List<LcPsiLadingBill> temp = psiLadingBillDao.find(dc);
		if(temp.size()>0){
			Map<Integer,LcPurchaseOrder> map = Maps.newHashMap();
			for (LcPsiLadingBillItem item : temp.get(0).getItems()) {
				LcPurchaseOrder order = item.getPurchaseOrderItem().getPurchaseOrder();
				map.put(order.getId(), order);
			}
			return Lists.newArrayList(map.values());
		}
		return null;
	}
	
	public boolean canSave(String productName,String num){
		String temp = productName.split("\\|")[0];
		String p1 = temp.split(" ")[0];
		String p2 = temp.split(" ")[1];
		String sql = "select pack_quantity from psi_product where brand=:p1 and model=:p2";
		List<Integer> list=psiLadingBillDao.findBySql(sql, new Parameter(p1,p2));
		try {
			return Integer.parseInt(num)%list.get(0).intValue()==0;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public boolean canEditOrderQuantity(String orderItemId,String num){
		String sql = "SELECT SUM(t.quantity_lading) FROM lc_psi_lading_bill_item AS t WHERE t.purchase_order_item_id=:p1 AND t.del_flag='0' ";
		List<Integer> list=psiLadingBillDao.findBySql(sql, new Parameter(orderItemId));
		if(Integer.parseInt(num)-list.get(0).intValue()>0){
			return false;
		}
		return true;
	}
	
	public LcPsiLadingBill get(String billNo) {
		DetachedCriteria dc = psiLadingBillDao.createDetachedCriteria();
		dc.add(Restrictions.eq("billNo", billNo));
		List<LcPsiLadingBill> rs = psiLadingBillDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	
	
	public Map<String,LcPsiLadingBill> get(Set<String> billNos) {
		Map<String,LcPsiLadingBill> rs = Maps.newHashMap();
		DetachedCriteria dc = psiLadingBillDao.createDetachedCriteria();
		dc.add(Restrictions.in("billNo", billNos));
		List<LcPsiLadingBill> ladings =psiLadingBillDao.find(dc);
		for(LcPsiLadingBill bill:ladings){
			rs.put(bill.getBillNo(), bill);
		}
		return rs;
	}
	
	/**
	 *通过提单itemId获取提单信息 
	 */
	public LcPsiLadingBill getLadingBillByItemId(Integer billItemId){
		DetachedCriteria dc = psiLadingBillItemDao.createDetachedCriteria();
		dc.add(Restrictions.eq("id", billItemId));
		List<LcPsiLadingBillItem> rs = psiLadingBillItemDao.find(dc);
		if(rs!=null&&rs.size()>0){
			return rs.get(0).getLadingBill();
		}else{
			return null;
		}
	}
	
	
	
	/**
	 * 查询提单list根据状态
	 */
	public List<LcPsiLadingBill> fingAllBySta(String  status) {
		DetachedCriteria dc = psiLadingBillDao.createDetachedCriteria();
		dc.add(Restrictions.eq("billSta", status));
		return  psiLadingBillDao.find(dc);
	}
	
	public Page<LcPsiLadingBill> findReceiptCargoList(Page<LcPsiLadingBill> page, LcPsiLadingBill ladingBill) {
		DetachedCriteria dc = psiLadingBillDao.createDetachedCriteria();
		dc.add(Restrictions.eq("billSta","1"));
		if(ladingBill.getSupplier()!=null&&ladingBill.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier", ladingBill.getSupplier()));
		}
		if(ladingBill.getCreateDate()!=null){
			dc.add(Restrictions.ge("sureDate",ladingBill.getCreateDate()));
		}
		
		if(ladingBill.getSureDate()!=null){
			dc.add(Restrictions.le("sureDate",DateUtils.addDays(ladingBill.getSureDate(),1)));
		}
		page.setOrderBy("supplier desc");
		return psiLadingBillDao.find(page,dc);
	}
	
	public List<LcPsiLadingBill> findLadingBill(LcPsiLadingBill ladingBill) {
		String getFinalSql="";
		if(ladingBill.getSupplier()!=null&&ladingBill.getSupplier().getId()!=1&&ladingBill.getSupplier().getId()!=12){
			getFinalSql="SELECT bill_no,total_payment_amount,total_amount,plb.currency_type FROM lc_psi_lading_bill AS plb WHERE  plb.supplier_id=:p1 " +
					" and plb.del_flag='0' AND  plb.total_amount>plb.total_payment_amount and bill_sta='1'  ";
			 if(ladingBill.getSureDate()!=null){
				 getFinalSql+=" AND plb.`sure_date` <= '"+new SimpleDateFormat("yyyy-MM-dd").format(ladingBill.getSureDate())+"'";
			 }
			 if(ladingBill.getCreateDate()!=null){
				 getFinalSql+=" and plb.`sure_date` >='"+new SimpleDateFormat("yyyy-MM-dd").format(ladingBill.getCreateDate())+"'  "; 
			 }

		}else if(ladingBill.getSupplier()!=null&&ladingBill.getSupplier().getId()==1){
		    getFinalSql="SELECT bill_no,total_payment_amount,total_amount,plb.currency_type FROM lc_psi_lading_bill AS plb WHERE  plb.supplier_id=:p1 " +
				" and plb.del_flag='0'  AND  plb.total_amount>plb.total_payment_amount and bill_sta='1'  ";
		    if(ladingBill.getSureDate()!=null){
				 getFinalSql+="  AND CONCAT(DATE_FORMAT(DATE_ADD(LAST_DAY(plb.sure_date), INTERVAL 2 MONTH), '%Y-%m'),'-10') <= '"+new SimpleDateFormat("yyyy-MM-dd").format(ladingBill.getSureDate())+"'";
			 }
			 if(ladingBill.getCreateDate()!=null){
				 getFinalSql+="  and  CONCAT(DATE_FORMAT(DATE_ADD(LAST_DAY(plb.sure_date), INTERVAL 2 MONTH), '%Y-%m'),'-10')>='"+new SimpleDateFormat("yyyy-MM-dd").format(ladingBill.getCreateDate())+"'  ";
			 }
		}else if(ladingBill.getSupplier()!=null&&ladingBill.getSupplier().getId()==12){
			getFinalSql="SELECT bill_no,total_payment_amount,total_amount,plb.currency_type FROM lc_psi_lading_bill AS plb WHERE plb.supplier_id=:p1 " +
						" AND  plb.total_amount>plb.total_payment_amount and bill_sta='1' and plb.del_flag='0'  ";
			 if(ladingBill.getSureDate()!=null){
				 getFinalSql+="  AND plb.`sure_date` <= '"+new SimpleDateFormat("yyyy-MM-dd").format(ladingBill.getSureDate())+"' AND LAST_DAY(plb.sure_date) <= '"+new SimpleDateFormat("yyyy-MM-dd").format(ladingBill.getSureDate())+"' "; 
			 }
			 if(ladingBill.getCreateDate()!=null){
				 getFinalSql+="  and  plb.`sure_date` >='"+new SimpleDateFormat("yyyy-MM-dd").format(ladingBill.getCreateDate())+"'  ";
			 }
		}   
		List<LcPsiLadingBill> billList=new ArrayList<LcPsiLadingBill>();
		List<Object[]> list=psiLadingBillDao.findBySql(getFinalSql, new Parameter(ladingBill.getSupplier()));
		for (Object[] object : list) {
			LcPsiLadingBill bill=new LcPsiLadingBill();
			bill.setBillNo(object[0].toString());
			bill.setTotalPaymentAmount((BigDecimal) object[1]);
			bill.setTotalAmount((BigDecimal) object[2]);
			PsiSupplier  supplier=new PsiSupplier();
			supplier.setId(ladingBill.getSupplier().getId());
			supplier.setCurrencyType(object[3].toString());
			bill.setSupplier(supplier);
			billList.add(bill);
		}
		return billList;
	}
	
	public List<LcPsiLadingBill> findWayBillList(LcPsiLadingBill ladingBill) {
		DetachedCriteria dc = psiLadingBillDao.createDetachedCriteria();
		dc.createAlias("this.items", "item");
		
		dc.add(Restrictions.ne("billSta","2"));
		if(ladingBill.getSupplier()!=null&&ladingBill.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier", ladingBill.getSupplier()));
		}
		if(ladingBill.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",ladingBill.getCreateDate()));
		}
		
		if(ladingBill.getSureDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(ladingBill.getSureDate(),1)));
		}
		if(ladingBill.getBillNo()!=null&&!"".equals(ladingBill.getBillNo())){
			String[] countryArr=null;
			if("eu".equals(ladingBill.getBillNo())){
				countryArr=new String[]{"de","fr","it","es","uk"};
			}else{
				countryArr=new String[]{ladingBill.getBillNo()};
			}
			dc.add(Restrictions.in("item.countryCode", countryArr));
		}
		return psiLadingBillDao.find(dc);
	}
	
	public Map<Integer,LcPsiLadingBill> findByIds(Set<Integer> ladingbillIds) {
		Map<Integer,LcPsiLadingBill> rs =Maps.newHashMap();
		DetachedCriteria dc = psiLadingBillDao.createDetachedCriteria();
		if(ladingbillIds!=null&&ladingbillIds.size()>0){
			dc.add(Restrictions.in("id",ladingbillIds));
		}
		List<LcPsiLadingBill> list= psiLadingBillDao.find(dc);
		for(LcPsiLadingBill bill:list){
			rs.put(bill.getId(), bill);
		}
		return rs;
	}
	
	
	public Map<Integer,LcPsiLadingBillItem> findItemsByIds(Set<Integer> ladingbillItemIds) {
		Map<Integer,LcPsiLadingBillItem> rs =Maps.newHashMap();
		DetachedCriteria dc = psiLadingBillItemDao.createDetachedCriteria();
		if(ladingbillItemIds!=null&&ladingbillItemIds.size()>0){
			dc.add(Restrictions.in("id",ladingbillItemIds));
		}
		List<LcPsiLadingBillItem> list= psiLadingBillItemDao.find(dc);
		for(LcPsiLadingBillItem bill:list){
			rs.put(bill.getId(), bill);
		}
		return rs;
	}
	
	
	public Page<LcPsiLadingBill> find(Page<LcPsiLadingBill> page, LcPsiLadingBill psiLadingBill,List<Integer> ladingBillIds,String isCheck) {
		DetachedCriteria dc = psiLadingBillDao.createDetachedCriteria();
		dc.createAlias("this.items", "item");
		if(psiLadingBill.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",psiLadingBill.getCreateDate()));
		}
		
		if(psiLadingBill.getUpdateDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(psiLadingBill.getUpdateDate(),1)));
		}
		
		if(StringUtils.isNotEmpty(psiLadingBill.getBillNo())){
			dc.add(Restrictions.or(Restrictions.like("billNo", "%"+psiLadingBill.getBillNo()+"%"),Restrictions.like("item.productName", "%"+psiLadingBill.getBillNo()+"%")));
		}   
		   
		if(StringUtils.isNotEmpty(psiLadingBill.getBillSta())){
			dc.add(Restrictions.eq("billSta",psiLadingBill.getBillSta()));
		}else{
			dc.add(Restrictions.ne("billSta","2"));
		}
		
		if(psiLadingBill.getSupplier()!=null&&psiLadingBill.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier", psiLadingBill.getSupplier()));
		}
		
		if("1".equals(isCheck)){
			dc.add(Restrictions.in("billSta",new String[]{"0","5"}));
			if(ladingBillIds!=null&&ladingBillIds.size()>0){
				dc.add(Restrictions.in("id", ladingBillIds));
			}
		}
		
		//dc.addOrder(Order.desc("id"));
		page.setOrderBy(" id desc");
		return psiLadingBillDao.find2(page, dc);
	}
	
	
	
	/**
	 *查询质检单
	 */
	public Page<LcPsiQualityTest> findTest(Page<LcPsiQualityTest> page,LcPsiQualityTest test){
		return this.testService.findTest(page, test);
	}
	
	public List<LcPsiLadingBill> exp(LcPsiLadingBill psiLadingBill) {
		DetachedCriteria dc = psiLadingBillDao.createDetachedCriteria();
		if(psiLadingBill.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",psiLadingBill.getCreateDate()));
		}
		
		if(psiLadingBill.getUpdateDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(psiLadingBill.getUpdateDate(),1)));
		}
		
		if(StringUtils.isNotEmpty(psiLadingBill.getBillNo())){
			dc.add(Restrictions.like("billNo", "%"+psiLadingBill.getBillNo()+"%"));
		}
		
		if(StringUtils.isNotEmpty(psiLadingBill.getBillSta())){
			dc.add(Restrictions.eq("billSta",psiLadingBill.getBillSta()));
		}else{
			dc.add(Restrictions.ne("billSta","2"));
		}
		
		if(psiLadingBill.getSupplier()!=null&&psiLadingBill.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier", psiLadingBill.getSupplier()));
		}
		dc.addOrder(Order.desc("id"));
		
		return psiLadingBillDao.find(dc);
	}
	
	public List<Object[]> getProductLading(Integer supplierId,String currencyType){
		String getProductLadingSql="SELECT CONCAT(poi.product_name,'|',CASE poi.country_code WHEN 'com' THEN 'us' ELSE poi.country_code END,CASE poi.color_code WHEN '' THEN '' ELSE  CONCAT('|',poi.color_code) END) AS aa,po.order_no, " +
				" (poi.quantity_ordered-poi.quantity_received-poi.quantity_pre_received) AS aaa,poi.id,poi.item_price,po.id AS order_id ,(SELECT pp.pack_quantity FROM psi_product AS pp WHERE pp.id=poi.product_id) as pack_quantity,poi.product_id, " +
				" (poi.`quantity_off_ordered`-poi.`quantity_off_received`-poi.`quantity_off_pre_received`) AS offQuantity,(SELECT pp.box_volume FROM psi_product AS pp WHERE pp.id=poi.product_id) as volume " +
				" FROM lc_psi_purchase_order_item  AS poi  ,lc_psi_purchase_order AS po  " +       
				" WHERE poi.purchase_order_id =po.id AND poi.del_flag='0' AND po.supplier_id=:p1 AND po.del_flag='0' AND po.currency_type=:p2 AND po.order_sta in ('2','3') And (poi.quantity_ordered-poi.quantity_received-poi.quantity_pre_received)>0 ";
		return psiLadingBillDao.findBySql(getProductLadingSql, new Parameter(supplierId,currencyType));
	}
	
	
	public List<Object[]> getProductLadingForEdit(Integer supplierId,String currencyType){
		String getProductLadingSql="SELECT CONCAT(poi.product_name,'|',CASE poi.country_code WHEN 'com' THEN 'us' ELSE poi.country_code END,CASE poi.color_code WHEN '' THEN '' ELSE  CONCAT('|',poi.color_code) END) AS aa,po.order_no, " +
				" (poi.quantity_ordered-poi.quantity_received-poi.quantity_pre_received) AS aaa,poi.id,poi.item_price,po.id AS order_id ,(SELECT pp.pack_quantity FROM psi_product AS pp WHERE pp.id=poi.product_id) as pack_quantity,poi.product_id, " +
				" (poi.`quantity_off_ordered`-poi.`quantity_off_received`-poi.`quantity_off_pre_received`) AS offQuantity,(SELECT pp.box_volume FROM psi_product AS pp WHERE pp.id=poi.product_id) as volume " +
				" FROM lc_psi_purchase_order_item  AS poi  ,lc_psi_purchase_order AS po  " +
				" WHERE poi.purchase_order_id =po.id AND po.supplier_id=:p1 AND po.del_flag='0' AND poi.del_flag='0' AND po.currency_type=:p2 AND po.order_sta in ('2','3') And (poi.quantity_ordered-poi.quantity_received-poi.quantity_pre_received)>=0 ";
		return psiLadingBillDao.findBySql(getProductLadingSql, new Parameter(supplierId,currencyType));
	}
	
	
	//查询订单item   第一次收货时间
	public Date getFirstReceiveDate(Set<Integer> set){
		String getProductLadingSql="SELECT  b.`sure_date` FROM lc_psi_lading_bill AS b ,lc_psi_lading_bill_item AS i WHERE b.`id`=i.`lading_bill_id` AND b.`bill_sta`<>'2'  and b.`sure_date` is not null AND i.`purchase_order_item_id` IN :p1 order by i.id asc LIMIT 1;  ";
		List<Date> list= psiLadingBillDao.findBySql(getProductLadingSql, new Parameter(set));
		if(list.size()>0){
			return list.get(0);
		}
		return null;    
	}
	
	//查询订单item   最后一次收货时间              可验货时间              实际收货时间    
	public Object[] getFinishedReceiveDate(Set<Integer> set){
		String getProductLadingSql="SELECT  b.`delivery_date`,b.`sure_date` FROM lc_psi_lading_bill AS b ,lc_psi_lading_bill_item AS i WHERE b.`id`=i.`lading_bill_id` AND b.`bill_sta`<>'2' and b.`sure_date` is not null  AND i.`purchase_order_item_id` IN :p1 order by i.id desc LIMIT 1;  ";
		List<Object[]> list= psiLadingBillDao.findBySql(getProductLadingSql, new Parameter(set));
		if(list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public Map<Integer,Date> getFirstReceiveDateById(Set<Integer> set)	{
		Map<Integer,Date> map=Maps.newHashMap();
		String sql="SELECT  i.`purchase_order_item_id`,min(b.`sure_date`) FROM lc_psi_lading_bill AS b ,lc_psi_lading_bill_item AS i WHERE b.`id`=i.`lading_bill_id` AND b.`bill_sta`<>'2'  and b.`sure_date` is not null AND i.`purchase_order_item_id` IN :p1 group by i.`purchase_order_item_id` ";
		List<Object[]> list=psiLadingBillDao.findBySql(sql, new Parameter(set));
		if(list.size()>0){
			for (Object[] obj: list) {
				map.put(Integer.parseInt(obj[0].toString()),(Date)obj[1]);
			}
		}
		return map;
	}
	
	public Map<Integer,Integer> getReceiverNum(Set<Integer> set){
		Map<Integer,Integer> map=Maps.newHashMap();
		String sql="SELECT  i.`purchase_order_item_id`,COUNT(*) FROM lc_psi_lading_bill AS b ,lc_psi_lading_bill_item AS i WHERE b.`id`=i.`lading_bill_id` AND b.`bill_sta`<>'2' AND i.`purchase_order_item_id` IN :p1 GROUP BY i.`purchase_order_item_id` ";
		List<Object[]> list=psiLadingBillDao.findBySql(sql, new Parameter(set));
		if(list.size()>0){
			for (Object[] obj: list) {
				map.put(Integer.parseInt(obj[0].toString()),Integer.parseInt(obj[1].toString()));
			}
		}
		return map;
	}
	
	public Integer getReceiverNum2(Set<Integer> set){
		String sql="SELECT b.`sure_date` FROM lc_psi_lading_bill AS b ,lc_psi_lading_bill_item AS i WHERE b.`id`=i.`lading_bill_id` AND b.`bill_sta`<>'2' AND i.`purchase_order_item_id` IN :p1 GROUP BY b.`sure_date` ";
		List<Object> list=psiLadingBillDao.findBySql(sql, new Parameter(set));
		return list.size();
	}
	
	public Map<Integer,Date> getFinishedReceiveDateById(Set<Integer> set)	{
		Map<Integer,Date> map=Maps.newHashMap();
		String sql="SELECT  i.`purchase_order_item_id`,max(b.`sure_date`) FROM lc_psi_lading_bill AS b ,lc_psi_lading_bill_item AS i WHERE b.`id`=i.`lading_bill_id` AND b.`bill_sta`<>'2'  and b.`sure_date` is not null AND i.`purchase_order_item_id` IN :p1 group by i.`purchase_order_item_id` ";
		List<Object[]> list=psiLadingBillDao.findBySql(sql, new Parameter(set));
		if(list.size()>0){
			for (Object[] obj: list) {
				map.put(Integer.parseInt(obj[0].toString()),(Date)obj[1]);
			}
		}
		return map;
	}
	
	/**
	 *获取提单价格、是否付定金 
	 * key:price+","+已付+数量;
	 */
	public Map<Integer,String> getLadingItemPayDetail(Set<Integer> ladingItems)	{
		Map<Integer,Integer> idMap=Maps.newHashMap();
		Map<Integer,Integer> depositMap=Maps.newHashMap();
		Map<Integer,String> map=Maps.newHashMap();
		Map<Integer,String> tempMap=Maps.newHashMap();
		Set<Integer> orderItemIds = Sets.newHashSet();
		String sql="SELECT a.id,CONCAT(a.`item_price`,',',a.`total_payment_amount`,',',a.`quantity_lading`,',',b.`supplier_id`),a.`purchase_order_item_id` FROM lc_psi_lading_bill_item AS a,lc_psi_lading_bill AS b WHERE b.id = a.`lading_bill_id` AND a.id IN :p1 ";
		List<Object[]> list=psiLadingBillDao.findBySql(sql, new Parameter(ladingItems));
		if(list!=null&&list.size()>0){
			for(Object[] obj: list) {
				Integer ladingBillId = Integer.parseInt(obj[0].toString());
				idMap.put(ladingBillId, Integer.parseInt(obj[2].toString()));
				tempMap.put(ladingBillId, obj[1].toString());
				orderItemIds.add(Integer.parseInt(obj[2].toString()));
			}
			sql="SELECT b.`id`,a.`deposit` FROM lc_psi_purchase_order AS a,lc_psi_purchase_order_item AS b WHERE a.id=b.`purchase_order_id` AND a.`deposit`>0 AND a.`deposit_amount`>0 AND b.id IN :p1 ";
			List<Object[]> orderItemInfos=psiLadingBillDao.findBySql(sql, new Parameter(ladingItems));
			if(orderItemInfos!=null&&orderItemInfos.size()>0){
				for(Object[] obj: orderItemInfos) {
					depositMap.put(Integer.parseInt(obj[0].toString()), Integer.parseInt(obj[1].toString()));
				}
			}
			if(depositMap!=null&&depositMap.size()>0){
				orderItemIds.containsAll(depositMap.keySet());
			}
			
			if(orderItemIds!=null&&orderItemIds.size()>0){
				for(Map.Entry<Integer,Integer> ladingItemIdEntry:idMap.entrySet()){
					Integer ladingItemId = ladingItemIdEntry.getKey();
					String flag="0,No";
					Integer orderItemId =ladingItemIdEntry.getValue();
					if(depositMap.get(orderItemId)!=null){
						flag=depositMap.get(orderItemId)+",Yes";
					}
					map.put(ladingItemId, tempMap.get(ladingItemId)+","+flag);
				}
			}else{
				for(Map.Entry<Integer,Integer> ladingItemIdEntry:idMap.entrySet()){
					Integer ladingItemId = ladingItemIdEntry.getKey();
					map.put(ladingItemId, tempMap.get(ladingItemId)+"0,No");
				}
			}
		}
		return map;
	}
		
	@Transactional(readOnly = false)
	public void save(LcPsiLadingBill psiLadingBill) {
		this.psiLadingBillDao.getSession().clear();
		psiLadingBillDao.save(psiLadingBill);
	}
	
	
	@Transactional(readOnly = false)
	public void addSave(LcPsiLadingBill psiLadingBill,PsiSupplier supplier) {
		Integer balanceDelay1= 0;//当前延迟付款天数
		Integer balanceDelay2= 0;
		Integer balanceRate1 = supplier.getBalanceRate1();
		Integer balanceRate2 = supplier.getBalanceRate2();
		
		Integer total = 0;
		BigDecimal   totalAmount = new BigDecimal(0) ;
		BigDecimal   noDepositAmount = new BigDecimal(0) ;
		for (Iterator<LcPsiLadingBillItem> iterator = psiLadingBill.getItems().iterator(); iterator.hasNext();) {
			LcPsiLadingBillItem item = (LcPsiLadingBillItem) iterator.next();
			if(item.getQuantityLading()==null||item.getQuantityLading().intValue()==0){
				iterator.remove();
			}else{
				item.setLadingBill(psiLadingBill);
				//把产品组合拆出来
				if(item.getProductConName()!=null&&!"".equals(item.getProductConName())){
					String [] arr= item.getProductConName().split("\\|");
					item.setProductName(arr[0]);
					item.setCountryCode(this.changeCountryToCom(arr[1]));
					if(arr.length>2){
						item.setColorCode(arr[2]);
					}else{
						item.setColorCode("");
					}
				}
				LcPurchaseOrderItem orderItem = this.orderItemService.get(item.getPurchaseOrderItem().getId());
				if(item.getQuantityLading()!=null){
					total+=item.getQuantityLading();
					//把提单数量    关联到    订单item里的      预收货数量
					Integer  curPreReceived =orderItem.getQuantityPreReceived()+item.getQuantityLading();
					orderItem.setQuantityPreReceived(curPreReceived);
//					//收货完成才更新实际到货时间
//					if(orderItem.getQuantityOrdered().equals((orderItem.getQuantityPreReceived()+orderItem.getQuantityReceived()))){
//						orderItem.setActualDeliveryDate(psiLadingBill.getDeliveryDate());
//					}
					
					//对线下订单数进行消减
					if(item.getQuantityOffLading()!=null&&item.getQuantityOffLading().intValue()!=0){
						orderItem.setQuantityOffPreReceived(orderItem.getQuantityOffPreReceived()+item.getQuantityOffLading());
					}
					this.orderItemDao.save(orderItem);
				}
				Float  ratio =(100-orderItem.getPurchaseOrder().getDeposit())/100f;
				BigDecimal  itemAmount=new BigDecimal(item.getQuantityLading()).multiply(item.getItemPrice()).multiply(new BigDecimal(ratio+""));
				//算出总金额   乘上支付比例
				totalAmount=totalAmount.add(itemAmount);
				noDepositAmount=noDepositAmount.add(new BigDecimal(item.getQuantityLading()).multiply(item.getItemPrice()));
				item.setQuantitySure(0);//确认收货数为0
				
				//付款精确到提单item产品改造--2016-06-21 start
				item.setTotalAmount(itemAmount);
				item.setTotalPaymentAmount(BigDecimal.ZERO);
				item.setTotalPaymentPreAmount(BigDecimal.ZERO);
				item.setBalanceDelay1(balanceDelay1);
				item.setBalanceDelay2(balanceDelay2);
				item.setBalanceRate1(balanceRate1);
				item.setBalanceRate2(balanceRate2);
				//付款精确到提单item产品改造--2016-06-21 end
			}
		}
		psiLadingBill.setCreateDate(new Date());
		psiLadingBill.setCreateUser(UserUtils.getUser());
		psiLadingBill.setUpdateDate(new Date());
		psiLadingBill.setUpdateUser(UserUtils.getUser());
		psiLadingBill.setBillSta("0");
		psiLadingBill.setDelFlag("0");
		psiLadingBill.setTotalAmount(totalAmount);       //总金额
		psiLadingBill.setTotalPaymentAmount(BigDecimal.ZERO);         //已付款金额
		psiLadingBill.setTotalPaymentPreAmount(BigDecimal.ZERO);      //已申请付款金额
		psiLadingBill.setNoDepositAmount(noDepositAmount);
		String ladingNo=this.genSequenceDao.genSequence(supplier.getNikename()+"_LC_TDH",2);   
		psiLadingBill.setBillNo(ladingNo);  
		psiLadingBillDao.save(psiLadingBill);       
	}
	
	
	@Transactional(readOnly = false)
	public void editSave(LcPsiLadingBill psiLadingBill,PsiSupplier supplier) {
		Integer balanceRate1 = supplier.getBalanceRate1();
		Integer balanceRate2 = supplier.getBalanceRate2();
		Integer total = 0;
		BigDecimal   totalAmount = new BigDecimal(0);
		BigDecimal   noDepositAmount =new BigDecimal(0) ;
		Set<String> setNewIds = new HashSet<String>();
		Set<Integer>  delItemSet = Sets.newHashSet();
		for (Iterator<LcPsiLadingBillItem> iterator = psiLadingBill.getItems().iterator(); iterator.hasNext();) {
			LcPsiLadingBillItem item = (LcPsiLadingBillItem) iterator.next();
			if(item.getQuantityLading()==null||item.getQuantityLading()==0){
				iterator.remove();
			}else{
				
				item.setLadingBill(psiLadingBill);   
				if(item.getId()!=null){
					setNewIds.add(item.getId().toString());
				}
				
				if(item.getProductConName()!=null&&!"".equals(item.getProductConName())){
					String [] arr= item.getProductConName().split("\\|");
					item.setProductName(arr[0]);
					item.setCountryCode(this.changeCountryToCom(arr[1]));
					if(arr.length>2){
						item.setColorCode(arr[2]);
					}else{
						item.setColorCode("");
					}
				}
				BigDecimal itemAmount =new BigDecimal(0);
				if(item.getQuantityLading()!=null){
					total+=item.getQuantityLading();
					Float rate =(100-this.purchaseOrderService.getDeposit(item.getPurchaseOrderItem().getId()))/100f;
					itemAmount=new BigDecimal(item.getQuantityLading()).multiply(item.getItemPrice()).multiply(new BigDecimal(rate+""));
					totalAmount=totalAmount.add(itemAmount);
					noDepositAmount=noDepositAmount.add(new BigDecimal(item.getQuantityLading()).multiply(item.getItemPrice()));
					//把提单数量    关联到    订单item里的      预收货数量
					LcPurchaseOrderItem orderItem = this.orderItemDao.get(item.getPurchaseOrderItem().getId());
					Integer  curPreReceived =0;
					Integer  curPreReceivedOff =0;
					//现在的数减去原来的数  + 原来
					if(item.getOldQuantityLading()!=null){
						 curPreReceived =orderItem.getQuantityPreReceived()+(item.getQuantityLading()-item.getOldQuantityLading());
					}else{
						 curPreReceived =orderItem.getQuantityPreReceived()+item.getQuantityLading();
					}
					
					if(item.getOldQuantityOffLading()!=null){
						curPreReceivedOff =orderItem.getQuantityOffPreReceived()+(item.getQuantityOffLading()-item.getOldQuantityOffLading());
					}else{
						curPreReceivedOff =orderItem.getQuantityOffPreReceived()+item.getQuantityOffLading();
					}
					
					orderItem.setQuantityPreReceived(curPreReceived);
					orderItem.setQuantityOffPreReceived(curPreReceivedOff);
					this.orderItemDao.save(orderItem);
				}
				item.setQuantitySure(0);
				
				//付款精确到提单item产品改造--2016-06-21 start
				item.setTotalAmount(itemAmount);
				//如果为新增出来的收货单，录入收货付款比例和延期率
				if(item.getBalanceDelay1()==null&&item.getBalanceRate1()==null){
					item.setBalanceDelay1(0);
					item.setBalanceDelay2(0);
					item.setBalanceRate1(balanceRate1);
					item.setBalanceRate2(balanceRate2);
					item.setTotalPaymentAmount(BigDecimal.ZERO);
					item.setTotalPaymentPreAmount(BigDecimal.ZERO);
				}
				
				//付款精确到提单item产品改造--2016-06-21 end
			}
		}
		
		String oldItemIds=psiLadingBill.getOldItemIds();
		String [] oldIds = oldItemIds.split(",");

		if(setNewIds!=null&&setNewIds.size()>0){
			for(int j=0;j<oldIds.length;j++){
				if(!setNewIds.contains(oldIds[j])){
					//先查出根据LadingItem查出orderItem，更新orderItem的预收货数量
					LcPsiLadingBillItem billItem= this.psiLadingBillItemDao.get(Integer.valueOf(oldIds[j]));
					LcPurchaseOrderItem  orderItem =billItem.getPurchaseOrderItem();
					orderItem.setQuantityPreReceived(orderItem.getQuantityPreReceived()-billItem.getQuantityLading());
					orderItem.setQuantityOffPreReceived(orderItem.getQuantityOffPreReceived()-billItem.getQuantityOffLading());
					this.orderItemDao.save(orderItem);
					//不包含就干掉
					delItemSet.add(Integer.valueOf(oldIds[j]));
				};
			}
		}else{
			for(int j=0;j<oldIds.length;j++){
				//先查出根据LadingItem查出orderItem，更新orderItem的预收货数量
				LcPsiLadingBillItem billItem= this.psiLadingBillItemDao.get(Integer.valueOf(oldIds[j]));
				LcPurchaseOrderItem  orderItem =billItem.getPurchaseOrderItem();
				orderItem.setQuantityPreReceived(orderItem.getQuantityPreReceived()-billItem.getQuantityLading());
				orderItem.setQuantityOffPreReceived(orderItem.getQuantityOffPreReceived()-billItem.getQuantityOffLading());
				this.orderItemDao.save(orderItem);
				delItemSet.add(Integer.valueOf(oldIds[j]));
			}
		}
		
		
		if(delItemSet.size()>0){
			for(LcPsiLadingBillItem item:this.getLadingBillItems(delItemSet)){
				item.setDelFlag("1");
				item.setLadingBill(psiLadingBill);
				psiLadingBill.getItems().add(item);
			};
		}
		
		psiLadingBill.setUpdateDate(new Date());
		psiLadingBill.setUpdateUser(UserUtils.getUser());
		psiLadingBill.setTotalAmount(totalAmount);
		psiLadingBill.setNoDepositAmount(noDepositAmount);
		
		this.psiLadingBillDao.getSession().merge(psiLadingBill);
	}
	
	@Transactional( readOnly = false)
	public String sureSave(LcPsiLadingBill psiLadingBill,MultipartFile[] attchmentFiles,Map<Integer,String> skuMap,Map<String,Map<String,Object>> avgPriceMap ) {
		//如果为确认状态不能再确认
		if("1".equals(psiLadingBill.getBillSta())){
			return null;
		}
		StringBuffer  sb = new StringBuffer("Hi,All<br/><br/>收货单号：["+psiLadingBill.getBillNo()+"]已确认收货，详情如下：<br/>");
		sb.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
		sb.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#f2f4f6;color:#666;'>");
		sb.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>产品名称</th>");
		sb.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>Sku</th>");
		sb.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>数量</th>");
		sb.append("</tr>");
		
		
		boolean flag =true;
		Map<String,Integer>    orderProductsMap = Maps.newHashMap();  //key:orderId,产品id,颜色   value:收货数量
		Map<String,Integer>    orderProductsNewMap = Maps.newHashMap();  //key:orderId,产品id,颜色   value:收货数量
		Set<String>    proColorSet = Sets.newHashSet();  //key:产品id,color   value:color
		Map<Integer ,List<String>>   orderMap= Maps.newHashMap();
		Map<Integer ,String>   orderStatus= Maps.newHashMap();
		String billNo =psiLadingBill.getBillNo();
		Map<Integer,String>  productNameMap=this.psiProductService.findProductNamesMap();
		Float rate=AmazonProduct2Service.getRateConfig().get("USD/CNY");
		
		//配件校验start
		for(LcPsiLadingBillItem billItem: psiLadingBill.getItems()){
			LcPurchaseOrderItem    orderItem = billItem.getPurchaseOrderItem();
			Integer orderId = orderItem.getPurchaseOrder().getId();
			if("1".equals(orderItem.getPurchaseOrder().getToPartsOrder())){
				Integer productId=orderItem.getProduct().getId();
				String key =orderId+","+productId+","+orderItem.getColorCode();
				String arr[] =skuMap.get(billItem.getId()).split(",,,");
				Integer curSureQuantity =0;
				if(!"null".equals(arr[1])){
					curSureQuantity=Integer.parseInt(arr[1]);
				}
				
				//本次确认数为0，跳出
				if(curSureQuantity.equals(0)){
					continue;
				}
				
				//本地确认收货的数量
				if(orderProductsMap.get(key)!=null){
					curSureQuantity+=orderProductsMap.get(key);
				}
				orderProductsMap.put(key, curSureQuantity);
				proColorSet.add(productId+","+orderItem.getColorCode());
			}
		
			
		}
		
		if(orderProductsMap!=null&&orderProductsMap.size()>0){
			for(Map.Entry<String, Integer> entry:orderProductsMap.entrySet()){
				String key = entry.getKey();
				String arr[] =key.split(",");
				String proColor =arr[1]+",";
				if(arr.length>2){
					proColor+=arr[2];
				}
				Set<String> proColors = this.productPartsService.getPartsColors(proColorSet);
				//组成有配件map
				if(proColors.contains(proColor)){
					orderProductsNewMap.put(key, entry.getValue());
				}
			}
			
			
			//处理配件和订单关系表      可用配件套数减本次收货的数量
			String res=this.partsValidate(orderProductsNewMap, productNameMap);
			if(StringUtils.isNotEmpty(res)){
				return res;
			}
		}
		//配件校验end
		
		//看这次是不是收货完成
		boolean receivedOverFlag =true;
		
		//获得中国仓B   id:130
		Stock  store =stockService.get(130);  
		//根据提单生成相应的入库单
		SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMddHHmmsss");
		String flowNo=sdf.format(new Date());
		String inNo=flowNo.substring(0,8)+"_RKD"+flowNo.substring(8);
		PsiInventoryIn inInventory = new PsiInventoryIn();
		inInventory.setAddDate(new Date());
		inInventory.setAddUser(UserUtils.getUser());
		inInventory.setTranLocalId(psiLadingBill.getId());
		inInventory.setTranLocalNo(psiLadingBill.getBillNo());
		inInventory.setOperationType("Purchase Storing");
		inInventory.setWarehouseId(store.getId());
		inInventory.setWarehouseName("中国本地B");
		inInventory.setSource("CN");
		inInventory.setBillNo(inNo);
		inInventory.setDataDate(psiLadingBill.getDeliveryDate());
		inInventory.setTranMan(psiLadingBill.getTranMan());
		inInventory.setPhone(psiLadingBill.getPhone());
		inInventory.setCarNo(psiLadingBill.getCarNo());
		String monthFlowNo=this.genSequenceDao.genSequenceByMonth2("I",4);  //采购入库添加入库流水号
		inInventory.setFlowNo(monthFlowNo);
		List<PsiInventoryInItem> inItems = Lists.newArrayList();
				
		for(LcPsiLadingBillItem billItem: psiLadingBill.getItems()){
			PsiInventoryInItem inItem = new PsiInventoryInItem();//入库item
			
			String arr[] =skuMap.get(billItem.getId()).split(",,,");
			String sku =arr[0];
			Integer curSureQuantity=0;
			if(!"null".equals(arr[1])){
				curSureQuantity=Integer.parseInt(arr[1]);
			}
			 
			//保存备注
			if(arr.length>2&&StringUtils.isNotEmpty(arr[2].trim())){
				billItem.setRemark(arr[2]);
			}
			if(arr.length>3){
				billItem.setQuantitySpares(Integer.parseInt(arr[3]));
			}
			//如果有一个没有收货确认完成就不处理
			if(receivedOverFlag&&(billItem.getQuantityLading()-billItem.getQuantitySure()-curSureQuantity!=0)){
				receivedOverFlag=false;
			}
			//如果当前确认数为0   或者可确认数为0 不处理
			if(curSureQuantity.intValue()==0||billItem.getQuantityLading()-billItem.getQuantitySure()==0){
				continue;
			}
			//如果确认数大于总数，报异常
			if(billItem.getQuantityLading()-billItem.getQuantitySure()-curSureQuantity<0){
				throw new RuntimeException("确认失败、请联系软件开发人员！");
			}
			LcPurchaseOrderItem    orderItem = billItem.getPurchaseOrderItem();
			if(StringUtils.isEmpty(sku)){
				sku = billItem.getProductNameColor()+"_"+billItem.getCountryCode();
				//throw new RuntimeException("sku为空");
			}
			
			//sb.append("产品："+billItem.getProductNameColor()+"sku:"+billItem.getSku()+"数量："+curSureQuantity+"<br/>");
			sb.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
			sb.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+billItem.getProductNameColor()+"</td>");
			sb.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+billItem.getSku()+"</td>");
			sb.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+curSureQuantity+"</td>");
			sb.append("</tr>");
			
			//16.05.13 对订单多批次收货处理 start
			Integer curTemp=curSureQuantity;
			for(LcPurchaseOrderDeliveryDate deliveryPo: orderItem.getDeliveryDateList()){
				Integer deQ=deliveryPo.getQuantity()-deliveryPo.getQuantityReceived();
				if(curTemp<=0){
					break;
				}
				if(deQ>0){
					
					//本次没有收完
					Integer tempQ=curTemp-deQ;
					curTemp=curTemp-deQ;
					if(curTemp>0){
						deliveryPo.setQuantityReceived(deliveryPo.getQuantity());
//						deliveryPo.setQuantityOffReceived(deliveryPo.getQuantityOff());
					}else{
						deliveryPo.setQuantityReceived(deliveryPo.getQuantityReceived()+deQ+tempQ);
						//对线下的进行处理
//						if(orderItem.getQuantityOffUnReceived()>0&&billItem.getQuantityOffLading()>0&&(deliveryPo.getUnReceivedOff()>0)&&(deliveryPo.getQuantityOffReceived()+deQ+tempQ)<deliveryPo.getQuantityOff()){
//							deliveryPo.setQuantityOffReceived(deliveryPo.getQuantityOffReceived()+deQ+tempQ);
//						}else{
//							deliveryPo.setQuantityOffReceived(deliveryPo.getQuantityOff());
//						}
					}
					
					this.deliveryDateDao.save(deliveryPo);
				}
			}
			//16.05.13 对订单多批次收货处理 end
			
			//入库的时候，注意是线上还是线下
			PsiInventory inventory= psiInventoryService.findBySku(sku, store.getId());
			if(inventory!=null){
				//如果这次的sku的国家  平台   颜色  与库里有一个不同，说明：1，当前sku设置错误(让销售在前台重新设置当前产品的sku)；2，库里第一次设置已经错误(只能把库里的sku的产品、国家、颜色改成正确的)
				if(!inventory.getProductName().equals(billItem.getProductName())||!inventory.getCountryCode().equals(billItem.getCountryCode())||!inventory.getColorCode().equals(billItem.getColorCode())){
					String countryCode= billItem.getCountryCode();
					if("com".equals(countryCode)){
						countryCode="us";
					}       
					throw new RuntimeException("当前库里的sku的产品id、国家、颜色跟仓库里的匹配不上，请检查是销售人员绑错本次sku绑定错误、还是第一次入库就是错误的：："+billItem.getProductName()+"----"+countryCode+"-----"+billItem.getColorCode());
				}
			}
			
			
			//可确认的  与线下数进行比较
			Integer oldQuantitySure=billItem.getQuantitySure();
			billItem.setSku(sku);
			billItem.setQuantitySure(billItem.getQuantitySure()+curSureQuantity);
			
			Map<String,Object> temp=avgPriceMap.get(billItem.getProductNameColor());
			if(temp==null){
				temp=Maps.newHashMap();
				avgPriceMap.put(billItem.getProductNameColor(), temp);
			}
			
			//线下数量少，优先收线下的。。
			if(billItem.getQuantityOffLading()==null){
				billItem.setQuantityOffLading(0);
			}
			
			if((oldQuantitySure-billItem.getQuantityOffLading())<=0){
				Integer canOff =billItem.getQuantityOffLading()-oldQuantitySure;
				if(canOff-curSureQuantity>=0){
					//如果线下可收的数>=本次确认数
					inItem=this.updateInventory(curSureQuantity,orderItem,store,sku,"offline",inInventory,billItem.getId(),billItem.getItemPrice().floatValue());
				}else{
					//如果线下可收数小于本次确认数      线下的收完         剩下的变为线上的
					inItem=this.updateInventory(curSureQuantity-canOff,orderItem,store,sku,"new",inInventory,billItem.getId(),billItem.getItemPrice().floatValue());
					if(canOff>0){
						inItem=this.updateInventory(canOff,orderItem,store,sku,"offline",inInventory,billItem.getId(),billItem.getItemPrice().floatValue());
					}
				}
				try{
					Integer quantity=curSureQuantity;
					temp.put("quantity",quantity+(Integer)(temp.get("quantity")==null?0:temp.get("quantity")));
					if("USD".equals(billItem.getLadingBill().getCurrencyType())){
						temp.put("price", quantity*rate*billItem.getItemPrice().floatValue()+(Float)(temp.get("price")==null?0f:temp.get("price")));
					}else{
						temp.put("price", quantity*billItem.getItemPrice().floatValue()+(Float)(temp.get("price")==null?0f:temp.get("price")));
					}
				}catch(Exception e){
					LOGGER.error("offline+new", e);
				}
				
			}else{
				//已确认的数量已经大于线下的数量，说明线下的已经收完
				inItem=this.updateInventory(curSureQuantity-billItem.getQuantityOffLading(),orderItem,store,sku,"new",inInventory,billItem.getId(),billItem.getItemPrice().floatValue());
				try{
					Integer quantity=curSureQuantity-billItem.getQuantityOffLading();
					temp.put("quantity",quantity+(Integer)(temp.get("quantity")==null?0:temp.get("quantity")));
					if("USD".equals(billItem.getLadingBill().getCurrencyType())){
						temp.put("price", quantity*rate*billItem.getItemPrice().floatValue()+(Float)(temp.get("price")==null?0f:temp.get("price")));
					}else{
						temp.put("price", quantity*billItem.getItemPrice().floatValue()+(Float)(temp.get("price")==null?0f:temp.get("price")));
					}
				}catch(Exception e){
					LOGGER.error("new", e);
				}
			}
			
			
			inItems.add(inItem);
			
			
			//处理订单数据
			Integer orderId = orderItem.getPurchaseOrder().getId();
			String  productName=orderItem.getProductName()+":"+orderItem.getColorCode()+":"+orderItem.getCountryCode();
			Integer  orderNum  = orderItem.getQuantityOrdered();
			Integer  perRecNum = orderItem.getQuantityPreReceived();
			Integer  recNum    = orderItem.getQuantityReceived();
			if(orderNum<perRecNum+recNum){
				flag = false;
				break;
			}else{
				orderItem.setQuantityReceived(recNum+curSureQuantity);
				orderItem.setQuantityPreReceived(perRecNum-curSureQuantity);
				
				//对订单的线下数进行处理
				if(curSureQuantity>=orderItem.getQuantityOffPreReceived()){
					orderItem.setQuantityOffReceived(orderItem.getQuantityOffReceived()+orderItem.getQuantityOffPreReceived());
					orderItem.setQuantityOffPreReceived(0);
				}else{
					orderItem.setQuantityOffReceived(orderItem.getQuantityOffReceived()+curSureQuantity);
					orderItem.setQuantityOffPreReceived(orderItem.getQuantityOffPreReceived()-curSureQuantity);
				}
				
				
				//如果预收货数为0；说明多次点击确认
				if(orderItem.getQuantityPreReceived()<0){
					throw new RuntimeException("提单进行了多次确认，请核实，操作已取消");
				}
				
				if(orderItem.getQuantityReceived()>orderItem.getQuantityOrdered()){
					throw new RuntimeException("该提单关联的订单项已收货数大于订单数，请核实，操作已取消");
				}
				
				if(orderNum.equals(recNum+curSureQuantity)){
					List<String> products = null;
					if(orderMap.get(orderId)==null){
						products =Lists.newArrayList();
					}else{
						products=orderMap.get(orderId);
					}
					products.add(productName);
					orderMap.put(orderId, products);
				}
				orderStatus.put(orderId, "3,"+orderItem.getPurchaseOrder().getOrderSta());
				
				//收货完成才更新实际到货时间
				if(orderItem.getQuantityOrdered().equals((orderItem.getQuantityPreReceived()+orderItem.getQuantityReceived()))){
					orderItem.setActualDeliveryDate(psiLadingBill.getActualDeliveryDate());
				}
				this.orderItemDao.save(orderItem);
			}
		}
		
		
		//处理订单信息 start
		for ( Map.Entry<Integer, List<String>> entry : orderMap.entrySet()) {
			Integer orderId = entry.getKey();
			boolean isFinal = true;
			LcPurchaseOrder order = this.orderDao.get(orderId);
			//看之前订单状态
			String oldSta = order.getOrderSta();
			for (LcPurchaseOrderItem item : order.getItems()) {
				String  productName=item.getProductName()+":"+item.getColorCode()+":"+item.getCountryCode();
//				if(!orderMap.get(orderId).contains(productName)){
				if(!entry.getValue().contains(productName)){
					Integer  orderNum  = item.getQuantityOrdered();
					Integer  recNum    = item.getQuantityReceived();
					if(orderNum!=null&&!orderNum.equals(recNum)){
						isFinal = false;
						break;
					}
				}
			}
			if(isFinal){
				//if(Float.floatToIntBits(order.getTotalAmount())==Float.floatToIntBits(order.getPaymentAmount()+order.getDepositAmount())){
				if(order.getTotalAmount().subtract(order.getPaymentAmount()).subtract(order.getDepositAmount())
						.abs().compareTo(new BigDecimal("0.1"))<=0){//一毛钱以内的误差认为完成
					orderStatus.put(orderId, "5,"+oldSta);
				}else{
					orderStatus.put(orderId, "4,"+oldSta);
				}
				
			}	
		}
		
		for (Map.Entry<Integer, String> entry : orderStatus.entrySet()) {
			Integer id = entry.getKey();
			String arr[] =entry.getValue().split(",");
			String curSta = arr[0];
			String oldSta = arr[1];
			if("4".equals(curSta)){
				//如果是已收货      获取收货时间  
				this.orderDao.updateOrderStaAndFinishedDate(id, curSta,new Date());
			}else if("5".equals(curSta)){
				if("4".equals(oldSta)){
					//如果原来已收货完成
					this.orderDao.updateOrderSta(id, curSta);
				}else{
					//原来没收货完成更新时间状态
					this.orderDao.updateOrderStaAndFinishedDate(id, curSta,new Date());
				}
			}else{
				//部分收货状态
				this.orderDao.updateOrderSta(id, curSta);
			}
		}
		
		//处理订单信息 end
		
		if(!flag){
			//收货数量大于订单数量，请编辑后保存
			throw new RuntimeException("收货数量大于订单数量，请编辑后保存");
		}
		
		//配件start
		if(orderProductsNewMap!=null&&orderProductsNewMap.size()>0){
			//处理配件和订单关系表      可用配件套数减本次收货的数量
			this.exePartsAndOrderRelative(orderProductsNewMap);
		}
		//配件end
		
		
		if(receivedOverFlag){
			sb.append("</table><br/>该收货单货品<span style='color:green' >已全部收货</span>");
			//收货完成     备品进入中国仓
			for(LcPsiLadingBillItem item:psiLadingBill.getItems()){
				if(item.getQuantitySpares()!=null&&item.getQuantitySpares()>0){
					//整理数据进入中国本地仓库数据 start
					PsiInventoryInItem inItem = new PsiInventoryInItem();//入库item
					inItem=this.updateInventory(item.getQuantitySpares(),item.getPurchaseOrderItem(),store,item.getSku(),"spares",inInventory,item.getId(),item.getItemPrice().floatValue());
					inItems.add(inItem);
					//整理数据进入中国本地仓库数据 end
				}
				
				
				//16.06.13 对订单多批次收货处理 start
				if(item.getQuantityOffLading()!=null&&item.getQuantityOffLading()>0){
					Integer offCurTemp = item.getQuantityOffLading();  //只有最后一次确认，才知道该提单有多少线下的
					for(LcPurchaseOrderDeliveryDate deliveryPo: item.getPurchaseOrderItem().getDeliveryDateList()){
						Integer deQ=deliveryPo.getQuantityOff()-deliveryPo.getQuantityOffReceived();
						if(offCurTemp<=0){
							break;
						}
						if(deQ>0){
							//本次没有收完
							Integer tempQ=offCurTemp-deQ;
							offCurTemp=offCurTemp-deQ;
							if(offCurTemp>0){
								deliveryPo.setQuantityOffReceived(deliveryPo.getQuantityOff());
							}else{
								deliveryPo.setQuantityOffReceived(deliveryPo.getQuantityOffReceived()+deQ+tempQ);
							}
							this.deliveryDateDao.save(deliveryPo);
						}
					}
				}
				
				//16.06.13 对订单多批次收货处理 start
			}
			psiLadingBill.setBillSta("1");
			
			
		}else{
			sb.append("</table>该收货单货品<span style='color:red' >未全部收货</span>！");
			psiLadingBill.setBillSta("5");
		}
		
		for (MultipartFile attchmentFile : attchmentFiles) {
			if(attchmentFile.getSize()!=0){
				String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/ladingBills";
				File baseDir = new File(baseDirStr+"/"+psiLadingBill.getBillNo()); 
				if(!baseDir.isDirectory())
					baseDir.mkdirs();
				String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
				String name=UUID.randomUUID().toString()+suffix;
				File dest = new File(baseDir,name);
				try {
					FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
					psiLadingBill.setAttchmentPathAppend("/psi/ladingBills/"+psiLadingBill.getBillNo()+"/"+name);
				} catch (IOException e) {
					logger.warn(name+"文件保存失败",e);
				}
			}
		}
		
		psiLadingBill.setSureUser(UserUtils.getUser());
		psiLadingBill.setSureDate(new Date());
		
		this.psiLadingBillDao.save(psiLadingBill);
		
		if(inItems.size()>0){
			//提单转化成入库单入库
			inInventory.setItems(inItems);
			this.psiInventoryInDao.save(inInventory);
		}
		
		try{
			sendNoticeEmail(psiLadingBill.getCreateUser().getEmail(), sb.toString(), "收货单已确认收货【"+psiLadingBill.getBillNo()+"】", UserUtils.getUser().getEmail()+","+UserUtils.logistics1+","+UserUtils.logistics2, "");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return "";
	}
	
	//分开处理平均价格
	@Transactional(readOnly = false)
	public void updateAvgPrice(Map<String,Map<String,Object>> avgPriceMap){
		try{
			Map<String,Map<String,Object>> stockMap=psiInventoryService.findInventoryAndPrice(130);
			if(avgPriceMap!=null&&avgPriceMap.size()>0){
				Map<String,Map<String,Object>> beforeMap=psiProductAttributeService.findProductPrice2();
				Map<String,Map<String,PsiProductInStock>> totalMap=psiProductInStockService.getHistoryInventory();
				Float rate=AmazonProduct2Service.getRateConfig().get("USD/CNY");
				String info="";
				for (Map.Entry<String,Map<String,Object>> entry: avgPriceMap.entrySet()) {
					String productName = entry.getKey();
					Map<String,Object> valueMap = entry.getValue();
					if(stockMap!=null&&stockMap.get(productName)!=null){
						if((Float)stockMap.get(productName).get("price")==0){
							Float avgPrice=(Float)valueMap.get("price")/(Integer)valueMap.get("quantity");
							psiInventoryService.updateInventoryAvgPrice(130,productName,avgPrice);
						}else{
							Float avgPrice=((Float)valueMap.get("price")+(Float)stockMap.get(productName).get("price"))/((Integer)valueMap.get("quantity")+(Integer)stockMap.get(productName).get("quantity"));
							psiInventoryService.updateInventoryAvgPrice(130,productName,avgPrice);
						}
					}else{
							Float avgPrice=(Float)valueMap.get("price")/(Integer)valueMap.get("quantity");
							psiInventoryService.updateInventoryAvgPrice(130,productName,avgPrice);
					}
						
						if(beforeMap.get(productName)!=null&&beforeMap.get(productName).get("price")!=null&&(Float)beforeMap.get(productName).get("price")>0){
							Integer totalStock = 0;
							if(totalMap.get(productName)!=null&&totalMap.get(productName).get("total")!=null
									&&totalMap.get(productName).get("total").getTotalStock()>0){
							    Float price = (Float)beforeMap.get(productName).get("price");
								if("USD".equals(beforeMap.get(productName).get("type"))){
									price = rate * price;
								}
								totalStock = totalMap.get(productName).get("total").getTotalStock();
								Float tempAvgPrice = ((Float)valueMap.get("price")+price*totalStock)/(totalStock+(Integer)valueMap.get("quantity"));
								psiProductAttributeService.update(tempAvgPrice,"CNY",productName);
								Float avgPrice=(Float)valueMap.get("price")/(Integer)valueMap.get("quantity");
								info+=productName+":"+tempAvgPrice+",入库:"+avgPrice+":"+(Integer)valueMap.get("quantity")+","+price+":"+totalStock+"\n";
							}else{
								Float avgPrice=(Float)valueMap.get("price")/(Integer)valueMap.get("quantity");
								logger.info("totalStock==0,新增"+productName+"价格"+avgPrice+"CNY");
								psiProductAttributeService.update(avgPrice,"CNY",productName);
								info+=productName+",totalStock=0,"+avgPrice+"\n";
							}
						}else{
							Float avgPrice=(Float)valueMap.get("price")/(Integer)valueMap.get("quantity");
							logger.info("新增"+productName+"价格"+avgPrice+"CNY");
							psiProductAttributeService.update(avgPrice,"CNY",productName);
							info+=productName+":"+avgPrice+"\n";
						}
					}
				
				
				    try{
						WeixinSendMsgUtil.sendTextMsgToUser("eileen",info);
					}catch(Exception e){
						logger.error("cross帖微信发送账号错误异常！",e);
					}
			}
		}catch(Exception e){
			LOGGER.error("更新库存平均价格异常", e);
		}
	}
	
	//处理配件订单关系表，减少可收数
	public void  exePartsAndOrderRelative(Map<String,Integer> orderProductsMap){
		for(Map.Entry<String,Integer> entry:orderProductsMap.entrySet()){
			String key = entry.getKey();
			String arr[] =key.split(",");
			String color="";
			if(arr.length>2){
				color=arr[2];
			}
			this.psiPartsInventoryOutDao.minusProductCanDeliveryQuantity(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]),  entry.getValue(),color);
		}
	}
	
	//校验配送套数
	public String  partsValidate(Map<String,Integer> orderProductsMap,Map<Integer,String> productNameMap){
		StringBuffer sb = new StringBuffer("");
		for(Map.Entry<String,Integer> entry:orderProductsMap.entrySet()){
			String key = entry.getKey();
			String arr[] =key.split(",");
			String color="";
			if(arr.length>2){
				color=arr[2];
			}
			Integer partsSets = this.psiPartsInventoryOutDao.getCanDeliveryQuantity(Integer.parseInt(arr[0]),Integer.parseInt(arr[1]),color);
			if(partsSets==null||entry.getValue()>partsSets){
				sb.append("产品名：").append(productNameMap.get(Integer.parseInt(arr[1]))).append(",本次收货数:").append(entry.getValue()).append(",包材配送套数:").append(partsSets==null?"空":partsSets+"").append(";");
			}
		}
		if(sb.length()>0){
			return sb.toString();
		}else{
			return "";
		}
		
	}
	
	
	@Transactional(readOnly = false)
	public boolean cancelBill(LcPsiLadingBill psiLadingBill) {
		boolean flag=true;
		try{
		for(LcPsiLadingBillItem item:psiLadingBill.getItems()){
			LcPurchaseOrderItem orderItem=item.getPurchaseOrderItem();
			//获取预提单数量 
			orderItem.setQuantityPreReceived(orderItem.getQuantityPreReceived()-item.getQuantityLading());
			orderItem.setQuantityOffPreReceived(orderItem.getQuantityOffPreReceived()-item.getQuantityOffLading());
			orderItemDao.save(orderItem);
		}
		}catch(Exception  ex){
			return false;
		}
		return flag;
	}
	
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		psiLadingBillDao.deleteById(id);
	}
	
	
	public String changeCountryToCom(String country){
		String reStr=country;
		if(!StringUtils.isEmpty(country)){
			if("us".equals(country)){
				reStr= "com";
			}
		}
		return reStr;
	}
	
	public String changeCountryToUs(String country){
		String reStr=country;
		if(!StringUtils.isEmpty(country)){
			if("com".equals(country)){
				reStr= "us";
			}
		}
		return reStr;
	}
	
	
	
	private PsiInventoryInItem updateInventory(Integer quantity,LcPurchaseOrderItem orderItem,Stock wareHouse,String sku,String type,PsiInventoryIn inventoryIn,Integer itemId,Float price){
		String relativeNumber=inventoryIn.getBillNo();
		PsiInventoryInItem inItem ;
		PsiInventory   inventory = new PsiInventory();
		Integer inventoryId=psiInventoryService.getInventoryIdBySkuAndHouseId(sku, wareHouse.getId());
		Integer timelyQ=null;
		if(inventoryId>0){
			//说明该条数据已经存在仓库里，更新原数据；
			inventory = psiInventoryService.get(inventoryId);
			/**
			 *如果用inventory_reversion_log表统计数据及时库存数据，开启下面注释
			 * 
			 */
			//this.psiInventoryLogService.getSumByInventory(warehouseId,inventory);
			if("new".equals(type)){
				inventory.setNewQuantity(inventory.getNewQuantity()+quantity);  
				timelyQ=inventory.getNewQuantity();
			}else if("spares".equals(type)){
				inventory.setSparesQuantity(inventory.getSparesQuantity()+quantity);  
				timelyQ=inventory.getSparesQuantity();
			}else if("offline".equals(type)){
				inventory.setOfflineQuantity(inventory.getOfflineQuantity()+quantity); 
				timelyQ=inventory.getOfflineQuantity();
			}
			
			inventory.setUpdateDate(new Date());
			psiInventoryDao.save(inventory);
			//添加一条仓库日志记录
			psiInventoryService.savelog("Purchase Storing",type,quantity,"",inventory.getColorCode(),inventory.getCountryCode(),inventory.getProductId(),inventory.getProductName(),wareHouse.getId(),relativeNumber,inventory.getSku(),wareHouse.getId(),wareHouse.getStockName(),timelyQ);
			 //添加一条入库记录
			inItem = new PsiInventoryInItem(inventory.getProductId(), inventory.getProductName(), inventory.getColorCode(), inventory.getCountryCode(), quantity, type, sku, inventoryIn, timelyQ,itemId,price);
		}else{
			inventory.setProductId(orderItem.getProduct().getId());
			inventory.setProductName(orderItem.getProductName());
			inventory.setColorCode(orderItem.getColorCode());
			inventory.setCountryCode(orderItem.getCountryCode());
			if("new".equals(type)){
				inventory.setNewQuantity(quantity);                             
				inventory.setSparesQuantity(0);
				inventory.setOfflineQuantity(0);
				timelyQ=inventory.getNewQuantity();
			}else if("spares".equals(type)){
				inventory.setSparesQuantity(quantity);                           
				inventory.setNewQuantity(0);
				inventory.setOfflineQuantity(0);
				timelyQ=inventory.getSparesQuantity();
			}else if("offline".equals(type)){
				inventory.setOfflineQuantity(quantity);                           
				inventory.setNewQuantity(0);
				inventory.setSparesQuantity(0);
				timelyQ=inventory.getOfflineQuantity();
			}
			inventory.setOldQuantity(0);
			inventory.setBrokenQuantity(0);
			inventory.setRenewQuantity(0);
			inventory.setWarehouse(wareHouse);
			inventory.setWarehouseName(wareHouse.getStockName());
			inventory.setUpdateDate(new Date());
			inventory.setSku(sku);
			psiInventoryDao.save(inventory);
			//添加一条仓库日志记录
			psiInventoryService.savelog("Purchase Storing",type,quantity,"",inventory.getColorCode(),inventory.getCountryCode(),inventory.getProductId(),inventory.getProductName(),wareHouse.getId(),relativeNumber,inventory.getSku(),wareHouse.getId(),wareHouse.getStockName(),timelyQ);
			 //添加一条入库记录
			inItem = new PsiInventoryInItem(inventory.getProductId(), inventory.getProductName(), inventory.getColorCode(), inventory.getCountryCode(), quantity, type, sku, inventoryIn, timelyQ,itemId,price);
		}
		return inItem;
	}
	
	
	public String ajaxSkuValidate(String productNames){
		StringBuffer  sb = new StringBuffer("");
		String[] allProduct =productNames.split(",");
		for(String singleProduct:allProduct){
			if(StringUtils.isEmpty(singleProduct)){
				continue;
			}
			String [] arr= singleProduct.split("\\|");
			String productName=arr[0];
			String countryCode=this.changeCountryToCom(arr[1]);
			String colorCode ="";
			if(arr.length>2){
				colorCode=arr[2];
			}else{
				colorCode="";
			}
			
			String sku ="";
			Stock  store =stockService.findByName("中国本地A");
			if(store==null){
				throw new RuntimeException("匹配不到仓库：："+"中国本地A");
			}
		
			//获取sku  并校验是否与库里的产品名  国家  颜色相同
			PsiSku psiSku =psiProductService.getSku(productName, countryCode, colorCode);
			if(psiSku==null){
				sb.append("产品："+productName+":::"+countryCode+":::"+colorCode+";;;没绑定使用条码,请联系销售人员绑定！");
			}else{
				sku=psiSku.getSku();
				PsiInventory inventory= psiInventoryService.findBySku(sku, store.getId());
				if(inventory!=null){
					//如果这次的sku的国家  平台   颜色  与库里有一个不同，说明：1，当前sku设置错误(让销售在前台重新设置当前产品的sku)；2，库里第一次设置已经错误(只能把库里的sku的产品、国家、颜色改成正确的)
					if(!inventory.getProductName().equals(productName)||!inventory.getCountryCode().equals(countryCode)||!inventory.getColorCode().equals(colorCode)){
						if("com".equals(countryCode)){
							countryCode="us";
						}       
						sb.append("当前库里的sku的产品名称、国家、颜色跟本次该sku对应的产品信息不同，请检查是销售人员本次sku绑定错误、还是第一次入库就是错误的："+productName+":::"+countryCode+":::"+colorCode+";;;");
					}
				}
			}
		}
		
		
		return sb.toString();
	}
	
	@Transactional(readOnly = false)
	public String  createSequenceNumber(String seqCodeName){
		return this.genSequenceDao.genSequence(seqCodeName,2);
	}
	
	@Transactional(readOnly = false)
	public void  updateLadingBillSeq(String billNo,Integer id){
		String sql ="update lc_psi_lading_bill set bill_no=:p1   where id=:p2";
		this.psiLadingBillDao.updateBySql(sql, new Parameter(billNo,id));
	}
	
	
	public List<Integer> getOrderIdByBillId(Set<Integer> billIds){
		String sql ="SELECT DISTINCT b.`purchase_order_id` FROM lc_psi_lading_bill_item AS a,lc_psi_purchase_order_item AS b WHERE a.`purchase_order_item_id`=b.`id` AND  a.`lading_bill_id` IN :p1 ";
		return this.psiLadingBillDao.findBySql(sql, new Parameter(billIds));
	}


	
	/**
	 *根据itemId获取list信息 
	 */
	public List<LcPsiLadingBillItem> getLadingBillItems(Set<Integer> ids){
		DetachedCriteria dc = this.psiLadingBillItemDao.createDetachedCriteria();
		dc.add(Restrictions.in("id", ids));
		return psiLadingBillItemDao.find(dc);
	}
	
	/**
	 *每天计算付款延时天数 
	 */
	@Transactional(readOnly = false)
	public void updatePayDelayDays(){
		//查供应商表，看几天算延时
		Date today = new Date();
		Map<Integer,PsiSupplier> supplierMap = Maps.newHashMap();
		List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
		for(PsiSupplier su:suppliers){
			supplierMap.put(su.getId(),su);
		}
		String  sql="SELECT a.`create_date`,a.`supplier_id`,b.`id`,b.`balance_delay1`,b.`balance_delay2`,b.`balance_rate1`,b.`balance_rate2`,b.`total_payment_amount` AS payAmount,b.`total_amount`*b.`balance_rate1`/100 AS parts1 FROM lc_psi_lading_bill AS a ,lc_psi_lading_bill_item AS b WHERE a.id=b.`lading_bill_id`" +
				" AND b.`total_amount`>b.`total_payment_amount` AND b.`del_flag`='0' AND a.`bill_sta`!='2'";
		List<Object[]> list=this.psiLadingBillDao.findBySql(sql);
		for(Object[] obj:list){
			Date createDate = (Date)obj[0];
			Integer itemId = Integer.parseInt(obj[2].toString());
			int tempSupplierId = Integer.parseInt(obj[1].toString());
			PsiSupplier supplier = supplierMap.get(tempSupplierId);
			Integer delayBalance1 =supplier.getBalanceDelay1();
			Integer delayBalance2 =supplier.getBalanceDelay2();
			Integer rate1=Integer.parseInt(obj[5].toString());
			Integer rate2=Integer.parseInt(obj[6].toString());
			Integer delay1=Integer.parseInt(obj[3].toString());
			Integer delay2=Integer.parseInt(obj[4].toString());
			Integer space =Integer.parseInt(DateUtils.spaceDays(createDate, new Date())+"");
			//如果尾款不分批,只更新第一个延迟1
			if(rate1.intValue()==100){
				//如果为丽锦，这个月的，下个月1号就过期
				/**
				 *1 UTK(1)的，取下下个月的10号为支付日期(分批)
				 *2、YLC(2)出货时付款75%，当月出乎下下月10号付清(分批)
				 *3、LIJ(12)   当月出货，下月30之前付清
				 *4、ULI(51)优耐当月出货，下月10之前付清
				 *5、XHT(9)新华天 当月出货，下月30之前付清
				 */
				String payType=supplier.getPayType();
				if(StringUtils.isNotEmpty(payType)){
					delay1=getDays(payType, today, createDate);
				}else{
					if(space>delayBalance1){
						delay1=space-delayBalance1;
					}
				}
			}else{
				//如果付款金额高于第一批价格，第一个不更新
				if(Float.parseFloat(obj[7].toString())<=Float.parseFloat(obj[8].toString())){
					if(space>delayBalance1){
						delay1=space-delayBalance1;
					}
				}
				//如果为UTK,第二笔到下下个月1号才算延迟
				if(tempSupplierId==1||tempSupplierId==2){
					Date firstDay=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 2)),10);
					if(today.after(firstDay)){
						delay2=Integer.parseInt(DateUtils.spaceDays(firstDay,today)+"");
					}
				}else{
					if(space>delayBalance2){
						delay2=space-delayBalance2;
					}
				}
				
			}
			
			String updateSql="UPDATE lc_psi_lading_bill_item AS a SET a.`balance_delay1`=:p1,a.`balance_delay2`=:p2  WHERE a.`id`=:p3";
			this.psiLadingBillDao.updateBySql(updateSql, new Parameter(delay1,delay2,itemId));
		}
	}
	
	
	public Integer getDays(String payType,Date today,Date createDate){
		Integer delay1=0;
		if("1".equals(payType)){
			Date firstDay=DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 1));
			if(today.after(firstDay)){
				delay1=Integer.parseInt(DateUtils.spaceDays(firstDay,today)+"");
			}
		}else if("2".equals(payType)){
			Date firstDay=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 1)),4);
			if(today.after(firstDay)){
				delay1=Integer.parseInt(DateUtils.spaceDays(firstDay,today)+"");
			}
		}else if("3".equals(payType)){
			Date firstDay=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 1)),9);
			if(today.after(firstDay)){
				delay1=Integer.parseInt(DateUtils.spaceDays(firstDay,today)+"");
			}
		}else if("4".equals(payType)){
			Date firstDay=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 1)),14);
			if(today.after(firstDay)){
				delay1=Integer.parseInt(DateUtils.spaceDays(firstDay,today)+"");
			}
		}else if("5".equals(payType)){
			Date firstDay=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 1)),19);
			if(today.after(firstDay)){
				delay1=Integer.parseInt(DateUtils.spaceDays(firstDay,today)+"");
			}
		}else if("6".equals(payType)){
			Date firstDay=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 1)),24);
			if(today.after(firstDay)){
				delay1=Integer.parseInt(DateUtils.spaceDays(firstDay,today)+"");
			}
		}else if("11".equals(payType)){
			Date firstDay=DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 2));
			if(today.after(firstDay)){
				delay1=Integer.parseInt(DateUtils.spaceDays(firstDay,today)+"");
			}
		}else if("12".equals(payType)){
			Date firstDay=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 2)),4);
			if(today.after(firstDay)){
				delay1=Integer.parseInt(DateUtils.spaceDays(firstDay,today)+"");
			}
		}else if("13".equals(payType)){
			Date firstDay=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 2)),9);
			if(today.after(firstDay)){
				delay1=Integer.parseInt(DateUtils.spaceDays(firstDay,today)+"");
			}
		}else if("14".equals(payType)){
			Date firstDay=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 2)),14);
			if(today.after(firstDay)){
				delay1=Integer.parseInt(DateUtils.spaceDays(firstDay,today)+"");
			}
		}else if("15".equals(payType)){
			Date firstDay=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 2)),19);
			if(today.after(firstDay)){
				delay1=Integer.parseInt(DateUtils.spaceDays(firstDay,today)+"");
			}
		}else if("16".equals(payType)){
			Date firstDay=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 2)),24);
			if(today.after(firstDay)){
				delay1=Integer.parseInt(DateUtils.spaceDays(firstDay,today)+"");
			}
		}
		
		return delay1;
	}
	
	
	public Date getPayDays(String payType,Date createDate){
		Date payDate=null;
		if("1".equals(payType)){
			payDate=DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 1));
		}else if("2".equals(payType)){
			payDate=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 1)),4);
		}else if("3".equals(payType)){
			payDate=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 1)),9);
		}else if("4".equals(payType)){
			payDate=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 1)),14);
		}else if("5".equals(payType)){
			payDate=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 1)),19);
		}else if("6".equals(payType)){
			payDate=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 1)),24);
		}else if("11".equals(payType)){
			payDate=DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 2));
		}else if("12".equals(payType)){
			payDate=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 2)),4);
		}else if("13".equals(payType)){
			payDate=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 2)),9);
		}else if("14".equals(payType)){
			payDate=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 2)),14);
		}else if("15".equals(payType)){
			payDate=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 2)),19);
		}else if("16".equals(payType)){
			payDate=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 2)),24);
		}
		
		return payDate;
	}
	
	
	public Map<String,Map<String,Integer>> getMapRate(){
		Map<String,Map<String,Integer>> map=new HashMap<String,Map<String,Integer>>();//类型-供应商-计数
		Date today =DateUtils.addMonths(new Date(), -1);
		LcPurchaseOrder purchaseOrder=new LcPurchaseOrder();
		purchaseOrder.setCreateDate(DateUtils.getFirstDayOfMonth(today));
        purchaseOrder.setPurchaseDate(DateUtils.getLastDayOfMonth(today));
		List<LcPurchaseOrder> list=purchaseOrderService.exp(purchaseOrder); 
		for(LcPurchaseOrder purchaseOrderTrue: list){
	    List<LcPurchaseOrder> tempOrders=purchaseOrderTrue.getTempOrders();
	    	for(int i =0;i<tempOrders.size();i++){
				LcPurchaseOrder tempOrder = tempOrders.get(i);
				List<LcPurchaseOrderItem> orderItems = tempOrder.getItems();
				Date   deliveredDate=null;
				Map<String,String> itemQuantityMap  = Maps.newHashMap();
				Set<Integer> orderItemSet = Sets.newHashSet();
				Map<String,String>  colorMap = Maps.newHashMap();
				String remark ="";
                for(int j =0;j<orderItems.size();j++){
                	LcPurchaseOrderItem item = orderItems.get(j);
                	if(j==0){
                		deliveredDate=item.getDeliveryDate();
                	}
                	String quantitys=item.getQuantityOrdered()+","+item.getQuantityReceived()+","+item.getQuantityUnReceived();
                	itemQuantityMap.put(item.getCountryCode()+","+item.getColorCode(), quantitys);
                	
                	if(colorMap.get(item.getColorCode())!=null){
                		String[] auqArr=colorMap.get(item.getColorCode()).split(",");
                		quantitys=(item.getQuantityOrdered()+Integer.parseInt(auqArr[0]))+","+(item.getQuantityReceived()+Integer.parseInt(auqArr[1]))+","+(item.getQuantityUnReceived()+Integer.parseInt(auqArr[2]));
                	}
                	colorMap.put(item.getColorCode(), quantitys);
                	if(StringUtils.isNotEmpty(item.getRemark())){
                		remark= item.getRemark();
                	}
                	orderItemSet.add(item.getId());
                }
                for(Map.Entry<String,String> entry:colorMap.entrySet()){
                     String[] arr=entry.getValue().split(",");
                     if(Integer.parseInt(arr[2])==0){
                    	 Date firstDate =getFirstReceiveDate(orderItemSet);
        				 if(firstDate!=null){
        					String deliveryStatus="";
        					int nDay = (int) ((firstDate.getTime() - deliveredDate.getTime()) / (24 * 60 * 60 * 1000));
    						if(nDay>3){
    							//deliveryStatus="0";//逾期
    							if(remark.contains("新品")||remark.contains("付款延后")||remark.contains("暂缓")||remark.contains("包材延误")||remark.contains("包材送晚")||remark.contains("配件交期延误")||remark.contains("恰逢法定假期")||remark.contains("等待运输")||remark.contains("暂停")){
    	    						deliveryStatus="0";//非供应商逾期
    							}else{
    								deliveryStatus="3";//供应商逾期
    							}
    						}else if(nDay<-3){
    							deliveryStatus="1";//提前
        					}else{
        						deliveryStatus="2";//正常
        					}
    						String nikeName=tempOrder.getSupplier().getNikename();
    						Map<String,Integer> data=map.get(deliveryStatus);
    						if(data==null){
    							data= Maps.newHashMap();
    							map.put(deliveryStatus,data);
    						}
    						Integer amount=data.get(nikeName);
    						if(amount==null){
    							data.put(nikeName,1);
    						}else{
    						    data.remove(nikeName);
    						    data.put(nikeName,amount+1);
    						}
        				 }
                     }
                }
	    	}
	  }
		return map;
	}
	
	public Map<String,Map<String,String>> getMapRemark(){
		Map<String,Map<String,String>> mapRemark=new HashMap<String,Map<String,String>>();
		Date today =DateUtils.addMonths(new Date(), -1);
		LcPurchaseOrder purchaseOrder=new LcPurchaseOrder();
		purchaseOrder.setCreateDate(DateUtils.getFirstDayOfMonth(today));
        purchaseOrder.setPurchaseDate(DateUtils.getLastDayOfMonth(today));
		List<LcPurchaseOrder> list=purchaseOrderService.exp(purchaseOrder); 
		for(LcPurchaseOrder purchaseOrderTrue: list){
	    List<LcPurchaseOrder> tempOrders=purchaseOrderTrue.getTempOrders();
	    	for(int i =0;i<tempOrders.size();i++){
				LcPurchaseOrder tempOrder = tempOrders.get(i);
				List<LcPurchaseOrderItem> orderItems = tempOrder.getItems();
				Date   deliveredDate=null;
				Map<String,String> itemQuantityMap  = Maps.newHashMap();
				Set<Integer> orderItemSet = Sets.newHashSet();
				Map<String,String>  colorMap = Maps.newHashMap();
				String remark ="";
                for(int j =0;j<orderItems.size();j++){
                	LcPurchaseOrderItem item = orderItems.get(j);
                	if(j==0){
                		deliveredDate=item.getDeliveryDate();
                	}
                	String quantitys=item.getQuantityOrdered()+","+item.getQuantityReceived()+","+item.getQuantityUnReceived();
                	itemQuantityMap.put(item.getCountryCode()+","+item.getColorCode(), quantitys);
                	
                	if(colorMap.get(item.getColorCode())!=null){
                		String[] auqArr=colorMap.get(item.getColorCode()).split(",");
                		quantitys=(item.getQuantityOrdered()+Integer.parseInt(auqArr[0]))+","+(item.getQuantityReceived()+Integer.parseInt(auqArr[1]))+","+(item.getQuantityUnReceived()+Integer.parseInt(auqArr[2]));
                	}
                	colorMap.put(item.getColorCode(), quantitys);
                	if(StringUtils.isNotEmpty(item.getRemark())){
                		remark= item.getRemark();
                	}
                	orderItemSet.add(item.getId());
                }
                for(Map.Entry<String,String> entry:colorMap.entrySet()){
                     String[] arr=entry.getValue().split(",");
                     if(Integer.parseInt(arr[2])==0){
                    	 Date firstDate =getFirstReceiveDate(orderItemSet);
        				 if(firstDate!=null){
        					String deliveryStatus="";
        					int nDay = (int) ((firstDate.getTime() - deliveredDate.getTime()) / (24 * 60 * 60 * 1000));
    						if(nDay>3){
    							//deliveryStatus="0";//逾期
    							if(remark.contains("新品")||remark.contains("付款延后")||remark.contains("暂缓")||remark.contains("包材延误")||remark.contains("包材送晚")||remark.contains("配件交期延误")||remark.contains("恰逢法定假期")||remark.contains("等待运输")||remark.contains("暂停")){
    	    						deliveryStatus="0";//非供应商逾期
    							}else{
    								deliveryStatus="3";//供应商逾期
    							}
    						}else if(nDay<-3){
    							deliveryStatus="1";//提前
        					}else{
        						deliveryStatus="2";//正常
        					}
    						String nikeName=tempOrder.getSupplier().getNikename();
    						Map<String,String> dataRemark=mapRemark.get(deliveryStatus);
    						if(dataRemark==null){
    							dataRemark=Maps.newHashMap();
    							mapRemark.put(deliveryStatus, dataRemark);
    						}
    						String detail=dataRemark.get(nikeName);
    					    if(detail==null){
    					    	dataRemark.put(nikeName, remark);
    					    }
        				 }
                     }
                }
	    	}
	  }
		return mapRemark;
	}
	
	
	public List<Object[]> getExciptionPurchaseOrder(){
		
		String sql="SELECT t.id,c.`is_new`,s.name,p.`nikename`,CONCAT(t.product_name,CASE WHEN t.`color_code`!='' THEN CONCAT ('_',t.`color_code`) ELSE '' END) productName,t.`delivery_date`,t.`actual_delivery_date`,0 expDay,d.`order_no`,t.`country_code`,d.id orderId,0 typeRemark,0 firstDDay,t.`remark`,t.`update_date` "+
          " FROM lc_psi_purchase_order d "+
          " JOIN lc_psi_purchase_order_item t ON d.id=t.`purchase_order_id` and t.del_flag='0' "+
          " JOIN sys_user s ON d.`merchandiser`=s.id "+
          " JOIN psi_supplier p ON p.id=d.`supplier_id` "+
          //" JOIN psi_product c ON CONCAT(brand,' ',model)=t.`product_name` "+ 分平台和颜色区分新品(psi_product_eliminate表代替psi_product)
          " JOIN psi_product_eliminate c ON c.`product_name`=t.`product_name` AND c.`color`=t.`color_code` AND c.`country`=t.`country_code`" +
          " WHERE d.`order_sta`!='1' AND  d.`order_sta`!='6'" +
          //" and DATE_FORMAT(t.`delivery_date`,'%Y-%m')>=DATE_FORMAT((DATE_SUB(NOW(),INTERVAL 1 MONTH)),'%Y-%m') and DATE_FORMAT(t.`delivery_date`,'%Y-%m')< NOW() "+
         " AND t.`delivery_date` BETWEEN   DATE_SUB(DATE_SUB(DATE_FORMAT(NOW(),'%y-%m-%d'),INTERVAL EXTRACT( DAY FROM NOW())-1 DAY),INTERVAL 1 MONTH) AND DATE_SUB(DATE_SUB(DATE_FORMAT(NOW(),'%y-%m-%d'),INTERVAL EXTRACT(DAY FROM NOW()) DAY),INTERVAL 0 MONTH) "+
          " and NOT EXISTS (SELECT 1 FROM lc_psi_purchase_order_item r WHERE d.id=r.`purchase_order_id` and r.remark like '%已处理%') order by s.name,p.nikename ";
		
		List<Object[]> realList=Lists.newArrayList();
		List<Object[]>  list=psiLadingBillDao.findBySql(sql);
		for (Object[] obj : list) {
			Set<Integer> orderItemSet = Sets.newHashSet();
			orderItemSet.add(Integer.parseInt(obj[0].toString()));
			Object[] ladingDateArr =this.getFinishedReceiveDate(orderItemSet);
			Date testDate = (ladingDateArr!=null&&ladingDateArr[0]!=null)?(Date)ladingDateArr[0]:null;
			Date firstDate = (ladingDateArr!=null&&ladingDateArr[1]!=null)?(Date)ladingDateArr[1]:null;
			Date deliveredDate=(Date)obj[5];
			Date actualDeliveredDate=(Date)obj[6];
			//不用预计收货时间比较，用可收货日期比较
			if(testDate!=null){
				actualDeliveredDate=testDate;
				obj[14]=new SimpleDateFormat("yyyy-MM-dd").format(testDate);
			}else{
				obj[14]="";
			}
			if(firstDate!=null){
				obj[12]=new SimpleDateFormat("yyyy-MM-dd").format(firstDate.getTime());
				int nDay = (int) ((firstDate.getTime() - deliveredDate.getTime()) / (24 * 60 * 60 * 1000));
				if("1".equals(obj[1].toString())){//新品
					if(nDay>=14){
						obj[7]=nDay;
						realList.add(obj);
						obj[11]="1";
					}else if(actualDeliveredDate!=null&&deliveredDate.getTime()<actualDeliveredDate.getTime()){
						nDay = (int) ((actualDeliveredDate.getTime() - deliveredDate.getTime()) / (24 * 60 * 60 * 1000));
						obj[7]=nDay;
						realList.add(obj);
						obj[11]="3";
					}
				}else if("0".equals(obj[1].toString())){
					if(nDay>=7){
						obj[7]=nDay;
						realList.add(obj);
						obj[11]="0";
					}else if(actualDeliveredDate!=null&&deliveredDate.getTime()<actualDeliveredDate.getTime()){
						nDay = (int) ((actualDeliveredDate.getTime() -deliveredDate.getTime() ) / (24 * 60 * 60 * 1000));
						obj[7]=nDay;
						realList.add(obj);
						obj[11]="3";
					}
				}
			}else{
				obj[12]="";
				int nDay = (int) ((new Date().getTime()- deliveredDate.getTime()) / (24 * 60 * 60 * 1000));
				if("1".equals(obj[1].toString())){//新品
					if(nDay>=14){
						obj[7]=nDay;
						realList.add(obj);
						obj[11]="1";
					}else if(actualDeliveredDate!=null&&deliveredDate.getTime()<actualDeliveredDate.getTime()){
						nDay = (int) ((actualDeliveredDate.getTime() - deliveredDate.getTime()) / (24 * 60 * 60 * 1000));
						obj[7]=nDay;
						realList.add(obj);
						obj[11]="3";
					}
				}else if("0".equals(obj[1].toString())){
					if(nDay>=7){
						obj[7]=nDay;
						realList.add(obj);
						obj[11]="0";
					}else if(actualDeliveredDate!=null&&deliveredDate.getTime()<actualDeliveredDate.getTime()){
						nDay = (int) ((actualDeliveredDate.getTime() -deliveredDate.getTime() ) / (24 * 60 * 60 * 1000));
						obj[7]=nDay;
						realList.add(obj);
						obj[11]="3";
					}
				}
				
			}
			
			
		}
		
		return realList;
	}
	
	public Date isExistLadingBill(String productName,Float price){
		String sql=" SELECT MIN(sure_date) FROM lc_psi_lading_bill_item a "+
				" JOIN  lc_psi_lading_bill b ON b.id=a.lading_bill_id "+
				"  WHERE a.`del_flag`='0' AND b.`del_flag`='0' AND sure_date IS NOT NULL and CONCAT(a.product_name,CASE WHEN a.`color_code`!='' THEN CONCAT ('_',a.`color_code`) ELSE '' END)=:p1 "+
				" and TRUNCATE( CASE WHEN b.`currency_type`='CNY' THEN item_price/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+"  ELSE item_price END ,2)=:p2  group by CONCAT(a.product_name,CASE WHEN a.`color_code`!='' THEN CONCAT ('_',a.`color_code`) ELSE '' END) ";
		List<Object>  list=psiLadingBillDao.findBySql(sql,new Parameter(productName,price));
		if(list!=null&&list.size()>0&&list.get(0)!=null){
			return (Timestamp)list.get(0);
		}
		return null;
	}
	
	
	/***
	 *获取未付完款    提单信息   
	 * return [供应商id[预期日期,金额]
	 * 如果当前预期的key：over
	 */
	public Map<Integer,Map<String,Float>> getUnPaymentLadingItem(Integer supplier,Map<Integer,PsiSupplier> supplierMap,Map<Integer,Float> depositMap){
		//查询供应商逾期日期  findAllMap；
		Map<Integer,Map<String,Float>> resMap=Maps.newHashMap();
		for(Map.Entry<Integer,Float> supplierIdEntry:depositMap.entrySet()){
			Integer supplierId=supplierIdEntry.getKey();
			if(depositMap.get(supplierId)!=null){
				Map<String,Float> overMap =Maps.newHashMap();
				overMap.put("over", depositMap.get(supplierId));
				resMap.put(supplierId, overMap);
			}
		}
		String getUnPaymentLading="";
		List<Object[]> list=null;
		if(supplier!=null){
			getUnPaymentLading="SELECT a.`create_date`,a.`supplier_id`,b.`balance_delay1`,b.`balance_delay2`,b.`balance_rate1`,b.`balance_rate2`,b.`total_amount`*(b.quantity_sure/b.quantity_lading) as totalAmount,b.`total_payment_amount`,b.`total_payment_pre_amount`,b.`total_amount`*b.`balance_rate1`/100 AS firstShouldPay,a.`currency_type` " +
					" FROM lc_psi_lading_bill AS a ,lc_psi_lading_bill_item AS b WHERE a.id=b.`lading_bill_id` AND a.`total_amount`>a.`total_payment_amount` AND b.`total_amount`*(b.quantity_sure/b.quantity_lading)>b.`total_payment_amount` " +
					" AND b.`del_flag`='0' AND a.`bill_sta` in ('1','5') AND a.`supplier_id`=:p1";
			list= this.psiLadingBillDao.findBySql(getUnPaymentLading,new Parameter(supplier));
		}else{
			getUnPaymentLading="SELECT a.`create_date`,a.`supplier_id`,b.`balance_delay1`,b.`balance_delay2`,b.`balance_rate1`,b.`balance_rate2`,b.`total_amount`*(b.quantity_sure/b.quantity_lading) as totalAmount,b.`total_payment_amount`,b.`total_payment_pre_amount`,b.`total_amount`*b.`balance_rate1`/100 AS firstShouldPay,a.`currency_type` " +
					" FROM lc_psi_lading_bill AS a ,lc_psi_lading_bill_item AS b WHERE a.id=b.`lading_bill_id` AND a.`total_amount`>a.`total_payment_amount` AND b.`total_amount`*(b.quantity_sure/b.quantity_lading)>b.`total_payment_amount` " +
			" AND b.`del_flag`='0' AND a.`bill_sta` in ('1','5') ";
			list= this.psiLadingBillDao.findBySql(getUnPaymentLading);
		}
			
		DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
		for(Object[] obj:list){
			Date createDate = (Date)obj[0];
			String  currency = obj[10].toString();
			Integer  supplierId = Integer.parseInt(obj[1].toString());
			Integer delay1=Integer.parseInt(obj[2].toString());
			Integer delay2=Integer.parseInt(obj[3].toString());
			Integer rate1=Integer.parseInt(obj[4].toString());
			Integer rate2=Integer.parseInt(obj[5].toString());
			Float   totalAmount = Float.parseFloat(obj[6].toString());
			Float   payAmount = Float.parseFloat(obj[7].toString());
			Float   prePayAmount = Float.parseFloat(obj[8].toString());
			Float   firstAmount  = Float.parseFloat(obj[9].toString());
			
			Float   upPayment = totalAmount-payAmount;
			if(upPayment.intValue()==0){
				continue;
			}
			Map<String,Float> overMap =null;
			if(resMap.get(supplierId)==null){
				overMap =Maps.newHashMap();
			}else{
				overMap=resMap.get(supplierId);
			}
			Float overAmount=0f;
			Float unOverAmount=0f;
			if(overMap.get("over")!=null){
				overAmount=overMap.get("over");
			}
			String weekStr="";
			PsiSupplier su=supplierMap.get(supplierId);
			if(rate1.intValue()==100){
				if(delay1>0){
					//已逾期
					overMap.put("over", overAmount+upPayment);
				}else{
					//未预期  算出将来预期时间点，精确到周，  以备统计未来12周的金额
					if(StringUtils.isNotEmpty(su.getPayType())){
						weekStr=DateUtils.getWeekStr(getPayDays(supplierMap.get(supplierId).getPayType(), createDate), formatWeek, 5, "-");
					}else{
						weekStr=DateUtils.getWeekStr(DateUtils.addDays(createDate,supplierMap.get(supplierId).getBalanceDelay1()), formatWeek, 5, "-");
					}
					
					if(overMap.get(weekStr)!=null){
						unOverAmount=overMap.get(weekStr);
					}
					overMap.put(weekStr, unOverAmount+upPayment);
				}
			}else if(rate2.intValue()==100){//一般不会出现
				if(delay2>0){
					//已逾期
					overMap.put("over", overAmount+upPayment);
				}else{
					if(StringUtils.isNotEmpty(su.getPayType())){
						weekStr=DateUtils.getWeekStr(getPayDays(supplierMap.get(supplierId).getPayType(), createDate), formatWeek, 5, "-");
					}else{
						weekStr=DateUtils.getWeekStr(DateUtils.addDays(createDate,supplierMap.get(supplierId).getBalanceDelay2()), formatWeek, 5, "-");
					}
					if(overMap.get(weekStr)!=null){
						unOverAmount=overMap.get(weekStr);
					}
					overMap.put(weekStr, unOverAmount+upPayment);
				}
			}else{
				if(payAmount>=firstAmount){
					//如果付款金额大于分批第一次金额，看第二笔逾期了没
					if(delay2>0){
						//已逾期
						overMap.put("over", overAmount+upPayment);
					}else{
						//如果是UTK，YLC从提单创建起，下下月10号为付款日期
						if(supplierId.intValue()==1||supplierId.intValue()==2){
							Date firstDay=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 2)),10);
							weekStr=DateUtils.getWeekStr(firstDay, formatWeek, 5, "-");
						}else{
							weekStr=DateUtils.getWeekStr(DateUtils.addDays(createDate,supplierMap.get(supplierId).getBalanceDelay2()), formatWeek, 5, "-");
						}
						if(overMap.get(weekStr)!=null){
							unOverAmount=overMap.get(weekStr);
						}
						overMap.put(weekStr, unOverAmount+upPayment);
					}
				}else{
					//第一笔都没付完
					if(delay1>0){
						//已逾期
						if(overMap.get("over")==null){
							overMap.put("over", firstAmount-payAmount);
						}else{
							overMap.put("over", overMap.get("over")+firstAmount-payAmount);
						}
					}else{
						//如果是UTK，YLC从提单创建起，
						if(supplierId.intValue()==1||supplierId.intValue()==2){
							Date firstDay=createDate;
							weekStr=DateUtils.getWeekStr(firstDay, formatWeek, 5, "-");
						}else{
							weekStr=DateUtils.getWeekStr(DateUtils.addDays(createDate,supplierMap.get(supplierId).getBalanceDelay1()), formatWeek, 5, "-");
						}
						Float overAmountTemp=0f;
						if(overMap.get(weekStr)!=null){
							overAmountTemp+=overMap.get(weekStr);
						}
						overAmountTemp+=firstAmount-payAmount;
						overMap.put(weekStr, overAmountTemp);
					}
					
					if(delay2>0){
						if(overMap.get("over")==null){
							overMap.put("over", totalAmount-firstAmount);
						}else{
							overMap.put("over", overMap.get("over")+totalAmount-firstAmount);
						}
					}else{
						//如果是UTK，YLC从提单创建起，下下月五号为付款日期
						if(supplierId.intValue()==1||supplierId.intValue()==2){
							Date firstDay=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(createDate, 2)),10);
							weekStr=DateUtils.getWeekStr(firstDay, formatWeek, 5, "-");
						}else{
						//其他的，根据尾款分批，第二批延迟日期，推算付款日期
							weekStr=DateUtils.getWeekStr(DateUtils.addDays(createDate,supplierMap.get(supplierId).getBalanceDelay2()), formatWeek, 5, "-");
						}
						Float overAmountTemp=0f;
						if(overMap.get(weekStr)!=null){
							overAmountTemp+=overMap.get(weekStr);
						}
						overAmountTemp+=totalAmount-firstAmount;
						overMap.put(weekStr, overAmountTemp);
					}
				}
			}
			resMap.put(supplierId, overMap);
		}
		return resMap;
	}
	
	
	
	/***
	 *获取未付完款    提单信息   已目前已收货未付款30天算，不可能有6周的
	 * return 供应商id：{预期日期：金额}；如果当前预期的key：over
	 * 
	 */
	public Map<Integer,Map<String,Float>> getUnPaymentLadingItemOver(Map<Integer,PsiSupplier> supplierMap,Map<Integer,Map<String,Float>> depositMap){
		String getUnPaymentLading="SELECT a.`create_date`,a.`supplier_id`,b.`balance_delay1`,b.`balance_delay2`,b.`balance_rate1`,b.`balance_rate2`,b.`total_amount`*(b.quantity_sure/b.quantity_lading) as totalAmount,b.`total_payment_amount`,b.`total_payment_pre_amount`,b.`total_amount`*b.`balance_rate1`/100 AS firstShouldPay,a.`currency_type` " +
					" FROM lc_psi_lading_bill AS a ,lc_psi_lading_bill_item AS b WHERE a.id=b.`lading_bill_id` AND a.`total_amount`>a.`total_payment_amount` " +
					" AND  b.`total_amount`*(b.quantity_sure/b.quantity_lading)>b.`total_payment_amount`  " +
					" AND b.`del_flag`='0' AND a.`bill_sta`!='2' AND (b.`balance_delay1`>0 OR b.`balance_delay2`>0) ";
		List<Object[]> list= this.psiLadingBillDao.findBySql(getUnPaymentLading);
		for(Object[] obj:list){
			Date createDate = (Date)obj[0];
			Integer  supplierId = Integer.parseInt(obj[1].toString());
			Integer delay1=Integer.parseInt(obj[2].toString());
			Integer delay2=Integer.parseInt(obj[3].toString());
			Integer rate1=Integer.parseInt(obj[4].toString());
			Integer rate2=Integer.parseInt(obj[5].toString());
			Float   totalAmount = Float.parseFloat(obj[6].toString());
			Float   payAmount = Float.parseFloat(obj[7].toString());
			Float   prePayAmount = Float.parseFloat(obj[8].toString());
			Float   firstAmount  = Float.parseFloat(obj[9].toString());
			String  currency = obj[10].toString();
			
			Float   upPayment = totalAmount-payAmount;
			Map<String,Float> inMap =null;
			if(depositMap.get(supplierId)==null){
				inMap=Maps.newHashMap();
			}else{
				inMap=depositMap.get(supplierId);
			}
			PsiSupplier supplier = supplierMap.get(supplierId);
			Integer overDay1 = supplier.getBalanceDelay1();
			Integer overDay2 = supplier.getBalanceDelay2();
			
			if(rate1.intValue()==100){
				if(delay1>0){
					String month = DateUtils.getDate(DateUtils.addDays(createDate, overDay1), "yyyy-MM");
					if(inMap.get("total")==null){
						inMap.put("total",upPayment);
					}else{
						inMap.put("total",inMap.get("total")+upPayment);
					}
					if(inMap.get(month)==null){
						inMap.put(month, upPayment);
					}else{
						inMap.put(month, inMap.get(month)+upPayment);
					}
				}
			}else if(rate2.intValue()==100){//一般不会出现
				if(delay2>0){
					String month = DateUtils.getDate(DateUtils.addDays(createDate, overDay2), "yyyy-MM");
					if(inMap.get("total")==null){
						inMap.put("total",upPayment);
					}else{
						inMap.put("total",inMap.get("total")+upPayment);
					}
					if(inMap.get(month)==null){
						inMap.put(month, upPayment);
					}else{
						inMap.put(month, inMap.get(month)+upPayment);
					}
				}
			}else{
			//	if(payAmount+prePayAmount>=firstAmount){
				if(payAmount>=firstAmount){
					//如果付款金额大于分批第一次金额
					if(delay2>0){
						//考虑供应商的付款时间
						String month = DateUtils.getDate(DateUtils.addDays(createDate, overDay2), "yyyy-MM");
						if(inMap.get("total")==null){
							inMap.put("total",upPayment);
						}else{
							inMap.put("total",inMap.get("total")+upPayment);
						}
						if(inMap.get(month)==null){
							inMap.put(month, upPayment);
						}else{
							inMap.put(month, inMap.get(month)+upPayment);
						}
					}
				}else{
					//第一笔都没付完
					if(delay1>0){
						String month = DateUtils.getDate(DateUtils.addDays(createDate, overDay1), "yyyy-MM");
						if(inMap.get("total")==null){
							inMap.put("total",firstAmount-payAmount);
						}else{
							inMap.put("total",inMap.get("total")+firstAmount-payAmount);
						}
						
						if(inMap.get(month)==null){
							inMap.put(month, firstAmount-payAmount);
						}else{
							inMap.put(month, inMap.get(month)+firstAmount-payAmount);
						}
					}
					
					if(delay2>0){
						String month = DateUtils.getDate(DateUtils.addDays(createDate, overDay2), "yyyy-MM");
						if(inMap.get("total")==null){
							inMap.put("total",totalAmount-firstAmount);
						}else{
							inMap.put("total",inMap.get("total")+totalAmount-firstAmount);
						}
						
						if(inMap.get(month)==null){
							inMap.put(month, totalAmount-firstAmount);
						}else{
							inMap.put(month, inMap.get(month)+totalAmount-firstAmount);
						}
					}
				}
			}
			depositMap.put(supplierId, inMap);
		}
		return depositMap;
	}
	/**
	 *未付定金的产品单,
	 */
	public Map<Integer,Float> getUnpayDepositAmount(Integer supplier){
		String sql="";
		List<Object[]>  list=null;
		if(supplier!=null){
			sql="SELECT a.`supplier_id`,SUM(TRUNCATE(a.`order_total`*a.`deposit`/100-a.`deposit_amount`,2)) as dAmount FROM lc_psi_purchase_order AS a WHERE (a.`order_total`*a.`deposit`/100-a.`deposit_amount`)>0 AND a.`deposit`>0 AND a.`order_total`>0 AND a.`order_sta`in ('2','3','4') AND a.`supplier_id`=:p1 GROUP BY a.`supplier_id` ";
			list=psiLadingBillDao.findBySql(sql,new Parameter(supplier));
		}else{
			sql="SELECT a.`supplier_id`,SUM(TRUNCATE(a.`order_total`*a.`deposit`/100-a.`deposit_amount`,2)) as dAmount FROM lc_psi_purchase_order AS a WHERE (a.`order_total`*a.`deposit`/100-a.`deposit_amount`)>0 AND a.`deposit`>0 AND a.`order_total`>0 AND a.`order_sta`in ('2','3','4') GROUP BY a.`supplier_id` ";
			list=psiLadingBillDao.findBySql(sql);
		}
				
		Map<Integer,Float> rsMap=Maps.newHashMap();
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				Integer supplierId=Integer.parseInt(obj[0].toString());
				Float   upPay =Float.valueOf(obj[1].toString());
				rsMap.put(supplierId, upPay);
			}
		}
		return rsMap;
	}
	
	/**
	 *提单转化成入库单
	 */
	@Transactional( readOnly = false)
	public void toInventoryInBill(){
		List<LcPsiLadingBill> bills = fingAllBySta("5");
		for (LcPsiLadingBill bill : bills) {
			PsiInventoryIn  in =bill.toInventoryIn();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMddHHmmsss");
			String flowNo=sdf.format(new Date());
			String sureStr =sdf.format(bill.getSureDate());
			String inNo=sureStr.substring(0, 8)+"_RKD"+flowNo.substring(8);
			in.setBillNo(inNo);
			psiInventoryInDao.save(in);
		}
	}
	
	/**
	 *未付定金  分月
	 */
	public Map<Integer,Map<String,Float>> getUnpayDepositAmountOver(){
		Map<Integer,Map<String,Float>> rsMap = Maps.newHashMap();
		String sql="SELECT a.`supplier_id`,DATE_FORMAT(create_date,'%Y-%m'),SUM(TRUNCATE(a.`order_total`*a.`deposit`/100-a.`deposit_amount`,2)) as dAmount FROM lc_psi_purchase_order AS a WHERE (a.`order_total`*a.`deposit`/100-a.`deposit_amount`)>0 AND a.`deposit`>0 AND a.`order_sta`in ('2','3','4') GROUP BY a.`supplier_id`,DATE_FORMAT(create_date,'%Y-%m') having dAmount>0";
		List<Object[]>  list=psiLadingBillDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				Integer supplierId=Integer.parseInt(obj[0].toString());
			    String  month =obj[1].toString();
				Float   upPay =Float.valueOf(obj[2].toString());
				Map<String,Float> inMap = null;
				if(rsMap.get(supplierId)!=null){
					 inMap = rsMap.get(supplierId);
				}else{
					 inMap = Maps.newHashMap();
				}
				if(inMap.get("total")==null){
					inMap.put("total", upPay);
				}else{
					inMap.put("total", inMap.get("total")+upPay);
				}
				inMap.put(month, upPay);
				rsMap.put(supplierId, inMap);
			}
		}
		return rsMap;
	}

	
	/**
	 * 未收货产品价值
	 */
	public Map<Integer,Map<String,Float>> getUnReceivedAmount(Integer supplier,Map<Integer,PsiSupplier> supplierMap,Map<Integer,Float> payNoReceivedMap){
		Map<Integer,Map<String,Float>> rsMap =Maps.newHashMap();
		DateFormat formatWeek = new SimpleDateFormat("yyyy-ww");
		String sql="";
		List<Object[]>  list=null;
		if(supplier!=null){
			sql=" SELECT a.`supplier_id`,CASE WHEN  c.`delivery_date` IS NOT NULL THEN c.`delivery_date` ELSE b.`actual_delivery_date` END AS delieryDate,TRUNCATE(" +
					" (CASE WHEN  c.`delivery_date` IS NOT NULL THEN c.`quantity`-c.`quantity_received` ELSE b.`quantity_ordered`-b.`quantity_received` END)*b.`item_price`*(100-a.`deposit`)/100,2) AS upPay FROM lc_psi_purchase_order AS a," +
					" lc_psi_purchase_order_item AS b LEFT JOIN lc_psi_purchase_order_delivery_date AS c ON  b.`id`=c.`purchase_order_item_id` AND c.`del_flag`='0' AND (c.quantity-c.quantity_received) >0 " +
					" WHERE a.id=b.`purchase_order_id` AND a.`del_flag`='0' AND a.`order_sta`IN ('2','3','4') AND b.`del_flag`='0' AND b.`item_price` IS NOT NULL AND a.`supplier_id`=:p1 ";
			list=psiLadingBillDao.findBySql(sql,new Parameter(supplier));
		}else{
			sql=" SELECT a.`supplier_id`,CASE WHEN  c.`delivery_date` IS NOT NULL THEN c.`delivery_date` ELSE b.`actual_delivery_date` END AS delieryDate,TRUNCATE(" +
					" (CASE WHEN  c.`delivery_date` IS NOT NULL THEN c.`quantity`-c.`quantity_received` ELSE b.`quantity_ordered`-b.`quantity_received` END)*b.`item_price`*(100-a.`deposit`)/100,2) AS upPay FROM lc_psi_purchase_order AS a," +
					" lc_psi_purchase_order_item AS b LEFT JOIN lc_psi_purchase_order_delivery_date AS c ON  b.`id`=c.`purchase_order_item_id` AND c.`del_flag`='0' AND (c.quantity-c.quantity_received) >0  WHERE a.id=b.`purchase_order_id` AND a.`del_flag`='0'" +
					" AND a.`order_sta`IN ('2','3','4') AND b.`del_flag`='0' AND b.`item_price` IS NOT NULL ";
			list=psiLadingBillDao.findBySql(sql);
		}
		Date   today  = new Date();
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				Integer supplierId=Integer.parseInt(obj[0].toString());
				Date    deliveryDate =(Date)obj[1];
				//根据供应商尾款比例，推算第二笔尾款的付款日期
				PsiSupplier psiSupplier = supplierMap.get(supplierId);
				int tempSupplierId = psiSupplier.getId().intValue();
				Float   unPay =Float.valueOf(obj[2].toString());
				if(unPay.intValue()==0){
					continue;
				}
				Float  payPreReceivedAmount =payNoReceivedMap.get(supplierId);//未收货完成，已支付金额
				if(psiSupplier.getBalanceRate1()==100){//一次付完
					/**
					 *1 UTK(1)的，取下下个月的10号为支付日期
					 *2、YLC(2)出货时付款75%，当月出乎下下月10号付清
					 *3、LIJ(12)   当月出货，下月30之前付清
					 *4、ULI(51)优耐当月出货，下月10之前付清
					 *5、XHT(9)新华天 当月出货，下月30之前付清
					 */
					Date payDate =null;
					if(StringUtils.isNotEmpty(psiSupplier.getPayType())){
						payDate=getPayDays(psiSupplier.getPayType(), deliveryDate);
					}else{
						payDate=DateUtils.addDays(deliveryDate,psiSupplier.getBalanceDelay1());
					}
					//未来12周，如果超过100天就不算了
					if(DateUtils.spaceDays(today, payDate)>100){
						continue;
					}else if(DateUtils.spaceDays(today, payDate)<=0){
						payDate=today;
					}
					//如果收货日期大于当前日期，则当前周围付款周
					String weekStr=DateUtils.getWeekStr(payDate, formatWeek, 5, "-");
					
					Map<String,Float> dateMap =null;
					if(rsMap.get(supplierId)==null){
						dateMap=Maps.newHashMap();
					}else{
						dateMap=rsMap.get(supplierId);
					}
					
					//对未收货已付款的进行处理
					if(payPreReceivedAmount!=null&&payPreReceivedAmount>=0){
						payPreReceivedAmount=payPreReceivedAmount-unPay;
						if(payPreReceivedAmount>0){
							continue;
						}else{
							unPay=-payPreReceivedAmount;
						}
					}
					
					if(dateMap.get(weekStr)!=null){
						unPay+=dateMap.get(weekStr);
					}
					dateMap.put(weekStr, unPay);
					rsMap.put(supplierId, dateMap);
				}else if(psiSupplier.getBalanceRate2()==100){//一次付完
					Date payDate = DateUtils.addDays(deliveryDate,psiSupplier.getBalanceDelay2());
					//未来12周，如果超过100天就不算了
					if(DateUtils.spaceDays(today, payDate)>100){
						continue;
					}else if(DateUtils.spaceDays(today, payDate)<=0){
						payDate=today;
					}
					
					String weekStr=DateUtils.getWeekStr(payDate, formatWeek, 5, "-");
					
					Map<String,Float> dateMap =null;
					if(rsMap.get(supplierId)==null){
						dateMap=Maps.newHashMap();
					}else{
						dateMap=rsMap.get(supplierId);
					}
					
					//对未收货已付款的进行处理
					if(payPreReceivedAmount!=null&&payPreReceivedAmount>0){
						payPreReceivedAmount=payPreReceivedAmount-unPay;
						if(payPreReceivedAmount>=0){
							continue;
						}else{
							unPay=-payPreReceivedAmount;
						}
					}
					
					if(dateMap.get(weekStr)!=null){
						unPay+=dateMap.get(weekStr);
					}
					dateMap.put(weekStr, unPay);
					rsMap.put(supplierId, dateMap);
				}else{
					//分两次付款
					Float firstPay=unPay*psiSupplier.getBalanceRate1()/100;
					Date payDate = DateUtils.addDays(deliveryDate,psiSupplier.getBalanceDelay1());
					//未来12周，如果超过100天就不算了
					if(DateUtils.spaceDays(today, payDate)>100){
						continue;
					}else if(DateUtils.spaceDays(today, payDate)<=0){
						payDate=today;
					}
					
					Map<String,Float> dateMap =null;
					if(rsMap.get(supplierId)==null){
						dateMap=Maps.newHashMap();
					}else{
						dateMap=rsMap.get(supplierId);
					}
					
					String weekStr=DateUtils.getWeekStr(payDate, formatWeek, 5, "-");
					//对未收货已付款的进行处理
					if(payPreReceivedAmount!=null&&payPreReceivedAmount>0){
						payPreReceivedAmount=payPreReceivedAmount-firstPay;
						if(payPreReceivedAmount<0){
							firstPay=-payPreReceivedAmount;
							if(dateMap.get(weekStr)!=null){
								firstPay+=dateMap.get(weekStr);
							}
							dateMap.put(weekStr, firstPay);
							rsMap.put(supplierId, dateMap);
						}
					}else{
						if(dateMap.get(weekStr)!=null){
							firstPay+=dateMap.get(weekStr);
						}
						dateMap.put(weekStr, firstPay);
						rsMap.put(supplierId, dateMap);
					}
					
					
					//第二批  
					/**
					 *1 UTK(1)的，取下下个月的10号为支付日期                    (尾款分批)
					 *2、YLC(2)出货时付款75%，当月出乎下下月10号付清  (尾款分批)
					 *3、LIJ(12)   当月出货，下月30之前付清
					 *4、ULI(51)优耐当月出货，下月10之前付清
					 *5、XHT(9)新华天 当月出货，下月30之前付清
					 */
					if(tempSupplierId==1||tempSupplierId==2){
						payDate=DateUtils.addDays(DateUtils.getFirstDayOfMonth(DateUtils.addMonths(deliveryDate, 2)),10);
					}else{
						payDate=DateUtils.addDays(deliveryDate,psiSupplier.getBalanceDelay2());
					}
					
					if(DateUtils.spaceDays(today, payDate)>100){
						continue;
					}else if(DateUtils.spaceDays(today, payDate)<=0){
						payDate=today;
					}
					Float twoPay=unPay*psiSupplier.getBalanceRate2()/100;
					
					weekStr=DateUtils.getWeekStr(payDate, formatWeek, 5, "-");
					Map<String,Float> dateMap1 =null;
					if(rsMap.get(supplierId)==null){
						dateMap1=Maps.newHashMap();
					}else{
						dateMap1=rsMap.get(supplierId);
					}
					if(payPreReceivedAmount!=null&&payPreReceivedAmount>0){
						payPreReceivedAmount=payPreReceivedAmount-twoPay;
						if(payPreReceivedAmount<0){
							twoPay=-payPreReceivedAmount;
							if(dateMap1.get(weekStr)!=null){
								twoPay+=dateMap1.get(weekStr);
							}
							dateMap1.put(weekStr, twoPay);
							rsMap.put(supplierId, dateMap1);
						}
					}else{
						if(dateMap1.get(weekStr)!=null){
							twoPay+=dateMap1.get(weekStr);
						}
						dateMap1.put(weekStr, twoPay);
						rsMap.put(supplierId, dateMap1);
					}
				}
				payNoReceivedMap.put(supplierId, payPreReceivedAmount);
			}
		}
		return rsMap;
	}
	
	
	/***
	 *获取未确认收货，但是已经付款的
	 * 
	 */
	public Map<Integer,Float> getPayPreLadingItem(Integer supplierId){
		//查询供应商逾期日期  findAllMap；
		Map<Integer,Float> resMap=Maps.newHashMap();
		String getUnPaymentLading="";
		List<Object[]> list=null;
		if(supplierId!=null){
			getUnPaymentLading="SELECT a.`supplier_id`,(b.`total_payment_amount`-b.`quantity_sure`*b.`total_amount`/b.`quantity_lading`) AS hasPay,a.`currency_type` FROM lc_psi_lading_bill AS a ,lc_psi_lading_bill_item AS b WHERE a.id=b.`lading_bill_id` AND b.`del_flag`='0' AND a.`bill_sta` IN ('0','5')  AND b.`quantity_lading`>b.`quantity_sure`" +
					" AND a.`total_payment_amount`>0 AND b.`total_payment_amount`>0 AND (b.`total_payment_amount`-b.`quantity_sure`*b.`total_amount`/b.`quantity_lading`) >0 AND a.`supplier_id`=:p1 GROUP BY a.`supplier_id`";
			list= this.psiLadingBillDao.findBySql(getUnPaymentLading,new Parameter(supplierId));
		}else{
			getUnPaymentLading="SELECT a.`supplier_id`,(b.`total_payment_amount`-b.`quantity_sure`*b.`total_amount`/b.`quantity_lading`) AS hasPay,a.`currency_type` FROM lc_psi_lading_bill AS a ,lc_psi_lading_bill_item AS b WHERE a.id=b.`lading_bill_id` AND b.`del_flag`='0' AND a.`bill_sta` IN ('0','5') AND b.`quantity_lading`>b.`quantity_sure`" +
					" AND a.`total_payment_amount`>0 AND b.`total_payment_amount`>0 AND (b.`total_payment_amount`-b.`quantity_sure`*b.`total_amount`/b.`quantity_lading`) >0 GROUP BY a.`supplier_id`";
			list= this.psiLadingBillDao.findBySql(getUnPaymentLading);
		}
		//已逾期     未预期（未来六周）  未收货（未来六周）
		for(Object[] obj:list){
			Integer   supplier = Integer.parseInt(obj[0].toString());
			Float     payAmount = Float.parseFloat(obj[1].toString());
			resMap.put(supplier, payAmount);
		}
		return resMap;
	}
	
	/**
	 *查询所有待审核的产品 
	 */
	public List<String> getCanReviewProducts(Integer ladingId){
		String sql="SELECT CASE WHEN  a.`color_code`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END AS proName  " +
				"FROM lc_psi_lading_bill_item AS a WHERE a.`lading_bill_id`=:p1 AND a.del_flag='0' AND a.`is_test_over`='1'" +
				" GROUP BY a.`product_name` ,a.`color_code`";
		List<String> list= this.psiLadingBillDao.findBySql(sql,new Parameter(ladingId));
		return list;
	}
	
	
	/**
	 *查询某提单、某产品收货总数
	 */
	public Integer getTotalByProductsColor(Integer ladingId,String productNameColor){
		String sql="SELECT SUM(a.quantity_lading) " +
				"FROM lc_psi_lading_bill_item AS a WHERE a.`lading_bill_id`=:p1 AND a.del_flag='0' AND (CASE WHEN  a.`color_code`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color_code`) END)=:p2" ;
		List<Object> list= this.psiLadingBillDao.findBySql(sql,new Parameter(ladingId,productNameColor));
		if(list!=null&&list.size()>0&&list.get(0)!=null){
			return Integer.parseInt(list.get(0).toString());
		}
		return 0;
	}
	
	/**
	 *获取已质检接收数量                           未审核的，也要占用接收数
	 */
	public Integer getTotalTestByProductsColor(Integer ladingId,String productNameColor){
		String sql="SELECT SUM(CASE WHEN a.`deal_way` IS  NULL AND a.`is_ok`='0' THEN a.`total_quantity` ELSE a.`received_quantity` END ) FROM lc_psi_quality_test AS a WHERE a.`lading_id`=:p1 AND a.`test_sta`<>'8' AND  (CASE WHEN  a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color`) END)=:p2";
		List<Object> list= this.psiLadingBillDao.findBySql(sql,new Parameter(ladingId,productNameColor));
		if(list!=null&&list.size()>0&&list.get(0)!=null){
			return Integer.parseInt(list.get(0).toString());
		}
		return 0;
	}
	
	
	@Transactional( readOnly = false)
	public void managerReivewSave(String ids) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<Integer,Map<String,Integer>> ladingSendEmailMap= Maps.newHashMap();
		Map<Integer,Map<String,String>> remarkMap = Maps.newHashMap();
		Map<Integer,String> contentMap = Maps.newHashMap();
		Map<Integer,String> attfileMap = Maps.newHashMap();
		//查询所有能确认的质检产品
		List<LcPsiQualityTest> tests=null;
		if(StringUtils.isNotEmpty(ids)){
			tests=this.testService.getNoReviewTestById(ids);
			Date date = new Date();
			User user = UserUtils.getUser();
			for(LcPsiQualityTest test:tests){
				String content="<table width='80%' style='border-right:1px solid;border-bottom:1px solid;color:#666;font-family:宋体' cellpadding='0' cellspacing='0' >";
				content+="<tr style='background-repeat:repeat-x;height:30px; '>" +
						 "<td style='text-align:center;border-left:1px solid;border-top:1px solid;background-color:#D7FFEE;color:#666;width:100%' font-size='15px' colspan='2'><b>"+test.getProductNameColor()+"</b></th></tr>";
				content+="<tr style='background-repeat:repeat-x;height:30px; '>" +
						 "<td style='border-left:1px solid;border-top:1px solid;background-color:#B2B2B2;color:#666;width:15%'>基本信息</th>";
				content+="<td style='border-left:1px solid;border-top:1px solid;color:#666;width:85%'>"+
						 "质检日期:"+sdf.format(test.getCreateDate())+
						 "&nbsp;&nbsp;&nbsp;&nbsp;质检人:"+test.getCreateUser().getName()+
						 "</td></tr>";
				content+="<tr style='background-repeat:repeat-x;height:30px; '>" +
						 "<td style='border-left:1px solid;border-top:1px solid;background-color:#B2B2B2;color:#666;width:15%'>AQL</th>";
				content+="<td style='border-left:1px solid;border-top:1px solid;color:#666;width:85%'>"+(test.getAql()==null?"":test.getAql())+"</td></tr>";
				
				content+="<tr style='background-repeat:repeat-x;height:30px; '>" +
						 "<td style='border-left:1px solid;border-top:1px solid;background-color:#B2B2B2;color:#666;width:15%'>内观</th>";
				content+="<td style='border-left:1px solid;border-top:1px solid;color:#666;width:85%'>"+(test.getInView()==null?"":test.getInView())+"</td></tr>";
				
				content+="<tr style='background-repeat:repeat-x;height:30px; '>" +
						 "<td style='border-left:1px solid;border-top:1px solid;background-color:#B2B2B2;color:#666;width:15%'>功能</th>";
				content+="<td style='border-left:1px solid;border-top:1px solid;color:#666;width:85%'>"+(test.getFunction()==null?"":test.getFunction())+"</td></tr>";
				
				
				content+="<tr style='background-repeat:repeat-x;height:30px; '>" +
						 "<td style='border-left:1px solid;border-top:1px solid;background-color:#B2B2B2;color:#666;width:15%'>外观</th>";
				content+="<td style='border-left:1px solid;border-top:1px solid;color:#666;width:85%'>"+(test.getOutView()==null?"":test.getOutView())+"</td></tr>";
				
				content+="<tr style='background-repeat:repeat-x;height:30px; '>" +
						 "<td style='border-left:1px solid;border-top:1px solid;background-color:#B2B2B2;color:#666;width:15%'>包装</th>";
				content+="<td style='border-left:1px solid;border-top:1px solid;color:#666;width:85%'>"+(test.getPacking()==null?"":test.getPacking())+"</td></tr>";
				
				content+="<tr style='background-repeat:repeat-x;height:30px; '>" +
						 "<td style='border-left:1px solid;border-top:1px solid;background-color:#B2B2B2;color:#666;width:15%'>检验结果判定</th>";
				String  result="";
				if("0".equals(test.getIsOk())){
					result="不合格";
					if("0".equals(test.getDealWay())){
						result+="特采";
					}else if("2".equals(test.getDealWay())){
						result+="返工";
					}
				}else if("1".equals(test.getIsOk())){
					result="合格";
				}
				content+="<td style='border-left:1px solid;border-top:1px solid;color:#666;width:85%'>"+result+"</td></tr>";
				
				content+="<tr style='background-repeat:repeat-x;height:30px; '>" +
						 "<td style='border-left:1px solid;border-top:1px solid;background-color:#B2B2B2;color:#666;width:15%'>订单数</th>";
				content+="<td style='border-left:1px solid;border-top:1px solid;color:#666;width:85%'>"+(test.getTotalQuantity()==null?"":test.getTotalQuantity())+"</td></tr>";
				
				content+="<tr style='background-repeat:repeat-x;height:30px; '>" +
						 "<td style='border-left:1px solid;border-top:1px solid;background-color:#B2B2B2;color:#666;width:15%'>抽样数</th>";
				content+="<td style='border-left:1px solid;border-top:1px solid;color:#666;width:85%'>"+(test.getTestQuantity()==null?"":test.getTestQuantity())+"</td></tr>";
				
				
				content+="<tr style='background-repeat:repeat-x;height:30px; '>" +
						 "<td style='border-left:1px solid;border-top:1px solid;background-color:#B2B2B2;color:#666;width:15%'>接收数</th>";
				content+="<td style='border-left:1px solid;border-top:1px solid;color:#666;width:85%'>"+(test.getReceivedQuantity()==null?"":test.getReceivedQuantity())+"</td></tr>";
				
				content+="</table><br/>";
				Integer ladingId = test.getLadingId();
				
				if(contentMap.get(ladingId)!=null){
					content+=contentMap.get(ladingId);
				}
				contentMap.put(ladingId, content);
				
				String productColor = test.getProductNameColor();
				Integer receivedQuantity = test.getReceivedQuantity();
				Map<String,Integer> map = null;
				if(ladingSendEmailMap.get(ladingId)==null){
					map = Maps.newHashMap();
				}else{
					map = ladingSendEmailMap.get(ladingId);
				}
				
				if(map.get(productColor)!=null){
					receivedQuantity=receivedQuantity+map.get(productColor);
				}
				
				map.put(productColor, receivedQuantity);
				ladingSendEmailMap.put(ladingId, map);
				
				if("0".equals(test.getIsOk())||"2".equals(test.getIsOk())){//如果为不合格或部分合格记录备注
					String  dealWay="";
					if("0".equals(test.getDealWay())){
						dealWay="让步";
					}else if("1".equals(test.getDealWay())){
						dealWay="特采";
					}else{
						dealWay="返工";
					}
					Map<String,String> inMap = null;
					if(remarkMap.get(ladingId)==null){
						inMap = Maps.newHashMap();
					}else{
						inMap = remarkMap.get(ladingId);
					}
					String remark=("0".equals(test.getIsOk())?"不合格":"部分合格")+dealWay+"接收数："+test.getReceivedQuantity()+"<br/>";
					if(inMap.get(productColor)!=null){
						remark+=inMap.get(productColor);
					}
					inMap.put(productColor, remark);
					remarkMap.put(ladingId, inMap);
					
					
				}
				   
				String filePath="";
				if(StringUtils.isNotEmpty(test.getReportFile())){
					filePath+=test.getReportFile()+",";
				}
				
				if(StringUtils.isNotEmpty(test.getGiveInFile())){
					filePath+=test.getGiveInFile()+",";  
				}
				
				if(filePath.length()>0&&attfileMap.get(ladingId)!=null){
					filePath+=attfileMap.get(ladingId);
				}
				
				attfileMap.put(ladingId, filePath);
				
				test.setSureDate(date);
				test.setSureUser(user);
				test.setTestSta("5");//产品经理已审核
				this.testService.save(test);
			}
		}
		
		//根据品检信息及提单信息发信
		for(Map.Entry<Integer,Map<String,Integer>> entry: ladingSendEmailMap.entrySet()){
			Integer ladingId =entry.getKey();
			LcPsiLadingBill lcBill = get(ladingId);
			this.sendEmailToVendor(lcBill,entry.getValue(),remarkMap.get(ladingId),attfileMap.get(ladingId),contentMap);
		}
		
	}
	
	
	private void sendEmailToVendor(LcPsiLadingBill psiLadingBill,Map<String,Integer> canSendMap,Map<String,String> remarkMap,String filePaths,Map<Integer,String> contentMap) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SendEmail sendEmail = new SendEmail();
		String purchaseManagerEmails ="";
		Map<String,String> purchaseMap =this.psiProductService.findPurchaseEmailProductName();
		String billNo = psiLadingBill.getBillNo();
		Integer canSendTotal =0;
		for (Iterator<LcPsiLadingBillItem> iterator = psiLadingBill.getItems().iterator(); iterator.hasNext();) {
			LcPsiLadingBillItem item = (LcPsiLadingBillItem) iterator.next();
			//如果没有品检则移除，不发给供应商
			String proColor = item.getProductNameColor();
			Integer canQ = canSendMap.get(proColor);
			if(canQ==null){//如果这次审核的没这个产品不发送pdf
				iterator.remove();
				continue;
			}else{
				canSendTotal+=canQ;
			}
			
			String email =purchaseMap.get(item.getProductName());
			if(email!=null&&!purchaseManagerEmails.contains(email+",")){
				purchaseManagerEmails=email+",";
			}
		}
		
		
		// 往采购供应商发信 获取供应商模板
		Map<String, Object> params = Maps.newHashMap();
		String template = "";
		try {
			params.put("Cname", UserUtils.getUser().getName());
			params.put("Cemail", UserUtils.getUser().getEmail());
			String content ="";
			
			if(canSendTotal>0){
				content="请查看附件质检报告及提货单中出货产品及数量，申请尾款时请告知对应的提货单号!<br/><b>(noreply为系统自动发送的邮箱，请不要回复到该邮箱！)</b><br/>";
			}else{
				content="<span style='color:red' >请查看附件质检报告并和我司品检联系及时改善!</span><br/>";
			}
			params.put("content", content+contentMap.get(psiLadingBill.getId()));
			template = PdfUtil.getPsiTemplate("lcLadingEmail.ftl", params);
		} catch (Exception e) {
		}
		sendEmail.setSendContent(template);
		sendEmail.setSendEmail(psiLadingBill.getSupplier().getMail());
		if(canSendTotal>0){
			sendEmail.setSendSubject("验货质检报告及提单["+billNo+"]明细"+sdf.format(new Date()));
		}else{
			sendEmail.setSendSubject(psiLadingBill.getSupplier().getNikename()+"验货质检报告"+sdf.format(new Date()));
		}
		
		
		sendEmail.setCreateBy(UserUtils.getUser());
		
		
		//发送邮件
		String flowUserEmail = UserUtils.getUserById(psiLadingBill.getCreateUser().getId()).getEmail();
		String bccEmail ="";
		if(canSendTotal>0){
			bccEmail="maik@inateck.com,emma.chao@inateck.com,penny@inateck.com,"+UserUtils.getUser().getEmail()+","+flowUserEmail;
		}else{
			bccEmail="maik@inateck.com,emma.chao@inateck.com,"+UserUtils.getUser().getEmail()+","+flowUserEmail;
		}
		 
		if(StringUtils.isNotEmpty(purchaseManagerEmails)){
			bccEmail=bccEmail+(","+purchaseManagerEmails.substring(0, purchaseManagerEmails.length()-1));
		}
		sendEmail.setBccToEmail(bccEmail);

		// 发送邮件
		final MailInfo mailInfo = sendEmail.getMailInfo();
		if (filePath == null) {
			filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/ladingBills";
		}
		
		if(canSendTotal>0){
			File file = new File(filePath, psiLadingBill.getBillNo());
			if (!file.exists()) {
				file.mkdirs();
			}
			File pdfFile = new File(file, psiLadingBill.getBillNo() + ".pdf");
			
			Set<String> skus = Sets.newHashSet();
			for (LcPsiLadingBillItem item : psiLadingBill.getItems()) {
				if (StringUtils.isNotEmpty(item.getSku())) {
					skus.add(item.getSku());
				}
			}
			Map<String, String> fnskuMap = psiProductService.getSkuAndFnskuMap(skus);
			PdfUtil.genPsiLadingBillPdf(pdfFile, psiLadingBill, fnskuMap,canSendMap,remarkMap);
	
			mailInfo.setFileName(billNo + ".pdf");
			mailInfo.setFilePath(pdfFile.getAbsolutePath());
			sendEmail.setSendAttchmentPath(billNo + ".pdf");
		}
		//添加多个质检文件
		if(StringUtils.isNotEmpty(filePaths)){
			for(String filePath:filePaths.split(",")){
				///psi/qualityTests/20161028QRT_LC_TDH01/bed88bc8-1a2a-4be1-a7d0-ea7c82354642.xls
				String path=ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir();
				String fileName = filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
				mailInfo.setFileName(fileName);
				mailInfo.setFilePath(path+"/"+filePath);
			}
		}
		
		new Thread() {
			@Override
			public void run() {
				mailManager.send(mailInfo);
			}
		}.start();

		
		sendEmail.setSentDate(new Date());
		sendEmail.setSendFlag("1");
		sendEmailService.save(sendEmail);
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
		
		
		
		/**
		 * 查询最后一笔的付款时间  参数  提单、产品、颜色
		 */
		public Date getLastPayDate(String billNo,String productName,String color){
			String sql="SELECT MAX(a.`create_date`) FROM lc_psi_purchase_payment AS a , lc_psi_purchase_payment_item AS b,lc_psi_lading_bill_item AS c WHERE a.id=b.`payment_id` " +
					"AND c.id=b.`lading_item_bill_id` AND a.`payment_sta`<>'3' AND b.`bill_no`=:p1  AND c.`product_name`=:p2 AND c.`color_code`=:p3";
			List<Date>	list=this.psiLadingBillDao.findBySql(sql, new Parameter(billNo,productName,color));
			if(list!=null&&list.size()>0){
			   return list.get(0);
			}
			return null;
		}

		public List<LcPsiLadingBill> expBill(LcPsiLadingBill psiLadingBill) {
	        DetachedCriteria dc = psiLadingBillDao.createDetachedCriteria();
	        if(psiLadingBill.getCreateDate()!=null){
	            dc.add(Restrictions.ge("createDate",psiLadingBill.getCreateDate()));
	        }
	        
	        if(psiLadingBill.getUpdateDate()!=null){
	            dc.add(Restrictions.le("createDate",DateUtils.addDays(psiLadingBill.getUpdateDate(),1)));
	        }
	        
	        if(StringUtils.isNotEmpty(psiLadingBill.getBillNo())){
	            dc.add(Restrictions.like("billNo", "%"+psiLadingBill.getBillNo()+"%"));
	        }
	        
	        if(StringUtils.isNotEmpty(psiLadingBill.getBillSta())){
	            dc.add(Restrictions.eq("billSta",psiLadingBill.getBillSta()));
	        }else{
	            List<String> list = Lists.newArrayList();
	            list.add("1");
	            list.add("5");
	            dc.add(Restrictions.in("billSta",list));
	        }
	        
	        if(psiLadingBill.getSupplier()!=null&&psiLadingBill.getSupplier().getId()!=null){
	            dc.add(Restrictions.eq("supplier", psiLadingBill.getSupplier()));
	        }
	        dc.addOrder(Order.desc("id"));
	        
	        return psiLadingBillDao.find(dc);
	    }
		
	//根据供应商查询逾期提单明细
	public List<Object[]> findOverdue(PsiSupplier supplier){
		List<Object[]> rs = Lists.newArrayList();
		if (supplier == null) {
			return rs;
		}
		String sql = "SELECT a.id,a.bill_no,a.`create_date`,b.`balance_delay1`,b.`balance_delay2`,b.`balance_rate1`,b.`balance_rate2`, "+
				" b.`total_amount`*(b.quantity_sure/b.quantity_lading) AS totalAmount,b.`total_payment_amount`,b.`total_payment_pre_amount`, "+
				" b.`total_amount`*b.`balance_rate1`/100 AS firstShouldPay,b.sku "+
				" FROM lc_psi_lading_bill AS a ,lc_psi_lading_bill_item AS b WHERE a.id=b.`lading_bill_id` "+
				" AND a.`total_amount`>a.`total_payment_amount` AND (b.`balance_delay1`>0 OR b.`balance_delay2`>0) AND "+
				" b.`total_amount`*(b.quantity_sure/b.quantity_lading)>b.`total_payment_amount` "+
				" AND b.`del_flag`='0' AND a.`bill_sta` IN ('1','5') AND a.`supplier_id`=:p1";
		List<Object[]> list = psiLadingBillDao.findBySql(sql, new Parameter(supplier.getId()));
		for (Object[] obj : list) {
			Integer delay1 = Integer.parseInt(obj[3].toString());
			Integer delay2 = Integer.parseInt(obj[4].toString());
			Integer rate1 = Integer.parseInt(obj[5].toString());
			Integer rate2 = Integer.parseInt(obj[6].toString());
			Float payAmount = Float.parseFloat(obj[8].toString());
			Float firstAmount  = Float.parseFloat(obj[10].toString());
			Date createDate = (Date)obj[2];
			obj[2] = getPayDays(supplier.getPayType(), createDate);
			if(rate1.intValue()==100){
				if(delay1>0){
					rs.add(obj);
				}
			}else if(rate2.intValue()==100){//一般不会出现
				if(delay2>0){
					rs.add(obj);
				}
			}else{
				if(payAmount>=firstAmount){
					//如果付款金额大于分批第一次金额，看第二笔逾期了没
					if(delay2>0){
						//已逾期
						rs.add(obj);
					}
				}else{
					//第一笔都没付完
					if(delay1>0){
						//已逾期
						rs.add(obj);
					}
				}
			}
		}
		return rs;
	}
}
