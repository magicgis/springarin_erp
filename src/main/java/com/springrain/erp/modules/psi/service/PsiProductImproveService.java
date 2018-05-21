/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.psi.dao.PsiProductImproveDao;
import com.springrain.erp.modules.psi.entity.PsiProductImprove;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * Service
 * @author Michael
 * @version 2015-06-01
 */
@Component
@Transactional(readOnly = true)
public class PsiProductImproveService extends BaseService {
	@Autowired
	private PsiProductImproveDao 			productImproveDao;
	
	public PsiProductImprove get(Integer id) {
		return productImproveDao.get(id);
	} 
	  
	public String getTips(String productName) {
		String rs ="";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		DetachedCriteria dc = productImproveDao.createDetachedCriteria();
		dc.add(Restrictions.eq("productNameColor", productName));
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.desc("id"));
		Criteria cri =  dc.getExecutableCriteria(productImproveDao.getSession());  
        cri.setMaxResults(1);  
		List<PsiProductImprove> list = cri.list();
		if(list!=null&&list.size()>0){
			PsiProductImprove im= list.get(0);
			rs="<b>最近一次产品优化记录：</b>"+sdf.format(im.getImproveDate())+","+(StringUtils.isEmpty(im.getOrderNo())?"":("<b>订单号:</b>" +
					"<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/lcPurchaseOrder/view?orderNo="+im.getOrderNo()+"'>"+im.getOrderNo()+"</a>"))
					+",<b>优化内容：</b>"+im.getImproveContent();
		}
		return rs;
	} 
	
	
	public Page<PsiProductImprove> find(Page<PsiProductImprove> page, PsiProductImprove productImprove) {
		DetachedCriteria dc = productImproveDao.createDetachedCriteria();
		if(StringUtils.isNotEmpty(productImprove.getImproveContent())){
			dc.add(Restrictions.or(Restrictions.like("improveContent", "%"+productImprove.getImproveContent()+"%"),
			Restrictions.like("productNameColor", "%"+productImprove.getImproveContent()+"%"),
			Restrictions.like("orderNo", "%"+productImprove.getImproveContent()+"%")));
		}
		if(StringUtils.isNotEmpty(productImprove.getIsChangeSku())){
			dc.add(Restrictions.eq("isChangeSku", productImprove.getIsChangeSku()));
		}
		dc.add(Restrictions.eq("delFlag", "0"));
		if(StringUtils.isEmpty(page.getOrderBy())){
			dc.addOrder(Order.desc("id"));
		}
		return productImproveDao.find(page, dc);
	}
	
	
	@Transactional(readOnly = false)
	public void save(PsiProductImprove productImprove) {
		//根据不同的颜色，拆分成多个提高
		List<PsiProductImprove>  list = Lists.newArrayList();
		String productName = productImprove.getProductName();
		if(StringUtils.isNotEmpty(productImprove.getColor())){
			for(String color:productImprove.getColor().split(",")){
				PsiProductImprove improve=new PsiProductImprove();
				improve.setOrderNo(productImprove.getOrderNo());
				improve.setImproveContent(productImprove.getImproveContent());
				improve.setIsChangeSku(productImprove.getIsChangeSku());
				improve.setImproveDate(productImprove.getImproveDate());
				improve.setProductNameColor(productName+"_"+color);
				improve.setCreateDate(new Date());
				improve.setCreateUser(UserUtils.getUser());
				improve.setDelFlag("0");
				list.add(improve);
			}
		}else{
			PsiProductImprove improve=new PsiProductImprove();
			improve.setOrderNo(productImprove.getOrderNo());
			improve.setImproveContent(productImprove.getImproveContent());
			improve.setIsChangeSku(productImprove.getIsChangeSku());
			improve.setImproveDate(productImprove.getImproveDate());
			improve.setProductNameColor(productName);
			improve.setCreateDate(new Date());
			improve.setCreateUser(UserUtils.getUser());
			improve.setDelFlag("0");
			list.add(improve);
		}
		productImproveDao.save(list);
	}
	
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		PsiProductImprove productImprove=this.get(id);
		productImprove.setDelFlag("1");
		productImprove.setDeleteDate(new Date());
		productImprove.setDeleteUser(UserUtils.getUser());
		productImproveDao.save(productImprove);
	}

			
}
