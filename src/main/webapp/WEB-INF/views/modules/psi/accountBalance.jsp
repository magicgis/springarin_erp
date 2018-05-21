<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊到账日期</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.red{color:red;}
	</style>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	
	$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn ){
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr,i) {
			return $('td:eq('+iColumn+')', tr).text().replace(/,/g, "");
		} );
	};
	
		$(document).ready(function() {
			
			$("#supplier").click(function(){
				$("#searchForm").submit();
			});
			
		});
	</script>
</head>
<body>
    <ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/purchaseOrder/reconciliation">对账</a></li>
		<!-- <li ><a href="${ctx}/psi/purchaseOrder/reconciliation2">资金计划</a></li> <li ><a href="${ctx}/psi/purchaseOrder/forecast">采购资金计划</a></li>-->
		<li ><a href="#">采购支付报表</a></li>
		<li ><a href="${ctx}/psi/purchaseOrder/capitalBudget">资金分配计划</a></li>
		<li ><a href="${ctx}/psi/purchaseOrder/overDetail">逾期付款详细</a></li>
		
		<li ><a href="${ctx}/psi/lcPurchaseOrder/reconciliation">(理诚)对账</a></li>
		<li ><a href="${ctx}/psi/lcPurchaseOrder/fReport">(理诚)采购支付报表</a></li>
		<li ><a href="${ctx}/psi/lcPurchaseOrder/capitalBudget">(理诚)资金分配计划</a></li>
		<li><a href="${ctx}/psi/lcPurchaseOrder/overDetail">(理诚)逾期付款详细</a></li>
		
		
		<shiro:hasPermission name="amazoninfo:sale:accountBalance">
			<li class="active"><a href="${ctx}/psi/purchaseOrder/accountBalance">亚马逊到款详情</a></li>
		</shiro:hasPermission>
		<li><a href="${ctx}/psi/purchaseOrder/importDutyCount">关税增值税资金计划</a></li>
	</ul>
	<table id="dataTable" class="table table-bordered table-condensed">
		<thead>
			<tr>
				<th width="5%" >平台</th>
				<th width="5%" >金额</th>
				<th width="5%" >更新时间</th>
				<th width="20%" >备注</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td >${fns:getDictLabel(accountMap['de'][0], 'platform', '')}</td>
				<td>
					<fmt:formatNumber value="${accountMap['de'][1]}" pattern=",###" /><b>€</b>
					=<fmt:formatNumber value="${accountMap['de'][4]}" pattern=",###" /><b>$</b>
				</td>
				<td ><fmt:formatDate value="${accountMap['de'][3]}" pattern="yyyy-MM-dd HH:mm:ss"/> </td>
				<td >${accountMap['de'][2]}</td>
			</tr>
			
			<tr>
				<td >${fns:getDictLabel(accountMap['fr'][0], 'platform', '')}</td>
				<td>
					<fmt:formatNumber value="${accountMap['fr'][1]}" pattern=",###" /><b>€</b>
					=<fmt:formatNumber value="${accountMap['fr'][4]}" pattern=",###" /><b>$</b>
				</td>
				<td ><fmt:formatDate value="${accountMap['fr'][3]}" pattern="yyyy-MM-dd HH:mm:ss"/> </td>
				<td >${accountMap['fr'][2]}</td>
			</tr>
			
			<tr>
				<td >${fns:getDictLabel(accountMap['it'][0], 'platform', '')}</td>
				<td>
					<fmt:formatNumber value="${accountMap['it'][1]}" pattern=",###" /><b>€</b>
					=<fmt:formatNumber value="${accountMap['it'][4]}" pattern=",###" /><b>$</b>
				</td>
				<td ><fmt:formatDate value="${accountMap['it'][3]}" pattern="yyyy-MM-dd HH:mm:ss"/> </td>
				<td >${accountMap['it'][2]}</td>
			</tr>
			
			<tr>
				<td >${fns:getDictLabel(accountMap['es'][0], 'platform', '')}</td>
				<td>
					<fmt:formatNumber value="${accountMap['es'][1]}" pattern=",###" /><b>€</b>
					=<fmt:formatNumber value="${accountMap['es'][4]}" pattern=",###" /><b>$</b>
				</td>
				<td ><fmt:formatDate value="${accountMap['es'][3]}" pattern="yyyy-MM-dd HH:mm:ss"/> </td>
				<td >${accountMap['es'][2]}</td>
			</tr>
			
			<tr>
				<td >${fns:getDictLabel(accountMap['uk'][0], 'platform', '')}</td>
				<td>
					<fmt:formatNumber value="${accountMap['uk'][1]}" pattern=",###" /><b>￡</b>
					=<fmt:formatNumber value="${accountMap['uk'][4]}" pattern=",###" /><b>$</b>
				</td>
				<td ><fmt:formatDate value="${accountMap['uk'][3]}" pattern="yyyy-MM-dd HH:mm:ss"/> </td>
				<td >${accountMap['uk'][2]}</td>
			</tr>
			<tr>
				<td >${fns:getDictLabel(accountMap['com'][0], 'platform', '')}</td>
				<td><fmt:formatNumber value="${accountMap['com'][1]}" pattern=",###" /><b>$</b></td>
				<td ><fmt:formatDate value="${accountMap['com'][3]}" pattern="yyyy-MM-dd HH:mm:ss"/> </td>
				<td >${accountMap['com'][2]}</td>
			</tr>
			
			<tr>
				<td >${fns:getDictLabel(accountMap['ca'][0], 'platform', '')}</td>
				<td>
					<fmt:formatNumber value="${accountMap['ca'][1]}" pattern=",###" /><b>C$</b>
					=<fmt:formatNumber value="${accountMap['ca'][4]}" pattern=",###" /><b>$</b>
				</td>
				<td ><fmt:formatDate value="${accountMap['ca'][3]}" pattern="yyyy-MM-dd HH:mm:ss"/> </td>
				<td >${accountMap['ca'][2]}</td>
			</tr>
			<tr>
				<td >${fns:getDictLabel(accountMap['jp'][0], 'platform', '')}</td>
				<td>
					<fmt:formatNumber value="${accountMap['jp'][1]}" pattern=",###" /><b>¥</b>
					=<fmt:formatNumber value="${accountMap['jp'][4]}" pattern=",###" /><b>$</b>
				</td>
				<td ><fmt:formatDate value="${accountMap['jp'][3]}" pattern="yyyy-MM-dd HH:mm:ss"/> </td>
				<td >${accountMap['jp'][2]}</td>
			</tr>
			<tr>
				<td>合计</td>
				<td>
					<fmt:formatNumber value="${accountMap['de'][4]+accountMap['com'][4]+accountMap['uk'][4]+accountMap['fr'][4]+accountMap['it'][4]+accountMap['es'][4]+accountMap['jp'][4]+accountMap['ca'][4]}" pattern=",###" /><b>$</b>
				</td>
				<td></td>
				<td></td>
			</tr>
		</tbody>
	</table>
</body>
</html>
