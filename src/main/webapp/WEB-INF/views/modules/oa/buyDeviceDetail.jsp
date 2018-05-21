<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设备采购详细</title>
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
		<li><a href="${ctx}/oa/buyDevice/list/task">待办列表</a></li>
		<li><a href="${ctx}/oa/buyDevice/list">办公设备采购列表</a></li>
		<li class="active"><a href="${ctx}/oa/buyDevice/detail?id=${buyDevice.id}">办公设备采购查看</a></li>
	</ul>
	<form class="form-horizontal">
		<div class="control-group">
			<label class="control-label">申请人：</label>
			<div class="controls">
				${buyDevice.createBy.name}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">办公设备采购类型：</label>
			<div class="controls">
				${buyDevice.deviceTypeDictLabel}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">办公设备清单：</label>
			<div class="controls">
				${buyDevice.name}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">总金额：</label>
			<div class="controls">
				${buyDevice.price}元
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">采购理由：</label>
			<div class="controls">
				${buyDevice.reason}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">当前状态：</label>
			<div class="controls">
				${buyDevice.processStatus}
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
	<c:if  test="${buyDevice.audit}">
		<c:if test="${buyDevice.processStatus eq '部门主管审批'}">
			<form:form id="inputForm" modelAttribute="buyDevice" action="${ctx}/oa/buyDevice/deptLeaderAudit" method="post" class="form-horizontal">
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
		<c:if test="${buyDevice.processStatus eq '调整申请'}">
			<form:form id="inputForm" modelAttribute="buyDevice" action="${ctx}/oa/buyDevice/modifyApply" method="post" class="form-horizontal">
				<form:hidden path="id"/>
				<form:hidden path="pass"/>
				<tags:message content="${message}"/>
				<div class="control-group">
					<label class="control-label">设备采购类型：</label>
					<div class="controls">
						<form:select path="deviceType" >
							<form:options items="${fns:getDictList('officeDeviceType')}" itemLabel="label" itemValue="value" htmlEscape="false" />
						</form:select>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">办公设备采购清单：</label>
					<div class="controls">
						<form:textarea path="name" class="required" rows="4" maxlength="255" cssStyle="width:600px"/>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">总金额：</label>
					<div class="controls">
						<div class="input-prepend input-append">
							<input  class="span2" name="price" size="16" type="text" value="${buyDevice.price}"/>
							<span class="add-on">￥</span>
						</div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">设备采购理由：</label>
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
		<c:if test="${buyDevice.processStatus eq '总经理审批'}">
			<form:form id="inputForm" modelAttribute="buyDevice" action="${ctx}/oa/buyDevice/mgrAudit" method="post" class="form-horizontal">
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
		<c:if test="${buyDevice.processStatus eq '财务支出费用'}">
			<form:form id="inputForm" modelAttribute="buyDevice" action="${ctx}/oa/buyDevice/getMoney" method="post" class="form-horizontal">
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
		<c:if test="${buyDevice.processStatus eq '资产登记'}">
			<form:form id="inputForm" modelAttribute="buyDevice" action="${ctx}/oa/buyDevice/report" method="post" class="form-horizontal">
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
