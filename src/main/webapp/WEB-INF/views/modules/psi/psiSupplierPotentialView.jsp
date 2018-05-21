<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<meta name="decorator" content="default"/>
<title>psisupplierPotentialView</title>
<script type="text/javascript">
var _hmt = _hmt || [];
(function() {
  var hm = document.createElement("script");
  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
  var s = document.getElementsByTagName("script")[0]; 
  s.parentNode.insertBefore(hm, s);
})();

	function back(){
		window.location.href="${ctx}/psi/supplierPotential/list";
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/supplierPotential">潜在供应商列表</a></li>
		<li class="active"><a
			href="${ctx}/psi/supplierPotential/form?id=${supplierPotential.id}">详细信息</a></li>
	</ul>
	<br/>
	<tags:message content="${message}" />
	<form:form id="inputForm" modelAttribute="supplierPotential" action="${ctx}/psi/supplierPotential/"
		class="form-horizontal">
		<form:hidden path="id"/>
		<blockquote>
			<p style="font-size: 14px">潜在供应商信息</p>
		</blockquote>
		<div class="control-group">
			<label class="control-label">名称</label>
			<div class="controls">${supplierPotential.name}</div>
		</div>
		<div class="control-group">
			<label class="control-label">简称</label>
			<div class="controls">${supplierPotential.nikename}</div>
		</div>
		<div class="control-group">
			<label class="control-label">中文简称</label>
			<div class="controls">${supplierPotential.shortName}</div>
		</div>
		<div class="control-group">
			<label class="control-label">类型</label>
			<div class="controls">${supplierPotential.typeName}</div>
		</div>
		<div class="control-group">
			<label class="control-label">地址</label>
			<div class="controls">${supplierPotential.address}</div>
		</div>
		<div class="control-group">
			<label class="control-label">网站地址</label>
			<div class="controls"><a href="${supplierPotential.site}" target="_blank">${supplierPotential.site}</a></div>
		</div>
		<div class="control-group">
			<label class="control-label">定金</label>
			<div class="controls">${supplierPotential.deposit}%</div>
		</div>
		<div class="control-group">
			<label class="control-label">支付货币类型</label>
			<div class="controls">${supplierPotential.currencyType}</div>
		</div>
		
		<blockquote>
			<p style="font-size: 14px">联系人信息</p>
		</blockquote>
		<div class="control-group">
			<label class="control-label">联系人</label>
			<div class="controls">${supplierPotential.contact}</div>
		</div>
		<div class="control-group">
			<label class="control-label">电话</label>
			<div class="controls">${supplierPotential.phone}</div>
		</div>
		<div class="control-group">
			<label class="control-label">邮箱</label>
			<div class="controls">${supplierPotential.mail}</div>
		</div>
		<div class="control-group">
			<label class="control-label">QQ</label>
			<div class="controls">${supplierPotential.qq}</div>
		</div>
		<div class="control-group">
			<label class="control-label">备注</label>
			<div class="controls">${supplierPotential.memo}</div>
		</div>
		<blockquote>
			<p style="font-size: 14px">文件信息</p>
		</blockquote>
		<c:if test="${not empty supplierPotential.suffixName}">
		<div class="control-group">
			<label class="control-label">已上传文件</label>
			<div class="controls">
			  <c:forEach items="${fn:split(supplierPotential.suffixName,'-')}" var="attFile" varStatus="i">
				<c:choose>
					<c:when test="${i.index eq 0 && attFile ne 'BL' }"><a target="_blank" href="<c:url value='/data/site/psi/supplierPotential/${supplierPotential.id}/${supplierPotential.id}_BL${attFile }'/>" >营业执照复印件</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
					<c:when test="${i.index eq 1 && attFile ne 'TR' }"><a target="_blank" href="<c:url value='/data/site/psi/supplierPotential/${supplierPotential.id}/${supplierPotential.id}_TR${attFile }'/>" >税务登记复印件</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
					<c:when test="${i.index eq 2 && attFile ne 'ISO' }"><a target="_blank" href="<c:url value='/data/site/psi/supplierPotential/${supplierPotential.id}/${supplierPotential.id}_ISO${attFile }'/>" >ISO认证及其他认证复印件</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
					<c:when test="${i.index eq 3 && attFile ne 'BI' }"><a target="_blank" href="<c:url value='/data/site/psi/supplierPotential/${supplierPotential.id}/${supplierPotential.id}_BI${attFile }'/>" >银行资料</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
					<c:when test="${i.index eq 4 && attFile ne 'PPT' }"><a target="_blank" href="<c:url value='/data/site/psi/supplierPotential/${supplierPotential.id}/${supplierPotential.id}_PPT${attFile }'/>" >公司介绍PPT</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
					<c:when test="${i.index eq 5 && attFile ne 'BC' }"><a target="_blank" href="<c:url value='/data/site/psi/supplierPotential/${supplierPotential.id}/${supplierPotential.id}_BC${attFile }'/>" >基本资料统计</a>&nbsp;&nbsp;&nbsp;&nbsp;</c:when>
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
				<fmt:formatDate value="${fns:getDateByInt(supplierPotential.addtime)}"
					type="both" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">编辑时间</label>
			<div class="controls">
				<c:if test="${not empty supplierPotential.uptime}">
				<fmt:formatDate value="${fns:getDateByInt(supplierPotential.uptime)}"
					type="both" />
				</c:if>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnCancel" class="btn btn-primary" type="button" value="<spring:message code="sys_but_back"/>" onclick="back();"/>
		</div>
	</form:form>
</body>
</html>