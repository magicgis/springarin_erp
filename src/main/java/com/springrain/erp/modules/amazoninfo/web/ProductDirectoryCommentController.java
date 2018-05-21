/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.web;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.MapValueComparator;
import com.springrain.erp.common.web.BaseController;
import com.springrain.erp.modules.amazoninfo.entity.ProductDirectory;
import com.springrain.erp.modules.amazoninfo.entity.ProductDirectoryComment;
import com.springrain.erp.modules.amazoninfo.service.OpponentStockService;
import com.springrain.erp.modules.amazoninfo.service.ProductDirectoryCommentDetailService;
import com.springrain.erp.modules.amazoninfo.service.ProductDirectoryCommentService;
import com.springrain.erp.modules.amazoninfo.service.ProductDirectoryService;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;

/**
 * 产品目录评论Controller
 * @author Michael
 * @version 2015-08-24
 */
@Controller
@RequestMapping(value = "${adminPath}/amazoninfo/productDirectoryComment")
public class ProductDirectoryCommentController extends BaseController {
	@Autowired
	private ProductDirectoryCommentService 		 produtDirectoryCommentService;
	@Autowired
	private ProductDirectoryCommentDetailService produtDirectoryCommentDetailService;
	@Autowired
	private ProductDirectoryService 			 produtDirectoryService;
	@Autowired
	private OpponentStockService    			 opponentStockService;
	
	
	@RequestMapping(value = {"list", ""})
	public String list(ProductDirectoryComment directoryComment, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		ProductDirectory productDirectory = this.produtDirectoryService.get(directoryComment.getDirectoryId());
		String currency ="";
		String country = productDirectory.getCountry();
		//获取系数，评论和销售比
		DecimalFormat df = new DecimalFormat("0.##");
		Float saleCommRate =directoryComment.getSaleCommRate();
		Float saleCommTotal=0f;
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
		//从库里查出最新一天的数据
		Date lastDate = this.produtDirectoryCommentService.getLastDate(productDirectory.getId());
		Map<String,ProductDirectoryComment>  selfComMap = Maps.newHashMap();
		//价格排序
		List<Float> percentPrices = Lists.newArrayList();
		
		//查出那些asin是自己的产品
		Set<String> selfAsinSet  =  this.produtDirectoryCommentService.getSelfAsin();
		Set<String> tempAsinSet  =  Sets.newHashSet();     
		Set<String> unCountAsins  =  Sets.newHashSet();    //不参与计算的asin
		Map<String,ProductDirectoryComment>  directoryMap = Maps.newHashMap();
		List<ProductDirectoryComment> list = this.produtDirectoryCommentService.find(productDirectory.getId(),lastDate);
		Map<String,Integer>  brandMap = Maps.newTreeMap();
		if(list!=null&&list.size()>0){
			String url ="";
			for(ProductDirectoryComment productComment:list){
				if(productComment.getRanking()!=null&&productComment.getRanking()<21&&StringUtils.isNotEmpty(productComment.getBrand())){
					Integer i =1;
					if(brandMap.get(productComment.getBrand())!=null){
						i+=brandMap.get(productComment.getBrand());
					}
					brandMap.put(productComment.getBrand(), i);
				}
				if(StringUtils.isEmpty(url)){
					url=productComment.getUrl();
				}
				if(StringUtils.isNotEmpty(country)){
					if("com".equals(country)){
						currency="USD";
					}else if("de,fr,es,it".contains(country)){
						currency="EUR";
					}else if("uk".equals(country)){
						currency="GBP";
					}else if("ca".equals(country)){
						currency="CAD";
					}else if("mx".equals(country)){
						currency="MXN";
					}
				}
				
				//找出自己的asin
				String asin = productComment.getAsin().replace("\n", "");//
				if(selfAsinSet.contains(asin)){
					Integer saleQuantiy = this.produtDirectoryCommentService.getSaleQuantityByAsin(asin,this.getCountry(url));
					if(productComment.getAllStar().intValue()!=0){
						saleCommTotal+=saleQuantiy*1.0f/productComment.getAllStar();
						tempAsinSet.add(asin);
					}
					selfComMap.put(asin, productComment);
				}
				if("1".equals(productComment.getIsShield())){
					unCountAsins.add(productComment.getAsin());
				}else{
					if(productComment.getSalePrice().floatValue()!=0){
						percentPrices.add(productComment.getSalePrice());
					}
				}
				directoryMap.put(asin, productComment);
			}
			
			
			//通过比较器实现比较排序 
			MapValueComparator bvc =  new MapValueComparator(brandMap,false);  
			TreeMap<String,Integer> sortKeyMap = new TreeMap<String,Integer>(bvc);  
			sortKeyMap.putAll(brandMap); 
	        Map<String,Integer> sortMap=Maps.newLinkedHashMap(); 
	        for(String sortKey:sortKeyMap.keySet()){
	        	sortMap.put(sortKey, brandMap.get(sortKey));
	        }
			
			if(directoryComment.getSaleCommRate()==null){
				if(tempAsinSet.size()>0){
					saleCommRate=Float.parseFloat(df.format(saleCommTotal/tempAsinSet.size()));
				}else{
					saleCommRate =100f;
				}
			}
			
			
			
			directoryComment.setSaleCommRate(saleCommRate);
			Float totalAmount = 0f;
			Integer starTotal =0;
			Integer goodStar = 0;
		
			//获得30天的评论数
//			Map<String,Integer> commentMap =this.produtDirectoryCommentDetailService.getOneMonthReviews(directoryMap.keySet(), country);
//			Integer total30Comms = 0;
//			for(Integer comms :commentMap.values()){
//				total30Comms+=comms;
//			}
			
			//获得前一周，一年前一周，两年前一周数据
			Map<String,String> weekComparMap = this.produtDirectoryCommentDetailService.getLast7DaysReviews(directoryMap.keySet(), country);
			//获取昨天的购物车销量  
			Map<String,Integer> yestardayMap = this.opponentStockService.getYesterdaySale(directoryMap.keySet(), country, sdf.parse(sdf.format(new Date())));
			//30天销量额
			Float sale30Amount = 0f;
			Float totalPrice=0f;;
			int i = 0;
			//算出价格颜色颜色
			Float floatArr[] = new Float[percentPrices.size()];
			for(int ii =0;ii<percentPrices.size();ii++){
				floatArr[ii]= percentPrices.get(ii);
			}
			Arrays.sort(floatArr);
			//10%
			Float percent10Price=floatArr[(int)(floatArr.length*0.1)-1];
			//30%
			Float percent30Price=floatArr[(int)(floatArr.length*0.3)-1];
			//50%
			Float percent50Price=floatArr[(int)(floatArr.length*0.5)-1];
			//70%
			Float percent70Price=floatArr[(int)(floatArr.length*0.7)-1];
			
			
			//算出均价  和30天销量
			for(String asin: directoryMap.keySet()){
				ProductDirectoryComment  tempComm = directoryMap.get(asin);
				String countStr=weekComparMap.get(asin);
				tempComm.setWeekCompare(countStr);
				if(StringUtils.isNotEmpty(countStr)){
					tempComm.setComm30Days(Integer.parseInt(countStr.split(",")[3])*saleCommRate.intValue());
				}
				if(unCountAsins.contains(asin.replace("\n", ""))){//如果设置了屏蔽    就不算平均价格和30天的总销量
					continue;
				}
				Float salePrice=tempComm.getSalePrice().floatValue();
				if(salePrice!=0){
					if(salePrice<=percent10Price.floatValue()){
						tempComm.setDisplayColor("#343831");
					}else if(percent10Price.floatValue()<salePrice&&salePrice<=percent30Price.floatValue()){
						tempComm.setDisplayColor("#552e89");
					}else if(percent30Price.floatValue()<salePrice&&salePrice<=percent50Price.floatValue()){
						tempComm.setDisplayColor("#00834e");
					}else if(percent50Price.floatValue()<salePrice&&salePrice<=percent70Price.floatValue()){
						tempComm.setDisplayColor("#a97463");
					}else if(salePrice>percent70Price.floatValue()){
						tempComm.setDisplayColor("#bb0334");
					}
					totalPrice+=tempComm.getSalePrice();
					i++;
				}
				if(weekComparMap.get(asin)!=null){
					sale30Amount+=Integer.parseInt(weekComparMap.get(asin).split(",")[3])*saleCommRate*tempComm.getSalePrice();
				}
			}
			
			Float avgPrice =0f;
			if(i!=0){
				avgPrice = totalPrice/i;
			}
			
			//大于平均价的产品个数
			Integer hightPriceQ =0;
			for(ProductDirectoryComment comment:directoryMap.values()){
				String asin =comment.getAsin().replace("\n", "");
				if(unCountAsins.contains(asin)){//如果设置了屏蔽    就不算平均价格和30天的总销量
					continue;
				}
				totalAmount+=comment.getAllStar()*saleCommRate*comment.getSalePrice();
				starTotal+=comment.getAllStar();
				goodStar+=comment.getGoodComments();
				if(directoryMap.get(asin).getSalePrice()>avgPrice){
					hightPriceQ++;
				}
			}
			//市场容积
			String totalContain=Float.valueOf(starTotal*saleCommRate).intValue()+"";
			//好评率   
			Float  goodCommentRate   = goodStar*100f/starTotal;

			//评论增长率
//			Float  addRateCommNums  =0f;
//			if(starTotal!=null&&starTotal.intValue()!=0){
//				addRateCommNums = (float) (total30Comms/starTotal);
//			}
			
//			String[]  resArr = new String[]{starTotal.toString(),totalContain,totalAmount.toString(),df.format(goodCommentRate),df.format(sale30Amount/30),df.format(addRateCommNums),df.format(avgPrice),hightPriceQ.toString(),total30Comms+""} ;
			String[]  resArr = new String[]{starTotal.toString(),totalContain,totalAmount.toString(),df.format(goodCommentRate),df.format(sale30Amount/30),df.format(avgPrice),hightPriceQ.toString()} ;
			if(selfComMap!=null&&selfComMap.size()>0){
				Map<String,String>  asinProductNameMap = this.produtDirectoryCommentService.getProductNameByAsin(selfComMap.keySet());
				model.addAttribute("asinProductNameMap", asinProductNameMap);
			}
			Map<String,Integer> asin30SaleMap =opponentStockService.getAsin30Sale(directoryMap.keySet(),country, DateUtils.addDays(sdf.parse(sdf.format(new Date())),-30));
			Integer total20=0;
			Integer ii =1;
			for(String key:sortMap.keySet()){
				if(ii>3){
					break;
				}
				total20+=sortMap.get(key);
				ii++;
			}
			
			
			model.addAttribute("yestardayMap", yestardayMap);
			model.addAttribute("asin30SaleMap", asin30SaleMap);
			model.addAttribute("selfComMap", selfComMap);
			model.addAttribute("resArr", resArr);
			model.addAttribute("total20", total20);
			model.addAttribute("dateEnd", lastDate);
			model.addAttribute("brandMap", sortMap);
			model.addAttribute("directoryMap", directoryMap);
			model.addAttribute("currency", currency);
			
			model.addAttribute("percent10Price", percent10Price);
			model.addAttribute("percent30Price", percent30Price);
			model.addAttribute("percent50Price", percent50Price);
			model.addAttribute("percent70Price", percent70Price);
		}
		
		model.addAttribute("subject", productDirectory.getSubject());
		return "modules/amazoninfo/productDirectoryComment";
	}
	
	
	//单产品图表页面
	@RequestMapping(value = {"showChart"})
	public String showChart(Integer directoryCommentId,String title, HttpServletRequest request, HttpServletResponse response, Model model) throws ParseException {
		ProductDirectoryComment  dirCom = this.produtDirectoryCommentService.get(directoryCommentId);
		if(StringUtils.isNotEmpty(title)){
			dirCom.setTitle(title);
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
		//查询评论的最近日期
		Date afterDate = this.produtDirectoryCommentDetailService.getLastCommDay(dirCom.getCountry(), dirCom.getAsin());
		Date beforeDate = DateUtils.addDays(afterDate, -31);
		Date date = beforeDate;
		List<String> xAxis = Lists.newArrayList();
		List<String> yAxis = Lists.newArrayList();
		List<String> yAxisGood = Lists.newArrayList();
		List<String> yAxisBad = Lists.newArrayList();
		List<Map<String,Integer>> rsList=this.produtDirectoryCommentDetailService.get30DaysComms(dirCom.getCountry(), dirCom.getAsin(),afterDate);
		Map<String,Integer>  goodMap = rsList.get(0);
		Map<String,Integer>  badMap = rsList.get(1);
		Map<String,Integer>  totalMap = rsList.get(2);
		int i =0;
		while(!date.after(afterDate)){
			i++;
			String dateStr = sdf.format(date);
			xAxis.add("'"+dateStr+"'");
			if(goodMap.containsKey(dateStr)){
				yAxisGood.add(goodMap.get(dateStr)+"");
			}else{
				yAxisGood.add("0");
			}
			
			if(badMap.containsKey(dateStr)){
				yAxisBad.add(badMap.get(dateStr)+"");
			}else{
				yAxisBad.add("0");
			}
			
			if(totalMap.containsKey(dateStr)){
				yAxis.add(totalMap.get(dateStr)+"");
			}else{
				yAxis.add("0");
			}
			
			date = DateUtils.addDays(beforeDate, i);
		}
		
		model.addAttribute("dirCom", dirCom);
		model.addAttribute("xAxis", xAxis.toString());
		model.addAttribute("yAxis", yAxis);
		model.addAttribute("yAxisGood", yAxisGood);
		model.addAttribute("yAxisBad", yAxisBad);
		return "modules/amazoninfo/productDirectoryCommentChart";
	}
	
	
	
	
	@ResponseBody
	@RequiresPermissions("psi:all:view")
	@RequestMapping(value={"updateShield"})
	public String updateShield(Integer directoryId,String asin,boolean checked) {
		String isShield ="0";
		if(checked){
			isShield="1";
		}
		String res=this.produtDirectoryCommentService.updateShieldSta(directoryId,asin,isShield);
		return res;
	}
	
	
	private String getCountry(String url){
		String suffix = url.split("/")[2].replace("www.amazon.", "");
		String country ="";
		if("co.uk,co.jp".contains(suffix)){
			country = suffix.replace("co.", "");
		}else if("com.mx".equals(country)){
			country = "mx";
		}else{
			country=suffix;
		}
		return country;
	}
	
	public static void main(String [] args){
		List<Float> list = Lists.newArrayList();
		String str="109.0, 129.99, 139.95, 399.0, 35.99, 29.99, 249.0, 56.66, 23.95, 112.98, 114.79, 17.95, 7.69, 16.99, 379.0, 59.0, 39.99, 175.0, 7.9, 15.78, 41.99, 19.99, 226.36, 95.8, 139.99, 27.99, 17.9, 15.99, 24.99, 24.95, 170.99, 6.38, 9.99, 59.0, 199.99, 26.99, 29.99, 29.99, 19.99, 36.99, 39.99, 139.95, 17.99, 28.0, 109.99, 27.99, 7.99, 379.0, 25.95, 79.99, 12.99, 14.99, 59.0, 74.99, 109.0, 54.9, 17.99, 29.99, 29.0, 12.99, 27.98, 32.99, 29.99, 158.43, 15.95, 209.0, 29.99, 29.99, 24.95, 83.99, 59.0, 169.0, 18.99, 27.8, 89.8, 29.0, 17.99, 28.99, 19.66, 39.99, 0.99, 32.99, 3.99, 29.99, 26.9, 65.33, 159.99, 19.99, 94.99, 186.99, 169.0, 109.0, 21.99, 94.99, 99.8, 15.99, 72.95, 15.94";
		String arr[]=str.split(",");
		
		Arrays.sort(arr);
		for(int i =0;i<arr.length;i++){
			list.add(Float.parseFloat(arr[i]));
		}
		
		Float floatArr[] = new Float[list.size()];
		for(int i =0;i<list.size();i++){
			floatArr[i]= list.get(i);
		}
		
		Arrays.sort(floatArr);
		//10%
		Float f10=floatArr[(int)(floatArr.length*0.1)];
		//30%
		Float f30=floatArr[(int)(floatArr.length*0.3)];
		//50%
		Float f50=floatArr[(int)(floatArr.length*0.5)];
		//70%
		Float f70=floatArr[(int)(floatArr.length*0.7)];
		
		System.out.println(f10);
		System.out.println(f30);
		System.out.println(f50);
		System.out.println(f70);
	}
	
	
	
	
}
