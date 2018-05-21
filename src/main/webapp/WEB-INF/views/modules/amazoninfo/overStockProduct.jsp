<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>积压统计</title>
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
			$("#country").on("click",function(){
				$("#searchForm").submit();
			});
			
			$("#selectLook").on("click",function(){
				if($(this).val()!=""){
					var params = {};
					params.country=$("input[name='country']").val();
					window.location.href = "${ctx}/psi/psiInventory/"+$(this).val()+"?"+$.param(params);
				}
			});
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			
			$("#expExcel").click(function(){
				var params = {};
				params.country=$("input[name='country']").val();
				params.type=$("input[name='type']").val();
				window.location.href = "${ctx}/psi/psiInventory/expOverStockProduct?"+$.param(params);
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
				 "aaSorting": [[ 6, "desc" ]]
			});
		});
		
		
		function searchType(type){
			if($("#type").val()==type){
				return;
			}
			$("#type").val(type);
			$("#searchForm").submit();
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
		<li class="${empty country ?'active':''}"><a class="countryHref" href="#" key="">总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	    
	</ul>
	<form:form id="searchForm"  action="${ctx}/psi/psiInventory/overStockProduct" method="post" class="breadcrumb form-search">
		<input name="country" type="hidden" value="${country}"/>
		<input name="type" id="type" type="hidden" value="${type}"/>
		<select id="selectLook" style="width:150px;float:left">
			<option value="">查看积压(产品)</option>
			<option value="overStock">查看积压(SKU)</option>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;&nbsp;<input id="expExcel" class="btn btn-warning" type="button" value="导出excel"/>
			<ul class="nav nav-pills" style="width:300px;float:left;" id="myTab">
				<li data-toggle="pills" class="${type eq '3'?'active':''}"><a href="#" onclick="javaScript:searchType('3')">超3个月</a></li>
				<li data-toggle="pills" class="${type eq '4'?'active':''}"><a href="#" onclick="javaScript:searchType('4')">超4个月</a></li>
				<li data-toggle="pills" class="${type eq '5'?'active':''}"><a href="#" onclick="javaScript:searchType('5')">超5个月</a></li>
			</ul>
	</form:form>
	<b style="color: red">注意：淘汰产品和新品不计入积压</b>
	
	<tags:message content="${message}"/>
	<table id="dataTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:15%">产品名</th><th style="width:10%">国家</th><th style="width:10%">当前FBA库存数</th><th style="width:10%">最近30天销量</th><th style="width:10%">可售月</th></tr></thead>
		<tbody>
		<c:forEach items="${overMap}" var="countryEntry" varStatus="i">
			<c:forEach items="${countryEntry.value}" var="proName">
				<tr>
				<td><a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${proName}">${proName}</a></td>
				<td>${fns:getDictLabel(countryEntry.key,'platform','')}</td>
				<td> ${fn:split(nameColorfbaMap[countryEntry.key][proName],',')[0]}</td>
				<td> ${sale31Map[countryEntry.key][proName]}</td>
				<td> 
					<c:if test="${not empty sale31Map[countryEntry.key][proName] && sale31Map[countryEntry.key][proName]!='0'}">
						<c:set value="${fn:split(nameColorfbaMap[countryEntry.key][proName],',')[0]}" var="fbaQ" />
						<c:set value="${sale31Map[countryEntry.key][proName]}" var="saleQ" />
						<fmt:formatNumber  maxFractionDigits="2"  value="${fbaQ/saleQ}"/> 
					</c:if>
				</td>
				</tr>
			</c:forEach>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
