package com.springrain.erp.modules.psi.service;

import java.text.SimpleDateFormat;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.dao.PsiProductImprovementDao;
import com.springrain.erp.modules.psi.dao.PsiProductImprovementItemDao;
import com.springrain.erp.modules.psi.entity.PsiProductImprove;
import com.springrain.erp.modules.psi.entity.PsiProductImprovement;
import com.springrain.erp.modules.psi.entity.PsiProductImprovementItem;

/**
 * 产品变更Service
 */
@Component
@Transactional(readOnly = true)
public class PsiProductImprovementService extends BaseService {

	@Autowired
	private PsiProductImprovementDao psiProductImprovementDao;

	@Autowired
	private PsiProductImprovementItemDao psiProductImprovementItemDao;

	public PsiProductImprovement get(Integer id) {
		return psiProductImprovementDao.get(id);
	}

	public PsiProductImprovementItem getItemByPermission(Integer id) {
		return psiProductImprovementItemDao.get(id);
	}

	@Transactional(readOnly = false)
	public void save(PsiProductImprovement psiProductImprovement) {
		psiProductImprovementDao.save(psiProductImprovement);
	}

	public Page<PsiProductImprovement> find(Page<PsiProductImprovement> page,
			PsiProductImprovement psiProductImprovement) {
		DetachedCriteria dc = psiProductImprovementDao.createDetachedCriteria();
		if (StringUtils.isNotEmpty(psiProductImprovement.getProductName())) {
			dc.add(Restrictions.like("productName", "%" + psiProductImprovement.getProductName() + "%"));
		}
		if (StringUtils.isNotEmpty(psiProductImprovement.getType())) {
			dc.add(Restrictions.eq("type", psiProductImprovement.getType()));
		}
		if (StringUtils.isNotEmpty(psiProductImprovement.getStatus())) {
			dc.add(Restrictions.eq("status", psiProductImprovement.getStatus()));
		} else {
			dc.add(Restrictions.ne("status", "4"));
		}
		if (psiProductImprovement.getCreateDate() != null) {
			dc.add(Restrictions.ge("createDate", psiProductImprovement.getCreateDate()));
		}
		if (psiProductImprovement.getUpdateDate() != null) {
			dc.add(Restrictions.le("createDate", DateUtils.addDays(psiProductImprovement.getUpdateDate(), 1)));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.desc("id"));
		return psiProductImprovementDao.find(page, dc);
	}

	public boolean notExits(int sort, Integer id) {
		String sql = "SELECT * FROM `psi_product_improvement_item` t WHERE t.`improvement_id`=:p1 AND t.`sort`=:p2";
		List<Object[]> list = psiProductImprovementDao.findBySql(sql, new Parameter(id, sort));
		if (list == null || list.size() == 0) {
			return true;
		}
		return false;
	}

	public String findSalesEmail(String line) {
		if (StringUtils.isEmpty(line)) {
			return null;
		}
		String sql = "SELECT DISTINCT s.`email` FROM `psi_product_type_dict` t, `psi_product_group_user` u, sys_user s"+ 
				" WHERE t.`id`=u.`product_group_id` AND u.`responsible`=s.`id` AND s.`del_flag`='0' AND t.`del_flag`='0' AND u.`del_flag`='0'"+
				" AND t.`name` LIKE :p1 ORDER BY s.`email` DESC";
		List<Object> list = psiProductImprovementDao.findBySql(sql, new Parameter(line+"%"));
		if (list != null && list.size() > 0) {
			return list.get(0).toString();
		}
		return null;
	} 
	  
	public String getTips(String productName) {
		String rs ="";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		DetachedCriteria dc = psiProductImprovementDao.createDetachedCriteria();
		dc.add(Restrictions.or(
				Restrictions.eq("productName", productName),
				Restrictions.like("productName", productName+",%"),
				Restrictions.like("productName", "%," + productName+",%"),
				Restrictions.like("productName", "%," + productName)));
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.add(Restrictions.eq("status", "3"));	//通过的
		dc.addOrder(Order.desc("id"));
		Criteria cri =  dc.getExecutableCriteria(psiProductImprovementDao.getSession());  
        cri.setMaxResults(1);  
		List<PsiProductImprovement> list = cri.list();
		if(list!=null&&list.size()>0){
			PsiProductImprovement im= list.get(0);
			String orderInfo = im.getOrderNo();
			if (StringUtils.isNotEmpty(orderInfo)) {
				orderInfo = orderInfo.trim();
				String sql = "SELECT COUNT(*) FROM lc_psi_purchase_order t WHERE t.`order_no`=:p1";
				List<Object> list2 = psiProductImprovementDao.findBySql(sql, new Parameter(orderInfo));
				if (Integer.parseInt(list2.get(0).toString()) > 0) {
					orderInfo = "<b>变更订单:</b>" +
							"<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/lcPurchaseOrder/view?orderNo="+orderInfo+"'>"+orderInfo+"</a>,";
				} else {
					orderInfo = "<b>变更订单:</b>" +orderInfo+",";
				}
			}
			rs="<b>最近一次产品变更记录：</b>"+sdf.format(im.getImproveDate())+","+(StringUtils.isEmpty(orderInfo)?"":orderInfo)
					+"<b>变更后说明：</b>"+im.getAfterRemark();
		}
		return rs;
	} 

}
