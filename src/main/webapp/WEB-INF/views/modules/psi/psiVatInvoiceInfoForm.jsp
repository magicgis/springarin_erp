<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<meta name="decorator" content="default" />
<title>增值发票信息</title>
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
			eval('var productMap=${productMap}');
			
			$(".Wdate").on("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			
			$("#supplier").change(function(){
				var supplierId = $(this).val();
				var arr=productMap[supplierId];
				var optionStr="";
					console.log(arr);
				if(arr!=undefined){
					for(var i=0;i<arr.length;i++){
						optionStr=optionStr+"<option value='"+arr[i]+"'>"+arr[i]+"</option>";
					}
				}
				$("#product").select2().empty();
				$("#product").select2("data",[]);
				$("#product").append(optionStr);
			});
			
			$("#supplier").change();
			
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
		<li><a href="${ctx}/psi/psiVatInvoiceInfo/">增值税发票信息管理</a></li>
		<li class="active"><a href="#">新增增值税发票信息</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiVatInvoiceInfo" action="${ctx}/psi/psiVatInvoiceInfo/save" method="post" class="form-horizontal" enctype="multipart/form-data">
		
		<div class="control-group">
			<label class="control-label">发票号码:</label>
			<div class="controls">
				<input  type="text"  style="width:200px"   class="required"  name="invoiceNo" value="${psiVatInvoiceInfo.invoiceNo}" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">开票时间:</label>
			<div class="controls">
				<input  type="text"  style="width:200px"   class="Wdate required"  name="invoiceDate"   id="invoiceDate" value="<fmt:formatDate value="${psiVatInvoiceInfo.invoiceDate}" pattern="yyyy-MM-dd" />" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">供应商:</label>
			<div class="controls">
				<select  id="supplier" name="supplier.id" class="required">
					<option value="">请选择</option>
					<c:forEach items="${suppliers}" var="supplier" varStatus="i">
						 <option value='${supplier.id}' ${psiVatInvoiceInfo.supplier.id eq supplier.id ?'selected' :''}>${supplier.nikename}</option>;
					</c:forEach>
				</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">产品:</label>
			<div class="controls">
			<select name="productName"    id="product" class="required">
				<c:forEach items="${products}" var="productName">
					<option value="${productName}" ${psiVatInvoiceInfo.productName eq productName ?'selected':''}  >${productName}</option>
				</c:forEach>	
			</select>
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label">数量:</label>
			<div class="controls" >
				<input  type="text"  style="width:200px"   class="required number"  name="quantity" value="${psiVatInvoiceInfo.quantity}" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">总金额(含税):</label>
			<div class="controls">
				<input  type="text"  style="width:200px"   class="required price"  name="totalAmount" value="${psiVatInvoiceInfo.totalAmount}" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<input  type="text"  style="width:200px"  name="remark" value="${psiVatInvoiceInfo.remark}" />
			</div>
		</div>
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
