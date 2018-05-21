<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Reviewer Email manager</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<link href="${ctxStatic}/common/mailstate.css" type="text/css" rel="stylesheet" />
	<%@include file="/WEB-INF/views/include/datatables.jsp"%>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		.spanexr {
			float: right;
			min-height: 40px
		}
		
		.spanexl {
			float: left;
		}
		
		.footer {
			padding: 20px 0;
			margin-top: 20px;
			border-top: 1px solid #e5e5e5;
			background-color: #f5f5f5;
		}
	</style>
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
			$(".shake").each(function(){
				shake($(this),"blue",20);
			});
			
			
			$("#flag").change(function(){
				$("#searchForm").attr("action","${ctx}/amazoninfo/reviewerEmail");
				$("#searchForm").submit();
			});
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			$("#contentTable").dataTable({
				"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 20,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"ordering" : true,
				"aaSorting": [[ 7, "asc" ]]
			});
		});
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#"><spring:message code='custom_email_list'/></a></li>
		<li><a href="${ctx}/amazoninfo/reviewer/reviewEventList">评测进度</a></li>
	</ul>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
		<th><div class="ico_mailtitle">&nbsp;</div></th>
		<!-- 发件人 -->
		<th><spring:message code="custom_email_form1" /></th>
		<!-- 发件人所属国家 -->
		<th><spring:message code='sys_label_country'/></th>
		<!-- 来至服务器邮箱 -->
		<th><spring:message code="custom_email_form2" /></th>
		<!-- 主题 -->
		<th><spring:message code="custom_email_form3" /></th>
		<!-- 负责人 -->
		<th><spring:message code="custom_email_form4" /></th>
		<!-- 邮件状态 -->
		<th><spring:message code="custom_email_form6" /></th>
		<!-- 客户发件时间 -->
		<th><spring:message code="custom_email_form9" /></th>
		<!-- 操作 -->
		<th ><spring:message code="sys_label_tips_operate"/></th></tr></thead>
		<tbody>
		<c:forEach items="${list}" var="email">
			<tr>
				<td class="ci"><div class="ciz${'1' eq email.urgent ?' Zh':''}"></div><div  class="cir ${email.stateCls}" ></div><div class="cij${not empty email.attchmentPath || not empty email.inlineAttchmentPath ?' Ju':''}"></div></td>
				<td>${email.revertEmail}</td>
				<td>${fns:getDictLabel(email.formReviewer.country,'platform','')}</td>
				<td>${email.revertServerEmail}</td>
				<td><a class="open" href="${ctx}/amazoninfo/reviewerEmail/${email.state eq '0' || email.state eq '1'?'form':'view'}?id=${email.id}" class="${email.shake?'shake':'' }"  rel="popover" data-content="${email.subject}">${empty fn:substring(email.subject,0,30)?'Empty':fn:substring(email.subject,0,30)}</a></td>
				<td>${email.masterBy.name}</td>
				<td>${email.stateStr}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd H:mm" value="${email.customSendDate}"/></td>
				<td>
					<%--
					<c:if test="${email.hasEvent}">
						<a href="${ctx}/amazoninfo/reviewer/viewEvent?id=${email.id}">事件跟踪</a>&nbsp;&nbsp;
					</c:if> --%>
					<a href="${ctx}/amazoninfo/reviewerEmail/form?id=${email.id}"><spring:message code="custom_email_btn5" /></a>&nbsp;&nbsp;
					<shiro:hasPermission name="reviewer:email:edit"><a href="${ctx}/amazoninfo/reviewerEmail/delete?id=${email.id}" onclick="return confirmx('Confirm you want to delete the email?', this.href)">Delete</a></shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
