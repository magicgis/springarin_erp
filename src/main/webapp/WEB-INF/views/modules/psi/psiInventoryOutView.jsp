<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>OutBound View</title>
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
		window.location.href="${ctx}/psi/psiInventoryOut";
	}
	</script>
</head>
	
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiInventoryOut/">Out-stock List</a></li>
		<li class="active"><a href="#">Out-stock Order</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiInventoryOut" action="" method="post" class="form-horizontal" >
	<input type="hidden" name="warehouseName" >
		<blockquote>
			<p style="font-size: 14px">Base Info.</p>
		</blockquote>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:25%;height:30px">
				<label class="control-label" ><b>Warehouse:</b></label>
				<div class="controls" >
				<b>
				<c:if test="${psiInventoryOut.warehouseName eq '中国本地A'||psiInventoryOut.warehouseName eq '中国本地B'}">China</c:if>
				<c:if test="${psiInventoryOut.warehouseName eq '德国本地A'}">Germany</c:if>
				<c:if test="${psiInventoryOut.warehouseName eq '美国本地A'}">American</c:if>
				<c:if test="${psiInventoryOut.warehouseName ne '美国本地A' && psiInventoryOut.warehouseName eq '德国本地A'&& psiInventoryOut.warehouseName eq '中国本地A'}">${psiInventoryOut.warehouseName}</c:if>
				</b>
				</div>
			</div>
			<c:if test="${not empty psiInventoryOut.attchmentPath }">
				<div class="control-group" style="float:left;width:25%;height:30px">
					<label class="control-label" ><b>Certificate:</b></label>
					<div class="controls" >
					<a target="_blank" href="<c:url value='/data/site${psiInventoryOut.attchmentPath}'/>">View</a>
					</div>
				</div>
			</c:if>
			<div class="control-group"  style="float:left;width:25%;height:30px">
					<label class="control-label"><b><spring:message code='sys_menu_actualTimeOut'/></b>:</label>
					<div class="controls">
						<fmt:formatDate value="${psiInventoryOut.dataDate}" pattern="yyyy-MM-dd"/>
					</div>
			</div>
			<div class="control-group" style="float:left;width:25%;;height:30px">
					<label class="control-label" ><b>To:</b></label>
					<div class="controls" >
					<c:if test="${psiInventoryOut.operationType eq 'FBA Delivery'}">
						${fns:getDictLabel(psiInventoryOut.whereabouts, 'platform', '')}
					</c:if>
					<c:if test="${psiInventoryOut.operationType ne 'FBA Delivery'}">
						${psiInventoryOut.whereabouts}
					</c:if>
					</div>
			</div>
		</div>
		<div style="float:left;width:100%">
				<div class="control-group" style="float:left;width:25%;height:30px">
					<label class="control-label"><b>Type:</b></label>
					<div class="controls" >
					${psiInventoryOut.operationType}
					</div>
				</div>
				<c:if test="${psiInventoryOut.operationType eq 'FBA Delivery'}">
					<div class="control-group" style="float:left;width:25%;height:30px">
						<label class="control-label" ><b>FBA:</b></label>
						<div class="controls" >
							<c:forEach items="${fn:split(psiInventoryOut.tranFbaNo,',')}" var="shipmentId">
								<a  target="_blank" href="${ctx}/psi/fbaInbound?shipmentId=${shipmentId}&country=">${shipmentId}</a>&nbsp;&nbsp;&nbsp;&nbsp;
							</c:forEach>
						</div>
					</div>
				</c:if>
				<c:if test="${psiInventoryOut.operationType eq 'Transport Delivery' ||(psiInventoryOut.operationType eq 'FBA Delivery' && not empty psiInventoryOut.tranLocalNo)}">
					<div class="control-group" style="float:left;width:25%;height:30px">
						<label class="control-label" ><b>Tran-No.:</b></label>
						<div class="controls" >
							<a  target="_blank" href="${ctx}/psi/${fn:contains(psiInventoryOut.tranLocalNo,'_LC_')?'lcPsiTransportOrder':'psiTransportOrder' }/view?transportNo=${psiInventoryOut.tranLocalNo}">${psiInventoryOut.tranLocalNo}</a>
						</div>
					</div>
				</c:if>
				<c:if test="${psiInventoryOut.operationType eq 'Offline Delivery'}">
					<div class="control-group"  style="float:left;width:25%;height:30px">
					<label class="control-label"  ><b>Unline-No.:</b></label>
					<div class="controls" >
					<%-- <a target="_blank" href="${ctx}/amazoninfo/unlineOrder/form?id=${psiInventoryOut.unlineOrderNo}">${psiInventoryOut.unlineOrderNo}</a> --%>
					<a target="_blank" href="${ctx}/amazoninfo/unlineOrder/form?id=${psiInventoryOut.tranLocalId}">${psiInventoryOut.tranLocalNo}</a> 
					</div>
					</div>
				</c:if>
				
				<c:if test="${psiInventoryOut.operationType eq 'Lot Delivery'}">
					<div class="control-group"  style="float:left;width:25%;height:30px">
					<label class="control-label"  ><b>Data File:</b></label>
					<div class="controls" >
					<a target="_blank" href="<c:url value='/data/site${psiInventoryOut.dataFile}'/>">View Data File</a>
					</div>
					<div class="controls" >
					<a href="${ctx}/psi/psiInventoryOut/compareDateFile?id=${psiInventoryOut.id}">Download CompareData File</a>
					</div>
					</div>
				</c:if>
			
				<div class="control-group"  style="float:left;width:25%;height:30px">
					<label class="control-label"  ><b>Bill No:</b></label>
					<div class="controls" >
						${psiInventoryOut.billNo}
					</div>
				</div>
		</div>
		<c:if test="${psiInventoryOut.warehouseId eq 130 && (psiInventoryOut.operationType eq 'FBA Delivery'||psiInventoryOut.operationType eq 'Transport Delivery')}">
			<div style="float:left;width:98% ;">
				<div class="control-group"  style="float:left;width:25%;height:30px">
					<label class="control-label" ><b>提货人：</b></label>
					<div class="controls" >
						${psiInventoryOut.tranMan}
					</div>
				</div>
				<div class="control-group"  style="float:left;width:25%;height:30px">
					<label class="control-label" ><b>电话：</b></label>
					<div class="controls" >
						${psiInventoryOut.phone}
					</div>
				</div>
				<div class="control-group"  style="float:left;width:25%;height:30px">
					<label class="control-label" ><b>身份证号：</b></label>
					<div class="controls" >
						${psiInventoryOut.idCard}
					</div>
				</div>
				<div class="control-group"  style="float:left;width:25%;height:30px">
					<label class="control-label" ><b>流水号：</b></label>
					<div class="controls" >
						${psiInventoryOut.flowNo}
					</div>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:25%;height:30px">
					<label class="control-label" ><b>车牌号：</b></label>
					<div class="controls" >
						${psiInventoryOut.carNo}
					</div>
				</div>
				<div class="control-group"  style="float:left;width:25%;height:30px">
					<label class="control-label" ><b>柜号：</b></label>
					<div class="controls" >
						${psiInventoryOut.boxNo}
					</div>
				</div>
		</c:if>
		
		
		
		
		<div style="float:left;width:98% ;">
		<div class="control-group" style="float:left;width:98%">
				<label class="control-label" >Remark:</label>
				<div class="controls" >
					<textarea name="remark"  rows="4"  readonly style="width:95%; height: 60px;" >${psiInventoryOut.remark}</textarea>
				</div>
		</div>
		</div>
		 <blockquote style="float:left;">
		 <div style="float: left"><p style="font-size: 14px">Product info.</p></div><div style="float: left" id=errorsShow></div>
		</blockquote>
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 20%">Product[SKU]</th>
				   <th style="width: 10%">Country</th>
				   <th style="width: 10%">Color</th>
				   <th style="width: 10%">Type</th>
				   <th style="width: 10%">Quantity</th>
				   <th style="width: 10%">TimelyInventoryQuantity</th>
				   <th >Remark</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${psiInventoryOut.items}" var="item">
			<tr>
				<td>${item.productName}[${item.sku}]</td>
				<td>${fns:getDictLabel(item.countryCode, 'platform', '')}</td>
				<td>${item.colorCode}</td>
				<td>${item.qualityType}</td>
				<td>${item.quantity}</td>
				<td>${item.timelyQuantity}</td>
				<td>${item.remark}</td>
			</tr>
			</c:forEach>
		</tbody>
		
	</table>
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="Back" onclick="goBack()"/>
		</div>
	</form:form>
</body>
</html>
