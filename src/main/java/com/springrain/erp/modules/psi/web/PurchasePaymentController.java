/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.Collections3;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PurchaseAmountAdjust;
import com.springrain.erp.modules.psi.entity.PurchasePayment;
import com.springrain.erp.modules.psi.entity.PurchasePaymentItem;
import com.springrain.erp.modules.psi.entity.PurchasePaymentItemDto;
import com.springrain.erp.modules.psi.service.PsiLadingBillService;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.PurchaseAmountAdjustService;
import com.springrain.erp.modules.psi.service.PurchaseOrderItemService;
import com.springrain.erp.modules.psi.service.PurchaseOrderService;
import com.springrain.erp.modules.psi.service.PurchasePaymentService;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 采购付款Controller
 * @author Michael
 * @version 2014-11-21
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/purchasePayment")
public class PurchasePaymentController extends BaseController {
	@Autowired
	private PurchasePaymentService		 purchasePaymentService;
	@Autowired
	private PsiLadingBillService         psiLadingBillService;
	@Autowired
	private PsiSupplierService           psiSupplierService;
	@Autowired
	private PurchaseAmountAdjustService  adjustService;
	
	private static String filePath;
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"list", ""})
	public String list(PurchasePayment purchasePayment, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (purchasePayment.getCreateDate() == null) {
			purchasePayment.setCreateDate(DateUtils.addMonths(today, -1));
			purchasePayment.setUpdateDate(today);
		}
		Page<PurchasePayment> page = new Page<PurchasePayment>(request, response);
        page = purchasePaymentService.find(page, purchasePayment); 
        List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
		model.addAttribute("suppliers", suppliers);
        model.addAttribute("page", page);
		return "modules/psi/purchasePaymentList";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "add")
	public String add(PurchasePayment purchasePayment, Model model) {
		List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
//		Map<String, Object[]>  orderMaps= Maps.newLinkedHashMap();
//		Map<String, Object[]>  ladingMaps=Maps.newLinkedHashMap();
		Map<String, String> accountMaps= null;
		Integer supplierId =0;
		PsiSupplier supplier= null;
		if(purchasePayment.getSupplier()==null){
			if(suppliers!=null&&suppliers.size()>0){
				supplier=suppliers.get(0);
				supplierId=supplier.getId();
			}
		}else{
			supplierId=purchasePayment.getSupplier().getId();
			for (PsiSupplier psiSupplier : suppliers) {
				if(psiSupplier.getId().equals(supplierId)){
					supplier=psiSupplier;
					break;
				}
			}
		}
		
		List<PurchasePaymentItemDto> ladingItemDtos =Lists.newArrayList();
		Set<String> months = Sets.newHashSet();
		Set<String> billNos = Sets.newHashSet();
		Set<Integer> rates = Sets.newHashSet();
		//查出未付款的订单信息
		List<Object[]> orders  = this.purchasePaymentService.getUnPaymentOrder(supplierId,null);
		for(Object[] object:orders){
			months.add(object[0].toString().substring(0, 6));
		}
		//账号信息
		if(supplier!=null){
			accountMaps= supplier.getAccountMap();
		}
		//查出未付完款的提单项信息
		ladingItemDtos = this.purchasePaymentService.getUnPaymentLadingItem(supplierId, null,false);
		for(PurchasePaymentItemDto dto: ladingItemDtos){
			months.add(dto.getBillNo().substring(0, 6));
			billNos.add(dto.getBillNo());
			rates.add(dto.getRate());
		}
	
		//根据supplierid查询要付的额外付款项
		List<PurchaseAmountAdjust> adjusts= null;
		if(supplier!=null){
			adjusts= adjustService.findAdjustOrders(supplier.getId(),null,"0",null);
		}
		
		model.addAttribute("ladingItemDtos", ladingItemDtos);
		model.addAttribute("orders", orders);
		model.addAttribute("months", months);
		model.addAttribute("billNos", billNos);
		model.addAttribute("rates", rates);
		model.addAttribute("accountMaps", accountMaps);
		model.addAttribute("purchasePayment", purchasePayment);
		model.addAttribute("adjusts", adjusts);
		model.addAttribute("suppliers", suppliers);
		return "modules/psi/purchasePaymentAdd";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "addSave")
	public String addSave(PurchasePayment purchasePayment, Model model, RedirectAttributes redirectAttributes) throws Exception {
		if(this.purchasePaymentService.addSave(purchasePayment)){
			addMessage(redirectAttributes, "保存采购付款'" + purchasePayment.getPaymentNo() + "'成功");
		}else{
			addMessage(redirectAttributes, "申请邮件发送失败，请重新申请!!");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/purchasePayment/?repage";
	}
	
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "edit")
	public String edit(PurchasePayment purchasePayment, Model model) {
		purchasePayment =this.purchasePaymentService.get(purchasePayment.getId());
		StringBuilder sb = new StringBuilder("");
		for(PurchasePaymentItem item:purchasePayment.getItems()){
			sb.append(item.getId()+",");
		}
		String itemIds="";
		if(StringUtils.isNotEmpty(sb.toString())){
			itemIds=sb.toString().substring(0,sb.toString().length()-1);
		}
		purchasePayment.setOldItemIds(itemIds);
		
		
		HashMap<String, Object[]>  orderMaps= new HashMap<String, Object[]>();
		HashMap<String, Object[]>  ladingMaps= new HashMap<String, Object[]>();
		PsiSupplier  supplier= purchasePayment.getSupplier();
		//查出未付款的订单信息
		List<Object[]> orders  = this.purchasePaymentService.getUnPaymentOrder(supplier.getId(),purchasePayment.getCurrencyType());
		for(Object[] object:orders){
			orderMaps.put(object[0].toString(), object);
		}
		//账号信息
		Map<String, String> accountMaps= purchasePayment.getSupplier().getAccountMap();
		
		//查出未付完款的提单项信息
		List<Object[]> ladings = this.purchasePaymentService.getUnPaymentLading(supplier.getId(),purchasePayment.getCurrencyType());
		for(Object[] object:ladings){
			ladingMaps.put(object[0].toString(), object);
		}
		
		//根据supplierid查询要付的额外付款项
		List<PurchaseAmountAdjust> adjusts= adjustService.findAdjustOrders(supplier.getId(),null,"0",purchasePayment.getCurrencyType());  
		model.addAttribute("orderSet", orderMaps.keySet());
		model.addAttribute("ladingSet",ladingMaps.keySet());
		model.addAttribute("orderKeys", JSON.toJSON(orderMaps.keySet()));
		model.addAttribute("ladingKeys", JSON.toJSON(ladingMaps.keySet()));
		model.addAttribute("accountMaps", accountMaps);
		model.addAttribute("orderMaps", JSON.toJSON(orderMaps));
		model.addAttribute("ladingMaps", JSON.toJSON(ladingMaps));
		model.addAttribute("purchasePayment", purchasePayment);
		model.addAttribute("adjusts", adjusts);
		return "modules/psi/purchasePaymentEdit";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "view")
	public String view(PurchasePayment purchasePayment, Model model) {
		purchasePayment =this.purchasePaymentService.get(purchasePayment.getId());
		List<PurchaseAmountAdjust> adjusts= adjustService.findAdjustOrders(purchasePayment.getSupplier().getId(),purchasePayment.getId(),null,purchasePayment.getCurrencyType());
		model.addAttribute("purchasePayment", purchasePayment);
		model.addAttribute("adjusts", adjusts);
		return "modules/psi/purchasePaymentView";
	}
	
	
	
//	@RequiresPermissions("psi:all:view")
//	@RequestMapping(value = "editSave")
//	public String editSave(PurchasePayment purchasePayment, Model model, RedirectAttributes redirectAttributes) {
//		//遍历item
//		if(purchasePayment.getItems()!=null){
//			if(this.purchasePaymentService.editSave(purchasePayment)){
//				addMessage(redirectAttributes, "保存采购付款'" + purchasePayment.getPaymentNo() + "'成功");
//			}else{
//				return "redirect:"+Global.getAdminPath()+"/psi/purchasePayment/?repage";
//			}
//		}else{
//			addMessage(redirectAttributes, "保存采购付款失败，付款项为空");
//		}
//		return "redirect:"+Global.getAdminPath()+"/psi/purchasePayment/?repage";
//	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "sure")
	public String sure(PurchasePayment purchasePayment, Model model) {
		purchasePayment =this.purchasePaymentService.get(purchasePayment.getId());
//		Map<String, Object[]>  orderMaps= Maps.newLinkedHashMap();
//		Map<String, Object[]>  ladingMaps=Maps.newLinkedHashMap();
//		for(PurchasePaymentItem item:purchasePayment.getItems()){
//			//区分是提单还是订单
//			if(item.getPaymentType()!=null&&!"".equals(item.getPaymentType())){
//				if("0".equals(item.getPaymentType())){
//					Object[] object={item.getOrder().getOrderNo(),item.getOrder().getDepositPreAmount()};
//					orderMaps.put(item.getOrder().getOrderNo(), object);
//				}else if("1".equals(item.getPaymentType())){
//					Object[] object={item.getLadingBill().getBillNo(),item.getLadingBill().getTotalAmount()};
//					ladingMaps.put(item.getLadingBill().getBillNo(), object);
//				}
//			}
//			
//		}
		//根据supplierid查询要付的额外付款项
		List<PurchaseAmountAdjust> adjusts= adjustService.findAdjustOrders(purchasePayment.getSupplier().getId(),purchasePayment.getId(),"1",purchasePayment.getCurrencyType());
		//账号信息
		Map<String, String> accountMaps= purchasePayment.getSupplier().getAccountMap();
		model.addAttribute("accountMaps", JSON.toJSON(accountMaps));
//		model.addAttribute("orderMaps", JSON.toJSON(orderMaps));
//		model.addAttribute("ladingMaps", JSON.toJSON(ladingMaps));
		model.addAttribute("adjusts", adjusts);
		model.addAttribute("purchasePayment", purchasePayment);
		return "modules/psi/purchasePaymentSure";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "sureSave")
	public String sureSave(MultipartFile memoFile,PurchasePayment purchasePayment, Model model, RedirectAttributes redirectAttributes) {
		if(!"1".equals(purchasePayment.getPaymentSta())){
			return null;
		}
		Float curPaymentAmount = purchasePayment.getCurPaymentAmount();
		purchasePayment=this.purchasePaymentService.get(purchasePayment.getId());
		//保存凭证
		if(memoFile!=null&&memoFile.getSize()!=0){
			if (filePath == null) {
				filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/purchasePayment";
			}
			File baseDir = new File(filePath+"/"+purchasePayment.getPaymentNo()); 
			if(!baseDir.isDirectory())
				baseDir.mkdirs();
			String suffix = memoFile.getOriginalFilename().substring(memoFile.getOriginalFilename().lastIndexOf("."));     
			String name=UUID.randomUUID().toString()+suffix;
			File dest = new File(baseDir,name);
			try {
				FileUtils.copyInputStreamToFile(memoFile.getInputStream(),dest);
				purchasePayment.setAttchmentPath("/psi/purchasePayment/"+purchasePayment.getPaymentNo()+"/"+name);
			} catch (IOException e) {
				logger.warn(name+"文件保存失败",e);
			}
		}
		  
		//如果这次不等
		purchasePayment.setRealPaymentAmount(purchasePayment.getRealPaymentAmount()+curPaymentAmount);
		if(Float.floatToIntBits(purchasePayment.getPaymentAmountTotal())!=Float.floatToIntBits(purchasePayment.getRealPaymentAmount())){
			this.purchasePaymentService.save(purchasePayment);
		}else{
			this.purchasePaymentService.sureSave(purchasePayment);
		}
		
		if(purchasePayment.getItems()!=null){
			addMessage(redirectAttributes, "确认采购付款'" + purchasePayment.getPaymentNo() + "'成功");
		}else{
			addMessage(redirectAttributes, "确认采购付款失败，付款项为空");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/purchasePayment/?repage";
		
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping("/download")   
    public ModelAndView download(String fileName, HttpServletRequest request, HttpServletResponse response)   
            throws Exception {   
		fileName = HtmlUtils.htmlUnescape(fileName);
        response.setContentType("text/html;charset=utf-8");   
        request.setCharacterEncoding("UTF-8");   
        java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null;   
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir();  
        String downLoadPath = ctxPath + fileName;
        fileName = fileName.substring(fileName.lastIndexOf("/")+1);
        try {   
            long fileLength = new File(downLoadPath).length();   
            response.setContentType("application/x-msdownload;");   
            response.setHeader("Content-disposition", "attachment; filename="  
                    + URLEncoder.encode(fileName,"utf-8"));   
            response.setHeader("Content-Length", String.valueOf(fileLength));   
            bis = new BufferedInputStream(new FileInputStream(downLoadPath));   
            bos = new BufferedOutputStream(response.getOutputStream());   
            byte[] buff = new byte[2048];   
            int bytesRead;   
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {   
                bos.write(buff, 0, bytesRead);   
            }   
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            if (bis != null)   
                bis.close();   
            if (bos != null)   
                bos.close();   
        }   
        return null;   
    }   

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "form")
	public String form(PurchasePayment purchasePayment, Model model) {
		model.addAttribute("purchasePayment", purchasePayment);
		return "modules/psi/purchasePaymentForm";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "save")
	public String save(PurchasePayment purchasePayment, Model model, RedirectAttributes redirectAttributes) {
		purchasePaymentService.save(purchasePayment);
		addMessage(redirectAttributes, "保存采购付款'" + purchasePayment.getPaymentNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/purchasePayment/?repage";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "cancel")
	public String cancel(PurchasePayment purchasePayment, Model model, RedirectAttributes redirectAttributes) {
		purchasePayment=this.purchasePaymentService.get(purchasePayment.getId());
		if("3".equals(purchasePayment.getPaymentSta())){
			addMessage(redirectAttributes, "error：不能多次取消'" + purchasePayment.getPaymentNo());
			return "redirect:"+Global.getAdminPath()+"/psi/purchasePayment/?repage";
		}
		//只有申请和已确认状态，没有草稿状态
		this.purchasePaymentService.cancelPurchasePayment(purchasePayment);
		//付款取消通知
		String toAddress = "emma.chao@inateck.com,maik@inateck.com,sophie@inateck.com,"+UserUtils.getUser().getEmail();
		String content = "Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;春雨付款单取消，请点击：<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/purchasePayment/view?id="+purchasePayment.getId()+"'>"+purchasePayment.getPaymentNo()+"</a>查看";
		this.purchasePaymentService.sendNoticeEmail(toAddress, content, "春雨付款单取消["+purchasePayment.getPaymentNo()+"]", purchasePayment.getCreateUser().getEmail(), "");
		addMessage(redirectAttributes, "取消采购付款'" + purchasePayment.getPaymentNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/purchasePayment/?repage";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		purchasePaymentService.delete(id);
		addMessage(redirectAttributes, "删除采购付款成功");
		return "redirect:"+Global.getAdminPath()+"/psi/purchasePayment/?repage";
	}
	
	@ResponseBody
	@RequestMapping(value = {"ajaxOrderAjust"})
	public String ajaxOrderAjust(String typeAndIds) {
		//提单set
		Set<Integer> orderIds=Sets.newHashSet();
		Set<Integer> ladingIds=Sets.newHashSet();
		if(StringUtils.isNotEmpty(typeAndIds)){
			for(String typeAndId:typeAndIds.split(",")){
				String arr[] = typeAndId.split(":");
				if("1".equals(arr[0])){
					ladingIds.add(Integer.parseInt(arr[1]));
				}else{
					orderIds.add(Integer.parseInt(arr[1]));
				}
			}
		}
		
		if(ladingIds.size()>0){
			//查出提单对应的订单
			List<Integer> tempOrderIds =this.psiLadingBillService.getOrderIdByBillId(ladingIds);
			if(tempOrderIds!=null&&tempOrderIds.size()>0){
				orderIds.addAll(tempOrderIds);
			}
		}
		
		//查询采购订单调整表里的信息
		List<PurchaseAmountAdjust> adjusts= adjustService.findAdjustOrders(orderIds);
		StringBuilder items= new StringBuilder("[");
		if(adjusts!=null&&adjusts.size()>0){
			for(PurchaseAmountAdjust adjust:adjusts){
				items.append("{\"id\":\"").append(adjust.getId()).append("\",\"orderNo\":\"").append(adjust.getSubject()).append("\",\"amount\":\"").append(adjust.getAdjustAmount())
				.append("\",\"remark\":\"").append(adjust.getRemark()).append("\"},");
			}
			items= new StringBuilder(items.substring(0, items.length()-1));
		}
		items.append("]");
		
		String rs="{\"items\":"+items.toString()+"}";
		return rs;
	}
	
	
	//导出仓库数据报表
		@RequiresPermissions("psi:all:view")
		@RequestMapping(value =  "expPayment" )
		public String expPayment(Integer supplierId,HttpServletRequest request, HttpServletResponse response, Model model) throws UnsupportedEncodingException{
			//查询barcode表，获得所有产品及市场
			
			//查出未付款的订单信息
			List<Object[]> orders  = this.purchasePaymentService.getUnPaymentOrder(supplierId,null);
			//查出未付完款的提单项信息
			List<PurchasePaymentItemDto> ladingItemDtos = this.purchasePaymentService.getUnPaymentLadingItem(supplierId, null,true);
			
			 HSSFWorkbook wb = new HSSFWorkbook();
				HSSFSheet sheet = wb.createSheet();
				HSSFRow row = sheet.createRow(0);
				HSSFCellStyle style = wb.createCellStyle();
				style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
				
				String[] title = {" 付款类型 ","逾期天数"," 单号 "," 月份 ", " 产品名 ", " 国家  "," 数量  "," 单价 "," 币种 "," 定金比例(%) " ," 尾款档位 (%)","总金额","已付金额","未付金额"};
				style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
				style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
				style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
				HSSFFont font = wb.createFont();
				font.setFontHeightInPoints((short) 11); // 字体高度
				style.setFont(font);
				
				row.setHeight((short) 400);
				HSSFCell cell = null;		
				for (int i = 0; i < title.length; i++) {
					cell = row.createCell(i);
					cell.setCellValue(title[i]);
					cell.setCellStyle(style);
					sheet.autoSizeColumn((short) i);
				}
				
				HSSFCellStyle style1 = wb.createCellStyle();
				HSSFDataFormat df1 = wb.createDataFormat();  //此处设置数据格式  
				style1.setDataFormat(df1.getFormat("#0.00")); //小数点后保留两位，可以写contentStyle.setDataFormat(df.getFormat("#,#0.00"));  
				  
				DecimalFormat  df = new DecimalFormat("0.##");
				int j =1;
				for(Object[] object:orders){
					row = sheet.createRow(j++);
					int i =0;
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("定金");
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(object[0].toString());
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(object[0].toString().substring(0, 6));
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
					cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
					cell.setCellStyle(style1);
					cell.setCellValue(Float.parseFloat(object[3].toString()));
					
					cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
					cell.setCellStyle(style1);
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(Float.parseFloat(object[4].toString()));
					
					cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
					cell.setCellStyle(style1);
					cell.setCellValue(df.format(object[5]));
					
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(0);
					
					cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
					cell.setCellStyle(style1);
					cell.setCellValue(Float.parseFloat(object[2].toString()));
				}

				for(PurchasePaymentItemDto dto :ladingItemDtos){
					row = sheet.createRow(j++);
					int i =0;
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("尾款"); 
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(dto.getDelayDays()); 
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(dto.getBillNo());
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(dto.getBillNo().substring(0, 6));
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(dto.getProductName());
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(dto.getCountry());
					cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
					cell.setCellValue(dto.getQuantity());
					
					cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
					cell.setCellStyle(style1);
					cell.setCellValue(dto.getItemPrice().doubleValue());
					
					row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(dto.getCurrency());
					cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
					cell.setCellStyle(style1);
					cell.setCellValue(dto.getDeposit());
					
					cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
					cell.setCellStyle(style1);
					cell.setCellValue(dto.getRate());
					
					cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
					cell.setCellStyle(style1);
					cell.setCellValue(dto.getTotalAmount().doubleValue());
					
					cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
					cell.setCellStyle(style1);
					cell.setCellValue(dto.getTotalPaymentAmount().doubleValue());
					
					cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
					cell.setCellStyle(style1);
					cell.setCellValue(dto.getTotalAmount().subtract(dto.getTotalPaymentAmount()).doubleValue());
				}
			
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
	
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	
		String fileName = "tranElementInfos" + sdf.format(new Date()) + ".xls";
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
