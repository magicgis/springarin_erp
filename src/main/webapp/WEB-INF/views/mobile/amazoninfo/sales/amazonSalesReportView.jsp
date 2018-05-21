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

#contentTable_length{
	display:none;
}

#contentTable_info{
	display:none;
}

.dataTables_paginate{
	display:none ;
}
/**
  搜索框隐藏
*/
.dataTables_filter {
   display: none;
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
	$(function() {

		var arr = $("#contentTable .qty");
		var num = 0;
		arr.each(function() {
			num += parseInt($(this).text());
		});

		$("#contentTable #tolQty").text(num);

		var arr2 = $("#contentTable .price");
		var dnum = 0.0;
		arr2.each(function() {
			if ($(this).text()) {
				dnum += parseFloat($(this).text());
			}
		});

		$("#contentTable #tolPrice").text(dnum.toFixed(2));

		var dataTable = $("#contentTable").dataTable({
			"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
			"sPaginationType" : "bootstrap",
			"iDisplayLength" : -1,
			"bScrollCollapse" : true,
			"ordering" : true,
			"bSort": false, //排序功能
			"bInfo": false //页脚信息
		});
		
		/**
		$("#searchbox").keyup(function() {
			dataTable.fnFilter(this.value);
		});*/
	});
	

</script>
</head>
<body>
<div data-role="page">
	<jsp:include page="../../sys/headDiv.jsp"></jsp:include>
	<%--
  	<div data-role="header" data-theme="b" data-position="fixed">
    	<a href="javascript:window.history.go(-1)" data-role="button">Back</a>
		<h4>${productName}</h4>
    	<a href="${ctx}/logout" data-role="button" class="ui-btn-right">Logout</a>
  	</div> --%>
	<div data-role="content">
		<div style="font-size: 15px; font-weight: bold; text-align: center;margin-bottom:-50px">
			${fns:getDictLabel(country,'platform', defaultStr)} ${productName} 
			<br/>${dateStr}${'1' eq byTime?'日':''}${'2' eq byTime?'周':''}${'3' eq byTime?'月':''}销售订单明细
		</div>
		<br/>
		<%--
		<div>
			<input id="searchbox" type="text" placeholder="Search">
		</div> --%>
		<div style="overflow:auto">
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th style="width: 160px">AmazonOrderId</th>
						<%--<th>Country</th> --%>
						<th><div style="width: 100px">Sku</div></th>
						<%--<th>产品名称</th> --%>
						<th>数量</th>
						<th>订单状态</th>
						<th>销售额(${currencySymbol })</th>
						<th><div style="width: 140px">下单时间</div></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${ops}" var="op" varStatus="i">
						<tr>
							<td>${op[0]}</td>
							<%--<td>${op[1]}</td> --%>
							<td>${op[2]}</td>
							<%--<td>${op[3]}</td>--%>
							<td class="qty">${op[4]}</td>
							<td>${op[5]}</td>
							<td class="price">${op[6]}</td>
							<td >${op[7]}</td>
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td style="font-size: 18px; font-weight: bold;">Total</td>
						<td></td>
						<td id="tolQty"></td>
						<td></td>
						<td id="tolPrice"></td>
						<td></td>
					</tr>

				</tfoot>
			</table>
		</div>
	</div>
	<jsp:include page="../../sys/footDiv.jsp"></jsp:include>
	</div>
</body>
</html>
