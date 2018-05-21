/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.web;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProductCatalog;
import com.springrain.erp.modules.amazoninfo.entity.CategoryDto;
import com.springrain.erp.modules.amazoninfo.entity.ProductCatelog;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductCatalogService;
import com.springrain.erp.modules.amazoninfo.service.CategoryCountService;
import com.springrain.erp.modules.amazoninfo.service.ProductCatelogService;


@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/productCatalog")
public class ProductCatalogController extends BaseController {
	
	@Autowired
	private ProductCatelogService 	productCatelogService;
	
	@Autowired
	private CategoryCountService 	categoryCountService;
	
	@Autowired
	private AmazonProductCatalogService amazonProductCatalogService;
	
	@RequestMapping(value = {"list", ""})
	public String list(ProductCatelog productCatelog,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		model.addAttribute("data", productCatelogService.find(productCatelog));
		model.addAttribute("typeTip", productCatelogService.findTypePcentTip());
		return "modules/amazoninfo/productCatalogList";
	}
	
	
	@RequestMapping(value = {"category"})
	public String list(String country,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		if(StringUtils.isEmpty(country)){
			country = "US";
		}
		model.addAttribute("data", categoryCountService.findMerchantsInfoByCountry(country));
		model.addAttribute("country", country);
		return "modules/amazoninfo/category/categoryList";
	}
	
	@RequestMapping(value = {"account"})
	public String list1(String name,String country,Long merchantCustomerId,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		model.addAttribute("data", categoryCountService.findAccountInfo(merchantCustomerId, country));
		model.addAttribute("name", name);
		model.addAttribute("country", country);
		model.addAttribute("merchantCustomerId", merchantCustomerId);
		return "modules/amazoninfo/category/categoryAccountList";
	}
	
	@RequestMapping(value = {"accountCategory"})
	public String list2(String name,String country,Long merchantCustomerId,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		model.addAttribute("data", categoryCountService.findAccountCategoryInfo(merchantCustomerId, country));
		model.addAttribute("name", name);
		model.addAttribute("country", country);
		model.addAttribute("merchantCustomerId", merchantCustomerId);
		return "modules/amazoninfo/category/accountCategoryList";
	}
	
	@RequestMapping(value = {"singleCategory"})
	public String list3(String name,String country,Long merchantCustomerId,String categoryName,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		model.addAttribute("data", categoryCountService.findAccountSingleCategoryInfo(merchantCustomerId, country, categoryName));
		model.addAttribute("categoryName", categoryName);
		model.addAttribute("name", name);
		model.addAttribute("country", country);
		model.addAttribute("merchantCustomerId", merchantCustomerId);
		return "modules/amazoninfo/category/singleCategory";
	}
	
	@RequestMapping(value = {"product"})
	public String list4(String name,String country,Long merchantCustomerId,String categoryName,String asin,String asinName,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		model.addAttribute("data", categoryCountService.findAccountProductInfo(merchantCustomerId, country, categoryName, asin));
		model.addAttribute("categoryName", categoryName);
		model.addAttribute("name", name);
		model.addAttribute("asin", asin);
		model.addAttribute("asinName", asinName);
		model.addAttribute("country", country);
		model.addAttribute("merchantCustomerId", merchantCustomerId);
		return "modules/amazoninfo/category/productList";
	}
	
	@ResponseBody
	@RequestMapping(value = "treeData")
	public String treeData2(String country,HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = categoryCountService.findCategoryTree(country);
		return JSON.toJSONString(mapList);
	}
	
	@RequestMapping(value = {"countCategory"})
	public String countCategory(String country,String catalog,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		if(StringUtils.isEmpty(country)){
			country = "US";
		}
		if(StringUtils.isNotEmpty(catalog)){
			model.addAttribute("data", categoryCountService.countCategoryInfo(country, catalog));
		}else{
			model.addAttribute("data", Lists.newArrayList());
		}
		model.addAttribute("country", country);
		model.addAttribute("catalog", catalog);
		
		return "modules/amazoninfo/category/categoryAnalyseList";
	}
	
	@RequestMapping(value = {"categoryPathList"})
	public String categoryPathList(String country,String catalog,String startDate,HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		model.addAttribute("country", country);
		model.addAttribute("catalog", catalog);
		model.addAttribute("startDate", startDate);
		return "modules/amazoninfo/category/categoryPathList";
	}
	
	
	@ResponseBody
	@RequestMapping(value = "catalogTreeDate")
	public String catalogTreeDate(String country,HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		List<Map<String, Object>> mapList = Lists.newArrayList();
		List<AmazonProductCatalog> catalogList=amazonProductCatalogService.findCatalogByCountry(country);
		Map<String,String>  tempMap = amazonProductCatalogService.findNum();
		for (int i=0; i<catalogList.size(); i++){
			    AmazonProductCatalog e = catalogList.get(i);
				Map<String, Object> map = Maps.newHashMap();
				map.put("id", e.getId());
				map.put("pId", e.getParent()!=null?e.getParent().getId():null);
				String title = e.getPathId();
				String info="";
				if(tempMap!=null&&StringUtils.isNotBlank(tempMap.get(e.getPathId()))){
					title +="["+tempMap.get(e.getPathId())+"]";
					info = "["+tempMap.get(e.getPathId())+"]";
				}else{
					map.put("chkDisabled",true);
				}
				if(StringUtils.isNotBlank(info)){
					map.put("name",e.getCatalogName()+"  "+info.split("销量")[0]+"]");
				}else{
					map.put("name",e.getCatalogName());
				}
				
				map.put("title",title);
				mapList.add(map);
		}
		return JSON.toJSONString(mapList);
	}
	
	@RequestMapping(value = "brandList")
	@ResponseBody
	public Map<String,List<CategoryDto>> brandList(String type,String catalog,String country,String startDate,String siblings,HttpServletResponse response) throws ParseException {
		if("com".equals(country)){
			country="us";
		}
		Map<String,List<CategoryDto>> map=Maps.newHashMap();
		
		
		/*Object[] obj= amazonProductCatalogService.findPrice(catalog);
		Integer num = Integer.parseInt(obj[3].toString());
		if(num>10){
			Double avgPrice = Double.parseDouble(obj[0].toString());
			Double lowerPrice = Double.parseDouble(obj[1].toString());
			Double higherPrice = Double.parseDouble(obj[2].toString());
			CategoryDto dto = new CategoryDto();
			Integer price= MathUtils.roundDown((avgPrice+lowerPrice)/2);	
			Integer price1 = MathUtils.roundDown((avgPrice+higherPrice)/2);	
			Integer avg =  MathUtils.roundDown(avgPrice);
			dto.setPrice1(MathUtils.roundDown(lowerPrice)+"~"+price);
			dto.setPrice2(price+"~"+avg);
			dto.setPrice3(avg+"~"+price1);
			dto.setPrice4(price1+"~"+MathUtils.roundUp(higherPrice));
			map.put("2",Lists.newArrayList(dto));
		}*/
		List<CategoryDto> tempList= categoryCountService.findBrand(catalog,country,startDate);
		map.put("3", tempList);
		List<CategoryDto> dtoList=categoryCountService.findBrand(catalog,country,startDate,type);
		map.put("0", dtoList);
		if(StringUtils.isNotBlank(siblings)){
			 List<CategoryDto> siblingsList=categoryCountService.findSiblings(Lists.newArrayList(siblings.split(",")),country,startDate,type);
			 map.put("1", siblingsList);
		}
		return map;
	}
}
