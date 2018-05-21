<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html> 
<head>
<meta name="decorator" content="default"/>
<title>psisupplierView</title>
<script type="text/javascript">
var _hmt = _hmt || [];
(function() {
  var hm = document.createElement("script");
  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
  var s = document.getElementsByTagName("script")[0]; 
  s.parentNode.insertBefore(hm, s);
})();

	function back(){
		window.location.href="${ctx}/psi/supplier/list";
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/supplier">供应商列表</a></li>
		<li class="active"><a
			href="${ctx}/psi/supplier/form?id=${supplier.id}">详细信息</a></li>
	</ul>
	<br/>
	<tags:message content="${message}" />
	<form:form id="inputForm" modelAttribute="supplier" action="${ctx}/psi/supplier/"
		class="form-horizontal">
		<form:hidden path="id"/>
		<blockquote>
			<p style="font-size: 14px">供应商信息</p>
		</blockquote>
		<div class="control-group">
			<label class="control-label">名称</label>
			<div class="controls">${supplier.name}</div>
		</div>
		<div class="control-group">
			<label class="control-label">简称</label>
			<div class="controls">${supplier.nikename}</div>
		</div>
		<div class="control-group">
			<label class="control-label">中文简称</label>
			<div class="controls">${supplier.shortName}</div>
		</div>
		<div class="control-group">
			<label class="control-label">类型</label>
			<div class="controls">${supplier.typeName}</div>
		</div>
		<div class="control-group">
			<label class="control-label">地址</label>
			<div class="controls">${supplier.address}</div>
		</div>
		<div class="control-group">
			<label class="control-label">网站地址</label>
			<div class="controls"><a href="${supplier.site}" target="_blank">${supplier.site}</a></div>
		</div>
		<div class="control-group">
			<label class="control-label">税率</label>
			<div class="controls">
				${supplier.taxRate}%
			</div>
		</div>
		
		<div class="control-group" >
			<label class="control-label">相关产品</label>
			<div class="controls">
				<c:forEach items="${supplier.products}" var="supplierProduct">
					${supplierProduct.product.model}     &nbsp;&nbsp;     
				</c:forEach>
			</div>
			
		</div>
		
		
		<blockquote>
			<p style="font-size: 14px">付款信息</p>
		</blockquote>
		<div class="control-group">
			<label class="control-label">定金</label>
			<div class="controls">${supplier.deposit}%</div>
		</div>
		<div class="control-group">
			<label class="control-label">支付货币类型</label>
			<div class="controls">${supplier.currencyType}</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">月结情况</label>
			<div class="controls">
			 <c:choose>
			 	<c:when test="${empty supplier.payType}">非月结</c:when>
			 	<c:when test="${supplier.payType eq '1'}">次月1号</c:when>
			 	<c:when test="${supplier.payType eq '2'}">次月5号</c:when>
			 	<c:when test="${supplier.payType eq '3'}">次月10号</c:when>
			 	<c:when test="${supplier.payType eq '4'}">次月15号</c:when>
			 	<c:when test="${supplier.payType eq '5'}">次月20号</c:when>
			 	<c:when test="${supplier.payType eq '6'}">次月25号</c:when>
			 	
			 	<c:when test="${supplier.payType eq '11'}">次次月1号</c:when>
			 	<c:when test="${supplier.payType eq '12'}">次次月5号</c:when>
			 	<c:when test="${supplier.payType eq '13'}">次次月10号</c:when>
			 	<c:when test="${supplier.payType eq '14'}">次次月15号</c:when>
			 	<c:when test="${supplier.payType eq '15'}">次次月20号</c:when>
			 	<c:when test="${supplier.payType eq '16'}">次次月25号</c:when>
			 </c:choose>
			</div>
		</div>
		<div class="control-group" >
			<div style="float:left;width:40%">
				<label class="control-label">尾款第一次付款比例</label>
				<div class="controls">
					${supplier.balanceRate1}%
				</div>
			</div>
			<div style="float:left;width:60%">   
				<label class="control-label">收货后几天付款算延迟</label>
				<div class="controls">
					${supplier.balanceDelay1}  
				</div>
			</div>
		</div>
		
		<div class="control-group" >
			<div style="float:left;width:40%">
				<label class="control-label">尾款第二次付款比例</label>
				<div class="controls">
					${supplier.balanceRate2}%
				</div>
			</div>
			<div style="float:left;width:60%">   
				<label class="control-label">收货后几天付款算延迟</label>
				<div class="controls">
					${supplier.balanceDelay2}  
				</div>
			</div>
		</div>
		
		<blockquote>
			<p style="font-size: 14px">账户信息</p>
		</blockquote>
		<div class="control-group">
			<label class="control-label">对公账号</label>
			<div class="controls" style="width:600px">${supplier.publicAccount}</div>
		</div>
		<div class="control-group">
			<label class="control-label">美金账号1</label>
			<div class="controls" style="width:600px">${supplier.dollarAccount1}</div>
		</div>
		<div class="control-group">
			<label class="control-label">美金账号2</label>
			<div class="controls" style="width:600px">${supplier.dollarAccount2}</div>
		</div>
		<div class="control-group">
			<label class="control-label">人民币账号1</label>
			<div class="controls" style="width:600px">${supplier.rmbAccount1}</div>
		</div>
		<div class="control-group">
			<label class="control-label">人民币账号2</label>
			<div class="controls" style="width:600px">${supplier.rmbAccount2}</div>
		</div>
		<blockquote>
			<p style="font-size: 14px">联系人信息</p>
		</blockquote>
		<div class="control-group">
			<label class="control-label">联系人</label>
			<div class="controls">${supplier.contact}</div>
		</div>
		<div class="control-group">
			<label class="control-label">电话</label>
			<div class="controls">${supplier.phone}</div>
		</div>
		<div class="control-group">
			<label class="control-label">邮箱</label>
			<div class="controls">${supplier.mail}</div>
		</div>
		<div class="control-group">
			<label class="control-label">QQ</label>
			<div class="controls">${supplier.qq}</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注</label>
			<div class="controls">${supplier.memo}</div>
		</div>
		<div class="control-group">
			<label class="control-label">付款方式备注</label>
			<div class="controls">${supplier.payMark}</div>
		</div>
		<div class="control-group">
			<label class="control-label">付款条款备注</label>
			<div class="controls">${supplier.payRemark}</div>
		</div>
		<blockquote>
			<p style="font-size: 14px">文件信息</p>
		</blockquote>
		<c:if test="${not empty supplier.suffixName}">
		<div class="control-group">
			<label class="control-label">已上传文件</label>
			<div class="controls">
			  <c:forEach items="${fn:split(supplier.suffixName,'-')}" var="attFile" varStatus="i">
				<c:choose>
					<c:when test="${i.index eq 0 && attFile ne 'BL' }"><a target="_blank" href="<c:url value='/data/site/psi/supplier/${supplier.id}/${supplier.id}_BL${attFile }'/>" >营业执照复印件</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
					<c:when test="${i.index eq 1 && attFile ne 'TR' }"><a target="_blank" href="<c:url value='/data/site/psi/supplier/${supplier.id}/${supplier.id}_TR${attFile }'/>" >税务登记复印件</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
					<c:when test="${i.index eq 2 && attFile ne 'ISO' }"><a target="_blank" href="<c:url value='/data/site/psi/supplier/${supplier.id}/${supplier.id}_ISO${attFile }'/>" >ISO认证及其他认证复印件</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
					<c:when test="${i.index eq 3 && attFile ne 'BI' }"><a target="_blank" href="<c:url value='/data/site/psi/supplier/${supplier.id}/${supplier.id}_BI${attFile }'/>" >银行资料</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
					<c:when test="${i.index eq 4 && attFile ne 'PPT' }"><a target="_blank" href="<c:url value='/data/site/psi/supplier/${supplier.id}/${supplier.id}_PPT${attFile }'/>" >公司介绍PPT</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
					<c:when test="${i.index eq 5 && attFile ne 'BC' }"><a target="_blank" href="<c:url value='/data/site/psi/supplier/${supplier.id}/${supplier.id}_BC${attFile }'/>" >基本资料统计</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
					<c:otherwise></c:otherwise>
				</c:choose> 
				</c:forEach>  
			</div>
		</div>
		</c:if>
		<blockquote>
			<p style="font-size: 14px">其他信息</p>
		</blockquote>
		<div class="control-group">
			<label class="control-label">添加时间</label>
			<div class="controls">
				<fmt:formatDate value="${fns:getDateByInt(supplier.addtime)}"
					type="both" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">编辑时间</label>
			<div class="controls">
				<c:if test="${not empty supplier.uptime}">
				<fmt:formatDate value="${fns:getDateByInt(supplier.uptime)}"
					type="both" />
				</c:if>
			</div>
		</div>
	
		<div class="control-group">
			<label class="control-label">合同编号</label>
			<div class="controls" >
				${supplier.contractNo}
			</div>
		</div>
		
		<c:if test="${not empty supplier.attchmentPath}">
		<div class="control-group" >
		<label class="control-label" >已上传合同附件:</label>
			<div class="controls">
				<c:forEach items="${fn:split(supplier.attchmentPath,',')}" var="attchment" varStatus="i">
					<span><a href="${ctx}/psi/supplier/download?fileName=/${attchment}">${supplier.contractNo}-${i.index+1}</a></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</c:forEach> 
			</div>  
		</div>
		</c:if>
		
		
		<c:if test="${not empty supplier.reviewPath}">
		<div class="control-group" >
		<label class="control-label" >已上传供应商考核附件:</label>
			<div class="controls">
				<c:forEach items="${fn:split(supplier.reviewPath,',')}" var="attchment" varStatus="i">
				<a href="${ctx}/psi/supplier/download?fileName=/${attchment}">${fn:substring(attchment,fn:indexOf(attchment,'2'),fn:indexOf(attchment,'.'))} </a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</c:forEach> 
			</div>  
		</div>
		</c:if>
		
		<div class="form-actions">
			<input id="btnCancel" class="btn btn-primary" type="button" value="<spring:message code="sys_but_back"/>" onclick="back();"/>
		</div>
	</form:form>
</body>
</html>