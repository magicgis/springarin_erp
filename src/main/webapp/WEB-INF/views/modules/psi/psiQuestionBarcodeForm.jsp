<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>贴码错误信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			
			$(".Wdate").on("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
					$("#btnSubmit").attr("disabled","disabled");
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiQuestionBarcode/">贴码错误信息记录列表</a></li>
		<li class="active"><a href="${ctx}/psi/psiQuestionBarcode/form?id=${psiQuestionBarcode.id}">贴码错误信息${not empty psiQuestionBarcode.id?'编辑':'添加'}</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiQuestionBarcode" action="${ctx}/psi/psiQuestionBarcode/save" method="post" class="form-horizontal">
		<form:hidden path="id"/>
		<form:hidden path="delFlag"/>
		<form:hidden path="createUser.id" />
		<form:hidden path="updateUser.id" />
		<input type="hidden" name="createDate" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiQuestionBarcode.createDate}'/>"/>
		<input type="hidden" name="updateDate" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiQuestionBarcode.updateDate}'/>"/>
		
		<tags:message content="${message}"/>
		
		<div class="control-group">
			<label class="control-label">产品:</label>
			<c:set value="${psiQuestionBarcode.productId},${psiQuestionBarcode.productName}" var="idNameColor"/>
			<div class="controls">
			<form:select path="productNameTemp" style="width: 220px" class="required" >
				<c:forEach items="${proMap}" var="proEntry">
					<option value="${proEntry.key}" ${proEntry.key eq idNameColor ?'selected':''}  >${proEntry.value}</option>
				</c:forEach>	
			</form:select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">数量</label>
			<div class="controls">
				 <form:input  path="quantity" class="number"    maxlength="10" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">贴错时间</label>
			<div class="controls">
				 <input type="text" name="questionDate" required="required" class="Wdate" value="<fmt:formatDate value="${psiQuestionBarcode.questionDate}" pattern="yyyy-MM-dd" />" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">运单号:</label>    
			<div class="controls">
				<input name="transportOrderNo" type="text" value="${psiQuestionBarcode.transportOrderNo}"/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">错误方:</label>    
			<div class="controls">
				<input name="wrongSide" type="text" value="${psiQuestionBarcode.wrongSide}" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">出错原因:</label>
			<div class="controls">
				<form:textarea path="reason" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge" />
			</div>
		</div>
		
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
