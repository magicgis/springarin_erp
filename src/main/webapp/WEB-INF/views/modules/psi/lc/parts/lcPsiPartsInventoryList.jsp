<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>配件库存管理</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	
	
	<script type="text/javascript">
		
		$(document).ready(function() {
			
			$("#takingInBtn").click(function(){
				var params = {};
				params.takingType='0';
				window.location.href = "${ctx}/psi/lcPsiPartsInventoryTaking/form?"+$.param(params);
			});
			
			$("#inventoryAdjust").click(function(){
				window.location.href = "${ctx}/psi/lcPsiPartsInventory/adjust";
			});
			
			
			$("#takingOutBtn").click(function(){
				var params = {};
				params.takingType='1';
				window.location.href = "${ctx}/psi/lcPsiPartsInventoryTaking/form?"+$.param(params);
			});
			
			// 表格排序
			var orderBy = $("#orderBy").val().split(" ");
			$("#contentTable th.sort").each(function(){
				if ($(this).hasClass(orderBy[0])){
					orderBy[1] = orderBy[1]&&orderBy[1].toUpperCase()=="DESC"?"down":"up";
					$(this).html($(this).html()+" <i class=\"icon icon-arrow-"+orderBy[1]+"\"></i>");
				}
			});
			
			
			$("#contentTable th.sort").click(function(){
				var order = $(this).attr("class").split(" ");
				var sort = $("#orderBy").val().split(" ");
				for(var i=0; i<order.length; i++){
					if (order[i] == "sort"){order = order[i+1]; break;}
				}
				if (order == sort[0]){
					sort = (sort[1]&&sort[1].toUpperCase()=="DESC"?"ASC":"DESC");
					$("#orderBy").val(order+" DESC"!=order+" "+sort?"":order+" "+sort);
				}else{
					$("#orderBy").val(order+" ASC");
				}
				page();
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
		<li ><a href="${ctx}/psi/psiPartsInventory/">配件库存列表</a></li>
		<li class="active"><a href="${ctx}/psi/lcPsiPartsInventory/">(理诚)配件库存列表</a></li>
	</ul><br/>
	<form:form id="searchForm" modelAttribute="psiPartsInventory" action="${ctx}/psi/lcPsiPartsInventory/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<label>配件名称 ：</label><input type="text" name="partsName" value="${psiPartsInventory.partsName}" style="width:150px"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/> &nbsp;&nbsp;&nbsp;&nbsp;
		<input id="inventoryAdjust" class="btn btn-info" type="button" value="库存转换"/>&nbsp;&nbsp;&nbsp;&nbsp;   
		<input id="takingOutBtn" class="btn btn-warning" type="button" value="盘出"/>&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="takingInBtn" class="btn btn-success" type="button" value="盘入"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:20%">配件名称</th><th class="sort poNotFrozen" style="width:15%">po可用数</th><th class="sort poFrozen" style="width:15%">po冻结数</th><th class="sort stockNotFrozen" style="width:15%">stock可用数</th><th class="sort stockFrozen" style="width:15%">stock冻结数</th><th>操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="lcPsiPartsInventory">
			<tr>
				<td>${lcPsiPartsInventory.partsName}</td>
				<td>${lcPsiPartsInventory.poNotFrozen}</td>
				<td>${lcPsiPartsInventory.poFrozen}</td>
				<td>${lcPsiPartsInventory.stockNotFrozen}</td>
				<td>${lcPsiPartsInventory.stockFrozen}</td>
				<td><a class="btn btn-small" href="${ctx}/psi/lcPsiPartsInventoryLog?partsId=${lcPsiPartsInventory.partsId}">查看日志</a></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
