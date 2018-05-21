<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品建议统计</title>
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

			$("a[rel='popover']").popover({html:true,trigger:'hover'});
			      
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
				"aaSorting": [[ 4, "desc" ]]
			});
		
			
			$("#expExcel").click(function(){
				 $("#searchForm").attr("action","${ctx}/custom/suggestion/expSuggestion/");
				 $("#searchForm").submit();
				 $("#searchForm").attr("action","${ctx}/custom/suggestion/");
			});
		
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="${empty suggestion.country ?'active':''}"><a class="countryHref" href="#" key=""><spring:message code='custom_event_all'/></a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${suggestion.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
		<li><a href="${ctx}/custom/emailManager/"><spring:message code='custom_email_list'/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="suggestion" action="${ctx}/custom/suggestion/" method="post" class="breadcrumb form-search">
		<input name="country" type="hidden" value="${suggestion.country}"/>
		<label><spring:message code="custom_email_template_createTime"/>：</label>
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${suggestion.createDate}" pattern="yyyy-MM-dd" />" id="createDate" class="input-small"/>
				&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${suggestion.endDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="endDate"/>
		&nbsp;&nbsp;&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
		&nbsp;&nbsp;&nbsp;&nbsp;<input id="expExcel" class="btn btn-warning" type="button" value="<spring:message code="sys_but_export"/>"/>
			
	</form:form>
	<table id="dataTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<th style="width: 10%">country</th>
			<th style="width: 20%">Product Name</th>
			<th style="width: 20%">Product Type</th>
			<th style="width: 10%">Product Manager</th>
			<th style="width: 10%">Create Date</th>
			<th style="width: 10%">Content</th>
		</thead>
		<tbody>
		<c:forEach items="${list}" var="suggestion">
			<tr>
				<td>${fns:getDictLabel(suggestion.country,'platform','')}</td>
				<td><a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${suggestion.productName}">${suggestion.productName}</a></td>
				<td>${suggestion.productType}</td>
				<td>${mangerMap[suggestion.productType]}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${suggestion.createDate}"/></td>
				<td><a href="#" rel="popover" data-content="${suggestion.content}" data-placement="left">${fns:abbr(suggestion.content,18)}</a></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
