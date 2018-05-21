<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>新增流程</title>
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
			$("#inputForm").validate();
			
			$("#pdfFileupload").on("change",function(){
				var shipmentId = "";
				//<c:forEach items="${fn:split(psiInventoryOut.tranFbaNo,',')}" var="shipmentId">
					shipmentId = '${shipmentId}';
				//</c:forEach>
				var filePath=$("#zipFileupload").val();
				var fileSuffix=filePath.substr(filePath.lastIndexOf(".")).toLowerCase();
				if(fileSuffix.indexOf(".")!=0||".zip,".indexOf(fileSuffix+",")==-1){
					top.$.jBox.tip("please select a pdf file", 'info',{timeout:2000});
					$("#pdfFileupload").val("").focus();
				}
			});
		});
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/workflow/processList">流程管理</a></li>
		<li class="active"><a href="${ctx}/sys/workflow/form">添加流程</a></li>
	</ul>
	<div class="alert alert-danger" id="showError" style="float:left;width:98%;height:15px;margin-left:20px;display:none"></div>
	<form:form id="inputForm" name="inputForm" modelAttribute="psiInventoryOut" action="${ctx}/sys/workflow/deploy" method="post" class="form-horizontal" enctype="multipart/form-data">
		<div class="control-group">
			<label class="control-label" style="width:100px"><b>ZIP File：</b></label>
			<div class="controls" style="margin-left:120px">
				<input name="file" type="file" id="zipFileupload" class="required"/>
			</div>
		</div>
		
		<div class="form-actions" style="float:left;width:100%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="Upload"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="Back" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
