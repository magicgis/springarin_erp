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
		
			function submit(month,dep,user){
				var params = {};
				params.month = month;
				params.dep = dep;
				params.userId = user;
				window.location.href = "${ctx}/plan/month/monthView?"+$.param(params);
			}
			
			$("#dep").change(function(){
				submit('${month}',$("#dep").val(),'');
			});
			$("#user").change(function(){
				submit('${month}',$("#dep").val(),$(this).val());
			});
			
			$($(".month").get((${month}-1))).parent().addClass("active");
			
			$(".month").click(function(){
				submit($(".month").index(this)+1,$("#dep").val(),$("#userId").val());
			});
		});	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/plan/month/view">日志填报按周查看</a></li>
		<li class="active"><a href="#">日志填报按月查看</a></li>
	</ul>
	<div style="height: 50px;line-height: 50px">
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
	<div class="pagination" style="float: right;height: 28px;">
		  <ul>
		    <li><a class="month">1月</a></li>
		    <li><a class="month">2月</a></li>
		    <li><a class="month">3月</a></li>
		    <li><a class="month">4月</a></li>
		    <li><a class="month">5月</a></li>
		    <li><a class="month">6月</a></li>
		    <li><a class="month">7月</a></li>
		    <li><a class="month">8月</a></li>
		    <li><a class="month">9月</a></li>
		    <li><a class="month">10月</a></li>
		    <li><a class="month">11月</a></li>
		    <li><a class="month">12月</a></li>
		  </ul>
		  &nbsp;&nbsp;&nbsp;&nbsp;
	</div>
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
			<thead><tr>
				<th colspan="2"><a href="${ctx}/plan/month?year=${year}&month=${month}&dep=${depId}&userId=${userIdMap[userPlan.key]}">${userPlan.key}工作计划列表</a><span class="help-inline">点击可直接修改</span></th></tr></thead>
			<tbody>
				<tr>
				<td style="width:200px;text-align: center;vertical-align: middle;"><h5>${month}月工作计划 :</h5></td>
				<td style="background-color: rgb(50,50,50)">
				<pre class="decodeHtml personCPre" >${userPlan.value.monthPlan[0].content}</pre>
				</td></tr>
				<c:forEach var="weekPlan" items="${userPlan.value.weekPlan}" varStatus="i">
				<c:if test="${weekPlan!=null && not empty weekPlan.content}">
				<tr>
					<td style="text-align: center;vertical-align: middle;" valign="middle"><h5>第${i.count}周工作计划</h5><fmt:formatDate value="${weekPlan.updateDate}" type="both"/>编辑</td>
					<td style="background-color: rgb(${204-20*i.count},${204-20*i.count},${204-20*i.count})"><span style="color:white;"></span>
					<pre class="decodeHtml">${weekPlan.content}</pre>
					</td>
				</tr>
				</c:if>
				</c:forEach>
				<c:set var="flag" scope="page" value="1" />
				<c:forEach var="logPlan" items="${userPlan.value.log}" varStatus="i">
					<c:if test="${i.index>0 && fns:subtraction(logPlan.flag,userPlan.value.log[i.index-1].flag)>=2}">
						<c:set var="flag" scope="page" value="${flag+1}" />
					</c:if>
					<tr>
						<td style="text-align: center;vertical-align: middle;" valign="middle"><fmt:parseDate pattern="yyyy/MM/dd" var="temp" value="${logPlan.flag}" />
							<h5><fmt:formatDate value="${temp}" pattern="E"/>(${logPlan.flag})日志</h5>于<fmt:formatDate value="${logPlan.updateDate}" type="both"/>编辑</td>
						<td style="background-color: rgb(${204-30*flag},${204-40*flag},${204-40*flag})">
							<pre class="decodeHtml">${logPlan.content}</pre>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:forEach>
</body>
</html>
