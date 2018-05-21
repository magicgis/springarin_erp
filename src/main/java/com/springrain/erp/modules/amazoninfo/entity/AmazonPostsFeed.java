package com.springrain.erp.modules.amazoninfo.entity;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.springrain.erp.common.utils.StringUtils;
import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "amazoninfo_posts_feed")
public class AmazonPostsFeed {
	private Integer id;
	private String state;
	private String result;
	private String resultFile;
	private User createUser;
	private Date createDate;
	private String country;
	private List<AmazonPostsChange> items = Lists.newArrayList();
	private String requestId;
	private Date endDate;
	private String sku;
	private String operateType;//0：编辑  1:新增普通帖  2：新建父帖 3：删帖  4帖子类型转换 5帖子一键还原  6复制贴 7 cross帖
	private String accountName;
	
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public String getOperateType() {
		return operateType;
	}
	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}
	
	@Transient
	public String getSku() {
		return sku;
	}
	public void setSku(String sku) {
		this.sku = sku;
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
		this.result = result;
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
	

	@OneToMany(mappedBy = "amazonPostsFeed",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@NotFound(action = NotFoundAction.IGNORE)
	public List<AmazonPostsChange> getItems() {
		return items;
	}
	public void setItems(List<AmazonPostsChange> items) {
		this.items = items;
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
			if(!"4".equals(operateType)&&!"5".equals(operateType)){
				if(result.contains("帖子编辑结果为")&&count==3&&result.contains("设置帖子")&&result.contains("修改帖子价格")){
					return "<b style='color:green;'>操作成功</b>";
				}else if(result.contains("帖子编辑结果为")&&count==1&&!result.contains("设置帖子")&&!result.contains("修改帖子价格")){
					return "<b style='color:green;'>操作成功</b>";
				}else if(result.contains("帖子编辑结果为")&&count==2&&result.contains("设置帖子")&&!result.contains("修改帖子价格")){
					return "<b style='color:green;'>操作成功</b>";
				}else if(result.contains("帖子编辑结果为")&&count==2&&!result.contains("设置帖子")&&result.contains("修改帖子价格")){
					return "<b style='color:green;'>操作成功</b>";
				}else if("0".equals(operateType)&&!result.contains("帖子编辑结果为")&&count==1&&!result.contains("设置帖子")&&!result.contains("修改帖子价格")){
					return "<b style='color:green;'>操作成功</b>";
				}else{
					return "<b style='color:red;'>操作失败或部分失败</b>";
				}
			}else if("5".equals(operateType)){
				if(result.contains("帖子编辑结果为")&&result.contains("帖子恢复结果")&&count==4&&result.contains("设置帖子")&&result.contains("修改帖子价格")){
					return "<b style='color:green;'>操作成功</b>";
				}else if(result.contains("帖子编辑结果为")&&result.contains("帖子恢复结果")&&count==2&&!result.contains("设置帖子")&&!result.contains("修改帖子价格")){
					return "<b style='color:green;'>操作成功</b>";
				}else if(result.contains("帖子编辑结果为")&&result.contains("帖子恢复结果")&&count==3&&result.contains("设置帖子")&&!result.contains("修改帖子价格")){
					return "<b style='color:green;'>操作成功</b>";
				}else if(result.contains("帖子编辑结果为")&&result.contains("帖子恢复结果")&&count==3&&!result.contains("设置帖子")&&result.contains("修改帖子价格")){
					return "<b style='color:green;'>操作成功</b>";
				}else{
					return "<b style='color:red;'>操作失败或部分失败</b>";
				}
			}else if("4".equals(operateType)&&count==1){
				return "<b style='color:green;'>操作成功</b>";
			}else{
				return "<b style='color:red;'>操作失败或部分失败</b>";
			}
		}else{
			return null;
		}
		
	}

	@Transient
	public String getResultStr2() {
		if(StringUtils.isNotBlank(result)){
			String substr= "&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;";
			int count = 0;//次数
			int start = 0;
			while (result.indexOf(substr, start) >= 0 && start < result.length()) {
				count++;
				start = result.indexOf(substr, start) + substr.length();
			}
			if(!"4".equals(operateType)&&!"5".equals(operateType)){
				if(result.contains("帖子编辑结果为")&&count==3&&result.contains("设置帖子")&&result.contains("修改帖子价格")){
					return "操作成功";
				}else if(result.contains("帖子编辑结果为")&&count==1&&!result.contains("设置帖子")&&!result.contains("修改帖子价格")){
					return "操作成功";
				}else if(result.contains("帖子编辑结果为")&&count==2&&result.contains("设置帖子")&&!result.contains("修改帖子价格")){
					return "操作成功";
				}else if(result.contains("帖子编辑结果为")&&count==2&&!result.contains("设置帖子")&&result.contains("修改帖子价格")){
					return "操作成功";
				}else if("0".equals(operateType)&&!result.contains("帖子编辑结果为")&&count==1&&!result.contains("设置帖子")&&!result.contains("修改帖子价格")){
					return "操作成功";
				}else{
					return "操作失败或部分失败";
				}
			}else if("5".equals(operateType)){
				if(result.contains("帖子编辑结果为")&&result.contains("帖子恢复结果")&&count==4&&result.contains("设置帖子")&&result.contains("修改帖子价格")){
					return "操作成功";
				}else if(result.contains("帖子编辑结果为")&&result.contains("帖子恢复结果")&&count==2&&!result.contains("设置帖子")&&!result.contains("修改帖子价格")){
					return "操作成功";
				}else if(result.contains("帖子编辑结果为")&&result.contains("帖子恢复结果")&&count==3&&result.contains("设置帖子")&&!result.contains("修改帖子价格")){
					return "操作成功";
				}else if(result.contains("帖子编辑结果为")&&result.contains("帖子恢复结果")&&count==3&&!result.contains("设置帖子")&&result.contains("修改帖子价格")){
					return "操作成功";
				}else{
					return "操作失败或部分失败";
				}
			}else if("4".equals(operateType)&&count==1){
				return "操作成功";
			}else{
				return "操作失败或部分失败";
			}
		}else if("4".equals(state)){
			return "累计10次获取结果失败";
		}else{
			return null;
		}
		
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
		}
		return state;
	}

	public File genXmlFile(String path) {
		Document doc = DocumentHelper.createDocument();
		Element rootElmt = doc.addElement("AmazonEnvelope");
		Element headerEl = rootElmt.addElement("Header");
		headerEl.addElement("DocumentVersion").addText("1.01");
		headerEl.addElement("MerchantIdentifier").addText(
				"ListingsContentHandler");
		rootElmt.addElement("MessageType").addText("Product");
		int i = 0;
		for (AmazonPostsChange item : items) {
			String sku = item.getSku();
			for (String skuStr : sku.split(",")) {
				Element msg = rootElmt.addElement("Message");
				msg.addElement("MessageID").addText(++i + "");
				if("0".equals(operateType)){
					msg.addElement("OperationType").addText("PartialUpdate");
				}else if("1,6,7,8".contains(operateType)){
					msg.addElement("OperationType").addText("Update");
				}else if("2".equals(operateType)){
					msg.addElement("OperationType").addText("Update");
				}else if("3,5".contains(operateType)){
					msg.addElement("OperationType").addText("Delete");
				}else if("-5".equals(operateType)){
					msg.addElement("OperationType").addText("Update");
				}
				item.genProductEl(msg.addElement("Product"),skuStr);
			}
		}
		File rs = new File(path + "/data.xml");
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
