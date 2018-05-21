<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Product Barcodes Manager</title>
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
		if(!(top)){
			top = self;			
		}	
		
		$(document).ready(function(){
			
			$("select[name='type']").change(function(){
				$("#searchForm").submit();
			});
			
			$("#contentTable").treeTable({expandLevel : 2});
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
		<li><a href="${ctx}/psi/product/barcodeslist">Current Barcode</a></li>
		<li><a href="${ctx}/amazoninfo/amazonProduct/barcodeSearch">Search History Barcode</a></li>
		<li  class="active"><a href="${ctx}/psi/product/oldBarcodeslist">DE Old Barcode</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiProduct" action="${ctx}/psi/product/oldBarcodeslist" method="post" class="breadcrumb form-search">
		<div style="vertical-align: middle;height: 40px;line-height: 40px">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<select name="type" style="width: 140px">
				<option value=""><spring:message code="psi_product_type"/></option>
				<c:forEach items="${fns:getDictList('product_type')}" var="dic">
					<option value="${dic.value}" ${psiProduct.type eq dic.value ?'selected':''}>${dic.label}</option>
				</c:forEach>
				<option value="other" ${psiProduct.type eq 'other' ?'selected':''}>Other</option>
			</select>
			<label><spring:message code="psi_product_model"/>：</label><form:input path="model" htmlEscape="false" maxlength="50" class="input-small"/>&nbsp;&nbsp;&nbsp;
			<label><spring:message code="psi_product_barcode"/>：</label><form:input path="brand" htmlEscape="false" maxlength="50" class="input-small"/>&nbsp;&nbsp;&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
				   <th style="width:10%;">No.</th>
				   <th style="width:30%;"><spring:message code="amaInfo_businessReport_productName"/></th>
				   <th>${fns:getDictLabel('de','platform','')}</th>
				</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="product">
			<tr id="${product.id}">
				<td>${product.id}</td>
				<td><b style="font-size: 14px">${product.name}</b>
				</td>
				<td>
				 </td>
			</tr>
			<c:forEach items="${product.barcodeMap}" var="barcodeMap" varStatus="i">
				<tr pid="${product.id}">
					<td></td>	
					<td style="text-align: center;vertical-align: middle;"><b style="font-size: 18px;">${barcodeMap.key}</b></td>
					<td>
						<c:if test="${fn:contains(product.platform,'de')}">
							 ${barcodeMap.value['de'].barcodeType}<br/>
							 <c:if test="${not empty barcodeMap.value['de'].barcode}">
							 	<a href="${ctx}/psi/product/genBarcode?isOld=1&country=${barcodeMap.value['de'].productPlatform}&type=${barcodeMap.value['de'].barcodeType}&productName=${barcodeMap.value['de'].barcodeProductName}&barcode=${barcodeMap.value['de'].barcode}$" target="_blank" class="btn btn-warning" >${barcodeMap.value['de'].barcode}$</a>								
							</c:if>
						</c:if>
					</td>	
					
				</tr>		
			</c:forEach>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
