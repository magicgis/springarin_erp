package com.springrain.erp.modules.amazoninfo.scheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonProduct2Dao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProduct2;
import com.springrain.erp.modules.psi.service.PsiInventoryFbaService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.sys.service.DictService;

public class OverStockMonitor {
	@Autowired
	private PsiProductService 		productService;
	@Autowired
	private PsiInventoryFbaService  fbaService;
	@Autowired
	private MailManager 			mailManager;
	@Autowired
	private DictService             dictService;
	@Autowired
	private AmazonProduct2Dao       amazonProduct2Dao;
	
	private  final Logger LOGGER = LoggerFactory.getLogger(getClass());
	/**
	 *处理库存预警 
	 * @throws ParseException 
	 * 
	 */
	
	
	public void overStock() throws ParseException{
		//Map<String,List<String>> refOutOfMap=Maps.newHashMap();
		//Map<String,List<String>> refOverMap=Maps.newHashMap();
		//Map<String,Map<String,List<String>>> refPreOutOfMap=Maps.newHashMap();
		//this.fbaService.getOverAndOutOfStock(refOutOfMap, refOverMap, refPreOutOfMap);
	}
	
	
	public void overStock1(){
		try {
			Map<String,String> skuMap = this.productService.getBandingSkuProduct();
			//不销售的sku
			List<String> noSaleSku=null;// this.fbaService.getNoSaleSku();
			//不销售的sku和新品
			List<String> noSaleAndNewSku= this.fbaService.getNoSaleAndNewSku();
			Set<String> noSaleAndNewSets = Sets.newHashSet();
			noSaleAndNewSets.addAll(noSaleAndNewSku);
			//查询30天销量  key:sku
			Map<String,Integer> sale30Map = this.fbaService.get31DaysSales(noSaleSku);
			//查询所有的fba库存 key:sku+国家 key:fba实+在途+fba总
			Map<String,String> fbaMap = this.fbaService.getFbaInventroy(noSaleSku,null);
			
			//即将断货 : 总数/日均销<15 并且transit数量=0 
			Map<String,List<String>> preOutOfMap = this.preOutOfStock(sale30Map, fbaMap);
			
			//排除新品
			Map<String,String> tempFbaMap = Maps.newHashMap();
			for (Map.Entry<String,String> entry : fbaMap.entrySet()) {  
			    String skuCountry=entry.getKey();
				if(!noSaleAndNewSets.contains(skuCountry.split(",")[0].toString())){
					tempFbaMap.put(skuCountry,entry.getValue());
				}
			}
			
			Map<String,Integer> tempSale30Map = Maps.newHashMap();
			for (Map.Entry<String,Integer> entry : sale30Map.entrySet()) { 
			    String sku=entry.getKey();
				if(!noSaleAndNewSets.contains(sku)){
					tempSale30Map.put(sku, entry.getValue());
				}
			}
			  
			//遍历amazon2表
			List<AmazonProduct2>  amaList = this.amazonProduct2Dao.findAll();
			//目前还在使用的sku
			Map<String,String>  usedSkuMap = Maps.newHashMap();
			//asin对应的sku及国家价格
			Map<String,Map<String,List<String>>> asinMap = Maps.newHashMap();
			for(AmazonProduct2 pro:amaList){
				String sku 		= pro.getSku();
				String asin 	= pro.getAsin();
				String active 	= pro.getActive();
				String country = pro.getCountry();
				String price =pro.getSalePrice()==null?" ":(pro.getSalePrice()+" ");
				if("1".equals(active)){
					if(usedSkuMap.get(sku)==null){
						usedSkuMap.put(sku, asin);
					}
					Map<String,List<String>> innerSkuMap =null;
					if(asinMap.get(asin)==null){
						innerSkuMap =Maps.newHashMap();
					}else{
						innerSkuMap=asinMap.get(asin);
					}
					
					List<String> innerList=null;
					if(innerSkuMap.get(sku)==null){
						innerList=Lists.newArrayList();
					}else{
						innerList=innerSkuMap.get(sku);
					}
					String countryPrice =country+";"+price;
					innerList.add(countryPrice);
					innerSkuMap.put(sku, innerList);
					asinMap.put(asin, innerSkuMap);
				}
			}
			
			//处理积压、断货
			Map<String,List<String>> overMap = this.overStock(tempSale30Map,tempFbaMap);//淘汰新品都不算  积压
			Map<String,List<String>> outOfMap = Maps.newHashMap();//this.fbaService.getOutOfStockSku(noSaleSku);
			
			
			
			//查询断货的时间段
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			Date endDate = sdf.parse(sdf.format(new Date()));
			Date startDate = DateUtils.addMonths(endDate, -2);
			Map<String,String> dateMap= Maps.newHashMap();
			if(outOfMap!=null&&outOfMap.size()>0){
				//获取所有断货sku
				Set<String> outSku = Sets.newHashSet();
				for (Map.Entry<String,List<String>> entry : outOfMap.entrySet()) { 
					outSku.addAll(entry.getValue());
				}
				dateMap=this.fbaService.getOutOfStockMail(startDate,endDate,outSku);
			}
			
			List<String> countrys = dictService.findByType("platform");
			StringBuffer contents= new StringBuffer("");
			
			for(String country:countrys){
				//积压
				contents.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '>");
				
					if(overMap.get(country)!=null){
						List<String> overSkus = Lists.newArrayList();
						//只要匹配上的，即目前使用的
						for(int i =0;i<overMap.get(country).size();i++){
							String sku = overMap.get(country).get(i);
							if(skuMap.get(sku)!=null){
								overSkus.add(sku);
							}
						}
						contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3;'><td colspan='8'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country).toUpperCase()+"积压详情</span></td></tr>");
						contents.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th style='width:10%'>序号</th><th style='width:15%'>产品名</th><th style='width:10%'>fba库存</th><th style='width:15%'>30天销量</th><th style='width:10%'>序号</th><th style='width:15%'>产品名</th><th style='width:10%'>fba库存</th><th style='width:15%'>30天销量</th></tr>");
						for(int i =0;i<overSkus.size();i++){
							String sku = overSkus.get(i);
							String skuKey=sku+","+country;
							String name=skuMap.get(sku)==null?sku+"(没匹配)":skuMap.get(sku).split(",")[0];
							String sales30=sale30Map.get(sku)!=null?sale30Map.get(sku)+"":"";
							String fbaQuantiy=fbaMap.get(skuKey).split(",")[0]+"";
							int index = i+1;
							if(index%2==1){
								contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '><td>"+index+"</td><td>"+name+"</td><td>"+fbaQuantiy+"</td><td>"+sales30+"</td>");
							}
							if(index%2==0||index==overMap.get(country).size()){
								if(index%2==1){
									contents.append("<td></td><td></td><td></td><td></td></tr>");
								}else{
									contents.append("<td>"+index+"</td><td>"+name+"</td><td>"+fbaQuantiy+"</td><td>"+sales30+"</td></tr>");
								}
							}
						}
				}
				
				if(outOfMap.get(country)!=null){
					List<String> outOfSkus = Lists.newArrayList();
					//只要匹配上的，即目前使用的
					for(int i =0;i<outOfMap.get(country).size();i++){
						String sku = outOfMap.get(country).get(i);
						//sku已绑定并且国家相等
						if(usedSkuMap.keySet().contains(sku)){
							outOfSkus.add(sku);
						}
							
					}
					//断货
					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#FA8072;'><td colspan='8'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country).toUpperCase()+"断货详情</span></td></tr>");
					contents.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>序号</th><th>产品名</th><th>断货天数</th><th>断货日</th><th>序号</th><th>产品名</th><th>断货天数</th><th>断货日</th></tr>");
					for(int i =0;i<outOfSkus.size();i++){
						String sku = outOfSkus.get(i);
						String name=skuMap.get(sku)==null?sku+"(没匹配)":skuMap.get(sku).split(",")[0];
						String outDateStr  = dateMap.get(sku);
						long quantum = 1;
						//根据时间段算出断货时间
						if(outDateStr.split("-").length>1){
							quantum=DateUtils.spaceDays(sdf.parse(outDateStr.split("-")[0]), sdf.parse(outDateStr.split("-")[1]))+1;
						}
						int index = i+1;
						if(index%2==1){
							contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '><td>"+index+"</td><td>"+name+"</td><td>"+quantum+"</td><td>"+outDateStr.split("-")[0]+"</td>");
						}
						if(index%2==0||index==outOfMap.get(country).size()){
							if(index%2==1){
								contents.append("<td></td><td></td><td></td><td></td></tr>");
							}else{
								contents.append("<td>"+index+"</td><td>"+name+"</td><td>"+quantum+"</td><td>"+outDateStr.split("-")[0]+"</td></tr>");
							}
						}
					}
				}   
				
				
				if(preOutOfMap.get(country)!=null){
					//即将断货
					List<String> preOutOfSkus = Lists.newArrayList();
					//只要匹配上的，即目前使用的
					for(int i =0;i<preOutOfMap.get(country).size();i++){
						String sku = preOutOfMap.get(country).get(i);
						if(usedSkuMap.keySet().contains(sku)){
							preOutOfSkus.add(sku);
						}
					}
					if(preOutOfSkus.size()>0){
						contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#9ACD32'><td colspan='8'><span style='font-weight: bold;font-size:25px'>"+("com".equals(country)?"us":country).toUpperCase()+"即将断货详情</span><span style='font-weight: bold;font-size:15px'>(排除已有在途)</span></td></tr>");
						contents.append("<tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#96FED1; '><th>序号</th><th>产品名</th><th>库存数</th><th>可售天数</th><th>序号</th><th>产品名</th><th>库存数</th><th>可售天数</th></tr>");
						for(int i =0;i<preOutOfSkus.size();i++){
							String sku = preOutOfSkus.get(i);
							String asin =usedSkuMap.get(sku);
							Map<String,List<String>> tempSkuMap=asinMap.get(asin);
							StringBuffer buf= new StringBuffer();
							for(String tempSku:tempSkuMap.keySet()){
								if(!tempSku.equals(sku)){
									for(String priceInfo:tempSkuMap.get(sku)){
										String arr[] = priceInfo.split(";");
										buf.append(tempSku+","+arr[0]+","+arr[1]);
									}
									buf.append("<br/>");
								}
							}
							String name=skuMap.get(sku)==null?sku+"(没匹配)":skuMap.get(sku).split(",")[0];
							String skuKey=sku+","+country;
							String sales30=sale30Map.get(sku)!=null?sale30Map.get(sku)+"":"";
							String fbaQuantiy=fbaMap.get(skuKey).split(",")[0]+"";
							String tranQuantity =fbaMap.get(skuKey).split(",")[1]+"";
							Integer canSaleDays = Integer.parseInt(fbaQuantiy)*30/Integer.parseInt(sales30);
							int index = i+1;
							if(index%2==1){
								contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px;  border:1px solid #cad9ea; padding:0 1em 0;background-color:#f5fafe; '><td>"+index+"</td><td>"+name+"</td><td>"+fbaQuantiy+"</td><td>"+canSaleDays+"</td>");
							}
							if(index%2==0||index==preOutOfMap.get(country).size()){
								if(index%2==1){
									contents.append("<td></td><td></td><td></td><td></td></tr>");
								}else{
									contents.append("<td>"+tranQuantity+"</td><td>"+name+"</td><td>"+fbaQuantiy+"</td><td>"+buf.toString()+"</td></tr>");
								}
							}
						}
					}
				}   
				contents.append("</table><br/><br/><br/>");
			}
			
			//发信
			if(StringUtils.isNotEmpty(contents)){
				Date date = new Date();
				//发信给相关人员
				String toAddress="amazon-sales@inateck.com,supply-chain@inateck.com,maik@inateck.com,tim@inateck.com";
				final MailInfo mailInfo = new MailInfo(toAddress,"产品积压断货"+DateUtils.getDate("-yyyy/M/dd"),date);
				mailInfo.setContent(contents.toString());
				new Thread(){
					public void run(){
						 mailManager.send(mailInfo);
					}
				}.start();
				
			}
			LOGGER.info("积压断货预警扫描结束！");
		} catch (Exception e) {
			LOGGER.error("积压断货预警扫描异常结束！");
			e.printStackTrace();
		}
	}
	
	
	//fba库存积压
	public Map<String,List<String>> overStock(Map<String,Integer> sale30Map,Map<String,String> fbaMap){
		Map<String,List<String>> overMap = Maps.newHashMap();
		Integer type =3;
		//获取积压的sku
		for (Map.Entry<String,String> entry : fbaMap.entrySet()) { 
		    String skuKey=entry.getKey();
			Integer fullQuantity = Integer.parseInt(entry.getValue().split(",")[0]);
			if(fullQuantity.equals(0)){
				continue;
			}
			String sku = skuKey.split(",")[0];
			String country = skuKey.split(",")[1];
			//如果30天销售没有这个    或者库存数为0   或者fba库存/30天销售>3
			if(sale30Map.get(sku)==null||sale30Map.get(sku).equals(0)||fullQuantity/sale30Map.get(sku)>type){
				List<String> skus = Lists.newArrayList();
				if(overMap.get(country)!=null){
					skus= overMap.get(country);
				}
				skus.add(sku);
				overMap.put(country, skus);
			}
		}
		return overMap;
	}
	
	//即将断货
	public Map<String,List<String>> preOutOfStock(Map<String,Integer> sale30Map,Map<String,String> fbaMap){
		Map<String,List<String>> preOutOfMap = Maps.newHashMap();
		//获取积压的sku
		for (Map.Entry<String,String> entry : fbaMap.entrySet()) { 
		    String skuKey=entry.getKey();
		    String quantityValue=entry.getValue();
			String sku = skuKey.split(",")[0];
			String country = skuKey.split(",")[1];
			Integer fullQuantity = Integer.parseInt(quantityValue.split(",")[0]);
			Integer totalQuantity = Integer.parseInt(quantityValue.split(",")[2]);
			if(fullQuantity.equals(0)||sale30Map.get(sku)==null||sale30Map.get(sku).equals(0)){
				continue;
			}
			
			//如果fba实/日均销<20,并且fba总/日均销<30 或者fba实/日均销<10
			if((fullQuantity*30/sale30Map.get(sku))<=20){
				if((totalQuantity*30/sale30Map.get(sku))<=30||(fullQuantity*30/sale30Map.get(sku))<=10){
					List<String> skus = Lists.newArrayList();
					if(preOutOfMap.get(country)!=null){
						skus= preOutOfMap.get(country);
					}
					skus.add(sku);
					preOutOfMap.put(country, skus);
				}
			}
		}
		return preOutOfMap;
	}
		
}
