<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>amazonCommentView</title>
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
	
		$(document).ready(function() {
			if(!(top)){
				top = self; 
			}
		
			$(".back").click(function(){
				history.go(-1);
			});
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#">信件详情</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="amazonComment"  class="form-horizontal">
		<form:hidden path="id"/>
		<div class="control-group">
			<!-- 返回 -->
			<input class="btn back" type="button" value="<spring:message code='sys_but_back'/> "/>
		</div>
		<div class="container-fluid">
			<div class="row-fluid">
				<div class="span12">
					<blockquote>
						<p style="font-size: 14px"><spring:message code='custom_event_detail'/></p>
					</blockquote>
					<div class="control-group">
						<label class="control-label">平台:</label>
						<div class="controls">
							${fns:getDictLabel(amazonComment.customer.country,'platform','')}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">客户名称:</label>
						<div class="controls">
							<a target="_blank" href="${amazonComment.customer.link }">${amazonComment.customer.name }</a>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">发送邮箱:</label>
						<div class="controls">
							${amazonComment.sendEmail }
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">发送状态:</label>
						<div class="controls">
							<c:if test="${'0' eq amazonComment.sendFlag }">未发送</c:if>
							<c:if test="${'1' eq amazonComment.sendFlag }">已发送</c:if>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">主题:</label>
						<div class="controls">
							${amazonComment.sendSubject }
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">发信日期:</label>
						<div class="controls">
							<fmt:formatDate pattern="yyyy-MM-dd" value="${amazonComment.sentDate}"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">信件内容:</label>
						<div class="controls">${amazonComment.content}</div>
					</div>
				</div>
			</div>
		</div>
		<div class="control-group">
			<!-- 返回 -->
			<input  class="btn back" type="button" value=" <spring:message code='sys_but_back'/> "/>
		</div>
	</form:form>
</body>
</html>
