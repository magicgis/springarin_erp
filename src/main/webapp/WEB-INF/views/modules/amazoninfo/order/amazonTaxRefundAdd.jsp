<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊退款</title>
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
					var flag3= true;

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
								
								//var shouldPrice=$(this).parent().prev().prev().prev().prev().children(":first").val();
								//var shippingPrice=$(this).parent().prev().prev().prev().children(":first").val();
								//var reason=$(this).parent().prev().children(":first").select2("val");
								//var type=$(this).parent().prev().prev().children(":first").select2("val");
							}
							var comparePrice=$(this).prev().val();
							var type=$(this).parent().prev().prev().children(":first").val();
							console.log(comparePrice+"="+type);
							if(type=='Principal'&&parseFloat(comparePrice)<parseFloat($(this).val())){
								flag3=false;
							}
							if(type=='Shipping'&&parseFloat(comparePrice)<parseFloat($(this).val())){
								flag3=false;
							}
						}
					});
					
						if(flag){
							if(flag3){
								 if(flag2){
									  $.ajax({
						     			   type: "POST",
						     			   url: "${ctx}/amazoninfo/refund/isExistOrder?orderTotal="+totalPrice+"&amazonOrderId=${amazonOrder.amazonOrderId }",
						     			   async: false,
						     			   success: function(msg){
						     				  if(msg=="0"){//存在
						     					 top.$.jBox.error("The same refund order already exists","<spring:message code="sys_label_tips_error"/>");
						     				  }else{
						     					
						     					 var confirmInfo="The refund will be processed directly as it does not reach the limit for verification.Do you want to continue?";
						     					 
						     					 top.$.jBox.confirm('Refund money is '+toDecimal(totalPrice)+'?'+confirmInfo,'<spring:message code="sys_label_tips_msg"/>',function(v,h,f){
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
						     				  }
						     			   }
						         		});
									 
									}else{
										top.$.jBox.error("Make sure the refund amount is not null and bigger than 0. Refund reason must be filled in.","<spring:message code="sys_label_tips_error"/>");
									}
							}else{
								top.$.jBox.error("The refund money can not exceed the price of the corresponding order type","<spring:message code="sys_label_tips_error"/>");
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
	<form:form id="inputForm" modelAttribute="amazonOrder"  action="${ctx}/amazoninfo/refund/saveQuick" method="post" class="form-horizontal">
		
		<blockquote>
			<p style="font-size: 14px">Order Info</p>
		</blockquote>
		<table>
		   <tr><td>
		<div class="control-group">
			<label class="control-label"><b>Order No.</b>:</label>
			<div class="controls">
				<input  type="text" name="amazonOrderId" value="${amazonOrder.amazonOrderId }" readonly/>
				<input type='hidden' name='isTax' value='0'/>
			</div> 
		</div>
		</td><td>
		<div class="control-group">
			<label class="control-label"><b>Country</b>:</label>
			<div class="controls">
				<%-- <input  type="text" value="${amazonOrder.country }" readonly/>
				<input  type="hidden" name="country" value="${fns:substringAfterLast(amazonOrder.salesChannel,'.')}"/> --%>
				
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
			<tr><c:set var="promotionDiscount" value="${empty item.promotionDiscount?0:item.promotionDiscount}"/><c:set var="shippingDiscount" value="${empty item.shippingDiscount?0:item.shippingDiscount}"/>
				<td style="vertical-align:middle; " rowspan="${not empty item.shippingPrice&&item.shippingPrice!=0&&!fn:contains(item.promotionIds,'Amazon Core Free Shipping')?'2':'1'}">
				  <input type="text"   style="width: 80%" value="${item.productName}${not empty item.color?'_':''}${item.color}" readonly/>
				</td>  
				<td style="vertical-align:middle; " rowspan="${not empty item.shippingPrice&&item.shippingPrice!=0&&!fn:contains(item.promotionIds,'Amazon Core Free Shipping')?'2':'1'}"><input type="text"  style="width: 80%"  value='<fmt:formatNumber value="${item.itemPrice-promotionDiscount }" maxFractionDigits="2" pattern='#0.00'/>' readonly/>
				</td>
				<td style="vertical-align:middle; " rowspan="${not empty item.shippingPrice&&item.shippingPrice!=0&&!fn:contains(item.promotionIds,'Amazon Core Free Shipping')?'2':'1'}"><input type="text"  style="width: 80%"  value='<fmt:formatNumber value="${empty item.shippingPrice?0:(item.shippingPrice-shippingDiscount) }" maxFractionDigits="2" pattern='#0.00' />' readonly/>
				</td>
				<td><input type="text"  style="width: 80%"  name='refundType' value="Principal" readonly/>
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
				<input type="hidden" value='<fmt:formatNumber value="${item.itemPrice-promotionDiscount }" maxFractionDigits="2" pattern='#0.00'/>' />
				<input type="text" maxlength="11" style="width: 80%" name="money" class="price" value="<fmt:formatNumber maxFractionDigits='2'  value='${(item.itemPrice-promotionDiscount)-((item.itemPrice-promotionDiscount)/(1+rate/100))}' pattern='#0.00'/>"/>
				
				</td> 
				<td><input type="text" maxlength="500" style="width: 80%" name="remark" class="remark" value='Tax Refund'/>
				    <input type="hidden" name="sku" value="${item.sellersku }" />
				    <input type="hidden" name="asin" value="${item.asin }" />
				    <input type="hidden" name="orderItemId" value="${item.orderItemId }" />
				    <input type="hidden"   name="productName"  value="${item.productName}${not empty item.color?'_':''}${item.color}"/>
			
				</td>
			</tr>
			<c:if test="${not empty item.shippingPrice&&item.shippingPrice!=0&&!fn:contains(item.promotionIds,'Amazon Core Free Shipping')}">
			<tr>
				<td>
				<input type="text"  style="width: 80%"  name='refundType' value="Shipping" readonly/>
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
				<input type="hidden"  value='<fmt:formatNumber value="${empty item.shippingPrice?0:(item.shippingPrice-shippingDiscount) }" maxFractionDigits="2" pattern='#0.00'/>'/>
				<input type="text" maxlength="11" style="width: 80%" name="money" class="price" value="<fmt:formatNumber maxFractionDigits='2'  value='${(item.shippingPrice-shippingDiscount)-((item.shippingPrice-shippingDiscount)/(1+rate/100))}' pattern='#0.00'/>"/></td> 
				<td><input type="text" maxlength="500" style="width: 80%" name="remark" class="remark" value='Tax Refund'/>
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
