<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>退货检测登记管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/returnTest/">ReturnTestList</a></li>
		<li class="active"><a href="#">View ReturnTest</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="returnTest" action="${ctx}/amazoninfo/returnTest/save" method="post" class="form-horizontal">
		<input type="hidden" name="id"			  value="${returnTest.id}"/>
		<input type="hidden" name="testSta" 	  value="${returnTest.testSta}"/>
		<input type="hidden" name="productName"   value="${returnTest.productName}"/>
		<input type="hidden" name="createUser.id" value="${returnTest.createUser.id}"/>
		<input type="hidden" name="createDate"    value="<fmt:formatDate pattern='yyyy-MM-dd hh:mm:ss' value='${returnTest.createDate}'/>"/>
		
		<blockquote style="float:left;width:98%;">
			<p style="font-size: 15px;font-weight: bold">Base Info.</p>
		</blockquote>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px">WareHouse:</label>
				<div class="controls" style="margin-left:120px">
					<input type="text" name="warehouseId" readonly="readonly" value="${returnTest.warehouseName}" /> 
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px">Product:</label>
				<div class="controls" style="margin-left:120px;">
					<input type="text" name="sku" readonly="readonly" value="${returnTest.sku}" /> 
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px">Quantity:</label>
				<div class="controls" style="margin-left:120px">
					<input type="text" name="quantity" readonly="readonly" value="${returnTest.quantity}" />   
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px">Stock-in No:</label>
				<div class="controls" style="margin-left:120px">
					<a target='_blank' class="nava" style="color:#2828FF;" href="${ctx}/psi/psiInventoryIn/view?billNo=${returnTest.stockInNo}">${returnTest.stockInNo}</a>   
				</div>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%;">
			<label class="control-label" style="width:100px">Reason:</label>
			<div class="controls" style="margin-left:120px;">
				<form:textarea path="reasonDetail" htmlEscape="false" rows="4" maxlength="200" style="width:100%; height: 60px;" disabled="true" />
			</div>
		</div>
		
		
		<blockquote style="float:left;width:98%;">
			<p style="font-size: 15px;font-weight: bold">Stock-In Info.</p>
		</blockquote>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px">New:</label>
				<div class="controls" style="margin-left:120px">
					<input type="text" name="newQuantity" value="${returnTest.newQuantity}" class="required number" readonly/>   
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px">Renew:</label>
				<div class="controls" style="margin-left:120px">
					<input type="text" name="renewQuantity" value="${returnTest.renewQuantity}" class="required number" readonly/>   
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px">Old:</label>
				<div class="controls" style="margin-left:120px">
					<input type="text" name="oldQuantity" value="${returnTest.oldQuantity}" class="required number" readonly/>   
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px">Broken:</label>
				<div class="controls" style="margin-left:120px">
					<input type="text" name="brokenQuantity" value="${returnTest.brokenQuantity}" class="required number" readonly/>   
				</div>
			</div>
		</div>
		<div class="form-actions" style="float:left;width:100%">
			<input id="btnCancel" class="btn" type="button" value="back" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
