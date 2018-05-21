<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品模具费</title>
	<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
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
			
			$("#supplier").on("change",function(e){
				var params = {};
				params['id'] = $("#id").val();
				params['supplier.id'] = $(this).val();
				window.location.href = "${ctx}/psi/productMoldFee/form?"+$.param(params);
			});
			

			$(".returnFlag").change(function(){
				var flag = $(this).val();
				if(flag == '0'){
					$("#returnNum").attr("required","required");
				} else{
					$("#returnNum").removeAttr("required");
				}
			});
			
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/productMoldFee">产品模具费列表</a></li>
		<li class="active"><a href="${ctx}/psi/productMoldFee/form?id=${productMoldFee.id}">${not empty user.id?'修改':'新增'}模具费</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiProductMoldFee" action="${ctx}/psi/productMoldFee/save" 
		method="post" class="form-horizontal">
		<form:hidden path="id"/>
		
		<div class="control-group">
			<label class="control-label">供应商:</label>
			<div class="controls" >
			<span>
				<select style="width:150px;" id="supplier" name="supplier.id">
					<c:forEach items="${suppliers}" var="supplier" varStatus="i">
						 <option value='${supplier.id}' ${psiProductMoldFee.supplier.id eq supplier.id ?'selected' :''}>${supplier.nikename}</option>;
					</c:forEach>
				</select>
			</span>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">产品:</label>
			<div class="controls">
				<select name="productName" multiple="multiple" class="required" style="width:43%" id="product">
					<c:forEach items="${psiProductMoldFee.productNames}" var="pName">
						<option value="${pName}" selected>${pName}</option>
					</c:forEach>
					<c:forEach items="${colorNameList}" var="colorName">
						<option value="${colorName}">${colorName}</option>
					</c:forEach>
				</select>
			</div>
		</div>
	
		<div class="control-group">
			<label class="control-label">模具费(CNY):</label>
			<div class="controls">
				<form:input path="moldFee" style="width:42%" class="required price"/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">是否返还:</label>
			<div class="controls">
				<form:radiobutton path="returnFlag" value="0" class="required returnFlag"/>返还
				<form:radiobutton path="returnFlag" value="1" class="required returnFlag"/>不返还
			</div>
		</div>
	
		<div class="control-group">
			<label class="control-label">返还数量:</label>
			<div class="controls">
				<form:input path="returnNum" style="width:42%" class="number"/>
			</div>
		</div>
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
