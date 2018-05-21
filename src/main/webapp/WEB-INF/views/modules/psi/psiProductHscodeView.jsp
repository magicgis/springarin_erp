<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ include file="/WEB-INF/views/include/dialog.jsp"%>
<html>
<head>
<meta name="decorator" content="default" />
<title>产品hscode查看</title>
</head>
<body>
	<div id="imgtest"></div> 
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/product">产品列表</a></li>
		<li class="active"><a href="#">产品hscode</a></li>
	</ul>
	<br />
	<tags:message content="${message}" />
	<form:form id="inputForm" modelAttribute="product" action="${ctx}/psi/product/save" method="post" class="form-horizontal" enctype="multipart/form-data">
	<input type="hidden" name="id" value="${product.id}" />
	<blockquote>
		<p style="font-size: 14px"><b>${product.name}</b></p>
	</blockquote>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>

			<tr>
				   <th style="width: 15%"/>
				   <th style="width: 15%">EU</th>
				   <th style="width: 15%">US</th>
				   <th style="width: 15%">JP</th>
				   <th style="width: 15%">CA</th>
				   <th style="width: 15%">HK</th>
				   <th style="width: 15%">CN</th>
			</tr>
		</thead>
				<tbody>
					<tr>
						<td><b>海关编码</b></td>
						<td>${product.euHscode}</td>
						<td>${product.usHscode}</td>
						<td>${product.jpHscode}</td>
						<td>${product.caHscode}</td>
						<td>${product.hkHscode}</td>
						<td>${product.cnHscode}</td>
					</tr>
					<tr>
						<td><b>关税</b></td>
						<td>${product.euCustomDuty}</td>
						<td>${product.usCustomDuty}</td>
						<td>${product.jpCustomDuty}</td>
						<td>${product.caCustomDuty}</td>
						<td/>
						<td/>
					</tr>
					<tr>
						<td><b>进口税</b></td>
						<td>${product.euImportDuty}</td>
						<td>${product.usImportDuty}</td>
						<td>${product.jpImportDuty}</td>
						<td>${product.caImportDuty}</td>
						<td/>
						<td/>
					</tr>
				</tbody>
	</table>
	<div style="width:100%;text-align:center">
		<input id="btnCancel" class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="window.location.href ='${ctx}/psi/product'" />
	</div>
	</form:form>
</body>
</html>