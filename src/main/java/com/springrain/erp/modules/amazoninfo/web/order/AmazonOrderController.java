package com.springrain.erp.modules.amazoninfo.web.order;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.CountryCode;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManagerInfo;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.MessageUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.ZipUtil;
import com.springrain.erp.common.utils.excel.ExportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.entity.ProductPrice;
import com.springrain.erp.modules.amazoninfo.entity.ReturnGoods;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonAddress;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderExtract;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderItem;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonRefund;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazoninfoRefundItem;
import com.springrain.erp.modules.amazoninfo.scheduler.SendEmailByOrderMonitor;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.ReturnGoodsService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOutboundOrderService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonRefundService;
import com.springrain.erp.modules.custom.entity.CustomEmail;
import com.springrain.erp.modules.custom.scheduler.CustomEmailManager;
import com.springrain.erp.modules.custom.service.UnsubscribeEmailService;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 亚马逊产品Controller
 * 
 * @author tim
 * @version 2014-06-26
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/order")
public class AmazonOrderController extends BaseController {

	@Autowired
	private AmazonOrderService amazonOrderService;

	@Autowired
	private AmazonProductService amazonProductService;
	
	
	@Autowired
	private AmazonProduct2Service amazonProduc2tService;

	@Autowired
	private CustomEmailManager sendCustomEmailManager;
	@Autowired
	private AmazonRefundService amazonRefundService;
	
	@Autowired
	private ReturnGoodsService returnGoodsService;
	
	@Autowired
	private PsiProductAttributeService psiProductAttributeService;

	
	@Autowired
	private PsiProductService psiProductService;
	
	@Autowired
	private PsiProductTypeGroupDictService 	   groupDictService;
	
	@Autowired
	private AmazonOutboundOrderService amazonOutboundOrderService;
	
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	@RequiresPermissions("amazoninfo:order:view")
	@RequestMapping(value = { "list", "" })
	public String list(AmazonOrder amazonOrder, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		Page<AmazonOrder> page = new Page<AmazonOrder>(request, response);
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (amazonOrder.getLastUpdateDate() == null) {
			amazonOrder.setLastUpdateDate(today);
		}
		if (amazonOrder.getPurchaseDate() == null) {
			amazonOrder.setPurchaseDate(DateUtils.addMonths(today, -1));
			amazonOrder.setLastUpdateDate(today);
		}
		String orderBy = page.getOrderBy();
		if ("".equals(orderBy)) {
			page.setOrderBy("id desc");
		} else {
			page.setOrderBy(orderBy + ",id desc");
		}
		page = amazonOrderService.find(page, amazonOrder);
		page.setOrderBy(orderBy);
		model.addAttribute("page", page);
		model.addAttribute("nameList",psiProductService.findAll(null));
		return "modules/amazoninfo/order/amazonOrderList";
	}
	
	@RequiresPermissions("amazoninfo:order:view")
	@RequestMapping(value =  "exp" )
	public String exp(AmazonOrder amazonOrder, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
		SimpleDateFormat   sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (amazonOrder.getPurchaseDate() == null) {
			amazonOrder.setPurchaseDate(DateUtils.addMonths(today, -1));
			amazonOrder.setLastUpdateDate(today);
		}
		

	    List<Object[]> list = amazonOrderService.findExp(amazonOrder);
	    int max = 65500;	//定义单sheet最大行,支持两个sheet
	    /*if(list.size()>65535){
	    	throw new RuntimeException("一次导出的最大数据不能超过65535条！请分两次导出！");
	    }*/
	    HSSFWorkbook wb = new HSSFWorkbook();
	    List<HSSFSheet> sheetList = Lists.newArrayList();
	    for (int i = 0; i < list.size()/max + 1; i++) {
	    	sheetList.add(wb.createSheet());
		}
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		String[] title = { "Invoice Id", "AmazonOrderId", "Receive Address","Zip Codes", "Purchase Date", "After-Tax",
				"Per-Tax", "Tax Rate"," On_line Order","Sales Channel"};
		
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
		  HSSFRow row = null;
		  HSSFCell cell = null;	
		  for (int n = 0; n < sheetList.size(); n++) {
			  row = sheetList.get(n).createRow(0);
			  row.setHeight((short) 600);
			  for (int i = 0; i < title.length; i++) {
				  cell = row.createCell(i);
				  cell.setCellValue(title[i]);
				  cell.setCellStyle(style);
			  }
		 }
		HSSFCell cell1 = null;		
		// 输出亚马逊excel订单
		int rowIndex = 0;
		for (int i =0;i<list.size();i++ ) {
			if (i > 0 && i%max==0) {
				rowIndex = 0;
			}
			rowIndex ++;
			row = sheetList.get(i/max).createRow(rowIndex);
			Object[] objs = list.get(i);
			row.createCell((short) 0).setCellValue(Integer.parseInt(objs[0].toString()));
			row.createCell((short) 1).setCellValue(objs[1].toString());
			String adress = (objs[2]==null?" ":objs[2].toString());
			row.createCell((short) 2).setCellValue(adress);
			String zipCode = "";
			if(StringUtils.isNotEmpty(adress)){
				zipCode = adress.substring(adress.lastIndexOf(",")+1);
			}
			row.createCell((short) 3).setCellValue(zipCode);
			row.createCell((short) 4).setCellValue(sdf1.format((Date)objs[3]));
			String country = objs[7].toString();
			String countryCode ="";
            if(objs[5]!=null){
            	countryCode = objs[5].toString().toUpperCase();
            }else{
            	String temp = country.replace("Amazon.", "").replace("co.", "");
				if("uk".equals(temp)){
					temp = "gb";
				}
				countryCode=temp.toUpperCase();
            }
			
			
			float vat = 0;
			if("Amazon.co.uk,Amazon.de,Amazon.fr,Amazon.es,Amazon.it".contains(country)){
				CountryCode code  = null;
				try {
					code = CountryCode.valueOf(countryCode);
				} catch (Exception e) {
					String temp = country.replace("Amazon.", "").replace("co.", "");
					if("uk".equals(temp)){
						temp = "gb";
					}
					code = CountryCode.valueOf(temp.toUpperCase());
				}
				vat = code.getVat();
			}
			
			double afterTax = Double.parseDouble(objs[4]==null?"0":objs[4].toString());
			if("Amazon.co.uk".equals(country)&&!"GB".equals(countryCode)){
				afterTax = afterTax*AmazonProduct2Service.getRateConfig().get("GBP/EUR");
				BigDecimal bd = new BigDecimal(afterTax);
				bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
				afterTax =bd.doubleValue() ;
			}
			cell1=row.createCell((short) 5);
			cell1.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			cell1.setCellValue(afterTax);
			
			cell1=row.createCell((short)6);
			cell1.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			double prePrice = afterTax*100/(100+vat);
			BigDecimal bd = new BigDecimal(prePrice);
			bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
			cell1.setCellValue(bd.doubleValue());
			
			row.createCell((short) 7).setCellValue(vat+"%");
			
			row.createCell((short) 8).setCellValue(objs[6].toString());
			row.createCell((short) 9).setCellValue(country);
		}

		
		// 自动调节列宽
		for (int i = 0; i < title.length; i++) {
			for (HSSFSheet sheet : sheetList) {
				sheet.autoSizeColumn((short) i);
			}
		}
		
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");

		SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");

		String fileName = "ShippedOrderData" + sdf.format(new Date()) + ".xls";
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
	
	@RequiresPermissions("amazoninfo:order:view")
	@RequestMapping(value =  "expByCsv" )
	public String expByCsv(AmazonOrder amazonOrder, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
		SimpleDateFormat   sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (amazonOrder.getPurchaseDate() == null) {
			amazonOrder.setPurchaseDate(DateUtils.addMonths(today, -1));
			amazonOrder.setLastUpdateDate(today);
		}
	    List<Object[]> list = amazonOrderService.findExp(amazonOrder);
	    response.setCharacterEncoding("UTF-8");
		response.setContentType("application/download;charset=UTF-8");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "ShippedOrderData" + sdf.format(new Date()) + ".csv";
		response.setHeader("Content-disposition", "attachment;filename=\""
				+fileName);
		OutputStream o;
		try {
			o = response.getOutputStream();
			OutputStreamWriter os = new OutputStreamWriter(o, "utf-8");
			
			for (Object[] str : list) {
				StringBuilder buf=new StringBuilder();
				if(str[5]==null){
					continue;
				}
				String countryCode = str[5].toString().toUpperCase();
				String country = str[7].toString();
				float vat = 0;
				if("Amazon.co.uk,Amazon.de,Amazon.fr,Amazon.es,Amazon.it".contains(country)){
					CountryCode code  = null;
					try {
						code = CountryCode.valueOf(countryCode);
					} catch (Exception e) {
						String temp = country.replace("Amazon.", "").replace("co.", "");
						if("uk".equals(temp)){
							temp = "gb";
						}
						code = CountryCode.valueOf(temp.toUpperCase());
					}
					vat = code.getVat();
				}
				
				double afterTax = Double.parseDouble(str[4]==null?"0":str[4].toString());
				if("Amazon.co.uk".equals(country)&&!"GB".equals(countryCode)){
					afterTax = afterTax*AmazonProduct2Service.getRateConfig().get("GBP/EUR");
					BigDecimal bd = new BigDecimal(afterTax);
					bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
					afterTax =bd.doubleValue() ;
				}
				double prePrice = afterTax*100/(100+vat);
				BigDecimal bd = new BigDecimal(prePrice);
				bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
				for (int i = 0; i < str.length; i++) {
					if (i > 0){
						buf.append("\t");
					}	
					if(i==2){
						String adress = (str[2]==null?" ":str[2].toString());
						adress = adress.replace("\t"," ");
						String zipCode = "";
						if(StringUtils.isNotEmpty(adress)){
							zipCode = adress.substring(adress.lastIndexOf(",")+1);
						}
						buf.append("\""+adress+"\","+zipCode);
					}else if(i==3){
						buf.append(sdf1.format((Date)str[3]));
					}else if(i==4){
						buf.append(afterTax+"");
					}else if(i==5){
						buf.append(bd.doubleValue()+"");
					}else{
						buf.append(str[i]);
					}
				}
				buf.append("\n");
				os.write(buf.toString());
			}
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static Map<String,String> threadGetPdf = Maps.newHashMap();

    @RequestMapping(value = "exportBySyn")
    public String exportBySyn(String country,final String month,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes){
    	final String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/orderTotal/"+country;
    	final String country1 = country.substring(0,country.indexOf("_")).toLowerCase();
    	final String key =  month+"_"+country1;
    	final SimpleDateFormat   sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    	final File zipFile = new File (baseDirStr+"/Amazon_orderTotal_"+key+".zip");
    	final File baseDir = new File(baseDirStr+"/"+month);
		if(!baseDir.exists()){
			baseDir.mkdirs();
		}
		if(threadGetPdf.get(key)==null){
			if(!zipFile.exists()|| zipFile.lastModified()+12*3600000<new Date().getTime()){
				if(zipFile.exists()){
					zipFile.delete();
				}
	    		new Thread(){
	    			public void run() {
	    				threadGetPdf.put(key, "1");
	    				OutputStreamWriter osw = null;
	    				OutputStreamWriter totalOsw = null;
	    				try {
	    					Date monthDate = DateUtils.parseDate(month+"-1",new String[]{"yyyy-MM-dd"});
	    					Date start = DateUtils.getFirstDayOfMonth(monthDate);
	    					Date end = DateUtils.getLastDayOfMonth(monthDate);
	    					AmazonOrder order = new AmazonOrder();
	    					order.setPurchaseDate(start);
	    					order.setLastUpdateDate(end);
	    					List<String> codes = amazonOrderService.getShippedCountrys(order);
							List<Object[]> list = amazonOrderService.findExp(month,country1);
							Map<String,List<Object[]>> dataMap = Maps.newHashMap();
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
							File totalFile = new File(baseDir,"total.csv");
							FileOutputStream totalFos =new FileOutputStream(totalFile);
							totalOsw = new OutputStreamWriter(totalFos, "utf-8");
							Float gbpRate =amazonProduc2tService.findAvgMonthRate("GBP/EUR", monthDate);
							BigDecimal bd = new BigDecimal(gbpRate);
							bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
							gbpRate = bd.floatValue();
							for (Map.Entry<String, List<Object[]>> entry: dataMap.entrySet()) { 
							    String name = entry.getKey();
								File cvsFile = new File(baseDir,name+".csv");
								FileOutputStream fos =new FileOutputStream(cvsFile);
								osw = new OutputStreamWriter(fos, "utf-8");
								for (Object[] str : entry.getValue()) {
									StringBuilder buf=new StringBuilder();
									String countryCode = str[5].toString().toUpperCase();
									if(str[5]==null){
										continue;
									}
									double afterTax = Double.parseDouble(str[4]==null?"0":str[4].toString());
									if(afterTax<=0){
										continue;
									}
									String country = str[7].toString();
									float vat = 0;
									CountryCode code  = null;
									if("Amazon.co.uk,Amazon.de,Amazon.fr,Amazon.es,Amazon.it".contains(country)){
										try {
											code = CountryCode.valueOf(countryCode);
										} catch (Exception e) {
											String temp = country.replace("Amazon.", "").replace("co.", "");
											if("uk".equals(temp)){
												temp = "gb";
											}
											code = CountryCode.valueOf(temp.toUpperCase());
										}
										vat = code.getVat();
									}
									float rate = 1f;
									//&&!"GB".equals(countryCode)
									if("Amazon.co.uk".equals(country)){
										rate = gbpRate;
										afterTax = afterTax*rate;
										bd = new BigDecimal(afterTax);
										bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
										afterTax =bd.doubleValue() ;
									}
									double prePrice = afterTax*100/(100+vat);
									bd = new BigDecimal(prePrice);
									bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
									for (int i = 0; i < str.length; i++) {
										if(i==2){
											/*String adress = (str[2]==null?" ":str[2].toString());
											String zipCode = "";
											if(StringUtils.isNotEmpty(adress)){
												zipCode = adress.substring(adress.lastIndexOf(",")+1);
											}
											str2 += (adress+"\t"+zipCode);*/
											continue;
										}else if(i==3){
											buf.append("\t");
											buf.append(sdf1.format((Date)str[3]));
										}else if(i==4){
											buf.append("\t");
											buf.append(afterTax+"");
										}else if(i==5 || i==6 || i==7){
											//str2 += (bd.doubleValue()+"");
											continue;
										}else if (i==0){
											buf.append(str[i]);
										}else{
											buf.append("\t");
											buf.append(str[i]);
										}
									}
									if(code!=null){
										buf.append("\t"+code.getNumberCode()+"\t"+rate+"\t100000");
									}
									buf.append("\n");
									osw.write(buf.toString());
									totalOsw.write(buf.toString());
								}
								osw.flush();
								osw.close();
							}
							//加入退款
							List<Object[]> refunds = amazonOrderService.getRefundOrders(month, country1);
							for (Object[] objects : refunds) {
								String country = objects[4].toString();
								String str2 = "";
								double afterTax = Double.parseDouble(objects[3]==null?"0":objects[3].toString());
								
								CountryCode code  = null;
								if("uk,de,fr,es,it".contains(country)){
									String temp = country;
									if("uk".equals(country)){
										temp = "gb";
									}
									try {
										code = CountryCode.valueOf(temp.toUpperCase());
									} catch (Exception e) {
									}
								}
								
								float rate = 1f;
								if("uk".equals(country)){
									rate = gbpRate;
									afterTax = afterTax*rate;
									bd = new BigDecimal(afterTax);
									bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
									afterTax =bd.doubleValue() ;
								}
								str2 += objects[0].toString()+"\t";
								
								str2 += objects[1].toString()+"\t";
								
								str2 += objects[2].toString()+"\t";
								
								str2 += afterTax;
								
								if(code!=null){
									str2 += ("\t"+code.getNumberCode()+"\t"+rate+"\t100000");
								}
								str2 += "\n";
								totalOsw.write(str2);
							}
							
							
							
							totalOsw.flush();
							totalOsw.close();
						} catch (Exception e) {
							logger.error("生成订单报表错误", e);
						}finally{
							if(osw!=null){
								try {
									osw.close();
								} catch (IOException e) {}
							}
							if(totalOsw!=null){
								try {
									totalOsw.close();
								} catch (IOException e) {}
							}
						}
    		    		try {
    						ZipUtil.zip(zipFile.getAbsolutePath(),"",baseDirStr+"/"+month);
    						FileUtils.deleteDirectory(baseDir);
    					} catch (Exception e) {
    						e.printStackTrace();
    					}
	    		    	threadGetPdf.remove(key);
	    			};
	    		}.start();
	    		addMessage(redirectAttributes, "Is background rendering, please make the request again after 5 minutes ...");
	    		return "redirect:"+Global.getAdminPath()+"/amazoninfo/order";
			}else{
				try {
    				response.addHeader("Content-Disposition", "attachment;filename="
    						+zipFile.getName());
    				OutputStream out = response.getOutputStream();
    				out.write(FileUtils.readFileToByteArray(zipFile));
    				out.flush();
    				out.close();
        		} catch (Exception e) {
    				e.printStackTrace();
    			}
        		return null;
			}
		}
		addMessage(redirectAttributes, "Is background rendering,Just a moment please ...");
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/order";
    }
	
	
	@RequiresPermissions("amazoninfo:order:view")
	@RequestMapping(value =  "expByEuro" )
	public String expByEuro(String month,String country,HttpServletResponse response) throws Exception, ParseException {
		
	    Map<String, Float> sales = amazonOrderService.findSales(month,country);
	    
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
		Map<String,Object[]> returnGoodsMap = amazonOrderService.getReturnOrder(month,country);
		//再开一个tab页
		List<Object[]> list = amazonOrderService.findExp(month,country);
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
	  	    String countryCode= entry.getKey();
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
			// 输出亚马逊excel订单
			List<Object[]> returnGoods = Lists.newArrayList();
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
				row1.createCell((short) 0).setCellValue(Integer.parseInt(objs[0].toString()));
				String orderId = objs[1].toString();
				HSSFCell cell3 = row1.createCell((short) 1);
				cell3.setCellValue(orderId);
				if(returnGoodsMap.get(orderId)!=null){
					returnGoods.add(returnGoodsMap.get(orderId));
					cell3.setCellStyle(redCell);
				}
				
				String adress = (objs[2]==null?" ":objs[2].toString());
				row1.createCell((short) 2).setCellValue(adress);
				String zipCode = "";
				if(StringUtils.isNotEmpty(adress)){
					zipCode = adress.substring(adress.lastIndexOf(",")+1);
				}
				row1.createCell((short) 3).setCellValue(zipCode);
				row1.createCell((short) 4).setCellValue(DateUtils.getDate(((Date)objs[3]), "yyyy-MM-dd"));

				/*cell1=row1.createCell((short) 4);
				cell1.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell1.setCellValue(Double.parseDouble(objs[4]==null?"0":objs[4].toString()));
				
				cell1=row1.createCell((short)5);
				cell1.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
				cell1.setCellValue(Double.parseDouble(objs[5]==null?"0":objs[5].toString()));
				
				row1.createCell((short) 6).setCellValue(objs[6].toString());
				row1.createCell((short) 7).setCellValue(objs[7].toString());
				row1.createCell((short) 8).setCellValue(objs[8].toString());*/
				
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
			if(returnGoods.size()>0){
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
				String[] title1 = { "Invoice Id", "AmazonOrderId", "sku","Return Quantity","After-Tax",
						"Per-Tax", "Tax Rate","Sales Channel"};
				row1.setHeight((short) 600);
				for (int j = 0; j < title1.length; j++) {
					cell2 = row1.createCell(j);
					cell2.setCellValue(title1[j]);
					cell2.setCellStyle(style);
				}
				for (Object[] objs : returnGoods) {
					if (sheet2 != null) {
						row1 = sheet2.createRow(sheet2.getLastRowNum()+1);
					} else {
						row1 = sheet1.createRow(sheet1.getLastRowNum()+1);
					}
					row1.createCell((short) 0).setCellValue(Integer.parseInt(objs[1].toString()));
					String orderId = objs[2].toString();
					row1.createCell((short) 1).setCellValue(orderId);
					String sku = objs[3].toString();
					row1.createCell((short) 2).setCellValue(sku);
					
					int quantity = Integer.parseInt(objs[5].toString());
					double price = Double.parseDouble(objs[4]==null?"0":objs[4].toString());
					String country1 = objs[6].toString();
					
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
					if(quantity>1){
						quantity = amazonOrderService.getReturnGoodsQuantity(orderId,sku);
						price = quantity*price;
						prePrice = quantity*prePrice;
					}
					
					row1.createCell((short) 3).setCellValue(quantity);
					cell1=row1.createCell((short) 4);
					cell1.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell1.setCellValue(price);
					
					cell1=row1.createCell((short)5);
					cell1.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
					cell1.setCellValue(prePrice);
					
					row1.createCell((short) 6).setCellValue(vat+"%");
					row1.createCell((short) 7).setCellValue(country1);
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
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");

		String fileName = country+"SalesSummary-" + month+ ".xls";
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

	@RequiresPermissions("amazoninfo:order:view")
	@RequestMapping(value = "form")
	public String form(AmazonOrder amazonOrder, Model model) {
		String orderId = amazonOrder.getAmazonOrderId(); 
		if(amazonOrder.getId()!=null){
			amazonOrder = this.amazonOrderService.get(amazonOrder.getId());
		}else if(orderId!=null){
			amazonOrder = this.amazonOrderService.findByEg(orderId);
		}
		if(amazonOrder==null)
			throw new RuntimeException("'"+orderId+"'>>>this is not Amazon Order!");
		model.addAttribute("amazonOrder", amazonOrder);
		
		AmazonRefund amazonRefund=new AmazonRefund();
		amazonRefund.setCountry(StringUtils.substringAfterLast(amazonOrder.getSalesChannel(), "."));
		amazonRefund.setAmazonOrderId(amazonOrder.getAmazonOrderId());
	//	List<AmazonRefund> records=amazonRefundService.getRefundRecord(amazonRefund.getAmazonOrderId());
		List<AmazoninfoRefundItem>  records=amazonRefundService.findRefund(amazonRefund);
		model.addAttribute("records", records);
		
		Map<String,ReturnGoods> returnGoods=returnGoodsService.getReturnGoodsByOrderId(amazonOrder.getAmazonOrderId());
		model.addAttribute("returnGoods", returnGoods);
		
		AmazonOrderExtract  orderExtract=amazonOrderService.findRateSnByOrderId(amazonOrder.getAmazonOrderId());
		model.addAttribute("orderExtract", orderExtract);
		
		if(UserUtils.hasPermission("amazoninfo:profits:view")){	//可查看利润
			Float reviewFee=amazonOutboundOrderService.findReviewRefund(amazonOrder.getAmazonOrderId());
			model.addAttribute("reviewFee",reviewFee);
			
			List<Object[]> settlements=amazonOrderService.getSettlementByOrderId(amazonOrder.getAmazonOrderId(),amazonRefund.getCountry());
			Map<String,Float> gwMap=psiProductService.findTranGw();
			model.addAttribute("gwMap", gwMap);
			Float tranAvgFee=ProductPrice.tranFee.get(amazonRefund.getCountry())/AmazonProduct2Service.getRateConfig().get("USD/CNY");
			if("de,fr,it,es".contains(amazonRefund.getCountry())){
				tranAvgFee=tranAvgFee/AmazonProduct2Service.getRateConfig().get("EUR/USD");
			}else if("uk".contains(amazonRefund.getCountry())){
				tranAvgFee=tranAvgFee/AmazonProduct2Service.getRateConfig().get("GBP/USD");
			}else if("ca".contains(amazonRefund.getCountry())){
				tranAvgFee=tranAvgFee/AmazonProduct2Service.getRateConfig().get("CAD/USD");
			}else if("jp".contains(amazonRefund.getCountry())){
				tranAvgFee=tranAvgFee/AmazonProduct2Service.getRateConfig().get("JPY/USD");
			}else if("mx".contains(amazonRefund.getCountry())){
				tranAvgFee=tranAvgFee/AmazonProduct2Service.getRateConfig().get("MXN/USD");
			}
			model.addAttribute("tranAvgFee",tranAvgFee);
			if(settlements!=null&&settlements.size()>0){
				Set<String> nameSet=Sets.newHashSet();
				for (Object[] obj: settlements) {
					String name=(String) obj[6];
					if(StringUtils.isNotBlank(name)){
						nameSet.add(name);
					}
				}
				if(nameSet!=null&&nameSet.size()>0){
					//sales_no_tax-amazonFee-otherFee-(退税价+运费+关税)*quanity
					Map<String,Float> purchaseTaxPrice=psiProductAttributeService.findTaxPrice(amazonRefund.getCountry(),nameSet);//产品含税价
					Map<String,Float> dutyMap=psiProductService.findCustomDuty();//关税(不含税算)和退税 name+"_eu"
					Map<String,Float> supplierTaxMap=psiProductService.findSupplier(nameSet);//供应商税点
					Map<String,Float> transFeeMap=psiProductService.findTransportAvgPrice(nameSet,amazonRefund.getCountry());//运费
					model.addAttribute("supplierTaxMap", supplierTaxMap);
					model.addAttribute("purchaseTaxPrice", purchaseTaxPrice);
					model.addAttribute("settlements", settlements);
					model.addAttribute("dutyMap", dutyMap);
					model.addAttribute("transFeeMap", transFeeMap);
				}
				
			}else{//估算
				/*fr、de、it ：0.5
				it 最低处理费 2.5
				de 最低处理费：2.5
				fr最低处理费：2.5
				es 最低处理费：2.5
				com :1  最低处理费 2.5
				uk:0.4 最低处理费 2
				ca: 1 最低处理费 3.5
				jp: 50 最低处理费 400*/
				String temp = amazonRefund.getCountry().toUpperCase();
				if("UK".equals(temp)){
					temp = "GB";
				}
				if(temp.startsWith("COM")){
					temp = "US";
				}
				CountryCode vatCode = CountryCode.valueOf(temp);
				if(vatCode!=null){
					model.addAttribute("vat",vatCode.getVat()/100f);
				}
				Set<String> skuSet=Sets.newHashSet();
				Set<String> nameSet=Sets.newHashSet();
				for (AmazonOrderItem item: amazonOrder.getItems()) {
					skuSet.add(item.getSellersku());
					if(StringUtils.isNotBlank(item.getName())){
						nameSet.add(item.getName());
					}
				}
				if(nameSet!=null&&nameSet.size()>0){
					Map<String,Object[]> skuMap=amazonOrderService.findDealFee(amazonRefund.getCountry(),skuSet);
					Map<String,Float> purchaseTaxPrice=psiProductAttributeService.findTaxPrice(amazonRefund.getCountry(),nameSet);//产品含税价
					Map<String,Float> dutyMap=psiProductService.findCustomDuty();//关税(不含税算)和退税 name+"_eu"
					Map<String,Float> supplierTaxMap=psiProductService.findSupplier(nameSet);//供应商税点
					Map<String,Float> transFeeMap=psiProductService.findTransportAvgPrice(nameSet,amazonRefund.getCountry());//运费
					model.addAttribute("supplierTaxMap", supplierTaxMap);
					model.addAttribute("purchaseTaxPrice", purchaseTaxPrice);
					model.addAttribute("skuMap", skuMap);
					model.addAttribute("dutyMap", dutyMap);
					model.addAttribute("transFeeMap", transFeeMap);
				}
				
			}
			
		}
		return "modules/amazoninfo/order/amazonOrderForm";
	}
	
	
	@RequiresPermissions("amazoninfo:order:view")
	@RequestMapping(value = "edit")
	public String edit(AmazonOrder amazonOrder, Model model) {
		amazonOrder = this.amazonOrderService.get(amazonOrder.getId());
		model.addAttribute("amazonOrder", amazonOrder);
		String key = amazonOrder.getSalesChannel();
		key = key.substring(key.lastIndexOf(".") + 1).toLowerCase();
		amazonOrder.setSalesChannel(key);
		StringBuilder sb = new StringBuilder("");
		List<AmazonOrderItem> items = amazonOrder.getItems();
		if (items != null && items.size() > 0) {
			for (AmazonOrderItem amazonOrderItem : items) {
				sb.append(amazonOrderItem.getId()+",");
				String name = amazonProductService.findProductName(amazonOrderItem.getAsin(), key);
				amazonOrderItem.setTitle(name);
			}
		}
		if(StringUtils.isNotEmpty(amazonOrder.getSalesChannel())){
			model.addAttribute("sku",amazonProductService.findSku(amazonOrder.getSalesChannel()));
		}
		return "modules/amazoninfo/order/amazonOrderEdit";
	}
	
	@RequiresPermissions("amazoninfo:order:view")
	@RequestMapping(value = "editSelect")
	public String editSelect(AmazonOrder amazonOrder,Model model) throws UnsupportedEncodingException {
		if(StringUtils.isNotEmpty(amazonOrder.getSalesChannel())){
			model.addAttribute("sku",amazonProductService.findSku(amazonOrder.getSalesChannel()));
			amazonOrder.setBuyerName(URLDecoder.decode(amazonOrder.getBuyerName(),"utf-8"));
			AmazonAddress addr=amazonOrder.getShippingAddress();
			
			if(StringUtils.isNotEmpty(addr.getAddressLine1())){
				addr.setAddressLine1(URLDecoder.decode(addr.getAddressLine1(),"utf-8"));
			}
			if(StringUtils.isNotEmpty(addr.getAddressLine2())){
				addr.setAddressLine2(URLDecoder.decode(addr.getAddressLine2(),"utf-8"));
			}
			if(StringUtils.isNotEmpty(addr.getAddressLine3())){
				addr.setAddressLine3(URLDecoder.decode(addr.getAddressLine3(),"utf-8"));
			}
			
			if(StringUtils.isNotEmpty(addr.getCity())){
				addr.setCity(URLDecoder.decode(addr.getCity(),"utf-8"));
			}
			if(StringUtils.isNotEmpty(addr.getCounty())){
				addr.setCounty(URLDecoder.decode(addr.getCounty(),"utf-8"));
			}
			if(StringUtils.isNotEmpty(addr.getStateOrRegion())){
				addr.setStateOrRegion(URLDecoder.decode(addr.getStateOrRegion(),"utf-8"));
			}
			if(StringUtils.isNotEmpty(addr.getName())){
				addr.setName(URLDecoder.decode(addr.getName(),"utf-8"));
			}
			if(StringUtils.isNotEmpty(addr.getPhone())){
				addr.setPhone(URLDecoder.decode(addr.getPhone(),"utf-8"));
			}
			
			amazonOrder.setShippingAddress(addr);
		}else{
			amazonOrder.setSalesChannel("");
		}
		model.addAttribute("amazonOrder", amazonOrder);
		return "modules/amazoninfo/order/amazonOrderEdit";
	}
	
	
	@RequiresPermissions("amazoninfo:order:view")
	@RequestMapping(value = "add")
	public String add(AmazonOrder amazonOrder,Model model) throws UnsupportedEncodingException {
		if(StringUtils.isNotEmpty(amazonOrder.getSalesChannel())){
			model.addAttribute("sku",amazonProductService.findSku(amazonOrder.getSalesChannel()));
			amazonOrder.setBuyerName(URLDecoder.decode(amazonOrder.getBuyerName(),"utf-8"));
			AmazonAddress addr=amazonOrder.getShippingAddress();
			
			if(StringUtils.isNotEmpty(addr.getAddressLine1())){
				addr.setAddressLine1(URLDecoder.decode(addr.getAddressLine1(),"utf-8"));
			}
			if(StringUtils.isNotEmpty(addr.getAddressLine2())){
				addr.setAddressLine2(URLDecoder.decode(addr.getAddressLine2(),"utf-8"));
			}
			if(StringUtils.isNotEmpty(addr.getAddressLine3())){
				addr.setAddressLine3(URLDecoder.decode(addr.getAddressLine3(),"utf-8"));
			}
			
			if(StringUtils.isNotEmpty(addr.getCity())){
				addr.setCity(URLDecoder.decode(addr.getCity(),"utf-8"));
			}
			if(StringUtils.isNotEmpty(addr.getCounty())){
				addr.setCounty(URLDecoder.decode(addr.getCounty(),"utf-8"));
			}
			if(StringUtils.isNotEmpty(addr.getStateOrRegion())){
				addr.setStateOrRegion(URLDecoder.decode(addr.getStateOrRegion(),"utf-8"));
			}
			if(StringUtils.isNotEmpty(addr.getName())){
				addr.setName(URLDecoder.decode(addr.getName(),"utf-8"));
			}
			if(StringUtils.isNotEmpty(addr.getPhone())){
				addr.setPhone(URLDecoder.decode(addr.getPhone(),"utf-8"));
			}
			
			amazonOrder.setShippingAddress(addr);
		}else{
			amazonOrder.setSalesChannel("");
		}
		model.addAttribute("amazonOrder", amazonOrder);
		return "modules/amazoninfo/order/amazonOrderAdd";
	}
	

	@RequiresPermissions("amazoninfo:order:view")
	@RequestMapping(value = "save")
	@ResponseBody
	public String save(AmazonOrder amazonOrder) {
		AmazonOrder temp = this.amazonOrderService.get(amazonOrder.getId());
		AmazonAddress address=amazonOrder.getInvoiceAddress();
		try{
			address.setAddressLine1(HtmlUtils.htmlUnescape(address.getAddressLine1()));
			address.setAddressLine2(HtmlUtils.htmlUnescape(address.getAddressLine2()));
			address.setAddressLine3(HtmlUtils.htmlUnescape(address.getAddressLine3()));
			address.setCity(HtmlUtils.htmlUnescape(address.getCity()));
			address.setCountryCode(HtmlUtils.htmlUnescape(address.getCountryCode()));
			address.setCounty(HtmlUtils.htmlUnescape(address.getCounty()));
			address.setDistrict(HtmlUtils.htmlUnescape(address.getDistrict()));
			address.setName(HtmlUtils.htmlUnescape(address.getName()));
			address.setStateOrRegion(HtmlUtils.htmlUnescape(address.getStateOrRegion()));
		}catch(Exception e){}
		temp.setInvoiceAddress(address);
		amazonOrderService.save(temp);
		amazonOrderService.updateRateSn(amazonOrder.getRateSn(),temp.getAmazonOrderId());
		return temp.getInvoiceAddress().getId() + "";
	};

	
	/*@RequiresPermissions("amazoninfo:order:view")
	@RequestMapping(value = "saveAdd")
	public String saveAdd(AmazonOrder amazonOrder,RedirectAttributes redirectAttributes) {
		if(amazonOrder.getId()!=null&&amazonOrder.getId()>0){
			amazonOrder.setInvoiceAddress(amazonOrderService.get(amazonOrder.getId()).getInvoiceAddress());
		}
		Float orderTotal = new Float(0);
		int    shipedTotal = 0;
		int    upshipedTotal =0;
		StringBuilder sb = new StringBuilder("");
		for(AmazonOrderItem item:amazonOrder.getItems()){
			//根据国家和sku     查询出asin    title
			Object[]  product =this.amazonProductService.findNameAndAsin(item.getSellersku(), amazonOrder.getSalesChannel());
			if(product!=null){
				item.setAsin(product[0]);
				item.setTitle(product[1]);
			}
			if(item.getItemPrice()==null){
				item.setItemPrice(0f);
			}
			if(item.getItemTax()==null){
				item.setItemTax(0f);
			}
			if(item.getShippingPrice()==null){
				item.setShippingPrice(0f);
			}
			if(item.getGiftWrapPrice()==null){
				item.setGiftWrapPrice(0f);
			}
			
			if(item.getQuantityOrdered()==null){
				item.setQuantityOrdered(0);
			}
			
			if(item.getOrder()==null){
				item.setOrder(amazonOrder);
			}
			upshipedTotal+=item.getQuantityOrdered()-item.getQuantityShipped();
			shipedTotal+=item.getQuantityShipped();
			//单价   单项总价变换
			Float itemTotalPrice=item.getItemPrice()*item.getQuantityOrdered();
			orderTotal +=itemTotalPrice+item.getItemTax()+item.getShippingPrice()+item.getGiftWrapPrice();
			item.setItemPrice(itemTotalPrice);
			if(item.getId()!=null){
				sb.append(item.getId()+",");
			}
			
		}
		String channel = amazonOrder.getSalesChannel();
		if(!channel.isEmpty()){
			amazonOrder.setSalesChannel(this.getSaleChannel(channel));
		}
		if(StringUtils.isEmpty(amazonOrder.getAmazonOrderId())){
			String orderId=Math.round(Math.random()*9000+1000)+""+ new Date().getTime();
			String newOrderId=orderId.substring(0, 3)+"-"+orderId.substring(3, 17);
			amazonOrder.setSellerOrderId(newOrderId);
			amazonOrder.setAmazonOrderId(newOrderId);
		}
		
		if(amazonOrder.getPurchaseDate()==null){
			amazonOrder.setPurchaseDate(new Date());
		}
		
		DecimalFormat df =new DecimalFormat("#.00");
		amazonOrder.setOrderTotal(Float.parseFloat(df.format(orderTotal)));
		
		amazonOrder.setFulfillmentChannel("MFN");
		amazonOrder.setLastUpdateDate(new Date());
		amazonOrder.setShipServiceLevel("Standard");
		amazonOrder.setShipmentServiceLevelCategory("Standard");
		amazonOrder.setOrderType("Standard");
		amazonOrder.setNumberOfItemsShipped(shipedTotal);//已发货总数
		amazonOrder.setNumberOfItemsUnshipped(upshipedTotal);//未发货数量
		
		//order channel 保存创建人的name
		amazonOrder.setOrderChannel(UserUtils.getUser().getName());
		//订单状态                 如果未发货数量为0，状态改为Shipped
		if(upshipedTotal==0){
			amazonOrder.setOrderStatus("Shipped");
		}
		//付款方式
		amazonOrder.setPaymentMethod("Other");
		
		amazonOrderService.save(amazonOrder);
		addMessage(redirectAttributes,MessageUtils.format("amazon_order_tips18",new Object[]{amazonOrder.getSellerOrderId()}));
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/order/?repage";
	}*/
	
	
	
	@RequiresPermissions("amazoninfo:order:view")
	@RequestMapping(value = "invoice")
	@ResponseBody
	public String invoice(String hasTax, AmazonOrder amazonOrder,String payment,String quantityStr,String itemStr) {
		File file = null;
		String itemIds=amazonOrder.getPaymentMethod();
		String quantitys=amazonOrder.getMarketplaceId();
		Date date=amazonOrder.getLastUpdateDate();
		Date deliveryDate=amazonOrder.getDeliveryDate();
		String remark=amazonOrder.getRemark();
		String orderId=amazonOrder.getAmazonOrderId();
		amazonOrder = this.amazonOrderService.get(amazonOrder.getId());
		String country = amazonOrder.getSalesChannel().substring(amazonOrder.getSalesChannel().lastIndexOf(".") + 1);
		if(StringUtils.isNotBlank(payment)){
			amazonOrder.setPaymentMethod(payment);
		}
		if(StringUtils.isNotBlank(remark)){
			amazonOrder.setRemark(remark);
		}
		try{
			AmazonOrderExtract  orderExtract=amazonOrderService.findRateSnByOrderId(amazonOrder.getAmazonOrderId());
			if(orderExtract!=null&&StringUtils.isNotBlank(orderExtract.getRateSn())){
				amazonOrder.setRateSn(orderExtract.getRateSn());
			}
			AmazonAccountConfig config = amazonAccountConfigService.getByName(orderExtract.getAccountName());
			String invoiceType = config.getInvoiceType();
			String flag= invoiceType.substring(0, invoiceType.lastIndexOf("_"));
			String suffix= invoiceType.substring(invoiceType.lastIndexOf("_")+1);
			
			if(orderExtract==null||StringUtils.isBlank(orderExtract.getInvoiceNo())){
				if((orderExtract.getInvoiceFlag().startsWith("0")&&!"fr".equals(country))||
					((orderExtract.getInvoiceFlag().startsWith("00")||orderExtract.getInvoiceFlag().startsWith("10"))&&"fr".equals(country))
				 ){//未发送账单
					String invoiceNo=amazonOrderService.createFlowNo(flag,8,suffix);
					amazonOrder.setInvoiceNo(invoiceNo);
					amazonOrder.setPrintDate(new Date());
					amazonOrderService.updateInvoiceNoById(amazonOrder.getInvoiceNo(),amazonOrder.getAmazonOrderId()) ;
				}else{
					amazonOrder.setInvoiceNo(orderExtract.getId()+"");
				}
			}else{
				amazonOrder.setInvoiceNo(orderExtract.getInvoiceNo());
				amazonOrder.setPrintDate(orderExtract.getPrintDate());
			}
		}catch(Exception e){}
		
		
		if(itemIds!=null&&quantitys!=null){
			//说明是退款单   itemid  数量
			if(date!=null){
				amazonOrder.setLastUpdateDate(date);
			}
			if("4".equals(hasTax)){
				file = SendEmailByOrderMonitor.genPartPdfByRefund(amazonProductService,country, amazonOrder, hasTax,itemIds,quantitys,orderId);
			}else{
				file = SendEmailByOrderMonitor.genPdfByRefund(amazonProductService,country, amazonOrder, hasTax,itemIds,quantitys);
			}
			
		}else if(StringUtils.isNotBlank(itemStr)&&StringUtils.isNotBlank(quantityStr)){
			if(deliveryDate!=null){
				amazonOrder.setDeliveryDate(deliveryDate);
			}
			file = SendEmailByOrderMonitor.genPdf(amazonProductService,country, amazonOrder, hasTax,itemStr,quantityStr);
		}else{
			if(deliveryDate!=null){
				amazonOrder.setDeliveryDate(deliveryDate);
			}
			file = SendEmailByOrderMonitor.genPdf(amazonProductService,country, amazonOrder, hasTax);
		}
		if (file != null) {
			return "1";
		} else {
			return "0";
		}
	}

	@RequiresPermissions("amazoninfo:order:view")
	@RequestMapping(value = "send")
	public String send(String bcc, String mail, String hasTax,String payment,	AmazonOrder amazonOrder, Model model,RedirectAttributes redirectAttributes,String quantityStr,String itemStr) {
		File file = null;
		String itemIds=amazonOrder.getPaymentMethod();
		String quantitys=amazonOrder.getMarketplaceId();
		Date date=amazonOrder.getLastUpdateDate();
		String orderId=amazonOrder.getAmazonOrderId();
		amazonOrder = this.amazonOrderService.get(amazonOrder.getId());
		String country= amazonOrder.getSalesChannel().substring(amazonOrder.getSalesChannel().lastIndexOf(".") + 1);
		/*try{
			Event event=eventService.IsExistEventByOrder(amazonOrder.getAmazonOrderId());
			if(event!=null&&StringUtils.isNotBlank(event.getTaxId())){
				amazonOrder.setRateSn(event.getTaxId());
			}
		}catch(Exception e){}*/
		if(StringUtils.isNotBlank(payment)){
			amazonOrder.setPaymentMethod(payment);
		}
		
		try{
			AmazonOrderExtract  orderExtract=amazonOrderService.findRateSnByOrderId(amazonOrder.getAmazonOrderId());
			if(orderExtract!=null&&StringUtils.isNotBlank(orderExtract.getRateSn())){
				amazonOrder.setRateSn(orderExtract.getRateSn());
			}
			AmazonAccountConfig config = amazonAccountConfigService.getByName(orderExtract.getAccountName());
			String invoiceType = config.getInvoiceType();
			String flag= invoiceType.substring(0, invoiceType.lastIndexOf("_"));
			String suffix= invoiceType.substring(invoiceType.lastIndexOf("_")+1);
			
			if(orderExtract==null||StringUtils.isBlank(orderExtract.getInvoiceNo())){
				if((orderExtract.getInvoiceFlag().startsWith("0")&&!"fr".equals(country))||
					((orderExtract.getInvoiceFlag().startsWith("00")||orderExtract.getInvoiceFlag().startsWith("10"))&&"fr".equals(country))
				 ){//未发送账单
					String invoiceNo=amazonOrderService.createFlowNo(flag,8,suffix);
					amazonOrder.setInvoiceNo(invoiceNo);
					amazonOrder.setPrintDate(new Date());
					amazonOrderService.updateInvoiceNoById(amazonOrder.getInvoiceNo(),amazonOrder.getAmazonOrderId()) ;
				}else{
					amazonOrder.setInvoiceNo(orderExtract.getId()+"");
				}
			}else{
				amazonOrder.setInvoiceNo(orderExtract.getInvoiceNo());
				amazonOrder.setPrintDate(orderExtract.getPrintDate());
			}
		}catch(Exception e){}
		
		if(itemIds!=null&&quantitys!=null){
			if(date!=null){
				amazonOrder.setLastUpdateDate(date);
			}
			
			if("4".equals(hasTax)){
				file = SendEmailByOrderMonitor.genPartPdfByRefund(amazonProductService,country, amazonOrder, hasTax,itemIds,quantitys,orderId);
			}else{
				file = SendEmailByOrderMonitor.genPdfByRefund(amazonProductService,country, amazonOrder, hasTax,itemIds,quantitys);
			}
			
		}else if(StringUtils.isNotBlank(itemStr)&&StringUtils.isNotBlank(quantityStr)){
			
			file = SendEmailByOrderMonitor.genPdf(amazonProductService,country, amazonOrder, hasTax,itemStr,quantityStr);
		}else{
			file = SendEmailByOrderMonitor.genPdf(amazonProductService,country, amazonOrder, hasTax);
		}
		
		if (file != null) {
			Map<String, String> params = Maps.newHashMap();
			/*for (AmazonOrderItem amazonOrderItem : amazonOrder.getItems()) {
				params.put("asin", amazonOrderItem.getAsin());
				break;
			}*/
			String toEmail = mail;
			if(toEmail.indexOf(",")>0){
				toEmail = mail.substring(0,mail.indexOf(","));
			}
			//不管怎样都放入token
			params.put("token",UnsubscribeEmailService.getUnsubscribeEmaliHref(toEmail));
			List<AmazonOrderItem> items = amazonOrder.getItems();
			params.put("asin", items.get(0).getAsin());
			if(StringUtils.isNotBlank(items.get(0).getName())){
				params.put("productName", items.get(0).getName());
			}else{
				params.put("productName", items.get(0).getTitle());
			}
			params.put("customerName",amazonOrder.getShippingAddress().getName());
			
			
			String template = SendEmailByOrderMonitor.getTemplate("invoice","_" +amazonOrder.getAccountName() , params);
			String subject = "Invoice";
			if ("de".equals(country)) {	subject = "Kaufbeleg";
			} else if ("fr".equals(country)) {
				subject = "[Important] Demande de facturation de la part du client Amazon Marketplace ";
			} 
			
			MailInfo mailInfo = new MailInfo(mail, subject + " "+ amazonOrder.getAmazonOrderId(), new Date());
			mailInfo.setContent(HtmlUtils.htmlUnescape(template));
			mailInfo.setFileName(file.getName());
			mailInfo.setFilePath(file.getAbsolutePath());
			if (StringUtils.isNotEmpty(bcc)) {
				mailInfo.setBccToAddress(bcc);
			}
			
			AmazonAccountConfig config=amazonAccountConfigService.getByName(amazonOrder.getAccountName());
			MailManagerInfo  info=sendCustomEmailManager.setCustomEmailManager(config.getEmailType(),config.getCustomerEmail(),config.getCustomerEmailPassword());
			sendCustomEmailManager.setManagerInfo(info);
			
			if (sendCustomEmailManager.send(mailInfo)) {
				sendCustomEmailManager.clearConnection();
				addMessage(redirectAttributes, MessageUtils.format("amazon_order_tips19"));
			} else {
				addMessage(redirectAttributes, MessageUtils.format("amazon_order_tips20"));
			}
		}
		return "redirect:" + Global.getAdminPath()+ "/amazoninfo/order/form?id=" + amazonOrder.getId();
	}
	
	
	
	public String getSaleChannel(String country){
		String saleChannel = "";
		StringBuilder sb = new StringBuilder("Amazon.");
		if(country.equals("de")||country.equals("com")||country.equals("ca")||country.equals("fr")||country.equals("it")||country.equals("es")){
			sb.append(country);
		}else if (country.equals("jp")||country.equals("uk")){
			sb.append("co.");
			sb.append(country);
		}else{
			sb.append(country);
		}
		saleChannel=sb.toString();
		return saleChannel;
	}
	
	//折扣统计
	@RequiresPermissions("amazoninfo:order:view")
	@RequestMapping(value = {"promotions"})
	public String listPromotions(AmazonOrder amazonOrder, HttpServletRequest request,
			HttpServletResponse response, Model model,String lineType) {
		if(StringUtils.isEmpty(amazonOrder.getSalesChannel())){
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					if (StringUtils.isEmpty(amazonOrder.getSalesChannel())) {
						amazonOrder.setSalesChannel(dict.getValue());
					} else {
						amazonOrder.setSalesChannel(null);
						break;
					}
				}
			}
		}
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (amazonOrder.getPurchaseDate() == null) {
			amazonOrder.setPurchaseDate(DateUtils.addMonths(today, -1));
			amazonOrder.setLastUpdateDate(today);
		}else{
			List<Map<String,Object>> data = amazonOrderService.countPromotions(amazonOrder.getPurchaseDate(),amazonOrder.getLastUpdateDate(),amazonOrder.getSalesChannel(),lineType);
			model.addAttribute("data",data);
		}
		model.addAttribute("nameTypeMap", psiProductService.findProductTypeMap());
		model.addAttribute("typeLineMap", groupDictService.getTypeLine(null));
		return "modules/amazoninfo/order/promotionsList";
	}
	
	private static DateFormat format = new SimpleDateFormat("yyyyMMdd");
	
	//折扣统计
	@RequiresPermissions("amazoninfo:order:view")
	@RequestMapping(value = {"promotionsList"})
	public String listPromotions(String country,String byTime,String dateStr, HttpServletRequest request,String lineType,
			HttpServletResponse response, Model model) {
		AmazonOrder  amazonOrder = new AmazonOrder();
		amazonOrder.setSalesChannel(country);
		Date start = null;
		Date end = null;
		if("1".equals(byTime)){
			try {
				start = format.parse(dateStr);
				end = DateUtils.addDays(start, 1);
				amazonOrder.setPurchaseDate(start);
				amazonOrder.setLastUpdateDate(end);
			} catch (ParseException e) {}
		}else if("2".equals(byTime)){
			int year = Integer.parseInt(dateStr.substring(0,4));
			int week = Integer.parseInt(dateStr.substring(4));
			start = DateUtils.getFirstDayOfWeek(year, week);
			end = DateUtils.getLastDayOfWeek(year, week);
			amazonOrder.setPurchaseDate(start);
			amazonOrder.setLastUpdateDate(end);
			end = DateUtils.addDays(end, 1);
		}else if("3".equals(byTime)){
			int year = Integer.parseInt(dateStr.substring(0,4));
			int month = Integer.parseInt(dateStr.substring(4))-1;
			try {
				start = DateUtils.getFirstDayOfMonth(year, month);
				end = DateUtils.getLastDayOfMonth(year, month);
				amazonOrder.setPurchaseDate(start);
				amazonOrder.setLastUpdateDate(end);
				end = DateUtils.addDays(end, 1);
			} catch (ParseException e) {}
		}
		List<Map<String,Object>> data = amazonOrderService.countPromotions(start,end,country,lineType);
		model.addAttribute("data",data);
		model.addAttribute("amazonOrder",amazonOrder);
		model.addAttribute("nameTypeMap", psiProductService.findProductTypeMap());
		model.addAttribute("typeLineMap", groupDictService.getTypeLine(null));
		return "modules/amazoninfo/order/promotionsList";
	}
	
	
	@ResponseBody
	@RequiresPermissions("amazoninfo:order:view")
	@RequestMapping(value = {"getPromotionOrders"})
	public String getPromotionsOrders(String promotionIds,Date startDate,Date endDate,String country,String asin,String price){
		try {
			promotionIds = URLDecoder.decode(promotionIds,"utf-8");
			promotionIds = HtmlUtils.htmlUnescape(promotionIds);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		List<Object[]> rs = amazonOrderService.findPromotionOrder(startDate, endDate, country, promotionIds,asin,price);
		if(rs.size()>0){
			List<String> orderIds = Lists.newArrayList();
			for (Object[] objs : rs) {
				orderIds.addAll(Lists.newArrayList(objs[2].toString().split(",")));
			}
			Map<String,String> reviewMap =  amazonOrderService.findPromotionReview(orderIds);
			if(reviewMap.size()>0){
				for (Object[] objs : rs) {
					List<String> orders = Lists.newArrayList(objs[2].toString().split(","));
					String str = "";
					StringBuffer buf= new StringBuffer();
					if(orders.size()>0){
						for (String oid : orders) {
							if(reviewMap.get(oid)!=null){
								buf.append(oid+"&nbsp;&nbsp;页面评分"+reviewMap.get(oid)+",");
							}else{
								buf.append(oid+",");
							}
						}
						str = buf.toString();
						objs[2] = str.substring(0,str.length()-1);
					}
				}
			}
			return JSON.toJSONString(rs);
		}
		return "";
	}
	
	
	@RequestMapping(value = {"promotionsExport"})
	public String listPromotionsExport(AmazonOrder amazonOrder, HttpServletRequest request,
			HttpServletResponse response, Model model,String lineType) {
		if(StringUtils.isEmpty(amazonOrder.getSalesChannel())){
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					if (StringUtils.isEmpty(amazonOrder.getSalesChannel())) {
						amazonOrder.setSalesChannel(dict.getValue());
					} else {
						amazonOrder.setSalesChannel(null);
						break;
					}
				}
			}
		}
		if(StringUtils.isEmpty(amazonOrder.getSalesChannel())){
			amazonOrder.setSalesChannel("de");
		}
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (amazonOrder.getPurchaseDate() == null) {
			amazonOrder.setPurchaseDate(DateUtils.addMonths(today, -1));
			amazonOrder.setLastUpdateDate(today);
		}else{
			List<Map<String,Object>> list = amazonOrderService.countPromotions(amazonOrder.getPurchaseDate(),amazonOrder.getLastUpdateDate(),amazonOrder.getSalesChannel(),lineType);
			try {
	            String name = SystemService.countryNameMap.get(amazonOrder.getSalesChannel());
	            if (StringUtils.isEmpty(name)) {
	            	name = "欧洲汇总";
				}
	        	SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	            String fileName = name + "折扣订单统计表"+sdf.format(new Date()) +".xlsx";
	            List<String> title = Lists.newArrayList("Name","Group","Asin","promotionIds","promotionDiscount","Sum","sales");
	            boolean flag = "eu".equals(amazonOrder.getSalesChannel())?true:false;
	            if (flag) {
					title.add("Country");
				}
	            DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
	            String str = "";
	            try {
	            	str = "("+ format.format(amazonOrder.getPurchaseDate()) + "~" + format.format(amazonOrder.getLastUpdateDate()) + ")";
				} catch (Exception e) {
					logger.info("导出折扣订单换算日期区间异常!!", e);
				}
	            ExportExcel excel = new ExportExcel(DictUtils.getDictLabel(amazonOrder.getSalesChannel(), "platform", "欧洲汇总") + "折扣订单统计表" + str,title);
	        	for (Map<String,Object> map : list) {
	        	    Row row = excel.addRow();
	        	    int i = 0;
					excel.addCell(row, i++, map.get("name"));
					excel.addCell(row, i++, map.get("groupName"));
					excel.addCell(row, i++, map.get("asin"));
					excel.addCell(row, i++, map.get("promotionIds"));
					excel.addCell(row, i++, map.get("promotionDiscount"));
					excel.addCell(row, i++, map.get("sum"));
					excel.addCell(row, i++, map.get("sales"));
					if (flag) {
						excel.addCell(row, i++, map.get("country"));
					}
				}
	            excel.write(response, fileName).dispose();
	    		return null;
			} catch (Exception e) {
				logger.error("导出折扣订单异常！！", e);
			}
		}
		return null;
	}
	
	@RequestMapping(value = {"promotionsExportAll"})
	public String listPromotionsExportAll(AmazonOrder amazonOrder, HttpServletRequest request,
			HttpServletResponse response, Model model) {
		if(StringUtils.isEmpty(amazonOrder.getSalesChannel())){
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					if (StringUtils.isEmpty(amazonOrder.getSalesChannel())) {
						amazonOrder.setSalesChannel(dict.getValue());
					} else {
						amazonOrder.setSalesChannel(null);
						break;
					}
				}
			}
		}
		Date today = new Date();
		today.setHours(0);
		today.setSeconds(0);
		today.setMinutes(0);
		if (amazonOrder.getPurchaseDate() == null) {
			amazonOrder.setPurchaseDate(DateUtils.addMonths(today, -1));
			amazonOrder.setLastUpdateDate(today);
		}else{
			List<Map<String,Object>> list = amazonOrderService.countPromotions(amazonOrder.getPurchaseDate(),amazonOrder.getLastUpdateDate());
			try {
	        	SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
	            String fileName = "各国汇总折扣订单统计表"+sdf.format(new Date()) +".xlsx";
	            List<String> title = Lists.newArrayList("Name","Group","Asin","promotionIds","promotionDiscount","Sum","sales","Country");
	            DateFormat format = new SimpleDateFormat("yyyy/MM/dd");
	            String str = "";
	            try {
	            	str = "("+ format.format(amazonOrder.getPurchaseDate()) + "~" + format.format(amazonOrder.getLastUpdateDate()) + ")";
				} catch (Exception e) {
					logger.info("导出各国汇总折扣订单换算日期区间异常!!", e);
				}
	            ExportExcel excel = new ExportExcel("各国汇总折扣订单统计表" + str, title);
	        	for (Map<String,Object> map : list) {
	        	    Row row = excel.addRow();
	        	    int i = 0;
					excel.addCell(row, i++, map.get("name"));
					excel.addCell(row, i++, map.get("groupName"));
					excel.addCell(row, i++, map.get("asin"));
					excel.addCell(row, i++, map.get("promotionIds"));
					excel.addCell(row, i++, map.get("promotionDiscount"));
					excel.addCell(row, i++, map.get("sum"));
					excel.addCell(row, i++, map.get("sales"));
					excel.addCell(row, i++, map.get("country"));
				}
	            excel.write(response, fileName).dispose();
	    		return null;
			} catch (Exception e) {
				logger.error("导出各国汇总折扣订单异常！！", e);
			}
		}
		return null;
	}
	
	@ResponseBody
	@RequestMapping(value = {"ajaxCumulative"})
	public  String getAjaxCumulativeQuantity(String sku,String promId){
		Integer  cumulative=this.amazonOrderService.getCumulativeQuantity(sku, promId);
		if(cumulative!=null){
			return cumulative+"";
		}else{
			return "";
		}
		
	}
	
	@ResponseBody
	@RequestMapping(value = {"ajaxCumulativeByAsin"})
	public  String ajaxCumulativeByAsin(String country,String asin,String promId,String start,String end){
		promId=HtmlUtils.htmlUnescape(promId);
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Integer  cumulative=this.amazonOrderService.getCumulativeQuantityByAsin(country,asin, promId);
		Integer totalQuantity=0;
		try {
			Date startDate = dateFormat.parse(start);
			Date endDate=dateFormat.parse(end);
			totalQuantity=amazonOrderService.getQuantityByTime(country,startDate,endDate,asin);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if(totalQuantity>0){
			if(cumulative!=null){
				return cumulative+",折扣期总销量:"+totalQuantity+",折扣使用率:"+MathUtils.roundUp(cumulative*100d/totalQuantity)+"%";
			}else{
				return "0,总数量:"+totalQuantity;
			}
		}else{
			if(cumulative!=null){
				return cumulative+"";
			}else{
				return "";
			}
		}
	}
	
	@RequestMapping(value =  "expOrderPhone" )
	public void expOrderPhone(AmazonOrder amazonOrder, HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
		
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/download;charset=utf-8");
			response.setHeader("Content-disposition", "attachment;filename=All.csv");
			OutputStream o = response.getOutputStream();
			OutputStreamWriter os = new OutputStreamWriter(o, "utf-8");
			BufferedWriter br=new BufferedWriter(os);
			Map<String,List<Object[]>> map=amazonOrderService.findAllPlatform(amazonOrder);
			//DISTINCT t.email,d.`phone`,r.`country`,d.name,d.postal_code,d.city_name,d.state_or_province
			String txt = "email,email,email,phone,phone,phone,madid,fn,ln,zip,ct,st,country,dob,doby,gen,age,uid\n";//Firstname, Lastname, Postalcode, Town, Country
			br.write(txt);
			for (Map.Entry<String, List<Object[]>> entry: map.entrySet()) { 
				List<Object[]> list= entry.getValue();
				for (Object[] obj: list) {
					String first="";
					String last="";
					if(obj[3]!=null){
						String name=(String)obj[3];
						if(name.indexOf(" ")>0){
							first=name.substring(0,name.lastIndexOf(" "));
							last=name.substring(name.lastIndexOf(" ")+1);
						}else{
							first=name;
						}
					}
					String email=(obj[0]==null?"":(String)obj[0]);
					String phone=(obj[1]==null?"":(String)obj[1]);
					String zip=(obj[4]==null?"":(String)obj[4]);
					String ct=(obj[5]==null?"":(String)obj[5]);
					String st=(obj[6]==null?"":(String)obj[6]);
					String country=(obj[2]==null?"":(String)obj[2]);
					txt =email+",,,"+phone+",,,,"+first+","+last+","+zip+","+ct+","+st+","+country+",,,,,\n";
					br.write(txt);
				}
			}
			br.flush();
			br.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		/* Map<String,List<Object[]>> map=amazonOrderService.findAllPlatform(amazonOrder);
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.YELLOW.index);
		style.setFillForegroundColor(HSSFColor.YELLOW.index);
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
		
		HSSFCellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		List<String> title=Lists.newArrayList("Email","Phone","Country","name","postal_code","city","state_or_region");
		
		if(map!=null&&map.size()>0){
			for (Map.Entry<String, List<Object[]>> entry: map.entrySet()) { 
			      String type = entry.getKey();
				  int rowIndex=1;
				  String sheetTitle="";
				  if("4".equals(type)){
					  sheetTitle="Amazon";
				  }else if("3".equals(type)){
					  sheetTitle="Ebay";
				  }else if("0".equals(type)){
					  sheetTitle="Official Website";
				  }else if("1".equals(type)){
					  sheetTitle="Check24";
				  }else{
					  sheetTitle="Offline";
				  }
	        	  HSSFSheet sheet= wb.createSheet(sheetTitle);
	        	  HSSFRow row = sheet.createRow(0);
				  row.setHeight((short) 400);
				  HSSFCell cell = null;		
				  for(int i = 0; i < title.size(); i++){
						cell = row.createCell(i);
						cell.setCellStyle(style);
						cell.setCellValue(title.get(i));
						sheet.autoSizeColumn((short)i);
				  }
				  List<Object[]> list= entry.getValue();
				  for (Object[] obj: list) {
					  int j=0;
					  row = sheet.createRow(rowIndex++);
					  row.createCell(j++).setCellValue((String)obj[0]);
					  row.getCell(j-1).setCellStyle(contentStyle);
					  row.createCell(j++).setCellValue((String)obj[1]);
					  row.getCell(j-1).setCellStyle(contentStyle);
					  row.createCell(j++).setCellValue((String)obj[2]);
					  row.getCell(j-1).setCellStyle(contentStyle);
					  row.createCell(j++).setCellValue((String)obj[3]);
					  row.getCell(j-1).setCellStyle(contentStyle);
					  row.createCell(j++).setCellValue((String)obj[4]);
					  row.getCell(j-1).setCellStyle(contentStyle);
					  row.createCell(j++).setCellValue((String)obj[5]);
					  row.getCell(j-1).setCellStyle(contentStyle);
					  row.createCell(j++).setCellValue((String)obj[6]);
					  row.getCell(j-1).setCellStyle(contentStyle);
					  
				  }
				  
				  sheet.setColumnWidth(0,36*256);
				  sheet.setColumnWidth(1,18*256);
				  sheet.setColumnWidth(2,7*256);
			}
		}
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "PhoneAndEmail" + sdf.format(new Date()) + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;*/
	}
	
	@RequestMapping(value =  "expOrderPhone2" )
	public String expOrderPhone2(AmazonOrder amazonOrder, String productName,HttpServletRequest request,HttpServletResponse response, Model model) throws UnsupportedEncodingException, ParseException {
		Map<String,List<Object[]>> map=Maps.newHashMap();
		if(StringUtils.isNotBlank(productName)){
			 map=amazonOrderService.findAllPlatform2(amazonOrder,productName);
		}else{
			 map=amazonOrderService.findAllPlatform2(amazonOrder);
		}
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setFillBackgroundColor(HSSFColor.YELLOW.index);
		style.setFillForegroundColor(HSSFColor.YELLOW.index);
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
		
		HSSFCellStyle contentStyle = wb.createCellStyle();
		contentStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		List<String> title=Lists.newArrayList("Email","Firstname","Lastname","Postalcode","Town","Country");
		
		if(map!=null&&map.size()>0){
			for (Map.Entry<String, List<Object[]>> entry: map.entrySet()) { 
			      String type = entry.getKey();
				  int rowIndex=1;
				  String sheetTitle="";
				  if("4".equals(type)){
					  sheetTitle="Amazon";
				  }else if("3".equals(type)){
					  sheetTitle="Ebay";
				  }else if("0".equals(type)){
					  sheetTitle="Official Website";
				  }else if("1".equals(type)){
					  sheetTitle="Check24";
				  }else{
					  sheetTitle="Offline";
				  }
	        	  HSSFSheet sheet= wb.createSheet(sheetTitle);
	        	  HSSFRow row = sheet.createRow(0);
				  row.setHeight((short) 400);
				  HSSFCell cell = null;		
				  for(int i = 0; i < title.size(); i++){
						cell = row.createCell(i);
						cell.setCellStyle(style);
						cell.setCellValue(title.get(i));
						sheet.autoSizeColumn((short)i);
				  }
				  List<Object[]> list= entry.getValue();
				  for (Object[] obj: list) {
					  int j=0;
					  row = sheet.createRow(rowIndex++);
					  row.createCell(j++).setCellValue(obj[0]==null?"":obj[0].toString());
					  row.getCell(j-1).setCellStyle(contentStyle);
					    String first="";
						String last="";
						if(obj[1]!=null){
							String name=(String)obj[1];
							if(name.indexOf(" ")>0){
								first=name.substring(0,name.lastIndexOf(" "));
								last=name.substring(name.lastIndexOf(" ")+1);
							}else{
								first=name;
							}
						}
					  row.createCell(j++).setCellValue(first);
					  row.getCell(j-1).setCellStyle(contentStyle);
					  row.createCell(j++).setCellValue(last);
					  row.getCell(j-1).setCellStyle(contentStyle);
					  row.createCell(j++).setCellValue(obj[2]==null?"":obj[2].toString());
					  row.getCell(j-1).setCellStyle(contentStyle);
					  row.createCell(j++).setCellValue(obj[3]==null?"":obj[3].toString());
					  row.getCell(j-1).setCellStyle(contentStyle);
					  row.createCell(j++).setCellValue(obj[4]==null?"":obj[4].toString());
					  row.getCell(j-1).setCellStyle(contentStyle);
				  }
				  
			}
		}
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			SimpleDateFormat sdf = new SimpleDateFormat("MMddhhmm");
			String fileName = "UserInfo" + sdf.format(new Date()) + ".xls";
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
	
	
	@RequestMapping(value = "exportCsv")
	public void exportCsv(AmazonOrder amazonOrder,HttpServletRequest request, HttpServletResponse response){
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/download;charset=utf-8");
			response.setHeader("Content-disposition", "attachment;filename=AmazonInfo.csv");
			OutputStream o = response.getOutputStream();
			OutputStreamWriter os = new OutputStreamWriter(o, "utf-8");
			BufferedWriter br=new BufferedWriter(os);
			Map<String,List<Object[]>> map=amazonOrderService.findAmazonPlatform(amazonOrder);
			String txt = "email,email,email,phone,phone,phone,madid,fn,ln,zip,ct,st,country,dob,doby,gen,age,uid\n";
			br.write(txt);
			for (Map.Entry<String, List<Object[]>> entry: map.entrySet()) { 
				List<Object[]> list= entry.getValue();
				for (Object[] obj: list) {
					String first="";
					String last="";
					if(obj[3]!=null){
						String name=(String)obj[3];
						if(name.indexOf(" ")>0){
							first=name.substring(0,name.lastIndexOf(" "));
							last=name.substring(name.lastIndexOf(" ")+1);
						}else{
							first=name;
						}
					}
					String email=(obj[0]==null?"":(String)obj[0]);
					String phone=(obj[1]==null?"":(String)obj[1]);
					String zip=(obj[4]==null?"":(String)obj[4]);
					String ct=(obj[5]==null?"":(String)obj[5]);
					String st=(obj[6]==null?"":(String)obj[6]);
					String country=(obj[2]==null?"":(String)obj[2]);
					txt =email+",,,"+phone+",,,,"+first+","+last+","+zip+","+ct+","+st+","+country+",,,,,\n";
					br.write(txt);
				}
			}
			br.flush();
			br.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	@RequestMapping(value = "exportEbayCsv")
	public void exportEbayCsv(AmazonOrder amazonOrder,HttpServletRequest request, HttpServletResponse response){
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/download;charset=utf-8");
			response.setHeader("Content-disposition", "attachment;filename=EbayInfo.csv");
			OutputStream o = response.getOutputStream();
			OutputStreamWriter os = new OutputStreamWriter(o, "utf-8");
			BufferedWriter br=new BufferedWriter(os);
			Map<String,List<Object[]>> map=amazonOrderService.findEbayPlatform(amazonOrder);
			String txt = "email,email,email,phone,phone,phone,madid,fn,ln,zip,ct,st,country,dob,doby,gen,age,uid\n";
			br.write(txt);
			for (Map.Entry<String, List<Object[]>> entry: map.entrySet()) { 
				List<Object[]> list= entry.getValue();
				for (Object[] obj: list) {
					String first="";
					String last="";
					if(obj[3]!=null){
						String name=(String)obj[3];
						if(name.indexOf(" ")>0){
							first=name.substring(0,name.lastIndexOf(" "));
							last=name.substring(name.lastIndexOf(" ")+1);
						}else{
							first=name;
						}
					}
					String email=(obj[0]==null?"":(String)obj[0]);
					String phone=(obj[1]==null?"":(String)obj[1]);
					String zip=(obj[4]==null?"":(String)obj[4]);
					String ct=(obj[5]==null?"":(String)obj[5]);
					String st=(obj[6]==null?"":(String)obj[6]);
					String country=(obj[2]==null?"":(String)obj[2]);
					txt =email+",,,"+phone+",,,,"+first+","+last+","+zip+","+ct+","+st+","+country+",,,,,\n";
					br.write(txt);
				}
			}
			br.flush();
			br.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
	}
}
