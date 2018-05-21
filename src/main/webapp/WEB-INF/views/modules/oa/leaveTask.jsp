<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>请假一览</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	$(function(){
		$(".encode").each(function(){
			$(this).attr("href",encodeURI(encodeURI($(this).attr("href"))));
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
		<li class="active"><a href="${ctx}/oa/leave/list/task">待办列表</a></li>
		<li><a href="${ctx}/oa/leave/list">请假列表</a></li>
		<shiro:hasPermission name="oa:leave:edit"><li><a href="${ctx}/oa/leave/form">请假申请</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="leave" action="${ctx}/oa/leave/list/task" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div>			
		<label>创建日期 ：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/oa/leave/list/task');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDateStart" value="<fmt:formatDate value="${leave.createDateStart}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;至&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/oa/leave/list/task');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDateEnd" value="<fmt:formatDate value="${leave.createDateEnd}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table style="width: 95%" id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<th style="width: 100px">请假人</th>
			<th style="width: 120px">申请时间</th>
			<th>请假原因</th>
			<th style="width: 100px">当前状态</th>
			<th style="width: 100px">操作</th>
		</tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="leave">
			<tr>
				<td>${leave.createBy.name}</td>
				<td><fmt:formatDate value="${leave.createDate}" pattern="yyyy-M-dd H:mm" />  </td>
				<td><a href="${ctx}/oa/leave/detail?id=${leave.id}">${leave.reason}</a></td>
				<td>${leave.processStatus}</td>
				<td>
					<a class="encode" href="${ctx}/oa/leave/detail?id=${leave.id}&audit=1&state=${leave.processStatus}">${leave.processStatus}</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
