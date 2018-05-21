/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.web;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Row;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.CountryCode;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.excel.ExportExcel;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.service.ProductPriceService;
import com.springrain.erp.modules.ebay.entity.EbayProductPrice;
import com.springrain.erp.modules.ebay.service.EbayOrderService;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 产品价格管理Controller
 * @author Tim
 * @version 2015-12-02
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/productPrice")
public class ProductPriceController extends BaseController {

	@Autowired
	private ProductPriceService productPriceService;
	
	@Autowired
	private PsiProductGroupUserService  psiProductGroupUserService;
	
	@Autowired
	private PsiProductTypeGroupDictService 	   groupDictService;
	
	@Autowired
	private PsiProductService psiProductService;
	
	@Autowired
	private EbayOrderService ebayOrderService;
	
	@RequiresPermissions("amazoninfo:productPrice:view")
	@RequestMapping(value = {"list", ""})
	public String list(Model model) {
		model.addAttribute("products",productPriceService.findAllProducPrice());
		return "modules/amazoninfo/productPriceList";
	}
	
	/*@RequiresPermissions("amazoninfo:ebayProductSalePrice:view")*/
	@RequestMapping(value = {"ebaySalePrice"})
	public String ebaySalePrice(Model model,@RequestParam(required=false) String number) {
		Map<String,Map<String,EbayProductPrice>>  ebayPriceMap=ebayOrderService.findEbayList();
		model.addAttribute("ebayPriceMap", ebayPriceMap);
		model.addAttribute("productsCurrent",productPriceService.findAllProducCurrentSalePrice());
		return "modules/ebay/order/ebayProductSalePriceList";
	}
	
	
	@RequiresPermissions("amazoninfo:productSalePrice:view")
	@RequestMapping(value = {"salePrice"})
	public String salePrice(Model model,@RequestParam(required=false) String number) {
		Set<String> products = null;
		List<String> countryList = Lists.newArrayList("de","com","uk","fr","it","es","jp","ca","mx");
		if(!UserUtils.hasPermission("amazoninfo:productSalePrice:all")){
			Set<String> countrys = Sets.newHashSet();
			String userId = UserUtils.getUser().getId();
			Map<String,User> countryMap = psiProductGroupUserService.getCountryManager();
			for(Map.Entry<String,User> entry:countryMap.entrySet()){
				String country = entry.getKey();
				if(entry.getValue()!=null&&(entry.getValue().getId().equals(userId))){
					countrys.add(country);
				}
			}
			model.addAttribute("countrys", countrys);
			//查询负责的品线
			model.addAttribute("lines", groupDictService.getCountryLines(userId,countryList));
		}
		model.addAttribute("products",productPriceService.findAllProducSalePrice(products));
		if(StringUtils.isNotEmpty(number)){
			model.addAttribute("number",number);
		}
		model.addAttribute("productsCurrent",productPriceService.findAllProducCurrentSalePrice());
		model.addAttribute("rate", productPriceService.findCommission());
		Map<String, Float> vat = Maps.newHashMap();
		for (String country : countryList) {
			String temp = country.toUpperCase();
			if("UK".equals(temp)){
				temp = "GB";
			}
			if("COM".equals(temp)){
				temp = "US";
			}
			CountryCode vatCode = CountryCode.valueOf(temp);
			if(vatCode!=null){
				vat.put(country, vatCode.getVat()/100f);
			}
		}
		model.addAttribute("vat", vat);
		model.addAttribute("countryList", countryList);
		model.addAttribute("nameTypeMap", psiProductService.findProductTypeMap());
		model.addAttribute("typeLineMap", groupDictService.getTypeLine(null));
		return "modules/amazoninfo/productSalePriceList";
	}
	
	@RequestMapping(value = {"priceDetail"})
	@ResponseBody
	public String priceDetail(String country,String productName) {
		return productPriceService.getPriceDetail(country, productName);
	}

	@RequestMapping(value = {"exportPrice"})
    public void exportPrice( HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
	    Map<String,Map<String,EbayProductPrice>>  ebayPriceMap=ebayOrderService.findEbayList();
        Map<String, Map<String, List<String>>> findAllProducCurrentSalePrice = productPriceService.findAllProducCurrentSalePrice();
	    ExportExcel export = new ExportExcel("产品保本价销售价列表", Lists.newArrayList("产品","型号","德国|DE(€)","德国|DE亚马逊在售(€)",
                "美国|US(€)","美国|US亚马逊在售(€)"));
	    export.getSheet().setColumnWidth(0, 6000);
	    for (Entry<String,Map<String,EbayProductPrice>> entry : ebayPriceMap.entrySet()){
            String pName = entry.getKey();
            Map<String, EbayProductPrice> value = entry.getValue();
            String pModel ="";
            if(pName.contains("_")){
                pModel = pName.substring(pName.indexOf(" "),pName.indexOf("_"));
            }else{
                pModel = pName.substring(pName.indexOf(" "),pName.length());
            }
            Double usSafePrice=0d;
            Double deSafePrice=0d;
            String deSalePrice="",usSalePrice="";
            for (String country : value.keySet()) {
                 EbayProductPrice ebayProductPrice = value.get(country);
                if("de".equals(country)){
                    if(findAllProducCurrentSalePrice.get(pName)!=null){
                    deSalePrice = findAllProducCurrentSalePrice.get(pName).get(country)!=null?findAllProducCurrentSalePrice.get(pName).get(country).get(0):" ";
                    }
                    deSafePrice = ebayProductPrice.getSafePrice();
                }else{
                    if(findAllProducCurrentSalePrice.get(pName)!=null){
                    usSalePrice = findAllProducCurrentSalePrice.get(pName).get(country)!=null?findAllProducCurrentSalePrice.get(pName).get(country).get(0):" ";
                    }
                    usSafePrice = ebayProductPrice.getSafePrice();
                }
                
            }
            Row addRow = export.addRow();
            export.addCell(addRow, 0, pName);
            export.addCell(addRow, 1, pModel);
            export.addCell(addRow, 2, deSafePrice);
            export.addCell(addRow, 3, deSalePrice);
            export.addCell(addRow, 4, usSafePrice);
            export.addCell(addRow, 5, usSalePrice);
        }
        
        try {
            export.write(response, "产品保本价和售价列表" + DateUtils.getDate() + ".xlsx").dispose();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
