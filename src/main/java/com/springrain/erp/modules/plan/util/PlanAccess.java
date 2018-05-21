package com.springrain.erp.modules.plan.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.springrain.erp.modules.plan.dto.Month;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

public class PlanAccess {
	
	
	
	
	public static boolean hasAccess(String userId,String key,String date){
		User currentUser = UserUtils.getUser(); 
		String currentUserId =currentUser.getId();
		String offId = currentUser.getOffice().getId();
		if("1".equals(offId)||"2".equals(offId)||currentUser.getRoleNames().contains("日志总管")){//系统管理员全权限
			return true;
		}else {
			if(!currentUserId.equals(userId)&& !"dep".equals(key)){//部门管理员可以改动组员的权限
				//总经理办公室单独处理
				if("2".equals(UserUtils.getUserById(userId).getOffice().getId())){
					return false;
				}
				List<String> roleList = currentUser.getRoleIdList();
				return roleList.contains("3");
			}else{
				Date today = new Date();
				if("week".equals(key)){
					int year =  today.getYear()+1900;
					int month = (today.getMonth()+1);
					//String [] temp = date.split("/");
					Month monthDto = Month.getInstance(year+"",month+"");
					int curWeek = monthDto.getCurrentWeek();
					today.setDate(curWeek);
					Date dto;
					try {
						DateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
						dto = sdf2.parse(date);
						return dto.after(today)||(dto.getYear()==today.getYear()&&dto.getMonth()==today.getMonth()&&dto.getDate()==today.getDate());
					} catch (ParseException e) {}
					/*if(year <= Integer.parseInt(temp[0])&&month <= Integer.parseInt(temp[1])){
						Month monthDto = Month.getInstance(year+"",month+"");
						int accessWeek;
						int accessYear;
						int accessMonth;
						int curWeek = monthDto.getCurrentWeek();
						if(curWeek == monthDto.getWeeks().size()){
							if(month==12){
								accessYear = year+1;
								accessMonth=1;
							}else{
								accessYear = year;
								accessMonth = month+1 ;
							}
							accessWeek = 1;
						}else{
							accessYear = monthDto.getYear();
							accessMonth = monthDto.getMonth();
							accessWeek = curWeek;
						}
						return Integer.parseInt(temp[0])>=accessYear &&(Integer.parseInt(temp[1]) >= accessMonth
								&& Integer.parseInt(temp[2]) >= accessWeek) || Integer.parseInt(temp[1]) == accessMonth+1;
					}*/
					return false;
				}else if("weekFinish".equals(key)){
					int year =  today.getYear()+1900;
					int month = (today.getMonth()+1);
					String [] temp = date.split("/");
					if(year >= Integer.parseInt(temp[0])&&month >= Integer.parseInt(temp[1])){
						Month monthDto = Month.getInstance(year+"",month+"");
						int accessWeek;
						int accessYear;
						int accessMonth;
						int curWeek = monthDto.getCurrentWeek();
						if(curWeek == 1){
							if(month==1){
								accessYear = year-1;
								accessMonth=12;
							}else{
								accessYear = year;
								accessMonth = month-1 ;
							}
							accessWeek = Month.getInstance(accessYear+"", accessMonth+"").getWeeks().size();
						}else{
							accessYear = monthDto.getYear();
							accessMonth = monthDto.getMonth();
							accessWeek = curWeek-1;
						}
						return Integer.parseInt(temp[0])==accessYear && Integer.parseInt(temp[1]) == accessMonth
								&& Integer.parseInt(temp[2]) == accessWeek;
					}
					return false;
				}else if("month".equals(key)|| "dep".equals(key)){
					if("dep".equals(key)){
						//如果涉及到部门计划询问肯定就是部门主管了
						int index = date.lastIndexOf("/");
						String dep = date.substring(index+1);
						if(!dep.equals(currentUser.getOffice().getId()) && !currentUser.getRoleIdList().contains("2")){
							return false;
						}
						date = date.substring(0,index);
					}
					try {
						DateFormat sdf1 = new SimpleDateFormat("yyyy/MM");
						Date temp1 = sdf1.parse(date);
						if(temp1.getYear()>today.getYear()){
							return true;
						}else if(temp1.getYear()==today.getYear()){
							return temp1.getMonth()>=today.getMonth();
						}
					} catch (ParseException e) {}
				}else if("day".equals(key)){
					try {
						DateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");
						Date temp1 = sdf2.parse(date);
						if(today.getYear()==temp1.getYear()&&today.getMonth() == temp1.getMonth() && today.getDate() == temp1.getDate()){
							DateFormat sdf3 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
							return today.after(sdf3.parse(date+" 9:00:00"));
						}
					} catch (ParseException e) {}
				}
				return false;
			}
		}	
	}	
}
