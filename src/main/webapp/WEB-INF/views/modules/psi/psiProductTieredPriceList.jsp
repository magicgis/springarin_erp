<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品阶梯价格</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr {
			float: right;
			min-height: 40px
		}
	</style>
	<script type="text/javascript">
			$(document).ready(function(){
				$("#contentTable").dataTable({
					"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
					"sPaginationType" : "bootstrap",
					"iDisplayLength" : 15,
					"aLengthMenu" : [ [15, 60, 100, -1 ],
							[ 15, 60, 100, "All" ] ],
					"bScrollCollapse" : true,
					"oLanguage" : {
						"sLengthMenu" : "_MENU_ 条/页"
					},
					"ordering" : true,
					 "aaSorting": [[ 0, "desc" ]]
				});
					
					
				$("#product,#supplier").change(function(){
					$("#searchForm").submit();
				});
				
				
				
				$("#btnLog").click(function(){
					window.location.href="${ctx}/psi/productTieredPriceLog";
				});
				
				$("#btnNoTax").click(function(){
					window.location.href="${ctx}/psi/productTieredPrice/noTax";
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
			
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/product/list">产品列表</a></li>
		<li class="active"><a href="${ctx}/psi/productTieredPrice/">产品阶梯价格</a></li>
		<shiro:hasPermission name="psi:product:viewPrice">
			<li ><a href="${ctx}/psi/productTieredPrice/noTax">产品税价</a></li>
			<li ><a href="${ctx}/psi/productTieredPrice/reviewlist">价格改动审核</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="productTieredPrice" action="${ctx}/psi/productTieredPrice/" method="post" class="breadcrumb form-search" cssStyle="height: 60px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 100px;line-height: 40px">
			<label>供应商：</label>
			<select style="width:150px;" id="supplier" name="supplier.id">
				<option value="" ${productTieredPrice.supplier eq '' ?'selected':''}>全部</option>
				<c:forEach items="${suppliers}" var="supplier" varStatus="i">
					 <option value='${supplier.id}' ${supplier.id eq  productTieredPrice.supplier.id?'selected':''}>${supplier.nikename}</option>;
				</c:forEach>
			</select>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<label>产品：</label>
			<select style="width:200px;" id="product" name="product.id">
				<option value="" ${productTieredPrice.product eq '' ?'selected':''}>全部</option>
				<c:forEach items="${products}" var="product" varStatus="i">
					 <option value='${product.id}' ${product.id eq  productTieredPrice.product.id?'selected':''}>${product.name}</option>;
				</c:forEach>
			</select>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnLog" class="btn btn-warning" type="button" value="价格改动日志"/>
			
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnNoTax" class="btn btn-success" type="button" value="产品税价"/>
		</div>
		
		
		</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table  table-bordered table-condensed">
		<thead>
			<tr>
			   <th style="width:8%;"rowspan="2">产品名(本页显示的都是<span style="color:red">不含税价</span>)</th>
			   <th style="width:5%"  rowspan="2">供应商</th>
			   <th style="width:5%"  rowspan="2">税率(%)</th>
			   <th style="width:10%;text-align: center" colspan="2">500</th>
			   <th style="width:10%;text-align: center" colspan="2">1000</th>
			   <th style="width:10%;text-align: center" colspan="2">2000</th>
			   <th style="width:10%;text-align: center" colspan="2">3000</th>
			   <th style="width:10%;text-align: center" colspan="2">5000</th>
			   <th style="width:10%;text-align: center" colspan="2">10000</th>
			   <th style="width:10%;text-align: center" colspan="2">15000</th>
			   <th style="width:8%;text-align: center" rowspan="2">操作</th>
			 </tr>
			 <tr>
			   <th style="width:5%;text-align: center">USD</th>
			   <th style="width:5%;text-align: center">CNY</th>
			   <th style="width:5%;text-align: center">USD</th>
			   <th style="width:5%;text-align: center">CNY</th>
			   <th style="width:5%;text-align: center">USD</th>
			   <th style="width:5%;text-align: center">CNY</th>
			   <th style="width:5%;text-align: center">USD</th>
			   <th style="width:5%;text-align: center">CNY</th>
			   <th style="width:5%;text-align: center">USD</th>
			   <th style="width:5%;text-align: center">CNY</th>
			   <th style="width:5%;text-align: center">USD</th>
			   <th style="width:5%;text-align: center">CNY</th>
			   <th style="width:5%;text-align: center">USD</th>
			   <th style="width:5%;text-align: center">CNY</th>
			 </tr>
		</thead>
		<tbody>
		<c:forEach items="${priceDtos}" var="priceDto">
			<tr>
			<td>${fn:split(priceDto.proNameColor,' ')[1]}</td>
			<td>${priceDto.nikeName}</td>
			<td>${priceDto.taxRate}%</td>
			<c:choose>
				<c:when test="${priceDto.moq>500}">
					<td/><td/>
				</c:when>
				<c:otherwise>
					<td style="text-align: center">${priceDto.leval500usd}</td>
					<td style="text-align: center">${priceDto.leval500cny}</td>
				</c:otherwise>
			</c:choose>
			
			<c:choose>
				<c:when test="${priceDto.moq>1000}">
					<td/><td/>
				</c:when>
				<c:otherwise>
					<td style="text-align: center;color:${priceDto.leval500usd eq priceDto.leval1000usd ?'':'red'}"> ${priceDto.leval1000usd}</td>
					<td style="text-align: center;color:${priceDto.leval500cny eq priceDto.leval1000cny ?'':'red'}">${priceDto.leval1000cny}</td>
				</c:otherwise>
			</c:choose>
			
		
			<c:choose>
				<c:when test="${priceDto.moq>2000}">
					<td/><td/>
				</c:when>
				<c:otherwise>
					<td style="text-align: center;color:${priceDto.leval1000usd eq priceDto.leval2000usd ?'':'red'}">${priceDto.leval2000usd}</td>
					<td style="text-align: center;color:${priceDto.leval1000cny eq priceDto.leval2000cny ?'':'red'}">${priceDto.leval2000cny}</td>
				</c:otherwise>
			</c:choose>
			
			<c:choose>
				<c:when test="${priceDto.moq>3000}">
					<td/><td/>
				</c:when>
				<c:otherwise>
					<td style="text-align: center;color:${priceDto.leval2000usd eq priceDto.leval3000usd ?'':'red'}">${priceDto.leval3000usd}</td>
					<td style="text-align: center;color:${priceDto.leval2000cny eq priceDto.leval3000cny ?'':'red'}">${priceDto.leval3000cny}</td>
				</c:otherwise>
			</c:choose>
			
			<c:choose>
				<c:when test="${priceDto.moq>5000}">
					<td/><td/>
				</c:when>
				<c:otherwise>
					<td style="text-align: center;color:${priceDto.leval3000usd eq priceDto.leval5000usd ?'':'red'}">${priceDto.leval5000usd}</td>
					<td style="text-align: center;color:${priceDto.leval3000cny eq priceDto.leval5000cny ?'':'red'}">${priceDto.leval5000cny}</td>
				</c:otherwise>
			</c:choose>
			
			<c:choose>
				<c:when test="${priceDto.moq>10000}">
					<td/><td/>
				</c:when>
				<c:otherwise>
					<td style="text-align: center;color:${priceDto.leval5000usd eq priceDto.leval10000usd ?'':'red'}">${priceDto.leval10000usd}</td>
					<td style="text-align: center;color:${priceDto.leval5000cny eq priceDto.leval10000cny ?'':'red'}">${priceDto.leval10000cny}</td>
				</c:otherwise>
			</c:choose>
			
			<c:choose>
				<c:when test="${priceDto.moq>15000}">
					<td/><td/>
				</c:when>
				<c:otherwise>
					<td style="text-align: center;color:${priceDto.leval10000usd eq priceDto.leval15000usd ?'':'red'}">${priceDto.leval15000usd}</td>
					<td style="text-align: center;color:${priceDto.leval10000cny eq priceDto.leval15000cny ?'':'red'}">${priceDto.leval15000cny}</td>
				</c:otherwise>
			</c:choose>
			
				<td style="text-align: center">
				<shiro:hasPermission name="psi:product:editPrice">
					<a href="${ctx}/psi/productTieredPrice/setPrice?productId=${priceDto.productId}&supplierId=${priceDto.supplierId}&color=${priceDto.color}">改价</a>&nbsp;&nbsp;&nbsp;
				</shiro:hasPermission>
				<a href="${ctx}/psi/productTieredPriceLog/list?product.id=${priceDto.productId}&supplier.id=${priceDto.supplierId}&color=${priceDto.color}&productNameColor=${priceDto.proNameColor}">日志</a>
			</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
