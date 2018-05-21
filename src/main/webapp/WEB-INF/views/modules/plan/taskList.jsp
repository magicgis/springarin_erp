<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>任务管理</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
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
		<li class="active"><a href="${ctx}/plan/task/">任务列表</a></li>
		<shiro:hasPermission name="plan:task:edit"><li><a href="${ctx}/plan/task/form">任务添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="task" action="${ctx}/plan/task/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<label>任务主题 ：</label><form:input path="subject" htmlEscape="false" maxlength="50" class="input-small"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:200px">任务主题</th><th style="width: 60px" class="sort createBy">任务发送者</th><th>任务执行者</th>
		<th style="width: 160px">创建时间</th><th style="width: 100px">开始时间</th>
		<th style="width: 100px">结束时间</th>
		<th style="width: 100px">任务状态</th>
		<shiro:hasPermission name="plan:task:edit"><th style="width: 150px">操作</th></shiro:hasPermission></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="task">
			<tr>
				<td>${task.subject}</td>
				<td>${task.createBy.name}</td>
				<td>${task.performer}</td>
				<td><fmt:formatDate type="both" value="${task.createDate}"/></td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${task.startDate}"/></td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${task.endDate}"/></td>
				<td>${task.stateStr}</td>
				<shiro:hasPermission name="plan:task:edit"><td>
					<a href="${ctx}/plan/task/form?id=${task.id}">查看</a>&nbsp;&nbsp;
					<a href="${ctx}/plan/task/delete?id=${task.id}" onclick="return confirmx('确认要删除该任务管理吗？', this.href)">删除</a>
					<c:if test="${task.createBy.id eq cuser.id && task.state eq 0 }">
						&nbsp;&nbsp;<a href="${ctx}/plan/task/finish?id=${task.id}&state=1">结束任务</a>
					</c:if>	
				</td></shiro:hasPermission>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
