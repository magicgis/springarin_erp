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
				var tbody=$("#contentTable tbody");
				var tr=$("<tr></tr>");
			    var option=" <option value=''>-请选择-</option>";
				<c:forEach items="${skuList}" var="item">
					 <c:if test="${'uk' eq amazonOutboundOrder.country}">
				        option+="<option asinVal='${item.asin }' powerVal='${powerMap[item.nameWithColor] }' fbaVal='${item.quantity }' localVal='${item.productId }' nameVal='${item.productName}'  colorVal='${item.color }' value='${item.sku}'>${item.nameWithColor}[${item.sku}]</option>	";
				     </c:if>
				     <c:if test="${'uk' ne amazonOutboundOrder.country}">
				        option+="<option asinVal='${item.asin }' fbaVal='${item.quantity }' localVal='${item.productId }' nameVal='${item.productName}'  colorVal='${item.color }' value='${item.sku}'>${item.nameWithColor}[${item.sku}]</option>	";
				     </c:if>
			    </c:forEach>  
			    tr.append("<td class='sku'> <input type='hidden' name='productName' class='productName' /> <input type='hidden' name='color' class='color' /> <select style='width: 90%' name='sellersku' onchange='setAsin(this);' class='sellersku'>"+option+"</select></td>");	
			    tr.append("<td><input type='text'  style='width: 80%'  name='asin' class='asin'  readonly /></td>");
			    tr.append("<td><input type='text'  style='width: 80%'  class='fbaQuantity'  readonly /></td>");
			    <c:if test="${fn:contains('de,fr,it,es,uk,com',amazonOutboundOrder.country)||fn:contains(amazonOutboundOrder.country,'com')||fn:contains(amazonOutboundOrder.country,'jp') }">
			        tr.append("<td><input type='text'  style='width: 80%'  class='localQuantity'  readonly /></td>");
			    </c:if>
			    tr.append("<td><input type='text' maxlength='10' style='width: 80%' name='quantityOrdered' class='digits required' value='1' class='quantityOrdered'/></td>");
			    tr.append("<td><a href='#' id='remove-row' class='remove-row'><span class='icon-minus'></span></a></td>");
			    tbody.append(tr);
				tr.find("select").select2();
			   
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
				rules:{
					"sellersku":{"required":true},
					"quantityOrdered":{"required":true}
				},
				messages:{
					"sellersku":{"required":'产品不能为空'},
					"quantityOrdered":{"required":'订单不能为空'}
				},
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
					
					if($("#eventType").val()=='5'||$("#eventType").val()=='7'||$("#eventType").val()=='13'||$("#eventType").val()=='14'||$("#eventType").val()=='12'){
						if($("#amazonOrderId").val()==null||$("#amazonOrderId").val()==''){
							 $.jBox.tip('替代订单号不能为空!');
							 return false;
						}
						if($("#amazonOrderId").val().match(/[\uff00-\uffff]/g)){
							$.jBox.tip("替代订单号不能输入全角字符，请切换到英文输入模式");
							return false;
						}
					}/* else{
						if($("#customerId").val()==null||$("#customerId").val()==''){
							 $.jBox.tip('评测customerId不能为空!');
							 return false;
						}
					} */
					
					if(!numberflag){
						top.$.jBox.error("<spring:message code='amazon_order_tips24'/>！","<spring:message code="sys_label_tips_error"/>");
					}else{
						if(flag){
							//top.$.jBox.confirm('Are you sure save?','<spring:message code="sys_label_tips_msg"/>',function(v,h,f){
							
							//if(v=='ok'){
									//loading('<spring:message code="sys_label_tips_submit"/>');
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
							//	}
							//},{buttonsFocus:1,persistent: true});
							//top.$('.jbox-body .jbox-icon').css('top','55px');
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
			
			 
			 
			
			  
			  $("#channel").change(function(){
				  window.location.href="${ctx}/amazoninfo/amazonTestOrReplace/add?country="+$("#channel").val();
			  });
			  
			  $("#accountName").change(function(){
				  window.location.href="${ctx}/amazoninfo/amazonTestOrReplace/add?country="+$("#channel").val()+"&accountName="+$("#accountName").val();
			  });
				
			  $("#eventType").change(function(){
				   if($("#eventType").val()=='5'||$("#eventType").val()=='7'||$("#eventType").val()=='12'||$("#eventType").val()=='13'||$("#eventType").val()=='14'||$("#eventType").val()=='15'){
					   $("#oldDiv").css("display","block");
				   }else{
					   $("#oldDiv").css("display","none");
				   }
				   if($("#eventType").val()=='15'){
					   $("#btnDiv").css("display","none");
				   }else{
					   $("#btnDiv").css("display","block");
				   }
			  });
			  
				 /* $("#btnSubmit").click(function(){
					 <c:if test="${fn:contains('de,fr,it,es,uk',amazonOutboundOrder.country) }">
					      var localFlag = true;
					      $(".localQuantity").each(function(){
					    	  var localStock=$(this).val();
					    	  if(localStock!=''){
					    		  var quantity=$(this).parent().next().children(":first").val();
					    		  if(parseInt(localStock)<parseInt(quantity)){
					    			  localFlag = false;
					    		  }
					    	  }else{
					    		  localFlag = false;
					    	  }
					      });
					      if(localFlag){
					    	  $.jBox.tip('Order can be fulfilled by Inateck. Please click Inateck Fulfillment button.');
					      }else{
					    	  $("#inputForm").submit();
					      }
					 </c:if>
					 <c:if test="${!fn:contains('de,fr,it,es,uk',amazonOutboundOrder.country) }">
					    $("#inputForm").submit();
					 </c:if>
				  */
				  
				 $("#btnSubmit").click(function(){
					 var localFlag = true;
					 $(".localQuantity").each(function(){
						 <c:if test="${'uk' eq  amazonOutboundOrder.country}">
				    	     var power=$(this).parent().parent().find("option:selected").attr("powerVal");
				    	     var sku = $(this).parent().parent().find("option:selected").val();
				    	     if(power=='1'&& sku.toLowerCase().indexOf('uk')<0){//带电
				    		     localFlag = false;
				    	     }
   	                     </c:if>
					 });
					 
					  /* if(localFlag){ */
						  var submit = function (v, h, f) {
							    if (v == 'ok'){
							    	  $("#inputForm").attr("action","${ctx}/amazoninfo/amazonTestOrReplace/nextSave");
									  $("#inputForm").submit();
									  $("#inputForm").attr("action","${ctx}/amazoninfo/amazonTestOrReplace/nextSave");
							    }else if (v == 'cancel'){
							    	top.$.jBox.tip(v, 'info');
							    }    
							    return true; //close
							};
							top.$.jBox.confirm("Order fulfilled by Amazon Fulfillment", "提示", submit); 
					 /*  }else{
						  $.jBox.tip('Please select uk sku!!!');
					  } */
				 });		 
					  
				 $("#localSave").click(function(){
					$("#inputForm").attr("action","${ctx}/amazoninfo/amazonTestOrReplace/localSave");
					$("#inputForm").submit();
					$("#inputForm").attr("action","${ctx}/amazoninfo/amazonTestOrReplace/nextSave");
			     });
				 
				 
				 $("#mfnSave").click(function(){
					  var localFlag = true;
					  
				      $(".localQuantity").each(function(){
				    	  var localStock=$(this).val();
				    	  if(localStock!=''){
				    		  var quantity=$(this).parent().next().children(":first").val();
				    		  if(parseInt(localStock)<parseInt(quantity)){
				    			  localFlag = false;
				    		  }
				    	  }else{
				    		  localFlag = false;
				    	  }
				    	  
				    	  <c:if test="${'uk' eq amazonOutboundOrder.country}">
					    	  var power=$(this).parent().parent().find("option:selected").attr("powerVal");
					    	  if(power=='1'){//带电
					    		  localFlag = false;
					    	  }
			    	      </c:if>
				    	  
				      });
				      
				      if(localFlag){
				    	  var submit = function (v, h, f) {
							    if (v == 'ok'){
							    	 $("#inputForm").attr("action","${ctx}/amazoninfo/amazonTestOrReplace/localSave2");
									  $("#inputForm").submit();
									  $("#inputForm").attr("action","${ctx}/amazoninfo/amazonTestOrReplace/nextSave");
							    }else if (v == 'cancel'){
							    	top.$.jBox.tip(v, 'info');
							    }    
							    return true; //close
							};
						  top.$.jBox.confirm("Order fulfilled by Inateck Fulfillment", "提示", submit); 
				    	 
				      }else{
				    	  $.jBox.tip('no stock or charged!!!');
				      } 
		        });
				 
				 $("#save").click(function(){
					 if($("#eventType").val()=='15'){
						 $("#inputForm").submit();
					 }else if($("#channel").val()=='fr'){
						 var fbaFlag = true;
						 $(".fbaQuantity").each(function(){
					    	  var fbaStock=$(this).val();
					    	  if(fbaStock!=''){
					    		  var quantity=$(this).parent().next().next().children(":first").val();
					    		  console.log(quantity);
					    		  if(parseInt(fbaStock)<parseInt(quantity)){
					    			  fbaFlag = false;
					    		  }
					    	  }else{
					    		  fbaFlag = false;
					    	  }
					      });
						  
						 if(fbaFlag){//有货
							    var submit = function (v, h, f) {
								    if (v == 'ok'){
								    	  $("#inputForm").attr("action","${ctx}/amazoninfo/amazonTestOrReplace/nextSave");
										  $("#inputForm").submit();
										  $("#inputForm").attr("action","${ctx}/amazoninfo/amazonTestOrReplace/nextSave");
								    }else if (v == 'cancel'){
								    	top.$.jBox.tip(v, 'info');
								    }    
								    return true; //close
								};
								top.$.jBox.confirm("Order fulfilled by Amazon Fulfillment", "提示", submit); 
						 }else{//缺货
							 var submit = function (v, h, f) {
								    if (v == 'ok'){
								    	  $("#inputForm").attr("action","${ctx}/amazoninfo/amazonTestOrReplace/localSave2");
										  $("#inputForm").submit();
										  $("#inputForm").attr("action","${ctx}/amazoninfo/amazonTestOrReplace/nextSave");
								    }else if (v == 'cancel'){
								    	top.$.jBox.tip(v, 'info');
								    }    
								    return true; //close
								};
								top.$.jBox.confirm("Order fulfilled by Inateck Fulfillment", "提示", submit);
						 }
					 }else{
						 <c:if test="${fn:contains('de,fr,it,es,uk',amazonOutboundOrder.country) }">
						 var localFlag = true;
						 $(".localQuantity").each(function(){
					    	  var localStock=$(this).val();
					    	  if(localStock!=''){
					    		  var quantity=$(this).parent().next().children(":first").val();
					    		  if(parseInt(localStock)<parseInt(quantity)){
					    			  localFlag = false;
					    		  }
					    	  }else{
					    		  localFlag = false;
					    	  }
					    	  <c:if test="${'uk' eq amazonOutboundOrder.country}">
						    	  var power=$(this).parent().parent().find("option:selected").attr("powerVal");
						    	  if(power=='1'){//带电
						    		  localFlag = false;
						    	  }
					    	  </c:if>
					      });
						 if(localFlag){//有货&英国不带电
							    var submit = function (v, h, f) {
								    if (v == 'ok'){
								    	  $("#inputForm").attr("action","${ctx}/amazoninfo/amazonTestOrReplace/localSave2");
										  $("#inputForm").submit();
										  $("#inputForm").attr("action","${ctx}/amazoninfo/amazonTestOrReplace/nextSave");
								    }else if (v == 'cancel'){
								    	top.$.jBox.tip(v, 'info');
								    }    
								    return true; //close
								};
								top.$.jBox.confirm("Order fulfilled by Inateck Fulfillment", "提示", submit); 
						 }else{
							 var submit = function (v, h, f) {
								    if (v == 'ok'){
								    	  $("#inputForm").attr("action","${ctx}/amazoninfo/amazonTestOrReplace/nextSave");
										  $("#inputForm").submit();
										  $("#inputForm").attr("action","${ctx}/amazoninfo/amazonTestOrReplace/nextSave");
								    }else if (v == 'cancel'){
								    	top.$.jBox.tip(v, 'info');
								    }    
								    return true; //close
								};
								top.$.jBox.confirm("Order fulfilled by Amazon Fulfillment", "提示", submit);
						 }
						 </c:if>
						 <c:if test="${!fn:contains('de,fr,it,es,uk',amazonOutboundOrder.country) }">
						     $("#inputForm").submit();
						 </c:if>
					 } 
					 
				 });
				 
				 $("#searchByName").click(function(){
						var name=$("input[name='shippingAddress.name']").val();
						$("input[name='shippingAddress.addressLine1']").val('');
						$("input[name='shippingAddress.addressLine2']").val('');
						$("input[name='shippingAddress.addressLine3']").val('');
						$("input[name='shippingAddress.city']").val('');
						$("input[name='shippingAddress.country']").val('');
						$("input[name='shippingAddress.stateOrRegion']").val('');
						$("input[name='shippingAddress.countryCode']").val('');
						$("input[name='shippingAddress.postalCode']").val('');
						$("input[name='shippingAddress.phone']").val('');
						if(name==''){
							return;
						}else{
							$.post("${ctx}/amazoninfo/amazonTestOrReplace/getAddrInfo",{addrName:name},function(date){
								if(date!=null){
									 $("input[name='shippingAddress.addressLine1']").val(date.addressLine1);
										$("input[name='shippingAddress.addressLine2']").val(date.addressLine2);
										$("input[name='shippingAddress.addressLine3']").val(date.addressLine3);
										$("input[name='shippingAddress.city']").val(date.city);
										$("input[name='shippingAddress.country']").val(date.country);
										$("input[name='shippingAddress.stateOrRegion']").val(date.stateOrRegion);
										$("input[name='shippingAddress.countryCode']").val(date.countryCode);
										$("input[name='shippingAddress.postalCode']").val(date.postalCode);
										$("input[name='shippingAddress.phone']").val(date.phone);
										$("#customerId").val(date.customId);
										$("#buyerEmail").val(date.buyerEmail);
										$("#buyerName").val(date.buyerName);
								}
						  });
						}
					});
				 
				 $(window).load(function() {
					 $(".fbaQuantity").each(function(){
				    	   if($(this).val()==''){
				    		 var asin=$(this).parent().parent().find("option:selected").attr("asinVal");
				  			 var name=$(this).parent().parent().find("option:selected").attr("nameVal");
				  			 var color=$(this).parent().parent().find("option:selected").attr("colorVal");
				  			 var fba=$(this).parent().parent().find("option:selected").attr("fbaVal");
				  			 var localStock=$(this).parent().parent().find("option:selected").attr("localVal");
				  			$(this).parent().parent().find("input[name='asin']").val(asin);
				  			$(this).parent().parent().find("input[name='productName']").val(name);
				  			$(this).parent().parent().find("input[name='color']").val(color);
				  			$(this).parent().parent().find(".fbaQuantity").val(fba);
				  			$(this).parent().parent().find(".localQuantity").val(localStock);
				    	   }
				     });
			     });
		});
		
		function findOrderInfo(){
			 var url='';
			 if($("#eventType").val()=='13'||$("#eventType").val()=='14'){
				 url='${ctx}/amazoninfo/amazonTestOrReplace/findOrderInfo2';
			 }else if($("#eventType").val()=='12'){
				 url='${ctx}/amazoninfo/amazonTestOrReplace/findOrderInfo3';
			 }else{
				 if($("#amazonOrderId").val()!=null&&$("#amazonOrderId").val()!=''&&$("#amazonOrderId").val().split("-").length==2){
					 url='${ctx}/amazoninfo/amazonTestOrReplace/findOrderInfo3';
				 }else{
					 url='${ctx}/amazoninfo/amazonTestOrReplace/findOrderInfo';
				 }
				
			 }
			 if($("#amazonOrderId").val()!=null&&$("#amazonOrderId").val()!=''&&$("#channel").val()!=''){
				  $.ajax({  
				        type : 'POST', 
				        url : url,  
				        dataType:"json",
				        data : 'amazonOrderId=' +$("#amazonOrderId").val()+'&country='+$("#channel").val(),  
				        async: false,
				        success : function(msg){ 
					        	$("#buyerName").val(msg.buyerUser);
					        	$("#buyerEmail").val(msg.buyerUserEmail);
					        	$("#customerId").val(msg.customerId);
					        	$("input[name='shippingAddress.addressLine1']").val('');
				        		$("input[name='shippingAddress.addressLine2']").val('');
				        		$("input[name='shippingAddress.addressLine3']").val('');
				        		$("input[name='shippingAddress.city']").val('');
								$("input[name='shippingAddress.country']").val('');
								$("input[name='shippingAddress.stateOrRegion']").val('');
								$("input[name='shippingAddress.countryCode']").val('');
								$("input[name='shippingAddress.postalCode']").val('');
								$("input[name='shippingAddress.phone']").val('');
								$("input[name='shippingAddress.name']").val('');
							
								if(msg.errorMsg!=null&&msg.errorMsg!=''){
									 $.jBox.tip(msg.errorMsg);
									 return false;
								}
								
								var streetInfo=msg.street;
				        	    if(streetInfo==null||streetInfo==''){
				        	    	streetInfo=msg.street1;
				        	    }
				        	    if(streetInfo==null||streetInfo==''){
				        	    	streetInfo=msg.street2;
				        	    }
				        		$("input[name='shippingAddress.addressLine1']").val(streetInfo);
				        		$("input[name='shippingAddress.addressLine2']").val(msg.street1);
				        		$("input[name='shippingAddress.addressLine3']").val(msg.street2);
				        		$("input[name='shippingAddress.city']").val(msg.cityName);
								$("input[name='shippingAddress.country']").val(msg.country);
								$("input[name='shippingAddress.stateOrRegion']").val(msg.stateOrProvince);
								$("input[name='shippingAddress.countryCode']").val(msg.countryCode);
								$("input[name='shippingAddress.postalCode']").val(msg.postalCode);
								$("input[name='shippingAddress.phone']").val(msg.phone);
								$("input[name='shippingAddress.name']").val(msg.name);
								
								if($("#eventType").val()!='12'){
									$("input[name='asin']").val(msg.asin);
									$("input[name='productName']").val(msg.productName);
									$("input[name='color']").val(msg.color);
									$("input[class='fbaQuantity']").val(msg.quantity);
									$("input[class='localQuantity']").val(msg.localQuantity);
									$("select[name='sellersku']").select2("val",msg.sku);
				                }	
								
								
				        }
				  }); 
			  }else{
				  $.jBox.tip('订单号或国家不能为空');
			  }
		}
		 
		 function setAsin(c){
			 var asin=$(c).find("option:selected").attr("asinVal");
			 var name=$(c).find("option:selected").attr("nameVal");
			 var color=$(c).find("option:selected").attr("colorVal");
			 var fba=$(c).find("option:selected").attr("fbaVal");
			 var localStock=$(c).find("option:selected").attr("localVal");
			// var country=$("#channel").val();
			 
			 $(c).parent().parent().find("input[name='asin']").val(asin);
			 $(c).parent().parent().find("input[name='productName']").val(name);
			 $(c).parent().parent().find("input[name='color']").val(color);
			 $(c).parent().parent().find(".fbaQuantity").val(fba);
			 $(c).parent().parent().find(".localQuantity").val(localStock);
		 }
		
	</script>
</head>
<body>
 <ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li><a class="countryHref" href="${ctx}/amazoninfo/amazonTestOrReplace/?country=${dic.value}" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
		<li  class='active'><a class="countryHref" href="${ctx}/amazoninfo/amazonTestOrReplace/add" key="${dic.value}">Order Add</a></li>	
	</ul>
<br/>
	<tags:message content="${message}"/>
	<c:if test="${not empty errorMsg}">
	  <div class="alert alert-info">${errorMsg }</div>
	</c:if>
	<form:form id="inputForm" modelAttribute="amazonOutboundOrder"  action="${ctx}/amazoninfo/amazonTestOrReplace/nextSave" method="post" class="form-horizontal">
	    <form:hidden path="id" id="id"/>
	    <form:hidden path='sellerOrderId'/>
	    <blockquote>
			<p style="font-size: 14px">Country</p>
		</blockquote>
		<table>
		   <tr>
		      <td>
				<div class="control-group">
					<label class="control-label"><b>Country</b>:</label>
					<%-- <div class="controls">
						<form:select path="country" style="width: 220px" class="required" id="channel">
							 <option value="" selected="selected">-请选择平台-</option>
							 <c:forEach items="${countrySet}" var="roleCountry">
						         <option  value="${roleCountry}" ${amazonOutboundOrder.country eq roleCountry?'selected':''}>${fns:getDictLabel(roleCountry,'platform','')}</option>									
					         </c:forEach>
						</form:select>
					</div> --%>
					<div class="controls">
								<form:select path="country" style="width: 220px" class="required" id="channel">
									<option value="" selected="selected">-请选择平台-</option>
									<shiro:hasPermission name="amazoninfo:fulfillment:all">
										<c:forEach items="${fns:getDictList('platform')}" var="dic">
											<c:if test="${dic.value ne 'com.unitek'}">
												<option value="${dic.value}" >${dic.label}</option>
											</c:if>
										</c:forEach>
									</shiro:hasPermission>
									<shiro:lacksPermission name="amazoninfo:fulfillment:all">
										<shiro:hasPermission name="amazoninfo:fulfillment:de">
											<option value="de" >德国|DE</option>
										</shiro:hasPermission>
										<shiro:hasAnyPermissions name="amazoninfo:fulfillment:com">
											<option value="com" >美国|US</option>
										</shiro:hasAnyPermissions>
										<shiro:hasPermission name="amazoninfo:fulfillment:fr">
											<option value="fr" >法国|FR</option>
										</shiro:hasPermission> 
										<shiro:hasPermission name="amazoninfo:fulfillment:jp">
											<option value="jp" >日本|JP</option>
										</shiro:hasPermission>
									    <shiro:hasPermission name="amazoninfo:fulfillment:es">
											<option value="es" >西班牙|ES</option>
										</shiro:hasPermission> 
										<shiro:hasPermission name="amazoninfo:fulfillment:it">
											<option value="it" >意大利|IT</option>
										</shiro:hasPermission>
										<shiro:hasPermission name="amazoninfo:fulfillment:uk">
											<option value="uk" >英国|UK</option>
										</shiro:hasPermission>
										<shiro:hasPermission name="amazoninfo:fulfillment:ca">
											<option value="ca" >加拿大|CA</option>
										</shiro:hasPermission>
										<shiro:hasPermission name="amazoninfo:fulfillment:mx">
											<option value="mx" >墨西哥|MX</option>
										</shiro:hasPermission>
									</shiro:lacksPermission>
								</form:select>
								<script type="text/javascript">
									$("option[value='${amazonOutboundOrder.country}']").attr("selected","selected");				
								</script>
					</div>
				</div>	
				
				
				
		  </td>
		  <td>
		  
		    
				<div class="control-group">
					<label class="control-label"><b>AccountName</b>:</label>
					<div class="controls">
						<form:select path="accountName" style="width: 220px" class="required" id="accountName">
							 <c:forEach items="${accountNameList}" var="accountName">
						         <option  value="${accountName}" ${amazonOutboundOrder.accountName eq accountName?'selected':''}>${accountName}</option>									
					         </c:forEach>
						</form:select>
					</div>
				</div>	
		  </td>
		<%--   <c:if test="${'com' eq  amazonOutboundOrder.country}">
		        <td>
					<div class="control-group">
						<label class="control-label"><b>AmazonOrEbay</b>:</label>
						<div class="controls">
							<form:select path="amazonOrEbay" style="width: 220px" class="required" id="amazonOrEbay">
								 <option value="Amazon" selected="selected">Amazon</option>
	                             <option value="Ebay" >Ebay</option>
							</form:select>
						</div>
					</div>	
		      </td>
		  </c:if>
		  --%>
		   </tr>
		</table>
		
	    <blockquote>
			<p style="font-size: 14px">Order Info</p>
		</blockquote>
		<table>
		<tr>
		 <td>
				<div class="control-group">
					<label class="control-label"><b>Order Type</b>:</label>
					<div class="controls">
						<form:select path="eventType" style="width: 220px" class="required" id="eventType">
						      <option value="5" ${amazonOutboundOrder.eventType eq '5'?'selected':''}>Support</option>
						      <option value="7" ${amazonOutboundOrder.eventType eq '7'?'selected':''}>Support_Voucher</option>
						      <option value="8" ${amazonOutboundOrder.eventType eq '8'?'selected':''}>Marketing</option>
							 
							<%--   <c:if test="${'com' eq  amazonOutboundOrder.country}"> --%>
							     <option value="12" ${amazonOutboundOrder.eventType eq '12'?'selected':''}>Ebay Order</option>
							 <%--  </c:if>    --%>
							  <option value="13" ${amazonOutboundOrder.eventType eq '13'?'selected':''}>Website SupportOrder</option>
							  <option value="14" ${amazonOutboundOrder.eventType eq '14'?'selected':''}>Offline SupportOrder</option>
							  <option value="15" ${amazonOutboundOrder.eventType eq '15'?'selected':''}>AmzMfn</option>
						</form:select>
					</div>
				</div>	
				
		  </td>
		  <td>
				<div class="control-group">
					<label class="control-label"><b>shippingSpeedCategory</b>:</label>
					<div class="controls">
						<form:select path="shippingSpeedCategory" style="width: 220px" class="required" id="shippingSpeedCategory">
							<option value="Standard" ${amazonOutboundOrder.shippingSpeedCategory eq 'Standard'?'selected':''}>Standard</option>
							<option value="Expedited" ${amazonOutboundOrder.shippingSpeedCategory eq 'Expedited'?'selected':''}>Expedited</option>
						</form:select>
					</div>
		     </div>
		   </td>
		   
		 <td >
				<div class="control-group"  id='oldDiv'>
					<label class="control-label"><b>Original OrderId</b>:</label>
					<div class="controls">
						<form:input maxlength="255"  path="amazonOrderId" id="amazonOrderId"  onblur='findOrderInfo();' value='${empty amazonOutboundOrder.amazonOrderId?amazonOutboundOrder.oldOrderId:amazonOutboundOrder.amazonOrderId}'/>
						<input id="searchByOrderId" class="btn btn-small" type="button" value="Search" onclick='findOrderInfo();' />
					</div>
				</div>	
				
		  </td>
		  
		</tr>
		 <td>
				<div class="control-group">
					<label class="control-label"><b>CustomId</b>:</label>
					<div class="controls">
						<form:input maxlength="255"  path="customId" id="customerId"  />
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
				<form:input maxlength="255" class="required" path="buyerName" id="buyerName"  />
			</div>
		</div>
		</td><td>
		<div class="control-group">
			<label class="control-label"><spring:message code="amazon_order_form10"/>:</label>
			<div class="controls">
				<form:input maxlength="255" class="email" path="buyerEmail" id="buyerEmail"/>
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
										<form:input maxlength="255" class="required" path="shippingAddress.name" id="shippingAddressName"/>
										<input id="searchByName" class="btn btn-small" type="button" value="Search"/>
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
								<label class="control-label"><b><spring:message code="amazon_order_form15"/></b>:</label>
								<div class="controls">
									<form:input maxlength="255" class="required" path="shippingAddress.stateOrRegion" id="shippingAddress.stateOrRegion" />
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
			   		 	<td width="80px">  
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
			<p style="font-size: 14px">Other</p>
		</blockquote>
			<table>
		<tr>
		  <td colspan="2">
		   <div class="control-group">
			<label class="control-label">DisplayableOrderComment:</label>
			<div class="controls">
				<form:textarea path="displayableOrderComment" class='required'  htmlEscape="false" maxlength="200" style="margin: 0px; width: 600px; height:50px;"/>
			</div>
		  </div>
		  </td>
		</tr>
		<tr>
		  <td colspan="2">
		   <div class="control-group">
			<label class="control-label">Remark:</label>
			<div class="controls">
				<form:textarea path="remark" class='required'  htmlEscape="false" maxlength="1000" style="margin: 0px; width: 600px; height: 100px;"/>
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
				   <th style="width: 12%">Asin</th>
				   <th style="width: 12%">FBA库存</th> 
				   <c:if test="${fn:contains('de,fr,it,es,uk',amazonOutboundOrder.country) }">
				     <th style="width: 12%"><spring:message code="psi_inventory_DE_stock"/></th> 
				   </c:if>
				   <c:if test="${fn:contains(amazonOutboundOrder.country,'com')}">
				     <th style="width: 12%"><spring:message code="psi_inventory_US_stock"/></th> 
				   </c:if>
				    <c:if test="${fn:contains(amazonOutboundOrder.country,'jp')}">
				     <th style="width: 12%"><spring:message code="psi_inventory_JP_stock"/></th> 
				   </c:if>
				   <th style="width: 12%"><spring:message code="amazon_order_form23"/></th>
				   <th style="width: 6%"><spring:message code="sys_label_tips_operate"/></th>
			</tr>
		</thead>
		<tbody>
		   <c:if test="${not empty amazonOutboundOrder&&not empty amazonOutboundOrder.items}">
		           <c:forEach items="${amazonOutboundOrder.items}" var="temp">
						<tr>
							<td class="sku">
							     <input type="hidden" name="id" value="${temp.id}" />
								 <input type='hidden' name='productName' class='productName' value='${temp.productName }'/>
								 <input type='hidden' name='color' class='color'  value='${temp.color }'/>
								 <select style="width: 90%" name="sellersku" onchange='setAsin(this);' class='sellersku'>	
								    <option value="">-请选择-</option>				
									<c:forEach items="${skuList}" var="item">
									   <c:if test="${'uk' eq amazonOutboundOrder.country}">
								           <option  ${temp.sellersku eq item.sku?'selected':''}  powerVal='${powerMap[item.nameWithColor] }'  localVal='${item.productId }' asinVal='${item.asin }' fbaVal='${item.quantity }' nameVal='${item.productName}'  colorVal='${item.color }' value="${item.sku}">${item.nameWithColor}[${item.sku}]</option>	
								       </c:if>
									   <c:if test="${'uk' ne amazonOutboundOrder.country}">
									       <option  ${temp.sellersku eq item.sku?'selected':''}  localVal='${item.productId }' asinVal='${item.asin }' fbaVal='${item.quantity }' nameVal='${item.productName}'  colorVal='${item.color }' value="${item.sku}">${item.nameWithColor}[${item.sku}]</option>	
						                </c:if>
									</c:forEach>
								</select>
							</td>
								<td><input type="text"  style="width: 80%"  name='asin' class='asin'  value='${temp.asin }' readonly /></td>
								<td><input type="text"  style="width: 80%"  class='fbaQuantity'  readonly /></td>
								<c:if test="${fn:contains('de,fr,it,es,uk,com',amazonOutboundOrder.country)||fn:contains(amazonOutboundOrder.country,'com')||fn:contains(amazonOutboundOrder.country,'jp') }">
							       <td><input type="text"  style="width: 80%"  class='localQuantity'  readonly /></td> 
							    </c:if>
								<td><input type="text" maxlength="10" style="width: 80%" name="quantityOrdered" value='${temp.quantityOrdered }' class="digits required"  class='quantityOrdered'/>
								</td>
								<td><a href="#" id="remove-row" class="remove-row"><span class="icon-minus"></span></a></td>
						</tr>
		         </c:forEach>
		   </c:if>
		    <c:if test="${empty amazonOutboundOrder||empty amazonOutboundOrder.items}">
		       <tr>
				<td class="sku">
				 <input type='hidden' name='productName' class='productName' />
				 <input type='hidden' name='color' class='color' />
				 <select style="width: 90%" name="sellersku" onchange='setAsin(this);' class='sellersku'>	
				    <option value="">-请选择-</option>				
					<c:forEach items="${skuList}" var="item">
					   <c:if test="${'uk' eq amazonOutboundOrder.country}">
					       <option  powerVal='${powerMap[item.nameWithColor] }'  asinVal='${item.asin }' fbaVal='${item.quantity }' localVal='${item.productId }' nameVal='${item.productName}'  colorVal='${item.color }' value="${item.sku}">${item.nameWithColor}[${item.sku}]</option>
					   </c:if>
					   <c:if test="${'uk' ne amazonOutboundOrder.country}">
					       <option asinVal='${item.asin }' fbaVal='${item.quantity }' localVal='${item.productId }' nameVal='${item.productName}'  colorVal='${item.color }' value="${item.sku}">${item.nameWithColor}[${item.sku}]</option>
					   </c:if>
					</c:forEach>
				</select></td>
				<td><input type="text"  style="width: 80%"  name='asin' class='asin'  readonly /></td>
				<td><input type="text"  style="width: 80%"  class='fbaQuantity'  readonly /></td>
				<c:if test="${fn:contains('de,fr,it,es,uk,com',amazonOutboundOrder.country)||fn:contains(amazonOutboundOrder.country,'com')||fn:contains(amazonOutboundOrder.country,'jp') }">
					<td><input type="text"  style="width: 80%"  class='localQuantity'  readonly /></td> 
				</c:if>
				<td><input type="text" maxlength="10" style="width: 80%" name="quantityOrdered" class="digits required" value="1" class='quantityOrdered'/>
				</td>
				<td><a href="#" id="remove-row" class="remove-row"><span class="icon-minus"></span></a></td>
			  </tr>
		   </c:if>
			
		</tbody>
	</table>
		 <div class="form-actions">
		          <shiro:hasPermission name="amazoninfo:fulfillment:${amazonOutboundOrder.country}">
		            <input id="btnSubmit" class="btn btn-primary" type="button" value="AMZ Fulfillment"/>&nbsp;&nbsp;&nbsp;
		          </shiro:hasPermission>
			 
			<!-- <input id="btnSubmit" class="btn btn-primary" type="button" value="AMZ Fulfillment"/>&nbsp;&nbsp;&nbsp; -->
			<c:if test="${fn:contains('de,fr,it,es,uk,com',amazonOutboundOrder.country)||fn:contains(amazonOutboundOrder.country,'com')||fn:contains(amazonOutboundOrder.country,'jp')}">
				<input id="mfnSave" class="btn btn-primary" type="button" value="Inateck Fulfillment"/>&nbsp;&nbsp;&nbsp;
			</c:if> 
			<!-- <input id="save" class="btn btn-primary" type="button" value="Fulfillment"/> -->
			<c:if test="${'AmzMfn' ne amazonOutboundOrder.orderType}"><span id='btnDiv'>&nbsp;&nbsp;&nbsp;<input id="localSave" class="btn btn-primary" type="button" value="Save without Upload"/></span></c:if>
			&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="<spring:message code="sys_but_back"/>" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
