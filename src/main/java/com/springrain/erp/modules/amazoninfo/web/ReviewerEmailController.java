package com.springrain.erp.modules.amazoninfo.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
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

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.ReviewerEmail;
import com.springrain.erp.modules.amazoninfo.entity.ReviewerSendEmail;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.service.ReviewerEmailService;
import com.springrain.erp.modules.amazoninfo.service.ReviewerSendEmailService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonRefundService;
import com.springrain.erp.modules.custom.entity.CustomEmailTemplate;
import com.springrain.erp.modules.custom.entity.Signature;
import com.springrain.erp.modules.custom.scheduler.CustomEmailMonitor;
import com.springrain.erp.modules.custom.service.CustomEmailService;
import com.springrain.erp.modules.custom.service.CustomEmailTemplateService;
import com.springrain.erp.modules.custom.service.CustomProductProblemService;
import com.springrain.erp.modules.custom.service.SignatureService;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.server.pojo.Message;

/**
 * 评测Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/reviewerEmail")
public class ReviewerEmailController extends BaseController {
	@Autowired
	private ReviewerEmailService reviewerEmailService;
	
	@Autowired
	private ReviewerSendEmailService reviewerSendEmailService;
	
	@Autowired
	private SignatureService signatureService;
	
	@Autowired
	private CustomEmailTemplateService customEmailTemplateService;
	
	@Autowired
	private AmazonOrderService orderService;
	
	@Autowired
	private PsiProductService psiProductService;
	
	@Autowired
	private AmazonRefundService  amazonRefundService;
	
	@Autowired
	private CustomEmailService customEmailService;
	@Autowired
	private CustomProductProblemService	 customProductProblemService;
	
	private final static Pattern ORDER_PATTERN = Pattern.compile("\\d{3}-\\d{7}-\\d{7}");
	
	private final static Pattern MSG_ID_PATTERN = Pattern.compile("\\[commMgrTok:.+\\]");

	@ModelAttribute
	public ReviewerEmail get(@RequestParam(required=false) String id) {
		if (!StringUtils.isBlank(id)){
			ReviewerEmail rs = reviewerEmailService.get(Integer.parseInt(id));
			return rs;
		}else{
			return new ReviewerEmail();
		}
	}
	
	@RequestMapping(value = {"list", ""})
	public String list(ReviewerEmail reviewerEmail, String aboutMe, HttpServletRequest request, HttpServletResponse response, Model model) {
		if (aboutMe == null) {
			aboutMe = "1";
		}
		User user = UserUtils.getUser(); 
		if (!user.isAdmin()){
			reviewerEmail.setCreateBy(user);
		}
		if(reviewerEmail.getState()==null){
			reviewerEmail.setState("5");
		}
		Page<ReviewerEmail> page = new Page<ReviewerEmail>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("customSendDate desc");
		}else{
			page.setOrderBy(orderBy+",customSendDate desc");
		}
        page = reviewerEmailService.find(page,reviewerEmail,aboutMe); 
        page.setOrderBy(orderBy);
        model.addAttribute("page", page);
        model.addAttribute("cuser", user);
        model.addAttribute("aboutMe", aboutMe);
		model.addAttribute("otherMaster",CustomEmailMonitor.getOtherMaster(customEmailService));
		return "modules/amazoninfo/reviewer/reviewerEmailList";
	}
	
	@RequestMapping(value = "form")
	public String form(ReviewerEmail emailManager, Model model) {
		String state = emailManager.getState();
		if (emailManager.getMasterBy() == null) {
			emailManager.setMasterBy(UserUtils.getUser());
		}
		if(emailManager.getMasterBy().getId().equals(UserUtils.getUser().getId())){
			if(state!=null && state.equals("0")){
				emailManager.setState("1");
				emailManager.setAnswerDate(new Date());
				reviewerEmailService.save(emailManager);
			}
		}
		
		//

		Set<String> ordersStr = getOrders(emailManager.getSubject());
		ordersStr.addAll(getOrders(emailManager.getReceiveContent()));
		String sku = "";
		String country ="";
		List<AmazonOrder> orders = Lists.newArrayList();
		for (String ordStr : ordersStr) {
			AmazonOrder order = orderService.findByEg(ordStr);
			if(order!=null){
				orders.add(order);
				order.setCustomId(orderService.getCustomIdByOrderId(order.getAmazonOrderId()));
				order.setAmazonRefunds(amazonRefundService.getRefundRecord(order.getAmazonOrderId()));
				if(StringUtils.isEmpty(sku)){
					//随意取一个订单一个item里的sku
					sku=order.getItems().get(0).getSellersku();
					country=order.getSalesChannel().substring(order.getSalesChannel().lastIndexOf(".")+1);
				}
			}
		}
		
		if(ordersStr.size()>0){
			String orderNos = "";
			StringBuffer buf= new StringBuffer();
			for(String orderNo:ordersStr){
				buf.append(orderNo+",");
			}
			orderNos = buf.toString();
			emailManager.setOrderNos(orderNos.substring(0, orderNos.length()-1));
			reviewerEmailService.save(emailManager);
		}
		String productName="";
		
		if(StringUtils.isNotEmpty(sku)){
			//查询该sku对应的产品名字
			PsiSku psiSku=this.psiProductService.getSkuBySku(sku);
			if(psiSku!=null){
				if(StringUtils.isNotEmpty(psiSku.getColor())){
					productName=psiSku.getProductName()+"_"+psiSku.getColor();
				}else{
					productName=psiSku.getProductName();
				}
			}
		}
		
		//找到所有的系统模板
		List<CustomEmailTemplate> templates = this.customEmailTemplateService.find();		
		model.addAttribute("templates", templates);
		//查询所有产品名及产品类型
		Map<String,String> productMap =this.psiProductService.findProductTypeMap();
		
		model.addAttribute("country", country);
		model.addAttribute("productName", productName);
		model.addAttribute("productMap", productMap);
		model.addAttribute("productMapJson", JSON.toJSON(productMap));
		model.addAttribute("mangerMapJson", JSON.toJSON(this.psiProductService.findManagerProductTypeMap()));//查询产品经理
		model.addAttribute("problemMapJson", JSON.toJSON(customProductProblemService.findProblemType()));
		model.addAttribute("orders", orders);
		model.addAttribute("emailManager", emailManager);
		
		model.addAttribute("otherMaster",CustomEmailMonitor.getOtherMaster(customEmailService));
		return "modules/amazoninfo/reviewer/reviewerEmailForm";
	}
	
	@RequestMapping(value = "view")
	public String view(String all, ReviewerEmail emailManager, Model model) {
		if(all!=null){
			model.addAttribute("flag", true);
		}
		model.addAttribute("emailManager", emailManager);
		return "modules/amazoninfo/reviewer/reviewerEmailView";
	}
	
	@RequestMapping(value = "reply")
	public String reply(ReviewerEmail emailManager,Model model) {
		ReviewerSendEmail sendEmail = null;
		List<ReviewerSendEmail> sends =  emailManager.getReviewerSendEmails();
		List<ReviewerSendEmail> sendeds =  Lists.newArrayList(sends);
		if(sends!=null&&sends.size()>0){
			if("0".equals(sends.get(0).getSendFlag())){
				sendEmail = sendeds.remove(0);
			}
		}
		if(sendEmail == null ){
			sendEmail = new ReviewerSendEmail();
			sendEmail.setType("1");
			sendEmail.setReviewerEmail(emailManager);
			sendEmail.setCreateBy(emailManager.getMasterBy());
			Signature signature = signatureService.get(emailManager.getMasterBy().getId());
			sendEmail.setSendContent("");
			if(sendeds.size()==0){
				sendEmail.setSendSubject("RE:"+emailManager.getSubject());
				if(signature!=null&&signature.getSignatureContent().length()>0){
					sendEmail.setSendContent("<br/><br/><br/><br/>--------------------<br/>"+signature.getSignatureContent());
				}
				sendEmail.setSendContent(sendEmail.getSendContent()
						+"<br/>------------------ Original ------------------<br/>"+emailManager.getReceiveContent());
				String email = emailManager.getRevertEmail();
				String temp = email.toLowerCase().replace("-","").replace("_","");
				if(temp.contains("notreply")||temp.contains("noreply")){
					email="";
				}
				sendEmail.setSendEmail(email);
			}else{
				ReviewerSendEmail sended = sendeds.get(sendeds.size()-1);
				sendEmail.setSendSubject("RE:"+sended.getSendSubject());
				if(signature!=null&&signature.getSignatureContent().length()>0){
					sendEmail.setSendContent("<br/><br/><br/><br/>--------------------<br/>"+signature.getSignatureContent());
				}
				sendEmail.setSendContent(sendEmail.getSendContent()
						+"<br/>------------------ Original ------------------<br/>"+sended.getSendContent());
				sendEmail.setSendEmail(sended.getSendEmail());
				sendEmail.setCcToEmail(sended.getCcToEmail());
				sendEmail.setBccToEmail(sended.getBccToEmail());
			}
			reviewerSendEmailService.save(sendEmail);
		}
		
		//生成邮件模板
		User user = UserUtils.getUser();
		List<CustomEmailTemplate> templates = customEmailTemplateService.find(user);
		model.addAttribute("templates", templates);
		
		model.addAttribute("customEmailId", emailManager.getId());
		model.addAttribute("sendEmail", sendEmail);
		model.addAttribute("sendeds", sendeds);
		return "modules/amazoninfo/reviewer/reviewerEmailReply";
	}
	
	@RequestMapping(value = "recall")
	public String recall(ReviewerEmail emailManager, Model model, RedirectAttributes redirectAttributes) {
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/reviewerEmail/reply?id="+emailManager.getId();
	}
	
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		if (StringUtils.isNotEmpty(id)) {
			reviewerEmailService.delete(Integer.parseInt(id));
		}
		addMessage(redirectAttributes, "Email has deleted");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/reviewerEmail/?repage";
	}
	
	@RequestMapping(value = "transmitOther")
	public String transmitOther(String userId ,ReviewerEmail emailManager, Model model, RedirectAttributes redirectAttributes) {
		User cuser = UserUtils.getUser();
		if(emailManager.getMasterBy() != null && !emailManager.getMasterBy().getId().equals(cuser.getId())){
			if(!SecurityUtils.getSubject().isPermitted("custom:email:proxy")){
				throw new RuntimeException("Email is masterBy "+emailManager.getMasterBy().getName()+",You do not have permission！");
			}
		}
		User user = UserUtils.getUserById(userId);
		emailManager.setAnswerDate(null);
		emailManager.setState("0");
		emailManager.setTransmit(emailManager.getMasterBy().getName()+"["+new Date().toLocaleString()+"]");
		emailManager.setMasterBy(user);
		emailManager.setCreateBy(user);
		reviewerEmailService.save(emailManager);
		addMessage(redirectAttributes, "Forward Email Successful,masterBy:'" +user.getName()+"'");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/reviewerEmail/?repage";
	}
	
	@RequestMapping(value = "batchtransmitOther")
	public String batchtransmitOther(@RequestParam("eid[]")String[] eid,String userId , Model model, RedirectAttributes redirectAttributes) {
		User user = UserUtils.getUserById(userId);
		User cuser = UserUtils.getUser();
		for (String id : eid) {
			ReviewerEmail emailManager = reviewerEmailService.get(Integer.parseInt(id));
			if (emailManager == null) {
				continue;
			}
			if(emailManager.getMasterBy() != null && !emailManager.getMasterBy().getId().equals(cuser.getId())){
				if(!SecurityUtils.getSubject().isPermitted("custom:email:proxy")){
					continue;
				}
			}
			emailManager.setAnswerDate(null);
			emailManager.setState("0");
			emailManager.setTransmit(emailManager.getMasterBy().getName()+"["+new Date().toLocaleString()+"]");
			emailManager.setMasterBy(user);
			emailManager.setCreateBy(user);
			reviewerEmailService.save(emailManager);
		}
		addMessage(redirectAttributes, "Forward Email Successful,masterBy:'" +user.getName()+"'");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/reviewerEmail/?repage";
	}
	
	@RequestMapping(value = "noreply")
	public String noreply(final ReviewerEmail emailManager, Model model, RedirectAttributes redirectAttributes) throws UnsupportedEncodingException {
		User cuser = UserUtils.getUser();
		if(!emailManager.getMasterBy().getId().equals(cuser.getId())){
			if(!SecurityUtils.getSubject().isPermitted("reviewer:email:proxy")){
				throw new RuntimeException("Email is masterBy "+emailManager.getMasterBy().getName()+",You do not have permission！");
			}else{
				emailManager.setMasterBy(cuser);
			}
		}	
		//保存备注
		if(StringUtils.isNotEmpty(emailManager.getRemarks())){
			emailManager.setRemarks(URLDecoder.decode(emailManager.getRemarks(), "UTF-8"));
		}
		emailManager.setEndDate(new Date());
		reviewerEmailService.save(emailManager);
		addMessage(redirectAttributes, "Email status has been changed to no treatment!");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/reviewerEmail/?repage";
	}
	
	@RequestMapping(value = "batchNoply")
	public String batchNoply(@RequestParam("eid[]")String[] eid, Model model, RedirectAttributes redirectAttributes) {
		final List<Message> rs = Lists.newArrayList();
		User cuser = UserUtils.getUser();
		for (String ide : eid) {
			final ReviewerEmail emailManager = reviewerEmailService.get(Integer.parseInt(ide));
			if(emailManager==null){
				continue;
			}
			if(!emailManager.getMasterBy().getId().equals(cuser.getId())){
				if(!SecurityUtils.getSubject().isPermitted("reviewer:email:proxy")){
					continue;
				}else{
					emailManager.setMasterBy(cuser);
				}
			}
			emailManager.setEndDate(new Date());
			emailManager.setState("4");
			Matcher matcher = MSG_ID_PATTERN.matcher(emailManager.getReceiveContent());
			String msgid = "";
			String country = emailManager.getRevertEmail().substring(emailManager.getRevertEmail().lastIndexOf(".")+1);
			while(matcher.find()){
				msgid = matcher.group();
				break;
			}
			if(StringUtils.isNotBlank(msgid)){
				msgid = msgid.split(":")[1];
				msgid = msgid.substring(0,msgid.length()-1);
				final String mid = msgid;
				final String countryStr = country;
				final String id = emailManager.getId().toString();
				emailManager.setResult("Please wait a moment!");
				rs.add(new Message(id,countryStr,mid,emailManager.getCustomSendDate().getTime()));
			}
			reviewerEmailService.save(emailManager);
		}
		addMessage(redirectAttributes, "Email status has been changed to no treatment");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/reviewerEmail/?repage";
	}
	
	@RequestMapping("/download")   
    public ModelAndView download(String fileName, HttpServletRequest request, HttpServletResponse response)   
            throws Exception {   
		fileName = HtmlUtils.htmlUnescape(fileName);
        response.setContentType("text/html;charset=utf-8");   
        request.setCharacterEncoding("UTF-8");   
        java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null;   
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/customEmail/";  
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
	
	//保存问题信息
	@ResponseBody
	@RequestMapping(value = {"saveProblem"})
	public String saveProblem(String country,String productName,String  problemType,String problem,String id,String orderNos){
		try{
			reviewerEmailService.saveProblem(country, productName, problemType, URLDecoder.decode(problem, "utf-8"), id,orderNos);
		}catch(Exception ex){
			return "保存问题失败："+ex.getMessage();
		}
		return "保存问题成功！！！";
	
	}
	
	public static Set<String> getOrders(String input){
		Set<String> rs = Sets.newHashSet();
		if(StringUtils.isNotEmpty(input)){
			Matcher matcher = ORDER_PATTERN.matcher(input);
			while(matcher.find()){
				rs.add(matcher.group());
			}
		}
		return rs;
	}
	
	@ResponseBody
	@RequestMapping(value = {"updateRemark"})
	public String updateRemark(Integer id,String remark) throws UnsupportedEncodingException {
		if(StringUtils.isNotEmpty(remark)){
			this.reviewerEmailService.saveRemark(URLDecoder.decode(remark, "UTF-8"), id);
		}
		return "true";
	}
}
