<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>自发货订单</title>
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
			$('#add-row').on('click', function(e){
			   e.preventDefault();
			   var tableBody = $('.table > tbody'), 
			   lastRowClone = $('tr:last-child', tableBody).clone();
			   $('input[type=text]', lastRowClone).val('');  
			 var html = " <option value='unknown'>unknown</option>	";
			 <c:forEach items="${sku}" var="item"> 
			 		html = html+"<option value=${item.key}>${item.value}</option>";
			 </c:forEach>
			 lastRowClone.find(".sku").html("<select class=\"required\" name=\"sku\" style=\"width: 90%\">"+html+"</select>");
			   lastRowClone.find("select").select2();
			   tableBody.append(lastRowClone);
			});
			$('#contentTable').on('click', '.remove-row', function(e){
			  e.preventDefault();
			  if($('#contentTable tr').size()>2){
				  var row = $(this).parent().parent();
				  row.remove();
			  }
			});
			
			
			$("#reset").click(function(){
				$("#panel-1 input[type != 'button']").each(function(){
					$(this).val($(this).parent().find("span").text().trim());				
				});
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
							if($(this).val()<0){
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
					
					if(!numberflag){
						top.$.jBox.error("<spring:message code='amazon_order_tips24'/>！","<spring:message code="sys_label_tips_error"/>");
					}else{
					
						if(flag){
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
			
			 $("#orderType").change(function(){    
				  $.ajax({  
				        type : 'POST', 
				        url : '${ctx}/amazonAndEbay/mfnOrder/getEventType',  
				        dataType:"json",
				        data : 'type=' +$("#orderType").val()+"&country="+$("#channel").val(),  
				        async: false,
				        success : function(msg){ 
				        	$("#event").empty();   
				        	var option = "<option value=''></option>";  
				            for(var i=0;i<msg.length;i++){
	 							option += "<option  value=\"" + msg[i].id + "\">" + msg[i].subject + "</option>"; 
	 	                    }
				            $("#event").append(option);
				            $("#event").select2("val","");
				        }
				  }); 
	    	  });  
			  
			  $("#event").change(function(){    
				  if($("#event").val()!=null&&$("#event").val()!=''){
					  $.ajax({  
					        type : 'POST', 
					        url : '${ctx}/amazonAndEbay/mfnOrder/getOrderInfo',  
					        dataType:"json",
					        data : 'eventId=' +$("#event").val(),  
					        async: false,
					        success : function(msg){ 
					        	$("#buyerUser").val(msg.customName);
					        	$("#buyerUserEmail").val(msg.customEmail);
					        	$("#shippingAddressName").val(msg.customName);
					        	$("#channel").select2("val",msg.country);
					        	$("select[name='sku']").select2("val",msg.remarks);
					        }
					  }); 
				  }
				  
	    	  }); 
			  
			  $("#btnSubmit").click(function(){
				  if($("#event").val()==null||$("#event").val()==''){
					  $.jBox.tip('event is not null!');
					  return false;
				  }
				  $("#inputForm").submit();
			  });
			  
			  $("#channel").change(function(){
					var params = {};
					params['country'] = $(this).val();
					params['shippingAddress.street1']=encodeURI($("input[name='shippingAddress.street1']").val());
					params['shippingAddress.street']=encodeURI($("input[name='shippingAddress.street']").val());
					params['shippingAddress.street2']=encodeURI($("input[name='shippingAddress.street2']").val());
					params['shippingAddress.cityName']=encodeURI($("input[name='shippingAddress.cityName']").val());
					params['shippingAddress.county']=encodeURI($("input[name='shippingAddress.county']").val());
					params['shippingAddress.stateOrProvince']=encodeURI($("input[name='shippingAddress.stateOrProvince']").val());
					params['shippingAddress.countryCode']=$("input[name='shippingAddress.countryCode']").val();
					params['shippingAddress.postalCode']=$("input[name='shippingAddress.postalCode']").val();
					params['shippingAddress.phone']=$("input[name='shippingAddress.phone']").val();
					params['shippingAddress.name']=encodeURI($("input[name='shippingAddress.name']").val());
					params['event.id']=$("#event").val();
					params.orderType=$("#orderType").val();
					params.status=$("#status").val();
					params.buyerUserEmail=$("#buyerUserEmail").val();
					params.buyerUser=encodeURI($("#buyerUser").val());
					window.location.href = "${ctx}/amazonAndEbay/mfnOrder/edit?"+$.param(params);
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
		
		function getPrice2(b){
				var itemTax = $(b).val();
				var itemPrice=$(b).parent().prev().children(":first").val();
				itemPrice = $.trim(itemPrice);
				itemTax = $.trim(itemTax);
	            if(itemPrice != ''&&itemTax!=''){
	            	$(b).parent().next().children(":first").val(toDecimal(parseFloat(itemPrice)+parseFloat(itemPrice*itemTax/100)));
				}
		}
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            f = Math.round(x*1000)/1000;  
	            return f;  
	     }  
		
	</script>
</head>
<body>

<ul class="nav nav-tabs">
    <li  class="active"><a href="#">${fns:getDictLabel(mfnOrder.country,'platform','')} Order Edit</a></li>	

         <li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">DE Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a href="${ctx}/amazonAndEbay/mfnOrder?country=de">Order List</a></li>	
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/package/packageListDown?country=de">Package List</a></li>	
				   <shiro:hasPermission name="amazon:mfnOrderEdit:de">
					   <li><a href="${ctx}/amazonAndEbay/mfnOrder/trackNumberAdd?country=de">Track Number</a></li>
				   </shiro:hasPermission>
				    <li><a href="${ctx}/amazonAndEbay/mfnOrder/showCurrentOrder?country=de">Delivery List</a></li>	
		    </ul>
	    </li>

         <li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">US Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a href="${ctx}/amazonAndEbay/mfnOrder?country=com">Order List</a></li>	
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/package/packageListDown?country=com">Package List</a></li>	
				   <shiro:hasPermission name="amazon:mfnOrderEdit:com">
					   <li><a href="${ctx}/amazonAndEbay/mfnOrder/trackNumberAdd?country=com">Track Number</a></li>
				   </shiro:hasPermission>
				    <li><a href="${ctx}/amazonAndEbay/mfnOrder/showCurrentOrder?country=com">Delivery List</a></li>	
		    </ul>
	    </li>
	    
	     <li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">JP Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a href="${ctx}/amazonAndEbay/mfnOrder?country=jp">Order List</a></li>	
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/package/packageListDown?country=jp">Package List</a></li>	
				   <shiro:hasPermission name="amazon:mfnOrderEdit:jp">
					   <li><a href="${ctx}/amazonAndEbay/mfnOrder/trackNumberAdd?country=jp">Track Number</a></li>
				   </shiro:hasPermission>
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/showCurrentOrder?country=jp">Delivery List</a></li>	
		    </ul>
	    </li>
	    
	      <li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">CN Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a href="${ctx}/amazonAndEbay/mfnOrder?country=cn">Order List</a></li>	
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/package/packageListDown?country=cn">Package List</a></li>	
				   <shiro:hasPermission name="amazon:mfnOrderEdit:cn">
					   <li><a href="${ctx}/amazonAndEbay/mfnOrder/trackNumberAdd?country=cn">Track Number</a></li>
				   </shiro:hasPermission>
				   <li><a href="${ctx}/amazonAndEbay/mfnOrder/showCurrentOrder?country=cn">Delivery List</a></li>	
		    </ul>
	    </li>
  </ul>
<br/>
	<tags:message content="${message}"/>
	<form:form id="inputForm" modelAttribute="mfnOrder"  action="${ctx}/amazonAndEbay/mfnOrder/saveAdd" method="post" class="form-horizontal">
	    <form:hidden path="id" id="id"/>
		<input type="hidden" name="shippingAddress.id" value="${mfnOrder.shippingAddress.id}" />
		 <input type="hidden" name="invoiceAddress.id" value="${mfnOrder.invoiceAddress.id}" /> 
		<input type="hidden" name="orderId"  value="${mfnOrder.orderId}" />
		<input type="hidden" name="rateSn"           value ="rateSn"  />
		<input type="hidden" name="paymentMethod"    value="${mfnOrder.paymentMethod}"   />
		<input type="hidden" name="buyTime"     value="<fmt:formatDate pattern='yyyy-MM-dd hh:mm:ss' value='${mfnOrder.buyTime}'/>" />
		<input type="hidden" name="paidTime"    value="<fmt:formatDate pattern='yyyy-MM-dd hh:mm:ss' value='${mfnOrder.paidTime}'/>" />
		 <blockquote>
			<p style="font-size: 14px">WareHouse</p>
		</blockquote>
		<table>
		   <tr>
		      <td>
				<div class="control-group">
					<label class="control-label"><b>WareHouse</b>:</label>
					<div class="controls">
						<form:select path="country" style="width: 220px" class="required" id="channel">
							 <option value="de" ${mfnOrder.country eq 'de'?'selected':''}>Germany</option>
							 <option value="com" ${mfnOrder.country eq 'com'?'selected':''}>American</option>
						</form:select>
					</div>
				</div>	
				
		  </td>
		   </tr>
		</table>
		  <blockquote>
			<p style="font-size: 14px">Order Type</p>
		</blockquote>
		<table>
		<tr>
		 <td>
				<div class="control-group">
					<label class="control-label"><b>Order Type</b>:</label>
					<div class="controls">
						<form:select path="orderType" style="width: 220px" class="required" id="orderType">
								<option value="1" ${mfnOrder.orderType eq '1'?'selected':''}>Review Order</option>
								<option value="5" ${mfnOrder.orderType eq '5'?'selected':''}>Support_Voucher</option>
								<option value="2" ${mfnOrder.orderType eq '2'?'selected':''}>Support</option>
						</form:select>
					</div>
				</div>	
				
		  </td>
		 <td>
				<div class="control-group">
					<label class="control-label"><b>Event</b>:</label>
					<div class="controls">
					   
						<form:select path="eventId" style="width: 400px" class="required" id="event">
						       <option value=""></option>
							   <c:forEach items="${eventMap}" var="eventMap">
									<option value="${eventMap.key}" ${mfnOrder.event.id eq eventMap.key?'selected':''}>${eventMap.value}</option>					
							   </c:forEach>
						</form:select>
					</div>
				</div>	
				
		  </td>
		</tr>
		
		</table>
		<blockquote>
			<p style="font-size: 14px"><spring:message code="amazon_order_form_tips1"/></p>
		</blockquote>
		<table>
		   <tr><td>
		<div class="control-group">
			<label class="control-label"><b><spring:message code="amazon_order_form9"/></b>:</label>
			<div class="controls">
				<form:input maxlength="255" class="required" path="buyerUser" id="buyerUser"  />
			</div>
		</div>
		</td><td>
		<div class="control-group">
			<label class="control-label"><spring:message code="amazon_order_form10"/>:</label>
			<div class="controls">
				<form:input maxlength="255" class="email" path="buyerUserEmail" id="buyerUserEmail"/>
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
			 
			 	<table >
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
			 	 		 <td ></td>
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
			   		 	<td ></td>
			   		 </tr>
			   		 
			   		  <tr>
			   		 	<td >
							<div class="control-group">
								<label class="control-label"><spring:message code="amazon_order_form15"/>:</label>
								<div class="controls">
									<form:input maxlength="255" class="span3" path="shippingAddress.stateOrProvince" id="shippingAddress.stateOrProvince" />
								</div>
							</div>
			   		 	</td>
			   		 	<td >
							<div class="control-group">
								<label class="control-label"><spring:message code="amazon_order_form16"/>:</label>
								<div class="controls">
									<form:input maxlength="255" class="span3" path="shippingAddress.cityName" id="shippingAddress.cityName" />
								</div>
							</div>
							
			   		 	</td>
			   		 	<td width="80px">
			     		 	<div class="control-group">
								<label class="control-label"><spring:message code="amazon_order_form30"/>:</label>
								<div class="controls">
									<form:input maxlength="255"  class="span2" path="shippingAddress.country" id="shippingAddress.country" />
								</div>
							</div>
			   		 	</td>
			   		 </tr>
			   		 <tr>
			   		 	<td >
							<div class="control-group">
								<label class="control-label"><b><spring:message code="amazon_order_form17"/></b> :</label>
								<div class="controls">
										<form:input maxlength="255" class="required" path="shippingAddress.street" id="shippingAddress.street" />
								</div>
							</div>
			   		 	</td>
			   		 	<td>
							<div class="control-group">
								<label class="control-label"><spring:message code="amazon_order_form18"/>:</label>
								<div class="controls" >
									<form:input maxlength="255" class="span3"  path="shippingAddress.street1" id="shippingAddress.street1"/>
								</div>
							</div>	
			   		 	</td>
			   		 	<td width="80px">  
							<div class="control-group">
								<label class="control-label"><spring:message code="amazon_order_form19"/>:</label>
								<div class="controls">
									<form:input class="span2" maxlength="255" path="shippingAddress.street2" id="shippingAddress.street2"/>
								</div>
							</div>		
			   		 	</td>
			   		 </tr>
			 	</table>
			 </div>
			</div>
		
		<blockquote>
			<p style="font-size: 14px">Order Info</p>
		</blockquote>
			<table>
		   <tr><td>
				<div class="control-group">
					<label class="control-label"><b><spring:message code="ebay_order_status"/></b>:</label>
					<div class="controls">
						<form:select path="status" style="width: 200px" class="required" id="status">
								<%-- <option value="" ${mfnOrder.status eq  ''?'selected':''}><spring:message code="ebay_order_status_all"/></option> --%>
									<option value="0" ${mfnOrder.status eq '0'?'selected':''}>paid,not deliver</option>
									<%-- <option value="1" ${mfnOrder.status eq '1'?'selected':''}>paid,deliver</option> --%>
									<%-- <option value="2" ${mfnOrder.status eq '2'?'selected':''}>not paid,deliver</option> --%>
									<option value="9" ${mfnOrder.status eq '9'?'selected':''}>cancel</option>
									<%-- <option value="4" ${mfnOrder.status eq '4'?'selected':''}><spring:message code="ebay_order_status_cashOnDelivery"/></option>
									<option value="3" ${mfnOrder.status eq '3'?'selected':''}><spring:message code="ebay_order_status_partShipped"/></option> --%>
						</form:select>
					</div>
		     </div>
		   </td>
		  <td>
		    <div class="control-group">
					<label class="control-label"><spring:message code="amazon_order_form26"/>:</label>
					<div class="controls">
							<form:input maxlength="11" class="price"   path="shippingServiceCost" id="shippingServiceCost"/>
					</div>
				</div>	
		  </td>
		</tr>
		<tr>
		  <td colspan="2">
		   <div class="control-group">
			<label class="control-label">remark:</label>
			<div class="controls">
				<form:textarea path="remark"  htmlEscape="false" maxlength="1000" style="margin: 0px; width: 600px; height: 100px;"/>
			</div>
		  </div>
		  </td>
		</tr>
		</table>
		
		
			
		<div align="right" style="font-size: 14px;margin: 5px 100px 5px 0px;"><a href="#" id="add-row"><span class="icon-plus"></span><spring:message code="order_event1"/></a></div>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 25%"><spring:message code="amazon_order_form20"/></th>
				   <th style="width: 12%"><spring:message code="amazon_order_form23"/></th>
				   <%-- <th style="width: 10%"><spring:message code="amazon_order_form7"/></th> --%>
				   <th style="width: 12%"><spring:message code="amazon_order_form24"/></th>
				   <th style="width: 12%"><spring:message code="amazon_order_form38"/></th>
				   <th style="width: 12%"><spring:message code="amazon_order_form39"/></th>
				   <th style="width: 12%">Discount Money</th>
				   <th style="width: 6%"><spring:message code="sys_label_tips_operate"/></th>
				   
			</tr>
		</thead>
		<tbody>
		    <c:forEach items="${mfnOrder.items}" var="item" >
			<tr>
				<td class="sku"><select style="width: 90%" name="sku" >
				    <option value="unknown">unknown</option>	
					<c:forEach items="${sku}" var="itemSku">
						<option value="${itemSku.key}"  ${itemSku.key eq item.sku ?'selected':''}>${itemSku.value}</option>									
					</c:forEach>
				</select></td>
				<td><input type="text" maxlength="10" style="width: 80%" value="${item.quantityPurchased }" name="quantityPurchased" class="digits required">
				<!-- <input type="hidden" maxlength="10" style="width: 80%" name="quantityShipped" class="number required"> -->
				</td>
				<td><input type="text" maxlength="11" style="width: 80%" name="itemPrice" value="${item.itemPrice }" class="price" onblur="getPrice1(this);"/></td>
				<td><input type="text" maxlength="11" style="width: 80%" name="itemTax" value="${item.itemTax }" class="price" onblur="getPrice2(this);"/></td>
				<td><input type="text" maxlength="11" style="width: 80%" name="includeTax" value="<fmt:formatNumber value='${item.itemPrice+item.itemPrice*item.itemTax/100}' maxFractionDigits='2' /> "  class="price" readonly='readonly'/></td>
				<td><input type="text" maxlength="11" style="width: 80%" name="codFee" value="${item.codFee }" class="price" /></td>
				<td><a href="#" id="remove-row" class="remove-row"><span class="icon-minus"></span></a></td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
		 <div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code="sys_but_save"/>"/>&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="<spring:message code="sys_but_back"/>" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
