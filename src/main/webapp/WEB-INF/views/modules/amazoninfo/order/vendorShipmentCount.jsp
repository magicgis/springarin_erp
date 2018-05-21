<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>vendor自发货订单统计</title>
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
			
			$("#btnSubmit").click(function(){
				$("#searchForm").attr("action","${ctx}/amazoninfo/vendorOrder/createDeliveryOrder");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/amazoninfo/vendorOrder/showDeliveryOrder");
			});
		});
		
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/amazoninfo/vendorOrder/list">Vendor Order List</a></li>
		<li class="active"><a href="#">${fns:getDictLabel(vendorShipment.country,'platform','')} Delivery List</a></li>
	</ul>
	<tags:message content="${message}"/>
	<form:form id="searchForm" modelAttribute="vendorShipment" action="${ctx}/amazoninfo/vendorOrder/showDeliveryOrder" method="post" class="breadcrumb form-search" cssStyle="height: 40px;">
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
				<label><strong>ShippedDate：</strong></label>
				<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="shipDate" value="<fmt:formatDate value="${vendorShipment.shipDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			    &nbsp;-&nbsp;
			    <input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="deliveryDate" value="<fmt:formatDate value="${vendorShipment.deliveryDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				<label><strong>ProductName：</strong></label>
				<input type="text" name="asn" value="${vendorShipment.asn}" class="input-small"/>
				<input type="hidden" name="country" value="${vendorShipment.country}"/>
				&nbsp;&nbsp;&nbsp;<input  class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
                &nbsp;&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="export"/>
			</div>
		</div>
   </form:form>		
	    <table id="contentTable" class="table table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 16%">Product Name</th>
				   <th style="width: 8%">Total Quantity</th>
				   <th style="width: 8%">ASN</th>
				   <th style="width: 10%">Order Id</th>
				   <th style="width: 8%">Order Status</th>
				   <th style="width: 8%">Accepted Quantity</th>
				   <th style="width: 8%">Received Quantity</th>
				   <th style="width: 20%">Shipped Date</th>
			</tr>
		</thead>
		<tbody>
		  <c:forEach items="${totalMap}" var="map">
		    <c:forEach items="${itemMap[map.key]}" var="list" varStatus="i">
				   <tr ><!-- style="${'Closed' eq list[8] && list[3]!=list[7]?'background-color:#cccccc;':'' }" -->
				     <c:if test="${i.count==1}">
				       <td rowspan="${fn:length(itemMap[map.key])}" style="vertical-align: middle;"><a  href="${ctx}/psi/psiInventory/productInfoDetail?productName=${map.key}" target='_blank'>${map.key}</a></td>
				       <td rowspan="${fn:length(itemMap[map.key])}" style="vertical-align: middle;">${totalMap[map.key]}</td>
				     </c:if>  
					   <td style="vertical-align: middle;"><a target='_blank' href="${ctx}/amazoninfo/vendorOrder/form?id=${list[5]}">${list[1]}</a></td>
					   <td style="vertical-align: middle;"><a target='_blank' href="${ctx}/amazoninfo/vendorOrder/vendorForm?id=${list[6]}">${list[2] }</a></td>
					   <td style="vertical-align: middle;">${list[8]}</td>
					   <td style="vertical-align: middle;">${list[3]}</td>
					   <td style="vertical-align: middle;">${list[7]}</td><!--${'Closed' eq list[8] && list[3]!=list[7]?'color:red;':'' }-->
				       <td style="vertical-align: middle;"><fmt:formatDate value="${list[4] }" pattern="yyyy-MM-dd HH:mm" /></td>
				   </tr>
			  </c:forEach>
		 </c:forEach>
		</tbody>
	</table>
</body>
</html>
