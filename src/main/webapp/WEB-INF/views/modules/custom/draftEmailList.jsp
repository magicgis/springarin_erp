<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>draft Email Manager</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	
	<link href="${ctxStatic}/common/mailstate.css" type="text/css" rel="stylesheet" />
	
	<script type="text/javascript">
		
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
	
		function shake(ele,cls,times){
			var i = 0,t= false ,o =ele.attr("class")+" ",c ="",times=times||2;
			if(t) return;
			t= setInterval(function(){
				i++;
				c = i%2 ? o+cls : o;
				ele.attr("class",c);
				if(i==2*times){
					clearInterval(t);
					ele.removeClass(cls);
					}
				},200);
		};
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
			
			$(".shake").each(function(){
				shake($(this),"blue",20);
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
		<li class="active"><a href="${ctx}/custom/sendEmail/draft"><spring:message code="custom_email_draftList" /></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="sendEmail" action="${ctx}/custom/sendEmail/draft" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 40px;line-height: 40px">
			<span style="float: right;">
			<label><spring:message code="custom_email_search1" />:</label><form:input path="sendSubject" htmlEscape="false" maxlength="50" class="input-small"/>
			&nbsp;
			<input class="btn btn-primary" type="submit" value="<spring:message code='sys_but_search' />"/>
			</span>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
		<th style="width: 160px"><spring:message code="custom_email_form15" /></th>
		<th style="width: 260px"><spring:message code="custom_email_form3" /></th>
		<th style="width: 110px"><spring:message code="custom_email_form14" /></th>
		<th style="width: 50px" class="sort sendFlag"><spring:message code="custom_email_form6" /></th>
		<th style="width: 50px">CustomEmail</th>
		<th style="width: 120px"><spring:message code="sys_label_tips_operate" /></th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="email">
			<tr>
				<td>${email.sendEmail}</td>
				<td><a href="${ctx}/custom/sendEmail/${email.sendFlag eq '0'?'form':'view'}?id=${email.id}" class="${email.shake?'shake':'' }">${email.sendSubject}</a></td>
				<td><fmt:formatDate type="both" value="${email.sentDate}"/></td>
				<td>
					${email.sendFlag eq '0' ?'Draft':(email.sendFlag eq '2'?'Undelivered message':'Sent')}
				</td>
				<td>
					<c:if test="${not empty email.customEmail}">
						<a href="${ctx}/custom/emailManager/view?id=${email.customEmail.id}&all"><spring:message code="sys_but_view" /></a>
					</c:if>
				</td>
				<td>
					<c:choose>
						<c:when test="${email.sendFlag eq '0'}">
							<a href="${ctx}/custom/sendEmail/form?id=${email.id}"><spring:message code="sys_but_edit" /></a>&nbsp;&nbsp;
						</c:when>
						<c:when test="${email.sendFlag eq '1'}">
							<a href="${ctx}/custom/sendEmail/recall?id=${email.id}"><spring:message code="custom_email_btn10" /></a>&nbsp;&nbsp;
							<a href="${ctx}/custom/sendEmail/view?id=${email.id}"><spring:message code="sys_but_view" /></a>&nbsp;&nbsp;
						</c:when>
					</c:choose>
					<c:if test="${empty email.customEmail || email.customEmail.state != '2'}">
						<a href="${ctx}/custom/sendEmail/delete?id=${email.id}" onclick="return confirmx('Confirm you want to delete the email?', this.href)">Delete</a>
					</c:if>
				</td>
				
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
