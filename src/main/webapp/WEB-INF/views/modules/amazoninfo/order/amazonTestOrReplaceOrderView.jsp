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
			
			
		});
		
		function openNewWindow(type,href,billNo){
			var arr=billNo.split(",");
			for(var i=0;i<arr.length;i++){
				if(arr[i].trim()!=""){
					if(type=='1'){
						window.open(href.replace("$$",arr[i]));  
					}else if(type=='2'){
					    var noArr=arr[i].split("-");
						window.open(href.replace("##",noArr[0])+noArr[1]);  
					}
				}
			}
		}
	</script>
</head>
<body>
	<br/>
	<tags:message content="${message}"/>
	<form:form id="inputForm" modelAttribute="amazonOutboundOrder"  class="form-horizontal">
		<form:hidden path="id"/>
		<table class="table table-bordered table-condensed">
				<tbody>
					<tr class="info">
						<td colspan="6"><spring:message code="amazon_order_form_tips1"/></td>
					</tr>
					
					<tr>
					   <td><b><spring:message code="amazon_order_form1"/></b></td>
					   <td><b style="font-size: 14px">
							  <a href='${amazonOutboundOrder.urlLink }' target='_blank'>${amazonOutboundOrder.sellerOrderId}</a>
						   </b>
				       </td>
					   <td><b>Event</b></td>
					   <td><c:if test="${not empty amazonOutboundOrder.eventId }">
					          <c:forEach items="${fn:split(amazonOutboundOrder.eventId,',')}" var="eventId">
								<a href="${ctx}/custom/event/form?id=${eventId}" target="blank">SPR-${eventId }</a>
								&nbsp;&nbsp;&nbsp;  
							</c:forEach>
					       </c:if>
					   </td>
					   <td><b><spring:message code="amazon_order_form2"/></b></td>
					   <td><fmt:formatDate value="${amazonOutboundOrder.createDate}" type="both"/></td>
				    </tr>
				    
				    <tr>
					   <td><b><spring:message code="amazon_order_form3"/></b></td>
					   <td><fmt:formatDate value="${amazonOutboundOrder.lastUpdateDate}" type="both"/></td>
					   <td><b>EarliestShipDate</b></td>
					   <td><c:if test="${not empty amazonOutboundOrder.earliestShipDate}"><fmt:formatDate value="${amazonOutboundOrder.earliestShipDate}" type="both"/></c:if></td>
					   <td><b>LatestShipDate</b></td>
					   <td><c:if test="${not empty amazonOutboundOrder.latestShipDate}"><fmt:formatDate value="${amazonOutboundOrder.latestShipDate}" type="both"/></c:if></td>
				    </tr>
				    
				    <tr>
					   <td><b>EarliestDeliveryDate</b></td>
					   <td>	<c:if test="${not empty amazonOutboundOrder.earliestDeliveryDate}"><fmt:formatDate value="${amazonOutboundOrder.earliestDeliveryDate}" type="both"/></c:if></td>
					   <td><b>LatestDeliveryDate</b></td>
					   <td>	<c:if test="${not empty amazonOutboundOrder.latestDeliveryDate}"><fmt:formatDate value="${amazonOutboundOrder.latestDeliveryDate}" type="both"/></c:if></td>
					   <td><b>Order Type</b></td>
					   <td>${amazonOutboundOrder.orderType}</td>
				    </tr>
				    
				    <tr>
					   <td><b>FulfillmentAction</b></td>
					   <td>${amazonOutboundOrder.fulfillmentAction}</td>
					   <td><b>ShippingSpeedCategory</b></td>
					   <td>${amazonOutboundOrder.fulfillmentAction}</td>
					   <td><b>orderStatus</b></td>
					   <td>${amazonOutboundOrder.orderStatus}</td>
				    </tr>
				    
				     <tr>
					   <td><b>Country</b></td>
					   <td>${fns:getDictLabel(amazonOutboundOrder.country,'platform','')}</td>
					   <td><b>remark</b></td>
					   <td>${amazonOutboundOrder.remark }</td>
					   <td><spring:message code="amazon_order_form_tips9" /></td>
					   <td>${amazonOutboundOrder.createUser.name }</td>
				    </tr>
				    
				    <tr class="success">
						<td colspan="6">Estimated Fees</td>
					</tr>
					<tr>
					   <td><b>EstimatedShipmentWeight</b></td>
					   <td>${amazonOutboundOrder.weight}</td>
					   <td><b>FBAPerUnitFulfillmentFee</b></td>
					   <td>${amazonOutboundOrder.fbaPerUnitFulfillmentFee}</td>
					   <td><b>FBATransportationFee</b></td>
					   <td>${amazonOutboundOrder.fbaTransportationFee}</td>
				    </tr>
				    
				    <tr>
					   <td><b>FBAPerOrderFulfillmentFee</b></td>
					   <td><c:if test="${not empty  amazonOutboundOrder.fbaPerOrderFulfillmentFee}">${amazonOutboundOrder.fbaPerOrderFulfillmentFee}</c:if></td>
					   <td></td>
					   <td></td>
					   <td></td>
					   <td></td>
				    </tr>
				    
				     <tr class="warning">
						<td colspan="6"><spring:message code="amazon_order_form_tips2"/></td>
					</tr>
					 <tr>
					   <td><b><spring:message code="amazon_order_form9"/></b></td>
					   <td>${amazonOutboundOrder.buyerName}</td>
					   <td><b><spring:message code="amazon_order_form10"/></b></td>
					   <td>${amazonOutboundOrder.buyerEmail}</td>
					   <td></td>
					   <td></td>
				    </tr>
					
				</tbody>
		</table>			


		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			    	<tr class="error">
			    	   <td colspan='5'><spring:message code="amazon_order_form_tips3"/></td>
			    	</tr>
				<tr>
						   <th style="width: 120px"><spring:message code="amazon_order_form20"/></th>
						   <th style="width: 150px"><spring:message code="amazon_order_form22"/></th>
						   <th style="width: 150px">Asin</th>
						   <th style="width: 50px"><spring:message code="amazon_order_form23"/></th>
						   <c:if test="${not empty returnGoods }">
						       <th style="width: 50px;color:#ff0033;"><spring:message code="amazon_return_order_quantity"/></th>
						   </c:if>
				</tr>
				<c:forEach items="${amazonOutboundOrder.items}" var="item">
					<tr>
						<td>${item.name}</td>
						<td>${item.sellersku}</td>
						<td>${item.asin}</td>
						<td>${item.quantityOrdered}</td>
						<c:if test="${not empty returnGoods }">
						  <th style="color:#ff0033;"><a title='${returnGoods[item.sellersku].reason}'>${returnGoods[item.sellersku].quantity}</a></th>
						</c:if>
					</tr>
					
				</c:forEach>
		</table>
		

		<table id="contentTable" class="table table-striped table-bordered table-condensed">
          	<tr class="success">
			    	   <td colspan='5'>Shipping Information</td>
			    	</tr>
			<tr>
				   <th style="width: 120px">ProductName</th>
				   <th style="width: 150px">Sku</th>
				   <th style="width: 80px">Quantity</th>
				   <th style="width: 150px">Delivery Estima</th>
			</tr>

		
		<c:forEach items="${amazonOutboundOrder.trackingList}" var="item1">
		    <tr  class="info">
		       <td colspan='4'><b>Carrier&Tracking Number:</b>${item1.key }
		          <c:set var="carrier" value="${fn:split(item1.key,',')[0] }"/>
		          <c:set var="trackNumber" value="${fn:split(item1.key,',')[1] }"/>
		          <c:if test="${not empty site[carrier]}">
		             <c:choose>
					  <c:when test="${fn:contains(site[carrier],'$$')}">
						 <a  class="billNo1" target="_blank" onclick="openNewWindow('1','${site[carrier]}','${trackNumber }')">Track</a>
					  </c:when>
					 <c:when test="${fn:contains(site[carrier],'##')}">
							 <a  class="billNo2" target="_blank" onclick="openNewWindow('2','${site[carrier]}','${trackNumber }')">Track</a>	
					 </c:when>
					 <c:otherwise>
							<a class="billNo3"  target="_blank" href="${site[carrier] }">Track</a> 
					</c:otherwise>
		          </c:choose>
		         </c:if>
		       
		       </td>
		    </tr>
		    <c:forEach items="${amazonOutboundOrder.trackingList[item1.key]}" var="item">
		      <tr>
				<td>${item.name}</td>
				<td>${item.sellersku}</td>
				<td>${item.quantity}</td>
				<td>${item.estimatedArrivalDate}</td>
			  </tr>
		    </c:forEach>
		</c:forEach>

	  </table>

		<div style="margin-left: 15px">
		
		<div class="tab-content" id="cTab">
			 <div class="tab-pane active" id="panel-0">
			 	<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form11"/></label>
						<div class="controls">
							${amazonOutboundOrder.shippingAddress.name}
						</div>
				</div>
				<c:if test="${not empty amazonOutboundOrder.shippingAddress.phone}"> 
 					<div class="control-group">
 						<label class="control-label">Phone</label> 
						<div class="controls">
							${amazonOutboundOrder.shippingAddress.phone} 
						</div> 
 					</div> 
				</c:if> 
				<c:if test="${not empty amazonOutboundOrder.shippingAddress.postalCode}">
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form13"/></label>
						<div class="controls">
							${amazonOutboundOrder.shippingAddress.postalCode}
						</div>
					</div>
				</c:if>
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form14"/></label>
					<div class="controls">
						${amazonOutboundOrder.shippingAddress.countryCode}
					</div>
				</div>
				<c:if test="${not empty amazonOutboundOrder.shippingAddress.stateOrRegion}">
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form15"/></label>
						<div class="controls">
							${amazonOutboundOrder.shippingAddress.stateOrRegion}
						</div>
					</div>
				</c:if>
				<c:if test="${not empty amazonOutboundOrder.shippingAddress.country}">
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form30"/></label>
					<div class="controls">
						${amazonOutboundOrder.shippingAddress.country}
					</div>
				</div>
				</c:if>
				<c:if test="${not empty amazonOutboundOrder.shippingAddress.city}">
					<div class="control-group">
						<label class="control-label"><spring:message code="amazon_order_form16"/></label>
						<div class="controls">
							${amazonOutboundOrder.shippingAddress.city}
						</div>
					</div>
				</c:if>
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form_addr9"/></label>
					<div class="controls">
						${amazonOutboundOrder.shippingAddress.addressLine1}
					</div>
				</div>
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form_addr10"/></label>
					<div class="controls">
						${amazonOutboundOrder.shippingAddress.addressLine2}
					</div>
				</div>	
				<div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form_addr11"/></label>
					<div class="controls">
						${amazonOutboundOrder.shippingAddress.addressLine3}
					</div>
				</div>		
			 </div>
			
			
		</div>
		</div>
		
	
	
	
	
	
		<div class="form-actions">
		  
			<input id="btnCancel" class="btn" type="button" value="<spring:message code="sys_but_back"/>" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
