package com.springrain.erp.modules.psi.scheduler;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.google.common.collect.Maps;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonAccountConfigService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.custom.service.AutoReplyService;
import com.springrain.erp.modules.custom.service.EventService;
import com.springrain.erp.modules.psi.entity.FbaInbound;
import com.springrain.erp.modules.psi.entity.FbaInboundItem;
import com.springrain.erp.modules.psi.service.FbaInboundService;
import com.springrain.erp.modules.psi.service.PsiInventoryService;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.psi.service.PsiTransportOrderService;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;

public class FbaInboundSynMonitor {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(FbaInboundSynMonitor.class);
	
	@Autowired
	private FbaInboundService fbaInboundService;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private AmazonProduct2Service amazonProduct2Service;
	
	@Autowired
	private PsiTransportOrderService transportOrderService;
	
	@Autowired
	private EventService eventService;
	
	@Autowired
	private AmazonOrderService amazonOrderService;
	
	@Autowired
	private AutoReplyService autoReplyService;
	@Autowired
	private PsiProductGroupUserService 	    groupUserService;
	@Autowired
	private PsiProductTypeGroupDictService 	   groupDictService;
	@Autowired
	private PsiInventoryService  psiInventoryService;
	@Autowired
	private AmazonAccountConfigService amazonAccountConfigService;
	
	@Autowired
	private MailManager mailManager;
	
	/**
	 * P0贴进度监控
	 * 1、当天中国时间19点未响应提醒(前提时库存足够)
	 * 2、早上9点提醒前一日响应的P0贴未出库
	 */
	public void fbaLevelMonitor(){
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(Calendar.HOUR_OF_DAY) < 12) {
			shipMonitor();	//上午进行一次发货监控
		} else if (calendar.get(Calendar.HOUR_OF_DAY) > 17){
			responseMonitor();	//响应监控，北京时间晚上7点提醒P0当天未响应
		}
	}
	
	private void responseMonitor(){
		List<FbaInbound> responseList = fbaInboundService.findResponseMonitor("DE", "0");
   	 	//查询sku库存
		Map<String, Integer> skuQty = psiInventoryService.findSkuNewQuantity(19);
		Map<Integer, String> canConfirmMap = Maps.newHashMap();
		for (FbaInbound inbound : responseList) {
			for (FbaInboundItem item : inbound.getItems()) {
				if (skuQty.get(item.getSku())==null || item.getQuantityShipped()>skuQty.get(item.getSku())) {
					LOGGER.info(item.getSku()+"(帖子编号："+inbound.getId()+")库存数不足,需求发货数："+item.getQuantityShipped()+",当前库存数为:"+ skuQty.get(item.getSku()));
					canConfirmMap.put(inbound.getId(), "1");//库存不足,不能确认
					break;
				}
			}
		}
		StringBuilder contents = new StringBuilder("");
		StringBuilder sb = new StringBuilder("");	//微信
		for(FbaInbound inbound: responseList){
			if ("1".equals(canConfirmMap.get(inbound.getId()))) {
				continue;//库存不足,不预警
			}
			try{
				sb.append("\n\nShipmentName:" + inbound.getShipmentName());
				contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
				contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'><td colspan='3' style='text-align:center'>"+inbound.getShipmentName()+"</td></tr>");
				contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#f2f4f6;color:#666;'>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>SKU</th>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>FNSKU</th>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>数量</th></tr>");
				for (FbaInboundItem item: inbound.getItems()) {
					sb.append("\nsku:" + item.getSku() + ",数量：" + item.getQuantityShipped());
					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getSku()+"</td>");
					contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getFnSku()+"</th>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getQuantityShipped()+"</td></tr>");
			    }
			    contents.append("</table><br/>");
			}catch(Exception ex){
				LOGGER.error("Note:FBA P0响应贴监控异常", ex);
			}
		}
		if (StringUtils.isNotEmpty(contents.toString())) {
			String content = "<p><span style='font-size:20px'>Hi all,<br/>&nbsp;&nbsp;&nbsp;&nbsp;以下是P0级FBA贴未确认明细,请立刻通知对应仓库人员进行确认并发货,请知悉。</span></p>";
			final MailInfo notice = new MailInfo("george@inateck.com,ethan@inateck.com,maik@inateck.com,bella@inateck.com","P0级FBA贴未确认提醒("+DateUtils.getDate("yyyy/MM/dd")+")",new Date());
			notice.setCcToAddress("it@inateck.com");
			notice.setContent(content + contents.toString());
			new Thread(){
			    public void run(){
			    	mailManager.send(notice);
				}
			}.start();
			WeixinSendMsgUtil.sendTextMsgToUser("ethan|bella|maik|leehong|tim", "以下是监控到P0级未确认FBA贴明细,请立刻通知对应仓库人员进行确认并发货"+sb.toString());
		}
	}
	
	private void shipMonitor(){
		List<FbaInbound> shipList = fbaInboundService.findShipMonitor("DE", "0");
		StringBuilder contents = new StringBuilder("");
		for(FbaInbound inbound: shipList){
			try{
				contents.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;' cellpadding='0' cellspacing='0' >");
				contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'><td colspan='3' style='text-align:center'>"+inbound.getShipmentName()+"</td></tr>");
				contents.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#f2f4f6;color:#666;'>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>SKU</th>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>FNSKU</th>");
				contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>数量</th></tr>");
				for (FbaInboundItem item: inbound.getItems()) {
					contents.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:#666; '>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getSku()+"</td>");
					contents.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getFnSku()+"</th>");
					contents.append("<td style='border-left:1px solid;border-top:1px solid;color:#666;'>"+item.getQuantityShipped()+"</td></tr>");
			    }
			    contents.append("</table><br/>");
			}catch(Exception ex){
				LOGGER.error("Note:FBA P0贴24小时发货监控异常", ex);
			}
		}
		if (StringUtils.isNotEmpty(contents.toString())) {
			String content = "<p><span style='font-size:20px'>Hi all,<br/>&nbsp;&nbsp;&nbsp;&nbsp;以下是P0级FBA贴当天未发货明细,请知悉。</span></p>";
			final MailInfo notice = new MailInfo("george@inateck.com,ethan@inateck.com,maik@inateck.com,bella@inateck.com","P0级FBA贴超过24小时未发货提醒("+DateUtils.getDate("yyyy/MM/dd")+")",new Date());
			notice.setCcToAddress("it@inateck.com");
			notice.setContent(content + contents.toString());
			new Thread(){
			    public void run(){
			    	mailManager.send(notice);
				}
			}.start();
		}
	}
}
