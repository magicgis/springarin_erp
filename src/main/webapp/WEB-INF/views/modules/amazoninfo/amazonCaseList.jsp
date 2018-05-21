<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Amazon Case List</title>
	<meta name="decorator" content="default"/>
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
	
		$(document).ready(function() {
			if(!(top)){
				top = self; 
			}
			$("#pType").val($("#productType").val());
			$("a[rel='popover']").popover({trigger:'hover'});
			
			// 表格排序
			var orderBy = $("#orderBy").val().split(" ");
			$("#contentTable th.sort").each(function(){
				if ($(this).hasClass(orderBy[0])){
					orderBy[1] = orderBy[1]&&orderBy[1].toUpperCase()=="DESC"?"down":"up";
					$(this).html($(this).html()+" <i class=\"icon icon-arrow-"+orderBy[1]+"\"></i>");
				}
			});
			$("#contentTable th.sort").click(function(){
				var order = $(this).attr("class").split(" ");
				var sort = $("#orderBy").val().split(" ");
				for(var i=0; i<order.length; i++){
					if (order[i] == "sort"){order = order[i+1]; break;}
				}
				if (order == sort[0]){
					sort = (sort[1]&&sort[1].toUpperCase()=="DESC"?"ASC":"DESC");
					$("#orderBy").val(order+" DESC"!=order+" "+sort?"":order+" "+sort);
				}else{
					$("#orderBy").val(order+" ASC");
				}
				page();
			});
			
			$(".back").click(function(){
				var country = '${reviewerContent.reviewer.country}';
				window.location.href = "${ctx}/amazoninfo/reviewer?country=" + country;
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
		
		function changeBrandType(){
			$("#searchForm").submit();
		}
		
		function autoSearch(){
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/amazoninfo/amazonCase/list">Case列表</a></li>
		<li><a href="${ctx}/amazoninfo/amazonCase/">添加Case</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="amazonCase" action="${ctx}/amazoninfo/amazonCase/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="productType" name="productType" type="hidden" value="${reviewerContent.productType}"/>
		<label>创建者：</label>
		<select name="createBy.id" style="width: 120px" id="createBy.id" onchange="autoSearch()">
			<option value=""><spring:message code='custom_event_all'/></option>
			<c:forEach items="${all}" var="user">
				<option value="${user.id}" ${amazonCase.createBy.id eq user.id?'selected':''} >${user.name}</option>
			</c:forEach>
		</select>
		&nbsp;&nbsp;&nbsp;<label>平台:</label>
		<select name="country" id="country" style="width: 120px" onchange="autoSearch()">
				<option value=""><spring:message code='custom_event_all'/></option>
				<c:forEach items="${fns:getDictList('platform')}" var="dic">
					<c:if test="${dic.value ne 'com.unitek' && dic.value ne 'mx'}">
						<option value="${dic.value}" ${amazonCase.country eq dic.value?'selected':''} >${dic.label}</option>
					</c:if>
				</c:forEach>	
			</select>
		&nbsp;&nbsp;&nbsp;<label>CaseId/Asin:</label><form:input type="text" path="caseId" value="${amazonCase.caseId}"/>
		&nbsp;&nbsp;&nbsp;<input class="btn btn-primary" type="submit" value="<spring:message code='sys_but_search' />"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
		<th class="sort country">国家</th>
		<th class="sort createBy">创建者</th>
		<th class="sort asin">asin</th>
		<th class="sort subject">subject</th>
		<th class="sort caseId">CaseId</th>
		<th class="sort sentDate">创建时间</th>
		</tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="amazonCase">
			<tr>
				<td>${fns:getDictLabel(amazonCase.country,'platform','')}</td>
				<td>${amazonCase.createBy.name}</td>
				<td>${amazonCase.asin}</td>
				<td><a rel="popover" data-content="${amazonCase.subject}">${empty fn:substring(amazonCase.subject,0,30)?'Empty':fn:substring(amazonCase.subject,0,30)}</a></td>
				<td>${amazonCase.caseId}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd HH:mm:ss" value="${amazonCase.sentDate}"/></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
