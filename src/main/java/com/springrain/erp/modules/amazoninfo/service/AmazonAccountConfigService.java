package com.springrain.erp.modules.amazoninfo.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.amazoninfo.dao.AmazonAccountConfigDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;


@Component
@Transactional(readOnly = true)
public class AmazonAccountConfigService extends BaseService {

	@Autowired
	private AmazonAccountConfigDao amazonAccountConfigDao;
	
	public AmazonAccountConfig getByName(String accountName){
		DetachedCriteria dc = amazonAccountConfigDao.createDetachedCriteria();
		dc.add(Restrictions.eq("accountName",accountName));
		dc.add(Restrictions.eq("serverId",Global.getConfig("server.id")));
		dc.add(Restrictions.eq("delFlag","0"));
		List<AmazonAccountConfig> list=amazonAccountConfigDao.find(dc);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public List<AmazonAccountConfig> findAllConfig(){
		DetachedCriteria dc = amazonAccountConfigDao.createDetachedCriteria();
		dc.add(Restrictions.eq("serverId",Global.getConfig("server.id")));
		dc.add(Restrictions.eq("delFlag","0"));
		return amazonAccountConfigDao.find(dc);
	}
	
	public List<String> findAccountName(){
		String sql="select account_name from amazoninfo_account_config where server_id=:p1 and del_flag='0'";
	    return amazonAccountConfigDao.findBySql(sql,new Parameter(Global.getConfig("server.id")));
	}
	
	
	public Map<String,AmazonAccountConfig> findConfigByAccountName(){
		Map<String,AmazonAccountConfig> map=Maps.newLinkedHashMap();
		DetachedCriteria dc = amazonAccountConfigDao.createDetachedCriteria();
		dc.add(Restrictions.eq("serverId",Global.getConfig("server.id")));
		dc.add(Restrictions.eq("delFlag","0"));
		List<AmazonAccountConfig> list=amazonAccountConfigDao.find(dc);
		for (AmazonAccountConfig config : list) {
			map.put(config.getAccountName(),config);
		}
		return map;
	}
	
	public Map<String,List<String>> findAccountByCountry(){
		Map<String,List<String>>  map=Maps.newLinkedHashMap();
	    String sql="select country,account_name from amazoninfo_account_config where server_id=:p1 and del_flag='0'";
	    List<Object[]> list= amazonAccountConfigDao.findBySql(sql,new Parameter(Global.getConfig("server.id")));
	    for (Object[] obj: list) {
	    	List<String> temp=map.get(obj[0].toString());
	    	if(temp==null){
	    		temp=Lists.newArrayList();
	    		map.put(obj[0].toString(),temp);
	    	}
	    	temp.add(obj[1].toString());
	    }
		return map;
	}
	
	public Map<String,String> findCountryByAccount(){
		Map<String,String>  map=Maps.newLinkedHashMap();
	    String sql="select country,account_name from amazoninfo_account_config where  del_flag='0'";
	    List<Object[]> list= amazonAccountConfigDao.findBySql(sql);
	    for (Object[] obj: list) {
	    	map.put(obj[1].toString(),obj[0].toString());
	    }
		return map;
	}
	
	public Map<String,String> findCountryByAccountByServer(){
		Map<String,String>  map=Maps.newLinkedHashMap();
	    String sql="select country,account_name from amazoninfo_account_config where server_id=:p1 and  del_flag='0'";
	    List<Object[]> list= amazonAccountConfigDao.findBySql(sql,new Parameter(Global.getConfig("server.id")));
	    for (Object[] obj: list) {
	    	map.put(obj[1].toString(),obj[0].toString());
	    }
		return map;
	}
	
	
	public AmazonAccountConfig getByName(String accountName,boolean flag){
		DetachedCriteria dc = amazonAccountConfigDao.createDetachedCriteria();
		dc.add(Restrictions.eq("accountName",accountName));
		if(flag){
			dc.add(Restrictions.eq("serverId",Global.getConfig("server.id")));
		}
		dc.add(Restrictions.eq("delFlag","0"));
		List<AmazonAccountConfig> list=amazonAccountConfigDao.find(dc);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	
	public List<String> findAccountByCountry(String country){
		String sql="select account_name from amazoninfo_account_config where country=:p1 and server_id=:p2 and del_flag='0'";
		return amazonAccountConfigDao.findBySql(sql, new Parameter(country,Global.getConfig("server.id")));
	}
	
	public List<String> findAccountByCountryNoServer(String country){
		String sql="select account_name from amazoninfo_account_config where country=:p1 and del_flag='0'";
		return amazonAccountConfigDao.findBySql(sql, new Parameter(country));
	}
	
	public List<AmazonAccountConfig> findEmail(){
		String sql=" select distinct customer_email,customer_email_password,email_type from amazoninfo_account_config where server_id=:p1 and del_flag='0' and customer_email is not null";
		List<Object[]> list=amazonAccountConfigDao.findBySql(sql,new Parameter(Global.getConfig("server.id")));
		List<AmazonAccountConfig> rs=Lists.newArrayList();
		for (Object[] obj : list) {
			AmazonAccountConfig config=new AmazonAccountConfig();
			config.setCustomerEmail(obj[0].toString());
			config.setCustomerEmailPassword(obj[1].toString());
			config.setEmailType(obj[2].toString());
			rs.add(config);
		}
		return rs;
	}
	
	
	///
	public AmazonAccountConfig findEmail(String fromServer){
		
		Set<String> emailSet=Sets.newHashSet(fromServer);
		Map<String,String> map=findMainEmail();
		if(map!=null&&map.get(fromServer)!=null){
			emailSet.add(map.get(fromServer));
		}
		
		String sql=" select distinct customer_email,customer_email_password,email_type from amazoninfo_account_config where customer_email in :p1 and del_flag='0' and customer_email is not null";
		List<Object[]> list=amazonAccountConfigDao.findBySql(sql,new Parameter(emailSet));
		
		if(list!=null&&list.size()>0){
			Object[] obj = list.get(0);
			AmazonAccountConfig config=new AmazonAccountConfig();
			config.setCustomerEmail(obj[0].toString());
			config.setCustomerEmailPassword(obj[1].toString());
			config.setEmailType(obj[2].toString());
			return config;
		}
		return null;
	}
	
	

	public  Map<String,Set<String>> findAnotherNameEmail(){
		Map<String,Set<String>> map=Maps.newLinkedHashMap();
		String anotherName1=Global.getConfig("anotherName1").trim();
		String[] arr=anotherName1.split(",");
		Set<String> temp=map.get(arr[0].trim());
		if(temp==null){
			temp=Sets.newHashSet();
			map.put(arr[0].trim(), temp);
		}
		for(int i=1;i<arr.length;i++){
			temp.add(arr[i].trim());
		}
		return map;
	}
	
	public  Map<String,String> findMainEmail(){
		Map<String,String> map=Maps.newLinkedHashMap();
		String anotherName1=Global.getConfig("anotherName1").trim();
		String[] arr=anotherName1.split(",");
		for(int i=1;i<arr.length;i++){
			map.put(arr[i].trim(),arr[0].trim());
		}
		return map;
	}
	
	///
	public List<String> findEmailByAccount(String accountName){
		List<String> rs=Lists.newArrayList();
		String sql="SELECT g.`customer_email`,country FROM amazoninfo_account_config g WHERE g.`account_name`=:p1 AND g.`del_flag`='0' "; 
		List<Object[]> list=amazonAccountConfigDao.findBySql(sql,new Parameter(accountName));
		if(list!=null&&list.size()>0){
			Object[] obj =list.get(0);
			String email = obj[0].toString();
			String country=("com".equals(obj[1].toString())?"us":obj[1].toString());
			
			rs.add(email+"=="+accountName);
			
			Map<String,Set<String>> tempMap=findAnotherNameEmail();
			if(tempMap!=null&&tempMap.get(email)!=null){
				Set<String> emailSet = tempMap.get(email);
				for (String aliasEmail : emailSet) {
					 if(aliasEmail.contains("_"+country)){
						rs.add(aliasEmail+"=="+accountName+"["+aliasEmail.split("@")[0]+"]");
					 }
				}
			}
		}
		return rs;
	}
	//support@inateck.com,amazon_us@inateck.com,support_jp@inateck.com,amazon_ca@inateck.com,support_mx@inateck.com
	public Map<String,String> findEmailByAccountName(String accountName){
		Map<String,String> map=Maps.newLinkedHashMap();
		String sql="SELECT g.`customer_email`,country FROM amazoninfo_account_config g WHERE g.`account_name`=:p1 AND g.`del_flag`='0' "; 
		List<Object[]> list=amazonAccountConfigDao.findBySql(sql,new Parameter(accountName));
		if(list!=null&&list.size()>0){
			Object[] obj =list.get(0);
			String email = obj[0].toString();
			String country=("com".equals(obj[1].toString())?"us":obj[1].toString());
			map.put(email, accountName);
			
			Map<String,Set<String>> tempMap=findAnotherNameEmail();
			if(tempMap!=null&&tempMap.get(email)!=null){
				Set<String> emailSet = tempMap.get(email);
				for (String aliasEmail : emailSet) {
					 if(aliasEmail.contains("_"+country)){
						 map.put(aliasEmail,accountName+"["+aliasEmail.split("@")[0]+"]");
					 }
				}
			}
		}
		return map;
	}
	
	
	///
	public String findAccountByEmail(String email){
		
		Set<String> emailSet=Sets.newHashSet(email);
		Map<String,String> map=findMainEmail();
		if(map!=null&&map.get(email)!=null){
			emailSet.add(map.get(email));
		}
		
		String sql1="SELECT g.`account_name` FROM amazoninfo_account_config g WHERE g.`customer_email` in :p1 AND g.`del_flag`='0' "; 
		List<String> list1=amazonAccountConfigDao.findBySql(sql1,new Parameter(emailSet));
		if(list1!=null&&list1.size()>0){
			return list1.get(0);
		}
		return null;
	}

	public Map<String,List<String>> findAccountByCountryNoServer(){
		Map<String,List<String>>  map=Maps.newLinkedHashMap();
	    String sql="select country,account_name from amazoninfo_account_config where del_flag='0'";
	    List<Object[]> list= amazonAccountConfigDao.findBySql(sql);
	    for (Object[] obj: list) {
	    	List<String> temp=map.get(obj[0].toString());
	    	if(temp==null){
	    		temp=Lists.newArrayList();
	    		map.put(obj[0].toString(),temp);
	    	}
	    	temp.add(obj[1].toString());
	    }
		return map;
	}
	
	public AmazonAccountConfig findBySellerId(String country,String sellerId){
		DetachedCriteria dc = amazonAccountConfigDao.createDetachedCriteria();
		dc.add(Restrictions.eq("country",country));
		dc.add(Restrictions.eq("sellerId",sellerId));
		dc.add(Restrictions.eq("serverId",Global.getConfig("server.id")));
		dc.add(Restrictions.eq("delFlag","0"));
		List<AmazonAccountConfig> list=amazonAccountConfigDao.find(dc);
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}


	public List<AmazonAccountConfig> findWsInfo(){
		List<AmazonAccountConfig> rs = Lists.newArrayList();
		String sql = "SELECT t.`account_name`,t.`server_ip` FROM `amazoninfo_account_config` t "+
				" WHERE t.`server_ip` IS NOT NULL AND t.`del_flag`='0' GROUP BY t.`server_ip`";
		List<Object[]> list = amazonAccountConfigDao.findBySql(sql);
		for (Object[] obj : list) {
			String accountName = obj[0].toString().split("_")[0];
			String serverIp = obj[1].toString();
			AmazonAccountConfig config = new AmazonAccountConfig();
			config.setAccountName(accountName);
			config.setServerIp(serverIp);
			rs.add(config);
		}
		return rs;
	}
	
	
	public Map<String,String> findServerIpByAccount(){
		Map<String,String> map=Maps.newHashMap(); 
	    String sql="select account_name,server_ip from amazoninfo_account_config where del_flag='0'";
	    List<Object[]> list= amazonAccountConfigDao.findBySql(sql);
	    if(list!=null&&list.size()>0){
	    	for (Object[] obj : list) {
				map.put(obj[0].toString(), obj[1].toString());
			}
	    }
	    return map;
	}
	
	public Map<String,String> findAccountByEmail(){
		Map<String,String> map=Maps.newHashMap();
		String sql="select customer_email,country,account_name from amazoninfo_account_config where customer_email is not null and del_flag='0' ";
		List<Object[]> list= amazonAccountConfigDao.findBySql(sql);
		for (Object[] obj: list) {
			map.put(obj[0].toString()+"_"+obj[1].toString(),obj[2].toString());
			map.put(obj[0].toString(),obj[2].toString().split("_")[0]);
		}
		return map;
	}
	
	
	public Map<String,String> findAllEmailByServer(){
		Map<String,String> map = Maps.newHashMap();
		String sql=" select customer_email,account_name from amazoninfo_account_config where  del_flag='0' and customer_email is not null";
		List<Object[]> list=amazonAccountConfigDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			Map<String,Set<String>> tempMap=findAnotherNameEmail();
			for (Object[] obj: list) {
				map.put(obj[0].toString(),obj[1].toString().split("_")[0]);
			}
			for (Map.Entry<String,Set<String>> entry: tempMap.entrySet()) {
				String key = entry.getKey();
				Set<String> emailSet = entry.getValue();
				for (String email: emailSet) {
					 map.put(email,map.get(key));
				}
			}
		}
		return map;
	}
	

	
}
