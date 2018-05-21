/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web.parts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiLadingBill;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsDelivery;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsDeliveryDto;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsDeliveryItem;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsOrderItem;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.parts.PsiPartsDeliveryService;
import com.springrain.erp.modules.psi.service.parts.PsiPartsOrderItemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 配件收货付款详情Controller
 * @author Michael
 * @version 2015-07-03
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiPartsDelivery")
public class PsiPartsDeliveryController extends BaseController {

	@Autowired
	private PsiPartsDeliveryService psiPartsDeliveryService;
	@Autowired
	private PsiPartsOrderItemService psiPartsOrderItemService;
	@Autowired
	private PsiSupplierService psiSupplierService;
	
	
	@RequestMapping(value = {"list", ""})
	public String list(PsiPartsDelivery psiPartsDelivery, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date today = sdf.parse(sdf.format(new Date()));
		if(psiPartsDelivery.getCreateDate() == null) {
			psiPartsDelivery.setCreateDate(DateUtils.addMonths(today, -1));
		}
		if(psiPartsDelivery.getUpdateDate()==null){
			psiPartsDelivery.setUpdateDate(today);
		}
        Page<PsiPartsDelivery> page = psiPartsDeliveryService.find(new Page<PsiPartsDelivery>(request, response), psiPartsDelivery); 
        //配件供应商
        List<PsiSupplier> suppliers=this.psiSupplierService.findSupplierByType(new String[]{"0","2"});
		model.addAttribute("suppliers", suppliers);
        model.addAttribute("psiPartsDelivery", psiPartsDelivery);
        model.addAttribute("page", page);
		return "modules/psi/parts/psiPartsDeliveryList";
	}
  

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "sure")
	public String sure(PsiPartsDelivery partsDelivery, Model model) {
		Map<String,List<Object[]>> ladingMap = new HashMap<String, List<Object[]>>();
		partsDelivery=this.psiPartsDeliveryService.get(partsDelivery.getId());
		for(PsiPartsDeliveryItem item :partsDelivery.getItems()){
			//组合map
			String key = item.getPartsName();
			List<Object[]> itemList = new ArrayList<Object[]>();
			Object[] object = {item.getQuantityLading(),item.getRemark(),item.getId(),item.getPartsOrderItem().getId(),item.getPartsOrderItem().getPartsOrder().getId(),item.getPartsOrderItem().getPartsOrder().getPartsOrderNo()};
			if(ladingMap.containsKey(key)){
				itemList=ladingMap.get(key);
			}
			itemList.add(object);
			ladingMap.put(key, itemList);
		}
		model.addAttribute("ladingMap",JSON.toJSON(ladingMap));
		model.addAttribute("partsDelivery", partsDelivery);
		return "modules/psi/parts/psiPartsDeliverySure";
	}
	
	@RequestMapping(value = "sureSave")
	public String sureSave(@RequestParam("attchmentFile")MultipartFile[] attchmentFiles,PsiPartsDelivery psiPartsDelivery, Model model, RedirectAttributes redirectAttributes) {
		synchronized (this) {
			psiPartsDelivery=this.psiPartsDeliveryService.get(psiPartsDelivery.getId());
			//遍历ladingItem里的数据      更新orderItem里的       收货数       预收货数
			psiPartsDeliveryService.sureSave(psiPartsDelivery, attchmentFiles);
		}
		addMessage(redirectAttributes, "确认配件收货单'" + psiPartsDelivery.getBillNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiPartsDelivery/?repage";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "upload")
	public String upload(PsiPartsDelivery partsDelivery, Model model) {
		Map<String,List<Object[]>> ladingMap = new HashMap<String, List<Object[]>>();
		partsDelivery=this.psiPartsDeliveryService.get(partsDelivery.getId());
		for(PsiPartsDeliveryItem item :partsDelivery.getItems()){
			//组合map
			String key = item.getPartsName();
			List<Object[]> itemList = new ArrayList<Object[]>();
			Object[] object = {item.getQuantityLading(),item.getRemark(),item.getId(),item.getPartsOrderItem().getId(),item.getPartsOrderItem().getPartsOrder().getId(),item.getPartsOrderItem().getPartsOrder().getPartsOrderNo()};
			if(ladingMap.containsKey(key)){
				itemList=ladingMap.get(key);
			}
			itemList.add(object);
			ladingMap.put(key, itemList);
		}
		model.addAttribute("ladingMap",JSON.toJSON(ladingMap));
		model.addAttribute("partsDelivery", partsDelivery);
		return "modules/psi/parts/psiPartsDeliveryUp";
	}
	
	@RequestMapping(value = "uploadSave")
	public String uploadSave(@RequestParam("attchmentFile")MultipartFile[] attchmentFiles,Integer id, Model model, RedirectAttributes redirectAttributes) {
			PsiPartsDelivery psiPartsDelivery=this.psiPartsDeliveryService.get(id);
			//遍历ladingItem里的数据      更新orderItem里的       收货数       预收货数
			for (MultipartFile attchmentFile : attchmentFiles) {
				if(attchmentFile.getSize()!=0){
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/partsDelivery";
					File baseDir = new File(baseDirStr+"/"+psiPartsDelivery.getBillNo()); 
					if(!baseDir.isDirectory())
						baseDir.mkdirs();
					String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
					String name=UUID.randomUUID().toString()+suffix;
					File dest = new File(baseDir,name);
					try {
						FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
						psiPartsDelivery.setAttchmentPathAppend("/psi/partsDelivery/"+psiPartsDelivery.getBillNo()+"/"+name);
					} catch (IOException e) {
						logger.warn(name+"文件保存失败",e);
					}
				}
			}
			this.psiPartsDeliveryService.save(psiPartsDelivery);
		addMessage(redirectAttributes, "确认配件收货单'" + psiPartsDelivery.getBillNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiPartsDelivery/?repage";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "cancel")
	public String cancel(Integer id, RedirectAttributes redirectAttributes) {
		PsiPartsDelivery bill=this.psiPartsDeliveryService.get(id);
		if(this.psiPartsDeliveryService.cancelBill(bill)){
			bill.setBillSta("2");//已取消
			bill.setCancelDate(new Date());
			bill.setCancelUser(UserUtils.getUser());
			this.psiPartsDeliveryService.save(bill);
		};
		
		addMessage(redirectAttributes, "取消配件收货单成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiPartsDelivery/?repage";
	}
	
	
	@RequestMapping(value = "save")
	public String batchSave(PsiPartsDelivery psiPartsDelivery, Model model, RedirectAttributes redirectAttributes) {
		if(psiPartsDelivery.getItems()!=null&&psiPartsDelivery.getItems().size()>0){
			 psiPartsDeliveryService.batchSave(psiPartsDelivery);
			 addMessage(redirectAttributes,"保存配件收货单'" +psiPartsDelivery.getBillNo() + "'成功");
		}else{
			addMessage(redirectAttributes, "配件收货单生成失败，配件明细项为空");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiPartsDelivery/?repage";
	}
	

	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "batchReceive")
	public String batchReceive(String orderItemIds,Integer supplierId,String currencyType,Model model) {
		PsiLadingBill psiLadingBill = new PsiLadingBill();
		psiLadingBill.setCurrencyType(currencyType);
		
		List<PsiPartsOrderItem> orderItems =psiPartsOrderItemService.getOrderItems(orderItemIds);
		Map<String,List<PsiPartsDeliveryDto>> ladingMap = Maps.newHashMap();
		//这里多生成一个map因为totalMap里面的数量和批量这里的可提单总数不同，这里只是选中的几个的总和
		Map<String,Integer> batchTotalMap = new HashMap<String, Integer>();
		for(PsiPartsOrderItem orderItem:orderItems){
			//组合map
			String partsName = orderItem.getPartsName();
			Integer partsId  = orderItem.getPsiParts().getId();
			List<PsiPartsDeliveryDto> itemList = Lists.newArrayList();
			Integer ladingQuantity = orderItem.getQuantityOrdered()-orderItem.getQuantityReceived()-orderItem.getQuantityPreReceived();
			if(ladingQuantity>0){
				PsiPartsDeliveryDto deliveryDto = new PsiPartsDeliveryDto(partsId, partsName, ladingQuantity, orderItem.getPartsOrder().getId(), orderItem.getPartsOrder().getPartsOrderNo(), orderItem.getId(), orderItem.getItemPrice());
				if(ladingMap.containsKey(partsName)){
					itemList=ladingMap.get(partsName);
				}
				itemList.add(deliveryDto);
				ladingMap.put(partsName, itemList);
				Integer canQuantity = 0;
				if(batchTotalMap.containsKey(partsName)){
					canQuantity=batchTotalMap.get(partsName)+ladingQuantity;
				}else{
					canQuantity=ladingQuantity;
				}
				batchTotalMap.put(partsName, canQuantity);
			}
		}
		
		
		
		List<String>  products = new ArrayList<String>();
		List<PsiSupplier> suppliers=this.psiSupplierService.findSupplierByType(new String[]{"0","2"});
		Map<String,List<PsiPartsDeliveryDto>> productMap = Maps.newHashMap();
		Map<String,Integer> totalMap = new HashMap<String, Integer>();
		//查出该供应商可提单产品  所在订单编号    数量
		List<PsiPartsDeliveryDto> list = this.psiPartsDeliveryService.getProductLading(supplierId,currencyType);
		
		if(list!=null&&list.size()>0){
			for(PsiPartsDeliveryDto deliveryDto:list){
				List<PsiPartsDeliveryDto> deliveryDtos = Lists.newArrayList();
				Integer canLadingQuantity = 0;
				String partsName=deliveryDto.getPartsName();
				if(productMap.containsKey(partsName)){
					deliveryDtos=productMap.get(partsName);
					Integer  total = totalMap.get(partsName);
					canLadingQuantity=total+deliveryDto.getCanLadingQuantity();
				}else{
					canLadingQuantity=deliveryDto.getCanLadingQuantity();
				}
				totalMap.put(partsName, canLadingQuantity);
				deliveryDtos.add(deliveryDto);
				productMap.put(partsName, deliveryDtos);
			}
		}
		
		products.addAll(productMap.keySet());
		PsiSupplier supplier = new PsiSupplier();
		supplier.setId(supplierId);
		psiLadingBill.setSupplier(supplier);
		
		//获取nikeName
		String nikeName="";
		for(PsiSupplier sup:suppliers){
			if(sup.getId().equals(supplierId)){
				nikeName=sup.getNikename();
				break;
			}
		}
		String billNo = this.psiPartsDeliveryService.createSequenceNumber(nikeName+"_PJTDH");
		psiLadingBill.setBillNo(billNo);
		
		//页面放两个map
		model.addAttribute("productMap",JSON.toJSON(productMap));
		model.addAttribute("totalMap",JSON.toJSON(totalMap));
		model.addAttribute("ladingMap",JSON.toJSON(ladingMap));
		model.addAttribute("batchTotalMap",JSON.toJSON(batchTotalMap));
		model.addAttribute("products",products);
		model.addAttribute("suppliers", suppliers);
		model.addAttribute("psiLadingBill", psiLadingBill);
		return "modules/psi/parts/psiPartsDeliveryBatch";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "view")
	public String view(PsiPartsDelivery partsDelivery, Model model) {
		Map<String,List<Object[]>> ladingMap = new HashMap<String, List<Object[]>>();
		partsDelivery=this.psiPartsDeliveryService.get(partsDelivery.getId());
		for(PsiPartsDeliveryItem item :partsDelivery.getItems()){
			//组合map
			String key = item.getPartsName();
			List<Object[]> itemList = new ArrayList<Object[]>();
			Object[] object = {item.getQuantityLading(),item.getRemark(),item.getId(),item.getPartsOrderItem().getId(),item.getPartsOrderItem().getPartsOrder().getId(),item.getPartsOrderItem().getPartsOrder().getPartsOrderNo()};
			if(ladingMap.containsKey(key)){
				itemList=ladingMap.get(key);
			}
			itemList.add(object);
			ladingMap.put(key, itemList);
		}
		
		model.addAttribute("ladingMap",JSON.toJSON(ladingMap));
		model.addAttribute("partsDelivery", partsDelivery);
		return "modules/psi/parts/psiPartsDeliveryView";
	}
	
	@RequiresPermissions("psi:all:view")
	@ResponseBody
	@RequestMapping(value = "print")
	public String print(PsiPartsDelivery partsDelivery,HttpServletResponse response) throws Exception {
		partsDelivery = this.psiPartsDeliveryService.get(partsDelivery.getId());
		String	filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/partsDelivery";
		File file = new File(filePath, partsDelivery.getBillNo());
		if (!file.exists()) {
			file.mkdirs();
		}
		File pdfFile = new File(file, partsDelivery.getBillNo() + ".pdf");
		PdfUtil.genPsiPartsDeliveryPdf(pdfFile,partsDelivery);
		FileInputStream in = new FileInputStream(pdfFile);
		ServletOutputStream out = response.getOutputStream();
		response.setContentType("application/pdf");// pdf文件
		response.addHeader("Content-Disposition", "filename="+ partsDelivery.getBillNo()+".pdf");
		byte data[] = new byte[1024];
		int len;
		while ((len = in.read(data)) != -1) {
			out.write(data, 0, len);
		}
		out.flush();
		in.close();
		out.close();
		return null;
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "exp")
	public String exp(PsiPartsDelivery partsDelivery, Model model, HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
		
		Page<PsiPartsDelivery> page  =new Page<PsiPartsDelivery>(request, response);
		page.setPageSize(600000);
		List<PsiPartsDelivery> list = this.psiPartsDeliveryService.find(page,partsDelivery).getList(); 
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		String[] title = { " 配件收货单号  ","   收货单状态     ","  配件订单号  "," 配件型号  " ,"  收货数量   ","  操作人  "};
	    style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
	    style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
	    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    //设置字体
	    HSSFFont font = wb.createFont();
	    font.setFontHeightInPoints((short) 11); // 字体高度
	    style.setFont(font);
	    row.setHeight((short) 400);
	    HSSFCell cell = null;		
	    for (int i = 0; i < title.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
		}
	    
		int j =1;
		for(PsiPartsDelivery delivery:list){
			
			for(PsiPartsDeliveryItem item:delivery.getItems()){
				int i =0;
				row = sheet.createRow(j++);
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(delivery.getBillNo()); 
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(delivery.getStaName()); 
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getPartsOrderItem().getPartsOrder().getPartsOrderNo()); 
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getPartsName()); 
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getQuantityLading()); 
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(delivery.getCreateUser().getName()); 
			 }
		}
		
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		String fileName = "partsDeliverys" + sdf.format(new Date()) + ".xls";
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
	
}
