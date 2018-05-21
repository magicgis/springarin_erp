<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>vendor订单详情</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			if(!(top)){
				top = self; 
			}
		});	
	</script>
</head>
<body>
    <form   class="form-horizontal">
     <table class="table table-bordered table-condensed">
			<tbody>
				<tr class="info">
					<td colspan="6">Vendor Order</td>
				</tr>
				<tr>
					   <td><b>orderId</b></td>
					   <td>  ${vendorOrder.orderId} </td>
					   <td><b>country</b></td>
					   <td>${fns:getDictLabel(vendorOrder.country,'platform','')}</td>
					   <td>status</td>
					   <td>${vendorOrder.status}</td>
				</tr>
				
				<tr>
					   <td><b>shipToLocation</b></td>
					   <td>  ${vendorOrder.shipToLocation} </td>
					   <td><b>deliveryWindow</b></td>
					   <td>${vendorOrder.deliveryWindow}</td>
					   <td>orderedDate</td>
					   <td><fmt:formatDate value="${vendorOrder.orderedDate}" pattern="yyyy-MM-dd HH:mm"/></td>
				</tr>
				
				<tr>
					   <td><b>freightTerms</b></td>
					   <td>  ${vendorOrder.freightTerms} </td>
					   <td><b>paymentMethod</b></td>
					   <td>${vendorOrder.paymentMethod}</td>
					   <td>paymentTerms</td>
					   <td>${vendorOrder.paymentTerms}</td>
				</tr>
				
				<tr>
					   <td><b>purchasingEntity</b></td>
					   <td>  ${vendorOrder.purchasingEntity} </td>
					   <td><b>submittedTotalCost</b></td>
					   <td>${vendorOrder.submittedTotalCost}</td>
					   <td>acceptedTotalCost</td>
					   <td>${vendorOrder.acceptedTotalCost}</td>
				</tr>
				
				<tr>
					   <td><b>cancelledTotalCost</b></td>
					   <td>  ${vendorOrder.cancelledTotalCost} </td>
					   <td><b>receivedTotalCost</b></td>
					   <td>${vendorOrder.receivedTotalCost}</td>
					   <td></td>
					   <td></td>
				</tr>
			</tbody>
	</table>
		

		<table id="contentTable" class="table table-striped table-bordered table-condensed">
            <tr class="success">
					<td colspan="10">Order Items</td>
			</tr>
			<tr>
				   <th style="width: 120px">asin</th>
				   <th style="width: 100px">sku</th>
				   <th style="width: 150px">skuInVendor</th>
				   <th style="width: 50px">productName</th>
				   <th style="width: 50px">submittedQuantity</th>
				   <th style="width: 50px">acceptedQuantity</th>
				   <th style="width: 50px">receivedQuantity</th>
				   <th style="width: 50px">outstandingQuantity</th>
				   <th style="width: 50px">itemPrice</th>
				   <th style="width: 50px">unitPrice</th>
			</tr>

		<c:forEach items="${vendorOrder.items}" var="order">
			<tr>
				<td>${order.asin}</td>
				<td>${order.sku}</td>
				<td>${order.skuInVendor}</td>
				<td><a  href="${ctx}/psi/psiInventory/productInfoDetail?productName=${order.productName}" target='_blank'>${order.productName}</a></td>
				<td>${order.submittedQuantity}</td>
				<td>${order.acceptedQuantity}</td>
				<td>${order.receivedQuantity}</td>
				<td>${order.outstandingQuantity}</td>
				<td>${order.itemPrice}</td>
				<td>${order.unitPrice}</td>
			</tr>
		</c:forEach>

	</table>
  </form>
</body>
</html>
