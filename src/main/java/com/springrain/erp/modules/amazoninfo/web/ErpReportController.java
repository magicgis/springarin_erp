package com.springrain.erp.modules.amazoninfo.web;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springrain.erp.common.web.BaseController;

/**
 * 系统报表Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/erpReport")
public class ErpReportController extends BaseController {

	/*@Autowired
	private PsiProductTypeGroupDictService psiTypeGroupService;*/
	
	/**
	 * 
	 * @param reportType 报表分类
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = {"list", ""})
	public String list(String reportType, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		/*//设置初始条件参数
		Date today = new Date();
		model.addAttribute("country", "de");
		model.addAttribute("start", DateUtils.addMonths(today, -1));
		model.addAttribute("end", today);
		model.addAttribute("yesterdy", DateUtils.addDays(today, -1));
		model.addAttribute("currencyType", "EUR");
		model.addAttribute("searchType", "1");
		model.addAttribute("groupType", psiTypeGroupService.getAllList());*/
		return "modules/amazoninfo/erpReportList";
	}
	
}