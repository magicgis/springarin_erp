<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Event Edit</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<meta name="decorator" content="default"/>
	
	<script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script>
	
	<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
	
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
	
		$(document).ready(function() {
			
			eval('var productMapJson=${productMapJson}');
			eval('var problemMapJson=${problemMapJson}');
			
			CKEDITOR.replace("description",{height:'280px',toolbarStartupExpanded:false,startupFocus:true});
					
			$("#in").change(function(){
				$("option:contains('<spring:message code='custom_event_note5'/>')").prop("value",$("#in").val());
			});
			
			initSelect2($("#accountName").val());
			
			$(".isCheck").on("click",function(){
				if(this.checked){
					$(".isCheck").val("1");
				}else{
					$(".isCheck").val("0");
				}
			});
			
			$("#accountName").on("change",function(e){
				$("#country").val($("#accountName").find("option:selected").attr("countryVal"));
				if($("#eventType").val() == 10){
					if($("#country").val() == "com"){
						$("#masterBy").val("80ec26b6be17462a813dffd775ab3102").trigger("change");//默认负责人为adam
						$(".isCheck").attr('checked', true);
						$(".isCheck").attr('value', '1');
					} else {
						$("#masterBy").val("cf71fba07d494df88581aa2b3edb8dd2").trigger("change");//欧洲默认负责人为Sebastian Thurm
						$(".isCheck").removeAttr("checked");
						$(".isCheck").attr('value', '0');
					}
				}
				initSelect2(e.val);
			});
			
			<c:if test="${event.id==null}">
				$("#masterBy").val('${supUser.id}').trigger("change");
			</c:if>
			
			if($("#eventType").val() == 10){	//召回事件初始化表单信息
				var accountName = '${event.accountName}';
				if(accountName == null || accountName == ""){
					accountName = "Inateck_US";
				}
				$("#accountName").val(accountName).trigger("change");
				initSelect2(accountName);
				$("#productQuantityDiv").css('display','block');
				$(".notRecall").css('display','none');
				//$("#country").attr('disabled',true);
				$("#eventType").attr('disabled',true);
				if(accountName){
					$("#masterBy").val("80ec26b6be17462a813dffd775ab3102").trigger("change");//默认负责人为adam
				} else {
					$("#masterBy").val("cf71fba07d494df88581aa2b3edb8dd2").trigger("change");//欧洲默认负责人为Sebastian Thurm
				}
			} else {
				$("#productQuantityDiv").css('display','none');
				$(".notRecall").css('display','block');
			}
			
			if($("#eventType").val()==4){
				$("#taxIdDiv").css('display','block');
			}else{
				$("#taxIdDiv").css('display','none');
			}
			
			if($("#eventType").val()==10){
				$("#isShipToChina").css('display','block');
			}else{
				$("#isShipToChina").css('display','none');
			}
			
			if($("#eventType").val()==11){
				$("#refundTypeDiv").css('display','block');
			}else{
				$("#refundTypeDiv").css('display','none');
			}
			
			if($("#eventType").val()==8){
				$("#name1").css('display','none');
				$("#name2").css('display','block');
			}else{
				$("#name1").css('display','block');
				$("#name2").css('display','none');
			}
			
			$("#refundType").on("change",function(e){
				if(e.val==1){
					<c:if test="${empty event.id}">
						$("#totalPriceDiv").css('display','block');
						$("#cardNumberDiv").css('display','none');
					</c:if>
					//设置产品价格
					var text = $("#asin").find("option:selected").text();	//当前选中的值
					var temp = text.split("[")[1];
					var sku = temp.split("]")[0];
					var rs = "";
					$.ajax({
		      			   type: "POST",
		      			   url: "${ctx}/amazoninfo/priceFeed/getPrice?country="+$("#country").val()+"&sku="+sku,
		      			   async: false,
		      			   success: function(msg){
		      				   rs = msg;		
		      			   }
		          	});
					if(rs.salePrice){
						$("#totalPrice").val(rs.salePrice);
					} else {
						$("#totalPrice").val(rs.price);
					}
				} else {
					<c:if test="${empty event.id}">
						$("#totalPriceDiv").css('display','none');
						$("#cardNumberDiv").css('display','block');
					</c:if>
				}
			});
			
			//Review Refund事件gift card方式自动显示产品价格
			$("#asin").on("change",function(e){
				if($("#eventType").val() == 11 && $("#refundType").val() == 1){
					//设置产品价格
					var text = $(this).find("option:selected").text();	//当前选中的值
					var temp = text.split("[")[1];
					var sku = temp.split("]")[0];
					var rs = "";
					$.ajax({
		      			   type: "POST",
		      			   url: "${ctx}/amazoninfo/priceFeed/getPrice?country="+$("#country").val()+"&sku="+sku,
		      			   async: false,
		      			   success: function(msg){
		      				   rs = msg;		
		      			   }
		          	});
					if(rs.salePrice){
						$("#totalPrice").val(rs.salePrice);
					} else {
						$("#totalPrice").val(rs.price);
					}
				}
			});
			
			$("#eventType").on("change",function(e){
				if(e.val==5){
					<c:if test="${supUser!=null}">
						$("#masterBy").val('${supUser.id}').trigger("change");
					</c:if>
				}else if(e.val==4){
					<c:if test="${taxUser!=null}">
						$("#masterBy").val('${taxUser.id}').trigger("change");
					</c:if>
				}
				if(e.val==10){	//召回事件暂时只针对美国
					$("#accountName").val("Inateck_US").trigger("change");
					$("#country").val("com");
					initSelect2("Inateck_US");
					$("#productQuantityDiv").css('display','block');
					$(".notRecall").css('display','none');
					//$("#country").attr('disabled',true);
					$("#masterBy").val("80ec26b6be17462a813dffd775ab3102").trigger("change");
					$("#isShipToChina").css('display','block');
					$(".isCheck").attr('checked', true);
				}else {
					$("#productQuantityDiv").css('display','none');
					$(".notRecall").css('display','block');
					$("#country").attr('disabled',false);
					$("#isShipToChina").css('display','none');
				}
				
				if(e.val==4){
					$("#taxIdDiv").css('display','block');
				}else{
					$("#taxIdDiv").css('display','none');
				}
				
				if(e.val==11){
					$("#refundTypeDiv").css('display','block');
					$("#masterBy").val("8").trigger("change");//默认负责人为ring
					//默认为gift card方式
					$("#totalPriceDiv").css('display','block');
					$("#cardNumberDiv").css('display','none');
				}else{
					$("#refundTypeDiv").css('display','none');
				}
				
				if(e.val==8){
					$("#name1").css('display','none');
					$("#name2").css('display','block');
				}else{
					$("#name1").css('display','block');
					$("#name2").css('display','none');
				}
			});
			
			$("#asin").on("select2-selecting",function(e){
				if(e.object.element[0].label=="<spring:message code='custom_event_note5'/>"){
					$("#spanIn").show();
				}else{
					$("#spanIn").hide();
				}
			});
			
			$("#asin").change(function(){
				//michael star
				var productName=$(this).find("option:selected").text().split("[")[0];
				var optStr="<option value=''></option>";
				if(productName!=''){
					var type = productMapJson[productName];
					if(type!=''){
						var problems = problemMapJson[type];
						for(var i in problems){
							if(problems[i]=='${event.problemType}'){
								optStr=optStr+"<option value='"+problems[i]+"' selected >"+problems[i]+"</option>";
							}else{
								optStr=optStr+"<option value='"+problems[i]+"'>"+problems[i]+"</option>";
							}
						}
					}else{
						optStr=optStr+"<option value='other'>other</option>";
					}
					$("#productName").val(productName);
				}
				$("#problemType").empty();
				$("#problemType").append(optStr).select2().change();
				//michael end
			});
			
			$("#inputForm").validate({
				submitHandler: function(form){
					$("#country").removeAttr("disabled");
					$("#eventType").removeAttr("disabled");
					var productName=$("#asin").find("option:selected").text().split("[")[0];
					if($("#eventType").val()=="4"&&$("#id").val()==''&&$("#isEmailTax").val()=='0'){
						if($("#invoiceNumber").val()==''){
							$.jBox.tip('订单号不能为空');
							return false;
						}
						
						$.ajax({
			      			   type: "POST",
			      			   url: "${ctx}/custom/event/isExistEvent?orderId="+$("#invoiceNumber").val(),
			      			   async: true,
			      			   success: function(msg){
			      				  if(msg=="0"){
			      					$.jBox.tip('退税订单号已存在');
			    					return false;
			      				  }else if(msg=="1"){
			      					$("#titleQuery").attr("disabled","disabled");
			      					top.$.jBox.tip("Loading...", 'loading',{timeout:10000});
			      					$.ajax({  
			      				        type : 'POST', 
			      				        url : '${ctx}/custom/event/titleByName',  
			      				        dataType:"json",
			      				        data : "country="+$("#accountName").val()+"&orderId="+$("#invoiceNumber").val(),  
			      				        async: true,
			      				        success : function(msg){
			      				        	    $("#titleQuery").removeAttr("disabled");
			      					        	var params = {};
			      								params.id = '${event.id}'; 
			      								var refundBillHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' ><table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tr><th style='width:50px'>Name</th><th style='width:300px'>Title</th></tr></thead><tbody>";
			      								var descHtml="";
			      								for(var p in msg){
			      									var uri_encoded = html_decode(html_decode(html_decode(html_decode(msg[p]))));
			      						        	encoded =htmlDecode(uri_encoded);
			      						        	descHtml=descHtml+"<tr><td class='pname'>"+p+"</td><td><textarea class='ptitle' name='quantity'  style='width:90%;'>"+encoded+"</textarea></td></tr>";
			      								}
			      								if(descHtml==''){
			      									descHtml=descHtml+"<tr><td class='pname'>"+productName+"</td><td><textarea class='ptitle' name='quantity'  style='width:90%;'>"+productName+"</textarea></td></tr>";
			      								}
			      								refundBillHtml+=descHtml;
			      								refundBillHtml=refundBillHtml+"</tbody></table></div>";
			      								
			      								var submitChild = function (v, h, f) {
			      									var isChecked =false;
			      									h.find("textarea[name='quantity']").each(function(){
			      										if($(this).val()!=''){
			      											isChecked=true;
			      											return ;
			      										};
			      										
			      									});
			      									
			      									if(!isChecked){
			      										top.$.jBox.tip("please input title！", 'info',{timeout:3000});
			      										return false;
			      									}
			      									
			      									var ids="";
			      									var quantitys="";
			      									h.find("textarea[name='quantity']").each(function(){
			      										var quantity=$(this).val();
			      											if(quantity!=''){
			      												var tr  =$(this).parent().parent();
			      												var  oldQuantity = tr.find(".ptitle").val();
			      												var id      =tr.find(".pname").text();
			      												ids=ids+id+",";
			      												quantitys=quantitys+quantity+",";
			      											};
			      										
			      									});
			      									$("#nameId").val(ids);
			    									$("#titleId").val(quantitys);
			    									form.submit();
			    									return true;
			      							  };
			      							  $.jBox(refundBillHtml, { title: "Confirm description",width:600,submit: submitChild,persistent: true});		
			      				        		
			      				        }
			      				  });
			      					
			      			   }
			      			}
			          });
					}else if($("#eventType").val()=="11" && $("#id").val()==''){
						var productName = $("#asin").find("option:selected").text().split("[")[0];
						if(productName == null || productName == ""){
	      					$.jBox.tip('请选择产品');
	    					return false;
						}
						$.ajax({
		      			   type: "POST",
		      			   url: "${ctx}/custom/event/isBeyond?productName="+productName,
		      			   async: true,
		      			   success: function(msg){
		      				  if(msg=="0"){
		      					$.jBox.tip('该产品Review Refund事件已达到15个,不能再创建Review Refund事件');
		    					return false;
		      				  }else if(msg=="1"){
								form.submit();
  									return true;
		      			   	  }
		      			  }
			          });
					}else if($("#eventType").val()=="8"){
						    if($("#customId").val()==''){
						    	$.jBox.tip('评测客户ID不能为空');
		    					return false;
						    }
						   
						    var refundBillHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' ><table style='width:98%;margin-top:10px' id='showChildrenTable' class='table table-striped table-bordered table-condensed'><thead><tr><th style='width:300px'>ProductName</th><th style='width:20px'>Asin</th><th style='width:50px'>Quantity</th></tr></thead><tbody>";
							var asin=$("#names").val();
						    console.log(asin);
                            
						    for(var i=0;i<asin.length;i++){
						    	var arr=asin[i].split(",");
								refundBillHtml=refundBillHtml+"<tr><td class='pname'>"+arr[0]+"</td><td class='pasin'>"+arr[1]+"</td><td><input name='pquantity' class='pquantity' value='1'></td></tr>";
							}
							refundBillHtml=refundBillHtml+"</tbody></table></div>";
							
							var submitChild = function (v, h, f) {
								var ids="";
								var quantitys="";
								var skus="";
								h.find("input[name='pquantity']").each(function(){
										    var quantity=$(this).val();
											if(quantity!=''){
												var tr  =$(this).parent().parent();
												var  oldQuantity = tr.find(".pquantity").val();
												var id      =tr.find(".pasin").text();
												var sku      =tr.find(".pname").text();
												ids=ids+id+",";
												quantitys=quantitys+oldQuantity+",";
												skus=skus+sku+",";
											};
										
								});
								$("#nameId").val(ids);
								$("#titleId").val(quantitys);
								$("#skuId").val(skus);
								
								form.submit();
								return true;
						  };
						  $.jBox(refundBillHtml, { title: "Confirm Quantity",width:600,submit: submitChild,persistent: true});
						
					}else{
						loading("<spring:message code='custom_event_note6'/>");
						form.submit();
						top.$('.jbox-body .jbox-icon').css('top','55px');
					}
					
					
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("<spring:message code='custom_event_note7'/>");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
			$('#myfileupload').MultiFile({
				max : 5,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			<c:if test="${not empty event.id}">
				<c:set var="flag" value="true" />
				<c:forEach items="${event.comments}" var="comment" varStatus="i">
					<c:if test="${comment.type eq '0' && comment.createBy.id eq cuser.id}">
						$("#cNav").append('<li ${flag?"class=active":""}><a href="#panel-${i.index}" data-toggle="tab"><fmt:formatDate type="both" value="${comment.createDate}"/>记录 By&nbsp;${comment.createBy.name}</a></li>');
						CKEDITOR.replace("comments-${i.index}",{height:'200px',toolbarStartupExpanded:false,startupFocus:true});
						<c:set var="flag" value="false" />
					</c:if>
				</c:forEach>
			</c:if>
		});
		
		function initSelect2 (val){
			$("#spanIn").hide();
			$("#asin").select2('destroy');
			$("#names").select2('destroy');
			if(val != ''){
				$.get("${ctx}/amazoninfo/amazonProduct/asin?accountName="+val,function(data){
					$("#asin").html("");
					$("#names").html("");
					eval("var arr = "+data);
					var temp = "${event.remarks}";
					var flag = 0;
					$(arr).each(function(){
						if(temp!= '' && this.key == temp && val == '${event.accountName}'){
							$("#asin").append("<option value='"+this.key+"' selected >"+this.value+"</option>");
							flag = 1;
						}else{
							$("#asin").append("<option value='"+this.key+"'>"+this.value+"</option>");
						}
						var tempName=this.value+","+this.key;
						$("#names").append("<option value='"+tempName+"'>"+this.value+"</option>");
					});
					$("#asin").change();
					if(flag ==0 && val == '${event.accountName}'&&temp!= ''){
						$("#asin").append("<option value='"+temp+"' selected ><spring:message code='custom_event_note5'/></option>");
						$("#in").show();
					}else{
						$("#asin").append("<option value=''><spring:message code='custom_event_note5'/></option>");
					}
					$("#asin").select2({"width":"380px"});
					$("#names").select2({"width":"380px"});
				});
				
				
			}else{
				$("#asin").html("<option value=''><spring:message code='custom_event_note'/></option>");
				$("#asin").select2();
			}
		}
		function html_decode(str) 
		{ 
		    var s = ""; 
		    if (str.length == 0) return ""; 
		    s = str.replace(/&amp;/g, "&"); 
		    s = s.replace(/&lt;/g, "<"); 
		    s = s.replace(/&gt;/g, ">"); 
		    s = s.replace(/&nbsp;/g, " "); 
		    s = s.replace(/&#39;/g, "\'"); 
		    s = s.replace(/&quot;/g, "\""); 
		    s = s.replace(/<br\/>/g, "\n"); 
		    return s; 
		} 

		function htmlEncode(str) {
		    var div = document.createElement("div");
		    div.appendChild(document.createTextNode(str));
		    return div.innerHTML;
		}
		function htmlDecode(str) {
		    var div = document.createElement("div");
		    div.innerHTML = str;
		    return div.innerHTML;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/custom/event/"><spring:message code='custom_event_list'/></a></li>
		<li class="active"><a href="${ctx}/custom/event/editView?id=${event.id}"><spring:message code='custom_event_event'/><c:choose>
			<c:when test="${empty event.id}"><spring:message code="sys_but_add"/></c:when>
			<c:otherwise><spring:message code="sys_but_edit"/></c:otherwise>
		</c:choose></a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="event" action="${ctx}/custom/event/saveEvent" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input type="hidden" id="id" name="id" value="${event.id}"/>
		<input type="hidden" id="productName" name="productName" value="${event.productName}"/>
		<input type="hidden" id="nameId" name="nameId" />
		<input type="hidden" id="titleId" name="titleId" />
		<input type="hidden" id="skuId" name="skuId" />
		<input type="hidden" name="isEmailTax" id="isEmailTax" value="${isEmailTax }" />
		<c:if test="${aboutEvent!=null}">
			<input type="hidden" name="aboutEvent" value="${aboutEvent}"/>
		</c:if>
		
		<c:if test="${not empty emailId}">
			<input type="hidden" name="emailId" value="${emailId}"/>
		</c:if>
		
		<tags:message content="${message}"/>
		
		<div class="control-group">
			<input class="btn btn-primary" type="submit" value="<spring:message code='sys_but_save'/>"/>
			<input class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)"/>
		</div>
		
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_event_form'/></label>
			<div class="controls">
				<input type="text" name ="subject" id="subject" style="width: 60%" value="${event.subject}" class="required" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_event_form2'/></label>
			<div class="controls">
			    <c:choose>
			      <c:when test="${'1' eq event.type&&!fn:contains(event.reason,'差评')}"><input type="text" name ="reason" id="subject" style="width: 60%" value="差评:${event.reason}" class="required" /></c:when>
			      <c:otherwise><input type="text" name ="reason" id="subject" style="width: 60%" value="${event.reason}" class="required" /></c:otherwise>
			    </c:choose>
				
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_event_form3'/></label>
			<div class="controls">
				<c:choose>
					<c:when test="${empty event.createBy.id || cuser.id eq event.createBy.id}">
						<select name="priority">
							<option value="1" ${event.priority eq '1' ?'selected':''}>L1</option>
							<option value="2" ${event.priority eq '2' ?'selected':''}>L2</option>
							<option value="3" ${event.priority eq '3' ?'selected':''}>L3</option>
						</select>
					</c:when>
					<c:otherwise>
						<select disabled="disabled">
							<option value="1" ${event.priority eq '1' ?'selected':''}>L1</option>
							<option value="2" ${event.priority eq '2' ?'selected':''}>L2</option>
							<option value="3" ${event.priority eq '3' ?'selected':''}>L3</option>
						</select>
						<input type="hidden" name="priority" value="${event.priority}"/>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_event_form4'/></label>
			<div class="controls">
				<c:choose>
					<c:when test="${not empty support && '8' eq support}">
						<select name="type" id="eventType">
							<option value="8" ${event.type eq '8'?'selected':''}>Marketing Order</option>
						</select>
					</c:when>
					<c:when test="${empty event.createBy.id || cuser.id eq event.createBy.id}">
						<select name="type" id="eventType">
							<c:if test="${not empty event.id}">
								<option value="1" ${event.type eq '1'?'selected':''}>Rating</option>
								<option value="2" ${event.type eq '2'?'selected':''}>Account Rating</option>
								<option value="5" ${event.type eq '5'?'selected':''}>Support</option>
								<option value="7" ${event.type eq '7'?'selected':''}>Support_Voucher</option>
							   
							</c:if>
							<option value="4" ${event.type eq '4'?'selected':''}>Tax_Refund</option> 
							 <option value="8" ${event.type eq '8'?'selected':''}>Marketing Order</option>
							
							<c:if test="${not empty event.id}">
							  <option value="6" ${event.type eq '6'?'selected':''}>FAQ</option>
							</c:if>
							<option value="3" ${event.type eq '3'?'selected':''}>FAQ_Email</option>
							<option value="9" ${event.type eq '9'?'selected':''}>Product Improvement</option>
							<option value="10" ${event.type eq '10'?'selected':''}>Product Recall</option>
							<option value="11" ${event.type eq '11'?'selected':''}>Review Refund</option>
							<option value="12" ${event.type eq '12'?'selected':''}>Ebay Order</option>
							<option value="13" ${event.type eq '13'?'selected':''}>Website SupportOrder</option>
							<option value="14" ${event.type eq '14'?'selected':''}>Offline SupportOrder</option>
							<option value="15" ${event.type eq '15'?'selected':''}>MFN Order</option>
						</select>
					</c:when>
					<c:otherwise>
						<select id="eventType" disabled="disabled">
						<c:if test="${not empty event.id}">
							<option value="1" ${event.type eq '1'?'selected':''}>Rating</option>
							<option value="2" ${event.type eq '2'?'selected':''}>Account Rating</option>
							<option value="5" ${event.type eq '5'?'selected':''}>Support</option>
							<option value="7" ${event.type eq '7'?'selected':''}>Support_Voucher</option>
							
						</c:if>	
							<option value="4" ${event.type eq '4'?'selected':''}>Tax_Refund</option> 
							<option value="8" ${event.type eq '8'?'selected':''}>Marketing Order</option>
							<c:if test="${not empty event.id}">
							  <option value="6" ${event.type eq '6'?'selected':''}>FAQ</option>
							</c:if>  
							<option value="3" ${event.type eq '3'?'selected':''}>FAQ_Email</option>
							<option value="9" ${event.type eq '9'?'selected':''}>Product</option>
							<option value="10" ${event.type eq '10'?'selected':''}>Product Recall</option>
							<option value="11" ${event.type eq '11'?'selected':''}>Review Refund</option>
							<option value="12" ${event.type eq '12'?'selected':''}>Ebay Order</option>
							<option value="13" ${event.type eq '13'?'selected':''}>Website SupportOrder</option>
							<option value="14" ${event.type eq '14'?'selected':''}>Offline SupportOrder</option>
							<option value="15" ${event.type eq '15'?'selected':''}>MFN Order</option>
						</select>
						<input type="hidden" name="type" value="${event.type}"/>
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		<%-- <div class="control-group">
			<label class="control-label"><spring:message code='sys_label_country'/></label>
			<div class="controls">
				<select name="country" id="country">
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
						<option value="${dic.value}" ${event.country eq dic.value ?'selected':''}  >${dic.label}</option>
					</c:forEach>	
					<option value="" ${empty event.country?'selected':''}><spring:message code='custom_event_other'/></option>
				</select>
			</div>
		</div>  --%>
		 <div class="control-group">
			<label class="control-label">Account</label>
			<input type='hidden' name='country' id='country' value='${event.country }'/>
			<div class="controls">
				<select name="accountName" id="accountName">
					<c:forEach items="${accountMap}" var="account">
						<option value="${account.key}"  countryVal='${accountMap[account.key]}'  ${event.accountName eq account.key?'selected':''}>${account.key}</option>
					</c:forEach>	
					<option value="" countryVal=''  ${empty event.accountName?'selected':''}><spring:message code='custom_event_other'/></option>
				</select>
			</div>
		</div>
		<div class="control-group" id='name1'>
			<label class="control-label"><spring:message code='custom_event_form5'/></label>
			<div class="controls">
				<select name="remarks" id="asin">
				</select>
				<span id="spanIn" style="display: none"><spring:message code='custom_event_note8'/>:<input value="${event.remarks}" id="in"/></span>
			</div>
		</div>
		
		<div class="control-group" style="display: none" id='name2'>
			<label class="control-label"><spring:message code='custom_event_form5'/></label>
			<div class="controls">
				<select id="names" name="names" style="width:250px" multiple class="multiSelect" >
				</select>
			</div>
		</div>
		
		<div class="control-group" id="problemDiv" style="display:${(event.type eq '1'||event.type eq '2')?'block':'none'}">
			<label class="control-label">Problem Type</label>
			<div class="controls">
				<select name="problemType" id="problemType" class="${(event.type eq '1'||event.type eq '2')?'required':''}" ></select>
			</div>
		</div>
		
		<c:choose>
		<c:when test="${empty event.id}">
			<div class="control-group">
				<label class="control-label"><spring:message code='custom_event_colName'/></label>
				<div class="controls">
					<select name="masterBy.id" id="masterBy">
						<c:forEach items="${all}" var="user">
							<c:choose>
								<c:when test=" ${event.masterBy==null}">
									<option value="${user.id}" ${cuser.id eq user.id?'selected':''} >${user.name}</option>
								</c:when>
								<c:otherwise>
									<option value="${user.id}" ${event.masterBy.id eq user.id?'selected':''} >${user.name}</option>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</select>
				</div>
			</div>
			<input type="hidden" value="0" name="state" />
		</c:when>
		<c:otherwise>
			<input type="hidden" name="event.masterBy.id" value="${event.masterBy.id}"/>
		</c:otherwise>
		</c:choose>
		<!-- ship to china -->
		<div class="control-group" id="isShipToChina">
			<label class="control-label"></label>
			<div class="controls">Ship to China
				<c:if test="${'1' ne flagToChina }">
					<input type="checkbox" name="shipToChina" class="isCheck" value="0"/>
				</c:if>
			<c:if test="${'1' eq flagToChina }">
				<input type="checkbox" name="shipToChina" class="isCheck" value="1" checked/>
			</c:if>
			</div>
		</div>
		
		<div class="control-group" id="productQuantityDiv">
			<label class="control-label"><spring:message code='custom_event_form26'/></label> <!-- 产品数量 -->
			<div class="controls">
				<input type="text" name ="productQuantity" value="${event.productQuantity}" class="required digits"/>
			</div>
		</div>
			<div class="control-group"  id="taxIdDiv">
			<label class="control-label">Tax ID</label> <!-- 产品数量 -->
			<div class="controls">
				<input type="text" name ="taxId" value="${event.taxId}"/>
			</div>
		</div>
		<div id="refundTypeDiv"><!--Review Refund事件 -->
			<div class="control-group">
				<label class="control-label"><spring:message code='custom_event_refund_way'/></label>
				<div class="controls">
					<select name="refundType" id="refundType">
						<option value="1" ${'1' eq event.refundType?'selected':''} >Gift Card</option>
						<option value="2" ${'2' eq event.refundType?'selected':''} >Paypal Card</option>
						<option value="3" ${'3' eq event.refundType?'selected':''} >Credit Card</option>
					</select>
				</div>
			</div>
			<div class="control-group"  id="totalPriceDiv">
				<label class="control-label"><spring:message code='custom_event_total_price'/></label> <!--Review Refund事件返款方式 -->
				<div class="controls">
					<input type="text" id="totalPrice" name ="totalPrice" value="${event.totalPrice}"/>
				</div>
			</div>
			<div class="control-group"  id="cardNumberDiv">
				<label class="control-label"><spring:message code='custom_event_card_number'/></label> <!--Review Refund事件返款方式 -->
				<div class="controls">
					<input type="text" name ="cardNumber" value="${event.cardNumber}" style="width: 60%" />
				</div>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_event_form6'/></label>
			<div class="controls">
				<input type="text" id="invoiceNumber" name ="invoiceNumber"  style="width: 60%" value="${event.invoiceNumber}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_event_form7'/></label>
			<div class="controls">
				<input type="text" name ="customName"  style="width: 60%" value="${event.customName}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_event_form8'/></label>
			<div class="controls">
				<input type="text" name ="customEmail"  style="width: 60%" value="${event.customEmail}" />
			</div>
		</div>
		<div class="control-group notRecall">
			<label class="control-label"><spring:message code='custom_event_form9'/></label>
			<div class="controls">
				<input type="text" name ="reviewLink" style="width: 60%" value="${event.reviewLink}" /><span class="help-inline">多个以逗号分隔</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_event_form10'/></label>
			<div class="controls">
				<input type="text" name ="customId"  style="width: 60%" value="${event.customId}" id='customId'/><span class="help-inline">事件类型为Marketing Order时，作为客户信息填写</span>
			</div>
		</div>
		<div class="control-group notRecall">
			<label class="control-label"><spring:message code='custom_event_form11'/></label>
			<div class="controls">
				<input onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" type="text" name ="reviewDate" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${event.reviewDate}'/>"  readonly="readonly"  class="Wdate"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_event_form12'/></label>
			<div class="controls">
				<textarea name="description">${event.description}</textarea>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_event_form13'/></label>
			<div class="controls">
				<span class="help-inline"><spring:message code='custom_event_note9'/></span>
				<span class="label label-warning" style="font-size: 18px;">No Chinese or space in the file name</span>
				<input name="attchmentFile" type="file" id="myfileupload" />
			</div>
		</div>
		<c:if test="${not empty event.id}">
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_event_form14'/></label>
			<div class="controls">
				<ul class="nav nav-tabs" id="cNav"></ul>
				<div class="tab-content" id="cTab">
					<c:if test="${not empty event.id}">
						<c:set var="flag" value="true" />
						<c:forEach items="${event.comments}" var="comment" varStatus="i">
							<c:if test="${comment.type eq '0' && comment.createBy.id eq cuser.id}">
								<div class="tab-pane ${flag?'active':''}" id="panel-${i.index}">
									<textarea id="comments-${i.index}" name="comms">${comment.comment}</textarea>
									<input type="hidden" value="${comment.id}" name="cids"/>
									<c:set var="flag" value="false" />
								</div>
							</c:if>
						</c:forEach>
					</c:if>
				</div>
			</div>
		</div>
		</c:if>
		<div class="control-group">
			<input class="btn btn-primary" type="submit" id='titleQuery' value="<spring:message code='sys_but_save'/>"/>
			<input class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
