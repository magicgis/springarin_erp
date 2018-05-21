/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web.parts;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.ProductParts;
import com.springrain.erp.modules.psi.entity.PurchaseOrder;
import com.springrain.erp.modules.psi.entity.PurchaseOrderItem;
import com.springrain.erp.modules.psi.entity.parts.PsiParts;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsInventoryOut;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsInventoryOutItem;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsInventoryOutOrder;
import com.springrain.erp.modules.psi.service.PsiProductPartsService;
import com.springrain.erp.modules.psi.service.PurchaseOrderService;
import com.springrain.erp.modules.psi.service.parts.PsiPartsInventoryOutService;
import com.springrain.erp.modules.psi.service.parts.PsiPartsInventoryService;

/**
 * 配件出库Controller
 * @author Michael
 * @version 2015-07-16
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiPartsInventoryOut")
public class PsiPartsInventoryOutController extends BaseController {

	@Autowired
	private PsiPartsInventoryOutService  psiPartsInventoryOutService;
	@Autowired
	private PurchaseOrderService 		 purchaseOrderService;
	@Autowired
	private PsiPartsInventoryService     psiPartsInventoryService;
	@Autowired
	private PsiProductPartsService       productPartsService;
	
	@RequestMapping(value = {"list", ""})
	public String list(PsiPartsInventoryOut psiPartsInventoryOut, HttpServletRequest request, HttpServletResponse response, Model model) {
        Page<PsiPartsInventoryOut> page = psiPartsInventoryOutService.find(new Page<PsiPartsInventoryOut>(request, response), psiPartsInventoryOut); 
        model.addAttribute("page", page);
		return "modules/psi/parts/psiPartsInventoryOutList";
	}

	@RequestMapping(value = "form")
	public String form(PsiPartsInventoryOut psiPartsInventoryOut, Model model) {
		//注意：排除不是配件的产品，已配送完成的产品
		List<PurchaseOrder> lists= purchaseOrderService.findHasPartsOfPurchaseOrder(new String []{"2","3"}, true);
		Map<String,List<ProductParts>> partsMap = Maps.newHashMap();//key:product,color  value:list
		Map<String,List<PsiPartsInventoryOutItem>> partsOutMap = Maps.newHashMap();
		Map<String,String>   productMap = Maps.newHashMap(); //key:productId,color   value:product_color
		Map<String,List<String[]>> productOrderMap =Maps.newHashMap();
		Map<String,Integer> productQuantityMap = Maps.newHashMap();
		Map<String,Integer> proColorQuantityMap = Maps.newHashMap();//key:productId,color value:quantity
		//查询所有有配件的productId,color
		Set<String> hasPartsSet= this.productPartsService.getPartsColors(null);
		Set<Integer> orderIds = Sets.newHashSet();
		for(PurchaseOrder purchaseOrder:lists){
			orderIds.add(purchaseOrder.getId());
		}
		
		Map<Integer,Map<String,Integer>> canPartsMap = psiPartsInventoryOutService.getCanLadingQuantitys(orderIds);
		for(PurchaseOrder purchaseOrder:lists){
			Integer purchaseOrderId = purchaseOrder.getId();
			String [] orderInfo = new String []{purchaseOrderId+"",purchaseOrder.getOrderNo()};
			Map<String,Integer> unReceiveMap = purchaseOrder.getProColorUnReceivedQuantity();
			for(PurchaseOrderItem item : purchaseOrder.getItems()){
				Integer productId=item.getProduct().getId();
				String  color    =item.getColorCode();
				String proColor  = productId+","+color;
				//如果该产品颜色没有配件，或者可配送数==未收货数 也排除
				if(!hasPartsSet.contains(proColor)){
					continue;
				}
				Integer unReceivedQuantity = unReceiveMap.get(proColor);
				Integer canPartsSetQuantity=0;
				if(canPartsMap!=null&&canPartsMap.get(purchaseOrderId)!=null&&canPartsMap.get(purchaseOrderId).get(proColor)!=null){
					canPartsSetQuantity=canPartsMap.get(purchaseOrderId).get(proColor) ;
				}
				
				if(unReceivedQuantity.equals(canPartsSetQuantity)){
					continue;
				}
				String  proColorKey  = productId+"_"+color;
				String productName =item.getProductName();
				if(StringUtils.isNotEmpty(color)){
					productName=productName+"_"+color;
				}
				String key=purchaseOrderId+"_"+item.getProduct().getId()+"_"+color;
				Integer canQuantity =item.getQuantityOrdered()-item.getQuantityReceived();
				if(canQuantity>0){
					if(partsMap.get(proColorKey)==null){
						partsMap.put(proColorKey, productPartsService.getProductParts(productId, color));
					}
					Integer totalCanQuantity=canQuantity;
					if(productQuantityMap.get(key)!=null){
						canQuantity+=productQuantityMap.get(key);
					}else{
						List<String[]> orderList = productOrderMap.get(proColorKey);
						if(orderList==null){
							orderList=Lists.newArrayList();
						}   
						orderList.add(orderInfo);
						productOrderMap.put(proColorKey, orderList);
					}
					
					if(proColorQuantityMap.get(proColorKey)!=null){
						totalCanQuantity+=proColorQuantityMap.get(proColorKey);
					}
					proColorQuantityMap.put(proColorKey,totalCanQuantity);
					productQuantityMap.put(key, canQuantity);
					if(productMap.get(proColorKey)==null){
						productMap.put(proColorKey, productName);
					}
				}
			}
		}
		
		
		
		//减去包装送去的数量
		for(Map.Entry<String, Integer> entry :productQuantityMap.entrySet()){
			String orderProColor = entry.getKey();
			String color="";
			String arr[] =orderProColor.split("_");
			if(arr.length>2){
				color=arr[2];
			}
			Integer minusQuantity=this.psiPartsInventoryOutService.getCanDeliveryQuantity(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]),color);
		    if(minusQuantity!=null){
		    	productQuantityMap.put(orderProColor, entry.getValue()-minusQuantity);
		    }
		}
		
		//查询所有配件的库存
		Map<Integer,Integer> stockMap =psiPartsInventoryService.getPartsInventoryMap(null);
		for(Map.Entry<String, List<ProductParts>> entry:partsMap.entrySet()){
			String proColorKey = entry.getKey();
			List<PsiPartsInventoryOutItem> outItems = Lists.newArrayList();
			List<Integer> canList = Lists.newArrayList();
			for(ProductParts proParts:entry.getValue()){
				PsiParts parts=proParts.getParts();
				Integer partsId = proParts.getParts().getId();
				Integer inventoryQuantity=stockMap.get(partsId)==null?0: stockMap.get(partsId);
				Integer canQuantity=0;
				if(!inventoryQuantity.equals(0)){
					if(proParts.getMixtureRatio()==null){
						canQuantity=0;//如果有配比为空的，可出货数置零
					}else{
						canQuantity	=inventoryQuantity/proParts.getMixtureRatio();
					}
					
				}
				outItems.add(new PsiPartsInventoryOutItem(partsId, parts.getPartsName(),proParts.getMixtureRatio(),inventoryQuantity,canQuantity));
				canList.add(canQuantity);
			}
			Integer canMinQuantity=0;
			if(canList!=null&&canList.size()>0){
				canMinQuantity=Collections.min(canList);
			}
			
			if(canMinQuantity<proColorQuantityMap.get(proColorKey)){
				proColorQuantityMap.put(proColorKey, canMinQuantity);
			}
			
			partsOutMap.put(proColorKey, outItems);
		}
		
		model.addAttribute("productMap", productMap);
		model.addAttribute("productOrderMap", JSON.toJSON(productOrderMap));
		model.addAttribute("productQuantityMap", JSON.toJSON(productQuantityMap));
		model.addAttribute("proColorQuantityMap", JSON.toJSON(proColorQuantityMap));   
		model.addAttribute("partsOutMap", JSON.toJSON(partsOutMap));
		model.addAttribute("psiPartsInventoryOut", psiPartsInventoryOut);
		return "modules/psi/parts/psiPartsInventoryOutForm";
	}

	@RequestMapping(value = "save")
	public String save(PsiPartsInventoryOut psiPartsInventoryOut, Model model, RedirectAttributes redirectAttributes) {
		//获取产品名产品id
		String productIdColor = psiPartsInventoryOut.getProductIdColor();
		String arr[]= productIdColor.split("_");
		Integer productId = Integer.parseInt(arr[0]);
		String  color  = "";
		if(arr.length==2){
			color=arr[1];
		}
		psiPartsInventoryOut.setProductId(productId);
		psiPartsInventoryOut.setColor(color);
		psiPartsInventoryOut.setProductName(psiPartsInventoryOut.getProductName().split("_")[0]);
		for (Iterator<PsiPartsInventoryOutOrder> iterator = psiPartsInventoryOut.getOrders().iterator(); iterator.hasNext();) {
			PsiPartsInventoryOutOrder outOrder = (PsiPartsInventoryOutOrder) iterator.next();
			if(outOrder.getQuantity()==null||outOrder.getQuantity().equals(0)){
				iterator.remove();
			}
			
		}
		psiPartsInventoryOutService.addSave(psiPartsInventoryOut);
		addMessage(redirectAttributes, "保存配件出库序号：'" +psiPartsInventoryOut.getId()+ "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiPartsInventoryOut/?repage";
	}
	

}
