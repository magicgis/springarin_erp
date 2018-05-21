/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.entity.PsiSkuChangeBill;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiSkuChangeBillService;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * sku调换清单Controller
 * @author Michael
 * @version 2015-05-25
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiSkuChangeBill")
public class PsiSkuChangeBillController extends BaseController {

	@Autowired
	private PsiSkuChangeBillService  psiSkuChangeBillService;
	@Autowired
	private SystemService   		 userService;  
	@Autowired
	private MailManager				 mailManager;
	@Autowired
	private AmazonProduct2Service	 amazonService;
	@Autowired
	private PsiProductService psiProductService;
	@Autowired
	private PsiInventoryService psiInventoryService;
	
	@RequiresPermissions("psi:skuChangeBill:view")
	@RequestMapping(value = {"list", ""})
	public String list(PsiSkuChangeBill psiSkuChangeBill, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(psiSkuChangeBill.getApplyDate()==null){
			Date date =new Date();
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			psiSkuChangeBill.setApplyDate(DateUtils.addMonths(date, -1));
			psiSkuChangeBill.setSureDate(date);
		}
		
		if(psiSkuChangeBill.getWarehouseId()==null){
			//首次进来
			Set<String> permissionsSet = Sets.newHashSet();
			//查询权限
			User user = UserUtils.getUser();
			if(!user.isAdmin()){
				for(Role role:UserUtils.getUser().getRoleList()){
					permissionsSet.addAll(role.getPermissions());
				}
				if(permissionsSet.contains("psi:inventory:edit:CN")||permissionsSet.contains("psi:inventory:revise:CN")){
					psiSkuChangeBill.setWarehouseId(130);
				}else if(permissionsSet.contains("psi:inventory:edit:US")||permissionsSet.contains("psi:inventory:revise:US")){
					psiSkuChangeBill.setWarehouseId(120);
				}
			}
			if(psiSkuChangeBill.getWarehouseId()==null){
				psiSkuChangeBill.setWarehouseId(19);
			}
		}
		
		
		List<User> allUser = userService.findAllUsers();
        Page<PsiSkuChangeBill> page = psiSkuChangeBillService.find(new Page<PsiSkuChangeBill>(request, response), psiSkuChangeBill); 
        
        Map<String,String> skuMap = this.psiSkuChangeBillService.getShipmentIds();
        for(PsiSkuChangeBill changeBill:page.getList()){
        	if("0".equals(changeBill.getChangeSta())&&changeBill.getWarehouseId().equals(19)){
        		String shipmentId="";
    			if(skuMap.size()>0&&skuMap.get(changeBill.getToSku())!=null){
    				shipmentId=skuMap.get(changeBill.getToSku());
    			}
            	changeBill.setShippmentId(shipmentId);
        	}
        }
        model.addAttribute("allUser", allUser);
        model.addAttribute("page", page);
		return "modules/psi/psiSkuChangeBillList";
	}

	@RequiresPermissions("psi:skuChangeBill:view")
	@RequestMapping(value = {"byMonth"})
	public String byMonth(PsiSkuChangeBill psiSkuChangeBill, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(psiSkuChangeBill.getApplyDate()==null){
			Date date =new Date();
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			psiSkuChangeBill.setApplyDate(DateUtils.addMonths(date, -1));
			psiSkuChangeBill.setSureDate(date);
		}
		
		Map<String,Map<String,Integer>> monthMap =this.psiSkuChangeBillService.find(psiSkuChangeBill) ;
		
		model.addAttribute("psiSkuChangeBill", psiSkuChangeBill);
		model.addAttribute("monthMap", monthMap);
		return "modules/psi/psiSkuChangeBillByMonth";
	}
	
	
	@RequiresPermissions("psi:skuChangeBill:view")
	@RequestMapping(value = "sendNoticeEmail")
	@ResponseBody
	public String sendNoticeEmail(Integer warehouseId, Model model, RedirectAttributes redirectAttributes) {
		if(warehouseId==null){
			return "false";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String,String> fnskuMap =this.amazonService.getSkuAndFnskuMap();
		List<PsiSkuChangeBill> skuChanges= this.psiSkuChangeBillService.findSkuChangeNoSure(warehouseId);
		StringBuffer contents= new StringBuffer("");
		if(skuChanges.size()>0){
			contents.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '><tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '><th>ProductName</th><th>原sku[barcode]</th><th>新sku[barcode]</th><th>Quantity</th><th>Creater</th><th>CrateDate</th></tr>");
			for(PsiSkuChangeBill skuChange:skuChanges){
				contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '><td>"+skuChange.getProductName()+"</td><td>"+skuChange.getFromSku()+"["+fnskuMap.get(skuChange.getFromSku())+"]</td><td>"+skuChange.getToSku()+"["+fnskuMap.get(skuChange.getToSku())+"]</td><td>"+skuChange.getQuantity()+"</td><td>"+skuChange.getApplyUser().getName()+"</td><td>"+sdf.format(skuChange.getApplyDate())+"</td></tr>");
			}   
			contents.append("</table>");
		}
		
        String bank="";
        if(warehouseId.intValue()==130){
        	bank="理诚";
        }else{
        	bank="春雨";
        }
        
		if(StringUtils.isNotEmpty(contents)){
			Date date = new Date();
			//发信给仓库人员：
			String toAddress="penny@inateck.com";
			final MailInfo mailInfo = new MailInfo(toAddress,bank+"中国仓,未确认转码明细："+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(contents.toString());
			mailInfo.setCcToAddress("supply-chain@inateck.com");
			new Thread(){
				public void run(){
					 mailManager.send(mailInfo);
				}
			}.start();
		}
		return "true";
	}
	
	
	@RequiresPermissions("psi:skuChangeBill:view")
	@RequestMapping(value = "sureSave")
	public String sureSave(PsiSkuChangeBill psiSkuChangeBill, Model model, RedirectAttributes redirectAttributes) {
		if(psiSkuChangeBill.getId()!=null){
			if(this.psiSkuChangeBillService.sureSave(psiSkuChangeBill)){
				addMessage(redirectAttributes, "sku adjust confirm '" + psiSkuChangeBill.getId()+ "'success");
			}else{
				addMessage(redirectAttributes, "sku adjust confirm '" + psiSkuChangeBill.getId()+ "'fail");
			}
		}
		
		return "redirect:"+Global.getAdminPath()+"/psi/psiSkuChangeBill?warehouseId="+psiSkuChangeBill.getWarehouseId();
	}
	

	@RequiresPermissions("psi:skuChangeBill:view")
	@RequestMapping(value = "cancel")
	public String cancel(Integer id, Model model, RedirectAttributes redirectAttributes) {
		if(id!=null){
			PsiSkuChangeBill psiSkuChangeBill=this.psiSkuChangeBillService.get(id);
			if("0".equals(psiSkuChangeBill.getChangeSta())){
				String res=this.psiSkuChangeBillService.cancel(psiSkuChangeBill);
				addMessage(redirectAttributes, res);
				return "redirect:"+Global.getAdminPath()+"/psi/psiSkuChangeBill/?repage";
			}
		}
		return null;
	}

	
	@ResponseBody
	@RequestMapping(value = {"ajaxSkuData"})
	public String ajaxSkuData(Integer warehouseId,Integer productId,String color,String fromSku) {
		String data =this.psiSkuChangeBillService.getSkuData(warehouseId, productId, color,fromSku);
		String rs="{\"msg\":\"true\",\"items\":"+data+"}";
		return rs;
	}
	
	@ResponseBody
	@RequestMapping(value = {"ajaxQuantityValidate"})
	public String ajaxQuantityValidate(Integer warehouseId,String outInfos) {
		StringBuilder res=new StringBuilder();
		Map<String,Integer>  outSkuMap=Maps.newHashMap();
		String outInfo=outInfos.substring(0,(outInfos.length()-1));
		String[] skuArr=outInfo.split(";");
		for(String skuInfo:skuArr){
			String arr[]=skuInfo.split(",");
			if(arr.length>0){
				String sku =arr[0];
				Integer quantity =Integer.parseInt(arr[1]);
				outSkuMap.put(sku, quantity);
			}
		}
		
		Map<String,Integer>  skuMap =this.psiSkuChangeBillService.getSkuChangeNoSure(outSkuMap.keySet(),warehouseId);
		if(skuMap!=null){
			for(Map.Entry<String,Integer> entry:skuMap.entrySet()){
				String sku = entry.getKey();
				if(outSkuMap.containsKey(sku)&&outSkuMap.get(sku)<skuMap.get(sku)){
					res.append("sku:").append(sku).append(" has ").append(skuMap.get(sku)).append(" barcode change info no confirm,please go to confirm first! ");
				}
			}
		}
		
		
		String rs="{\"msg\":\""+res+"\"}";
		return rs;
	}   
	
	//新品贴码确认
	@RequestMapping(value = "skuSureForm")
	public String skuSureForm(PsiSkuChangeBill psiSkuChangeBill, Model model, RedirectAttributes redirectAttributes) {
		//未贴码新品
        List<String> newProducts = psiInventoryService.getNoSkus(null);
        if (StringUtils.isNotBlank(psiSkuChangeBill.getFromSku())) {
        	String fromSku = psiSkuChangeBill.getFromSku();
    		String nameWithColor = fromSku.substring(0, fromSku.lastIndexOf("_"));
    		String productName = nameWithColor;
    		String color = "";
    		if (nameWithColor.contains("_")) {	//带颜色
    			productName = nameWithColor.split("_")[0];
    			color = nameWithColor.split("_")[1];
    		}
    		List<String> skus = psiSkuChangeBillService.findSkuByProductAndCountry(productName, color, fromSku.substring(fromSku.lastIndexOf("_")+1, fromSku.length()));
    		if (skus != null && skus.size() > 0) {
    			model.addAttribute("skus", skus);
			}
        }
        model.addAttribute("newProducts", newProducts);
        model.addAttribute("psiSkuChangeBill", psiSkuChangeBill);
        return "modules/psi/psiSkuChangeSureForm";
	}   
	
	//新品贴码确认
	@RequestMapping(value = "skuSureSave")
	public String skuSureSave(PsiSkuChangeBill psiSkuChangeBill, Model model, RedirectAttributes redirectAttributes) {
		String fromSku = psiSkuChangeBill.getFromSku();
		String toSku = psiSkuChangeBill.getToSku();
		String nameWithColor = fromSku.substring(0, fromSku.lastIndexOf("_"));
		String productName = nameWithColor;
		String color = "";
		if (nameWithColor.contains("_")) {	//带颜色
			productName = nameWithColor.split("_")[0];
			color = nameWithColor.split("_")[1];
		}
		psiSkuChangeBill.setProductName(productName);
		psiSkuChangeBill.setProductColor(color);
		psiSkuChangeBill.setProductCountry(fromSku.substring(fromSku.lastIndexOf("_")+1, fromSku.length()));
		psiSkuChangeBill.setApplyDate(new Date());
		psiSkuChangeBill.setApplyUser(UserUtils.getUser());
		psiSkuChangeBill.setChangeSta("3");
		psiSkuChangeBill.setRemark("新品贴码确认");
		psiSkuChangeBill.setEvenName("From "+ fromSku + " To " + toSku);
		psiSkuChangeBill.setWarehouseId(19);
		psiSkuChangeBill.setWarehouseName("德国本地A");
		psiSkuChangeBill.setBatchNumber("");
		psiSkuChangeBillService.save(psiSkuChangeBill);
		psiProductService.updateAllSku(fromSku, toSku);
		addMessage(redirectAttributes, fromSku + " change to '" + psiSkuChangeBill.getToSku() + "'success");
		return "redirect:"+Global.getAdminPath()+"/psi/psiSkuChangeBill?warehouseId="+psiSkuChangeBill.getWarehouseId();
	}
}
