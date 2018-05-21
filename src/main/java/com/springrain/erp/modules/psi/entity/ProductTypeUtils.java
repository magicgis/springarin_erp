package com.springrain.erp.modules.psi.entity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.StringUtils;

public class ProductTypeUtils {   
//	String str="Tablet PC bag,Phone case,Kindle cover,Stand,sport series,Cable,Adapter,Wall charger,USB charger,Car Charger,Keyboard,speaker,Earphone,Tomons,Scanner,HDD case,Express card,Docking station,HDD enclosure,Wireless presenter,Hub,other";
	public final static Map<String,List<String>> typeProblemMap = Maps.newHashMap();
		static{
			//组装问题map
			typeProblemMap.put("Tablet PC bag",Arrays.asList("too small","too big","out of shape","not protective","other"));
			typeProblemMap.put("Phone case", Arrays.asList("not water-resistant","touch-screen insensitive","other"));
			typeProblemMap.put("Kindle cover", Arrays.asList("quality issue","not protective","other"));
			typeProblemMap.put("Stand", Arrays.asList("other"));
			typeProblemMap.put("Sport series", Arrays.asList("other"));
			typeProblemMap.put("Cable", Arrays.asList("quality issue","other"));
			typeProblemMap.put("Adaptor", Arrays.asList("quality issue","other"));
			
			typeProblemMap.put("Wall charger", Arrays.asList("not chargeable","slow charge","other"));
			typeProblemMap.put("USB Charger", Arrays.asList("not chargeable","slow charge","other"));
			typeProblemMap.put("Car Charger", Arrays.asList("not chargeable","lightning","slow charge","other"));
			
			typeProblemMap.put("Keyboard", Arrays.asList("bad connection","long wake-up time","not durable","other"));
			typeProblemMap.put("Speaker", Arrays.asList("low volume","poor sound quality","bluetooth connection","poor material","bad FM signal","battery issue","not water-resistant","other"));
			typeProblemMap.put("Earphone",Arrays.asList("not durable","poor sound quality","bluetooth connection","poor material","other"));
			
			typeProblemMap.put("Tomons",Arrays.asList("complex assembling","parts missing","bad appearance","other"));
			typeProblemMap.put("Scanner", Arrays.asList("complex operation","not durable","bluetooth connection","poor material","cannot scan","battery issue","other"));
			
			
			typeProblemMap.put("HDD Adapter", Arrays.asList("compatibility issue","not durable","slow transmission","other"));
			
			typeProblemMap.put("HDD case", Arrays.asList("poor material","other"));
			typeProblemMap.put("Express card", Arrays.asList("compatibility issue","slow transmission","driver issue","poor quality","not recognized","network","other"));
			typeProblemMap.put("Docking station", Arrays.asList("cannot clone","bad connection","cannot read","slow transmission","poor quality","other"));
			typeProblemMap.put("HDD enclosures", Arrays.asList("bad connection","cannot read","slow transmission","poor quality","other"));
			typeProblemMap.put("Wireless presenter", Arrays.asList("limited distance","not durable","other"));
			typeProblemMap.put("Hub", Arrays.asList("not recognized","insufficient power","bad connection","slow transmission","Ethernet issue","compatibility issue","not durable","other"));
			
			typeProblemMap.put("Strip light",Arrays.asList("peel off","poor contact","cannot change colour","not water-resistant","not durable","other"));
			typeProblemMap.put("Bluetooth Adaptor",Arrays.asList("not recognized","weak signal","not durable","compatibility issue","other"));
			
			typeProblemMap.put("USB Wall Charger",Arrays.asList("slow charge","easily toppled","narrow slot","bad material","not durable","other"));
			typeProblemMap.put("Electronic Scale",Arrays.asList("pallet fragile","result too heavy","result too light","not durable","complex operation","other"));
			
			typeProblemMap.put("Tomons Lamps",Arrays.asList("incomplete package","quality issue","other"));
			
		}
	//通过产品类型获取常见的问题列表
	public static List<String> getProblemByType(String type){
		List<String>  resList = typeProblemMap.get(type);
		return resList;
	}
}
