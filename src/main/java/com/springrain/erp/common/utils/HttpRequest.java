package com.springrain.erp.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class HttpRequest {
	
	 public  static String reqUrlStr(String url,String params,boolean isPost){
		   if(isPost){
			   return  sendPost(url, params,0);
		   }else{
			   return  sendGet(url, params,0);
		   }
	 }
	 
	 public static String sendGet(String url, String params) {
		 return  sendGet(url, params,0);
	 }
	 
	 public static String sendPost(String url, String param) {
		 return  sendPost(url, param,0);
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
		private static String sendGet(String url, String param,int num) {
			if(num>20){
	    		return null;
	    	}
			String result = "";
			BufferedReader in = null;
			try {
				String urlNameString = url;
				if (StringUtils.isNotBlank(param)) {
					urlNameString = urlNameString + "?" + param;
				}
				URL realUrl = new URL(urlNameString);
				// 打开和URL之间的连接
				URLConnection connection = realUrl.openConnection();
				// 设置通用的请求属性
				connection.setRequestProperty("accept", "*/*");
				connection.setRequestProperty("connection", "Keep-Alive");
				connection.setRequestProperty("user-agent",
						"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
				// 建立实际的连接
				connection.connect();			
							
				String redirect = connection.getHeaderField("Location");
			    if (redirect != null){
			    	connection = new URL(redirect).openConnection();
			    	connection.setRequestProperty("accept", "*/*");
					connection.setRequestProperty("connection", "Keep-Alive");
					connection.setRequestProperty("user-agent",
							"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
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
				e.printStackTrace();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e1) {
				}
				return sendGet(url, param,++num);
			}
			// 使用finally块来关闭输入流
			finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			return result;
		}
		
		public static String sendGet2(String url, String param,int num) {
			if(num>20){
	    		return null;
	    	}
			String result = "";
			BufferedReader in = null;
			try {
				String urlNameString = url;
				if (StringUtils.isNotBlank(param)) {
					urlNameString = urlNameString + "?" + param;
				}
				URL realUrl = new URL(urlNameString);
				if("https".equalsIgnoreCase(realUrl.getProtocol())){  
		            SslUtils.ignoreSsl();  
		        }  
				// 打开和URL之间的连接
				URLConnection connection = realUrl.openConnection();
				// 设置通用的请求属性
				connection.setRequestProperty("accept", "*/*");
				connection.setRequestProperty("connection", "Keep-Alive");
				connection.setRequestProperty("user-agent",
						"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
				// 建立实际的连接
				connection.connect();			
							
				String redirect = connection.getHeaderField("Location");
			    if (redirect != null){
			    	connection = new URL(redirect).openConnection();
			    	connection.setRequestProperty("accept", "*/*");
					connection.setRequestProperty("connection", "Keep-Alive");
					connection.setRequestProperty("user-agent",
							"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
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
				e.printStackTrace();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e1) {
				}
				return sendGet(url, param,++num);
			}
			// 使用finally块来关闭输入流
			finally {
				try {
					if (in != null) {
						in.close();
					}
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			return result;
		}

		public static JSONObject httpPost(String url, Object data) throws Exception {
	        HttpPost httpPost = new HttpPost(url);
	        CloseableHttpResponse response = null;
	        CloseableHttpClient httpClient = HttpClients.createDefault();
	        RequestConfig requestConfig = RequestConfig.custom().
	        		setSocketTimeout(2000).setConnectTimeout(2000).build();
	        httpPost.setConfig(requestConfig);
	        httpPost.addHeader("Content-Type", "application/json");

	        try {
	        	StringEntity requestEntity = new StringEntity(JSON.toJSONString(data), "utf-8");
	            httpPost.setEntity(requestEntity);
	            
	            response = httpClient.execute(httpPost, new BasicHttpContext());

	            if (response.getStatusLine().getStatusCode() != 200) {

	                System.out.println("request url failed, http code=" + response.getStatusLine().getStatusCode()
	                                   + ", url=" + url);
	                return null;
	            }
	            HttpEntity entity = response.getEntity();
	            if (entity != null) {
	                String resultStr = EntityUtils.toString(entity, "utf-8");

	                JSONObject result = JSON.parseObject(resultStr);
	                if (result.getInteger("errcode") == 0) {
	                	result.remove("errcode");
	                	result.remove("errmsg");
	                    return result;
	                } else {
	                    System.out.println("request url=" + url + ",return value=");
	                    System.out.println(resultStr);
	                    int errCode = result.getInteger("errcode");
	                    String errMsg = result.getString("errmsg");
	                    throw new Exception("errCode:"+errCode+", errMsg:"+errMsg);
	                }
	            }
	        } catch (IOException e) {} finally {
	            if (response != null) try {
	                response.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }

	        return null;
	    }
		
		
		
		
		/**
		 * 向指定 URL 发送POST方法的请求
		 * 
		 * @param url
		 *            发送请求的 URL
		 * @param param
		 *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
		 * @return 所代表远程资源的响应结果
		 */
		private static String sendPost(String url, String param,int num) {
			if(num>20){
	    		return null;
	    	}
			PrintWriter out = null;
			BufferedReader in = null;
			String result = "";
			try {
				URL realUrl = new URL(url);
				// 打开和URL之间的连接
				URLConnection conn = realUrl.openConnection();
				
				// 设置通用的请求属性
				conn.setRequestProperty("accept", "*/*");
				conn.setRequestProperty("connection", "Keep-Alive");
				conn.setRequestProperty("user-agent",
						"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
				// 发送POST请求必须设置如下两行
				conn.setDoOutput(true);
				conn.setDoInput(true);
				// 获取URLConnection对象对应的输出流
				out = new PrintWriter(conn.getOutputStream());
				// 发送请求参数
				if (StringUtils.isNotBlank(param)) {
					out.print(param);
				}
				String redirect = conn.getHeaderField("Location");
			    if (redirect != null){
			    	conn = new URL(redirect).openConnection();
			    	conn.setRequestProperty("accept", "*/*");
					conn.setRequestProperty("connection", "Keep-Alive");
					conn.setRequestProperty("user-agent",
							"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
					// 发送POST请求必须设置如下两行
					conn.setDoOutput(true);
					conn.setDoInput(true);
					// 获取URLConnection对象对应的输出流
					out = new PrintWriter(conn.getOutputStream());
					// 发送请求参数
					if (StringUtils.isNotBlank(param)) {
						out.print(param);
					}
			    }
			    
				// flush输出流的缓冲
				out.flush();
				// 定义BufferedReader输入流来读取URL的响应

				in = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));
				String line;
				while ((line = in.readLine()) != null) {
					result += line;
				}
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(200);
				} catch (InterruptedException e1) {
				}
				return sendPost(url, param,++num);
			}
			// 使用finally块来关闭输出流、输入流
			finally {
				try {
					if (out != null) {
						out.close();
					}
					if (in != null) {
						in.close();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			return result;
		}   
    
    public  static Document reqUrl(String url,Map<String, String> params,boolean isPost){
    	return reqUrl(url,params,isPost,0);
    }
    
    private  static Document reqUrl(String url,Map<String, String> params,boolean isPost,int num){
    	if(num>20){
    		return null;
    	}
    	Connection conn =Jsoup.connect(url);
    	conn.timeout(5000);
    	if(params!=null){
    		conn.data(params);
    	}
    	try {
    		Document rs = null;
	    	if(isPost){
	    		rs = conn.ignoreContentType(true).post();
	    		if(rs.text().contains("we just need to make sure you're not a robot")){
	    			try {
	    				Thread.sleep(1000);
	    			} catch (InterruptedException e1) {}
	    			num++;
	    			if(url.contains("?")){
	    				url +="&1=1";
	    			}else{
	    				url +="?1=1";
	    			}
	    			return reqUrl(url,params,isPost,num);
	    		}
	    	}else{
	    		rs = conn.ignoreContentType(true).get();
	    		if(rs.text().contains("we just need to make sure you're not a robot")){
	    			try {
	    				Thread.sleep(1000);
	    			} catch (InterruptedException e1) {}
	    			num++;
	    			if(url.contains("?")){
	    				url +="&1=1";
	    			}else{
	    				url +="?1=1";
	    			}
	    			return reqUrl(url,params,isPost,num);
	    		}
	    	}
	    	return rs;
    	} catch (IOException e) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e1) {}
			num++;
			if(url.contains("?")){
				url +="&1=1";
			}else{
				url +="?1=1";
			}
			return reqUrl(url,params,isPost,num);
		}
    }
    
    public  static Document reqUrl2(String url,Map<String, String> params,boolean isPost) throws IOException{
    	return reqUrl2(url,params,isPost,0);
    }
    
    
    public  static Document reqUrl2(String url,Map<String, String> params,boolean isPost,int num) throws IOException{
    	if(num>20){
    		return null;
    	}
    	Connection conn =Jsoup.connect(url);
    	conn.timeout(5000);
    	if(params!=null){
    		conn.data(params);
    	}
    	try {
    		Document rs = null;
	    	if(isPost){
	    		rs = conn.ignoreContentType(true).post();
	    		if(rs.text().contains("we just need to make sure you're not a robot")){
	    			try {
	    				Thread.sleep(1000);
	    			} catch (InterruptedException e1) {}
	    			num++;
	    			if(url.contains("?")){
	    				url +="&1=1";
	    			}else{
	    				url +="?1=1";
	    			}
	    			return reqUrl2(url,params,isPost,num);
	    		}
	    	}else{
	    		rs = conn.ignoreContentType(true).get();
	    		if(rs.text().contains("we just need to make sure you're not a robot")){
	    			try {
	    				Thread.sleep(1000);
	    			} catch (InterruptedException e1) {}
	    			num++;
	    			if(url.contains("?")){
	    				url +="&1=1";
	    			}else{
	    				url +="?1=1";
	    			}
	    			return reqUrl2(url,params,isPost,num);
	    		}
	    	}
	    	return rs;
    	} catch (IOException e) {
			try {
				if(e.getMessage().contains("404")){
					throw e;
				}else{
					Thread.sleep(2000);	
				}
			} catch (InterruptedException e1) {}
			num++;
			if(url.contains("?")){
				url +="&1=1";
			}else{
				url +="?1=1";
			}
			return reqUrl2(url,params,isPost,num);
		}
    }
    
    public static Page getPage(WebClient client,String url){
    	client.getOptions().setThrowExceptionOnScriptError(false);
    	return getPage(client, url, 0);
    }
	
	/**
	 * 
	 * @param client
	 * @param url
	 * @param requestBody(页面抓包提供格式例如： "method=GET&model=%7B%22shipmentId%22%3A%22"+shipmentId")
	 * @return
	 */
	public static Page getPageAjax(WebClient client,String url,String requestBody){
		try {
			return getPagePost(client, url, requestBody, 0);
		} catch (MalformedURLException e) {
			//logger.error("抓取页面失败", e);
			return null;
		}
	}
	
	/**
	 * 
	 * @param client
	 * @param url
	 * @param paraMap 参数map
	 * @return
	 */
	public static Page getPagePost(WebClient client, String url, Map<String, String> paraMap){
		try {
			String requestBody = "";
			for (Entry<String, String> entry : paraMap.entrySet()) {
				if (StringUtils.isEmpty(requestBody)) {
					requestBody = entry.getKey()+"="+entry.getValue();
				} else {
					requestBody = "&"+entry.getKey()+"="+entry.getValue();
				}
			}
			return getPagePost(client, url, requestBody, 0);
		} catch (MalformedURLException e) {
			//logger.error("抓取页面失败", e);
			return null;
		}
	}
	
	private static Page getPage(WebClient client,String url,int num){
		if(num>5){
			return null;
		}
		try {
			Page page = null;
			try {
				page =  client.getPage(url);
			} catch (Exception e) {
				num = num +1;
				return getPage(client,url,num);
			}
			if (page == null) {
				num = num +1;
				return getPage(client,url,num);
			}
			try {
				if (page.isHtmlPage()) {
					HtmlPage htmlPage = (HtmlPage)page;
					//en:Robot Check de:Bot Check  fr/it:Amazon CAPTCHA
					if (htmlPage.asXml().contains("Robot Check") || htmlPage.asXml().contains("Bot Check")
							 || htmlPage.asXml().contains("Amazon CAPTCHA")) {
						
						Thread.sleep(2000);
						num = num +1;
						return getPage(client,url,num);
					}
				}
			} catch (Exception e1) {
				num = num +1;
				return getPage(client,url,num);
			}
			return page;
		} catch (Exception e) {
			num = num +1;
			return getPage(client,url,num);
		}
	}
	
	private static Page getPagePost(WebClient client,String url,String requestBody, int num) throws MalformedURLException{
		if(num>5){
			return null;
		}
		WebRequest req = new WebRequest(new URL(url), HttpMethod.POST);
		req.setAdditionalHeader("X-Requested-With", "XMLHttpRequest");
		req.setRequestBody(requestBody);
		try {
			Page page = null;
			try {
				page =  client.getPage(req);
			} catch (Exception e) {
				num = num +1;
				return getPage(client,url,num);
			}
			if (page == null) {
				num = num +1;
				return getPage(client,url,num);
			}
			try {
				//en:Robot Check de:Bot Check  fr/it:Amazon CAPTCHA
				if (page.isHtmlPage()) {
					HtmlPage htmlPage = (HtmlPage)page;
					if (htmlPage.asXml().contains("Robot Check") || htmlPage.asXml().contains("Bot Check")
							 || htmlPage.asXml().contains("Amazon CAPTCHA")) {
						
						Thread.sleep(2000);
						num = num +1;
						return getPage(client,url,num);
					}
				}
			} catch (Exception e1) {
				num = num +1;
				return getPage(client,url,num);
			}
			return page;
		} catch (Exception e) {
			num = num +1;
			return getPage(client,url,num);
		}
	}
	
	public static void main(String[] args) {
		try {
			WebClient client = new WebClient();
			String url = "http://127.0.0.1:8090/springrain-erp/php/getAmazonAccount";
			String requestBody = "{\"country\":\"com\",\"name\":\"leehong\",\"pw\":\"123456\"}";
			WebRequest req = new WebRequest(new URL(url),HttpMethod.POST);
			req.setAdditionalHeader("X-Requested-With", "XMLHttpRequest");
			req.setRequestBody(requestBody);
			Page page = client.getPage(req);
			/*Page page = getPageAjax(client, url, requestBody);*/
			System.err.println(page.getWebResponse().getContentAsString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*String url = "http://production.shippingapis.com/ShippingAPI.dll?API=TrackV2&XML=";
		//测试地址
		url = "http://stg-production.shippingapis.com/ShippingAPI.dll?API=TrackV2&XML=";
		url += "<?xml version =\"1.0\" encoding =\"UTF-8\"?>"
				+ "<TrackFieldRequest USERID =\"1001674\"><Revision>1</Revision>"
				+ "<ClientIp>127.0.0.1</ClientIp>"
				+ "<TrackID ID =\"EJ123456780US\"/>" + "</TrackFieldRequest>";
		System.out.println(url);
		String rs = sendPost(url, null);
		System.out.println(rs);
		WebClient client = new WebClient();
		Page page = getPage(client, url);
		System.out.println(page.getWebResponse().getContentAsString());*/
	}
   
}