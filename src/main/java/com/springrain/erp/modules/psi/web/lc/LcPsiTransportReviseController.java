/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web.lc;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.excel.ExportTransportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportRevise;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportReviseService;

/**
 * 运单付款修正表Controller
 * @author Michael
 * @version 2015-01-29
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/lcPsiTransportRevise")
public class LcPsiTransportReviseController extends BaseController {
	
	
	@Autowired
	private LcPsiTransportReviseService psiTransportReviseService;
	@Autowired
	private PsiSupplierService psiSupplierService;
	
	
	@RequiresPermissions("psi:tranRevise:view")
	@RequestMapping(value = {"list", ""})
	public String list(LcPsiTransportRevise psiTransportRevise, HttpServletRequest request, HttpServletResponse response, Model model) {
        Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (psiTransportRevise.getApplyDate() == null) {
			psiTransportRevise.setApplyDate(DateUtils.addMonths(today, -1));
			psiTransportRevise.setSureDate(today);
		}
		Page<LcPsiTransportRevise>  page = new Page<LcPsiTransportRevise>(request, response);
		psiTransportReviseService.find(page, psiTransportRevise); 
        List<PsiSupplier> suppliers=this.psiSupplierService.findAllTransporter();
		model.addAttribute("suppliers", suppliers);
        model.addAttribute("page", page);
		return "modules/psi/lc/lcPsiTransportReviseList";
	}
	
	@RequiresPermissions("psi:tranRevise:edit")
	@RequestMapping(value = "save")
	public String save(LcPsiTransportRevise psiTransportRevise,@RequestParam("filePath") MultipartFile[] filePaths, Model model,RedirectAttributes redirectAttributes) {
		//获取该运单，该供应商，有没有为完成的修正项，如果有，不保存，强制其先完成上个单
		List<LcPsiTransportRevise> list =this.psiTransportReviseService.findUpDoneRevisePay(psiTransportRevise.getSupplier().getId(),psiTransportRevise.getTranOrderId());
		if(list==null||list.size()==0){
			psiTransportReviseService.addSave(psiTransportRevise,filePaths);
		}else{
			addMessage(redirectAttributes, "该运单已经有修正单在申请，请先完成上个修正单");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportRevise/?repage";
	}
	
	@RequiresPermissions("psi:tranRevise:view")
	@RequestMapping(value = "view")
	public String view(LcPsiTransportRevise psiTransportRevise, Model model) {
		if(psiTransportRevise.getId()!=null){
			psiTransportRevise=this.psiTransportReviseService.get(psiTransportRevise.getId());
		}
		model.addAttribute("psiTransportRevise", psiTransportRevise);
		return "modules/psi/lc/lcPsiTransportReviseView";
	}
	
	@RequiresPermissions("psi:tranRevise:review")
	@RequestMapping(value = "review")
	public String review(LcPsiTransportRevise psiTransportRevise, Model model) {
		if(psiTransportRevise.getId()!=null){
			psiTransportRevise=this.psiTransportReviseService.get(psiTransportRevise.getId());
		}
		model.addAttribute("psiTransportRevise", psiTransportRevise);
		if("0".equals(psiTransportRevise.getReviseSta())){
			return "modules/psi/lc/lcPsiTransportReviseReview";
		}else{
			return "modules/psi/lc/lcPsiTransportReviseView";
		}
		
	}
	
	@RequiresPermissions("psi:tranRevise:review")
	@RequestMapping(value = "reviewSave")
	public String reviewSave(LcPsiTransportRevise psiTransportRevise, RedirectAttributes redirectAttributes) {
		psiTransportRevise=this.psiTransportReviseService.get(psiTransportRevise.getId());
		if(psiTransportReviseService.reviewSave(psiTransportRevise)){
			addMessage(redirectAttributes, "审核运单修正付款'" + psiTransportRevise.getPaymentNo() + "'成功");
		}else{
			addMessage(redirectAttributes, "申请邮件发送失败，请重新申请!!");
		};
		
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportRevise/?repage";
	}
	
	
	@RequiresPermissions("psi:tranRevise:sure")
	@RequestMapping(value = "sure")
	public String sure(LcPsiTransportRevise psiTransportRevise, Model model) {
		if(psiTransportRevise.getId()!=null){
			psiTransportRevise=this.psiTransportReviseService.get(psiTransportRevise.getId());
		}
		model.addAttribute("psiTransportRevise", psiTransportRevise);
		return "modules/psi/lc/lcPsiTransportReviseSure";
	}
	
	@RequiresPermissions("psi:tranRevise:sure")
	@RequestMapping(value = "sureSave")
	public String sureSave(@RequestParam("attchmentFile")MultipartFile[] attchmentFiles,LcPsiTransportRevise psiTransportRevise, RedirectAttributes redirectAttributes) {
		this.psiTransportReviseService.sureSave(psiTransportRevise,attchmentFiles);
		addMessage(redirectAttributes, "确认运单修正付款'" + psiTransportRevise.getPaymentNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportRevise/?repage";
	}
	
	@RequiresPermissions("psi:tranRevise:edit")
	@RequestMapping(value = "cancel")
	public String cancel(LcPsiTransportRevise psiTransportRevise,RedirectAttributes redirectAttributes){
		if(psiTransportRevise.getId()!=null){
			psiTransportRevise=this.psiTransportReviseService.get(psiTransportRevise.getId());
			psiTransportRevise.setReviseSta("8");
			psiTransportReviseService.save(psiTransportRevise);
			addMessage(redirectAttributes, "取消运单付款修正'" + psiTransportRevise.getPaymentNo() + "'成功");
			return "redirect:"+Global.getAdminPath()+"/psi/lcPsiTransportRevise/?repage";
		}
		return null;
	}
	
	@RequestMapping(value = "printPayment")
	public String printPayment(LcPsiTransportRevise psiTransportRevise, Model model,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
		if(psiTransportRevise.getId()!=null){
			psiTransportRevise=this.psiTransportReviseService.get(psiTransportRevise.getId());
			psiTransportRevise.setFlowInfo("编号：");
			psiTransportRevise.setApplyTime(new SimpleDateFormat("yyyy/MM/dd").format(psiTransportRevise.getApplyDate()));
			psiTransportRevise.setSureTime(psiTransportRevise.getSureDate()!=null?new SimpleDateFormat("yyyy/MM/dd").format(psiTransportRevise.getSureDate()):"");
			psiTransportRevise.setMoneyInfo(psiTransportRevise.getCurrency()+"  "+psiTransportRevise.getReviseAmount());
			psiTransportRevise.setSupplierName(psiTransportRevise.getSupplier().getName());
			psiTransportRevise.setTransportNoRemark("运单号:"+psiTransportRevise.getTranOrderNo());
			psiTransportRevise.setApplyUserInfo(psiTransportRevise.getApplyUser()==null?"":psiTransportRevise.getApplyUser().getName());
			psiTransportRevise.setApplyInfo(psiTransportRevise.getApplyUserInfo()+"申请,申请时间:"+new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			psiTransportRevise.setCheckInfo((psiTransportRevise.getApplyUser()!=null?psiTransportRevise.getApplyUser().getName():"")+"审核,审核时间:"+new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			psiTransportRevise.setSupplierAccount(psiTransportRevise.getAccount());
			String modelName = "paymentApplication";//模板文件名称
			String xmlName = "paymentApplication";
			ExportTransportExcel ete = new ExportTransportExcel();
			Workbook  workbook = ete.writeData(psiTransportRevise, xmlName,modelName, 0);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = modelName + sdf.format(new Date()) + ".xlsx";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition","attachment;filename=" + fileName);
			try {
				OutputStream out = response.getOutputStream();
				workbook.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
