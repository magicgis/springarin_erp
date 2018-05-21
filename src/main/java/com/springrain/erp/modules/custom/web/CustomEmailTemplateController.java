package com.springrain.erp.modules.custom.web;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import com.google.common.collect.Lists;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.MessageUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.custom.entity.CustomEmailTemplate;
import com.springrain.erp.modules.custom.entity.Event;
import com.springrain.erp.modules.custom.entity.Signature;
import com.springrain.erp.modules.custom.service.CustomEmailTemplateService;
import com.springrain.erp.modules.custom.service.SignatureService;
import com.springrain.erp.modules.sys.entity.Office;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 邮件模板Controller
 * @author Hudson
 * @version 2015-05-11
 */
@Controller
@RequestMapping(value = "${adminPath}/custom/emailTemplate")
public class CustomEmailTemplateController extends BaseController{
   
	@Autowired
	private CustomEmailTemplateService customEmailTemplateService;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private SignatureService signatureService;
	
	
	@ModelAttribute
	public CustomEmailTemplate get(@RequestParam(required=false) Integer id){
		 if (id!=null && id>0){
			CustomEmailTemplate cet = customEmailTemplateService.get(id);
            return cet;
		}else{
			return new CustomEmailTemplate();
		}
		
	}
	
	
	@RequestMapping(value={"list",""})
	public String list(CustomEmailTemplate customEmailTemplate, HttpServletRequest request, HttpServletResponse response, Model model){
		 User user = UserUtils.getUser();		
		 customEmailTemplate.setCreateBy(user);
		 Page<CustomEmailTemplate> page = new Page<CustomEmailTemplate>(request,response);
		 String orderBy = page.getOrderBy();
		 if("".equals(orderBy)){
			 page.setOrderBy("lastUpdateDate desc");
		 }else{
			 page.setOrderBy(orderBy+",lastUpdateDate desc");
		 }
		 
		 page = customEmailTemplateService.find(page,customEmailTemplate,user);
		 page.setOrderBy(orderBy);
		 model.addAttribute("page", page);
		 model.addAttribute("cuser", user);
		 model.addAttribute("customEmailtemplate", customEmailTemplate);
		 model.addAttribute("offices", UserUtils.getOfficeList());
		 return "modules/custom/customEmailTemplateList";
		
	}
	
	
	@RequestMapping(value={"delete"})
	public String delete(Integer id, RedirectAttributes redirectAttributes){
		this.customEmailTemplateService.delete(id);
		addMessage(redirectAttributes, "Template has delete");
		return "redirect:"+Global.getAdminPath()+"/custom/emailTemplate/?repage";
	}
	
	
	@RequestMapping(value={"update","add"})
	public String addOrUpdate(CustomEmailTemplate customEmailTemplate,Model model){
		model.addAttribute("customEmailTemplate", customEmailTemplate);
		return "modules/custom/customEmailTemplateForm";
	}
	
	@RequestMapping(value={"save"})
	public String save(CustomEmailTemplate customEmailTemplate,String roleId,@RequestParam(required=false)MultipartFile attachmentFile,Model model,RedirectAttributes redirectAttributes){
		User user = UserUtils.getUser();
		if(roleId!=null){
			Role role = this.systemService.getRole(roleId);
			customEmailTemplate.setRole(role);
		}
		
		if(customEmailTemplate.getId()!=null){
			customEmailTemplate.setLastUpdateBy(user);
			addMessage(redirectAttributes, "模板修改成功！");			
		}else{
			customEmailTemplate.setCreateBy(user);
			addMessage(redirectAttributes, "模板保存成功！");
		}
		if (StringUtils.isNotBlank(customEmailTemplate.getTemplateSubject())) {
			try {
				customEmailTemplate.setTemplateSubject(URLDecoder.decode(customEmailTemplate.getTemplateSubject(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {}
		}
		if ("3,4,5".contains(customEmailTemplate.getTemplateType())) {
			String temp = customEmailTemplate.getCountry();
			if ("com".equals(temp)) {
				temp = "us";
			}
			String name = customEmailTemplate.getTemplateName();
			if (!temp.equalsIgnoreCase(name.substring(0, 2))) {
				model.addAttribute("message", "操作失败！模板名称必须以平台缩写开头,如："+temp.toUpperCase()+name);
				model.addAttribute("customEmailTemplate", customEmailTemplate);
				return "modules/custom/customEmailTemplateForm";
			}
		}
		String content = customEmailTemplate.getTemplateContent();
		if (StringUtils.isNotEmpty(content)) {
			if ((content.contains("%7b") || content.contains("%7d"))) {
				content = content.replaceAll("%7b", "{");
				content = content.replaceAll("%7d", "}");
			}
			customEmailTemplate.setTemplateContent(content);
			String country = customEmailTemplate.getCountry();
			String suff = country;
			if("uk,jp".contains(country)){
				 suff = "co."+suff;
			}else if("mx".equals(country)){
				suff = "com."+suff;
			}
			if (content.contains("www.amazon") && !content.contains("www.amazon."+suff)) {
				model.addAttribute("message", "操作失败！模板链接地址与所选平台亚马逊网址不一致,请更改！");
				model.addAttribute("customEmailTemplate", customEmailTemplate);
				return "modules/custom/customEmailTemplateForm";
			}
		}
		//处理附件,附件为可选项
		if (attachmentFile != null && attachmentFile.getSize() != 0) {
			String filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/emailTemplateAttachment";
			String uuid = UUID.randomUUID().toString();
			File baseDir = new File(filePath + "/" + uuid);
			if(!baseDir.isDirectory())
				baseDir.mkdirs();
			String fileName = attachmentFile.getOriginalFilename();
			File dest = new File(baseDir, fileName);
			try {
				FileUtils.copyInputStreamToFile(attachmentFile.getInputStream(), dest);
				customEmailTemplate.setFileName(uuid + "/" + fileName);
				customEmailTemplate.setFilePath(dest.getAbsolutePath());
			} catch (IOException e) {
				logger.warn("模板附件保存失败", e);
				throw new RuntimeException("模板附件保存失败");
			}
		}
		this.customEmailTemplateService.save(customEmailTemplate);
		return "redirect:" + Global.getAdminPath() + "/custom/emailTemplate/list";
	}
	
	@RequestMapping(value={"view"})
	public String view(CustomEmailTemplate customEmailTemplate,Model model){
		model.addAttribute("customEmailTemplate", customEmailTemplate);
		return "modules/custom/customEmailTemplateView";
	}
	
	@RequestMapping(value = "batchTransmitOther")
	public String batchTransmitOther(@RequestParam("eid[]")Integer[] eid, String userId , Model model, RedirectAttributes redirectAttributes) {
		User user = UserUtils.getUserById(userId);
		for (Integer id : eid) {
			CustomEmailTemplate template = customEmailTemplateService.get(id);
			template.setCreateBy(user);
			template.setLastUpdateDate(new Date());
			template.setLastUpdateBy(UserUtils.getUser());
			customEmailTemplateService.save(template);
		}
		addMessage(redirectAttributes, "Operation is successful！");
		return "redirect:" + Global.getAdminPath() + "/custom/emailTemplate/list";
	}
	
	
	@ResponseBody
	@RequestMapping(value={"roleList"})
	public List<Map<String,String>> roleList(){
		User user = UserUtils.getUser();
		Set<Role> roles = user.getRoleList();
		List<Map<String,String>>data = new ArrayList<Map<String,String>>();
		for(Role r:roles){
			Map<String,String> map = new HashMap<String,String>();
			map.put("id", r.getId());
			map.put("name", r.getName());
			data.add(map);
		}
		return data;
		
	}
	
	/**
	 * 精密验证
	 * @param templateName
	 * @param templateType
	 * @param roleId
	 * @return
	 */
	/*
	@ResponseBody
	@RequestMapping(value={"isExistName"})
	public String isExistName(String templateName,String templateType,String roleId){
		Role role = this.systemService.getRole(roleId);
		boolean isExistName = this.customEmailTemplateService.isExistName(templateName,templateType,role);		
		return (!isExistName)+"";
	}*/
	
	/*
	@ResponseBody
	@RequestMapping(value={"templateList"})
	public List<CustomEmailTemplate> templateList(){
		User user = UserUtils.getUser();	
		return this.customEmailTemplateService.find(user);
		
	}*/
	
	
	@ResponseBody
	@RequestMapping(value={"isExistName"})
	public String isExistName(String templateName){
		boolean isExistName = this.customEmailTemplateService.isExistName(templateName);		
		return (!isExistName)+"";
	}
	
	
	
	@ResponseBody
	@RequestMapping(value={"findTemplate"})
	public CustomEmailTemplate findTemplate(@RequestParam(required=false)String reply ,CustomEmailTemplate customEmailTemplate){
		if(StringUtils.isEmpty(reply)){
			User user = UserUtils.getUser();
			Signature signature = signatureService.get(user.getId());
			if(signature!=null&&signature.getSignatureContent().length()>0){
				customEmailTemplate.setTemplateContent(customEmailTemplate.getTemplateContent()+"<br/><br/><br/><br/>--------------------<br/>"+signature.getSignatureContent());
			}
		}
		return customEmailTemplate;
	}
	
	
}
