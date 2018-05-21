<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>供应商退款处理记录</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			
			$(".Wdate").on("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
		 	$('#myfileupload').MultiFile({
				max : 30,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			}); 
			
			
			$("#inputForm").validate({
				submitHandler: function(form){
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
		<li><a href="${ctx}/psi/purchaseOrder/deliveryRate">交货延期率</a></li>
    	<li><a href="${ctx}/psi/lcPsiLadingBill/testCount">产品合格率</a></li>
    	<li><a href="${ctx}/psi/lcPsiLadingBill/testCountSupplier">供应商合格率</a></li>
    	<li ><a href="${ctx}/psi/purchaseOrder/supplierIndemnifyList">供应商赔偿记录</a></li>
    	<li class="active"><a href="${ctx}/psi/purchaseOrder/supplierIndemnifyForm">新增供应商赔偿记录</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiSupplierIndemnify" action="${ctx}/psi/purchaseOrder/saveIndemnify" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input type="hidden" name="id"  value="${psiSupplierIndemnify.id}"/>
		<input type="hidden" name="attchmentPath"  value="${psiSupplierIndemnify.attchmentPath}"/>
		<input type="hidden" name="createDate" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiSupplierIndemnify.createDate}'/>" />
		<input type="hidden" name="createUser.id" value="${psiSupplierIndemnify.createUser.id}"/>
		<input type="hidden" name="delFlag"  value="${psiSupplierIndemnify.delFlag}"/>
		<div class="control-group">
			<label class="control-label">供应商:</label>
			<div class="controls">
				<select name="supplier.id"  id="supplierId" style="width:41%">
						<c:forEach items="${suppliers}" var="supplier" varStatus="i">
				            <option value='${supplier.id}' ${supplier.id eq  psiSupplierIndemnify.supplier.id?'selected':''}>${supplier.nikename}</option>;
			            </c:forEach>
				</select>
			</div>
		</div>
	
		<div class="control-group">
			<label class="control-label">主题:</label>
			<div class="controls">
				<input name="subject"  type="text" maxlength="100" class="required" style="width:40%" value="${psiSupplierIndemnify.subject}" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">金额:</label>
			<div class="controls">
				<input name="money"  type="text" maxlength="100" class="required price" style="width:40%" value="${psiSupplierIndemnify.money}" />
			</div>
		</div>
		
			<div class="control-group">
			<label class="control-label">赔偿状态:</label>
			<div class="controls">
			<select name="state"  style="width:43%">
				<option value="0" ${'0' eq psiSupplierIndemnify.state?'selected':''}>未支付</option>
				<option value="1" ${'1' eq psiSupplierIndemnify.state?'selected':''}>已支付</option>
				<option value="8" ${'8' eq psiSupplierIndemnify.state?'selected':''}>已取消</option>
			</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<textarea name="remark"  htmlEscape="false" rows="4" maxlength="200" class="required input-xxlarge" />${psiSupplierIndemnify.remark }</textarea>
			</div>
		</div>
		
		
		<div class="control-group" >
		    <label class="control-label" style="height:100px">附件:</label>
			<div class="controls">
				<span class="help-inline">支持多附件</span>
				<span class="label label-warning" style="font-size: 18px;">No Chinese or space in the file name</span>
				<input name="attchmentFile" type="file" id="myfileupload" />
			</div>
		</div> 
		
		<div class="form-actions">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
