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
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.SimpleFormatter;

import javax.servlet.ServletOutputStream;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
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
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.entity.PsiLadingBillItemDto;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductDto;
import com.springrain.erp.modules.psi.entity.PsiQuestionSupplier;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.lc.LcPsiLadingBill;
import com.springrain.erp.modules.psi.entity.lc.LcPsiLadingBillItem;
import com.springrain.erp.modules.psi.entity.lc.LcPsiQualityTest;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrderItem;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiQuestionSupplierService;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.StockService;
import com.springrain.erp.modules.psi.service.lc.LcPsiLadingBillService;
import com.springrain.erp.modules.psi.service.lc.LcPsiQualityTestService;
import com.springrain.erp.modules.psi.service.lc.LcPurchaseOrderItemService;
import com.springrain.erp.modules.psi.service.lc.LcPurchaseOrderService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 提单明细Controller
 * 
 * @author Michael
 * @version 2014-11-11
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/lcPsiLadingBill")
public class LcPsiLadingBillController extends BaseController {
	@Autowired
	private LcPsiLadingBillService 			psiLadingBillService;
	@Autowired
	private LcPurchaseOrderItemService 		purchaseOrderItemService;
	@Autowired
	private LcPurchaseOrderService 			purchaseOrderService;
	@Autowired
	private PsiSupplierService 				psiSupplierService;
	@Autowired
	private PsiProductService 				psiProductService;
	@Autowired
	private PsiProductService 				productService;
	@Autowired
	private PsiInventoryService 			inventoryService;
	@Autowired
	private AmazonProduct2Service 			amazonService;
	@Autowired
	private LcPsiQualityTestService 	    testService;
	@Autowired
	private SystemService                   systemService;
	@Autowired
	private StockService                    stockService;
	@Autowired
    private PsiQuestionSupplierService      psiQuestionSupplierService;
	
	private static String 				filePath;

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = { "list", "listShow", "" })
	public String list(LcPsiLadingBill psiLadingBill,String isCheck,HttpServletRequest request, HttpServletResponse response,	Model model) throws ParseException {
		Date today = sdf.parse(sdf.format(new Date()));
		if (psiLadingBill.getCreateDate() == null) {
			psiLadingBill.setCreateDate(DateUtils.addMonths(today, -3));
			psiLadingBill.setUpdateDate(today);
		}
		
		List<Integer> ladingBillIds = Lists.newArrayList();
		if(StringUtils.isEmpty(isCheck)){
			isCheck="0";
		}else if("1".equals(isCheck)){
			ladingBillIds=this.testService.getHasTest();
		}
		
		
		Map<String, String> fnskuMap = this.amazonService.getSkuAndFnskuMap();
		Page<LcPsiLadingBill> page = psiLadingBillService.find(new Page<LcPsiLadingBill>(request, response), psiLadingBill,ladingBillIds,isCheck);
		List<PsiSupplier> suppliers = this.psiSupplierService.findAll();
		Set<Integer> ladingIds =Sets.newHashSet();
		Map<String,String>  testReceivedMap = Maps.newHashMap();
		Map<String,List<String>>  firstTestMap = Maps.newHashMap();
		for(LcPsiLadingBill bill :page.getList()){
//			if("0".equals(bill.getBillSta())||"5".equals(bill.getBillSta())){//申请的和部分收货的
				ladingIds.add(bill.getId());
//			}
		}
		//根据提单id，获取已经品检的产品数量
		if(ladingIds.size()>0){    
			testReceivedMap=this.testService.getTestQuantityAll(ladingIds);
			firstTestMap=this.testService.getTestInfo(ladingIds);
		}
		
		//查出第一个单的质检类型
		
		
		//查询产品经理，产品类型关系
		Map<String,Set<String>> productMangerIdMap=this.productService.findMangerByProductNames();
		
		model.addAttribute("isCheck", isCheck);
		model.addAttribute("firstTestMap", firstTestMap);
		model.addAttribute("testReceivedMap", testReceivedMap);
		model.addAttribute("productMangerIdMap", productMangerIdMap);
		model.addAttribute("fnskuMap", fnskuMap);
		model.addAttribute("suppliers", suppliers);
		model.addAttribute("page", page);
		return "modules/psi/lc/lcPsiLadingBillList";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = { "testList"})
	public String testList(LcPsiQualityTest test,HttpServletRequest request, HttpServletResponse response,	Model model) throws ParseException{
		Date today = sdf.parse(sdf.format(new Date()));
		if (test.getCreateDate() == null) {
			test.setCreateDate(DateUtils.addMonths(today, -3));
			test.setSureDate(today);
		}
		
		Page<LcPsiQualityTest> page = testService.findTest(new Page<LcPsiQualityTest>(request, response), test);
		
		model.addAttribute("page", page);
		model.addAttribute("test", test);
		return "modules/psi/lc/lcPsiLadingBillTestList";
	}

	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = { "testReview"})
	public String testReview(LcPsiQualityTest test,HttpServletRequest request, HttpServletResponse response,Model model) throws ParseException{
		if(test.getId()==null){
			return null;
		}
		test = this.testService.get(test.getId());
		model.addAttribute("test", test);
		return "modules/psi/lc/lcPsiLadingBillTestReview";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = { "testReviewView"})
	public String testReviewView(LcPsiQualityTest test,HttpServletRequest request, HttpServletResponse response,Model model) throws ParseException{
		if(test.getId()==null){
			return null;
		}
		test = this.testService.get(test.getId());
		model.addAttribute("test", test);
		return "modules/psi/lc/lcPsiLadingBillTestReviewView";
	}
	

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = { "genSeq" })
	public String genSeq(LcPsiLadingBill psiLadingBill, Integer ladingBillId,
			String nikeName, HttpServletRequest request,
			HttpServletResponse response, Model model) throws ParseException {
		Date today = sdf.parse(sdf.format(new Date()));
		psiLadingBill.setCreateDate(DateUtils.addMonths(today, -1));
		psiLadingBill.setUpdateDate(today);
		// 生成序列号1
		String billNo = this.psiLadingBillService.createSequenceNumber(nikeName
				+ "_LC_TDH");
		if (ladingBillId != null) {
			this.psiLadingBillService.updateLadingBillSeq(billNo, ladingBillId);
		}

		Page<LcPsiLadingBill> page = psiLadingBillService.find(new Page<LcPsiLadingBill>(request, response), psiLadingBill,null,null);
		List<PsiSupplier> suppliers = this.psiSupplierService.findAll();
		model.addAttribute("suppliers", suppliers);
		model.addAttribute("page", page);
		addMessage(model, "保存提单'" + billNo + "'成功");
		return "modules/psi/lc/lcPsiLadingBillList";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "add")
	public String add(LcPsiLadingBill psiLadingBill, Model model) {
		List<String> products = new ArrayList<String>();
		List<PsiSupplier> suppliers = this.psiSupplierService.findAll();
		List<PsiSupplier> tranSuppliers = this.psiSupplierService
				.findAllTransporter();
		List<Object[]> list = null;

		Map<String, List<Object[]>> productMap = new HashMap<String, List<Object[]>>();
		Map<String, Integer> totalMap = new HashMap<String, Integer>();
		// 查出该供应商可提单产品 所在订单编号 数量
		if (psiLadingBill.getSupplier() == null) {
			if (suppliers != null && suppliers.size() > 0) {
				list = this.psiLadingBillService.getProductLading(suppliers
						.get(0).getId(), psiLadingBill.getCurrencyType());
			}
		} else {
			list = this.psiLadingBillService.getProductLading(psiLadingBill
					.getSupplier().getId(), psiLadingBill.getCurrencyType());
		}

		if (list != null && list.size() > 0) {
			for (Object[] obs : list) {
				List<Object[]> listObj = new ArrayList<Object[]>();
				Integer canLadingQuantity = 0;
				if (productMap.containsKey(obs[0].toString())) {
					listObj = productMap.get(obs[0].toString());
					Integer total = totalMap.get(obs[0].toString());
					canLadingQuantity = total
							+ Integer.parseInt(obs[2].toString());
				} else {
					canLadingQuantity = Integer.parseInt(obs[2].toString());
				}
				totalMap.put(obs[0].toString(), canLadingQuantity);
				listObj.add(obs);
				productMap.put(obs[0].toString(), listObj);

			}
		}
		products.addAll(productMap.keySet());

		// 页面放两个map
		model.addAttribute("productMap", JSON.toJSON(productMap));
		model.addAttribute("totalMap", JSON.toJSON(totalMap));
		model.addAttribute("products", products);
		model.addAttribute("tranSuppliers", tranSuppliers);
		model.addAttribute("suppliers", suppliers);
		model.addAttribute("psiLadingBill", psiLadingBill);
		return "modules/psi/lc/lcPsiLadingBillAdd";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "batchReceive")
	public String batchReceive(String orderIds, String orderItemIds,
			Integer supplierId, String currencyType, Model model) {
		SimpleDateFormat dFormat = new SimpleDateFormat("yyyyMMdd");
		LcPsiLadingBill psiLadingBill = new LcPsiLadingBill();
		psiLadingBill.setCurrencyType(currencyType);
		List<LcPurchaseOrderItem> orderItems = purchaseOrderItemService
				.getOrderItems(orderItemIds);
		Map<String, List<PsiLadingBillItemDto>> ladingMap = Maps.newHashMap();
		// 这里多生成一个map因为totalMap里面的数量和批量这里的可提单总数不同，这里只是选中的几个的总和
		Map<String, Integer> batchTotalMap = new HashMap<String, Integer>();
		for (LcPurchaseOrderItem orderItem : orderItems) {
			// 组合map
			String key = orderItem.getProductName()	+ "|"+ this.psiLadingBillService.changeCountryToUs(orderItem.getCountryCode());
			if (StringUtils.isNotEmpty(orderItem.getColorCode())) {
				key += "|" + orderItem.getColorCode();
			}

			List<PsiLadingBillItemDto> itemList = null;
			Integer ladingQuantity = orderItem.getQuantityOrdered()	- orderItem.getQuantityReceived()- orderItem.getQuantityPreReceived();
			if (ladingQuantity > 0) {
				PsiLadingBillItemDto dto = new PsiLadingBillItemDto(
						orderItem.getItemPrice(), orderItem.getId(), orderItem.getPurchaseOrder().getId(), orderItem.getPurchaseOrder().getOrderNo(), orderItem.getProduct().getPackQuantity(),	ladingQuantity, null,
						orderItem.getCanLadingOffQuantity(),dFormat.format(orderItem.getDeliveryDate()),orderItem.getProduct().getBoxVolume()!=null?orderItem.getProduct().getBoxVolume().floatValue():0f);
				if (ladingMap.containsKey(key)) {
					itemList = ladingMap.get(key);
				} else {
					itemList = Lists.newArrayList();
				}
				itemList.add(dto);
				ladingMap.put(key, itemList);
				Integer canQuantity = 0;
				if (batchTotalMap.containsKey(key)) {
					canQuantity = batchTotalMap.get(key) + ladingQuantity;
				} else {
					canQuantity = ladingQuantity;
				}
				batchTotalMap.put(key, canQuantity);
			}
		}

		List<String> products = new ArrayList<String>();
		List<PsiSupplier> suppliers = this.psiSupplierService.findAll();
		List<PsiSupplier> tranSuppliers = this.psiSupplierService.findAllTransporter();
		List<Object[]> list = null;

		Map<String, List<Object[]>> productMap = new HashMap<String, List<Object[]>>();
		Map<String, Integer> totalMap = new HashMap<String, Integer>();
		// 查出该供应商可提单产品 所在订单编号 数量
		list = this.psiLadingBillService.getProductLading(supplierId,currencyType);
		Set<Integer> productIdSet = Sets.newHashSet();
		if (list != null && list.size() > 0) {
			for (Object[] obs : list) {
				List<Object[]> listObj = null;
				Integer canLadingQuantity = 0;
				if (productMap.containsKey(obs[0].toString())) {
					listObj = productMap.get(obs[0].toString());
					Integer total = totalMap.get(obs[0].toString());
					canLadingQuantity = total+ Integer.parseInt(obs[2].toString());
				} else {
					listObj = Lists.newArrayList();
					canLadingQuantity = Integer.parseInt(obs[2].toString());
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

		Set<String> skus = Sets.newHashSet();
		
		//添加质检员质检时间
		List<User> userList = systemService.findUserByPermission("psi:ladingBill:qualityTest");
		if(userList!=null){
			model.addAttribute("testUsers", userList);
		}
		
		
		//算出及时库存容量
        Float timelyCap = this.inventoryService.getTimelyCapacity(130);
        //查出库存最大容量
        Float maxCap = this.stockService.get(130).getCapacity();
        
		Map<String, Set<String>> skuMap = this.productService.getSkuMapByProduct(productIdSet, skus);
		Map<String, String> fnskuMap = this.productService.getSkuAndFnskuMap(skus);
		model.addAttribute("skuMap", JSON.toJSON(skuMap));
		model.addAttribute("fnskuMap", JSON.toJSON(fnskuMap));

		model.addAttribute("productMap", JSON.toJSON(productMap));
		model.addAttribute("totalMap", JSON.toJSON(totalMap));
		model.addAttribute("ladingMap", JSON.toJSON(ladingMap));
		model.addAttribute("batchTotalMap", JSON.toJSON(batchTotalMap));
		model.addAttribute("products", products);
		model.addAttribute("tranSuppliers", tranSuppliers);
		model.addAttribute("suppliers", suppliers);
		model.addAttribute("psiLadingBill", psiLadingBill);
		model.addAttribute("timelyCap", timelyCap);
		model.addAttribute("maxCap", maxCap);
		return "modules/psi/lc/lcPsiLadingBillBatchAdd";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "addSave")
	public String addSave(String nikeName, LcPsiLadingBill psiLadingBill,
			Model model, RedirectAttributes redirectAttributes)
			throws Exception {
		if (psiLadingBill.getItems() != null) {
			PsiSupplier supplier = this.psiSupplierService.get(psiLadingBill.getSupplier().getId());
			this.psiLadingBillService.addSave(psiLadingBill, supplier);
			addMessage(redirectAttributes, "保存提单'" + psiLadingBill.getBillNo()	+ "'成功");
			//通知品检
			sendEmailToQc(psiLadingBill, supplier);
		} else {
			addMessage(redirectAttributes, "保存提单失败,提单项为空");
		}
		return "redirect:" + Global.getAdminPath() + "/psi/lcPsiLadingBill";
	}

	@ResponseBody
	@RequestMapping(value = "canSave")
	public String canSave(String ids, String nums) {
		String[] temp = ids.split(",");
		String[] temp1 = nums.split(",");
		for (int i = 0; i < temp1.length; i++) {
			String str = temp1[i];
			if (str.length() > 0 && temp[i].length() > 0) {
				if (!this.psiLadingBillService.canSave(temp[i], str)) {
					return "false";
				}
			}
		}
		return "true";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "edit")
	public String edit(LcPsiLadingBill psiLadingBill, Model model) {
		List<Object[]> orderProductlist ;
		List<String> products = new ArrayList<String>();
		Map<String, List<PsiLadingBillItemDto>> ladingMap = Maps.newHashMap();
		Map<String, List<Object[]>> productMap = new HashMap<String, List<Object[]>>();
		Map<String, Integer> totalMap = new HashMap<String, Integer>();
		List<PsiSupplier> tranSuppliers = this.psiSupplierService.findAllTransporter();
		StringBuilder sb = new StringBuilder("");
		Map<Integer,String> orderItemInfoMap = Maps.newHashMap();
		Set<Integer> productIdSet = Sets.newHashSet();
		psiLadingBill = this.psiLadingBillService.get(psiLadingBill.getId());

		for (LcPsiLadingBillItem item : psiLadingBill.getItems()) {
			// 组合map
			String key = item.getProductName()+ "|"	+ this.psiLadingBillService.changeCountryToUs(item.getCountryCode());
			if (StringUtils.isNotEmpty(item.getColorCode())) {
				key += "|" + item.getColorCode();
			}
			List<PsiLadingBillItemDto> itemList = null;
			if (ladingMap.get(key) == null) {
				itemList = Lists.newArrayList();
			} else {
				itemList = ladingMap.get(key);
			}
			itemList.add(new PsiLadingBillItemDto(item));
			ladingMap.put(key, itemList);
			sb.append(item.getId() + ",");
			orderItemInfoMap.put(item.getPurchaseOrderItem().getId(), item.getQuantityLading()+","+item.getQuantityOffLading());
		}

		String itemIds = "";
		if (StringUtils.isNotEmpty(sb.toString())) {
			itemIds = sb.toString().substring(0, sb.toString().length() - 1);
		}

		psiLadingBill.setOldItemIds(itemIds);

		// 查出该供应商可提单产品 所在订单编号 数量 可提单数量 = 订单数-已提单数-预提单数 >=0
		// 对等于零的进行过滤，匹配本提货单的item，其他的不要
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

		Set<String> skus = Sets.newHashSet();
		Map<String, Set<String>> skuMap = this.productService.getSkuMapByProduct(productIdSet, skus);
		Map<String, String> fnskuMap = this.productService.getSkuAndFnskuMap(skus);
		//添加质检员质检时间
		List<User> userList = systemService.findUserByPermission("psi:ladingBill:qualityTest");
		if(userList!=null){
			model.addAttribute("testUsers", userList);
		}
				
		model.addAttribute("skuMap", JSON.toJSON(skuMap));
		model.addAttribute("fnskuMap", JSON.toJSON(fnskuMap));
		model.addAttribute("ladingMap", JSON.toJSON(ladingMap));
		model.addAttribute("productMap", JSON.toJSON(productMap));
		model.addAttribute("totalMap", JSON.toJSON(totalMap));
		model.addAttribute("products", products);
		model.addAttribute("psiLadingBill", psiLadingBill);
		model.addAttribute("tranSuppliers", tranSuppliers);
		return "modules/psi/lc/lcPsiLadingBillEdit";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "editSave")
	public String editSave(LcPsiLadingBill psiLadingBill, Model model,
			RedirectAttributes redirectAttributes) {
		if (psiLadingBill.getItems() != null) {
			PsiSupplier supplier = this.psiSupplierService.get(psiLadingBill
					.getSupplier().getId());
			this.psiLadingBillService.editSave(psiLadingBill, supplier);
			addMessage(redirectAttributes, "编辑提单'" + psiLadingBill.getBillNo()	+ "'成功");
			return "redirect:" + Global.getAdminPath()	+ "/psi/lcPsiLadingBill/?repage";
		} else {
			addMessage(redirectAttributes, "编辑提单失败,提单项为空");
			return "redirect:" + Global.getAdminPath()	+ "/psi/lcPsiLadingBill/?repage";
		}
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "sure")
	public String sure(LcPsiLadingBill psiLadingBill, Model model) {
		List<String> products = new ArrayList<String>();
		Map<String, List<PsiLadingBillItemDto>> ladingMap = Maps.newHashMap();
		psiLadingBill = this.psiLadingBillService.get(psiLadingBill.getId());
		Set<Integer> productIdSet = Sets.newHashSet();
		
		//获取已质检总数
		Map<String,Integer> canLadingMap=this.testService.getCanLadingQuantity(psiLadingBill.getId());
		for (LcPsiLadingBillItem item : psiLadingBill.getItems()) {
			// 组合map
			String key = item.getProductName()+ "|"	+ this.psiLadingBillService.changeCountryToUs(item.getCountryCode());
			if (StringUtils.isNotEmpty(item.getColorCode())) {
				key += "|" + item.getColorCode();
			}
			List<PsiLadingBillItemDto> itemList = null;
			if (ladingMap.get(key) == null) {
				itemList = Lists.newArrayList();
			} else {
				itemList = ladingMap.get(key);
			}
			itemList.add(new PsiLadingBillItemDto(item));
			ladingMap.put(key, itemList);
			productIdSet.add(item.getPurchaseOrderItem().getProduct().getId());
			//处理质检数
			String proColor = item.getProductNameColor();
			Integer total = canLadingMap.get(proColor);
			if(total!=null){
				total=total-item.getQuantitySure();//减去已确认数
				canLadingMap.put(proColor, total);
			}
		}
		
		
		List<String> newProducts =this.productService.findNewProducts();
		Set<String> skus = Sets.newHashSet();
		Map<String, Set<String>> skuMap = this.productService.getSkuMapByProduct(productIdSet, skus);
		Map<String, String> fnskuMap = this.productService.getSkuAndFnskuMap(skus);
		model.addAttribute("newProducts",JSON.toJSON(newProducts));
		model.addAttribute("skuMap", JSON.toJSON(skuMap));
		model.addAttribute("fnskuMap", JSON.toJSON(fnskuMap));
		model.addAttribute("ladingMap", JSON.toJSON(ladingMap));
		model.addAttribute("canLadingMap", JSON.toJSON(canLadingMap));
		model.addAttribute("products", products);
		model.addAttribute("psiLadingBill", psiLadingBill);
		return "modules/psi/lc/lcPsiLadingBillSure";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "sureSave")
	public String sureSave(@RequestParam("attchmentFile") MultipartFile[] attchmentFiles,LcPsiLadingBill psiLadingBill, Model model,RedirectAttributes redirectAttributes) {
		String res = "";
		synchronized (this) {
			Map<Integer, String> skuMap = Maps.newHashMap();
			for (LcPsiLadingBillItem item : psiLadingBill.getItems()) {
				skuMap.put(item.getId(),item.getSku()+ ",,,"+ item.getQuantitySureTemp()+ ",,,"	+ item.getRemark()+ " ,,,"+ (item.getQuantitySpares() == null ? "" : item.getQuantitySpares()));
			}
			Date actualDeliveryDate = psiLadingBill.getActualDeliveryDate();
			String tranMan = psiLadingBill.getTranMan();
			String phone = psiLadingBill.getPhone();
			String carNo = psiLadingBill.getCarNo();
			
			psiLadingBill = this.psiLadingBillService.get(psiLadingBill.getId());
			psiLadingBill.setActualDeliveryDate(actualDeliveryDate);//保存实际入仓时间
			psiLadingBill.setTranMan(tranMan);//入库表里保存
			psiLadingBill.setPhone(phone);//入库表里保存
			psiLadingBill.setCarNo(carNo);//入库表里保存
			Map<String,Map<String,Object>> avgPriceMap=Maps.newHashMap();
			res = psiLadingBillService.sureSave(psiLadingBill, attchmentFiles,skuMap,avgPriceMap);
			this.psiLadingBillService.updateAvgPrice(avgPriceMap);
		}
		if (StringUtils.isNotEmpty(res)) {
			addMessage(redirectAttributes,"error:确认提单" + psiLadingBill.getBillNo() + "失败，原因为：" + res);
		} else {
			addMessage(redirectAttributes, "确认提单'" + psiLadingBill.getBillNo()	+ "'成功");
		}
		return "redirect:" + Global.getAdminPath()+ "/psi/lcPsiLadingBill/?repage";
	}

	@RequestMapping(value = "pass")
	public String pass(LcPsiLadingBill psiLadingBill, Model model) {
		List<String> products = new ArrayList<String>();
		Map<String, List<PsiLadingBillItemDto>> ladingMap = Maps.newHashMap();
		psiLadingBill = this.psiLadingBillService.get(psiLadingBill.getId());
		Set<Integer> productIdSet = Sets.newHashSet();
		for (LcPsiLadingBillItem item : psiLadingBill.getItems()) {
			// 组合map
			String key = item.getProductName()+ "|"	+ this.psiLadingBillService.changeCountryToUs(item.getCountryCode());
			if (StringUtils.isNotEmpty(item.getColorCode())) {
				key += "|" + item.getColorCode();
			}
			List<PsiLadingBillItemDto> itemList = null;
			if (ladingMap.get(key) == null) {
				itemList = Lists.newArrayList();
			} else {
				itemList = ladingMap.get(key);
			}
			itemList.add(new PsiLadingBillItemDto(item));
			ladingMap.put(key, itemList);
			productIdSet.add(item.getPurchaseOrderItem().getProduct().getId());
		}
		Set<String> skus = Sets.newHashSet();
		Map<String, Set<String>> skuMap = this.productService.getSkuMapByProduct(productIdSet, skus);
		Map<String, String> fnskuMap = this.productService.getSkuAndFnskuMap(skus);
		model.addAttribute("skuMap", JSON.toJSON(skuMap));
		model.addAttribute("fnskuMap", JSON.toJSON(fnskuMap));
		model.addAttribute("ladingMap", JSON.toJSON(ladingMap));
		model.addAttribute("products", products);
		model.addAttribute("psiLadingBill", psiLadingBill);
		model.addAttribute("now", new Date());
		return "modules/psi/lc/lcPsiLadingBillPass";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "passSave")
	public String passSave(LcPsiLadingBill psiLadingBill, Model model,
			RedirectAttributes redirectAttributes) {
		Map<Integer, String> passMap = Maps.newHashMap();
		Map<Integer, LcPsiLadingBillItem> itemMap = Maps.newHashMap();
		for (LcPsiLadingBillItem item : psiLadingBill.getItems()) {
			passMap.put(item.getId(), item.getIsPass());
			if (item.getQuantityActual() > 0) {
				if (item.getQuantityActual() + item.getQuantityGoods() > item.getQuantityLading()) {
					logger.info("验货总数大于收货单总数量,id:" + item.getId() + ",本次验货数量"+ item.getQuantityActual());
					addMessage(redirectAttributes,"验货总数大于收货单总数量,产品信息：" + item.getProductConName()+ ",本次验货数量：" + item.getQuantityActual());
					return "redirect:" + Global.getAdminPath()+ "/psi/lcPsiLadingBill/?repage";
				} else {
					itemMap.put(item.getId(), item);
				}
			}
		}

		psiLadingBill = this.psiLadingBillService.get(psiLadingBill.getId());
		// 找出为不合格的订单号，和产品名
		Set<String> orderNos = Sets.newHashSet();
		Set<String> productNames = Sets.newHashSet();
		String orderNoStr = "";
		String productNameStr = "";
		String isPass = "1";
		for (LcPsiLadingBillItem item : psiLadingBill.getItems()) {
			item.setIsPass(passMap.get(item.getId()));
			String result = "合格";
			if ("0".equals(item.getIsPass())) {
				isPass = "0";
				result = "不合格";
				productNames.add(item.getProductNameColor());
				orderNos.add(item.getPurchaseOrderItem().getPurchaseOrder()
						.getOrderNo());
			}
			if (itemMap.get(item.getId()) != null) {

				LcPsiLadingBillItem itemTemp = itemMap.get(item.getId());
				if ("1".equals(item.getIsPass())) {// 合格后统计验收总数
					int oldQuantityGoods = item.getQuantityGoods() == null ? 0
							: item.getQuantityGoods();
					item.setQuantityGoods(oldQuantityGoods
							+ itemTemp.getQuantityActual());
				}
				if (StringUtils.isEmpty(item.getHisRecord())) {
					item.setHisRecord("验货数量:" + itemTemp.getQuantityActual()
							+ ";时间:" + itemTemp.getQualityDate() + ";结果:"
							+ result);
				} else {
					item.setHisRecord(item.getHisRecord() + "<br/>验货数量:"
							+ itemTemp.getQuantityActual() + ";时间:"
							+ itemTemp.getQualityDate() + ";结果:" + result);
				}
			}
		}
		if (productNames.size() > 0) {
			orderNoStr = orderNos.toString().substring(1,
					orderNos.toString().length() - 1);
			productNameStr = productNames.toString().substring(1,
					productNames.toString().length() - 1);
		}
		this.psiLadingBillService.save(psiLadingBill);
		return "redirect:" + Global.getAdminPath()
				+ "/psi/psiQuestionSupplier/form?productName=" + productNameStr
				+ "&orderNo=" + orderNoStr + "&supplier.id="
				+ psiLadingBill.getSupplier().getId() + "&isPass=" + isPass;
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "uploadPi")
	public String uploadPi(LcPsiLadingBill psiLadingBill, Model model) {
		psiLadingBill = this.psiLadingBillService.get(psiLadingBill.getId());
		model.addAttribute("psiLadingBill", psiLadingBill);
		return "modules/psi/lc/lcPsiLadingBillUploadPi";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "uploadPiSave")
	public String uploadPiSave(
			@RequestParam("attchmentFile") MultipartFile[] attchmentFiles,
			LcPsiLadingBill psiLadingBill, Model model,
			RedirectAttributes redirectAttributes) {
		psiLadingBill = this.psiLadingBillService.get(psiLadingBill.getId());
		for (MultipartFile attchmentFile : attchmentFiles) {
			if (attchmentFile.getSize() != 0) {
				String baseDirStr = ContextLoader
						.getCurrentWebApplicationContext().getServletContext()
						.getRealPath("/")
						+ Global.getCkBaseDir() + "/psi/ladingBills";
				File baseDir = new File(baseDirStr + "/"
						+ psiLadingBill.getBillNo());
				if (!baseDir.isDirectory())
					baseDir.mkdirs();
				String suffix = attchmentFile.getOriginalFilename().substring(
						attchmentFile.getOriginalFilename().lastIndexOf("."));
				String name = UUID.randomUUID().toString() + suffix;
				File dest = new File(baseDir, name);
				try {
					FileUtils.copyInputStreamToFile(
							attchmentFile.getInputStream(), dest);
					psiLadingBill.setAttchmentPathAppend("/psi/ladingBills/"
							+ psiLadingBill.getBillNo() + "/" + name);
				} catch (IOException e) {
					logger.warn(name + "文件保存失败", e);
				}
			}
		}
		this.psiLadingBillService.save(psiLadingBill);
		addMessage(redirectAttributes, "提单'" + psiLadingBill.getBillNo()
				+ "'上传凭证成功");
		return "redirect:" + Global.getAdminPath()
				+ "/psi/lcPsiLadingBill/?repage";
	}
	

	@RequiresPermissions("psi:all:view")
	@ResponseBody
	@RequestMapping(value = "print")
	public String print(LcPsiLadingBill ladingBill, HttpServletResponse response)
			throws Exception {
		ladingBill = this.psiLadingBillService.get(ladingBill.getId());
		if (filePath == null) {
			filePath = ContextLoader.getCurrentWebApplicationContext()
					.getServletContext().getRealPath("/")
					+ Global.getCkBaseDir() + "/psi/ladingBills";
		}
		File file = new File(filePath, ladingBill.getBillNo());
		if (!file.exists()) {
			file.mkdirs();
		}
		File pdfFile = new File(file, ladingBill.getBillNo() + ".pdf");
		Set<String> skus = Sets.newHashSet();
		for (LcPsiLadingBillItem item : ladingBill.getItems()) {
			if (StringUtils.isNotEmpty(item.getSku())) {
				skus.add(item.getSku());
			}
		}
		// Map<String,String> versionMap =
		// productService.getFnskuAndSkuVersionMap();
		Map<String, String> fnskuMap = productService.getSkuAndFnskuMap(skus);
		PdfUtil.genPsiLadingBillPdf(pdfFile, ladingBill, fnskuMap,null,null);
		FileInputStream in = new FileInputStream(pdfFile);
		ServletOutputStream out = response.getOutputStream();
		response.setContentType("application/pdf");// pdf文件
		response.addHeader("Content-Disposition",
				"filename=" + ladingBill.getBillNo() + ".pdf");
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
	public String view(LcPsiLadingBill psiLadingBill, Model model) {
		Map<String, List<PsiLadingBillItemDto>> ladingMap = new HashMap<String, List<PsiLadingBillItemDto>>();
		if (psiLadingBill.getId() != null) {
			psiLadingBill = this.psiLadingBillService
					.get(psiLadingBill.getId());
		} else if (psiLadingBill.getBillNo() != null) {
			psiLadingBill = this.psiLadingBillService.get(psiLadingBill
					.getBillNo());
		} else {
			return null;
		}

		StringBuilder sb = new StringBuilder("");
		for (LcPsiLadingBillItem item : psiLadingBill.getItems()) {
			// 组合map
			String key = item.getProductName()
					+ "|"
					+ this.psiLadingBillService.changeCountryToUs(item
							.getCountryCode());
			if (StringUtils.isNotEmpty(item.getColorCode())) {
				key += "|" + item.getColorCode();
			}
			List<PsiLadingBillItemDto> itemList = null;
			if (ladingMap.get(key) == null) {
				itemList = Lists.newArrayList();
			} else {
				itemList = ladingMap.get(key);
			}
			itemList.add(new PsiLadingBillItemDto(item));
			ladingMap.put(key, itemList);
			sb.append(item.getId() + ",");
		}

		String itemIds = "";
		if (StringUtils.isNotEmpty(sb.toString())) {
			itemIds = sb.toString().substring(0, sb.toString().length() - 1);
		}
		psiLadingBill.setOldItemIds(itemIds);
		//应付款时间
		String payType = psiLadingBill.getSupplier().getPayType();
		Date firstDay = psiLadingBillService.getPayDays(payType, psiLadingBill.getCreateDate());
		model.addAttribute("firstDay", firstDay);
		
		List<LcPsiQualityTest> tests=this.testService.getTestByProductNameColor(psiLadingBill.getId(), null, null);        
		   
		model.addAttribute("tests", tests);
		model.addAttribute("ladingMap", JSON.toJSON(ladingMap));
		model.addAttribute("psiLadingBill", psiLadingBill);
		return "modules/psi/lc/lcPsiLadingBillView";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping("/download")
	public ModelAndView download(String fileName, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		fileName = HtmlUtils.htmlUnescape(fileName);
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("UTF-8");
		java.io.BufferedInputStream bis = null;
		java.io.BufferedOutputStream bos = null;
		String ctxPath = ContextLoader.getCurrentWebApplicationContext()
				.getServletContext().getRealPath("/")
				+ Global.getCkBaseDir();
		String downLoadPath = ctxPath + fileName;
		fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		try {
			long fileLength = new File(downLoadPath).length();
			response.setContentType("application/x-msdownload;");
			response.setHeader("Content-disposition", "attachment; filename="
					+ URLEncoder.encode(fileName, "utf-8"));
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
		// 取消提单 把订单item预接收数量 - 该提单的数量
		LcPsiLadingBill bill = this.psiLadingBillService.get(id);
		if (this.psiLadingBillService.cancelBill(bill)) {
			bill.setBillSta("2");// 已取消
			bill.setCancelDate(new Date());
			bill.setCancelUser(UserUtils.getUser());
			this.psiLadingBillService.save(bill);
		}
		;

		addMessage(redirectAttributes, "取消提单成功");
		return "redirect:" + Global.getAdminPath()
				+ "/psi/lcPsiLadingBill/?repage";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "delete")
	public String delete(String id, RedirectAttributes redirectAttributes) {
		psiLadingBillService.delete(id);
		addMessage(redirectAttributes, "删除提单成功");
		return "redirect:" + Global.getAdminPath()
				+ "/modules/psi/lc/lcPsiLadingBill/?repage";
	}

	/**
	 * 收货明细
	 * 
	 * @throws ParseException
	 * 
	 */
	@RequestMapping(value = "receiptCargoList")
	public String receiptCargoList(LcPsiLadingBill ladingBill,
			HttpServletRequest request, HttpServletResponse response,
			Model model) throws ParseException {

		if (ladingBill.getBillSta() == null) {
			Date today = sdf.parse(sdf.format(new Date()));
			if (ladingBill.getCreateDate() == null) {
				ladingBill.setCreateDate(DateUtils.addMonths(today, -1));
				ladingBill.setSureDate(today);
			}
			ladingBill.setBillSta("");
		}

		List<PsiSupplier> suppliers = this.psiSupplierService.findAll();
		Page<LcPsiLadingBill> page = this.psiLadingBillService
				.findReceiptCargoList(new Page<LcPsiLadingBill>(request,
						response), ladingBill);
		Map<String, LcPsiLadingBill> productMap = Maps.newLinkedHashMap();
		if (page.getList().size() > 0) {
			List<LcPsiLadingBill> bills = page.getList();
			for (LcPsiLadingBill bill : bills) {
				for (LcPsiLadingBillItem item : bill.getItems()) {
					LcPsiLadingBill temp = new LcPsiLadingBill();
					if (productMap.get(item.getProductName()) == null) {
						temp.setTempQuantity(item.getQuantityLading());
						productMap.put(item.getProductName(), temp);
					} else {
						temp = productMap.get(item.getProductName());
						temp.setTempQuantity(temp.getTempQuantity()
								+ item.getQuantityLading());
					}
					temp.getItems().add(item);
				}
			}

		}

		model.addAttribute("suppliers", suppliers);
		model.addAttribute("productMap", productMap);
		model.addAttribute("psiLadingBill", ladingBill);

		// model.addAttribute("page", page);
		return "modules/psi/lc/lcPurchaseReceiptCargoList";
	}

	/**
	 * 收货/预收货明细 (运单试算)
	 * 
	 * @throws ParseException
	 * 
	 */
	@RequestMapping(value = "wayBillList")
	public String wayBillList(LcPsiLadingBill ladingBill,
			HttpServletRequest request, HttpServletResponse response,
			Model model) throws ParseException {
		if (StringUtils.isEmpty(ladingBill.getBillNo())) {
			ladingBill.setBillNo("de");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (ladingBill.getBillSta() == null) {
			Date today = sdf.parse(sdf.format(new Date()));
			if (ladingBill.getCreateDate() == null) {
				ladingBill.setCreateDate(today);
				ladingBill.setSureDate(DateUtils.addMonths(today, 1));
			}
			ladingBill.setBillSta("");
		}

		List<PsiSupplier> suppliers = this.psiSupplierService.findAll();
		// List<LcPsiLadingBill> list =
		// this.psiLadingBillService.findWayBillList(ladingBill);
		List<LcPurchaseOrder> list = this.purchaseOrderService
				.findWayBillList(ladingBill);
		Map<String, String[]> productMap = Maps.newHashMap();
		Map<String, List<LcPurchaseOrderItem>> orderMap = Maps.newHashMap();

		Integer supplierId = null;
		if (ladingBill.getSupplier() != null
				&& ladingBill.getSupplier().getId() != null) {
			supplierId = ladingBill.getSupplier().getId();
		}
		// 查询中国仓的数据
		Map<String, Integer> productNameMap = this.inventoryService
				.findByStockAndCountry(130, ladingBill.getBillNo(), supplierId);

		if (list.size() > 0) {
			for (LcPurchaseOrder order : list) {
				for (LcPurchaseOrderItem item : order.getItems()) {
					if (item.getQuantityUnReceived() > 0) {
						Integer unReceivedQuantity = item
								.getQuantityUnReceived();
						if (item.getCountryCode().equals(ladingBill.getBillNo())|| ("eu".equals(ladingBill.getBillNo()) && "de,fr,it,es,uk,".contains(item.getCountryCode() + ","))) {
							if (productNameMap.get(item.getProductName()) != null) {
								unReceivedQuantity += productNameMap.get(item.getProductName());
							}
							productNameMap.put(item.getProductName(),unReceivedQuantity);
						}

						List<LcPurchaseOrderItem> orderItems = null;
						if (orderMap.get(item.getProductName()) == null) {
							if (item.getCountryCode().equals(
									ladingBill.getBillNo())
									|| ("eu".equals(ladingBill.getBillNo()) && "de,fr,it,es,uk,"
											.contains(item.getCountryCode()
													+ ","))) {
								orderItems = Lists.newLinkedList();
								orderMap.put(item.getProductName(), orderItems);
							}
						} else {

							if (item.getCountryCode().equals(
									ladingBill.getBillNo())
									|| ("eu".equals(ladingBill.getBillNo()) && "de,fr,it,es,uk,"
											.contains(item.getCountryCode()
													+ ","))) {
								orderItems = orderMap
										.get(item.getProductName());
							}
						}
						if (orderItems != null) {
							orderItems.add(item);
							orderMap.put(item.getProductName(), orderItems);
						}
					}
				}
			}
		}
		Map<String, PsiProduct> proMap = Maps.newHashMap();
		List<PsiProduct> pros = psiProductService.findAll();
		for (PsiProduct pro : pros) {
			String proName = pro.getName();
			proMap.put(proName, pro);
		}
		// 组成页面显示 数量 体积 重量

		 for(Map.Entry<String,Integer> entry:productNameMap.entrySet()){
	    	String productName = entry.getKey();
			PsiProduct product = proMap.get(productName);
			if(product==null||productNameMap.get(productName)==null||productNameMap==null){
				continue;
			}
			String volume = product.getBoxVolume().floatValue()	* (productNameMap.get(productName) /(float)product.getPackQuantity()) + "";
			String weight = product.getGw().floatValue()* (productNameMap.get(productName) / (float)product.getPackQuantity()) + "";
			productMap.put(productName,
					new String[] { productNameMap.get(productName) + "",volume, weight });
		}
		model.addAttribute("suppliers", suppliers);
		model.addAttribute("orderMap", orderMap);
		model.addAttribute("productMap", productMap);
		model.addAttribute("psiLadingBill", ladingBill);
		return "modules/psi/lc/lcPurchaseWayCountList";
	}

	/**
	 * 收货/预收货明细 (运单试算)
	 * 
	 */
	@RequestMapping(value = "trial")
	public String trial(HttpServletRequest request,
			HttpServletResponse response, Model model) {
		// 查询所有产品
		Map<Integer, PsiProductDto> proMap = Maps.newHashMap();
		List<PsiProduct> products = productService.findAll();
		for (int i = 0; i < products.size(); i++) {
			PsiProduct pro = products.get(i);
			proMap.put(
					pro.getId(),
					new PsiProductDto(pro.getId(), pro.getName(), pro
							.getPackQuantity(), pro.getBoxVolume(), pro.getGw()));
		}
		model.addAttribute("proMap", JSON.toJSON(proMap));
		return "modules/psi/lc/lcTranTrial";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "exp")
	public String exp(LcPsiLadingBill psiLadingBill,
			HttpServletRequest request, HttpServletResponse response,
			Model model) throws UnsupportedEncodingException {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		int excelNo = 1;
		Map<String, Integer> packMap = this.psiProductService
				.findPackQuantityMap();
		List<LcPsiLadingBill> list = psiLadingBillService.exp(psiLadingBill);

		if (list.size() > 65535) {
			throw new RuntimeException("一次导出的最大数据不能超过65535条！请分两次导出！");
		}

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);

		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		String[] title = { " 产品名 ", "收货日期", " 仓位 ", "供应商", "  品名  ",
				"    型号     ", "数量", " MPQ ", "件数", "PurchaseOrderNO",
				"  DE  ", "  UK  ", "  FR  ", "  IT  ", "  ES  ", "  JP  ",
				"  US  ", "  CA  " };

		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// 设置Excel中的边框(表头的边框)
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);

		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);

		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);

		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		// 设置字体
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
		for (LcPsiLadingBill bilTrue : list) {
			List<LcPsiLadingBill> tempBills = bilTrue.getTempLadingBills();
			for (int i = 0; i < tempBills.size(); i++) {
				row = sheet.createRow(excelNo++);
				LcPsiLadingBill tempBill = tempBills.get(i);

				List<LcPsiLadingBillItem> billItems = tempBill.getItems();
				String productName = "";
				Map<String, Integer> itemQuantityMap = Maps.newHashMap();
				Set<Integer> orderItemSet = Sets.newHashSet();
				for (int j = 0; j < billItems.size(); j++) {
					LcPsiLadingBillItem item = billItems.get(j);
					if (j == 0) {
						productName = item.getProductName();
					}

					if (itemQuantityMap.get(item.getCountryCode()) != null) {
						itemQuantityMap.put(item.getCountryCode(),item.getQuantityLading()+ itemQuantityMap.get(item.getCountryCode()));
					} else {
						itemQuantityMap.put(item.getCountryCode(),item.getQuantityLading());
					}
					orderItemSet.add(item.getId());
				}

				row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(productName);
				row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(sdf1.format(tempBill.getActualDeliveryDate()!=null?tempBill.getActualDeliveryDate():tempBill.getDeliveryDate())); // 创建日期
				row.createCell(2, Cell.CELL_TYPE_STRING).setCellValue("深圳仓");
				row.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(
						tempBill.getSupplier().getNikename());
				row.createCell(4, Cell.CELL_TYPE_STRING).setCellValue(
						productName.split(" ")[0]);
				row.createCell(5, Cell.CELL_TYPE_STRING).setCellValue(
						productName.split(" ")[1]);
				row.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(
						tempBill.getLadingTotal());
				row.createCell(7, Cell.CELL_TYPE_STRING).setCellValue(
						packMap.get(productName));
				row.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(
						(int) (tempBill.getLadingTotal()/(float)packMap.get(productName)));
				row.createCell(9, Cell.CELL_TYPE_STRING).setCellValue(
						tempBill.getBillNo());

				if (itemQuantityMap.get("de") != null) {
					Integer quantity = itemQuantityMap.get("de");
					row.createCell(10, Cell.CELL_TYPE_NUMERIC).setCellValue(
							quantity);
				} else {
					row.createCell(10, Cell.CELL_TYPE_NUMERIC).setCellValue("");
				}

				if (itemQuantityMap.get("uk") != null) {
					Integer quantity = itemQuantityMap.get("uk");
					row.createCell(11, Cell.CELL_TYPE_NUMERIC).setCellValue(
							quantity);
				} else {
					row.createCell(11, Cell.CELL_TYPE_NUMERIC).setCellValue("");
				}

				if (itemQuantityMap.get("fr") != null) {
					Integer quantity = itemQuantityMap.get("fr");
					row.createCell(12, Cell.CELL_TYPE_NUMERIC).setCellValue(
							quantity);
				} else {
					row.createCell(12, Cell.CELL_TYPE_NUMERIC).setCellValue("");
				}

				if (itemQuantityMap.get("it") != null) {
					Integer quantity = itemQuantityMap.get("it");
					row.createCell(13, Cell.CELL_TYPE_NUMERIC).setCellValue(
							quantity);
				} else {
					row.createCell(13, Cell.CELL_TYPE_NUMERIC).setCellValue("");
				}

				if (itemQuantityMap.get("es") != null) {
					Integer quantity = itemQuantityMap.get("es");
					row.createCell(14, Cell.CELL_TYPE_NUMERIC).setCellValue(
							quantity);
				} else {
					row.createCell(14, Cell.CELL_TYPE_NUMERIC).setCellValue("");
				}

				if (itemQuantityMap.get("jp") != null) {
					Integer quantity = itemQuantityMap.get("jp");
					row.createCell(15, Cell.CELL_TYPE_NUMERIC).setCellValue(
							quantity);
				} else {
					row.createCell(15, Cell.CELL_TYPE_NUMERIC).setCellValue("");
				}

				if (itemQuantityMap.get("com") != null) {
					Integer quantity = itemQuantityMap.get("com");
					row.createCell(16, Cell.CELL_TYPE_NUMERIC).setCellValue(
							quantity);
				} else {
					row.createCell(16, Cell.CELL_TYPE_NUMERIC).setCellValue("");
				}

				if (itemQuantityMap.get("ca") != null) {
					Integer quantity = itemQuantityMap.get("ca");
					row.createCell(17, Cell.CELL_TYPE_NUMERIC).setCellValue(
							quantity);
				} else {
					row.createCell(17, Cell.CELL_TYPE_NUMERIC).setCellValue("");
				}
			}
		}

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");

		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		String fileName = "LadingBillData" + sdf.format(new Date()) + ".xls";
		fileName = URLEncoder.encode(fileName, "UTF-8");
		response.addHeader("Content-Disposition", "attachment;filename="
				+ fileName);
		try {
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private void sendEmailToQc(LcPsiLadingBill psiLadingBill,PsiSupplier supplier) throws Exception {
		String subject="提单["+psiLadingBill.getBillNo()+"]已创建,请及时安排品检,并登陆erp填写品检信息";
		String content="Hi,All<br/>"+subject+"<br/>";
		this.psiLadingBillService.sendNoticeEmail("qc@inateck.com", content, subject, "", "");
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
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "qualityTest")
	public String qualityTest(LcPsiQualityTest test, Model model) {
		if(test.getId()!=null){
			test=this.testService.get(test.getId());
		}
		//查出该提单，该产品总数：    减去已质检接收数
		Integer  ladingQ =this.psiLadingBillService.getTotalByProductsColor(test.getLadingId(), test.getProductNameColor());
		Integer testQ = this.psiLadingBillService.getTotalTestByProductsColor(test.getLadingId(), test.getProductNameColor());
		
		//查询最近一笔付款的时间
		String canEdit="1";
		if(test.getId()!=null){
			Date lastDate=this.psiLadingBillService.getLastPayDate(test.getLadingBillNo(), test.getProductName(), test.getColor());
			if(lastDate!=null&&lastDate.compareTo(test.getCreateDate())>0){
				canEdit="0";
			}
		}
		
		model.addAttribute("canEdit", canEdit);
		model.addAttribute("totalQuantity", ladingQ-testQ);
		model.addAttribute("test", test);
		return "modules/psi/lc/lcPsiLadingBillTest";
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"qualityRemarkSave"})
	public String qualityRemarkSave(Integer id,String remark,String flag) {
		try {
			if(id==null||StringUtils.isEmpty(remark)){
				return "false";
			}
			return this.testService.updateRemark(id, URLDecoder.decode(remark, "UTF-8"),flag);
		} catch (UnsupportedEncodingException e) {
			return "false";
		}
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "qualityTestSave")
	public String qualityTestSave(LcPsiQualityTest test,@RequestParam(value="reportPath", required = false) MultipartFile[] reportPaths, Model model,RedirectAttributes redirectAttributes) {
	    if("0".equals(test.getIsOk())){
		    PsiQuestionSupplier supplier = new PsiQuestionSupplier();
		    try {
                supplier.setQuestionDate(new SimpleDateFormat("yyyy-MM-dd").parse(new SimpleDateFormat("yyyy-MM-dd kk:mm:ss ").format(new Date())));
            } catch (ParseException e) {
                logger.error("日期转换失败",e);
                return "日期转换失败";
            }
		    User user = new User("1");
		    supplier.setCreateUser(user);
		    supplier.setSupplier(new PsiSupplier(test.getSupplierId()));
		    supplier.setProductName(test.getProductName());
		    supplier.setResult("NG");
		    supplier.setOrderNo(test.getLadingBillNo());
		    StringBuilder sb = new StringBuilder();
		    if(StringUtils.isNotBlank(test.getInView())){
		        sb.append("内观："+test.getInView()+" ");
		    }
		    if(StringUtils.isNotBlank(test.getFunction())){
                sb.append("功能："+test.getFunction()+" ");
            }
		    if(StringUtils.isNotBlank(test.getOutView())){
                sb.append("外观："+test.getOutView()+" ");
            }
		    if(StringUtils.isNotBlank(test.getPacking())){
                sb.append("包装："+test.getPacking()+" ");
            }
		    supplier.setEvent(sb.toString());
		    String deal = "2".equals(test.getDealWay()) ? " 直接返工 " : " 各方协商 ";
		    supplier.setDeal(deal);
		    psiQuestionSupplierService.save(supplier, reportPaths);
		}
	    testService.qualityTestSave(test, reportPaths);
		addMessage(redirectAttributes, "提单'" + test.getLadingBillNo()+ "'产品["+test.getProductNameColor()+"]添加质检记录成功");
		return "redirect:" + Global.getAdminPath()+ "/psi/lcPsiLadingBill/?repage";
	}

	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "qualityTestReviewSave")
	public String qualityReviewSave(LcPsiQualityTest test,@RequestParam("giveInPath") MultipartFile[] giveInPaths, Model model,RedirectAttributes redirectAttributes) {
		testService.qualityTestReviewSave(test, giveInPaths);
		addMessage(redirectAttributes, "序号：["+test.getId() +"]不合格处理成功");
		return "redirect:" + Global.getAdminPath()+ "/psi/lcPsiLadingBill/testList/?repage";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "qualityView")
	public String qualityView(Integer ladingId,String ladingBillNo,String productName,String color, Model model) {
		String nameColor = productName;
		if(StringUtils.isNotEmpty(color)){
			nameColor=nameColor+"_"+color;
		}
		Date payDate=this.psiLadingBillService.getLastPayDate(ladingBillNo, productName, color);
		List<LcPsiQualityTest> tests=this.testService.getTestByProductNameColor(ladingId, productName, color);
		
		model.addAttribute("payDate", payDate);
		model.addAttribute("ladingBillNo", ladingBillNo);
		model.addAttribute("productName", productName);
		model.addAttribute("color", color);
		model.addAttribute("nameColor", nameColor);
		model.addAttribute("tests", tests);
		return "modules/psi/lc/lcPsiLadingBillTestView";
	}
	
	
	
	@RequiresPermissions("psi:ladingBill:managerTest")
	@RequestMapping(value = "managerReivew")
	public String managerReivew(String productName, Model model) {
		//查询该产品经理所有可以审核的产品
//		if(StringUtils.isEmpty(oldUserId)&&StringUtils.isNotEmpty(productName)){
//			//根据产品名获取对应的产品经理
//			oldUserId=this.productService.findMangerByProductName(productName);
//		}
//		if(StringUtils.isEmpty(oldUserId)){
//			return "该产品无产品经理，请进行匹配";
//		}
//		List<String> productNames=this.productService.findProductByManagerId(oldUserId);
//		List<LcPsiQualityTest> tests=null;
//		if(productNames!=null){
//			tests=this.testService.getNoReviewTest(productNames);
//		}
		
		//测试品检单的id集合
		List<LcPsiQualityTest> tests=this.testService.getNoReviewTest(null);
//		StringBuilder ids= new StringBuilder();
//		for(LcPsiQualityTest test:tests){
//			ids.append(test.getId()).append(",");
//		}
//		if(StringUtils.isNotEmpty(ids)){
//			ids=new StringBuilder(ids.substring(0, ids.length()-1));
//		}
//		model.addAttribute("ids", ids.toString());
		model.addAttribute("tests", tests);
		return "modules/psi/lc/lcPsiLadingBillManagerReview";
	}
	
	
	/**
	 * 产品合格率统计
	 */
	@RequestMapping(value = "testCount")
	public String testCount(String startDate,String endDate, Model model) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(StringUtils.isEmpty(endDate)){
			endDate=sdf.format(new Date());
			startDate=sdf.format(DateUtils.addMonths(sdf.parse(endDate), -3));
		}
		Map<String,String> supplierMap=this.testService.getSupplierProduct();
		Map<String,Object[]> okRate=this.testService.getOkRate(sdf.parse(startDate), sdf.parse(endDate));
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("okRate", okRate);
		model.addAttribute("supplierMap", supplierMap);
		return "modules/psi/lc/lcPsiLadingBillTestCount";
	}
	
	@RequestMapping(value = "testCountSupplier")
	public String testCountSupplier(String startDate,String endDate, Model model) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(StringUtils.isEmpty(endDate)){
			endDate=sdf.format(new Date());
			startDate=sdf.format(DateUtils.addMonths(sdf.parse(endDate), -3));
		}
		Map<Integer,PsiSupplier> supplierMap=this.psiSupplierService.findAllMap();
		List<Object[]> rateInfos=this.testService.getOkRateBySupplier(sdf.parse(startDate), sdf.parse(endDate));
		model.addAttribute("startDate", startDate);
		model.addAttribute("endDate", endDate);
		model.addAttribute("rateInfos", rateInfos);
		model.addAttribute("supplierMap", supplierMap);
		return "modules/psi/lc/lcPsiLadingBillTestSupplierCount";
	}
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "testCountExport")
	public String testCountExport(String startDate,String endDate,HttpServletRequest request, HttpServletResponse response,	Model model) throws UnsupportedEncodingException, ParseException {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		int excelNo = 1;
		Map<String,String> supplierMap=this.testService.getSupplierProduct();
		Map<String,Object[]> rateMap=this.testService.getOkRate(sdf1.parse(startDate), sdf1.parse(endDate));
		if (rateMap.size() > 65535) {
			throw new RuntimeException("一次导出的最大数据不能超过65535条！请分两次导出！");
		}

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);

		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		String[] title = { " 序号 ","供应商","产品名","合格批次","不合格批次","合格率" };

		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// 设置Excel中的边框(表头的边框)
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);

		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);

		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);

		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		
		HSSFCellStyle style1 = wb.createCellStyle();
		HSSFDataFormat df1 = wb.createDataFormat();  //此处设置数据格式  
		style1.setDataFormat(df1.getFormat("#0.00")); //小数点后保留两位，可以写contentStyle.setDataFormat(df.getFormat("#,#0.00"));  
		
		// 设置字体
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
		for (Map.Entry<String, Object[]> entry : rateMap.entrySet()) {
				Object[] info=entry.getValue();
				row = sheet.createRow(excelNo++);
				String productName = info[1].toString();
				String productNameColor = info[0].toString();
				String supplierName = supplierMap.get(productName);

				row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(excelNo - 1);
				row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(supplierName);
				row.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(productNameColor);
				row.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(info[2].toString()));
				row.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(info[3].toString()));
				cell=row.createCell(5,Cell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(style1);
				cell.setCellValue(Float.parseFloat(info[4].toString()));
		}

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");

		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		String fileName = "ProductTestCount" + sdf.format(new Date()) + ".xls";
		fileName = URLEncoder.encode(fileName, "UTF-8");
		response.addHeader("Content-Disposition", "attachment;filename="
				+ fileName);
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
	@RequestMapping(value = "testCountSupplierExport")
	public String testCountSupplierExport(String startDate,String endDate,HttpServletRequest request, HttpServletResponse response,	Model model) throws UnsupportedEncodingException, ParseException {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		int excelNo = 1;
		Map<Integer,PsiSupplier> supplierMap=this.psiSupplierService.findAllMap();
		List<Object[]> rateInfos=this.testService.getOkRateBySupplier(sdf1.parse(startDate), sdf1.parse(endDate));
		if (rateInfos.size() > 65535) {
			throw new RuntimeException("一次导出的最大数据不能超过65535条！请分两次导出！");
		}

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);

		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		String[] title = { " 序号 ","供应商","合格批次","不合格批次","合格率" };

		style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		// 设置Excel中的边框(表头的边框)
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
		style.setBottomBorderColor(HSSFColor.BLACK.index);

		style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
		style.setLeftBorderColor(HSSFColor.BLACK.index);

		style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
		style.setRightBorderColor(HSSFColor.BLACK.index);

		style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		
		HSSFCellStyle style1 = wb.createCellStyle();
		HSSFDataFormat df1 = wb.createDataFormat();  //此处设置数据格式  
		style1.setDataFormat(df1.getFormat("#0.00")); //小数点后保留两位，可以写contentStyle.setDataFormat(df.getFormat("#,#0.00"));  
		
		// 设置字体
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
		for (Object[]  info : rateInfos) {
				row = sheet.createRow(excelNo++);
				Integer supplierId = Integer.parseInt(info[0].toString());
				row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(excelNo - 1);
				row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(supplierMap.get(supplierId).getNikename());
				row.createCell(2, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(info[1].toString()));
				row.createCell(3, Cell.CELL_TYPE_NUMERIC).setCellValue(Integer.parseInt(info[2].toString()));
				cell=row.createCell(4,Cell.CELL_TYPE_NUMERIC);
				cell.setCellStyle(style1);
				cell.setCellValue(Float.parseFloat(info[3].toString()));
		}

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");

		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
		String fileName = "SupplierTestCount" + sdf.format(new Date()) + ".xls";
		fileName = URLEncoder.encode(fileName, "UTF-8");
		response.addHeader("Content-Disposition", "attachment;filename="
				+ fileName);
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
	@RequestMapping(value = "cancelTest")
	public String cancelTest(Integer id, Model model,RedirectAttributes redirectAttributes) {
		if(id!=null){
			LcPsiQualityTest test = this.testService.get(id);
			test.setTestSta("8");
			test.setCancelDate(new Date());
			test.setCancelUser(UserUtils.getUser());
			this.testService.save(test);
		}
		addMessage(redirectAttributes, "取消品检单成功！");
		return "redirect:" + Global.getAdminPath()+ "/psi/lcPsiLadingBill/?repage";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "applyTest")
	public String applyTest(Integer id, Model model,RedirectAttributes redirectAttributes) {
		if(id!=null){
			LcPsiQualityTest test = this.testService.get(id);
			test.setTestSta("3");
			this.testService.save(test);
		}
		addMessage(redirectAttributes, "品检单申请审核成功！");
		return "redirect:" + Global.getAdminPath()+ "/psi/lcPsiLadingBill/?repage";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "cancelTestDraft")
	public String cancelTestDraft(Integer id, Model model,RedirectAttributes redirectAttributes) {
		if(id!=null){
			LcPsiQualityTest test = this.testService.get(id);
			test.setTestSta("0");
			test.setCancelDate(new Date());
			test.setCancelUser(UserUtils.getUser());
			this.testService.save(test);
		}
		addMessage(redirectAttributes, "取消品检单成草稿状态成功！");
		return "redirect:" + Global.getAdminPath()+ "/psi/lcPsiLadingBill/?repage";
	}
	
	/**
	 *产品经理审核后给各方人员发信 
	 * @throws Exception 
	 */
	@RequiresPermissions("psi:ladingBill:managerTest")
	@RequestMapping(value = "managerReivewSave")
	public String managerReivewSave(String ids,Model model,RedirectAttributes redirectAttributes) throws Exception {
//		List<String> productNames=this.productService.findProductByManagerId(oldUserId);
//		if(productNames!=null){
//			this.psiLadingBillService.managerReivewSave(productNames);
//		}
		if(StringUtils.isNotEmpty(ids)){
			this.psiLadingBillService.managerReivewSave(ids);
		}
		addMessage(redirectAttributes, "品质主管审核质检成功");
		return "redirect:" + Global.getAdminPath()+ "/psi/lcPsiLadingBill/?repage";
	}
	
	
	
	
	@ResponseBody
	@RequestMapping(value = {"updateTestDate"})
	public String updateTestDate(Integer ladingId,String testDate) {
		try {
			Date newDate = new Date(testDate);
			LcPsiLadingBill  ladingBill = this.psiLadingBillService.get(ladingId);
			ladingBill.setTestDate(newDate);
			this.psiLadingBillService.save(ladingBill);
		} catch (Exception e) {
			return "false";
		}
		return "true";
	}
	
	
	@ResponseBody
	@RequestMapping(value = {"updateTestUser"})
	public String updateTestUser(Integer ladingId,String testUser) {
		try {
			LcPsiLadingBill  ladingBill = this.psiLadingBillService.get(ladingId);
			ladingBill.setTestUser(UserUtils.getUserById(testUser));
			this.psiLadingBillService.save(ladingBill);
		} catch (Exception e) {
			return "false";
		}
		return "true";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "updateTestFile")
	public String updateTestFile(LcPsiQualityTest test, Model model) {
		if(test.getId()!=null){
			test=this.testService.get(test.getId());
		}
		model.addAttribute("test", test);
		return "modules/psi/lc/lcPsiLadingBillTestFile";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "qualityTestFileSave")
	public String qualityTestFileSave(LcPsiQualityTest test,@RequestParam("reportPath") MultipartFile[] reportPaths, Model model,RedirectAttributes redirectAttributes) {
		if(test.getId()!=null){
			test=testService.get(test.getId());
		}else{
			return null;
		}
		testService.qualityTestFileSave(test, reportPaths);
		addMessage(redirectAttributes, "提单'" + test.getLadingBillNo()+ "'产品["+test.getProductNameColor()+"]更新质检报告成功");
		return "redirect:" + Global.getAdminPath()+ "/psi/lcPsiLadingBill/?repage";
	}
	
	@ResponseBody
	@RequestMapping(value = "uploadFile")
	public List<Object[]> uploadFile(@RequestParam("excel")MultipartFile excelFile,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes, Model model) {
		List<Object[]> list=Lists.newArrayList();
		try {
				Workbook workBook = WorkbookFactory.create(excelFile.getInputStream());
				Sheet sheet = workBook.getSheetAt(0);
				sheet.setForceFormulaRecalculation(true);
				List<PsiProduct> pros= psiProductService.findAll();
				Map<String,PsiProduct> proMap=Maps.newHashMap();
			    for(PsiProduct pro:pros){
			    	String proName = pro.getName();
			    	proMap.put(proName, pro);
			    }
			    Float totalVolume=0f;
			    Float totalWeight=0f;
			    Integer totalQuantity=0;
				for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
					Row row = sheet.getRow(rowNum);
					if (row == null) {
						continue;
					}
					String productName=row.getCell(0).getStringCellValue().trim();
					String name="";
					if(productName.indexOf("_")>0){
						name = productName.substring(0,productName.indexOf("_"));
					}else{
						name=productName;
					}
					Integer quantity=0;
					try{
						quantity=(int) row.getCell(1).getNumericCellValue();
					}catch(Exception e){}
					if(quantity>0){
						PsiProduct product = proMap.get(name);
						if(product!=null){
							Float volume=product.getBoxVolume().floatValue()*(quantity*1.0f/product.getPackQuantity()); 
							Float weight=product.getGw().floatValue()*(quantity*1.0f/product.getPackQuantity());
							totalVolume+=volume;
							totalWeight+=weight;
							totalQuantity+=quantity;
							Object[] obj=new Object[4];
							obj[0]=volume;
							obj[1]=weight;
							obj[2]=quantity;
							obj[3]=productName;
							list.add(obj);
						}
					}
				}	
				Object[] obj=new Object[4];
				obj[0]=totalVolume;
				obj[1]=totalWeight;
				obj[2]=totalQuantity;
				obj[3]="total";
				list.add(0, obj);
		} catch (Exception e) {System.out.println(e.getMessage());}
		return list;
	}
	
	@RequiresPermissions("psi:all:view")
    @RequestMapping(value = "exprotLadingBill")
    public String exprotLadingBill(LcPsiLadingBill psiLadingBill,
            HttpServletRequest request, HttpServletResponse response,
            Model model) throws UnsupportedEncodingException {
        int excelNo = 1;
        List<PsiProduct> productList = psiProductService.findAll();
        Map<String, PsiProduct> map = Maps.newHashMap();;
        for(PsiProduct product : productList){
                map.put(product.getName(), product);
        }
        List<LcPsiLadingBill> exprotLadingBill = psiLadingBillService.expBill(psiLadingBill);

        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();
        HSSFRow row = sheet.createRow(0);

        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        sheet.autoSizeColumn((short)0); //调整第一列宽度
        sheet.autoSizeColumn((short)1); //调整第二列宽度
        sheet.autoSizeColumn((short)2); //调整第三列宽度
        sheet.autoSizeColumn((short)10);
        String[] title = { "    产品名      ", "    品类         ", "    中英文名       ", "供应商", "价格(含税)",
                "价格(不含税) ", "数量", "总价(含税)", "总价(不含税)", "收货单确认时间","    采购订单号      "};

        style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
        style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        // 设置Excel中的边框(表头的边框)
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        style.setBorderBottom(HSSFCellStyle.BORDER_MEDIUM);
        style.setBottomBorderColor(HSSFColor.BLACK.index);

        style.setBorderLeft(HSSFCellStyle.BORDER_MEDIUM);
        style.setLeftBorderColor(HSSFColor.BLACK.index);

        style.setBorderRight(HSSFCellStyle.BORDER_MEDIUM);
        style.setRightBorderColor(HSSFColor.BLACK.index);

        style.setBorderTop(HSSFCellStyle.BORDER_MEDIUM);
        style.setTopBorderColor(HSSFColor.BLACK.index);
        // 设置字体
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
        for (LcPsiLadingBill bill : exprotLadingBill) {
                List<LcPsiLadingBillItem> items = bill.getItems();
                for(LcPsiLadingBillItem billItem : items){
                    if(billItem.getQuantitySure() != 0) {
                        String productName = billItem.getProductName();

                        row = sheet.createRow(excelNo++);
                        row.createCell(0, Cell.CELL_TYPE_STRING).setCellValue(productName+(billItem.getColorCode()!=""?"_"+billItem.getColorCode():""));
                        row.createCell(1, Cell.CELL_TYPE_STRING).setCellValue(map.get(productName)!=null?map.get(productName).getType():""); 
                        row.createCell(2, Cell.CELL_TYPE_STRING).setCellValue(map.get(productName)!=null?map.get(productName).getChineseName():"");
                        row.createCell(3, Cell.CELL_TYPE_STRING).setCellValue(bill.getSupplier().getNikename());
                        row.createCell(4, Cell.CELL_TYPE_NUMERIC).setCellValue(billItem.getItemPrice().doubleValue());
                        Double priceNotTax = billItem.getItemPrice().doubleValue()/1.17;
                        
                        double  price  =   new BigDecimal(priceNotTax).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
                        row.createCell(5, Cell.CELL_TYPE_NUMERIC).setCellValue(price);
                        row.createCell(6, Cell.CELL_TYPE_NUMERIC).setCellValue(billItem.getQuantitySure());
                        row.createCell(7, Cell.CELL_TYPE_NUMERIC).setCellValue(billItem.getItemPrice().doubleValue()*billItem.getQuantitySure());
                        row.createCell(8, Cell.CELL_TYPE_NUMERIC).setCellValue(price*billItem.getQuantitySure());
                        row.createCell(9, Cell.CELL_TYPE_STRING).setCellValue(bill.getSureDate()!=null?bill.getSureDate().toString():"");
                        row.createCell(10, Cell.CELL_TYPE_STRING).setCellValue(billItem.getPurchaseOrderItem()!=null?billItem.getPurchaseOrderItem().getPurchaseOrder().getOrderNo():"");
                    }
                }
                
        }

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/x-download");

        SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
        String fileName = "财务导出" + sdf.format(new Date()) + ".xls";
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.addHeader("Content-Disposition", "attachment;filename="
                + fileName);
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
