/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.oa.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.modules.oa.dao.RecruitDao;
import com.springrain.erp.modules.oa.entity.Recruit;
import com.springrain.erp.modules.oa.entity.Roster;
import com.springrain.erp.modules.sys.entity.Office;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 招聘 Service
 * @author michael
 * @version 2016-11-21
 */

@Service
@Transactional(readOnly = true)
public class RecruitService extends BaseService {

	@Autowired
	private RecruitDao recruitDao;
	
	
	private static String filePath;
	
	public Recruit get(Integer id) {
		return  recruitDao.get(id);
	}
	
	public List<Recruit> find(Recruit recruit) {
		DetachedCriteria dc = recruitDao.createDetachedCriteria();
		if(recruit.getCreateDate()!=null) {
			dc.add(Restrictions.ge("interviewDate", recruit.getCreateDate()));
		} 
		if(recruit.getUpdateDate()!=null) {
			dc.add(Restrictions.le("interviewDate", recruit.getUpdateDate()));
		} 
		
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.desc("id"));
	    return recruitDao.find(dc);
	}
	
	
	

	@Transactional(readOnly = false)
	public void save(Recruit recruit,MultipartFile resumePath) throws IOException, ParseException {
			if(resumePath!=null&&resumePath.getSize()!=0){
				if (filePath == null) {
					filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "oa/recruit";
				}
				String suffix = resumePath.getOriginalFilename().substring(resumePath.getOriginalFilename().lastIndexOf("."));  
				String uuid = UUID.randomUUID().toString();
				File file1 = new File(filePath);
				if (!file1.exists()) {
					file1.mkdirs();
				}
				FileUtils.copyInputStreamToFile(resumePath.getInputStream(),new File(file1, uuid+suffix));
				recruit.setResumeFile("oa/recruit/"+uuid+suffix);
			}
		
			if(recruit.getId()!=null){
				recruit.setUpdateDate(new Date());
				recruit.setUpdateUser(UserUtils.getUser());
			}else{
				recruit.setCreateDate(new Date());
				recruit.setCreateUser(UserUtils.getUser());
			}
			recruit.setDelFlag("0");
			this.recruitDao.save(recruit);
		}
	
	

	@Transactional(readOnly = false)
	public void save(Recruit recruit){
		this.recruitDao.save(recruit);
	}
	
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		Recruit recruit = this.recruitDao.get(id);
		recruit.setDelFlag("1");
		this.recruitDao.save(recruit);
	}
	
	
	@Transactional(readOnly = false)
    public void initData(MultipartFile excelPath) throws InvalidFormatException, IOException, ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		InputStream instream = excelPath.getInputStream();
		Workbook workBook = WorkbookFactory.create(instream);
		Sheet sheet = workBook.getSheetAt(0);
		List<Recruit> recruits =Lists.newArrayList();
		
		List<Office> offices=UserUtils.getOfficeList();
		Map<String,Office> oMap = Maps.newHashMap();
		for(Office office:offices){
			oMap.put(office.getName(), office);
		}
		// 循环行Row
		for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
			Row row = sheet.getRow(rowNum);
			if (row == null) {
				continue;
			}
			
			Recruit recruit = new Recruit();
			Cell cell = row.getCell(1);
			if (cell != null) {
				String origin=getValue(cell);
				recruit.setOrigin(origin);
			}
			
			cell = row.getCell(2);
			if (cell != null) {
				String deptName=getValue(cell);
				if(oMap.get(deptName)!=null){
					recruit.setOffice(oMap.get(deptName));
				}
				//部门id
			}
			
			cell = row.getCell(3);
			if (cell != null) {
				String position=getValue(cell);
				recruit.setPosition(position);
			}
			
			cell = row.getCell(4);
			if (cell != null) {
				String name=getValue(cell);
				recruit.setName(name);
			}
			
			cell = row.getCell(5);
			if (cell != null) {
				String sex=getValue(cell);
				recruit.setSex(sex);
			}
			
			cell = row.getCell(6);
			if (cell != null) {
				String phone=getValue(cell);
				recruit.setPhone(phone);
			}
			
			cell = row.getCell(7);
			if (cell != null) {
				String email=getValue(cell);
				recruit.setEmail(email);
			}
			
			
			cell = row.getCell(8);
			if (cell != null) {
				String enDate=getValue(cell);
				if(StringUtils.isNotEmpty(enDate)){
					recruit.setNoticeDate(sdf.parse(enDate));
				}
			}
			
			cell = row.getCell(9);
			if (cell != null) {
				String enDate=getValue(cell);
				if(StringUtils.isNotEmpty(enDate)){
					String time ="";
					cell = row.getCell(10);
					if(cell!=null&&StringUtils.isNotEmpty(getValue(cell))){
						time=" "+getValue(cell);
						recruit.setInterviewDate(sdf1.parse(enDate+time));
					}else{
						recruit.setInterviewDate(sdf.parse(enDate));
					}
				}  
			}
	
			cell = row.getCell(11);
			if (cell != null) {
				String review1=getValue(cell);
				recruit.setInterviewReview1(review1);
			}
			
			cell = row.getCell(12);
			if (cell != null) {
				String review2=getValue(cell);
				recruit.setInterviewReview2(review2);
			}
			
			cell = row.getCell(13);
			if (cell != null) {
				String resumeUrl=getValue(cell);
				recruit.setResumeUrl(resumeUrl);
			}
			
			cell = row.getCell(14);
			if (cell != null) {
				String remark=getValue(cell);
				recruit.setRemark(remark);
			}
			
			recruit.setCreateDate(new Date());
			recruit.setCreateUser(UserUtils.getUserById("1"));
			recruit.setDelFlag("0");
			recruits.add(recruit);
		}
		
		
		
		this.recruitDao.save(recruits);
  
	}
	
	
	@SuppressWarnings("static-access")
	private String getValue(Cell cell) {
		if (cell.getCellType() == cell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue()).trim();
		} else if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
			 short format = cell.getCellStyle().getDataFormat();  
			    SimpleDateFormat sdf = null;  
			    if(format == 14||format == 20||format == 176){  
			        //日期  
			        sdf = new SimpleDateFormat("yyyy/MM/dd");  
			        double value = cell.getNumericCellValue();  
				    Date date = org.apache.poi.ss.usermodel.DateUtil.getJavaDate(value);  
				    return sdf.format(date); 
			    }else{
			    	return String.valueOf(cell.getNumericCellValue()).trim();
			    }  
			   
			
			
		} else {
			return String.valueOf(cell.getStringCellValue()).trim();
		}
	}
}
