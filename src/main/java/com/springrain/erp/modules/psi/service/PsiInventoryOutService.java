/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.MapValueComparator;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.dao.PsiInventoryDao;
import com.springrain.erp.modules.psi.dao.PsiInventoryOutDao;
import com.springrain.erp.modules.psi.dao.PsiTransportOrderDao;
import com.springrain.erp.modules.psi.dao.lc.LcPsiTransportOrderDao;
import com.springrain.erp.modules.psi.entity.PsiInventory;
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
 * 出库管理Service
 * @author Michael
 * @version 2015-01-05
 */
@Component
@Transactional(readOnly = true)
public class PsiInventoryOutService extends BaseService {
	
	@Autowired
	private PsiInventoryOutDao 			   psiInventoryOutDao;
	@Autowired
	private GenerateSequenceDao 		   genDao;
	@Autowired
	private PsiInventoryDao			       psiInventoryDao;
	@Autowired
	private PsiInventoryService		       psiInventoryService;
	@Autowired
	private PsiInventoryInService          psiInventoryInService;
	@Autowired
	private PsiTransportOrderDao           psiTransportOrderDao;
	@Autowired
	private LcPsiTransportOrderDao         lcPsiTransportOrderDao;
	@Autowired
	private PsiTransportOrderService       psiTransportOrderService;
	@Autowired
	private LcPsiTransportOrderService     lcPsiTransportOrderService;
	@Autowired  
	private FbaInboundService              boundService;
	@Autowired
	private PsiProductService              psiProductService;
	@Autowired
	private StockService                   stockService;
	
	private  final Logger LOGGER = LoggerFactory.getLogger(getClass());
	public PsiInventoryOut get(Integer id) {
		return psiInventoryOutDao.get(id);
	}
	
	public PsiInventoryOut get(String outNo) {
		DetachedCriteria dc = this.psiInventoryOutDao.createDetachedCriteria();
		dc.add(Restrictions.eq("billNo", outNo));
		List<PsiInventoryOut> rs = this.psiInventoryOutDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	
	public Page<PsiInventoryOut> find(Page<PsiInventoryOut> page, PsiInventoryOut psiInventoryOut) {
		DetachedCriteria dc = psiInventoryOutDao.createDetachedCriteria();
		if(psiInventoryOut.getAddDate()!=null){
			dc.add(Restrictions.ge("addDate",psiInventoryOut.getAddDate()));
		}
		
		if(psiInventoryOut.getAddDateS()!=null){
			dc.add(Restrictions.le("addDate",DateUtils.addDays(psiInventoryOut.getAddDateS(),1)));
		}
		
		if(StringUtils.isNotEmpty(psiInventoryOut.getBillNo())){
			dc.add(Restrictions.or(Restrictions.like("billNo", "%"+psiInventoryOut.getBillNo()+"%"),Restrictions.like("tranLocalNo", "%"+psiInventoryOut.getBillNo()+"%"),Restrictions.like("tranFbaNo", "%"+psiInventoryOut.getBillNo()+"%")));
		}
		
		if(StringUtils.isNotEmpty(psiInventoryOut.getTranLocalNo())){
			dc.createAlias("this.items", "item");  
			dc.add(Restrictions.like("item.productName", "%"+psiInventoryOut.getTranLocalNo()+"%"));
		}
		
		if(StringUtils.isNotEmpty(psiInventoryOut.getOperationType())){
			if("other".equals(psiInventoryOut.getOperationType())){
				String [] arr= new String[]{"Inventory Taking Delivery","Replacement/Testing Delivery","Wholesale Delivery","FBA Delivery","Transport Delivery","Lot Delivery"};
				dc.add(Restrictions.not(Restrictions.in("operationType",arr)));
			}else{
				dc.add(Restrictions.eq("operationType", psiInventoryOut.getOperationType()));
			}
			
		}
		
		if(psiInventoryOut.getWarehouseId()!=null){
			dc.add(Restrictions.eq("warehouseId", psiInventoryOut.getWarehouseId()));
		}
		if(psiInventoryOut.getAddUser()!=null&&psiInventoryOut.getAddUser().getId()!=null&&!psiInventoryOut.getAddUser().getId().equals("")){
			dc.add(Restrictions.eq("addUser.id", psiInventoryOut.getAddUser().getId()));
		}
		
		page.setOrderBy(" id desc");
		return psiInventoryOutDao.find2(page, dc);
	}
	
	public List<PsiInventoryOut> find(PsiInventoryOut psiInventoryOut) {
		DetachedCriteria dc = psiInventoryOutDao.createDetachedCriteria();
		if(psiInventoryOut.getAddDate()!=null){
			dc.add(Restrictions.ge("addDate",psiInventoryOut.getAddDate()));
		}
		
		if(psiInventoryOut.getAddDateS()!=null){
			dc.add(Restrictions.le("addDate",DateUtils.addDays(psiInventoryOut.getAddDateS(),1)));
		}
		
		if(StringUtils.isNotEmpty(psiInventoryOut.getBillNo())){
			dc.add(Restrictions.or(Restrictions.like("billNo", "%"+psiInventoryOut.getBillNo()+"%"),Restrictions.like("tranLocalNo", "%"+psiInventoryOut.getBillNo()+"%"),Restrictions.like("tranFbaNo", "%"+psiInventoryOut.getBillNo()+"%")));
		}
		
		if(StringUtils.isNotEmpty(psiInventoryOut.getOperationType())){
			if("other".equals(psiInventoryOut.getOperationType())){
				String [] arr= new String[]{"Inventory Taking Delivery","Replacement/Testing Delivery","Wholesale Delivery","FBA Delivery","Transport Delivery","Lot Delivery"};
				dc.add(Restrictions.not(Restrictions.in("operationType",arr)));
			}else{
				dc.add(Restrictions.eq("operationType", psiInventoryOut.getOperationType()));
			}
		}
		
		if(psiInventoryOut.getWarehouseId()!=null){
			dc.add(Restrictions.eq("warehouseId", psiInventoryOut.getWarehouseId()));
		}
		
		dc.addOrder(Order.desc("id"));
		return psiInventoryOutDao.find(dc);
	}
	
	
	public Map<String,Integer> getLotDeliveryQuantity(MultipartFile excelFile) throws Exception{
		Map<String,Integer> dataMap =null;
		if(excelFile!=null&&excelFile.getSize()!=0){
			String suffix = excelFile.getOriginalFilename().substring(excelFile.getOriginalFilename().lastIndexOf(".")); 
			if(!".csv,.xls,.xlsx".contains(suffix)){
				throw new RuntimeException("Lot Delivery file type is not right,Operation has been canceled");
			}
			if(".csv".equals(suffix)){
				dataMap=psiInventoryInService.resolveCsvFile(excelFile.getInputStream());
			}else if(".xls,.xlsx".contains(suffix)){
				dataMap=psiInventoryInService.resolveExcelFile(excelFile.getInputStream());
			}else{
				throw new RuntimeException("Please choice right csv or excel file："+suffix+"");
			}
		}else{
			throw new RuntimeException("Data file is empty,Operation has been canceled");
		}
		
		Map<String,String> fnskuMap=Maps.newHashMap();
		Map<String,Integer> resDataMap=Maps.newHashMap();
		if(dataMap.size()>0){
			fnskuMap=this.psiProductService.getFnskuMap(dataMap.keySet());
		}else{
			throw new RuntimeException("Data file is empty,Operation has been canceled");
		}
		for(Map.Entry<String, Integer> entry:dataMap.entrySet()){
			String fnsku = entry.getKey();
			if(fnskuMap.get(fnsku)==null){
				resDataMap.put(fnsku, entry.getValue());
			}else{
				resDataMap.put(fnskuMap.get(fnsku), entry.getValue());
			}
		}
		return resDataMap;
	}
	
	
	@Transactional(readOnly = false)
	public String addSave(MultipartFile memoFile,MultipartFile excelFile,MultipartFile pdfFile,PsiInventoryOut psiInventoryOut) throws Exception {
		String res="";
		Map<String,PsiInventory> inventoryMap = Maps.newHashMap();
		String operatoinType=psiInventoryOut.getOperationType();
		Integer warehouseId =psiInventoryOut.getWarehouseId();
		String  warehouseName=psiInventoryOut.getWarehouseName();
		String filePath="";
		//String inNo=this.genDao.genSequence("_CKD",3);
		SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMddHHmmsss");
		String flowNo=sdf.format(new Date());
		String inNo=flowNo.substring(0,8)+"_CKD"+flowNo.substring(8);
		psiInventoryOut.setBillNo(inNo);
		if(memoFile!=null&&memoFile.getSize()!=0){
			filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/psiInventoryOut";
			File baseDir = new File(filePath+"/"+inNo); 
			if(!baseDir.isDirectory())
				baseDir.mkdirs();
			String suffix = memoFile.getOriginalFilename().substring(memoFile.getOriginalFilename().lastIndexOf("."));     
			String name=UUID.randomUUID().toString()+suffix;
			File dest = new File(baseDir,name);
			try {
				FileUtils.copyInputStreamToFile(memoFile.getInputStream(),dest);
				psiInventoryOut.setAttchmentPath("/psi/psiInventoryOut/"+inNo+"/"+name);
			} catch (IOException e) {
				logger.warn(name+",File save fail",e);
			}
		}
		if (pdfFile != null && pdfFile.getSize() != 0) {
			filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/psiInventoryOut";
			File baseDir = new File(filePath + "/" + inNo);
			if(!baseDir.isDirectory())
				baseDir.mkdirs();
			String suffix = pdfFile.getOriginalFilename().substring(pdfFile.getOriginalFilename().lastIndexOf("."));
			String name = UUID.randomUUID().toString() + suffix;
			File dest = new File(baseDir, name);
			try {
				FileUtils.copyInputStreamToFile(pdfFile.getInputStream(), dest);
				psiInventoryOut.setPdfFile("/psi/psiInventoryOut/" + inNo	+ "/" + name);
			} catch (IOException e) {
				logger.warn(name+",pdf File save fail",e);
			}
		}
		
		if("Lot Delivery".equals(psiInventoryOut.getOperationType())){
			//每天最多只能使用两次
			if(!this.canOutTypeQuantity("Lot Delivery", 2)){
				return "error：Today,Lot Delivery has been outBound twice!Please tomorrow contine...";
			}
			
			//把运单信息    fba信息置空
			psiInventoryOut.setTranFbaNo(null);
			psiInventoryOut.setTranLocalId(null);
			psiInventoryOut.setTranLocalNo(null);
			
			psiInventoryOut.setItems(null);
			Map<String,Integer> dataMap =null;
			//解析excel或csv文件
			if(excelFile!=null&&excelFile.getSize()!=0){
				filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/psiInventoryOut";
				File baseDir = new File(filePath+"/"+inNo); 
				if(!baseDir.isDirectory()){
					baseDir.mkdirs();
				}
				String suffix = excelFile.getOriginalFilename().substring(excelFile.getOriginalFilename().lastIndexOf(".")); 
				if(!".csv,.xls,.xlsx".contains(suffix)){
					throw new RuntimeException("Lot Delivery file type is not right，Operation has been canceled");
				}
				psiInventoryOut.setOriginName(excelFile.getOriginalFilename());
				
				//检查文件名是否重复出库
				if(this.isExistFile(psiInventoryOut.getOriginName())){
					throw new RuntimeException("The data file("+psiInventoryOut.getOriginName()+") has been out-stock,please to check out-stock list function!");
				}
				
				String name=UUID.randomUUID().toString()+suffix;
				File dest = new File(baseDir,name);
				FileUtils.copyInputStreamToFile(excelFile.getInputStream(),dest);
				psiInventoryOut.setDataFile("/psi/psiInventoryOut/"+inNo+"/"+name);
				//放入文件解析文件
				if(".csv".equals(suffix)){
					dataMap=psiInventoryInService.resolveCsvFile(excelFile.getInputStream());
				}else if(".xls,.xlsx".contains(suffix)){
					dataMap=psiInventoryInService.resolveExcelFile(excelFile.getInputStream());
				}else{
					throw new RuntimeException("Please choice right csv or excel file："+suffix+"");
				}
			}else{
				throw new RuntimeException("Data file is empty，Operation has been canceled");
			}
			
			Map<String,String> fnskuMap=Maps.newHashMap();
			Map<String,Integer> resDataMap=Maps.newHashMap();
			if(dataMap.size()>0){
				Set<String> fnskuSet=Sets.newHashSet();
				for (String fnsku:dataMap.keySet()) {
					fnskuSet.add(fnsku.replace("$",""));
				}
				//fnskuMap=this.psiProductService.getFnskuMap(dataMap.keySet());
				fnskuMap=this.psiProductService.getFnskuMap(fnskuSet);
			}else{
				throw new RuntimeException("Data file is empty，Operation has been canceled");
			}
			
			//对sku进行处理，找出sku对应的产品信息颜色。
			Map<String,String> skuNameMap =psiProductService.findAllProductNamesWithSku();
			Map<String,Integer> productQMap  =Maps.newHashMap();
			if(!psiInventoryOut.getWarehouseId().equals(21)&&!psiInventoryOut.getWarehouseId().equals(130)){//如果不为中国仓，查询上次excel之间形成的线下订单
				productQMap=this.getOffLineTotalAmountByName();
			}
			
			Map<String,String> skuFnskuMap = Maps.newHashMap();
			for(Map.Entry<String, Integer> entry:dataMap.entrySet()){
				String fnsku = entry.getKey();
				//String sku = fnskuMap.get(fnsku);
				String sku=fnskuMap.get(fnsku.replace("$",""));
				Integer quantity = entry.getValue();
				if(sku==null){
					//如果没有条码，当做sku
					sku = fnsku;
					resDataMap.put(sku, quantity);
				}else{
					if(fnsku.contains("$")){
						resDataMap.put(sku+"$", quantity);
						skuFnskuMap.put(sku+"$", fnsku);
					}else{
						resDataMap.put(sku, quantity);
						skuFnskuMap.put(sku, fnsku);	
					}
					
				}
			}
			
			//加入值排序
			MapValueComparator bvc =  new MapValueComparator(resDataMap,false);  
			TreeMap<String,Integer> sortKeyMap = new TreeMap<String,Integer>(bvc);  
			sortKeyMap.putAll(resDataMap); 
	        Map<String,Integer> sortMap=Maps.newLinkedHashMap(); 
	        for(String sortKey:sortKeyMap.keySet()){
	        	sortMap.put(sortKey, resDataMap.get(sortKey));
	        }
				
			//获取批量入库产品的质量类型
			//String type =psiInventoryOut.getDataType();
			List<PsiInventoryOutItem> inItem = Lists.newArrayList(); 
			for(Map.Entry<String,Integer> entry:sortMap.entrySet()){
				String sku = entry.getKey();
				Integer dataQuantity =entry.getValue();
				/**
				 *如果用inventory_reversion_log表统计数据及时库存数据，开启下面注释
				 * 
				 */
				//this.psiInventoryLogService.getSumByInventory(warehouseId,inventory);
			//	PsiInventory inventory = psiInventoryService.findBySku(sku, warehouseId);
				PsiInventory inventory = psiInventoryService.findBySku(sku.replace("$",""), warehouseId);
				if(inventory==null){
					throw new RuntimeException("The "+sku+" has not exist  in inventory, cannot out-stocks，Operation has been canceled!");
				}
				
				Integer offlineQuantity = inventory.getOfflineQuantity();
				Integer newQuantity     = inventory.getNewQuantity();
				Integer oldOuantity     = inventory.getOldQuantity();
				
				
				String productName=inventory.getProductName();
				Integer productId=inventory.getProductId();
				String country = inventory.getCountryCode();
				String color = inventory.getColorCode();
				
				if(sku.endsWith("$")){
					if(inventory.getOldQuantity()<dataQuantity){
						throw new RuntimeException("Lot Delivery low stocks,sku:"+sku.replace("$","")+"[old's quantity："+oldOuantity+"], Outbound quantity："+dataQuantity+",Operation has been canceled");
					}
					makeUpItem("old", inventory, psiInventoryOut, productId, productName, country, color, sku.replace("$",""), dataQuantity, operatoinType, warehouseId, warehouseName, inNo, inItem);
				}else{
					//如果new、offline数量总数少于要出库的数量，报异常
					if((inventory.getNewQuantity()+inventory.getOfflineQuantity())<dataQuantity){
						throw new RuntimeException("Lot Delivery low stocks,sku:"+sku+"[new's quantity："+newQuantity+",offline's quantity："+offlineQuantity+"], Outbound quantity："+dataQuantity+",Operation has been canceled");
					}
					String proName = skuNameMap.get(sku);
					Integer curQ = productQMap.get(proName);
					//当前有线下的并且线下的数量没减完，就优先减线线下的
					if(curQ!=null&&curQ>0){
						//如果数量大于50个，线下的不足就报异常，通知进行转码
						if(dataQuantity>=10&&offlineQuantity<dataQuantity){
							throw new RuntimeException("Lot Delivery low stocks,offline's quantity："+offlineQuantity+"], Outbound quantity："+dataQuantity+",Operation has been canceled!! Please change inventory new's quantity to offline in erp!");
						}
						
						Integer tempQ = offlineQuantity-dataQuantity;
						if(tempQ<0){
							//一部分线下   一部分线上    线下的都出完   线下数量为0
							if(offlineQuantity>0){
								makeUpItem("offline", inventory, psiInventoryOut, productId, productName, country, color, sku, offlineQuantity, operatoinType, warehouseId, warehouseName, inNo, inItem);
							}
							makeUpItem("new", inventory, psiInventoryOut, productId, productName, country, color, sku, dataQuantity-offlineQuantity, operatoinType, warehouseId, warehouseName, inNo, inItem);
						}else{
							//全部减去线下的
							makeUpItem("offline", inventory, psiInventoryOut, productId, productName, country, color, sku, dataQuantity, operatoinType, warehouseId, warehouseName, inNo, inItem);
						}
						productQMap.put(proName,curQ-dataQuantity);
					}else{
						//全部减除线上的
						makeUpItem("new", inventory, psiInventoryOut, productId, productName, country, color, sku, dataQuantity, operatoinType, warehouseId, warehouseName, inNo, inItem);
					}
				}

				//保存库存记录
				inventory.setUpdateDate(new Date());  //最近更新的查询时最先显示
				psiInventoryDao.save(inventory);
			}
			psiInventoryOut.setItems(inItem);
		}else{
			//清除Lot Delivery操作的信息
			psiInventoryOut.setDataType(null);
			PsiTransportOrder tranOrder=null;
			LcPsiTransportOrder lcTranOrder=null;
			int houseId = warehouseId.intValue();
			//如果是Transport Delivery   把fba信息置空
			if("FBA Delivery".equals(psiInventoryOut.getOperationType())){
				//是中国发出的，或者德国发出的目的地为美洲或日本，或者美国发出的目的地为欧洲或日本
				String toCountry = psiInventoryOut.getWhereabouts();
				if(houseId==21||houseId==130||(houseId==19&&"com,ca,mx,jp".contains(toCountry))||(houseId==120&&"fr,it,es,uk,de,jp".contains(toCountry))){
					Map<String,Integer> tempMap = Maps.newHashMap();  //校验用
					for (Iterator<PsiInventoryOutItem> iterator = psiInventoryOut.getItems().iterator(); iterator.hasNext();) {
						PsiInventoryOutItem item = iterator.next();
						String sku = item.getSku();
						PsiInventory   inventory = psiInventoryService.findBySku(sku, warehouseId);
						if(inventory!=null){
							tempMap.put(inventory.getSku(),item.getQuantity());
						}
					}
					
					tranOrder=psiTransportOrderService.getByFbaShipmentNo(psiInventoryOut.getTranFbaNo());
					String rs="michael";
					if(tranOrder==null){
						lcTranOrder=lcPsiTransportOrderService.getByFbaShipmentNo(psiInventoryOut.getTranFbaNo());
						if(lcTranOrder==null){
							return "没有运单匹配fba贴："+psiInventoryOut.getTranFbaNo()+",请先到运单那里匹配fba贴";
						}else{
							rs = fbaTranValidate(lcTranOrder, tempMap);
						}
					}else{
						rs = fbaTranValidate(tranOrder, tempMap);
					}
					//fba和运单数量校验
					if(StringUtils.isNotEmpty(rs)){
						return rs;
					}
				}
				
				String	result=this.boundService.shippedFbaInBound(psiInventoryOut);
				if(!result.contains("outBound success!")){
					return result;
				}
				
				try{
					String shipMentIds = psiInventoryOut.getTranFbaNo();
					String[] shipmentIds =  shipMentIds.split(",");
					this.updateShippedDate(Arrays.asList(shipmentIds));
				}catch(Exception ex){
					logger.error("shipmentId更新失败");
				}
				
				if(houseId==21||houseId==130||(houseId==19&&"com,ca,mx,jp".contains(toCountry))||(houseId==120&&"fr,it,es,uk,de,jp".contains(toCountry))){
					//更新订单状态为出库    fba关联的这个单状态改为已出库
					if(tranOrder!=null){
						this.psiTransportOrderDao.updateTranSta(tranOrder.getId(),"1");
						psiInventoryOut.setTranLocalId(tranOrder.getId()+"");
						psiInventoryOut.setTranLocalNo(tranOrder.getTransportNo());
					}else if(lcTranOrder!=null){
						this.lcPsiTransportOrderDao.updateTranSta(lcTranOrder.getId(),"1");
						psiInventoryOut.setTranLocalId(lcTranOrder.getId()+"");
						psiInventoryOut.setTranLocalNo(lcTranOrder.getTransportNo());
					}
				}  
			}
			//查询入库仓库
			Integer inStockId=null;
			String inStockName=null;
			
			if("Transport Delivery".equals(psiInventoryOut.getOperationType())){
				String transportNo = psiInventoryOut.getTranLocalNo();
				if(transportNo.contains("_LC_")){
					lcTranOrder=lcPsiTransportOrderService.get(transportNo);
					if(lcTranOrder.getToStore()!=null){
						inStockId=lcTranOrder.getToStore().getId();
						inStockName = lcTranOrder.getToStore().getStockName();
					}
				}else{
					tranOrder=psiTransportOrderService.get(transportNo);
					if(tranOrder.getToStore()!=null){
						inStockId=tranOrder.getToStore().getId();
						inStockName = tranOrder.getToStore().getStockName();
					}
				}
			}
			
			Map<String,Integer> productMap = Maps.newHashMap();  //因为有可能不用原来的sku(必须用运单sku)
			
			for (Iterator<PsiInventoryOutItem> iterator = psiInventoryOut.getItems().iterator(); iterator.hasNext();) {
				PsiInventoryOutItem item = iterator.next();
				
				String sku = item.getSku();
				PsiInventory inventory = null;
				//获取选中的库存id
				if(inventoryMap.get(sku)==null){
					inventory = psiInventoryService.findBySku(sku, warehouseId);
					if(inventory==null){
						throw new RuntimeException("The sku:"+sku+" has no data in inventory,cannot out-stocks，Operation has been canceled!");
					}
					if(item.getQuantity()!=null&&item.getQuantity()!=0){
						inventoryMap.put(item.getSku(), inventory);
					}
				}else{
					inventory = inventoryMap.get(sku);
				}
				
				item.setColorCode(inventory.getColorCode());
				item.setCountryCode(inventory.getCountryCode());
				item.setProductId(inventory.getProductId());
				item.setProductName(inventory.getProductName());
				item.setAvgPrice(inventory.getAvgPrice());
				if("FBA Delivery".equals(psiInventoryOut.getOperationType())||"Transport Delivery".equals(psiInventoryOut.getOperationType())){	
					String offlindeSta="0";
					if(item.getQualityType().equals("offline")){
						offlindeSta="1";
					}
					productMap.put(item.getSku()+","+offlindeSta,item.getQuantity());
				}
				
				//如果数量为0的话，不进行库存操作
				if(item.getQuantity()==null||item.getQuantity()==0){
					iterator.remove();
					continue;
				}
				
				item.setInventoryOut(psiInventoryOut);
				String remark=item.getRemark();
				
				
				
				/**
				 *如果用inventory_reversion_log表统计数据及时库存数据，开启下面注释
				 * 
				 */
				//this.psiInventoryLogService.getSumByInventory(warehouseId,inventory);
				String type=item.getQualityType();
				if(type.equals("new")){
					inventory.setNewQuantity(inventory.getNewQuantity()-item.getQuantity());
					item.setTimelyQuantity(inventory.getNewQuantity());
					if(inventory.getNewQuantity()<0){
						throw new RuntimeException(" After outBound inventory quantity<0 (new、old、broken、renew、spares、offline),Operation has been canceled");
					}
					this.psiInventoryService.savelog(operatoinType, type, -item.getQuantity(), remark, inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), warehouseId,inNo,sku,inStockId,inStockName,item.getTimelyQuantity());
				}else if(type.equals("old")){
					inventory.setOldQuantity(inventory.getOldQuantity()-item.getQuantity());
					item.setTimelyQuantity(inventory.getOldQuantity());
					if(inventory.getOldQuantity()<0){
						throw new RuntimeException("After outBound inventory quantity<0 (new、old、broken、renew、spares、offline),Operation has been canceled");
					}
					this.psiInventoryService.savelog(operatoinType, type, -item.getQuantity(), remark, inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), warehouseId,inNo,sku,inStockId,inStockName,item.getTimelyQuantity());
				}else if(type.equals("broken")){
					inventory.setBrokenQuantity(inventory.getBrokenQuantity()-item.getQuantity());
					item.setTimelyQuantity(inventory.getBrokenQuantity());
					if(inventory.getBrokenQuantity()<0){
						throw new RuntimeException("After outBound inventory quantity<0 (new、old、broken、renew、spares、offline),Operation has been canceled");
					}
					this.psiInventoryService.savelog(operatoinType, type, -item.getQuantity(), remark, inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), warehouseId,inNo,sku,inStockId,inStockName,item.getTimelyQuantity());
				}else if(type.equals("renew")){
					inventory.setRenewQuantity(inventory.getRenewQuantity()-item.getQuantity());
					item.setTimelyQuantity(inventory.getRenewQuantity());
					if(inventory.getRenewQuantity()<0){
						throw new RuntimeException("After outBound inventory quantity<0 (new、old、broken、renew、spares、offline),Operation has been canceled");
					}
					this.psiInventoryService.savelog(operatoinType,type, -item.getQuantity(), remark, inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), warehouseId,inNo,sku,inStockId,inStockName,item.getTimelyQuantity());
				}else if(type.equals("spares")){
					inventory.setSparesQuantity(inventory.getSparesQuantity()-item.getQuantity());
					item.setTimelyQuantity(inventory.getSparesQuantity());
					if(inventory.getSparesQuantity()<0){
						throw new RuntimeException("After outBound inventory quantity<0 (new、old、broken、renew、spares、offline),Operation has been canceled");
					}
					this.psiInventoryService.savelog(operatoinType, type, -item.getQuantity(), remark, inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), warehouseId,inNo,sku,inStockId,inStockName,item.getTimelyQuantity());
				}else if(type.equals("offline")){
					inventory.setOfflineQuantity(inventory.getOfflineQuantity()-item.getQuantity());
					item.setTimelyQuantity(inventory.getOfflineQuantity());
					if(inventory.getOfflineQuantity()<0){
						throw new RuntimeException("After outBound inventory quantity<0 (new、old、broken、renew、spares、offline),Operation has been canceled");
					}
					this.psiInventoryService.savelog(operatoinType, type, -item.getQuantity(), remark, inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), warehouseId,inNo,sku,inStockId,inStockName,item.getTimelyQuantity());
				}
				
			}
			
			//遍历map保存库存数据
			for(Map.Entry<String, PsiInventory> entry : inventoryMap.entrySet()){
				PsiInventory inventory = entry.getValue();
				inventory.setUpdateDate(new Date());  //最近更新的查询时最先显示
				psiInventoryDao.save(inventory);
			}
			
			
			//如果是Transport Delivery   把fba信息置空
			if(!"FBA Delivery".equals(psiInventoryOut.getOperationType())){
				psiInventoryOut.setTranFbaNo(null);
				if(!"Transport Delivery".equals(psiInventoryOut.getOperationType())&&!"Offline Delivery".equals(psiInventoryOut.getOperationType())){
					psiInventoryOut.setTranLocalId(null);
					psiInventoryOut.setTranLocalNo(null);
				}else if("Transport Delivery".equals(psiInventoryOut.getOperationType())){
					String transportNo = psiInventoryOut.getTranLocalNo();
					if(transportNo.contains("_LC_")){
						changeTransport(lcTranOrder,productMap);
					}else{
						changeTransport(tranOrder,productMap);
					}
					psiInventoryOut.setTranLocalNo(transportNo);
				}else if("Offline Delivery".equals(psiInventoryOut.getOperationType())){
					psiInventoryOut.setTranLocalId(psiInventoryOut.getTranLocalId());
					psiInventoryOut.setTranLocalNo(psiInventoryOut.getTranLocalNo());
				}
			}
			
			
		}
		
		//理诚，fba或者运单出库
		if((psiInventoryOut.getWarehouseId().intValue()==130)&&("FBA Delivery".equals(psiInventoryOut.getOperationType())||"Transport Delivery".equals(psiInventoryOut.getOperationType()))){
			String monthFlowNo=this.genDao.genSequenceByMonth2("E",4);  //出库流水号
			psiInventoryOut.setFlowNo(monthFlowNo);
		}
		psiInventoryOut.setAddDate(new Date());
		psiInventoryOut.setAddUser(UserUtils.getUser());
		psiInventoryOutDao.save(psiInventoryOut);
		return res;
	}
	
	
	public void changeTransport(PsiTransportOrder tranOrder,Map<String,Integer> productMap){
		tranOrder.setTransportSta("1");//在途状态
		for(PsiTransportOrderItem item:tranOrder.getItems()){
			item.setShippedQuantity(productMap.get((item.getSku()+","+item.getOfflineSta())));   //发货数量即前台填的数量，原来quantity没变
		}
		//添加操作出库人  和离港时间
		tranOrder.setOperDeliveryDate(new Date());
		tranOrder.setOperDeliveryUser(UserUtils.getUser());
		try{
			Map<String,Float> priceMap=psiInventoryService.getCnPrice();
			for (PsiTransportOrderItem item : tranOrder.getItems()) {
				item.setCnPrice(priceMap.get(item.getNameWithColor()));
			}
		}catch(Exception e){
			LOGGER.error("更新运单价异常", e);
		}
		psiTransportOrderDao.save(tranOrder);
	}
	
	
	public void changeTransport(LcPsiTransportOrder tranOrder,Map<String,Integer> productMap){
		tranOrder.setTransportSta("1");//在途状态
		for(LcPsiTransportOrderItem item:tranOrder.getItems()){
			item.setShippedQuantity(productMap.get((item.getSku()+","+item.getOfflineSta())));   //发货数量即前台填的数量，原来quantity没变
		}
		//添加操作出库人  和离港时间
		tranOrder.setOperDeliveryDate(new Date());
		tranOrder.setOperDeliveryUser(UserUtils.getUser());
		try{
			Map<String,Float> priceMap=psiInventoryService.getCnPrice();
			for (LcPsiTransportOrderItem item : tranOrder.getItems()) {
				item.setCnPrice(priceMap.get(item.getNameWithColor()));
			}
		}catch(Exception e){
			LOGGER.error("更新运单价异常", e);
		}
		lcPsiTransportOrderDao.save(tranOrder);
	}
	
	
	public String fbaTranValidate(PsiTransportOrder tranOrder,Map<String,Integer> productMap){
		Set<String> productSet = Sets.newHashSet();
		for(PsiTransportOrderItem item:tranOrder.getItems()){
			String key =  item.getSku()  ;//item.getProductName()+","+item.getCountryCode()+","+item.getColorCode();
			
				if(productMap.get(key)==null){
					return "error:产品："+key+",fba贴里没有,运单里有....操作已取消！！！";
				}
				if(productMap.get(key).intValue()!=0){
					if(!item.getQuantity().equals(productMap.get(key))){
						return "error:产品："+key+"运单数量："+item.getQuantity()+",fba贴数量："+productMap.get(key)+"......操作已取消！！！";
					}
					productSet.add(key);
				}
		}
		
		for(Map.Entry<String, Integer> entry:productMap.entrySet()){
			String key= entry.getKey();
			if(!productSet.contains(key)&&(entry.getValue().intValue()!=0)){
				return "error:产品："+key+",fba贴有运单里没有......操作已取消！！！";
			}
		}
		
		return "";
	}
	
	public String fbaTranValidate(LcPsiTransportOrder tranOrder,Map<String,Integer> productMap){
		Set<String> productSet = Sets.newHashSet();
		for(LcPsiTransportOrderItem item:tranOrder.getItems()){
			String key =  item.getSku()  ;//item.getProductName()+","+item.getCountryCode()+","+item.getColorCode();
			
				if(productMap.get(key)==null){
					return "error:产品："+key+",fba贴里没有,运单里有....操作已取消！！！";
				}
				if(productMap.get(key).intValue()!=0){
					if(!item.getQuantity().equals(productMap.get(key))){
						return "error:产品："+key+"运单数量："+item.getQuantity()+",fba贴数量："+productMap.get(key)+"......操作已取消！！！";
					}
					productSet.add(key);
				}
		}
		
		for(Map.Entry<String, Integer> entry:productMap.entrySet()){
			String key= entry.getKey();
			if(!productSet.contains(key)&&(entry.getValue().intValue()!=0)){
				return "error:产品："+key+",fba贴有运单里没有......操作已取消！！！";
			}
		}
		
		return "";
	}
	
	public void mitiChangeTransport(String[] tranIds,Map<String,String> productMap,Integer storeId,PsiInventoryOut  psiInventoryOut){
		Integer iFlag=0;
		List<PsiTransportOrder> orders = Lists.newArrayList();
		Set<String> productSet = Sets.newHashSet();
		String tranSportNos = "";
		for(String tranSportId:tranIds){
			PsiTransportOrder tOrder = this.psiTransportOrderDao.get(Integer.parseInt(tranSportId));
			tranSportNos+=tOrder.getTransportNo();
			orders.add(tOrder);
			for(PsiTransportOrderItem item :tOrder.getItems()){
				String key = item.getProductName()+","+item.getCountryCode()+","+item.getColorCode();
				if(productMap.containsKey(key)&&Integer.parseInt(productMap.get(key).split(",")[1])==item.getQuantity().intValue()){
					iFlag=iFlag+1;
					break;
				};
			}
		}
		
		
		if(iFlag!=tranIds.length){
			throw new RuntimeException("fba贴关联的多个运单，其中有运单的产品项没有一条与fba贴关联");
		}
		
		for(PsiTransportOrder order:orders){
			for(PsiTransportOrderItem item:order.getItems()){
				String key = item.getProductName()+","+item.getCountryCode()+","+item.getColorCode();
				productSet.add(key);
			}
		}
		
		//比较多了哪些sku
		List<PsiTransportOrderItem> items = Lists.newArrayList();
		
		for(Map.Entry<String, String> entry:productMap.entrySet()){
			String productStr = entry.getKey();
			if(!productSet.contains(productStr)){
				//如果前台有新的sku加入，说明出库时粘贴sku错误
				String [] productArr = productStr.split(",");
				PsiSku psiSku = psiProductService.getSku(productArr[0], productArr[1], productArr[2]);
				PsiTransportOrderItem item = new PsiTransportOrderItem();
				PsiProduct product = new PsiProduct();
				product.setId(psiSku.getProductId());
				item.setProduct(product);
				item.setProductName(psiSku.getProductName());
				item.setCountryCode(psiSku.getCountry());
				item.setColorCode(psiSku.getColor());
				item.setSku(psiSku.getSku());
				item.setQuantity(0);  		              //运单数量
				item.setShippedQuantity(Integer.parseInt(entry.getValue().split(",")[1])); //发货数量
				items.add(item);
			}
		}
		
		//遍历运单把状态更新
		for(int i=0 ;i<orders.size();i++){
			PsiTransportOrder addTransportOrder = orders.get(i);
			if(items.size()>0&&i==0){
				for(PsiTransportOrderItem fbaItem:items){
					fbaItem.setTransportOrder(addTransportOrder);
					addTransportOrder.getItems().add(fbaItem);
				}
			}
			addTransportOrder.setTransportSta("1");
			//添加操作出库人  和离港时间
			addTransportOrder.setOperDeliveryDate(new Date());
			addTransportOrder.setOperDeliveryUser(UserUtils.getUser());
			this.psiTransportOrderDao.save(addTransportOrder);
		}
		
		psiInventoryOut.setTranLocalNo(tranSportNos);
	}
	
	
	@Transactional(readOnly = false)
	public void save(PsiInventoryOut psiInventoryOut) {
		psiInventoryOutDao.save(psiInventoryOut);
	}
	@Transactional(readOnly = false)
	public void delete(String id) {
		psiInventoryOutDao.deleteById(id);
	}
	
	
	public List<Integer> getHasInventoryOut(){
		String sql ="SELECT a.`tran_fba_id` FROM psi_inventory_out AS a  WHERE a.`operation_type`='FBA Delivery'";
		List<Integer> objects=this.psiInventoryOutDao.findBySql(sql);
		return objects;
		}
	
	public boolean isExistFile(String fileName){
		boolean flag=false;
		String sql="SELECT a.`origin_name` FROM psi_inventory_out AS a  WHERE a.`operation_type`='Lot Delivery' AND a.`origin_name`=:p1 LIMIT 1";
		List<String> list =this.psiInventoryOutDao.findBySql(sql,new Parameter(fileName));
		if(list.size()>0){
			flag=true;
		}
		return flag;
	}
	
	@Transactional(readOnly = false)
	public String getOrignFileName(String billNo){
		String sql="SELECT a.`origin_name`,a.`data_file` FROM psi_inventory_out AS a  WHERE a.`operation_type`='Lot Delivery' AND a.bill_no=:p1 LIMIT 1";
		List<Object[]> list =this.psiInventoryOutDao.findBySql(sql,new Parameter(billNo));
		if(list.size()==1){
			return (String)list.get(0)[0]+",,,"+(String)list.get(0)[1];
		}
		return null;
	}
	
	
	public boolean offLineOrderOutInventory(Map<String,Integer> skuMap,Integer warehouseId,String warehouseName,String orderNo){
		if(skuMap.size()==0||warehouseId==null){
			return false;
		}
		  
		for(Map.Entry<String,Integer> entry:skuMap.entrySet()){
			String sku = entry.getKey();
			Integer quantity=entry.getValue();
			PsiInventory inventory = psiInventoryService.findBySku(sku, warehouseId);
			if(inventory==null){
				throw new RuntimeException("The sku:"+sku+" has no data in inventory，Operation has been canceled!");
			}
			String productName=inventory.getProductName();
			Integer productId=inventory.getProductId();
			String country = inventory.getCountryCode();
			String color = inventory.getColorCode();
			inventory.setNewQuantity(inventory.getNewQuantity()-quantity);
			if(inventory.getNewQuantity()<0){
				throw new RuntimeException("Lot Delivery low stocks,sku:"+sku+",Inventory quantity："+inventory.getNewQuantity()+quantity+", Outbound quantity："+quantity+"Operation has been canceled");
			}
			//添加操作记录
			this.psiInventoryService.savelog("off-line Delivery", "new", -quantity, "", color,country,productId,productName, warehouseId,orderNo,sku,warehouseId,warehouseName,inventory.getNewQuantity());
			//保存库存记录
			inventory.setUpdateDate(new Date());  //最近更新的查询时最先显示
			psiInventoryDao.save(inventory);
		}
		
		return true;
	}
	
	//查询该出库类型今天可不可以继续出库   出库次数<限制次数允许出库
	public boolean canOutTypeQuantity(String outType,Integer quantity){
		String sql ="SELECT COUNT(*) FROM psi_inventory_out AS a WHERE a.`operation_type`=:p1 AND DATE_FORMAT(a.`add_date`,'%Y-%m-%d')=CURDATE()";
		List<Object> list = this.psiInventoryDao.findBySql(sql,new Parameter(outType));
		if(list!=null&&Integer.parseInt(list.get(0).toString())>=quantity){
			return false;
		}
		return true;
	}
		
	public Timestamp getMaxDate(Date date){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql="SELECT MAX(add_date) FROM psi_inventory_out WHERE operation_type='Lot Delivery' AND DATE_FORMAT(add_date, '%Y-%m-%d %H:%i:%s')<:p1 ";
		List<Object> rs=psiInventoryDao.findBySql(sql,new Parameter(sdf.format(date)));
		if(rs!=null&&rs.size()>0){
			return (Timestamp)rs.get(0);
		}
		return null;
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
			}else if("5".equals(flag)){
				paraSql=" a.`id_card` ";
			}else if("6".equals(flag)){
				paraSql=" a.`box_no` ";
			}
			String sql ="UPDATE psi_inventory_out AS a Set "+paraSql+"=:p2 WHERE a.`id`=:p1";
			this.psiInventoryOutDao.updateBySql(sql, new Parameter(id,content));
			return "true";
		}catch (Exception ex){
			return "false";
		}
	}
	
	/**
	 * 组合outboundItem
	 * 
	 */
	public void makeUpItem(String type,PsiInventory inventory,PsiInventoryOut psiInventoryOut,Integer productId,String productName,String country,String color,String sku,Integer quantity,String operatoinType,Integer warehouseId,String warehouseName,String inNo,List<PsiInventoryOutItem> outItem){
		Integer timelyQuantity =0;
		PsiInventoryOutItem  item = new PsiInventoryOutItem();
		item.setProductId(productId);
		item.setProductName(productName);
		item.setCountryCode(country);
		item.setColorCode(color);
		item.setSku(sku);
		item.setInventoryOut(psiInventoryOut);
		item.setQualityType(type);
		item.setQuantity(quantity);
		if("offline".equals(type)){
			inventory.setOfflineQuantity(inventory.getOfflineQuantity()-item.getQuantity());
			timelyQuantity=inventory.getOfflineQuantity();
			if(inventory.getOfflineQuantity()<0){
				throw new RuntimeException("Lot Delivery low stocks,sku:"+sku+",Quality type："+type+",Inventory quantity："+(inventory.getOfflineQuantity()+item.getQuantity())+", Outbound quantity："+item.getQuantity()+",Operation has been canceled");
			}
		}else if("old".equals(type)){
			inventory.setOldQuantity(inventory.getOldQuantity()-item.getQuantity());
			timelyQuantity=inventory.getOldQuantity();
			if(inventory.getOldQuantity()<0){
				throw new RuntimeException("Lot Delivery low stocks,sku:"+sku+",Quality type："+type+",Inventory quantity："+(inventory.getOldQuantity()+item.getQuantity())+", Outbound quantity："+item.getQuantity()+",Operation has been canceled");
			}
		}else{
			inventory.setNewQuantity(inventory.getNewQuantity()-item.getQuantity());
			timelyQuantity=inventory.getNewQuantity();
			if(inventory.getNewQuantity()<0){
				throw new RuntimeException("Lot Delivery low stocks,sku:"+sku+",Quality type："+type+",Inventory quantity："+(inventory.getNewQuantity()+item.getQuantity())+", Outbound quantity："+item.getQuantity()+",Operation has been canceled");
			}
		}
		
		
		//添加操作记录
		this.psiInventoryService.savelog(operatoinType, type, -item.getQuantity(), "", inventory.getColorCode(), inventory.getCountryCode(), inventory.getProductId(), inventory.getProductName(), warehouseId,inNo,sku,warehouseId,warehouseName,timelyQuantity);
		item.setTimelyQuantity(timelyQuantity);
		//添加及时的均价  如果
		item.setAvgPrice(inventory.getAvgPrice());
		outItem.add(item);
	}
	
	/**
	 *打印了包箱单的，有可能漏
	 *获得上次excel上传后到目前的订单 
	 */
	public Map<String,Integer> getOffLineTotalAmountByName(){
		Date maxDate = this.getMaxDate(new Date());
		Map<String,Integer> map=new HashMap<String,Integer>();
		if(maxDate!=null){
			Date start=new Date(maxDate.getTime());
			String sql="SELECT t.title,SUM(t.`quantity_shipped`) quantity_total FROM amazoninfo_ebay_order o JOIN amazoninfo_ebay_orderitem t ON o.`id`=t.`order_id`  "+
		               " join amazoninfo_ebay_package p on p.id=o.package_id "+
		               " WHERE (LENGTH(o.`order_id`) - LENGTH(REPLACE(o.`order_id`,'-','')))=1 AND o.order_type='3' and DATE_FORMAT(p.`print_time`,'%Y-%m-%d %H:%i:%s')>=:p1  and DATE_FORMAT(p.`print_time`,'%Y-%m-%d %H:%i:%s')<:p2 and o.country=:p3  "+
		               " group by t.title ";
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			List<Object[]> list=this.psiInventoryOutDao.findBySql(sql,new Parameter(sdf.format(start),sdf.format(new Date()),"de"));
			for (Object[] obj : list) {
				String productName=obj[0].toString();
				if(map.get(productName)==null){
					map.put(productName,Integer.parseInt(obj[1].toString()));
				}else{
					Integer quantity=map.get(productName);
					map.put(productName,Integer.parseInt(obj[1].toString())+quantity);
				}
			}
		}
		return map;
	}
	
	@Transactional(readOnly = false)      
	public void updateShippedDate(List<String> shipmentIds){
		String sql=" UPDATE psi_fba_inbound AS a SET a.`shipped_date`=NOW() WHERE a.`shipment_id` IN :p1 ";
		this.psiInventoryDao.updateBySql(sql, new Parameter(shipmentIds));
	}
	
	
	/**
	 *根据产品名自行出库 
	 */
	public String outBoundByProduct(Integer inventoryId,Map<String,Integer> productNameColorMap){
		StringBuffer sb = new StringBuffer();
		Set<String> productNameColors=productNameColorMap.keySet();
		Set<String> productNames = Sets.newHashSet();
		for(String nameColor:productNameColors){
			String arr[] = nameColor.split("_");
			productNames.add(arr[0]);
		}
		Map<String,Integer> colorMap= Maps.newHashMap();
		Map<String,List<PsiInventory>> stockMap= Maps.newHashMap();
		List<PsiInventoryOutItem> inItem = Lists.newArrayList(); 
		Stock stock=stockService.get(inventoryId);
		String stockName=stock.getName();
		//查出产品所有颜色的
		List<PsiInventory> list=this.psiInventoryService.find(productNames, inventoryId);
		if(list!=null&&list.size()>0){
			for(PsiInventory inventory:list){
				if(productNameColors.contains(inventory.getProductNameColor())){
					if((inventory.getNewQuantity()+inventory.getOfflineQuantity())>0){//只要有库存的
						String proNameColor =inventory.getProductNameColor();
						List<PsiInventory>  inventorys = null;
						if(stockMap.get(proNameColor)==null){
							inventorys=Lists.newArrayList();
						}else{
							inventorys=stockMap.get(proNameColor);
						}
						inventorys.add(inventory);
						stockMap.put(proNameColor, inventorys);
						Integer quantity = inventory.getNewQuantity()+inventory.getOfflineQuantity();
						if(colorMap.get(proNameColor)!=null){
							quantity+=colorMap.get(proNameColor);
						}
						colorMap.put(proNameColor, quantity);
					}
				}
			}
			
			for(Map.Entry<String,Integer> entry :productNameColorMap.entrySet()){
				String nameColor = entry.getKey();
				Integer quantity = entry.getValue();
				Integer stockQ = colorMap.get(nameColor);
				if(stockQ==null||quantity>stockQ){
					sb.append(nameColor+" (out:"+quantity+",stock:"+(stockQ==null?0:stockQ)+");");
				}
			}
			
			//没有库存不足的情况
			if(StringUtils.isEmpty(sb)){
				PsiInventoryOut psiInventoryOut = new PsiInventoryOut();
				psiInventoryOut.setAddDate(new Date());
				SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMddHHmmsss");
				String flowNo=sdf.format(new Date());
				String inNo=flowNo.substring(0,8)+"_CKD"+flowNo.substring(8);
				String operationType="track outbound";
				psiInventoryOut.setBillNo(inNo);
				psiInventoryOut.setOperationType(operationType);
				psiInventoryOut.setWarehouseId(inventoryId);
				psiInventoryOut.setWarehouseName(stockName);
				psiInventoryOut.setDataDate(new Date());
				for(Map.Entry<String,Integer> entry :productNameColorMap.entrySet()){
					String nameColor = entry.getKey();
					Integer quantity = entry.getValue();
					List<PsiInventory> inventorys = stockMap.get(nameColor);
					for(PsiInventory inventory: inventorys){
						if(quantity.intValue()<=0){
							break;
						}
						Integer offlineQ=inventory.getOfflineQuantity();
						Integer newQ=inventory.getNewQuantity();
						Integer tempQ = 0;
						if(offlineQ>0){
							quantity -=offlineQ;
							if(quantity>0){
								//线下数不够
								tempQ=offlineQ;
							}else{
								tempQ = quantity+offlineQ;
							}
							//生成出库单信息
							makeUpItem("offline", inventory, psiInventoryOut, inventory.getProductId(), inventory.getProductName(), inventory.getCountryCode(), inventory.getColorCode(),inventory.getSku(), tempQ, operationType, inventoryId, "", inNo, inItem);
						}
						if(newQ>0){
							quantity -=inventory.getNewQuantity();
							if(quantity>0){
								//new数不够
								tempQ = newQ;
							}else{
								tempQ = quantity+newQ;
							}
							//生成出库单信息
							makeUpItem("new", inventory, psiInventoryOut, inventory.getProductId(), inventory.getProductName(), inventory.getCountryCode(), inventory.getColorCode(),inventory.getSku(), tempQ, operationType, inventoryId, "", inNo, inItem);
						}
						inventory.setUpdateDate(new Date());  //最近更新的查询时最先显示
						psiInventoryDao.save(inventory);
					}
					
				}
				psiInventoryOut.getItems().addAll(inItem);
				this.psiInventoryOutDao.save(psiInventoryOut);
			}
		}else{
			return "No Stocks";
		}
		
		
		return sb.toString().replace(";", "<br/>");
	}
	
	
	public String outBoundByProduct(Integer inventoryId,Map<String,Integer> productNameColorMap,Map<String,Integer> offlineMap){
		StringBuffer sb = new StringBuffer();
		Set<String> productNames = Sets.newHashSet();
		if(productNameColorMap!=null){
			Set<String> productNameColors=productNameColorMap.keySet();
			for(String nameColor:productNameColors){
				String arr[] = nameColor.split("_");
				productNames.add(arr[0]);
			}
		}
		
		if(offlineMap!=null){
			Set<String> productNameColors=offlineMap.keySet();
			for(String nameColor:productNameColors){
				String arr[] = nameColor.split("_");
				productNames.add(arr[0]);
			}
		}
		
		Map<String,Integer> colorMap= Maps.newHashMap();
		Map<String,Integer> offlineColorMap= Maps.newHashMap();
		
		Map<String,List<PsiInventory>> stockMap= Maps.newHashMap();
		Map<String,List<PsiInventory>> offlineStockMap= Maps.newHashMap();
		List<PsiInventoryOutItem> inItem = Lists.newArrayList(); 
		Stock stock=stockService.get(inventoryId);
		String stockName=stock.getName();
		//查出产品所有颜色的
		List<PsiInventory> list=this.psiInventoryService.find(productNames, inventoryId);
		if(list!=null&&list.size()>0){
			for(PsiInventory inventory:list){
					if(inventory.getNewQuantity()>0){//只要有库存的
						String proNameColor =inventory.getProductNameColor();
						List<PsiInventory>  inventorys = null;
						if(stockMap.get(proNameColor)==null){
							inventorys=Lists.newArrayList();
						}else{
							inventorys=stockMap.get(proNameColor);
						}
						inventorys.add(inventory);
						stockMap.put(proNameColor, inventorys);
						Integer quantity = inventory.getNewQuantity();
						if(colorMap.get(proNameColor)!=null){
							quantity+=colorMap.get(proNameColor);
						}
						colorMap.put(proNameColor, quantity);
					}
					
					if(inventory.getOfflineQuantity()>0){//只要有库存的
						String proNameColor =inventory.getProductNameColor();
						List<PsiInventory>  inventorys = null;
						if(offlineStockMap.get(proNameColor)==null){
							inventorys=Lists.newArrayList();
						}else{
							inventorys=offlineStockMap.get(proNameColor);
						}
						inventorys.add(inventory);
						offlineStockMap.put(proNameColor, inventorys);
						Integer quantity = inventory.getOfflineQuantity();
						if(offlineColorMap.get(proNameColor)!=null){
							quantity+=offlineColorMap.get(proNameColor);
						}
						offlineColorMap.put(proNameColor, quantity);
					}
					
			}
			
			if(productNameColorMap!=null){
				for(Map.Entry<String,Integer> entry :productNameColorMap.entrySet()){
					String nameColor = entry.getKey();
					Integer quantity = entry.getValue();
					Integer stockQ = colorMap.get(nameColor);
					if(stockQ==null||quantity>stockQ){
						sb.append(nameColor+" (out:"+quantity+",stock:"+(stockQ==null?0:stockQ)+");");
					}
				}
			}
			
			
            if(offlineMap!=null){
            	for(Map.Entry<String,Integer> entry :offlineMap.entrySet()){
    				String nameColor = entry.getKey();
    				Integer quantity = entry.getValue();
    				Integer stockQ = offlineColorMap.get(nameColor);
    				if(stockQ==null||quantity>stockQ){
    					sb.append(nameColor+" (offline out:"+quantity+",stock:"+(stockQ==null?0:stockQ)+");");
    				}
    			}
			}
			
			//没有库存不足的情况
			if(StringUtils.isEmpty(sb)){
				PsiInventoryOut psiInventoryOut = new PsiInventoryOut();
				psiInventoryOut.setAddDate(new Date());
				SimpleDateFormat sdf =new SimpleDateFormat("yyyyMMddHHmmsss");
				String flowNo=sdf.format(new Date());
				String inNo=flowNo.substring(0,8)+"_CKD"+flowNo.substring(8);
				String operationType="track outbound";
				psiInventoryOut.setBillNo(inNo);
				psiInventoryOut.setOperationType(operationType);
				psiInventoryOut.setWarehouseId(inventoryId);
				psiInventoryOut.setWarehouseName(stockName);
				psiInventoryOut.setDataDate(new Date());
				if(productNameColorMap!=null){
					for(Map.Entry<String,Integer> entry :productNameColorMap.entrySet()){
						String nameColor = entry.getKey();
						Integer quantity = entry.getValue();
						List<PsiInventory> inventorys = stockMap.get(nameColor);
						for(PsiInventory inventory: inventorys){
							if(quantity.intValue()<=0){
								break;
							}
							Integer newQ=inventory.getNewQuantity();
							Integer tempQ = 0;
							
							if(newQ>0){
								quantity -=inventory.getNewQuantity();
								if(quantity>0){
									//new数不够
									tempQ = newQ;
								}else{
									tempQ = quantity+newQ;
								}
								//生成出库单信息
								makeUpItem("new", inventory, psiInventoryOut, inventory.getProductId(), inventory.getProductName(), inventory.getCountryCode(), inventory.getColorCode(),inventory.getSku(), tempQ, operationType, inventoryId, "", inNo, inItem);
							}
							inventory.setUpdateDate(new Date());  //最近更新的查询时最先显示
							psiInventoryDao.save(inventory);
						}
						
					}
				}
				
                if(offlineMap!=null){
                	for(Map.Entry<String,Integer> entry :offlineMap.entrySet()){
    					String nameColor = entry.getKey();
    					Integer quantity = entry.getValue();
    					List<PsiInventory> inventorys = offlineStockMap.get(nameColor);
    					for(PsiInventory inventory: inventorys){
    						if(quantity.intValue()<=0){
    							break;
    						}
    						Integer offlineQ=inventory.getOfflineQuantity();
    						Integer tempQ = 0;
    						if(offlineQ>0){
    							quantity -=offlineQ;
    							if(quantity>0){
    								//线下数不够
    								tempQ=offlineQ;
    							}else{
    								tempQ = quantity+offlineQ;
    							}
    							//生成出库单信息
    							makeUpItem("offline", inventory, psiInventoryOut, inventory.getProductId(), inventory.getProductName(), inventory.getCountryCode(), inventory.getColorCode(),inventory.getSku(), tempQ, operationType, inventoryId, "", inNo, inItem);
    						}
    						
    						inventory.setUpdateDate(new Date());  //最近更新的查询时最先显示
    						psiInventoryDao.save(inventory);
    					}
    					
    				}
				}
				
				psiInventoryOut.getItems().addAll(inItem);
				this.psiInventoryOutDao.save(psiInventoryOut);
			}
		}else{
			return "No Stocks";
		}
		
		
		return sb.toString().replace(";", "<br/>");
	}
	
}
