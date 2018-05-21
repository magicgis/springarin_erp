<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ include file="/WEB-INF/views/include/dialog.jsp"%>
<html>
<head>
<meta name="decorator" content="default" />
<title>psisupplierView</title>
<style type="text/css">
#uploadPreview {
	width: 120px;
	height: 120px;
	background-position: center center;
	background-size: cover;
	border: 4px solid #fff;
	-webkit-box-shadow: 0 0 1px 1px rgba(0, 0, 0, .3);
	display: inline-block;
}

pre {
	border-style: none
}
</style>
<script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script>
<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();

	$(document).ready(function() {
       CKEDITOR.replace("templateContent",{height:'280px',width:'700px',toolbarStartupExpanded:false,startupFocus:true});	
	});
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a  href="${ctx}/custom/emailTemplate"><spring:message code='custom_email_template_list'/></a></li>
		<li class="active"><a href="${ctx}/custom/emailTemplate/view?id=${customEmailTemplate.id}"><spring:message code='custom_email_template_detail'/></a></li>
	</ul>
	<br />
	<tags:message content="${message}" />
	<form:form id="inputForm" modelAttribute="customEmailTemplate"
		action="${ctx}/psi/product/save" method="post" class="form-horizontal"
		enctype="multipart/form-data">
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_template_type'/></label>
			 <c:choose>
			    <c:when test="${customEmailTemplate.templateType eq '0'}">
			      <div class="controls"><spring:message code='custom_email_template_sys'/></div>
			    </c:when>
			    <c:when test="${customEmailTemplate.templateType eq '1'}">
			      <div class="controls"><spring:message code='custom_email_template_share'/></div>
			    </c:when>
			    <c:when test="${customEmailTemplate.templateType eq '3'}">
			      <div class="controls"><spring:message code='custom_email_template_after'/></div>
			    </c:when>
			    <c:when test="${customEmailTemplate.templateType eq '4'}">
			      <div class="controls"><spring:message code='custom_email_template_review'/></div>
			    </c:when>
			    <c:when test="${customEmailTemplate.templateType eq '5'}">
			      <div class="controls"><spring:message code='custom_email_template_manual'/></div>
			    </c:when>
			    <c:when test="${customEmailTemplate.templateType eq '6'}">
			      <div class="controls"><spring:message code='custom_email_template_feedback'/></div>
			    </c:when>
			    <c:otherwise>
			      <div class="controls"><spring:message code='custom_email_template_self'/></div>
			    </c:otherwise>
			 </c:choose>
			<div class="controls"></div>
		</div>
		
		 <c:if test="${customEmailTemplate.templateType eq '1'}">
		    <div class="control-group">
			<label class="control-label"><spring:message code='custom_email_template_group'/></label>
			<div class="controls">${customEmailTemplate.role.name}</div>
		</div>
		 </c:if>
				

		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_template_name'/></label>
			<div class="controls">${customEmailTemplate.templateName}</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_template_createBy'/></label>
			<div class="controls">${customEmailTemplate.createBy}</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_template_createTime'/></label>
			<div class="controls">${customEmailTemplate.createDate}</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_template_lastUpdateBy'/></label>
			<div class="controls">${customEmailTemplate.lastUpdateBy.name}</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_template_subject'/></label>
			<div class="controls">${customEmailTemplate.templateSubject}</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_template_content'/></label>
			<div class="controls">
			<textarea name="templateContent"  htmlEscape="false" maxlength="5000" style="margin: 0px; width: 600px; height:300px;" disabled="disabled">
			  ${customEmailTemplate.templateContent}
			</textarea>				
			</div>
		</div>		
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="window.location.href ='${ctx}/custom/emailTemplate'"/>
		</div>
	</form:form>
	
</body>
</html>