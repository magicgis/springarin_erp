package com.springrain.erp.modules.psi.scheduler;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.HttpRequest;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.modules.amazoninfo.entity.AmazonPostsDetail;
import com.springrain.erp.modules.amazoninfo.entity.SalesForecastByMonth;
import com.springrain.erp.modules.amazoninfo.scheduler.SendCustomEmail1Manager;
import com.springrain.erp.modules.amazoninfo.service.AmazonPostsDetailService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.AmazonPromotionsWarningService;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.amazoninfo.service.SalesForecastServiceByMonth;
import com.springrain.erp.modules.psi.entity.ProductSalesInfo;
import com.springrain.erp.modules.psi.entity.PsiInventoryFba;
import com.springrain.erp.modules.psi.entity.PsiInventoryGap;
import com.springrain.erp.modules.psi.entity.PsiInventoryTotalDtoByInStock;
import com.springrain.erp.modules.psi.entity.PsiOutOfStockInfo;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductAvgPrice;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.entity.PsiProductInStock;
import com.springrain.erp.modules.psi.entity.PsiProductLocalAvgPrice;
import com.springrain.erp.modules.psi.entity.PsiProductTransportRate;
import com.springrain.erp.modules.psi.entity.PsiTransportDto;
import com.springrain.erp.modules.psi.entity.PsiTransportForecastAnalyseOrder;
import com.springrain.erp.modules.psi.entity.PsiTransportForecastDto;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPsiTransportOrderItem;
import com.springrain.erp.modules.psi.service.FbaInboundService;
import com.springrain.erp.modules.psi.service.ProductSalesInfoService;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiOutOfStockInfoService;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductInStockService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.psi.service.PsiTransportForecastOrderService;
import com.springrain.erp.modules.psi.service.PsiTransportOrderService;
import com.springrain.erp.modules.psi.service.PsiTransportPaymentService;
import com.springrain.erp.modules.psi.service.lc.LcPsiTransportOrderService;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;

public class PsiOutOfStockInfoMonitor {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(PsiOutOfStockInfoMonitor.class);
	@Autowired
	private PsiOutOfStockInfoService psiOutOfStockInfoService ;
	@Autowired
	private PsiInventoryService psiInventoryService ;
	@Autowired
	private ProductSalesInfoService 		productSalesInfoService;
	@Autowired
	private AmazonProduct2Service 		amazonProduct2Service;
	@Autowired
	private PsiTransportOrderService psiTransportOrderService;
	@Autowired
	private SalesForecastServiceByMonth 	salesForecastService;
	@Autowired
	private PsiInventoryFbaService psiInventoryFbaService;
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
	@Autowired
	private PsiProductService psiProductService;
	@Autowired
	private PsiProductInStockService  psiProductInStockService;
	@Autowired
	private MailManager mailManager;
	@Autowired
	private SendCustomEmail1Manager sendCustomEmail1Manager;
	@Autowired
	private PsiTransportPaymentService psiTransportPaymentService;
	@Autowired
	private FbaInboundService fbaInboundService;
	@Autowired
	private AmazonPromotionsWarningService amazonPromotionsWarningService;
	@Autowired
	private LcPsiTransportOrderService lcPsiTransportOrderService;
	@Autowired
	private AmazonPostsDetailService amazonPostsDetailService;
	
	@Autowired
	private SaleReportService saleReportService;
	
	@Autowired
	private PsiTransportForecastOrderService	forecastOrderService;
	@Autowired
	private PsiProductAttributeService    psiProductAttributeService;
	@Autowired
	private PsiProductTypeGroupDictService 	   groupDictService;
	
	public void generateDayOrdert() {
			
			try{
				generateDayOrder();
			}catch(Exception e){
				 LOGGER.error("生成分析运单异常！",e);
			}
			
	}

	public void savePsiInventoryGapList() {
		
		try{
			List<PsiInventoryGap> gapList=Lists.newArrayList();
			LOGGER.info("统计产品缺口");
			Date createDate=new Date();
			Date start = new Date();
			start.setHours(0);
			start.setMinutes(0);
			start.setSeconds(0);
			Date end = DateUtils.addMonths(start,5);
			DateFormat formatWeek = new SimpleDateFormat("yyyyww");
			DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
			if(start.getDay()==0){
				start = DateUtils.addDays(start, -1);
			}
			Map<String,Map<String,Map<String,SalesForecastByMonth>>>  data = salesForecastService.findAllWithType(DateUtils.addMonths(start, -1),end);//产品 [国家[月  数]]
			Map<String,Integer> totalFbaTrans=psiOutOfStockInfoService.findFbaTrans();
			Map<String,Map<String,Map<String,Integer>>> allGapInfoMap=psiOutOfStockInfoService.findGapInfo();
			Map<String,Map<String,Integer>>  sale30Map = psiInventoryFbaService.get31SalesMap(null);//country_name_quantity
			Map<String, List<String>> countryOnSaleNotNewProduct=psiProductEliminateService.findCountryOnSaleNotNewProduct() ;
			Map<String,PsiInventoryFba> amazonStock=psiInventoryService.getAllProductFbaInfo();//productName+"_"+country
			 //Map<String,PsiInventoryFba>  getProductFbaInfo(String productName);//productName+"_"+eu
			
			Map<String,PsiProductInStock> euStockMap=psiProductInStockService.getHistoryInventory("eu");
			//亚马逊仓在途
			Map<String,Map<String,Integer>> fbaTransMap=allGapInfoMap.get("0");
			//本地仓
			Map<String,Map<String,Integer>> localStock=allGapInfoMap.get("1");
			//本地运输
			Map<String,Map<String,Integer>> localTrans=allGapInfoMap.get("2");
			//CN仓
			Map<String,Map<String,Integer>> cnTrans=allGapInfoMap.get("3");
			//PO
			Map<String,Map<String,Integer>> productingMap=allGapInfoMap.get("4");
			
			List<String> countryList=Lists.newArrayList("de","fr","uk","it","es");
			List<String> allCountry=Lists.newArrayList("de","fr","uk","it","es","com","ca","jp");
			 
			Map<String,Map<String,Integer>> forecastMap=Maps.newHashMap();
			Map<String,Integer> amazonWareHouse=Maps.newHashMap();
			Map<String,Map<String,Integer>> weekMap=Maps.newHashMap();
			Map<String,Integer> weekList=Maps.newLinkedHashMap();
			Map<String, String> tip = Maps.newHashMap();
			int num=1;
			while(end.after(start)||end.equals(start)){
					String key = formatWeek.format(start);
					int year =DateUtils.getSunday(start).getYear()+1900;
					int week =  Integer.parseInt(key.substring(4));
					if(week==53){
		                year =DateUtils.getMonday(start).getYear()+1900;
				    }
					if(week<10){
						key = year+"0"+week;
					}else{
						key =year+""+week;
					}
					
					if(weekList.size()>16){
						break;
					}else{
						weekList.put(key,num++);
						Date first = DateUtils.getFirstDayOfWeek(year, week);
						tip.put(key,DateUtils.getDate(first,"yyyy-MM-dd")+","+DateUtils.getDate(DateUtils.getSunday(first),"yyyy-MM-dd"));
						start = DateUtils.addWeeks(start, 1);
					}
			}
			
			
			
		   
			//1:周日销 所有 在售产品
			for(Map.Entry<String, List<String>> entry : countryOnSaleNotNewProduct.entrySet()){
				String country=entry.getKey();
				List<String> nameList=entry.getValue();
				if("de".equals(country)){
					for(String name:nameList){
						PsiProduct product=psiProductService.findProductByProductName(name);
						if(product!=null&&"0".equals(product.getHasPower())){//不带电源 泛欧   hasPower 0：不带电源 1：带电源
							PsiInventoryGap gap=new PsiInventoryGap();
							Integer totalSal=0;
							for(String euCountry:countryList){
								if(sale30Map.get(euCountry)!=null&&sale30Map.get(euCountry).get(name)!=null){
									totalSal+=sale30Map.get(euCountry).get(name);
								}
							}
							Integer avgWeekQuantity=MathUtils.roundUp(totalSal/30d)*7;
							Integer period=16;
						
							if(euStockMap!=null&&euStockMap.get(name)!=null&&euStockMap.get(name).getPeriod()!=null){
								period=MathUtils.roundUp(euStockMap.get(name).getPeriod()*1.0/7);
							}
							gap.setNameColor(name);
							gap.setCountry(country);
							gap.setForecastType("1");
							gap.setType("1");
							gap.setCreateDate(createDate);
							Map<String,Integer> temp=weekMap.get(name+"_"+country);
							if(temp==null){
								temp=Maps.newHashMap();
								weekMap.put(name+"_"+country,temp);
							}
//							for(String week:weekList.keySet()){
							for(Map.Entry<String,Integer> weekEntry:weekList.entrySet()){
								String week = weekEntry.getKey();
								if(weekEntry.getValue()>period){
									setWeekQuantity(weekEntry.getValue(),0,gap);
									temp.put(week,0);
								}else{
									setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
									temp.put(week,avgWeekQuantity);
								}
							}
							
							gapList.add(gap);
							
							//亚马逊仓2
							Map<String,PsiInventoryFba>  amazonInventory=psiInventoryService.getProductFbaInfo(name);
							Integer totalQuantity=0;
							if(amazonInventory.get(name+"_eu")!=null){
								 totalQuantity=amazonInventory.get(name+"_eu").getRealTotal();
							}
							//减fba在途
							Integer quantity=totalQuantity;
							PsiInventoryGap gap2=new PsiInventoryGap();
							gap2.setNameColor(name);
							gap2.setCountry(country);
							gap2.setForecastType("1");
							gap2.setType("2");
							gap2.setCreateDate(createDate);
							
							for(int i=1;i<=16;i++){
								if(i==1){
									gap2.setWeek1(quantity);
								}else{
									setWeekQuantity(i,0,gap2);
								}
							}
							gapList.add(gap2);
							amazonWareHouse.put(name+"_eu", quantity);
							//亚马逊仓
							
							//亚马逊仓在途3
							if(fbaTransMap!=null&&fbaTransMap.size()>0){
								Map<String,Integer> fabTrans=fbaTransMap.get(name+"_eu");
								if(fabTrans!=null&&fabTrans.size()>0){
									PsiInventoryGap tranGap=new PsiInventoryGap();
									tranGap.setNameColor(name);
									tranGap.setCountry(country);
									tranGap.setForecastType("1");
									tranGap.setType("3");
									tranGap.setCreateDate(createDate);
//									for(String w:fabTrans.keySet()){
									for(Map.Entry<String,Integer> wEntry : fabTrans.entrySet()){
										Integer c=wEntry.getValue();
										if(c!=null&&c<=16){
											setWeekQuantity(c,wEntry.getValue(),tranGap);
										}
									}
									gapList.add(tranGap);
								}
							}
							//亚马逊仓在途
							
							//本地仓
							setOthereInfo(localStock,name,"eu",weekList,createDate,gapList,"4","1");
							//本地运输
							setOthereInfo(localTrans,name,"eu",weekList,createDate,gapList,"5","1");
							//CN仓
							setOthereInfo(cnTrans,name,"eu",weekList,createDate,gapList,"6","1");
							//PO
							setOthereInfo(productingMap,name,"eu",weekList,createDate,gapList,"7","1");
						
						}else if(product!=null){//带电源
							setAllInfo(weekMap,gapList,name,country,createDate,sale30Map,amazonStock,totalFbaTrans,fbaTransMap,weekList);
							//亚马逊仓2
							Integer totalQuantity=0;
							if(amazonStock.get(name+"_"+country)!=null){
								 totalQuantity=amazonStock.get(name+"_"+country).getRealTotal();
							}
							//减fba在途
							Integer quantity=totalQuantity;
							PsiInventoryGap gap2=new PsiInventoryGap();
							gap2.setNameColor(name);
							gap2.setCountry(country);
							gap2.setForecastType("1");
							gap2.setType("2");
							gap2.setCreateDate(createDate);
							for(int i=1;i<=16;i++){
								if(i==1){
									gap2.setWeek1(quantity);
								}else{
									setWeekQuantity(i,0,gap2);
								}
							}
							gapList.add(gap2);
							amazonWareHouse.put(name+"_"+country, quantity);
							//亚马逊仓
							//本地仓
							setOthereInfo(localStock,name,country,weekList,createDate,gapList,"4","1");
							//本地运输
							setOthereInfo(localTrans,name,country,weekList,createDate,gapList,"5","1");
							//CN仓
							setOthereInfo(cnTrans,name,country,weekList,createDate,gapList,"6","1");
							//PO
							setOthereInfo(productingMap,name,country,weekList,createDate,gapList,"7","1");
						}
					}
				}else if("fr,it,es,uk".contains(country)){
					for(String name:nameList){
						PsiProduct product=psiProductService.findProductByProductName(name);
						if(product!=null&&"1".equals(product.getHasPower())){//带电源的
							setAllInfo(weekMap,gapList,name,country,createDate,sale30Map,amazonStock,totalFbaTrans,fbaTransMap,weekList);
							//亚马逊仓2
							Integer totalQuantity=0;
							if(amazonStock.get(name+"_"+country)!=null){
								 totalQuantity=amazonStock.get(name+"_"+country).getRealTotal();
							}
							//减fba在途
							Integer quantity=totalQuantity;
							PsiInventoryGap gap2=new PsiInventoryGap();
							gap2.setNameColor(name);
							gap2.setCountry(country);
							gap2.setForecastType("1");
							gap2.setType("2");
							gap2.setCreateDate(createDate);
							for(int i=1;i<=16;i++){
								if(i==1){
									gap2.setWeek1(quantity);
								}else{
									setWeekQuantity(i,0,gap2);
								}
							}
							gapList.add(gap2);
							amazonWareHouse.put(name+"_"+country, quantity);
							//亚马逊仓
							//本地仓
							setOthereInfo(localStock,name,country,weekList,createDate,gapList,"4","1");
							//本地运输
							setOthereInfo(localTrans,name,country,weekList,createDate,gapList,"5","1");
							//CN仓
							setOthereInfo(cnTrans,name,country,weekList,createDate,gapList,"6","1");
							//PO
							setOthereInfo(productingMap,name,country,weekList,createDate,gapList,"7","1");
						}
					}
				}else{
					for(String name:nameList){
						setAllInfo(weekMap,gapList,name,country,createDate,sale30Map,amazonStock,totalFbaTrans,fbaTransMap,weekList);
						//亚马逊仓2
						Integer totalQuantity=0;
						if(amazonStock.get(name+"_"+country)!=null){
							 totalQuantity=amazonStock.get(name+"_"+country).getRealTotal();
						}
						//减fba在途
						Integer quantity=totalQuantity;
						PsiInventoryGap gap2=new PsiInventoryGap();
						gap2.setNameColor(name);
						gap2.setCountry(country);
						gap2.setForecastType("1");
						gap2.setType("2");
						gap2.setCreateDate(createDate);
						for(int i=1;i<=16;i++){
							if(i==1){
								gap2.setWeek1(quantity);
							}else{
								setWeekQuantity(i,0,gap2);
							}
						}
						gapList.add(gap2);
						amazonWareHouse.put(name+"_"+country, quantity);
						//亚马逊仓
						//本地仓
						setOthereInfo(localStock,name,country,weekList,createDate,gapList,"4","1");
						//本地运输
						setOthereInfo(localTrans,name,country,weekList,createDate,gapList,"5","1");
						//CN仓
						setOthereInfo(cnTrans,name,country,weekList,createDate,gapList,"6","1");
						//PO
						setOthereInfo(productingMap,name,country,weekList,createDate,gapList,"7","1");
					}
				}
			}
			
			
			//1:周日销 所有 在售产品
//					for(String country:isSaleProduct.keySet()){
					for(Map.Entry<String, List<String>> entry : countryOnSaleNotNewProduct.entrySet()){
						String country=entry.getKey();
						List<String> nameList=entry.getValue();
						if("de".equals(country)){
							for(String name:nameList){
								PsiProduct product=psiProductService.findProductByProductName(name);
								if(product!=null&&"0".equals(product.getHasPower())){//不带电源 泛欧   hasPower 0：不带电源 1：带电源
									//Amazon仓balance
									PsiInventoryGap gap=new PsiInventoryGap();
									gap.setNameColor(name);
									gap.setCountry(country);
									gap.setForecastType("1");
									gap.setType("8");
									gap.setCreateDate(createDate);
									//Amazon仓balance
									
									//FBA在途balance
									PsiInventoryGap gap1=new PsiInventoryGap();
									gap1.setNameColor(name);
									gap1.setCountry(country);
									gap1.setForecastType("1");
									gap1.setType("9");
									gap1.setCreateDate(createDate);
									//FBA在途balance
									
									//本地仓balance
									PsiInventoryGap gap2=new PsiInventoryGap();
									gap2.setNameColor(name);
									gap2.setCountry(country);
									gap2.setForecastType("1");
									gap2.setType("10");
									gap2.setCreateDate(createDate);
									//本地仓balance
									
									PsiInventoryGap gap3=new PsiInventoryGap();
									gap3.setNameColor(name);
									gap3.setCountry(country);
									gap3.setForecastType("1");
									gap3.setType("11");
									gap3.setCreateDate(createDate);
									
									PsiInventoryGap gap4=new PsiInventoryGap();
									gap4.setNameColor(name);
									gap4.setCountry(country);
									gap4.setForecastType("1");
									gap4.setType("12");
									gap4.setCreateDate(createDate);
									
									PsiInventoryGap gap5=new PsiInventoryGap();
									gap5.setNameColor(name);
									gap5.setCountry(country);
									gap5.setForecastType("1");
									gap5.setType("13");
									gap5.setCreateDate(createDate);
									
									Integer totalSal=0;
									for(String euCountry:countryList){
										if(sale30Map.get(euCountry)!=null&&sale30Map.get(euCountry).get(name)!=null){
											totalSal+=sale30Map.get(euCountry).get(name);
										}
									}
									//Integer avgWeekQuantity=MathUtils.roundUp(totalSal/30d)*7;
									Map<String,Integer> fabTrans=fbaTransMap.get(name+"_eu");
									//本地仓
									Map<String,Integer> localWareHouseTrans=localStock.get(name+"_eu");
									
									Map<String,Integer> localTransQuantity=localTrans.get(name+"_eu");//本地运输
									Map<String,Integer> cnTransQuantity=cnTrans.get(name+"_eu");//CN仓
									Map<String,Integer> productingQuantity=productingMap.get(name+"_eu");//PO
									
									Integer amazonBalance=0;
									Integer fbaTransBalance=0;
									Integer localTransBalance=0;
									Integer transBalance=0;
									Integer cnTransBalance=0;
									Integer productingBalance=0;
//									for(String week:weekList.keySet()){
									for(Map.Entry<String,Integer> weekEntry:weekList.entrySet()){
										String week = weekEntry.getKey();
										int i=weekEntry.getValue();
										int total=0;
										Integer avgWeekQuantity=0;
										if(weekMap!=null&&weekMap.get(name+"_"+country)!=null&&weekMap.get(name+"_"+country).get(week)!=null){
											avgWeekQuantity=weekMap.get(name+"_"+country).get(week);
										}
										
										if(i==1&&amazonWareHouse!=null){
											//Amazon仓balance
											amazonBalance=(amazonWareHouse.get(name+"_eu")==null?0:amazonWareHouse.get(name+"_eu"))-avgWeekQuantity;
											setWeekQuantity(i,amazonBalance,gap);
											total+=(amazonWareHouse.get(name+"_eu")==null?0:amazonWareHouse.get(name+"_eu"));
											//FBA在途balance
											if(fabTrans!=null&&fabTrans.size()>0&&fabTrans.get(week)!=null){
												total+=fabTrans.get(week);
												fbaTransBalance=total-avgWeekQuantity;
												setWeekQuantity(i,fbaTransBalance,gap1);
											}else{
												fbaTransBalance=total-avgWeekQuantity;
												setWeekQuantity(i,fbaTransBalance,gap1);
											}
											//本地仓balance
											if(localWareHouseTrans!=null&&localWareHouseTrans.size()>0&&localWareHouseTrans.get(week)!=null){
												total+=localWareHouseTrans.get(week);
												localTransBalance=total-avgWeekQuantity;
												setWeekQuantity(i,localTransBalance,gap2);
											}else{
												localTransBalance=total-avgWeekQuantity;
												setWeekQuantity(i,localTransBalance,gap2);
											}
											
											//本地运输balance
											if(localTransQuantity!=null&&localTransQuantity.size()>0&&localTransQuantity.get(week)!=null){
												total+=localTransQuantity.get(week);
												transBalance=total-avgWeekQuantity;
												setWeekQuantity(i,transBalance,gap3);
											}else{
												transBalance=total-avgWeekQuantity;
												setWeekQuantity(i,transBalance,gap3);
											}
											
											//cn仓balance
											if(cnTransQuantity!=null&&cnTransQuantity.size()>0&&cnTransQuantity.get(week)!=null){
												total+=cnTransQuantity.get(week);
												cnTransBalance=total-avgWeekQuantity;
												setWeekQuantity(i,cnTransBalance,gap4);
											}else{
												cnTransBalance=total-avgWeekQuantity;
												setWeekQuantity(i,cnTransBalance,gap4);
											}
											
											//PO balance
											if(productingQuantity!=null&&productingQuantity.size()>0&&productingQuantity.get(week)!=null){
												total+=productingQuantity.get(week);
												productingBalance=total-avgWeekQuantity;
												setWeekQuantity(i,productingBalance,gap5);
											}else{
												productingBalance=total-avgWeekQuantity;
												setWeekQuantity(i,productingBalance,gap5);
											}
											
										}else if(amazonWareHouse!=null){
											//Amazon仓balance
											amazonBalance=amazonBalance-avgWeekQuantity;
											setWeekQuantity(i,amazonBalance,gap);
											
							                 //FBA在途balance
											if(fabTrans!=null&&fabTrans.size()>0&&fabTrans.get(week)!=null){
												total+=fabTrans.get(week);
												fbaTransBalance=fbaTransBalance+total-avgWeekQuantity;
												setWeekQuantity(i,fbaTransBalance,gap1);
											}else{
												fbaTransBalance=fbaTransBalance+total-avgWeekQuantity;
												setWeekQuantity(i,fbaTransBalance,gap1);
											}
											//本地仓balance
											if(localWareHouseTrans!=null&&localWareHouseTrans.size()>0&&localWareHouseTrans.get(week)!=null){
												total+=localWareHouseTrans.get(week);
												localTransBalance=localTransBalance+total-avgWeekQuantity;
												setWeekQuantity(i,localTransBalance,gap2);
											}else{
												localTransBalance=localTransBalance+total-avgWeekQuantity;
												setWeekQuantity(i,localTransBalance,gap2);
											}
											
											//本地运输balance
											if(localTransQuantity!=null&&localTransQuantity.size()>0&&localTransQuantity.get(week)!=null){
												total+=localTransQuantity.get(week);
												transBalance=transBalance+total-avgWeekQuantity;
												setWeekQuantity(i,transBalance,gap3);
											}else{
												transBalance=transBalance+total-avgWeekQuantity;
												setWeekQuantity(i,transBalance,gap3);
											}
											
											//cn仓balance
											if(cnTransQuantity!=null&&cnTransQuantity.size()>0&&cnTransQuantity.get(week)!=null){
												total+=cnTransQuantity.get(week);
												cnTransBalance=cnTransBalance+total-avgWeekQuantity;
												setWeekQuantity(i,cnTransBalance,gap4);
											}else{
												cnTransBalance=cnTransBalance+total-avgWeekQuantity;
												setWeekQuantity(i,cnTransBalance,gap4);
											}
											
											//PO balance
											if(productingQuantity!=null&&productingQuantity.size()>0&&productingQuantity.get(week)!=null){
												total+=productingQuantity.get(week);
												productingBalance=productingBalance+total-avgWeekQuantity;
												setWeekQuantity(i,productingBalance,gap5);
											}else{
												productingBalance=productingBalance+total-avgWeekQuantity;
												setWeekQuantity(i,productingBalance,gap5);
											}
											
										}
									
									}
									gapList.add(gap);
									gapList.add(gap1);
									gapList.add(gap2);
									gapList.add(gap3);
									gapList.add(gap4);
									gapList.add(gap5);
								}else if(product!=null){//带电源
									 if(amazonWareHouse!=null){
										setBalanceInfo(fbaTransMap,localStock,localTrans,cnTrans,productingMap,
												amazonWareHouse,sale30Map,name,country,weekList,createDate,gapList);
									 }	
								}
							}
						}else if("fr,it,es,uk".contains(country)){
							for(String name:nameList){
								PsiProduct product=psiProductService.findProductByProductName(name);
								if(product!=null&&"1".equals(product.getHasPower())){//带电源的
									 if(amazonWareHouse!=null){
									setBalanceInfo(fbaTransMap,localStock,localTrans,cnTrans,productingMap,
											amazonWareHouse,sale30Map,name,country,weekList,createDate,gapList);
									 }
								}
							}
						}else{
							for(String name:nameList){
								 if(amazonWareHouse!=null){
								setBalanceInfo(fbaTransMap,localStock,localTrans,cnTrans,productingMap,
										amazonWareHouse,sale30Map,name,country,weekList,createDate,gapList);
								 }
							}
						}
					}
			
					
					//1:周日销 所有 在售产品
//					for(String country:isSaleProduct.keySet()){
					for(Map.Entry<String, List<String>> entry : countryOnSaleNotNewProduct.entrySet()){
						String country=entry.getKey();
						List<String> nameList=entry.getValue();
						if("de".equals(country)){
							for(String name:nameList){
								PsiProduct product=psiProductService.findProductByProductName(name);
								if(product!=null&&"0".equals(product.getHasPower())){//不带电源 泛欧   hasPower 0：不带电源 1：带电源
									PsiInventoryGap gap=new PsiInventoryGap();
									Integer totalSal=0;
									for(String euCountry:countryList){
										if(sale30Map.get(euCountry)!=null&&sale30Map.get(euCountry).get(name)!=null){
											totalSal+=sale30Map.get(euCountry).get(name);
										}
									}
									Integer avgWeekQuantity=MathUtils.roundUp(totalSal/30d)*7;
									Integer period=16;
									if(euStockMap!=null&&euStockMap.get(name)!=null&&euStockMap.get(name).getPeriod()!=null){
										period=MathUtils.roundUp(euStockMap.get(name).getPeriod()*1.0/7);
									}
									gap.setNameColor(name);
									gap.setCountry(country);
									gap.setForecastType("2");
									gap.setType("1");
									gap.setCreateDate(createDate);
									for(int i=1;i<=16;i++){
										//setWeekQuantity(i,avgWeekQuantity,gap);
										if(i>period){
											setWeekQuantity(i,0,gap);
										}else{
											setWeekQuantity(i,avgWeekQuantity,gap);
										}
									}
									gapList.add(gap);
									
									//亚马逊仓2
									Map<String,PsiInventoryFba>  amazonInventory=psiInventoryService.getProductFbaInfo(name);
									Integer totalQuantity=0;
									if(amazonInventory.get(name+"_eu")!=null){
										 totalQuantity=amazonInventory.get(name+"_eu").getRealTotal();
									}
									//减fba在途
									Integer quantity=totalQuantity;
									PsiInventoryGap gap2=new PsiInventoryGap();
									gap2.setNameColor(name);
									gap2.setCountry(country);
									gap2.setForecastType("2");
									gap2.setType("2");
									gap2.setCreateDate(createDate);
									
									for(int i=1;i<=16;i++){
										if(i==1){
											gap2.setWeek1(quantity);
										}else{
											setWeekQuantity(i,0,gap2);
										}
									}
									gapList.add(gap2);
									amazonWareHouse.put(name+"_eu", quantity);
									//亚马逊仓
									
									//亚马逊仓在途3
									if(fbaTransMap!=null&&fbaTransMap.size()>0){
										Map<String,Integer> fabTrans=fbaTransMap.get(name+"_eu");
										if(fabTrans!=null&&fabTrans.size()>0){
											PsiInventoryGap tranGap=new PsiInventoryGap();
											tranGap.setNameColor(name);
											tranGap.setCountry(country);
											tranGap.setForecastType("2");
											tranGap.setType("3");
											tranGap.setCreateDate(createDate);
//											for(String w:fabTrans.keySet()){
											for(Map.Entry<String,Integer> wEntry : fabTrans.entrySet()){
												String w = wEntry.getKey();
												Integer c=wEntry.getValue();
												if(c!=null&&c<=16){
													setWeekQuantity(c,wEntry.getValue(),tranGap);
												}
											}
											gapList.add(tranGap);
										}
									}
									//亚马逊仓在途
									
									//本地仓
									setOthereInfo(localStock,name,"eu",weekList,createDate,gapList,"4","2");
									//本地运输
									setOthereInfo(localTrans,name,"eu",weekList,createDate,gapList,"5","2");
									//CN仓
									setOthereInfo(cnTrans,name,"eu",weekList,createDate,gapList,"6","2");
									//PO
									setOthereInfo(productingMap,name,"eu",weekList,createDate,gapList,"7","2");
								
								}else if(product!=null){//带电源
									setAllInfo2(gapList,name,country,createDate,sale30Map,amazonStock,totalFbaTrans,fbaTransMap,weekList);
									//亚马逊仓2
									Integer totalQuantity=0;
									if(amazonStock.get(name+"_"+country)!=null){
										 totalQuantity=amazonStock.get(name+"_"+country).getRealTotal();
									}
									//减fba在途
									Integer quantity=totalQuantity;
									PsiInventoryGap gap2=new PsiInventoryGap();
									gap2.setNameColor(name);
									gap2.setCountry(country);
									gap2.setForecastType("2");
									gap2.setType("2");
									gap2.setCreateDate(createDate);
									for(int i=1;i<=16;i++){
										if(i==1){
											gap2.setWeek1(quantity);
										}else{
											setWeekQuantity(i,0,gap2);
										}
									}
									gapList.add(gap2);
									amazonWareHouse.put(name+"_"+country, quantity);
									//亚马逊仓
									//本地仓
									setOthereInfo(localStock,name,country,weekList,createDate,gapList,"4","2");
									//本地运输
									setOthereInfo(localTrans,name,country,weekList,createDate,gapList,"5","2");
									//CN仓
									setOthereInfo(cnTrans,name,country,weekList,createDate,gapList,"6","2");
									//PO
									setOthereInfo(productingMap,name,country,weekList,createDate,gapList,"7","2");
								}
							}
						}else if("fr,it,es,uk".contains(country)){
							for(String name:nameList){
								PsiProduct product=psiProductService.findProductByProductName(name);
								if(product!=null&&"1".equals(product.getHasPower())){//带电源的
									setAllInfo2(gapList,name,country,createDate,sale30Map,amazonStock,totalFbaTrans,fbaTransMap,weekList);
									//亚马逊仓2
									Integer totalQuantity=0;
									if(amazonStock.get(name+"_"+country)!=null){
										 totalQuantity=amazonStock.get(name+"_"+country).getRealTotal();
									}
									//减fba在途
									Integer quantity=totalQuantity;
									PsiInventoryGap gap2=new PsiInventoryGap();
									gap2.setNameColor(name);
									gap2.setCountry(country);
									gap2.setForecastType("2");
									gap2.setType("2");
									gap2.setCreateDate(createDate);
									for(int i=1;i<=16;i++){
										if(i==1){
											gap2.setWeek1(quantity);
										}else{
											setWeekQuantity(i,0,gap2);
										}
									}
									gapList.add(gap2);
									amazonWareHouse.put(name+"_"+country, quantity);
									//亚马逊仓
									//本地仓
									setOthereInfo(localStock,name,country,weekList,createDate,gapList,"4","2");
									//本地运输
									setOthereInfo(localTrans,name,country,weekList,createDate,gapList,"5","2");
									//CN仓
									setOthereInfo(cnTrans,name,country,weekList,createDate,gapList,"6","2");
									//PO
									setOthereInfo(productingMap,name,country,weekList,createDate,gapList,"7","2");
								}
							}
						}else{
							for(String name:nameList){
								setAllInfo2(gapList,name,country,createDate,sale30Map,amazonStock,totalFbaTrans,fbaTransMap,weekList);
								//亚马逊仓2
								Integer totalQuantity=0;
								if(amazonStock.get(name+"_"+country)!=null){
									 totalQuantity=amazonStock.get(name+"_"+country).getRealTotal();
								}
								//减fba在途
								Integer quantity=totalQuantity;
								PsiInventoryGap gap2=new PsiInventoryGap();
								gap2.setNameColor(name);
								gap2.setCountry(country);
								gap2.setForecastType("2");
								gap2.setType("2");
								gap2.setCreateDate(createDate);
								for(int i=1;i<=16;i++){
									if(i==1){
										gap2.setWeek1(quantity);
									}else{
										setWeekQuantity(i,0,gap2);
									}
								}
								gapList.add(gap2);
								amazonWareHouse.put(name+"_"+country, quantity);
								//亚马逊仓
								//本地仓
								setOthereInfo(localStock,name,country,weekList,createDate,gapList,"4","2");
								//本地运输
								setOthereInfo(localTrans,name,country,weekList,createDate,gapList,"5","2");
								//CN仓
								setOthereInfo(cnTrans,name,country,weekList,createDate,gapList,"6","2");
								//PO
								setOthereInfo(productingMap,name,country,weekList,createDate,gapList,"7","2");
							}
						}
					}
					
					//1:周日销 所有 在售产品  -安全库存
//					for(String country:isSaleProduct.keySet()){
					for(Map.Entry<String, List<String>> entry : countryOnSaleNotNewProduct.entrySet()){
						String country=entry.getKey();
						List<String> nameList=entry.getValue();
						Map<String,PsiProductInStock> stockMap=psiProductInStockService.getHistoryInventory(country);
						if("de".equals(country)){
							for(String name:nameList){
								PsiProduct product=psiProductService.findProductByProductName(name);
								if(product!=null&&"0".equals(product.getHasPower())){//不带电源 泛欧   hasPower 0：不带电源 1：带电源
								
									//Amazon仓balance
									PsiInventoryGap gap=new PsiInventoryGap();
									gap.setNameColor(name);
									gap.setCountry(country);
									gap.setForecastType("2");
									gap.setType("8");
									gap.setCreateDate(createDate);
									//Amazon仓balance
									
									//FBA在途balance
									PsiInventoryGap gap1=new PsiInventoryGap();
									gap1.setNameColor(name);
									gap1.setCountry(country);
									gap1.setForecastType("2");
									gap1.setType("9");
									gap1.setCreateDate(createDate);
									//FBA在途balance
									
									//本地仓balance
									PsiInventoryGap gap2=new PsiInventoryGap();
									gap2.setNameColor(name);
									gap2.setCountry(country);
									gap2.setForecastType("2");
									gap2.setType("10");
									gap2.setCreateDate(createDate);
									//本地仓balance
									
									PsiInventoryGap gap3=new PsiInventoryGap();
									gap3.setNameColor(name);
									gap3.setCountry(country);
									gap3.setForecastType("2");
									gap3.setType("11");
									gap3.setCreateDate(createDate);
									
									PsiInventoryGap gap4=new PsiInventoryGap();
									gap4.setNameColor(name);
									gap4.setCountry(country);
									gap4.setForecastType("2");
									gap4.setType("12");
									gap4.setCreateDate(createDate);
									
									PsiInventoryGap gap5=new PsiInventoryGap();
									gap5.setNameColor(name);
									gap5.setCountry(country);
									gap5.setForecastType("2");
									gap5.setType("13");
									gap5.setCreateDate(createDate);
									
									Integer totalSal=0;
									for(String euCountry:countryList){
										if(sale30Map.get(euCountry)!=null&&sale30Map.get(euCountry).get(name)!=null){
											totalSal+=sale30Map.get(euCountry).get(name);
										}
									}
									Integer safeInventory=0;
									if(euStockMap!=null&&euStockMap.get(name)!=null){
										safeInventory=MathUtils.roundUp(euStockMap.get(name).getSafeInventory());
									}
									//Integer avgWeekQuantity=MathUtils.roundUp(totalSal/30d)*7+safeInventory;
									Map<String,Integer> fabTrans=fbaTransMap.get(name+"_eu");
									//本地仓
									Map<String,Integer> localWareHouseTrans=localStock.get(name+"_eu");
									
									Map<String,Integer> localTransQuantity=localTrans.get(name+"_eu");//本地运输
									Map<String,Integer> cnTransQuantity=cnTrans.get(name+"_eu");//CN仓
									Map<String,Integer> productingQuantity=productingMap.get(name+"_eu");//PO
									
									Integer amazonBalance=0;
									Integer fbaTransBalance=0;
									Integer localTransBalance=0;
									Integer transBalance=0;
									Integer cnTransBalance=0;
									Integer productingBalance=0;
//									for(String week:weekList.keySet()){
									for(Map.Entry<String,Integer> weekEntry:weekList.entrySet()){
										String week = weekEntry.getKey();
										int i=weekEntry.getValue();
										int total=0;
										Integer avgWeekQuantity=0;
										if(weekMap!=null&&weekMap.get(name+"_"+country)!=null&&weekMap.get(name+"_"+country).get(week)!=null){
											avgWeekQuantity=weekMap.get(name+"_"+country).get(week);
										}
										//avgWeekQuantity=avgWeekQuantity+safeInventory;
										if(i==1&&amazonWareHouse!=null){
											//Amazon仓balance
											amazonBalance=(amazonWareHouse.get(name+"_eu")==null?0:amazonWareHouse.get(name+"_eu"))-avgWeekQuantity;
											setWeekQuantity(i,amazonBalance-safeInventory,gap);
											total+=(amazonWareHouse.get(name+"_eu")==null?0:amazonWareHouse.get(name+"_eu"));
											//FBA在途balance
											if(fabTrans!=null&&fabTrans.size()>0&&fabTrans.get(week)!=null){
												total+=fabTrans.get(week);
												fbaTransBalance=total-avgWeekQuantity;
												setWeekQuantity(i,fbaTransBalance-safeInventory,gap1);
											}else{
												fbaTransBalance=total-avgWeekQuantity;
												setWeekQuantity(i,fbaTransBalance-safeInventory,gap1);
											}
											//本地仓balance
											if(localWareHouseTrans!=null&&localWareHouseTrans.size()>0&&localWareHouseTrans.get(week)!=null){
												total+=localWareHouseTrans.get(week);
												localTransBalance=total-avgWeekQuantity;
												setWeekQuantity(i,localTransBalance-safeInventory,gap2);
											}else{
												localTransBalance=total-avgWeekQuantity;
												setWeekQuantity(i,localTransBalance-safeInventory,gap2);
											}
											
											//本地运输balance
											if(localTransQuantity!=null&&localTransQuantity.size()>0&&localTransQuantity.get(week)!=null){
												total+=localTransQuantity.get(week);
												transBalance=total-avgWeekQuantity;
												setWeekQuantity(i,transBalance-safeInventory,gap3);
											}else{
												transBalance=total-avgWeekQuantity;
												setWeekQuantity(i,transBalance-safeInventory,gap3);
											}
											
											//cn仓balance
											if(cnTransQuantity!=null&&cnTransQuantity.size()>0&&cnTransQuantity.get(week)!=null){
												total+=cnTransQuantity.get(week);
												cnTransBalance=total-avgWeekQuantity;
												setWeekQuantity(i,cnTransBalance-safeInventory,gap4);
											}else{
												cnTransBalance=total-avgWeekQuantity;
												setWeekQuantity(i,cnTransBalance-safeInventory,gap4);
											}
											
											//PO balance
											if(productingQuantity!=null&&productingQuantity.size()>0&&productingQuantity.get(week)!=null){
												total+=productingQuantity.get(week);
												productingBalance=total-avgWeekQuantity;
												setWeekQuantity(i,productingBalance-safeInventory,gap5);
											}else{
												productingBalance=total-avgWeekQuantity;
												setWeekQuantity(i,productingBalance-safeInventory,gap5);
											}
											
										}else if(amazonWareHouse!=null){
											//Amazon仓balance
											amazonBalance=amazonBalance-avgWeekQuantity;
											setWeekQuantity(i,amazonBalance-safeInventory,gap);
											
							                 //FBA在途balance
											if(fabTrans!=null&&fabTrans.size()>0&&fabTrans.get(week)!=null){
												total+=fabTrans.get(week);
												fbaTransBalance=fbaTransBalance+total-avgWeekQuantity;
												setWeekQuantity(i,fbaTransBalance-safeInventory,gap1);
											}else{
												fbaTransBalance=fbaTransBalance+total-avgWeekQuantity;
												setWeekQuantity(i,fbaTransBalance-safeInventory,gap1);
											}
											//本地仓balance
											if(localWareHouseTrans!=null&&localWareHouseTrans.size()>0&&localWareHouseTrans.get(week)!=null){
												total+=localWareHouseTrans.get(week);
												localTransBalance=localTransBalance+total-avgWeekQuantity;
												setWeekQuantity(i,localTransBalance-safeInventory,gap2);
											}else{
												localTransBalance=localTransBalance+total-avgWeekQuantity;
												setWeekQuantity(i,localTransBalance-safeInventory,gap2);
											}
											
											//本地运输balance
											if(localTransQuantity!=null&&localTransQuantity.size()>0&&localTransQuantity.get(week)!=null){
												total+=localTransQuantity.get(week);
												transBalance=transBalance+total-avgWeekQuantity;
												setWeekQuantity(i,transBalance-safeInventory,gap3);
											}else{
												transBalance=transBalance+total-avgWeekQuantity;
												setWeekQuantity(i,transBalance-safeInventory,gap3);
											}
											
											//cn仓balance
											if(cnTransQuantity!=null&&cnTransQuantity.size()>0&&cnTransQuantity.get(week)!=null){
												total+=cnTransQuantity.get(week);
												cnTransBalance=cnTransBalance+total-avgWeekQuantity;
												setWeekQuantity(i,cnTransBalance-safeInventory,gap4);
											}else{
												cnTransBalance=cnTransBalance+total-avgWeekQuantity;
												setWeekQuantity(i,cnTransBalance-safeInventory,gap4);
											}
											
											//PO balance
											if(productingQuantity!=null&&productingQuantity.size()>0&&productingQuantity.get(week)!=null){
												total+=productingQuantity.get(week);
												productingBalance=productingBalance+total-avgWeekQuantity;
												setWeekQuantity(i,productingBalance-safeInventory,gap5);
											}else{
												productingBalance=productingBalance+total-avgWeekQuantity;
												setWeekQuantity(i,productingBalance-safeInventory,gap5);
											}
											
										}
									
									}
									gapList.add(gap);
									gapList.add(gap1);
									gapList.add(gap2);
									gapList.add(gap3);
									gapList.add(gap4);
									gapList.add(gap5);
								}else if(product!=null){//带电源
								    if(amazonWareHouse!=null){
										setSafeBalanceInfo(weekMap,stockMap,fbaTransMap,localStock,localTrans,cnTrans,productingMap,
												amazonWareHouse,sale30Map,name,country,weekList,createDate,gapList);
								    }
								}
							}
						}else if("fr,it,es,uk".contains(country)){
							for(String name:nameList){
								PsiProduct product=psiProductService.findProductByProductName(name);
								if(product!=null&&"1".equals(product.getHasPower())){//带电源的
								    if(amazonWareHouse!=null){
									setSafeBalanceInfo(weekMap,stockMap,fbaTransMap,localStock,localTrans,cnTrans,productingMap,
											amazonWareHouse,sale30Map,name,country,weekList,createDate,gapList);
								    }
								}
							}
						}else{
							for(String name:nameList){
							    if(amazonWareHouse!=null){
								setSafeBalanceInfo(weekMap,stockMap,fbaTransMap,localStock,localTrans,cnTrans,productingMap,
										amazonWareHouse,sale30Map,name,country,weekList,createDate,gapList);
							    }
							}
						}
					}
					
					//0:销量预测 在售排除新品
					for (String country: allCountry) {
							List<PsiProductEliminate> nameList=psiProductEliminateService.findOnSaleNotNew(country);
							Map<String,PsiProductInStock> stockMap=psiProductInStockService.getHistoryInventory(country);
							if("de".equals(country)){
								for(PsiProductEliminate liminate:nameList){
									String name=liminate.getColorName();
									PsiProduct product=psiProductService.findProductByProductName(name);
									if(product!=null&&"0".equals(product.getHasPower())){//不带电源 泛欧   hasPower 0：不带电源 1：带电源
										PsiInventoryGap gap=new PsiInventoryGap();
										gap.setNameColor(name);
										gap.setCountry(country);
										gap.setForecastType("0");
										gap.setType("1");
										gap.setCreateDate(createDate);
										Map<String,Integer> fMap=forecastMap.get(name+"_"+country);
										if(fMap==null){
											fMap=Maps.newHashMap();
											forecastMap.put(name+"_"+country,fMap);
										}
//										for(String week:weekList.keySet()){
										for(Map.Entry<String,Integer> weekEntry:weekList.entrySet()){
											String week = weekEntry.getKey();
											Double totalSal=0d;
											String section=tip.get(week);
											String[] arr=section.split(",");
											String[] temp1=arr[0].split("-");
											String[] temp2=arr[1].split("-");
											Calendar  cal1=Calendar.getInstance();  
											Calendar  cal2=Calendar.getInstance();  
											try {
												cal1.setTime(formatDay.parse(arr[0].toString()));
												cal2.setTime(formatDay.parse(arr[1].toString()));
											} catch (ParseException e) {
												e.printStackTrace();
											} 
											int day1=cal1.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
											int day2=cal2.getActualMaximum(Calendar.DAY_OF_MONTH);
											
											if(temp1[0].equals(temp2[0])&&temp1[1].equals(temp2[1])){
												for(String euCountry:countryList){
													if(data!=null&&data.get(name)!=null&&data.get(name).get(euCountry)!=null
															&&data.get(name).get(euCountry).get(temp1[0]+"-"+temp1[1])!=null&&data.get(name).get(euCountry).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()!=null){
														totalSal+=(data.get(name).get(euCountry).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()*7.0d/day1);
													}
												}
											}else{
												Integer apart1=day1-Integer.parseInt(temp1[2])+1;//上个月天数
												Integer apart2=7-apart1;//上个月天数
												for(String euCountry:countryList){
													if(data!=null&&data.get(name)!=null&&data.get(name).get(euCountry)!=null
															&&data.get(name).get(euCountry).get(temp1[0]+"-"+temp1[1])!=null&&data.get(name).get(euCountry).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()!=null){
														Integer forecast1=data.get(name).get(euCountry).get(temp1[0]+"-"+temp1[1]).getQuantityForecast();
														Integer forecast2=0;
														if(data.get(name).get(euCountry).get(temp2[0]+"-"+temp2[1])!=null&&data.get(name).get(euCountry).get(temp2[0]+"-"+temp2[1]).getQuantityForecast()!=null){
															forecast2=data.get(name).get(euCountry).get(temp2[0]+"-"+temp2[1]).getQuantityForecast();
														}
														totalSal+=(forecast1*apart1*1.0d/day1+forecast2*apart2*1.0d/day2);
													}
												}
											}
											Integer avgWeekQuantity=MathUtils.roundUp(totalSal);
											Integer period=16;
											if(euStockMap!=null&&euStockMap.get(name)!=null&&euStockMap.get(name).getPeriod()!=null){
												period=MathUtils.roundUp(euStockMap.get(name).getPeriod()*1.0/7);
											}
											if(weekEntry.getValue()>period){
												setWeekQuantity(weekEntry.getValue(),0,gap);
												fMap.put(week,0);
											}else{
												fMap.put(week,avgWeekQuantity);
												setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
											}
											
											
											
										}
										gapList.add(gap);
										
										//亚马逊仓2
										Map<String,PsiInventoryFba>  amazonInventory=psiInventoryService.getProductFbaInfo(name);
										Integer totalQuantity=0;
										if(amazonInventory.get(name+"_eu")!=null){
											 totalQuantity=amazonInventory.get(name+"_eu").getRealTotal();
										}
										//减fba在途
										Integer quantity=totalQuantity;
										PsiInventoryGap gap2=new PsiInventoryGap();
										gap2.setNameColor(name);
										gap2.setCountry(country);
										gap2.setForecastType("0");
										gap2.setType("2");
										gap2.setCreateDate(createDate);
										
										for(int i=1;i<=16;i++){
											if(i==1){
												gap2.setWeek1(quantity);
											}else{
												setWeekQuantity(i,0,gap2);
											}
										}
										gapList.add(gap2);
										//亚马逊仓
										
										//亚马逊仓在途3
										if(fbaTransMap!=null&&fbaTransMap.size()>0){
											Map<String,Integer> fabTrans=fbaTransMap.get(name+"_eu");
											if(fabTrans!=null&&fabTrans.size()>0){
												PsiInventoryGap tranGap=new PsiInventoryGap();
												tranGap.setNameColor(name);
												tranGap.setCountry(country);
												tranGap.setForecastType("0");
												tranGap.setType("3");
												tranGap.setCreateDate(createDate);
//												for(String w:fabTrans.keySet()){
												for(Map.Entry<String,Integer> wEntry : fabTrans.entrySet()){
													Integer c=wEntry.getValue();
													if(c!=null&&c<=16){
														setWeekQuantity(c,wEntry.getValue(),tranGap);
													}
												}
												gapList.add(tranGap);
											}
										}
										//亚马逊仓在途
										
										//本地仓
										setOthereInfo(localStock,name,"eu",weekList,createDate,gapList,"4","0");
										//本地运输
										setOthereInfo(localTrans,name,"eu",weekList,createDate,gapList,"5","0");
										//CN仓
										setOthereInfo(cnTrans,name,"eu",weekList,createDate,gapList,"6","0");
										//PO
										setOthereInfo(productingMap,name,"eu",weekList,createDate,gapList,"7","0");
									}else if(product!=null){
										
										PsiInventoryGap gap=new PsiInventoryGap();
										gap.setNameColor(name);
										gap.setCountry(country);
										gap.setForecastType("0");
										gap.setType("1");
										gap.setCreateDate(createDate);

										Map<String,Integer> fMap=forecastMap.get(name+"_"+country);
										if(fMap==null){
											fMap=Maps.newHashMap();
											forecastMap.put(name+"_"+country,fMap);
										}
//										for(String week:weekList.keySet()){
										for(Map.Entry<String,Integer> weekEntry:weekList.entrySet()){
											String week = weekEntry.getKey();
											Double totalSal=0d;
											String section=tip.get(week);
											String[] arr=section.split(",");
											String[] temp1=arr[0].split("-");
											String[] temp2=arr[1].split("-");
											Calendar  cal1=Calendar.getInstance();  
											Calendar  cal2=Calendar.getInstance();  
											try {
												cal1.setTime(formatDay.parse(arr[0].toString()));
												cal2.setTime(formatDay.parse(arr[1].toString()));
											} catch (ParseException e) {
												e.printStackTrace();
											} 
											int day1=cal1.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
											int day2=cal2.getActualMaximum(Calendar.DAY_OF_MONTH);
											
											if(temp1[0].equals(temp2[0])&&temp1[1].equals(temp2[1])){
													if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null
															&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1])!=null&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()!=null){
														totalSal=data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()*7.0d/day1;
												    }
											}else{
												//Integer apart1=Integer.parseInt(temp1[2])-day1+1;//上个月天数
												Integer apart1=day1-Integer.parseInt(temp1[2])+1;//上个月天数
												Integer apart2=7-apart1;//上个月天数
												//for(String euCountry:countryList){
													if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null
															&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1])!=null&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()!=null){
														Integer forecast1=data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast();
														Integer forecast2=0;
														if(data.get(name).get(country).get(temp2[0]+"-"+temp2[1])!=null&&data.get(name).get(country).get(temp2[0]+"-"+temp2[1]).getQuantityForecast()!=null){
															forecast2=data.get(name).get(country).get(temp2[0]+"-"+temp2[1]).getQuantityForecast();
														}
														totalSal+=(forecast1*apart1*1.0d/day1+forecast2*apart2*1.0d/day2);
													}
												//}
											}
											Integer avgWeekQuantity=MathUtils.roundUp(totalSal);
											
											Integer period=16;
											if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).getPeriod()!=null){
												period=MathUtils.roundUp(stockMap.get(name).getPeriod()*1.0/7);
											}
											if(weekEntry.getValue()>period){
												setWeekQuantity(weekEntry.getValue(),0,gap);
												fMap.put(week,0);
											}else{
												//setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
												setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
												fMap.put(week,avgWeekQuantity);
											}
											
											
										}
										gapList.add(gap);
										gapList.add(gap);
										

										//亚马逊仓在途3
										if(fbaTransMap!=null&&fbaTransMap.size()>0){
											Map<String,Integer> fabTrans=fbaTransMap.get(name+"_"+country);
											if(fabTrans!=null&&fabTrans.size()>0){
												PsiInventoryGap tranGap=new PsiInventoryGap();
												tranGap.setNameColor(name);
												tranGap.setCountry(country);
												tranGap.setForecastType("0");
												tranGap.setType("3");
												tranGap.setCreateDate(createDate);
												for(Map.Entry<String,Integer> wEntry:fabTrans.entrySet()){
													Integer c=wEntry.getValue();
													if(c!=null&&c<=16){
														setWeekQuantity(c,wEntry.getValue(),tranGap);
													}
												}
												gapList.add(tranGap);
											}
										}
										//亚马逊仓在途
										
										//亚马逊仓2
										Integer totalQuantity=0;
										if(amazonStock.get(name+"_"+country)!=null){
											 totalQuantity=amazonStock.get(name+"_"+country).getRealTotal();
										}
										//减fba在途
										Integer quantity=totalQuantity;
										PsiInventoryGap gap2=new PsiInventoryGap();
										gap2.setNameColor(name);
										gap2.setCountry(country);
										gap2.setForecastType("0");
										gap2.setType("2");
										gap2.setCreateDate(createDate);
										for(int i=1;i<=16;i++){
											if(i==1){
												gap2.setWeek1(quantity);
											}else{
												setWeekQuantity(i,0,gap2);
											}
										}
										gapList.add(gap2);
										
										//亚马逊仓
										//本地仓
										setOthereInfo(localStock,name,country,weekList,createDate,gapList,"4","0");
										//本地运输
										setOthereInfo(localTrans,name,country,weekList,createDate,gapList,"5","0");
										//CN仓
										setOthereInfo(cnTrans,name,country,weekList,createDate,gapList,"6","0");
										//PO
										setOthereInfo(productingMap,name,country,weekList,createDate,gapList,"7","0");
									}
								}
							}else if("fr,it,es,uk".contains(country)){
								for(PsiProductEliminate liminate:nameList){
									String name=liminate.getColorName();
									PsiProduct product=psiProductService.findProductByProductName(name);
									if(product!=null&&"1".equals(product.getHasPower())){//带电源的
									
										PsiInventoryGap gap=new PsiInventoryGap();
										gap.setNameColor(name);
										gap.setCountry(country);
										gap.setForecastType("0");
										gap.setType("1");
										gap.setCreateDate(createDate);
										Map<String,Integer> fMap=forecastMap.get(name+"_"+country);
										if(fMap==null){
											fMap=Maps.newHashMap();
											forecastMap.put(name+"_"+country,fMap);
										}
//										for(String week:weekList.keySet()){
										for(Map.Entry<String,Integer> weekEntry:weekList.entrySet()){
											String week = weekEntry.getKey();
											Double totalSal=0d;
											String section=tip.get(week);
											String[] arr=section.split(",");
											String[] temp1=arr[0].split("-");
											String[] temp2=arr[1].split("-");
											Calendar  cal1=Calendar.getInstance();  
											Calendar  cal2=Calendar.getInstance();  
											try {
												cal1.setTime(formatDay.parse(arr[0].toString()));
												cal2.setTime(formatDay.parse(arr[1].toString()));
											} catch (ParseException e) {
												e.printStackTrace();
											} 
											int day1=cal1.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
											int day2=cal2.getActualMaximum(Calendar.DAY_OF_MONTH);
											
											if(temp1[0].equals(temp2[0])&&temp1[1].equals(temp2[1])){
													if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null
															&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1])!=null&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()!=null){
														totalSal=data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()*7.0d/day1;
												    }
											}else{
												//Integer apart1=Integer.parseInt(temp1[2])-day1+1;//上个月天数
												Integer apart1=day1-Integer.parseInt(temp1[2])+1;//上个月天数
												Integer apart2=7-apart1;//上个月天数
													if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null
															&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1])!=null&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()!=null){
														Integer forecast1=data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast();
														Integer forecast2=0;
														if(data.get(name).get(country).get(temp2[0]+"-"+temp2[1])!=null&&data.get(name).get(country).get(temp2[0]+"-"+temp2[1]).getQuantityForecast()!=null){
															forecast2=data.get(name).get(country).get(temp2[0]+"-"+temp2[1]).getQuantityForecast();
														}
														totalSal=forecast1*apart1*1.0d/day1+forecast2*apart2*1.0d/day2;
													}
											}
											Integer avgWeekQuantity=MathUtils.roundUp(totalSal);
										//	setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
											Integer period=16;
											if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).getPeriod()!=null){
												period=MathUtils.roundUp(stockMap.get(name).getPeriod()*1.0/7);
											}
											if(weekEntry.getValue()>period){
												setWeekQuantity(weekEntry.getValue(),0,gap);
												fMap.put(week,0);
											}else{
												//setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
												setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
												fMap.put(week,avgWeekQuantity);
											}
											
											
											
										}
										gapList.add(gap);
										gapList.add(gap);
										

										//亚马逊仓在途3
										if(fbaTransMap!=null&&fbaTransMap.size()>0){
											Map<String,Integer> fabTrans=fbaTransMap.get(name+"_"+country);
											if(fabTrans!=null&&fabTrans.size()>0){
												PsiInventoryGap tranGap=new PsiInventoryGap();
												tranGap.setNameColor(name);
												tranGap.setCountry(country);
												tranGap.setForecastType("0");
												tranGap.setType("3");
												tranGap.setCreateDate(createDate);
												for(Map.Entry<String,Integer> wEntry:fabTrans.entrySet()){
													Integer c=wEntry.getValue();
													if(c!=null&&c<=16){
														setWeekQuantity(c,wEntry.getValue(),tranGap);
													}
												}
												gapList.add(tranGap);
											}
										}
										//亚马逊仓在途
										
										//亚马逊仓2
										Integer totalQuantity=0;
										if(amazonStock.get(name+"_"+country)!=null){
											 totalQuantity=amazonStock.get(name+"_"+country).getRealTotal();
										}
										//减fba在途
										Integer quantity=totalQuantity;
										PsiInventoryGap gap2=new PsiInventoryGap();
										gap2.setNameColor(name);
										gap2.setCountry(country);
										gap2.setForecastType("0");
										gap2.setType("2");
										gap2.setCreateDate(createDate);
										for(int i=1;i<=16;i++){
											if(i==1){
												gap2.setWeek1(quantity);
											}else{
												setWeekQuantity(i,0,gap2);
											}
										}
										gapList.add(gap2);
										
										//亚马逊仓
										//本地仓
										setOthereInfo(localStock,name,country,weekList,createDate,gapList,"4","0");
										//本地运输
										setOthereInfo(localTrans,name,country,weekList,createDate,gapList,"5","0");
										//CN仓
										setOthereInfo(cnTrans,name,country,weekList,createDate,gapList,"6","0");
										//PO
										setOthereInfo(productingMap,name,country,weekList,createDate,gapList,"7","0");
									}
								}	
								
							}else{
								for(PsiProductEliminate liminate:nameList){
									    String name=liminate.getColorName();
									
										PsiInventoryGap gap=new PsiInventoryGap();
										gap.setNameColor(name);
										gap.setCountry(country);
										gap.setForecastType("0");
										gap.setType("1");
										gap.setCreateDate(createDate);
										Map<String,Integer> fMap=forecastMap.get(name+"_"+country);
										if(fMap==null){
											fMap=Maps.newHashMap();
											forecastMap.put(name+"_"+country,fMap);
										}
//										for(String week:weekList.keySet()){
										for(Map.Entry<String,Integer> weekEntry:weekList.entrySet()){
											String week = weekEntry.getKey();
											Double totalSal=0d;
											String section=tip.get(week);
											String[] arr=section.split(",");
											String[] temp1=arr[0].split("-");
											String[] temp2=arr[1].split("-");
											Calendar  cal1=Calendar.getInstance();  
											Calendar  cal2=Calendar.getInstance();  
											try {
												cal1.setTime(formatDay.parse(arr[0].toString()));
												cal2.setTime(formatDay.parse(arr[1].toString()));
											} catch (ParseException e) {
												e.printStackTrace();
											} 
											int day1=cal1.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
											int day2=cal2.getActualMaximum(Calendar.DAY_OF_MONTH);
											
											if(temp1[0].equals(temp2[0])&&temp1[1].equals(temp2[1])){
													if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null
															&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1])!=null&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()!=null){
														totalSal=data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()*7.0d/day1;
												    }
											}else{
												//Integer apart1=Integer.parseInt(temp1[2])-day1+1;//上个月天数
												Integer apart1=day1-Integer.parseInt(temp1[2])+1;//上个月天数
												Integer apart2=7-apart1;//上个月天数
													if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null
															&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1])!=null&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()!=null){
														Integer forecast1=data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast();
														Integer forecast2=0;
														if(data.get(name).get(country).get(temp2[0]+"-"+temp2[1])!=null&&data.get(name).get(country).get(temp2[0]+"-"+temp2[1]).getQuantityForecast()!=null){
															forecast2=data.get(name).get(country).get(temp2[0]+"-"+temp2[1]).getQuantityForecast();
														}
														totalSal=forecast1*apart1*1.0d/day1+forecast2*apart2*1.0d/day2;
													}
											}
											Integer avgWeekQuantity=MathUtils.roundUp(totalSal);
											//setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
											Integer period=16;
											if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).getPeriod()!=null){
												period=MathUtils.roundUp(stockMap.get(name).getPeriod()*1.0/7);
											}
											if(weekEntry.getValue()>period){
												setWeekQuantity(weekEntry.getValue(),0,gap);
												fMap.put(week,0);
											}else{
												//setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
												setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
												fMap.put(week,avgWeekQuantity);
											}
											
											
											
										}
										gapList.add(gap);
										gapList.add(gap);
										

										//亚马逊仓在途3
										if(fbaTransMap!=null&&fbaTransMap.size()>0){
											Map<String,Integer> fabTrans=fbaTransMap.get(name+"_"+country);
											if(fabTrans!=null&&fabTrans.size()>0){
												PsiInventoryGap tranGap=new PsiInventoryGap();
												tranGap.setNameColor(name);
												tranGap.setCountry(country);
												tranGap.setForecastType("0");
												tranGap.setType("3");
												tranGap.setCreateDate(createDate);
												for(Map.Entry<String,Integer> wEntry:fabTrans.entrySet()){
													Integer c=wEntry.getValue();
													if(c!=null&&c<=16){
														setWeekQuantity(c,wEntry.getValue(),tranGap);
													}
												}
												gapList.add(tranGap);
											}
										}
										//亚马逊仓在途
										
										//亚马逊仓2
										Integer totalQuantity=0;
										if(amazonStock.get(name+"_"+country)!=null){
											 totalQuantity=amazonStock.get(name+"_"+country).getRealTotal();
										}
										//减fba在途
										Integer quantity=totalQuantity;
										PsiInventoryGap gap2=new PsiInventoryGap();
										gap2.setNameColor(name);
										gap2.setCountry(country);
										gap2.setForecastType("0");
										gap2.setType("2");
										gap2.setCreateDate(createDate);
										for(int i=1;i<=16;i++){
											if(i==1){
												gap2.setWeek1(quantity);
											}else{
												setWeekQuantity(i,0,gap2);
											}
										}
										gapList.add(gap2);
									
										//亚马逊仓
										//本地仓
										setOthereInfo(localStock,name,country,weekList,createDate,gapList,"4","0");
										//本地运输
										setOthereInfo(localTrans,name,country,weekList,createDate,gapList,"5","0");
										//CN仓
										setOthereInfo(cnTrans,name,country,weekList,createDate,gapList,"6","0");
										//PO
										setOthereInfo(productingMap,name,country,weekList,createDate,gapList,"7","0");
									}
							  }
						
					}
					
					
					//1:月预测 所有 在售new产品
					for (String country: allCountry) {
							List<PsiProductEliminate> nameList=psiProductEliminateService.findOnSaleNotNew(country);
							if("de".equals(country)){
								for(PsiProductEliminate liminate:nameList){
								    String name=liminate.getColorName();
									PsiProduct product=psiProductService.findProductByProductName(name);
									if(product!=null&&"0".equals(product.getHasPower())){//不带电源 泛欧   hasPower 0：不带电源 1：带电源
										//Amazon仓balance
										PsiInventoryGap gap=new PsiInventoryGap();
										gap.setNameColor(name);
										gap.setCountry(country);
										gap.setForecastType("0");
										gap.setType("8");
										gap.setCreateDate(createDate);
										//Amazon仓balance
										
										//FBA在途balance
										PsiInventoryGap gap1=new PsiInventoryGap();
										gap1.setNameColor(name);
										gap1.setCountry(country);
										gap1.setForecastType("0");
										gap1.setType("9");
										gap1.setCreateDate(createDate);
										//FBA在途balance
										
										//本地仓balance
										PsiInventoryGap gap2=new PsiInventoryGap();
										gap2.setNameColor(name);
										gap2.setCountry(country);
										gap2.setForecastType("0");
										gap2.setType("10");
										gap2.setCreateDate(createDate);
										//本地仓balance
										
										PsiInventoryGap gap3=new PsiInventoryGap();
										gap3.setNameColor(name);
										gap3.setCountry(country);
										gap3.setForecastType("0");
										gap3.setType("11");
										gap3.setCreateDate(createDate);
										
										PsiInventoryGap gap4=new PsiInventoryGap();
										gap4.setNameColor(name);
										gap4.setCountry(country);
										gap4.setForecastType("0");
										gap4.setType("12");
										gap4.setCreateDate(createDate);
										
										PsiInventoryGap gap5=new PsiInventoryGap();
										gap5.setNameColor(name);
										gap5.setCountry(country);
										gap5.setForecastType("0");
										gap5.setType("13");
										gap5.setCreateDate(createDate);
										
										Map<String,Integer> fabTrans=fbaTransMap.get(name+"_eu");
										//本地仓
										Map<String,Integer> localWareHouseTrans=localStock.get(name+"_eu");
										
										Map<String,Integer> localTransQuantity=localTrans.get(name+"_eu");//本地运输
										Map<String,Integer> cnTransQuantity=cnTrans.get(name+"_eu");//CN仓
										Map<String,Integer> productingQuantity=productingMap.get(name+"_eu");//PO
										
										Integer amazonBalance=0;
										Integer fbaTransBalance=0;
										Integer localTransBalance=0;
										Integer transBalance=0;
										Integer cnTransBalance=0;
										Integer productingBalance=0;
//										for(String week:weekList.keySet()){
										for(Map.Entry<String,Integer> weekEntry:weekList.entrySet()){
											String week = weekEntry.getKey();
											int i=weekEntry.getValue();
											int total=0;
											Integer avgWeekQuantity=0;
											if(forecastMap!=null&&forecastMap.get(name+"_"+country)!=null&&forecastMap.get(name+"_"+country).get(week)!=null){
												avgWeekQuantity=forecastMap.get(name+"_"+country).get(week);
											}
											if(i==1&&amazonWareHouse!=null){
												//Amazon仓balance
												amazonBalance=(amazonWareHouse.get(name+"_eu")==null?0:amazonWareHouse.get(name+"_eu"))-avgWeekQuantity;
												setWeekQuantity(i,amazonBalance,gap);
												total+=(amazonWareHouse.get(name+"_eu")==null?0:amazonWareHouse.get(name+"_eu"));
												//FBA在途balance
												if(fabTrans!=null&&fabTrans.size()>0&&fabTrans.get(week)!=null){
													total+=fabTrans.get(week);
													fbaTransBalance=total-avgWeekQuantity;
													setWeekQuantity(i,fbaTransBalance,gap1);
												}else{
													fbaTransBalance=total-avgWeekQuantity;
													setWeekQuantity(i,fbaTransBalance,gap1);
												}
												//本地仓balance
												if(localWareHouseTrans!=null&&localWareHouseTrans.size()>0&&localWareHouseTrans.get(week)!=null){
													total+=localWareHouseTrans.get(week);
													localTransBalance=total-avgWeekQuantity;
													setWeekQuantity(i,localTransBalance,gap2);
												}else{
													localTransBalance=total-avgWeekQuantity;
													setWeekQuantity(i,localTransBalance,gap2);
												}
												
												//本地运输balance
												if(localTransQuantity!=null&&localTransQuantity.size()>0&&localTransQuantity.get(week)!=null){
													total+=localTransQuantity.get(week);
													transBalance=total-avgWeekQuantity;
													setWeekQuantity(i,transBalance,gap3);
												}else{
													transBalance=total-avgWeekQuantity;
													setWeekQuantity(i,transBalance,gap3);
												}
												
												//cn仓balance
												if(cnTransQuantity!=null&&cnTransQuantity.size()>0&&cnTransQuantity.get(week)!=null){
													total+=cnTransQuantity.get(week);
													cnTransBalance=total-avgWeekQuantity;
													setWeekQuantity(i,cnTransBalance,gap4);
												}else{
													cnTransBalance=total-avgWeekQuantity;
													setWeekQuantity(i,cnTransBalance,gap4);
												}
												
												//PO balance
												if(productingQuantity!=null&&productingQuantity.size()>0&&productingQuantity.get(week)!=null){
													total+=productingQuantity.get(week);
													productingBalance=total-avgWeekQuantity;
													setWeekQuantity(i,productingBalance,gap5);
												}else{
													productingBalance=total-avgWeekQuantity;
													setWeekQuantity(i,productingBalance,gap5);
												}
												
											}else if(amazonWareHouse!=null){
												//Amazon仓balance
												amazonBalance=amazonBalance-avgWeekQuantity;
												setWeekQuantity(i,amazonBalance,gap);
												
								                 //FBA在途balance
												if(fabTrans!=null&&fabTrans.size()>0&&fabTrans.get(week)!=null){
													total+=fabTrans.get(week);
													fbaTransBalance=fbaTransBalance+total-avgWeekQuantity;
													setWeekQuantity(i,fbaTransBalance,gap1);
												}else{
													fbaTransBalance=fbaTransBalance+total-avgWeekQuantity;
													setWeekQuantity(i,fbaTransBalance,gap1);
												}
												//本地仓balance
												if(localWareHouseTrans!=null&&localWareHouseTrans.size()>0&&localWareHouseTrans.get(week)!=null){
													total+=localWareHouseTrans.get(week);
													localTransBalance=localTransBalance+total-avgWeekQuantity;
													setWeekQuantity(i,localTransBalance,gap2);
												}else{
													localTransBalance=localTransBalance+total-avgWeekQuantity;
													setWeekQuantity(i,localTransBalance,gap2);
												}
												
												//本地运输balance
												if(localTransQuantity!=null&&localTransQuantity.size()>0&&localTransQuantity.get(week)!=null){
													total+=localTransQuantity.get(week);
													transBalance=transBalance+total-avgWeekQuantity;
													setWeekQuantity(i,transBalance,gap3);
												}else{
													transBalance=transBalance+total-avgWeekQuantity;
													setWeekQuantity(i,transBalance,gap3);
												}
												
												//cn仓balance
												if(cnTransQuantity!=null&&cnTransQuantity.size()>0&&cnTransQuantity.get(week)!=null){
													total+=cnTransQuantity.get(week);
													cnTransBalance=cnTransBalance+total-avgWeekQuantity;
													setWeekQuantity(i,cnTransBalance,gap4);
												}else{
													cnTransBalance=cnTransBalance+total-avgWeekQuantity;
													setWeekQuantity(i,cnTransBalance,gap4);
												}
												
												//PO balance
												if(productingQuantity!=null&&productingQuantity.size()>0&&productingQuantity.get(week)!=null){
													total+=productingQuantity.get(week);
													productingBalance=productingBalance+total-avgWeekQuantity;
													setWeekQuantity(i,productingBalance,gap5);
												}else{
													productingBalance=productingBalance+total-avgWeekQuantity;
													setWeekQuantity(i,productingBalance,gap5);
												}
												
											}
										
										}
										gapList.add(gap);
										gapList.add(gap1);
										gapList.add(gap2);
										gapList.add(gap3);
										gapList.add(gap4);
										gapList.add(gap5);
									}else if(product!=null){//带电源
									    if(amazonWareHouse!=null){
											setBalanceInfo2(forecastMap,fbaTransMap,localStock,localTrans,cnTrans,productingMap,
													amazonWareHouse,sale30Map,name,country,weekList,createDate,gapList);
									    }	
									}
								}    
							}else if("fr,it,es,uk".contains(country)){
								for(PsiProductEliminate liminate:nameList){
								    String name=liminate.getColorName();
									PsiProduct product=psiProductService.findProductByProductName(name);
									if(product!=null&&"1".equals(product.getHasPower())){//带电源的
									    if(amazonWareHouse!=null){
										setBalanceInfo2(forecastMap,fbaTransMap,localStock,localTrans,cnTrans,productingMap,
												amazonWareHouse,sale30Map,name,country,weekList,createDate,gapList);
									    }
									}
								}
							}else{
								for(PsiProductEliminate liminate:nameList){
								    String name=liminate.getColorName();
								    if(amazonWareHouse!=null){
								    	setBalanceInfo2(forecastMap,fbaTransMap,localStock,localTrans,cnTrans,productingMap,
												amazonWareHouse,sale30Map,name,country,weekList,createDate,gapList);
								    }
									
								}
							}
					}	
					
					
					//0:销量预测 在售排除新品
					for (String country: allCountry) {
							List<PsiProductEliminate> nameList=psiProductEliminateService.findOnSaleNotNew(country);
							Map<String,PsiProductInStock> stockMap=psiProductInStockService.getHistoryInventory(country);
							if("de".equals(country)){
								for(PsiProductEliminate liminate:nameList){
									String name=liminate.getColorName();
									PsiProduct product=psiProductService.findProductByProductName(name);
									if(product!=null&&"0".equals(product.getHasPower())){//不带电源 泛欧   hasPower 0：不带电源 1：带电源
										PsiInventoryGap gap=new PsiInventoryGap();
										gap.setNameColor(name);
										gap.setCountry(country);
										gap.setForecastType("3");
										gap.setType("1");
										gap.setCreateDate(createDate);
										
//										for(String week:weekList.keySet()){
										for(Map.Entry<String,Integer> weekEntry:weekList.entrySet()){
											String week = weekEntry.getKey();
											Double totalSal=0d;
											String section=tip.get(week);
											String[] arr=section.split(",");
											String[] temp1=arr[0].split("-");
											String[] temp2=arr[1].split("-");
											Calendar  cal1=Calendar.getInstance();  
											Calendar  cal2=Calendar.getInstance();  
											try {
												cal1.setTime(formatDay.parse(arr[0].toString()));
												cal2.setTime(formatDay.parse(arr[1].toString()));
											} catch (ParseException e) {
												e.printStackTrace();
											} 
											int day1=cal1.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
											int day2=cal2.getActualMaximum(Calendar.DAY_OF_MONTH);
											
											if(temp1[0].equals(temp2[0])&&temp1[1].equals(temp2[1])){
												for(String euCountry:countryList){
													if(data!=null&&data.get(name)!=null&&data.get(name).get(euCountry)!=null
															&&data.get(name).get(euCountry).get(temp1[0]+"-"+temp1[1])!=null&&data.get(name).get(euCountry).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()!=null){
														totalSal+=(data.get(name).get(euCountry).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()*7.0d/day1);
													}
												}
											}else{
												Integer apart1=day1-Integer.parseInt(temp1[2])+1;//上个月天数
												Integer apart2=7-apart1;//上个月天数
												for(String euCountry:countryList){
													if(data!=null&&data.get(name)!=null&&data.get(name).get(euCountry)!=null
															&&data.get(name).get(euCountry).get(temp1[0]+"-"+temp1[1])!=null&&data.get(name).get(euCountry).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()!=null){
														Integer forecast1=data.get(name).get(euCountry).get(temp1[0]+"-"+temp1[1]).getQuantityForecast();
														Integer forecast2=0;
														if(data.get(name).get(euCountry).get(temp2[0]+"-"+temp2[1])!=null&&data.get(name).get(euCountry).get(temp2[0]+"-"+temp2[1]).getQuantityForecast()!=null){
															forecast2=data.get(name).get(euCountry).get(temp2[0]+"-"+temp2[1]).getQuantityForecast();
														}
														totalSal+=(forecast1*apart1*1.0d/day1+forecast2*apart2*1.0d/day2);
													}
												}
											}
											Integer avgWeekQuantity=MathUtils.roundUp(totalSal);
										//	setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
											Integer period=16;
											if(euStockMap!=null&&euStockMap.get(name)!=null&&euStockMap.get(name).getPeriod()!=null){
												period=MathUtils.roundUp(euStockMap.get(name).getPeriod()*1.0/7);
											}
											if(weekEntry.getValue()>period){
												setWeekQuantity(weekEntry.getValue(),0,gap);
											}else{
												setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
											}
											
										}
										gapList.add(gap);
										
										//亚马逊仓2
										Map<String,PsiInventoryFba>  amazonInventory=psiInventoryService.getProductFbaInfo(name);
										Integer totalQuantity=0;
										if(amazonInventory.get(name+"_eu")!=null){
											 totalQuantity=amazonInventory.get(name+"_eu").getRealTotal();
										}
										//减fba在途
										Integer quantity=totalQuantity;
										PsiInventoryGap gap2=new PsiInventoryGap();
										gap2.setNameColor(name);
										gap2.setCountry(country);
										gap2.setForecastType("3");
										gap2.setType("2");
										gap2.setCreateDate(createDate);
										
										for(int i=1;i<=16;i++){
											if(i==1){
												gap2.setWeek1(quantity);
											}else{
												setWeekQuantity(i,0,gap2);
											}
										}
										gapList.add(gap2);
										//亚马逊仓
										
										//亚马逊仓在途3
										if(fbaTransMap!=null&&fbaTransMap.size()>0){
											Map<String,Integer> fabTrans=fbaTransMap.get(name+"_eu");
											if(fabTrans!=null&&fabTrans.size()>0){
												PsiInventoryGap tranGap=new PsiInventoryGap();
												tranGap.setNameColor(name);
												tranGap.setCountry(country);
												tranGap.setForecastType("3");
												tranGap.setType("3");
												tranGap.setCreateDate(createDate);
												for(Map.Entry<String,Integer> wEntry:fabTrans.entrySet()){
													Integer c=wEntry.getValue();
													if(c!=null&&c<=16){
														setWeekQuantity(c,wEntry.getValue(),tranGap);
													}
												}
												gapList.add(tranGap);
											}
										}
										//亚马逊仓在途
										
										//本地仓
										setOthereInfo(localStock,name,"eu",weekList,createDate,gapList,"4","3");
										//本地运输
										setOthereInfo(localTrans,name,"eu",weekList,createDate,gapList,"5","3");
										//CN仓
										setOthereInfo(cnTrans,name,"eu",weekList,createDate,gapList,"6","3");
										//PO
										setOthereInfo(productingMap,name,"eu",weekList,createDate,gapList,"7","3");
									}else if(product!=null){
										
										PsiInventoryGap gap=new PsiInventoryGap();
										gap.setNameColor(name);
										gap.setCountry(country);
										gap.setForecastType("3");
										gap.setType("1");
										gap.setCreateDate(createDate);
//										for(String week:weekList.keySet()){
										for(Map.Entry<String,Integer> weekEntry:weekList.entrySet()){
											String week = weekEntry.getKey();
											Double totalSal=0d;
											String section=tip.get(week);
											String[] arr=section.split(",");
											String[] temp1=arr[0].split("-");
											String[] temp2=arr[1].split("-");
											Calendar  cal1=Calendar.getInstance();  
											Calendar  cal2=Calendar.getInstance();  
											try {
												cal1.setTime(formatDay.parse(arr[0].toString()));
												cal2.setTime(formatDay.parse(arr[1].toString()));
											} catch (ParseException e) {
												e.printStackTrace();
											} 
											int day1=cal1.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
											int day2=cal2.getActualMaximum(Calendar.DAY_OF_MONTH);
											
											if(temp1[0].equals(temp2[0])&&temp1[1].equals(temp2[1])){
												//for(String euCountry:countryList){
													if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null
															&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1])!=null&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()!=null){
														totalSal+=(data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()*7.0d/day1);
													}
												//}
											}else{
												//Integer apart1=Integer.parseInt(temp1[2])-day1+1;//上个月天数
												Integer apart1=day1-Integer.parseInt(temp1[2])+1;//上个月天数
												Integer apart2=7-apart1;//上个月天数
												//for(String euCountry:countryList){
													if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null
															&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1])!=null&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()!=null){
														Integer forecast1=data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast();
														Integer forecast2=0;
														if(data.get(name).get(country).get(temp2[0]+"-"+temp2[1])!=null&&data.get(name).get(country).get(temp2[0]+"-"+temp2[1]).getQuantityForecast()!=null){
															forecast2=data.get(name).get(country).get(temp2[0]+"-"+temp2[1]).getQuantityForecast();
														}
														totalSal+=(forecast1*apart1*1.0d/day1+forecast2*apart2*1.0d/day2);
													}
												//}
											}
											Integer avgWeekQuantity=MathUtils.roundUp(totalSal);
											//setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
											Integer period=16;
											if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).getPeriod()!=null){
												period=MathUtils.roundUp(stockMap.get(name).getPeriod()*1.0/7);
											}
											if(weekEntry.getValue()>period){
												setWeekQuantity(weekEntry.getValue(),0,gap);
											}else{
												setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
											}
										}
										gapList.add(gap);
										gapList.add(gap);
										

										//亚马逊仓在途3
										if(fbaTransMap!=null&&fbaTransMap.size()>0){
											Map<String,Integer> fabTrans=fbaTransMap.get(name+"_"+country);
											if(fabTrans!=null&&fabTrans.size()>0){
												PsiInventoryGap tranGap=new PsiInventoryGap();
												tranGap.setNameColor(name);
												tranGap.setCountry(country);
												tranGap.setForecastType("3");
												tranGap.setType("3");
												tranGap.setCreateDate(createDate);
												for(Map.Entry<String,Integer> wEntry:fabTrans.entrySet()){
													Integer c=wEntry.getValue();
													if(c!=null&&c<=16){
														setWeekQuantity(c,wEntry.getValue(),tranGap);
													}
												}
												gapList.add(tranGap);
											}
										}
										//亚马逊仓在途
										
										//亚马逊仓2
										Integer totalQuantity=0;
										if(amazonStock.get(name+"_"+country)!=null){
											 totalQuantity=amazonStock.get(name+"_"+country).getRealTotal();
										}
										//减fba在途
										Integer quantity=totalQuantity;
										PsiInventoryGap gap2=new PsiInventoryGap();
										gap2.setNameColor(name);
										gap2.setCountry(country);
										gap2.setForecastType("3");
										gap2.setType("2");
										gap2.setCreateDate(createDate);
										for(int i=1;i<=16;i++){
											if(i==1){
												gap2.setWeek1(quantity);
											}else{
												setWeekQuantity(i,0,gap2);
											}
										}
										gapList.add(gap2);
										
										//亚马逊仓
										//本地仓
										setOthereInfo(localStock,name,country,weekList,createDate,gapList,"4","3");
										//本地运输
										setOthereInfo(localTrans,name,country,weekList,createDate,gapList,"5","3");
										//CN仓
										setOthereInfo(cnTrans,name,country,weekList,createDate,gapList,"6","3");
										//PO
										setOthereInfo(productingMap,name,country,weekList,createDate,gapList,"7","3");
									}
								}
							}else if("fr,it,es,uk".contains(country)){
								for(PsiProductEliminate liminate:nameList){
									String name=liminate.getColorName();
									PsiProduct product=psiProductService.findProductByProductName(name);
									if(product!=null&&"1".equals(product.getHasPower())){//带电源的
									
										PsiInventoryGap gap=new PsiInventoryGap();
										gap.setNameColor(name);
										gap.setCountry(country);
										gap.setForecastType("3");
										gap.setType("1");
										gap.setCreateDate(createDate);
//										for(String week:weekList.keySet()){
										for(Map.Entry<String,Integer> weekEntry:weekList.entrySet()){
											String week = weekEntry.getKey();
											Double totalSal=0d;
											String section=tip.get(week);
											String[] arr=section.split(",");
											String[] temp1=arr[0].split("-");
											String[] temp2=arr[1].split("-");
											Calendar  cal1=Calendar.getInstance();  
											Calendar  cal2=Calendar.getInstance();  
											try {
												cal1.setTime(formatDay.parse(arr[0].toString()));
												cal2.setTime(formatDay.parse(arr[1].toString()));
											} catch (ParseException e) {
												e.printStackTrace();
											} 
											int day1=cal1.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
											int day2=cal2.getActualMaximum(Calendar.DAY_OF_MONTH);
											
											if(temp1[0].equals(temp2[0])&&temp1[1].equals(temp2[1])){
													if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null
															&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1])!=null&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()!=null){
														totalSal=data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()*7.0d/day1;
												    }
											}else{
												//Integer apart1=Integer.parseInt(temp1[2])-day1+1;//上个月天数
												Integer apart1=day1-Integer.parseInt(temp1[2])+1;//上个月天数
												Integer apart2=7-apart1;//上个月天数
													if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null
															&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1])!=null&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()!=null){
														Integer forecast1=data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast();
														Integer forecast2=0;
														if(data.get(name).get(country).get(temp2[0]+"-"+temp2[1])!=null&&data.get(name).get(country).get(temp2[0]+"-"+temp2[1]).getQuantityForecast()!=null){
															forecast2=data.get(name).get(country).get(temp2[0]+"-"+temp2[1]).getQuantityForecast();
														}
														totalSal=forecast1*apart1*1.0d/day1+forecast2*apart2*1.0d/day2;
													}
											}
											Integer avgWeekQuantity=MathUtils.roundUp(totalSal);
											//setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
											Integer period=16;
											if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).getPeriod()!=null){
												period=MathUtils.roundUp(stockMap.get(name).getPeriod()*1.0/7);
											}
											if(weekEntry.getValue()>period){
												setWeekQuantity(weekEntry.getValue(),0,gap);
											}else{
												setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
											}
										}
										gapList.add(gap);
										gapList.add(gap);
										

										//亚马逊仓在途3
										if(fbaTransMap!=null&&fbaTransMap.size()>0){
											Map<String,Integer> fabTrans=fbaTransMap.get(name+"_"+country);
											if(fabTrans!=null&&fabTrans.size()>0){
												PsiInventoryGap tranGap=new PsiInventoryGap();
												tranGap.setNameColor(name);
												tranGap.setCountry(country);
												tranGap.setForecastType("3");
												tranGap.setType("3");
												tranGap.setCreateDate(createDate);
												for(Map.Entry<String,Integer> wEntry:fabTrans.entrySet()){
													Integer c=wEntry.getValue();
													if(c!=null&&c<=16){
														setWeekQuantity(c,wEntry.getValue(),tranGap);
													}
												}
												gapList.add(tranGap);
											}
										}
										//亚马逊仓在途
										
										//亚马逊仓2
										Integer totalQuantity=0;
										if(amazonStock.get(name+"_"+country)!=null){
											 totalQuantity=amazonStock.get(name+"_"+country).getRealTotal();
										}
										//减fba在途
										Integer quantity=totalQuantity;
										PsiInventoryGap gap2=new PsiInventoryGap();
										gap2.setNameColor(name);
										gap2.setCountry(country);
										gap2.setForecastType("3");
										gap2.setType("2");
										gap2.setCreateDate(createDate);
										for(int i=1;i<=16;i++){
											if(i==1){
												gap2.setWeek1(quantity);
											}else{
												setWeekQuantity(i,0,gap2);
											}
										}
										gapList.add(gap2);
										
										//亚马逊仓
										//本地仓
										setOthereInfo(localStock,name,country,weekList,createDate,gapList,"4","3");
										//本地运输
										setOthereInfo(localTrans,name,country,weekList,createDate,gapList,"5","3");
										//CN仓
										setOthereInfo(cnTrans,name,country,weekList,createDate,gapList,"6","3");
										//PO
										setOthereInfo(productingMap,name,country,weekList,createDate,gapList,"7","3");
									}
								}	
								
							}else{
								for(PsiProductEliminate liminate:nameList){
									    String name=liminate.getColorName();
									
										PsiInventoryGap gap=new PsiInventoryGap();
										gap.setNameColor(name);
										gap.setCountry(country);
										gap.setForecastType("3");
										gap.setType("1");
										gap.setCreateDate(createDate);
//										for(String week:weekList.keySet()){
										for(Map.Entry<String,Integer> weekEntry:weekList.entrySet()){
											String week = weekEntry.getKey();
											Double totalSal=0d;
											String section=tip.get(week);
											String[] arr=section.split(",");
											String[] temp1=arr[0].split("-");
											String[] temp2=arr[1].split("-");
											Calendar  cal1=Calendar.getInstance();  
											Calendar  cal2=Calendar.getInstance();  
											try {
												cal1.setTime(formatDay.parse(arr[0].toString()));
												cal2.setTime(formatDay.parse(arr[1].toString()));
											} catch (ParseException e) {
												e.printStackTrace();
											} 
											int day1=cal1.getActualMaximum(Calendar.DAY_OF_MONTH);//本月份的天数
											int day2=cal2.getActualMaximum(Calendar.DAY_OF_MONTH);
											
											if(temp1[0].equals(temp2[0])&&temp1[1].equals(temp2[1])){
													if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null
															&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1])!=null&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()!=null){
														totalSal=data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()*7.0d/day1;
												    }
											}else{
												//Integer apart1=Integer.parseInt(temp1[2])-day1+1;//上个月天数
												Integer apart1=day1-Integer.parseInt(temp1[2])+1;//上个月天数
												Integer apart2=7-apart1;//上个月天数
													if(data!=null&&data.get(name)!=null&&data.get(name).get(country)!=null
															&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1])!=null&&data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast()!=null){
														Integer forecast1=data.get(name).get(country).get(temp1[0]+"-"+temp1[1]).getQuantityForecast();
														Integer forecast2=0;
														if(data.get(name).get(country).get(temp2[0]+"-"+temp2[1])!=null&&data.get(name).get(country).get(temp2[0]+"-"+temp2[1]).getQuantityForecast()!=null){
															forecast2=data.get(name).get(country).get(temp2[0]+"-"+temp2[1]).getQuantityForecast();
														}
														totalSal=forecast1*apart1*1.0d/day1+forecast2*apart2*1.0d/day2;
													}
											}
											Integer avgWeekQuantity=MathUtils.roundUp(totalSal);
											//setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
											Integer period=16;
											if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).getPeriod()!=null){
												period=MathUtils.roundUp(stockMap.get(name).getPeriod()*1.0/7);
											}
											if(weekEntry.getValue()>period){
												setWeekQuantity(weekEntry.getValue(),0,gap);
											}else{
												setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
											}
										}
										gapList.add(gap);
										gapList.add(gap);
										

										//亚马逊仓在途3
										if(fbaTransMap!=null&&fbaTransMap.size()>0){
											Map<String,Integer> fabTrans=fbaTransMap.get(name+"_"+country);
											if(fabTrans!=null&&fabTrans.size()>0){
												PsiInventoryGap tranGap=new PsiInventoryGap();
												tranGap.setNameColor(name);
												tranGap.setCountry(country);
												tranGap.setForecastType("3");
												tranGap.setType("3");
												tranGap.setCreateDate(createDate);
												for(Map.Entry<String,Integer> wEntry:fabTrans.entrySet()){
													Integer c=wEntry.getValue();
													if(c!=null&&c<=16){
														setWeekQuantity(c,wEntry.getValue(),tranGap);
													}
												}
												gapList.add(tranGap);
											}
										}
										//亚马逊仓在途
										
										//亚马逊仓2
										Integer totalQuantity=0;
										if(amazonStock.get(name+"_"+country)!=null){
											 totalQuantity=amazonStock.get(name+"_"+country).getRealTotal();
										}
										//减fba在途
										Integer quantity=totalQuantity;
										PsiInventoryGap gap2=new PsiInventoryGap();
										gap2.setNameColor(name);
										gap2.setCountry(country);
										gap2.setForecastType("3");
										gap2.setType("2");
										gap2.setCreateDate(createDate);
										for(int i=1;i<=16;i++){
											if(i==1){
												gap2.setWeek1(quantity);
											}else{
												setWeekQuantity(i,0,gap2);
											}
										}
										gapList.add(gap2);
									
										//亚马逊仓
										//本地仓
										setOthereInfo(localStock,name,country,weekList,createDate,gapList,"4","3");
										//本地运输
										setOthereInfo(localTrans,name,country,weekList,createDate,gapList,"5","3");
										//CN仓
										setOthereInfo(cnTrans,name,country,weekList,createDate,gapList,"6","3");
										//PO
										setOthereInfo(productingMap,name,country,weekList,createDate,gapList,"7","3");
									}
							  }
						
					}
					
					//1:月预测 所有 在售new产品
					for (String country: allCountry) {
							List<PsiProductEliminate> nameList=psiProductEliminateService.findOnSaleNotNew(country);
							Map<String,PsiProductInStock> stockMap=psiProductInStockService.getHistoryInventory(country);
							if("de".equals(country)){
								for(PsiProductEliminate liminate:nameList){
								    String name=liminate.getColorName();
									PsiProduct product=psiProductService.findProductByProductName(name);
									if(product!=null&&"0".equals(product.getHasPower())){//不带电源 泛欧   hasPower 0：不带电源 1：带电源
										//Amazon仓balance
										PsiInventoryGap gap=new PsiInventoryGap();
										gap.setNameColor(name);
										gap.setCountry(country);
										gap.setForecastType("3");
										gap.setType("8");
										gap.setCreateDate(createDate);
										//Amazon仓balance
										
										//FBA在途balance
										PsiInventoryGap gap1=new PsiInventoryGap();
										gap1.setNameColor(name);
										gap1.setCountry(country);
										gap1.setForecastType("3");
										gap1.setType("9");
										gap1.setCreateDate(createDate);
										//FBA在途balance
										
										//本地仓balance
										PsiInventoryGap gap2=new PsiInventoryGap();
										gap2.setNameColor(name);
										gap2.setCountry(country);
										gap2.setForecastType("3");
										gap2.setType("10");
										gap2.setCreateDate(createDate);
										//本地仓balance
										
										PsiInventoryGap gap3=new PsiInventoryGap();
										gap3.setNameColor(name);
										gap3.setCountry(country);
										gap3.setForecastType("3");
										gap3.setType("11");
										gap3.setCreateDate(createDate);
										
										PsiInventoryGap gap4=new PsiInventoryGap();
										gap4.setNameColor(name);
										gap4.setCountry(country);
										gap4.setForecastType("3");
										gap4.setType("12");
										gap4.setCreateDate(createDate);
										
										PsiInventoryGap gap5=new PsiInventoryGap();
										gap5.setNameColor(name);
										gap5.setCountry(country);
										gap5.setForecastType("3");
										gap5.setType("13");
										gap5.setCreateDate(createDate);
										
										Map<String,Integer> fabTrans=fbaTransMap.get(name+"_eu");
										//本地仓
										Map<String,Integer> localWareHouseTrans=localStock.get(name+"_eu");
										
										Map<String,Integer> localTransQuantity=localTrans.get(name+"_eu");//本地运输
										Map<String,Integer> cnTransQuantity=cnTrans.get(name+"_eu");//CN仓
										Map<String,Integer> productingQuantity=productingMap.get(name+"_eu");//PO
										
										Integer amazonBalance=0;
										Integer fbaTransBalance=0;
										Integer localTransBalance=0;
										Integer transBalance=0;
										Integer cnTransBalance=0;
										Integer productingBalance=0;
										Integer safeInventory=0;
										if(euStockMap!=null&&euStockMap.get(name)!=null){
											safeInventory=MathUtils.roundUp(euStockMap.get(name).getSafeInventory());
										}
//										for(String week:weekList.keySet()){
										for(Map.Entry<String,Integer> weekEntry:weekList.entrySet()){
											String week = weekEntry.getKey();
											int i=weekEntry.getValue();
											int total=0;
											Integer avgWeekQuantity=0;
											if(forecastMap!=null&&forecastMap.get(name+"_"+country)!=null&&forecastMap.get(name+"_"+country).get(week)!=null){
												avgWeekQuantity=forecastMap.get(name+"_"+country).get(week);
											}
											
											if(i==1&&amazonWareHouse!=null){
												//Amazon仓balance
												amazonBalance=(amazonWareHouse.get(name+"_eu")==null?0:amazonWareHouse.get(name+"_eu"))-avgWeekQuantity;
												setWeekQuantity(i,amazonBalance-safeInventory,gap);
												total+=(amazonWareHouse.get(name+"_eu")==null?0:amazonWareHouse.get(name+"_eu"));
												//FBA在途balance
												if(fabTrans!=null&&fabTrans.size()>0&&fabTrans.get(week)!=null){
													total+=fabTrans.get(week);
													fbaTransBalance=total-avgWeekQuantity;
													setWeekQuantity(i,fbaTransBalance-safeInventory,gap1);
												}else{
													fbaTransBalance=total-avgWeekQuantity;
													setWeekQuantity(i,fbaTransBalance-safeInventory,gap1);
												}
												//本地仓balance
												if(localWareHouseTrans!=null&&localWareHouseTrans.size()>0&&localWareHouseTrans.get(week)!=null){
													total+=localWareHouseTrans.get(week);
													localTransBalance=total-avgWeekQuantity;
													setWeekQuantity(i,localTransBalance-safeInventory,gap2);
												}else{
													localTransBalance=total-avgWeekQuantity;
													setWeekQuantity(i,localTransBalance-safeInventory,gap2);
												}
												
												//本地运输balance
												if(localTransQuantity!=null&&localTransQuantity.size()>0&&localTransQuantity.get(week)!=null){
													total+=localTransQuantity.get(week);
													transBalance=total-avgWeekQuantity;
													setWeekQuantity(i,transBalance-safeInventory,gap3);
												}else{
													transBalance=total-avgWeekQuantity;
													setWeekQuantity(i,transBalance-safeInventory,gap3);
												}
												
												//cn仓balance
												if(cnTransQuantity!=null&&cnTransQuantity.size()>0&&cnTransQuantity.get(week)!=null){
													total+=cnTransQuantity.get(week);
													cnTransBalance=total-avgWeekQuantity;
													setWeekQuantity(i,cnTransBalance-safeInventory,gap4);
												}else{
													cnTransBalance=total-avgWeekQuantity;
													setWeekQuantity(i,cnTransBalance-safeInventory,gap4);
												}
												
												//PO balance
												if(productingQuantity!=null&&productingQuantity.size()>0&&productingQuantity.get(week)!=null){
													total+=productingQuantity.get(week);
													productingBalance=total-avgWeekQuantity;
													setWeekQuantity(i,productingBalance-safeInventory,gap5);
												}else{
													productingBalance=total-avgWeekQuantity;
													setWeekQuantity(i,productingBalance-safeInventory,gap5);
												}
												
											}else if(amazonWareHouse!=null){
												//Amazon仓balance
												amazonBalance=amazonBalance-avgWeekQuantity;
												setWeekQuantity(i,amazonBalance-safeInventory,gap);
												
								                 //FBA在途balance
												if(fabTrans!=null&&fabTrans.size()>0&&fabTrans.get(week)!=null){
													total+=fabTrans.get(week);
													fbaTransBalance=fbaTransBalance+total-avgWeekQuantity;
													setWeekQuantity(i,fbaTransBalance-safeInventory,gap1);
												}else{
													fbaTransBalance=fbaTransBalance+total-avgWeekQuantity;
													setWeekQuantity(i,fbaTransBalance-safeInventory,gap1);
												}
												//本地仓balance
												if(localWareHouseTrans!=null&&localWareHouseTrans.size()>0&&localWareHouseTrans.get(week)!=null){
													total+=localWareHouseTrans.get(week);
													localTransBalance=localTransBalance+total-avgWeekQuantity;
													setWeekQuantity(i,localTransBalance-safeInventory,gap2);
												}else{
													localTransBalance=localTransBalance+total-avgWeekQuantity;
													setWeekQuantity(i,localTransBalance-safeInventory,gap2);
												}
												
												//本地运输balance
												if(localTransQuantity!=null&&localTransQuantity.size()>0&&localTransQuantity.get(week)!=null){
													total+=localTransQuantity.get(week);
													transBalance=transBalance+total-avgWeekQuantity;
													setWeekQuantity(i,transBalance-safeInventory,gap3);
												}else{
													transBalance=transBalance+total-avgWeekQuantity;
													setWeekQuantity(i,transBalance-safeInventory,gap3);
												}
												
												//cn仓balance
												if(cnTransQuantity!=null&&cnTransQuantity.size()>0&&cnTransQuantity.get(week)!=null){
													total+=cnTransQuantity.get(week);
													cnTransBalance=cnTransBalance+total-avgWeekQuantity;
													setWeekQuantity(i,cnTransBalance-safeInventory,gap4);
												}else{
													cnTransBalance=cnTransBalance+total-avgWeekQuantity;
													setWeekQuantity(i,cnTransBalance-safeInventory,gap4);
												}
												
												//PO balance
												if(productingQuantity!=null&&productingQuantity.size()>0&&productingQuantity.get(week)!=null){
													total+=productingQuantity.get(week);
													productingBalance=productingBalance+total-avgWeekQuantity;
													setWeekQuantity(i,productingBalance-safeInventory,gap5);
												}else{
													productingBalance=productingBalance+total-avgWeekQuantity;
													setWeekQuantity(i,productingBalance-safeInventory,gap5);
												}
												
											}
										
										}
										gapList.add(gap);
										gapList.add(gap1);
										gapList.add(gap2);
										gapList.add(gap3);
										gapList.add(gap4);
										gapList.add(gap5);
									}else if(product!=null){//带电源
										 if(amazonWareHouse!=null){
										setSafeBalanceInfo2(stockMap,forecastMap,fbaTransMap,localStock,localTrans,cnTrans,productingMap,
													amazonWareHouse,sale30Map,name,country,weekList,createDate,gapList);
										 }
									}
								}    
							}else if("fr,it,es,uk".contains(country)){
								for(PsiProductEliminate liminate:nameList){
								    String name=liminate.getColorName();
									PsiProduct product=psiProductService.findProductByProductName(name);
									if(product!=null&&"1".equals(product.getHasPower())){//带电源的
										 if(amazonWareHouse!=null){
										setSafeBalanceInfo2(stockMap,forecastMap,fbaTransMap,localStock,localTrans,cnTrans,productingMap,
												amazonWareHouse,sale30Map,name,country,weekList,createDate,gapList);
										 }	
									}
								}
							}else{
								for(PsiProductEliminate liminate:nameList){
								    String name=liminate.getColorName();
								    if(amazonWareHouse!=null){
								    	setSafeBalanceInfo2(stockMap,forecastMap,fbaTransMap,localStock,localTrans,cnTrans,productingMap,
												amazonWareHouse,sale30Map,name,country,weekList,createDate,gapList);
								    }
								    
								}
							}
					}	
			
			if(gapList!=null&&gapList.size()>0){
				psiOutOfStockInfoService.saveGaps(gapList);
			}
			
		}catch(Exception e){
			 LOGGER.error("缺口异常！",e);
		}
		
		
		
		
		try{
			LOGGER.info("更新报关金额开始");
			List<Integer> orderList=lcPsiTransportOrderService.findOutboundOrderId();
			if(orderList!=null&&orderList.size()>0){
				for(Integer orderId:orderList){
					LcPsiTransportOrder psiTransportOrder=lcPsiTransportOrderService.getById(orderId);
				 	float totalMoney = 0f;
					List<LcPsiTransportOrderItem> items = psiTransportOrder.getItems();
					for (LcPsiTransportOrderItem item : items) {
						totalMoney+=item.getLowerPrice()*item.getQuantity();
				    }
					lcPsiTransportOrderService.updateDeclareAmount(psiTransportOrder.getId(),totalMoney);
				}
			}
		}catch(Exception e){
			LOGGER.error("更新报关金额异常！",e);
		}
		
		Calendar calendar = Calendar.getInstance();
		int day = calendar.get(Calendar.DAY_OF_WEEK);
		if (day ==3) {	
			try{
				countAmazonFee();
			}catch(Exception e){
				LOGGER.error("更新FBA Fee异常！",e);
			}
		}
	}
	

	public void setBalanceInfo(Map<String,Map<String,Integer>> fbaTransMap,Map<String,Map<String,Integer>> localStock,Map<String,Map<String,Integer>> localTrans,Map<String,Map<String,Integer>> cnTrans,Map<String,Map<String,Integer>> productingMap,
			Map<String,Integer> amazonWareHouse,Map<String,Map<String,Integer>>  sale30Map,String name,String country,Map<String,Integer> weekList,Date createDate,List<PsiInventoryGap> gapList){
		//Amazon仓balance
		PsiInventoryGap gap=new PsiInventoryGap();
		gap.setNameColor(name);
		gap.setCountry(country);
		gap.setForecastType("1");
		gap.setType("8");
		gap.setCreateDate(createDate);
		//Amazon仓balance
		
		//FBA在途balance
		PsiInventoryGap gap1=new PsiInventoryGap();
		gap1.setNameColor(name);
		gap1.setCountry(country);
		gap1.setForecastType("1");
		gap1.setType("9");
		gap1.setCreateDate(createDate);
		//FBA在途balance
		
		//本地仓balance
		PsiInventoryGap gap2=new PsiInventoryGap();
		gap2.setNameColor(name);
		gap2.setCountry(country);
		gap2.setForecastType("1");
		gap2.setType("10");
		gap2.setCreateDate(createDate);
		//本地仓balance
		
		PsiInventoryGap gap3=new PsiInventoryGap();
		gap3.setNameColor(name);
		gap3.setCountry(country);
		gap3.setForecastType("1");
		gap3.setType("11");
		gap3.setCreateDate(createDate);
		
		PsiInventoryGap gap4=new PsiInventoryGap();
		gap4.setNameColor(name);
		gap4.setCountry(country);
		gap4.setForecastType("1");
		gap4.setType("12");
		gap4.setCreateDate(createDate);
		
		PsiInventoryGap gap5=new PsiInventoryGap();
		gap5.setNameColor(name);
		gap5.setCountry(country);
		gap5.setForecastType("1");
		gap5.setType("13");
		gap5.setCreateDate(createDate);
		
		Integer totalSal=(sale30Map.get(country)==null||sale30Map.get(country).get(name)==null?0:sale30Map.get(country).get(name));
		Integer avgWeekQuantity=MathUtils.roundUp(totalSal/30d)*7;
		
		Map<String,Integer> fabTrans=fbaTransMap.get(name+"_"+country);
		//本地仓
		Map<String,Integer> localWareHouseTrans=localStock.get(name+"_"+country);
		
		Map<String,Integer> localTransQuantity=localTrans.get(name+"_"+country);//本地运输
		Map<String,Integer> cnTransQuantity=cnTrans.get(name+"_"+country);//CN仓
		Map<String,Integer> productingQuantity=productingMap.get(name+"_"+country);//PO
		
		Integer amazonBalance=0;
		Integer fbaTransBalance=0;
		Integer localTransBalance=0;
		Integer transBalance=0;
		Integer cnTransBalance=0;
		Integer productingBalance=0;
//		for(String week:weekList.keySet()){
		for(Map.Entry<String,Integer> weekEntry:weekList.entrySet()){
			String week = weekEntry.getKey();
			int i=weekEntry.getValue();
			int total=0;
			if(i==1){
				//Amazon仓balance
				amazonBalance=(amazonWareHouse.get(name+"_"+country)==null?0:(amazonWareHouse.get(name+"_"+country)))-avgWeekQuantity;
				setWeekQuantity(i,amazonBalance,gap);
				total+=(amazonWareHouse.get(name+"_"+country)==null?0:(amazonWareHouse.get(name+"_"+country)));
				//FBA在途balance
				if(fabTrans!=null&&fabTrans.size()>0&&fabTrans.get(week)!=null){
					total+=fabTrans.get(week);
					fbaTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,fbaTransBalance,gap1);
				}else{
					fbaTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,fbaTransBalance,gap1);
				}
				//本地仓balance
				if(localWareHouseTrans!=null&&localWareHouseTrans.size()>0&&localWareHouseTrans.get(week)!=null){
					total+=localWareHouseTrans.get(week);
					localTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,localTransBalance,gap2);
				}else{
					localTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,localTransBalance,gap2);
				}
				
				//本地运输balance
				if(localTransQuantity!=null&&localTransQuantity.size()>0&&localTransQuantity.get(week)!=null){
					total+=localTransQuantity.get(week);
					transBalance=total-avgWeekQuantity;
					setWeekQuantity(i,transBalance,gap3);
				}else{
					transBalance=total-avgWeekQuantity;
					setWeekQuantity(i,transBalance,gap3);
				}
				
				//cn仓balance
				if(cnTransQuantity!=null&&cnTransQuantity.size()>0&&cnTransQuantity.get(week)!=null){
					total+=cnTransQuantity.get(week);
					cnTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,cnTransBalance,gap4);
				}else{
					cnTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,cnTransBalance,gap4);
				}
				
				//PO balance
				if(productingQuantity!=null&&productingQuantity.size()>0&&productingQuantity.get(week)!=null){
					total+=productingQuantity.get(week);
					productingBalance=total-avgWeekQuantity;
					setWeekQuantity(i,productingBalance,gap5);
				}else{
					productingBalance=total-avgWeekQuantity;
					setWeekQuantity(i,productingBalance,gap5);
				}
				
			}else{
				//Amazon仓balance
				amazonBalance=amazonBalance-avgWeekQuantity;
				setWeekQuantity(i,amazonBalance,gap);
				
                 //FBA在途balance
				if(fabTrans!=null&&fabTrans.size()>0&&fabTrans.get(week)!=null){
					total+=fabTrans.get(week);
					fbaTransBalance=fbaTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,fbaTransBalance,gap1);
				}else{
					fbaTransBalance=fbaTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,fbaTransBalance,gap1);
				}
				//本地仓balance
				if(localWareHouseTrans!=null&&localWareHouseTrans.size()>0&&localWareHouseTrans.get(week)!=null){
					total+=localWareHouseTrans.get(week);
					localTransBalance=localTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,localTransBalance,gap2);
				}else{
					localTransBalance=localTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,localTransBalance,gap2);
				}
				
				//本地运输balance
				if(localTransQuantity!=null&&localTransQuantity.size()>0&&localTransQuantity.get(week)!=null){
					total+=localTransQuantity.get(week);
					transBalance=transBalance+total-avgWeekQuantity;
					setWeekQuantity(i,transBalance,gap3);
				}else{
					transBalance=transBalance+total-avgWeekQuantity;
					setWeekQuantity(i,transBalance,gap3);
				}
				
				//cn仓balance
				if(cnTransQuantity!=null&&cnTransQuantity.size()>0&&cnTransQuantity.get(week)!=null){
					total+=cnTransQuantity.get(week);
					cnTransBalance=cnTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,cnTransBalance,gap4);
				}else{
					cnTransBalance=cnTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,cnTransBalance,gap4);
				}
				
				//PO balance
				if(productingQuantity!=null&&productingQuantity.size()>0&&productingQuantity.get(week)!=null){
					total+=productingQuantity.get(week);
					productingBalance=productingBalance+total-avgWeekQuantity;
					setWeekQuantity(i,productingBalance,gap5);
				}else{
					productingBalance=productingBalance+total-avgWeekQuantity;
					setWeekQuantity(i,productingBalance,gap5);
				}
				
			}
		
		}
		gapList.add(gap);
		gapList.add(gap1);
		gapList.add(gap2);
		gapList.add(gap3);
		gapList.add(gap4);
		gapList.add(gap5);
	}
	
	public void setSafeBalanceInfo(Map<String,Map<String,Integer>> weekMap,Map<String,PsiProductInStock> stockMap,Map<String,Map<String,Integer>> fbaTransMap,Map<String,Map<String,Integer>> localStock,Map<String,Map<String,Integer>> localTrans,Map<String,Map<String,Integer>> cnTrans,Map<String,Map<String,Integer>> productingMap,
			Map<String,Integer> amazonWareHouse,Map<String,Map<String,Integer>>  sale30Map,String name,String country,Map<String,Integer> weekList,Date createDate,List<PsiInventoryGap> gapList){
		//Amazon仓balance
		PsiInventoryGap gap=new PsiInventoryGap();
		gap.setNameColor(name);
		gap.setCountry(country);
		gap.setForecastType("2");
		gap.setType("8");
		gap.setCreateDate(createDate);
		//Amazon仓balance
		
		//FBA在途balance
		PsiInventoryGap gap1=new PsiInventoryGap();
		gap1.setNameColor(name);
		gap1.setCountry(country);
		gap1.setForecastType("2");
		gap1.setType("9");
		gap1.setCreateDate(createDate);
		//FBA在途balance
		
		//本地仓balance
		PsiInventoryGap gap2=new PsiInventoryGap();
		gap2.setNameColor(name);
		gap2.setCountry(country);
		gap2.setForecastType("2");
		gap2.setType("10");
		gap2.setCreateDate(createDate);
		//本地仓balance
		
		PsiInventoryGap gap3=new PsiInventoryGap();
		gap3.setNameColor(name);
		gap3.setCountry(country);
		gap3.setForecastType("2");
		gap3.setType("11");
		gap3.setCreateDate(createDate);
		
		PsiInventoryGap gap4=new PsiInventoryGap();
		gap4.setNameColor(name);
		gap4.setCountry(country);
		gap4.setForecastType("2");
		gap4.setType("12");
		gap4.setCreateDate(createDate);
		
		PsiInventoryGap gap5=new PsiInventoryGap();
		gap5.setNameColor(name);
		gap5.setCountry(country);
		gap5.setForecastType("2");
		gap5.setType("13");
		gap5.setCreateDate(createDate);
		
		//Integer totalSal=(sale30Map.get(country)==null||sale30Map.get(country).get(name)==null?0:sale30Map.get(country).get(name));
		Integer safeInventory=0;
		if(stockMap!=null&&stockMap.get(name)!=null){
			safeInventory=MathUtils.roundUp(stockMap.get(name).getSafeInventory());
		}
		//Integer avgWeekQuantity=MathUtils.roundUp(totalSal/30d)*7+safeInventory;
		
		Map<String,Integer> fabTrans=fbaTransMap.get(name+"_"+country);
		//本地仓
		Map<String,Integer> localWareHouseTrans=localStock.get(name+"_"+country);
		
		Map<String,Integer> localTransQuantity=localTrans.get(name+"_"+country);//本地运输
		Map<String,Integer> cnTransQuantity=cnTrans.get(name+"_"+country);//CN仓
		Map<String,Integer> productingQuantity=productingMap.get(name+"_"+country);//PO
		
		Integer amazonBalance=0;
		Integer fbaTransBalance=0;
		Integer localTransBalance=0;
		Integer transBalance=0;
		Integer cnTransBalance=0;
		Integer productingBalance=0;
//		for(String week:weekList.keySet()){
		for(Map.Entry<String,Integer> weekEntry:weekList.entrySet()){
			String week = weekEntry.getKey();
			int i=weekEntry.getValue();
			int total=0;
			Integer avgWeekQuantity=0;
			if(weekMap!=null&&weekMap.get(name+"_"+country)!=null&&weekMap.get(name+"_"+country).get(week)!=null){
				avgWeekQuantity=weekMap.get(name+"_"+country).get(week);
			}
			if(i==1){
				//Amazon仓balance
				amazonBalance=(amazonWareHouse.get(name+"_"+country)==null?0:(amazonWareHouse.get(name+"_"+country)))-avgWeekQuantity;
				setWeekQuantity(i,amazonBalance-safeInventory,gap);
				total+=(amazonWareHouse.get(name+"_"+country)==null?0:(amazonWareHouse.get(name+"_"+country)));
				//FBA在途balance
				if(fabTrans!=null&&fabTrans.size()>0&&fabTrans.get(week)!=null){
					total+=fabTrans.get(week);
					fbaTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,fbaTransBalance-safeInventory,gap1);
				}else{
					fbaTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,fbaTransBalance-safeInventory,gap1);
				}
				//本地仓balance
				if(localWareHouseTrans!=null&&localWareHouseTrans.size()>0&&localWareHouseTrans.get(week)!=null){
					total+=localWareHouseTrans.get(week);
					localTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,localTransBalance-safeInventory,gap2);
				}else{
					localTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,localTransBalance-safeInventory,gap2);
				}
				
				//本地运输balance
				if(localTransQuantity!=null&&localTransQuantity.size()>0&&localTransQuantity.get(week)!=null){
					total+=localTransQuantity.get(week);
					transBalance=total-avgWeekQuantity;
					setWeekQuantity(i,transBalance-safeInventory,gap3);
				}else{
					transBalance=total-avgWeekQuantity;
					setWeekQuantity(i,transBalance-safeInventory,gap3);
				}
				
				//cn仓balance
				if(cnTransQuantity!=null&&cnTransQuantity.size()>0&&cnTransQuantity.get(week)!=null){
					total+=cnTransQuantity.get(week);
					cnTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,cnTransBalance-safeInventory,gap4);
				}else{
					cnTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,cnTransBalance-safeInventory,gap4);
				}
				
				//PO balance
				if(productingQuantity!=null&&productingQuantity.size()>0&&productingQuantity.get(week)!=null){
					total+=productingQuantity.get(week);
					productingBalance=total-avgWeekQuantity;
					setWeekQuantity(i,productingBalance-safeInventory,gap5);
				}else{
					productingBalance=total-avgWeekQuantity;
					setWeekQuantity(i,productingBalance-safeInventory,gap5);
				}
				
			}else{
				//Amazon仓balance
				amazonBalance=amazonBalance-avgWeekQuantity;
				setWeekQuantity(i,amazonBalance-safeInventory,gap);
				
                 //FBA在途balance
				if(fabTrans!=null&&fabTrans.size()>0&&fabTrans.get(week)!=null){
					total+=fabTrans.get(week);
					fbaTransBalance=fbaTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,fbaTransBalance-safeInventory,gap1);
				}else{
					fbaTransBalance=fbaTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,fbaTransBalance-safeInventory,gap1);
				}
				//本地仓balance
				if(localWareHouseTrans!=null&&localWareHouseTrans.size()>0&&localWareHouseTrans.get(week)!=null){
					total+=localWareHouseTrans.get(week);
					localTransBalance=localTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,localTransBalance-safeInventory,gap2);
				}else{
					localTransBalance=localTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,localTransBalance-safeInventory,gap2);
				}
				
				//本地运输balance
				if(localTransQuantity!=null&&localTransQuantity.size()>0&&localTransQuantity.get(week)!=null){
					total+=localTransQuantity.get(week);
					transBalance=transBalance+total-avgWeekQuantity;
					setWeekQuantity(i,transBalance-safeInventory,gap3);
				}else{
					transBalance=transBalance+total-avgWeekQuantity;
					setWeekQuantity(i,transBalance-safeInventory,gap3);
				}
				
				//cn仓balance
				if(cnTransQuantity!=null&&cnTransQuantity.size()>0&&cnTransQuantity.get(week)!=null){
					total+=cnTransQuantity.get(week);
					cnTransBalance=cnTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,cnTransBalance-safeInventory,gap4);
				}else{
					cnTransBalance=cnTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,cnTransBalance-safeInventory,gap4);
				}
				
				//PO balance
				if(productingQuantity!=null&&productingQuantity.size()>0&&productingQuantity.get(week)!=null){
					total+=productingQuantity.get(week);
					productingBalance=productingBalance+total-avgWeekQuantity;
					setWeekQuantity(i,productingBalance-safeInventory,gap5);
				}else{
					productingBalance=productingBalance+total-avgWeekQuantity;
					setWeekQuantity(i,productingBalance-safeInventory,gap5);
				}
				
			}
		
		}
		gapList.add(gap);
		gapList.add(gap1);
		gapList.add(gap2);
		gapList.add(gap3);
		gapList.add(gap4);
		gapList.add(gap5);
	}
	
	
	public void setBalanceInfo2(Map<String,Map<String,Integer>> forecastMap,Map<String,Map<String,Integer>> fbaTransMap,Map<String,Map<String,Integer>> localStock,Map<String,Map<String,Integer>> localTrans,Map<String,Map<String,Integer>> cnTrans,Map<String,Map<String,Integer>> productingMap,
			Map<String,Integer> amazonWareHouse,Map<String,Map<String,Integer>>  sale30Map,String name,String country,Map<String,Integer> weekList,Date createDate,List<PsiInventoryGap> gapList){
		//Amazon仓balance
		PsiInventoryGap gap=new PsiInventoryGap();
		gap.setNameColor(name);
		gap.setCountry(country);
		gap.setForecastType("0");
		gap.setType("8");
		gap.setCreateDate(createDate);
		//Amazon仓balance
		
		//FBA在途balance
		PsiInventoryGap gap1=new PsiInventoryGap();
		gap1.setNameColor(name);
		gap1.setCountry(country);
		gap1.setForecastType("0");
		gap1.setType("9");
		gap1.setCreateDate(createDate);
		//FBA在途balance
		
		//本地仓balance
		PsiInventoryGap gap2=new PsiInventoryGap();
		gap2.setNameColor(name);
		gap2.setCountry(country);
		gap2.setForecastType("0");
		gap2.setType("10");
		gap2.setCreateDate(createDate);
		//本地仓balance
		
		PsiInventoryGap gap3=new PsiInventoryGap();
		gap3.setNameColor(name);
		gap3.setCountry(country);
		gap3.setForecastType("0");
		gap3.setType("11");
		gap3.setCreateDate(createDate);
		
		PsiInventoryGap gap4=new PsiInventoryGap();
		gap4.setNameColor(name);
		gap4.setCountry(country);
		gap4.setForecastType("0");
		gap4.setType("12");
		gap4.setCreateDate(createDate);
		
		PsiInventoryGap gap5=new PsiInventoryGap();
		gap5.setNameColor(name);
		gap5.setCountry(country);
		gap5.setForecastType("0");
		gap5.setType("13");
		gap5.setCreateDate(createDate);
		
		
		
		Map<String,Integer> fabTrans=fbaTransMap.get(name+"_"+country);
		//本地仓
		Map<String,Integer> localWareHouseTrans=localStock.get(name+"_"+country);
		
		Map<String,Integer> localTransQuantity=localTrans.get(name+"_"+country);//本地运输
		Map<String,Integer> cnTransQuantity=cnTrans.get(name+"_"+country);//CN仓
		Map<String,Integer> productingQuantity=productingMap.get(name+"_"+country);//PO
		
		Integer amazonBalance=0;
		Integer fbaTransBalance=0;
		Integer localTransBalance=0;
		Integer transBalance=0;
		Integer cnTransBalance=0;
		Integer productingBalance=0;
//		for(String week:weekList.keySet()){
		for(Map.Entry<String,Integer> weekEntry:weekList.entrySet()){
			String week = weekEntry.getKey();
			int i=weekEntry.getValue();
			Integer avgWeekQuantity=0;
			if(forecastMap!=null&&forecastMap.get(name+"_"+country)!=null&&forecastMap.get(name+"_"+country).get(week)!=null){
				avgWeekQuantity=forecastMap.get(name+"_"+country).get(week);
			}
			int total=0;
			if(i==1){
				//Amazon仓balance
				amazonBalance=(amazonWareHouse.get(name+"_"+country)==null?0:(amazonWareHouse.get(name+"_"+country)))-avgWeekQuantity;
				setWeekQuantity(i,amazonBalance,gap);
				total+=(amazonWareHouse.get(name+"_"+country)==null?0:(amazonWareHouse.get(name+"_"+country)));
				//FBA在途balance
				if(fabTrans!=null&&fabTrans.size()>0&&fabTrans.get(week)!=null){
					total+=fabTrans.get(week);
					fbaTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,fbaTransBalance,gap1);
				}else{
					fbaTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,fbaTransBalance,gap1);
				}
				//本地仓balance
				if(localWareHouseTrans!=null&&localWareHouseTrans.size()>0&&localWareHouseTrans.get(week)!=null){
					total+=localWareHouseTrans.get(week);
					localTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,localTransBalance,gap2);
				}else{
					localTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,localTransBalance,gap2);
				}
				
				//本地运输balance
				if(localTransQuantity!=null&&localTransQuantity.size()>0&&localTransQuantity.get(week)!=null){
					total+=localTransQuantity.get(week);
					transBalance=total-avgWeekQuantity;
					setWeekQuantity(i,transBalance,gap3);
				}else{
					transBalance=total-avgWeekQuantity;
					setWeekQuantity(i,transBalance,gap3);
				}
				
				//cn仓balance
				if(cnTransQuantity!=null&&cnTransQuantity.size()>0&&cnTransQuantity.get(week)!=null){
					total+=cnTransQuantity.get(week);
					cnTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,cnTransBalance,gap4);
				}else{
					cnTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,cnTransBalance,gap4);
				}
				
				//PO balance
				if(productingQuantity!=null&&productingQuantity.size()>0&&productingQuantity.get(week)!=null){
					total+=productingQuantity.get(week);
					productingBalance=total-avgWeekQuantity;
					setWeekQuantity(i,productingBalance,gap5);
				}else{
					productingBalance=total-avgWeekQuantity;
					setWeekQuantity(i,productingBalance,gap5);
				}
				
			}else{
				//Amazon仓balance
				amazonBalance=amazonBalance-avgWeekQuantity;
				setWeekQuantity(i,amazonBalance,gap);
				
                 //FBA在途balance
				if(fabTrans!=null&&fabTrans.size()>0&&fabTrans.get(week)!=null){
					total+=fabTrans.get(week);
					fbaTransBalance=fbaTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,fbaTransBalance,gap1);
				}else{
					fbaTransBalance=fbaTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,fbaTransBalance,gap1);
				}
				//本地仓balance
				if(localWareHouseTrans!=null&&localWareHouseTrans.size()>0&&localWareHouseTrans.get(week)!=null){
					total+=localWareHouseTrans.get(week);
					localTransBalance=localTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,localTransBalance,gap2);
				}else{
					localTransBalance=localTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,localTransBalance,gap2);
				}
				
				//本地运输balance
				if(localTransQuantity!=null&&localTransQuantity.size()>0&&localTransQuantity.get(week)!=null){
					total+=localTransQuantity.get(week);
					transBalance=transBalance+total-avgWeekQuantity;
					setWeekQuantity(i,transBalance,gap3);
				}else{
					transBalance=transBalance+total-avgWeekQuantity;
					setWeekQuantity(i,transBalance,gap3);
				}
				
				//cn仓balance
				if(cnTransQuantity!=null&&cnTransQuantity.size()>0&&cnTransQuantity.get(week)!=null){
					total+=cnTransQuantity.get(week);
					cnTransBalance=cnTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,cnTransBalance,gap4);
				}else{
					cnTransBalance=cnTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,cnTransBalance,gap4);
				}
				
				//PO balance
				if(productingQuantity!=null&&productingQuantity.size()>0&&productingQuantity.get(week)!=null){
					total+=productingQuantity.get(week);
					productingBalance=productingBalance+total-avgWeekQuantity;
					setWeekQuantity(i,productingBalance,gap5);
				}else{
					productingBalance=productingBalance+total-avgWeekQuantity;
					setWeekQuantity(i,productingBalance,gap5);
				}
				
			}
		
		}
		gapList.add(gap);
		gapList.add(gap1);
		gapList.add(gap2);
		gapList.add(gap3);
		gapList.add(gap4);
		gapList.add(gap5);
	}
	
	public void setSafeBalanceInfo2(Map<String,PsiProductInStock> stockMap,Map<String,Map<String,Integer>> forecastMap,Map<String,Map<String,Integer>> fbaTransMap,Map<String,Map<String,Integer>> localStock,Map<String,Map<String,Integer>> localTrans,Map<String,Map<String,Integer>> cnTrans,Map<String,Map<String,Integer>> productingMap,
			Map<String,Integer> amazonWareHouse,Map<String,Map<String,Integer>>  sale30Map,String name,String country,Map<String,Integer> weekList,Date createDate,List<PsiInventoryGap> gapList){
		//Amazon仓balance
		PsiInventoryGap gap=new PsiInventoryGap();
		gap.setNameColor(name);
		gap.setCountry(country);
		gap.setForecastType("3");
		gap.setType("8");
		gap.setCreateDate(createDate);
		//Amazon仓balance
		
		//FBA在途balance
		PsiInventoryGap gap1=new PsiInventoryGap();
		gap1.setNameColor(name);
		gap1.setCountry(country);
		gap1.setForecastType("3");
		gap1.setType("9");
		gap1.setCreateDate(createDate);
		//FBA在途balance
		
		//本地仓balance
		PsiInventoryGap gap2=new PsiInventoryGap();
		gap2.setNameColor(name);
		gap2.setCountry(country);
		gap2.setForecastType("3");
		gap2.setType("10");
		gap2.setCreateDate(createDate);
		//本地仓balance
		
		PsiInventoryGap gap3=new PsiInventoryGap();
		gap3.setNameColor(name);
		gap3.setCountry(country);
		gap3.setForecastType("3");
		gap3.setType("11");
		gap3.setCreateDate(createDate);
		
		PsiInventoryGap gap4=new PsiInventoryGap();
		gap4.setNameColor(name);
		gap4.setCountry(country);
		gap4.setForecastType("3");
		gap4.setType("12");
		gap4.setCreateDate(createDate);
		
		PsiInventoryGap gap5=new PsiInventoryGap();
		gap5.setNameColor(name);
		gap5.setCountry(country);
		gap5.setForecastType("3");
		gap5.setType("13");
		gap5.setCreateDate(createDate);
		
		
		
		Map<String,Integer> fabTrans=fbaTransMap.get(name+"_"+country);
		//本地仓
		Map<String,Integer> localWareHouseTrans=localStock.get(name+"_"+country);
		
		Map<String,Integer> localTransQuantity=localTrans.get(name+"_"+country);//本地运输
		Map<String,Integer> cnTransQuantity=cnTrans.get(name+"_"+country);//CN仓
		Map<String,Integer> productingQuantity=productingMap.get(name+"_"+country);//PO
		
		Integer amazonBalance=0;
		Integer fbaTransBalance=0;
		Integer localTransBalance=0;
		Integer transBalance=0;
		Integer cnTransBalance=0;
		Integer productingBalance=0;
		Integer safeInventory=0;
		if(stockMap!=null&&stockMap.get(name)!=null){
			safeInventory=MathUtils.roundUp(stockMap.get(name).getSafeInventory());
		}
//		for(String week:weekList.keySet()){
		for(Map.Entry<String,Integer> weekEntry:weekList.entrySet()){
			String week = weekEntry.getKey();
			int i=weekEntry.getValue();
			Integer avgWeekQuantity=0;
			if(forecastMap!=null&&forecastMap.get(name+"_"+country)!=null&&forecastMap.get(name+"_"+country).get(week)!=null){
				avgWeekQuantity=forecastMap.get(name+"_"+country).get(week);
			}
			int total=0;
			if(i==1){
				//Amazon仓balance
				amazonBalance=(amazonWareHouse.get(name+"_"+country)==null?0:(amazonWareHouse.get(name+"_"+country)))-avgWeekQuantity;
				setWeekQuantity(i,amazonBalance-safeInventory,gap);
				total+=(amazonWareHouse.get(name+"_"+country)==null?0:(amazonWareHouse.get(name+"_"+country)));
				//FBA在途balance
				if(fabTrans!=null&&fabTrans.size()>0&&fabTrans.get(week)!=null){
					total+=fabTrans.get(week);
					fbaTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,fbaTransBalance-safeInventory,gap1);
				}else{
					fbaTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,fbaTransBalance-safeInventory,gap1);
				}
				//本地仓balance
				if(localWareHouseTrans!=null&&localWareHouseTrans.size()>0&&localWareHouseTrans.get(week)!=null){
					total+=localWareHouseTrans.get(week);
					localTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,localTransBalance-safeInventory,gap2);
				}else{
					localTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,localTransBalance-safeInventory,gap2);
				}
				
				//本地运输balance
				if(localTransQuantity!=null&&localTransQuantity.size()>0&&localTransQuantity.get(week)!=null){
					total+=localTransQuantity.get(week);
					transBalance=total-avgWeekQuantity;
					setWeekQuantity(i,transBalance-safeInventory,gap3);
				}else{
					transBalance=total-avgWeekQuantity;
					setWeekQuantity(i,transBalance-safeInventory,gap3);
				}
				
				//cn仓balance
				if(cnTransQuantity!=null&&cnTransQuantity.size()>0&&cnTransQuantity.get(week)!=null){
					total+=cnTransQuantity.get(week);
					cnTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,cnTransBalance-safeInventory,gap4);
				}else{
					cnTransBalance=total-avgWeekQuantity;
					setWeekQuantity(i,cnTransBalance-safeInventory,gap4);
				}
				
				//PO balance
				if(productingQuantity!=null&&productingQuantity.size()>0&&productingQuantity.get(week)!=null){
					total+=productingQuantity.get(week);
					productingBalance=total-avgWeekQuantity;
					setWeekQuantity(i,productingBalance-safeInventory,gap5);
				}else{
					productingBalance=total-avgWeekQuantity;
					setWeekQuantity(i,productingBalance-safeInventory,gap5);
				}
				
			}else{
				//Amazon仓balance
				amazonBalance=amazonBalance-avgWeekQuantity;
				setWeekQuantity(i,amazonBalance-safeInventory,gap);
				
                 //FBA在途balance
				if(fabTrans!=null&&fabTrans.size()>0&&fabTrans.get(week)!=null){
					total+=fabTrans.get(week);
					fbaTransBalance=fbaTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,fbaTransBalance-safeInventory,gap1);
				}else{
					fbaTransBalance=fbaTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,fbaTransBalance-safeInventory,gap1);
				}
				//本地仓balance
				if(localWareHouseTrans!=null&&localWareHouseTrans.size()>0&&localWareHouseTrans.get(week)!=null){
					total+=localWareHouseTrans.get(week);
					localTransBalance=localTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,localTransBalance-safeInventory,gap2);
				}else{
					localTransBalance=localTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,localTransBalance-safeInventory,gap2);
				}
				
				//本地运输balance
				if(localTransQuantity!=null&&localTransQuantity.size()>0&&localTransQuantity.get(week)!=null){
					total+=localTransQuantity.get(week);
					transBalance=transBalance+total-avgWeekQuantity;
					setWeekQuantity(i,transBalance-safeInventory,gap3);
				}else{
					transBalance=transBalance+total-avgWeekQuantity;
					setWeekQuantity(i,transBalance-safeInventory,gap3);
				}
				
				//cn仓balance
				if(cnTransQuantity!=null&&cnTransQuantity.size()>0&&cnTransQuantity.get(week)!=null){
					total+=cnTransQuantity.get(week);
					cnTransBalance=cnTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,cnTransBalance-safeInventory,gap4);
				}else{
					cnTransBalance=cnTransBalance+total-avgWeekQuantity;
					setWeekQuantity(i,cnTransBalance-safeInventory,gap4);
				}
				
				//PO balance
				if(productingQuantity!=null&&productingQuantity.size()>0&&productingQuantity.get(week)!=null){
					total+=productingQuantity.get(week);
					productingBalance=productingBalance+total-avgWeekQuantity;
					setWeekQuantity(i,productingBalance-safeInventory,gap5);
				}else{
					productingBalance=productingBalance+total-avgWeekQuantity;
					setWeekQuantity(i,productingBalance-safeInventory,gap5);
				}
				
			}
		
		}
		gapList.add(gap);
		gapList.add(gap1);
		gapList.add(gap2);
		gapList.add(gap3);
		gapList.add(gap4);
		gapList.add(gap5);
	}
	
	public void  setOthereInfo(Map<String,Map<String,Integer>> map,String name,String country,Map<String,Integer> weekList,Date createDate,List<PsiInventoryGap> gapList,String type,String forecastType){
		if(map!=null&&map.size()>0){
			Map<String,Integer> trans=map.get(name+"_"+country);
			if(trans!=null&&trans.size()>0){
				PsiInventoryGap tranGap=new PsiInventoryGap();
				tranGap.setNameColor(name);
				tranGap.setCountry("eu".equals(country)?"de":country);
				tranGap.setForecastType(forecastType);
				tranGap.setType(type);
				tranGap.setCreateDate(createDate);
//				for(String w:trans.keySet()){
				for(Map.Entry<String,Integer> entry:trans.entrySet()){
					Integer c=entry.getValue();
					if(c!=null&&c<=16){
						setWeekQuantity(c,entry.getValue(),tranGap);
					}
				}
				gapList.add(tranGap);
			}
		}
	}
	
	public void setAllInfo(Map<String,Map<String,Integer>> weekMap,List<PsiInventoryGap> gapList,String name,String country,Date createDate,Map<String,Map<String,Integer>>  sale30Map,
			Map<String,PsiInventoryFba> amazonStock,Map<String,Integer> totalFbaTrans,Map<String,Map<String,Integer>> fbaTransMap,Map<String,Integer> weekList
			){
			PsiInventoryGap gap=new PsiInventoryGap();
			Integer avgWeekQuantity=MathUtils.roundUp((sale30Map.get(country)==null||sale30Map.get(country).get(name)==null?0:sale30Map.get(country).get(name))/30d)*7;
			
			Map<String,PsiProductInStock> stockMap=psiProductInStockService.getHistoryInventory(country);
			Integer period=16;
			if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).getPeriod()!=null){
				period=MathUtils.roundUp(stockMap.get(name).getPeriod()*1.0/7);
			}
			
			gap.setNameColor(name);
			gap.setCountry(country);
			gap.setForecastType("1");
			gap.setType("1");
			gap.setCreateDate(createDate);
			/*for(int i=1;i<=16;i++){
				//setWeekQuantity(i,avgWeekQuantity,gap);
				if(i>period){
					setWeekQuantity(i,0,gap);
				}else{
					setWeekQuantity(i,avgWeekQuantity,gap);
				}
			}*/
			Map<String,Integer> temp=weekMap.get(name+"_"+country);
			if(temp==null){
				temp=Maps.newHashMap();
				weekMap.put(name+"_"+country,temp);
			}
//			for(String week:weekList.keySet()){
			for(Map.Entry<String,Integer> weekEntry:weekList.entrySet()){
				String week = weekEntry.getKey();
				if(weekEntry.getValue()>period){
					setWeekQuantity(weekEntry.getValue(),0,gap);
					temp.put(week,0);
				}else{
					setWeekQuantity(weekEntry.getValue(),avgWeekQuantity,gap);
					temp.put(week,avgWeekQuantity);
				}
			}
			gapList.add(gap);
			

			//亚马逊仓在途3
			if(fbaTransMap!=null&&fbaTransMap.size()>0){
				Map<String,Integer> fabTrans=fbaTransMap.get(name+"_"+country);
				if(fabTrans!=null&&fabTrans.size()>0){
					PsiInventoryGap tranGap=new PsiInventoryGap();
					tranGap.setNameColor(name);
					tranGap.setCountry(country);
					tranGap.setForecastType("1");
					tranGap.setType("3");
					tranGap.setCreateDate(createDate);
					for(Map.Entry<String,Integer> entry:fabTrans.entrySet()){
						Integer c=entry.getValue();
						if(c!=null&&c<=16){
							setWeekQuantity(c,entry.getValue(),tranGap);
						}
					}
					gapList.add(tranGap);
				}
			}
			//亚马逊仓在途
		
	}
	
	
	public void setAllInfo2(List<PsiInventoryGap> gapList,String name,String country,Date createDate,Map<String,Map<String,Integer>>  sale30Map,
			Map<String,PsiInventoryFba> amazonStock,Map<String,Integer> totalFbaTrans,Map<String,Map<String,Integer>> fbaTransMap,Map<String,Integer> weekList
			){
			PsiInventoryGap gap=new PsiInventoryGap();
			Integer avgWeekQuantity=MathUtils.roundUp((sale30Map.get(country)==null||sale30Map.get(country).get(name)==null?0:sale30Map.get(country).get(name))/30d)*7;
			
			Map<String,PsiProductInStock> stockMap=psiProductInStockService.getHistoryInventory(country);
			Integer period=16;
			if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).getPeriod()!=null){
				period=MathUtils.roundUp(stockMap.get(name).getPeriod()*1.0/7);
			}
			
			gap.setNameColor(name);
			gap.setCountry(country);
			gap.setForecastType("2");
			gap.setType("1");
			gap.setCreateDate(createDate);
			for(int i=1;i<=16;i++){
				//setWeekQuantity(i,avgWeekQuantity,gap);
				if(i>period){
					setWeekQuantity(i,0,gap);
				}else{
					setWeekQuantity(i,avgWeekQuantity,gap);
				}
			}
			gapList.add(gap);
			

			//亚马逊仓在途3
			if(fbaTransMap!=null&&fbaTransMap.size()>0){
				Map<String,Integer> fabTrans=fbaTransMap.get(name+"_"+country);
				if(fabTrans!=null&&fabTrans.size()>0){
					PsiInventoryGap tranGap=new PsiInventoryGap();
					tranGap.setNameColor(name);
					tranGap.setCountry(country);
					tranGap.setForecastType("2");
					tranGap.setType("3");
					tranGap.setCreateDate(createDate);
//					for(String w:fabTrans.keySet()){
					for(Map.Entry<String,Integer> entry:fabTrans.entrySet()){
						Integer c=entry.getValue();
						if(c!=null&&c<=16){
							setWeekQuantity(c,entry.getValue(),tranGap);
						}
					}
					gapList.add(tranGap);
				}
			}
			//亚马逊仓在途
		
	}
	
	public void setWeekQuantity(int i,int quantity,PsiInventoryGap gap){
		if(i==1){
			gap.setWeek1(quantity);
		}else if(i==2){
			gap.setWeek2(quantity);
		}else if(i==3){
			gap.setWeek3(quantity);
		}else if(i==4){
			gap.setWeek4(quantity);
		}else if(i==5){
			gap.setWeek5(quantity);
		}else if(i==6){
			gap.setWeek6(quantity);
		}else if(i==7){
			gap.setWeek7(quantity);
		}else if(i==8){
			gap.setWeek8(quantity);
		}else if(i==9){
			gap.setWeek9(quantity);
		}else if(i==10){
			gap.setWeek10(quantity);
		}else if(i==11){
			gap.setWeek11(quantity);
		}else if(i==12){
			gap.setWeek12(quantity);
		}else if(i==13){
			gap.setWeek13(quantity);
		}else if(i==14){
			gap.setWeek14(quantity);
		}else if(i==15){
			gap.setWeek15(quantity);
		}else if(i==16){
			gap.setWeek16(quantity);
		}
	}
	
	public void savePsiOutOfStockInfoList() {
		LOGGER.info("开始查询今天断货调价产品");
		List<PsiOutOfStockInfo> psiOutOfStockInfoList=Lists.newArrayList();
		List<Object[]> productList=psiOutOfStockInfoService.getOutOfStockChangePrice();
		//产品名_国家 fba
		Map<String, PsiInventoryFba>  fbas = psiInventoryService.getAllProductFbaInfo();
		//产品名_国家
		Map<String,ProductSalesInfo> fancha = productSalesInfoService.findAll();
		
		Map<String,Map<String,List<PsiTransportForecastDto>>> forecastMap=psiTransportOrderService.findTransportForecast();
		SimpleDateFormat pattern=new SimpleDateFormat("yyyy-MM-dd");
		Date date=new Date();
		for (Object[] obj : productList) {
			PsiOutOfStockInfo info=new PsiOutOfStockInfo();
			info.setCountry(obj[0].toString());
			info.setSku(obj[1].toString());
			info.setAfterPrice(Float.parseFloat(obj[2].toString()));
			info.setProductName(obj[3].toString());
			info.setColor(obj[4].toString());
			info.setCreateDate(date);
			String key=obj[3].toString()+(StringUtils.isBlank(obj[4].toString())?"":("_"+obj[4].toString()))+"_"+obj[0].toString();
			info.setFbaQuantity(fbas.get(key)==null?0:fbas.get(key).getTotal());
			info.setQuantityDay31(fancha.get(key)==null?0:fancha.get(key).getDay31Sales());
			Float price=amazonProduct2Service.getProductPriceYesterday(obj[0].toString(),obj[1].toString());
			if(price!=null){
				info.setBeforePrice(price);
			}
			Map<String,List<PsiTransportForecastDto>> temp=forecastMap.get(obj[1].toString());
			if(temp!=null&&temp.size()>0){
				if(temp.get("0")!=null){//在途
					List<PsiTransportForecastDto> temp0=temp.get("0");
					StringBuilder content= new StringBuilder("");
				    for(PsiTransportForecastDto forecastDto:temp0){
				    	content.append(pattern.format(forecastDto.getForecastDate())).append(",数量:").append(forecastDto.getQuantity()).append(",天数:")
				    	.append(forecastDto.getSeparateDay()).append("<br/>");
				    }
				    info.setInfo1(content.toString());
				}
                if(temp.get("1")!=null){//在产
                	List<PsiTransportForecastDto> temp0=temp.get("1");
                	StringBuilder content=new StringBuilder();
				    for(PsiTransportForecastDto forecastDto:temp0){
				    	content.append(pattern.format(forecastDto.getForecastDate())).append(",数量:").append(forecastDto.getQuantity()).append(",天数:")
				    	.append(forecastDto.getSeparateDay()).append("<br/>");
				    }
				    info.setInfo2(content.toString());
				}
                if(temp.get("2")!=null){//在仓（CN）
                	List<PsiTransportForecastDto> temp0=temp.get("2");
                	StringBuilder content=new StringBuilder();
				    for(PsiTransportForecastDto forecastDto:temp0){
				    	content.append(pattern.format(forecastDto.getForecastDate())).append(",数量:").append(forecastDto.getQuantity()).append(",天数:")
				    	.append(forecastDto.getSeparateDay()).append("<br/>");
				    }
				    info.setInfo3(content.toString());
				}
                if(temp.get("3")!=null){//在仓（海外）
                	List<PsiTransportForecastDto> temp0=temp.get("3");
                	StringBuilder content=new StringBuilder();
				    for(PsiTransportForecastDto forecastDto:temp0){
				    	content.append(pattern.format(forecastDto.getForecastDate())).append(",数量:").append(forecastDto.getQuantity()).append(",天数:")
				    	.append(forecastDto.getSeparateDay()).append("<br/>");
				    }
				    info.setInfo4(content.toString());
				}
			}
			psiOutOfStockInfoList.add(info);
		}
		psiOutOfStockInfoService.save(psiOutOfStockInfoList);
		LOGGER.info("查询今天断货调价产品结束！");
	}
	
	public void sendEuGapEmail() {
		try{
			List<String> productList=psiProductEliminateService.findNoCnPI();
			if(productList!=null&&productList.size()>0){
				StringBuffer contents= new StringBuffer("Hi all,以下产品未录入出口价格<br/><br/>");
				final MailInfo mailInfo = new MailInfo("bella@inateck.com","未录入出口价格提醒"+DateUtils.getDate("-yyyy/MM/dd"),new Date());
				for(String name:productList){
					contents.append(name+"<br/>");
				}
				mailInfo.setContent(contents.toString());
				mailInfo.setCcToAddress("alisa@inateck.com,belinda@inateck.com,eileen@inateck.com");
				new Thread(){
					public void run(){   
						mailManager.send(mailInfo);
					}
				}.start();
			}
		}catch(Exception e){
			LOGGER.error("新品无价格提醒异常",e);
		}
		
		
		LOGGER.info("开始德国本地仓发货提醒...");
		//country-预测类型-name-type
		Map<String,Map<String,Map<String,Map<String,PsiInventoryGap>>>>  gapMap=psiOutOfStockInfoService.findEuCountryGap();
		Map<String,PsiInventoryFba> fbaStock=psiInventoryService.getAllProductFbaInfo();//productName+"_"+country
		//Map<String,Map<String,Integer>>  sale30Map = psiInventoryFbaService.get31SalesMap(null);//country_name_quantity
		Map<String,Map<String,Integer>> workingFba=fbaInboundService.findWorkingFBA();
		Map<String,String> powerMap=psiProductService.getHasPowerByName();
		Map<String,PsiProductInStock> euStockMap=psiProductInStockService.getHistoryInventory("eu");
		Map<String,Map<String,Integer>> deStock=psiOutOfStockInfoService.getDeStock();
		Map<String,Integer> packageQuantity=psiProductService.findPackQuantityMap();
		List<String> countryList=Lists.newArrayList("de","fr","uk","it","es");
		Map<String,List<String>> localMap=psiOutOfStockInfoService.getLoclaTransDetail();
		Map<String,Map<String,PsiInventoryGap>> weekSales=psiOutOfStockInfoService.findByCountryName();
		DateFormat formatWeek = new SimpleDateFormat("yyyyww");
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		Date date=new Date();
		if(date.getDay()==0){
			date = DateUtils.addDays(date, -1);
		}
		String key = formatWeek.format(date);
		int year =DateUtils.getSunday(date).getYear()+1900;
		int week =  Integer.parseInt(key.substring(4));
		if(week<10){
			key = year+"0"+week;
		}else{
			key =year+""+week;
		}
		StringBuffer contents= new StringBuffer("Hi all,今天是"+key+"周，以下是未来三周，欧洲FBA在途balance低于安全库存的型号。请及时提醒德国仓发货，以免断货。谢谢！(预计断货日仅依据FBA库存计算)<br/>");
		StringBuffer temp= new StringBuffer("");
		contents.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
		String toAddress ="lisa@inateck.com,susie@inateck.com";
		//String toAddress ="eileen@inateck.com";
		if(gapMap!=null&&gapMap.size()>0){
			for (Map.Entry<String, Map<String, Map<String, Map<String, PsiInventoryGap>>>> entryCountry: gapMap.entrySet()) {
				String country = entryCountry.getKey();
				Map<String,PsiProductInStock> stockMap=psiProductInStockService.getHistoryInventory(country);
				if("de".equals(country)){
					Map<String,Map<String,Map<String,PsiInventoryGap>>> countryGap=entryCountry.getValue();
					if(countryGap!=null&&countryGap.size()>0){
						temp.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='11'><span style='font-weight: bold;font-size:25px'>eu_FBA在途balance低于安全库存详情</span></td></tr>");
						temp.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '><th>依据</th><th>产品</th><th>安全库存</th><th>德国仓库存</th><th>FBA库存</th><th>FBA在途</th><th>预计断货日</th><th>缺口量(箱数)</th><th>过去31天平均周销量</th><th>3周内在途预计到达数</th><th>建帖数</th></tr>");
						for (Map.Entry<String,Map<String,Map<String,PsiInventoryGap>>> countryEntry: countryGap.entrySet()) {
							String forecastType = countryEntry.getKey();
							Map<String,Map<String,PsiInventoryGap>> forecastMap=countryEntry.getValue();
							if(forecastMap!=null&&forecastMap.size()>0){
								for(Map.Entry<String,Map<String,PsiInventoryGap>> entry:forecastMap.entrySet()){
									String name = entry.getKey();
									if("0".equals(powerMap.get(name))){
										Map<String,PsiInventoryGap> typeMap=entry.getValue();
										if(typeMap!=null&&typeMap.size()>0){
											String keyName=name+"_eu";
											Integer deStockQuantity=0;
											if(deStock!=null&&deStock.get("eu")!=null&&deStock.get("eu").get(name)!=null){
												deStockQuantity=deStock.get("eu").get(name);
											}
											String packageName=name;
											if(name.contains("_")){
												packageName=name.split("_")[0];
											}
											/*if(deStockQuantity<packageQuantity.get(packageName)){
												LOGGER.info(keyName+"德国仓库存小于一箱数");
												continue;
											}*/
											temp.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
											temp.append("<td>"+("0".equals(forecastType)?"销售预测":("1".equals(forecastType)?"周日销":("2".equals(forecastType)?"周日销(安)":"销售预测(安)")))+"</td>");
							                temp.append("<td>"+name+"</td>");
											
							                Integer safeInventory=0;
											if(euStockMap!=null&&euStockMap.get(name)!=null){
												safeInventory=MathUtils.roundUp(euStockMap.get(name).getSafeInventory());
											}
											temp.append("<td>"+safeInventory+"</td>");
											
											temp.append("<td>"+deStockQuantity+"</td>");
											Integer fbaInventory=0;
											if(fbaStock!=null&&fbaStock.get(keyName)!=null&&fbaStock.get(keyName).getFulfillableQuantity()!=null){
												fbaInventory=fbaStock.get(keyName).getFulfillableQuantity();
												temp.append("<td>"+fbaInventory+"</td>");
											}else{
												temp.append("<td></td>");
											}
											//Integer totalSal=0;
											String info="";
											for(String euCountry:countryList){
												/*if(sale30Map.get(euCountry)!=null&&sale30Map.get(euCountry).get(name)!=null){
													totalSal+=sale30Map.get(euCountry).get(name);
												}*/
												if(localMap!=null&&localMap.get(name+"_"+euCountry)!=null){
													info+=localMap.get(name+"_"+euCountry)+"<br/>";
												}
											}
											//Integer avgWeekQuantity=MathUtils.roundUp(totalSal/30d)*7;
											for(Map.Entry<String, PsiInventoryGap> typeEntry:typeMap.entrySet()){
												Integer num=MathUtils.roundUp(Math.abs(typeEntry.getValue().getGap())*1.0/packageQuantity.get(packageName));
												temp.append("<td>"+typeEntry.getValue().getDesc()+"</td>");
												String time="";
												if(fbaInventory==0){
													time=formatDay.format(new Date());
												}else{
													if(typeEntry.getValue().getWeek1()<0){
														if("0".equals(forecastType)||"3".equals(forecastType)){
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("0")!=null&&weekSales.get("0").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("0").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>7){
																		day=7;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}
															}
														}else{
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("1").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>7){
																		day=7;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}
																
															}
														}
													}
													if(typeEntry.getValue().getWeek1()>0&&typeEntry.getValue().getWeek2()<0){
														if("0".equals(forecastType)||"3".equals(forecastType)){
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("0")!=null&&weekSales.get("0").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("0").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>14){
																		day=14;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}else{
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("1").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>14){
																		day=14;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}
													}
													if(typeEntry.getValue().getWeek1()>0&&typeEntry.getValue().getWeek2()>0&&typeEntry.getValue().getWeek3()<0){
														if("0".equals(forecastType)||"3".equals(forecastType)){
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("0")!=null&&weekSales.get("0").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("0").get(name+"_"+country).getWeek1()/7d);
																	if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>21){
																		day=21;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}else{
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("1").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>21){
																		day=21;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}
																
															}
														}
													}
												}
												
												temp.append("<td>"+time+"</td>");
												//temp.append("<td>"+typeEntry.getValue().getTime()+"</td>");
												temp.append("<td>"+typeEntry.getValue().getGap()+"("+num+")</td>");
											}
											Integer avgWeekQuantity=0;
											if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
												avgWeekQuantity=weekSales.get("1").get(name+"_"+country).getWeek1();
											}
											
											temp.append("<td>"+avgWeekQuantity+"</td>");
											temp.append("<td>"+info+"</td>");
											if(workingFba!=null&&workingFba.get(country)!=null&&workingFba.get(country).get(name)!=null){
												temp.append("<td>"+workingFba.get(country).get(name)+"</td>");
											}else{
												temp.append("<td></td>");
											}
											temp.append("</tr>");
										}
									}
								}
							}
						}
						
						
						temp.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='11'><span style='font-weight: bold;font-size:25px'>de_FBA在途balance 低于安全库存详情</span></td></tr>");
						temp.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '><th>依据</th><th>产品</th><th>安全库存</th><th>德国仓库存</th><th>FBA库存</th><th>FBA在途</th><th>预计断货日</th><th>缺口量(箱数)</th><th>过去31天平均周销量</th><th>3周内在途预计到达数</th><th>建贴数</th></tr>");
						for (Map.Entry<String, Map<String, Map<String, PsiInventoryGap>>> countryEntry: countryGap.entrySet()) {
							String forecastType = countryEntry.getKey();
							Map<String,Map<String,PsiInventoryGap>> forecastMap=countryEntry.getValue();
							if(forecastMap!=null&&forecastMap.size()>0){
								for(Map.Entry<String,Map<String,PsiInventoryGap>> entry:forecastMap.entrySet()){
									String name = entry.getKey();
									if("1".equals(powerMap.get(name))){
										Map<String,PsiInventoryGap> typeMap=entry.getValue();
										if(typeMap!=null&&typeMap.size()>0){
											String keyName=name+"_"+country;
											Integer deStockQuantity=0;
											if(deStock!=null&&deStock.get("de")!=null&&deStock.get("de").get(name)!=null){
												deStockQuantity=deStock.get("de").get(name);
											}
											/*if(deStockQuantity<=10){
												LOGGER.info(keyName+"德国仓库存小于等于10");
												continue;
											}
										
											if(deStockQuantity<packageQuantity.get(packageName)){
												LOGGER.info(keyName+"德国仓库存小于一箱数");
												continue;
											}*/
											String packageName=name;
											if(name.contains("_")){
												packageName=name.split("_")[0];
											}
											temp.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
											temp.append("<td>"+("0".equals(forecastType)?"销售预测":("1".equals(forecastType)?"周日销":("2".equals(forecastType)?"周日销(安)":"销售预测(安)")))+"</td>");
							                temp.append("<td>"+name+"</td>");
							                
							                Integer safeInventory=0;
											if(stockMap!=null&&stockMap.get(name)!=null){
												safeInventory=MathUtils.roundUp(stockMap.get(name).getSafeInventory());
											}
											temp.append("<td>"+safeInventory+"</td>");
											
											temp.append("<td>"+deStockQuantity+"</td>");
											Integer fbaInventory=0;
											if(fbaStock!=null&&fbaStock.get(keyName)!=null&&fbaStock.get(keyName).getFulfillableQuantity()!=null){
												fbaInventory=fbaStock.get(keyName).getFulfillableQuantity();
												temp.append("<td>"+fbaInventory+"</td>");
											}else{
												temp.append("<td></td>");
											}
											
											for(Map.Entry<String, PsiInventoryGap> typeEntry:typeMap.entrySet()){
												Integer num=MathUtils.roundUp(Math.abs(typeEntry.getValue().getGap())*1.0/packageQuantity.get(packageName));
												temp.append("<td>"+typeEntry.getValue().getDesc()+"</td>");
												//temp.append("<td>"+typeEntry.getValue().getTime()+"</td>");
												String time="";
												if(fbaInventory==0){
													time=formatDay.format(new Date());
												}else{
													if(typeEntry.getValue().getWeek1()<0){
														if("0".equals(forecastType)||"3".equals(forecastType)){
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("0")!=null&&weekSales.get("0").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("0").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>7){
																		day=7;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}else{
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("1").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>7){
																		day=7;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}
													}
													if(typeEntry.getValue().getWeek1()>0&&typeEntry.getValue().getWeek2()<0){
														if("0".equals(forecastType)||"3".equals(forecastType)){
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("0")!=null&&weekSales.get("0").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("0").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>14){
																		day=14;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}else{
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("1").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>14){
																		day=14;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}
													}
													if(typeEntry.getValue().getWeek1()>0&&typeEntry.getValue().getWeek2()>0&&typeEntry.getValue().getWeek3()<0){
														if("0".equals(forecastType)||"3".equals(forecastType)){
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("0")!=null&&weekSales.get("0").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("0").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>21){
																		day=21;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}else{
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("1").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>21){
																		day=21;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}
													}
												}
												temp.append("<td>"+time+"</td>");
												temp.append("<td>"+typeEntry.getValue().getGap()+"("+num+")</td>");
												
											}
											Integer avgWeekQuantity=0;
											if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
												avgWeekQuantity=weekSales.get("1").get(name+"_"+country).getWeek1();
											}
											
											temp.append("<td>"+avgWeekQuantity+"</td>");
											String info="";
											if(localMap!=null&&localMap.get(name+"_"+country)!=null){
												info+=localMap.get(name+"_"+country)+"<br/>";
											}
											temp.append("<td>"+info+"</td>");
											if(workingFba!=null&&workingFba.get(country)!=null&&workingFba.get(country).get(name)!=null){
												temp.append("<td>"+workingFba.get(country).get(name)+"</td>");
											}else{
												temp.append("<td></td>");
											}
											temp.append("</tr>");
										}
									}
								}
							}
						}
					}
				}else{
					Map<String,Map<String,Map<String,PsiInventoryGap>>> countryGap=gapMap.get(country);
					if(countryGap!=null&&countryGap.size()>0){
						temp.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='11'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country)+"_FBA在途balance 低于安全库存详情</span></td></tr>");
						temp.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '><th>依据</th><th>产品</th><th>安全库存</th><th>德国仓库存</th><th>FBA库存</th><th>FBA在途</th><th>预计断货日</th><th>缺口量(箱数)</th><th>过去31天平均周销量</th><th>3周内在途预计到达数</th><th>建帖数</th></tr>");
						for (Map.Entry<String,Map<String,Map<String,PsiInventoryGap>>> entry: countryGap.entrySet()) {
							String forecastType = entry.getKey();
							Map<String,Map<String,PsiInventoryGap>> forecastMap=entry.getValue();
							if(forecastMap!=null&&forecastMap.size()>0){
								for(Map.Entry<String,Map<String,PsiInventoryGap>> foreEntry:forecastMap.entrySet()){
									 String name = foreEntry.getKey();
									Map<String,PsiInventoryGap> typeMap=foreEntry.getValue();
									if(typeMap!=null&&typeMap.size()>0){
										String keyName=name+"_"+country;
										Integer deStockQuantity=0;
										if(deStock!=null&&deStock.get(country)!=null&&deStock.get(country).get(name)!=null){
											deStockQuantity=deStock.get(country).get(name);
										}
										String packageName=name;
										if(name.contains("_")){
											packageName=name.split("_")[0];
										}
										/*if(deStockQuantity<packageQuantity.get(packageName)){
											LOGGER.info(keyName+"德国仓库存小于一箱数");
											continue;
										}*/
										
										temp.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
										temp.append("<td>"+("0".equals(forecastType)?"销售预测":("1".equals(forecastType)?"周日销":("2".equals(forecastType)?"周日销(安)":"销售预测(安)")))+"</td>");
						                temp.append("<td>"+name+"</td>");
						                Integer safeInventory=0;
										if(stockMap!=null&&stockMap.get(name)!=null){
											safeInventory=MathUtils.roundUp(stockMap.get(name).getSafeInventory());
										}
										temp.append("<td>"+safeInventory+"</td>");
										
										temp.append("<td>"+deStockQuantity+"</td>");
										Integer fbaInventory=0;
										if(fbaStock!=null&&fbaStock.get(keyName)!=null&&fbaStock.get(keyName).getFulfillableQuantity()!=null){
											fbaInventory=fbaStock.get(keyName).getFulfillableQuantity();
											temp.append("<td>"+fbaInventory+"</td>");
										}else{
											temp.append("<td></td>");
										}
										for(Map.Entry<String,PsiInventoryGap> typeEntry:typeMap.entrySet()){
											Integer num=MathUtils.roundUp(Math.abs(typeEntry.getValue().getGap())*1.0/packageQuantity.get(packageName));
											temp.append("<td>"+typeEntry.getValue().getDesc()+"</td>");
											//temp.append("<td>"+typeEntry.getValue().getTime()+"</td>");
											String time="";
											if(fbaInventory==0){
												time=formatDay.format(new Date());
											}else{
												if(typeEntry.getValue().getWeek1()<0){
													if("0".equals(forecastType)||"3".equals(forecastType)){
														Integer forecastWeek=0;
														if(weekSales!=null&&weekSales.get("0")!=null&&weekSales.get("0").get(name+"_"+country)!=null){
															forecastWeek=MathUtils.roundUp(weekSales.get("0").get(name+"_"+country).getWeek1()/7d);
															if(forecastWeek>0){
																Integer day=fbaInventory/forecastWeek;
																if(day>7){
																	day=7;
																}
																time=formatDay.format(DateUtils.addDays(new Date(),day));
															}	
														}
													}else{
														Integer forecastWeek=0;
														if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
															forecastWeek=MathUtils.roundUp(weekSales.get("1").get(name+"_"+country).getWeek1()/7d);
															if(forecastWeek>0){
																Integer day=fbaInventory/forecastWeek;
																if(day>7){
																	day=7;
																}
																time=formatDay.format(DateUtils.addDays(new Date(),day));
															}	
														}
													}
												}
												if(typeEntry.getValue().getWeek1()>0&&typeEntry.getValue().getWeek2()<0){
													if("0".equals(forecastType)||"3".equals(forecastType)){
														Integer forecastWeek=0;
														if(weekSales!=null&&weekSales.get("0")!=null&&weekSales.get("0").get(name+"_"+country)!=null){
															forecastWeek=MathUtils.roundUp(weekSales.get("0").get(name+"_"+country).getWeek1()/7d);
															if(forecastWeek>0){
																Integer day=fbaInventory/forecastWeek;
																if(day>14){
																	day=14;
																}
																time=formatDay.format(DateUtils.addDays(new Date(),day));
															}	
														}
													}else{
														Integer forecastWeek=0;
														if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
															forecastWeek=MathUtils.roundUp(weekSales.get("1").get(name+"_"+country).getWeek1()/7d);
															if(forecastWeek>0){
																Integer day=fbaInventory/forecastWeek;
																if(day>14){
																	day=14;
																}
																time=formatDay.format(DateUtils.addDays(new Date(),day));
															}	
														}
													}
												}
												if(typeEntry.getValue().getWeek1()>0&&typeEntry.getValue().getWeek2()>0&&typeEntry.getValue().getWeek3()<0){
													if("0".equals(forecastType)||"3".equals(forecastType)){
														Integer forecastWeek=0;
														if(weekSales!=null&&weekSales.get("0")!=null&&weekSales.get("0").get(name+"_"+country)!=null){
															forecastWeek=MathUtils.roundUp(weekSales.get("0").get(name+"_"+country).getWeek1()/7d);
															if(forecastWeek>0){
																Integer day=fbaInventory/forecastWeek;
																if(day>21){
																	day=21;
																}
																time=formatDay.format(DateUtils.addDays(new Date(),day));
															}	
														}
													}else{
														Integer forecastWeek=0;
														if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
															forecastWeek=MathUtils.roundUp(weekSales.get("1").get(name+"_"+country).getWeek1()/7d);
															if(forecastWeek>0){
																Integer day=fbaInventory/forecastWeek;
																if(day>21){
																	day=21;
																}
																time=formatDay.format(DateUtils.addDays(new Date(),day));
															}	
														}
													}
												}
											}
											temp.append("<td>"+time+"</td>");
											temp.append("<td>"+typeEntry.getValue().getGap()+"("+num+")</td>");
										}
										Integer avgWeekQuantity=0;
										if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
											avgWeekQuantity=weekSales.get("1").get(name+"_"+country).getWeek1();
										}
										
										temp.append("<td>"+avgWeekQuantity+"</td>");
										String info="";
										if(localMap!=null&&localMap.get(name+"_"+country)!=null){
											info+=localMap.get(name+"_"+country)+"<br/>";
										}
										temp.append("<td>"+info+"</td>");
										
										if(workingFba!=null&&workingFba.get(country)!=null&&workingFba.get(country).get(name)!=null){
											temp.append("<td>"+workingFba.get(country).get(name)+"</td>");
										}else{
											temp.append("<td></td>");
										}
										temp.append("</tr>");
									}
								}
							}
						}
					}
				}
			}
		}
		contents.append(temp);
		contents.append("</table>");
		if(StringUtils.isNotBlank(temp)){
				final MailInfo mailInfo = new MailInfo(toAddress,"德国本地仓发货提醒"+DateUtils.getDate("-yyyy/MM/dd"),date);
				mailInfo.setContent(contents.toString());
				mailInfo.setCcToAddress("ethan@inateck.com,ashley@inateck.com,eileen@inateck.com");
				new Thread(){
					public void run(){   
						sendCustomEmail1Manager.send(mailInfo);
						//new MailManager().send(mailInfo);
					}
				}.start();
		}
		LOGGER.info("德国本地仓发货提醒结束...");
	}
	
	
	
	public void sendGapEmail() {  
		LOGGER.info("开始查询欠料统计两周数据...");
		Map<String,Map<String,Map<String,Map<String,PsiInventoryGap>>>>  gapMap=psiOutOfStockInfoService.findAllCountryGapByTwoWeek();
		Map<String,PsiInventoryFba> fbaStock=psiInventoryService.getAllProductFbaInfo();//productName+"_"+country
		//Map<String,Map<String,Integer>>  sale30Map = psiInventoryFbaService.get31SalesMap(null);//country_name_quantity
		Map<String,String> powerMap=psiProductService.getHasPowerByName();
		Map<String,Map<String,Integer>> deStock=psiOutOfStockInfoService.getDeStock();
		Map<String,Map<String,Integer>> usStock=psiOutOfStockInfoService.getUsStock();
		Map<String,Map<String,PsiInventoryGap>> weekSales=psiOutOfStockInfoService.findByCountryName();
		
		List<String> countryList=Lists.newArrayList("de","fr","uk","it","es");
		
		Map<String,List<String>> localMap=psiOutOfStockInfoService.getLoclaTransDetail();
		DateFormat formatWeek = new SimpleDateFormat("yyyyww");
		DateFormat formatDay = new SimpleDateFormat("yyyy-MM-dd");
		Date date=new Date();
		if(date.getDay()==0){
			date = DateUtils.addDays(date, -1);
		}
		String key = formatWeek.format(date);
		int year =DateUtils.getSunday(date).getYear()+1900;
		int week =  Integer.parseInt(key.substring(4));
		if(week<10){
			key = year+"0"+week;
		}else{
			key =year+""+week;
		}
		StringBuffer contents= new StringBuffer("Hi all,今天是"+key+"周,以下是未来3周所有国家FBA在途balance小于0的型号。(预计断货日仅依据FBA库存计算)<br/>");
		StringBuffer temp= new StringBuffer("");
		contents.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
		String toAddress ="amazon-sales@inateck.com";
		//String toAddress ="eileen@inateck.com";
		if(gapMap!=null&&gapMap.size()>0){
			for (Map.Entry<String,Map<String,Map<String,Map<String,PsiInventoryGap>>>> entry: gapMap.entrySet()) {
				String country = entry.getKey();
				//Map<String,PsiProductInStock> stockMap=psiProductInStockService.getHistoryInventory(country);
				if("de".equals(country)){
					Map<String,Map<String,Map<String,PsiInventoryGap>>> countryGap=entry.getValue();
					if(countryGap!=null&&countryGap.size()>0){
						temp.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='9'><span style='font-weight: bold;font-size:25px'>eu_FBA在途balance小于0详情</span></td></tr>");
						temp.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '><th>依据</th><th>产品</th><th>海外仓库存</th><th>FBA库存</th><th>FBA在途</th><th>预计断货日</th><th>缺口量</th><th>过去31天平均周销量</th><th>3周内在途预计到达数</th></tr>");
						for (Map.Entry<String,Map<String,Map<String,PsiInventoryGap>>> countryEntry: countryGap.entrySet()) {
							String forecastType = countryEntry.getKey();
							Map<String,Map<String,PsiInventoryGap>> forecastMap=countryEntry.getValue();
							if(forecastMap!=null&&forecastMap.size()>0){
								for(Map.Entry<String,Map<String,PsiInventoryGap>> forecastEntry:forecastMap.entrySet()){
									String name = forecastEntry.getKey();
									if("0".equals(powerMap.get(name))){
										Map<String,PsiInventoryGap> typeMap=forecastEntry.getValue();
										if(typeMap!=null&&typeMap.size()>0){
											String keyName=name+"_eu";
											temp.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
											temp.append("<td>"+("0".equals(forecastType)?"销售预测":("1".equals(forecastType)?"周日销":("2".equals(forecastType)?"周日销(安)":"销售预测(安)")))+"</td>");
							                temp.append("<td>"+name+"</td>");
											
							                
											Integer deStockQuantity=0;
											if(deStock!=null&&deStock.get("eu")!=null&&deStock.get("eu").get(name)!=null){
												deStockQuantity=deStock.get("eu").get(name);
											}
											temp.append("<td>"+deStockQuantity+"</td>");
											Integer fbaInventory=0;
											if(fbaStock!=null&&fbaStock.get(keyName)!=null&&fbaStock.get(keyName).getFulfillableQuantity()!=null){
												fbaInventory=fbaStock.get(keyName).getFulfillableQuantity();
												temp.append("<td>"+fbaInventory+"</td>");
											}else{
												temp.append("<td></td>");
											}
											String info="";
											for(String euCountry:countryList){
												//if(sale30Map.get(euCountry)!=null&&sale30Map.get(euCountry).get(name)!=null){
												//	totalSal+=sale30Map.get(euCountry).get(name);
											//	}
												if(localMap!=null&&localMap.get(name+"_"+euCountry)!=null){
													info+=localMap.get(name+"_"+euCountry)+"<br/>";
												}
											}
											Integer avgWeekQuantity=0;
											if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
												avgWeekQuantity=weekSales.get("1").get(name+"_"+country).getWeek1();
											}
											
											
											for(Map.Entry<String,PsiInventoryGap> typeEntry:typeMap.entrySet()){
												temp.append("<td>"+typeEntry.getValue().getDesc()+"</td>");
												String time="";
												if(fbaInventory==0){
													time=formatDay.format(new Date());
												}else{
													if(typeEntry.getValue().getWeek1()<0){
														if("0".equals(forecastType)||"3".equals(forecastType)){
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("0")!=null&&weekSales.get("0").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("0").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>7){
																		day=7;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}else{
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("1").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>7){
																		day=7;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}
													}
													if(typeEntry.getValue().getWeek1()>0&&typeEntry.getValue().getWeek2()<0){
														if("0".equals(forecastType)||"3".equals(forecastType)){
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("0")!=null&&weekSales.get("0").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("0").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>14){
																		day=14;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}else{
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("1").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>14){
																		day=14;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}
													}
													if(typeEntry.getValue().getWeek1()>0&&typeEntry.getValue().getWeek2()>0&&typeEntry.getValue().getWeek3()<0){
														if("0".equals(forecastType)||"3".equals(forecastType)){
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("0")!=null&&weekSales.get("0").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("0").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>21){
																		day=21;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}else{
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("1").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>21){
																		day=21;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}
													}
												}
												
												temp.append("<td>"+time+"</td>");
												temp.append("<td>"+typeEntry.getValue().getGap()+"</td>");
											}
											temp.append("<td>"+avgWeekQuantity+"</td>");
											temp.append("<td>"+info+"</td>");
											temp.append("</tr>");
										}
									}
								}
							}
						}
						
						
						temp.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='9'><span style='font-weight: bold;font-size:25px'>de_FBA在途balance小于0详情</span></td></tr>");
						temp.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '><th>依据</th><th>产品</th><th>海外仓库存</th><th>FBA库存</th><th>FBA在途</th><th>预计断货日</th><th>缺口量</th><th>过去31天平均周销量</th><th>3周内在途预计到达数</th></tr>");
						for (Map.Entry<String,Map<String,Map<String,PsiInventoryGap>>> couEntry: countryGap.entrySet()) {
							String forecastType=couEntry.getKey();
							Map<String,Map<String,PsiInventoryGap>> forecastMap=countryGap.get(forecastType);
							if(forecastMap!=null&&forecastMap.size()>0){
								for(Map.Entry<String,Map<String,PsiInventoryGap>> foreEntry:forecastMap.entrySet()){
									String name=foreEntry.getKey();
									if("1".equals(powerMap.get(name))){
										Map<String,PsiInventoryGap> typeMap=foreEntry.getValue();
										if(typeMap!=null&&typeMap.size()>0){
											String keyName=name+"_"+country;
											temp.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
											temp.append("<td>"+("0".equals(forecastType)?"销售预测":("1".equals(forecastType)?"周日销":("2".equals(forecastType)?"周日销(安)":"销售预测(安)")))+"</td>");
							                temp.append("<td>"+name+"</td>");
							                
											Integer deStockQuantity=0;
											if(deStock!=null&&deStock.get("de")!=null&&deStock.get("de").get(name)!=null){
												deStockQuantity=deStock.get("de").get(name);
											}
											temp.append("<td>"+deStockQuantity+"</td>");
											Integer fbaInventory=0;
											if(fbaStock!=null&&fbaStock.get(keyName)!=null&&fbaStock.get(keyName).getFulfillableQuantity()!=null){
												fbaInventory=fbaStock.get(keyName).getFulfillableQuantity();
												temp.append("<td>"+fbaInventory+"</td>");
											}else{
												temp.append("<td></td>");
											}
											
											for(Map.Entry<String,PsiInventoryGap> typeEntry:typeMap.entrySet()){
												temp.append("<td>"+typeEntry.getValue().getDesc()+"</td>");
												String time="";
												if(fbaInventory==0){
													time=formatDay.format(new Date());
												}else{
													if(typeEntry.getValue().getWeek1()<0){
														if("0".equals(forecastType)||"3".equals(forecastType)){
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("0")!=null&&weekSales.get("0").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("0").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>7){
																		day=7;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}else{
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("1").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>7){
																		day=7;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}
													}
													if(typeEntry.getValue().getWeek1()>0&&typeEntry.getValue().getWeek2()<0){
														if("0".equals(forecastType)||"3".equals(forecastType)){
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("0")!=null&&weekSales.get("0").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("0").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>14){
																		day=14;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}else{
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("1").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>14){
																		day=14;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}
													}
													if(typeEntry.getValue().getWeek1()>0&&typeEntry.getValue().getWeek2()>0&&typeEntry.getValue().getWeek3()<0){
														if("0".equals(forecastType)||"3".equals(forecastType)){
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("0")!=null&&weekSales.get("0").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("0").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>21){
																		day=21;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}else{
															Integer forecastWeek=0;
															if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
																forecastWeek=MathUtils.roundUp(weekSales.get("1").get(name+"_"+country).getWeek1()/7d);
																if(forecastWeek>0){
																	Integer day=fbaInventory/forecastWeek;
																	if(day>21){
																		day=21;
																	}
																	time=formatDay.format(DateUtils.addDays(new Date(),day));
																}	
															}
														}
													}
												}
												temp.append("<td>"+time+"</td>");
												temp.append("<td>"+typeEntry.getValue().getGap()+"</td>");
											}
											
											Integer avgWeekQuantity=0;
											if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
												avgWeekQuantity=weekSales.get("1").get(name+"_"+country).getWeek1();
											}
											temp.append("<td>"+avgWeekQuantity+"</td>");
											String info="";
											if(localMap!=null&&localMap.get(name+"_"+country)!=null){
												info+=localMap.get(name+"_"+country)+"<br/>";
											}
											temp.append("<td>"+info+"</td>");
											temp.append("</tr>");
										}
									}
								}
							}
						}
					}
				}else{
					Map<String,Map<String,Map<String,PsiInventoryGap>>> countryGap=gapMap.get(country);
					if(countryGap!=null&&countryGap.size()>0){
						temp.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32;'><td colspan='9'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country)+"_FBA在途balance小于0详情</span></td></tr>");
						temp.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '><th>依据</th><th>产品</th><th>海外仓库存</th><th>FBA库存</th><th>FBA在途</th><th>预计断货日</th><th>缺口量</th><th>过去31天平均周销量</th><th>3周内在途预计到达数</th></tr>");
						for (Map.Entry<String,Map<String,Map<String,PsiInventoryGap>>> gapEntry: countryGap.entrySet()) {
							String forecastType = gapEntry.getKey();
							Map<String,Map<String,PsiInventoryGap>> forecastMap=countryGap.get(forecastType);
							if(forecastMap!=null&&forecastMap.size()>0){
								for(Map.Entry<String,Map<String,PsiInventoryGap>> foreEntry:forecastMap.entrySet()){
									String name = foreEntry.getKey();
									Map<String,PsiInventoryGap> typeMap=forecastMap.get(name);
									if(typeMap!=null&&typeMap.size()>0){
										String keyName=name+"_"+country;
										temp.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
										temp.append("<td>"+("0".equals(forecastType)?"销售预测":("1".equals(forecastType)?"周日销":("2".equals(forecastType)?"周日销(安)":"销售预测(安)")))+"</td>");
						                temp.append("<td>"+name+"</td>");
						               
										Integer usStockQuantity=0;
										if(usStock!=null&&usStock.get(country)!=null&&usStock.get(country).get(name)!=null){
											usStockQuantity=usStock.get(country).get(name);
										}
										temp.append("<td>"+usStockQuantity+"</td>");
										Integer fbaInventory=0;
										if(fbaStock!=null&&fbaStock.get(keyName)!=null&&fbaStock.get(keyName).getFulfillableQuantity()!=null){
											fbaInventory=fbaStock.get(keyName).getFulfillableQuantity();
											temp.append("<td>"+fbaInventory+"</td>");
										}else{
											temp.append("<td></td>");
										}
										
										for(Map.Entry<String,PsiInventoryGap> typeEntry:typeMap.entrySet()){
											temp.append("<td>"+typeEntry.getValue().getDesc()+"</td>");
											String time="";
											if(fbaInventory==0){
												time=formatDay.format(new Date());
											}else{
												if(typeEntry.getValue().getWeek1()<0){
													if("0".equals(forecastType)||"3".equals(forecastType)){
														Integer forecastWeek=0;
														if(weekSales!=null&&weekSales.get("0")!=null&&weekSales.get("0").get(name+"_"+country)!=null){
															forecastWeek=MathUtils.roundUp(weekSales.get("0").get(name+"_"+country).getWeek1()/7d);
															if(forecastWeek>0){
																Integer day=fbaInventory/forecastWeek;
																if(day>7){
																	day=7;
																}
																time=formatDay.format(DateUtils.addDays(new Date(),day));
															}	
														}
													}else{
														Integer forecastWeek=0;
														if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
															forecastWeek=MathUtils.roundUp(weekSales.get("1").get(name+"_"+country).getWeek1()/7d);
															if(forecastWeek>0){
																Integer day=fbaInventory/forecastWeek;
																if(day>7){
																	day=7;
																}
																time=formatDay.format(DateUtils.addDays(new Date(),day));
															}	
														}
													}
												}
												if(typeEntry.getValue().getWeek1()>0&&typeEntry.getValue().getWeek2()<0){
													if("0".equals(forecastType)||"3".equals(forecastType)){
														Integer forecastWeek=0;
														if(weekSales!=null&&weekSales.get("0")!=null&&weekSales.get("0").get(name+"_"+country)!=null){
															forecastWeek=MathUtils.roundUp(weekSales.get("0").get(name+"_"+country).getWeek1()/7d);
															if(forecastWeek>0){
																Integer day=fbaInventory/forecastWeek;
																if(day>14){
																	day=14;
																}
																time=formatDay.format(DateUtils.addDays(new Date(),day));
															}	
														}
													}else{
														Integer forecastWeek=0;
														if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
															forecastWeek=MathUtils.roundUp(weekSales.get("1").get(name+"_"+country).getWeek1()/7d);
															if(forecastWeek>0){
																Integer day=fbaInventory/forecastWeek;
																if(day>14){
																	day=14;
																}
																time=formatDay.format(DateUtils.addDays(new Date(),day));
															}	
														}
													}
												}
												if(typeEntry.getValue().getWeek1()>0&&typeEntry.getValue().getWeek2()>0&&typeEntry.getValue().getWeek3()<0){
													if("0".equals(forecastType)||"3".equals(forecastType)){
														Integer forecastWeek=0;
														if(weekSales!=null&&weekSales.get("0")!=null&&weekSales.get("0").get(name+"_"+country)!=null){
															forecastWeek=MathUtils.roundUp(weekSales.get("0").get(name+"_"+country).getWeek1()/7d);
															if(forecastWeek>0){
																Integer day=fbaInventory/forecastWeek;
																if(day>21){
																	day=21;
																}
																time=formatDay.format(DateUtils.addDays(new Date(),day));
															}	
														}
													}else{
														Integer forecastWeek=0;
														if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
															forecastWeek=MathUtils.roundUp(weekSales.get("1").get(name+"_"+country).getWeek1()/7d);
															if(forecastWeek>0){
																Integer day=fbaInventory/forecastWeek;
																if(day>21){
																	day=21;
																}
																time=formatDay.format(DateUtils.addDays(new Date(),day));
															}	
														}
													}
												}
											}
											temp.append("<td>"+time+"</td>");
											temp.append("<td>"+typeEntry.getValue().getGap()+"</td>");
										}
										Integer avgWeekQuantity=0;
										if(weekSales!=null&&weekSales.get("1")!=null&&weekSales.get("1").get(name+"_"+country)!=null){
											avgWeekQuantity=weekSales.get("1").get(name+"_"+country).getWeek1();
										}
										temp.append("<td>"+avgWeekQuantity+"</td>");
										String info="";
										if(localMap!=null&&localMap.get(name+"_"+country)!=null){
											info+=localMap.get(name+"_"+country)+"<br/>";
										}
										temp.append("<td>"+info+"</td>");
										temp.append("</tr>");
									}
								}
							}
						}
					}
				}
			}
		}
		contents.append(temp);
		contents.append("</table>");
		if(StringUtils.isNotBlank(temp)){
				final MailInfo mailInfo = new MailInfo(toAddress,"亚马逊库存不足3周，请确认是否提价"+DateUtils.getDate("-yyyy/MM/dd"),date);
				mailInfo.setContent(contents.toString());
				mailInfo.setCcToAddress("eileen@inateck.com");
				new Thread(){
					public void run(){   
						sendCustomEmail1Manager.send(mailInfo);
						// new MailManager().send(mailInfo);
					}
				}.start();
		}
		LOGGER.info("查询欠料统计两周数据结束...");
		
		try{
			 LOGGER.info("ean个数不足20个开始...");
			 if(amazonPostsDetailService.countEanActive()){
				 WeixinSendMsgUtil.sendTextMsgToUser("ethan|eileen","ean个数不足20个,请尽快上传新ean文件");
			 }
			 LOGGER.info("ean个数不足20个开始结束...");
		}catch(Exception e){
			 LOGGER.error("ean个数不足20个开始异常！",e);
		}
		
	}
	
	 // 国家- 产品名称- 运输方式
	public void initPsiTransportRateAndPrice() {
		
		try{
			 LOGGER.info("计算运费平均价格开始...");
			 psiTransportPaymentService.updateIsCount();
			 saveLocalAvgPrice();
			 saveAvgPrice();
			 LOGGER.info("计算运费平均价格结束...");
		}catch(Exception e){
			 LOGGER.error("计算运费平均价格异常！",e);
		}
		
		 
		 
	   LOGGER.info("分产品空海运比例和价格...");
	   Map<String,Map<String,Map<String,PsiTransportDto>>> map=psiTransportPaymentService.findPsiTransportRateAndPrice();
	   Map<String,Map<String,Map<String,PsiTransportDto>>> map2=psiTransportPaymentService.findPsiTransportRateAndPrice2();
	 //  Map<String,Map<String,PsiProductTransportRate>> existMap=psiTransportPaymentService.findTransportRateAndPrice();//国家- 产品名称-
	   Map<String,Map<String,String>> existMap=psiTransportPaymentService.getExistId();
	   List<PsiProductTransportRate> saveList=Lists.newArrayList();
	   List<PsiProductTransportRate> saveList2=Lists.newArrayList();
	   if(map!=null&&map.size()>0){
		   for (Map.Entry<String,Map<String,Map<String,PsiTransportDto>>> entry: map.entrySet()) {
			   String country = entry.getKey();
			   Map<String,Map<String,PsiTransportDto>> countryMap=entry.getValue();
			   if(countryMap!=null&&countryMap.size()>0){
				   for(Map.Entry<String,Map<String,PsiTransportDto>> entry1:countryMap.entrySet()){
					   String name=entry1.getKey();
					   Map<String,PsiTransportDto> nameMap=entry1.getValue();
					   if(nameMap!=null&&nameMap.size()>0){
						   if(existMap!=null&&existMap.get(name)!=null&&existMap.get(name).get(country)!=null){
							    String existId=existMap.get(name).get(country);
							    if(existId.contains(",")){
							    	String[] rateId=existId.split(",");
							    	for (String idArr : rateId) {
							    		    PsiProductTransportRate rate=psiTransportPaymentService.getById(Integer.parseInt(idArr));
							    		    rate.setUpdateDate(new Date());
										    PsiTransportDto trans=nameMap.get("total");
										    PsiTransportDto airTrans=nameMap.get("0");
											if(airTrans!=null&&airTrans.getWeight()!=null&&airTrans.getWeight()!=0){
												rate.setAirRate(airTrans.getWeight()/trans.getWeight());
											}else{
												rate.setAirRate(null);
											}
											if(airTrans!=null&&airTrans.getMoney()!=null&&airTrans.getQuantity()!=null&&airTrans.getQuantity()!=0){
												rate.setAirPrice(airTrans.getMoney()/airTrans.getQuantity());
											}else{
												rate.setAirPrice(null);
											}
											
											PsiTransportDto seaTrans=nameMap.get("1");
											if(seaTrans!=null&&seaTrans.getWeight()!=null&&seaTrans.getWeight()!=0){
												rate.setSeaRate(seaTrans.getWeight()/trans.getWeight());
											}else{
												rate.setSeaRate(null);
											}
											if(seaTrans!=null&&seaTrans.getMoney()!=null&&seaTrans.getQuantity()!=null&&seaTrans.getQuantity()!=0){
												rate.setSeaPrice(seaTrans.getMoney()/seaTrans.getQuantity());
											}else{
												rate.setSeaPrice(null);
											}
											
											PsiTransportDto expressTrans=nameMap.get("2");
											if(expressTrans!=null&&expressTrans.getWeight()!=null&&expressTrans.getWeight()!=0){
												rate.setExpressRate(expressTrans.getWeight()/trans.getWeight());
											}else{
												rate.setExpressRate(null);
											}
											if(expressTrans!=null&&expressTrans.getMoney()!=null&&expressTrans.getQuantity()!=null&&expressTrans.getQuantity()!=0){
												rate.setExpressPrice(expressTrans.getMoney()/expressTrans.getQuantity());
											}else{
												rate.setExpressPrice(null);
											}
											saveList.add(rate);
									}
							    }else{
							    	PsiProductTransportRate rate=psiTransportPaymentService.getById(Integer.parseInt(existId));
					    		    rate.setUpdateDate(new Date());
								    PsiTransportDto trans=nameMap.get("total");
								    PsiTransportDto airTrans=nameMap.get("0");
									if(airTrans!=null&&airTrans.getWeight()!=null&&airTrans.getWeight()!=0){
										rate.setAirRate(airTrans.getWeight()/trans.getWeight());
									}else{
										rate.setAirRate(null);
									}
									if(airTrans!=null&&airTrans.getMoney()!=null&&airTrans.getQuantity()!=null&&airTrans.getQuantity()!=0){
										rate.setAirPrice(airTrans.getMoney()/airTrans.getQuantity());
									}else{
										rate.setAirPrice(null);
									}
									
									PsiTransportDto seaTrans=nameMap.get("1");
									if(seaTrans!=null&&seaTrans.getWeight()!=null&&seaTrans.getWeight()!=0){
										rate.setSeaRate(seaTrans.getWeight()/trans.getWeight());
									}else{
										rate.setSeaRate(null);
									}
									if(seaTrans!=null&&seaTrans.getMoney()!=null&&seaTrans.getQuantity()!=null&&seaTrans.getQuantity()!=0){
										rate.setSeaPrice(seaTrans.getMoney()/seaTrans.getQuantity());
									}else{
										rate.setSeaPrice(null);
									}
									
									PsiTransportDto expressTrans=nameMap.get("2");
									if(expressTrans!=null&&expressTrans.getWeight()!=null&&expressTrans.getWeight()!=0){
										rate.setExpressRate(expressTrans.getWeight()/trans.getWeight());
									}else{
										rate.setExpressRate(null);
									}
									if(expressTrans!=null&&expressTrans.getMoney()!=null&&expressTrans.getQuantity()!=null&&expressTrans.getQuantity()!=0){
										rate.setExpressPrice(expressTrans.getMoney()/expressTrans.getQuantity());
									}else{
										rate.setExpressPrice(null);
									}
									saveList.add(rate);
							    }
							   
							    
							   
						   }else{
							   PsiProductTransportRate rate=new PsiProductTransportRate();
							   rate.setCountry(country);
							   rate.setProductName(name);
							   rate.setUpdateDate(new Date());
							   PsiTransportDto trans=nameMap.get("total");
							    PsiTransportDto airTrans=nameMap.get("0");
							    if(airTrans!=null&&airTrans.getWeight()!=null&&airTrans.getWeight()!=0){
									rate.setAirRate(airTrans.getWeight()/trans.getWeight());
								}
							    if(airTrans!=null&&airTrans.getMoney()!=null&&airTrans.getQuantity()!=null&&airTrans.getQuantity()!=0){
									rate.setAirPrice(airTrans.getMoney()/airTrans.getQuantity());
								}
								PsiTransportDto seaTrans=nameMap.get("1");
								if(seaTrans!=null&&seaTrans.getWeight()!=null&&seaTrans.getWeight()!=0){
									rate.setSeaRate(seaTrans.getWeight()/trans.getWeight());
								}
								if(seaTrans!=null&&seaTrans.getMoney()!=null&&seaTrans.getQuantity()!=null&&seaTrans.getQuantity()!=0){
									rate.setSeaPrice(seaTrans.getMoney()/seaTrans.getQuantity());
								}
								PsiTransportDto expressTrans=nameMap.get("2");
								if(expressTrans!=null&&expressTrans.getWeight()!=null&&expressTrans.getWeight()!=0){
									rate.setExpressRate(expressTrans.getWeight()/trans.getWeight());
								}
								if(expressTrans!=null&&expressTrans.getMoney()!=null&&expressTrans.getQuantity()!=null&&expressTrans.getQuantity()!=0){
									rate.setExpressPrice(expressTrans.getMoney()/expressTrans.getQuantity());
								}
							   saveList.add(rate);
						   }
					   }
				   }
			   }
		   }
		   
		   if(saveList!=null&&saveList.size()>0){
			   psiTransportPaymentService.saveRateList(saveList);
		   }
	   }
	 
	   existMap=psiTransportPaymentService.getExistId();
	   if(map2!=null&&map2.size()>0){
		   for (Map.Entry<String,Map<String,Map<String,PsiTransportDto>>> entry: map2.entrySet()) {
			   String country = entry.getKey();
			   Map<String,Map<String,PsiTransportDto>> countryMap=entry.getValue();
			   if(countryMap!=null&&countryMap.size()>0){
				   for(Map.Entry<String,Map<String,PsiTransportDto>> dtoEntry :countryMap.entrySet()){
					   String name=dtoEntry.getKey();
					   Map<String,PsiTransportDto> nameMap=countryMap.get(name);
					   if(nameMap!=null&&nameMap.size()>0){
						   if(existMap!=null&&existMap.get(name)!=null&&existMap.get(name).get(country)!=null){
							    String existId=existMap.get(name).get(country);
							    if(existId.contains(",")){
							    	String[] rateId=existId.split(",");
							    	for (String idArr : rateId) {
							    		    PsiProductTransportRate rate=psiTransportPaymentService.getById(Integer.parseInt(idArr));
							    		    rate.setUpdateDate(new Date());
										    PsiTransportDto trans=nameMap.get("total");
										    PsiTransportDto airTrans=nameMap.get("0");
											if(airTrans!=null&&airTrans.getWeight()!=null&&airTrans.getWeight()!=0){
												rate.setAirRate(airTrans.getWeight()/trans.getWeight());
											}else{
												rate.setAirRate(null);
											}
											if(airTrans!=null&&airTrans.getMoney()!=null&&airTrans.getQuantity()!=null&&airTrans.getQuantity()!=0){
												rate.setAirPrice(airTrans.getMoney()/airTrans.getQuantity());
											}else{
												rate.setAirPrice(null);
											}
											
											PsiTransportDto seaTrans=nameMap.get("1");
											if(seaTrans!=null&&seaTrans.getWeight()!=null&&seaTrans.getWeight()!=0){
												rate.setSeaRate(seaTrans.getWeight()/trans.getWeight());
											}else{
												rate.setSeaRate(null);
											}
											if(seaTrans!=null&&seaTrans.getMoney()!=null&&seaTrans.getQuantity()!=null&&seaTrans.getQuantity()!=0){
												rate.setSeaPrice(seaTrans.getMoney()/seaTrans.getQuantity());
											}else{
												rate.setSeaPrice(null);
											}
											
											PsiTransportDto expressTrans=nameMap.get("2");
											if(expressTrans!=null&&expressTrans.getWeight()!=null&&expressTrans.getWeight()!=0){
												rate.setExpressRate(expressTrans.getWeight()/trans.getWeight());
											}else{
												rate.setExpressRate(null);
											}
											if(expressTrans!=null&&expressTrans.getMoney()!=null&&expressTrans.getQuantity()!=null&&expressTrans.getQuantity()!=0){
												rate.setExpressPrice(expressTrans.getMoney()/expressTrans.getQuantity());
											}else{
												rate.setExpressPrice(null);
											}
											saveList2.add(rate);
									}
							    }else{
							    	PsiProductTransportRate rate=psiTransportPaymentService.getById(Integer.parseInt(existId));
					    		    rate.setUpdateDate(new Date());
								    PsiTransportDto trans=nameMap.get("total");
								    PsiTransportDto airTrans=nameMap.get("0");
									if(airTrans!=null&&airTrans.getWeight()!=null&&airTrans.getWeight()!=0){
										rate.setAirRate(airTrans.getWeight()/trans.getWeight());
									}else{
										rate.setAirRate(null);
									}
									if(airTrans!=null&&airTrans.getMoney()!=null&&airTrans.getQuantity()!=null&&airTrans.getQuantity()!=0){
										rate.setAirPrice(airTrans.getMoney()/airTrans.getQuantity());
									}else{
										rate.setAirPrice(null);
									}
									
									PsiTransportDto seaTrans=nameMap.get("1");
									if(seaTrans!=null&&seaTrans.getWeight()!=null&&seaTrans.getWeight()!=0){
										rate.setSeaRate(seaTrans.getWeight()/trans.getWeight());
									}else{
										rate.setSeaRate(null);
									}
									if(seaTrans!=null&&seaTrans.getMoney()!=null&&seaTrans.getQuantity()!=null&&seaTrans.getQuantity()!=0){
										rate.setSeaPrice(seaTrans.getMoney()/seaTrans.getQuantity());
									}else{
										rate.setSeaPrice(null);
									}
									
									PsiTransportDto expressTrans=nameMap.get("2");
									if(expressTrans!=null&&expressTrans.getWeight()!=null&&expressTrans.getWeight()!=0){
										rate.setExpressRate(expressTrans.getWeight()/trans.getWeight());
									}else{
										rate.setExpressRate(null);
									}
									if(expressTrans!=null&&expressTrans.getMoney()!=null&&expressTrans.getQuantity()!=null&&expressTrans.getQuantity()!=0){
										rate.setExpressPrice(expressTrans.getMoney()/expressTrans.getQuantity());
									}else{
										rate.setExpressPrice(null);
									}
									saveList2.add(rate);
							    }
							   
							    
							   
						   }else{
							   PsiProductTransportRate rate=new PsiProductTransportRate();
							   rate.setCountry(country);
							   rate.setProductName(name);
							   rate.setUpdateDate(new Date());
							   PsiTransportDto trans=nameMap.get("total");
							    PsiTransportDto airTrans=nameMap.get("0");
							    if(airTrans!=null&&airTrans.getWeight()!=null&&airTrans.getWeight()!=0){
									rate.setAirRate(airTrans.getWeight()/trans.getWeight());
								}
							    if(airTrans!=null&&airTrans.getMoney()!=null&&airTrans.getQuantity()!=null&&airTrans.getQuantity()!=0){
									rate.setAirPrice(airTrans.getMoney()/airTrans.getQuantity());
								}
								PsiTransportDto seaTrans=nameMap.get("1");
								if(seaTrans!=null&&seaTrans.getWeight()!=null&&seaTrans.getWeight()!=0){
									rate.setSeaRate(seaTrans.getWeight()/trans.getWeight());
								}
								if(seaTrans!=null&&seaTrans.getMoney()!=null&&seaTrans.getQuantity()!=null&&seaTrans.getQuantity()!=0){
									rate.setSeaPrice(seaTrans.getMoney()/seaTrans.getQuantity());
								}
								PsiTransportDto expressTrans=nameMap.get("2");
								if(expressTrans!=null&&expressTrans.getWeight()!=null&&expressTrans.getWeight()!=0){
									rate.setExpressRate(expressTrans.getWeight()/trans.getWeight());
								}
								if(expressTrans!=null&&expressTrans.getMoney()!=null&&expressTrans.getQuantity()!=null&&expressTrans.getQuantity()!=0){
									rate.setExpressPrice(expressTrans.getMoney()/expressTrans.getQuantity());
								}
							   saveList2.add(rate);
						   }
					   }
				   }
			   }
		   }
		   
		   if(saveList2!=null&&saveList2.size()>0){
			   psiTransportPaymentService.saveRateList(saveList2);
		   }
	   }
	}
	
	public void initAvgPrice(){
		 Map<String,Map<String,PsiTransportDto>>  map=psiTransportPaymentService.initPsiTransportAvgPrice();
		 List<PsiProductAvgPrice>  priceList=Lists.newArrayList();
		 Date date=new Date();
		 for(Map.Entry<String,Map<String,PsiTransportDto>> entry1:map.entrySet()){
			 String country = entry1.getKey();
			 Map<String,PsiTransportDto> temp= map.get(country);
			 for (Map.Entry<String,PsiTransportDto> entry :temp.entrySet()) {
				 String name = entry.getKey();
				 PsiTransportDto dto=entry.getValue();
				 priceList.add(new PsiProductAvgPrice(name,country,dto.getMoney()/dto.getQuantity(),date));
			 }
		 }
		 if(priceList!=null&&priceList.size()>0){
			 psiTransportPaymentService.saveAvgPriceList(priceList);
		 }
	}
	
	
	public void saveLocalAvgPrice(){
		 Map<String,Map<String,PsiTransportDto>>  map=psiTransportPaymentService.getYesterdayInStock("0");//country-name  sr
		 Map<String,Map<String,PsiTransportDto>>  map2=psiTransportPaymentService.getYesterdayInStock2("0");//country-name  lc
		 
		 Map<String,Map<String,Float>>  priceMap=psiTransportPaymentService.findTransportLocalAvgPrice();
		 Map<String,Map<String,Integer>> localStock=psiInventoryService.getLocalInventory();
		 
		 Date date=new Date();
		 List<PsiProductLocalAvgPrice>  avgPriceList=Lists.newArrayList();
		 List<PsiProductLocalAvgPrice>  avgPriceList2=Lists.newArrayList();
		 if(map!=null&&map.size()>0){
			 for(Map.Entry<String,Map<String,PsiTransportDto>> entry :map.entrySet()){
				 String country = entry.getKey();
				 Map<String,PsiTransportDto> temp=entry.getValue();
				 for (Map.Entry<String,PsiTransportDto> entry1: temp.entrySet()) {
					 String name =entry1.getKey();
					 PsiTransportDto dto=temp.get(name);
					if(priceMap==null||priceMap.get(country)==null||priceMap.get(country).get(name)==null||priceMap.get(country).get(name)==0){
						 avgPriceList.add(new PsiProductLocalAvgPrice(name,country,dto.getMoney()/dto.getQuantity(),date));
					}else{//存在
						if("EU".equals(country)){//19
							Integer totalStock=0;
							
							if(localStock.get(19)!=null&&localStock.get(19).get(name)!=null){
								totalStock+=localStock.get(19).get(name);
							}
							Float latestPrice=priceMap.get(country).get(name);
							Float price=0f;
							if(totalStock==0||(totalStock-dto.getQuantity()<=0)){
								LOGGER.info(totalStock+"-"+country+"-"+name+" stock less");
								price=dto.getMoney()/dto.getQuantity();
							}else{
								price=(latestPrice*(totalStock-dto.getQuantity())+dto.getMoney())/totalStock;
							}
							if(price>0){
								avgPriceList.add(new PsiProductLocalAvgPrice(name,country,price,date,totalStock));
							}
						}else if("US".equals(country)){//120
							Integer totalStock=0;
							
							if(localStock.get(19)!=null&&localStock.get(19).get(name)!=null){
								totalStock+=localStock.get(19).get(name);
							}
							Float latestPrice=priceMap.get(country).get(name);
							Float price=0f;
							if(totalStock==0||(totalStock-dto.getQuantity()<=0)){
								LOGGER.info(totalStock+"-"+country+"-"+name+" stock less");
								price=dto.getMoney()/dto.getQuantity();
							}else{
								price=(latestPrice*(totalStock-dto.getQuantity())+dto.getMoney())/totalStock;
							}
							if(price>0){
								avgPriceList.add(new PsiProductLocalAvgPrice(name,country,price,date,totalStock));
							}
						}else{
							Integer totalStock=0;
							if(localStock.get(147)!=null&&localStock.get(147).get(name)!=null){
								totalStock+=localStock.get(147).get(name);
							}
							Float latestPrice=priceMap.get(country).get(name);
							Float price=0f;
							if(totalStock==0||(totalStock-dto.getQuantity()<=0)){
								LOGGER.info(totalStock+"-"+country+"-"+name+" stock less");
								price=dto.getMoney()/dto.getQuantity();
							}else{
								price=(latestPrice*(totalStock-dto.getQuantity())+dto.getMoney())/totalStock;
							}
							if(price>0){
								avgPriceList.add(new PsiProductLocalAvgPrice(name,country,price,date,totalStock));
							}
						}
					}
				}
			 }
		 }

		 if(avgPriceList!=null&&avgPriceList.size()>0){
			 psiTransportPaymentService.saveLocalAvgPriceList(avgPriceList);
			 try{
				 List<Integer> idList=psiTransportPaymentService.findIsCount("0");
				 psiTransportPaymentService.updateIsCount(idList);
			 }catch(Exception e){
				 LOGGER.error("1更新查询标识异常！",e.getMessage());
			 }
		 }
		 
		 priceMap=psiTransportPaymentService.findTransportLocalAvgPrice();
		 if(map2!=null&&map2.size()>0){
			 for(Map.Entry<String,Map<String,PsiTransportDto>> entry :map2.entrySet()){
				 String country = entry.getKey();
				 Map<String,PsiTransportDto> temp=entry.getValue();
				 for (Map.Entry<String,PsiTransportDto> entry1: temp.entrySet()) {
					 String name =entry1.getKey();
					 PsiTransportDto dto=temp.get(name);
					if(priceMap==null||priceMap.get(country)==null||priceMap.get(country).get(name)==null||priceMap.get(country).get(name)==0){
						 avgPriceList2.add(new PsiProductLocalAvgPrice(name,country,dto.getMoney()/dto.getQuantity(),date));
					}else{//存在
						if("EU".equals(country)){//19
							Integer totalStock=0;
							
							if(localStock.get(19)!=null&&localStock.get(19).get(name)!=null){
								totalStock+=localStock.get(19).get(name);
							}
							Float latestPrice=priceMap.get(country).get(name);
							Float price=0f;
							if(totalStock==0||(totalStock-dto.getQuantity()<=0)){
								LOGGER.info(totalStock+"-"+country+"-"+name+" stock less");
								price=dto.getMoney()/dto.getQuantity();
							}else{
								price=(latestPrice*(totalStock-dto.getQuantity())+dto.getMoney())/totalStock;
							}
							if(price>0){
								avgPriceList2.add(new PsiProductLocalAvgPrice(name,country,price,date,totalStock));
							}
						}else if("US".equals(country)){//120PsiProductLocalAvgPrice
							Integer totalStock=0;
							
							if(localStock.get(120)!=null&&localStock.get(120).get(name)!=null){
								totalStock+=localStock.get(19).get(name);
							}
							Float latestPrice=priceMap.get(country).get(name);
							Float price=0f;
							if(totalStock==0||(totalStock-dto.getQuantity()<=0)){
								LOGGER.info(totalStock+"-"+country+"-"+name+" stock less");
								price=dto.getMoney()/dto.getQuantity();
							}else{
								price=(latestPrice*(totalStock-dto.getQuantity())+dto.getMoney())/totalStock;
							}
							if(price>0){
								avgPriceList2.add(new PsiProductLocalAvgPrice(name,country,price,date,totalStock));
							}
						}else{
							Integer totalStock=0;
							if(localStock.get(147)!=null&&localStock.get(147).get(name)!=null){
								totalStock+=localStock.get(147).get(name);
							}
							Float latestPrice=priceMap.get(country).get(name);
							Float price=0f;
							if(totalStock==0||(totalStock-dto.getQuantity()<=0)){
								LOGGER.info(totalStock+"-"+country+"-"+name+" stock less");
								price=dto.getMoney()/dto.getQuantity();
							}else{
								price=(latestPrice*(totalStock-dto.getQuantity())+dto.getMoney())/totalStock;
							}
							if(price>0){
								avgPriceList2.add(new PsiProductLocalAvgPrice(name,country,price,date,totalStock));
							}
						}
					}
				}
			 }
		 }
		 
		 
		 
		 if(avgPriceList2!=null&&avgPriceList2.size()>0){
			 psiTransportPaymentService.saveLocalAvgPriceList(avgPriceList2);
			 try{
				 List<Integer> idList=psiTransportPaymentService.findIsCount2("0");
				 psiTransportPaymentService.updateIsCount2(idList);
			 }catch(Exception e){
				 LOGGER.error("1更新查询标识异常2！",e.getMessage());
			 }
		 }
		 
		 
		 
		 
	}
	
	
	
	public void saveAvgPrice() {  

		 Date date=new Date();
		 List<PsiProductAvgPrice>  avgPriceList=Lists.newArrayList();
		 List<PsiProductAvgPrice>  avgPriceList2=Lists.newArrayList();
		 //0:local 1:fba
		 Map<String,Map<String,PsiTransportDto>>  map=psiTransportPaymentService.getYesterdayInStock("1");//country-name
		 Map<String,Map<String,PsiTransportDto>>  map2=psiTransportPaymentService.getYesterdayInStock2("1");//country-name
		 
		 
		 Map<String,Map<String,Float>>  priceMap=psiTransportPaymentService.findTransportAvgPrice();
		 Map<String,PsiInventoryFba> amazonStock=psiInventoryService.getAllProductFbaInfo();//productName+"_"+country
		 if(map!=null&&map.size()>0){
			 for(Map.Entry<String,Map<String,PsiTransportDto>> entry :map.entrySet()){
				 String country = entry.getKey();
				 Map<String,PsiTransportDto> temp=entry.getValue();
				 for (Map.Entry<String,PsiTransportDto> entry1: temp.entrySet()) {
					 String name =entry1.getKey();
					 PsiTransportDto dto=temp.get(name);
					if(priceMap==null||priceMap.get(country)==null||priceMap.get(country).get(name)==null||priceMap.get(country).get(name)==0){
						 avgPriceList.add(new PsiProductAvgPrice(name,country,dto.getMoney()/dto.getQuantity(),date));
					}else{//存在
						if("EU".equals(country)){//19
							Integer totalStock=0;
							if(amazonStock.get(name+"_eu")!=null){
								totalStock+=amazonStock.get(name+"_eu").getTotal();
							}
							Float latestPrice=priceMap.get(country).get(name);
							Float price=0f;
							if(totalStock==0||(totalStock-dto.getQuantity()<=0)){
								price=dto.getMoney()/dto.getQuantity();
							}else{
								price=(latestPrice*(totalStock-dto.getQuantity())+dto.getMoney())/totalStock;
							}
							if(price>0){
								avgPriceList.add(new PsiProductAvgPrice(name,country,price,date,totalStock));
							}
						}else if("US".equals(country)){//120
							Integer totalStock=0;
							if(amazonStock.get(name+"_com")!=null){
								totalStock+=amazonStock.get(name+"_com").getTotal();
							}
							if(amazonStock.get(name+"_ca")!=null){
								totalStock+=amazonStock.get(name+"_ca").getTotal();
							}
							if(amazonStock.get(name+"_mx")!=null){
								totalStock+=amazonStock.get(name+"_mx").getTotal();
							}
							Float latestPrice=priceMap.get(country).get(name);
							Float price=0f;
							if(totalStock==0||(totalStock-dto.getQuantity()<=0)){
								price=dto.getMoney()/dto.getQuantity();
							}else{
								price=(latestPrice*(totalStock-dto.getQuantity())+dto.getMoney())/totalStock;
							}
							if(price>0){
								avgPriceList.add(new PsiProductAvgPrice(name,country,price,date,totalStock));
							}
						}else{
							Integer totalStock=0;
							if(amazonStock.get(name+"_jp")!=null){
								totalStock+=amazonStock.get(name+"_jp").getTotal();
							}
							Float latestPrice=priceMap.get(country).get(name);
							Float price=0f;
							if(totalStock==0||(totalStock-dto.getQuantity()<=0)){
								price=dto.getMoney()/dto.getQuantity();
							}else{
								price=(latestPrice*(totalStock-dto.getQuantity())+dto.getMoney())/totalStock;
							}
							if(price>0){
								avgPriceList.add(new PsiProductAvgPrice(name,country,price,date,totalStock));
							}
						}
					}
				}
			 }
		 }
		
		 
		 if(avgPriceList!=null&&avgPriceList.size()>0){
			 psiTransportPaymentService.saveAvgPriceList(avgPriceList);
			 try{
				 List<Integer> idList=psiTransportPaymentService.findIsCount("1");
				 psiTransportPaymentService.updateIsCount(idList);
			 }catch(Exception e){
				 LOGGER.error("更新查询标识异常！",e.getMessage());
			 }
		 }
		 
		 priceMap=psiTransportPaymentService.findTransportAvgPrice();
		 if(map2!=null&&map2.size()>0){
			 for(Map.Entry<String,Map<String,PsiTransportDto>> entry :map2.entrySet()){
				 String country = entry.getKey();
				 Map<String,PsiTransportDto> temp=entry.getValue();
				 for (Map.Entry<String,PsiTransportDto> entry1: temp.entrySet()) {
					 String name =entry1.getKey();
					 PsiTransportDto dto=temp.get(name);
					if(priceMap==null||priceMap.get(country)==null||priceMap.get(country).get(name)==null||priceMap.get(country).get(name)==0){
						 avgPriceList2.add(new PsiProductAvgPrice(name,country,dto.getMoney()/dto.getQuantity(),date));
					}else{//存在
						if("EU".equals(country)){//19
							Integer totalStock=0;
							if(amazonStock.get(name+"_eu")!=null){
								totalStock+=amazonStock.get(name+"_eu").getTotal();
							}
							Float latestPrice=priceMap.get(country).get(name);
							Float price=0f;
							if(totalStock==0||(totalStock-dto.getQuantity()<=0)){
								price=dto.getMoney()/dto.getQuantity();
							}else{
								price=(latestPrice*(totalStock-dto.getQuantity())+dto.getMoney())/totalStock;
							}
							if(price>0){
								avgPriceList2.add(new PsiProductAvgPrice(name,country,price,date,totalStock));
							}
						}else if("US".equals(country)){//120
							Integer totalStock=0;
							if(amazonStock.get(name+"_com")!=null){
								totalStock+=amazonStock.get(name+"_com").getTotal();
							}
							if(amazonStock.get(name+"_ca")!=null){
								totalStock+=amazonStock.get(name+"_ca").getTotal();
							}
							if(amazonStock.get(name+"_mx")!=null){
								totalStock+=amazonStock.get(name+"_mx").getTotal();
							}
							Float latestPrice=priceMap.get(country).get(name);
							Float price=0f;
							if(totalStock==0||(totalStock-dto.getQuantity()<=0)){
								price=dto.getMoney()/dto.getQuantity();
							}else{
								price=(latestPrice*(totalStock-dto.getQuantity())+dto.getMoney())/totalStock;
							}
							if(price>0){
								avgPriceList2.add(new PsiProductAvgPrice(name,country,price,date,totalStock));
							}
						}else{
							Integer totalStock=0;
							if(amazonStock.get(name+"_jp")!=null){
								totalStock+=amazonStock.get(name+"_jp").getTotal();
							}
							Float latestPrice=priceMap.get(country).get(name);
							Float price=0f;
							if(totalStock==0||(totalStock-dto.getQuantity()<=0)){
								price=dto.getMoney()/dto.getQuantity();
							}else{
								price=(latestPrice*(totalStock-dto.getQuantity())+dto.getMoney())/totalStock;
							}
							if(price>0){
								avgPriceList2.add(new PsiProductAvgPrice(name,country,price,date,totalStock));
							}
						}
					}
				}
			 }
		 }
		 
		
		 if(avgPriceList2!=null&&avgPriceList2.size()>0){
			 psiTransportPaymentService.saveAvgPriceList(avgPriceList2);
			 try{
				 List<Integer> idList=psiTransportPaymentService.findIsCount2("1");
				 psiTransportPaymentService.updateIsCount2(idList);
			 }catch(Exception e){
				 LOGGER.error("更新查询标识异常2！",e.getMessage());
			 }
		 }
		 
		 fbaInboundService.findErrorFba();
		 List<Object[]>  fbaList=fbaInboundService.findFbaTran();
		 countFba(fbaList,amazonStock);
		 
		 List<Object[]>  fbaList1=fbaInboundService.findFbaTranItemNoFlag();
		 countFba(fbaList1,amazonStock);
	}
	
	
	public void  countFba(List<Object[]>  fbaList,Map<String,PsiInventoryFba> amazonStock){
		 Date date=new Date();
		 List<PsiProductAvgPrice>  avgPriceList3=Lists.newArrayList();
		 Set<String> idSet=Sets.newHashSet();
		 Map<String,Set<String>> noLocalPrice=Maps.newHashMap();
		 if(fbaList!=null&&fbaList.size()>0){
			 Map<String,Map<String,Float>> priceMap=psiTransportPaymentService.findTransportAvgPrice();//fbaPrice US JP EU - name 
			 Map<String,Map<String,Float>> localPriceMap=psiTransportPaymentService.findTransportLocalAvgPrice();
			 for (Object[] obj: fbaList) {
				String country=obj[1].toString();
				String name=obj[2].toString();
				Integer quantity=Integer.parseInt(obj[3].toString());
				if(obj[4]==null){
					continue;
				}
				Float fee=Float.parseFloat(obj[4].toString());
				if(fee/quantity>60){
					LOGGER.warn(">60:"+obj[0].toString()+"-"+country+"-"+name+"-"+fee+"-"+quantity);
					continue;
				}
				Integer receiverQuantity=Integer.parseInt(obj[5]==null?"0":obj[5].toString());
				if("DE".equals(country)){
					country="EU";
				}	
				
				if(localPriceMap==null||localPriceMap.get(country)==null||localPriceMap.get(country).get(name)==null||localPriceMap.get(country).get(name)==0){
					String sku=obj[6].toString();
					Set<String> skuSet=noLocalPrice.get(obj[0].toString());
					if(skuSet==null){
						skuSet=Sets.newHashSet();
						noLocalPrice.put(obj[0].toString(), skuSet);
					}
					skuSet.add(sku);
					LOGGER.info(country+"-"+name+" no local price!!!");
				}else{
					Float latestLocalPrice=localPriceMap.get(country).get(name);
					if(priceMap==null||priceMap.get(country)==null||priceMap.get(country).get(name)==null||priceMap.get(country).get(name)==0){
						avgPriceList3.add(new PsiProductAvgPrice(name,country,latestLocalPrice+fee/quantity,date,quantity));
						idSet.add(obj[0].toString());
					}else{
						float latestPrice=priceMap.get(country).get(name);
						Integer totalStock=0;
						if("DE".equals(country)||"EU".equals(country)){
							if(amazonStock.get(name+"_eu")!=null){
								totalStock+=amazonStock.get(name+"_eu").getTotal();
							}  
						}else if("US".equals(country)){
							if(amazonStock.get(name+"_com")!=null){
								totalStock+=amazonStock.get(name+"_com").getTotal();
							}
							if(amazonStock.get(name+"_ca")!=null){
								totalStock+=amazonStock.get(name+"_ca").getTotal();
							}
							if(amazonStock.get(name+"_mx")!=null){
								totalStock+=amazonStock.get(name+"_mx").getTotal();
							}
						}else if("JP".equals(country)){
							if(amazonStock.get(name+"_jp")!=null){
								totalStock+=amazonStock.get(name+"_jp").getTotal();
							}
						}
						Float price=0f;
						if(totalStock==0||(totalStock-receiverQuantity<=0)){
							price=latestLocalPrice+fee/quantity;
						}else{
							price=(latestPrice*(totalStock-receiverQuantity)+latestLocalPrice*quantity+fee)/(totalStock-receiverQuantity+quantity);
						}
						if(price>0){
							avgPriceList3.add(new PsiProductAvgPrice(name,country,price,date,(totalStock-receiverQuantity<0?0:totalStock-receiverQuantity)));
							idSet.add(obj[0].toString());
						}
					}
				}
			 }
		 }
		
		 if(avgPriceList3!=null&&avgPriceList3.size()>0){
			 psiTransportPaymentService.saveAvgPriceList(avgPriceList3);
			 try{
				 fbaInboundService.updateCount(idSet);
			 }catch(Exception e){
				 LOGGER.error("更新fba统计标识！",e.getMessage());
			 }
		 }
		 
		 if(noLocalPrice!=null&&noLocalPrice.size()>0){
			 try{
				 fbaInboundService.updateItemCount(noLocalPrice);
			 }catch(Exception e){
				 LOGGER.error("更新fba帖未统计列表标识！",e.getMessage());
			 }
		 }
	}
	
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("/spring-context.xml");
		PsiInventoryService psiInventoryService = applicationContext.getBean(PsiInventoryService.class);
		PsiOutOfStockInfoService psiOutOfStockInfoService = applicationContext.getBean(PsiOutOfStockInfoService.class);
		ProductSalesInfoService productSalesInfoService = applicationContext.getBean(ProductSalesInfoService.class);
		AmazonProduct2Service amazonProduct2Service = applicationContext.getBean(AmazonProduct2Service.class);
		PsiTransportOrderService psiTransportOrderService = applicationContext.getBean(PsiTransportOrderService.class);
		SalesForecastServiceByMonth salesForecastServiceByMonth = applicationContext.getBean(SalesForecastServiceByMonth.class);
		PsiInventoryFbaService psiInventoryFbaService=applicationContext.getBean(PsiInventoryFbaService.class);
		PsiProductEliminateService psiProductEliminateService=applicationContext.getBean(PsiProductEliminateService.class);
		PsiProductService  psiProductService=applicationContext.getBean(PsiProductService.class);
		PsiProductInStockService psiProductInStockService = applicationContext.getBean(PsiProductInStockService.class);
		PsiTransportPaymentService  psiTransportPaymentService=applicationContext.getBean(PsiTransportPaymentService.class);
		AmazonPromotionsWarningService  amazonPromotionsWarningService=applicationContext.getBean(AmazonPromotionsWarningService.class);
		PsiOutOfStockInfoMonitor temp=new PsiOutOfStockInfoMonitor();
		temp.setProductSalesInfoService(productSalesInfoService);
		temp.setPsiInventoryService(psiInventoryService);
		temp.setPsiOutOfStockInfoService(psiOutOfStockInfoService);
		temp.setAmazonProduct2Service(amazonProduct2Service);
		temp.setPsiTransportOrderService(psiTransportOrderService);
		temp.setSalesForecastService(salesForecastServiceByMonth);
		temp.setPsiInventoryFbaService(psiInventoryFbaService);
		temp.setPsiProductEliminateService(psiProductEliminateService);
		temp.setPsiProductService(psiProductService);
		temp.setPsiProductInStockService(psiProductInStockService);
		temp.setPsiTransportPaymentService(psiTransportPaymentService);
		temp.setAmazonPromotionsWarningService(amazonPromotionsWarningService);
		//temp.savePsiOutOfStockInfoList();
		//temp.savePsiInventoryGapList();
		//temp.sendGapEmail();
		//temp.sendEuGapEmail();
		//temp.initPsiTransportRateAndPrice();
		//temp.initAvgPrice();
		applicationContext.close();
	}

	
	
	public AmazonPromotionsWarningService getAmazonPromotionsWarningService() {
		return amazonPromotionsWarningService;
	}


	public void setAmazonPromotionsWarningService(
			AmazonPromotionsWarningService amazonPromotionsWarningService) {
		this.amazonPromotionsWarningService = amazonPromotionsWarningService;
	}


	public SalesForecastServiceByMonth getSalesForecastService() {
		return salesForecastService;
	}


	public void setSalesForecastService(
			SalesForecastServiceByMonth salesForecastService) {
		this.salesForecastService = salesForecastService;
	}




	public PsiInventoryFbaService getPsiInventoryFbaService() {
		return psiInventoryFbaService;
	}




	public void setPsiInventoryFbaService(
			PsiInventoryFbaService psiInventoryFbaService) {
		this.psiInventoryFbaService = psiInventoryFbaService;
	}




	public PsiProductEliminateService getPsiProductEliminateService() {
		return psiProductEliminateService;
	}




	public void setPsiProductEliminateService(
			PsiProductEliminateService psiProductEliminateService) {
		this.psiProductEliminateService = psiProductEliminateService;
	}




	public PsiProductService getPsiProductService() {
		return psiProductService;
	}




	public void setPsiProductService(PsiProductService psiProductService) {
		this.psiProductService = psiProductService;
	}




	public PsiOutOfStockInfoService getPsiOutOfStockInfoService() {
		return psiOutOfStockInfoService;
	}

	public void setPsiOutOfStockInfoService(
			PsiOutOfStockInfoService psiOutOfStockInfoService) {
		this.psiOutOfStockInfoService = psiOutOfStockInfoService;
	}

	public PsiInventoryService getPsiInventoryService() {
		return psiInventoryService;
	}

	public void setPsiInventoryService(PsiInventoryService psiInventoryService) {
		this.psiInventoryService = psiInventoryService;
	}

	public ProductSalesInfoService getProductSalesInfoService() {
		return productSalesInfoService;
	}

	public void setProductSalesInfoService(
			ProductSalesInfoService productSalesInfoService) {
		this.productSalesInfoService = productSalesInfoService;
	}

	public AmazonProduct2Service getAmazonProduct2Service() {
		return amazonProduct2Service;
	}

	public void setAmazonProduct2Service(AmazonProduct2Service amazonProduct2Service) {
		this.amazonProduct2Service = amazonProduct2Service;
	}

	public PsiTransportOrderService getPsiTransportOrderService() {
		return psiTransportOrderService;
	}

	public void setPsiTransportOrderService(
			PsiTransportOrderService psiTransportOrderService) {
		this.psiTransportOrderService = psiTransportOrderService;
	}


	public PsiProductInStockService getPsiProductInStockService() {
		return psiProductInStockService;
	}


	public void setPsiProductInStockService(
			PsiProductInStockService psiProductInStockService) {
		this.psiProductInStockService = psiProductInStockService;
	}


	public PsiTransportPaymentService getPsiTransportPaymentService() {
		return psiTransportPaymentService;
	}


	public void setPsiTransportPaymentService(
			PsiTransportPaymentService psiTransportPaymentService) {
		this.psiTransportPaymentService = psiTransportPaymentService;
	}
	
	 public Map<String,Map<String,String>> updatePromotionsCodeEmail(){
		 Map<String,String> map=amazonPromotionsWarningService.getNoEmail();
		 if(map!=null&&map.size()>0){
			 Map<String,Map<String,String>> codeMap=Maps.newHashMap();
			for(Map.Entry<String,String> entry :map.entrySet()){
				String country = entry.getKey();
				   try {
					   String result=HttpRequest.sendPost("http://50.62.30.1/api2.0/public/codeReflectOne", 
							    "platform="+country+"&codes="+entry.getValue()+"&app_key=azmv9M3sU2SI2aMK9qMAfF6sB5l2pF96");
								JSONObject object =(JSONObject) JSON.parse(result.toString());
						        Integer orderStatus=(Integer) object.get("status");
						        if(orderStatus==1){
						        	 JSONArray jsonArray=object.getJSONArray("data");
						                for (Object obj: jsonArray) {
						                	 JSONObject o = (JSONObject)obj;
						                	 String codeNumber=(String) o.get("code_number");
						                	 String email=(String) o.get("user_login");
						                	 if(StringUtils.isNotBlank(email)){
						                		 Map<String,String> temp=codeMap.get(country);
						                		 if(temp==null){
						                			 temp=Maps.newHashMap();
						                			 codeMap.put(country, temp);
						                		 }
						                		 temp.put(codeNumber, email);
						                	 }
						                }
						        }
					} catch (Exception e) {
						e.printStackTrace();
					}
			 }
			 return codeMap;
		 }
		 return null;
	 }
	
	 
	 
	 public void countAmazonFee(){
		 Map<String,Map<String,PsiProductEliminate>> eliminateMap=psiProductEliminateService.findAllByNameIdAndCountry();
		 Map<String,AmazonPostsDetail> sizeMap=amazonPostsDetailService.findPostsSize();
		 Map<String, String> isEuMap=psiProductService.getPowerOrKeyboardByName();//1:keyboard+带电
		 Map<Integer,Float> tempMap=Maps.newHashMap();
		 Map<Integer,Float> tempEuMap=Maps.newHashMap();
		 for (Map.Entry<String,Map<String,PsiProductEliminate>> entry: eliminateMap.entrySet()) {
			Map<String,PsiProductEliminate> map=entry.getValue();
			for (Map.Entry<String,PsiProductEliminate> ety : map.entrySet()) {
				String country=ety.getKey();
				PsiProductEliminate e=ety.getValue();
				AmazonPostsDetail detail=sizeMap.get(e.getColorName()+country);
				if(detail!=null){
					boolean flag=true;
					if("1".equals(isEuMap.get(e.getColorName()))){//不泛欧
						flag=false;
					}
					
					float length=detail.getPackageLength();
					float width=detail.getPackageWidth();
					float height=detail.getPackageHeight();
					float weight=detail.getPackageWeight();
					
					float length1=detail.getPackageLength()*2.54f;
					float width1=detail.getPackageWidth()*2.54f;
					float height1=detail.getPackageHeight()*2.54f;
					float weight1=detail.getPackageWeight()*0.4535924f;
					
					//float tempFbaFee=e.getFbaFee();
					float fbaFee=0f;
					float fbaFeeEu=0f;
					if("com".equals(country)){
						float vw=length*width*height/139;
						if(vw<=weight){
				    		vw=weight;
				    	}
						float girth = 2 * (width+height);
						if(weight<=1&&length<=15&&width<=12&&height<=0.75){//Small standard-size
							fbaFee=2.41f;
				    	}else if(weight<=20&&length<=18&&width<=14&&height<=8){//Large standard-size
				    	    vw=vw+0.25f;
				    	    if(vw<=1){
				    	    	fbaFee=3.19f;
				    	    }else if(vw>1&&vw<=2){
				    	    	fbaFee=4.71f;
				    	    }else{
				    	    	fbaFee=4.71f+0.38f*(vw-2);
				    	    }
				    	}else if(weight<=70&&length<=60&&width<=30&&girth<=130){//Small oversize
				    		vw=vw+1;
				    		fbaFee=8.13f+0.38f*(vw-2);
				    	}else if(weight<=150&&length<=108&&girth<=130){//Medium oversize
				    		vw=vw+1;
				    		fbaFee=9.44f+0.38f*(vw-2);
				    	}else if(weight<=150&&length<=108&&girth<=165){//Large oversize
				    		vw=vw+1;
				    		fbaFee=73.18f+0.79f*(vw-90);
				    	}else if(weight>150||length>108&&girth>165){//Special oversize
				    		vw=weight+1;
				    		fbaFee=137.32f+0.91f*(vw-90);
				    	}
					}else if("de".equals(country)){
						if(length1<=20&&width1<=15&&height1<=1){
							weight1=weight1+0.02f;
						}else if(length1<=33&&width1<=23&&height1<=2.5){
							weight1=weight1+0.04f;
						}else if(length1<=33&&width1<=23&&height1<=5){
							weight1=weight1+0.04f;
						}else if(length1<=45&&width1<=34&&height1<=26){
							weight1=weight1+0.1f;
						}else if(length1<=61&&width1<=46&&height1<=46){
							weight1=weight1+0.24f;
						}else if(length1<=120&&width1<=60&&height1<=60){
							weight1=weight1+0.24f;
						}else{
							weight1=weight1+0.24f;
						}
						
						if(length1<=20&&width1<=15&&height1<=1&&weight1*1000<=100){
							fbaFee=1.64f;
						}else if(length1<=33&&width1<=23&&height1<=2.5&&weight1*1000<=500){
							if(weight1*1000<101){
								fbaFee=1.81f;
							}else if(weight1*1000>=101&&weight1*1000<251){
								fbaFee=1.82f;
							}else{
								fbaFee=1.95f;
							}
						}else if(length1<=33&&width1<=23&&height1<=5&&weight1*1000<=1000){
							fbaFee=2.34f;
						}else if(length1<=45&&width1<=34&&height1<=26&&weight1<=12){
							if(weight1*1000<251){
								fbaFee=2.39f;
							}else if(weight1*1000>=251&&weight1*1000<501){
								fbaFee=2.5f;
							}else if(weight1*1000>=501&&weight1*1000<1001){
								fbaFee=3.08f;
							}else if(weight1*1000>=1001&&weight1*1000<1501){
								fbaFee=3.62f;
							}else if(weight1*1000>=1501&&weight1*1000<2001){
								fbaFee=3.66f;
							}else if(weight1*1000>=2001&&weight1*1000<3001){
								fbaFee=4.34f;
							}else if(weight1*1000>=3001&&weight1*1000<4001){
								fbaFee=4.36f;
							}else if(weight1*1000>=4001&&weight1*1000<5001){
								fbaFee=4.37f;
							}else if(weight1*1000>=5001&&weight1*1000<6001){
								fbaFee=4.7f;
							}else if(weight1*1000>=6001&&weight1*1000<7001){
								fbaFee=4.7f;
							}else if(weight1*1000>=7001&&weight1*1000<8001){
								fbaFee=4.83f;
							}else if(weight1*1000>=8001&&weight1*1000<9001){
								fbaFee=4.83f;
							}else if(weight1*1000>=9001&&weight1*1000<10001){
								fbaFee=4.83f;
							}else if(weight1*1000>=10001&&weight1*1000<11001){
								fbaFee=4.99f;
							}else if(weight1*1000>=11001&&weight1*1000<12001){
								fbaFee=5f;
							}
						}else if(length1<=61&&width1<=46&&height1<=46&&weight1*1000<=2000){
							if(weight1*1000<1001){
								fbaFee=5.03f;
							}else if(weight1*1000>=1001&&weight1*1000<1251){
								fbaFee=5.14f;
							}else if(weight1*1000>=1251&&weight1*1000<1501){
								fbaFee=5.18f;
							}else if(weight1*1000>=1501&&weight1*1000<1751){
								fbaFee=5.18f;
							}else if(weight1*1000>=1751&&weight1*1000<2000){
								fbaFee=5.25f;
							}
						}else if(length1<=120&&width1<=60&&height1<=60&&weight1<=30){
							
							if(weight1<=1){
								fbaFee=5.01f;
							}else if(weight1<=2){
								fbaFee=5.21f;
							}else if(weight1<=3){
								fbaFee=6.13f;
							}else if(weight1<=4){
								fbaFee=6.18f;
							}else if(weight1<=5){
								fbaFee=6.18f;
							}else if(weight1<=6){
								fbaFee=6.38f;
							}else if(weight1<=7){
								fbaFee=6.47f;
							}else if(weight1<=8){
								fbaFee=6.52f;
							}else if(weight1<=9){
								fbaFee=6.52f;
							}else if(weight1<=10){
								fbaFee=6.55f;
							}else if(weight1<=15){
								fbaFee=7.1f;
							}else if(weight1<=20){
								fbaFee=7.55f;
							}else if(weight1<=25){
								fbaFee=8.55f;
							}else if(weight1<=30){
								fbaFee=8.55f;
							}
						}else{
							if(weight1<=5){
								fbaFee=6.71f;
							}else if(weight1<=10){
								fbaFee=7.74f;
							}else if(weight1<=15){
								fbaFee=8.28f;
							}else if(weight1<=20){
								fbaFee=8.75f;
							}else if(weight1<=25){
								fbaFee=9.69f;
							}else if(weight1<=30){
								fbaFee=9.71f;
							}
						}
						fbaFee=fbaFee*MathUtils.getRate("EUR","USD",null);
					}else if("uk".equals(country)){
						
							if(length1<=20&&width1<=15&&height1<=1){
								weight1=weight1+0.02f;
							}else if(length1<=33&&width1<=23&&height1<=2.5){
								weight1=weight1+0.04f;
							}else if(length1<=33&&width1<=23&&height1<=5){
								weight1=weight1+0.04f;
							}else if(length1<=45&&width1<=34&&height1<=26){
								weight1=weight1+0.1f;
							}else if(length1<=61&&width1<=46&&height1<=46){
								weight1=weight1+0.24f;
							}else if(length1<=120&&width1<=60&&height1<=60){
								weight1=weight1+0.24f;
							}else{
								weight1=weight1+0.24f;
							}
							
							if(length1<=20&&width1<=15&&height1<=1&&weight1*1000<=100){
								fbaFee=1.34f;
							}else if(length1<=33&&width1<=23&&height1<=2.5&&weight1*1000<=500){
								if(weight1*1000<101){
									fbaFee=1.47f;
								}else if(weight1*1000>=101&&weight1*1000<251){
									fbaFee=1.62f;
								}else{
									fbaFee=1.72f;
								}
							}else if(length1<=33&&width1<=23&&height1<=5&&weight1*1000<=1000){
								fbaFee=1.97f;
							}else if(length1<=45&&width1<=34&&height1<=26&&weight1<=12){
								if(weight1*1000<251){
									fbaFee=1.98f;
								}else if(weight1*1000>=251&&weight1*1000<501){
									fbaFee=2.09f;
								}else if(weight1*1000>=501&&weight1*1000<1001){
									fbaFee=2.17f;
								}else if(weight1*1000>=1001&&weight1*1000<1501){
									fbaFee=2.31f;
								}else if(weight1*1000>=1501&&weight1*1000<2001){
									fbaFee=2.53f;
								}else if(weight1*1000>=2001&&weight1*1000<3001){
									fbaFee=3.61f;
								}else if(weight1*1000>=3001&&weight1*1000<4001){
									fbaFee=3.61f;
								}else if(weight1*1000>=4001&&weight1*1000<5001){
									fbaFee=3.71f;
								}else if(weight1*1000>=5001&&weight1*1000<6001){
									fbaFee=3.76f;
								}else if(weight1*1000>=6001&&weight1*1000<7001){
									fbaFee=3.76f;
								}else if(weight1*1000>=7001&&weight1*1000<8001){
									fbaFee=3.85f;
								}else if(weight1*1000>=8001&&weight1*1000<9001){
									fbaFee=3.85f;
								}else if(weight1*1000>=9001&&weight1*1000<10001){
									fbaFee=3.85f;
								}else if(weight1*1000>=10001&&weight1*1000<11001){
									fbaFee=3.86f;
								}else if(weight1*1000>=11001&&weight1*1000<12001){
									fbaFee=4f;
								}
							}else if(length1<=61&&width1<=46&&height1<=46&&weight1*1000<=2000){
								if(weight1*1000<1001){
									fbaFee=3.66f;
								}else if(weight1*1000>=1001&&weight1*1000<1251){
									fbaFee=4.08f;
								}else if(weight1*1000>=1251&&weight1*1000<1501){
									fbaFee=4.39f;
								}else if(weight1*1000>=1501&&weight1*1000<1751){
									fbaFee=4.48f;
								}else if(weight1*1000>=1751&&weight1*1000<2001){
									fbaFee=4.54f;
								}
							}else if(length1<=120&&width1<=60&&height1<=60&&weight1<=30){
								if(weight1<=1){
									fbaFee=4.65f;
								}else if(weight1<=2){
									fbaFee=4.96f;
								}else if(weight1<=3){
									fbaFee=5.05f;
								}else if(weight1<=4){
									fbaFee=5.08f;
								}else if(weight1<=5){
									fbaFee=5.12f;
								}else if(weight1<=6){
									fbaFee=6.04f;
								}else if(weight1<=7){
									fbaFee=6.1f;
								}else if(weight1<=8){
									fbaFee=6.13f;
								}else if(weight1<=9){
									fbaFee=6.13f;
								}else if(weight1<=10){
									fbaFee=6.16f;
								}else if(weight1<=15){
									fbaFee=6.55f;
								}else if(weight1<=20){
									fbaFee=6.88f;
								}else if(weight1<=25){
									fbaFee=7.62f;
								}else if(weight1<=30){
									fbaFee=7.62f;
								}
							}else{
								
								if(weight1<=5){
									fbaFee=5.71f;
								}else if(weight1<=10){
									fbaFee=6.88f;
								}else if(weight1<=15){
									fbaFee=7.27f;
								}else if(weight1<=20){
									fbaFee=7.62f;
								}else if(weight1<=25){
									fbaFee=8.3f;
								}else if(weight1<=30){
									fbaFee=8.32f;
								}
							}
						/*}*/
						fbaFee=fbaFee*MathUtils.getRate("GBP","USD",null);
					}else if("fr,it,es".contains(country)){
						//泛欧和不泛欧-country  用本地    -de 用cross
						if("it".equals(country)){
							if(length1<=20&&width1<=15&&height1<=1){
								weight1=weight1+0.02f;
							}else if(length1<=33&&width1<=23&&height1<=2.5){
								weight1=weight1+0.04f;
							}else if(length1<=33&&width1<=23&&height1<=5){
								weight1=weight1+0.04f;
							}else if(length1<=45&&width1<=34&&height1<=26){
								weight1=weight1+0.1f;
							}else if(length1<=61&&width1<=46&&height1<=46){
								weight1=weight1+0.24f;
							}else if(length1<=120&&width1<=60&&height1<=60){
								weight1=weight1+0.24f;
							}else{
								weight1=weight1+0.24f;
							}
							
							if(length1<=20&&width1<=15&&height1<=1&&weight1*1000<=100){
								fbaFee=2.55f;
							}else if(length1<=33&&width1<=23&&height1<=2.5&&weight1*1000<=500){
								if(weight1*1000<101){
									fbaFee=2.64f;
								}else if(weight1*1000>=101&&weight1*1000<251){
									fbaFee=2.89f;
								}else{
									fbaFee=3.14f;
								}
							}else if(length1<=33&&width1<=23&&height1<=5&&weight1*1000<=1000){
								fbaFee=3.39f;
							}else if(length1<=45&&width1<=34&&height1<=26&&weight1<=11){
								
								if(weight1*1000<251){
									fbaFee=3.52f;
								}else if(weight1*1000>=251&&weight1*1000<501){
									fbaFee=3.78f;
								}else if(weight1*1000>=501&&weight1*1000<1001){
									fbaFee=4.41f;
								}else if(weight1*1000>=1001&&weight1*1000<1501){
									fbaFee=4.75f;
								}else if(weight1*1000>=1501&&weight1*1000<2001){
									fbaFee=4.96f;
								}else if(weight1*1000>=2001&&weight1*1000<3001){
									fbaFee=5.39f;
								}else if(weight1*1000>=3001&&weight1*1000<4001){
									fbaFee=5.92f;
								}else if(weight1*1000>=4001&&weight1*1000<5001){
									fbaFee=6.16f;
								}else if(weight1*1000>=5001&&weight1*1000<6001){
									fbaFee=6.26f;
								}else if(weight1*1000>=6001&&weight1*1000<7001){
									fbaFee=6.26f;
								}else if(weight1*1000>=7001&&weight1*1000<8001){
									fbaFee=6.46f;
								}else if(weight1*1000>=8001&&weight1*1000<9001){
									fbaFee=6.48f;
								}else if(weight1*1000>=9001&&weight1*1000<10001){
									fbaFee=6.62f;
								}else if(weight1*1000>=10001&&weight1*1000<11001){
									fbaFee=6.62f;
								}else if(weight1*1000>=11001&&weight1*1000<12000){
									fbaFee=6.63f;
								}
							}else if(length1<=61&&width1<=46&&height1<=46&&weight1*1000<=2000){
								
								if(weight1*1000<1001){
									fbaFee=6.66f;
								}else if(weight1*1000>=1001&&weight1*1000<1251){
									fbaFee=6.81f;
								}else if(weight1*1000>=1251&&weight1*1000<1501){
									fbaFee=7.05f;
								}else if(weight1*1000>=1501&&weight1*1000<1751){
									fbaFee=7.1f;
								}else if(weight1*1000>=1751&&weight1*1000<2001){
									fbaFee=7.14f;
								}
							}else if(length1<=120&&width1<=60&&height1<=60&&weight1<=30){
								
								if(weight1<=1){
									fbaFee=7.02f;
								}else if(weight1<=2){
									fbaFee=7.14f;
								}else if(weight1<=3){
									fbaFee=7.15f;
								}else if(weight1<=4){
									fbaFee=7.64f;
								}else if(weight1<=5){
									fbaFee=7.68f;
								}else if(weight1<=6){
									fbaFee=8.52f;
								}else if(weight1<=7){
									fbaFee=8.52f;
								}else if(weight1<=8){
									fbaFee=8.64f;
								}else if(weight1<=9){
									fbaFee=8.69f;
								}else if(weight1<=10){
									fbaFee=8.74f;
								}else if(weight1<=15){
									fbaFee=9.68f;
								}else if(weight1<=20){
									fbaFee=9.98f;
								}else if(weight1<=25){
									fbaFee=10.62f;
								}else if(weight1<=30){
									fbaFee=11.15f;
								}
							}else{
								if(weight1<=5){
									fbaFee=7.68f;
								}else if(weight1<=10){
									fbaFee=8.74f;
								}else if(weight1<=15){
									fbaFee=9.63f;
								}else if(weight1<=20){
									fbaFee=9.94f;
								}else if(weight1<=25){
									fbaFee=11.15f;
								}else if(weight1<=30){
									fbaFee=11.22f;
								}
							}
							fbaFee=fbaFee*MathUtils.getRate("EUR","USD",null);
						}else if("es".equals(country)){
							if(length1<=20&&width1<=15&&height1<=1){
								weight1=weight1+0.02f;
							}else if(length1<=33&&width1<=23&&height1<=2.5){
								weight1=weight1+0.04f;
							}else if(length1<=33&&width1<=23&&height1<=5){
								weight1=weight1+0.04f;
							}else if(length1<=45&&width1<=34&&height1<=26){
								weight1=weight1+0.1f;
							}else if(length1<=61&&width1<=46&&height1<=46){
								weight1=weight1+0.24f;
							}else if(length1<=120&&width1<=60&&height1<=60){
								weight1=weight1+0.24f;
							}else{
								weight1=weight1+0.24f;
							}
							
							if(length1<=20&&width1<=15&&height1<=1&&weight1*1000<=100){
								fbaFee=2.07f;
							}else if(length1<=33&&width1<=23&&height1<=2.5&&weight1*1000<=500){
								if(weight1*1000<101){
									fbaFee=2.4f;
								}else if(weight1*1000>=101&&weight1*1000<251){
									fbaFee=2.61f;
								}else{
									fbaFee=2.82f;
								}
							}else if(length1<=33&&width1<=23&&height1<=5&&weight1*1000<=1000){
								fbaFee=2.93f;
							}else if(length1<=45&&width1<=34&&height1<=26&&weight1<=11){
								
								if(weight1*1000<251){
									fbaFee=2.76f;
								}else if(weight1*1000>=251&&weight1*1000<501){
									fbaFee=3.19f;
								}else if(weight1*1000>=501&&weight1*1000<1001){
									fbaFee=3.41f;
								}else if(weight1*1000>=1001&&weight1*1000<1501){
									fbaFee=3.82f;
								}else if(weight1*1000>=1501&&weight1*1000<2001){
									fbaFee=3.88f;
								}else if(weight1*1000>=2001&&weight1*1000<3001){
									fbaFee=4.41f;
								}else if(weight1*1000>=3001&&weight1*1000<4001){
									fbaFee=4.85f;
								}else if(weight1*1000>=4001&&weight1*1000<5001){
									fbaFee=5.16f;
								}else if(weight1*1000>=5001&&weight1*1000<6001){
									fbaFee=5.25f;
								}else if(weight1*1000>=6001&&weight1*1000<7001){
									fbaFee=5.25f;
								}else if(weight1*1000>=7001&&weight1*1000<8001){
									fbaFee=5.38f;
								}else if(weight1*1000>=8001&&weight1*1000<9001){
									fbaFee=5.38f;
								}else if(weight1*1000>=9001&&weight1*1000<10001){
									fbaFee=5.38f;
								}else if(weight1*1000>=10001&&weight1*1000<11001){
									fbaFee=5.38f;
								}else if(weight1*1000>=11001&&weight1*1000<12001){
									fbaFee=5.39f;
								}
							}else if(length1<=61&&width1<=46&&height1<=46&&weight1*1000<=1750){
								
								if(weight1*1000<1001){
									fbaFee=3.67f;
								}else if(weight1*1000>=1001&&weight1*1000<1251){
									fbaFee=3.67f;
								}else if(weight1*1000>=1251&&weight1*1000<1501){
									fbaFee=3.98f;
								}else if(weight1*1000>=1501&&weight1*1000<1751){
									fbaFee=3.98f;
								}else if(weight1*1000>=1751&&weight1*1000<2001){
									fbaFee=4.23f;
								}
							}else if(length1<=120&&width1<=60&&height1<=60&&weight1<=30){
								
								if(weight1<=1){
									fbaFee=4.02f;
								}else if(weight1<=2){
									fbaFee=4.49f;
								}else if(weight1<=3){
									fbaFee=4.97f;
								}else if(weight1<=4){
									fbaFee=5.02f;
								}else if(weight1<=5){
									fbaFee=5.18f;
								}else if(weight1<=6){
									fbaFee=6.59f;
								}else if(weight1<=7){
									fbaFee=6.71f;
								}else if(weight1<=8){
									fbaFee=6.91f;
								}else if(weight1<=9){
									fbaFee=7.32f;
								}else if(weight1<=10){
									fbaFee=7.62f;
								}else if(weight1<=15){
									fbaFee=8.2f;
								}else if(weight1<=20){
									fbaFee=8.9f;
								}else if(weight1<=25){
									fbaFee=8.9f;
								}else if(weight1<=30){
									fbaFee=9.89f;
								}
							}else{
								
								if(weight1<=5){
									fbaFee=5.18f;
								}else if(weight1<=10){
									fbaFee=7.62f;
								}else if(weight1<=15){
									fbaFee=8.24f;
								}else if(weight1<=20){
									fbaFee=8.9f;
								}else if(weight1<=25){
									fbaFee=9.65f;
								}else if(weight1<=30){
									fbaFee=11.07f;
								}
							}
							fbaFee=fbaFee*MathUtils.getRate("EUR","USD",null);
						}else if("fr".equals(country)){
							if(length1<=20&&width1<=15&&height1<=1){
								weight1=weight1+0.02f;
							}else if(length1<=33&&width1<=23&&height1<=2.5){
								weight1=weight1+0.04f;
							}else if(length1<=33&&width1<=23&&height1<=5){
								weight1=weight1+0.04f;
							}else if(length1<=45&&width1<=34&&height1<=26){
								weight1=weight1+0.1f;
							}else if(length1<=61&&width1<=46&&height1<=46){
								weight1=weight1+0.24f;
							}else if(length1<=120&&width1<=60&&height1<=60){
								weight1=weight1+0.24f;
							}else{
								weight1=weight1+0.24f;
							}
							
							if(length1<=20&&width1<=15&&height1<=1&&weight1*1000<=100){
								fbaFee=2.11f;
							}else if(length1<=33&&width1<=23&&height1<=2.5&&weight1*1000<=500){
								if(weight1*1000<100){
									fbaFee=2.24f;
								}else if(weight1*1000>=101&&weight1*1000<251){
									fbaFee=2.83f;
								}else{
									fbaFee=3.47f;
								}
							}else if(length1<=33&&width1<=23&&height1<=5&&weight1*1000<=1000){
								fbaFee=4.15f;
							}else if(length1<=45&&width1<=34&&height1<=26&&weight1<=11){
								
								if(weight1*1000<251){
									fbaFee=4.39f;
								}else if(weight1*1000>=251&&weight1*1000<501){
									fbaFee=4.98f;
								}else if(weight1*1000>=501&&weight1*1000<1001){
									fbaFee=5.05f;
								}else if(weight1*1000>=1001&&weight1*1000<1501){
									fbaFee=5.16f;
								}else if(weight1*1000>=1501&&weight1*1000<2001){
									fbaFee=5.27f;
								}else if(weight1*1000>=2001&&weight1*1000<3001){
									fbaFee=6.52f;
								}else if(weight1*1000>=3001&&weight1*1000<4001){
									fbaFee=6.54f;
								}else if(weight1*1000>=4001&&weight1*1000<5001){
									fbaFee=6.54f;
								}else if(weight1*1000>=5001&&weight1*1000<6001){
									fbaFee=6.65f;
								}else if(weight1*1000>=6001&&weight1*1000<7001){
									fbaFee=6.65f;
								}else if(weight1*1000>=7001&&weight1*1000<8001){
									fbaFee=6.82f;
								}else if(weight1*1000>=8001&&weight1*1000<9001){
									fbaFee=6.82f;
								}else if(weight1*1000>=9001&&weight1*1000<10001){
									fbaFee=6.82f;
								}else if(weight1*1000>=10001&&weight1*1000<11001){
									fbaFee=6.86f;
								}else if(weight1*1000>=11001&&weight1*1000<12001){
									fbaFee=6.87f;
								}
							}else if(length1<=61&&width1<=46&&height1<=46&&weight1*1000<=2000){
								
								if(weight1*1000<1001){
									fbaFee=6.64f;
								}else if(weight1*1000>=1001&&weight1*1000<1251){
									fbaFee=6.88f;
								}else if(weight1*1000>=1251&&weight1*1000<1501){
									fbaFee=6.96f;
								}else if(weight1*1750>=1501&&weight1*1000<1751){
									fbaFee=6.96f;
								}else if(weight1*1000>=1751&&weight1*1000<2001){
									fbaFee=7.42f;
								}
							}else if(length1<=120&&width1<=60&&height1<=60&&weight1<=30){
								
								if(weight1<=1){
									fbaFee=7.14f;
								}else if(weight1<=2){
									fbaFee=8.15f;
								}else if(weight1<=3){
									fbaFee=8.56f;
								}else if(weight1<=4){
									fbaFee=8.92f;
								}else if(weight1<=5){
									fbaFee=8.98f;
								}else if(weight1<=6){
									fbaFee=9.51f;
								}else if(weight1<=7){
									fbaFee=9.62f;
								}else if(weight1<=8){
									fbaFee=9.67f;
								}else if(weight1<=9){
									fbaFee=9.67f;
								}else if(weight1<=10){
									fbaFee=9.72f;
								}else if(weight1<=15){
									fbaFee=10.39f;
								}else if(weight1<=20){
									fbaFee=10.92f;
								}else if(weight1<=25){
									fbaFee=10.92f;
								}else if(weight1<=30){
									fbaFee=12.16f;
								}
							}else{
								
								if(weight1<=5){
									fbaFee=9.03f;
								}else if(weight1<=10){
									fbaFee=10.95f;
								}else if(weight1<=15){
									fbaFee=11.59f;
								}else if(weight1<=20){
									fbaFee=12.16f;
								}else if(weight1<=25){
									fbaFee=13.29f;
								}else if(weight1<=30){
									fbaFee=13.61f;
								}
							}
							fbaFee=fbaFee*MathUtils.getRate("EUR","USD",null);
						  }
						
						
						
						
						if(!flag){//不泛欧
							 length=detail.getPackageLength();
							 width=detail.getPackageWidth();
							 height=detail.getPackageHeight();
							 weight=detail.getPackageWeight();
							
							 length1=detail.getPackageLength()*2.54f;
							 width1=detail.getPackageWidth()*2.54f;
							 height1=detail.getPackageHeight()*2.54f;
							 weight1=detail.getPackageWeight()*0.4535924f;
							
							if(length1<=20&&width1<=15&&height1<=1){
								weight1=weight1+0.02f;
							}else if(length1<=33&&width1<=23&&height1<=2.5){
								weight1=weight1+0.04f;
							}else if(length1<=33&&width1<=23&&height1<=5){
								weight1=weight1+0.04f;
							}else if(length1<=45&&width1<=34&&height1<=26){
								weight1=weight1+0.1f;
							}else if(length1<=61&&width1<=46&&height1<=46){
								weight1=weight1+0.24f;
							}else if(length1<=120&&width1<=60&&height1<=60){
								weight1=weight1+0.24f;
							}else{
								weight1=weight1+0.24f;
							}
							
							if(length1<=20&&width1<=15&&height1<=1&&weight1*1000<=100){
								fbaFeeEu=2.9f;
							}else if(length1<=33&&width1<=23&&height1<=2.5&&weight1*1000<=500){
								if(weight1*1000<101){
									fbaFeeEu=3.04f;
								}else if(weight1*1000>=101&&weight1*1000<251){
									fbaFeeEu=3.09f;
								}else{
									fbaFeeEu=3.15f;
								}
							}else if(length1<=33&&width1<=23&&height1<=5&&weight1*1000<=1000){
								fbaFeeEu=3.58f;
							}else if(length1<=45&&width1<=34&&height1<=26&&weight1<=12){
								if(weight1*1000<251){
									fbaFeeEu=3.68f;
								}else if(weight1*1000>=251&&weight1*1000<501){
									fbaFeeEu=3.81f;
								}else if(weight1*1000>=501&&weight1*1000<1001){
									fbaFeeEu=4.4f;
								}else if(weight1*1000>=1001&&weight1*1000<1501){
									fbaFeeEu=4.54f;
								}else if(weight1*1000>=1501&&weight1*1000<2001){
									fbaFeeEu=4.61f;
								}else if(weight1*1000>=2001&&weight1*1000<3001){
									fbaFeeEu=5.34f;
								}else if(weight1*1000>=3001&&weight1*1000<4001){
									fbaFeeEu=5.52f;
								}else if(weight1*1000>=4001&&weight1*1000<5001){
									fbaFeeEu=5.54f;
								}else if(weight1*1000>=5001&&weight1*1000<6001){
									fbaFeeEu=5.88f;
								}else if(weight1*1000>=6001&&weight1*1000<7001){
									fbaFeeEu=5.88f;
								}else if(weight1*1000>=7001&&weight1*1000<8001){
									fbaFeeEu=5.9f;
								}else if(weight1*1000>=8001&&weight1*1000<9001){
									fbaFeeEu=5.9f;
								}else if(weight1*1000>=9001&&weight1*1000<10001){
									fbaFeeEu=5.9f;
								}else if(weight1*1000>=10001&&weight1*1000<11001){
									fbaFeeEu=6.28f;
								}else if(weight1*1000>=11001&&weight1*1000<12001){
									fbaFeeEu=6.28f;
								}
							}else if(length1<=61&&width1<=46&&height1<=46&&weight1*1000<=2000){
								if(weight1*1000<1001){
									fbaFeeEu=6.94f;
								}else if(weight1*1000>=1001&&weight1*1000<1251){
									fbaFeeEu=7.16f;
								}else if(weight1*1000>=1251&&weight1*1000<1501){
									fbaFeeEu=7.18f;
								}else if(weight1*1000>=1501&&weight1*1000<1751){
									fbaFeeEu=7.22f;
								}else if(weight1*1000>=1751&&weight1*1000<2001){
									fbaFeeEu=7.24f;
								}
							}else if(length1<=120&&width1<=60&&height1<=60&&weight1<=30){
								if(weight1<=1){
									fbaFeeEu=6.93f;
								}else if(weight1<=2){
									fbaFeeEu=7.13f;
								}else if(weight1<=3){
									fbaFeeEu=7.61f;
								}else if(weight1<=4){
									fbaFeeEu=7.65f;
								}else if(weight1<=5){
									fbaFeeEu=7.71f;
								}else if(weight1<=6){
									fbaFeeEu=8.46f;
								}else if(weight1<=7){
									fbaFeeEu=8.53f;
								}else if(weight1<=8){
									fbaFeeEu=8.53f;
								}else if(weight1<=9){
									fbaFeeEu=8.53f;
								}else if(weight1<=10){
									fbaFeeEu=8.73f;
								}else if(weight1<=15){
									fbaFeeEu=9.25f;
								}else if(weight1<=20){
									fbaFeeEu=10.06f;
								}else if(weight1<=25){
									fbaFeeEu=10.79f;
								}else if(weight1<=30){
									fbaFeeEu=10.95f;
								}
							}else{
								if(weight1<=5){
									fbaFeeEu=7.96f;
								}else if(weight1<=10){
									fbaFeeEu=9.53f;
								}else if(weight1<=15){
									fbaFeeEu=10.13f;
								}else if(weight1<=20){
									fbaFeeEu=11.17f;
								}else if(weight1<=25){
									fbaFeeEu=12.12f;
								}else if(weight1<=30){
									fbaFeeEu=12.12f;
								}
							}
							fbaFeeEu=fbaFeeEu*MathUtils.getRate("EUR","USD",null);
						}
						
					}else if("jp".equals(country)){
						if(length1<=25&&width1<=18&&height1<=2&&weight1<0.25){
							fbaFee=226f;
				    	}else if(length1<=45&&width1<=35&&height1<=20&&weight1<9){
				    		if(weight1<2){
				    			fbaFee = 360f;
				    		}else{
				    			fbaFee=360f+MathUtils.roundUp(weight1-2d)*6;
				    		}
				    	}else if((length1>45||width1>35||height1>20)&&weight1<9&&(length1+width1+height1)<170){
				    		if(length1+width1+height1<100){
				    			fbaFee=622f;
				    		}else if(length1+width1+height1>=100&&length1+width1+height1<140){
				    			fbaFee=676f;
				    		}else if(length1+width1+height1>=140&&length1+width1+height1<170){
				    			fbaFee=738f;
				    		}
				    	}else if((length1+width1+height1)>=170&&(length1+width1+height1)<200&&length1<90&&weight1<40){
				    		fbaFee=1398f;
				    	}
						fbaFee=fbaFee*MathUtils.getRate("JPY","USD",null);
					}else if("ca".equals(country)){
						float vw=length1*height1*width1/6000;
						if(vw<weight1){
							vw=weight1;
						}
						if(weight1<=0.5&&length1<=38&&width1<=27&&height1<=2){//Small standard-size
							vw=vw+0.025f;
							if(vw>0.1){
								int mod=MathUtils.roundUp((vw-0.1d)/0.1d);
								fbaFee=1.6f+1.9f+mod*0.25f;
							}else{
								fbaFee=1.6f+1.9f;
							}
				    	}else if(weight1<=9&&length1<=45&&width1<=35&&height1<=20){//Small standard-size
				    		vw=vw+0.125f;
				    		if(vw>0.5){
								int mod=MathUtils.roundUp((vw-0.5d)/0.5d);
								fbaFee=1.6f+4f+mod*0.4f;
							}else{
								fbaFee=1.6f+4f;
							}
				    	}else{
				    		vw=vw+0.5f;
				    		if((length1+2*height1+2*width1)>419&&vw>69){
				    			fbaFee=125;
				    		}else{
				    			if(vw>0.5){
				    				int mod=MathUtils.roundUp((vw-0.5d)/0.5d);
									fbaFee=2.65f+4f+mod*0.4f;
								}else{
									fbaFee=2.65f+4f;
								}
				    		}
				    	}
						fbaFee=fbaFee*1.15f*MathUtils.getRate("CAD","USD",null);
					}
					
					if(fbaFee>0){
						tempMap.put(e.getId(),fbaFee);
					}
					if(fbaFeeEu>0){
						tempEuMap.put(e.getId(),fbaFeeEu);
					}
				}
			}
		}
		 
		 if(tempMap!=null&&tempMap.size()>0){
			 psiProductEliminateService.updateFbaFee(tempMap);
		 }
		 
		 if(tempEuMap!=null&&tempEuMap.size()>0){
			 psiProductEliminateService.updateFbaEuFee(tempEuMap);
		 }
		 
		 Map<String,List<Object[]>> fbaFeeMap=psiProductEliminateService.findExceptionFbaFee();
		 if(fbaFeeMap!=null&&fbaFeeMap.size()>0){
			    Map<String,String> activeMap=amazonProduct2Service.findIsActive();
			    StringBuffer content= new StringBuffer("Hi,All<br/><br/>货币单位为对应国家货币单位<br/><br/>");
			 
			    content.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
			    for (Entry<String, List<Object[]>> entry : fbaFeeMap.entrySet()) {  
				    String country=entry.getKey();
					content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32'><td colspan='6'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country).toUpperCase()+" FBA异常费用</span></td></tr>");
					content.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>产品</th><th>SKU</th><th>亚马逊FBA费用</th><th>ERP FBA费用</th><th>费用差</th><th>状态</th></tr>");
					List<Object[]> list=entry.getValue();
					for (Object[] obj: list) {
						content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '>");
	        			content.append("<td>"+(obj[1]==null?"":obj[1].toString())+"</td>");
	        			content.append("<td>"+(obj[5]==null?"":obj[5].toString())+"</td>");
	        			float rate=1f;
	        			if("de,fr,it,es".contains(country)){
	        				rate=MathUtils.getRate("EUR","USD",null);
	        			}else if("uk".equals(country)){
	        				rate=MathUtils.getRate("GBP","USD",null);
	        			}else if("ca".equals(country)){
	        				rate=MathUtils.getRate("CAD","USD",null);
	        			}else if("jp".equals(country)){
	        				rate=MathUtils.getRate("JPY","USD",null);
	        			}else if("mx".equals(country)){
	        				rate=MathUtils.getRate("MXN","USD",null);
	        			}
	        			float fbaFee=0f;
	        			float erpFee=0f;
	        			float fee=0f;
	        			if(obj[2]!=null){
	        				fbaFee=new BigDecimal(Float.parseFloat(obj[2].toString())/rate).setScale(2,4).floatValue();
	        			}
	        			if(obj[3]!=null){
	        				erpFee=new BigDecimal(Float.parseFloat(obj[3].toString())/rate).setScale(2,4).floatValue();
	        			}
	        			if(obj[4]!=null){
	        				fee=new BigDecimal(Float.parseFloat(obj[4].toString())/rate).setScale(2,4).floatValue();
	        			}
	        			content.append("<td>"+(fbaFee>0?fbaFee:"")+"</td>");
	        			content.append("<td>"+(erpFee>0?erpFee:"")+"</td>");
	        			if(Float.parseFloat(obj[4].toString())>0){
	        				content.append("<td style='color:red' >"+fee+"</td>");
	        			}else{
	        				content.append("<td>"+fee+"</td>");
	        			}
	        			String key=country+(obj[5]==null?"":obj[5].toString());
	        			content.append("<td>"+(activeMap.get(key)==null?"":activeMap.get(key))+"</td>");
	        			content.append("</tr>");
					}
	        	}
				content.append("</table><br/><br/>");
				
				
			    Date date = new Date();
				String  toAddress="eileen@inateck.com,tim@inateck.com,bella@inateck.com";
			   //String  toAddress="eileen@inateck.com";
				final MailInfo mailInfo = new MailInfo(toAddress,"亚马逊FBA费用异常提醒"+new SimpleDateFormat("yyyyMMdd").format(date),date);
				mailInfo.setContent(content.toString());
				new Thread(){
					public void run(){
						mailManager.send(mailInfo);
					}
				}.start();
		 }
		 
	 }
	 
	 

		public void generateDayOrder(){
			 List<PsiProduct> productList=psiProductService.findIsComponents("0");
			 Map<String,PsiProductEliminate> productCountryAttrMap=psiProductEliminateService.findProductCountryAttr();
			 Map<String,String> skuMap=psiProductService.getSkuByProduct();
			 Map<String,Map<Date,Map<String,Integer>>> poMap=forecastOrderService.getPurchaseOrder(productCountryAttrMap,skuMap);//name_country date sku/total quantity
			 Map<String,Map<Date,Map<String,Integer>>> tranMap=forecastOrderService.getAllTransportQuantity();
			 Map<String,Integer> receiveFbaTran = psiInventoryService.getFbaTransporttingReceiveByName();
			 Map<String,PsiInventoryFba> amazonStock=psiInventoryService.getAllProductFbaInfo();
			 Map<String,Set<String>> typeMap=saleReportService.findProductByType();////0:keyboard 1:四国泛欧
			 Set<String> keyBoardSet=typeMap.get("0");
			 Set<String> euNoUkSet=typeMap.get("1");
			 Map<String,PsiInventoryTotalDtoByInStock> inventoryMap=psiInventoryService.getInventoryQuantity();
			 Map<String,Map<String,PsiProductInStock>> stockMap=psiProductInStockService.getHistoryInventory();
			 Map<String,Map<String,Integer>> cnStock=forecastOrderService.getCnStockProduct();//name_country sku 
			 
			Map<String,Map<String,Integer>> allDaySalesMap= salesForecastService.findAllWithType();//name+"_"+country 月    月销  
			 Map<String,Map<String,Integer>>  sale30Map = psiInventoryFbaService.get31SalesMap(null);//country name 
		     SimpleDateFormat dateFormatMonth=new SimpleDateFormat("yyyyMM");		
			 List<String> euCountryList=Lists.newArrayList("de","fr","uk","it","es");	
			 SimpleDateFormat dateFormatDay=new SimpleDateFormat("yyyyMMdd");
			// Map<String,Integer> cnUsedQtyMap=Maps.newHashMap();
			// Map<String,Map<Date,Map<String,Integer>>> poUsedMap=Maps.newHashMap();
			 boolean flag=false;//缓冲期开关
			 Date today=new Date();
			 List<PsiTransportForecastAnalyseOrder> orderList=Lists.newArrayList();
			 for (PsiProduct product : productList) {
				 String platform=product.getPlatform();
				 String[] countryArr=platform.split(",");
				 for(String name:product.getProductNameWithColor()){
					    for (String country: countryArr) {
							  String key=name+"_"+country;
							  PsiProductEliminate eliminate=productCountryAttrMap.get(key);
							  if(eliminate==null){
								  continue;
							  }
							  PsiInventoryTotalDtoByInStock inventorys = inventoryMap.get(name);
							  //淘汰无采购单无库存产品不分析
	                          if("淘汰".equals(eliminate.getIsSale())&&(poMap==null||poMap.get(name)==null||poMap.get(name).get(country)==null)&&(inventorys==null||inventorys.getInventorys()==null||inventorys.getInventorys().get(country)==null
										||inventorys.getInventorys().get(country).getQuantityInventory()==null||inventorys.getInventorys().get(country).getQuantityInventory().get("CN")==null
										||inventorys.getInventorys().get(country).getQuantityInventory().get("CN").getNewQuantity()==null||inventorys.getInventorys().get(country).getQuantityInventory().get("CN").getNewQuantity()==0)){
					    		  continue;
					    	  } 
	                          PsiTransportForecastAnalyseOrder order=new PsiTransportForecastAnalyseOrder();
	                          order.setProductName(name);
	                          order.setCountryCode(country);
	                          order.setUpdateDate(today);
	                          Map<Date,Map<String,Integer>> namePoMap=Maps.newLinkedHashMap();
	                          List<Date> poDateList=Lists.newArrayList();
							  if(poMap!=null&&poMap.get(key)!=null){
								  namePoMap=poMap.get(key);
								  poDateList=Lists.newArrayList(namePoMap.keySet());
								  Collections.sort(poDateList);
							  }
						      Map<String,Integer> cnMap=Maps.newHashMap();
						      if(cnStock.get(name+"_"+country)!=null){
						    	  cnMap=cnStock.get(name+"_"+country);
						      }
							  Integer seaDays=PsiConfig.get(country).getFbaBySea();
							  Integer airDays=PsiConfig.get(country).getFbaBySky();
							  Integer period=product.getProducePeriod()+7;//7天备货期
							  Date airDate=DateUtils.addDays(today, airDays+7);
							  Date seaDate=DateUtils.addDays(today, seaDays+7);
							  if(1==eliminate.getTransportType()&&!"ca".equals(country)){//1:海运 2：空运
								  period+=seaDays;
							  }else{
								  period+=airDays;
							  }
							  if(flag){
								  period+=eliminate.getBufferPeriod();
							  }
							  order.setPeirod(period);
							  Integer fbaStock=0;
							  Integer oversea=0;
							  Integer safeInventory=0;
								
							  Map<String,Integer> daySalesMap=Maps.newHashMap();
							  String tip="";
							  String salesInfo="";
							  String poInfo="";
							  String fillUpTip="";
							  String transInfo="";
							  for(Date date:poDateList){
								  Map<String, Integer> dateMap= namePoMap.get(date);
								  poInfo+=dateFormatDay.format(date)+":"+dateMap.get("total")+"<br/><br/>";
							  }
							  order.setPoInfo(poInfo);
							  if(cnMap.get("total")!=null&&cnMap.get("total")>0){
								  order.setCnStock(cnMap.get("total"));
							  }
							  
							  Map<Date,Map<String,Integer>> dateTranMap=Maps.newHashMap();
							  Integer day31Sales=0;
							  if(!keyBoardSet.contains(name)&&!euNoUkSet.contains(name)&&"de".equals(country)){//泛欧
								  PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_eu");
								  if(psiInventoryFba!=null){
										fbaStock=psiInventoryFba.getRealTotal();
										Integer receiveNum=0;
										if(receiveFbaTran!=null&&receiveFbaTran.size()>0){
											for(String euCountry:euCountryList){
												receiveNum+=(receiveFbaTran.get(name+"_"+euCountry)==null?0:receiveFbaTran.get(name+"_"+euCountry));
											}
										}
										if(fbaStock-receiveNum>=psiInventoryFba.getFulfillableQuantity()){
											fbaStock=fbaStock-receiveNum;
										}
									}
								    
								    if(inventorys!=null&&inventorys.getQuantityEuro()!=null&&inventorys.getQuantityEuro().get("DE")!=null&&inventorys.getQuantityEuro().get("DE").getNewQuantity()!=null){
										oversea=inventorys.getQuantityEuro().get("DE").getNewQuantity();
									}
								    daySalesMap=allDaySalesMap.get(name+"_eu");
								    
								    if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).get("eu")!=null){
										safeInventory=MathUtils.roundUp(stockMap.get(name).get("eu").getSafeInventory());
									}
								    dateTranMap=tranMap.get(name+"_eu");
								    
								    Integer totalSal=0;
									for(String euCountry:euCountryList){
										if(sale30Map.get(euCountry)!=null&&sale30Map.get(euCountry).get(name)!=null){
											totalSal+=sale30Map.get(euCountry).get(name);
										}
									}
									day31Sales=MathUtils.roundUp(totalSal/31d);
									
							  }else if(euNoUkSet.contains(name)&&"de".contains(country)){//四国泛欧
								    PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_four");
									if(psiInventoryFba!=null){
										fbaStock=psiInventoryFba.getRealTotal();
										Integer receiveNum=0;
										if(receiveFbaTran!=null&&receiveFbaTran.size()>0){
											for(String euCountry:euCountryList){
												if(!"uk".equals(euCountry)){
													receiveNum+=(receiveFbaTran.get(name+"_"+euCountry)==null?0:receiveFbaTran.get(name+"_"+euCountry));
												}
											}
										}
										if(fbaStock-receiveNum>=psiInventoryFba.getFulfillableQuantity()){
											fbaStock=fbaStock-receiveNum;
										}
									}
									
									for(String cty:euCountryList){
										if(!"uk".equals(cty)){
											if(inventorys!=null&&inventorys.getInventorys()!=null&&inventorys.getInventorys().get(cty)!=null
													&&inventorys.getInventorys().get(cty).getQuantityInventory()!=null&&inventorys.getInventorys().get(cty).getQuantityInventory().get("DE")!=null
													&&inventorys.getInventorys().get(cty).getQuantityInventory().get("DE").getNewQuantity()!=null){
														oversea+=inventorys.getInventorys().get(cty).getQuantityInventory().get("DE").getNewQuantity();
											}
										}
									}
									daySalesMap=allDaySalesMap.get(name+"_euNoUk");
									
									if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).get("eu")!=null){
										safeInventory=MathUtils.roundUp(stockMap.get(name).get("eu").getSafeInventory());
										if(stockMap.get(name).get("uk")!=null){
											safeInventory=safeInventory-MathUtils.roundUp(stockMap.get(name).get("uk").getSafeInventory());
										}
									}
									
									dateTranMap=tranMap.get(name+"_euNoUk");
									
									Integer totalSal=0;
									for(String euCountry:euCountryList){
										if(!"uk".equals(euCountry)){
											if(sale30Map.get(euCountry)!=null&&sale30Map.get(euCountry).get(name)!=null){
												totalSal+=sale30Map.get(euCountry).get(name);
											}
										}
									}
									day31Sales=MathUtils.roundUp(totalSal/31d);
									
							  }else if(("ca,com,jp,mx".contains(country))
									  ||(keyBoardSet.contains(name)&&"de,fr,it,es,uk".contains(country))
									  ||(euNoUkSet.contains(name)&&"uk".contains(country))){
								  
								    PsiInventoryFba psiInventoryFba=amazonStock.get(name+"_"+country);
									if(psiInventoryFba!=null){
										fbaStock=psiInventoryFba.getRealTotal();
										Integer receiveNum=0;
										if(receiveFbaTran!=null&&receiveFbaTran.size()>0){
											receiveNum+=(receiveFbaTran.get(name+"_"+country)==null?0:receiveFbaTran.get(name+"_"+country));
										}
										if(fbaStock-receiveNum>=psiInventoryFba.getFulfillableQuantity()){
											fbaStock=fbaStock-receiveNum;
										}
									}
									String code="";
									if("jp".equals(country)){
										code="JP";
									}else if("com,ca".contains(country)){
										code="US";
									}else if("de,fr,it,es,uk".contains(country)){
										code="DE";
									}
									if(inventorys!=null&&inventorys.getInventorys()!=null&&inventorys.getInventorys().get(country)!=null
											&&inventorys.getInventorys().get(country).getQuantityInventory()!=null&&inventorys.getInventorys().get(country).getQuantityInventory().get(code)!=null
											&&inventorys.getInventorys().get(country).getQuantityInventory().get(code).getNewQuantity()!=null){
											   oversea=inventorys.getInventorys().get(country).getQuantityInventory().get(code).getNewQuantity();
									}
									daySalesMap=allDaySalesMap.get(name+"_"+country);
									
									if(stockMap!=null&&stockMap.get(name)!=null&&stockMap.get(name).get(country)!=null){
										safeInventory=MathUtils.roundUp(stockMap.get(name).get(country).getSafeInventory());
									}
									
									dateTranMap=tranMap.get(name+"_"+country);
									
									Integer totalSal=0;
									if(sale30Map.get(country)!=null&&sale30Map.get(country).get(name)!=null){
										totalSal+=sale30Map.get(country).get(name);
									}
									
									day31Sales=MathUtils.roundUp(totalSal/31d);
							  }
							  if(daySalesMap==null||daySalesMap.size()==0||day31Sales==0){//
								  continue;
							  }
							  salesInfo+="31均日销："+day31Sales+"<br/>";
							  for(Map.Entry<String,Integer> monthMap:daySalesMap.entrySet()){
								  salesInfo+=monthMap.getKey()+"预月销："+monthMap.getValue()+"<br/>";
							  }
							  order.setSalesInfo(salesInfo);
							  order.setFbaStock(fbaStock);
							  order.setOversea(oversea);
							  order.setSafeInventory(safeInventory);
							  
							  
							  
							  //先看FBA库存+海外仓-安全库存可售天
							  Integer sellableQty=fbaStock+oversea-safeInventory;
							  Date fbaDate=today;
							  Integer fbaSalesDay=0;
							  if(sellableQty>0){
								  Date start=today;
								  Date lastDate=DateUtils.getLastDayOfMonth(start);
								  String month=dateFormatMonth.format(start);
								  Integer daySales=0;
								  if(daySalesMap.get(month)!=null){
									  daySales=MathUtils.roundUp(daySalesMap.get(month)*1d/getDaysOfMonth(start));
								  }else{
									  daySales=day31Sales;
								  }
								  int days=(int)DateUtils.spaceDays(start,lastDate)+1;
								  Integer requireQty=days*daySales;
								 
								  if(sellableQty>requireQty){
									  Integer  remainingQty=sellableQty-requireQty;
									  fbaDate=DateUtils.getLastDayOfMonth(start);
									  fbaSalesDay=days;
									  while(remainingQty>0){
										  start=DateUtils.addDays(lastDate,1);
										  lastDate=DateUtils.getLastDayOfMonth(start);
										  month=dateFormatMonth.format(start);
										  if(daySalesMap.get(month)==null){
											  daySales= day31Sales;
										  }else{
											  daySales=MathUtils.roundUp(1d*daySalesMap.get(month)/getDaysOfMonth(start)); 
										  }
										 
										  days=(int)DateUtils.spaceDays(start,lastDate)+1;
										  requireQty=days*daySales;
										  if(remainingQty>requireQty){
											  fbaDate=lastDate;
											  fbaSalesDay+=days;
										  }else{
											  Integer fbaSales=remainingQty/daySales;
											  fbaSalesDay+=fbaSales;
											  fbaDate=DateUtils.addDays(fbaDate,fbaSales); 
										  }
										  remainingQty=remainingQty-requireQty;
									  }
								  }else{
									  fbaSalesDay=sellableQty/daySales;
									  fbaDate=DateUtils.addDays(fbaDate,fbaSalesDay);
								  }
							  }else{
								  Date start=today;
								  String month=dateFormatMonth.format(start);
								  Integer daySales=0;
								  if(daySalesMap.get(month)!=null){
									  daySales=MathUtils.roundUp(1d*daySalesMap.get(month)/getDaysOfMonth(start));
								  }else{
									  daySales=day31Sales;
								  }
								  fbaSalesDay=sellableQty/daySales;
							  }
							  if(!fbaDate.equals(today)){
								  transInfo="FBA+LOCAL-安:"+dateFormatDay.format(fbaDate)+"  FBA可售天:"+fbaSalesDay+"<br/><br/>";
							  }
							  
							  Date beforeDate=fbaDate;
							  if(dateTranMap!=null&&dateTranMap.size()>0){
								    Set<Date> tempDateSet=dateTranMap.keySet();
									List<Date> dateSet=Lists.newArrayList(tempDateSet);
									Collections.sort(dateSet);
									for (Date date: dateSet) {
										Map<String, Integer> tranTypeMap=dateTranMap.get(date);
										String tranDate=dateFormatDay.format(date);
										for(Map.Entry<String,Integer> entry:tranTypeMap.entrySet()){
											    String type=("1".equals(entry.getKey())?"[FBA]":"");
											    Integer quantity=entry.getValue();
											 
											    if(date.before(DateUtils.addDays(today,period+7))){//周采购
													if(beforeDate.after(DateUtils.addDays(today, period+7))){
														transInfo+=tranDate+type+"::"+quantity+"<br/><br/>";
													    continue;	
													}
													if(date.before(beforeDate)||date.equals(beforeDate)){
														Date afterDate=getDate(beforeDate,quantity,dateFormatMonth,daySalesMap,day31Sales);
														transInfo+=tranDate+type+":"+quantity+",可售:"+dateFormatDay.format(beforeDate)+"~"+dateFormatDay.format(afterDate)+"<br/><br/>";
														beforeDate=afterDate;
													}else if(date.after(beforeDate)){
														Integer gap=getGapByDate(beforeDate,date,dateFormatMonth,daySalesMap,day31Sales);
														tip+="运输缺口日期:"+dateFormatDay.format(beforeDate)+"~"+dateFormatDay.format(date)+",数量:"+gap+"<br/><br/>";
														if(date.before(airDate)){
															fillUpTip+="缺货最后日期在空运到达期"+dateFormatDay.format(airDate)+"前,不补货<br/><br/>";
														}else{
															if(beforeDate.before(airDate)){
																 fillUpTip+="缺货开始日期在空运到达期前,"+dateFormatDay.format(beforeDate)+"-"+dateFormatDay.format(airDate)+"不补货<br/><br/>";
																 beforeDate=airDate;
															}
															
															if(date.before(seaDate)){//都是空运缺
																fillUpTip+=distributionQty(order,product.getProducePeriod(),country,"0",seaDays,airDays,poDateList,beforeDate,date,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
															}else{
																if(beforeDate.before(seaDate)&&!"ca".equals(country)){
																	//1 beforeDate-seaDate
																	fillUpTip+=distributionQty(order,product.getProducePeriod(),country,"0",seaDays,airDays,poDateList,beforeDate,seaDate,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
																	//2 seaDate-date
																	fillUpTip+=distributionQty(order,product.getProducePeriod(),country,"1",seaDays,airDays,poDateList,seaDate,date,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
																}else{
																	fillUpTip+=distributionQty(order,product.getProducePeriod(),country,"1",seaDays,airDays,poDateList,beforeDate,date,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
																}
															}
														}/*else if(beforeDate.after(seaDate)){
															Date lastedDate=DateUtils.addDays(beforeDate,-seaDays);
															fillUpTip+="缺货开始日期"+dateFormatDay.format(beforeDate)+"在海运期"+dateFormatDay.format(seaDate)+"外,最迟海运日期"+dateFormatDay.format(lastedDate)+",";
															//判断是否有货 有无空运
															Integer stock=findProductQuantity(lastedDate,namePoMap,cnMap);
															if(stock>=gap){
																String disQty=distributionQty(gap,lastedDate,namePoMap,cnMap,poDateList);
																fillUpTip+=disQty;
															}
														}else{
															 if(beforeDate.before(airDate)){
																 beforeDate=airDate;
																 fillUpTip+="缺货开始日期在空运到达期前,"+dateFormatDay.format(beforeDate)+"-"+dateFormatDay.format(airDate)+"不补货<br/><br/>";
															 }
															 fillUpTip+=distributionQty(gap,DateUtils.addDays(airDate,-7),namePoMap,cnMap,poDateList);
														}*/
														
														Date afterDate=getDate(date,quantity,dateFormatMonth,daySalesMap,day31Sales);
														transInfo+=tranDate+type+":"+quantity+",可售:"+dateFormatDay.format(date)+"~"+dateFormatDay.format(afterDate)+"<br/><br/>";
														beforeDate=afterDate;
													}
												}else{
													transInfo+=tranDate+type+"::"+quantity+"<br/><br/>";
												}
										}
									}
							  }
							  
							  if(beforeDate.before(DateUtils.addDays(today,period+7))){
								  if(beforeDate.before(DateUtils.addDays(today,period))){
									  Date tempDate=DateUtils.addDays(today,period);
									  Integer gap=getGapByDate(beforeDate,tempDate,dateFormatMonth,daySalesMap,day31Sales);
									  if(gap>0){
										  tip+="运输缺口日期:"+dateFormatDay.format(beforeDate)+"~"+dateFormatDay.format(tempDate)+",数量:"+gap+"<br/><br/>";
									  }
									  
									  if(beforeDate.before(airDate)){
											 fillUpTip+="缺货开始日期在空运到达期前,"+dateFormatDay.format(beforeDate)+"-"+dateFormatDay.format(airDate)+"不补货<br/><br/>";
											 beforeDate=airDate;
									  }
									  if(tempDate.before(seaDate)){//都是空运缺
											fillUpTip+=distributionQty(order,product.getProducePeriod(),country,"0",seaDays,airDays,poDateList,beforeDate,tempDate,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
									  }else{
										    if(beforeDate.before(seaDate)&&!"ca".equals(country)){
												fillUpTip+=distributionQty(order,product.getProducePeriod(),country,"0",seaDays,airDays,poDateList,beforeDate,seaDate,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
												beforeDate=seaDate;
												fillUpTip+=distributionQty(order,product.getProducePeriod(),country,"1",seaDays,airDays,poDateList,beforeDate,tempDate,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
											}else{
												fillUpTip+=distributionQty(order,product.getProducePeriod(),country,"1",seaDays,airDays,poDateList,beforeDate,tempDate,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
											}
									  }
									  beforeDate=tempDate;
									  Date endDate=DateUtils.addDays(tempDate,7);
									  if(beforeDate.before(endDate)){
										  Integer poAnalyseGap=getGapByDate(beforeDate,endDate,dateFormatMonth,daySalesMap,day31Sales);
										  tip+="采购缺口日期:"+dateFormatDay.format(beforeDate)+"~"+dateFormatDay.format(endDate)+",数量:"+poAnalyseGap+"<br/><br/>";
										  if("ca".equals(country)){
												fillUpTip+="采购:"+distributionQty(order,product.getProducePeriod(),country,"0",seaDays,airDays,poDateList,beforeDate,endDate,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
										  }else{
												fillUpTip+="采购:"+distributionQty(order,product.getProducePeriod(),country,"1",seaDays,airDays,poDateList,beforeDate,endDate,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
										  }
									  }
								  }else{
									  Date tempDate=DateUtils.addDays(today,period+7);
									  if(beforeDate.before(tempDate)){
										  Integer poAnalyseGap=getGapByDate(beforeDate,tempDate,dateFormatMonth,daySalesMap,day31Sales);
										  tip+="采购缺口日期:"+dateFormatDay.format(beforeDate)+"~"+dateFormatDay.format(tempDate)+",数量:"+poAnalyseGap+"<br/><br/>";
										  if("ca".equals(country)){
												fillUpTip+="采购:"+distributionQty(order,product.getProducePeriod(),country,"0",seaDays,airDays,poDateList,beforeDate,tempDate,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
										  }else{
												fillUpTip+="采购:"+distributionQty(order,product.getProducePeriod(),country,"1",seaDays,airDays,poDateList,beforeDate,tempDate,dateFormatMonth,daySalesMap,day31Sales,namePoMap,cnMap);
										  }
									  }
								  }
							  }
							  order.setTransInfo(transInfo);
							  order.setTip(tip);
							  order.setFillUpTip(fillUpTip);
							  
							  orderList.add(order);
						}
				 }
			 }
			 if(orderList.size()>0){
				 forecastOrderService.save(orderList);
			 }
		}
		
		
		public int getDaysOfMonth(Date date) {  
	        Calendar calendar = Calendar.getInstance();  
	        calendar.setTime(date);  
	        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);  
	    }  
		
		public Date getDate(Date beforeDate,Integer quantity,SimpleDateFormat dateFormatMonth,Map<String,Integer> daySalesMap,Integer day31Sales){
			Date start=beforeDate;
			Date lastDate=DateUtils.getLastDayOfMonth(start);
		    String month=dateFormatMonth.format(start);
			Integer daySales=0;
			if(daySalesMap.get(month)!=null){
				  daySales=MathUtils.roundUp(1d*daySalesMap.get(month)/getDaysOfMonth(start));
			}else{
				  daySales=day31Sales;
			}
			int days=(int)DateUtils.spaceDays(start,lastDate)+1;
			Integer requireQty=days*daySales;
			
			if(quantity>requireQty){
				  Integer  remainingQty=quantity-requireQty;
				  beforeDate=DateUtils.getLastDayOfMonth(start);
				  while(remainingQty>0){
					  start=DateUtils.addDays(lastDate,1);
					  lastDate=DateUtils.getLastDayOfMonth(start);
					  month=dateFormatMonth.format(start);
					  //daySales=daySalesMap.get(month)/getDaysOfMonth(start);
					  if(daySalesMap.get(month)!=null){
						  daySales=MathUtils.roundUp(1d*daySalesMap.get(month))/getDaysOfMonth(start);
					  }else{
						  daySales=day31Sales;
					  }
					  days=(int)DateUtils.spaceDays(start,lastDate)+1;
					  requireQty=days*daySales;
					  if(remainingQty>requireQty){
						  beforeDate=lastDate;
					  }else{
						  Integer fbaSales=remainingQty/daySales;
						  beforeDate=DateUtils.addDays(beforeDate,fbaSales); 
					  }
					  remainingQty=remainingQty-requireQty;
				  }
			 }else{
				  beforeDate=DateUtils.addDays(beforeDate,quantity/daySales);
			 }
			 return beforeDate;
		}
		
		public Integer getGapByDate(Date startDate,Date endDate,SimpleDateFormat dateFormatMonth,Map<String,Integer> daySalesMap,Integer day31Sales){
			Integer quantity=0;
			String startMonth=dateFormatMonth.format(startDate);
			String endMonth=dateFormatMonth.format(endDate);
			if(startMonth.equals(endMonth)){
				Integer daySales=0;
				if(daySalesMap.get(startMonth)!=null){
					  daySales=MathUtils.roundUp(1d*daySalesMap.get(startMonth)/getDaysOfMonth(startDate));
				}else{
					  daySales=day31Sales;
				}
				int days=(int)DateUtils.spaceDays(startDate,endDate)+1;
				return days*daySales;
			}else{
				Date start=startDate;
				Date lastDate=DateUtils.getLastDayOfMonth(start);
				//Integer daySales=daySalesMap.get(startMonth)/getDaysOfMonth(startDate);
				Integer daySales=0;
				if(daySalesMap.get(startMonth)!=null){
					  daySales=MathUtils.roundUp(1d*daySalesMap.get(startMonth)/getDaysOfMonth(startDate));
				}else{
					  daySales=day31Sales;
				}
				int days=(int)DateUtils.spaceDays(start,lastDate)+1;
				quantity=days*daySales;
				
				start=DateUtils.addDays(lastDate, 1);
				startMonth=dateFormatMonth.format(start);
				endMonth=dateFormatMonth.format(endDate);
				while(!startMonth.equals(endMonth)){
					lastDate=DateUtils.getLastDayOfMonth(start);
					//daySales=daySalesMap.get(startMonth)/getDaysOfMonth(start);
				
					if(daySalesMap.get(startMonth)!=null){
						  daySales=MathUtils.roundUp(1d*daySalesMap.get(startMonth)/getDaysOfMonth(start));
					}else{
						  daySales=day31Sales;
					}
					days=(int)DateUtils.spaceDays(start,lastDate)+1;
					quantity+=days*daySales;
					
					start=DateUtils.addDays(lastDate, 1);
					startMonth=dateFormatMonth.format(start);
					endMonth=dateFormatMonth.format(endDate);
				}
				//daySales=daySalesMap.get(startMonth)/getDaysOfMonth(start);
				if(daySalesMap.get(startMonth)!=null){
					  daySales=MathUtils.roundUp(1d*daySalesMap.get(startMonth)/getDaysOfMonth(start));
				}else{
					  daySales=day31Sales;
				}
				days=(int)DateUtils.spaceDays(start,endDate)+1;
				quantity+=days*daySales;
			}
			return quantity;
		}
		
		
		public String distributionGap(PsiTransportForecastAnalyseOrder order,Integer period,String country,SimpleDateFormat dateFormatDay,Date shippingDate,String type,Integer seaDays,Integer airDays,Integer gap,List<Date> poDateList,Date startDate,Date endDate,SimpleDateFormat dateFormatMonth,Map<String,Integer> daySalesMap,Integer daySales,Map<Date,Map<String,Integer>> namePoMap,Map<String,Integer> nameCnMap){
			String info="";
			Date today=new Date();
			String todayStr=dateFormatDay.format(today);
			Integer cnAndPoStock=findProductQuantity(shippingDate,namePoMap,nameCnMap);
			if(cnAndPoStock>=gap){
				info=dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+","+("0".equals(type)||"ca".equals(country)?"空运":"海运")+"最迟发货日期"+dateFormatDay.format(shippingDate)+",PO+CN分配"+gap+",可售至"+dateFormatDay.format(endDate)+"<br/><br/>";
				gap=distributionQty2(gap,shippingDate,namePoMap,nameCnMap,poDateList);
				if(todayStr.equals(dateFormatDay.format(shippingDate))){
					if("0".equals(type)||"ca".equals(country)){
						order.setAirGap(gap+(order.getAirGap()==null?0:order.getAirGap()));
					}else{
						order.setSeaGap(gap+(order.getSeaGap()==null?0:order.getSeaGap()));
					}
				}
				return info;
				//return distributionQty(gap,shippingDate,namePoMap,nameCnMap,poDateList);
			}
			startDate=DateUtils.addDays(startDate, MathUtils.roundDown(cnAndPoStock*1d/daySales));
			if(cnAndPoStock>0){
				info=dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+","+("0".equals(type)||"ca".equals(country)?"空运":"海运")+"最迟发货日期"+dateFormatDay.format(shippingDate)+",PO+CN分配"+cnAndPoStock+",可售至"+dateFormatDay.format(startDate)+"<br/><br/>";
				gap=distributionQty2(gap,shippingDate,namePoMap,nameCnMap,poDateList);
				if(todayStr.equals(dateFormatDay.format(shippingDate))){
					if("0".equals(type)||"ca".equals(country)){
						order.setAirGap(cnAndPoStock+(order.getAirGap()==null?0:order.getAirGap()));
					}else{
						order.setSeaGap(cnAndPoStock+(order.getSeaGap()==null?0:order.getSeaGap()));
					}
				}
			}
			if(namePoMap!=null&&namePoMap.size()>0){
				for(Date poDate:poDateList){
					if(poDate.before(endDate)||poDate.equals(endDate)){
						Map<String,Integer> temp=namePoMap.get(poDate);
						if(temp.get("total")>0){
							Date tempDate=DateUtils.addDays(poDate,seaDays);
							if(tempDate.before(startDate)||tempDate.equals(startDate)){
								
								if("ca".equals(country)){
									info="空运";
								}else if("0".equals(type)){
									info+="空转海";
								}else{
									info="海运";
								}
								
								info+=",最迟发货日期"+dateFormatDay.format(DateUtils.addDays(startDate,-seaDays));
								if(temp.get("total")>=gap){
									info+=dateFormatDay.format(poDate)+"PO分配"+gap+",可售至"+dateFormatDay.format(endDate)+"<br/><br/>";
									startDate=endDate;
									
									if(todayStr.equals(dateFormatDay.format(DateUtils.addDays(startDate,-seaDays)))){
										if("ca".equals(country)){
											order.setAirGap(gap+(order.getAirGap()==null?0:order.getAirGap()));
										}else{
											order.setSeaGap(gap+(order.getSeaGap()==null?0:order.getSeaGap()));
										}
									}
									
								}else{
									startDate=DateUtils.addDays(startDate, MathUtils.roundDown(temp.get("total")*1d/daySales));
									info+=dateFormatDay.format(poDate)+"PO分配"+temp.get("total")+",可售至"+dateFormatDay.format(startDate)+"<br/><br/>";
									
									if(todayStr.equals(dateFormatDay.format(DateUtils.addDays(startDate,-seaDays)))){
										if("ca".equals(country)){
											order.setAirGap(temp.get("total")+(order.getAirGap()==null?0:order.getAirGap()));
										}else{
											order.setSeaGap(temp.get("total")+(order.getSeaGap()==null?0:order.getSeaGap()));
										}
									}
								}
								gap=distributionQty2(gap,poDate,namePoMap,nameCnMap,poDateList);
							}else{
								tempDate=DateUtils.addDays(poDate,airDays);
								if(tempDate.after(startDate)){
									info+=dateFormatDay.format(startDate)+"~"+dateFormatDay.format(tempDate)+"空运前缺货不补<br/><br/>";
									if(endDate.before(tempDate)){
										break;
									}
									info+=dateFormatDay.format(tempDate)+"~"+dateFormatDay.format(endDate)+",";
									startDate=tempDate;
								}else{
									info+=dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+",";
								}
								
								if("ca".equals(country)){
									info="空运,";
								}else if("1".equals(type)){
									info+="海转空,";
								}else{
									info="空运,";
								}
								
								info+="最迟发货日期"+dateFormatDay.format(DateUtils.addDays(poDate,-airDays))+",";
								if(temp.get("total")>=gap){
									info+=dateFormatDay.format(poDate)+"PO分配"+gap+",可售至"+dateFormatDay.format(endDate)+"<br/><br/>";
									startDate=endDate;
									
									if(todayStr.equals(dateFormatDay.format(DateUtils.addDays(poDate,-airDays)))){
										order.setAirGap(gap+(order.getAirGap()==null?0:order.getAirGap()));
									}
								}else{
									startDate=DateUtils.addDays(startDate, MathUtils.roundDown(temp.get("total")*1d/daySales));
									info+=dateFormatDay.format(poDate)+"PO分配"+temp.get("total")+",可售至"+dateFormatDay.format(startDate)+"<br/><br/>";
									
									if(todayStr.equals(dateFormatDay.format(DateUtils.addDays(poDate,-airDays)))){
										order.setAirGap(temp.get("total")+(order.getAirGap()==null?0:order.getAirGap()));
									}
								}
								gap=distributionQty2(gap,poDate,namePoMap,nameCnMap,poDateList);
							}
							if(gap<=0){
								break;
							}
						}
					}else{
						break;
					}
				}
			}
			
			if(gap>0){
				
				if("ca".equals(country)){
					Integer time=period+airDays;
					Date cmpDate=DateUtils.addDays(today,time);
					if(startDate.before(cmpDate)){
						 if(endDate.before(cmpDate)){
							 info+="空运最迟采购日期"+dateFormatDay.format(today)+",采购赶不上缺货日,";
						 }else{
							 info+="空运最迟采购日期"+dateFormatDay.format(today)+",采购只赶上部分缺货数:("+dateFormatDay.format(cmpDate)+"~"+dateFormatDay.format(endDate)+"),";
							 Integer poDays=(int)DateUtils.spaceDays(cmpDate,endDate)+1;
							 order.setPoGap(poDays*daySales+(order.getPoGap()==null?0:order.getAirGap()));
						 }
					}else{
						Date finalDate=DateUtils.addDays(startDate,-time);
						info+="空运最迟采购日期"+dateFormatDay.format(finalDate)+",";
						if(todayStr.equals(dateFormatDay.format(finalDate))){
							 order.setPoGap(gap+(order.getPoGap()==null?0:order.getAirGap()));
						}
					}
					info+="<font color='red'>"+dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+"采购缺货:"+gap+"</font><br/><br/>";
				}else{
					Integer time=period+seaDays;
					Date cmpDate=DateUtils.addDays(today,time);
					if(startDate.before(cmpDate)){
						time=period+airDays;
						cmpDate=DateUtils.addDays(today,time);
						if(startDate.before(cmpDate)){
							 if(endDate.before(cmpDate)){
								 info+="空运最迟采购日期"+dateFormatDay.format(today)+",采购赶不上缺货日,";
							 }else{
								 info+="空运最迟采购日期"+dateFormatDay.format(today)+",采购只赶上部分缺货数:("+dateFormatDay.format(cmpDate)+"~"+dateFormatDay.format(endDate)+"),";
								 Integer poDays=(int)DateUtils.spaceDays(cmpDate,endDate)+1;
								 order.setPoGap(poDays*daySales+(order.getPoGap()==null?0:order.getAirGap()));
							 }
						}else{
							Date finalDate=DateUtils.addDays(startDate,-time);
							info+="空运最迟采购日期"+dateFormatDay.format(finalDate)+",";
							if(todayStr.equals(dateFormatDay.format(finalDate))){
								 order.setPoGap(gap+(order.getPoGap()==null?0:order.getAirGap()));
							}
						}
					}else{
						Date finalDate=DateUtils.addDays(startDate,-time);
						info+="海运最迟采购日期"+dateFormatDay.format(finalDate)+",";
						if(todayStr.equals(dateFormatDay.format(finalDate))){
							 order.setPoGap(gap+(order.getPoGap()==null?0:order.getAirGap()));
						}
					}
					info+="<font color='red'>"+dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+"采购缺货:"+gap+"</font><br/><br/>";
				}
			}
			return info;
		}
		
		public String distributionQty(PsiTransportForecastAnalyseOrder order,Integer period,String country,String type,Integer seaDays,Integer airDays,List<Date> poDateList,Date startDate,Date endDate,SimpleDateFormat dateFormatMonth,Map<String,Integer> daySalesMap,Integer day31Sales,Map<Date,Map<String,Integer>> namePoMap,Map<String,Integer> nameCnMap){
			Integer quantity=0;
			String startMonth=dateFormatMonth.format(startDate);
			String endMonth=dateFormatMonth.format(endDate);
			SimpleDateFormat dateFormatDay=new SimpleDateFormat("yyyyMMdd");
			Date shippingDate=null;
			Date today=new Date();
			String todayStr=dateFormatDay.format(today);
			if("0".equals(type)||"ca".equals(country)){
				shippingDate=DateUtils.addDays(startDate,-airDays);
			}else{
				shippingDate=DateUtils.addDays(startDate,-seaDays);
			}
			String info="";
			Integer cnAndPoStock=findProductQuantity(shippingDate,namePoMap,nameCnMap);
			if(startMonth.equals(endMonth)){
				Integer daySales=0;
				if(daySalesMap.get(startMonth)!=null){
					  daySales=MathUtils.roundUp(1d*daySalesMap.get(startMonth)/getDaysOfMonth(startDate));
				}else{
					  daySales=day31Sales;
				}
				Integer days=(int)DateUtils.spaceDays(startDate,endDate)+1;
				Integer gap=days*daySales;
				if(cnAndPoStock>=gap){
					info=dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+","+("0".equals(type)||"ca".equals(country)?"空运":"海运")+"最迟发货日期"+dateFormatDay.format(shippingDate)+",PO+CN分配"+gap+",可售至"+dateFormatDay.format(endDate)+"<br/><br/>";
					distributionQty2(gap,shippingDate,namePoMap,nameCnMap,poDateList);
					if(todayStr.equals(dateFormatDay.format(shippingDate))){
						if("0".equals(type)||"ca".equals(country)){
							order.setAirGap(gap+(order.getAirGap()==null?0:order.getAirGap()));
						}else{
							order.setSeaGap(gap+(order.getSeaGap()==null?0:order.getSeaGap()));
						}
					}
					return info;
					//return distributionQty(gap,shippingDate,namePoMap,nameCnMap,poDateList);
				}
				startDate=DateUtils.addDays(startDate, MathUtils.roundDown(cnAndPoStock*1d/daySales));
				if(cnAndPoStock>0){
					info=dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+","+("0".equals(type)||"ca".equals(country)?"空运":"海运")+"最迟发货日期"+dateFormatDay.format(shippingDate)+",PO+CN分配"+cnAndPoStock+",可售至"+dateFormatDay.format(startDate)+"<br/><br/>";
					gap=distributionQty2(gap,shippingDate,namePoMap,nameCnMap,poDateList);
					if(todayStr.equals(dateFormatDay.format(shippingDate))){
						if("0".equals(type)||"ca".equals(country)){
							order.setAirGap(cnAndPoStock+(order.getAirGap()==null?0:order.getAirGap()));
						}else{
							order.setSeaGap(cnAndPoStock+(order.getSeaGap()==null?0:order.getSeaGap()));
						}
					}
				}
				
				if(namePoMap!=null&&namePoMap.size()>0){
					for(Date poDate:poDateList){
						if(poDate.before(endDate)||poDate.equals(endDate)){
							Map<String,Integer> temp=namePoMap.get(poDate);
							if(temp.get("total")>0){
								Date tempDate=DateUtils.addDays(poDate,seaDays);
								if(tempDate.before(startDate)||tempDate.equals(startDate)){
									if("ca".equals(country)){
										info="空运";
									}else if("0".equals(type)){
										info+="空转海";
									}else{
										info="海运";
									}
									info+=",最迟发货日期"+dateFormatDay.format(DateUtils.addDays(startDate,-seaDays));
									if(temp.get("total")>=gap){
										info+=dateFormatDay.format(poDate)+"PO分配"+gap+",可售至"+dateFormatDay.format(endDate)+"<br/><br/>";
										startDate=endDate;
										
										if(todayStr.equals(dateFormatDay.format(DateUtils.addDays(startDate,-seaDays)))){
											if("ca".equals(country)){
												order.setAirGap(gap+(order.getAirGap()==null?0:order.getAirGap()));
											}else{
												order.setSeaGap(gap+(order.getSeaGap()==null?0:order.getSeaGap()));
											}
										}
										
									}else{
										startDate=DateUtils.addDays(startDate, MathUtils.roundDown(temp.get("total")*1d/daySales));
										info+=dateFormatDay.format(poDate)+"PO分配"+temp.get("total")+",可售至"+dateFormatDay.format(startDate)+"<br/><br/>";
										
										if(todayStr.equals(dateFormatDay.format(DateUtils.addDays(startDate,-seaDays)))){
											if("ca".equals(country)){
												order.setAirGap(temp.get("total")+(order.getAirGap()==null?0:order.getAirGap()));
											}else{
												order.setSeaGap(temp.get("total")+(order.getSeaGap()==null?0:order.getSeaGap()));
											}
										}
									}
									
									gap=distributionQty2(gap,poDate,namePoMap,nameCnMap,poDateList);
								}else{
									tempDate=DateUtils.addDays(poDate,airDays);
									if(tempDate.after(startDate)){
										info+=dateFormatDay.format(startDate)+"~"+dateFormatDay.format(tempDate)+"空运前缺货不补<br/><br/>";
										if(endDate.before(tempDate)){
											break;
										}
										info+=dateFormatDay.format(tempDate)+"~"+dateFormatDay.format(endDate)+",";
										startDate=tempDate;
									}else{
										info+=dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+",";
									}
									if("ca".equals(country)){
										info="空运";
									}else if("1".equals(type)){
										info+="海转空,";
									}else{
										info="空运";
									}
									
									info+="最迟发货日期"+dateFormatDay.format(DateUtils.addDays(poDate,-airDays))+",";
									if(temp.get("total")>=gap){
										info+=dateFormatDay.format(poDate)+"PO分配"+gap+",可售至"+dateFormatDay.format(endDate)+"<br/><br/>";
										startDate=endDate;
										
										if(todayStr.equals(dateFormatDay.format(DateUtils.addDays(poDate,-airDays)))){
											order.setAirGap(gap+(order.getAirGap()==null?0:order.getAirGap()));
										}
										
									}else{
										startDate=DateUtils.addDays(startDate, MathUtils.roundDown(temp.get("total")*1d/daySales));
										info+=dateFormatDay.format(poDate)+"PO分配"+temp.get("total")+",可售至"+dateFormatDay.format(startDate)+"<br/><br/>";
										
										if(todayStr.equals(dateFormatDay.format(DateUtils.addDays(poDate,-airDays)))){
											order.setAirGap(temp.get("total")+(order.getAirGap()==null?0:order.getAirGap()));
										}
									}
									gap=distributionQty2(gap,poDate,namePoMap,nameCnMap,poDateList);
								}
								if(gap<=0){
									break;
								}
							}
						}else{
							break;
						}
					}
				}
				if(gap>0){
				
					if("ca".equals(country)){
						Integer time=period+airDays;
						Date cmpDate=DateUtils.addDays(today,time);
						if(startDate.before(cmpDate)){
							 if(endDate.before(cmpDate)){
								 info+="空运最迟采购日期"+dateFormatDay.format(today)+",采购赶不上缺货日,";
							 }else{
								 info+="空运最迟采购日期"+dateFormatDay.format(today)+",采购只赶上部分缺货数:("+dateFormatDay.format(cmpDate)+"~"+dateFormatDay.format(endDate)+"),";
								 Integer poDays=(int)DateUtils.spaceDays(cmpDate,endDate)+1;
								 order.setPoGap(poDays*daySales+(order.getPoGap()==null?0:order.getAirGap()));
							 }
						}else{
							Date finalDate=DateUtils.addDays(startDate,-time);
							info+="空运最迟采购日期"+dateFormatDay.format(finalDate)+",";
							if(todayStr.equals(dateFormatDay.format(finalDate))){
								 order.setPoGap(gap+(order.getPoGap()==null?0:order.getAirGap()));
							}
						}
						info+="<font color='red'>"+dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+"采购缺货:"+gap+"</font><br/><br/>";
					}else{
						Integer time=period+seaDays;
						Date cmpDate=DateUtils.addDays(today,time);
						if(startDate.before(cmpDate)){
							time=period+airDays;
							cmpDate=DateUtils.addDays(today,time);
							if(startDate.before(cmpDate)){
								 if(endDate.before(cmpDate)){
									 info+="空运最迟采购日期"+dateFormatDay.format(today)+",采购赶不上缺货日,";
								 }else{
									 info+="空运最迟采购日期"+dateFormatDay.format(today)+",采购只赶上部分缺货数:("+dateFormatDay.format(cmpDate)+"~"+dateFormatDay.format(endDate)+"),";
									 Integer poDays=(int)DateUtils.spaceDays(cmpDate,endDate)+1;
									 order.setPoGap(poDays*daySales+(order.getPoGap()==null?0:order.getAirGap()));
								 }
							}else{
								Date finalDate=DateUtils.addDays(startDate,-time);
								info+="空运最迟采购日期"+dateFormatDay.format(finalDate)+",";
								if(todayStr.equals(dateFormatDay.format(finalDate))){
									 order.setPoGap(gap+(order.getPoGap()==null?0:order.getAirGap()));
								}
							}
						}else{
							Date finalDate=DateUtils.addDays(startDate,-time);
							info+="海运最迟采购日期"+dateFormatDay.format(finalDate)+",";
							if(todayStr.equals(dateFormatDay.format(finalDate))){
								 order.setPoGap(gap+(order.getPoGap()==null?0:order.getAirGap()));
							}
						}
						info+="<font color='red'>"+dateFormatDay.format(startDate)+"~"+dateFormatDay.format(endDate)+"采购缺货:"+gap+"</font><br/><br/>";
					}
				}
			}else{
				Date start=startDate;
				Date lastDate=DateUtils.getLastDayOfMonth(startDate);
				//Integer daySales=daySalesMap.get(startMonth)/getDaysOfMonth(startDate);
				Integer daySales=0;
				if(daySalesMap.get(startMonth)!=null){
					daySales=MathUtils.roundUp(1d*daySalesMap.get(startMonth)/getDaysOfMonth(startDate));
				}else{
					daySales=day31Sales;
				}
				int days=(int)DateUtils.spaceDays(start,lastDate)+1;
				quantity=days*daySales;
				
				info+=distributionGap(order,period,country,dateFormatDay,shippingDate,type,seaDays,airDays,days*daySales,poDateList,startDate,lastDate,dateFormatMonth,daySalesMap,daySales,namePoMap,nameCnMap);
				
				start=DateUtils.addDays(lastDate, 1);
				startMonth=dateFormatMonth.format(start);
				endMonth=dateFormatMonth.format(endDate);
				while(!startMonth.equals(endMonth)){
					lastDate=DateUtils.getLastDayOfMonth(start);
					//daySales=daySalesMap.get(startMonth)/getDaysOfMonth(start);
				
					if(daySalesMap.get(startMonth)!=null){
						  daySales=MathUtils.roundUp(1d*daySalesMap.get(startMonth)/getDaysOfMonth(start));
					}else{
						  daySales=day31Sales;
					}
					days=(int)DateUtils.spaceDays(start,lastDate)+1;
					quantity+=days*daySales;
					
					if("0".equals(type)||"ca".equals(country)){
						shippingDate=DateUtils.addDays(start,-airDays);
					}else{
						shippingDate=DateUtils.addDays(start,-seaDays);
					}
					
					info+=distributionGap(order,period,country,dateFormatDay,shippingDate,type,seaDays,airDays,days*daySales,poDateList,start,lastDate,dateFormatMonth,daySalesMap,daySales,namePoMap,nameCnMap);
					
					start=DateUtils.addDays(lastDate, 1);
					startMonth=dateFormatMonth.format(start);
					endMonth=dateFormatMonth.format(endDate);
				}
				//daySales=daySalesMap.get(startMonth)/getDaysOfMonth(start);
				if(daySalesMap.get(startMonth)!=null){
					  daySales=MathUtils.roundUp(1d*daySalesMap.get(startMonth)/getDaysOfMonth(start));
				}else{
					  daySales=day31Sales;
				}
				days=(int)DateUtils.spaceDays(start,endDate)+1;
				quantity+=days*daySales;
				
				if("0".equals(type)||"ca".equals(country)){
					shippingDate=DateUtils.addDays(start,-airDays);
				}else{
					shippingDate=DateUtils.addDays(start,-seaDays);
				}
				
				info+=distributionGap(order,period,country,dateFormatDay,shippingDate,type,seaDays,airDays,days*daySales,poDateList,start,endDate,dateFormatMonth,daySalesMap,daySales,namePoMap,nameCnMap);
				
			}
			return info;
		}
		
		//<=date库存
		public Integer findProductQuantity(Date date,Map<Date,Map<String,Integer>> namePoMap,Map<String,Integer> nameCnMap){
			Integer quantity=0;
			if(nameCnMap!=null&&nameCnMap.get("total")!=null&&nameCnMap.get("total")>0){
				quantity=nameCnMap.get("total");
			}
			if(namePoMap!=null){
				for(Date poDate:namePoMap.keySet()){
					if(poDate.before(date)||poDate.equals(date)){
						Map<String,Integer> temp=namePoMap.get(poDate);
						if(temp.get("total")>0){
							quantity+=temp.get("total");
						}
					}
				}
			}
			
			return quantity;
		}
		
		
		public Integer findProductQuantity(Map<Date,Map<String,Integer>> namePoMap,Map<String,Integer> nameCnMap){
			Integer quantity=0;
			if(nameCnMap!=null&&nameCnMap.get("total")!=null&&nameCnMap.get("total")>0){
				quantity=nameCnMap.get("total");
			}
			if(namePoMap!=null){
				for(Date poDate:namePoMap.keySet()){
						Map<String,Integer> temp=namePoMap.get(poDate);
						if(temp.get("total")>0){
							quantity+=temp.get("total");
						}
				}
			}
			return quantity;
		}
		
		
		public String distributionQty(Integer gap,Date date,Map<Date,Map<String,Integer>> namePoMap,Map<String,Integer> nameCnMap,List<Date> poDateList){
			Integer quantity=0;
			if(nameCnMap!=null&&nameCnMap.get("total")!=null&&nameCnMap.get("total")>0){
				quantity=nameCnMap.get("total");
			}
			if(quantity>0){
				if(quantity>=gap){//中国仓数量满足缺口
					Integer countQty=0;
					for(Map.Entry<String,Integer> entry:nameCnMap.entrySet()){
						  String key=entry.getKey();
						  if(!"total".equals(key)){
							  Integer skuQty=entry.getValue();
							  if(skuQty>0){
								  countQty+=skuQty;//10 3       use 1 r 2 
								  if(countQty>=gap){//13 11 
									  nameCnMap.put(key,countQty-gap);
									  break;
								  }else{
									  nameCnMap.put(key,0);
								  }
							  }
						  }
					}
					nameCnMap.put("total",quantity-gap);
					return "中国仓分配缺口数"+gap+"<br/><br/>";
				}else{
					for(Map.Entry<String,Integer> entry:nameCnMap.entrySet()){
						nameCnMap.put(entry.getKey(),0);
					}
				}
			}
			String info="";
			if(quantity>0){
				info="中国仓分配缺口数"+quantity;
			}
			gap=gap-quantity;
			Integer countQty=0;
			boolean flag=false;
			for(Date poDate:poDateList){
				if(poDate.before(date)||poDate.equals(date)){
					Map<String,Integer> temp=namePoMap.get(poDate);
					Integer useQty=0;
					for(Map.Entry<String,Integer> entry:temp.entrySet()){
						String key=entry.getKey();
						if(!"total".equals(key)){
							Integer skuQty=entry.getValue();
							if(skuQty>0){
								   countQty+=skuQty;
								  if(countQty>=gap){
									  useQty+=(gap-countQty+skuQty);
									  temp.put(key,countQty-gap);
									  flag=true;
									  break;
								  }else{
									  temp.put(key,0);
									  useQty+=skuQty;
								  } 
							}
						}
					}
				    temp.put("total",temp.get("total")-useQty);
				    if(flag){
				    	break;
				    }
				}	
			}
			if(flag){
				if(StringUtils.isNotBlank(info)){
					info+=info+",PO分配缺口数"+gap+"<br/><br/>";
				}else{
					info+="PO分配缺口数"+gap+"<br/><br/>";
				}
				return info;
			}
			if(countQty>0){
				info+=",PO分配缺口数"+countQty;
			}
			if(gap-countQty>0){
				info+=",还剩缺口没有库存分配"+(gap-countQty);
			}
			return  info;
		}
		
		
		public Integer distributionQty2(Integer gap,Date date,Map<Date,Map<String,Integer>> namePoMap,Map<String,Integer> nameCnMap,List<Date> poDateList){
			Integer quantity=0;
			if(nameCnMap!=null&&nameCnMap.get("total")!=null&&nameCnMap.get("total")>0){
				quantity=nameCnMap.get("total");
			}
			if(quantity>0){
				if(quantity>=gap){//中国仓数量满足缺口
					Integer countQty=0;
					for(Map.Entry<String,Integer> entry:nameCnMap.entrySet()){
						  String key=entry.getKey();
						  if(!"total".equals(key)){
							  Integer skuQty=entry.getValue();
							  if(skuQty>0){
								  countQty+=skuQty;//10 3       use 1 r 2 
								  if(countQty>=gap){//13 11 
									  nameCnMap.put(key,countQty-gap);
									  break;
								  }else{
									  nameCnMap.put(key,0);
								  }
							  }
						  }
					}
					nameCnMap.put("total",quantity-gap);
				}else{
					for(Map.Entry<String,Integer> entry:nameCnMap.entrySet()){
						nameCnMap.put(entry.getKey(),0);
					}
				}
			}
			gap=gap-quantity;
			Integer countQty=0;
			boolean flag=false;
			for(Date poDate:poDateList){
				if(poDate.before(date)||poDate.equals(date)){
					Map<String,Integer> temp=namePoMap.get(poDate);
					Integer useQty=0;
					for(Map.Entry<String,Integer> entry:temp.entrySet()){
						String key=entry.getKey();
						if(!"total".equals(key)){
							Integer skuQty=entry.getValue();
							if(skuQty>0){
								   countQty+=skuQty;
								  if(countQty>=gap){
									  useQty+=(gap-countQty+skuQty);
									  temp.put(key,countQty-gap);
									  flag=true;
									  break;
								  }else{
									  temp.put(key,0);
									  useQty+=skuQty;
								  } 
							}
						}
					}
				    temp.put("total",temp.get("total")-useQty);
				    if(flag){
				    	break;
				    }
				}	
			}
			return gap-countQty;
		}
		
}
