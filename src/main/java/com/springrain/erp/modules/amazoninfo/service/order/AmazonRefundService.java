package com.springrain.erp.modules.amazoninfo.service.order;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.hibernate.Hibernate;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.dao.order.AmazonRefundDao;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonRefund;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazoninfoRefundItem;

@Component
@Transactional(readOnly = true)
public class AmazonRefundService extends BaseService {

	@Autowired
	private AmazonRefundDao amazonRefundDao;

	public AmazonRefund get(Integer id) {
		return amazonRefundDao.get(id);
	}
	
	public Page<AmazonRefund> find(Page<AmazonRefund> page, AmazonRefund amazonRefund) {
		DetachedCriteria dc = amazonRefundDao.createDetachedCriteria();
		
		if (amazonRefund.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",amazonRefund.getCreateDate()));
		}
		if (amazonRefund.getEndDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(amazonRefund.getEndDate(),1)));
		}
		if(StringUtils.isNotEmpty(amazonRefund.getAmazonOrderId())){
			dc.add(Restrictions.eq("amazonOrderId", amazonRefund.getAmazonOrderId()));
		}
		if(StringUtils.isNotEmpty(amazonRefund.getCountry())){
			dc.add(Restrictions.eq("country", amazonRefund.getCountry()));
		}
		if(StringUtils.isNotBlank(amazonRefund.getRefundState())){
			dc.add(Restrictions.eq("refundState", amazonRefund.getRefundState()));
		}
		if (amazonRefund.getCreateUser()!=null && StringUtils.isNotEmpty(amazonRefund.getCreateUser().getId())){
			dc.add(Restrictions.eq("createUser", amazonRefund.getCreateUser()));
		}
		if (amazonRefund.getOperUser()!=null && StringUtils.isNotEmpty(amazonRefund.getOperUser().getId())){
			dc.add(Restrictions.eq("operUser", amazonRefund.getOperUser()));
		}
		if(StringUtils.isNotBlank(amazonRefund.getIsTax())){
			dc.createAlias("this.items", "item");
			dc.add(Restrictions.eq("item.productName",amazonRefund.getIsTax()));
		}
		return amazonRefundDao.find(page, dc);
	}
	
	
	public List<AmazonRefund> getRefundRecord(String amazonOrderId){
		DetachedCriteria dc = amazonRefundDao.createDetachedCriteria();
		dc.add(Restrictions.eq("amazonOrderId", amazonOrderId));
		//dc.add(Restrictions.like("result","%&lt;MessagesProcessed&gt;1&lt;/MessagesProcessed&gt;%"));
		//dc.add(Restrictions.like("result","%&lt;MessagesSuccessful&gt;1&lt;/MessagesSuccessful&gt;%"));
		dc.add(Restrictions.like("result","%&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;%"));
		return amazonRefundDao.find(dc);
	}
	
	public List<Object[]> getsettlementreport(String amazonOrderId){
		String sql=" SELECT CONCAT(p.`product_name`,CASE  WHEN p.`color`='' THEN '' ELSE CONCAT('_',p.`color`) END) NAME,r.`posted_date`,t.`principal`,t.`shipping` FROM settlementreport_order r JOIN settlementreport_item t ON r.id=t.`order_id` JOIN psi_sku p ON p.sku=t.`sku` AND p.`country`=r.`country` AND p.`del_flag`='0' "+
                 " WHERE r.`amazon_order_id`=:p1 AND r.`type`='Refund'  AND (t.`principal` IS NOT NULL OR t.`shipping` IS NOT NULL) ";
		return amazonRefundDao.findBySql(sql,new Parameter(amazonOrderId));
	}
	
	public List<AmazonRefund> getAllRefundRecord(){
		DetachedCriteria dc = amazonRefundDao.createDetachedCriteria();
		dc.add(Restrictions.like("result","%&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;%"));
		List<AmazonRefund> refundList=amazonRefundDao.find(dc);
		for (AmazonRefund amazonRefund : refundList) {
			Hibernate.initialize(amazonRefund.getItems());
		}
		return refundList;
	}
	
	
	public boolean hasRefundRecord(String amazonOrderId){
		DetachedCriteria dc = amazonRefundDao.createDetachedCriteria();
		dc.add(Restrictions.eq("amazonOrderId", amazonOrderId));
		dc.add(Restrictions.like("result","%&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;%"));
		long refunds =  amazonRefundDao.count(dc);
		if(refunds==0){
			String sql = "SELECT COUNT(1) FROM custom_event_manager a WHERE a.`type` IN ('1','2') AND a.`del_flag` = '0' AND a.`invoice_number` LIKE :p1";
			List<Object> list = amazonRefundDao.findBySql(sql, new Parameter("%"+amazonOrderId+"%"));
			refunds += Integer.parseInt(list.get(0).toString());
		}
		return refunds>0;
	}

	@Transactional(readOnly = false)
	public  void save(AmazonRefund amazonRefund) {
		amazonRefundDao.save(amazonRefund);
	}
	
	@Transactional(readOnly = false)
	public  void updateState(AmazonRefund amazonRefund) {
		String sql="update amazoninfo_refund set refund_state='2',oper_user=:p1 where id=:p2 ";
		amazonRefundDao.updateBySql(sql, new Parameter(amazonRefund.getOperUser().getId(),amazonRefund.getId()));
	}
	
	public List<Object> getPassOrderIdByTax(String country){
		
		String sql="SELECT DISTINCT d.`amazon_order_id` FROM amazoninfo_refund d JOIN amazoninfo_refund_item t ON d.id=t.`refund_id` "+
              " WHERE d.`refund_state`='1' AND (t.`remark` LIKE '%退税%' OR t.`remark` LIKE '%tax%') and d.country=:p1 ";
		return amazonRefundDao.findBySql(sql, new Parameter(country));
	}
	
	public String findExistOrder(String amazonOrderId,Float refundMoney){
		String sql="SELECT 1 FROM amazoninfo_refund d WHERE d.`amazon_order_id`=:p1  AND refund_state in ('0','1') AND  refund_total=:p2 and create_date>=:p3 ";
		List<String> list=amazonRefundDao.findBySql(sql, new Parameter(amazonOrderId,refundMoney,DateUtils.addHours(new Date(),-1)));
		if(list!=null&&list.size()>0){
			return "0";
		}
		return "1";
	}
	
	public List<AmazoninfoRefundItem>  findRefund(AmazonRefund amazonRefund){
		List<AmazoninfoRefundItem> list=Lists.newArrayList();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql="SELECT CONCAT(p.`product_name`,CASE WHEN p.`color`!='' THEN CONCAT ('_',p.`color`) ELSE '' END) NAME,r.`posted_date`,IFNULL(t.`principal`,0),IFNULL(t.`shipping`,0) FROM amazoninfo_financial r JOIN amazoninfo_financial_item t ON r.id=t.`order_id` "+ 
				" JOIN psi_sku p ON t.sku=p.sku AND p.country=:p2 AND p.del_flag='0'  "+
				" WHERE r.`amazon_order_id` = :p1 AND r.`type`='Refund' AND r.`posted_date`<CURDATE()  "+
				" UNION ALL "+
				" SELECT t.product_name,r.create_date,(CASE WHEN refund_type='principal' THEN -money ELSE 0 END) principal,(CASE WHEN refund_type='Shipping' THEN -money ELSE 0 END) shipping "+
				" FROM amazoninfo_refund r "+
				" JOIN amazoninfo_refund_item  t ON r.id=t.`refund_id` "+
				" WHERE r.`amazon_order_id`=:p1  AND r.create_date>CURDATE()  "+
				" AND result LIKE '%&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;%' "+
				" GROUP BY t.product_name,r.create_date ";
		List<Object[]> tempList=amazonRefundDao.findBySql(sql,new Parameter(amazonRefund.getAmazonOrderId(),amazonRefund.getCountry()));
		if(tempList.size()>0){
			for (Object[] obj: tempList) {
				String name=obj[0].toString();
				String date=dateFormat.format((Timestamp)obj[1]);
				Float principal=Float.parseFloat(obj[2].toString());
				Float shipping=Float.parseFloat(obj[3].toString());
				AmazoninfoRefundItem item=new AmazoninfoRefundItem();
				item.setProductName(name);
				item.setRemark(date);
				item.setMoney(principal);
				item.setShippingMoney(shipping);
				list.add(item);
			}
		}
		return list;
	}
}
