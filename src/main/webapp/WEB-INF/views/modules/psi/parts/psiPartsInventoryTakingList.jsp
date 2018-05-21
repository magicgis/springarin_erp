<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>配件库存盘点管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#takingType").change(function(){
				$("#searchForm").submit();
			});
			
			
			$(".open").click(function(e){
				if($(this).text()=='Summary'){
					$(this).text('Closed');
				}else{
					$(this).text('Summary');
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
		<li class="active"><a href="#">配件库存盘点列表</a></li>
		<li><a href="${ctx}/psi/lcPsiPartsInventoryTaking">(理诚)配件库存盘点列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiPartsInventoryTaking" action="${ctx}/psi/psiPartsInventoryTaking/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>盘点编号 ：</label><form:input path="takingNo" htmlEscape="false" maxlength="50" class="input-small"/>&nbsp;&nbsp;&nbsp;&nbsp;
		<label>配件名称 ：</label><form:input path="remark" htmlEscape="false" maxlength="50" class="input-small"/>&nbsp;&nbsp;&nbsp;&nbsp;
		<label>盘点类型 ：</label>
		<select name="takingType" id="takingType">
			<option value="" >全部</option>
			<option value="0" ${psiPartsInventoryTaking.takingType eq '0'?'selected':''} >盘入</option>
			<option value="1" ${psiPartsInventoryTaking.takingType eq '1'?'selected':''} >盘出</option>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<table id="contentTable" class="table table-bordered table-condensed">
	<thead><tr><th style="width:20%">盘点编号</th><th style="width:15%">盘点类型</th><th style="width:15%">盘点人</th><th style="width:15%">盘点时间</th><th style="width:20%">备注</th><th>操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="taking">
		<tr>
			<td>${taking.takingNo}</td>
			<td>${taking.takingType eq '0' ?'盘入':'盘出'}</td><td>${taking.createUser.name}</td><td><fmt:formatDate pattern="yyyy-MM-dd" value="${taking.createDate}"/></td><td>${taking.remark}</td><td><input type="hidden" value="${taking.id}"/><a class="btn btn-small btn-info open">Summary</a></td>
		</tr>
		<c:if test="${fn:length(taking.items)>0}">
				<tr style="background-color:#D2E9FF;display: none" name="${taking.id}"><td>配件名称</td><td>PoFrozen</td><td>PoNotFrozen</td><td>StockFrozen</td><td>StockNotFrozen</td><td colspan="2"></td></tr>
				<c:forEach items="${taking.items}" var="item">
					<tr style="background-color:#D2E9FF;display: none" name="${taking.id}">
					<td>${item.partsName}</td><td>${item.poFrozen}</td><td>${item.poNotFrozen}</td><td>${item.stockFrozen}</td><td>${item.stockNotFrozen}</td><td colspan="3"></td>
					</tr>
				</c:forEach>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
