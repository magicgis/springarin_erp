<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购金额调整管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
			eval('var curMap=${curMap}');
			$(".Wdate").on("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			$("#supplierId").on("change",function(){
				$("input[name='currency']").val(curMap[$(this).val()]);
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
		<li><a href="${ctx}/psi/purchaseAmountAdjust/">采购金额调整列表</a></li>
		<li class="active"><a href="#">${not empty purchaseAmountAdjust.id?'修改':'添加'}采购金额调整</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="purchaseAmountAdjust" action="${ctx}/psi/purchaseAmountAdjust/save" method="post" class="form-horizontal">
		<input type="hidden" name="id"            value="${purchaseAmountAdjust.id}"/>
		<input type="hidden" name="adjustSta"     value="${purchaseAmountAdjust.adjustSta}"/>
		<input type="hidden" name="createUser.id" value="${purchaseAmountAdjust.createUser.id}"/>
		<input type="hidden" name="createDate" value="<fmt:formatDate pattern='yyyy-MM-dd hh:mm:ss' value='${purchaseAmountAdjust.createDate}'/>"/>
		<div class="control-group">
			<label class="control-label">供应商:</label>
			<div class="controls">
				<select name="supplier.id"  id="supplierId" style="width:41%">
					<c:forEach items="${supplierMap}" var="supplierEntry">
						<option value="${supplierEntry.key}" ${supplierEntry.key eq purchaseAmountAdjust.supplier.id ?'selected':'' } >${supplierEntry.value}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">货币类型:</label>
			<div class="controls">
				<input name="currency"  type="text" readonly="readonly" style="width:40%" value="${purchaseAmountAdjust.currency}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">主题:</label>
			<div class="controls">
				<input name="subject"  type="text" maxlength="100" style="width:40%" value="${purchaseAmountAdjust.subject}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">调整金额(可输入负号):</label>
			<div class="controls">
				<input name="adjustAmount"  type="text" maxlength="100" class="required price" style="width:40%" value="${purchaseAmountAdjust.adjustAmount}" />
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remark" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge" />
			</div>
		</div>
		
		
		<div class="form-actions">
			<shiro:hasPermission name="psi:purchaseAdjust:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</shiro:hasPermission>
		</div>
	</form:form>
</body>
</html>
