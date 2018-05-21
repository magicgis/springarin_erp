/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.common.collect.Lists;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiInventoryRevisionLog;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.service.PsiInventoryOutService;
import com.springrain.erp.modules.psi.service.PsiInventoryRevisionLogService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.StockService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;

/**
 * 仓库进出库明细Controller
 * @author Michael
 * @version 2014-12-24
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiInventoryRevisionLog")
public class PsiInventoryRevisionLogController extends BaseController {

	@Autowired
	private PsiInventoryRevisionLogService psiInventoryRevisionLogService;
	@Autowired
	private PsiProductService productService;
	@Autowired
	private StockService stockService;
	@Autowired
	private PsiInventoryService psiInventoryService;
	@Autowired
	private PsiInventoryOutService psiInventoryOutService;
	@Autowired
	private SystemService userService;
	
	@RequestMapping(value = {"list", ""})
	public String list(PsiInventoryRevisionLog psiInventoryRevisionLog, HttpServletRequest request, HttpServletResponse response, Model model) {
        PsiProduct  product = new PsiProduct();
       // Stock  stock  = new Stock();
        List<PsiInventory> psiInventorys = Lists.newArrayList();
        Integer warehouseId 	= psiInventoryRevisionLog.getWarehouseId();
        Integer productId		= psiInventoryRevisionLog.getProductId();
        String  color 			= psiInventoryRevisionLog.getColorCode();
        String  country			= psiInventoryRevisionLog.getCountryCode();
        String  showFlag 		= psiInventoryRevisionLog.getShowFlag();
        StringBuffer sb = new StringBuffer("");
		Page<PsiInventoryRevisionLog> page = psiInventoryRevisionLogService.findBySingleProduct(new Page<PsiInventoryRevisionLog>(request, response,20), psiInventoryRevisionLog,sb); 
	    if(productId!=null){
        	product=this.productService.get(productId);
        } 
	  //查询所有仓库：
  		List<Stock> stocks =stockService.findStocks("0");
  		if(psiInventoryRevisionLog.getWarehouseId()==null&&stocks.size()>0){
  			psiInventoryRevisionLog.setWarehouseId(stocks.get(0).getId());
  		}
  		//stock = stockService.get(warehouseId);
  		psiInventorys=psiInventoryService.findInventorySum(productId, warehouseId, country,color);
  		
  		List<Object[]> sumArr = this.psiInventoryRevisionLogService.getSumdata(sb,showFlag);
  		List<Object[]> formArr = Lists.newArrayList();
  		List<Object[]> noFormArr= Lists.newArrayList();
  		if(showFlag.equals("1")){
  			for(Object[] obj:sumArr){
  				if(psiInventoryRevisionLog.getOperationType()!=null&&!psiInventoryRevisionLog.getOperationType().equals("")){
  					if(psiInventoryRevisionLog.getOperationType().equals(obj[0])){
  						if(obj[0].toString().contains("From_")){
  		  					formArr.add(obj);
  		  					break;
  		  				}else{
  		  					noFormArr.add(obj);
  		  					break;
  		  				}
  					}
  				}else{
	  				if(obj[0].toString().contains("From_")){
	  					formArr.add(obj);
	  				}else{
	  					noFormArr.add(obj);
	  				}
  				}
  			}
  		}
  		String colors = product.getColor();
  		String countrys = product.getPlatform();
  		String[] colorArr =colors.split(",");
  		if(colorArr==null||colorArr.length==0){
  			colorArr=new String[]{""};
  		}
  		String[] countryArr = countrys.split(",");
  		if(color==null){
  			psiInventoryRevisionLog.setColorCode("All");
  		}
  		
  		List<User> allUser = userService.findAllUsers();
  		
  		
  		//如果是批量出库显示文件名
  		for(PsiInventoryRevisionLog log:page.getList()){
  			if("Lot Delivery".equals(log.getOperationType())){
  				String originName=this.psiInventoryOutService.getOrignFileName(log.getRelativeNumber());
  				if(StringUtils.isNotEmpty(originName)){
  					log.setLotFileName(originName.split(",,,")[0]);
  	  				log.setLotFileUrl(originName.split(",,,")[1]);
  				}
  				
  			}
  		}
  		
  		
  		model.addAttribute("formArr", formArr);
  		model.addAttribute("noFormArr", noFormArr);
  		model.addAttribute("allUser", allUser);
  		model.addAttribute("sumArr", sumArr);
  		model.addAttribute("colorArr", colorArr);
  		model.addAttribute("countryArr", countryArr);
  		model.addAttribute("psiInventorys", psiInventorys);
  		model.addAttribute("stocks", stocks);
        model.addAttribute("product", product);
        model.addAttribute("page", page);
		return "modules/psi/psiInventoryRevisionLogList";
	}

}
