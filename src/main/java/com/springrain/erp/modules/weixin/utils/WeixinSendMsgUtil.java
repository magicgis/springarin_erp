package com.springrain.erp.modules.weixin.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.SpringContextHolder;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.weixin.utils.ParamesAPI;
import com.springrain.server.webservice.WeixinMessageRequest;

/**
 * 向微信企业成员主动发送消息工具类
 * @author lee
 * @date 2015-12-15
 */
public class WeixinSendMsgUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(WeixinSendMsgUtil.class);
	
	public static String messageUser = "leehong";	//系统通知消息的接收用户,多个已“|”隔开
	

	public static void main(String[] args) {
		sendTextMsgToUser("leehong", "R-13.99Euro off WP1005 AMZtrackerââ0906-FR测试信息\n<a href='http://www.baidu.com'>换行测试</a>");
		/*String content = "R-13.99Euro off WP1005 AMZtrackerââ0906-FR,销量:30\nR-12.99Euro off WP1004-W AMZtrackerââ0906-FR,销量:20\n";
		//特殊字符过多时json转String失败，
		JSONObject contentJson = new JSONObject();
		contentJson.put("content", content);
		System.out.println(contentJson.toJSONString());;*/
	}

	/**
	 * 
	 * @param toUser	微信企业号成员ID(erp内对应为登录账号)列表（多个接收者用‘|’分隔，最多1000个）。指定为@all，则向关注该企业应用的全部成员发送
	 * @param content	消息内容,可使用\n进行换行,支持超链接即<a>标签
	 * @return	true：成功   false：失败
	 */
	public static boolean sendTextMsgToUser(final String toUser, final String content) {
		new Thread(){
		    public void run(){
		    	String token = getToken();
		    	sendTextMsg(token, toUser, content);
			}
		}.start();
		return true;
	}

	/**
	 * 获取交互令牌,87获取，服务器上面直接返回null
	 * 
	 */
	public static String getToken() {
		String serverId = Global.getConfig("server.id");
		if ("1".equals(serverId)) {	//国外服务器不能连接微信服务器
			return null;
		}
		try {
			URL url = new URL(
					"https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + ParamesAPI.corpId + "&corpsecret=" + ParamesAPI.secrect);
			URLConnection connection = url.openConnection();
			connection.connect();

			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuffer result = new StringBuffer();
			while ((line = bufferedReader.readLine()) != null) {
				result.append(line);
			}
			bufferedReader.close();
			
			//发送消息
			JSONObject object = (JSONObject) JSON.parse(result.toString());
			if (object.get("access_token") == null) {
				logger.info("获取交互令牌失败,错误代码：" + object.getString("errcode") + " 错误消息：" + object.getString("errmsg"));
				return null;
			}
			return object.get("access_token").toString();
		} catch (Exception e) {
			logger.error("获取交互令牌失败", e);
		}
		return null;
	}
	
	private static boolean sendTextMsg(String token, String toUser, String content) {
		return sendTextMsg(token, toUser, null, null, ParamesAPI.appId, content, "0");
	}
	/**
	 * 发送文本消息
	 * @param token		令牌,实时获取
	 * @param toUser	微信企业号成员ID(erp内对应为登录账号)列表（多个接收者用‘|’分隔，最多1000个）。指定为@all，则向关注该企业应用的全部成员发送
	 * @param toParty	部门ID列表，多个接收者用‘|’分隔，最多支持100个。当touser为@all时忽略本参数
	 * @param toTag		标签ID列表，多个接收者用‘|’分隔。当touser为@all时忽略本参数
	 * @param agentId	企业应用的id，整型。可在应用的设置页面查看
	 * @param content	消息内容,可使用\n进行换行,支持超链接即<a>标签
	 * @param safe		表示是否是保密消息，0表示否，1表示是，默认0
	 */
	public static boolean sendTextMsg(final String token,final String toUser,final String toParty,final String toTag, 
			final String agentId, final String content,final String safe) {
		new Thread(){
		    public void run(){
		    	String serverId = Global.getConfig("server.id");
				if ("1".equals(serverId)) {	//国外服务器不能连接微信服务器
					sendByWs(token, toUser, toParty, toTag, agentId, content, safe, 0);
				} else {
					sendByLocal(token, toUser, toParty, toTag, agentId, content, safe, 0);
				}
			}
		}.start();
		return true;
	}
	
	//erp使用,调用WS接口转87服务推送消息
	private static boolean sendByWs(String token,String toUser,String toParty,String toTag, 
			String agentId, String content,String safe, int num) {
		if (num > 3) {
			return false;
		}
		String key = "Hip6k8wOkQ2qb2*Bb";
		try {
			JSONObject object = new JSONObject();
			object.put("touser", toUser);
			object.put("toparty", toParty);
			object.put("totag", toTag);
			object.put("msgtype", "text");
			object.put("agentid", agentId);
			JSONObject contentJson = new JSONObject();
			contentJson.put("content", "springraincontent");
			object.put("text", contentJson);
			object.put("safe", safe);	//是否加密发送 默认0 不加密  1：加密
			
			String sendContent = object.toJSONString();
			//特殊字符(德法语重音字符等)过多时json转String失败,直接替换发送内容
			sendContent = sendContent.replace("springraincontent", content);
			sendContent = Encodes.getBASE64String(sendContent.getBytes());
			WeixinMessageRequest request = SpringContextHolder.getBean(WeixinMessageRequest.class);
			//调用备份服务器提供的接口推送
			request.sendMessage(sendContent, key);
		} catch (Exception e) {
			num++;
			logger.error("第"+num+"次向微信企业号用户发送消息异常", e);
			try {
				Thread.sleep(1000 * 60);
			} catch (InterruptedException e1) {}//休眠一分钟再试
			sendByWs(token, toUser, toParty, toTag, agentId, content, safe, num);
		}
    	return false;
	}
	
	private static boolean sendByLocal(String token,String toUser,String toParty,String toTag, 
			String agentId, String content,String safe, int num) {
		if (num > 3) {
			return false;
		}
		if (StringUtils.isEmpty(token)) {
			token = getToken();
		}
		try {
			URL url = new URL("https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + token);
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			connection.connect();
			
			OutputStream outputStream = connection.getOutputStream();
			JSONObject object = new JSONObject();
			object.put("touser", toUser);
			object.put("toparty", toParty);
			object.put("totag", toTag);
			object.put("msgtype", "text");
			object.put("agentid", agentId);
			JSONObject contentJson = new JSONObject();
			contentJson.put("content", "springraincontent");
			object.put("text", contentJson);
			object.put("safe", safe);	//是否加密发送 默认0 不加密  1：加密
			
			String sendContent = object.toJSONString();
			//特殊字符(德法语重音字符等)过多时json转String失败,直接替换发送内容
			sendContent = sendContent.replace("springraincontent", content);
			outputStream.write(sendContent.getBytes("UTF-8"));
			outputStream.flush();
			outputStream.close();
		    //接受连接返回参数
		    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    StringBuffer result = new StringBuffer();
		    String line;
	        while ((line = bufferedReader.readLine()) != null) {
	            result.append(line);
	        }
	        bufferedReader.close();
	        object = (JSONObject) JSON.parse(result.toString());
			if (StringUtils.isNotBlank(object.getString("errcode"))
					&& "0".equals(object.getString("errcode").toString())) {
				return true;
			} else {
				num++;
				Thread.sleep(1000 * 60);//休眠一分钟再试
				token = getToken();
				sendByLocal(token, toUser, toParty, toTag, agentId, content, safe, num);
			}
		} catch (Exception e) {
			num++;
			logger.error("第"+num+"次向微信企业号用户发送消息异常", e);
			try {
				Thread.sleep(1000 * 60);
			} catch (InterruptedException e1) {}//休眠一分钟再试
			token = getToken();
			sendByLocal(token, toUser, toParty, toTag, agentId, content, safe, num);
		}
    	return false;
	}
	
	//用于本地87发送从ERP服务器上面接收过来的微信推送消息
	public static boolean sendErpTxt(String token, String sendContent, int num) {
		if (num > 3) {
			return false;
		}
		if (StringUtils.isEmpty(token)) {
			token = getToken();
		}
		try {
			URL url = new URL("https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=" + token);
			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			connection.connect();
			
			OutputStream outputStream = connection.getOutputStream();
			outputStream.write(sendContent.getBytes("UTF-8"));
			outputStream.flush();
			outputStream.close();
		    //接受连接返回参数
		    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		    StringBuffer result = new StringBuffer();
		    String line;
	        while ((line = bufferedReader.readLine()) != null) {
	            result.append(line);
	        }
	        bufferedReader.close();
	        JSONObject object = (JSONObject) JSON.parse(result.toString());
			if (StringUtils.isNotBlank(object.getString("errcode"))
					&& "0".equals(object.getString("errcode").toString())) {
				return true;
			} else {
				num++;
				Thread.sleep(1000 * 60);//休眠一分钟再试
				sendErpTxt(token, sendContent, num);
			}
		} catch (Exception e) {
			num++;
			logger.error("第"+num+"次向微信企业号用户发送消息异常", e);
			try {
				Thread.sleep(1000 * 60);
			} catch (InterruptedException e1) {}//休眠一分钟再试
			sendErpTxt(token, sendContent, num);
		}
    	return false;
	}

	/**
	 * 获取用户信息
	 * 
	 */
	public static JSONObject getUserInfo(String userId) {
		try {
			URL url = new URL(
					"https://qyapi.weixin.qq.com/cgi-bin/user/get?access_token="+getToken()+"&userid=" + userId);
			URLConnection connection = url.openConnection();
			connection.connect();

			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			String line;
			StringBuffer result = new StringBuffer();
			while ((line = bufferedReader.readLine()) != null) {
				result.append(line);
			}
			bufferedReader.close();
			
			//发送消息
			JSONObject object = (JSONObject) JSON.parse(result.toString());
			if (object.getIntValue("errcode") != 0) {
				logger.info("获取微信用户("+userId+")信息失败,错误代码：" + object.getString("errcode") + " 错误消息：" + object.getString("errmsg"));
				return null;
			}
			return object;
		} catch (Exception e) {
			logger.error("获取微信用户("+userId+")信息失败", e);
		}
		return null;
	}
	
	/**
	 * 生成6位数随机密码
	 * @return
	 */
	public static String getRandPwd() {
//		char[] codeSeq = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J',
//				'K', 'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
//				'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		char[] codeSeq = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		Random random = new Random();
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < 6; i++) {
			String r = String.valueOf(codeSeq[random.nextInt(codeSeq.length)]);	//random.nextInt(10));
			s.append(r);
		}
		return s.toString();
	}

}
