package com.springrain.erp.modules.amazoninfo.service;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonFinancialDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonFinancial;

@Component
@Transactional(readOnly = true)
public class AmazonFinancialService extends BaseService {

	@Autowired
	private AmazonFinancialDao amazoninfoFinancialDao;

	@Transactional(readOnly = false)
	public void save(AmazonFinancial amazoninfoFinancial){
		amazoninfoFinancialDao.save(amazoninfoFinancial);
	}

	@Transactional(readOnly = false)
	public void save(List<AmazonFinancial> amazoninfoFinancials){
		amazoninfoFinancialDao.save(amazoninfoFinancials);
	}
	
	public Timestamp getMaxOrderDate(String accountName){
		String sql = "";
		List<Object> rs = null;
		List<String> countryList = Lists.newArrayList();
		if (StringUtils.isBlank(accountName)) {
			sql = "SELECT t.`posted_date` FROM `amazoninfo_financial` t ORDER BY t.`posted_date` DESC LIMIT 1";
			rs = amazoninfoFinancialDao.findBySql(sql);
		} else {
			if (accountName.contains("DE")) {
				String account = accountName.split("_")[0];
				countryList.add(account+"_DE");
				countryList.add(account+"_UK");
				countryList.add(account+"_FR");
				countryList.add(account+"_IT");
				countryList.add(account+"_ES");
			} else {
				countryList.add(accountName);
			}
			sql = "SELECT t.`posted_date` FROM `amazoninfo_financial` t where t.`account_name` IN :p1 ORDER BY t.`posted_date` DESC LIMIT 1";
			rs = amazoninfoFinancialDao.findBySql(sql, new Parameter(countryList));
		}
		if (rs.size() > 0) {
			return (Timestamp) rs.get(0);
		} else {
			return null;
		}
	}
}
