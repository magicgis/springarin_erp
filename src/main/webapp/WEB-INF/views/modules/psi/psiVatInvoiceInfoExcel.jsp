<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>增值税发票信息记录</title>
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

			
			$("#myDatafileupload").on("change",function(){
				var filePath=$("#myDatafileupload").val();
				var fileSuffix=filePath.substr(filePath.lastIndexOf(".")).toLowerCase();
				if(fileSuffix.indexOf(".")!=0||".xls,.xlsx,".indexOf(fileSuffix+",")==-1){
					top.$.jBox.tip("请选择excel文件 上传", 'info',{timeout:3000});
					$("#myDatafileupload").val("").focus();
				}
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
		<li><a href="${ctx}/psi/psiVatInvoiceInfo/">增值税发票信息管理</a></li>
		<li class="active"><a href="#">新增增值税发票信息</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiVatInvoiceInfo" action="${ctx}/psi/psiVatInvoiceInfo/excelSave" method="post" class="form-horizontal" enctype="multipart/form-data">
		<div class="control-group" id="dataFile" >
			<label class="control-label" style=""><b>请上传发票数据文件:</b></label>
			<div class="controls" >
			<input name="excelFile" type="file" id="myDatafileupload" class="required"/><span class="help-inline">(.xls/.xlsx)</span>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
