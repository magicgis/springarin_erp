<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>任务管理</title>
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
			
			<c:if test="${empty task.id}">
			var ckeditor = CKEDITOR.replace("remarks");
			ckeditor.config.height = "200";
			
			var depUser = []; 
			var users;
			var user ;
			<c:forEach items="${fns:getOfficeList()}" var="office" varStatus="i">
				<c:if test="${office.id != 1}">
			 	users = [];
				$("#dep").append("<option value='${office.id}'>${office.name}</option>");
				<c:forEach items="${office.userList}" var="user" varStatus="j">
					 user = {};
					 user.id = '${user.id}';
					 user.name = '${user.name}';
					 users[${j.index}] = user; 
				</c:forEach>
				depUser['${office.id}'] = users;
				</c:if>
		    </c:forEach>
			$("#dep").on("change",function(){
				$("#userId").empty();
				var depVal = $(this).val();
				$(depUser[depVal]).each(function(i,data){
					$("#userId").append("<option value='"+data.id+"' >"+data.name+"</option>");
				});
				//$("#userId").data("select2").initSelection(0);
			});
			
			var values = {};
			$("#userId").on("change",function(e){
				if(e.added)
					values[e.added.id] = true;
				if(e.removed)
					delete values[e.removed.id];
			});
			
			jQuery.validator.addMethod("validateUser", function(value, element) {
				for(id in values){
					return true;
				}
				return false;
			}, "请选择执行人！");
			
			$("#dep").data("select2").initSelection(0);
			var depVal = $("#dep").val();
			$(depUser[depVal]).each(function(i,data){
				$("#userId").append("<option value='"+data.id+"' >"+data.name+"</option>");
			});
			
			$("#subject").focus();
			
			$("#inputForm").validate({
				rules:{
					"userId":"validateUser"
				},
				submitHandler: function(form){
					loading('正在派发任务，请稍等...');
					var i = 0 ;
					for(id in values){
						$("#inputForm").append("<input name='performers["+i+"].id' type='hidden' value='"+id+"' />");
						i++;
					}
					$("#userId").removeProp("name");
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
			$("#end").rules('add', {greaterThan: "#start"});
			</c:if>
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/plan/task/">任务列表</a></li>
		<li class="active"><a href="${ctx}/plan/task/form?id=${task.id}">任务<shiro:hasPermission name="plan:task:edit">${not empty task.id?'查看':'添加'}</shiro:hasPermission><shiro:lacksPermission name="plan:task:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="task" action="${ctx}/plan/task/save" method="post" class="form-horizontal" enctype="multipart/form-data">
		<form:hidden path="id"/>
		<input type="hidden" value="0" name="state" />
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">任务主题:</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty task.id}">
						<form:input path="subject" htmlEscape="false" maxlength="200" class="required"/>
					</c:when>
					<c:otherwise>
						${task.subject}				
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">指派执行人:</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty task.id}">
						<span>
							<select style="width: 150px;" id="dep"></select>　
						</span>	
						<span id="masters">
							 <select name="userId" id="userId" style="width: 150px;" multiple class="multiSelect"></select>
						</span>
					</c:when>
					<c:otherwise>
						${task.performer}
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">开始时间:</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty task.id}">
						<input id="start" name="startDate" type="text" readonly="readonly" maxlength="20" class="Wdate"
						onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
					</c:when>
					<c:otherwise>
						<fmt:formatDate pattern="yyyy-MM-dd" value="${task.startDate}"/>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">结束时间:</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty task.id}">
						<input id="end" name="endDate" type="text" readonly="readonly" maxlength="20" class="Wdate"
						onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:true});"/>
					</c:when>
					<c:otherwise>
						<fmt:formatDate pattern="yyyy-MM-dd" value="${task.endDate}"/>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<c:if test="${not empty task.id}">
			<div class="control-group">
				<label class="control-label">创建时间:</label>
				<div class="controls">
					<fmt:formatDate type="both" value="${task.createDate}"/>
				</div>
			</div>
		</c:if>
		<div class="control-group">
			<label class="control-label">任务内容:</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty task.id}">
						<form:textarea path="remarks" cssClass="required" htmlEscape="false" rows="8" maxlength="200" class="input-xxlarge"/>
					</c:when>
					<c:otherwise>
						${task.remarks}								
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">附件:</label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty task.id}">
							<input name="attchmentFile" type="file"/>
					</c:when>
					<c:otherwise>
							${task.realName}						
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="form-actions" style="text-align: center;">
			<c:if test="${empty task.id}">
				<shiro:hasPermission name="plan:task:edit"><input id="btnSubmit" class="btn btn-primary" type="submit" value="派 发"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</shiro:hasPermission>
			</c:if>	
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
