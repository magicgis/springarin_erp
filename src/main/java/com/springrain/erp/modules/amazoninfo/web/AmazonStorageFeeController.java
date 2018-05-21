package com.springrain.erp.modules.amazoninfo.web;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.excel.ExportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonMonthlyStorageFees;
import com.springrain.erp.modules.amazoninfo.service.AmazonStorageFeeService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;

/**
 * 亚马逊长期仓储、月仓储汇总Controller
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/storageFee")
public class AmazonStorageFeeController extends BaseController {
	@Autowired
	private AmazonStorageFeeService amazonStorageFeeService;
	
    @Autowired
    private PsiProductTypeGroupDictService groupDictService;
    
    @Autowired
    private PsiProductService psiProductService;
    
	@RequestMapping(value = {"list", ""})
	public String list(HttpServletRequest request,String startDate,HttpServletResponse response, Model model) {
		String country = request.getParameter("country");
		String start = request.getParameter("startDate");
		String end = request.getParameter("endDate");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		Date date = new Date();
		List<AmazonMonthlyStorageFees> list = null;
		
		Calendar calendar = Calendar.getInstance();
		if (!StringUtils.isNotBlank(start) && !StringUtils.isNotBlank(end)){//默认查询时间
			calendar.setTime(date);
			calendar.add(Calendar.MONTH, -5);
			model.addAttribute("startDate", calendar.getTime());
			model.addAttribute("endDate", date);
		    list = amazonStorageFeeService.findStorageFees(format.format(calendar.getTime()),format.format(date),
		               dateFormat.format(calendar.getTime()), dateFormat.format(date), country);
		} else {
			try {
				Date endLongTerm = dateFormat.parse(end);
				calendar.setTime(endLongTerm);
				calendar.roll(Calendar.DATE, -1);
				model.addAttribute("startDate", dateFormat.parse(start));
				model.addAttribute("endDate", dateFormat.parse(end));
			    list = amazonStorageFeeService.findStorageFees(format.format(dateFormat.parse(start)), 
			               format.format(calendar.getTime()), start, end, country);
				}
			catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
			list = (list.size() > 0) ? list : null;
			model.addAttribute("maxDate", date);
			model.addAttribute("storageFee", list);
			model.addAttribute("country", country);
			model.addAttribute("typeLineMap", groupDictService.getTypeLine(null));
		    model.addAttribute("nameTypeMap", psiProductService.findProductTypeMap());
		return "modules/amazoninfo/amazonStorageFeeList";
	}
	
	@RequestMapping(value = {"findFeeByTime"})
	@ResponseBody
	public String findFeeByTime(String country, String productName,String start,
			String end,String type,HttpServletRequest request,HttpServletResponse response, Model model) throws ParseException {
		return amazonStorageFeeService.findStorageByMonthAndLong(start, end, productName, country, type);
	}
	
	@RequestMapping(value = {"export"})
	public void export(String country, String start, String end, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		Date startdDate = dateFormat.parse(start);
		Date endDate = dateFormat.parse(end);
		endDate = DateUtils.getLastDayOfMonth(endDate);
	    List<AmazonMonthlyStorageFees> list = amazonStorageFeeService.findStorageFees(format.format(startdDate),
	    		format.format(endDate), start, end, country);
		 //title
        ExportExcel export = new ExportExcel("StorageFee", Lists.newArrayList("ProductName","longestSide","medianSide","shortestSide",
                "measurementUnits","TotalMonthStorageFee","TotalLongStorageFee", "totalFee"));
        Row exportMessage = export.addRow(); 
        exportMessage.getSheet().addMergedRegion(new CellRangeAddress(2, 2, 0, 7));
        export.addCell(exportMessage, 0,"时间：" + start + " ~ "
            + end + "    平台： " + (country != "" ? country : "汇总")+"(所有费用单位均为欧元)" );
        for (AmazonMonthlyStorageFees storageFee : list) {
        	Row row = export.addRow();
        	export.addCell(row, 0, storageFee.getProductName());
        	export.addCell(row, 1, storageFee.getLongestSideNew());
            export.addCell(row, 2, storageFee.getMedianSideNew());
            export.addCell(row, 3, storageFee.getShortestSideNew());
            export.addCell(row, 4, storageFee.getMeasurementUnits());
        	export.addCell(row, 5, storageFee.getTotalMonthFee());
        	export.addCell(row, 6, storageFee.getTotalLongFee());
        	export.addCell(row, 7, storageFee.getTotalFee());
		 }
        try {
			export.write(response, "StorageFee" + DateUtils.getDate() + ".xlsx").dispose();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
