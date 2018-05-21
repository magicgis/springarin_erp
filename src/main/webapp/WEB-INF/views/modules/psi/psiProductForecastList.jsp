<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Product Forecast Manager</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<style type="text/css">
		.editable-empty{
			color:#DD1144 !important;
		}
	</style>
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
		<li class="active"><a href="${ctx}/psi/productEliminate/forecastlist">销售预测方案</a></li>
		<li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">产品其他管理<b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
		       <li><a href="${ctx}/psi/productEliminate">产品定位管理</a></li>
		       <li><a href="${ctx}/psi/productEliminate/isNewlist">新品明细</a></li>
		       <li><a href="${ctx}/psi/productEliminate/addedMonthlist">上架日期</a></li>
		       <li><a href="${ctx}/psi/productEliminate/forecastlist">销售预测方案</a></li>
		        <li><a href="${ctx}/amazoninfo/amazonPortsDetail/findProductTypeChargeList">品类佣金</a></li>
		    </ul>
	   </li>
	</ul>
	<div class="alert alert-info"><strong>销售预测方案说明：A-前月预测法&nbsp;&nbsp;&nbsp;&nbsp; B-月平均预测法&nbsp;&nbsp;&nbsp;&nbsp;  C-季节指数预测法</strong></div>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table  table-bordered table-condensed">
		<thead><tr>
				   <th>No.</th>
				   <th style="width:200px"><spring:message code="amaInfo_businessReport_productName"/></th>
				   <th style="width:120px">产品类型</th>
				   <th style="width:120px">${fns:getDictLabel('de','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('uk','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('it','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('es','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('fr','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('jp','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('com','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('ca','platform','')}</th>
				   <th style="width:120px">${fns:getDictLabel('mx','platform','')}</th>
				   <%--<th style="width:120px">操作</th> --%>
				</tr>
		</thead>
		<tbody>
		<c:forEach items="${list}" var="productName" varStatus="i">
			<tr>
				<td>${i.count}</td>
				<td style="width:180px"><a onclick="productDetail('${productName}')" href="#">${productName}</a>
				</td>
				<td style="width:120px">${products[productName]['total'][1]}
				</td>
				<td>
					<c:if test="${not empty products[productName]['de'][0]}">
						<c:if test="${'1' eq products[productName]['de'][0]}">A</c:if>
						<c:if test="${'2' eq products[productName]['de'][0]}">B</c:if>
						<c:if test="${'3' eq products[productName]['de'][0]}">C</c:if>
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['uk'][0]}">
						<c:if test="${'1' eq products[productName]['uk'][0]}">A</c:if>
						<c:if test="${'2' eq products[productName]['uk'][0]}">B</c:if>
						<c:if test="${'3' eq products[productName]['uk'][0]}">C</c:if>
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['it'][0]}">
						<c:if test="${'1' eq products[productName]['it'][0]}">A</c:if>
						<c:if test="${'2' eq products[productName]['it'][0]}">B</c:if>
						<c:if test="${'3' eq products[productName]['it'][0]}">C</c:if>
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['es'][0]}">
						<c:if test="${'1' eq products[productName]['es'][0]}">A</c:if>
						<c:if test="${'2' eq products[productName]['es'][0]}">B</c:if>
						<c:if test="${'3' eq products[productName]['es'][0]}">C</c:if>
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['fr'][0]}">
						<c:if test="${'1' eq products[productName]['fr'][0]}">A</c:if>
						<c:if test="${'2' eq products[productName]['fr'][0]}">B</c:if>
						<c:if test="${'3' eq products[productName]['fr'][0]}">C</c:if>
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['jp'][0]}">
						<c:if test="${'1' eq products[productName]['jp'][0]}">A</c:if>
						<c:if test="${'2' eq products[productName]['jp'][0]}">B</c:if>
						<c:if test="${'3' eq products[productName]['jp'][0]}">C</c:if>
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['com'][0]}">
						<c:if test="${'1' eq products[productName]['com'][0]}">A</c:if>
						<c:if test="${'2' eq products[productName]['com'][0]}">B</c:if>
						<c:if test="${'3' eq products[productName]['com'][0]}">C</c:if>
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['ca'][0]}">
						<c:if test="${'1' eq products[productName]['ca'][0]}">A</c:if>
						<c:if test="${'2' eq products[productName]['ca'][0]}">B</c:if>
						<c:if test="${'3' eq products[productName]['ca'][0]}">C</c:if>
					</c:if>
				</td>
				<td>
					<c:if test="${not empty products[productName]['mx'][0]}">
						<c:if test="${'1' eq products[productName]['mx'][0]}">A</c:if>
						<c:if test="${'2' eq products[productName]['mx'][0]}">B</c:if>
						<c:if test="${'3' eq products[productName]['mx'][0]}">C</c:if>
					</c:if>
				</td>
				<%--<td>
					<a class="btn btn-success btn-small" href="${ctx}/psi/productEliminate/setStatus?productName=${productName}&flag=4">设置</a>
				</td> --%>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
