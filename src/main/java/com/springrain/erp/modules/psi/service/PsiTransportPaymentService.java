/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.email.MailInfo;
import com.springrain.erp.common.email.MailManager;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.Collections3;
import com.springrain.erp.common.utils.DateUtils;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.common.utils.pdf.PdfUtil;
import com.springrain.erp.modules.psi.dao.PsiProductAvgPriceDao;
import com.springrain.erp.modules.psi.dao.PsiProductLocalAvgPriceDao;
import com.springrain.erp.modules.psi.dao.PsiProductTransportRateDao;
import com.springrain.erp.modules.psi.dao.PsiTransportOrderDao;
import com.springrain.erp.modules.psi.dao.PsiTransportPaymentDao;
import com.springrain.erp.modules.psi.entity.PsiProductAvgPrice;
import com.springrain.erp.modules.psi.entity.PsiProductLocalAvgPrice;
import com.springrain.erp.modules.psi.entity.PsiProductTransportRate;
import com.springrain.erp.modules.psi.entity.PsiTransportDto;
import com.springrain.erp.modules.psi.entity.PsiTransportOrder;
import com.springrain.erp.modules.psi.entity.PsiTransportPayment;
import com.springrain.erp.modules.psi.entity.PsiTransportPaymentItem;
import com.springrain.erp.modules.sys.dao.GenerateSequenceDao;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;
import com.springrain.erp.modules.weixin.utils.WeixinSendMsgUtil;

/**
 * 运单付款明细表Service
 * 
 * @author Michael
 * @version 2015-01-21
 */
@Component
@Transactional(readOnly = true)
public class PsiTransportPaymentService extends BaseService {
	@Autowired
	private PsiTransportPaymentDao psiTransportPaymentDao;
	@Autowired
	private PsiTransportOrderDao psiTransportOrderDao;
	@Autowired
	private PsiSupplierService psiSupplierService;
	@Autowired
	private SystemService systemService;
	@Autowired
	private GenerateSequenceDao genDao;
	@Autowired
	private MailManager mailManager;
	@Autowired
	private PsiProductTransportRateDao psiProductTransportRateDao;
	@Autowired
	private PsiProductAvgPriceDao psiProductAvgPriceDao;
	@Autowired
	private PsiProductLocalAvgPriceDao PsiProductLocalAvgPriceDao;
	
	@Transactional(readOnly = false)
	public void saveRateList(List<PsiProductTransportRate> psiProductTransportRates) {
		psiProductTransportRateDao.save(psiProductTransportRates);
	}
	
	@Transactional(readOnly = false)
	public void saveAvgPriceList(List<PsiProductAvgPrice> psiProductAvgPrices) {
		psiProductAvgPriceDao.save(psiProductAvgPrices);
	}
	
	@Transactional(readOnly = false)
	public void saveLocalAvgPriceList(List<PsiProductLocalAvgPrice> psiProductAvgPrices) {
		PsiProductLocalAvgPriceDao.save(psiProductAvgPrices);
	}

	public PsiTransportPayment get(Integer id) {
		return psiTransportPaymentDao.get(id);
	}

	public Page<PsiTransportPayment> find(Page<PsiTransportPayment> page,
			PsiTransportPayment psiTransportPayment) {
		DetachedCriteria dc = psiTransportPaymentDao.createDetachedCriteria();
		if (psiTransportPayment.getCreateDate() != null) {
			dc.add(Restrictions.ge("createDate",
					psiTransportPayment.getCreateDate()));
		}

		if (psiTransportPayment.getUpdateDate() != null) {
			dc.add(Restrictions.le("createDate",
					DateUtils.addDays(psiTransportPayment.getUpdateDate(), 1)));
		}

		if (StringUtils.isNotEmpty(psiTransportPayment.getPaymentNo())) {
			dc.add(Restrictions.like("paymentNo",
					"%" + psiTransportPayment.getPaymentNo() + "%"));
		}

		if (StringUtils.isNotEmpty(psiTransportPayment.getPaymentSta())) {
			dc.add(Restrictions.eq("paymentSta",
					psiTransportPayment.getPaymentSta()));
		} else {
			dc.add(Restrictions.ne("paymentSta", "8"));
		}

		if (psiTransportPayment.getSupplier() != null
				&& psiTransportPayment.getSupplier().getId() != null) {
			dc.add(Restrictions.eq("supplier",
					psiTransportPayment.getSupplier()));
		}
		dc.addOrder(Order.desc("id"));

		return psiTransportPaymentDao.find(page, dc);
	}

	@Transactional(readOnly = false)
	public void addSave(PsiTransportPayment psiTransportPayment) {
		// 生成单号
		String tranPaymentNo = genDao.genSequenceByMonth("_YDFK", 3);
		psiTransportPayment.setPaymentNo(tranPaymentNo);
		if (psiTransportPayment.getItems() != null) {
			for (PsiTransportPaymentItem item : psiTransportPayment.getItems()) {
				item.setTransportPayment(psiTransportPayment);
				// 如果付款类型为空，直接跳出
				if (StringUtils.isEmpty(item.getPaymentType())) {
					return;
				}
			}
			psiTransportPayment.setCreateDate(new Date());
			psiTransportPayment.setCreateUser(UserUtils.getUser());

			if ("1".equals(psiTransportPayment.getPaymentSta())) {
				// 如果是申请发信通知
				psiTransportPayment.setPayFlowNo(genDao.genSequenceByMonth2("CY-TRA-", 4));
				
				List<User> userList = systemService.findUserByPermission("psi:tranPayment:review");
				List<User> replys = Lists.newArrayList();
				if (userList != null) {
					replys.addAll(userList);
				}
				String toAddress = Collections3.extractToString(replys, "email", ",");
				
				String content = "运单付款编号：<a href='" + BaseService.BASE_WEBPATH
						+ Global.getAdminPath()
						+ "/psi/psiTransportPayment/review?id="
						+ psiTransportPayment.getId() + "'>"
						+ psiTransportPayment.getPaymentNo()
						+ "</a>已创建，请尽快登陆erp系统审批";
				if (StringUtils.isNotBlank(content)) {
					Date date = new Date();
					final MailInfo mailInfo = new MailInfo(toAddress,
							"运单付款单已创建待审批" + DateUtils.getDate("-yyyy/M/dd"),
							date);
					mailInfo.setContent(content);
					mailInfo.setCcToAddress(UserUtils.getUser().getEmail());
					// 发送成功不成功都能保存
					new Thread() {
						@Override
						public void run() {
							mailManager.send(mailInfo);
						}
					}.start();
				}
			}

			psiTransportPaymentDao.save(psiTransportPayment);
		}

	}

	@Transactional(readOnly = false)
	public void editSave(PsiTransportPayment psiTransportPayment) {
		Set<String> setNewIds = new HashSet<String>();
		if (psiTransportPayment.getItems() != null) {
			// 生成单号
			for (PsiTransportPaymentItem item : psiTransportPayment.getItems()) {
				if (item.getId() != null) {
					setNewIds.add(item.getId().toString());
				}
				item.setTransportPayment(psiTransportPayment);
			}

			String[] oldIds = psiTransportPayment.getOldItemIds().split(",");
			if (setNewIds != null && setNewIds.size() > 0) {
				for (int j = 0; j < oldIds.length; j++) {
					if (!setNewIds.contains(oldIds[j])) {
						// 不包含就干掉
						this.psiTransportPaymentDao.deleteOrderItem(Integer
								.valueOf(oldIds[j]));
					}
					;
				}
			} else {
				for (int j = 0; j < oldIds.length; j++) {
					this.psiTransportPaymentDao.deleteOrderItem(Integer
							.valueOf(oldIds[j]));
				}
			}

			psiTransportPayment.setUpdateDate(new Date());
			psiTransportPayment.setUpdateUser(UserUtils.getUser());

			if ("1".equals(psiTransportPayment.getPaymentSta())) {
				psiTransportPayment.setPayFlowNo(genDao.genSequenceByMonth2("CY-TRA-", 4));
				// 如果是申请发信通知运单付款审核人
				List<User> userList = systemService.findUserByPermission("psi:tranPayment:review");
				List<User> replys = Lists.newArrayList();
				if (userList != null) {
					replys.addAll(userList);
				}
				String toAddress = Collections3.extractToString(replys, "email", ",");
				
				String content = "运单付款编号：<a href='" + BaseService.BASE_WEBPATH
						+ Global.getAdminPath()
						+ "/psi/psiTransportPayment/review?id="
						+ psiTransportPayment.getId() + "'>"
						+ psiTransportPayment.getPaymentNo()
						+ "</a>已创建，请尽快登陆erp系统审批";
				if (StringUtils.isNotBlank(content)) {
					Date date = new Date();
					final MailInfo mailInfo = new MailInfo(toAddress,
							"运单付款单已创建待审批" + DateUtils.getDate("-yyyy/M/dd"),
							date);
					mailInfo.setContent(content);
					mailInfo.setCcToAddress(UserUtils.getUser().getEmail());
					// 发送成功不成功都能保存
					new Thread() {
						@Override
						public void run() {
							mailManager.send(mailInfo);
						}
					}.start();
				}
			}

			psiTransportPaymentDao.save(psiTransportPayment);
		}
	}

	@Transactional(readOnly = false)
	public void sureSave(PsiTransportPayment psiTransportPayment,
			MultipartFile[] attchmentFiles) {

		Map<Integer, PsiTransportOrder> maps = Maps.newHashMap();
		if (psiTransportPayment.getId() != null) {
			psiTransportPayment = this.psiTransportPaymentDao
					.get(psiTransportPayment.getId());

			for (PsiTransportPaymentItem item : psiTransportPayment.getItems()) {
				Integer tranId = item.getTranOrderId();
				PsiTransportOrder tranOrder = new PsiTransportOrder();
				if (maps.get(tranId) != null) {
					tranOrder = maps.get(tranId);
				}
				String payType = item.getPaymentType();
				Float paymentAmount = item.getPaymentAmount();
				// LocalAmount TranAmount DapAmount OtherAmount InsuranceAmount
				// TaxAmount
				if (payType.equals("LocalAmount")) {
					tranOrder.setPayAmount1(paymentAmount); // 记录付款后的人民币额度
															// 取跟原来要付的金额相等的值，转化后的值有可能没记录
				} else if (payType.equals("TranAmount")) {
					tranOrder.setPayAmount2(paymentAmount);
				} else if (payType.equals("DapAmount")) {
					tranOrder.setPayAmount3(paymentAmount);
				} else if (payType.equals("OtherAmount")) {
					tranOrder.setPayAmount4(paymentAmount);
				} else if (payType.equals("InsuranceAmount")) {
					tranOrder.setPayAmount5(paymentAmount);
				} else if (payType.equals("TaxAmount")) {
					tranOrder.setPayAmount6(paymentAmount);
				} else if (payType.equals("OtherAmount1")) {
					tranOrder.setPayAmount7(paymentAmount);
				}
				maps.put(tranId, tranOrder);
			}

			// 遍历map，更新运单主表信息
			for (Map.Entry<Integer, PsiTransportOrder> entry : maps.entrySet()) {
				Integer key = entry.getKey();
				PsiTransportOrder order = this.psiTransportOrderDao.get(key);
				PsiTransportOrder temp = entry.getValue();
				if (temp.getPayAmount1() != null) {
					order.setPayAmount1(temp.getPayAmount1());
				}
				if (temp.getPayAmount2() != null) {
					order.setPayAmount2(temp.getPayAmount2());
				}
				if (temp.getPayAmount3() != null) {
					order.setPayAmount3(temp.getPayAmount3());
				}
				if (temp.getPayAmount4() != null) {
					order.setPayAmount4(temp.getPayAmount4());
				}
				if (temp.getPayAmount5() != null) {
					order.setPayAmount5(temp.getPayAmount5());
				}
				if (temp.getPayAmount6() != null) {
					order.setPayAmount6(temp.getPayAmount6());
				}

				if (temp.getPayAmount7() != null) {
					order.setPayAmount7(temp.getPayAmount7());
				}
				// 如果是未支付更新到部分支付状态
				if ("0".equals(order.getPaymentSta())) {
					order.setPaymentSta("1");
				}
				this.psiTransportOrderDao.save(order);
			}

			for (MultipartFile attchmentFile : attchmentFiles) {
				if (attchmentFile.getSize() != 0) {
					String baseDirStr = ContextLoader
							.getCurrentWebApplicationContext()
							.getServletContext().getRealPath("/")
							+ Global.getCkBaseDir()
							+ "/psi/psiTranSportPayments";
					File baseDir = new File(baseDirStr + "/"
							+ psiTransportPayment.getPaymentNo());
					if (!baseDir.isDirectory())
						baseDir.mkdirs();
					String name = attchmentFile.getOriginalFilename();
					File dest = new File(baseDir, name);
					try {
						FileUtils.copyInputStreamToFile(
								attchmentFile.getInputStream(), dest);
						psiTransportPayment
								.setAttchmentPath("/psi/psiTranSportPayments/"
										+ psiTransportPayment.getPaymentNo()
										+ "/" + name);
					} catch (IOException e) {
						logger.warn(name + "文件保存失败", e);
					}
				}
			}

			psiTransportPayment.setPaymentSta("5"); // 确认状态状态
			psiTransportPayment.setSureDate(new Date());
			psiTransportPayment.setSureUser(UserUtils.getUser());
			psiTransportPaymentDao.save(psiTransportPayment);
		}
	}

	@Transactional(readOnly = false)
	public boolean reviewSave(PsiTransportPayment psiTransportPayment) {
		boolean rs = true;
		String supplierPath = psiTransportPayment.getSupplierCostPath();
		if (psiTransportPayment.getId() != null) {
			psiTransportPayment = this.psiTransportPaymentDao
					.get(psiTransportPayment.getId());
			// 发信给财务

			List<File> files = Lists.newArrayList();
			for (String costPath : supplierPath.split(",")) {
				String filePath = ContextLoader
						.getCurrentWebApplicationContext().getServletContext()
						.getRealPath("/")
						+ Global.getCkBaseDir()
						+ "/psi/psiTransport/"
						+ costPath;
				File file = new File(filePath);
				if (file.exists()) {
					files.add(file);
				}
			}

			psiTransportPayment.setPaymentSta("3");// 已审核
			try {
				rs = replyEmailOrGetTotal(psiTransportPayment, files);
			} catch (Exception e) {
				logger.warn(e.getMessage(), e);
			}
			if (!rs) {
				// 说明发送失败
				psiTransportPayment.setPaymentSta("1");// 已申请
			}
			this.psiTransportPaymentDao.save(psiTransportPayment);
		}
		return rs;
	}

	@Transactional(readOnly = false)
	public void uploadBillSave(PsiTransportPayment psiTransportPayment,
			MultipartFile[] attchmentFiles) {
		if (psiTransportPayment.getId() != null) {
			psiTransportPayment = this.psiTransportPaymentDao
					.get(psiTransportPayment.getId());
		}
		for (MultipartFile attchmentFile : attchmentFiles) {
			if (attchmentFile.getSize() != 0) {
				String baseDirStr = ContextLoader
						.getCurrentWebApplicationContext().getServletContext()
						.getRealPath("/")
						+ Global.getCkBaseDir() + "/psi/psiTranSportPayments";
				File baseDir = new File(baseDirStr + "/"
						+ psiTransportPayment.getPaymentNo());
				if (!baseDir.isDirectory())
					baseDir.mkdirs();
				String name = attchmentFile.getOriginalFilename();
				File dest = new File(baseDir, name);
				try {
					FileUtils.copyInputStreamToFile(
							attchmentFile.getInputStream(), dest);
					psiTransportPayment
							.setSupplierAttchmentPath("/psi/psiTranSportPayments/"
									+ psiTransportPayment.getPaymentNo()
									+ "/"
									+ name);
				} catch (IOException e) {
					logger.warn(name + "文件保存失败", e);
				}
			}
		}
		psiTransportPaymentDao.save(psiTransportPayment);
	}

	/**
	 * 查询配出自己以外的单 未完成订单
	 */
	public List<PsiTransportPayment> findUpPayIngorSelf(Integer payId,
			Integer supplierId) {
		DetachedCriteria dc = psiTransportPaymentDao.createDetachedCriteria();
		dc.add(Restrictions.in("paymentSta", new String[] { "0", "1", "3" }));
		dc.add(Restrictions.eq("supplier.id", supplierId));
		dc.add(Restrictions.ne("id", payId));
		dc.addOrder(Order.desc("id"));
		return psiTransportPaymentDao.find(dc);
	}

	/**
	 * 查询 未完成订单
	 */
	public List<PsiTransportPayment> findUpPay(Integer supplierId) {
		DetachedCriteria dc = psiTransportPaymentDao.createDetachedCriteria();
		dc.add(Restrictions.in("paymentSta", new String[] { "0", "1", "3" }));
		dc.add(Restrictions.eq("supplier.id", supplierId));
		dc.addOrder(Order.desc("id"));
		return psiTransportPaymentDao.find(dc);
	}

	@Transactional(readOnly = false)
	public void save(PsiTransportPayment psiTransportPayment) {
		psiTransportPaymentDao.save(psiTransportPayment);
	}

	private boolean replyEmailOrGetTotal(PsiTransportPayment transportPayment,
			List<File> files) throws Exception {
		transportPayment.setSupplier(psiSupplierService.get(transportPayment
				.getSupplier().getId()));
		transportPayment.setApplyDate(new Date());
		transportPayment.setApplyUser(UserUtils.getUser());
		Map<String, Object> prarms = Maps.newHashMap();
		prarms.put("payment", transportPayment);

		List<User> userList = systemService.findUserByPermission("payment:operate:user");
		List<User> replys = Lists.newArrayList();
		if (userList != null) {
			replys.addAll(userList);
		}

		String toAddress = Collections3.extractToString(replys, "email", ",");
		String content = PdfUtil.getPsiTemplate("applyPaymentByTransportEmail.ftl", prarms);
		if (StringUtils.isNotBlank(content)) {
			Date date = new Date();
			final MailInfo mailInfo = new MailInfo(toAddress, "(春雨)"+transportPayment.getSupplier().getNikename()+ "支付运单物流款申请"+ DateUtils.getDate("-yyyy/M/dd"), date);
			mailInfo.setContent(content);
			String ccToAddress = "maik@inateck.com,alisa@inateck.com,elena@inateck.com";
			mailInfo.setCcToAddress(ccToAddress);
			for (File file : files) {
				mailInfo.setFilePath(file.getAbsolutePath());
				mailInfo.setFileName(file.getName());
			}
			new Thread() {
				@Override
				public void run() {
					mailManager.send(mailInfo);
				};
			}.start();

		}
		return true;
	}
	
	
	public Map<String,Map<String,Float>> getByMonthMoney(PsiTransportPayment psiTransportPayment){
		/*String sql="SELECT DATE_FORMAT(etd_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,SUM(t.`payment_amount`*(CASE WHEN t.`payment_type`='LocalAmount' THEN r.`rate1` WHEN t.`payment_type`='TranAmount' THEN r.`rate2` WHEN t.`payment_type`='DapAmount' THEN r.`rate3` ELSE r.`rate4` END) ) money   "+
	    " FROM psi_transport_payment p join psi_transport_payment_item t on p.id=t.payment_id "+
        " JOIN psi_transport_order r ON r.`transport_no`=t.`transport_no` "+
        " WHERE p.payment_sta!='8' and r.transport_sta!='8' AND t.`payment_type`!='TaxAmount' AND r.to_country IS NOT NULL and DATE_FORMAT(etd_date,'%Y%m')>=:p1 and DATE_FORMAT(etd_date,'%Y%m')<=:p2 "+
        " GROUP BY LOWER(r.to_country),DATE_FORMAT(etd_date,'%Y%m') ORDER BY country,yearAndMonth ";
		*/
		String sql="select yearAndMonth,country,sum(money) from (SELECT DATE_FORMAT(etd_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country, "+
          " SUM(IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0)) money  "+
          " FROM  psi_transport_order r "+
         " WHERE  r.transport_type in ('0','1')  AND r.`model`!='4' and r.from_store in ('21','130') and r.transport_sta!='8' AND etd_date IS NOT NULL AND r.to_country IS NOT NULL  AND DATE_FORMAT(etd_date,'%Y%m')>=:p1 AND DATE_FORMAT(etd_date,'%Y%m')<=:p2  "+
         " GROUP BY LOWER(r.to_country),DATE_FORMAT(etd_date,'%Y%m') "+
         " union all SELECT DATE_FORMAT(export_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country, "+
         " SUM(IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0)) money  "+
         " FROM  lc_psi_transport_order r "+
        " WHERE  r.transport_type in ('0','1')  AND r.`model`!='4' and r.from_store in ('21','130') and r.transport_sta!='8' AND export_date IS NOT NULL AND r.to_country IS NOT NULL  AND DATE_FORMAT(export_date,'%Y%m')>=:p1 AND DATE_FORMAT(export_date,'%Y%m')<=:p2  "+
        " GROUP BY LOWER(r.to_country),DATE_FORMAT(export_date,'%Y%m') ) b  group by yearAndMonth,country ORDER BY yearAndMonth ";
		
		List<Object[]> list=psiTransportPaymentDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getSureDate()),new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getUpdateDate())));
		Map<String,Map<String,Float>> map=Maps.newLinkedHashMap();
		for (Object[] obj : list) {
			Map<String,Float> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(obj[0].toString(),temp);
			}
			temp.put(obj[1].toString(),((BigDecimal)obj[2]).floatValue());
			
			if("de,fr,it,es,uk".contains(obj[1].toString())){
				Map<String,Float> euTemp=map.get(obj[0].toString());
				if(euTemp==null){
					euTemp=Maps.newLinkedHashMap();
					map.put(obj[0].toString(),euTemp);
				}
				Float euMoney=euTemp.get("eu");
				euTemp.put("eu",((BigDecimal)obj[2]).floatValue()+(euMoney==null?0:euMoney));
			}
			
			if("com,ca,us,mx".contains(obj[1].toString())||obj[1].toString().contains("com")){
				Map<String,Float> usTemp=map.get(obj[0].toString());
				if(usTemp==null){
					usTemp=Maps.newLinkedHashMap();
					map.put(obj[0].toString(),usTemp);
				}
				Float usMoney=usTemp.get("US");
				usTemp.put("US",((BigDecimal)obj[2]).floatValue()+(usMoney==null?0:usMoney));
			}
			
		}
		return map;
	}
	
	public Map<String,Map<String,PsiTransportOrder>> getByMonthOtherInfo(PsiTransportPayment psiTransportPayment){
		String sql="select yearAndMonth,country,SUM(weight) ,SUM(cnts) ,SUM(cbm) ,SUM(shpt)  from  (SELECT DATE_FORMAT(etd_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,SUM(weight) weight,SUM(box_number) cnts,SUM(volume) cbm,COUNT(*) shpt FROM psi_transport_order r "+
               " WHERE  r.transport_type in ('0','1')  AND r.`model`!='4' and r.from_store in ('21','130') and r.transport_sta!='8' and etd_date is not null  AND  r.to_country IS NOT NULL and DATE_FORMAT(etd_date,'%Y%m')>=:p1 and DATE_FORMAT(etd_date,'%Y%m')<=:p2 GROUP BY LOWER(r.to_country),DATE_FORMAT(etd_date,'%Y%m')  "+
               "union all SELECT DATE_FORMAT(export_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,SUM(weight) weight,SUM(box_number) cnts,SUM(volume) cbm,COUNT(*) shpt FROM lc_psi_transport_order r "+
               " WHERE  r.transport_type in ('0','1')  AND r.`model`!='4' and r.from_store in ('21','130') and r.transport_sta!='8' and export_date is not null  AND  r.to_country IS NOT NULL and DATE_FORMAT(export_date,'%Y%m')>=:p1 and DATE_FORMAT(export_date,'%Y%m')<=:p2 GROUP BY LOWER(r.to_country),DATE_FORMAT(export_date,'%Y%m') )b group by yearAndMonth,country ORDER BY yearAndMonth ";
		
		List<Object[]> list=psiTransportPaymentDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getSureDate()),new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getUpdateDate())));
		Map<String,Map<String,PsiTransportOrder>> map=Maps.newLinkedHashMap();
		for (Object[] obj : list) {
			Map<String,PsiTransportOrder> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(obj[0].toString(),temp);
			}
			PsiTransportOrder transportOrder=new PsiTransportOrder();
			transportOrder.setWeight(((BigDecimal)obj[2]).floatValue());
			transportOrder.setBoxNumber(Integer.parseInt(obj[3].toString()));
			transportOrder.setVolume(((BigDecimal)obj[4]).floatValue());
			transportOrder.setTeu(Integer.parseInt(obj[5].toString()));
			temp.put(obj[1].toString(), transportOrder);
			
			if("de,fr,it,es,uk".contains(obj[1].toString())){
				Map<String,PsiTransportOrder> euTemp=map.get(obj[0].toString());
				if(euTemp==null){
					euTemp=Maps.newLinkedHashMap();
					map.put(obj[0].toString(),euTemp);
				}
				PsiTransportOrder euTransportOrder=euTemp.get("eu");
				
				PsiTransportOrder transportOrderEu=new PsiTransportOrder();
				transportOrderEu.setWeight(((BigDecimal)obj[2]).floatValue()+((euTransportOrder==null||euTransportOrder.getWeight()==null)?0f:euTransportOrder.getWeight()));
				transportOrderEu.setBoxNumber(Integer.parseInt(obj[3].toString())+((euTransportOrder==null||euTransportOrder.getBoxNumber()==null)?0:euTransportOrder.getBoxNumber().intValue()));
				transportOrderEu.setVolume(((BigDecimal)obj[4]).floatValue()+((euTransportOrder==null||euTransportOrder.getVolume()==null)?0f:euTransportOrder.getVolume()));
				transportOrderEu.setTeu(Integer.parseInt(obj[5].toString())+((euTransportOrder==null||euTransportOrder.getVolume()==null)?0:euTransportOrder.getTeu().intValue()));
				euTemp.put("eu", transportOrderEu);
			}
			
			if("com,ca,us,mx".contains(obj[1].toString())||obj[1].toString().contains("com")){
				Map<String,PsiTransportOrder> usTemp=map.get(obj[0].toString());
				if(usTemp==null){
					usTemp=Maps.newLinkedHashMap();
					map.put(obj[0].toString(),usTemp);
				}
				PsiTransportOrder usTransportOrder=usTemp.get("US");
				PsiTransportOrder transportOrderUs=new PsiTransportOrder();
				transportOrderUs.setWeight(((BigDecimal)obj[2]).floatValue()+((usTransportOrder==null||usTransportOrder.getWeight()==null)?0f:usTransportOrder.getWeight()));
				transportOrderUs.setBoxNumber(Integer.parseInt(obj[3].toString())+((usTransportOrder==null||usTransportOrder.getBoxNumber()==null)?0:usTransportOrder.getBoxNumber().intValue()));
				transportOrderUs.setVolume(((BigDecimal)obj[4]).floatValue()+((usTransportOrder==null||usTransportOrder.getVolume()==null)?0f:usTransportOrder.getVolume()));
				transportOrderUs.setTeu(Integer.parseInt(obj[5].toString())+((usTransportOrder==null||usTransportOrder.getVolume()==null)?0:usTransportOrder.getTeu().intValue()));
				usTemp.put("US", transportOrderUs);
			}
		}
		return map;
	}
	
	public Map<String,Map<String,Map<String,Float>>> getTransportInfo(PsiTransportPayment psiTransportPayment){
		Map<String,Map<String,Map<String,Float>>> map=Maps.newLinkedHashMap();
		String sql="select yearAndMonth,country,model,sum(weight) from (SELECT DATE_FORMAT(etd_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,model,SUM(weight) weight FROM  psi_transport_order r "+
               " WHERE   r.transport_type in ('0','1')  AND r.`model`!='4' and r.from_store in ('21','130') and r.transport_sta!='8' and etd_date is not null AND  r.to_country IS NOT NULL  and DATE_FORMAT(etd_date,'%Y%m')>=:p1 and DATE_FORMAT(etd_date,'%Y%m')<=:p2  "+
               " GROUP BY LOWER(r.to_country),DATE_FORMAT(etd_date,'%Y%m'),model  "+
               " union all SELECT DATE_FORMAT(export_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,model,SUM(weight) weight FROM  lc_psi_transport_order r "+
               " WHERE   r.transport_type in ('0','1')  AND r.`model`!='4' and r.from_store in ('21','130') and r.transport_sta!='8' and export_date is not null AND  r.to_country IS NOT NULL  and DATE_FORMAT(export_date,'%Y%m')>=:p1 and DATE_FORMAT(export_date,'%Y%m')<=:p2  "+
               " GROUP BY LOWER(r.to_country),DATE_FORMAT(export_date,'%Y%m'),model) b group by yearAndMonth,country,model ORDER BY yearAndMonth ";
		List<Object[]> list=psiTransportPaymentDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getSureDate()),new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getUpdateDate())));
		for (Object[] obj: list) {
			Map<String,Map<String,Float>> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(obj[0].toString(),temp);
			}
			Map<String,Float> weightTemp=temp.get(obj[1].toString());
			if(weightTemp==null){
				weightTemp=Maps.newLinkedHashMap();
				temp.put(obj[1].toString(),weightTemp);
			}
			weightTemp.put(obj[2].toString(),((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue());
			if("de,fr,it,es,uk".contains(obj[1].toString())){
				Map<String,Map<String,Float>> tempEU=map.get(obj[0].toString());
				if(tempEU==null){
					tempEU=Maps.newLinkedHashMap();
					map.put(obj[0].toString(),tempEU);
				}
				Map<String,Float> weightTempEU=tempEU.get("eu");
				if(weightTempEU==null){
					weightTempEU=Maps.newLinkedHashMap();
					tempEU.put("eu", weightTempEU);
				}
				weightTempEU.put(obj[2].toString(),((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue()+(weightTempEU.get(obj[2].toString())==null?0:weightTempEU.get(obj[2].toString())) );
			}
			
			if("com,ca,us,mx".contains(obj[1].toString())||obj[1].toString().contains("com")){
				Map<String,Map<String,Float>> tempUS=map.get(obj[0].toString());
				if(tempUS==null){
					tempUS=Maps.newLinkedHashMap();
					map.put(obj[0].toString(),tempUS);
				}
				Map<String,Float> weightTempUS=tempUS.get("US");
				if(weightTempUS==null){
					weightTempUS=Maps.newLinkedHashMap();
					tempUS.put("US", weightTempUS);
				}
				weightTempUS.put(obj[2].toString(),((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue()+(weightTempUS.get(obj[2].toString())==null?0:weightTempUS.get(obj[2].toString())) );
			}
		}
		return map;
	}
	
	
	//单产品分产品、分运输模式、分区域，统计重量     :年、区域、模式、重量
	public Map<String,Map<String,Map<String,Float>>> getTransportInfoByProduct(PsiTransportPayment psiTransportPayment,String productName){
		SimpleDateFormat   sdf =new SimpleDateFormat("yyyyMM");
		Map<String,Map<String,Map<String,Float>>> map=Maps.newLinkedHashMap();
		String sql=" select  b.yearAndMonth,b.country,b.model,sum(b.gw) from (SELECT DATE_FORMAT(etd_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,r.model,SUM(a.`quantity`*b.`gw`/"+
				"(CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  "+
				"		 WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  "+
				"		 WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  "+
				") gw  FROM  psi_transport_order r,psi_transport_order_item a,psi_product AS b" +
				" WHERE r.transport_type in ('0','1') AND r.`model`!='4' and r.from_store in ('21','130') and r.id=a.`transport_order_id` AND a.`product_id`=b.`id` and b.del_flag='0' AND a.del_flag='0' AND r.transport_sta!='8' AND etd_date IS NOT NULL AND  r.to_country IS NOT NULL  AND DATE_FORMAT(etd_date,'%Y%m')>=:p1 AND DATE_FORMAT(etd_date,'%Y%m')<=:p2 "+
				" AND CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END )=:p3 "+
				" GROUP BY LOWER(r.to_country),DATE_FORMAT(etd_date,'%Y%m'),model  "+
				" union all SELECT DATE_FORMAT(export_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,r.model,SUM(a.`quantity`*b.`gw`/"+
				"(CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  "+
				"		 WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  "+
				"		 WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  "+
				") gw  FROM  lc_psi_transport_order r,lc_psi_transport_order_item a,psi_product AS b" +
				" WHERE r.transport_type in ('0','1')  AND r.`model`!='4' and r.from_store in ('21','130')  and r.id=a.`transport_order_id` AND a.`product_id`=b.`id` and b.del_flag='0' AND a.del_flag='0' AND r.transport_sta!='8' AND export_date IS NOT NULL AND  r.to_country IS NOT NULL  AND DATE_FORMAT(export_date,'%Y%m')>=:p1 AND DATE_FORMAT(export_date,'%Y%m')<=:p2 "+
				" AND CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END )=:p3 "+
				" GROUP BY LOWER(r.to_country),DATE_FORMAT(export_date,'%Y%m'),model ) b  group by b.yearAndMonth,b.country,b.model ORDER BY b.yearAndMonth  ";
		Parameter  parm = new Parameter(sdf.format(psiTransportPayment.getSureDate()),sdf.format(psiTransportPayment.getUpdateDate()),productName);
		List<Object[]> list=psiTransportPaymentDao.findBySql(sql,parm);
		for (Object[] obj: list) {
			Map<String,Map<String,Float>> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(obj[0].toString(),temp);
			}
			Map<String,Float> weightTemp=temp.get(obj[1].toString());
			if(weightTemp==null){
				weightTemp=Maps.newLinkedHashMap();
				temp.put(obj[1].toString(),weightTemp);
			}
			weightTemp.put(obj[2].toString(),((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue());
			if("de,fr,it,es,uk".contains(obj[1].toString())){
				Map<String,Map<String,Float>> tempEU=map.get(obj[0].toString());
				if(tempEU==null){
					tempEU=Maps.newLinkedHashMap();
					map.put(obj[0].toString(),tempEU);
				}
				Map<String,Float> weightTempEU=tempEU.get("eu");
				if(weightTempEU==null){
					weightTempEU=Maps.newLinkedHashMap();
					tempEU.put("eu", weightTempEU);
				}
				weightTempEU.put(obj[2].toString(),((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue()+(weightTempEU.get(obj[2].toString())==null?0:weightTempEU.get(obj[2].toString())) );
			}
			
			if("com,ca,us,mx".contains(obj[1].toString())||obj[1].toString().contains("com")){
				Map<String,Map<String,Float>> tempUS=map.get(obj[0].toString());
				if(tempUS==null){
					tempUS=Maps.newLinkedHashMap();
					map.put(obj[0].toString(),tempUS);
				}
				Map<String,Float> weightTempUS=tempUS.get("US");
				if(weightTempUS==null){
					weightTempUS=Maps.newLinkedHashMap();
					tempUS.put("US", weightTempUS);
				}
				weightTempUS.put(obj[2].toString(),((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue()+(weightTempUS.get(obj[2].toString())==null?0:weightTempUS.get(obj[2].toString())) );
			}
		}
		return map;
	}
	
	
	
	public Map<String,Map<String,Map<String,Float>>> getTransportInfo2(PsiTransportPayment psiTransportPayment){
		Map<String,Map<String,Map<String,Float>>> totalMap=getTransportInfo(psiTransportPayment);
		Map<String,Map<String,Map<String,Float>>> map=Maps.newLinkedHashMap();
		/*String sql="SELECT DATE_FORMAT(r.etd_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,r.model,SUM(t.quantity*p.gw/p.pack_quantity) FROM  psi_transport_order r "+
		       " join psi_transport_order_item t on r.id=t.transport_order_id AND  t.del_flag='0' "+
			   " join psi_product p on concat(p.brand,' ',p.model)=t.product_name and p.del_flag='0' "+
		       "  AND (p.is_new='1' OR  TO_DAYS(r.create_date) - TO_DAYS(p.added_month)<=180) "+
		      // " join psi_product_eliminate p on p.country=lower(r.to_country) "+
               " WHERE  r.transport_sta!='8' and r.etd_date is not null AND  r.to_country IS NOT NULL  and DATE_FORMAT(r.etd_date,'%Y%m')>=:p1 and DATE_FORMAT(r.etd_date,'%Y%m')<=:p2  "+
               " GROUP BY LOWER(r.to_country),DATE_FORMAT(r.etd_date,'%Y%m'),r.model ";*/
		String sql="select yearAndMonth,country,model,sum(gw) from (SELECT DATE_FORMAT(r.etd_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,r.model,SUM(t.quantity*d.gw/d.pack_quantity) gw FROM  psi_transport_order r "+
				" JOIN psi_transport_order_item t ON r.id=t.transport_order_id and t.del_flag='0'   "+
				" JOIN psi_product_eliminate p ON p.country=(CASE WHEN LOWER(r.to_country)='us' THEN 'com' ELSE LOWER(r.to_country) END ) "+
				" AND CONCAT(t.`product_name`,IF(t.`color_code`='','',CONCAT('_',t.`color_code`)))=CONCAT(p.`product_name`,IF(p.`color`='','',CONCAT('_',p.`color`))) "+
				" AND p.`is_new`='1' "+
				" JOIN psi_product  d ON d.id=p.product_id AND d.`del_flag`='0' "+ 
				" WHERE  r.transport_type in ('0','1')  AND r.`model`!='4' and r.from_store in ('21','130') and  r.transport_sta!='8' AND r.etd_date IS NOT NULL AND  r.to_country IS NOT NULL  "+
				" and DATE_FORMAT(r.etd_date,'%Y%m')>=:p1 and DATE_FORMAT(r.etd_date,'%Y%m')<=:p2  "+
				" GROUP BY LOWER(r.to_country),DATE_FORMAT(r.etd_date,'%Y%m'),r.model  "+
				" union all SELECT DATE_FORMAT(r.export_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,r.model,SUM(t.quantity*d.gw/d.pack_quantity) gw FROM  lc_psi_transport_order r "+
				" JOIN lc_psi_transport_order_item t ON r.id=t.transport_order_id and t.del_flag='0'   "+
				" JOIN psi_product_eliminate p ON p.country=(CASE WHEN LOWER(r.to_country)='us' THEN 'com' ELSE LOWER(r.to_country) END ) "+
				" AND CONCAT(t.`product_name`,IF(t.`color_code`='','',CONCAT('_',t.`color_code`)))=CONCAT(p.`product_name`,IF(p.`color`='','',CONCAT('_',p.`color`))) "+
				" AND p.`is_new`='1' "+
				" JOIN psi_product  d ON d.id=p.product_id AND d.`del_flag`='0' "+ 
				" WHERE  r.transport_type in ('0','1')  AND r.`model`!='4' and r.from_store in ('21','130') and  r.transport_sta!='8' AND r.export_date IS NOT NULL AND  r.to_country IS NOT NULL  "+
				" and DATE_FORMAT(r.export_date,'%Y%m')>=:p1 and DATE_FORMAT(r.export_date,'%Y%m')<=:p2  "+
				" GROUP BY LOWER(r.to_country),DATE_FORMAT(r.export_date,'%Y%m'),r.model) b group by yearAndMonth,country,model ORDER BY yearAndMonth  ";
				 
		List<Object[]> list=psiTransportPaymentDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getSureDate()),new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getUpdateDate())));
		for (Object[] obj: list) {
			Map<String,Map<String,Float>> temp=map.get(obj[0].toString());
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(obj[0].toString(),temp);
			}
			Map<String,Float> weightTemp=temp.get(obj[1].toString());
			if(weightTemp==null){
				weightTemp=Maps.newLinkedHashMap();
				temp.put(obj[1].toString(),weightTemp);
			}
			weightTemp.put(obj[2].toString(),((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue());
			if("de,fr,it,es,uk".contains(obj[1].toString())){
				Map<String,Map<String,Float>> tempEU=map.get(obj[0].toString());
				if(tempEU==null){
					tempEU=Maps.newLinkedHashMap();
					map.put(obj[0].toString(),tempEU);
				}
				Map<String,Float> weightTempEU=tempEU.get("eu");
				if(weightTempEU==null){
					weightTempEU=Maps.newLinkedHashMap();
					tempEU.put("eu", weightTempEU);
				}
				weightTempEU.put(obj[2].toString(),((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue()+(weightTempEU.get(obj[2].toString())==null?0:weightTempEU.get(obj[2].toString())) );
			}
			
			if("com,ca,us,mx".contains(obj[1].toString())||obj[1].toString().contains("com")){
				Map<String,Map<String,Float>> tempUS=map.get(obj[0].toString());
				if(tempUS==null){
					tempUS=Maps.newLinkedHashMap();
					map.put(obj[0].toString(),tempUS);
				}
				Map<String,Float> weightTempUS=tempUS.get("US");
				if(weightTempUS==null){
					weightTempUS=Maps.newLinkedHashMap();
					tempUS.put("US", weightTempUS);
				}
				weightTempUS.put(obj[2].toString(),((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue()+(weightTempUS.get(obj[2].toString())==null?0:weightTempUS.get(obj[2].toString())) );
			}
		}
		
		for (Map.Entry<String, Map<String, Map<String, Float>>> entry2: totalMap.entrySet()) {
			String key = entry2.getKey();
			Map<String,Map<String,Float>> temp=entry2.getValue();
			for (Map.Entry<String,Map<String,Float>> entry: temp.entrySet()) {
				String key1 = entry.getKey();
				Map<String,Float> temp1=entry.getValue();
				for (Map.Entry<String,Float> entry1: temp1.entrySet()) {
					String key2 = entry1.getKey();
					Float weight=entry1.getValue();
					if(map.size()>0&&map.get(key)!=null&&map.get(key).get(key1)!=null&&map.get(key).get(key1).get(key2)!=null){
						weight=weight-map.get(key).get(key1).get(key2);
						if(weight<0){
							temp1.put(key2, 0f);
						}else{
							temp1.put(key2, weight);
						}
					}
				}
			}
		}
		return totalMap;
	}
	
	public Map<String,Map<String,Map<String,Float>>> getTransportInfoMoney(PsiTransportPayment psiTransportPayment){
		Map<String,Map<String,Map<String,Float>>> map=Maps.newLinkedHashMap();
		String sql="select yearAndMonth,country,model,sum(money) from (SELECT DATE_FORMAT(etd_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,model, "+
				" SUM(IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0)) money   "+
				" FROM psi_transport_order r  "+ 
				" WHERE  r.transport_type in ('0','1') AND r.`model`!='4' and r.from_store in ('21','130') and  r.transport_sta!='8' AND etd_date IS NOT NULL AND  r.to_country IS NOT NULL  AND DATE_FORMAT(etd_date,'%Y%m')>=:p1 AND DATE_FORMAT(etd_date,'%Y%m')<=:p2   "+
				" GROUP BY LOWER(r.to_country),DATE_FORMAT(etd_date,'%Y%m'),model "+
				" union all SELECT DATE_FORMAT(export_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,model, "+
				" SUM(IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0)) money   "+
				" FROM lc_psi_transport_order r  "+ 
				" WHERE  r.transport_type in ('0','1') AND r.`model`!='4' and r.from_store in ('21','130') and  r.transport_sta!='8' AND export_date IS NOT NULL AND  r.to_country IS NOT NULL  AND DATE_FORMAT(export_date,'%Y%m')>=:p1 AND DATE_FORMAT(export_date,'%Y%m')<=:p2   "+
				" GROUP BY LOWER(r.to_country),DATE_FORMAT(export_date,'%Y%m'),model) b group by yearAndMonth,country,model ORDER BY yearAndMonth ";
				         
		List<Object[]> list=psiTransportPaymentDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getSureDate()),new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getUpdateDate())));
		for (Object[] obj: list) {
			String yearMonth =obj[0].toString();
			Map<String,Map<String,Float>> temp=map.get(yearMonth);
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(yearMonth,temp);
			}
			Map<String,Float> weightTemp=temp.get(obj[1].toString());
			if(weightTemp==null){
				weightTemp=Maps.newLinkedHashMap();
				temp.put(obj[1].toString(),weightTemp);
			}
			Float amount= ((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue();
			weightTemp.put(obj[2].toString(),amount);
			if("de,fr,it,es,uk".contains(obj[1].toString())){
				Map<String,Map<String,Float>> tempEU=map.get(obj[0].toString());
				if(tempEU==null){
					tempEU=Maps.newLinkedHashMap();
					map.put(obj[0].toString(),tempEU);
				}
				Map<String,Float> weightTempEU=tempEU.get("eu");
				if(weightTempEU==null){
					weightTempEU=Maps.newLinkedHashMap();
					tempEU.put("eu", weightTempEU);
				}
				weightTempEU.put(obj[2].toString(),amount+(weightTempEU.get(obj[2].toString())==null?0:weightTempEU.get(obj[2].toString())));
			}
			
			if("com,ca,us,mx".contains(obj[1].toString())||obj[1].toString().contains("com")){
				Map<String,Map<String,Float>> tempUS=map.get(obj[0].toString());
				if(tempUS==null){
					tempUS=Maps.newLinkedHashMap();
					map.put(obj[0].toString(),tempUS);
				}
				Map<String,Float> weightTempUS=tempUS.get("US");
				if(weightTempUS==null){
					weightTempUS=Maps.newLinkedHashMap();
					tempUS.put("US", weightTempUS);
				}
				weightTempUS.put(obj[2].toString(),amount+(weightTempUS.get(obj[2].toString())==null?0:weightTempUS.get(obj[2].toString())));
			}
		}
		return map;
	}
	
	
	
	public Map<String,Map<String,Map<String,Float>>> getTransportInfoMoneyProduct(PsiTransportPayment psiTransportPayment,String productName){
		Map<String,Map<String,Map<String,Float>>> map=Maps.newLinkedHashMap();
		String sql="select  yearAndMonth,country,model,sum(money) from (SELECT DATE_FORMAT(etd_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,r.model, " +
				" SUM((IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0))*(a.`quantity`*b.`gw`/("+
				" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  " +
				" 		 WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  " +
				" 		 WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  " +
				"*r.`weight`))) money " +
				" FROM psi_transport_order r ,psi_transport_order_item AS a,psi_product AS b WHERE  r.transport_type in ('0','1')  AND r.`model`!='4' and r.from_store in ('21','130') and r.id=a.`transport_order_id` AND a.del_flag='0' AND b.id=a.`product_id`  and b.del_flag='0' AND  r.transport_sta!='8' "+
				" AND CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END )=:p3 "+
				" AND etd_date IS NOT NULL AND  r.to_country IS NOT NULL  AND DATE_FORMAT(etd_date,'%Y%m')>=:p1 AND DATE_FORMAT(etd_date,'%Y%m')<=:p2 GROUP BY LOWER(r.to_country),DATE_FORMAT(etd_date,'%Y%m'),model  "+
				" union all SELECT DATE_FORMAT(export_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,r.model, " +
				" SUM((IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0))*(a.`quantity`*b.`gw`/("+
				" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  " +
				" 		 WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  " +
				" 		 WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  " +
				"*r.`weight`))) money " +
				" FROM lc_psi_transport_order r ,lc_psi_transport_order_item AS a,psi_product AS b WHERE  r.transport_type in ('0','1')  AND r.`model`!='4' and r.from_store in ('21','130') and r.id=a.`transport_order_id` AND a.del_flag='0' AND b.id=a.`product_id`  and b.del_flag='0' AND  r.transport_sta!='8' "+
				" AND CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END )=:p3 "+
				" AND export_date IS NOT NULL AND  r.to_country IS NOT NULL  AND DATE_FORMAT(export_date,'%Y%m')>=:p1 AND DATE_FORMAT(export_date,'%Y%m')<=:p2 GROUP BY LOWER(r.to_country),DATE_FORMAT(export_date,'%Y%m'),model  ) b group by yearAndMonth,country,model ORDER BY yearAndMonth ";
		List<Object[]> list=psiTransportPaymentDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getSureDate()),new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getUpdateDate()),productName));
		for (Object[] obj: list) {
			String yearMonth =obj[0].toString();
			Map<String,Map<String,Float>> temp=map.get(yearMonth);
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(yearMonth,temp);
			}
			Map<String,Float> weightTemp=temp.get(obj[1].toString());
			if(weightTemp==null){
				weightTemp=Maps.newLinkedHashMap();
				temp.put(obj[1].toString(),weightTemp);
			}
			Float amount= ((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue();
			weightTemp.put(obj[2].toString(),amount);
			if("de,fr,it,es,uk".contains(obj[1].toString())){
				Map<String,Map<String,Float>> tempEU=map.get(obj[0].toString());
				if(tempEU==null){
					tempEU=Maps.newLinkedHashMap();
					map.put(obj[0].toString(),tempEU);
				}
				Map<String,Float> weightTempEU=tempEU.get("eu");
				if(weightTempEU==null){
					weightTempEU=Maps.newLinkedHashMap();
					tempEU.put("eu", weightTempEU);
				}
				weightTempEU.put(obj[2].toString(),amount+(weightTempEU.get(obj[2].toString())==null?0:weightTempEU.get(obj[2].toString())));
			}
			
			if("com,ca,us,mx".contains(obj[1].toString())||obj[1].toString().contains("com")){
				Map<String,Map<String,Float>> tempUS=map.get(obj[0].toString());
				if(tempUS==null){
					tempUS=Maps.newLinkedHashMap();
					map.put(obj[0].toString(),tempUS);
				}
				Map<String,Float> weightTempUS=tempUS.get("US");
				if(weightTempUS==null){
					weightTempUS=Maps.newLinkedHashMap();
					tempUS.put("US", weightTempUS);
				}
				weightTempUS.put(obj[2].toString(),amount+(weightTempUS.get(obj[2].toString())==null?0:weightTempUS.get(obj[2].toString())));
			}
		}
		return map;
	}
	
	public Map<String,Map<String,Map<String,Integer>>> getTransportQuantity(PsiTransportPayment psiTransportPayment,String productName){
		Map<String,Map<String,Map<String,Integer>>> map=Maps.newLinkedHashMap();
		String sql="select yearAndMonth,country,model,sum(quantity) from (SELECT DATE_FORMAT(etd_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,r.model, " +
				" SUM(a.quantity) quantity " +
				" FROM psi_transport_order r ,psi_transport_order_item AS a,psi_product AS b WHERE  r.transport_type in ('0','1')  AND r.`model`!='4' and r.from_store in ('21','130') and r.id=a.`transport_order_id` AND a.del_flag='0' AND b.id=a.`product_id`  and b.del_flag='0' AND  r.transport_sta!='8' "+
				//" AND a.`product_id`=:p3 " +
				" AND CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END )=:p3 "+
				" AND etd_date IS NOT NULL AND  r.to_country IS NOT NULL  AND DATE_FORMAT(etd_date,'%Y%m')>=:p1 AND DATE_FORMAT(etd_date,'%Y%m')<=:p2 GROUP BY LOWER(r.to_country),DATE_FORMAT(etd_date,'%Y%m'),model  "+
				" union all SELECT DATE_FORMAT(export_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,r.model, " +
				" SUM(a.quantity) quantity " +
				" FROM lc_psi_transport_order r ,lc_psi_transport_order_item AS a,psi_product AS b WHERE  r.transport_type in ('0','1')  AND r.`model`!='4' and r.from_store in ('21','130') and r.id=a.`transport_order_id` AND a.del_flag='0' AND b.id=a.`product_id`  and b.del_flag='0' AND  r.transport_sta!='8' "+
				//" AND a.`product_id`=:p3 " +
				" AND CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END )=:p3 "+
				" AND export_date IS NOT NULL AND  r.to_country IS NOT NULL  AND DATE_FORMAT(export_date,'%Y%m')>=:p1 AND DATE_FORMAT(export_date,'%Y%m')<=:p2 GROUP BY LOWER(r.to_country),DATE_FORMAT(export_date,'%Y%m'),model) b group by  yearAndMonth,country,model  ORDER BY yearAndMonth ";
		List<Object[]> list=psiTransportPaymentDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getSureDate()),new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getUpdateDate()),productName));
		for (Object[] obj: list) {
			String yearMonth =obj[0].toString();
			Map<String,Map<String,Integer>> temp=map.get(yearMonth);
			if(temp==null){
				temp=Maps.newLinkedHashMap();
				map.put(yearMonth,temp);
			}
			Map<String,Integer> weightTemp=temp.get(obj[1].toString());
			if(weightTemp==null){
				weightTemp=Maps.newLinkedHashMap();
				temp.put(obj[1].toString(),weightTemp);
			}
			Integer amount= Integer.parseInt(obj[3].toString());
			weightTemp.put(obj[2].toString(),amount);
			if("de,fr,it,es,uk".contains(obj[1].toString())){
				Map<String,Map<String,Integer>> tempEU=map.get(obj[0].toString());
				if(tempEU==null){
					tempEU=Maps.newLinkedHashMap();
					map.put(obj[0].toString(),tempEU);
				}
				Map<String,Integer> weightTempEU=tempEU.get("eu");
				if(weightTempEU==null){
					weightTempEU=Maps.newLinkedHashMap();
					tempEU.put("eu", weightTempEU);
				}
				weightTempEU.put(obj[2].toString(),amount+(weightTempEU.get(obj[2].toString())==null?0:weightTempEU.get(obj[2].toString())));
			}
			
			if("com,ca,us,mx".contains(obj[1].toString())||obj[1].toString().contains("com")){
				Map<String,Map<String,Integer>> tempUS=map.get(obj[0].toString());
				if(tempUS==null){
					tempUS=Maps.newLinkedHashMap();
					map.put(obj[0].toString(),tempUS);
				}
				Map<String,Integer> weightTempUS=tempUS.get("US");
				if(weightTempUS==null){
					weightTempUS=Maps.newLinkedHashMap();
					tempUS.put("US", weightTempUS);
				}
				weightTempUS.put(obj[2].toString(),amount+(weightTempUS.get(obj[2].toString())==null?0:weightTempUS.get(obj[2].toString())));
			}
		}
		return map;
	}
	
	
	//月份 国家 产品名称 运输方式
	public Map<String,Map<String,Map<String,Map<String,PsiTransportDto>>>> getAllInfoByModel(PsiTransportPayment psiTransportPayment){
		Map<String,Map<String,Map<String,Map<String,PsiTransportDto>>>>  map=Maps.newLinkedHashMap();
		String sql="SELECT DATE_FORMAT(etd_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,r.model,CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END ), " +
				" SUM((IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0))*(a.`quantity`*b.`gw`/( "+
				" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  " +
				" WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  " +
				" WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  " +
	            " *r.`weight`))) money, " +
				" SUM(a.`quantity`*b.`gw`/ "+
				" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  " +
				" WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  " +
				" WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  " +
	            " ),sum(a.quantity) "+
				" FROM psi_transport_order r ,psi_transport_order_item AS a,psi_product AS b WHERE r.id=a.`transport_order_id` AND a.del_flag='0' AND b.id=a.`product_id`  and b.del_flag='0' AND  r.transport_sta!='8' "+
				"  and r.transport_type in ('0','1') AND etd_date IS NOT NULL AND  r.to_country IS NOT NULL  AND DATE_FORMAT(etd_date,'%Y%m')>=:p1 AND DATE_FORMAT(etd_date,'%Y%m')<=:p2 GROUP BY LOWER(r.to_country),DATE_FORMAT(etd_date,'%Y%m'),model,CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END )  ORDER BY yearAndMonth ";

		List<Object[]> list=psiTransportPaymentDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getSureDate()),new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getUpdateDate())));
		for (Object[] obj: list) {
			String yearMonth =obj[0].toString();
			Float money= ((BigDecimal)(obj[4]==null?new BigDecimal(0):obj[4])).floatValue();
			Float weight= ((BigDecimal)(obj[5]==null?new BigDecimal(0):obj[5])).floatValue();
			Integer quantity=Integer.parseInt(obj[6].toString());
			
			Map<String,Map<String,Map<String,PsiTransportDto>>> tempTotal=map.get(yearMonth);
			if(tempTotal==null){
				tempTotal=Maps.newLinkedHashMap();
				map.put(yearMonth,tempTotal);
			}
			Map<String,Map<String,PsiTransportDto>> weightTempTotal=tempTotal.get("total");
			if(weightTempTotal==null){
				weightTempTotal=Maps.newLinkedHashMap();
				tempTotal.put("total",weightTempTotal);
			}
			Map<String,PsiTransportDto> productTempToal=weightTempTotal.get(obj[3].toString());
			if(productTempToal==null){
				productTempToal=Maps.newLinkedHashMap();
				weightTempTotal.put(obj[3].toString(),productTempToal);
			}
			Float tempWeightTotal1=weight+((productTempToal.get(obj[2].toString())==null||productTempToal.get(obj[2].toString()).getWeight()==null)?0:productTempToal.get(obj[2].toString()).getWeight());
			Float tempMoneyTotal1=money+((productTempToal.get(obj[2].toString())==null||productTempToal.get(obj[2].toString()).getMoney()==null)?0:productTempToal.get(obj[2].toString()).getMoney());
			Integer tempQuantityTotal1=quantity+((productTempToal.get(obj[2].toString())==null||productTempToal.get(obj[2].toString()).getQuantity()==null)?0:productTempToal.get(obj[2].toString()).getQuantity());
			productTempToal.put(obj[2].toString(),new PsiTransportDto(tempWeightTotal1,tempMoneyTotal1,tempQuantityTotal1));
			
			Float tempWeightTotal=weight+((productTempToal.get("total")==null||productTempToal.get("total").getWeight()==null)?0:productTempToal.get("total").getWeight());
			Float tempMoneyTotal=money+((productTempToal.get("total")==null||productTempToal.get("total").getMoney()==null)?0:productTempToal.get("total").getMoney());
			Integer tempQuantityTotal=quantity+((productTempToal.get("total")==null||productTempToal.get("total").getQuantity()==null)?0:productTempToal.get("total").getQuantity());
			productTempToal.put("total",new PsiTransportDto(tempWeightTotal,tempMoneyTotal,tempQuantityTotal));
			
			
			if("jp".equals(obj[1].toString())){
				Map<String,Map<String,Map<String,PsiTransportDto>>> temp=map.get(yearMonth);
				if(temp==null){
					temp=Maps.newLinkedHashMap();
					map.put(yearMonth,temp);
				}
				Map<String,Map<String,PsiTransportDto>> weightTemp=temp.get("JP");
				if(weightTemp==null){
					weightTemp=Maps.newLinkedHashMap();
					temp.put("JP",weightTemp);
				}
				Map<String,PsiTransportDto> productTemp=weightTemp.get(obj[3].toString());
				if(productTemp==null){
					productTemp=Maps.newLinkedHashMap();
					weightTemp.put(obj[3].toString(),productTemp);
				}
				productTemp.put(obj[2].toString(),new PsiTransportDto(weight,money,quantity));
				Float tempWeight=weight+((productTemp.get("total")==null||productTemp.get("total").getWeight()==null)?0:productTemp.get("total").getWeight());
				Float tempMoney=money+((productTemp.get("total")==null||productTemp.get("total").getMoney()==null)?0:productTemp.get("total").getMoney());
				Integer tempQuantity=quantity+((productTemp.get("total")==null||productTemp.get("total").getQuantity()==null)?0:productTemp.get("total").getQuantity());
				
				productTemp.put("total",new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
			}else if("de,fr,it,es,uk".contains(obj[1].toString())){
				Map<String,Map<String,Map<String,PsiTransportDto>>> tempEU=map.get(yearMonth);
				if(tempEU==null){
					tempEU=Maps.newLinkedHashMap();
					map.put(obj[0].toString(),tempEU);
				} 
				Map<String,Map<String,PsiTransportDto>> tempCountryEu=tempEU.get("EU");
				if(tempCountryEu==null){
					tempCountryEu=Maps.newLinkedHashMap();
					tempEU.put("EU",tempCountryEu);
				}
				Map<String,PsiTransportDto> weightTempEU=tempCountryEu.get(obj[3].toString());
				if(weightTempEU==null){
					weightTempEU=Maps.newLinkedHashMap();
					tempCountryEu.put(obj[3].toString(), weightTempEU);
				} 
				Float tempWeight=weight+((weightTempEU.get(obj[2].toString())==null||weightTempEU.get(obj[2].toString()).getWeight()==null)?0:weightTempEU.get(obj[2].toString()).getWeight());
				Float tempMoney=money+((weightTempEU.get(obj[2].toString())==null||weightTempEU.get(obj[2].toString()).getMoney()==null)?0:weightTempEU.get(obj[2].toString()).getMoney());
				Integer tempQuantity=quantity+((weightTempEU.get(obj[2].toString())==null||weightTempEU.get(obj[2].toString()).getQuantity()==null)?0:weightTempEU.get(obj[2].toString()).getQuantity());
				weightTempEU.put(obj[2].toString(),new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
				
				Float tempWeight1=weight+((weightTempEU.get("total")==null||weightTempEU.get("total").getWeight()==null)?0:weightTempEU.get("total").getWeight());
				Float tempMoney1=money+((weightTempEU.get("total")==null||weightTempEU.get("total").getMoney()==null)?0:weightTempEU.get("total").getMoney());
				Integer tempQuantity1=quantity+((weightTempEU.get("total")==null||weightTempEU.get("total").getQuantity()==null)?0:weightTempEU.get("total").getQuantity());
				
				weightTempEU.put("total",new PsiTransportDto(tempWeight1,tempMoney1,tempQuantity1));
			}else if("com,ca,us,mx".contains(obj[1].toString())){
				Map<String,Map<String,Map<String,PsiTransportDto>>> tempEU=map.get(yearMonth);
				if(tempEU==null){
					tempEU=Maps.newLinkedHashMap();
					map.put(obj[0].toString(),tempEU);
				}
				Map<String,Map<String,PsiTransportDto>> tempCountryEu=tempEU.get("US");
				if(tempCountryEu==null){
					tempCountryEu=Maps.newLinkedHashMap();
					tempEU.put("US",tempCountryEu);
				}
				Map<String,PsiTransportDto> weightTempEU=tempCountryEu.get(obj[3].toString());
				if(weightTempEU==null){
					weightTempEU=Maps.newLinkedHashMap();
					tempCountryEu.put(obj[3].toString(), weightTempEU);
				}
				Float tempWeight=weight+((weightTempEU.get(obj[2].toString())==null||weightTempEU.get(obj[2].toString()).getWeight()==null)?0:weightTempEU.get(obj[2].toString()).getWeight());
				Float tempMoney=money+((weightTempEU.get(obj[2].toString())==null||weightTempEU.get(obj[2].toString()).getMoney()==null)?0:weightTempEU.get(obj[2].toString()).getMoney());
				Integer tempQuantity=quantity+((weightTempEU.get(obj[2].toString())==null||weightTempEU.get(obj[2].toString()).getQuantity()==null)?0:weightTempEU.get(obj[2].toString()).getQuantity());
				
				weightTempEU.put(obj[2].toString(),new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
				
				Float tempWeight1=weight+((weightTempEU.get("total")==null||weightTempEU.get("total").getWeight()==null)?0:weightTempEU.get("total").getWeight());
				Float tempMoney1=money+((weightTempEU.get("total")==null||weightTempEU.get("total").getMoney()==null)?0:weightTempEU.get("total").getMoney());
				Integer tempQuantity1=quantity+((weightTempEU.get("total")==null||weightTempEU.get("total").getQuantity()==null)?0:weightTempEU.get("total").getQuantity());
				
				weightTempEU.put("total",new PsiTransportDto(tempWeight1,tempMoney1,tempQuantity1));
			}
		}
		return map;
	}
	
	   //月份 国家 产品名称 运输方式
		public Map<String,Map<String,Map<String,Map<String,PsiTransportDto>>>> getTransportInfoByModel(PsiTransportPayment psiTransportPayment){
			Map<String,Map<String,Map<String,Map<String,PsiTransportDto>>>>  map=Maps.newLinkedHashMap();
			String sql="SELECT LOWER(r.to_country) country1,LOWER(r.to_country) country,r.model,CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END ), " +
					" SUM((IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0))*(a.`quantity`*b.`gw`/("+
					" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  " +
					" WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  " +
					" WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  " +
					" *r.`weight`))) money, " +
					" SUM(a.`quantity`*b.`gw`/"+
					" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  " +
					" WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  " +
					" WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  " +
					" ),sum(a.quantity) "+
					" FROM psi_transport_order r ,psi_transport_order_item AS a,psi_product AS b WHERE r.id=a.`transport_order_id` AND a.del_flag='0' AND b.id=a.`product_id`  and b.del_flag='0' AND  r.transport_sta!='8' "+
					"  and r.transport_type in ('0','1') AND etd_date IS NOT NULL AND  r.to_country IS NOT NULL  AND DATE_FORMAT(etd_date,'%Y%m')>=:p1 AND DATE_FORMAT(etd_date,'%Y%m')<=:p2 GROUP BY LOWER(r.to_country),model,CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END ) ";

			List<Object[]> list=psiTransportPaymentDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getSureDate()),new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getUpdateDate())));
			for (Object[] obj: list) {
				String yearMonth =new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getSureDate())+"-"+new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getUpdateDate());
				Float money= ((BigDecimal)(obj[4]==null?new BigDecimal(0):obj[4])).floatValue();
				Float weight= ((BigDecimal)(obj[5]==null?new BigDecimal(0):obj[5])).floatValue();
				Integer quantity=Integer.parseInt(obj[6].toString());
				
				Map<String,Map<String,Map<String,PsiTransportDto>>> tempTotal=map.get(yearMonth);
				if(tempTotal==null){
					tempTotal=Maps.newLinkedHashMap();
					map.put(yearMonth,tempTotal);
				}
				Map<String,Map<String,PsiTransportDto>> weightTempTotal=tempTotal.get("total");
				if(weightTempTotal==null){
					weightTempTotal=Maps.newLinkedHashMap();
					tempTotal.put("total",weightTempTotal);
				}
				Map<String,PsiTransportDto> productTempToal=weightTempTotal.get(obj[3].toString());
				if(productTempToal==null){
					productTempToal=Maps.newLinkedHashMap();
					weightTempTotal.put(obj[3].toString(),productTempToal);
				}
				Float tempWeightTotal1=weight+((productTempToal.get(obj[2].toString())==null||productTempToal.get(obj[2].toString()).getWeight()==null)?0:productTempToal.get(obj[2].toString()).getWeight());
				Float tempMoneyTotal1=money+((productTempToal.get(obj[2].toString())==null||productTempToal.get(obj[2].toString()).getMoney()==null)?0:productTempToal.get(obj[2].toString()).getMoney());
				Integer tempQuantityTotal1=quantity+((productTempToal.get(obj[2].toString())==null||productTempToal.get(obj[2].toString()).getQuantity()==null)?0:productTempToal.get(obj[2].toString()).getQuantity());
				productTempToal.put(obj[2].toString(),new PsiTransportDto(tempWeightTotal1,tempMoneyTotal1,tempQuantityTotal1));
				
				Float tempWeightTotal=weight+((productTempToal.get("total")==null||productTempToal.get("total").getWeight()==null)?0:productTempToal.get("total").getWeight());
				Float tempMoneyTotal=money+((productTempToal.get("total")==null||productTempToal.get("total").getMoney()==null)?0:productTempToal.get("total").getMoney());
				Integer tempQuantityTotal=quantity+((productTempToal.get("total")==null||productTempToal.get("total").getQuantity()==null)?0:productTempToal.get("total").getQuantity());
				productTempToal.put("total",new PsiTransportDto(tempWeightTotal,tempMoneyTotal,tempQuantityTotal));
				
				
				if("jp".equals(obj[1].toString())){
					Map<String,Map<String,Map<String,PsiTransportDto>>> temp=map.get(yearMonth);
					if(temp==null){
						temp=Maps.newLinkedHashMap();
						map.put(yearMonth,temp);
					}
					Map<String,Map<String,PsiTransportDto>> weightTemp=temp.get("JP");
					if(weightTemp==null){
						weightTemp=Maps.newLinkedHashMap();
						temp.put("JP",weightTemp);
					}
					Map<String,PsiTransportDto> productTemp=weightTemp.get(obj[3].toString());
					if(productTemp==null){
						productTemp=Maps.newLinkedHashMap();
						weightTemp.put(obj[3].toString(),productTemp);
					}
					productTemp.put(obj[2].toString(),new PsiTransportDto(weight,money,quantity));
					Float tempWeight=weight+((productTemp.get("total")==null||productTemp.get("total").getWeight()==null)?0:productTemp.get("total").getWeight());
					Float tempMoney=money+((productTemp.get("total")==null||productTemp.get("total").getMoney()==null)?0:productTemp.get("total").getMoney());
					Integer tempQuantity=quantity+((productTemp.get("total")==null||productTemp.get("total").getQuantity()==null)?0:productTemp.get("total").getQuantity());
					
					productTemp.put("total",new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
				}else if("de,fr,it,es,uk".contains(obj[1].toString())){
					Map<String,Map<String,Map<String,PsiTransportDto>>> tempEU=map.get(yearMonth);
					if(tempEU==null){
						tempEU=Maps.newLinkedHashMap();
						map.put(obj[0].toString(),tempEU);
					} 
					Map<String,Map<String,PsiTransportDto>> tempCountryEu=tempEU.get("EU");
					if(tempCountryEu==null){
						tempCountryEu=Maps.newLinkedHashMap();
						tempEU.put("EU",tempCountryEu);
					}
					Map<String,PsiTransportDto> weightTempEU=tempCountryEu.get(obj[3].toString());
					if(weightTempEU==null){
						weightTempEU=Maps.newLinkedHashMap();
						tempCountryEu.put(obj[3].toString(), weightTempEU);
					} 
					Float tempWeight=weight+((weightTempEU.get(obj[2].toString())==null||weightTempEU.get(obj[2].toString()).getWeight()==null)?0:weightTempEU.get(obj[2].toString()).getWeight());
					Float tempMoney=money+((weightTempEU.get(obj[2].toString())==null||weightTempEU.get(obj[2].toString()).getMoney()==null)?0:weightTempEU.get(obj[2].toString()).getMoney());
					Integer tempQuantity=quantity+((weightTempEU.get(obj[2].toString())==null||weightTempEU.get(obj[2].toString()).getQuantity()==null)?0:weightTempEU.get(obj[2].toString()).getQuantity());
					weightTempEU.put(obj[2].toString(),new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
					
					Float tempWeight1=weight+((weightTempEU.get("total")==null||weightTempEU.get("total").getWeight()==null)?0:weightTempEU.get("total").getWeight());
					Float tempMoney1=money+((weightTempEU.get("total")==null||weightTempEU.get("total").getMoney()==null)?0:weightTempEU.get("total").getMoney());
					Integer tempQuantity1=quantity+((weightTempEU.get("total")==null||weightTempEU.get("total").getQuantity()==null)?0:weightTempEU.get("total").getQuantity());
					
					weightTempEU.put("total",new PsiTransportDto(tempWeight1,tempMoney1,tempQuantity1));
				}else if("com,ca,us,mx".contains(obj[1].toString())){
					Map<String,Map<String,Map<String,PsiTransportDto>>> tempEU=map.get(yearMonth);
					if(tempEU==null){
						tempEU=Maps.newLinkedHashMap();
						map.put(obj[0].toString(),tempEU);
					}
					Map<String,Map<String,PsiTransportDto>> tempCountryEu=tempEU.get("US");
					if(tempCountryEu==null){
						tempCountryEu=Maps.newLinkedHashMap();
						tempEU.put("US",tempCountryEu);
					}
					Map<String,PsiTransportDto> weightTempEU=tempCountryEu.get(obj[3].toString());
					if(weightTempEU==null){
						weightTempEU=Maps.newLinkedHashMap();
						tempCountryEu.put(obj[3].toString(), weightTempEU);
					}
					Float tempWeight=weight+((weightTempEU.get(obj[2].toString())==null||weightTempEU.get(obj[2].toString()).getWeight()==null)?0:weightTempEU.get(obj[2].toString()).getWeight());
					Float tempMoney=money+((weightTempEU.get(obj[2].toString())==null||weightTempEU.get(obj[2].toString()).getMoney()==null)?0:weightTempEU.get(obj[2].toString()).getMoney());
					Integer tempQuantity=quantity+((weightTempEU.get(obj[2].toString())==null||weightTempEU.get(obj[2].toString()).getQuantity()==null)?0:weightTempEU.get(obj[2].toString()).getQuantity());
					
					weightTempEU.put(obj[2].toString(),new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
					
					Float tempWeight1=weight+((weightTempEU.get("total")==null||weightTempEU.get("total").getWeight()==null)?0:weightTempEU.get("total").getWeight());
					Float tempMoney1=money+((weightTempEU.get("total")==null||weightTempEU.get("total").getMoney()==null)?0:weightTempEU.get("total").getMoney());
					Integer tempQuantity1=quantity+((weightTempEU.get("total")==null||weightTempEU.get("total").getQuantity()==null)?0:weightTempEU.get("total").getQuantity());
					
					weightTempEU.put("total",new PsiTransportDto(tempWeight1,tempMoney1,tempQuantity1));
				}
			}
			return map;
		}
		
	public Map<String,Map<String,String>> getExistId(){
		 Map<String,Map<String,String>> map=Maps.newHashMap();
		 String sql="SELECT product_name,country,GROUP_CONCAT(id) FROM psi_product_transport_rate r GROUP BY product_name,country";
		 List<Object[]> list=psiTransportPaymentDao.findBySql(sql);
		 for (Object[] obj: list) {
			    Map<String,String> temp=map.get(obj[0].toString());
				if(temp==null){
					temp=Maps.newHashMap();
					map.put(obj[0].toString(), temp);
				}
				temp.put(obj[1].toString(),obj[2].toString());
		 }
		 return map;
	}

	public PsiProductTransportRate getById(Integer id){
		return psiProductTransportRateDao.get(id);
	}
	//产品-国家(EU、JP、US)-PsiProductTransportRateDto
	public Map<String,Map<String,PsiProductTransportRate>> findTransportRateAndPrice(){
		 Map<String,Map<String,PsiProductTransportRate>> map=Maps.newHashMap();
		 Map<String,Map<String,Float>>  priceMap=findTransportAvgPrice();//country_name
		 String sql="SELECT r.`product_name`,r.`country`,r.`sea_rate`,r.`air_rate`,r.`express_rate`,r.`sea_price`,r.`air_price`,r.`express_price`,r.id FROM psi_product_transport_rate r ";
		 List<Object[]> list=psiTransportPaymentDao.findBySql(sql);
		 for (Object[] obj: list) {
			String productName=obj[0].toString();
			String country=obj[1].toString();
			Float seaRate=(obj[2]==null?new BigDecimal(0):(BigDecimal)obj[2]).floatValue();
			Float airRate=(obj[3]==null?new BigDecimal(0):(BigDecimal)obj[3]).floatValue();
			Float expressRate=(obj[4]==null?new BigDecimal(0):(BigDecimal)obj[4]).floatValue();
			
			Float seaPrice=(obj[5]==null?new BigDecimal(0):(BigDecimal)obj[5]).floatValue();
			Float airPrice=(obj[6]==null?new BigDecimal(0):(BigDecimal)obj[6]).floatValue();
			Float expressPrice=(obj[7]==null?new BigDecimal(0):(BigDecimal)obj[7]).floatValue();
			//Integer id=Integer.parseInt(obj[8].toString());
			Map<String,PsiProductTransportRate> temp=map.get(productName);
			if(temp==null){
				temp=Maps.newHashMap();
				map.put(productName, temp);
			}
			Float avgPrice=0f;
			if(priceMap!=null&&priceMap.get(country)!=null&&priceMap.get(country).get(productName)!=null&&priceMap.get(country).get(productName)!=0){
				avgPrice=priceMap.get(country).get(productName);
			}else{
				if(seaRate!=null&&seaPrice!=null){
					avgPrice+=seaRate*seaPrice;
				}
				if(airRate!=null&&airPrice!=null){
					avgPrice+=airRate*airPrice;
				}
				if(expressRate!=null&&expressPrice!=null){
					avgPrice+=expressRate*expressPrice;
				}
			}
			temp.put(country,new PsiProductTransportRate(productName,country,seaRate,airRate,expressRate,seaPrice,airPrice,expressPrice,avgPrice));
		}
		 return map;
	}
	
	    public  Map<String,Float> findAvgAirFee(){
	    	Map<String,Float>  map=Maps.newHashMap();
	    	Map<String,PsiTransportDto> tempMap=Maps.newHashMap();
	    	String sql=" SELECT LOWER(r.to_country) country,SUM(weight) weight, "+
					" SUM(IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0)) money   "+
					" 	FROM lc_psi_transport_order r  "+
					" WHERE  r.transport_sta='5' and model='0' AND tran_amount IS NOT NULL  AND  r.to_country IS NOT NULL AND oper_arrival_fixed_date>=:p1   "+
					" GROUP BY country ";
	    	 List<Object[]> list=psiTransportPaymentDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(),-180)));
	    	 for (Object[] obj: list) {
					String country=obj[0].toString();
					Float weight= ((BigDecimal)(obj[1]==null?new BigDecimal(0):obj[1])).floatValue();
					Float money= ((BigDecimal)(obj[2]==null?new BigDecimal(0):obj[2])).floatValue();
					if("jp".equals(country)){
						PsiTransportDto rp=tempMap.get("JP");
						tempMap.put("JP", new PsiTransportDto(weight+((rp==null||rp.getWeight()==null)?0:rp.getWeight()), money+((rp==null||rp.getMoney()==null)?0:rp.getMoney())));
					}else if("de,fr,it,es,uk".contains(country)){
						PsiTransportDto rp=tempMap.get("EU");
						tempMap.put("EU", new PsiTransportDto(weight+((rp==null||rp.getWeight()==null)?0:rp.getWeight()), money+((rp==null||rp.getMoney()==null)?0:rp.getMoney())));
					}else if("com,ca,us,mx".contains(country)){
						PsiTransportDto rp=tempMap.get("US");
						tempMap.put("US", new PsiTransportDto(weight+((rp==null||rp.getWeight()==null)?0:rp.getWeight()), money+((rp==null||rp.getMoney()==null)?0:rp.getMoney())));
					}
	    	 }	
	    	 for (Map.Entry<String,PsiTransportDto> entry: tempMap.entrySet()) {
	    		 PsiTransportDto dto=entry.getValue();
				 map.put(entry.getKey(),dto.getMoney()/dto.getWeight());
			 }
	    	 return map;
	    }
	
	//国家(EU、JP、US)-总重量和总运费(统计2个月到达的)
		public Map<String,PsiProductTransportRate> findTransportPriceByToCountry(){
			Map<String,PsiProductTransportRate> priceMap=Maps.newHashMap();
			Map<String,Map<String,PsiTransportDto>> map=Maps.newHashMap();
			/*String sql="SELECT LOWER(r.to_country) country,model,SUM(weight), "+
					" SUM(IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0)) money   "+
					" FROM psi_transport_order r  "+ 
					" WHERE  r.transport_sta='5'  and tran_amount is not null  AND  r.to_country IS NOT NULL  AND oper_arrival_fixed_date>=:p1 AND oper_arrival_fixed_date<=:p2   "+
					" GROUP BY LOWER(r.to_country),model  ";*/
			String sql=" SELECT r.country,r.model,SUM(r.weight),SUM(r.money) FROM "+
					" 	(SELECT LOWER(r.to_country) country,model,SUM(weight) weight,  "+
					" 	SUM(IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0)) money   "+
					" 	FROM psi_transport_order r  "+
					" 	WHERE  r.transport_sta='5'  AND tran_amount IS NOT NULL  AND  r.to_country IS NOT NULL  AND oper_arrival_fixed_date>=:p1 AND oper_arrival_fixed_date<=:p2 "+  
					" 	GROUP BY LOWER(r.to_country),model "+
					" 	UNION "+
					" 	SELECT LOWER(r.to_country) country,model,SUM(weight) weight, "+
					" SUM(IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0)) money   "+
					" 	FROM lc_psi_transport_order r  "+
					" WHERE  r.transport_sta='5'  AND tran_amount IS NOT NULL  AND  r.to_country IS NOT NULL AND oper_arrival_fixed_date>=:p1 AND oper_arrival_fixed_date<=:p2   "+
					" GROUP BY LOWER(r.to_country),model ) r GROUP BY r.country,r.model ";
					
			 List<Object[]> list=psiTransportPaymentDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(),-60),new Date()));
			 for (Object[] obj: list) {
				String country=obj[0].toString();
				String model=obj[1].toString();
				Float weight= ((BigDecimal)(obj[2]==null?new BigDecimal(0):obj[2])).floatValue();
				Float money= ((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue();
				
				if("jp".equals(country)){
					Map<String,PsiTransportDto> temp=map.get("JP");
					if(temp==null){
						temp=Maps.newHashMap();
						map.put("JP",temp);
					}
					temp.put(model, new PsiTransportDto(weight, money));
					PsiTransportDto rp=temp.get("total");
					temp.put("total", new PsiTransportDto(weight+((rp==null||rp.getWeight()==null)?0:rp.getWeight()), money+((rp==null||rp.getMoney()==null)?0:rp.getMoney())));
				}else if("de,fr,it,es,uk".contains(country)){
					Map<String,PsiTransportDto> temp=map.get("EU");
					if(temp==null){
						temp=Maps.newHashMap();
						map.put("EU",temp);
					}
					temp.put(model, new PsiTransportDto((temp.get(model)==null||temp.get(model).getWeight()==null)?weight:temp.get(model).getWeight()+weight,
							(temp.get(model)==null||temp.get(model).getMoney()==null)?money:temp.get(model).getMoney()+money));
					PsiTransportDto rp=temp.get("total");
					temp.put("total", new PsiTransportDto(weight+((rp==null||rp.getWeight()==null)?0:rp.getWeight()), money+((rp==null||rp.getMoney()==null)?0:rp.getMoney())));
				
				}else if("com,ca,us,mx".contains(country)){
					Map<String,PsiTransportDto> temp=map.get("US");
					if(temp==null){
						temp=Maps.newHashMap();
						map.put("US",temp);
					}
					temp.put(model, new PsiTransportDto((temp.get(model)==null||temp.get(model).getWeight()==null)?weight:temp.get(model).getWeight()+weight,
							(temp.get(model)==null||temp.get(model).getMoney()==null)?money:temp.get(model).getMoney()+money));
					PsiTransportDto rp=temp.get("total");
					temp.put("total", new PsiTransportDto(weight+((rp==null||rp.getWeight()==null)?0:rp.getWeight()), money+((rp==null||rp.getMoney()==null)?0:rp.getMoney())));
				}
			}	
			if(map!=null&&map.size()>0){
				for (Map.Entry<String, Map<String, PsiTransportDto>> entry: map.entrySet()) {
						String country = entry.getKey();
						Map<String,PsiTransportDto> temp=entry.getValue();
						PsiProductTransportRate rate=new PsiProductTransportRate();
					    PsiTransportDto trans=temp.get("total");//0:空运 1：海运 2：快递
					    PsiTransportDto airTrans=temp.get("0");
					    if(airTrans!=null&&airTrans.getWeight()!=null&&airTrans.getWeight()!=0){
							rate.setAirRate(airTrans.getWeight()/trans.getWeight());
						}
					    if(airTrans!=null&&airTrans.getMoney()!=null&&airTrans.getWeight()!=null&&airTrans.getWeight()!=0){
							rate.setAirPrice(airTrans.getMoney()/airTrans.getWeight());
						}
						PsiTransportDto seaTrans=temp.get("1");
						if(seaTrans!=null&&seaTrans.getWeight()!=null&&seaTrans.getWeight()!=0){
							rate.setSeaRate(seaTrans.getWeight()/trans.getWeight());
						}
						if(seaTrans!=null&&seaTrans.getMoney()!=null&&seaTrans.getWeight()!=null&&seaTrans.getWeight()!=0){
							rate.setSeaPrice(seaTrans.getMoney()/seaTrans.getWeight());
						}
						PsiTransportDto expressTrans=temp.get("2");
						if(expressTrans!=null&&expressTrans.getWeight()!=null&&expressTrans.getWeight()!=0){
							rate.setExpressRate(expressTrans.getWeight()/trans.getWeight());
						}
						if(expressTrans!=null&&expressTrans.getMoney()!=null&&expressTrans.getWeight()!=null&&expressTrans.getWeight()!=0){
							rate.setExpressPrice(expressTrans.getMoney()/expressTrans.getWeight());
						}
						float avgPrice=0f;
						if(rate.getSeaRate()!=null&&rate.getSeaPrice()!=null){
							avgPrice+=rate.getSeaRate()*rate.getSeaPrice();
						}
						if(rate.getAirRate()!=null&&rate.getAirPrice()!=null){
							avgPrice+=rate.getAirRate()*rate.getAirPrice();
						}
						if(rate.getExpressRate()!=null&&rate.getExpressPrice()!=null){
							avgPrice+=rate.getExpressRate()*rate.getExpressPrice();
						}
						rate.setAvgPrice(avgPrice);
						priceMap.put(country, rate);
				}
			}
			return priceMap;
		}
	
	/*//国家(EU、JP、US)-类型(0:空运 1：海运 2：快递)-总重量和总运费(统计2个月到达的)
	public Map<String,Map<String,PsiTransportDto>> findTransportPriceByToCountry(){
		Map<String,Map<String,PsiTransportDto>> map=Maps.newHashMap();
		String sql="SELECT LOWER(r.to_country) country,model,SUM(weight), "+
				" SUM(IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)) money   "+
				" FROM psi_transport_order r  "+ 
				" WHERE  r.transport_sta='5' AND arrival_date IS NOT NULL AND  r.to_country IS NOT NULL  AND arrival_date>=:p1 AND arrival_date<=:p2   "+
				" GROUP BY LOWER(r.to_country),model  ";
		 List<Object[]> list=psiTransportPaymentDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(),-60),new Date()));
		 for (Object[] obj: list) {
			String country=obj[0].toString();
			String model=obj[1].toString();
			Float weight= ((BigDecimal)(obj[2]==null?new BigDecimal(0):obj[2])).floatValue();
			Float money= ((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue();
			
			if("jp".equals(country)){
				Map<String,PsiTransportDto> temp=map.get("JP");
				if(temp==null){
					temp=Maps.newHashMap();
					map.put("JP",temp);
				}
				temp.put(model, new PsiTransportDto(weight, money));
			}else if("de,fr,it,es,uk".contains(country)){
				Map<String,PsiTransportDto> temp=map.get("EU");
				if(temp==null){
					temp=Maps.newHashMap();
					map.put("EU",temp);
				}
				temp.put(model, new PsiTransportDto((temp.get(model)==null||temp.get(model).getWeight()==null)?weight:temp.get(model).getWeight()+weight,
						(temp.get(model)==null||temp.get(model).getMoney()==null)?money:temp.get(model).getMoney()+money));
			}else if("com,ca,us,mx".contains(country)){
				Map<String,PsiTransportDto> temp=map.get("US");
				if(temp==null){
					temp=Maps.newHashMap();
					map.put("US",temp);
				}
				temp.put(model, new PsiTransportDto((temp.get(model)==null||temp.get(model).getWeight()==null)?weight:temp.get(model).getWeight()+weight,
						(temp.get(model)==null||temp.get(model).getMoney()==null)?money:temp.get(model).getMoney()+money));
			}
		}		
		return map;
	}*/
	
	
		public List<String> getErrorGw(){
			String sql="SELECT CONCAT(CONCAT(brand,' ',model),CASE WHEN SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)!='' THEN CONCAT('_',SUBSTRING_INDEX(SUBSTRING_INDEX(a.color,',',b.help_topic_id+1),',',-1)) ELSE '' END) NAME "+
					" FROM psi_product a JOIN mysql.help_topic b  ON b.help_topic_id < (LENGTH(a.color) - LENGTH(REPLACE(a.color,',',''))+1) WHERE a.`del_flag`='0' "+
					" AND a.gw/a.`pack_quantity`>3 AND a.id NOT IN (217,218) and a.type!='Tomons Lamps' ";
			return psiTransportPaymentDao.findBySql(sql);
		}
	    // 国家(EU、JP、US)- 产品名称- 运输方式(0:空运 1：海运 2：快递)(只统计60天前到达的)
		public Map<String,Map<String,Map<String,PsiTransportDto>>> findPsiTransportRateAndPrice(){
			Map<String,Map<String,Map<String,PsiTransportDto>>>  map=Maps.newLinkedHashMap();
			List<String> errorList=getErrorGw();
			Set<String> sendErrorName=Sets.newHashSet();
			String sql="SELECT LOWER(r.to_country) country,r.model,CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END ), " +
					" SUM((IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0))*(a.`quantity`*b.`gw`/("+
					" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  "+
					"		 WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  "+
					"		 WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  "+
					
					"*r.`weight`))) money, " +
					" SUM(a.`quantity`*b.`gw`/"+
					" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  "+
					"		 WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  "+
					"		 WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  "+
					
					"),sum(a.quantity) "+
					" FROM psi_transport_order r ,psi_transport_order_item AS a,psi_product AS b WHERE  r.transport_type in ('0','1') and r.id=a.`transport_order_id` AND a.del_flag='0' AND b.id=a.`product_id`  and b.del_flag='0' AND  r.transport_sta='5' "+
					" and tran_amount is not null AND  r.to_country IS NOT NULL  AND oper_arrival_fixed_date>=:p1 AND oper_arrival_fixed_date<=:p2 GROUP BY LOWER(r.to_country),model,CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END ) ";

			List<Object[]> list=psiTransportPaymentDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(),-60),new Date()));
			for (Object[] obj: list) {
				String country =obj[0].toString();
				String model =obj[1].toString();
				String name=obj[2].toString();
				Float money= ((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue();
				Float weight= ((BigDecimal)(obj[4]==null?new BigDecimal(0):obj[4])).floatValue();
				Integer quantity=Integer.parseInt(obj[5].toString());
				if(errorList!=null&&errorList.size()>0&&errorList.contains(name)){
					sendErrorName.add(name);
					continue;
				}
				if("jp".equals(country)){
					
					Map<String,Map<String,PsiTransportDto>> weightTemp=map.get("JP");
					if(weightTemp==null){
						weightTemp=Maps.newLinkedHashMap();
						map.put("JP",weightTemp);
					}
					Map<String,PsiTransportDto> productTemp=weightTemp.get(name);
					if(productTemp==null){
						productTemp=Maps.newLinkedHashMap();
						weightTemp.put(name,productTemp);
					}
					productTemp.put(model,new PsiTransportDto(weight,money,quantity));
					Float tempWeight=weight+((productTemp.get("total")==null||productTemp.get("total").getWeight()==null)?0:productTemp.get("total").getWeight());
					Float tempMoney=money+((productTemp.get("total")==null||productTemp.get("total").getMoney()==null)?0:productTemp.get("total").getMoney());
					Integer tempQuantity=quantity+((productTemp.get("total")==null||productTemp.get("total").getQuantity()==null)?0:productTemp.get("total").getQuantity());
					
					productTemp.put("total",new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
				}else if("de,fr,it,es,uk".contains(country)){
					
					Map<String,Map<String,PsiTransportDto>> tempCountryEu=map.get("EU");
					if(tempCountryEu==null){
						tempCountryEu=Maps.newLinkedHashMap();
						map.put("EU",tempCountryEu);
					}
					Map<String,PsiTransportDto> weightTempEU=tempCountryEu.get(name);
					if(weightTempEU==null){
						weightTempEU=Maps.newLinkedHashMap();
						tempCountryEu.put(name, weightTempEU);
					}
					Float tempWeight=weight+((weightTempEU.get(model)==null||weightTempEU.get(model).getWeight()==null)?0:weightTempEU.get(model).getWeight());
					Float tempMoney=money+((weightTempEU.get(model)==null||weightTempEU.get(model).getMoney()==null)?0:weightTempEU.get(model).getMoney());
					Integer tempQuantity=quantity+((weightTempEU.get(model)==null||weightTempEU.get(model).getQuantity()==null)?0:weightTempEU.get(model).getQuantity());
					
					weightTempEU.put(model,new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
					
					Float tempWeight1=weight+((weightTempEU.get("total")==null||weightTempEU.get("total").getWeight()==null)?0:weightTempEU.get("total").getWeight());
					Float tempMoney1=money+((weightTempEU.get("total")==null||weightTempEU.get("total").getMoney()==null)?0:weightTempEU.get("total").getMoney());
					Integer tempQuantity1=quantity+((weightTempEU.get("total")==null||weightTempEU.get("total").getQuantity()==null)?0:weightTempEU.get("total").getQuantity());
					
					weightTempEU.put("total",new PsiTransportDto(tempWeight1,tempMoney1,tempQuantity1));
				}else if("com,ca,us,mx".contains(country)){
					
					Map<String,Map<String,PsiTransportDto>> tempCountryEu=map.get("US");
					if(tempCountryEu==null){
						tempCountryEu=Maps.newLinkedHashMap();
						map.put("US",tempCountryEu);
					}
					Map<String,PsiTransportDto> weightTempEU=tempCountryEu.get(name);
					if(weightTempEU==null){
						weightTempEU=Maps.newLinkedHashMap();
						tempCountryEu.put(name, weightTempEU);
					}
					Float tempWeight=weight+((weightTempEU.get(model)==null||weightTempEU.get(model).getWeight()==null)?0:weightTempEU.get(model).getWeight());
					Float tempMoney=money+((weightTempEU.get(model)==null||weightTempEU.get(model).getMoney()==null)?0:weightTempEU.get(model).getMoney());
					Integer tempQuantity=quantity+((weightTempEU.get(model)==null||weightTempEU.get(model).getQuantity()==null)?0:weightTempEU.get(model).getQuantity());
					
					weightTempEU.put(model,new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
					
					Float tempWeight1=weight+((weightTempEU.get("total")==null||weightTempEU.get("total").getWeight()==null)?0:weightTempEU.get("total").getWeight());
					Float tempMoney1=money+((weightTempEU.get("total")==null||weightTempEU.get("total").getMoney()==null)?0:weightTempEU.get("total").getMoney());
					Integer tempQuantity1=quantity+((weightTempEU.get("total")==null||weightTempEU.get("total").getQuantity()==null)?0:weightTempEU.get("total").getQuantity());
					
					weightTempEU.put("total",new PsiTransportDto(tempWeight1,tempMoney1,tempQuantity1));
				}
			}
			if(sendErrorName!=null&&sendErrorName.size()>0){
				String toEmail="eileen|tim";
				StringBuilder cnt=new StringBuilder("产品毛重异常：\n\n");
				for (String name: sendErrorName) {
					cnt.append(name).append("\n");
				}
				WeixinSendMsgUtil.sendTextMsgToUser(toEmail,cnt.toString());
			}
			return map;
		}
		
		public Map<String,Map<String,Map<String,PsiTransportDto>>> findPsiTransportRateAndPrice2(){
			Map<String,Map<String,Map<String,PsiTransportDto>>>  map=Maps.newLinkedHashMap();
			List<String> errorList=getErrorGw();
			Set<String> sendErrorName=Sets.newHashSet();
			String sql="SELECT LOWER(r.to_country) country,r.model,CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END ), " +
					" SUM((IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0))*(a.`quantity`*b.`gw`/("+
					" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  "+
					"		 WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  "+
					"		 WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  "+
					
					"*r.`weight`))) money, " +
					" SUM(a.`quantity`*b.`gw`/"+
					" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  "+
					"		 WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  "+
					"		 WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  "+
					
					"),sum(a.quantity) "+
					" FROM lc_psi_transport_order r ,lc_psi_transport_order_item AS a,psi_product AS b WHERE  r.transport_type in ('0','1') and r.id=a.`transport_order_id` AND a.del_flag='0' AND b.id=a.`product_id`  and b.del_flag='0' AND  r.transport_sta='5' "+
					" and tran_amount is not null AND  r.to_country IS NOT NULL  AND oper_arrival_fixed_date>=:p1 AND oper_arrival_fixed_date<=:p2 GROUP BY LOWER(r.to_country),model,CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END ) ";

			List<Object[]> list=psiTransportPaymentDao.findBySql(sql,new Parameter(DateUtils.addDays(new Date(),-60),new Date()));
			for (Object[] obj: list) {
				String country =obj[0].toString();
				String model =obj[1].toString();
				String name=obj[2].toString();
				Float money= ((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue();
				Float weight= ((BigDecimal)(obj[4]==null?new BigDecimal(0):obj[4])).floatValue();
				Integer quantity=Integer.parseInt(obj[5].toString());
				if(errorList!=null&&errorList.size()>0&&errorList.contains(name)){
					sendErrorName.add(name);
					continue;
				}
				if("jp".equals(country)){
					
					Map<String,Map<String,PsiTransportDto>> weightTemp=map.get("JP");
					if(weightTemp==null){
						weightTemp=Maps.newLinkedHashMap();
						map.put("JP",weightTemp);
					}
					Map<String,PsiTransportDto> productTemp=weightTemp.get(name);
					if(productTemp==null){
						productTemp=Maps.newLinkedHashMap();
						weightTemp.put(name,productTemp);
					}
					productTemp.put(model,new PsiTransportDto(weight,money,quantity));
					Float tempWeight=weight+((productTemp.get("total")==null||productTemp.get("total").getWeight()==null)?0:productTemp.get("total").getWeight());
					Float tempMoney=money+((productTemp.get("total")==null||productTemp.get("total").getMoney()==null)?0:productTemp.get("total").getMoney());
					Integer tempQuantity=quantity+((productTemp.get("total")==null||productTemp.get("total").getQuantity()==null)?0:productTemp.get("total").getQuantity());
					
					productTemp.put("total",new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
				}else if("de,fr,it,es,uk".contains(country)){
					
					Map<String,Map<String,PsiTransportDto>> tempCountryEu=map.get("EU");
					if(tempCountryEu==null){
						tempCountryEu=Maps.newLinkedHashMap();
						map.put("EU",tempCountryEu);
					}
					Map<String,PsiTransportDto> weightTempEU=tempCountryEu.get(name);
					if(weightTempEU==null){
						weightTempEU=Maps.newLinkedHashMap();
						tempCountryEu.put(name, weightTempEU);
					}
					Float tempWeight=weight+((weightTempEU.get(model)==null||weightTempEU.get(model).getWeight()==null)?0:weightTempEU.get(model).getWeight());
					Float tempMoney=money+((weightTempEU.get(model)==null||weightTempEU.get(model).getMoney()==null)?0:weightTempEU.get(model).getMoney());
					Integer tempQuantity=quantity+((weightTempEU.get(model)==null||weightTempEU.get(model).getQuantity()==null)?0:weightTempEU.get(model).getQuantity());
					
					weightTempEU.put(model,new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
					
					Float tempWeight1=weight+((weightTempEU.get("total")==null||weightTempEU.get("total").getWeight()==null)?0:weightTempEU.get("total").getWeight());
					Float tempMoney1=money+((weightTempEU.get("total")==null||weightTempEU.get("total").getMoney()==null)?0:weightTempEU.get("total").getMoney());
					Integer tempQuantity1=quantity+((weightTempEU.get("total")==null||weightTempEU.get("total").getQuantity()==null)?0:weightTempEU.get("total").getQuantity());
					
					weightTempEU.put("total",new PsiTransportDto(tempWeight1,tempMoney1,tempQuantity1));
				}else if("com,ca,us,mx".contains(country)){
					
					Map<String,Map<String,PsiTransportDto>> tempCountryEu=map.get("US");
					if(tempCountryEu==null){
						tempCountryEu=Maps.newLinkedHashMap();
						map.put("US",tempCountryEu);
					}
					Map<String,PsiTransportDto> weightTempEU=tempCountryEu.get(name);
					if(weightTempEU==null){
						weightTempEU=Maps.newLinkedHashMap();
						tempCountryEu.put(name, weightTempEU);
					}
					Float tempWeight=weight+((weightTempEU.get(model)==null||weightTempEU.get(model).getWeight()==null)?0:weightTempEU.get(model).getWeight());
					Float tempMoney=money+((weightTempEU.get(model)==null||weightTempEU.get(model).getMoney()==null)?0:weightTempEU.get(model).getMoney());
					Integer tempQuantity=quantity+((weightTempEU.get(model)==null||weightTempEU.get(model).getQuantity()==null)?0:weightTempEU.get(model).getQuantity());
					
					weightTempEU.put(model,new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
					
					Float tempWeight1=weight+((weightTempEU.get("total")==null||weightTempEU.get("total").getWeight()==null)?0:weightTempEU.get("total").getWeight());
					Float tempMoney1=money+((weightTempEU.get("total")==null||weightTempEU.get("total").getMoney()==null)?0:weightTempEU.get("total").getMoney());
					Integer tempQuantity1=quantity+((weightTempEU.get("total")==null||weightTempEU.get("total").getQuantity()==null)?0:weightTempEU.get("total").getQuantity());
					
					weightTempEU.put("total",new PsiTransportDto(tempWeight1,tempMoney1,tempQuantity1));
				}
			}
			if(sendErrorName!=null&&sendErrorName.size()>0){
				String toEmail="eileen|tim";
				StringBuilder cnt=new StringBuilder("产品毛重异常：\n\n");
				for (String name: sendErrorName) {
					cnt.append(name).append("\n");
				}
				WeixinSendMsgUtil.sendTextMsgToUser(toEmail,cnt.toString());
			}
			return map;
		}
		
		// 国家(EU、JP、US)- 产品名称- 数量和运费(all到达)
				public Map<String,Map<String,PsiTransportDto>> initPsiTransportAvgPrice(){
					List<String> errorList=getErrorGw();
					Map<String,Map<String,PsiTransportDto>>   map=Maps.newLinkedHashMap();
					String sql="SELECT LOWER(r.to_country) country,CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END ), " +
							" SUM((IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0))*(a.`quantity`*b.`gw`/("+
							" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  "+
							"		 WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  "+
							"		 WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  "+
							
							" *r.`weight`))) money, " +
							" SUM(a.`quantity`*b.`gw`/"+
							" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  "+
							"		 WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  "+
							"		 WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  "+
							
							" ),sum(a.quantity) "+
							" FROM psi_transport_order r ,psi_transport_order_item AS a,psi_product AS b WHERE  r.transport_type in ('0','1') and r.id=a.`transport_order_id` AND a.del_flag='0' AND b.id=a.`product_id`  and b.del_flag='0' AND  r.transport_sta='5' "+
							" and r.`transport_type` in ('0','1') and tran_amount is not null AND  r.to_country IS NOT NULL GROUP BY LOWER(r.to_country),CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END ) ";
					List<Object[]> list=psiTransportPaymentDao.findBySql(sql);
					for (Object[] obj: list) {
						String country =obj[0].toString();
						String name=obj[1].toString();
						Float money= ((BigDecimal)(obj[2]==null?new BigDecimal(0):obj[2])).floatValue();
						Float weight= ((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue();
						Integer quantity=Integer.parseInt(obj[4].toString());
						if(errorList!=null&&errorList.size()>0&&errorList.contains(name)){
							continue;
						}
						if("jp".equals(country)){
							Map<String,PsiTransportDto> countryMap=map.get("JP");
							if(countryMap==null){
								countryMap=Maps.newLinkedHashMap();
								map.put("JP",countryMap);
							}
							countryMap.put(name,new PsiTransportDto(weight,money,quantity));
						}else if("de,fr,it,es,uk".contains(country)){
							Map<String,PsiTransportDto> countryMap=map.get("EU");
							if(countryMap==null){
								countryMap=Maps.newLinkedHashMap();
								map.put("EU",countryMap);
							}
							Float tempWeight=((countryMap.get(name)==null||countryMap.get(name).getWeight()==null)?weight:weight+countryMap.get(name).getWeight());
							Float tempMoney=((countryMap.get(name)==null||countryMap.get(name).getMoney()==null)?money:money+countryMap.get(name).getMoney());
							Integer tempQuantity=((countryMap.get(name)==null||countryMap.get(name).getQuantity()==null)?quantity:quantity+countryMap.get(name).getQuantity());
							countryMap.put(name,new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
						}else if("com,ca,us,mx".contains(country)){
							Map<String,PsiTransportDto> countryMap=map.get("US");
							if(countryMap==null){
								countryMap=Maps.newLinkedHashMap();
								map.put("US",countryMap);
							}
							Float tempWeight=((countryMap.get(name)==null||countryMap.get(name).getWeight()==null)?weight:weight+countryMap.get(name).getWeight());
							Float tempMoney=((countryMap.get(name)==null||countryMap.get(name).getMoney()==null)?money:money+countryMap.get(name).getMoney());
							Integer tempQuantity=((countryMap.get(name)==null||countryMap.get(name).getQuantity()==null)?quantity:quantity+countryMap.get(name).getQuantity());
							countryMap.put(name,new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
						}
					}
					return map;
				}
	
			public List<Integer> findIsCount(String type){
				String sql="SELECT id FROM psi_transport_order r WHERE  r.transport_sta='5' and r.transport_type=:p1 AND  r.tran_amount IS NOT NULL AND (r.is_count IS NULL or r.is_count='')";
				return psiTransportPaymentDao.findBySql(sql,new Parameter(type));
			}
			
			public List<Integer> findIsCount2(String type){
				String sql="SELECT id FROM lc_psi_transport_order r WHERE  r.transport_sta='5' and r.transport_type=:p1 AND  r.tran_amount IS NOT NULL AND (r.is_count IS NULL or r.is_count='')";
				return psiTransportPaymentDao.findBySql(sql,new Parameter(type));
			}
			
			
			@Transactional(readOnly = false)
			public void updateIsCount(){//com ca de jp us mx
				String sql="SELECT transport_no,LOWER(r.to_country) toCountry, r.`model`, "+
						" (IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0))/r.`weight`  "+
						" FROM lc_psi_transport_order r  WHERE r.transport_sta='5'  AND tran_amount IS NOT NULL AND (r.is_count IS NULL or r.is_count='') and r.to_country is not null";
				List<Object[]> list=psiTransportPaymentDao.findBySql(sql);
				List<String> errorList=Lists.newArrayList();
				if(list!=null&&list.size()>0){
					for (Object[] obj: list) {//1：sea 0:air 2:express
						String tranNo=obj[0].toString();
						String country=obj[1].toString();
						String model=obj[2].toString();
						Float price=Float.parseFloat(obj[3].toString());
						
						if("com,ca,mx,us".contains(country)){
							if("0".equals(model)&&price>39*1.2){
								errorList.add(tranNo);
							}else if("1".equals(model)&&price>25*1.2){
								errorList.add(tranNo);
							}else if("2".equals(model)&&price>67*1.2){
								errorList.add(tranNo);
							}
						}else if("de".equals(country)){
                            if("0".equals(model)&&price>33*1.2){
                            	errorList.add(tranNo);
							}else if("1".equals(model)&&price>9.8*1.2){
								errorList.add(tranNo);
							}else if("2".equals(model)&&price>43.6*1.2){
								errorList.add(tranNo);
							}else if("3".equals(model)&&price>9.6*1.2){
								errorList.add(tranNo);
							}
						}else if("jp".equals(country)){
                            if("0".equals(model)&&price>51*1.2){
                            	errorList.add(tranNo);
							}else if("1".equals(model)&&price>17*1.2){
								errorList.add(tranNo);
							}else if("2".equals(model)&&price>42*1.2){
								errorList.add(tranNo);
							}
						}
					}
				}
				if(errorList!=null&&errorList.size()>0){
					updateIsCountByTranNo(errorList);
					String toEmail="eileen";
					StringBuilder cnt=new StringBuilder("运单异常：\n\n");
					for (String name: errorList) {
						cnt.append(name).append("\n");
					}
					WeixinSendMsgUtil.sendTextMsgToUser(toEmail,cnt.toString());
				}
			}
			
			@Transactional(readOnly = false)
			public void updateIsCountByTranNo(List<String>  tranNo){
				String sql="update lc_psi_transport_order set is_count='2' where transport_no in :p1 ";
				psiTransportPaymentDao.updateBySql(sql, new Parameter(tranNo));
			}
			
			
			@Transactional(readOnly = false)
			public void updateIsCount(List<Integer>  idList){
				String sql="update psi_transport_order set is_count='0' where id in :p1 ";
				psiTransportPaymentDao.updateBySql(sql, new Parameter(idList));
			}
			
			@Transactional(readOnly = false)
			public void updateIsCount2(List<Integer>  idList){
				String sql="update lc_psi_transport_order set is_count='0' where id in :p1 ";
				psiTransportPaymentDao.updateBySql(sql, new Parameter(idList));
			}
				
			public Map<String,Map<String,PsiTransportDto>> getYesterdayInStock(String type){
				List<String> errorList=getErrorGw();
				String sql= " SELECT LOWER(r.to_country) toCountry,CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END ) NAME "+
						"  ,SUM((IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0))*(a.`quantity`*b.`gw`/(" +
						" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  "+
						"		 WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  "+
						"		 WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  "+
						" *r.`weight`))) money "+ 
						" ,SUM(a.`quantity`*b.`gw`/"+
						" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  "+
						"		 WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  "+
						"		 WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  "+
						" ),sum(a.quantity) "+
						"  FROM psi_transport_order r ,psi_transport_order_item AS a,psi_product AS b WHERE  r.transport_type=:p1 and r.id=a.`transport_order_id` AND a.del_flag='0' AND b.id=a.`product_id`  AND b.del_flag='0' AND  r.transport_sta='5'  "+
						" and tran_amount is not null and (r.is_count is null or r.is_count='')  GROUP BY toCountry,NAME ";//AND r.oper_arrival_fixed_date<CURDATE() AND r.oper_arrival_fixed_date>=DATE_ADD(CURDATE(),INTERVAL -1 DAY)
				Map<String,Map<String,PsiTransportDto>>   map=Maps.newLinkedHashMap();
				List<Object[]> list=psiTransportPaymentDao.findBySql(sql,new Parameter(type));
				for (Object[] obj: list) {
					String country =obj[0].toString();
					String name=obj[1].toString();
					Float money= ((BigDecimal)(obj[2]==null?new BigDecimal(0):obj[2])).floatValue();
					Float weight= ((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue();
					Integer quantity=Integer.parseInt(obj[4].toString());
					if(errorList!=null&&errorList.size()>0&&errorList.contains(name)){
						continue;
					}
					if("jp".equals(country)){
						Map<String,PsiTransportDto> countryMap=map.get("JP");
						if(countryMap==null){
							countryMap=Maps.newLinkedHashMap();
							map.put("JP",countryMap);
						}
						countryMap.put(name,new PsiTransportDto(weight,money,quantity));
					}else if("de,fr,it,es,uk".contains(country)){
						Map<String,PsiTransportDto> countryMap=map.get("EU");
						if(countryMap==null){
							countryMap=Maps.newLinkedHashMap();
							map.put("EU",countryMap);
						}
						Float tempWeight=((countryMap.get(name)==null||countryMap.get(name).getWeight()==null)?weight:weight+countryMap.get(name).getWeight());
						Float tempMoney=((countryMap.get(name)==null||countryMap.get(name).getMoney()==null)?money:money+countryMap.get(name).getMoney());
						Integer tempQuantity=((countryMap.get(name)==null||countryMap.get(name).getQuantity()==null)?quantity:quantity+countryMap.get(name).getQuantity());
						countryMap.put(name,new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
					}else if("com,ca,us,mx".contains(country)){
						Map<String,PsiTransportDto> countryMap=map.get("US");
						if(countryMap==null){
							countryMap=Maps.newLinkedHashMap();
							map.put("US",countryMap);
						}
						Float tempWeight=((countryMap.get(name)==null||countryMap.get(name).getWeight()==null)?weight:weight+countryMap.get(name).getWeight());
						Float tempMoney=((countryMap.get(name)==null||countryMap.get(name).getMoney()==null)?money:money+countryMap.get(name).getMoney());
						Integer tempQuantity=((countryMap.get(name)==null||countryMap.get(name).getQuantity()==null)?quantity:quantity+countryMap.get(name).getQuantity());
						countryMap.put(name,new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
					}
				}
				return map;
			}
			
			public Map<String,Map<String,PsiTransportDto>> getYesterdayInStock2(String type){
				List<String> errorList=getErrorGw();
				String sql= " SELECT LOWER(r.to_country) toCountry,CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END ) NAME "+
						"  ,SUM((IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0))*(a.`quantity`*b.`gw`/(" +
						" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  "+
						"		 WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  "+
						"		 WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  "+
						" *r.`weight`))) money "+ 
						" ,SUM(a.`quantity`*b.`gw`/"+
						" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  "+
						"		 WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  "+
						"		 WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  "+
						" ),sum(a.quantity) "+
						"  FROM lc_psi_transport_order r ,lc_psi_transport_order_item AS a,psi_product AS b WHERE  r.transport_type =:p1 and r.id=a.`transport_order_id` AND a.del_flag='0' AND b.id=a.`product_id`  AND b.del_flag='0' AND  r.transport_sta='5'  ";
				    if("1".equals(type)){//fba
				    	sql+=" and r.from_store='130' ";
				    }
					sql+=	" and tran_amount is not null and (r.is_count is null or r.is_count='') GROUP BY toCountry,NAME ";//AND r.oper_arrival_fixed_date<CURDATE() AND r.oper_arrival_fixed_date>=DATE_ADD(CURDATE(),INTERVAL -1 DAY)
				Map<String,Map<String,PsiTransportDto>>   map=Maps.newLinkedHashMap();
				List<Object[]> list=psiTransportPaymentDao.findBySql(sql,new Parameter(type));
				for (Object[] obj: list) {
					String country =obj[0].toString();
					String name=obj[1].toString();
					Float money= ((BigDecimal)(obj[2]==null?new BigDecimal(0):obj[2])).floatValue();
					Float weight= ((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue();
					Integer quantity=Integer.parseInt(obj[4].toString());
					if(errorList!=null&&errorList.size()>0&&errorList.contains(name)){
						continue;
					}
					if("jp".equals(country)){
						Map<String,PsiTransportDto> countryMap=map.get("JP");
						if(countryMap==null){
							countryMap=Maps.newLinkedHashMap();
							map.put("JP",countryMap);
						}
						countryMap.put(name,new PsiTransportDto(weight,money,quantity));
					}else if("de,fr,it,es,uk".contains(country)){
						Map<String,PsiTransportDto> countryMap=map.get("EU");
						if(countryMap==null){
							countryMap=Maps.newLinkedHashMap();
							map.put("EU",countryMap);
						}
						Float tempWeight=((countryMap.get(name)==null||countryMap.get(name).getWeight()==null)?weight:weight+countryMap.get(name).getWeight());
						Float tempMoney=((countryMap.get(name)==null||countryMap.get(name).getMoney()==null)?money:money+countryMap.get(name).getMoney());
						Integer tempQuantity=((countryMap.get(name)==null||countryMap.get(name).getQuantity()==null)?quantity:quantity+countryMap.get(name).getQuantity());
						countryMap.put(name,new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
					}else if("com,ca,us,mx".contains(country)){
						Map<String,PsiTransportDto> countryMap=map.get("US");
						if(countryMap==null){
							countryMap=Maps.newLinkedHashMap();
							map.put("US",countryMap);
						}
						Float tempWeight=((countryMap.get(name)==null||countryMap.get(name).getWeight()==null)?weight:weight+countryMap.get(name).getWeight());
						Float tempMoney=((countryMap.get(name)==null||countryMap.get(name).getMoney()==null)?money:money+countryMap.get(name).getMoney());
						Integer tempQuantity=((countryMap.get(name)==null||countryMap.get(name).getQuantity()==null)?quantity:quantity+countryMap.get(name).getQuantity());
						countryMap.put(name,new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
					}
				}
				return map;
			}

			public Map<String,Map<String,Float>> findTransportAvgPrice(){
				Map<String,Map<String,Float>> map=Maps.newHashMap();
				String sql="SELECT country,product_name,GROUP_CONCAT(avg_price ORDER BY update_date DESC) FROM psi_product_avg_price p  "+
                   " GROUP BY country,product_name ORDER BY update_date DESC ";
				List<Object[]> list=psiTransportPaymentDao.findBySql(sql);
				for (Object[] obj: list) {
					String country=obj[0].toString();
					String productName=obj[1].toString();
					Float price=Float.parseFloat(obj[2].toString().split(",")[0]);
					Map<String,Float> temp=map.get(country);
					if(temp==null){
						temp=Maps.newHashMap();
						map.put(country,temp);
					}
					temp.put(productName, price);
				}
				return map;
			}
			
			public Map<String,Map<String,Float>> findTransportLocalAvgPrice(){
				Map<String,Map<String,Float>> map=Maps.newHashMap();
				String sql="SELECT country,product_name,GROUP_CONCAT(avg_price ORDER BY update_date DESC) FROM psi_product_avg_price_local p  "+
                   " GROUP BY country,product_name ORDER BY update_date DESC ";
				List<Object[]> list=psiTransportPaymentDao.findBySql(sql);
				for (Object[] obj: list) {
					String country=obj[0].toString();
					String productName=obj[1].toString();
					Float price=Float.parseFloat(obj[2].toString().split(",")[0]);
					Map<String,Float> temp=map.get(country);
					if(temp==null){
						temp=Maps.newHashMap();
						map.put(country,temp);
					}
					temp.put(productName, price);
				}
				return map;
			}
			
			
			//月份 国家 产品名称 运输方式
			public Map<String,Map<String,Map<String,PsiTransportDto>>> getOffLine(PsiTransportPayment psiTransportPayment,String productName){
				Map<String,Map<String,Map<String,PsiTransportDto>>> map=Maps.newLinkedHashMap();
				String sql="SELECT DATE_FORMAT(etd_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,r.model, " +
						" SUM((IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0))*(a.`quantity`*b.`gw`/( "+
						" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  " +
						" WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  " +
						" WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  " +
			            " *r.`weight`))) money, " +
						" SUM(a.`quantity`*b.`gw`/ "+
						" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  " +
						" WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  " +
						" WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  " +
			            " ),sum(a.quantity) "+
						" FROM psi_transport_order r ,psi_transport_order_item AS a,psi_product AS b WHERE r.id=a.`transport_order_id` AND a.del_flag='0' AND b.id=a.`product_id`  and b.del_flag='0' AND  r.transport_sta!='8' "+
						"  and r.transport_type='3' AND etd_date IS NOT NULL AND  r.to_country IS NOT NULL  AND DATE_FORMAT(etd_date,'%Y%m')>=:p1 AND DATE_FORMAT(etd_date,'%Y%m')<=:p2 ";
				        if(StringUtils.isNotBlank(productName)){
				        	sql+=" and CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END )=:p3 ";
				        }
				sql+=" GROUP BY LOWER(r.to_country),DATE_FORMAT(etd_date,'%Y%m'),model  ORDER BY yearAndMonth ";
				List<Object[]> list=Lists.newArrayList();
				if(StringUtils.isNotBlank(productName)){
					list=psiTransportPaymentDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getSureDate()),new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getUpdateDate()),productName));
				}else{
					list=psiTransportPaymentDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getSureDate()),new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getUpdateDate())));
				}
				for (Object[] obj: list) {
					String yearMonth =obj[0].toString();
					String country=obj[1].toString();
					String model=obj[2].toString();
					Float money= ((BigDecimal)(obj[3]==null?new BigDecimal(0):obj[3])).floatValue();
					Float weight= ((BigDecimal)(obj[4]==null?new BigDecimal(0):obj[4])).floatValue();
					Integer quantity=Integer.parseInt(obj[5].toString());	
					if("jp".equals(obj[1].toString())){
						country="JP";
					}else if("de,fr,it,es,uk".contains(obj[1].toString())){
						country="EU";
					}else if("com,ca,us,mx".contains(obj[1].toString())){
						country="US";
					}
					Map<String,Map<String,PsiTransportDto>> monthMap=map.get(yearMonth);
					if(monthMap==null){
						monthMap=Maps.newLinkedHashMap();
						map.put(yearMonth, monthMap);
					}
					Map<String,PsiTransportDto> countryMap=monthMap.get(country);
					if(countryMap==null){
						countryMap=Maps.newLinkedHashMap();
						monthMap.put(country, countryMap);
					}
					PsiTransportDto dto=new PsiTransportDto(weight,money,quantity);
					PsiTransportDto modelDto=countryMap.get(model);
					if(modelDto==null){
						countryMap.put(model,dto);
					}else{
						countryMap.put(model,new PsiTransportDto(weight+modelDto.getWeight(),money+modelDto.getMoney(),quantity+modelDto.getQuantity()));
					}
				    
				    PsiTransportDto ycDto=countryMap.get("total");
					if(ycDto==null){
						countryMap.put("total",dto);
					}else{
						countryMap.put("total",new PsiTransportDto(weight+ycDto.getWeight(),money+ycDto.getMoney(),quantity+ycDto.getQuantity()));
					}
				    
				    
				    Map<String,Map<String,PsiTransportDto>> totalMonthMap=map.get("total");
					if(totalMonthMap==null){
						totalMonthMap=Maps.newLinkedHashMap();
						map.put("total", totalMonthMap);
					}
					Map<String,PsiTransportDto> totalCountryMap=totalMonthMap.get(country);
					if(totalCountryMap==null){
						totalCountryMap=Maps.newLinkedHashMap();
						totalMonthMap.put(country, totalCountryMap);
					}
					PsiTransportDto totalDto=totalCountryMap.get(model);
					if(totalDto==null){
						totalCountryMap.put(model,dto);
					}else{
						totalCountryMap.put(model,new PsiTransportDto(weight+totalDto.getWeight(),money+totalDto.getMoney(),quantity+totalDto.getQuantity()));
					}
					PsiTransportDto totalYcDto=totalCountryMap.get("total");
					if(totalYcDto==null){
						totalCountryMap.put("total",dto);
					}else{
						totalCountryMap.put("total",new PsiTransportDto(weight+totalYcDto.getWeight(),money+totalYcDto.getMoney(),quantity+totalYcDto.getQuantity()));
					}
					
					
				}
				return map;
			}
			
			
			
			
			//月份 国家 产品名称 运输方式
			public Map<String,Map<String,Map<String,Map<String,PsiTransportDto>>>> getOffLineByModel(PsiTransportPayment psiTransportPayment){
				Map<String,Map<String,Map<String,Map<String,PsiTransportDto>>>>  map=Maps.newLinkedHashMap();
				String sql="SELECT DATE_FORMAT(etd_date,'%Y%m') yearAndMonth,LOWER(r.to_country) country,r.model,CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END ), " +
						" SUM((IFNULL(local_amount*rate1,0)+IFNULL(tran_amount*rate2,0)+IFNULL(dap_amount*rate3,0)+IFNULL(other_amount*rate4,0)+IFNULL(other_amount1*rate7,0))*(a.`quantity`*b.`gw`/( "+
						" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  " +
						" WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  " +
						" WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  " +
			            " *r.`weight`))) money, " +
						" SUM(a.`quantity`*b.`gw`/ "+
						" (CASE WHEN (b.id=217 AND  a.country_code IN ('com','uk','jp','ca','mx')) THEN 60  " +
						" WHEN (b.id=217 AND  a.country_code IN ('de','fr','it','es')) THEN 44 WHEN (b.id=218 AND  a.country_code IN ('com','jp','ca','mx')) THEN 32  " +
						" WHEN (b.id=218 AND  a.country_code IN ('de','fr','it','es','uk')) THEN 24 ELSE b.pack_quantity END)  " +
			            " ),sum(a.quantity) "+
						" FROM psi_transport_order r ,psi_transport_order_item AS a,psi_product AS b WHERE r.id=a.`transport_order_id` AND a.del_flag='0' AND b.id=a.`product_id`  and b.del_flag='0' AND  r.transport_sta!='8' "+
						"  and r.transport_type='3' AND etd_date IS NOT NULL AND  r.to_country IS NOT NULL  AND DATE_FORMAT(etd_date,'%Y%m')>=:p1 AND DATE_FORMAT(etd_date,'%Y%m')<=:p2 GROUP BY LOWER(r.to_country),DATE_FORMAT(etd_date,'%Y%m'),model,CONCAT(a.`product_name`,CASE  WHEN a.color_code='' THEN '' ELSE CONCAT('_',a.color_code) END )  ORDER BY yearAndMonth ";

				List<Object[]> list=psiTransportPaymentDao.findBySql(sql,new Parameter(new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getSureDate()),new SimpleDateFormat("yyyyMM").format(psiTransportPayment.getUpdateDate())));
				for (Object[] obj: list) {
					String yearMonth =obj[0].toString();
					Float money= ((BigDecimal)(obj[4]==null?new BigDecimal(0):obj[4])).floatValue();
					Float weight= ((BigDecimal)(obj[5]==null?new BigDecimal(0):obj[5])).floatValue();
					Integer quantity=Integer.parseInt(obj[6].toString());
					
					Map<String,Map<String,Map<String,PsiTransportDto>>> tempTotal=map.get(yearMonth);
					if(tempTotal==null){
						tempTotal=Maps.newLinkedHashMap();
						map.put(yearMonth,tempTotal);
					}
					Map<String,Map<String,PsiTransportDto>> weightTempTotal=tempTotal.get("total");
					if(weightTempTotal==null){
						weightTempTotal=Maps.newLinkedHashMap();
						tempTotal.put("total",weightTempTotal);
					}
					Map<String,PsiTransportDto> productTempToal=weightTempTotal.get(obj[3].toString());
					if(productTempToal==null){
						productTempToal=Maps.newLinkedHashMap();
						weightTempTotal.put(obj[3].toString(),productTempToal);
					}
					Float tempWeightTotal1=weight+((productTempToal.get(obj[2].toString())==null||productTempToal.get(obj[2].toString()).getWeight()==null)?0:productTempToal.get(obj[2].toString()).getWeight());
					Float tempMoneyTotal1=money+((productTempToal.get(obj[2].toString())==null||productTempToal.get(obj[2].toString()).getMoney()==null)?0:productTempToal.get(obj[2].toString()).getMoney());
					Integer tempQuantityTotal1=quantity+((productTempToal.get(obj[2].toString())==null||productTempToal.get(obj[2].toString()).getQuantity()==null)?0:productTempToal.get(obj[2].toString()).getQuantity());
					productTempToal.put(obj[2].toString(),new PsiTransportDto(tempWeightTotal1,tempMoneyTotal1,tempQuantityTotal1));
					
					Float tempWeightTotal=weight+((productTempToal.get("total")==null||productTempToal.get("total").getWeight()==null)?0:productTempToal.get("total").getWeight());
					Float tempMoneyTotal=money+((productTempToal.get("total")==null||productTempToal.get("total").getMoney()==null)?0:productTempToal.get("total").getMoney());
					Integer tempQuantityTotal=quantity+((productTempToal.get("total")==null||productTempToal.get("total").getQuantity()==null)?0:productTempToal.get("total").getQuantity());
					productTempToal.put("total",new PsiTransportDto(tempWeightTotal,tempMoneyTotal,tempQuantityTotal));
					
					
					if("jp".equals(obj[1].toString())){
						Map<String,Map<String,Map<String,PsiTransportDto>>> temp=map.get(yearMonth);
						if(temp==null){
							temp=Maps.newLinkedHashMap();
							map.put(yearMonth,temp);
						}
						Map<String,Map<String,PsiTransportDto>> weightTemp=temp.get("JP");
						if(weightTemp==null){
							weightTemp=Maps.newLinkedHashMap();
							temp.put("JP",weightTemp);
						}
						Map<String,PsiTransportDto> productTemp=weightTemp.get(obj[3].toString());
						if(productTemp==null){
							productTemp=Maps.newLinkedHashMap();
							weightTemp.put(obj[3].toString(),productTemp);
						}
						productTemp.put(obj[2].toString(),new PsiTransportDto(weight,money,quantity));
						Float tempWeight=weight+((productTemp.get("total")==null||productTemp.get("total").getWeight()==null)?0:productTemp.get("total").getWeight());
						Float tempMoney=money+((productTemp.get("total")==null||productTemp.get("total").getMoney()==null)?0:productTemp.get("total").getMoney());
						Integer tempQuantity=quantity+((productTemp.get("total")==null||productTemp.get("total").getQuantity()==null)?0:productTemp.get("total").getQuantity());
						
						productTemp.put("total",new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
					}else if("de,fr,it,es,uk".contains(obj[1].toString())){
						Map<String,Map<String,Map<String,PsiTransportDto>>> tempEU=map.get(yearMonth);
						if(tempEU==null){
							tempEU=Maps.newLinkedHashMap();
							map.put(obj[0].toString(),tempEU);
						} 
						Map<String,Map<String,PsiTransportDto>> tempCountryEu=tempEU.get("EU");
						if(tempCountryEu==null){
							tempCountryEu=Maps.newLinkedHashMap();
							tempEU.put("EU",tempCountryEu);
						}
						Map<String,PsiTransportDto> weightTempEU=tempCountryEu.get(obj[3].toString());
						if(weightTempEU==null){
							weightTempEU=Maps.newLinkedHashMap();
							tempCountryEu.put(obj[3].toString(), weightTempEU);
						} 
						Float tempWeight=weight+((weightTempEU.get(obj[2].toString())==null||weightTempEU.get(obj[2].toString()).getWeight()==null)?0:weightTempEU.get(obj[2].toString()).getWeight());
						Float tempMoney=money+((weightTempEU.get(obj[2].toString())==null||weightTempEU.get(obj[2].toString()).getMoney()==null)?0:weightTempEU.get(obj[2].toString()).getMoney());
						Integer tempQuantity=quantity+((weightTempEU.get(obj[2].toString())==null||weightTempEU.get(obj[2].toString()).getQuantity()==null)?0:weightTempEU.get(obj[2].toString()).getQuantity());
						weightTempEU.put(obj[2].toString(),new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
						
						Float tempWeight1=weight+((weightTempEU.get("total")==null||weightTempEU.get("total").getWeight()==null)?0:weightTempEU.get("total").getWeight());
						Float tempMoney1=money+((weightTempEU.get("total")==null||weightTempEU.get("total").getMoney()==null)?0:weightTempEU.get("total").getMoney());
						Integer tempQuantity1=quantity+((weightTempEU.get("total")==null||weightTempEU.get("total").getQuantity()==null)?0:weightTempEU.get("total").getQuantity());
						
						weightTempEU.put("total",new PsiTransportDto(tempWeight1,tempMoney1,tempQuantity1));
					}else if("com,ca,us,mx".contains(obj[1].toString())){
						Map<String,Map<String,Map<String,PsiTransportDto>>> tempEU=map.get(yearMonth);
						if(tempEU==null){
							tempEU=Maps.newLinkedHashMap();
							map.put(obj[0].toString(),tempEU);
						}
						Map<String,Map<String,PsiTransportDto>> tempCountryEu=tempEU.get("US");
						if(tempCountryEu==null){
							tempCountryEu=Maps.newLinkedHashMap();
							tempEU.put("US",tempCountryEu);
						}
						Map<String,PsiTransportDto> weightTempEU=tempCountryEu.get(obj[3].toString());
						if(weightTempEU==null){
							weightTempEU=Maps.newLinkedHashMap();
							tempCountryEu.put(obj[3].toString(), weightTempEU);
						}
						Float tempWeight=weight+((weightTempEU.get(obj[2].toString())==null||weightTempEU.get(obj[2].toString()).getWeight()==null)?0:weightTempEU.get(obj[2].toString()).getWeight());
						Float tempMoney=money+((weightTempEU.get(obj[2].toString())==null||weightTempEU.get(obj[2].toString()).getMoney()==null)?0:weightTempEU.get(obj[2].toString()).getMoney());
						Integer tempQuantity=quantity+((weightTempEU.get(obj[2].toString())==null||weightTempEU.get(obj[2].toString()).getQuantity()==null)?0:weightTempEU.get(obj[2].toString()).getQuantity());
						
						weightTempEU.put(obj[2].toString(),new PsiTransportDto(tempWeight,tempMoney,tempQuantity));
						
						Float tempWeight1=weight+((weightTempEU.get("total")==null||weightTempEU.get("total").getWeight()==null)?0:weightTempEU.get("total").getWeight());
						Float tempMoney1=money+((weightTempEU.get("total")==null||weightTempEU.get("total").getMoney()==null)?0:weightTempEU.get("total").getMoney());
						Integer tempQuantity1=quantity+((weightTempEU.get("total")==null||weightTempEU.get("total").getQuantity()==null)?0:weightTempEU.get("total").getQuantity());
						
						weightTempEU.put("total",new PsiTransportDto(tempWeight1,tempMoney1,tempQuantity1));
					}
				}
				return map;
			}
			
			
}
