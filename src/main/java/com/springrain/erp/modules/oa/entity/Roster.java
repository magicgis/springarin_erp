/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.oa.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import com.springrain.erp.modules.sys.entity.Office;
import com.springrain.erp.modules.sys.entity.User;

/**
 * 请假Entity
 * @author liuj
 * @version 2013-04-05
 */
@Entity
@Table(name = "oa_roster")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Roster  implements Serializable {
	private static final long serialVersionUID = 2043290522905308298L;
	private		 Integer   		 id; 		          	 // id
	private		 String   		 jobNo; 	          	 // 工号
	private      User            user;                   // 员工
	private 	 String	         cnName;            	 // 中文名
	private		 String   		 enName;        	     // 英文名
	private		 String   		 workSta;        	     // 在岗状态
	private      Office          office;                 // 部门
	
	private		 String   		 position;        	     // 职位
	private      Date            entryDate;              // 入职日期
	private      Date            egularDate;             // 转正日期
	
	
	private      Date            birthDate;              // 出生日期
	private      String          sex;                    // 性别
	private      String          idCard;                 // 身份证
	private      String          phone;                  // 联系电话
	private      String          email;                  // E-mail
	
	private       String         hasSecret;              // 是否签订保密协议 0：无    1：有
	private       String         hasCompete;             // 是否签订竞业协议 0：无    1：有
	private       String         education;              // 最终学历
	private       String         school;                 // 毕业学校
	private       String         isKey;                  // 是否是985或者211 
	
	private       String         specialities;           // 专业
	private       String         homeType;               // 户口性质
	private       String         homeAddress;            // 户口所在地
	private       String         politicsFace;           // 政治面貌
	private       String         isMarry;                // 是否结婚
	private       String         address;                // 现居住地址
	
	private       String         familyAddress;          // 家庭地址
	private       String         originPlace;            // 籍贯
	private       String         zodiac;                 // 星座
	private       String         englishLevel;           // 英语等级
	private       String         qualification;          // 职称、职业资格证书
	
	private       String         contactName1;            // 紧急联系人         
	private       String         contactPhone1;           // 紧急联系电话
	private       String         contactRelationship1;    // 关系
	
	private       String         contactName2;            // 紧急联系人         
	private       String         contactPhone2;           // 紧急联系电话
	private       String         contactRelationship2;    // 关系
	
	private       Date           cyContractStartDate;     // 春雨合同起始时间
	private       Date           cyContractEndDate;       // 春雨合同结束时间
	private       String         cyContractContinue;      // 春雨劳动合同续签期限
	
	private       Date           lcContractStartDate1;    // 理诚合同起始时间
	private       Date           lcContractEndDate1;      // 理诚合同结束时间
	private       Date           lcContractStartDate2;    // 理诚合同起始时间
	private       Date           lcContractEndDate2;      // 理诚合同结束时间
	private       Date           lcContractStartDate3;    // 理诚合同起始时间
	private       Date           lcContractEndDate3;      // 理诚合同结束时间
	
	private       Date           leaveDate;               // 离职日期
	private       String         leaveReason;             // 离职原因
	
	private       String         resumeUrl;               // 简历链接
	private       String         resumeFile;              // 简历文件
	private       String         probationUrl;            // 试用期跟踪链接
	private       String         probationFile;           // 试用期跟踪文件
	private       String         trainUrl;                // 培训链接
	private       String         trainFile;               // 培训文件
	
	private       String         innovateUrl;             // 创新链接
	
	private       User           createUser;              // 创建人
	private       Date           createDate;              // 创建时间
	private       User           updateUser;              // 编辑人
	private       Date           updateDate;              // 编辑时间
	private       String         delFlag;                 // 删除状态
	
	private       String         oldOfficeId;             // 老部门id
	
	private       List<RewardPunishmentLog>    logs;                  
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	
	public Roster() {
		super();
	}

	public Roster(Integer id){
		this();
		this.id = id;
	}
	
	
	@Transient
	public String getOldOfficeId() {
		return oldOfficeId;
	}

	public void setOldOfficeId(String oldOfficeId) {
		this.oldOfficeId = oldOfficeId;
	}

	public String getJobNo() {
		return jobNo;
	}

	public void setJobNo(String jobNo) {
		this.jobNo = jobNo;
	}

	
	@ManyToOne()
	@JoinColumn(name="user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne()
	@JoinColumn(name="office")
	@NotFound(action = NotFoundAction.IGNORE)
	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public Date getEgularDate() {
		return egularDate;
	}

	public void setEgularDate(Date egularDate) {
		this.egularDate = egularDate;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getIdCard() {
		return idCard;
	}

	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public String getHasSecret() {
		return hasSecret;
	}

	public void setHasSecret(String hasSecret) {
		this.hasSecret = hasSecret;
	}

	public String getHasCompete() {
		return hasCompete;
	}

	public void setHasCompete(String hasCompete) {
		this.hasCompete = hasCompete;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public String getSchool() {
		return school;
	}

	public void setSchool(String school) {
		this.school = school;
	}

	public String getIsKey() {
		return isKey;
	}

	public void setIsKey(String isKey) {
		this.isKey = isKey;
	}

	public String getSpecialities() {
		return specialities;
	}

	public void setSpecialities(String specialities) {
		this.specialities = specialities;
	}

	public String getHomeType() {
		return homeType;
	}

	public void setHomeType(String homeType) {
		this.homeType = homeType;
	}

	public String getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}

	public String getPoliticsFace() {
		return politicsFace;
	}

	public void setPoliticsFace(String politicsFace) {
		this.politicsFace = politicsFace;
	}

	public String getIsMarry() {
		return isMarry;
	}

	public void setIsMarry(String isMarry) {
		this.isMarry = isMarry;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFamilyAddress() {
		return familyAddress;
	}

	public void setFamilyAddress(String familyAddress) {
		this.familyAddress = familyAddress;
	}

	public String getOriginPlace() {
		return originPlace;
	}

	public void setOriginPlace(String originPlace) {
		this.originPlace = originPlace;
	}

	public String getZodiac() {
		return zodiac;
	}

	public void setZodiac(String zodiac) {
		this.zodiac = zodiac;
	}

	public String getEnglishLevel() {
		return englishLevel;
	}

	public void setEnglishLevel(String englishLevel) {
		this.englishLevel = englishLevel;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getContactName1() {
		return contactName1;
	}

	public void setContactName1(String contactName1) {
		this.contactName1 = contactName1;
	}

	public String getContactPhone1() {
		return contactPhone1;
	}

	public void setContactPhone1(String contactPhone1) {
		this.contactPhone1 = contactPhone1;
	}

	public String getContactRelationship1() {
		return contactRelationship1;
	}

	public String getCnName() {
		return cnName;
	}

	public void setCnName(String cnName) {
		this.cnName = cnName;
	}

	public String getEnName() {
		return enName;
	}

	public void setEnName(String enName) {
		this.enName = enName;
	}

	public String getWorkSta() {
		return workSta;
	}

	public void setWorkSta(String workSta) {
		this.workSta = workSta;
	}


	public String getCyContractContinue() {
		return cyContractContinue;
	}

	public void setCyContractContinue(String cyContractContinue) {
		this.cyContractContinue = cyContractContinue;
	}

	public void setContactRelationship1(String contactRelationship1) {
		this.contactRelationship1 = contactRelationship1;
	}

	public String getContactName2() {
		return contactName2;
	}

	public void setContactName2(String contactName2) {
		this.contactName2 = contactName2;
	}

	public String getContactPhone2() {
		return contactPhone2;
	}

	public void setContactPhone2(String contactPhone2) {
		this.contactPhone2 = contactPhone2;
	}

	public String getContactRelationship2() {
		return contactRelationship2;
	}

	public void setContactRelationship2(String contactRelationship2) {
		this.contactRelationship2 = contactRelationship2;
	}

	public Date getCyContractStartDate() {
		return cyContractStartDate;
	}

	public void setCyContractStartDate(Date cyContractStartDate) {
		this.cyContractStartDate = cyContractStartDate;
	}

	public Date getCyContractEndDate() {
		return cyContractEndDate;
	}

	public void setCyContractEndDate(Date cyContractEndDate) {
		this.cyContractEndDate = cyContractEndDate;
	}

	

	public Date getLcContractStartDate1() {
		return lcContractStartDate1;
	}

	public void setLcContractStartDate1(Date lcContractStartDate1) {
		this.lcContractStartDate1 = lcContractStartDate1;
	}

	public Date getLcContractEndDate1() {
		return lcContractEndDate1;
	}

	public void setLcContractEndDate1(Date lcContractEndDate1) {
		this.lcContractEndDate1 = lcContractEndDate1;
	}

	public Date getLcContractStartDate2() {
		return lcContractStartDate2;
	}

	public void setLcContractStartDate2(Date lcContractStartDate2) {
		this.lcContractStartDate2 = lcContractStartDate2;
	}

	public Date getLcContractEndDate2() {
		return lcContractEndDate2;
	}

	public void setLcContractEndDate2(Date lcContractEndDate2) {
		this.lcContractEndDate2 = lcContractEndDate2;
	}

	public Date getLcContractStartDate3() {
		return lcContractStartDate3;
	}

	public void setLcContractStartDate3(Date lcContractStartDate3) {
		this.lcContractStartDate3 = lcContractStartDate3;
	}

	public Date getLcContractEndDate3() {
		return lcContractEndDate3;
	}

	public void setLcContractEndDate3(Date lcContractEndDate3) {
		this.lcContractEndDate3 = lcContractEndDate3;
	}

	public Date getLeaveDate() {
		return leaveDate;
	}

	public void setLeaveDate(Date leaveDate) {
		this.leaveDate = leaveDate;
	}

	public String getLeaveReason() {
		return leaveReason;
	}

	public void setLeaveReason(String leaveReason) {
		this.leaveReason = leaveReason;
	}

	public String getResumeUrl() {
		return resumeUrl;
	}

	public void setResumeUrl(String resumeUrl) {
		this.resumeUrl = resumeUrl;
	}

	public String getResumeFile() {
		return resumeFile;
	}

	public void setResumeFile(String resumeFile) {
		this.resumeFile = resumeFile;
	}

	public String getProbationUrl() {
		return probationUrl;
	}

	public void setProbationUrl(String probationUrl) {
		this.probationUrl = probationUrl;
	}

	public String getProbationFile() {
		return probationFile;
	}

	public void setProbationFile(String probationFile) {
		this.probationFile = probationFile;
	}

	public String getTrainUrl() {
		return trainUrl;
	}

	public void setTrainUrl(String trainUrl) {
		this.trainUrl = trainUrl;
	}

	public String getTrainFile() {
		return trainFile;
	}

	public void setTrainFile(String trainFile) {
		this.trainFile = trainFile;
	}

	public String getInnovateUrl() {
		return innovateUrl;
	}

	public void setInnovateUrl(String innovateUrl) {
		this.innovateUrl = innovateUrl;
	}

	
	@ManyToOne()
	@JoinColumn(name="create_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}

	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	@ManyToOne()
	@JoinColumn(name="update_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(User updateUser) {
		this.updateUser = updateUser;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public String getDelFlag() {
		return delFlag;
	}

	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}

	
	@OneToMany(mappedBy = "roster",fetch=FetchType.LAZY)
	@Where(clause="del_flag=0")
	@Fetch(FetchMode.SELECT)
	@NotFound(action = NotFoundAction.IGNORE)
	@Cascade(CascadeType.ALL)
	public List<RewardPunishmentLog> getLogs() {
		return logs;
	}

	public void setLogs(List<RewardPunishmentLog> logs) {
		this.logs = logs;
	}

	
	/**
	 * 算出司龄
	 */
	@Transient
	public String getWorkYear(){
		if(this.entryDate!=null){
			long days=(this.leaveDate!=null?this.leaveDate.getTime():new Date().getTime()-this.entryDate.getTime())/(1000*60*60*24);
			long year=days/365;
			long month = (days%365)/30;
			if(year==0){
				return month+"个月";
			}else{
				return year+"年"+month+"个月";
			}
		}
		return "";
	}
	
	
	@Transient
	public Integer getAge(){
		if(birthDate!=null){
			return new Date().getYear()-birthDate.getYear();
		}else{
			return null;
		}
		
	}
	
}


