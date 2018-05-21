<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>贴码错误管理</title>
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
		});
	
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
	</script> 
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="">贴码错误列表</a></li>
		<shiro:hasPermission name="psi:psiQuestionBarcode:edit">
		<li ><a href="${ctx}/psi/psiQuestionBarcode/form">增加贴码错误</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="psiQuestionBarcode" action="${ctx}/psi/psiQuestionBarcode/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>产品名称/错误方/错误原因/运单号 ：</label><input type="text" name="productName" value="${psiQuestionBarcode.productName}" style="width:150px"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<tags:message content="${message}"/>
	
	<div id="imgtest"></div> 
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:5%">序号</th><th style="width:15%">产品名</th><th style="width:10%" >数量</th><th style="width:10%">运单号</th><th style="width:10%">错误方</th><th style="width:20%">出错原因</th><th style="width:10%">录入人</th><th>操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="psiQuestionBarcode">
			<tr>
				<td>${psiQuestionBarcode.id}</td>
				<td>${psiQuestionBarcode.productName}</td>
				<td>${psiQuestionBarcode.quantity}</td>
				<td>${psiQuestionBarcode.transportOrderNo}</td>
				<td>${psiQuestionBarcode.wrongSide}</td>
				<td>${psiQuestionBarcode.reason}</td>
				<td>${psiQuestionBarcode.createUser.name}</td>
				<td>
    				<a class="btn btn-small" href="${ctx}/psi/psiQuestionBarcode/form?id=${psiQuestionBarcode.id}">修改</a>
					<a class="btn btn-small" href="${ctx}/psi/psiQuestionBarcode/delete?id=${psiQuestionBarcode.id}" onclick="return confirmx('确认要删除该贴码错误信息吗？', this.href)">删除</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
