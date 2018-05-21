<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>任务管理</title>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
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
		$(document).ready(function() {

			
			$("#contentTable").dataTable({
				"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				"aaSorting": [[ 2, "asc" ]]
			});
		});
		
		function changeJobStatus(action, jobName, jobGroup){
			$("#action").val(action);
			$("#jobName").val(jobName);
			$("#jobGroup").val(jobGroup);
			$("#searchForm").attr("action","${ctx}/sys/job/changeStatus");
			$("#searchForm").submit();
		}

		function updateCron(jobName, jobGroup){
			var cron = prompt("输入cron表达式！", "");
			if (cron) {
				$("#cronExpression").val(cron);
				$("#jobName").val(jobName);
				$("#jobGroup").val(jobGroup);
				$("#searchForm").attr("action","${ctx}/sys/job/updateCron");
				$("#searchForm").submit();
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="${empty name?'active':''}"><a class="countryHref" href="#" key="">本地任务</a></li>
		<c:forEach items="${wsConfigs}" var="config">
			<li><a href="${ctx}/sys/job/wsList?name=${config.accountName}&host=${config.serverIp}">${config.accountName}(${config.serverIp})</a></li>
		</c:forEach>
	</ul>
	<form:form id="searchForm" modelAttribute="user" action="${ctx}/sys/job/" method="post" style="margin:0">
		<input id="action" name="action" type="hidden" value=""/>
		<input id="jobName" name="jobName" type="hidden" value=""/>
		<input id="jobGroup" name="jobGroup" type="hidden" value=""/>
		<input id="cronExpression" name="cronExpression" type="hidden" value=""/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<td>描述</td>
				<td>触发器</td>
				<%--<td>name</td> --%>
				<td>状态&nbsp;&nbsp;</td>
				<td>cron表达式</td>
				<td>下次触发时间</td>
				<td>操作</td>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="job" items="${list}">
				<tr>
					<td>${job.jobGroup }</td>
					<td>${job.description }</td>
					<td>
						<c:choose>
							<c:when test="${job.jobStatus=='NORMAL' }">
								空闲
							</c:when>
							<c:when test="${job.jobStatus=='BLOCKED' }">
								执行中
							</c:when>
							<c:otherwise>
								${job.jobStatus }
							</c:otherwise>
						</c:choose>
					</td>
					<td>${job.cronExpression }</td>
					<td>${job.updateTime }</td>
					<td>
						<input type="button" class="btn btn-primary" onclick="changeJobStatus('run','${job.jobName}','${job.jobGroup}')" value="执行一次"/>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>

