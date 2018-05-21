<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>配件出库管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
			$(".open").click(function(e){
				if($(this).text()=='概要'){
					$(this).text('关闭');
				}else{
					$(this).text('概要');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
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
		<li><a href="${ctx}/psi/psiPartsInventoryOut/">配件出库列表</a></li>
	 	<li><a href="${ctx}/psi/psiPartsInventoryOut/form">配件出库添加</a></li>
		<li class="active"><a href="${ctx}/psi/lcPsiPartsInventoryOut/">(理诚)配件出库列表</a></li>
	 	<li><a href="${ctx}/psi/lcPsiPartsInventoryOut/form">(理诚)配件出库添加</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="lcPsiPartsInventoryOut" action="${ctx}/psi/lcPsiPartsInventoryOut/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>产品订单号：</label>
		<input name="billNo" value="${lcPsiPartsInventoryOut.billNo}" type="text" style="width:150px"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<label>产品名称 ：</label>
		<input name="productName" value="${lcPsiPartsInventoryOut.productName}" type="text" style="width:150px"/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<label>配件名称 ：</label>
		<input name="remark" value="${lcPsiPartsInventoryOut.remark}" type="text" style="width:150px"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:5%">序号</th><th style="width:20%">产品名</th><th style="width:10%">产品数</th><th style="width:10%">创建人</th><th style="width:10%">创建时间</th><th >操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="lcPsiPartsInventoryOut">
			<tr>
				<td>${lcPsiPartsInventoryOut.id}</td><td>${lcPsiPartsInventoryOut.productNameColor}</td><td>${lcPsiPartsInventoryOut.quantity}</td><td>${lcPsiPartsInventoryOut.createUser.name}</td><td><fmt:formatDate pattern="yyyy-MM-dd" value="${lcPsiPartsInventoryOut.createDate}"/></td>
				<td>
    				<input type="hidden" value="${lcPsiPartsInventoryOut.id}"/>
				<a class="btn btn-small btn-info open">概要</a>&nbsp;&nbsp;
				</td>
			</tr>
			<c:if test="${fn:length(lcPsiPartsInventoryOut.items)>0}">
				<tr style="background-color:#D2E9FF;display: none" name="${lcPsiPartsInventoryOut.id}"><td></td><td>配件名称</td><td>配件数量</td><td colspan="3">相关订单及配送数</td></tr>
				<c:forEach items="${lcPsiPartsInventoryOut.items}" var="item" varStatus="i">
					<tr style="background-color:#D2E9FF;display: none" name="${lcPsiPartsInventoryOut.id}">
					<td></td>
					<td>${item.partsName}</td><td>${item.quantity}</td>
					<c:if test="${i.index==0}">
						<td  colspan="3" rowspan="${fn:length(lcPsiPartsInventoryOut.items)}">
						<c:forEach items="${lcPsiPartsInventoryOut.orders}" var="orderItem">
							订单号：${orderItem.purchaseOrderNo},数量：${orderItem.quantity} ;<br/>
						</c:forEach>
						</td>
					</c:if>
					</tr>
				</c:forEach>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
