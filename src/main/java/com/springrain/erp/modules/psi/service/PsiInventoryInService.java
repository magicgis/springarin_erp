/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;

import au.com.bytecode.opencsv.CSVReader;

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
import com.springrain.erp.modules.amazoninfo.service.ReturnTestService;
import com.springrain.erp.modules.psi.dao.PsiInventoryDao;
import com.springrain.erp.modules.psi.dao.PsiInventoryInDao;
import com.springrain.erp.modules.psi.dao.PsiInventoryOutDao;
import com.springrain.erp.modules.psi.dao.PsiTransportOrderDao;
import com.springrain.erp.modules.psi.dao.lc.LcPsiTransportOrderDao;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiInventoryIn;
import com.springrain.erp.modules.psi.entity.PsiInventoryInItem;
import com.springrain.erp.modules.psi.entity.PsiInventoryOut;
import com.springrain.erp.modules.psi.entity.PsiInventoryOutItem;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.entity.PsiTransportOrder;
import com.springrain.erp.modules.psi.entity.PsiTransportOrderItem;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrderItem;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportOrderService;
import com.springrain.erp.modules.sys.dao.GenerateSequenceDao;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 入库管理Service
 * @author Michael
 * @version 2015-01-05
 */
@Component
@Transactional(readOnly = true)
public class PsiInventoryInService extends BaseService {
	
	@Autowired
	private PsiInventoryInDao 				psiInventoryInDao;
	@Autowired
	private PsiInventoryOutDao 				psiInventoryOutDao;
	@Autowired
	private PsiInventoryService 			psiInventoryService;
	@Autowired
	private PsiInventoryDao 				psiInventoryDao;
	@Autowired
	private PsiProductService 				psiProductService;
	@Autowired
	private GenerateSequenceDao 			genDao;
	@Autowired
	private PsiTransportOrderDao 			psiTransportOrderDao; 
	@Autowired
	private LcPsiTransportOrderDao 			lcPsiTransportOrderDao; 
	@Autowired
	private PsiTransportOrderService 		psiTransportOrderService; 
	@Autowired
	private LcPsiTransportOrderService		lcPsiTransportOrderService; 
	@Autowired
	private ReturnTestService 				returnTestService;
	
	public PsiInventoryIn get(Integer id) {
		return psiInventoryInDao.get(id);
	}
	
	
	public PsiInventoryIn get(String inNo) {
		DetachedCriteria dc = this.psiInventoryInDao.createDetachedCriteria();
		dc.add(Restrictions.eq("billNo", inNo));
		List<PsiInventoryIn> rs = this.psiInventoryInDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	
	public Page<PsiInventoryIn> find(Page<PsiInventoryIn> page, PsiInventoryIn psiInventoryIn) {
		DetachedCriteria dc = psiInventoryInDao.createDetachedCriteria();
		if(psiInventoryIn.getAddDate()!=null){
			dc.add(Restrictions.ge("addDate",psiInventoryIn.getAddDate()));
		}
		
		if(psiInventoryIn.getAddDateS()!=null){
			dc.add(Restrictions.le("addDate",DateUtils.addDays(psiInventoryIn.getAddDateS(),1)));
		}
		
		if(StringUtils.isNotEmpty(psiInventoryIn.getBillNo())){
			dc.add(Restrictions.or(Restrictions.like("billNo", "%"+psiInventoryIn.getBillNo()+"%"),Restrictions.like("tranLocalNo", "%"+psiInventoryIn.getBillNo()+"%")));
		}
		
		
		if(StringUtils.isNotEmpty(psiInventoryIn.getTranLocalNo())){
			dc.createAlias("this.items", "item");
			dc.add(Restrictions.like("item.productName", "%"+psiInventoryIn.getTranLocalNo()+"%"));
		}
		
		
		if(StringUtils.isNotEmpty(psiInventoryIn.getOperationType())){
			if("other".equals(psiInventoryIn.getOperationType())){
				String [] arr= new String[]{"Inventory Taking Storing","Return Storing","Recall Storing","Transport Storing","Lot Storing"};
				dc.add(Restrictions.not(Restrictions.in("operationType",arr)));
			}else{
				dc.add(Restrictions.eq("operationType", psiInventoryIn.getOperationType()));
			}
			
		}
		
		if(psiInventoryIn.getWarehouseId()!=null){
			dc.add(Restrictions.eq("warehouseId", psiInventoryIn.getWarehouseId()));
		}
		if(psiInventoryIn.getAddUser()!=null&&psiInventoryIn.getAddUser().getId()!=null&&!psiInventoryIn.getAddUser().getId().equals("")){
			dc.add(Restrictions.eq("addUser.id", psiInventoryIn.getAddUser().getId()));
		}
		
		page.setOrderBy(" id desc");
		return psiInventoryInDao.find2(page, dc);
	}
	
	
	public List<PsiInventoryIn> find(PsiInventoryIn psiInventoryIn) {
		DetachedCriteria dc = psiInventoryInDao.createDetachedCriteria();
		if(psiInventoryIn.getAddDate()!=null){
			dc.add(Restrictions.ge("addDate",psiInventoryIn.getAddDate()));
		}
		
		if(psiInventoryIn.getAddDateS()!=null){
			dc.add(Restrictions.le("addDate",DateUtils.addDays(psiInventoryIn.getAddDateS(),1)));
		}
		
		if(StringUtils.isNotEmpty(psiInventoryIn.getBillNo())){
			dc.add(Restrictions.or(Restrictions.like("billNo", "%"+psiInventoryIn.getBillNo()+"%"),Restrictions.like("tranLocalNo", "%"+psiInventoryIn.getBillNo()+"%")));
		}
		
		if(StringUtils.isNotEmpty(psiInventoryIn.getOperationType())){
			if("other".equals(psiInventoryIn.getOperationType())){
				String [] arr= new String[]{"Inventory Taking Storing","Return Storing","Recall Storing","Transport Storing","Lot Storing"};
				dc.add(Restrictions.not(Restrictions.in("operationType",arr)));
			}else{
				dc.add(Restrictions.eq("operationType", psiInventoryIn.getOperationType()));
			}
			
		}
		
		if(psiInventoryIn.getWarehouseId()!=null){
			dc.add(Restrictions.eq("warehouseId", psiInventoryIn.getWarehouseId()));
		}
		
		if(psiInventoryIn.getAddUser()!=null&&psiInventoryIn.getAddUser().getId()!=null&&!psiInventoryIn.getAddUser().getId().equals("")){
			dc.add(Restrictions.eq("addUser.id", psiInventoryIn.getAddUser().getId()));
		}
		
		dc.addOrder(Order.desc("id"));
		return psiInventoryInDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void addSave(PsiInventoryIn psiInventoryIn,Integer returnTestId ,MultipartFile memoFile,MultipartFile excelFile) throws Exception {
		String filePath ="";
		
		Integer warehouseId = psiInventoryIn.getWarehouseId();
		String warehouseName = psiInventoryIn.getWarehouseName();
		
//		String inNo=this.genDao.genSequence("_RKD", 3);
		
		SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMddHHmmsss");
		String flowNo=sdf.format(new Date());
		String inNo=flowNo.substring(0,8)+"_RKD"+flowNo.substring(8);
		psiInventoryIn.setBillNo(inNo);
		
		
		if(memoFile!=null&&memoFile.getSize()!=0){
			filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/psiInventoryIn";
			File baseDir = new File(filePath+"/"+inNo); 
			if(!baseDir.isDirectory())
				baseDir.mkdirs();
			String suffix = memoFile.getOriginalFilename().substring(memoFile.getOriginalFilename().lastIndexOf("."));     
			String name=UUID.randomUUID().toString()+suffix;
			File dest = new File(baseDir,name);
			try {
				FileUtils.copyInputStreamToFile(memoFile.getInputStream(),dest);
				psiInventoryIn.setAttchmentPath("/psi/psiInventoryIn/"+inNo+"/"+name);
			} catch (IOException e) {
				logger.warn(name+"文件保存失败",e);
			}
		}
		
		String operatoinType = psiInventoryIn.getOperationType();
		Stock  stock =new Stock();
		stock.setId(warehouseId);
		
		
		
		if("Lot Storing".equals(psiInventoryIn.getOperationType())){
			//清除运单信息
			psiInventoryIn.setTranLocalId(null);
			psiInventoryIn.setTranLocalNo(null);
			
			psiInventoryIn.setItems(null);
			//解析excel或csv文件
			if(excelFile!=null&&excelFile.getSize()!=0){
				filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/psiInventoryIn";
				File baseDir = new File(filePath+"/"+inNo); 
				if(!baseDir.isDirectory()){
					baseDir.mkdirs();
				}
				String suffix = excelFile.getOriginalFilename().substring(excelFile.getOriginalFilename().lastIndexOf(".")); 
				if(!".csv,.xls,.xlsx".contains(suffix)){
					throw new RuntimeException("Lot Storing文件格式不对，Operation has been canceled");
				}
				psiInventoryIn.setOriginName(excelFile.getOriginalFilename());
				
				//检查文件名是否重复出库
				if(this.isExistFile(psiInventoryIn.getOriginName())){
					throw new RuntimeException("The data file("+psiInventoryIn.getOriginName()+") has been in-stock,please to check in-stock list function!");
				}
				
				String name=UUID.randomUUID().toString()+suffix;
				File dest = new File(baseDir,name);
				try {
					FileUtils.copyInputStreamToFile(excelFile.getInputStream(),dest);
					psiInventoryIn.setDataFile("/psi/psiInventoryIn/"+inNo+"/"+name);
					//放入文件解析文件
					Map<String,Integer> dataMap =Maps.newHashMap();
					if(".csv".equals(suffix)){
						dataMap=resolveCsvFile(excelFile.getInputStream());
					}else if(".xls,.xlsx".contains(suffix)){
						dataMap=resolveExcelFile(excelFile.getInputStream());
					}else{
						throw new RuntimeException("请选择正确的csv或excel文件："+suffix+"");
					}
					
					Map<String,String> fnskuMap=Maps.newHashMap();
					Map<String,Integer> resDataMap=Maps.newHashMap();
					if(dataMap.size()>0){
						fnskuMap=this.psiProductService.getFnskuMap(dataMap.keySet());
					}else{
						throw new RuntimeException("Data file is empty，Operation has been canceled");
					}
					
					for(Map.Entry<String,Integer> entry:dataMap.entrySet()){
						String fnsku = entry.getKey();
						//resDataMap.put(fnskuMap.get(fnsku), dataMap.get(fnsku));
						if(fnskuMap.get(fnsku)==null){
							//如果没有条码，当做sku
							throw new RuntimeException("The barcode ："+fnsku+"cannot find matching sku so cannot in-stock，Operation has been canceled");
						}else{
							resDataMap.put(fnskuMap.get(fnsku), entry.getValue());
						}
					}
					//获取Lot Storing产品的质量类型
					String type =psiInventoryIn.getDataType();
					
					List<PsiInventoryInItem> inItem = Lists.newArrayList(); 
					for(Map.Entry<String,Integer> entry:resDataMap.entrySet()){
						String sku = entry.getKey();
						PsiInventoryInItem  item = new PsiInventoryInItem();
						//产出该sku的产品名   国家  颜色
						Integer dataQuantity =entry.getValue();
						PsiSku psiSku=this.psiProductService.getSkuBySku(sku,"1");
						if(psiSku==null){
							throw new RuntimeException("The sku :"+sku+"not exist,Operation has been canceled ");
						}
						String productName=psiSku.getProductName();
						Integer productId=psiSku.getProductId();
						String country = psiSku.getCountry();
						String color = psiSku.getColor();
						//生成入库单item
						item.setProductId(productId);
						item.setProductName(productName);
						item.setCountryCode(country);
						item.setColorCode(color);
						item.setSku(sku);
						item.setInventoryIn(psiInventoryIn);
						item.setQualityType(type);
						item.setQuantity(dataQuantity);
						
						Integer timelyQuantity=0;
						//查库存数据
						PsiInventory  inventory = this.psiInventoryService.findBySku(sku, warehouseId);
						
						if(inventory!=null){
							/**
							 *如果用inventory_reversion_log表统计数算出及时库存数据，开启下面注释
							 * 
							 */
							//this.psiInventoryLogService.getSumByInventory(warehouseId,inventory);
							if(type.equals("new")){
								inventory.setNewQuantity(inventory.getNewQuantity()+dataQuantity);
								timelyQuantity=inventory.getNewQuantity();
							}else if(type.equals("old")){
								inventory.setOldQuantity(inventory.getOldQuantity()+dataQuantity);
								timelyQuantity=inventory.getOldQuantity();
							}else if(type.equals("broken")){
								inventory.setBrokenQuantity(inventory.getBrokenQuantity()+dataQuantity);
								timelyQuantity=inventory.getBrokenQuantity();
							}else if(type.equals("renew")){
								inventory.setRenewQuantity(inventory.getRenewQuantity()+dataQuantity);
								timelyQuantity=inventory.getRenewQuantity();
							}else if(type.equals("spares")){
								inventory.setSparesQuantity(inventory.getSparesQuantity()+dataQuantity);
								timelyQuantity=inventory.getSparesQuantity();
							}else if(type.equals("offline")){
								inventory.setOfflineQuantity(inventory.getOfflineQuantity()+dataQuantity);
								timelyQuantity=inventory.getOfflineQuantity();
							}
						}else{
							//重新插入一条新的库存记录
							inventory= new PsiInventory();
							inventory.setProductId(productId);
							inventory.setProductName(productName);
							inventory.setColorCode(color);
							inventory.setCountryCode(country);
							inventory.setSku(sku);
							inventory.setWarehouse(stock);
							inventory.setWarehouseName(warehouseName);
							if(type.equals("new")){
								inventory.setNewQuantity(dataQuantity);
							}else{
								inventory.setNewQuantity(0);
							}
							if(type.equals("old")){
								inventory.setOldQuantity(dataQuantity);
							}else{
								inventory.setOldQuantity(0);
							}
							if(type.equals("broken")){
								inventory.setBrokenQuantity(dataQuantity);
							}else{
								inventory.setBrokenQuantity(0);
							}
							if(type.equals("renew")){
								inventory.setRenewQuantity(dataQuantity);
							}else{
								inventory.setRenewQuantity(0);
							}
							if(type.equals("spares")){
								inventory.setSparesQuantity(dataQuantity);
							}else{
								inventory.setSparesQuantity(0);
							}
							if(type.equals("offline")){
								inventory.setOfflineQuantity(dataQuantity);
							}else{
								inventory.setOfflineQuantity(0);
							}
							timelyQuantity=dataQuantity;
						}
						
						item.setTimelyQuantity(timelyQuantity);
						inItem.add(item);
						inventory.setUpdateDate(new Date());
						this.psiInventoryDao.save(inventory);
						//添加操作记录
						this.psiInventoryService.savelog(operatoinType, type, dataQuantity, "", color, country, productId, productName, warehouseId,inNo,sku,psiInventoryIn.getWarehouseId(),warehouseName,timelyQuantity);
					}
					//保存items
					psiInventoryIn.setItems(inItem);
					
					
				} catch (IOException e) {
					logger.warn(name+"文件保存失败",e);
				}
			}else{
				throw new RuntimeException("data file is empty，Operation has been canceled");
			}
			
		}else{
			//清除Lot Storing操作的信息
			psiInventoryIn.setDataType(null);
			
			//获取出库仓库 
			PsiTransportOrder tranOrder=null;
			LcPsiTransportOrder lcTranOrder=null;
			Integer outStockId=null;
			String outStockName=null;
			if("Transport Storing".equals(psiInventoryIn.getOperationType())){
				String tranNo =psiInventoryIn.getTranLocalNo();
				if(tranNo.contains("_LC_")){
					lcTranOrder =lcPsiTransportOrderService.get(tranNo);
					outStockId=lcTranOrder.getFromStore().getId();
					outStockName= lcTranOrder.getFromStore().getName();
				}else{
					tranOrder =psiTransportOrderService.get(tranNo);
					outStockId=tranOrder.getFromStore().getId();
					outStockName= tranOrder.getFromStore().getName();
				}
				
			}
			
			Map<String,PsiInventory>  inventoryMap = Maps.newHashMap();
			Map<String,PsiInventory>  curInventoryMap = Maps.newHashMap();  //及时库存po
			Map<String,Integer> skuMap = Maps.newHashMap();
			
			//for(PsiInventoryInItem item:psiInventoryIn.getItems()){
			for (Iterator<PsiInventoryInItem> iterator = psiInventoryIn.getItems().iterator(); iterator.hasNext();) {
				PsiInventoryInItem item = iterator.next();
				if("Transport Storing".equals(psiInventoryIn.getOperationType())){
					String type="0";
					if("offline".equals(item.getQualityType())){
						type="1";
					}
					skuMap.put(item.getSku()+","+type, item.getQuantity());
				}
				//如果数量为0的话，不进行库存操作
				if(item.getQuantity()==null||item.getQuantity()==0){
					iterator.remove();
					continue;
				}
				
				item.setInventoryIn(psiInventoryIn);
				//处理及时库存数据  组装map   key:仓库、产品、国家、颜色，值psiInventory（new、old。。。）
				PsiInventory inventory=null;
				String key = item.getSku();
				String country= item.getCountryCode();
				String color  = item.getColorCode();
				String productName =item.getProductName();
				Integer productId = item.getProductId();
				if(inventoryMap.get(key)==null){
					inventory= new PsiInventory();
					inventory.setColorCode(color);
					inventory.setCountryCode(country);
					inventory.setProductId(productId);
					inventory.setProductName(productName);
					inventoryMap.put(key, inventory);
				}else{
					inventory=inventoryMap.get(key);
				}
				
				if(curInventoryMap.get(key)==null){
					curInventoryMap.put(key,this.psiInventoryService.findBySku(key, warehouseId));
				}
				
				
				PsiInventory  curInventory = curInventoryMap.get(key);
				
				if(item.getQualityType()!=null&&!item.getQualityType().equals("")){
					Integer timelyQ=item.getQuantity();
					if(item.getQualityType().equals("new")){
						if(curInventory!=null){
							timelyQ=curInventory.getNewQuantity()+item.getQuantity();
						}
						inventory.setNewQuantity(item.getQuantity());
						this.psiInventoryService.savelog(operatoinType, "new", item.getQuantity(), item.getRemark(), color, country, productId, productName, warehouseId,inNo,key,outStockId,outStockName,timelyQ);
					}else if(item.getQualityType().equals("old")){
						if(curInventory!=null){
							timelyQ=curInventory.getOldQuantity()+item.getQuantity();
						}
						inventory.setOldQuantity(item.getQuantity());
						this.psiInventoryService.savelog(operatoinType, "old", item.getQuantity(), item.getRemark(), color, country, productId, productName, warehouseId,inNo,key,outStockId,outStockName,timelyQ);
					}else if(item.getQualityType().equals("broken")){
						if(curInventory!=null){
							timelyQ=curInventory.getBrokenQuantity()+item.getQuantity();
						}
						inventory.setBrokenQuantity(item.getQuantity());
						this.psiInventoryService.savelog(operatoinType, "broken", item.getQuantity(), item.getRemark(), color, country, productId, productName, warehouseId,inNo,key,outStockId,outStockName,timelyQ);
					}else if(item.getQualityType().equals("renew")){
						if(curInventory!=null){
							timelyQ=curInventory.getRenewQuantity()+item.getQuantity();
						}
						inventory.setRenewQuantity(item.getQuantity());
						this.psiInventoryService.savelog(operatoinType, "renew", item.getQuantity(), item.getRemark(), color, country, productId, productName, warehouseId,inNo,key,outStockId,outStockName,timelyQ);
					}else if(item.getQualityType().equals("spares")){
						if(curInventory!=null){
							timelyQ=curInventory.getSparesQuantity()+item.getQuantity();
						}
						inventory.setSparesQuantity(item.getQuantity());
						this.psiInventoryService.savelog(operatoinType, "spares", item.getQuantity(), item.getRemark(), color, country, productId, productName, warehouseId,inNo,key,outStockId,outStockName,timelyQ);
					}else if(item.getQualityType().equals("offline")){
						if(curInventory!=null){
							timelyQ=curInventory.getOfflineQuantity()+item.getQuantity();
						}
						inventory.setOfflineQuantity(item.getQuantity());
						this.psiInventoryService.savelog(operatoinType, "offline", item.getQuantity(), item.getRemark(), color, country, productId, productName, warehouseId,inNo,key,outStockId,outStockName,timelyQ);
					}
				}
			}
			
			//组成sku，质量类型map
			Map<String,Integer>  timelyMap = Maps.newHashMap();
			//处理map
			for(Map.Entry<String, PsiInventory> entry : inventoryMap.entrySet()){
				String key = entry.getKey();
				PsiInventory  temp = entry.getValue();
				PsiInventory  inventory = curInventoryMap.get(key);
				if(inventory!=null){
					/**
					 *如果用inventory_reversion_log表统计数算出及时库存数据，开启下面注释
					 * 
					 */
					//this.psiInventoryLogService.getSumByInventory(warehouseId,inventory);
					
					if(temp.getNewQuantity()!=null){
						inventory.setNewQuantity(inventory.getNewQuantity()+temp.getNewQuantity());
						timelyMap.put(key+",new",inventory.getNewQuantity());
					}
					if(temp.getOldQuantity()!=null){
						inventory.setOldQuantity(inventory.getOldQuantity()+temp.getOldQuantity());
						timelyMap.put(key+",old",inventory.getOldQuantity());
					}
					if(temp.getBrokenQuantity()!=null){
						inventory.setBrokenQuantity(inventory.getBrokenQuantity()+temp.getBrokenQuantity());
						timelyMap.put(key+",broken",inventory.getBrokenQuantity());
					}
					if(temp.getRenewQuantity()!=null){
						inventory.setRenewQuantity(inventory.getRenewQuantity()+temp.getRenewQuantity());
						timelyMap.put(key+",renew",inventory.getRenewQuantity());
					}
					if(temp.getSparesQuantity()!=null){
						inventory.setSparesQuantity(inventory.getSparesQuantity()+temp.getSparesQuantity());
						timelyMap.put(key+",spares",inventory.getSparesQuantity());
					}
					if(temp.getOfflineQuantity()!=null){
						inventory.setOfflineQuantity(inventory.getOfflineQuantity()+temp.getOfflineQuantity());
						timelyMap.put(key+",offline",inventory.getOfflineQuantity());
					}
				}else{
					//重新插入一条新的库存记录
					inventory= new PsiInventory();
					inventory.setProductId(temp.getProductId());
					inventory.setProductName(temp.getProductName());
					inventory.setColorCode(temp.getColorCode());
					inventory.setCountryCode(temp.getCountryCode());
					inventory.setSku(key);
					inventory.setWarehouse(stock);
					inventory.setWarehouseName(warehouseName);
					if(temp.getNewQuantity()!=null){
						inventory.setNewQuantity(temp.getNewQuantity());
						timelyMap.put(key+",new",inventory.getNewQuantity());
					}else{
						inventory.setNewQuantity(0);
					}
					if(temp.getOldQuantity()!=null){
						inventory.setOldQuantity(temp.getOldQuantity());
						timelyMap.put(key+",old",inventory.getOldQuantity());
					}else{
						inventory.setOldQuantity(0);
					}
					if(temp.getBrokenQuantity()!=null){
						inventory.setBrokenQuantity(temp.getBrokenQuantity());
						timelyMap.put(key+",broken",inventory.getBrokenQuantity());
					}else{
						inventory.setBrokenQuantity(0);
					}
					if(temp.getRenewQuantity()!=null){
						inventory.setRenewQuantity(temp.getRenewQuantity());
						timelyMap.put(key+",renew",inventory.getRenewQuantity());
					}else{
						inventory.setRenewQuantity(0);
					}
					if(temp.getSparesQuantity()!=null){
						inventory.setSparesQuantity(temp.getSparesQuantity());
						timelyMap.put(key+",spares",inventory.getSparesQuantity());
					}else{
						inventory.setSparesQuantity(0);
					}
					if(temp.getOfflineQuantity()!=null){
						inventory.setOfflineQuantity(temp.getOfflineQuantity());
						timelyMap.put(key+",offline",inventory.getOfflineQuantity());
					}else{
						inventory.setOfflineQuantity(0);
					}
				}
				inventory.setUpdateDate(new Date());
				this.psiInventoryDao.save(inventory);
			}
			
			for(PsiInventoryInItem item:psiInventoryIn.getItems()){
				String key=item.getSku()+","+item.getQualityType();
				if(timelyMap.get(key)!=null){
					item.setTimelyQuantity(timelyMap.get(key));
				}
			}
			
			if("Transport Storing".equals(psiInventoryIn.getOperationType())){
				boolean isFinished = false;
				if(tranOrder!=null){//运单为空则为理诚运单
					//如果是空运看下是否多个飞机都收完 
					if("0".equals(tranOrder.getModel())&&tranOrder.getPlaneNum()==(tranOrder.getPlaneIndex()+1)){
						isFinished=true;
					}else{
						isFinished=true;
					}
					//改变对应运单的接收数量,sku和接收item
					changeTranOrder(tranOrder,skuMap,isFinished);
					if(psiInventoryIn.getItems().size()==0){
						return ;
					}
					psiInventoryIn.setTranLocalNo(tranOrder.getTransportNo());
				}else{
					//如果是空运看下是否多个飞机都收完 
					if("0".equals(lcTranOrder.getModel())&&lcTranOrder.getPlaneNum()==(lcTranOrder.getPlaneIndex()+1)){
						isFinished=true;
					}else{
						isFinished=true;
					}
					//改变对应运单的接收数量,sku和接收item
					changeTranOrder(lcTranOrder,skuMap,isFinished);
					if(psiInventoryIn.getItems().size()==0){
						return ;
					}
					psiInventoryIn.setTranLocalNo(lcTranOrder.getTransportNo());
				}
				
			}else{
				psiInventoryIn.setTranLocalId(null);
				psiInventoryIn.setTranLocalNo(null);
			}
		}
		psiInventoryIn.setAddDate(new Date());
		psiInventoryIn.setAddUser(UserUtils.getUser());
		psiInventoryInDao.save(psiInventoryIn);
		if(returnTestId!=null){
			//如果退货检修后入库，保存出库单号到检修单
			this.returnTestService.updateStaAndStockInNo(returnTestId, psiInventoryIn.getBillNo());
		}
	}
	
	public void changeTranOrder(PsiTransportOrder tranOrder,Map<String,Integer> skuMap ,boolean isFinished){
		if(isFinished){
			tranOrder.setTransportSta("5");//完成状态
		}else{
			tranOrder.setTransportSta("4");//部分收货状态
		}
		
		Set<String> skuInfoSet = Sets.newHashSet();
		//誊写接收数据
		for(PsiTransportOrderItem item:tranOrder.getItems()){
			String skuInfoKey=item.getSku()+","+item.getOfflineSta();
			//接收数量有值就累加上上次接收的
			if(item.getReceiveQuantity()==null){
				item.setReceiveQuantity(skuMap.get(skuInfoKey));
			}else{
				//本次接收的数量+上次接收的数量
				item.setReceiveQuantity(skuMap.get(skuInfoKey)+item.getReceiveQuantity());
			}
			skuInfoSet.add(skuInfoKey);
		}
		
		for(Map.Entry<String, Integer> entry:skuMap.entrySet()){
			String skuKey = entry.getKey();
			if(!skuInfoSet.contains(skuKey)){
				String sku =skuKey.split(",")[0];
				//如果前台有新的sku加入，说明出库时粘贴sku错误
				PsiSku psiSku=	psiProductService.getSkuBySku(sku,"1");
				PsiTransportOrderItem item = new PsiTransportOrderItem();
				PsiProduct product = new PsiProduct();
				product.setId(psiSku.getProductId());
				item.setProduct(product);
				item.setProductName(psiSku.getProductName());
				item.setCountryCode(psiSku.getCountry());
				item.setColorCode(psiSku.getColor());
				item.setSku(sku);
				item.setQuantity(0);  		              //运单数量
				item.setShippedQuantity(0);               //运单发货数量
				item.setReceiveQuantity(skuMap.get(sku+","+item.getOfflineSta())); //接收数量
				item.setTransportOrder(tranOrder);
				tranOrder.getItems().add(item);
			}
		}
		
		tranOrder.setPlaneIndex(tranOrder.getPlaneIndex()+1);
		//添加操作出库人  和离港时间
		tranOrder.setOperArrivalDate(new Date());
		tranOrder.setOperArrivalFixedDate(new Date());
		tranOrder.setOperArrivalUser(UserUtils.getUser());
		psiTransportOrderDao.save(tranOrder);
	}
	
	public void changeTranOrder(LcPsiTransportOrder tranOrder,Map<String,Integer> skuMap ,boolean isFinished){
		if(isFinished){
			tranOrder.setTransportSta("5");//完成状态
		}else{
			tranOrder.setTransportSta("4");//部分收货状态
		}
		
		Set<String> skuInfoSet = Sets.newHashSet();
		//誊写接收数据
		for(LcPsiTransportOrderItem item:tranOrder.getItems()){
			String skuInfoKey=item.getSku()+","+item.getOfflineSta();
			//接收数量有值就累加上上次接收的
			if(item.getReceiveQuantity()==null){
				item.setReceiveQuantity(skuMap.get(skuInfoKey));
			}else{
				//本次接收的数量+上次接收的数量
				item.setReceiveQuantity(skuMap.get(skuInfoKey)+item.getReceiveQuantity());
			}
			skuInfoSet.add(skuInfoKey);
		}
		
		for(Map.Entry<String, Integer> entry:skuMap.entrySet()){
			String skuKey = entry.getKey();
			if(!skuInfoSet.contains(skuKey)){
				String sku =skuKey.split(",")[0];
				//如果前台有新的sku加入，说明出库时粘贴sku错误
				PsiSku psiSku=	psiProductService.getSkuBySku(sku,"1");
				LcPsiTransportOrderItem item = new LcPsiTransportOrderItem();
				PsiProduct product = new PsiProduct();
				product.setId(psiSku.getProductId());
				item.setProduct(product);
				item.setProductName(psiSku.getProductName());
				item.setCountryCode(psiSku.getCountry());
				item.setColorCode(psiSku.getColor());
				item.setSku(sku);
				item.setQuantity(0);  		              //运单数量
				item.setShippedQuantity(0);               //运单发货数量
				item.setReceiveQuantity(skuMap.get(sku+","+item.getOfflineSta())); //接收数量
				item.setTransportOrder(tranOrder);
				tranOrder.getItems().add(item);
			}
		}
		
		tranOrder.setPlaneIndex(tranOrder.getPlaneIndex()+1);
		//添加操作出库人  和离港时间
		tranOrder.setOperArrivalDate(new Date());
		tranOrder.setOperArrivalFixedDate(new Date());
		tranOrder.setOperArrivalUser(UserUtils.getUser());
		lcPsiTransportOrderDao.save(tranOrder);
	}
	
	
	public Map<String,Integer> resolveCsvFile(InputStream csvFile) throws IOException{
		CSVReader reader = new CSVReader(new InputStreamReader(csvFile, "utf-8"));
		List<String[]> data = reader.readAll();
		reader.close();
		//组成一个map key：sku  value:数量
		Map<String,Integer> skuMap = Maps.newHashMap();
		if(data.size()==1&&StringUtils.isEmpty(data.get(0)[0])){
			throw new RuntimeException("csv文件中没数据!，Operation has been canceled!");
		}
		for(String [] arr:data){
			String key = arr[0].trim();
			Integer quantity=1;
			//如果csv中存在数量字段
			if(arr.length>1){
				quantity = Integer.parseInt(arr[1].trim());	
			}
			if(skuMap.get(key)!=null){
				quantity+=skuMap.get(key);
			}
			skuMap.put(key, quantity);
		}
		return skuMap;
	}
	
	
	public Map<String,Integer> resolveExcelFile(InputStream excelFile) throws Exception{
		Map<String,Integer> fnSkuMap = Maps.newHashMap();
			Workbook workBook = WorkbookFactory.create(excelFile);
			Sheet sheet = workBook.getSheet("BatchInventoryData");
			if(sheet==null){
				sheet = workBook.getSheet("BATCHINVENTORYDATA");
			}
			if(sheet==null){
				sheet = workBook.getSheetAt(0);
			}
			
			sheet.setForceFormulaRecalculation(true);
			int rows = sheet.getPhysicalNumberOfRows();
			if(rows <= 0){
				throw new RuntimeException("Excel file no data，Operation has been canceled!");
			}
			
			// 循环行Row
			for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
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
				//解析上面的list看       组成一个map key：sku  value:数量
				if(arrCell.size()==0){
					continue;
				}   
				
				String key = arrCell.get(0);
				//暂时对HPB灰色的错误条码进行手动处理成正确的
				if(key.equals("X0007SGWE")){
					key="X0007SGWEB";
				}
				
				Integer quantity=1;
				//如果csv中存在数量字段
				if(arrCell.size()>1){
					BigDecimal bigDecimal=new BigDecimal(arrCell.get(1));	
					quantity = bigDecimal.intValue();
				}
				if(fnSkuMap.get(key)!=null){
					quantity+=fnSkuMap.get(key);
				}
				fnSkuMap.put(key, quantity);
			}
//		}
		return fnSkuMap;
	}
	

	
	/**
	 * 执行批量调整库存文件，与库存数据对比，自动生成出入库信息单
	 * @throws Exception 
	 * @throws IOException 
	 * 
	 */
	
	@Transactional(readOnly = false)
	public void batchSave(MultipartFile excelFile,PsiInventoryIn psiInventoryIn) throws IOException, Exception{
	    Integer warehouseId=psiInventoryIn.getWarehouseId();
	    String warehouseName=psiInventoryIn.getWarehouseName();
	    String remark =psiInventoryIn.getRemark();
	    String operationType =psiInventoryIn.getOperationType();
		// 判断文件是sku+数量，还是产品名+国家+颜色+数量
		String filePath="";
		String fileSavePath="";
		//解析excel或csv文件
		if(excelFile!=null&&excelFile.getSize()!=0){
			filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/psiInventoryIn";
			File baseDir = new File(filePath);
			if(!baseDir.isDirectory()){
				baseDir.mkdirs();
			}
			String suffix = excelFile.getOriginalFilename().substring(excelFile.getOriginalFilename().lastIndexOf(".")); 
			if(".xls,.xlsx".contains(suffix)){
				String name=UUID.randomUUID().toString()+suffix;
				File dest = new File(baseDir,name);
				try {
					FileUtils.copyInputStreamToFile(excelFile.getInputStream(),dest);
					fileSavePath="/psi/psiInventoryIn/"+name;
				} catch (IOException e) {
					logger.warn(name+"文件保存失败",e);
				}
				resolveExcelInventoryFile(excelFile.getInputStream(),warehouseId,fileSavePath,warehouseName,remark,operationType);   
			}else{
				throw new RuntimeException("请选择正确的excel文件："+suffix+"");
			}
		}
		
	}
	
	private void resolveExcelInventoryFile(InputStream excelFile,Integer warehouseId,String filePath,String warehouseName,String remark,String operationType) throws Exception{
			Map<String,String> productSku =this.psiProductService.getAllBandingSku();
			Map<String,Integer> skuMap = Maps.newHashMap();
			Workbook workBook = WorkbookFactory.create(excelFile);
			Sheet sheet = workBook.getSheet("BatchReviseInventoryData");
			if(sheet==null){
				sheet = workBook.getSheet("BATCHREVISEINVENTORYDATA");
			}
			if(sheet==null){
				sheet = workBook.getSheetAt(0);
			}
			
			sheet.setForceFormulaRecalculation(true);
			int rows = sheet.getPhysicalNumberOfRows();
			if(rows <= 0){
				throw new RuntimeException("Excel文件中没数据!，Operation has been canceled");
			}
			
			StringBuffer sb =new StringBuffer("");
			Map<String,String> fnSkuMap =this.psiProductService.getFnskuMap(null);
			// 循环行Row
			for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
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
				//解析上面的list看       组成一个map key：sku  value:数量
				String sku = "";
				Integer quantity=null;
				//如果是产品名  + 国家  +颜色   +数量
				if(arrCell.size()==4){
					String productName=arrCell.get(0);
					String countryCode=arrCell.get(1);
					String colorCode =arrCell.get(2);
					BigDecimal bigDecimal=new BigDecimal(arrCell.get(3));	
					quantity = bigDecimal.intValue();
					//从产品名   国家    颜色   匹配  产品
					sku=productSku.get(productName+","+countryCode+","+colorCode);
					if(StringUtils.isEmpty(sku)){
						sb.append(productName+","+countryCode+","+colorCode);
					}
				}else if(arrCell.size()==2){
					String fnSku=arrCell.get(0).trim();
					if(StringUtils.isEmpty(fnSku)){
						continue;
					}
					//传入的有可能是fnsku，找不到对应的sku就当做sku处理
					sku =fnSkuMap.get(fnSku);
					if(StringUtils.isEmpty(sku)){
						sku = fnSku;
					}
					if(StringUtils.isBlank(arrCell.get(1))){
						quantity=1;
					}else{
						BigDecimal bigDecimal=new BigDecimal(arrCell.get(1));	
						quantity = bigDecimal.intValue();
					}
					
				}else if(arrCell.size()==1){
					String fnSku=arrCell.get(0).trim();
					if(StringUtils.isEmpty(fnSku)){
						continue;
					}
					//传入的有可能是fnsku，找不到对应的sku就当做sku处理
					sku =fnSkuMap.get(fnSku);
					if(StringUtils.isEmpty(sku)){
						sku = fnSku;
					}
					quantity=1;
				}
				
				if(skuMap.get(sku)!=null){
					quantity+=skuMap.get(sku);
				}
				skuMap.put(sku, quantity);
			}
			
			
			
			//查出仓库  sku数量
			Map<String,Integer>  inventoryMap = this.psiInventoryService.getProductSkuByHouseId(warehouseId);
			Map<String,Integer>  psiOutMap = Maps.newHashMap();
			Map<String,Integer>  psiInMap  = Maps.newHashMap();
			
			if(inventoryMap.size()>0){
				//迭代库存sku  并筛选出出入库map
				for(Map.Entry<String, Integer> entry:inventoryMap.entrySet()){
					String sku = entry.getKey();
					if(skuMap.containsKey(sku)){
						Integer checkQuantity=skuMap.get(sku);
						/*Integer inventoryQuantity=entry.getValue();
						if(checkQuantity>inventoryQuantity){
							psiInMap.put(sku, checkQuantity-inventoryQuantity);
						}else if(checkQuantity<inventoryQuantity){
							psiOutMap.put(sku, inventoryQuantity-checkQuantity);
						}*/
						psiInMap.put(sku, checkQuantity);
					}else{
						//盘点没有，库里多了 直接盘出
						/*if(!inventoryMap.get(sku).equals(0)){
							psiOutMap.put(sku, entry.getValue());
						}*/
					}
				}
				
				for(Map.Entry<String, Integer> entry1 :skuMap.entrySet()){
					String sku = entry1.getKey();
					//如果文件里有的，库里没有直接盘入
					if(!inventoryMap.containsKey(sku)){
						psiInMap.put(sku, entry1.getValue());
					}
				}
			}else{
				//如果仓库没数据，都是入库操作
				for(Map.Entry<String, Integer> entry1 :skuMap.entrySet()){
					String sku = entry1.getKey();
					psiInMap.put(sku, entry1.getValue());
				}
			}
			
			if(!StringUtils.isEmpty(sb)){
				throw new RuntimeException("以下产品没有匹配sku"+sb.toString()+"不能批量调平数据，Operation has been canceled");
			}
			if(psiInMap.keySet().size()>0){
				this.exeInventoryIn(psiInMap, warehouseId,filePath,warehouseName,remark,operationType);
			}
			
			/*if(psiOutMap.keySet().size()>0){
				this.exeInventoryOut(psiOutMap, warehouseId,filePath,warehouseName,remark,operationType);
			}*/
			
	}
	
	
	
	@SuppressWarnings("static-access")
	private String getValue(Cell cell) {
		if (cell.getCellType() == cell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue()).trim();
		} else if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
			return String.valueOf(cell.getNumericCellValue()).trim();
		} else {
			return String.valueOf(cell.getStringCellValue()).trim();
		}
	}
	
	
	private void exeInventoryOut(Map<String,Integer> dataMap,Integer warehouseId,String filePath,String wareHouseName,String remark,String operatoinType){
		String type="new";
		PsiInventoryOut psiInventoryOut =new PsiInventoryOut();
		String outNo = this.genDao.genSequence("_CKD",3);
		//把运单信息    fba信息置空
	//	psiInventoryOut.setTranFbaId(null);
		psiInventoryOut.setTranFbaNo(null);
		psiInventoryOut.setTranLocalId(null);
		psiInventoryOut.setTranLocalNo(null);
		
		psiInventoryOut.setItems(null);
		
		
		//获取Lot Storing产品的质量类型
		List<PsiInventoryOutItem> inItem = Lists.newArrayList(); 
		for(Map.Entry<String,Integer> entry:dataMap.entrySet()){
			String sku = entry.getKey();
			PsiInventoryOutItem  item = new PsiInventoryOutItem();
			//产出该sku的产品名   国家  颜色
			Integer dataQuantity =entry.getValue();
			
			PsiInventory inventory = psiInventoryService.findBySku(sku, warehouseId);
			if(inventory==null){
				throw new RuntimeException("The sku"+sku+" no data in inventory，cannot out-stock，Operation has been canceled!");
			}
			
			String productName=inventory.getProductName();
			Integer productId=inventory.getProductId();
			String country = inventory.getCountryCode();
			String color = inventory.getColorCode();
			//生成入库单item
			item.setProductId(productId);
			item.setProductName(productName);
			item.setCountryCode(country);
			item.setColorCode(color);
			item.setSku(sku);
			item.setInventoryOut(psiInventoryOut);
			item.setQualityType(type);
			item.setQuantity(dataQuantity);
			inItem.add(item);
			
			
			if(type.equals("new")){
				inventory.setNewQuantity(inventory.getNewQuantity()-item.getQuantity());
//				if(inventory.getNewQuantity()<0){
//					throw new RuntimeException("批量出库后库存不足,sku:"+sku+",类型："+type+",库存数量："+inventory.getRenewQuantity()+item.getQuantity()+",本次出库数量："+item.getQuantity()+"Operation has been canceled");
//				}
				//添加操作记录
				this.psiInventoryService.savelog(operatoinType, "new", 0-item.getQuantity(), "", inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), warehouseId,outNo,sku,warehouseId,inventory.getWarehouseName(),inventory.getNewQuantity());
			}
			//保存库存记录
			inventory.setUpdateDate(new Date());  //最近更新的查询时最先显示
			psiInventoryDao.save(inventory);
		}
		psiInventoryOut.setRemark(remark);
		psiInventoryOut.setBillNo(outNo);
		psiInventoryOut.setWarehouseId(warehouseId);
		psiInventoryOut.setWarehouseName(wareHouseName);
		psiInventoryOut.setOperationType(operatoinType);
		psiInventoryOut.setAttchmentPath(filePath);
		psiInventoryOut.setItems(inItem);
		psiInventoryOut.setAddDate(new Date());
		psiInventoryOut.setAddUser(UserUtils.getUser());
		this.psiInventoryOutDao.save(psiInventoryOut);
	}
	
	private void exeInventoryIn(Map<String,Integer> dataMap,Integer warehouseId,String filePath,String warehouseName,String remark,String operatoinType) throws IOException{
			Stock stock =  new Stock();
			stock.setId(warehouseId);
			
			PsiInventoryIn psiInventoryIn  =new PsiInventoryIn();
			//清除运单信息
			psiInventoryIn.setTranLocalId(null);
			psiInventoryIn.setTranLocalNo(null);
			psiInventoryIn.setItems(null);
			
			//获取Lot Storing产品的质量类型
			String type ="new";
			//String inNo = this.genSeq("_RKD");
			String inNo =this.createFlowNo();
			psiInventoryIn.setBillNo(inNo);
			psiInventoryIn.setWarehouseId(warehouseId);
			psiInventoryIn.setWarehouseName(warehouseName);
			psiInventoryIn.setOperationType(operatoinType);
			psiInventoryIn.setAttchmentPath(filePath);
			
			List<PsiInventoryInItem> inItem = Lists.newArrayList(); 
			for(Map.Entry<String,Integer> entry:dataMap.entrySet()){
				String sku = entry.getKey();
				PsiInventoryInItem  item = new PsiInventoryInItem();
				//产出该sku的产品名   国家  颜色
				Integer dataQuantity =entry.getValue();
				PsiSku psiSku=this.psiProductService.getSkuBySku(sku,"1");
				String productName="";
				Integer productId = null;
				String country = "";
				String color = "";
				//如果原来解绑定了，查下库里有没，如果库里有就用库里的信息
				if(psiSku!=null){
					productName=psiSku.getProductName();
					productId=psiSku.getProductId();
					country = psiSku.getCountry();
					color = psiSku.getColor();
				}else{
					PsiInventory inv =this.psiInventoryService.findBySku(sku, warehouseId);
					if(inv!=null){
						productName=inv.getProductName();
						productId=inv.getProductId();
						country = inv.getCountryCode();
						color = inv.getColorCode();
					}else{
						psiSku=this.psiProductService.getSkuBySku(sku,"0");
						if(psiSku!=null){
							productName=psiSku.getProductName();
							productId=psiSku.getProductId();
							country = psiSku.getCountry();
							color = psiSku.getColor();
						}else{
							throw new RuntimeException("The sku:"+sku+"is not used sku and no data in stock!");
						}
					}
				}
			
				//生成入库单item
				item.setProductId(productId);
				item.setProductName(productName);
				item.setCountryCode(country);
				item.setColorCode(color);
				item.setSku(sku);
				item.setInventoryIn(psiInventoryIn);
				item.setQualityType(type);
				item.setQuantity(dataQuantity);
				inItem.add(item);
				
				//查库存数据
				PsiInventory  inventory = this.psiInventoryService.findBySku(sku, warehouseId);
				
				if(inventory!=null){
					/**
					 *如果用inventory_reversion_log表统计数算出及时库存数据，开启下面注释
					 * 
					 */
					//this.psiInventoryLogService.getSumByInventory(warehouseId,inventory);
					if(type.equals("new")){
						inventory.setNewQuantity(inventory.getNewQuantity()+dataQuantity);
					}
				}else{
					//重新插入一条新的库存记录
					inventory= new PsiInventory();
					inventory.setProductId(productId);
					inventory.setProductName(productName);
					inventory.setColorCode(color);
					inventory.setCountryCode(country);
					inventory.setSku(sku);
					inventory.setWarehouse(stock);
					inventory.setWarehouseName(warehouseName);
					if(type.equals("new")){
						inventory.setNewQuantity(dataQuantity);
					}
					inventory.setOldQuantity(0);
					inventory.setBrokenQuantity(0);
					inventory.setRenewQuantity(0);
					inventory.setSparesQuantity(0);
					inventory.setOfflineQuantity(0);
				}
				inventory.setUpdateDate(new Date());
				this.psiInventoryDao.save(inventory);
				//添加操作记录
				this.psiInventoryService.savelog(operatoinType, type, dataQuantity, "", color, country, productId, productName, warehouseId,inNo,sku,psiInventoryIn.getWarehouseId(),stock.getName(),inventory.getNewQuantity());
			}
			//保存items
			psiInventoryIn.setItems(inItem);
			psiInventoryIn.setRemark(remark);
			psiInventoryIn.setAddDate(new Date());
			psiInventoryIn.setAddUser(UserUtils.getUser());
			this.psiInventoryInDao.save(psiInventoryIn);
		}
	
	@Transactional(readOnly = false)
	public String createFlowNo() throws IOException {
		return this.genDao.genSequence("_RKD",3);
	}
	
//	@Transactional(readOnly = false)
//	private String genSeq(String code){
//		return this.genDao.genSequence(code,3);
//	}
	
	
	public boolean isExistFile(String fileName){
		boolean flag=false;
		String sql="SELECT a.`origin_name` FROM psi_inventory_in AS a  WHERE a.`operation_type`='Lot Storing' AND a.`origin_name`=:p1 LIMIT 1";
		List<String> list =this.psiInventoryInDao.findBySql(sql,new Parameter(fileName));
		if(list.size()>0){
			flag=true;
		}
		return flag;
	}
	
	

	
	@Transactional(readOnly = false)
	public String updateRemark(Integer id,String content,String flag){
		try{
			String paraSql="";
			if("1".equals(flag)){
				paraSql=" a.`remark` ";
			}else if("2".equals(flag)){
				paraSql=" a.`tran_man` ";
			}else if("3".equals(flag)){
				paraSql=" a.`car_no` ";
			}else if("4".equals(flag)){
				paraSql=" a.`phone` ";
			}
			
			String sql ="UPDATE psi_inventory_in AS a SET "+paraSql+"=:p2 WHERE a.`id`=:p1";
			this.psiInventoryInDao.updateBySql(sql, new Parameter(id,content));
			return "true";
		}catch (Exception ex){
			return "false";
		}
	}
}
