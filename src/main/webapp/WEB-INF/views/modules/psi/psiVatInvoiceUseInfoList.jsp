<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>发票管理</title>
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
		});
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiVatInvoiceInfo/">增值税发票列表</a></li>
		<li class="active"><a href='#'>明细查看</a></li>
	</ul>
	<div class="alert">供应商名称:${psiVatInvoiceInfo.supplierName}&nbsp;&nbsp;&nbsp;&nbsp;发票号码:${psiVatInvoiceInfo.invoiceNo}&nbsp;&nbsp;&nbsp;&nbsp;产品名称:${psiVatInvoiceInfo.productName}</div>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:3%">No.</th><th style="width:8%">运单号</th><th style="width:8%">产品名</th><th style="width:8%">国家</th><th style="width:8%">数量</th><th style="width:8%">创建人</th><th style="width:8%">创建时间</th></tr></thead>
		<tbody>
			   <c:forEach items="${detailList}" var="item" varStatus="i">
			       <tr>
			         <td>${i.index+1}</td>
			         <td><a target='_blank' href='${ctx}/psi/lcPsiTransportOrder/view?transportNo=${item.item.transportOrder.transportNo}'>${item.item.transportOrder.transportNo}</a></td>
			         <td>${item.productName}</td>
					 <td>${fns:getDictLabel(item.countryCode, 'platform', defaultValue)}</td>
					 <td>${item.quantity}</td>
					 <td>${item.createUser.name}</td>
					 <td>${item.createDate}</td>
				   </tr>
			   </c:forEach>
		</tbody>
	</table>
</body>
</html>
