/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.FollowAsin;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.FollowAsinService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 跟卖产品产品监控Controller
 * @author Michael
 * @version 2015-08-24
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/followAsin")
public class FollowAsinController extends BaseController {

	@Autowired
	private FollowAsinService followAsinService;
	
	@Autowired
	private AmazonProduct2Service ama2Service;
	
	@Autowired
	private PsiProductService  productService;
	
	@RequestMapping(value = {"list", ""})
	public String list(FollowAsin followAsin,String isCheck, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		if(StringUtils.isEmpty(isCheck)){
			isCheck="1";
		}
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
		if(followAsin.getCreateDate()==null){
			followAsin.setCreateDate(sdf.parse((sdf.format(DateUtils.addMonths(new Date(), -3)))));
		}
        Page<FollowAsin> page = followAsinService.find(new Page<FollowAsin>(request, response), followAsin,isCheck); 
        model.addAttribute("isCheck", isCheck);
        model.addAttribute("page", page);
		return "modules/amazoninfo/followAsinList";
	}

	@RequestMapping(value = "form")
	public String form(FollowAsin followAsin, Model model) {
		if(followAsin.getId()!=null){
			followAsin=this.followAsinService.get(followAsin.getId());
		}
		Map<String,String> asinMap =this.ama2Service.getAsinByCountry(followAsin.getCountry());
		model.addAttribute("followAsin", followAsin);
		model.addAttribute("asinMap", asinMap);
		return "modules/amazoninfo/followAsinForm";
	}
		
	@RequestMapping(value = "show")
	public String show(FollowAsin followAsin, Model model) {
		if(followAsin.getId()!=null){
			followAsin=this.followAsinService.get(followAsin.getId());
		}
		model.addAttribute("followAsin", followAsin);
	return "modules/amazoninfo/followAsinBadList";
	}
  
  
	@RequestMapping(value = "save")
	public String save(FollowAsin followAsin, Model model, RedirectAttributes redirectAttributes) {
		if(followAsin.getId()==null){
			followAsin.setCreateDate(new Date());
			followAsin.setCreateUser(UserUtils.getUser());
		}
		if(followAsin.getAsin().contains(",")){
			String[] asinArr=followAsin.getAsin().split(",");
			List<FollowAsin> list=Lists.newArrayList();
			Map<String,String> asinMap =this.productService.getProductNameByAsin();
			for (int i=0;i<asinArr.length;i++) {
				FollowAsin temp=new FollowAsin();
				temp.setAsin(asinArr[i]);
				
				temp.setProductName(asinMap.get(temp.getAsin()));
				temp.setCreateDate(new Date());
				temp.setCreateUser(UserUtils.getUser());
				temp.setCountry(followAsin.getCountry());
				temp.setState("1");
				list.add(temp);
			}
			followAsinService.save(list);
		}else{
			followAsinService.save(followAsin);
		}
		addMessage(redirectAttributes, "保存跟卖产品监控'" + ""+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/followAsin/?repage";
	}
	
	@RequestMapping(value = "cancel")
	public String cancel(FollowAsin followAsin, Model model,RedirectAttributes redirectAttributes) {
		followAsin = this.followAsinService.get(followAsin.getId());
		followAsin.setState("0");//已取消
		this.followAsinService.save(followAsin);
		addMessage(redirectAttributes, "取消跟卖产品监控'" + followAsin.getProductName()+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/followAsin/?repage";
	}
	
	@RequestMapping(value = "noCancel")
	public String noCancel(FollowAsin followAsin, Model model,RedirectAttributes redirectAttributes) {
		followAsin = this.followAsinService.get(followAsin.getId());
		followAsin.setState("1");//已恢复
		this.followAsinService.save(followAsin);
		addMessage(redirectAttributes, "恢复跟卖产品监控'" + followAsin.getProductName()+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/followAsin/?repage";
	}
	
	@ResponseBody
	@RequestMapping(value = {"ajaxSelfAsin"})
	public String ajaxSelfAsin(String asin) {
		String res="";
		Set<String> set = ama2Service.getAllAsin();
		if(set.contains(asin)){
			res="1";
		}
		String rs="{\"msg\":\""+res+"\"}";
		return rs;
	} 
	
}
