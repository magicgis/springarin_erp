package com.springrain.erp.common.utils.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.springframework.web.context.ContextLoader;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.BarcodeEAN;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.lowagie.text.pdf.BaseFont;
import com.springrain.erp.common.utils.FreeMarkers;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.entity.FbaInbound;
import com.springrain.erp.modules.psi.entity.PsiLadingBill;
import com.springrain.erp.modules.psi.entity.PsiLadingBillItem;
import com.springrain.erp.modules.psi.entity.PurchaseOrder;
import com.springrain.erp.modules.psi.entity.PurchaseOrderItem;
import com.springrain.erp.modules.psi.entity.lc.LcPsiLadingBill;
import com.springrain.erp.modules.psi.entity.lc.LcPsiLadingBillItem;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsDelivery;
import com.springrain.erp.modules.psi.entity.lc.LcPsiPartsOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrderItem;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsDelivery;
import com.springrain.erp.modules.psi.entity.parts.PsiPartsOrder;
import com.springrain.erp.modules.sys.utils.UserUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class PdfUtil {
	
	public final static String EAN13 = "2";
	
	public final static String CODE128 = "1";
	
	private static String path;
	
	public static void htmlToPdf(File htmlFile, File pdfFile, File imageDir)
			throws Exception {
		// step 1
		String url = htmlFile.toURI().toURL().toString();
		System.out.println(url);
		// step 2
		OutputStream os = new FileOutputStream(pdfFile);
		ITextRenderer renderer = new ITextRenderer();
		renderer.setDocument(url);

		// step 3 解决日文支持
		org.xhtmlrenderer.pdf.ITextFontResolver fontResolver = renderer
				.getFontResolver();
		if(new File(imageDir.getAbsolutePath(),"/MSGOTHIC.TTC").exists()){
			fontResolver.addFont(imageDir.getAbsolutePath() + "/MSGOTHIC.TTC",
					BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
		}
		if(path==null){
			path = ContextLoader.getCurrentWebApplicationContext()
					.getServletContext().getRealPath("/")
					+ "WEB-INF/classes/templates";
		}
		fontResolver.addFont(path+"/simsun.ttc", BaseFont.IDENTITY_H,     
	                BaseFont.NOT_EMBEDDED);  
		renderer.getSharedContext().setBaseURL(
				imageDir.toURI().toURL().toString());
		renderer.layout();
		renderer.createPDF(os);
		os.close();
		System.out.println("create pdf done!!");
	}

	public static void createBarCodePdf(String type,File file, String code, String title,
			String newLogo) throws IOException, DocumentException {
		if(StringUtils.isEmpty(code)){
			return;
		}
		// step 1
		float right = 253;
		if(!CODE128.equals(type)){
			right = 333;
		}
		Document document = new Document(PageSize.A4, 50, right, 100, 50);
		// step 2
		PdfWriter writer = PdfWriter.getInstance(document,
				new FileOutputStream(file));
		// step 3
		document.open();
		// step 4
		PdfContentByte cb = writer.getDirectContent();
		if(CODE128.equals(type)){
		// CODE 128
			Barcode128 shipBarCode = new Barcode128();
			shipBarCode.setX(2f);
			shipBarCode.setSize(20f);
			shipBarCode.getFont().setPostscriptFontName(BaseFont.HELVETICA_BOLD);
			shipBarCode.setTextAlignment(Element.ALIGN_CENTER);
			shipBarCode.setBaseline(23f);
			shipBarCode.setBarHeight(50f);
			shipBarCode.setCode(code);
			document.add(shipBarCode.createImageWithBarcode(cb, null, null));
			title = title+"\n"+newLogo;
		}else{
			BarcodeEAN codeEAN = new BarcodeEAN();  
			codeEAN.setX(2f);
			codeEAN.setSize(20f);
			codeEAN.getFont().setPostscriptFontName(BaseFont.HELVETICA_BOLD);
			codeEAN.setTextAlignment(Element.ALIGN_CENTER);
			codeEAN.setBaseline(23f);
			codeEAN.setBarHeight(100f);
	        codeEAN.setCode(code);  
	        document.add(codeEAN.createImageWithBarcode(cb, null, null));  
		}
		if(CODE128.equals(type)){
			Paragraph text = new Paragraph(16f, title, FontFactory.getFont(
					FontFactory.HELVETICA, 20f, Font.BOLD));
			int index = 27-title.length();
			if(index>0){
				text.setIndentationLeft(10f*index);
			}
			document.add(text);
		}
		// step 5
		document.close();
	}
	
	public static void genPurchaseOrderPdf(File pdfFile,PurchaseOrder order,Map<String,String> versionMap) throws Exception{
		//获得产品的合同号
		Set<String>  contracts = Sets.newHashSet();
		for(PurchaseOrderItem item :order.getItems()){
			if(StringUtils.isNotEmpty(item.getProduct().getContractNo())){
				contracts.add(item.getProduct().getContractNo());
			}
		}
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("myOrder", order);
		params.put("versionMap", versionMap);
		params.put("cuser", UserUtils.getUser());
		params.put("contracts", contracts.toString());
	
		String template = getPsiTemplate("purpdfTemplet.ftl",params);
		File parent = pdfFile.getParentFile();
		if(!parent.exists()){
			parent.mkdirs();
		}
		File htmlFile = new File(parent,"temp.html");
		Files.write(template.subSequence(0, template.length()), htmlFile, Charset.forName("utf-8"));
		htmlToPdf(htmlFile, pdfFile,new File(path+"/psi"));
		htmlFile.delete();
	}
	
	public static void genPurchaseOrderPdf(File pdfFile,LcPurchaseOrder order,Map<String,String> versionMap) throws Exception{
		//获得产品的合同号
		Set<String>  contracts = Sets.newHashSet();
		for(LcPurchaseOrderItem item :order.getItems()){
			if(StringUtils.isNotEmpty(item.getProduct().getContractNo())){
				contracts.add(item.getProduct().getContractNo());
			}
			//PDF中加入模具费,目的为把模具费算到发票中
			if (item.getMoldFee() != null) {
				item.setItemPrice(item.getItemPrice().add(item.getMoldFee()));
			}
		}
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("myOrder", order);
		params.put("versionMap", versionMap);
		params.put("cuser", UserUtils.getUser());
		params.put("contracts", contracts.toString());
	
		String template = getPsiTemplate("lcPurpdfTemplet.ftl",params);
		File parent = pdfFile.getParentFile();
		if(!parent.exists()){
			parent.mkdirs();
		}
		File htmlFile = new File(parent,"temp.html");
		Files.write(template.subSequence(0, template.length()), htmlFile, Charset.forName("utf-8"));
		htmlToPdf(htmlFile, pdfFile,new File(path+"/psi"));
		htmlFile.delete();
	}
	
	public static void genFbaInboundPdf(File pdfFile,FbaInbound fbaInbound) throws Exception{
		Map<String, Object> params = Maps.newHashMap();
		params.put("fbaInbound", fbaInbound);
		params.put("currentDate",new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
		String currentDateNo=new SimpleDateFormat("ddMMyyyy").format(new Date());
		if("uk".equals(fbaInbound.getCountry())){
			currentDateNo+="01";
		}else if("fr".equals(fbaInbound.getCountry())){
			currentDateNo+="02";
		}else if("it".equals(fbaInbound.getCountry())){
			currentDateNo+="03";
		}else if("es".equals(fbaInbound.getCountry())){
			currentDateNo+="04";
		}
		params.put("currentDateNo",currentDateNo);
		String template = getPsiTemplate("fbaPackingList.ftl",params);
		File parent = pdfFile.getParentFile();
		if(!parent.exists()){
			parent.mkdirs();
		}
		File htmlFile = new File(parent,"temp.html");
		Files.write(template.subSequence(0, template.length()), htmlFile, Charset.forName("utf-8"));
		htmlToPdf(htmlFile, pdfFile,new File(path+"/psi"));
		htmlFile.delete();
	}
	
	public static void genPsiLadingBillPdf(File pdfFile,PsiLadingBill bill,Map<String,String> fnSkuMap ) throws Exception{
		Map<String,Map<String,Integer>> singleCountryMap = Maps.newHashMap();
		Map<String,Integer> singleProductMap = Maps.newHashMap();
		
		for(PsiLadingBillItem item:bill.getItems()){
			String productColor = item.getProductName()+item.getColorCode();
			String country =item.getCountryCode();
			Integer boxNum =new BigDecimal(item.getQuantityLading()).divide(new BigDecimal(item.getPurchaseOrderItem().getProduct().getPackNumsByCountry(country)),0,BigDecimal.ROUND_UP).intValue();
			Map<String,Integer> countryMap = null;
			if(singleCountryMap.get(productColor)==null){
				countryMap=Maps.newHashMap();
			}else{
				countryMap=singleCountryMap.get(productColor);
				if(countryMap.get(country)!=null){
					boxNum+=countryMap.get(country);
				}
			}
			countryMap.put(country, boxNum);
			singleCountryMap.put(productColor, countryMap);
		}
		
		Integer totalPack=0;
		
		for(String productColor:singleCountryMap.keySet()){
			Integer proPack=0;
			Map<String,Integer> couMap = singleCountryMap.get(productColor);
			for(String countryKey:couMap.keySet()){
				proPack+=couMap.get(countryKey);
			}
			totalPack+=proPack;
			singleProductMap.put(productColor, proPack);
		}
		
		
		Map<String, Object> params = Maps.newHashMap();
		params.put("fnSkuMap", fnSkuMap);
		params.put("bill", bill);
		params.put("cuser", UserUtils.getUser());
		params.put("totalPack", totalPack);
		params.put("singleProductMap", singleProductMap);
		params.put("singleCountryMap", singleCountryMap);
		String template = getPsiTemplate("ladingpdfTemplet.ftl",params);
		File parent = pdfFile.getParentFile();
		if(!parent.exists()){
			parent.mkdirs();
		}
		File htmlFile = new File(parent,"temp.html");
		Files.write(template.subSequence(0, template.length()), htmlFile, Charset.forName("utf-8"));
		htmlToPdf(htmlFile, pdfFile,new File(path+"/psi"));
		htmlFile.delete();
	}
	
	public static void genPsiLadingBillPdf(File pdfFile,LcPsiLadingBill bill,Map<String,String> fnSkuMap,Map<String,Integer> canSendMap,Map<String,String> remarkMap) throws Exception{
		Map<String,Map<String,Integer>> singleCountryMap = Maps.newHashMap();
		Map<String,Integer> singleProductMap = Maps.newHashMap();
		Map<String,Integer> packMap = Maps.newHashMap();
		for(LcPsiLadingBillItem item:bill.getItems()){
			String productColor = item.getProductName()+item.getColorCode();
			String country =item.getCountryCode();
			Integer quantity = item.getQuantityLading();
//			Integer boxNum =new BigDecimal(item.getQuantityLading()).divide(new BigDecimal(item.getPurchaseOrderItem().getProduct().getPackNumsByCountry(country)),0,BigDecimal.ROUND_UP).intValue();
			Map<String,Integer> countryMap = null;
			if(singleCountryMap.get(productColor)==null){
				countryMap=Maps.newHashMap();
			}else{
				countryMap=singleCountryMap.get(productColor);
				if(countryMap.get(country)!=null){
					quantity+=countryMap.get(country);
				}
			}
			if(packMap.get(productColor+",,"+country)==null){
				packMap.put(productColor+",,"+country, item.getPurchaseOrderItem().getProduct().getPackNumsByCountry(country));
			}
			
			countryMap.put(country, quantity);
			singleCountryMap.put(productColor, countryMap);
		}
		
		Integer totalPack=0;
		
		for(String productColor:singleCountryMap.keySet()){
			Integer proPack=0;
			Map<String,Integer> couMap = singleCountryMap.get(productColor);
			Map<String,Integer> copyMap = Maps.newHashMap();
			for(String countryKey:couMap.keySet()){
				Integer boxNum =new BigDecimal(couMap.get(countryKey)).divide(new BigDecimal(packMap.get(productColor+",,"+countryKey)),0,BigDecimal.ROUND_UP).intValue();
				copyMap.put(countryKey, boxNum);
				proPack+=boxNum;
			}
			totalPack+=proPack;
			singleProductMap.put(productColor, proPack);
			singleCountryMap.put(productColor, copyMap);
		}
		
		
		Map<String, Object> params = Maps.newHashMap();
//		params.put("versionMap", versionMap);
		params.put("remarkMap", remarkMap);
		params.put("canSendMap", canSendMap);
		params.put("fnSkuMap", fnSkuMap);
		params.put("bill", bill);
		params.put("cuser", UserUtils.getUser());
		params.put("totalPack", totalPack);
		params.put("singleProductMap", singleProductMap);
		params.put("singleCountryMap", singleCountryMap);
		String template = getPsiTemplate("lcLadingpdfTemplet.ftl",params);
		File parent = pdfFile.getParentFile();
		if(!parent.exists()){
			parent.mkdirs();
		}
		File htmlFile = new File(parent,"temp.html");
		Files.write(template.subSequence(0, template.length()), htmlFile, Charset.forName("utf-8"));
		htmlToPdf(htmlFile, pdfFile,new File(path+"/psi"));
		htmlFile.delete();
	}
	
	
	public static void genPartsOrderPdf(File pdfFile,PsiPartsOrder order) throws Exception{
		Map<String, Object> params = Maps.newHashMap();
		params.put("myOrder", order);
		params.put("cuser", UserUtils.getUser());
		String template = getPsiTemplate("partsPdfTemplet.ftl",params);
		File parent = pdfFile.getParentFile();
		if(!parent.exists()){
			parent.mkdirs();
		}
		File htmlFile = new File(parent,"temp.html");
		Files.write(template.subSequence(0, template.length()), htmlFile, Charset.forName("utf-8"));
		htmlToPdf(htmlFile, pdfFile,new File(path+"/psi"));
		htmlFile.delete();
	}
	
	public static void genPartsOrderPdf(File pdfFile,LcPsiPartsOrder order) throws Exception{
		Map<String, Object> params = Maps.newHashMap();
		params.put("myOrder", order);
		params.put("cuser", UserUtils.getUser());
		String template = getPsiTemplate("partsPdfTemplet.ftl",params);
		File parent = pdfFile.getParentFile();
		if(!parent.exists()){
			parent.mkdirs();
		}
		File htmlFile = new File(parent,"temp.html");
		Files.write(template.subSequence(0, template.length()), htmlFile, Charset.forName("utf-8"));
		htmlToPdf(htmlFile, pdfFile,new File(path+"/psi"));
		htmlFile.delete();
	}
	
	public static void genPsiPartsDeliveryPdf(File pdfFile,PsiPartsDelivery bill) throws Exception{
		Map<String, Object> params = Maps.newHashMap();
		params.put("bill", bill);
		params.put("cuser", UserUtils.getUser());
		String template = getPsiTemplate("partsDeliveryPdfTemplet.ftl",params);
		File parent = pdfFile.getParentFile();
		if(!parent.exists()){
			parent.mkdirs();
		}
		File htmlFile = new File(parent,"temp.html");
		Files.write(template.subSequence(0, template.length()), htmlFile, Charset.forName("utf-8"));
		htmlToPdf(htmlFile, pdfFile,new File(path+"/psi"));
		htmlFile.delete();
	}
	
	public static void genPsiPartsDeliveryPdf(File pdfFile,LcPsiPartsDelivery bill) throws Exception{
		Map<String, Object> params = Maps.newHashMap();
		params.put("bill", bill);
		params.put("cuser", UserUtils.getUser());
		String template = getPsiTemplate("partsDeliveryPdfTemplet.ftl",params);
		File parent = pdfFile.getParentFile();
		if(!parent.exists()){
			parent.mkdirs();
		}
		File htmlFile = new File(parent,"temp.html");
		Files.write(template.subSequence(0, template.length()), htmlFile, Charset.forName("utf-8"));
		htmlToPdf(htmlFile, pdfFile,new File(path+"/psi"));
		htmlFile.delete();
	}

	
	public static String getPsiTemplate(String name,Map<String, Object> params) throws Exception {
		Configuration cfg = new Configuration();
		if(path==null){
			path = ContextLoader.getCurrentWebApplicationContext()
					.getServletContext().getRealPath("/")
					+ "WEB-INF/classes/templates";
		}
		cfg.setDefaultEncoding("utf-8");
		cfg.setDirectoryForTemplateLoading(new File(path+"/psi"));
		Template template = cfg.getTemplate(name);
		return FreeMarkers.renderTemplate(template, params);
	}
	
}
