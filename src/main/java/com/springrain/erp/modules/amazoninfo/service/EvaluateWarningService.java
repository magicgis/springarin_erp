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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.EvaluateWarningDao;
import com.springrain.erp.modules.amazoninfo.entity.EvaluateWarning;

/**
 * 折扣预警Service
 * @author Michael
 * @version 2015-08-24
 */
@Component
@Transactional(readOnly = true)
public class EvaluateWarningService extends BaseService {

	@Autowired
	private EvaluateWarningDao evaluateWarningDao;
	
	
	public EvaluateWarning get(Integer id) {
		return evaluateWarningDao.get(id);
	}
	
	
	public Page<EvaluateWarning> find(Page<EvaluateWarning> page, EvaluateWarning evaluateWarning) {
		DetachedCriteria dc = evaluateWarningDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(evaluateWarning.getPromotionId())){
			dc.add(Restrictions.like("promotionId","%"+evaluateWarning.getPromotionId()+"%"));
		}
		if(StringUtils.isNotEmpty(evaluateWarning.getCountry())){
			dc.add(Restrictions.eq("country", evaluateWarning.getCountry()));
		}
		if(evaluateWarning.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate", evaluateWarning.getCreateDate()));
		}
		if (evaluateWarning.getUpdateDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(evaluateWarning.getUpdateDate(),1)));
		}
		if(StringUtils.isNotEmpty(evaluateWarning.getWarningSta())){
			if("1".equals(evaluateWarning.getWarningSta())){
				dc.add(Restrictions.and(Restrictions.ne("warningSta","2"),Restrictions.ne("warningSta","0")));
			}else{
				dc.add(Restrictions.eq("warningSta",evaluateWarning.getWarningSta()));
			}
		}
		page.setOrderBy("warningSta,id desc");
		return evaluateWarningDao.find(page, dc);
	}
	
	
	@Transactional(readOnly = false)
	public void save(EvaluateWarning evaluateWarning) {
		evaluateWarningDao.save(evaluateWarning);
	}
	
	/**
	 *更新结code及原因置空
	 */
	@Transactional(readOnly = false)
	public void updateEvaluateCode(String tranId,String country,String newCode,String orderAndOldCode,Date date) {
		EvaluateWarning warning = this.findByTranId(tranId, country);
		Integer evaluateId = warning.getId();
		String sql ="UPDATE amazoninfo_evaluate_warning  AS a SET a.`promotion_code`=:p2,a.`result`='',a.`update_date`=:p3 WHERE id =:p1";
		evaluateWarningDao.updateBySql(sql, new Parameter(evaluateId,newCode,date));
		String insertSQL="INSERT INTO  amazoninfo_evaluate_warning_log (evaluate_warning_id,promotion_code,relative_order_id,create_date) VALUES(:p1,:p2,:p3,SYSDATE());";
		evaluateWarningDao.updateBySql(insertSQL, new Parameter(evaluateId,warning.getPromotionCode(),orderAndOldCode));
	}
	
	/**
	 *更新原因
	 */
	@Transactional(readOnly = false)
	public void updateEvaluateResult(String tranId,String country,String res,Date date) {
		String sql ="UPDATE amazoninfo_evaluate_warning  AS a SET a.`result`=:p3,a.`update_date`=:p4 WHERE a.`promotion_id` =:p1 AND a.`country`=:p2 AND a.`warning_sta`='0'";
		evaluateWarningDao.updateBySql(sql, new Parameter(tranId,country,res,date));
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		evaluateWarningDao.deleteById(id);
	}
	
	public List<EvaluateWarning> find() {
		DetachedCriteria dc = evaluateWarningDao.createDetachedCriteria();
		dc.add(Restrictions.eq("warningSta","0"));
		return evaluateWarningDao.find(dc);
	}
	
	public EvaluateWarning findByTranId(String tranId,String country) {
		DetachedCriteria dc = evaluateWarningDao.createDetachedCriteria();
		dc.add(Restrictions.eq("warningSta","0"));
		dc.add(Restrictions.eq("promotionId",tranId));
		dc.add(Restrictions.eq("country",country));
		return evaluateWarningDao.find(dc).get(0);
	}
	
	
}
