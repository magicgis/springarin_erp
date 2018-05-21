<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>售后邮件任务</title>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@ include file="/WEB-INF/views/include/dialog.jsp"%>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr {
			float: right;
			min-height: 40px
		}
		
		.spanexl {
			float: left;
		 }
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
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

		$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn ) {
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				return $('td:eq('+iColumn+')', tr).text().replace("%","");
			} );
		};

		$.fn.dataTableExt.afnSortData['dom-html1'] = function ( oSettings, iColumn ) {
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				var a = $('td:eq('+iColumn+')', tr).find("a:eq(0)");
				if(a.text() == null || a.text() == ''){
					return -1;
				}
				return parseInt(a.text().split('-').join(''));
			});
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

			$("a[rel='popover']").popover({html:true,trigger:'hover'});
			
			$("#country").change(function(){
				$("#searchForm").submit();
			});
		
			$("#state").change(function(){
				$("#searchForm").submit();
			});
			
			$("#aboutMe").change(function(){
				$("#searchForm").submit();
			});
			
			$(".autoSubmit").change(function(){
				$("#searchForm").submit();
			});
			
			$("#contentTable").dataTable({
				"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"aoColumns": [
						         null,
						         null,
						         null,
						         null,
						         null,
						         null,
						         null,
						         null,
						         null,
							     { "sSortDataType":"dom-html1", "sType":"numeric" },	
							     { "sSortDataType":"dom-html", "sType":"numeric" },	
							     { "sSortDataType":"dom-html1", "sType":"numeric" },	
							     { "sSortDataType":"dom-html", "sType":"numeric" },	
						         null,
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
						         null,
						         null
							     ],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				"aoColumnDefs": [ { "bSortable": false, "aTargets": [ 0 ] }],
				"aaSorting": [[ 3, "desc" ]]
			});
			
			$(".dateEditor").editable({
				mode:'inline',
				showbuttons:'bottom',
				success:function(response,newValue){
					var param = {};
					var $this = $(this);
					var oldVal = $this.text();
					param.id = $this.parent().find(":hidden").val();
					param.endDateStr = newValue;
					$.get("${ctx}/amazoninfo/afterSale/updateEndDate?"+$.param(param),function(data){
						if(data != "1"){
							$this.text(oldVal);
							$.jBox.tip(data, 'info',{timeout:2000});
						}else{
							$.jBox.tip("修改成功！", 'info',{timeout:2000});
						}
					});
					return true;
				}
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
							window.location.href = "${ctx}/amazoninfo/afterSale/batchTransmitOther?"+$.param(params);
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
			$("#searchForm").submit();
        	return false;
        }
		
		
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#">售后邮件任务列表</a></li>
		<li><a href="${ctx}/amazoninfo/afterSale/auditList">任务审批</a></li>
		<li><a href="${ctx}/amazoninfo/afterSale/form">添加任务</a></li>
		<li><a href="${ctx}/amazoninfo/afterSale/sendList">邮件发送记录</a></li>
		<li><a href="${ctx}/custom/emailTemplate">邮件模板管理</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="amazonCustomFilter" action="${ctx}/amazoninfo/afterSale" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 75px;line-height: 40px">
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
				状态：<select name="state" id="state" style="width: 120px">
						<option value="" ${amazonCustomFilter.state eq ''?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<option value="0" ${amazonCustomFilter.state eq '0'?'selected':''}  >未开始</option>
						<option value="1" ${amazonCustomFilter.state eq '1'?'selected':''}  >进行中</option>
						<option value="2" ${amazonCustomFilter.state eq '2'?'selected':''}  >已完成</option>
						<option value="3" ${amazonCustomFilter.state eq '3'?'selected':''}  >已取消</option>
						<option value="4" ${amazonCustomFilter.state eq '4'?'selected':''}  >已暂停</option>
				</select>&nbsp;&nbsp;
			</div>
			<div>
				<select id="transmitSelOther" style="width: 100px;margin-left: 10px;">
					<c:forEach items="${offices}" var="office">
						<c:if test="${'3' eq office.id }">
							<c:forEach items="${office.userList}" var="user">
								<option value="${user.id}">${user.name}</option>
							</c:forEach>
						</c:if>
					</c:forEach>
				</select>&nbsp;
				<input id="bathTransmitOther" class="btn btn-primary" type="button" value="移  交"/>&nbsp;
				&nbsp;
				任务类型：<select name="taskType" id="taskType" style="width: 120px" class="autoSubmit">
						<option value="" ${empty amazonCustomFilter.taskType?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<option value="1" ${amazonCustomFilter.taskType eq '1'?'selected':''}  >售后询问</option>
						<option value="2" ${amazonCustomFilter.taskType eq '2'?'selected':''}  >邀请评测</option>
						<option value="3" ${amazonCustomFilter.taskType eq '3'?'selected':''}  >产品说明书</option>
						<option value="4" ${amazonCustomFilter.taskType eq '4'?'selected':''}  >好评反馈</option>
				</select>&nbsp;&nbsp;
				&nbsp;
				<input type="checkbox" name="aboutMe" id="aboutMe" value="1" ${aboutMe eq '1'?'checked':''}/><spring:message code='custom_email_btn3'/>
				 &nbsp;&nbsp;&nbsp;&nbsp;
			     <input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>	
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
			   <%--<th style="width: 10px">编号</th> --%>
			   <th>平台</th>	
			   <th>创建人</th>
			   <th>创建时间</th>
			   <th style="width: 100px">群发原因</th>
			   <th style="width: 100px">模板名称</th>
			   <th>客户总数</th>
			   <th>已发送</th>
			   <th>未发送</th>
			   <th>已回复</th>
			   <th>回复率</th>
			   <th>已评论</th>
			   <th>评论率</th>
			   <th>购买起始时间</th>
			   <th>购买截止时间</th>
			   <th>状态</th>
			   <th><spring:message code="sys_label_tips_operate"/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="amazonCustomFilter">
			<tr>
				 <td>
					<div class="checker">
					<span>
						<c:if test="${fns:getUser().id eq amazonCustomFilter.createBy.id || '1' eq fns:getUser().id}">	
						  <input type="checkbox"/>
						  <input type="hidden" value="${amazonCustomFilter.id}" class="filterId"/>
						</c:if>  
					</span>
					</div>
				 </td>
				<%--<td style="text-align: center;vertical-align: middle;">${amazonCustomFilter.id}</td> --%>
				<td>${fns:getDictLabel(amazonCustomFilter.country,'platform','')}</td>
				<td>${amazonCustomFilter.createBy.name}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${amazonCustomFilter.createDate}"/></td>
				<td>${amazonCustomFilter.reason}</td>
				<td><a href="#"  rel="popover" data-content="${amazonCustomFilter.remark}">${amazonCustomFilter.template.templateName}</a></td>
				<td>${amazonCustomFilter.totalCustomer}</td>
				<td>${amazonCustomFilter.sendNum}</td>
				<td>${amazonCustomFilter.notSendNum}</td>
				<td>
					<c:if test="${amazonCustomFilter.replyNum > 0}">
						<a class="btn btn-warning btn-small" href="${ctx}/amazoninfo/afterSale/sendList?isReply=1&task.id=${amazonCustomFilter.id}">${amazonCustomFilter.replyNum}</a>
					</c:if>
					<c:if test="${amazonCustomFilter.replyNum == 0}">	<%--兼容排序 --%>
						<a></a>
					</c:if>
				</td>
				<td>
					<c:if test="${amazonCustomFilter.replyNum > 0}">
						<fmt:formatNumber type="number" value="${amazonCustomFilter.replyPct}" pattern="0.00%" maxFractionDigits="2"/>
					</c:if>
				</td>
				<td>
					<c:if test="${amazonCustomFilter.reviewNum > 0}">
						<a class="btn btn-success btn-small" href="${ctx}/amazoninfo/afterSale/sendList?isReview=1&task.id=${amazonCustomFilter.id}">${amazonCustomFilter.reviewNum}</a>
					</c:if>
					<c:if test="${amazonCustomFilter.reviewNum == 0}">	<%--兼容排序 --%>
						<a></a>
					</c:if>
				</td>
				<td>
					<c:if test="${amazonCustomFilter.reviewNum > 0}">
						<fmt:formatNumber type="number" value="${amazonCustomFilter.reviewPct}" pattern="0.00%" maxFractionDigits="2"/>
					</c:if>
				</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${amazonCustomFilter.startDate}"/></td>
				<td>
					<c:choose>
						<c:when test="${('0' eq amazonCustomFilter.state || '1' eq amazonCustomFilter.state) && fns:getUser().id eq amazonCustomFilter.createBy.id }">
							<input type="hidden" value="${amazonCustomFilter.id}" />
							<a href="#" class="dateEditor"  data-type="date" data-pk="1" data-title="Enter Date">
								<fmt:formatDate pattern="yyyy-MM-dd" value="${amazonCustomFilter.endDate}"/>
							</a>
						</c:when>
						<c:otherwise>
							<a style="text-decoration:none;color:black">	<%--加a标签兼容自定义排序方法 --%>
								<fmt:formatDate pattern="yyyy-MM-dd" value="${amazonCustomFilter.endDate}"/>
							</a>
						</c:otherwise>
					</c:choose>
				</td>
				<td>${amazonCustomFilter.stateStr}</td>
				<td>
					<c:if test="${'0' eq amazonCustomFilter.state && fns:getUser().id eq amazonCustomFilter.createBy.id}">
						<a class="btn btn-warning btn-small" href="${ctx}/amazoninfo/afterSale/delete?id=${amazonCustomFilter.id}">删除</a>
					</c:if>
					<c:if test="${('1' eq amazonCustomFilter.state || '0' eq amazonCustomFilter.state) && fns:getUser().id eq amazonCustomFilter.createBy.id}">
						<a class="btn btn-success btn-small" href="${ctx}/amazoninfo/afterSale/form?id=${amazonCustomFilter.id}">编辑</a>
						<a class="btn btn-primary btn-small" href="${ctx}/amazoninfo/afterSale/stop?id=${amazonCustomFilter.id}" onclick="return confirmx('确认要暂停该任务吗？', this.href)">暂停</a>
					</c:if>
					<a class="btn btn-info btn-small" href="${ctx}/amazoninfo/afterSale/view?id=${amazonCustomFilter.id}">查看</a>
					<c:if test="${('1' eq amazonCustomFilter.state || '4' eq amazonCustomFilter.state) && fns:getUser().id eq amazonCustomFilter.createBy.id}">
						<a class="btn btn-warning btn-small" href="${ctx}/amazoninfo/afterSale/cancel?id=${amazonCustomFilter.id}">取消任务</a>
					</c:if>
					<c:if test="${'4' eq amazonCustomFilter.state && fns:getUser().id eq amazonCustomFilter.createBy.id}">
						<a class="btn btn-primary btn-small" href="${ctx}/amazoninfo/afterSale/restart?id=${amazonCustomFilter.id}" onclick="return confirmx('确认要恢复该任务吗？', this.href)">恢复</a>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<%--<div class="pagination">${page}</div> --%>
</body>
</html>
