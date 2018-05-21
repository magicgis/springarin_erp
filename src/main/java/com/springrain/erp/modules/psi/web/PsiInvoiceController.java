/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProductTypeCode;
import com.springrain.erp.modules.psi.entity.PsiInvoiceProduct;
import com.springrain.erp.modules.psi.entity.PsiInvoiceTransportDeclare;
import com.springrain.erp.modules.psi.entity.PsiSupplierInvoice;
import com.springrain.erp.modules.psi.service.PsiInvoiceService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;


@Controller
@RequestMapping(value = "${adminPath}/psi/psiInvoice")
public class PsiInvoiceController extends BaseController {

	@Autowired
	private PsiInvoiceService       psiInvoiceService;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PsiInvoiceController.class);
	
	@RequestMapping(value = {"list", ""})
	public String list(PsiSupplierInvoice psiSupplierInvoice, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		Page<PsiSupplierInvoice> page=new Page<PsiSupplierInvoice>(request, response);
		if (psiSupplierInvoice.getCreateDate() == null) {
			Date today=new Date();
			psiSupplierInvoice.setCreateDate(DateUtils.addYears(today, -1));
			psiSupplierInvoice.setInvoiceDate(today);
		}
        model.addAttribute("page",psiInvoiceService.find(page, psiSupplierInvoice));
		return "modules/psi/psiInvoiceList";
	}
	
	@RequestMapping(value = {"declareList"})
	public String list(PsiInvoiceTransportDeclare psiInvoiceTransportDeclare, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		Page<PsiInvoiceTransportDeclare> page=new Page<PsiInvoiceTransportDeclare>(request, response);
		if (psiInvoiceTransportDeclare.getCreateDate() == null) {
			Date today=new Date();
			psiInvoiceTransportDeclare.setCreateDate(DateUtils.addMonths(today,-2));
			psiInvoiceTransportDeclare.setArrangeDate(today);
		}
        model.addAttribute("page",psiInvoiceService.find(page,psiInvoiceTransportDeclare));
		return "modules/psi/psiDeclareList";
	}
	
	
	@RequestMapping(value = {"productList"})
	public String productList(HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
        model.addAttribute("productList",psiInvoiceService.find());
		return "modules/psi/psiInvoiceProductList";
	}
	
	@RequestMapping(value = "deleteProduct")
	public String deleteProduct(Integer id, RedirectAttributes redirectAttributes){
		psiInvoiceService.updateProduct(id);
		addMessage(redirectAttributes,id+" 删除成功");
	    return "redirect:"+Global.getAdminPath()+"/psi/psiInvoice/productList?repage";
	}
	
	@ResponseBody
	@RequestMapping(value = {"uploadRate"})
	public String uploadRate(Integer id,Float rate) {
		psiInvoiceService.updateRate(id, rate);
		return "1";
	}
	
	@RequestMapping(value = "uploadProductFile")
	public String uploadProductFile(@RequestParam("excel")MultipartFile excelFile,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/productInvoice/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
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
	
		try {
			    List<PsiInvoiceProduct> invoiceList=Lists.newArrayList();
			    StringBuilder sb = new StringBuilder();
				Workbook workBook = WorkbookFactory.create(excelFile.getInputStream());
				Sheet sheet = workBook.getSheetAt(0);
				sheet.setForceFormulaRecalculation(true);
				// 循环行Row
				for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
					Row row = sheet.getRow(rowNum);
					if (row == null||row.getCell(0)==null||StringUtils.isBlank(getData(row.getCell(0)))) {
						continue;
					}
					String code=getData(row.getCell(0)).trim();
					String productName=getData(row.getCell(1)).trim();
					Float rate=Float.parseFloat(getData(row.getCell(2)).trim());
					PsiInvoiceProduct existProduct=psiInvoiceService.findProduct(code);
					if(existProduct==null){
						invoiceList.add(new PsiInvoiceProduct(code,productName,rate,"0"));
					}
				}
				if(invoiceList.size()>0){
					psiInvoiceService.savePsiInvoiceProduct(invoiceList);
				}
				addMessage(redirectAttributes,"文件上传成功"+sb.toString());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			addMessage(redirectAttributes,"文件上传失败"+e);
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiInvoice/productList?repage";
	}
	
	
	@RequestMapping(value = {"viewDeclareList"})
	public String viewDeclareList(PsiSupplierInvoice psiSupplierInvoice, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
        model.addAttribute("declareList",psiInvoiceService.find(psiSupplierInvoice.getId()));
		return "modules/psi/psiDeclareViewList";
	}
	
	
	@RequestMapping(value = {"resetDeclareList"})
	public String resetDeclareList(String declareNo, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) throws ParseException {
		List<PsiInvoiceTransportDeclare> declareList=psiInvoiceService.findDeclareByDeclareNo(declareNo); 
		Map<Integer,String> idQtyMap=Maps.newHashMap();
		Map<Integer,Integer> qtyMap=Maps.newHashMap();
		Map<Integer,Integer> invoiceMap=Maps.newHashMap();
		for (PsiInvoiceTransportDeclare declare : declareList) {//0:全部归还
			PsiSupplierInvoice  invoice=declare.getInvoice();
			if(invoice!=null&&invoice.getId()!=null){
				qtyMap.put(invoice.getId(),declare.getQuantity()+(qtyMap.get(invoice.getId())==null?0:qtyMap.get(invoice.getId())));
				invoiceMap.put(invoice.getId(),invoice.getUseQuantity());
			}
		}
		
		if(qtyMap.size()>0){
			for (Map.Entry<Integer,Integer> entry: qtyMap.entrySet()) {
				if(entry.getValue().intValue()==invoiceMap.get(entry.getKey()).intValue()){
					idQtyMap.put(entry.getKey(),"0,"+entry.getValue());
				}else{
					idQtyMap.put(entry.getKey(),"1,"+entry.getValue());
				}
			}
		}
		
		boolean flag=psiInvoiceService.resetArrange(declareNo,idQtyMap); 
		if(flag){
			addMessage(redirectAttributes,"重置报关单"+declareNo+"发票分配成功");
		}else{
			addMessage(redirectAttributes,"重置报关单"+declareNo+"发票分配失败");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiInvoice/editDeclare?declareNo="+declareNo;
	}
	
	@RequestMapping(value = {"editDeclare"})
	public String editDeclare(String declareNo, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		float rate=MathUtils.getRate("CNY","USD",null);
		model.addAttribute("rate", rate);
		List<PsiInvoiceTransportDeclare> declareList=psiInvoiceService.findDeclareByDeclareNo(declareNo); 
		model.addAttribute("declareList", declareList);
		
		Map<String,List<PsiSupplierInvoice>> invoiceMap=psiInvoiceService.findUnUseInvoiceInfo(); 
		
		for (PsiInvoiceTransportDeclare declare : declareList) {
			if(declare.getInvoice()!=null&&declare.getInvoice().getId()!=null){
				List<PsiSupplierInvoice> tempList=invoiceMap.get(declare.getProductName());
				if(tempList==null){
					tempList=Lists.newArrayList();
					invoiceMap.put(declare.getProductName(),tempList);
				}
				boolean flag=true;
				for (PsiSupplierInvoice invoice : tempList) {
					if(invoice.getId().equals(declare.getInvoice().getId())){
						flag=false;
						break;
					}
				}
				if(flag){
					tempList.add(declare.getInvoice());
				}
			}
		}
		model.addAttribute("invoiceMap",invoiceMap);
		model.addAttribute("declareNo",declareNo);
		return "modules/psi/psiDeclareEdit";
	}
	
	
	
	@RequestMapping(value = "invoiceSingleArrange")
	@ResponseBody
	public String invoiceSingleArrange(String declareNo,String invoiceIds){
		Map<Integer,PsiInvoiceTransportDeclare> map=psiInvoiceService.findDeclareIdByDeclareNo(declareNo);
		Map<Integer,String> idQtyMap=Maps.newHashMap();
		Map<Integer,Integer> qtyMap=Maps.newHashMap();
		Map<Integer,Integer> invoiceMap=Maps.newHashMap();
		for (Map.Entry<Integer,PsiInvoiceTransportDeclare> declareEntry: map.entrySet()) {//0:全部归还
			PsiInvoiceTransportDeclare declare=declareEntry.getValue();
			PsiSupplierInvoice  invoice=declare.getInvoice();
			if(invoice!=null&&invoice.getId()!=null){
				qtyMap.put(invoice.getId(),declare.getQuantity()+(qtyMap.get(invoice.getId())==null?0:qtyMap.get(invoice.getId())));
				invoiceMap.put(invoice.getId(),invoice.getUseQuantity());
			}
		}
		
		if(qtyMap.size()>0){
			for (Map.Entry<Integer,Integer> entry: qtyMap.entrySet()) {
				if(entry.getValue().intValue()==invoiceMap.get(entry.getKey()).intValue()){
					idQtyMap.put(entry.getKey(),"0,"+entry.getValue());
				}else{
					idQtyMap.put(entry.getKey(),"1,"+entry.getValue());
				}
			}
		}
		try{
			psiInvoiceService.resetArrange(map,declareNo,idQtyMap,invoiceIds); 
		}catch(Exception e){
			return e.getMessage();
		}
		return "0";
	}
	
	@RequestMapping(value = "updateState")
	@ResponseBody
	public String updateState(Integer id,String state){
		try{
			boolean flag=psiInvoiceService.updateState(id,state);
			if(flag){
				return "1";
			}else{
				return "0";
			}
		}catch(Exception e){
			return "0";
		}
	}
	
	

	@ResponseBody
	@RequestMapping(value = {"updateReturnDate"})
	public String updateReturnDate(Integer id,String returnDate) {
		try {
			Date newDate = new Date(returnDate);
			psiInvoiceService.updateReturnDate(id,newDate);
		} catch (Exception e) {
			return "false";
		}
		return "true";
	}
	
	@RequestMapping(value = "uploadFile")
	public String uploadFile(@RequestParam("excel")MultipartFile excelFile,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/vatInvoice/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
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
	
		try {
			    List<PsiSupplierInvoice> invoiceList=Lists.newArrayList();
			    StringBuilder sb = new StringBuilder();
				Workbook workBook = WorkbookFactory.create(excelFile.getInputStream());
				Sheet sheet = workBook.getSheetAt(0);
				sheet.setForceFormulaRecalculation(true);
				// 循环行Row
				Date date=new Date();
				User user=UserUtils.getUser();
				for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
					Row row = sheet.getRow(rowNum);
					if (row == null||row.getCell(2)==null||StringUtils.isBlank(getData(row.getCell(2)))) {
						continue;
					}
					String invoiceNo=getData(row.getCell(2)).trim();
					String productName=getData(row.getCell(5)).trim();
					Integer quantity=Integer.parseInt(getData(row.getCell(8)).trim());
					float totalPrice=Float.parseFloat(getData(row.getCell(10)).trim());
					float price=Float.parseFloat(new DecimalFormat("0.00").format(totalPrice*1d/quantity));
					if(!psiInvoiceService.isExistInfo(invoiceNo,productName,price)){
						Date invoiceDate=dateFormat.parse(getData(row.getCell(0)));
						String invoiceCode=getData(row.getCell(1)).trim();
						String companyName=getData(row.getCell(3)).trim();
						String taxpayerNo=getData(row.getCell(4)).trim();
						String model=getData(row.getCell(6)).trim();
						String unit=getData(row.getCell(7)).trim();
						
						
						String tempRate=getData(row.getCell(11)).trim();
						float rate=0.17f;
						if(StringUtils.isNotBlank(tempRate)){
							rate=Float.parseFloat(tempRate);
						}
						String state="0";
						String checkState=getData(row.getCell(16)).trim();
						if(StringUtils.isNotBlank(checkState)){
							if(checkState.contains("未认证")){
								state="0";
							}else{
								state="1";
							}
						}
						Integer remainQuantity=Integer.parseInt(getData(row.getCell(17)).trim());
						float taxRate=0f;
						
						PsiSupplierInvoice invoice=new PsiSupplierInvoice(invoiceDate,invoiceCode,invoiceNo,companyName,taxpayerNo,
								productName,model,unit,quantity,price,totalPrice,rate,state,remainQuantity,taxRate);
						invoice.setCreateDate(date);
						invoice.setCreateUser(user);
						invoice.setDelFlag("0");
						invoiceList.add(invoice);
					}else{
						sb.append("第"+(rowNum+1)+"行,发票号:"+invoiceNo+",产品名称"+productName+"重复");
					}
				}
				if(invoiceList.size()>0){
					psiInvoiceService.save(invoiceList);
				}
				addMessage(redirectAttributes,"文件上传成功"+sb.toString());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			addMessage(redirectAttributes,"文件上传失败"+e);
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiInvoice/?repage";
	}
	
	
	@RequestMapping(value = "uploadDeclareFile")
	public String uploadDeclareFile(@RequestParam("excel")MultipartFile excelFile,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		try {
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/declareInfo/"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
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
	
		try {
			    Map<String,Float> rateMap=psiInvoiceService.findTaxRate();
			    List<PsiInvoiceTransportDeclare> invoiceList=Lists.newArrayList();
			    StringBuilder sb = new StringBuilder();
				Workbook workBook = WorkbookFactory.create(excelFile.getInputStream());
				Sheet sheet = workBook.getSheetAt(0);
				sheet.setForceFormulaRecalculation(true);
				// 循环行Row
				Date date=new Date();
				User user=UserUtils.getUser();
				for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
					Row row = sheet.getRow(rowNum);
					if (row == null) {
						continue;
					}
					String declareNo=getData(row.getCell(0)).trim();
					if (StringUtils.isBlank(declareNo)) {
						continue;
					}
					String declareNum=getData(row.getCell(3)).trim();
					if(declareNum.toString().length()<3){
						String num="";
						StringBuffer buf= new StringBuffer();
						for(int m=0;m<3-declareNum.toString().length();m++){
							buf.append("0");
						}
						if(StringUtils.isNotBlank(buf.toString())){
							num=buf.toString();
						}
						declareNum=num+declareNum;
					}	
					if(!psiInvoiceService.isExistDeclareInfo(declareNo,declareNum)){
						String declareCode=declareNo+declareNum;
						Date declareDate=dateFormat.parse(getData(row.getCell(1)));
						String transportNo=getData(row.getCell(2)).trim();
						String productNo=getData(row.getCell(4)).trim();
						String productName=getData(row.getCell(5)).trim();
						String productModel="";
						Integer quantity=Integer.parseInt(getData(row.getCell(6)).trim());
						String unit=getData(row.getCell(7)).trim();
						float legalQuantity=Float.parseFloat(getData(row.getCell(8)).trim());
						String legalUnit=getData(row.getCell(9)).trim();
						float price=Float.parseFloat(getData(row.getCell(10)).trim());
						float totalPrice=Float.parseFloat(getData(row.getCell(11)).trim());
						float usdPrice=Float.parseFloat(getData(row.getCell(12)).trim());
						float usdRate=Float.parseFloat(getData(row.getCell(13)).trim());
						float cnyPrice=Float.parseFloat(getData(row.getCell(14)).trim());
						float taxRate=(rateMap.get(productNo)==null?0:rateMap.get(productNo));
						/*try{
							if(StringUtils.isNotBlank(getData(row.getCell(15)).trim())){
								taxRate=Float.parseFloat(getData(row.getCell(15)).trim());
							}
						}catch(Exception e){}*/
						
						invoiceList.add(new PsiInvoiceTransportDeclare(declareDate,declareNo,declareNum,declareCode,transportNo,productNo,productName,productModel,
									quantity,price,"0",user,date,legalUnit,legalQuantity,unit,totalPrice,usdPrice,usdRate,cnyPrice,taxRate));
						
					}else{
						sb.append("第"+(rowNum+1)+"行,报关单号:"+declareNo+",项号"+declareNum+"重复");
					}
				}
				if(invoiceList.size()>0){
					psiInvoiceService.saveDeclare(invoiceList);
				}
				addMessage(redirectAttributes,"文件上传成功"+sb.toString());
		} catch (Exception e) {
			System.out.println(e.getMessage());
			addMessage(redirectAttributes,"文件上传失败"+e);
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiInvoice/declareList?repage";
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"arrangeInvoice"})
	public String arrangeInvoice(){
		Map<String,List<PsiInvoiceTransportDeclare>> map=psiInvoiceService.findUnArrangeDeclare();
		Map<String,List<PsiSupplierInvoice>> invoiceMap=psiInvoiceService.findUnUseInvoiceInfo(); 
		Map<Integer,Integer> remainMap=Maps.newHashMap();
		User user=UserUtils.getUser();
		Date date=new Date();
		List<PsiInvoiceTransportDeclare> declareList=Lists.newArrayList();
		Set<String> transportSet=Sets.newHashSet();
		
		if(map!=null&&map.size()>0){//LOGGER
			float rate=MathUtils.getRate("CNY","USD",null);
			for (Map.Entry<String,List<PsiInvoiceTransportDeclare>>  entry: map.entrySet()) {
				List<PsiInvoiceTransportDeclare> temp=entry.getValue();
				Map<Integer,Integer> tempMap=Maps.newHashMap();
				//单价要合适，首选是出口单价高于进货发票单价 5%-20%，次选出口单价高于进货发票单价>20%，次次选为出口单价低于进货发票单价；
				for (PsiInvoiceTransportDeclare declare: temp) {//换汇成本 3-14内 人民币    发票单价*1.02/(报关单金额/6.8)
					String name=declare.getProductName();
					Integer quantity=declare.getQuantity();
					float price=declare.getPrice()/rate;//出口单价
					
					List<PsiSupplierInvoice> invoiceList=invoiceMap.get(name);
					if(invoiceList==null||invoiceList.size()==0){
						tempMap.clear();
						break;
					}
					Map<Float,List<PsiSupplierInvoice>> tempInvoiceMap=Maps.newHashMap();
					for (PsiSupplierInvoice invoice: invoiceList) {
						Integer invoiceId=invoice.getId();
						Integer remainQuantity=invoice.getRemainingQuantity();
						if(tempMap.get(invoiceId)!=null){
							remainQuantity=remainQuantity-tempMap.get(invoiceId);
						}
						if(remainMap.get(invoiceId)!=null){
							remainQuantity=remainQuantity-remainMap.get(invoiceId);
						}
						float starand=invoice.getPrice()*1.02f/(price/6.8f);
						if(remainQuantity>=quantity&&starand>=3&&starand<=14){//发票《=报关单金额
							List<PsiSupplierInvoice>  list=tempInvoiceMap.get(invoice.getPrice());
							if(list==null){
								list=Lists.newArrayList();
								tempInvoiceMap.put(invoice.getPrice(), list);
							}
							list.add(invoice);
						}
					}
					
					
					if(tempInvoiceMap==null||tempInvoiceMap.size()==0){
						tempMap.clear();
						break;
					}
					
					Set<Float> tempPriceSet=tempInvoiceMap.keySet();
					Float minPirce=0f;
					if(tempPriceSet.size()==1){
						for(Float priceSet:tempPriceSet){
							minPirce=priceSet;
						}
					}else{
						Float[] arr = new Float[tempPriceSet.size()];   
						int i=minDifference(tempPriceSet.toArray(arr),price);
						minPirce=arr[i];
					}
					PsiSupplierInvoice invoice=tempInvoiceMap.get(minPirce).get(0);
					tempMap.put(invoice.getId(),quantity+(tempMap.get(invoice.getId())!=null?tempMap.get(invoice.getId()):0));
						
					declare.setArrangeUser(user);
					declare.setInvoice(invoice);
					declare.setArrangeDate(date);
					
				}
				
				if(tempMap.size()>0){
					declareList.addAll(temp);
					transportSet.add(entry.getKey());
					for (Map.Entry<Integer,Integer> idMap: tempMap.entrySet()) {
						Integer id=idMap.getKey();
						Integer quantiy=(remainMap.get(id)==null?0:remainMap.get(id));
						remainMap.put(id, quantiy+(tempMap.get(id)!=null?tempMap.get(id):0));
					}
				}else{
					for (PsiInvoiceTransportDeclare declare: temp) {
						declare.setArrangeUser(null);
						declare.setInvoice(null);
						declare.setArrangeDate(null);
					}
				}
			}
			
			
			//报关单<=5项的，换汇成本：3-14，其中<=1项超出范围，该1项换汇成本可以在1-30之间。
			//报关单<=10项的，换汇成本：3-14，其中<=2项超出范围，该2项换汇成本可以在1-30之间。
			//报关单<=20项的，换汇成本：3-14，其中<=3项超出范围，该3项换汇成本可以在1-30之间。
			
			for (Map.Entry<String,List<PsiInvoiceTransportDeclare>>  entry: map.entrySet()) {
				if(transportSet.contains(entry.getKey())){//已匹配
					continue;
				}
				List<PsiInvoiceTransportDeclare> temp=entry.getValue();
				Map<Integer,Integer> tempMap=Maps.newHashMap();
				int length=temp.size();
				int suitInvoice=0;
				
				for (PsiInvoiceTransportDeclare declare: temp) {//换汇成本 3-14内 人民币    发票单价*1.02/(报关单金额/6.8)
					String name=declare.getProductName();
					Integer quantity=declare.getQuantity();
					float price=declare.getPrice()/rate;//出口单价
					
					List<PsiSupplierInvoice> invoiceList=invoiceMap.get(name);
					if(invoiceList==null||invoiceList.size()==0){
						tempMap.clear();
						break;
					}
					Map<Float,List<PsiSupplierInvoice>> tempInvoiceMap=Maps.newHashMap();
					for (PsiSupplierInvoice invoice: invoiceList) {
						Integer invoiceId=invoice.getId();
						Integer remainQuantity=invoice.getRemainingQuantity();
						if(tempMap.get(invoiceId)!=null){
							remainQuantity=remainQuantity-tempMap.get(invoiceId);
						}
						if(remainMap.get(invoiceId)!=null){
							remainQuantity=remainQuantity-remainMap.get(invoiceId);
						}
						float starand=invoice.getPrice()*1.02f/(price/6.8f);
						if(remainQuantity>=quantity&&starand>=3&&starand<=14){//发票《=报关单金额
							List<PsiSupplierInvoice>  list=tempInvoiceMap.get(invoice.getPrice());
							if(list==null){
								list=Lists.newArrayList();
								tempInvoiceMap.put(invoice.getPrice(), list);
							}
							list.add(invoice);
						}
					}
					if(tempInvoiceMap==null||tempInvoiceMap.size()==0){
						for (PsiSupplierInvoice invoice: invoiceList) {
							Integer invoiceId=invoice.getId();
							Integer remainQuantity=invoice.getRemainingQuantity();
							if(tempMap.get(invoiceId)!=null){
								remainQuantity=remainQuantity-tempMap.get(invoiceId);
							}
							if(remainMap.get(invoiceId)!=null){
								remainQuantity=remainQuantity-remainMap.get(invoiceId);
							}
							float starand=invoice.getPrice()*1.02f/(price/6.8f);
							if(remainQuantity>=quantity&&starand>=1&&starand<=40){//发票>报关单金额
								List<PsiSupplierInvoice>  list=tempInvoiceMap.get(invoice.getPrice());
								if(list==null){
									list=Lists.newArrayList();
									tempInvoiceMap.put(invoice.getPrice(), list);
								}
								list.add(invoice);
							}
						}
						++suitInvoice;
					}
					
					
					if(tempInvoiceMap==null||tempInvoiceMap.size()==0){
						tempMap.clear();
						break;
					}
					
					Set<Float> tempPriceSet=tempInvoiceMap.keySet();
					Float minPirce=0f;
					if(tempPriceSet.size()==1){
						for(Float priceSet:tempPriceSet){
							minPirce=priceSet;
						}
					}else{
						Float[] arr = new Float[tempPriceSet.size()];   
						int i=minDifference(tempPriceSet.toArray(arr),price);
						minPirce=arr[i];
					}
					PsiSupplierInvoice invoice=tempInvoiceMap.get(minPirce).get(0);
					tempMap.put(invoice.getId(),quantity+(tempMap.get(invoice.getId())!=null?tempMap.get(invoice.getId()):0));
						
					declare.setArrangeUser(user);
					declare.setInvoice(invoice);
					declare.setArrangeDate(date);
					
				}
				
               
				if(tempMap.size()>0){
					//if((length<=5&&suitInvoice<=1)||(length<=10&&suitInvoice<=2)||(length<=20&&suitInvoice<=3)){
					if(suitInvoice==0||(length<=30&&suitInvoice<=5)){
						declareList.addAll(temp);
						transportSet.add(entry.getKey());
						for (Map.Entry<Integer,Integer> idMap: tempMap.entrySet()) {
							Integer id=idMap.getKey();
							Integer quantiy=(remainMap.get(id)==null?0:remainMap.get(id));
							remainMap.put(id, quantiy+(tempMap.get(id)!=null?tempMap.get(id):0));
						}
					}else{
						for (PsiInvoiceTransportDeclare declare: temp) {
							declare.setArrangeUser(null);
							declare.setInvoice(null);
							declare.setArrangeDate(null);
						}
					}
					
				}else{
					for (PsiInvoiceTransportDeclare declare: temp) {
						declare.setArrangeUser(null);
						declare.setInvoice(null);
						declare.setArrangeDate(null);
					}
				}
			}
			
			
			
			if(declareList.size()>0){
				psiInvoiceService.updateRemainQuantity(declareList,remainMap);
			}
		}
		if(transportSet.size()>0){
			return transportSet.toString()+"成功匹配发票";
		}
		return "";
	}
	
	
	                                                     
	public int minDifference(Float[] data,float price){
        int len=data.length;  
        float[] diff=new float[len];  
        for(int i=0;i<=len-1;i++){ 
            diff[i]=data[i]-price;  
        }  
        return min(diff);  
    }  
	
	
	public int min(float[] diff){ 
		Arrays.sort(diff);
        float min=diff[0]; 
        int index=0;
        if(min<0){
            for(int i=1,len=diff.length;i<len;i++){  
              if(diff[i]>0){
                 break;
              }
          	  if(diff[i]<=0&&diff[i]>min){
          		min=diff[i];  
                index=i;
          	  }
            }
        }
        return index;  
    }  
	
     
	private static SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat dateFormat2=new SimpleDateFormat("yyyy/MM/dd");
	public String getData(Cell cell){
		String value="";
		if(cell!=null){

			switch (cell.getCellType()) {
		        case HSSFCell.CELL_TYPE_NUMERIC: // 数字
		            value = cell.getNumericCellValue() + "";
		            if (HSSFDateUtil.isCellDateFormatted(cell)) {
		                Date date = cell.getDateCellValue();
		                try{
		                	 value = dateFormat.format(date);
		                }catch(Exception e){
		                	 value = dateFormat.format(dateFormat2.format(date));
		                }
		               
		             } else {
		            	 value = new DecimalFormat("0.00").format(cell.getNumericCellValue());
		                 value=value.replace(".00","");
		             }
		            break;
		        case HSSFCell.CELL_TYPE_STRING: // 字符串
		            value = cell.getStringCellValue();
		            break;
		        case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
		            value = cell.getBooleanCellValue() + "";
		            break;
		        case HSSFCell.CELL_TYPE_FORMULA: // 公式
		            value = new DecimalFormat("0.00").format(cell.getNumericCellValue());
	                value=value.replace(".00","");
		            break;
		        case HSSFCell.CELL_TYPE_BLANK: // 空值
		            value = "";
		            break;
		        case HSSFCell.CELL_TYPE_ERROR: // 故障
		            value = "";//非法字符
		            break;
		        default:
		            value = "";//未知类型
		            break;
		        }
		}
		return value;
	}
	/*
	public String getData(Cell cell,FormulaEvaluator evaluator){
		String value="";
		if(cell!=null){

			switch (cell.getCellType()) {
		        case HSSFCell.CELL_TYPE_NUMERIC: // 数字
		            value = cell.getNumericCellValue() + "";
		            if (HSSFDateUtil.isCellDateFormatted(cell)) {
		                Date date = cell.getDateCellValue();
		                try{
		                	 value = dateFormat.format(date);
		                }catch(Exception e){
		                	 value = dateFormat.format(dateFormat2.format(date));
		                }
		               
		             } else {
		            	 value = new DecimalFormat("0.00").format(cell.getNumericCellValue());
		                 value=value.replace(".00","").replace(".0","");
		             }
		            break;
		        case HSSFCell.CELL_TYPE_STRING: // 字符串
		            value = cell.getStringCellValue();
		            break;
		        case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
		            value = cell.getBooleanCellValue() + "";
		            break;
		        case HSSFCell.CELL_TYPE_FORMULA: // 公式
		            //value = cell.getCellFormula() + "";
		            value=getCellValue(evaluator.evaluate(cell));
		        case HSSFCell.CELL_TYPE_BLANK: // 空值
		            value = "";
		            break;
		        case HSSFCell.CELL_TYPE_ERROR: // 故障
		            value = "";//非法字符
		            break;
		        default:
		            value = "";//未知类型
		            break;
		        }
		}
		return value;
	}
	*/
	 private String getCellValue(CellValue cell) {
	        String cellValue = null;
	        switch (cell.getCellType()) {
	        case Cell.CELL_TYPE_STRING:
	            System.out.print("String :");
	            cellValue=cell.getStringValue();
	            break;

	        case Cell.CELL_TYPE_NUMERIC:
	            System.out.print("NUMERIC:");
	            cellValue=String.valueOf(cell.getNumberValue());
	            break;
	        case Cell.CELL_TYPE_FORMULA:
	            System.out.print("FORMULA:");
	            break;
	        default:
	            break;
	        }
	        return cellValue;
	 }
	 
	@RequestMapping(value = { "expDeclare"})
	public String expDeclare(PsiInvoiceTransportDeclare psiInvoiceTransportDeclare, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException {
	
		List<PsiInvoiceTransportDeclare> delcareList=psiInvoiceService.find(psiInvoiceTransportDeclare);
		 Map<String,String> nameMap=psiInvoiceService.findName();
	    int excelNo=1;
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		sheet.setDefaultColumnWidth(18);
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //进货凭证号是发票代码+发票号码
		String[] title = {"出口日期","报关单号21位","发票号码","发票代码","进货凭证号","供货方纳税号","发票开票日期","商品代码","商品名称","下载商品名称","单位","数量","法定单位","法定数量","计税金额","法定征税税率","税额","退税率","可退税额","美元离岸价","美元汇率","人民币离岸价"};
	    style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
	    style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
	    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	  //设置Excel中的边框(表头的边框)
	    HSSFFont font = wb.createFont();
	    font.setFontHeightInPoints((short) 11); // 字体高度
	    style.setFont(font);
	    row.setHeight((short) 400);
	    HSSFCell cell = null;		
	    for(int i = 0; i < title.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
		}
	    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	    for (PsiInvoiceTransportDeclare declare: delcareList) {
	    	 row = sheet.createRow(excelNo++);  //生成行
	    	 int j =0;
	    	 PsiSupplierInvoice invoice=declare.getInvoice();
	    	 String invoiceNo="";
	    	 String invoiceCode="";
	    	 String payerNo="";
	    	 String date="";
	    	 float rate=0f;
	    	 float taxRate=(declare.getTaxRate()==null?0f:declare.getTaxRate());
	    	 BigDecimal totalPrice=new BigDecimal(0);
	    	 BigDecimal ratePrice=new BigDecimal(0);
	    	 BigDecimal taxRatePrice=new BigDecimal(0);
	    	 if(invoice!=null){
	    		invoiceNo=(invoice.getInvoiceNo()==null?"":invoice.getInvoiceNo());
		    	invoiceCode=(invoice.getInvoiceCode()==null?"":invoice.getInvoiceCode());
		    	payerNo=(invoice.getTaxpayerNo()==null?"":invoice.getTaxpayerNo());
		    	date=dateFormat.format(invoice.getInvoiceDate());
		    	rate=(invoice.getRate()==null?0f:invoice.getRate());
		    	totalPrice=new BigDecimal(declare.getQuantity()*invoice.getPrice()).setScale(2,4);
		    	ratePrice=new BigDecimal(declare.getQuantity()*invoice.getPrice()*rate).setScale(2,4);
		    	taxRatePrice=new BigDecimal(declare.getQuantity()*invoice.getPrice()*taxRate).setScale(2,4);
	    	 }
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dateFormat.format(declare.getDeclareDate()));  
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getDeclareCode());  
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(invoiceNo);  
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(invoiceCode);  
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(invoiceCode+invoiceNo);  
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(payerNo);  
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(date);  
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getProductNo());  
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getProductName()); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(nameMap.get(declare.getProductNo())==null?"":nameMap.get(declare.getProductNo())); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getUnit()); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getQuantity()); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getLegalUnit()); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getLegalQuantity()+""); 
	    	 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalPrice+""); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(rate+""); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(ratePrice+""); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(taxRate+""); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(taxRatePrice+""); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getUsdPrice()+""); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getUsdRate()+""); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getCnyPrice()+""); 
		}
	    
	    
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");

		String fileName = "Invoice" + sdf.format(new Date()) + ".xls";
		fileName = URLEncoder.encode(fileName, "UTF-8");
		response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
		try {
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
			  
		return null;
	}
	
	
	@RequestMapping(value = { "expOutputDeclare"})
	public String expOutputDeclare(PsiInvoiceTransportDeclare psiInvoiceTransportDeclare, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException {
	
		List<PsiInvoiceTransportDeclare> delcareList=psiInvoiceService.find2(psiInvoiceTransportDeclare);
		 Map<String,String> nameMap=psiInvoiceService.findName();
	    int excelNo=1;
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		sheet.setDefaultColumnWidth(18);
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        //进货凭证号是发票代码+发票号码
		String[] title = {"出口日期","报关单号","美元离岸价","美元汇率","人民币离岸价","核销单号","代理证明号","商品代码","商品名称","下载商品名称","单位","申报商品代码","申报商品名称","出口数量"};
	    style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
	    style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
	    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	  //设置Excel中的边框(表头的边框)
	    HSSFFont font = wb.createFont();
	    font.setFontHeightInPoints((short) 11); // 字体高度
	    style.setFont(font);
	    row.setHeight((short) 400);
	    HSSFCell cell = null;		
	    for(int i = 0; i < title.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
		}
	    SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
	    for (PsiInvoiceTransportDeclare declare: delcareList) {
	    	 row = sheet.createRow(excelNo++);  //生成行
	    	 int j =0;
	    	 PsiSupplierInvoice invoice=declare.getInvoice();
	    	 String invoiceNo="";
	    	 String invoiceCode="";
	    	 String payerNo="";
	    	 String date="";
	    	 float rate=0f;
	    	 float taxRate=(declare.getTaxRate()==null?0f:declare.getTaxRate());
	    	 BigDecimal totalPrice=new BigDecimal(0);
	    	 BigDecimal ratePrice=new BigDecimal(0);
	    	 BigDecimal taxRatePrice=new BigDecimal(0);
	    	 if(invoice!=null){
	    		invoiceNo=(invoice.getInvoiceNo()==null?"":invoice.getInvoiceNo());
		    	invoiceCode=(invoice.getInvoiceCode()==null?"":invoice.getInvoiceCode());
		    	payerNo=(invoice.getTaxpayerNo()==null?"":invoice.getTaxpayerNo());
		    	date=dateFormat.format(invoice.getInvoiceDate());
		    	rate=(invoice.getRate()==null?0f:invoice.getRate());
		    	totalPrice=new BigDecimal(declare.getQuantity()*invoice.getPrice()).setScale(2,4);
		    	ratePrice=new BigDecimal(declare.getQuantity()*invoice.getPrice()*rate).setScale(2,4);
		    	taxRatePrice=new BigDecimal(declare.getQuantity()*invoice.getPrice()*taxRate).setScale(2,4);
	    	 }
	    	
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dateFormat.format(declare.getDeclareDate()));  
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getDeclareCode());  
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getUsdPrice()+""); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getUsdRate()+""); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getCnyPrice()+""); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(""); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(""); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getProductNo());  
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getProductName()); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(nameMap.get(declare.getProductNo())==null?"":nameMap.get(declare.getProductNo())); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getLegalUnit()); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getProductNo());  
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getProductName()); 
	    	 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(declare.getLegalQuantity()+""); 
	    	 
		}
	    
	    
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");

		String fileName = "出口明细" + sdf.format(new Date()) + ".xls";
		fileName = URLEncoder.encode(fileName, "UTF-8");
		response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
		try {
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
			  
		return null;
	}
	
	
	@RequestMapping(value = "deleteInvoice")
	@ResponseBody
	public String deleteInvoice(String delIds) {
		String[] idArr=delIds.split(",");
		psiInvoiceService.updateDeleteState(Sets.newHashSet(idArr));
		return "0";
	}
	
	@RequestMapping(value = "updateInvoiceDate")
	@ResponseBody
	public String updateInvoiceDate(String delIds) {
		String[] idArr=delIds.split(",");
		psiInvoiceService.updateInvoiceDate(Sets.newHashSet(idArr));
		return "0";
	}
	
	@RequestMapping(value = "deleteDeclare")
	@ResponseBody
	public String deleteDeclare(String delIds) {
		String[] idArr=delIds.split(",");
		psiInvoiceService.updateDeclareDeleteState(Sets.newHashSet(idArr));
		return "0";
	}
	
	@RequestMapping(value = "updateTaxRate")
	@ResponseBody
	public String updateTaxRate() {
		psiInvoiceService.updateTaxRate();
		return "0";
	}
	
	
	
	@RequestMapping(value = {"editDeclareInfo"})
	public String editDeclareInfo(PsiInvoiceTransportDeclare psiInvoiceTransportDeclare, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
        model.addAttribute("declare",psiInvoiceService.getDeclare(psiInvoiceTransportDeclare.getId()));
		return "modules/psi/psiDeclareForm";
	}
	
	
	@RequestMapping(value = {"editInvoiceInfo"})
	public String editInvoiceInfo(PsiSupplierInvoice psiSupplierInvoice, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
        model.addAttribute("psiSupplierInvoice",psiInvoiceService.get(psiSupplierInvoice.getId()));
		return "modules/psi/psiInvoiceForm";
	}
	
	
	@RequestMapping(value = {"saveInvoice"})
	public String saveDeclare(PsiSupplierInvoice psiSupplierInvoice){
		PsiSupplierInvoice old=psiInvoiceService.get(psiSupplierInvoice.getId());
		old.setInvoiceCode(psiSupplierInvoice.getInvoiceCode());
		old.setInvoiceNo(psiSupplierInvoice.getInvoiceNo());
		old.setCompanyName(psiSupplierInvoice.getCompanyName());
        old.setTaxpayerNo(psiSupplierInvoice.getTaxpayerNo());
        old.setProductName(psiSupplierInvoice.getProductName());
        old.setModel(psiSupplierInvoice.getModel());
        old.setUnit(psiSupplierInvoice.getUnit());
        old.setQuantity(psiSupplierInvoice.getQuantity());
        old.setTotalPrice(psiSupplierInvoice.getTotalPrice());
        old.setPrice(psiSupplierInvoice.getTotalPrice()/psiSupplierInvoice.getQuantity());
        old.setRate(psiSupplierInvoice.getRate());
        old.setState(psiSupplierInvoice.getState());
        old.setRemainingQuantity(psiSupplierInvoice.getRemainingQuantity());
        old.setCreateDate(new Date());
        old.setCreateUser(UserUtils.getUser());
		psiInvoiceService.save(old);
		return "redirect:"+Global.getAdminPath()+"/psi/psiInvoice/";
	}
			

	@RequestMapping(value = {"saveDeclare"})
	public String saveDeclare(PsiInvoiceTransportDeclare psiInvoiceTransportDeclare){
		psiInvoiceTransportDeclare.setCreateDate(new Date());
		psiInvoiceTransportDeclare.setCreateUser(UserUtils.getUser());
		psiInvoiceTransportDeclare.setDelFlag("0");
		psiInvoiceService.saveDeclare(psiInvoiceTransportDeclare);
		return "redirect:"+Global.getAdminPath()+"/psi/psiInvoice/declareList";
	}
	
	@RequestMapping(value = {"deleteAll"})
	@ResponseBody
	public String deleteAll(){
		psiInvoiceService.deleteAll();
		return "0";
	}	
	//2017-11-02 08:03:54  2017-11-01 14:03:54    2017-11-02T01:18:45.482Z
	//11/1/17 5:03:55 PM PDT  2017-11-02T01:18:45.288Z
	//11/1/17 12:03:55
}
