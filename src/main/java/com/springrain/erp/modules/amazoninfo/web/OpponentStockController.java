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

import com.springrain.erp.modules.amazoninfo.entity.OpponentStock;
import com.springrain.erp.modules.amazoninfo.service.OpponentStockService;
import com.springrain.erp.modules.amazoninfo.service.ProductDirectoryCommentService;

import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;

/**
 * 对手库存数Controller
 * @author Michael
 * @version 2015-08-24
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/opponentStock")
public class OpponentStockController extends BaseController {
	
	@Autowired     
	private OpponentStockService OpponentStockService;
	
	@Autowired     
	private ProductDirectoryCommentService directoryService;
	
	
	@RequestMapping(value = {"list", ""})
	public String list(OpponentStock opponentStock,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		//找出一个月之前的日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDay =DateUtils.addDays(sdf.parse(sdf.format(new Date())),-30);
		List<OpponentStock> list = this.OpponentStockService.find(opponentStock, false,startDay);
		model.addAttribute("title", directoryService.getTitleByAsin(opponentStock.getAsin(), opponentStock.getCountry()));
		model.addAttribute("asin", opponentStock.getAsin());
		model.addAttribute("list", list);
		return "modules/amazoninfo/opponentStockList";
	}
	
	
	
	
}
