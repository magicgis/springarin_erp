<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>OutOfStockInfo</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<style type="text/css">
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
		}
	</style>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		
		
		
		$(document).ready(function() {
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
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
        <li class="${empty psiOutOfStockInfo.country ?'active':''}"><a class="countryHref" href="#" key="">总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${psiOutOfStockInfo.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>
	<div  id="searchContent">
	<form:form id="searchForm"  modelAttribute="psiOutOfStockInfo" action="${ctx}/psi/psiOutOfStockInfo" method="post" class="breadcrumb form-search" >
		&nbsp;&nbsp;查询时间:&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"  readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${psiOutOfStockInfo.createDate}" pattern="yyyy-MM-dd" />" id="satrt" class="input-small"/>
		&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"  readonly="readonly"  class="Wdate" type="text" name="actualDate" value="<fmt:formatDate value="${psiOutOfStockInfo.actualDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			<input  name="country" type="hidden" value="${psiOutOfStockInfo.country}" />
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
	        <input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		    <input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		    <input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
	</form:form>
	</div>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
		    <tr>
				<th rowspan='2' style="text-align: center;vertical-align: middle;width:12%;">产品</th>
				<th rowspan='2' style="text-align: center;vertical-align: middle;width:8%;">Sku</th>
				<th rowspan='2' style="text-align: center;vertical-align: middle;width:3%;">国家</th>
				<th rowspan='2' style="text-align: center;vertical-align: middle;width:5%;">调价前</th> 
				<th rowspan='2'style="text-align: center;vertical-align: middle;width:5%;">调价后</th>
				<th rowspan='2' style="text-align: center;vertical-align: middle;width:5%;">FBA数量</th>
				<th rowspan='2' style="text-align: center;vertical-align: middle;width:5%;">31日销</th>
				<th rowspan='2' style="text-align: center;vertical-align: middle;width:5%;">平均日销</th>
				<th colspan='4' style="text-align: center;vertical-align: middle;">预计到货时间</th>
				<th rowspan='2' style="text-align: center;vertical-align: middle;width:8%;">调价时间</th>
			</tr>
			<tr><th style="text-align: center;vertical-align: middle;">在产</th>
				<th style="text-align: center;vertical-align: middle;">在途</th>
				<th style="text-align: center;vertical-align: middle;">中国仓</th>
				<th style="text-align: center;vertical-align: middle;">海外仓</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="info">
			<tr>
				<td>${info.productName}${not empty info.color?'_':''}${not empty info.color?info.color:''}</td>
				<td>${info.sku }</td>
				<td>${'com' eq info.country?'us':info.country }</td>
				<td  style="text-align: center;vertical-align: middle;">${info.beforePrice }</td> 
				<td style="text-align: center;vertical-align: middle;">${info.afterPrice }</td>
				<td style="text-align: center;vertical-align: middle;">${info.fbaQuantity }</td>
				<td style="text-align: center;vertical-align: middle;">${info.quantityDay31 }</td>
				<td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${info.quantityDay31/31}" maxFractionDigits="0" pattern="#0" /></td>
				<td style="text-align: center;vertical-align: middle;">${info.info2 }</td>
				<td style="text-align: center;vertical-align: middle;">${info.info1 }</td>
				<td style="text-align: center;vertical-align: middle;">${info.info3 }</td>
				<td style="text-align: center;vertical-align: middle;">${info.info4 }</td>
				<td style="text-align: center;vertical-align: middle;"><fmt:formatDate value="${info.createDate }" pattern="yyyy-MM-dd" /></td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
