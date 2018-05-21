<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Custom Email manager</title>
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
			$("#s${customEmail.state}").css("color","black");
			
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
				$("#searchForm").attr("action","${ctx}/custom/emailManager");
				$("#searchForm").submit();
			});
			
			<shiro:hasPermission name="custom:email:proxy">
				$("select[name='masterBy.id']").change(function(){
					/* if(this.checked){
						$("#aboutMeVal").val('${cuser.id}');
					}else{
						$("#aboutMeVal").val('');
					} */
					$("#searchForm").attr("action","${ctx}/custom/emailManager");
					$("#searchForm").submit();
				});
			</shiro:hasPermission>
			$("#transmitOther").click(function(){
				
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
							window.location.href = "${ctx}/custom/emailManager/batchtransmitOther?"+$.param(params);
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				
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
							window.location.href = "${ctx}/custom/emailManager/batchNoply?"+$.param(params);
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
			
			$(".tax").click(function(){
				var $this = $(this);
				$.ajax({
	      			   type: "POST",
	      			   url: "${ctx}/custom/emailManager/isExistEvent?id="+ $this.parent().find(".attrId").val(),
	      			   async: true,
	      			   success: function(msg){
	      				  if(msg=="1"){
	      					$.jBox.tip('Tax-refund event not found'); 
	    					return;
	      				  }else if(msg=="0"){ 
	      					 window.location.href="${ctx}/custom/emailManager/taxRefund?id="+ $this.parent().find(".attrId").val();
	      				  }
	      			   }
	          		});
			});
			
			$("#flag,#productName,#followState").change(function(){
				$("#searchForm").attr("action","${ctx}/custom/emailManager");
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
		<li class="active"><a href="${ctx}/custom/emailManager/"><spring:message code='custom_email_list'/></a></li>
		<li><a href="${ctx}/custom/autoReply/form?type=1"><spring:message code='custom_email_autoReply'/></a></li>
		<li><a href="${ctx}/custom/suggestion/"><spring:message code='custom_email_customSuggestion'/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="customEmail" action="${ctx}/custom/emailManager" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="stateSel" name="state" type="hidden" value="${customEmail.state}"/>
		<div style="height:160px;line-height: 40px">
			
			<a id="s" onclick="select('')">All</a>&nbsp;
			<a id="s5" onclick="select('5')">未处理</a>&nbsp;
			<a id="s0" onclick="select('0')"><spring:message code="custom_email_state1"/></a>&nbsp;
			<a id="s1" onclick="select('1')"><spring:message code="custom_email_state2"/></a>&nbsp;
			<a id="s2" onclick="select('2')"><spring:message code="custom_email_state3"/></a>&nbsp;
			<a id="s4" onclick="select('4')"><spring:message code="custom_email_state4"/></a>&nbsp;
			<shiro:hasPermission name="custom:email:proxy">
				<b style="font-size: 18px"><spring:message code="custom_email_form4" />[Switching to reply to all Email]:</b>
				<select name="masterBy.id" style="width: 100px">
					<option ${empty customEmail.masterBy.id?'selected':''} value="">All</option>
					<c:forEach items="${otherMaster}" var="user">
						<option ${user.id eq customEmail.masterBy.id?'selected':''} value="${user.id}">${user.name}</option>
					</c:forEach>
				</select>&nbsp;
			</shiro:hasPermission>
			<select id="followState" style="width: 100px" name="followState">
				<option value="">--All--</option>
			    <option value="0" ${'0' eq customEmail.followState?'selected':''} >FollowUP</option>	
		    </select>
			<input type="checkbox" id="attchmentPathCk" ${not empty customEmail.attchmentPath?'checked':''}/>attachment
			<input type="hidden"name="attchmentPath" id="attchmentPath" value="${customEmail.attchmentPath}">
			<br/>
			<c:if test="${'1' eq dateLimit }">
			<spring:message code='custom_email_form9'/>:
				<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/custom/emailManager/');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="customSendDate" value="<fmt:formatDate value="${customEmail.customSendDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="customSendDate"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/custom/emailManager/');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${customEmail.endDate}" pattern="yyyy-MM-dd" />" id="endDate" class="input-small"/>
				</c:if>
			<label><spring:message code="custom_email_form1"/>:</label><form:input path="revertEmail" htmlEscape="false" maxlength="100" class="input-small"/>
			<label>CustomerId:</label><form:input path="customId" htmlEscape="false" maxlength="30" class="input-small"/>
			<label><spring:message code="custom_email_form3" />:</label><form:input path="subject" htmlEscape="false" maxlength="100" class="input-small"/>
			<label>Remark:</label><form:input path="remarks" htmlEscape="false" maxlength="30" class="input-small"/>
			<br/>
			<label>Content:</label><form:input path="receiveContent" htmlEscape="false" maxlength="1000" class="input-xxlarge"/>
			<spring:message code="amazon_sales_product_line"/>:<select id="productName" style="width: 150px" name="productName">
						<option value="">--All--</option>
						<c:forEach items="${groupType}" var="groupType">
							<option value="${groupType.id}" ${fn:trim(groupType.id) eq fn:trim(customEmail.productName)?'selected':''}>${groupType.name}</option>			
						</c:forEach>
		    </select>
		
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
			&nbsp;&nbsp;&nbsp;
			<select id="flag" name="flag" style="width: 168px">
				<option value="">--Select the filter tag--</option>
				<option value="p1" ${'p1' eq customEmail.flag?'selected':''}>P1</option>
				<option value="p2" ${'p2' eq customEmail.flag?'selected':''}>P2</option>
			</select>&nbsp;
			&nbsp;
			<input class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
			<input id="noPly" class="btn btn-primary" type="button" value="<spring:message code="custom_email_btn1"/>"/>&nbsp;&nbsp;
			<input id="open" class="btn btn-primary" type="button" value="<spring:message code="custom_email_btn2"/>"/>&nbsp;
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
		<th style="width: 160px"><spring:message code="custom_email_form1" /></th>
		<th style="width: 60px" class="sort revertServerEmail">Account<%-- <spring:message code="custom_email_form2" /> --%></th>
		<th style="width: 190px"><spring:message code="custom_email_form3" /></th>
		<th style="width: 60px"><spring:message code="custom_email_form4" /></th>
		<th style="width: 60px"><spring:message code="custom_email_form5" /></th>
		<th style="width: 50px" class="sort state"><spring:message code="custom_email_form6" /></th>
		<th style="width: 80px" class="sort followDate">FollowDate</th>
		
		<th style="width: 100px" class="sort customSendDate"><spring:message code="custom_email_form9" /></th>
		<th style="width: 110px" class="sort endDate"><spring:message code='custom_email_form12'/></th>
		<th style="width: 100px">Remark</th>
		<th ><spring:message code="sys_label_tips_operate"/></th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="email">
			<tr>
				<td>
					<div class="checker">
					<span><input type="checkbox"/>
						  <input type="hidden" value="${email.id}" class="emailId"/>
					</span>
					</div>
				</td>
				<td class="ci"><div class="ciz${'1' eq email.urgent ?' Zh':''}"></div><div  class="cir ${email.stateCls}" ></div><div class="cij${not empty email.attchmentPath || not empty email.inlineAttchmentPath ?' Ju':''}"></div></td>
				<td><a target='_blank' href='${ctx}/amazoninfo/customers/viewDetail?amzEmail=${email.revertEmail}'>${email.encryptionEmail}</a>
				  <br/>${email.senderIp}
				</td>
				<td>
				 <%--  <c:set var='emailKey' value="${email.revertServerEmail}_${email.senderCountry}"/>
				   ${not empty emailMap[emailKey]?emailMap[emailKey]:emailMap[email.revertServerEmail]} --%>
				   ${emailMap[email.revertServerEmail]}
				</td>
				<td><a class="open" href="${ctx}/custom/emailManager/${email.state eq '0' || email.state eq '1'?'form':'view'}?id=${email.id}${not empty email.customId?'&checkEmail=2':''}" class="${email.shake?'shake':'' }"  rel="popover" data-content="${email.subject}">${empty fn:substring(email.subject,0,30)?'Empty':fn:substring(email.subject,0,30)}</a></td>
				<td>${email.masterBy.name}</td>
				<td><c:if test="${not empty email.customId}">
					<a href="${ctx}/custom/event/form?id=${email.customId}">SPR-${email.customId}</a>
				</c:if></td>
				<td>${email.stateStr}${email.checkState}</td>
				<%-- <td><c:choose><c:when test="${email.remarks eq '1'}">Yes</c:when><c:when test="${email.remarks eq '2'}">System Reply</c:when><c:otherwise>No</c:otherwise></c:choose></td>
				 --%>
				 <td><fmt:formatDate pattern="yyyy-MM-dd" value="${email.followDate}"/></td>
				<td><fmt:formatDate pattern="yyyy-MM-dd H:mm" value="${email.customSendDate}"/></td>
				<td><fmt:formatDate pattern="yyyy-MM-dd H:mm" value="${email.endDate}"/></td>
				<td><c:choose><c:when test="${email.remarks eq '1'}"><spring:message code="custom_email_form7" />:Yes<br/></c:when><c:when test="${email.remarks eq '2'}"><spring:message code="custom_email_form7" />:System Reply<br/></c:when></c:choose>${email.remarks}</td>
				<td>
					<c:choose>
						<c:when test="${email.state eq '0' || email.state eq '1' }">
						    <a href="${ctx}/custom/emailManager/form?id=${email.id}${(not empty email.customId&&fn:contains(email.revertEmail,'.amazon.'))?'&checkEmail=2':''}"><spring:message code="custom_email_btn5" /></a>&nbsp;&nbsp;
						</c:when>
						<c:when test="${email.state eq '2'}">
							<a href="${ctx}/custom/emailManager/recall?id=${email.id}${(not empty email.customId&&fn:contains(email.revertEmail,'.amazon.'))?'&checkEmail=2':''}"><spring:message code="custom_email_btn6" /></a>&nbsp;&nbsp;
							<a href="${ctx}/custom/emailManager/view?id=${email.id}"><spring:message code="sys_but_view" /></a>&nbsp;&nbsp;
						</c:when>
						<c:when test="${email.state eq '4'}">
							<a href="${ctx}/custom/emailManager/process?id=${email.id}${(not empty email.customId&&fn:contains(email.revertEmail,'.amazon.'))?'&checkEmail=2':''}"><spring:message code="custom_email_btn7" /></a>&nbsp;&nbsp;
							<a href="${ctx}/custom/emailManager/view?id=${email.id}"><spring:message code="sys_but_view" /></a>&nbsp;&nbsp;
						</c:when>
					</c:choose>
					<shiro:hasPermission name="custom:email:edit"><a href="${ctx}/custom/emailManager/delete?id=${email.id}" onclick="return confirmx('Confirm you want to delete the email?', this.href)">Delete</a>&nbsp;&nbsp;</shiro:hasPermission>
				   <%--  <input type='hidden' class='attrId' value='${email.id}'/>
				    <a class='tax'>Create Tax_Refund</a>&nbsp;&nbsp; --%>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
