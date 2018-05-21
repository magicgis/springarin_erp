<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>售后邮件审批</title>
	<%@ include file="/WEB-INF/views/include/dialog.jsp"%>
	<meta name="decorator" content="default"/>
	<style type="text/css">.sort{color:#0663A2;cursor:pointer;}</style>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
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

			$("a[rel='popover']").popover({html:true,trigger:'hover'});
			
			$("#country").change(function(){
				$("#searchForm").submit();
			});
			
			$(".autoSubmit").change(function(){
				$("#searchForm").submit();
			});
			
			$("#btnApproval").click(function(){
				if($(".checked :hidden").size()){
					top.$.jBox.confirm('确认审批通过?','提示',function(v,h,f){
						if(v=='ok'){
							var params = {};
							params.state = "1";
							params.eid = [];
							$(".checked :hidden").each(function(){
								params.eid[params.eid.length] = $(this).val();
							});
							window.location.href = "${ctx}/amazoninfo/afterSale/batchApproval?"+$.param(params);
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}else{
					top.$.jBox.tip("Please select at least one!","error",{persistent:false,opacity:0});
				}
			});
			
			$("#btnApprovalVeto").click(function(){
				if($(".checked :hidden").size()){
					top.$.jBox.confirm('确认审批否决?','提示',function(v,h,f){
						if(v=='ok'){
							var params = {};
							params.state = "2";
							params.eid = [];
							$(".checked :hidden").each(function(){
								params.eid[params.eid.length] = $(this).val();
							});
							window.location.href = "${ctx}/amazoninfo/afterSale/batchApproval?"+$.param(params);
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				}else{
					top.$.jBox.tip("Please select at least one!","error",{persistent:false,opacity:0});
				}
			});
			
		});
		
		function approv(id, state){
			var msg = "确认审批通过?";
			if(state == 2){
				msg = "确认审批否决?";
			}
			top.$.jBox.confirm(msg, "提示", function(v, h, f){
			  if (v == 'ok'){
			  	window.location.href = "${ctx}/amazoninfo/afterSale/approval?stateStr="+state+"&id=" + id;
			  }else{
			  	return true;
			  }
			  return true; //close
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
		<li><a href="${ctx}/amazoninfo/afterSale/list">售后邮件任务列表</a></li>
		<li class="active"><a href="#">任务审批</a></li>
		<li><a href="${ctx}/amazoninfo/afterSale/form">添加任务</a></li>
		<li><a href="${ctx}/amazoninfo/afterSale/sendList">邮件发送记录</a></li>
	</ul>
	
	<form:form id="searchForm" modelAttribute="amazonCustomFilter" action="${ctx}/amazoninfo/afterSale/auditList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 40px;line-height: 40px">
			<div style="height: 40px;">
				<label>创建时间：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="startDate" value="<fmt:formatDate value="${amazonCustomFilter.startDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="startDate"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${amazonCustomFilter.endDate}" pattern="yyyy-MM-dd" />" id="endDate" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				国家：<select name="country" id="country" style="width: 120px">
						<option value="" ${amazonCustomFilter.country eq ''?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<c:if test="${dic.value ne 'com.unitek'}">
								<option value="${dic.value}" ${amazonCustomFilter.country eq dic.value?'selected':''}  >${dic.label}</option>
							</c:if>
						</c:forEach>
				</select>&nbsp;&nbsp;
				任务类型：<select name="taskType" id="taskType" style="width: 120px" class="autoSubmit">
						<option value="" ${empty amazonCustomFilter.taskType?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<option value="1" ${amazonCustomFilter.taskType eq '1'?'selected':''}  >售后询问</option>
						<option value="2" ${amazonCustomFilter.taskType eq '2'?'selected':''}  >邀请评测</option>
						<option value="3" ${amazonCustomFilter.taskType eq '3'?'selected':''}  >产品说明书</option>
						<option value="4" ${amazonCustomFilter.taskType eq '4'?'selected':''}  >好评反馈</option>
				</select>&nbsp;&nbsp;
				审批状态：<select name="auditState" id="auditState" style="width: 120px" class="autoSubmit">
						<option value="" ${empty amazonCustomFilter.auditState?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<option value="0" ${amazonCustomFilter.auditState eq '0'?'selected':''}  >未审批</option>
						<option value="1" ${amazonCustomFilter.auditState eq '1'?'selected':''}  >审批通过</option>
						<option value="2" ${amazonCustomFilter.auditState eq '2'?'selected':''}  >审批否决</option>
				</select>
			    <input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>	
				<shiro:hasPermission name="amazoninfo:afterSale:approve">
					&nbsp;<input id="btnApproval" class="btn btn-primary" type="button" value="审批通过"/>
					&nbsp;<input id="btnApprovalVeto" class="btn btn-primary" type="button" value="审批否决"/>
				</shiro:hasPermission>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<shiro:hasPermission name="amazoninfo:afterSale:approve">
					<th style="width: 20px">
						<span><input type="checkbox"></span>
					</th>
				</shiro:hasPermission>
			   <th class="sort country">平台</th>	
			   <th class="sort createBy">创建人</th>
			   <th class="sort createDate">创建时间</th>
			   <th class="sort taskType">任务类型</th>
			   <th class="sort reason">群发原因</th>
			   <th class="sort template">模板名称</th>
			   <th class="sort totalCustomer">客户总数</th>
			   <th class="sort startDate">购买起始时间</th>
			   <th class="sort endDate">购买截止时间</th>
			   <th class="sort auditState">审核状态</th>
			   <th><spring:message code="sys_label_tips_operate"/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="amazonCustomFilter">
			<tr>
				<shiro:hasPermission name="amazoninfo:afterSale:approve">
					<td>
					<c:if test="${'0' eq amazonCustomFilter.auditState }">
						<div class="checker">
						<span>
						  <input type="checkbox"/>
						  <input type="hidden" value="${amazonCustomFilter.id}" class="taskId"/>
						</span>
						</div>
					</c:if>
					</td>
				</shiro:hasPermission>
				<td>${fns:getDictLabel(amazonCustomFilter.country,'platform','')}</td>
				<td>${amazonCustomFilter.createBy.name}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${amazonCustomFilter.createDate}"/></td>
				<td>
					<c:if test="${'1' eq amazonCustomFilter.taskType }">售后询问</c:if>
					<c:if test="${'2' eq amazonCustomFilter.taskType }">邀请评测</c:if>
					<c:if test="${'3' eq amazonCustomFilter.taskType }">产品说明书</c:if>
					<c:if test="${'4' eq amazonCustomFilter.taskType }">好评反馈</c:if>
				</td>
				<td>${amazonCustomFilter.reason}</td>
				<td><a href="#"  rel="popover" data-content="${amazonCustomFilter.remark}">${amazonCustomFilter.template.templateName}</a></td>
				<td>${amazonCustomFilter.totalCustomer}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${amazonCustomFilter.startDate}"/></td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${amazonCustomFilter.endDate}"/></td>
				<td>
					<c:if test="${'0' eq amazonCustomFilter.auditState }">未审批</c:if>
					<c:if test="${'1' eq amazonCustomFilter.auditState }">审批通过</c:if>
					<c:if test="${'2' eq amazonCustomFilter.auditState }">审批否决</c:if>
				</td>
				<td>
					<c:if test="${'0' eq amazonCustomFilter.state && fns:getUser().id eq amazonCustomFilter.createBy.id}">
						<a class="btn btn-success btn-small" href="${ctx}/amazoninfo/afterSale/form?id=${amazonCustomFilter.id}">编辑</a>
						<a class="btn btn-warning btn-small" href="${ctx}/amazoninfo/afterSale/delete?id=${amazonCustomFilter.id}">删除</a>
					</c:if>
					<a class="btn btn-info btn-small" href="${ctx}/amazoninfo/afterSale/view?flag=1&id=${amazonCustomFilter.id}">查看</a>
					<shiro:hasPermission name="amazoninfo:afterSale:approve">
						<c:if test="${'0' eq amazonCustomFilter.auditState }">
							<div class="btn-group">
								<button type="button" class="btn btn-success" >审批</button>
								<button type="button" class="btn btn-success dropdown-toggle" data-toggle="dropdown">
									<span class="caret"></span>
									<span class="sr-only"></span>
								</button>
								<ul class="dropdown-menu" id="allExport">
									<li><a onclick="approv(${amazonCustomFilter.id},1)" href="#">审批通过</a></li>
									<li><a onclick="approv(${amazonCustomFilter.id},2)" href="#">审批否决</a></li>
								</ul>
							</div>
						</c:if>
					</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
