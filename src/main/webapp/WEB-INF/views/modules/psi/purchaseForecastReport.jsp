<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购财务报表</title>
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
		$(document).ready(function() {
			
			$("#supplier,#orderNo").click(function(){
				$("#searchForm").submit();
			});
			
			$("#reset").click(function(){
				$("#supplier").val("");
				$("#orderNo").val("");
				$("#searchForm").submit();
			});
			
			var orderAmount = 0 ;
			var depositAmount = 0 ;
			var ladingAmount = 0 ;
			var balanceLadingAmount =0;
			var allPayment=0;
			$(".orderAmount").each(function(){
				orderAmount =orderAmount+parseFloat($(this).text());
			});
			
			$(".depositAmount").each(function(){
				depositAmount =depositAmount+parseFloat($(this).text());
			});
			
			$(".ladingAmount").each(function(){
				ladingAmount =ladingAmount+parseFloat($(this).text());
			});
			
			$(".balanceLadingAmount").each(function(){
				balanceLadingAmount =balanceLadingAmount+parseFloat($(this).text());
			});
			
			$(".allPayment").each(function(){
				allPayment =allPayment+parseFloat($(this).text());
			});
			
			
			$("#orderAmount").append("<b>"+toDecimal(orderAmount)+"<span style='color:#EAC100;padding-left:5px;font-size:16px'>$<span></b>");
			$("#depositAmount").append("<b>"+toDecimal(depositAmount)+"<span style='color:#EAC100;padding-left:5px;font-size:16px'>$<span></b></b>");
			$("#ladingAmount").append("<b>"+toDecimal(ladingAmount)+"<span style='color:#EAC100;padding-left:5px;font-size:16px'>$<span></b></b>");
			$("#balanceLadingAmount").append("<b>"+toDecimal(balanceLadingAmount)+"<span style='color:#EAC100;padding-left:5px;font-size:16px'>$<span></b></b>");
			$("#allPayment").append("<b>"+toDecimal(allPayment)+"<span style='color:#EAC100;padding-left:5px;font-size:16px'>$<span></b></b>");
			
		});
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            return x.toFixed(2);  
	     }  
		
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
		<li ><a href="${ctx}/psi/purchaseOrder/reconciliation">对账</a></li>
		<!-- <li ><a href="${ctx}/psi/purchaseOrder/reconciliation2">资金计划</a></li> -->
		<li ><a href="${ctx}/psi/purchaseOrder/fReport">采购支付报表</a></li>
		<li class="active"><a href="#">采购资金计划</a></li>
	</ul>
	<br/>
	<form:form id="searchForm" modelAttribute="purchaseOrder" action="${ctx}/psi/purchaseOrder/forecast" method="post" class="breadcrumb form-search" cssStyle="height: 30px;">
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
			<label>供应商：</label>
			<select style="width:150px;" id="supplier" name="supplier.id" ${supplierDisabled eq '1'?'disabled':'' }>
				<option value="">全部</option>
				<c:forEach items="${suppliers}" var="supplier" varStatus="i">
					 <option value='${supplier.id}' ${supplier.id eq purchaseOrder.supplier.id?'selected':'' }>${supplier.nikename}</option>;
				</c:forEach>
			</select>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<label>产品：</label>
			<select style="width:250px;" name="orderNo" id="orderNo" ${productDisabled eq '1'?'disabled':'' }>
				<option value="">全部</option>
				<c:forEach items="${proNameColors}" var="proNameColor">
					<option value="${proNameColor}" ${proNameColor eq purchaseOrder.orderNo?'selected':''}>${proNameColor}</option>
				</c:forEach>
			</select>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="reset" class="btn btn-warning" type="reset" value="重置"/>
	</form:form>
	<tags:message content="${message}"/>   
	
	
	<table id="dataTable" class="table table-bordered table-condensed">
		<thead><tr>
		<th width="5%">月份</th><th width="5%">预测订单总金额</th><th width="5%">预测支付定金</th><th width="5%">预测支付尾款</th><th width="5%">结余尾款</th><th width="5%">预测支付总金额</th></tr></thead>
		<tbody>
		<c:forEach items="${forecastDtos}" var="dto" varStatus="i">
		<tr>
			<td>${dto.month}</td>
			<td class="orderAmount"><fmt:formatNumber value="${dto.orderAmount}" pattern="0.00" maxFractionDigits="2" /> </td>
			<td class="depositAmount"><fmt:formatNumber value="${dto.depositAmount}" pattern="0.00" maxFractionDigits="2" /></td>
			<td class="ladingAmount"><fmt:formatNumber value="${dto.ladingAmount}" pattern="0.00" maxFractionDigits="2" /></td>
			<td class="balanceLadingAmount"><fmt:formatNumber value="${dto.balanceLadingAmount}" pattern="0.00" maxFractionDigits="2" /></td>
			<td class="allPayment"><fmt:formatNumber value="${dto.allAmount}" pattern="0.00" maxFractionDigits="2" /></td>
		</tr>
		</c:forEach>
		</tbody>
		
		
		<tfoot>
			<tr>
				<td>合计</td>
				<td id="orderAmount"></td>
				<td id="depositAmount"></td>
				<td id="ladingAmount"></td>
				<td id="balanceLadingAmount"></td>
				<td id="allPayment"></td>
			</tr>
		</tfoot>
	</table>
</body>
</html>
