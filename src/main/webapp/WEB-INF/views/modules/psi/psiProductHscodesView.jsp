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
		<li class="active"><a href="#">查看Hscode</a></li>
	</ul>
	<div class="alert alert-info" style="width:100%;text-align:center"><b>进口税： 
	 DE:VAT:19%;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	 UK:VAT:20%;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	 FR:VAT:20%;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	 IT:VAT:22%;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	 ES:VAT:21%;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  
	 MX:VAT:16%;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;  
	 US:MPF+HMF=0.47%;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	 JP:TAX+VAT=8%;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	 CA:GST+HST=15%</b></div> 
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
		
			<tr>
				   <th style="width: 10%">产品名</th>
				   <th style="width: 15%">EU <span style="margin-right:20px;float:right">Duty</span></th>
				   <th style="width: 15%">US  <span style="margin-right:20px;float:right">Duty</span></th>
				   <th style="width: 15%">JP <span style="margin-right:20px;float:right">Duty</span></th>
				   <th style="width: 15%">CA <span style="margin-right:20px;float:right">Duty</span></th>
				   <th style="width: 15%">MX <span style="margin-right:20px;float:right">Duty</span></th>
				   <th style="width: 15%">HK</th>
				   <th style="width: 15%">CN</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${proList}" var="product">
			<tr>
				<td style="text-align:center;vertical-align: middle;">${product.name}</td>
				<td>${product.euHscode}<span style="margin-right:20px;float:right">${(product.euCustomDuty eq 0 || empty product.euCustomDuty)?'Free':product.euCustomDuty} ${(product.euCustomDuty eq 0 || empty product.euCustomDuty)?'':'%'}</span></td>
				<td>${product.usHscode}<span style="margin-right:20px;float:right">${(product.usCustomDuty eq 0 || empty product.usCustomDuty)?'Free':product.usCustomDuty} ${(product.usCustomDuty eq 0 || empty product.usCustomDuty)?'':'%'}</span></td>
				<td>${product.jpHscode}<span style="margin-right:20px;float:right">${(product.jpCustomDuty eq 0 || empty product.jpCustomDuty)?'Free':product.jpCustomDuty} ${(product.jpCustomDuty eq 0 || empty product.jpCustomDuty)?'':'%'}</span></td>
				<td>${product.caHscode}<span style="margin-right:20px;float:right">${(product.caCustomDuty eq 0 || empty product.caCustomDuty)?'Free':product.caCustomDuty} ${(product.caCustomDuty eq 0 || empty product.caCustomDuty)?'':'%'}</span></td>
				<td>${product.mxHscode}<span style="margin-right:20px;float:right">${(product.mxCustomDuty eq 0 || empty product.mxCustomDuty)?'Free':product.mxCustomDuty} ${(product.mxCustomDuty eq 0 || empty product.mxCustomDuty)?'':'%'}</span></td>
				<td>${product.hkHscode}</td>
				<td>${product.cnHscode}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div style="width:100%;text-align:center">
		<input id="btnCancel" class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="window.location.href ='${ctx}/psi/product'" />
	</div>
</body>
</html>