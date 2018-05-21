<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>折扣预警管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<script type="text/javascript">
	
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	
		$(document).ready(function() {
			
			$("#selectLook").on("click",function(){
				if($(this).val()!=""){
					var params = {};
					params.country=$("input[name='country']").val();
					window.location.href = "${ctx}/psi/psiInventory/"+$(this).val()+"?"+$.param(params);
				}
			});
			
			$(".isCheck").on("click",function(){
				if(this.checked){
					$("#isCheck").val("1");
				}else{
					$("#isCheck").val("0");
				}
				$("#searchForm").submit();
			});
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$("#lookOver").click(function(){
				var params = {};
				params.country=$("input[name='country']").val();
				window.location.href = "${ctx}/psi/psiInventory/overStock?"+$.param(params);
			});
			
			$("#lookOverByProduct").click(function(){
				var params = {};
				params.country=$("input[name='country']").val();
				window.location.href = "${ctx}/psi/psiInventory/overStockProduct?"+$.param(params);
			});
			
			$("#lookOutOf").click(function(){
				var params = {};
				params.country=$("input[name='country']").val();
				window.location.href = "${ctx}/psi/psiInventory/outOfStock?"+$.param(params);
			});
			
			$("#lookOutOfByProduct").click(function(){
				var params = {};
				params.country=$("input[name='country']").val();
				window.location.href = "${ctx}/psi/psiInventory/outOfStockByProduct?"+$.param(params);
			});
			
			
			$("#expExcel").click(function(){
				var params = {};
				params.startDate=$("input[name='startDate']").val();
				params.endDate=$("input[name='endDate']").val();
				params.country=$("input[name='country']").val();
				params.isCheck=$("input[name='isCheck']").val();
				window.location.href = "${ctx}/psi/psiInventory/expOutOfStockByProduct?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
			$("#dataTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 20,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				 "aaSorting": [[ 2, "desc" ]]
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
		<li class="${empty country ?'active':''}"><a class="countryHref" href="#" key="">总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'&&dic.value ne 'it'&&dic.value ne 'es'}">
				<li class="${country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
		<li><a href="${ctx}/psi/psiInventory/findOutOfStock">断货次数</a></li>
		<li><a href="${ctx}/psi/psiInventory/countPurchase">采购次数</a></li>
		 <li><a href="${ctx}/psi/psiInventory/getTurnoverRate">周转率</a></li>
		  <li><a href="${ctx}/amazoninfo/amazonFbaHealthReport/findOverFbaProduct">FBA超期库存统计</a></li>
	</ul>
	<form:form id="searchForm"  action="${ctx}/psi/psiInventory/outOfStockByProduct" method="post" class="breadcrumb form-search">
		<input name="country" type="hidden" value="${country}"/>
		<select id="selectLook" style="width:150px">
			<option value="">查看断货(产品)</option>
			<option value="outOfStock">查看断货(SKU)</option>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<label>断货日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="startDate" value="<fmt:formatDate value="${startDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<label>断货到昨日：</label><input type="checkbox"  class="isCheck" value="${isCheck}" ${isCheck eq '1' ?'checked':'' }/><input type="hidden" name="isCheck" id="isCheck" value="${isCheck}"/>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="expExcel" class="btn btn-warning" type="button" value="导出excel"/>
	</form:form>
	
	<tags:message content="${message}"/>
	<table id="dataTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:20%">产品名</th><th style="width:10%">国家</th><th style="width:5%">断货天数</th><th style="width:300px;word-break:break-all;word-wrap:break-word;">断货日期</th></tr></thead>
		<tbody>
		<c:set value="0" var="total" />
		<c:forEach items="${list}" var="outOfProduct" varStatus="i">
			<tr>
				<td><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${outOfProduct.productNameColor}">${outOfProduct.productNameColor}</a>
				<c:set var="proCountry" value="${outOfProduct.productNameColor}_${outOfProduct.country}"/>
				&nbsp;
				${fns:getDictLabel(productPosition[outOfProduct.productNameColor],'product_position','')}
				&nbsp;
				<c:if test="${fn:contains(newProducts,proCountry)}">新品</c:if>  
				</td>
				<td>${fns:getDictLabel(outOfProduct.country, 'platform', '')}</td>
				<td>${outOfProduct.daySpace}</td>
				<td style="width:300px;word-break:break-all;word-wrap:break-word;">${outOfProduct.dayStr }	</td>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
