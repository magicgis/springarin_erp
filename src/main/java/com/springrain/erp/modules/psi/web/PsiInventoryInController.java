/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.excel.ExportTransportExcel;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonRemovalOrderService;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiInventoryIn;
import com.springrain.erp.modules.psi.entity.PsiInventoryInItem;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PsiTransportOrder;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.entity.lc.LcPsiLadingBill;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrder;
import com.springrain.erp.modules.psi.service.PsiInventoryInService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.PsiTransportOrderService;
import com.springrain.erp.modules.psi.service.StockService;
import com.springrain.erp.modules.psi.service.lc.LcPsiLadingBillService;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportOrderService;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 入库管理Controller
 * @author Michael
 * @version 2015-01-05
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiInventoryIn")
public class PsiInventoryInController extends BaseController {
	@Autowired
	private PsiInventoryInService 			psiInventoryInService;
	@Autowired
	private PsiInventoryService 			psiInventoryService;
	@Autowired
	private SystemService 					userService;
	@Autowired
	private StockService 					stockService;
	@Autowired
	private PsiProductService 				productService;
	@Autowired
	private PsiTransportOrderService 		tranSportService;
	@Autowired
	private LcPsiTransportOrderService 		lcTranSportService;
	@Autowired
	private LcPsiLadingBillService 			lcLadingBillService;
	@Autowired
	private MailManager 					mailManager;
	@Autowired
	private PsiSupplierService 				supplierService;
	@Autowired
	private AmazonRemovalOrderService amazonRemovalOrderService;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	
	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = {"list", ""})
	public String list(PsiInventoryIn psiInventoryIn, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (psiInventoryIn.getAddDate()== null) {
			psiInventoryIn.setAddDate(DateUtils.addMonths(today, -1));
			psiInventoryIn.setAddDateS(today);
		}
		
		Page<PsiInventoryIn> page = new Page<PsiInventoryIn>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("addDate desc");
		}else{
			page.setOrderBy(orderBy+",addDate desc");
		}	
		
        page = psiInventoryInService.find(page, psiInventoryIn); 
        
        
        //添加运输方式一栏   如果是运单入库，就查出运输模式，如果是fba运输，就查出是哪个供应商运输的
        Set<String>  tranNos = Sets.newHashSet();
        Map<Integer,String> localTranModel = Maps.newHashMap();
        for(PsiInventoryIn in:page.getList()){
        	if("Transport Storing".equals(in.getOperationType())){
        		try{
        			tranNos.add(in.getTranLocalNo());
        			localTranModel.put(in.getId(), in.getTranLocalNo());
        		}catch(Exception ex){}
        	}
        }
        
        
       Map<String,String>  modelMap= Maps.newHashMap();
       if(tranNos.size()>0){
    	   modelMap=  this.tranSportService.getTranModel(tranNos);
       }
       
        if(localTranModel.size()>0){
        	for(Map.Entry<Integer, String> entry:localTranModel.entrySet()){
        		Integer outId = entry.getKey();
         	   String tranNo = entry.getValue();
         	   localTranModel.put(outId, modelMap.get(tranNo)) ;
            } 
        }
       
       model.addAttribute("localTranModel", localTranModel);
        
       
       
        //查询用户和本地仓库
        List<User> allUser = userService.findAllUsers();
        List<Stock> stocks =stockService.findStocks("0");
        model.addAttribute("stocks", stocks);
  		model.addAttribute("allUser", allUser);
        model.addAttribute("page", page);
		return "modules/psi/psiInventoryInList";
	}
	
	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = "add")
	public String add(PsiInventoryIn psiInventoryIn, Model model) throws IOException {
		//只查询本地仓库
		String   warehouseName=psiInventoryIn.getWarehouseName();
		psiInventoryIn.setWarehouseName(URLDecoder.decode(warehouseName,"utf-8"));
		
		//获取仓库国家编码
		Set<String> countrySet=Sets.newHashSet();
		//生成出库单流水号，
		//psiInventoryIn.setBillNo(this.psiInventoryInService.createFlowNo());
		
		//根据用户权限获得仓库可选择信息
		List<Stock> stocks=stockService.findStocks("0");
		List<Stock> tempStocks = Lists.newArrayList();
		//首次进来
		Set<String> permissionsSet = Sets.newHashSet();
		//查询权限
		User user = UserUtils.getUser();
		if(!user.isAdmin()){
			for(Role role:UserUtils.getUser().getRoleList()){
				permissionsSet.addAll(role.getPermissions());
			}
			for(Stock stock:stocks){
				if(stock.getId().equals(psiInventoryIn.getWarehouseId())){
					if(StringUtils.isNotEmpty(stock.getCountrycode())){
						if("US".equals(stock.getCountrycode())){
							countrySet.add("com");
							countrySet.add("ca");
						}else if("DE".equals(stock.getCountrycode())){
							countrySet.add("de");
							countrySet.add("fr");
							countrySet.add("it");
							countrySet.add("uk");
							countrySet.add("es");
						}
					};
				}
				String countryCode=stock.getCountrycode();
				if(permissionsSet.contains("psi:inventory:edit:"+countryCode+"")){
					tempStocks.add(stock);
				}
			}
			stocks=tempStocks;
		}
		
		List<PsiSku> skuList = productService.getSkus(countrySet);
		Map<String,Object[]> skus=Maps.newHashMap();
		List<String> qualityTypes= Lists.newArrayList();
		qualityTypes.add("new");
		qualityTypes.add("old");
		qualityTypes.add("broken");
		qualityTypes.add("renew");
		qualityTypes.add("spares");
		qualityTypes.add("offline");
		List<String> operationTypes = Lists.newArrayList();
		operationTypes.add("Inventory Taking Storing");
		operationTypes.add("Transport Storing");
		operationTypes.add("Lot Storing");
		operationTypes.add("Return Storing");
		operationTypes.add("Recall Storing");
		operationTypes.add("Manual Operation");
		//查询所有运单  ：已出库的        类型非批发发货      本地仓的 id数组
		List<PsiTransportOrder> tranOrders=tranSportService.findInventoryInTranOrder(new String[]{"1","2","3","4"},psiInventoryIn.getWarehouseId(),new String[]{"0","1"});
		//所有理诚的订单
		List<LcPsiTransportOrder> lcTranOrders=lcTranSportService.findInventoryInTranOrder(new String[]{"1","2","3","4"},psiInventoryIn.getWarehouseId(),new String[]{"0","1"});
		for(PsiSku sku:skuList){
			if("1".equals(sku.getUseBarcode())){   
				Object[] obj = {sku.getProductId(),sku.getProductName(),sku.getCountry(),sku.getColor()};
				skus.put(sku.getSku(), obj);
			}
		}
		
		//查询库存所有的sku  
		List<PsiInventory> inventorys=this.psiInventoryService.findByStock(psiInventoryIn.getWarehouseId());
		for(PsiInventory inventory:inventorys){
			if(skus.get(inventory.getSku())==null){
				Object[] obj = {inventory.getProductId(),inventory.getProductName(),inventory.getCountryCode(),inventory.getColorCode()};
				skus.put(inventory.getSku(), obj);
			}   
		}
		
		Map<String,String>  tranMap = Maps.newHashMap();
		
		for(PsiTransportOrder tranOrder:tranOrders){
			tranMap.put(tranOrder.getTransportNo(),tranOrder.getBillNo());
		}
		
		for(LcPsiTransportOrder tranOrder:lcTranOrders){
			tranMap.put(tranOrder.getTransportNo(),tranOrder.getBillNo());
		}
		
		List<String> noSkus = this.psiInventoryService.getNoSkus(psiInventoryIn.getWarehouseId());
		model.addAttribute("noSkus", JSON.toJSON(noSkus));
		model.addAttribute("operationTypes", operationTypes);
		model.addAttribute("qualityTypes", qualityTypes);
        model.addAttribute("stocks", stocks);
        model.addAttribute("skus", JSON.toJSON(skus));
        model.addAttribute("tranMap",tranMap);
        model.addAttribute("recallMap", amazonRemovalOrderService.findForStore());
        model.addAttribute("skuFnskuMap",productService.getSkuAndFnskuMap(null));
		return "modules/psi/psiInventoryInAdd";
	}
	
	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = "batch")
	public String batch(PsiInventoryIn psiInventoryIn, Model model) throws UnsupportedEncodingException {
		String   warehouseName=psiInventoryIn.getWarehouseName();
		psiInventoryIn.setWarehouseName(URLDecoder.decode(warehouseName,"utf-8"));
		return "modules/psi/psiInventoryBatch";
	}
	
	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = "batchSave")
	public String batchSave(MultipartFile excelFile,PsiInventoryIn psiInventoryIn, RedirectAttributes redirectAttributes) throws Exception {
		try {
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/batchStock/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			File baseDir = new File(baseDirStr); 
			if(!baseDir.isDirectory())
				baseDir.mkdirs();
			String name = excelFile.getOriginalFilename();
			File dest = new File(baseDir,name);
			if(dest.exists()){
				dest.delete();
			}
			FileUtils.copyInputStreamToFile(excelFile.getInputStream(),dest);
		}catch(Exception e){}	
		this.psiInventoryInService.batchSave(excelFile,psiInventoryIn);
		addMessage(redirectAttributes, "批量矫正数据成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiInventory/list?warehouse.id="+psiInventoryIn.getWarehouseId();
	}
	
	
	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = "addSave")
	public String addSave(PsiInventoryIn psiInventoryIn,Integer returnTestId,MultipartFile memoFile,MultipartFile excelFile, RedirectAttributes redirectAttributes) throws Exception {
		if(!"Lot Storing".equals(psiInventoryIn.getOperationType())){
			if(psiInventoryIn.getItems()==null||psiInventoryIn.getItems().size()==0){
				return null;
			}
		}
		
		this.psiInventoryInService.addSave(psiInventoryIn,returnTestId,memoFile,excelFile);
		addMessage(redirectAttributes, "入库单"+psiInventoryIn.getBillNo()+"添加成功");
		if("Transport Storing".equals(psiInventoryIn.getOperationType())){
			//如果是运输入库就发邮件
			try{
				this.sendEmailToSales(psiInventoryIn);
			}catch(Exception e){
				logger.error("入库发送邮件错误",e);
			}
		}
		
		return "redirect:"+Global.getAdminPath()+"/psi/psiInventory/list?warehouse.id="+psiInventoryIn.getWarehouseId();
	}

	@RequiresPermissions("psi:inventory:view")
	@RequestMapping(value = "view")
	public String view(PsiInventoryIn psiInventoryIn, Model model) {
		if(psiInventoryIn.getId()!=null){
			psiInventoryIn=this.psiInventoryInService.get(psiInventoryIn.getId());
		}else if(StringUtils.isNotEmpty(psiInventoryIn.getBillNo())){
			psiInventoryIn=this.psiInventoryInService.get(psiInventoryIn.getBillNo());
		}else{
			return null;
		}
		model.addAttribute("psiInventoryIn", psiInventoryIn);
		return "modules/psi/psiInventoryInView";
	}
	
	private  void sendEmailToSales(PsiInventoryIn inventoryIn) throws Exception{
		String warehoueCode=this.stockService.get(inventoryIn.getWarehouseId()).getCountrycode();
		Map<String, Object> prarms = Maps.newHashMap();
		prarms.put("inventoryIn",inventoryIn);
		String toAddress = "";
		if("US".equals(warehoueCode)){
			toAddress = "amazon-sales@inateck.com,logistics.usa@inateck.com";
		}else if("DE".equals(warehoueCode)){
			toAddress = "amazon-sales@inateck.com,logistics.eu@inateck.com,fbamitteilung@inateck.com";
		}else if("JP".equals(warehoueCode)){  
			toAddress = "amazon-sales@inateck.com,logistics.jp@inateck.com";
		}else{
			toAddress = "amazon-sales@inateck.com";
		}
		if(inventoryIn.getIsNew()){
			toAddress=toAddress+",supply-chain@inateck.com";
		}
		String content = PdfUtil.getPsiTemplate("warehouseInEmail.ftl",prarms);
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();
			String newContent="";
			if(inventoryIn.getIsNew()){
				newContent="(含未贴码的新品)";
			}
			final MailInfo mailInfo = new MailInfo(toAddress,"Transport In-bound："+inventoryIn.getTranLocalNo()+newContent+",By "+UserUtils.getUser().getName()+" Operation "+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(content);
			new Thread(){
				public void run(){
					mailManager.send(mailInfo);
				} 
			}.start();
		}
	}
		
	
	@ResponseBody
	@RequestMapping(value = {"updateRemark"})
	public String updateRemark(Integer inId,String content,String flag) throws UnsupportedEncodingException {
		return this.psiInventoryInService.updateRemark(inId, URLDecoder.decode(content,"UTF-8"),flag);
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"ajaxTranData"})
	public String ajaxTranData(String tranNo) {
		String rs="";
		if(tranNo.contains("_LC_")){
			LcPsiTransportOrder tranOrder=this.lcTranSportService.get(tranNo);
			rs="{\"msg\":\"true\","+tranOrder.toJson()+"}";
		}else{
			PsiTransportOrder tranOrder=this.tranSportService.get(tranNo);
			rs="{\"msg\":\"true\","+tranOrder.toJson()+"}";
		}
		return rs;
	}
	
	
	
	/**
	 *导出理诚入库单数据
	 * 
	 */
	@RequiresPermissions("psi:order:financeReview")
	@RequestMapping(value ="payExport")
	public String payExport(PsiInventoryIn inventoryIn, HttpServletRequest request, HttpServletResponse response, Model model) {
		inventoryIn.setWarehouseId(130);
		List<PsiInventoryIn> list = this.psiInventoryInService.find(inventoryIn); 
        List<String> title=Lists.newArrayList("入库单编号","入库类型","供应商名字","品名","sku", "数量","不含税单价","含税单价","不含税总计","应付","已付");
        HSSFWorkbook wb = new HSSFWorkbook();
      		HSSFSheet sheet = wb.createSheet();
      		HSSFRow row = sheet.createRow(0);
      		HSSFCellStyle style = wb.createCellStyle();
      		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
      		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
      		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
      		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
      		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
      		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
      		style.setBottomBorderColor(HSSFColor.BLACK.index);
      		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
      		style.setLeftBorderColor(HSSFColor.BLACK.index);
      		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
      		style.setRightBorderColor(HSSFColor.BLACK.index);
      		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
      		style.setTopBorderColor(HSSFColor.BLACK.index);
      		HSSFFont font = wb.createFont();
      		font.setFontHeightInPoints((short) 16); // 字体高度
      		font.setFontName(" 黑体 "); // 字体
      		font.setBoldweight((short) 16);
      		style.setFont(font);
      		
      		HSSFCellStyle style1 = wb.createCellStyle();
    		HSSFDataFormat df1 = wb.createDataFormat();  //此处设置数据格式  
    		style1.setDataFormat(df1.getFormat("#0.00")); //小数点后保留两位，可以写contentStyle.setDataFormat(df.getFormat("#,#0.00"));  
    		 
    		
      	    CellStyle contentStyle = wb.createCellStyle();
		    contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		    contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		    contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		    contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		    contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		    contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		    contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		    contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
      		row.setHeight((short) 600);
      		HSSFCell cell = null;
      		int  rowIndex=1;
      		for (int i = 0; i < title.size(); i++) {
 				cell = row.createCell(i);
 				cell.setCellValue(title.get(i));
 				cell.setCellStyle(style);
 				sheet.autoSizeColumn((short) i);
 			}
      		if(list!=null){
      			Set<Integer> ladingItemIds = Sets.newHashSet();
      			for(int i=0;i<list.size();i++){
      				PsiInventoryIn in=list.get(i);
      				//是提单入库
      				for(PsiInventoryInItem inItem:in.getItems()){
      					if(inItem.getBillItemId()!=null){
      						ladingItemIds.add(inItem.getBillItemId());
      					}
      				}
      			}
      			
      			Map<Integer,PsiSupplier> supplierMap =this.supplierService.findAllMap();
      			
      			Map<Integer,String> ladingItemMap =Maps.newHashMap();
      			if(ladingItemIds.size()>0){
      				ladingItemMap=this.lcLadingBillService.getLadingItemPayDetail(ladingItemIds);
      			}
      			Map<String,Float> avgPrice=this.psiInventoryService.getAvgPriceByWarehouseId(130);
      			
      			Float noTaxAmount =0f;
      			Float shouldPayAmount =0f;
      			Float hasPayAmount=0f;
      			
      			for(int i=0;i<list.size();i++){
      				PsiInventoryIn in=list.get(i);
  					//"入库单编号","品名","国家", "数量","不含税单价","含税单价","不含税总计","应付","已付"
  					for(PsiInventoryInItem inItem:in.getItems()){
  						String sku =inItem.getSku();
  						row=sheet.createRow(rowIndex++);
  						int j=0;
  						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(in.getBillNo());
  						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(in.getOperationType());
  						
  						String supplierName = "";
  	    				Integer billItemId = inItem.getBillItemId();
  	    				Integer quantity = inItem.getQuantity();
  	    				Float price =0f;
						Float hasPay =0f;
  						if(billItemId!=null){
  							String payDetail=ladingItemMap.get(billItemId);
  							if(StringUtils.isNotEmpty(payDetail)){
  								String payArr[]=payDetail.split(",");
  								//a.`item_price`,',',a.`total_payment_amount`,',',a.`quantity_lading`),a
  								price = Float.parseFloat(payArr[0]);
  								Float ladingItemPayAmount = Float.parseFloat(payArr[1]);
  								Integer ladingQuantity = Integer.parseInt(payArr[2]);
  								Integer supplierId = Integer.parseInt(payArr[3]);
  								Integer deposit = Integer.parseInt(payArr[4]);
  								hasPay=ladingItemPayAmount*quantity/ladingQuantity;
  								if("Yes".equals(payArr[5])){
  									hasPay+=price*quantity*deposit/100;
  								}

  		  						if(supplierMap.get(supplierId)!=null){
  		  							supplierName=supplierMap.get(supplierId).getName();
  		  						}
  							}else{
  								if(avgPrice.get(sku)!=null){
  									price=avgPrice.get(sku);
  								}
  							}
      					}else{
      						if(avgPrice.get(sku)!=null){
								price=avgPrice.get(sku);
							}
      					}
  						
  						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(supplierName);
  	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(inItem.getProductNameColor());
  	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(inItem.getSku());
  	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(inItem.getQuantity());
  	    				
  	    				cell=row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
  						cell.setCellStyle(style1);
  						cell.setCellValue(price/1.17);
  						
  						cell=row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
  						cell.setCellStyle(style1);
  						cell.setCellValue(price);
  						
  						cell=row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
  						cell.setCellStyle(style1);
  						cell.setCellValue(price*quantity/1.17);
  						
  						cell=row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
  						cell.setCellStyle(style1);
  						cell.setCellValue(price*quantity);
  						
  						cell=row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
  						cell.setCellStyle(style1);
  						cell.setCellValue(hasPay);
  						
  						
  						noTaxAmount +=price*quantity/1.17f;
  		      			shouldPayAmount +=price*quantity;
  		      			hasPayAmount+=hasPay;
      				}
      			}
      			row=sheet.createRow(rowIndex++);
      			row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue("合计");
      			
      			cell=row.createCell(8,Cell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(style1);
				cell.setCellValue(noTaxAmount);
				
				cell=row.createCell(9,Cell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(style1);
				cell.setCellValue(shouldPayAmount);
				
				cell=row.createCell(10,Cell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(style1);
				cell.setCellValue(hasPayAmount);
      		}
      		try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
		
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		
				String fileName = "psiInventoryIn" + sdf.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}
	
	
	
	

	/**
	 *导出理诚入库单数据
	 * 
	 */
	@RequiresPermissions("psi:order:financeReview")
	@RequestMapping(value ="payExportLc")
	public String payExportLc(PsiInventoryIn inventoryIn, HttpServletRequest request, HttpServletResponse response, Model model) {
		inventoryIn.setWarehouseId(130);
		inventoryIn.setOperationType("Purchase Storing");
		List<PsiInventoryIn> list = this.psiInventoryInService.find(inventoryIn); 
        List<String> title=Lists.newArrayList("品名","收货单编号","收货日期", "供应商(中文全称)","单位","数量","装箱数","箱数","进项发票号","不含税单价","不含税总金额","产品描述");
        HSSFWorkbook wb = new HSSFWorkbook();
      		HSSFSheet sheet = wb.createSheet();
      		HSSFRow row = sheet.createRow(0);
      		HSSFCellStyle style = wb.createCellStyle();
      		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
      		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
      		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
      		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
      		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
      		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
      		style.setBottomBorderColor(HSSFColor.BLACK.index);
      		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
      		style.setLeftBorderColor(HSSFColor.BLACK.index);
      		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
      		style.setRightBorderColor(HSSFColor.BLACK.index);
      		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
      		style.setTopBorderColor(HSSFColor.BLACK.index);
      		HSSFFont font = wb.createFont();
      		font.setFontHeightInPoints((short) 16); // 字体高度
      		font.setFontName(" 黑体 "); // 字体
      		font.setBoldweight((short) 16);
      		style.setFont(font);
      		
      		HSSFCellStyle style1 = wb.createCellStyle();
    		HSSFDataFormat df1 = wb.createDataFormat();  //此处设置数据格式  
    		style1.setDataFormat(df1.getFormat("#0.00")); //小数点后保留两位，可以写contentStyle.setDataFormat(df.getFormat("#,#0.00"));  
    		 
    		SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd");
      	    CellStyle contentStyle = wb.createCellStyle();
		    contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		    contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		    contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		    contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		    contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		    contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		    contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		    contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
      		row.setHeight((short) 600);
      		HSSFCell cell = null;
      		int  rowIndex=1;
      		for (int i = 0; i < title.size(); i++) {
 				cell = row.createCell(i);
 				cell.setCellValue(title.get(i));
 				cell.setCellStyle(style);
 				sheet.autoSizeColumn((short) i);
 			}
      		if(list!=null){
      			Set<String> ladingNos = Sets.newHashSet();
      			for(int i=0;i<list.size();i++){
      				PsiInventoryIn in=list.get(i);
      				//是提单入库
      				ladingNos.add(in.getBillNo());
      			}
  				Map<String,PsiSupplier> supplierMap =this.supplierService.findNikeMap();
  				Map<String,PsiProduct> productMap = this.productService.getProductMap();
      			for(int i=0;i<list.size();i++){
      				PsiInventoryIn in=list.get(i);
      				Map<String,Integer> productQuantity = Maps.newHashMap();
      				Map<String,Float> productPrice = Maps.newHashMap();
  					for(PsiInventoryInItem inItem:in.getItems()){
  						String productName =inItem.getProductName();
  						if("spares".equals(inItem.getQualityType())){//备品不要
  							continue;
  						}
  						Integer quantity = inItem.getQuantity();
  						Float price = inItem.getPrice();
  						if(productQuantity.get(productName)!=null){
  							quantity+=productQuantity.get(productName);
  						}
  						productQuantity.put(productName, quantity);
  						
  						if(price!=null&&productPrice.get(productName)==null){
  							productPrice.put(productName, price);
  						}
      				}
  					
	      			for(Map.Entry<String, Integer> entry :productQuantity.entrySet()){
	      				int j =0;
	      				row=sheet.createRow(rowIndex++);
	      				//"品名","收货单编号","收货日期", "供应商(中文全称)","产品描述","单位","数量","进项发票号","不含税单价","不含税总金额"
	      				//20170227XIM_LC_TDH01
	      				String nikeName = in.getTranLocalNo().substring(8,in.getTranLocalNo().indexOf("_"));
	      				PsiSupplier supplier = supplierMap.get(nikeName);
      					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(entry.getKey());
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(in.getTranLocalNo());
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(sdf.format(in.getDataDate()));
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(supplier.getName());
	    				PsiProduct product = productMap.get(entry.getKey());
	    				
	    				String chineseName = product.getChineseName()==null?"":product.getChineseName();
	    				String unit ="";
	    				if(chineseName.contains("(")){
	    					unit=chineseName.substring(chineseName.indexOf("(")+1,chineseName.indexOf(")"));
	    				}
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(unit);
	    				
	    				
	    				
	    				cell=row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
	    				cell.setCellValue(entry.getValue());
	    				int boxN=(int)(entry.getValue()/product.getPackQuantity());
	    				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getPackQuantity());
	    				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue((entry.getValue()%product.getPackQuantity()==0)?boxN:(boxN+1));
	    				
	    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
	    				Float  taxPrice =productPrice.get(entry.getKey());
						cell=row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
						cell.setCellStyle(style1);
						cell.setCellValue(taxPrice*(100f/(supplier.getTaxRate()+100)));
						
						cell=row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
						cell.setCellStyle(style1);
						cell.setCellValue(entry.getValue()*taxPrice*(100f/(supplier.getTaxRate()+100)));
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getDescription());
	      			}
	      				
  				}
  			}
  		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
	
			SimpleDateFormat sdf1 = new SimpleDateFormat("MMddhhmm");
	
			String fileName = "psiInventoryIn" + sdf1.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	//将运单信息导出为excel表格
		@RequestMapping(value="expInBound")
		public String exportInBoundBill(Integer id,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
			SimpleDateFormat sdfD = new SimpleDateFormat("yyyy-MM-dd");
			PsiInventoryIn inBound = this.psiInventoryInService.get(id);
			
			ExportTransportExcel ete = new ExportTransportExcel();
			PsiInventoryIn tempInBound = new PsiInventoryIn();
			
			//根据出库单item里的提单id，查询出提单号
			LcPsiLadingBill ladingBill= this.lcLadingBillService.getLadingBillByItemId(inBound.getItems().get(0).getBillItemId());
			List<PsiInventoryInItem> itemTemp = Lists.newArrayList();
			Map<Integer,PsiProduct> map = this.productService.findProductsMap(null);
			for(PsiInventoryInItem item:inBound.getItems()){
				//装箱数、箱数、毛重、外箱尺寸
				Integer boxNumber =0;
				Integer packQuantity=0;
				Float gw =0f;
				Float boxVolume=0f;
				PsiProduct product =map.get(item.getProductId());
				if(product!=null){
					packQuantity=(product.getPackQuantity()==null||product.getPackQuantity().intValue()==0)?1:product.getPackQuantity();
					int i =item.getQuantity()%packQuantity;
					if(i==0){
						boxNumber=item.getQuantity()/packQuantity;
					}else{
						boxNumber=item.getQuantity()/packQuantity+1;
					}
					gw=product.getGw().multiply(new BigDecimal(boxNumber)).floatValue();
					boxVolume=product.getBoxVolume().multiply(new BigDecimal(boxNumber)).floatValue();
				}
				itemTemp.add(new PsiInventoryInItem(item.getProductId(), item.getProductName(), item.getColorCode(), item.getCountryCode(),
						item.getQuantity(), null, null, tempInBound, null, null, null,packQuantity,boxNumber,gw,boxVolume));
			}
			
			
			tempInBound.setFlowNo(inBound.getFlowNo());
			tempInBound.setTranMan(inBound.getTranMan());
			tempInBound.setPhone(inBound.getPhone());
			tempInBound.setCarNo(inBound.getCarNo());
			tempInBound.setBillNo(ladingBill.getBillNo());//提单号
			PsiSupplier supplier = ladingBill.getSupplier();
			tempInBound.setSupplierName(supplier.getName());
			tempInBound.setSupplierPhone(supplier.getPhone());
			tempInBound.setFormatDate(sdfD.format(inBound.getAddDate()));
			tempInBound.setItems(itemTemp);
			
			
			Workbook workbook = null;
			String modelName = "InBound";//模板文件名称
			String xmlName = "InBound";
			workbook = ete.writeData(tempInBound,	xmlName, modelName, 0);
			//下载excel文档
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = modelName + sdf.format(new Date())+ ".xlsx";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition","attachment;filename=" + fileName);
			try {
				OutputStream out = response.getOutputStream();
				workbook.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
}
