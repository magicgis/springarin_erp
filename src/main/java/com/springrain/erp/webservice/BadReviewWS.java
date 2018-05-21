package com.springrain.erp.webservice;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.util.HtmlUtils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.FreeMarkers;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonComment;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonCustomFilter;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonReviewComment;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrder;
import com.springrain.erp.modules.amazoninfo.entity.order.AmazonOrderItem;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerFilterService;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerService;
import com.springrain.erp.modules.amazoninfo.service.AmazonProductService;
import com.springrain.erp.modules.amazoninfo.service.order.AmazonOrderService;
import com.springrain.erp.modules.custom.entity.Comment;
import com.springrain.erp.modules.custom.entity.Event;
import com.springrain.erp.modules.custom.service.AutoReplyService;
import com.springrain.erp.modules.custom.service.CommentService;
import com.springrain.erp.modules.custom.service.EventService;
import com.springrain.erp.modules.psi.entity.PsiProductEliminate;
import com.springrain.erp.modules.psi.entity.PsiProductGroupCustomer;
import com.springrain.erp.modules.psi.entity.PsiSku;
import com.springrain.erp.modules.psi.service.PsiProductAttributeService;
import com.springrain.erp.modules.psi.service.PsiProductEliminateService;
import com.springrain.erp.modules.psi.service.PsiProductGroupUserService;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.psi.service.PsiProductTypeGroupDictService;
import com.springrain.erp.modules.sys.entity.Dict;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.DictUtils;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;

@WebService
public class BadReviewWS{
	@Autowired
	private EventService eventService;
	
	@Autowired
	private CommentService commentService;
	
	@Autowired
	private AmazonProductService amazonProductService;
	
	@Autowired
	private SystemService systemService;
	
	@Autowired
	private AmazonOrderService amazonOrderService;
	
	@Autowired
	private AutoReplyService autoReplyService;
	
	@Autowired
	private MailManager mailManager;
	
	@Autowired
	private AmazonCustomerService amazonCustomerService;
	
	@Autowired
	private PsiProductGroupUserService 	    groupUserService;
	@Autowired
	private PsiProductTypeGroupDictService 	   groupDictService;
	
	@Autowired
	private AmazonCustomerFilterService customerFilterService;
	
	@Autowired
	private PsiProductEliminateService psiProductEliminateService;
	
	@Autowired
	private PsiProductAttributeService psiProductAttributeService;
	
	@Autowired
	private PsiProductService psiProductService;
	
	private static final Logger logger = LoggerFactory.getLogger(BadReviewWS.class);
	
	private static String key = Global.getConfig("ws.key");
	
	private static DateFormat  format= new SimpleDateFormat("yyyy-MM-dd");
	
	private static Map<String, String> countryKey = Maps.newHashMap();
	
	public boolean submitBadReview(String key,List<Review> badReviews) {
		if(BadReviewWS.key.equals(key)){
			countryKey.clear();
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				countryKey.put(dict.getValue(), dict.getLabel());
			}
			//String template = SendEmailByOrderMonitor.getTemplate("thankReview","jp",null);
			Map<String,Map<String,List<PsiProductGroupCustomer>>> customerMap=groupUserService.findAllGroupCustomer();//产品线ID-国家-用户ID
			Map<String,String> nameAndLineMap=groupDictService.getLineByName();//产品名-产品线ID
			Map<String,Map<String,PsiProductEliminate>> isNewMap=psiProductEliminateService.findAllByNameAndCountry();
			 
			Set<String> faqsId = Sets.newHashSet(); 
			
			for (Review badReview : badReviews) {
				String reviewLink = badReview.getUrl();
				Date reviewDate = null;
				try {
					reviewDate = format.parse(badReview.getReviewDate());
				} catch (Exception e) {}
				if(reviewDate==null || reviewDate.after(new Date())){
					reviewDate = DateUtils.addDays(new Date(), -1);
				}
				String type = badReview.getType();
				String country = badReview.getCountry();
				country = country.replace(".unitek","").replace(".inateck","").replace("co.","");
				if(type.equals("-1")){
					if(StringUtils.isNotBlank(badReview.getCustomId())&&StringUtils.isNotBlank(badReview.getAsin())){
						if(badReview.getStar()>3){
							String reviewAsin = reviewLink.substring(reviewLink.lastIndexOf("/")+1);
							if(!amazonCustomerService.reviewIsExist(badReview.getCustomId(), reviewAsin, badReview.getStar())){
								AmazonReviewComment reviewComment = new AmazonReviewComment(new Date(),reviewAsin,badReview.getAsin(),country,reviewDate,badReview.getStar()+"",badReview.getSubject(),badReview.getCustomId());
								amazonCustomerService.save(reviewComment);
								//关联售后邮件
								try {
									AmazonComment comment = customerFilterService.getCommentByEmailOrCustomerId(null, badReview.getCustomId());
									if (comment != null && comment.getReviewComment() == null) {
										//评论数加一
										AmazonCustomFilter customFilter = comment.getTask();
										customFilter.setReviewNum(customFilter.getReviewNum() + 1);
										customerFilterService.updateCommentBySql(comment);
										customerFilterService.save(customFilter);
									}
								} catch (Exception e) {
									logger.error(badReview.getCustomId()+"关联售后邮件异常!", e);
								}
							}
						}
						
					}
					continue;
				}
				String masterKey = badReview.getCountry();
				if("com.inateck".equals(masterKey)){
					masterKey = "com";
				}
				//停掉unitek 5-29 9点
				if("com.unitek".equals(masterKey)){
					continue;
				}
				String productName = amazonProductService.findProductName(badReview.getAsin(), masterKey);
				//产品属性： 主力》新品》普通》淘汰 
				String attr="";
				if(StringUtils.isEmpty(productName)){
					productName = badReview.getProductName();
				}
				//淘汰》主力》新品》普通
				if(StringUtils.isNotBlank(productName)){
					if(isNewMap.get(productName)!=null&&isNewMap.get(productName).get(masterKey)!=null){
						PsiProductEliminate eliminate=isNewMap.get(productName).get(masterKey);
						if("4".equals(eliminate.getIsSale())){
							attr="淘汰";
						}else if("1".equals(eliminate.getIsNew())){
							attr="新品";
						}else{
							attr=DictUtils.getDictLabel(eliminate.getIsSale(), "product_position", "");
						}
					}else{
						attr="普通";
					}
				}
				String subject = "unknown";
				String accountName="";
				if(type.equals("1")){
					logger.info(country + "差评asin：" + badReview.getAsin() + "\tCustomId:" + badReview.getCustomId() + "\tURL:" + badReview.getUrl());
					subject =countryKey.get(masterKey)
									+"产品:"+productName+";<br/>"
									+"页面:"+badReview.getStar()+"分;<br/>"
									+"评论主题:"+badReview.getSubject()+";<br/>";
					badReview.setCustomEmail("not find customEmail");
					badReview.setInvoiceNumber("not find oderID");
					badReview.setCustomName("not find customName");
					String asin = badReview.getAsin();
					if(StringUtils.isNotBlank(badReview.getCustomId())){
						List<AmazonOrder> orders = amazonOrderService.findEgByCustomId(badReview.getCustomId());
						if(orders!=null&&orders.size()>0){
							String orderIds = "";
							for (AmazonOrder amazonOrder : orders) {
								int flag = 0 ;
					    		for (AmazonOrderItem item : amazonOrder.getItems()) {
									if(asin.equals(item.getAsin())){
										flag =1;
										break;
									}
								}
					    		//将关联差评的订单号放在前面
					    		if(flag==0){
					    			orderIds +=(amazonOrder.getAmazonOrderId()+",");
					    		}else{
					    			orderIds =(amazonOrder.getAmazonOrderId()+",")+orderIds;
					    		}
							}
							badReview.setCustomEmail(orders.get(0).getBuyerEmail());
							badReview.setInvoiceNumber(orderIds.substring(0,orderIds.length()-1));
							badReview.setCustomName(orders.get(0).getBuyerName());
							accountName=orders.get(0).getAccountName();
						}
					}
				}else if(type.equals("2")){
					subject =countryKey.get(masterKey)
							+"产品:"+productName+";<br/>"
							+"账号差评:"+badReview.getStar()+"分;<br/>";
				}else if(type.equals("6")){
					subject =countryKey.get(masterKey)
							+"产品:"+productName+";<br/>";
				}
				
				String priority = "1";
				if(type.equals("6")){
					if(badReview.getStar() > 0){
						priority = "2";
					}
				}else if("2".equals(type)){
					if(badReview.getStar() == 3){
						priority = "2";
					}
				}else if("1".equals(type)){
					if(badReview.getStar() == 3){
						priority = "2";
					}
				}
				Event event = new Event("0", null,subject, type, priority, badReview.getCustomId(), reviewLink, reviewDate, badReview.getDescription(),badReview.getCustomName(),badReview.getCustomEmail(),masterKey,"");
				event.setRemarks(badReview.getAsin());
				event.setCreateBy(new User("1"));
				event.setUpdateBy(new User("1"));
				event.setInvoiceNumber(badReview.getInvoiceNumber()); 
				if(StringUtils.isBlank(accountName)){
					String actName=psiProductService.getAccountByAsin(event.getRemarks(),masterKey);
					if(StringUtils.isNotBlank(actName)){
						accountName=actName;
					}
				}
				event.setAccountName(accountName);
				if("2".equals(type) && eventService.isExistByorderId(event)){
					continue;
				}
				if("6".equals(type)){
					String faqId =  event.getReviewLink().split("\\?")[0];
					if(faqsId.contains(faqsId)){
						continue;
					}else{
						faqsId.add(faqId);
					}
					event.setReviewLink(reviewLink);
				}
				Event temp = eventService.findEvent(event);
				if(temp==null){
					if("2".equals(type)||"1".equals(type)||"6".equals(type)){
						try{
							if(StringUtils.isNotBlank(productName)){
								String lineId=nameAndLineMap.get(productName);
								if(lineId!=null){
									if(customerMap.get(lineId)!=null&&customerMap.get(lineId).get(country)!=null&&customerMap.get(lineId).get(country).size()>0){
										List<PsiProductGroupCustomer> customerList=customerMap.get(lineId).get(country);
										if(customerList.size()==1){
											event.setMasterBy(systemService.getUser(customerList.get(0).getUserId()));
										}else{
											event.setMasterBy(eventService.getMaster(masterKey,type,customerList));
										}
									}else{
										logger.error("该产品线"+lineId+"国家"+key+"还未分配具体负责人！");
										User masterBy = eventService.getMaster(masterKey,type,badReview.getInvoiceNumber());
										event.setMasterBy(masterBy);
									}
								}else{
									logger.error("产品名"+productName+"未分到具体产品线！");
									User masterBy = eventService.getMaster(masterKey,type,badReview.getInvoiceNumber());
									event.setMasterBy(masterBy);
								}
							}else{
								logger.error("产品名为空,无法确定产品线！");
								User masterBy = eventService.getMaster(masterKey,type,badReview.getInvoiceNumber());
								event.setMasterBy(masterBy);
							}
						}catch(Exception e){
							logger.error("按产品线分配事件客服出错！", e);
							User masterBy = eventService.getMaster(masterKey,type,badReview.getInvoiceNumber());
							event.setMasterBy(masterBy);
						}
					}else{
						User masterBy = eventService.getMaster(masterKey,type,badReview.getInvoiceNumber());
						event.setMasterBy(masterBy);
					}
					
					if("1".equals(type)){
						String reviewAsin = reviewLink.substring(reviewLink.lastIndexOf("/")+1);
						AmazonReviewComment reviewComment = new AmazonReviewComment(new Date(),reviewAsin,badReview.getAsin(),country,reviewDate,badReview.getStar()+"",badReview.getSubject(),badReview.getCustomId());
						amazonCustomerService.save(reviewComment);
						//关联售后邮件
						try {
							AmazonComment comment = customerFilterService.getCommentByEmailOrCustomerId(null, badReview.getCustomId());
							if (comment != null && comment.getReviewComment() == null) {
								//评论数加一
								AmazonCustomFilter customFilter = comment.getTask();
								customFilter.setReviewNum(customFilter.getReviewNum() + 1);
								customerFilterService.updateCommentBySql(comment);
								customerFilterService.save(customFilter);
							}
						} catch (Exception e) {
							logger.error(badReview.getCustomId()+"关联售后邮件异常。", e);
						}
					}
					event.setProductAttribute(attr);
					try{
						event.setDescription(Encodes.filterOffUtf8Mb4(event.getDescription()));
						eventService.save(event);
					}catch(Exception e){
						logger.error("type：" + event.getType() + "asin：" + badReview.getAsin() + "\tCustomId:" + badReview.getCustomId() + "\tURL:" + event.getReviewLink(), e);
						return false;
					}
				}else{
					if("1".equals(type)&&!temp.getSubject().contains(badReview.getStar()+"分")){
						Comment comm = new Comment();
						comm.setComment("事件发生了改变"+event.toString());
						comm.setType("1");
						comm.setCreateBy(UserUtils.getUserById("1"));
						comm.setEvent(temp);
						commentService.save(comm);
					}
				}
			}
			return true;
		}
		logger.info("校验key失败：" + key);
		return false;
	}
	
	public boolean noteEmail(String key,String subject,String emailContent) {
		if(BadReviewWS.key.equals(key)){
			String toAddress = "";
			MailInfo mailInfo =  null;
			if(subject.startsWith("Promotions change notice email")){
				subject = subject+" "+DateUtils.getDate();
				toAddress = "amazon-sales@inateck.com";
				mailInfo = new MailInfo(toAddress,subject,new Date());
				mailInfo.setContent(HtmlUtils.htmlUnescape(emailContent));
			}else if(subject.startsWith("Malicious sellers notice email")){
				if(new Date().getHours()<=10){
					toAddress = "amazon-sales@inateck.com,maik@inateck.com,tim@inateck.com";
				}else{
					toAddress = "amazon-sales@inateck.com,tim@inateck.com";
				}
				mailInfo = new MailInfo(toAddress,subject,new Date());
				Map<String, String> model = amazonProductService.findProductNameMap();
				mailInfo.setContent(FreeMarkers.renderString(HtmlUtils.htmlUnescape(emailContent),model));
			}else{
				toAddress = "amazon-review@inateck.com";
				mailInfo = new MailInfo(toAddress,subject,new Date());
				Map<String, String> model = amazonProductService.findProductNameMap();
				model.putAll(customerFilterService.getCustomEmailMap());
				mailInfo.setContent(FreeMarkers.renderString(HtmlUtils.htmlUnescape(emailContent),model));
			}
			return mailManager.send(mailInfo);
		}
		return false;
	}
	
	//http://localhost:8080/inateck-erp/cxf/badreview/submitFAQ?key=Hip6k8wOkQ2qb2*Bb&subject=sadasdas
	public boolean submitFAQ(@WebParam(name="key")String key,@WebParam(name="subject")String subject
			,@WebParam(name="link")String link,@WebParam(name="customName")String customName,@WebParam(name="content")String content	
			) {
		if(BadReviewWS.key.equals(key)){
			countryKey.clear();
			List<Dict> dicts = DictUtils.getDictList("platform");
			for (Dict dict : dicts) {
				countryKey.put(dict.getValue(), dict.getLabel());
			}
			return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		String a = "https://www.amazon.fr/forum/-/Tx36ZRTTGMLZQXB/ref=ask_ql_ql_al_hza?asin=B01D0VM2L0";
		String faqId =  a.split("\\?")[0];
		System.out.println(faqId);
	}
	
	
}
