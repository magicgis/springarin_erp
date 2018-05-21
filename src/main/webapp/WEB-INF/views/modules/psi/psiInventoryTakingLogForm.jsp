<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>登记盘点记录</title>
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
					loading('正在提交，请稍等...');
					
					var filePath="";
					$(".icon-remove").each(function(){
						if($(this).parent().css("display")!='none'){
							filePath=filePath+$(this).attr("type")+",";
						};
					});
					
					$("input[name='filePath']").val(filePath.substr(0,filePath.length-1));
					
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
		<li><a href="${ctx}/psi/psiInventoryTakingLog/">人工盘点记录</a></li>
		<li class="active"><a href="#">新增盘点记录</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiInventoryTakingLog" action="${ctx}/psi/psiInventoryTakingLog/save" method="post" class="form-horizontal" enctype="multipart/form-data">
		
		<div class="control-group">
			<label class="control-label">盘点时间:</label>
			<div class="controls">
				<input  type="text"  style="width:200px"   class="Wdate required"  name="purchaseDate"   id="purchaseDate" value="<fmt:formatDate value="${psiInventoryTakingLog.takingDate}" pattern="yyyy-MM-dd" />" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">结果:</label>
			<div class="controls">
				<form:textarea path="result" htmlEscape="false" rows="4" maxlength="200" style="width:98%" class="input-xxlarge" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">备注:</label>
			<div class="controls">
				<form:textarea path="remark" htmlEscape="false" rows="4" maxlength="200" style="width:98%" class="input-xxlarge" />
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
