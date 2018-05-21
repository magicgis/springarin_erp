<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品优化记录</title>
	<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
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
			
			$("#product").change(function(){
				var optionStr  ="";
				var color =$(this).children("option:selected").attr("type");
				if(color==""){
					optionStr=optionStr+"<option value='' >无颜色</option>";
				}else{
					var arr = color.split(",");
					for(var i in arr){
						optionStr=optionStr+"<option value='"+arr[i]+"'  >"+arr[i]+"</option>";
					}
				}
				
				$("#color").select2().empty();
				$("#color").select2("data",[]);
				$("#color").append(optionStr);
			});
			
			$("#product").change();
			
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
		<li><a href="${ctx}/psi/productImprove/">产品优化信息记录</a></li>
		<li class="active"><a href="${ctx}/psi/productImprove/form?id=${productImprove.id}">产品优化信息添加</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiProductImprove" action="${ctx}/psi/productImprove/save" method="post" class="form-horizontal" >

		<div class="control-group">
			<label class="control-label">产品:</label>
			<div class="controls">
			<select name="productName"  style="width:43%"  id="product" class="required">
				<c:forEach items="${products}" var="product">
					<option value="${product.name}" type="${product.color}">${product.name}</option>
				</c:forEach>	
			</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">颜色:</label>
			<div class="controls">
			<select name="color"  multiple="multiple" style="width:43%"  id="color" class="multiSelect required"></select>
			</div>
		</div>
	
		
		<div class="control-group">
			<label class="control-label">订单号:</label>    
			<div class="controls">
				<input name="orderNo" type="text" value="${productImprove.orderNo}" style="width:42%"/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">优化内容:</label>
			<div class="controls">
				<form:textarea path="improveContent" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">优化时间</label>
			<div class="controls">
				 <input type="text" name="improveDate" style="width:42%" required="required" class="Wdate"  value="<fmt:formatDate value='${productImprove.improveDate}' pattern='yyyy-MM-dd' />" /> 
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">是否更换sku:</label>
			<div class="controls">
				<input type="radio" name="isChangeSku" value="0" required="required">否</input>
				<input type="radio" name="isChangeSku" value="1" required="required">是</input>
			</div>
		</div>
		
			
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
