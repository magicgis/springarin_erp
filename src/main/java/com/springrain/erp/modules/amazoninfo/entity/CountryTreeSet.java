package com.springrain.erp.modules.amazoninfo.entity;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeSet;

import com.google.common.collect.Maps;

public class CountryTreeSet{
	
	public final static Map<String, Integer> countryCode = Maps.newHashMap();

	static{
		countryCode.put("de", 9);
		countryCode.put("fr", 8);
		countryCode.put("it", 7);
		countryCode.put("es", 6);
		countryCode.put("uk", 4);
		countryCode.put("com", 3);
		countryCode.put("jp", 2);
		countryCode.put("ca", 1);
	}
	
	public static TreeSet<String> getCountryTreeSet(){
		return new TreeSet<String>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return countryCode.get(o1)-countryCode.get(o2);
			}
		});
	}
}
