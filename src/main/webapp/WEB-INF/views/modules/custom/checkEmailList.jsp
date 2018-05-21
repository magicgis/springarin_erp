<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Sent Email Manager</title>
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
			
			$("#checkState").change(function(){
				$("#searchForm").submit();
			});
			
			  $("a[rel='popover']").popover({trigger:'hover'});
			  

				$("#btnExport").click(function(){
					top.$.jBox.confirm("Export？","<spring:message code='sys_label_tips_msg'/>",function(v,h,f){
						if(v=="ok"){
							$("#searchForm").attr("action","${ctx}/custom/sendEmail/exportCheck");
							$("#searchForm").submit();
							$("#searchForm").attr("action","${ctx}/custom/sendEmail/checkList");
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
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
	    <li class="active"><a href="${ctx}/custom/sendEmail/checkList">Mail Audit List</a></li>
		<li ><a href="${ctx}/custom/sendEmail"><spring:message code="custom_email_sentList" /></a></li>
		<li><a href="${ctx}/custom/sendEmail/form"><spring:message code="custom_email_sentEmail"/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="sendEmail" action="${ctx}/custom/sendEmail/checkList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 40px;line-height: 40px">
			<span style="float: right;">
			<label>Date:</label>
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/custom/sendEmail/checkList');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="sentDate" value="<fmt:formatDate value="${sendEmail.sentDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="customSendDate"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/custom/sendEmail/checkList');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${sendEmail.endDate}" pattern="yyyy-MM-dd" />" id="endDate" class="input-small"/>
				
			<label>CheckState:</label>
			<select name="checkState"  style="width: 222px;" id='checkState'>
					 <option value="">--请选择--</option>
					 <option value="0" ${'0' eq sendEmail.checkState?'selected':'' }>未审核</option>
					<%--  <option value="1" ${'1' eq sendEmail.checkState?'selected':'' }>审核通过</option> --%>
					 <option value="2" ${'2' eq sendEmail.checkState?'selected':'' }>审核未通过</option>
			</select>
			&nbsp;&nbsp;
			<label><spring:message code="custom_email_search1" />:</label><form:input path="sendSubject" htmlEscape="false" maxlength="50" class="input-small"/>
			&nbsp;
			<input class="btn btn-primary" type="submit" value="<spring:message code='sys_but_search' />"/>
			&nbsp;
			<input id="btnExport" class="btn btn-primary" type="button" value="<spring:message code='sys_but_export'/>"/>
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
		<th style="width: 50px">Event</th>
		<th style="width: 50px">State</th>
		<th style="width: 50px">CheckUser</th>
		<th style="width: 50px">CreateUser</th>
		<th style="width: 120px"><spring:message code="sys_label_tips_operate" /></th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="email">
			<tr>
				<td>${email.encryptionEmail}</td>
				<td><a href="${ctx}/custom/sendEmail/view?id=${email.id}&checkEmail=2" class="${email.shake?'shake':'' }">${email.sendSubject}</a></td>
				<td><fmt:formatDate type="both" value="${email.sentDate}"/></td>
				<td>
					${email.sendFlag eq '0' ?'Unsent':(email.sendFlag eq '2'?'Undelivered message':'Sent')}
				</td>
				<td><c:if test="${not empty email.remark }">
					<a href="${ctx}/custom/event/form?id=${email.remark}">SPR-${email.remark}</a>
				</c:if></td>
				<td>${email.checkState eq '0'?'Not Check':''}
					${email.checkState eq '1'?'Pass':''}
					${email.checkState eq '2'?'Not Pass':''}
					${email.checkState eq '4'?'Not Check':''}
					<c:if test="${'2' eq email.checkState}"><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="${email.reason}"><br/>${fns:rabbr(email.reason,8)}</a></c:if>
					</td>
				<td>${email.checkUser.name}</td>
				<td>${email.createBy.name}</td>
				<td>
					<a href="${ctx}/custom/sendEmail/view?id=${email.id}&checkEmail=2"><spring:message code="sys_but_view" /></a>
					<c:if test="${email.sendFlag eq '0'}">
					   <a href="${ctx}/custom/sendEmail/form?id=${email.id}&checkEmail=2&country=${email.country}"><spring:message code="sys_but_edit" /></a>
					</c:if>
						
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
