<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Product TransportType Manager</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
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

			$("#contentTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 15,
				"aLengthMenu" : [ [ 15, 30, 60, 100, -1 ],
						[ 15, 30, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : false,
				 "aaSorting": [[ 0, "asc" ]]
			});
			
		});
				
		function productDetail(productName){
			var url = "${ctx}/psi/psiInventory/productInfoDetail?productName=" + encodeURIComponent(productName);
			window.location.href = url;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/product/list">产品列表</a></li>
		<li class="active"><a href="${ctx}/psi/productEliminate/transportTypelist">运输方式明细</a></li>
		<li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">产品其他管理<b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
		       <li><a href="${ctx}/psi/productEliminate">产品定位管理</a></li>
		       <%--<li><a href="${ctx}/psi/productEliminate/isMainlist">主力明细</a></li> --%>
		       <li><a href="${ctx}/psi/productEliminate/isNewlist">新品明细</a></li>
		       <li><a href="${ctx}/psi/productEliminate/addedMonthlist">上架日期</a></li>
		       <li><a href="${ctx}/psi/productEliminate/forecastlist">销售预测方案</a></li>
		       <li><a href="${ctx}/psi/productEliminate/transportTypelist">运输方式明细</a></li>
		        <li><a href="${ctx}/amazoninfo/amazonPortsDetail/findProductTypeChargeList">品类佣金</a></li>
		    </ul>
	   </li>
	</ul>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table  table-bordered table-condensed">
		<thead><tr>
				   <th>No.</th>
				   <th style="width:200px"><spring:message code="amaInfo_businessReport_productName"/></th>
				   <th style="width:120px">${fns:getDictLabel('de','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('uk','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('it','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('es','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('fr','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('jp','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('com','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('ca','platform','')}</th>
				   <th style="width:120px">操作</th>
				</tr>
		</thead>
		<tbody>
		<c:forEach items="${list}" var="productName" varStatus="i">
			<tr ${products[productName]['total'][0] eq '0'?'style=background-color:#cccccc':''}>
				<td>${i.count}</td>
				<td style="width:180px"><a onclick="productDetail('${productName}')" href="#">${productName}</a>
				</td>
				<td>
					<c:if test="${not empty products[productName]['de'][0]}">
						<c:choose>
							<c:when test="${products[productName]['de'][0] eq '1'}">海运</c:when>
							<c:when test="${products[productName]['de'][0] eq '2'}"><span style="color:#0000EE;">空运</span></c:when>
						</c:choose>
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['uk'][0]}">
						<c:choose>
							<c:when test="${products[productName]['uk'][0] eq '1'}">海运</c:when>
							<c:when test="${products[productName]['uk'][0] eq '2'}"><span style="color:#0000EE;">空运</span></c:when>
						</c:choose>
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['it'][0]}">
						<c:choose>
							<c:when test="${products[productName]['it'][0] eq '1'}">海运</c:when>
							<c:when test="${products[productName]['it'][0] eq '2'}"><span style="color:#0000EE;">空运</span></c:when>
						</c:choose>
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['es'][0]}">
						<c:choose>
							<c:when test="${products[productName]['es'][0] eq '1'}">海运</c:when>
							<c:when test="${products[productName]['es'][0] eq '2'}"><span style="color:#0000EE;">空运</span></c:when>
						</c:choose>
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['fr'][0]}">
						<c:choose>
							<c:when test="${products[productName]['fr'][0] eq '1'}">海运</c:when>
							<c:when test="${products[productName]['fr'][0] eq '2'}"><span style="color:#0000EE;">空运</span></c:when>
						</c:choose>
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['jp'][0]}">
						<c:choose>
							<c:when test="${products[productName]['jp'][0] eq '1'}">海运</c:when>
							<c:when test="${products[productName]['jp'][0] eq '2'}"><span style="color:#0000EE;">空运</span></c:when>
						</c:choose>
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['com'][0]}">
						<c:choose>
							<c:when test="${products[productName]['com'][0] eq '1'}">海运</c:when>
							<c:when test="${products[productName]['com'][0] eq '2'}"><span style="color:#0000EE;">空运</span></c:when>
						</c:choose>
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['ca'][0]}">
						<c:choose>
							<c:when test="${products[productName]['ca'][0] eq '1'}">海运</c:when>
							<c:when test="${products[productName]['ca'][0] eq '2'}"><span style="color:#0000EE;">空运</span></c:when>
						</c:choose>
					</c:if>
				</td>
				<td>
					<a href="${ctx}/psi/productEliminate/setStatus?productName=${productName}&flag=5">修改</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
