package com.springrain.erp.modules.amazoninfo.entity;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.web.util.HtmlUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "amazoninfo_posts_relationship_feed")
public class AmazonPostsRelationshipFeed {
	private Integer id;
	private String state;
	private String result;
	private String resultFile;
	private User createUser;
	private Date createDate;
	private String country;
	private String operat = "1";  //1.解绑 2.绑定 3.修改颜色大小
	
	private List<AmazonPostsRelationshipChange> items = Lists.newArrayList();
	private String parentSku;//新sku
	private String requestId;
	private Date endDate;
	
	private User checkUser;
	private Date checkDate;
	private String accountName;
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	
	@ManyToOne()
	@JoinColumn(name = "check_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCheckUser() {
		return checkUser;
	}
	public void setCheckUser(User checkUser) {
		this.checkUser = checkUser;
	}
	
	@Temporal(TemporalType.DATE)
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getCheckDate() {
		return checkDate;
	}
	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}
	
	@Transient
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		if(StringUtils.isNotEmpty(this.requestId)){
			this.requestId = this.requestId+","+requestId;
		}else{
			this.requestId = requestId;
		}
	}
	
	public String getOperat() {
		return operat;
	}
	public void setOperat(String operat) {
		this.operat = operat;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		if(StringUtils.isNotEmpty(this.result)){
			this.result = this.result+"<br/><br/>"+result;
		}else{
			this.result = result;
		}
	}
	public String getResultFile() {
		return resultFile;
	}
	public void setResultFile(String resultFile) {
		this.resultFile = resultFile;
	}
	
	@ManyToOne()
	@JoinColumn(name = "create_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getCreateUser() {
		return createUser;
	}
	public void setCreateUser(User createUser) {
		this.createUser = createUser;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	

	public String getParentSku() {
		return parentSku;
	}
	public void setParentSku(String parentSku) {
		this.parentSku = parentSku;
	}
	
	@OneToMany(mappedBy = "amazonPostsRelationshipFeed",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@NotFound(action = NotFoundAction.IGNORE)
	public List<AmazonPostsRelationshipChange> getItems() {
		return items;
	}
	public void setItems(List<AmazonPostsRelationshipChange> items) {
		this.items = items;
	}

	@Transient
	public String getStateStr() {
		if("1".equals(state)){
			return "发送到服务器...";
		}else if("2".equals(state)){
			return "等待反馈结果...";
		}else if("3".equals(state)){
			return "已完成";
		}else if("4".equals(state)){
			if(StringUtils.isEmpty(requestId)){
				return "上传失败,可能亚马逊服务不可用或网络原因,请稍后重试！";
			}else{
				return "累计10次获取结果失败,请自行观察上传结果;可能亚马逊服务不稳定或网络原因！";
			}
		}else if("0".equals(state)){
			return "待审核";
		}else if("5".equals(state)){
			return "<b style='color:red;'>审核未通过</b>";
		}
		return state;
	}

	@Transient
	public String getResultStr() {
		if(StringUtils.isNotBlank(result)){
			String substr= "&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;";
			int count = 0;//次数
			int start = 0;
			while (result.indexOf(substr, start) >= 0 && start < result.length()) {
				count++;
				start = result.indexOf(substr, start) + substr.length();
			}
			if(result.contains("设置产品属性结果")&&count==2&&result.contains("修改帖子关系结果")){
				return "<b style='color:green;'>修改成功</b>";
			}else if(result.contains("设置产品属性结果")&&count==1&&!result.contains("修改帖子关系结果")){
				return "<b style='color:green;'>修改成功</b>";
			}else if(!result.contains("设置产品属性结果")&&count==1&&result.contains("修改帖子关系结果")){
				return "<b style='color:green;'>修改成功</b>";
			}else{
				return "<b style='color:red;'>修改失败或部分失败</b>";
			}
		}else{
			return null;
		}
		
	}
	
	public File genUpdateProductXmlFile(String path,Map<String,String> allSkuMap) {
		Document doc = DocumentHelper.createDocument();
		Element rootElmt = doc.addElement("AmazonEnvelope");
		Element headerEl = rootElmt.addElement("Header");
		headerEl.addElement("DocumentVersion").addText("1.01");
		headerEl.addElement("MerchantIdentifier").addText(
				"ListingsContentHandler");
		rootElmt.addElement("MessageType").addText("Product");
		int i = 0;
		for (AmazonPostsRelationshipChange item : items) {
			if(item.getColor()!=null || item.getSize()!=null){
				String allSku = null;
				if(allSkuMap!=null){
					allSku = allSkuMap.get(item.getSku());
					if(allSku==null){
						allSku = item.getSku();
					}
				}else{
					allSku = item.getSku();
				}
				for (String sku : allSku.split(",")) {
					Element msg = rootElmt.addElement("Message");
					msg.addElement("MessageID").addText(++i + "");
					msg.addElement("OperationType").addText("PartialUpdate");
					Element product = msg.addElement("Product");
					product.addElement("SKU").addText(sku);
					
					if(country.startsWith("com")){
						Element variationData = null;
						Element catalogType1Data = null;
						if(!"Home".equals(item.getCatalogType1())){
							catalogType1Data = product.addElement("ProductData").addElement(item.getCatalogType1()==null?"CE":item.getCatalogType1()).addElement("ProductType")
									 .addElement(item.getCatalogType2()==null?"ConsumerElectronics":item.getCatalogType2());
							variationData = catalogType1Data.addElement("VariationData");
						}else{
							catalogType1Data = product.addElement("ProductData").addElement(item.getCatalogType1());
							variationData = catalogType1Data .addElement("VariationData");
						}
						variationData.addElement("Parentage").addText("child");
						if(item.getColor()!=null && item.getSize()!=null){
							if("OfficeElectronics".equals(item.getCatalogType2())){
								variationData.addElement("VariationTheme").addText("SizeColor");
								catalogType1Data.addElement("Color").addText(HtmlUtils.htmlUnescape(item.getColor()));
							}else{
								variationData.addElement("VariationTheme").addText("Size-Color");
								catalogType1Data.addElement("Size").addText(HtmlUtils.htmlUnescape(item.getSize()));
								catalogType1Data.addElement("Color").addText(HtmlUtils.htmlUnescape(item.getColor()));
							}
						}else if(item.getSize()!=null){
							variationData.addElement("VariationTheme").addText("Size");
							catalogType1Data.addElement("Size").addText(HtmlUtils.htmlUnescape(item.getSize()));
						}else if(item.getColor()!=null){
							variationData.addElement("VariationTheme").addText("Color");
							catalogType1Data.addElement("Color").addText(HtmlUtils.htmlUnescape(item.getColor()));
						}
					}else{
						Element variationData =product.addElement("ProductData").addElement("Home").addElement("VariationData");
						variationData.addElement("Parentage").addText("child");
						if(item.getColor()!=null && item.getSize()!=null){
							variationData.addElement("VariationTheme").addText("Size-Color");
							variationData.addElement("Size").addText(HtmlUtils.htmlUnescape(item.getSize()));
							variationData.addElement("Color").addText(HtmlUtils.htmlUnescape(item.getColor()));
						}else if(item.getSize()!=null){
							variationData.addElement("VariationTheme").addText("Size");
							variationData.addElement("Size").addText(HtmlUtils.htmlUnescape(item.getSize()));
						}else if(item.getColor()!=null){
							variationData.addElement("VariationTheme").addText("Color");
							variationData.addElement("Color").addText(HtmlUtils.htmlUnescape(item.getColor()));
						}
					}
				}
			}
		}
		if(i==0){
			return null;
		}
		File rs = new File(path + "/data1.xml");
		try {
			// 定义输出流的目的地
			FileWriter fw = new FileWriter(rs);
			// 定义输出格式和字符集
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");
			// 定义用于输出xml文件的XMLWriter对象
			XMLWriter xmlWriter = new XMLWriter(fw, format);
			xmlWriter.write(doc);
			xmlWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public File genUpdateRelationshipXmlFile(String path,Map<String,String> allSkuMap) {
		Document doc = DocumentHelper.createDocument();
		Element rootElmt = doc.addElement("AmazonEnvelope");
		Element headerEl = rootElmt.addElement("Header");
		headerEl.addElement("DocumentVersion").addText("1.01");
		headerEl.addElement("MerchantIdentifier").addText(
				"ListingsContentHandler");
		rootElmt.addElement("MessageType").addText("Relationship");
		int i = 0;
		for (AmazonPostsRelationshipChange item : items) {
			if(StringUtils.isNotEmpty(item.getParentSku())){
				String allSku = null;
				if(allSkuMap!=null){
					allSku = allSkuMap.get(item.getSku());
					if(allSku==null){
						allSku = item.getSku();
					}
				}else{
					allSku = item.getSku();
				}
				for (String sku : allSku.split(",")) {
					if(sku.toLowerCase().contains("-old")||sku.toLowerCase().contains("-local")){
						continue;
					}
					Element msg = rootElmt.addElement("Message");
					msg.addElement("MessageID").addText(++i + "");
					msg.addElement("OperationType").addText("Delete");
					Element relationship = msg.addElement("Relationship");
					relationship.addElement("ParentSKU").addText(item.getParentSku());
					Element relation=relationship.addElement("Relation");
					relation.addElement("SKU").addText(sku);
					relation.addElement("Type").addText("Variation");
				}
			}
			if("2".equals(operat)){
				Element msg = rootElmt.addElement("Message");
				msg.addElement("MessageID").addText(++i + "");
				msg.addElement("OperationType").addText("Update");
				Element relationship = msg.addElement("Relationship");
				relationship.addElement("ParentSKU").addText(HtmlUtils.htmlUnescape(parentSku));
				Element relation=relationship.addElement("Relation");
				relation.addElement("SKU").addText(item.getSku());
				relation.addElement("Type").addText("Variation");
			}
		}
		File rs = new File(path + "/data2.xml");
		try {
			// 定义输出流的目的地
			FileWriter fw = new FileWriter(rs);
			// 定义输出格式和字符集
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("utf-8");
			// 定义用于输出xml文件的XMLWriter对象
			XMLWriter xmlWriter = new XMLWriter(fw, format);
			xmlWriter.write(doc);
			xmlWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rs;
	}
}
