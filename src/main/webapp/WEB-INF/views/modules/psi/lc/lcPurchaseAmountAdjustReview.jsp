<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购金额调整管理</title>
	<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
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
		<li><a href="${ctx}/psi/lcPurchaseAmountAdjust/">(理诚)采购金额调整列表</a></li>
		<li class="active"><a href="#">(理诚)采购金额调整审核</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="lcPurchaseAmountAdjust" action="${ctx}/psi/lcPurchaseAmountAdjust/reviewSave" method="post" class="form-horizontal">
		<input type="hidden" name="id"            value="${lcPurchaseAmountAdjust.id}"/>
		<div class="control-group">
			<label class="control-label">供应商:</label>
			<div class="controls">
				${lcPurchaseAmountAdjust.supplier.nikename}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">货币类型:</label>
			<div class="controls">
				${lcPurchaseAmountAdjust.currency}
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label">订单号:</label>
			<div class="controls">
				${lcPurchaseAmountAdjust.orderNo}
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">产品:</label>
			<div class="controls">
				${lcPurchaseAmountAdjust.productNameColor}
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">主题:</label>
			<div class="controls">
				${lcPurchaseAmountAdjust.subject}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">调整金额:</label>
			<div class="controls">
				${lcPurchaseAmountAdjust.adjustAmount}
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				${lcPurchaseAmountAdjust.remark}
			</div>
		</div>
		
		
		<c:if test="${not empty lcPurchaseAmountAdjust.filePath}">
		<div class="control-group" >
		<label class="control-label" >已上传附件:</label>
			<div class="controls">
				<c:forEach items="${fn:split(lcPurchaseAmountAdjust.filePath,',')}" var="attchment" varStatus="i">
					<span><a target="_blank" href="<c:url value='/data/site/${attchment}'/>">附件 ${i.index+1}</a></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</c:forEach> 
			</div>  
		</div>
		</c:if>
		<shiro:hasPermission name="psi:purchaseAdjust:review">
			<div class="form-actions">
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="审核通过"/>&nbsp;
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>
		</shiro:hasPermission>
	</form:form>
</body>
</html>
