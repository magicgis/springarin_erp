package com.springrain.erp.modules.plan.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.utils.DateUtils;

public class Month {
	
	private final static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd",Locale.CHINESE);
	
	private final static SimpleDateFormat sdf2 = new SimpleDateFormat("E",Locale.CHINESE);
	
	private final static List<String> weekDaysName =ImmutableList.of("星期一", "星期二", "星期三", "星期四", "星期五", "星期六","星期日");
	
	private final static List<Integer> weekDaysCode =ImmutableList.of(1, 2, 3, 4, 5, 6,7); 
	
	private int year ;
	
	private int month;
	
	private final static Map<String,Month> cache = Maps.newHashMap();
	
	private int currentWeek = 1 ;
	
	private List<Week> weeks = Lists.newArrayList();

	private Month(int year, int month) {
		this.year = year;
		this.month = month;
		getWeeksByMonth();
	}

	public static Month getInstance(String year,String month){
		String key = year+month;
		synchronized (cache) {
			if(cache.get(key)==null){
				cache.put(key,new Month(Integer.parseInt(year),Integer.parseInt(month)));
			}
		}
		return cache.get(key);
	}
	
	public int getCurrentWeek() {
		Date today = new Date();
		if(today.getMonth() == month-1){
			if(today.getDay()==1){
				boolean flag = false;
				synchronized (this) {
					for (Date date : weeks.get(currentWeek-1).getDayData()) {
						if(today.getDate() == date.getDate()){
							flag = true;
							break;
						}
					}
					if(!flag){
						this.currentWeek = this.currentWeek+1;
					}
				}
			}
		}
		return 	this.currentWeek;
	}
	
	public static int getCurrentWeek(Date date) {
		if(date.getDay()==6){
			date = DateUtils.addDays(date,-1);
		}else if(date.getDay()==0){
			date = DateUtils.addDays(date,-2);
		}
		String year = (date.getYear()+1900)+"";
		String month = (date.getMonth()+1)+"";
		Month monthDto = getInstance(year, month);
		List<Week> weeks =  monthDto.getWeeks();
		for (int i = 0; i < weeks.size(); i++) {
			Week week = weeks.get(i);
			for (Date day : week.getDayData()) {
				if(isSameDay(date, day)){
					return i+1;
				}
			}
		}
		return 1;
	}
	
	private static boolean isSameDay(Date d1 ,Date d2){
		if(d1 !=null && d2 != null){
			return d1.getYear()==d2.getYear()&&d1.getMonth()==d2.getMonth()&&d1.getDate()==d2.getDate();
		}
		return false;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public List<Week> getWeeks() {
		return weeks;
	}
	
	private void getWeeksByMonth(){
		String temp = year+"/"+month;
		String[] today = DateUtils.getDate("yyyy/MM/dd").split("/");
		boolean flag = year==Integer.parseInt(today[0])&&month==Integer.parseInt(today[1]);
		sdf1.setLenient(false);
		Date date = null;
		Week week=null;
		int index = 0;
        for(int i = 1; i < 32; i++){
            try {
                date = sdf1.parse(temp + "/" + i);
                String weekStr = sdf2.format(date);
                index = weekDaysCode.get(weekDaysName.indexOf(weekStr));
                if(index<=5){
                	if(index==1){
                		week = new Week();
                		weeks.add(week);
                	}else{
                		 if(i==1){
                 			week = new Week();
                 			weeks.add(week);
                 			for (int j = 1; j < index; j++) {
                 				Date cDate =  DateUtils.addDays(date,-j);
                 				//补偿前面的天
                 				week.putDay(index-j, cDate);
     						}
                     	}
                		 
                	}
                	week.putDay(index, date);
                }
                if(flag&&i==Integer.parseInt(today[2])){
            		currentWeek = weeks.indexOf(week)+1;
            	}
            } catch (ParseException e) {}
        }
        //补偿后面的天
    	if(index<5){
    		for (int j = 1; j <= 5-index; j++) {
 				Date cDate =  DateUtils.addDays(date,j);
 				week.putDay(index+j, cDate);
			}
    	}
	}
	
	public static class Week{
		private Date[] dayData = new Date[5];

		public Date[] getDayData() {
			return dayData;
		}

		public void putDay(int index,Date day){
			dayData[index-1] = day;
		}
		
		@Override
		public String toString() {
			StringBuffer sb = new StringBuffer("(");
			sb.append(sdf1.format(this.dayData[0])+"~"+sdf1.format(this.dayData[4]));
			sb.append(")");
			return sb.toString();
		}
	}
	
	public Week getCurrentWeekDto(int week){
		return weeks.get(week-1);
	}
}
