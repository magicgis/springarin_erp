<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>增值税发票管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
		}
		.modal.fade.in {
		 	top: 0%;
		}
		.modal{
			 width: auto;
			 margin-left:-500px 
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
		$(document).ready(function() {
			
		});
	
	</script> 
</head>
<body>

	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
		<tr>
		   <th>序号</th>
		   <th>出口日期</th>
		   <th>报关单号</th>
		   <th>报关单号21位</th>
		   <th>合同号</th>
		   <th>品名</th>
		   <th>数量</th>
		   <th>单价</th>
		   <th>发票号</th>
		   <th>发票单价</th>
		   <th>分配时间</th>
		 </tr>
		 </thead>   
		<tbody>
		<c:forEach items="${declareList}" var="declare">
			<tr>
				<td>${declare.id}</td>
				<td><fmt:formatDate value="${declare.declareDate}" pattern="yyyy-MM-dd"/></td>
				<td>${declare.declareNo}</td>
				<td>${declare.declareCode}</td>
				<td>${declare.transportNo}</td>
				<td>${declare.productName}</td>
				<td>${declare.quantity}</td>	
				<td>${declare.price}</td>	
				
				<td>${declare.invoice.invoiceNo}</td>	
				<td>${declare.invoice.price }</td>
				<td><fmt:formatDate value="${declare.arrangeDate}" pattern="yyyy-MM-dd"/></td>	
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
