/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.psi.dao.PsiInvoiceProductDao;
import com.springrain.erp.modules.psi.dao.PsiInvoiceTransportDeclareDao;
import com.springrain.erp.modules.psi.dao.PsiSupplierInvoiceDao;
import com.springrain.erp.modules.psi.entity.PsiInvoiceProduct;
import com.springrain.erp.modules.psi.entity.PsiInvoiceTransportDeclare;
import com.springrain.erp.modules.psi.entity.PsiSupplierInvoice;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Component
@Transactional(readOnly = true)
public class PsiInvoiceService extends BaseService {
	@Autowired
	private PsiSupplierInvoiceDao 			psiSupplierInvoiceDao;
	
	@Autowired
	private PsiInvoiceTransportDeclareDao psiInvoiceTransportDeclareDao;
	
	@Autowired
	private PsiInvoiceProductDao psiInvoiceProductDao;
	
	
	public List<PsiInvoiceProduct> find() {
		DetachedCriteria dc = psiInvoiceProductDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiInvoiceProductDao.find(dc);
	}
	

	@Transactional(readOnly = false)
	public void updateProduct(Integer id) {
		String sql="update psi_invoice_product set del_flag='1' where id=:p1 ";
		psiInvoiceProductDao.updateBySql(sql, new Parameter(id));
	}	
	
	
	public PsiInvoiceProduct findProduct(String code) {
		DetachedCriteria dc = psiInvoiceProductDao.createDetachedCriteria();
		dc.add(Restrictions.eq("productCode",code));
		dc.add(Restrictions.eq("delFlag", "0"));
		List<PsiInvoiceProduct>  list=psiInvoiceProductDao.find(dc);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}

	@Transactional(readOnly = false)
	public void savePsiInvoiceProduct(List<PsiInvoiceProduct> psiInvoiceProduct) {
		psiInvoiceProductDao.save(psiInvoiceProduct);
	}
	
	@Transactional(readOnly = false)
	public void updateRate(Integer id,Float rate) {
		String sql="update psi_invoice_product set tax_rate=:p1 where id=:p2 and del_flag='0'";
		psiInvoiceProductDao.updateBySql(sql, new Parameter(rate,id));
	}
	
	public Map<String,Float> findTaxRate(){
		Map<String,Float> map=Maps.newHashMap();
		String sql="select product_code,tax_rate from psi_invoice_product where del_flag='0'";
		List<Object[]>  list=psiInvoiceProductDao.findBySql(sql);
		for (Object[] obj: list) {
			map.put(obj[0].toString(),Float.parseFloat(obj[1].toString()));
		}
		return map;
	}
	
	public Map<String,String> findName(){
		Map<String,String> map=Maps.newHashMap();
		String sql="select product_code,name from psi_invoice_product where del_flag='0'";
		List<Object[]>  list=psiInvoiceProductDao.findBySql(sql);
		for (Object[] obj: list) {
			map.put(obj[0].toString(),obj[1].toString());
		}
		return map;
	}
	
	public PsiSupplierInvoice get(Integer id) {
		return psiSupplierInvoiceDao.get(id);
	} 
	
	
	public PsiInvoiceTransportDeclare getDeclare(Integer id) {
		return psiInvoiceTransportDeclareDao.get(id);
	} 
	
	
	@Transactional(readOnly = false)
	public void saveDeclare(List<PsiInvoiceTransportDeclare> declares) {
		psiInvoiceTransportDeclareDao.save(declares);
	}
	
	
	@Transactional(readOnly = false)
	public void updateRemainQuantity(List<PsiInvoiceTransportDeclare> declares,Map<Integer,Integer> quantityMap) {
		psiInvoiceTransportDeclareDao.save(declares);
		String sql="update psi_supplier_invoice set remaining_quantity=remaining_quantity-:p1,use_quantity=:p1,use_date=now(),return_date=null where id=:p2  ";
		for (Map.Entry<Integer,Integer> entry: quantityMap.entrySet()) {
			psiSupplierInvoiceDao.updateBySql(sql,new Parameter(entry.getValue(),entry.getKey()));
		}
	}
	
	
	@Transactional(readOnly = false)
	public void save(List<PsiSupplierInvoice> invoice) {
		psiSupplierInvoiceDao.save(invoice);
	}
	
	
	@Transactional(readOnly = false)
	public void save(PsiSupplierInvoice invoice) {
		psiSupplierInvoiceDao.save(invoice);
	}
	
	
	@Transactional(readOnly = false)
	public void saveDeclare(PsiInvoiceTransportDeclare declares) {
		psiInvoiceTransportDeclareDao.save(declares);
	}
	    
	public Page<PsiSupplierInvoice> find(Page<PsiSupplierInvoice> page, PsiSupplierInvoice invoice) {
		DetachedCriteria dc = psiSupplierInvoiceDao.createDetachedCriteria();
		
		if(StringUtils.isNotEmpty(invoice.getProductName())){
			dc.add(Restrictions.or(Restrictions.like("invoiceNo", "%"+invoice.getProductName().trim()+"%"),
					Restrictions.like("productName", "%"+invoice.getProductName().trim()+"%"),Restrictions.like("companyName", "%"+invoice.getProductName().trim()+"%")));
		}
		if (invoice.getCreateDate()!=null){
			dc.add(Restrictions.ge("invoiceDate",invoice.getCreateDate()));
		}
		if (invoice.getInvoiceDate()!=null){
			dc.add(Restrictions.le("invoiceDate",invoice.getInvoiceDate()));
		}
		if("1".equals(invoice.getState())){
			 dc.add(Restrictions.or(Restrictions.and(Restrictions.isNotNull("useDate"),Restrictions.isNull("returnDate")),Restrictions.and(Restrictions.isNotNull("useDate"),Restrictions.ge("returnDate",new Date()))
					 ,Restrictions.and(Restrictions.isNull("useDate"),Restrictions.ge("returnDate",new Date()))));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		if(StringUtils.isEmpty(page.getOrderBy())){
			dc.addOrder(Order.desc("id"));
		}
		return psiSupplierInvoiceDao.find(page, dc);
	}
	
	
	 public List<PsiInvoiceTransportDeclare> find(PsiInvoiceTransportDeclare invoice) {
			DetachedCriteria dc = psiInvoiceTransportDeclareDao.createDetachedCriteria();
			
			if(StringUtils.isNotEmpty(invoice.getProductName())){
				dc.add(Restrictions.or(Restrictions.like("declareNo", "%"+invoice.getProductName().trim()+"%"),
						Restrictions.like("transportNo", "%"+invoice.getProductName().trim()+"%")));
			}
			if (invoice.getCreateDate()!=null){
				dc.add(Restrictions.ge("createDate",invoice.getCreateDate()));
			}
			if (invoice.getArrangeDate()!=null){
				dc.add(Restrictions.le("createDate",invoice.getArrangeDate()));
			}
			dc.add(Restrictions.eq("delFlag", "0"));
			dc.addOrder(Order.desc("transportNo"));
			return psiInvoiceTransportDeclareDao.find(dc);
	 }
	 
	 public List<PsiInvoiceTransportDeclare> find2(PsiInvoiceTransportDeclare invoice) {
			DetachedCriteria dc = psiInvoiceTransportDeclareDao.createDetachedCriteria();
			
			if(StringUtils.isNotEmpty(invoice.getProductName())){
				dc.add(Restrictions.or(Restrictions.like("declareNo", "%"+invoice.getProductName().trim()+"%"),
						Restrictions.like("transportNo", "%"+invoice.getProductName().trim()+"%")));
			}
			if (invoice.getCreateDate()!=null){
				dc.add(Restrictions.ge("createDate",invoice.getCreateDate()));
			}
			if (invoice.getArrangeDate()!=null){
				dc.add(Restrictions.le("createDate",invoice.getArrangeDate()));
			}
			dc.add(Restrictions.and(Property.forName("arrangeDate").isNotNull()));
			dc.add(Restrictions.eq("delFlag", "0"));
			dc.addOrder(Order.desc("transportNo"));
			return psiInvoiceTransportDeclareDao.find(dc);
	 }
	 
	 
    
	 public Page<PsiInvoiceTransportDeclare> find(Page<PsiInvoiceTransportDeclare> page, PsiInvoiceTransportDeclare invoice) {
		DetachedCriteria dc = psiInvoiceTransportDeclareDao.createDetachedCriteria();
		
		if(StringUtils.isNotEmpty(invoice.getProductName())){
			dc.add(Restrictions.or(Restrictions.like("declareNo", "%"+invoice.getProductName().trim()+"%"),
					Restrictions.like("transportNo", "%"+invoice.getProductName().trim()+"%")));
		}
		if (invoice.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",invoice.getCreateDate()));
		}
		if (invoice.getArrangeDate()!=null){
			dc.add(Restrictions.le("createDate",invoice.getArrangeDate()));
		}
		
		dc.add(Restrictions.eq("delFlag", "0"));
		if(StringUtils.isEmpty(page.getOrderBy())){
			dc.addOrder(Order.desc("id"));
		}
		return psiInvoiceTransportDeclareDao.find(page, dc);
	}


	
	
	public boolean isExistInfo(String invoiceNo,String product_name,Float price){
		boolean flag=false;
		String sql="SELECT COUNT(*) FROM psi_supplier_invoice AS a WHERE a.`invoice_no`=:p1 AND product_name=:p2 and price=:p3 AND a.`del_flag`='0'";
		List<BigInteger> invoices =this.psiSupplierInvoiceDao.findBySql(sql, new Parameter(invoiceNo,product_name,price));
		if(invoices!=null&&invoices.size()>0){
			if(invoices.get(0).intValue()>0){
				flag=true;
			}
		}
		return flag;
	}
	
	public boolean isExistDeclareInfo(String declareNo,String productNo){
		boolean flag=false;
		String sql="SELECT COUNT(*) FROM psi_transport_declare AS a WHERE a.`declare_no`=:p1 AND product_no=:p2 AND a.`del_flag`='0'";
		List<BigInteger> invoices =this.psiSupplierInvoiceDao.findBySql(sql, new Parameter(declareNo,productNo));
		if(invoices!=null&&invoices.size()>0){
			if(invoices.get(0).intValue()>0){
				flag=true;
			}
		}
		return flag;
	}
	
	@Transactional(readOnly = false)
	public void updateDeleteState(Integer id) {
		String sql="update psi_supplier_invoice set del_flag='1' where id=:p1 ";
		psiSupplierInvoiceDao.updateBySql(sql, new Parameter(id));
	}	
	
	@Transactional(readOnly = false)
	public void updateDeleteState(Set<String> ids) {
		String sql="update psi_supplier_invoice set del_flag='1' where id in :p1 ";
		psiSupplierInvoiceDao.updateBySql(sql, new Parameter(ids));
	}	
	
	

	@Transactional(readOnly = false)
	public void updateInvoiceDate(Set<String> ids) {
		String sql="update psi_supplier_invoice set return_date=now() where id in :p1 ";
		psiSupplierInvoiceDao.updateBySql(sql, new Parameter(ids));
	}	
	
	@Transactional(readOnly = false)
	public void updateDeclareDeleteState(Set<String> ids) {
		String sql="update psi_transport_declare set del_flag='1' where id in :p1 ";
		psiSupplierInvoiceDao.updateBySql(sql, new Parameter(ids));
	}	
	
	
	@Transactional(readOnly = false)
	public boolean updateState(Integer id,String state) {
		String sql="update psi_supplier_invoice set state=:p1 where id=:p2 ";
		psiSupplierInvoiceDao.updateBySql(sql, new Parameter(state,id));
		return true;
	}	
	
	@Transactional(readOnly = false)
	public boolean resetArrange(String declareNo,Map<Integer,String> idQtyMap) {
		String sql1="UPDATE psi_transport_declare SET arrange_date=NULL,invoice_id=NULL,arrange_user=NULL where declare_no=:p1";
		psiSupplierInvoiceDao.updateBySql(sql1, new Parameter(declareNo));
		if(idQtyMap.size()>0){
			String sql2="UPDATE psi_supplier_invoice SET remaining_quantity=remaining_quantity+:p1,use_quantity=use_quantity-:p1,return_date=now() where id=:p2 ";
			String sql3="UPDATE psi_supplier_invoice SET remaining_quantity=remaining_quantity+:p1,use_quantity=use_quantity-:p1,return_date=null where id=:p2 ";
			
			for (Map.Entry<Integer,String>  entry : idQtyMap.entrySet()) {
				Integer invoiceId=entry.getKey();
				String[] arr=entry.getValue().split(",");
				Integer quanity=Integer.parseInt(arr[1]);
				if("0".equals(arr[0])){//全部归还
					psiSupplierInvoiceDao.updateBySql(sql2, new Parameter(quanity,invoiceId));
				}else{
					psiSupplierInvoiceDao.updateBySql(sql3, new Parameter(quanity,invoiceId));
				}
			}
		}
		return true;
	}	
	
	
	@Transactional(readOnly = false)
	public String resetArrange(Map<Integer,PsiInvoiceTransportDeclare> map,String declareNo,Map<Integer,String> idQtyMap,String invoiceIds) {
		String sql1="UPDATE psi_transport_declare SET arrange_date=NULL,invoice_id=NULL,arrange_user=NULL where declare_no=:p1";
		psiSupplierInvoiceDao.updateBySql(sql1, new Parameter(declareNo));
		if(idQtyMap.size()>0){
			String sql2="UPDATE psi_supplier_invoice SET remaining_quantity=remaining_quantity+:p1,use_quantity=use_quantity-:p1,return_date=now() where id=:p2 ";
			String sql3="UPDATE psi_supplier_invoice SET remaining_quantity=remaining_quantity+:p1,use_quantity=use_quantity-:p1,return_date=null where id=:p2 ";
			
			for (Map.Entry<Integer,String>  entry : idQtyMap.entrySet()) {
				Integer invoiceId=entry.getKey();
				String[] arr=entry.getValue().split(",");
				Integer quanity=Integer.parseInt(arr[1]);
				if("0".equals(arr[0])){//全部归还
					psiSupplierInvoiceDao.updateBySql(sql2, new Parameter(quanity,invoiceId));
				}else{
					psiSupplierInvoiceDao.updateBySql(sql3, new Parameter(quanity,invoiceId));
				}
			}
		}
		if(StringUtils.isNotBlank(invoiceIds)){
			String returnStr="";
			Map<Integer,Integer> invoiceMap=findQuantityByInvoice();
			String[] arr=invoiceIds.split(";");
			
			Map<Integer,Integer> tempMap=Maps.newHashMap();
			Map<Integer,Integer> declareMap=Maps.newHashMap();
			for (String temp: arr) {
				if(StringUtils.isNotBlank(temp)){
					String[] idArr=temp.split(",");
					Integer declareId=Integer.parseInt(idArr[0]);
					Integer quantity=map.get(declareId).getQuantity();
					Integer invoiceId=Integer.parseInt(idArr[1]);
					Integer invoiceQuantity=invoiceMap.get(invoiceId);
					if(tempMap.get(invoiceId)!=null){
						invoiceQuantity=invoiceQuantity-tempMap.get(invoiceId);
					}
					if(quantity>invoiceQuantity){
					   returnStr=invoiceId+",发票数量："+invoiceQuantity+",报关单数量："+quantity;
				       break;
					}
					tempMap.put(invoiceId,quantity+(tempMap.get(invoiceId)==null?0:tempMap.get(invoiceId)));
					declareMap.put(declareId, invoiceId);
				}
			}
			if(StringUtils.isBlank(returnStr)){
				String sql="update psi_supplier_invoice set remaining_quantity=remaining_quantity-:p1,use_quantity=:p1,use_date=now(),return_date=null where id=:p2  ";
				for (Map.Entry<Integer,Integer> entry: tempMap.entrySet()) {
					psiSupplierInvoiceDao.updateBySql(sql,new Parameter(entry.getValue(),entry.getKey()));
				}
				User user=UserUtils.getUser();
				String declareSql="UPDATE psi_transport_declare SET arrange_date=now(),invoice_id=:p1,arrange_user=:p2 where id=:p3";
				for (Map.Entry<Integer,Integer> entry: declareMap.entrySet()) {
					psiSupplierInvoiceDao.updateBySql(declareSql,new Parameter(entry.getValue(),user,entry.getKey()));
				}
			}else{
				throw new RuntimeException(returnStr);
			}
		}
		return "";
	}	

	@Transactional(readOnly = false)
	public boolean updateReturnDate(Integer id,Date returnDate) {
		String sql="update psi_supplier_invoice set return_date=:p1 where id=:p2 ";
		psiSupplierInvoiceDao.updateBySql(sql, new Parameter(returnDate,id));
		String sql2="update psi_transport_declare set state='1' where invoice_id=:p1 and arrange_date<=:p2 and state is null";
		psiSupplierInvoiceDao.updateBySql(sql2, new Parameter(id,returnDate));
		return true;
	}	
	
	
	 public Map<String,List<PsiInvoiceTransportDeclare>> findUnArrangeDeclare() {
		 Map<String,List<PsiInvoiceTransportDeclare>> map=Maps.newLinkedHashMap();
		 DetachedCriteria dc = psiInvoiceTransportDeclareDao.createDetachedCriteria();
		 dc.add(Restrictions.eq("delFlag", "0"));
		 dc.addOrder(Order.asc("transportNo"));
		 List<PsiInvoiceTransportDeclare> list=psiInvoiceTransportDeclareDao.find(dc);
		 for (PsiInvoiceTransportDeclare declare : list) {
			 List<PsiInvoiceTransportDeclare> temp=map.get(declare.getTransportNo());
			 if(temp==null){
				 temp=Lists.newArrayList();
				 map.put(declare.getTransportNo(),temp);
			 }
			 temp.add(declare);
		 }
		 return map;	
	}
	 
	 public List<PsiInvoiceTransportDeclare> findDeclareByDeclareNo(String declareNo) {
		 DetachedCriteria dc = psiInvoiceTransportDeclareDao.createDetachedCriteria();
		 dc.add(Restrictions.eq("delFlag", "0"));
		 dc.add(Restrictions.eq("declareNo",declareNo));
		 return psiInvoiceTransportDeclareDao.find(dc);
	}
	 
	 public Map<Integer,PsiInvoiceTransportDeclare> findDeclareIdByDeclareNo(String declareNo) {
		 Map<Integer,PsiInvoiceTransportDeclare> map=Maps.newHashMap();
		 DetachedCriteria dc = psiInvoiceTransportDeclareDao.createDetachedCriteria();
		 dc.add(Restrictions.eq("delFlag", "0"));
		 dc.add(Restrictions.eq("declareNo",declareNo));
		 List<PsiInvoiceTransportDeclare> list=psiInvoiceTransportDeclareDao.find(dc);
		 for (PsiInvoiceTransportDeclare psiInvoiceTransportDeclare : list) {
			map.put(psiInvoiceTransportDeclare.getId(),psiInvoiceTransportDeclare);
		 }
		 return map;
	}
	 
	 public Map<String,Map<Integer,Map<Integer,Map<Float,PsiSupplierInvoice>>>> findUnUseInvoice() {
		 Map<String,Map<Integer,Map<Integer,Map<Float,PsiSupplierInvoice>>>>  map=Maps.newLinkedHashMap();
		 DetachedCriteria dc = psiSupplierInvoiceDao.createDetachedCriteria();
		 dc.add(Restrictions.eq("delFlag", "0"));
		 dc.add(Restrictions.eq("state", "1"));
		 dc.add(Restrictions.lt("remainingQuantity",0));
		 dc.add(Restrictions.or(Restrictions.isNull("useDate"),Restrictions.and(Restrictions.isNotNull("useDate"),Restrictions.le("returnDate",new Date()))));
		 dc.addOrder(Order.asc("invoiceDate"));
		 List<PsiSupplierInvoice> list=psiSupplierInvoiceDao.find(dc);
		 for (PsiSupplierInvoice invoice : list) {
			 Map<Integer,Map<Integer,Map<Float,PsiSupplierInvoice>>> temp=map.get(invoice.getProductName());
			 if(temp==null){
				 temp=Maps.newLinkedHashMap();
				 map.put(invoice.getProductName(),temp);
			 }
			 
			 Map<Integer,Map<Float,PsiSupplierInvoice>> invoiceIdMap=temp.get(invoice.getId());
			 if(invoiceIdMap==null){
				 invoiceIdMap=Maps.newLinkedHashMap();
				 temp.put(invoice.getId(),invoiceIdMap);
			 }
			 
			 
			 Map<Float,PsiSupplierInvoice> quantityMap=invoiceIdMap.get(invoice.getQuantity());
			 if(quantityMap==null){
				 quantityMap=Maps.newLinkedHashMap();
				 invoiceIdMap.put(invoice.getQuantity(), quantityMap);
			 }
			 quantityMap.put(invoice.getPrice(),invoice);
		 }
		 return map;	
	}
	 
	 public Map<String,List<PsiSupplierInvoice>> findUnUseInvoiceInfo() {
		 Map<String,List<PsiSupplierInvoice>>  map=Maps.newLinkedHashMap();
		 DetachedCriteria dc = psiSupplierInvoiceDao.createDetachedCriteria();
		 dc.add(Restrictions.eq("delFlag", "0"));
		 dc.add(Restrictions.eq("state", "1"));
		 dc.add(Restrictions.gt("remainingQuantity",0));
		 dc.add(Restrictions.or(Restrictions.isNull("useDate"),Restrictions.and(Restrictions.isNotNull("useDate"),Restrictions.le("returnDate",new Date()))));
		 dc.addOrder(Order.asc("invoiceDate"));
		 List<PsiSupplierInvoice> list=psiSupplierInvoiceDao.find(dc);
		 for (PsiSupplierInvoice invoice : list) {
			 List<PsiSupplierInvoice> temp=map.get(invoice.getProductName());
			 if(temp==null){
				 temp=Lists.newArrayList();
				 map.put(invoice.getProductName(), temp);
			 }
			 temp.add(invoice);
		 }
		 return map;	
	}
	 
	  
		 public List<PsiInvoiceTransportDeclare> find(Integer id) {
			DetachedCriteria dc = psiInvoiceTransportDeclareDao.createDetachedCriteria();
			dc.createAlias("invoice","invoice");
			dc.add(Restrictions.eq("invoice.id",id));
			dc.add(Restrictions.eq("delFlag", "0"));
			return psiInvoiceTransportDeclareDao.find(dc);
		}

		 
		public Map<Integer,Integer> findQuantityByInvoice(){
			 Map<Integer,Integer>  map=Maps.newHashMap();
			 String sql="select id,remaining_quantity  from psi_supplier_invoice  where remaining_quantity>0 and del_flag='0' ";
			 List<Object[]> list=psiInvoiceTransportDeclareDao.findBySql(sql);
			 for (Object[] obj: list) {
				map.put(Integer.parseInt(obj[0].toString()),Integer.parseInt(obj[1].toString()));
			 }
			 return map;
		}
		
		public void updateTaxRate(){
			String sql="select product_code,tax_rate from psi_invoice_product where del_flag='0'";
			String updateSql="update psi_transport_declare  set tax_rate=:p1 where product_no=:p2";
			List<Object[]>  list=psiInvoiceProductDao.findBySql(sql);
			for (Object[] obj: list) {
				psiInvoiceTransportDeclareDao.updateBySql(updateSql, new Parameter(Float.parseFloat(obj[1].toString()),obj[0].toString()));
			}
		}
		
		@Transactional(readOnly = false)
		public void deleteAll() {
			String sql="TRUNCATE TABLE psi_transport_declare";
			psiSupplierInvoiceDao.updateBySql(sql,null);
			String sql2="truncate table psi_supplier_invoice";
			psiSupplierInvoiceDao.updateBySql(sql2,null);
		}	
}
