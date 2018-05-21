<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Warning Letter</title>
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
			$("#subject1").val($("#subject").val());
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$("#addReviewer").click(function(){
				$('#searchForm').attr('action','${ctx}/amazoninfo/warningLetter/form');
				$("#searchForm").submit();
			});
			
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
			
			$("a[rel='popover']").popover({trigger:'hover'});
		});
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		
		function changeSubject(){
			var pType = encodeURIComponent($("#subject1").val());
			$("#subject").val(pType);
		}
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
	<li class="${empty warningLetter.country ?'active':''}"><a class="countryHref" href="#" key="">总计</a></li>
	<c:forEach items="${fns:getDictList('platform')}" var="dic">
		<c:if test="${dic.value ne 'com.unitek'}">
			<li class="${warningLetter.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
		</c:if>
	</c:forEach>
	</ul>
	<form:form id="searchForm" modelAttribute="warningLetter" action="${ctx}/amazoninfo/warningLetter" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="country" name="country" type="hidden" value="${warningLetter.country}"/>
		<input id="subject" name="subject" type="hidden" value="${warningLetter.subject}"/>
		<div style="line-height: 40px">
			<div >
			<label>主题:</label>
			<input id="subject1" name="subject1" maxlength="50" type="text" class="input-small" onkeyup="changeSubject()"/>
			&nbsp;
			<label>产品名:</label>
			<input id="productName" name="productName" value="${warningLetter.productName }" maxlength="50" type="text" class="input-small"/>
			&nbsp;
			<input class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
		<th class="sort country">平台</th>
		<th class="sort subject">主题</th>
		<th class="sort letterDate">发信日期</th>
		<th ><spring:message code="sys_label_tips_operate"/></th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="warningLetter">
			<tr>
				<td>${fns:getDictLabel(warningLetter.country,'platform','')}</td>
				<td>${warningLetter.subject}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${warningLetter.letterDate}"/></td>
				<td>
					<a class="btn btn-success btn-small" href="${ctx}/amazoninfo/warningLetter/form?id=${warningLetter.id}">查看</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
