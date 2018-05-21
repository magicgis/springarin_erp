<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>运单管理</title>
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
			$("#productId,#type").change(function(){
				$("#searchForm").submit();
			});
			
			$("#expByMonth").click(function(){
				var params = {};
				params.startDate=$("input[name='startDate']").val();    
				params.endDate=$("input[name='endDate']").val();    
				window.location.href = "${ctx}/psi/psiTransportOrder/expByMonth?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
		});			
	</script>
	
</head>

<style>
	table thead tr th{
		margin:auto;
		text-align:center
	}
	</style>
<body>
	<form:form id="searchForm" modelAttribute="psiTransportOrder" action="${ctx}/psi/psiTransportOrder/byMonth" method="post" class="breadcrumb form-search" cssStyle="height: 50px;">
	<div>
		<label>产品：</label>
		<select name="productId" id="productId" style="width:200px">
			<c:forEach items="${products}" var="pro">
				<option value="${pro.id}" ${productId eq pro.id ?'selected':'' }>${pro.name}</option>
			</c:forEach>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<label>类型：</label>
		<select name="type" id="type" style="width:120px">
			<option value="0" ${type eq '0'?'selected':''}>体积</option>
			<option value="1" ${type eq '1'?'selected':''}>重量</option>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<label>新建日期：</label>
		<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="startDate" value="${startDate}"  class="input-small" id="start"/>
		&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="${endDate}" id="end" class="input-small"/>
		&nbsp;&nbsp;&nbsp;&nbsp;<input id="expByMonth" class="btn btn-success" type="button" value="导出所有产品统计"/>
	</div>
	</form:form>
		<blockquote  style="float:left;">
			<div style="float: left;"><p style="font-size: 14px">单产品按月份、运输类型、目的地统计体积重量</p></div>
		</blockquote>
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
			    <th rowspan="2" style="width: 5%;text-align:center">月份</th>
			   <th colspan="4" style="width: 19%;text-align:center">EU</th>
			   <th colspan="4" style="width: 19%;text-align:center">US</th>
			   <th colspan="4" style="width: 19%;text-align:center">JP</th>
			   <th colspan="4" style="width: 19%;text-align:center">CA</th>
			   <th colspan="4" style="width: 19%;text-align:center">MX</th>
			</tr>
			<tr>
			  <th style="text-align:center">空运</th>
			   <th style="text-align:center">海运</th>
			   <th style="text-align:center">快递</th>
			   <th style="text-align:center">铁路</th>
			   <th style="text-align:center">空运</th>
			   <th style="text-align:center">海运</th>
			   <th style="text-align:center">快递</th>
			    <th style="text-align:center">铁路</th>
			   <th style="text-align:center">空运</th>
			   <th style="text-align:center">海运</th>
			   <th style="text-align:center">快递</th>
			    <th style="text-align:center">铁路</th>
			   <th style="text-align:center">空运</th>
			   <th style="text-align:center">海运</th>
			   <th style="text-align:center">快递</th>
			    <th style="text-align:center">铁路</th>
			    <th style="text-align:center">空运</th>
			   <th style="text-align:center">海运</th>
			   <th style="text-align:center">快递</th>
			   <th style="text-align:center">铁路</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${monthMap}" var="monthEntry">
					<c:set  var="euTotal" value="${monthEntry.value['EU']['AE']+monthEntry.value['EU']['OE']+monthEntry.value['EU']['EX']+ monthEntry.value['EU']['TR']}"/>
					<c:set  var="usTotal" value="${monthEntry.value['US']['AE']+monthEntry.value['US']['OE']+monthEntry.value['US']['EX']+ monthEntry.value['US']['TR']}"/>
					<c:set  var="jpTotal" value="${monthEntry.value['JP']['AE']+monthEntry.value['JP']['OE']+monthEntry.value['JP']['EX']+ monthEntry.value['JP']['TR']}"/>
					<c:set  var="caTotal" value="${monthEntry.value['CA']['AE']+monthEntry.value['CA']['OE']+monthEntry.value['CA']['EX']+ monthEntry.value['CA']['TR']}"/>
					<c:set  var="mxTotal" value="${monthEntry.value['MX']['AE']+monthEntry.value['MX']['OE']+monthEntry.value['MX']['EX']+ monthEntry.value['MX']['TR']}"/>
					<tr>
						<td style="width:5%">${monthEntry.key}</td>
						<td style="width:5%"><fmt:formatNumber value="${monthEntry.value['EU']['AE']}" maxFractionDigits="2"></fmt:formatNumber> 
							<c:if test="${not empty monthEntry.value['EU']['AE']}">
							(<fmt:formatNumber value="${monthEntry.value['EU']['AE']*100/euTotal}" maxFractionDigits="2"/>%)
							</c:if>
						</td>
						<td style="width:5%"><fmt:formatNumber value="${monthEntry.value['EU']['OE']}" maxFractionDigits="2"></fmt:formatNumber>
							<c:if test="${not empty monthEntry.value['EU']['OE']}">
							(<fmt:formatNumber value="${monthEntry.value['EU']['OE']*100/euTotal}" maxFractionDigits="2"/>%)
							</c:if>
						 </td>
						<td style="width:5%"><fmt:formatNumber value="${monthEntry.value['EU']['EX']}" maxFractionDigits="2"></fmt:formatNumber>
							<c:if test="${not empty monthEntry.value['EU']['EX']}">
							(<fmt:formatNumber value="${monthEntry.value['EU']['EX']*100/euTotal}" maxFractionDigits="2"/>%)
							</c:if>
						 </td>
						
						<td style="width:5%"><fmt:formatNumber value="${monthEntry.value['EU']['TR']}" maxFractionDigits="2"></fmt:formatNumber>
							<c:if test="${not empty monthEntry.value['EU']['TR']}">
							(<fmt:formatNumber value="${monthEntry.value['EU']['TR']*100/euTotal}" maxFractionDigits="2"/>%)
							</c:if>
						 </td>
						
						
						
						
						<td style="width:5%"><fmt:formatNumber value="${monthEntry.value['US']['AE']}" maxFractionDigits="2"></fmt:formatNumber> 
							<c:if test="${not empty monthEntry.value['US']['AE']}">
							(<fmt:formatNumber value="${monthEntry.value['US']['AE']*100/usTotal}" maxFractionDigits="2"/>%)
							</c:if>
						</td>
						<td style="width:5%"><fmt:formatNumber value="${monthEntry.value['US']['OE']}" maxFractionDigits="2"></fmt:formatNumber>
							<c:if test="${not empty monthEntry.value['US']['OE']}">
							(<fmt:formatNumber value="${monthEntry.value['US']['OE']*100/usTotal}" maxFractionDigits="2"/>%)
							</c:if>
						 </td>
						<td style="width:5%"><fmt:formatNumber value="${monthEntry.value['US']['EX']}" maxFractionDigits="2"></fmt:formatNumber>
							<c:if test="${not empty monthEntry.value['US']['EX']}">
							(<fmt:formatNumber value="${monthEntry.value['US']['EX']*100/usTotal}" maxFractionDigits="2"/>%)
							</c:if>
						 </td>
						 	<td style="width:5%"><fmt:formatNumber value="${monthEntry.value['US']['TR']}" maxFractionDigits="2"></fmt:formatNumber>
							<c:if test="${not empty monthEntry.value['US']['TR']}">
							(<fmt:formatNumber value="${monthEntry.value['US']['TR']*100/usTotal}" maxFractionDigits="2"/>%)
							</c:if>
						 </td>
						 
						
						<td style="width:5%"><fmt:formatNumber value="${monthEntry.value['JP']['AE']}" maxFractionDigits="2"></fmt:formatNumber> 
							<c:if test="${not empty monthEntry.value['JP']['AE']}">
							(<fmt:formatNumber value="${monthEntry.value['JP']['AE']*100/jpTotal}" maxFractionDigits="2"/>%)
							</c:if>
						</td>
						<td style="width:5%"><fmt:formatNumber value="${monthEntry.value['JP']['OE']}" maxFractionDigits="2"></fmt:formatNumber>
							<c:if test="${not empty monthEntry.value['JP']['OE']}">
							(<fmt:formatNumber value="${monthEntry.value['JP']['OE']*100/jpTotal}" maxFractionDigits="2"/>%)
							</c:if>
						 </td>
						<td style="width:5%"><fmt:formatNumber value="${monthEntry.value['JP']['EX']}" maxFractionDigits="2"></fmt:formatNumber>
							<c:if test="${not empty monthEntry.value['JP']['EX']}">
							(<fmt:formatNumber value="${monthEntry.value['JP']['EX']*100/jpTotal}" maxFractionDigits="2"/>%)
							</c:if>
						 </td>
						 <td style="width:5%"><fmt:formatNumber value="${monthEntry.value['JP']['TR']}" maxFractionDigits="2"></fmt:formatNumber>
							<c:if test="${not empty monthEntry.value['JP']['TR']}">
							(<fmt:formatNumber value="${monthEntry.value['JP']['TR']*100/jpTotal}" maxFractionDigits="2"/>%)
							</c:if>
						 </td>
						
						<td style="width:5%"><fmt:formatNumber value="${monthEntry.value['CA']['AE']}" maxFractionDigits="2"></fmt:formatNumber> 
							<c:if test="${not empty monthEntry.value['CA']['AE']}">
							(<fmt:formatNumber value="${monthEntry.value['CA']['AE']*100/caTotal}" maxFractionDigits="2"/>%)
							</c:if>
						</td>
						<td style="width:5%"><fmt:formatNumber value="${monthEntry.value['CA']['OE']}" maxFractionDigits="2"></fmt:formatNumber>
							<c:if test="${not empty monthEntry.value['CA']['OE']}">
							(<fmt:formatNumber value="${monthEntry.value['CA']['OE']*100/caTotal}" maxFractionDigits="2"/>%)
							</c:if>
						 </td>
						<td style="width:5%"><fmt:formatNumber value="${monthEntry.value['CA']['EX']}" maxFractionDigits="2"></fmt:formatNumber>
							<c:if test="${not empty monthEntry.value['CA']['EX']}">
							(<fmt:formatNumber value="${monthEntry.value['CA']['EX']*100/caTotal}" maxFractionDigits="2"/>%)
							</c:if>
						 </td>
						 <td style="width:5%"><fmt:formatNumber value="${monthEntry.value['CA']['TR']}" maxFractionDigits="2"></fmt:formatNumber>
							<c:if test="${not empty monthEntry.value['CA']['TR']}">
							(<fmt:formatNumber value="${monthEntry.value['CA']['TR']*100/caTotal}" maxFractionDigits="2"/>%)
							</c:if>
						 </td>
						  
						 <td style="width:5%"><fmt:formatNumber value="${monthEntry.value['MX']['AE']}" maxFractionDigits="2"></fmt:formatNumber> 
							<c:if test="${not empty monthEntry.value['MX']['AE']}">
							(<fmt:formatNumber value="${monthEntry.value['MX']['AE']*100/caTotal}" maxFractionDigits="2"/>%)
							</c:if>
						</td>
						<td style="width:5%"><fmt:formatNumber value="${monthEntry.value['MX']['OE']}" maxFractionDigits="2"></fmt:formatNumber>
							<c:if test="${not empty monthEntry.value['MX']['OE']}">
							(<fmt:formatNumber value="${monthEntry.value['MX']['OE']*100/caTotal}" maxFractionDigits="2"/>%)
							</c:if>
						 </td>
						<td style="width:5%"><fmt:formatNumber value="${monthEntry.value['MX']['EX']}" maxFractionDigits="2"></fmt:formatNumber>
							<c:if test="${not empty monthEntry.value['MX']['EX']}">
							(<fmt:formatNumber value="${monthEntry.value['MX']['EX']*100/caTotal}" maxFractionDigits="2"/>%)
							</c:if>
						 </td>
						 <td style="width:5%"><fmt:formatNumber value="${monthEntry.value['MX']['TR']}" maxFractionDigits="2"></fmt:formatNumber>
							<c:if test="${not empty monthEntry.value['MX']['TR']}">
							(<fmt:formatNumber value="${monthEntry.value['MX']['TR']*100/caTotal}" maxFractionDigits="2"/>%)
							</c:if>
						 </td>
					</tr>
			</c:forEach>
		</tbody>
		</table>
</body>
</html>
