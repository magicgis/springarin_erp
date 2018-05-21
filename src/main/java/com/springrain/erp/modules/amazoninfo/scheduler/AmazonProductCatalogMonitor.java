package com.springrain.erp.modules.amazoninfo.scheduler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.utils.HttpRequest;
import com.springrain.erp.modules.amazoninfo.entity.AmazonCatalogRank;
import com.springrain.erp.modules.amazoninfo.entity.AmazonKeyword;
import com.springrain.erp.modules.amazoninfo.entity.AmazonKeywordSearch;
import com.springrain.erp.modules.amazoninfo.entity.AmazonNewReleasesRank;
import com.springrain.erp.modules.amazoninfo.service.AmazonKeywordService;
import com.springrain.erp.modules.amazoninfo.service.AmazonPostsDetailService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductCatalogService;
import com.springrain.erp.modules.psi.service.PsiProductService;
public class AmazonProductCatalogMonitor {
	
	private final static Logger logger = LoggerFactory.getLogger(AmazonProductCatalogMonitor.class);
	
	@Autowired
	private AmazonProductCatalogService amazonProductCatalogService;
	
	@Autowired
	private AmazonPostsDetailService amazonPostsDetailService;
	
	@Autowired
	private  AmazonKeywordService amazonKeywordService;
	
	@Autowired
	private PsiProductService psiProductService;

	public void scanNewProdcutTop100(){
		final Map<String,List<String>> productMap=psiProductService.findNewProduct();
		if(productMap!=null&&productMap.size()>0){
			for (final String country : productMap.keySet()) {
				new Thread(){
					public void run() {
						Date date=new Date();
						List<AmazonNewReleasesRank> rankList=Lists.newArrayList();
						String suff=country;
						if("uk,jp".contains(country)){
							 suff = "co."+suff;
						}else if("mx".equals(country)){
							suff = "com."+suff;
						}
						List<String> nameList=productMap.get(country);
						Map<String,List<AmazonCatalogRank>> catalogAndName=amazonPostsDetailService.findCatalogByProduct(country, nameList);
						
						for (Map.Entry<String,List<AmazonCatalogRank>> entry : catalogAndName.entrySet()) { 
							String catalog=entry.getKey();
							//https://www.amazon.com/gp/new-releases/pc/11036491/ref=zg_bs_tab_t_bsnr
							try{
								List<AmazonCatalogRank> asinAndNameList=entry.getValue();
								Set<String> asinSet=Sets.newHashSet();
								for (AmazonCatalogRank rank : asinAndNameList) {
									asinSet.add(rank.getAsin());
								}
								Map<String,Integer> rankMap=Maps.newHashMap();
								for(int i =1;i<6;i++){
									String pageUrl="";
									if("com".equals(country)){
										pageUrl="http://www.amazon."+suff+"/gp/new-releases/pc/"+catalog+"/?pg="+i;
									}else if("it,ca".contains(country)){
										pageUrl="http://www.amazon."+suff+"/gp/new-releases/electronics/"+catalog+"/?pg="+i;
									}else{
										pageUrl="http://www.amazon."+suff+"/gp/new-releases/computers/"+catalog+"/?pg="+i;
									}
									Document doc = HttpRequest.reqUrl(pageUrl, null, false);
									if(doc!=null){
										Elements itemDivs = doc.getElementsByClass("jp".equals(country)?"zg_itemRow":"zg_itemImmersion");
										for(Element itemDiv:itemDivs){
											String rank="";
											String asin="";
											if("jp".equals(country)){
												rank= itemDiv.getElementsByClass("zg_rankNumber").get(0).text().replace(".","");
												String asinText=itemDiv.childNodes().get(1).attr("data-p13n-asin-metadata");//{"ref":"zg_bsnr_11036491_1","asin":"B01LXC1QL0"}
												asin=asinText.substring(asinText.indexOf("asin")+7).replace("\"}", "");
											}else{
												rank= itemDiv.getElementsByClass("zg_rankNumber").get(0).text().replace(".","");
												Elements el=itemDiv.getElementsByClass("zg_itemWrapper");
												String asinText=el.get(0).childNodes().get(1).attr("data-p13n-asin-metadata");
												asin=asinText.substring(asinText.indexOf("asin")+7).replace("\"}", "");
											}
											if(asinSet.contains(asin)){
												rankMap.put(asin, Integer.parseInt(rank));
												asinSet.remove(asin);
											}
											if(asinSet.size()==0){
												break;
											}
										}
									}
									if(asinSet.size()==0){
										break;
									}
								}
								for (AmazonCatalogRank rank : asinAndNameList) {
									if(rankMap!=null&&rankMap.size()>0&&rankMap.get(rank.getAsin())!=null){
										AmazonNewReleasesRank newReleasesRank=new AmazonNewReleasesRank();
										newReleasesRank.setAsin(rank.getAsin());
										newReleasesRank.setCountry(rank.getCountry());
										newReleasesRank.setProductName(rank.getProductName());
										newReleasesRank.setCatalog(rank.getCatalog());
										newReleasesRank.setCatalogName(rank.getCatalogName());
										newReleasesRank.setPath(rank.getPath());
										newReleasesRank.setPathName(rank.getPathName());
										newReleasesRank.setRank(rankMap.get(rank.getAsin()));
										newReleasesRank.setQueryTime(date);
										rankList.add(newReleasesRank);
									}
								}
							}catch(Exception e){
								logger.error(country+" new-releases:"+catalog, e.getMessage());
							}
						}
						
						if(rankList!=null&&rankList.size()>0){
							amazonPostsDetailService.saveNewReleasesRank(rankList);
						}
					}
		        }.start();
				
			}
		}
	}
	

	public void saveKeyword(){
		List<AmazonKeywordSearch>  searchList=amazonKeywordService.findKeywordByCountry();
		if(searchList!=null&&searchList.size()>0){
			for (AmazonKeywordSearch keywordSearch : searchList) {
				String country=keywordSearch.getCountry();
				String keyword=keywordSearch.getKeyword();
				String suff=country;
				if("uk,jp".contains(country)){
					 suff = "co."+suff;
				}else if("mx".equals(country)){
					suff = "com."+suff;
				}
				List<AmazonKeyword> keywordList=find(suff,keyword);
				if(keywordList!=null&&keywordList.size()>0){
					List<AmazonKeyword> oldItems= keywordSearch.getItems();
					if(oldItems==null||oldItems.size()==0){//add
						for (AmazonKeyword amazonKeyword: keywordList) {
							amazonKeyword.setSearch(keywordSearch);
						}
						keywordSearch.setItems(keywordList);
					}else{
						boolean flag=true;
						for (AmazonKeyword oldItem : oldItems) {
							for (AmazonKeyword newItem: keywordList) {
								 if(oldItem.getAsin().equals(newItem.getAsin())){//edit
									 flag=false;
									 oldItem.setTitle(newItem.getTitle());
									 oldItem.setRank(newItem.getRank());
									 break;
								 }
							}
							if(flag){//delete
								oldItem.setDelFlag("1");
							}
							flag=true;
						}
						boolean addFlag=true;
						for (AmazonKeyword newItem: keywordList) {//add
							for (AmazonKeyword oldItem : oldItems) {
								if(newItem.getAsin().equals(oldItem.getAsin())){
									addFlag=false;
									break;
								}
							}
							if(addFlag){
								newItem.setSearch(keywordSearch);
								keywordSearch.getItems().add(newItem);
							}
							addFlag=true;
						}
					}
				}
			}
			amazonKeywordService.saveSearchList(searchList);
		}
		try{
			amazonKeywordService.cancelKey();
		}catch(Exception e){
			logger.error("取消监控关键字",e);
		}
	}
	
	public List<AmazonKeyword> find(String country,String keyword){
	    int rank=1;
		List<AmazonKeyword> list=Lists.newArrayList();
		for (int i=1;i<=3;i++) {
			String pageUrl="https://www.amazon."+country+"/s/?page="+i+"&keywords="+keyword;
			try {
				Document doc = HttpRequest.reqUrl(pageUrl,null,false);
				if(doc!=null){
					Element element=doc.getElementById("s-results-list-atf");
					Elements els=element.children();
					for (Element ele: els) {
						AmazonKeyword amazonKeyword=new AmazonKeyword();
						String asin=ele.attr("data-asin");
					    String title=ele.getElementsByTag("h2").get(0).attr("data-attribute");
					   // System.out.println(asin+"==="+title);
					    amazonKeyword.setAsin(asin);
					    amazonKeyword.setTitle(title);
					    amazonKeyword.setRank(rank++);
					    amazonKeyword.setDelFlag("0");
					    list.add(amazonKeyword);
					}
				}
				
			} catch (Exception e) {
				logger.error("keyword", e.getMessage());
			}
		}
		return list;
	}
	
	public AmazonPostsDetailService getAmazonPostsDetailService() {
		return amazonPostsDetailService;
	}

	public void setAmazonPostsDetailService(
			AmazonPostsDetailService amazonPostsDetailService) {
		this.amazonPostsDetailService = amazonPostsDetailService;
	}

	public AmazonKeywordService getAmazonKeywordService() {
		return amazonKeywordService;
	}

	public void setAmazonKeywordService(AmazonKeywordService amazonKeywordService) {
		this.amazonKeywordService = amazonKeywordService;
	}

	public AmazonProductCatalogService getAmazonProductCatalogService() {
		return amazonProductCatalogService;
	}

	public void setAmazonProductCatalogService(
			AmazonProductCatalogService amazonProductCatalogService) {
		this.amazonProductCatalogService = amazonProductCatalogService;
	}
}
