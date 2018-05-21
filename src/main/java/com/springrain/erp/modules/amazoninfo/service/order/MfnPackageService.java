package com.springrain.erp.modules.amazoninfo.service.order;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.amazoninfo.dao.order.MfnPackageDao;
import com.springrain.erp.modules.amazoninfo.entity.order.MfnOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.MfnPackage;

@Component
@Transactional(readOnly = true)
public class MfnPackageService extends BaseService {

	@Autowired
	private MfnPackageDao mfnPackageDao;

	public MfnPackage get(Integer id) {
		return mfnPackageDao.get(id);
	}

	@Transactional(readOnly = false)
	public void save(MfnPackage mfnPackage) {
		mfnPackageDao.save(mfnPackage);
	}
	
	public Page<MfnPackage> getPackage(Page<MfnPackage> page, MfnPackage mfnPackage) {
		DetachedCriteria dc = mfnPackageDao.createDetachedCriteria();	
		if (mfnPackage.getStart()!= null) {
			dc.add(Restrictions.ge("printTime",mfnPackage.getStart()));
		}
		if (mfnPackage.getPrintTime()!= null) {
			dc.add(Restrictions.le("printTime", DateUtils.addDays(mfnPackage.getPrintTime(),1)));
		}
		
		if(StringUtils.isNotBlank(mfnPackage.getPackageNo())||StringUtils.isNotBlank(mfnPackage.getStatus())||StringUtils.isNotBlank(mfnPackage.getRemark())){
			dc.createAlias("this.orders", "orders");
		}
		if(StringUtils.isNotBlank(mfnPackage.getPackageNo())){//账单号
			 Pattern pattern = Pattern.compile("[0-9]*");
			 if(pattern.matcher(mfnPackage.getPackageNo()).matches()){
				   char strs[] = mfnPackage.getPackageNo().toCharArray();
				   int index = 0;
				   int len=mfnPackage.getPackageNo().length();
				   for(int i=0; i<len; i++){
				     if('0'!=strs[i]){
				       index=i;
				       break;
				     }
				   }
				   String strLast = mfnPackage.getPackageNo().substring(index, len);// 截取字符串
				   dc.add(Restrictions.eq("orders.billNo",Integer.parseInt(strLast)));
			   }else if(mfnPackage.getPackageNo().contains("Test")){
			    	dc.add(Restrictions.eq("orders.billNo",Integer.parseInt(mfnPackage.getPackageNo().replaceAll("Test","").trim())));
			    	dc.add(Restrictions.eq("orders.orderType","1"));
			   }else if(mfnPackage.getPackageNo().contains("Ersatz")){
			    	dc.add(Restrictions.eq("orders.billNo",Integer.parseInt(mfnPackage.getPackageNo().replaceAll("Ersatz","").trim())));
			    	dc.add(Restrictions.or(Restrictions.eq("orders.orderType","2"),Restrictions.eq("orders.orderType","5")));
			   }else if(mfnPackage.getPackageNo().contains("Mfn")){
			    	dc.add(Restrictions.eq("orders.billNo",Integer.parseInt(mfnPackage.getPackageNo().replaceAll("Mfn","").trim())));
			    	dc.add(Restrictions.eq("orders.orderType","3"));
			   }
		}
		if(StringUtils.isNotBlank(mfnPackage.getStatus())){//订单号
			dc.add(Restrictions.eq("orders.orderId",mfnPackage.getStatus()));
		}
		if(StringUtils.isNotBlank(mfnPackage.getCountry())){
			dc.add(Restrictions.eq("country",mfnPackage.getCountry()));
		}
		if(StringUtils.isNotBlank(mfnPackage.getRemark())){//收货人姓名
			dc.createAlias("orders.shippingAddress", "shippingAddress");
			dc.add(Restrictions.like("shippingAddress.name","%"+mfnPackage.getRemark()+"%"));
		}
		
		dc.addOrder(Order.desc("printTime"));
		return mfnPackageDao.find(page, dc);
	}
	
	

	public Map<String,MfnOrder> getUnChangeOrder() {
		Map<String,MfnOrder> map=Maps.newHashMap();
		DetachedCriteria dc = mfnPackageDao.createDetachedCriteria();
		dc.add(Restrictions.ge("printTime", DateUtils.addHours(new Date(),-1)));
		dc.createAlias("this.orders", "orders");
		dc.add(Restrictions.eq("orders.status","0")); 
		dc.add(Restrictions.like("orders.id","%amazon")); 
		List<MfnPackage> mfnPackage=mfnPackageDao.find(dc);
		if(mfnPackage!=null&&mfnPackage.size()>0){
			 for (MfnPackage pack: mfnPackage) {
				List<MfnOrder>  mfnOrders=pack.getOrders();
				for (MfnOrder mfnOrder : mfnOrders) {
					map.put(mfnOrder.getOrderId(),mfnOrder);
				}
			 }
		}
		return map;
	}

}
