/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web.lc;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.Region;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsDelivery;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsDeliveryItem;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsPayment;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsPaymentItem;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.lc.LcPsiPartsPaymentService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 配件订单付款Controller
 * @author Michael
 * @version 2015-06-15
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/lcPsiPartsPayment")
public class LcPsiPartsPaymentController extends BaseController {
	@Autowired
	private LcPsiPartsPaymentService psiPartsPaymentService;
	@Autowired
	private PsiSupplierService psiSupplierService;
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"list", ""})
	public String list(LcPsiPartsPayment psiPartsPayment, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);   
		today.setMinutes(0);
		if (psiPartsPayment.getCreateDate() == null) {
			psiPartsPayment.setCreateDate(DateUtils.addMonths(today, -1));
			psiPartsPayment.setUpdateDate(today);
		}
		//配件供应商
        List<PsiSupplier> suppliers=this.psiSupplierService.findSupplierByType(new String[]{"0","2"});
        Page<LcPsiPartsPayment> page = psiPartsPaymentService.find(new Page<LcPsiPartsPayment>(request, response), psiPartsPayment); 
        model.addAttribute("page", page);
        model.addAttribute("suppliers", suppliers);
		return "modules/psi/lc/parts/lcPsiPartsPaymentList";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "form")
	public String form(LcPsiPartsPayment psiPartsPayment, Model model) {
		model.addAttribute("psiPartsPayment", psiPartsPayment);
		return "modules/psi/lc/parts/lcPsiPartsPaymentForm";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "add")
	public String add(LcPsiPartsPayment psiPartsPayment, Model model) {
		PsiSupplier supplier = new PsiSupplier();
		Map<String, String> accountMaps= null;
		//配件供应商
        List<PsiSupplier> suppliers=this.psiSupplierService.findSupplierByType(new String[]{"0","2"});
      //根据配件供应商id,选择配件信息
      	Integer supplierId =0;
        if(psiPartsPayment.getSupplier()!=null&&psiPartsPayment.getSupplier().getId()!=null){
			supplierId=psiPartsPayment.getSupplier().getId();
			//获取选择供应商的货币类型
			for(PsiSupplier sup:suppliers){
				if(sup.getId().equals(psiPartsPayment.getSupplier().getId())){
					supplier=sup;
					psiPartsPayment.setCurrencyType(sup.getCurrencyType());
					break;
				}
			}
		}else{
			supplier=suppliers.get(0);
			supplierId=supplier.getId();
			psiPartsPayment.setCurrencyType(supplier.getCurrencyType());
		}
        
        accountMaps= supplier.getAccountMap();
        //查询出未付款完成的配件订单
        Map<String,LcPsiPartsOrder> partsOrderMap=this.psiPartsPaymentService.getUnPaymentDoneOrder(supplierId,null);
        Map<String,LcPsiPartsDelivery> ladingMap=this.psiPartsPaymentService.getUnPaymentDoneLading(supplierId,null);
        
        model.addAttribute("accountMaps", accountMaps);
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("orderKeys", JSON.toJSON(partsOrderMap.keySet()));
		model.addAttribute("ladingKeys", JSON.toJSON(ladingMap.keySet()));
	    model.addAttribute("partsOrderMap", JSON.toJSON(partsOrderMap));
	    model.addAttribute("ladingMap", JSON.toJSON(ladingMap));
	    model.addAttribute("psiPartsPayment", psiPartsPayment);
		return "modules/psi/lc/parts/lcPsiPartsPaymentAdd";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "sure")
	public String sure(LcPsiPartsPayment psiPartsPayment, Model model) {
		if(psiPartsPayment.getId()==null){
			return null;
		}
		psiPartsPayment=this.psiPartsPaymentService.get(psiPartsPayment.getId());
		//账号信息
		Map<String, String> accountMaps= psiPartsPayment.getSupplier().getAccountMap();
		model.addAttribute("accountMaps", JSON.toJSON(accountMaps));
		model.addAttribute("psiPartsPayment", psiPartsPayment);
		return "modules/psi/lc/parts/lcPsiPartsPaymentSure";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "view")
	public String view(LcPsiPartsPayment psiPartsPayment, Model model) {
		if(psiPartsPayment.getId()==null){
			return null;
		}
		psiPartsPayment=this.psiPartsPaymentService.get(psiPartsPayment.getId());
		model.addAttribute("psiPartsPayment", psiPartsPayment);
		return "modules/psi/lc/parts/lcPsiPartsPaymentView";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "addSave")
	public String addSave(LcPsiPartsPayment psiPartsPayment, Model model, RedirectAttributes redirectAttributes) throws Exception {
		if(psiPartsPayment.getItems()!=null){
			psiPartsPaymentService.addSave(psiPartsPayment);     
			addMessage(redirectAttributes, "保存配件订单付款'" + psiPartsPayment.getPaymentNo() + "'成功");
			return "redirect:"+Global.getAdminPath()+"/psi/lcPsiPartsPayment/?repage";    
		}
		return null;
	
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "editSave")
	public String editSave(LcPsiPartsPayment psiPartsPayment, Model model, RedirectAttributes redirectAttributes) {
		if(psiPartsPayment.getItems()!=null){
			if(this.psiPartsPaymentService.editSave(psiPartsPayment)){
				addMessage(redirectAttributes, "保存配件订单付款'" + psiPartsPayment.getPaymentNo() + "'成功");
				return "redirect:"+Global.getAdminPath()+"/psi/lcPsiPartsPayment/?repage";
			}
		}
		return null;
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "edit")
	public String edit(LcPsiPartsPayment psiPartsPayment, Model model) {
		Map<String, String> accountMaps= null;
		psiPartsPayment =this.psiPartsPaymentService.get(psiPartsPayment.getId());
		StringBuilder sb = new StringBuilder("");
		for(LcPsiPartsPaymentItem item:psiPartsPayment.getItems()){
			sb.append(item.getId()+",");
		}
		String itemIds="";
		if(StringUtils.isNotEmpty(sb.toString())){
			itemIds=sb.toString().substring(0,sb.toString().length()-1);
		}
		psiPartsPayment.setOldItemIds(itemIds);
		
		
		PsiSupplier  supplier= psiPartsPayment.getSupplier();
		//查出未付款的订单信息
		 Map<String,LcPsiPartsOrder> partsOrderMap=this.psiPartsPaymentService.getUnPaymentDoneOrder(supplier.getId(),supplier.getCurrencyType());
	     Map<String,LcPsiPartsDelivery> ladingMap=this.psiPartsPaymentService.getUnPaymentDoneLading(supplier.getId(),supplier.getCurrencyType());
	     
	     accountMaps= supplier.getAccountMap();
		model.addAttribute("orderSet", partsOrderMap.keySet());
		model.addAttribute("ladingSet",ladingMap.keySet());
		model.addAttribute("accountMaps", accountMaps);
		
		model.addAttribute("orderKeys", JSON.toJSON(partsOrderMap.keySet()));
		model.addAttribute("ladingKeys", JSON.toJSON(ladingMap.keySet()));
		model.addAttribute("orderMaps", JSON.toJSON(partsOrderMap));
		model.addAttribute("ladingMaps", JSON.toJSON(ladingMap));
		
		model.addAttribute("psiPartsPayment", psiPartsPayment);
		return "modules/psi/lc/parts/lcPsiPartsPaymentEdit";
	}
	
	@RequestMapping(value =  "print" )
	public String print(Integer id,HttpServletRequest request, HttpServletResponse response, Model model) throws UnsupportedEncodingException{
		if(id==null){
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		LcPsiPartsPayment  payment = this.psiPartsPaymentService.get(id);
		
		StringBuilder orderNo=new StringBuilder();
		StringBuilder ladingNo =new StringBuilder();
		StringBuilder ladingOrderNo=new StringBuilder();
		for(LcPsiPartsPaymentItem item:payment.getItems()){
			if(item.getOrder()!=null){
				if(orderNo.indexOf(item.getBillNo()+",")<0){
					orderNo.append(item.getOrder().getPartsOrderNo()).append(",");
				}
			}else{
				if(ladingNo.indexOf(item.getBillNo()+",")<0){
					ladingNo.append(item.getBillNo()).append(",");
				}
				for(LcPsiPartsDeliveryItem ladingItem:item.getLadingBill().getItems()){
					String ladingOrderNoTemp=ladingItem.getPartsOrderItem().getPartsOrder().getPartsOrderNo();
					if(ladingOrderNo.indexOf(ladingOrderNoTemp+",")<0&&orderNo.indexOf(ladingOrderNoTemp+",")<0){
						ladingOrderNo.append(ladingOrderNoTemp).append(",");
					}
				}
				
			}
		}
		
		
		PsiSupplier supplier =payment.getSupplier();
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		sheet.setColumnWidth(0, (short)15 * 256);
		sheet.setColumnWidth(1, (short)22 * 256);
		sheet.setColumnWidth(2, (short)12 * 256);
		sheet.setColumnWidth(3, (short)10 * 256);
		sheet.setColumnWidth(4, (short)12 * 256);
		sheet.setColumnWidth(5, (short)10 * 256);
		sheet.setColumnWidth(6, (short)10 * 256);
		sheet.setColumnWidth(7, (short)10 * 256);
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 14); // 字体高度
		font.setFontName("宋体");
		style1.setFont(font);
		style1.setWrapText(true);
		
		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style2.setBorderLeft((short)2);
		style2.setBorderRight((short)2);
		style2.setBorderTop((short)2);
		style2.setBorderBottom((short)2);
		HSSFFont font2 = wb.createFont();
		font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font2.setFontHeightInPoints((short) 12); // 字体高度
		font2.setFontName("宋体");
		style2.setFont(font2);
		style2.setWrapText(true);
		
		HSSFCellStyle style3 = wb.createCellStyle();
		style3.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		style3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style3.setBorderLeft((short)2);
		style3.setBorderRight((short)2);
		style3.setBorderTop((short)2);
		style3.setBorderBottom((short)2);
		HSSFFont font3 = wb.createFont();
		font3.setFontHeightInPoints((short) 10); // 字体高度
		font3.setFontName("宋体");
		style3.setFont(font3);
		style3.setWrapText(true);
		
		
		sheet.setMargin(HSSFSheet.TopMargin,0);// 页边距（上）    
//		sheet.setMargin(HSSFSheet.BottomMargin,1.5);// 页边距（下）    
		sheet.setMargin(HSSFSheet.LeftMargin,1);// 页边距（左）    
//		sheet.setMargin(HSSFSheet.RightMargin,1.5);// 页边距（右   
        
		 // 四个参数分别是：起始行，起始列，结束行，结束列  
		sheet.addMergedRegion(new Region(0, new Short("0"), 0, new Short("7")));   
		sheet.addMergedRegion(new Region(1, new Short("0"),1, new Short("7")));
		sheet.addMergedRegion(new Region(2, new Short("0"),2, new Short("7")));
		sheet.addMergedRegion(new Region(3, new Short("3"),3, new Short("7")));
		sheet.addMergedRegion(new Region(4, new Short("3"),4, new Short("7")));
		sheet.addMergedRegion(new Region(5, new Short("3"),5, new Short("7")));
		sheet.addMergedRegion(new Region(6, new Short("3"),6, new Short("7")));
		
		sheet.addMergedRegion(new Region(7, new Short("1"),7, new Short("7")));
		sheet.addMergedRegion(new Region(8, new Short("1"),8, new Short("7")));
		sheet.addMergedRegion(new Region(9, new Short("1"),9, new Short("7")));
		
		
		sheet.addMergedRegion(new Region(11, new Short("3"),11, new Short("7")));
		
		sheet.addMergedRegion(new Region(12, new Short("1"),12, new Short("7")));
		sheet.addMergedRegion(new Region(13, new Short("0"),13, new Short("7")));
		sheet.addMergedRegion(new Region(14, new Short("0"),14, new Short("7")));
		sheet.addMergedRegion(new Region(15, new Short("0"),15, new Short("7")));
		
		Integer rowIndex=0;
		HSSFRow row = sheet.createRow(rowIndex++);
		row.setHeight((short)(2*256));
		HSSFCell cell =row.createCell(0,Cell.CELL_TYPE_STRING);
		cell.setCellValue("深圳市理诚科技有限公司"); 
		cell.setCellStyle(style1);
		
		row = sheet.createRow(rowIndex++);
		row.setHeight((short)(2*256));
		cell =row.createCell(0,Cell.CELL_TYPE_STRING);
		cell.setCellValue("采购付款申请单"); 
		cell.setCellStyle(style1);
		
		row = sheet.createRow(rowIndex++);
		cell =row.createCell(0,Cell.CELL_TYPE_STRING);
		cell.setCellValue("编号："+payment.getPayFlowNo()==null?"":payment.getPayFlowNo()); 
		
		List<String> list = Lists.newArrayList();
		list.add("申请日期,付款日期");
		list.add("申请部门,收款单位");
		list.add("付款方式,发票");
		list.add("付款单号,发票号");
		list.add("账号信息");
		list.add("付款事由");
		list.add("付款金额");
		list.add("经办人,财务审核,财务主管,出纳");
		list.add("部门负责人,总经理");
		list.add("备注");
		for(int i=0;i<10;i++){
			String title = list.get(i);
			if(i!=4&&i!=5&&i!=6&&i!=9){
				
				String value1="";
				String value2="";
				String arr[]=title.split(",");
				if("申请日期".equals(arr[0])){
					value1=sdf.format(payment.getCreateDate());
					if(payment.getSureDate()!=null){
						value2=sdf.format(payment.getSureDate());
					}
				}else if("申请部门".equals(arr[0])){
					value1=payment.getCreateUser().getOffice().getName();
					value2="("+supplier.getNikename()+")"+supplier.getName();
				}else if("付款方式".equals(arr[0])){
					value1="网银转账";
					value2="已开/未开";
				}else if("付款单号".equals(arr[0])){
					value1=payment.getPaymentNo();
				}else if("经办人".equals(arr[0])){
					value1=payment.getCreateUser().getName();
					value2="susan";
				}else if("审核人".equals(arr[0])){
					value1="";
					value2="";
				}
				
				
				row = sheet.createRow(rowIndex++);
				row.setHeight((short)(1.6*256));
				cell =row.createCell(0,Cell.CELL_TYPE_STRING);
				cell.setCellValue(arr[0]); 
				cell.setCellStyle(style2);
				
				cell =row.createCell(1,Cell.CELL_TYPE_STRING);
				cell.setCellValue(value1); 
				cell.setCellStyle(style3);
				
				cell =row.createCell(2,Cell.CELL_TYPE_STRING);
				cell.setCellValue(arr[1]); 
				cell.setCellStyle(style2);   
				
				cell =row.createCell(3,Cell.CELL_TYPE_STRING);
				cell.setCellValue(value2); 
				cell.setCellStyle(style3);
				
				if("经办人".equals(arr[0])){
					cell =row.createCell(4,Cell.CELL_TYPE_STRING);
					cell.setCellValue(arr[2]); 
					cell.setCellStyle(style2);   
					
					cell =row.createCell(5,Cell.CELL_TYPE_STRING);
					cell.setCellValue(""); 
					cell.setCellStyle(style3);
					
					cell =row.createCell(6,Cell.CELL_TYPE_STRING);
					cell.setCellValue(arr[3]); 
					cell.setCellStyle(style2);   
					
					cell =row.createCell(7,Cell.CELL_TYPE_STRING);
					cell.setCellValue(""); 
					cell.setCellStyle(style3);
					
				}else{
					cell =row.createCell(4,Cell.CELL_TYPE_STRING);
					cell.setCellValue(""); 
					cell.setCellStyle(style2);   
					
					cell =row.createCell(5,Cell.CELL_TYPE_STRING);
					cell.setCellValue(""); 
					cell.setCellStyle(style3);
					
					cell =row.createCell(6,Cell.CELL_TYPE_STRING);
					cell.setCellValue(""); 
					cell.setCellStyle(style2);   
					
					cell =row.createCell(7,Cell.CELL_TYPE_STRING);
					cell.setCellValue(""); 
					cell.setCellStyle(style3);
				}
				
			}else{
				row = sheet.createRow(rowIndex++);
				if(i==6){
					row.setHeight((short)(1.6*256));
				}else if(i==4){
					row.setHeight((short)(6*256));
				}else{
					row.setHeight((short)(2*256));
				}
				String value1="";
				if("账号信息".equals(title)){
					value1=payment.getAccountNoBr();
				}else if("付款事由".equals(title)){
					if(StringUtils.isNotEmpty(ladingNo)){
						value1+="尾款";
					}
					if(StringUtils.isNotEmpty(orderNo)){
						value1+="定金";
					}
				}else if("付款金额".equals(title)){
					value1=payment.getPaymentAmountTotal()+payment.getCurrencyType();
				}else if("备注".equals(title)){
					if(StringUtils.isNotEmpty(ladingNo)){
						value1+="提单号:"+ladingNo;
					}
					
					if(StringUtils.isNotEmpty(orderNo)&&StringUtils.isEmpty(ladingOrderNo)){
						value1+="订单号:"+orderNo;
					}else if(StringUtils.isEmpty(orderNo)&&StringUtils.isNotEmpty(ladingOrderNo)){
						value1+="订单号:"+ladingOrderNo;
					}else if(StringUtils.isNotEmpty(orderNo)&&StringUtils.isNotEmpty(ladingOrderNo)){
						value1+="订单号:"+orderNo+ladingOrderNo;
					}
				}
				
				cell =row.createCell(0,Cell.CELL_TYPE_STRING);
				cell.setCellValue(title); 
				cell.setCellStyle(style2);
				
				cell =row.createCell(1,Cell.CELL_TYPE_STRING);
				cell.setCellValue(value1); 
				cell.setCellStyle(style3);
				
				cell =row.createCell(2,Cell.CELL_TYPE_STRING);
				cell.setCellValue(""); 
				cell.setCellStyle(style3);
				
				cell =row.createCell(3,Cell.CELL_TYPE_STRING);
				cell.setCellValue(""); 
				cell.setCellStyle(style3);
				
				for(int j=2;j<8;j++){
					cell =row.createCell(j,Cell.CELL_TYPE_STRING);
					cell.setCellValue(""); 
					cell.setCellStyle(style3);
				}
			}
		}
		  
		row = sheet.createRow(rowIndex++);
		cell =row.createCell(0,Cell.CELL_TYPE_STRING);
		cell.setCellValue(payment.getCreateUser().getName()+"申请，申请时间："+sdf.format(payment.getCreateDate()));
		cell.setCellStyle(style3);
		
		cell =row.createCell(1,Cell.CELL_TYPE_STRING);
		cell.setCellValue(""); 
		cell.setCellStyle(style3);
		
		cell =row.createCell(2,Cell.CELL_TYPE_STRING);
		cell.setCellValue(""); 
		cell.setCellStyle(style3);
		
		cell =row.createCell(3,Cell.CELL_TYPE_STRING);
		cell.setCellValue(""); 
		cell.setCellStyle(style3);
		
		for(int j=1;j<8;j++){
			cell =row.createCell(j,Cell.CELL_TYPE_STRING);
			cell.setCellValue(""); 
			cell.setCellStyle(style3);
		}
		
		row = sheet.createRow(rowIndex++);
		cell =row.createCell(0,Cell.CELL_TYPE_STRING);
		if(payment.getApplyUser()!=null){
			cell.setCellValue(payment.getApplyUser().getName()+"审核，申请时间："+sdf.format(payment.getApplyDate()));
		}else{
			cell.setCellValue("");
		}
		cell.setCellStyle(style3);
		cell =row.createCell(1,Cell.CELL_TYPE_STRING);
		cell.setCellValue(""); 
		cell.setCellStyle(style3);
		
		cell =row.createCell(2,Cell.CELL_TYPE_STRING);
		cell.setCellValue(""); 
		cell.setCellStyle(style3);
		
		cell =row.createCell(3,Cell.CELL_TYPE_STRING);
		cell.setCellValue(""); 
		cell.setCellStyle(style3);
		
		for(int j=1;j<8;j++){
			cell =row.createCell(j,Cell.CELL_TYPE_STRING);
			cell.setCellValue(""); 
			cell.setCellStyle(style3);
		}
		
		row = sheet.createRow(rowIndex++);
		cell =row.createCell(0,Cell.CELL_TYPE_STRING);
		if(payment.getSureUser()!=null){
			cell.setCellValue(payment.getSureUser().getName()+"复核，复核时间："+sdf.format(payment.getSureDate()));
		}else{
			cell.setCellValue("");
		}
		cell.setCellStyle(style3);
		cell =row.createCell(1,Cell.CELL_TYPE_STRING);
		cell.setCellValue(""); 
		cell.setCellStyle(style3);
		
		cell =row.createCell(2,Cell.CELL_TYPE_STRING);
		cell.setCellValue(""); 
		cell.setCellStyle(style3);
		
		cell =row.createCell(3,Cell.CELL_TYPE_STRING);
		cell.setCellValue(""); 
		cell.setCellStyle(style3);
		for(int j=1;j<8;j++){
			cell =row.createCell(j,Cell.CELL_TYPE_STRING);
			cell.setCellValue(""); 
			cell.setCellStyle(style3);
		}
		
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
	response.setContentType("application/x-download");

	SimpleDateFormat sdf1 = new SimpleDateFormat("MMddhhmm");

	String fileName = "partsPay" + sdf1.format(new Date()) + ".xls";
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
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "cancel")
	public String cancel(LcPsiPartsPayment psiPartsPayment, Model model, RedirectAttributes redirectAttributes) {
		if(psiPartsPayment.getId()==null){
			return null;
		}
		String flag= "";
		if(psiPartsPayment.getPaymentSta()!=null){
			flag=psiPartsPayment.getPaymentSta();
			psiPartsPayment.setPaymentSta(null);
		}
		
		if(!"".equals(flag)){
			psiPartsPayment=this.psiPartsPaymentService.get(psiPartsPayment.getId());
			//如果是申请状态下取消     追回申请款项；
			if(flag.equals("4")){
				psiPartsPayment.setPaymentSta("0");   //草稿状态
				this.psiPartsPaymentService.cancel(psiPartsPayment);
			}else if(flag.equals("5")){
				psiPartsPayment.setPaymentSta("3");   //已取消
				this.psiPartsPaymentService.cancel(psiPartsPayment);
			}else if(flag.equals("6")){
				psiPartsPayment.setPaymentSta("3");   //已取消
				psiPartsPayment.setCancelDate(new Date());
				psiPartsPayment.setCancelUser(UserUtils.getUser());
				this.psiPartsPaymentService.save(psiPartsPayment);
			}
		}
		
		addMessage(redirectAttributes, "取消配件订单付款'" + psiPartsPayment.getPaymentNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiPartsPayment/?repage";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "sureSave")
	public String sureSave(MultipartFile memoFile,LcPsiPartsPayment psiPartsPayment, Model model, RedirectAttributes redirectAttributes) {
		if(psiPartsPayment.getId()==null){
			return null;
		}
		psiPartsPayment=this.psiPartsPaymentService.get(psiPartsPayment.getId());
		psiPartsPaymentService.sureSave(memoFile,psiPartsPayment);
		addMessage(redirectAttributes, "确认配件订单付款'" + psiPartsPayment.getPaymentNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiPartsPayment/?repage";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "save")
	public String save(LcPsiPartsPayment psiPartsPayment, Model model, RedirectAttributes redirectAttributes) {
		psiPartsPaymentService.save(psiPartsPayment);
		addMessage(redirectAttributes, "保存配件订单付款'" + psiPartsPayment.getPaymentNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiPartsPayment/?repage";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value =  "expPaymentTotal" )
	public String expPaymentTotal(LcPsiPartsPayment purchasePayment, HttpServletRequest request, HttpServletResponse response, Model model) throws UnsupportedEncodingException{
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日");
		List<LcPsiPartsPayment> payments  = this.psiPartsPaymentService.find(purchasePayment);    
		//查出未付完款的提单项信息
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		sheet.addMergedRegion(new Region(0, new Short("0"), 0, new Short("13")));   
		sheet.addMergedRegion(new Region(1, new Short("0"),1, new Short("7")));
		sheet.addMergedRegion(new Region(1, new Short("8"),1, new Short("13")));
		
		HSSFCellStyle style3 = wb.createCellStyle();
		style3.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont font1 = wb.createFont();
		font1.setFontHeightInPoints((short) 18); // 字体高度
		font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style3.setFont(font1);
		
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 14); // 字体高度
		style.setFont(font);
		
		String[] title = {" 序号 ","申请日期"," 申请人 "," 申请部门  ", " 付款单号 ", " 收款单位  "," 付款事由 "," 币别 "," 付款日期 " ," 付款金额", "收发票日期", "发票号码", "发票金额"," 收款银行账号  " };
		
		
		
		HSSFCellStyle style1 = wb.createCellStyle();
		HSSFDataFormat df1 = wb.createDataFormat();  //此处设置数据格式  
		style1.setDataFormat(df1.getFormat("#0.00")); //小数点后保留两位，可以写contentStyle.setDataFormat(df.getFormat("#,#0.00"));  
		
		HSSFCellStyle style2 = wb.createCellStyle();
		
		style2.setWrapText(true);
		
		int j =0;
		row = sheet.createRow(j++);
		HSSFCell headCell= row.createCell(0,Cell.CELL_TYPE_STRING);
		headCell.setCellStyle(style3);
		headCell.setCellValue("深圳理诚科技有限公司");
		
		row = sheet.createRow(j++);
		row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue("付款明细");
		String dateStr=sdf2.format(purchasePayment.getCreateDate())+"至"+sdf2.format(purchasePayment.getUpdateDate());
		row.createCell(8,Cell.CELL_TYPE_STRING).setCellValue("付款日期："+dateStr);
		
		row = sheet.createRow(j++);
		HSSFCell cell = null;		
		for (int i = 0; i < title.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		
		BigDecimal  total = BigDecimal.ZERO;
		for(LcPsiPartsPayment payment:payments){
			total=total.add(new BigDecimal(payment.getPaymentAmountTotal()));
			StringBuilder orderNo=new StringBuilder();
			StringBuilder ladingNo = new StringBuilder();
			for(LcPsiPartsPaymentItem item:payment.getItems()){
				if(item.getOrder()!=null){
//					if(!orderNo.contains(item.getBillNo()+",")){
					if(orderNo.indexOf(item.getBillNo()+",")<0){
						orderNo.append(item.getOrder().getPartsOrderNo()).append(",");
					}
				}else{
					if(ladingNo.indexOf(item.getBillNo()+",")<0){
						ladingNo.append(item.getBillNo()).append(",");
					}
				}
			}
			
			String reason ="";
			if(StringUtils.isNotEmpty(orderNo)&&StringUtils.isNotEmpty(ladingNo)){
				reason="定金/尾款";
			}else if(StringUtils.isNotEmpty(orderNo)){
				reason="定金";
			}else if(StringUtils.isNotEmpty(ladingNo)){
				reason="尾款";
			}
			
			row = sheet.createRow(j++);
			int i =0;
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(payment.getId());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sdf1.format(payment.getCreateDate()));
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(payment.getCreateUser().getName());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(payment.getCreateUser().getOffice().getName());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(payment.getPaymentNo());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(payment.getSupplier().getName());
			
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(reason);
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(payment.getCurrencyType());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sdf1.format(payment.getSureDate()));
			
			HSSFCell cellF= row.createCell(i++,Cell.CELL_TYPE_STRING);
			cellF.setCellStyle(style1);
			cellF.setCellValue(payment.getPaymentAmountTotal().floatValue());
			
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
			
			HSSFCell cellFF =row.createCell(i++,Cell.CELL_TYPE_STRING);
			cellFF.setCellValue(payment.getAccountNoBr());
		}
		
		row = sheet.createRow(j++);
		row.createCell(5,Cell.CELL_TYPE_STRING).setCellValue("合计");
		HSSFCell cell1 =row.createCell(9,Cell.CELL_TYPE_STRING);
		cell1.setCellStyle(style1);
		cell1.setCellValue(total.doubleValue());
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
	
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	
		String fileName = "partsPaymentTotalInfos" + sdf.format(new Date()) + ".xls";
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
