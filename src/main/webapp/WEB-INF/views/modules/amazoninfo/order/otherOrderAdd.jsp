<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>track number</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
		
		if(!(top)){
			top = self; 
		}
		$(document).ready(function() {
			$("#inputForm").validate({
				submitHandler: function(form){
					var filepath = $("#excel").val();
					var extStart = filepath.lastIndexOf(".");

					var ext = filepath.substring(extStart, filepath.length).toUpperCase();
					if (ext != ".XLS" && ext != ".XLSX") {
						alert("只支持xls和xlsx格式文件!");
						return;
					}
					top.$.jBox.confirm('确定上传文件!','提示',function(v,h,f){
						if(v=='ok'){
							loading('loading...');
							form.submit();
							$("#btnSubmit").attr("disabled","disabled");
						}
					},{buttonsFocus:1,persistent: true});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("<spring:message code='amazon_custom_email_tip48'/>。");
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
      <li><a href="${ctx}/amazoninfo/unlineOrder/">Unline Order List</a></li>	
      <li  class="active"><a href="#">订单上传</a></li>	
   </ul>
	
	<form id="inputForm"  action="${ctx}/amazoninfo/unlineOrder/readOrderFile" enctype="multipart/form-data" method="post" class="form-horizontal" >
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label"><strong>订单模板:</strong></label>
			<div class="controls">
			   <a href="${ctx}/amazoninfo/unlineOrder/downloadTemplate">下载</a>
			</div>
		</div>
		
		<div class="control-group">
						<label class="control-label"><b>本地仓库</b>:</label>
						<div class="controls">
							<select id="stockId" name="stockId" style="width: 200px" class="required">
							    <option value="120">American</option>
							    <option value="19">Germany</option>
							    <option value="130">China_LC</option>
							</select>
						</div>
		</div>
							
		<div class="control-group">
			<label class="control-label"><strong>订单文件:</strong></label>
			<div class="controls">
				<input type="file" name="excel"  id="excel"  class="required"/> 
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-info" type="submit" value="保存"/>
			<input id="btnCancel" class="btn" type="button" value="Back" onclick="history.go(-1)"/>
		</div>
	</form>
</body>
</html>
