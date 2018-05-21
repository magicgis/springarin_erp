package com.springrain.erp.modules.custom.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.email.MailManagerInfo;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.FreeMarkers;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderExtract;
import com.springrain.erp.modules.amazoninfo.scheduler.SendEmailByOrderMonitor;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.custom.entity.Comment;
import com.springrain.erp.modules.custom.entity.CustomEmail;
import com.springrain.erp.modules.custom.entity.CustomEmailTemplate;
import com.springrain.erp.modules.custom.entity.Event;
import com.springrain.erp.modules.custom.entity.SendEmail;
import com.springrain.erp.modules.custom.entity.Signature;
import com.springrain.erp.modules.custom.scheduler.CustomEmailManager;
import com.springrain.erp.modules.custom.scheduler.CustomEmailMonitor;
import com.springrain.erp.modules.custom.scheduler.CustomNoReplyEmailManager;
import com.springrain.erp.modules.custom.service.CommentService;
import com.springrain.erp.modules.custom.service.CustomEmailService;
import com.springrain.erp.modules.custom.service.CustomEmailTemplateService;
import com.springrain.erp.modules.custom.service.EventService;
import com.springrain.erp.modules.custom.service.SendEmailService;
import com.springrain.erp.modules.custom.service.SignatureService;
import com.springrain.erp.modules.custom.service.UnsubscribeEmailService;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PurchaseOrder;
import com.springrain.erp.modules.psi.entity.PurchaseOrderItem;
import com.springrain.erp.modules.psi.service.FbaInboundService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.PurchaseOrderService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 发送邮件Controller
 * @author tim
 * @version 2014-05-13
 */
@Controller
@RequestMapping(value = "${adminPath}/custom/sendEmail")
public class SendEmailController extends BaseController {
	
	@Autowired
	private CustomEmailManager customEmailManager;
	
	@Autowired
	private MailManager mailManager;
	
	@Autowired
	private SendEmailService sendEmailService;
	
	@Autowired
	private SignatureService signatureService;
	
	@Autowired
	private EventService eventService;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private CustomEmailService customEmailService;
	
	@Autowired
	private AmazonOrderService orderService;
	
	@Autowired
	private PsiSupplierService psiSupplierService;
	
	@Autowired
	private PurchaseOrderService purchaseOrderService;
	
	@Autowired
	private PsiProductService productService;
	
	@Autowired
	private CustomEmailTemplateService customEmailTemplateService;
	
	@Autowired
	private FbaInboundService fbaInboundService;
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	@Autowired
	private AmazonProductService amazonProductService;
	
	@Autowired
	private CustomNoReplyEmailManager  noReplyEmailManager;
	
	@ModelAttribute
	public SendEmail get(@RequestParam(required=false) String id) {
		if (!StringUtils.isBlank(id)){
			return sendEmailService.get(id);
		}else{
			return new SendEmail();
		}
	}
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = {"list", ""})
	public String list(SendEmail sendEmail, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			sendEmail.setCreateBy(user);
		}
		Page<SendEmail> page = new Page<SendEmail>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("sentDate desc");
		}else{
			page.setOrderBy(orderBy+",sentDate desc");
		}
		if(StringUtils.isBlank(sendEmail.getSendFlag())){
			sendEmail.setSendFlag("1");
		}
        page = sendEmailService.find(page, sendEmail);
        page.setOrderBy(orderBy);
        model.addAttribute("page", page);
		return "modules/custom/sendEmailList";
	}
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = {"draft"})
	public String draftList(SendEmail sendEmail, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			sendEmail.setCreateBy(user);
		}
		Page<SendEmail> page = new Page<SendEmail>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("sentDate desc");
		}else{
			page.setOrderBy(orderBy+",sentDate desc");
		}	
		sendEmail.setSendFlag("0");
        page = sendEmailService.find(page, sendEmail); 
        page.setOrderBy(orderBy);
        model.addAttribute("page", page);
		return "modules/custom/draftEmailList";
	}
	
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = {"checkList"})
	public String checkList(SendEmail sendEmail, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		Page<SendEmail> page = new Page<SendEmail>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("sentDate desc");
		}else{
			page.setOrderBy(orderBy+",sentDate desc");
		}	
		
        page = sendEmailService.findCheck(page, sendEmail); 
        page.setOrderBy(orderBy);
        model.addAttribute("page", page);
		return "modules/custom/checkEmailList";
	}

	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "form")
	public String form(SendEmail sendEmail, @RequestParam(required=false)String eventId,Model model,String country,String checkEmail) {
		
		
		User user = UserUtils.getUser();
		if(!"2".equals(sendEmail.getType())){
			if(sendEmail.getId()==null){
				SendEmail sendEmail2 = sendEmailService.findBlankEmail(sendEmail.getSendEmail());
				if(sendEmail2==null){
					sendEmail.setCreateBy(user);
					Signature signature = signatureService.get(sendEmail.getCreateBy().getId());
					if(signature!=null&&signature.getSignatureContent().length()>0){
						sendEmail.setSendContent("<br/><br/><br/><br/>--------------------<br/>"+signature.getSignatureContent());
					}
					sendEmailService.save(sendEmail);
				}else{
					if(StringUtils.isNotEmpty(sendEmail.getSendEmail())){
						sendEmail2.setSendEmail(sendEmail.getSendEmail());
					}
					sendEmail = sendEmail2;
				}
			}
			if(StringUtils.isNotEmpty(eventId)){
				model.addAttribute("eventId", eventId);
			}
			sendEmail.setType("0");
			if(StringUtils.isNotBlank(sendEmail.getServerEmail())){
				String accountName=amazonAccountConfigService.findAccountByEmail(sendEmail.getServerEmail());
				if(StringUtils.isNotBlank(accountName)){
					sendEmail.setAccountName(accountName);
				}
			}else if(StringUtils.isNotEmpty(eventId)){
				Event event=eventService.get(Integer.parseInt(eventId));
				if(event!=null&&StringUtils.isNotBlank(event.getAccountName())){
					sendEmail.setAccountName(event.getAccountName());
				}
			}
			
		}else{
			//往采购供应商发信 获取供应商模板
			String suppId = sendEmail.getSendEmail();
			PsiSupplier psi = psiSupplierService.get(Integer.parseInt(suppId));
			if(psi==null){
				throw new RuntimeException("供应商不存在！");
			}
			Map<String,Object> params = Maps.newHashMap();
			String template = "";
			try {
				params.put("supplier", psi);
				params.put("cuser", user);
				template = PdfUtil.getPsiTemplate("orderEmail.ftl", params);
			} catch (Exception e) {
				logger.error("", e);
			}
			sendEmail.setSendContent(template);
			sendEmail.setSendEmail(psi.getMail());
			model.addAttribute("orderId", sendEmail.getSendSubject());
			sendEmail.setSendSubject("新订单PN"+sendEmail.getSendSubject()+"("+DateUtils.getDate()+")");
		}
		
		if(StringUtils.isNotBlank(sendEmail.getAccountName())){
			model.addAttribute("emailList",amazonAccountConfigService.findEmailByAccountName(sendEmail.getAccountName()));
		}
		model.addAttribute("accountMap",amazonAccountConfigService.findCountryByAccount());
		//生成邮件模板
		List<CustomEmailTemplate> templates = this.customEmailTemplateService.find(user);		
		model.addAttribute("templates", templates);
		model.addAttribute("sendEmail", sendEmail);
		if("2".equals(checkEmail)/*&&"uk,com,ca".contains(country)*/){//需审核
			/*if(StringUtils.isBlank(country)){
				country="com";
			}*/
			List<User> userList=systemService.hasPerssion("custom:emailCheck:"+country);
			model.addAttribute("userList", userList);
			model.addAttribute("checkEmail",checkEmail);
			model.addAttribute("checkCountry",country);
		}
		
		return "modules/custom/sendEmail";
	}
	
	
	@ResponseBody
	@RequestMapping(value = "findEmailByAccount")
	public List<String> findEmailByAccount(String accountName,Model model) {
		return amazonAccountConfigService.findEmailByAccount(accountName);
	}
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "recall")
	public String recall(SendEmail sendEmail, @RequestParam(required=false)String eventId, Model model, RedirectAttributes redirectAttributes,String country,String checkEmail) {
		SendEmail sendEmail2  =  sendEmailService.findBlankEmail(sendEmail.getSendEmail());
		SendEmail newSendEmail = null;
		User user = UserUtils.getUser();
		if(sendEmail2==null){
			newSendEmail = new SendEmail();
			newSendEmail.setType("0");
			newSendEmail.setCreateBy(user);
			Signature signature = signatureService.get(sendEmail.getCreateBy().getId());
			newSendEmail.setSendSubject("RE:"+sendEmail.getSendSubject());
			if(signature!=null&&signature.getSignatureContent().length()>0){
				newSendEmail.setSendContent("<br/><br/><br/><br/>--------------------<br/>"+signature.getSignatureContent());
			}	
			newSendEmail.setSendContent("<br/>------------------ Original ------------------<br/>"+sendEmail.getSendContent());
			newSendEmail.setSendEmail(sendEmail.getSendEmail());
			newSendEmail.setCcToEmail(sendEmail.getCcToEmail());
			newSendEmail.setBccToEmail(sendEmail.getBccToEmail());
			sendEmailService.save(newSendEmail);
		}else{
			newSendEmail = sendEmail2;
		}
		if(StringUtils.isNotBlank(newSendEmail.getServerEmail())){
			String accountName=amazonAccountConfigService.findAccountByEmail(newSendEmail.getServerEmail());
			if(StringUtils.isNotBlank(accountName)){
				newSendEmail.setAccountName(accountName);
			}
		}
		if(StringUtils.isNotEmpty(eventId)){
			Event event=eventService.get(Integer.parseInt(eventId));
			if(event!=null&&StringUtils.isNotBlank(event.getAccountName())){
				newSendEmail.setAccountName(event.getAccountName());
			}
			model.addAttribute("eventId", eventId);
		}
		
		
		if(StringUtils.isBlank(newSendEmail.getAccountName())&&StringUtils.isNotBlank(sendEmail.getAccountName())){
			newSendEmail.setAccountName(sendEmail.getAccountName());
		}
		model.addAttribute("sendEmail", newSendEmail);
		if(StringUtils.isNotBlank(newSendEmail.getAccountName())){
			model.addAttribute("emailList",amazonAccountConfigService.findEmailByAccountName(newSendEmail.getAccountName()));
		}
		
		model.addAttribute("accountMap",amazonAccountConfigService.findCountryByAccount());
		
		List<CustomEmailTemplate> templates = this.customEmailTemplateService.find(user);		
		model.addAttribute("templates", templates);
		if("2".equals(checkEmail)/*&&"uk,com,ca".contains(country)*/){//需审核
			List<User> userList=systemService.hasPerssion("custom:emailCheck:"+country);
			model.addAttribute("userList", userList);
			model.addAttribute("checkEmail",checkEmail);
			model.addAttribute("checkCountry",country);
		}
		return "modules/custom/sendEmail";
	}
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "saveSendEmail")
	public String saveSendEmail(@RequestParam(required=false)String fromEmail,@RequestParam(value="attchmentFile",required=false)MultipartFile[] attchmentFiles,SendEmail sendEmail,@RequestParam(required=false)String orderId,@RequestParam(required=false)String eventId,@RequestParam(required=false)String level,@RequestParam(required=false)String sid,Model model, RedirectAttributes redirectAttributes,HttpServletRequest request) throws Exception {
		sendEmail.setSendSubject(HtmlUtils.htmlUnescape(sendEmail.getSendSubject()));
		sendEmail.setSendContent(HtmlUtils.htmlUnescape(sendEmail.getSendContent()));
		sendEmail.setSendContent(CustomEmailMonitor.stringFilter(sendEmail.getSendContent()));
		Map<String,String> emailMap=amazonAccountConfigService.findAllEmailByServer();
		for (Map.Entry<String,String> entry : emailMap.entrySet()) {
			if(StringUtils.isNotBlank(sendEmail.getSendContent())){
				sendEmail.setSendContent(sendEmail.getSendContent().replaceAll(entry.getKey(), entry.getValue()));
			}
			if(StringUtils.isNotBlank(sendEmail.getSendSubject())){
				sendEmail.setSendSubject(sendEmail.getSendSubject().replaceAll(entry.getKey(), entry.getValue()));
			}
		}
		if(StringUtils.isEmpty(sendEmail.getSendEmail())){
			addMessage(redirectAttributes, "'"+sendEmail.getSendSubject()+"'Mail failed!");
			return "redirect:"+Global.getAdminPath()+"/psi/fbaInbound?repage";
		}
		//发送邮件
		MailInfo mailInfo = sendEmail.getMailInfo();
		mailInfo.setFromServer(fromEmail);
		if("1".equals(sendEmail.getTaxInvoice())&&StringUtils.isNotBlank(sendEmail.getOrderId())){
			AmazonOrder amazonOrder = orderService.findByEg(sendEmail.getOrderId().trim());
			if(amazonOrder != null ){
				try{
					String country= amazonOrder.getSalesChannel().substring(amazonOrder.getSalesChannel().lastIndexOf(".") + 1);
					AmazonOrderExtract  orderExtract=orderService.findRateSnByOrderId(amazonOrder.getAmazonOrderId());
					if(orderExtract!=null){
						if(StringUtils.isNotBlank(orderExtract.getRateSn())){
							amazonOrder.setRateSn(orderExtract.getRateSn());
						}
						AmazonAccountConfig config = amazonAccountConfigService.getByName(orderExtract.getAccountName());
						String invoiceType = config.getInvoiceType();
						String flag= invoiceType.substring(0, invoiceType.lastIndexOf("_"));
						String suffix= invoiceType.substring(invoiceType.lastIndexOf("_")+1);
						
						if(StringUtils.isBlank(orderExtract.getInvoiceNo())){
							if((orderExtract.getInvoiceFlag().startsWith("0")&&!"fr".equals(country))||
								((orderExtract.getInvoiceFlag().startsWith("00")||orderExtract.getInvoiceFlag().startsWith("10"))&&"fr".equals(country))
							 ){//未发送账单
								String invoiceNo=orderService.createFlowNo(flag,8,suffix);
								amazonOrder.setInvoiceNo(invoiceNo);
								amazonOrder.setPrintDate(new Date());
								orderService.updateInvoiceNoById(amazonOrder.getInvoiceNo(),amazonOrder.getAmazonOrderId()) ;
							}else{
								amazonOrder.setInvoiceNo(orderExtract.getId()+"");
							}
						}else{
							amazonOrder.setInvoiceNo(orderExtract.getInvoiceNo());
							amazonOrder.setPrintDate(orderExtract.getPrintDate());
						}
						
						File file = SendEmailByOrderMonitor.genPdf(amazonProductService,country, amazonOrder, "1");
						if(file!=null){
							String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/customEmail";
							String uuid = UUID.randomUUID().toString();
							File baseDir = new File(baseDirStr+"/"+uuid); 
							if(!baseDir.isDirectory())
								baseDir.mkdirs();
							String name = file.getName();
							name = HtmlUtils.htmlUnescape(name);
							File dest = new File(baseDir,name);
							mailInfo.setFileName(file.getName());
							mailInfo.setFilePath(dest.getAbsolutePath());
							FileUtils.copyInputStreamToFile(new FileInputStream(file),dest);
							sendEmail.setSendAttchmentPath(uuid+"/"+name);
						}
					}
				}catch(Exception e){logger.error("Note:邮件发送账单异常",e);}
			}
		}
		if(attchmentFiles!=null){
			for (MultipartFile attchmentFile : attchmentFiles) {
				if(attchmentFile.getSize()!=0){
					String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/customEmail";
					String uuid = UUID.randomUUID().toString();
					File baseDir = new File(baseDirStr+"/"+uuid); 
					if(!baseDir.isDirectory())
						baseDir.mkdirs();
					String name = attchmentFile.getOriginalFilename();
					name = HtmlUtils.htmlUnescape(name);
					File dest = new File(baseDir,name);
					mailInfo.setFileName(attchmentFile.getOriginalFilename());
					mailInfo.setFilePath(dest.getAbsolutePath());
					try {
						FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
						sendEmail.setSendAttchmentPath(uuid+"/"+name);
					} catch (IOException e) {
						logger.warn(name+"文件保存失败",e);
					}
				}
			}
		}
		boolean flag = false;
		//fba贴提醒信件,不需要存储
		if("-1".equals(sendEmail.getType())){
			mailInfo.setFromServer(null);
			if ("p0".equals(level)) {
				sendEmail.setCreateBy(UserUtils.getUser());
				sendEmail.setServerEmail(fromEmail);
				sendEmail.setSentDate(new Date());
				if (StringUtils.isEmpty(sendEmail.getCcToEmail())) {
					sendEmail.setCcToEmail("george@inateck.com,ethan@inateck.com,bella@inateck.com,"+UserUtils.logistics1);
				} else {
					sendEmail.setCcToEmail(sendEmail.getCcToEmail()+",george@inateck.com,ethan@inateck.com,bella@inateck.com,"+UserUtils.logistics1);
				}
				sendEmail.setSendSubject(sendEmail.getSendSubject().replace("[P]", "[P0]"));
				sendEmail.setSendContent(sendEmail.getSendContent().replace("[P]", "[P0]"));
				sendEmail.setCheckState("4"); //需要运营审核的邮件
				sendEmail.setType("0");
				sendEmailService.save(sendEmail);
				fbaInboundService.upLoadEmailFlag(sid, "0");
				//发送P0审核通知
				List<User> users = systemService.findUserByPermission("fbap0:email:approve");
				if(users!=null && users.size()>0){
					String  toAddress="";
					StringBuffer buf= new StringBuffer();
					for (User user : users) {
						buf.append(user.getEmail()+",");
					}
					toAddress = buf.toString();
					toAddress = toAddress.substring(0,toAddress.length()-1);
					String content = "<p><span style='font-size:20px'>Hi all,<br/>&nbsp;&nbsp;&nbsp;&nbsp;"+sendEmail.getCreateBy().getName()+"申请了一封FBA P0贴邮件,邮件内容如下,请尽快处理。</span>" +
	            			"<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/custom/sendEmail/checkList'>点击审批</a></p>";
	    			final MailInfo notice = new MailInfo(toAddress,"FBA P0贴审核提醒("+DateUtils.getDate("yyyy/MM/dd")+")",new Date());
	    			notice.setContent(content + sendEmail.getSendContent());
	    			new Thread(){
	    			    public void run(){
	    			    	mailManager.send(notice);
	    				}
	    			}.start();
				}
				addMessage(redirectAttributes, "'"+sendEmail.getSendSubject()+"'Email saved successfully but also needs to be reviewed");
				return "redirect:"+Global.getAdminPath()+"/psi/fbaInbound?repage";
			} else {
				mailInfo.setSubject(mailInfo.getSubject().replace("[P]", "[P1]"));
				mailInfo.setContent(mailInfo.getContent().replace("[P]", "[P1]"));
				flag = mailManager.send(mailInfo);
				if(flag){
					fbaInboundService.upLoadEmailFlag(sid, "1");
					addMessage(redirectAttributes, "'"+mailInfo.getSubject()+"'Email sent successfully");
				}else{
					addMessage(redirectAttributes, "'"+mailInfo.getSubject()+"'Mail failed!");
				}
				return "redirect:"+Global.getAdminPath()+"/psi/fbaInbound?repage";
			}
		}
		User user = UserUtils.getUser();
		if(sendEmail.getCreateBy()==null){
			sendEmail.setCreateBy(user);
		}
		CustomEmail email =  sendEmail.getCustomEmail();
		if(email!=null&&email.getMasterBy()!=null){
			if(!email.getMasterBy().getId().equals(user.getId())){
				if(SecurityUtils.getSubject().isPermitted("custom:email:proxy")){
					email.setAnswerDate(new Date());
					email.setMasterBy(user);
				}else{
					throw new RuntimeException("Email is masterBy "+email.getMasterBy().getName()+",You do not have permission！");
				}
			}
		}
		
		PurchaseOrder purchaseOrder = null;
		String checkEmail="";
		if("2".equals(sendEmail.getType())){
			//向供应商发送邮件 加入附件和抄送人
			String address = mailInfo.getBccToAddress();
			if(StringUtils.isNotBlank(address)){
				address +=(","+UserUtils.getUser().getEmail()+",lynn@inateck.com,frank@inateck.com,sophie@inateck.com");
			}else{
				address = UserUtils.getUser().getEmail()+",lynn@inateck.com,frank@inateck.com,sophie@inateck.com";
			}
			mailInfo.setBccToAddress(address);
			sendEmail.setBccToEmail(address);
			//增加附件
			purchaseOrder = purchaseOrderService.get(orderId);
			purchaseOrder.setSendEmailFlag("1");
			for (PurchaseOrderItem item : purchaseOrder.getItems()) {
				item.setProduct(productService.get(item.getProduct().getId()));
			}
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/customEmail";
			String uuid = UUID.randomUUID().toString();
			File baseDir = new File(baseDirStr+"/"+uuid); 
			if(!baseDir.isDirectory())
				baseDir.mkdirs();
			File pdfFile = new File(baseDir, purchaseOrder.getOrderNo() + ".pdf");
			Map<String,String> versionMap = productService.getFnskuAndSkuVersionMap();
			PdfUtil.genPurchaseOrderPdf(pdfFile, purchaseOrder,versionMap);
			mailInfo.setFileName(purchaseOrder.getOrderNo() + ".pdf");
			mailInfo.setFilePath(pdfFile.getAbsolutePath());
			sendEmail.setSendAttchmentPath(uuid+"/"+purchaseOrder.getOrderNo() + ".pdf");
			flag = mailManager.send(mailInfo);
		}else{

			if(sendEmail.getSendEmail().contains("marketplace.amazon.")){//发往亚马逊个人邮件需要审核
				if(sendEmail.getSendEmail().contains(",")){
					String[] emailArr=sendEmail.getSendEmail().split(",");
					String tempEmail="";
					for(String arr:emailArr){
						 if(arr.startsWith("erp")){
							 String[] temp=arr.split("@");
							 arr=new String(Encodes.decodeBase64(temp[0].substring(3)))+"@"+temp[1];
						 }else{
							  List<String> customerEmail=orderService.findServerByEmail(sendEmail.getSendEmail());
						      if(customerEmail!=null&&customerEmail.size()>0&&!customerEmail.contains(fromEmail)){
								 throw new RuntimeException("Please choose "+customerEmail.get(0)+" reply mail！！！");
							  }
						 }
						 tempEmail+=arr+",";
					}
					sendEmail.setSendEmail(tempEmail.substring(0,tempEmail.length()-1));
				}else{
					List<String> customerEmail=orderService.findServerByEmail(sendEmail.getSendEmail());
					if(customerEmail!=null&&customerEmail.size()>0&&!customerEmail.contains(fromEmail)){
						throw new RuntimeException("Please choose "+customerEmail.get(0)+" reply mail！！！");
					}
					if(sendEmail.getSendEmail().startsWith("erp")){
						 String mail=sendEmail.getSendEmail();
						 String[] temp=mail.split("@");
						 mail=new String(Encodes.decodeBase64(temp[0].substring(3)))+"@"+temp[1];
						 sendEmail.setSendEmail(mail);
					}
				}
				mailInfo.setToAddress(sendEmail.getSendEmail());
				
				boolean eventFlag=sendEmailService.findEvent(sendEmail.getSendEmail());
				if( (eventFlag&&!SecurityUtils.getSubject().isPermitted("custom:noCheck:eventEmail"))//找到差评
						||(!eventFlag&&!SecurityUtils.getSubject().isPermitted("custom:noCheck:email")) ){
					
					String country = sendEmail.getSendEmail().substring(sendEmail.getSendEmail().lastIndexOf(".") + 1);
					sendEmail.setCountry(country);
					sendEmail.setSendFlag("0");//未发送
					sendEmail.setCheckState("0");//0:未审核
					flag=true;
					checkEmail="2";
				}else{
					AmazonAccountConfig config=amazonAccountConfigService.findEmail(fromEmail);
					if(config==null){
						throw new RuntimeException(fromEmail+":Mailbox server is not configured！！！");
					}
					MailManagerInfo  info=customEmailManager.setCustomEmailManager(config.getEmailType(),fromEmail,config.getCustomerEmailPassword());
					customEmailManager.setManagerInfo(info);
					flag = customEmailManager.send(mailInfo);
					customEmailManager.clearConnection();
					sendEmail.setSendFlag("1");
					sendEmail.setCheckState("3");
				}
			
			}else{
				
				if(sendEmail.getSendEmail().contains(",")){
					String[] emailArr=sendEmail.getSendEmail().split(",");
					String tempEmail="";
					for(String arr:emailArr){
						 if(arr.startsWith("erp")){
							 String[] temp=arr.split("@");
							 arr=new String(Encodes.decodeBase64(temp[0].substring(3)))+"@"+temp[1];
						 }
						 tempEmail+=arr+",";
					}
					sendEmail.setSendEmail(tempEmail.substring(0,tempEmail.length()-1));
				}else{
					if(sendEmail.getSendEmail().startsWith("erp")){
						 String mail=sendEmail.getSendEmail();
						 String[] temp=mail.split("@");
						 mail=new String(Encodes.decodeBase64(temp[0].substring(3)))+"@"+temp[1];
						 
						sendEmail.setSendEmail(mail);
					}
				}
				mailInfo.setToAddress(sendEmail.getSendEmail());
				
				if(!fromEmail.contains("@inateck")&&!sendEmail.getSendEmail().contains("amazon")){
					flag = noReplyEmailManager.send(mailInfo);
					logger.info("私人邮箱:"+fromEmail+"发送"+sendEmail.getSendEmail());
				}else{
					AmazonAccountConfig config=amazonAccountConfigService.findEmail(fromEmail);
					if(config==null){
						throw new RuntimeException(fromEmail+":Mailbox server is not configured！！！");
					}
					MailManagerInfo  info=customEmailManager.setCustomEmailManager(config.getEmailType(),fromEmail,config.getCustomerEmailPassword());
					customEmailManager.setManagerInfo(info);
					flag = customEmailManager.send(mailInfo);
					customEmailManager.clearConnection();
				}
				sendEmail.setSendFlag("1");
				sendEmail.setCheckState("3");
				
			}
		}
		sendEmail.setServerEmail(fromEmail);
		sendEmail.setSentDate(new Date());
		if(flag){
			if("1".equals(sendEmail.getType())){
				if(email!=null){
					if(email.getEndDate()==null){
						email.setEndDate(new Date());
					}
					email.setState("2");
					email.setUpdateDate(new Date());
				}
			}else if("2".equals(sendEmail.getType())){
				purchaseOrderService.save(purchaseOrder);
			}
			//未知原因，信的内容没了
			if(StringUtils.isEmpty(sendEmail.getSendContent())){
				sendEmail.setSendContent(mailInfo.getContent());
				sendEmail.setSendSubject(mailInfo.getSubject());
				sendEmail.setCreateBy(user);
			}
			
			sendEmailService.save(sendEmail);
			//保存备注 michael
			if(sendEmail.getCustomEmail()!=null&&sendEmail.getCustomEmail().getId()!=null){
				customEmailService.saveRemark(sendEmail.getRemark(),sendEmail.getCustomEmail().getId());
			}
			if("2".equals(checkEmail)){
				addMessage(redirectAttributes, "'"+mailInfo.getSubject()+"'Email saved successfully but also needs to be reviewed");
			}else{
				addMessage(redirectAttributes, "'"+mailInfo.getSubject()+"'Email sent successfully");
			}
		}else{
			addMessage(redirectAttributes, "'"+mailInfo.getSubject()+"'Mail failed!");
		}
		
		/*if("2".equals(checkEmail)){
			sendEmail(sendEmail);
		}*/
		
		if("1".equals(sendEmail.getType())){
			Event event = eventService.findEvent(sendEmail.getSendEmail(), UserUtils.getUser());
			if(event!=null){
				String common = "";
				if(sendEmail.getCustomEmail()!=null){
					common = UserUtils.getUser().getName()+" reply to an email to customers<a href='../emailManager/view?id="+sendEmail.getCustomEmail().getId()+"'>view</a>" ;
				}else{
					common = UserUtils.getUser().getName()+" reply to an email to customers<a href='../sendEmail/view?id="+sendEmail.getId()+"'>view</a>";
				}
				Comment comm = new Comment();
				comm.setComment(common);
				comm.setType("1");
				comm.setCreateBy(UserUtils.getUserById("1"));
				comm.setEvent(event);
				commentService.save(comm);
			}
		}
		if(StringUtils.isNotEmpty(eventId)){
			Comment comm = new Comment();
			comm.setComment(UserUtils.getUser().getName()+" sent to an email to customers<a href='../sendEmail/view?id="+sendEmail.getId()+"' userId='"+UserUtils.getUser().getId()+"' id='"+sendEmail.getId()+"'>View</a>");
			comm.setType("1");
			comm.setCreateBy(UserUtils.getUserById("1"));
			Event event = eventService.get(Integer.parseInt(eventId));
			if("0".equals(event.getState())){
				event.setState("1");
				eventService.save(event);
			}
			comm.setEvent(event);
			commentService.save(comm);
			return "redirect:"+Global.getAdminPath()+"/custom/event/form?id="+eventId;
		}
		if("1".equals(sendEmail.getType())){
			return "redirect:"+Global.getAdminPath()+"/custom/emailManager/?repage";
		}else if("2".equals(sendEmail.getType())){
			return "redirect:"+Global.getAdminPath()+"/psi/purchaseOrder?repage";
		}else{
			if("2".equals(checkEmail)){
				return "redirect:"+Global.getAdminPath()+"/custom/sendEmail/checkList?repage";
			}else{
				return "redirect:"+Global.getAdminPath()+"/custom/sendEmail/?repage";
			}
		}
	}
	
	private  boolean sendEmail(SendEmail email){
		String toAddress ="";
		StringBuffer content= new StringBuffer("");
		if(email!=null){
			toAddress=systemService.getUser(email.getCheckUser().getId()).getEmail();
			content.append("<p>请审核"+email.getCreateBy().getName()+"提交的差评邮件申请,<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/custom/sendEmail/view?id="+email.getId()+"'>"+email.getSendSubject()+"点击审核</a></p>");
		}
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();   
			final MailInfo mailInfo = new MailInfo(toAddress,"差评邮件审核通知"+email.getSendSubject()+DateUtils.getDate("-yyyy/MM/dd"),date);
			mailInfo.setContent(content.toString());
			new Thread(){
				public void run(){   
					 mailManager.send(mailInfo);
				}
			}.start();
		}
		return true;
	}
	private static final String TEMPLATE_PATH = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+"/WEB-INF/classes/templates/email";  
	
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "template")
	public String returnGoodsTemplate(String customEmailId,RedirectAttributes redirectAttributes) {
		CustomEmail email = customEmailService.get(customEmailId);
		if(email==null){
			throw new RuntimeException("Customer-email does not exist!");
		}
		String key = email.getRevertEmail();
		key = key.substring(key.lastIndexOf(".")+1);
		Set<String> ordersStr = CustomEmailController.getOrders(email.getSubject());
		ordersStr.addAll(CustomEmailController.getOrders(email.getReceiveContent()));
		Set<String> emails = Sets.newHashSet();
		String mail = "";
		for (String ordStr : ordersStr) {
			AmazonOrder order = orderService.findByLazy(ordStr);
			if(order!=null){
				emails.add(order.getBuyerEmail());
				mail = order.getBuyerEmail();
			}
		}
		String content = getTemplate(mail,"replyReturnGoods","_"+key);
		if(StringUtils.isEmpty(content)){
			throw new RuntimeException("Template does not exist!");
		}
		content = content + "<br/>------------------ Original ------------------<br/>"+email.getReceiveContent();
		if(emails.size()==0){
			throw new RuntimeException("Order number does not exist, can not be sent!");
		}
		String toEmail = emails.toString();
		toEmail = toEmail.substring(1,toEmail.length()-1);
		SendEmail sendEmail = new SendEmail("RE:"+email.getSubject(),content,toEmail,"1");
		//发送邮件
		MailInfo mailInfo = sendEmail.getMailInfo();
		User user = UserUtils.getUser();
		if(sendEmail.getCreateBy()==null){
			sendEmail.setCreateBy(user);
		}
		if(!email.getMasterBy().getId().equals(user.getId())){
			if(SecurityUtils.getSubject().isPermitted("custom:email:proxy")){
				email.setMasterBy(user);
			}else{
				throw new RuntimeException("Email is masterBy "+email.getMasterBy().getName()+",You do not have permission！");
			}
		}
		String fromEmail=email.getRevertServerEmail();
		AmazonAccountConfig config=amazonAccountConfigService.findEmail(fromEmail);
		if(config==null){
			throw new RuntimeException(fromEmail+":Mailbox server is not configured！！！");
		}
		MailManagerInfo  info=customEmailManager.setCustomEmailManager(config.getEmailType(),fromEmail,config.getCustomerEmailPassword());
		customEmailManager.setManagerInfo(info);
		
		if(customEmailManager.send(mailInfo)){
			customEmailManager.clearConnection();
			sendEmail.setSentDate(new Date());
			sendEmail.setSendFlag("1");
			sendEmail.setCustomEmail(email);
			if(email.getEndDate()==null){
				email.setEndDate(new Date());
			}
			email.setState("2");
			email.setUpdateDate(new Date());
			sendEmailService.save(sendEmail);
			addMessage(redirectAttributes, "'"+mailInfo.getSubject()+"'Email sent successfully");
		}else{
			addMessage(redirectAttributes, "'"+mailInfo.getSubject()+"'Mail failed, probably because the network is not clear, please try again later!");
		};
		return "redirect:"+Global.getAdminPath()+"/custom/emailManager/?repage";
	}
	
	
	@ResponseBody
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "note")
	public String note(SendEmail sendEmail) {
		if(sendEmail.getCreateBy()==null){
			sendEmail.setCreateBy(UserUtils.getUser());
		}
		sendEmail.setSendContent(HtmlUtils.htmlUnescape(sendEmail.getSendContent()));
		sendEmail.setSendContent(Encodes.filterOffUtf8Mb4(sendEmail.getSendContent()));
		if(StringUtils.isNotBlank(sendEmail.getSendEmail())){
			if(sendEmail.getSendEmail().contains(",")){
				String[] emailArr=sendEmail.getSendEmail().split(",");
				String tempEmail="";
				for(String arr:emailArr){
					 if(arr.startsWith("erp")){
						 String[] temp=arr.split("@");
						 arr=new String(Encodes.decodeBase64(temp[0].substring(3)))+"@"+temp[1];
					 }
					 tempEmail+=arr+",";
				}
				sendEmail.setSendEmail(tempEmail.substring(0,tempEmail.length()-1));
			}else{
				if(sendEmail.getSendEmail().startsWith("erp")){
					 String mail=sendEmail.getSendEmail();
					 String[] temp=mail.split("@");
					 mail=new String(Encodes.decodeBase64(temp[0].substring(3)))+"@"+temp[1];
					 sendEmail.setSendEmail(mail);
				}
			}
			sendEmailService.save(sendEmail);
			return "1";
		}
		return "0";
	}
	
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "checkState")
	public String checkState(final SendEmail sendEmail,String oldCheckState,RedirectAttributes redirectAttributes) {
		String rs="";
		if("1".equals(sendEmail.getCheckState())){//pass
			final MailInfo mailInfo = sendEmail.getMailInfo();
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/customEmail";
			if(StringUtils.isNotBlank(sendEmail.getSendAttchmentPath())){
				String[] arr=sendEmail.getSendAttchmentPath().split(",");
				for (String file: arr) {
					mailInfo.setFileName(StringUtils.substringAfterLast(file, "/"));
					mailInfo.setFilePath(baseDirStr+"/"+file);
				}
			}
			
			rs= "1";
			
			final String fromEmail=sendEmail.getServerEmail();
			
			if(StringUtils.isBlank(fromEmail)&&"4".equals(oldCheckState)){
				boolean flag=mailManager.send(mailInfo);
				if(flag){
					sendEmail.setSendFlag("1");
					sendEmail.setCheckState("1");
					sendEmail.setSendFlag("1");
					sendEmail.setCheckUser(UserUtils.getUser());
					sendEmailService.save(sendEmail);
				}
			}else{
				final AmazonAccountConfig config=amazonAccountConfigService.findEmail(fromEmail);
				if(config==null){
					throw new RuntimeException(fromEmail+":Mailbox server is not configured！！！");
				}
				sendEmail.setCheckState("1");
				sendEmail.setSendFlag("0");
				sendEmail.setCheckUser(UserUtils.getUser());
				sendEmailService.save(sendEmail);
				rs= "1";
					
				new Thread(){
					public void run(){   
						MailManagerInfo  info=customEmailManager.setCustomEmailManager(config.getEmailType(),fromEmail,config.getCustomerEmailPassword());
						customEmailManager.setManagerInfo(info);
						boolean flag=customEmailManager.send(mailInfo);
						customEmailManager.clearConnection();
						if(flag){
							sendEmail.setSendFlag("1");
							sendEmailService.save(sendEmail);
						}
					}
				}.start();
			}
			
			//sendCheckEmail(sendEmail,"通过",rs);
		}else{
			sendEmail.setCheckState("2");
			sendEmail.setCheckUser(UserUtils.getUser());
			sendEmailService.save(sendEmail);
			rs="2";
			sendCheckEmail(sendEmail,"The mail was checked not passed, you need to edit the mail again",null);
		}
		   if("0".equals(rs)){
			 addMessage(redirectAttributes, "Passed, failed to send mail");
		   }else if("1".equals(rs)){
			   addMessage(redirectAttributes,"Passed,the mail status needs to be changed for a second");
		   } else{
			   addMessage(redirectAttributes,"Not Passed");
		   }
		
		return "redirect:"+Global.getAdminPath()+"/custom/sendEmail/checkList?checkState=0";
	}
	
	
	private  boolean sendCheckEmail(SendEmail email,String type,String sendState){
		String toAddress ="";
		StringBuffer content= new StringBuffer("");
		if(email!=null){
			toAddress=systemService.getUser(email.getCreateBy().getId()).getEmail();
			content.append("<p>Hi,<br/><br/><a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/custom/sendEmail/view?id="+email.getId()+"'>"+email.getSendSubject()+"</a><br/><br/>Remark:"+email.getReason()+"<br/><br/>Result:"+type+"</p>");
		}
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();   
			final MailInfo mailInfo = new MailInfo(toAddress,"Mail audit failed"+email.getSendSubject()+DateUtils.getDate("-yyyy/MM/dd"),date);
			mailInfo.setContent(content.toString());
			new Thread(){
				public void run(){   
					 mailManager.send(mailInfo);
				}
			}.start();
		}
		return true;
	}
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		sendEmailService.delete(id);
		addMessage(redirectAttributes, "Delete Email successfully");
		return "redirect:"+Global.getAdminPath()+"/custom/sendEmail/?repage";
	}
	
	@RequestMapping(value = "view")
	public String view(SendEmail sendEmail,String checkEmail, Model model) {
		List<String> wordList=Lists.newArrayList("fire","burn","exlode","smoke","crash","remove","negative","free","positive",
				 "compensation", "discount", "money","bank","paypal","gratuit","gratuitement","commentaire","négatif","argent","réduction","banque","supprimer",
				 "retirar","negativo","comentario","dinero","banco","reembolso","descuento","eliminar",
				 "negativ","entfernen","aktualisieren","ändern","kommentar","geld","paypal","entschädigung",
				 "eliminare","rimborsare","rimvuovere","cancellare","migliorare","modificare",
				 "現金","ネガティブレビュー","ネガティブレビューを削除","ディスカウント","賠償","バンク","クレーム");
		String content=sendEmail.getSendContent();
		if(StringUtils.isNotBlank(content)&&sendEmail.getSendEmail().contains("marketplace.amazon")){
			 for (String word: wordList) {
				if(content.toLowerCase().contains(" "+word+" ")||content.toLowerCase().contains(" "+word+",")
						||content.toLowerCase().contains(","+word+" ")||content.toLowerCase().contains(","+word+",")
						||content.toLowerCase().contains(word+" ")||content.toLowerCase().contains(" "+word)
						){
					
					content=content.replaceAll(" "+word+" ","<b><span style='color:red;'>"+" "+word+" "+"</span></b>");
					content=content.replaceAll(" "+word.toUpperCase()+" ","<b><span style='color:red;'>"+" "+word.toUpperCase()+" "+"</span></b>");
					
					content=content.replaceAll(" "+word+",","<b><span style='color:red;'>"+" "+word+","+"</span></b>");
					content=content.replaceAll(" "+word.toUpperCase()+",","<b><span style='color:red;'>"+" "+word.toUpperCase()+","+"</span></b>");
					
					content=content.replaceAll(","+word+" ","<b><span style='color:red;'>"+","+word+" "+"</span></b>");
					content=content.replaceAll(","+word.toUpperCase()+" ","<b><span style='color:red;'>"+","+word.toUpperCase()+" "+"</span></b>");
					
					content=content.replaceAll(","+word+",","<b><span style='color:red;'>"+","+word+","+"</span></b>");
					content=content.replaceAll(","+word.toUpperCase()+",","<b><span style='color:red;'>"+","+word.toUpperCase()+","+"</span></b>");
					
					content=content.replaceAll(word+" ","<b><span style='color:red;'>"+word+" "+"</span></b>");
					content=content.replaceAll(word.toUpperCase()+" ","<b><span style='color:red;'>"+word.toUpperCase()+" "+"</span></b>");
					
					content=content.replaceAll(" "+word,"<b><span style='color:red;'>"+" "+word+"</span></b>");
					content=content.replaceAll(" "+word.toUpperCase(),"<b><span style='color:red;'>"+" "+word.toUpperCase()+"</span></b>");
					
					String upperWord=word.substring(0,1).toUpperCase()+word.substring(1);
					content=content.replaceAll(" "+upperWord+" ","<b><span style='color:red;'>"+" "+upperWord+" "+"</span></b>");
					content=content.replaceAll(" "+upperWord+",","<b><span style='color:red;'>"+" "+upperWord+","+"</span></b>");
					content=content.replaceAll(","+upperWord+" ","<b><span style='color:red;'>"+","+upperWord+" "+"</span></b>");
					content=content.replaceAll(","+upperWord+",","<b><span style='color:red;'>"+","+upperWord+","+"</span></b>");
					content=content.replaceAll(" "+upperWord,"<b><span style='color:red;'>"+" "+upperWord+"</span></b>");
					content=content.replaceAll(upperWord+" ","<b><span style='color:red;'>"+upperWord+" "+"</span></b>");
					
				}
			 }
		}
		sendEmail.setRemark(content);
		model.addAttribute("email", sendEmail);
		model.addAttribute("checkEmail",checkEmail);
		return "modules/custom/sendEmailView";
	}
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "signature")
	public String signature(String userId, Model model) {
		model.addAttribute("signature", signatureService.get(userId));
		return "modules/custom/signatureForm";
	}
	
	@ResponseBody
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "saveSign")
	public String saveSign(Signature signature) {
		signature.setSignatureContent(HtmlUtils.htmlUnescape(signature.getSignatureContent()));
		signatureService.save(signature);
		return "1";
	}
	
	@ResponseBody
	@RequestMapping(value = {"updateRemark"})
	public String updateRemark(String id,String remark) throws UnsupportedEncodingException {
		if(StringUtils.isNotEmpty(remark)){
			this.sendEmailService.saveRemark(URLDecoder.decode(remark, "UTF-8"), id);
		}
		return "true";
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"findUndelivered"})
	public String findUndelivered(String email) throws UnsupportedEncodingException {
		if(customEmailService.findUndelivered(email)){//不能发送邮箱
			return "1";
		}
		return "0";
	}
	
	public static String getTemplate(String email,String name,String country){
		if(country==null){
			return "";
		}
		try{
			Configuration cfg = new Configuration();
			cfg.setDefaultEncoding("utf-8");
			cfg.setDirectoryForTemplateLoading(new File(TEMPLATE_PATH));
			Template template = cfg.getTemplate(name+country+".ftl");
			Map<String, String> model = Maps
					.newHashMap();
			//不管怎样都放入token
			model.put("token",UnsubscribeEmailService.getUnsubscribeEmaliHref(email));
			return FreeMarkers.renderTemplate(template,model);
		}catch(Exception e){
			if(country.length()>0){
				return getTemplate(email,name,"");
			}else{
				return getTemplate(email,name,null);
			}
		}
	}
	
	@RequestMapping(value = {"exportCheck"})
	public String exportCheck(SendEmail sendEmail, HttpServletRequest request, HttpServletResponse response, Model model) {
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Page<SendEmail> page = new Page<SendEmail>(request, response,-1);
			String orderBy = page.getOrderBy();
			if("".equals(orderBy)){
				page.setOrderBy("sentDate desc");
			}else{
				page.setOrderBy(orderBy+",sentDate desc");
			}	
	        page = sendEmailService.findCheck2(page, sendEmail); 
	        HSSFWorkbook wb = new HSSFWorkbook();
			
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
			List<String> title =Lists.newArrayList("Email","Event","Subject","SendDate","Remark","CreateUser","CheckUser");
			
			style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
			style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			HSSFFont font = wb.createFont();
			font.setFontHeightInPoints((short) 11); // 字体高度
			style.setFont(font);
		
				  HSSFSheet sheet= wb.createSheet();
				  HSSFRow row = sheet.createRow(0);
				  row.setHeight((short) 400);
				  HSSFCell cell = null;		
				  for (int i = 0; i < title.size(); i++) {
						cell = row.createCell(i);
						cell.setCellValue(title.get(i));
						cell.setCellStyle(style);
						sheet.autoSizeColumn((short) i);
				  }
				  int excelNo =1;
				 
				  for (SendEmail email:page.getList()) {
						  row = sheet.createRow(excelNo++);
						  int j =0;
						  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(email.getEncryptionEmail()); 
						  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(email.getRemark()==null?"":("SPR-"+email.getRemark())); 
						  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(email.getSendSubject()==null?"":email.getSendSubject()); 
						  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(email.getSentDate()==null?"":dateFormat.format(email.getSentDate())); 
						  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(email.getReason()==null?"":email.getReason()); 
						  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(email.getCreateBy()==null?"":email.getCreateBy().getName()); 
						  row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(email.getCheckUser()==null?"":email.getCheckUser().getName());
				  }	  
				 
				  for (int k = 0; k <title.size(); k++) {
				       sheet.autoSizeColumn((short)k, true);
				  }
			
			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
			
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
				String fileName = "Email" + sdf.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return null;
	}

}
