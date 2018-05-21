package com.springrain.erp.modules.amazoninfo.scheduler;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.HtmlUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.deserializer.ExtraProcessor;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.springrain.erp.common.utils.HttpRequest;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.entity.AmazonProduct;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;

public class ProductSynMonitor {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ProductSynMonitor.class);
	
	@Autowired
	private AmazonProductService amazonProductService;
	
	/*public void synchronize() {
		int flag = 0 ;
		String json = "";
		while(flag<10 && StringUtils.isEmpty(json)){
			json = HttpRequest.sendPost("http://ec2-54-187-34-202.us-west-2.compute.amazonaws.com/api/Asin_API.php"
				, "k1=masin123.Op9787/LJqw&k2=3efs59jf>pasjd");
			if(StringUtils.isNotEmpty(json)){
				List<AmazonProduct> list = Lists.newArrayList();
				for (Object text : JSON.parseArray(json)) {
					list.add(JSON.parseObject(text.toString(),AmazonProduct.class));
				}
				amazonProductService.save(list);
			}else{
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {}
				flag++;
			}
		}	
    }*/
	
	public void synchronize1() {
		int flag = 0 ;
		String json = "";
		while(flag<10 && StringUtils.isEmpty(json)){
			json = HttpRequest.sendPost("http://50.62.30.143/api/AAsin_API.php"
				, "k1=masin123.Op9787/LJqw&k2=3efs59jf>pasjd");
			if(StringUtils.isNotEmpty(json)){
				List<AmazonProduct> list = Lists.newArrayList();
				final Set<String> countrys = Sets.newHashSet(); 
				for (Object text : JSON.parseArray(json)) {
					AmazonProduct temp = JSON.parseObject(text.toString(),AmazonProduct.class,new ExtraProcessor() {
						@Override
						public void processExtra(Object object, String key, Object value) {
							if("parent".equals(key)){
								if(value.toString().length()>0){
									AmazonProduct product = new AmazonProduct();
									product.setAsin(value.toString());
									((AmazonProduct)object).setParentProduct(product);
								}
							}
						}
					});
					temp.setSku(HtmlUtils.htmlUnescape(temp.getSku()));
					countrys.add(temp.getCountry());
					list.add(temp);
				}
				if(countrys.size() == 8){
					Collections.sort(list);
					amazonProductService.save(list);
				}else{
					LOGGER.warn("lose country,update error"+countrys.toString());
				}
			}else{
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {}
				flag++;
			}
		}	
    }	
}
