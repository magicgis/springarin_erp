<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Email View</title>
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

			$("#reason").on("blur",function(){
				var params = {};
				if($("#reason").val()){
					params.id='${sendEmail.id}';
					params.remark = encodeURI($("#reason").val());
					$.get("${ctx}/custom/sendEmail/updateRemark?"+$.param(params),function(data){
						if(!(data)){    
							top.$.jBox.error("Save remark fail", 'info',{timeout:2000});
						}else{
							top.$.jBox.tip("Save remark success", 'info',{timeout:2000});
						}
					});
				}
			});
			
			
			$("#translateEn").click(function(){
				var cnt=$("#tempCnt").text().replace(/[\n\r]/g,' ');  
				cnt=encodeURI(cnt);
				window.open("https://translate.google.cn/#auto/en/"+cnt);  
			});
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
	    <li><a href="${ctx}/custom/sendEmail/checkList">Mail Audit List</a></li>
		<li><a href="${ctx}/custom/sendEmail"><spring:message code="custom_email_sentList" /></a></li>
		<li class="active"><a href="${ctx}/custom/sendEmail/view?id=${sendEmail.id}"><spring:message code="custom_email_view" /></a></li>
	</ul>
	<div class="form-horizontal">
	   <%--  <input type='hidden' value="${empty email.remark?email.sendContent:email.remark}" id='tempCnt' /> --%>
		<div class="control-group">		
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)"/>
			
			<input id='translateEn' class="btn btn-primary" type="button" value="Translate"/>
		</div>
		
		<%-- <div class="control-group">
			<label class="control-label"><b>From:</b></label>
			<div class="controls">
				${email.serverEmail}
			</div>
		</div> --%>
		
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form1'/>:</label>
			<div class="controls">
				${email.createBy.name}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form15'/>:</label>
			<div class="controls">
				${email.encryptionEmail}
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
				<div class="controls" id="tempCnt">${empty email.remark?email.sendContent:email.remark}</div>
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
		
		<c:choose>
		   <c:when test="${('0' eq email.checkState ||'2' eq email.checkState||'0' eq email.sendFlag)&&'2' eq checkEmail&&fns:getUser().name ne email.createBy.name}">
		        <div class="control-group">
						<label class="control-label"><b>Check Remark:</b></label>
						<div class="controls">
							<input type="text" name="reason"  id="reason" style="width: 60%" value="${email.reason}" /> 
						</div>
		        </div>
		   </c:when>
		   <c:otherwise>
		         <div class="control-group">
						<label class="control-label"><b>Check Remark:</b></label>
						<div class="controls">
							${email.reason}
						</div>
		        </div>
		   </c:otherwise>
		</c:choose>
	
	    			
		<div class="control-group">
		    <c:if test="${('0' eq email.checkState ||'2' eq email.checkState||'0' eq email.sendFlag )&&'2' eq checkEmail&&fns:getUser().name ne email.createBy.name}">
					<a class="btn btn-warning" href="${ctx}/custom/sendEmail/checkState?id=${sendEmail.id}&checkState=1&oldCheckState=${sendEmail.checkState}" onclick="return confirm('The mail audit passed？', this.href)">Pass</a> 
				        &nbsp;&nbsp;
				    <a class="btn btn-warning" href="${ctx}/custom/sendEmail/checkState?id=${sendEmail.id}&checkState=2&oldCheckState=${sendEmail.checkState}" onclick="return confirm('The mail audit not passed？',this.href)">Not Pass</a> 
		       
		    </c:if>
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)"/>
		</div>
		
		
	</div>
</body>
</html>