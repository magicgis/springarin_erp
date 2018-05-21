<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<%@include file="/WEB-INF/views/mobile/include/head.jsp" %>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/datatables.jsp"%>
<style type="text/css">
.spanexr {
	float: right;
	min-height: 40px
}

.spanexl {
	float: left;
}

.footer {
	padding: 20px 0;
	margin-top: 20px;
	border-top: 1px solid #e5e5e5;
	background-color: #f5f5f5;
}

.dataTables_filter {
	float: right;
	margin-bottom: 5px;
	margin-right: 8px;
}

#contentTable_length{
	display:none;
}

#contentTable_info{
	display:none;
}

.dataTables_paginate{
	display:none ;
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
	
	function fmoney(s, n){   
	   temp = s;	
	   if(s<0){
		  temp= -s;
	   }
	   n1 = n;
	   n = n > 0 && n <= 20 ? n : 2;   
	   temp  = parseFloat((temp + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";   
	   var l = temp.split(".")[0].split("").reverse(),   
	   r = temp.split(".")[1];   
	   t = "";   
	   for(i = 0; i < l.length; i ++ )   
	   {   
	      t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");   
	   }   
	   temp =  t.split("").reverse().join("") + "." + r;   
	   if(s<0){
		   temp= "-"+temp;
	   }
	   if(n1==0){
		   temp = temp.replace(".00","")
	   }
	   return temp;
	} 
	
	$.fn.dataTableExt.afnSortData['dom-html1'] = function ( oSettings, iColumn )
	{
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
			return $('td:eq('+iColumn+')', tr).text().replace(",","");
		} );
	}
	
	$(function() {

		var arr = $("#contentTable tbody tr");
		var num1 = 0;
		var num2 = 0;
		var num3 = 0;
		var num4 = 0;
		arr.each(function() {
			num1 += parseInt($(this).find("td :eq(1)").text().replace(',',''));
			num2 += parseFloat($(this).find("td :eq(2)").text().replace(',',''));
			<c:if test="${not empty priceAndGw }">
			<shiro:hasPermission name="amazoninfo:sale:run">
				if($(this).find("td :eq(3)").text())
				num3 += parseInt($(this).find("td :eq(3)").text().replace(',',''));
				if($(this).find("td :eq(4)").text())
				num4 += parseFloat($(this).find("td :eq(4)").text().replace(',',''));
			</shiro:hasPermission>
			</c:if>
		});

		var tr = $("#contentTable tfoot tr");
		tr.find("td :eq(1)").text(fmoney(num1,0));
		tr.find("td :eq(2)").text(fmoney(num2.toFixed(2),2));
		<c:if test="${not empty priceAndGw }">
			<shiro:hasPermission name="amazoninfo:sale:run">
				tr.find("td :eq(3)").text(fmoney(num3,0));
				tr.find("td :eq(4)").text(fmoney(num4.toFixed(2),2));
			</shiro:hasPermission>
		</c:if>
		
		$("#contentTable").dataTable({
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : -1,
				"bScrollCollapse" : true,
				"aoColumns": [
						         null,
						         null,	
							     { "sSortDataType":"dom-html1", "sType":"numeric" }
							     <c:if test="${not empty priceAndGw }">
								<shiro:hasPermission name="amazoninfo:sale:run">
							     ,{ "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" }
							     </shiro:hasPermission>
								 </c:if>
							     ],
				"ordering" : true,
				"aaSorting": [[ 1, "desc" ]]
		});

	});

</script>
</head>
<body>
<div data-role="page">
  <jsp:include page="../../sys/headDiv.jsp" />
	<div data-role="content">
		<div style="font-size: 15px; font-weight: bold; text-align: center;margin-bottom:-20px">
			${fns:getDictLabel(country,'platform',defaultStr)}销售报告${dateStr}${'1' eq byTime?'日':''}${'2' eq byTime?'周':''}${'3' eq byTime?'月':''}产品明细
		</div>
		<div style="overflow:auto">
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th>产品名称</th>
						<th>销量</th>
						<th>销售额(${currencySymbol })</th>
						<c:if test="${not empty priceAndGw }">
							<shiro:hasPermission name="amazoninfo:sale:run">
								<th>已确认<br/>费用数量</th>
								<th>利润</th>
							</shiro:hasPermission>
						</c:if>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${ops}" var="op" varStatus="i">
						<tr>
							<td>
								<c:if test="${!fn:contains(op[0],'未匹配')}">
									<a href="${ctx}/psi/psiInventory/mobileProductInfoDetail?productName=${op[0]}&sTime=${dateStr}&sCountry=${country}&sType=${byTime}&orderType=${orderType}">${op[0]}</a>
								</c:if>
								<c:if test="${fn:contains(op[0],'未匹配')}">
									<span style="color: red">${op[0]}</span>
								</c:if>
							</td>
							<td class="qty">${op[1]}</td>
							<td class="price">${op[2]}</td>
							<c:if test="${not empty priceAndGw }">
								<shiro:hasPermission name="amazoninfo:sale:run">
									<c:set var="gw" value="${priceAndGw[op[0]]['gw']}" />
									<c:set var="price" value="${priceAndGw[op[0]]['price']}" />
									<td><fmt:formatNumber value="${op[7]}" maxFractionDigits="0" /></td>
									<td>
										<fmt:formatNumber value="${op[3]+op[4]+op[5]-op[6]*gw-op[7]*price}" maxFractionDigits="2" />
									</td>
								</shiro:hasPermission>
							</c:if>
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td style="font-size: 18px; font-weight: bold;">Total</td>
						<td id="tolQty"></td>
						<td id="tolPrice"></td>
						<c:if test="${not empty priceAndGw }">
							<shiro:hasPermission name="amazoninfo:sale:run">
								<td></td>
								<td></td>
							</shiro:hasPermission>
						</c:if>
					</tr>
				</tfoot>
			</table>
		</div>
	</div>
	<jsp:include page="../../sys/footDiv.jsp"></jsp:include>
</div>
</body>
</html>
