<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品配件订单生成依据管理</title>
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
			$("#partsType,#supplier").on("click",function(){
				$("#searchForm").submit();
			});
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
		<li class="active"><a href="${ctx}/psi/psiPartsOrderBasis/">产品配件订单生成依据列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiPartsOrderBasis" action="${ctx}/psi/psiPartsOrderBasis/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>配件名称 /采购订单NO.：</label><input type="text" name="partsName" value="${psiPartsOrderBasis.partsName}" style="width:150px"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:5%">序号</th><th style="width:20%">采购订单NO.</th><th style="width:15%" >配件名称</th><th style="width:10%">需要数量</th><th style="width:10%">订单数</th><th style="width:10%">PO可用数</th><th style="width:10%">PO冻结数</th><th style="width:10%">库存可用数</th><th style="width:10%">库存冻结数</th><th style="width:20%">备注</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="psiPartsOrderBasis">
			<tr>
				<td>${psiPartsOrderBasis.id}</td>
				<td>${psiPartsOrderBasis.purchaseOrderNo}</td>
				<td>${psiPartsOrderBasis.partsName}</td>
				<td>${psiPartsOrderBasis.needQuantity}</td>
				<td>${psiPartsOrderBasis.orderQuantity}</td>
				<td>${psiPartsOrderBasis.poNotFrozen}</td>
				<td>${psiPartsOrderBasis.poFrozen}</td>
				<td>${psiPartsOrderBasis.stockNotFrozen}</td>
				<td>${psiPartsOrderBasis.stockFrozen}</td>
				<td>${psiPartsOrderBasis.remark}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
