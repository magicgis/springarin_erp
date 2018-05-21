package com.springrain.erp.modules.weixin.service;
/**
 * 核心Service类
 * 
 */
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.amazoninfo.entity.SaleReport;
import com.springrain.erp.modules.amazoninfo.htmlunit.GoogleAuthenticator;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.EnterpriseGoalService;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.custom.scheduler.CustomEmailMonitor;
import com.springrain.erp.modules.custom.service.CustomEmailService;
import com.springrain.erp.modules.oa.entity.Roster;
import com.springrain.erp.modules.oa.service.RosterService;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.sys.entity.Office;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.OfficeService;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.weixin.msg.TextMessage;
import com.springrain.erp.modules.weixin.utils.MessageUtil;
import com.springrain.erp.modules.weixin.utils.ParamesAPI;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;
public class CoreService {
	private final static Logger logger = LoggerFactory.getLogger(CoreService.class);
	/**
	 * 处理微信发来的请求
	 * 
	 * @param request
	 * @return xml
	 */
	public String processRequest(String request, SystemService systemService,EnterpriseGoalService goalService,
			AmazonProduct2Service product2Service, SaleReportService saleReportService, OfficeService officeService, 
			CustomEmailService customEmailService,PsiProductGroupUserService productGroupUserService,RosterService rosterService) {
		// xml格式的消息数据
		String respXml = null;
		// 默认返回的文本消息内容
		String respContent = "";
		try {
			// 调用parseXml方法解析请求消息
			Map<String, String> requestMap = MessageUtil.parseXml(request);
			// 发送方帐号
			String fromUserName = requestMap.get("FromUserName");
			// 开发者微信号
			String toUserName = requestMap.get("ToUserName");
			// 消息类型
			String msgType = requestMap.get("MsgType");
			logger.info("接收到的消息类型：" + msgType);

			// 回复文本消息
			TextMessage textMessage = new TextMessage();
			textMessage.setToUserName(fromUserName);
			textMessage.setFromUserName(toUserName);
			textMessage.setCreateTime(new Date().getTime());
			textMessage.setMsgType(MessageUtil.RESP_MESSAGE_TYPE_TEXT);

			String content = requestMap.get("Content");//文本消息的消息内容
			// 文本消息
			if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)) {
				if (content!= null && "1".equals(content)) { //获取动态密码
					//处理业务(动态密码)
					if (systemService != null) {
						User user = systemService.getUserByLoginName(fromUserName);
						if(user != null ){
							respContent = doEventByKey("dynamicPassword", systemService, fromUserName, 
		                			goalService, product2Service, saleReportService);
						}
					}
				}
			}
			// 图片消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_IMAGE)) {
				respContent = "";//"您发送的是图片消息！";
			}
			// 语音消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VOICE)) {
				respContent = "";//"您发送的是语音消息！";
			}
			// 视频消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_VIDEO)) {
				respContent = "";//"您发送的是视频消息！";
			}
			// 地理位置消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
				respContent = "";//"您发送的是地理位置消息！";
			}
			// 链接消息
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_LINK)) {
				respContent = "";//"您发送的是链接消息！";
			}
			// 事件推送
			else if (msgType.equals(MessageUtil.REQ_MESSAGE_TYPE_EVENT)) {
				// 事件类型
				String eventType = requestMap.get("Event");
				logger.info("接收到的事件推送类型：" + eventType);
				// 关注
				if (eventType.equals(MessageUtil.EVENT_TYPE_SUBSCRIBE)) {
					synchronized(this){
						respContent = "";//"谢谢您的关注！";
						User user = systemService.getUserByLoginName(fromUserName);
						if(user == null){	//创建用户
							JSONObject object = WeixinSendMsgUtil.getUserInfo(fromUserName);
							if (object != null) {
								try {
									user = new User();
									user.setLoginName(fromUserName);
									user.setName(object.getString("name"));
									String pwd = WeixinSendMsgUtil.getRandPwd();
									user.setPassword(SystemService.entryptPassword(pwd));
									user.setCompany(officeService.get("1"));	//公司,固定id为1
									String department = object.getString("department");
									department = department.substring(1, department.length()-1).split(",")[0];
									Office office = officeService.get(department);
									if (office == null) {
										throw new RuntimeException("用户"+fromUserName+"部门不存在,部门ID为：" + department);
									}
									user.setOffice(office);
									
									Set<Role> roleList = Sets.newHashSet();
									if ("3".equals(department)) {//销售部自动追加角色：销售人员  其他客服
										roleList.add(systemService.getRole("58b810aaeace4142a2171c173a7c30d1"));
										roleList.add(systemService.getRole("b223271bb677413398d2fdda24593dea"));
									}
									Role role = systemService.getRole("4");
									if (role != null) {
										roleList.add(role);
										user.setRoleList(roleList);
									}
									
									user.setMobile(object.getString("mobile"));
									user.setEmail(object.getString("email"));
									user.setUserType("3");
									user.setNo("123");
									systemService.saveUser(user);
									
									//关注时生成花名册信息开始
									Roster   roster = new Roster();
									roster.setUser(user);
									roster.setOffice(user.getOffice());
									roster.setPhone(user.getMobile());
									roster.setEmail(user.getEmail());
									roster.setWorkSta("1");
									rosterService.save(roster);
									//关注时生成花名册信息开始结束
									
									if ("3".equals(department)) {
										CustomEmailMonitor.initMasterUsers(customEmailService,true);
									}
									WeixinSendMsgUtil.sendTextMsg(null, "tim|leehong", "6", null, ParamesAPI.appId, "新员工" + fromUserName + "已关注微信企业号,在ERP自动创建账号成功！", "0");
									WeixinSendMsgUtil.sendTextMsgToUser(fromUserName, "你已成功关注SpringRain企业号,你的ERP账号信息如下：\n" +
											"登录名：" + fromUserName + "\n密码："+pwd+"\n请尽快在PC端登录ERP在【我的面板-》个人信息】模块修改您的密码\nERP登录地址：\nhttp://192.81.128.219/inateck-erp/a/login");
									logger.info("微信用户" + fromUserName + "关注成功,在ERP自动创建账号成功！");
								} catch (Exception e) {
									WeixinSendMsgUtil.sendTextMsgToUser("tim|leehong", "新员工" + fromUserName + "已关注微信企业号,在ERP自动创建账号失败,请手动创建！");
									logger.error("微信用户" + fromUserName + "关注成功,在ERP自动创建账号失败！", e);
								}
							}
						}
					}
				}
				// 取消关注
				else if (eventType.equals(MessageUtil.EVENT_TYPE_UNSUBSCRIBE)) {
					// 取消订阅后ERP自动删除用户
					User user = systemService.getUserByLoginName(fromUserName);
					if (user != null) {
						try {
							user.setDelFlag("1");
							systemService.saveUser(user);
							//产品线人员关系解除
							productGroupUserService.deleteProductGroupUser(user,null);
							rosterService.updateWorkSta(user.getId(), "0");//花名册为离职状态
							WeixinSendMsgUtil.sendTextMsgToUser("leehong|tim|cici|anne", fromUserName + "已取消关注,在ERP自动删除账号成功！");
							logger.error("微信用户" + fromUserName + "取消关注,在ERP自动删除账号成功！");
						} catch (Exception e) {
							WeixinSendMsgUtil.sendTextMsgToUser("leehong|tim", fromUserName + "已取消关注,在ERP自动删除账号失败,请手动删除！");
							logger.error("微信用户" + fromUserName + "取消关注,在ERP自动删除账号失败！", e);
						}
					}
				}
				// 扫描带参数二维码
				else if (eventType.equals(MessageUtil.EVENT_TYPE_SCAN)) {
					// 处理扫描带参数二维码事件
				}
				// 上报地理位置
				else if (eventType.equals(MessageUtil.REQ_MESSAGE_TYPE_LOCATION)) {
					// 处理上报地理位置事件
				}
				// 自定义菜单
				else if (eventType.equals(MessageUtil.EVENT_TYPE_CLICK)) {
					// 处理菜单点击事件, 事件KEY值，与创建自定义菜单时指定的KEY值对应  
	                String eventKey = requestMap.get("EventKey");
	                logger.info(fromUserName + "触发click事件,EventKey为：" + eventKey);
	                if (eventKey != null) {
	                	respContent = doEventByKey(eventKey, systemService, fromUserName, 
	                			goalService, product2Service, saleReportService);
					}
				}
				// 自定义菜单
				else if (eventType.equals(MessageUtil.EVENT_TYPE_VIEW)) {
					// view事件,不处理
				}
			}
			// 设置文本消息的内容
			textMessage.setContent(respContent);
			// 将文本消息对象转换成xml
			respXml = MessageUtil.textMessageToXml(textMessage);
		} catch (Exception e) {
			logger.error("文本消息对象转换成xml发生异常", e);
		}
		return respXml;
	}
	
	private static String doEventByKey(final String eventKey, SystemService systemService, final String fromUserName,final EnterpriseGoalService goalService,
			final AmazonProduct2Service product2Service, final SaleReportService saleReportService){
		String respContent = "";
		if ("dynamicPassword".equals(eventKey)) {	//忘记密码
			if (systemService != null) {
				User user = systemService.getUserByLoginName(fromUserName);
				if(user != null){
					try {
						//生成6位数随机密码
						String pwd = WeixinSendMsgUtil.getRandPwd();
						user.setDynamicPassword((SystemService.entryptPassword(pwd)));
						user.setPasswordDate(new Date());
						systemService.saveUser(user);
						respContent = "您的ERP密码重置成功,新密码为：\n" + pwd + ",10分钟之内登录成功后生效";
					} catch (Exception e) {
						respContent = "重置密码失败,请重试";
					}
				}
			}
		} else if(eventKey.startsWith("sales_")){	//销量查询,耗时可能大于5s,异步处理
			respContent = querySales(eventKey, fromUserName, goalService, product2Service, saleReportService);
		} else if(eventKey.startsWith("bestseller_")){	//销量排行榜
			respContent = queryBestseller(eventKey, fromUserName, saleReportService);
		} else if("onlinePassword".equals(eventKey)){	//翻墙上网密码
			try {
				User user = systemService.getUser("1");
				//生成6位数随机密码
				respContent = user.getDynamicPassword();
			} catch (Exception e) {
				respContent = "获取翻墙上网密码失败,请重试";
			}
		} else if("googleAuthCode".equals(eventKey)){	//google认证验证码
			try {
				if ("maik".equals(fromUserName)) {
					StringBuffer stringBuffer = new StringBuffer("");
					//总账号
					Map<String, String> parentMap = Maps.newHashMap();
					parentMap.put("欧洲", "2QCI 7VFB JBND X46L MP22 WD25 RIBF T3GD HFC6 J4UW AOAZ XOTK D6WQ");
					parentMap.put("美国", "HXVG 2GRO 6BMH EAPZ XX5F R3H4 BH4G 32PT EYZ5 KYD5 JWIK 7SNI Z4DQ");
					parentMap.put("加拿大", "6PA3 RSIA 2KOL M7AM BV3Y ZHXO JV35 3IUF 2XVN 3SZV B7XU OV6I I53Q");
					parentMap.put("日本", "A3RU D6BE KNMV VZ3N ZLEE LR6M TPTX 2X6X PJKJ ICNN JXTW EDVI UJMQ");
					parentMap.put("墨西哥", "ERDD AVOP QZMA NO3Q EX6V Z3I7 NHVU 4TP7 OMMS MTCJ LUJA AEP3 A22Q");
					stringBuffer.append("总账号校验码：\n");
					for (Entry<String, String> entry : parentMap.entrySet()) {
						String code = GoogleAuthenticator.get_code(entry.getValue())[0];
						stringBuffer.append(entry.getKey() + ":" + code + "\n");
					}
					//运营账号
					Map<String, String> salesMap = Maps.newHashMap();
					salesMap.put("欧洲", "SESZ ASSI 2LTB QTGP 5GUY 5FYD PMAF 2QHT CMXA DUCZ PO76 EAGU MHKA");
					salesMap.put("美州", "5RSO SVAI LVKJ SAI5 QNVD Z3UN 4W5P 2IGC DMB5 OA4J PRER RUI7 5PLQ");
					salesMap.put("日本", "JYAE T2G2 BQZH OWH6 UT5R HIND HZMY VZRN I4IL CYJ3 QMQJ U47Y S76Q");
					stringBuffer.append("\n运营账号校验码：\n");
					for (Entry<String, String> entry : salesMap.entrySet()) {
						String code = GoogleAuthenticator.get_code(entry.getValue())[0];
						stringBuffer.append(entry.getKey() + ":" + code + "\n");
					}
					logger.info("已经为" + fromUserName + "生成亚马逊校验码,开始推送微信消息");
					respContent = "校验码30秒内有效,如校验失败请重新获!\n" + stringBuffer.toString();
				} else {
					User user = systemService.getUserByLoginName(fromUserName);
					Map<String, String> rs = systemService.getAmazonUserByErpInfo(user.getRoleColorNames());
					if (rs == null || rs.size() == 0) {
						respContent = "没有为你找到对应的账号信息";
					} else {
						StringBuffer stringBuffer = new StringBuffer("");
						for (Entry<String, String> entry : rs.entrySet()) {
							String code = GoogleAuthenticator.get_code(entry.getValue())[0];
							stringBuffer.append(entry.getKey() + ":" + code + "\n");
						}
						logger.info("已经为" + fromUserName + "生成亚马逊校验码,开始推送微信消息");
						respContent = "校验码30秒内有效,如校验失败请重新获!\n" + stringBuffer.toString();
						final MailInfo mailInfo = new MailInfo(user.getEmail(), "亚马逊后台登录校验码("+DateUtils.getDate("yyyy/MM/dd")+")", new Date());
						mailInfo.setContent(respContent.replace("\n", "<br/>"));
						new Thread(){
						    public void run(){
								new MailManager().send(mailInfo);
							}
						}.start();
					}
				}
			} catch (Exception e) {
				respContent = "获取验证码失败,请重试";
			}
		}
		return respContent;
	}
	
	private static String querySales(String eventKey, String fromUserName, EnterpriseGoalService goalService,
			AmazonProduct2Service product2Service, SaleReportService saleReportService){
		String goalDateStr = goalService.findTotalByCurrentMonth();
		Map<String, Float> rateMap = product2Service.getRateByDate(goalDateStr);

		//设置起止时间
		SaleReport saleReport =  new SaleReport();
		Date end = new Date();
		saleReport.setStart(DateUtils.addDays(end, -3));
		saleReport.setEnd(end);
		Map<String, Map<String, SaleReport>>  data = saleReportService.getSales(saleReport, rateMap);
		String respContent = "";
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy年MM月dd日");
		Date date = new Date();
		StringBuffer sb = new StringBuffer();
		String tip = "";
		String key = "";
		String total = "";
		if ("sales_today".equals(eventKey)) {
			tip = "今日销量数据(欧元)：\n时间:" + format1.format(date) + "\n\n";
			key = format.format(date);
		} else if("sales_yesterday".equals(eventKey)){
			date = DateUtils.addDays(new Date(), -1);
			tip = "昨日销量数据(欧元)：\n时间:" + format1.format(date) + "\n\n";
			key = format.format(date);
		} else if("sales_before_yesterday".equals(eventKey)){
			date = DateUtils.addDays(new Date(), -2);
			tip = "前日销量数据(欧元)：\n时间:" + format1.format(date) + "\n\n";
			key = format.format(date);
		}
		//折扣订单数量
		Map<String, Integer> promotions = saleReportService.findPromotions(date);
		//折扣ID
		Map<String,Map<String, Integer>> promotionIds = saleReportService.findPromotionsId(date);
		for (Entry<String, Map<String, SaleReport>> dataEntry : data.entrySet()) {
			String string = dataEntry.getKey();
			Map<String, SaleReport> rs = dataEntry.getValue();
			if (!"total".equals(string)) {
				if ("eu".equals(string) || "en".equals(string) || "unEn".equals(string)) {
					continue;
				}
				for (Entry<String, SaleReport> saleReporteEntry : rs.entrySet()) {
					if (key.equals(saleReporteEntry.getKey())) {
						sb.append("国家：" + SystemService.countryNameMap.get(string)+"\n");
						sb.append(saleReporteEntry.getValue().getSales()==null?"销售额:\n": "销售额:" + saleReporteEntry.getValue().getSales()+ "(€)\n");
						sb.append("销量:" + saleReporteEntry.getValue().getSalesVolume() + "\n");
						if (promotions.get(string) != null) {
							sb.append("折扣销量:" + promotions.get(string) + "\n");
						}
						if (promotionIds.get(string) != null && promotionIds.get(string).size() > 0) {
							Map<String, Integer> idAndQuantity = promotionIds.get(string);
							int i = 0;
							sb.append("销量排名前三折扣信息:\n");
							for (Entry<String, Integer> entry : idAndQuantity.entrySet()) {
								if (i == 3) {
									break;
								}
							    String promotionId = entry.getKey();
								if (promotionId.contains(",")) {
									promotionId = promotionId.split(",")[0];//避免字符串太长超出微信限制
								}
								sb.append(promotionId + ",销量:" + entry.getValue() + "\n");
								i++;
							}
						}
						sb.append("\n");
					}
				}
			} else {
				for (Entry<String, SaleReport> entry : rs.entrySet()) {
					if (key.equals(entry.getKey())) {
						total = "总计\n销售额:" + entry.getValue().getSales()+ "(€)\n销量:" + entry.getValue().getSalesVolume() + "\n";
					}
				}
			}
		}
		total = total + "\n";
		respContent = tip + total + sb.toString();
		return respContent;
	}
	
	private static String queryBestseller(String eventKey, String fromUserName, SaleReportService saleReportService){
		
		//设置起止时间
		int num = 0;
		Date date = new Date();
		SimpleDateFormat format1 = new SimpleDateFormat("yyyy年MM月dd日");
		String head = "今日畅销榜信息：\n时间:" + format1.format(date) + "\n\n";
		if ("bestseller_yesterday".equals(eventKey)) {
			num = 1;
			date = DateUtils.addDays(date, -1);
			head = "昨日畅销榜信息：\n时间:" + format1.format(date) + "\n\n";
		} else if ("bestseller_before_yesterday".equals(eventKey)) {
			num = 2;
			date = DateUtils.addDays(date, -2);
			head = "前日畅销榜信息：\n时间:" + format1.format(date) + "\n\n";
		}
		Map<String, Map<String, String>>  data = saleReportService.getBestseller(num);
		StringBuffer sb = new StringBuffer();
		int rank = 0;
		for (Entry<String, Map<String, String>> entry : data.entrySet()) {
			rank++;
			String productName = entry.getKey();
			Map<String, String> rs = entry.getValue();
			String totalNum = rs.get("total");	//总销量
			String detail = rs.get("detail");	//各国明细
			sb.append("NO." + rank + ":" + productName + "\n");
			sb.append("总销量:" + totalNum + "\n");
			for (String info : detail.split(",")) {
				String[] arr = info.split("&");
				sb.append(SystemService.countryNameMap.get(arr[0]) +":"+ arr[1] + "\n");
			}
			sb.append("\n");
		}
		//WeixinSendMsgUtil.sendTextMsgToUser(fromUserName, head + sb.toString());
		return head + sb.toString();
	}
}
