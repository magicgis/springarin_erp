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
					"iDisplayLength" : 20,
					"aLengthMenu" : [ [20, 60, 100, -1 ],
							[ 20, 60, 100, "All" ] ],
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
		<li class="active"><a href="${ctx}/psi/productTieredPrice/noTax">产品税价</a></li>
	</ul>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table  table-bordered table-condensed">
		<thead>
			<tr>
			   <th style="width:10%;"rowspan="2">产品名</th>
			   <th style="width:5%"  rowspan="2">供应商</th>
			   <th style="width:5%"  rowspan="2">税率(%)</th>
			   <th style="width:10%;text-align: center" colspan="3">500</th>
			   <th style="width:10%;text-align: center" colspan="3">1000</th>
			   <th style="width:10%;text-align: center" colspan="3">2000</th>
			   <th style="width:10%;text-align: center" colspan="3">3000</th>
			   <th style="width:10%;text-align: center" colspan="3">5000</th>
			   <th style="width:10%;text-align: center" colspan="3">10000</th>
			   <th style="width:10%;text-align: center" colspan="3">15000</th>
			 </tr>
			 <tr>
			   <th style="width:5%;text-align: center">不含税</th>
			   <th style="width:5%;text-align: center">含税</th>
			   <th style="width:5%;text-align: center">退税价</th>
			   
			   <th style="width:5%;text-align: center">不含税</th>
			   <th style="width:5%;text-align: center">含税</th>
			   <th style="width:5%;text-align: center">退税价</th>
			   
			   <th style="width:5%;text-align: center">不含税</th>
			   <th style="width:5%;text-align: center">含税</th>
			   <th style="width:5%;text-align: center">退税价</th>
			   
			   <th style="width:5%;text-align: center">不含税</th>
			   <th style="width:5%;text-align: center">含税</th>
			   <th style="width:5%;text-align: center">退税价</th>
			   
			   <th style="width:5%;text-align: center">不含税</th>
			   <th style="width:5%;text-align: center">含税</th>
			   <th style="width:5%;text-align: center">退税价</th>
			   
			   <th style="width:5%;text-align: center">不含税</th>
			   <th style="width:5%;text-align: center">含税</th>
			   <th style="width:5%;text-align: center">退税价</th>
			   
			   <th style="width:5%;text-align: center">不含税</th>
			   <th style="width:5%;text-align: center">含税</th>
			   <th style="width:5%;text-align: center">退税价</th>
			 </tr>
		</thead>
		<tbody>
		<c:forEach items="${priceDtos}" var="priceDto">
			<tr>
			<td>${priceDto.proNameColor}</td>
			<td>${priceDto.nikeName}</td>
			<td>${priceDto.taxRate}%</td>
			<c:choose>
				<c:when test="${priceDto.moq<=500 && not empty priceDto.leval500cny}">
					<td style="text-align: center"><fmt:formatNumber value="${priceDto.leval500cny}" pattern="0.00"/> </td>
					<td style="text-align: center"><fmt:formatNumber value="${priceDto.leval500cny*(100+priceDto.taxRate)/100}" pattern="0.00"/></td>
					<td style="text-align: center"><fmt:formatNumber value="${priceDto.leval500cny*(100+priceDto.taxRate)/117}" pattern="0.00"/></td>
				</c:when>
				<c:otherwise>
					<td/><td/><td/>
				</c:otherwise>
			</c:choose>
			
			
			<c:choose>
				<c:when test="${priceDto.moq<=1000 && not empty priceDto.leval1000cny}">
					<td style="text-align: center">${priceDto.leval1000cny}</td>
					<td style="text-align: center"><fmt:formatNumber value="${priceDto.leval1000cny*(100+priceDto.taxRate)/100}" pattern="0.00"/></td>
					<td style="text-align: center"><fmt:formatNumber value="${priceDto.leval1000cny*(100+priceDto.taxRate)/117}" pattern="0.00"/></td>
				</c:when>
				<c:otherwise>
					<td/><td/><td/>
				</c:otherwise>
			</c:choose>
			
			<c:choose>
				<c:when test="${priceDto.moq<=2000 && not empty priceDto.leval2000cny}">
					<td style="text-align: center">${priceDto.leval2000cny} </td>
					<td style="text-align: center"><fmt:formatNumber value="${priceDto.leval2000cny*(100+priceDto.taxRate)/100}" pattern="0.00"/></td>
					<td style="text-align: center"><fmt:formatNumber value="${priceDto.leval2000cny*(100+priceDto.taxRate)/117}" pattern="0.00"/></td>
				</c:when>
				<c:otherwise>
					<td/><td/><td/>
				</c:otherwise>
			</c:choose>
			
			<c:choose>
				<c:when test="${priceDto.moq<=3000 && not empty priceDto.leval3000cny}">
					<td style="text-align: center">${priceDto.leval3000cny} </td>
					<td style="text-align: center"><fmt:formatNumber value="${priceDto.leval3000cny*(100+priceDto.taxRate)/100}" pattern="0.00"/></td>
					<td style="text-align: center"><fmt:formatNumber value="${priceDto.leval3000cny*(100+priceDto.taxRate)/117}" pattern="0.00"/></td>
				</c:when>
				<c:otherwise>
					<td/><td/><td/>
				</c:otherwise>
			</c:choose>
			
			
			<c:choose>
				<c:when test="${priceDto.moq<=5000 && not empty priceDto.leval5000cny}">
					<td style="text-align: center">${priceDto.leval5000cny} </td>
					<td style="text-align: center"><fmt:formatNumber value="${priceDto.leval5000cny*(100+priceDto.taxRate)/100}" pattern="0.00"/></td>
					<td style="text-align: center"><fmt:formatNumber value="${priceDto.leval5000cny*(100+priceDto.taxRate)/117}" pattern="0.00"/></td>
				</c:when>
				<c:otherwise>
					<td/><td/><td/>
				</c:otherwise>
			</c:choose>
			
			
			
			<c:choose>
				<c:when test="${priceDto.moq<=10000 && not empty priceDto.leval10000cny}">
					<td style="text-align: center">${priceDto.leval10000cny} </td>
					<td style="text-align: center"><fmt:formatNumber value="${priceDto.leval10000cny*(100+priceDto.taxRate)/100}" pattern="0.00"/></td>
					<td style="text-align: center"><fmt:formatNumber value="${priceDto.leval10000cny*(100+priceDto.taxRate)/117}" pattern="0.00"/></td>
				</c:when>
				<c:otherwise>
					<td/><td/><td/>
				</c:otherwise>
			</c:choose>
			
			<c:choose>
				<c:when test="${priceDto.moq<=15000 && not empty priceDto.leval15000cny}">
					<td style="text-align: center">${priceDto.leval15000cny} </td>
					<td style="text-align: center"><fmt:formatNumber value="${priceDto.leval15000cny*(100+priceDto.taxRate)/100}" pattern="0.00"/></td>
					<td style="text-align: center"><fmt:formatNumber value="${priceDto.leval15000cny*(100+priceDto.taxRate)/117}" pattern="0.00"/></td>
				</c:when>
				<c:otherwise>
					<td/><td/><td/>
				</c:otherwise>
			</c:choose>
			
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
