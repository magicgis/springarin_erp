<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Size</title>
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
		
		.spanexl {
			float: left;
		 }
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
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
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$("#contentTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true
			});
			$(".row:first").append('&nbsp;&nbsp;&nbsp;&nbsp;<input id="exportSubmit" class="btn btn-primary" type="button" value="导出" onclick="exportList();"/>');
		});
		
		function exportList(){
			$("#searchForm").attr("action","${ctx}/amazoninfo/amazonPortsDetail/exportExceptionSize");
			$("#searchForm").submit();
			$("#searchForm").attr("action","${ctx}/amazoninfo/amazonPortsDetail/exceptionSizeList");
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="${empty country ?'active':''}"><a class="countryHref" href="#" key="">总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
	</ul>
	<form id="searchForm"  action="${ctx}/amazoninfo/amazonPortsDetail/exceptionSizeList" method="post">
	   <input name="country" id="country" type="hidden" value="${country}"/>
	  <!--  <input id="exportSubmit" class="btn btn-primary" type="button" value="导出" onclick="exportList();"/>	 -->
	</form>	
		
	   <table id="contentTable" class="table table-bordered table-condensed">
		<thead>
		  <tr>
		    <td>国家</td>
		    <td>产品</td>
		    <td>Asin</td>
		    <td>亚马逊长(cm)</td>
		    <td>ERP长(cm)</td>
		    <td>亚马逊宽(cm)</td>
		    <td>ERP宽(cm)</td>
		    <td>亚马逊高(cm)</td>
		    <td>ERP高(cm)</td>
		  </tr>
		</thead>
		<tbody>
		<c:forEach items="${sizeList}" var="temp">
			<tr>
				<td>${fns:getDictLabel(temp[0], 'platform', defaultValue)}</td>
				<td><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${temp[1]}" target="_blank">${temp[1]}</a></td>
				<td>${temp[2]}</td>
				
				<td>${temp[4]}</td>
				<td>${temp[7]}</td>
				<td>${temp[5]}</td>
				<td>${temp[8]}</td>
				<td>${temp[6]}</td>
				<td>${temp[9]}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>

</body>
</html>
