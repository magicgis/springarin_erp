<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Email view</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
	
		$(function(){
			$("#unsubscribe").click(function(event){
				event.preventDefault();
				$.get($(this).attr("href"),function(data){
					if(data){
						top.$.jBox.tip("Email has been added to the unsubscribe list!");
					}
				});
			});
			
			$(".btn-group button").click(function(){
				var param = {};
				param.id='${emailManager.id}';
				param.flag = $(this).attr("key");
				$.get("${ctx}/custom/emailManager/saveMark?"+$.param(param),function(data){
					if(data){
						top.$.jBox.tip("Mark success!");
					}
				});
			});
			
		});
		
		function processTaxRefund(pathName){
			$.ajax({
   			   type: "POST",
   			   url: "${ctx}/custom/emailManager/isExistEvent?id=${emailManager.id}&attchmentPath="+pathName,
   			   async: true,
   			   success: function(msg){
   				  if(msg=="1"){
   					$.jBox.tip('Tax-refund event not found'); 
 					return;
   				  }else if(msg=="0"){ 
   					 window.location.href="${ctx}/custom/emailManager/taxRefund?id=${emailManager.id}";
   				  }
   			   }
       		});
		}
	</script>
	
	
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/custom/emailManager/${flag?'all':''}"><spring:message code='custom_email_list'/></a></li>
		<li class="active"><a href="${ctx}/custom/emailManager/view?id=${emailManager.id}"><spring:message code='custom_email_view'/></a></li>
	</ul>
	<div class="form-horizontal">
		<div class="control-group">		
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)"/>
			<div class="btn-group" data-toggle="buttons-radio">
			  <button key="" type="button" class="btn btn-info${empty customEmail.flag?' active':''}">No Flag</button>
			  <button key="p1" type="button" class="btn btn-info${'p1' eq customEmail.flag?' active':''}" >P1</button>
			  <button key="p2" type="button" class="btn btn-info${'p2' eq customEmail.flag?' active':''}">P2</button>
			</div>
		</div>
		
		<c:if test="${not empty customEmail.transmit}">
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form13'/>:</label>
			<div class="controls">
				${customEmail.transmit}
			</div>
		</div>
		</c:if>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form1'/>:</label>
			<div class="controls">
					${customEmail.encryptionEmail}<c:if test="${'0' eq customEmail.followState}">&nbsp;&nbsp;<b>Reminder date:${customEmail.followDate }</b></c:if> <c:if test="${fn:contains(customEmail.revertEmail,'@marketplace.amazon.')}">
					 <a id="unsubscribe" href="${ctx}/custom/emailManager/unsubscribeEmail?customEmail=${customEmail.revertEmail}" style="font-size: 18px">[Add To UnsubscribeEmail List]</a>
					 <span class="help-inline">Click on the link, the system will not automatically send e-mail bills, and other promotional messages to guests</span>
				</c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form3'/>:</label>
			<div class="controls">
				${customEmail.subject}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form14'/>:</label>
			<div class="controls">
				<fmt:formatDate type="both" value="${customEmail.customSendDate}"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_his_problem'/></label>
			<div class="controls" style="background-color:#FAFAD2">
				<c:if test="${not empty hisProblems}">
					<c:forEach items="${hisProblems}" var="hisPro" varStatus="j">
					  ${j.index+1}.&nbsp;&nbsp;${hisPro[0]} &nbsp;&nbsp; ${hisPro[1]} <spring:message code='custom_email_problem_type'/>： ${hisPro[2]} <spring:message code='custom_email_problem_detail'/>： ${hisPro[3]} <br/>
					</c:forEach>
				</c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Reply Emails Number:</label>
			<div class="controls">
				${customEmail.state eq '2' ? customEmail.sendedEmails:'0'}
			</div>
		</div>
		<div class="control-group">
				<label class="control-label">Content:</label>
				<div class="controls">${customEmail.receiveContent}</div>
		</div>
		
		<%-- <c:if test="${not empty customEmail.attchmentPath || not empty customEmail.inlineAttchmentPath}">
			<div class="control-group">
				<label class="control-label">Attchment</label>
				<div class="controls">
					<c:forEach items="${fn:split(customEmail.attchmentPath,',')}" var="attchment">
						<a href="${ctx}/custom/emailManager/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
						&nbsp;&nbsp;&nbsp;  
					</c:forEach>
					<c:forEach items="${fn:split(customEmail.inlineAttchmentPath,',')}" var="attchment">
						<a href="${ctx}/custom/emailManager/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
						&nbsp;&nbsp;&nbsp;  
					</c:forEach>
				</div>
			</div>
		</c:if>
		 --%>
		<c:if test="${not empty customEmail.attchmentPath || not empty customEmail.inlineAttchmentPath}">
			<div class="control-group">
				<label class="control-label">Attchment</label>
				<div class="controls">
					<c:forEach items="${fn:split(customEmail.attchmentPath,',')}" var="attchment">
						<a href="${ctx}/custom/emailManager/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
						&nbsp;
						<input class="btn btn-warning createTax" onclick="processTaxRefund('${attchment}')" type="button" value="Process Tax-refund"/>&nbsp;&nbsp;&nbsp;  
					</c:forEach>
					<c:forEach items="${fn:split(customEmail.inlineAttchmentPath,',')}" var="attchment">
						<a href="${ctx}/custom/emailManager/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
						&nbsp;&nbsp;&nbsp;  
					</c:forEach>
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">Master By</label>
			<div class="controls">
				${customEmail.masterBy.name}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form11'/>:</label>
			<div class="controls">
				<fmt:formatDate type="both" value="${customEmail.answerDate}"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form12'/>:</label>
			<div class="controls">
				<fmt:formatDate type="both" value="${customEmail.endDate}"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Process Time:</label>
			<div class="controls">
				${customEmail.masterTime}Minutes
			</div>
		</div>
		
		<c:if test="${customEmail.state eq '2' || customEmail.state eq '3'}">
			<div class="control-group">
				<h3>Reply Content：</h3>
			</div>
			
			<c:forEach items="${customEmail.sendEmails}" var="sendEmail">
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
									<a href="${ctx}/custom/emailManager/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
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