<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊打折详细</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
	
		$(document).ready(function() {
			$("#name").focus();
			$("#inputForm").validate({
				rules:{price:{number:true,required:true}},
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
		function auditPass(isPass) {
			if(!(top)){
				top = self; 
			}
			top.$.jBox.confirm("确认提交数据？","系统提示",function(v,h,f){
			    if (v == 'ok') {
					$("#pass").val(isPass);
					$("#inputForm").submit();
			    }
			});
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/oa/amazonDiscount/list/task">待办列表</a></li>
		<li><a href="${ctx}/oa/amazonDiscount/list">办公亚马逊打折列表</a></li>
		<li class="active"><a href="${ctx}/oa/amazonDiscount/detail?id=${amazonDiscount.id}">办公亚马逊打折查看</a></li>
	</ul>
	<form class="form-horizontal">
		<div class="control-group">
			<label class="control-label">申请人：</label>
			<div class="controls">
				${amazonDiscount.createBy.name}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">打折范围：</label>
			<div class="controls">
				${amazonDiscount.discountScope}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">开始时间：</label>
			<div class="controls">
				<fmt:formatDate value="${amazonDiscount.startDate}" pattern="yyyy-MM-dd"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">结束时间：</label>
			<div class="controls">
				<fmt:formatDate value="${amazonDiscount.endDate}" pattern="yyyy-MM-dd"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">打折金额：</label>
			<div class="controls">
				${amazonDiscount.price}元
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">打折理由：</label>
			<div class="controls">
				${amazonDiscount.reason}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">当前状态：</label>
			<div class="controls">
				${amazonDiscount.processStatus}
			</div>
		</div>
	</form>
	<c:if  test="${not empty workflowEntity.historicTaskInstances}">
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead><tr>
				<th style="width: 100px">节点名称</th>
				<th style="width: 100px">审批人</th>
				<th style="width: 150px">审批时间</th>
				<th style="width: 50px">审批结果</th>
				<th>审批备注</th>
			</tr></thead>
			<tbody>
				<c:forEach items="${workflowEntity.historicTaskInstances}" var="historicTaskInstance">
					<c:if test="${not empty  historicTaskInstance.endTime}">
						<tr>
							<td>${historicTaskInstance.name}</td>
							<td>${fns:getUserById(historicTaskInstance.assignee).name}</td>
							<td><fmt:formatDate value="${historicTaskInstance.endTime}" pattern="yyyy-MM-dd hh:mm:ss"/></td>
							<td><c:if test="${not empty workflowEntity.variableMap[historicTaskInstance.id] }">${workflowEntity.variableMap[historicTaskInstance.id]?'通过':'不通过'}</c:if></td>
							<td>${workflowEntity.commentMap[historicTaskInstance.id]}</td>
						</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</c:if>
	<c:if  test="${amazonDiscount.audit}">
		<c:if test="${amazonDiscount.processStatus eq '部门主管审批'}">
			<form:form id="inputForm" modelAttribute="amazonDiscount" action="${ctx}/oa/amazonDiscount/deptLeaderAudit" method="post" class="form-horizontal">
				<form:hidden path="id"/>
				<form:hidden path="pass"/>
				<div class="control-group">
					<label class="control-label">审批备注：</label>
					<div class="controls">
						<form:textarea path="auditRemarks" class="required" rows="8" maxlength="200" cssStyle="width:600px"/>
					</div>
				</div>
				<div class="form-actions">
					<input class="btn btn-primary" type="button" value="通过"  onclick="auditPass(true);"/>&nbsp;
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
					<div class="pull-right">
						<input class="btn btn-warning" type="button" value="不通过" onclick="auditPass(false); " style="margin-right: 300px;"/>
					</div>
				</div>
			</form:form>
		</c:if>
		<c:if test="${amazonDiscount.processStatus eq '重新调整'}">
			<form:form id="inputForm" modelAttribute="amazonDiscount" action="${ctx}/oa/amazonDiscount/modifyApply" method="post" class="form-horizontal">
				<form:hidden path="id"/>
				<form:hidden path="pass"/>
				<tags:message content="${message}"/>
				<div class="control-group">
					<label class="control-label">亚马逊打折范围：</label>
					<div class="controls">
						<form:textarea path="discountScope" class="required" rows="4" maxlength="255" cssStyle="width:600px"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">开始时间：</label>
					<div class="controls">
						<input id="startDate" value="<fmt:formatDate value="${amazonDiscount.startDate}" pattern="yyyy-MM-dd"/>" name="startDate" type="text" readonly="readonly" maxlength="20" class="Wdate required"
							onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">结束时间：</label>
					<div class="controls">
						<input id="endDate" value="<fmt:formatDate value="${amazonDiscount.endDate}" pattern="yyyy-MM-dd"/>" name="endDate" type="text" readonly="readonly" maxlength="20" class="Wdate required"
							onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">打折金额：</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input value="${amazonDiscount.price}" class="span2" name="price" size="16" type="text"/>
							<span class="add-on">EUR</span>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">打折理由：</label>
					<div class="controls">
						<form:textarea path="reason" class="required" rows="8" maxlength="255" cssStyle="width:600px"/>
					</div>
				</div>
				<div class="form-actions">
					<input class="btn btn-primary" type="button" value="保存"  onclick="auditPass(true);"/>&nbsp;
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
					<div class="pull-right">
						<input class="btn btn-warning" type="button" value="取消申请" onclick="auditPass(false); " style="margin-right: 300px;"/>
					</div>
				</div>
			</form:form>
		</c:if>
		<c:if test="${amazonDiscount.processStatus eq '总经理审批'}">
			<form:form id="inputForm" modelAttribute="amazonDiscount" action="${ctx}/oa/amazonDiscount/mgrAudit" method="post" class="form-horizontal">
				<form:hidden path="id"/>
				<form:hidden path="pass"/>
				<div class="control-group">
					<label class="control-label">审批备注：</label>
					<div class="controls">
						<form:textarea path="auditRemarks" class="required" rows="8" maxlength="200" cssStyle="width:600px" />
					</div>
				</div>
				<div class="form-actions">
					<input class="btn btn-primary" type="button" value="通过"  onclick="auditPass(true);"/>&nbsp;
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
					<div class="pull-right">
						<input class="btn btn-warning" type="button" value="不通过" onclick="auditPass(false); " style="margin-right: 300px;"/>
					</div>
				</div>
			</form:form>
		</c:if>
		<c:if test="${amazonDiscount.processStatus eq '执行打折'}">
			<form:form id="inputForm" modelAttribute="amazonDiscount" action="${ctx}/oa/amazonDiscount/excute" method="post" class="form-horizontal">
				<form:hidden path="id"/>
				<form:hidden path="pass"/>
				<div class="control-group">
					<label class="control-label">备注：</label>
					<div class="controls">
						<form:textarea path="auditRemarks" class="required" rows="8" maxlength="200" cssStyle="width:600px" />
					</div>
				</div>
				<div class="form-actions">
					<input class="btn btn-primary" type="submit" value="保存" />&nbsp;
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>
			</form:form>
		</c:if>
		<c:if test="${amazonDiscount.processStatus eq '部门主管确认'}">
			<form:form id="inputForm" modelAttribute="amazonDiscount" action="${ctx}/oa/amazonDiscount/report" method="post" class="form-horizontal">
				<form:hidden path="id"/>
				<form:hidden path="pass"/>
				<div class="control-group">
					<label class="control-label">备注：</label>
					<div class="controls">
						<form:textarea path="auditRemarks" class="required" rows="8" maxlength="200" cssStyle="width:600px" />
					</div>
				</div>
				<div class="form-actions">
					<input class="btn btn-primary" type="submit" value="保存" />&nbsp;
					<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
				</div>
			</form:form>
		</c:if>
	</c:if>
</body>
</html>
