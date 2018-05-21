package com.springrain.erp.modules.amazoninfo.scheduler;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.amazoninfo.service.AmazonStorageMonitorService;

/**
 * 库存预警Monitor
 */
@Component
@Transactional(readOnly = true)
public class AmazonStorageMonitor extends BaseService{

    @Autowired
    private AmazonStorageMonitorService amazonStorageMonitorService;
    @Autowired
    private MailManager mailManager;

    public void sendEmailOfStorage(){
        StringBuilder sb = new StringBuilder();
        if(amazonStorageMonitorService.contentOfStorageBySale().length()!=0){
            sb.append("Hi,<br/></br>以下是产品超过3个月库存总数 /31平均销量 >120天  ，其他市场FBA库存可售天<90天详情，供市场间调货参考(红色部分为淘汰产品)：<br/>"+amazonStorageMonitorService.contentOfStorageBySale()+"<br/><br/>");
        }
        if(amazonStorageMonitorService.contentOfStorage().length()!=0){
            sb.append("以下是 |所有产品|  的库存详情(红色部分为淘汰产品)：<br/>"+amazonStorageMonitorService.contentOfStorage());
        }
        if(sb.toString().length()!=0){       
            MailInfo mailInfo = new MailInfo("amazon-sales@inateck.com", "产品库存详情", new Date());
            mailInfo.setContent(sb.toString());
            mailInfo.setCcToAddress("bella@inateck.com,erp_development@inateck.com");
            mailManager.send(mailInfo);
        }
    }

 }

