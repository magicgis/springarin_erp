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
		});
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").attr("action","${ctx}/amazoninfo/removalOrder/");
			$("#searchForm").submit();
        	return false;
        }
		
        function exportQty(){
            window.location.href = "${ctx}/amazoninfo/removalOrder/export";
            top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/amazoninfo/removalOrder/"><spring:message code='amazon_recall_order'/></a></li>
	    <li><a href="${ctx}/amazoninfo/removalOrder/returnOrder">发货列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="amazonRemovalOrder" action="${ctx}/amazoninfo/removalOrder/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height:80px;line-height: 40px">
			<label><spring:message code='custom_event_form6'/>：</label><%--订单号 --%>
			<input name="amazonOrderId" id="amazonOrderId" value="${amazonRemovalOrder.amazonOrderId}" maxlength="50" class="input-small" />
				<label><spring:message code='amazon_order_form4'/>：</label><%--平台 --%>
				<select name="country" id="country" style="width: 120px" class="autoSubmit">
					<option value="" ${amazonRemovalOrder.country eq ''?'selected':''}><spring:message code="amazon_order_tips4"/></option>
					<option value="de" ${amazonRemovalOrder.country eq 'de'?'selected':''}>欧洲|EU</option>
					<option value="com" ${amazonRemovalOrder.country eq 'com'?'selected':''}>美国|US</option>
					<option value="ca" ${amazonRemovalOrder.country eq 'ca'?'selected':''}>加拿大|CA</option>
					<option value="jp" ${amazonRemovalOrder.country eq 'jp'?'selected':''}>日本|JP</option>
				</select>&nbsp;&nbsp;
				<label><spring:message code='amazon_order_form35'/>：</label><%--订单状态 --%>
				<select name="orderStatus" id="orderStatus" style="width: 120px" class="autoSubmit">
						<option value="" ${amazonRemovalOrder.orderStatus eq ''?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<option value="Pending" ${amazonRemovalOrder.orderStatus eq 'Pending'?'selected':''}  >Pending</option>
						<option value="Completed" ${amazonRemovalOrder.orderStatus eq 'Completed'?'selected':''}  >Completed</option>
						<option value="Cancelled" ${amazonRemovalOrder.orderStatus eq 'Cancelled'?'selected':''}  >Cancelled</option>
				</select>&nbsp;&nbsp;
			<div>
				<label>Sku or Product Name：</label><%--SKU或者产品名 --%>
				<input name="source" id="source" value="${amazonRemovalOrder.source}" maxlength="50" class="input-media" />
				<label><spring:message code='amazon_product_type'/>：</label><%--类型 --%>
				<select name="orderType" id="orderType" style="width: 120px" class="autoSubmit">
						<option value="" ${amazonRemovalOrder.orderType eq ''?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<option value="Return" ${amazonRemovalOrder.orderType eq 'Return'?'selected':''}  >Return</option>
						<option value="Liquidate" ${amazonRemovalOrder.orderType eq 'Liquidate'?'selected':''}  >Liquidate</option>
						<option value="Disposal" ${amazonRemovalOrder.orderType eq 'Disposal'?'selected':''}  >Disposal</option>
				</select>&nbsp;&nbsp;
				<input type="checkbox" name="inStorage" id="inStorage" value="1" class="autoSubmit" ${inStorage eq '1'?'checked':''}/>
				<spring:message code='amazon_recall_pending'/><%--待入库 --%>
				&nbsp;
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>" onclick="return page();" />
				<input id="btnExportSum" onClick="exportQty()" class="btn btn-primary" type="button" value="导出美国召回产品明细"/>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead>
			<tr>
				<th class="sort country"><spring:message code='amazon_order_form4'/></th><%--平台 --%>
				<th class="sort amazonOrderId"><spring:message code='custom_event_form6'/></th><%--订单号 --%>
				<th class="sort purchaseDate"><spring:message code='amazon_order_tips3'/></th><%--订单时间 --%>
				<th class="sort lastUpdateDate"><spring:message code='amazon_sales_data_updating_time'/></th><%--数据最后更新时间 --%>
				<th class="sort orderStatus"><spring:message code='amazon_order_form35'/></th><%--订单状态 --%>
				<th class="sort orderType"><spring:message code='amazon_product_type'/></th><%--类型 --%>
				<th colspan="3"><spring:message code='sys_label_tips_operate'/></th><%--操作 --%>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="order">
				<tr>
					<td>${'de' eq order.country?'欧洲|EU':fns:getDictLabel(order.country,'platform','')}</td>
					<td>${order.amazonOrderId}</td>
					<td><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${order.purchaseDate}"/></td>
					<td><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${order.lastUpdateDate}"/></td>
					<td>${order.orderStatus}</td>
					<td>${order.orderType}</td>
					<td colspan="3">
						<input type="hidden" value="${order.id }"/>
						<a class="btn btn-small btn-info open">Summary</a>
						<shiro:hasPermission name="amazon:removal:edit">
						   <c:if test="${'Return' eq order.orderType && '1' eq order.canStore }">
							   <a class="btn btn-success btn-small" href="${ctx}/amazoninfo/removalOrder/stored?id=${order.id}">In Bound</a>
						    </c:if>
						</shiro:hasPermission>
					</td>
				</tr>
				<c:if test="${fn:length(order.items)>0}">
					<tr style="background-color:#D2E9FF;display: none" name="${order.id}">
						<td>Product Name</td>
						<td>SKU</td>
						<td>requestedQty</td>
						<td>completedQty
						<c:if test="${'com' eq order.country }">/deliveredQty</c:if>
						</td>
						<td>cancelledQty</td>
						<td>inProcessQty</td>
						<td>storedQty</td>
						<td>removalFee(${'com' eq order.country?'USD':'ca' eq order.country?'CAD':'jp' eq order.country?'JPY':'EUR' })</td>
						<shiro:hasPermission name="amazoninfo:profits:view">
						<td>cost(€)</td>
						</shiro:hasPermission>
					</tr>
					<c:forEach items="${order.items}" var="item">
						<c:if test="${fn:containsIgnoreCase(item.productName, amazonRemovalOrder.source) || fn:containsIgnoreCase(item.sellersku, amazonRemovalOrder.source)}">
						<tr style="background-color:#D2E9FF;display: none" name="${order.id}">
							<td>${item.productName}</td>
							<td>
								<c:if test="${'com' ne order.country }">${item.sellersku}</c:if>
								<c:if test="${'com' eq order.country }">
									<a title="物流信息" target="_blank" href="${ctx}/amazoninfo/removalOrder/returnOrder?orderId=${order.amazonOrderId}&shippedDate=${item.sellersku}">${item.sellersku}</a>
								</c:if>
							</td>
							<td>${item.requestedQty}</td>
							<td>${item.completedQty}<c:if test="${'com' eq order.country }">&nbsp;/&nbsp;${item.deliveredQty}</c:if></td>
							<td>${item.cancelledQty}</td>
							<td>${item.inProcessQty}</td>
							<td>${item.storedQty}</td>
							<td>${item.removalFee}</td>
							<shiro:hasPermission name="amazoninfo:profits:view">
							<td>
								<fmt:formatNumber pattern="#,##0.##" value="${item.buyCost}"  maxFractionDigits="2" />
							</td>
							</shiro:hasPermission>
						</tr>
						</c:if>
					</c:forEach>
				</c:if>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>