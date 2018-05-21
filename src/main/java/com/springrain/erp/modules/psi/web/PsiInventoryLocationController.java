package com.springrain.erp.modules.psi.web;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiInventoryLocation;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.service.PsiInventoryLocationService;
import com.springrain.erp.modules.psi.service.StockService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 库存库位Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiInventoryLocation")
public class PsiInventoryLocationController extends BaseController {
	
	
	@Autowired
	private PsiInventoryLocationService psiInventoryLocationService;

	@Autowired
	private StockService stockService;
	
	@ModelAttribute
	public PsiInventoryLocation get(@RequestParam(required=false) Integer id) {
		if (id != null){
			return psiInventoryLocationService.get(id);
		}else{
			return new PsiInventoryLocation();
		}
	}

	@RequestMapping(value = {"list", ""})
	public String list(PsiInventoryLocation psiInventoryLocation, Integer stockId, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		List<Stock> stocks= stockService.findStocks("0");
		if(stockId == null){
			User user = UserUtils.getUser();
			if(!user.isAdmin()){
				String countryCode = "CN";
				if(UserUtils.hasPermission("psi:inventory:edit:DE")||UserUtils.hasPermission("psi:inventory:revise:DE")){
					countryCode="DE";
				}else if(UserUtils.hasPermission("psi:inventory:edit:CN")||UserUtils.hasPermission("psi:inventory:revise:CN")){
					countryCode="CN";
				}else if(UserUtils.hasPermission("psi:inventory:edit:US")||UserUtils.hasPermission("psi:inventory:revise:US")){
					countryCode="US";
				}else if(UserUtils.hasPermission("psi:inventory:edit:JP")||UserUtils.hasPermission("psi:inventory:revise:JP")){
					countryCode="JP";
				}
				List<Stock> psiStocks =this.stockService.findByCountryCode(countryCode, "",null);
				if(psiStocks!=null&&psiStocks.size()>0){
					stockId = psiStocks.get(0).getId();
				}else{
					stockId = stocks.get(0).getId();
				}
			}else{
				stockId = stocks.get(0).getId();
			}
		}
		Page<PsiInventoryLocation> page = new Page<PsiInventoryLocation>(request, response);
        page = psiInventoryLocationService.find(page, psiInventoryLocation, stockId);
        
        Map<Integer, String> locationMap = psiInventoryLocationService.findAllLocation(stockId);
        model.addAttribute("locationMap", locationMap);
        model.addAttribute("stocks", stocks);
        model.addAttribute("stockId", stockId);
        model.addAttribute("psiInventoryLocation", psiInventoryLocation);
        model.addAttribute("page", page);
		return "modules/psi/psiInventoryLocationList";
	}

	@RequestMapping(value = "locationAdjust")
	public String locationAdjust(PsiInventoryLocation psiInventoryLocation, Integer stockId, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		List<Stock> stocks= stockService.findStocks("0");
		psiInventoryLocation = psiInventoryLocationService.getByUnique(psiInventoryLocation.getSku(),psiInventoryLocation.getStockLocation().getId(),psiInventoryLocation.getSnCode());
        model.addAttribute("stocks", stocks);
        model.addAttribute("stockId", psiInventoryLocation.getStockLocation().getStockArea().getStock().getId());
        model.addAttribute("psiInventoryLocation", psiInventoryLocation);
        return "modules/psi/psiInventoryLocationAdjust";
	}

	@RequestMapping(value = "locationAdjustSave")
	public String locationAdjustSave(Integer sourceId, PsiInventoryLocation psiInventoryLocation, Integer stockId, HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) throws ParseException {
		if (psiInventoryLocation.getNewQuantity() == null) {
			psiInventoryLocation.setNewQuantity(0);
		}
		if (psiInventoryLocation.getOldQuantity() == null) {
			psiInventoryLocation.setOldQuantity(0);
		}
		if (psiInventoryLocation.getOfflineQuantity() == null) {
			psiInventoryLocation.setOfflineQuantity(0);
		}
		if (psiInventoryLocation.getBrokenQuantity() == null) {
			psiInventoryLocation.setBrokenQuantity(0);
		}
		if (psiInventoryLocation.getRenewQuantity() == null) {
			psiInventoryLocation.setRenewQuantity(0);
		}
		if (psiInventoryLocation.getSparesQuantity() == null) {
			psiInventoryLocation.setSparesQuantity(0);
		}
		if (psiInventoryLocation.getTotalQuantity()>0) {
			List<PsiInventoryLocation> list = Lists.newArrayList();
			PsiInventoryLocation sourceLocation = psiInventoryLocationService.get(sourceId);
			list.add(sourceLocation);
			sourceLocation.setNewQuantity(sourceLocation.getNewQuantity()-psiInventoryLocation.getNewQuantity());
			sourceLocation.setOldQuantity(sourceLocation.getOldQuantity()-psiInventoryLocation.getOldQuantity());
			sourceLocation.setOfflineQuantity(sourceLocation.getOfflineQuantity()-psiInventoryLocation.getOfflineQuantity());
			sourceLocation.setBrokenQuantity(sourceLocation.getBrokenQuantity()-psiInventoryLocation.getBrokenQuantity());
			sourceLocation.setRenewQuantity(sourceLocation.getRenewQuantity()-psiInventoryLocation.getRenewQuantity());
			sourceLocation.setSparesQuantity(sourceLocation.getSparesQuantity()-psiInventoryLocation.getSparesQuantity());
			//目标
			PsiInventoryLocation targetLocation = psiInventoryLocationService.getByUnique(sourceLocation.getSku(), psiInventoryLocation.getStockLocation().getId(), sourceLocation.getSnCode());
			if(targetLocation == null){
				//目标库位没有改产品新建一条记录
				psiInventoryLocation.setSku(sourceLocation.getSku());
				psiInventoryLocation.setProductName(sourceLocation.getProductName());
				psiInventoryLocation.setColorCode(sourceLocation.getColorCode());
				psiInventoryLocation.setProductId(sourceLocation.getProductId());
				psiInventoryLocation.setCountryCode(sourceLocation.getCountryCode());
				psiInventoryLocation.setSnCode(sourceLocation.getSnCode());
				psiInventoryLocation.setCreateDate(new Date());
				psiInventoryLocation.setUpdateDate(new Date());
				psiInventoryLocation.setRemark(sourceLocation.getRemark());
				list.add(psiInventoryLocation);
			} else {
				//目标库位有相同批次的同sku，直接合并数量
				targetLocation.setNewQuantity(targetLocation.getNewQuantity()+psiInventoryLocation.getNewQuantity());
				targetLocation.setOldQuantity(targetLocation.getOldQuantity()+psiInventoryLocation.getOldQuantity());
				targetLocation.setOfflineQuantity(targetLocation.getOfflineQuantity()+psiInventoryLocation.getOfflineQuantity());
				targetLocation.setBrokenQuantity(targetLocation.getBrokenQuantity()+psiInventoryLocation.getBrokenQuantity());
				targetLocation.setRenewQuantity(targetLocation.getRenewQuantity()+psiInventoryLocation.getRenewQuantity());
				targetLocation.setSparesQuantity(targetLocation.getSparesQuantity()+psiInventoryLocation.getSparesQuantity());
				list.add(targetLocation);
			}
			psiInventoryLocationService.save(list);
		}
		addMessage(redirectAttributes, "操作成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiInventoryLocation/?stockId="+stockId;
	}
	
	@RequestMapping(value = {"getInventoryLocation"})
	@ResponseBody
	public PsiInventoryLocation getInventoryLocation(Integer id) {
		return psiInventoryLocationService.get(id);
	}
	
}
