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
			$("#expExcel").click(function(){
				var params = {};
				params.type=$("input[name='type']").val();
				params.model=$("input[name='model']").val();
				params.brand=$("input[name='brand']").val();
				window.location.href = "${ctx}/psi/product/expBarcode?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
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
		<li class="active"><a href="${ctx}/psi/product/barcodeslist">Current Barcode</a></li>
		<li><a href="${ctx}/amazoninfo/amazonProduct/barcodeSearch">Search History Barcode</a></li>
		<li><a href="${ctx}/psi/product/oldBarcodeslist">DE Old Barcode</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiProduct" action="${ctx}/psi/product/barcodeslist" method="post" class="breadcrumb form-search">
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
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>&nbsp;&nbsp;
			<input id="expExcel" class="btn btn-success" type="button" value="<spring:message code="sys_but_export"/>"/>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
				   <th style="width:40px;">No.</th>
				   <th><spring:message code="amaInfo_businessReport_productName"/></th>
				   <c:forEach items="${fns:getDictList('platform')}" var="dic">
					  <c:if test="${dic.value ne 'com.unitek'}">
						 <th>${dic.label}</th>
					  </c:if>
		          </c:forEach>	
		
				 <!--   <th>Vendor_US</th>
				   <th>Vendor_DE</th> -->
				</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="product">
			<tr id="${product.id}">
				<td>${product.id}</td>
				<td><b style="font-size: 14px">${product.name}</b>
					<shiro:hasPermission name="psi:barcode:edit">
						<a class="btn btn-info" href="${ctx}/psi/product/updateBarcodeByUser?id=${product.id}"><spring:message code="sys_but_edit"/></a>
					</shiro:hasPermission>
				</td>
				<c:forEach items="${fns:getDictList('platform')}" var="dic">
					<c:if test="${dic.value ne 'com.unitek'}">
						 <td><c:if test="${fn:contains(product.platform,dic.value)}"><spring:message code='psi_product_sale'/></c:if><c:if test="${!fn:contains(product.platform,dic.value)}"><spring:message code='psi_product_notSale'/></c:if></td>
					</c:if>
		       </c:forEach>	
		
				<!-- <td></td><td></td> -->
			</tr>
			<c:forEach items="${product.barcodeMapByColor}" var="barcodeMap" varStatus="i">
				<tr pid="${product.id}">
					<td></td>	
					<td style="text-align: center;vertical-align: middle;"><b style="font-size: 18px;">${barcodeMap.key}</b></td>
					
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
					   <c:if test="${dic.value ne 'com.unitek'}">
						 <td>
						    
						   <c:if test="${fn:contains(product.platform,dic.value)}">
						      <c:forEach items="${barcodeMap.value[dic.value]}" var="accountBarcode">
								  <c:if test="${not empty accountBarcode.barcode}">
								    <c:if test="${not empty accountBarcode.accountName}"><b>${accountBarcode.accountName}:</b></c:if>${accountBarcode.barcodeType}<br/>
								 	<a href="${ctx}/psi/product/genBarcode?country=${accountBarcode.productPlatform}&type=${accountBarcode.barcodeType}&productName=${accountBarcode.barcodeProductName}&barcode=${accountBarcode.barcode}" target="_blank" class="btn btn-warning" >${accountBarcode.barcode}</a><br/><br/>									
								 </c:if>
						      </c:forEach>
						  </c:if>
						 
						 </td>
					   </c:if>
		            </c:forEach>	
					<td>
					   
					</td>	
					
					<%-- 
					 <td>  <c:if test="${not empty vendorMap['com'][barcodeMap.value['com'].name]}"> FNSKU<br/>
							 	<a href="${ctx}/psi/product/genBarcode?country=com&type=FNSKU&productName=${barcodeMap.value['com'].barcodeProductName}&barcode=${vendorMap['com'][barcodeMap.value['com'].name]}" target="_blank" class="btn btn-warning" >${vendorMap['com'][barcodeMap.value['com'].name]}</a>								
						  </c:if>
					</td>
					<td> 
					      <c:if test="${not empty vendorMap['de'][barcodeMap.value['de'].name]}"> EAN<br/>
							 	<a href="${ctx}/psi/product/genBarcode?country=de&type=EAN&productName=${barcodeMap.value['de'].barcodeProductName}&barcode=${vendorMap['de'][barcodeMap.value['de'].name]}" target="_blank" class="btn btn-warning" >${vendorMap['de'][barcodeMap.value['de'].name]}</a>								
						  </c:if>
					</td> --%>
				</tr>		
			</c:forEach>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
