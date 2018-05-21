<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>查看hscode</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.red{color:red;}
	</style>
<script type="text/javascript">
	$(document).ready(function() {
		$("#contentTable").dataTable({
			"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
			"sPaginationType" : "bootstrap",
			"iDisplayLength" : 15,
			"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
					[ 10, 20, 60, 100, "All" ] ],   
			"bScrollCollapse" : true,
			"oLanguage" : {
				"sLengthMenu" : "_MENU_ 条/页"
			},
			"ordering" : true,
			 "aaSorting": [[ 0, "desc" ]]
		});
	});
	</script>
<body>
	
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/product">产品列表</a></li>
		<shiro:hasPermission name="psi:product:edit"><li><a href="${ctx}/psi/product/add">新增产品</a></li></shiro:hasPermission>
		<li><a href="${ctx}/psi/product/showHscodes">查看Hscode</a></li>
		<li class="active"><a href="#">查看产品运输费用<b>（¥）</b></a></li>
	</ul>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
		<tr>
			<th style="">产品名</th>
			<th style="width: 3%">淘汰</th>
		    <th style="width: 5%">运输重量</th>
		    <th style="width: 5%">空运重量</th>
		    <th style="width: 5%">空运泡重(%)</th>
		    <th style="width: 5%">快递重量</th>
		    <th style="width: 5%">快递泡重(%)</th>
		    <th style="width: 5%;">EU 空运</th><th style="width: 5%;">EU 海运</th><th style="width: 5%;">EU 快递</th>	 
		    <th style="width: 5%;">US 空运</th><th style="width: 5%;">US 海运</th><th style="width: 5%;">US 快递</th>	
		    <th style="width: 5%;">JP 空运</th><th style="width: 5%;">JP 海运</th><th style="width: 5%;">JP 快递</th>   
		    <th style="width: 5%;">CA 空运</th><th style="width: 5%;">CA 海运</th><th style="width: 5%;">CA 快递</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${tranMoneyList}" var="product">
			<tr>
				<td>${product.name}</td><td>${product.isSale eq '0'?'是':'否'}</td><td>${product.tranGw}kg</td>
				<td>
				<fmt:formatNumber value="${product.airGw}" pattern="0.##"/>kg
				</td>
				<td>
				${product.airOverGw}
				</td>
				<td>
				<fmt:formatNumber value="${product.expressGw}" pattern="0.##"/>kg
				</td>
				<td>
				${product.expressOverGw}
				</td>
				<td ><fmt:formatNumber value="${product.airGw*airMap['eu']}" maxFractionDigits="2"/></td><td><fmt:formatNumber value="${product.tranGw*seaMap['eu']}" maxFractionDigits="2"/></td><td  ><fmt:formatNumber value="${product.expressGw*expressMap['eu']}" maxFractionDigits="2"/></td>
				<td ><fmt:formatNumber value="${product.airGw*airMap['com']}" maxFractionDigits="2"/></td><td><fmt:formatNumber value="${product.tranGw*seaMap['com']}" maxFractionDigits="2"/></td><td  ><fmt:formatNumber value="${product.expressGw*expressMap['com']}" maxFractionDigits="2"/></td>
				<td ><fmt:formatNumber value="${product.airGw*airMap['jp']}" maxFractionDigits="2"/></td><td><fmt:formatNumber value="${product.tranGw*seaMap['jp']}" maxFractionDigits="2"/></td><td  ><fmt:formatNumber value="${product.expressGw*expressMap['jp']}" maxFractionDigits="2"/></td>
				<td ><fmt:formatNumber value="${product.airGw*airMap['ca']}" maxFractionDigits="2"/></td><td/><td ><fmt:formatNumber value="${product.tranGw*expressMap['ca']}" maxFractionDigits="2"/></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div style="width:100%;text-align:center">
		<input id="btnCancel" class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="window.location.href ='${ctx}/psi/product'" />
	</div>
</body>
</html>