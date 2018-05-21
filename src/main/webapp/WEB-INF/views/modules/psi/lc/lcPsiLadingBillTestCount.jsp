<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购财务报表</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.red{color:red;}
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
			
			$("#supplier,#orderNo").click(function(){
				$("#searchForm").submit();
			});
			
			$("#export").click(function(){
				var params = {};
				params.startDate=$("input[name='startDate']").val();
				params.endDate=$("input[name='endDate']").val();
				window.location.href = "${ctx}/psi/lcPsiLadingBill/testCountExport?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
			
			$("#dataTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 15,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"aoColumns": [
			          null,
			          null,
			          null,
			          null,
			          null
				],
				"ordering" : true,
				 "aaSorting": [[ 4, "desc" ]]
			});
		
			
			
		});
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            return x.toFixed(2);  
	     }  
		
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
   		<li><a href="${ctx}/psi/purchaseOrder/deliveryRate">交货延期率</a></li>
    	<li class="active"><a href="${ctx}/psi/lcPsiLadingBill/testCount">产品合格率</a></li>
    	<li><a href="${ctx}/psi/lcPsiLadingBill/testCountSupplier">供应商合格率</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="lcPsiLadingBill" action="${ctx}/psi/lcPsiLadingBill/testCount" method="post" class="breadcrumb form-search" cssStyle="height: 30px;">
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<label>日期：</label>
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="startDate" value="${startDate}" class="input-small" id="startDate"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="${endDate}" class="input-small" id="endDate"/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="export" class="btn btn-success" type="button" value="导出"/>
	</form:form>
	
	<table id="dataTable" class="table table-bordered table-condensed">
		<thead><tr>
		<th width="5%">供应商</th><th width="5%">产品名</th><th width="5%">总批次</th><th width="5%">不合格批次</th><th width="5%">合格率</th></tr></thead>
		<tbody>
		<c:forEach items="${okRate}" var="obj" varStatus="i">
		<tr>
			<td>${supplierMap[obj.value[1]]}</td>
			<td>${obj.value[0]}</td>
			<td>${obj.value[2]}</td>
			<td>${obj.value[3]}</td>
			<td><fmt:formatNumber value="${obj.value[4]}" pattern="##.##"/></td>
		</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
