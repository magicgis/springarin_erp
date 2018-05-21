<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊召回订单</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">.sort{color:#0663A2;cursor:pointer;}</style>
	<script type="text/javascript">
		$(document).ready(function() {
			// 表格排序
			var orderBy = $("#orderBy").val().split(" ");
			$("#contentTable th.sort").each(function(){
				if ($(this).hasClass(orderBy[0])){
					orderBy[1] = orderBy[1]&&orderBy[1].toUpperCase()=="DESC"?"down":"up";
					$(this).html($(this).html()+" <i class=\"icon icon-arrow-"+orderBy[1]+"\"></i>");
				}
			});
			$("#contentTable th.sort").click(function(){
				var order = $(this).attr("class").split(" ");
				var sort = $("#orderBy").val().split(" ");
				for(var i=0; i<order.length; i++){
					if (order[i] == "sort"){order = order[i+1]; break;}
				}
				if (order == sort[0]){
					sort = (sort[1]&&sort[1].toUpperCase()=="DESC"?"ASC":"DESC");
					$("#orderBy").val(order+" DESC"!=order+" "+sort?"":order+" "+sort);
				}else{
					$("#orderBy").val(order+" ASC");
				}
				page();
			});
			
			$(".autoSubmit").change(function(){
				$("#searchForm").submit();
			});
			
			$(".open").click(function(e){
				if($(this).text()=='Summary'){
					$(this).text('Close');
				}else{
					$(this).text('Summary');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
			});
			
			$("a[rel='popover']").popover({trigger:'hover'});	
			
			$("#checkall").click(function(){
				 $('[name=checkId]:checkbox').each(function(){
				     if($(this).attr("disabled")!='disabled'){
				    	 this.checked=this.checked;
				     }else{
				    	 this.checked=false;
				     }
				 });
			});
			
			
			$("#updateState").click(function(){
				var ids = $("input:checkbox[name='checkId']:checked");
				if(!ids.length){
			    	$.jBox.tip('Please select data ！');
					return;
				}		
				var arr = new Array();
				for(var i=0;i<ids.length; i++){
					var id = ids[i].value;
					arr.push(id);
				}
				var idsAll = arr.join(',');
				top.$.jBox.confirm("确认更新这票运单到达？","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						   $("#delInvoice").attr("disabled","disabled");
						   $.post("${ctx}/amazoninfo/removalOrder/updateTrackState",{delIds:idsAll},function(date){
							   if(date=='0'){
								   $.jBox.tip('更新成功');
								   $("#searchForm").submit();
							   }else{
								   $.jBox.tip('更新失败');
							   }
							  
					       }); 
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			
			$("#aboutMe").click(function(){
				if(this.checked){
					$("#aboutMeVal").val('1');
				}else{
					$("#aboutMeVal").val('2');
				}
				$("#searchForm").submit();
			});
			
		});
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").attr("action","${ctx}/amazoninfo/removalOrder/returnOrder");
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/removalOrder/"><spring:message code='amazon_recall_order'/></a></li>
		<li class="active"><a href="${ctx}/amazoninfo/removalOrder/returnOrder">发货列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="amazonReturnOrderShipment" action="${ctx}/amazoninfo/removalOrder/returnOrder" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height:40px;line-height: 40px">
			<label><spring:message code='custom_event_form6'/>：</label><%--订单号 --%>
			    <input name="orderId" id="orderId" value="${amazonReturnOrderShipment.orderId}" maxlength="50" class="input-small" />
			    
			    ShipmentId：</label><%--订单号 --%>
			    <input name="shipmentId" id="shipmentId" value="${amazonReturnOrderShipment.shipmentId}" maxlength="50" class="input-small" />
			    
			    Sku：</label><%--订单号 --%>
			    <input name="shippedDate" id="shippedDate" value="${amazonReturnOrderShipment.shippedDate}" maxlength="50" class="input-small" />
				
				<label><spring:message code='amazon_order_form35'/>：</label><%--订单状态 --%>
				<select name="trackingState" id="trackingState" style="width: 120px" class="autoSubmit">
						<option value="" ${amazonReturnOrderShipment.trackingState eq ''?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<option value="1" ${amazonReturnOrderShipment.trackingState eq '1'?'selected':''}>到达</option>
						<option value="0" ${amazonReturnOrderShipment.trackingState eq '0'?'selected':''}>未到达</option>
				</select>&nbsp;&nbsp;
			    <input type="checkbox" id="aboutMe" ${'1' eq amazonReturnOrderShipment.trackingNumber?'checked':''}/>非UPS物流
				<input type="hidden" name="trackingNumber" id="aboutMeVal" value="${amazonReturnOrderShipment.trackingNumber}">
				
				
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>" onclick="return page();" />
			    <input id="updateState" class="btn btn-primary" type="button"  value="到达"/>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead>
			<tr>
			     <th style="width: 3%"><input type="checkBox" id="checkall"></th>
				<th>订单号</th>
				<th>ShipmentId</th>
				<th>发货日期</th>
				<th>是否到达</th>
				<th>物流链接</th>
				<th><spring:message code='sys_label_tips_operate'/></th><%--操作 --%>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="order">
				<tr>
				    <td><c:if test="${'1' ne order.trackingState}"><input type="checkBox" class="chebox" name="checkId" value="${order.id}"/></c:if></td>
					<td>${order.orderId}</td>
					<td>${order.shipmentId}</td>
					<td>${order.shippedDate}</td>
					<td>${'1' eq order.trackingState?'是':'否' }</td>
					<td>
					 <c:choose>
					     <c:when test="${fn:contains(order.tracking,'wwwapps.ups.com') }">
					        UPS
					     </c:when>
					     <c:when test="${fn:contains(order.tracking,'estes') }">
					        ESTES
					     </c:when>
					     <c:when test="${fn:contains(order.tracking,'fedex') }">
					        FEDEX
					     </c:when>
					      <c:when test="${fn:contains(order.tracking,'arcb') }">
					        ARCB
					     </c:when>
					      <c:when test="${fn:contains(order.tracking,'abfs') }">
					        ABFS
					     </c:when>
					     <c:otherwise>
					        OTHER
					     </c:otherwise>
					  </c:choose>
					  <a target='_blank' href='${order.tracking}'>跟踪</a>
					</td>
					<td>
						<input type="hidden" value="${order.id }"/>
						<a class="btn btn-small btn-info open">Summary</a>
					</td>
				</tr>
				<c:if test="${fn:length(order.items)>0}">
					<tr style="background-color:#D2E9FF;display: none" name="${order.id}">
						<td colspan='2'>SKU</td>
						<td>发货数量</td>
						<td colspan='4'>disposition</td>
					</tr>
					<c:forEach items="${order.items}" var="item">
						<tr style="background-color:#D2E9FF;display: none" name="${order.id}">
							<td colspan='2'>${item.sku}</td>
							<td>${item.quantityShipped}</td>
							<td colspan='4'>${item.disposition}</td>
						</tr>
					</c:forEach>
				</c:if>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>