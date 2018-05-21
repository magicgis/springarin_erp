package com.springrain.erp.common.utils;

import java.util.Comparator;
import java.util.Map;

public class  MapValueComparator implements Comparator<String> {  
	    Map<String, Integer> base; 
	    boolean flag;
	    public MapValueComparator(Map<String, Integer> base,boolean flag) {  
	        this.base = base;
	        this.flag=flag;
	    }  
	    // Note: this comparator imposes orderings that are inconsistent with equals.      
	    public int compare(String a, String b) {  
	    	if(flag){
	    		if (base.get(a) >= base.get(b)) {  
	 	            return 1;  
	 	        } else {  
	 	            return -1;  
	 	        } 
	    	}else{
	    		if (base.get(b) >= base.get(a)) {  
	 	            return 1;  
	 	        } else {  
	 	            return -1;  
	 	        } 
	    	}
	       
	    }  

}
