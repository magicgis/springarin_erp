<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>日常工作计划管理</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		pre{
			border-style: none
		}
	</style>
	
	<script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script>
	
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		function batchShowText(button){
			$("textarea[id$='Content']").each(function(){
				showText(button,$(this).attr("id"));
			});
			$("textarea[id$='Perf']").each(function(){
				showText(button,$(this).attr("id"));
			});
		}
		
		function showText(button,id,configs){
			//渲染ckeditor出来
			/* var ckeditor = CKEDITOR.replace(id);
			$.each(configs,function(i,v){
				ckeditor.config[i] = v;
			}); */
			//隐藏按钮
			$("#"+id).show();
			$(button).parent().children().show();
			$(button).hide();
			//隐藏pre
			$("."+id+"Pre").hide();
		}
		
		/* function ckeditorReset(id){
			CKEDITOR.instances[id].setData($("textarea #"+id).val());
		}
		
		function batchCkeditorReset(){
			$("textarea[id$='Content']").each(function(){
				ckeditorReset($(this).attr("id"));
			});
			$("textarea[id$='Perf']").each(function(){
				ckeditorReset($(this).attr("id"));
			});
		} */
	
		$(document).ready(function() {
			
			$(".decodeHtml").each(function(){
				var str = $(this).text()+"";
				$(this).html(str.decodeHtml());
			});	
		
			function submit(year,month,userId,dep){
				var params = {};
				params.year = year;
				params.month = month;
				params.userId = userId;
				params.dep = dep;
				window.location.href = "${ctx}/plan/month?"+$.param(params);
			}
			$($(".month").get((${monthDto.month}-1))).parent().addClass("active");
			
			$(".month").click(function(){
				submit($("#year").val(),$(".month").index(this)+1,$("#userId").val());
			});
			
			$("#year").change(function(){
				submit($(this).val(),$(".month").parent().index($(".month").parent(".active"))+1,$("#userId").val());
			});
			
			$("#userId").change(function(){
				submit($("#year").val(),$(".month").parent().index($(".month").parent(".active"))+1,$(this).val());
			});
			
			$("#dep").change(function(){
				submit($("#year").val(),$(".month").parent().index($(".month").parent(".active"))+1,'',$(this).val());
			});
			
			$("#week").click(function(){
				var params = {};
				params.year = '${monthDto.year}';
				params.month = '${monthDto.month}';
				params.userId = '${user.id}';
				window.location.href = "${ctx}/plan/week?"+$.param(params);
			});
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a>月度工作计划</a></li>
		<li><a id="week">日志填写</a></li>
	</ul>
	<span>
		部门：<select style="width: 150px;" id="dep">
				<c:forEach items="${fns:getOfficeList()}" var="office">
					<c:choose>
						<c:when test="${user.office.id eq office.id || office.id eq '2' || cuser.office.id eq office.id}">
							<option value="${office.id}" <c:if test="${user.office.id eq office.id}">
								selected="selected"
							</c:if> >${office.name}</option>
						</c:when>
						<c:otherwise>
							<shiro:hasPermission name="sys:plan:view"><option value="${office.id}">${office.name}</option></shiro:hasPermission>
							<c:if test="${office.id eq '8'}">
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
	</span>	
	<span>
		员工：<select id="userId" style="width: 150px;" >
			<c:forEach items="${user.office.userList}" var="offUser">
				<option value="${offUser.id}" <c:if test="${user.id eq offUser.id}">
				selected="selected"
			</c:if> >${offUser.name}</option>
			</c:forEach>
		</select>
	</span>
	<c:set scope="page" var="currentYear" value="${fns:getYear()}" />　
	选择年份：<select id="year" style="width: 100px;">
		<option value="${currentYear+1}" <c:if test="${currentYear== monthDto.year-1}" >selected</c:if>   >${currentYear+1}年</option>
		<option value="${currentYear}" <c:if test="${currentYear== monthDto.year}" >selected</c:if>   >${currentYear}年</option>
		<option value="${currentYear - 1}" <c:if test="${currentYear== monthDto.year+1}">selected</c:if> >${currentYear - 1}年</option>
		<option value="${currentYear - 2}" <c:if test="${currentYear== monthDto.year+2}">selected</c:if> >${currentYear - 2}年</option>
		<option value="${currentYear - 3}" <c:if test="${currentYear== monthDto.year+3}">selected</c:if> >${currentYear - 3}年</option>
	</select>
	<div class="pagination">
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
	</div>
	<tags:message content="${message}"/>
	<table  class="table table-striped table-bordered table-condensed">
		<thead><tr><th>部门${monthDto.month}月计划</th></tr></thead>
		<tbody>
			<tr><td>
				 <pre class="decodeHtml depcPre">${plans.depPlan.content}</pre>
				 <shiro:hasPermission name="sys:plan:edit">
				 <c:set value="${monthDto.year}/${monthDto.month}/${user.office.id}" scope="page" var="depflag" />
				 	<c:if test="${fns:hasAccess('','dep',depflag)}">
				 		 <form:form action="${ctx}/plan/month/saveDepPlan">
				 				<input type="hidden" name="flag"  value="${monthDto.year}/${monthDto.month}/${user.office.id}"/>
					 			<input type="hidden" name="type"  value="3"/>
					 			<input type="hidden" name="id"  value="${plans.depPlan.id}"/>
					 			<input type="hidden" name="createBy.id"  value="${user.id}"/>
				 				<div class="controls">
									<textarea  id="depc" name="content" style="display: none;width: 98%;height: 260px">${plans.depPlan.content}</textarea>
								</div>
								<div style="float: right;">
					 				<input id="btnUpdate" class="btn btn-primary" type="button" value="填写" onclick="showText(this,'depc');"/>&nbsp;
									<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" style="display: none"/>&nbsp;
									<input id="btnCancel" class="btn" type="reset" value="重置" style="display: none" />&nbsp;&nbsp;
								</div>
				 		</form:form>
				 	</c:if>
				 </shiro:hasPermission>
			</td></tr>
		</tbody>
	</table>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th colspan="2">${user.name}工作计划列表</th></tr></thead>
		<tbody>
			<tr><td colspan="2">
			个人${monthDto.month}月工作计划 : 
			<pre class="decodeHtml personCPre">${plans.monthPlan.content}</pre>
			
			<c:set value="${monthDto.year}/${monthDto.month}" scope="page" var="monthflag" />
			<c:if test="${fns:hasAccess(user.id,'month',monthflag)}">
		 		 <form:form action="${ctx}/plan/month/saveMonthPlan">
		 				<div class="controls">
							<textarea id="personC" name="content" style="display: none;width: 98%;height: 260px">${plans.monthPlan.content}</textarea>
							<input type="hidden" name="flag"  value="${monthDto.year}/${monthDto.month}"/>
							<input type="hidden" name="type"  value="2"/>
							<input type="hidden" name="id"  value="${plans.monthPlan.id}"/>
							<input type="hidden" name="createBy.id"  value="${user.id}"/>
						</div>
						<div style="float: right;">
			 				<input id="btnUpdate" class="btn btn-primary" type="button" value="填写" onclick="showText(this,'personC');"/>&nbsp;
							<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" style="display: none"/>&nbsp;
							<input id="btnCancel" class="btn" type="reset" value="重置" style="display: none" />&nbsp;&nbsp;
						</div>
		 		</form:form>
		 	</c:if>
			</td></tr>
			<form:form action="${ctx}/plan/month/saveWeekPlan" htmlEscape="false">
			<c:set scope="page" value="false" var="flag"/>
			<c:forEach items="${monthDto.weeks}" var="week" varStatus="index">
			<tr>
				<c:set scope="page" value="w${index.count}" var="i"></c:set>
				<c:set value="${monthDto.year}/${monthDto.month}/${index.count}" scope="page" var="weekflag" />
				<c:set scope="page" value="${fns:hasAccess(user.id,'week',weekflag)}" var="f1"></c:set>
				<c:set scope="page" value="${fns:hasAccess(user.id,'weekFinish',weekflag)}" var="f2"></c:set>
				<td style="width:40%">第${index.count}周计划　<span style="font-weight:900">${week}</span>:
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<c:if test="${plans[i] != null}">于<fmt:formatDate value="${plans[i].updateDate}" type="both" />编辑</c:if>
				<pre class="decodeHtml ${i}ContentPre">${plans[i].content}</pre>
					<c:if test="${f1}">
						<c:set scope="page" value="true" var="flag"/>
						<textarea id="${i}Content"  name="plansMap['${i}'].content" style="display: none;width: 98%;height: 260px">${plans[i].content}</textarea>
					</c:if>
				</td>
				<td style="width:40%">完成情况&nbsp;&nbsp;&nbsp;&nbsp;<c:if test="${plans[i] != null}">于<fmt:formatDate value="${plans[i].updateDate}" type="both"/>编辑</c:if>:<pre class="decodeHtml ${i}PerfPre">${plans[i].performance}</pre>
					<c:if test="${f2}">
						<c:set scope="page" value="true" var="flag"/>
						<textarea id="${i}Perf" style="display: none;width: 98%;height: 260px" name="plansMap['${i}'].performance">${plans[i].performance}</textarea>
					</c:if>
					<c:if test="${f1 || f2 }">
						<input type="hidden" name="plansMap['${i}'].flag"  value="${weekflag}"/>
						<input type="hidden" name="plansMap['${i}'].type"  value="1"/>
						<input type="hidden" name="plansMap['${i}'].id"  value="${plans[i].id}"/>
						<input type="hidden" name="plansMap['${i}'].createBy.id"  value="${user.id}"/>
					</c:if>
					<c:choose>
					<c:when test="${f2 == true && f1==false}">
						<input type="hidden" name="plansMap['${i}'].content" value="${plans[i].content}"/>
					</c:when>
					<c:when test="${f1 == true && f2==false}">
						<input type="hidden"  name="plansMap['${i}'].performance" value="${plans[i].performance}"/>
					</c:when>
				</c:choose>
				</td>
			</tr>
			</c:forEach>
			<c:if test="${flag}">
				<tr height="50px"><td colspan="2" style="line-height: 50px">
					<div style="float: right;">
						<input id="btnUpdate" class="btn btn-primary" type="button" value="填写" onclick="batchShowText(this);"/>&nbsp;
						<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" style="display: none"/>&nbsp;
						<input id="btnCancel" class="btn" type="reset" value="重置" style="display: none" />&nbsp;&nbsp;
					</div>
				</td></tr>
			</c:if>
			</form:form>	
		</tbody>
	</table>
	
</body>
</html>
