<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品配件管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
		$(document).ready(function() {
		
			$("#inputForm").validate({
				rules:{
					"itemPrice":{
						"required":true
					}
				},
				messages:{
					"itemPrice":{"required":'价格不能为空'}
				},
				submitHandler: function(form){
										
					$("#contentTable tbody tr").each(function(i,j){
						$(j).find("select").each(function(){
							if($(this).attr("name")){
								$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
							}
						});
						$(j).find("input[type!='']").each(function(){
							if($(this).attr("name")){
								$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
							}
						});
					}); 
					
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
		<li><a href="${ctx}/psi/lcPsiPartsOrder/">配件订单列表</a></li>
		<li class="active"><a href="#">配件订单确认</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiPartsOrder" action="${ctx}/psi/lcPsiPartsOrder/sureSave" method="post" class="form-horizontal">
		<input type="hidden" 	name="id" 		   			value="${psiPartsOrder.id}" />
		<input type="hidden" 	name="orderSta" 		   	value="${psiPartsOrder.orderSta}" />
		<input type="hidden" 	name="paymentSta" 		    value="${psiPartsOrder.paymentSta}" />
		<input type="hidden" 	name="paymentAmount" 		value="${psiPartsOrder.paymentAmount}" />
		<input type="hidden" 	name="prePaymentAmount" 	value="${psiPartsOrder.prePaymentAmount}" />
		<input type="hidden" 	name="purchaseOrderId" 		value="${psiPartsOrder.purchaseOrderId}" />
		<input type="hidden" 	name="purchaseOrderNo" 		value="${psiPartsOrder.purchaseOrderNo}" />
		<input type="hidden" 	name="depositPreAmount" 	value="${psiPartsOrder.depositPreAmount}" />
		<input type="hidden" 	name="depositAmount" 		value="${psiPartsOrder.depositAmount}" />
		
	    <input type="hidden" 	name="createUser.id" 		value="${psiPartsOrder.createUser.id}" />
	    <input type="hidden" 	name="createDate" 			value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiPartsOrder.createDate}'/>" />
	    <input type="hidden" 	name="updateUser.id" 		value="${psiPartsOrder.updateUser.id}" />
	    <input type="hidden" 	name="updateDate" 			value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiPartsOrder.updateDate}'/>" />
	    
	    <input type="hidden" 	name="purchaseDate" 		value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiPartsOrder.purchaseDate}'/>" />
	    
	    <input type="hidden" 	name="sendEamil" 			value="${psiPartsOrder.sendEamil}" />
	    <input type="hidden" 	name="isProductReceive" 	value="${psiPartsOrder.isProductReceive}" />
		 <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px"><b>配件单号</b>:</label>
				<div class="controls" style="margin-left:120px">
				<span>
					<input type="text" readonly="readonly"  name="partsOrderNo" value="${psiPartsOrder.partsOrderNo}"/>
				</span>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" style="width:100px"><b>供应商</b>:</label>
				<div class="controls" style="margin-left:120px">
				<span>
					<input type="text" readonly="readonly"  value="${psiPartsOrder.supplier.nikename}"/>
					<input type="hidden" name="supplier.id" value="${psiPartsOrder.supplier.id}"/>
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:25%;height:30px" >
				<label class="control-label" style="width:100px"><b>定金</b>:</label>
				<div class="controls" style="margin-left:120px">
					<div class="input-prepend input-append">
						<input  type="text" class="number required" style="width:80%;" name="deposit" value="${psiPartsOrder.deposit}" /><span class="add-on">%</span>
					</div>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:25%;height:30px" >
				<label class="control-label"style="width:100px"><b>货币类型</b>:</label>
				<div class="controls" style="margin-left:120px">
					<input  type="text" readonly style="width:80%" name="currencyType" value="${psiPartsOrder.currencyType}" />
				</div>
			</div>
		</div>
		
		<div class="control-group"  style="float:left;width:100%">
			<label class="control-label" style="width:100px"><b>备注</b>:</label>
			<div class="controls" style="margin-left:120px">
				<textarea  maxlength="255" style="height:50px;width:98%" name="remark"  >${psiPartsOrder.remark}</textarea>
			</div>
		</div>
		
		
		<div style="float:left;width:100%">
		 <blockquote>
		 <div style="float: left"><p style="font-size: 14px">配件信息</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		</div>
		<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;">
			<c:if test="${psipartsOrder.orderSta eq '0'}">   
				<span class="icon-plus"></span><a href="#" id="add-row">增加配件</a>
			</c:if>
		</div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 30%">配件名称</th>
				   <th style="width: 10%">订单数量</th>
				   <th style="width: 10%">价格</th>
				   <th style="width: 20%">备注</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${psiPartsOrder.items}" var="item">
				<tr>
					<td>
					<input type="hidden" name="id" 					value="${item.id}"/>
					<input type="hidden" name="psiParts.id" 	    value="${item.psiParts.id}"/>
					<input type="hidden" name="quantityReceived" 	value="${item.quantityReceived}"/>
					<input type="hidden" name="quantityPreReceived" value="${item.quantityPreReceived}"/>
					<input type="hidden" name="quantityPayment" 	value="${item.quantityPayment}" />
					<input type="hidden" name="paymentAmount" 		value="${item.paymentAmount}" />
					<input type="hidden"name="deliveryDate" 	    value="<fmt:formatDate pattern='yyyy-MM-dd' value='${item.deliveryDate}'/>" />
					<input type="hidden"name="actualDeliveryDate" 	value="<fmt:formatDate pattern='yyyy-MM-dd' value='${item.actualDeliveryDate}'/>" />
					
					<input type="text" style="width: 80%"	   name="partsName" readonly="readonly" value="${item.partsName}" /></td>
					<td><input type="text" style="width: 80%"  name="quantityOrdered"  readonly="readonly" value="${item.quantityOrdered}" /></td>
					<td><input type="text" style="width: 80%"  name="itemPrice" class="price required"    value="${psiPartsOrder.currencyType eq 'CNY' ? item.psiParts.rmbPrice:item.psiParts.price}" /></td>
					<td><input type="text" style="width: 80%"  name="remark" readonly="readonly" value="${item.remark}"/></td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	
	
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
