package com.springrain.erp.modules.amazoninfo.entity.order;

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
import com.springrain.erp.modules.amazoninfo.entity.AmazonAccountConfig;
import com.springrain.erp.modules.sys.entity.User;

@Entity
@Table(name = "amazoninfo_refund")
public class AmazonRefund {
	private Integer id;
	private String state;
	private String result;
	private String resultFile;
	private User createUser;
	private Date createDate;
	private Date endDate;
	private Float refundTotal;
	private String amazonOrderId;
	private String country;
	private List<AmazoninfoRefundItem> items = Lists.newArrayList();
	private String requestId;
	private String refundState;
	private User operUser;
	private String isTax;
	private Float  orderTotal;
	private String accountName;
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public Float getOrderTotal() {
		return orderTotal;
	}
	public void setOrderTotal(Float orderTotal) {
		this.orderTotal = orderTotal;
	}
	public String getIsTax() {
		return isTax;
	}
	public void setIsTax(String isTax) {
		this.isTax = isTax;
	}
	public String getRefundState() {
		return refundState;
	}
	public void setRefundState(String refundState) {
		this.refundState = refundState;
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
		this.requestId = requestId;
	}
	public String getAmazonOrderId() {
		return amazonOrderId;
	}
	public void setAmazonOrderId(String amazonOrderId) {
		this.amazonOrderId = amazonOrderId;
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
	
	@ManyToOne()
	@JoinColumn(name = "oper_user")
	@NotFound(action = NotFoundAction.IGNORE)
	public User getOperUser() {
		return operUser;
	}
	public void setOperUser(User operUser) {
		this.operUser = operUser;
	}
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd hh:mm")
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Float getRefundTotal() {
		return refundTotal;
	}
	public void setRefundTotal(Float refundTotal) {
		this.refundTotal = refundTotal;
	}

	@OneToMany(mappedBy = "amazonRefund",fetch=FetchType.LAZY,cascade={javax.persistence.CascadeType.ALL},orphanRemoval=true)
	@NotFound(action = NotFoundAction.IGNORE)
	public List<AmazoninfoRefundItem> getItems() {
		return items;
	}
	public void setItems(List<AmazoninfoRefundItem> items) {
		this.items = items;
	}

	@Transient
	public String getStateStr() {
		if("1".equals(state)){
			return "Sent to the server...";
		}else if("2".equals(state)){
			return "Wait for the feedback results...";
		}else if("3".equals(state)){
			if(result!=null&&result.contains("&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;")){
				return "<H5 style='color:green;'>Successfully</H5>";
			}
			return "<h5 style='color:red;'>Failure</h5>";
		}else if("4".equals(state)){
			if(StringUtils.isEmpty(requestId)){
				return "Upload failed, maybe Amazon service is unavailable or the network itself, please try again later!";
			}else{
				return "Get 10 times the failure result, please carefully observe the upload results; Amazon service possible instability or network reasons!";
			}
		}
		return state;
	}

	@Transient
	public String getRefundStateStr() {
		if("0".equals(refundState)){
			return "Waiting for audit";
		}else if("1".equals(refundState)){
			return "Audited";
		}else if("2".equals(refundState)){
			return "Cancel";
		}
		return refundState;
	}
	
	public File genXmlFile(String path,AmazonAccountConfig config) {
		Document doc = DocumentHelper.createDocument();
		Element rootElmt = doc.addElement("AmazonEnvelope");
		Element headerEl = rootElmt.addElement("Header");
		headerEl.addElement("DocumentVersion").addText("1.01");
		headerEl.addElement("MerchantIdentifier").addText(
				"ListingsContentHandler");
		rootElmt.addElement("MessageType").addText("OrderAdjustment");
		String priceUnit = config.getPriceUnit();
		Element msg = rootElmt.addElement("Message");
		msg.addElement("MessageID").addText("1");
		Element itemsEl = msg.addElement("OrderAdjustment");
		itemsEl.addElement("AmazonOrderID").addText(amazonOrderId);
		for (AmazoninfoRefundItem item : items) {
			Element itemEl = itemsEl.addElement("AdjustedItem");
			itemEl.addElement("AmazonOrderItemCode").addText(item.getOrderItemId());
			itemEl.addElement("AdjustmentReason").addText(item.getRefundReason());
			Element ele = itemEl.addElement("ItemPriceAdjustments").addElement("Component");
			ele.addElement("Type").addText(item.getRefundType());
			ele.addElement("Amount").addAttribute("currency", priceUnit).addText(item.getMoney() + "");
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
