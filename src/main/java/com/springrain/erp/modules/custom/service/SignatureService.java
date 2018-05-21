package com.springrain.erp.modules.custom.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.custom.dao.SignatureDao;
import com.springrain.erp.modules.custom.entity.Signature;

/**
 * 用户签名Service
 * @author tim
 * @version 2014-05-16
 */
@Component
@Transactional(readOnly = true)
public class SignatureService extends BaseService {

	@Autowired
	private SignatureDao signatureDao;
	
	public Signature findByUserId(String userId) {
		DetachedCriteria dc = signatureDao.createDetachedCriteria();
		dc.add(Restrictions.eq("userId",userId));
		List<Signature> list =  signatureDao.find(dc);
		if(list.size()==1){
			return list.get(0);
		}
		return null;
	}
	
	public Signature get(String id) {
		return signatureDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(Signature signature) {
		signatureDao.save(signature);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		signatureDao.deleteById(id);
	}
	
}
