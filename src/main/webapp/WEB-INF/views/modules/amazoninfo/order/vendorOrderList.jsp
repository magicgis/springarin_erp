<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>vendor订单</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
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
			 $("a[rel='popover']").popover({trigger:'hover'});
			 
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			$(".open").click(function(e){
				if($(this).text()=='Summary'){
					$(this).text('close');
				}else{
					$(this).text('Summary');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
			});
			
			
			$("#checkall").click(function(){
				 $('[name=checkId]:checkbox').each(function(){
				     if($(this).attr("disabled")!='disabled'){
				    	 this.checked=this.checked;
				     }else{
				    	 this.checked=false;
				     }
				 });
			});
			
			$("#deliveryList").click(function(){
				$("#searchForm").attr("action","${ctx}/amazoninfo/vendorOrder/showDeliveryOrder");
				$("#start").val('');
				$("#end").val('');
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/amazoninfo/vendorOrder/list");
			});
			
			$(".countryHref").click(function(){
				$("#country").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
		
			$("#btnExport").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/vendorOrder/expUnconfirmedOrder");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/vendorOrder/unconfirmedOrder?country=${vendorOrder.country}");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			
			
		});	
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
	    	return false;
	    }
		
		function refreshForm(){
			$("#searchForm").submit();
		}
		
	</script>
</head>
<body>
    <ul class="nav nav-tabs">
		<li  class="active"><a href="#">${fns:getDictLabel(vendorOrder.country,'platform','')} Unconfirmed Order List</a></li>	
		<li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">DE Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a  class="countryHref" href="${ctx}/amazoninfo/vendorOrder/list?country=de" key="de" >DE Order List</a></li>	
				   <li><a  href="${ctx}/amazoninfo/vendorOrder/returnList?country=de?">DE Return List</a></li> 
				   <li><a  href="${ctx}/amazoninfo/vendorOrder/showDeliveryOrder?country=de">DE Delivery List</a></li> 
				   <li><a  href="${ctx}/amazoninfo/vendorOrder/unconfirmedOrder?country=de">DE Unconfirmed Order List</a></li> 
		    </ul>
	    </li>
	    
	    <li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">US Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a  class="countryHref" href="${ctx}/amazoninfo/vendorOrder/list?country=com" key="com" >US Order List</a></li>	
				 <%--   <li><a  href="${ctx}/amazoninfo/vendorOrder/returnList?country=com?">US Return List</a></li>  --%>
				   <li><a  href="${ctx}/amazoninfo/vendorOrder/showDeliveryOrder?country=com">US Delivery List</a></li> 
				   <li><a  href="${ctx}/amazoninfo/vendorOrder/unconfirmedOrder?country=com">US Unconfirmed Order List</a></li> 
		    </ul>
	    </li>
	    
	    <li class="dropdown"  >
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">UK Order<b class="caret"></b> </a>
		    <ul class="dropdown-menu" style="min-width:110px">
		           <li><a  class="countryHref" href="${ctx}/amazoninfo/vendorOrder/list?country=uk" key="uk" >UK Order List</a></li>	
				   <%-- <li><a  href="${ctx}/amazoninfo/vendorOrder/returnList?country=uk?">UK Return List</a></li>  --%>
				   <li><a  href="${ctx}/amazoninfo/vendorOrder/showDeliveryOrder?country=uk">UK Delivery List</a></li> 
				   <li><a  href="${ctx}/amazoninfo/vendorOrder/unconfirmedOrder?country=uk">UK Unconfirmed Order List</a></li> 
		    </ul>
	    </li>
	    
	<%-- 	<li  class="${vendorShipment.country  eq 'de' ?'active':''}"><a class="countryHref" href="#" key="de" >DE Vendor List</a></li>
		<li  class="${vendorShipment.country  eq 'com' ?'active':''}"><a class="countryHref" href="#" key="com">US Vendor List</a></li>
		<li  class="${vendorShipment.country  eq 'uk' ?'active':''}"><a class="countryHref" href="#" key="uk">UK Vendor List</a></li>
	    
		 --%>
	</ul> 
	<form:form id="searchForm" modelAttribute="vendorOrder" action="${ctx}/amazoninfo/vendorOrder/unconfirmedOrder" method="post" class="breadcrumb form-search" cssStyle="height: 40px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="country" name="country" type="hidden" value="${vendorOrder.country}" />
		<div style="height: 30px;line-height: 30px">
			<div>
				<label><strong>OrderId：</strong></label><form:input path="orderId" htmlEscape="false" maxlength="50" class="input-small"/>
				<label><strong>SKU/Asin：</strong></label><form:input path="status" htmlEscape="false" maxlength="50" class="input-small"/>
				<input  class="btn btn-primary" type="submit" value="Search"/> 
				<input id="btnExport" class="btn btn-primary" type="button" value="Export"/> 
			</div>
		</div>
		
	</form:form>
	<table id="contentTable" class="table  table-bordered table-condensed">
		<thead><tr>
				    <th>orderId</th>
				    <th>orderDate</th>
				    <th>deliveryWindow</th>
				    <th>shipToLocation</th>
				    <th>submittedCost</th>
				    <th>operate</th>
			 </tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="vendor"  varStatus="k">
		   <tr>
				<td><c:if test="${'1' eq vendor.qtyFlag}"><b style='color:red'>!</b></c:if>${vendor.orderId}</td>
				<td><fmt:formatDate value="${vendor.orderedDate}" pattern="yyyy-MM-dd"/> </td>
				<td>${vendor.deliveryWindow}</td>
				<td>${vendor.shipToLocation}</td>
				<td>${vendor.submittedTotalCost}</td>
				<td><input type="hidden" value="${vendor.id}"/>
				    <a class="btn btn-small btn-info open">概要</a>
				</td>
			</tr>
			<tr style="background-color:#D2E9FF;display: none" name="${vendor.id}">
			    <td>Name</td><td>Asin</td><td>Sku</td><td>unitPrice</td><td>submittedQuantity</td><td>warehouseQuantity</td>
			</tr>
				<c:forEach items="${vendor.items}" var="item">
				        <tr  style="background-color:#D2E9FF;display: none" name="${vendor.id}" class="${vendor.id}">
				            <td>${item.productName }<c:if test="${item.submittedQuantity >item.stockQty }"><span class="label label-warning">Out of stock</span></c:if></td>
				            <td>${item.asin }</td>
				            <td>${item.sku }</td>
				            <td>${item.unitPrice }</td>
				            <td>${item.submittedQuantity }</td>
				            <td>${item.stockQty }</td>
				        </tr>
				</c:forEach>
		
			
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
