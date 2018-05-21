<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>供应商税点调整管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
			$(".Wdate").on("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			
			$("#supplierId").change();
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
		<li><a href="${ctx}/psi/psiSupplierTaxAdjust/">供应商税点调整列表</a></li>
		<li class="active"><a href="#">税点调整审核</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiSupplierTaxAdjust" action="${ctx}/psi/psiSupplierTaxAdjust/reviewSave" method="post" class="form-horizontal"   >
		<input type="hidden" name="id"            value="${psiSupplierTaxAdjust.id}"/>
		<div class="control-group">
			<label class="control-label">供应商:</label>
			<div class="controls">
				<input  type="text" readonly="readonly" style="width:40%" value="${psiSupplierTaxAdjust.supplier.name}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">调整前税点:</label>
			<div class="controls">
				<input  type="text" readonly="readonly" style="width:40%" value="${psiSupplierTaxAdjust.oldTax}" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">调整后税点:</label>
			<div class="controls">
				<input  type="text"  readonly="readonly" style="width:40%" value="${psiSupplierTaxAdjust.tax}" class="number" />
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label">凭证</label>
			<div class="controls">
				<c:if test="${not empty psiSupplierTaxAdjust.filePath}">
					<a  target="_blank" class="btn btn-success" href="<c:url value='${psiSupplierTaxAdjust.filePath}'/>">点击查看</a>
				</c:if>
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remark" htmlEscape="false" readonly="true" rows="4" maxlength="200" class="input-xxlarge" />
			</div>
		</div>
		
		
		<div class="form-actions">
			<shiro:hasPermission name="psi:supplierTaxAdjust:review">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="审核通过"/>&nbsp;
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</shiro:hasPermission>
		</div>
	</form:form>
</body>
</html>
