/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.web;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.excel.ExportTransportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PsiTransportDto;
import com.springrain.erp.modules.psi.entity.PsiTransportOrder;
import com.springrain.erp.modules.psi.entity.PsiTransportPayment;
import com.springrain.erp.modules.psi.entity.PsiTransportPaymentItem;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.PsiTransportOrderService;
import com.springrain.erp.modules.psi.service.PsiTransportPaymentService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 运单付款表Controller
 * @author Michael
 * @version 2015-01-21
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/psiTransportPayment")
public class PsiTransportPaymentController extends BaseController {

	
	@Autowired
	private PsiTransportPaymentService  psiTransportPaymentService;
	@Autowired
	private PsiTransportOrderService 	psiTransportOrderService;
	@Autowired
	private PsiSupplierService  		psiSupplierService;
	@Autowired
	private PsiProductService 	  		productService;
	
	
	@RequiresPermissions("psi:tranPayment:view")
	@RequestMapping(value = {"list", ""})
	public String list(PsiTransportPayment psiTransportPayment, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date today = sdf.parse(sdf.format(new Date()));
		if (psiTransportPayment.getCreateDate() == null) {
			psiTransportPayment.setCreateDate(DateUtils.addMonths(today, -1));
			psiTransportPayment.setUpdateDate(today);
		}
		Page<PsiTransportPayment>  page = new Page<PsiTransportPayment>(request, response);
        List<PsiSupplier> suppliers=this.psiSupplierService.findAllTransporter();
		model.addAttribute("suppliers", suppliers);
        psiTransportPaymentService.find(page, psiTransportPayment); 
        model.addAttribute("page", page);
		return "modules/psi/psiTransportPaymentList";
	}

	@RequiresPermissions("psi:tranPayment:edit")
	@RequestMapping(value = "add")
	public String add(PsiTransportPayment psiTransportPayment, Model model) {
		List<PsiSupplier> suppliers=this.psiSupplierService.findAllTransporter();
		Integer supplierId =0;
		PsiSupplier supplier= null;
		Map<String, String> accountMaps= null;
		Map<String,List<String []>>  unPaymentTransMap = Maps.newLinkedHashMap();
		if(psiTransportPayment.getSupplier()==null){
			if(suppliers!=null&&suppliers.size()>0){
				supplier=suppliers.get(0);
				supplierId=supplier.getId();
			}
		}else{
			supplierId=psiTransportPayment.getSupplier().getId();
			for (PsiSupplier psiSupplier : suppliers) {
				if(psiSupplier.getId().equals(supplierId)){
					supplier=psiSupplier;
					break;
				}
			}
		}
		
		//查出未付款的订单信息
		if(supplierId>0){
			
			Map<String,List<String>> ingorePayItem = Maps.newLinkedHashMap();
			//查出没完成的付款单，组成一个map （key：tranNo value：付款类型）
			List<PsiTransportPayment> pays = this.psiTransportPaymentService.findUpPay(supplierId);
			
			if(pays.size()>0){
				for(PsiTransportPayment pay:pays){
					
					for(PsiTransportPaymentItem item:pay.getItems()){
						List<String> lists =Lists.newArrayList();
						if(ingorePayItem.get(item.getTransportNo())!=null){
							lists=ingorePayItem.get(item.getTransportNo());
						}
						lists.add(item.getPaymentType());
						ingorePayItem.put(item.getTransportNo(), lists);
					}
				}
			}
			
			
			//账号信息
			accountMaps= supplier.getAccountMap();
			//查出未付完款的提单项信息
			List<PsiTransportOrder> tranOrders= this.psiTransportOrderService.findUnDonePayment(supplierId);
			if(tranOrders!=null&&tranOrders.size()>0){
				for(PsiTransportOrder tran:tranOrders){
					List<String[]> list= Lists.newLinkedList();
					String  id = tran.getId()+"";
					String  tranNo = tran.getTransportNo();
					//查出六种未付款信息项         运单id、运单no、付款种类、要付款金额、货币类型、                  根据供应商id排除非本供应商的单；
					if(tran.getLocalAmount()!=null&&tran.getPayAmount1()==null&&tran.getVendor1()!=null&&tran.getVendor1().getId().equals(supplierId)){
						if(ingorePayItem.get(tranNo)==null||!ingorePayItem.get(tranNo).contains("LocalAmount")){ //排除是草稿和申请状态的
							String amountInfo[]={id,tranNo,"LocalAmount",tran.getLocalAmount()+"",tran.getCurrency1(),tran.getLocalPath()};
							list.add(amountInfo);
						}
					}
					
					if(tran.getTranAmount()!=null&&tran.getPayAmount2()==null&&tran.getVendor2()!=null&&tran.getVendor2().getId().equals(supplierId)){
						if(ingorePayItem.get(tranNo)==null||!ingorePayItem.get(tranNo).contains("TranAmount")){ //排除是草稿和申请状态的
							String amountInfo[]={id,tranNo,"TranAmount",tran.getTranAmount()+"",tran.getCurrency2(),tran.getTranPath()};
							list.add(amountInfo);
						}
					}
					
					if(tran.getDapAmount()!=null&&tran.getPayAmount3()==null&&tran.getVendor3()!=null&&tran.getVendor3().getId().equals(supplierId)){
						if(ingorePayItem.get(tranNo)==null||!ingorePayItem.get(tranNo).contains("DapAmount")){ //排除是草稿和申请状态的
							String amountInfo[]={id,tranNo,"DapAmount",tran.getDapAmount()+"",tran.getCurrency3(),tran.getDapPath()};
							list.add(amountInfo);
						}
					}
					
					if(tran.getOtherAmount()!=null&&tran.getPayAmount4()==null&&tran.getVendor4()!=null&&tran.getVendor4().getId().equals(supplierId)){
						if(ingorePayItem.get(tranNo)==null||!ingorePayItem.get(tranNo).contains("OtherAmount")){ //排除是草稿和申请状态的
							String amountInfo[]={id,tranNo,"OtherAmount",tran.getOtherAmount()+"",tran.getCurrency4(),tran.getOtherPath()};
							list.add(amountInfo);
						}
					}
					
					if(tran.getOtherAmount1()!=null&&tran.getPayAmount7()==null&&tran.getVendor7()!=null&&tran.getVendor7().getId().equals(supplierId)){
						if(ingorePayItem.get(tranNo)==null||!ingorePayItem.get(tranNo).contains("OtherAmount1")){ //排除是草稿和申请状态的
							String amountInfo[]={id,tranNo,"OtherAmount1",tran.getOtherAmount1()+"",tran.getCurrency7(),tran.getOtherPath1()};
							list.add(amountInfo);
						}
					}
					
					if(tran.getInsuranceAmount()!=null&&tran.getPayAmount5()==null&&tran.getVendor5()!=null&&tran.getVendor5().getId().equals(supplierId)){
						if(ingorePayItem.get(tranNo)==null||!ingorePayItem.get(tranNo).contains("InsuranceAmount")){ //排除是草稿和申请状态的
							String amountInfo[]={id,tranNo,"InsuranceAmount",tran.getInsuranceAmount()+"",tran.getCurrency5(),tran.getInsurancePath()};
							list.add(amountInfo);
						}
					}
					
					if(tran.getDutyTaxes()!=null&&tran.getPayAmount6()==null&&tran.getVendor6()!=null&&tran.getVendor6().getId().equals(supplierId)){
						if(ingorePayItem.get(tranNo)==null||!ingorePayItem.get(tranNo).contains("TaxAmount")){ //排除是草稿和申请状态的
							if(tran.getTaxTaxes()==null){
								tran.setTaxTaxes(0f);
							}
							String amountInfo[]={id,tranNo,"TaxAmount",tran.getDutyTaxes()+tran.getTaxTaxes()+(tran.getOtherTaxes()==null?0:tran.getOtherTaxes())+"",tran.getCurrency6(),tran.getTaxPath()};
							list.add(amountInfo);
						}
					}
					
					if(list.size()>0){
						unPaymentTransMap.put(tran.getTransportNo(),list );
					}
				}
			}
			
		}
		String currencyStr ="CNY,USD,EUR,JPY,CAD,GBP";
		List<String> currencys=Lists.newArrayList(currencyStr.split(","));
		
		model.addAttribute("currencys", currencys);
		model.addAttribute("suppliers", suppliers);
		model.addAttribute("accountMaps", accountMaps);
		model.addAttribute("unPaymentTransMap", unPaymentTransMap);
		return "modules/psi/psiTransportPaymentAdd";
	}
	
	@RequiresPermissions("psi:tranPayment:edit")
	@RequestMapping(value = "edit")
	public String edit(PsiTransportPayment psiTransportPayment, Model model) {
		Integer supplierId=0;
		Map<String, String> accountMaps= null;
		StringBuilder sb = new StringBuilder("");
		Map<String,List<PsiTransportPaymentItem>>  unPaymentTransMap = Maps.newLinkedHashMap();
		Map<String,String>  supplierFilePath = Maps.newLinkedHashMap();
		Map<String,List<String>> ingorePayItem = Maps.newLinkedHashMap();
		Map<String,PsiTransportPaymentItem> selfMap = Maps.newLinkedHashMap();
		PsiSupplier supplier  = new PsiSupplier();
		
		if(psiTransportPayment.getId()!=null){
			psiTransportPayment=this.psiTransportPaymentService.get(psiTransportPayment.getId());
			supplier=psiTransportPayment.getSupplier();
			supplierId=supplier.getId();
		}
		
		for(PsiTransportPaymentItem item: psiTransportPayment.getItems()){
			selfMap.put(item.getTransportNo()+","+item.getPaymentType(), item);
			sb.append(item.getId()+",");
		}
		
		String itemIds="";
		if(StringUtils.isNotEmpty(sb.toString())){
			itemIds=sb.toString().substring(0,sb.toString().length()-1);
		}
		psiTransportPayment.setOldItemIds(itemIds);//把编辑前的itemId放到页面上
			//账号信息
			accountMaps= supplier.getAccountMap();
			//查出未付完款的提单项信息
			List<PsiTransportOrder> tranOrders= this.psiTransportOrderService.findUnDonePayment(supplierId);
			
			//查出除了自身以外的没完成的付款单，组成一个map （key：id+付款类型）
			List<PsiTransportPayment> pays = this.psiTransportPaymentService.findUpPayIngorSelf(psiTransportPayment.getId(),supplierId);
			
			if(pays.size()>0){
				for(PsiTransportPayment pay:pays){
					for(PsiTransportPaymentItem item:pay.getItems()){
						List<String> lists =Lists.newArrayList();
						if(ingorePayItem.get(item.getTransportNo())!=null){
							lists=ingorePayItem.get(item.getTransportNo());
						}
						lists.add(item.getPaymentType());
						ingorePayItem.put(item.getTransportNo(), lists);
					}
				}
			}
			
			if(tranOrders!=null&&tranOrders.size()>0){
				for(PsiTransportOrder tran:tranOrders){
					List<PsiTransportPaymentItem> list= Lists.newLinkedList();
					String  tranNo = tran.getTransportNo();
					//查出六种未付款信息项         运单id、运单no、付款种类、要付款金额、货币类型、                  根据供应商id排除非本供应商的单；
					if(tran.getLocalAmount()!=null&&tran.getPayAmount1()==null&&tran.getVendor1()!=null&&tran.getVendor1().getId().equals(supplierId)){
						if(ingorePayItem.get(tranNo)==null||!ingorePayItem.get(tranNo).contains("LocalAmount")){ //排除是草稿和申请状态的
							//放供应商费用明细
							if(StringUtils.isNotEmpty(tran.getLocalPath())){
								supplierFilePath.put(tranNo+"_LocalAmount",tran.getLocalPath());	
							}
							list.add(this.getItemData(selfMap, tran, "LocalAmount"));	
						}
					}
					
				
					if(tran.getTranAmount()!=null&&tran.getPayAmount2()==null&&tran.getVendor2()!=null&&tran.getVendor2().getId().equals(supplierId)){
						if(ingorePayItem.get(tranNo)==null||!ingorePayItem.get(tranNo).contains("TranAmount")){ //排除是草稿和申请状态的
							//放供应商费用明细
							if(StringUtils.isNotEmpty(tran.getTranPath())){
								supplierFilePath.put(tranNo+"_TranAmount",tran.getTranPath());	
							}
							list.add(this.getItemData(selfMap, tran, "TranAmount"));	
						}
					}
					
					if(tran.getDapAmount()!=null&&tran.getPayAmount3()==null&&tran.getVendor3()!=null&&tran.getVendor3().getId().equals(supplierId)){
						if(ingorePayItem.get(tranNo)==null||!ingorePayItem.get(tranNo).contains("DapAmount")){ //排除是草稿和申请状态的
							//放供应商费用明细
							if(StringUtils.isNotEmpty(tran.getDapPath())){
								supplierFilePath.put(tranNo+"_DapAmount",tran.getDapPath());	
							}
							list.add(this.getItemData(selfMap, tran, "DapAmount"));	
						}
					}
					
					if(tran.getOtherAmount()!=null&&tran.getPayAmount4()==null&&tran.getVendor4()!=null&&tran.getVendor4().getId().equals(supplierId)){
						if(ingorePayItem.get(tranNo)==null||!ingorePayItem.get(tranNo).contains("OtherAmount")){ //排除是草稿和申请状态的
							//放供应商费用明细
							if(StringUtils.isNotEmpty(tran.getOtherPath())){
								supplierFilePath.put(tranNo+"_OtherAmount",tran.getOtherPath());	
							}
							list.add(this.getItemData(selfMap, tran, "OtherAmount"));	
						}
					}
					
					if(tran.getOtherAmount1()!=null&&tran.getPayAmount7()==null&&tran.getVendor7()!=null&&tran.getVendor7().getId().equals(supplierId)){
						if(ingorePayItem.get(tranNo)==null||!ingorePayItem.get(tranNo).contains("OtherAmount1")){ //排除是草稿和申请状态的
							//放供应商费用明细
							if(StringUtils.isNotEmpty(tran.getOtherPath1())){
								supplierFilePath.put(tranNo+"_OtherAmount1",tran.getOtherPath1());	
							}
							list.add(this.getItemData(selfMap, tran, "OtherAmount1"));	
						}
					}
					
					if(tran.getInsuranceAmount()!=null&&tran.getPayAmount5()==null&&tran.getVendor5()!=null&&tran.getVendor5().getId().equals(supplierId)){
						if(ingorePayItem.get(tranNo)==null||!ingorePayItem.get(tranNo).contains("InsuranceAmount")){ //排除是草稿和申请状态的
							//放供应商费用明细
							if(StringUtils.isNotEmpty(tran.getInsurancePath())){
								supplierFilePath.put(tranNo+"_InsuranceAmount",tran.getInsurancePath());	
							}
							list.add(this.getItemData(selfMap, tran, "InsuranceAmount"));	
						}
					}
					
					if(tran.getDutyTaxes()!=null&&tran.getPayAmount6()==null&&tran.getVendor6()!=null&&tran.getVendor6().getId().equals(supplierId)){
						if(ingorePayItem.get(tranNo)==null||!ingorePayItem.get(tranNo).contains("TaxAmount")){ //排除是草稿和申请状态的
							//放供应商费用明细
							if(StringUtils.isNotEmpty(tran.getTaxPath())){
								supplierFilePath.put(tranNo+"_TaxAmount",tran.getTaxPath());	
							}
							list.add(this.getItemData(selfMap, tran, "TaxAmount"));	
						}
					}
					
					unPaymentTransMap.put(tran.getTransportNo(),list );
				}
				
			}
			
			
		String currencyStr ="CNY,USD,EUR,JPY,CAD,GBP";
		List<String> currencys=Lists.newArrayList(currencyStr.split(","));
		
		model.addAttribute("psiTransportPayment", psiTransportPayment);
		model.addAttribute("currencys", currencys);
		model.addAttribute("accountMaps", accountMaps);
		model.addAttribute("unPaymentTransMap", unPaymentTransMap);
		model.addAttribute("supplierFilePath", supplierFilePath);
		model.addAttribute("selfMap", selfMap);
		return "modules/psi/psiTransportPaymentEdit";
	}
	
	@RequiresPermissions("psi:tranPayment:view")
	@RequestMapping(value = "view")
	public String view(PsiTransportPayment psiTransportPayment, Model model) {
		if(psiTransportPayment.getId()!=null){
			psiTransportPayment=this.psiTransportPaymentService.get(psiTransportPayment.getId());
		}
		Map<String,List<PsiTransportPaymentItem>> ItemMap = Maps.newHashMap();
		for(PsiTransportPaymentItem item: psiTransportPayment.getItems()){
			String payNo  = item.getTransportNo();
			List<PsiTransportPaymentItem> lists=Lists.newArrayList();
			if(ItemMap.get(payNo)!=null){
				lists=ItemMap.get(payNo); 
			}
			lists.add(item);
			ItemMap.put(payNo, lists);
		}
			
		Map<String,String> fileMap= this.psiTransportOrderService.getSupplierCostPath(ItemMap,psiTransportPayment);
		//查询出关联的附件，放到supplierCostPath里	
		model.addAttribute("fileMap", fileMap);
		model.addAttribute("ItemMap", ItemMap);
		model.addAttribute("psiTransportPayment", psiTransportPayment);
		return "modules/psi/psiTransportPaymentView";
	}
	
	
	@RequestMapping(value = "printPayment")
	public String printPayment(PsiTransportPayment psiTransportPayment, Model model,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException {
		if(psiTransportPayment.getId()!=null){
			psiTransportPayment=this.psiTransportPaymentService.get(psiTransportPayment.getId());
			psiTransportPayment.setFlowInfo("编号："+(psiTransportPayment.getPayFlowNo()!=null?psiTransportPayment.getPayFlowNo():""));
			psiTransportPayment.setApplyTime(new SimpleDateFormat("yyyy/MM/dd").format(psiTransportPayment.getCreateDate()));
			psiTransportPayment.setSureTime(psiTransportPayment.getSureDate()!=null?new SimpleDateFormat("yyyy/MM/dd").format(psiTransportPayment.getSureDate()):"");
			psiTransportPayment.setMoneyInfo(psiTransportPayment.getCurrency()+"  "+psiTransportPayment.getPaymentAmount());
			StringBuilder transportNo=new StringBuilder();
			for (PsiTransportPaymentItem item : psiTransportPayment.getItems()) {
				if(transportNo.indexOf(item.getTransportNo())<0){
					transportNo.append(item.getTransportNo()).append("  ");
				}
			}
			psiTransportPayment.setSupplierName(psiTransportPayment.getSupplier().getName());
			psiTransportPayment.setTransportNoRemark("运单号:"+transportNo);
			psiTransportPayment.setApplyUserInfo(psiTransportPayment.getApplyUser()==null?psiTransportPayment.getCreateUser().getName():psiTransportPayment.getApplyUser().getName());
			psiTransportPayment.setApplyInfo(psiTransportPayment.getApplyUserInfo()+"申请,申请时间:"+new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			psiTransportPayment.setCheckInfo((psiTransportPayment.getApplyUser()!=null?psiTransportPayment.getApplyUser().getName():"")+"审核,审核时间:"+new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
			psiTransportPayment.setSupplierAccount(psiTransportPayment.getAccount());
			String modelName = "paymentApplication";//模板文件名称
			String xmlName = "paymentApplication";
			ExportTransportExcel ete = new ExportTransportExcel();
			Workbook  workbook = ete.writeData(psiTransportPayment, xmlName,modelName, 0);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = modelName + sdf.format(new Date()) + ".xlsx";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition","attachment;filename=" + fileName);
			try {
				OutputStream out = response.getOutputStream();
				workbook.write(out);
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	
	@RequiresPermissions("psi:tranPayment:sure")
	@RequestMapping(value = "sure")
	public String sure(PsiTransportPayment psiTransportPayment, Model model) {
		if(psiTransportPayment.getId()!=null){
			psiTransportPayment=this.psiTransportPaymentService.get(psiTransportPayment.getId());
		}
		Map<String,List<PsiTransportPaymentItem>> ItemMap = Maps.newHashMap();
		for(PsiTransportPaymentItem item: psiTransportPayment.getItems()){
			String payNo  = item.getTransportNo();
			List<PsiTransportPaymentItem> lists=Lists.newArrayList();
			if(ItemMap.get(payNo)!=null){
				lists=ItemMap.get(payNo); 
			}
			lists.add(item);
			ItemMap.put(payNo, lists);
		}
		Map<String,String> fileMap= this.psiTransportOrderService.getSupplierCostPath(ItemMap,psiTransportPayment);
		//查询出关联的附件，放到supplierCostPath里	
		model.addAttribute("fileMap", fileMap);
		model.addAttribute("ItemMap", ItemMap);
		model.addAttribute("psiTransportPayment", psiTransportPayment);
		return "modules/psi/psiTransportPaymentSure";
	}
	
	
	@RequiresPermissions("psi:tranPayment:review")
	@RequestMapping(value = "review")
	public String review(PsiTransportPayment psiTransportPayment, Model model) {
		if(psiTransportPayment.getId()!=null){
			psiTransportPayment=this.psiTransportPaymentService.get(psiTransportPayment.getId());
		}
		Map<String,List<PsiTransportPaymentItem>> ItemMap = Maps.newHashMap();
		for(PsiTransportPaymentItem item: psiTransportPayment.getItems()){
			String payNo  = item.getTransportNo();
			List<PsiTransportPaymentItem> lists=Lists.newArrayList();
			if(ItemMap.get(payNo)!=null){
				lists=ItemMap.get(payNo); 
			}
			lists.add(item);
			ItemMap.put(payNo, lists);
		}
		Map<String,String> fileMap= this.psiTransportOrderService.getSupplierCostPath(ItemMap,psiTransportPayment);
		//查询出关联的附件，放到supplierCostPath里	
		model.addAttribute("ItemMap", ItemMap);
		model.addAttribute("fileMap", fileMap);
		model.addAttribute("psiTransportPayment", psiTransportPayment);
		if("1".equals(psiTransportPayment.getPaymentSta())){
			return "modules/psi/psiTransportPaymentReview";
		}else{
			return "modules/psi/psiTransportPaymentView";
		}
		
	}
	
	@RequiresPermissions("psi:tranPayment:edit")
	@RequestMapping(value = "uploadBill")
	public String uploadBill(PsiTransportPayment psiTransportPayment, Model model) {
		if(psiTransportPayment.getId()!=null){
			psiTransportPayment=this.psiTransportPaymentService.get(psiTransportPayment.getId());
		}
		Map<String,List<PsiTransportPaymentItem>> ItemMap = Maps.newHashMap();
		for(PsiTransportPaymentItem item: psiTransportPayment.getItems()){
			String payNo  = item.getTransportNo();
			List<PsiTransportPaymentItem> lists=Lists.newArrayList();
			if(ItemMap.get(payNo)!=null){
				lists=ItemMap.get(payNo); 
			}
			lists.add(item);
			ItemMap.put(payNo, lists);
		}
			
		Map<String,String> fileMap= this.psiTransportOrderService.getSupplierCostPath(ItemMap,psiTransportPayment);
		//查询出关联的附件，放到supplierCostPath里	
		model.addAttribute("fileMap", fileMap);
		model.addAttribute("ItemMap", ItemMap);
		model.addAttribute("psiTransportPayment", psiTransportPayment);
		return "modules/psi/psiTransportPaymentUploadBill";
	}
	
	
	@RequiresPermissions("psi:tranPayment:edit")
	@RequestMapping(value = "uploadBillSave")
	public String uploadBillSave(@RequestParam("attchmentFile")MultipartFile[] attchmentFiles,PsiTransportPayment psiTransportPayment, Model model, RedirectAttributes redirectAttributes) {
		psiTransportPaymentService.uploadBillSave(psiTransportPayment,attchmentFiles);
		addMessage(redirectAttributes, "上传供应商税务发票'" + psiTransportPayment.getPaymentNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportPayment/?repage";
	}
	
	@RequiresPermissions("psi:tranPayment:edit")
	@RequestMapping(value = "addSave")
	public String addSave(PsiTransportPayment psiTransportPayment, Model model, RedirectAttributes redirectAttributes) {
		psiTransportPaymentService.addSave(psiTransportPayment);
		addMessage(redirectAttributes, "保存运单付款'" + psiTransportPayment.getPaymentNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportPayment/?repage";
	}
	
	@RequiresPermissions("psi:tranPayment:edit")
	@RequestMapping(value = "editSave")
	public String editSave(PsiTransportPayment psiTransportPayment, Model model, RedirectAttributes redirectAttributes) {
			psiTransportPaymentService.editSave(psiTransportPayment);
			addMessage(redirectAttributes, "编辑运单付款'"+psiTransportPayment.getPaymentNo() + "" + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportPayment/?repage";
	}
	
	@RequiresPermissions("psi:tranPayment:sure")
	@RequestMapping(value = "sureSave")
	public String sureSave(@RequestParam("attchmentFile")MultipartFile[] attchmentFiles,PsiTransportPayment psiTransportPayment, Model model, RedirectAttributes redirectAttributes) {
		psiTransportPaymentService.sureSave(psiTransportPayment,attchmentFiles);
		addMessage(redirectAttributes, "确认运单付款'" + psiTransportPayment.getPaymentNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportPayment/?repage";
	}
	
	@RequiresPermissions("psi:tranPayment:review")
	@RequestMapping(value = "reviewSave")
	public String reviewSave(PsiTransportPayment psiTransportPayment, Model model, RedirectAttributes redirectAttributes) {
		if(psiTransportPaymentService.reviewSave(psiTransportPayment)){
			addMessage(redirectAttributes, "审核运单付款'" + psiTransportPayment.getPaymentNo() + "'成功");
		}else{
			addMessage(redirectAttributes, "发送运单付款邮件失败，请重新审核!!");
		}
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportPayment/?repage";
	}
	
	@RequestMapping(value = "cancel")
	public String cancel(Integer id, RedirectAttributes redirectAttributes) {
		PsiTransportPayment psiTransportPayment=this.psiTransportPaymentService.get(id);
		psiTransportPayment.setPaymentSta("8");
		psiTransportPayment.setCancelDate(new Date());
		psiTransportPayment.setCancelUser(UserUtils.getUser());
		this.psiTransportPaymentService.save(psiTransportPayment);
		addMessage(redirectAttributes, "取消运单'" + psiTransportPayment.getPaymentNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportPayment/?repage";
	}
	
	@RequestMapping(value = "toDraft")
	public String toDraft(Integer id, RedirectAttributes redirectAttributes) {
		PsiTransportPayment psiTransportPayment=this.psiTransportPaymentService.get(id);
		psiTransportPayment.setPaymentSta("0");
		psiTransportPayment.setCancelDate(new Date());
		psiTransportPayment.setCancelUser(UserUtils.getUser());
		this.psiTransportPaymentService.save(psiTransportPayment);
		addMessage(redirectAttributes, "取消成草稿状态'" + psiTransportPayment.getPaymentNo() + "'成功");
		return "redirect:"+Global.getAdminPath()+"/psi/psiTransportPayment/?repage";
	}
	
	public PsiTransportPaymentItem getItemData(Map<String,PsiTransportPaymentItem> selfMap,PsiTransportOrder tran,String payType){
		PsiTransportPaymentItem item=new PsiTransportPaymentItem();
		item.setTranOrderId(tran.getId());
		item.setTransportNo(tran.getTransportNo());
		item.setPaymentType(payType);
		if("LocalAmount".equals(payType)){
			item.setPaymentAmount(tran.getLocalAmount());
			item.setCurrency(tran.getCurrency1());
		}else if("TranAmount".equals(payType)){
			item.setPaymentAmount(tran.getTranAmount());
			item.setCurrency(tran.getCurrency2());
		}else if("DapAmount".equals(payType)){
			item.setPaymentAmount(tran.getDapAmount());
			item.setCurrency(tran.getCurrency3());
		}else if("OtherAmount".equals(payType)){
			item.setPaymentAmount(tran.getOtherAmount());
			item.setCurrency(tran.getCurrency4());
		}else if("OtherAmount1".equals(payType)){
			item.setPaymentAmount(tran.getOtherAmount1());
			item.setCurrency(tran.getCurrency7());
		}else if("InsuranceAmount".equals(payType)){
			item.setPaymentAmount(tran.getInsuranceAmount());
			item.setCurrency(tran.getCurrency5());
		}else if("TaxAmount".equals(payType)){
			if(tran.getTaxTaxes()!=null){
				item.setPaymentAmount(tran.getDutyTaxes()+tran.getTaxTaxes()+(tran.getOtherTaxes()==null?0:tran.getOtherTaxes()));
			}else{
				item.setPaymentAmount(tran.getDutyTaxes()+(tran.getOtherTaxes()==null?0:tran.getOtherTaxes()));
			}
			item.setCurrency(tran.getCurrency6());
		}
		String key =tran.getTransportNo()+","+payType;
		if(selfMap.get(key)!=null){
			item.setRemark(selfMap.get(key).getRemark());
			item.setId(selfMap.get(key).getId());
			item.setRate(selfMap.get(key).getRate());
			item.setAfterAmount(selfMap.get(key).getAfterAmount());
		}
		return item;
	}

	@RequestMapping(value = "byMonthTransport")
	public String byMonthTransport(PsiTransportPayment psiTransportPayment,String productName, Model model, RedirectAttributes redirectAttributes,String sureDate,String updateDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM"); 
		SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy"); 
		List<String>  productList = this.productService. findProductNameAndColorList();
		if(StringUtils.isBlank(sureDate)){
			Date date=new Date();
			Date endDate=DateUtils.getYearFirst(Integer.parseInt(yearSdf.format(date))); 
			//Date endDate=DateUtils.addMonths(date, -12);
			psiTransportPayment.setSureDate(endDate);
			psiTransportPayment.setUpdateDate(date);
		}else{
			try {
				psiTransportPayment.setSureDate(sdf.parse(sureDate));
				psiTransportPayment.setUpdateDate(sdf.parse(updateDate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		Map<String,Map<String,Map<String,Float>>> byMonthTransport= null;
		Map<String,Map<String,Map<String,Float>>> byMonthTransportMoney=null;
		if(StringUtils.isNotBlank(productName)){
			byMonthTransport=psiTransportPaymentService.getTransportInfoByProduct(psiTransportPayment,productName);
			byMonthTransportMoney=psiTransportPaymentService.getTransportInfoMoneyProduct(psiTransportPayment, productName);
			Map<String,Map<String,Map<String,Integer>>> tempQuantityMap=psiTransportPaymentService.getTransportQuantity(psiTransportPayment,productName);
			model.addAttribute("tempQuantityMap", tempQuantityMap);
		}else{
			byMonthTransport=psiTransportPaymentService.getTransportInfo(psiTransportPayment);
			byMonthTransportMoney=psiTransportPaymentService.getTransportInfoMoney(psiTransportPayment);
		}
		
		model.addAttribute("byMonthTransport", byMonthTransport);
		model.addAttribute("byMonthTransportMoney", byMonthTransportMoney);
		model.addAttribute("psiTransportPayment", psiTransportPayment);
		model.addAttribute("productList", productList);
		model.addAttribute("productName",productName);
		if(StringUtils.isNotBlank(productName)){
			return "modules/psi/psiTransportModelReportByProduct";
		}else{
			return "modules/psi/psiTransportModelReport";
		}
		
	}
	
	@RequestMapping(value = "byMonthTransport2")
	public String byMonthTransport2(PsiTransportPayment psiTransportPayment, Model model, RedirectAttributes redirectAttributes,String sureDate,String updateDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
		SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy"); 
		if(StringUtils.isBlank(sureDate)){
			Date date=new Date();
		//	Date endDate=DateUtils.addMonths(date, -12);
			Date endDate=DateUtils.getYearFirst(Integer.parseInt(yearSdf.format(date))); 
			psiTransportPayment.setSureDate(endDate);
			psiTransportPayment.setUpdateDate(date);
		}else{
			try {
				psiTransportPayment.setSureDate(sdf.parse(sureDate));
				psiTransportPayment.setUpdateDate(sdf.parse(updateDate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Map<String,Map<String,Map<String,Float>>> byMonthTransport=psiTransportPaymentService.getTransportInfo2(psiTransportPayment);
		//Map<String,Map<String,Map<String,Float>>> byMonthTransportMoney=psiTransportPaymentService.getTransportInfoMoney(psiTransportPayment);
		model.addAttribute("byMonthTransport", byMonthTransport);
		//model.addAttribute("byMonthTransportMoney", byMonthTransportMoney);
		model.addAttribute("psiTransportPayment", psiTransportPayment);
		return "modules/psi/psiTransportModelReport2";
	}

	@RequestMapping(value = "byMonthInfo")
	public String getByMonthInfo(PsiTransportPayment psiTransportPayment, Model model, RedirectAttributes redirectAttributes,@RequestParam(required = false)String sureDate,@RequestParam(required = false)String updateDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM"); 
		SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy"); 
		if(StringUtils.isBlank(sureDate)){
			Date date=new Date();
			//Date endDate=DateUtils.addMonths(date, -12);
			Date endDate=DateUtils.getYearFirst(Integer.parseInt(yearSdf.format(date))); 
			psiTransportPayment.setSureDate(endDate);
			psiTransportPayment.setUpdateDate(date);
		}else{
			try {
				psiTransportPayment.setSureDate(sdf.parse(sureDate));
				psiTransportPayment.setUpdateDate(sdf.parse(updateDate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Map<String,Map<String,Float>> byMonthMoney=psiTransportPaymentService.getByMonthMoney(psiTransportPayment);
		Map<String,Map<String,PsiTransportOrder>> byMonthOtherInfo=psiTransportPaymentService.getByMonthOtherInfo(psiTransportPayment);
		model.addAttribute("byMonthMoney", byMonthMoney);
		model.addAttribute("byMonthOtherInfo", byMonthOtherInfo);
		model.addAttribute("psiTransportPayment", psiTransportPayment);
		return "modules/psi/psiTransportMoneyReport";
	}
	
	
	@RequestMapping(value = "exportTransportReport")
	public String exportTransportReport(PsiTransportPayment psiTransportPayment,HttpServletRequest request,HttpServletResponse response, Model model, RedirectAttributes redirectAttributes,String sureDate,String updateDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM"); 
		if(StringUtils.isBlank(sureDate)){
			Date date=new Date();
			Date endDate=DateUtils.addMonths(date, -12);
			psiTransportPayment.setSureDate(endDate);
			psiTransportPayment.setUpdateDate(date);
		}else{
			try {
				psiTransportPayment.setSureDate(sdf.parse(sureDate));
				psiTransportPayment.setUpdateDate(sdf.parse(updateDate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Map<String,Map<String,Float>> byMonthMoney=psiTransportPaymentService.getByMonthMoney(psiTransportPayment);
		Map<String,Map<String,PsiTransportOrder>> byMonthOtherInfo=psiTransportPaymentService.getByMonthOtherInfo(psiTransportPayment);
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
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
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
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
		HSSFCell cell = null;
		List<String> title1 = Lists.newArrayList("总额");
		for (String monthTitle : byMonthMoney.keySet()) {
			title1.add(monthTitle);
		}
		title1.add("Total/RMB");
		for (int i = 0; i < title1.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title1.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		Map<String,String> rowTitle1=Maps.newLinkedHashMap();
		rowTitle1.put("EU", "eu");
		rowTitle1.put("US", "US");
		rowTitle1.put("JP", "jp");
		rowTitle1.put("Total/RMB", "total");
		int index=1;
		for (Map.Entry<String,String> entry:rowTitle1.entrySet()) {
			String constantTitle = entry.getKey();
			int j=0;
			row=sheet.createRow(index++);
    		row.setHeight((short) 400);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(constantTitle);
			sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
			Float total=0f;
			for (Map.Entry<String, Map<String, Float>> entry1 : byMonthMoney.entrySet()) {
				if(!"Total/RMB".equals(constantTitle)){
					if(byMonthMoney!=null&&entry1.getValue()!=null&&entry1.getValue().get(entry.getValue())!=null){
						total+=entry1.getValue().get(entry.getValue());
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(entry1.getValue().get(entry.getValue()));
						sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
					}else{
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
					}
				}else{
					float singleTotal=0f;
					if(byMonthMoney!=null&&entry1.getValue()!=null&&entry1.getValue().get("jp")!=null){
						total+=entry1.getValue().get("jp");
						singleTotal+=entry1.getValue().get("jp");
					}
					if(byMonthMoney!=null&&entry1.getValue()!=null&&entry1.getValue().get("US")!=null){
						total+=entry1.getValue().get("US");
						singleTotal+=entry1.getValue().get("US");
					}
					if(byMonthMoney!=null&&entry1.getValue()!=null&&entry1.getValue().get("eu")!=null){
						total+=entry1.getValue().get("eu");
						singleTotal+=entry1.getValue().get("eu");
					}
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(singleTotal);
					sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
				}
			}
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(total);
			sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
		}
		
		index++;
		row=sheet.createRow(index++);
		row.setHeight((short) 600);
		List<String> title2 = Lists.newArrayList("单价");
		for (String monthTitle : byMonthMoney.keySet()) {
			title2.add(monthTitle);
		}
		title2.add("RMB/KG");
		for (int i = 0; i < title2.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title2.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		Map<String,String> rowTitle2=Maps.newLinkedHashMap();
		rowTitle2.put("EU", "eu");
		rowTitle2.put("US", "US");
		rowTitle2.put("JP", "jp");
		rowTitle2.put("RMB/KG", "total");
		for (Map.Entry<String,String> entry:rowTitle2.entrySet()) {
			String constantTitle = entry.getKey();
			int j=0;
			row=sheet.createRow(index++);
    		row.setHeight((short) 400);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(constantTitle);
			sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
			Float total=0f;
			Float total1=0f;
			for (Map.Entry<String, Map<String, Float>> entry2 : byMonthMoney.entrySet()) {
				String monthTitle = entry2.getKey();
				if(!"RMB/KG".equals(constantTitle)){
					if(byMonthMoney!=null&&entry2.getValue()!=null&&entry2.getValue().get(entry.getValue())!=null
					 &&byMonthOtherInfo!=null&&byMonthOtherInfo.get(monthTitle)!=null&&byMonthOtherInfo.get(monthTitle).get(entry.getValue())!=null
					 &&byMonthOtherInfo.get(monthTitle).get(entry.getValue()).getWeight()!=null&&byMonthOtherInfo.get(monthTitle).get(entry.getValue()).getWeight()>0){
						total+=entry2.getValue().get(entry.getValue());
						total1+=byMonthOtherInfo.get(monthTitle).get(entry.getValue()).getWeight();
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(entry2.getValue().get(entry.getValue())/byMonthOtherInfo.get(monthTitle).get(entry.getValue()).getWeight());
						sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
					}else{
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
					}
				}else{
					float singleTotal=0f;
					float singleTotal1=0f;
					if(byMonthMoney!=null&&entry2.getValue()!=null&&entry2.getValue().get("jp")!=null
							 &&byMonthOtherInfo!=null&&byMonthOtherInfo.get(monthTitle)!=null&&byMonthOtherInfo.get(monthTitle).get("jp")!=null
							 &&byMonthOtherInfo.get(monthTitle).get("jp").getWeight()!=null&&byMonthOtherInfo.get(monthTitle).get("jp").getWeight()>0){
						total+=entry2.getValue().get("jp");
						total1+=byMonthOtherInfo.get(monthTitle).get("jp").getWeight();
						singleTotal+=entry2.getValue().get("jp");
						singleTotal1+=byMonthOtherInfo.get(monthTitle).get("jp").getWeight();
					}
					if(byMonthMoney!=null&&entry2.getValue()!=null&&entry2.getValue().get("US")!=null
							 &&byMonthOtherInfo!=null&&byMonthOtherInfo.get(monthTitle)!=null&&byMonthOtherInfo.get(monthTitle).get("US")!=null
							 &&byMonthOtherInfo.get(monthTitle).get("US").getWeight()!=null&&byMonthOtherInfo.get(monthTitle).get("US").getWeight()>0){
						total+=entry2.getValue().get("US");
						total1+=byMonthOtherInfo.get(monthTitle).get("US").getWeight();
						singleTotal+=entry2.getValue().get("US");
						singleTotal1+=byMonthOtherInfo.get(monthTitle).get("US").getWeight();
					}
					if(byMonthMoney!=null&&entry2.getValue()!=null&&entry2.getValue().get("eu")!=null
							 &&byMonthOtherInfo!=null&&byMonthOtherInfo.get(monthTitle)!=null&&byMonthOtherInfo.get(monthTitle).get("eu")!=null
							 &&byMonthOtherInfo.get(monthTitle).get("eu").getWeight()!=null&&byMonthOtherInfo.get(monthTitle).get("eu").getWeight()>0){
						total+=entry2.getValue().get("eu");
						total1+=byMonthOtherInfo.get(monthTitle).get("eu").getWeight();
						singleTotal+=entry2.getValue().get("eu");
						singleTotal1+=byMonthOtherInfo.get(monthTitle).get("eu").getWeight();
					}
					if(singleTotal1>0){
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(singleTotal/singleTotal1);
					}else{
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
				}
			}
			if(total1>0){
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(total/total1);
			}else{
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
			}
			
			sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
		}
		
		index++;
		row=sheet.createRow(index++);
		row.setHeight((short) 600);
		List<String> title3 = Lists.newArrayList("KGS");
		for (String monthTitle : byMonthOtherInfo.keySet()) {
			title3.add(monthTitle);
		}
		title3.add("Total/KG");
		for (int i = 0; i < title3.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title3.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		Map<String,String> rowTitle3=Maps.newLinkedHashMap();
		rowTitle3.put("EU", "eu");
		rowTitle3.put("US", "US");
		rowTitle3.put("JP", "jp");
		rowTitle3.put("Total/KG", "total");
		for (Map.Entry<String,String> entry:rowTitle3.entrySet()) {
			String constantTitle = entry.getKey();
			int j=0;
			row=sheet.createRow(index++);
    		row.setHeight((short) 400);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(constantTitle);
			sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
			Float total=0f;
			for (Map.Entry<String, Map<String, PsiTransportOrder>> entry2: byMonthOtherInfo.entrySet()) {
				if(!"Total/KG".equals(constantTitle)){
					if(byMonthOtherInfo!=null&&entry2.getValue()!=null&&entry2.getValue().get(entry.getValue())!=null&&entry2.getValue().get(entry.getValue()).getWeight()!=null){
						total+=entry2.getValue().get(entry.getValue()).getWeight();
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(entry2.getValue().get(entry.getValue()).getWeight());
						sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
					}else{
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
					}
				}else{
					float singleTotal=0f;
					if(byMonthOtherInfo!=null&&entry2.getValue()!=null&&entry2.getValue().get("jp")!=null&&entry2.getValue().get("jp").getWeight()!=null){
						total+=entry2.getValue().get("jp").getWeight();
						singleTotal+=entry2.getValue().get("jp").getWeight();
					}
					if(byMonthOtherInfo!=null&&entry2.getValue()!=null&&entry2.getValue().get("US")!=null&&entry2.getValue().get("US").getWeight()!=null){
						total+=entry2.getValue().get("US").getWeight();
						singleTotal+=entry2.getValue().get("US").getWeight();
					}
					if(byMonthOtherInfo!=null&&entry2.getValue()!=null&&entry2.getValue().get("eu")!=null&&entry2.getValue().get("eu").getWeight()!=null){
						total+=entry2.getValue().get("eu").getWeight();
						singleTotal+=entry2.getValue().get("eu").getWeight();
					}
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(singleTotal);
					sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
				}
			}
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(total);
			sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
		}
		
		index++;
		row=sheet.createRow(index++);
		row.setHeight((short) 600);
		List<String> title4 = Lists.newArrayList("CBM");
		for (String monthTitle : byMonthOtherInfo.keySet()) {
			title4.add(monthTitle);
		}
		title4.add("Total/CBM");
		for (int i = 0; i < title4.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title4.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		Map<String,String> rowTitle4=Maps.newLinkedHashMap();
		rowTitle4.put("EU", "eu");
		rowTitle4.put("US", "US");
		rowTitle4.put("JP", "jp");
		rowTitle4.put("Total/CBM", "total");
		for (Map.Entry<String,String> entry:rowTitle4.entrySet()) {
			String constantTitle = entry.getKey();
			int j=0;
			row=sheet.createRow(index++);
    		row.setHeight((short) 400);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(constantTitle);
			sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
			Float total=0f;
			for (Map.Entry<String, Map<String, PsiTransportOrder>> entry2: byMonthOtherInfo.entrySet()) {
				if(!"Total/CBM".equals(constantTitle)){
					if(byMonthOtherInfo!=null&&entry2.getValue()!=null&&entry2.getValue().get(entry.getValue())!=null&&entry2.getValue().get(entry.getValue()).getVolume()!=null){
						total+=entry2.getValue().get(entry.getValue()).getVolume();
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(entry2.getValue().get(entry.getValue()).getVolume());
						sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
					}else{
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
					}
				}else{
					float singleTotal=0f;
					if(byMonthOtherInfo!=null&&entry2.getValue()!=null&&entry2.getValue().get("jp")!=null&&entry2.getValue().get("jp").getVolume()!=null){
						total+=entry2.getValue().get("jp").getVolume();
						singleTotal+=entry2.getValue().get("jp").getVolume();
					}
					if(byMonthOtherInfo!=null&&entry2.getValue()!=null&&entry2.getValue().get("US")!=null&&entry2.getValue().get("US").getVolume()!=null){
						total+=entry2.getValue().get("US").getVolume();
						singleTotal+=entry2.getValue().get("US").getVolume();
					}
					if(byMonthOtherInfo!=null&&entry2.getValue()!=null&&entry2.getValue().get("eu")!=null&&entry2.getValue().get("eu").getVolume()!=null){
						total+=entry2.getValue().get("eu").getVolume();
						singleTotal+=entry2.getValue().get("eu").getVolume();
					}
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(singleTotal);
					sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
				}
			}
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(total);
			sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
		}
		
		
		index++;
		row=sheet.createRow(index++);
		row.setHeight((short) 600);
		List<String> title5 = Lists.newArrayList("Shpt");
		for (String monthTitle : byMonthOtherInfo.keySet()) {
			title5.add(monthTitle);
		}
		title5.add("Total/Shpt");
		for (int i = 0; i < title5.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title5.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		Map<String,String> rowTitle5=Maps.newLinkedHashMap();
		rowTitle5.put("EU", "eu");
		rowTitle5.put("US", "US");
		rowTitle5.put("JP", "jp");
		rowTitle5.put("Total/Shpt", "total");
		for (Map.Entry<String,String> entry:rowTitle5.entrySet()) {
			String constantTitle = entry.getKey();
			int j=0;
			row=sheet.createRow(index++);
    		row.setHeight((short) 400);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(constantTitle);
			sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
			Float total=0f;
//			for (String monthTitle : byMonthOtherInfo.keySet()) {
			for (Map.Entry<String, Map<String, PsiTransportOrder>> entry2: byMonthOtherInfo.entrySet()) {
				if(!"Total/Shpt".equals(constantTitle)){
					if(byMonthOtherInfo!=null&&entry2.getValue()!=null&&entry2.getValue().get(entry.getValue())!=null&&entry2.getValue().get(entry.getValue()).getTeu()!=null){
						total+=entry2.getValue().get(entry.getValue()).getTeu();
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(entry2.getValue().get(entry.getValue()).getTeu());
						sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
					}else{
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
					}
				}else{
					float singleTotal=0f;
					if(byMonthOtherInfo!=null&&entry2.getValue()!=null&&entry2.getValue().get("jp")!=null&&entry2.getValue().get("jp").getTeu()!=null){
						total+=entry2.getValue().get("jp").getTeu();
						singleTotal+=entry2.getValue().get("jp").getTeu();
					}
					if(byMonthOtherInfo!=null&&entry2.getValue()!=null&&entry2.getValue().get("US")!=null&&entry2.getValue().get("US").getTeu()!=null){
						total+=entry2.getValue().get("US").getTeu();
						singleTotal+=entry2.getValue().get("US").getTeu();
					}
					if(byMonthOtherInfo!=null&&entry2.getValue()!=null&&entry2.getValue().get("eu")!=null&&entry2.getValue().get("eu").getTeu()!=null){
						total+=entry2.getValue().get("eu").getTeu();
						singleTotal+=entry2.getValue().get("eu").getTeu();
					}
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(singleTotal);
					sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
				}
			}
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(total);
			sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
		}
		
		index++;
		row=sheet.createRow(index++);
		row.setHeight((short) 600);
		List<String> title6 = Lists.newArrayList("CTNS");
		for (String monthTitle : byMonthOtherInfo.keySet()) {
			title6.add(monthTitle);
		}
		title6.add("Total/Shpt");
		for (int i = 0; i < title6.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title6.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		Map<String,String> rowTitle6=Maps.newLinkedHashMap();
		rowTitle6.put("EU", "eu");
		rowTitle6.put("US", "US");
		rowTitle6.put("JP", "jp");
		rowTitle6.put("Total/CTNS", "total");
		for (Map.Entry<String,String> entry:rowTitle6.entrySet()) {
			String constantTitle = entry.getKey();
			int j=0;
			row=sheet.createRow(index++);
    		row.setHeight((short) 400);
    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(constantTitle);
			sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
			Float total=0f;
			for (Map.Entry<String, Map<String, PsiTransportOrder>> entry2: byMonthOtherInfo.entrySet()) {
				if(!"Total/CTNS".equals(constantTitle)){
					if(byMonthOtherInfo!=null&&entry2.getValue()!=null&&entry2.getValue().get(entry.getValue())!=null&&entry2.getValue().get(entry.getValue()).getBoxNumber()!=null){
						total+=entry2.getValue().get(entry.getValue()).getBoxNumber();
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(entry2.getValue().get(entry.getValue()).getBoxNumber());
						sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
					}else{
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
					}
				}else{
					float singleTotal=0f;
					if(byMonthOtherInfo!=null&&entry2.getValue()!=null&&entry2.getValue().get("jp")!=null&&entry2.getValue().get("jp").getBoxNumber()!=null){
						total+=entry2.getValue().get("jp").getBoxNumber();
						singleTotal+=entry2.getValue().get("jp").getBoxNumber();
					}
					if(byMonthOtherInfo!=null&&entry2.getValue()!=null&&entry2.getValue().get("US")!=null&&entry2.getValue().get("US").getBoxNumber()!=null){
						total+=entry2.getValue().get("US").getBoxNumber();
						singleTotal+=entry2.getValue().get("US").getBoxNumber();
					}
					if(byMonthOtherInfo!=null&&entry2.getValue()!=null&&entry2.getValue().get("eu")!=null&&entry2.getValue().get("eu").getBoxNumber()!=null){
						total+=entry2.getValue().get("eu").getBoxNumber();
						singleTotal+=entry2.getValue().get("eu").getBoxNumber();
					}
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(singleTotal);
					sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
				}
			}
			row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(total);
			sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
		}
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyMMddhhmm");
			String fileName = "transport_" + sdf1.format(new Date()) + ".xls";
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
	
	@RequestMapping(value = "exportTransportReport2")
	public String exportTransportReport2(PsiTransportPayment psiTransportPayment,HttpServletRequest request,HttpServletResponse response, Model model, RedirectAttributes redirectAttributes,String sureDate,String updateDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM"); 
		if(StringUtils.isBlank(sureDate)){
			Date date=new Date();
			Date endDate=DateUtils.addMonths(date, -12);
			psiTransportPayment.setSureDate(endDate);
			psiTransportPayment.setUpdateDate(date);
		}else{
			try {
				psiTransportPayment.setSureDate(sdf.parse(sureDate));
				psiTransportPayment.setUpdateDate(sdf.parse(updateDate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Map<String,Map<String,Map<String,Float>>> byMonthTransport=psiTransportPaymentService.getTransportInfo(psiTransportPayment);
		Map<String,Map<String,Map<String,Float>>> byMonthTransportMoney=psiTransportPaymentService.getTransportInfoMoney(psiTransportPayment);
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
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
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
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
        
        HSSFCellStyle cellStyle1 = wb.createCellStyle();
        cellStyle1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0%"));
        cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFCell cell = null;
		
		List<String> title = Lists.newArrayList("金额","Model");
		for (String monthTitle : byMonthTransportMoney.keySet()) {
			title.add(monthTitle);
		}
		title.add("Total/RMB");
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		Map<String,String> countryTitle=Maps.newLinkedHashMap();
		countryTitle.put("EU", "eu");
		countryTitle.put("US", "US");
		countryTitle.put("JP", "jp");
		countryTitle.put("Total/KG", "total");
		
		Map<String,String> rowTitle=Maps.newLinkedHashMap();
		rowTitle.put("AE", "0");
		rowTitle.put("OE", "1");
		rowTitle.put("EX", "2");
		rowTitle.put("Total","3");
		int index=1;
		for (String country:countryTitle.keySet()) {
			if(!"Total/KG".equals(country)){
				for (String type : rowTitle.keySet()) {
					int j=0;
					row=sheet.createRow(index++);
		    		row.setHeight((short) 400);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(country);
					sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(type);
					sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
					Float total=0f;
					for (String monthTitle : byMonthTransportMoney.keySet()) {
						if(!"Total".equals(type)){
							if(byMonthTransportMoney!=null&&byMonthTransportMoney.get(monthTitle)!=null&&byMonthTransportMoney.get(monthTitle).get(countryTitle.get(country))!=null&&byMonthTransportMoney.get(monthTitle).get(countryTitle.get(country)).get(rowTitle.get(type))!=null){
								total+=byMonthTransportMoney.get(monthTitle).get(countryTitle.get(country)).get(rowTitle.get(type));
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(byMonthTransportMoney.get(monthTitle).get(countryTitle.get(country)).get(rowTitle.get(type)));
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
						}else{
							float totalSingle=0f;
							if(byMonthTransportMoney!=null&&byMonthTransportMoney.get(monthTitle)!=null&&byMonthTransportMoney.get(monthTitle).get(countryTitle.get(country))!=null&&byMonthTransportMoney.get(monthTitle).get(countryTitle.get(country)).get("0")!=null){
								total+=byMonthTransportMoney.get(monthTitle).get(countryTitle.get(country)).get("0");
								totalSingle+=byMonthTransportMoney.get(monthTitle).get(countryTitle.get(country)).get("0");
							}
							if(byMonthTransportMoney!=null&&byMonthTransportMoney.get(monthTitle)!=null&&byMonthTransportMoney.get(monthTitle).get(countryTitle.get(country))!=null&&byMonthTransportMoney.get(monthTitle).get(countryTitle.get(country)).get("1")!=null){
							 	total+=byMonthTransportMoney.get(monthTitle).get(countryTitle.get(country)).get("1");
							 	totalSingle+=byMonthTransportMoney.get(monthTitle).get(countryTitle.get(country)).get("1");
							}
							if(byMonthTransportMoney!=null&&byMonthTransportMoney.get(monthTitle)!=null&&byMonthTransportMoney.get(monthTitle).get(countryTitle.get(country))!=null&&byMonthTransportMoney.get(monthTitle).get(countryTitle.get(country)).get("2")!=null){
								total+=byMonthTransportMoney.get(monthTitle).get(countryTitle.get(country)).get("2");
								totalSingle+=byMonthTransportMoney.get(monthTitle).get(countryTitle.get(country)).get("2");
							}
							
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSingle);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
						}
						
					}
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(total);
					sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
					
				}
				sheet.addMergedRegion(new CellRangeAddress(index-4,index-1,0,0));
			}else{
				int j=0;
				row=sheet.createRow(index++);
	    		row.setHeight((short) 400);
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(country);
				sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
				Float total=0f;
				for (String monthTitle : byMonthTransportMoney.keySet()) {
					float single=0f;
					if(byMonthTransportMoney!=null&&byMonthTransportMoney.get(monthTitle)!=null&&byMonthTransportMoney.get(monthTitle).get("eu")!=null&&byMonthTransportMoney.get(monthTitle).get("eu").get("0")!=null){
						total+=byMonthTransportMoney.get(monthTitle).get("eu").get("0");
						single+=byMonthTransportMoney.get(monthTitle).get("eu").get("0");
					}
					if(byMonthTransportMoney!=null&&byMonthTransportMoney.get(monthTitle)!=null&&byMonthTransportMoney.get(monthTitle).get("eu")!=null&&byMonthTransportMoney.get(monthTitle).get("eu").get("1")!=null){
						total+=byMonthTransportMoney.get(monthTitle).get("eu").get("1");
						single+=byMonthTransportMoney.get(monthTitle).get("eu").get("1");
					}
					if(byMonthTransportMoney!=null&&byMonthTransportMoney.get(monthTitle)!=null&&byMonthTransportMoney.get(monthTitle).get("eu")!=null&&byMonthTransportMoney.get(monthTitle).get("eu").get("2")!=null){
						total+=byMonthTransportMoney.get(monthTitle).get("eu").get("2");
						single+=byMonthTransportMoney.get(monthTitle).get("eu").get("2");
					}
					if(byMonthTransportMoney!=null&&byMonthTransportMoney.get(monthTitle)!=null&&byMonthTransportMoney.get(monthTitle).get("jp")!=null&&byMonthTransportMoney.get(monthTitle).get("jp").get("0")!=null){
						total+=byMonthTransportMoney.get(monthTitle).get("jp").get("0");
						single+=byMonthTransportMoney.get(monthTitle).get("jp").get("0");
					}
					if(byMonthTransportMoney!=null&&byMonthTransportMoney.get(monthTitle)!=null&&byMonthTransportMoney.get(monthTitle).get("jp")!=null&&byMonthTransportMoney.get(monthTitle).get("jp").get("1")!=null){
						total+=byMonthTransportMoney.get(monthTitle).get("jp").get("1");
						single+=byMonthTransportMoney.get(monthTitle).get("jp").get("1");
					}
					if(byMonthTransportMoney!=null&&byMonthTransportMoney.get(monthTitle)!=null&&byMonthTransportMoney.get(monthTitle).get("jp")!=null&&byMonthTransportMoney.get(monthTitle).get("jp").get("2")!=null){
						total+=byMonthTransportMoney.get(monthTitle).get("jp").get("2");
						single+=byMonthTransportMoney.get(monthTitle).get("jp").get("2");
					}
					if(byMonthTransportMoney!=null&&byMonthTransportMoney.get(monthTitle)!=null&&byMonthTransportMoney.get(monthTitle).get("US")!=null&&byMonthTransportMoney.get(monthTitle).get("US").get("0")!=null){
						total+=byMonthTransportMoney.get(monthTitle).get("US").get("0");
						single+=byMonthTransportMoney.get(monthTitle).get("US").get("0");
					}
					if(byMonthTransportMoney!=null&&byMonthTransportMoney.get(monthTitle)!=null&&byMonthTransportMoney.get(monthTitle).get("US")!=null&&byMonthTransportMoney.get(monthTitle).get("US").get("1")!=null){
						total+=byMonthTransportMoney.get(monthTitle).get("US").get("1");
						single+=byMonthTransportMoney.get(monthTitle).get("US").get("1");
					}
					if(byMonthTransportMoney!=null&&byMonthTransportMoney.get(monthTitle)!=null&&byMonthTransportMoney.get(monthTitle).get("US")!=null&&byMonthTransportMoney.get(monthTitle).get("US").get("2")!=null){
						total+=byMonthTransportMoney.get(monthTitle).get("US").get("2");
						single+=byMonthTransportMoney.get(monthTitle).get("US").get("2");
					}
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(single);
					sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
				}
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(total);
				sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
				
			}
		}
		
		index++;
		row=sheet.createRow(index++);
		row.setHeight((short) 600);
		List<String> title0 = Lists.newArrayList("单价","Model");
		for (String monthTitle : byMonthTransportMoney.keySet()) {
			title0.add(monthTitle);
		}
		title0.add("RMB/KG");
		for (int i = 0; i < title0.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title0.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		Map<String,String> countryTitle0=Maps.newLinkedHashMap();
		countryTitle0.put("EU", "eu");
		countryTitle0.put("US", "US");
		countryTitle0.put("JP", "jp");
		countryTitle0.put("RMB/KG", "total");
		
		Map<String,String> rowTitle0=Maps.newLinkedHashMap();
		rowTitle0.put("AE", "0");
		rowTitle0.put("OE", "1");
		rowTitle0.put("EX", "2");
		rowTitle0.put("Total","3");
		
		for (String country:countryTitle0.keySet()) {
			if(!"RMB/KG".equals(country)){
				for (String type : rowTitle.keySet()) {
					int j=0;
					row=sheet.createRow(index++);
		    		row.setHeight((short) 400);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(country);
					sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(type);
					sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
					Float total=0f;
					Float total1=0f;
					for (String monthTitle : byMonthTransportMoney.keySet()) {
						if(!"Total".equals(type)){
							float single1=0f;
							float single2=0f;
							if(byMonthTransportMoney!=null&&byMonthTransportMoney.get(monthTitle)!=null&&byMonthTransportMoney.get(monthTitle).get(countryTitle0.get(country))!=null&&byMonthTransportMoney.get(monthTitle).get(countryTitle0.get(country)).get(rowTitle0.get(type))!=null){
								total+=byMonthTransportMoney.get(monthTitle).get(countryTitle0.get(country)).get(rowTitle0.get(type));
								single1+=byMonthTransportMoney.get(monthTitle).get(countryTitle0.get(country)).get(rowTitle0.get(type));
							}
							if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get(countryTitle0.get(country))!=null&&byMonthTransport.get(monthTitle).get(countryTitle0.get(country)).get(rowTitle0.get(type))!=null){
								total1+=byMonthTransport.get(monthTitle).get(countryTitle0.get(country)).get(rowTitle0.get(type));
								single2+=byMonthTransport.get(monthTitle).get(countryTitle0.get(country)).get(rowTitle0.get(type));
							}
							
							if(single2>0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(single1/single2);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
						}else{
							float totalSingle=0f;
							float totalSingle1=0f;
							List<String> typeAll=Lists.newArrayList("0","1","2");
							for (String transType : typeAll) {
								if(byMonthTransportMoney!=null&&byMonthTransportMoney.get(monthTitle)!=null&&byMonthTransportMoney.get(monthTitle).get(countryTitle0.get(country))!=null&&byMonthTransportMoney.get(monthTitle).get(countryTitle0.get(country)).get(transType)!=null){
									total+=byMonthTransportMoney.get(monthTitle).get(countryTitle0.get(country)).get(transType);
									totalSingle+=byMonthTransportMoney.get(monthTitle).get(countryTitle0.get(country)).get(transType);
								}
								if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get(countryTitle0.get(country))!=null&&byMonthTransport.get(monthTitle).get(countryTitle0.get(country)).get(transType)!=null){
									total1+=byMonthTransport.get(monthTitle).get(countryTitle0.get(country)).get(transType);
									totalSingle1+=byMonthTransport.get(monthTitle).get(countryTitle0.get(country)).get(transType);
								}
							}
							if(totalSingle1>0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalSingle/totalSingle1);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
							}
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
						}
						
					}
					if(total1>0){
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(total/total1);
					}else{
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
					
				}
				sheet.addMergedRegion(new CellRangeAddress(index-4,index-1,0,0));
			}else{
				int j=0;
				row=sheet.createRow(index++);
	    		row.setHeight((short) 400);
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(country);
				sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
				Float total=0f;
				Float total1=0f;
				for (String monthTitle : byMonthTransportMoney.keySet()) {
					float single=0f;
					float single1=0f;
					List<String> typeAll=Lists.newArrayList("0","1","2");
					List<String> countryAll=Lists.newArrayList("eu","jp","US");
					for (String countryType : countryAll) {
						for (String transType : typeAll) {
							if(byMonthTransportMoney!=null&&byMonthTransportMoney.get(monthTitle)!=null&&byMonthTransportMoney.get(monthTitle).get(countryType)!=null&&byMonthTransportMoney.get(monthTitle).get(countryType).get(transType)!=null){
								total+=byMonthTransportMoney.get(monthTitle).get(countryType).get(transType);
								single+=byMonthTransportMoney.get(monthTitle).get(countryType).get(transType);
							}
							if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get(countryType)!=null&&byMonthTransport.get(monthTitle).get(countryType).get(transType)!=null){
								total1+=byMonthTransport.get(monthTitle).get(countryType).get(transType);
								single1+=byMonthTransport.get(monthTitle).get(countryType).get(transType);
							}
						}
					}
					if(single1>0){
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(single/single1);
					}else{
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
					}
					sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
				}
				if(total1>0){
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(total/total1);
				}else{
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				}
				sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
			}
		}
		
		
		index++;
		row=sheet.createRow(index++);
		row.setHeight((short) 600);
		List<String> title1 = Lists.newArrayList("KGS","Model");
		for (String monthTitle : byMonthTransport.keySet()) {
			title1.add(monthTitle);
		}
		title1.add("Total/KG");
		for (int i = 0; i < title1.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title1.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		Map<String,String> countryTitle1=Maps.newLinkedHashMap();
		countryTitle1.put("EU", "eu");
		countryTitle1.put("US", "US");
		countryTitle1.put("JP", "jp");
		countryTitle1.put("Total/KG", "total");
		
		Map<String,String> rowTitle1=Maps.newLinkedHashMap();
		rowTitle1.put("AE", "0");
		rowTitle1.put("OE", "1");
		rowTitle1.put("EX", "2");
		rowTitle1.put("Total","3");
		//int index=1;
		float total1=0f;
		float total2=0f;
		float total3=0f;
		float total4=0f;
		for (String country:countryTitle1.keySet()) {
			if(!"Total/KG".equals(country)){
				for (String type : rowTitle1.keySet()) {
					int j=0;
					row=sheet.createRow(index++);
		    		row.setHeight((short) 400);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(country);
					sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
		    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(type);
					sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
					Float total=0f;
					for (String monthTitle : byMonthTransport.keySet()) {
						if(!"Total".equals(type)){
							if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get(countryTitle1.get(country))!=null&&byMonthTransport.get(monthTitle).get(countryTitle1.get(country)).get(rowTitle1.get(type))!=null){
								total+=byMonthTransport.get(monthTitle).get(countryTitle1.get(country)).get(rowTitle1.get(type));
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(byMonthTransport.get(monthTitle).get(countryTitle1.get(country)).get(rowTitle1.get(type)));
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
						}else{
							float single=0f;
							if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get(countryTitle1.get(country))!=null&&byMonthTransport.get(monthTitle).get(countryTitle1.get(country)).get("0")!=null){
								total+=byMonthTransport.get(monthTitle).get(countryTitle1.get(country)).get("0");
								single+=byMonthTransport.get(monthTitle).get(countryTitle1.get(country)).get("0");
							}
							if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get(countryTitle1.get(country))!=null&&byMonthTransport.get(monthTitle).get(countryTitle1.get(country)).get("1")!=null){
								total+=byMonthTransport.get(monthTitle).get(countryTitle1.get(country)).get("1");
								single+=byMonthTransport.get(monthTitle).get(countryTitle1.get(country)).get("1");
							}
							if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get(countryTitle1.get(country))!=null&&byMonthTransport.get(monthTitle).get(countryTitle1.get(country)).get("2")!=null){
								total+=byMonthTransport.get(monthTitle).get(countryTitle1.get(country)).get("2");
								single+=byMonthTransport.get(monthTitle).get(countryTitle1.get(country)).get("2");
							}
							
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(single);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
						}
						
					}
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(total);
					sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
					if("EU".equals(country)){
						total1=total;
					}else if("US".equals(country)){
						total2=total;
					}else if("JP".equals(country)){
						total3=total;
					}
				}
				sheet.addMergedRegion(new CellRangeAddress(index-4,index-1,0,0));
			}else{
				int j=0;
				row=sheet.createRow(index++);
	    		row.setHeight((short) 400);
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(country);
				sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
				sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
				Float total=0f;
				for (String monthTitle : byMonthTransport.keySet()) {
					float single=0f;
					List<String> typeAll=Lists.newArrayList("0","1","2");
					List<String> countryAll=Lists.newArrayList("eu","jp","US");
					for (String countryType : countryAll) {
						for (String transType : typeAll) {
							if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get(countryType)!=null&&byMonthTransport.get(monthTitle).get(countryType).get(transType)!=null){
								total+=byMonthTransport.get(monthTitle).get(countryType).get(transType);
								single+=byMonthTransport.get(monthTitle).get(countryType).get(transType);
							}
						}
					}
					row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(single);
					sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
				}
				row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(total);
				sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
				total4=total;
			}
		}
		
		index++;
		row=sheet.createRow(index++);
		row.setHeight((short) 600);
		List<String> title2 = Lists.newArrayList("%","Model");
		for (String monthTitle : byMonthTransport.keySet()) {
			title2.add(monthTitle);
		}
		title2.add("Total/KG");
		for (int i = 0; i < title2.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title2.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		Map<String,String> countryTitle2=Maps.newLinkedHashMap();
		countryTitle2.put("EU", "eu");
		countryTitle2.put("US", "US");
		countryTitle2.put("JP", "jp");
		countryTitle2.put("Total", "total");
		
		Map<String,String> rowTitle2=Maps.newLinkedHashMap();
		rowTitle2.put("AE", "0");
		rowTitle2.put("OE", "1");
		rowTitle2.put("EX", "2");
		for (String country : countryTitle2.keySet()) {
			for (String type : rowTitle2.keySet()) {
				int j=0;
				row=sheet.createRow(index++);
	    		row.setHeight((short) 400);
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(country);
				sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
	    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(type);
				sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
				if(!"Total".equals(country)){
					Float total=0f;
					for (String monthTitle : byMonthTransport.keySet()) {
						float single=0f;
						if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get(countryTitle2.get(country))!=null&&byMonthTransport.get(monthTitle).get(countryTitle2.get(country)).get(rowTitle2.get(type))!=null){
							 if(byMonthTransport.get(monthTitle).get(countryTitle2.get(country)).get("0")!=null){
								 single+=byMonthTransport.get(monthTitle).get(countryTitle2.get(country)).get("0");
							 }
							 if(byMonthTransport.get(monthTitle).get(countryTitle2.get(country)).get("1")!=null){
								 single+=byMonthTransport.get(monthTitle).get(countryTitle2.get(country)).get("1");
							 }
							 if(byMonthTransport.get(monthTitle).get(countryTitle2.get(country)).get("2")!=null){
								 single+=byMonthTransport.get(monthTitle).get(countryTitle2.get(country)).get("2");
							 }
							 total+=byMonthTransport.get(monthTitle).get(countryTitle2.get(country)).get(rowTitle2.get(type));
							 row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(byMonthTransport.get(monthTitle).get(countryTitle2.get(country)).get(rowTitle2.get(type))/single);
							 sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle1);	
						}else{
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle1);	
						}
					}
					if("EU".equals(country)){
						if(total1>0){
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(total/total1);
						}else{
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
					}else if("US".equals(country)){
						if(total2>0){
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(total/total2);
						}else{
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
					}else if("JP".equals(country)){
						if(total3>0){
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(total/total3);
						}else{
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
					}
					sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle1);	
				}else{
					Float total=0f;
					for (String monthTitle : byMonthTransport.keySet()) {
						float totalAE=0f;
						float totalAll=0f;
						if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get("eu")!=null&&byMonthTransport.get(monthTitle).get("eu").get(rowTitle2.get(type))!=null){
							totalAE+=byMonthTransport.get(monthTitle).get("eu").get(rowTitle2.get(type));
							total+=byMonthTransport.get(monthTitle).get("eu").get(rowTitle2.get(type));
						 }
						 if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get("US")!=null&&byMonthTransport.get(monthTitle).get("US").get(rowTitle2.get(type))!=null){
							 totalAE+=byMonthTransport.get(monthTitle).get("US").get(rowTitle2.get(type));
							 total+=byMonthTransport.get(monthTitle).get("US").get(rowTitle2.get(type));
						 }
						 if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get("jp")!=null&&byMonthTransport.get(monthTitle).get("jp").get(rowTitle2.get(type))!=null){
							 totalAE+=byMonthTransport.get(monthTitle).get("jp").get(rowTitle2.get(type));
							 total+=byMonthTransport.get(monthTitle).get("jp").get(rowTitle2.get(type));
						 }
						 
						if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get("eu")!=null&&byMonthTransport.get(monthTitle).get("eu").get("0")!=null){
							totalAll+=byMonthTransport.get(monthTitle).get("eu").get("0");
						 }
						 if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get("US")!=null&&byMonthTransport.get(monthTitle).get("US").get("0")!=null){
							 totalAll+=byMonthTransport.get(monthTitle).get("US").get("0");
						 }
						 if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get("jp")!=null&&byMonthTransport.get(monthTitle).get("jp").get("0")!=null){
							 totalAll+=byMonthTransport.get(monthTitle).get("jp").get("0");
						 }
						 if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get("eu")!=null&&byMonthTransport.get(monthTitle).get("eu").get("1")!=null){
							totalAll+=byMonthTransport.get(monthTitle).get("eu").get("1");
						 }
					     if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get("US")!=null&&byMonthTransport.get(monthTitle).get("US").get("1")!=null){
							totalAll+=byMonthTransport.get(monthTitle).get("US").get("1");
						}
						if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get("jp")!=null&&byMonthTransport.get(monthTitle).get("jp").get("1")!=null){
							totalAll+=byMonthTransport.get(monthTitle).get("jp").get("1");
						} 
						if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get("eu")!=null&&byMonthTransport.get(monthTitle).get("eu").get("2")!=null){
							totalAll+=byMonthTransport.get(monthTitle).get("eu").get("2");
						}
					    if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get("US")!=null&&byMonthTransport.get(monthTitle).get("US").get("2")!=null){
							totalAll+=byMonthTransport.get(monthTitle).get("US").get("2");
						}
						if(byMonthTransport!=null&&byMonthTransport.get(monthTitle)!=null&&byMonthTransport.get(monthTitle).get("jp")!=null&&byMonthTransport.get(monthTitle).get("jp").get("2")!=null){
							totalAll+=byMonthTransport.get(monthTitle).get("jp").get("2");
						} 
						if(totalAll!=0){
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(totalAE/totalAll);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle1);	
						}else{
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle1);	
						}
					}
						if(total4>0){
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(total/total4);
						}else{
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
						}
					   sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle1);	
				}
			}
			sheet.addMergedRegion(new CellRangeAddress(index-3,index-1,0,0));
		}
		
		
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyMMddhhmm");
			String fileName = "transport_" + sdf1.format(new Date()) + ".xls";
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
	
	@RequestMapping(value = "exportAllTransportInfo")
	public String exportAllTransportInfo(String country,PsiTransportPayment psiTransportPayment,HttpServletRequest request,HttpServletResponse response, Model model, RedirectAttributes redirectAttributes,String sureDate,String updateDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM"); 
		if(StringUtils.isBlank(sureDate)){
			Date date=new Date();
			Date endDate=DateUtils.addMonths(date, -12);
			psiTransportPayment.setSureDate(endDate);
			psiTransportPayment.setUpdateDate(date);
		}else{
			try {
				psiTransportPayment.setSureDate(sdf.parse(sureDate));
				psiTransportPayment.setUpdateDate(sdf.parse(updateDate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Map<String,Map<String,Map<String,Map<String,PsiTransportDto>>>> transMap=psiTransportPaymentService.getAllInfoByModel(psiTransportPayment);
		
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet0 = wb.createSheet("全球");
		HSSFSheet sheet1 = wb.createSheet("EU");
		HSSFSheet sheet2 = wb.createSheet("US");
		HSSFSheet sheet3 = wb.createSheet("JP");
		HSSFSheet sheet4 = wb.createSheet("单价");
		HSSFSheet sheet5 = wb.createSheet("重量");
		HSSFSheet sheet6 = wb.createSheet("百分比");
		
		exportAllCountryTransport("total",sheet0,wb,transMap);
		exportAllCountryTransport("EU",sheet1,wb,transMap);
		exportAllCountryTransport("US",sheet2,wb,transMap);
		exportAllCountryTransport("JP",sheet3,wb,transMap);
		exportAllCountryTransportPrice(sheet4,wb,transMap);
		exportAllCountryTransportWeight(sheet5,wb,transMap);
		exportAllCountryTransportRate(sheet6,wb,transMap);
		
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyMMddhhmm");
			String fileName = "transport_"+ sdf1.format(new Date());
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName + ".xls");
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}	
	
	@RequestMapping(value = "exportTotalTransportInfo")
	public String exportTotalTransportInfo(String country,PsiTransportPayment psiTransportPayment,HttpServletRequest request,HttpServletResponse response, Model model, RedirectAttributes redirectAttributes,String sureDate,String updateDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM"); 
		if(StringUtils.isBlank(sureDate)){
			Date date=new Date();
			Date endDate=DateUtils.addMonths(date, -12);
			psiTransportPayment.setSureDate(endDate);
			psiTransportPayment.setUpdateDate(date);
		}else{
			try {
				psiTransportPayment.setSureDate(sdf.parse(sureDate));
				psiTransportPayment.setUpdateDate(sdf.parse(updateDate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Map<String,Map<String,Map<String,Map<String,PsiTransportDto>>>> transMap=psiTransportPaymentService.getTransportInfoByModel(psiTransportPayment);
		
		
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet0 = wb.createSheet("全球");
		HSSFSheet sheet1 = wb.createSheet("EU");
		HSSFSheet sheet2 = wb.createSheet("US");
		HSSFSheet sheet3 = wb.createSheet("JP");
		HSSFSheet sheet4 = wb.createSheet("单价");
		HSSFSheet sheet5 = wb.createSheet("重量");
		HSSFSheet sheet6 = wb.createSheet("百分比");
		
		exportAllCountryTransport("total",sheet0,wb,transMap);
		exportAllCountryTransport("EU",sheet1,wb,transMap);
		exportAllCountryTransport("US",sheet2,wb,transMap);
		exportAllCountryTransport("JP",sheet3,wb,transMap);
		exportAllCountryTransportPrice(sheet4,wb,transMap);
		exportAllCountryTransportWeight(sheet5,wb,transMap);
		exportAllCountryTransportRate(sheet6,wb,transMap);
		
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyMMddhhmm");
			String fileName = "transport_"+ sdf1.format(new Date());
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName + ".xls");
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}	
	
	
//	@RequestMapping(value = "exportAllCountryTransport")
	public void exportAllCountryTransport(String country,HSSFSheet sheet,HSSFWorkbook wb,Map<String,Map<String,Map<String,Map<String,PsiTransportDto>>>> transMap) {
		HSSFRow row = sheet.createRow(0);
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
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
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
        
        HSSFCellStyle cellStyle1 = wb.createCellStyle();
        cellStyle1.setDataFormat(HSSFDataFormat.getBuiltinFormat("0%"));
        cellStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle1.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFCell cell = null;
		
		List<String> title = Lists.newArrayList("月份","产品名称","总运费","总重量","总数量","总单价","空运%","海运%","快递%","空运费","空运重量","空运单价","海运费","海运重量","海运单价","快递费","快递重量","快递单价");
	
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		//月份 国家 产品名称 运输方式
		
		int index=1;
		for ( Map.Entry<String, Map<String, Map<String, Map<String, PsiTransportDto>>>> entry : transMap.entrySet()) {
			String date = entry.getKey();
			Map<String,Map<String,Map<String,PsiTransportDto>>> dateMap=entry.getValue();
			if(dateMap!=null&&dateMap.size()>0){
				Map<String,Map<String,PsiTransportDto>> countryMap=dateMap.get(country);
				if(countryMap!=null&&countryMap.size()>0){
					for (Map.Entry<String,Map<String,PsiTransportDto>> entry1: countryMap.entrySet()) {
						String name = entry1.getKey();
						Map<String,PsiTransportDto> nameMap=entry1.getValue();
                        if(nameMap==null||nameMap.get("total")==null){
							continue;
						}
                        PsiTransportDto trans=nameMap.get("total");
                        if(trans.getQuantity()==0){
                        	continue;
                        }
						int j=0;
						row=sheet.createRow(index++);
			    		row.setHeight((short) 400);
			    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(date);
						sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
						sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
						
						
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(trans.getMoney());
						sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(trans.getWeight());
						sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
						
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(trans.getQuantity());
						sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
						
						row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(trans.getMoney()/trans.getQuantity());
						sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
						
							
						PsiTransportDto airTrans=nameMap.get("0");
						if(airTrans!=null){
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(airTrans.getWeight()/trans.getWeight());
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle1);
						}else{
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle1);
						}
						
						
						PsiTransportDto seaTrans=nameMap.get("1");
						if(seaTrans!=null){
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(seaTrans.getWeight()/trans.getWeight());
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle1);
						}else{
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle1);
						}
						
						PsiTransportDto expressTrans=nameMap.get("2");
						if(expressTrans!=null){
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(expressTrans.getWeight()/trans.getWeight());
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle1);
						}else{
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle1);
						}
						
						if(airTrans!=null){
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(airTrans.getMoney());
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(airTrans.getWeight());
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							if(airTrans.getQuantity()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(airTrans.getMoney()/airTrans.getQuantity());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
						}else{
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
						}
						
						if(seaTrans!=null){
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(seaTrans.getMoney());
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(seaTrans.getWeight());
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							if(seaTrans.getQuantity()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(seaTrans.getMoney()/seaTrans.getQuantity());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
						}else{
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
						}
						
						
						if(expressTrans!=null){
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(expressTrans.getMoney());
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(expressTrans.getWeight());
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							if(expressTrans.getQuantity()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(expressTrans.getMoney()/expressTrans.getQuantity());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
						}else{
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
						}
					}
				}
			}
		}
	}
	
	
	//@RequestMapping(value = "exportAllCountryTransportPrice")
	public void exportAllCountryTransportPrice(HSSFSheet sheet,HSSFWorkbook wb,Map<String,Map<String,Map<String,Map<String,PsiTransportDto>>>> transMap) {
		HSSFRow row = sheet.createRow(0);
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
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
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
        HSSFCell cell = null;
		List<String> title = Lists.newArrayList("月份","产品名称","空运(总计/RMB)","海运(总计/RMB)","快递(总计/RMB)","空运(EU/RMB)","海运(EU/RMB)","快递(EU/RMB)","空运(US/RMB)","海运(US/RMB)","快递(US/RMB)","空运(JP/RMB)","海运(JP/RMB)","快递(JP/RMB)");
	
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		//月份 国家 产品名称 运输方式
		//Map<String,Map<String,Map<String,Map<String,PsiTransportDto>>>> transMap=psiTransportPaymentService.getAllInfoByModel(psiTransportPayment);
		
		int index=1;
		for (Map.Entry<String, Map<String, Map<String, Map<String, PsiTransportDto>>>> entry : transMap.entrySet()) {
			String date = entry.getKey();
			Map<String,Map<String,Map<String,PsiTransportDto>>> dateMap=entry.getValue();
			if(dateMap!=null&&dateMap.size()>0){
					Map<String,Map<String,PsiTransportDto>> countryMap=dateMap.get("total");
					Map<String,Map<String,PsiTransportDto>> usMap=dateMap.get("US");
					Map<String,Map<String,PsiTransportDto>> euMap=dateMap.get("EU");
					Map<String,Map<String,PsiTransportDto>> jpMap=dateMap.get("JP");
					if(countryMap!=null&&countryMap.size()>0){
						for (Map.Entry<String,Map<String,PsiTransportDto>> entry1: countryMap.entrySet()) {
							String name = entry1.getKey();
							Map<String,PsiTransportDto> nameMap=entry1.getValue();
	                        if(nameMap==null||nameMap.get("total")==null){
								continue;
							}
							int j=0;
							row=sheet.createRow(index++);
				    		row.setHeight((short) 400);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(date);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
							
								
							PsiTransportDto airTrans=nameMap.get("0");
							if(airTrans!=null&&airTrans.getQuantity()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(airTrans.getMoney()/airTrans.getQuantity());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							
							PsiTransportDto seaTrans=nameMap.get("1");
							if(seaTrans!=null&&seaTrans.getQuantity()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(seaTrans.getMoney()/seaTrans.getQuantity());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							PsiTransportDto expressTrans=nameMap.get("2");
							if(expressTrans!=null&&expressTrans.getQuantity()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(expressTrans.getMoney()/expressTrans.getQuantity());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(euMap!=null&&euMap.get(name)!=null&&euMap.get(name).get("0")!=null&&euMap.get(name).get("0").getQuantity()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(euMap.get(name).get("0").getMoney()/euMap.get(name).get("0").getQuantity());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(euMap!=null&&euMap.get(name)!=null&&euMap.get(name).get("1")!=null&&euMap.get(name).get("1").getQuantity()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(euMap.get(name).get("1").getMoney()/euMap.get(name).get("1").getQuantity());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(euMap!=null&&euMap.get(name)!=null&&euMap.get(name).get("2")!=null&&euMap.get(name).get("2").getQuantity()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(euMap.get(name).get("2").getMoney()/euMap.get(name).get("2").getQuantity());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							
							if(usMap!=null&&usMap.get(name)!=null&&usMap.get(name).get("0")!=null&&usMap.get(name).get("0").getQuantity()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(usMap.get(name).get("0").getMoney()/usMap.get(name).get("0").getQuantity());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(usMap!=null&&usMap.get(name)!=null&&usMap.get(name).get("1")!=null&&usMap.get(name).get("1").getQuantity()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(usMap.get(name).get("1").getMoney()/usMap.get(name).get("1").getQuantity());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(usMap!=null&&usMap.get(name)!=null&&usMap.get(name).get("2")!=null&&usMap.get(name).get("2").getQuantity()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(usMap.get(name).get("2").getMoney()/usMap.get(name).get("2").getQuantity());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(jpMap!=null&&jpMap.get(name)!=null&&jpMap.get(name).get("0")!=null&&jpMap.get(name).get("0").getQuantity()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(jpMap.get(name).get("0").getMoney()/jpMap.get(name).get("0").getQuantity());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(jpMap!=null&&jpMap.get(name)!=null&&jpMap.get(name).get("1")!=null&&jpMap.get(name).get("1").getQuantity()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(jpMap.get(name).get("1").getMoney()/jpMap.get(name).get("1").getQuantity());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(jpMap!=null&&jpMap.get(name)!=null&&jpMap.get(name).get("2")!=null&&jpMap.get(name).get("2").getQuantity()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(jpMap.get(name).get("2").getMoney()/jpMap.get(name).get("2").getQuantity());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
						}
					}
			}
		}
		
	}
	
	//@RequestMapping(value = "exportAllCountryTransportWeight")
	public void exportAllCountryTransportWeight(HSSFSheet sheet,HSSFWorkbook wb,Map<String,Map<String,Map<String,Map<String,PsiTransportDto>>>> transMap) {
		
		HSSFRow row = sheet.createRow(0);
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
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
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
        HSSFCell cell = null;
		List<String> title = Lists.newArrayList("月份","产品名称","空运(总计/KG)","海运(总计/KG)","快递(总计/KG)","空运(EU/KG)","海运(EU/KG)","快递(EU/KG)","空运(US/KG)","海运(US/KG)","快递(US/KG)","空运(JP/KG)","海运(JP/KG)","快递(JP/KG)");
	
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		//月份 国家 产品名称 运输方式
	//	Map<String,Map<String,Map<String,Map<String,PsiTransportDto>>>> transMap=psiTransportPaymentService.getAllInfoByModel(psiTransportPayment);
		
		int index=1;
		for (Map.Entry<String, Map<String, Map<String, Map<String, PsiTransportDto>>>> entry : transMap.entrySet()) {
			String date = entry.getKey();
			Map<String,Map<String,Map<String,PsiTransportDto>>> dateMap=entry.getValue();
			if(dateMap!=null&&dateMap.size()>0){
					Map<String,Map<String,PsiTransportDto>> countryMap=dateMap.get("total");
					Map<String,Map<String,PsiTransportDto>> usMap=dateMap.get("US");
					Map<String,Map<String,PsiTransportDto>> euMap=dateMap.get("EU");
					Map<String,Map<String,PsiTransportDto>> jpMap=dateMap.get("JP");
					if(countryMap!=null&&countryMap.size()>0){
						for (Map.Entry<String,Map<String,PsiTransportDto>> entry1: countryMap.entrySet()) {
							String name = entry1.getKey();
							Map<String,PsiTransportDto> nameMap=entry1.getValue();
	                        if(nameMap==null||nameMap.get("total")==null){
								continue;
							}
							int j=0;
							row=sheet.createRow(index++);
				    		row.setHeight((short) 400);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(date);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
							
								
							PsiTransportDto airTrans=nameMap.get("0");
							if(airTrans!=null&&airTrans.getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(airTrans.getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							
							PsiTransportDto seaTrans=nameMap.get("1");
							if(seaTrans!=null&&seaTrans.getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(seaTrans.getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							PsiTransportDto expressTrans=nameMap.get("2");
							if(expressTrans!=null&&expressTrans.getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(expressTrans.getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(euMap!=null&&euMap.get(name)!=null&&euMap.get(name).get("0")!=null&&euMap.get(name).get("0").getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(euMap.get(name).get("0").getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(euMap!=null&&euMap.get(name)!=null&&euMap.get(name).get("1")!=null&&euMap.get(name).get("1").getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(euMap.get(name).get("1").getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(euMap!=null&&euMap.get(name)!=null&&euMap.get(name).get("2")!=null&&euMap.get(name).get("2").getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(euMap.get(name).get("2").getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							
							if(usMap!=null&&usMap.get(name)!=null&&usMap.get(name).get("0")!=null&&usMap.get(name).get("0").getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(usMap.get(name).get("0").getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(usMap!=null&&usMap.get(name)!=null&&usMap.get(name).get("1")!=null&&usMap.get(name).get("1").getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(usMap.get(name).get("1").getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(usMap!=null&&usMap.get(name)!=null&&usMap.get(name).get("2")!=null&&usMap.get(name).get("2").getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(usMap.get(name).get("2").getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(jpMap!=null&&jpMap.get(name)!=null&&jpMap.get(name).get("0")!=null&&jpMap.get(name).get("0").getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(jpMap.get(name).get("0").getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(jpMap!=null&&jpMap.get(name)!=null&&jpMap.get(name).get("1")!=null&&jpMap.get(name).get("1").getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(jpMap.get(name).get("1").getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(jpMap!=null&&jpMap.get(name)!=null&&jpMap.get(name).get("2")!=null&&jpMap.get(name).get("2").getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(jpMap.get(name).get("2").getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
						}
					}
			}
		}
		
		
	}
	
	
	public void exportAllCountryTransportRate(HSSFSheet sheet,HSSFWorkbook wb,Map<String,Map<String,Map<String,Map<String,PsiTransportDto>>>> transMap) {
		
		HSSFRow row = sheet.createRow(0);
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
		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
        
        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0%"));
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        cellStyle.setBottomBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        cellStyle.setLeftBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        cellStyle.setRightBorderColor(HSSFColor.BLACK.index);
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        cellStyle.setTopBorderColor(HSSFColor.BLACK.index);
        
        HSSFCell cell = null;
		List<String> title = Lists.newArrayList("月份","产品名称","空运(总计)","海运(总计)","快递(总计)","空运(EU)","海运(EU)","快递(EU)","空运(US)","海运(US)","快递(US)","空运(JP)","海运(JP)","快递(JP)");
	
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		//月份 国家 产品名称 运输方式
		//Map<String,Map<String,Map<String,Map<String,PsiTransportDto>>>> transMap=psiTransportPaymentService.getAllInfoByModel(psiTransportPayment);
		
		int index=1;
		for (Map.Entry<String, Map<String, Map<String, Map<String, PsiTransportDto>>>> entry : transMap.entrySet()) {
			String date = entry.getKey();
			Map<String,Map<String,Map<String,PsiTransportDto>>> dateMap=entry.getValue();
			if(dateMap!=null&&dateMap.size()>0){
					Map<String,Map<String,PsiTransportDto>> countryMap=dateMap.get("total");
					Map<String,Map<String,PsiTransportDto>> usMap=dateMap.get("US");
					Map<String,Map<String,PsiTransportDto>> euMap=dateMap.get("EU");
					Map<String,Map<String,PsiTransportDto>> jpMap=dateMap.get("JP");
					if(countryMap!=null&&countryMap.size()>0){
						for (Map.Entry<String,Map<String,PsiTransportDto>> entry1: countryMap.entrySet()) {
							String name = entry1.getKey();
							Map<String,PsiTransportDto> nameMap=entry1.getValue();
	                        if(nameMap==null||nameMap.get("total")==null){
								continue;
							}
	                        PsiTransportDto trans=nameMap.get("total");
	                        if(trans.getWeight()==0){
	                        	continue;
	                        }
							
							int j=0;
							row=sheet.createRow(index++);
				    		row.setHeight((short) 400);
				    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(date);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
							row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
							sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
							
							PsiTransportDto airTrans=nameMap.get("0");
							if(airTrans!=null){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(airTrans.getWeight()/trans.getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							
							PsiTransportDto seaTrans=nameMap.get("1");
							if(seaTrans!=null){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(seaTrans.getWeight()/trans.getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							PsiTransportDto expressTrans=nameMap.get("2");
							if(expressTrans!=null){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(expressTrans.getWeight()/trans.getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							
							if(euMap!=null&&euMap.get(name)!=null&&euMap.get(name).get("0")!=null&&euMap.get(name).get("total").getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(euMap.get(name).get("0").getWeight()/euMap.get(name).get("total").getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(euMap!=null&&euMap.get(name)!=null&&euMap.get(name).get("1")!=null&&euMap.get(name).get("total").getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(euMap.get(name).get("1").getWeight()/euMap.get(name).get("total").getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(euMap!=null&&euMap.get(name)!=null&&euMap.get(name).get("2")!=null&&euMap.get(name).get("total").getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(euMap.get(name).get("2").getWeight()/euMap.get(name).get("total").getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							
							if(usMap!=null&&usMap.get(name)!=null&&usMap.get(name).get("0")!=null&&usMap.get(name).get("total").getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(usMap.get(name).get("0").getWeight()/usMap.get(name).get("total").getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(usMap!=null&&usMap.get(name)!=null&&usMap.get(name).get("1")!=null&&usMap.get(name).get("total").getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(usMap.get(name).get("1").getWeight()/usMap.get(name).get("total").getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(usMap!=null&&usMap.get(name)!=null&&usMap.get(name).get("2")!=null&&usMap.get(name).get("total").getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(usMap.get(name).get("2").getWeight()/usMap.get(name).get("total").getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(jpMap!=null&&jpMap.get(name)!=null&&jpMap.get(name).get("0")!=null&&jpMap.get(name).get("total").getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(jpMap.get(name).get("0").getWeight()/jpMap.get(name).get("total").getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(jpMap!=null&&jpMap.get(name)!=null&&jpMap.get(name).get("1")!=null&&jpMap.get(name).get("total").getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(jpMap.get(name).get("1").getWeight()/jpMap.get(name).get("total").getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
							
							if(jpMap!=null&&jpMap.get(name)!=null&&jpMap.get(name).get("2")!=null&&jpMap.get(name).get("total").getWeight()!=0){
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(jpMap.get(name).get("2").getWeight()/jpMap.get(name).get("total").getWeight());
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}else{
								row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(0);
								sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
							}
						}
					}
			}
		}
	}
	
	

	@RequestMapping(value = "byMonthTransportOffLine")
	public String byMonthTransportOffLine(PsiTransportPayment psiTransportPayment,String productName, Model model, RedirectAttributes redirectAttributes,String sureDate,String updateDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM"); 
		SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy"); 
		List<String>  productList = this.productService.findProductNameAndColorList();
		if(StringUtils.isBlank(sureDate)){
			Date date=new Date();
			Date endDate=DateUtils.getYearFirst(Integer.parseInt(yearSdf.format(date))); 
			psiTransportPayment.setSureDate(endDate);
			psiTransportPayment.setUpdateDate(date);
		}else{
			try {
				psiTransportPayment.setSureDate(sdf.parse(sureDate));
				psiTransportPayment.setUpdateDate(sdf.parse(updateDate));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		Map<String,Map<String,Map<String,PsiTransportDto>>>  offLineMap=psiTransportPaymentService.getOffLine(psiTransportPayment,productName);
		model.addAttribute("offLineMap",offLineMap);
		model.addAttribute("psiTransportPayment", psiTransportPayment);
		model.addAttribute("productList", productList);
		model.addAttribute("productName",productName);
		return "modules/psi/psiTransportModelReportByOffLine";
	}
	
	 @RequestMapping(value = "exportOffLineTransport")
     public String exportOffLineTransport(PsiTransportPayment psiTransportPayment,HttpServletRequest request,HttpServletResponse response,String sureDate,String updateDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM"); 
		SimpleDateFormat yearSdf = new SimpleDateFormat("yyyy"); 
		if(StringUtils.isBlank(sureDate)){
				Date date=new Date();
				Date endDate=DateUtils.getYearFirst(Integer.parseInt(yearSdf.format(date))); 
				psiTransportPayment.setSureDate(endDate);
				psiTransportPayment.setUpdateDate(date);
		}else{
				try {
					psiTransportPayment.setSureDate(sdf.parse(sureDate));
					psiTransportPayment.setUpdateDate(sdf.parse(updateDate));
				} catch (ParseException e) {
					e.printStackTrace();
				}
		}
			
    	HSSFWorkbook wb = new HSSFWorkbook();
   		HSSFSheet sheet = wb.createSheet();
   		HSSFRow row = sheet.createRow(0);
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
   		contentStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
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
        
        HSSFCell cell = null;
		List<String> title = Lists.newArrayList("月份","市场","运输方式","产品","金额","个数","重量","箱数","体积");
	
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		
		
		//月份 国家 产品名称 运输方式
		Map<String,Map<String,Map<String,Map<String,PsiTransportDto>>>> transMap=psiTransportPaymentService. getOffLineByModel( psiTransportPayment);
		Map<String, String>  productMap=productService.getVomueAndWeightByName();
		
		int index=1;
		for (Map.Entry<String, Map<String, Map<String, Map<String, PsiTransportDto>>>> entry : transMap.entrySet()) {
			String date = entry.getKey();
			if(!"total".equals(date)){
				Map<String,Map<String,Map<String,PsiTransportDto>>> dateMap=entry.getValue();
				if(dateMap!=null&&dateMap.size()>0){
					    for (Map.Entry<String,Map<String,Map<String,PsiTransportDto>>> countryEntry : dateMap.entrySet()) {
					    	String country = countryEntry.getKey();
					      if(!"total".equals(country)){
					    	Map<String,Map<String,PsiTransportDto>> countryMap=countryEntry.getValue();
					    	for (Map.Entry<String,Map<String,PsiTransportDto>> entry1: countryMap.entrySet()) {
								String name = entry1.getKey();
					    		Map<String,PsiTransportDto> nameMap=entry1.getValue();
					    		if(nameMap!=null&&nameMap.size()>0){
									for (Map.Entry<String,PsiTransportDto> entry2: nameMap.entrySet()) {
										String model = entry2.getKey();
										if(!"total".equals(model)){
											int j=0;
											row=sheet.createRow(index++);
								    		row.setHeight((short) 400);
								    		row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(date);
											sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
											row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(country);
											sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
											row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("0".equals(model)?"AE":("1".equals(model)?"OE":"EX"));
											sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
											row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(name);
											sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
											
											PsiTransportDto dto=entry2.getValue();
											row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getMoney());
											sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
											row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getQuantity());
											sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
											row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(dto.getWeight());
											sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
											
											if(productMap!=null&&productMap.get(name)!=null){
												String[] arr=productMap.get(name).split(",");
												Integer pack=Integer.parseInt(arr[2]);//a.`box_volume`,a.`gw`,a.`pack_quantity`
												if(name.contains("Inateck DB1001")){
													if("com,uk,jp,ca,mx,".contains(country)){
														pack=60;
													}else{
														pack=44;
													}
												}else if(name.contains("Inateck DB2001")){
													if("com,jp,ca,mx,".contains(country)){
														pack=32;
													}else{
														pack=24;
													}
												}
												
												Integer boxNum=MathUtils.roundUp(dto.getQuantity()*1.0d/pack);
												row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(boxNum);
												sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
												
												row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue(boxNum*Float.parseFloat(arr[0]));
												sheet.getRow(index-1).getCell(j-1).setCellStyle(cellStyle);
											}else{
												row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
												sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
												
												row.createCell(j++,Cell.CELL_TYPE_STRING).setCellValue("");
												sheet.getRow(index-1).getCell(j-1).setCellStyle(contentStyle);
											}
											
										}
									}
					    			
					    		}
					    		
							}
					    }	
					}
				}
			}
			
		}

		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyMMddhhmm");
			String fileName = "offLineTransport_"+ sdf1.format(new Date());
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName + ".xls");
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null;
	}
	
}
