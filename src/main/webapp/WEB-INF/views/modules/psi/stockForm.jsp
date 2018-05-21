<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>仓库管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
		$(document).ready(function() {
			$("#stockSign").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/stock/">仓库列表</a></li>
		<li class="active"><a href="${ctx}/psi/stock/form?id=${stock.id}">仓库<shiro:hasPermission name="psi:stock:edit">${not empty stock.id?'编辑':'新增'}</shiro:hasPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="stock" action="${ctx}/psi/stock/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<tags:message content="${message}"/>
		
		<div class="control-group">
			<label class="control-label"><b>仓库名称:</b></label>
			<div class="controls">
				<form:input path="stockName" htmlEscape="false" maxlength="200" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><b>仓库代号:</b></label>
			<div class="controls">
				<form:input path="stockSign" htmlEscape="false" maxlength="200" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">平台:</label>
			<div class="controls">
				<select  name="platform" >
					<option value="">-请选择平台-</option>
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
						<c:if test="${dic.value ne 'com.unitek'}">
							<option ${stock.platform eq dic.value?'selected':''} value="${dic.value}" >${dic.label}</option>
						</c:if>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><b>类型:</b></label>
			<div class="controls">
				<select  name="type" class="required">
					<option ${stock.type eq '0'?'selected':''} value="1" >FBA仓库</option>
					<option ${stock.type eq '0'?'selected':''} value="0" >本地仓库</option>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><b>仓库容量(m³):</b></label>
			<div class="controls">
				<form:input path="capacity" htmlEscape="false"  class="price required"/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">国家代号:</label>
			<div class="controls">
				<form:input path="countrycode" htmlEscape="false" maxlength="200"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">邮编:</label>
			<div class="controls">
				<form:input path="postalcode" htmlEscape="false" maxlength="200"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">名称:</label>
			<div class="controls">
				<form:input path="name" htmlEscape="false" maxlength="200"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">区/省:</label>
			<div class="controls">
				<form:input path="stateorprovincecode" htmlEscape="false" maxlength="200"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">街道/郡:</label>
			<div class="controls">
				<form:input path="districtorcounty" htmlEscape="false" maxlength="200"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">城市:</label>
			<div class="controls">
				<form:input path="city" htmlEscape="false" maxlength="200"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">地址1:</label>
			<div class="controls">
				<form:input path="addressLine1" htmlEscape="false" maxlength="200"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">地址2:</label>
			<div class="controls">
				<form:input path="addressLine2" htmlEscape="false" maxlength="200"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="psi:stock:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
