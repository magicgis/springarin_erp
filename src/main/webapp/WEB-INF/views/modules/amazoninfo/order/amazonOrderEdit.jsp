<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊发货订单详情</title>
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
		 <c:if test="${amazonUnlineOrder.outBound!='0'}" >
			$("#contentTable tbody tr").each(function(i,j){
				$(j).find("select").each(function(){
					$(this).attr("disabled","disabled");
				});
				$(j).find("input[type!='']").each(function(){
					$(this).attr("readonly","readonly");
				});
			}); 
			$("#salesChannel").attr("disabled","disabled");
			$("#orderStatus").attr("disabled","disabled");
		</c:if>
		
			$('#add-row').on('click', function(e){
			   e.preventDefault();
			   var tableBody = $('.table > tbody'), 
			   lastRowClone = $('tr:last-child', tableBody).clone();
			   $('input[type=text]', lastRowClone).val('');  
			   $('input[type=hidden]', lastRowClone).val('');  
			 var html = "";
			 <c:forEach items="${sku}" var="itemSku"> 
			 		html = html+"<option value=${itemSku.key}>${itemSku.value}</option>";
			 </c:forEach>
			 lastRowClone.find(".sku").html("<select class=\"required\" name=\"sellersku\" style=\"width: 90%\">"+html+"</select>");
			   lastRowClone.find("select").select2();
			   tableBody.append(lastRowClone);
			});
			$('#contentTable').on('click', '.remove-row', function(e){
			  e.preventDefault();
			  if($('#contentTable tr').size()>2){
				  var row = $(this).parent().parent();
				  if(row.find("input[name='quantityShipped']").val()>0);
					top.$.jBox.confirm("<spring:message code="amazon_order_tips23"/>？",'<spring:message code="sys_label_tips_msg"/>',function(v,h,f){
						if(v=='ok'){
							 row.remove();
						}
					},{buttonsFocus:1,persistent: true});
			  }
			});
			
			
			$("#btnCancel").click(function(){
				window.location.href = "${ctx}/amazoninfo/unlineOrder";
			});
			
		
			
			$("#inputForm").validate({
				
				submitHandler: function(form){
					var flag = true;
					var numberflag = true;
					$(".price").each(function(){
						if($(this).val()!=''){
							if(!$.isNumeric($(this).val())){
								flag = false;
							}
						} 
					});
					
					$(".digits").each(function(){
						if($(this).val()!=''){
							if(!$.isNumeric($(this).val())){
								flag = false;
							}
							if($(this).val()<=0){
								flag = false;
							}
						} 
					});
					
					$("#contentTable tbody tr").each(function(i,j){
						if($(this).find("input[name='quantityOrdered']").val()-$(this).find("input[name='quantityShipped']").val()<0){
							numberflag = false;
						}
					});
					
					if(!numberflag){
						top.$.jBox.error("<spring:message code='amazon_order_tips24'/>！","<spring:message code="sys_label_tips_error"/>");
					}else{
						if(flag){
							var aa="";
							var unShipped = 0;
							$("#contentTable tbody tr").each(function(i,j){
								unShipped+=$(this).find("input[name='quantityOrdered']").val()-$(this).find("input[name='quantityShipped']").val();
							});
							
							if(unShipped==0){
								aa="<spring:message code="amazon_order_tips26"/>";
							}else{
								aa="<spring:message code="amazon_order_tips6"/>";
							}
							
							top.$.jBox.confirm(aa,'<spring:message code="sys_label_tips_msg"/>',function(v,h,f){
								if(v=='ok'){
									$("#contentTable tbody tr").each(function(i,j){
										$(j).find("select").attr("name","items"+"["+i+"]."+$(j).find("select").attr("name"));
										$(j).find("input[type!='']").each(function(){
											if($(this).attr("name")){
												$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
											}
										});
										
									});
									$("select[disabled]").each(function(){
										$(this).removeAttr("disabled");
									});
									form.submit();
									$("#btnSubmit").attr("disabled","disabled");
								}
							},{buttonsFocus:1,persistent: true});
							
							
							top.$('.jbox-body .jbox-icon').css('top','55px');
						}else{
							top.$.jBox.error("<spring:message code="amazon_order_tips5"/>","<spring:message code="sys_label_tips_error"/>");
						}
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
		function getPrice1(b){
				var itemPrice = $(b).val();
				var includeTax=$(b).parent().next().children(":first").val();
				itemPrice = $.trim(itemPrice);
				includeTax = $.trim(includeTax);
				if(itemPrice != ''&&includeTax!=''){
					$(b).parent().next().next().children(":first").val(toDecimal(parseFloat(itemPrice)+parseFloat(itemPrice*includeTax/100)));
				}
		
		}
		function setShipQuantity(quantity){
			var amount=$(quantity).val();
			$(quantity).next().val(amount);
		}
		function getPrice2(b){
				var itemTax = $(b).val();
				var itemPrice=$(b).parent().prev().children(":first").val();
				itemPrice = $.trim(itemPrice);
				itemTax = $.trim(itemTax);
	            if(itemPrice != ''&&itemTax!=''){
	            	$(b).parent().next().children(":first").val(toDecimal(parseFloat(itemPrice)+parseFloat(itemPrice*itemTax/100)));
				}
		}
		
		function getPrice3(b){
			//$("input[name='itemTax']").blur(function() {
				var itemTaxPrice = $(b).val();
				var itemTax=$(b).parent().prev().children(":first").val();
				itemTaxPrice = $.trim(itemTaxPrice);
				itemTax = $.trim(itemTax);
				if(itemTaxPrice != ''&&itemTax!=''){
					$(b).parent().prev().prev().children(":first").val(toDecimal(parseFloat(itemTaxPrice)/(1+itemTax/100)));
				}
			//});
		}
		
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
		<li ><a href="${ctx}/amazoninfo/unlineOrder">Unline Order List</a></li>
		<li class="active"><a href="#">Unline Order Edit</a></li>
	</ul>
<br/>
	<tags:message content="${message}"/>
	<form:form id="inputForm" modelAttribute="amazonUnlineOrder"  action="${ctx}/amazoninfo/unlineOrder/saveAdd" method="post" class="form-horizontal">
		<form:hidden path="id" id="id"/>
		<input type="hidden" name="shippingAddress.id" value="${amazonUnlineOrder.shippingAddress.id}" />
		<input type="hidden" name="amazonOrderId"  value="${amazonUnlineOrder.amazonOrderId}" id="amazonOrderId"/>
		<input type="hidden" name="sellerOrderId"  value="${amazonUnlineOrder.sellerOrderId}" id="sellerOrderId"/>
		<input type="hidden" name="invoiceFlag"      value="${amazonUnlineOrder.invoiceFlag}"    id="invoiceFlag" />
		<input type="hidden" name="rateSn"           value ="rateSn" id="rateSn" />
		<input type="hidden" name="paymentMethod"    value="${amazonUnlineOrder.paymentMethod}"  id="paymentMethod" />
		<input type="hidden" name="orderChannel"     value="${amazonUnlineOrder.orderChannel}"   id="orderChannel" />
		<input type="hidden" name="purchaseDate"     value="${amazonUnlineOrder.purchaseDate}"   id="purchaseDate" />
		<input type="hidden" name="lastUpdateDate"     value="${amazonUnlineOrder.lastUpdateDate}"   id="lastUpdateDate" />
		<input type="hidden" name="outBound"     value="${amazonUnlineOrder.outBound}"   id="outBound" />
		<input type="hidden" name="invoiceNo"     value="${amazonUnlineOrder.invoiceNo}"   id="invoiceNo" />
		<blockquote>
			<p style="font-size: 14px"><spring:message code="amazon_order_form_tips1"/></p>
		</blockquote>
		<table>
		   <tr><td>
		<div class="control-group">
			<label class="control-label"><b><spring:message code="amazon_order_form9"/></b>:</label>
			<div class="controls">
				<form:input maxlength="255" class="required" path="buyerName" id="buyerName"  />
			</div>
		</div>
		</td><td>
		<div class="control-group">
			<label class="control-label"><b><spring:message code="amazon_order_form10"/></b>:</label>
			<div class="controls">
				<form:input maxlength="255" class="email required" path="buyerEmail" id="buyerEmail"/>
			</div>
		</div>
		</td>
		</tr>
		
		</table>
		
		
		<blockquote>
			<p style="font-size: 14px"><spring:message code="amazon_order_form_tips2"/></p>
		</blockquote>
		
		<div class="tab-content" id="cTab">
			 <div class="tab-pane active" id="panel-0">
			 
			 	<table>
			 	 	<tr>
			 	  		<td >
						 	<div class="control-group">
									<label class="control-label"><b><spring:message code="amazon_order_form11"/></b>:</label>
									<div class="controls">
										<form:input maxlength="255" class="required" path="shippingAddress.name" id="shippingAddress.name"/>
									</div>
							</div>
			 	 		 </td>
			 	 		 <td >
							<div class = "control-group">
									<label class="control-label"><spring:message code="amazon_order_form12"/>:</label>
									<div class="controls">
										<form:input maxlength="255"  path="shippingAddress.phone" id="shippingAddress.phone" />
									</div>
							</div>
			 	 		 
			 	 		 </td>
			   		 </tr>
			   		 <tr>
			   		 	<td >
							<div class="control-group">
								<label class="control-label"><b><spring:message code="amazon_order_form13"/></b>:</label>
								<div class="controls">
									<form:input maxlength="255" class="required" path="shippingAddress.postalCode" id="shippingAddress.postalCode"/>
								</div>
							</div>
			   		 	</td>
			   		 	<td >
							<div class="control-group">
								<label class="control-label"><b><spring:message code="amazon_order_form14"/></b>:</label>
								<div class="controls">
									<form:input maxlength="255" class="required" path="shippingAddress.countryCode" id="shippingAddress.countryCode"/>
								</div>
							</div>
			   		 	
			   		 	</td>
			   		 </tr>
			   		 </table>
			   		 <table>
			   		 
			   		  <tr>
			   		 	<td >
							<div class="control-group">
								<label class="control-label"><spring:message code="amazon_order_form15"/>:</label>
								<div class="controls">
									<form:input maxlength="255" class="span3" path="shippingAddress.stateOrRegion" id="shippingAddress.stateOrRegion" />
								</div>
							</div>
			   		 	</td>
			   		 	<td >
							<div class="control-group">
								<label class="control-label"><spring:message code="amazon_order_form16"/>:</label>
								<div class="controls">
									<form:input maxlength="255" class="span3" path="shippingAddress.city" id="shippingAddress.city" />
								</div>
							</div>
							
			   		 	</td>
			   		 	<td>
			     		 	<div class="control-group">
								<label class="control-label"><spring:message code="amazon_order_form30"/>:</label>
								<div class="controls">
									<form:input maxlength="255"  class="span2" path="shippingAddress.county" id="shippingAddress.county" />
								</div>
							</div>
			   		 	</td>
			   		 </tr>
			   		 <tr>
			   		 	<td >
							<div class="control-group">
								<label class="control-label"><b><spring:message code="amazon_order_form17"/></b> :</label>
								<div class="controls">
										<form:input maxlength="255" class="required" path="shippingAddress.addressLine1" id="shippingAddress.addressLine1" />
								</div>
							</div>
			   		 	</td>
			   		 	<td>
							<div class="control-group">
								<label class="control-label"><spring:message code="amazon_order_form18"/>:</label>
								<div class="controls" >
									<form:input maxlength="255" class="span3"  path="shippingAddress.addressLine2" id="shippingAddress.addressLine2"/>
								</div>
							</div>	
			   		 	</td>
			   		 	<td>  
							<div class="control-group">
								<label class="control-label"><spring:message code="amazon_order_form19"/>:</label>
								<div class="controls">
									<form:input class="span2" maxlength="255" path="shippingAddress.addressLine3" id="shippingAddress.addressLine3"/>
								</div>
							</div>		
			   		 	</td>
			   		 </tr>
			 	</table>
			 </div>
			</div>
  			
		<blockquote>
			<p style="font-size: 14px"><spring:message code="amazon_order_form_tips3"/></p>
		</blockquote>
		
			<table>
			<tr>
				<td>
					<div class="control-group">
						<label class="control-label"><b>WareHouse</b>:</label>
						<div class="controls">
							<select id="salesChannel" name="salesChannel.id" style="width: 200px" class="required">
								<c:forEach items="${stocks}" var="stock">
										<option value="${stock.id}" ${stock.id eq amazonUnlineOrder.salesChannel.id?'selected':''}>
											<c:choose>
												<c:when test="${stock.id eq '19' }">Germany</c:when>
												<c:when test="${stock.id eq '21' }">China_ChunYu</c:when>
												<c:when test="${stock.id eq '120' }">American</c:when>
												<c:when test="${stock.id eq '130' }">China_LiCheng</c:when>
												<c:otherwise>${stock.stockName}</c:otherwise>
											</c:choose>
										</option>
								</c:forEach>	
							</select>
						</div>
					</div>
				</td>
				<td>
					<div class="control-group" style="display:${amazonUnlineOrder.salesChannel.id eq '21'?'block':'none'}">
						<label class="control-label"><b>货品来源</b>:</label>
						<div class="controls">
							<select id="origin" name="origin" style="width: 200px" class="required">
								<option value="" >请选择</option>
								<option value="0" ${amazonUnlineOrder.origin eq '0'?'selected':'' }>重新下订单</option>
								<option value="1" ${amazonUnlineOrder.origin eq '1'?'selected':'' }>仓库有现货</option>
							</select>
						</div>
					</div>
				</td>
				</tr>
		</table>
		
		<blockquote>
			<p style="font-size: 14px"><spring:message code="amazon_order_form35"/></p>
		</blockquote>
		
		<div class="control-group">
			<label class="control-label"><b><spring:message code="amazon_order_form35"/></b>:</label>
			<div class="controls">
				<select name="orderStatus" style="width: 200px" class="required" id="orderStatus">
				        <option value="PaymentPending"  ${amazonUnlineOrder.orderStatus eq 'PaymentPending'?'selected':''}>PaymentPending</option>
						<option value="PendingAvailability" ${amazonUnlineOrder.orderStatus eq 'PendingAvailability'?'selected':''}>PendingAvailability</option>
						<option value="Pending" ${amazonUnlineOrder.orderStatus eq 'Pending'?'selected':''}>Pending</option>
						<option value="Unshipped" ${amazonUnlineOrder.orderStatus eq 'Unshipped'?'selected':''}>Unshipped</option> 
					    <option value="Shipped" ${amazonUnlineOrder.orderStatus eq 'Shipped'?'selected':''}>Shipped</option> 
						<option value="Canceled" ${amazonUnlineOrder.orderStatus eq 'Canceled'?'selected':''}>Canceled</option>
						<option value="Unfulfillable" ${amazonUnlineOrder.orderStatus eq 'Unfulfillable'?'selected':''}>Unfulfillable</option>
				</select>
			</div>
		</div>
		
		<blockquote>
			<p style="font-size: 14px">Remark</p>
		</blockquote>
	   <table>
			 <tr>
			  <td>	
				  <div class="control-group">
					 <div class="control-group">
						<label class="control-label">Remark:</label>
						<div class="controls">
							<form:textarea path="cbaDisplayableShippingLabel"  htmlEscape="false" maxlength="1000" style="margin: 0px; width: 600px; height: 100px;"/>
						</div>
					  </div>
				</div>
			  </td>
			</tr>
		</table>
		
			<blockquote>
			<p style="font-size: 14px">Supplier</p>
		</blockquote>
	 <table>
		 <tr><td>	
		  <div class="control-group">
			<label class="control-label">Supplier:</label>
			<div class="controls">
				<form:select path="supplier" style="width: 200px"  id="supplier">
					<option value="" ${amazonUnlineOrder.supplier eq '' ?'selected':''}></option>
					<c:forEach items="${typeSupplier}" var="typeSupplier">
						<option value="${typeSupplier}" ${amazonUnlineOrder.supplier eq typeSupplier ?'selected':''}>${typeSupplier}</option>
					</c:forEach>
				</form:select>
			</div>
		</div></td><td>
		<div class="control-group">
				<label class="control-label">Bill No :</label>
					<div class="controls">
						<form:input maxlength="100" path="billNo" id="billNo" />
					</div>
				</div>
		</td></tr></table>
		
		<blockquote>
			<p style="font-size: 14px">Currency</p>
		</blockquote>
	 <table>
		 <tr><td>	
		  <div class="control-group">
			<label class="control-label">Currency:</label>
			<div class="controls">
				<form:select path="marketplaceId" style="width: 200px"  id="marketplaceId">
					<option value="EUR" ${amazonUnlineOrder.marketplaceId eq 'EUR' ?'selected':''}>EUR</option>
					<option value="GBP" ${amazonUnlineOrder.marketplaceId eq 'GBP' ?'selected':''}>GBP</option>
					<option value="CNY" ${amazonUnlineOrder.marketplaceId eq 'CNY' ?'selected':''}>CNY</option>
					<option value="USD" ${amazonUnlineOrder.marketplaceId eq 'USD' ?'selected':''}>USD</option>
					<option value="CAD" ${amazonUnlineOrder.marketplaceId eq 'CAD' ?'selected':''}>CAD</option>
					<option value="MXN" ${amazonUnlineOrder.marketplaceId eq 'MXN' ?'selected':''}>MXN</option>
					<option value="JPY" ${amazonUnlineOrder.marketplaceId eq 'JPY' ?'selected':''}>JPY</option>
				</form:select>
			</div>
		</div></td><td>
		</td></tr></table>
		
		<c:if test="${amazonUnlineOrder.outBound=='0'}">
		  <div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;"><a href="#" id="add-row"><span class="icon-plus"></span><spring:message code="order_event1"/></a></div>
		</c:if>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				  <th style="width: 25%"><spring:message code="amazon_order_form20"/></th>
				   <th style="width: 10%"><spring:message code="amazon_order_form23"/></th>
				  <%--  <th style="width: 10%"><spring:message code="amazon_order_form7"/></th> --%>
				   <th style="width: 10%"><spring:message code="amazon_order_form24"/></th>
				   <th style="width: 10%"><spring:message code="amazon_order_form38"/></th>
				   <th style="width: 10%"><spring:message code="amazon_order_form39"/></th>
				   <th style="width: 10%"><spring:message code="amazon_order_form26"/></th>
				   <th style="width: 10%"><spring:message code="amazon_order_form27"/></th>
				   <th style="width: 6%"><spring:message code="sys_label_tips_operate"/></th>
				   
			</tr>
		</thead>
		<tbody>
		<c:if test="${not empty amazonUnlineOrder.items}" >
		<c:forEach items="${amazonUnlineOrder.items}" var="item" >
			<tr>
				<td class="sku">
				<select style="width: 90%" name="sellersku">
					<c:forEach items="${sku}" var="itemSku">
						<option value="${itemSku.key}" ${itemSku.key eq item.sellersku ?'selected':''}>${itemSku.value}</option>	
													
					</c:forEach>
				</select>
				</td>
				<td>
				<input type="hidden" name="id" value="${item.id}" /><%-- <fmt:formatNumber value='${item.itemPrice/item.quantityOrdered}' maxFractionDigits='2' /> --%>
				<input type="text" maxlength="10" style="width: 80%" name="quantityOrdered" class="digits required" value="${item.quantityOrdered}" onblur="setShipQuantity(this);">
				<input type="hidden" maxlength="11" style="width: 80%" name="quantityShipped" class="number required" value="${item.quantityOrdered}" />
				</td>
				<%-- <td><input type="text" maxlength="11" style="width: 80%" name="quantityShipped" class="number required" value="${item.quantityShipped}" /></td> --%>
				<td><input type="text" maxlength="11" style="width: 80%" name="itemPrice" class="price required" value="${item.itemPrice}" onkeyup="getPrice1(this);"/></td>
				<td><input type="text" maxlength="11" style="width: 80%" name="itemTax" class="price" value="${item.itemTax}" onkeyup="getPrice2(this);"/></td>
				<td><input type="text" maxlength="11" style="width: 80%" name="includeTax" class="price"   onkeyup="getPrice3(this);" value="<fmt:formatNumber value='${item.itemPrice+item.itemPrice*item.itemTax/100}' maxFractionDigits='2' /> " /></td>
				<td><input type="text" maxlength="11" style="width: 80%" name="shippingPrice" class="price" value="${item.shippingPrice}"/></td> 
				<td><input type="text" maxlength="11" style="width: 80%" name="giftWrapPrice" class="price" value="${item.giftWrapPrice}"/></td>
				<td><c:if test="${amazonUnlineOrder.outBound=='0'}"><a href="#" id="remove-row" class="remove-row"><span class="icon-minus"></span></a></c:if></td>
			
			</tr>
		</c:forEach>
		</c:if>
		<c:if test="${empty amazonUnlineOrder.items}" >
			<tr>
				<td class="sku"><select style="width: 90%" name="sellersku" >
					<c:forEach items="${sku}" var="item">
						<option value="${item.key}">${item.value}</option>									
					</c:forEach>
				</select></td>
				<td><input type="text" maxlength="10" style="width: 80%" name="quantityOrdered" class="digits required" onblur="setShipQuantity(this);">
				<input type="text" maxlength="10" style="width: 80%" name="quantityShipped" class="number required"></td>
				<!-- <td><input type="text" maxlength="10" style="width: 80%" name="quantityShipped" class="number required"></td> -->
				<td><input type="text" maxlength="11" style="width: 80%" name="itemPrice" class="price required" /></td>
				<td><input type="text" maxlength="11" style="width: 80%" name="itemTax" class="price"/></td>
				<td><input type="text" maxlength="11" style="width: 80%" name="includeTax" class="price" /></td>
				<td><input type="text" maxlength="11" style="width: 80%" name="shippingPrice" class="price" /></td>
				<td><input type="text" maxlength="11" style="width: 80%" name="giftWrapPrice" class="price" /></td>
				<td><c:if test="${amazonUnlineOrder.outBound=='0'}"><a href="#" id="remove-row" class="remove-row"><span class="icon-minus"></span></a></c:if></td>
			</tr>
		</c:if>
		</tbody>
	</table>
		 <div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_save"/>"/>&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="<spring:message code="sys_but_back"/>" />
		</div>
	</form:form>
</body>
</html>
