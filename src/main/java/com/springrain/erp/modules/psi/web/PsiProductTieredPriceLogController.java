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

import org.apache.commons.lang3.StringUtils;
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

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductTieredPrice;
import com.springrain.erp.modules.psi.entity.PsiProductTieredPriceDto;
import com.springrain.erp.modules.psi.entity.PsiProductTieredPriceLog;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.parts.PsiParts;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTieredPriceLogService;
import com.springrain.erp.modules.psi.service.PsiProductTieredPriceService;
import com.springrain.erp.modules.psi.service.PsiSupplierService;
import com.springrain.erp.modules.psi.service.PurchaseOrderService;

@Controller
@RequestMapping(value = "${adminPath}/psi/productTieredPriceLog")
public class PsiProductTieredPriceLogController extends BaseController {
	@Autowired
	private      PsiProductTieredPriceLogService    logService;
	@Autowired
	private      PsiSupplierService                  psiSupplierService;
	@Autowired
	private      PurchaseOrderService                purchaseOrderService;
	@RequestMapping(value = { "list", "" })
	public String list(PsiProductTieredPriceLog log,HttpServletRequest request,HttpServletResponse response, Model model) throws ParseException {
		if(StringUtils.isEmpty(log.getProductIdColor())&&(log.getProduct()!=null&&log.getProduct().getId()!=null)){
			log.setProductIdColor(log.getProduct().getId()+","+log.getColor());
		}
		SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(log.getCreateTime()==null){
			log.setUpdateTime(sdf.parse(sdf.format(new Date())));
			log.setCreateTime(DateUtils.addYears(log.getUpdateTime(), -1));
		}
		
		Page<PsiProductTieredPriceLog> page=new Page<PsiProductTieredPriceLog>(request, response);
		page =this.logService.find(page,log);
		List<PsiSupplier> suppliers=this.psiSupplierService.findAll();
		Map<String,String> proColorMap =this.purchaseOrderService.getAllProductColors(); 
		model.addAttribute("proColorMap",proColorMap);
		model.addAttribute("suppliers",suppliers);
		model.addAttribute("priceLog",log);
		model.addAttribute("page",page);
		return "modules/psi/psiProductTieredPriceLog";
	}
	
	
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value = "exp")
	public String exp(PsiProductTieredPriceLog log, Model model, HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException, ParseException {
		Page<PsiProductTieredPriceLog> page=new Page<PsiProductTieredPriceLog>(request, response);
		page.setPageSize(600000);
		SimpleDateFormat  sdf =new SimpleDateFormat("yyyy-MM-dd");
		if(log.getCreateTime()==null){
			log.setUpdateTime(sdf.parse(sdf.format(new Date())));
			log.setCreateTime(DateUtils.addMonths(log.getUpdateTime(), -1));
		}
		this.logService.find(page,log);
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet();
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		
		String[] title = { " 日期  ","   供应商     ","  产品型号   ","  产品线 "," MOQ  " ," 阶梯级 "," 调整前  ","  调整后   ","  调整   ","  调整比例 ","  原因   ","  备注  ","操作人"};
	    style.setFillBackgroundColor(HSSFColor.GREY_50_PERCENT.index);
	    style.setFillForegroundColor(HSSFColor.GREY_50_PERCENT.index);
	    style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
	    //设置字体
	    HSSFFont font = wb.createFont();
	    font.setFontHeightInPoints((short) 11); // 字体高度
	    style.setFont(font);
	    row.setHeight((short) 400);
	    HSSFCell cell = null;		
	    for (int i = 0; i < title.length; i++) {
			cell = row.createCell(i);
			cell.setCellValue(title[i]);
			cell.setCellStyle(style);
		}
	    
		int j =1;
		for(PsiProductTieredPriceLog price:page.getList()){
			int i =0;
			row = sheet.createRow(j++);
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(sdf.format(price.getCreateTime())); 
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(price.getSupplier().getNikename()); 
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(price.getProductNameColor());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(price.getProduct().getType());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(price.getProduct().getMinOrderPlaced());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(price.getTieredType());
			if(price.getOldPrice()!=null){
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(price.getOldPrice());
			}else{
				i++;
			}
			if(price.getPrice()!=null){
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(price.getPrice());
			}else{
				i++;
			}
			if(price.getOldPrice()!=null&&price.getPrice()!=null){
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(price.getOldPrice()-price.getPrice());
				row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue((float)Math.round((price.getOldPrice()-price.getPrice())*10000/price.getOldPrice())/10);
			}else{
				i++;
				i++;
			}
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(price.getContent());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(price.getRemark());
			row.createCell(i++,Cell.CELL_TYPE_STRING).setCellValue(price.getCreateUser().getLoginName());
		}
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
		SimpleDateFormat sdf1 = new SimpleDateFormat("MMddhhmm");
		String fileName = "priceTiered" + sdf1.format(new Date()) + ".xls";
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
