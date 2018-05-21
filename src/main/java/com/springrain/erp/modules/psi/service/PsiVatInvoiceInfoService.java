/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.psi.dao.PsiVatInvoiceInfoDao;
import com.springrain.erp.modules.psi.dao.PsiVatInvoiceUseInfoDao;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PsiVatInvoiceInfo;
import com.springrain.erp.modules.psi.entity.PsiVatInvoiceUseInfo;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * Service
 * @author Michael
 * @version 2015-06-01
 */
@Component
@Transactional(readOnly = true)
public class PsiVatInvoiceInfoService extends BaseService {
	@Autowired
	private PsiVatInvoiceInfoDao 			psiVatInvoiceInfoDao;
	
	@Autowired
	private PsiVatInvoiceUseInfoDao psiVatInvoiceUseInfoDao;
	
	public PsiVatInvoiceInfo get(Integer id) {
		return psiVatInvoiceInfoDao.get(id);
	} 
	
	public PsiVatInvoiceUseInfo getById(Integer id) {
		return psiVatInvoiceUseInfoDao.get(id);
	} 
	
	@Transactional(readOnly = false)
	public void save(PsiVatInvoiceUseInfo invoice) {
		invoice.setCreateDate(new Date());
		invoice.setCreateUser(UserUtils.getUser());
		invoice.setDelFlag("0");
		psiVatInvoiceUseInfoDao.save(invoice);
	}
	@Transactional(readOnly = false)
	public void save2(PsiVatInvoiceUseInfo invoice) {
		psiVatInvoiceUseInfoDao.save(invoice);
	}
	
	@Transactional(readOnly = false)
	public void save(List<PsiVatInvoiceUseInfo> invoice) {
		psiVatInvoiceUseInfoDao.save(invoice);
	}
	    
	public Page<PsiVatInvoiceInfo> find(Page<PsiVatInvoiceInfo> page, PsiVatInvoiceInfo psiVatInvoiceInfo) {
		DetachedCriteria dc = psiVatInvoiceInfoDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(psiVatInvoiceInfo.getRemark())){
			dc.add(Restrictions.like("remark", "%"+psiVatInvoiceInfo.getRemark()+"%"));
		}
		
		if(StringUtils.isNotEmpty(psiVatInvoiceInfo.getProductName())){
			dc.add(Restrictions.or(Restrictions.like("invoiceNo", "%"+psiVatInvoiceInfo.getProductName().trim()+"%"),
					Restrictions.like("productName", "%"+psiVatInvoiceInfo.getProductName().trim()+"%"),Restrictions.like("supplierName", "%"+psiVatInvoiceInfo.getProductName().trim()+"%")));
		}
		if (psiVatInvoiceInfo.getCreateDate()!=null){
			dc.add(Restrictions.ge("invoiceDate",psiVatInvoiceInfo.getCreateDate()));
		}
		if (psiVatInvoiceInfo.getInvoiceDate()!=null){
			dc.add(Restrictions.le("invoiceDate",psiVatInvoiceInfo.getInvoiceDate()));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		if(StringUtils.isEmpty(page.getOrderBy())){
			dc.addOrder(Order.desc("id"));
		}
		return psiVatInvoiceInfoDao.find(page, dc);
	}
	
	
	@Transactional(readOnly = false)
	public String save(PsiVatInvoiceInfo invoice) {
		invoice.setCreateDate(new Date());
		invoice.setCreateUser(UserUtils.getUser());
		invoice.setDelFlag("0");
		if(this.isExistInfo(invoice.getInvoiceNo(), invoice.getSupplierName(), invoice.getProductName(), invoice.getTotalAmount(), invoice.getQuantity())){
			return "error:数据与库里的重复了,请核对！";
		}
		psiVatInvoiceInfoDao.save(invoice);
		return "";
	}
	
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		PsiVatInvoiceInfo psiVatInvoiceInfo=this.get(id);
		psiVatInvoiceInfo.setDelFlag("1");
		psiVatInvoiceInfoDao.save(psiVatInvoiceInfo);
	}

	@Transactional(readOnly = false)
	public String excelSave(MultipartFile excelFile,PsiVatInvoiceInfo psiVatInvoiceInfo) throws InvalidFormatException, IOException{
		//查出供应商名称对应的供应商id
		 Map<String,Integer> supplierNameIdMap =getSupplierNameId();
		 Map<Integer,String> supplierIdNameMap =getSupplierIdName();
		 Map<String,String> productMap =getProductName();
		//查出所有的产品名
		 StringBuilder sb = new StringBuilder();
		if(excelFile!=null&&excelFile.getSize()!=0){
			String suffix = excelFile.getOriginalFilename().substring(excelFile.getOriginalFilename().lastIndexOf(".")); 
			if(!".csv,.xls,.xlsx".contains(suffix)){
				throw new RuntimeException("Lot Delivery file type is not right，Operation has been canceled");
			}
			
			//放入文件解析文件
			List<PsiVatInvoiceInfo>  list = Lists.newArrayList();
			Workbook workBook = WorkbookFactory.create(excelFile.getInputStream());
			Sheet sheet = workBook.getSheetAt(0);
			sheet.setForceFormulaRecalculation(true);
			int rows = sheet.getPhysicalNumberOfRows();
			if(rows <= 0){
				throw new RuntimeException("Excel file no data，Operation has been canceled!");
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			// 循环行Row
			for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
				PsiVatInvoiceInfo invoice = new PsiVatInvoiceInfo();
				Row row = sheet.getRow(rowNum);
				if (row == null) {
					continue;
				}
				
				// 循环列Cell
				ArrayList<String> arrCell =new ArrayList<String>();
				for (int cellNum = 0; cellNum <= row.getLastCellNum(); cellNum++) {
					Cell cell = row.getCell(cellNum);
					if (cell == null) {
						continue;
					}
					arrCell.add(getValue(cell));
				}
				//开票日期，发票号码，公司名称，总金额，型号，数量
				if(arrCell.size()==0){
					continue;
				}   
				try{
				Date invoiceDate = sdf.parse(arrCell.get(0));
				String invoiceNo = arrCell.get(1);
				String supplierName = arrCell.get(2);
				BigDecimal totalAmount = new BigDecimal(arrCell.get(3));
				String model = arrCell.get(4);
				Integer quantity = (int)Float.parseFloat(arrCell.get(5));
				String remark ="";
				if(arrCell.size()>6){
					remark=arrCell.get(6);
				}
				PsiSupplier supplier = null;
				if(supplierNameIdMap.get(supplierName)!=null){
					supplier = new PsiSupplier();
					Integer supplierId = supplierNameIdMap.get(supplierName);
					supplier.setId(supplierId);
					supplierName=supplierIdNameMap.get(supplierId);
				}
				
				invoice.setSupplierName(supplierName);//如果找不到供应商说明改名字了
				
				String productName ="" ;
				if(productMap.get(model)!=null){
					productName=productMap.get(model);
				}else{
					sb.append("第"+(rowNum+1)+"行,产品型号有误,请核对");
				}
				
				if(invoiceNo.contains(".")&&invoiceNo.contains("E")){
					sb.append("第"+(rowNum+1)+"行,发票号码应为文本类型,请核对");
				}
				
				invoice.setInvoiceNo(invoiceNo);
				invoice.setInvoiceDate(invoiceDate);
				invoice.setSupplier(supplier);
				invoice.setTotalAmount(totalAmount);
				invoice.setProductName(productName);
				invoice.setQuantity(quantity);
				invoice.setCreateDate(new Date());
				invoice.setCreateUser(UserUtils.getUser());
				invoice.setRemainingQuantity(quantity);
				invoice.setDelFlag("0");
				invoice.setRemark(remark);
				}catch(Exception ex){
					sb.append("第"+(rowNum+1)+"行,数据有问题,请核对"+ex.getMessage());
				}
				if(this.isExistInfo(invoice.getInvoiceNo(), invoice.getSupplierName(), invoice.getProductName(), invoice.getTotalAmount(), invoice.getQuantity())){
					return "第"+(rowNum+1)+"行,数据与库里的重复了,请核对！";
				}
				list.add(invoice);
			}
			
			if(StringUtils.isEmpty(sb)){
				this.psiVatInvoiceInfoDao.save(list);
			}
		}
		
		return sb.toString();
	}
			
		
	public Map<String,Integer> getSupplierNameId(){
		Map<String,Integer> rs = Maps.newHashMap();
		String sql="SELECT a.`NAME`,a.`id` FROM psi_supplier AS a WHERE a.`del_flag`='0'";
		List<Object[]> list =this.psiVatInvoiceInfoDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				rs.put((String)obj[0], (Integer)obj[1]);
			}
		}
		return rs;
	}
	
	public Map<Integer,String> getSupplierIdName(){
		Map<Integer,String> rs = Maps.newHashMap();
		String sql="SELECT a.`NAME`,a.`id` FROM psi_supplier AS a WHERE a.`del_flag`='0'";
		List<Object[]> list =this.psiVatInvoiceInfoDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				rs.put((Integer)obj[1],(String)obj[0]);
			}
		}
		return rs;
	}
	@Transactional(readOnly = false)
	public Map<String,String> getProductName(){
		Map<String,String> rs = Maps.newHashMap();
		String sql="SELECT a.`model`,CONCAT(a.`brand`,' ',a.`model`) FROM psi_product AS a WHERE a.`del_flag`='0'";
		List<Object[]> list =this.psiVatInvoiceInfoDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				rs.put((String)obj[0], (String)obj[1]);
			}
		}
		return rs;
	}
	
	@SuppressWarnings("static-access")
	private String getValue(Cell cell) {
		if (cell.getCellType() == cell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue()).trim();
		}else if(cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
			 if(HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式  
                SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd");  
                Date date = cell.getDateCellValue();  
                return  sdf.format(date);  
            }else if(cell.getCellStyle().getDataFormat() == 58) {  
                // 处理自定义日期格式：m月d日(通过判断单元格的格式id解决，id的值是58)  
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
                double value = cell.getNumericCellValue();  
                Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);  
                return sdf.format(date);  
            }else{  
            	return String.valueOf(cell.getNumericCellValue()).trim();
            }
		}else if(cell.getCellType() == cell.CELL_TYPE_FORMULA) {
			return String.valueOf(cell.getCellFormula()).trim();
		}else{
			return String.valueOf(cell.getStringCellValue()).trim();
		}
	}
	
	
	@Transactional(readOnly = false)
	public String updateRemark(Integer id,String remark){
		try{
			String sql =" UPDATE psi_vat_invoice_info AS a SET a.`remark`=:p2 WHERE a.`id`=:p1";
			this.psiVatInvoiceInfoDao.updateBySql(sql, new Parameter(id,remark));
			return "true";
		}catch (Exception ex){
			return "false";
		}
	}
	
	public List<Object[]> getInfo(Date endDate){
		//获取收货信息             和支付尾款金额
		String sql="SELECT b.`product_name`,SUM(b.`item_price`*b.`quantity_sure`),SUM(b.`total_payment_amount`) FROM lc_psi_lading_bill AS a ,lc_psi_lading_bill_item AS b WHERE a.id=b.`lading_bill_id` AND a.`bill_sta`<>'2' AND b.del_flag='0' AND a.`create_date`<=:p1 GROUP BY b.`product_name`";
		List<Object[]> receiveds =this.psiVatInvoiceInfoDao.findBySql(sql, new Parameter(endDate));
		// 获取支付定金 金额
		sql="SELECT b.`product_name`,SUM(b.`item_price`*b.`quantity_ordered`*(a.`deposit`/100)) FROM lc_psi_purchase_order AS a ,lc_psi_purchase_order_item AS b WHERE a.`id`=b.`purchase_order_id` AND a.`order_sta` <>'6' AND b.del_flag='0' AND a.`deposit_amount`>0 AND a.`create_date`<=:p1 GROUP BY b.`product_name`";
		List<Object[]> deposits =this.psiVatInvoiceInfoDao.findBySql(sql, new Parameter(endDate));
		Map<String,BigDecimal> depositMap = Maps.newHashMap();
		if(deposits!=null&&deposits.size()>0){
			for(Object[] obj:deposits){
				depositMap.put((String)obj[0], (BigDecimal)obj[1]);
			}
		}
		if(receiveds!=null&&receiveds.size()>0){
			for(Object[] obj:receiveds){
				String productName = obj[0].toString();
				if(depositMap.get(productName)!=null){
					obj[2]=((BigDecimal)obj[2]).add(depositMap.get(productName));
				}
			}
		}
		
		//如果是DL1001\DL1002\DL1004
		Map<String,Object[]> rsMap =Maps.newHashMap();
		for (Iterator<Object[]> iterator = receiveds.iterator(); iterator.hasNext();) {
			Object[] obj = (Object[]) iterator.next();
			String productName = (String)obj[0];
			if("Tomons DL1001EU,Tomons DL1001JP,Tomons DL1001US,Tomons DL1001UK".contains(productName)){
				productName="Tomons DL1001";
				Object[] obj1=rsMap.get(productName);
				if(obj1!=null){
					 obj[1]=((BigDecimal)obj[1]).add((BigDecimal)obj1[1]);
					 obj[2]=((BigDecimal)obj[2]).add((BigDecimal)obj1[2]);
				}
				obj[0]=productName;
				rsMap.put(productName, obj);
				iterator.remove();
			}else if("Tomons DL1002EU,Tomons DL1002JP,Tomons DL1002US,Tomons DL1002UK".contains(productName)){
				productName="Tomons DL1002";
				Object[] obj1=rsMap.get(productName);
				if(obj1!=null){
					 obj[1]=((BigDecimal)obj[1]).add((BigDecimal)obj1[1]);
					 obj[2]=((BigDecimal)obj[2]).add((BigDecimal)obj1[2]);
				}
				obj[0]=productName;
				rsMap.put(productName, obj);
				iterator.remove();
			}else if("Tomons DL1004EU,Tomons DL1004JP,Tomons DL1004US,Tomons DL1004UK".contains(productName)){
				productName="Tomons DL1004";
				Object[] obj1=rsMap.get(productName);
				if(obj1!=null){
					 obj[1]=((BigDecimal)obj[1]).add((BigDecimal)obj1[1]);
					 obj[2]=((BigDecimal)obj[2]).add((BigDecimal)obj1[2]);
				}
				obj[0]=productName;
				rsMap.put(productName, obj);
				iterator.remove();
			}else if("Inateck DS1001,Inateck DS1001JP".contains(productName)){
				productName="Inateck DS1001";
				Object[] obj1=rsMap.get(productName);
				if(obj1!=null){
					 obj[1]=((BigDecimal)obj[1]).add((BigDecimal)obj1[1]);
					 obj[2]=((BigDecimal)obj[2]).add((BigDecimal)obj1[2]);
				}
				obj[0]=productName;
				rsMap.put(productName, obj);   
				iterator.remove();
			}
		}
		if(rsMap.size()>0){
			receiveds.addAll(rsMap.values());
		}
		return receiveds;
	}
	
	
	public Map<String,Object>   findAllPurchase(){
		Map<String,Object> map=Maps.newHashMap();
		Map<String,Integer> qtyMap = Maps.newHashMap();
		Map<String,Integer> totalMap = Maps.newHashMap();
		String sql="SELECT d.`product_name`,SUM(d.`quantity_ordered`),SUM(CASE WHEN r.`order_sta` IN ('1','2','3') THEN d.`quantity_ordered` ELSE 0 END) quantity FROM lc_psi_purchase_order r "+
			" JOIN lc_psi_purchase_order_item d ON r.id=d.`purchase_order_id` AND d.`del_flag`='0' "+
			" WHERE r.`del_flag`='0' AND r.`order_sta` NOT IN ('0','6') GROUP BY d.`product_name` ";
		
		List<Object[]> list=this.psiVatInvoiceInfoDao.findBySql(sql);
		for (Object[] obj: list) {
			String productName = obj[0].toString();
			Integer total=Integer.parseInt(obj[1].toString());
			Integer quantity=Integer.parseInt(obj[2].toString());
			if("Tomons DL1001EU,Tomons DL1001JP,Tomons DL1001US,Tomons DL1001UK".contains(productName)){
				productName="Tomons DL1001";
				if(totalMap.get(productName)!=null){
					total+=totalMap.get(productName);
				}
				if(qtyMap.get(productName)!=null){
					quantity+=qtyMap.get(productName);
				}
			}else if("Tomons DL1002EU,Tomons DL1002JP,Tomons DL1002US,Tomons DL1002UK".contains(productName)){
				productName="Tomons DL1002";
				if(totalMap.get(productName)!=null){
					total+=totalMap.get(productName);
				}
				if(qtyMap.get(productName)!=null){
					quantity+=qtyMap.get(productName);
				}
			}else if("Tomons DL1004EU,Tomons DL1004JP,Tomons DL1004US,Tomons DL1004UK".contains(productName)){
				productName="Tomons DL1004";
				if(totalMap.get(productName)!=null){
					total+=totalMap.get(productName);
				}
				if(qtyMap.get(productName)!=null){
					quantity+=qtyMap.get(productName);
				}
			}else if("Inateck DS1001,Inateck DS1001JP".contains(productName)){
				productName="Inateck DS1001";
				if(totalMap.get(productName)!=null){
					total+=totalMap.get(productName);
				}
				if(qtyMap.get(productName)!=null){
					quantity+=qtyMap.get(productName);
				}
			}
			totalMap.put(productName,total);
			qtyMap.put(productName,quantity);
		}
		map.put("0", totalMap);
		map.put("1", qtyMap);
		return map;
	}
	
	
	public Map<String,Object> getInvoiceInfo(Date endDate){
		Map<String,Object> map=Maps.newHashMap();
		Map<String,BigDecimal> rs = Maps.newHashMap();
		Map<String,Integer> qtyMap = Maps.newHashMap();
		
		String sql="SELECT a.`product_name`,SUM(a.`total_amount`),sum(a.quantity-a.remaining_quantity),sum(a.quantity) FROM psi_vat_invoice_info AS a  WHERE a.`del_flag`='0' AND a.`invoice_date`<=:p1 GROUP BY a.`product_name`";
		List<Object[]> invoices =this.psiVatInvoiceInfoDao.findBySql(sql, new Parameter(endDate));
		if(invoices!=null&&invoices.size()>0){
			for(Object[] obj:invoices){
				String productName = obj[0].toString();
				BigDecimal  amount =  new BigDecimal(obj[1].toString());
				Integer quantity=Integer.parseInt(obj[2].toString());
				if("Tomons DL1001EU,Tomons DL1001JP,Tomons DL1001US,Tomons DL1001UK".contains(productName)){
					productName="Tomons DL1001";
					if(rs.get(productName)!=null){
						amount=amount.add(rs.get(productName));
					}
					if(qtyMap.get(productName)!=null){
						quantity+=qtyMap.get(productName);
					}
				}else if("Tomons DL1002EU,Tomons DL1002JP,Tomons DL1002US,Tomons DL1002UK".contains(productName)){
					productName="Tomons DL1002";
					if(rs.get(productName)!=null){
						amount=amount.add(rs.get(productName));
					}
					if(qtyMap.get(productName)!=null){
						quantity+=qtyMap.get(productName);
					}
				}else if("Tomons DL1004EU,Tomons DL1004JP,Tomons DL1004US,Tomons DL1004UK".contains(productName)){
					productName="Tomons DL1004";
					if(rs.get(productName)!=null){
						amount=amount.add(rs.get(productName));
					}
					if(qtyMap.get(productName)!=null){
						quantity+=qtyMap.get(productName);
					}
				}else if("Inateck DS1001,Inateck DS1001JP".contains(productName)){
					productName="Inateck DS1001";
					if(rs.get(productName)!=null){
						amount=amount.add(rs.get(productName));
					}
					if(qtyMap.get(productName)!=null){
						quantity+=qtyMap.get(productName);
					}
				}
				rs.put(productName,amount);
				qtyMap.put(productName,quantity);
			}
		}
		map.put("0", rs);
		map.put("1", qtyMap);
		return map;
	}
	
	
	/**
	 * 判重            发票号、供应商名称、产品型号、数量、金额
	 * 
	 */
	public boolean isExistInfo(String invoiceNo,String supplierName,String productName,BigDecimal totalAmount,Integer quantity){
		boolean flag=false;
		String sql="SELECT COUNT(*) FROM psi_vat_invoice_info AS a WHERE a.`invoice_no`=:p1 AND a.`supplier_name`=:p2 AND a.`product_name`=:p3 AND a.`total_amount`=:p4 AND a.`quantity`=:p5 AND a.`del_flag`='0'";
		List<BigInteger> invoices =this.psiVatInvoiceInfoDao.findBySql(sql, new Parameter(invoiceNo,supplierName,productName,totalAmount,quantity));
		if(invoices!=null&&invoices.size()>0){
			if(invoices.get(0).intValue()>0){
				flag=true;
			}
		}
		return flag;
	}
	
	
	public Map<String,Map<String,List<PsiVatInvoiceInfo>>>  findInvoice(){
		String sql="SELECT f.`supplier_id`,f.`product_name`,f.`remaining_quantity`,f.`invoice_no`,f.`supplier_name`,f.id "+
				"	FROM psi_vat_invoice_info f WHERE  f.`remaining_quantity`>0  AND f.`del_flag`='0' AND (f.`remark` IS NULL OR (f.`remark` IS NOT NULL AND f.`remark` NOT LIKE '%不分配%')) "+
				"	ORDER BY f.`invoice_date` ASC ";
		List<Object[]> invoices =this.psiVatInvoiceInfoDao.findBySql(sql);
		Map<String,Map<String,List<PsiVatInvoiceInfo>>> map=Maps.newLinkedHashMap();
		for (Object[] obj: invoices) {
			String id=(obj[0]==null?"":obj[0].toString());
			String pname=obj[1].toString();
			if(pname.endsWith("US")||pname.endsWith("JP")||pname.endsWith("UK")||pname.endsWith("EU")||pname.endsWith("DE")){
				pname=pname.replace("US","").replace("JP","").replace("UK","").replace("EU","").replace("DE","");
			}
			Integer quantity=Integer.parseInt(obj[2].toString());
			String invoiceNo=obj[3].toString();
			Map<String,List<PsiVatInvoiceInfo>> temp=map.get(pname);
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(pname, temp);
			}
			List<PsiVatInvoiceInfo>  list=temp.get(id);
			if(list==null){
				list=Lists.newArrayList();
				temp.put(id, list);
			}
			PsiVatInvoiceInfo info=new PsiVatInvoiceInfo();
			info.setProductName(pname);
			info.setRemainingQuantity(quantity);
			info.setInvoiceNo(invoiceNo);
			info.setRemark(obj[4].toString());
			info.setId(Integer.parseInt(obj[5].toString()));
			list.add(info);
		}
		return map;
	}
	
	
	@Transactional(readOnly = false)
	public void updateQuantity(Map<Integer,Integer> quantityMap) {
		String sql="update psi_vat_invoice_info set remaining_quantity=:p1 where id=:p2";
		for (Map.Entry<Integer,Integer> temp: quantityMap.entrySet()) {
			psiVatInvoiceInfoDao.updateBySql(sql, new Parameter(temp.getValue(),temp.getKey()));
		}
	}
	
	@Transactional(readOnly = false)
	public void updateQuantity2(Map<Integer,Integer> quantityMap) {
		String sql="update psi_vat_invoice_info set remaining_quantity=remaining_quantity+:p1 where id=:p2";
		for (Map.Entry<Integer,Integer> temp: quantityMap.entrySet()) {
			psiVatInvoiceInfoDao.updateBySql(sql, new Parameter(temp.getValue(),temp.getKey()));
		}
	}
	
	@Transactional(readOnly = false)
	public void updateQuantity(Integer quantitiy,Integer id) {
		String sql="update psi_vat_invoice_info set remaining_quantity=remaining_quantity-:p1 where id=:p2";
		psiVatInvoiceInfoDao.updateBySql(sql, new Parameter(quantitiy,id));
	}
	
	@Transactional(readOnly = false)
	public String updateQuantity2(Integer quantitiy,Integer id) {
		String sql="update psi_vat_invoice_info set remaining_quantity=remaining_quantity+:p1 where id=:p2";
		psiVatInvoiceInfoDao.updateBySql(sql, new Parameter(quantitiy,id));
		return "1";
	}
	
	
	public List<PsiVatInvoiceInfo> find() {
		DetachedCriteria dc = psiVatInvoiceInfoDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.add(Restrictions.gt("remainingQuantity",0));
		dc.addOrder(Order.asc("id"));
		List<PsiVatInvoiceInfo> list= psiVatInvoiceInfoDao.find(dc);
		for (PsiVatInvoiceInfo psiVatInvoiceInfo : list) {
			String pname=psiVatInvoiceInfo.getProductName();
			if(pname.endsWith("US")||pname.endsWith("JP")||pname.endsWith("UK")||pname.endsWith("EU")||pname.endsWith("DE")){
				pname=pname.replace("US","").replace("JP","").replace("UK","").replace("EU","").replace("DE","");
			}
			psiVatInvoiceInfo.setProductName(pname);
		}
		return list;
	}
	
	public List<PsiVatInvoiceUseInfo> findUseInfo(Integer id) {
		DetachedCriteria dc = psiVatInvoiceUseInfoDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.createAlias("this.invoice", "invoice");
		dc.add(Restrictions.eq("invoice.id",id));
		return psiVatInvoiceUseInfoDao.find(dc);
	}
	
	public List<PsiVatInvoiceUseInfo> findUseInfo(Set<Integer> idSet) {
		DetachedCriteria dc = psiVatInvoiceUseInfoDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.createAlias("this.item", "item");
		dc.add(Restrictions.in("item.id",idSet));
		return psiVatInvoiceUseInfoDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void updateRemainingQuantity(Integer quantitiy,Integer id) {
		String sql="update psi_vat_invoice_info set remaining_quantity=:p1 where id=:p2";
		psiVatInvoiceInfoDao.updateBySql(sql, new Parameter(quantitiy,id));
	}
	
}
