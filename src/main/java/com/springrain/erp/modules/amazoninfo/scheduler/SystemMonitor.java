package com.springrain.erp.modules.amazoninfo.scheduler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.weixin.utils.ParamesAPI;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;

/**
 * 系统运行状态监控
 */
public class SystemMonitor {

	@Autowired
	private AmazonOrderService amazonOrderService;
	
	@Autowired
	AmazonAccountConfigService configService;

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private static List<String> errorInfoList = Lists.newArrayList();
	private static String logPath = "/opt/apache-tomcat-7.0.53/logs/jeesite.log";
	
	static {
		errorInfoList.add("Java heap space");
		errorInfoList.add("Could not open connection");
		//errorInfoList.add("abandon connection");
	}
	
	public void otterMonitor(){
		Timestamp lastUpdateTime = amazonOrderService.getMaxOrderDate(null);
		long diff=DateUtils.parseDate(DateUtils.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss")).getTime()-lastUpdateTime.getTime();
		long min=diff/(1000*60);
		if(min>120){	//超过两小时
			WeixinSendMsgUtil.sendTextMsgToUser("leehong|tim|eileen", "Otter同步订单延时超过两小时,请查看原因");
		}
	}
	
	public void monitor(){
		logger.info("监控系统运行状态...");
		String user = "tim|leehong|eileen";
		String info = "ERP服务运转正常,请知悉！";
		if (DateUtils.isHoliday()) {
			WeixinSendMsgUtil.sendTextMsgToUser(user, info);
		} else{	//非节假限时推送信息
			Calendar calendar = Calendar.getInstance();
			if (calendar.get(Calendar.HOUR_OF_DAY) >= 18) {
				WeixinSendMsgUtil.sendTextMsgToUser(user, info);
			}
		}
		List<AmazonAccountConfig> list = configService.findAllConfig();
		Set<String> clientIpSet = Sets.newHashSet();
		for (AmazonAccountConfig config : list) {
			if ("0".equals(config.getState()) && StringUtils.isNotBlank(config.getServerIp())) {
				clientIpSet.add(config.getServerIp());
			}
		}
		for (String ip : clientIpSet) {
			String url = "http://"+ip+":8080";
			String rs = sendGet(url, null);
			if (rs == null) {
				logger.info("监控ERP客户端运行异常,ip为:"+ip);
				//发送微信通知
				String content = "监控程序连接ERP客户端超时,ip为:"+ip+",请知悉!";
				WeixinSendMsgUtil.sendTextMsg(WeixinSendMsgUtil.getToken(), null, "7", null, ParamesAPI.appId, content, "0");
			}
		}
	}
	
	public void monitorTimes(){
		logger.info("监控系统运行状态...");
		/*String info = "ERP服务运转异常,数据库读取数据失败！";
		try {
			List<Object> list = systemService.findDual();
			//读取不到信息,自动重启
			if (list == null || list.size() == 0) {
				restartService(info);
				return;
			}
		} catch (Exception e) {
			logger.error(info, e);
			//读取信息失败,自动重启
			restartService(info);
			return;
		}*/
		//数据读取正常,解析日志文件是否含有致命异常
		try {
			//解析文件
			File txtFile = new File(logPath);
			if (txtFile.exists()) {
				// 进行处理
				List<String> lines = FileUtils.readLines(txtFile);
				for (String line : lines) {
					for (String errorInfo : errorInfoList) {
						if (line.contains(errorInfo)) {
							restartService(line);
							return;
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("解析日志文件异常！！", e);
			WeixinSendMsgUtil.sendTextMsgToUser(WeixinSendMsgUtil.messageUser, "系统监控解析ERP日志文件异常！！");
		}
		logger.info("系统监控完毕");
	}
	
	private void restartService(String info){
		try {
			//去掉邮件定时器
			/*File file = new File("/opt/apache-tomcat-7.0.53/webapps/inateck-erp/WEB-INF/classes/spring-context-quartz.xml");
			String lines = FileUtils.readFileToString(file, "UTF-8");
			lines = lines.replace("<ref local=\"sayRunTrigger\"/>", "");
			FileUtils.writeStringToFile(file, lines, "UTF-8");*/

			//发送微信通知
			String content = "系统出现致命异常,已尝试自行重启,请知悉并关注系统状态\n异常信息:" + info;
			WeixinSendMsgUtil.sendTextMsg(WeixinSendMsgUtil.getToken(), null, "7", null, ParamesAPI.appId, content, "0");
			String shpath="/opt/restart.sh";   //程序路径
		    String command2 = "sh " + shpath;
		    Runtime.getRuntime().exec(command2);
		} catch (Exception e) {
			logger.error("系统重启失败", e);
			String content = "系统出现致命异常,系统尝试自行重启已失败,请尽快重启服务\n异常信息:" + info;
			WeixinSendMsgUtil.sendTextMsg(WeixinSendMsgUtil.getToken(), null, "7", null, ParamesAPI.appId, content, "0");
		}
	}

	public String sendGet(String url, String params) {
		return sendGet(url, params, 0);
	}

	/**
	 * 向指定URL发送GET方法的请求
	 * 
	 * @param url
	 *            发送请求的URL
	 * @param param
	 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
	 * @return URL 所代表远程资源的响应结果
	 */
	private String sendGet(String url, String param, int num) {
		if (num > 2) {
			return null;
		}
		String result = "";
		BufferedReader in = null;
		try {
			String urlNameString = url;
			if (StringUtils.isNotEmpty(param)) {
				urlNameString = urlNameString + "?" + param;
			}
			URL realUrl = new URL(urlNameString);
			// 打开和URL之间的连接
			URLConnection connection = realUrl.openConnection();
			// 设置通用的请求属性
			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alive");
			connection.setConnectTimeout(1000 * 30);
			connection.setReadTimeout(1000 * 30);
			// 建立实际的连接
			connection.connect();

			String redirect = connection.getHeaderField("Location");
			if (redirect != null) {
				connection = new URL(redirect).openConnection();
				connection.setRequestProperty("accept", "*/*");
				connection.setRequestProperty("connection", "Keep-Alive");
				// 建立实际的连接
				connection.connect();
			}

			// 定义 BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			logger.error(url+"连接异常", e);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e1) {
			}
			return sendGet(url, param, ++num);
		}
		// 使用finally块来关闭输入流
		finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e2) {
				logger.error("关闭流异常", e2);
			}
		}
		return result;
	}
}
