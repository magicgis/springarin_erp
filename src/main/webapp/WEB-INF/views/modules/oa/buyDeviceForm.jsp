<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>办公设备申请</title>
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
			$("#name").focus();
			$("#inputForm").validate({
				rules:{price:{number:true,required:true}},
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
		<li><a href="${ctx}/oa/buyDevice/">待办列表</a></li>
		<li><a href="${ctx}/oa/buyDevice/list">办公设备采购列表</a></li>
		<shiro:hasPermission name="oa:buyDevice:view"><li class="active"><a href="${ctx}/oa/buyDevice/form">办公设备采购申请</a></li></shiro:hasPermission>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="buyDevice" action="${ctx}/oa/buyDevice/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">设备采购类型：</label>
			<div class="controls">
				<form:select path="deviceType" >
					<form:options items="${fns:getDictList('officeDeviceType')}" itemLabel="label" itemValue="value" htmlEscape="false" />
				</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">办公设备采购清单：</label>
			<div class="controls">
				<form:textarea path="name" class="required" rows="4" maxlength="255" cssStyle="width:600px"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">总金额：</label>
			<div class="controls">
				<div class="input-prepend input-append">
					<input  class="span2" name="price" size="16" type="text"/>
					<span class="add-on">￥</span>
				</div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">设备采购理由：</label>
			<div class="controls">
				<form:textarea path="reason" class="required" rows="8" maxlength="255" cssStyle="width:600px"/>
			</div>
		</div>
		<div class="form-actions">
			<shiro:hasPermission name="oa:buyDevice:view"><input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;</shiro:hasPermission>
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
