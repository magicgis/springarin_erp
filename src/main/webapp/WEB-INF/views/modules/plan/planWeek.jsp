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
	<%-- <script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script> --%>
	<script type="text/javascript">
		
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
	
		function batchShowText(button){
			$("textarea[id$='log']").each(function(){
				$(this).show();
				showText(button,$(this).attr("id"));
			});
		}
		
		function showText(button,id){
			//渲染ckeditor出来
			//var ckeditor = CKEDITOR.replace(id);
			/* $.each(configs,function(i,v){
				ckeditor.config[i] = v;
			}); */
			//隐藏按钮
			$(button).parent().children().show();
			$(button).hide();
			//隐藏pre
			$("."+id+"Pre").hide();
		}
		
		/* function ckeditorReset(id){
			CKEDITOR.instances[id].setData($("textarea #"+id).val());
		} 
		
		function batchCkeditorReset(){
			$("textarea[id$='log']").each(function(){
				ckeditorReset($(this).attr("id"));
			});
		}*/
	
		$(document).ready(function() {
			
			$(".decodeHtml").each(function(){
				var str = $(this).text()+"";
				$(this).html(str.decodeHtml());
			});	
			
			$("#week").change(function(data){
				var params = {};
				params.year = '${monthDto.year}';
				params.month = '${monthDto.month}';
				params.userId = '${user.id}';
				params.week =$("#week").val();
				window.location.href = "${ctx}/plan/week?"+$.param(params);
			});
			
			$("#month").click(function(){
				var params = {};
				params.year = '${monthDto.year}';
				params.month = '${monthDto.month}';
				params.userId = '${user.id}';
				window.location.href = "${ctx}/plan/month?"+$.param(params);
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a id="month">月度工作计划</a></li>
		<li class="active"><a>日志填写</a></li>
	</ul>
	选择周：<select id="week" style="width: 100px">
		<option value="1" <c:if test="${currentWeek == 1}" >selected</c:if>>第一周</option>
		<option value="2" <c:if test="${currentWeek == 2}" >selected</c:if>>第二周</option>
		<option value="3" <c:if test="${currentWeek == 3}" >selected</c:if>>第三周</option>
		<option value="4" <c:if test="${currentWeek == 4}" >selected</c:if>>第四周</option>
		<c:if test="${fn:length(monthDto.weeks) == 5}">
			<option value="5" <c:if test="${currentWeek== 5}" >selected</c:if>>第五周</option>
		</c:if>
	</select>
	<br/><br/>
	<table  style="width: 99%" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>${user.name}第${currentWeek}周计划</th></tr></thead>
		<tbody>
			<tr><td> <pre class="decodeHtml">${plans.weekPlan.content}</pre></td></tr>
		</tbody>
	</table>
	<tags:message content="${message}"/>
	<c:set scope="page" var="currentMonth" value="${monthDto.year}/${monthDto.month<10?'0':''}${monthDto.month}" />
	<table  class="table table-striped table-bordered table-condensed">
		<form:form action="${ctx}/plan/week/save" >
		
		<input type="hidden" value="${currentWeek}" name="week"/>
		
		<thead><tr><th colspan="2">${user.name}个人日志(当天9:00~24:00可以填写)</th></tr></thead>
		<tbody>
			<c:set scope="page" var="flag" value="false"/>
			<c:forEach items="${currentWeekDto.dayData}" var="week" varStatus="index">
				${index.count%2!=0?'<tr>':''}
				<c:set scope="page" var="dateStr" value="${fns:getDateByPattern(week,'yyyy/MM/dd')}"/>
				<td style="width:40%">星期${index.count}&nbsp;&nbsp;<span style="font-weight:900">(${dateStr})</span>
				&nbsp;&nbsp;&nbsp;&nbsp;<c:if test="${plans[dateStr]!=null}">于<fmt:formatDate value="${plans[dateStr].updateDate}" type="both"/>编辑</c:if>:
				<pre class="decodeHtml ${index.count}logPre">${plans[dateStr].content}</pre>
				<c:if test="${fns:hasAccess(user.id,'day',dateStr)}">
					<c:set scope="page" var="flag" value="true"/>
					<textarea id="${index.count}log" name="plansMap['${index.count}'].content" style="display: none;width: 98%;height: 260px">${plans[dateStr].content}</textarea>
					<input type="hidden" name="plansMap['${index.count}'].flag"  value="${dateStr}"/>
					<input type="hidden" name="plansMap['${index.count}'].type"  value="0"/>
					<input type="hidden" name="plansMap['${index.count}'].id"  value="${plans[dateStr].id}"/>
					<input type="hidden" name="plansMap['${index.count}'].createBy.id"  value="${user.id}"/>
				</c:if>
				</td>
				${index.count%2!=0?'':'</tr>'}
			</c:forEach>
			${fn:length(currentWeekDto.dayData)%2==0?'':'<td></td></tr>'}
			<c:if test="${flag}">
				<tr height="50px">
					<td colspan="2" style="line-height: 50px">
						<div style="float: right;">
							<input id="btnUpdate" class="btn btn-primary" type="button" value="填写" onclick="batchShowText(this);"/>&nbsp;
							<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存" style="display: none"/>&nbsp;
							<input id="btnCancel" class="btn" type="reset" value="重置" style="display: none" />&nbsp;&nbsp;
						</div>
					</td>
				</tr>
			</c:if>
		</tbody>
	  </form:form>		
	</table>
</body>
</html>
