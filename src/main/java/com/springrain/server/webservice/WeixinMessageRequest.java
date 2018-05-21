package com.springrain.server.webservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService(targetNamespace="http://www.licheng.com")
public interface WeixinMessageRequest {
		
	@WebMethod 
	public Boolean sendMessage(String message, String key);
}
