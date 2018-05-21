package com.springrain.erp.modules.psi.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.cxf.endpoint.Client;
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
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.config.LogisticsSupplier;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.ZipUtil;
import com.springrain.erp.common.utils.excel.ExportTransportExcel;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.custom.entity.SendEmail;
import com.springrain.erp.modules.psi.entity.FbaInbound;
import com.springrain.erp.modules.psi.entity.FbaInboundItem;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.Stock;
import com.springrain.erp.modules.psi.service.FbaInboundService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiSkuChangeBillService;
import com.springrain.erp.modules.psi.service.PsiTransportOrderService;
import com.springrain.erp.modules.psi.service.StockService;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportOrderService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * FBA帖子Controller
 * @author Tim
 * @version 2015-01-29
 */
@Controller
@RequestMapping(value = "${adminPath}/psi/fbaInbound")
public class FbaInboundController extends BaseController {
	
	
	@Autowired
	private FbaInboundService fbaInboundService;
	@Autowired
	private PsiProductService psiProductService;
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	@Autowired
	private PsiInventoryService  psiInventoryService;
	@Autowired
	private PsiTransportOrderService   tranService;
	@Autowired
	private LcPsiTransportOrderService   lcTranService;
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(FbaInboundController.class);
	
	@ModelAttribute
	public FbaInbound get(@RequestParam(required=false) Integer id) {
		if (id !=null){
			return fbaInboundService.get(id);
		}else{
			return new FbaInbound();
		}
	}
	
	@RequestMapping(value = "exportRegExcel")
	public String exportRegExcel(FbaInbound fbaInbound,HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		ExportTransportExcel ete = new ExportTransportExcel();
		Workbook workbook = null;
		String modelName = "";//模板文件名称
		String xmlName = "";
		if("fr".equals(fbaInbound.getCountry())){
			xmlName="FBA-FR";
			modelName="FBA-FR";
				if("LIL1".equals(fbaInbound.getDestinationFulfillmentCenterId())){
					fbaInbound.setAmazonWareHouse("59553 Lauwin Planque(LIL1)");
				}else if("LYS1".equals(fbaInbound.getDestinationFulfillmentCenterId())){
					fbaInbound.setAmazonWareHouse("71100 Sevrey(LYS1)");
				}else if("MRS1".equals(fbaInbound.getDestinationFulfillmentCenterId())){
					fbaInbound.setAmazonWareHouse("F-26200 Montelimar(MRS1)");
				}else if("ORY1".equals(fbaInbound.getDestinationFulfillmentCenterId())){
					fbaInbound.setAmazonWareHouse("1401 rue du champ rouge(ORY1)");
				}
		}else if("it".equals(fbaInbound.getCountry())){
			xmlName="FBA-IT";
			modelName="FBA-IT";
		}else if("es".equals(fbaInbound.getCountry())){
			xmlName="FBA-ES";
			modelName="FBA-ES";
		}else if("uk".equals(fbaInbound.getCountry())){
			xmlName="FBA-UK";
			modelName="FBA-UK";
				if("LTN1".equals(fbaInbound.getDestinationFulfillmentCenterId())){	
					fbaInbound.setAmazonWareHouse("Bedfordshire(LTN1)");
				}else if("EUK5".equals(fbaInbound.getDestinationFulfillmentCenterId())){	
					fbaInbound.setAmazonWareHouse("Peterborough(EUK5)");
				}else if("GLA1".equals(fbaInbound.getDestinationFulfillmentCenterId())){	
					fbaInbound.setAmazonWareHouse("Inverclyde(GLA1)");
				}else if("CWL1".equals(fbaInbound.getDestinationFulfillmentCenterId())){	
					fbaInbound.setAmazonWareHouse("Swansea(CWL1)");
				}else if("EDI4".equals(fbaInbound.getDestinationFulfillmentCenterId())){
					fbaInbound.setAmazonWareHouse("Dunfermline(EDI4)");
				}else if("LBA1".equals(fbaInbound.getDestinationFulfillmentCenterId())){	
					fbaInbound.setAmazonWareHouse("Doncaster(LBA1)");
				}else if("BHX1".equals(fbaInbound.getDestinationFulfillmentCenterId())){	
					fbaInbound.setAmazonWareHouse("Rugeley(BHX1)");
				}else if("LTN2".equals(fbaInbound.getDestinationFulfillmentCenterId())){	
					fbaInbound.setAmazonWareHouse("Hertfordshire(LTN2)");
				}
		}
		int totalCarton=0;
		float totalWeight=0f;
		int totalQuantity=0;
		for (FbaInboundItem item:fbaInbound.getItemsByOrder()) {
			Object[] obj=amazonProduct2Service.findProductPackAndTypeBySku(item.getSku());
			if(obj!=null){
				totalQuantity+=item.getQuantityShipped();
				Integer carton=MathUtils.roundUp((double)item.getQuantityShipped()/(Integer)obj[1]);
				totalCarton+=carton;
				item.setCartonNo(carton);
				item.setDescription(obj[0].toString());
				totalWeight+=((BigDecimal)obj[2]).floatValue()*item.getQuantityShipped();
			}else{
				item.setCartonNo(0);
				item.setDescription("");
			}
		}
		fbaInbound.setTotalCarton(totalCarton);
		fbaInbound.setTotalWeight(totalWeight);
		fbaInbound.setTotalQuantity(totalQuantity);
		workbook = ete.writeData(fbaInbound, xmlName,modelName, 0);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/x-download");
		String fileName = modelName+"_"+fbaInbound.getShipmentId()+".xlsx";
		fileName = URLEncoder.encode(fileName, "UTF-8");
		response.addHeader("Content-Disposition","attachment;filename=" + fileName);
		try {
			OutputStream out = response.getOutputStream();
			workbook.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@ResponseBody
	@RequestMapping(value = "printFbaInboundPdf")
	public String printPdf(String amazonZnr,Float totalWeight,FbaInbound fbaInbound,HttpServletResponse response) throws Exception {
		fbaInbound = fbaInboundService.get(fbaInbound.getId());
		int totalCarton=0;
		//float totalWeight=0f;
		int totalQuantity=0;
		for (FbaInboundItem item:fbaInbound.getItemsByOrder()) {
			Object[] obj=amazonProduct2Service.findProductPackAndTypeBySku(item.getSku());
			if(obj!=null){
				totalQuantity+=item.getQuantityShipped();
				Integer carton=MathUtils.roundUp((double)item.getQuantityShipped()/(Integer)obj[1]);
				totalCarton+=carton;
				item.setCartonNo(carton);
				item.setDescription(obj[0].toString());
				//totalWeight+=((BigDecimal)obj[2]).floatValue()*item.getQuantityShipped();
			}else{
				item.setCartonNo(0);
				item.setDescription("");
			}
		}
		fbaInbound.setAmazonZnr(amazonZnr);
		fbaInbound.setTotalCarton(totalCarton);
		fbaInbound.setTotalWeight(totalWeight);
		fbaInbound.setTotalQuantity(totalQuantity);
		if("fr".equals(fbaInbound.getCountry())){
			if("LIL1".equals(fbaInbound.getDestinationFulfillmentCenterId())){
				fbaInbound.setShippingAddress("lil1-dechargement@amazon.fr<br/>LIL1<br/>Amazon.fr Logistique SAS<br/>1 rue Amazon<br/>59553 Lauwin Planque<br/>");
			}else if("LYS1".equals(fbaInbound.getDestinationFulfillmentCenterId())){
				fbaInbound.setShippingAddress("lys1-dechargement@amazon.fr<br/>LYS1<br/>Amazon.fr Logistique SAS<br/>1 rue Amazon<br/>71100 Sevrey<br/>");
			}else if("MRS1".equals(fbaInbound.getDestinationFulfillmentCenterId())){
				fbaInbound.setShippingAddress("mrs1-dechargement@amazon.fr<br/>MRS1<br/>Amazon.fr Logistique SAS<br/>Building II, ZAC Les Portes de Provence<br/>F-26200  Montelimar<br/>");
			}else if("ORY1".equals(fbaInbound.getDestinationFulfillmentCenterId())){
				fbaInbound.setShippingAddress("dc-dechargement@amazon.fr<br/>ORY1<br/>Amazon.fr Logistique<br/>Pole 45<br/>1401 rue du champ rouge<br/>45770 Saran<br/>");
			}
		}else if("es".equals(fbaInbound.getCountry())){
			if("MAD4".equals(fbaInbound.getDestinationFulfillmentCenterId())){		
				fbaInbound.setShippingAddress("AMAZON EU SARL (MAD4)<br/>mad4-bookings@amazon.com<br/>C/O Amazon España Corporate Services S.L.<br/>Avenida de la Astronomía, 24<br/>San Fernando de Henares (Madrid), 28830<br/>Spain<br/>");			
			}
		}else if("it".equals(fbaInbound.getCountry())){
			if("MXP5".equals(fbaInbound.getDestinationFulfillmentCenterId())){		
				fbaInbound.setShippingAddress("Amazon IT Logistica Srl (MXP5)<br/>Strada Dogana Po, 2 U<br/>29015 Castel San Giovanni (PC) - Italia<br/>MXP5-bookings@amazon.com<br/>Opening hours : 6h00-19h00<br/>");			
			}
		}else if("uk".equals(fbaInbound.getCountry())){
			if("LTN1".equals(fbaInbound.getDestinationFulfillmentCenterId())){		
				fbaInbound.setShippingAddress("Milton Keynes (LTN1)<br/>LTN1-Bookings@amazon.com<br/>Amazon.co.uk<br/>Marston Gate Distribution Centre<br/>Ridgmont<br/>Bedfordshire<br/>MK43 OZA<br/>");			
			}else if("EUK5".equals(fbaInbound.getDestinationFulfillmentCenterId())){		
				fbaInbound.setShippingAddress("Peterborough (EUK5)<br/>peterboroughbookings@amazon.com<br/>Amazon.co.uk<br/>Phase Two, Kingston Park<br/>Flaxley Road<br/>Peterborough<br/>PE2 9EN<br/>");			
			}else if("GLA1".equals(fbaInbound.getDestinationFulfillmentCenterId())){		
				fbaInbound.setShippingAddress("Gourock (GLA1)<br/>gourockbookings@amazon.com<br/>Amazon.co.uk<br/>2 Cloch Road, Faulds Park<br/>Gourock<br/>Inverclyde<br/>PA19 1BQ<br/>");			
			}else if("CWL1".equals(fbaInbound.getDestinationFulfillmentCenterId())){		
				fbaInbound.setShippingAddress("Swansea (CWL1)<br/>swanseabookings@amazon.com<br/>Amazon.co.uk<br/>Ffordd Amazon<br/>Crymlyn Burrows<br/>Swansea<br/>SA1 8QX<br/>");			
			}else if("EDI4".equals(fbaInbound.getDestinationFulfillmentCenterId())){		
				fbaInbound.setShippingAddress("Dunfermline (EDI4)<br/>dunfermlinebookings@amazon.com<br/>Amazon.co.uk<br/>Amazon Way<br/>Dunfermline<br/>Swansea<br/>KY11 8ST<br/>");			
			}else if("LBA1".equals(fbaInbound.getDestinationFulfillmentCenterId())){		
				fbaInbound.setShippingAddress("Doncaster (LBA1)<br/>doncasterbookings@amazon.com<br/>Amazon.co.uk<br/>Firstpoint Business Park<br/>Balby Carr Bank<br/>Doncaster<br/>DN4 5JS<br/>");			
			}else if("BHX1".equals(fbaInbound.getDestinationFulfillmentCenterId())){		
				fbaInbound.setShippingAddress("Rugeley (BHX1)<br/>rugeleybookings@amazon.com<br/>Amazon.co.uk<br/>Towers Business Park<br/>Power Station Road<br/>Rugeley<br/>WS15 1LX<br/>");			
			}else if("LTN2".equals(fbaInbound.getDestinationFulfillmentCenterId())){		
				fbaInbound.setShippingAddress("Hemel Hempstead (LTN2)<br/>hemelhempsteadbookings@amazon.com<br/>Amazon.co.uk<br/>Hemel Hempstead<br/>Boundary Way<br/>Hertfordshire<br/>HP27LF<br/>");			
			}
		}
		String filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/fbaInbound";
		File file = new File(filePath, fbaInbound.getShipmentId());
		if (!file.exists()) {
			file.mkdirs();
		}
		File pdfFile = new File(file, fbaInbound.getShipmentId() + ".pdf");
		PdfUtil.genFbaInboundPdf(pdfFile, fbaInbound);
		FileInputStream in = new FileInputStream(pdfFile);
		ServletOutputStream out = response.getOutputStream();
		response.setContentType("application/pdf");// pdf文件
		response.addHeader("Content-Disposition", "filename="
					+ fbaInbound.getShipmentId()+".pdf");
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
	@RequiresPermissions("psi:fbaInbound:view")
	@RequestMapping(value = {"list",""})
	public String list(FbaInbound fbaInbound, HttpServletRequest request, HttpServletResponse response, Model model) {
		User user = UserUtils.getUser();
		if (!user.isAdmin()){
			fbaInbound.setCreateBy(user);
		}
		if(fbaInbound.getCreateDate()==null){
			Date today = new Date();
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			fbaInbound.setLastUpdateDate(today);
			if(StringUtils.isEmpty(fbaInbound.getShipmentId())){
				fbaInbound.setCreateDate(DateUtils.addDays(today, -60));
			}else{
				fbaInbound.setCreateDate(new Date(114,0,1));
			}
		}
		fbaInbound.getLastUpdateDate().setHours(23);
		fbaInbound.getLastUpdateDate().setMinutes(59);
		fbaInbound.getLastUpdateDate().setSeconds(59);
		if(fbaInbound.getCountry()==null){
			fbaInbound.setCountry("de");
		}
		if(fbaInbound.getShipmentStatus()==null){
			fbaInbound.setShipmentStatus("-1");
		}
		Page<FbaInbound> page = new Page<FbaInbound>(request, response);
		String orderBy = page.getOrderBy();
		if ("".equals(orderBy)) {
			page.setOrderBy("id desc");
		} else {
			page.setOrderBy(orderBy + ",id desc");
		}
        page = fbaInboundService.find(page, fbaInbound);
        if (UserUtils.hasPermission("psi:fbaInbound:response")) {
        	 //查询sku库存
            Map<String, Integer> skuQty = psiInventoryService.findSkuNewQuantity(19);
            Map<String, String> canConfirmMap = Maps.newHashMap();
            for (FbaInbound inbound : page.getList()) {
    			if("DE".equals(inbound.getShipFromAddress()) && 
    					"1".equals(inbound.getAreCasesRequired()) && 
    					"WORKING".equals(inbound.getShipmentStatus()) &&
    					"0".equals(inbound.getResponseLevel()) &&
    					inbound.getResponseTime() == null){
    				canConfirmMap.put(inbound.getId()+inbound.getShipFromAddress(), "0");
    				for (FbaInboundItem item : inbound.getItems()) {
    					if (skuQty.get(item.getSku())==null || item.getQuantityShipped()>skuQty.get(item.getSku())) {
    						canConfirmMap.put(inbound.getId()+inbound.getShipFromAddress(), "1");//库存不足,不能确认
    						break;
    					}
    				}
    			}
    		}
            model.addAttribute("canConfirmMap", canConfirmMap);
		}
        
        model.addAttribute("tranMap",psiInventoryService.getFbaTranModel());
        page.setOrderBy(orderBy);
        model.addAttribute("page", page);
        model.addAttribute("skuMap",psiProductService.findProductNameWithSku(fbaInbound.getCountry()));
        model.addAttribute("site",LogisticsSupplier.getWebSite());
        model.addAttribute("accountList",amazonAccountConfigService.findAccountByCountryNoServer(fbaInbound.getCountry()));
		return "modules/psi/fbaInboundList";
	}

	@RequiresPermissions("psi:fbaInbound:view")
	@RequestMapping(value = "sync")
	@ResponseBody
	public String sync(FbaInbound fbaInbound, Model model) {
		fbaInbound.setProessStatus("正在同步AMZ...");
		final AmazonAccountConfig config=amazonAccountConfigService.getByName(fbaInbound.getAccountName());
		final Integer id =fbaInbound.getId();
		try{
		    String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
			Client client = BaseService.getCxfClient(interfaceUrl);
			Object[] str = new Object[]{Global.getConfig("ws.key"),id};
			client.invoke("syncFbaInbound", str);
		}catch(Exception e){
		    logger.error(config.getAccountName()+"同步FBA帖错误："+e.getMessage(), e);
		}
		return fbaInbound.getProessStatus();
	}
	
	@RequiresPermissions("psi:fbaInbound:view")
	@RequestMapping(value = "upload")
	@ResponseBody
	public void upload(FbaInbound fbaInbound, Model model) {
		final AmazonAccountConfig config=amazonAccountConfigService.getByName(fbaInbound.getAccountName());
		final Integer id =fbaInbound.getId();
		if(StringUtils.isEmpty(fbaInbound.getShipmentId())){
			//2016-07-29如果运单里面有该id，更新shipmentId
			try{
			    String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
				Client client = BaseService.getCxfClient(interfaceUrl);
				Object[] str = new Object[]{Global.getConfig("ws.key"),id,"1"};
				client.invoke("createFbaInBound", str);
			}catch(Exception e){
			    logger.error(config.getAccountName()+"创建FBA帖错误："+e.getMessage(), e);
			}
		}else{
			//销售后台已建贴，同步后台的shipmentId，，如果乱填可能导致绑定错误！！
			logger.info("跟踪fba贴"+fbaInbound.getShipmentId()+"绑定运单");
			tranService.updateShipmentIdByFbaId(fbaInbound.getShipmentId(),fbaInbound.getId());
			lcTranService.updateShipmentIdByFbaId(fbaInbound.getShipmentId(),fbaInbound.getId());
			try{
				String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
				Client client = BaseService.getCxfClient(interfaceUrl);
				Object[] str = new Object[]{Global.getConfig("ws.key"),id};
				client.invoke("upLoadFbaInBound", str);
				client.invoke("syncAutoFbaInbound", str);
			}catch(Exception e){
			    logger.error(config.getAccountName()+"创建FBA帖错误："+e.getMessage(), e);
			}
		}
	}
	
	@Autowired
	private PsiSkuChangeBillService psiSkuChangeBillService; 
	
	@RequiresPermissions("psi:fbaInbound:view")
	@RequestMapping(value = "sendEmail")
	public String sendEmail(FbaInbound fbaInbound, Model model) {
		String country = fbaInbound.getCountry();
		if(country.startsWith("com")){
			country = "us";
		}
		SendEmail sendEmail = new SendEmail();
		sendEmail.setType("-1");
		Map<String,Object> params = Maps.newHashMap();
		Map<String, String> rs = psiProductService.findOneProductHasManySkus(Sets.newHashSet(fbaInbound.getCountry()),fbaInbound.getShipFromAddress());
		params.put("manySkus", rs);
		String template = "";
		try {
			params.put("fba", fbaInbound);
			params.put("country", country.toUpperCase());
			params.put("user", UserUtils.getUser());
			Set<String> skus = fbaInbound.getSkus();
			params.put("fnsku",psiProductService.getSkuAndFnskuMap(skus));
			if("RECEIVING,CLOSED".contains(fbaInbound.getShipmentStatus())){
				template = PdfUtil.getPsiTemplate("fbaInboundEmailError.ftl", params);
				String temp = "dewarehouse@inateck.com";
				if("us,ca,jp".contains(country)){
					temp="sophie@inateck.com";
				}
				sendEmail.setSendEmail(temp);
				sendEmail.setSendSubject("["+country.toUpperCase()+"] FBA problems - "+"("+fbaInbound.getShipmentId()+")");
			}else{
				String subject = " - ["+country.toUpperCase()+"] FBA replenishment"+"("+DateUtils.getDate()+")";
				sendEmail.setSendSubject("[P]"+subject);
				params.put("change",psiSkuChangeBillService.getSkuChangeNoSure(fbaInbound.getShipmentId()));
				params.put("subject",subject);
				template = PdfUtil.getPsiTemplate("fbaInboundEmail.ftl", params);
				if("DE".equals(fbaInbound.getShipFromAddress())){
					sendEmail.setSendEmail("fbamitteilung@inateck.com");
				}
				sendEmail.setCcToEmail("amazon-sales@inateck.com");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		sendEmail.setSendContent(template);
		sendEmail.setCountry(country);
		sendEmail.setAccountName(fbaInbound.getAccountName());
		model.addAttribute("sendEmail", sendEmail);
		model.addAttribute("sid", fbaInbound.getId());
		model.addAttribute("accountMap",amazonAccountConfigService.findCountryByAccount());
		//model.addAttribute("emailList",amazonAccountConfigService.findEmailByAccount(fbaInbound.getAccountName()));
		
		return "modules/custom/sendEmail";
	}
	
	
	@RequiresPermissions("psi:fbaInbound:view")
	@RequestMapping(value = "sendPlanEmail")
	public String sendPlanEmail(String fbaIds, Model model) {
		fbaIds = fbaIds.substring(0,fbaIds.length()-1);
		String[] idStrs = fbaIds.split(",");
		List<Integer> ids = Lists.newArrayList();
		for (String str : idStrs) {
			ids.add(Integer.parseInt(str));
		}
		List<FbaInbound> fbas = fbaInboundService.find(ids);
		Set<String> countrys = Sets.newHashSet();
		String shipFromAddress = "";
		Set<String> skus =Sets.newHashSet();
		Map<String,String> boxes = Maps.newHashMap();
		String accountName="";
		for (FbaInbound fbaInbound2 : fbas) {
			countrys.add(fbaInbound2.getCountry());
			shipFromAddress= fbaInbound2.getShipFromAddress();
			skus.addAll(fbaInbound2.getSkus());
			
			int rs = 0;
			Set<String> skus1 = Sets.newHashSet();
			for (FbaInboundItem item : fbaInbound2.getItems()) {
				skus1.add(item.getSku());
			}
			
			String shipmentIdOrPlanId = StringUtils.isEmpty(fbaInbound2.getShipmentId())?""+fbaInbound2.getId():fbaInbound2.getShipmentId();
			Map<String, Integer> packInTran = amazonProduct2Service.findFbaPackInTran(shipmentIdOrPlanId);
			if(packInTran.size()==0){
				packInTran = amazonProduct2Service.findProductPackBySkus(skus, fbaInbound2.getCountry());
			}
			
			for (FbaInboundItem item : fbaInbound2.getItems()) {
				Integer num =item.getPackQuantity();
				if(num==null){
					num=packInTran.get(item.getSku());
				}
				if(num!=null){
					Float pack = (float)item.getQuantityShipped()/(float)num;
					BigDecimal bd = new BigDecimal(pack);
					bd =  bd.setScale(0, BigDecimal.ROUND_UP);
					rs +=bd.intValue();
				}
			}
			boxes.put(fbaInbound2.getId()+"", rs+"");
			accountName=fbaInbound2.getAccountName();
		}
		SendEmail sendEmail = new SendEmail();
		sendEmail.setType("-1");
		Map<String,Object> params = Maps.newHashMap();
		Map<String, String> rs = psiProductService.findOneProductHasManySkus(countrys,shipFromAddress);
		params.put("manySkus", rs);
		String template = "";
		try {
			params.put("fbas", fbas);
			params.put("boxes", boxes);
			params.put("fnsku",psiProductService.getSkuAndFnskuMap(skus));
			params.put("user", UserUtils.getUser());
			String subject = countrys.toString().toUpperCase()+" Shipping Plan"+"("+DateUtils.getDate()+")";
			sendEmail.setSendSubject(subject);
			Map<String,Map<String, String>> changes = Maps.newHashMap();
			for (Integer id : ids) {
				changes.put(id+"",psiSkuChangeBillService.getSkuChangeNoSure(id));
			}
			params.put("change",changes);
			params.put("subject",subject);
			template = PdfUtil.getPsiTemplate("fbaPlanEmail.ftl", params);
			sendEmail.setSendEmail("dewarehouse@inateck.com");
			sendEmail.setCcToEmail("amazon-sales@inateck.com");
		} catch (Exception e) {
			e.printStackTrace();
		}
		sendEmail.setSendContent(template);
		
		if(StringUtils.isNotBlank(accountName)){
			//model.addAttribute("emailList",amazonAccountConfigService.findEmailByAccount(accountName));
			sendEmail.setAccountName(accountName);
		}
		model.addAttribute("sendEmail", sendEmail);
		model.addAttribute("user", UserUtils.getUser());
		model.addAttribute("sid", fbaIds);
		
		model.addAttribute("accountMap",amazonAccountConfigService.findCountryByAccount());
		return "modules/custom/sendEmail";
	}
	
	
	private static String baseDirStr;
	
	@RequiresPermissions("psi:fbaInbound:view")
	@RequestMapping(value = "genLabel")
	@ResponseBody
	public String genLabel(@RequestParam(required=false)MultipartFile dataFile,FbaInbound fbaInbound, Model model,int number ,String type) {
		String shipmentId = fbaInbound.getShipmentId();
		if(StringUtils.isBlank(shipmentId)){
			logger.info(fbaInbound.getId()+","+fbaInbound.getShipmentName()+"为空");
			return "ShipmentId is null！";
		}
		//fbaInbound.setProessStatus("gen PackageLabel...");
		//fbaInboundService.save(fbaInbound);
		AmazonAccountConfig config=amazonAccountConfigService.getByName(fbaInbound.getAccountName());
		Integer id = fbaInbound.getId();
		
		//上传箱单
		try{
			String fileName="";
	        byte[] updateFile=new byte[1];
			InputStream is = null;
			if(dataFile!=null&&dataFile.getSize()>0){
				try {
					is = dataFile.getInputStream();
					updateFile=new byte [is.available()];  
					fileName = dataFile.getOriginalFilename();
				} catch (IOException e) {}
			}
			
			String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
			Client client = BaseService.getCxfClient(interfaceUrl);
			Object[] str = new Object[]{Global.getConfig("ws.key"),id,type,number,updateFile,fileName,shipmentId};
			
			Object[] res = client.invoke("genLabel", str);
			String pdf = (String)res[0];
			if(StringUtils.isNotBlank(pdf)&&!pdf.contains("Error")){
				String country = fbaInbound.getCountry();
	    		String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") + Global.getCkBaseDir() + "fbaLabel";
	    		File dir = new File(baseDirStr+"/"+country+"/"+fbaInbound.getShipmentId());
	    		if(!dir.exists()){
	    			dir.mkdirs();
	    		}
	    		
				File pdfZip = new File(dir,fbaInbound.getShipmentId()+".zip");
    			base64Decode(pdfZip, pdf);
    			try {
    				ZipUtil.unzip(pdfZip.getAbsolutePath(),pdfZip.getParent());
    				fbaInbound.setProessStatus("gen PackageLabel Success！");
    				fbaInbound.setHasGenLabel("1");
    				fbaInboundService.save(fbaInbound);
    				return "gen PackageLabel Success！";
    			} catch (IOException e) {}
			}
		}catch(Exception e){
		    logger.error(config.getAccountName()+"genLabel 错误："+e.getMessage(), e);
		}
		
		return "稍后刷新查看结果";
	}
	
	private void base64Decode(File file, String code) {
  		byte[] data = Base64.decodeBase64(code);
  		try {
  			FileUtils.writeByteArrayToFile(file, data);
  		} catch (IOException e) {}
  	}
	
	@RequiresPermissions("psi:fbaInbound:view")
	@RequestMapping(value = "genPalletLabels")
	@ResponseBody
	public String genPalletLabels(FbaInbound fbaInbound, Model model,int number,String type) {
		String shipmentId = fbaInbound.getShipmentId();
		if(StringUtils.isBlank(shipmentId)){
			logger.info(fbaInbound.getId()+","+fbaInbound.getShipmentName()+"为空");
			return "ShipmentId is null！";
		}
		//fbaInbound.setProessStatus("gen PalletLabels...");
		//fbaInboundService.save(fbaInbound);
		AmazonAccountConfig config=amazonAccountConfigService.getByName(fbaInbound.getAccountName());
		Integer id =fbaInbound.getId();
		try{
			String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
			Client client = BaseService.getCxfClient(interfaceUrl);
			Object[] str = new Object[]{Global.getConfig("ws.key"),id,number,type,shipmentId};
			
			Object[] res = client.invoke("genPalletLabels", str);
			String pdf = (String)res[0];
			if(StringUtils.isNotBlank(pdf)&&!pdf.contains("Error")){
				String country = fbaInbound.getCountry();
		  		String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") + Global.getCkBaseDir() + "fbaLabel";
		  		File dir = new File(baseDirStr+"/"+country+"/"+fbaInbound.getShipmentId()+"/pallet");
		  		if(!dir.exists()){
		  			dir.mkdirs();
		  		}
	    		File pdfZip = new File(dir,fbaInbound.getShipmentId()+".zip");
	  			base64Decode(pdfZip, pdf);
	  			try {
	  				ZipUtil.unzip(pdfZip.getAbsolutePath(),pdfZip.getParent());
	  				fbaInbound.setProessStatus("gen PalletLabels Success！");
	  				fbaInbound.setHasGenLabel("1");
	  				fbaInboundService.save(fbaInbound);
	  				return "gen PackageLabel Success！";
	  			} catch (IOException e) {}
			}
			
			
		}catch(Exception e){
		    logger.error(config.getAccountName()+"genPalletLabels 错误："+e.getMessage(), e);
		}
		return "稍后刷新查看结果";
	}
	
	
	@RequiresPermissions("psi:fbaInbound:view")
	@RequestMapping(value = "genUniqueLabel")
	@ResponseBody
	public String genUniqueLabel(@RequestParam(required=false)MultipartFile uploadFile,FbaInbound fbaInbound, Model model,String type) {
		String shipmentId = fbaInbound.getShipmentId();
		if(StringUtils.isBlank(shipmentId)){
			logger.info(fbaInbound.getId()+","+fbaInbound.getShipmentName()+"为空");
			return "ShipmentId is null！";
		}
	//	fbaInbound.setProessStatus("gen UniquePackageLabel...Wait 1-2 minutes");
	//	fbaInboundService.save(fbaInbound);
		AmazonAccountConfig config=amazonAccountConfigService.getByName(fbaInbound.getAccountName());
		Integer id = fbaInbound.getId();
		//上传箱单
		try{
			String fileName="";
	        byte[] updateFile=new byte[1];
			InputStream is = null;
			if(uploadFile!=null&&uploadFile.getSize()>0){
				try {
					is = uploadFile.getInputStream();
					updateFile=new byte [is.available()];  
					fileName = uploadFile.getOriginalFilename();
				} catch (IOException e) {}
			}
			
			String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
			Client client = BaseService.getCxfClient(interfaceUrl);
			Object[] str = new Object[]{Global.getConfig("ws.key"),id,type,updateFile,fileName,shipmentId};
			
			Object[] res = client.invoke("genUniqueLabel", str);
			String pdf = (String)res[0];
			if(StringUtils.isNotBlank(pdf)&&!pdf.contains("Error")){
				String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") + Global.getCkBaseDir() + "fbaLabel";
	    		File dir = new File(baseDirStr+"/"+fbaInbound.getCountry()+"/"+fbaInbound.getShipmentId());
	    		if(!dir.exists()){
	    			dir.mkdirs();
	    		}
	    		File pdfZip = new File(dir,fbaInbound.getShipmentId()+".zip");
    			base64Decode(pdfZip, pdf);
    			try {
    				ZipUtil.unzip(pdfZip.getAbsolutePath(),pdfZip.getParent());
    				fbaInbound.setProessStatus("gen Unique PackageLabel Success！");
    				fbaInbound.setHasGenLabel("1");
    				fbaInboundService.save(fbaInbound);
    				return "gen Unique PackageLabel Success！";
    			} catch (IOException e) {}
			}
			
			
		}catch(Exception e){
		    logger.error(config.getAccountName()+"genUniqueLabel 错误："+e.getMessage(), e);
		}
		return "稍后刷新查看结果";
	}
	
	@RequiresPermissions("psi:fbaInbound:view")
	@RequestMapping(value = "export/xml")
	public void xml(FbaInbound fbaInbound,HttpServletRequest request, HttpServletResponse response) {
		if(baseDirStr==null){
			baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") + Global.getCkBaseDir() + "fbaLabel";
		}
		File xml = new File(baseDirStr+"/"+fbaInbound.getCountry()+"/"+fbaInbound.getShipmentId()+"/data.xml");
		response.addHeader("content-type", "application/x-msdownload;");
		response.addHeader("Content-Disposition", "inline; filename="+fbaInbound.getShipmentId()+".xml");
		response.addHeader("content-length", xml.length()+"");
		try {
			response.getWriter().write(FileUtils.readFileToString(xml, "utf-8"));
		} catch (IOException e) {} 
	}
	
	
	@RequiresPermissions("psi:fbaInbound:view")
	@RequestMapping(value = "export/diy")
	public void xmlByDiy(FbaInbound fbaInbound,HttpServletRequest request, HttpServletResponse response) {
		if(baseDirStr==null){
			baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/") + Global.getCkBaseDir() + "fbaLabel";
		}
		File xml = new File(baseDirStr+"/"+fbaInbound.getCountry()+"/"+fbaInbound.getShipmentId()+"/diyData.xml");
		response.addHeader("content-type", "application/x-msdownload;");
		response.addHeader("Content-Disposition", "inline; filename="+fbaInbound.getShipmentId()+"_diy.xml");
		response.addHeader("content-length", xml.length()+"");
		try {
			response.getWriter().write(FileUtils.readFileToString(xml, "utf-8"));
		} catch (IOException e) {} 
	}
	
	
	@RequiresPermissions("psi:fbaInbound:view")
	@RequestMapping(value = "form")
	public String form(FbaInbound fbaInbound, Model model) {
		//装箱数和可用库存
		Set<String> skus = Sets.newHashSet();
		for (FbaInboundItem item : fbaInbound.getItems()) {
			skus.add(item.getSku());
		}
		if(skus.size()>0){
			String stockCode = fbaInbound.getShipFromAddress();
			
			
			String shipmentIdOrPlanId = StringUtils.isEmpty(fbaInbound.getShipmentId())?""+fbaInbound.getId():fbaInbound.getShipmentId();
			Map<String, Integer> packInTran = amazonProduct2Service.findFbaPackInTran(shipmentIdOrPlanId);
			if(packInTran.size()==0){
				packInTran = amazonProduct2Service.findProductPackBySkus(skus, fbaInbound.getCountry());
			}
			model.addAttribute("packs",packInTran);
			
			Map<String, Integer> inStock = psiInventoryService.getInventorySkuMap(stockCode,skus);
			if(inStock.size()>0){
				Map<String, Integer> temp = psiInventoryService.getFbaWorkingMap(stockCode, skus, fbaInbound.getId());
				for (Map.Entry<String, Integer> entry : inStock.entrySet()) {
					String sku = entry.getKey();
					 Integer number = temp.get(sku);
					if(number!=null){
						inStock.put(sku,entry.getValue()-number);
					}
				}
				model.addAttribute("inStock",inStock);
			}
		}
		model.addAttribute("skus",psiProductService.findSkusByFbaUpdate(fbaInbound.getCountry()));
		model.addAttribute("fbaInbound", fbaInbound);
		return "modules/psi/fbaInboundForm";
	}

	@RequiresPermissions("psi:fbaInbound:view")
	@RequestMapping(value = "save")
	public String save(final FbaInbound fbaInbound, Model model, RedirectAttributes redirectAttributes) {
		Map<String,FbaInboundItem> map = Maps.newHashMap();
		List<FbaInboundItem> olds = Lists.newArrayList();
		for (Iterator<FbaInboundItem> iterator = fbaInbound.getItems().iterator(); iterator.hasNext();) {
			FbaInboundItem item = iterator.next();
			if(fbaInbound.getId()!=null&&StringUtils.isNotEmpty(fbaInbound.getShipmentStatus())){
				if(StringUtils.isEmpty(item.getPageDelFlag())){
					if(StringUtils.isEmpty(fbaInbound.getShipmentId())){
						iterator.remove();
					}else{
						item.setQuantityShipped(0);
						map.put(item.getSku(), item);
					}
				}else{
					String sku = item.getSku();
					if(StringUtils.isNotEmpty(fbaInbound.getShipmentId())){
						if(item.getPageDelFlag().equals(item.getSku())){
							item.setFbaInbound(fbaInbound);
						} else if("0".equals(item.getPageDelFlag())){
							if(map.containsKey(sku)){
								map.get(sku).setQuantityShipped(item.getQuantityShipped());
								iterator.remove();
							}else{
								item.setFbaInbound(fbaInbound);
								map.put(item.getSku(), item);
							}
						}else{
							if(!map.containsKey(item.getPageDelFlag())){
								FbaInboundItem oldItem = new FbaInboundItem();
								oldItem.setQuantityShipped(0);
								oldItem.setSku(item.getPageDelFlag());
								oldItem.setFbaInbound(fbaInbound);
								olds.add(oldItem);
								map.put(oldItem.getSku(), oldItem);
							}
							if(map.containsKey(sku)){
								map.get(sku).setQuantityShipped(item.getQuantityShipped());
								iterator.remove();
							}else{
								item.setFbaInbound(fbaInbound);
								map.put(item.getSku(), item);
							}
						}
						
					}else{
						item.setFbaInbound(fbaInbound);
					}
				}
			}else if (fbaInbound.getId()!=null){
				if(StringUtils.isEmpty(item.getPageDelFlag())){
					iterator.remove();
				}else{
					item.setFbaInbound(fbaInbound);
				}
			}else{
				item.setFbaInbound(fbaInbound);
			}
		}
		if(olds.size()>0){
			fbaInbound.getItems().addAll(olds);
		}
		if(StringUtils.isNotEmpty(fbaInbound.getShipmentId())){
			fbaInbound.setProessStatus("正在上传到亚马逊...稍后刷新查看结果");
		}
		fbaInboundService.savePlan(fbaInbound);
		
		final AmazonAccountConfig config=amazonAccountConfigService.getByName(fbaInbound.getAccountName());
		final Integer id =fbaInbound.getId();
		
		if(StringUtils.isNotEmpty(fbaInbound.getShipmentId())){
			
			
					try{
						String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
						Client client = BaseService.getCxfClient(interfaceUrl);
						Object[] str = new Object[]{Global.getConfig("ws.key"),id};
						Object[] res = client.invoke("upLoadFbaInBound", str);
						String rs = (String)res[0];
						if(!"Upload To Amz Successful".equals(rs)){
							 logger.info(config.getAccountName()+"修改失败："+fbaInbound.getProessStatus());
						}else{
							client.invoke("syncAutoFbaInbound", str);
						}
					}catch(Exception e){
					    logger.error(config.getAccountName()+"创建FBA帖错误："+e.getMessage(), e);
					}
			
			addMessage(redirectAttributes, "正在上传到亚马逊...稍后刷新查看结果!");
		}else if(StringUtils.isNotEmpty(fbaInbound.getShipmentStatus())){
			//新建帖子
			
					try{
					    String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
						Client client = BaseService.getCxfClient(interfaceUrl);
						Object[] str = new Object[]{Global.getConfig("ws.key"),id,"2"};
						client.invoke("createFbaInBound", str);
					}catch(Exception e){
					    logger.error(config.getAccountName()+"创建FBA帖错误："+e.getMessage(), e);
					}
			
		}
		
		if((StringUtils.isEmpty(fbaInbound.getShipmentStatus())||"WORKING".equals(fbaInbound.getShipmentStatus()))&&"CN".equals(fbaInbound.getShipFromAddress())){
			//同步运单
			fbaInboundService.asyTransportFba(fbaInbound);
		}
		return "redirect:"+Global.getAdminPath()+"/psi/fbaInbound/?country="+fbaInbound.getCountry();
	}

	@RequiresPermissions("psi:fbaInbound:response")
	@RequestMapping(value = "response")
	public String response(FbaInbound fbaInbound, Model model, RedirectAttributes redirectAttributes) {
		fbaInbound.setResponseTime(new Date());
		fbaInbound.setResponseUser(UserUtils.getUser());
		fbaInboundService.save(fbaInbound);
		addMessage(redirectAttributes, "Successful operation(NO. "+fbaInbound.getId()+")");
		return "redirect:"+Global.getAdminPath()+"/psi/fbaInbound/?country="+fbaInbound.getCountry();
	}
	
	@RequiresPermissions("psi:fbaInbound:view")
	@RequestMapping(value = "cancel")
	public String cancel(final FbaInbound fbaInbound, Model model, RedirectAttributes redirectAttributes) {
		if(StringUtils.isNotEmpty(fbaInbound.getShipmentId())){
			final AmazonAccountConfig config=amazonAccountConfigService.getByName(fbaInbound.getAccountName());
			final Integer id =fbaInbound.getId();
		
					String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
					Client client = BaseService.getCxfClient(interfaceUrl);
					Object[] str = new Object[]{Global.getConfig("ws.key"),id};
					try {
						Object[] res = client.invoke("cancelFbaInBoundState", str);
						String rs = (String)res[0];
						if(!"Upload To Amz Successful".equals(rs)){
							client.invoke("syncAutoFbaInbound", str);
						}
					} catch (Exception e) {
						 logger.error(config.getAccountName()+"取消FBA帖错误："+e.getMessage(), e);
					}
			
			addMessage(redirectAttributes, "正在上传到亚马逊...稍后刷新查看结果!");
		}else{  
			fbaInbound.setShipmentStatus("DELETED");
	 		fbaInbound.setLastUpdateDate(new Date());
	 		fbaInboundService.save(fbaInbound);
			addMessage(redirectAttributes, "取消帖子成功！！");
		}
		tranService.cancelTransportBading(fbaInbound.getId(),fbaInbound.getShipmentId());
		
		return "redirect:"+Global.getAdminPath()+"/psi/fbaInbound/?country="+fbaInbound.getCountry();
	}
	
	@Autowired
	private StockService stockService;
	
	@RequiresPermissions("psi:fbaInbound:view")
	@RequestMapping(value = "export/txt")
	public void txt(FbaInbound fbaInbound,HttpServletRequest request, HttpServletResponse response) {
		AmazonAccountConfig config=amazonAccountConfigService.getByName(fbaInbound.getAccountName());
		
		Stock stockLocal = stockService.findByCountryCode(fbaInbound.getDocAddress(),fbaInbound.getCountry(),config).get(0);
		String txt = "PlanName\t"+fbaInbound.getShipmentName()+"\r\n";
			   txt+="ShipToCountry\t"+config.getCountryCode()+"\r\n";	
			   txt+="AddressName\t"+HtmlUtils.htmlUnescape(stockLocal.getName())+"\r\n";	
			   txt+="AddressFieldOne\t"+HtmlUtils.htmlUnescape(stockLocal.getAddressLine1())+"\r\n";	
			   txt+="AddressFieldTwo\t\r\n";	
			   txt+="AddressCity\t"+stockLocal.getCity()+"\r\n";	
			   txt+="AddressCountryCode\t"+fbaInbound.getDocAddress()+"\r\n";
			   txt+="AddressStateOrRegion\t"+stockLocal.getStateorprovincecode()+"\r\n";	
			   txt+="AddressPostalCode\t"+stockLocal.getPostalcode()+"\r\n";	
			   txt+="AddressDistrict\t\r\n\r\n";	
			   txt+="MerchantSKU\tQuantity\r\n";
			   StringBuilder str = new StringBuilder();
			   for (FbaInboundItem item : fbaInbound.getItemsByOrder()) {
				   str.append(item.getSku()).append("\t").append(item.getQuantityShipped()).append("\r\n");
			   }
			   txt+=str.toString();
		response.addHeader("content-type", "application/x-msdownload;");
		response.addHeader("Content-Disposition", "inline; filename=" + response.encodeURL(fbaInbound.getShipmentName()+".txt"));
		response.addHeader("content-length", txt.length()+"");
		try {
			response.getOutputStream().write(txt.getBytes());
		} catch (IOException e) {} 
	}
	
	
	@RequiresPermissions("psi:fbaInbound:view")
	@RequestMapping(value = "update")
	@ResponseBody
	public String update(FbaInbound fbaInbound,String deliveryDate1,String toDhlDate1) {
		if(StringUtils.isNotEmpty(deliveryDate1)){
			fbaInbound.setDeliveryDate(new Date(deliveryDate1));
		}
		if(StringUtils.isNotEmpty(toDhlDate1)){
			fbaInbound.setToDhl(new Date(toDhlDate1));
		}
//		fbaInbound.setQuantity1(fbaInbound.getQuantity3()==null?0:fbaInbound.getQuantity3());
//		fbaInbound.setQuantity2(fbaInbound.getQuantity4()==null?0:fbaInbound.getQuantity4());
//		if("DPD".equals(fbaInbound.getSupplier())&&"de".equals(fbaInbound.getCountry())){
//			fbaInbound.setFee(fbaInbound.getQuantity1()*3.38f+fbaInbound.getQuantity2()*3.78f);// 30kg  15kg 3.69
//		}
//		if("DPD".equals(fbaInbound.getSupplier())&&fbaInbound.getTray()!=null){
//			if("fr".equals(fbaInbound.getCountry())){
//				fbaInbound.setFee(fbaInbound.getQuantity1()*9.09f);
//			}else if("uk".equals(fbaInbound.getCountry())){
//				fbaInbound.setFee(fbaInbound.getQuantity1()*9.09f);
//			}else if("it".equals(fbaInbound.getCountry())){
//				fbaInbound.setFee(fbaInbound.getQuantity1()*10.1f);
//			}else if("es".equals(fbaInbound.getCountry())){
//				fbaInbound.setFee(fbaInbound.getQuantity1()*13.13f);
//			}
//		}
		fbaInboundService.save(fbaInbound);
		return "1";
	}
	
	
	
	@RequiresPermissions("psi:fbaInbound:view")
	@RequestMapping(value = "notAuto")
	public String notAuto(FbaInbound fbaInbound, RedirectAttributes redirectAttributes) {
		fbaInboundService.save(fbaInbound);
		final AmazonAccountConfig config=amazonAccountConfigService.getByName(fbaInbound.getAccountName());
		final Integer id =fbaInbound.getId();
		
	
				try{
				    String interfaceUrl = BaseService.AMAZONAPI_WEBPATH.replace("host", config.getServerIp()+":8080");
					Client client = BaseService.getCxfClient(interfaceUrl);
					Object[] str = new Object[]{Global.getConfig("ws.key"),id};
					client.invoke("notAuto", str);
				}catch(Exception e){
				    logger.error(config.getAccountName()+" notAuto 错误："+e.getMessage(), e);
				}
	
	    addMessage(redirectAttributes,"正在上传到亚马逊...稍后刷新查看结果");
		return "redirect:"+Global.getAdminPath()+"/psi/fbaInbound/?country="+fbaInbound.getCountry();
	}
	
	@ResponseBody
	@RequestMapping(value ="getShipddQuantityAvailableBySku")
	public String getQuantityAvailableBySku(String sku,String stockCode,String country,Integer inboundId){
		Integer num = psiInventoryService.getProductFbaWorking(sku,stockCode,inboundId);
		Integer quantity = psiInventoryService.getInventoryBySku(sku,stockCode);
		
		Integer pack = amazonProduct2Service.findProductPackBySku(sku);
		
		Map<String,Integer> rs = Maps.newHashMap();
		rs.put("canUsed", quantity-num);
		rs.put("pack", pack);
		return JSON.toJSONString(rs);
	}
	
	@ResponseBody
	@RequestMapping(value ="getPacks")
	public String getPacks(FbaInbound fbaInbound){
		int rs = 0;
		Set<String> skus = Sets.newHashSet();
		for (FbaInboundItem item : fbaInbound.getItems()) {
			skus.add(item.getSku());
		}
		String shipmentIdOrPlanId = StringUtils.isEmpty(fbaInbound.getShipmentId())?""+fbaInbound.getId():fbaInbound.getShipmentId();
		Map<String, Integer> packInTran = amazonProduct2Service.findFbaPackInTran(shipmentIdOrPlanId);
		if(packInTran.size()==0){
			packInTran = amazonProduct2Service.findProductPackBySkus(skus, fbaInbound.getCountry());
		}
		
//		Map<String,String> proMap = this.psiInventoryService.findProductInfosBySku(skus);
		//从中国仓获取产品信息
		for (FbaInboundItem item : fbaInbound.getItems()) {
			Integer num =item.getPackQuantity();
			if(num==null){
				num=packInTran.get(item.getSku());
			}
			if(num!=null){
				Float pack = (float)item.getQuantityShipped()/(float)num;
				BigDecimal bd = new BigDecimal(pack);
				bd =  bd.setScale(0, BigDecimal.ROUND_UP);
				rs +=bd.intValue();
			}
		}
		return rs+"";
	}
	
	@RequestMapping(value = "export")
	public void export(FbaInbound fbaInbound, HttpServletRequest request, HttpServletResponse response, Model model) {
		
		 Map<String,List<Object[]>> psiList = fbaInboundService.exportFbaInbound(fbaInbound); 
		 psiList.remove("CN");
		
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
		HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		for (Map.Entry<String, List<Object[]>> entry : psiList.entrySet()) {
			String country = entry.getKey();
			HSSFSheet sheet1 = wb.createSheet(country);
			HSSFRow row1 = sheet1.createRow(0);
			List<Object[]> data = psiList.get(country);
			String[] title = { "Country", "ShipmentId", "ShipmentName","Gross Weight(KG)","Gross Weight(KG) By Ada", "Volume",
					"Total Fee","ERP Shipped Date","To Amz Date","Pick Up Date","Average Fee"};
			row1.setHeight((short) 600);
			HSSFCell cell2 = null;						
			for (int j = 0; j < title.length; j++) {
				cell2 = row1.createCell(j);
				cell2.setCellValue(title[j]);
				cell2.setCellStyle(style);
			}
			float totalWeigth = 0f;
			float totalfee = 0f;
			for (int j =0;j<data.size();j++ ) {
				Object[] objs = data.get(j);
				row1 = sheet1.createRow(j + 1);
				Float weight = 0f;
				if(objs[3]!=null){
					weight = ((BigDecimal)objs[3]).floatValue();
				}
				Float fee = 0f;
				if(objs[4]!=null){
					fee =  ((BigDecimal)objs[4]).floatValue();
				}
				totalWeigth +=weight;
				totalfee +=fee;
				
				row1.createCell((short) 0).setCellValue(country); 
				row1.createCell((short) 1).setCellValue(objs[1]==null?"":objs[1].toString()); 
				row1.createCell((short) 2).setCellValue(objs[2]==null?"":objs[2].toString()); 
				HSSFCell cell = row1.createCell((short) 3);
				cell.setCellValue(weight); 
				cell.setCellStyle(cellStyle);
				row1.createCell((short) 4).setCellValue(objs[9]==null?"":objs[9].toString()); 
				row1.createCell((short) 5).setCellValue(objs[8]==null?"":objs[8].toString()); 
				cell = row1.createCell((short) 6);; 
				cell.setCellValue(fee); 
				cell.setCellStyle(cellStyle);
				row1.createCell((short) 7).setCellValue(objs[5]==null?"":DateUtils.getDate((Date)objs[5],"yyyy/M/dd")); 
				row1.createCell((short) 8).setCellValue(objs[6]==null?"":DateUtils.getDate((Date)objs[6],"yyyy/M/dd")); 
				row1.createCell((short) 9).setCellValue(objs[7]==null?"":DateUtils.getDate((Date)objs[7],"yyyy/M/dd")); 
				cell = row1.createCell((short) 10);
				cell.setCellValue(weight>0?(fee/weight):0f); 
				cell.setCellStyle(cellStyle);
			} 
			row1 = sheet1.createRow(data.size()+ 1);
			row1.createCell((short) 2).setCellValue("Total:"); 
			HSSFCell cell = row1.createCell((short) 3);
			cell.setCellValue(totalWeigth); 
			cell.setCellStyle(cellStyle);
			cell = row1.createCell((short) 6);; 
			cell.setCellValue(totalfee); 
			cell.setCellStyle(cellStyle);
			cell = row1.createCell((short) 10);
			cell.setCellValue(totalWeigth>0?(totalfee/totalWeigth):0f); 
			cell.setCellStyle(cellStyle);
			// 自动调节列宽
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
			sheet1.autoSizeColumn((short) 10);
		 }
		try {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
	
			String fileName = "FBA贴汇总导出.xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename="
					+ fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//导出明细,sku按amazon规则排序
	@RequestMapping(value = "exportDetail")
	public String exportDetail(FbaInbound fbaInbound, HttpServletRequest request,HttpServletResponse response, Model model) {
	    HSSFWorkbook wb = new HSSFWorkbook();
		HSSFCellStyle style = wb.createCellStyle();
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

		style.setFillBackgroundColor(HSSFColor.WHITE.index);
		style.setFillForegroundColor(HSSFColor.WHITE.index);
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
		font.setFontHeightInPoints((short) 12); // 字体高度
		font.setFontName(" 黑体 "); // 字体
		font.setBoldweight((short) 12);
		style.setFont(font);
		
		HSSFCellStyle style1 = wb.createCellStyle();
		style1.setFont(font);

		CellStyle contentStyle1 = wb.createCellStyle();
		contentStyle1.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setBottomBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setLeftBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderRight(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setRightBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setBorderTop(HSSFCellStyle.BORDER_THIN);
		contentStyle1.setTopBorderColor(HSSFColor.BLACK.index);
		contentStyle1.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		contentStyle1.setFillPattern(CellStyle.SOLID_FOREGROUND);
		contentStyle1.setWrapText(true);
		
		//两位小数显示
		HSSFCellStyle decimalStyle = wb.createCellStyle();
		decimalStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
		decimalStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		decimalStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		decimalStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setRightBorderColor(HSSFColor.BLACK.index);
		decimalStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		decimalStyle.setTopBorderColor(HSSFColor.BLACK.index);
		
		//高亮显示
		HSSFCellStyle colorStyle = wb.createCellStyle();
		colorStyle.setFillBackgroundColor(HSSFColor.YELLOW.index);
		colorStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		colorStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		//colorStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		colorStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直
		colorStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
		colorStyle.setBottomBorderColor(HSSFColor.BLACK.index);
		colorStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
		colorStyle.setLeftBorderColor(HSSFColor.BLACK.index);
		colorStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
		colorStyle.setRightBorderColor(HSSFColor.BLACK.index);
		colorStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
		colorStyle.setTopBorderColor(HSSFColor.BLACK.index);
		HSSFCell cell = null;

		List<FbaInboundItem> items = fbaInbound.getItemsByOrder();
		Map<String,String> skuMap = psiProductService.findProductNameWithSku(fbaInbound.getCountry());
		List<PsiProduct> list = psiProductService.find();
		Map<String, PsiProduct> productMap = Maps.newHashMap();
		for (PsiProduct psiProduct : list) {
			productMap.put(psiProduct.getBrand() + " " + psiProduct.getModel(), psiProduct);
		}
		
		Set<String> multipleSku = Sets.newHashSet();
		Map<String, String> rs = psiProductService.findOneProductHasManySkus(Sets.newHashSet(fbaInbound.getCountry()),fbaInbound.getShipFromAddress());
		for (Map.Entry<String, String>entry : rs.entrySet()) {
			String key = entry.getKey();
			String skus = rs.get(key);
			String[] strings = skus.split(",");
			for (String string : strings) {
				multipleSku.add(string);
			}
		}

		HSSFSheet sheet = wb.createSheet();
		int rowIndex = 0;
		//第一行
		List<String> rowTitle = Lists.newArrayList("Shipment ID", "Name", "Amazon FC", "Number of SKUs", "porducts combined");
		for (int i = 0; i < rowTitle.size(); i++) {
			HSSFRow row = sheet.createRow(rowIndex++);
			cell = row.createCell(0);
			cell.setCellValue(rowTitle.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) 0);
			cell = row.createCell(1);
			if (i == 0) {
				cell.setCellValue(fbaInbound.getShipmentId());
			} else if (i == 1) {
				cell.setCellValue(fbaInbound.getShipmentName());
			} else if (i == 2) {
				cell.setCellValue(fbaInbound.getDestinationFulfillmentCenterId());
			} else if (i == 3) {
				cell.setCellValue(fbaInbound.getSkus().size());
			} else if (i == 4) {
				cell.setCellValue(fbaInbound.getQuantityShipped());
			}
			cell.setCellStyle(style);
			cell = row.createCell(2);
			cell.setCellStyle(style);
			cell = row.createCell(3);
			cell.setCellStyle(style);
		}
		for (int i = 0; i < rowTitle.size(); i++) {
			sheet.addMergedRegion(new CellRangeAddress(i, i, 1, 3));
		}
		rowIndex += 2;
		List<String> title = null;
		if("de,fr,uk,es,it".contains(fbaInbound.getCountry())){
			title=Lists.newArrayList("SKU", "FNSKU", "Units/package", "relabeled", "shiped units", "Packages", "Weight(kg)", "Volume(m³)");
		}else{
			title=Lists.newArrayList("ProductName","SKU", "FNSKU", "Units/package", "relabeled", "shiped units", "Packages","Length","Width","Height","单箱尺寸","单箱规格","两箱尺寸","两箱规格", "Weight(kg)", "Volume(m³)");
		}
		if (fbaInbound.getShippedDate() == null) {
			title.add("In Stock");
		}
		title.add("Remark");
		
		HSSFRow row = sheet.createRow(rowIndex++);
		row.setHeight((short) 300);
		for (int i = 0; i < title.size(); i++) {
			cell = row.createCell(i);
			cell.setCellValue(title.get(i));
			cell.setCellStyle(style);
			sheet.autoSizeColumn((short) i);
		}
		Integer warehouseId = 130; //中国仓
		if ("DE".equals(fbaInbound.getShipFromAddress())) {
			warehouseId = 19; //德国仓
		} else if ("JP".equals(fbaInbound.getShipFromAddress())) {
			warehouseId = 147; //日本仓
		} else if ("US".equals(fbaInbound.getShipFromAddress())) {
			warehouseId = 120; //美国仓
		}
		//新品未贴码
        List<String> newProducts=this.psiInventoryService.getNoSkus(warehouseId);
		Map<String, Integer> skuQty = psiInventoryService.findSkuNewQuantity(warehouseId);
		Map<String, Map<String, Float>> skuTranInfo = fbaInboundService.findSkuTranInfo();
		Map<String,String> change = psiSkuChangeBillService.getSkuChangeNoSure(fbaInbound.getShipmentId());
		int totalQuantity = 0;
		double totalBox = 0;
		float totalWeight = 0;
		float totalVolume = 0;
		
		if(!"de,fr,uk,es,it".contains(fbaInbound.getCountry())){
			for (int i = 0; i < items.size(); i++) {
				FbaInboundItem item = items.get(i);
				int j = 0;
				row = sheet.createRow(rowIndex++);
				row.setHeight((short) 300);
				//productName
				PsiProduct  product = productMap.get(skuMap.get(item.getSku()).split("_")[0]);
				String proName=skuMap.get(item.getSku());
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(proName);
				row.getCell(j-1).setCellStyle(contentStyle1);
				//SKU
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(item.getSku());
				if (multipleSku.contains(item.getSku())) {	//多sku对应同一产品
					row.getCell(j-1).setCellStyle(colorStyle);
				} else {
					row.getCell(j-1).setCellStyle(contentStyle1);
				}
				//FNSKU
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(item.getFnSku());
				row.getCell(j-1).setCellStyle(contentStyle1);
				int quantity = item.getQuantityShipped()==null?0:item.getQuantityShipped();
				totalQuantity += quantity;
				//Units/package
				Integer pack=item.getPackQuantity();
				try {
					if(pack==null){
						pack = product.getPackQuantity();
					}
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(pack);
				} catch (Exception e) {
					row.createCell(j, Cell.CELL_TYPE_STRING).setCellValue("");
				}
				row.getCell(j-1).setCellStyle(contentStyle1);
				//relabeled
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(change.get(item.getSku()));
				row.getCell(j-1).setCellStyle(contentStyle1);
				//shiped units
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(quantity);
				row.getCell(j-1).setCellStyle(contentStyle1);
				//Packages
				try {
					double box = (double)item.getQuantityShipped()/(pack);
					totalBox += box;
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(box);
				} catch (Exception e) {
					row.createCell(j, Cell.CELL_TYPE_STRING).setCellValue("");
				}
				row.getCell(j-1).setCellStyle(contentStyle1);
				
				//一箱和
				HSSFCellStyle cellStyle = wb.createCellStyle();
	            cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00"));
	            
				//长宽高
	            HSSFCell cell1=row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
	            cell1.setCellValue(product.getPackLength().floatValue());
	            cell1.setCellStyle(cellStyle);
	            
	            cell1=row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
	            cell1.setCellValue(product.getPackWidth().floatValue());
	            cell1.setCellStyle(cellStyle);
	            
	            cell1=row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
	            cell1.setCellValue(product.getPackHeight().floatValue());
	            cell1.setCellStyle(cellStyle);
	            
				BigDecimal oneBox = product.getPackLength().add(product.getPackWidth()).add(product.getPackHeight());
				BigDecimal twoBox = product.getPackLength().add(product.getPackWidth()).add(product.getPackHeight()).add(product.getMinPackSize());
	            
	            cell1=row.createCell(j++,Cell.CELL_TYPE_NUMERIC);
	            cell1.setCellValue(oneBox.floatValue());
	            cell1.setCellStyle(cellStyle);
	            
				//规格1
				String oneRule =this.getRule(oneBox);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(oneRule);
				row.getCell(j-1).setCellStyle(contentStyle1);
				
				//两箱和
	            HSSFCell cell2=row.createCell(j++,Cell.CELL_TYPE_NUMERIC);  
	            cell2.setCellValue(twoBox.floatValue());
	            cell2.setCellStyle(cellStyle);
	            
				//规格2
				String twoRule =this.getRule(twoBox);
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(twoRule);
				row.getCell(j-1).setCellStyle(contentStyle1);
	            
				if (skuTranInfo.get(item.getSku()) != null) {
					Map<String, Float> info = skuTranInfo.get(item.getSku());
					float weight = info.get("gw")*item.getQuantityShipped()/pack;
					totalWeight += weight;
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(weight);
					row.getCell(j-1).setCellStyle(decimalStyle);
					float volume = info.get("volume")*item.getQuantityShipped()/pack;
					totalVolume += volume;
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(volume);
					row.getCell(j-1).setCellStyle(decimalStyle);
				} else {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(j-1).setCellStyle(contentStyle1);
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(j-1).setCellStyle(contentStyle1);
				}
				if (fbaInbound.getShippedDate() == null) {
					if (skuQty.get(item.getSku()) != null && skuQty.get(item.getSku()) >= item.getQuantityShipped()) {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("yes");
						row.getCell(j-1).setCellStyle(contentStyle1);
					} else {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
						row.getCell(j-1).setCellStyle(contentStyle1);
					}
				}
				if (newProducts.contains(item.getSku())) {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("Attention：New product,pls help put SKU label");
					row.getCell(j-1).setCellStyle(contentStyle1);
				} else {
					List<String> skuList = fbaInboundService.getOtherSkus(proName, warehouseId, item.getSku());
					if (skuList!= null && skuList.size() > 0) {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("Attention：The item has more than one sku" + skuList.toString());
						row.getCell(j-1).setCellStyle(contentStyle1);
					} else {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
						row.getCell(j-1).setCellStyle(contentStyle1);
					}
				}
			}
		}else{
			for (int i = 0; i < items.size(); i++) {
				FbaInboundItem item = items.get(i);
				int j = 0;
				row = sheet.createRow(rowIndex++);
				row.setHeight((short) 300);
				//SKU
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(item.getSku());
				if (multipleSku.contains(item.getSku())) {	//多sku对应同一产品
					row.getCell(j-1).setCellStyle(colorStyle);
				} else {
					row.getCell(j-1).setCellStyle(contentStyle1);
				}
				//FNSKU
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(item.getFnSku());
				row.getCell(j-1).setCellStyle(contentStyle1);
				int quantity = item.getQuantityShipped()==null?0:item.getQuantityShipped();
				totalQuantity += quantity;
				//Units/package
				Integer pack=1;
				try {
					PsiProduct  product = productMap.get(skuMap.get(item.getSku()).split("_")[0]);
					pack =item.getPackQuantity();
					if(pack==null){
						pack =product.getPackQuantity();
					}
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(pack);
					
				} catch (Exception e) {
					row.createCell(j, Cell.CELL_TYPE_STRING).setCellValue("");
				}
				row.getCell(j-1).setCellStyle(contentStyle1);
				//relabeled
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(change.get(item.getSku()));
				row.getCell(j-1).setCellStyle(contentStyle1);
				//shiped units
				row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(quantity);
				row.getCell(j-1).setCellStyle(contentStyle1);
				//Packages
				try {
					double box = (double)item.getQuantityShipped()/(pack);
					totalBox += box;
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(box);
				} catch (Exception e) {
					row.createCell(j, Cell.CELL_TYPE_STRING).setCellValue("");
				}
				row.getCell(j-1).setCellStyle(contentStyle1);
	            
				if (skuTranInfo.get(item.getSku()) != null) {
					Map<String, Float> info = skuTranInfo.get(item.getSku());
					float weight = info.get("gw")*item.getQuantityShipped()/pack;
					totalWeight += weight;
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(weight);
					row.getCell(j-1).setCellStyle(decimalStyle);
					float volume = info.get("volume")*item.getQuantityShipped()/pack;
					totalVolume += volume;
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue(volume);
					row.getCell(j-1).setCellStyle(decimalStyle);
				} else {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(j-1).setCellStyle(contentStyle1);
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
					row.getCell(j-1).setCellStyle(contentStyle1);
				}
				if (fbaInbound.getShippedDate() == null) {
					if (skuQty.get(item.getSku()) != null && skuQty.get(item.getSku()) >= item.getQuantityShipped()) {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("yes");
						row.getCell(j-1).setCellStyle(contentStyle1);
					} else {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
						row.getCell(j-1).setCellStyle(contentStyle1);
					}
				}
				if (newProducts.contains(item.getSku())) {
					row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("Attention：New product,pls help put SKU label");
					row.getCell(j-1).setCellStyle(contentStyle1);
				} else {
					String proName = skuMap.get(item.getSku());
					List<String> skuList = fbaInboundService.getOtherSkus(proName, warehouseId, item.getSku());
					if (skuList!= null && skuList.size() > 0) {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("Attention：The item has more than one sku" + skuList.toString());
						row.getCell(j-1).setCellStyle(contentStyle1);
					} else {
						row.createCell(j++, Cell.CELL_TYPE_STRING).setCellValue("");
						row.getCell(j-1).setCellStyle(contentStyle1);
					}
				}
			}
		}
		int start = rowIndex;
		int h =0;
		row = sheet.createRow(rowIndex++);
		row.setHeight((short) 300);
		row.createCell(h++).setCellValue("");
		row.createCell(h++).setCellValue("combined:");
		row.createCell(h++).setCellValue("");
		row.createCell(h++).setCellValue("");
		if(!"de,fr,uk,es,it".contains(fbaInbound.getCountry())){
			row.createCell(h++).setCellValue("");
		}
		row.createCell(h++).setCellValue(totalQuantity);
		row.createCell(h++).setCellValue(totalBox);
		for (int i = start; i < rowIndex; i++) {
			for (int j = 0; j < title.size(); j++) {
				try {
					sheet.getRow(i).getCell(j).setCellStyle(contentStyle1);
				} catch (Exception e) {}
			}
		}
		if (!"de,fr,uk,es,it".contains(fbaInbound.getCountry())) {
			h = h + 7;
		}
		row.createCell(h++).setCellValue(totalWeight);
		sheet.getRow(rowIndex-1).getCell(h-1).setCellStyle(decimalStyle);
		row.createCell(h++).setCellValue(totalVolume);
		sheet.getRow(rowIndex-1).getCell(h-1).setCellStyle(decimalStyle);
		if (fbaInbound.getShippedDate() == null) {
			row.createCell(h++).setCellValue("");
			sheet.getRow(rowIndex-1).getCell(h-1).setCellStyle(contentStyle1);
		}
		row.createCell(h++).setCellValue("");
		sheet.getRow(rowIndex-1).getCell(h-1).setCellStyle(contentStyle1);
		
		rowIndex+=1;
		row = sheet.createRow(rowIndex++);
		row.setHeight((short) 300);
		row.createCell(0).setCellValue("preparation time:");
		row.getCell(0).setCellStyle(style1);
		row.createCell(1).setCellValue("");
		row.getCell(1).setCellStyle(style1);
		sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, 1));
		rowIndex++;
		row = sheet.createRow(rowIndex++);
		row.setHeight((short) 300);
		row.createCell(0).setCellValue("employee signature:");
		row.getCell(0).setCellStyle(style1);
		row.createCell(1).setCellValue("");
		row.getCell(1).setCellStyle(style1);
		sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, 1));
		rowIndex++;
		row = sheet.createRow(rowIndex++);
		row.setHeight((short) 300);
		row.createCell(0).setCellValue("number of parcels:");
		row.getCell(0).setCellStyle(style1);
		row.createCell(1).setCellValue("");
		row.getCell(1).setCellStyle(style1);
		sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, 1));
		rowIndex++;
		row = sheet.createRow(rowIndex++);
		row.setHeight((short) 300);
		row.createCell(0).setCellValue("date:");
		row.getCell(0).setCellStyle(style1);
		row.createCell(1).setCellValue("");
		row.getCell(1).setCellStyle(style1);
		sheet.addMergedRegion(new CellRangeAddress(rowIndex - 1, rowIndex - 1, 0, 1));
		
		for (int i = 0; i < title.size(); i++) {
			try {
				sheet.autoSizeColumn((short) i);
			} catch (Exception e) {}
		}

		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/x-download");
			String fileName = fbaInbound.getShipmentId() + ".xls";
			fileName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			wb.write(out);
			out.close();
		} catch (Exception e) {
			logger.error("FBA表单导出异常", e);
		}
		return null;
	}
	
	private String  getRule(BigDecimal boxSize){
		if(boxSize.compareTo(new BigDecimal(60))<=0){
			return "60";
		}else if(boxSize.compareTo(new BigDecimal(90))<=0){
			return "90";
		}else if(boxSize.compareTo(new BigDecimal(100))<=0){
			return "100";
		}else if(boxSize.compareTo(new BigDecimal(120))<=0){
			return "120";
		}else if(boxSize.compareTo(new BigDecimal(140))<=0){
			return "140";
		}else if(boxSize.compareTo(new BigDecimal(160))<=0){
			return "160";
		}else if(boxSize.compareTo(new BigDecimal(170))<=0){
			return "170";
		}else{
			return "无规格";
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "viewPdf")
	public String viewPdf(Integer id,HttpServletResponse response) throws Exception {
		FbaInbound fbaInbound = fbaInboundService.get(id);
		File pdfFile = new File(ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+fbaInbound.getPdfFile());
		FileInputStream in = new FileInputStream(pdfFile);
		ServletOutputStream out = response.getOutputStream();
		response.setContentType("application/pdf");// pdf文件
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
	
	
	@ResponseBody
	@RequestMapping(value = "genFbaIn")
	public String genFba() {
		fbaInboundService.genFbaInbound();
		return "生成Fba贴成功";
	}
	
}
