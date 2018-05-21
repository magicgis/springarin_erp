<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>系统邮件列表</title>
	<%@ include file="/WEB-INF/views/include/dialog.jsp"%>
	<meta name="decorator" content="default"/>
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

			$("a[rel='popover']").popover({html:true,trigger:'hover'});
			
			$(".autoSubmit").change(function(){
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
		<li class="active"><a href="#">邮件订阅列表</a></li>
		<li><a href="${ctx}/amazoninfo/newsSubscribe/form">添加</a></li>
	</ul>
	
	<form:form id="searchForm" modelAttribute="newsType" action="${ctx}/amazoninfo/newsType" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 40px;line-height: 40px">
			<div style="height: 40px;">
				状态：<select name="state" id="state" style="width: 120px" class="autoSubmit">
						<option value="" ${empty newsType.state?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<option value="1" ${newsType.state eq '1'?'selected':''}  >启用</option>
						<option value="0" ${newsType.state eq '0'?'selected':''}  >停用</option>
				</select>&nbsp;&nbsp;
				条件类型：<select name="type" id="type" style="width: 120px" class="autoSubmit">
						<option value="" ${empty newsType.type?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<option value="1" ${newsType.type eq '1'?'selected':''}  >产品</option>
						<option value="2" ${newsType.type eq '2'?'selected':''}  >产品类型</option>
						<option value="3" ${newsType.type eq '3'?'selected':''}  >产品线</option>
						<option value="4" ${newsType.type eq '4'?'selected':''}  >产品属性</option>
				</select>
			    <input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>	
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
			   <th class="sort id">编号</th>
			   <th class="sort name">邮件名称</th>
			   <th class="sort email">收件箱</th>
			   <th class="sort productName">条件类型</th>
			   <th class="sort emailType">订阅邮件</th>
			   <th class="sort state">状态</th>
			   <th><spring:message code="sys_label_tips_operate"/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="newsType">
			<tr>
				<td>${newsType.id}</td>
				<td>${newsType.name}</td>
				<td>${newsType.type}</td>
				<td><a href="${ctx}/amazoninfo/newsSubscribe/view?id=${newsSubscribe.id}" rel="popover" data-content="${newsSubscribe.subscribeStr}">${empty fn:substring(newsSubscribe.subscribeStr,0,25)?'':fn:substring(newsSubscribe.subscribeStr,0,25)}</a></td>
				<td>
					<c:if test="${'1' eq newsType.type }">产品</c:if>
					<c:if test="${'2' eq newsType.type }">产品类型</c:if>
					<c:if test="${'3' eq newsType.type }">产品线</c:if>
					<c:if test="${'4' eq newsType.type }">产品属性</c:if>
				</td>
				<td><a href="${ctx}/amazoninfo/newsSubscribe/view?id=${newsSubscribe.id}" rel="popover" data-content="${newsSubscribe.emailTypeStr}">${empty fn:substring(newsSubscribe.emailTypeStr,0,15)?'':fn:substring(newsSubscribe.emailTypeStr,0,15)}</a></td>
				<td>
					<c:if test="${'0' eq newsType.state }">停用</c:if>
					<c:if test="${'1' eq newsType.state }">启用</c:if>
				</td>
				<td>
					<a class="btn btn-info btn-small" href="${ctx}/amazoninfo/newsSubscribe/form?id=${newsType.id}">编辑</a>
					<c:if test="${'0' eq newsType.state }">
						<a class="btn btn-primary btn-small" href="${ctx}/amazoninfo/newsSubscribe/updateState?state=1&id=${newsType.id}">启用</a>
					</c:if>
					<c:if test="${'1' eq newsType.state }">
						<a class="btn btn-success btn-small" href="${ctx}/amazoninfo/newsSubscribe/updateState?state=0&id=${newsType.id}">停用</a>
					</c:if>
					<a class="btn btn-warning btn-small" href="${ctx}/amazoninfo/newsSubscribe/delete?id=${newsType.id}" onclick="return confirmx('确认要删除吗？', this.href)">删除</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
