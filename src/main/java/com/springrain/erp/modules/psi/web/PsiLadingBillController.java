/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.custom.entity.SendEmail;
import com.springrain.erp.modules.custom.service.SendEmailService;
import com.springrain.erp.modules.psi.entity.PsiLadingBill;
import com.springrain.erp.modules.psi.entity.PsiLadingBillItem;
import com.springrain.erp.modules.psi.entity.PsiLadingBillItemDto;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductDto;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PurchaseOrder;
import com.springrain.erp.modules.psi.entity.PurchaseOrderItem;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiLadingBillService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.PurchaseOrderItemService;
import com.springrain.erp.modules.psi.service.PurchaseOrderService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 提单明细Controller
 * @author Michael
 * @version 2014-11-11
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiLadingBill")
public class PsiLadingBillController extends BaseController {
	
	
	@Autowired
	private PsiLadingBillService 		psiLadingBillService;
	@Autowired
	private PurchaseOrderItemService 	purchaseOrderItemService;
	@Autowired
	private PurchaseOrderService 		purchaseOrderService;
	@Autowired
	private PsiSupplierService 			psiSupplierService;
	@Autowired
	private PsiProductService 			psiProductService;
	@Autowired
	private MailManager 				mailManager;
	@Autowired
	private SendEmailService 			sendEmailService;
	@Autowired
	private PsiProductService 			productService;
	@Autowired
	private PsiInventoryService			inventoryService;
	@Autowired
	private AmazonProduct2Service		amazonService;
	private static String filePath;
	
	private  static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"list","listShow",""})
	public String list(PsiLadingBill psiLadingBill, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		Date today = sdf.parse(sdf.format(new Date()));
		if (psiLadingBill.getCreateDate() == null) {
			psiLadingBill.setCreateDate(DateUtils.addMonths(today, -3));
			psiLadingBill.setUpdateDate(today);
		}
		Map<String,String> fnskuMap =this.amazonService.getSkuAndFnskuMap();
        Page<PsiLadingBill> page = psiLadingBillService.find(new Page<PsiLadingBill>(request, response), psiLadingBill); 
        List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
        model.addAttribute("fnskuMap", fnskuMap);
		model.addAttribute("suppliers", suppliers);
        model.addAttribute("page", page);
		return "modules/psi/psiLadingBillList";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"genSeq"})
	public String genSeq(PsiLadingBill psiLadingBill,Integer ladingBillId,String nikeName, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		Date today = sdf.parse(sdf.format(new Date()));
		psiLadingBill.setCreateDate(DateUtils.addMonths(today, -1));
		psiLadingBill.setUpdateDate(today);
		//生成序列号1
		String billNo = this.psiLadingBillService.createSequenceNumber(nikeName+"_TDH");
		if(ladingBillId!=null){
			this.psiLadingBillService.updateLadingBillSeq(billNo, ladingBillId);
		}
		
		
        Page<PsiLadingBill> page = psiLadingBillService.find(new Page<PsiLadingBill>(request, response), psiLadingBill); 
        List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
		model.addAttribute("suppliers", suppliers);
        model.addAttribute("page", page);
        addMessage(model,"保存提单'" +billNo + "'成功");
		return "modules/psi/psiLadingBillList";
	}
	

		
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "add")
	public String add(PsiLadingBill psiLadingBill, Model model) {
		List<String>  products = new ArrayList<String>();
		List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
		List<PsiSupplier> tranSuppliers=this.psiSupplierService.findAllTransporter();
		List<Object[]> list = null;
		
		Map<String,List<Object[]>> productMap = new HashMap<String, List<Object[]>>();
		Map<String,Integer> totalMap = new HashMap<String, Integer>();
		//查出该供应商可提单产品  所在订单编号    数量
		if(psiLadingBill.getSupplier()==null){
			if(suppliers!=null&&suppliers.size()>0){
				list=this.psiLadingBillService.getProductLading(suppliers.get(0).getId(),psiLadingBill.getCurrencyType());
			}
		}else{
			list = this.psiLadingBillService.getProductLading(psiLadingBill.getSupplier().getId(),psiLadingBill.getCurrencyType());
		}
		
		if(list!=null&&list.size()>0){
			for(Object[] obs:list){
				List<Object[]> listObj = new ArrayList<Object[]>();
				Integer canLadingQuantity = 0;
				if(productMap.containsKey(obs[0].toString())){
					listObj=productMap.get(obs[0].toString());
					Integer  total = totalMap.get(obs[0].toString());
					canLadingQuantity=total+Integer.parseInt(obs[2].toString());
				}else{
					canLadingQuantity=Integer.parseInt(obs[2].toString());
				}
				totalMap.put(obs[0].toString(), canLadingQuantity);
				listObj.add(obs);
				productMap.put(obs[0].toString(), listObj);
				
			}
		}
		products.addAll(productMap.keySet());
		
		//页面放两个map
		model.addAttribute("productMap",JSON.toJSON(productMap));
		model.addAttribute("totalMap",JSON.toJSON(totalMap));
		model.addAttribute("products",products);
		model.addAttribute("tranSuppliers", tranSuppliers);
		model.addAttribute("suppliers", suppliers);
		model.addAttribute("psiLadingBill", psiLadingBill);
		return "modules/psi/psiLadingBillAdd";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "batchReceive")
	public String batchReceive(String orderIds, String orderItemIds,Integer supplierId,String currencyType,Model model) {
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyyMMdd");
		PsiLadingBill psiLadingBill = new PsiLadingBill();
		psiLadingBill.setCurrencyType(currencyType);
		List<PurchaseOrderItem> orderItems =purchaseOrderItemService.getOrderItems(orderItemIds);
		Map<String,List<PsiLadingBillItemDto>> ladingMap = Maps.newHashMap();
		//这里多生成一个map因为totalMap里面的数量和批量这里的可提单总数不同，这里只是选中的几个的总和
		Map<String,Integer> batchTotalMap = new HashMap<String, Integer>();
		for(PurchaseOrderItem orderItem:orderItems){
			//组合map
			String key = orderItem.getProductName()+"|"+this.psiLadingBillService.changeCountryToUs(orderItem.getCountryCode());
			if(StringUtils.isNotEmpty(orderItem.getColorCode())){
				key+="|"+orderItem.getColorCode();
			}
			
			List<PsiLadingBillItemDto> itemList = null;
			Integer ladingQuantity = orderItem.getQuantityOrdered()-orderItem.getQuantityReceived()-orderItem.getQuantityPreReceived();
			if(ladingQuantity>0){
				PsiLadingBillItemDto dto = new PsiLadingBillItemDto(orderItem.getItemPrice(), orderItem.getId(), orderItem.getPurchaseOrder().getId(), orderItem.getPurchaseOrder().getOrderNo(),orderItem.getProduct().getPackQuantity(),ladingQuantity
						,null,orderItem.getCanLadingOffQuantity(),dFormat.format(orderItem.getDeliveryDate()),null);
				if(ladingMap.containsKey(key)){    
					itemList=ladingMap.get(key);
				}else{
					itemList=Lists.newArrayList();    
				}
				itemList.add(dto);
				ladingMap.put(key, itemList);
				Integer canQuantity = 0;
				if(batchTotalMap.containsKey(key)){
					canQuantity=batchTotalMap.get(key)+ladingQuantity;
				}else{
					canQuantity=ladingQuantity;
				}
				batchTotalMap.put(key, canQuantity);
			}
		}
		
		
		List<String>  products = new ArrayList<String>();
		List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
		List<PsiSupplier> tranSuppliers=this.psiSupplierService.findAllTransporter();
		List<Object[]> list = null;
		
		Map<String,List<Object[]>> productMap = new HashMap<String, List<Object[]>>();
		Map<String,Integer> totalMap = new HashMap<String, Integer>();
		//查出该供应商可提单产品  所在订单编号    数量
		list = this.psiLadingBillService.getProductLading(supplierId,currencyType);
		Set<Integer>  productIdSet = Sets.newHashSet();
		if(list!=null&&list.size()>0){
			for(Object[] obs:list){
				List<Object[]> listObj =null;
				Integer canLadingQuantity = 0;
				if(productMap.containsKey(obs[0].toString())){
					listObj=productMap.get(obs[0].toString());
					Integer  total = totalMap.get(obs[0].toString());
					canLadingQuantity=total+Integer.parseInt(obs[2].toString());
				}else{
					listObj =Lists.newArrayList();
					canLadingQuantity=Integer.parseInt(obs[2].toString());
				}
				productIdSet.add(Integer.parseInt(obs[7].toString()));
				totalMap.put(obs[0].toString(), canLadingQuantity);
				listObj.add(obs);
				productMap.put(obs[0].toString(), listObj);
			}
		}
		products.addAll(productMap.keySet());
		
		PsiSupplier supplier = new PsiSupplier();
		supplier.setId(supplierId);
		psiLadingBill.setSupplier(supplier);
		
		//获取nikeName
//		String nikeName="";
//		for(PsiSupplier sup:suppliers){
//			if(sup.getId().equals(supplierId)){
//				nikeName=sup.getNikename();
//				break;
//			}
//		}
//		String billNo = this.psiLadingBillService.createSequenceNumber(nikeName+"_TDH");
//		psiLadingBill.setBillNo(billNo);
		Set<String>  skus = Sets.newHashSet();
		Map<String,Set<String>> skuMap = this.productService.getSkuMapByProduct(productIdSet,skus);
		Map<String,String> fnskuMap =this.productService.getSkuAndFnskuMap(skus);
		model.addAttribute("skuMap",JSON.toJSON(skuMap));
		model.addAttribute("fnskuMap",JSON.toJSON(fnskuMap));
		
		model.addAttribute("productMap",JSON.toJSON(productMap));
		model.addAttribute("totalMap",JSON.toJSON(totalMap));
		model.addAttribute("ladingMap",JSON.toJSON(ladingMap));
		model.addAttribute("batchTotalMap",JSON.toJSON(batchTotalMap));
		model.addAttribute("products",products);
		model.addAttribute("tranSuppliers", tranSuppliers);
		model.addAttribute("suppliers", suppliers);
		model.addAttribute("psiLadingBill", psiLadingBill);
		return "modules/psi/psiLadingBillBatchAdd";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "addSave")
	public String  addSave(String nikeName,PsiLadingBill psiLadingBill, Model model, RedirectAttributes redirectAttributes) throws Exception {
		if(psiLadingBill.getItems()!=null){
			PsiSupplier  supplier=this.psiSupplierService.get(psiLadingBill.getSupplier().getId());
			this.psiLadingBillService.addSave(psiLadingBill,supplier);
			addMessage(redirectAttributes,"保存提单'" +psiLadingBill.getBillNo() + "'成功");
			sendEmailToVendor(psiLadingBill,supplier);
		}else{
			addMessage(redirectAttributes, "保存提单失败,提单项为空");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiLadingBill";
	}
	
	@ResponseBody
	@RequestMapping(value = "canSave")
	public String canSave(String ids,String nums){
		String[]temp =  ids.split(",");
		String[]temp1 =  nums.split(",");
		for (int i = 0; i < temp1.length; i++) {
			String str = temp1[i];
			if(str.length()>0&&temp[i].length()>0){
				if(!this.psiLadingBillService.canSave(temp[i],str)){
					return "false";
				}
			}
		}
		return "true";
	}
	
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "edit")
	public String edit(PsiLadingBill psiLadingBill, Model model) {
		List<Object[]> orderProductlist ;
		List<String>  products = new ArrayList<String>();
		Map<String,List<PsiLadingBillItemDto>> ladingMap = Maps.newHashMap();
		Map<String,List<Object[]>> productMap = new HashMap<String, List<Object[]>>();
		Map<String,Integer> totalMap = new HashMap<String, Integer>();
		List<PsiSupplier> tranSuppliers=this.psiSupplierService.findAllTransporter();
		StringBuilder sb = new StringBuilder("");
		Map<Integer,String> orderItemInfoMap = Maps.newHashMap();
		Set<Integer>   productIdSet = Sets.newHashSet();
		psiLadingBill=this.psiLadingBillService.get(psiLadingBill.getId());
		
		for(PsiLadingBillItem item :psiLadingBill.getItems()){
			//组合map
			String key = item.getProductName()+"|"+("com".equals(item.getCountryCode())?"us":item.getCountryCode());
			if(StringUtils.isNotEmpty(item.getColorCode())){
				key+="|"+item.getColorCode();
			}
			List<PsiLadingBillItemDto> itemList = null;
			if(ladingMap.get(key)==null){
				itemList=Lists.newArrayList();
			}else{
				itemList=ladingMap.get(key);
			}
			itemList.add(new PsiLadingBillItemDto(item));
			ladingMap.put(key, itemList);
			sb.append(item.getId()+",");
			orderItemInfoMap.put(item.getPurchaseOrderItem().getId(), item.getQuantityLading()+","+item.getQuantityOffLading());
		}
		
		String itemIds="";
		if(StringUtils.isNotEmpty(sb.toString())){
			itemIds=sb.toString().substring(0,sb.toString().length()-1);
		}
		
		psiLadingBill.setOldItemIds(itemIds);
		
	
		//查出该供应商可提单产品  所在订单编号    数量                         可提单数量 = 订单数-已提单数-预提单数 >=0    对等于零的进行过滤，匹配本提货单的item，其他的不要         
		orderProductlist = this.psiLadingBillService.getProductLadingForEdit(psiLadingBill.getSupplier().getId(),psiLadingBill.getCurrencyType());
		if(orderProductlist!=null&&orderProductlist.size()>0){
			for(Object[] obs:orderProductlist){
				List<Object[]> listObj = new ArrayList<Object[]>();
				String productColorCountry=obs[0].toString();
				//如果相减为0   并且itemId不在本提单内，数据舍弃
				Integer orderItemId = Integer.parseInt(obs[3].toString());
				if(obs[2].toString().equals("0")&&!orderItemInfoMap.keySet().contains(orderItemId)){
					continue;
				}
				if(orderItemInfoMap.get(orderItemId)!=null){
					//本单的数量加上
					String arr[]=orderItemInfoMap.get(orderItemId).split(",");
					Integer ladingQ = Integer.parseInt(arr[0]);
					Integer ladingOfflineQ = Integer.parseInt(arr[1]);
					obs[2]=Integer.parseInt(obs[2].toString())+ladingQ;
					obs[8]=Integer.parseInt(obs[8].toString())+ladingOfflineQ;
				}
				Integer canQuantity = 0;
				if(productMap.containsKey(productColorCountry)){
					listObj=productMap.get(productColorCountry);
					canQuantity=Integer.parseInt(obs[2].toString())+totalMap.get(productColorCountry);
				}else{
					canQuantity=Integer.parseInt(obs[2].toString());
				}
				totalMap.put(productColorCountry, canQuantity);
				listObj.add(obs);
				productMap.put(productColorCountry, listObj);
				productIdSet.add(Integer.parseInt(obs[7].toString()));
			}
		}
		products.addAll(productMap.keySet());
		
		Set<String>  skus = Sets.newHashSet();
		Map<String,Set<String>> skuMap = this.productService.getSkuMapByProduct(productIdSet,skus);
		Map<String,String> fnskuMap =this.productService.getSkuAndFnskuMap(skus);
		model.addAttribute("skuMap",JSON.toJSON(skuMap));
		model.addAttribute("fnskuMap",JSON.toJSON(fnskuMap));
		model.addAttribute("ladingMap",JSON.toJSON(ladingMap));
		model.addAttribute("productMap",JSON.toJSON(productMap));
		model.addAttribute("totalMap",JSON.toJSON(totalMap));
		model.addAttribute("products",products);
		model.addAttribute("psiLadingBill", psiLadingBill);
		model.addAttribute("tranSuppliers",tranSuppliers);
		return "modules/psi/psiLadingBillEdit";
	}

	
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "editSave")
	public String editSave(PsiLadingBill psiLadingBill, Model model, RedirectAttributes redirectAttributes) {
		if(psiLadingBill.getItems()!=null){
			PsiSupplier  supplier=this.psiSupplierService.get(psiLadingBill.getSupplier().getId());    
			this.psiLadingBillService.editSave(psiLadingBill,supplier);
			addMessage(redirectAttributes, "编辑提单'" + psiLadingBill.getBillNo() + "'成功");
			return "redirect:"+Global.getAdminPath()+"/psi/psiLadingBill/?repage";
		}else{
			addMessage(redirectAttributes, "编辑提单失败,提单项为空");
			return "redirect:"+Global.getAdminPath()+"/psi/psiLadingBill/?repage";
		}
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "sure")
	public String sure(PsiLadingBill psiLadingBill, Model model) {
		List<String>  products = new ArrayList<String>();
		Map<String,List<PsiLadingBillItemDto>> ladingMap = Maps.newHashMap();
		psiLadingBill=this.psiLadingBillService.get(psiLadingBill.getId());
		Set<Integer>   productIdSet = Sets.newHashSet();
		for(PsiLadingBillItem item :psiLadingBill.getItems()){
			//组合map
			String key = item.getProductName()+"|"+this.psiLadingBillService.changeCountryToUs(item.getCountryCode());
			if(StringUtils.isNotEmpty(item.getColorCode())){
				key+="|"+item.getColorCode();
			}
			List<PsiLadingBillItemDto> itemList = null;
			if(ladingMap.get(key)==null){
				itemList=Lists.newArrayList();
			}else{
				itemList=ladingMap.get(key);
			}
			itemList.add(new PsiLadingBillItemDto(item));
			ladingMap.put(key, itemList);
			productIdSet.add(item.getPurchaseOrderItem().getProduct().getId());
		}
		Set<String>  skus = Sets.newHashSet();
		Map<String,Set<String>> skuMap = this.productService.getSkuMapByProduct(productIdSet,skus);
		Map<String,String> fnskuMap =this.productService.getSkuAndFnskuMap(skus);
		model.addAttribute("skuMap",JSON.toJSON(skuMap));
		model.addAttribute("fnskuMap",JSON.toJSON(fnskuMap));
		model.addAttribute("ladingMap",JSON.toJSON(ladingMap));
		model.addAttribute("products",products);
		model.addAttribute("psiLadingBill", psiLadingBill);
		return "modules/psi/psiLadingBillSure";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "sureSave")
	public String sureSave(@RequestParam("attchmentFile")MultipartFile[] attchmentFiles,PsiLadingBill psiLadingBill, Model model, RedirectAttributes redirectAttributes) {
		String res="";
		synchronized (this) {
			Map<Integer,String> skuMap = Maps.newHashMap();
			for(PsiLadingBillItem item:psiLadingBill.getItems()){
				skuMap.put(item.getId(), item.getSku()+",,,"+item.getQuantitySureTemp()+",,,"+item.getRemark()+" ,,,"+(item.getQuantitySpares()==null?"":item.getQuantitySpares()));
			}
			Date actualDeliveryDate = psiLadingBill.getActualDeliveryDate();
			psiLadingBill=this.psiLadingBillService.get(psiLadingBill.getId());
			psiLadingBill.setActualDeliveryDate(actualDeliveryDate);//保存实际入仓时间
			Map<String,Map<String,Object>> avgPriceMap=Maps.newHashMap(); 
			res=psiLadingBillService.sureSave(psiLadingBill, attchmentFiles,skuMap,avgPriceMap);
			this.psiLadingBillService.updateAvgPrice(avgPriceMap);
		}
		if(StringUtils.isNotEmpty(res)){
			addMessage(redirectAttributes, "error:确认提单"+psiLadingBill.getBillNo()+"失败，原因为："+ res);
		}else{
			addMessage(redirectAttributes, "确认提单'" + psiLadingBill.getBillNo() + "'成功");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiLadingBill/?repage";
	}
	
	
	
	@RequestMapping(value = "pass")
	public String pass(PsiLadingBill psiLadingBill, Model model) {
		List<String>  products = new ArrayList<String>();
		Map<String,List<PsiLadingBillItemDto>> ladingMap = Maps.newHashMap();
		psiLadingBill=this.psiLadingBillService.get(psiLadingBill.getId());
		Set<Integer>   productIdSet = Sets.newHashSet();
		for(PsiLadingBillItem item :psiLadingBill.getItems()){
			//组合map
			String key = item.getProductName()+"|"+this.psiLadingBillService.changeCountryToUs(item.getCountryCode());
			if(StringUtils.isNotEmpty(item.getColorCode())){
				key+="|"+item.getColorCode();
			}
			List<PsiLadingBillItemDto> itemList = null;
			if(ladingMap.get(key)==null){
				itemList=Lists.newArrayList();
			}else{
				itemList=ladingMap.get(key);
			}
			itemList.add(new PsiLadingBillItemDto(item));
			ladingMap.put(key, itemList);
			productIdSet.add(item.getPurchaseOrderItem().getProduct().getId());
		}
		Set<String>  skus = Sets.newHashSet();
		Map<String,Set<String>> skuMap = this.productService.getSkuMapByProduct(productIdSet,skus);
		Map<String,String> fnskuMap =this.productService.getSkuAndFnskuMap(skus);
		model.addAttribute("skuMap",JSON.toJSON(skuMap));
		model.addAttribute("fnskuMap",JSON.toJSON(fnskuMap));
		model.addAttribute("ladingMap",JSON.toJSON(ladingMap));
		model.addAttribute("products",products);
		model.addAttribute("psiLadingBill", psiLadingBill);
		model.addAttribute("now", new Date());
		return "modules/psi/psiLadingBillPass";
	}
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "passSave")
	public String passSave(PsiLadingBill psiLadingBill, Model model, RedirectAttributes redirectAttributes) {
		Map<Integer,String> passMap = Maps.newHashMap();
		Map<Integer,PsiLadingBillItem> itemMap = Maps.newHashMap();
		for(PsiLadingBillItem item:psiLadingBill.getItems()){
			passMap.put(item.getId(), item.getIsPass());
			if (item.getQuantityActual() > 0) {
				if (item.getQuantityActual() + item.getQuantityGoods() > item.getQuantityLading()) {
					logger.info("验货总数大于收货单总数量,id:" + item.getId()+",本次验货数量" + item.getQuantityActual());
					addMessage(redirectAttributes, "验货总数大于收货单总数量,产品信息：" + item.getProductConName()+",本次验货数量：" + item.getQuantityActual());
					return "redirect:"+Global.getAdminPath()+"/psi/psiLadingBill/?repage";
				} else {
					itemMap.put(item.getId(), item);
				}
			}
		}
		
		psiLadingBill = this.psiLadingBillService.get(psiLadingBill.getId());
		//找出为不合格的订单号，和产品名
		Set<String> orderNos =Sets.newHashSet();
		Set<String> productNames=Sets.newHashSet();
		String orderNoStr ="";
		String productNameStr="";
		String isPass = "1";
		for(PsiLadingBillItem item :psiLadingBill.getItems()){
			item.setIsPass(passMap.get(item.getId()));
			String result = "合格";
			if("0".equals(item.getIsPass())){
				isPass = "0";
				result = "不合格";
				productNames.add(item.getProductNameColor());
				orderNos.add(item.getPurchaseOrderItem().getPurchaseOrder().getOrderNo());
			}
			if (itemMap.get(item.getId()) != null) {
				
				PsiLadingBillItem itemTemp = itemMap.get(item.getId());
				if ("1".equals(item.getIsPass())) {//合格后统计验收总数
					int oldQuantityGoods = item.getQuantityGoods()==null?0:item.getQuantityGoods();
					item.setQuantityGoods(oldQuantityGoods + itemTemp.getQuantityActual());
				}
				if (StringUtils.isEmpty(item.getHisRecord())) {
					item.setHisRecord("验货数量:" + itemTemp.getQuantityActual() + ";时间:" + itemTemp.getQualityDate() + ";结果:" + result);
				} else {
					item.setHisRecord(item.getHisRecord() + "<br/>验货数量:" + itemTemp.getQuantityActual() + ";时间:" + itemTemp.getQualityDate() + ";结果:" + result);
				}
			}
		}
		if(productNames.size()>0){
			orderNoStr=orderNos.toString().substring(1, orderNos.toString().length()-1);
			productNameStr=productNames.toString().substring(1, productNames.toString().length()-1);
		}
		this.psiLadingBillService.save(psiLadingBill);
		return "redirect:"+Global.getAdminPath()+"/psi/psiQuestionSupplier/form?productName="+productNameStr+"&orderNo="+orderNoStr+"&supplier.id="+psiLadingBill.getSupplier().getId()+"&isPass="+isPass;
	}
	
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "uploadPi")
	public String uploadPi(PsiLadingBill psiLadingBill, Model model) {
		psiLadingBill=this.psiLadingBillService.get(psiLadingBill.getId());
		model.addAttribute("psiLadingBill", psiLadingBill);
		return "modules/psi/psiLadingBillUploadPi";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "uploadPiSave")
	public String uploadPiSave(@RequestParam("attchmentFile")MultipartFile[] attchmentFiles,PsiLadingBill psiLadingBill, Model model, RedirectAttributes redirectAttributes) {
		psiLadingBill=this.psiLadingBillService.get(psiLadingBill.getId());
		for (MultipartFile attchmentFile : attchmentFiles) {
			if(attchmentFile.getSize()!=0){
				String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/ladingBills";
				File baseDir = new File(baseDirStr+"/"+psiLadingBill.getBillNo()); 
				if(!baseDir.isDirectory())
					baseDir.mkdirs();
				String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
				String name=UUID.randomUUID().toString()+suffix;
				File dest = new File(baseDir,name);
				try {
					FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
					psiLadingBill.setAttchmentPathAppend("/psi/ladingBills/"+psiLadingBill.getBillNo()+"/"+name);
				} catch (IOException e) {
					logger.warn(name+"文件保存失败",e);
				}
			}
		}
		this.psiLadingBillService.save(psiLadingBill);
		addMessage(redirectAttributes, "提单'" + psiLadingBill.getBillNo() + "'上传凭证成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiLadingBill/?repage";
	}
	
	@RequiresPermissions("psi:all:view")
	@ResponseBody
	@RequestMapping(value = "print")
	public String print(PsiLadingBill ladingBill,HttpServletResponse response) throws Exception {
		ladingBill = this.psiLadingBillService.get(ladingBill.getId());
		if (filePath == null) {
			filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/ladingBills";
		}
		File file = new File(filePath, ladingBill.getBillNo());
		if (!file.exists()) {
			file.mkdirs();
		}
		File pdfFile = new File(file, ladingBill.getBillNo() + ".pdf");
//		Map<String,String> versionMap = productService.getFnskuAndSkuVersionMap();
		Set<String> skus = Sets.newHashSet();
		for(PsiLadingBillItem item:ladingBill.getItems()){
			if(StringUtils.isNotEmpty(item.getSku())){
				skus.add(item.getSku());
			}
		}
		Map<String,String> fnskuMap = productService.getSkuAndFnskuMap(skus);
		PdfUtil.genPsiLadingBillPdf(pdfFile,ladingBill,fnskuMap);   
		FileInputStream in = new FileInputStream(pdfFile);
		ServletOutputStream out = response.getOutputStream();
		response.setContentType("application/pdf");// pdf文件
		response.addHeader("Content-Disposition", "filename="+ ladingBill.getBillNo()+".pdf");
		byte data[] = new byte[1024];
		int len;
		while ((len = in.read(data)) != -1) {
			out.write(data, 0, len);
		}
		out.flush();
		in.close();
		out.close();
		return null;
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "view")
	public String view(PsiLadingBill psiLadingBill, Model model) {
		Map<String,List<PsiLadingBillItemDto>> ladingMap = new HashMap<String, List<PsiLadingBillItemDto>>();
		if(psiLadingBill.getId()!=null){
			psiLadingBill=this.psiLadingBillService.get(psiLadingBill.getId());
		}else if(psiLadingBill.getBillNo()!=null){
			psiLadingBill=this.psiLadingBillService.get(psiLadingBill.getBillNo());
		}else{
			return null;
		}
		
		StringBuilder sb = new StringBuilder("");
		for(PsiLadingBillItem item :psiLadingBill.getItems()){
			//组合map
			String key = item.getProductName()+"|"+this.psiLadingBillService.changeCountryToUs(item.getCountryCode());
			if(StringUtils.isNotEmpty(item.getColorCode())){
				key+="|"+item.getColorCode();
			}
			List<PsiLadingBillItemDto> itemList = null;
			if(ladingMap.get(key)==null){
				itemList=Lists.newArrayList();
			}else{
				itemList=ladingMap.get(key);
			}
			itemList.add(new PsiLadingBillItemDto(item));
			ladingMap.put(key, itemList);
			sb.append(item.getId()+",");
		}
		
		String itemIds="";
		if(StringUtils.isNotEmpty(sb.toString())){
			itemIds=sb.toString().substring(0,sb.toString().length()-1);
		}
		psiLadingBill.setOldItemIds(itemIds);
		model.addAttribute("ladingMap",JSON.toJSON(ladingMap));
		model.addAttribute("psiLadingBill", psiLadingBill);
		return "modules/psi/psiLadingBillView";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping("/download")   
    public ModelAndView download(String fileName, HttpServletRequest request, HttpServletResponse response)   
            throws Exception {   
		fileName = HtmlUtils.htmlUnescape(fileName);
        response.setContentType("text/html;charset=utf-8");   
        request.setCharacterEncoding("UTF-8");   
        java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null;   
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir();  
        String downLoadPath = ctxPath + fileName;
        fileName = fileName.substring(fileName.lastIndexOf("/")+1);
        try {   
            long fileLength = new File(downLoadPath).length();   
            response.setContentType("application/x-msdownload;");   
            response.setHeader("Content-disposition", "attachment; filename="  
                    + URLEncoder.encode(fileName,"utf-8"));   
            response.setHeader("Content-Length", String.valueOf(fileLength));   
            bis = new BufferedInputStream(new FileInputStream(downLoadPath));   
            bos = new BufferedOutputStream(response.getOutputStream());   
            byte[] buff = new byte[2048];   
            int bytesRead;   
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {   
                bos.write(buff, 0, bytesRead);   
            }   
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            if (bis != null)   
                bis.close();   
            if (bos != null)   
                bos.close();   
        }   
        return null;   
    }   

	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "cancel")
	public String cancel(Integer id, RedirectAttributes redirectAttributes) {
		//取消提单                                  把订单item预接收数量  - 该提单的数量
		PsiLadingBill bill=this.psiLadingBillService.get(id);
		if(this.psiLadingBillService.cancelBill(bill)){
			bill.setBillSta("2");//已取消
			bill.setCancelDate(new Date());
			bill.setCancelUser(UserUtils.getUser());
			this.psiLadingBillService.save(bill);
		};
		
		addMessage(redirectAttributes, "取消提单成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiLadingBill/?repage";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		psiLadingBillService.delete(id);
		addMessage(redirectAttributes, "删除提单成功");
		return "redirect:"+Global.getAdminPath()+"/modules/psi/psiLadingBill/?repage";
	}
	
	/**
	 *收货明细 
	 * @throws ParseException 
	 * 
	 */
	@RequestMapping(value ="receiptCargoList")
	public String receiptCargoList(PsiLadingBill ladingBill, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		
		if(ladingBill.getBillSta()==null){
			Date today = sdf.parse(sdf.format(new Date()));
			if (ladingBill.getCreateDate() == null) {
				ladingBill.setCreateDate(DateUtils.addMonths(today, -1));
				ladingBill.setSureDate(today);
			}
			ladingBill.setBillSta("");
		}
		
		List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
        Page<PsiLadingBill> page = this.psiLadingBillService.findReceiptCargoList(new Page<PsiLadingBill>(request, response), ladingBill); 
        Map<String,PsiLadingBill> productMap = Maps.newLinkedHashMap();
        if(page.getList().size()>0){
        	 List<PsiLadingBill>  bills = page.getList();
        	 for(PsiLadingBill bill:bills){
        		 for(PsiLadingBillItem item:bill.getItems()){
        			 PsiLadingBill temp= new PsiLadingBill();
	        			 if(productMap.get(item.getProductName())==null){
	    					 temp.setTempQuantity(item.getQuantityLading());
	    					 productMap.put(item.getProductName(),temp);
	        			 }else{
	        				 temp=productMap.get(item.getProductName());
	        				 temp.setTempQuantity(temp.getTempQuantity()+item.getQuantityLading());
	        			 }
	        			 temp.getItems().add(item);
        		 }
        	 }
             
        }
        
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("productMap", productMap);
        model.addAttribute("psiLadingBill", ladingBill);
        
        //model.addAttribute("page", page);
		return "modules/psi/purchaseReceiptCargoList";
	}
	
	/**
	 *收货/预收货明细 (运单试算)
	 * @throws ParseException 
	 * 
	 */
	@RequestMapping(value ="wayBillList")
	public String wayBillList(PsiLadingBill ladingBill, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		if(StringUtils.isEmpty(ladingBill.getBillNo())){
			ladingBill.setBillNo("de");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(ladingBill.getBillSta()==null){
			Date today = sdf.parse(sdf.format(new Date()));
			if (ladingBill.getCreateDate() == null) {
				ladingBill.setCreateDate(today);
				ladingBill.setSureDate(DateUtils.addMonths(today, 1));
			}
			ladingBill.setBillSta("");
		}
		
		List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
      //  List<PsiLadingBill> list = this.psiLadingBillService.findWayBillList(ladingBill); 
		List<PurchaseOrder> list = this.purchaseOrderService.findWayBillList(ladingBill);
        Map<String,String []> productMap = Maps.newHashMap();
        Map<String,List<PurchaseOrderItem>> orderMap = Maps.newHashMap();
        
        Integer supplierId =null;
        if(ladingBill.getSupplier()!=null&&ladingBill.getSupplier().getId()!=null){
        	supplierId=ladingBill.getSupplier().getId();
        }
   	 //查询中国仓的数据
     Map<String,Integer> productNameMap =this.inventoryService.findByStockAndCountry(21,ladingBill.getBillNo(),supplierId);
        
        if(list.size()>0){
        	 for(PurchaseOrder order:list){
        		 for(PurchaseOrderItem item:order.getItems()){
    				 if(item.getQuantityUnReceived()>0){
    					 Integer unReceivedQuantity = item.getQuantityUnReceived();
        				 if(item.getCountryCode().equals(ladingBill.getBillNo())||("eu".equals(ladingBill.getBillNo())&&"de,fr,it,es,uk,".contains(item.getCountryCode()+","))){
        					 if(productNameMap.get(item.getProductName())!=null){
        						 unReceivedQuantity += productNameMap.get(item.getProductName());
        					 }
        					 productNameMap.put(item.getProductName(), unReceivedQuantity);
        				 }
        				 
        				 List<PurchaseOrderItem> orderItems = null;
        				 if(orderMap.get(item.getProductName())==null){
	        				 if(item.getCountryCode().equals(ladingBill.getBillNo())||("eu".equals(ladingBill.getBillNo())&&"de,fr,it,es,uk,".contains(item.getCountryCode()+","))){
	        					 orderItems = Lists.newLinkedList();
	        					 orderMap.put(item.getProductName(),orderItems);
	        				 }
	        			 }else{
	        				
	        				 if(item.getCountryCode().equals(ladingBill.getBillNo())||("eu".equals(ladingBill.getBillNo())&&"de,fr,it,es,uk,".contains(item.getCountryCode()+","))){
	        					 orderItems = orderMap.get(item.getProductName());
	        				 }
	        			 }
        				 if(orderItems!=null){
        					 orderItems.add(item);
        					 orderMap.put(item.getProductName(), orderItems);
        				 }
        			 }
        		 }
        	 }
        }
       Map<String,PsiProduct> proMap = Maps.newHashMap();
       List<PsiProduct> pros= psiProductService.findAll();
       for(PsiProduct pro:pros){
    	   String proName = pro.getName();
    	   proMap.put(proName, pro);
       }
        //组成页面显示  数量   体积   重量
       
       for(Map.Entry<String,Integer> entry:productNameMap.entrySet()){
    	   String productName = entry.getKey();
    	   PsiProduct product = proMap.get(productName);
    	   String volume=product.getBoxVolume().floatValue()*(productNameMap.get(productName)/(double)product.getPackQuantity())+"";
    	   String weight=product.getGw().floatValue()*(productNameMap.get(productName)/(double)product.getPackQuantity())+"";
    	   productMap.put(productName, new String []{productNameMap.get(productName)+"",volume,weight});
       }
        model.addAttribute("suppliers", suppliers);
        model.addAttribute("orderMap", orderMap);
        model.addAttribute("productMap", productMap);
        model.addAttribute("psiLadingBill", ladingBill);
		return "modules/psi/purchaseWayCountList";
	}
	
	
	
	/**
	 *收货/预收货明细 (运单试算)
	 * 
	 */
	@RequestMapping(value ="trial")
	public String trial(HttpServletRequest request, HttpServletResponse response, Model model) {
		//查询所有产品
		Map<Integer,PsiProductDto>  proMap = Maps.newHashMap();
		List<PsiProduct>  products = productService.findAll();
		for(int i =0;i<products.size();i++){
			PsiProduct pro = products.get(i);
			proMap.put(pro.getId(), new PsiProductDto(pro.getId(),pro.getName(),pro.getPackQuantity(),pro.getBoxVolume(),pro.getGw()));
		}
		 model.addAttribute("proMap", JSON.toJSON(proMap));
		return "modules/psi/tranTrial";
	}
	

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "exp")
	public String exp(PsiLadingBill psiLadingBill, HttpServletRequest request, HttpServletResponse response, Model model) throws UnsupportedEncodingException {
		SimpleDateFormat   sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        int excelNo=1;
        Map<String,Integer> packMap= this.psiProductService.findPackQuantityMap();
        List<PsiLadingBill> list = psiLadingBillService.exp(psiLadingBill);
        
	    if(list.size()>65535){
	    	throw new RuntimeException("一次导出的最大数据不能超过65535条！请分两次导出！");
	    }
	    
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		
		
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		String[] title = { " SR. ", "收货日期", " 仓位 ", "供应商", "  品名  ","    型号     ","数量"," MPQ ","件数","PurchaseOrderNO","  DE  ","  UK  ","  FR  ","  IT  ","  ES  ","  JP  ","  US  ","  CA  "};
		
		  style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		  //设置Excel中的边框(表头的边框)
		  style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		  
		  style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		  style.setBottomBorderColor(HSSFColor.BLACK.index);
		  
		  style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		  style.setLeftBorderColor(HSSFColor.BLACK.index);
		  
		  style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		  style.setRightBorderColor(HSSFColor.BLACK.index);
		  
		  style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		  style.setTopBorderColor(HSSFColor.BLACK.index);
		  //设置字体
		  HSSFFont font = wb.createFont();
		  font.setFontHeightInPoints((short) 16); // 字体高度
		  font.setFontName(" 黑体 "); // 字体
		  font.setBoldweight((short) 16);
		  style.setFont(font);
		  row.setHeight((short) 500);
		  HSSFCell cell = null;		
		  for (int i = 0; i < title.length; i++) {
				cell = row.createCell(i);
				cell.setCellValue(title[i]);
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
			}
		  
		// 输出亚马逊excel订单
		 for(PsiLadingBill bilTrue: list){
		    	List<PsiLadingBill> tempBills=bilTrue.getTempLadingBills();
		    	for(int i =0;i<tempBills.size();i++){
		    		row = sheet.createRow(excelNo++);
		    		PsiLadingBill tempBill = tempBills.get(i);
		    		
		    		List<PsiLadingBillItem> billItems = tempBill.getItems();
    				String productName = "";
    				Map<String,Integer> itemQuantityMap  = Maps.newHashMap();
    				Set<Integer> orderItemSet = Sets.newHashSet();
                    for(int j =0;j<billItems.size();j++){
                    	PsiLadingBillItem item = billItems.get(j);
                    	if(j==0){
                    		productName=item.getProductName();
                    	}
                    	
                    	if(itemQuantityMap.get(item.getCountryCode())!=null){
                    		itemQuantityMap.put(item.getCountryCode(), item.getQuantityLading()+itemQuantityMap.get(item.getCountryCode()));
                    	}else{
                    		itemQuantityMap.put(item.getCountryCode(), item.getQuantityLading());
                    	}
                    	orderItemSet.add(item.getId());
                    }
                    
    				row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue(excelNo-1);
    				row.createCell(1,Cell.CELL_TYPE_STRING).setCellValue(sdf1.format(tempBill.getActualDeliveryDate()!=null?tempBill.getActualDeliveryDate():tempBill.getDeliveryDate()));  //创建日期
    				row.createCell(2,Cell.CELL_TYPE_STRING).setCellValue("深圳仓");
    				row.createCell(3,Cell.CELL_TYPE_STRING).setCellValue(tempBill.getSupplier().getNikename());
    				row.createCell(4,Cell.CELL_TYPE_STRING).setCellValue(productName.split(" ")[0]);
    				row.createCell(5,Cell.CELL_TYPE_STRING).setCellValue(productName.split(" ")[1]);
    				row.createCell(6,Cell.CELL_TYPE_NUMERIC).setCellValue(tempBill.getLadingTotal());
    				row.createCell(7,Cell.CELL_TYPE_STRING).setCellValue(packMap.get(productName));
    				row.createCell(8,Cell.CELL_TYPE_NUMERIC).setCellValue((int)(tempBill.getLadingTotal()/(float)packMap.get(productName)));
    				row.createCell(9,Cell.CELL_TYPE_STRING).setCellValue(tempBill.getBillNo());
    				
    				if(itemQuantityMap.get("de")!=null){
    					Integer quantity = itemQuantityMap.get("de");
    					row.createCell(10,Cell.CELL_TYPE_NUMERIC).setCellValue(quantity);
    				}else{
    					row.createCell(10,Cell.CELL_TYPE_NUMERIC).setCellValue("");
    				}
    				
    				if(itemQuantityMap.get("uk")!=null){
    					Integer quantity = itemQuantityMap.get("uk");
    					row.createCell(11,Cell.CELL_TYPE_NUMERIC).setCellValue(quantity);
    				}else{
    					row.createCell(11,Cell.CELL_TYPE_NUMERIC).setCellValue("");
    				}
    				
    				if(itemQuantityMap.get("fr")!=null){
    					Integer quantity = itemQuantityMap.get("fr");
    					row.createCell(12,Cell.CELL_TYPE_NUMERIC).setCellValue(quantity);
    				}else{
    					row.createCell(12,Cell.CELL_TYPE_NUMERIC).setCellValue("");
    				}
    				
    				if(itemQuantityMap.get("it")!=null){
    					Integer quantity = itemQuantityMap.get("it");
    					row.createCell(13,Cell.CELL_TYPE_NUMERIC).setCellValue(quantity);
    				}else{
    					row.createCell(13,Cell.CELL_TYPE_NUMERIC).setCellValue("");
    				}
    			
    				if(itemQuantityMap.get("es")!=null){
    					Integer quantity = itemQuantityMap.get("es");
    					row.createCell(14,Cell.CELL_TYPE_NUMERIC).setCellValue(quantity);
    				}else{
    					row.createCell(14,Cell.CELL_TYPE_NUMERIC).setCellValue("");
    				}
    				

    				if(itemQuantityMap.get("jp")!=null){
    					Integer quantity = itemQuantityMap.get("jp");
    					row.createCell(15,Cell.CELL_TYPE_NUMERIC).setCellValue(quantity);
    				}else{
    					row.createCell(15,Cell.CELL_TYPE_NUMERIC).setCellValue("");
    				}
    				
    				if(itemQuantityMap.get("com")!=null){
    					Integer quantity = itemQuantityMap.get("com");
    					row.createCell(16,Cell.CELL_TYPE_NUMERIC).setCellValue(quantity);
    				}else{
    					row.createCell(16,Cell.CELL_TYPE_NUMERIC).setCellValue("");
    				}
    				
    				if(itemQuantityMap.get("ca")!=null){
    					Integer quantity = itemQuantityMap.get("ca");
    					row.createCell(17,Cell.CELL_TYPE_NUMERIC).setCellValue(quantity);
    				}else{
    					row.createCell(17,Cell.CELL_TYPE_NUMERIC).setCellValue("");
    				}
		    	}
		 }
	    				 
					request.setCharacterEncoding("UTF-8");
	    			response.setCharacterEncoding("UTF-8");
	    			response.setContentType("application/x-download");

	    			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	    			String fileName = "LadingBillData" + sdf.format(new Date()) + ".xls";
					fileName = URLEncoder.encode(fileName, "UTF-8");
	    			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
	    			try {
	    				OutputStream out = response.getOutputStream();
	    				wb.write(out);
	    				out.close();
	    			} catch (Exception e) {
	    				e.printStackTrace();
	    			}
			return null;
		}
	
	
	private void sendEmailToVendor(PsiLadingBill psiLadingBill,PsiSupplier supplier) throws Exception{
		SendEmail  sendEmail = new SendEmail();
		String billNo =psiLadingBill.getBillNo();
		
		psiLadingBill.setTranSupplier(this.psiSupplierService.get(psiLadingBill.getTranSupplier().getId()));
		psiLadingBill.setSupplier(supplier);
		for(PsiLadingBillItem item:psiLadingBill.getItems()){
			item.setPurchaseOrderItem(this.purchaseOrderItemService.get(item.getPurchaseOrderItem().getId()));
		}
		//往采购供应商发信 获取供应商模板
		Map<String,Object> params = Maps.newHashMap();
		String template = "";
		try {
			params.put("Cname", UserUtils.getUser().getName());
			params.put("Cemail", UserUtils.getUser().getEmail());
			template = PdfUtil.getPsiTemplate("ladingEmail.ftl", params);
		} catch (Exception e) {}
		sendEmail.setSendContent(template);
		sendEmail.setSendEmail(psiLadingBill.getSupplier().getMail());    
		sendEmail.setSendSubject("新建提单LN:"+billNo+"("+DateUtils.getDate()+")");
		sendEmail.setCreateBy(UserUtils.getUser());
		//向供应商发送邮件 加入附件和抄送人  luofeng@de-gui.com,sandy@de-gui.com,long@de-gui.com
		String bccEmail =  UserUtils.getUser().getEmail();
		sendEmail.setBccToEmail(bccEmail);
		//sendEmail.setCcToEmail(UserUtils.getUser().getEmail());
		  
		//发送邮件
		final MailInfo mailInfo = sendEmail.getMailInfo();
		if (filePath == null) {
			filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/ladingBills";
		}
		File file = new File(filePath, psiLadingBill.getBillNo());
		if (!file.exists()) {
			file.mkdirs();
		}
		File pdfFile = new File(file, psiLadingBill.getBillNo() + ".pdf");
		Set<String> skus = Sets.newHashSet();
		for(PsiLadingBillItem item:psiLadingBill.getItems()){
			if(StringUtils.isNotEmpty(item.getSku())){
				skus.add(item.getSku());
			}
		}
		Map<String,String> fnskuMap = productService.getSkuAndFnskuMap(skus);
//		Map<String,String> versionMap = productService.getFnskuAndSkuVersionMap();
		PdfUtil.genPsiLadingBillPdf(pdfFile,psiLadingBill,fnskuMap);
		
		mailInfo.setFileName(billNo + ".pdf");
		mailInfo.setFilePath(pdfFile.getAbsolutePath());
		sendEmail.setSendAttchmentPath(billNo + ".pdf");
		new Thread(){
			@Override
			public void run() {
				mailManager.send(mailInfo);
			}
		}.start();
		
		sendEmail.setSentDate(new Date());
		sendEmail.setSendFlag("1");
		sendEmailService.save(sendEmail);
	}

	
	
	/**
	 * 提单转换成入库单
	 * 
	 */
	@RequestMapping(value = "toIn")
	@ResponseBody
	public String toInventoryInBill() {
		this.psiLadingBillService.toInventoryInBill();
		return "提单生成入库单完毕！！！";
	}
	
	@RequestMapping(value = "updateDelayDays")
	@ResponseBody
	public String updateDelayDays() {
		this.psiLadingBillService.updatePayDelayDays();
		return "updateDelayDays完毕！！！";
	}

}
