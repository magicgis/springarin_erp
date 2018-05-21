package com.springrain.erp;

import com.springrain.erp.common.utils.HttpRequest;

public class Test2 {
	
	public static void main(String[] args) {
		/*String json = HttpRequest.sendPost("http://50.62.30.143/api/AAsin_API.php"
				, "k1=masin123.Op9787/LJqw&k2=3efs59jf>pasjd");*/
		
		String json1 = HttpRequest.sendPost("http://50.62.30.143/api/AAsin_API.php"
				, "k1=masin123.Op9787/LJqw&k2=3efs59jf>pasjd");
		
		System.out.println(json1);
		/*final File excel = new File("d:/111/68-BK1003E-UK  2014.8.21.xlsx");
		try {
			final File txt = FeedSubmissionController.excelToTabTxt(false, new FeedSubmission(), excel);
		SubmitFeedRequest request = new SubmitFeedRequest();
		String sellerId = AmazonWSConfig.get("fr").getSellerId();
		String marketId = AmazonWSConfig.get("fr").getMarketplaceId();
		request.setMerchant(sellerId);
		request.setMWSAuthToken(AmazonWSConfig.get("fr").getMwsAuthToken());
		request.setMarketplaceIdList(new IdList(Lists.newArrayList(marketId)));
		request.setFeedType("_POST_FLAT_FILE_LISTINGS_DATA_");
			FileInputStream steam = new FileInputStream(txt);
			String md5 = FeedSubmissionController.computeContentMD5HeaderValue(steam);
			request.setContentMD5(md5);
			request.setFeedContent(steam);
			String submitId = FeedSubmissionController.invokeSubmitFeed(MarketplaceWSConfig.getClient("fr"), request);
			while(submitId==null||submitId.length()==0){
				Thread.sleep(5000);
				submitId = FeedSubmissionController.invokeSubmitFeed(MarketplaceWSConfig.getClient("fr"), request);
			}
			Thread.sleep(30000);
			File result =  new File(excel.getParentFile(),excel.getName().replace(".xlsx","_result.txt").replace(".xls","_result.txt"));
			FeedSubmissionController.getResult(submitId,"fr",result);
			//结果出来了
			List<String> rs = Files.readLines(result,Charset.forName("utf-8"));
			String resultStr = "";
			for (String str : rs) {
				if(str.trim().length()==0){
					break;
				}
				resultStr += (str+"<br/>");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		
		
		
		
		
		
		
	}
	
	
	
}
