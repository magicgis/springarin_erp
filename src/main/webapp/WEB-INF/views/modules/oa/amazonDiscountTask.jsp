<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊打折一览</title>
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
		<li class="active"><a href="${ctx}/oa/amazonDiscount/list/task">待办列表</a></li>
		<li><a href="${ctx}/oa/amazonDiscount/list">亚马逊打折列表</a></li>
		<shiro:hasPermission name="oa:amazonDiscount:view"><li><a href="${ctx}/oa/amazonDiscount/form">亚马逊打折申请</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="amazonDiscount" action="${ctx}/oa/amazonDiscount/list/task" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div>			
		<label>创建日期 ：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/oa/amazonDiscount/list/task');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDateStart" value="<fmt:formatDate value="${amazonDiscount.createDateStart}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;至&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/oa/amazonDiscount/list/task');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDateEnd" value="<fmt:formatDate value="${amazonDiscount.createDateEnd}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
		</div>
		
	</form:form>
	<tags:message content="${message}"/>
	<table style="width: 95%" id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<th style="width: 100px">申请人</th>
			<th style="width: 220px">打折范围</th>
			<th style="width: 220px">亚马逊打折理由</th>
			<th style="width: 80px">打折金额</th>
			<th style="width: 80px">申请时间</th>
			<th style="width: 80px">当前状态</th>
			<th >操作</th>
		</tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="amazonDiscount">
			<tr>
				<td>${amazonDiscount.createBy.name}</td>
				<td><a href="${ctx}/oa/amazonDiscount/detail?id=${amazonDiscount.id}" rel="popover" data-content="${amazonDiscount.discountScope}">${fn:substring(amazonDiscount.discountScope,0,20)}</a></td>
				<td><a href="${ctx}/oa/amazonDiscount/detail?id=${amazonDiscount.id}" rel="popover" data-content="${amazonDiscount.reason}">${fn:substring(amazonDiscount.reason,0,20)}</a></td>
				<td>${amazonDiscount.price}</td>
				<td><fmt:formatDate value="${amazonDiscount.createDate}" pattern="yyyy-M-d"/></td>
				<td>${amazonDiscount.processStatus}</td>
				<td>
					<a class="encode" href="${ctx}/oa/amazonDiscount/detail?id=${amazonDiscount.id}&audit=1&state=${amazonDiscount.processStatus}">${amazonDiscount.processStatus}</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
