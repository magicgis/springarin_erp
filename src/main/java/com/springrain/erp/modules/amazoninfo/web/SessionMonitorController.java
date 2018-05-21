package com.springrain.erp.modules.amazoninfo.web;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.excel.ExportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.SessionMonitor;
import com.springrain.erp.modules.amazoninfo.entity.SessionMonitorResultDto;
import com.springrain.erp.modules.amazoninfo.service.SessionMonitorService;
import com.springrain.erp.modules.psi.entity.PsiBarcode;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * session和转化率监控Controller
 * @author Tim
 * @version 2015-02-09
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/sessionMonitor")
public class SessionMonitorController extends BaseController {

	@Autowired
	private SessionMonitorService sessionMonitorService;
	
	@Autowired
	private PsiProductService psiProductService;
	
	@ModelAttribute
	public SessionMonitor get(@RequestParam(required = false) Integer id) {
		if (id != null) {
			return sessionMonitorService.get(id);
		} else {
			return new SessionMonitor();
		}
	}
	
	@RequestMapping(value = {"list", ""})
	public String list(String syn,String orderBy,SessionMonitor sessionMonitor, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isEmpty(sessionMonitor.getCountry())){
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					sessionMonitor.setCountry(dict.getValue());
					break;
				}
			}
		}
		if(StringUtils.isEmpty(sessionMonitor.getCountry())){
			sessionMonitor.setCountry("total");
		}
		if(!"total".equals(sessionMonitor.getCountry())){
			if("1".equals(syn)){
				//同步月份数据
				sessionMonitorService.synSessions(sessionMonitor);
			}
			Page<PsiProduct> page = new Page<PsiProduct>(request, response,100000);
			page.setOrderBy("");
			psiProductService.findWithSessionsMonitor(page, sessionMonitor);
			//排除多颜色但其中没有绑定sku的产品
			for (PsiProduct product : page.getList()) {
				Map<String, PsiBarcode> temp = product.getBarcodeMap2().get(sessionMonitor.getCountry());
				if(temp!=null&&temp.size()>1){
					for (Iterator <Entry<String, PsiBarcode>>  iterator = temp.entrySet().iterator(); iterator
							.hasNext();) {
						Entry<String, PsiBarcode> entry = iterator.next();
						if(!psiProductService.getProductIsHasSku(product.getId(), sessionMonitor.getCountry(), entry.getKey())){
							iterator.remove();
						}
					}
				}
			}
			if (null==sessionMonitor.getMonth()){
				Date today = new Date();
				today.setDate(1);
				today.setHours(0);
				today.setMinutes(0);
				today.setSeconds(0);
				sessionMonitor.setMonth(today);
			}
			List<SessionMonitor> list = sessionMonitorService.find(sessionMonitor); 
			final Map<String, SessionMonitor> map = Maps.newHashMap();
			for (SessionMonitor sessionMonitor2 : list) {
				map.put(sessionMonitor2.getProductName()+"_"+sessionMonitor2.getColor(), sessionMonitor2);
			}
			//排序
			if(StringUtils.isNotBlank(orderBy)){
				final String productNames = map.values().toString();
				String[] temps =  orderBy.split(" ");
				final String order = temps[0] ;
				Collections.sort(page.getList(),new Comparator<PsiProduct>() {
					@Override
					public int compare(PsiProduct o1, PsiProduct o2) {
						Boolean flag1 = productNames.contains(o1.getName()+","+order);
						Boolean flag2 = productNames.contains(o2.getName()+","+order);
						return flag2.compareTo(flag1);
					}
				});
			}
			model.addAttribute("page", page);
	        model.addAttribute("map",map);
		}else{
			List<SessionMonitor> list = sessionMonitorService.find(sessionMonitor);
			Map<String,Map<String,String>> sessionsMap = Maps.newHashMap();
			for (SessionMonitor sessionMonitor2 : list) {
				String color = "";
				if(StringUtils.isNotBlank(sessionMonitor2.getColor())){
					color = "_"+sessionMonitor2.getColor();
				}
				String productName = sessionMonitor2.getProductName()+color;
				if(sessionMonitor2.getSessions()!=null || sessionMonitor2.getConver()!=null){
					Map<String,String> sessions = sessionsMap.get(productName);
					if(sessions==null){
						sessions = Maps.newHashMap();
						sessionsMap.put(productName, sessions);
					}
					String val = "";
					if(sessionMonitor2.getSessions()!=null){
						val = sessionMonitor2.getSessions()+"";
					}
					if(sessionMonitor2.getConver()!=null){
						if(val.length()>0){
							val =val+"<b> , </b>"+sessionMonitor2.getConver()+"%";
						}else{
							val = sessionMonitor2.getConver()+"%";
						}
					}
					sessions.put(sessionMonitor2.getCountry(), val);
				}
			}
			//排序
			if(StringUtils.isNotBlank(orderBy)){
				String[] temps =  orderBy.split(" ");
				final String country = temps[0] ;
				final String sort =temps[1];
				List<Map.Entry<String,Map<String,String>>> infoIds = new ArrayList<Map.Entry<String,Map<String,String>>>( 
						sessionsMap.entrySet()); 
				Collections.sort(infoIds,new Comparator<Map.Entry<String,Map<String,String>>>(){
					@Override
					public int compare(Entry<String, Map<String, String>> o1,
							Entry<String, Map<String, String>> o2) {
						if(o1.getValue().get(country)!=null && o2.getValue().get(country)!=null ){
							return o1.getKey().compareToIgnoreCase(o2.getKey());
						}else if(o1.getValue().get(country)==null && o2.getValue().get(country) ==null ){
							return o1.getKey().compareToIgnoreCase(o2.getKey());
						}else{
							if(o1.getValue().get(country)!=null){
								if("ASC".equals(sort)){
									return 1;
								}else{
									return -1;
								}	
							}else{
								if("ASC".equals(sort)){
									return -1;
								}else{
									return 1;
								}	
							}
						}
					}
				});
				sessionsMap = Maps.newLinkedHashMap();
				for (Entry<String, Map<String, String>> entry : infoIds) {
					sessionsMap.put(entry.getKey(), entry.getValue());
				}
			}
			model.addAttribute("sessionsMap",sessionsMap);
		}
		model.addAttribute("orderBy",orderBy);
		return "modules/amazoninfo/sessionMonitorList";
	}
	
	@RequestMapping(value = {"export"})
	public String export(String orderBy,SessionMonitor sessionMonitor, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<SessionMonitor> list = sessionMonitorService.find(sessionMonitor);
		Map<String,Map<String,String>> sessionsMap = Maps.newHashMap();
		for (SessionMonitor sessionMonitor2 : list) {
			String color = "";
			if(StringUtils.isNotBlank(sessionMonitor2.getColor())){
				color = "_"+sessionMonitor2.getColor();
			}
			String productName = sessionMonitor2.getProductName()+color;
			if(sessionMonitor2.getSessions()!=null || sessionMonitor2.getConver()!=null){
				Map<String,String> sessions = sessionsMap.get(productName);
				if(sessions==null){
					sessions = Maps.newHashMap();
					sessionsMap.put(productName, sessions);
				}
				String val = "";
				if(sessionMonitor2.getSessions()!=null){
					val = sessionMonitor2.getSessions()+"";
				}
				if(sessionMonitor2.getConver()!=null){
					if(val.length()>0){
						val =val+"<b> , </b>"+sessionMonitor2.getConver()+"%";
					}else{
						val = sessionMonitor2.getConver()+"%";
					}
				}
				sessions.put(sessionMonitor2.getCountry(), val);
			}
		}
		//排序
		if(StringUtils.isNotBlank(orderBy)){
			String[] temps =  orderBy.split(" ");
			final String country = temps[0] ;
			final String sort =temps[1];
			List<Map.Entry<String,Map<String,String>>> infoIds = new ArrayList<Map.Entry<String,Map<String,String>>>( 
					sessionsMap.entrySet()); 
			Collections.sort(infoIds,new Comparator<Map.Entry<String,Map<String,String>>>(){
				@Override
				public int compare(Entry<String, Map<String, String>> o1,
						Entry<String, Map<String, String>> o2) {
					if(o1.getValue().get(country)!=null && o2.getValue().get(country)!=null ){
						return o1.getKey().compareToIgnoreCase(o2.getKey());
					}else if(o1.getValue().get(country)==null && o2.getValue().get(country) ==null ){
						return o1.getKey().compareToIgnoreCase(o2.getKey());
					}else{
						if(o1.getValue().get(country)!=null){
							if("ASC".equals(sort)){
								return 1;
							}else{
								return -1;
							}	
						}else{
							if("ASC".equals(sort)){
								return -1;
							}else{
								return 1;
							}	
						}
					}
				}
			});
			sessionsMap = Maps.newLinkedHashMap();
			for (Entry<String, Map<String, String>> entry : infoIds) {
				sessionsMap.put(entry.getKey(), entry.getValue());
			}
		}
		try {
            String fileName = "各平台产品指标监控汇总表"+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
            ExportExcel excel = new ExportExcel("指标监控汇总表",Lists.newArrayList("产品名","德国","美国","英国","法国","日本","意大利","西班牙","加拿大"));
            for (Map.Entry<String, Map<String, String>> entryRs : sessionsMap.entrySet()) { 
                String name = entryRs.getKey();
                Map<String, String>  tempMap=entryRs.getValue();
    			Row row = excel.addRow();
    			excel.addCell(row,0, name);
    			excel.addCell(row,1, tempMap.get("de"));
    			excel.addCell(row,2, tempMap.get("com"));
    			excel.addCell(row,3, tempMap.get("uk"));
    			excel.addCell(row,4, tempMap.get("fr"));
    			excel.addCell(row,5, tempMap.get("jp"));
    			excel.addCell(row,6, tempMap.get("it"));
    			excel.addCell(row,7, tempMap.get("es"));
    			excel.addCell(row,8, tempMap.get("ca"));
			}
            excel.write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/sessionMonitorList/?repage";
	}
	
	
	
	@RequestMapping(value = {"result/export"})
	public String listResultExport(SessionMonitor sessionMonitor, HttpServletRequest request, HttpServletResponse response, Model model) {
		List<SessionMonitorResultDto> data = sessionMonitorService.getData(sessionMonitor);
		Collections.sort(data);
		try {
			String name = DictUtils.getDictLabel(sessionMonitor.getCountry(),"platform","")+"产品按"+(sessionMonitor.getSearchFlag().equals("2")?"月":"天")+"["+sessionMonitor.getMonth().toLocaleString()+"]指标监控结果";
            String fileName =  DictUtils.getDictLabel(sessionMonitor.getCountry(),"platform","").split("\\|")[0]+"产品按"+(sessionMonitor.getSearchFlag().equals("2")?"月":"天")+DateUtils.getDate("yyyyMMddHHmmss")+".xlsx"; 
            ExportExcel excel = new ExportExcel(name,Lists.newArrayList("产品名","Asin","Target Session","Session","Target Conversion(%)","Conversion(%)","Sale Price","Sale forecast","Pass"));
    		double count = 0d;
            for (SessionMonitorResultDto dto : data) {
    			Row row = excel.addRow();
    			excel.addCell(row,0, dto.getProductName());
    			excel.addCell(row,1,dto.getAsins().keySet().toString().replace("[","").replace("]",""));
    			if(sessionMonitor.getSearchFlag().equals("0")){
    				excel.addCell(row,2, dto.getSessionsByDate());
    			}else{
    				excel.addCell(row,2, dto.getSessions());
    			}
    			excel.addCell(row,3, dto.getRealSessions());
    			excel.addCell(row,4, dto.getConver());
    			excel.addCell(row,5, dto.getRealConver());
    			if(dto.getPrice()!=null){
    				BigDecimal  temp = new  BigDecimal(dto.getPrice());
    				excel.addCell(row,6,temp.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() );
    			}else{
    				excel.addCell(row,6, "");
    			}
    			count +=dto.getProductsPrice();
    			excel.addCell(row,7, dto.getProductsPrice());
    			excel.addCell(row,8, dto.getIsPass()?"√":"×");
			}
    		Row row = excel.addRow();
			excel.addCell(row,0, "Total");
			excel.addCell(row,1, "");
			excel.addCell(row,2, "");
			excel.addCell(row,3, "");
			excel.addCell(row,4, "");
			excel.addCell(row,5, "");
			excel.addCell(row,6, "");
			excel.addCell(row,7, count);
			excel.addCell(row,8, "");
            excel.write(response, fileName).dispose();
    		return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:"+Global.getAdminPath()+"/amazoninfo/sessionMonitor/result?counrty"+sessionMonitor.getCountry();
	}
	
	@RequestMapping(value = {"result"})
	public String listResult(SessionMonitor sessionMonitor, HttpServletRequest request, HttpServletResponse response, Model model) {
		if(StringUtils.isEmpty(sessionMonitor.getCountry())){
			//根据上贴权限设置默认的country
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				if(UserUtils.hasPermission("amazoninfo:feedSubmission:" + dict.getValue())){
					sessionMonitor.setCountry(dict.getValue());
					break;
				}
			}
		}
		List<SessionMonitorResultDto> data = sessionMonitorService.getData(sessionMonitor);
		Collections.sort(data);
		model.addAttribute("data",data);
		return "modules/amazoninfo/sessionMonitorResultList";
	}
	
	
	

	@RequestMapping(value = "save")
	@ResponseBody
	public String save(SessionMonitor sessionMonitor, Model model, RedirectAttributes redirectAttributes) {
		if(sessionMonitor.getId()==null){
			sessionMonitor.setCreateDate(new Date());
			sessionMonitor.setCreateUser(UserUtils.getUser());
		}
		if(sessionMonitor.getSessions()!=null){
			float temp = sessionMonitor.getSessions()/(float)getMonthMaxDays(sessionMonitor.getMonth());
			BigDecimal bd = new BigDecimal(temp);
			bd = bd.setScale(0,BigDecimal.ROUND_UP);
			sessionMonitor.setSessionsByDate(bd.intValue());
		}else{
			sessionMonitor.setSessionsByDate(null);
		}
		sessionMonitor.setLastUpdateDate(new Date());
		sessionMonitor.setLastUpdateUser(UserUtils.getUser());
		sessionMonitorService.save(sessionMonitor);
		return sessionMonitor.getId()+"";
	}
	
	private int getMonthMaxDays(Date date){
		Calendar   calendar   =   Calendar.getInstance();   
	    calendar.set(1900+date.getYear(),date.getMonth(),1);   
	    calendar.roll(Calendar.DATE,   false);   
	    return calendar.get(Calendar.DATE); 
	}
}
