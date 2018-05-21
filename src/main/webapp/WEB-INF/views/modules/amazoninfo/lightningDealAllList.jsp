<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>list</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<style type="text/css">
		.spanexr{ float:right;min-height:40px;padding-top: 5px}
		.spanexl{ float:left;}
		.blue{background-color:#D2E9FF;font-style: italic;font-weight: bold;}
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
			
				 var oTable = $("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
						"sPaginationType": "bootstrap","sScrollX": "100%",
					 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
					 	"aaSorting": [[0, "desc" ]]
					});
				
		});

		function exportList(){
			$("#searchForm").attr("action","${ctx}/amazoninfo/promotionsWarning/exportLightDeal");
			$("#searchForm").submit();
			$("#searchForm").attr("action","${ctx}/amazoninfo/promotionsWarning/lightningDealAllList");
		}
		
	</script>
</head>
<body>
    <ul class="nav nav-tabs">
	<c:forEach items="${fns:getDictList('platform')}" var="dic">
		<c:if test="${dic.value ne 'com.unitek'}">
			<li class="${amazonLightningDeals.country eq dic.value ?'active':''}"><a class="countryHref" href="${ctx}/amazoninfo/promotionsWarning/lightningDealAllList?country=${dic.value}" key="${dic.value}">${dic.label}</a></li>
		</c:if>
	</c:forEach>
	</ul>	
<!-- 	<div style="display: none" id="searchContent"> -->
		<form id="searchForm"  action="${ctx}/amazoninfo/promotionsWarning/lightningDealAllList" method="post"  class="breadcrumb form-search">
		<label><b>Starts：</b></label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="start" value="<fmt:formatDate value="${amazonLightningDeals.start}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;至&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${amazonLightningDeals.end}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
	     &nbsp;
				<b><spring:message code="amazon_sales_product_line"/>:</b><select id="productName" style="width: 150px" name="productName">
						<option value="">--All--</option>
						<c:forEach items="${groupType}" var="groupType">
							<option value="${groupType.id}" ${fn:trim(groupType.id) eq fn:trim(amazonLightningDeals.productName)?'selected':''}>${groupType.name}</option>			
						</c:forEach>
			        </select>
			        <input type='hidden' name='country' value='${amazonLightningDeals.country}' />
				&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>	&nbsp;&nbsp;
				<input id="exportSubmit" class="btn btn-primary" type="button" value="导出" onclick="exportList();"/>	&nbsp;&nbsp;
	</form>
<!-- 	</div> -->
	
	<tags:message content="${message}"/>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr> <th>闪促名称</th>
					<th>开始时间</th>
					<th>结束时间</th>
					<th>产品名称</th>
					<th>SKU</th>
					<th>售价(€)</th>
					<th>闪促价(€)</th>
					<th>闪促数量</th>
					<th>操作</th>
		</tr></thead>
		<tbody>
		<c:forEach items="${map}" var="deals">
		    <c:forEach items="${map[deals.key]}" var="deal">
		          <tr>
					<td>${deal.internalDesc}</td>
					<td><fmt:formatDate value="${deal.start}" pattern="yyyy-MM-dd HH:mm"/></td>
					<td><fmt:formatDate value="${deal.end}" pattern="yyyy-MM-dd HH:mm"/></td>
					<td>${deal.productName}</td>
					<td>${deal.sku}</td>
					<td><fmt:formatNumber  pattern="#######.##" value="${deal.salePrice*rate}"  maxFractionDigits="2" /></td>
					<td><fmt:formatNumber  pattern="#######.##" value="${deal.dealPrice*rate}"  maxFractionDigits="2" /></td>
				    <td>${deal.actualQuantity}</td>
				    <td><a target='_blank' href="${ctx}/amazoninfo/promotionsWarning/lightningDealList?country=${deal.country}&name=${deal.productName}&endDate=${deal.end}">分析</a></td>
			     </tr>
		    </c:forEach>
		</c:forEach>
		</tbody>
	</table>
	
</body>
</html>
