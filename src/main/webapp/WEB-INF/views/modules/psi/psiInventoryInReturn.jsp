<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Outbound order</title>
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
			submitHandler: function(form){
					top.$.jBox.confirm("<span style='color:red;font-weight:bold'>After confirmation will change the number of inventory,Operation will not be reversible,Are you sure?</span>","System Tips",function(v,h,f){
					if(v=='ok'){
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
					}
					},{buttons: { 'Confirm': 'ok', 'Cancel': ''}},{buttonsFocus:1,persistent: true});
					top.$('.jbox-body .jbox-icon').css('top','55px');
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
		<li class="active"><a href="#" style="font-size: 16px">Create Stock in Entry</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="psiInventoryIn" action="${ctx}/psi/psiInventoryIn/addSave" method="post" class="form-horizontal" enctype="multipart/form-data" >
		<input type="hidden" name="warehouseId" value="${psiInventoryIn.warehouseId}" >
		<input type="hidden" name="returnTestId" value="${returnTestId}" >
		<input type="hidden" name="billNo" value="${psiInventoryIn.billNo}" >
		
		<input type="hidden" name="tranLocalNo" class="tranLocalNo">
		<blockquote style="float:left;width:98%;">
			<p style="font-size: 15px;font-weight: bold">Base Info.</p>
		</blockquote>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label" style="width:100px"><b>Warehouse:</b></label>
				<div class="controls" style="margin-left:120px">
					 <input type="text" name="warehouseName" readonly="readonly" value="${psiInventoryIn.warehouseName}" />
					 <input type="hidden" name="warehouseId" value="${psiInventoryIn.warehouseId}" />
					 <c:choose>
						<c:when test="${psiInventoryIn.warehouseName eq 'Germany' }"><input type="hidden" name="warehouseName" value="德国本地A"/></c:when>
						<c:when test="${psiInventoryIn.warehouseName eq 'China' }"><input type="hidden" name="warehouseName" value="中国本地A"/></c:when>
						<c:when test="${psiInventoryIn.warehouseName eq 'American' }"><input type="hidden" name="warehouseName" value="美国本地A"/></c:when>
						<c:otherwise><input type="hidden" name="warehouseName" value="${psiInventoryIn.warehouseName}"/></c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="control-group" style="float:left;width:35%;height:30px">
					<label class="control-label" style="width:100px">Certificate:</label>
					<div class="controls" style="margin-left:120px">
					<input name="memoFile" type="file" id="myfileupload" />
					</div>
			</div>
			<div class="control-group" style="float:left;width:35%;height:30px">
				<label class="control-label" style="width:100px"><b>Type:</b></label>
				<div class="controls" style="margin-left:120px">
				<input type="text" readonly="readonly" name="operationType" value="Return Storing" />
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100% ;">
		<div class="control-group" style="float:left;width:98%">
				<label class="control-label" style="width:100px"><b>Remark:</b></label>
				<div class="controls" style="margin-left:120px">
					<form:textarea path="remark" htmlEscape="false" rows="4" maxlength="200" style="width:98%; height: 60px;" id="bigRemark"/>
				</div>
		</div>
		</div>
		<div style="display: block" id="itemData">
			 <blockquote style="float:left;width:98%">
			 <div style="float: left"><p style="font-size: 15px;font-weight: bold">Product Info.</p></div><div style="float: left" id=errorsShow></div>
			</blockquote>
			
			<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;"></div>
			<table id="contentTable" class="table table-striped table-bordered table-condensed" >
			<thead>
				<tr>
					   <th style="width: 30%">Product[SKU]</th>
					   <th style="width: 10%">Type</th>
					   <th style="width: 10%">Quantity</th>
					   <th style="width: 30%">Remark</th>
					   <th>Operate</th>
					   
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${psiInventoryIn.items}" var="item" varStatus="i">
					<tr>
					<c:if test="${i.index==0}">
						<td rowspan="${fn:length(psiInventoryIn.items)}" style="text-align: center;vertical-align: middle">
						${item.productName}[${item.sku}]</td>
					</c:if>
					<td>
						<input type="hidden" name="sku" value="${item.sku}"/><input type="hidden" name="productName" value="${item.productName}"/><input type="hidden" name="productId" value="${item.productId}"/>
						<input type="hidden" name="colorCode" value="${item.colorCode}"/><input type="hidden" name="countryCode" value="${item.countryCode}"/>
						<input type="text" readonly="readonly" name="qualityType" value="${item.qualityType}"/>
					</td>
					<td><input type="text" readonly="readonly" name="quantity" value="${item.quantity}"/></td>
					<td><input type="text"  name="remark" value=""/></td>
					</tr>
				</c:forEach>
			</tbody>
			
		</table>
		</div>
		<div class="form-actions" style="float:left;width:100%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="Create"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="Back" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
