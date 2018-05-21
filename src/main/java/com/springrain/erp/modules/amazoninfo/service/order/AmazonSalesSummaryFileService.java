package com.springrain.erp.modules.amazoninfo.service.order;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.CountryCode;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.ZipUtil;
import com.springrain.erp.modules.amazoninfo.dao.order.AmazonSalesSummaryFileDao;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonSalesSummaryFile;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;

/**
 * 订单按月报表Service
 */
@SuppressWarnings("all")
@Component
@Transactional(readOnly = true)
public class AmazonSalesSummaryFileService extends BaseService {
	
	@Autowired
	private AmazonProduct2Service amazonProduc2tService;
	
	@Autowired
	private AmazonOrderService amazonOrderService;

	@Autowired
	private AmazonSalesSummaryFileDao amazonSalesSummaryFileDao;
	
	public AmazonSalesSummaryFile get(Integer id) {
		return amazonSalesSummaryFileDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(AmazonSalesSummaryFile amazonSalesSummaryFile) {
		amazonSalesSummaryFileDao.save(amazonSalesSummaryFile);
	}
	
	public Page<AmazonSalesSummaryFile> find(Page<AmazonSalesSummaryFile> page, AmazonSalesSummaryFile amazonSalesSummaryFile, String end) {
		DetachedCriteria dc = amazonSalesSummaryFileDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(amazonSalesSummaryFile.getMonth())){
			dc.add(Restrictions.ge("month", amazonSalesSummaryFile.getMonth()));
		}
		if(StringUtils.isNotEmpty(end)){
			dc.add(Restrictions.le("month", end));
		}
		if(StringUtils.isNotEmpty(amazonSalesSummaryFile.getPlatform())){
			dc.add(Restrictions.eq("platform", amazonSalesSummaryFile.getPlatform()));
		}
		if(StringUtils.isNotEmpty(amazonSalesSummaryFile.getType())){
			dc.add(Restrictions.eq("type", amazonSalesSummaryFile.getType()));
		}
		return amazonSalesSummaryFileDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void generateReports(String month, String country) throws Exception, ParseException {
			
	    Map<String, Float> sales = findSales(month,country);
	    if (sales == null || sales.size() == 0) {
			return;
		}
	    
	    HSSFWorkbook wb = new HSSFWorkbook();
	    HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
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
		
		HSSFFont red = wb.createFont();
		red.setColor(HSSFFont.COLOR_RED);
		HSSFCellStyle redCell = wb.createCellStyle();
		redCell.setFont(red);
		
		style.setFont(font);
	  	HSSFSheet sheet = wb.createSheet("Total Sales");
	  	int i = 0 ;
	  	HSSFCell cell = null;
	  	//按月查询前一个月退款明细map(结算报告延迟,往前推一个月)value:时间_退款数
		Map<String, String> refundMap = getOrderReturnMoney(month, country);
	  	//退款订单信息(账单号，收货地址，邮编等)
		Map<String, Object[]> refundOrderMap = getrefundOrderDetail(refundMap.keySet());
	  	//当月退货map
		//Map<String, List<Object[]>> returnGoodsMap = getReturnOrder(month, country);
		//订单号与国家编号关系map
		Map<String, String> idAndCode = findCountryCode(refundMap.keySet());
		//分国家统计订单退款
		Map<String, List<Object[]>> returnGoodsCountryMap = Maps.newLinkedHashMap();
		 for (Map.Entry<String, String> entry : refundMap.entrySet()) { 
		    String key =entry.getKey();
			String countryCode = idAndCode.get(key);
			//amazon_order_id,order_id,address,sales_channel
			Object[] obj = refundOrderMap.get(key);
			if (obj == null) {
				continue;
			}
			//amazon_order_id,order_id,address,sales_channel,posted_date,refund
			Object[] objs = new Object[obj.length+2];
			if (StringUtils.isNotEmpty(countryCode)) {
				List<Object[]> list = returnGoodsCountryMap.get(countryCode);
				if (list == null) {
					list = Lists.newArrayList();
					returnGoodsCountryMap.put(countryCode, list);
				}
				for (int j = 0; j < obj.length; j++) {
					objs[j] = obj[j];
				}
				objs[objs.length-2] = entry.getValue().split("_")[0];
				objs[objs.length-1] = entry.getValue().split("_")[1];
				list.add(objs);
			}
		}
		//再开一个tab页
		List<Object[]> list = findExp(month,country);
		Map<String,List<Object[]>> dataMap = Maps.newHashMap();
		Set<String> codes = sales.keySet();
		for (Object[] objects : list) {
			if(objects[2]!=null){
				String key = objects[2].toString();
				String[] tempStrs = key.split(",");
				key = tempStrs[tempStrs.length-2];
				if(!codes.contains(key)){
					key = tempStrs[tempStrs.length-1];
				}
				if(!codes.contains(key)){
					continue;
				}	
				List<Object[]> temp = dataMap.get(key);
				if(temp==null){
					temp = Lists.newArrayList();
					dataMap.put(key, temp);
				}
				temp.add(objects);
			}
		}
		Date monthDate = DateUtils.parseDate(month+"-1",new String[]{"yyyy-MM-dd"});
		Float rate = amazonProduc2tService.findAvgMonthRate("GBP/EUR", monthDate);
		 for (Map.Entry<String,Float> entry : sales.entrySet()) { 
	  	    String countryCode =entry.getKey();
	  		HSSFRow row = sheet.createRow(i++);
	  		row.setHeight((short) 600);
	  		String name = countryCode;
	  		float vat  = 0f;
	  		if("eu".equalsIgnoreCase(country)){
				try {
					CountryCode code = CountryCode.valueOf(countryCode);
					if(code!=null){
		  				vat = code.getVat();
		  				name = code.getName();
		  			}
				} catch (Exception e) {}
	  		}
			cell = row.createCell(0);
			cell.setCellValue(name);
			cell = row.createCell(1);
			if(vat ==0f){
				cell.setCellValue("-");
			}else{
				cell.setCellValue(vat+"%");
			}
			cell = row.createCell(2);
			BigDecimal temp = new BigDecimal(entry.getValue());
			temp = temp.setScale(2,BigDecimal.ROUND_HALF_UP);
			cell.setCellValue(temp.doubleValue());	
			HSSFSheet sheet1 = wb.createSheet(name);
			HSSFRow row1 = sheet1.createRow(0);

			String[] title = { "Invoice Id", "AmazonOrderId", "Receive Address","Zip Codes","Purchase Date", "After-Tax",
					"Per-Tax", "Tax Rate"," On_line Order","Sales Channel"};
			
			row1.setHeight((short) 600);
			HSSFCell cell2 = null;						
			for (int j = 0; j < title.length; j++) {
				cell2 = row1.createCell(j);
				cell2.setCellValue(title[j]);
				cell2.setCellStyle(style);
			}
			HSSFCell cell1 = null;	
			// 分国家输出亚马逊excel订单
			List<Object[]> list1 = dataMap.get(countryCode);
			HSSFSheet sheet2 = null;
			HSSFRow row2 = null;
			if (list1.size()>60000) {	//大于6万分两个sheet
				sheet2 = wb.createSheet(name+"-1");
				row2 = sheet2.createRow(0);
				row2.setHeight((short) 600);
				for (int j = 0; j < title.length; j++) {
					cell2 = row2.createCell(j);
					cell2.setCellValue(title[j]);
					cell2.setCellStyle(style);
				}
			}
			int rowIndex = 0;
			boolean flag = false;	//标记是否换页了
			for (int j =0;j<list1.size();j++ ) {
				if (j == 60000) {
					rowIndex = 0;	//6万以后换table页
					flag = true;
				}
				rowIndex += 1;
				if (flag) {
					row1 = sheet2.createRow(rowIndex);
				} else {
					row1 = sheet1.createRow(rowIndex);
				}
				
				Object[] objs = list1.get(j);
				row1.createCell((short) 0).setCellValue(objs[0].toString());
				String orderId = objs[1].toString();
				HSSFCell cell3 = row1.createCell((short) 1);
				cell3.setCellValue(orderId);
				if(refundMap.get(orderId)!=null){
					cell3.setCellStyle(redCell);
				}
				
				String adress = (objs[2]==null?" ":objs[2].toString());
				row1.createCell((short) 2).setCellValue(adress);
				String zipCode = "";
				if(StringUtils.isNotEmpty(adress)){
					zipCode = adress.substring(adress.lastIndexOf(",")+1);
				}
				row1.createCell((short) 3).setCellValue(zipCode);
				row1.createCell((short) 4).setCellValue(objs[3]==null?"":objs[3].toString());

				String country1 = objs[7].toString();
				double afterTax = Double.parseDouble(objs[4]==null?"0":objs[4].toString());
				
				
				if("Amazon.co.uk".equals(country1)&&!"GB".equals(countryCode)){
					double gbafterTax = afterTax*rate;
					BigDecimal bd = new BigDecimal(gbafterTax);
					bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
					gbafterTax =bd.doubleValue() ;
					row1.createCell((short) 11).setCellValue(gbafterTax);
					row1.createCell((short) 12).setCellValue(rate);
				}
				
				cell1=row1.createCell((short) 5);
				cell1.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell1.setCellValue(afterTax);
				float vat1 = vat;
				if("Amazon.co.uk,Amazon.de,Amazon.fr,Amazon.es,Amazon.it".contains(country1)){
					if(vat == 0f){
						String temp1 = country1.replace("Amazon.", "").replace("co.", "");
						if("uk".equals(temp1)){
							temp1 = "gb";
						}
						CountryCode code = CountryCode.valueOf(temp1.toUpperCase());
						vat1 = code.getVat();
					}	
				}
				
				cell1=row1.createCell((short)6);
				cell1.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				double prePrice = afterTax*100/(100+vat1);
				BigDecimal bd = new BigDecimal(prePrice);
				bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
				cell1.setCellValue(bd.doubleValue());
				
				row1.createCell((short) 7).setCellValue(vat1+"%");
				
				row1.createCell((short) 8).setCellValue(objs[6].toString());
				row1.createCell((short) 9).setCellValue(country1);
			}
			//加入退货订单
			if(returnGoodsCountryMap.get(countryCode)!=null && returnGoodsCountryMap.get(countryCode).size()>0){
				if (sheet2 != null) {
					row1 = sheet2.createRow(sheet2.getLastRowNum()+1);
					row1 = sheet2.createRow(sheet2.getLastRowNum()+1);
					row1 = sheet2.createRow(sheet2.getLastRowNum()+1);
					row1 = sheet2.createRow(sheet2.getLastRowNum()+1);
					row1 = sheet2.createRow(sheet2.getLastRowNum()+1);
				} else {
					row1 = sheet1.createRow(sheet1.getLastRowNum()+1);
					row1 = sheet1.createRow(sheet1.getLastRowNum()+1);
					row1 = sheet1.createRow(sheet1.getLastRowNum()+1);
					row1 = sheet1.createRow(sheet1.getLastRowNum()+1);
					row1 = sheet1.createRow(sheet1.getLastRowNum()+1);
				}
				String[] title1 = { "Invoice Id", "AmazonOrderId", "Receive Address","Zip Codes","Posted Date","After-Tax",
						"Per-Tax", "Tax Rate","Sales Channel"};
				row1.setHeight((short) 600);
				for (int j = 0; j < title1.length; j++) {
					cell2 = row1.createCell(j);
					cell2.setCellValue(title1[j]);
					cell2.setCellStyle(style);
				}
				//objs:amazon_order_id,order_id,address,sales_channel,posted_date,refund
				for (Object[] objs : returnGoodsCountryMap.get(countryCode)) {
					if (sheet2 != null) {
						row1 = sheet2.createRow(sheet2.getLastRowNum()+1);
					} else {
						row1 = sheet1.createRow(sheet1.getLastRowNum()+1);
					}
					row1.createCell((short) 0).setCellValue(objs[1].toString());
					String orderId = objs[0].toString();
					row1.createCell((short) 1).setCellValue(orderId);
					String address = objs[2].toString();
					row1.createCell((short) 2).setCellValue(address);
					String zipCode = "";
					if(StringUtils.isNotEmpty(address)){
						zipCode = address.substring(address.lastIndexOf(",")+1);
					}
					row1.createCell((short) 3).setCellValue(zipCode);
					row1.createCell((short) 4).setCellValue(objs[4].toString());
					
					double price = Double.parseDouble(objs[5]==null?"0":objs[5].toString());
					String country1 = objs[3].toString();
					
					if("Amazon.co.uk".equals(country1)&&!"GB".equals(countryCode)){
						price = price*rate;
						BigDecimal bd = new BigDecimal(price);
						bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
						price =bd.doubleValue() ;
					}
				
					float vat1 = vat;
					if("Amazon.co.uk,Amazon.de,Amazon.fr,Amazon.es,Amazon.it".contains(country1)){
						if(vat == 0f){
							String temp1 = country1.replace("Amazon.", "").replace("co.", "");
							if("uk".equals(temp1)){
								temp1 = "gb";
							}
							CountryCode code = CountryCode.valueOf(temp1.toUpperCase());
							vat1 = code.getVat();
						}	
					}
					double prePrice = price*100/(100+vat1);
					BigDecimal bd = new BigDecimal(prePrice);
					bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
					prePrice =bd.doubleValue() ;
					
					cell1=row1.createCell((short) 5);
					cell1.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell1.setCellValue(price);
					
					cell1=row1.createCell((short)6);
					cell1.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell1.setCellValue(prePrice);
					
					row1.createCell((short) 7).setCellValue(vat+"%");
					row1.createCell((short) 8).setCellValue(country1);
				}
			}
			// 自动调节列宽
			if (sheet2 != null) {
				sheet2.autoSizeColumn((short) 0);
				sheet2.autoSizeColumn((short) 1);
				sheet2.autoSizeColumn((short) 2);
				sheet2.autoSizeColumn((short) 3);
				sheet2.autoSizeColumn((short) 4);
				sheet2.autoSizeColumn((short) 5);
				sheet2.autoSizeColumn((short) 6);
				sheet2.autoSizeColumn((short) 7);
				sheet2.autoSizeColumn((short) 8);
				sheet2.autoSizeColumn((short) 9);
			}
			sheet1.autoSizeColumn((short) 0);
			sheet1.autoSizeColumn((short) 1);
			sheet1.autoSizeColumn((short) 2);
			sheet1.autoSizeColumn((short) 3);
			sheet1.autoSizeColumn((short) 4);
			sheet1.autoSizeColumn((short) 5);
			sheet1.autoSizeColumn((short) 6);
			sheet1.autoSizeColumn((short) 7);
			sheet1.autoSizeColumn((short) 8);
			sheet1.autoSizeColumn((short) 9);
		}
	  	sheet.autoSizeColumn((short) 0);
		sheet.autoSizeColumn((short) 1);
		sheet.autoSizeColumn((short) 2);

		String filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/salesSummary";
		String month1 = month.replace("-", "");
		File baseDir = new File(filePath + "/" + month1);
		AmazonSalesSummaryFile salesFile = getByPlatformAndMonth(country, month1, "1");
		if (salesFile == null) {
			salesFile = new AmazonSalesSummaryFile();
		}
		String fileName = country+"SalesSummary-" + month+ ".xls";
		if(!baseDir.isDirectory())
			baseDir.mkdirs();
		File dest = new File(baseDir, fileName);
		salesFile.setFilePath("/salesSummary/" + month1 + "/" +fileName);
		salesFile.setMonth(month1);
		salesFile.setPlatform(country);
		salesFile.setType("1"); //xls
		try {
			OutputStream out = new FileOutputStream(dest);
			wb.write(out);
			out.close();
			try {
				String zipFileName = country+"SalesSummary-" + month+ ".zip";
				File zipFile = new File(baseDir, zipFileName);
				ZipUtil.zip(zipFile.getAbsolutePath(),"",baseDir+"/"+fileName);
				salesFile.setFilePath("/salesSummary/" + month1 + "/" +zipFileName);
			} catch (Exception e) {
				logger.error("压缩" + country + "订单报表异常", e);
			}
			save(salesFile);
		} catch (Exception e) {
			logger.error("自动生成" + country + "订单报表异常", e);
		}
	}
	
	@Transactional(readOnly = false)
	public void generateCsvReports(String month, String country) throws Exception, ParseException {
	    Map<String, Float> sales = findSales(month,country);
	    if (sales == null || sales.size() == 0) {
			return;
		}
	    String filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/salesSummary";
		String month1 = month.replace("-", "");
		File baseDir = new File(filePath + "/" + month1);
		AmazonSalesSummaryFile salesFile = getByPlatformAndMonth(country, month1, "2");
		if (salesFile == null) {
			salesFile = new AmazonSalesSummaryFile();
		}
		String fileName = country+"OrderTotal-" + month+ ".csv";
		if(!baseDir.isDirectory())
			baseDir.mkdirs();
		File dest = new File(baseDir, fileName);
		salesFile.setFilePath("/salesSummary/" + month1 + "/" +fileName);
		salesFile.setMonth(month1);
		salesFile.setPlatform(country);
		salesFile.setType("2"); //CSV
	   
		File totalFile = new File(baseDir, fileName);
		FileOutputStream totalFos =new FileOutputStream(totalFile);
		OutputStreamWriter totalOsw = new OutputStreamWriter(totalFos, "utf-8");
	  	int i = 0 ;
	  	//按月查询前一个月退款明细map(结算报告延迟,往前推一个月)value:时间_退款数
		Map<String, String> refundMap = getOrderReturnMoney(month, country);
	  	//退款订单信息(账单号，收货地址，邮编等)
		Map<String, Object[]> refundOrderMap = getrefundOrderDetail(refundMap.keySet());
		//订单号与国家编号关系map
		Map<String, String> idAndCode = findCountryCode(refundMap.keySet());
		//分国家统计订单退款
		Map<String, List<Object[]>> returnGoodsCountryMap = Maps.newLinkedHashMap();
		for (Map.Entry<String, String> entry : refundMap.entrySet()) {
		    String key =entry.getKey();
			String countryCode = idAndCode.get(key);
			//amazon_order_id,order_id,address,sales_channel
			Object[] obj = refundOrderMap.get(key);
			if (obj == null) {
				continue;
			}
			//amazon_order_id,order_id,address,sales_channel,posted_date,refund
			Object[] objs = new Object[obj.length+2];
			if (StringUtils.isNotEmpty(countryCode)) {
				List<Object[]> list = returnGoodsCountryMap.get(countryCode);
				if (list == null) {
					list = Lists.newArrayList();
					returnGoodsCountryMap.put(countryCode, list);
				}
				for (int j = 0; j < obj.length; j++) {
					objs[j] = obj[j];
				}
				objs[objs.length-2] = entry.getValue().split("_")[0];
				objs[objs.length-1] = entry.getValue().split("_")[1];
				list.add(objs);
			}
		}
		List<Object[]> list = findExp(month,country);
		Map<String,List<Object[]>> dataMap = Maps.newHashMap();
		Set<String> codes = sales.keySet();
		for (Object[] objects : list) {
			if(objects[2]!=null){
				String key = objects[2].toString();
				String[] tempStrs = key.split(",");
				key = tempStrs[tempStrs.length-2];
				if(!codes.contains(key)){
					key = tempStrs[tempStrs.length-1];
				}
				if(!codes.contains(key)){
					continue;
				}	
				List<Object[]> temp = dataMap.get(key);
				if(temp==null){
					temp = Lists.newArrayList();
					dataMap.put(key, temp);
				}
				temp.add(objects);
			}
		}
		Date monthDate = DateUtils.parseDate(month+"-1",new String[]{"yyyy-MM-dd"});
		Float gbpRate = amazonProduc2tService.findAvgMonthRate("GBP/EUR", monthDate);
		BigDecimal bdRate = new BigDecimal(gbpRate);
		bdRate = bdRate.setScale(2, BigDecimal.ROUND_HALF_UP);
		gbpRate = bdRate.floatValue();
		StringBuilder returnBuilder = new StringBuilder();
		for (Map.Entry<String,Float> entry : sales.entrySet()) { 
	  	    String countryCode =entry.getKey();
	  		String name = countryCode;
	  		float vat  = 0f;
	  		CountryCode code = null;
	  		if("eu".equalsIgnoreCase(country)){
				try {
					code = CountryCode.valueOf(countryCode);
					if(code!=null){
		  				vat = code.getVat();
		  				name = code.getName();
		  			}
				} catch (Exception e) {}
	  		}
			//分国家输出亚马逊订单
			List<Object[]> list1 = dataMap.get(countryCode);
			for (int j =0;j<list1.size();j++ ) {
				StringBuilder sb = new StringBuilder();
				Object[] objs = list1.get(j);
				sb.append(objs[0].toString());	//id
				sb.append("\t"+objs[1].toString());	//orderId
				sb.append("\t"+objs[3].toString());	//date

				String country1 = objs[7].toString();
				double afterTax = Double.parseDouble(objs[4]==null?"0":objs[4].toString());
				float rate = 1f;
				if("Amazon.co.uk".equals(country1)&&!"GB".equals(countryCode)){
					rate = gbpRate;
					afterTax = afterTax*rate;
					BigDecimal bd = new BigDecimal(afterTax);
					bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
					afterTax = bd.doubleValue() ;
				}
				sb.append("\t"+afterTax);
				if(code!=null){
					sb.append("\t"+code.getNumberCode()+"\t"+rate+"\t100000");
				}
				sb.append("\n");
				totalOsw.write(sb.toString());
			}
			
			//加入退货订单
			if(returnGoodsCountryMap.get(countryCode)!=null 
					&& returnGoodsCountryMap.get(countryCode).size()>0){
				//objs:amazon_order_id,order_id,address,sales_channel,posted_date,refund
				for (Object[] objs : returnGoodsCountryMap.get(countryCode)) {
					returnBuilder.append("g_"+objs[1].toString());	//order_id
					returnBuilder.append("\t"+objs[0].toString());	//objs:amazon_order_id
					returnBuilder.append("\t"+objs[4].toString());	//date
					
					double price = Double.parseDouble(objs[5]==null?"0":objs[5].toString());
					String country1 = objs[3].toString();
					Float rate = 1f;
					if("Amazon.co.uk".equals(country1)&&!"GB".equals(countryCode)){
						rate = gbpRate;
						price = price*rate;
						BigDecimal bd = new BigDecimal(price);
						bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
						price =bd.doubleValue() ;
					}
					returnBuilder.append("\t"+ price);
					if(code!=null){
						returnBuilder.append("\t"+code.getNumberCode()+"\t"+rate+"\t100000");
					}
					returnBuilder.append("\n");
				}
			}
		}
		totalOsw.write(returnBuilder.toString());
		totalOsw.flush();
		totalOsw.close();
		save(salesFile);
	}
	
	public Map<String, Object[]> getrefundOrderDetail(Set<String> orderIdSet) {
		Map<String, Object[]> rs = Maps.newHashMap();
		if (orderIdSet != null && orderIdSet.size() > 0) {
			String sql = "SELECT t.`amazon_order_id`,IFNULL(t.`invoice_no`,t.`order_id`),t.`address`,t.`sales_channel` FROM `amazoninfo_sales_summary` t WHERE t.`amazon_order_id` IN :p1  ORDER BY t.`invoice_no`";
			List<Object[]> list = amazonSalesSummaryFileDao.findBySql(sql, new Parameter(orderIdSet));
			for (Object[] obj : list) {
				rs.put(obj[0].toString(), obj);
			}
		}
		return rs;
	}

	/**
	 * 按月分国家统计销售额
	 * @param month
	 * @param country
	 * @return
	 * @throws ParseException
	 */
	public Map<String, Float> findSales(String month,String country) throws ParseException {
		List<String> countrys = null;
		if("eu".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("Amazon.co.uk","Amazon.de","Amazon.fr","Amazon.es","Amazon.it") ;
		}else if("ca".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("Amazon.ca") ;
		}else if("us".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("Amazon.com","Amazon.com1","Amazon.com2","Amazon.com3") ;
		}else if("jp".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("Amazon.co.jp") ;
		}else if("mx".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("Amazon.com.mx") ;
		}
		String sql = "SELECT t.`country_code`,SUM(IFNULL(t.`order_total`,0)) AS sales FROM `amazoninfo_sales_summary` t WHERE t.`shipped_date` like :p1 AND t.`sales_channel` IN :p2 AND t.`country_code` IS NOT NULL GROUP BY t.`country_code` ORDER BY sales DESC" ;
		List<Object[]> list = amazonSalesSummaryFileDao.findBySql(sql, new Parameter(month+"%", countrys));
		Map<String, Float> rs = Maps.newLinkedHashMap();
		for (Object[] objs : list) {
			rs.put(objs[0].toString(), ((BigDecimal)objs[1]).floatValue());
		}
		return rs;
	}
	
	
	/**
	 * 按月查询退货订单(一个订单存在多次退货的情况,用List<Object[]>)
	 * @param month
	 * @param country
	 * @return Map<String, List<Object[]>> key:亚马逊订单号
	 * @throws Exception
	 */
	public Map<String, List<Object[]>> getReturnOrder(String month,String country) throws Exception{
		Date monthDate = DateUtils.parseDate(month+"-1",new String[]{"yyyy-MM-dd"});
		Date start = DateUtils.getFirstDayOfMonth(monthDate);
		Date end = DateUtils.getLastDayOfMonth(monthDate);
		List<String> countrys = null;
		List<String> countrys1 = null;
		if("eu".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("uk","de","fr","es","it") ;
			countrys1 = Lists.newArrayList("Amazon.co.uk","Amazon.de","Amazon.fr","Amazon.es","Amazon.it") ;
		}else if("ca".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("ca") ;
			countrys1 = Lists.newArrayList("Amazon.ca") ;
		}else if("us".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("com","com1","com2","com3") ;
			countrys1 = Lists.newArrayList("Amazon.com","Amazon.com1","Amazon.com2","Amazon.com3") ;
		}else if("jp".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("jp") ;
			countrys1 = Lists.newArrayList("Amazon.co.jp") ;
		}
		//当月退货统计
		String sql = "SELECT CONCAT(a.`order_id`,',',a.`sku`)  FROM amazoninfo_return_goods a WHERE a.`return_date` >= :p1 AND a.`return_date`<:p2 AND a.`country` in :p3 ";
		List<Object> temp = amazonSalesSummaryFileDao.findBySql(sql, new Parameter(start, DateUtils.addDays(end, 1), countrys));
		Map<String, List<Object[]>> rs = Maps.newHashMap();
		if(temp.size()>0){
			sql ="SELECT CONCAT(c.`amazon_order_id`,',',b.`sellersku`) AS key1," +
						" c.`id`,c.`amazon_order_id`,b.`sellersku`,TRUNCATE(b.`item_price`/b.`quantity_shipped`,2) AS price," +
						" b.`quantity_shipped`,c.`sales_channel` FROM amazoninfo_orderitem b , amazoninfo_order c " +
						" WHERE b.`order_id` = c.`id`  AND c.`purchase_date` >= :p1 AND c.`purchase_date` < :p2 AND c.`order_status` IN ('Shipped','UnShipped') AND  c.`sales_channel` IN :p4 AND b.`quantity_shipped`>0 " +
						" HAVING key1 IN (:p3)";
			List<Object[]> list = amazonSalesSummaryFileDao.findBySql(sql, new Parameter(DateUtils.addMonths(start, -3),DateUtils.addDays(end, 1),temp,countrys1));	//开始时间前推三个月，退货时间跨度可能超一个月
			for (Object[] objects : list) {
				List<Object[]> list2 = rs.get(objects[2].toString());
				if (list2 == null) {
					list2 = Lists.newArrayList();
					rs.put(objects[2].toString(), list2);
				}
				list2.add(objects);
			}
		}
		return rs;
	}
	
	
	/**
	 * 按月查询订单退款明细(从结算报告统计,含退货和退款但是不能区分是退货还是退款)
	 * @param month
	 * @param country
	 * @return Map<String, refund> key:亚马逊订单号 value:时间_退款数
	 * @throws Exception
	 */
	public Map<String, String> getOrderReturnMoney(String month, String country) throws Exception{
		Date start = DateUtils.parseDate(month+"-1",new String[]{"yyyy-MM-dd"});
		Date end = DateUtils.addMonths(start, 1);
		List<String> countrys = null;
		if("eu".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("uk","de","fr","es","it") ;
		}else if("ca".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("ca") ;
		}else if("us".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("com","com1","com2","com3") ;
		}else if("jp".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("jp") ;
		}else if("mx".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("mx") ;
		}
		//当月退款统计
		Map<String, String> rs = Maps.newLinkedHashMap();
		String sql ="SELECT o.`amazon_order_id`,DATE_FORMAT(o.`posted_date`,'%Y-%m-%d'),SUM(IFNULL(t.`principal`,0) + IFNULL(t.`shipping`,0) + IFNULL(t.`cod`,0) + IFNULL(t.`gift_wrap`,0) + IFNULL(t.`goodwill`,0) "+
				" + IFNULL(t.`cross_border_fulfillment_fee`,0) + IFNULL(t.`fba_per_unit_fulfillment_fee`,0) + IFNULL(t.`fba_weight_based_fee`,0) "+
				" + IFNULL(t.`commission`,0) + IFNULL(t.`shipping_chargeback`,0) + IFNULL(t.`giftwrap_chargeback`,0) + IFNULL(t.`refund_commission`,0) "+
				" + IFNULL(t.`restocking_fee`,0) + IFNULL(t.`promotion`,0) + IFNULL(t.`cod_fee`,0) + IFNULL(t.`other_fee`,0) + IFNULL(t.`shipping_hb`,0) "+
				" + IFNULL(t.`shipment_fee`,0) + IFNULL(t.`fba_per_order_fulfillment_fee`,0) + IFNULL(t.`tax`,0) + IFNULL(t.`sales_tax_service_fee`,0) + IFNULL(t.`shipping_tax`,0)) AS refund"+
				//" FROM `settlementreport_order` o, `settlementreport_item` t "+
				" FROM `amazoninfo_financial` o, `amazoninfo_financial_item` t "+
				" WHERE t.`order_id`=o.`id` AND o.`type` IN ('Refund','Chargeback Refund') AND o.`posted_date`>=:p1 AND o.`posted_date`< :p2 "+
				" AND o.`country` IN :p3 GROUP BY o.`amazon_order_id` HAVING refund<0 ORDER BY o.`posted_date`";
		List<Object[]> list = amazonSalesSummaryFileDao.findBySql(sql, new Parameter(start, end, countrys));
		for (Object[] objects : list) {
			String orderId = objects[0].toString();
			String value = objects[1].toString()+"_"+objects[2].toString();
			rs.put(orderId, value);
		}
		return rs;
	}
	
	public List<Object[]> findExp(String month,String country) {
		List<String> countrys = null;
		if("eu".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("Amazon.co.uk","Amazon.de","Amazon.fr","Amazon.es","Amazon.it") ;
		}else if("ca".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("Amazon.ca") ;
		}else if("us".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("Amazon.com","Amazon.com1","Amazon.com2","Amazon.com3") ;
		}else if("jp".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("Amazon.co.jp") ;
		}else if("mx".equalsIgnoreCase(country)){
			countrys = Lists.newArrayList("Amazon.com.mx") ;
		}
		String sql = "SELECT IFNULL(t.`invoice_no`,t.`order_id`),t.`amazon_order_id`,t.`address`,t.`shipped_date`,t.`order_total`,t.`country_code`,'yes',t.`sales_channel` FROM `amazoninfo_sales_summary` t"+
				" WHERE t.`shipped_date` like :p1 AND t.`sales_channel` IN :p2 AND t.`country_code` IS NOT NULL ORDER BY t.`invoice_no`";
		return amazonSalesSummaryFileDao.findBySql(sql, new Parameter(month + "%", countrys));
		
	}
	
	/**
	 * 根据亚马逊订单号匹配发货国家编码
	 * @param idList 订单号
	 * @return
	 */
	public Map<String, String> findCountryCode(Set<String> idList) {
		Map<String, String> rs = Maps.newHashMap();
		if (idList != null && idList.size() > 0) {
			String sql = "SELECT t.`amazon_order_id`,a.`country_code` FROM amazoninfo_order t, amazoninfo_address a WHERE t.`shipping_address`=a.`id` AND t.`amazon_order_id` IN (:p1)";
			List<Object[]> list = amazonSalesSummaryFileDao.findBySql(sql, new Parameter(idList));
			for (Object[] obj : list) {
				rs.put(obj[0].toString(), obj[1].toString());
			}
		}
		return rs;
	}
	
	public AmazonSalesSummaryFile getByPlatformAndMonth(String platform, String month, String type) {
		DetachedCriteria dc = amazonSalesSummaryFileDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(month)){
			dc.add(Restrictions.ge("month", month));
		}
		if(StringUtils.isNotEmpty(platform)){
			dc.add(Restrictions.eq("platform", platform));
		}
		if(StringUtils.isNotEmpty(type)){
			dc.add(Restrictions.eq("type", type));
		}
		List<AmazonSalesSummaryFile> list = amazonSalesSummaryFileDao.find(dc);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

   /**
    * 统计昨日发货订单
    * 注：初始化数据统一按照订单日期统计,初始化2016年之后的数据
    * 	INSERT IGNORE INTO amazoninfo_sales_summary (order_id,amazon_order_id,shipped_date,address,order_total,country_code,sales_channel) 
		SELECT t.`id`,t.`amazon_order_id`,DATE_FORMAT(t.`purchase_date`,'%Y-%m-%d'),CONCAT_WS(',',a.`name`,a.address_line1,a.address_line2,a.address_line3,a.county,a.city,a.country_code,a.postal_code),t.`order_total`,a.`country_code`,t.`sales_channel`
		FROM `amazoninfo_order` t, `amazoninfo_address` a WHERE t.`shipping_address`=a.`id` AND t.`purchase_date`>='2016-01-01 00:00:00'  AND t.`order_status`='Shipped' AND t.`order_channel` IS NULL ORDER BY t.`purchase_date`;
    * @return
    */
	@Transactional(readOnly = false)
	public void salesSummary(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		String dateString = format.format(DateUtils.addDays(date, -1));
		String shippedDate = "'" + dateString + "'";
		String p1 = format.format(DateUtils.addMonths(date, -1)) + " 00:00:00";	//统计开始时间,推到一个月前
	   	String p2 = dateString + " 23:59:59";	//统计截止时间
		logger.info("统计发货订单日期：" + shippedDate + "\tp1:" + p1 + "\tp2:" + p2);
	   	String sql="INSERT IGNORE INTO amazoninfo_sales_summary (order_id,amazon_order_id,shipped_date,address,order_total,country_code,sales_channel) "+
			  " SELECT t.`id`,t.`amazon_order_id`,"+shippedDate+",CONCAT_WS(',',a.`name`,a.address_line1,a.address_line2,a.address_line3,a.county,a.city,a.country_code,a.postal_code),t.`order_total`,a.`country_code`,t.`sales_channel` "+
			  " FROM `amazoninfo_order` t, `amazoninfo_address` a WHERE t.`shipping_address`=a.`id` AND t.`purchase_date`>=:p1 AND t.`purchase_date`<=:p2 AND t.`order_status`='Shipped' AND t.`order_channel` IS NULL";
	   	int count = amazonSalesSummaryFileDao.updateBySql(sql, new Parameter(p1, p2));
	   	try {
		   	//初始化账单号
		   	sql = "UPDATE `amazoninfo_sales_summary` t,`amazoninfo_order_extract` e "+
		   			" SET t.`invoice_no`= e.`invoice_no` WHERE t.`amazon_order_id`=e.`amazon_order_id` "+
		   			" AND t.`shipped_date`>:p1 AND t.`invoice_no` IS NULL AND e.`invoice_no` IS NOT NULL";
		   	amazonSalesSummaryFileDao.updateBySql(sql, new Parameter(format.format(DateUtils.addMonths(date, -1))));
	   	} catch (Exception e) {
			logger.error("匹配账单号异常", e);
		}
	   	logger.info("统计发货订单行:" + count + "\tsql：" + sql);
	}
   
}
