package com.springrain.erp.modules.ebay.scheduler;

import com.ebay.sdk.ApiAccount;
import com.ebay.sdk.ApiContext;
import com.ebay.sdk.ApiCredential;
import com.ebay.sdk.ApiException;
import com.ebay.sdk.ApiLogging;
import com.ebay.sdk.SdkException;
import com.ebay.sdk.call.CompleteSaleCall;
import com.ebay.soap.eBLBaseComponents.SiteCodeType;

public class test {
	 public void uploadtrackingnumber() {
         try {
                 //String oid = orders.getOrderChannelId();
        	 	ApiContext apiContext = new ApiContext();
	     		ApiCredential cred = apiContext.getApiCredential();
	     		cred.seteBayToken(EbayConstants.EBAYTOKEN);
	     		ApiAccount account = cred.getApiAccount();
	     		account.setDeveloper(EbayConstants.DEVID);
	     		account.setApplication(EbayConstants.APPID);
	     		account.setCertificate(EbayConstants.CERTID);
	     		apiContext.setApiServerUrl(EbayConstants.APISERVERURL);
	     		apiContext.setSite(SiteCodeType.GERMANY);
	
	     		ApiLogging apiLog = apiContext.getApiLogging();
	     		apiLog.setLogSOAPMessages(false);
	     		apiLog.setLogHTTPHeaders(false);
	     		apiLog.setLogExceptions(false);
	     		apiContext.setApiLogging(apiLog);
                CompleteSaleCall call = new CompleteSaleCall(apiContext);
                call.setOrderID("171137344439-1423633263007");
                call.setShipped(false);
                call.completeSale();
         } catch (ApiException e) {
        	 	 System.out.println("1");
                 e.printStackTrace();
         } catch (SdkException e) {
        	 System.out.println("2");
                 e.printStackTrace();
         } catch (Exception e) {
        	 System.out.println("3");
                 e.printStackTrace();
         }
 }
	 
	 
	 public static void main(String[] args) {
//		 	File file = new File("e:\\write.csv");  
//	        Writer writer = null;
//			try {
//				writer = new FileWriter(file);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}  
//	        CSVWriter csvWriter = new CSVWriter(writer, ',');  
//	        
//	        List<String[]> list = null;
//	        
//	        
//	        String[] strs = {"123" , "123" , "123"};  
//	        csvWriter.writeNext(strs);  
//	        try {
//				csvWriter.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}  
		 new test().uploadtrackingnumber();
		 
	 }
	 
}
