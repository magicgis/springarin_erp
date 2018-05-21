package com.springrain.server.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(targetNamespace="http://www.springrain.eu/cs/erp")
public interface ICommentRequest {
		
		@WebMethod 
		public String getCountComments(String key ,String startDate,String endDate);
		
}
