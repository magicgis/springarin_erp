/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web.lc;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.custom.entity.SendEmail;
import com.springrain.erp.modules.custom.service.SendEmailService;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsOrderItem;
import com.springrain.erp.modules.psi.entity.parts.PsiParts;
import com.springrain.erp.modules.psi.scheduler.PoEmailManager;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.lc.LcPsiPartsOrderService;
import com.springrain.erp.modules.psi.service.parts.PsiPartsService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 产品配件Controller
 * @author Michael
 * @version 2015-06-02
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/lcPsiPartsOrder")
public class LcPsiPartsOrderController extends BaseController {
	@Autowired
	private LcPsiPartsOrderService  psiPartsOrderService;
	@Autowired
	private PsiPartsService       psiPartsService;
	@Autowired
	private PsiSupplierService    psiSupplierService;
	@Autowired
	private SendEmailService      sendEmailService;
	@Autowired
	private PoEmailManager        poMaillManager;  
	private static String filePath;    
	   
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"list", ""})
	public String list(LcPsiPartsOrder psiPartsOrder, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date today = sdf.parse(sdf.format(new Date()));
		if(psiPartsOrder.getCreateDate() == null) {
			psiPartsOrder.setCreateDate(DateUtils.addMonths(today, -1));
		}
		if(psiPartsOrder.getUpdateDate()==null){
			psiPartsOrder.setUpdateDate(today);
		}
		
        Page<LcPsiPartsOrder> page = psiPartsOrderService.find(new Page<LcPsiPartsOrder>(request, response), psiPartsOrder); 
        //配件供应商
        List<PsiSupplier> suppliers=this.psiSupplierService.findSupplierByType(new String[]{"0","2"});
		model.addAttribute("suppliers", suppliers);
        model.addAttribute("page", page);
		return "modules/psi/lc/parts/lcPsiPartsOrderList";
	}

	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "edit")
	public String edit(LcPsiPartsOrder psiPartsOrder, Model model) {
		StringBuilder sb = new StringBuilder("");
		if(psiPartsOrder.getId()!=null){
			psiPartsOrder=this.psiPartsOrderService.get(psiPartsOrder.getId());
		}
		
		for(LcPsiPartsOrderItem item:psiPartsOrder.getItems()){
			sb.append(item.getId()+",");
		}
		
		if(StringUtils.isNotEmpty(sb.toString())){
			psiPartsOrder.setOldItemIds(sb.toString().substring(0,sb.toString().length()-1));
		}
		
		Map<Integer,PsiParts> partsMap= this.psiPartsService.getPartsBySupplierId(psiPartsOrder.getSupplier().getId());
		model.addAttribute("partsMap", JSON.toJSON(partsMap));
		
		model.addAttribute("partsMapEdit", partsMap);
		model.addAttribute("psiPartsOrder", psiPartsOrder);
		return "modules/psi/lc/parts/lcPsiPartsOrderEdit";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "sure")
	public String sure(LcPsiPartsOrder psiPartsOrder, Model model) {
		if(psiPartsOrder.getId()!=null){
			psiPartsOrder=this.psiPartsOrderService.get(psiPartsOrder.getId());
		}else{
			return null;
		}
		model.addAttribute("psiPartsOrder", psiPartsOrder);
		return "modules/psi/lc/parts/lcPsiPartsOrderSure";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "view")
	public String view(LcPsiPartsOrder psiPartsOrder, Model model) {
		if(psiPartsOrder.getId()!=null){
			psiPartsOrder=this.psiPartsOrderService.get(psiPartsOrder.getId());
		}
		model.addAttribute("psiPartsOrder", psiPartsOrder);
		return "modules/psi/lc/parts/lcPsiPartsOrderView";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "add")
	public String add(LcPsiPartsOrder psiPartsOrder, Model model) throws ParseException {
		//配件供应商
        List<PsiSupplier> suppliers=this.psiSupplierService.findSupplierByType(new String[]{"0","2"});
		
		//根据配件供应商id,选择配件信息
		Integer supplierId =0;
		if(psiPartsOrder.getSupplier()!=null&&psiPartsOrder.getSupplier().getId()!=null){
			supplierId=psiPartsOrder.getSupplier().getId();
			//获取选择供应商的货币类型
			for(PsiSupplier sup:suppliers){
				if(sup.getId().equals(psiPartsOrder.getSupplier().getId())){
					psiPartsOrder.setDeposit(sup.getDeposit());
					psiPartsOrder.setCurrencyType(sup.getCurrencyType());
					break;
				}
			}
		}else{
			PsiSupplier s1=suppliers.get(0);
			supplierId=s1.getId();
			psiPartsOrder.setDeposit(s1.getDeposit());
			psiPartsOrder.setCurrencyType(s1.getCurrencyType());
			
		}
		Map<Integer,PsiParts> partsMap= this.psiPartsService.getPartsBySupplierId(supplierId);
		
		
		model.addAttribute("suppliers", suppliers);
		model.addAttribute("partsMap", JSON.toJSON(partsMap));
		model.addAttribute("psiPartsOrder", psiPartsOrder);
		return "modules/psi/lc/parts/lcPsiPartsOrderAdd";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "sendEmail")
	public String sendEmail(LcPsiPartsOrder psiPartsOrder, Model model,RedirectAttributes redirectAttributes) throws Exception {
		if(psiPartsOrder.getId()!=null){
			psiPartsOrder=this.psiPartsOrderService.get(psiPartsOrder.getId());
		}
		if("0".equals(psiPartsOrder.getOrderSta())){
			//发送pdf给供应商  
			sendEmailToSupplier(psiPartsOrder);
			//更新发送状态      更新数量到库存表   
			this.psiPartsOrderService.updateSendSta(psiPartsOrder.getId(), "1");
			addMessage(redirectAttributes, "邮件发送成功'" + psiPartsOrder.getPartsOrderNo() + "'成功");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiPartsOrder/?repage";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@ResponseBody
	@RequestMapping(value = "printPdf")
	public String printPdf(LcPsiPartsOrder partsOrder,HttpServletResponse response) throws Exception {
		partsOrder = this.psiPartsOrderService.get(partsOrder.getId());
		if (filePath == null) {
			filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/partsOrder";
		}
		File file = new File(filePath, partsOrder.getPartsOrderNo());
		if (!file.exists()) {
			file.mkdirs();
		}
		File pdfFile = new File(file, partsOrder.getPartsOrderNo() + ".pdf");   
		PdfUtil.genPartsOrderPdf(pdfFile, partsOrder);
		FileInputStream in = new FileInputStream(pdfFile);
		ServletOutputStream out = response.getOutputStream();
		response.setContentType("application/pdf");// pdf文件
		response.addHeader("Content-Disposition", "filename="+ partsOrder.getPartsOrderNo()+".pdf");
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
	@RequestMapping(value = "productReceive")
	public String productReceive(Integer id, Model model,RedirectAttributes redirectAttributes) {
		LcPsiPartsOrder partsOrder = this.psiPartsOrderService.get(id);
		this.psiPartsOrderService.productReceive(partsOrder);
		addMessage(redirectAttributes, "配件订单'" + partsOrder.getPartsOrderNo() + "'的产品已经收货成功，库存冻结数量已减！");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiPartsOrder/?repage";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "cancel")
	public String cancle(Integer id, Model model,RedirectAttributes redirectAttributes) {
		LcPsiPartsOrder partsOrder = this.psiPartsOrderService.get(id);
		this.psiPartsOrderService.cancel(partsOrder);
		addMessage(redirectAttributes, "取消订单'" + partsOrder.getPartsOrderNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiPartsOrder/?repage";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "addSave")
	public String addSave(String nikeName,LcPsiPartsOrder psiPartsOrder, Model model, RedirectAttributes redirectAttributes) throws Exception {
		psiPartsOrderService.addSave(psiPartsOrder,nikeName);
		addMessage(redirectAttributes, "保存产品配件'" + psiPartsOrder.getPartsOrderNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiPartsOrder/?repage";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "editSave")
	public String editSave(String nikeName,LcPsiPartsOrder psiPartsOrder, Model model, RedirectAttributes redirectAttributes) {
		psiPartsOrderService.editSave(psiPartsOrder);
		addMessage(redirectAttributes, "保存产品配件'" + psiPartsOrder.getPartsOrderNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiPartsOrder/?repage";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "sureSave")
	public String sureSave(LcPsiPartsOrder psiPartsOrder, Model model, RedirectAttributes redirectAttributes) {
		psiPartsOrderService.sureSave(psiPartsOrder);
		addMessage(redirectAttributes, "确认产品配件'" + psiPartsOrder.getPartsOrderNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiPartsOrder/?repage";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "expOrder")
	public String expOrder(LcPsiPartsOrder psiPartsOrder, Model model, HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
		
		Page<LcPsiPartsOrder> page  =new Page<LcPsiPartsOrder>(request, response);
		page.setPageSize(600000);
		List<LcPsiPartsOrder> list = psiPartsOrderService.find(page,psiPartsOrder).getList(); 
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		String[] title = { " 配件订单号  ","   订单状态     ","  产品订单号   ","  下单日期  "," 配件型号  " ,"订单数量","  收货数量   "," 付款数量  "};
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
		for(LcPsiPartsOrder order:list){
			for(LcPsiPartsOrderItem item:order.getItems()){
				int i =0;
				row = sheet.createRow(j++);
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(order.getPartsOrderNo()); 
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(order.getOrderStaName()); 
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(StringUtils.isNotEmpty(order.getPurchaseOrderNo())?order.getPurchaseOrderNo():"");
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(order.getPurchaseDate());
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getPartsName());
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getQuantityOrdered());
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getQuantityReceived());
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(item.getQuantityPayment());
			 }
		}
		
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		String fileName = "partsOrders" + sdf.format(new Date()) + ".xls";
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
	
	private void sendEmailToSupplier(LcPsiPartsOrder psiPartsOrder) throws Exception{
		SendEmail  sendEmail = new SendEmail();
		//往采购供应商发信 获取供应商模板
		PsiSupplier supplier = psiSupplierService.get(psiPartsOrder.getSupplier().getId());
		String orderNo=psiPartsOrder.getPartsOrderNo();
		Map<String,Object> params = Maps.newHashMap();
		String template = "";
		try {
			params.put("supplier", supplier);
			params.put("cuser", UserUtils.getUser());
			template = PdfUtil.getPsiTemplate("partsOrderEmail.ftl", params);
		} catch (Exception e) {}
		sendEmail.setSendContent(template);
		sendEmail.setSendEmail(supplier.getMail());    
		sendEmail.setSendSubject("新配件订单PN"+orderNo+"("+DateUtils.getDate()+")");
		sendEmail.setCreateBy(UserUtils.getUser());
		//向供应商发送邮件 加入附件和抄送人
		String address =  "frank@inateck.com,sophie@inateck.com,bella@inateck.com,emma.chao@inateck.com,"+UserUtils.getUser().getEmail();  
		sendEmail.setBccToEmail(address);
		//发送邮件
		final MailInfo mailInfo = sendEmail.getMailInfo();
		String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiParts";
		String uuid = UUID.randomUUID().toString();
		File baseDir = new File(baseDirStr+"/"+uuid); 
		if(!baseDir.isDirectory())
			baseDir.mkdirs();
		File pdfFile = new File(baseDir,orderNo + ".pdf");
		PdfUtil.genPartsOrderPdf(pdfFile, psiPartsOrder);
		mailInfo.setFileName(orderNo + ".pdf");
		mailInfo.setFilePath(pdfFile.getAbsolutePath());
		sendEmail.setSendAttchmentPath(uuid+"/"+orderNo + ".pdf");
		new Thread(){
			@Override
			public void run() {
				poMaillManager.send(mailInfo);
			}
		}.start();
		
		sendEmail.setSentDate(new Date());
		sendEmail.setSendFlag("1");
		sendEmailService.save(sendEmail);
		
	}
	

}
