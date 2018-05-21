package com.springrain.erp.modules.psi.web;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductImprovement;
import com.springrain.erp.modules.psi.entity.PsiProductImprovementItem;
import com.springrain.erp.modules.psi.service.PsiProductImprovementService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 产品改进信息记录Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/productImprovement")
public class PsiProductImprovementController extends BaseController {
	
	@Autowired
	private PsiProductService psiProductService;
	
	@Autowired
	private PsiProductImprovementService psiProductImprovementService;
	
	@Autowired
	private MailManager mailManager;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private PsiProductTypeGroupDictService dictService;
	
	@ModelAttribute
	public PsiProductImprovement get(@RequestParam(required=false) Integer id) {
		if (id != null){
			return psiProductImprovementService.get(id);
		}else{
			return new PsiProductImprovement();
		}
	}
	
	@RequestMapping(value = {"list", ""})
	public String list(PsiProductImprovement productImprovement, HttpServletRequest request, HttpServletResponse response, Model model) {
		if (productImprovement.getCreateDate() == null) {
			productImprovement.setUpdateDate(new Date());
			productImprovement.setCreateDate(DateUtils.addMonths(new Date(), -3));
		}
		Page<PsiProductImprovement> page=new Page<PsiProductImprovement>(request, response);
		page.setPageSize(10000);
		psiProductImprovementService.find(page, productImprovement);
        model.addAttribute("productImprovement", productImprovement);
        model.addAttribute("page", page);
		return "modules/psi/psiProductImprovementList";
	}
	
	@RequestMapping(value = "form")
	public String form(PsiProductImprovement productImprovement, Model model) {
		List<String> allLine = dictService.getAllLineShotrName();
		model.addAttribute("allLine", allLine);
		if (StringUtils.isEmpty(productImprovement.getLine())) {
			productImprovement.setLine(allLine.get(0));
		}
		List<PsiProduct> products =this.psiProductService.findAll();
		Map<String, String> typeLine = dictService.getTypeLine(null);
		List<String> colorNameList = Lists.newArrayList();
		for (PsiProduct psiProduct : products) {
			String line = typeLine.get(psiProduct.getType().toLowerCase());
			if (StringUtils.isNotEmpty(line) && line.equals(productImprovement.getLine())) {
				for (String colorName : psiProduct.getProductNameWithColor()) {
					if (!productImprovement.getProductNames().contains(colorName)) {
						colorNameList.add(colorName);
					}
				}
			}
		}
		model.addAttribute("colorNameList", colorNameList);
		if (StringUtils.isEmpty(productImprovement.getType())) {
			productImprovement.setType("1");
		}
		model.addAttribute("productImprovement", productImprovement);
		return "modules/psi/psiProductImprovementForm";
	}
	
	/**
	 * 
	 * @param productImprovement
	 * @param type 1:填写意见  2：审批
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "goApproval")
	public String goApproval(PsiProductImprovement productImprovement, String type, Model model) {
		model.addAttribute("type", type);
		model.addAttribute("productImprovement", productImprovement);
		return "modules/psi/psiProductImprovementApproval";
	}
	
	@RequestMapping(value = "view")
	public String view(PsiProductImprovement productImprovement, Model model) {
		return "modules/psi/psiProductImprovementView";
	}
	
	@RequestMapping(value = "save")
	public String save(@RequestParam(required=false)MultipartFile attachmentFile,PsiProductImprovement psiProductImprovement, RedirectAttributes redirectAttributes) {
		if(attachmentFile!=null&&attachmentFile.getSize()!=0){
			String filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/productImprovement";
			String uuid = UUID.randomUUID().toString();
			File baseDir = new File(filePath+"/"+uuid); 
			if(!baseDir.isDirectory())
				baseDir.mkdirs();
			String name = attachmentFile.getOriginalFilename();
			String suffix = name.substring(name.lastIndexOf("."));
			name = uuid+suffix;
			File dest = new File(baseDir,name);
			try {
				FileUtils.copyInputStreamToFile(attachmentFile.getInputStream(),dest);
				psiProductImprovement.setFilePath("/"+uuid+"/"+name);
			} catch (IOException e) {
				logger.warn(name+"文件保存失败",e);
			}
		}
		if (psiProductImprovement.getId() != null) {
			psiProductImprovement.setUpdateBy(UserUtils.getUser());
			psiProductImprovement.setUpdateDate(new Date());
			psiProductImprovementService.save(psiProductImprovement);
			addMessage(redirectAttributes, "操作成功!");
			return "redirect:"+Global.getAdminPath()+"/psi/productImprovement/?repage";
		}
		psiProductImprovement.setStatus("0");
		psiProductImprovement.setDelFlag("0");
		psiProductImprovement.setCreateBy(UserUtils.getUser());
		psiProductImprovement.setCreateDate(new Date());
		psiProductImprovement.setUpdateBy(UserUtils.getUser());
		psiProductImprovement.setUpdateDate(new Date());
		List<String> list = Lists.newArrayList("销售","采购","品检");
		int sort = 1;
		List<PsiProductImprovementItem> items = Lists.newArrayList();
		psiProductImprovement.setItems(items);
		for (String key : list) {
			PsiProductImprovementItem item = new PsiProductImprovementItem();
			items.add(item);
			item.setSort(sort);
			item.setDelFlag("0");
			item.setProductImprovement(psiProductImprovement);
			item.setDepartment(key);
			//设置提出处理意见需要的权限,根据权限找到对应的人发邮件通知
			if ("销售".equals(key)) {
				item.setPermission("psi:productImprovement:sales");
			} else if ("采购".equals(key)) {
				item.setPermission("psi:productImprovement:purchase");
			} else if ("品检".equals(key)) {
				item.setPermission("psi:productImprovement:quality");
			}
			if (sort == 1) {
				psiProductImprovement.setPermission(item.getPermission());
			}
			sort++;
		}
		psiProductImprovementService.save(psiProductImprovement);
		//根据产品线发信给销售填写意见
		String toAddress = psiProductImprovementService.findSalesEmail(psiProductImprovement.getLine());
		if (StringUtils.isNotEmpty(toAddress)) {
			String name = toAddress.split("@")[0];
			String emailContent = "<p><span style='font-size:20px'>Hi "+name+":<br/>&nbsp;&nbsp;&nbsp;&nbsp;"+psiProductImprovement.getCreateBy().getName()+"提交了一个产品变更申请,需要你填写处理意见,变更明细如下,请尽快处理。</span>" +
	        			"<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/productImprovement/goApproval?type=1&id="+psiProductImprovement.getId()+"'>立即处理</a></p>";
			String improvementEmail = getEmailContent(psiProductImprovement, "1");
			final MailInfo mailInfo = new MailInfo(toAddress, "产品变更意见收集通知", new Date());
			mailInfo.setContent(emailContent + improvementEmail);
			new Thread(){
			    public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
			
		}
		addMessage(redirectAttributes, "操作成功!");
		return "redirect:"+Global.getAdminPath()+"/psi/productImprovement/?repage";
	}
	
	@RequestMapping(value = "saveSuggestion")
	public String saveSuggestion(PsiProductImprovement psiProductImprovement, String content, Model model, RedirectAttributes redirectAttributes) {
		String permission = psiProductImprovement.getPermission();
		int sort = 0;
		for (PsiProductImprovementItem item : psiProductImprovement.getItems()) {
			if (permission.equals(item.getPermission())) {
				item.setContent(content);
				item.setCreateBy(UserUtils.getUser());
				item.setCreateDate(new Date());
				if (item.getSort() == 1) {
					psiProductImprovement.setStatus("1");
				}
				sort = item.getSort()+1;	//下一个
			}
			if (sort == item.getSort()) {
				psiProductImprovement.setPermission(item.getPermission());
			}
		}
		//如果不存在下一个了，修改状态
		if (psiProductImprovementService.notExits(sort, psiProductImprovement.getId())) {
			psiProductImprovement.setStatus("2");	//待审批
			psiProductImprovement.setPermission("psi:productImprovement:approval");
		}
		psiProductImprovementService.save(psiProductImprovement);
		String toAddress = "";
		String name = "";
		List<User> list = systemService.findUserByPermission(psiProductImprovement.getPermission());
		if (list != null && list.size() > 0) {
			toAddress = list.get(0).getEmail();
			name = list.get(0).getName();
		}
		String emailContent = "";
		String subject = "产品变更意见收集通知";
		if ("2".equals(psiProductImprovement.getStatus())) {
			subject = "产品变更审批通知";
			emailContent = "<p><span style='font-size:20px'>Hi "+name+":<br/>&nbsp;&nbsp;&nbsp;&nbsp;"+psiProductImprovement.getCreateBy().getName()+"提交了一个产品变更申请,变更明细如下,请尽快处理。</span>" +
        			"<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/productImprovement/goApproval?id="+psiProductImprovement.getId()+"'>立即审批</a></p>";
		} else {
			emailContent = "<p><span style='font-size:20px'>Hi "+name+":<br/>&nbsp;&nbsp;&nbsp;&nbsp;"+psiProductImprovement.getCreateBy().getName()+"提交了一个产品变更申请,需要你填写处理意见,变更明细如下,请尽快处理。</span>" +
        			"<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/productImprovement/goApproval?type=1&id="+psiProductImprovement.getId()+"'>立即处理</a></p>";
		}
		String improvementEmail = getEmailContent(psiProductImprovement, "1");
		final MailInfo mailInfo = new MailInfo(toAddress, subject, new Date());
		mailInfo.setContent(emailContent + improvementEmail);
		new Thread(){
		    public void run(){
				mailManager.send(mailInfo);
			}
		}.start();
		addMessage(redirectAttributes, "操作成功!");
		return "redirect:"+Global.getAdminPath()+"/psi/productImprovement/?repage";
	}
	
	@RequestMapping(value = "downloadFile")
	public String downloadFile(PsiProductImprovement productImprovement,Model model,RedirectAttributes redirectAttributes,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, ParseException{
		String path = productImprovement.getFilePath();
		String filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/productImprovement";
		String fileName = filePath + path;
		InputStream inStream = null;
		try {
			inStream = new FileInputStream(fileName);
			ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
			byte[] buff = new byte[4096];
			int len = 0;
			while ((len = inStream.read(buff, 0, 4096)) > 0) {
				swapStream.write(buff, 0, len);
				swapStream.flush();
			}
	        byte[] in2b = swapStream.toByteArray();
	        swapStream.close();
	        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			out.write(in2b);
			out.close();
		} catch (Exception e) {
			logger.error("下载产品变更附件异常", e);
		} finally {
			if (inStream != null) {
				try {
					inStream.close();
				} catch (IOException e) {
					logger.warn("下载产品变更附件关闭流异常", e);
				}
			}
		}
		return null;
	}
	
	@RequestMapping(value = "approval")
	public String approval(Integer id, String appStatus, String approvalContent, Model model, RedirectAttributes redirectAttributes) {
		PsiProductImprovement productImprovement = psiProductImprovementService.get(id);
		productImprovement.setApprovalContent(approvalContent);
		productImprovement.setApprovalBy(UserUtils.getUser());
		productImprovement.setApprovalDate(new Date());
		productImprovement.setPermission("");	//流程完毕,不再需要权限标志
		if ("1".equals(appStatus)) {	//通过
			productImprovement.setStatus("3");
		} else {
			productImprovement.setStatus("4"); //直接取消
		}
		psiProductImprovementService.save(productImprovement);
		if ("1".equals(appStatus)) {
			String ccToAddress = "erp_development@inateck.com";
			List<User> users = systemService.findUserByRealPermission("psi:purchase:member");
			for (User user : users) {
				ccToAddress = ccToAddress + "," + user.getEmail();
			}
			//发邮件通知(销售，产品，设计，客服，品质，采购)
			String toAddress = "amazon-sales@inateck.com,after-sales@inateck.com,pmg@inateck.com,design@inateck.com,qc@inateck.com,sand@inateck.com";
			String content = "<p><span style='font-size:20px'>Hi All:<br/>&nbsp;&nbsp;&nbsp;&nbsp;以下是产品变更详情，请知悉。</span>" +
					"<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/productImprovement/view?id="+productImprovement.getId()+"'>查看详情</a></p>";
			String improvementEmail = getEmailContent(productImprovement, "2");
			final MailInfo mailInfo = new MailInfo(toAddress, "产品变更通知", new Date());
			mailInfo.setContent(content + improvementEmail);
			mailInfo.setCcToAddress(ccToAddress);
			new Thread(){
			    public void run(){
			    	mailManager.send(mailInfo);
				}
			}.start();
		} else {
			//审批否决通知创建人
			String toAddress = productImprovement.getCreateBy().getEmail();
			String content = "<p><span style='font-size:20px'>Hi "+productImprovement.getCreateBy().getName()+":<br/>&nbsp;&nbsp;&nbsp;&nbsp;你提交的产品变更申请已被否决，请知悉。</span>" +
					"<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/productImprovement/view?id="+productImprovement.getId()+"'>查看详情</a></p>";
			String improvementEmail = getEmailContent(productImprovement, "2");
			final MailInfo mailInfo = new MailInfo(toAddress, "产品变更审批否决通知", new Date());
			mailInfo.setContent(content + improvementEmail);
			new Thread(){
			    public void run(){
			    	mailManager.send(mailInfo);
				}
			}.start();
		}
		addMessage(redirectAttributes, "操作成功!");
		return "redirect:"+Global.getAdminPath()+"/psi/productImprovement/?repage";
	}
	
	@RequestMapping(value = "delete")
	public String delete(PsiProductImprovement productImprovement, RedirectAttributes redirectAttributes) {
		productImprovement.setDelFlag("1");
		psiProductImprovementService.save(productImprovement);
		addMessage(redirectAttributes, "删除产品变更信息成功");
		return "redirect:"+Global.getAdminPath()+"/psi/productImprovement/?repage";
	}
	  
	@RequestMapping(value = "getTips")
	@ResponseBody
	public String getTips(String name) {
		return psiProductImprovementService.getTips(name);
	}
	
	/**
	 * 
	 * @param productImprovement
	 * @param flag	1:通知审批或填写意见  2：产品变更生效通知
	 * @return
	 */
	private String getEmailContent(PsiProductImprovement productImprovement, String flag) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		StringBuffer contents = new StringBuffer("");
		contents.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
		contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe;'>" +
				"<td colspan='5'><span style='font-weight: bold;font-size:25px'>产品变更单</span></td></tr>");
		
		contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
		contents.append("<td>变更产品</td>");
		contents.append("<td>"+productImprovement.getProductName()+"("+productImprovement.getLine()+"线)</td>");
		contents.append("</tr>");
		
		contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
		contents.append("<td>变更时间</td>");
		contents.append("<td>"+format.format(productImprovement.getImproveDate())+"</td>");
		contents.append("</tr>");
		
		String color = "";	//特急
		if ("3".equals(productImprovement.getType())) {
			color = "style='color:red'";
		}
		contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
		contents.append("<td>紧急度</td>");
		contents.append("<td "+color+">"+("1".equals(productImprovement.getType())?"普通":("2".equals(productImprovement.getType())?"紧急":"特急"))+"</td>");
		contents.append("</tr>");
		
		contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
		contents.append("<td>变更订单</td>");
		contents.append("<td>"+productImprovement.getOrderNo()+"</td>");
		contents.append("</tr>");
		
		contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
		contents.append("<td>变更原因</td>");
		contents.append("<td>"+productImprovement.getReason()+"</td>");
		contents.append("</tr>");
		
		contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
		contents.append("<td>变更前说明</td>");
		contents.append("<td>"+productImprovement.getPerRemark()+"</td>");
		contents.append("</tr>");
		
		contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
		contents.append("<td>变更后说明</td>");
		contents.append("<td>"+productImprovement.getAfterRemark()+"</td>");
		contents.append("</tr>");
		
		if ("2".equals(flag)) {
			contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
			contents.append("<td>审批意见</td>");
			contents.append("<td>"+productImprovement.getApprovalContent()+"</td>");
			contents.append("</tr>");
			
			contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe;'>" +
					"<td colspan='5'><span style='font-weight: bold;font-size:25px'>各部门处理意见</span></td></tr>");
			
			contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
			contents.append("<td>部门</td>");
			contents.append("<td>处理意见</td>");
			contents.append("</tr>");
			for (PsiProductImprovementItem item : productImprovement.getItems()) {
				contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
				contents.append("<td>"+item.getDepartment()+"</td>");
				contents.append("<td>"+item.getContent()+"</td>");
				contents.append("</tr>");
			}
		}
		contents.append("</table><br/>");
		return contents.toString();
	}

}
