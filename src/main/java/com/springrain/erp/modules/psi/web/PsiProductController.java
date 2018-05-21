package com.springrain.erp.modules.psi.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.coobird.thumbnailator.Thumbnails;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.Collections3;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.ZipUtil;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.ProductPrice;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.order.VendorShipmentService;
import com.springrain.erp.modules.ebay.service.EbayOrderService;
import com.springrain.erp.modules.psi.entity.ProductSupplier;
import com.springrain.erp.modules.psi.entity.PsiBarcode;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.entity.PsiProductGroupUser;
import com.springrain.erp.modules.psi.entity.PsiProductHsCodeDetail;
import com.springrain.erp.modules.psi.entity.PsiProductMoldFee;
import com.springrain.erp.modules.psi.entity.PsiProductTypeGroupDict;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.entity.PsiTransportOrder;
import com.springrain.erp.modules.psi.entity.PsiTransportOrderItem;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrderItem;
import com.springrain.erp.modules.psi.entity.parts.PsiParts;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.psi.service.PsiProductMoldFeeService;
import com.springrain.erp.modules.psi.service.PsiProductPartsService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTieredPriceService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.PsiTransportOrderService;
import com.springrain.erp.modules.psi.service.PurchaseOrderService;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportOrderService;
import com.springrain.erp.modules.psi.service.parts.PsiPartsService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.DictService;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

@Controller
@RequestMapping(value = "${adminPath}/psi/product")
public class PsiProductController extends BaseController {
	@Autowired
	private 	PsiProductService		 		psiProductService;
	@Autowired
	private 	DictService 			 		dictService;
	@Autowired
	private 	PsiSupplierService       		psiSupplierService;
	@Autowired
	private 	PurchaseOrderService     		purchaseOrderService;
	@Autowired
	private 	AmazonProduct2Service    		amazonProduct2Service;
	@Autowired
	private 	EbayOrderService         		ebayOrderService;  
	@Autowired
	private 	SystemService            		systemService;
	@Autowired
	private 	MailManager              		mailManager;
	@Autowired
	private 	PsiPartsService          		partsService;
	@Autowired
	private 	PsiProductPartsService   		productPartsService;
	@Autowired
	private 	PsiTransportOrderService 		transportService;
	@Autowired
	private 	LcPsiTransportOrderService 		lcTransportService;
	@Autowired
	private 	PsiProductTieredPriceService 	tieredPriceService;
	@Autowired
	private 	PsiProductEliminateService 		psiProductEliminateService;
	@Autowired
	private 	PsiInventoryService		 		inventoryService;
	@Autowired
	private 	PsiProductTypeGroupDictService 	groupService;
	@Autowired
	private 	PsiProductGroupUserService 	    groupUserService;
	@Autowired
	private 	PsiProductAttributeService 		psiProductAttributeService;
	@Autowired
	private 	VendorShipmentService    vendorShipmentService;
	@Autowired
	private     AmazonAccountConfigService      amazonAccountConfigService;
	
	@Autowired
	private PsiProductMoldFeeService psiProductMoldFeeService;
	
	@ModelAttribute
	public PsiProduct get(@RequestParam(required = false) Integer id) {
		if (id != null) {
			return psiProductService.get(id);
		} else {
			return new PsiProduct();
		}
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = { "list", "" })
	public String list(String isNewAdd, HttpServletRequest request,HttpServletResponse response, Model model) {
		if (StringUtils.isEmpty(isNewAdd)) {
			isNewAdd = "0";
		}
		
		List<Integer> productIds=tieredPriceService.getProductIdsByManagerUserId(UserUtils.getUser().getId());
		StringBuffer canEditIds = new StringBuffer();
		for(Integer productId:productIds){
			canEditIds.append(productId+",");
		}
			
		model.addAttribute("list",psiProductService.findForList(isNewAdd));
		model.addAttribute("transportTypeMap", psiProductAttributeService.findProductTransportType());
		model.addAttribute("modelAndSupplierMap",psiProductService.getModelAndSupplierMap());
		model.addAttribute("purchaseManagerMap",psiProductService.getPurchaseByProductType());
		List<PsiProductTypeGroupDict> lineList=groupService.getAllList();
		model.addAttribute("lineList",lineList);
		model.addAttribute("isNewAdd", isNewAdd);
		model.addAttribute("canEditIds", canEditIds.toString());
		return "modules/psi/psiProductList";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"productGroupUser"})
	public String productGroupUser(HttpServletRequest request,HttpServletResponse response, Model model) {
		//查询产品线、平台、人员关系
		List<PsiProductTypeGroupDict> productGroups = this.groupService.getAllList();
//		Map<String,Map<String,List<User>>> groupUserMap =groupUserService.getGroupUser();
		Map<String,Map<String,String>> userMap =groupUserService.getSingleGroupUser();
		//TODO
		Map<String,Map<String,List<PsiProductGroupUser>>> lineSalesMap=groupUserService.getLineGroupSales();
		model.addAttribute("lineSalesMap", lineSalesMap);
		List<PsiProductTypeGroupDict>  lineList = groupService.getAllList();
		model.addAttribute("lineList", lineList);
		
		//查找不同国家产品上架
		Map<String,List<String>> shelvesMap= groupUserService.getOnShelves();
		List<String> countryList = Lists.newArrayList("de","fr","es","it","jp","uk","com","ca","mx");
		model.addAttribute("countryList", countryList);
		model.addAttribute("shelvesMap",shelvesMap);
		model.addAttribute("productGroups",productGroups);
		model.addAttribute("userMap",userMap);
		return "modules/psi/psiProductGroupUserList";
	}
	
	@ResponseBody
	@RequestMapping(value = {"productGroupUserSingleSave"})
	public String productGroupUserSingleSave(String groupId,String country,String userId) {
		if(StringUtils.isNotEmpty(country)&&StringUtils.isNotEmpty(groupId)){
			this.groupUserService.editGroupUserRelivate(groupId, country, userId);
		}
		return "保存成功";
	}

	@ResponseBody
	@RequestMapping(value = {"decalreV"})
	public String declareV(String declarePoint) throws UnsupportedEncodingException{
		StringBuffer sb = new StringBuffer();
		declarePoint=URLDecoder.decode(declarePoint, "UTF-8");
		int i=declarePoint.lastIndexOf("电压");
		if(i>0){
			String aa=declarePoint.substring(i);
			try{
				aa=aa.substring(3,aa.indexOf(";"));
			}catch(Exception ex){
				sb.append("[电压];");
			}
		}
		
		i=declarePoint.lastIndexOf("有无接头");
		if(i>0){
			String aa=declarePoint.substring(i);
			try{
				aa=aa.substring(5,aa.indexOf(";"));
			}catch(Exception ex){
				sb.append("[有无接头];");
			}
		}
		
		i=declarePoint.lastIndexOf("试用网络");
		if(i>0){
			String aa=declarePoint.substring(i);
			try{
				aa=aa.substring(5,aa.indexOf(";"));
			}catch(Exception ex){
				sb.append("[试用网络];");
			}
		}
		
		i=declarePoint.lastIndexOf("试用机型");
		if(i>0){
			String aa=declarePoint.substring(i);
			try{
				aa=aa.substring(5,aa.indexOf(";"));
			}catch(Exception ex){
				sb.append("[试用机型];");
			}
		}
		
		i=declarePoint.lastIndexOf("连接方式");
		if(i>0){
			String aa=declarePoint.substring(i);
			try{
				aa=aa.substring(5,aa.indexOf(";"));
			}catch(Exception ex){
				sb.append("[连接方式];");
			}
		}
		
		i=declarePoint.lastIndexOf("有无箱体");
		if(i>0){
			String aa=declarePoint.substring(i);
			try{
				aa=aa.substring(5,aa.indexOf(";"));
			}catch(Exception ex){
				sb.append("[有无箱体];");
			}
		}
		
		i=declarePoint.lastIndexOf("单/双喇叭");
		if(i>0){
			String aa=declarePoint.substring(i);
			try{
				aa=aa.substring(6,aa.indexOf(";"));
			}catch(Exception ex){
				sb.append("[单/双喇叭];");
			}
		}
		
		i=declarePoint.lastIndexOf("功率");
		if(i>0){
			String aa=declarePoint.substring(i);
			try{
				aa=aa.substring(3,aa.indexOf(";"));
			}catch(Exception ex){
				sb.append("[功率];");
			}
		}
		
		i=declarePoint.lastIndexOf("喇叭口径");
		if(i>0){
			String aa=declarePoint.substring(i);
			try{
				aa=aa.substring(5,aa.indexOf(";"));
			}catch(Exception ex){
				sb.append("[喇叭口径];");
			}
		}
		
		i=declarePoint.lastIndexOf("直流或交流电");
		if(i>0){
			String aa=declarePoint.substring(i);
			try{
				aa=aa.substring(7,aa.indexOf(";"));
			}catch(Exception ex){
				sb.append("[直流或交流电];");
			}
		}
		
		
		i=declarePoint.lastIndexOf("精度");
		if(i>0){
			String aa=declarePoint.substring(i);
			try{
				aa=aa.substring(3,aa.indexOf(";"));
			}catch(Exception ex){
				sb.append("[精度];");
			}
		}
		if(sb.length()>0){
			sb.append("没按规则填写!");
			return sb.toString();
		}
		return "";
	}
	
	
	@RequestMapping(value = { "showHscodes"})
	public String showHscodes(HttpServletRequest request,HttpServletResponse response, Model model) {
		model.addAttribute("proList",psiProductService.find());
		return "modules/psi/psiProductHscodesView";
	}
	
	
	@RequestMapping(value = { "showTranMoney"})
	public String showTranMoney(HttpServletRequest request,HttpServletResponse response, Model model) {
		//三个价格map
		Map<String,Float> seaMap =  ProductPrice.sea;
		Map<String,Float> airMap =  ProductPrice.sky;
		Map<String,Float> expressMap =  ProductPrice.express;
		
		model.addAttribute("seaMap",seaMap);
		model.addAttribute("airMap",airMap);
		model.addAttribute("expressMap",expressMap);
		model.addAttribute("tranMoneyList",psiProductService.find());
		return "modules/psi/psiProductTranMoneyView";
	}
	
	
	@RequestMapping(value = "exportProduct")
	public String exportProduct(String isNewAdd, HttpServletRequest request,HttpServletResponse response, Model model) {
	    List<PsiProduct> productList=psiProductService.findForList(isNewAdd);
	    Map<String,Map<Integer,Map<String,Float>>> proPriceMap=tieredPriceService.getPriceBaseMoqNoColor();
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		List<String> title = Lists.newArrayList("product_name","id", "brand", "model","type","line", "is_new","is_sale","purchaser","purchaseManager","productManager","supplier缩写","supplier name",
				"product_parts","transport_type","L/T","MOQ","platform","description","added_time","create_time","update_time","length","width","height","weight",
				"pack_quantity","box_volume","gw","volume_ratio","pack_length","pack_width","pack_height","product_pack_length","product_pack_width","product_pack_height",
				"tranVolume","tranGw","eu_hscode","ca_hscode","jp_hscode","us_hscode","mx_hscode","hk_hscode","cn_hscode","product_list","first purchase date",
				"euImportDuty","usImportDuty","caImportDuty","jpImportDuty","mxImportDuty","euCustomDuty","usCustomDuty","caCustomDuty","jpCustomDuty","mxCustomDuty","taxRefund","是否带电池","是否带电源","是否带磁");
		if(SecurityUtils.getSubject().isPermitted("psi:product:viewPrice")){
			title.add(11, "price($)");
			title.add(12, "price(￥)");
		}
		  style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		  style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		  style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		  style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		  style.setBottomBorderColor(HSSFColor.BLACK.index);
		  style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		  style.setLeftBorderColor(HSSFColor.BLACK.index);
		  style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		  style.setRightBorderColor(HSSFColor.BLACK.index);
		  style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		  style.setTopBorderColor(HSSFColor.BLACK.index);
		  HSSFFont font = wb.createFont();
		  font.setFontHeightInPoints((short) 16); // 字体高度
		  font.setFontName(" 黑体 "); // 字体
		  font.setBoldweight((short) 16);
		  style.setFont(font);
		  row.setHeight((short) 600);
		  
		  CellStyle contentStyle = wb.createCellStyle();
		  contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		  contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		  contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		  contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		  contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		  contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		  contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		  contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		  contentStyle.setWrapText(true);
		  
		  CellStyle contentStyle1 = wb.createCellStyle();
		  contentStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		  contentStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
		  contentStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		  contentStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
		  contentStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
		  contentStyle1.setRightBorderColor(HSSFColor.BLACK.index);
		  contentStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
		  contentStyle1.setTopBorderColor(HSSFColor.BLACK.index);
		  contentStyle1.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		  contentStyle1.setFillPattern(CellStyle.SOLID_FOREGROUND);
		  contentStyle1.setWrapText(true);
		  HSSFCell cell = null;		
		  for (int i = 0; i < title.size(); i++) {
				cell = row.createCell(i);
				cell.setCellValue(title.get(i));
				cell.setCellStyle(style);
				sheet.autoSizeColumn((short) i);
			}
		  List<Integer> index=new ArrayList<Integer>();
		  Map<String,String> firstPurchaseMap = this.psiProductService.getFirstPurchaseDate();
		  Map<String,String> purchaseManager=psiProductService.getPurchaseByProductType();
		  Map<String,String> productManager = psiProductService.getManagerByProductType();
		  Map<String,String> typeLineMap = groupService.getTypeLine(null);
		 int rowIndex=1;
         if(productList!=null){
     	    //产品定位
     	    Map<String, String> productPositionMap = psiProductEliminateService.findProductPositionByCountry(null);
        	 List<String> isNewColorNameList = psiProductEliminateService.findIsNewProductName();
        	 Map<String,Map<String,Integer>> skyOrSea = psiProductAttributeService.findtransportType();
        	 Map<String, PsiProductEliminate> allAttrMap = psiProductEliminateService.findProductAllAttr();
		    	for (int i=0;i<productList.size();i++) {
		    		PsiProduct product=productList.get(i);
		    		for(String colorName:product.getProductNameWithColor()){
		    			int j=0;
			    		row=sheet.createRow(rowIndex++);
			    		row.setHeight((short) 400);
			    		//if("0".equals(product.getIsSale())){
			    		if("4".equals(productPositionMap.get(colorName))){
			    			index.add(rowIndex-1);
			    		}
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(colorName);
	                    row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getId());
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getBrand());
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getChineseName());
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getType());
			    		String line = typeLineMap.get(product.getType().toLowerCase());
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(line==null?"":line);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(isNewColorNameList.contains(colorName)?"新品":"普通");
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(DictUtils.getDictLabel(productPositionMap.get(colorName), "product_position", ""));
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getCreateUser()==null?"":product.getCreateUser().getName());
			    		
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(StringUtils.isNotEmpty(purchaseManager.get(product.getType()))?purchaseManager.get(product.getType()):"");
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(StringUtils.isNotEmpty(productManager.get(product.getType()))?productManager.get(product.getType()):"");
			    		String nikeName="";
			    		String name="";
			    		String price="";
			    		String rmbPrice="";
			    		String key=colorName;
			    		for (ProductSupplier supplier : product.getPsiSuppliers()) {
			    			Integer supplierId = supplier.getSupplier().getId();
			    			
			    			if( product.getPsiSuppliers().size()>1){
			    				nikeName+=supplier.getSupplier().getNikename()+";";
				    			name+=supplier.getSupplier().getName()+";";
				    			if(SecurityUtils.getSubject().isPermitted("psi:product:viewPrice")&&proPriceMap.get(key)!=null&&proPriceMap.get(key).get(supplierId)!=null){
				    				price+=(proPriceMap.get(key).get(supplierId).get("USD")==null?" ":new java.text.DecimalFormat("#.00").format((double)proPriceMap.get(key).get(supplierId).get("USD")))+";";
				    				rmbPrice+=(proPriceMap.get(key).get(supplierId).get("CNY")==null?" ":new java.text.DecimalFormat("#.00").format((double)proPriceMap.get(key).get(supplierId).get("CNY")))+";";
				    			}
				    		
			    			}else{
			    				nikeName=supplier.getSupplier().getNikename();
				    			name=supplier.getSupplier().getName();
				    			if(SecurityUtils.getSubject().isPermitted("psi:product:viewPrice")&&proPriceMap.get(key)!=null&&(proPriceMap.get(key).get(supplierId)!=null)){
				    				price=(proPriceMap.get(key).get(supplierId).get("USD")==null?"":proPriceMap.get(key).get(supplierId).get("USD")+"");
				    				rmbPrice=(proPriceMap.get(key).get(supplierId).get("CNY")==null?"":proPriceMap.get(key).get(supplierId).get("CNY")+"");
				    			}
			    			}
						}
			    		if(SecurityUtils.getSubject().isPermitted("psi:product:viewPrice")){
				    		if(product.getPsiSuppliers()!=null&&product.getPsiSuppliers().size()>1){
				    			Pattern p = Pattern.compile(".*\\d+.*");
			    				Matcher m1 = p.matcher(price);
			    				Matcher m2 = p.matcher(rmbPrice);
			    			    if (m1.matches()){
			    			    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(price);
			    			    }else{
			    			    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			    }
			    			    if (m2.matches()){
			    			    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(rmbPrice);
			    			    }else{
			    			    	row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    			    }
				    		}else{
				    			if(price==""){
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(price);
				    			}else{
				    				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(Double.parseDouble(price));
				    			}
				    			if(rmbPrice==""){
				    				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(rmbPrice);
				    			}else{
				    				row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(Double.parseDouble(rmbPrice));
				    			}
			    			}
			    		}
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(nikeName);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
			    		
			    		if(product.getTempPartsMap()!=null&&product.getTempPartsMap().size()>0){
			    			StringBuilder content=new StringBuilder();
			    			for (Map.Entry<String, List<PsiParts>> entry: product.getTempPartsMap().entrySet()) {
			    				String color = entry.getKey();
			    				content.append(color).append(":");
								for (PsiParts part: entry.getValue()) {
									content.append(part.getPartsName()).append("  ");
								}
								content.append("\n");
							}
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(content.toString());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		String str = "";
			    		if (skyOrSea.get(colorName)!=null) {
			    			str = skyOrSea.get(colorName).get("total")==1?"海运":skyOrSea.get(colorName).get("total")==2?"空运":"海运/空运";
						}
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(str);
			    		
			    		if(product.getProducePeriod()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getProducePeriod());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(product.getMinOrderPlaced()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getMinOrderPlaced());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getPlatform());
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getDescription());
			    		if (allAttrMap.get(colorName)!=null && StringUtils.isNotEmpty(allAttrMap.get(colorName).getAddedMonth())) {
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(allAttrMap.get(colorName).getAddedMonth());
						}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		/*if(StringUtils.isNotBlank(product.getAddedMonth())){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getAddedMonth().replace(" 00:00:00",""));
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}*/
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(product.getCreateTime()));
			    		if(product.getUpdateTime()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(product.getUpdateTime()));
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		
			    		
			    		if(product.getLength()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getLength().doubleValue());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		
			    		if(product.getWidth()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getWidth().doubleValue());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		
			    		if(product.getHeight()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getHeight().doubleValue());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		
			    		if(product.getWeight()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getWeight().doubleValue());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getPackQuantity());
			    		
			    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getBoxVolume().doubleValue());
			    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getGw().doubleValue());
			    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getVolumeRatio().doubleValue());
			    		
			    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getPackLength().doubleValue());
			    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getPackWidth().doubleValue());
			    		row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getPackHeight().doubleValue());
			    		if(product.getProductPackLength()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getProductPackLength().doubleValue());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(product.getProductPackWidth()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getProductPackWidth().doubleValue());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(product.getProductPackHeight()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getProductPackHeight().doubleValue());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		
			    		if(product.getTranVolume()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getTranVolume());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		
			    		if(product.getTranGw()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getTranGw());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		
			    		//	"eu_hscode","ca_hscode","jp_hscode","us_hscode","hk_hscode");
			    		if(StringUtils.isNotBlank(product.getEuHscode())){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getEuHscode());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(StringUtils.isNotBlank(product.getCaHscode())){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getCaHscode());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(StringUtils.isNotBlank(product.getJpHscode())){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getJpHscode());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(StringUtils.isNotBlank(product.getUsHscode())){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getUsHscode());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(StringUtils.isNotBlank(product.getMxHscode())){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getMxHscode());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(StringUtils.isNotBlank(product.getHkHscode())){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getHkHscode());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(StringUtils.isNotBlank(product.getCnHscode())){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getCnHscode());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(StringUtils.isNotBlank(product.getProductList())){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(product.getProductList());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		
			    		//获取该产品第一次下单时间
			    		if(firstPurchaseMap.get(product.getId().toString())!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(firstPurchaseMap.get(product.getId().toString()));
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		//"euImportDuty","usImportDuty","caImportDuty","jpImportDuty","euCustomDuty","usCustomDuty","caCustomDuty","jpCustomDuty"
			    		if(product.getEuImportDuty()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getEuImportDuty());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(product.getUsImportDuty()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getUsImportDuty());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(product.getCaImportDuty()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getCaImportDuty());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(product.getJpImportDuty()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getJpImportDuty());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(product.getMxImportDuty()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getMxImportDuty());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(product.getEuCustomDuty()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getEuCustomDuty());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(product.getUsCustomDuty()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getUsCustomDuty());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(product.getCaCustomDuty()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getCaCustomDuty());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(product.getJpCustomDuty()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getJpCustomDuty());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(product.getMxCustomDuty()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getMxCustomDuty());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		if(product.getTaxRefund()!=null){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(product.getTaxRefund());
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			    		}
			    		
			    		//"是否带电池","是否带电源","是否带磁"
			    		if(product.getHasElectric()!=null&&"1".equals(product.getHasElectric())){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("是");
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("否");
			    		}
			    		
			    		
			    		if(product.getHasPower()!=null&&"0".equals(product.getHasPower())){
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("否");
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("是");
			    		}
			    		
			    		
			    		
			    		if(product.getHasMagnetic()!=null&&"1".equals(product.getHasMagnetic())){
			    			row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue("是");
			    		}else{
			    			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("否");
			    		}
		    		}
		    		
				}
		    	
		    	for (int i=0;i<rowIndex-1;i++) {
		        	 for (int j = 0; j < title.size(); j++) {
		        		 if(index.contains(i+1)){
		        			 sheet.getRow(i+1).getCell(j).setCellStyle(contentStyle1);
		        		 }else{
		        			 sheet.getRow(i+1).getCell(j).setCellStyle(contentStyle);
		        		 }
			        	 
					 }
		         }
		    	
		     }
         
	          for (int i = 0; i < title.size(); i++) {
	        		 sheet.autoSizeColumn((short)i);
			  }
	          
			try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
		
				SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		
				String fileName = "product" + sdf.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "view")
	public String view(PsiProduct product, Model model) {
		model.addAttribute("product", product);
		model.addAttribute("transportTypeMap", psiProductAttributeService.findProductTransportType());
		PsiProductMoldFee moldFee = psiProductMoldFeeService.findMoldFeeByModel(product.getName());
		model.addAttribute("moldFee", moldFee);
		return "modules/psi/psiProductView";
	}
	
	@RequiresPermissions("psi:product:review")
	@RequestMapping(value = "review")
	public String review(PsiProduct product, Model model) {
		model.addAttribute("product", product);
		model.addAttribute("transportTypeMap", psiProductAttributeService.findProductTransportType());
		return "modules/psi/psiProductReview";
	}
	
	@RequiresPermissions("psi:product:review")
	@RequestMapping(value = "reviewSave")
	public String reviewSave(PsiProduct product, Model model,RedirectAttributes redirectAttributes) {
		product=this.psiProductService.get(product.getId());
		String title=("1".equals(product.getComponents())?"配件":"")+"产品["+product.getModel()+"]审核通过！";
		this.psiProductService.reviewSave(product);
		sendEmail(product.getCreateUser().getEmail()+","+UserUtils.getUser().getEmail(), "Hi,All<br/>"+title+",请知悉！", title);
		addMessage(redirectAttributes, title);
		return "redirect:" + Global.getAdminPath() + "/psi/product/list";
	}
	
	@ResponseBody
	@RequestMapping(value = "genNewPlan")
	public String genNewPlan(PsiProduct product, Model model,RedirectAttributes redirectAttributes) {
		product=this.psiProductService.get(product.getId());
		this.psiProductService.genPlan(product);     
		return "success,,"+product.getName();
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "viewHscode")
	public String viewHscode(PsiProduct product, Model model) {
		model.addAttribute("product", product);
		return "modules/psi/psiProductHscodeView";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value={"update","add"})
	public String addOrUpdate(PsiProduct product, Model model) {
		model.addAttribute("suppliers",psiSupplierService.findAll());
		model.addAttribute("products", psiProductService.findAllOnSale());
		model.addAttribute("product", product);
		boolean canEdit = true;
		if(product.getId()!=null){
			for (ProductSupplier supplier : product.getPsiSuppliers()) {
				canEdit = canEdit&&purchaseOrderService.canEditPackingQuantity(supplier.getSupplier().getId(), product.getId());
			}
			//装箱数、大箱体积、大箱重量 组成个字符串
			String packVolumeWeight = product.getPackQuantity()+","+product.getBoxVolume()+","+product.getGw();
			model.addAttribute("packVolumeWeight", packVolumeWeight);
		}
		List<PsiParts> partsAll= this.partsService.findAllParts();
		List<User> users =Lists.newArrayList();
		for(User user:systemService.findAllUsers()){
			if("0".equals(user.getDelFlag())){
				users.add(user);
			}
		}
		model.addAttribute("users", users);
		model.addAttribute("partsAll", partsAll);
		model.addAttribute("canEdit", canEdit);
		return "modules/psi/psiProductForm";
	}
	

	@ResponseBody
	@RequestMapping(value = "deleteFilePath")
	public String deleteFilePath(Integer productId,String deletePath){
		PsiProduct product = psiProductService.get(productId);
		String filePath=product.getFilePath();
		StringBuilder newPath= new StringBuilder();
		if(StringUtils.isNotBlank(filePath)){
			String[] path=filePath.split(",");
			for (int i=0;i<path.length;i++) {
				if(!deletePath.equals(path[i])){
					if(i==path.length-1){
						newPath.append(path[i]);
					}else{
						newPath.append(path[i]).append(",");
					}
				}
			}
		}
		psiProductService.deleteFilePath(newPath.toString(),productId);
		return "1";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "save")
	public String save(@RequestParam(required=false,value="supplier")String[] suppliers,@RequestParam(required= false)MultipartFile imagePeview,@RequestParam(required= false)MultipartFile uploadFile,PsiProduct product,
			String packVolumeWeight,Model model, RedirectAttributes redirectAttributes) {
		if (imagePeview!=null&&imagePeview.getSize() != 0) {
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir()+"psiproduct";
			File baseDir = new File(baseDirStr);
			if (!baseDir.isDirectory())
				baseDir.mkdirs();
			String name = imagePeview.getOriginalFilename();
			String suffix = name.substring(name.lastIndexOf("."));
			name = product.getName() + suffix;
			try {
				File imageFile = new File(baseDir, name);
				FileUtils.copyInputStreamToFile(imagePeview.getInputStream(),imageFile);
				logger.info("图片保存成功:" + imageFile.getAbsolutePath());
				product.setImage(Global.getCkBaseDir()+"psiproduct/"+ name);
				
				
				File dir = new File(baseDirStr+"/compressPic");
				if (!dir.isDirectory())
					dir.mkdirs();
				Thumbnails.of(baseDirStr+"/"+name).size(120, 120).keepAspectRatio(true)
							.toFile(baseDirStr+"/compressPic/"+name);
			
				Thumbnails.of(baseDirStr+"/compressPic/"+name).scale(1f).outputQuality(0.25f).toFile(baseDirStr+"/compressPic/"+name); 
				
	    	  
			} catch (IOException e) {
				logger.warn(name + "文件保存失败", e);
			}
		}
		if(uploadFile!=null&&uploadFile.getSize() != 0){
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir()+"/psiproduct/"+product.getName();
			File baseDir = new File(baseDirStr);
			if (!baseDir.isDirectory())
				baseDir.mkdirs();
			String name = uploadFile.getOriginalFilename();
			String suffix = name.substring(name.lastIndexOf("."));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			name = sdf.format(new Date())+ suffix;
			File dest = new File(baseDir,name);
			try {
				FileUtils.copyInputStreamToFile(uploadFile.getInputStream(),dest);
				product.setFilePathAppend(name);
				logger.info("文件上传成功");
			} catch (IOException e) {
				logger.warn("文件保存失败");
			}
			
		}
		User user = UserUtils.getUser();
		Date date = new Date();
		boolean flag = (product.getId() ==null);
		if (!flag) {
			product.setUpdateUser(user);
			product.setUpdateTime(new Date());
			
		} else {
			product.setDelFlag("0"); // 删除标志
			product.setIsSale("1"); // 是否在售默认否
			product.setIsMain("0"); // 是否主力默认否
			product.setIsNew("1"); // 是否新品默认是
			product.setCreateUser1(user);
			//提醒去审核
			String title="产品["+product.getModel()+"]已创建待审核！";
			String aa="<a href='" + BaseService.BASE_WEBPATH+ Global.getAdminPath()+ "/psi/product/review?id="+ product.getId() + ">点此处审核</a>";
			
			List<User> userList = systemService.findUserByRealPermission("psi:product:review");
			List<User> replys = Lists.newArrayList();
			if (userList != null) {
				replys.addAll(userList);
			}
			String toAddress = Collections3.extractToString(replys, "email", ",");
			
			if(StringUtils.isNotEmpty(toAddress)){
				sendEmail(toAddress, "Hi,All<br/>"+title+aa, title);
			}
		}
		DecimalFormat df = new DecimalFormat("0.00");
		String curVolume = df.format(product.getPackHeight().floatValue()*product.getPackLength().floatValue()*product.getPackWidth().floatValue()/1000000);
		product.setBoxVolume(new BigDecimal(product.getPackHeight().floatValue()*product.getPackLength().floatValue()*product.getPackWidth().floatValue()/1000000));
		product.setVolumeRatio(new BigDecimal(product.getGw().floatValue()/product.getBoxVolume().floatValue()));
		product.setCreateTime(date);
		
		Integer oldPackQuantity =1;
		try{
			oldPackQuantity=Integer.parseInt(packVolumeWeight.split(",")[0]);
		}catch(Exception ex){}
				
		psiProductService.save(product,suppliers,oldPackQuantity);
		if(!flag){
			psiProductService.delSkus();
		}
		
		//如果产品装箱数、体积、重量变了，就更新运单表信息
		/*if(product.getId()!=null&&!packVolumeWeight.equals(product.getPackQuantity()+","+curVolume+","+product.getGw())){
			this.updateTransportOrder(product);
		}
		*/
		
		if(flag){
			Integer pId=psiProductService.getId(product.getName());
			product.setId(pId);
			sendNewProductEmail(product);
		}
		addMessage(redirectAttributes, "产品保存成功！");
		return "redirect:" + Global.getAdminPath() + "/psi/product/list";
	}
	
	
	//更新运单信息
	public void updateTransportOrder(PsiProduct product){
		//更新春雨
		//查询未出库、并且含有某产品id的  
		Integer curProductId=product.getId();
		List<PsiTransportOrder> list = this.transportService.findByProductId(curProductId);
		DecimalFormat df = new DecimalFormat("0.00");
		if(list!=null&&list.size()>0){
			for(PsiTransportOrder order:list){
				Float volume = 0f;
				Float weight = 0f;
				Float totalBoxNumber =0f;
				for(PsiTransportOrderItem item:order.getItems()){
					
					PsiProduct itemProduct = item.getProduct();
					Integer productId = itemProduct.getId();
					Integer packQuantity = item.getPackQuantity();
					//如果id为：217,218,不用及时装箱数        装箱数分国家
					if((!curProductId.equals(217)&&!curProductId.equals(218))&&curProductId.equals(productId)){
						item.setPackQuantity(product.getPackQuantity());//更新装箱数
						packQuantity=product.getPackQuantity();
					}
					
					float boxNumber  =	item.getQuantity()/(float)packQuantity;
					volume+=boxNumber*itemProduct.getBoxVolume().floatValue();
					weight+=boxNumber*itemProduct.getGw().floatValue();
					totalBoxNumber+=boxNumber;
				}    
				totalBoxNumber+=0.5f;
				order.setVolume(Float.parseFloat(df.format(volume)));//更新体积
				order.setWeight(Float.parseFloat(df.format(weight)));//更新重量
				order.setBoxNumber(totalBoxNumber.intValue());//更新总箱数
				this.transportService.save(order);
			}
		}
		
		
		//更新理诚
		//查询未出库、并且含有某产品id的  
		List<LcPsiTransportOrder> lcList = this.lcTransportService.findByProductId(curProductId);
		if(lcList!=null&&lcList.size()>0){
			for(LcPsiTransportOrder order:lcList){
				BigDecimal volume = new BigDecimal(0);
				BigDecimal weight = new BigDecimal(0);
				Float totalBoxNumber =0f;
				for(LcPsiTransportOrderItem item:order.getItems()){
					PsiProduct itemProduct = item.getProduct();
					Integer productId = itemProduct.getId();
					Integer packQuantity = item.getPackQuantity();
					//如果id为：217,218,不用及时装箱数        装箱数分国家
					if((!curProductId.equals(217)&&!curProductId.equals(218))&&curProductId.equals(productId)){
						item.setPackQuantity(product.getPackQuantity());//更新装箱数
						packQuantity=product.getPackQuantity();
					}
					
					float boxNumber  =	item.getQuantity()/(float)packQuantity;
					volume=volume.add(new BigDecimal(boxNumber).multiply(itemProduct.getBoxVolume()));
					weight=weight.add(new BigDecimal(boxNumber).multiply(itemProduct.getGw()));
					totalBoxNumber+=boxNumber;
				}    
				totalBoxNumber+=0.5f;
				order.setVolume(volume.floatValue());//更新体积
				order.setWeight(weight.floatValue());//更新重量
				order.setBoxNumber(totalBoxNumber.intValue());//更新总箱数
				this.lcTransportService.save(order);
			}
		}
	}

	private  boolean sendNewProductEmail(PsiProduct product){
		String toAddress ="supply-chain@inateck.com,"+UserUtils.logistics1;
		//String toAddress ="eileen@inateck.com";
		//产品型号、颜色、MOQ、交期、ERP系统链接。
		StringBuffer content= new StringBuffer("");
		if(product!=null){
			content.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '><tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '>");
			content.append("<th>产品名称</th><th>类型</th><th>颜色</th><th>MOQ</th><th>交期</th></tr>");
			content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
			content.append("<td><a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/product/view?id="+product.getId()+"'>"+product.getName()+"</a></td><td>"+product.getType()+"</td><td>"+product.getColor()+"</td>");
			content.append("<td>"+(product.getMinOrderPlaced()==null?"":product.getMinOrderPlaced())+"</td><td>"+(product.getProducePeriod()==null?"":product.getProducePeriod())+"</td></tr>");
			content.append("</table>");
		}
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();   
			final MailInfo mailInfo = new MailInfo(toAddress,"新增产品"+product.getName()+DateUtils.getDate("-yyyy/MM/dd"),date);
			mailInfo.setContent(content.toString());
			mailInfo.setCcToAddress("eileen@inateck.com");
			new Thread(){
				public void run(){   
					 mailManager.send(mailInfo);
				}
			}.start();
		}
		return true;
	}
	
	
	@ResponseBody
	@RequestMapping(value = "updateChineseName")
	public String updateChineseName(String chineseName,Integer productId) {
		try {
			chineseName=URLDecoder.decode(chineseName,"utf-8");
			psiProductService.updateChineseName(chineseName, productId);
			return "1";
		} catch (UnsupportedEncodingException e) {
			return "0";
		}
		
	}
	
	@ResponseBody
	@RequestMapping(value = "updateHscode")
	public String updateHscode(String euHscode,String usHscode,String jpHscode,String mxHscode,String caHscode,String hkHscode,String cnHscode,String euImportDuty,String usImportDuty,String caImportDuty,String jpImportDuty,String mxImportDuty,String euCustomDuty,String usCustomDuty,String caCustomDuty,String jpCustomDuty,String mxCustomDuty,Integer productId,Integer taxRefund) {
		psiProductService.updateHscode(euHscode, usHscode, jpHscode, caHscode,hkHscode,cnHscode,euImportDuty,usImportDuty,caImportDuty,jpImportDuty,euCustomDuty,usCustomDuty,caCustomDuty,jpCustomDuty, productId,mxHscode,mxImportDuty,mxCustomDuty);
		psiProductService.updateTaxRefund(taxRefund, productId);
		return "1";
	}
	
	@ResponseBody
	@RequestMapping(value = "getHistoryHscodeById")
	public List<PsiProductHsCodeDetail> getHistoryHscodeById(Integer productId) {
		return psiProductService.getHistoryHscodeById(productId);
	}
	
	
	@ResponseBody
	@RequestMapping(value = "addedMonth")
	public String addedMonth(Integer pid,String addedMonth) {
		if(StringUtils.isNotEmpty(addedMonth)){
			addedMonth =  DateUtils.getDate(new Date(addedMonth),"yyyy-MM-dd");
		}
		psiProductService.updateProductAddedMontn(pid,addedMonth);
		return "1";
	}
	
	@RequestMapping(value = "listDict")
	public String listDict(Dict dict, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<Dict> page = new Page<Dict>(request, response);
		page.setPageSize(Integer.MAX_VALUE);
		if (dict.getType() == null) {
			// 默认查询产品分类
			dict.setType("product_type");
		}
		page = dictService.find(page, dict);
		model.addAttribute("page", page);
		return "modules/psi/psiProductInfoPro";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "addDict")
	public String addDict(Dict dict,@RequestParam(required=false)String dictId, Model model) {
		if(StringUtils.isNotEmpty(dictId)){
			dict = dictService.get(dictId);
		}
		model.addAttribute(dict);
		return "modules/psi/psiProductInfoProAdd";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "saveDict")
	@ResponseBody
	public String saveDict(Dict dict,@RequestParam(required=false)String dictId) {
		if(StringUtils.isNotEmpty(dictId)){
			Dict temp = dictService.get(dictId);
			temp.setValue(dict.getValue());
			temp.setLabel(dict.getLabel());
			dictService.save(temp);
			return temp.getId();
		}else{
			dict.setLabel(dict.getLabel()); 
			dict.setDescription(dict.getType());
			dict.setSort(30);
			dictService.save(dict);
		}
		return dict.getId();
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "isExist")
	@ResponseBody
	public String isExist(Dict dict,String dicId) {
		return (!dictService.isExist(dict,dicId))+"";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "isExistName")
	@ResponseBody
	public String isExistName(PsiProduct product) {
		if(StringUtils.isBlank(product.getBrand())||StringUtils.isBlank(product.getModel())||product.getId()!=null){
			return "true";
		}
		return (!psiProductService.isExistName(product))+"";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "updateProductState")
	@ResponseBody
	public String updateProductState(PsiProduct product) {
		psiProductService.updateProductState(product);
		return product.getName()+"状态修改成功！";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = { "barcodeslist"})
	public String barcodeslist(PsiProduct psiProduct, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<PsiProduct> page = new Page<PsiProduct>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("id desc");
		}else{
			page.setOrderBy(orderBy+",id desc");
		}	
		psiProductService.find(page,psiProduct);
		page.setOrderBy(orderBy);
		
		Map<String,Map<String,String>> vendorMap=vendorShipmentService.findBarcode();
	
		model.addAttribute("page", page);
		model.addAttribute("vendorMap", vendorMap);
		return "modules/psi/psiBarcodeList";
	}
	
	@RequestMapping(value = { "oldBarcodeslist"})
	public String oldBarcodeslist(PsiProduct psiProduct, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<PsiProduct> page = new Page<PsiProduct>(request, response);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("id desc");
		}else{
			page.setOrderBy(orderBy+",id desc");
		}	
		psiProductService.find(page,psiProduct);
		page.setOrderBy(orderBy);
		model.addAttribute("page", page);
		return "modules/psi/psiOldBarcodeList";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = { "expBarcode"})
	public String expBarcode(PsiProduct psiProduct, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException {
		Page<PsiProduct> page = new Page<PsiProduct>(request, response);
		page.setPageSize(60000);
		List<PsiProduct> products=psiProductService.find(page,psiProduct).getList();
		if(products==null){
			return null;
		}
		if(products.size()>65535){
		    throw new RuntimeException("一次导出的最大数据不能超过65535条！请分两次导出！");
		}
	    int excelNo=1;
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		sheet.setDefaultColumnWidth(18);
		//sheet.setColumnWidth(columnIndex, width)
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		String[] title = { " product "," supplier ", " DE "," UK "," FR "," IT "," ES "," JP "," US "," CA "," MX "};
	    style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
	    style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
	    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	  //设置Excel中的边框(表头的边框)
	    HSSFFont font = wb.createFont();
	    font.setFontHeightInPoints((short) 11); // 字体高度
	    style.setFont(font);
	    row.setHeight((short) 400);
	    HSSFCell cell = null;		
	    for(int i = 0; i < title.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
		}
			  
	    for(PsiProduct pro:products){
	    	Map<String,Map<String,PsiBarcode>> barMap =pro.getBarcodeMap();
	    	for(Map.Entry<String,Map<String,PsiBarcode>> entry:barMap.entrySet()){
	    		String colorKey=entry.getKey();
	    		Map<String,PsiBarcode> coutryMap = barMap.get(colorKey);
	    		 row = sheet.createRow(excelNo++);  //生成行
	    		 int i =0;
	    		 StringBuilder supplierName =new StringBuilder();
	    		 for(ProductSupplier psu:pro.getPsiSuppliers()){
	    			 supplierName.append(psu.getSupplier().getNikename()).append(",");
	    		 }
				 
				 String modelStr =pro.getBrand()+" "+pro.getModel();
				 if(StringUtils.isNotEmpty(colorKey)){
					 modelStr=modelStr+"_"+colorKey;
				 }
				 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(modelStr);  
				 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(supplierName.substring(0, supplierName.length()-1));
				 
				if(coutryMap.get("de")!=null){
					 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(coutryMap.get("de").getBarcode());  
				}else{
					 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");  
				}
				if(coutryMap.get("uk")!=null){
					 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(coutryMap.get("uk").getBarcode());  
				}else{
					 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");  
				}
				if(coutryMap.get("fr")!=null){
					 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(coutryMap.get("fr").getBarcode());  
				}else{
					 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");  
				}
				if(coutryMap.get("it")!=null){
					 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(coutryMap.get("it").getBarcode());  
				}else{
					 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");  
				}
				if(coutryMap.get("es")!=null){
					 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(coutryMap.get("es").getBarcode());  
				}else{
					 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");  
				}
				if(coutryMap.get("jp")!=null){
					 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(coutryMap.get("jp").getBarcode());  
				}else{
					 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");  
				}
				if(coutryMap.get("com")!=null){
					 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(coutryMap.get("com").getBarcode());  
				}else{
					 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");  
				}
				if(coutryMap.get("ca")!=null){
					 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(coutryMap.get("ca").getBarcode());  
				}else{
					 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");  
				}
				if(coutryMap.get("mx")!=null){
					 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(coutryMap.get("mx").getBarcode());  
				}else{
					 row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue("");  
				}
	    	}
	    }
	
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");

		String fileName = "Barcodes" + sdf.format(new Date()) + ".xls";
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
	@RequestMapping(value = {"skulist"})
	public String skulist(PsiProduct psiProduct, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<PsiProduct> page = new Page<PsiProduct>(request, response,20);
		String orderBy = page.getOrderBy();
		/*if("".equals(orderBy)){
			page.setOrderBy("isSale desc");
		}else{
			page.setOrderBy(orderBy+",isSale desc");
		}	*/
		Map<String,List<String>> accountMap=amazonAccountConfigService.findAccountByCountryNoServer();
		model.addAttribute("accountMap",accountMap);
		if(StringUtils.isEmpty(psiProduct.getPlatform())){
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					psiProduct.setPlatform(dict.getValue());
					if(accountMap!=null&&accountMap.get(dict.getValue())!=null&&accountMap.get(dict.getValue()).size()>0){
						psiProduct.setBrand(accountMap.get(dict.getValue()).get(0));
					}
					break;
				}
			}
		}
		if(StringUtils.isEmpty(psiProduct.getPlatform())){
			psiProduct.setPlatform("ebay");
		}
		psiProductService.findWithSku(page, psiProduct);
		page.setOrderBy(orderBy);
		model.addAttribute("page", page);
		List<Object>skus = null;
		if("ebay".equals(psiProduct.getPlatform())){
			skus = ebayOrderService.getAllEbaySkus("de");
		}else if("ebay_com".equals(psiProduct.getPlatform())){
			skus = ebayOrderService.getAllEbaySkus("com");
		}else{
			skus=amazonProduct2Service.findSku(psiProduct.getPlatform());
		}
		Map<String,String> fnskuMap =this.amazonProduct2Service.getSkuAndFnskuMap();
		model.addAttribute("fnskuMap",fnskuMap);
		model.addAttribute("skus",skus);
		
		return "modules/psi/psiSkuList";
	}
	
	
	@RequestMapping(value = {"skulistExport"})
	public String skulistExport(PsiProduct psiProduct, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<PsiProduct> page = new Page<PsiProduct>(request, response,20);
		page.setPageNo(1);
 		page.setPageSize(100000);
		String orderBy = page.getOrderBy();
		if("".equals(orderBy)){
			page.setOrderBy("isSale desc");
		}else{
			page.setOrderBy(orderBy+",isSale desc");
		}	
		if(StringUtils.isEmpty(psiProduct.getPlatform())){
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					psiProduct.setPlatform(dict.getValue());
					break;
				}
			}
		}
		psiProductService.findWithSkuAllCountry(page, psiProduct);
		page.setOrderBy(orderBy);
		Set<String> countrySet=Sets.newHashSet();
		List<PsiProduct> productList=page.getList();
		Map<String, Map<String, PsiBarcode>> barcodeMap=Maps.newLinkedHashMap();
		for (PsiProduct product : productList) {
			List<PsiBarcode> barcodesList=product.getBarcodes();
			for (PsiBarcode psiBarcode : barcodesList) {
				countrySet.add(psiBarcode.getProductPlatform());
				String key = "";
				if(!StringUtils.isBlank(psiBarcode.getProductColor())){
					key=psiBarcode.getProductName()+"_"+psiBarcode.getProductColor();
				}else{
					key=psiBarcode.getProductName();
				}
				
				Map<String, PsiBarcode> tempMap = barcodeMap.get(key);
				if(tempMap==null){
					tempMap = Maps.newLinkedHashMap();
					barcodeMap.put(key, tempMap);
				}
				tempMap.put(psiBarcode.getProductPlatform(), psiBarcode);
			}
		}
		List<String> countryList = Lists.newArrayList(countrySet);
		List<String> list = Lists.newArrayList(barcodeMap.keySet());
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) 600);
	    HSSFCell cell = null;		
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
	    style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
	    HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
		
		CellStyle contentStyle1 = wb.createCellStyle();
		contentStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
		HSSFFont font1 = wb.createFont();
		font1.setColor(HSSFFont.COLOR_RED);  
		font1.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);  
		contentStyle1.setFont(font1);
	    countryList.add(0, "名称");
	    
		  for (int i = 0; i < countryList.size(); i++) {
				cell = row.createCell(i);
				if(i==0){
					cell.setCellValue("名称");
				}else{
				    cell.setCellValue(DictUtils.getDictLabel(countryList.get(i),"platform",""));
				}
				sheet.autoSizeColumn((short) i);
				cell.setCellStyle(style);
		  }
		  
		
		  int startRow=1;
		  for(int i=1;i<=list.size();i++){
			  int maxSku=0;
			  for (Map.Entry<String, PsiBarcode> entry : barcodeMap.get(list.get(i-1)).entrySet()) {
				  if(entry.getValue().getSkus().size()> maxSku){
					  maxSku=entry.getValue().getSkus().size();
				  }
			  }
			  int count=0;
			  for(int k=startRow;k<startRow+maxSku;k++){
				  row = sheet.createRow(k);
				  if(count==0){
					  row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue(list.get(i-1));
					  row.getCell(0).setCellStyle(contentStyle); 
				  }else{
					  row.createCell(0,Cell.CELL_TYPE_STRING).setCellValue("");
					  row.getCell(0).setCellStyle(contentStyle); 
				  }
				  
				  for (int j = 1; j <countryList.size(); j++) {
					  PsiBarcode barcode= barcodeMap.get(list.get(i-1)).get(countryList.get(j));
					  if(barcode!=null&&barcode.getSkus()!=null&&barcode.getSkus().size()>count&&barcode.getSkus().get(count)!=null){
						  row.createCell(j,Cell.CELL_TYPE_STRING).setCellValue(barcode.getSkus().get(count).getSku()); 
					     // row.createCell(j+1,Cell.CELL_TYPE_STRING).setCellValue("1".equals(barcode.getSkus().get(count).getUseBarcode())?"是":""); 
						  if("1".equals(barcode.getSkus().get(count).getUseBarcode())){
							  row.getCell(j).setCellStyle(contentStyle1);
						  }else{
							  row.getCell(j).setCellStyle(contentStyle);
						  }
					      
					  }else{
						  row.createCell(j,Cell.CELL_TYPE_STRING).setCellValue(""); 
					      row.getCell(j).setCellStyle(contentStyle);
					  }
				  }
				  count++;
			  }
			  if(maxSku>0){
				  sheet.addMergedRegion(new CellRangeAddress(startRow, startRow+maxSku-1, 0, 0));
			  }
			  startRow+=maxSku;
		  }
		  sheet.setColumnWidth(0, 7000);
		  for (int i = 1; i < countryList.size(); i++) {
				sheet.setColumnWidth(i, 7000);
			
		  }
		  try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				SimpleDateFormat sdf1 = new SimpleDateFormat("MMddhhmm");
				String fileName = "sku" + sdf1.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"skuMate"})
	public String skuMate(PsiProduct psiProduct, HttpServletRequest request,HttpServletResponse response,RedirectAttributes redirectAttributes, Model model) {
		Map<String,List<String>> accountMap=amazonAccountConfigService.findAccountByCountry();
		model.addAttribute("accountMap",accountMap);
		if(StringUtils.isEmpty(psiProduct.getPlatform())){
			psiProduct.setPlatform("de");
			if(accountMap!=null&&accountMap.get("de")!=null&&accountMap.get("de").size()>0){
				psiProduct.setBrand(accountMap.get("de").get(0));
			}
		}
//		this.amaMateOther();
//		this.ebayMateOther();
		//获取所有未匹配的sku
		Set<String>  amaSkus =amazonProduct2Service.findUnMateSku(psiProduct.getPlatform(),psiProduct.getBrand());
		
		Set<String> amaSkus1 = new TreeSet<String>(new Comparator<String>() {
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			};
		});
		amaSkus1.addAll(amaSkus);
		model.addAttribute("skus",amaSkus1);
		return "modules/psi/psiSkuMateList";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"goSkuMate"})
	public String goSkuMate(String country,String sku, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException {
		model.addAttribute("country",country);
		if("ebay".equals(country)){
			country="de";
		}else if("ebay_com".equals(country)){
			country="com";
		}
		List<Object>  products =amazonProduct2Service.findAllProductsFromBarcode(country);
		model.addAttribute("products",products);
		model.addAttribute("sku",URLDecoder.decode(sku,"utf-8"));
		
		return "modules/psi/psiSkuMate";
	}
	
	//反向匹配
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = {"skuMateSave"})
	public String skuMateSave(String sku,String country,String account,String productCon,boolean isCheck, HttpServletRequest request,HttpServletResponse response,RedirectAttributes redirectAttributes, Model model) throws UnsupportedEncodingException {
		sku =URLDecoder.decode(URLDecoder.decode(sku,"utf-8"),"utf-8");
		PsiSku psisku = new PsiSku();
		String[] proCon = productCon.split("_");
		String productId=proCon[0];
		String color ="";
		String ebayCountry=country;
		if(proCon.length>1){
			color=proCon[1];
		}
		if("ebay".equals(country)){
			ebayCountry="de";
		}else if("ebay_com".equals(country)){
			ebayCountry="com";
		}
		PsiBarcode barcode =  psiProductService.getBarcodeByProCouCol(Integer.parseInt(productId),ebayCountry,color,account);
		String productName = barcode.getProductName();
		String countryCode="";
		if("com".equals(country)){
			countryCode="us";
		}
		//如果选择了fnsku
		if(isCheck){
			String oldSku ="";
			String rs = psiProductService.validateFnSku(sku); 
			if(rs.length()>0){
				return rs;
			}
			psiProductService.updateUseBarcode(country,Integer.parseInt(productId),color);
			psisku.setUseBarcode("1");
			//把原来的fnsku都设置成没绑定
			for (PsiSku skus : barcode.getSkus()) {
				if(skus.getUseBarcode().equals("1")){
					oldSku=skus.getSku();
				}
				skus.setUseBarcode("0");
			}
			String fnsku = amazonProduct2Service.findFnsku(ebayCountry, sku);
			if(fnsku==null){
				barcode.setBarcodeType(null);
				barcode.setBarcode(null);
			}else{
				barcode.setBarcodeType(fnsku.split(":")[0]);
				barcode.setBarcode(fnsku.split(":")[1]);
			}
			
			barcode.setLastUpdateBy(UserUtils.getUser());
			barcode.setLastUpdateTime(new Date());
			
			if(!oldSku.equals("")){
				//查询是否有未出货订单
				findUnReceivedDoneOrder(Integer.parseInt(productId), ebayCountry, color, productName, oldSku, sku);
			}
		}else{
			psisku.setUseBarcode("0");
		}
		psisku.setUpdateUser(UserUtils.getUser());
		psisku.setBarcode(barcode);
		psisku.setColor(color);
		psisku.setCountry(country);
		psisku.setSku(sku);
		psisku.setAccountName(account);
		if(!"ebay".equals(country)&&!"ebay_com".equals(country)){
			psisku.setAsin(amazonProduct2Service.findAsin(country, sku));
			psiProductService.setItemProductInfo(sku, productName, color);
		}else{
			String nameWithColor=productName+(StringUtils.isNotBlank(color)?("_"+color):"");
			psiProductService.updateEbayItem(sku, nameWithColor);
		}
		psisku.setProductName(productName);
		psisku.setProductId(Integer.parseInt(productId));
		psiProductService.save(psisku);
		psiProductService.save(barcode);
		
		addMessage(redirectAttributes, "sku:"+sku+"匹配产品："+productName+":::"+countryCode+":::"+color+"成功！");
		return "redirect:"+Global.getAdminPath()+"/psi/product/skuMate?platform="+country;
	}
	
	@ResponseBody
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value={"updateSku"})
	public String updateSku(String sku,Integer skuId,Integer barcodeId,String country,String accountName) throws UnsupportedEncodingException {
		sku =URLDecoder.decode(URLDecoder.decode(sku,"utf-8"),"utf-8");
		PsiBarcode barcode =  psiProductService.getBarcodeById(barcodeId);
		PsiSku psisku  = null;
		if(skuId!=null){
			psisku = psiProductService.getSkuById(skuId);
			psisku.setSku(sku);
			psisku.setAsin(amazonProduct2Service.findAsin(country, sku));
		}else{
			psisku = new PsiSku();
			psisku.setBarcode(barcode);
			psisku.setColor(barcode.getProductColor());
			psisku.setCountry(country);
			psisku.setSku(sku);
			psisku.setAsin(amazonProduct2Service.findAsin(country, sku));
			psisku.setProductName(barcode.getProductName());
			psisku.setUseBarcode("0");
			psisku.setAccountName(accountName);
			psisku.setProductId(barcode.getPsiProduct().getId());
		}
		psisku.setUpdateUser(UserUtils.getUser());
		if(!"ebay".equals(country)){
			psiProductService.setItemProductInfo(sku, psisku.getProductName(), psisku.getColor());
		}else{
			String nameWithColor=psisku.getProductName()+(StringUtils.isNotBlank(psisku.getColor())?("_"+psisku.getColor()):"");
			psiProductService.updateEbayItem(sku, nameWithColor);
		}
	
		psiProductService.save(psisku);
		return psisku.getId()+"";
	}
	
	@ResponseBody
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value={"deleteSku"})
	public String deleteSku(Integer skuId,String sku) throws UnsupportedEncodingException {
		sku =URLDecoder.decode(URLDecoder.decode(sku,"utf-8"),"utf-8");
		PsiSku sku1  = psiProductService.getSkuById(skuId);
		if("1".equals(sku1.getUseBarcode())){
			PsiBarcode psiBarcode = sku1.getBarcode();
			psiBarcode.setBarcodeType(null);
			psiBarcode.setBarcode(null);
			psiBarcode.setLastUpdateBy(UserUtils.getUser());
			psiBarcode.setLastUpdateTime(new Date());
			psiProductService.save(psiBarcode);
			findUnReceivedDoneOrder(sku1.getProductId(), sku1.getCountry(), sku1.getColor(), sku1.getProductName(), sku, null);
		}
		sku1.setDelFlag("1");
		sku1.setUpdateUser(UserUtils.getUser());
		//psiProductService.deleteSku(skuId);
		psiProductService.clearItemProductInfo(sku);
		return "1";
	}
	
	@ResponseBody
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value={"updateBarcode"})
	public String updateBarcode(Integer skuId,boolean checked) {
		PsiSku sku  = psiProductService.getSkuById(skuId);
		PsiBarcode psiBarcode = sku.getBarcode();
		if(checked){
			String rs = psiProductService.validateFnSku(sku.getSku()); 
			if(rs.length()>0){
				return rs;
			}
			psiProductService.updateUseBarcode(sku.getCountry(),sku.getProductId(),psiBarcode.getProductColor());
			String  newSku=sku.getSku();
			String  oldSku="";
			for (PsiSku psisku : psiBarcode.getSkus()) {
				if(psisku.getSku().equals(sku.getSku())){
					psisku.setUseBarcode("1");
					psiBarcode.setLastUpdateBy(UserUtils.getUser());
					psiBarcode.setLastUpdateTime(new Date());
					String fnsku = amazonProduct2Service.findFnsku(psisku.getCountry(), psisku.getSku());
					if(StringUtils.isEmpty(fnsku)){
						psiBarcode.setBarcodeType(null);
						psiBarcode.setBarcode(null);
					}else if(fnsku.contains(":")){
						psiBarcode.setBarcodeType(fnsku.split(":")[0]);
						psiBarcode.setBarcode(fnsku.split(":")[1]);
					}
				}else{
					if("1".equals(psisku.getUseBarcode())){
						oldSku=psisku.getSku();
					}
					psisku.setUseBarcode("0");
				}
			}
			try{
				findUnReceivedDoneOrder(sku.getProductId(), psiBarcode.getProductPlatform(), psiBarcode.getProductColor(), psiBarcode.getProductName(), oldSku, newSku);
			}catch(Exception ex){
				logger.error("绑定条码，发信异常");
			}
		}else{
			sku.setUseBarcode("0");
			psiBarcode.setBarcodeType(null);
			psiBarcode.setBarcode(null);
			findUnReceivedDoneOrder(sku.getProductId(), psiBarcode.getProductPlatform(), psiBarcode.getProductColor(), psiBarcode.getProductName(), sku.getSku(),"");
		}
		
		psiProductService.save(psiBarcode);
		return "";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value={"updateBarcodeByUser"})
	public String updateBarcode(PsiProduct product, Model model) {
		model.addAttribute("products", psiProductService.findAll());
		model.addAttribute("product", product);
		return "modules/psi/psiBarcodeForm";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "saveBarcodes")
	public String saveBarcodes(PsiProduct product,
			Model model, RedirectAttributes redirectAttributes) {
		for (PsiBarcode barcode : product.getBarcodes()) {
			barcode.setLastUpdateBy(UserUtils.getUser());
			barcode.setLastUpdateTime(new Date());
		}
		psiProductService.save(product.getBarcodes());
		addMessage(redirectAttributes, "产品条码保存成功！");
		return "redirect:" + Global.getAdminPath() + "/psi/product/barcodeslist";
	}
	
	@ResponseBody
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value={"validateSku"})
	public String validateSku(String sku,String country) {
		return psiProductService.validateSku(sku,country);
	}
	
	@RequiresPermissions("psi:all:view")
	@ResponseBody
	@RequestMapping(value = "genBarcode")
	public String genBarcode(String country,String type,String productName,String barcode,String isOld,HttpServletResponse response) throws Exception {
		   String newLogo = "de".equals(country)?"Neu":"New";
		   if("de".equals(country)&&"1".equals(isOld)){
			   newLogo="alt";
		   }
		   if(country.startsWith("com")){
			   country = "us";
		   }
		   productName = productName +" "+country.toUpperCase(); 
		   String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/barcode";
			File baseDir = new File(baseDirStr);
			if(!baseDir.exists()){
				baseDir.mkdirs();
			}
			if("FNSKU".equals(type)){
				type= PdfUtil.CODE128;
				if(!barcode.startsWith("X")&&!barcode.startsWith("B")){
					throw new RuntimeException("条码有误，请于销售联系确认条码!");
				}
			}else{
				type=PdfUtil.EAN13;
			}
		   File file = new File(baseDir,UUID.randomUUID().toString()+".pdf");
		   PdfUtil.createBarCodePdf(type, file, barcode, productName, newLogo);
		   FileInputStream in = new FileInputStream(file);
		   ServletOutputStream out = response.getOutputStream();
		   response.setContentType("application/pdf");//pdf文件
		   response.addHeader("Content-Disposition", "filename="
					+ barcode+".pdf");
		   byte data[]=new byte[1024];
		   int len;
		   while((len=in.read(data)) != -1){
			   out.write(data,0,len);
		   }
		   out.flush();
		   in.close();
		   out.close();
		   return null;
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "delete")
	public String delete(Integer id,RedirectAttributes redirectAttributes) {
		psiProductService.delete(id);
		return "redirect:" + Global.getAdminPath() + "/psi/product/list";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "deleteDic")
	public String deleteDic(String did,
			RedirectAttributes redirectAttributes) {
		dictService.delete(did);
		return "redirect:" + Global.getAdminPath() + "/psi/product/listDict";
	}
	
	
	private  boolean sendEmail(String toAddress,String content,String title){
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();   
			final MailInfo mailInfo = new MailInfo(toAddress,title+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(content);
			new Thread(){
				public void run(){   
					 mailManager.send(mailInfo);
				}
			}.start();
		}
		return true;
	}
	
	private void findUnReceivedDoneOrder(Integer productId,String country,String color,String productName,String oldSku,String newSku){
		List<String> orders =this.purchaseOrderService.findUnReceivedDone(productId, country, color);
		StringBuffer  sb = new StringBuffer("");
		if(orders!=null&&orders.size()>0){
			for(String orderNo:orders){
				sb.append(orderNo).append(",");
			}
		}
		String orderNos = sb.toString();
		String afterSb = "";
		if(StringUtils.isNotEmpty(orderNos)){
			afterSb= "未收货订单："+orderNos.substring(0, orderNos.length()-1);
		}
		
		String countryCode=DictUtils.getDictLabel(country,"platform", "");
		
		String  nameColor = productName;
		if(StringUtils.isNotEmpty(color)){
			nameColor=nameColor+"_"+color;
		}
		String  content="请注意：<br/>";
		if(StringUtils.isNotEmpty(oldSku)&&StringUtils.isNotEmpty(newSku)){
			//查询仓库原sku库存情况
			Map<Integer,Integer> resMap=this.inventoryService.getInventoryBySku(oldSku);
			String inventoryStr="";
			if(resMap.size()>0){
				inventoryStr="<br/><b>现有库存("+oldSku+")</b>：中国仓:<b>"+(resMap.get(21)!=null?resMap.get(21):"")+"</b>德国仓：<b>"+(resMap.get(19)!=null?resMap.get(19):"")+"</b>";
			}
			String tipOldFnSku=this.amazonProduct2Service.findFnsku(country,oldSku);
			if(StringUtils.isEmpty(tipOldFnSku)||!tipOldFnSku.contains(":")){
				tipOldFnSku="empty";
			}else{
				tipOldFnSku=tipOldFnSku.split(":")[1];
			}
			String tipNewFnSku=this.amazonProduct2Service.findFnsku(country,newSku);
			if(StringUtils.isEmpty(tipNewFnSku)||!tipNewFnSku.contains(":")){
				tipNewFnSku="empty";
			}else{
				tipNewFnSku=tipNewFnSku.split(":")[1];
			}
			content=content+countryCode+"<b>"+nameColor+"</b>的条码进行如下切换：<br/> 原<b>"+oldSku+"</b>[<b>"+tipOldFnSku+"</b>]-->现<b>"+newSku+"</b>[<b>"+tipNewFnSku+"</b>],操作人:<b>"+UserUtils.getUser().getName()+"</b><br/><b>"+afterSb+"</b>"+inventoryStr;
			
			
			
		}else if(StringUtils.isEmpty(newSku)){
			//删除条码
			String tipOldFnSku=this.amazonProduct2Service.findFnsku(country,oldSku);
			if(StringUtils.isEmpty(tipOldFnSku)){
				tipOldFnSku="empty";
			}
			content=content+countryCode+"<b>"+nameColor+"</b>的条码进行了删除操作：<br/> Delete <b>"+oldSku+"</b>[<b>"+tipOldFnSku+"</b>],操作人:<b>"+UserUtils.getUser().getName()+"</b><br/>未收货订单<b>"+afterSb+"</b>";
		}else if(StringUtils.isEmpty(oldSku)){
			//新绑定条码
			String tipNewFnSku=this.amazonProduct2Service.findFnsku(country,newSku);
			if(StringUtils.isEmpty(tipNewFnSku)){
				tipNewFnSku="empty";
			}
			content=content+countryCode+"<b>"+nameColor+"</b>的新绑定条码：<br/> Add <b>"+newSku+"</b>[<b>"+tipNewFnSku+"</b>],操作人:<b>"+UserUtils.getUser().getName()+"</b><br/>未收货订单<b>"+afterSb+"</b>";
		}
		String addReceivedMan = "";   
		if("de,fr,es,it,uk".contains(country)&&StringUtils.isNotEmpty(newSku)&&StringUtils.isNotEmpty(oldSku)&&psiProductService.getHasSameAsin(newSku,oldSku)){
			addReceivedMan=",george@inateck.com";
		}
		String title="["+countryCode+"--"+nameColor+""+"FNSKU切换通知]";
		
		String toAddress =UserUtils.logistics1+",lily@inateck.com,bella@inateck.com,lena@inateck.com,"+UserUtils.getUser().getEmail()+addReceivedMan;
		sendEmail(toAddress,content, title);
	}
	
	/*private void amaMateOther(){
		String countryArr ="de,com,uk,fr,jp,it,es,ca";
		int i = 0;
		for(String country:countryArr.split(",")){
			Set<String>  amaSkus =amazonProduct2Service.findUnMateSku(country);
			for(String sku:amaSkus){
				boolean res = sku.matches("[0-9]+");
				if(res){
					PsiBarcode barcode =  psiProductService.getBarcodeByProCouCol(87,country,"");
					String productName = barcode.getProductName();
					PsiSku psisku = new PsiSku();
					psisku.setUseBarcode("0");
					psisku.setBarcode(barcode);
					psisku.setColor("");
					psisku.setCountry(country);
					psisku.setSku(sku);
					if(!"ebay".equals(country)){
						psisku.setAsin(amazonProduct2Service.findAsin(country, sku));
					}
					psisku.setProductName(productName);
					psisku.setProductId(87);
					psiProductService.save(psisku);
					i++;
				}
				
			}
			System.out.println("处理亚马逊sku："+country+"个数："+i);
		}
	}*/
	
	
	
/*	private void ebayMateOther(){
			String country="ebay";
			int i= 0;
			Set<String>  amaSkus =amazonProduct2Service.findUnMateSku(country);
			for(String sku:amaSkus){
				boolean res = sku.matches("[0-9]+");
				if(res){
					PsiBarcode barcode =  psiProductService.getBarcodeByProCouCol(87,"de","");
					String productName = barcode.getProductName();
					PsiSku psisku = new PsiSku();
					psisku.setUseBarcode("0");
					psisku.setBarcode(barcode);
					psisku.setColor("");
					psisku.setCountry(country);
					psisku.setSku(sku);
					psisku.setProductName(productName);
					psisku.setProductId(87);
					psiProductService.save(psisku);
					i++;
				}
			}
			System.out.println("处理ebay，sku个数："+i);
	}
	  */
	
	@ResponseBody
	@RequestMapping(value = {"ajaxPartsData"})
	public String ajaxPartsData(Integer productId,String colorStr) {
		StringBuilder colorJson=new StringBuilder();
		for(String color:colorStr.split(",")){
			String sdata =this.productPartsService.getSelectedPartsData(productId, color);
			String  data =this.partsService.getPartsData();  
			colorJson.append("{\"color\":\"").append(color).append("\",\"selectedIds\":").append(sdata).append(",\"items\":").append(data).append("},");
		}
		
		String rs="{\"msg\":\"true\",\"colors\":["+colorJson.substring(0,colorJson.length()-1)+"]}";
		
		return rs;
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"editParts"})
	public String editParts(Integer productId,String color,String[] parts){
		this.productPartsService.editProductPartsRelivate(productId, color, parts);
		return "保存成功！！！";
	}
	
	@RequestMapping(value="upload")
	@ResponseBody
	public  String uploadFile(Integer id,MultipartFile uploadFile,String uploadType,HttpServletResponse response,RedirectAttributes redirectAttributes) throws UnsupportedEncodingException{
		if(uploadFile.getSize()!=0){
			PsiProduct product=this.psiProductService.get(id);
			String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/psiproduct/";
			File baseDir = new File(baseDirStr); 
			if(!baseDir.isDirectory())
				baseDir.mkdirs();
			
			String name = uploadFile.getOriginalFilename();
			String suffix = uploadFile.getOriginalFilename().substring(uploadFile.getOriginalFilename().lastIndexOf(".")); 
			String suffixName="";
			String model =product.getModel();
			String flag="0";
			if("0,1,2,3,4,5,6,20,21,".contains(uploadType+",")){
				String[] suffixArr=StringUtils.isBlank(product.getCertificationFile())?"CE-ROHS-FCC-FDA-BQB-UL-PSE-TELEC-ETL".split("-"):product.getCertificationFile().split("-");
				String suffixStr=StringUtils.isBlank(product.getCertificationFile())?"CE-ROHS-FCC-FDA-BQB-UL-PSE-TELEC-ETL":product.getCertificationFile();
				List<Integer> index=new ArrayList<Integer>();
				for(int i=0;i<suffixArr.length;i++){
				    if(i==0){
				    	index.add(suffixArr[0].length());
				    }else{//取到分隔符位置
				    	index.add(index.get(i-1)+suffixArr[i].length()+1);
				    }
				}
				if("0".equals(uploadType)){
					name = model+"_CE"+suffix;
					suffixName= suffix+"-"+suffixStr.substring(index.get(0)+1);
				}else if("1".equals(uploadType)){
					name=model+"_ROHS"+suffix;
					suffixName=suffixStr.substring(0,index.get(0))+"-"+suffix+"-"+suffixStr.substring(index.get(1)+1);
				}else if("2".equals(uploadType)){
					name=model+"_FCC"+suffix;
					suffixName=suffixStr.substring(0,index.get(1))+"-"+suffix+"-"+suffixStr.substring(index.get(2)+1);
				}else if("3".equals(uploadType)){
					name=model+"_FDA"+suffix;
					suffixName=suffixStr.substring(0,index.get(2))+"-"+suffix+"-"+suffixStr.substring(index.get(3)+1);
				}else if("4".equals(uploadType)){
					name=model+"_BQB"+suffix;
					suffixName=suffixStr.substring(0,index.get(3))+"-"+suffix+"-"+suffixStr.substring(index.get(4)+1);
				}else if("5".equals(uploadType)){
					name=model+"_UL"+suffix;
					suffixName=suffixStr.substring(0,index.get(4))+"-"+suffix+"-"+suffixStr.substring(index.get(5)+1);
				}else if("6".equals(uploadType)){
					name=model+"_PSE"+suffix;
					suffixName=suffixStr.substring(0,index.get(5))+"-"+suffix+"-"+suffixStr.substring(index.get(6)+1);
				}else if("20".equals(uploadType)){
					name=model+"_TELEC"+suffix;
					suffixName=suffixStr.substring(0,index.get(6))+"-"+suffix+"-"+suffixStr.substring(index.get(7)+1);
				}else if("21".equals(uploadType)){
					name=model+"_ETL"+suffix;
					suffixName=suffixStr.substring(0,index.get(7))+"-"+suffix;
				}
			}else if("7,8,9,10,11,12,".contains(uploadType+",")){
				flag ="1";
				//带电的运输报告
				String[] suffixArr=StringUtils.isBlank(product.getTranReportFile())?"MSDS-UN38.3-DROP1.2-AIR-SEA-SP188".split("-"):product.getTranReportFile().split("-");
				String suffixStr=StringUtils.isBlank(product.getTranReportFile())?"MSDS-UN38.3-DROP1.2-AIR-SEA-SP188":product.getTranReportFile();
				List<Integer> index=new ArrayList<Integer>();
				for(int i=0;i<suffixArr.length;i++){
				    if(i==0){
				    	index.add(suffixArr[0].length());
				    }else{//取到分隔符位置
				    	index.add(index.get(i-1)+suffixArr[i].length()+1);
				    }
				}
				if("7".equals(uploadType)){
					name = model+"_MSDS"+suffix;
					suffixName= suffix+"-"+suffixStr.substring(index.get(0)+1);
				}else if("8".equals(uploadType)){
					name=model+"_UN38.3"+suffix;
					suffixName=suffixStr.substring(0,index.get(0))+"-"+suffix+"-"+suffixStr.substring(index.get(1)+1);
				}else if("9".equals(uploadType)){
					name=model+"_DROP1.2"+suffix;
					suffixName=suffixStr.substring(0,index.get(1))+"-"+suffix+"-"+suffixStr.substring(index.get(2)+1);
				}else if("10".equals(uploadType)){
					name=model+"_AIR"+suffix;
					suffixName=suffixStr.substring(0,index.get(2))+"-"+suffix+"-"+suffixStr.substring(index.get(3)+1);
				}else if("11".equals(uploadType)){
					name=model+"_SEA"+suffix;
					suffixName=suffixStr.substring(0,index.get(3))+"-"+suffix+"-"+suffixStr.substring(index.get(4)+1);
				}else if("12".equals(uploadType)){
					name=model+"_SP188"+suffix;
					suffixName=suffixStr.substring(0,index.get(4))+"-"+suffix;
				}
				
			}else if("30,".contains(uploadType+",")){
				flag="2";
				name=model+"/"+model+"_CL_"+name;
				suffixName=name;
			}else if("31,".contains(uploadType+",")){
				flag="3";
				name=model+"/"+model+"_TF_"+name;
				suffixName=name;
			}else if("32,".contains(uploadType+",")){
				flag="4";
				name = model + "/" + model + " BOM LIST" + suffix;
				suffixName=name;
			}
			File dest = new File(baseDir,name);
			if(response.getCharacterEncoding()==null){
				response.setCharacterEncoding("UTF-8");
			};
			
			try {
				FileUtils.copyInputStreamToFile(uploadFile.getInputStream(),dest);
				//uploadFile.transferTo(dest);
				this.psiProductService.updateSuffixName(product.getId(), suffixName,flag);
				addMessage(redirectAttributes, "文件上传成功"+("30,".contains(uploadType+",")?",Check List需品质主管确认才能下载":""));
				if("30,".contains(uploadType+",")){//checkList
					Date date = new Date();   
					StringBuffer content= new StringBuffer("");
					content.append("<p>Hi All,请审核"+UserUtils.getUser().getName()+"上传"+model+"产品的Check List</p>");
					final MailInfo mailInfo = new MailInfo("qc@inateck.com",model+" Check List审核"+DateUtils.getDate("-yyyy/MM/dd"),date);//
					mailInfo.setContent(content.toString());
					new Thread(){
						public void run(){   
							 mailManager.send(mailInfo);
						}
					}.start();
				}
				return "0";
			} catch (IOException e) {
				logger.warn(name+"文件保存失败",e);
				addMessage(redirectAttributes, "文件上传失败");
				return "1";
			}
		}else{
			addMessage(redirectAttributes, "上传文件名为空");
			return "2";
		}
	}
	
	@RequestMapping("/download")   
    public ModelAndView download(String fileName, HttpServletRequest request, HttpServletResponse response)   
            throws Exception {   
		fileName = HtmlUtils.htmlUnescape(fileName);
        response.setContentType("text/html;charset=utf-8");   
        request.setCharacterEncoding("UTF-8");   
        java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null;   
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/psi/psiproduct";  
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
	
	@RequestMapping("/download1")   
    public ModelAndView download1(String fileName, HttpServletRequest request, HttpServletResponse response)   
            throws Exception {   
		fileName = URLDecoder.decode(URLDecoder.decode(fileName, "UTF-8"),"UTF-8");
        response.setContentType("text/html;charset=utf-8");   
        request.setCharacterEncoding("UTF-8");   
        java.io.BufferedInputStream bis = null;   
        java.io.BufferedOutputStream bos = null;   
        String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/psi/psiproduct";  
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
	
	@RequestMapping(value = {"exportPrice"})
	public String exportPrice(HttpServletRequest request,
			HttpServletResponse response, Model model) {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) 600);
	    HSSFCell cell = null;		
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
	    style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
	    HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
		

		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
        List<String> title =Lists.newArrayList("产品名","MOQ","供应商","价格");
		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
		}
		int rownum=1;
		Map<String,Map<Integer,Float>> map=tieredPriceService.getPriceBaseMoq(null,null);
		for (Map.Entry<String,Map<Integer,Float>> entry1: map.entrySet()) {
			String name = entry1.getKey();
			row = sheet.createRow(rownum++);
			int j=0;
			String[] arr=name.split(",;");
			Map<Integer,Float> supplier=entry1.getValue();
			for (Map.Entry<Integer,Float> entry: supplier.entrySet()) {
				Integer supId = entry.getKey();
				if(supId!=null){
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(arr[0]);
					row.getCell(j-1).setCellStyle(contentStyle);
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(arr[1]);
					row.getCell(j-1).setCellStyle(contentStyle);
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(psiSupplierService.get(supId).getNikename());
					row.getCell(j-1).setCellStyle(contentStyle);
					if(entry.getValue()!=null){
						row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(entry.getValue()*2);
						row.getCell(j-1).setCellStyle(cellStyle);
					}else{
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						row.getCell(j-1).setCellStyle(contentStyle);
					}
					
				}
			}
		}
		for (int i = 0; i < title.size(); i++) {
	   		   sheet.autoSizeColumn((short)i,true);
		}
		  try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				SimpleDateFormat sdf1 = new SimpleDateFormat("MMddhhmm");
				String fileName = "price" + sdf1.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"signedSample"})
	public String signedSample(Integer productId,String signedSample) {
		try{
			this.psiProductService.signedSample(productId, URLDecoder.decode(signedSample,"UTF-8"));
			return "true";
		}catch(Exception ex){
			return "false";
		}
		
	}
	
	
	@RequestMapping(value = "updateCheckListState")
	public String updateCheckListState(Integer id,String state,RedirectAttributes redirectAttributes) {
		PsiProduct product=this.psiProductService.get(id);
		psiProductService.updateCheckListState(id,state);
		addMessage(redirectAttributes,product.getModel()+" Check List审核"+("1".equals(state)?"通过":"驳回"));
		Date date = new Date();   
		StringBuffer content= new StringBuffer("");
		content.append("<p>Hi "+product.getCheckListUser().getName()+","+UserUtils.getUser().getName()+("1".equals(state)?"通过":"驳回")+product.getModel()+"产品的Check List</p>");
		final MailInfo mailInfo = new MailInfo(product.getCheckListUser().getEmail(),product.getModel()+" Check List审核"+("1".equals(state)?"通过":"驳回")+DateUtils.getDate("-yyyy/MM/dd"),date);//
		mailInfo.setContent(content.toString());
		new Thread(){
			public void run(){   
				 mailManager.send(mailInfo);
			}
		}.start();
		return "redirect:" + Global.getAdminPath() + "/psi/product/list";
	}
	

	@RequestMapping(value = "downloadZipFile")
	public String downloadZipFile(String type,String line,String lineName,HttpServletResponse response) throws Exception { 
		Map<String,String> fileMap=Maps.newHashMap();
		
		String fileName="";
		if("0".equals(type)){//checkList
			fileMap=psiProductService.findAllCheckList(line);
			fileName="CheckList";
		}else if("1".equals(type)){//技术规格书
			fileMap=psiProductService.findAllTechFileList(line);
			fileName="技术规格书";
		}else{//技术规格书
			fileMap=psiProductService.findAllBomList(line);
			fileName="BomList";
		}
		if(fileMap!=null&&fileMap.size()>0){
			String ctxPath =  ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") +Global.getCkBaseDir()+"/psi/psiproduct/";  
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
			String baseDirStr= ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/tempFileList/";
			fileName=baseDirStr+dateFormat.format(new Date())+fileName;
			File zipFile = new File (fileName+".zip");
			if(!zipFile.exists()){
	    	    //先得到文件的上级目录，并创建上级目录，在创建文件
	    		zipFile.getParentFile().mkdir();
	    	    try {
	    	        //创建文件
	    	    	zipFile.createNewFile();
	    	    } catch (IOException e) {
	    	        e.printStackTrace();
	    	    }
	    	}
			FileOutputStream fous = new FileOutputStream(zipFile); 
	    	ZipOutputStream zipOut = new ZipOutputStream(fous);
	    	ZipUtil.zipFile(fileMap,zipOut,ctxPath);
	    	zipOut.close();
	    	fous.close();
	    	downloadZip(zipFile,response);
	    	return null;
		}
		return "redirect:" + Global.getAdminPath() + "/psi/product/list";
	}
	
	public static HttpServletResponse downloadZip(File file,HttpServletResponse response) {
        try {
        // 以流的形式下载文件。
        InputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();
        // 清空response
        response.reset();

        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
        response.setContentType("application/octet-stream");

       //如果输出的是中文名的文件，在此处就要用URLEncoder.encode方法进行处理
        response.setHeader("Content-Disposition", "attachment;filename=" +URLEncoder.encode(file.getName(), "UTF-8"));
        toClient.write(buffer);
        toClient.flush();
        toClient.close();
        } catch (IOException ex) {
        ex.printStackTrace();
        }finally{
             try {
                    File f = new File(file.getPath());
                    f.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
        return response;
    }
	
	
	@RequestMapping(value = {"exportSize"})
	public String exportSize(HttpServletRequest request,
			HttpServletResponse response, Model model) {
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		row.setHeight((short) 600);
	    HSSFCell cell = null;		
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
	    style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
	    HSSFFont font = wb.createFont();
		font.setFontHeightInPoints((short) 16); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 16);
		style.setFont(font);
		CellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
		

		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
        List<String> title =Lists.newArrayList("产品名","长","宽","高","重","类型","价格($)");
		for(int i=0;i<title.size();i++){
			cell = row.createCell(i);
			cell.setCellStyle(style);
			cell.setCellValue(title.get(i));
		}
		int rownum=1;
		List<PsiProduct> list =psiProductService.findTempSize();
		for (PsiProduct p: list) {
			
			 row = sheet.createRow(rownum++);
			 int j=0;
		
			 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(p.getBrand());
			 boolean flag=false;
			 float length =0f;
			 float width =0f;
			 float height =0f;
			 float weight =0f;
			 if(p.getProductPackLength()==null){
				 flag = true;
				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(""); 
			 }else{
				 length=p.getProductPackLength().floatValue();
				 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(length);
				 row.getCell(j-1).setCellStyle(cellStyle);
			 }
			 if(p.getProductPackWidth()==null){
				 flag = true;
				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(""); 
			 }else{
				 width=p.getProductPackWidth().floatValue();
				 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(width);
				 row.getCell(j-1).setCellStyle(cellStyle);
			 }
			 
			 if(p.getProductPackHeight()==null){
				 flag = true;
				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(""); 
			 }else{
				 height=p.getProductPackHeight().floatValue();
				 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(height);
				 row.getCell(j-1).setCellStyle(cellStyle);
			 }
			 
			 if(p.getProductPackWeight()==null){
				 flag = true;
				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(""); 
			 }else{
				 weight = p.getProductPackWeight().floatValue();
				 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(weight);
				 row.getCell(j-1).setCellStyle(cellStyle);
			 }
				
			 if(flag){
				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(""); 
				 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(""); 
			 }else{
				 if(length<=15&&width<=0.75&&height<=12){
					 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("Large Envelopes"); 
					 if(weight<=0.06){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(1); 
					 }else if(weight<=0.13){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(1.21); 
					 }else if(weight<=0.19){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(1.42); 
					 }else if(weight<=0.25){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(1.63); 
					 }else if(weight<=0.31){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(1.84); 
					 }else if(weight<=0.38){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(2.05); 
					 }else if(weight<=0.44){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(2.26); 
					 }else if(weight<=0.5){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(2.47); 
					 }else if(weight<=0.56){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(2.68);  
					 }else if(weight<=0.63){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(2.89); 
					 }else if(weight<=0.69){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(3.1); 
					 }else if(weight<=0.75){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(3.31); 
					 }else if(weight<=0.81){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(3.52); 
					 }else{
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(""); 
					 }
				 }else if(length+(width+height)*2<=108){
					 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("Parcels"); 
					 if(weight<=0.06){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(3.5); 
					 }else if(weight<=0.13){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(3.5); 
					 }else if(weight<=0.19){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(3.5); 
					 }else if(weight<=0.25){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(3.5); 
					 }else if(weight<=0.31){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(3.5); 
					 }else if(weight<=0.38){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(3.75); 
					 }else if(weight<=0.44){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(3.75); 
					 }else if(weight<=0.5){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(3.75); 
					 }else if(weight<=0.56){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(4.1);  
					 }else if(weight<=0.63){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(4.45); 
					 }else if(weight<=0.69){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(4.8); 
					 }else if(weight<=0.75){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(5.15); 
					 }else if(weight<=0.81){
						 row.createCell(j++,Cell.CELL_TYPE_NUMERIC).setCellValue(5.5); 
					 }else{
						 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(""); 
					 }
				 }else{
					 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(""); 
					 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(""); 
				 }
			 }
			
		}
		for (int i = 0; i < title.size(); i++) {
	   		   sheet.autoSizeColumn((short)i,true);
		}
		  try {
				request.setCharacterEncoding("UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.setContentType("application/x-download");
				SimpleDateFormat sdf1 = new SimpleDateFormat("MMddhhmm");
				String fileName = "size" + sdf1.format(new Date()) + ".xls";
				fileName = URLEncoder.encode(fileName, "UTF-8");
				response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
				OutputStream out = response.getOutputStream();
				wb.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return null;
	}
}
