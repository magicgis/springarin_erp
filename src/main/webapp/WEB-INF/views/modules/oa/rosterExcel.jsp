<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>花名册编辑</title>
	<meta name="decorator" content="default"/>
		<script type="text/javascript" src="${ctxStatic}/common/jquery.MultiFile.js"></script>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			
			
			
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
		<li><a href="${ctx}/oa/roster/">花名册列表</a></li>
		<li class="active"><a href="${ctx}/oa/roster/excel">花名册excel导入</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="roster" action="${ctx}/oa/roster/excelSave" method="post" class="form-horizontal" enctype="multipart/form-data">
		
		<div class="control-group" style="float:left;width:25%;height:25px;">
			<label class="control-label" style="width:100px"><b>数据文件:</b></label>
			<div class="controls" style="margin-left:120px" >
		      <input name="excelPath"  type="file" style="width:95%" />
			</div>
		</div>
		
		<div class="form-actions" style="float:left;width:98%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
