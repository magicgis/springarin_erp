<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Custom Email Manager</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		.modal.fade.in {
		 	top: 0%;
		}
		.modal{
			 width: auto;
			 margin-left:-500px 
		}
	
	</style>
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
			eval('var mangerMapJson=${mangerMapJson}');
			eval('var problemMapJson=${problemMapJson}');
			
			if(!(top)){
				top = self; 
			}
			$("#subject").focus();
			
			$("#singleRemark").on("blur",function(){
				var params = {};
				if($("#singleRemark").val()){
					params.id='${emailManager.id}';
					params.remark = encodeURI($("#singleRemark").val());
					$.get("${ctx}/custom/emailManager/updateRemark?"+$.param(params),function(data){
						if(!(data)){    
							top.$.jBox.error("Save remark fail", 'info',{timeout:2000});
						}else{
							top.$.jBox.tip("Save remark success", 'info',{timeout:2000});
						}
					});
				}
			});
			
			$("#transmit").click(function(){
				if(!(top)){
					top = self; 
				}
				top.$.jBox.confirm('To determine customer email forwarded to other customer groups deal with it?','Prompted',function(v,h,f){
					if(v=='ok'){
						loading('Please wait a moment!');
						var params = {};
						params.id = '${customEmail.id}'; 
						params.customKey =$("#transmitSel").val();
						window.location.href = "${ctx}/custom/emailManager/transmit?"+$.param(params);
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#transmitOther").click(function(){
				if(!(top)){
					top = self; 
				}
				var userId = $("#transmitSelOther").val();
				var userName = $("option[value='"+userId+"']").text();
				top.$.jBox.confirm('Forwarding To '+userName+'?','Prompted',function(v,h,f){
					if(v=='ok'){
						loading('Please wait a moment!');
						var params = {};
						params.id = '${customEmail.id}'; 
						params.userId = userId;
						window.location.href = "${ctx}/custom/emailManager/transmitOther?"+$.param(params);
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			
			$(".noreply").click(function(){
				if(!(top)){
					top = self; 
				}
				top.$.jBox.confirm("Determine that the message does not need to deal with it?</br>Remrk:<input id='remarks' type='text' style='width:200px'/>",'Prompted',function(v,h,f){
					if(v=='ok'){
						loading('Please wait a moment!');
						var params = {};
						params.id = '${customEmail.id}'; 
						params.state = '4';
						params.remarks=encodeURI($.trim(h.find("#remarks").val()));
						window.location.href = "${ctx}/custom/emailManager/noreply?"+$.param(params);
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			<c:if test="${fn:length(orders)>0}">
			$(".returnGoods").click(function(){
				top.$.jBox.confirm('Determined to send return templates mail?','Prompted',function(v,h,f){
					if(!(top)){
						top = self; 
					}
					if(v=='ok'){
						loading('Please wait a moment!');
						var params = {};
						params.customEmailId = '${customEmail.id}'; 
						window.location.href = "${ctx}/custom/sendEmail/template?"+$.param(params);
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			</c:if>
			
			$(".reply").click(function(){
				var params = {};
				params.id = '${customEmail.id}'; 
				params.checkEmail='${checkEmail}';
				params.country='${country}';
				window.location.href = "${ctx}/custom/emailManager/reply?"+$.param(params);
			});
			
			$("#unsubscribe").click(function(event){
				event.preventDefault();
				$.get($(this).attr("href"),function(data){
					if(data){
						top.$.jBox.tip("Email has been added to the unsubscribe list!");
					}
				});
			});
			
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('Please wait a moment!');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("Entered incorrectly, please correct");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
			
			$(".btn-group button").click(function(){
				var param = {};
				param.id='${emailManager.id}';
				param.flag = $(this).attr("key");
				$.get("${ctx}/custom/emailManager/saveMark?"+$.param(param),function(data){
					if(data){
						top.$.jBox.tip("Mark success!");
					}
				});
			});
			
			$(".support").click(function(){
				var params = {};
				<c:if test="${fn:length(orders)>0}">
					params.amazonOrderId = '${orders[0].amazonOrderId}'; 
				</c:if>
				params.problem=encodeURI($("span.problemDetail").text());
				
				if($("span.problemDetail").text()==''){
					<c:if test="${not empty hisProblems}">
					   params.problem=encodeURI($(".problemStr:eq(0)").text());
				    </c:if>
				}
				window.open("${ctx}/amazoninfo/amazonTestOrReplace/createSupportEvent?"+$.param(params));
			});
			
			$(".support2").click(function(){
				var params = {};
				<c:if test="${fn:length(orders)>0}">
					params.orderId = '${orders[0].amazonOrderId}'; 
				</c:if>
				params.emailId = '${customEmail.id}'; 
				params.support = '1';
				window.open("${ctx}/custom/event/editView?"+$.param(params));
			});
			
			$(".tax").click(function(){
				var params = {};
				<c:if test="${fn:length(orders)>0}">
					params.orderId = '${orders[0].amazonOrderId}'; 
					if('${orders[0].orderStatus}'!='Shipped'){
						$.jBox.tip("tax-refund not allowed for pending order");
						return;
					}
				</c:if>
				params.emailId = '${customEmail.id}'; 
				params.support = '4';
				params.isEmailTax='0';
				window.open("${ctx}/custom/event/editView?"+$.param(params));
			});
			
			$(".updateEmail").click(function(){
				var email="${customEmail.revertEmail}";
				var cusId=$(this).attr("customKey");
				var amzEmail=$(this).attr("amzEmailKey");
				$.ajax({
	      			   type: "POST",
	      			   url: "${ctx}/amazoninfo/customers/updateEmail?customerId="+cusId+"&email="+email+"&amzEmail="+amzEmail,
	      			   async: true,
	      			   success: function(msg){
	      				  if(msg=="1"){
	      					$.jBox.tip('个人邮箱修改成功'); 
	    					return;
	      				  }else{ 
	      					$.jBox.tip('个人邮箱修改失败'); 
	      					return;
	      				  }
	      			   }
	          		});
			});
			
			/* $(".createTax").click(function(){
				$.ajax({
	      			   type: "POST",
	      			   url: "${ctx}/custom/emailManager/isExistEvent?id=${customEmail.id}",
	      			   async: true,
	      			   success: function(msg){
	      				  if(msg=="1"){
	      					$.jBox.tip('Tax-refund event not found'); 
	    					return;
	      				  }else if(msg=="0"){ 
	      					 window.location.href="${ctx}/custom/emailManager/taxRefund?id=${customEmail.id}";
	      				  }
	      			   }
	          		});
			});
			 */
			$("#productRecall").click(function(){
				var params = {};
				<c:if test="${fn:length(orders)>0}">
					params.orderId = '${orders[0].amazonOrderId}'; 
				</c:if>
				params.emailId = '${customEmail.id}'; 
				params.support = '10';
				window.open("${ctx}/custom/event/editView?"+$.param(params));
			});
			 
			$("#toPM").click(function(){
				var params = {};
				<c:if test="${fn:length(orders)>0}">
					params.orderId = '${orders[0].amazonOrderId}'; 
				</c:if>
				params.emailId = '${customEmail.id}'; 
				params.support = '9';
				window.open("${ctx}/custom/event/editView?"+$.param(params));
			});
			
			$("#saveProblem").on("click",function(){
					var param = {};
					param.country=$("#country").val();
					param.productName=$("#productName").val();
					param.problemType=$("#problemType").val();
					param.problem=encodeURI($("#problemDetail").val());
					param.id='${customEmail.id}';
					param.orderNos='${customEmail.orderNos}';
					if(param.country==''){
						top.$.jBox.tip("Country not empty！","info",{timeout:3000});
						return false;
					}
					if(param.productName==''){
						top.$.jBox.tip("ProductName not empty！","info",{timeout:3000});
						return false;
					}
					if(param.problemType==''){
						top.$.jBox.tip("ProblemType not empty！","info",{timeout:3000});
						return false;
					}
					
					if(param.problemType=='other'&&param.problem==''){
						top.$.jBox.tip("ProblemDetail not empty！","info",{timeout:3000});
						return false;
					}
					
					$.get("${ctx}/custom/emailManager/saveProblem?"+$.param(param,true),function(){});
					top.$.jBox.tip("保存成功！","info");
					$(".close").click();
					$(".problemDiv").css("display","block");
					$(".replayDiv").css("display","block");
					$("span.productName").text(param.productName);
					$("span.problemType").text(param.problemType);
					$("span.problemDetail").text($("#problemDetail").val());
					$("span.country").text(param.country);
			});
			
			
			$("#productName").change(function(){
				var productName=$(this).val();
				var managerName ="";
				var optStr ="";
				if(productName!=''){
					var type = productMapJson[productName];
					managerName = mangerMapJson[type];
					var problems = problemMapJson[type];
					for(var i in problems){
						optStr=optStr+"<option value='"+problems[i]+"'>"+problems[i]+"</option>";
					}
				}
				
				$("#manager").text(managerName);
				$("#problemType").empty();
				$("#problemType").append(optStr).select2().change();
			});
			
			$("#problem").click(function(){
				$("#problemModal").modal();
				//如果原来已经回复了，只需编辑原来的
				if('${customEmail.problemType}'!=''){
					$("#productName").select2("val",$("span.productName").text()).change();
					$("#problemType").select2("val",$("span.problemType").text());
					$("#problemDetail").val($("span.problemDetail").text());
					$("#manager").text(mangerMapJson[$("span.problemType").text()]);
					$("#country").select2("val",$("span.country").text()).change();
				}else{
					//如果有订单，默认选中订单中的一个产品 
					if('${productName}'!=''){
						$("#productName").select2("val",'${productName}').change();
						var type = productMapJson['${productName}'];
						$("#problemType").select2("val",type);
						$("#manager").text(mangerMapJson[type]);
						$("#country").select2("val",'${country}').change();
						console.log('${country}');
					}
					
				}
			});
			
			$("#suggestion").click(function(){
				$("#suggestionModal").modal();
				//如果原来已经回复了，只需编辑原来的
				if($("span.suProductName").text()!=''){
					$("#suProductName").select2("val",$("span.suProductName").text()).change();
					var type = productMapJson[$("span.suProductName").text()];
					$("#suggestionDetail").val($("span.content").text());
					$("#manager").text(mangerMapJson[type]);
					$("#suCountry").select2("val",$("span.suCountry").text()).change();
				} else {
					//如果有订单，默认选中订单中的一个产品 
					if('${productName}'!=''){
						$("#suProductName").select2("val",'${productName}').change();
						var type = productMapJson['${productName}'];
						$("#productManager").text(mangerMapJson[type]);
						$("#suCountry").select2("val",'${country}').change();
					}
				}
			});
			
			$("#suProductName").change(function(){
				var productName=$(this).val();
				var managerName ="";
				var optStr ="";
				if(productName!=''){
					var type = productMapJson[productName];
					managerName = mangerMapJson[type];
					var problems = problemMapJson[type];
					for(var i in problems){
						optStr=optStr+"<option value='"+problems[i]+"'>"+problems[i]+"</option>";
					}
				}
				$("#productManager").text(managerName);
			});
			
			$("#saveSuggestion").on("click",function(){
				var param = {};
				param.id='${suggetion.id}';
				param.country=$("#suCountry").val();
				param.productName=$("#suProductName").val();
				param.content=encodeURI($("#suggestionDetail").val());
				param.emailId='${customEmail.id}';
				if(param.country==''){
					top.$.jBox.tip("Country not empty！","info",{timeout:3000});
					return false;
				}
				if(param.productName==''){
					top.$.jBox.tip("ProductName not empty！","info",{timeout:3000});
					return false;
				}
				if(param.content=='' || param.content.length==0){
					top.$.jBox.tip("Suggetion Detail not empty！","info",{timeout:3000});
					return false;
				}
				
				$.get("${ctx}/custom/suggestion/ajaxSave?"+$.param(param,true),function(data){
					if(data && data=='0'){
						top.$.jBox.tip("保存成功！","info");
						$(".close").click();
						$(".suggestionDiv").css("display","block");
						$("span.suProductName").text(param.productName);
						$("span.content").text($("#suggestionDetail").val());
						$("span.suCountry").text(param.country);
					} else {
						top.$.jBox.tip(data,"info");
					}
				});
				
			});
			
			$("#followState").change(function(){
				var type=$(this).val();
				if(type=='0'){
					$("#followDateSpan").show();
				}else{
					$("#followDateSpan").hide();
				}
			});
			
			$("#markState").click(function(e){
				  var followState=$("#followState").val();
				  var followDate=$("#followDate").val();
				  
				    var params = {};

					params.id='${emailManager.id}';
					params.followState=followState;
					params.followDate =followDate;
					$.get("${ctx}/custom/emailManager/updateFollow?"+$.param(params),function(data){
							if(!(data)){    
								top.$.jBox.error("Save fail", 'info',{timeout:2000});
							}else{
								top.$.jBox.tip("Save success", 'info',{timeout:2000});
							}
							$("#buttonClose").click();
					});
			});
		});
		
		function processTaxRefund(pathName){
			<c:if test="${fn:length(orders)>1}">
			    var result = "<li><a href='#' onclick=\"$('#amazonOrderId').prop('value',$(this).text())\"></a></li>";
			    <c:forEach items="${orders}" var="order">
			    result += "<li><a href='#' onclick=\"$('#amazonOrderId').prop('value',$(this).text())\">${order.amazonOrderId}</a></li>";
			    </c:forEach>
				var html2 ="<b>AmazonOrderId:</b> <div class='input-append btn-group btn-input'>"+
				"<input id='amazonOrderId' name='amazonOrderId' style='height: 25px;width:200px' class='span2' size='16' />"
				+"<a class='btn btn-default dropdown-toggle' data-toggle='dropdown'><span class='caret'></span></a><ul class='dropdown-menu'>"
				+result+"</ul></div>";

				top.$.jBox.confirm(html2,"Select TaxRefund OrderId",function(v,h,f){
						if(v=='ok'){
							var amazonOrderId = h.find("#amazonOrderId").val(); 
							if(amazonOrderId==""){
								top.$.jBox.tip("OrderId is Required!","error",{persistent:false,opacity:0});
								return false;
							}
							 $.ajax({
					   			   type: "POST",
					   			   url: "${ctx}/custom/emailManager/isExistEvent?id=${customEmail.id}&attchmentPath="+pathName+"&amazonOrderId="+amazonOrderId,
					   			   async: true,
					   			   success: function(msg){
					   				  if(msg=="1"){
					   					$.jBox.tip('Tax-refund event not found'); 
					 					return;
					   				  }else if(msg=="0"){ 
					   					 window.location.href="${ctx}/custom/emailManager/taxRefund?id=${customEmail.id}&amazonOrderId="+amazonOrderId;
					   				  }
					   			   }
					       	});
						}
					},{buttonsFocus:1,height:250});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				
			</c:if>
            <c:if test="${fn:length(orders)<=1}">
               $.ajax({
	   			   type: "POST",
	   			   url: "${ctx}/custom/emailManager/isExistEvent?id=${customEmail.id}&attchmentPath="+pathName,
	   			   async: true,
	   			   success: function(msg){
	   				  if(msg=="1"){
	   					$.jBox.tip('Tax-refund event not found'); 
	 					return;
	   				  }else if(msg=="0"){ 
	   					 window.location.href="${ctx}/custom/emailManager/taxRefund?id=${customEmail.id}";
	   				  }
	   			   }
	       		});
			
			</c:if>
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/custom/emailManager/"><spring:message code='custom_email_list'/></a></li>
		<li class="active"><a href="${ctx}/custom/emailManager/form?id=${emailManager.id}"><spring:message code='custom_email_reply'/></a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="customEmail" action="${ctx}/custom/emailManager/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<tags:message content="${message}"/>
		<div class="control-group">
		   <div style="float: left;">
		   <div style="float:left;display:${empty customEmail.problemType?'block':'block'}" class="replayDiv">
			   	<input class="btn btn-primary reply" type="button" value='<spring:message code="custom_email_btn5" />'/>&nbsp;
			<%-- 	<c:if test="${fn:length(orders)>0}">
				<input class="btn btn-primary returnGoods" type="button" value="<spring:message code='custom_email_replyReturnGoods' />"/>&nbsp;
				</c:if> --%>
		   </div>
			
			<input class="btn btn-primary noreply" type="button" value="<spring:message code='custom_email_btn1'/>"/>&nbsp;
			<input class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)"/>
			<div class="btn-group" data-toggle="buttons-radio">
			  <button key="" type="button" class="btn btn-info${empty customEmail.flag?' active':''}">No Flag</button>
			  <button key="p1" type="button" class="btn btn-info${'p1' eq customEmail.flag?' active':''}" >P1</button>
			  <button key="p2" type="button" class="btn btn-info${'p2' eq customEmail.flag?' active':''}">P2</button>
			</div>
			 <a href="#followEmailDiv" role="button" class="btn btn-info" data-toggle="modal" id="followEmail">FollowUP</a> 
			&nbsp;Remark:<input type="text" id="singleRemark" value="${customEmail.remarks}" style="width:200px"/>  
			<br/>
			 <div style="float:left;margin-top:6px">
				<!--  <input class="btn btn-warning support2" type="button" value="Inateck-Fulfillment"/>&nbsp;&nbsp;  -->
				  <input class="btn btn-warning support" type="button" value="Fulfillment"/>&nbsp;
				&nbsp;
				<input class="btn btn-warning tax" type="button" value="Tax-refund Event"/>&nbsp;
				&nbsp;
				
				<input class="btn btn-warning " id="problem" type="button" value="Edit Problem"/>&nbsp;
				&nbsp;
				<input class="btn btn-warning" id="productRecall" type="button" value="ProductRecall"/>&nbsp;
				&nbsp;
				<input class="btn btn-warning" id="toPM" type="button" value="to PM"/>&nbsp;
				&nbsp;
				<input class="btn btn-warning " id="suggestion" type="button" value="Edit Customer Suggestions"/>&nbsp;
			 </div>
			
			<br/><span class="help-inline"><spring:message code='custom_email_tips'/></span>
			</div>
			<div style="float: right;">
				<div>
				<select id="transmitSel" style="width: 150px">
					<c:if test="${not empty customRoles['de']}">
						<option value="de">DE Customer Service</option>
					</c:if>
					<c:if test="${not empty customRoles['uk']}">
						<option value="uk">UK Customer Service</option>
					</c:if>
					<c:if test="${not empty customRoles['com.unitek']}">
						<option value="com.unitek">US(unitek)Customer Service</option>
					</c:if>
					<c:if test="${not empty customRoles['it']}">
						<option value="it">IT Customer Service</option>
					</c:if>
					<c:if test="${not empty customRoles['com']}">
						<option value="com">US Customer Service</option>
					</c:if>
					<c:if test="${not empty customRoles['jp']}">
						<option value="jp">JP Customer Service</option>
					</c:if>
					<c:if test="${not empty customRoles['ca']}">
						<option value="ca">CA Customer Service</option>
					</c:if>
					<c:if test="${not empty customRoles['fr']}">
						<option value="fr">FR Customer Service</option>
					</c:if>
					<c:if test="${not empty customRoles['es']}">
						<option value="es">ES Customer Service</option>
					</c:if>
					<%-- <c:forEach items="${customRoles}" var="customRole">
						<option value="${customRole.key}">${customRole.value}</option>
					</c:forEach> --%>
				</select>&nbsp;
				<input class="btn btn-primary"  id="transmit" type="button" value='<spring:message code="custom_email_btn8"/>'/>&nbsp;
				</div>
				<c:if test="${fn:length(otherMaster)>0}">
					<div style="margin-top:6px">
					<select id="transmitSelOther" style="width: 100px">
						<c:forEach items="${otherMaster}" var="user">
							<option value="${user.id}">${user.name}</option>
						</c:forEach>
					</select>&nbsp;
					<input id="transmitOther" class="btn btn-primary" type="button" value="<spring:message code='custom_email_btn4'/>"/>&nbsp;
					</div>
				</c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Account</label>
			<div class="controls">
				${emailMap[customEmail.revertServerEmail]}
			</div>
		</div>
		<c:if test="${not empty customEmail.transmit}">
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form13'/></label>
			<div class="controls">
				${customEmail.transmit}
			</div>
		</div>
		</c:if>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form1'/></label>
			<div class="controls">
				${customEmail.encryptionEmail} <c:if test="${'0' eq customEmail.followState}">&nbsp;&nbsp;<b>Reminder date:${customEmail.followDate }</b></c:if>
				
				<c:if test="${fn:contains(customEmail.revertEmail,'@marketplace.amazon.')}">
					 <a id="unsubscribe" href="${ctx}/custom/emailManager/unsubscribeEmail?customEmail=${customEmail.revertEmail}" style="font-size: 18px">[Add To UnsubscribeEmail List]</a>
					 <span class="help-inline">Click on the link, the system will not automatically send e-mail bills, and other promotional messages to guests</span>
				</c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form3'/></label>
			<div class="controls">
				${customEmail.subject}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form14'/></label>
			<div class="controls">
				<fmt:formatDate type="both" value="${customEmail.customSendDate}"/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_his_problem'/></label>
			<div class="controls" style="background-color:#FAFAD2">
				<c:if test="${not empty hisProblems}">
					<c:forEach items="${hisProblems}" var="hisPro" varStatus="j">
					  ${j.index+1}.&nbsp;&nbsp;${hisPro[0]} &nbsp;&nbsp; ${hisPro[1]} <spring:message code='custom_email_problem_type'/>： ${hisPro[2]} <spring:message code='custom_email_problem_detail'/>：<span class='problemStr'>${hisPro[3]}</span> <br/>
					</c:forEach>
				</c:if>
			</div>
		</div>
		
		
		<div class="problemDiv" style="display:${ empty customEmail.problemType?'none':'block'}">
			<div class="control-group">
				<label class="control-label">product Name</label>
				<div class="controls">
					<span class="productName">${customEmail.productName}</span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">country</label>
				<div class="controls">
					<span class="country">${customEmail.country}</span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">problem type</label>
				<div class="controls">
					<span class="problemType">${customEmail.problemType}</span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">problem detail</label>
				<div class="controls">
					<span class="problemDetail">${customEmail.problem}</span>
				</div>
			</div>
		</div>
		
		<div class="suggestionDiv" style="display:${ empty suggestion.id?'none':'block'}">
			<p style="font-size: 14px">Customer Suggestion:</p>
			<div class="control-group">
				<label class="control-label">product Name</label>
				<div class="controls">
					<span class="suProductName">${suggestion.productName}</span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">country</label>
				<div class="controls">
					<span class="suCountry">${suggestion.country}</span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">suggestion detail</label>
				<div class="controls">
					<span class="content">${suggestion.content}</span>
				</div>
			</div>
		</div>
		<c:if test="${fn:length(orders)>0}">
			<blockquote>
				<p style="font-size: 14px"><spring:message code='custom_email_tips1'/></p>
				<c:forEach items="${orders}" var="order">
						<div class="control-group" style="border-bottom-color: blue">
							<label class="control-label"><spring:message code='amazon_order_form1'/></label>
							<div class="controls">
								<a target="_blank" href="${ctx}/amazoninfo/order/form?amazonOrderId=${order.amazonOrderId}">${order.amazonOrderId}</a>
								<c:set var='emailKey' value="${customEmail.revertServerEmail}_${order.countryChar}"/>
								<c:if test="${not empty emailMap[emailKey]&&order.accountName ne emailMap[emailKey]}">
								    <span style='color:#ff0033;'><b>Mailbox account and order account are different!!!</b></span>
								</c:if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">Account</label>
							<div class="controls">
								<b>${order.accountName }</b>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label"><spring:message code='amazon_order_form35'/></label>
							<div class="controls">
								${order.orderStatus}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label"><spring:message code='amazon_order_form2'/></label>
							<div class="controls">
								<fmt:formatDate value="${order.purchaseDate}"/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">Customer Info</label>
							<div class="controls">
								<a target="_blank" href="${ctx}/amazoninfo/customers/view?customId=${order.customId}">${order.customId}</a>
								<%--  <c:if test="${!fn:contains(customEmail.revertEmail,'marketplace.amazon.')}">
								     <a class="btn btn-info updateEmail"  customKey='${order.customId}' amzEmailKey='${order.buyerEmail }'>Match Amz Email</a>
								 </c:if> --%>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label"><spring:message code='amazon_order_form9'/></label>
							<div class="controls">
								${order.buyerName}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label"><spring:message code='amazon_order_form10'/></label>
							<div class="controls">
								${order.buyerEmail}
								<c:if test="${not empty eventIdMap&&not empty eventIdMap[order.buyerEmail] }">
								    &nbsp;&nbsp;Rating Event:
								    <c:forEach items="${eventIdMap[order.buyerEmail]}" var="eventId"><a target='_blank' href="${ctx}/custom/event/form?id=${eventId}">SPR-${eventId }</a>&nbsp;&nbsp;</c:forEach>
								</c:if>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label"><spring:message code='amazon_order_form4'/></label>
							<div class="controls">
								${order.salesChannel}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label"><spring:message code='amazon_order_form5'/></label>
							<div class="controls">
								${order.orderTotal}<span class="help-inline">(Platform Currency Unit)</span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label"><spring:message code='custom_email_tips2'/></label>
							<div class="controls">
								<c:forEach items="${order.items}" var="item">
									SKU:${item.sellersku};Quantity:${item.quantityOrdered}<br/>
								</c:forEach>
							</div>
						</div>
						<c:if test="${not empty order.amazonRefunds}">
					<blockquote>
						<p style="font-size: 14px">Refund Record</p>
					</blockquote>
					<table class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							   <th style="width: 20%">产品名称</th>
							   <th style="width: 25%">退款类型</th>
							   <th style="width: 25%">亚马逊退款原因</th>
							   <th style="width: 10%">退款金额</th>
							   <th style="width: 20%">实际退款原因</th>
						</tr>
					</thead>
					<tbody>
					<c:set var="totalMoney" value="0"/>
					<c:forEach items="${order.amazonRefunds}" var="record" >
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
					    <th colspan="3" style="text-align: center;vertical-align: middle">合计</th>
					    <th><fmt:formatNumber value="${totalMoney}" maxFractionDigits="2"/></th>
					    <th></th>
					  </tr> 
					</tfoot>
				 </table>
					</c:if>
				</c:forEach>
			</blockquote>
		</c:if>
		<div class="control-group">${customEmail.tempContent}</div>
		<c:if test="${not empty customEmail.attchmentPath || not empty customEmail.inlineAttchmentPath}">
			<div class="control-group">
				<label class="control-label">Attchment</label>
				<div class="controls">
					<c:forEach items="${fn:split(customEmail.attchmentPath,',')}" var="attchment">
						<a href="${ctx}/custom/emailManager/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
						&nbsp;
						<c:if test="${not empty attchment}"><input class="btn btn-warning createTax" onclick="processTaxRefund('${attchment}')" type="button" value="Process Tax-refund"/>&nbsp;&nbsp;&nbsp;</c:if>
					</c:forEach>
					<c:forEach items="${fn:split(customEmail.inlineAttchmentPath,',')}" var="attchment">
						<a href="${ctx}/custom/emailManager/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
						&nbsp;
						<c:if test="${not empty attchment}"><input class="btn btn-warning createTax" onclick="processTaxRefund('${attchment}')" type="button" value="Process Tax-refund"/>&nbsp;&nbsp;&nbsp;</c:if> 
					</c:forEach>
				</div>
			</div>
		</c:if>
		<div class="control-group">
		 <div style="float:left;display:${empty customEmail.problemType?'block':'block'}" class="replayDiv">
			<input  class="btn btn-primary reply" type="button" value='<spring:message code="custom_email_btn5" />'/>&nbsp;
			<%-- <c:if test="${fn:length(orders)>0}">
			<input class="btn btn-primary returnGoods" type="button" value="<spring:message code='custom_email_replyReturnGoods' />"/>&nbsp;
			</c:if> --%>
		</div>
			<input  class="btn btn-primary noreply" type="button" value="<spring:message code='custom_email_btn1'/>"/>&nbsp;
			
			<input  class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)"/>
		</div>
	</form:form>
	
	
	
		<div id="problemModal" class="modal hide fade"  style="width:600px;">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3>录入产品问题</h3>
		</div>
		<div class="modal-body">
			<div class="control-group" style="height:30px;">
				<div class="controls" >
					<label style="width:150px"><b>Country:</b></label>
					<select id="country" class="required" style="width:300px">
							<option value=""></option>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<c:if test="${dic.value ne 'com.unitek' }">
								<option value="${dic.value}">${dic.label}</option>
							</c:if>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="control-group" style="height:30px;">
				<div class="controls" >
					<label style="width:150px"><b>Product Name:</b></label>
					<select id="productName"  class="required" style="width:300px" >
							<option value=""></option>
						<c:forEach items="${productMap}" var="productEntry">
							<option value="${productEntry.key}">${productEntry.key}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="control-group" >
				<div class="controls" style="height:30px;">
					<label style="width:150px;"><b>Product Manager:</b></label>
					<span id="manager"></span>
				</div>
			</div>
			<div class="control-group" >
				<div class="controls" style="height:30px;">
					<label style="width:150px"><b>Problem Type:</b></label>
					<select id="problemType" class="required" style="width:300px"></select>
				</div>
			</div>
			
			<div class="control-group" style="height:80px;">
				<div class="controls">
				<label style="width:150px"><b>Problem Detail:</b></label>
					<textarea  class="required" id="problemDetail" style="width: 400px; margin: 0px 0px 10px; height: 70px;"></textarea>
				</div>
			</div>
			<div class="modal-footer">
				<button class="btn btn-primary"  type="button" id="saveProblem">Submit</button>
				<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
			</div>
		</div>
	</div>
	
	<div id="suggestionModal" class="modal hide fade"  style="width:600px;">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3>Edit Customer Suggestions</h3>
		</div>
		<div class="modal-body">
			<div class="control-group" style="height:30px;">
				<div class="controls" >
					<label style="width:150px"><b>Country:</b></label>
					<select id="suCountry" class="required" style="width:300px">
							<option value=""></option>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<c:if test="${dic.value ne 'com.unitek' }">
								<option value="${dic.value}">${dic.label}</option>
							</c:if>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="control-group" style="height:30px;">
				<div class="controls" >
					<label style="width:150px"><b>Product Name:</b></label>
					<select id="suProductName"  class="required" style="width:300px" >
							<option value=""></option>
						<c:forEach items="${productMap}" var="productEntry">
							<option value="${productEntry.key}">${productEntry.key}</option>
						</c:forEach>
					</select>
				</div>
			</div>
			<div class="control-group" >
				<div class="controls" style="height:30px;">
					<label style="width:150px;"><b>Product Manager:</b></label>
					<span id="productManager"></span>
				</div>
			</div>
			<div class="control-group" style="height:80px;">
				<div class="controls">
				<label style="width:150px"><b>Suggestion Detail:</b></label>
					<textarea  class="required" id="suggestionDetail" style="width: 400px; margin: 0px 0px 10px; height: 70px;"></textarea>
				</div>
			</div>
			<div class="modal-footer">
				<button class="btn btn-primary"  type="button" id="saveSuggestion">Submit</button>
				<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
			</div>
		</div>
	</div>
	
	
	 <div id="followEmailDiv" class="modal hide fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true"> 
				 <form  id="followForm"  method="post" action="${ctx}/custom/emailManager/followEmail?id=${customEmail.id}">
						  <div class="modal-header">
						    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
						    <h3 id="myModalLabel">Follow</h3>
						  </div>
						  <div class="modal-body">
						        <label><b>Type:</b></label>
								<select id="followState" name="followState" style='width:130px' >
									<option value="0">Follow UP</option>
									<option value="1">Not Follow UP</option>
								</select>
								
								<span id='followDateSpan'>
								   <label><b>Reminder date:</b></label>
								   <input id='followDate' name='followDate' value='${fns:getDate('yyyy-MM-dd')}' style='width: 130px' onclick=WdatePicker({dateFmt:'yyyy-MM-dd'}) readonly='readonly'  class='Wdate' type='text'  class='input-small' />
								</span>
						  </div>
						   <div class="modal-footer">
						       <button class="btn btn-primary"  type="button" id="markState"><spring:message code="sys_but_save"/></button>
						       <button class="btn btn-primary" id="buttonClose" data-dismiss="modal" aria-hidden="true"><spring:message code="sys_but_closed"/></button>
						  </div> 
					</form>
			 </div>
</body>
</html>
