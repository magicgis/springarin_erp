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
			
			
			  
			  $("#btnSubmit").click(function(){
				 
				  $("#inputForm").submit();
			  });
			  
			 
		});
		
		
	</script>
</head>
<body>
<%-- 	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/amazonAndEbay/mfnOrder">Order List</a></li>
		<li class="active"><a href="#">Order Edit</a></li>
	</ul> --%>
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
	<form:form id="inputForm" modelAttribute="mfnOrder"  action="${ctx}/amazonAndEbay/mfnOrder/saveAddress" method="post" class="form-horizontal">
	    <form:hidden path="id" id="id"/>
		<input type="hidden" name="shippingAddress.id" value="${mfnOrder.shippingAddress.id}" />
		 
		 <blockquote>
			<p style="font-size: 14px">Order Info</p>
		</blockquote>
		<table>
		   <tr><td>
		<div class="control-group">
			<label class="control-label"><b>Order NO.</b>:</label>
			<div class="controls">
				${mfnOrder.orderId }
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
				${mfnOrder.buyerUser }
			</div>
		</div>
		</td><td>
		<div class="control-group">
			<label class="control-label"><b><spring:message code="amazon_order_form10"/></b>:</label>
			<div class="controls">
				${mfnOrder.buyerUserEmail }
			</div>
		</div>
		</td>
		</tr>
		
		</table>
		
		
		<blockquote>
			<p style="font-size: 14px">Size Info:</p>
		</blockquote>
		<table>
		   <tr>
		    <td>
				<div class="control-group">
					<label class="control-label"><b>Length(inch)</b>:</label>
					<div class="controls">
						<input type="text"  class="price" name="length" id="length"  value='<fmt:formatNumber value="${empty mfnOrder.length?1:mfnOrder.length}" maxFractionDigits="2" pattern="#0.00"/>' />
					</div>
				</div>
				</td><td>
				<div class="control-group">
					<label class="control-label"><b>Width(inch)</b>:</label>
					<div class="controls">
						<input type="text"  class="price" name="width" id="width" value='<fmt:formatNumber value="${empty mfnOrder.width?1:mfnOrder.width}" maxFractionDigits="2" pattern="#0.00"/>' />
					</div>
				</div>
		   </td>
		</tr>
		
		 <tr>
		    <td>
				<div class="control-group">
					<label class="control-label"><b>Height(inch)</b>:</label>
					<div class="controls">
						<input type="text"  class="price" name="height" id="height" value='<fmt:formatNumber value="${empty mfnOrder.height?1:mfnOrder.height}" maxFractionDigits="2" pattern="#0.00"/>' />
					</div>
				</div>
				</td><td>
				<div class="control-group">
					<label class="control-label"><b>Weight(oz)</b>:</label>
					<div class="controls">
						<input type="text"   name="weight" id="weight" value='${mfnOrder.weight}'/>
					</div>
				</div>
		   </td>
		</tr>
		
		</table>
		
		<blockquote>
			<p style="font-size: 14px">Remark</p>
		</blockquote>
		<table>
		   <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>Remark</b>:</label>
					<div class="controls">
						<form:textarea path="remark" htmlEscape="false" maxlength="1000" style="margin: 0px; width: 600px; height: 100px;"/>
					</div>
				</div>
		    </td>
		   </tr>
		</table>
		
	<%-- 	<c:if test="${mfnOrder.status eq '0' }"> --%>
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
									<form:input maxlength="255"  path="shippingAddress.postalCode" id="shippingAddress.postalCode"/>
								</div>
							</div>
			   		 	</td>
			   		 	<td >
							<div class="control-group">
								<label class="control-label"><b><spring:message code="amazon_order_form14"/></b>:</label>
								<div class="controls">
									<form:input maxlength="255"  path="shippingAddress.countryCode" id="shippingAddress.countryCode"/>
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
										<form:input maxlength="255"  path="shippingAddress.street" id="shippingAddress.street" />
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
		<%-- </c:if> --%>
		
		
		 <div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code="sys_but_save"/>"/>&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="<spring:message code="sys_but_back"/>" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
