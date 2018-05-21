package com.springrain.erp.modules.amazoninfo.dao;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Order;

public class MyOrder extends Order{

	private static final long serialVersionUID = 1L;

	public  MyOrder(String propertyName, boolean ascending) {
		super(propertyName, ascending);
	}
	
	@Override
	public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery)
			throws HibernateException {
		return super.getPropertyName()+ (super.isAscending()? "  asc" : "  desc") ;
	}
}
