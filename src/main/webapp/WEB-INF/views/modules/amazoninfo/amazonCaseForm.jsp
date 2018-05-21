<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>Contact Amazon</title>
<meta name="decorator" content="default" />

<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script>
<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
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
		
		$(document).ready(function() {
			/**
			var ckeditor = CKEDITOR.replace("sendContent");
			ckeditor.config.height = "280px";
			ckeditor.config.toolbarStartupExpanded = false;
			ckeditor.config.startupFocus = true;*/
			
			$("#inputForm").validate({
				submitHandler: function(form){
					top.$.jBox.confirm('OK,Create the case!!','Prompted',function(v,h,f){
						if(v=='ok'){
							loading('Sent to a message being sent, please wait ...');
							form.submit();
						}
					},{buttonsFocus:1,persistent: true});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("Entered incorrectly, please correct.");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
			$("#ccToEmail").rules('add', {mutEmail: true});
		});
		
		function changeContent(){
			var sendContent = encodeURIComponent($("#content").val());
			$("#sendContent").val(sendContent);
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/amazonCase/list">Case列表</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/amazonCase/form">添加Case</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="amazonCase" action="${ctx}/amazoninfo/amazonCase/send" method="post" class="form-horizontal" enctype="multipart/form-data">
		<tags:message content="${message}" />
		<input id="sendContent" name="sendContent" type="hidden"/>
		<div class="control-group">
			<input class="btn btn-primary" type="submit" value="<spring:message code='sys_but_send'/>" />&nbsp;
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='sys_label_country'/></label>
			<div class="controls">
				<select name="country" id="country" class="required">
					<option value="">---请选择---</option>
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
						<c:if test="${dic.value ne 'com.unitek' && dic.value ne 'mx'}">
							<option value="${dic.value}">${dic.label}</option>
						</c:if>
					</c:forEach>	
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">email抄送地址</label>
			<div class="controls">
				<input type="text" name="ccToEmail" id="ccToEmail" style="width: 60%" placeholder="选填项"/> <span class="help-inline">More than in English","Split</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Asin</label>
			<div class="controls">
				<input type="text" name="asin" id="asin" style="width: 60%" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Subject</label>
			<div class="controls">
				<input type="text" name="subject" id="subject" style="width: 80%" class="required"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Content</label>
			<div class="controls">
				<textarea name="content" id="content" style="width:80%;height:300px" class="required"  onkeyup="changeContent()"></textarea>
			</div>
		</div>
	</form:form>
</body>
</html>
