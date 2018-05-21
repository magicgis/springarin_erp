package com.springrain.erp.modules.amazoninfo.scheduler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.modules.amazoninfo.service.AdvertisingEmaiService;
import com.springrain.erp.modules.amazoninfo.service.AmazonStorageFeeService;

/**
 * 广告预警
 * @author sally
 * date 2017-10-27
 */
@Component
@Transactional(readOnly = true)
public class AdvertisingEmailMonitor{
    private final static Logger LOGGER = LoggerFactory.getLogger(AdvertisingEmaiService.class);
    
    @Autowired
    private AdvertisingEmaiService advertisingEmaiService;
    @Autowired
    private  MailManager mailManager;
    
    public void findAdvertising(){
        LOGGER.info("广告预警开始");
        List<String> listCountry = advertisingEmaiService.findCountry();
        Map<String, String> map = advertisingEmaiService.findMinPriceMap();
        List<Object[]> findBySql = advertisingEmaiService.findAllPlanAdv();
        
        Boolean sendEmailFlag = false;
        StringBuilder content = new StringBuilder();
        content.append("Hi,<br/><br>");
        content.append(DateUtils.getDate("yyyy年MM月dd日")+"的广告预警信息如下所示 :<br/>");
        content.append("<table width='90%' style='border:1px solid #cad9ea;color:#666; '><tr  style='background-repeat:repeat-x;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#4EFEB3; '>" +
                       "<th>产品名</th><th>应打广告sku</th><th>正在打广告sku</th></tr>");  
        String countryFlag = "flag";
        HashMap<String, String> mapFlag = new HashMap<String, String>();
        
         Map<String, Set<String>> allProduct = advertisingEmaiService.getAllProduct();
         for(Entry<String, Set<String>> entry : allProduct.entrySet()){
             String productNameAndColor = entry.getKey();
             Set<String> countryList = entry.getValue();
             for(String country:countryList){
                 String productName=null;
                 String color = null;
                 if(productNameAndColor.contains("_")){
                     productName=productNameAndColor.substring(0, productNameAndColor.indexOf("_"));
                     color=productNameAndColor.substring(productNameAndColor.indexOf("_")+1,productNameAndColor.length());
                 }else{
                     productName=productNameAndColor;
                     color="";
                 }
                 
                 if(listCountry.contains(country)){//判断是否有在打广告的该国家
                     List<Object[]> advFlagList = advertisingEmaiService.findIsExistInAdv(country, productName, color);
                         if(advFlagList.size()!=0){//正在打广告
                             StringBuilder sb = new StringBuilder();
                             List<String> listAdvSku = new ArrayList<String>();
                             for(Object[] advFlags:advFlagList){
                                String advSku = advFlags[0].toString();
                                sb.append(advSku+"\t");
                                listAdvSku.add(advSku);
                             }
                            List<String> findAllMinSku = advertisingEmaiService.findAllMinSku(productName, country, color,map.get(productName+"_"+country+"_"+color));
                            if(findAllMinSku!=null){
                                boolean contentFlag = false;
                                StringBuilder sb01 = new StringBuilder();
                                for(int i=0;i<findAllMinSku.size();i++){
                                    if(!listAdvSku.contains(findAllMinSku.get(i))){//该sku不在打广告,写入邮件
                                        sendEmailFlag = true;
                                        contentFlag = true;
                                        if("flag".equals(countryFlag) && !mapFlag.containsKey(country)){
                                            content.append("<tr style='text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;'><td colspan='3' bgcolor='#A0DCBD'>"+("com".equals(country)?"us":country).toUpperCase()+"</td></tr>");
                                            mapFlag.put(country, "");
                                        }
                                        if(!countryFlag.equals(country) && !mapFlag.containsKey(country)){
                                            content.append("<tr style='text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;'><td colspan='3' bgcolor='#A0DCBD'>"+("com".equals(country)?"us":country).toUpperCase()+"</td></tr>");
                                            mapFlag.put(country, "");
                                        }
                                        sb01.append(findAllMinSku.get(i)+"\t");
                                        countryFlag = country;
                                    }
                                }
                                if(contentFlag){
                                    if(color.length()>0){
                                        content.append("<tr style='text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#DBEEEE;'>" +
                                       "<td>"+productName+"_"+color+"</td><td >"+sb01.toString()+"</td><td>"+sb.toString()+"</td></tr>");
                                    }else{
                                          content.append("<tr style='text-align:center;height:30px; border:1px solid #cad9ea; padding:0 1em 0;background-color:#DBEEEE;'>" +
                                                  "<td>"+productName+"</td><td>"+sb01.toString()+"</td><td>"+sb.toString()+"</td></tr>");
                                    }
                            }
                         }
                     }
                 }
             }
         }
         
        
        if(sendEmailFlag){
            MailInfo mailInfo = new MailInfo();
            mailInfo.setToAddress("amazon-sales@inateck.com");
            mailInfo.setCcToAddress("erp_development@inateck.com");
            mailInfo.setContent(content.toString());
            mailInfo.setSubject("广告预警 "+DateUtils.getDate("yyyy/MM/dd"));
            mailInfo.setSentdate(new Date());
            mailManager.send(mailInfo);
        }
    }
    
}

