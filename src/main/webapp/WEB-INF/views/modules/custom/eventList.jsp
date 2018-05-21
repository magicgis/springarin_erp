<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Event List</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
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
			if(!(top)){
				top = self;
			}
			
			$("#s${event.state}").css("color","black");
			
			$(".asin").click(function(){
				$("#remarks").prop("value",$(this).parent().find("input").val());
				$("#searchForm").attr("action","${ctx}/custom/event");
				$("#searchForm").submit();
			});
			
			<shiro:hasPermission name="custom:event:all">
				$("#aboutMe").click(function(){
					if(this.checked){
						$("#aboutMeVal").val('${cuser.id}');
					}else{
						$("#aboutMeVal").val('');
					}
					$("#searchForm").attr("action","${ctx}/custom/event");
					$("#searchForm").submit();
				});
			</shiro:hasPermission>
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
					sort = (sort[1]&&sort[1].toUpperCase()=="ASC"?"DESC":"ASC");
					$("#orderBy").val(order+" ASC"!=order+" "+sort?"":order+" "+sort);
				}else{
					$("#orderBy").val(order+" DESC");
				}
				page();
			});
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			$("#selectM,#isEvil").change(function(){
				$("#searchForm").attr("action","${ctx}/custom/event");
				$("#searchForm").submit();
			});
			
			$("#selectT,#country,#me,#productAttribute,#productLine").change(function(){
				$("#searchForm").attr("action","${ctx}/custom/event");
				$("#searchForm").submit();
			});
			
			$("#reback").click(function(){
				$("#remarks").prop("value","");
				$("#searchForm").attr("action","${ctx}/custom/event");
				$("#searchForm").submit();
			});
			
			
			$("#btnExport").click(function(){
				top.$.jBox.confirm("<spring:message code='custom_event_note17'/>","<spring:message code='sys_label_tips_msg'/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/custom/event/export");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/custom/event");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$("#timeoutEvent").click(function(){
				top.$.jBox.confirm("<spring:message code='custom_event_note17'/>","<spring:message code='sys_label_tips_msg'/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/custom/event/timeOutExport");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/custom/event");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			$(".btnTaxExp").click(function(){
                var temp="";
				<c:forEach items="${fns:getDictList('platform')}" var="dic">
					temp+="<option value='${dic.value}' ${event.country eq dic.value ?'selected':''}  >${dic.label}</option>";
				</c:forEach>
				
				var countryHtml="Country:<select name='country' id='country' style='width: 120px'>"+
				" <option><spring:message code='custom_event_all'/></option>"+temp+"</select> ";
				
				top.$.jBox.confirm(countryHtml+"<br/><br/>Month  :<input style='width: 160px'  readonly='readonly'  class='Wdate' type='text' onclick=WdatePicker({dateFmt:'yyyy-MM'}); />", "Select Export Month", function(v, h, f){
					  if (v == 'ok'){
						  	var params = {};
						  	params.month = h.find("input").val();
						  	params.country = h.find("#country").val();
						  	if(params.month){
						  		window.location.href = "${ctx}/custom/event/exportTaxPdfs?"+$.param(params);
								top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:250000});
						  	}else{
						  		return false;
						  	}
					  }
					  return true; //close
				});
			});
	
		
			
			var html = "<div class='input-append btn-group btn-input'><input id='reason' name='reason'  style='height: 25px;width:200px' class='span2' id='t' size='16'/><a class='btn btn-default dropdown-toggle' data-toggle='dropdown'><span class='caret'></span></a><ul class='dropdown-menu'><li><a href='#' onclick=\"$('#t').prop('value',$(this).text())\"></a></li></ul></div>";
			$("#bathTransmitOther").click(function(){
				if($(".checked :hidden").size()){
					var userId = $("#transmitSelOther").val();
					var userName = $("#transmitSelOther option[value='"+userId+"']").text();
					top.$.jBox.confirm(html,'Forwarding To '+userName+'?',function(v,h,f){
						if(v=='ok'){
							loading("<spring:message code='custom_event_note10'/>");
							var params = {};
							params.eid = [];
							$(".checked :hidden").each(function(){
								params.eid[params.eid.length] = $(this).val();
							}); 
							params.userId = userId;
							params.tReason = encodeURI(h.find("#reason").val());
							window.location.href = "${ctx}/custom/event/batchTransmitOther?"+$.param(params);
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}else{
					top.$.jBox.tip("No one yet finished processing the message, not forward!","error",{persistent:false,opacity:0});
				}
			});
			
		});
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").attr("action","${ctx}/custom/event");
			$("#searchForm").submit();
        	return false;
        }
		function select(state){
			$("#stateSel").prop("value",state);
			$("#searchForm").attr("action","${ctx}/custom/event");
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/custom/event/"><spring:message code='custom_event_list'/></a></li>
		<li><a href="${ctx}/custom/event/editView"><spring:message code='sys_but_add'/><spring:message code='custom_event_event'/></a></li>
		<li><a href="${ctx}/custom/autoReply/form?type=2"><spring:message code='custom_event_autoReply'/></a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="event" action="${ctx}/custom/event" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="stateSel" name="state" type="hidden" value="${event.state}"/>
		<input id="remarks" name="remarks" type="hidden" value="${event.remarks}"/>
		<div style="height: 60px;line-height: 60px">
			<div>
				<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/custom/event');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${event.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/custom/event');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${event.endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				<spring:message code='sys_label_country'/>：<select name="country" id="country" style="width: 120px">
						<option value="" ${event.country eq ''?'selected':''}><spring:message code='custom_event_all'/></option>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<option value="${dic.value}" ${event.country eq dic.value ?'selected':''}  >${dic.label}</option>
						</c:forEach>
						<option value="other" ${event.country eq 'other'?'selected':''}><spring:message code='custom_event_other'/></option>	
				</select>&nbsp;&nbsp;
					<spring:message code='custom_event_form4'/>：<select name="type" style="width: 160px" id="selectT">
						<option value=""><spring:message code='custom_event_all'/></option>
						<option value="1" ${event.type eq '1'?'selected':''}>Rating</option>
						<option value="2" ${event.type eq '2'?'selected':''}>Account Rating</option>
						<option value="-1" ${event.type eq '-1'?'selected':''}>Negative</option>
						<option value="4" ${event.type eq '4'?'selected':''}>Tax_Refund</option> 
						<option value="5" ${event.type eq '5'?'selected':''}>Support</option>
						<option value="7" ${event.type eq '7'?'selected':''}>Support_Voucher</option>
						<option value="8" ${event.type eq '8'?'selected':''}>Marketing Order</option>
						<option value="6" ${event.type eq '6'?'selected':''}>FAQ</option>
						<option value="3" ${event.type eq '3'?'selected':''}>FAQ_Email</option>
						<option value="9" ${event.type eq '9'?'selected':''}>Product Improvement</option>
						<option value="10" ${event.type eq '10'?'selected':''}>Product Recall</option>
						<option value="11" ${event.type eq '11'?'selected':''}>Review Refund</option>
						<option value="12" ${event.type eq '12'?'selected':''}>Ebay Order</option>
						<option value="13" ${event.type eq '13'?'selected':''}>Website SupportOrder</option>
						<option value="14" ${event.type eq '14'?'selected':''}>Offline SupportOrder</option>
						<option value="15" ${event.type eq '15'?'selected':''}>MFN Order</option>
					</select>&nbsp;&nbsp;
				<shiro:hasPermission name="custom:event:all">
					<spring:message code='custom_event_colName'/>：<select name="createBy.id" style="width: 120px" id="selectM">
						<option value=""><spring:message code='custom_event_all'/></option>
						<c:forEach items="${all}" var="user">
							<option value="${user.id}" ${event.createBy.id eq user.id?'selected':''} >${user.name}</option>
						</c:forEach>		
					</select>&nbsp;&nbsp;
				</shiro:hasPermission>
				   Serious case：<select name="isEvil" style="width: 80px" id="isEvil">
						<option value=""><spring:message code='custom_event_all'/></option>
						<option value="0" ${'0' eq event.isEvil?'selected':''} >YES</option>
						<option value="1" ${'1' eq event.isEvil?'selected':''} >NO</option>
					</select>&nbsp;&nbsp;
			</div>
		</div>
		&nbsp;&nbsp;<a id="s" onclick="select('')"><spring:message code='custom_event_all'/></a>&nbsp;
		<a id="s0" onclick="select('0')"><spring:message code='custom_event_noResponse'/></a>&nbsp;
		<a id="s1" onclick="select('1')"><spring:message code='custom_event_processing'/></a>&nbsp;
		<a id="s2" onclick="select('2')"><spring:message code='custom_event_completed'/></a>&nbsp;
		<a id="s4" onclick="select('4')"><spring:message code='custom_event_closed'/></a>&nbsp;
		&nbsp;&nbsp;
		<shiro:hasPermission name="custom:event:all">
			<input type="checkbox" id="aboutMe" ${not empty event.masterBy.id?'checked':''}/><spring:message code='custom_event_note18'/>
			<input type="hidden" name="masterBy.id" id="aboutMeVal" value="${not empty event.masterBy.id?cuser.id:''}">
		</shiro:hasPermission>
		<input type="checkbox" name="priority" id="me" value="1" ${event.priority eq '1'?'checked':''}/><spring:message code='custom_event_note19'/>&nbsp;&nbsp;
		<shiro:lacksPermission name="custom:event:all">
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</shiro:lacksPermission>
		<c:if test="${'1' eq event.type }">
		   Attr：<select name="productAttribute" style="width: 80px" id="productAttribute">
					<option value=""><spring:message code='custom_event_all'/></option>
					<option value="主力" ${'主力' eq event.productAttribute?'selected':''} >主力</option>
					<option value="新品" ${'新品' eq event.productAttribute?'selected':''} >新品</option>
					<option value="普通" ${'普通' eq event.productAttribute?'selected':''} >普通</option>
					<option value="淘汰" ${'淘汰' eq event.productAttribute?'selected':''} >淘汰</option>
			</select>&nbsp;&nbsp;
		</c:if>
		 <spring:message code="amazon_sales_product_line"/>:<select id="productLine" style="width: 150px" name="productLine">
						<option value="">--All--</option>
						<c:forEach items="${groupType}" var="groupType">
							<option value="${groupType.id}" ${fn:trim(groupType.id) eq fn:trim(event.productLine)?'selected':''}>${groupType.name}</option>			
						</c:forEach>
		 </select>
		
		<c:if test="${canProcess && fn:length(offices)>0}">
			<br/><br/>
			<select id="transmitSelOther" style="width: 100px">
				<c:forEach items="${offices}" var="office">
					<c:forEach items="${office.userList}" var="user">
						<option value="${user.id}">${user.name}</option>
					</c:forEach>
				</c:forEach>
			</select>&nbsp;
			<input id="bathTransmitOther" class="btn btn-primary" type="button" value="<spring:message code='custom_event_btn6'/>"/>&nbsp;
		</c:if>
		
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<label><spring:message code='custom_event_note20'/>/Email/CustomName/CustomerID ：</label><form:input path="subject" htmlEscape="false" maxlength="50" class="input-small" cssStyle="width:180px"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<input class="btn btn-primary" type="submit" value="<spring:message code='sys_but_search'/>"/>
				<input id="btnExport" class="btn btn-primary" type="button" value="<spring:message code='sys_but_export'/>"/>
				<input id="timeoutEvent" class="btn btn-primary" type="button" value="Timeout event"/>
				
				<input  class="btn btn-primary btnTaxExp" type="button" value="TaxBill Export"/>
	</form:form>
	<c:if test="${not empty event.remarks}">
		<div class="alert alert-info"><strong >${event.remarks}<spring:message code='custom_event_note21'/></strong> <input class="btn" type="button" value="<spring:message code='sys_but_back'/>" id="reback"/></div>
	</c:if>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
		<th style="width: 20px">
			<span>
				<input type="checkbox">
				</span>
		</th>
		<th style="width: 30px"><spring:message code='custom_event_form4'/></th>
		<th style="width: 70px"><spring:message code='sys_label_country'/></th>
		<th style="width: 85px"><spring:message code='custom_event_form15'/></th>
		<th style="width: 70px"><spring:message code='custom_event_form23'/></th>
		<th style="width: 270px"><spring:message code='custom_event_form'/></th>
		<th><spring:message code='custom_event_form2'/></th>
		<th style="width: 60px" class="sort createBy"><spring:message code='custom_event_form17'/></th>
		<th style="width: 60px" class="sort masterBy"><spring:message code='custom_event_colName'/></th>
		<th style="width: 50px"><spring:message code='custom_event_form3'/></th>
		<th style="width: 50px"><spring:message code='custom_event_form21'/></th>
		<th style="width: 50px"><spring:message code='custom_event_form20'/></th>
		<th style="width: 55px" class="sort createDate"><spring:message code='custom_event_form24'/></th>
		<th style="width: 55px" class="sort updateDate"><spring:message code='custom_event_form25'/></th>
		<th style="width: 90px">Operation</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="event" varStatus="i">
			<tr>
				<td>
					<div class="checker">
					<span>
						<c:if test="${(canProcess || cuser.id eq event.masterBy.id) && (event.state eq '0' || event.state eq '1')}">	
						  <input type="checkbox"/>
						  <input type="hidden" value="${event.id}" class="eventId"/>
						</c:if>  
					</span>
					</div>
				</td>
				<td>${event.typeStr}</td>
				<td>${fns:getDictLabel(event.country,'platform','Others')}<br/>${event.accountName}</td>
				<td><c:choose>
				<c:when test="${empty event.remarks}">
					<spring:message code='custom_event_note' />
				</c:when>
				<c:otherwise>
				    <c:set value='' var='classType'/>
				    <c:if test="${('1' eq event.type||'6' eq event.type)&&not empty event.productAttribute}">
					    <c:if test="${'新品' eq event.productAttribute}"><c:set value='style="color:orange"'   var='classType'/></c:if>
						<c:if test="${'主力' eq event.productAttribute}"><c:set value='style="color:green"'   var='classType'/></c:if>
						<c:if test="${'淘汰' eq event.productAttribute}"><c:set value='style="color:gray"'  var='classType'/></c:if>
					</c:if>
					<a class="asin" title='${event.productAttribute}' ${classType }>${products[i.index]}</a>
					<input type="hidden" value="${event.remarks}:${event.country}" />
				</c:otherwise></c:choose></td>
				<td>
					<c:set var="flag"  value="true" />
					<c:if test="${not empty event.reviewLink }">
						<c:forEach items="${fn:split(event.reviewLink, ',')}" var="link">
							<c:if test="${not empty link && flag}">
								<c:set var="flag"  value="false" />
								<a href="${link}" target=_blank>SPR-${event.id}</a>
							</c:if>
						</c:forEach>
					</c:if>
					<c:if test="${flag}">
						SPR-${event.id}
					</c:if>
				</td>
				<td><a href="${ctx}/custom/event/form?id=${event.id}" rel="popover" data-content="${event.subjectStr2}">${event.subjectStr}<span style='color: orange;'>${countComment[i.index]}</span>
					<c:if test="${event.type eq '10' && event.emailNotice ne '0'}">
					<span style='color: red;'>[Already send email notification]</span>
					</c:if>
				</a></td>
				<td><a href="${ctx}/custom/event/form?id=${event.id}" rel="popover" data-content="${event.reason}">${fn:substring(event.reason,0,6)}${not empty event.reason?'...':''}</a></td>
				<td>${event.createBy.name}</td>
				<td>${event.masterBy.name}</td>
				<td>${event.priorityStr}</td>
				<td>${event.stateStr}</td>
				<td><a rel="popover" data-content="${event.result}">${fn:substring(event.result,0,6)}</a></td>
				<td><fmt:formatDate pattern="M-dd H" value="${event.createDate}"/></td>
				<td><fmt:formatDate pattern="M-dd H" value="${event.updateDate}"/></td>
				<td>
					<c:choose>
						<c:when test="${cuser.id eq event.masterBy.id && (event.state eq '0' || event.state eq '1')}">
							<a href="${ctx}/custom/event/form?id=${event.id}">process</a>&nbsp;&nbsp;
						</c:when>
						<c:when test="${cuser.id eq event.masterBy.id && (event.state eq '4' || event.state eq '2')}">
							<a href="${ctx}/custom/event/reopen?id=${event.id}"><spring:message code='custom_event_btn5'/></a>&nbsp;&nbsp;
							<a href="${ctx}/custom/event/form?id=${event.id}">view</a>&nbsp;&nbsp;
						</c:when>
						<c:otherwise>
							<a href="${ctx}/custom/event/form?id=${event.id}">view</a>&nbsp;&nbsp;
						</c:otherwise>
					</c:choose>
					<shiro:hasPermission name="custom:event:edit"><a href="${ctx}/custom/event/delete?id=${event.id}" onclick="return confirmx('<spring:message code="custom_event_note22" />', this.href)">delete</a></shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
