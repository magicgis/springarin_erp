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
		<li ><a href="${ctx}/psi/productTieredPrice/">产品阶梯价格</a></li>
		<li ><a href="${ctx}/psi/productTieredPrice/noTax">产品不含税价</a></li>
		<li class="active"><a href="${ctx}/psi/productTieredPrice/reviewlist">价格改动审核</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="productTieredPriceDto" action="${ctx}/psi/productTieredPrice/reviewlist" method="post" class="breadcrumb form-search" cssStyle="height: 60px;">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 100px;line-height: 40px">
			<label>供应商：</label>
			<select style="width:150px;" id="supplier" name="supplierId">
				<option value="" ${productTieredPriceDto.supplierId eq '' ?'selected':''}>全部</option>
				<c:forEach items="${suppliers}" var="supplier" varStatus="i">
					 <option value='${supplier.id}' ${supplier.id eq  productTieredPriceDto.supplierId?'selected':''}>${supplier.nikename}</option>;
				</c:forEach>
			</select>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<label>产品：</label>
			<select style="width:200px;" id="product" name="productId">
				<option value="" ${productTieredPriceDto.productId eq '' ?'selected':''}>全部</option>
				<c:forEach items="${products}" var="product" varStatus="i">
					 <option value='${product.id}' ${product.id eq  productTieredPriceDto.productId?'selected':''}>${product.name}</option>;
				</c:forEach>
			</select>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnLog" class="btn btn-warning" type="button" value="价格改动日志"/>
			
		</div>
		
		
		</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table  table-bordered table-condensed">
		<thead>
			<tr>
			   <th style="width:5%;">序号</th>
			   <th style="width:10%;">产品名</th>
			   <th style="width:10%" >供应商</th>
			   <th style="width:20%;">备注</th>
			   <th style="width:10%;">申请人</th>
			   <th style="width:10%;">申请时间</th>
			   <th style="width:10%;">状态</th>
			   <th style="width:15%;">操作</th>
			 </tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="priceDto">
			<tr>
			<td>${priceDto.id}</td>
			<td>${priceDto.proNameColor}</td>
			<td>${priceDto.nikeName}</td>
			<td>${priceDto.content}</td>
			<td>${priceDto.createUser.name}</td>
			<td><fmt:formatDate value="${priceDto.createDate}" pattern="yyyy-MM-dd"/></td>
			<td style="text-align: center">
				<c:choose>
					<c:when test="${priceDto.reviewSta eq '0'}"><span class="label label-important">申请</span></c:when>
					<c:when test="${priceDto.reviewSta eq '2'}"><span class="label label-success">已审核</span></c:when>
					<c:when test="${priceDto.reviewSta eq '3'}"><span class="label label-inverse">已取消</span></c:when>
				</c:choose>
			</td>
			<td style="text-align: center">
				<c:if test="${priceDto.reviewSta eq '0'}">
				<shiro:hasPermission name="psi:product:reviewPrice">
					<a class="btn btn-small"  href="${ctx}/psi/productTieredPrice/reviewPrice?id=${priceDto.id}">审核</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<a  class="btn btn-small"  href="${ctx}/psi/productTieredPrice/cancelPrice?id=${priceDto.id}" onclick="return confirmx('确认要取消该申请吗？', this.href)">取消</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</shiro:hasPermission>
				<shiro:lacksPermission name="psi:product:reviewPrice">
					<c:if test="${fns:getUser().id eq priceDto.createUser.id}">
						<a  class="btn btn-small"  href="${ctx}/psi/productTieredPrice/cancelPrice?id=${priceDto.id}" onclick="return confirmx('确认要取消该申请吗？', this.href)">取消</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</c:if>
				</shiro:lacksPermission>
				</c:if>
				<a class="btn btn-small"  href="${ctx}/psi/productTieredPrice/view?id=${priceDto.id}">查看</a>
			</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
