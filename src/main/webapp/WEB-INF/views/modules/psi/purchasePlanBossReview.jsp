<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title><spring:message code='psi_purchase_order'/>--<spring:message code='sys_but_add'/></title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	 
		
		$(document).ready(function() {
		
			
			
			$("#inputForm").validate({
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
				$("#btnSureSubmit").attr("disabled","disabled");
					
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("error");
					error.appendTo($("#errorsShow"));
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/purchasePlan/">新品采购计划列表</a></li>
		<li class="active"><a href="#">终极审核新品采购计划</a></li>
	</ul>
	<br/>
	
	<form:form id="inputForm" modelAttribute="purchasePlan" action="${ctx}/psi/purchasePlan/bossReviewSave" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input type="hidden" name="id"     				value="${purchasePlan.id}" />
	    <input type="hidden" name="oldItemIds"			value="${purchasePlan.oldItemIds}" />
	    <input type="hidden" name="planSta" id="planSta" value="${purchasePlan.planSta}" />
	    <input type="hidden" name="createUser.id"		value="${purchasePlan.createUser.id}" />
	    <input type="hidden" name="createDate" 			value="<fmt:formatDate pattern='yyyy-MM-dd' value='${purchasePlan.createDate}'/>" />
	    <input type="hidden" name="remark"				value="${purchasePlan.remark}" />
	    <input type="hidden" name="reviewUser.id"		value="${purchasePlan.reviewUser.id}" />
	    <input type="hidden" name="reviewDate" 			value="<fmt:formatDate pattern='yyyy-MM-dd' value='${purchasePlan.reviewDate}'/>" />
	    <input type="hidden" name="attFilePath"  		value="${purchasePlan.attFilePath}" />
	    <input type="hidden" name="productPosition"  	value="${purchasePlan.productPosition}" />
	    <blockquote>
			<p style="font-size: 14px"><b>基本信息：</b></p>
		</blockquote>
		
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:100%" >
				<label class="control-label" style="width:80px">备注:</label>
				<div class="controls" style="margin-left:100px">
					${purchasePlan.remark}
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:100%" >
				<label class="control-label" style="width:80px">产品定位:</label>
				<div class="controls" style="margin-left:100px">
					${fns:getDictLabel(purchasePlan.productPosition,'product_position','')}
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:100%" >
				<label class="control-label" style="width:80px">销售计划:</label>
				<div class="controls" style="margin-left:100px">
					<c:if test="${not empty purchasePlan.attFilePath}"><a href="${ctx}/psi/purchasePlan/download?fileName=/${purchasePlan.attFilePath}&productName=${purchasePlan.productName}">查看</a></c:if>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100%">
		 <blockquote>
		 <div style="float: left"><p style="font-size: 14px"><b><spring:message code='psi_product_productInfo'/>:</b></p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		</div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 15%"><spring:message code='psi_product_name'/></th>
				   <th style="width: 5%"><spring:message code='sys_label_country'/></th>
				   <th style="width: 5%">MOQ</th>
				   <th style="width: 5%">装箱数</th>
				   <th style="width: 10%">备注</th>
				   <th style="width: 10%">初审备注</th>
				   <th style="width: 5%">申请数量</th>
				   <th style="width: 5%">初审数量</th>
				   <th style="width: 5%">终极数量</th>
				   <th style="width: 15%">终极备注</th>
				   
				   
			</tr>
		</thead>
			<c:if test="${not empty purchasePlan.id}">
				<c:forEach items="${purchasePlan.items}" var="item" >
					<tr>
						<td>
							<input type="hidden" name="id" value="${item.id}" />
							<input type="hidden" name="plan.id" value="${item.plan.id}" />
							<input type="hidden" name="colorCode" value="${item.colorCode}"/>
							<input type="hidden" name="countryCode" value="${item.countryCode}"/>
							<input type="hidden" name="productName" value="${item.productName}"/>
							<input type="hidden" name="product.id" value="${item.product.id}"/>
							<input type="hidden" name="remark" value="${item.remark}"/>
							<input type="hidden" name="remarkReview" value="${item.remarkReview}"/>
							<input type="hidden" name="quantity" value="${item.quantity}"/>
							<input type="hidden" name="quantityReview" value="${item.quantityReview}"/>
							<input type="hidden" name="delFlag" value="${item.delFlag}"/>
							${item.productNameColor}
						</td>
						<td>${fns:getDictLabel(item.countryCode, 'platform', '')}</td>
						<td>${item.product.minOrderPlaced}</td>
						<td>${item.product.packQuantity}</td>
						<td>${item.remark}</td>
						<td>${item.remarkReview}</td>
						<td>${item.quantity}</td>
						<td>${item.quantityReview}</td>
						<td><input type="text" maxlength="11" style="width: 80%" name="quantityBossReview" class="number" value="${item.quantityReview}"/></td>
						<td><input type="text" style="width: 80%" name="remarkBossReview" /></td>
					</tr>
			</c:forEach>
		</c:if>
	</table>
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-info" type="submit" value="审核通过"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
