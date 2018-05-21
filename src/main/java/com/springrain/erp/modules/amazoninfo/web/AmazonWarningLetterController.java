package com.springrain.erp.modules.amazoninfo.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonWarningLetter;
import com.springrain.erp.modules.amazoninfo.service.AmazonWarningLetterService;

/**
 * 亚马逊警告信件信息Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/warningLetter")
public class AmazonWarningLetterController extends BaseController {

	@Autowired
	private AmazonWarningLetterService warningLetterService;
	
	@ModelAttribute
	public AmazonWarningLetter get(@RequestParam(required=false) Integer id) {
		if (id != null){
			return warningLetterService.get(id);
		}else{
			return new AmazonWarningLetter();
		}
	}
	
	@RequestMapping(value = {"list", ""})
	public String list(AmazonWarningLetter warningLetter, HttpServletRequest request, HttpServletResponse response, Model model) {
		if (StringUtils.isNotBlank(warningLetter.getSubject())) {
			try {
				warningLetter.setSubject(URLDecoder.decode(warningLetter.getSubject(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {}
		}
		Page<AmazonWarningLetter> page = new Page<AmazonWarningLetter>(request, response);
		if(StringUtils.isEmpty(page.getOrderBy())){
			page.setOrderBy("letterDate desc");
		}
		page = warningLetterService.find(page, warningLetter); 
		model.addAttribute("page", page);
        model.addAttribute("warningLetter", warningLetter);
		return "modules/amazoninfo/amazonWarningLetterList";
	}
	
	@RequestMapping(value = "form")
	public String form(AmazonWarningLetter warningLetter, Model model) {
		model.addAttribute("warningLetter", warningLetter);
		return "modules/amazoninfo/amazonWarningLetterForm";
	}
}
