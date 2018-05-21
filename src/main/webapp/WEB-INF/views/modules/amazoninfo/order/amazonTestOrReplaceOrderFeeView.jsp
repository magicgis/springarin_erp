<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>测评订单详情</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
			var _hmt = _hmt || [];
			(function() {
			  var hm = document.createElement("script");
			  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
			  var s = document.getElementsByTagName("script")[0]; 
			  s.parentNode.insertBefore(hm, s);
			})();
			
			if(!(top)){
				top = self; 
			}
			
             $("#inputForm").validate({
				
				submitHandler: function(form){
					
							top.$.jBox.confirm('Are you sure save?','<spring:message code="sys_label_tips_msg"/>',function(v,h,f){
							
							if(v=='ok'){
									loading('<spring:message code="sys_label_tips_submit"/>');
									$("#contentTable tbody tr").each(function(i,j){
										$(j).find("select").attr("name","items"+"["+i+"]."+$(j).find("select").attr("name"));
										$(j).find("input[type!='']").each(function(){
											if($(this).attr("name")){
												$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
											}
										});
									});
									form.submit();
									$("#btnSubmit").attr("disabled","disabled");
								}
							},{buttonsFocus:1,persistent: true});
							top.$('.jbox-body .jbox-icon').css('top','55px');
						
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("<spring:message code="sys_label_tips_input_error"/>");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
		});
	</script>
</head>
<body>
	<br/>
	<tags:message content="${message}"/>
	<form:form id="inputForm" modelAttribute="amazonOutboundOrder"  class="form-horizontal" action="${ctx}/amazoninfo/amazonTestOrReplace/save">
	 <form:hidden path="id" id="id"/>
	  <input  type='hidden' name='ratingEventId' value='${ratingEventId}'/>
	<input type='hidden' name='orderType'  value='${amazonOutboundOrder.orderType}'/>
	<input type='hidden' name='eventType'  value='${amazonOutboundOrder.eventType}'/>
	<input type='hidden' name='amazonOrderId'  value='${amazonOutboundOrder.amazonOrderId}'/>
	<input type='hidden' name='sellerOrderId'  value='${amazonOutboundOrder.sellerOrderId}'/>
	<input type='hidden' name='oldOrderId'  value='${amazonOutboundOrder.oldOrderId}'/>
	
	<input type='hidden' name='orderStatus'  value='${amazonOutboundOrder.orderStatus}'/>
	<input type='hidden' name='shippingSpeedCategory'  value='${amazonOutboundOrder.shippingSpeedCategory}'/>
	<input type='hidden' name='fulfillmentAction'  value='${amazonOutboundOrder.fulfillmentAction}'/>
	<input type='hidden' name='displayableOrderComment'  value='${amazonOutboundOrder.displayableOrderComment}'/>
	<input type='hidden' name='country'  value='${amazonOutboundOrder.country}'/>
	<input type='hidden' name='accountName'  value='${amazonOutboundOrder.accountName}'/>
	<input type='hidden' name='buyerEmail'  value='${amazonOutboundOrder.buyerEmail}'/>
	<input type='hidden' name='buyerName'  value='${amazonOutboundOrder.buyerName}'/>
	
	<input type='hidden' name='earliestShipDate'  value='<fmt:formatDate value="${amazonOutboundOrder.earliestShipDate}" type="both"/>'/>
	<input type='hidden' name='latestShipDate'  value='<fmt:formatDate value="${amazonOutboundOrder.latestShipDate}" type="both"/>'/>
	<input type='hidden' name='earliestDeliveryDate'  value='<fmt:formatDate value="${amazonOutboundOrder.earliestDeliveryDate}" type="both"/>'/>
	<input type='hidden' name='latestDeliveryDate' value='<fmt:formatDate value="${amazonOutboundOrder.latestDeliveryDate}" type="both"/>'/>	
	
	<input type='hidden' name='weight'  value='${amazonOutboundOrder.weight}'/>
	<input type='hidden' name='fbaPerUnitFulfillmentFee'  value='${amazonOutboundOrder.fbaPerUnitFulfillmentFee}'/>
	<input type='hidden' name='fbaPerOrderFulfillmentFee'  value='${amazonOutboundOrder.fbaPerOrderFulfillmentFee}'/>
	<input type='hidden' name='fbaTransportationFee'  value='${amazonOutboundOrder.fbaTransportationFee}'/>
	<input type='hidden' name='remark'  value='${amazonOutboundOrder.remark}'/>	
	<input type='hidden' name='customId'  value='${amazonOutboundOrder.customId}'/>	
	
	
	<input type='hidden'  name="shippingAddress.name"  value="${amazonOutboundOrder.shippingAddress.name}"/>
	<input type='hidden'  name="shippingAddress.addressLine1"  value="${amazonOutboundOrder.shippingAddress.addressLine1}"/>
	<input type='hidden'  name="shippingAddress.addressLine2"  value="${amazonOutboundOrder.shippingAddress.addressLine2}"/>
	<input type='hidden'  name="shippingAddress.addressLine3"  value="${amazonOutboundOrder.shippingAddress.addressLine3}"/>
	<input type='hidden'  name="shippingAddress.city"  value="${amazonOutboundOrder.shippingAddress.city}"/>
	<input type='hidden'  name="shippingAddress.country"  value="${amazonOutboundOrder.shippingAddress.country}"/>
	<input type='hidden'  name="shippingAddress.district"  value="${amazonOutboundOrder.shippingAddress.district}"/>
	<input type='hidden'  name="shippingAddress.stateOrRegion"  value="${amazonOutboundOrder.shippingAddress.stateOrRegion}"/>
	<input type='hidden'  name="shippingAddress.postalCode"  value="${amazonOutboundOrder.shippingAddress.postalCode}"/>
	<input type='hidden'  name="shippingAddress.countryCode"  value="${amazonOutboundOrder.shippingAddress.countryCode}"/>
	<input type='hidden'  name="shippingAddress.phone"  value="${amazonOutboundOrder.shippingAddress.phone}"/>
	
	
	
	<div style="float: left;width:30%">
		<blockquote>
			<p style="font-size: 14px"><b><spring:message code="amazon_order_form_tips1"/></b></p>
		</blockquote>
		
		<div class="control-group">
			<label class="control-label"><b>AccountName</b></label>
			<div class="controls">
				${amazonOutboundOrder.accountName}
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label"><b>SellerOrderId</b></label>
			<div class="controls">
				${amazonOutboundOrder.sellerOrderId}
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label"><b>Order Type</b></label>
			<div class="controls">
				${amazonOutboundOrder.orderType}
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label"><b>FulfillmentAction</b></label>
			<div class="controls">
				${amazonOutboundOrder.fulfillmentAction}
			</div>
		</div>
	
		<div class="control-group">
			<label class="control-label"><b>ShippingSpeedCategory</b></label>
			<div class="controls">
				${amazonOutboundOrder.shippingSpeedCategory}
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label"><b>orderStatus</b></label>
			<div class="controls">
				${amazonOutboundOrder.orderStatus}
			</div>
		</div>
		<c:if test="${'Hold' eq amazonOutboundOrder.fulfillmentAction}">
		   <div class="control-group">
				<label class="control-label">Reviewer:</label>
				<div class="controls">
					<select name="checkUser.id" style="width:150px" id="checkUser">
						<c:forEach items="${checkUserSet}" var="user">
							<option value="${user.id}">${user.name}</option>
						</c:forEach>
					</select>
				</div>
			</div>
		</c:if>
	</div>
	
	<div style="float: left;width:40%">
	   <blockquote>
			<p style="font-size: 14px"><b><spring:message code="amazon_order_form_tips2"/></b></p>
		</blockquote>
		<div class="control-group">
			<label class="control-label"><b><spring:message code="amazon_order_form9"/></b></label>
			<div class="controls">
				${amazonOutboundOrder.buyerName}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><b><spring:message code="amazon_order_form10"/></b></label>
			<div class="controls">
				${amazonOutboundOrder.buyerEmail}
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label"><b>Shipping To</b></label>
			<div class="controls">
				${amazonOutboundOrder.shippingAddress.name}&nbsp;&nbsp;<c:if test="${not empty amazonOutboundOrder.shippingAddress.phone}">${amazonOutboundOrder.shippingAddress.phone} </c:if><br/>
				${amazonOutboundOrder.shippingAddress.addressLine1}<br/>
				<c:if test="${not empty amazonOutboundOrder.shippingAddress.addressLine2}">${amazonOutboundOrder.shippingAddress.addressLine2} <br/></c:if>
				<c:if test="${not empty amazonOutboundOrder.shippingAddress.addressLine3}">${amazonOutboundOrder.shippingAddress.addressLine3}<br/> </c:if>
				${amazonOutboundOrder.shippingAddress.postalCode}&nbsp;&nbsp;${amazonOutboundOrder.shippingAddress.city}<br/>
				${amazonOutboundOrder.shippingAddress.stateOrRegion}&&${amazonOutboundOrder.shippingAddress.countryCode}
			</div>
		</div>
		
	</div>
	<div style="float: left;width:30%">	
	<blockquote>
			<p style="font-size: 14px"><b>Estimated Fees</b></p>
	</blockquote>	
	    <div class="control-group">
			<label class="control-label"><b>EstimatedShipmentWeight</b></label>
			<div class="controls">
				${amazonOutboundOrder.weight}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><b>FBAPerUnitFulfillmentFee</b></label>
			<div class="controls">
				${amazonOutboundOrder.fbaPerUnitFulfillmentFee}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><b>FBATransportationFee</b></label>
			<div class="controls">
				${amazonOutboundOrder.fbaTransportationFee}
			</div>
		</div>
		<c:if test="${not empty  amazonOutboundOrder.fbaPerOrderFulfillmentFee}">
		<div class="control-group">
			<label class="control-label"><b>FBAPerOrderFulfillmentFee</b></label>
			<div class="controls">
				${amazonOutboundOrder.fbaPerOrderFulfillmentFee}
			</div>
		</div>
		</c:if>
		
		
		<div class="control-group">
			<label class="control-label"><b>EarliestShipDate</b></label>
			<div class="controls">
				${amazonOutboundOrder.earliestShipDate}
			</div>
		</div>	
			<div class="control-group">
			<label class="control-label"><b>EarliestDeliveryDate</b></label>
			<div class="controls">
				${amazonOutboundOrder.earliestDeliveryDate}
			</div>
		</div>	
		
	</div>	
	
	<div style="float: left;width:100%">
		<blockquote>
			<p style="font-size: 14px"><b><spring:message code="amazon_order_form_tips3"/></b></p>
		</blockquote>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 120px"><spring:message code="amazon_order_form20"/></th>
				   <th style="width: 150px"><spring:message code="amazon_order_form22"/></th>
				    <th style="width: 150px">Asin</th>
				   <th style="width: 50px"><spring:message code="amazon_order_form23"/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${amazonOutboundOrder.items}" var="item">
			<tr>
				<td>${item.name}
				  <input type="hidden" name="id" value="${item.id}" />
				  <input type='hidden' name='productName' class='productName' value='${item.productName}'/>
				  <input type='hidden' name='color' class='color' value='${item.color}' />
				  <input type='hidden' name='sellersku' class='sellersku' value='${item.sellersku}'/>
				  <input type='hidden' name='asin' class='asin' value='${item.asin}'/>
				  <input type='hidden' name='quantityOrdered' class='quantityOrdered' value='${item.quantityOrdered}'/>
				 </td>
				<td>${item.sellersku}</td>
				<td>${item.asin}</td>
				<td>${item.quantityOrdered}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="form-actions">
		    <input id="btnSubmit" class="btn btn-primary" type="submit" value="Save"/>&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="<spring:message code="sys_but_back"/>" onclick="history.go(-1)"/>
		</div>
	</div>
		
	</form:form>
</body>
</html>
