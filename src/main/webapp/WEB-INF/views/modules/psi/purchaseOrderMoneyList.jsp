<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
	<title>采购订单资金视图</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
		$(document).ready(function() {
			$("#treeTable").treeTable({expandLevel : 1});
			$("#orderSta,#supplier").change(function(){
				$("#searchForm").submit();
			})
		});
		
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
		<li><a href="${ctx}/psi/purchasePayment/">采购付款列表</a></li>
		<shiro:hasPermission name="psi:payment:edit">
			<li><a href="${ctx}/psi/purchasePayment/add">新建采购付款</a></li>
		</shiro:hasPermission>
		<li class="active"><a href="#">采购订单资金列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="purchaseOrder" action="${ctx}/psi/purchaseOrder/moneyView" method="post" class="breadcrumb form-search" cssStyle="height: 80px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 100px;line-height: 40px">
			<div style="height: 40px;">
			<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${purchaseOrder.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="purchaseDate" value="<fmt:formatDate value="${purchaseOrder.purchaseDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				
			<label>订单编号/产品名称：</label>
			<form:input path="orderNo" htmlEscape="false" maxlength="50" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			</div>
			<div style="height: 40px;">
			<label>供应商：</label>
			<select style="width:150px;" id="supplier" name="supplier.id">
				<option value="" ${purchaseOrder.supplier eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
				<c:forEach items="${suppliers}" var="supplier" varStatus="i">
					 <option value='${supplier.id}'>${supplier.nikename}</option>;
				</c:forEach>
			</select>
			<script type="text/javascript">
			$("option[value='${purchaseOrder.supplier.id}']").attr("selected","selected");	
			</script>
			&nbsp;&nbsp;&nbsp;&nbsp;
			<label>订单状态：</label>
			<form:select path="orderSta" style="width: 200px" id="orderSta">
				<option value="" >全部</option>
				<option value="0" ${purchaseOrder.orderSta eq '0' ?'selected':''} >草稿</option>
				<option value="1" ${purchaseOrder.orderSta eq '1' ?'selected':''} >已审核</option>
				<option value="2" ${purchaseOrder.orderSta eq '2' ?'selected':''} >生产</option>
				<option value="3" ${purchaseOrder.orderSta eq '3' ?'selected':''} >部分收货</option>
				<option value="4" ${purchaseOrder.orderSta eq '4' ?'selected':''} >已收货</option>
				<option value="5" ${purchaseOrder.orderSta eq '5' ?'selected':''} >已完成</option>
				<option value="6" ${purchaseOrder.orderSta eq '6' ?'selected':''} >已取消</option>
			</form:select>
			&nbsp;&nbsp;&nbsp;&nbsp;
			</div>
		</div>
		
	</form:form>
	<tags:message content="${message}"/>   
	
	
	
	<table id="treeTable" class="table table-bordered table-condensed">
		<thead><tr><th width="5%">序号</th><th width="10%">订单编号</th><th width="10%">供应商</th><th width="5%">货币类型</th><th width="5%">总款</th><th width="5%">定金比例</th><th width="6%">已付定金</th><th width="6%">已付总金额</th><th width="10%">支付状态</th><th width="10%">订单状态</th><th width="100px">创建日期</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="purchaseOrder" varStatus="i">
			<tr id="${purchaseOrder.id}">
				<td>${purchaseOrder.id}</td>
				<td><a href="${ctx}/psi/purchaseOrder/view?id=${purchaseOrder.id}">${purchaseOrder.orderNo}</a></td>
				<td>${purchaseOrder.supplier.nikename}</td>
				<td>${purchaseOrder.currencyType}</td>
				<td>${purchaseOrder.totalAmount}</td>
				<td>${purchaseOrder.deposit}%</td>
				<td>${purchaseOrder.depositAmount}</td>
				<td>${purchaseOrder.totalPaymentAmount}</td>
				<td>
				<c:if test="${purchaseOrder.totalAmount ne purchaseOrder.totalPaymentAmount }"><span class="label label-important">未完成</span></c:if>
				<c:if test="${purchaseOrder.totalAmount eq purchaseOrder.totalPaymentAmount }"><span class="label  label-success">已完成</span></c:if>
				</td>
				<td align="center">
				
					<c:if test="${purchaseOrder.orderSta eq '0'}"><span class="label label-important">草稿</span></c:if>
					<c:if test="${purchaseOrder.orderSta eq '1'}"><span class="label " style="background-color:#DCB5FF">已审核</span></c:if>
					<c:if test="${purchaseOrder.orderSta eq '2'}"><span class="label label-warning">生产</span></c:if>
					<c:if test="${purchaseOrder.orderSta eq '3'}"><span class="label label-info">部分收货</span></c:if>
					<c:if test="${purchaseOrder.orderSta eq '4'}"><span class="label" style="background-color:#00E3E3">已收货</span></c:if>
					<c:if test="${purchaseOrder.orderSta eq '5'}"><span class="label  label-success">已完成</span></c:if>
					<c:if test="${purchaseOrder.orderSta eq '6'}"><span class="label  label-inverse">已取消</span></c:if>
				</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${purchaseOrder.createDate}"/></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
