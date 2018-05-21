<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>亚马逊产品销售报告订单明细管理</title>
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

		var arr2 = $("#contentTable .price")
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

		<%-- 货币美元&欧元切换,默认为欧元版	 --%>
		var html1 = " 货币类型:<select name=\"currencyType\" id=\"currencyType\" style=\"width: 100px\" onchange=\"changeCurrencyType()\">"+
			"<option value=\"EUR\" ${'EUR' eq fn:trim(currencyType)?'selected':''}>EUR</option>"+
			"<option value=\"USD\" ${'USD' eq fn:trim(currencyType)?'selected':''}>USD</option></select> &nbsp;&nbsp;&nbsp;";
	
		var html = "<a class=\"btn btn-warning btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/orderList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">订单</a>&nbsp;"+
		" <a class=\"btn btn-info btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/skuList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">Sku</a>&nbsp;"+
		" <a class=\"btn btn-warning btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/productList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">商品</a>&nbsp;"+
		<c:if test="${'3' eq byTime}">
           "<a class=\"btn btn-warning btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/productListByDate?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">商品按日</a>&nbsp;"+
        </c:if>
		" <a class=\"btn btn-warning btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/exportAll?flag=1&country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">导出</a>&nbsp; ";
		
		$("#contentTbDiv .spanexr div:first").append(html1 + html);
	});
	
	function changeCurrencyType(){
		var currencyType = $("#currencyType").val();
		var url = "${ctx}/amazoninfo/salesReprots/skuList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=" + currencyType;
		window.location.href = url;
	}

</script>
</head>
<body>
	<div id="contentTbDiv" style="width:900px;margin: auto">
		<br/>
		<div style="font-size: 25px; font-weight: bold; text-align: center;">
		<c:choose>
			<c:when test="${country eq 'eu'}">欧洲</c:when>
			<c:when test="${country eq 'en'}">英语国家</c:when>
			<c:when test="${country eq 'unEn'}">非英语国家</c:when>
			<c:otherwise>${fns:getDictLabel(country,'platform','汇总')}</c:otherwise>
		</c:choose>
		${'total' eq lineType?'':allLine[lineType] }销售报告${dateStr}${'1' eq byTime?'日':''}${'2' eq byTime?'周':''}${'3' eq byTime?'月':''}SKU明细</div>
		<br/>
		<div>
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th>No.</th>
						<th style="width: 150px">Sku</th>
						<th>Product</th>
						<th>Country</th>
						<th>Quantity</th>
						<th>Sales(${currencySymbol })</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${ops}" var="op" varStatus="i">
						<tr>
							<td>${i.count}</td>
							<td>
								<c:if test="${fn:contains(op[0], '_pack')}">
									<font style="color:red">${op[0]}</font>
								</c:if>
								<c:if test="${!fn:contains(op[0], '_pack')}">
									${op[0]}
								</c:if>
							</td>
							<td>
								<a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${op[1]}">${op[1]}</a>
								<c:if test="${not empty typeLineMap[fn:toLowerCase(nameTypeMap[op[1]])] }">(${typeLineMap[fn:toLowerCase(nameTypeMap[op[1]])] }线)</c:if>
							</td>
							<td>${fns:getDictLabel(op[2],'platform','')}</td>
							<td class="qty">${op[3]}</td>
							<td class="price">${op[4]}</td>
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td style="font-size: 18px; font-weight: bold;">Total</td>
						<td></td>
						<td></td>
						<td></td>
						<td id="tolQty"></td>
						<td id="tolPrice"></td>
					</tr>
				</tfoot>
			</table>
		</div>
	</div>
</body>
</html>
