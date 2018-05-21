package com.springrain.erp.modules.custom.scheduler;


import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.springrain.erp.common.utils.HttpRequest;
import com.springrain.erp.modules.amazoninfo.entity.customer.AmazonReviewComment;
import com.springrain.erp.modules.amazoninfo.scheduler.AmazonWSConfig;
import com.springrain.erp.modules.amazoninfo.service.AmazonCustomerService;
import com.springrain.erp.modules.custom.entity.Comment;
import com.springrain.erp.modules.custom.entity.Event;
import com.springrain.erp.modules.custom.service.CommentService;
import com.springrain.erp.modules.custom.service.EventService;
import com.springrain.erp.modules.sys.entity.User;


public class EventCommentMonitor {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired  
	private EventService	eventService;
	@Autowired
	private CommentService commentService;
	@Autowired
	private AmazonCustomerService amazonCustomerService;
	
	private static Pattern pattern = Pattern.compile("\\[\"appendFadeIn\",.+?\"\\]");
	
	public void scanEventComments() throws ParseException{
		logger.info("扫描差评跟帖开始...");
		final Map<String,List<Event>> eventMap=eventService.findScanEvent();
		//查最近两天
		final Date afterDay=new SimpleDateFormat("yyyy-MM-dd").parse("2017-11-25");
			   if(eventMap!=null&&eventMap.size()>0){
			      for(final String country:eventMap.keySet()){
			        new Thread(){
			     	   public void run() { 	 
			        	 List<Event> list=eventMap.get(country);
			        	 Set<Integer> eventId=Sets.newHashSet();
			        	 List<Comment> commentList=Lists.newArrayList();
			        	 String suffix=country;
			        	 if("uk,jp".contains(country)){
			        		 suffix="co."+country;
			        	 }else if("mx".contains(country)){
			        		 suffix="com."+country;
			        	 }
			        	 int countryFlag=0;
			        	 for (Event event : list) {
			 				try{
			 					String url=event.getReviewLink();
			 					if(StringUtils.isNotBlank(url)){
			 						if(url.startsWith("http://")){
			 							url=url.replace("http://", "https://");
			 						}
			 						String comment="";
			 						String commentHtml="";
			 					    String delHtml="";
			 						List<Comment> temp=event.getComments();
			 						
			 						int twoDay=0;
			 						
			 						Document doc = null;
		 							try{
		 								doc = HttpRequest.reqUrl2(url, null, true,0);
		 							}catch(IOException e){
		 								boolean delFlag=true;
		 								if(temp!=null&&temp.size()>0){
		 								  for (Comment cmt : temp) {
		 									  if(cmt.getComment().contains("差评帖已删除")){
		 										  delFlag=false;
		 										  break;
		 									  }
		 								  }
		 								} 
		 								if(delFlag){
		 									delHtml="差评帖已删除(Negative review deleted)";
		 									break;
		 								}
		 							}catch(Exception ex){
		 								logger.info("异常",ex);
		 							}
			 						
		 							
		 							if(doc!=null){
		 								try{
	 										String star="";
	 										if("com,jp".contains(event.getCountry())){
	 											star=doc.getElementById(url.substring(url.lastIndexOf("/")+1)).select("i").get(0).text();
	 										}else{
	 											try{
	 												star=doc.getElementsByClass("crReviewHeader").get(0).nextElementSibling().select("img").get(0).attr("title");
	 											}catch(Exception e){
	 												star=doc.getElementById(url.substring(url.lastIndexOf("/")+1)).select("i").get(0).text();
	 											}
	 										}
	 										
	 										String starNum=findStarByNew(star);
	 										if(Float.parseFloat(starNum)>3){
	 											boolean starFlag=true;
	 											if(temp!=null&&temp.size()>0){
	 											  for (Comment cmt : temp) {
	 												  if(cmt.getComment().contains("差评事件已改成好评："+starNum)){
	 													  starFlag=false;
	 													  break;
	 												  }
	 											  }
	 											} 
	 											if(StringUtils.isNotBlank(star)&&starFlag){
	 												comment="差评事件已改成好评："+starNum+"分;(Changed to positive:"+starNum+"分)";
	 											}
	 										}
	 									}catch (Exception e) {
	 										logger.info(url+"扫描差评跟帖评分异常",e);
	 									}
		 								
		 								if(!event.getCreateDate().after(afterDay)){
		 									continue;
		 								}
		 								
		 								String reviewId=event.getReviewLink().substring(event.getReviewLink().lastIndexOf("/")+1);
		 								
		 								String fadeUrl="https://www.amazon."+suffix+"/ss/customer-reviews/ajax/comment/get/ref=cm_cr_unknown";
		 								
		 								WebRequest request = null;
		 								try {
		 									WebClient client = new WebClient(BrowserVersion.CHROME);
		 									request = new WebRequest(new URL(fadeUrl),HttpMethod.POST);
		 									List<NameValuePair> params = Lists.newArrayList();
		 									params.add(new NameValuePair("threadId",reviewId));
		 									params.add(new NameValuePair("reviewId",reviewId));
		 									params.add(new NameValuePair("asin",event.getRemarks()));
		 									params.add(new NameValuePair("count","5"));
		 									params.add(new NameValuePair("sortCommentsBy","newest"));
		 									params.add(new NameValuePair("offset","0"));
		 									params.add(new NameValuePair("pageIteration","0"));
		 									request.setRequestParameters(params);
		 									String htmlStr =client.getPage(request).getWebResponse().getContentAsString();
		 									if(StringUtils.isNotBlank(htmlStr)){
		 										 List<Document> divs = Lists.newArrayList();
		 										
		 										Matcher matcher = pattern.matcher(htmlStr);
		 										
		 										while(matcher.find()){
		 											String div = matcher.group();
		 											div = div.replace("[\"appendFadeIn\",\"#"+reviewId+" \",\"","").replace("\"]","").replace("/\\\"/", "/").replace("\\","");
		 											Document tempDoc = Jsoup.parse(div);
		 											Elements eles = tempDoc.select(".author");
		 											if(eles.size()>0){
		 												divs.add(tempDoc);
		 											}
		 										}
		 										if(divs.size()>0&&countryFlag==0){
		 											logger.info(fadeUrl+","+reviewId+","+divs.size());
		 										}
		 										for (Document document: divs) {
		 											String author = document.select("a.author").text();
		 											//String time = document.select("span.comment-time-stamp").get(0).text(); Inateck. D
		 											String tempCnt=document.select("span.review-comment-text").text();
		 											String cnt="";
		 											if(tempCnt.length()<50){
		 												cnt=tempCnt;
		 											}else{
		 												cnt=document.select("span.review-comment-text").text().substring(0,50);
		 											}
		 											boolean flag=false;
		 											if(!"Inateck. D".equals(author)&&!"Seller Support".equals(author)&&!"Customer Support".equals(author)){
		 												if(temp!=null&&temp.size()>0){
		 													for (Comment cmt : temp) {
		 														if(cmt.getComment().contains("<b>"+author+"跟帖,</b>"+cnt)){
		 															flag=true;
		 															break;
		 														}
		 													}
		 												}
		 											   
		 												if(!flag&&!commentHtml.contains("<b>"+author+"跟帖,</b>"+cnt)){
		 													commentHtml+="<b>"+author+"跟帖,</b>"+cnt+";";
		 													++twoDay;
		 												}
		 											}
		 											
		 										}
		 										
		 									}
		 									
		 								} catch (Exception e1) {
		 									logger.info("跟帖异常",e1);
		 								}
		 								countryFlag=1;
		 							}
		 							
			 						/*while(i>0){
			 							String pageUrl=url+"?cdSort=newest&cdPage="+i;
			 							
			 							Document doc = null;
			 							try{
			 								doc = HttpRequest.reqUrl2(pageUrl, null, true,0);
			 							}catch(IOException e){
			 								boolean delFlag=true;
			 								if(temp!=null&&temp.size()>0){
			 								  for (Comment cmt : temp) {
			 									  if(cmt.getComment().contains("差评帖已删除")){
			 										  delFlag=false;
			 										  break;
			 									  }
			 								  }
			 								} 
			 								if(delFlag){
			 									delHtml="差评帖已删除(Negative review deleted)";
			 									break;
			 								}
			 							}catch(Exception ex){
			 								logger.info("异常",ex);
			 							}
			 							boolean flag=false;
			 							
			 							
			 							if(doc!=null){
			 								
			 								Elements itemDivs = doc.getElementsByClass("postBody");
			 								if(i==1){
			 									try{
			 										String star="";
			 										if("com,jp".contains(event.getCountry())){
			 											star=doc.getElementById(url.substring(url.lastIndexOf("/")+1)).select("i").get(0).text();
			 										}else{
			 											try{
			 												star=doc.getElementsByClass("crReviewHeader").get(0).nextElementSibling().select("img").get(0).attr("title");
			 											}catch(Exception e){
			 												star=doc.getElementById(url.substring(url.lastIndexOf("/")+1)).select("i").get(0).text();
			 											}
			 										}
			 										
			 										String starNum=findStarByNew(star);
			 										if(Float.parseFloat(starNum)>3){
			 											boolean starFlag=true;
			 											if(temp!=null&&temp.size()>0){
			 											  for (Comment cmt : temp) {
			 												  if(cmt.getComment().contains("差评事件已改成好评："+starNum)){
			 													  starFlag=false;
			 													  break;
			 												  }
			 											  }
			 											} 
			 											if(StringUtils.isNotBlank(star)&&starFlag){
			 												comment="差评事件已改成好评："+starNum+"分;(Changed to positive:"+starNum+"分)";
			 											}
			 										}
			 									}catch (Exception e) {
			 										logger.info(url+"扫描差评跟帖评分异常",e);
			 									}
			 								}
			 								
			 								if("com,jp,uk,de".contains(event.getCountry())){
			 									Date date=new Date();
			 									date.setHours(0);
			 									date.setMinutes(0);
			 									date.setSeconds(0);
			 									Date before=DateUtils.addDays(date,-10);
			 									String beforeDate=new SimpleDateFormat("yyyy-MM-dd").format(before);
			 									for(Element itemDiv:itemDivs){
			 										if(itemDiv.getElementsByClass("postFrom")!=null&&itemDiv.getElementsByClass("postFrom").size()>0){
			 											String commentUser=itemDiv.getElementsByClass("postFrom").get(0).getElementsByTag("a").text();
			 											String commentDate=itemDiv.getElementsByClass("postHeader").get(0).text();
			 											String formatDate=parseDate(event.getCountry(),commentDate);
			 											if(formatDate.compareTo(beforeDate)>=0&&!"Inateck. D".equals(commentUser)){
			 												if(temp!=null&&temp.size()>0){
			 													for (Comment cmt : temp) {
			 														if(cmt.getComment().contains("<b>"+commentUser+"跟帖,</b>"+formatDate)){
			 															flag=true;
			 															break;
			 														}
			 													}
			 												}
			 											   
			 												if(!flag&&!commentHtml.contains("<b>"+commentUser+"跟帖,</b>"+formatDate)){
			 													commentHtml+="<b>"+commentUser+"跟帖,</b>"+formatDate+";";
			 													++twoDay;
			 												}
			 											}
			 											
			 										}
			 										++count;
			 									}
			 								}else{
			 									i=-1;
			 								}
			 								
			 							}
			 						
			 								if(count==10*i){
			 									i++;
			 								}else{
			 									i=-1;
			 								}
			 							
			 						}*/
			 						if(StringUtils.isNotBlank(delHtml)){
			 							Comment comm=new Comment();
			 							comm.setComment(delHtml);
			 							comm.setType("1");
			 							comm.setCreateBy(new User("1"));
			 							comm.setUpdateBy(new User("1"));
			 							event.setUpdateDate(new Date());
			 							comm.setEvent(event);
			 							try{
			 								commentService.save(comm);
						 				}catch(Exception ex) {
						 					logger.info("扫描差评跟帖记录",ex);
						 				}
			 							commentList.add(comm);
			 							eventId.add(event.getId());
			 						}
			 						if(StringUtils.isNotBlank(commentHtml)){
			 							Comment comm=new Comment();
			 							comm.setComment("新增"+twoDay+"差评跟帖("+twoDay+" follow-up review)<div style='display:none'>"+commentHtml+"</div>");
			 							comm.setType("1");
			 							comm.setCreateBy(new User("1"));
			 							comm.setUpdateBy(new User("1"));
			 							event.setUpdateDate(new Date());
			 							comm.setEvent(event);
			 							try{
			 								commentService.save(comm);
						 				}catch(Exception ex) {
						 					logger.info("扫描差评跟帖记录",ex);
						 				}
			 							commentList.add(comm);
			 							eventId.add(event.getId());
			 						}
			 						if(StringUtils.isNotBlank(comment)){
			 							Comment comm=new Comment();
			 							comm.setComment(comment);
			 							comm.setType("1");
			 							comm.setCreateBy(new User("1"));
			 							comm.setUpdateBy(new User("1"));
			 							event.setUpdateDate(new Date());
			 							comm.setEvent(event);
			 							try{
			 								commentService.save(comm);
						 				}catch(Exception ex) {
						 					logger.info("扫描差评跟帖记录",ex);
						 				}
			 							commentList.add(comm);
			 							eventId.add(event.getId());
			 						}
			 					}
			 					
			 				}catch (Exception e) {
			 					logger.info(event.getReviewLink()+"扫描差评跟帖异常",e);
			 				}
			 		    }

			        	 logger.info(country+"扫描差评跟帖结束...");
				 	    };
				 	  }.start();
			        	 
			       }
		  }
	      
		  findFAQFollow();
			   
	      try{
	    	  Calendar calendar = Calendar.getInstance();
			  int day = calendar.get(Calendar.DAY_OF_MONTH);
			  if (day == 27||day==15) {	
	  	  		logger.info("扫描完成差评跟帖开始...");
	  	  		final Map<String,List<Event>> eventFinishedMap=eventService.findScanFinishedEvent();
	  	  		//查最近两天
	  	  			   if(eventFinishedMap!=null&&eventFinishedMap.size()>0){
	  	  			      for(final String country:eventFinishedMap.keySet()){
	  	  			        new Thread(){
	  	  			     	   public void run() { 	 
	  	  			     		 String suffix=country;
	  				        	 if("uk,jp".contains(country)){
	  				        		 suffix="co."+country;
	  				        	 }else if("mx".contains(country)){
	  				        		 suffix="com."+country;
	  				        	 }
	  	  			        	 List<Event> list=eventFinishedMap.get(country);
	  	  			        	 Set<Integer> eventId=Sets.newHashSet();
	  	  			        	 List<Comment> commentList=Lists.newArrayList();
	  	  			        	 for (Event event : list) {
	  	  			 				try{
	  	  			 					String url=event.getReviewLink();
	  	  			 					if(StringUtils.isNotBlank(url)){
	  	  			 						if(url.startsWith("http://")){
	  	  			 							url=url.replace("http://", "https://");
	  	  			 						}
	  	  			 						String comment="";
	  	  			 					
	  	  			 					    String delHtml="";
	  	  			 						List<Comment> temp=event.getComments();
	  	  			 						
	  	  			 						
	  	  			 					Document doc = null;
			 							try{
			 								doc = HttpRequest.reqUrl2(url, null, true,0);
			 							}catch(IOException e){
			 								boolean delFlag=true;
			 								if(temp!=null&&temp.size()>0){
			 								  for (Comment cmt : temp) {
			 									  if(cmt.getComment().contains("差评帖已删除")){
			 										  delFlag=false;
			 										  break;
			 									  }
			 								  }
			 								} 
			 								if(delFlag){
			 									delHtml="差评帖已删除(Negative review deleted)";
			 									break;
			 								}
			 							}catch(Exception ex){
			 								logger.info("异常",ex);
			 							}
				 						
			 							if(doc!=null){
			 								try{
		 										String star="";
		 										if("com,jp".contains(event.getCountry())){
		 											star=doc.getElementById(url.substring(url.lastIndexOf("/")+1)).select("i").get(0).text();
		 										}else{
		 											try{
		 												star=doc.getElementsByClass("crReviewHeader").get(0).nextElementSibling().select("img").get(0).attr("title");
		 											}catch(Exception e){
		 												star=doc.getElementById(url.substring(url.lastIndexOf("/")+1)).select("i").get(0).text();
		 											}
		 										}
		 										
		 										String starNum=findStarByNew(star);
		 										if(Float.parseFloat(starNum)>3){
		 											boolean starFlag=true;
		 											if(temp!=null&&temp.size()>0){
		 											  for (Comment cmt : temp) {
		 												  if(cmt.getComment().contains("差评事件已改成好评："+starNum)){
		 													  starFlag=false;
		 													  break;
		 												  }
		 											  }
		 											} 
		 											if(StringUtils.isNotBlank(star)&&starFlag){
		 												comment="差评事件已改成好评："+starNum+"分;(Changed to positive:"+starNum+"分)";
		 											}
		 										}
		 									}catch (Exception e) {
		 										logger.info(url+"扫描差评跟帖评分异常",e);
		 									}
			 							}
	  	  			 						
	  	  			 						
	  	  			 						if(StringUtils.isNotBlank(delHtml)){
	  	  			 							Comment comm=new Comment();
	  	  			 							comm.setComment(delHtml);
	  	  			 							comm.setType("1");
	  	  			 							comm.setCreateBy(new User("1"));
	  	  			 							comm.setUpdateBy(new User("1"));
	  	  			 							event.setUpdateDate(new Date());
	  	  			 							comm.setEvent(event);
	  	  			 							commentList.add(comm);
	  	  			 							eventId.add(event.getId());
	  	  			 						}
	  	  			 						if(StringUtils.isNotBlank(comment)){
	  	  			 							Comment comm=new Comment();
	  	  			 							comm.setComment(comment);
	  	  			 							comm.setType("1");
	  	  			 							comm.setCreateBy(new User("1"));
	  	  			 							comm.setUpdateBy(new User("1"));
	  	  			 							event.setUpdateDate(new Date());
	  	  			 							comm.setEvent(event);
	  	  			 							commentList.add(comm);
	  	  			 							eventId.add(event.getId());
	  	  			 						}
	  	  			 					}
	  	  			 					
	  	  			 				}catch (Exception e) {
	  	  			 					logger.info(event.getReviewLink()+"扫描差评跟帖异常",e);
	  	  			 				}
	  	  			 		    }
	  	  			        	 
	  	  			        	 if(commentList!=null&&commentList.size()>0){
	  	  			 				commentService.save(commentList);
	  	  			 			} 

	  	  			        		logger.info(country+"扫描差评跟帖结束...");
	  	  				 	    };
	  	  				 	  }.start();
	  	  			        	 
	  	  			       }
	  	  		  }
	  	    	  
	  	      }
	      }catch(Exception e){
	    	  logger.error("扫描完成差评异常",e.getMessage());
	      }
			   
			   
		  try{
			  findReviewComment(); 
		  }catch(Exception e){
			  logger.info("review order",e);
		  }
	}
	
	
	public void findReviewComment(){
		Map<String,List<Event>> eventMap=eventService.findReviewOrderEvent();
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		if(eventMap!=null&&eventMap.size()>0){
			for (Entry<String,List<Event>> entry : eventMap.entrySet()) {
				String country = entry.getKey();
				String suffix=country;
				if("jp,uk".contains(country)){
					suffix="co."+country;
				}else if("mx".equals(country)){
					suffix="com."+country;
				}
				List<Comment> commentList=Lists.newArrayList();
				List<Event> eventList = entry.getValue();
				for (Event event : eventList) {
					List<Comment> tempComments=event.getComments();
					List<AmazonReviewComment> reviewList=amazonCustomerService.findReview(country,event.getCustomId(),event.getCreateDate(),event.getRemarks());
					for (AmazonReviewComment amazonReviewComment : reviewList) {
						   boolean flag=true;
						   for (Comment comment: tempComments) {
							    if(comment.getComment().contains(dateFormat.format(amazonReviewComment.getReviewDate())+","+amazonReviewComment.getStar()+" Star,Subject:")){
							    	flag=false;
							    	break;
							    }
						   }
						   if(flag){
							   Comment comm=new Comment();
							    String urlLink="https://www.amazon."+suffix+"/review/"+amazonReviewComment.getReviewAsin();
								comm.setComment("Review Order add comment,"+dateFormat.format(amazonReviewComment.getReviewDate())+","+amazonReviewComment.getStar()+" Star,Subject:<a target='_blank' href='"+urlLink+"'>"+amazonReviewComment.getSubject()+"</a>");
								comm.setType("1");
								comm.setCreateBy(new User("1"));
								comm.setUpdateBy(new User("1"));
								event.setUpdateDate(new Date());
								Event tempEvent=new Event();
								tempEvent.setId(event.getId());
								comm.setEvent(tempEvent);
								commentList.add(comm);
						   }
					}
				}
				if(commentList!=null&&commentList.size()>0){
					commentService.save(commentList);
				}
			}
		}
	}

	private String parseStar(String country,String star){
		if("com,uk,ca".contains(country)){
			return star.split("out")[0].trim();
		}else if("de".contains(country)){
			return star.split("von")[0].trim();
		}else if("fr".equals(country)){
			return star.split("étoiles")[0].trim();
		}else if("jp".equals(country)){
			return star.split("つ星のうち")[1].trim();
		}else if("it".equals(country)){
			return star.split("su")[0].trim();
		}else if("es".equals(country)){
		    return star.split("de")[0].trim();
		}
		return star;
	}
	
	
	private String findStarByNew(String str) {
		str = str.replace(",",".");
		String result = null;
		String regex = "\\d{1}\\.0";
		Pattern pattern = Pattern.compile(regex);
		Matcher match = pattern.matcher(str);
		if (match.find()) {
			result = match.group().replace(".0","");
		}
		return result;
	}
	
	
	public EventService getEventService() {
		return eventService;
	}


	public void setEventService(EventService eventService) {
		this.eventService = eventService;
	}


	public CommentService getCommentService() {
		return commentService;
	}


	public void setCommentService(CommentService commentService) {
		this.commentService = commentService;
	}

	public String parseDate(String country,String dateStr){
		String regEx="";
		if("com".equals(country)){   //In reply to an earlier post on Nov 22, 2015 7:53:30 PM PST
			regEx = "([a-zA-Z]{3})\\s\\d{1,2},\\s\\d{4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}";
		}else if("de".equals(country)){   //Ersteintrag: 20.10.2015 09:11:29 GMT+02:00  
			regEx="\\d{1,2}.\\d{1,2}.\\d{4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}";
		}else if("uk".equals(country)){   //Initial post: 29 Oct 2015 07:40:36 GMT 
			regEx = "\\d{1,2}\\s([a-zA-Z]{3})\\s\\d{4}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}";
		}else if("fr".equals(country)){   //Message initial: 2 nov. 15 08:51:42 GMT+01:00
			regEx = "\\d{1,2}\\s([a-zA-Z]{3}).\\s\\d{2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}";
		}else if("jp".equals(country)){   //最初の投稿: 2015/10/28 10:23:49:JST
			regEx="\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{1,2}:\\d{1,2}";
		}
		if(StringUtils.isNotBlank(regEx)){
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(dateStr);
			if (m.find()) {
				String paserDate=m.group();
				Date date=formatDate(paserDate,AmazonWSConfig.get(country).getLocale());
				if(date!=null){
					return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
				}
				return paserDate;
			}
		}
		return dateStr;
	}
	
	
	public Date formatDate(String str, Locale locale) {
		DateFormat format = DateFormat.getDateInstance(DateFormat.LONG, locale);
		str = str.replace("September","Sep").replace("Sept","Sep");
		if(locale==Locale.US){
			format = new SimpleDateFormat("MMM dd yyyy HH:mm:ss",locale);
			str = str.replace(",", "");
		}else if(locale == Locale.GERMANY){
			format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss",locale);
		}else if(locale == Locale.UK){
			format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss",locale);
		}else if(locale==Locale.FRANCE){
			format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss",locale);
			str = str.replace(".", "");
		}else if(locale==Locale.JAPAN){
			format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss",locale);
		}
		Date date = null;
		try {
			date = format.parse(str);
		} catch (ParseException e) {
			str = str.replace(".", "").replace(",", "");
			if(locale == Locale.JAPAN){
				format = new SimpleDateFormat("yyyy年MM月dd日",locale);
			}else{
				format = new SimpleDateFormat("MMM dd yyyy",locale);
			}
			try {
				date = format.parse(str);
			} catch (ParseException e1) {
				format = new SimpleDateFormat("dd MMM yyyy",locale);
				try {
					date = format.parse(str);
				} catch (ParseException e2) {
				}
			}
		}
		return date;
	}
	
	public void findFAQFollow(){
		logger.info("扫描FAQ跟帖开始...");
		final Map<String,List<Event>> eventMap=eventService.findScanFAQEvent();
		if(eventMap!=null&&eventMap.size()>0){
		      for(final String country:eventMap.keySet()){
		        new Thread(){
		     	   public void run() { 	 
		     		  List<Event> list=eventMap.get(country);
		     		  for (Event event : list) {
		     			   String commentHtml="";
	 					   int twoDay=0;
	 						
		     			   String url=event.getReviewLink();
		     			   List<Comment> comments=event.getComments();
			 				try{
			 					if(StringUtils.isNotBlank(url)){
			 						if(url.startsWith("http://")){
			 							url=url.replace("http://", "https://");
			 						}
			 						Document doc = HttpRequest.reqUrl2(url, null, true,0);
			 						if(doc!=null){
			 							Elements itemDivs = doc.getElementsByClass("askWrapText");
			 							if(itemDivs!=null&&itemDivs.size()>1){
			 								List<Node> divs =itemDivs.get(1).childNodes();
			 								for (Node element : divs) {
			 									if(element.hasAttr("id")){
			 									    Document temp= Jsoup.parse(element.outerHtml());
			 									    if(temp.getElementsByClass("a-color-tertiary")!=null&&temp.getElementsByClass("a-color-tertiary").size()>0){
			 									    	String dateComment=temp.getElementsByClass("a-color-tertiary").get(0).html();
				 									    if(!dateComment.contains("Inateck. D")){
				 									    	boolean flag=false;
				 									    	if(comments!=null&&comments.size()>0){
			 													for (Comment cmt : comments) {
			 														if(cmt.getComment().contains("跟帖,"+dateComment)){
			 															flag=true;
			 															break;
			 														}
			 													}
			 												}
			 											   
			 												if(!flag&&!commentHtml.contains("跟帖,"+dateComment)){
			 													commentHtml+="跟帖,"+dateComment+";";
			 													++twoDay;
			 												}
				 									    }
			 									    }
			 									    
			 									}
			 							    }
			 							}
			 						}
			 					}	
			 					
			 				 if(StringUtils.isNotBlank(commentHtml)){
			 						Comment comm=new Comment();
			 						comm.setComment("新增"+twoDay+"跟帖("+twoDay+" follow-up)<div style='display:none'>"+commentHtml+"</div>");
			 						comm.setType("1");
			 						comm.setCreateBy(new User("1"));
			 						comm.setUpdateBy(new User("1"));
			 						event.setUpdateDate(new Date());
			 						comm.setEvent(event);
			 						commentService.save(comm);
			 				 }
			 					
			 				}catch(Exception e){
			 					logger.info(country+"=="+url+"扫描FAQ跟帖",e);
		     		        }
		     		  }		
		     		  logger.info(country+"扫描FAQ跟帖结束...");
		     	   }
		        };
		      }
		}     
	}
	
	public static void main(String[] args) throws IOException {
		    Document doc = HttpRequest.reqUrl2("https://www.amazon.ca/ask/questions/Tx2XRIG31RFEJY2/ref=ask_ql_ql_al_hza", null, false,0);
			if(doc!=null){
				Elements itemDivs = doc.getElementsByClass("askWrapText");
				if(itemDivs!=null&&itemDivs.size()>1){
					List<Node> divs =itemDivs.get(1).childNodes();
					for (Node element : divs) {
						if(element.hasAttr("id")){
						    Document temp= Jsoup.parse(element.outerHtml());
						    String dateComment=temp.getElementsByClass("a-color-tertiary").get(0).html();
						    if(dateComment.contains("Inateck. D")){
						    	
						    }
						}
				    }
				}
			}
		
	}
}
