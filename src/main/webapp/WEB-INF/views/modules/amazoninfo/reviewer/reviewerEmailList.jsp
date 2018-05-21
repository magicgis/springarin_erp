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
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
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
			$("#s${reviewerEmail.state}").css("color","black");
			
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
			
			$("#attchmentPathCk").click(function(){
				if(this.checked){
					$("#attchmentPath").val('true');
				}else{
					$("#attchmentPath").val('');
				}
				$("#searchForm").attr("action","${ctx}/amazoninfo/reviewerEmail");
				$("#searchForm").submit();
			});
			
			<shiro:hasPermission name="reviewer:email:proxy">
				$("select[name='masterBy.id']").change(function(){
					$("#searchForm").attr("action","${ctx}/amazoninfo/reviewerEmail");
					$("#searchForm").submit();
				});
			</shiro:hasPermission>
			$("#transmitOther").click(function(){
				if($(".checked :hidden").size()){
					var userId = $("#transmitSelOther").val();
					var userName = $("#transmitSelOther option[value='"+userId+"']").text();
					top.$.jBox.confirm('Forwarding To '+userName+'?','Prompted',function(v,h,f){
						if(v=='ok'){
							loading('Please wait a moment!');
							var params = {};
							params.eid = [];
							$(".checked :hidden").each(function(){
								params.eid[params.eid.length] = $(this).val();
							}); 
							params.userId = userId;
							window.location.href = "${ctx}/amazoninfo/reviewerEmail/batchtransmitOther?"+$.param(params);
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}else{
					top.$.jBox.tip("No one yet finished processing the message, not forward!","error",{persistent:false,opacity:0});
				}
			});
			
			$("#noPly").click(function(){
				if($(".checked :hidden").size()){
					top.$.jBox.confirm('To determine the client does not handle email it?','Prompted',function(v,h,f){
						if(v=='ok'){
							loading('Please wait a moment!');
							var params = {};
							params.eid = [];
							$(".checked :hidden").each(function(){
								params.eid[params.eid.length] = $(this).val();
							}); 
							window.location.href = "${ctx}/amazoninfo/reviewerEmail/batchNoply?"+$.param(params);
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}else{
					top.$.jBox.tip("No one yet finished processing the mail, you can not change the state!","error",{persistent:false,opacity:0});
				}
			});
			
			$("#open").click(function(){
				if($(".checked").size()){
					$(".checked").each(function(){
						window.open($(this).parent().parent().parent().find(".open").attr("href"));
					}); 
				}else{
					top.$.jBox.tip("Not checked the mail!","error",{persistent:false,opacity:0});
				}
			});
			
			$("#flag").change(function(){
				$("#searchForm").attr("action","${ctx}/amazoninfo/reviewerEmail");
				$("#searchForm").submit();
			});
			
			$("#country").change(function(){
				$("#searchForm").submit();
			});
			
			$("#aboutMeCheck").change(function(){
				if(this.checked){
					$("#aboutMe").val("1");
				} else {
					$("#aboutMe").val("0");
				}
				$("#searchForm").submit();
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
		<li class="active"><a href="${ctx}/amazoninfo/reviewerEmail/"><spring:message code='custom_email_list'/></a></li>
		<li><a href="${ctx}/custom/autoReply/form?type=3"><spring:message code='custom_email_autoReply'/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="reviewerEmail" action="${ctx}/amazoninfo/reviewerEmail" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="stateSel" name="state" type="hidden" value="${reviewerEmail.state}"/>
		<input id="aboutMe" name="aboutMe" type="hidden" value="${aboutMe}"/>
		<div style="height:80px;line-height: 40px">
			
			<a id="s" onclick="select('')">All</a>&nbsp;
			<a id="s5" onclick="select('5')">未处理</a>&nbsp;
			<a id="s0" onclick="select('0')"><spring:message code="custom_email_state1"/></a>&nbsp;
			<a id="s1" onclick="select('1')"><spring:message code="custom_email_state2"/></a>&nbsp;
			<a id="s2" onclick="select('2')"><spring:message code="custom_email_state3"/></a>&nbsp;
			<a id="s4" onclick="select('4')"><spring:message code="custom_email_state4"/></a>&nbsp;
			
			<!-- 不处理 -->
			<input id="noPly" class="btn btn-primary" type="button" value="<spring:message code="custom_email_btn1"/>"/>&nbsp;&nbsp;
			<!-- 打开邮件 -->
			<input id="open" class="btn btn-primary" type="button" value="<spring:message code="custom_email_btn2"/>"/>&nbsp;
			<!-- 客户发件时间 -->
			<spring:message code='custom_email_form9'/>:
				<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/amazoninfo/reviewerEmail/');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="customSendDate" value="<fmt:formatDate value="${reviewerEmail.customSendDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="customSendDate"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/amazoninfo/reviewerEmail/');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${reviewerEmail.endDate}" pattern="yyyy-MM-dd" />" id="endDate" class="input-small"/>
			
			<input type="checkbox" id="attchmentPathCk" ${not empty reviewerEmail.attchmentPath?'checked':''}/>attachment
			<input type="hidden"name="attchmentPath" id="attchmentPath" value="${reviewerEmail.attchmentPath}">
				&nbsp;
			<input type="checkbox" name="aboutMeCheck" id="aboutMeCheck" value="${aboutMe }" ${aboutMe eq '1'?'checked':''}/><spring:message code='custom_email_btn3'/>
			<div >
			<c:if test="${fn:length(otherMaster)>0}">
				<span style="margin-top: 3px">
				<select id="transmitSelOther" style="width: 100px">
					<c:forEach items="${otherMaster}" var="user">
						<option value="${user.id}">${user.name}</option>
					</c:forEach>
				</select>&nbsp;
				<input id="transmitOther" class="btn btn-primary" type="button" value="<spring:message code="custom_email_btn4"/>"/>&nbsp;
				</span>
			</c:if>
			<span style="margin-top: 3px">
			<label><spring:message code="sys_label_country"/>:</label>
			<select id="country" name="country" style="width: 140px">
				<option value="">---All---</option>
				<c:forEach items="${fns:getDictList('platform')}" var="dic">
					<c:if test="${dic.value ne 'com.unitek' && dic.value ne 'mx'}">
						<option value="${dic.value}" ${dic.value eq reviewerEmail.country?'selected':'' }>${dic.label}</option>
					</c:if>
				</c:forEach>
			</select>&nbsp;
			</span>
			
			<label><spring:message code="custom_email_search"/>:</label><form:input path="subject" htmlEscape="false" maxlength="50" class="input-small"/>
			&nbsp;
			<span style="margin-top: 3px">
			<label>Remark:</label><input type="text" name="remarks" value="${reviewerEmail.remarks}" style="width:80px"/>
			</span>
			<input class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
		<th style="width: 20px">
			<span>
				<input type="checkbox">
				</span>
		</th>
		<th><div class="ico_mailtitle">&nbsp;</div></th>
		<!-- 发件人 -->
		<th style="width: 160px"><spring:message code="custom_email_form1" /></th>
		<!-- 发件人所属国家 -->
		<th style="width: 100px"><spring:message code='sys_label_country'/></th>
		<!-- 来至服务器邮箱 -->
		<th style="width: 160px"><spring:message code="custom_email_form2" /></th>
		<!-- 主题 -->
		<th style="width: 190px"><spring:message code="custom_email_form3" /></th>
		<!-- 负责人 -->
		<th style="width: 60px"><spring:message code="custom_email_form4" /></th>
		<!-- 邮件状态 -->
		<th style="width: 50px" class="sort state"><spring:message code="custom_email_form6" /></th>
		<!-- 客户发件时间 -->
		<th style="width: 100px"><spring:message code="custom_email_form9" /></th>
		<th style="width: 100px">Remark</th>
		<!-- 操作 -->
		<th ><spring:message code="sys_label_tips_operate"/></th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="email">
			<tr>
				<td>
					<div class="checker">
					<span><input type="checkbox"/>
						<c:if test="${email.state eq '0' || email.state eq '1'}">	
						  <input type="hidden" value="${email.id}" class="emailId"/>
						</c:if>  
					</span>
					</div>
				</td>
				<td class="ci"><div class="ciz${'1' eq email.urgent ?' Zh':''}"></div><div  class="cir ${email.stateCls}" ></div><div class="cij${not empty email.attchmentPath || not empty email.inlineAttchmentPath ?' Ju':''}"></div></td>
				<td>${email.revertEmail}</td>
				<td>${fns:getDictLabel(email.formReviewer.country,'platform','')}</td>
				<td>${email.revertServerEmail}</td>
				<td><a class="open" href="${ctx}/amazoninfo/reviewerEmail/${email.state eq '0' || email.state eq '1'?'form':'view'}?id=${email.id}" class="${email.shake?'shake':'' }"  rel="popover" data-content="${email.subject}">${empty fn:substring(email.subject,0,30)?'Empty':fn:substring(email.subject,0,30)}</a></td>
				<td>${email.masterBy.name}</td>
				<td>${email.stateStr}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd H:mm" value="${email.customSendDate}"/></td>
				<td>${email.remarks}</td>
				<td>
					<c:choose>
						<c:when test="${email.state eq '0' || email.state eq '1' }">
							<a href="${ctx}/amazoninfo/reviewerEmail/form?id=${email.id}"><spring:message code="custom_email_btn5" /></a>&nbsp;&nbsp;
						</c:when>
						<c:when test="${email.state eq '2'}">
							<a href="${ctx}/amazoninfo/reviewerEmail/recall?id=${email.id}"><spring:message code="custom_email_btn6" /></a>&nbsp;&nbsp;
							<a href="${ctx}/amazoninfo/reviewerEmail/view?id=${email.id}"><spring:message code="sys_but_view" /></a>&nbsp;&nbsp;
						</c:when>
						<c:when test="${email.state eq '4'}">
							<%--<a href="${ctx}/amazoninfo/reviewerEmail/process?id=${email.id}"><spring:message code="custom_email_btn7" /></a>&nbsp;&nbsp; --%>
							<a href="${ctx}/amazoninfo/reviewerEmail/view?id=${email.id}"><spring:message code="sys_but_view" /></a>&nbsp;&nbsp;
						</c:when>
					</c:choose>
					<shiro:hasPermission name="reviewer:email:edit"><a href="${ctx}/amazoninfo/reviewerEmail/delete?id=${email.id}" onclick="return confirmx('Confirm you want to delete the email?', this.href)">Delete</a></shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
