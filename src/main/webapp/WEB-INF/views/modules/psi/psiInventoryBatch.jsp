<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>入库管理管理</title>
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
					top.$.jBox.tip("请选择excel文件 ", 'info',{timeout:3000});
					$("#myDatafileupload").val("").focus();
				}
			});
			
			$("#inputForm").validate({
			submitHandler: function(form){
				top.$.jBox.confirm("<span style='color:red;font-weight:bold'>确认后将改变库存数量,操作将不可逆,您确定要提交入库单吗?</span>","系统提示",function(v,h,f){
				if(v=='ok'){
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
	});	
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#">Warehouse Batch Adjustment</a></li>
	</ul>
	
	<form:form id="inputForm" modelAttribute="psiInventoryIn" action="${ctx}/psi/psiInventoryIn/batchSave" method="post" class="form-horizontal" enctype="multipart/form-data" >
		<input type="hidden" name="warehouseId" value="${psiInventoryIn.warehouseId}" >
		
		<blockquote style="float:left;width:98%;">
			<p style="font-size: 14px">Base Info.</p>
		</blockquote>
		
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:30%;height:30px">
				<label class="control-label" style="width:100px"><b>WareHouse:</b></label>
				<div class="controls" style="margin-left:120px">
				 <input type="text" name="warehouseName" readonly="readonly" value="${psiInventoryIn.warehouseName}" />
				</div>
			</div>
		
			<div class="control-group" id="dataFile" style="float:left;width:35%;height:30px;">
				<label class="control-label" style="width:100px"><b>Data File:</b></label>
				<div class="controls" style="margin-left:120px">
				<input name="excelFile" type="file" id="myDatafileupload" class="required"/><span class="help-inline">(.xls/.xlsx)</span>
				</div>
			</div>
			
			<div class="control-group"  style="float:left;width:35%;height:30px;">
				<label class="control-label" style="width:100px"><b>OperationType:</b></label>
				<div class="controls" style="margin-left:120px">
				<input name="operationType" type="text" value="Batch leveling" readonly class="required"/>
				</div>
			</div>
			
		</div>
		<div style="float:left;width:100% ;">
		<div class="control-group" style="float:left;width:98%">
			<label class="control-label" style="width:100px">Remark:</label>
			<div class="controls" style="margin-left:120px">
				<form:textarea path="remark" htmlEscape="false" rows="4" maxlength="200" style="width:98%; height: 60px;"/>
			</div>
		</div>
		</div>
		
		<div class="form-actions" style="float:left;width:100%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="Save"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="Back" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
