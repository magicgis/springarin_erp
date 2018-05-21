<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊后台账号管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">.sort{color:#0663A2;cursor:pointer;}</style>
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
			
			$(".change").change(function(){
				$("#searchForm").submit();
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
		<li><a href="${ctx}/amazoninfo/amazonUser/">亚马逊后台账号列表</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/amazonUser/logList">亚马逊后台登录记录</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="amazonLoginLog" action="${ctx}/amazoninfo/amazonUser/logList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		平台：<select name="country" id="country" style="width: 120px" class="change">
				<option value="" ${empty amazonLoginLog.country?'selected':''}>---All---</option>
				<option value="eu" ${amazonLoginLog.country eq 'eu' ?'selected':''}>欧洲|EUR</option>
				<option value="com" ${amazonLoginLog.country eq 'com' ?'selected':''}>美国|US</option>
				<option value="jp" ${amazonLoginLog.country eq 'jp' ?'selected':''}>日本|JP</option>
				<option value="ca" ${amazonLoginLog.country eq 'ca' ?'selected':''}>加拿大|CA</option>
			</select>&nbsp;&nbsp;
			<label>操作人：</label>
			<select name="user.id" style="width: 100px" class="change">
				<option ${empty amazonLoginLog.user.id?'selected':''} value="">All</option>
				<c:forEach items="${users}" var="user">
					<option ${user.id eq amazonLoginLog.user.id?'selected':''} value="${user.id}">${user.name}</option>
				</c:forEach>
			</select>
			<label>操作时间：</label>
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="dataDate" value="<fmt:formatDate value="${amazonLoginLog.dataDate}" pattern="yyyy-MM-dd" />" id="dataDate" class="input-small"/>
				&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${amazonLoginLog.endDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="endDate"/>
				&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th class="sort user">操作人</th>
				<th class="sort dataDate">登录时间</th>
				<th class="sort country">平台</th>
				<th class="sort ip">IP地址</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="log">
			<tr>
				<td>${log.user.name}</td>
			 	<td><fmt:formatDate value="${log.dataDate}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
				<td>
					<c:if test="${log.country eq 'eu'}">欧洲|EUR</c:if>
					<c:if test="${log.country ne 'eu'}">${fns:getDictLabel(log.country,'platform','')}</c:if>
				</td>
				<td>${log.ip}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
