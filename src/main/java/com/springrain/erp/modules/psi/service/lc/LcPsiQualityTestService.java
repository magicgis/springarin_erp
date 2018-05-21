/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service.lc;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.psi.dao.lc.LcPsiLadingBillDao;
import com.springrain.erp.modules.psi.dao.lc.LcPsiQualityTestDao;
import com.springrain.erp.modules.psi.entity.lc.LcPsiQualityTest;
import com.springrain.erp.modules.psi.service.PsiProductService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;

/**
 * @author Michael
 * @version 2015-07-03
 */
@Component
@Transactional(readOnly = true)
public class LcPsiQualityTestService extends BaseService {
	
	
	@Autowired  
	private LcPsiQualityTestDao      psiQualityTestDao;
	@Autowired  
	private PsiProductService        productService;
	@Autowired
	private MailManager				 mailManager;  
	@Autowired  
	private LcPsiLadingBillDao       psiLadingBillDao;
	
	public List<LcPsiQualityTest> getTestByProductNameColor(Integer ladingId,String productName,String color){
		DetachedCriteria dc = psiQualityTestDao.createDetachedCriteria();
		dc.addOrder(Order.desc("id"));
		if(ladingId!=null){
			dc.add(Restrictions.eq("ladingId", ladingId));
		}
		
		if(StringUtils.isNotEmpty(productName)){
			dc.add(Restrictions.eq("productName", productName));
		}
		
		if(color!=null){
			dc.add(Restrictions.eq("color", color));
		}
		
		return psiQualityTestDao.find(dc);
	}
	
	
	/**
	 * 查询所有不合格的质检单
	 * 
	 */
	public Page<LcPsiQualityTest> findTest(Page<LcPsiQualityTest> page, LcPsiQualityTest test) {
		DetachedCriteria dc = psiQualityTestDao.createDetachedCriteria();
		if(test.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",test.getCreateDate()));
		}
		
		if(test.getSureDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(test.getSureDate(),1)));
		}
		
		if(StringUtils.isNotEmpty(test.getProductName())){
			dc.add(Restrictions.or(Restrictions.like("ladingBillNo", "%"+test.getProductName()+"%"),Restrictions.like("productName", "%"+test.getProductName()+"%")));
		}
		//已处理、未处理
		if(StringUtils.isNotEmpty(test.getDealWay())){
			if("4".equals(test.getDealWay())){
				dc.add(Restrictions.isNotNull("dealWay"));
			}else if("5".equals(test.getDealWay())){
				dc.add(Restrictions.isNull("dealWay"));
			}
		}
		dc.add(Restrictions.eq("isOk", "0"));
		dc.add(Restrictions.ne("testSta", "8"));
		page.setOrderBy(" id desc");
		return psiQualityTestDao.find(page, dc);
	}
	
	
	public LcPsiQualityTest get(Integer id){
		return this.psiQualityTestDao.get(id);
	}
	

	public List<LcPsiQualityTest> getNoReviewTest(List<String> canReviewProductNames){
		DetachedCriteria dc = psiQualityTestDao.createDetachedCriteria();
		dc.addOrder(Order.desc("id"));
		if(canReviewProductNames!=null&&canReviewProductNames.size()>0){
			dc.add(Restrictions.in("productName", canReviewProductNames));
		}
		dc.add(Restrictions.or(Restrictions.eq("isOk", "1"),Restrictions.and(Restrictions.eq("isOk", "0"), Restrictions.ne("dealWay",""))));
		dc.add(Restrictions.eq("testSta", "3"));//申请审核状态的
		return psiQualityTestDao.find(dc);
	}
	
	public List<LcPsiQualityTest> getNoReviewTestById(String ids){
		DetachedCriteria dc = psiQualityTestDao.createDetachedCriteria();
		dc.addOrder(Order.desc("id"));
		if(StringUtils.isNotEmpty(ids)){
			List<Integer> list = Lists.newArrayList();
			for(String id:ids.split(",")){
				list.add(Integer.parseInt(id));
			}
			dc.add(Restrictions.in("id", list));
		}
		dc.add(Restrictions.eq("testSta", "3"));//申请审核状态的
		return psiQualityTestDao.find(dc);
	}
	
	@Transactional( readOnly = false)
	public void qualityTestSave(LcPsiQualityTest test,MultipartFile[] reportPaths) {
		String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/qualityTests";
		if (reportPaths != null) {
			for (MultipartFile attchmentFile : reportPaths) {
				if(attchmentFile.getSize()!=0){
					File baseDir = new File(baseDirStr+"/"+test.getLadingBillNo()); 
					if(!baseDir.isDirectory())
						baseDir.mkdirs();
					String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
					String name=UUID.randomUUID().toString()+suffix;
					File dest = new File(baseDir,name);
					try {
						FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
						test.setReportPathAppend("/psi/qualityTests/"+test.getLadingBillNo()+"/"+name);
					} catch (IOException e) {
						logger.warn(name+"文件保存失败",e);
					}
				}
			}
		}
		
		if("1".equals(test.getIsOk())){
			//如果为合格       合格数和接收数都为质检数
			test.setOkQuantity(test.getTotalQuantity());
			test.setReceivedQuantity(test.getTotalQuantity());
		}else if("0".equals(test.getIsOk())){
			//如果为不合格,合格数为0
			test.setOkQuantity(0);
			test.setReceivedQuantity(0);
		}else if("2".equals(test.getIsOk())){
			test.setReceivedQuantity(test.getOkQuantity());
		}
		test.setCreateDate(new Date());
		test.setCreateUser(UserUtils.getUser());
		
		this.psiQualityTestDao.save(test);//先保存一下，下面要用到id链接
		//如果提交了审核才发信到产品经理       3为申请状态
		//如果为合格、（不合格特采、让步）、（部分合格 特采、让步） 或者返工合格数>0，根据接收数发信
		if("3".equals(test.getTestSta())){
			StringBuilder noticeUser = new StringBuilder(UserUtils.getUser().getLoginName());
			String okAddress=UserUtils.getUser().getEmail();//如果结果ok发信通知品检主管
			StringBuilder noOkAddress= new StringBuilder("emma.chao@inateck.com,lynn@inateck.com,"+UserUtils.getUser().getEmail());//不合格通知各方
		
			//产品经理
			Map<String,Set<String>> mangerEmails=this.productService.findMangerEmailByProductNames();
			if(mangerEmails!=null&&mangerEmails.get(test.getProductName())!=null){
				Set<String> emails =mangerEmails.get(test.getProductName());
				for(String email:emails){
					noOkAddress.append(",").append(email);
					noticeUser.append("|").append(email.substring(0, email.indexOf("@")));
				}
			}
			
			//采购经理
			Map<String,Set<String>> purchaseEmails=this.productService.findPurchaseEmailByProductNames();
			if(purchaseEmails!=null&&purchaseEmails.get(test.getProductName())!=null){
				Set<String> emails =purchaseEmails.get(test.getProductName());
				for(String email:emails){
					noOkAddress.append(",").append(email);
					noticeUser.append("|").append(email.substring(0, email.indexOf("@")));
				}
			}
			
			noOkAddress.append(","+psiLadingBillDao.get(test.getLadingId()).getCreateUser().getEmail());
			
			if("1".equals(test.getIsOk())){
				String subject ="提单["+test.getLadingBillNo()+"] <a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/lcPsiLadingBill/managerReivew?productName="+test.getProductName()+"'>"+test.getProductNameColor()+"</a>品检已验货,准备接收数量["+test.getReceivedQuantity()+"]";
				String subject1 ="提单["+test.getLadingBillNo()+"]"+test.getProductNameColor()+"品检已验货,准备接收数量["+test.getReceivedQuantity()+"]";
				this.sendNoticeEmail(okAddress, "Hi,<br/>&nbsp;&nbsp;&nbsp;&nbsp;"+subject+",请及时登录erp系统审核",subject1 , "", "");
				WeixinSendMsgUtil.sendTextMsgToUser(noticeUser.toString(),subject1+"请及时登录erp系统审核!");
			}else if("0".equals(test.getIsOk())){
				//如果为不合格    如果处理方式为返工，
				if("2".equals(test.getDealWay())){
					String subject ="提单["+test.getLadingBillNo()+"] <a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/lcPsiLadingBill/qualityView?ladingId="+test.getLadingId()+"" +
								"&ladingBillNo="+test.getLadingBillNo()+"&productName="+test.getProductName()+"&color="+test.getColor()+"'>"+test.getProductNameColor()+"</a>品检不合格,直接返工！";
					String subject1 ="提单["+test.getLadingBillNo()+"]"+test.getProductNameColor()+"品检不合格,直接返工！";
					this.sendNoticeEmail(okAddress, "Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;"+subject+"",subject1 , "", "");
					WeixinSendMsgUtil.sendTextMsgToUser("austin|"+UserUtils.getUser().getLoginName(),subject1+"请及时确认该质检单！");
				}else{
					String subject ="提单["+test.getLadingBillNo()+"] <a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/lcPsiLadingBill/testReview?id="+test.getId()+"'>"+test.getProductNameColor()+"</a>品检不合格，请尽快协商是'特采'还是'返工'！";
					String subject1 ="提单["+test.getLadingBillNo()+"]"+test.getProductNameColor()+"品检不合格";
					this.sendNoticeEmail(noOkAddress.toString(), "Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;"+subject+"",subject1 , "", "");
					WeixinSendMsgUtil.sendTextMsgToUser(noticeUser+"|emma_chao|lynn",subject1+"请尽快协商是'特采'还是'返工'!");
				}
			}else if("2".equals(test.getIsOk())){
				Integer unOkQuantity = test.getTotalQuantity()-test.getOkQuantity();
				//如果为部分合格,生成个不合格的
				if(unOkQuantity.intValue()>0){
					LcPsiQualityTest test1 = new LcPsiQualityTest(test.getAql(), test.getTestQuantity(), test.getInView(), test.getOutView(), test.getPacking(), test.getFunction(),
							"0", null, test.getReason(), test.getReportFile(), null, test.getLadingId(), test.getLadingBillNo(), test.getProductId(), test.getProductName(), test.getColor(),
							UserUtils.getUser(), new Date(), null, null, 0, 0, unOkQuantity, 
							test.getTestSta(), null, null, test.getSupplierId());
					this.psiQualityTestDao.save(test1);
					//如果为不合格    如果处理方式为返工，
					if("2".equals(test.getDealWay())){
						test1.setDealWay(test.getDealWay());//qualityView?ladingId=340&ladingBillNo=20170107AKA_LC_T  &productName=Inateck%20FE3001&color=
						this.psiQualityTestDao.save(test1);
						String subject ="提单["+test.getLadingBillNo()+"] <a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/lcPsiLadingBill/qualityView?ladingId="+test.getLadingId()+"" +
								"&ladingBillNo="+test.getLadingBillNo()+"&productName="+test.getProductName()+"&color="+test.getColor()+"'>"+test.getProductNameColor()+"</a>品检部分合格,不合格部分,直接返工！";
						String subject1 ="提单["+test.getLadingBillNo()+"]"+test.getProductNameColor()+"品检部分合格,不合格部分,直接返工！";
						this.sendNoticeEmail(okAddress, "Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;"+subject+"",subject1 , "", "");
						WeixinSendMsgUtil.sendTextMsgToUser("austin|"+UserUtils.getUser().getLoginName(),subject1+"请及时确认该质检单！");
					}else{
						String subject ="提单["+test.getLadingBillNo()+"] <a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/lcPsiLadingBill/testReview?id="+test.getId()+"'>"+test.getProductNameColor()+"</a>品检不合格，请尽快协商是'特采'还是'返工'！";
						String subject1 ="提单["+test.getLadingBillNo()+"]"+test.getProductNameColor()+"品检不合格";
						this.sendNoticeEmail(noOkAddress.toString(), "Hi,All<br/>&nbsp;&nbsp;&nbsp;&nbsp;"+subject+"",subject1 , "", "");
						WeixinSendMsgUtil.sendTextMsgToUser(noticeUser+"|emma_chao|lynn",subject1+"请尽快协商是'特采'还是'返工'!");
					}
					
					
				}
				test.setDealWay(null);//原来的变成合格的；
				test.setIsOk("1");
				test.setTotalQuantity(test.getOkQuantity());
				test.setReceivedQuantity(test.getOkQuantity());
				
				String subject ="提单["+test.getLadingBillNo()+"] <a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/lcPsiLadingBill/managerReivew?productName="+test.getProductName()+"'>"+test.getProductNameColor()+"</a>品检已验货,准备接收数量["+test.getReceivedQuantity()+"]";
				String subject1 ="提单["+test.getLadingBillNo()+"]"+test.getProductNameColor()+"品检已验货,准备接收数量["+test.getReceivedQuantity()+"]";
				WeixinSendMsgUtil.sendTextMsgToUser(noticeUser.toString(),subject1+"请及时登录erp系统审核!");
				this.sendNoticeEmail(okAddress, "Hi,<br/>&nbsp;&nbsp;&nbsp;&nbsp;"+subject+",请及时登录erp系统审核",subject1 , "", "");
			}
		}
		
		test.setCreateDate(new Date());
		test.setCreateUser(UserUtils.getUser());
		this.psiQualityTestDao.save(test);
		//更新提单质检人
		this.updateTestUser(test.getLadingId());
	}
	
	
	
	//三个部门协商结果保存
	@Transactional( readOnly = false)
	public void qualityTestReviewSave(LcPsiQualityTest test,MultipartFile[] giveInPaths) {
		String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/qualityTests";
		
		String dealWay = test.getDealWay();
		String remark = test.getReviewRemark();
		test = psiQualityTestDao.get(test.getId());
		
		for (MultipartFile attchmentFile : giveInPaths) {
			if(attchmentFile.getSize()!=0){
				File baseDir = new File(baseDirStr+"/"+test.getLadingBillNo());  
				if(!baseDir.isDirectory())
					baseDir.mkdirs();
				String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
				String name=UUID.randomUUID().toString()+suffix;
				File dest = new File(baseDir,name);
				try {
					FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
					test.setGiveInPathAppend("/psi/qualityTests/"+test.getLadingBillNo()+"/"+name);
				} catch (IOException e) {
					logger.warn(name+"文件保存失败",e);
				}
			}
		}
		
		//保存三方协商拍板人信息
		test.setReviewDate(new Date());
		test.setReviewUser(UserUtils.getUser());
		test.setReviewRemark(remark);
		test.setDealWay(dealWay);
		
		if("0".equals(test.getDealWay())){
			test.setReceivedQuantity(test.getTotalQuantity());//特采特采接收数改为检测数
		}
		this.psiQualityTestDao.save(test);
	}
	
	
	/**
	 *查出产品经理已经确认的品检数量 
	 */
	public Map<String,String> getTestQuantity(Integer ladingId){
		Map<String,String>  rs = Maps.newHashMap();
		String sql=" SELECT SUM(a.`received_quantity`),SUM(CASE WHEN a.sure_user IS NOT NULL THEN a.`received_quantity` ELSE 0 END) as aaa,(CASE WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color`) END) AS proName  FROM lc_psi_quality_test AS a  WHERE a.`lading_id`=:p1 AND a.`sure_user` IS NOT NULL  GROUP BY a.`product_name`,a.`color` ";
		List<Object[]> list=this.psiQualityTestDao.findBySql(sql, new Parameter(ladingId));
		for(Object[] obj:list){
			String  productName = obj[2].toString();
			rs.put(productName, obj[0]+","+obj[1]);
		}
		return rs;
	}
	
	
	@Transactional( readOnly = false)
	public void qualityTestFileSave(LcPsiQualityTest test,MultipartFile[] reportPaths) {
		String baseDirStr = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+Global.getCkBaseDir()+"/psi/qualityTests";
		test.setReportFile(null);
		for (MultipartFile attchmentFile : reportPaths) {
			if(attchmentFile.getSize()!=0){
				File baseDir = new File(baseDirStr+"/"+test.getLadingBillNo()); 
				if(!baseDir.isDirectory())
					baseDir.mkdirs();
				String suffix = attchmentFile.getOriginalFilename().substring(attchmentFile.getOriginalFilename().lastIndexOf("."));     
				String name=UUID.randomUUID().toString()+suffix;
				File dest = new File(baseDir,name);
				try {
					FileUtils.copyInputStreamToFile(attchmentFile.getInputStream(),dest);
					test.setReportPathAppend("/psi/qualityTests/"+test.getLadingBillNo()+"/"+name);
				} catch (IOException e) {
					logger.warn(name+"文件保存失败",e);
				}
			}
		}
		this.psiQualityTestDao.save(test);
	}
	
	/**
	 *查出产品经理已经确认的品检数量   sta：5
	 */
	public Map<String,Integer> getCanLadingQuantity(Integer ladingId){
		Map<String,Integer>  rs = Maps.newHashMap();
		String sql=" SELECT SUM(a.`received_quantity`),(CASE WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color`) END) AS proName  FROM lc_psi_quality_test AS a  WHERE a.`lading_id`=:p1 AND a.test_sta='5'  GROUP BY a.`product_name`,a.`color` ";
		List<Object[]> list=this.psiQualityTestDao.findBySql(sql, new Parameter(ladingId));
		for(Object[] obj:list){
			String  productName = obj[1].toString();
			rs.put(productName, Integer.parseInt(obj[0].toString()));
		}
		return rs;
	}
	
	
	/**
	 *查出已品检数量 key:billNo_product_color   // 未审核的占用不合格总数
	 */
	public Map<String,String> getTestQuantityAll(Set<Integer> ladingIds){
		Map<String,String>  rs = Maps.newHashMap();
		String sql=" SELECT SUM(CASE WHEN a.`deal_way` IS  NULL AND a.`is_ok`='0' THEN a.`total_quantity` ELSE a.`received_quantity` END ),SUM(CASE WHEN a.test_sta='5' THEN a.`received_quantity` ELSE 0 END) as aaa,(CASE WHEN a.`color`='' THEN CONCAT(a.`lading_bill_no`,'_',a.`product_name`) ELSE CONCAT(a.`lading_bill_no`,'_',a.`product_name`,'_',a.`color`) END) AS conKey  FROM lc_psi_quality_test AS a  WHERE a.test_sta<>'8' AND a.`lading_id` in :p1 GROUP BY a.`lading_bill_no`,a.`product_name`,a.`color` ";
		List<Object[]> list=this.psiQualityTestDao.findBySql(sql, new Parameter(ladingIds));
		for(Object[] obj:list){
			String  key = obj[2].toString();
			rs.put(key, obj[0]+","+obj[1]);
		}
		return rs;
	}
	
	
	public List<String> getLadingBillNos(){
		String sql=" SELECT a.`lading_bill_no` FROM lc_psi_quality_test AS a GROUP BY a.`lading_id`";
		return this.psiQualityTestDao.findBySql(sql);
	}
	
	
	
	/**
	 *查出已品检数量 key:billNo_product_color
	 */
	public Map<String,List<String>> getTestInfo(Set<Integer> ladingIds){
		Map<String,List<String>>  rs = Maps.newHashMap();
		//先找出id，再找出
		String	sql="  SELECT CONCAT(DATE_FORMAT(a.`create_date`,'%Y-%m-%d'),',',a.test_sta,'_',a.is_ok,'_',(CASE WHEN (a.`is_ok`='0' AND a.deal_way IS NULL ) THEN '8' ELSE '9' END)),(CASE WHEN a.`color`='' THEN CONCAT(a.`lading_bill_no`,'_',a.`product_name`) ELSE CONCAT(a.`lading_bill_no`,'_',a.`product_name`,'_',a.`color`) END) AS conKey  FROM lc_psi_quality_test AS a  WHERE a.`lading_id` in :p1 ORDER BY a.`id` DESC";
		List<Object[]> list = this.psiQualityTestDao.findBySql(sql,new Parameter(ladingIds));
		for(Object[] obj:list){
			String  key = obj[1].toString();
			List<String> tempList = null;
			if(rs.get(key)==null){
				tempList = Lists.newArrayList();
			}else{
				tempList = rs.get(key);
			}
			tempList.add(obj[0].toString());
			rs.put(key, tempList);
		}
		return rs;
	}
	
	
	/**
	 * sendemail
	 */
	public void sendNoticeEmail(String email,String content,String subject,String ccEmail,String bccEmail){
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();
			final MailInfo mailInfo = new MailInfo(email,subject+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(content);
			if(StringUtils.isNotEmpty(bccEmail)){
				mailInfo.setBccToAddress(bccEmail);
			}
			if(StringUtils.isNotEmpty(ccEmail)){
				mailInfo.setCcToAddress(ccEmail);
			}
			//发送成功不成功都能保存
			new Thread(){
				@Override
				public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
		}
	}
	
	@Transactional( readOnly = false)
	public void updateTestUser(Integer ladingId){
		String sql=" UPDATE lc_psi_lading_bill AS a SET a.`test_user`=:p2 WHERE a.`id`=:p1";
		this.psiQualityTestDao.updateBySql(sql, new Parameter(ladingId,UserUtils.getUser().getId()));
	}
	
	
	@Transactional( readOnly = false)
	public void save(LcPsiQualityTest test){
		this.psiQualityTestDao.save(test);
	}
	
	
	/**
	 * 查询一段时间内所有产品的合格率
	 */
	 public Map<String,Object[]> getOkRate(Date startDate,Date endDate){
		 Map<String,Object[]> rs = Maps.newHashMap();
		 String sql="SELECT CASE WHEN c.`color`='' THEN c.`product_name` ELSE CONCAT(c.`product_name`,'_',c.`color`)  END  AS proName,c.`product_name`, SUM(1),SUM(CASE WHEN (c.okStr LIKE '%0%' ||  c.okStr LIKE '%2%')='1' THEN 1 ELSE 0 END) as unOk, " +
		 		" (SUM(1)-SUM(CASE WHEN (c.okStr LIKE '%0%' ||  c.okStr LIKE '%2%')='1' THEN 1 ELSE 0 END))*100/SUM(1) AS okRate,c.okStr " +
		 		" FROM (SELECT a.`lading_bill_no`,a.`product_name`,a.`color`,GROUP_CONCAT(a.`is_ok`) AS okStr" +
			 	" FROM lc_psi_quality_test AS a WHERE a.test_sta<>'8' AND a.`create_date` BETWEEN :p1 AND :p2  GROUP BY a.`lading_bill_no`,a.`product_name`,a.`color`) AS c GROUP BY c.`product_name`,c.`color` ";
		List<Object[]> list = this.psiQualityTestDao.findBySql(sql,new Parameter(startDate,endDate));	
		 for(Object[] obj: list){
			 String key =obj[0].toString();
			 rs.put(key, obj);
		 }
		 return rs;
	 }
	

 	/**
	 * 查询一段时间内供应商的合格率
	 */
	 public List<Object[]> getOkRateBySupplier(Date startDate,Date endDate){
		 String sql="SELECT c.supplier_id, SUM(1),SUM(CASE WHEN (c.okStr LIKE '%0%' ||  c.okStr LIKE '%2%')='1' THEN 1 ELSE 0 END) AS unOk, "+
	 		 " (SUM(1)-SUM(CASE WHEN (c.okStr LIKE '%0%' ||  c.okStr LIKE '%2%')='1' THEN 1 ELSE 0 END))*100/SUM(1) AS okRate "+
	 		 " FROM (SELECT a.supplier_id,a.`lading_bill_no`,a.`product_name`,a.`color`,GROUP_CONCAT(a.`is_ok`) AS okStr"+
		 	 " FROM lc_psi_quality_test AS a WHERE a.test_sta<>'8' AND a.`create_date` BETWEEN :p1 AND :p2  GROUP BY a.`lading_bill_no`,a.`product_name`,a.`color`) AS c GROUP BY c.supplier_id	";
		return this.psiQualityTestDao.findBySql(sql,new Parameter(startDate,endDate));	
	 }
	 
	 
	 /**
	 * 按供应商按月统计合格率
	 * Map<String,Map<String,"不合格数，合格数">>
	 */
	 public Map<Integer,Map<String,String>> getOkRateBySupplierByMonth(Date startDate,Date endDate,Integer supId,String productName){
		 Map<Integer,Map<String,String>>  rs = Maps.newHashMap();
		 
		 String sql="";
		 List<Object[]>  list=null;
		 Parameter para= null;
		 //供应商id不为空   产品名为空
		 if(supId==null&&StringUtils.isEmpty(productName)){
			 sql=" SELECT c.supplier_id,c.monthStr, SUM(1),SUM(CASE WHEN (c.okStr LIKE '%0%' ||  c.okStr LIKE '%2%')='1' THEN 1 ELSE 0 END) AS unOk"+
			 		  " FROM (SELECT a.supplier_id,a.`lading_bill_no`,a.`product_name`,a.`color`,GROUP_CONCAT(a.`is_ok`) AS okStr,SUBSTRING(a.`lading_bill_no`,1,6) AS monthStr"+
				 	 " FROM lc_psi_quality_test AS a WHERE a.test_sta<>'8' AND a.`create_date` BETWEEN :p1 AND :p2  GROUP BY a.`lading_bill_no`,a.`product_name`,a.`color`) AS c GROUP BY c.supplier_id,c.monthStr";
			 para= new Parameter(startDate,endDate);
			 list= this.psiQualityTestDao.findBySql(sql,para);	
		 }else if(supId!=null&&StringUtils.isEmpty(productName)){
			 sql=" SELECT c.supplier_id,c.monthStr, SUM(1),SUM(CASE WHEN (c.okStr LIKE '%0%' ||  c.okStr LIKE '%2%')='1' THEN 1 ELSE 0 END) AS unOk"+
			 		  " FROM (SELECT a.supplier_id,a.`lading_bill_no`,a.`product_name`,a.`color`,GROUP_CONCAT(a.`is_ok`) AS okStr,SUBSTRING(a.`lading_bill_no`,1,6) AS monthStr"+
				 	 " FROM lc_psi_quality_test AS a WHERE a.test_sta<>'8' AND a.supplier_id =:p3 AND a.`create_date` BETWEEN :p1 AND :p2  GROUP BY a.`lading_bill_no`,a.`product_name`,a.`color`) AS c GROUP BY c.supplier_id,c.monthStr";
			 para= new Parameter(startDate,endDate,supId);
			 list= this.psiQualityTestDao.findBySql(sql,para);	
		 }else if(supId==null&&StringUtils.isNotEmpty(productName)){
			 sql=" SELECT c.supplier_id,c.monthStr, SUM(1),SUM(CASE WHEN (c.okStr LIKE '%0%' ||  c.okStr LIKE '%2%')='1' THEN 1 ELSE 0 END) AS unOk"+
			 		  " FROM (SELECT a.supplier_id,a.`lading_bill_no`,a.`product_name`,a.`color`,GROUP_CONCAT(a.`is_ok`) AS okStr,SUBSTRING(a.`lading_bill_no`,1,6) AS monthStr"+
				 	 " FROM lc_psi_quality_test AS a WHERE a.test_sta<>'8' AND (CASE WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color`)  END)=:p3 AND a.`create_date` BETWEEN :p1 AND :p2  GROUP BY a.`lading_bill_no`,a.`product_name`,a.`color`) AS c GROUP BY c.supplier_id,c.monthStr";
			 para= new Parameter(startDate,endDate,productName);
			 list= this.psiQualityTestDao.findBySql(sql,para);	
		 }else{
			 sql=" SELECT c.supplier_id,c.monthStr, SUM(1),SUM(CASE WHEN (c.okStr LIKE '%0%' ||  c.okStr LIKE '%2%')='1' THEN 1 ELSE 0 END) AS unOk"+
			 		  " FROM (SELECT a.supplier_id,a.`lading_bill_no`,a.`product_name`,a.`color`,GROUP_CONCAT(a.`is_ok`) AS okStr,SUBSTRING(a.`lading_bill_no`,1,6) AS monthStr"+
				 	 " FROM lc_psi_quality_test AS a WHERE a.test_sta<>'8' AND a.supplier_id =:p3 AND (CASE WHEN a.`color`='' THEN a.`product_name` ELSE CONCAT(a.`product_name`,'_',a.`color`)  END)=:p4 AND a.`create_date` BETWEEN :p1 AND :p2  GROUP BY a.`lading_bill_no`,a.`product_name`,a.`color`) AS c GROUP BY c.supplier_id,c.monthStr";
			 para= new Parameter(startDate,endDate,supId,productName);
			 list= this.psiQualityTestDao.findBySql(sql,para);	
		 }
		 
		 for(Object[] obj:list){
			 Integer supplierId = Integer.parseInt(obj[0].toString()); 
			 String month = obj[1].toString();
			 Integer allQ = Integer.parseInt(obj[2].toString());
			 Integer unOkQ = Integer.parseInt(obj[3].toString());
			 Map<String,String> inMap =null;
			 if(rs.get(supplierId)==null){
				 inMap = Maps.newHashMap();
			 }else{
				 inMap = rs.get(supplierId);
			 }
			 
			 String qStr=allQ+","+unOkQ;
			 String totalStr=allQ+","+unOkQ;
			 if(StringUtils.isNotEmpty(inMap.get(month))){
				 String arr[]= inMap.get(month).split(",");
				 qStr=(allQ+Integer.parseInt(arr[0]))+","+(unOkQ+Integer.parseInt(arr[1]));
			 }
			 
			 inMap.put(month, qStr);
			 
			 if(StringUtils.isNotEmpty(inMap.get("total"))){
				 String arr[]= inMap.get("total").split(",");
				 totalStr=(allQ+Integer.parseInt(arr[0]))+","+(unOkQ+Integer.parseInt(arr[1]));
			 }
			 inMap.put("total", totalStr);
			 
			 
			 
			 
			 String qTotalStr=allQ+","+unOkQ;
			 String totalTotalStr=allQ+","+unOkQ;
			 Map<String,String>  totalInMap = null;
			 //不分供应商  supplierId:0
			 if(rs.get(0)==null){
				 totalInMap=Maps.newHashMap();
			 }else{
				 totalInMap=rs.get(0);
			 }
			 
			 if(StringUtils.isNotEmpty(totalInMap.get(month))){
				 String arr[]= totalInMap.get(month).split(",");
				 qTotalStr=(allQ+Integer.parseInt(arr[0]))+","+(unOkQ+Integer.parseInt(arr[1]));
			 }
			 
			 totalInMap.put(month, qTotalStr);
			 if(StringUtils.isNotEmpty(totalInMap.get("total"))){
				 String arr[]= totalInMap.get("total").split(",");
				 totalTotalStr=(allQ+Integer.parseInt(arr[0]))+","+(unOkQ+Integer.parseInt(arr[1]));
			 }
			 totalInMap.put("total", totalTotalStr);
			 rs.put(0, totalInMap);
			 
			 
			 
			 
			 rs.put(supplierId, inMap);
		 }
		 
		 
		 return rs;
	 }
		 
		 
		 

	/**
	 * 查询供应商对应的产品
	 */
	 public Map<String,String> getSupplierProduct(){
		 Map<String,String> rs =Maps.newHashMap();
		 String sql="SELECT CONCAT(c.`brand`,' ',c.`model`),GROUP_CONCAT(a.`nikename`) FROM psi_supplier AS a ,psi_product_supplier AS b,psi_product AS c WHERE a.`id`=b.`supplier_id` AND c.id=b.`product_id` GROUP BY b.`product_id`";
		 List<Object[]> list = this.psiQualityTestDao.findBySql(sql);
		 for(Object[] obj: list){
			 String key =obj[0].toString();
			 String nikeName=obj[1].toString();
			 rs.put(key, nikeName);
		 }
		 return rs;
	 }


	 @Transactional(readOnly = false)
		public String updateRemark(Integer id,String remark,String flag){
			try{
				LcPsiQualityTest test = this.get(id);
				Date date = new Date();
				User user = UserUtils.getUser();
				if("1".equals(flag)){
					test.setReviewDate1(date);
					test.setReviewUser1(user);
					test.setReviewRemark1(remark);
				}else if("2".equals(flag)){
					test.setReviewDate2(date);
					test.setReviewUser2(user);
					test.setReviewRemark2(remark);
				}else if("3".equals(flag)){
					test.setReviewDate3(date);
					test.setReviewUser3(user);
					test.setReviewRemark3(remark);
				}
				return "true";
			}catch (Exception ex){
				return "false";
			}
		}
	 
	 
	public List<Integer> getHasTest(){
		String sql="SELECT DISTINCT(a.`lading_id`) FROM lc_psi_quality_test AS a WHERE a.`test_sta`=5 AND a.`received_quantity`>0 ";
		 return this.psiQualityTestDao.findBySql(sql);
	}
	
}
