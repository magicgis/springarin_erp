<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品变更申请</title>
	<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {

			$(".Wdate").on("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			$("#inputForm").validate({
				submitHandler: function(form){
					top.$.jBox.confirm('确定要提交申请吗?','系统提示',function(v,h,f){
						if(v=='ok'){
							loading('正在提交，请稍等...');
							form.submit();
							$("#btnSubmit").attr("disabled","disabled");
						}
					},{buttonsFocus:1,persistent: true});
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
			
			$("#line").change(function(){
				var id = '${productImprovement.id}';
				var line = $(this).val();
				window.location.href="${ctx}/psi/productImprovement/form?id="+id+"&line="+line;
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/productImprovement/">产品变更记录</a></li>
		<li class="active"><a href="${ctx}/psi/productImprovement/form?id=${productImprovement.id}">产品变更申请</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiProductImprovement" action="${ctx}/psi/productImprovement/save" 
		method="post" class="form-horizontal" enctype="multipart/form-data">
		<form:hidden path="id"/>
		<form:hidden path="filePath"/>
		<div class="control-group">
			<label class="control-label">产品线:</label>
			<div class="controls">
				<select name="line" class="required" style="width:43%" id="line">
					<c:forEach var="temp" items="${allLine}" varStatus="i">
						<option ${temp eq psiProductImprovement.line?'selected':'' } value="${temp}">${temp}产品线</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">产品:</label>
			<div class="controls">
				<select name="productName" multiple="multiple" class="required" style="width:43%" id="product">
					<c:forEach items="${productImprovement.productNames}" var="pName">
						<option value="${pName}" selected>${pName}</option>
					</c:forEach>
					<c:forEach items="${colorNameList}" var="colorName">
						<option value="${colorName}">${colorName}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">紧急程度:</label>
			<div class="controls">
				<form:radiobutton path="type" value="1"/>一般
				<form:radiobutton path="type" value="2"/>紧急
				<form:radiobutton path="type" value="3"/>特急
				<%--<input type="radio" name="type" value="1" required="required"/>一般
				<input type="radio" name="type" value="2" required="required"/>紧急
				<input type="radio" name="type" value="3" required="required"/>特急 --%>
			</div>
		</div>
	
		<div class="control-group">
			<label class="control-label">起始订单号:</label>
			<div class="controls">
				<form:input path="orderNo" style="width:42%"/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">变更时间</label>
			<div class="controls">
				 <input type="text" name="improveDate" style="width:42%" required="required" class="Wdate" value="<fmt:formatDate value='${productImprovement.improveDate}' pattern='yyyy-MM-dd' />"/> 
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">变更原因:</label>
			<div class="controls">
				<form:textarea path="reason" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge required"/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">变更前说明:</label>
			<div class="controls">
				<form:textarea path="perRemark" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge required" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">变更后说明:</label>
			<div class="controls">
				<form:textarea path="afterRemark" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge required" />
			</div>
		</div>
	
		<div class="control-group">
			<label class="control-label">变更涉及相关文件:</label>
			<div class="controls">
				<input name="attachmentFile" type="file" id="myfileupload"/>
			</div>
		</div>
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
