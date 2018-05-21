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
import com.springrain.erp.modules.amazoninfo.entity.OpponentAsin;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.OpponentAsinService;
import com.springrain.erp.modules.amazoninfo.service.OpponentStockService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 竞争对手产品监控Controller
 * @author Michael
 * @version 2015-08-24
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/opponentAsin")
public class OpponentAsinController extends BaseController {

	@Autowired
	private OpponentAsinService opponentAsinService;
	@Autowired
	private OpponentStockService opponentStockService;
	@Autowired
	private AmazonProduct2Service ama2Service;
	
	@RequestMapping(value = {"list", ""})
	public String list(OpponentAsin opponentAsin,String isCheck, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		if(StringUtils.isEmpty(isCheck)){
			isCheck="1";
		}
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
		if(opponentAsin.getCreateDate()==null){
			opponentAsin.setCreateDate(sdf.parse((sdf.format(DateUtils.addMonths(new Date(), -3)))));
		}
        Page<OpponentAsin> page = opponentAsinService.find(new Page<OpponentAsin>(request, response), opponentAsin,isCheck); 
        Map<String,Map<String,Integer>> saleMap =opponentStockService.getAsin30Sale(DateUtils.addDays(sdf.parse(sdf.format(new Date())),-30));
        model.addAttribute("isCheck", isCheck);
		model.addAttribute("saleMap", saleMap);
        model.addAttribute("page", page);
		return "modules/amazoninfo/opponentAsinList";
	}

	@RequestMapping(value = "form")
	public String form(OpponentAsin opponentAsin, Model model) {
		if(opponentAsin.getId()!=null){
			opponentAsin=this.opponentAsinService.get(opponentAsin.getId());
		}
		model.addAttribute("opponentAsin", opponentAsin);
		return "modules/amazoninfo/opponentAsinForm";
	}
		
	@RequestMapping(value = "show")
	public String show(OpponentAsin opponentAsin, Model model) {
		if(opponentAsin.getId()!=null){
			opponentAsin=this.opponentAsinService.get(opponentAsin.getId());
		}
		model.addAttribute("opponentAsin", opponentAsin);
	return "modules/amazoninfo/opponentAsinBadList";
	}
  
  
	@RequestMapping(value = "save")
	public String save(OpponentAsin opponentAsin, Model model, RedirectAttributes redirectAttributes) {
		if(opponentAsin.getId()==null){
			opponentAsin.setCreateDate(new Date());
			opponentAsin.setCreateUser(UserUtils.getUser());
		}
		if(opponentAsin.getAsin().contains(",")){
			String[] asinArr=opponentAsin.getAsin().split(",");
			String[] nameArr=opponentAsin.getProductName().split(",");	
			List<OpponentAsin> list=Lists.newArrayList();
			for (int i=0;i<asinArr.length;i++) {
				OpponentAsin temp=new OpponentAsin();
				temp.setAsin(asinArr[i]);
				temp.setProductName(nameArr[i]);
				temp.setCreateDate(new Date());
				temp.setCreateUser(UserUtils.getUser());
				temp.setCountry(opponentAsin.getCountry());
				temp.setState("1");
				list.add(temp);
			}
			opponentAsinService.save(list);
		}else{
			opponentAsinService.save(opponentAsin);
		}
		addMessage(redirectAttributes, "保存对手销量监控'" + ""+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/opponentAsin/?repage";
	}
	
	@RequestMapping(value = "cancel")
	public String cancel(OpponentAsin opponentAsin, Model model,RedirectAttributes redirectAttributes) {
		opponentAsin = this.opponentAsinService.get(opponentAsin.getId());
		opponentAsin.setState("0");//已取消
		this.opponentAsinService.save(opponentAsin);
		addMessage(redirectAttributes, "取消对手销量监控'" + opponentAsin.getProductName()+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/opponentAsin/?repage";
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
