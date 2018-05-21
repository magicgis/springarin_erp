/**
 * There are <a href="https://github.com/thinkgem/jeesite">JeeSite</a> code generation
 */
package com.springrain.erp.modules.psi.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;

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
import com.springrain.erp.modules.psi.dao.PurchasePlanDao;
import com.springrain.erp.modules.psi.dao.PurchasePlanItemDao;
import com.springrain.erp.modules.psi.entity.ProductSupplier;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductTieredPriceDto;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.psi.entity.PurchasePlan;
import com.springrain.erp.modules.psi.entity.PurchasePlanItem;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrder;
import com.springrain.erp.modules.psi.entity.lc.LcPurchaseOrderItem;
import com.springrain.erp.modules.psi.service.lc.LcPurchaseOrderService;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.service.SystemService;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 采购订单Service
 * @author Michael  
 * @version 2014-10-29
 */
@Component
@Transactional(readOnly = true)
public class PurchasePlanService extends BaseService {
	
	@Autowired
	private 	PurchasePlanDao    				purchasePlanDao;
	@Autowired
	private 	PurchasePlanItemDao  			purchasePlanItemDao;
	@Autowired
	private 	PsiSupplierService 			  	psiSupplierService;
	@Autowired
	private 	LcPurchaseOrderService        	purchaseOService;
	@Autowired
	private 	PsiProductTypeGroupDictService  typeLineService;
	@Autowired
	private 	PsiProductGroupUserService 		psiProductGroupUserService;
	@Autowired
	private 	PsiProductService	            psiProductService;
	@Autowired
	private 	PsiProductTieredPriceService 	productTieredPriceService;
	@Autowired
	private 	PsiSupplierService    			supplierService;
	@Autowired
	private 	SystemService 					systemService;
	@Autowired
	private 	MailManager              		mailManager;
	
	
	public PurchasePlan get(Integer id) {
		return purchasePlanDao.get(id);
	}
	
	
	public Page<PurchasePlan> find(Page<PurchasePlan> page, PurchasePlan purchasePlan,String isCheck,String productIdColor) {
		DetachedCriteria dc = purchasePlanDao.createDetachedCriteria();
		dc.createAlias("this.items", "item");
		
		if(StringUtils.isNotEmpty(productIdColor)){
			 String[]  arr=productIdColor.split("_");
		     String productName =  arr[0];
		     String colorCode	=  "";
		     if(arr.length>1){
		    	 colorCode	=  arr[1];
		     }
		    
			dc.add(Restrictions.eq("item.productName",productName));   
			dc.add(Restrictions.eq("item.colorCode",colorCode));
		}
		
		if (purchasePlan.getCreateDate()!=null){
			dc.add(Restrictions.ge("createDate",purchasePlan.getCreateDate()));
		}
		
		if(purchasePlan.getReviewDate()!=null){
			dc.add(Restrictions.le("createDate",DateUtils.addDays(purchasePlan.getReviewDate(),1)));
		}
		
		if("1".equals(isCheck)){
			dc.add(Restrictions.eq("createUser.id",UserUtils.getUser().getId()));
		}
		
		if(StringUtils.isNotEmpty(purchasePlan.getPlanSta())){
			dc.add(Restrictions.eq("planSta", purchasePlan.getPlanSta()));
		}else{
			dc.add(Restrictions.ne("planSta","8"));
		}
		page.setOrderBy("id desc");
		
		return purchasePlanDao.find2(page, dc);
	}	
	
	
	
	/**
	 *获取带颜色的产品
	 * 
	 */
	public Map<String,String> getAllProductColors(){
		Map<String,String>  rs = Maps.newHashMap();
		String sql="SELECT DISTINCT CONCAT(a.`product_name`,CASE  WHEN a.`product_color`='' THEN '' ELSE CONCAT('_',a.`product_color`) END) AS productName,a.`psi_product`,a.`product_color` FROM psi_barcode a WHERE a.`del_flag`='0'";
		List<Object[]> objs=this.purchasePlanDao.findBySql(sql);
		if(objs!=null&&objs.size()>0){
			for(Object[] obj:objs){
				String proColor=obj[0].toString();
				rs.put(proColor, obj[1]+","+obj[2]);
			}
		}
		return rs;
	}
	
	@Transactional(readOnly=false)
	public void save(PurchasePlan plan){
		this.purchasePlanDao.save(plan);
	}
	
	@Transactional(readOnly=false)
	public void reviewSave(PurchasePlan purchasePlan) throws IOException{
		if(purchasePlan.getId()!=null){
			purchasePlan.setReviewDate(new Date());
			purchasePlan.setReviewUser(UserUtils.getUser());
			purchasePlan.setPlanSta("3");
			this.purchasePlanDao.save(purchasePlan);
			
			String productName = "";
			Integer quantity = 0;
			for(PurchasePlanItem item:purchasePlan.getItems()){
				if(StringUtils.isEmpty(productName)){
					productName= item.getProductName();
				}
				quantity+=item.getQuantityReview();
			}
			
			List<User> userList = systemService.findUserByPermission("psi:purchasePlan:bossReview");
			List<User> replys = Lists.newArrayList();
			if(userList!=null){
				replys.addAll(userList);
			}
			replys.add(UserUtils.getUser());
			String toAddress = Collections3.extractToString(replys,"email", ",");
			String content = "(<span style='color:red;'><b>"+productName+"</b></span>,总数量："+quantity+")新品采购申请已初级审核,请尽快登陆erp系统进行终极审批,<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/purchasePlan/bossReview?id="+purchasePlan.getId()+"'>点击审核</a>";
			if(StringUtils.isNotBlank(content)){
				Date date = new Date();
				final MailInfo mailInfo = new MailInfo(toAddress,"新品采购申请待终极审批["+productName+"]"+DateUtils.getDate("-yyyy/M/dd"),date);
				mailInfo.setContent(content);
				//发送成功不成功都能保存
				new Thread(){
					@Override
					public void run(){
						mailManager.send(mailInfo);
					}
				}.start();
			}
		}
	}
	
	@Transactional(readOnly=false)
	public void bossReviewSave(PurchasePlan purchasePlan) throws IOException{
		if(purchasePlan.getId()!=null){
			//产品名称-产品线ID
			Map<String,String> nameAndLineIdMap=typeLineService.getLineByName();
			//产品线-国家-人id+","+name
			Map<String,Map<String,String>> saleUserMap=psiProductGroupUserService.getSingleGroupUser();
			
			purchasePlan.setBossReviewDate(new Date());
			purchasePlan.setBossReviewUser(UserUtils.getUser());
			purchasePlan.setPlanSta("4");
			String productName = "";
			Set<Integer> productIds = Sets.newHashSet();
			for(PurchasePlanItem item :purchasePlan.getItems()){
				if(item.getQuantity().intValue()>0){
					item.setCreateSta("0");//可生成订单
				}
				if(StringUtils.isEmpty(productName)){
					productName= item.getProductName();
					productIds.add(item.getProduct().getId());
				}
			}
			this.purchasePlanDao.save(purchasePlan);
			User createUser =UserUtils.getUserById(purchasePlan.getCreateUser().getId());//查出创建人
			User reviewUser =UserUtils.getUserById(purchasePlan.getReviewUser().getId());//查出审核人
			String toAddress=UserUtils.getUser().getEmail()+","+reviewUser.getEmail()+","+createUser.getEmail();
			StringBuffer content= new StringBuffer("");
			content.append("Hi,All<br/>(<span style='color:red;'><b>"+productName+"</b></span>)新品采购申请已审核：<a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/purchasePlan/view?id="+purchasePlan.getId()+"'>点击查看</a>,请知悉,明细如下：<br/>");
			//整理邮件表格
			
			content.append("<table width='90%' style='border-right:1px solid;border-bottom:1px solid;color:#666;width:100%' cellpadding='0' cellspacing='0' >");
			content.append("<tr style='background-repeat:repeat-x;height:30px; background-color:#B2B2B2;color:#666;'>");
			content.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:20%'>产品名</th>");
			content.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:5%'>国家</th>");
			content.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:5%'>申请数量</th>");
			content.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:8%'>初审数量</th>");
			content.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:8%'>确认数量</th>");
			content.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:8%'>备注</th>");
			content.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:15%'>初审备注</th>");
			content.append("<th style='border-left:1px solid;border-top:1px solid;color:#666;width:15%'>确认备注</th>");
			content.append("</tr>");
			Set<String> emailSet = Sets.newHashSet();
			Set<String> productManagerEmails = this.psiProductService.findMangerByProductIds(productIds);
			if(productManagerEmails!=null&&productManagerEmails.size()>0){
				emailSet.addAll(productManagerEmails);
			}
			//根据产品获取销售人员邮箱和跟单邮箱，，，
			for(PurchasePlanItem item:purchasePlan.getItems()){
				String name = item.getProductNameColor();
				String lineId=nameAndLineIdMap.get(name);
				Integer productId = item.getProduct().getId();
				PsiProduct product = this.psiProductService.get(productId);
				if(product!=null&&product.getCreateUser()!=null&&StringUtils.isNotEmpty(product.getCreateUser().getEmail())){//查出跟单员
					emailSet.add(product.getCreateUser().getEmail());
				}
				if(StringUtils.isNotBlank(lineId)&&saleUserMap!=null&&saleUserMap.get(lineId)!=null&&saleUserMap.get(lineId).get(item.getCountryCode())!=null){
					String userId =saleUserMap.get(lineId).get(item.getCountryCode()).split(",")[0];
					User saleUser =UserUtils.getUserById(userId);
					if(saleUser!=null&&StringUtils.isNotEmpty(saleUser.getEmail())){
						emailSet.add(saleUser.getEmail());
					}
				}
				
				Integer quantity=item.getQuantity();
				Integer reviewQuantity = item.getQuantityReview();
				Integer bossQuantity = item.getQuantityBossReview();
				
				//判断有变动没有，有变动就标红
				String color="#666";
				if(!bossQuantity.equals(quantity)){
					color="red";
				}
				content.append("<tr style='background-repeat:repeat-x;text-align:center;height:30px; border-left:1px solid;border-top:1px solid;color:"+color+"; '>");
				content.append("<td style='border-left:1px solid;border-top:1px solid;color:"+color+";'>"+item.getProductNameColor()+"</td>");
				content.append("<td style='border-left:1px solid;border-top:1px solid;color:"+color+";'>"+(("com".equals(item.getCountryCode())?"us":item.getCountryCode()).toUpperCase())+"</td>");
				content.append("<td style='border-left:1px solid;border-top:1px solid;color:"+color+";'>"+quantity+"</td>");
				content.append("<td style='border-left:1px solid;border-top:1px solid;color:"+color+";'>"+reviewQuantity+"</td>");
				content.append("<td style='border-left:1px solid;border-top:1px solid;color:"+color+";'>"+bossQuantity+"</td>");
				content.append("<td style='border-left:1px solid;border-top:1px solid;color:"+color+";'>"+(StringUtils.isEmpty(item.getRemark())?"":item.getRemark())+"</td>");
				content.append("<td style='border-left:1px solid;border-top:1px solid;color:"+color+";'>"+(StringUtils.isEmpty(item.getRemarkReview())?"":item.getRemarkReview())+"</td>");
				content.append("<td style='border-left:1px solid;border-top:1px solid;color:"+color+";'>"+(StringUtils.isEmpty(item.getRemarkBossReview())?"":item.getRemarkBossReview())+"</td>");
				content.append("</tr>"); 
			}
			
			for(String email:emailSet){
				toAddress=toAddress+","+email;
			}
			 content.append("</table><br/>");
			
			if(StringUtils.isNotBlank(content)){
				Date date = new Date();
				final MailInfo mailInfo = new MailInfo(toAddress+",sand@inateck.com,emma.chao@inateck.com","新品采购申请已终极审批["+productName+"]"+DateUtils.getDate("-yyyy/M/dd"),date);
				mailInfo.setContent(content.toString());
				//发送成功不成功都能保存
				new Thread(){
					@Override
					public void run(){
						mailManager.send(mailInfo);
					}
				}.start();
			}
		}
	}
	
	
	@Transactional(readOnly=false)
	public void editSave(PurchasePlan purchasePlan) throws IOException{
		String	filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/purchasePlan";
		if(purchasePlan.getAttFile()!=null&&purchasePlan.getAttFile().getSize()>0){
			String suffix = purchasePlan.getAttFile().getOriginalFilename().substring(purchasePlan.getAttFile().getOriginalFilename().lastIndexOf("."));  
			String uuid = UUID.randomUUID().toString();
			File file1 = new File(filePath);
			if (!file1.exists()) {
				file1.mkdirs();
			}
			File file = new File(file1, uuid+suffix);
			FileUtils.copyInputStreamToFile(purchasePlan.getAttFile().getInputStream(),file);
			purchasePlan.setAttFilePath(Global.getCkBaseDir() + "psi/purchasePlan/"+uuid+suffix);
		}
		if(purchasePlan.getId()!=null){
			//编辑   
			Set<Integer>  delItemSet = Sets.newHashSet();
			Set<String> setNewIds = new HashSet<String>();
			String oldItemIds=purchasePlan.getOldItemIds();
			String [] oldIds = oldItemIds.split(",");
			for(PurchasePlanItem item : purchasePlan.getItems()){
				//如果是申请状态并且数量为0
				if("2".equals(purchasePlan.getPlanSta())){
					if(item.getQuantity()==null||item.getQuantity().intValue()==0){
						item.setDelFlag("1");
					}
				}
				item.setPlan(purchasePlan);
				if(item.getId()!=null&&!"".equals(item.getId())){
					setNewIds.add(item.getId().toString());
				}else{
					//如果id为空     说明是新增的
					item.setDelFlag("0");
				}
			}
			
			if(setNewIds!=null&&setNewIds.size()>0){
				for(int j=0;j<oldIds.length;j++){
					if(!setNewIds.contains(oldIds[j])){
						//不包含就干掉
						delItemSet.add(Integer.valueOf(oldIds[j]));
					};
				}
			}else{
				//说明原来的都删除了
				for(int j=0;j<oldIds.length;j++){
					delItemSet.add(Integer.valueOf(oldIds[j]));
				}
			}
			if(delItemSet.size()>0){
				for(PurchasePlanItem item:this.getPurchasePlanItems(delItemSet)){
					item.setDelFlag("1");
					item.setPlan(purchasePlan);
					purchasePlan.getItems().add(item);
				}
			}
			purchasePlan.setCreateUser(UserUtils.getUser());
			purchasePlanDao.getSession().merge(purchasePlan);
			//如果有申请审核状态，发信通知审核人
			if("2".equals(purchasePlan.getPlanSta())){
				//初始化产品定位标签
				Set<String> productNames = Sets.newHashSet();
				for(PurchasePlanItem item :purchasePlan.getItems()){
					if (StringUtils.isNotEmpty(item.getColorCode())) {
						productNames.add(item.getProductName() + "_" + item.getColorCode());
					} else {
						productNames.add(item.getProductName());
					}
				}
				updateProductPosition(productNames, purchasePlan.getProductPosition());
				this.toReviewEmail(purchasePlan);
			}
		}else{
			//新增
			for(PurchasePlanItem item : purchasePlan.getItems()){
				item.setPlan(purchasePlan);
			}
			purchasePlan.setCreateDate(new Date());
			purchasePlan.setCreateUser(UserUtils.getUser());
			purchasePlan.setPlanSta("1");
			this.purchasePlanDao.save(purchasePlan);
		}
	}
	
	public void toReviewEmail(PurchasePlan purchasePlan){
		List<User> userList = systemService.findUserByPermission("psi:purchasePlan:review");
		List<User> replys = Lists.newArrayList();
		if(userList!=null){
			replys.addAll(userList);
		}
		replys.add(UserUtils.getUser());
		String toAddress = Collections3.extractToString(replys,"email", ",");
		String productName = "";
		Integer quantity = 0;
		for(PurchasePlanItem item:purchasePlan.getItems()){
			if(StringUtils.isEmpty(productName)){
				productName= item.getProductName();
			}
			quantity+=item.getQuantity();
		}
		String content = "(<span style='color:red;'><b>"+productName+"</b></span>,总数量："+quantity+"),新品采购申请已创建,请尽快登陆erp系统审批.   <a href='"+BaseService.BASE_WEBPATH+Global.getAdminPath()+"/psi/purchasePlan/review?id="+purchasePlan.getId()+"'>点击审核</a>";
		if(StringUtils.isNotBlank(content)){
			Date date = new Date();
			final MailInfo mailInfo = new MailInfo(toAddress,"新品采购申请已创建待审批["+productName+"]"+DateUtils.getDate("-yyyy/M/dd"),date);
			mailInfo.setContent(content);
			//发送成功不成功都能保存
			new Thread(){
				@Override
				public void run(){
					mailManager.send(mailInfo);
				}
			}.start();
		}
	}
	
	/**
	 *根据itemId获取list信息 
	 */
	public List<PurchasePlanItem> getPurchasePlanItems(Set<Integer> ids){
		DetachedCriteria dc = this.purchasePlanItemDao.createDetachedCriteria();
		dc.add(Restrictions.in("id", ids));
		return purchasePlanItemDao.find(dc);
	}
	
	@Transactional(readOnly=false)
	public String createOrder(String planItemId,String planId) throws ParseException{
		String itemArgs[] =planItemId.split(",");
		Set<Integer> itemIds = Sets.newHashSet();
		for(String itemId :itemArgs){
			itemIds.add(Integer.parseInt(itemId));
		}
		List<PurchasePlanItem> planItems = this.getPurchasePlanItems(itemIds);
		
		Map<Integer,List<PurchasePlanItem>> forecastMap = Maps.newHashMap();
		Map<Integer,Map<Integer,Integer>> supplierProductMap = Maps.newHashMap(); //供应商，产品，数量    
		Integer resI=0;
		List<PsiSupplier> suppliers=supplierService.findAll();
		Map<Integer,PsiSupplier> supMap = Maps.newHashMap();
		for(PsiSupplier sup:suppliers){
			supMap.put(sup.getId(), sup);
		}
		for(PurchasePlanItem item:planItems){
			if("1".equals(item.getCreateSta())||item.getQuantityBossReview().intValue()==0){
				continue;
			}
			PsiProduct  product = item.getProduct();
			Integer productId = product.getId();
			List<ProductSupplier> productSuppliers = product.getPsiSuppliers();
			if(productSuppliers==null||productSuppliers.size()==0){
				continue;
			}
			Integer supplierId = productSuppliers.get(0).getSupplier().getId();
			//生成供应商产品数量map
			Map<Integer,Integer> productQuantityMap =null;
			if(supplierProductMap.get(supplierId)==null){
				productQuantityMap = Maps.newHashMap();
			}else{
				productQuantityMap = supplierProductMap.get(supplierId);
			}
			
			Integer productQuantity = item.getQuantityBossReview();
			if(productQuantityMap.get(productId)!=null){
				productQuantity+=productQuantityMap.get(productId);
			}
			productQuantityMap.put(productId, productQuantity);
			supplierProductMap.put(supplierId, productQuantityMap);
			List<PurchasePlanItem> tempList = null;
			if(forecastMap.get(supplierId)==null){
				tempList = Lists.newArrayList();
			}else{
				tempList = forecastMap.get(supplierId);
			}
			tempList.add(item);
			forecastMap.put(supplierId, tempList);
			//更新生成状态
			item.setCreateSta("1");
			this.purchasePlanItemDao.save(item);
		}
		resI=forecastMap.size();
		Map<Integer,String> receivedMap =this.psiProductService.getAllReceivedDate(new Date());  
		if(forecastMap.size()>0){
			Map<Integer,Map<String,Set<Integer>>> followMap = getFollowMap(forecastMap.keySet());
			//根据供应商生成采购订单
			 List<LcPurchaseOrder>  purchaseOrders = Lists.newArrayList();
			for(Map.Entry<Integer,List<PurchasePlanItem>> supplierIdEntry:forecastMap.entrySet()){
				Integer supplierId = supplierIdEntry.getKey();
				PsiSupplier supplier = supMap.get(supplierId);
				 //查询每个供应商产品的价格
				 Map<String,PsiProductTieredPriceDto> dtoMap = productTieredPriceService.findPrices(supplierProductMap.get(supplierId).keySet(), supplierId, supplier.getCurrencyType());
				 //查询每个供应商的跟单员信息
				Map<String,Set<Integer>> followProductMap = followMap.get(supplierId);
				//生成采购数据
				purchaseOrders.addAll(this.createPurchaseOrder(supplier, followProductMap, forecastMap.get(supplierId),receivedMap,dtoMap,supplierProductMap.get(supplierId)));
			}
			this.purchaseOService.saveAll(purchaseOrders);
		}
		return "生成"+resI+"个订单";
	}
	
	/**
	 *获取跟单员信息       供应商：用户：产品
	 */
	public Map<Integer,Map<String,Set<Integer>>> getFollowMap(Set<Integer> supplierIds){
		Map<Integer,Map<String,Set<Integer>>> resMap = Maps.newHashMap();
		String sql="SELECT DISTINCT c.`supplier_id`,a.`create_user`,a.id FROM psi_product AS a,sys_user AS b,psi_product_supplier AS c WHERE a.id=c.`product_id`AND  a.`create_user`=b.`id` AND a.`del_flag`='0' AND b.`del_flag`='0' AND c.`supplier_id` IN :p1";
		List<Object[]>  list=  this.purchasePlanDao.findBySql(sql, new Parameter(supplierIds));
		for(Object[] obj:list){
			Integer supplierId = (Integer)obj[0];
			String userId = (String)obj[1];
			Integer productId = (Integer)obj[2];
			Map<String,Set<Integer>> followUserMap=null;
			if(resMap.get(supplierId)==null){
				followUserMap=Maps.newHashMap();
			}else{
				followUserMap=resMap.get(supplierId);
			}
			Set<Integer> productIds = null;
			if(followUserMap.get(userId)==null){
				productIds=Sets.newHashSet();
			}else{
				productIds=followUserMap.get(userId);
			}
			productIds.add(productId);
			followUserMap.put(userId, productIds);
			resMap.put(supplierId, followUserMap);
		}
		return resMap;
	}
	
	/**
	 *获取跟单员信息 
	 */
	public Map<Integer,String> getSupplierMap(){
		Map<Integer,String> resMap = Maps.newHashMap();
		String sql="SELECT b.`product_id`,a.id,a.`nikename` FROM psi_supplier AS a,psi_product_supplier AS b WHERE a.`id`=b.`supplier_id`";
		List<Object[]>  list=  this.purchasePlanDao.findBySql(sql);
		for(Object[] obj:list){
			Integer productId = (Integer)obj[0];
			String supplierInfo = obj[1]+","+obj[2];
			resMap.put(productId, supplierInfo);
		}
		return resMap;
	}
	
	@Transactional(readOnly=false)
	public void updatePlanSta(String planIds){
		List<Integer> ids = Lists.newArrayList();
		for(String planId:planIds.split(",")){
			ids.add(Integer.parseInt(planId));
		}
		String sql="SELECT a.`id`,COUNT(*),SUM(CONVERT(b.create_sta,SIGNED)) FROM psi_purchase_plan AS a,psi_purchase_plan_item AS b WHERE a.id=b.`plan_id` AND b.`del_flag`='0' AND a.`plan_sta`<>'8' AND b.`create_sta` IS NOT NULL AND a.id in :p1 GROUP BY a.`id` ";
		List<Object[]>  list=  this.purchasePlanDao.findBySql(sql,new Parameter(ids));
		for(Object[] obj:list){
			Integer planId = (Integer)obj[0];
			Integer o1 = ((BigInteger)obj[1]).intValue();
			Integer o2 = ((BigDecimal)obj[2]).intValue();
			PurchasePlan plan= this.get(planId);
			if(o1==o2){//全生成
				plan.setPlanSta("6");
			}else{//部分生成
				plan.setPlanSta("5");
			}
			this.save(plan);
		}
		
	}
	
	
	/**
	 * 
	 *根据新品采购计划，自动生成采购订单 
	 *不同供应商、不同采购跟单的产品分成不同采购订单
	 */
	public List<LcPurchaseOrder> createPurchaseOrder(PsiSupplier supplier,Map<String,Set<Integer>> followProductMap,List<PurchasePlanItem> planItems,
			Map<Integer,String> receivedMap, Map<String,PsiProductTieredPriceDto> dtoMap,Map<Integer,Integer> productQuantityMap) throws ParseException {
		SimpleDateFormat  sdf = new SimpleDateFormat("yyyy-MM-dd");
		Integer tax=supplier.getTaxRate();
		Float  taxRate= (tax+100)/100f;
		//获取产品价格 start
		Map<String,BigDecimal> productPrices = Maps.newHashMap();
		for(Map.Entry<String,PsiProductTieredPriceDto> entry:dtoMap.entrySet()){
			String key = entry.getKey();
			String [] arr=key.split(",");
			String productIdStr=arr[0];
			String color =arr[1];
			PsiProductTieredPriceDto dto = dtoMap.get(key);
			Integer orderQuantity = productQuantityMap.get(Integer.parseInt(productIdStr));
			Float price = null;
			String proColorKey = productIdStr+"_"+color;
			if(productPrices.get(proColorKey)==null){
				if("USD".equals(supplier.getCurrencyType())){
					if(orderQuantity<1000){ 		//小于1000用500的价
						price=dto.getLeval500usd(); 
					}else if(orderQuantity<2000){   //小于2000用1000的价
						price=dto.getLeval1000usd();
					}else if(orderQuantity<3000){   //小于3000用2000的价
						price=dto.getLeval2000usd();
					}else if(orderQuantity<5000){   //小于5000用3000的价
						price=dto.getLeval3000usd();
					}else if(orderQuantity<10000){  //小于10000用5000的价
						price=dto.getLeval5000usd();
					}else if(orderQuantity<15000){  //小于15000用10000的价
						price=dto.getLeval10000usd();
					}else{                          //大于15000用15000的价
						price=dto.getLeval15000usd();
					}
				}else{
					if(orderQuantity<1000){ 		//小于1000用500的价
						price=dto.getLeval500cny(); 
					}else if(orderQuantity<2000){   //小于2000用1000的价
						price=dto.getLeval1000cny();
					}else if(orderQuantity<3000){   //小于3000用2000的价
						price=dto.getLeval2000cny();
					}else if(orderQuantity<5000){   //小于5000用3000的价
						price=dto.getLeval3000cny();
					}else if(orderQuantity<10000){  //小于10000用5000的价
						price=dto.getLeval5000cny();
					}else if(orderQuantity<15000){  //小于15000用10000的价
						price=dto.getLeval10000cny();
					}else{                          //大于15000用15000的价
						price=dto.getLeval15000cny();
					}
				}
				if(price==null){
					productPrices.put(proColorKey, null);
				}else{
					productPrices.put(proColorKey, new BigDecimal(price+"").multiply(new BigDecimal(taxRate+"")).setScale(2, BigDecimal.ROUND_HALF_UP));
				}
			}
		}
		//获取产品价格 end
		
		//产品名称-产品线ID
		Map<String,String> nameAndLineIdMap=typeLineService.getLineByName();
		//产品线-国家-人id+","+name
		Map<String,Map<String,String>> saleUserMap=psiProductGroupUserService.getSingleGroupUser();
		
		List<LcPurchaseOrder> purchaseOrders = Lists.newArrayList();
		//根据跟单和供应商不同，分成多个订单
		for(Map.Entry<String,Set<Integer>> userEntry:followProductMap.entrySet()){
			String userId =userEntry.getKey();
			LcPurchaseOrder  purchaseOrder = new LcPurchaseOrder();
			purchaseOrder.setSupplier(supplier);
			List<LcPurchaseOrderItem> itemList = new ArrayList<LcPurchaseOrderItem>();
			String shortName  = supplier.getNikename();
			BigDecimal  totalAmount =BigDecimal.ZERO;
			for(PurchasePlanItem planItem :planItems){
				if(!followProductMap.get(userId).contains(planItem.getProduct().getId())){
					continue;
				}
				LcPurchaseOrderItem orderItem = new LcPurchaseOrderItem();
				String name=planItem.getProductNameColor();
				if(StringUtils.isNotBlank(name)){
					String lineId=nameAndLineIdMap.get(name);
					if(StringUtils.isNotBlank(lineId)&&saleUserMap!=null&&saleUserMap.get(lineId)!=null&&saleUserMap.get(lineId).get(planItem.getCountryCode())!=null){
						orderItem.setSalesUser(saleUserMap.get(lineId).get(planItem.getCountryCode()).split(",")[1]);
					}
				}
				orderItem.setProduct(planItem.getProduct());
				orderItem.setColorCode(planItem.getColorCode());
				orderItem.setCountryCode(planItem.getCountryCode());
				orderItem.setQuantityOrdered(planItem.getQuantityBossReview());
				orderItem.setQuantityPreReceived(0);   //预收货数量为0
				orderItem.setQuantityReceived(0);      //已收货数量为0
				//线下数量
				orderItem.setQuantityOffPreReceived(0);   //线下预收货数量为0
				orderItem.setQuantityOffReceived(0);      //线下已收货数量为0
				orderItem.setQuantityOffOrdered(0);       //线下订单数为0
				
				orderItem.setQuantityPayment(0);       //已付款数量为0
				orderItem.setPaymentAmount(BigDecimal.ZERO);        //已支付金额    0
				orderItem.setProductName(planItem.getProductName());
				orderItem.setDeliveryDate(sdf.parse(receivedMap.get(orderItem.getProduct().getId())));
				orderItem.setActualDeliveryDate(orderItem.getDeliveryDate());
				orderItem.setPurchaseOrder(purchaseOrder);  
				String productColor = planItem.getProduct().getId()+"_"+planItem.getColorCode();
				orderItem.setItemPrice(productPrices.get(productColor));
				if(orderItem.getItemPrice()!=null){
					totalAmount=totalAmount.add(new BigDecimal(orderItem.getQuantityOrdered()).multiply(orderItem.getItemPrice()));
				}
				itemList.add(orderItem);
			}
			if(itemList.size()>0){
				User user = UserUtils.getUserById(userId);
				Date curDate = new Date();
				String orderNo = this.purchaseOService.createSequenceNumber(shortName+"_LC");
				purchaseOrder.setOrderNo(orderNo);
				purchaseOrder.setIsOverInventory("0");//这种拆分的应该不超标吧？
				purchaseOrder.setPurchaseDate(curDate);
				purchaseOrder.setCreateDate(curDate);
				purchaseOrder.setCreateUser(user);
				purchaseOrder.setUpdateDate(curDate);
				purchaseOrder.setUpdateUser(user);
				purchaseOrder.setTotalAmount(totalAmount);    //订单总金额
				purchaseOrder.setDepositAmount(BigDecimal.ZERO);           //已支付定金金额 0f
				purchaseOrder.setDepositPreAmount(BigDecimal.ZERO);        //已申请定金金额0f
				purchaseOrder.setOrderSta("0");  		      //草稿状态
				purchaseOrder.setDelFlag("0");                //删除状态
				purchaseOrder.setPaySta("0");                 //是否付款
				purchaseOrder.setPaymentAmount(BigDecimal.ZERO);           //支付尾款金额0f
				purchaseOrder.setCurrencyType(supplier.getCurrencyType());
				purchaseOrder.setDeposit(supplier.getDeposit());
				purchaseOrder.setMerchandiser(user);
				purchaseOrder.setItems(itemList);
				purchaseOrders.add(purchaseOrder);
			}
			
		}
	    return purchaseOrders;
	}


    public List<PurchasePlanItem> getProductPositionByProductName(String productName) {
        // TODO Auto-generated method stub
        List<PurchasePlanItem> item = purchasePlanDao.find("from PurchasePlanItem where productName=:p1",new Parameter(productName.contains("_")?productName.substring(0, productName.indexOf("_")):productName));
        return item;
    }
	
    @Transactional(readOnly=false)
    public void updateProductPosition(Set<String> productNames, String productPosition){
    	String sql = "UPDATE `psi_product_eliminate` t SET t.`is_sale`=:p1 WHERE t.`product_name`=:p2 AND t.`color`=:p3";
    	for (String productName : productNames) {
			String name = productName;
			String color = "";
			if (productName.split("_").length == 2) {
				name = productName.split("_")[0];
				color = productName.split("_")[1];
			}
			purchasePlanDao.updateBySql(sql, new Parameter(productPosition, name, color));
		}
    	
    }
	
}
