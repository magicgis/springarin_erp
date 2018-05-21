package com.springrain.erp.modules.amazoninfo.scheduler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.util.HtmlUtils;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.utils.FreeMarkers;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.excel.ExportExcel;
import com.springrain.erp.common.utils.excel.ImportExcel;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonAddress;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderItem;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.custom.scheduler.CustomEmailManager;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class GetOrderInfo {

	
	public static void main(String[] args) {
		/*ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		CustomEmailManager customEmailManager = applicationContext.getBean(CustomEmailManager.class);
		Set<String> set = getExcelInfo(new File("d:/2.xlsx"), -1,0);
		System.out.println(set.size());
		String content = getTemplate("111", "_it", null, new File("d:/111"));
		//Inateck - lettera di cercare aiuto
		for (String email : set) {
			noteCustom(email,"Inateck - lettera di cercare aiuto",content,customEmailManager);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		applicationContext.close();*/
		/*ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		AmazonOrderService orderService = applicationContext.getBean(AmazonOrderService.class);
		AmazonOrder a = new AmazonOrder();
		AmazonAddress d = new AmazonAddress();
		d.setOrder(a);
		a.setShippingAddress(d);
		orderService.save(a);
		applicationContext.close();*/
		try {
			CSVReader reader1 = new CSVReader(new InputStreamReader(new FileInputStream("d:/111/退货统计/退货单.csv"),"utf-8"));
			CSVReader reader2 = new CSVReader(new InputStreamReader(new FileInputStream("d:/111/退货统计/uk-10.csv"),"utf-8"));
			List<String[]> list1 = reader1.readAll();
			Set<String> rs1 = Sets.newHashSet();
			for (String[] strings : list1) {
				String s = strings[0];
				rs1.addAll(getOrders(s));
			}
			
			List<String[]> list2 = reader2.readAll();
			Set<String> rs2 = Sets.newHashSet();
			for (String[] strs : list2) {
				rs2.add(strs[0]);
			}
			System.out.println("一共订单号"+rs2.size()+"个");
			System.out.println("明细为："+rs2.toString());
			List<String> rst = Lists.newArrayList();
			for (String order : rs2) {
				if(rs1.contains(order)){
					rst.add(order);
				}
			}
			System.out.println("匹配退货信件"+rst.size()+"个");
			System.out.println("明细为："+rst.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	static Pattern ORDER_PATTERN = Pattern.compile("\\d{3}-\\d{7}-\\d{7}");
	public static Set<String> getOrders(String input){
		
		Set<String> rs = Sets.newHashSet();
		if(StringUtils.isNotEmpty(input)){
			Matcher matcher = ORDER_PATTERN.matcher(input);
			while(matcher.find()){
				rs.add(matcher.group());
			}
		}
		return rs;
	}  
	
	
	public static String getTemplate(String name, String country,
			Map<String, String> params,File dir) {
		if (country == null) {
			return "";
		}
		try {
			Configuration cfg = new Configuration();
			cfg.setDirectoryForTemplateLoading(dir);
			Template template = cfg.getTemplate(name + country + ".ftl");
			if (params != null && params.size() > 0) {
				return FreeMarkers.renderTemplate(template, params);
			} else {
				return FreeMarkers.renderTemplate(template, null);
			}
		} catch (Exception e) {
			if (country.length() > 0) {
				return getTemplate(name, "", params,dir);
			} else {
				return getTemplate(name, null, params,dir);
			}
		}
	}
	
	public static boolean noteCustom(String email, String subject, String template,CustomEmailManager customEmailManager) {
		try {
			MailInfo mailInfo = new MailInfo(email, subject, new Date());
			mailInfo.setContent(HtmlUtils.htmlUnescape(template));
			return customEmailManager.send(mailInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static Set<String> getExcelInfo(File file,int header,int col){
		ImportExcel importExcel;
		Set<String> set = Sets.newHashSet();
		try {
			importExcel = new ImportExcel(file,header);
			for (int i = importExcel.getDataRowNum(); i <= importExcel.getLastDataRowNum(); i++) {
				Row row = importExcel.getRow(i);
				if(row!=null && row.getCell(col)!=null&&row.getCell(col).toString().length()>0){
					set.add(row.getCell(col).toString());
				}	
			}
			return set;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return set;
	}
	
	public static void getOrderInfo(){
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		AmazonOrderService service = applicationContext.getBean(AmazonOrderService.class);
		try {
			ImportExcel importExcel = new ImportExcel("d:/shipped.xlsx",0);
			Set<String> set = Sets.newHashSet();
			for (int i = importExcel.getDataRowNum(); i <= importExcel.getLastDataRowNum(); i++) {
				Row row = importExcel.getRow(i);
				if(row!=null && row.getCell(0)!=null){
					set.add(row.getCell(0).toString());
				}	
			}
			List<Map<String,String>> result = Lists.newArrayList();
			for (String orderId : set) {
				Map<String, String> map = Maps.newHashMap();
				AmazonOrder order = service.findByEg(orderId);
				map.put("order", orderId);
				map.put("price", "");
				map.put("email", "");
				map.put("phone", "");
				if(order!=null){
					List<AmazonOrderItem> items = order.getItems();
					float price = 0;
					for (AmazonOrderItem amazonOrderItem : items) {
						if(amazonOrderItem.getItemPrice()!=null)
							price += amazonOrderItem.getItemPrice();
					}
					AmazonAddress address = order.getShippingAddress();
					map.put("price", price+"");
					map.put("email", order.getBuyerEmail());
					if(address!=null){
						map.put("phone", address.getPhone());
					}
				}
				result.add(map);
			}
			ExportExcel export = new ExportExcel("客户邮箱数据", new String[]{"订单","价格","邮箱","电话"});
			for (Map<String, String> map : result) {
				Row row = export.addRow();
				export.addCell(row, 0, map.get("order"));
				export.addCell(row, 1,  map.get("price"));
				export.addCell(row, 2,  map.get("email"));
				export.addCell(row, 3,  map.get("phone"));
			}
			export.writeFile("d:/111.xlsx");
		} catch (Exception e) {
			e.printStackTrace();
		}
		applicationContext.close();
	}
	
	
}
