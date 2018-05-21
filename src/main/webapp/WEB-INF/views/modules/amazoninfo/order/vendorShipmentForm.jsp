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
					<td colspan="6">Vendor Shipment</td>
				</tr>
				<tr>
					   <td><b>ASN</b></td>
					   <td> ${vendorShipment.asn} </td>
					   <td><b>country</b></td>
					   <td>${fns:getDictLabel(vendorShipment.country,'platform','')}</td>
					   <td>shipmentStatus</td>
					   <td>${vendorShipment.shipmentStatus}</td>
				</tr>
				<tr>
					   <td><b>type</b></td>
					   <td>${vendorShipment.type}</td>
					   <td><b>status</b></td>
					   <td>${vendorShipment.status}</td>
					   <td>shipDate</td>
					   <td><fmt:formatDate value="${vendorShipment.shipDate}" pattern="yyyy-MM-dd HH:mm"/></td>
				</tr>
				
				<tr>
					   <td><b>deliveryDate</b></td>
					   <td><fmt:formatDate value="${vendorShipment.deliveryDate}" pattern="yyyy-MM-dd HH:mm"/></td>
					   <td><b>shippedDate</b></td>
					   <td><fmt:formatDate value="${vendorShipment.shippedDate}" pattern="yyyy-MM-dd HH:mm"/></td>
					   <td>deliveryUser</td>
					   <td>	<c:if test="${not empty vendorShipment.deliveryUser }">${vendorShipment.deliveryUser.name}</c:if></td>
				</tr>
				
				<tr>
					   <td><b>checkUser</b></td>
					   <td>	<c:if test="${not empty vendorShipment.checkUser }">${vendorShipment.checkUser.name}</c:if></td>
					   <td><b>freightTerms</b></td>
					   <td>${vendorShipment.freightTerms}</td>
					   <td>carrierSCAC</td>
					   <td>${vendorShipment.carrierSCAC}</td>
				</tr>
				
				<tr>
					   <td><b>carrierTracking</b></td>
					   <td>${vendorShipment.carrierTracking}</td>
					   <td><b>shipAddress</b></td>
					   <td>${vendorShipment.shipAddress}</td>
					   <td>packages</td>
					   <td>${vendorShipment.packages}</td>
				</tr>
				
				<tr>
					   <td><b>stackedPallets</b></td>
					   <td>${vendorShipment.stackedPallets}</td>
					   <td><b>unstackedPallets</b></td>
					   <td>${vendorShipment.unstackedPallets}</td>
					   <td></td>
					   <td></td>
				</tr>
				
				
			</tbody>
	</table>			


		<table id="contentTable" class="table table-striped table-bordered table-condensed">
              <tr class="success">
					<td colspan="11">Vendor Order</td>
				</tr>
				
			<tr>
				   <th style="width: 120px">orderId</th>
				   <th style="width: 100px">orderedDate</th>
				   <th style="width: 150px">deliveryWindow</th>
				   <th style="width: 50px">freightTerms</th>
				   <th style="width: 50px">paymentMethod</th>
				   <th style="width: 50px">paymentTerms</th>
				   <th style="width: 50px">purchasingEntity</th>
				   <th style="width: 50px">submittedTotalCost</th>
				   <th style="width: 50px">acceptedTotalCost</th>
				   <th style="width: 50px">receivedTotalCost</th>
				   <th style="width: 50px">cancelledTotalCost</th>
			</tr>

		<c:forEach items="${vendorShipment.orders}" var="order">
			<tr>
				<td>${order.orderId}</td>
				<td><fmt:formatDate value="${order.orderedDate}" pattern="yyyy-MM-dd"/></td>
				<td>${order.deliveryWindow}</td>
				<td>${order.freightTerms}</td>
				<td>${order.paymentMethod}</td>
				<td>${order.paymentTerms}</td>
				<td>${order.purchasingEntity}</td>
				<td>${order.submittedTotalCost}</td>
				<td>${order.acceptedTotalCost}</td>
				<td>${order.receivedTotalCost}</td>
				<td>${order.cancelledTotalCost}</td>
			</tr>
			<tr>
			    <td colspan="11">
					<c:forEach items="${order.items}" var="item">
						<b style="font-size: 14px"><a  href="${ctx}/psi/psiInventory/productInfoDetail?productName=${item.productName}" target='_blank'>${item.productName}</a>;</b>Asin:${item.asin};sku:${item.sku};skuInVendor:${item.skuInVendor};submittedQuantity:${item.submittedQuantity};
						acceptedQuantity:${item.acceptedQuantity};receivedQuantity:${item.receivedQuantity};outstandingQuantity:${item.outstandingQuantity};itemPrice:${item.itemPrice};unitPrice:${item.unitPrice};<br/>
					</c:forEach>
				</td>
			</tr>
		</c:forEach>
	</table>
  </form>
</body>
</html>
