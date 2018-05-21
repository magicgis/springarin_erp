<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>vendor return订单</title>
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
			 
			
			$(".open").click(function(e){
				if($(this).text()=='Summary'){
					$(this).text('close');
				}else{
					$(this).text('Summary');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
			});
			
			
			$("#btnExport").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/vendorOrder/exportReturns");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/vendorOrder/returnList");
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
		<li><a class="countryHref" href="${ctx}/amazoninfo/vendorOrder/list?country=de" >DE Vendor List</a></li>
		<li  class="active"><a  href="${ctx}/amazoninfo/vendorOrder/returnList" >DE Return List</a></li>
		
	</ul> 
	<form:form id="searchForm" modelAttribute="vendorReturns" action="${ctx}/amazoninfo/vendorOrder/returnList" method="post" class="breadcrumb form-search" cssStyle="height: 40px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>

		<div style="height: 30px;line-height: 30px">
			<div>
			<label><strong>CreateDate：</strong></label>
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createTime" value="<fmt:formatDate value="${vendorReturns.createTime}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
		    &nbsp;-&nbsp;
		    <input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="queryTime" value="<fmt:formatDate value="${vendorReturns.queryTime}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			&nbsp;&nbsp;
		
			&nbsp;&nbsp;<input  class="btn btn-primary" type="submit" value="Search"/> 
			
		 	&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="<spring:message code="sys_but_export"/>"/>
		   
			</div>
		</div>
		
	</form:form>
	<table id="contentTable" class="table  table-bordered table-condensed">
		<thead><tr>
				   <th style="width: 10%">request_id</th>
				   <th style="width: 10%">request_type</th>
				   <th style="width: 12%">return_reason</th>
				   <th style="width: 5%">status</th>
				   <th style="width: 5%">requested_quantity</th>
				   <th style="width: 5%">approved_quantity</th>
				   <th style="width: 5%">requested_refund</th>
				   <th style="width: 5%">approved_refund</th>
				   <th style="width: 5%">total_cost</th>
				   <th style="width: 10%">warehouse</th>
				   <th style="width: 20%">create_date</th>
				   <th style="width: 10%">Operate</th>
			 </tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="vendor"  varStatus="k">
		   
		   <tr>
			 <td>${vendor.requestId }</td>
			 <td>${vendor.requestType}</td>
			 <td>${vendor.returnReason}</td>
			 <td>${vendor.status}</td>
			 <td>${vendor.requestedQuantity}</td>
			 <td>${vendor.approvedQuantity}</td>
			 <td>${vendor.requestedRefund}</td>
			 <td>${vendor.approvedRefund}</td>
			 <td>${vendor.totalCost}</td>
			 <td>${vendor.warehouse}</td>
			 <td><fmt:formatDate value="${vendor.queryTime}" pattern="yyyy-MM-dd" /></td>
			 <td>
			    <input type="hidden" value="${vendor.id }"/>
				<a class="btn btn-small btn-info open">Summary</a>
			 </td>
			<tr>
			
			<c:if test="${fn:length(vendor.items)>0}">
				   <tr style="background-color:#D2E9FF;display: none" name="${vendor.id}">
					    <td colspan='2'>request_item_id</td><td colspan='3'>product_name</td><td>asin</td><td>ean</td>
					    <td>requested_quantity</td><td>approved_quantity</td><td>requested_refund</td>
				        <td>approved_refund</td> <td>total_cost</td>
	               </tr>
				  <c:forEach items="${vendor.items}" var="item"  varStatus="k">
						<tr style="background-color:#D2E9FF;display: none" name="${vendor.id}">
						    <td colspan='2'>${item.requestItemId}</td>
							<td colspan='3'><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${item.productName}" target='_blank'>${item.productName}</a></td>
							<td>${item.asin}</td>
							<td>${item.ean}</td>
							<td>${item.requestedQuantity}</td>
							<td>${item.approvedQuantity}</td>
							<td>${item.requestedRefund}</td>
							<td>${item.approvedRefund}</td>
							<td>${item.totalCost}</td>
						</tr> 
				</c:forEach>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
