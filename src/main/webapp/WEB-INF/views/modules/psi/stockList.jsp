<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>仓库管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
		if(!(top)){
			top = self;			
		}	
		$(document).ready(function() {
			$("a[rel='popover']").popover({trigger:'hover'});
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
		<li  class="active"><a href="${ctx}/psi/stock/">仓库列表</a></li>
		<shiro:hasPermission name="psi:stock:edit"><li><a href="${ctx}/psi/stock/form">新增仓库</a></li></shiro:hasPermission>
	</ul>
        <script type="text/javascript">
            function addStockArea(){
                top.$.jBox.open("iframe:${ctx}/psi/stockArea/positionArea?id=${role.id}&type=${type}", "仓库",810,$(top.document).height()-240,{
                    buttons:{"关闭":true}, bottomText:"库区库位管理", loaded:function(h){
                        $(".jbox-content", top.document).css("overflow-y","hidden");
                    }
                });
            }
         </script>
	<form:form id="searchForm" modelAttribute="stock" action="${ctx}/psi/stock/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>仓库代号 ：</label><form:input path="name" htmlEscape="false" maxlength="50" class="input-small"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>           
		 <a id="addStockArea" onclick="addStockArea()" href="javascript:"  class="btn btn-primary">
                           库区库位管理
         </a>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<th>仓库名称</th>
			<th>类型</th>
			<th>所属平台</th>
			<th>代号</th>
			<th>实时库容(m³)</th>
			<th>仓库容量(m³)</th>
			<th>库容占比(%)</th>
			<th>地址</th>
			<th>邮编</th>
			<shiro:hasPermission name="psi:stock:edit">
				<th>操作</th>
			</shiro:hasPermission>
			</tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="stock">
			<tr>
				<td><c:if test="${stock.type eq '0'}"><a href="${ctx}/psi/stock/form?id=${stock.id}">${stock.stockName}</a></c:if><c:if test="${stock.type eq '1'}">${stock.stockName}</c:if></td>
				<td>${stock.type eq '0'?'本地仓库':'FBA仓库'}</td>
				<td>${fns:getDictLabel(stock.platform,'platform','')}</td>
				<td>${stock.stockSign}</td>
				<td>${capMap[stock.id]}</td>
				<td>${stock.capacity}</td>
				<td>
					<c:if test="${not empty stock.capacity && stock.type eq '0'}">
						<c:set value="${capMap[stock.id]*100/stock.capacity}" var="caRate"/>
						<span style="color:${caRate>80?'red':'green'}"><b><fmt:formatNumber value="${caRate}" pattern="0.00"/></b> </span>
					</c:if>
					
				</td>
				<td><a rel="popover" data-content="${stock.address}">${fn:substring(stock.address,0,30)}</a></td>
				<td>${stock.postalcode}</td>
				<shiro:hasPermission name="psi:stock:edit"><td>
					<c:if test="${stock.type eq '0'}">
    					<a href="${ctx}/psi/stock/form?id=${stock.id}">编辑</a>
    				</c:if>
					<a href="${ctx}/psi/stock/delete?id=${stock.id}" onclick="return confirmx('确认要删除该仓库吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
