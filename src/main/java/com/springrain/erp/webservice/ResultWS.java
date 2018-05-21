package com.springrain.erp.webservice;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.modules.custom.entity.CustomEmail;
import com.springrain.erp.modules.custom.service.CustomEmailService;
import com.springrain.erp.modules.custom.service.EventService;

@WebService
@SOAPBinding(style =SOAPBinding.Style.RPC, use =SOAPBinding.Use.LITERAL)  
public class ResultWS {

	private static String key = Global.getConfig("ws.key");

	@Autowired
	private CustomEmailService customEmailService;

	@Autowired
	private EventService eventService;

	public boolean sendResult(String key, String msgId, String state) {
		if (ResultWS.key.equals(key)) {
			CustomEmail customEmail = customEmailService.get(msgId);
			if (customEmail != null) {
				if ("1".equals(state) || "2".equals(state)) {
					customEmail.setResult("Amazon has been changed back to not deal with!");
				} else {
					customEmail.setResult("Is not the background Amazon-Email!");
				}
				customEmailService.save(customEmail);
				return true;
			}
		}
		return false;
	}

	public String eventIsExistByOrder(@WebParam(name="key")String key,@WebParam(name="orders")String orders) {
		if (ResultWS.key.equals(key) && (orders != null) && (orders.length() > 0)) {
			String[] orderIds = orders.split(",");
			String[] rs = new String[orderIds.length];
			for (int i = 0; i < orderIds.length; ++i) {
				String order = orderIds[i];
				rs[i] = (order + ":" + this.eventService
						.getEventIsExistByOrder(order));
			}
			return JSON.toJSONString(rs);
		}
		return null;
	}
}
