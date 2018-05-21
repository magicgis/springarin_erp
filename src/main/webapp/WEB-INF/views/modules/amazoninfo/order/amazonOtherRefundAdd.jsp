<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊其他退款</title>
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
			
			
			$("#inputForm").validate({
				
				submitHandler: function(form){
					var flag = true;
					var flag2 = false;
					var totalPrice=0;
					if($("#operUser").val()==''||$("#operUser").val()==null){
						top.$.jBox.error("Reviewer is not null","<spring:message code="sys_label_tips_error"/>");
						return;
					}
					$(".price").each(function(){
						if($(this).val()!=''){
							if(!$.isNumeric($(this).val())){
								flag = false;
							}
							if($(this).val()<0){
								flag = false;
							}
						} 
						if($(this).val()!=''&&parseFloat($(this).val().trim())!=0){
							totalPrice+=parseFloat($(this).val().trim());
							var remark=$(this).parent().next().children(":first").val();
							if(remark!=null&&remark!=''){
								flag2=true;
							}
							
						}
					});
					
						if(flag){
								 if(flag2){
										top.$.jBox.confirm('Refund money is '+toDecimal(totalPrice)+'?','<spring:message code="sys_label_tips_msg"/>',function(v,h,f){
											if(v=='ok'){
													loading('<spring:message code="sys_label_tips_submit"/>');
													var k=0;
													$("#contentTable tbody tr").each(function(i,j){
														var money=$(j).find("input[name='money']").val();
														if(money!=""&&parseFloat(money.trim())!=0){
															$(j).find("select").each(function(){
																if($(this).attr("name")){
																	$(this).attr("name","items"+"["+k+"]."+$(this).attr("name"));
																}
															});
															$(j).find("input[type!='']").each(function(){
																if($(this).attr("name")){
																	$(this).attr("name","items"+"["+k+"]."+$(this).attr("name"));
																}
															});
															k++;
														}
													});
													form.submit();
													$("#btnSubmit").attr("disabled","disabled");
												}
											},{buttonsFocus:1,persistent: true});
											top.$('.jbox-body .jbox-icon').css('top','55px');
										
									}else{
										top.$.jBox.error("Make sure the refund amount is not null and bigger than 0. Refund reason must be filled in.","<spring:message code="sys_label_tips_error"/>");
									}
						}else{
							top.$.jBox.error("<spring:message code="amazon_order_tips5"/>","<spring:message code="sys_label_tips_error"/>");
						}
					
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
		
		
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            f = Math.round(x*100)/100;  
	            return f;  
	     }  
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="#"><spring:message code="amazon_order_tab1"/></a></li>
		<li class="active"><a href="#">Refund Add</a></li>
	</ul>
<br/>
	<tags:message content="${message}"/>
	<form:form id="inputForm" modelAttribute="amazonOrder"  action="${ctx}/amazoninfo/refund/save" method="post" class="form-horizontal">
		
		<blockquote>
			<p style="font-size: 14px">Order Info</p>
		</blockquote>
		<table>
		   <tr><td>
		<div class="control-group">
			<label class="control-label"><b>Order No.</b>:</label>
			<div class="controls">
				<input  type="text" name="amazonOrderId" value="${amazonOrder.amazonOrderId }" readonly/>
			</div>
		</div>
		</td><td>
		<div class="control-group">
			<label class="control-label"><b>Country</b>:</label>
			<div class="controls">
				<input  type="text" value="${amazonOrder.accountName}  ${amazonOrder.country }" readonly/>
				<input  type="hidden" name="country" value="${fns:substringAfterLast(amazonOrder.salesChannel,'.')}"/>
				<input  type="hidden" name="accountName" value="${amazonOrder.accountName}"/>
			</div>
		</div>
		</td>
		
		</tr>
		<tr>
		  <td>
		<div class="control-group">
			<label class="control-label"><b>Order Total</b>:</label>
			<div class="controls">
				<input  type="text" name="orderTotal" value="${amazonOrder.orderTotal }" readonly/>
			</div>
		</div>
		</td>
		<td>
		  <div class="control-group">
				<label class="control-label">Reviewer:</label>
				<div class="controls">
					<select name="operUser.id" style="width:150px" id="operUser">
						<c:forEach items="${all}" var="user">
							<option value="${user.id}">${user.name}</option>
						</c:forEach>
					</select>
				</div>
			</div>
		</td>
		</tr>
		</table>
		
		<blockquote>
			<p style="font-size: 14px">Refund Record</p>
		</blockquote>
		<table class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 20%">Product Name</th>
				   <th style="width: 25%">Refund Type</th>
				   <th style="width: 25%">Amazon Refund Reason</th>
				   <th style="width: 10%">Refund Money</th>
				   <th style="width: 20%">Actual Refund Reason</th>
			</tr>
		</thead>
		<tbody>
		<c:set var="totalMoney" value="0"/>
		<c:forEach items="${records}" var="record" >
		   <c:forEach items="${record.items}" var="temp" >
		        <c:set var="totalMoney" value="${totalMoney+temp.money }"/>
				<tr>
					<td>${temp.productName }</td>  
					<td>${temp.refundType }</td> 
					<td>${temp.refundReason }</td>
					<td>${temp.money }</td> 
					<td>${temp.remark  }</td>
				</tr>
			</c:forEach>
		</c:forEach>
		</tbody>
		<tfoot>
		  <tr>
		    <th colspan="3" style="text-align: center;vertical-align: middle">Total</th>
		    <th><fmt:formatNumber value="${totalMoney}" maxFractionDigits="2"/></th>
		    <th></th>
		  </tr> 
		</tfoot>
	</table>
		<blockquote>
			<p style="font-size: 14px">Refund Detail</p>
		</blockquote>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 20%">Product Name</th>
				   <th style="width: 10%">Total Money</th>
				   <th style="width: 10%">Shipping Price</th>
				   <th style="width: 15%">Refund Type</th>
				   <th style="width: 15%">Amazon Refund Reason</th>
				   <th style="width: 10%">Refund Money</th>
				   <th style="width: 20%">Actual Refund Reason</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${amazonOrder.items}" var="item" >
		 <c:if test="${fn:contains(selectItems,item.id)}">
			<tr><c:set var="promotionDiscount" value="${empty item.promotionDiscount?0:item.promotionDiscount}"/><c:set var="shippingDiscount" value="${empty item.shippingDiscount?0:item.shippingDiscount}"/>
				<td style="vertical-align:middle; ">
				  <input type="text"   style="width: 80%" value="${item.productName}${not empty item.color?'_':''}${item.color}" readonly/>
				</td>  
				<td style="vertical-align:middle;"><input type="text"  style="width: 80%"  value='<fmt:formatNumber value="${item.itemPrice-promotionDiscount }" maxFractionDigits="2"/>' readonly/>
				</td>
				<td style="vertical-align:middle; "><input type="text"  style="width: 80%"  value='<fmt:formatNumber value="${empty item.shippingPrice?0:(item.shippingPrice-shippingDiscount) }" maxFractionDigits="2"/>' readonly/>
				</td>
				<td><input type="text"  style="width: 80%"  name='refundType' value="Other" readonly/>
				</td> 
				<td>
				  <select name="refundReason" style="width:80%" class="required">
						<option value="GeneralAdjustment" >GeneralAdjustment</option>
						<option value="NoInventory">NoInventory</option>
						<option value="CustomerReturn">CustomerReturn</option>
						<option value="CouldNotShip">CouldNotShip</option>
						<option value="DifferentItem">DifferentItem</option>
						<option value="Abandoned">Abandoned</option>
						<option value="CustomerCancel">CustomerCancel</option>
						<option value="PriceError">PriceError</option>
						<option value="ProductOutofStock">ProductOutofStock</option>
						<option value="CustomerAddressIncorrect">CustomerAddressIncorrect</option>
						<option value="Exchange">Exchange</option>
						<option value="Other">Other</option>
						<option value="CarrierCreditDecision">CarrierCreditDecision</option>
						<option value="RiskAssessmentInformationNotValid">RiskAssessmentInformationNotValid</option>
						<option value="CarrierCoverageFailure">CarrierCoverageFailure</option>
						<option value="TransactionRecord">TransactionRecord</option>
				  </select>
				</td>
				<td>
				<input type="text" maxlength="11" style="width: 80%" name="money" class="price"/></td> 
				<td><input type="text" maxlength="500" style="width: 80%" name="remark" class="remark"/>
				    <input type="hidden" name="sku" value="${item.sellersku }" />
				    <input type="hidden" name="asin" value="${item.asin }" />
				    <input type="hidden" name="orderItemId" value="${item.orderItemId }" />
				    <input type="hidden"   name="productName"  value="${item.productName}${not empty item.color?'_':''}${item.color}"/>
				</td>
			</tr>
			
		</c:if>
		</c:forEach>
		</tbody>
	</table>
		 <div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_save"/>"/>&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="<spring:message code="sys_but_back"/>" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
