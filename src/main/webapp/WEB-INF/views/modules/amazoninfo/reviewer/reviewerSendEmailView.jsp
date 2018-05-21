<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Reviewer Send Email View</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		
		if(!(top)){
			top = self; 
		}
	
	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/reviewerSendEmail"><spring:message code="custom_email_sentList" /></a></li>
		<li class="active"><a href="${ctx}/amazoninfo/reviewerSendEmail/view?id=${sendEmail.id}"><spring:message code="custom_email_view" /></a></li>
	</ul>
	<div class="form-horizontal">
		<div class="control-group">		
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)"/>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form1'/>:</label>
			<div class="controls">
				${email.createBy.name}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form15'/>:</label>
			<div class="controls">
				${email.sendEmail}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Cc:</label>
			<div class="controls">
				${email.ccToEmail}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Bcc:</label>
			<div class="controls">
				${email.bccToEmail}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form3'/>:</label>
			<div class="controls">
				${email.sendSubject}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form14'/>:</label>
			<div class="controls">
				<fmt:formatDate type="both" value="${email.sentDate}"/>
			</div>
		</div>
		<div class="control-group">
				<label class="control-label">Content:</label>
				<div class="controls">${email.sendContent}</div>
		</div>
		
		<c:if test="${not empty email.sendAttchmentPath}">
			<div class="control-group">
				<label class="control-label">Attchment</label>
				<div class="controls">
					<c:forEach items="${fn:split(email.sendAttchmentPath,',')}" var="attchment">
						<a href="${ctx}/custom/emailManager/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
						&nbsp;&nbsp;&nbsp;  
					</c:forEach>
				</div>
			</div>
		</c:if>
		
		<div class="control-group">
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)"/>
		</div>
	</div>
</body>
</html>