<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>折扣预警管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#country").on("change",function(e){
				var params = {};
				params['country'] = $(this).val();
				window.location.href = "${ctx}/psi/evaluateWarning/form?"+$.param(params);
			});
			
			$(".Wdate").on("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			$("#inputForm").validate({
				submitHandler: function(form){
					form.submit();
					$("#btnSubmit").attr("disabled","disabled");
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					error.appendTo($("#errorsShow"));
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/evaluateWarning/">折扣预警列表</a></li>
		<li class="active"><a href="#">${not empty evaluateWarning.id?'修改':'添加'}折扣预警</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="evaluateWarning" action="${ctx}/amazoninfo/evaluateWarning/save" method="post" class="form-horizontal">
		<input type="hidden" name="id" value="${evaluateWarning.id}"/>
		<input type="hidden" name="warningSta" value="${evaluateWarning.warningSta}"/>
		<input type="hidden" name="promotionCode" value="${evaluateWarning.promotionCode}"/>
		<input type="hidden" name="result" 		  value="${evaluateWarning.result}"/>
		<input type="hidden" name="createUser.id" value="${evaluateWarning.createUser.id}"/>
		<input type="hidden" name="updateDate" value="<fmt:formatDate pattern='yyyy-MM-dd hh:mm:ss' value='${evaluateWarning.updateDate}'/>"/>
		
		<div class="control-group">
			<label class="control-label">Country:</label>
			<div class="controls">
				<select name="country" >
					<c:forEach items="${countrySet}" var="country">
						<option value="${country}" ${evaluateWarning.country eq country ?'selected':'' } >${fns:getDictLabel(country, 'platform', defaultValue)}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">Tracking Id:</label>
			<div class="controls">
				<input name="promotionId"  type="text" maxlength="100" class="required" style="width:40%" value="${evaluateWarning.promotionId}" />
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label">Remark:</label>
			<div class="controls">
				<form:textarea path="remark" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge" />
			</div>
		</div>
		
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
