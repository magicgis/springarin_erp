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
			
			<c:forEach items="${monthSet}" var="month">
			   var total=0;
			   $(".${month}").each(function(){
				   total =total+parseFloat($(this).text().replace(/,/g,""));
			  });
			   $("#${month}").append(toDecimal(total));
			</c:forEach>		
			
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
			<li ><a href="${ctx}/psi/lcPurchaseOrder/accountBalance">亚马逊到款详情</a></li>
		</shiro:hasPermission>
		<li><a href="${ctx}/psi/purchaseOrder/importDutyCount">进口关税</a></li>
		<li class="active"><a href="${ctx}/psi/purchaseOrder/findVatByCountry">增值税</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="purchaseOrder" action="${ctx}/psi/purchaseOrder/findVatByCountry" method="post" class="breadcrumb form-search" cssStyle="height: 30px;">
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
			
			<label>日期：</label>
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyyMM',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="startMonth" value="${startMonth}" class="input-small" id="startMonth"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyyMM',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endMonth" value="${endMonth}" class="input-small" id="endMonth"/>
	</form:form>
	<tags:message content="${message}"/>   
	
	<table id="dataTable" class="table table-bordered table-condensed">
		<thead>
		   <tr>
		      <th>国家</th>
		      <c:forEach items="${monthSet}" var="month"> <th>${month }($) </th></c:forEach>
		   </tr>
		</thead>
		<tbody>
			<c:forEach items="${fns:getDictList('platform')}" var="dic">
				<c:if test="${dic.value ne 'com.unitek' && dic.value ne 'mx'}">
					 <tr>
				         <td>${dic.label}</td>
				         <c:forEach items="${monthSet}" var="month">
				           <td class="${month }"><fmt:formatNumber value="${data[dic.value][month].sales*vatMap[dic.value] }" maxFractionDigits="2"/> </td>
				         </c:forEach>
			         </tr>
				</c:if>
			</c:forEach>		
			
		</tbody>
		<tfoot>
			<tr>
				<td>合计</td>
				<c:forEach items="${monthSet}" var="month">
				  <td id="${month}"></td>
				</c:forEach>
			</tr>
		</tfoot>
		
	</table>
</body>
</html>
