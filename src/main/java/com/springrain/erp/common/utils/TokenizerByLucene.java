package com.springrain.erp.common.utils;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class TokenizerByLucene {
	
	public static Map<String, Integer> titleWordsRate(Set<String> titles){
		HashMap<String, Integer> base = new HashMap<String, Integer>();
		MapValueComparator comparator =  new MapValueComparator(base, false);
		TreeMap<String, Integer> result = new TreeMap<String, Integer>(comparator);
		for (String title : titles) {
			Map<String, Integer> temp = titleTokens(title);
			for (String word : temp.keySet()) {
				if(base.get(word)!=null){
					base.put(word, base.get(word)+temp.get(word));
				}else{
					base.put(word, temp.get(word));
				}
			}
		}
		result.putAll(base);
		return result;
	}
	
	private static StandardTokenizer tokenizer = null;
	
	private static StopFilter stopFilter = null;
	
	static{
		tokenizer = new StandardTokenizer(Version.LUCENE_36,null);
		stopFilter = new StopFilter(Version.LUCENE_36,tokenizer,StopAnalyzer.ENGLISH_STOP_WORDS_SET);
	}
	
	private  static Map<String, Integer> titleTokens(String title){
		Map<String, Integer> result = Maps.newHashMap();
		try {
			tokenizer.reset(new StringReader(title));
			CharTermAttribute charTermAttribute = stopFilter.addAttribute(CharTermAttribute.class);
			while (stopFilter.incrementToken()) {
				String word = charTermAttribute.toString();
				if (result.get(word) != null) {
					int value = ((Integer) result.get(word)).intValue();
					value++;
					result.put(word, new Integer(value));
				} else {
					result.put(word, new Integer(1));
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) {
		Map<String, Integer> rs = TokenizerByLucene.titleWordsRate(Sets.newHashSet("Inateck 15-15.4 Inch 2016 New Macbook Pro 15 Sleeve/ MacBook Pro 15 Retina Case Ultrabook Netbook Bag Carrying Case Cover with Pocket, Gray","Inateck 15-15.4 Inch 2016 New Macbook Pro 15 Sleeve/ MacBook Pro 15 Retina Case Ultrabook Netbook Bag Carrying Case Cover with Pocket, Gray22"));
		for (Entry<String, Integer> key : rs.entrySet()) {
			System.out.println(key.getKey()+"---"+key.getValue());
		}
	}
	
	
	
}
