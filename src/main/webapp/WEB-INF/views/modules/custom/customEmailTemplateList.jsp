<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Product</title>
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
	
		if(!(top)){
			top = self;			
		}	
		
		$(function(){
			
			$("select[name='templateType']").change(function(){
				$("#searchForm").submit();
			});
		
			$("select[name='country']").change(function(){
				$("#searchForm").submit();
			});
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
			
			$("#bathTransmitOther").click(function(){
				if($(".checked :hidden").size()){
					var userId = $("#transmitSelOther").val();
					var userName = $("#transmitSelOther option[value='"+userId+"']").text();
					top.$.jBox.confirm("",'Forwarding To '+userName+'?',function(v,h,f){
						if(v=='ok'){
							var params = {};
							params.eid = [];
							$(".checked :hidden").each(function(){
								params.eid[params.eid.length] = $(this).val();
							}); 
							params.userId = userId;
							window.location.href = "${ctx}/custom/emailTemplate/batchTransmitOther?"+$.param(params);
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}else{
					top.$.jBox.tip("No one yet finished processing the message, not forward!","error",{persistent:false,opacity:0});
				}
			});
			
		});
		
		function updateState(obj,id,stateStr){
			if($(obj).attr("checked")){
				stateStr = stateStr+"=1";
			}else{
				stateStr = stateStr+"=0";
			}
			stateStr = stateStr+"&id="+id;
			$.get("${ctx}/psi/product/updateProductState?"+stateStr,function(data){
				if(data){
					top.$.jBox.tip(data);
				}
			});
		}
		
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
		<li class="active"><a href="${ctx}/custom/emailTemplate/list"><spring:message code='custom_email_template_list'/></a></li>
	    <li><a href="${ctx}/custom/emailTemplate/add"><spring:message code='custom_email_template_add'/></a></li>
		<shiro:hasPermission name="amazoninfo:afterSale:view">
			<li><a href="${ctx}/amazoninfo/afterSale"><spring:message code='sys_menu_afterSaleEmail'/></a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="customEmailTemplate" action="${ctx}/custom/emailTemplate/list" method="post" class="breadcrumb form-search">
		<div style="vertical-align: middle;height: 75px;line-height: 40px">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>			
			<label class="control-label"><spring:message code='custom_email_template_type'/></label>:
			<select  id="templateType" name="templateType" style="width: 140px" >
				<option value="0" ${customEmailTemplate.templateType eq '0'?'selected':''}><spring:message code='custom_email_template_sys'/></option>
				<option value="1" ${customEmailTemplate.templateType eq '1'?'selected':''}><spring:message code='custom_email_template_share'/></option>
				<option value="2" ${customEmailTemplate.templateType eq '2'?'selected':''}><spring:message code='custom_email_template_self'/></option>
				<option value="3" ${customEmailTemplate.templateType eq '3'?'selected':''}><spring:message code='custom_email_template_after'/></option>
				<option value="4" ${customEmailTemplate.templateType eq '4'?'selected':''}><spring:message code='custom_email_template_review'/></option>
				<option value="5" ${customEmailTemplate.templateType eq '5'?'selected':''}><spring:message code='custom_email_template_manual'/></option>
				<option value="6" ${customEmailTemplate.templateType eq '6'?'selected':''}><spring:message code='custom_email_template_feedback'/></option>
			</select>
			&nbsp;&nbsp;
			<label class="control-label"><spring:message code='amazon_order_form4'/></label>
			<select name="country" id="country" style="width: 140px">
				<option value="" ><spring:message code='custom_event_all'/></option>
				<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
					<c:if test="${dic.value ne 'com.unitek'}">
						<option value="${dic.value}" ${dic.value eq customEmailTemplate.country?'selected':''}>${dic.label}</option>
					</c:if>
				</c:forEach>
			</select>
			<div>
				<select id="transmitSelOther" style="width: 100px">
					<c:forEach items="${offices}" var="office">
						<c:forEach items="${office.userList}" var="user">
							<option value="${user.id}">${user.name}</option>
						</c:forEach>
					</c:forEach>
				</select>&nbsp;
				<input id="bathTransmitOther" class="btn btn-primary" type="button" value="<spring:message code='custom_email_template_transfer'/>"/>&nbsp;
				<label><spring:message code='custom_email_template_name'/>/<spring:message code='custom_email_template_subject'/>
					/<spring:message code='custom_email_template_content'/>：</label><form:input path="templateName" htmlEscape="false" maxlength="50" class="input-small"/>
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width: 20px">
					<span><input type="checkbox"></span>
				</th>
			   <th class="sort templateType"><spring:message code='custom_email_template_type'/></th>
			   <th class="sort country"><spring:message code='amazon_order_form4'/></th>
			   <th class="sort role"><spring:message code='custom_email_template_group'/></th>
			   <th class="sort templateName"><spring:message code='custom_email_template_name'/></th>
			   <th class="sort templateSubject"><spring:message code='custom_email_template_subject'/></th>
			   <th class="sort createBy"><spring:message code='custom_email_template_createBy'/></th>
			   <th class="sort createDate"><spring:message code='custom_email_template_createTime'/></th>
			   <th><spring:message code='custom_email_template_lastUpdateBy'/></th>
			   <th><spring:message code='sys_label_tips_operate'/></th>
		   </tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="template">
			<tr>
				 <td>
					<div class="checker">
					<span>
						<c:if test="${fns:getUser().id eq template.createBy.id}">	
						  <input type="checkbox"/>
						  <input type="hidden" value="${template.id}" class="templateId"/>
						</c:if>  
					</span>
					</div>
				 </td>
	             <td>
	              <c:choose>
	                 <c:when test="${template.templateType eq '0'}">
	                     <spring:message code='custom_email_template_sys'/>
	                 </c:when>
	                  <c:when test="${template.templateType eq '1'}">
	                     <spring:message code='custom_email_template_share'/>                                                     
	                 </c:when>
	                  <c:when test="${template.templateType eq '3'}">
	                     <spring:message code='custom_email_template_after'/>                                                     
	                 </c:when>
	                  <c:when test="${template.templateType eq '4'}">
	                     <spring:message code='custom_email_template_review'/>                                                     
	                 </c:when>
	                  <c:when test="${template.templateType eq '5'}">
	                     <spring:message code='custom_email_template_manual'/>                                                     
	                 </c:when>
	                  <c:when test="${template.templateType eq '6'}">
	                     <spring:message code='custom_email_template_feedback'/>                                                  
	                 </c:when>
	                 <c:otherwise>
	                     <spring:message code='custom_email_template_self'/>                                                    
	                 </c:otherwise>
	              </c:choose>	             
	             </td>
	             <td>${fns:getDictLabel(template.country,'platform','')}</td>
	             <td>${template.role.name}</td>
	             <td>${template.templateName}</td>
	             <td>${template.templateSubject}</td>
	             <td>${template.createBy.name}</td>
	             <td>
	             <fmt:formatDate type="both" value="${template.createDate}" pattern="yyyy-MM-dd H:mm"/>
	             </td>
	              <td>${template.lastUpdateBy}</td>
	             <td>
	              
	                 <c:choose>
	                    <c:when test="${template.templateType eq '0'}">
	                        <a href="${ctx}/custom/emailTemplate/view?id=${template.id}"><spring:message code='sys_but_view'/></a>&nbsp;&nbsp;
	                    </c:when>
	                    <c:otherwise>
	                        <a href="${ctx}/custom/emailTemplate/view?id=${template.id}"><spring:message code='sys_but_view'/></a>&nbsp;&nbsp;
	                	<a href="${ctx}/custom/emailTemplate/update?id=${template.id}"><spring:message code='sys_but_edit'/></a>&nbsp;&nbsp;
						<a href="${ctx}/custom/emailTemplate/delete?id=${template.id}" onclick="return confirmx('确定要删除该模板吗？', this.href)"><spring:message code='sys_but_delete'/></a>
	                    </c:otherwise>
	                 </c:choose>   
	             </td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>