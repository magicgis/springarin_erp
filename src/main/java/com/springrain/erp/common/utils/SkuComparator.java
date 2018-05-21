package com.springrain.erp.common.utils;

import java.util.Comparator;

public class SkuComparator implements Comparator<Object> {

	@Override
	public int compare(Object o1, Object o2) {
		if(o1==null || o2 ==null){
			return 0;
		}
		Object o1Obj = Reflections.invokeGetter(o1, "sku");
		Object o2Obj = Reflections.invokeGetter(o2, "sku");
		if(o1Obj!=null && o2Obj !=null){
			String o1Sku = o1Obj.toString();
			String o2Sku = o2Obj.toString();
			if(o1Sku.contains("-")&&o2Sku.contains("-")){
				o1Sku = o1Sku.substring(o1Sku.indexOf("-")+1);
				o2Sku = o2Sku.substring(o2Sku.indexOf("-")+1);
				if(o1Sku.toLowerCase().startsWith("new")){
					o1Sku = o1Sku.substring(3);
				}
				if(o2Sku.toLowerCase().startsWith("new")){
					o2Sku = o2Sku.substring(3);
				}
				return o1Sku.compareTo(o2Sku);
			}else if (o1Sku.contains("-")){
				return -1;
			}else if (o2Sku.contains("-")){
				return 1;
			}else {
				return o1Sku.compareTo(o2Sku);
			}
		}else{
			return 0;
		}
	}

}
