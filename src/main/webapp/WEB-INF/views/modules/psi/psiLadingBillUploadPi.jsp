<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>收货单上传pi</title>
	<meta name="decorator" content="default"/>
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
			
			
			$('#myfileupload').MultiFile({
				max : 5,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
		
			$("#inputForm").validate({
				submitHandler: function(form){
					if($(".MultiFile-title").size()==0){
						top.$.jBox.tip("凭证不能为空！", 'info',{timeout:3000});
						$("input[type='file']").removeAttr("disabled");
						return false;  
					}else{
						$("#contentTable tbody tr").each(function(i,j){
							$(j).find("select").each(function(){
								if($(this).attr("name")){
									$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
								}
							});
							$(j).find("input[type!='']").each(function(){
								if($(this).attr("name")){
									$(this).attr("name","items"+"["+i+"]."+$(this).attr("name"));
								}
							});
						});
						
						
						top.$.jBox.confirm('您确定要提交吗','系统提示',function(v,h,f){
							if(v=='ok'){
								form.submit();
								$("#btnSubmit").attr("disabled","disabled");
							}
							},{buttonsFocus:1,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
						
					}
					
					
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
		
		
		function goEdit(ladingId){
			var params = {};
			params.id = ladingId;
			window.location.href = "${ctx}/psi/psiLadingBill/edit?"+$.param(params);
		}
		
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/psi/psiLadingBill/">收货单列表</a></li>
		<li class="active"><a href="#">上传收货单PI</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiLadingBill" action="${ctx}/psi/psiLadingBill/uploadPiSave" method="post" class="form-horizontal" enctype="multipart/form-data">
	    <input type='hidden' name="id" value="${psiLadingBill.id}">
	    <blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		
			<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:30%">
				<label class="control-label"><b>供应商</b>:</label>
				<div class="controls" >
				<span>
				<input type="text" readonly   value="${psiLadingBill.supplier.nikename}"/>
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:70%" >
				<label class="control-label"><b>承运商</b>:</label>
				<div class="controls" >
				<span>
					<input type="text" readonly   value="${psiLadingBill.tranSupplier.nikename}"/>
				</span>
				
				</div>
			</div>
			</div>
			
		<blockquote  style="float:left;">
			<p style="font-size: 14px">收货单项信息</p>
		</blockquote>
		
		<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				   <th style="width: 20%">产品类型</th>
				   <th style="width: 20%">订单号</th>
				   <th style="width: 10%">收货单数量</th>
				   <th style="width: 34%">备注</th>
				   
			</tr>
			<c:forEach items="${psiLadingBill.items}" var="item">
			<tr>
			<td> <input type='text' readOnly style='width:90%'value="${item.productName}|${item.countryStr}|${item.colorCode}"/></td>
			<td> <input type='text' readOnly style='width:90%'value="${item.purchaseOrderItem.purchaseOrder.orderNo}"/></td>
			<td> <input type='text' readOnly style='width:90%'value="${item.remark}"/></td>
			<td> <input type='text' readOnly style='width:90%'value="${item.quantityLading}"/></td>
			</tr>
			</c:forEach>
		</thead>
	</table>
		<blockquote>
			<p style="font-size: 14px">凭证</p>
		</blockquote>
		
		<div class="control-group">
			<div class="controls">
				<span class="help-inline">支持多附件</span>
				<span class="label label-warning" style="font-size: 18px;">No Chinese or space in the file name</span>
				<input name="attchmentFile" type="file" id="myfileupload" />
			</div>
		</div>
					
	
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="确认"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
