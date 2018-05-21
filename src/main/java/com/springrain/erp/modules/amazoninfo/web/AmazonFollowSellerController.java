/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.web;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonFollowSeller;
import com.springrain.erp.modules.amazoninfo.service.AmazonFollowSellerService;

/**
 * 对手库存数Controller
 * @author Michael
 * @version 2015-08-24
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/followSeller")
public class AmazonFollowSellerController extends BaseController {
	
	@Autowired     
	private AmazonFollowSellerService followSellerService;
	
	
	
	@RequestMapping(value = {"list", ""})
	public String list(AmazonFollowSeller followSeller,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(followSeller.getDataDate()==null){
			followSeller.setDataDate(sdf.parse(sdf.format(new Date())));
			followSeller.setUpdateDate(followSeller.getDataDate());
		}
		List<AmazonFollowSeller> follows = this.followSellerService.find(followSeller);
		model.addAttribute("followSeller", followSeller);
		model.addAttribute("follows", follows);
		return "modules/amazoninfo/followSellerList";
	}
	
	
	
	
}
