/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web.lc;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.ProductParts;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsInventory;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsOrderBasis;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsOrderBasisTotal;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrderItem;
import com.springrain.erp.modules.psi.entity.parts.PsiParts;
import com.springrain.erp.modules.psi.service.PsiProductPartsService;
import com.springrain.erp.modules.psi.service.lc.LcPsiPartsInventoryService;
import com.springrain.erp.modules.psi.service.lc.LcPsiPartsOrderBasisService;
import com.springrain.erp.modules.psi.service.lc.LcPurchaseOrderService;
import com.springrain.erp.modules.psi.service.parts.PsiPartsService;

/**
 * 配件订单付款详情Controller
 * @author Michael
 * @version 2015-06-29
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/lcPsiPartsOrderBasis")
public class LcPsiPartsOrderBasisController extends BaseController {

	@Autowired
	private LcPsiPartsOrderBasisService	 psiPartsOrderBasisService;
	@Autowired
	private LcPsiPartsInventoryService     psiPartsInventoryService;
	@Autowired
	private PsiPartsService              partsService;
	@Autowired
	private LcPurchaseOrderService       purchaseOrderService;
	@Autowired
	private PsiProductPartsService       productPartsService;
	
	
	@RequestMapping(value = {"list", ""})
	public String list(LcPsiPartsOrderBasis psiPartsOrderBasis, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<LcPsiPartsOrderBasis> page = psiPartsOrderBasisService.find(new Page<LcPsiPartsOrderBasis>(request, response), psiPartsOrderBasis); 
        model.addAttribute("page", page);
		return "modules/psi/lc/parts/lcPsiPartsOrderBasisList";
	}
	
	@RequestMapping(value = "save")
	public String save(LcPsiPartsOrderBasisTotal psiPartsOrderBasisTotal, Model model,RedirectAttributes redirectAttributes) throws Exception {
		List<LcPsiPartsOrderBasis> list= psiPartsOrderBasisTotal.getItems();
		if(list==null){
			return null;
		}
		String res=this.psiPartsOrderBasisService.save(psiPartsOrderBasisTotal);
		if("1".equals(res)){
			addMessage(redirectAttributes, "配件库存分配成功");
			return "redirect:"+Global.getAdminPath()+"/psi/lcPurchaseOrder/?repage";	
		}else{  
			addMessage(redirectAttributes, "生成如下配件订单：'" + res + "'成功");
			return "redirect:"+Global.getAdminPath()+"/psi/lcPsiPartsOrder/?repage";	
		}
	}

	@RequestMapping(value = "form")
	public String form(LcPsiPartsOrderBasis psiPartsOrderBasis, Model model,RedirectAttributes redirectAttributes) {
		LcPsiPartsOrderBasisTotal basisTotal = new LcPsiPartsOrderBasisTotal();
		if(psiPartsOrderBasis.getId()==null){
			return null;
		}
		
		Integer orderId=psiPartsOrderBasis.getId();
		LcPurchaseOrder purchaseOrder=purchaseOrderService.get(orderId);
		//查询该订单包含的需要生成配件信息
		Map<Integer,String> productMap  = Maps.newHashMap();
		Map<String,Integer> productColorMap = Maps.newHashMap();
		Map<String,Integer> productNameColorMap = Maps.newHashMap();   //key：产品名+颜色        value： 数量
		Map<String,Integer> proNameColorPartsMap = Maps.newHashMap();     //key：产品名+颜色        value： 配件比例
		for(LcPurchaseOrderItem orderItem:purchaseOrder.getItems()){
			Integer productId=orderItem.getProduct().getId();
			Integer quantity =orderItem.getQuantityOrdered();
			String  productName=orderItem.getProductName();    
			String key = productId+","+orderItem.getColorCode();
			if(productColorMap.get(key)!=null){   
				quantity+=productColorMap.get(key);
			}
			productColorMap.put(key, quantity);
			productMap.put(productId, productName);
		}
		
		Map<Integer,Map<String,Integer>>  partsProMap = Maps.newHashMap();              //配件id  产品id 产品名_color
		Map<Integer,Integer> partsIdMap=Maps.newHashMap();
		Map<Integer,PsiParts> partsMap = Maps.newHashMap();
		
		for(Map.Entry<String, Integer>  entry:productColorMap.entrySet()){
			String productColor = entry.getKey();
			String arr[] = productColor.split(",");
			Integer productId=Integer.parseInt(arr[0]);
			String productName=productMap.get(productId);
			String color ="";
			if(arr!=null&&arr.length>1){
				color=arr[1];
				productName=productName+"_"+color;
			}
			
			productNameColorMap.put(productName, entry.getValue());
			List<ProductParts> proPartsList=this.productPartsService.getProductParts(productId, color);
				for(ProductParts proParts:proPartsList){
					Integer partsId = proParts.getParts().getId();
					Map<String,Integer> innerMap = partsProMap.get(partsId); 
					if(innerMap!=null){
						innerMap=partsProMap.get(partsId);
					}else{
						innerMap=Maps.newHashMap();
					}
					
					innerMap.put(productName,productId);
					partsProMap.put(partsId, innerMap);
					if(proParts.getMixtureRatio()==null){
						 addMessage(redirectAttributes,"配件比例为空,请检查！");
						 return "redirect:"+Global.getAdminPath()+"/psi/lcPurchaseOrder/?repage";
					}
					Integer quantity = entry.getValue()*proParts.getMixtureRatio();
					if(partsIdMap.get(partsId)!=null){
						quantity +=partsIdMap.get(partsId);
					}else{
						partsMap.put(partsId, proParts.getParts());
					}
					partsIdMap.put(partsId, quantity);
					String proNameParts = productName+","+partsId;
					
					proNameColorPartsMap.put(proNameParts, proParts.getMixtureRatio());
				}
		}
		
		
		if(partsIdMap.size()==0){
			 addMessage(redirectAttributes,"该订单没有产品关联配件！");
			 return "redirect:"+Global.getAdminPath()+"/psi/lcPurchaseOrder/?repage";
		}
		
		
		
		//查询该配件的库存信息
		Map<Integer,LcPsiPartsInventory> partInventoryMap = this.psiPartsInventoryService.getPsiPartsInventorys(partsIdMap.keySet());
		List<LcPsiPartsOrderBasis>  orderBasises =Lists.newArrayList();
		for(Map.Entry<Integer, PsiParts> entry:partsMap.entrySet()){
			Integer partsId = entry.getKey();
			LcPsiPartsOrderBasis orderBasis =new LcPsiPartsOrderBasis();
			PsiParts parts =entry.getValue();
			orderBasis.setPartsId(partsId);
			orderBasis.setPartsName(parts.getPartsName());
			orderBasis.setPurchaseOrderId(purchaseOrder.getId());
			orderBasis.setPurchaseOrderNo(purchaseOrder.getOrderNo());
			orderBasis.setNeedQuantity(partsIdMap.get(partsId));
			orderBasis.setSupplierId(parts.getSupplier().getId());
			orderBasis.setMoq(parts.getMoq());
			//算出收货日期
			orderBasis.setDeliveryDate(DateUtils.addDays(new Date(), parts.getProducePeriod()));
			if(partInventoryMap.get(partsId)!=null){
				orderBasis.setPoNotFrozen(partInventoryMap.get(partsId).getPoNotFrozen());
				orderBasis.setStockNotFrozen(partInventoryMap.get(partsId).getStockNotFrozen());
			}else{
				orderBasis.setPoNotFrozen(0);
				orderBasis.setStockNotFrozen(0);
				orderBasis.setPoFrozen(0);
				orderBasis.setStockFrozen(0);
			}
			orderBasises.add(orderBasis);
		}
		basisTotal.setItems(orderBasises);
		basisTotal.setPurchaseDate(new Date());
		basisTotal.setPurchaseOrderNo(purchaseOrder.getOrderNo());
		
		Map<Integer,String> receivedMap = this.partsService.getAllReceivedDate();
		model.addAttribute("receivedMap", JSON.toJSON(receivedMap));
		model.addAttribute("partsProMap", partsProMap);
		model.addAttribute("basisTotal", basisTotal);
		model.addAttribute("proNameColorPartsMap", proNameColorPartsMap);
		model.addAttribute("productNameColorMap", productNameColorMap);
		return "modules/psi/lc/parts/lcPsiPartsOrderBasisForm";
	}


}
