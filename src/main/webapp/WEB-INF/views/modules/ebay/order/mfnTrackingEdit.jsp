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
			
			  
			  $("#btnSubmit").click(function(){
				   top.$.jBox.confirm("Generating tranking number？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
						if(v=="ok"){
							 $("#btnSubmit").attr("disabled","disabled");
							 loading('loading...');
							 var formdata = new FormData($("#inputForm")[0]);   
							 $.ajax({  
					                url :"${ctx}/amazonAndEbay/mfnOrder/genTrackingNumber",  
					                type : 'POST',  
					                data : formdata,  
					                processData : false,  
					                contentType : false,  
					                success : function(responseStr) {
					                	top.$.jBox.closeTip(); 
					                	if("0"==responseStr){
							        		$.jBox.error('Please input product size');
							        		$('#btnSubmit').removeAttr("disabled");
							        	}else if(responseStr.indexOf("1,")>=0){
							        		$.jBox.tip('Successful');
							        		var ctx='${ctx}';
							        		var link=ctx.substring(0,ctx.length-2)+responseStr.substring(2);
							        		$("#imageLabel").html("<a target='_blank' href='"+link+"'>LabelImage</a>");
							        	}else{
							        		$.jBox.error(responseStr);
							        		$('#btnSubmit').removeAttr("disabled");
							        	}
					                },  
					                error : function(responseStr) {  
					                	top.$.jBox.closeTip(); 
					                	$.jBox.tip('failed', 'error');
					                	$('#btnSubmit').removeAttr("disabled");
					                }  
					         });  
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
			  });
			  
			  
			  $("#feeView").click(function(){   
				   var formdata = new FormData($("#inputForm")[0]); 
				   loading('loading...');
				   $.ajax({  
		                url :"${ctx}/amazonAndEbay/mfnOrder/feeView",  
		                type : 'POST',  
		                data : formdata,  
		                processData : false,  
		                contentType : false,  
		                success : function(responseStr) { 
		                	top.$.jBox.closeTip(); 
		                	$("#fee").text(responseStr);
		                },  
		                error : function(responseStr) {  
		                	top.$.jBox.closeTip(); 
		                	$.jBox.tip('failed', 'error');
		                }  
		            });  
			  });
			  
			  $("#postageBalance").click(function(){
				   
		        	top.$.jBox.confirm("View PostageBalance？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
						if(v=="ok"){
							 loading('loading...');
							 $.post("${ctx}/amazonAndEbay/mfnOrder/postageBalance",{},function(msg){
								 top.$.jBox.closeTip(); 
								 if(msg.indexOf("error")>=0){
									 $.jBox.error(msg);
								 }else{
									 $.jBox.tip(msg);
								 }
						     });
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
			  });
			  
			  $("#cancelTracking").click(function(){   
				    var id = '${mfnOrder.id}';
		        	
		        	top.$.jBox.confirm("Cancel tranking number？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
						if(v=="ok"){
							 loading('loading...');
							 $.post("${ctx}/amazonAndEbay/mfnOrder/voidByTrackingNumber",{id:id},function(msg){
								 top.$.jBox.closeTip(); 
								 if(msg.indexOf("error")>=0){
									 $.jBox.error(msg);
								 }else{
									 $.jBox.tip(msg);
								 }
						     });
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
					 
			  });
			  
		});
		
		function setType(c){
			  var mailClass= $(c).val();
			  var packageOption="";
			  if(mailClass=='PM'){
				    packageOption="<option value='PACKAGE'>PACKAGE</option><option value='FLAT RATE ENV'>FLAT RATE ENV</option><option value='LEGAL FLAT RATE ENV'>LEGAL FLAT RATE ENV</option>";
					packageOption+="<option value='PADDED FLAT RATE ENV'>PADDED FLAT RATE ENV</option><option value='SM FLAT RATE BOX'>SM FLAT RATE BOX</option><option value='MD FLAT RATE BOX'>MD FLAT RATE BOX</option>";
					packageOption+="<option value='LG FLAT RATE BOX'>LG FLAT RATE BOX</option><option value='REGIONAL RATE BOX A'>REGIONAL RATE BOX A</option><option value='REGIONAL RATE BOX B'>REGIONAL RATE BOX B</option>";
			  }else if(mailClass=='EX'){
				 packageOption="<option value='PACKAGE'>PACKAGE</option><option value='FLAT RATE ENV'>FLAT RATE ENV</option><option value='LEGAL FLAT RATE ENV'>LEGAL FLAT RATE ENV</option><option value='PADDED FLAT RATE ENV'>PADDED FLAT RATE ENV</option>";
			  }else if(mailClass=='FC'){
				  packageOption="<option value='PACKAGE'>PACKAGE</option>";
			  }
			  $("#packageType").empty(); 
			  $("#packageType").append(packageOption);
			  $("#packageType").select2("val","PACKAGE");
		}
	</script>
</head>
<body>

	<ul class="nav nav-tabs">
    <li  class="active"><a href="#">${fns:getDictLabel(mfnOrder.country,'platform','')} Tracking Edit</a></li>	
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
	<form:form id="inputForm" modelAttribute="mfnOrder" method="post" class="form-horizontal">
	    <input name="id"  value='${mfnOrder.id }' type='hidden'/>
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
			<p style="font-size: 14px">PackageType</p>
		</blockquote>
		<table>
		   <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>MailClass</b>:</label>
					<div class="controls">
						   <select id='mailClass' name='mailClass' onchange='setType(this);'>
						      <option value='PM'>Priority Mail</option>
						      <option value='EX'>Priority Mail Express</option>
						      <option value='FC'>First Class</option>
						   </select>
					</div>
				</div>
		    </td>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>PackageType</b>:</label>
					<div class="controls">
						   <select id='packageType' name='packageType'>
						        <option value='PACKAGE'>PACKAGE</option>
						        <option value='FLAT RATE ENV'>FLAT RATE ENV</option>
						        <option value='LEGAL FLAT RATE ENV'>LEGAL FLAT RATE ENV</option>
								<option value='PADDED FLAT RATE ENV'>PADDED FLAT RATE ENV</option>
								<option value='SM FLAT RATE BOX'>SM FLAT RATE BOX</option>
								<option value='MD FLAT RATE BOX'>MD FLAT RATE BOX</option>
								<option value='LG FLAT RATE BOX'>LG FLAT RATE BOX</option>
								<option value='REGIONAL RATE BOX A'>REGIONAL RATE BOX A</option>
								<option value='REGIONAL RATE BOX B'>REGIONAL RATE BOX B</option>
						   </select>
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
		
		<blockquote>
			<p style="font-size: 14px">Tracking Info</p>
		</blockquote>
		<table>
		   <tr>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>Fee</b>:</label>
					<div class="controls">
						<b><span id='fee'></span></b>
					</div>
				</div>
		    </td>
		     <td>
				<div class="control-group">
					<label class="control-label"><b>ImageLabel</b>:</label>
					<div class="controls">
						<b><span id='imageLabel'><c:if test="${not empty mfnOrder.labelImage}"><a target="_blank" href="<c:url value='/${mfnOrder.labelImage}'/>">LabelImage</a></c:if></span></b>
					</div>
				</div>
		    </td>
		   </tr>
		</table>
		
		 <div class="form-actions">
			
			<c:if test="${empty mfnOrder.trackNumber}">
			   <input id="btnSubmit" class="btn btn-primary" type="button" value="Gen TrackingNumber"/>&nbsp;
			</c:if>
			<input id="feeView" class="btn btn-primary" type="button" value="View Fee"/>&nbsp;
			<c:if test="${not empty mfnOrder.trackingFlag&&'2' ne mfnOrder.trackingFlag&&'1' ne mfnOrder.trackingFlag}">
				<input id="cancelTracking" class="btn btn-primary" type="button" value="Cancel TrackingNumber"/>
			 </c:if>
			<!--  <input id="postageBalance" class="btn btn-primary" type="button" value="PostageBalance"/>&nbsp; -->
			 
	</form:form>
</body>
</html>
