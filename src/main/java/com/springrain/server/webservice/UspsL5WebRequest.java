package com.springrain.server.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(targetNamespace="http://www.licheng.com")
public interface UspsL5WebRequest {
	
	@WebMethod
	public String postService(String serviceUrl, String jsonData, String key);
	
	@WebMethod
	public String dpdService(String serviceUrl, String soapData, String key);
}
