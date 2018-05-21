<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>大订单明细管理</title>
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

		$("#contentTable").dataTable({
				"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 20,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true
		});
		
	});

</script>
</head>
<body>
	<div id="contentTbDiv" style="width: 900px;margin: auto">
		<br/>
		<div style="font-size: 25px; font-weight: bold; text-align: center;">${fns:getDictLabel(country,'platform','eu' eq country?'欧洲':'汇总')}${time}${'1' eq type?'日':''}${'2' eq type?'周':''}${'3' eq type?'月':''}大订单明细</div>
		<br/>
		<div>  
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th>No.</th>
						<th style="width: 160px">OrderId</th>
						<th>Country</th>
						<th>Sku</th>
						<th>产品名称</th>
						<th>数量</th>
						<th>订单状态</th>
						<th>销售额(${currencySymbol })</th>
						<th style="width: 160px">下单时间</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${ops}" var="op" varStatus="i">
						<tr>
							<td>${i.count}</td>
							<td>
							  <a target="_blank" href="${ctx}/amazoninfo/order/form?amazonOrderId=${op[0]}">${op[0]}</a>
							 </td>
							<td>${op[1]}</td>
							<td>${op[2]}</td>
							<td><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${op[3]}">${op[3]}</a></td>
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
						<td></td>
						<td></td>
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
</body>
</html>
