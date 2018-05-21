<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>OrderManagement</title>
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
			$("#select1").change(function(){
				$("#searchForm").submit();
			});
			
			$(".expByCsv").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/ebay/order/expEbayOrderByCsv");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/ebay/order");
						top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$(".expByExcel").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/ebay/order/exportEbayOrder");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/ebay/order");
						top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
		});
		
// 		function exp(){
// 			loading('正在导出订单，请稍等...');
// 			var params = {};
// 			params.createdTime = $("#start").val();
// 			params.shippedTime = '${ebayOrder.shippedTime}';
// 			params.status = $("#select1").val();
// 			window.location.href = "${ctx}/ebay/order/exportfile";
// 		}
		
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/order/list"><spring:message code="amazon_order_tab1"/></a></li>
		<li ${ebayOrder.country eq 'de'?'class=active':''}><a href="${ctx}/ebay/order/list">DE Ebay List</a></li>
		<li ${ebayOrder.country eq 'com'?'class=active':''}><a href="${ctx}/ebay/order/list?country=com">US Ebay List</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="ebayOrder" action="${ctx}/ebay/order" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input name="country" type="hidden" value="${ebayOrder.country}"/>
		<label><spring:message code="amazon_order_tips3"/>：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createdTime" value="<fmt:formatDate value="${ebayOrder.createdTime}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="shippedTime" value="<fmt:formatDate value="${ebayOrder.shippedTime}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<spring:message code="ebay_order_status"/>：<select name="status" id="select1" style="width: 120px">
						<option value="" ${ebayOrder.status eq ''?'selected':''}><spring:message code="ebay_order_status_all"/></option>
						<option value="0" ${ebayOrder.status eq '0'?'selected':''}><spring:message code="ebay_order_status_nopay"/></option>
						<option value="1" ${ebayOrder.status eq '1'?'selected':''}><spring:message code="ebay_order_status_ispay"/></option>
						<option value="2" ${ebayOrder.status eq '2'?'selected':''}><spring:message code="ebay_order_status_shipped"/></option>
				</select>&nbsp;&nbsp;	
		<label><spring:message code="amazon_order_tips1"/>/Receiver：</label><form:input path="orderId" htmlEscape="false" maxlength="50" class="input-small"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
		&nbsp;<div class="btn-group">
				<button type="button" class="btn btn-primary"><spring:message code="sys_but_export"/></button>
					 <button type="button" class="btn btn-primary dropdown-toggle"  data-toggle="dropdown">
					     <span class="caret"></span>
					     <span class="sr-only"></span>
					 </button>
					 <ul class="dropdown-menu" >
				 		<li><a class="expByExcel">Excel</a></li>
				 		<li><a class="expByCsv">CSV</a></li>
					 </ul>
				</div>	
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
		           <th style="width: 60px">ID</th>	
				   <th style="width: 60px"><spring:message code="amazon_order_form_tab_tips4"/></th>	
				   <th style="width: 200px"><spring:message code="amazon_order_form1"/></th>
				   <th style="width: 100px"><spring:message code="amazon_order_form2"/></th>
				   <th style="width: 80px"><spring:message code="amazon_order_form5"/></th>
				   <th style="width: 220px"><spring:message code="amazon_order_form9"/></th>
				   <th style="width: 250px"><spring:message code="amazon_order_form10" /></th>
				   <th><spring:message code="ebay_order_status"></spring:message></th>
				   <th>Operate</th>
				   </tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="ebayOrder">
			<tr>
			    <td rowspan="2" style="text-align: center;vertical-align: middle;"><b>
				   ${ebayOrder.id}</b></td>
				<td rowspan="2" style="text-align: center;vertical-align: middle;"><b>
				   <c:if test='${not empty ebayOrder.invoiceNo}'>${ebayOrder.invoiceNo}</c:if>
				  </b></td>
				<td><a href="${ctx}/ebay/order/form?id=${ebayOrder.id}">${ebayOrder.orderId}</a></td>
				<td><fmt:formatDate pattern="yyyy-M-d H:mm" value="${ebayOrder.createdTime}"/></td>
				<td>${ebayOrder.total}</td>
				<td>${ebayOrder.buyerUserId}</td>
				<td><a href="${ctx}/custom/sendEmail/form?sendEmail=${ebayOrder.buyerEmail}" >${ebayOrder.encryptionBuyerEmail}</a></td>
				<td>${ebayOrder.statusW}</td>
				<td><%-- <c:if test="${'com' eq ebayOrder.country&&'1' eq ebayOrder.status}"><a target='_black' class='btn btn-success' href='${ctx}/amazoninfo/amazonTestOrReplace/createEbayEvent?amazonOrderId=${ebayOrder.orderId}&country=com'>Fulfillment</a></c:if> --%>
				   <c:if test="${'com' eq ebayOrder.country&&'2' eq ebayOrder.status&&fns:getDateByPattern(ebayOrder.createdTime,'yyyyMMdd')>='20161109'}"><a target='_black' class='btn btn-warning' href='${ctx}/amazoninfo/amazonTestOrReplace/view?sellerOrderId=DZW-${ebayOrder.orderId}'>FulfillmentView</a></c:if>
				</td>
			</tr>
			<tr>
				<td colspan="8"><spring:message code="amazon_order_form_tab_tips6"/><br/>
					<c:forEach items="${ebayOrder.items}" var="item">
						  EbayOrderNo: ${item.sellingmanagersalesrecordNumber} itemId:<a target='_blank' href='https://www.ebay.${ebayOrder.country}/itm/${item.itemId}'>${item.itemId}</a> Sku:<b style="font-size: 14px">${item.sku}</b>;<spring:message code="amazon_order_form23"/>:${item.quantityPurchased};<spring:message code="amazon_order_form24"/>:${item.transactionPrice}<br/>
					</c:forEach>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
