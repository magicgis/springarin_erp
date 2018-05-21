<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<meta name="decorator" content="default" />
<title>email template edit</title>
<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script>
<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
     var sendFlag = false;
	  $(function(){		
		  $("#inputForm").validate();
			CKEDITOR.replace("templateContent",{height:'280px',width:'700px',toolbarStartupExpanded:false,startupFocus:true});		  
		  $("#saveBtn").click(function(){
			  //upodate
			  if($("#idTemp").val()!=""){
				  if($("#templateSubject").val() != null && $("#templateSubject").val() != ""){
					  var subject = encodeURIComponent($("#templateSubject").val());
					  $("#templateSubject").val(subject);
				  }
				  $("#inputForm").submit();
				  return;
			  }
			  
			  //save
			  if(sendFlag){
				  if(($("#type").val()==3 || $("#type").val()==4 || $("#type").val()==5 || $("#type").val()==6) && $("#templateSubject").val()==""){
					  $("#subjectInfo").css("color","red").text("<spring:message code='custom_email_template_msg5'/>"); 
				  } else {
					  var subject = encodeURIComponent($("#templateSubject").val());
					  $("#templateSubject").val(subject);
					  $("#inputForm").submit();
				  }
			  }else{
				  $("#nameInfo").css("color","red").text("<spring:message code='custom_email_template_msg3'/>");
			  }
		  });
		 
		  $("#type").change(function(){
			  $("#subjectInfo").text("");
			  $("#role").empty();
			  if($(this).val()==3 || $(this).val()==4 || $(this).val()==5 || $(this).val()==6){
				  $("#attachment").css("display","block");
				  $("#tips").css("display","block");
			  } else {
				  $("#attachment").css("display","none");
				  $("#tips").css("display","none");
			  }
			  if($(this).val()==1){
		           $("#roleGrp").show();				
				  $.post(
					 "${ctx}/custom/emailTemplate/roleList",
					 {},
					 function(data){						
						for(var i=0;i<data.length;i++){
							if(data[i].name.endWith('客服')){
						     	$("#role").append("<option value="+data[i].id+">"+data[i].name+"</option>");
							}
						}
						$("#role option:first").attr("selected",true);
					 }
				  );				  
				  				  				  
			  }else{
				  $("#roleGrp").hide();
			  }
		  });
		  
		  
		  $("#templateName").blur(function(){
			  if($("#templateName").val()==""){
				  $("#nameInfo").css("color","red").text("Template name can not be null");
				  return;
			  }
			  $.post(
				"${ctx}/custom/emailTemplate/isExistName",
				{"templateName":$("#templateName").val(),"templateType":$("#type").val(),"roleId":$("#role").val()},
				function(data){
					if(data=="true"){
						$("#nameInfo").css("color","green").text("<spring:message code='custom_email_template_msg1'/>");
					    sendFlag = true;
					}else{
						$("#nameInfo").css("color","red").text("<spring:message code='custom_email_template_msg2'/>");
					}
				}
			  );
		  });
		  $("#tips").css("display","none");
		  
		  $("#attachment").css("display","none");
	  });
		  
	  String.prototype.endWith=function(str){     
		  var reg=new RegExp(str+"$");     
		  return reg.test(this);        
	  }
		  
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a  href="${ctx}/custom/emailTemplate"><spring:message code='custom_email_template_list'/></a></li>
		<c:choose>
			<c:when test="${not empty customEmailTemplate.id}">
				<li class="active"><a href="${ctx}/custom/emailTemplate/update?id=${customEmailTemplate.id}"><spring:message code='custom_email_template_edit'/></a></li>
			</c:when>
			<c:otherwise>
				<li class="active"><a href="${ctx}/custom/emailTemplate/add"><spring:message code='custom_email_template_add'/></a></li>
			</c:otherwise>
		</c:choose>
	</ul>
	<br />
	<tags:message content="${message}" />
	<form:form id="inputForm" modelAttribute="customEmailTemplate"
		action="${ctx}/custom/emailTemplate/save" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input type="hidden" name="id" id="idTemp" value="${customEmailTemplate.id}" />
		
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_template_type'/></label>
			<div class="controls">
			   <c:choose>
			      <c:when test="${not empty customEmailTemplate.templateType && not empty customEmailTemplate.id && customEmailTemplate.templateType eq '2' }">
			      		<spring:message code='custom_email_template_self'/>
			      </c:when>
			       <c:when test="${not empty customEmailTemplate.templateType && not empty customEmailTemplate.id && customEmailTemplate.templateType eq '1' }">
			      		<spring:message code='custom_email_template_share'/>
			      </c:when>
			       <c:when test="${not empty customEmailTemplate.templateType && not empty customEmailTemplate.id && customEmailTemplate.templateType eq '3' }">
			      		<spring:message code='custom_email_template_after'/>
			      </c:when>
			       <c:when test="${not empty customEmailTemplate.templateType && not empty customEmailTemplate.id && customEmailTemplate.templateType eq '4' }">
			      		<spring:message code='custom_email_template_review'/>
			      </c:when>
			       <c:when test="${not empty customEmailTemplate.templateType && not empty customEmailTemplate.id && customEmailTemplate.templateType eq '5' }">
			      		<spring:message code='custom_email_template_manual'/>
			      </c:when>
			       <c:when test="${not empty customEmailTemplate.templateType && not empty customEmailTemplate.id && customEmailTemplate.templateType eq '6' }">
			      		<spring:message code='custom_email_template_feedback'/>
			      </c:when>
			      <c:otherwise>
			       <select name="templateType" id="type" style="width: 222px" class="required">
			       <!-- 
			        <option value="0"><spring:message code='custom_email_template_sys'/></option>
			        -->
				   <option value="2" ${'2' eq customEmailTemplate.templateType?'selected':''}><spring:message code='custom_email_template_self'/></option>
				     <option value="1" ${'1' eq customEmailTemplate.templateType?'selected':''}><spring:message code='custom_email_template_share'/></option>
					 <option value="3" ${'3' eq customEmailTemplate.templateType?'selected':''}><spring:message code='custom_email_template_after'/></option>
					 <option value="4" ${'4' eq customEmailTemplate.templateType?'selected':''}><spring:message code='custom_email_template_review'/></option>
					 <option value="5" ${'5' eq customEmailTemplate.templateType?'selected':''}><spring:message code='custom_email_template_manual'/></option>
					 <option value="6" ${'6' eq customEmailTemplate.templateType?'selected':''}><spring:message code='custom_email_template_feedback'/></option>
				   	
				   	</select>
			      </c:otherwise>
			   </c:choose>
			   	<span class="help-inline"><spring:message code='custom_email_template_msg4'/></span>		
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='amazon_order_form4'/></label>
			<div class="controls">
				<select name="country" id="country" class="required">
					<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
						<c:if test="${dic.value ne 'com.unitek'}">
							<option value="${dic.value}" ${dic.value eq customEmailTemplate.country?'selected':''}>${dic.label}</option>
						</c:if>
					</c:forEach>
				</select>
			</div>
		</div>

		<c:choose>
			<c:when test="${not empty customEmailTemplate.role}">
				<div class="control-group" id="roleGrp">
					<label class="control-label"><spring:message code='custom_email_template_group' /></label>
					<div class="controls">
						${customEmailTemplate.role.name}
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<div class="control-group" id="roleGrp" style="display: none;">
					<label class="control-label"><spring:message
							code='custom_email_template_group' /></label>
					<div class="controls">
						<select name="roleId" id="role" style="width: 222px;"
							class="required">
						</select>
					</div>
				</div>

			</c:otherwise>
		</c:choose>

		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_template_name'/></label>
			<div class="controls">
				<div class="input-prepend input-append">				
				  <c:choose>
				     <c:when test="${not empty customEmailTemplate.templateName && not empty customEmailTemplate.id}">
				       <form:input path="templateName" id="templateName" value="${customEmailTemplate.templateName}" htmlEscape="false" maxlength="200" disabled="true" />
				   </c:when>
				   <c:otherwise>
				       <form:input path="templateName" htmlEscape="false" maxlength="200" value="${customEmailTemplate.templateName}"/>
				   </c:otherwise>
				  </c:choose>									
				</div>
				<span id="nameInfo"></span>
			</div>
		</div>

       	<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_template_subject'/></label>
			<div class="controls">
				<div class="input-prepend input-append">
					<form:input id="templateSubject" path="templateSubject" value="${customEmailTemplate.templateSubject}" htmlEscape="false" maxlength="200" />
				</div>
				<span id="subjectInfo"></span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"></label>
			<div class="controls">
				<div class="input-prepend input-append">
				</div><span id="tips"><span style="color:red">模板名称格式：国家+描述(国家为代码，如US/UK),模板主题支持用变量</span><%out.print("${orderId}"); %><span style="color:red">代替订单号<br/>
				模板内容支持产品名、asin、订单号、客户名称和评论asin变量，分别为</span><%out.print("${productName}、${asin}、${orderId}、${customerName}、${reviewAsin}"); %></span>
			</div>
		</div>
		<div class="control-group" id="attachment">
			<label class="control-label">Attachment</label>
			<div class="controls">
				<input name="attachmentFile" type="file" id="attachmentFile" value="${attachmentFile }"/>
				<span style="color:red" class="help-inline">文件名不要为中/日文以及带有"+"、多空格等特殊字符,文件大小最好不要超过1M,可压缩后上传</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_template_content' /></label>
			<div class="controls">
				<span class="help-inline"> You can use "@V_x@" or "@V_xx@" or"@V_xxx@" to replace words. "x" is number from 0 to 9.You must input 1 number at least,and 3 numbers at most.<br />Those variables can be decided when you use template.</span>
				<div class="input-prepend input-append">
					<textarea name="templateContent">${customEmailTemplate.templateContent}</textarea>
				</div>
			</div>
		</div>
		<div class="form-actions">
			<input id="saveBtn" class="btn btn-primary" type="button" value="<spring:message code='sys_but_save'/>" />&nbsp;&nbsp;&nbsp;
			<input class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)"/>
		</div>		
	</form:form>

</body>
</html>