<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>InBound View</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		function goBack(){
			window.location.href="${ctx}/psi/psiInventoryIn";
		}
	</script>
</head>
	
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiInventoryIn/">In-stock List</a></li>
		<li class="active"><a href="#">In-stock order </a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiInventoryIn" action="${ctx}/psi/psiInventoryIn/" method="post" class="form-horizontal" enctype="multipart/form-data" >
		<blockquote>
			<p style="font-size: 14px">Base info.</p>
		</blockquote>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label"><b>Warehouse:</b></label>
				<div class="controls">
				<b>
				<c:if test="${psiInventoryIn.warehouseId eq '21'}">China</c:if>
				<c:if test="${psiInventoryIn.warehouseId eq '19'}">Germany</c:if>
				<c:if test="${psiInventoryIn.warehouseId eq '121'}">American</c:if>
				<c:if test="${psiInventoryIn.warehouseId eq '130'}">China_LC</c:if>
				<c:if test="${psiInventoryIn.warehouseId ne '121' && psiInventoryIn.warehouseId ne '21' && psiInventoryIn.warehouseId ne '19'&& psiInventoryIn.warehouseId ne '130'}">${psiInventoryIn.warehouseName}</c:if>
				</b>
				</div>
			</div>
			<c:if test="${not empty psiInventoryIn.attchmentPath}">
				<div class="control-group" style="float:left;width:25%;height:30px">
					<label class="control-label">Certificate:</label>
					<div class="controls">
					<a target="_blank" href="<c:url value='/data/site${psiInventoryIn.attchmentPath}'/>">View</a>
					</div>
				</div>
			</c:if>
			
			<div class="control-group"  style="float:left;width:25%;height:30px">
					<label class="control-label"><spring:message code='sys_menu_actualTimeIn'/>:</label>
					<div class="controls">
						<fmt:formatDate value="${psiInventoryIn.dataDate}" pattern="yyyy-MM-dd"/>
					</div>
			</div>
			
			<div class="control-group"  style="float:left;width:25%;height:30px">
					<label class="control-label">From:</label>
					<div class="controls">
						${psiInventoryIn.source}
					</div>
			</div>
		</div>
		
		<div style="float:left;width:100% ;">
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label"><b>Type:</b></label>
				<div class="controls">
				${psiInventoryIn.operationType}
				</div>
			</div>
			
			<c:if test="${psiInventoryIn.operationType eq 'Transport Storing'}">
				<div class="control-group"  style="float:left;width:25%;height:30px">
					<label class="control-label"><b>Transport No.:</b></label>
					<div class="controls">
						<a  target="_blank" href="${ctx}/psi/${fn:contains(psiInventoryIn.tranLocalNo,'_LC_')?'lcPsiTransportOrder':'psiTransportOrder' }/view?transportNo=${psiInventoryIn.tranLocalNo}">${psiInventoryIn.tranLocalNo}</a>
					</div>
				</div>
			</c:if>
			
			<c:if test="${psiInventoryIn.operationType eq 'Purchase Storing'}">
				<div class="control-group"  style="float:left;width:25%;height:30px">
					<label class="control-label"><b>Lading No.:</b></label>
					<div class="controls">
						<a  target="_blank" href="${ctx}/psi/${fn:contains(psiInventoryIn.tranLocalNo,'_LC_')?'lcPsiLadingBill':'psiLadingBill' }/view?billNo=${psiInventoryIn.tranLocalNo}">${psiInventoryIn.tranLocalNo}</a>
					</div>
				</div>
			</c:if>
			
				  
		   <c:if test="${psiInventoryIn.operationType eq 'Lot Storing'}">
			   <div class="control-group" id="dataFile" style="float:left;width:25%;height:30px">
				<label class="control-label"><b>Data file:</b></label>
				<div class="controls">
				<a target="_blank" href="<c:url value='/data/site${psiInventoryIn.dataFile}'/>">View File</a>
				</div>
				</div>
		   </c:if>
				
			<div class="control-group" id="dataFile" style="float:left;width:25%;height:30px">
				<label class="control-label" ><b>Bill No:</b></label>
				<div class="controls" >
					${psiInventoryIn.billNo}
				</div>
			</div>
		</div>
		
		<c:if test="${psiInventoryIn.warehouseId eq 130 && psiInventoryIn.operationType eq 'Purchase Storing'}">
			<div style="float:left;width:98% ;">
				<div class="control-group" id="dataFile" style="float:left;width:25%;height:30px">
					<label class="control-label" ><b>提货人：</b></label>
					<div class="controls" >
						${psiInventoryIn.tranMan}
					</div>
				</div>
				<div class="control-group" id="dataFile" style="float:left;width:25%;height:30px">
					<label class="control-label" ><b>电话：</b></label>
					<div class="controls" >
						${psiInventoryIn.phone}
					</div>
				</div>
				<div class="control-group" id="dataFile" style="float:left;width:25%;height:30px">
					<label class="control-label" ><b>车牌号：</b></label>
					<div class="controls" >
						${psiInventoryIn.carNo}
					</div>
				</div>
				<div class="control-group" id="dataFile" style="float:left;width:25%;height:30px">
					<label class="control-label" ><b>流水号：</b></label>
					<div class="controls" >
						${psiInventoryIn.flowNo}
					</div>
				</div>
			</div>
		</c:if>
		
		
		<div style="float:left;width:98% ;">
		<div class="control-group" style="float:left;width:98%">
				<label class="control-label">Remark:</label>
				<div class="controls">
					<textarea name="remark" rows="4"  readonly="readonly" maxlength="200" style="width:98%; height: 60px;" >${psiInventoryIn.remark}</textarea>
				</div>
			</div>
		</div>
		
		 <blockquote style="float:left;width:98%">
		 <div style="float: left"><p style="font-size: 14px">Product info.</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 30%">Product[SKU]</th>
				   <th style="width: 10%">Type</th>
				   <th style="width: 10%">Quantity</th>
				   <th style="width: 10%">TimelyInventoryQuantity</th>
				   <th style="width: 30%">Remark</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${psiInventoryIn.items}" var="item">
			<tr>
				<td>${item.productName}[${item.sku}]</td>
				<td>${item.qualityType}</td>
				<td>${item.quantity}</td>
				<td>${item.timelyQuantity}</td>
				<td>${item.remark}</td>
			</tr>
			</c:forEach>
		</tbody>
		
	</table>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="Back" onclick="goBack();return false"/>
		</div>
	</form:form>
</body>
</html>
