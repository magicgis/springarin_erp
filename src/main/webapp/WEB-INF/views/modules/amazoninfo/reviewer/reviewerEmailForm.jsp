<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Reviewer Email Manager</title>
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
			
			$("#unsubscribe").click(function(event){
				event.preventDefault();
				$.get($(this).attr("href"),function(data){
					if(data){
						top.$.jBox.tip("Email has been added to the unsubscribe list!");
					}
				});
			});
			
			$("#singleRemark").on("blur",function(){
				var params = {};
				if($("#singleRemark").val()){
					params.id='${emailManager.id}';
					params.remark = encodeURI($("#singleRemark").val());
					$.get("${ctx}/amazoninfo/reviewerEmail/updateRemark?"+$.param(params),function(data){
						if(!(data)){    
							top.$.jBox.tip("Save remark fail！", 'info',{timeout:2000});
						}else{
							top.$.jBox.tip("Save remark success", 'info',{timeout:2000});
						}
					});
				}
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
						params.id = '${reviewerEmail.id}'; 
						params.userId = userId;
						window.location.href = "${ctx}/amazoninfo/reviewerEmail/transmitOther?"+$.param(params);
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$(".noreply").click(function(){
				if(!(top)){
					top = self; 
				}
				top.$.jBox.confirm("Determine that the message does not need to deal with it?</br>Remark:<input id='remarks' type='text' style='width:200px'/>",'Prompted',function(v,h,f){
					if(v=='ok'){
						loading('Please wait a moment!');
						var params = {};
						params.id = '${reviewerEmail.id}'; 
						params.state = '4';
						params.remarks=encodeURI($.trim(h.find("#remarks").val()));
						window.location.href = "${ctx}/amazoninfo/reviewerEmail/noreply?"+$.param(params);
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$(".reply").click(function(){
				var params = {};
				params.id = '${reviewerEmail.id}'; 
				window.location.href = "${ctx}/amazoninfo/reviewerEmail/reply?"+$.param(params);
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
			
			$(".support").click(function(){
				var params = {};
				<c:if test="${fn:length(orders)>0}">
					params.orderId = '${orders[0].amazonOrderId}'; 
				</c:if>
				params.emailId = '${reviewerEmail.id}'; 
				params.support = '8';
				window.location.href = "${ctx}/custom/event/editView?"+$.param(params);
			});
			
			
			$(".support2").click(function(){
				var params = {};
				params.eventType = '8';
				params.country='${reviewerEmail.formReviewer.country}';
				window.location.href = "${ctx}/amazoninfo/amazonTestOrReplace/add?"+$.param(params);
			});
			
			$("#saveProblem").on("click",function(){
					var param = {};
					param.country=$("#country").val();
					param.productName=$("#productName").val();
					param.problemType=$("#problemType").val();
					param.problem=encodeURI($("#problemDetail").val());
					param.id='${reviewerEmail.id}';
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
					
					$.get("${ctx}/amazoninfo/reviewerEmail/saveProblem?"+$.param(param,true),function(){});
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
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/reviewerEmail/"><spring:message code='custom_email_list'/></a></li>
		<li class="active"><a href="${ctx}/amazoninfo/reviewerEmail/form?id=${emailManager.id}"><spring:message code='custom_email_reply'/></a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="reviewerEmail" action="${ctx}/amazoninfo/reviewerEmail/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<tags:message content="${message}"/>
		<div class="control-group">
		   <div style="float: left;">
			<input class="btn btn-primary reply" type="button" value='<spring:message code="custom_email_btn5" />'/>&nbsp;
			<input class="btn btn-primary noreply" type="button" value="<spring:message code='custom_email_btn1'/>"/>&nbsp;
			<input class="btn" type="button" value="<spring:message code='sys_but_back'/>" onclick="history.go(-1)"/>
			<%-- 事件关联 --%>
		<!-- 	&nbsp;&nbsp;
			<input class="btn btn-warning support" type="button" value="ReviewEvent"/>&nbsp; -->
				&nbsp;&nbsp;
			<input class="btn btn-warning support2" type="button" value="Fulfillment"/>&nbsp;
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Remark:<input type="text" id="singleRemark" value="${emailManager.remarks}" style="width:200px"/>   
			<%-- 编辑问题
			&nbsp;&nbsp;&nbsp;
			<input class="btn btn-warning " id="problem" type="button" value="Edit Problem"/>&nbsp;
			 --%>
			&nbsp;&nbsp;&nbsp;
			<br/><span class="help-inline"><spring:message code='custom_email_tips'/></span>
			</div>
			<div style="float: right;">
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
		<c:if test="${not empty reviewerEmail.transmit}">
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form13'/></label>
			<div class="controls">
				${reviewerEmail.transmit}
			</div>
		</div>
		</c:if>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form1'/></label>
			<div class="controls">
				${reviewerEmail.revertEmail}
				<a id="unsubscribe" href="${ctx}/custom/emailManager/unsubscribeEmail?customEmail=${reviewerEmail.revertEmail}" style="font-size: 18px">[Add To UnsubscribeEmail List]</a>
				<span class="help-inline">Click on the link, the system will not automatically send e-mail to guests</span>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form3'/></label>
			<div class="controls">
				${reviewerEmail.subject}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label"><spring:message code='custom_email_form14'/></label>
			<div class="controls">
				<fmt:formatDate type="both" value="${reviewerEmail.customSendDate}"/>
			</div>
		</div>
		<!-- 事件信息 -->
		<div class="problemDiv" style="display:${ empty reviewerEmail.problemType?'none':'block'}">
			<div class="control-group">
				<label class="control-label">product Name</label>
				<div class="controls">
					<span class="productName">${reviewerEmail.productName}</span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">country</label>
				<div class="controls">
					<span class="country">${reviewerEmail.country}</span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">problem type</label>
				<div class="controls">
					<span class="problemType">${reviewerEmail.problemType}</span>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label">problem detail</label>
				<div class="controls">
					<span class="problemDetail">${reviewerEmail.problem}</span>
				</div>
			</div>
		</div>
		<!-- 订单信息 -->
		<c:if test="${fn:length(orders)>0}">
			<blockquote>
				<p style="font-size: 14px"><spring:message code='custom_email_tips1'/></p>
				<c:forEach items="${orders}" var="order">
						<div class="control-group" style="border-bottom-color: blue">
							<label class="control-label"><spring:message code='amazon_order_form1'/></label>
							<div class="controls">
								<a target="_blank" href="${ctx}/amazoninfo/order/form?amazonOrderId=${order.amazonOrderId}">${order.amazonOrderId}</a>
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
		<div class="control-group">${reviewerEmail.receiveContent}</div>
		<c:if test="${not empty reviewerEmail.attchmentPath || not empty reviewerEmail.inlineAttchmentPath}">
			<div class="control-group">
				<label class="control-label">Attchment</label>
				<div class="controls">
					<c:forEach items="${fn:split(reviewerEmail.attchmentPath,',')}" var="attchment">
						<a href="${ctx}/amazoninfo/reviewerEmail/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
						&nbsp;&nbsp;&nbsp;  
					</c:forEach>
					<c:forEach items="${fn:split(reviewerEmail.inlineAttchmentPath,',')}" var="attchment">
						<a href="${ctx}/amazoninfo/reviewerEmail/download?fileName=${attchment}">${fns:substringAfterLast(attchment,"/")}</a>
						&nbsp;&nbsp;&nbsp;  
					</c:forEach>
				</div>
			</div>
		</c:if>
		<div class="control-group">
		 
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
		
</body>
</html>
