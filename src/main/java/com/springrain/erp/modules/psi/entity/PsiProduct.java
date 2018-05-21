package com.springrain.erp.modules.psi.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.amazoninfo.service.AmazonProduct2Service;
import com.springrain.erp.modules.psi.entity.parts.PsiParts;
import com.springrain.erp.modules.sys.entity.User;
import com.springrain.erp.modules.sys.utils.UserUtils;

/**
 * 进销存产品
 */
@Entity
@Table(name = "psi_product")
@DynamicInsert
@DynamicUpdate
public class PsiProduct implements Serializable{
	private static final long serialVersionUID = 6753681231805289651L;
	private			 Integer 			id;
	private			 String				brand;
	private			 String 			model;  
	private			 String 			type;
	private			 String			    color;
	private			 String			    delColor;	//0库存淘汰被系统删除的颜色
	private 		 String 			platform;
	private 		 String 			image;
	private 		 BigDecimal 		length;
	private 		 BigDecimal 		width;
	private 		 BigDecimal         height;
	private 		 BigDecimal 		weight;
	private 		 Integer            packQuantity;	//装箱个数
	private 		 BigDecimal 		boxVolume;  	//大箱体积
	private 		 BigDecimal 		gw;         	//毛重
	private 		 BigDecimal 		volumeRatio;    //体积比
	private 		 String 			isSale;
	private 		 String 			isMain;
	private			 String 			isNew;
	private 		 User 				createUser;  //跟单员
	private 		 User 				createUser1; //创建人
	private			 Date				createTime;
	private 		 User				updateUser;
	private 		 Date 				updateTime;
	private 		 String				delFlag;
	private			 String 			description;
	private 		 String 			combination;
	private 		 BigDecimal 		packLength;
	private 		 BigDecimal 		packWidth;
	private			 BigDecimal 		packHeight;
	private			 Integer			transportType;
	private 		 Integer 			producePeriod;
	private 		 List<PsiBarcode> 	barcodes;
	private 		 Integer		    minOrderPlaced;

	
	private 		 BigDecimal 		productPackLength;
	private 		 BigDecimal 		productPackWidth;
	private			 BigDecimal 		productPackHeight;
	private			 BigDecimal 		productPackWeight;
	
	private 		 List<ProductSupplier>  psiSuppliers;
	
	private 		 Set<ProductParts>  productParts = Sets.newHashSet();
	
	private 		 String				remark;
	private 		 String				filePath;
	private			 String	            addedMonth;
	
	private          String             euHscode;
	private          String             jpHscode;
	private          String             caHscode;
	private          String             usHscode;
	private          String             hkHscode;
	private          String             cnHscode;
	private          String             mxHscode;
	private          Float              euCustomDuty;
	private          Float              euImportDuty;
	private          Float              jpCustomDuty;
	private          Float              jpImportDuty;
	private          Float              caCustomDuty;
	private          Float              caImportDuty;
	private          Float              usCustomDuty;
	private          Float              usImportDuty;
	private          Float              mxCustomDuty;
	private          Float              mxImportDuty;
	
	
	private          String             chineseName;
	private          String             productList;
	
	private          String             certification;
	private          String             factoryCertification;
	private          String             inateckCertification;
	
	
	private          String             certificationFile;
	private          String             hasElectric;
	private          String             hasPower;	//是否带电源 0：不带电源 1：带电源
	private          String             tranReportFile;
	private          String             priceChangeLog;
	private          String             colorPlatform;
	
	private          String             contractNo; 	//合同号
	private          String             declarePoint;   //申报要素
	
	private 		 List<PsiProductEliminate> 	eliminates;
	
	private          String            material;       //产品材质
	private          Integer           taxRefund;      //退税率
	private          String            signedSample;   //签样
	private          String            checkList;      //checkList文件
	private          String            techFile;       //技术规格书文件
	private          String            improveRemark;  //改进备注
	
	private          String            hasMagnetic;    //带磁
	private          String            reviewSta;      //审核状态 0未审核1已审核
	
	private          String            checkState;     //checklist审核 0:未审核 1:审核通过  2：审核未通过
	private          User              checkUser;//check list审核人
	private          User              checkListUser;//check list上传者
	
	private          String            modelShort;
	
	private          String            components; //配件 0:否 1：是
	
	public String getModelShort() {
		return modelShort;
	}

	public void setModelShort(String modelShort) {
		this.modelShort = modelShort;
	}

	public String getProductList() {
		return productList;
	}

	public void setProductList(String productList) {
		this.productList = productList;
	}

	public PsiProduct() {
	}
	
	public PsiProduct(Integer id) {
		super();
		this.id = id;
	}
	
	
	public String getCnHscode() {
		return cnHscode;
	}

	public void setCnHscode(String cnHscode) {
		this.cnHscode = cnHscode;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getHasElectric() {
		return hasElectric;
	}

	public String getHasPower() {
		return hasPower;
	}

	public void setHasPower(String hasPower) {
		this.hasPower = hasPower;
	}

	public String getCertification() {
		return certification;
	}

	public void setCertification(String certification) {
		this.certification = certification;
	}

	public String getCertificationFile() {
		return certificationFile;
	}

	public void setCertificationFile(String certificationFile) {
		this.certificationFile = certificationFile;
	}

	public String getTranReportFile() {
		return tranReportFile;
	}

	public void setTranReportFile(String tranReportFile) {
		this.tranReportFile = tranReportFile;
	}

	public String getPriceChangeLog() {
		return priceChangeLog;
	}

	public void setPriceChangeLog(String priceChangeLog) {
		this.priceChangeLog=priceChangeLog;
	}
	
	
	@Transient
	public void setPriceChangeLogs(String priceChangeLog) {
		if(StringUtils.isBlank(this.priceChangeLog)){
			this.priceChangeLog=priceChangeLog;
		}else{
			this.priceChangeLog = this.priceChangeLog+ priceChangeLog;
		}
	}

	public void setHasElectric(String hasElectric) {
		this.hasElectric = hasElectric;
	}

	public String getChineseName() {
		return chineseName;
	}
	public void setChineseName(String chineseName) {
		this.chineseName = chineseName;
	}
	public String getHkHscode() {
		return hkHscode;
	}
	public void setHkHscode(String hkHscode) {
		this.hkHscode = hkHscode;
	}
	public String getEuHscode() {
		return euHscode;
	}
	public void setEuHscode(String euHscode) {
		this.euHscode = euHscode;
	}
	public String getJpHscode() {
		return jpHscode;
	}
	public void setJpHscode(String jpHscode) {
		this.jpHscode = jpHscode;
	}
	public String getCaHscode() {
		return caHscode;
	}
	public void setCaHscode(String caHscode) {
		this.caHscode = caHscode;
	}
	public String getUsHscode() {
		return usHscode;
	}
	public void setUsHscode(String usHscode) {
		this.usHscode = usHscode;
	}
	
	public Float getEuCustomDuty() {
		return euCustomDuty;
	}

	public void setEuCustomDuty(Float euCustomDuty) {
		this.euCustomDuty = euCustomDuty;
	}

	public Float getEuImportDuty() {
		return euImportDuty;
	}

	public void setEuImportDuty(Float euImportDuty) {
		this.euImportDuty = euImportDuty;
	}

	public Float getJpCustomDuty() {
		return jpCustomDuty;
	}

	public void setJpCustomDuty(Float jpCustomDuty) {
		this.jpCustomDuty = jpCustomDuty;
	}

	public Float getJpImportDuty() {
		return jpImportDuty;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getDeclarePoint() {
		return declarePoint;
	}

	public void setDeclarePoint(String declarePoint) {
		this.declarePoint = declarePoint;
	}

	public void setJpImportDuty(Float jpImportDuty) {
		this.jpImportDuty = jpImportDuty;
	}

	public Float getCaCustomDuty() {
		return caCustomDuty;
	}

	public void setCaCustomDuty(Float caCustomDuty) {
		this.caCustomDuty = caCustomDuty;
	}

	public Float getCaImportDuty() {
		return caImportDuty;
	}

	public void setCaImportDuty(Float caImportDuty) {
		this.caImportDuty = caImportDuty;
	}

	public Float getUsCustomDuty() {
		return usCustomDuty;
	}

	public void setUsCustomDuty(Float usCustomDuty) {
		this.usCustomDuty = usCustomDuty;
	}

	public Float getUsImportDuty() {
		return usImportDuty;
	}

	public void setUsImportDuty(Float usImportDuty) {
		this.usImportDuty = usImportDuty;
	}

	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	
	public void setFilePathAppend(String filePath){
		if(StringUtils.isNotEmpty(this.filePath)){
			this.filePath=this.filePath+","+filePath;
		}else{
			this.filePath = filePath;
		}
	}
	
	public Integer getTransportType() {
		return transportType;
	}
	public void setTransportType(Integer transportType) {
		this.transportType = transportType;
	}
	public Integer getProducePeriod() {
		return producePeriod;
	}
	public void setProducePeriod(Integer producePeriod) {
		this.producePeriod = producePeriod;
	}
	@Transient
	public String getName() {
		return brand+" "+model;
	}
	
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getType() {
		if(type!=null){
			type = HtmlUtils.htmlUnescape(type);
		}
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	
	public String getDelColor() {
		return delColor;
	}

	public void setDelColor(String delColor) {
		this.delColor = delColor;
	}

	@Transient
	public String getPlatformToUp() {
		if(StringUtils.isNotEmpty(platform)){
			return platform.toUpperCase();
		}
		return platform;
	}
	
	public String getPlatform() {
		return platform;
	}
	public void setPlatform(String platform) {
		this.platform = platform;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public BigDecimal getLength() {
		return length;
	}
	public void setLength(BigDecimal length) {
		this.length = length;
	}
	public BigDecimal getWidth() {
		return width;
	}
	public void setWidth(BigDecimal width) {
		this.width = width;
	}
	public BigDecimal getHeight() {
		return height;
	}
	public void setHeight(BigDecimal height) {
		this.height = height;
	}
	public BigDecimal getWeight() {
		return weight;
	}
	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}
	public String getIsSale() {
		return isSale;
	}
	public void setIsSale(String isSale) {
		this.isSale = isSale;
	}
	public String getIsMain() {
		return isMain;
	}
	public void setIsMain(String isMain) {
		this.isMain = isMain;
	}
	public String getIsNew() {
		return isNew;
	}
	public void setIsNew(String isNew) {
		this.isNew = isNew;
	}
	
	public BigDecimal getProductPackLength() {
		return productPackLength;
	}
	public void setProductPackLength(BigDecimal productPackLength) {
		this.productPackLength = productPackLength;
	}
	public BigDecimal getProductPackWidth() {
		return productPackWidth;
	}
	public void setProductPackWidth(BigDecimal productPackWidth) {
		this.productPackWidth = productPackWidth;
	}
	public BigDecimal getProductPackHeight() {
		return productPackHeight;
	}
	public void setProductPackHeight(BigDecimal productPackHeight) {
		this.productPackHeight = productPackHeight;
	}
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "create_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}
	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "create_user1",updatable= false)
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser1() {
		return createUser1;
	}
	public void setCreateUser1(User createUser1) {
		this.createUser1 = createUser1;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	@Column(updatable=false)
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "update_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(User updateUser) {
		this.updateUser = updateUser;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	public Integer getPackQuantity() {
		return packQuantity;
	}
	public void setPackQuantity(Integer packQuantity) {
		this.packQuantity = packQuantity;
	}
	public BigDecimal getBoxVolume() {
		return boxVolume;
	}
	public void setBoxVolume(BigDecimal boxVolume) {
		this.boxVolume = boxVolume;
	}
	public BigDecimal getGw() {
		return gw;
	}
	public void setGw(BigDecimal gw) {
		this.gw = gw;
	}
	public BigDecimal getVolumeRatio() {
		return volumeRatio;
	}
	public void setVolumeRatio(BigDecimal volumeRatio) {
		this.volumeRatio = volumeRatio;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public BigDecimal getPackLength() {
		return packLength;
	}
	public void setPackLength(BigDecimal packLength) {
		this.packLength = packLength;
	}
	public BigDecimal getPackWidth() {
		return packWidth;
	}
	public void setPackWidth(BigDecimal packWidth) {
		this.packWidth = packWidth;
	}
	public BigDecimal getPackHeight() {
		return packHeight;
	}
	public void setPackHeight(BigDecimal packHeight) {
		this.packHeight = packHeight;
	}
	
	public Integer getMinOrderPlaced() {
		return minOrderPlaced;
	}
	public void setMinOrderPlaced(Integer minOrderPlaced) {
		this.minOrderPlaced = minOrderPlaced;
	}
	public String getAddedMonth() {
		return addedMonth;
	}
	public void setAddedMonth(String addedMonth) {
		this.addedMonth = addedMonth;
	}
	
	
	@OneToMany(mappedBy = "product",fetch=FetchType.LAZY)
	@NotFound(action = NotFoundAction.IGNORE)
	public Set<ProductParts> getProductParts() {
		return productParts;
	}
	public void setProductParts(Set<ProductParts> productParts) {
		this.productParts = productParts;
	}
	
	@JsonIgnore
	@OneToMany(mappedBy = "product",cascade=javax.persistence.CascadeType.ALL)
	@NotFound(action = NotFoundAction.IGNORE)
	public List<ProductSupplier> getPsiSuppliers() {
		return psiSuppliers;
	}
	public void setPsiSuppliers(List<ProductSupplier> psiSuppliers) {
		this.psiSuppliers = psiSuppliers;
	}
	
	public String getCombination() {
		return combination;
	}
	public void setCombination(String combination) {
		this.combination = combination;
	}
	
	@OneToMany(mappedBy = "psiProduct", fetch=FetchType.LAZY)
	@Where(clause="del_flag=0")
	@OrderBy(value="productName")
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Cascade(CascadeType.SAVE_UPDATE)
	public List<PsiBarcode> getBarcodes() {
		return barcodes;
	}
	public void setBarcodes(List<PsiBarcode> barcodes) {
		this.barcodes = barcodes;
	}
	
	@OneToMany(mappedBy = "product", fetch=FetchType.LAZY)
	@Where(clause="del_flag=0")
	@OrderBy(value="color")
	@NotFound(action = NotFoundAction.IGNORE)
	@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@Cascade(CascadeType.SAVE_UPDATE)
	public List<PsiProductEliminate> getEliminates() {
		return eliminates;
	}

	public void setEliminates(List<PsiProductEliminate> eliminates) {
		this.eliminates = eliminates;
	}

	@Transient
	public Map<String, Map<String, PsiBarcode>> getBarcodeMap() {
		Map<String, Map<String, PsiBarcode>> rs = Maps.newLinkedHashMap();
		for (PsiBarcode barcode : barcodes) {
			String key = barcode.getProductColor();
			Map<String, PsiBarcode> tempMap = rs.get(key);
			if(tempMap==null){
				tempMap = Maps.newHashMap();
				rs.put(key, tempMap);
			}
			tempMap.put(barcode.getProductPlatform(), barcode);
		}
		return rs;
	}
	
	
	@Transient
	public Map<String, Map<String, List<PsiBarcode>>> getDupBarcodeMap() {
		Map<String, Map<String, List<PsiBarcode>>> rs = Maps.newLinkedHashMap();
		for (PsiBarcode barcode : barcodes) {
			String key = barcode.getProductColor();
			Map<String, List<PsiBarcode>> tempMap = rs.get(key);
			if(tempMap==null){
				tempMap = Maps.newHashMap();
				rs.put(key, tempMap);
			}
			List<PsiBarcode> list = tempMap.get(barcode.getProductPlatform());
			if(list==null){
				list = Lists.newArrayList();
				tempMap.put(barcode.getProductPlatform(), list);
			}		
			list.add(barcode);
		}
		return rs;
	}
	
	
	@Transient
	public Map<String, Map<String,List<PsiBarcode>>> getBarcodeMapByColor() {
		Map<String, Map<String, List<PsiBarcode>>> rs = Maps.newLinkedHashMap();
		for (PsiBarcode barcode : barcodes) {
			String key = barcode.getProductColor();
			Map<String, List<PsiBarcode>> tempMap = rs.get(key);
			if(tempMap==null){
				tempMap = Maps.newHashMap();
				rs.put(key, tempMap);
			}
			List<PsiBarcode> list=tempMap.get(barcode.getProductPlatform());
			if(list==null){
				list=Lists.newArrayList();
				tempMap.put(barcode.getProductPlatform(),list);
			}
			list.add(barcode);
		}
		return rs;
	}
	
	
	private Map<String, Map<String, PsiBarcode>> barcodeMap2;
	
	@Transient
	public Map<String, Map<String, PsiBarcode>> getBarcodeMap2() {
		if(barcodeMap2==null){
			barcodeMap2 = Maps.newHashMap();
			for (PsiBarcode barcode : barcodes) {
				String key = barcode.getProductPlatform();
				Map<String, PsiBarcode> tempMap = barcodeMap2.get(key);
				if(tempMap==null){
					tempMap = Maps.newLinkedHashMap();
					barcodeMap2.put(key, tempMap);
				}
				tempMap.put(barcode.getProductColor(), barcode);
			}
		}
		return barcodeMap2;
	}
	
	@Transient
	public Map<String, Map<String,List<PsiBarcode>>> getBarcodeMap2ByAccount() {
		Map<String, Map<String, List<PsiBarcode>>> barcodeMap=Maps.newHashMap();

		for (PsiBarcode barcode : barcodes) {
				String key = barcode.getProductPlatform();
				Map<String, List<PsiBarcode>> tempMap = barcodeMap.get(key);
				if(tempMap==null){
					tempMap = Maps.newLinkedHashMap();
					barcodeMap.put(key, tempMap);
				}
				List<PsiBarcode> list=tempMap.get(barcode.getProductColor());
				if(list==null){
					list=Lists.newArrayList();
					tempMap.put(barcode.getProductColor(),list);
				}
				list.add(barcode);
		}
		
		return barcodeMap;
	}
	
	@Transient
	public Map<String, Map<String,PsiBarcode>> getBarcodeMap2ByAccount2() {
		Map<String, Map<String,PsiBarcode>> barcodeMap=Maps.newHashMap();

		for (PsiBarcode barcode : barcodes) {
			    String name = (barcode.getAccountName()==null?"":barcode.getAccountName());
				String key = barcode.getProductPlatform();
				Map<String, PsiBarcode> tempMap = barcodeMap.get(key+name);
				if(tempMap==null){
					tempMap = Maps.newLinkedHashMap();
					barcodeMap.put(key+name, tempMap);
				}
				tempMap.put(barcode.getProductColor(), barcode);
		}
		return barcodeMap;
	}
	
	
	@Transient
	public Map<String, Map<String, PsiBarcode>> getBarcodeMapByUpdate() {
		Map<String, Map<String, PsiBarcode>> rs = Maps.newHashMap();
		for (PsiBarcode barcode : barcodes) {
			String key = barcode.getProductPlatform();
			Map<String, PsiBarcode> tempMap = rs.get(key);
			if(tempMap==null){
				tempMap = Maps.newHashMap();
				rs.put(key, tempMap);
			}
			barcode.setDelFlag("1");
			barcode.setLastUpdateBy(UserUtils.getUser());
			barcode.setLastUpdateTime(new Date());
			tempMap.put(barcode.getProductColor(), barcode);
			List<PsiSku> skus = barcode.getSkus();
			if(skus!=null){
				for (PsiSku psiSku : skus) {
					psiSku.setDelFlag("1");
				}
			}
		}
		return rs;
	}
	
	@Transient
	public Map<String, Map<String, PsiBarcode>> getBarcodeMapByAccount() {
		Map<String, Map<String, PsiBarcode>> rs = Maps.newHashMap();
		for (PsiBarcode barcode : barcodes) {
			String key = (barcode.getAccountName()==null?"":barcode.getAccountName());
			Map<String, PsiBarcode> tempMap = rs.get(key);
			if(tempMap==null){
				tempMap = Maps.newHashMap();
				rs.put(key, tempMap);
			}
			barcode.setDelFlag("1");
			barcode.setLastUpdateBy(UserUtils.getUser());
			barcode.setLastUpdateTime(new Date());
			tempMap.put(barcode.getProductColor(), barcode);
			List<PsiSku> skus = barcode.getSkus();
			if(skus!=null){
				for (PsiSku psiSku : skus) {
					psiSku.setDelFlag("1");
				}
			}
		}
		return rs;
	}
	
	@Transient
	public List<String> getProductNameWithColor() {
		String name = getName();
		if(StringUtils.isNotEmpty(color)){
			List<String> rs = Lists.newArrayList();
			for(String col:color.split(",")){
				if(StringUtils.isNotEmpty(col)){
					rs.add(name+"_"+col);
				}
			}
			return rs;
		}else{
			return Lists.newArrayList(name);
		}
	}
	
	
	@Transient
	public Map<String,List<PsiParts>> getTempPartsMap(){
		Map<String,List<PsiParts>> tempMap =Maps.newHashMap();
		for(ProductParts proParts:this.productParts){
			String color =proParts.getColor();
			if(StringUtils.isEmpty(color)){
				color="No Color";
			}
			List<PsiParts> list=tempMap.get(color);
			if(list==null){
				list = Lists.newArrayList();
			}
			list.add(proParts.getParts());
			tempMap.put(color, list);
		}
		return tempMap;
	}
	
	@Transient
	public Map<String,Float> getTempPartsTotalMap(){
		Map<String,Float> tempMap =Maps.newHashMap();
		for(ProductParts proParts:this.productParts){
			String productName =this.brand+" "+this.model;
			String productColor =proParts.getColor();
			if(StringUtils.isNotEmpty(productColor)){
				productName+="_"+productColor;
			};
			
			PsiParts pps = proParts.getParts();
			Integer ratio = proParts.getMixtureRatio()==null?0:proParts.getMixtureRatio();
			Float totalPrice = 0f;
			if(pps.getPrice()!=null){
				totalPrice=pps.getPrice()*ratio;
			}else if(pps.getRmbPrice()!=null){
				totalPrice=pps.getRmbPrice()/AmazonProduct2Service.getRateConfig().get("USD/CNY")*ratio;
			}
			
			if(tempMap.get(productName)!=null){
				totalPrice+=tempMap.get(productName);
			};
			tempMap.put(productName, totalPrice);
		}
		
		return tempMap;
	}
	
	@Transient
	public Float getTranVolume(){
		Float rs = 0f;
		if(packQuantity!=null && boxVolume!=null){
			rs = boxVolume.floatValue()/packQuantity;
			BigDecimal bd = new BigDecimal(rs);
			bd = bd.setScale(4, BigDecimal.ROUND_HALF_UP);
			rs = bd.floatValue();
		}
		return rs;
	}
	
	@Transient
	public Float getExpressGw(){
		BigDecimal volumeWg =packLength.multiply(packWidth).multiply(packHeight).divide(new BigDecimal(5000),0,BigDecimal.ROUND_UP);
		
		int i = volumeWg.intValue();
		float f =i +0.5f;
		float tempF=0;
		if(volumeWg.floatValue()>f){
			//大于0.5进1
			tempF=(float)(i+1);
		}else{
			tempF=f;
		}
		
		BigDecimal expressWg =new BigDecimal(tempF/packQuantity);
		expressWg=expressWg.setScale(2,BigDecimal.ROUND_HALF_UP);
		/*if(expressWg.floatValue()>getTranGw()){
			return expressWg.floatValue();
		}else{
			return getTranGw();
		}*/
		if(this.volumeRatio.floatValue()>167f){
			return getTranGw();
		}else{
			return expressWg.floatValue();
		}
	}
	
	//快递泡重
	@Transient
	public Integer getExpressOverGw(){
		Float rs = (getExpressGw()-getTranGw())*100/getTranGw();
		return rs.intValue();
	}
	
	//产品实际运输重量  海运重量
	@Transient
	public Float getTranGw(){
		Float rs = 0f;
		BigDecimal bd = gw.divide(new BigDecimal(packQuantity),2, BigDecimal.ROUND_UP);  
		rs = bd.floatValue();
		return rs;
	}
	
	
	//空运重量
	@Transient
	public Float getAirGw(){
		Float rs = 0f;
		BigDecimal volumeWg =packLength.multiply(packWidth).multiply(packHeight).divide(new BigDecimal(packQuantity*6000),2,BigDecimal.ROUND_UP);
		rs=volumeWg.floatValue();
		if(volumeWg.floatValue()<getTranGw()){
			rs=getTranGw();
		}
		return rs;
	}
	
	//空运泡重
	@Transient
	public Integer getAirOverGw(){
		Float rs = (getAirGw()-getTranGw())*100/getTranGw();
		return rs.intValue();
	}
	
	
	
	
	@Transient
	public String getLink(){
		return "/data/site/psiproduct/compressPic/"+StringUtils.substringAfterLast(image, "/");
	}

	@Transient
	public String getColorPlatform() {
		return colorPlatform;
	}

	@Transient
	public void setColorPlatform(String colorPlatform) {
		this.colorPlatform = colorPlatform;
	}

	public void setBarcodeMap2(Map<String, Map<String, PsiBarcode>> barcodeMap2) {
		this.barcodeMap2 = barcodeMap2;
	}

	public String getFactoryCertification() {
		return factoryCertification;
	}

	public void setFactoryCertification(String factoryCertification) {
		this.factoryCertification = factoryCertification;
	}

	public String getInateckCertification() {
		return inateckCertification;
	}

	public void setInateckCertification(String inateckCertification) {
		this.inateckCertification = inateckCertification;
	}

	
	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}
	
	
	public String getSignedSample() {
		return signedSample;
	}

	public void setSignedSample(String signedSample) {
		this.signedSample = signedSample;
	}

	public String getCheckList() {
		return checkList;
	}

	public void setCheckList(String checkList) {
		this.checkList = checkList;
	}

	
	public String getTechFile() {
		return techFile;
	}

	public void setTechFile(String techFile) {
		this.techFile = techFile;
	}

	public Integer getTaxRefund() {
		return taxRefund;
	}

	public void setTaxRefund(Integer taxRefund) {
		this.taxRefund = taxRefund;
	}

	@Transient
	public Integer getPackNumsByCountry(String countryCode){
		Integer pack =this.packQuantity;
		return pack;
	}

	public String getImproveRemark() {
		return improveRemark;
	}

	public void setImproveRemark(String improveRemark) {
		this.improveRemark = improveRemark;
	}

	public String getHasMagnetic() {
		return hasMagnetic;
	}

	public void setHasMagnetic(String hasMagnetic) {
		this.hasMagnetic = hasMagnetic;
	}

	public String getReviewSta() {
		return reviewSta;
	}

	public void setReviewSta(String reviewSta) {
		this.reviewSta = reviewSta;
	}

	public String getCheckState() {
		return checkState;
	}

	public void setCheckState(String checkState) {
		this.checkState = checkState;
	}

	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "check_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCheckUser() {
		return checkUser;
	}

	public void setCheckUser(User checkUser) {
		this.checkUser = checkUser;
	}

	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "check_list_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCheckListUser() {
		return checkListUser;
	}

	public void setCheckListUser(User checkListUser) {
		this.checkListUser = checkListUser;
	}

	public String getMxHscode() {
		return mxHscode;
	}

	public void setMxHscode(String mxHscode) {
		this.mxHscode = mxHscode;
	}

	public Float getMxCustomDuty() {
		return mxCustomDuty;
	}

	public void setMxCustomDuty(Float mxCustomDuty) {
		this.mxCustomDuty = mxCustomDuty;
	}

	public Float getMxImportDuty() {
		return mxImportDuty;
	}

	public void setMxImportDuty(Float mxImportDuty) {
		this.mxImportDuty = mxImportDuty;
	}

	public BigDecimal getProductPackWeight() {
		return productPackWeight;
	}

	public void setProductPackWeight(BigDecimal productPackWeight) {
		this.productPackWeight = productPackWeight;
	}

	@Transient
	public boolean getFanOu(){
		boolean fanOu = "0".equals(hasPower)?true:false;
		if (fanOu && "keyboard".equals(type.toLowerCase())) {
			fanOu = false;	//keyboard不带电源也不能泛欧,接口不一致
		}
		return fanOu;
	}
	@Transient
	public BigDecimal getMinPackSize(){
		if(packHeight.compareTo(packLength)>0){
			if(packLength.compareTo(packWidth)>0){
				return packWidth;
			}else{
				return packLength;
			}
		}else{
			if(packHeight.compareTo(packWidth)>0){
				return packWidth;
			}else{
				return packHeight;
			}
		}
	}

	public String getComponents() {
		return components;
	}

	public void setComponents(String components) {
		this.components = components;
	}
	
}
