/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web.lc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.Region;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
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
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.Collections3;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PurchasePaymentItemDto;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseAmountAdjust;
import com.springrain.erp.modules.psi.entity.lc.LcPurchasePayment;
import com.springrain.erp.modules.psi.entity.lc.LcPurchasePaymentItem;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.lc.LcPsiLadingBillService;
import com.springrain.erp.modules.psi.service.lc.LcPurchaseAmountAdjustService;
import com.springrain.erp.modules.psi.service.lc.LcPurchaseOrderItemService;
import com.springrain.erp.modules.psi.service.lc.LcPurchaseOrderService;
import com.springrain.erp.modules.psi.service.lc.LcPurchasePaymentService;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 采购付款Controller
 * @author Michael
 * @version 2014-11-21
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/lcPurchasePayment")
public class LcPurchasePaymentController extends BaseController {
	
	@Autowired
	private LcPurchasePaymentService		 purchasePaymentService;
	@Autowired
	private LcPsiLadingBillService           psiLadingBillService;
	@Autowired
	private PsiSupplierService               psiSupplierService;
	@Autowired
	private LcPurchaseAmountAdjustService    adjustService;
	@Autowired
	private SystemService 					 systemService;
	
	private static String filePath;
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"list", ""})
	public String list(LcPurchasePayment purchasePayment, HttpServletRequest request, HttpServletResponse response, Model model) {
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (purchasePayment.getCreateDate() == null) {
			purchasePayment.setCreateDate(DateUtils.addMonths(today, -1));
			purchasePayment.setUpdateDate(today);
		}
		Page<LcPurchasePayment> page = new Page<LcPurchasePayment>(request, response);
        page = purchasePaymentService.find(page, purchasePayment); 
        List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
		model.addAttribute("suppliers", suppliers);
        model.addAttribute("page", page);
		return "modules/psi/lc/lcPurchasePaymentList";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "add")
	public String add(LcPurchasePayment purchasePayment, Model model) {
		List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
//		Map<String, Object[]>  orderMaps= Maps.newLinkedHashMap();
//		Map<String, Object[]>  ladingMaps=Maps.newLinkedHashMap();
		Map<String, String> accountMaps= null;
		Integer supplierId =0;
		PsiSupplier supplier= null;
		if(purchasePayment.getSupplier()==null){
			if(suppliers!=null&&suppliers.size()>0){
				supplier=suppliers.get(0);
				supplierId=supplier.getId();
			}
		}else{
			supplierId=purchasePayment.getSupplier().getId();
			for (PsiSupplier psiSupplier : suppliers) {
				if(psiSupplier.getId().equals(supplierId)){
					supplier=psiSupplier;
					break;
				}
			}
		}
		
		List<PurchasePaymentItemDto> ladingItemDtos =Lists.newArrayList();
		Set<String> months = Sets.newHashSet();
		Set<String> billNos = Sets.newHashSet();
		Set<Integer> rates = Sets.newHashSet();
		//查出未付款的订单信息
		List<Object[]> orders  = this.purchasePaymentService.getUnPaymentOrder(supplierId,null);
		Set<String> orderNoSet = Sets.newHashSet();
		Set<String> orderNoProNameSet = Sets.newHashSet();
		for(Object[] object:orders){
			months.add(object[0].toString().substring(0, 6));
			orderNoSet.add(object[0].toString());
		}
		//账号信息
		if(supplier!=null){
			accountMaps= supplier.getAccountMap();
		}
		//查出未付完款的提单项信息
		
		ladingItemDtos = this.purchasePaymentService.getUnPaymentLadingItem(supplierId, null,false);
		Set<Integer> ladingIds = Sets.newHashSet();
		for(PurchasePaymentItemDto dto: ladingItemDtos){
			months.add(dto.getBillNo().substring(0, 6));
			billNos.add(dto.getBillNo());
			rates.add(dto.getRate());
			orderNoProNameSet.add(dto.getOrderNo()+","+dto.getProductName());
			ladingIds.add(dto.getLadingBillId());
		}
	
		//根据supplierid查询要付的额外付款项
		List<LcPurchaseAmountAdjust> adjusts=null;
		if(supplier!=null){
			adjusts= adjustService.findAdjustOrders(supplier.getId(),null,"r",null);
			for (Iterator<LcPurchaseAmountAdjust> iterator = adjusts.iterator(); iterator.hasNext();) {
				LcPurchaseAmountAdjust adjust = (LcPurchaseAmountAdjust) iterator.next();
				String orderNo = adjust.getOrderNo();
				String orderKey = orderNo+","+adjust.getProductNameColor();
				if(!orderNoSet.contains(orderNo)&&!orderNoProNameSet.contains(orderKey)){//如果订金不存在，并且提单里面也不存在，这个单的这个产品                       删除
					iterator.remove();
				}
			}
		}
		
		
		//添加质检金额限制
		Map<String,Float> testAmountMap=Maps.newHashMap();
		if(ladingIds.size()>0){
			testAmountMap =this.purchasePaymentService.getTestPayAmount(ladingIds);
		}
		
		model.addAttribute("testAmountMap", JSON.toJSON(testAmountMap));
		
		model.addAttribute("ladingItemDtos", ladingItemDtos);
		model.addAttribute("orders", orders);
		model.addAttribute("months", months);
		model.addAttribute("billNos", billNos);
		model.addAttribute("rates", rates);
		model.addAttribute("accountMaps", accountMaps);
		model.addAttribute("purchasePayment", purchasePayment);
		model.addAttribute("adjusts", adjusts);
		model.addAttribute("suppliers", suppliers);
		return "modules/psi/lc/lcPurchasePaymentAdd";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "addSave")
	public String addSave(LcPurchasePayment purchasePayment, Model model, RedirectAttributes redirectAttributes) throws Exception {
		if(this.purchasePaymentService.addSave(purchasePayment)){
			addMessage(redirectAttributes, "保存采购付款'" + purchasePayment.getPaymentNo() + "'成功");
		}else{
			addMessage(redirectAttributes, "申请邮件发送失败，请重新申请!!");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/lcPurchasePayment/?repage";
	}
	
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "edit")
	public String edit(LcPurchasePayment purchasePayment, Model model) {
		purchasePayment =this.purchasePaymentService.get(purchasePayment.getId());
		StringBuilder sb = new StringBuilder("");
		for(LcPurchasePaymentItem item:purchasePayment.getItems()){
			sb.append(item.getId()+",");
		}
		String itemIds="";
		if(StringUtils.isNotEmpty(sb.toString())){
			itemIds=sb.toString().substring(0,sb.toString().length()-1);
		}
		purchasePayment.setOldItemIds(itemIds);
		
		
		HashMap<String, Object[]>  orderMaps= new HashMap<String, Object[]>();
		HashMap<String, Object[]>  ladingMaps= new HashMap<String, Object[]>();
		PsiSupplier  supplier= purchasePayment.getSupplier();
		//查出未付款的订单信息
		List<Object[]> orders  = this.purchasePaymentService.getUnPaymentOrder(supplier.getId(),purchasePayment.getCurrencyType());
		for(Object[] object:orders){
			orderMaps.put(object[0].toString(), object);
		}
		//账号信息
		Map<String, String> accountMaps= purchasePayment.getSupplier().getAccountMap();
		
		//查出未付完款的提单项信息
		List<Object[]> ladings = this.purchasePaymentService.getUnPaymentLading(supplier.getId(),purchasePayment.getCurrencyType());
		for(Object[] object:ladings){
			ladingMaps.put(object[0].toString(), object);
		}
		
		//根据supplierid查询要付的额外付款项
		List<LcPurchaseAmountAdjust> adjusts= adjustService.findAdjustOrders(supplier.getId(),null,"0",purchasePayment.getCurrencyType());  
		model.addAttribute("orderSet", orderMaps.keySet());
		model.addAttribute("ladingSet",ladingMaps.keySet());
		model.addAttribute("orderKeys", JSON.toJSON(orderMaps.keySet()));
		model.addAttribute("ladingKeys", JSON.toJSON(ladingMaps.keySet()));
		model.addAttribute("accountMaps", accountMaps);
		model.addAttribute("orderMaps", JSON.toJSON(orderMaps));
		model.addAttribute("ladingMaps", JSON.toJSON(ladingMaps));
		model.addAttribute("purchasePayment", purchasePayment);
		model.addAttribute("adjusts", adjusts);
		return "modules/psi/lc/lcPurchasePaymentEdit";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "view")
	public String view(LcPurchasePayment purchasePayment, Model model) {
		purchasePayment =this.purchasePaymentService.get(purchasePayment.getId());
		List<LcPurchaseAmountAdjust> adjusts= adjustService.findAdjustOrders(purchasePayment.getSupplier().getId(),purchasePayment.getId(),null,purchasePayment.getCurrencyType());
		model.addAttribute("purchasePayment", purchasePayment);
		model.addAttribute("adjusts", adjusts);
		return "modules/psi/lc/lcPurchasePaymentView";
	}
	
	
	
//	@RequiresPermissions("psi:all:view")
//	@RequestMapping(value = "editSave")
//	public String editSave(LcPurchasePayment purchasePayment, Model model, RedirectAttributes redirectAttributes) {
//		//遍历item
//		if(purchasePayment.getItems()!=null){
//			if(this.purchasePaymentService.editSave(purchasePayment)){
//				addMessage(redirectAttributes, "保存采购付款'" + purchasePayment.getPaymentNo() + "'成功");
//			}else{
//				return "redirect:"+Global.getAdminPath()+"/psi/lcPurchasePayment/?repage";
//			}
//		}else{
//			addMessage(redirectAttributes, "保存采购付款失败，付款项为空");
//		}
//		return "redirect:"+Global.getAdminPath()+"/psi/lcPurchasePayment/?repage";
//	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "sure")
	public String sure(LcPurchasePayment purchasePayment, Model model) {
		purchasePayment =this.purchasePaymentService.get(purchasePayment.getId());
//		Map<String, Object[]>  orderMaps= Maps.newLinkedHashMap();
//		Map<String, Object[]>  ladingMaps=Maps.newLinkedHashMap();
//		for(LcPurchasePaymentItem item:purchasePayment.getItems()){
//			//区分是提单还是订单
//			if(item.getPaymentType()!=null&&!"".equals(item.getPaymentType())){
//				if("0".equals(item.getPaymentType())){
//					Object[] object={item.getOrder().getOrderNo(),item.getOrder().getDepositPreAmount()};
//					orderMaps.put(item.getOrder().getOrderNo(), object);
//				}else if("1".equals(item.getPaymentType())){
//					Object[] object={item.getLadingBill().getBillNo(),item.getLadingBill().getTotalAmount()};
//					ladingMaps.put(item.getLadingBill().getBillNo(), object);
//				}
//			}
//			
//		}
		//根据supplierid查询要付的额外付款项
		List<LcPurchaseAmountAdjust> adjusts= adjustService.findAdjustOrders(purchasePayment.getSupplier().getId(),purchasePayment.getId(),"1",purchasePayment.getCurrencyType());
		//账号信息
		Map<String, String> accountMaps= purchasePayment.getSupplier().getAccountMap();
		model.addAttribute("accountMaps", JSON.toJSON(accountMaps));
//		model.addAttribute("orderMaps", JSON.toJSON(orderMaps));
//		model.addAttribute("ladingMaps", JSON.toJSON(ladingMaps));
		model.addAttribute("adjusts", adjusts);
		model.addAttribute("purchasePayment", purchasePayment);
		return "modules/psi/lc/lcPurchasePaymentSure";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "review")
	public String review(LcPurchasePayment purchasePayment, Model model,RedirectAttributes redirectAttributes) {
		purchasePayment =this.purchasePaymentService.get(purchasePayment.getId());
		if(!"1".equals(purchasePayment.getPaymentSta())){
			addMessage(redirectAttributes, "付款状态不对，请刷新后重试!!");
			return "redirect:"+Global.getAdminPath()+"/psi/lcPurchasePayment/?repage";
		}
		//根据supplierid查询要付的额外付款项
		List<LcPurchaseAmountAdjust> adjusts= adjustService.findAdjustOrders(purchasePayment.getSupplier().getId(),purchasePayment.getId(),"1",purchasePayment.getCurrencyType());
		//账号信息
		Map<String, String> accountMaps= purchasePayment.getSupplier().getAccountMap();
		model.addAttribute("accountMaps", JSON.toJSON(accountMaps));
		model.addAttribute("adjusts", adjusts);
		model.addAttribute("purchasePayment", purchasePayment);
		return "modules/psi/lc/lcPurchasePaymentReview";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "reviewSave")
	public String reviewSave(LcPurchasePayment purchasePayment, Model model, RedirectAttributes redirectAttributes) {
		//财务审核通过，或者取消
		this.purchasePaymentService.reviewSave(purchasePayment);
		addMessage(redirectAttributes, "财务审核采购付款成功^…^");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPurchasePayment/?repage";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "sureSave")
	public String sureSave(MultipartFile memoFile,LcPurchasePayment purchasePayment, Model model, RedirectAttributes redirectAttributes) {
		if(!"r".equals(purchasePayment.getPaymentSta())){
			addMessage(redirectAttributes, "付款状态不对，请刷新后重试!!");
			return "redirect:"+Global.getAdminPath()+"/psi/lcPurchasePayment/?repage";
		}
		BigDecimal curPaymentAmount = purchasePayment.getCurPaymentAmount();
		purchasePayment=this.purchasePaymentService.get(purchasePayment.getId());
		//保存凭证
		if(memoFile!=null&&memoFile.getSize()!=0){
			if (filePath == null) {
				filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/lcPurchasePayment";
			}
			File baseDir = new File(filePath+"/"+purchasePayment.getPaymentNo()); 
			if(!baseDir.isDirectory())
				baseDir.mkdirs();
			String suffix = memoFile.getOriginalFilename().substring(memoFile.getOriginalFilename().lastIndexOf("."));     
			String name=UUID.randomUUID().toString()+suffix;
			File dest = new File(baseDir,name);
			try {
				FileUtils.copyInputStreamToFile(memoFile.getInputStream(),dest);
				purchasePayment.setAttchmentPath("/psi/lcPurchasePayment/"+purchasePayment.getPaymentNo()+"/"+name);
			} catch (IOException e) {
				logger.warn(name+"文件保存失败",e);
			}
		}
		  
		//如果这次不等
		purchasePayment.setRealPaymentAmount(purchasePayment.getRealPaymentAmount().add(curPaymentAmount));
		if(purchasePayment.getPaymentAmountTotal().compareTo(purchasePayment.getRealPaymentAmount())!=0){
			this.purchasePaymentService.save(purchasePayment);
		}else{
			this.purchasePaymentService.sureSave(purchasePayment);
		}
		
		if(purchasePayment.getItems()!=null){
			addMessage(redirectAttributes, "确认采购付款'" + purchasePayment.getPaymentNo() + "'成功");
		}else{
			addMessage(redirectAttributes, "确认采购付款失败，付款项为空");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/lcPurchasePayment/?repage";
		
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
	@RequestMapping(value = "form")
	public String form(LcPurchasePayment purchasePayment, Model model) {
		model.addAttribute("purchasePayment", purchasePayment);
		return "modules/psi/lc/lcPurchasePaymentForm";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "save")
	public String save(LcPurchasePayment purchasePayment, Model model, RedirectAttributes redirectAttributes) {
		purchasePaymentService.save(purchasePayment);
		addMessage(redirectAttributes, "保存采购付款'" + purchasePayment.getPaymentNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPurchasePayment/?repage";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "cancel")
	public String cancel(LcPurchasePayment purchasePayment, Model model, RedirectAttributes redirectAttributes) {
		purchasePayment=this.purchasePaymentService.get(purchasePayment.getId());
		if("3".equals(purchasePayment.getPaymentSta())){
			addMessage(redirectAttributes, "error：不能多次取消'" + purchasePayment.getPaymentNo());
			return "redirect:"+Global.getAdminPath()+"/psi/lcPurchasePayment/?repage";
		}
		//只有申请和已确认状态，没有草稿状态
		this.purchasePaymentService.cancelPurchasePayment(purchasePayment);
		//付款取消通知
		List<User> userList = systemService.findUserByPermission("payment:operate:user");
		List<User> replys = Lists.newArrayList();
		if(userList!=null){
			replys.addAll(userList);
		}
		replys.add(UserUtils.getUser());
		String toAddress = Collections3.extractToString(replys,"email", ",");
		String content = "Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;理诚付款单取消，请点击：<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/lcPurchasePayment/view?id="+purchasePayment.getId()+"'>"+purchasePayment.getPaymentNo()+"</a>查看";
		this.purchasePaymentService.sendNoticeEmail(toAddress, content, "理诚付款单取消["+purchasePayment.getPaymentNo()+"]", purchasePayment.getCreateUser().getEmail(), "");
		addMessage(redirectAttributes, "取消采购付款'" + purchasePayment.getPaymentNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPurchasePayment/?repage";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		purchasePaymentService.delete(id);
		addMessage(redirectAttributes, "删除采购付款成功");
		return "redirect:"+Global.getAdminPath()+"/psi/lcPurchasePayment/?repage";
	}
	
	@ResponseBody
	@RequestMapping(value = {"ajaxOrderAjust"})
	public String ajaxOrderAjust(String typeAndIds) {
		//提单set
		Set<Integer> orderIds=Sets.newHashSet();
		Set<Integer> ladingIds=Sets.newHashSet();
		if(StringUtils.isNotEmpty(typeAndIds)){
			for(String typeAndId:typeAndIds.split(",")){
				String arr[] = typeAndId.split(":");
				if("1".equals(arr[0])){
					ladingIds.add(Integer.parseInt(arr[1]));
				}else{
					orderIds.add(Integer.parseInt(arr[1]));
				}
			}
		}
		
		if(ladingIds.size()>0){
			//查出提单对应的订单
			List<Integer> tempOrderIds =this.psiLadingBillService.getOrderIdByBillId(ladingIds);
			if(tempOrderIds!=null&&tempOrderIds.size()>0){
				orderIds.addAll(tempOrderIds);
			}
		}
		
		//查询采购订单调整表里的信息
		List<LcPurchaseAmountAdjust> adjusts= adjustService.findAdjustOrders(orderIds);
		StringBuilder items= new StringBuilder("[");
		if(adjusts!=null&&adjusts.size()>0){
			for(LcPurchaseAmountAdjust adjust:adjusts){
				items.append("{\"id\":\"").append(adjust.getId()).append("\",\"orderNo\":\"").append(adjust.getSubject()).append("\",\"amount\":\"").append(adjust.getAdjustAmount())
				.append("\",\"remark\":\"").append(adjust.getRemark()).append("\"},");
			}
			items= new StringBuilder(items.substring(0, items.length()-1));
		}
		items.append("]");
		
		String rs="{\"items\":"+items.toString()+"}";
		return rs;
	}
	
	@RequestMapping(value =  "print" )
	public String print(Integer id,HttpServletRequest request, HttpServletResponse response, Model model) throws UnsupportedEncodingException{
		if(id==null){
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		LcPurchasePayment  payment = this.purchasePaymentService.get(id);
		
		StringBuilder orderNo=new StringBuilder();
		StringBuilder ladingNo =new StringBuilder();
		StringBuilder ladingOrderNo=new StringBuilder();
		for(LcPurchasePaymentItem item:payment.getItems()){
			if(item.getOrder()!=null){
				if(orderNo.indexOf(item.getBillNo()+",")<0){
					orderNo.append(item.getOrder().getOrderNo()).append(",");
				}
			}else{
				if(ladingNo.indexOf(item.getBillNo()+",")<0){
					ladingNo.append(item.getBillNo()).append(",");
				}
				String ladingOrderNoTemp=item.getLadingBillItem().getPurchaseOrderItem().getPurchaseOrder().getOrderNo();
				if(ladingOrderNo.indexOf(ladingOrderNoTemp+",")<0&&orderNo.indexOf(ladingOrderNoTemp+",")<0){
					ladingOrderNo.append(ladingOrderNoTemp).append(",");
				}
			}
		}
		
		
		PsiSupplier supplier =payment.getSupplier();
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		sheet.setColumnWidth(0, (short)15 * 256);
		sheet.setColumnWidth(1, (short)22 * 256);
		sheet.setColumnWidth(2, (short)12 * 256);
		sheet.setColumnWidth(3, (short)10 * 256);
		sheet.setColumnWidth(4, (short)12 * 256);
		sheet.setColumnWidth(5, (short)10 * 256);
		sheet.setColumnWidth(6, (short)10 * 256);
		sheet.setColumnWidth(7, (short)10 * 256);
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont font = wb.createFont();
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font.setFontHeightInPoints((short) 14); // 字体高度
		font.setFontName("宋体");
		style1.setFont(font);
		style1.setWrapText(true);
		
		HSSFCellStyle style2 = wb.createCellStyle();
		style2.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		style2.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style2.setBorderLeft((short)2);
		style2.setBorderRight((short)2);
		style2.setBorderTop((short)2);
		style2.setBorderBottom((short)2);
		HSSFFont font2 = wb.createFont();
		font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		font2.setFontHeightInPoints((short) 12); // 字体高度
		font2.setFontName("宋体");
		style2.setFont(font2);
		style2.setWrapText(true);
		
		HSSFCellStyle style3 = wb.createCellStyle();
		style3.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		style3.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		style3.setBorderLeft((short)2);
		style3.setBorderRight((short)2);
		style3.setBorderTop((short)2);
		style3.setBorderBottom((short)2);
		HSSFFont font3 = wb.createFont();
		font3.setFontHeightInPoints((short) 10); // 字体高度
		font3.setFontName("宋体");
		style3.setFont(font3);
		style3.setWrapText(true);
		
		sheet.setMargin(HSSFSheet.TopMargin,0);// 页边距（上）    
//		sheet.setMargin(HSSFSheet.BottomMargin,1.5);// 页边距（下）    
		sheet.setMargin(HSSFSheet.LeftMargin,1);// 页边距（左）    
//		sheet.setMargin(HSSFSheet.RightMargin,1.5);// 页边距（右   
        
		 // 四个参数分别是：起始行，起始列，结束行，结束列  
		sheet.addMergedRegion(new Region(0, new Short("0"), 0, new Short("7")));   
		sheet.addMergedRegion(new Region(1, new Short("0"),1, new Short("7")));
		sheet.addMergedRegion(new Region(2, new Short("0"),2, new Short("7")));
		sheet.addMergedRegion(new Region(3, new Short("3"),3, new Short("7")));
		sheet.addMergedRegion(new Region(4, new Short("3"),4, new Short("7")));
		sheet.addMergedRegion(new Region(5, new Short("3"),5, new Short("7")));
		sheet.addMergedRegion(new Region(6, new Short("3"),6, new Short("7")));
		
		sheet.addMergedRegion(new Region(7, new Short("1"),7, new Short("7")));
		sheet.addMergedRegion(new Region(8, new Short("1"),8, new Short("7")));
		sheet.addMergedRegion(new Region(9, new Short("1"),9, new Short("7")));
		
		
		sheet.addMergedRegion(new Region(11, new Short("3"),11, new Short("7")));
		
		sheet.addMergedRegion(new Region(12, new Short("1"),12, new Short("7")));
		sheet.addMergedRegion(new Region(13, new Short("0"),13, new Short("7")));
		sheet.addMergedRegion(new Region(14, new Short("0"),14, new Short("7")));
		sheet.addMergedRegion(new Region(15, new Short("0"),15, new Short("7")));
		
		Integer rowIndex=0;
		HSSFRow row = sheet.createRow(rowIndex++);
		row.setHeight((short)(2*256));
		HSSFCell cell =row.createCell(0,Cell.CELL_TYPE_STRING);
		cell.setCellValue("深圳市理诚科技有限公司"); 
		cell.setCellStyle(style1);
		
		row = sheet.createRow(rowIndex++);
		row.setHeight((short)(2*256));
		cell =row.createCell(0,Cell.CELL_TYPE_STRING);
		cell.setCellValue("采购付款申请单"); 
		cell.setCellStyle(style1);
		
		row = sheet.createRow(rowIndex++);
		cell =row.createCell(0,Cell.CELL_TYPE_STRING);
		cell.setCellValue("编号："+(payment.getPayFlowNo()==null?"":payment.getPayFlowNo())); 
		
		List<String> list = Lists.newArrayList();
		list.add("申请日期,付款日期");
		list.add("申请部门,收款单位");
		list.add("付款方式,发票");
		list.add("付款单号,发票号");
		list.add("账号信息");
		list.add("付款事由");
		list.add("付款金额");
		list.add("经办人,财务审核,财务主管,出纳");
		list.add("部门负责人,总经理");
		list.add("备注");
		for(int i=0;i<10;i++){
			String title = list.get(i);   
			if(i!=4&&i!=5&&i!=6&&i!=9){
				
				String value1="";
				String value2="";
				String arr[]=title.split(",");
				if("申请日期".equals(arr[0])){
					value1=sdf.format(payment.getCreateDate());
					if(payment.getSureDate()!=null){
						value2=sdf.format(payment.getSureDate());
					}
				}else if("申请部门".equals(arr[0])){
					value1=payment.getCreateUser().getOffice().getName();
					value2="("+supplier.getNikename()+")"+supplier.getName();
				}else if("付款方式".equals(arr[0])){
					value1="网银转账";
					value2="已开/未开";
				}else if("付款单号".equals(arr[0])){
					value1=payment.getPaymentNo();
				}else if("经办人".equals(arr[0])){
					value1=payment.getCreateUser().getName();
					value2="";
				}else if("部门负责人".equals(arr[0])){
					value1="emma";
				}
				
				
				row = sheet.createRow(rowIndex++);
				row.setHeight((short)(1.6*256));
				cell =row.createCell(0,Cell.CELL_TYPE_STRING);
				cell.setCellValue(arr[0]); 
				cell.setCellStyle(style2);
				
				cell =row.createCell(1,Cell.CELL_TYPE_STRING);
				cell.setCellValue(value1); 
				cell.setCellStyle(style3);
				
				cell =row.createCell(2,Cell.CELL_TYPE_STRING);
				cell.setCellValue(arr[1]); 
				cell.setCellStyle(style2);   
				
				cell =row.createCell(3,Cell.CELL_TYPE_STRING);
				cell.setCellValue(value2); 
				cell.setCellStyle(style3);
				
				if("经办人".equals(arr[0])){
					cell =row.createCell(4,Cell.CELL_TYPE_STRING);
					cell.setCellValue(arr[2]); 
					cell.setCellStyle(style2);   
					
					cell =row.createCell(5,Cell.CELL_TYPE_STRING);
					cell.setCellValue(""); 
					cell.setCellStyle(style3);
					
					cell =row.createCell(6,Cell.CELL_TYPE_STRING);
					cell.setCellValue(arr[3]); 
					cell.setCellStyle(style2);   
					
					cell =row.createCell(7,Cell.CELL_TYPE_STRING);
					cell.setCellValue(""); 
					cell.setCellStyle(style3);
					
				}else{
					cell =row.createCell(4,Cell.CELL_TYPE_STRING);
					cell.setCellValue(""); 
					cell.setCellStyle(style2);   
					
					cell =row.createCell(5,Cell.CELL_TYPE_STRING);
					cell.setCellValue(""); 
					cell.setCellStyle(style3);
					
					cell =row.createCell(6,Cell.CELL_TYPE_STRING);
					cell.setCellValue(""); 
					cell.setCellStyle(style2);   
					
					cell =row.createCell(7,Cell.CELL_TYPE_STRING);
					cell.setCellValue(""); 
					cell.setCellStyle(style3);
				}
				
			}else{
				row = sheet.createRow(rowIndex++);
				if(i==6){
					row.setHeight((short)(1.6*256));
				}else if(i==4){
					row.setHeight((short)(6*256));
				}else{
					row.setHeight((short)(2*256));
				}
				String value1="";
				if("账号信息".equals(title)){
					value1=payment.getAccountNoBr();
				}else if("付款事由".equals(title)){
					if(StringUtils.isNotEmpty(ladingNo)){
						value1+="尾款";
					}
					if(StringUtils.isNotEmpty(orderNo)){
						value1+="定金";
					}
				}else if("付款金额".equals(title)){
					value1=payment.getPaymentAmountTotal()+payment.getCurrencyType();
				}else if("备注".equals(title)){
					if(StringUtils.isNotEmpty(ladingNo)){
						value1+="提单号:"+ladingNo;
					}
					
					if(StringUtils.isNotEmpty(orderNo)&&StringUtils.isEmpty(ladingOrderNo)){
						value1+="订单号:"+orderNo;
					}else if(StringUtils.isEmpty(orderNo)&&StringUtils.isNotEmpty(ladingOrderNo)){
						value1+="订单号:"+ladingOrderNo;
					}else if(StringUtils.isNotEmpty(orderNo)&&StringUtils.isNotEmpty(ladingOrderNo)){
						value1+="订单号:"+orderNo+ladingOrderNo;
					}
				}
				
				cell =row.createCell(0,Cell.CELL_TYPE_STRING);
				cell.setCellValue(title); 
				cell.setCellStyle(style2);
				
				cell =row.createCell(1,Cell.CELL_TYPE_STRING);
				cell.setCellValue(value1); 
				cell.setCellStyle(style3);
				
				for(int j=2;j<8;j++){
					cell =row.createCell(j,Cell.CELL_TYPE_STRING);
					cell.setCellValue(""); 
					cell.setCellStyle(style3);
				}
			}
		}
		  
		row = sheet.createRow(rowIndex++);
		cell =row.createCell(0,Cell.CELL_TYPE_STRING);
		cell.setCellValue(payment.getCreateUser().getName()+"申请，申请时间："+sdf.format(payment.getCreateDate()));
		cell.setCellStyle(style3);
		
		for(int j=1;j<8;j++){
			cell =row.createCell(j,Cell.CELL_TYPE_STRING);
			cell.setCellValue(""); 
			cell.setCellStyle(style3);
		}
		
		row = sheet.createRow(rowIndex++);
		cell =row.createCell(0,Cell.CELL_TYPE_STRING);
		if(payment.getApplyUser()!=null){
			cell.setCellValue(payment.getApplyUser().getName()+"审核，申请时间："+sdf.format(payment.getApplyDate()));
		}else{
			cell.setCellValue("");
		}
		cell.setCellStyle(style3);
		
		
		for(int j=1;j<8;j++){
			cell =row.createCell(j,Cell.CELL_TYPE_STRING);
			cell.setCellValue(""); 
			cell.setCellStyle(style3);
		}
		
		row = sheet.createRow(rowIndex++);
		cell =row.createCell(0,Cell.CELL_TYPE_STRING);
		if(payment.getSureUser()!=null){
			cell.setCellValue(payment.getSureUser().getName()+"复核，复核时间："+sdf.format(payment.getSureDate()));
		}else{
			cell.setCellValue("");
		}
		cell.setCellStyle(style3);
		
		
		for(int j=1;j<8;j++){
			cell =row.createCell(j,Cell.CELL_TYPE_STRING);
			cell.setCellValue(""); 
			cell.setCellStyle(style3);
		}
		
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");
	response.setContentType("application/x-download");

	SimpleDateFormat sdf1 = new SimpleDateFormat("MMddhhmm");

	String fileName = "Pay" + sdf1.format(new Date()) + ".xls";
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
	
	
	//导出付款新增选项
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value =  "expPayment" )
	public String expPayment(Integer supplierId,HttpServletRequest request, HttpServletResponse response, Model model) throws UnsupportedEncodingException{
		//查询barcode表，获得所有产品及市场
		//查出未付款的订单信息
		List<Object[]> orders  = this.purchasePaymentService.getUnPaymentOrder(supplierId,null);
		//查出未付完款的提单项信息
		List<PurchasePaymentItemDto> ladingItemDtos = this.purchasePaymentService.getUnPaymentLadingItem(supplierId, null,true);
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		String[] title = {" 付款类型 ","逾期天数"," 单号 "," 月份  ", " 产品名 ", " 国家  "," 数量  "," 单价 "," 币种 "," 定金比例(%) " ," 尾款档位 (%)","总金额","已付金额","未付金额"};
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 11); // 字体高度
		style.setFont(font);
		
		row.setHeight((short) 400);
		HSSFCell cell = null;		
		for (int i = 0; i < title.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		
		
		HSSFCellStyle style1 = wb.createCellStyle();
		HSSFDataFormat df1 = wb.createDataFormat();  //此处设置数据格式  
		style1.setDataFormat(df1.getFormat("#0.00")); //小数点后保留两位，可以写contentStyle.setDataFormat(df.getFormat("#,#0.00"));  
		DecimalFormat  df = new DecimalFormat("0.##");
		int j =1;
		for(Object[] object:orders){
			row = sheet.createRow(j++);
			int i =0;
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("定金");
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(object[0].toString());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(object[0].toString().substring(0, 6));
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
			cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(style1);
			cell.setCellValue(Float.parseFloat(object[3].toString()));
			
			cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(style1);
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(Float.parseFloat(object[4].toString()));
			
			cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(style1);
			cell.setCellValue(df.format(object[5]));
			
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(0);
			
			cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(style1);
			cell.setCellValue(Float.parseFloat(object[2].toString()));
		}

		for(PurchasePaymentItemDto dto :ladingItemDtos){
			row = sheet.createRow(j++);
			int i =0;
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("尾款"); 
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(dto.getDelayDays()); 
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(dto.getBillNo());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(dto.getBillNo().substring(0, 6));
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(dto.getProductName());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(dto.getCountry());
			cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
			cell.setCellValue(dto.getQuantity());
			
			cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(style1);
			cell.setCellValue(dto.getItemPrice().doubleValue());
			
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(dto.getCurrency());
			cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(style1);
			cell.setCellValue(dto.getDeposit());
			
			cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(style1);
			cell.setCellValue(dto.getRate());
			
			cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(style1);
			cell.setCellValue(dto.getTotalAmount().doubleValue());
			
			cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(style1);
			cell.setCellValue(dto.getTotalPaymentAmount().doubleValue());
			
			cell=row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
			cell.setCellStyle(style1);
			cell.setCellValue(dto.getTotalAmount().subtract(dto.getTotalPaymentAmount()).doubleValue());
		}
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
	
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	
		String fileName = "purchasePaymentInfos" + sdf.format(new Date()) + ".xls";
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
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value =  "expPaymentTotal" )
	public String expPaymentTotal(LcPurchasePayment purchasePayment, HttpServletRequest request, HttpServletResponse response, Model model) throws UnsupportedEncodingException{
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年MM月dd日");
		List<LcPurchasePayment> payments  = this.purchasePaymentService.find(purchasePayment);
		//查出未付完款的提单项信息
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		sheet.addMergedRegion(new Region(0, new Short("0"), 0, new Short("13")));   
		sheet.addMergedRegion(new Region(1, new Short("0"),1, new Short("7")));
		sheet.addMergedRegion(new Region(1, new Short("8"),1, new Short("13")));
		
		HSSFCellStyle style3 = wb.createCellStyle();
		style3.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont font1 = wb.createFont();
		font1.setFontHeightInPoints((short) 18); // 字体高度
		font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style3.setFont(font1);
		
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 14); // 字体高度
		style.setFont(font);
		
		String[] title = {" 序号 ","申请日期"," 申请人 "," 申请部门  ", " 付款单号 ", " 收款单位  "," 付款事由 "," 币别 "," 付款日期 " ," 付款金额", "收发票日期", "发票号码", "发票金额"," 收款银行账号  " };
		
		
		
		HSSFCellStyle style1 = wb.createCellStyle();
		HSSFDataFormat df1 = wb.createDataFormat();  //此处设置数据格式  
		style1.setDataFormat(df1.getFormat("#0.00")); //小数点后保留两位，可以写contentStyle.setDataFormat(df.getFormat("#,#0.00"));  
		
		HSSFCellStyle style2 = wb.createCellStyle();
		
		style2.setWrapText(true);
		
		int j =0;
		row = sheet.createRow(j++);
		HSSFCell headCell= row.createCell(0,Cell.CELL_TYPE_STRING);
		headCell.setCellStyle(style3);
		headCell.setCellValue("深圳理诚科技有限公司");
		
		row = sheet.createRow(j++);
		row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue("付款明细");
		String dateStr=sdf2.format(purchasePayment.getCreateDate())+"至"+sdf2.format(purchasePayment.getUpdateDate());
		row.createCell(8,Cell.CELL_TYPE_STRING).setCellValue("付款日期："+dateStr);
		
		row = sheet.createRow(j++);
		HSSFCell cell = null;		
		for (int i = 0; i < title.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		
		BigDecimal  total = BigDecimal.ZERO;
		for(LcPurchasePayment payment:payments){
			total=total.add(payment.getPaymentAmountTotal());
			StringBuilder orderNo=new StringBuilder();
			StringBuilder ladingNo =new StringBuilder();
			for(LcPurchasePaymentItem item:payment.getItems()){
				if(item.getOrder()!=null){
					if(orderNo.indexOf(item.getBillNo()+",")<0){
						orderNo.append(item.getOrder().getOrderNo()).append(",");
					}
				}else{
					if(ladingNo.indexOf(item.getBillNo()+",")<0){
						ladingNo.append(item.getBillNo()).append(",");
					}
				}
			}
			
			String reason ="";
			if(StringUtils.isNotEmpty(orderNo)&&StringUtils.isNotEmpty(ladingNo)){
				reason="定金/尾款";
			}else if(StringUtils.isNotEmpty(orderNo)){
				reason="定金";
			}else if(StringUtils.isNotEmpty(ladingNo)){
				reason="尾款";
			}
			
			row = sheet.createRow(j++);
			int i =0;
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(payment.getId());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sdf1.format(payment.getCreateDate()));
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(payment.getCreateUser().getName());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(payment.getCreateUser().getOffice().getName());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(payment.getPaymentNo());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(payment.getSupplier().getName());
			
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(reason);
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(payment.getCurrencyType());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sdf1.format(payment.getSureDate()));
			
			HSSFCell cellF= row.createCell(i++,Cell.CELL_TYPE_NUMERIC);
			cellF.setCellStyle(style1);
			cellF.setCellValue(payment.getPaymentAmountTotal().doubleValue());
			
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");
			
			HSSFCell cellFF =row.createCell(i++,Cell.CELL_TYPE_STRING);
			cellFF.setCellValue(payment.getAccountNoBr());
		}
		
		row = sheet.createRow(j++);
		row.createCell(5,Cell.CELL_TYPE_STRING).setCellValue("合计");
		HSSFCell cell1 =row.createCell(9,Cell.CELL_TYPE_STRING);
		cell1.setCellStyle(style1);
		cell1.setCellValue(total.doubleValue());
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
	
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	
		String fileName = "paymentTotalInfos" + sdf.format(new Date()) + ".xls";
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
	
		
		
		
}
