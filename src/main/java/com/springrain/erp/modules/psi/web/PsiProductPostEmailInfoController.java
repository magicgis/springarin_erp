package com.springrain.erp.modules.psi.web;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiProductPostMailInfo;
import com.springrain.erp.modules.psi.service.PsiProductPostMailInfoService;

@Controller
@RequestMapping(value = "${adminPath}/psi/productPostEmailInfo")
public class PsiProductPostEmailInfoController extends BaseController {

	@Autowired
	private PsiProductPostMailInfoService postMailService;
	
	@RequestMapping(value = {"list", ""})
	public String list(PsiProductPostMailInfo postInfo, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		Page<PsiProductPostMailInfo> page=new Page<PsiProductPostMailInfo>(request, response);
		if(StringUtils.isEmpty(postInfo.getCountry())){
			postInfo.setCountry("de");
		}
		postMailService.find(page, postInfo); 
        model.addAttribute("page", page);
		return "modules/psi/psiProductPostEmailInfoList";
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"updateStatus"})
	public String updateStatus(Integer id,String status) {
		return this.postMailService.updateStatus(id, status);
	}
	
	
}
