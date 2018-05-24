package com.springrain.erp.modules.dingding.message;

import com.dingtalk.open.client.api.model.corp.MessageBody;

public class MessageDelivery {
	
	public String msgType;
	public MessageBody message;
	
	public MessageDelivery withMessage(String msgType, MessageBody msg) {
		this.msgType = msgType;
		this.message = msg;
		return this;
	}
}
