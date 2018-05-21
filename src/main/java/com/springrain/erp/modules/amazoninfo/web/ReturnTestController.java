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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.ReturnTest;
import com.springrain.erp.modules.amazoninfo.service.ReturnTestService;
import com.springrain.erp.modules.psi.entity.PsiInventory;
import com.springrain.erp.modules.psi.entity.PsiInventoryIn;
import com.springrain.erp.modules.psi.entity.PsiInventoryInItem;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.StockService;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 测试检测Controller
 * @author Michael
 * @version 2015-08-24
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/returnTest")
public class ReturnTestController extends BaseController {
	@Autowired
	private ReturnTestService 			returnTestService;
	@Autowired
	private StockService 				stockService;
	@Autowired
	private PsiProductService 			productService;
	@Autowired
	private PsiInventoryService 		psiInventoryService;
	@Autowired
	private PsiProductService 			psiProductService;
	@Autowired
	private PsiInventoryService  	    inventoryService;
	
	@RequestMapping(value = {"list", ""})
	public String list(ReturnTest returnTest, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
		if(returnTest.getCreateDate()==null){
			returnTest.setCreateDate(sdf.parse((sdf.format(DateUtils.addMonths(new Date(), -3)))));
		}
		if(returnTest.getUpdateDate()==null){
			returnTest.setUpdateDate(sdf.parse((sdf.format(new Date()))));
		}
		
        Page<ReturnTest> page = returnTestService.find(new Page<ReturnTest>(request, response), returnTest); 
        model.addAttribute("page", page);
		return "modules/amazoninfo/returnTestList";
	}

	@RequestMapping(value = "form")
	public String form(ReturnTest returnTest, Model model) {
		Integer warehouseIdEdit = returnTest.getWarehouseId();
		if(returnTest.getId()!=null){
			returnTest=this.returnTestService.get(returnTest.getId());
		}
		
		if(warehouseIdEdit!=null){//编辑页面也可以切换仓库
			returnTest.setWarehouseId(warehouseIdEdit);
		}
		//根据用户权限获得仓库可选择信息
		List<Stock> stocks=stockService.findStocks("0");
		List<Stock> tempStocks = Lists.newArrayList();
		//首次进来
		Set<String> permissionsSet = Sets.newHashSet();
		Set<String> countrySet = Sets.newHashSet();
		//查询权限
		User user = UserUtils.getUser();
		if(!user.isAdmin()){
			for(Role role:UserUtils.getUser().getRoleList()){
				permissionsSet.addAll(role.getPermissions());
			}
			for(Stock stock:stocks){
				String countryCode=stock.getCountrycode();
				if(permissionsSet.contains("psi:inventory:edit:"+countryCode+"")){
					tempStocks.add(stock);
				}
			}
			stocks=tempStocks;
		}
		
		if(stocks!=null&&stocks.size()>0){
			String countryCode = "";
			if(returnTest.getWarehouseId()==null){
				returnTest.setWarehouseId(stocks.get(0).getId());
			}
			for(Stock stock:stocks){
				if(returnTest.getWarehouseId().equals(stock.getId())){
					countryCode=stock.getCountrycode();
					break;
				}
			}
				
			if("US".equals(countryCode)){
				countrySet.add("com");
				countrySet.add("ca");
			}else if("DE".equals(countryCode)){
				countrySet.add("de");
				countrySet.add("fr");
				countrySet.add("it");
				countrySet.add("uk");
				countrySet.add("es");
			}
			
			List<PsiSku> skuList = productService.getSkus(countrySet);
			Map<String,PsiSku> skuMap = Maps.newHashMap();
			for(PsiSku sku:skuList){
				if("1".equals(sku.getUseBarcode())){   
					skuMap.put(sku.getSku(),new PsiSku(sku.getSku(), sku.getProductId(), sku.getCountry(), sku.getColor(), sku.getProductName()));
				}
			}
			
			//查询库存所有的sku  
			List<PsiInventory> inventorys=this.psiInventoryService.findByStock(returnTest.getWarehouseId());
			for(PsiInventory inventory:inventorys){
				skuMap.put(inventory.getSku(),new PsiSku(inventory.getSku(), inventory.getProductId(), inventory.getCountryCode(), inventory.getColorCode(), inventory.getProductName()));
			}
			
	        model.addAttribute("stocks", stocks);
	        model.addAttribute("skus", skuMap);
	        model.addAttribute("skuMap",JSON.toJSON(skuMap));
	       
		}
	
		model.addAttribute("returnTest", returnTest);
		return "modules/amazoninfo/returnTestForm";
	}
  
	

	@RequestMapping(value = "view")
	public String view(ReturnTest returnTest, Model model) {
		if(returnTest.getId()!=null){
			returnTest=this.returnTestService.get(returnTest.getId());
		}
		model.addAttribute("returnTest", returnTest);
		return "modules/amazoninfo/returnTestView";
	}
	@RequestMapping(value = "save")
	public String save(ReturnTest returnTest,String inStockSta, Model model, RedirectAttributes redirectAttributes) {
		returnTestService.save(returnTest);
		if("1".equals(inStockSta)){
			Integer productId =0;
			String colorCode="";
			String countryCode="";
			PsiSku psiSku=this.psiProductService.getSkuBySku(returnTest.getSku(),"1");//如果没绑定可以从库里获得productId等
			if(psiSku!=null){
				productId = psiSku.getProductId();
				colorCode = psiSku.getColor();
				countryCode = psiSku.getCountry();
			}else{
				PsiInventory inv = inventoryService.findBySku(returnTest.getSku(), returnTest.getWarehouseId());
				productId = inv.getProductId();
				colorCode = inv.getColorCode();
				countryCode = inv.getCountryCode();
			}
			//转向出库页面
			PsiInventoryIn  inventoryIn = new PsiInventoryIn();
			if(returnTest.getNewQuantity()!=null){
				PsiInventoryInItem item = new PsiInventoryInItem();
				item.setSku(returnTest.getSku());
				item.setQualityType("new");
				item.setQuantity(returnTest.getNewQuantity());
				item.setColorCode(colorCode);
				item.setCountryCode(countryCode);
				item.setProductId(productId);
				item.setProductName(returnTest.getProductName());
				inventoryIn.getItems().add(item);
			}
			if(returnTest.getRenewQuantity()!=null){
				PsiInventoryInItem item = new PsiInventoryInItem();
				item.setSku(returnTest.getSku());
				item.setQualityType("renew");
				item.setQuantity(returnTest.getRenewQuantity());
				item.setColorCode(colorCode);
				item.setCountryCode(countryCode);
				item.setProductId(productId);
				item.setProductName(returnTest.getProductName());
				inventoryIn.getItems().add(item);		
			}
			if(returnTest.getOldQuantity()!=null){
				PsiInventoryInItem item = new PsiInventoryInItem();
				item.setSku(returnTest.getSku());
				item.setQualityType("old");
				item.setQuantity(returnTest.getOldQuantity());
				item.setColorCode(colorCode);
				item.setCountryCode(countryCode);
				item.setProductId(productId);
				item.setProductName(returnTest.getProductName());
				inventoryIn.getItems().add(item);
			}
			if(returnTest.getBrokenQuantity()!=null){
				PsiInventoryInItem item = new PsiInventoryInItem();
				item.setSku(returnTest.getSku());
				item.setQualityType("broken");
				item.setQuantity(returnTest.getBrokenQuantity());
				item.setColorCode(colorCode);
				item.setCountryCode(countryCode);
				item.setProductId(productId);
				item.setProductName(returnTest.getProductName());
				inventoryIn.getItems().add(item);
			}
			
			inventoryIn.setWarehouseId(returnTest.getWarehouseId());
			inventoryIn.setWarehouseName(returnTest.getWarehouseName());
			model.addAttribute("returnTestId", returnTest.getId());
			model.addAttribute("psiInventoryIn", inventoryIn);
			return "modules/psi/psiInventoryInReturn";
		}else{
			addMessage(redirectAttributes, "保存评测退货测试'" + returnTest.getSku()+ "'成功");
			return "redirect:"+Global.getAdminPath()+"/amazoninfo/returnTest/?repage";
		}
		
	}
	
	
	
	@RequestMapping(value = "cancel")
	public String cancel(ReturnTest returnTest, Model model,RedirectAttributes redirectAttributes) {
		returnTest = this.returnTestService.get(returnTest.getId());
		this.returnTestService.save(returnTest);
		addMessage(redirectAttributes, "取消评测退货测试'" + returnTest.getSku()+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/returnTest/?repage";
	}
	
}
