<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品变更详情</title>
	<meta name="decorator" content="default"/>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/productImprovement/">产品优化信息记录</a></li>
		<li class="active"><a href="${ctx}/psi/productImprovement/form?id=${productImprovement.id}">产品优化信息添加</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiProductImprovement" action="${ctx}/psi/productImprovement/approval" 
		method="post" class="form-horizontal">
		<input type="hidden" name="id" value="${psiProductImprovement.id }"/>
		<input type="hidden" id="appStatus" name="appStatus" value=""/>
		
		<div class="control-group">
			<label class="control-label">产品:</label>
			<div class="controls">
				<c:forEach items="${fn:split(psiProductImprovement.productName,',')}" var ="pName">
					<a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${pName}">${pName}</a>
				</c:forEach>
				(${psiProductImprovement.line }线)
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">创建人:</label>
			<div class="controls">
				${psiProductImprovement.createBy.name }
				(<fmt:formatDate type="date" value="${psiProductImprovement.createDate}" />)
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">紧急程度:</label>
			<div class="controls">
				<c:if test="${'1' eq psiProductImprovement.type }">一般</c:if>
				<c:if test="${'2' eq psiProductImprovement.type }">紧急</c:if>
				<c:if test="${'3' eq psiProductImprovement.type }">特急</c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">变更状态:</label>
			<div class="controls">
				<c:if test="${'0' eq psiProductImprovement.status }">新建</c:if>
				<c:if test="${'1' eq psiProductImprovement.status }">意见收集中</c:if>
				<c:if test="${'2' eq psiProductImprovement.status }">待审批</c:if>
				<c:if test="${'3' eq psiProductImprovement.status }">审批通过</c:if>
				<c:if test="${'4' eq psiProductImprovement.status }">已取消</c:if>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">起始订单号:</label>
			<div class="controls">
				${psiProductImprovement.orderNo }
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">变更时间:</label>
			<div class="controls">
				<fmt:formatDate type="date" value="${psiProductImprovement.improveDate}" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">变更原因:</label>
			<div class="controls">
				${psiProductImprovement.reason }
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">变更前说明:</label>
			<div class="controls">
				${psiProductImprovement.perRemark }
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">变更后说明:</label>
			<div class="controls">
				${psiProductImprovement.afterRemark }
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">审核人:</label>
			<div class="controls">
				${psiProductImprovement.approvalBy.name }
				(<fmt:formatDate type="date" value="${psiProductImprovement.approvalDate}" />)
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">审核意见:</label>
			<div class="controls">
				${psiProductImprovement.approvalContent }
			</div>
		</div>
		
		<c:if test="${not empty psiProductImprovement.filePath }">
			<div class="control-group">
				<label class="control-label">变更涉及附件:</label>
				<div class="controls">
					<a href="${ctx}/psi/productImprovement/downloadFile?id=${psiProductImprovement.id}">点击下载</a>
				</div>
			</div>
		</c:if>
		
		<div class="control-group">
			<label class="control-label">处理意见：</label>
			<div class="controls">
				<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th>部门</th>
						<th>处理意见</th>
						<th>创建人</th>
					</tr>
				</thead>
				<tbody>
				<c:forEach items="${psiProductImprovement.items }" var="item">
					<tr>
						<td>${item.department}</td>
						<td>${item.content}</td>
						<td>${item.createBy.name}</td>
					</tr>
				</c:forEach>
				</tbody>
			</table>
			</div>
		</div>
		
		<div class="form-actions">
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
