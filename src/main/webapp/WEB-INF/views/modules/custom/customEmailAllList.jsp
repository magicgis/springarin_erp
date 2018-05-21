<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Custom Email View</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
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
	
		$(document).ready(function() {
			
			$("#s${customEmail.state}").css("color","black");
			
			$("#update").click(function(){
				$("#update").prop("disabled","true");
				$.get("${ctx}/custom/emailManager/update",{},function(data){
					if(data =='1'){
						top.$.jBox.tip("更新轮训队列成功！","success",{persistent:false,opacity:0});
					}else
						top.$.jBox.tip("更新轮训队列失败！","error",{persistent:false,opacity:0});
					$("#update").removeAttr("disabled");
				});
			});
			
			$("#update2").click(function(){
				$("#update2").prop("disabled","true");
				$.get("${ctx}/custom/emailManager/update2",{},function(data){
					if(data =='1'){
						top.$.jBox.tip("重新分发成功！","success",{persistent:false,opacity:0});
					}else
						top.$.jBox.tip("重新分发失败！","error",{persistent:false,opacity:0});
					$("#update2").removeAttr("disabled");
				});
			});
			
			$("#selectM").change(function(){
				$("#searchForm").submit();
			});
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
			
			$("a[rel='popover']").popover({trigger:'hover'});
		});
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		function select(state){
			$("#stateSel").prop("value",state);
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/custom/emailManager/all"><spring:message code='custom_email_list'/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="customEmail" action="${ctx}/custom/emailManager/all" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="stateSel" name="state" type="hidden" value="${customEmail.state}"/>
		<div style="height: 80px;line-height: 40px">
			<a id="s" onclick="select('')">All</a>&nbsp;
			<a id="s0" onclick="select('0')"><spring:message code='custom_email_state1'/></a>&nbsp;
			<a id="s1" onclick="select('1')"><spring:message code='custom_email_state2'/></a>&nbsp;
			<a id="s2" onclick="select('2')"><spring:message code='custom_email_state3'/></a>&nbsp;
			<a id="s4" onclick="select('4')"><spring:message code='custom_email_state4'/></a>&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;
			<spring:message code='custom_email_form4'/>：
			<select name="masterBy.id" style="width: 120px" id="selectM">
				<option value="">All</option>
				<c:forEach items="${all}" var="user">
					<option value="${user.id}" ${customEmail.masterBy.id eq user.id?'selected':''} >${user.name}</option>
				</c:forEach>
			</select>
			&nbsp;&nbsp;
			<c:if test="${'1' eq dateLimit }">
			<spring:message code='custom_email_form9'/>:
				<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/custom/emailManager/all');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="customSendDate" value="<fmt:formatDate value="${customEmail.customSendDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="customSendDate"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/custom/emailManager/all');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${customEmail.endDate}" pattern="yyyy-MM-dd" />" id="endDate" class="input-small"/>
				&nbsp;&nbsp;</c:if>
			<div >
			<%--&nbsp;<label><spring:message code='custom_email_search'/>:</label><form:input path="subject" htmlEscape="false" maxlength="50" class="input-small"/> --%>
			<label><spring:message code="custom_email_form1"/>:</label><form:input path="revertEmail" htmlEscape="false" maxlength="100" class="input-small"/>
			<label>CustomerId:</label><form:input path="customId" htmlEscape="false" maxlength="30" class="input-small"/>
			<label><spring:message code="custom_email_form3" />:</label><form:input path="subject" htmlEscape="false" maxlength="100" class="input-small"/>
			&nbsp;
			<input class="btn btn-primary" type="submit" value="<spring:message code='sys_but_search'/>"/>
			<shiro:hasPermission name="custom:email:edit">
				<input id="update"  class="btn btn-primary" type="button" value="立即更新轮训队列"/>
				<input id="update2"  class="btn btn-primary" type="button" value="重新分发离职人员邮件"/>
			</shiro:hasPermission>
			</div>
		</div>
	</form:form>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th><div class="ico_mailtitle">&nbsp;</div></th>
		<th style="width: 160px"><spring:message code='custom_email_form1'/></th>
		<th style="width: 100px" class="sort revertServerEmail">Account</th>
		<th style="width:190px"><spring:message code='custom_email_form3'/></th>
		<th style="width: 60px"><spring:message code='custom_email_form4'/></th>
		<th style="width: 50px" class="sort state"><spring:message code='custom_email_form10'/></th>
		<th style="width: 110px" class="sort customSendDate"><spring:message code='custom_email_form9'/></th>
		<th style="width: 110px"><spring:message code='custom_email_form11'/></th>
		<th style="width: 110px" class="sort endDate"><spring:message code='custom_email_form12'/></th>
		</tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="email">
			<tr>
				<td class="ci"><div class="ciz${'1' eq email.urgent ?' Zh':''}"></div><div  class="cir ${email.stateCls}" ></div><div class="cij${not empty email.attchmentPath || not empty email.inlineAttchmentPath ?' Ju':''}"></div></td>
				<td>${email.encryptionEmail}</td>
				<td>
				     ${emailMap[email.revertServerEmail]}
				</td>
				<td><a href="${ctx}/custom/emailManager/view?id=${email.id}&all" rel="popover" data-content="${email.subject}" >${empty fn:substring(email.subject,0,30) ?'Empty':fn:substring(email.subject,0,30)}</a></td>
				<td>${email.masterBy.name}</td>
				<td>${email.stateStr}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd H:mm" value="${email.customSendDate}"/></td>
				<td><fmt:formatDate pattern="yyyy-MM-dd H:mm" value="${email.answerDate}"/></td>
				<td><fmt:formatDate pattern="yyyy-MM-dd H:mm" value="${email.endDate}"/></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
