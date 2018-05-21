package com.springrain.erp.modules.psi.entity;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.StringUtils;

public class PsiInventoryTotalDtoByInStock {
	private String productName;
	
	private String color;
	
	private Map<String,PsiInventoryDtoByInStock> inventorys =Maps.newHashMap();

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Map<String,PsiInventoryDtoByInStockWithWarehouseCode> getQuantity() {
		Map<String,PsiInventoryDtoByInStockWithWarehouseCode> rs = Maps.newHashMap() ;
		if(inventorys!=null){
			for(PsiInventoryDtoByInStock dto : inventorys.values()) {
				Map<String,PsiInventoryDtoByInStockWithWarehouseCode> map = dto.getQuantityInventory();
				for (Map.Entry<String,PsiInventoryDtoByInStockWithWarehouseCode> entry : map.entrySet()) {
					String stockId = entry.getKey();
					PsiInventoryDtoByInStockWithWarehouseCode temp = rs.get(stockId);
					if(temp==null){
						temp = new PsiInventoryDtoByInStockWithWarehouseCode(productName,"",color,new HashMap<String, Integer>(),new HashMap<String, Integer>(),new HashMap<String, Integer>(),new HashMap<String, Integer>(),new HashMap<String, Integer>(),stockId);
						rs.put(stockId, temp);
					}
					PsiInventoryDtoByInStockWithWarehouseCode stock=entry.getValue();
					temp.getSkusBrokenQuantity().putAll(stock.getSkusBrokenQuantity());
					temp.getSkusNewQuantity().putAll(stock.getSkusNewQuantity());
					temp.getSkusOldQuantity().putAll(stock.getSkusOldQuantity());
					temp.getSkusRenewQuantity().putAll(stock.getSkusRenewQuantity());
					temp.getSkusOfflineQuantity().putAll(stock.getSkusOfflineQuantity());
				}
			}
			
		}
		return rs;
	}
	
	public int getTotalQuantity() {
		int rs = 0; ;
		if(inventorys!=null){
			for(PsiInventoryDtoByInStock dto : inventorys.values()) {
				Map<String,PsiInventoryDtoByInStockWithWarehouseCode> map = dto.getQuantityInventory();
				for (PsiInventoryDtoByInStockWithWarehouseCode stock : map.values()) {
					Integer temp = stock.getNewQuantity();
					rs += (temp==null?0:temp);
				}
			}
		}
		return rs;
	}
	
	public int getTotalQuantityCN() {
		int rs = 0; ;
		if(inventorys!=null){
			for(PsiInventoryDtoByInStock dto : inventorys.values()) {
				Map<String,PsiInventoryDtoByInStockWithWarehouseCode> map = dto.getQuantityInventory();
				for (PsiInventoryDtoByInStockWithWarehouseCode stock : map.values()) {
					if(!"CN".equals(stock.getWarehouseCode())){
						continue;
					}
					Integer temp = stock.getNewQuantity();
					rs += (temp==null?0:temp);
				}
			}
		}
		return rs;
	}
	
	
	public int getTotalQuantityNotCN() {
		int rs = 0; ;
		if(inventorys!=null){
			for(PsiInventoryDtoByInStock dto : inventorys.values()) {
				Map<String,PsiInventoryDtoByInStockWithWarehouseCode> map = dto.getQuantityInventory();
				for (PsiInventoryDtoByInStockWithWarehouseCode stock : map.values()) {
					if("CN".equals(stock.getWarehouseCode())){
						continue;
					}
					Integer temp = stock.getNewQuantity();
					rs += (temp==null?0:temp);
				}
			}
		}
		return rs;
	}
	public Map<String,PsiInventoryDtoByInStockWithWarehouseCode> getQuantityEuro() {
		Map<String,PsiInventoryDtoByInStockWithWarehouseCode> rs = Maps.newHashMap() ;
		if(inventorys!=null){
			for(PsiInventoryDtoByInStock dto : inventorys.values()) {
				if("fr,es,it,de,uk".contains(dto.getCountry())){
					Map<String,PsiInventoryDtoByInStockWithWarehouseCode> map = dto.getQuantityInventory();
//					for (String stockId : map.keySet()) {
					for (Map.Entry<String,PsiInventoryDtoByInStockWithWarehouseCode> entry : map.entrySet()) {
						String stockId = entry.getKey();
						PsiInventoryDtoByInStockWithWarehouseCode temp = rs.get(stockId);
						if(temp==null){
							temp = new PsiInventoryDtoByInStockWithWarehouseCode(productName,"",color,new HashMap<String, Integer>(),new HashMap<String, Integer>(),new HashMap<String, Integer>(),new HashMap<String, Integer>(),new HashMap<String, Integer>(),stockId);
							rs.put(stockId, temp);
						}
						PsiInventoryDtoByInStockWithWarehouseCode stock = map.get(stockId);
						temp.getSkusBrokenQuantity().putAll(stock.getSkusBrokenQuantity());
						temp.getSkusNewQuantity().putAll(stock.getSkusNewQuantity());
						temp.getSkusOldQuantity().putAll(stock.getSkusOldQuantity());
						temp.getSkusRenewQuantity().putAll(stock.getSkusRenewQuantity());
						temp.getSkusOfflineQuantity().putAll(stock.getSkusOfflineQuantity());
					}
				}
			}
		}
		return rs;
	}

	
	public Map<String,PsiInventoryDtoByInStockWithWarehouseCode> getQuantityEuro2() {
		Map<String,PsiInventoryDtoByInStockWithWarehouseCode> rs = Maps.newHashMap() ;
		if(inventorys!=null){
			for(PsiInventoryDtoByInStock dto : inventorys.values()) {
				//if("fr,es,it,de,uk".contains(dto.getCountry())){
					Map<String,PsiInventoryDtoByInStockWithWarehouseCode> map = dto.getQuantityInventory();
//					for (String stockId : map.keySet()) {
					for (Map.Entry<String,PsiInventoryDtoByInStockWithWarehouseCode> entry : map.entrySet()) {
						String stockId = entry.getKey();
						PsiInventoryDtoByInStockWithWarehouseCode temp = rs.get(stockId);
						if(temp==null){
							temp = new PsiInventoryDtoByInStockWithWarehouseCode(productName,"",color,new HashMap<String, Integer>(),new HashMap<String, Integer>(),new HashMap<String, Integer>(),new HashMap<String, Integer>(),new HashMap<String, Integer>(),stockId);
							rs.put(stockId, temp);
						}
						PsiInventoryDtoByInStockWithWarehouseCode stock = map.get(stockId);
						temp.getSkusBrokenQuantity().putAll(stock.getSkusBrokenQuantity());
						temp.getSkusNewQuantity().putAll(stock.getSkusNewQuantity());
						temp.getSkusOldQuantity().putAll(stock.getSkusOldQuantity());
						temp.getSkusRenewQuantity().putAll(stock.getSkusRenewQuantity());
						temp.getSkusOfflineQuantity().putAll(stock.getSkusOfflineQuantity());
					}
				//}
			}
		}
		return rs;
	}

	
	public String getProductNameWithColor() {
		if(StringUtils.isNotBlank(color)){
			return productName+"_"+color;
		}
		return productName;
	}

	public Map<String, PsiInventoryDtoByInStock> getInventorys() {
		return inventorys;
	}

	public void setInventorys(Map<String, PsiInventoryDtoByInStock> inventorys) {
		this.inventorys = inventorys;
	}

	public PsiInventoryTotalDtoByInStock(String productName, String color,Map<String, PsiInventoryDtoByInStock> inventorys) {
		super();
		this.productName = productName;
		this.color = color;
		this.inventorys = inventorys;
	}

	public String getTip(){
		StringBuilder tip = new StringBuilder("");
		StringBuilder de = new StringBuilder("");
		StringBuilder us = new StringBuilder("");
		
		StringBuilder offde = new StringBuilder("");
		StringBuilder offus = new StringBuilder("");
		StringBuilder cn = new StringBuilder("");
		StringBuilder offcn = new StringBuilder("");
		StringBuilder jp = new StringBuilder("");
		StringBuilder offjp = new StringBuilder("");
		
		for (String country : inventorys.keySet()) {
			PsiInventoryDtoByInStock  stock =  inventorys.get(country);
			for (String stockCode : stock.getQuantityInventory().keySet()) {
				Map<String, Integer>  temp = stock.getQuantityInventory().get(stockCode).getSkusNewQuantity();
				Map<String, Integer>  tempoff = stock.getQuantityInventory().get(stockCode).getSkusOfflineQuantity();
				
				if("CN".equals(stockCode)){
//					for (String sku : temp.keySet()) {
					for (Map.Entry<String, Integer> entry : temp.entrySet()) {
						String sku = entry.getKey();
						if(temp.get(sku)>0){
							cn.append(sku);
							cn.append("=");
							cn.append(entry.getValue());
							cn.append("<br/>");
						}
					}
//					for (String sku : tempoff.keySet()) {
					for (Map.Entry<String, Integer> entry : tempoff.entrySet()) {
						String sku = entry.getKey();
						if(temp.get(sku)>0){
							offcn.append(sku);
							offcn.append("=");
							offcn.append(entry.getValue());
							offcn.append("<br/>");
						}
					}
				}else if("US".equals(stockCode)){
//					for (String sku : temp.keySet()) {
					for (Map.Entry<String, Integer> entry : temp.entrySet()) {
						String sku = entry.getKey();
						if(temp.get(sku)>0){
							us.append(sku);
							us.append("=");
							us.append(entry.getValue());
							us.append("<br/>");
						}
					}
//					for (String sku : tempoff.keySet()) {
					for (Map.Entry<String, Integer> entry : tempoff.entrySet()) {
						String sku = entry.getKey();
						if(temp.get(sku)>0){
							offus.append(sku);
							offus.append("=");
							offus.append(entry.getValue());
							offus.append("<br/>");
						}
					}
				}else if("DE".equals(stockCode)){
//					for (String sku : temp.keySet()) {
					for (Map.Entry<String, Integer> entry : temp.entrySet()) {
						String sku = entry.getKey();
						if(temp.get(sku)>0){
							de.append(sku);
							de.append("=");
							de.append(entry.getValue());
							de.append("<br/>");
						}
					}
//					for (String sku : tempoff.keySet()) {
					for (Map.Entry<String, Integer> entry : tempoff.entrySet()) {
						String sku = entry.getKey();
						if(temp.get(sku)>0){
							offde.append(sku);
							offde.append("=");
							offde.append(entry.getValue());
							offde.append("<br/>");
						}
					}
					
				}else if("JP".equals(stockCode)){
//					for (String sku : temp.keySet()) {
					for (Map.Entry<String, Integer> entry : temp.entrySet()) {
						String sku = entry.getKey();
						if(temp.get(sku)>0){
							jp.append(sku);
							jp.append("=");
							jp.append(entry.getValue());
							jp.append("<br/>");
						}
					}
//					for (String sku : tempoff.keySet()) {
					for (Map.Entry<String, Integer> entry : tempoff.entrySet()) {
						String sku = entry.getKey();
						if(temp.get(sku)>0){
							offjp.append(sku);
							offjp.append("=");
							offjp.append(entry.getValue());
							offjp.append("<br/>");
						}
					}
				}
			}
		}
		if(de.length()>0){
			tip.append((tip.length()>0?"<br/>":"")+"DE:<br/>"+de);
		}
		if(offde.length()>0){
			tip.append((tip.length()>0?"<br/>":"")+"DE OFFLINE:<br/>"+offde);
		}
		if(cn.length()>0){
			tip.append((tip.length()>0?"<br/>":"")+"CN:<br/>"+cn);
		}
		if(offcn.length()>0){
			tip.append((tip.length()>0?"<br/>":"")+"CN OFFLINE:<br/>"+offcn);
		}
		if(us.length()>0){
			tip.append((tip.length()>0?"<br/>":"")+"US:<br/>"+us);
		}
		if(offus.length()>0){
			tip.append((tip.length()>0?"<br/>":"")+"US OFFLINE:<br/>"+offus);
		}
		if(jp.length()>0){
			tip.append((tip.length()>0?"<br/>":"")+"JP:<br/>"+jp);
		}
		if(offjp.length()>0){
			tip.append((tip.length()>0?"<br/>":"")+"JP OFFLINE:<br/>"+offjp);
		}
		return tip.toString();
	} 

	public String getCnTip(){
		StringBuilder cn = new StringBuilder("");
		for (String country : inventorys.keySet()) {
			PsiInventoryDtoByInStock  stock =  inventorys.get(country);
			for (String stockCode : stock.getQuantityInventory().keySet()) {
				Map<String, Integer>  temp = stock.getQuantityInventory().get(stockCode).getSkusNewQuantity();
				Map<String, Integer>  tempoff = stock.getQuantityInventory().get(stockCode).getSkusOfflineQuantity();
				if("CN".equals(stockCode)){
//					for (String sku : temp.keySet()) {
					for (Map.Entry<String, Integer> entry : temp.entrySet()) {
						String sku = entry.getKey();
						if(temp.get(sku)>0){
							cn.append(sku).append("=").append(entry.getValue()).append("<br/>");
						}
					}
					for (Map.Entry<String, Integer> entry : tempoff.entrySet()) {
						String sku = entry.getKey();
						if(tempoff.get(sku)>0){
							cn.append("OFFLINE:").append(sku).append("=").append(entry.getValue()).append("<br/>");
						}
					}
				}
			}
		}
		return cn.toString();
	} 

	public Map<String,String> getCnTipMap(){
		Map<String,String> map = Maps.newHashMap();
		for (String country : inventorys.keySet()) {
			StringBuilder cn = new StringBuilder(map.get(country)==null?"":map.get(country));
			PsiInventoryDtoByInStock  stock =  inventorys.get(country);
			for (String stockCode : stock.getQuantityInventory().keySet()) {
				Map<String, Integer>  temp = stock.getQuantityInventory().get(stockCode).getSkusNewQuantity();
				Map<String, Integer>  tempoff = stock.getQuantityInventory().get(stockCode).getSkusOfflineQuantity();
				if("CN".equals(stockCode)){
					for (Map.Entry<String, Integer> entry : temp.entrySet()) {
						String sku = entry.getKey();
						if(temp.get(sku)>0){
							if(cn.length()==0){
								cn.append(sku).append("=").append(entry.getValue());
							}else{
								cn.append("<br/>").append(sku).append("=").append(entry.getValue());
							}
							map.put(country, cn.toString());
						}
					}
					for (Map.Entry<String, Integer> entry : tempoff.entrySet()) {
						String sku = entry.getKey();
						if(tempoff.get(sku)>0){
							if(cn.length()==0){
								cn.append("OFFLINE:").append(sku).append("=").append(entry.getValue());
							}else{
								cn.append("<br/>OFFLINE:").append(sku).append("=").append(entry.getValue());
							}
							map.put(country, cn.toString());
						}
					}
				}
			}
		}
		return map;
	} 
	
	
	public String getNotCnTip(){
		StringBuilder tip = new StringBuilder("");
		StringBuilder de = new StringBuilder("");
		StringBuilder us = new StringBuilder("");
		StringBuilder offde = new StringBuilder("");
		StringBuilder offus = new StringBuilder("");
		StringBuilder jp = new StringBuilder("");
		StringBuilder offjp = new StringBuilder("");
		for (String country : inventorys.keySet()) {
			PsiInventoryDtoByInStock  stock =  inventorys.get(country);
			for (String stockCode : stock.getQuantityInventory().keySet()) {
				Map<String, Integer>  temp = stock.getQuantityInventory().get(stockCode).getSkusNewQuantity();
				Map<String, Integer>  tempoff = stock.getQuantityInventory().get(stockCode).getSkusOfflineQuantity();
				if("US".equals(stockCode)){
					for (Map.Entry<String, Integer> entry : temp.entrySet()) {
							String sku = entry.getKey();
						if(temp.get(sku)>0){
							us.append(sku).append("=").append(entry.getValue()).append("<br/>");
						}
					}
					for (Map.Entry<String, Integer> entry : tempoff.entrySet()) {
						String sku = entry.getKey();
						if(tempoff.get(sku)>0){
							offus.append(sku).append("=").append(entry.getValue()).append("<br/>");
						}
					}
				}else if("DE".equals(stockCode)){
						for (Map.Entry<String, Integer> entry : temp.entrySet()) {
							String sku = entry.getKey();
						if(temp.get(sku)>0){
							de.append(sku).append("=").append(entry.getValue()).append("<br/>");
						}
					}
					for (Map.Entry<String, Integer> entry : tempoff.entrySet()) {
							String sku = entry.getKey();
						if(tempoff.get(sku)>0){
							offde.append(sku).append("=").append(entry.getValue()).append("<br/>");
						}
					}
				}else if("JP".equals(stockCode)){
					for (Map.Entry<String, Integer> entry : temp.entrySet()) {
						String sku = entry.getKey();
					if(temp.get(sku)>0){
						jp.append(sku).append("=").append(entry.getValue()).append("<br/>");
					}
				}
				for (Map.Entry<String, Integer> entry : tempoff.entrySet()) {
						String sku = entry.getKey();
					if(tempoff.get(sku)>0){
						offjp.append(sku).append("=").append(entry.getValue()).append("<br/>");
					}
				}
			}
			}
		}
		if(de.length()>0){
			tip.append(tip.length()>0?"<br/>":"").append("DE:<br/>").append(de);
		}
		if(offde.length()>0){
			tip.append(tip.length()>0?"<br/>":"").append("DE OFFLINE:<br/>").append(offde);
		}
		if(us.length()>0){
			tip.append(tip.length()>0?"<br/>":"").append("US:<br/>").append(us);
		}
		if(offus.length()>0){
			tip.append(tip.length()>0?"<br/>":"").append("US OFFLINE:<br/>").append(offus);
		}
		
		if(jp.length()>0){
			tip.append(tip.length()>0?"<br/>":"").append("JP:<br/>").append(jp);
		}
		if(offjp.length()>0){
			tip.append(tip.length()>0?"<br/>":"").append("JP OFFLINE:<br/>").append(offjp);
		}
		
		return tip.toString();
	} 
	
	public Map<String,String> getNotCnTipMap(){
		Map<String,String> map = Maps.newHashMap();
		for (String country : inventorys.keySet()) {
			PsiInventoryDtoByInStock  stock =  inventorys.get(country);
			for (String stockCode : stock.getQuantityInventory().keySet()) {
				Map<String, Integer>  temp = stock.getQuantityInventory().get(stockCode).getSkusNewQuantity();
				Map<String, Integer>  tempoff = stock.getQuantityInventory().get(stockCode).getSkusOfflineQuantity();
				StringBuilder tip = new StringBuilder(map.get(country)==null?"":map.get(country));
				if("US".equals(stockCode)){
					for (Map.Entry<String, Integer> entry : temp.entrySet()) {
						String sku = entry.getKey();
						if(temp.get(sku)>0){
							if(tip.length()==0){
								tip.append(sku);
								tip.append("=");
								tip.append(entry.getValue());
							}else{
								tip.append("<br/>");
								tip.append(sku);
								tip.append("=");
								tip.append(entry.getValue());
							}
							map.put(country, tip.toString());
						}
					}
					for (Map.Entry<String, Integer> entry : tempoff.entrySet()) {
						String sku = entry.getKey();
						if(tempoff.get(sku)>0){
							if(tip.length()==0){
								tip.append(sku);
								tip.append("=");
								tip.append(entry.getValue());
							}else{
								tip.append("<br/>");
								tip.append(sku);
								tip.append("=");
								tip.append(entry.getValue());
							}
							map.put(country, tip.toString());
						}
					}
					
					
				}else if("DE".equals(stockCode)){
					for (Map.Entry<String, Integer> entry : temp.entrySet()) {
						String sku = entry.getKey();
						if(temp.get(sku)>0){
							if(tip.length()==0){
								tip.append(sku);
								tip.append("=");
								tip.append(entry.getValue());
							}else{
								tip.append("<br/>");
								tip.append(sku);
								tip.append("=");
								tip.append(entry.getValue());
							}
							map.put(country, tip.toString());
						}
					}
					for (Map.Entry<String, Integer> entry : tempoff.entrySet()) {
						String sku = entry.getKey();
						if(tempoff.get(sku)>0){
							if(tip.length()==0){
								tip.append(sku);
								tip.append("=");
								tip.append(entry.getValue());
							}else{
								tip.append("<br/>");
								tip.append(sku);
								tip.append("=");
								tip.append(entry.getValue());
							}
							map.put(country, tip.toString());
						}
					}
				}else if("JP".equals(stockCode)){
					for (Map.Entry<String, Integer> entry : temp.entrySet()) {
						String sku = entry.getKey();
						if(temp.get(sku)>0){
							if(tip.length()==0){
								tip.append(sku);
								tip.append("=");
								tip.append(entry.getValue());
							}else{
								tip.append("<br/>");
								tip.append(sku);
								tip.append("=");
								tip.append(entry.getValue());
							}
							map.put(country, tip.toString());
						}
					}
					for (Map.Entry<String, Integer> entry : tempoff.entrySet()) {
						String sku = entry.getKey();
						if(tempoff.get(sku)>0){
							if(tip.length()==0){
								tip.append(sku);
								tip.append("=");
								tip.append(entry.getValue());
							}else{
								tip.append("<br/>");
								tip.append(sku);
								tip.append("=");
								tip.append(entry.getValue());
							}
							map.put(country, tip.toString());
						}
					}
				}
			}
		}
		return map;
	} 
	
}
