package com.springrain.erp.modules.psi.service;

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
import com.google.common.collect.Sets;
import com.springrain.erp.common.config.Global;
import com.springrain.erp.common.persistence.Page;
import com.springrain.erp.common.persistence.Parameter;
import com.springrain.erp.common.service.BaseService;
import com.springrain.erp.common.utils.FileUtils;
import com.springrain.erp.common.utils.MathUtils;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.dao.PsiProductTieredPriceDao;
import com.springrain.erp.modules.psi.dao.PsiProductTieredPriceLogDao;
import com.springrain.erp.modules.psi.dao.PsiProductTieredPriceReviewDao;
import com.springrain.erp.modules.psi.entity.PsiProduct;
import com.springrain.erp.modules.psi.entity.PsiProductTieredPrice;
import com.springrain.erp.modules.psi.entity.PsiProductTieredPriceDto;
import com.springrain.erp.modules.psi.entity.PsiProductTieredPriceLog;
import com.springrain.erp.modules.psi.entity.PsiSupplier;
import com.springrain.erp.modules.sys.entity.Role;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 *	供应商产品Service
 */
@Component
@Transactional(readOnly = true)
public class PsiProductTieredPriceService extends BaseService {
	@Autowired
	private PsiProductTieredPriceDao psiProductTieredPriceDao;
	@Autowired
	private PsiProductTieredPriceLogDao psiProductTieredPriceLogDao;
	@Autowired
	private PsiProductTieredPriceReviewDao priceReviewDao;
	@Autowired
	private PsiSupplierService psiSupplierService;
	
	
	public List<PsiProductTieredPrice> find() {
		DetachedCriteria dc = psiProductTieredPriceDao.createDetachedCriteria();
		dc.addOrder(Order.desc("id"));
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiProductTieredPriceDao.find(dc);
	}  
	
	public PsiProductTieredPriceDto findDto(Integer dtoId) {
		PsiProductTieredPriceDto dto = priceReviewDao.get(dtoId);
		dto.setTaxRate(psiSupplierService.get(dto.getSupplierId()).getTaxRate());
		return dto;
	}  
	
	public Map<String,PsiProductTieredPriceDto> findPrices(PsiProductTieredPrice tieredPrice) {
		DetachedCriteria dc = psiProductTieredPriceDao.createDetachedCriteria();
		dc.addOrder(Order.desc("id"));
		dc.add(Restrictions.eq("delFlag", "0"));
		if(tieredPrice.getSupplier()!=null&&tieredPrice.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier.id", tieredPrice.getSupplier().getId()));
		}
		
		if(tieredPrice.getProduct()!=null&&tieredPrice.getProduct().getId()!=null){
			dc.add(Restrictions.eq("product.id", tieredPrice.getProduct().getId()));
		}
		
		//对跟单和采购经理     进行权限控制
		String roleStr = UserUtils.getUser().getRoleNames()+",";
		String userId = UserUtils.getUser().getId();
		List<Integer> productIds=Lists.newArrayList();
		
		//首次进来
		Set<String> permissionsSet = Sets.newHashSet();
		//查询权限
		User user = UserUtils.getUser();
		if(!user.isAdmin()){
			for(Role role:UserUtils.getUser().getRoleList()){
				permissionsSet.addAll(role.getPermissions());
			}
			if(!permissionsSet.contains("psi:product:reviewPrice")){//如果不包含价格审批权限，过滤
				if(UserUtils.hasPermission("psi:purchase:manager")){//采购经理
					//如果是采购经理
					productIds=getProductIdsByPurchaseUserId(userId);
					if (productIds != null && productIds.size()>0) {
						dc.add(Restrictions.in("product.id", productIds));
					}
				}else if(UserUtils.hasPermission("psi:order:edit")){
					//如果是跟单员
					productIds = this.psiSupplierService.getProductIdsByFollowId(userId);
					if (productIds != null && productIds.size()>0) {
						dc.add(Restrictions.in("product.id", productIds));
					}
				}else if(UserUtils.hasPermission("psi:product:manager")){
					//如果是产品经理
					productIds= this.getProductIdsByManagerUserId(userId);
					if (productIds != null && productIds.size()>0) {
						dc.add(Restrictions.in("product.id", productIds));
					}
				}
			}
		}
		List<PsiProductTieredPrice> list=psiProductTieredPriceDao.find(dc);
		Map<String,PsiProductTieredPriceDto>  map = Maps.newHashMap();
		for(PsiProductTieredPrice price:list){
			String key =price.getProduct().getId()+","+price.getColor()+","+price.getSupplier().getId();
			PsiProductTieredPriceDto priceDto = null;
			if(map.get(key)==null){
				priceDto=new PsiProductTieredPriceDto();
				priceDto.setProductId(price.getProduct().getId());
				priceDto.setMoq(price.getProduct().getMinOrderPlaced());
				priceDto.setSupplierId(price.getSupplier().getId());
				priceDto.setProNameColor(price.getName());
				priceDto.setColor(price.getColor());
				priceDto.setNikeName(price.getSupplier().getNikename());
				priceDto.setTaxRate(price.getSupplier().getTaxRate());
			}else{
				priceDto=map.get(key);
			}
			this.createDtoFromTiered(priceDto, price);
			map.put(key, priceDto);
		}
		
		return map;
	} 
	
	
	
	public Page<PsiProductTieredPriceDto> findDtos(Page<PsiProductTieredPriceDto> page,PsiProductTieredPriceDto tieredPriceDto) {
		DetachedCriteria dc = priceReviewDao.createDetachedCriteria();
		page.setOrderBy("id desc");
		if(tieredPriceDto.getSupplierId()!=null&&tieredPriceDto.getSupplierId()!=null){
			dc.add(Restrictions.eq("supplierId", tieredPriceDto.getSupplierId()));
		}
		
		if(tieredPriceDto.getProductId()!=null&&tieredPriceDto.getProductId()!=null){
			dc.add(Restrictions.eq("productId", tieredPriceDto.getProductId()));
		}
		
		//对跟单和采购经理     进行权限控制
		//首次进来
			Set<String> permissionsSet = Sets.newHashSet();
			//查询权限
			User user = UserUtils.getUser();
			if(!user.isAdmin()){
				for(Role role:UserUtils.getUser().getRoleList()){
					permissionsSet.addAll(role.getPermissions());
				}
				if(!permissionsSet.contains("psi:product:reviewPrice")){//如果不包含价格审批权限，过滤
					String roleStr = UserUtils.getUser().getRoleNames()+",";
					String userId = UserUtils.getUser().getId();
					List<Integer> supplierIds=Lists.newArrayList();
					if(roleStr.contains("采购经理,")){
						//如果是采购经理
						List<String> types = this.psiSupplierService.getSupplierByPurchaseUserId(userId);
						if(types==null||types.size()==0){
							supplierIds.add(0);
						}else{
							supplierIds=this.psiSupplierService.getSupplierByType(types);
						}
						dc.add(Restrictions.in("supplierId", supplierIds));
//					}else if(roleStr.contains("跟单员,")){
					}else if(UserUtils.hasPermission("psi:order:edit")){
						//如果是跟单员
						supplierIds = this.psiSupplierService.getProductIdsByFollowId(userId);
						if(supplierIds==null||supplierIds.size()==0){
							supplierIds=Lists.newArrayList();
							supplierIds.add(0);
						}
						dc.add(Restrictions.in("productId", supplierIds));
					}
				}
			}
		return priceReviewDao.find(page,dc);
	} 
	
	

	public Map<String,PsiProductTieredPriceDto> findPrices(Set<Integer> productIds,Integer supplierId,String currency) {
		DetachedCriteria dc = psiProductTieredPriceDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		if(supplierId!=null){
			dc.add(Restrictions.eq("supplier.id", supplierId));
		}
		
		if(productIds!=null){
			dc.add(Restrictions.in("product.id", productIds));
		}
		
		if(currency!=null){
			dc.add(Restrictions.eq("currencyType", currency));
		}
		
		List<PsiProductTieredPrice> list=psiProductTieredPriceDao.find(dc);
		Map<String,PsiProductTieredPriceDto>  map = Maps.newHashMap();
		for(PsiProductTieredPrice price:list){
			String key =price.getProduct().getId()+","+price.getColor()+","+price.getSupplier().getId();
			PsiProductTieredPriceDto priceDto = null;
			if(map.get(key)==null){
				priceDto=new PsiProductTieredPriceDto();
				priceDto.setProductId(price.getProduct().getId());
				priceDto.setSupplierId(price.getSupplier().getId());
				priceDto.setProNameColor(price.getName());
				priceDto.setColor(price.getColor());
				priceDto.setNikeName(price.getSupplier().getNikename());
			}else{
				priceDto=map.get(key);
			}
			this.createDtoFromTiered(priceDto, price);
			map.put(key, priceDto);
		}
		return map;
	} 
	
	
	//获得各阶级价格
	public PsiProductTieredPriceDto findPrices(Integer productId,Integer supplierId,String currency,String color) {
		DetachedCriteria dc = psiProductTieredPriceDao.createDetachedCriteria();
		dc.add(Restrictions.eq("delFlag", "0"));
		if(supplierId!=null){
			dc.add(Restrictions.eq("supplier.id", supplierId));
		}
		
		if(productId!=null){
			dc.add(Restrictions.eq("product.id", productId));
		}
		
		if(currency!=null){
			dc.add(Restrictions.eq("currencyType", currency));
		}
		
		dc.add(Restrictions.eq("color", color));
		List<PsiProductTieredPrice> list=psiProductTieredPriceDao.find(dc);
		PsiProductTieredPriceDto priceDto = new PsiProductTieredPriceDto();
		for(PsiProductTieredPrice price:list){
			priceDto.setProductId(price.getProduct().getId());
			priceDto.setSupplierId(price.getSupplier().getId());
			priceDto.setProNameColor(price.getName());
			priceDto.setColor(price.getColor());
			priceDto.setNikeName(price.getSupplier().getNikename());
			this.createDtoFromTiered(priceDto, price);
		}
		return priceDto;
	} 
	
	
	
	public Page<PsiProductTieredPrice> find(Page<PsiProductTieredPrice> page, PsiProductTieredPrice tieredPrice) {
		DetachedCriteria dc = psiProductTieredPriceDao.createDetachedCriteria();
		if(tieredPrice.getSupplier()!=null&&tieredPrice.getSupplier().getId()!=null){
			dc.add(Restrictions.eq("supplier.id", tieredPrice.getSupplier().getId()));
		}
		if(tieredPrice.getProduct()!=null&&tieredPrice.getProduct().getId()!=null){
			dc.add(Restrictions.eq("product.id", tieredPrice.getProduct().getId()));
		}
		dc.addOrder(Order.desc("id"));
		dc.add(Restrictions.eq("delFlag", "0"));
		return psiProductTieredPriceDao.find(page, dc);
	}
	
	
	public PsiProductTieredPriceDto find(Integer productId,Integer supplierId,String color,String productIdColor) {
		PsiProductTieredPriceDto priceDto =new PsiProductTieredPriceDto();
		if(StringUtils.isNotEmpty(productIdColor)){
			String arr[]=productIdColor.split(",");
			if(arr.length>1){
				productId=Integer.parseInt(arr[0].toString());
				color=arr[1].toString();
			}else{
				productId=Integer.parseInt(arr[0].toString());
				color="";
			}
		}
		priceDto.setProductId(productId);
		priceDto.setSupplierId(supplierId);
		priceDto.setColor(color);
		DetachedCriteria dc = psiProductTieredPriceDao.createDetachedCriteria();
		if(supplierId!=null){
			dc.add(Restrictions.eq("supplier.id",supplierId));
		}
		dc.add(Restrictions.eq("product.id", productId));
		dc.add(Restrictions.eq("color", color));
		List<PsiProductTieredPrice> prices= psiProductTieredPriceDao.find(dc);
		
		if(supplierId!=null){
			for(PsiProductTieredPrice price:prices){
				if(StringUtils.isEmpty(priceDto.getProNameColor())){
					PsiProduct product = price.getProduct();
					PsiSupplier supplier =price.getSupplier();
					priceDto.setProNameColor(price.getName());
					priceDto.setNikeName(supplier.getNikename());
					priceDto.setTaxRate(supplier.getTaxRate());
					priceDto.setCurrencyType(supplier.getCurrencyType());
					priceDto.setMoq(product.getMinOrderPlaced());
					if(product.getColor().contains(",")){
						priceDto.setHasMulColor(true);
					}else{
						priceDto.setHasMulColor(false);
					}
				}
				this.createDtoFromTiered(priceDto, price);
			}
		}else{
			Map<Integer,List<PsiProductTieredPrice>>  supplierMap = Maps.newHashMap();
			//如果有多个供应商随机取个供应商
			for(PsiProductTieredPrice price:prices){
				Integer supplierid=price.getSupplier().getId();
				List<PsiProductTieredPrice> priceList = null;
				if(supplierMap.get(supplierid)==null){
					priceList = Lists.newArrayList();
				}else{
					priceList=supplierMap.get(supplierid);
				}
				priceList.add(price);
				supplierMap.put(supplierid, priceList);
			}
			
			for(Map.Entry<Integer, List<PsiProductTieredPrice>> entry:supplierMap.entrySet()){
				List<PsiProductTieredPrice>  priceList =entry.getValue();
				for(PsiProductTieredPrice price:priceList){
					if(StringUtils.isEmpty(priceDto.getProNameColor())){
						PsiProduct product = price.getProduct();
						PsiSupplier supplier =price.getSupplier();
						priceDto.setSupplierId(supplier.getId());
						priceDto.setProNameColor(price.getName());
						priceDto.setNikeName(supplier.getNikename());
						priceDto.setCurrencyType(supplier.getCurrencyType());
						priceDto.setMoq(product.getMinOrderPlaced());
						if(product.getColor().contains(",")){
							priceDto.setHasMulColor(true);
						}else{
							priceDto.setHasMulColor(false);
						}
					}
					this.createDtoFromTiered(priceDto, price);
				}
				break;//随机取一个供应商的id
			}
			
		}
		
	
		return priceDto;
	}
	
	@Transactional(readOnly = false)
	public void applyPrice(PsiProductTieredPriceDto priceDto,MultipartFile supplierFile) throws IOException {
		//查出及时产品价格，作为改前价格
		PsiProductTieredPriceDto beforeDto=this.findPrices(priceDto.getProductId(), priceDto.getSupplierId(),priceDto.getCurrencyType(), priceDto.getColor());
		priceDto.setBefore500cny(beforeDto.getLeval500cny());
		priceDto.setBefore1000cny(beforeDto.getLeval1000cny());
		priceDto.setBefore2000cny(beforeDto.getLeval2000cny());
		priceDto.setBefore3000cny(beforeDto.getLeval3000cny());
		priceDto.setBefore5000cny(beforeDto.getLeval5000cny());
		priceDto.setBefore10000cny(beforeDto.getLeval10000cny());
		priceDto.setBefore15000cny(beforeDto.getLeval15000cny());
		priceDto.setBefore500usd(beforeDto.getLeval500usd());
		priceDto.setBefore1000usd(beforeDto.getLeval1000usd());
		priceDto.setBefore2000usd(beforeDto.getLeval2000usd());
		priceDto.setBefore3000usd(beforeDto.getLeval3000usd());
		priceDto.setBefore5000usd(beforeDto.getLeval5000usd());
		priceDto.setBefore10000usd(beforeDto.getLeval10000usd());
		priceDto.setBefore15000usd(beforeDto.getLeval15000usd());
		
		priceDto.setCreateDate(new Date());
		priceDto.setCreateUser(UserUtils.getUser());
		priceDto.setReviewSta("0");
		
		
		if(supplierFile!=null&&supplierFile.getSize()!=0){
			String	filePath = ContextLoader.getCurrentWebApplicationContext().getServletContext().getRealPath("/")+ Global.getCkBaseDir() + "/psi/productPrice";
			String suffix = supplierFile.getOriginalFilename().substring(supplierFile.getOriginalFilename().lastIndexOf("."));  
			String uuid = UUID.randomUUID().toString();
			File file1 = new File(filePath, beforeDto.getProNameColor());
			if (!file1.exists()) {
				file1.mkdirs();
			}
			File piFilePdf = new File(file1, uuid+suffix);
			FileUtils.copyInputStreamToFile(supplierFile.getInputStream(),piFilePdf);
			priceDto.setFilePath(Global.getCkBaseDir() + "/psi/productPrice/"+beforeDto.getProNameColor()+"/"+uuid+suffix);
		}
		
		this.priceReviewDao.save(priceDto);
	}

	
	@Transactional(readOnly = false)
	public void cancelPrice(PsiProductTieredPriceDto  dto) {
		dto.setCancelDate(new Date());
		dto.setCancelUser(UserUtils.getUser());
		dto.setReviewSta("3");
		this.priceReviewDao.save(dto);
	}
	
	
	/**
	 * 价格保存
	 */
	@Transactional(readOnly = false)
	public void reviewSave(PsiProductTieredPriceDto priceDtoTemp) {
		//保存审核状态
		PsiProductTieredPriceDto  priceDto=this.priceReviewDao.get(priceDtoTemp.getId());
		priceDto.setReviewDate(new Date());
		priceDto.setReviewUser(UserUtils.getUser());
		priceDto.setReviewSta("2");
		this.priceReviewDao.save(priceDto);
		//查询最新税率  
//		Float taxRate = (this.psiSupplierService.get(priceDto.getSupplierId()).getTaxRate()+100)/100f;
		DetachedCriteria dc = psiProductTieredPriceDao.createDetachedCriteria();
		dc.add(Restrictions.eq("supplier.id",priceDto.getSupplierId()));
		dc.add(Restrictions.eq("product.id", priceDto.getProductId()));
		List<PsiProductTieredPrice> prices= psiProductTieredPriceDao.find(dc);
		Map<String,List<PsiProductTieredPrice>> dataMap=Maps.newHashMap();
		
		for(PsiProductTieredPrice price:prices){
			String color = price.getColor();
			List<PsiProductTieredPrice> priceList = null;
			if(dataMap.get(color)==null){
				priceList=Lists.newArrayList();
			}else{
				priceList =dataMap.get(color);
			}
			priceList.add(price);
			dataMap.put(color, priceList);
		}
		
		for(Map.Entry<String,List<PsiProductTieredPrice>> entry:dataMap.entrySet()){
			String key = entry.getKey();
			//if("1".equals(isCheck)||(!"1".equals(isCheck)&&key.equals(priceDto.getColor()))){
			if("1".equals(priceDto.getHasColor())||(!"1".equals(priceDto.getHasColor())&&key.equals(priceDto.getColor()))){
				saveData(entry.getValue(), priceDto,key);
			}
		}
	}
		
		
	public  void saveData(List<PsiProductTieredPrice> prices,PsiProductTieredPriceDto priceDto,String color){
		String remark = priceDto.getRemark();
		String productName="";
		Integer supplierId = priceDto.getSupplierId();
		Integer productId = priceDto.getProductId();
		String  content = priceDto.getContent();
		
		User createUser = priceDto.getCreateUser();
		Date createTime = priceDto.getCreateDate();
		//如果都改了,则为整体变化
		Float cny500=priceDto.getLeval500cny();
		Float usd500=priceDto.getLeval500usd();
		boolean flag=true;
		String currency="CNY";
		Float curPrice=0f;
		if(cny500!=null&&cny500.equals(priceDto.getLeval1000cny())&&cny500.equals(priceDto.getLeval2000cny())&&cny500.equals(priceDto.getLeval3000cny())&&cny500.equals(priceDto.getLeval5000cny())&&cny500.equals(priceDto.getLeval10000cny())&&cny500.equals(priceDto.getLeval15000cny())){
			flag=false;
			curPrice=cny500;
		}else if(usd500!=null&&usd500.equals(priceDto.getLeval1000usd())&&usd500.equals(priceDto.getLeval2000usd())&&usd500.equals(priceDto.getLeval3000usd())&&usd500.equals(priceDto.getLeval5000usd())&&usd500.equals(priceDto.getLeval10000usd())&&usd500.equals(priceDto.getLeval15000usd())){
			flag=false;
			currency="USD";
			curPrice=usd500;
		}
		
		//分开保存阶梯价格日志
		for(PsiProductTieredPrice price:prices){
			if(StringUtils.isEmpty(productName)){
				productName=price.getProduct().getName();
				if(StringUtils.isNotEmpty(price.getColor())){
					productName=productName+"_"+color;
				}
			}
			Float afterPrice =null;
			Float beforePrice =price.getPrice();
			String currencyType =price.getCurrencyType();
//			Float noTaxBeforePrice =null;
//			if(beforePrice!=null){
//				noTaxBeforePrice=new BigDecimal(beforePrice/taxRate).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
//			}
			
			//判断是不是所有的都变了  500CNY的全变，或者500USD全变
			if(!flag&&currency.equals(currencyType)&&(price.getLevel().intValue()==500)){
				if(("USD".equals(currency)&&!usd500.equals(beforePrice))||("CNY".equals(currency)&&!cny500.equals(beforePrice))){
					this.savePriceLog(curPrice, beforePrice, remark, "All", color, productName, content, supplierId, productId,currencyType,createUser,createTime);
				}
			}
			
			if("USD".equals(currencyType)){
				if(price.getLevel().intValue()==500){
					afterPrice=priceDto.getLeval500usd();
					if(afterPrice==null&&beforePrice==null){
						continue;
					}else if(afterPrice==null||beforePrice==null||Float.floatToIntBits(afterPrice)!=Float.floatToIntBits(beforePrice)){
						this.savePrice(price, afterPrice);
						if(flag){
							this.savePriceLog(afterPrice, beforePrice, remark, "500", color, productName, content, supplierId, productId,currencyType,createUser,createTime);
						}
					}
				}else if(price.getLevel().intValue()==1000){
					afterPrice=priceDto.getLeval1000usd();
					if(afterPrice==null&&beforePrice==null){
						continue;
					}else if(afterPrice==null||beforePrice==null||Float.floatToIntBits(afterPrice)!=Float.floatToIntBits(beforePrice)){
						this.savePrice(price, afterPrice);
						if(flag){
							this.savePriceLog(afterPrice, beforePrice, remark, "1000", color, productName, content, supplierId, productId,currencyType,createUser,createTime);
						}
					}
				}else if(price.getLevel().intValue()==2000){
					afterPrice=priceDto.getLeval2000usd();
					if(afterPrice==null&&beforePrice==null){
						continue;
					}else if(afterPrice==null||beforePrice==null||Float.floatToIntBits(afterPrice)!=Float.floatToIntBits(beforePrice)){
						this.savePrice(price, afterPrice);
						if(flag){
							this.savePriceLog(afterPrice, beforePrice, remark, "2000", color, productName, content, supplierId, productId,currencyType,createUser,createTime);
						}
					}
				}else if(price.getLevel().intValue()==3000){
					afterPrice=priceDto.getLeval3000usd();
					if(afterPrice==null&&beforePrice==null){
						continue;
					}else if(afterPrice==null||beforePrice==null||Float.floatToIntBits(afterPrice)!=Float.floatToIntBits(beforePrice)){
						this.savePrice(price, afterPrice);
						if(flag){
							this.savePriceLog(afterPrice, beforePrice, remark, "3000", color, productName, content, supplierId, productId,currencyType,createUser,createTime);
						}
					}
				}else if(price.getLevel().intValue()==5000){
					afterPrice=priceDto.getLeval5000usd();
					if(afterPrice==null&&beforePrice==null){
						continue;
					}else if(afterPrice==null||beforePrice==null||Float.floatToIntBits(afterPrice)!=Float.floatToIntBits(beforePrice)){
						this.savePrice(price, afterPrice);
						if(flag){
							this.savePriceLog(afterPrice, beforePrice, remark, "5000", color, productName, content, supplierId, productId,currencyType,createUser,createTime);
						}
					}
				}else if(price.getLevel().intValue()==10000){
					afterPrice=priceDto.getLeval10000usd();
					if(afterPrice==null&&beforePrice==null){
						continue;
					}else if(afterPrice==null||beforePrice==null||Float.floatToIntBits(afterPrice)!=Float.floatToIntBits(beforePrice)){
						this.savePrice(price, afterPrice);
						if(flag){
							this.savePriceLog(afterPrice, beforePrice, remark, "10000", color, productName, content, supplierId, productId,currencyType,createUser,createTime);
						}
					}
				}else if(price.getLevel().intValue()==15000){
					afterPrice=priceDto.getLeval15000usd();
					if(afterPrice==null&&beforePrice==null){
						continue;
					}else if(afterPrice==null||beforePrice==null||Float.floatToIntBits(afterPrice)!=Float.floatToIntBits(beforePrice)){
						this.savePrice(price, afterPrice);
						if(flag){
							this.savePriceLog(afterPrice, beforePrice, remark, "15000", color, productName, content, supplierId, productId,currencyType,createUser,createTime);
						}
					}
				}
			}else{
				if(price.getLevel().intValue()==500){
					afterPrice=priceDto.getLeval500cny();
					if(afterPrice==null&&beforePrice==null){
						continue;
					}else if(afterPrice==null||beforePrice==null||Float.floatToIntBits(afterPrice)!=Float.floatToIntBits(beforePrice)){
						this.savePrice(price, afterPrice);
						if(flag){
							this.savePriceLog(afterPrice, beforePrice, remark, "500", color, productName, content, supplierId, productId,currencyType,createUser,createTime);
						}
					}
				}else if(price.getLevel().intValue()==1000){
					afterPrice=priceDto.getLeval1000cny();
					if(afterPrice==null&&beforePrice==null){
						continue;
					}else if(afterPrice==null||beforePrice==null||Float.floatToIntBits(afterPrice)!=Float.floatToIntBits(beforePrice)){
						this.savePrice(price, afterPrice);
						if(flag){
							this.savePriceLog(afterPrice, beforePrice, remark, "1000", color, productName, content, supplierId, productId,currencyType,createUser,createTime);
						}
					}
				}else if(price.getLevel().intValue()==2000){
					afterPrice=priceDto.getLeval2000cny();
					if(afterPrice==null&&beforePrice==null){
						continue;
					}else if(afterPrice==null||beforePrice==null||Float.floatToIntBits(afterPrice)!=Float.floatToIntBits(beforePrice)){
						this.savePrice(price, afterPrice);
						if(flag){
							this.savePriceLog(afterPrice, beforePrice, remark, "2000", color, productName, content, supplierId, productId,currencyType,createUser,createTime);
						}
					}
				}else if(price.getLevel().intValue()==3000){
					afterPrice=priceDto.getLeval3000cny();
					if(afterPrice==null&&beforePrice==null){
						continue;
					}else if(afterPrice==null||beforePrice==null||Float.floatToIntBits(afterPrice)!=Float.floatToIntBits(beforePrice)){
						this.savePrice(price, afterPrice);
						if(flag){
							this.savePriceLog(afterPrice, beforePrice, remark, "3000", color, productName, content, supplierId, productId,currencyType,createUser,createTime);
						}
					}
				}else if(price.getLevel().intValue()==5000){
					afterPrice=priceDto.getLeval5000cny();
					if(afterPrice==null&&beforePrice==null){
						continue;
					}else if(afterPrice==null||beforePrice==null||Float.floatToIntBits(afterPrice)!=Float.floatToIntBits(beforePrice)){
						this.savePrice(price, afterPrice);
						if(flag){
							this.savePriceLog(afterPrice, beforePrice, remark, "5000", color, productName, content, supplierId, productId,currencyType,createUser,createTime);
						}
					}
				}else if(price.getLevel().intValue()==10000){
					afterPrice=priceDto.getLeval10000cny();
					if(afterPrice==null&&beforePrice==null){
						continue;
					}else if(afterPrice==null||beforePrice==null||Float.floatToIntBits(afterPrice)!=Float.floatToIntBits(beforePrice)){
						this.savePrice(price, afterPrice);
						if(flag){
							this.savePriceLog(afterPrice, beforePrice, remark, "10000", color, productName, content, supplierId, productId,currencyType,createUser,createTime);
						}
					}
				}else if(price.getLevel().intValue()==15000){
					afterPrice=priceDto.getLeval15000cny();
					if(afterPrice==null&&beforePrice==null){
						continue;
					}else if(afterPrice==null||beforePrice==null||Float.floatToIntBits(afterPrice)!=Float.floatToIntBits(beforePrice)){
						this.savePrice(price, afterPrice);
						if(flag){
							this.savePriceLog(afterPrice, beforePrice, remark, "15000", color, productName, content, supplierId, productId,currencyType,createUser,createTime);
						}
					}
				}
			}
			
		}
	}
	
	public void savePrice(PsiProductTieredPrice price,Float afterPrice){
			price.setPrice(afterPrice);
			this.psiProductTieredPriceDao.save(price);
	}
	
	/**
	 * 保存日志信息
	 */
	public void savePriceLog(Float afterPrice,Float beforePrice,String remark,String type,String color,String productName,String content,Integer supplierId,Integer productId,String currencyType,User createUser,Date createTime){
		PsiSupplier  sup = new PsiSupplier();
		sup.setId(supplierId);
		PsiProductTieredPriceLog   priceLog = new PsiProductTieredPriceLog();
		priceLog.setContent(content);
		priceLog.setProduct(new PsiProduct(productId));
		priceLog.setColor(color);
		priceLog.setSupplier(sup);
		priceLog.setTieredType(type);
		priceLog.setProductNameColor(productName);
		priceLog.setRemark(remark);
		priceLog.setCurrencyType(currencyType);
		priceLog.setSureUser(UserUtils.getUser());
		priceLog.setSureTime(new Date());
		priceLog.setCreateUser(createUser);
		priceLog.setCreateTime(createTime);
		priceLog.setOldPrice(beforePrice);
		priceLog.setPrice(afterPrice);
		psiProductTieredPriceLogDao.save(priceLog);
}
	
	public void createDtoFromTiered(PsiProductTieredPriceDto priceDto, PsiProductTieredPrice price){
		Float beforePrice=price.getPrice();
		if("USD".equals(price.getCurrencyType())){
			if(price.getLevel().intValue()==500){
				priceDto.setLeval500usd(beforePrice);
			}else if(price.getLevel().intValue()==1000){
				priceDto.setLeval1000usd(beforePrice);
			}else if(price.getLevel().intValue()==2000){
				priceDto.setLeval2000usd(beforePrice);
			}else if(price.getLevel().intValue()==3000){
				priceDto.setLeval3000usd(beforePrice);
			}else if(price.getLevel().intValue()==5000){
				priceDto.setLeval5000usd(beforePrice);
			}else if(price.getLevel().intValue()==10000){
				priceDto.setLeval10000usd(beforePrice);
			}else if(price.getLevel().intValue()==15000){
				priceDto.setLeval15000usd(beforePrice);
			}
		}else{
			if(price.getLevel().intValue()==500){
				priceDto.setLeval500cny(beforePrice);
			}else if(price.getLevel().intValue()==1000){
				priceDto.setLeval1000cny(beforePrice);
			}else if(price.getLevel().intValue()==2000){
				priceDto.setLeval2000cny(beforePrice);
			}else if(price.getLevel().intValue()==3000){
				priceDto.setLeval3000cny(beforePrice);
			}else if(price.getLevel().intValue()==5000){
				priceDto.setLeval5000cny(beforePrice);
			}else if(price.getLevel().intValue()==10000){
				priceDto.setLeval10000cny(beforePrice);
			}else if(price.getLevel().intValue()==15000){
				priceDto.setLeval15000cny(beforePrice);
			}
		}
	}
	
	//------------------------------------
	
	/**
	 * 根据产品moq获得产品的 美元价格                
	 * key:产品名   key：供应商id value:价格
	 */
	public Map<String,Map<Integer,Float>> getPriceBaseMoq(Set<Integer> productIds,Set<Integer> supplierIds){
		Map<String,Map<Integer,Float>> resMap =Maps.newHashMap();
		String sql="SELECT CONCAT(b.`brand`,' ',b.`model`) AS proName,a.`color`,b.`min_order_placed`,a.`product_id`,a.`supplier_id`,TRUNCATE( CASE WHEN a.`currency_type`='CNY' THEN   MIN(a.`price`)*((100+c.`tax_rate`)/100)/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+"  ELSE   MIN(a.`price`)*((100+c.`tax_rate`)/100) END ,2) FROM psi_product_tiered_price AS a,psi_product AS b ,psi_supplier AS c WHERE a.`product_id`=b.`id` AND a.`supplier_id`=c.`id` AND a.`currency_type`=c.`currency_type`" +
				" AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END )  ";
		Parameter parameter =null;
		List<Object[]> list =null;
		if(productIds!=null&&supplierIds!=null){
			sql+=" AND a.`product_id` in :p1 AND a.`supplier_id` in :p2";
			parameter= new Parameter(productIds,supplierIds);
		}else if(productIds!=null&&supplierIds==null){
			sql+=" AND a.`product_id` in :p1";
			parameter= new Parameter(productIds);
		}else if(productIds==null&&supplierIds!=null){
			sql+=" AND a.`supplier_id` in :p1";
			parameter= new Parameter(supplierIds);
		}
		sql+=" GROUP BY a.`product_id`,a.`supplier_id`,a.`color`";
		
		if(parameter!=null){
			list=this.psiProductTieredPriceDao.findBySql(sql,parameter);
		}else{
			list=this.psiProductTieredPriceDao.findBySql(sql);
		}
		
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				String proName =obj[0].toString();
				String color=obj[1].toString();
				if(StringUtils.isNotEmpty(color)){
					proName=proName+"_"+color;
				}
				Integer moq =Integer.parseInt(obj[2].toString());
				Integer productId=Integer.parseInt(obj[3].toString());
				Integer supplierId=Integer.parseInt(obj[4].toString());
				Float  price =obj[5]!=null?Float.parseFloat(obj[5].toString()):null;
				String proKey=proName+",;"+moq;
				Map<Integer,Float> supMap =null;
				if(resMap.get(proKey)==null){
					supMap=Maps.newHashMap();
				}else{
					supMap=resMap.get(proKey);
				}
				supMap.put(supplierId, price);
				resMap.put(proKey, supMap);
			}
		}
		return resMap;
	}
	
	
	
	/**
	 * 根据产品moq获得产品的价格
	 * key:产品名+颜色    value:moq price
	 */
	public Map<String,Map<String,Object>> getMoqPriceBaseMoq(String currencyType,Map<String, Float> rateRs){
		Map<String,Map<String,Object>> rs = Maps.newHashMap();
		String sql="SELECT CASE WHEN  a.color ='' THEN CONCAT(b.`brand`,' ',b.`model`) ELSE CONCAT(b.`brand`,' ',b.`model`,'_',a.color) END  AS proName," +
				"TRUNCATE( CASE WHEN a.`currency_type`='CNY' THEN  MIN(a.`price`)*((100+c.`tax_rate`)/100)*"+MathUtils.getRate("CNY", currencyType, rateRs)
				+"  ELSE  MIN(a.`price`)*((100+c.`tax_rate`)/100)*"+MathUtils.getRate("USD", currencyType, rateRs)+" END ,2) as price "+
				",b.`min_order_placed`,a.`currency_type` FROM psi_product_tiered_price AS a,psi_product AS b,psi_supplier AS c" +
				"  WHERE a.`supplier_id`=c.id AND  a.`product_id`=b.`id` AND a.`currency_type`=c.`currency_type` AND a.`del_flag`='0' AND b.`del_flag`='0'AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END )  GROUP BY a.`product_id`,a.`color`,a.`currency_type`  ";
		List<Object[]> 	list=this.psiProductTieredPriceDao.findBySql(sql);
		
		for (Object[] obj : list) {
			String proColor =obj[0].toString();
			Object price = obj[1];
			Object moq = obj[2];
			
			Map<String,Object> data = Maps.newHashMap();
			data.put("moq", moq);
			data.put("price",price);
			rs.put(proColor, data);
		}
		return rs;
	}
	
	
	/**
	 * 根据产品moq获得产品的价格     不分供应商  
	 * key:产品名+颜色    value:moq price
	 */
	public Map<String,Map<String,Object>> getMoqPriceBaseMoqNoSupplier(){
		Map<String,Map<String,Object>> rs = Maps.newLinkedHashMap();
		//不按供应商默认货币单位优先,直接去高价
		String sql="SELECT CASE WHEN  a.color ='' THEN CONCAT(b.`brand`,' ',b.`model`) ELSE CONCAT(b.`brand`,' ',b.`model`,'_',a.color) END  AS proName," +
				" ROUND( CASE WHEN a.`currency_type`='CNY' THEN   MIN(a.`price`)*((100+c.`tax_rate`)/100)/"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+"  ELSE   MIN(a.`price`)*((100+c.`tax_rate`)/100) END ,2) as price "+
				",b.`min_order_placed` FROM psi_product_tiered_price AS a,psi_product AS b,psi_supplier AS c" +
				"  WHERE a.`supplier_id`=c.id AND  a.`product_id`=b.`id` AND a.`del_flag`='0' AND b.`del_flag`='0' AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END )  GROUP BY a.`product_id`,a.`color`,a.`supplier_id` ORDER BY a.`price` ASC  ";
		List<Object[]> 	list=this.psiProductTieredPriceDao.findBySql(sql);
		
		for (Object[] obj : list) {
			String proColor =obj[0].toString();
			Object price = obj[1];
			Object moq = obj[2];
			Map<String,Object> data = Maps.newLinkedHashMap();
			data.put("moq", moq);
			data.put("price",price);
			rs.put(proColor, data);
		}
		return rs;
	}
	
	public Map<String,Map<String,Object>> getMoqCNYPriceBaseMoqNoSupplier(){
		Map<String,Map<String,Object>> rs = Maps.newHashMap();
		String sql="SELECT CASE WHEN  a.color ='' THEN CONCAT(b.`brand`,' ',b.`model`) ELSE CONCAT(b.`brand`,' ',b.`model`,'_',a.color) END  AS proName," +
				" TRUNCATE( CASE WHEN a.`currency_type`='USD' THEN   MIN(a.`price`)*((100+c.`tax_rate`)/100)*"+AmazonProduct2Service.getRateConfig().get("USD/CNY")+"  ELSE   MIN(a.`price`*((100+c.`tax_rate`)/100)) END ,2) as price "+
				",b.`min_order_placed` FROM psi_product_tiered_price AS a,psi_product AS b,psi_supplier AS c" +
				"  WHERE a.`supplier_id`=c.id AND  a.`product_id`=b.`id` AND a.`currency_type`=c.`currency_type` AND a.`del_flag`='0' AND b.`del_flag`='0' AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END )  GROUP BY a.`product_id`,a.`color`,a.`currency_type`  HAVING price IS NOT NULL ";
		List<Object[]> 	list=this.psiProductTieredPriceDao.findBySql(sql);
		
		for (Object[] obj : list) {
			String proColor =obj[0].toString();
			Object price = obj[1];
			Object moq = obj[2];
			Map<String,Object> data = Maps.newHashMap();
			data.put("moq", moq);
			data.put("price",price);
			rs.put(proColor, data);
		}
		return rs;
	}
	

	
	
	/**
	 * 根据产品moq获得产品的价格  不分供应商
	 * key:产品名   key：供应商id value:价格
	 */
	public Map<String,Map<String,Float>> getPriceBaseMoqNoSupplier(){
		Map<String,Map<String,Float>> resMap =Maps.newLinkedHashMap();
		String sql="SELECT CASE WHEN  a.color ='' THEN CONCAT(b.`brand`,' ',b.`model`) ELSE CONCAT(b.`brand`,' ',b.`model`,'_',a.color) END  AS proName,ROUND(MIN(a.`price`)*((100+c.`tax_rate`)/100),2),a.`currency_type`  FROM psi_product_tiered_price AS a,psi_product AS b,psi_supplier AS c WHERE a.`product_id`=b.`id`and a.`supplier_id`=c.id " +
				" AND a.del_flag='0' AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END ) AND a.price IS NOT NULL  GROUP BY a.`product_id`,a.`color`,a.`currency_type`,a.`supplier_id` ORDER BY a.`price` ASC  ";
		List<Object[]>	list=this.psiProductTieredPriceDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object [] obj:list){
				String productColor=obj[0].toString();
				Map<String,Float> priceMap =null;
				String currency = obj[2].toString();
				Float price =null;
				if(obj[1]!=null){
					price=Float.parseFloat(obj[1].toString());
				}
				
				if(resMap.get(productColor)==null){
					priceMap = Maps.newLinkedHashMap();
				}else{
					priceMap=resMap.get(productColor);
				}
				
				if("USD".equals(currency)){
					priceMap.put("USD", price);
				}else{
					priceMap.put("CNY", price);
				}
				resMap.put(productColor, priceMap);
			}
		}
		return resMap;
	}
	
	
	public Map<String,List<Object[]>> getPriceBaseMoqNoSupplier2(){
		Map<String,List<Object[]>> resMap =Maps.newLinkedHashMap();
		String sql="SELECT distinct CASE WHEN  a.color ='' THEN CONCAT(b.`brand`,' ',b.`model`) ELSE CONCAT(b.`brand`,' ',b.`model`,'_',a.color) END  AS proName,ROUND(a.`price`*((100+c.`tax_rate`)/100),2),a.`currency_type`  FROM psi_product_tiered_price AS a,psi_product AS b,psi_supplier AS c WHERE a.`product_id`=b.`id`and a.`supplier_id`=c.id " +
				" AND a.del_flag='0'  AND a.price IS NOT NULL  GROUP BY a.`product_id`,a.`color`,a.`currency_type`,a.`supplier_id`,a.`level` ORDER BY a.`price` ASC  ";
		List<Object[]>	list=this.psiProductTieredPriceDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object [] obj:list){
				String productColor=obj[0].toString();
				//String currency = obj[2].toString();
				//Float price =Float.parseFloat(obj[1].toString());
				List<Object[]> temp=resMap.get(productColor);
				if(temp==null){
					temp=Lists.newArrayList();
					resMap.put(productColor, temp);
				}
				temp.add(obj);
			}
		}
		return resMap;
	}
	
	
	/**
	 * 根据产品moq获得产品的价格 (不含税) 不分供应商
	 * key:产品名   key：供应商id value:价格
	 */
	public Map<String,Map<String,Float>> getNoTaxPriceBaseMoqNoSupplier(){
		Map<String,Map<String,Float>> resMap =Maps.newHashMap();
		String sql="SELECT CASE WHEN  a.color ='' THEN CONCAT(b.`brand`,' ',b.`model`) ELSE CONCAT(b.`brand`,' ',b.`model`,'_',a.color) END  AS proName,MIN(a.`price`),a.`currency_type`  FROM psi_product_tiered_price AS a,psi_product AS b WHERE a.`product_id`=b.`id` " +
				" AND a.del_flag='0' AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END ) AND a.price IS NOT NULL  GROUP BY a.`product_id`,a.`color`,a.`currency_type` ";
		List<Object[]>	list=this.psiProductTieredPriceDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object [] obj:list){
				String productColor=obj[0].toString();
				Map<String,Float> priceMap =null;
				String currency = obj[2].toString();
				Float price =null;
				if(obj[1]!=null){
					price=Float.parseFloat(obj[1].toString());
				}
				
				if(resMap.get(productColor)==null){
					priceMap = Maps.newHashMap();
				}else{
					priceMap=resMap.get(productColor);
				}
				
				if("USD".equals(currency)){
					priceMap.put("USD", price);
				}else{
					priceMap.put("CNY", price);
				}
				resMap.put(productColor, priceMap);
			}
		}
		return resMap;
	}
	
	
	/**
	 * 根据产品moq获得产品的价格 (不含税CNY价格) 不分供应商
	 * key:产品名   key：供应商id value:价格
	 */
	public Map<String,Float> getNoTaxCnyPriceNoSupplier(){
		Map<String,Float> resMap =Maps.newHashMap();
		String sql="SELECT CASE WHEN  a.color ='' THEN CONCAT(b.`brand`,' ',b.`model`) ELSE CONCAT(b.`brand`,' ',b.`model`,'_',a.color) END  AS proName,MIN(a.`price`),a.`currency_type`  FROM psi_product_tiered_price AS a,psi_product AS b WHERE a.`product_id`=b.`id` " +
				" AND a.del_flag='0' AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END ) AND a.price IS NOT NULL  GROUP BY a.`product_id`,a.`color`,a.`currency_type` ";
		List<Object[]> list=this.psiProductTieredPriceDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object [] obj:list){
				String productColor=obj[0].toString();
				String currency = obj[2].toString();
				Float price =null;
				if(obj[1]!=null){
					price=Float.parseFloat(obj[1].toString());
				}
				if("USD".equals(currency)){	//转换成CNY价格
					price = price / MathUtils.getRate("CNY", "USD", null);
				}
				resMap.put(productColor, price);
			}
		}
		return resMap;
	}
	
	/**
	 * 根据产品moq获得产品的价格   不分颜色
	 * key:产品名   key：供应商id value:价格
	 */
	public Map<String,Map<Integer,Map<String,Float>>> getPriceBaseMoqNoColor(){
		Map<String,Map<Integer,Map<String,Float>>> resMap =Maps.newHashMap();
		String sql="SELECT (case when a.color='' then concat(b.brand,' ',b.model) else  concat(concat(b.brand,' ',b.model),'_',a.color)  end) name,a.`supplier_id`,a.`currency_type`,ROUND(MIN(a.`price`)*((100+c.`tax_rate`)/100),2)  FROM psi_product_tiered_price AS a,psi_product AS b,psi_supplier AS c WHERE a.`product_id`=b.`id` AND a.`supplier_id`=c.id AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END ) GROUP BY a.`product_id`,a.`color`,a.`supplier_id`,a.`currency_type`";
		List<Object[]>	list=this.psiProductTieredPriceDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object [] obj:list){
				String product=obj[0].toString();
				Integer supplierId =Integer.parseInt(obj[1].toString());
				Map<Integer,Map<String,Float>> supplierMap =null;
				Map<String,Float> priceMap = null;
				String currency = obj[2].toString();
				Float price =null;
				if(obj[3]!=null){
					price=Float.parseFloat(obj[3].toString());
				}
				if(resMap.get(product)==null){
					supplierMap = Maps.newHashMap();
				}else{
					supplierMap=resMap.get(product);
				}
				
				if(supplierMap.get(supplierId)==null){
					priceMap=Maps.newHashMap();
				}else{
					priceMap=supplierMap.get(supplierId);
				}
				
				if("USD".equals(currency)){
					priceMap.put("USD", price);
				}else{
					priceMap.put("CNY", price);
				}
				
				supplierMap.put(supplierId, priceMap);
				resMap.put(product, supplierMap);
			}
		}
		return resMap;
	}
	
	
	
	
	/**
	 * 根据产品moq获得产品的价格  不分供应商
	 * 获得单产品不同供应商的美元、人民币价格
	 */
	public Map<Integer,Map<String,Float>> getSinglePriceBaseMoq(String proNameColor,Float partsPrice){
		Map<Integer,Map<String,Float>> resMap =Maps.newHashMap();
		String sql="SELECT a.`supplier_id`,ROUND(MIN(a.`price`)*((100+c.`tax_rate`)/100),2),a.`currency_type`  FROM psi_product_tiered_price AS a,psi_product AS b,psi_supplier AS c WHERE a.`product_id`=b.`id` AND a.`supplier_id`=c.id AND a.`del_flag`='0' AND b.`del_flag`='0' AND a.`price` IS NOT NULL AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END )" +
				" AND (CASE WHEN  a.color ='' THEN CONCAT(b.`brand`,' ',b.`model`) ELSE CONCAT(b.`brand`,' ',b.`model`,'_',a.color) END)=:p1  GROUP BY a.`product_id`,a.`color`,a.`currency_type`,a.supplier_id";
		List<Object[]>	list=this.psiProductTieredPriceDao.findBySql(sql,new Parameter(proNameColor));
		if(list!=null&&list.size()>0){
			for(Object [] obj:list){
				Integer supplierId=Integer.parseInt(obj[0].toString());
				Map<String,Float> priceMap =null;
				String currency = obj[2].toString();
				Float price =null;
				if(obj[1]!=null){
					price=Float.parseFloat(obj[1].toString());
				}
				if(resMap.get(supplierId)==null){
					priceMap = Maps.newHashMap();
				}else{
					priceMap=resMap.get(supplierId);
				}
				
				if("USD".equals(currency)){
					if(price!=null){
						//是美元也转换成人民币显示
						priceMap.put("CNY", price*AmazonProduct2Service.getRateConfig().get("USD/CNY")+partsPrice/AmazonProduct2Service.getRateConfig().get("USD/CNY"));
					}else{
						priceMap.put("USD", null);
					}
				}else{
					if(price!=null){
						priceMap.put("CNY", price+partsPrice/AmazonProduct2Service.getRateConfig().get("USD/CNY"));
					}else{
						priceMap.put("CNY", null);
					}
				}
				resMap.put(supplierId, priceMap);
			}
		}
		return resMap;
	}
	
	/**
	 * 根据产品moq获得产品的价格  不分供应商颜色(含税价格)
	 *
	 */
	public String getPriceBaseMoqNoColorNoSupplier(Integer productId){
		String res="";
		String sql="SELECT ROUND(MIN(a.`price`)*((100+c.`tax_rate`)/100),2),a.`currency_type`,b.`pack_quantity`,b.`has_electric`,b.gw,b.tax_refund,b.`has_magnetic`  FROM psi_product_tiered_price AS a,psi_product AS b,psi_supplier AS c WHERE a.`supplier_id`=c.id AND  a.`product_id`=b.`id` AND a.`currency_type`=c.`currency_type` AND a.`del_flag`='0' AND b.`del_flag`='0' " +
				"AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END ) AND a.`product_id`=:p1 GROUP BY a.`product_id`,a.`color`,a.`currency_type`";
		List<Object[]>	list=this.psiProductTieredPriceDao.findBySql(sql,new Parameter(productId));
		if(list!=null&&list.size()>0){
			Object[] obj=list.get(0);
			res=(obj[0]!=null?obj[0].toString():"0")+"_"+obj[1]+"_"+obj[2]+"_"+(obj[3]!=null?obj[3].toString():"0")+"_"+obj[4]+"_"+(obj[5]!=null?obj[5].toString():"17")+"_"+(obj[6]!=null?obj[6].toString():"0");
		}
		return res;
	}
	
	public Map<String,String> getPriceBaseMoqNoColorNoSupplier(){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT MIN(a.`price`),a.`currency_type`,b.`pack_quantity`,b.`has_electric`,b.gw,b.tax_refund,(CASE WHEN a.`color`='' THEN CONCAT( b.`brand`,' ',b.`model`) ELSE CONCAT( b.`brand`,' ',b.`model`,'_',a.`color`) END ) AS nameColor,b.id  FROM psi_product_tiered_price AS a,psi_product AS b,psi_supplier AS c WHERE a.`supplier_id`=c.id AND  a.`product_id`=b.`id` AND a.`currency_type`=c.`currency_type` AND a.`del_flag`='0' AND b.`del_flag`='0' " +
				"AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END )  GROUP BY a.`product_id`,a.`color`,a.`currency_type`";
		List<Object[]>	list=this.psiProductTieredPriceDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				String  res=(obj[0]!=null?obj[0].toString():"0")+"_"+obj[1]+"_"+obj[2]+"_"+(obj[3]!=null?obj[3].toString():"0")+"_"+obj[4]+"_"+(obj[5]!=null?obj[5].toString():"17")+"_"+obj[7].toString();
				map.put(obj[6].toString(), res);
			}
		}
		return map;
	}
	
	public Map<String,String> getPriceBaseMoqNoColorNoSupplier2(){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT ROUND(MAX(a.`price`)*((100+c.`tax_rate`)/100),2),a.`currency_type`,b.`pack_quantity`,b.`has_electric`,b.gw,b.tax_refund,(CASE WHEN a.`color`='' THEN CONCAT( b.`brand`,' ',b.`model`) ELSE CONCAT( b.`brand`,' ',b.`model`,'_',a.`color`) END ) AS nameColor,b.id  FROM psi_product_tiered_price AS a,psi_product AS b,psi_supplier AS c WHERE a.`supplier_id`=c.id AND  a.`product_id`=b.`id` AND a.`currency_type`=c.`currency_type` AND a.`del_flag`='0' AND b.`del_flag`='0' " +
				"AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END )  GROUP BY a.`product_id`,a.`color`,a.`currency_type`";
		List<Object[]>	list=this.psiProductTieredPriceDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				String  res=(obj[0]!=null?obj[0].toString():"0")+"_"+obj[1]+"_"+obj[2]+"_"+(obj[3]!=null?obj[3].toString():"0")+"_"+obj[4]+"_"+(obj[5]!=null?obj[5].toString():"17")+"_"+obj[7].toString();
				map.put(obj[6].toString(), res);
			}
		}
		return map;
	}
	
	
	public Map<String,String> getPriceBaseMoqNoColorNoSupplier3(){
		Map<String,String> map=Maps.newHashMap();
		String sql="SELECT MAX(a.`price`),a.`currency_type`,b.`pack_quantity`,b.`has_electric`,b.gw,b.tax_refund,(CASE WHEN a.`color`='' THEN CONCAT( b.`brand`,' ',b.`model`) ELSE CONCAT( b.`brand`,' ',b.`model`,'_',a.`color`) END ) AS nameColor,b.id  FROM psi_product_tiered_price AS a,psi_product AS b,psi_supplier AS c WHERE a.`supplier_id`=c.id AND  a.`product_id`=b.`id` AND a.`currency_type`=c.`currency_type` AND a.`del_flag`='0' AND b.`del_flag`='0' " +
				"AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END )  GROUP BY a.`product_id`,a.`color`,a.`currency_type`";
		List<Object[]>	list=this.psiProductTieredPriceDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for (Object[] obj: list) {
				String  res=(obj[0]!=null?obj[0].toString():"0")+"_"+obj[1]+"_"+obj[2]+"_"+(obj[3]!=null?obj[3].toString():"0")+"_"+obj[4]+"_"+(obj[5]!=null?obj[5].toString():"17")+"_"+obj[7].toString();
				map.put(obj[6].toString(), res);
			}
		}
		return map;
	}
	
	
	public Map<String,String> getProductColors(){
		 Map<String,String> resMap = Maps.newHashMap();
		String sql="SELECT (CASE WHEN a.`color`='' THEN CONCAT( b.`brand`,' ',b.`model`) ELSE CONCAT( b.`brand`,' ',b.`model`,'_',a.`color`) END ) AS nameColor,a.`product_id`,a.`color` FROM psi_product_tiered_price AS a ,psi_product AS b WHERE a.`product_id`=b.`id`AND a.`del_flag`='0' AND b.`del_flag`='0' GROUP BY a.`product_id`,a.`color`";
		List<Object[]>	list=this.psiProductTieredPriceDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object[] obj:list){
				String key = obj[1]+","+obj[2];
				resMap.put(key, obj[0].toString());
			}
		}
		return resMap;
	}
	
	@Transactional(readOnly = false)
	public void saveList(List<PsiProductTieredPrice> list){
		this.psiProductTieredPriceDao.save(list);
	}
	

	
	/**
	 *获取人民币的产品价格，产品加颜色为主键 
	 */
	public Map<String,Float> findMoqPriceByCurrency(){
		Map<String,Float> map=Maps.newHashMap();
		String sql="SELECT CONCAT(CONCAT(b.`brand`,' ',b.`model`),CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) NAME,"+ 
               " ROUND(MIN(a.`price`),2)  AS realprice FROM psi_product_tiered_price AS a,psi_product AS b,psi_supplier AS c WHERE a.`product_id`=b.`id` AND a.`supplier_id`=c.id AND a.`price` >0 AND a.`currency_type`='CNY' AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END )   " +
               "  GROUP BY NAME";
		List<Object[]> list =psiProductTieredPriceDao.findBySql(sql);
		for (Object[] obj: list) {
			map.put(obj[0].toString(),obj[1]!=null?Float.parseFloat(obj[1].toString()):0f);
		}
		return map;
	}
	
	
	/**
	 * 产品经理id,查询所有管辖的产品
	 */
	public List<Integer> getProductIdsByManagerUserId(String managerUserId){
		//String sql ="SELECT c.`id` FROM psi_product_manage_group AS a,sys_dict AS b,psi_product AS c WHERE a.`dict_id`=b.`id` AND c.`TYPE`=b.`value` AND a.`user_id`=:p1 ";
		String sql="SELECT  DISTINCT p.id FROM ( "+
				"	SELECT SUBSTRING_INDEX(SUBSTRING_INDEX(a.user_id,',',b.help_topic_id+1),',',-1) userId,dict_id  "+
				"	FROM psi_product_manage_group a JOIN mysql.help_topic b ON b.help_topic_id < (LENGTH(a.user_id) - LENGTH(REPLACE(a.user_id,',',''))+1)  "+
				"	) d JOIN psi_product_type_group g ON d.dict_id=g.id "+
				"	JOIN sys_dict t ON g.`dict_id`=t.id AND t.`del_flag`='0' AND  t.`type`='product_type' "+
				"	JOIN psi_product p ON p.type=t.value AND p.del_flag='0' "+
				"	JOIN sys_user r ON r.id=d.userId AND r.del_flag='0' where r.id=:p1 ";
		return  psiProductTieredPriceDao.findBySql(sql,new Parameter(managerUserId));
	}
	

	/**
	 * 采购经理id,查询所有管辖的产品
	 */
	public List<Integer> getProductIdsByPurchaseUserId(String purchaseUserId){
		String sql ="SELECT c.`id` FROM psi_product_purchase_group AS a,sys_dict AS b,psi_product AS c WHERE a.`dict_id`=b.`id` AND c.`TYPE`=b.`value` AND a.`user_id`=:p1 ";
		return  psiProductTieredPriceDao.findBySql(sql,new Parameter(purchaseUserId));
	}
	
	
	/**
	 * 根据产品moq获得产品的价格 (不含税) 不分供应商
	 * key:产品名   key：供应商id value:价格
	 */
	public Map<String,Map<String,Float>> getHasTaxPriceBaseMoqNoSupplier(){
		Map<String,Map<String,Float>> resMap =Maps.newHashMap();
		String sql="SELECT CASE WHEN  a.color ='' THEN CONCAT(b.`brand`,' ',b.`model`) ELSE CONCAT(b.`brand`,' ',b.`model`,'_',a.color) END  AS proName,ROUND(MIN(a.`price`)*((100+c.`tax_rate`)/100),2)  AS realprice,a.`currency_type`  FROM psi_product_tiered_price AS a,psi_product AS b,psi_supplier AS c WHERE a.`product_id`=b.`id` " +
				"	AND c.id=a.supplier_id		 AND a.del_flag='0' AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END ) AND a.price IS NOT NULL  GROUP BY a.`product_id`,a.`color`,a.`currency_type` ";
		List<Object[]>	list=this.psiProductTieredPriceDao.findBySql(sql);
		if(list!=null&&list.size()>0){
			for(Object [] obj:list){
				String productColor=obj[0].toString();
				Map<String,Float> priceMap =null;
				String currency = obj[2].toString();
				Float price =null;
				if(obj[1]!=null){
					price=Float.parseFloat(obj[1].toString());
				}
				
				if(resMap.get(productColor)==null){
					priceMap = Maps.newHashMap();
				}else{
					priceMap=resMap.get(productColor);
				}
				
				if("USD".equals(currency)){
					priceMap.put("USD", price);
				}else{
					priceMap.put("CNY", price);
				}
				resMap.put(productColor, priceMap);
			}
		}
		return resMap;
	}
	
//	public Map<String,Map<String,Object>> findMoqPrice(){
//	Map<String,Map<String,Object>> map=Maps.newHashMap();
//	String sql="SELECT CONCAT(CONCAT(b.`brand`,' ',b.`model`),CASE WHEN a.`color`!='' THEN CONCAT ('_',a.`color`) ELSE '' END) NAME, "+
//               " a.`currency_type`,MIN(a.`price`)  AS realprice FROM psi_product_tiered_price AS a,psi_product AS b WHERE a.`product_id`=b.`id` AND a.`price` >0 AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END )  "+
//               " GROUP BY NAME,a.`currency_type` ";
//	List<Object[]> list =psiProductTieredPriceDao.findBySql(sql);
//	for (Object[] obj: list) {
//		Map<String,Object> temp=map.get(obj[0].toString());
//		if(temp==null){
//			temp=Maps.newHashMap();
//			map.put(obj[0].toString(),temp);
//		}
//		temp.put("type",obj[1].toString());
//		temp.put("price",Float.parseFloat(obj[2].toString()));
//	}
//	return map;
//}

	
	/**
	 * 根据产品moq获得产品的价格
	 * key:产品名_颜色 ,供应商id   value:价格
	 */
//	public Map<String,Float> getPriceBaseMoq2(Set<Integer> productIds,Set<Integer> supplierIds){
//		Map<String,Float> resMap =Maps.newHashMap();
//		String sql="SELECT CONCAT(b.`brand`,' ',b.`model`) AS proName,a.`color`,a.`product_id`,a.`supplier_id`,MIN(a.`price`) FROM psi_product_tiered_price AS a,psi_product AS b ,psi_supplier AS c WHERE a.`product_id`=b.`id` AND a.`supplier_id`=c.`id` AND a.`currency_type`=c.`currency_type`" +
//				" AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END )  ";
//		Parameter parameter =null;
//		List<Object[]> list =null;
//		if(productIds!=null&&supplierIds!=null){
//			sql+=" AND a.`product_id` in :p1 AND a.`supplier_id` in :p2";
//			parameter= new Parameter(productIds,supplierIds);
//		}else if(productIds!=null&&supplierIds==null){
//			sql+=" AND a.`product_id` in :p1";
//			parameter= new Parameter(productIds);
//		}else if(productIds==null&&supplierIds!=null){
//			sql+=" AND a.`supplier_id` in :p1";
//			parameter= new Parameter(supplierIds);
//		}
//		sql+=" GROUP BY a.`product_id`,a.`supplier_id`,a.`color`";
//		
//		if(parameter!=null){
//			list=this.psiProductTieredPriceDao.findBySql(sql,parameter);
//		}else{
//			list=this.psiProductTieredPriceDao.findBySql(sql);
//		}
//		
//		if(list!=null&&list.size()>0){
//			for(Object[] obj:list){
//				String proName =obj[0].toString();
//				String color=obj[1].toString();
//				if(StringUtils.isNotEmpty(color)){
//					proName=proName+"_"+color;
//				}
//				Integer productId=Integer.parseInt(obj[2].toString());
//				Integer supplierId=Integer.parseInt(obj[3].toString());
//				Float  price =obj[4]!=null?Float.parseFloat(obj[4].toString()):null;
//				String key=proName+","+supplierId;
//				resMap.put(key, price);
//			}
//		}
//		return resMap;
//	}

	
//	/***
//	 * 产品转化成美元的价格
//	 * 不分颜色、供应商
//	 */
//	public Map<Integer,Float>  getPrice(){
//		String sql="SELECT a.`product_id`, TRUNCATE( CASE WHEN a.`currency_type`='CNY' THEN   MIN(a.`price`)/6.18 ELSE   MIN(a.`price`) END ,2) AS price  FROM psi_product_tiered_price AS a,psi_product AS b,psi_supplier AS c" +
//				" WHERE a.`supplier_id`=c.id AND  a.`product_id`=b.`id` AND a.`currency_type`=c.`currency_type` AND a.`del_flag`='0' AND b.`del_flag`='0' AND  a.`level`<=(CASE WHEN b.`min_order_placed`<500 THEN 500 ELSE b.`min_order_placed`END )  GROUP BY a.`product_id`";
//		Map<Integer,Float> resMap = Maps.newHashMap();
//		List<Object[]> 	list=this.psiProductTieredPriceDao.findBySql(sql);
//		for (Object[] obj : list) {
//			Integer productId = Integer.parseInt(obj[0].toString());
//			Float   price = obj[1]!=null?Float.parseFloat(obj[1].toString()):null;
//			resMap.put(productId, price);
//		}
//		return resMap;
//	}
	
	
}
