<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品问题统计</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<link href="${ctxStatic}/common/mailstate.css" type="text/css" rel="stylesheet" />
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
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
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			      
			$("#dataTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				 "aaSorting": [[ 3, "asc" ]]
			});
		
			
			$("#expExcel").click(function(){
				var params = {};
				params.createDate=$("input[name='createDate']").val();
				params.dataDate=$("input[name='dataDate']").val();
				window.location.href = "${ctx}/custom/productProblem/expProblem?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
		
		});
	</script>
</head>
<body>
	
		<ul class="nav nav-tabs">
		<li class="${empty productProblem.country ?'active':''}"><a class="countryHref" href="#" key="">全部</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${productProblem.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>
	<form:form id="searchForm" modelAttribute="productProblem" action="${ctx}/custom/productProblem" method="post" class="breadcrumb form-search">
		<input name="country" type="hidden" value="${productProblem.country}"/>
		<label>创建日期：</label>
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="dataDate" value="<fmt:formatDate value="${productProblem.dataDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${productProblem.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
		&nbsp;&nbsp;&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		&nbsp;&nbsp;&nbsp;&nbsp;<input id="expExcel" class="btn btn-warning" type="button" value="导出excel"/>
			
	</form:form>
	<table id="dataTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<th style="width: 20%">Product Name</th>
			<th style="width: 10%">Product Manager</th>
			<th style="width: 10%">Sales Quantity</th>
			<th style="width: 10%">Faulty Rate(%)</th>
			<th style="width: 10%">Return Quantity</th>
			<th style="width: 10%">Return Rate</th>
			<th style="width: 30%">Problem Type <span style="float:right;margin-right:350px">Problem Number</span></th>
		</thead>
		<tbody>
		<c:forEach items="${problemMap}" var="masterMap">
			<tr>
				<td><a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${masterMap.key}">${masterMap.key}</a></td>
				<td>${mangerMap[productMap[masterMap.key]]}</td>
				<td>${saleMap[masterMap.key]}</td>
				<td>
				<c:if test="${not empty saleMap[masterMap.key] && not empty problemNumMap[masterMap.key] && problemNumMap[masterMap.key] ne 0 }">
					<fmt:formatNumber value="${problemNumMap[masterMap.key]*100/saleMap[masterMap.key]}" maxFractionDigits="2" pattern="0.##"/></td>
				</c:if>
				<td>${returnMap[masterMap.key][1]}</td>
				<td><c:if test="${not empty saleMap[masterMap.key]}"><fmt:formatNumber value="${returnMap[masterMap.key][1]*100/saleMap[masterMap.key]}" maxFractionDigits="2" pattern="0.##"/></td></c:if></td>
				<td>
					<c:forEach items="${masterMap.value}" var="innerMap">
						<span>${innerMap.key}</span><span style="float:right;margin-right:350px">
						<a target="_blank" href="${ctx}/custom/productProblem/detail?productName=${masterMap.key}&problemType=${innerMap.key}&country=${productProblem.country}&createDate=<fmt:formatDate value="${productProblem.createDate}" pattern="yyyy-MM-dd"/>&dataDate=<fmt:formatDate value="${productProblem.dataDate}" pattern="yyyy-MM-dd"/>">${innerMap.value}</a>
						</span><br/>
					</c:forEach>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
