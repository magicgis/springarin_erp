package com.springrain.erp.modules.amazoninfo.web.order;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.cxf.endpoint.Client;
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
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.email.MailManagerInfo;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonBuyComment;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonCustomer;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderExtract;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderItem;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonRefund;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazoninfoRefundItem;
import com.springrain.erp.modules.amazoninfo.scheduler.SendEmailByOrderMonitor;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonRefundService;
import com.springrain.erp.modules.custom.entity.Event;
import com.springrain.erp.modules.custom.scheduler.CustomEmailManager;
import com.springrain.erp.modules.custom.service.EventService;
import com.springrain.erp.modules.custom.service.UnsubscribeEmailService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/refund")
public class AmazonRefundController extends BaseController {
	@Autowired
	private AmazonOrderService amazonOrderService;
	@Autowired
	private AmazonRefundService amazonRefundService;
	@Autowired
	private MailManager              mailManager;
	@Autowired
	private SystemService systemService;
	@Autowired
	private AmazonProductService amazonProductService;
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	@Autowired
	private CustomEmailManager sendCustomEmailManager;
	@Autowired
	private EventService eventService;
	@Autowired
	private AmazonCustomerService amazonCustomerService;
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	
	private static DateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSS"); 
    
	@ModelAttribute
	public AmazonRefund get(@RequestParam(required=false) Integer id) {
		if (id!=null&&id>0){
			return amazonRefundService.get(id);
		}else{
			return new AmazonRefund();
		}
	}
	
	@RequiresPermissions("amazoninfo:refund:view")
	@RequestMapping(value = { "list", "" })
	public String list(AmazonRefund amazonRefund, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<AmazonRefund> page = new Page<AmazonRefund>(request, response);
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (amazonRefund.getCreateDate() == null) {
			amazonRefund.setCreateDate(DateUtils.addMonths(today, -1));
		}
		if (amazonRefund.getEndDate()== null) {
			amazonRefund.setEndDate(today);
		}
		
		/*if(StringUtils.isEmpty(amazonRefund.getCountry())){
			amazonRefund.setCountry("de");
		}*/
		String orderBy = page.getOrderBy();
		if ("".equals(orderBy)) {
			page.setOrderBy("createDate desc");
		} else {
			page.setOrderBy(orderBy + ",createDate desc");
		}
		page = amazonRefundService.find(page,amazonRefund);
		page.setOrderBy(orderBy);
		model.addAttribute("page", page);
		List<User> all =systemService.findAllUsers();
		model.addAttribute("all", all);
		return "modules/amazoninfo/order/amazonRefundList";
	}
	
	@RequiresPermissions("amazoninfo:refund:view")
	@RequestMapping(value = { "add"})
	public String add(String type,AmazonOrder amazonOrder,String selectItems, HttpServletRequest request,HttpServletResponse response, Model model) {
		amazonOrder=amazonOrderService.get(amazonOrder.getId());
		AmazonRefund amazonRefund=new AmazonRefund();
		amazonRefund.setCountry(StringUtils.substringAfterLast(amazonOrder.getSalesChannel(), "."));
		amazonRefund.setAccountName(amazonOrder.getAccountName());
		amazonRefund.setAmazonOrderId(amazonOrder.getAmazonOrderId());
		List<AmazonRefund> records=amazonRefundService.getRefundRecord(amazonRefund.getAmazonOrderId());
		List<User> all = Lists.newArrayList();
	     /*   for (Office office :  officeService.findAll()) {
	        	all.addAll(office.getUserList());
			}*/
		try{
			List<Object[]> settlementreportList=amazonRefundService.getsettlementreport(amazonRefund.getAmazonOrderId());
			model.addAttribute("settlementreportList", settlementreportList);
		}catch(Exception e){}
		List<User> list1 = systemService.findUserByPermission("amazoninfo:refund:"+StringUtils.substringAfterLast(amazonOrder.getSalesChannel(),"."));
		if (list1 != null && list1.size() > 0) {
			all.addAll(list1);
		}
		//全球退款审核人
		List<User> list2 = systemService.findUserByPermission("amazoninfo:refund:all");
		if (list2 != null && list2.size() > 0) {
			all.addAll(list2);
		}
		model.addAttribute("records", records);
		model.addAttribute("selectItems", selectItems);
		model.addAttribute("amazonOrder", amazonOrder);
		model.addAttribute("all", all);
		if("1".equals(type)){
			return "modules/amazoninfo/order/amazonOtherRefundAdd";
		}else{
			return "modules/amazoninfo/order/amazonRefundAdd";
		}
		
	}
	
	
	@RequestMapping(value = "isExistOrder")
	@ResponseBody
	public String isExistOrder(Float orderTotal,String amazonOrderId){
		return amazonRefundService.findExistOrder(amazonOrderId, orderTotal);
	}
	
	
	@RequiresPermissions("amazoninfo:refund:view")
	@RequestMapping(value = { "save"})
	public String save(AmazonRefund amazonRefund,HttpServletRequest request,HttpServletResponse response, Model model) {
		Float orderTotal = new Float(0);
		for (AmazoninfoRefundItem item : amazonRefund.getItems()) {
			orderTotal+=item.getMoney();
			item.setAmazonRefund(amazonRefund);
		}
		DecimalFormat df =new DecimalFormat("#.00");
		amazonRefund.setRefundTotal(Float.parseFloat(df.format(orderTotal)));
		/*String ctxPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/refund/";
		String dateStr = format.format(new Date());
		amazonRefund.setResultFile(dateStr);
		File dir = new File(ctxPath+dateStr);
		dir.mkdirs();
		amazonRefundService.submit(amazonRefund,dir);*/
		if(amazonRefund.getId()==null){
			amazonRefund.setCreateUser(UserUtils.getUser());
			amazonRefund.setCreateDate(new Date());
		}
		amazonRefund.setRefundState("0");
		amazonRefundService.save(amazonRefund);
		sendEmail(amazonRefund);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/refund/?country="+amazonRefund.getCountry()+"&repage";
	}
	
	

	@RequiresPermissions("amazoninfo:refund:view")
	@RequestMapping(value = { "saveQuick"})
	public String saveQuick(AmazonRefund amazonRefund,HttpServletRequest request,HttpServletResponse response, Model model) {
		Float orderTotal = new Float(0);
		for (AmazoninfoRefundItem item : amazonRefund.getItems()) {
			orderTotal+=item.getMoney();
			item.setAmazonRefund(amazonRefund);
		}
		DecimalFormat df =new DecimalFormat("#.00");
		amazonRefund.setRefundTotal(Float.parseFloat(df.format(orderTotal)));
		amazonRefund.setRefundState("1");
		amazonRefund.setOperUser(UserUtils.getUser());
		String ctxPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/refund/";
		String dateStr = format.format(new Date());
		amazonRefund.setResultFile(dateStr);
		File dir = new File(ctxPath+dateStr);
		dir.mkdirs();
		AmazonAccountConfig config=amazonAccountConfigService.getByName(amazonRefund.getAccountName());
		submit(amazonRefund,dir,config);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/refund/?country="+amazonRefund.getCountry()+"&repage";
	}
	
	

	private  boolean sendEmail(AmazonRefund amazonRefund){
		String toAddress ="";
		StringBuffer content= new StringBuffer("");
		if(amazonRefund!=null){
			toAddress=systemService.getUser(amazonRefund.getOperUser().getId()).getEmail();
			content.append("<p>请审核"+amazonRefund.getCreateUser().getName()+"提交的退款申请,<a title='点击链接到退款列表' href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/amazoninfo/refund?country="+amazonRefund.getCountry()+"&amazonOrderId="+amazonRefund.getAmazonOrderId()+"'>"+amazonRefund.getAmazonOrderId()+"点击审核</a></p>");
			content.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '><tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '>");
			content.append("<th>产品名称</th><th>sku</th><th>退款原因</th><th>退款类型</th><th>退款金额</th><th>备注</th></tr>");
			for (AmazoninfoRefundItem item: amazonRefund.getItems()) {
				content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
				content.append("<td>"+item.getProductName()+"</td><td>"+item.getSku()+"</td><td>"+item.getRefundReason()+"</td>");
				content.append("<td>"+item.getRefundType()+"</td><td>"+item.getMoney()+"</td><td>"+item.getRemark()+"</td></tr>");
			}
			content.append("</table>");
		}
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();   
			final MailInfo mailInfo = new MailInfo(toAddress,amazonRefund.getAmazonOrderId()+"亚马逊订单退款审核通知"+DateUtils.getDate("-yyyy/MM/dd"),date);
			mailInfo.setContent(content.toString());
			new Thread(){
				public void run(){   
					 mailManager.send(mailInfo);
				}
			}.start();
		}
		return true;
	}
	
	@RequiresPermissions("amazoninfo:refund:edit")
	@RequestMapping(value = { "isChecked"})
	public String isChecked(AmazonRefund amazonRefund,String checkCountry,HttpServletRequest request,HttpServletResponse response, Model model,RedirectAttributes redirectAttributes) {
		amazonRefund.setRefundState("1");
		amazonRefund.setOperUser(UserUtils.getUser());
		String ctxPath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/refund/";
		String dateStr = format.format(new Date());
		amazonRefund.setResultFile(dateStr);
		File dir = new File(ctxPath+dateStr);
		dir.mkdirs();
		AmazonAccountConfig config=amazonAccountConfigService.getByName(amazonRefund.getAccountName());
		submit(amazonRefund,dir,config);
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/refund/?country="+checkCountry+"&refundState="+amazonRefund.getRefundState()+"&repage";
	}
	
	@RequiresPermissions("amazoninfo:refund:edit")
	@RequestMapping(value = {"cancel"})
	public String cancel(AmazonRefund amazonRefund,String checkCountry,HttpServletRequest request,HttpServletResponse response, Model model) {
		amazonRefund.setOperUser(UserUtils.getUser());
		amazonRefundService.updateState(amazonRefund);
		sendCheckEmail(amazonRefund,"0");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/refund/?country="+checkCountry+"&refundState="+amazonRefund.getRefundState()+"&repage";
	}
	
	private  boolean sendCheckEmail(AmazonRefund amazonRefund,String type){
		String toAddress ="";
		StringBuffer content= new StringBuffer("");
		if(amazonRefund!=null){
			toAddress =amazonRefund.getCreateUser().getEmail();
			content.append("<p>"+amazonRefund.getOperUser().getName()+("1".equals(type)?"审核 ":"取消 ")+amazonRefund.getAmazonOrderId()+" 亚马逊订单退款"+("1".equals(type)?",退款结果为："+amazonRefund.getStateStr():"")+"</p>");
			content.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '><tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '>");
			content.append("<th>产品名称</th><th>sku</th><th>退款原因</th><th>退款类型</th><th>退款金额</th><th>备注</th></tr>");
			for (AmazoninfoRefundItem item: amazonRefund.getItems()) {
				content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
				content.append("<td>"+item.getProductName()+"</td><td>"+item.getSku()+"</td><td>"+item.getRefundReason()+"</td>");
				content.append("<td>"+item.getRefundType()+"</td><td>"+item.getMoney()+"</td><td>"+item.getRemark()+"</td></tr>");
			}
			content.append("</table>");
		}
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();   
			final MailInfo mailInfo = new MailInfo(toAddress,amazonRefund.getAmazonOrderId()+"亚马逊订单退款审核"+("1".equals(type)?"通过":"取消")+DateUtils.getDate("-yyyy/MM/dd"),date);
			mailInfo.setContent(content.toString());
			new Thread(){
				public void run(){   
					 mailManager.send(mailInfo);
				}
			}.start();
		}
		return true;
	}
	
	@RequestMapping("/download")   
    public ModelAndView download(String fileName, HttpServletRequest request, HttpServletResponse response)   
            throws Exception {   
		fileName = HtmlUtils.htmlUnescape(fileName);
        response.setContentType("text/html;charset=utf-8");   
        request.setCharacterEncoding("UTF-8");   
        java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null;   
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/refund/";  
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
            bos.flush();
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
	
	@RequestMapping(value = "export")
	public String export(AmazonRefund amazonRefund,HttpServletRequest request,HttpServletResponse response, Model model) {
	    
	    Page<AmazonRefund> page = new Page<AmazonRefund>(request, response,-1);
		String orderBy = page.getOrderBy();
		if ("".equals(orderBy)) {
			page.setOrderBy("createDate desc");
		} else {
			page.setOrderBy(orderBy + ",createDate desc");
		}
		page.setPageSize(6000);
		page = amazonRefundService.find(page,amazonRefund);
		page.setOrderBy(orderBy);
		List<AmazonRefund> list=page.getList();
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		List<String> title = Lists.newArrayList("产品名称","订单号","状态","时间", "Sku","Asin","退款类型","亚马逊退款原因","退款金额","实际退款原因");
		
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
		  row.setHeight((short) 600);
		  
		  CellStyle contentStyle = wb.createCellStyle();
		  contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		  contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		  contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		  contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		  contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		  contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		  contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		  contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		  contentStyle.setWrapText(true);
		  
		  
		  HSSFCellStyle cellStyle = wb.createCellStyle();
	        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
	        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
	        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
	        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
	        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
	        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
	        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
	        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
	        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		  HSSFCell cell = null;		
		  for (int i = 0; i < title.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(title.get(i));
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
			}
		  int rowIndex=1;
          if(list!=null){
		    	for (int i=0;i<list.size();i++) {
		    		AmazonRefund refund=list.get(i);
		    		int count=0;
		    		for (AmazoninfoRefundItem item:refund.getItems()) {
		    			int j=0;
		    			row = sheet.createRow(rowIndex++);
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getProductName());
		    			if(count==0){
		    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(refund.getAmazonOrderId());
		    				if(refund.getResult()!=null&&refund.getResult().contains("&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;")){
		    					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("退款成功");
		    				}else{
		    					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("退款失败");
		    				}
		    				
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(refund.getCreateDate()));
		    			}else{
		    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
		    			}
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getSku());
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getAsin());
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getRefundType());
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getRefundReason());
		    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(item.getMoney());
		    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(item.getRemark());
		    			count++;
					}
		    		if(refund.getItems().size()>1){
		    			sheet.addMergedRegion(new CellRangeAddress(rowIndex-refund.getItems().size(), rowIndex-1, 0, 0));
		    			sheet.addMergedRegion(new CellRangeAddress(rowIndex-refund.getItems().size(), rowIndex-1, 1, 1));
		    			sheet.addMergedRegion(new CellRangeAddress(rowIndex-refund.getItems().size(), rowIndex-1, 2, 2));
		    		}
		    		 
				}
		    	
		    	for (int i=0;i<rowIndex-1;i++) {
		        	 for (int j = 0; j < title.size(); j++) {
		        		 if(title.get(j)=="退款金额"){
		        			 sheet.getRow(i+1).getCell(j).setCellStyle(cellStyle);
		        		 }else{
		        			 sheet.getRow(i+1).getCell(j).setCellStyle(contentStyle);
		        		 }
			        	 
					 }
		         }
		    	
		     }
         
	          for (int i = 0; i < title.size(); i++) {
	        		 sheet.autoSizeColumn((short)i);
			  }
	          
			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
		
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		
				String fileName = "refund" + sdf.format(new Date()) + ".xls";
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
	
	private  String submit(final AmazonRefund refund,final File dir,final AmazonAccountConfig config){
		String rs = "正在发到服务器，请等待结果。。。";
		Hibernate.initialize(refund.getItems());
		try {
			if(refund.getId()==null){
				refund.setCreateUser(UserUtils.getUser());
				refund.setCreateDate(new Date());
			}
			refund.setState("1");
			amazonRefundService.save(refund);
			new Thread(){
				public void run() {
					try {
						String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
						Client client = BaseService.getCxfClient(interfaceUrl);
						Object[] str = new Object[]{Global.getConfig("ws.key"), refund.getId()};
						client.invoke("submitRefund", str);
						
						AmazonRefund amazonRefund = amazonRefundService.get(refund.getId());
						//amazoninfo_buy_comment
						AmazonOrder amazonOrder=amazonOrderService.findByEg(amazonRefund.getAmazonOrderId());
						try{
							if(amazonRefund.getResult()!=null&&amazonRefund.getResult().contains("&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;")){
								String amzEmail = amazonOrder.getBuyerEmail();
								if(StringUtils.isNotBlank(amzEmail)){
									AmazonCustomer customer =  amazonCustomerService.getByEg(amzEmail);
									if(customer!=null){
										customer.setRefundMoney(customer.getRefundMoney()+amazonRefund.getRefundTotal());
										List<AmazonBuyComment> list = customer.getBuyComments();
										if(list==null){
											list = Lists.newArrayList();
											customer.setBuyComments(list);
										}
										for (AmazoninfoRefundItem item : amazonRefund.getItems()) {
											list.add(new AmazonBuyComment(new Date(),amazonRefund.getCreateDate(), "4", amazonRefund.getAmazonOrderId(), item.getAsin(), item.getSku(), item.getProductName(),item.getMoney(), item.getId(),  item.getRemark(), customer));
										}
										amazonCustomerService.save(customer);
									}
								}
							}
						}catch(Exception e){
							logger.warn(e.getMessage(), e);
						}
						
						
						
						sendCheckEmail(amazonRefund,"1");
							
						if("0".equals(amazonRefund.getIsTax())&&"3".equals(amazonRefund.getState())
							&&amazonRefund.getResult()!=null&&amazonRefund.getResult().contains("&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;")){
							
							Event event=eventService.isExistEventByOrder(amazonOrder.getAmazonOrderId());
							
							AmazonOrderExtract  orderExtract=amazonOrderService.findRateSnByOrderId(amazonOrder.getAmazonOrderId());
							if(orderExtract!=null&&StringUtils.isNotBlank(orderExtract.getRateSn())){
								amazonOrder.setRateSn(orderExtract.getRateSn());
							}
							AmazonAccountConfig config = amazonAccountConfigService.getByName(orderExtract.getAccountName());
							String invoiceType = config.getInvoiceType();
							String flag= invoiceType.substring(0, invoiceType.lastIndexOf("_"));
							String suffix= invoiceType.substring(invoiceType.lastIndexOf("_")+1);
							
							if(orderExtract==null||StringUtils.isBlank(orderExtract.getInvoiceNo())){
								if((orderExtract.getInvoiceFlag().startsWith("0")&&!"fr".equals(amazonRefund.getCountry()))||
									((orderExtract.getInvoiceFlag().startsWith("00")||orderExtract.getInvoiceFlag().startsWith("10"))&&"fr".equals(amazonRefund.getCountry()))
								 ){//未发送账单
									String invoiceNo=amazonOrderService.createFlowNo(flag,8,suffix);
									amazonOrder.setInvoiceNo(invoiceNo);
									amazonOrderService.updateInvoiceNoById(orderExtract.getInvoiceNo(),orderExtract.getAmazonOrderId()) ;
								}else{
									amazonOrder.setInvoiceNo(orderExtract.getId()+"");
								}
							}else{
								amazonOrder.setInvoiceNo(orderExtract.getInvoiceNo());
							}
							
							File file= SendEmailByOrderMonitor.genTaxRefundPdf(event.getTaxId(),amazonRefund.getRefundTotal(),amazonProductService,amazonRefund.getCountry(), amazonOrder,"0");
							Float avgRate=1f;
							if("uk".equals(amazonRefund.getCountry())){
								avgRate=amazonProduct2Service.findAvgMonthRate("GBP/EUR",amazonOrder.getPurchaseDate());
							}
						
							String[] arr=event.getAttchmentPath().split(",");
							String imgPath="";
							for (String  path : arr) {
								if(path.split("/")[1].startsWith("TP")){
									imgPath=path;
									break;
								}
							}
							File composefile= null;
							if(StringUtils.isNotBlank(imgPath)){
								
								composefile=SendEmailByOrderMonitor.genEuTaxRefundPdf(event.getTaxId(),imgPath,avgRate,amazonRefund.getRefundTotal(),amazonProductService,amazonRefund.getCountry(), amazonOrder,"3");
							}
							
							if (file != null) {
								Map<String, String> params = Maps.newHashMap();
								for (AmazonOrderItem amazonOrderItem : amazonOrder.getItems()) {
									params.put("asin", amazonOrderItem.getAsin());
									break;
								}
								String mail=event.getCustomEmail();
								if(StringUtils.isBlank(mail)){
									mail=amazonOrder.getBuyerEmail();
								}
								String toEmail = mail;
								if(toEmail.indexOf(",")>0){
									toEmail = mail.substring(0,mail.indexOf(","));
								}
								//不管怎样都放入token
								params.put("token",UnsubscribeEmailService.getUnsubscribeEmaliHref(toEmail));
								String template = SendEmailByOrderMonitor.getTemplate("invoice","_" + amazonRefund.getAccountName(), params);
								String subject = "invoice";
								if ("de".equals(amazonRefund.getCountry())) {	subject = "Rechnung";
								} else if ("fr".equals(amazonRefund.getCountry())) {
									subject = "La facture de votre commande";
								}
								MailInfo mailInfo = new MailInfo(mail, subject + " "+ amazonOrder.getAmazonOrderId(), new Date());
								mailInfo.setContent(HtmlUtils.htmlUnescape(template));
								mailInfo.setFileName(file.getName());
								mailInfo.setFilePath(file.getAbsolutePath());
								
								mailInfo.setBccToAddress(UserUtils.getUser().getEmail());
								
								
								MailManagerInfo  info=sendCustomEmailManager.setCustomEmailManager(config.getEmailType(),config.getCustomerEmail(),config.getCustomerEmailPassword());
								sendCustomEmailManager.setManagerInfo(info);
								sendCustomEmailManager.send(mailInfo); 
								sendCustomEmailManager.clearConnection();
								
								
								try{
									String pathName=file.getParentFile().getName()+"/"+file.getName();
									
									if(composefile!=null){
										pathName+=","+composefile.getParentFile().getName()+"/"+composefile.getName();
									}
									event.setAttchmentPath(pathName);
									event.setEndDate(new Date());
									event.setState("2");
									eventService.save(event);
								}catch(Exception e){
									e.printStackTrace();
								}
								
							}
						}
					} catch (Exception e) {
						logger.warn(e.getMessage(), e);
					}
				};
			}.start();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			rs = refund.getAmazonOrderId()+"退款出错了!";
		}
		return rs;
	}
	
	
	private static String getResultStr(File result){
		String rs = "";
		StringBuffer buf= new StringBuffer();
		SAXReader reader = new SAXReader();
		try {
			Document doc = reader.read(result);
			Element root = doc.getRootElement();
			List<Element> msgs = root.elements("Message");
			for (int i = 0; i < msgs.size(); i++) {
				Element el = (Element)msgs.get(i).selectSingleNode("//ProcessingSummary");
				buf.append(HtmlUtils.htmlEscape(el.asXML())+"<br/>");
			}
			rs = buf.toString();
			return rs;
		} catch (DocumentException e) {
			return "解析结果文件出错！请下载查看";
		}
	}
}
