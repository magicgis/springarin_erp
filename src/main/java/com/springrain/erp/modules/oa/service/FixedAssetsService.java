/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.oa.service;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.modules.oa.dao.FixedAssetsDao;
import com.springrain.erp.modules.oa.entity.FixedAssets;
import com.springrain.erp.modules.oa.entity.Recruit;
import com.springrain.erp.modules.sys.entity.Office;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 固定资产Service
 * @author michael
 * @version 2016-11-21
 */
@Service
@Transactional(readOnly = true)
public class FixedAssetsService extends BaseService {

	@Autowired
	private FixedAssetsDao fixedAssetsDao;
	@Autowired
	private SystemService  systemService;
	
	public FixedAssets get(Integer id) {
		return  fixedAssetsDao.get(id);
	}
	
	public List<FixedAssets> find(FixedAssets fixedAssets) {
		DetachedCriteria dc = fixedAssetsDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.desc("id"));
	    return fixedAssetsDao.find(dc);
	}
	
	
	

	@Transactional(readOnly = false)
	public void saveFixed(FixedAssets fixedAssets,String owner){
		if("0".equals(owner)){
			fixedAssets.setOwnerOffice(null);
			fixedAssets.setOwnerUser(null);
		}else if("1".equals(owner)){
			fixedAssets.setOwnerOffice(null);
		}else if("2".equals(owner)){
			fixedAssets.setOwnerUser(null);
		}
		
		if(fixedAssets.getId()!=null){
			fixedAssets.setUpdateDate(new Date());
			fixedAssets.setUpdateUser(UserUtils.getUser());
		}else{
			fixedAssets.setCreateDate(new Date());
			fixedAssets.setCreateUser(UserUtils.getUser());
			fixedAssets.setDelFlag("0");
		}
		
		this.fixedAssetsDao.save(fixedAssets);
	}
	
	

	@Transactional(readOnly = false)
	public void save(FixedAssets fixedAssets){
		this.fixedAssetsDao.save(fixedAssets);
	}
	
	
	
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		FixedAssets fixedAssets = this.fixedAssetsDao.get(id);
		fixedAssets.setDelFlag("1");
		this.fixedAssetsDao.save(fixedAssets);
	}
	
	
	

	@Transactional(readOnly = false)
    public void initData(MultipartFile excelPath) throws InvalidFormatException, IOException, ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		InputStream instream = excelPath.getInputStream();
		Workbook workBook = WorkbookFactory.create(instream);
		Sheet sheet = workBook.getSheetAt(0);
		List<FixedAssets> fixeds =Lists.newArrayList();
		
		List<Office> offices=UserUtils.getOfficeList();
		Map<String,Office> oMap = Maps.newHashMap();
		for(Office office:offices){
			oMap.put(office.getName(), office);
		}
		Map<String,User> uMap = Maps.newHashMap();
		List<User> list = systemService.findActiveUsers();
		for(User user:list){
			if("helen".equals(user.getName().toLowerCase())){
				System.out.println("");
			}
			if("0".equals(user.getDelFlag())){
				uMap.put(user.getName().toLowerCase(), user);
			}
		}
		
		// 循环行Row
		for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
			Row row = sheet.getRow(rowNum);
			if (row == null) {
				continue;
			}
			
			FixedAssets fixedAssets = new FixedAssets();
			Cell cell = row.getCell(1);
			if (cell != null) {
				String name=getValue(cell).toLowerCase();
				if(uMap.get(name)!=null){
					fixedAssets.setOwnerUser(uMap.get(name));
				}else if(oMap.get(name)!=null){
					fixedAssets.setOwnerOffice(oMap.get(name));
				}
			}
			
			
			cell = row.getCell(2);
			if (cell != null) {
				String fixedName=getValue(cell);
				if(StringUtils.isEmpty(fixedName)){
					continue;
				}
				fixedAssets.setName(fixedName);
			}
			
			cell = row.getCell(3);
			if (cell != null) {
				String model=getValue(cell);
				fixedAssets.setModel(model);
			}
			
			cell = row.getCell(4);
			if (cell != null) {
				String billNo=getValue(cell);
				fixedAssets.setBillNo(billNo);
			}
			
			
			cell = row.getCell(5);
			if (cell != null) {
				String enDate=getValue(cell);
				if(StringUtils.isNotEmpty(enDate)){
					fixedAssets.setBuyDate(sdf.parse(enDate));
				}
			}
			
			cell = row.getCell(6);
			if (cell != null) {
				String sta=getValue(cell);
				fixedAssets.setFixedSta(sta);
			}
			
			cell = row.getCell(7);
			if (cell != null) {
				String place=getValue(cell);
				fixedAssets.setPlace(place);
			}
			
			cell = row.getCell(8);
			if (cell != null) {
				String remark=getValue(cell);
				fixedAssets.setRemark(remark);
			}
			
			fixedAssets.setCreateDate(new Date());
			fixedAssets.setCreateUser(UserUtils.getUserById("1"));
			fixedAssets.setDelFlag("0");
			fixeds.add(fixedAssets);
		}
		
		this.fixedAssetsDao.save(fixeds);
  
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
