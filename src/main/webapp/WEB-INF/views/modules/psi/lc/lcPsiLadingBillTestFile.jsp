<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>质检单填写</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript" src="${ctxStatic}/ckeditor/ckeditor.js"></script>
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
			if(!(top)){
				top = self; 
			}
			
			$('#myfileupload').MultiFile({
				max : 3,
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
					$("#btnSureSubmit").attr("disabled","disabled");
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
		<li><a href="${ctx}/psi/lcPsiLadingBill/">收货单列表</a></li>
		<li class="active"><a href="#">品检信息填写</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="test" action="${ctx}/psi/lcPsiLadingBill/qualityTestFileSave" method="post" class="form-horizontal" enctype="multipart/form-data">
	    <input type='hidden' name="id" value="${test.id}">
	    <input type='hidden' name="ladingId" value="${test.ladingId}">
	    <input type='hidden' name="ladingBillNo" value="${test.ladingBillNo}">
	    <input type='hidden' name="productName" value="${test.productName}">
	     <input type='hidden' name="supplierId" value="${test.supplierId}">
	    <input type='hidden' name="color" value="${test.color}">
	    <input type='hidden' class="receivedQuantity" value="${test.receivedQuantity}">
	     <input type="hidden" name="testSta" 	value="0" id="toReview" />
	       
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:33%">
				<label class="control-label"><b>提单号</b>:</label>
				<div class="controls" >
				<span>
				<input type="text" readonly   value="${test.ladingBillNo}" />
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:33%" >
				<label class="control-label"><b>产品</b>:</label>
				<div class="controls" >
				<span>
					<input type="text" readonly   value="${test.productName}"/>
				</span>
				
				</div>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>AQL</b>:</label>
			<div class="controls" >
			<span>
				<textarea rows="2" cols="2" name="aql" style="width:95%" readonly >${test.aql}</textarea>
			</span>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>内观</b>:</label>
			<div class="controls" >
			<span>
			<textarea rows="2" cols="2" name="inView" style="width:95%" readonly >${test.inView}</textarea>
			</span>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>功能</b>:</label>
			<div class="controls" >
			<span>
			<textarea rows="2" cols="2" name="function" style="width:95%" readonly >${test.function}</textarea>
			</span>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>外观</b>:</label>
			<div class="controls" >
			<span>
			<textarea rows="2" cols="2" name="outView" style="width:95%"  readonly>${test.outView}</textarea>
			</span>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>包装</b>:</label>
			<div class="controls" >
			<span>
			<textarea rows="2" cols="2" name="packing" style="width:95%" readonly >${test.packing}</textarea>
			</span>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>检验结果判定</b>:</label>
			<div class="controls" >
			<span>
				<c:if test="${test.isOk eq '0'}">不合格</c:if> 
				<c:if test="${test.isOk eq '1'}">合格</c:if> 
				<c:if test="${test.isOk eq '2'}">部分合格</c:if> 
			</span>
			</div>
		</div>
		
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:33%">
				<label class="control-label"><b>抽样数</b>:</label>
				<div class="controls" >
				<span>
				<input type="text"  name="testQuantity" style="width:200px" class="number" value="${test.reason}" readonly/>
				</span>
				</div>
			</div>
			<div class="control-group" style="float:left;width:33%">
				<label class="control-label"><b>订单数</b>:</label>
				<div class="controls" >
				<span>
					<input type="text"  name="totalQuantity" style="width:200px" class="number" readonly value="${not empty test.totalQuantity?test.totalQuantity:totalQuantity}" />
				</span>
				</div>
			</div>
			<div class="control-group" style="float:left;width:33%">
				<label class="control-label"><b>合格数(<span style='color:red'>部分合格时必填</span>)</b>:</label>
				<div class="controls" >
				<span>
				<input type="text"  name="okQuantity" style="width:200px" class="number" value="${test.okQuantity}" readonly/>
				</span>
				</div>
			</div>
		</div>
		
		
		<div class="control-group" style="float:left;width:100%">
		<label class="control-label"><b>品检报告上传</b>:</label>
			<div class="controls">
				<span class="help-inline">支持多附件</span>
				<span class="label label-warning" style="font-size: 18px;">No Chinese or space in the file name</span>
				<input name="reportPath" type="file" id="myfileupload" />
			</div>
		</div>
	
		<div class="form-actions" style="float:left;width:100%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存附件"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
