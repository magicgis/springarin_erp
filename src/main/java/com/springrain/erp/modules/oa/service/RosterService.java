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
import java.util.Set;
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
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.modules.oa.dao.RewardPunishmentLogDao;
import com.springrain.erp.modules.oa.dao.RosterDao;
import com.springrain.erp.modules.oa.entity.RewardPunishmentLog;
import com.springrain.erp.modules.oa.entity.Roster;
import com.springrain.erp.modules.sys.entity.Office;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 请假Service
 * @author liuj
 * @version 2013-04-05
 */
@Service
@Transactional(readOnly = true)
public class RosterService extends BaseService {

	@Autowired
	private RosterDao rosterDao;
	@Autowired
	private RewardPunishmentLogDao logDao;
	
	
	private static String filePath;
	
	public Roster get(Integer id) {
		return  rosterDao.get(id);
	}
	
	public List<Roster> find(Roster roster,String month) {
		DetachedCriteria dc = rosterDao.createDetachedCriteria();
		if(roster.getCreateDate()!=null) {
			dc.add(Restrictions.ge("entryDate", roster.getCreateDate()));
		} 
		if(roster.getUpdateDate()!=null) {
			dc.add(Restrictions.le("entryDate", roster.getUpdateDate()));
		} 
		
		if(StringUtils.isNotEmpty(month)){
			dc.add(Restrictions.sqlRestriction("DATE_FORMAT(birth_Date,'%m')='"+month+"'"));
		}
		
		if(roster.getOffice()!=null&&StringUtils.isNotEmpty(roster.getOffice().getId())){
			dc.add(Restrictions.eq("office.id", roster.getOffice().getId()));
		}
		
		if(StringUtils.isNotEmpty(roster.getWorkSta())){
			dc.add(Restrictions.eq("workSta", roster.getWorkSta()));
		}
		
		dc.add(Restrictions.eq("delFlag", "0"));
		dc.addOrder(Order.desc("entryDate"));
	    return rosterDao.find(dc);
	}
	
	
	

	@Transactional(readOnly = false)
	public void save(Roster roster,MultipartFile resumePath,MultipartFile probationPath,MultipartFile trainPath) throws IOException, ParseException {
		
		//根据id查出原来的“个人奖惩记录”
		
		if(resumePath!=null&&resumePath.getSize()!=0){
			if (filePath == null) {
				filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "oa/roster";
			}
			String suffix = resumePath.getOriginalFilename().substring(resumePath.getOriginalFilename().lastIndexOf("."));  
			String uuid = UUID.randomUUID().toString();
			File file1 = new File(filePath);
			if (!file1.exists()) {
				file1.mkdirs();
			}
			FileUtils.copyInputStreamToFile(resumePath.getInputStream(),new File(file1, uuid+suffix));
			roster.setResumeFile("oa/roster/"+uuid+suffix);
		}
		
		if(probationPath!=null&&probationPath.getSize()!=0){
			if (filePath == null) {
				filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "oa/roster";
			}
			String suffix = probationPath.getOriginalFilename().substring(probationPath.getOriginalFilename().lastIndexOf("."));  
			String uuid = UUID.randomUUID().toString();
			File file1 = new File(filePath);
			if (!file1.exists()) {
				file1.mkdirs();
			}
			FileUtils.copyInputStreamToFile(probationPath.getInputStream(),new File(file1, uuid+suffix));
			roster.setProbationFile("oa/roster/"+uuid+suffix);
		}
		
		if(trainPath!=null&&trainPath.getSize()!=0){
			if (filePath == null) {
				filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "oa/roster";
			}
			String suffix = trainPath.getOriginalFilename().substring(trainPath.getOriginalFilename().lastIndexOf("."));  
			String uuid = UUID.randomUUID().toString();
			File file1 = new File(filePath);
			if (!file1.exists()) {
				file1.mkdirs();
			}
			FileUtils.copyInputStreamToFile(trainPath.getInputStream(),new File(file1, uuid+suffix));
			roster.setTrainFile("oa/roster/"+uuid+suffix);
		}
		
		Set<Integer> setNewLogIds = Sets.newHashSet();
		Set<Integer> delItemSet = Sets.newHashSet();
		if(roster.getLogs()!=null){
			for(RewardPunishmentLog log:roster.getLogs()){
				log.setRoster(roster);
				if(log.getId()!=null){
					setNewLogIds.add(log.getId());
				}
				log.setDelFlag("0");
				MultipartFile  fileData = log.getFileData();
				if(fileData!=null&&fileData.getSize()!=0){
					if (filePath == null) {
						filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "oa/roster";
					}
					String suffix = fileData.getOriginalFilename().substring(fileData.getOriginalFilename().lastIndexOf("."));  
					String uuid = UUID.randomUUID().toString();
					File file1 = new File(filePath);
					if (!file1.exists()) {
						file1.mkdirs();
					}
					FileUtils.copyInputStreamToFile(fileData.getInputStream(),new File(file1, uuid+suffix));
					log.setFilePath("oa/roster/"+uuid+suffix);
				}
			}
		}
		
		
		//如果身份证号不为空，读出年龄、出生日期、性别
		String idCard=roster.getIdCard();
		if(StringUtils.isNotEmpty(idCard)&&idCard.length()==18){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String birth = idCard.substring(6, 14);
			Integer sex   = Integer.parseInt(idCard.substring(idCard.length()-2, idCard.length()-1));
			//Integer age = new Date().getYear()-sdf.parse(birth).getYear();
			
			
			if(sex%2==0){
				roster.setSex("0");
			}else{
				roster.setSex("1");
			}
			roster.setBirthDate(sdf.parse(birth));
		}
		
		
		if(roster.getId()!=null){
			List<Integer> oldRewardIds = getRewardLogs(roster.getId());
			if(setNewLogIds!=null&&setNewLogIds.size()>0){
				for(int j=0;j<oldRewardIds.size();j++){
					if(!setNewLogIds.contains(oldRewardIds.get(j))){
						//不包含就干掉
						delItemSet.add(oldRewardIds.get(j));
					};
				}
			}else{
				//都不包含全干掉
				for(int j=0;j<oldRewardIds.size();j++){
					delItemSet.add(oldRewardIds.get(j));
				}
			}
			
			
			if(delItemSet.size()>0){
				for(RewardPunishmentLog log:this.getLogItems(delItemSet)){
					log.setDelFlag("1");
					log.setRoster(roster);
					roster.getLogs().add(log);
				};
			}
			roster.setUpdateDate(new Date());
			roster.setUpdateUser(UserUtils.getUser());
			
			//如果部门改变，更新用户表信息
			if(roster.getOffice()!=null&&roster.getOffice().getId()!=null&&!roster.getOffice().getId().equals(roster.getOldOfficeId())){
				updateOffice(roster.getUser().getId(), roster.getOffice().getId());
			}
			
		}else{
			roster.setCreateDate(new Date());
			roster.setCreateUser(UserUtils.getUser());
		}
		roster.setDelFlag("0");
		this.rosterDao.getSession().merge(roster);
	}
	
	

	@Transactional(readOnly = false)
	public void save(Roster roster){
		this.rosterDao.save(roster);
	}
	
	
	@Transactional(readOnly = false)
	public void delete(Integer id) {
		Roster roster = this.rosterDao.get(id);
		roster.setDelFlag("1");
		this.rosterDao.save(roster);
	}
	
	//更改部门
	@Transactional(readOnly = false)
	public void updateOffice(String userId,String officeId){
		String sql="UPDATE sys_user AS a SET a.`office_id`=:p2 WHERE a.`id`=:p1 ";
		Parameter para = new Parameter(userId,officeId);
		this.rosterDao.updateBySql(sql, para);
	}
	
	
	
	//更新在职状态
	@Transactional(readOnly = false)
	public void updateWorkSta(String userId,String workSta){
		String sql=" UPDATE oa_roster AS a SET a.`work_sta`=:p2 WHERE a.`user`=:p1 ";
		Parameter para = new Parameter(userId,workSta);
		this.rosterDao.updateBySql(sql, para);
	}
	
	
	
	public String existUser(String userId,Integer id){
		String sql="SELECT a.id FROM oa_roster AS a WHERE a.`del_flag`='0' AND a.`user`=:p1 ";
		Parameter para = null;
		if(id==null){
			para = new Parameter(userId);
		}else{
			para = new Parameter(userId,id);
			sql+=" AND a.id<>:p2";
		}
		List<Integer>  list = this.logDao.findBySql(sql,para);
		if(list!=null&&list.size()>0){
			return "true";
		}
		return "false";
	}
	
	
	/**
	 *奖惩记录 
	 */
	public List<Integer> getRewardLogs(Integer rosterId){
		String sql="SELECT a.`id` FROM oa_reward_punishment_log AS a WHERE a.`roster_id`=:p1 AND a.`del_flag`='0'";
		return this.rosterDao.findBySql(sql,new Parameter(rosterId));
	}
	
	/**
	 *根据itemId获取list信息 
	 */
	public List<RewardPunishmentLog> getLogItems(Set<Integer> ids){
		DetachedCriteria dc = this.logDao.createDetachedCriteria();
		dc.add(Restrictions.in("id", ids));
		return logDao.find(dc);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	@Transactional(readOnly = false)
    public void initData(MultipartFile excelPath) throws InvalidFormatException, IOException, ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
		InputStream instream = excelPath.getInputStream();
		Workbook workBook = WorkbookFactory.create(instream);
		Sheet sheet = workBook.getSheetAt(0);
		List<Roster> rosters =Lists.newArrayList();
		// 循环行Row
		for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
			Row row = sheet.getRow(rowNum);
			if (row == null) {
				continue;
			}
			
			Roster roster = new Roster();
			Cell cell = row.getCell(1);
			if (cell != null) {
				String cnName=getValue(cell);
				roster.setCnName(cnName);
			}
			
			cell = row.getCell(2);
			if (cell != null) {
				String jobNo=getValue(cell);
				roster.setJobNo(jobNo);
			}
			
			cell = row.getCell(3);
			if (cell != null) {
				String enName=getValue(cell);
				roster.setEnName(enName);
			}
			
			cell = row.getCell(4);
			if (cell != null) {
				String workSta=getValue(cell);
				roster.setWorkSta((int)Float.parseFloat(workSta)+"");
			}
			
			cell = row.getCell(6);
			if (cell != null) {
				String position=getValue(cell);
				roster.setPosition(position);
			}
			
			cell = row.getCell(7);
			if (cell != null) {
				String enDate=getValue(cell);
				if(StringUtils.isNotEmpty(enDate)){
					roster.setEntryDate(sdf.parse(enDate));
				}
			}
			
			
			cell = row.getCell(8);
			if (cell != null) {
				String egularDate=getValue(cell);
				if(StringUtils.isNotEmpty(egularDate)){
					roster.setEgularDate(sdf.parse(egularDate));
				}
			}
			
			cell = row.getCell(9);
			if (cell != null) {
				String sex=getValue(cell);
				roster.setSex((int)Float.parseFloat(sex)+"");
			}
			
			
//			cell = row.getCell(10);
//			if (cell != null) {
//				String birthDate=getValue(cell);
//				if(StringUtils.isNotEmpty(birthDate)){
//					
//				}
//			}
			
			
			//年龄
//			cell = row.getCell(11);
//			if (cell != null) {
//				String age=getValue(cell);
//				roster.setAge((int)Float.parseFloat(age));
//			}
			
			
			cell = row.getCell(12);
			if (cell != null) {
				String idCard=getValue(cell);
				roster.setIdCard(idCard);
				if(StringUtils.isNotEmpty(idCard)&&idCard.length()==18){
					Date birth = sdf1.parse(idCard.substring(6, 14));
//					Integer age = new Date().getYear()-birth.getYear();
					roster.setBirthDate(birth);
//					roster.setAge(age);
					//老外的身份证另外处理
				}
				
			}
			
			cell = row.getCell(13);
			if (cell != null) {
				String phone=getValue(cell);
				roster.setPhone(phone);
			}
			
			cell = row.getCell(14);
			if (cell != null) {
				String email=getValue(cell);
				roster.setEmail(email);
			}
			
			
			cell = row.getCell(16);
			if (cell != null) {
				String hasSecret=getValue(cell);
				if(StringUtils.isNotEmpty(hasSecret)){
					roster.setHasSecret((int)Float.parseFloat(hasSecret)+"");
				}
			}
			
			
			cell = row.getCell(17);
			if (cell != null) {
				String hasCompete=getValue(cell);
				roster.setHasCompete(hasCompete);
			}
			
			cell = row.getCell(18);
			if (cell != null) {
				String education=getValue(cell);
				roster.setEducation(education);
			}
			
			cell = row.getCell(19);
			if (cell != null) {
				String school=getValue(cell);
				roster.setSchool(school);
			}
			
			cell = row.getCell(20);
			if (cell != null) {
				String schoolType=getValue(cell);
				if(StringUtils.isNotEmpty(schoolType)){
					if("985".equals(schoolType)){
						roster.setIsKey("1");
					}else if("211".equals(schoolType)){
						roster.setIsKey("0");
					}else if("985&211".equals(schoolType)){
						roster.setIsKey("2");
					}
				}
			}
			
			cell = row.getCell(21);
			if (cell != null) {
				String specialities=getValue(cell);
				roster.setSpecialities(specialities);
			}
			
			cell = row.getCell(22);
			if (cell != null) {
				String homeType=getValue(cell);
				if(StringUtils.isNotEmpty(homeType)){
					roster.setHomeType((int)Float.parseFloat(homeType)+"");
				}
			}
			
			cell = row.getCell(23);
			if (cell != null) {
				String homeAddress=getValue(cell);
				roster.setHomeAddress(homeAddress);
			}
			
			cell = row.getCell(24);
			if (cell != null) {
				String politicsFace=getValue(cell);
				roster.setPoliticsFace(politicsFace);
			}
			
			cell = row.getCell(25);
			if (cell != null) {
				String isMarry=getValue(cell);
				roster.setIsMarry((int)Float.parseFloat(isMarry)+"");
			}
			
			cell = row.getCell(26);
			if (cell != null) {
				String address=getValue(cell);
				roster.setAddress(address);
			}
			
			cell = row.getCell(27);
			if (cell != null) {
				String address=getValue(cell);
				roster.setFamilyAddress(address);
			}
			
			cell = row.getCell(28);
			if (cell != null) {
				String address=getValue(cell);
				roster.setOriginPlace(address);
			}

			cell = row.getCell(29);
			if (cell != null) {
				String address=getValue(cell);
				roster.setZodiac(address);
			}
			
			cell = row.getCell(30);
			if (cell != null) {
				String address=getValue(cell);
				roster.setEnglishLevel(address);
			}
			
			cell = row.getCell(31);
			if (cell != null) {
				String address=getValue(cell);
				roster.setQualification(address);
			}
			
			
			//紧急联系人
			cell = row.getCell(32);
			if (cell != null) {
				String address=getValue(cell);
				roster.setContactName1(address);
			}
			
			cell = row.getCell(34);
			if (cell != null) {
				String address=getValue(cell);
				roster.setContactPhone1(address);
			}
			
			
			cell = row.getCell(35);
			if (cell != null) {
				String birthDate=getValue(cell);
				if(StringUtils.isNotEmpty(birthDate)){
					roster.setCyContractStartDate(sdf.parse(birthDate));
				}
			}
			
			cell = row.getCell(36);
			if (cell != null) {
				String birthDate=getValue(cell);
				if(StringUtils.isNotEmpty(birthDate)){
					roster.setCyContractEndDate(sdf.parse(birthDate));
				}
			}
			
			cell = row.getCell(37);
			if (cell != null) {
				String cyContractContinue=getValue(cell);
				roster.setCyContractContinue(cyContractContinue);
			}
			
			cell = row.getCell(38);
			if (cell != null) {
				String birthDate=getValue(cell);
				if(StringUtils.isNotEmpty(birthDate)){
					roster.setLcContractStartDate1(sdf.parse(birthDate));
				}
			}
			
			cell = row.getCell(39);
			if (cell != null) {
				String birthDate=getValue(cell);
				if(StringUtils.isNotEmpty(birthDate)){
					roster.setLcContractEndDate1(sdf.parse(birthDate));
				}
			}
			
			
			cell = row.getCell(46);
			if (cell != null) {
				String officeId=getValue(cell);
				roster.setOffice(new Office(officeId));
			}
			
			cell = row.getCell(47);
			if (cell != null) {
				String userId=getValue(cell);
				roster.setUser(new User(userId));
			}
			roster.setCreateDate(new Date());
			roster.setCreateUser(UserUtils.getUserById("1"));
			roster.setDelFlag("0");
			rosters.add(roster);
		}
		
		
		
		this.rosterDao.save(rosters);
  
	}
	
	@SuppressWarnings("static-access")
	private String getValue(Cell cell) {
		if (cell.getCellType() == cell.CELL_TYPE_BOOLEAN) {
			return String.valueOf(cell.getBooleanCellValue()).trim();
		} else if (cell.getCellType() == cell.CELL_TYPE_NUMERIC) {
			 short format = cell.getCellStyle().getDataFormat();  
			    SimpleDateFormat sdf = null;  
			    if(format == 176){  
			        //日期  
			        sdf = new SimpleDateFormat("yyyy-MM-dd");  
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
