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
		
		
		var arr = $("#contentTable2 .quantity");
		var tempNum = 0;
		arr.each(function() {
			tempNum += parseInt($(this).text());
		});

		$("#contentTable2 #totalReview").text(tempNum);
		

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
		
		$("#contentTable2").dataTable({
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
		
			var html = "<a class=\"btn btn-info btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/orderList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">订单</a>&nbsp;"+
			"<a class=\"btn btn-warning btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/skuList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">Sku</a>&nbsp;"+
			"<a class=\"btn btn-warning btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/productList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">商品</a>&nbsp;";
			<c:if test="${'3' eq byTime}">
	           htmll+="<a class=\"btn btn-warning btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/productListByDate?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">商品按日</a>&nbsp;";
	       </c:if>
			<c:if test="${not empty productName}">
				html += "<a class=\"btn btn-warning btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/exportAll?flag=0&country=${country}&type=${byTime}&time=${dateStr}&productName=${productName}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">导出</a>&nbsp; ";
			</c:if>
		$("#contentTbDiv .spanexr div:first").append(html1 + html);
		
		
		  $("a[rel='popover']").popover({trigger:'hover'});
	});
	
	function changeCurrencyType(){
		var currencyType = $("#currencyType").val();
		var url = "${ctx}/amazoninfo/salesReprots/orderList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=" + currencyType;
		window.location.href = url;
	}

</script>
</head>
<body>
	<div id="contentTbDiv" style="width: 900px;margin: auto">
		<br/>
		<div style="font-size: 25px; font-weight: bold; text-align: center;">
		<c:choose>
			<c:when test="${country eq 'eu'}">欧洲</c:when>
			<c:when test="${country eq 'en'}">英语国家</c:when>
			<c:when test="${country eq 'unEn'}">非英语国家</c:when>
			<c:otherwise>${fns:getDictLabel(country,'platform','汇总')}</c:otherwise>
		</c:choose>
		 ${productName}${'total' eq lineType?'':allLine[lineType] } ${dateStr}${'1' eq byTime?'日':''}${'2' eq byTime?'周':''}${'3' eq byTime?'月':''}
		</div>
		<br/>
		<div style="font-size: 25px; font-weight: bold; text-align: center;">销售订单明细</div>
		<div style='color:#ff0033;text-align: right;vertical-align: right;'>汇率使用目标设置天汇率</div>
		<br/>
		
		<div>  
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th>No.</th>
						<th>OrderId</th>
						<th>Country</th>
						<th>Sku</th>
						<th>ProductName</th>
						<th>Quantity</th>
						<th>Order Status</th>
						<th>Sales(${currencySymbol })</th>
						<th>Order Time</th>
						
						<c:if test="${empty orderType||fns:startsWith(orderType,'1')}">
						    <th>State Or Region</th>
						    <th>Country Code</th>
						</c:if>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${ops}" var="op" varStatus="i">
						<tr>
							<td>${i.count}</td>
							<td>
							   <c:if test="${fn:startsWith(orderType, '1')|| empty orderType}">
							      <c:set value='' var='classType'/>
							      <c:if test="${'1' eq op[8]}"><c:set value='style="color:orange"  title="促销" ' var='classType'/></c:if>
							      <c:if test="${'2' eq op[8]}"><c:set value='style="color:red"  title="闪购" ' var='classType'/></c:if>
							      <c:if test="${'5' eq op[8]}"><c:set value='style="color:green"  title="免费" ' var='classType'/></c:if>
							      <c:if test="${'9' eq op[8]}"><c:set value='style="color:#DA70D6"  title="B2B" ' var='classType'/></c:if>
							      <c:if test="${fn:contains(orderIdList,op[0])}"><c:set value='style="color:#49afcd"  title="营销" ' var='classType'/></c:if>
							      <a ${classType } target="_blank" href="${ctx}/amazoninfo/order/form?amazonOrderId=${op[0]}">${op[0]}</a>
							   </c:if>
							   <c:if test="${fn:startsWith(orderType, '2')}"><a target="_blank" href="${ctx}/amazoninfo/vendorOrder/vendorForm?id=${op[8]}">${op[0]}</a></c:if>
							   <c:if test="${fn:startsWith(orderType, '3')}"><a target="_blank" href="${ctx}/ebay/order/form?id=${op[8]}">${op[0]}</a></c:if>
							   <c:if test="${fn:startsWith(orderType, '4')||fn:startsWith(orderType, '7')}"><a target="_blank" href="${ctx}/amazoninfo/unlineOrder/form?id=${op[8]}">${op[0]}</a></c:if>
							   <c:if test="${fn:startsWith(orderType, '5')}"><a target="_blank" href="${ctx}/amazoninfo/unlineOrder/form?id=${op[8]}">${op[0]}</a></c:if>
							    <c:if test="${fn:startsWith(orderType, '6')}"><a target="_blank" href="${ctx}/amazoninfo/unlineOrder/form?id=${op[8]}">${op[0]}</a></c:if>
							 </td>
							<td>${op[1]}</td>
							<td>
								<c:if test="${fn:contains(op[2], '_pack')}">
									<font style="color:red">${op[2]}</font>
								</c:if>
								<c:if test="${!fn:contains(op[2], '_pack')}">
									${op[2]}
								</c:if>
							</td>
							<td>
								<a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${op[3]}">${op[3]}</a>
								<c:if test="${not empty typeLineMap[fn:toLowerCase(nameTypeMap[op[3]])] }">(${typeLineMap[fn:toLowerCase(nameTypeMap[op[3]])] }线)</c:if>
							</td>
							<td class="qty">${op[4]}</td>
							<td>${op[5]}</td>
							<td class="price">${op[6]}</td>
							<td >${op[7]}</td>
							
							<c:if test="${empty orderType||fns:startsWith(orderType,'1')}">
						       <td >${op[9]}</td>
						       <td >${op[10]}</td>
						   </c:if>
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
						<c:if test="${empty orderType||fns:startsWith(orderType,'1')}">
						       <td></td>
						       <td></td>
						   </c:if>
					</tr>

				</tfoot>
			</table>
		</div>
		
		
		<c:if test="${not empty reviewOps }">
		   <div style="font-size: 25px; font-weight: bold; text-align: center;">
		       营销和替代订单明细</div>
		<br/>
		<div>  
			<table id="contentTable2" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th>No.</th>
						<th>OrderId</th>
						<th>Country</th>
						<th>Sku</th>
						<th>ProductName</th>
						<th>Quantity</th>
						<th>Order Status</th>
						<th>Order Time</th>
						<th>Order Type</th>
						<th>Remark</th>
						<th>Create User</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${reviewOps}" var="op" varStatus="i">
						<tr>
							<td>${i.count}</td>
							<td><a href="${ctx}/amazoninfo/amazonTestOrReplace/view?sellerOrderId=${op[0]}">${op[0]}</a></td>
							<td>${op[1]}</td>
							<td>${op[2]}</td>
							<td>
								<a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${op[3]}">${op[3]}</a>
							</td>
							<td class="quantity">${op[4]}</td>
							<td>${op[5]}</td>
							<td>${op[6]}</td>
							<td>${op[7]}</td>
							<td>
							   <a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${op[8]}">${fns:rabbr(op[8],8)}</a>
							</td>
							<td>${op[9]}</td>
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
						<td id="totalReview"></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
					</tr>

				</tfoot>
			</table>
		</div>
		
		   
		</c:if>
	</div>
</body>
</html>
