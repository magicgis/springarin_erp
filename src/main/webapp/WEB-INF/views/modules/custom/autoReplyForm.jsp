<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>autoReplay manager</title>
	<meta name="decorator" content="default"/>
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
			var ck = CKEDITOR.replace("content",{height:'280px',toolbarStartupExpanded:false,startupFocus:true});
				  
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li>	
			<c:choose>
				<c:when test="${autoReply.type eq '1'}">
					<li><a href="${ctx}/custom/emailManager/"><spring:message code='custom_email_list'/></a></li>
					<li class="active"><a href="${ctx}/custom/autoReply/form?type=${autoReply.type}"><spring:message code='custom_email_autoReply'/></a></li>
				</c:when>
				<c:when test="${autoReply.type eq '3'}">
					<li><a href="${ctx}/amazoninfo/reviewerEmail/"><spring:message code='custom_email_list'/></a></li>
					<li class="active"><a href="${ctx}/custom/autoReply/form?type=${autoReply.type}"><spring:message code='custom_email_autoReply'/></a></li>
				</c:when>
				<c:otherwise>
					<li><a href="${ctx}/custom/event/"><spring:message code='custom_event_list'/></a></li>
					<li><a href="${ctx}/custom/event/editView"><spring:message code='sys_but_add'/><spring:message code='custom_event_event'/></a></li>
					<li class="active"><a href="${ctx}/custom/autoReply/form?type=${autoReply.type}"><spring:message code='custom_event_autoReply'/></a></li>
				</c:otherwise>
			</c:choose>
		</li>
	</ul><br/>
	<tags:message content="${message}"/>
	<form:form id="inputForm" modelAttribute="autoReply" action="${ctx}/custom/autoReply/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="type"/>
		<c:if test="${autoReply.type eq '3'}">
			<form:hidden path="used" value="0"/>
		</c:if>
		<input type="hidden" name="createBy" value="${autoReply.createBy.id}"/>
		<c:if test="${autoReply.type ne '3'}">
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_autoReply_autoReply'/>:</label>
			<div class="controls">
				<spring:message code='custom_autoReply_open'/><input  type="radio" ${autoReply.used eq '1'?'checked':''}  value="1" name="used" />
				<spring:message code='custom_autoReply_close'/><input type="radio" ${autoReply.used eq '0'?'checked':''}  value="0" name="used"/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_autoReply_subject'/>:</label>
			<div class="controls">
				<form:input path="subject" cssStyle="width:400px"/>
				<c:choose>
					<c:when test="${autoReply.type eq '1'}">
						<span class="help-inline"><spring:message code='custom_autoReply_emailSubject'/></span>
					</c:when>
					<c:otherwise>
						<span class="help-inline"><spring:message code='custom_autoReply_eventSubject'/></span>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_autoReply_content'/>:</label>
			<div class="controls">
				<textarea name="content" id="content">${autoReply.content}</textarea>
			</div>
		</div>
		</c:if>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_autoReply_forward'/>:</label>
			<div class="controls">
				<spring:message code='custom_autoReply_open'/><input type="radio" ${autoReply.usedForward eq '1'?'checked':''} value="1" name="usedForward"/>
				<spring:message code='custom_autoReply_close'/><input type="radio" ${autoReply.usedForward eq '0'?'checked':''} value="0" name="usedForward"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_autoReply_forwardTo'/>:</label>
			<div class="controls">
				<select name="forwardTo">
					<option value="">--  select  --</option>
					<c:forEach items="${all}" var="user">
						<c:if test="${fns:getUser().id ne user.id}">
							<option value="${user.id}" ${autoReply.forwardTo.id eq user.id?'selected':''} >${user.name}</option>
						</c:if>
				   </c:forEach>
				</select>
			</div>
		</div>
		
		<div class="form-actions" style="text-align: right;">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code='sys_but_save'/>"/>&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
