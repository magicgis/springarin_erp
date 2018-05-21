<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>邮件订阅列表</title>
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
	
	<form:form id="searchForm" modelAttribute="newsSubscribe" action="${ctx}/amazoninfo/newsSubscribe" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 40px;line-height: 40px">
			<div style="height: 40px;">
				平台：<select name="platform" id="platform" style="width: 120px" class="autoSubmit">
						<option value="" ${empty newsSubscribe.platform?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<c:if test="${dic.value ne 'com.unitek' && dic.value ne 'mx'}">
								<option value="${dic.value}" ${newsSubscribe.platform eq dic.value?'selected':''}  >${dic.label}</option>
							</c:if>
						</c:forEach>
				</select>&nbsp;&nbsp;
				状态：<select name="state" id="state" style="width: 120px" class="autoSubmit">
						<option value="" ${empty newsSubscribe.state?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<option value="1" ${newsSubscribe.state eq '1'?'selected':''}  >启用</option>
						<option value="0" ${newsSubscribe.state eq '0'?'selected':''}  >停用</option>
				</select>&nbsp;&nbsp;
				条件类型：<select name="type" id="type" style="width: 120px" class="autoSubmit">
						<option value="" ${empty newsSubscribe.type?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<option value="1" ${newsSubscribe.type eq '1'?'selected':''}  >产品</option>
						<option value="2" ${newsSubscribe.type eq '2'?'selected':''}  >产品类型</option>
						<option value="3" ${newsSubscribe.type eq '3'?'selected':''}  >产品线</option>
						<option value="4" ${newsSubscribe.type eq '4'?'selected':''}  >产品属性</option>
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
			   <th class="sort platform">平台</th>
			   <th class="sort createDate">创建时间</th>
			   <th class="sort email">收件箱</th>
			   <th class="sort productName">订阅条件</th>
			   <th class="sort type">条件类型</th>
			   <th class="sort emailType">订阅邮件</th>
			   <th class="sort state">状态</th>
			   <th><spring:message code="sys_label_tips_operate"/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="newsSubscribe">
			<tr>
				<td>${newsSubscribe.id}</td>
				<%--<td>${fns:getDictLabel(newsSubscribe.platform,'platform','')}</td> --%>
				<td>${fn:replace(fn:toUpperCase(newsSubscribe.platform),"COM","US")}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${newsSubscribe.createDate}"/></td>
				<td>${newsSubscribe.email}</td>
				<td><a href="${ctx}/amazoninfo/newsSubscribe/view?id=${newsSubscribe.id}" rel="popover" data-content="${newsSubscribe.subscribeStr}">${empty fn:substring(newsSubscribe.subscribeStr,0,25)?'':fn:substring(newsSubscribe.subscribeStr,0,25)}</a></td>
				<td>
					<c:if test="${'1' eq newsSubscribe.type }">产品</c:if>
					<c:if test="${'2' eq newsSubscribe.type }">产品类型</c:if>
					<c:if test="${'3' eq newsSubscribe.type }">产品线</c:if>
					<c:if test="${'4' eq newsSubscribe.type }">产品属性</c:if>
				</td>
				<td><a href="${ctx}/amazoninfo/newsSubscribe/view?id=${newsSubscribe.id}" rel="popover" data-content="${newsSubscribe.emailTypeStr}">${empty fn:substring(newsSubscribe.emailTypeStr,0,15)?'':fn:substring(newsSubscribe.emailTypeStr,0,15)}</a></td>
				<td>
					<c:if test="${'0' eq newsSubscribe.state }">停用</c:if>
					<c:if test="${'1' eq newsSubscribe.state }">启用</c:if>
				</td>
				<td>
					<a class="btn btn-info btn-small" href="${ctx}/amazoninfo/newsSubscribe/form?id=${newsSubscribe.id}">编辑</a>
					<c:if test="${'0' eq newsSubscribe.state }">
						<a class="btn btn-primary btn-small" href="${ctx}/amazoninfo/newsSubscribe/updateState?state=1&id=${newsSubscribe.id}">启用</a>
					</c:if>
					<c:if test="${'1' eq newsSubscribe.state }">
						<a class="btn btn-success btn-small" href="${ctx}/amazoninfo/newsSubscribe/updateState?state=0&id=${newsSubscribe.id}">停用</a>
					</c:if>
					<a class="btn btn-warning btn-small" href="${ctx}/amazoninfo/newsSubscribe/delete?id=${newsSubscribe.id}" onclick="return confirmx('确认要删除吗？', this.href)">删除</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
