<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Reviewer Email view</title>
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
	
		$(function(){
			if(!(top)){
				top = self; 
			}
			
			$(".btn-group button").click(function(){
				var param = {};
				param.id='${emailManager.id}';
				param.flag = $(this).attr("key");
				$.get("${ctx}/amazoninfo/reviewerEmail/saveMark?"+$.param(param),function(data){
					if(data){
						top.$.jBox.tip("Mark success!");
					}
				});
			});
			
		});
		
		
	</script>
	
	
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/reviewerEmail"><spring:message code='custom_email_list'/></a></li>
		<li class="active"><a href="${ctx}/amazoninfo/reviewerEmail/view?id=${emailManager.id}"><spring:message code='custom_email_view'/></a></li>
	</ul>
	<div class="form-horizontal">
		<div class="control-group">		
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)"/>
		</div>
		
		<c:if test="${not empty reviewerEmail.transmit}">
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form13'/>:</label>
			<div class="controls">
				${reviewerEmail.transmit}
			</div>
		</div>
		</c:if>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form1'/>:</label>
			<div class="controls">
					${reviewerEmail.revertEmail}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form3'/>:</label>
			<div class="controls">
				${reviewerEmail.subject}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form14'/>:</label>
			<div class="controls">
				<fmt:formatDate type="both" value="${reviewerEmail.customSendDate}"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Reply Emails Number:</label>
			<div class="controls">
				${reviewerEmail.state eq '2' ? reviewerEmail.sendedEmails:'0'}
			</div>
		</div>
		<div class="control-group">
				<label class="control-label">Content:</label>
				<div class="controls">${reviewerEmail.receiveContent}</div>
		</div>
		
		<c:if test="${not empty reviewerEmail.attchmentPath || not empty reviewerEmail.inlineAttchmentPath}">
			<div class="control-group">
				<label class="control-label">Attchment</label>
				<div class="controls">
					<c:forEach items="${fn:split(reviewerEmail.attchmentPath,',')}" var="attchment">
						<a href="${ctx}/amazoninfo/reviewerEmail/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
						&nbsp;&nbsp;&nbsp;  
					</c:forEach>
					<c:forEach items="${fn:split(reviewerEmail.inlineAttchmentPath,',')}" var="attchment">
						<a href="${ctx}/amazoninfo/reviewerEmail/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
						&nbsp;&nbsp;&nbsp;  
					</c:forEach>
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">Master By</label>
			<div class="controls">
				${reviewerEmail.masterBy.name}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form11'/>:</label>
			<div class="controls">
				<fmt:formatDate type="both" value="${reviewerEmail.answerDate}"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form12'/>:</label>
			<div class="controls">
				<fmt:formatDate type="both" value="${reviewerEmail.endDate}"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Process Time:</label>
			<div class="controls">
				${reviewerEmail.masterTime}Minutes
			</div>
		</div>
		
		<c:if test="${reviewerEmail.state eq '2' || reviewerEmail.state eq '3'}">
			<div class="control-group">
				<h3>Reply Content：</h3>
			</div>
			
			<c:forEach items="${reviewerEmail.reviewerSendEmails}" var="sendEmail">
				<c:if test="${sendEmail.sendFlag eq '1'}">
					<div class="control-group">
						<label class="control-label"><spring:message code='custom_email_form1'/></label>
						<div class="controls">
							${sendEmail.sendEmail}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label"><spring:message code='custom_email_form14'/></label>
						<div class="controls">
							<fmt:formatDate type="both" value="${sendEmail.sentDate}"/>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label"><spring:message code='custom_email_form3'/>:</label>
						<div class="controls">
							${sendEmail.sendSubject}
						</div>
					</div>
					
					<div class="control-group">
						<label class="control-label">Content:</label>
						<div class="controls">${sendEmail.sendContent}</div>
					</div>
					<c:if test="${not empty sendEmail.sendAttchmentPath}">
						<div class="control-group">
							<label class="control-label">Attchment</label>
							<div class="controls">
								<c:forEach items="${fn:split(sendEmail.sendAttchmentPath,',')}" var="attchment">
									<a href="${ctx}/amazoninfo/reviewerEmail/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
									&nbsp;&nbsp;&nbsp;  
								</c:forEach>
							</div>
						</div>
					</c:if>
					<hr style="border-color: blue;"/>
				</c:if>
			</c:forEach>
		</c:if>
		<div class="control-group">
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)"/>
		</div>
	</div>
</body>
</html>