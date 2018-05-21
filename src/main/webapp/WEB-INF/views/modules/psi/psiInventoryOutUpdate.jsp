<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>出库管理更新PDF出库单</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">	
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			$("#inputForm").validate();
			
			$("#pdfFileupload").on("change",function(){
				var shipmentId = "";
				//<c:forEach items="${fn:split(psiInventoryOut.tranFbaNo,',')}" var="shipmentId">
					shipmentId = '${shipmentId}';
				//</c:forEach>
				var filePath=$("#pdfFileupload").val();
				var fileSuffix=filePath.substr(filePath.lastIndexOf(".")).toLowerCase();
				if(fileSuffix.indexOf(".")!=0||".pdf,".indexOf(fileSuffix+",")==-1){
					top.$.jBox.tip("please select a pdf file", 'info',{timeout:2000});
					$("#pdfFileupload").val("").focus();
				}
			});
		});
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiInventoryOut/">Out-stock List</a></li>
		<li class="active"><a href="#">Update PDF File</a></li>
	</ul> 
	
	<div class="alert alert-danger" id="showError" style="float:left;width:98%;height:15px;margin-left:20px;display:none"></div>
	<form:form id="inputForm" name="inputForm" modelAttribute="psiInventoryOut" action="${ctx}/psi/psiInventoryOut/updatePdf" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input type="hidden" name="id" id="id" value="${psiInventoryOut.id}" />
		<blockquote>
			<p style="font-size: 14px">Base Info.</p>
		</blockquote>
		<div class="control-group">
			<label class="control-label" style="width:80px"><b>Warehouse:</b></label>
			<div class="controls" style="margin-left:100px">
			<b>
			<c:if test="${psiInventoryOut.warehouseName eq '中国本地A'}">China</c:if>
			<c:if test="${psiInventoryOut.warehouseName eq '德国本地A'}">Germany</c:if>
			<c:if test="${psiInventoryOut.warehouseName eq '美国本地A'}">American</c:if>
			<c:if test="${psiInventoryOut.warehouseName ne '美国本地A' && psiInventoryOut.warehouseName eq '德国本地A'&& psiInventoryOut.warehouseName eq '中国本地A'}">${psiInventoryOut.warehouseName}</c:if>
			</b>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" style="width:80px"><b>To:</b></label>
			<div class="controls" style="margin-left:100px">
			<c:if test="${psiInventoryOut.operationType eq 'FBA Delivery'}">
				${fns:getDictLabel(psiInventoryOut.whereabouts, 'platform', '')}
			</c:if>
			<c:if test="${psiInventoryOut.operationType ne 'FBA Delivery'}">
				${psiInventoryOut.whereabouts}x
			</c:if>
			</div>
		</div>
	
		<div class="control-group">
			<label class="control-label"  style="width:80px"><b>Bill No:</b></label>
			<div class="controls" style="margin-left:100px">
				${psiInventoryOut.billNo}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" style="width:80px"><b>FBA:</b></label>
			<div class="controls" style="margin-left:100px">
				<c:forEach items="${fn:split(psiInventoryOut.tranFbaNo,',')}" var="shipmentId">
					<a  target="_blank" href="${ctx}/psi/fbaInbound?shipmentId=${shipmentId}&country=">${shipmentId}</a>&nbsp;&nbsp;&nbsp;&nbsp;
				</c:forEach>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label" style="width:100px"><b>PDF File：</b></label>
			<div class="controls" style="margin-left:120px">
				<input name="pdfOutboundFile" type="file" id="pdfFileupload" class="required"/>
			</div>
		</div>
		
		<div class="form-actions" style="float:left;width:100%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="Upload"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="Back" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
