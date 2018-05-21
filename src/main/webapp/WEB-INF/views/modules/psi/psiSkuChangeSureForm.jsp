<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>新品贴码确认</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {

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
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
			$("#fromSku").change(function(){
				var fromSku = $(this).val();
				window.location.href="${ctx}/psi/psiSkuChangeBill/skuSureForm?fromSku="+fromSku;
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiSkuChangeBill">Sku Change List</a></li>
		<li class="active"><a href="#">Label Confirmation</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiSkuChangeBill" action="${ctx}/psi/psiSkuChangeBill/skuSureSave/" 
		method="post" class="form-horizontal">
		<div class="control-group">
			<label class="control-label">Product:</label>
			<div class="controls">
				<select name="fromSku" class="required" style="width:43%" id="fromSku">
					<c:forEach var="temp" items="${newProducts}" varStatus="i">
						<option ${temp eq psiSkuChangeBill.fromSku?'selected':'' } value="${temp}">${temp}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Sku:</label>
			<div class="controls">
				<select name="toSku" class="required" style="width:43%" id="toSku">
					<c:forEach var="temp" items="${skus}" varStatus="i">
						<option ${skus eq psiSkuChangeBill.toSku?'selected':'' } value="${temp}">${temp}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="Save"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="Back" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
