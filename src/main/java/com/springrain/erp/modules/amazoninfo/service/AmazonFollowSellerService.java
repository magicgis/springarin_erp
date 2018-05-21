/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.amazoninfo.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonFollowSellerDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonFollowSeller;

/**
 * 广告报表Service
 * @author Tim
 * @version 2015-03-03
 */
@Component
@Transactional(readOnly = true)
public class AmazonFollowSellerService extends BaseService {

	@Autowired
	private AmazonFollowSellerDao sellerDao;
	
	
	public List<AmazonFollowSeller> find(AmazonFollowSeller followSeller) {
		DetachedCriteria dc = sellerDao.createDetachedCriteria();
		if(followSeller.getDataDate()!=null){
			dc.add(Restrictions.ge("dataDate",followSeller.getDataDate()));
		}
		if(followSeller.getUpdateDate()!=null){
			dc.add(Restrictions.le("dataDate",DateUtils.addDays(followSeller.getUpdateDate(),1)));
		}
		if(StringUtils.isNotEmpty(followSeller.getSellerName())){
			dc.add(Restrictions.eq("sellerName", followSeller.getSellerName()));
		}
		if(StringUtils.isNotEmpty(followSeller.getCountry())){
			dc.add(Restrictions.eq("country", followSeller.getCountry()));
		}
		if(StringUtils.isNotEmpty(followSeller.getProductName())){
			dc.add(Restrictions.or(Restrictions.like("productName", "%"+followSeller.getProductName()+"%"),Restrictions.like("asin", "%"+followSeller.getProductName()+"%")));
		}
		return sellerDao.findGroupByProduct(dc);
	}
	
	public AmazonFollowSeller getByAsinCountrySuplier(Date date,String sellerName,String country,String asin){
		DetachedCriteria dc = this.sellerDao.createDetachedCriteria();
		dc.add(Restrictions.eq("dataDate", date));
		dc.add(Restrictions.eq("sellerName", sellerName));
		dc.add(Restrictions.eq("asin", asin));
		dc.add(Restrictions.eq("country", country));
		List<AmazonFollowSeller> rs = this.sellerDao.find(dc);
		if(rs.size()>0){
			return rs.get(0);
		}
		return null;
	}
	
	
	@Transactional(readOnly = false)
	public void updateSeller(Date date,String sellerName,String country,String asin,String a,String productTitle,String productName){
		AmazonFollowSeller followSerller = this.getByAsinCountrySuplier(date, sellerName, country, asin);
		if(followSerller!=null){
			followSerller.setQuantity(followSerller.getQuantity()+1);
		}else{
			followSerller=new AmazonFollowSeller(date, 1, sellerName, a, country, productTitle, asin, productName);
		}
		this.sellerDao.save(followSerller);
	}
	
	
	public String renderData(){
		StringBuffer contents=new StringBuffer();
		String sql="SELECT a.`country`,a.`asin`,a,a.`product_name`,SUM(a.`quantity`),a.seller_name FROM amazoninfo_follow_seller AS a WHERE a.`data_date`>=DATE_ADD(CURDATE(),INTERVAL -1 DAY) GROUP BY a.`country`,a.`asin`,a.`seller_name`";
		List<Object[]> list = this.sellerDao.findBySql(sql);
		//国家：供应商：asin/name
		Map<String,String> aMap = Maps.newHashMap();
		Map<String,Map<String,List<String>>> rs = Maps.newHashMap();
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				String country = obj[0].toString();
				String asin = obj[1].toString();
				String sellerA = obj[2].toString();
				String proName = obj[3].toString();
				Integer quantity = Integer.parseInt(obj[4].toString());
				String sellerName = obj[5].toString();
				Map<String,List<String>> inMap =null;
				if(rs.get(country)==null){
					inMap = Maps.newHashMap();
				}else{
					inMap = rs.get(country);
				}
				
				List<String> infos= null;
				if(inMap.get(sellerName)==null){
					infos=Lists.newArrayList();
				}else{
					infos=inMap.get(sellerName);
				}
				infos.add(asin+","+proName+","+quantity);
				inMap.put(sellerName, infos);
				rs.put(country, inMap);
				aMap.put(sellerName, sellerA);
			}
			
			//组装邮件
			contents.append("Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;<br/>从昨天凌晨到目前，跟卖信息如下：<br/>");
			contents.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '><tr style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '>" +
			"<th>国家</th><th>卖家</th><th>产品名</th><th>asin</th><th>抓到次数</th></tr>");
			for(Map.Entry<String,Map<String,List<String>>> entry:rs.entrySet()){
				String country = entry.getKey();
				String suffix =country;
				if("jp".equals(country)){
					suffix="co.jp";
				}else if("uk".equals(country)){
					suffix="co.uk";
				}else if("mx".equals(country)){
					suffix="com.mx";
				}
				int i=0;
				for(Map.Entry<String,List<String>> entry1:entry.getValue().entrySet()){
					String sellerName = entry1.getKey();
					String sellerAthor =aMap.get(sellerName);
					List<String> infos = entry1.getValue();
					String color="#f5fafe";
					if(i==0){
						color="#99CCFF";
					}
					i++;
					StringBuffer asins = new StringBuffer();
					StringBuffer names = new StringBuffer();
					StringBuffer quantitys = new StringBuffer();
					for(String info :infos){
						String arr[] = info.split(",");
						asins.append("<a target='_blank' href='https://www.amazon."+suffix+"/gp/offer-listing/"+arr[0]+"?condition=new' >"+ arr[0]+"</a><br/>");
						names.append(arr[1]+"<br/>");
						quantitys.append(arr[2]+"<br/>");
					}
					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:"+color+"; '>" +
							"<td>"+("com".equals(country)?"us":country)+"</td>"+
							"<td>"+sellerAthor+"</td><td>"+names+"</td><td>"+asins+"</td><td>"+quantitys+"</td></tr>");
				}
			}
			contents.append("</table>");
		}
		return contents.toString();
	}
	
	
	
	public static void main(String [] arr){
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/spring-context.xml");
		AmazonFollowSellerService seller = context.getBean(AmazonFollowSellerService.class);
		seller.renderData();
	}
	
}
