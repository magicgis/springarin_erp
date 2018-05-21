package com.springrain.server.webservice;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebService;

import com.springrain.server.pojo.Message;

@WebService
public interface IRequest {
		
		@WebMethod 
		public Boolean sendMessageID(String key,List<Message> lists );
		
}
