<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>日常工作计划查看</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		pre{
			border-style: none;
			font: normal;
			font-size: 14px;
			font-family: 宋体;
			font-style: normal
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
		$(document).ready(function() {
			
			$(".decodeHtml").each(function(){
				var str = $(this).text()+"";
				$(this).html(str.decodeHtml());
			});	
		
			function submit(date,dep,user){
				var params = {};
				params.date = date;
				params.dep = dep;
				params.userId = user;
				window.location.href = "${ctx}/plan/month/view?"+$.param(params);
			}
			
			$("#dep").change(function(){
				submit($("#date").val(),$("#dep").val(),'');
			});
			$("#user").change(function(){
				submit($("#date").val(),$("#dep").val(),$(this).val());
			});
		});	
		function cDayFunc(dp){
			if(dp.cal.getDateStr()){
				var params = {};
				params.date = dp.cal.getNewDateStr();
				params.dep = $("#dep").val();
				params.userId = $("#user").val();
				window.location.href = "${ctx}/plan/month/view?"+$.param(params);
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#">日志填报按周查看</a></li>
		<li><a href="${ctx}/plan/month/monthView">日志填报按月查看</a></li>
	</ul>
	<div style="height:58px;line-height: 58px">
	<span>
		部门：<select style="width: 150px;" id="dep">
				<c:forEach items="${fns:getOfficeList()}" var="office">
					<c:choose>
						<c:when test="${depId eq office.id || office.id eq '2' || cdepId eq office.id}">
							<option value="${office.id}" <c:if test="${depId eq office.id}">
								selected="selected"
							</c:if> >${office.name}</option>
						</c:when>
						<c:otherwise>
							<shiro:hasPermission name="sys:plan:view"><option value="${office.id}">${office.name}</option></shiro:hasPermission>
							<c:if test="${office.id==8}">
								<shiro:lacksPermission name="sys:plan:view">
									<shiro:hasPermission name="plan:market:view">
											<option value="${office.id}">${office.name}</option>
									</shiro:hasPermission>
								</shiro:lacksPermission>
							</c:if>
						</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>　
		员工：<select style="width: 150px;" id="user">
				<option value="" ${'' eq userId?'selected':''}>全部</option>
				<c:forEach items="${users}" var="user">
					<option value="${user.id}" ${user.id eq userId?'selected':''}>${user.name}</option>
				</c:forEach>
		</select>　	
	</span>	
	
	
	日期：<input id="date" style="width: 150px;height: 18px;margin-top: 10px" type="text" readonly="readonly"  class="Wdate"
					onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:cDayFunc,isShowClear:false});" value="${date}" />
	</div>
	<table  class="table table-striped table-bordered table-condensed">
		<thead><tr><th>${month}月部门计划</th></tr></thead>
		<tbody>
			<tr><td>
				 <pre class="decodeHtml depcPre">${depPlan.content}</pre>
			</td></tr>
		</tbody>
	</table>
	
	<c:forEach var="userPlan" items="${plans}">
		<table  class="table table-striped table-bordered table-condensed">
			<thead><tr><th colspan="2"><a href="${ctx}/plan/week?year=${year}&month=${month}&week=${week}&userId=${userIdMap[userPlan.key]}">${userPlan.key}工作计划列表</a><span class="help-inline">点击可直接修改</span></th></tr></thead>
			<tbody>
				<tr><td colspan="2">
				${month}月工作计划 : 
				<pre class="decodeHtml personCPre">${userPlan.value.monthPlan[0].content}</pre>
				</td></tr>
				<tr>
					<td style="width:40%">第${week}周工作计划
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<c:if test="${userPlan.value.weekPlan!=null}"><span style="float: right;"><fmt:formatDate value="${userPlan.value.weekPlan[0].updateDate}" type="both"/>编辑</span></c:if>
					<pre class="decodeHtml">${userPlan.value.weekPlan[0].content}</pre>
					</td>
					<td style="width:40%">完成情况&nbsp;&nbsp;&nbsp;&nbsp;:
						<pre class="decodeHtml">${userPlan.value.weekPlan[0].performance}</pre>
					</td>
				</tr>
				<c:forEach var="logPlan" items="${userPlan.value.log}">
					<tr><td colspan="2">
					<fmt:parseDate pattern="yyyy/MM/dd" var="temp" value="${logPlan.flag}" />
					<fmt:formatDate value="${temp}" pattern="E"/>(${logPlan.flag})日志 <span style="float: right;"><fmt:formatDate value="${logPlan.updateDate}" type="both"/>编辑</span>
					<pre class="decodeHtml">${logPlan.content}</pre>
					</td></tr>
				</c:forEach>
			</tbody>
		</table>
	</c:forEach>
</body>
</html>
