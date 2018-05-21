package com.springrain.erp.modules.weixin.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.EnterpriseGoalService;
import com.springrain.erp.modules.amazoninfo.service.SaleReportService;
import com.springrain.erp.modules.custom.service.CustomEmailService;
import com.springrain.erp.modules.oa.service.RosterService;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.sys.service.OfficeService;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.weixin.service.CoreService;
import com.springrain.erp.modules.weixin.utils.AesException;
import com.springrain.erp.modules.weixin.utils.ParamesAPI;
import com.springrain.erp.modules.weixin.utils.WXBizMsgCrypt;

/**
 * 微信企业号回调模式交互servlet
 * http://127.0.0.1:8080/inateck-erp/servlet/weixinServlet
 */
public class WeixinServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	private final static Logger logger = LoggerFactory.getLogger(WeixinServlet.class);
	private static CoreService coreService = new CoreService();
	
	public WeixinServlet() {
		super();
	}
	
	public void destroy() {
		super.destroy();
	}

	/**
	 * 验证回调接口URL有效性
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 微信加密签名  
        String msg_signature = request.getParameter("msg_signature");
        // 时间戳  
        String timestamp = request.getParameter("timestamp");
        // 随机数  
        String nonce = request.getParameter("nonce");
        // 随机字符串  
        String echostr = request.getParameter("echostr");
        if (msg_signature == null || timestamp == null || nonce == null || echostr == null) {
			logger.info("微信企业号回调接口验证失败,请求参数为空!!!");
        	return;
		}
        // 流
        PrintWriter out = response.getWriter();
        // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败  
        String result = null;
        try {  
        	WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(ParamesAPI.token,ParamesAPI.encodingAESKey,ParamesAPI.corpId);  
        	// 验证URL函数
        	result = wxcpt.verifyURL(msg_signature, timestamp, nonce, echostr);
        } catch (AesException e) {
        	logger.error("微信企业号回调接口验证失败!!!", e);
        }  
        if (result == null) {
        	// result为空，赋予token
        	result = ParamesAPI.token;
        }
        // 拼接请求参数
        String str = msg_signature+" "+timestamp+" "+nonce+" "+echostr;
        // 打印参数+地址+result
        logger.info("微信企业号回调接口验证请求参数 result:"+result+" URL:"+ request.getRequestURL()+" "+"FourParames:"+str);
        out.print(result);
        out.close();
        out = null;
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 将请求、响应的编码均设置为UTF-8（防止中文乱码）
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		// 微信加密签名
		String msg_signature = request.getParameter("msg_signature");
		// 时间戳
		String timestamp = request.getParameter("timestamp");
		// 随机数
		String nonce = request.getParameter("nonce");
		
		//从请求中读取整个post数据
		InputStream inputStream = request.getInputStream();
		String Post = IOUtils.toString(inputStream, "UTF-8");
		
		String msg = "";
		WXBizMsgCrypt wxcpt = null;
		try {
			wxcpt = new WXBizMsgCrypt(ParamesAPI.token,ParamesAPI.encodingAESKey,ParamesAPI.corpId);
			//解密消息
			msg = wxcpt.decryptMsg(msg_signature, timestamp, nonce, Post);
		} catch (AesException e) {
			logger.error("微信企业号消息解密失败!!!", e);
		}
		
		//获取service
		WebApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
		SystemService systemService = context.getBean(SystemService.class);
		EnterpriseGoalService enterpriseGoalService = context.getBean(EnterpriseGoalService.class);
		AmazonProduct2Service amazonProduct2Service = context.getBean(AmazonProduct2Service.class);
		SaleReportService saleReportService = context.getBean(SaleReportService.class);
		OfficeService officeService = context.getBean(OfficeService.class);
		CustomEmailService customEmailService = context.getBean(CustomEmailService.class);
		//删除用户时，删除产品线用户关系
		PsiProductGroupUserService  productGroupUserService = context.getBean(PsiProductGroupUserService.class);
		//花名册类
		RosterService  rosterService = context.getBean(RosterService.class);
		// 调用核心业务类接收消息、处理消息
		String respMessage = coreService.processRequest(msg, systemService, enterpriseGoalService, 
				amazonProduct2Service, saleReportService, officeService, customEmailService,productGroupUserService,rosterService);
		
		// respMessage打印结果
		//logger.info("respMessage打印结果：" + respMessage);
		String encryptMsg = "";
		try {
			//加密回复消息
			encryptMsg = wxcpt.encryptMsg(respMessage, timestamp, nonce);
		} catch (AesException e) {
			logger.error("加密回复消息失败!!!", e);
		}
		
		// 响应消息
		PrintWriter out = response.getWriter();
		out.print(encryptMsg);
		out.close();
	}
	
}
