<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Barcode Search</title>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		(function() {
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
		})();
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
		<li ><a href="${ctx}/psi/product/barcodeslist">Current Barcode</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/amazonProduct/barcodeSearch">Search History Barcode</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="amazonProduct2" action="${ctx}/amazoninfo/amazonProduct/barcodeSearch" method="post" class="breadcrumb form-search">
		<div style="height: 30px">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
			<input name="country" type="hidden" value="${amazonProduct2.country}"/>
				<label>Asin/Ean/Sku/Barcode:</label><form:input path="sku" htmlEscape="false" maxlength="50" class="input-small"/>
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="search"/>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table  id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
			<th>ProductName</th>
			<th>Country</th>
			<th>Sku</th>
			<th>Fnsku</th>
			<th>Ean</th>
			<th>Asin</th>
			<th>Statu</th>
		<tbody>
		<c:forEach items="${page.list}" var="amazonProduct2">
			<tr>
				<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}>${nameMap[amazonProduct2.asin]}</td>
				<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}>${fns:getDictLabel(amazonProduct2.country,'platform','')}</td>
				<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}>${amazonProduct2.sku}</td>
				<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}>${empty amazonProduct2.fnsku?'Local':amazonProduct2.fnsku}</td>
				<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}>${amazonProduct2.ean}</td>
				<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}><a href="http://www.amazon.${amazonProduct2.country eq 'jp' || amazonProduct2.country eq 'uk'?'co.':''}${amazonProduct2.country eq 'com.unitek'||amazonProduct2.country eq 'com2'?'com':amazonProduct2.country}/dp/${amazonProduct2.asin}" target="_blank">${amazonProduct2.asin}</a></td>
				<td ${amazonProduct2.active eq '1' ?'':'style= background-color:#C5C1AA'}>${amazonProduct2.active eq '1' ?'Active':'Delete'}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
