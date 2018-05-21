package com.springrain.erp.modules.amazoninfo.web;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonReviewer;
import com.springrain.erp.modules.amazoninfo.entity.ReviewerComment;
import com.springrain.erp.modules.amazoninfo.entity.ReviewerEmail;
import com.springrain.erp.modules.amazoninfo.entity.ReviewerSendEmail;
import com.springrain.erp.modules.amazoninfo.scheduler.ReviewerEmailManager;
import com.springrain.erp.modules.amazoninfo.service.AmazonReviewerService;
import com.springrain.erp.modules.amazoninfo.service.ReviewerCommentService;
import com.springrain.erp.modules.amazoninfo.service.ReviewerEmailService;
import com.springrain.erp.modules.amazoninfo.service.ReviewerSendEmailService;
import com.springrain.erp.modules.custom.entity.CustomEmailTemplate;
import com.springrain.erp.modules.custom.entity.Signature;
import com.springrain.erp.modules.custom.service.CustomEmailTemplateService;
import com.springrain.erp.modules.custom.service.SignatureService;
import com.springrain.erp.modules.custom.service.UnsubscribeEmailService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 评测邮件发送Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/reviewerSendEmail")
public class ReviewerSendEmailController extends BaseController {
	@Autowired
	private ReviewerEmailManager reviewerEmailManager;
	
	@Autowired
	private AmazonReviewerService reviewerService;
	
	@Autowired
	private ReviewerSendEmailService reviewerSendEmailService;
	
	@Autowired
	private ReviewerEmailService reviewerEmailService;
	
	@Autowired
	private SignatureService signatureService;
	
	@Autowired
	private CustomEmailTemplateService customEmailTemplateService;
	
	@Autowired
	private ReviewerCommentService reviewerCommentService;

	@Autowired
	private UnsubscribeEmailService unsubscribeEmailService;
	
	@ModelAttribute
	public ReviewerSendEmail get(@RequestParam(required=false) String id) {
		if (!StringUtils.isBlank(id)){
			return reviewerSendEmailService.get(Integer.parseInt(id));
		}else{
			return new ReviewerSendEmail();
		}
	}
	
	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = {"list", ""})
	public String list(ReviewerSendEmail sendEmail, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			sendEmail.setCreateBy(user);
		}
		Page<ReviewerSendEmail> page = new Page<ReviewerSendEmail>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("sentDate desc");
		}else{
			page.setOrderBy(orderBy+",sentDate desc");
		}
		sendEmail.setSendFlag("1");
        page = reviewerSendEmailService.find(page, sendEmail);
        page.setOrderBy(orderBy);
        model.addAttribute("page", page);
		return "modules/amazoninfo/reviewer/reviewerSendEmailList";
	}
	
	@RequestMapping(value = {"draft"})
	public String draftList(ReviewerSendEmail sendEmail, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			sendEmail.setCreateBy(user);
		}
		Page<ReviewerSendEmail> page = new Page<ReviewerSendEmail>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("sentDate desc");
		}else{
			page.setOrderBy(orderBy+",sentDate desc");
		}	
		sendEmail.setSendFlag("0");
        page = reviewerSendEmailService.find(page, sendEmail); 
        page.setOrderBy(orderBy);
        model.addAttribute("page", page);
		return "modules/amazoninfo/reviewer/reviewerDraftList";
	}

	@RequiresPermissions("custom:email:view")
	@RequestMapping(value = "form")
	public String form(ReviewerSendEmail sendEmail, Model model) {
		User user = UserUtils.getUser();
		if(sendEmail.getId()==null){
			ReviewerSendEmail sendEmail2 = reviewerSendEmailService.findBlankEmail(sendEmail.getSendEmail());
			if(sendEmail2==null){
				sendEmail.setCreateBy(user);
				Signature signature = signatureService.get(sendEmail.getCreateBy().getId());
				if(signature!=null&&signature.getSignatureContent().length()>0){
					sendEmail.setSendContent("<br/><br/><br/><br/>--------------------<br/>"+signature.getSignatureContent());
				}
				reviewerSendEmailService.save(sendEmail);
			}else{
				if(StringUtils.isNotEmpty(sendEmail.getSendEmail())){
					sendEmail2.setSendEmail(sendEmail.getSendEmail());
				}
				sendEmail = sendEmail2;
			}
		}
		sendEmail.setType("0");
		
		//生成邮件模板
		List<CustomEmailTemplate> templates = this.customEmailTemplateService.find(user);		
		model.addAttribute("templates", templates);
		
		model.addAttribute("sendEmail", sendEmail);
		return "modules/amazoninfo/reviewer/reviewerSendEmail";
	}
	
	@RequestMapping(value = "saveSendEmail")
	public String saveSendEmail(@RequestParam(value="attchmentFile",required=false)MultipartFile[] attchmentFiles,ReviewerSendEmail sendEmail, Model model, RedirectAttributes redirectAttributes,HttpServletRequest request) throws Exception {
		sendEmail.setSendSubject(HtmlUtils.htmlUnescape(sendEmail.getSendSubject()));
		sendEmail.setSendContent(HtmlUtils.htmlUnescape(sendEmail.getSendContent()));
		
		MailInfo sendMailInfo = sendEmail.getMailInfo();
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
					sendMailInfo.setFileName(attchmentFile.getOriginalFilename());
					sendMailInfo.setFilePath(dest.getAbsolutePath());
					try {
						FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
						sendEmail.setSendAttchmentPath(uuid+"/"+name);
					} catch (IOException e) {
						logger.warn(name+"文件保存失败",e);
					}
				}
			}
		}
		
		User user = UserUtils.getUser();
		if(sendEmail.getCreateBy()==null){
			sendEmail.setCreateBy(user);
		}
		ReviewerEmail email =  sendEmail.getReviewerEmail();
		if(email!=null&&email.getMasterBy()!=null){
			if(!email.getMasterBy().getId().equals(user.getId())){
				if(SecurityUtils.getSubject().isPermitted("reviewer:email:proxy")){
					email.setAnswerDate(new Date());
					email.setMasterBy(user);
				}else{
					throw new RuntimeException("Email is masterBy "+email.getMasterBy().getName()+",You do not have permission！");
				}
			}
		}
		//免打扰邮箱
		List<Object> unSentEmails = unsubscribeEmailService.findEmails();
		Set<String> unSents =  Sets.newHashSet();
		for (Object obj : unSentEmails) {
			unSents.add(obj.toString());
		}
		//多个地址是分别发送邮件
		List<MailInfo> list = sendEmail.getMailInfoList();
		for (MailInfo mailInfo : list) {
			String toAddress = mailInfo.getToAddress();
			if (unSents.contains(toAddress)) {
				logger.info("跳过免打扰邮箱" + toAddress);
				continue;
			}
			List<String> fileNameList = sendMailInfo.getFileName();
			for (String fileName : fileNameList) {
				mailInfo.setFileName(fileName);
			}
			List<String> filePathList = sendMailInfo.getFilePath();
			for (String filePath : filePathList) {
				mailInfo.setFilePath(filePath);
			}
			boolean flag = false;
			flag = reviewerEmailManager.send(mailInfo);
			if(flag){
				ReviewerSendEmail sent = new ReviewerSendEmail();
				//复制信息
				sent.setSendContent(sendEmail.getSendContent());
				sent.setSendSubject(sendEmail.getSendSubject());
				if (StringUtils.isNotEmpty(sendEmail.getBccToEmail())) {
					sent.setBccToEmail(sendEmail.getBccToEmail());
				}
				if (StringUtils.isNotEmpty(sendEmail.getCcToEmail())) {
					sent.setCcToEmail(sendEmail.getCcToEmail());
				}
				sent.setCreateBy(sendEmail.getCreateBy());
				sent.setReviewerEmail(sendEmail.getReviewerEmail());
				sent.setSendEmail(mailInfo.getToAddress());
				sent.setType(sendEmail.getType());
				sent.setSentDate(new Date());
				sent.setSendFlag("1");
				if("1".equals(sent.getType())){
					if(email!=null){
						if(email.getEndDate()==null){
							email.setEndDate(new Date());
						}
						email.setState("2");
						email.setUpdateDate(new Date());
					}
				}
				reviewerSendEmailService.save(sent);
				//保存备注michael
				if(sent.getReviewerEmail()!=null&&sent.getReviewerEmail().getId()!=null){
					reviewerEmailService.saveRemark(sendEmail.getRemark(),sent.getReviewerEmail().getId());
				}
				
				//自动追加联系记录
				AmazonReviewer reviewer = reviewerService.findReviewer(sent.getSendEmail(), null);
				if(reviewer != null){
					//设置最后联系人
					reviewer.setContactBy(user);
					reviewerService.save(reviewer);
					String common = "";
					if(sent.getReviewerEmail()!=null){
						common = UserUtils.getUser().getName()+" reply to an email to reviewer <a href='../reviewerEmail/view?id="+sent.getReviewerEmail().getId()+"'>view</a>" ;
					}else{
						common = UserUtils.getUser().getName()+" reply to an email to reviewer <a href='../reviewerSendEmail/view?id="+sent.getId()+"'>view</a>";
					}
					ReviewerComment comm = new ReviewerComment();
					comm.setComment(common);
					comm.setType("1");
					//comm.setCreateBy(UserUtils.getUserById("1"));
					comm.setCreateBy(UserUtils.getUser());
					comm.setAmazonReviewer(reviewer);
					reviewerCommentService.save(comm);
				}
				addMessage(redirectAttributes, "'"+mailInfo.getSubject()+"'Email sent successfully");
			}else{
				addMessage(redirectAttributes, "'"+mailInfo.getSubject()+"'Mail failed, probably because the network is not clear, please try again later!");
			}
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/reviewerSendEmail/?repage";
	}
	
	@RequestMapping(value = "recall")
	public String recall(ReviewerSendEmail sendEmail, Model model, RedirectAttributes redirectAttributes) {
		ReviewerSendEmail sendEmail2  =  reviewerSendEmailService.findBlankEmail(sendEmail.getSendEmail());
		ReviewerSendEmail newSendEmail = null;
		User user = UserUtils.getUser();
		if(sendEmail2==null){
			newSendEmail = new ReviewerSendEmail();
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
			reviewerSendEmailService.save(newSendEmail);
		}else{
			newSendEmail = sendEmail2;
		}
		model.addAttribute("sendEmail", newSendEmail);
		return "modules/amazoninfo/reviewer/reviewerSendEmail";
	}
	
	@RequestMapping(value = "view")
	public String view(ReviewerSendEmail sendEmail, Model model) {
		model.addAttribute("email", sendEmail);
		return "modules/amazoninfo/reviewer/reviewerSendEmailView";
	}
	
	
	@ResponseBody
	@RequestMapping(value = "note")
	public String note(ReviewerSendEmail sendEmail) {
		if(sendEmail.getCreateBy()==null){
			sendEmail.setCreateBy(UserUtils.getUser());
		}
		sendEmail.setSendContent(HtmlUtils.htmlUnescape(sendEmail.getSendContent()));
		reviewerSendEmailService.save(sendEmail);
		return "1";
	}
	
	@RequestMapping(value = "delete")
	public String delete(Integer id, RedirectAttributes redirectAttributes) {
		reviewerSendEmailService.delete(id);
		addMessage(redirectAttributes, "Delete Email successfully");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/reviewerSendEmail/?repage";
	}
	
}
