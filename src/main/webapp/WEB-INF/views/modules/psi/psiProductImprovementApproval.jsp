<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品变更审批</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {

			if(!(top)){
				top = self; 
			}
			
			$("#inputForm").validate({
				submitHandler: function(form){
					top.$.jBox.confirm('确定要提交吗?','系统提示',function(v,h,f){
						if(v=='ok'){
							loading('正在提交，请稍等...');
							form.submit();
							$("#btnSubmit").attr("disabled","disabled");
						}
					},{buttonsFocus:1,persistent: true});
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
		
		function approval(appStatus){
			$("#appStatus").val(appStatus);
			$("#inputForm").submit();
		}
		
		function saveSuggestion(){
			$("#inputForm").attr("action","${ctx}/psi/productImprovement/saveSuggestion");
			$("#inputForm").submit();
		}
	</script>
</head>
<body>
	<br/>
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
			<label class="control-label">紧急程度:</label>
			<div class="controls">
				<c:if test="${'1' eq psiProductImprovement.type }">一般</c:if>
				<c:if test="${'2' eq psiProductImprovement.type }">紧急</c:if>
				<c:if test="${'3' eq psiProductImprovement.type }">特急</c:if>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">变更时间:</label>
			<div class="controls">
				<fmt:formatDate value="${psiProductImprovement.improveDate}" pattern="yyyy-MM-dd"/>
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
		
		<c:if test="${not empty psiProductImprovement.filePath }">
			<div class="control-group">
				<label class="control-label">变更涉及附件:</label>
				<div class="controls">
					<a href="${ctx}/psi/productImprovement/downloadFile?id=${productImprovement.id}">点击下载</a>
				</div>
			</div>
		</c:if>
		
		<c:if test="${'1' ne type }">
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
			
			<div class="control-group">
				<label class="control-label">审核意见:</label>
				<div class="controls">
					<form:textarea path="approvalContent" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge required" />
				</div>
			</div>
			<div class="form-actions">
				<input id="btnSubmit" class="btn btn-primary" type="button" onclick="approval('1')" value="审批通过"/>&nbsp;
				<input id="btnSubmit" class="btn btn-primary" type="button" onclick="approval('2')" value="审批否决"/>&nbsp;
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>
		</c:if>
		
		<c:if test="${'1' eq type }">
			<div class="control-group">
				<label class="control-label">处理意见:</label>
				<div class="controls">
					<textarea name="content" rows="4" maxlength="200" class="input-xxlarge required" ></textarea>
				</div>
			</div>
			<div class="form-actions">
				<input id="btnSubmit" class="btn btn-primary" type="button" onclick="saveSuggestion()" value="提交"/>&nbsp;
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</div>
		</c:if>
	</form:form>
</body>
</html>
