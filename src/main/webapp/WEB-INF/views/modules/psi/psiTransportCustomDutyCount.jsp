<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>进口关税统计</title>
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
			
			 var oTable = $("#dataTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				 "sScrollX": "100%",
				 	"ordering":false,
				 	"bSort":false,
				 	"searching": false, 
				    "sDom": '"top"i',  
				    "bFilter": false,    
				    "bLengthChange": false,
				    "bInfo":false,
				    "bPaginate":false
				});
				 new FixedColumns( oTable,{
				 		"iLeftColumns":2,
						"iLeftWidth":200
				 	} );
				
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
		<li><a href="${ctx}/psi/purchaseOrder/fReport">采购支付报表</a></li>
		<li ><a href="${ctx}/psi/purchaseOrder/capitalBudget">资金分配计划</a></li>
		<li ><a href="${ctx}/psi/purchaseOrder/overDetail">逾期付款详细</a></li>
		
		
		<li ><a href="${ctx}/psi/lcPurchaseOrder/reconciliation">(理诚)对账</a></li>
		<!-- <li ><a href="${ctx}/psi/purchaseOrder/reconciliation2">资金计划</a></li> <li ><a href="${ctx}/psi/purchaseOrder/forecast">采购资金计划</a></li>-->
		<li ><a href="#">(理诚)采购支付报表</a></li>
		<li ><a href="${ctx}/psi/lcPurchaseOrder/capitalBudget">(理诚)资金分配计划</a></li>
		<li ><a href="${ctx}/psi/lcPurchaseOrder/overDetail">(理诚)逾期付款详细</a></li>
		<shiro:hasPermission name="amazoninfo:sale:accountBalance">
			<li ><a href="${ctx}/psi/purchaseOrder/accountBalance">亚马逊到款详情</a></li>
		</shiro:hasPermission>
		<li class="active"><a href="${ctx}/psi/purchaseOrder/importDutyCount">关税增值税资金计划</a></li>
	<%-- 	<li><a href="${ctx}/psi/purchaseOrder/findVatByCountry">增值税</a></li> --%>
	</ul>
	<%-- <form:form id="searchForm" modelAttribute="purchaseOrder" action="${ctx}/psi/purchaseOrder/importDutyCount" method="post" class="breadcrumb form-search" cssStyle="height: 30px;">
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
			
			<label>日期：</label>
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyyMM',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="startMonth" value="${startMonth}" class="input-small" id="startMonth"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyyMM',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endMonth" value="${endMonth}" class="input-small" id="endMonth"/>
	</form:form> --%>
	<tags:message content="${message}"/>   
	
	<table id="dataTable" class="table table-bordered table-condensed">
		<thead>
		
		   <tr>
		      <th rowspan='2'>月份</th>
		      <th rowspan='2'>合计</th>
		      <c:forEach items="${monthSet}" var="month" varStatus="i">
		        <th colspan='3'>${month}<br/>${tip[month]}</th>
		      </c:forEach>
		   </tr>
		   
		   <tr>
		     <c:forEach items="${monthSet}" var="month" varStatus="i">
		       <th>关税</th>
		       <th>增值税</th>
		       <th>合计</th>
		     </c:forEach>  
		   </tr>
		</thead>
		<tbody>
		   <tr>
		       <td>合计($)</td>
		       <td><fmt:formatNumber value="${moneyMap['total']['total']+piDutyMap['total']['total']}" maxFractionDigits="2"/> </td> 
		       <c:forEach items="${monthSet}" var="month" varStatus="i">
		            <td><fmt:formatNumber value="${moneyMap['total'][month]}" maxFractionDigits="2"/> </td>  
		            <td><fmt:formatNumber value="${piDutyMap['total'][month]}" maxFractionDigits="2"/> </td> 
		            <td><fmt:formatNumber value="${moneyMap['total'][month]+piDutyMap['total'][month]}" maxFractionDigits="2"/> </td> 
		       </c:forEach>
		    </tr>
		   
		    <tr>
		       <td>德国($)</td>
		         <td><fmt:formatNumber value="${moneyMap['eu']['total']+piDutyMap['eu']['total']}" maxFractionDigits="2"/> </td> 
		       <c:forEach items="${monthSet}" var="month" varStatus="i">
		            <td><fmt:formatNumber value="${moneyMap['eu'][month]}" maxFractionDigits="2"/> </td>  
		            <td><fmt:formatNumber value="${piDutyMap['eu'][month]}" maxFractionDigits="2"/> </td> 
		            <td><fmt:formatNumber value="${moneyMap['eu'][month]+piDutyMap['eu'][month]}" maxFractionDigits="2"/> </td> 
		       </c:forEach>
		    </tr>
		   
		    <tr>
		       <td>英国($)</td>
		        <td><fmt:formatNumber value="${moneyMap['uk']['total']+piDutyMap['uk']['total']}" maxFractionDigits="2"/> </td> 
		       <c:forEach items="${monthSet}" var="month" varStatus="i">
		            <td><fmt:formatNumber value="${moneyMap['uk'][month]}" maxFractionDigits="2"/> </td>  
		            <td><fmt:formatNumber value="${piDutyMap['uk'][month]}" maxFractionDigits="2"/> </td> 
		            <td><fmt:formatNumber value="${moneyMap['uk'][month]+piDutyMap['uk'][month]}" maxFractionDigits="2"/> </td>   
		       </c:forEach>
		    </tr>
		     <tr>
		       <td>美国($)</td>
		        <td><fmt:formatNumber value="${moneyMap['com']['total']+piDutyMap['com']['total']}" maxFractionDigits="2"/> </td> 
		       <c:forEach items="${monthSet}" var="month" varStatus="i">
		            <td><fmt:formatNumber value="${moneyMap['com'][month]}" maxFractionDigits="2"/> </td>  
		            <td><fmt:formatNumber value="${piDutyMap['com'][month]}" maxFractionDigits="2"/> </td> 
		            <td><fmt:formatNumber value="${moneyMap['com'][month]+piDutyMap['com'][month]}" maxFractionDigits="2"/> </td>   
		       </c:forEach>
		    </tr>
		    <tr>
		       <td>日本($)</td>
		        <td><fmt:formatNumber value="${moneyMap['jp']['total']+piDutyMap['jp']['total']}" maxFractionDigits="2"/> </td> 
		       <c:forEach items="${monthSet}" var="month" varStatus="i">
		            <td><fmt:formatNumber value="${moneyMap['jp'][month]}" maxFractionDigits="2"/> </td>  
		            <td><fmt:formatNumber value="${piDutyMap['jp'][month]}" maxFractionDigits="2"/> </td> 
		            <td><fmt:formatNumber value="${moneyMap['jp'][month]+piDutyMap['jp'][month]}" maxFractionDigits="2"/> </td>   
		       </c:forEach>
		    </tr>
		    <tr>
		     <td>加拿大($)</td>
		     <td><fmt:formatNumber value="${moneyMap['ca']['total']+piDutyMap['ca']['total']}" maxFractionDigits="2"/> </td> 
		       <c:forEach items="${monthSet}" var="month" varStatus="i">
		            <td><fmt:formatNumber value="${moneyMap['ca'][month]}" maxFractionDigits="2"/> </td>  
		            <td><fmt:formatNumber value="${piDutyMap['ca'][month]}" maxFractionDigits="2"/> </td> 
		            <td><fmt:formatNumber value="${moneyMap['ca'][month]+piDutyMap['ca'][month]}" maxFractionDigits="2"/> </td>   
		       </c:forEach>
		    </tr>
		</tbody>
	</table>
</body>
</html>
