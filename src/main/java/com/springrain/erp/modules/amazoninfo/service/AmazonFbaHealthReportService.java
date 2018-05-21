package com.springrain.erp.modules.amazoninfo.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.amazoninfo.dao.AmazonFbaHealthReportDao;
import com.springrain.erp.modules.amazoninfo.entity.AmazonFbaHealthReport;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;

@Component
@Transactional(readOnly = true)
public class AmazonFbaHealthReportService extends BaseService {

	@Autowired
	private AmazonFbaHealthReportDao amazonFbaHealthReportDao;
	@Autowired
	private PsiProductEliminateService 		psiProductEliminateService;
	
	public AmazonFbaHealthReport get(Integer id) {
		return amazonFbaHealthReportDao.get(id);
	}
	
	public Page<AmazonFbaHealthReport> find(Page<AmazonFbaHealthReport> page, AmazonFbaHealthReport amazonFbaHealthReport) {
		DetachedCriteria dc = amazonFbaHealthReportDao.createDetachedCriteria();
		dc.add(Restrictions.ge("createTime",amazonFbaHealthReport.getCreateTime()));
		dc.add(Restrictions.le("createTime",DateUtils.addDays(amazonFbaHealthReport.getCreateTime(),1)));
		if(StringUtils.isNotEmpty(amazonFbaHealthReport.getCountry())){
			if("eu".equals(amazonFbaHealthReport.getCountry())){
				dc.add(Restrictions.in("country",Sets.newHashSet("de","fr","it","es","uk")));
			}else{
				dc.add(Restrictions.eq("country",amazonFbaHealthReport.getCountry()));
			}
		}
		return amazonFbaHealthReportDao.find(page, dc);
	}

	
	
	public List<AmazonFbaHealthReport> findWarnning(AmazonFbaHealthReport amazonFbaHealthReport) {
			DetachedCriteria dc = amazonFbaHealthReportDao.createDetachedCriteria();
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			date.setHours(0);
			date.setMinutes(0);
			date.setSeconds(0);
			cal.setTime(date);       
			/*if("com,ca,mx,uk,de,it,es,fr".contains(amazonFbaHealthReport.getCountry())){
				cal.add (cal.DAY_OF_MONTH, -3); 
			}else if("jp".equals(amazonFbaHealthReport.getCountry())){
				cal.add (cal.DAY_OF_MONTH, -2); 
			}*/
			amazonFbaHealthReport.setCreateTime(cal.getTime());
			dc.add(Restrictions.or(Restrictions.gt("ageDays365",0),Restrictions.gt("agePlusDays365",0)));
			dc.add(Restrictions.ge("createTime",amazonFbaHealthReport.getCreateTime()));
			dc.add(Restrictions.le("createTime",DateUtils.addDays(amazonFbaHealthReport.getCreateTime(),1)));
			if(StringUtils.isNotEmpty(amazonFbaHealthReport.getCountry())){
				dc.add(Restrictions.eq("country",amazonFbaHealthReport.getCountry()));
			}
			return amazonFbaHealthReportDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void save(List<AmazonFbaHealthReport> fbasHealths) {
		amazonFbaHealthReportDao.save(fbasHealths);
	}
	
	public Map<String, String> findSkuProductName(String time) {
		String 	sql="SELECT DISTINCT t.`sku`,s.`product_name` FROM `amazoninfo_fba_health_report` t,`psi_sku` s WHERE DATE_FORMAT(t.`create_time`,'%Y%m%d')=:p1 AND t.`sku`=s.`sku`";
		Map<String, String> map = Maps.newHashMap();
		List<Object[]> list = amazonFbaHealthReportDao.findBySql(sql, new Parameter(time));
	    for (Object[] obj : list) {
			map.put(obj[0].toString(),obj[1]==null?"":obj[1].toString());
		}
		return map;
	}
	
	public Map<String,Map<String,AmazonFbaHealthReport>> findWarnningInventory(AmazonFbaHealthReport amazonFbaHealthReport) {
		Map<String,Map<String,AmazonFbaHealthReport>> map=Maps.newHashMap();
		Map<String,PsiProductEliminate> productCountryAttrMap=psiProductEliminateService.findProductCountryAttr();
		String sql="SELECT t.`country`,CONCAT(p.`product_name`,CASE WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END) NAME,t.`age_days270`,t.`age_days365`,t.`age_plus_days365` "+
				" FROM amazoninfo_fba_health_report t  "+
				" JOIN psi_sku p ON t.`country`=p.`country` AND t.`sku`=p.sku AND p.`del_flag`='0'  "+
				" WHERE t.`create_time`=:p1 AND (t.`age_days270`>0 || t.`age_days365`>0 || t.`age_plus_days365`>0) and p.`product_name`!='Inateck other' and p.`product_name`!='Inateck old' ";
		List<Object[]> list = amazonFbaHealthReportDao.findBySql(sql, new Parameter(amazonFbaHealthReport.getCreateTime()));
		for (Object[] obj: list) {
			String country=obj[0].toString();
			String name=obj[1].toString();
			Integer quantity1=Integer.parseInt(obj[2]==null?"0":obj[2].toString());
			Integer quantity2=Integer.parseInt(obj[3]==null?"0":obj[3].toString());
			Integer quantity3=Integer.parseInt(obj[4]==null?"0":obj[4].toString());
			
			PsiProductEliminate ate=productCountryAttrMap.get(name+"_"+country);
			if(ate==null){
				continue;
			}
			if("淘汰".equals(ate.getIsSale())&&ate.getEliminateTime()==null){
				quantity1=0;
				quantity2=0;
			}
			if("淘汰".equals(ate.getIsSale())&&ate.getEliminateTime()!=null&&ate.getEliminateTime().before(amazonFbaHealthReport.getCreateTime())){
				quantity1=0;
				quantity2=0;
			}
			if(quantity1==0&&quantity2==0&&quantity3==0){
				continue;
			}
			AmazonFbaHealthReport report=new AmazonFbaHealthReport();
			report.setCountry(country);
			report.setProductName(name);
			report.setAgeDays365(quantity1+quantity2);
			report.setAgePlusDays365(quantity3);
			
			
			Map<String,AmazonFbaHealthReport> temp=map.get(country);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(country, temp);
			}
			temp.put(name, report);
			
			if("ca,com,uk".contains(country)){
				Map<String,AmazonFbaHealthReport> enTemp=map.get("en");
				if(enTemp==null){
					enTemp=Maps.newHashMap();
					map.put("en", enTemp);
				}
				AmazonFbaHealthReport tempReport=enTemp.get(name);
				if(tempReport==null){
					tempReport=new AmazonFbaHealthReport(0,0);
					enTemp.put(name, tempReport);
				}
				tempReport.setAgeDays365(tempReport.getAgeDays365()+report.getAgeDays365());
				tempReport.setAgePlusDays365(tempReport.getAgePlusDays365()+report.getAgePlusDays365());
			}
			
			if("de,fr,it,es,jp".contains(country)){
				Map<String,AmazonFbaHealthReport> nonEnTemp=map.get("nonEn");
				if(nonEnTemp==null){
					nonEnTemp=Maps.newHashMap();
					map.put("nonEn", nonEnTemp);
				}
				AmazonFbaHealthReport tempReport=nonEnTemp.get(name);
				if(tempReport==null){
					tempReport=new AmazonFbaHealthReport(0,0);
					nonEnTemp.put(name, tempReport);
				}
				tempReport.setAgeDays365(tempReport.getAgeDays365()+report.getAgeDays365());
				tempReport.setAgePlusDays365(tempReport.getAgePlusDays365()+report.getAgePlusDays365());
			}
		}
        return map;	
    }
	
}
