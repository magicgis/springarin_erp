package com.springrain.magento;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.springrain.erp.common.utils.Encodes;
import com.springrain.erp.common.utils.StringUtils;

public class Test {
	
	private static Logger logger = LoggerFactory.getLogger(Test.class);
	
	/*public static void main(String[] args) {
		BindingStub bindingStub;
		try {
			bindingStub = (BindingStub) new MagentoServiceLocator()
				.getPort(new URL("http://www.inatecktest.com/api/v2_soap"));
			bindingStub.setTimeout(600000);// 设置接口超时1分钟
			String sessionId = bindingStub.login("gone", "v7h07E9a5J3z96#Q");// 执行接口获取响应报文
			System.out.println(sessionId);
//			String[] products = new String[]{"DL1001W","BH1105","BH1101"};
//			CatalogInventoryStockItemEntity[] arr = bindingStub.catalogInventoryStockItemList(sessionId, products);
//			for (CatalogInventoryStockItemEntity entity : arr) {
//				System.out.println(entity.getProduct_id());
//				System.out.println(entity.getSku());
//				System.out.println(entity.getQty());
//				System.out.println();
//			}
			SalesOrderListEntity[] arr=bindingStub.salesOrderList(sessionId, new Filters());
			System.out.println(arr.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	public static void main(String[] args) {
		//new InateckInventorySynMonitor().synInventory();
		testCatalogProductUpdate();
		//testCatalogProductAttributeMediaUpdate();
		/*BindingStub stub = MagentoClientService.getBindingStub();
		String sessionId = MagentoClientService.getSessionId(stub);
		if (StringUtils.isEmpty(sessionId)) {
			logger.info("获取sessionId失败");
		}
		try {
			CatalogProductImageEntity[] entitys = stub.catalogProductAttributeMediaList(sessionId, "441", "0", "0");
			for (CatalogProductImageEntity entity : entitys) {
				System.out.println(entity.getPosition() + "\t" + entity.getFile());
			}
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
	
	/**
	 * 产品基础属性修改(价格、name、描述等)
	 */
	public static void testCatalogProductUpdate(){
		BindingStub stub = MagentoClientService.getBindingStub();
		String sessionId = MagentoClientService.getSessionId(stub);
		if (StringUtils.isEmpty(sessionId)) {
			logger.info("获取sessionId失败");
		}
		String product = "441";
		CatalogProductCreateEntity productData = new CatalogProductCreateEntity();
		productData.setPrice("16.99");
		productData.setName("HPE-6");
		String deString = "<ul>"+
			"<li>High quality PP material</li>"+
			"<li>Simple design, easy to open, rich texture surface design, smooth curve treatment in both ends</li>"+
			"<li>Built-in 2 EVA material, dustproof, anti-static, shockproof, waterproof, protect HDD safely</li>"+
			"<li>Super big label on the front panel, you can mark the information on it conveniently</li>"+
			"<li>Internally and externally ribbed housing design, reduce pressure and prevent from temperature distortion</li>"+
			"</ul>";
		productData.setShort_description(deString);
		try {
			boolean rs = stub.catalogProductUpdate(sessionId, product, productData, "de", "0");
			System.out.println(rs);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * //TODO 图片修改
	 */
	public static void testCatalogProductAttributeMediaUpdate(){
		BindingStub stub = MagentoClientService.getBindingStub();
		String sessionId = MagentoClientService.getSessionId(stub);
		if (StringUtils.isEmpty(sessionId)) {
			logger.info("获取sessionId失败");
		}
		String product = "441";
		CatalogProductAttributeMediaCreateEntity data = new CatalogProductAttributeMediaCreateEntity();
		CatalogProductImageFileEntity file = new CatalogProductImageFileEntity();
		file.setName("/H/P/HPE-6_6.jpg");
		file.setMime("image/jpeg");
		File imgFile = new File("e:/3.jpg");
		FileInputStream in;
		try {
			in = new FileInputStream(imgFile);
			String content = Encodes.getBASE64String(Encodes.transInputstreamToBytes(in));
			file.setContent(content);
		} catch (FileNotFoundException e1) {
			logger.error("File not found", e1);
		}
		data.setFile(file);
		data.setExclude("0");	//显示标记
		data.setRemove("0");	//删除标记
		data.setPosition("6");	//图片位置
		try {
			boolean rs = stub.catalogProductAttributeMediaUpdate(sessionId, product, "/H/P/HPE-6_6.jpg", data, "0", "0");
			System.out.println(rs);
		} catch (RemoteException e) {
			if (e.getMessage() != null && e.getMessage().contains("image not exists")) {
				System.out.println("not exists create");
				/*try {
					String name = stub.catalogProductAttributeMediaCreate(sessionId, product, data, "0", "0");
					System.out.println(name);
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}*/
			} else {
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 库存修改(含批量修改)
	 */
	public static void testCatalogInventoryStockUpdate(){
		BindingStub stub = MagentoClientService.getBindingStub();
		String sessionId = MagentoClientService.getSessionId(stub);
		if (StringUtils.isEmpty(sessionId)) {
			logger.info("获取sessionId失败");
		}
		String product = "436";
		CatalogInventoryStockItemUpdateEntity entity = new CatalogInventoryStockItemUpdateEntity();
		entity.setQty("224");
		entity.setIs_in_stock(1);
		entity.setUse_config_min_qty(1);
		entity.setUse_config_max_sale_qty(1);
		entity.setManage_stock(1);
		entity.setUse_config_manage_stock(0);
		entity.setIs_qty_decimal(0);
		entity.setUse_config_backorders(1);
		entity.setNotify_stock_qty(10);
		entity.setUse_config_notify_stock_qty(0);
		int num = 0;
		while (num < 10) {
			try {	//更新单个产品
				int rs = stub.catalogInventoryStockItemUpdate(sessionId, product, entity);
				System.out.println("更新" + rs + "行");
				break;
			} catch (Exception e) {
				num++;
				if (num==10) {
					logger.error("更新库存异常", e);
				}
			}
		}

		String[] productIds = "256,257,258".split(",");
		CatalogInventoryStockItemUpdateEntity[] productData = new CatalogInventoryStockItemUpdateEntity[3];
		productData[0] = entity;
		CatalogInventoryStockItemUpdateEntity entity1 = new CatalogInventoryStockItemUpdateEntity();
		entity1.setQty("286");
		productData[1] = entity1;
		CatalogInventoryStockItemUpdateEntity entity2 = new CatalogInventoryStockItemUpdateEntity();
		entity2.setQty("386");
		productData[2] = entity2;
		try {	//批量更新
			boolean flag = stub.catalogInventoryStockItemMultiUpdate(sessionId, productIds, productData);
			System.out.println(flag);
		} catch (Exception e) {
			logger.error("更新库存异常", e);
		}
	}
}
