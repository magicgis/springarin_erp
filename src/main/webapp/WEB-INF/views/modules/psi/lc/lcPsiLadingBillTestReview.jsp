<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>不合格订单处理</title>
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
			
			$('#myfileupload1').MultiFile({
				max : 3,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			
			$("#btnSave1,#btnSave2,#btnSave3").on("click",function(){
				var remark =$(this).parent().parent().find(".remark").val();
				if(remark==""){
					top.$.jBox.tip("请先填写意见再点保存按钮！","info",{timeout:3000});
					return false;
				}
				
				var flag ="";
				var attrId=$(this).attr("id");
				if(attrId=="btnSave1"){
					flag="1";
				}else if(attrId=="btnSave2"){
					flag="2";
				}else if(attrId=="btnSave3"){
					flag="3";
				}
				//异步保存备注
				var param = {};
				param.remark = encodeURI(remark);
				param.id=$("#testId").val();
				param.flag=flag;
				$.post("${ctx}/psi/lcPsiLadingBill/qualityRemarkSave?"+$.param(param),function(data){
					if(!(data)){    
						top.$.jBox.tip("保存失败！", 'info',{timeout:2000});
					}else{
						top.$.jBox.tip("保存成功！", 'info',{timeout:2000});
					}
				});
				//把保存按钮拉黑
				$(this).parent().css("display","none");
				$(this).parent().parent().find(".remark").attr("readonly","readonly");
			});
			
			
			
			
			$("#inputForm").validate({
				submitHandler: function(form){
					
					var dealWay="";
					$("input[name=dealWay]").each(function(){
						if($(this).is(":checked")){
							dealWay=$(this).val();
							return false;
						}
					});
					
					if(dealWay==0){
						//如果是特采 只要有一个保存按钮是亮着的，就不让保存
						var display="";
						$(".remarkSave").each(function(){
							if($(this).css("display")=="block"){
								display="block";
								return false;
							}
						});
						
						if(display=="block"){
							top.$.jBox.tip("特采要各方都填写了意见才可以保存！","info",{timeout:3000});
							return false;
						}
					}
					
					
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
		<li ><a href="${ctx}/psi/psiLadingBill/">收货单列表</a></li>
		<li ><a href="${ctx}/psi/lcPsiLadingBill/">(理诚)收货单列表</a></li>
		<li ><a href="${ctx}/psi/lcPsiLadingBill/testList">不合格质检列表</a></li>
		<li class="active"><a >不合格质检处理</a></li>
	</ul>
	<form:form id="inputForm" modelAttribute="test" action="${ctx}/psi/lcPsiLadingBill/qualityTestReviewSave" method="post" class="form-horizontal" enctype="multipart/form-data">
	    <input type='hidden' name="id" value="${test.id}" id="testId">
	    
	    <blockquote style="float:left;width:100%;">
			<p style="font-size: 14px">质检单信息：</p>
		</blockquote>
		
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:20%">
				<label class="control-label" style="width:80px"><b>提单号</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					${test.ladingBillNo}
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:20%" >
				<label class="control-label" style="width:80px"><b>产品</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					${test.productNameColor}
				</span>
				
				</div>
			</div>
			<div class="control-group"  style="float:left;width:20%" >
				<label class="control-label" style="width:80px"><b>品检人</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					${test.createUser.name}
				</span>
				
				</div>
			</div>
			<div class="control-group"  style="float:left;width:20%" >
				<label class="control-label" style="width:80px"><b>品检日期</b>:</label>
				<div class="controls" style="margin-left:100px">
					<span>
						<fmt:formatDate value="${test.createDate}" pattern="yyyy-MM-dd"/>
					</span>
				</div>
			</div>
				<div class="control-group"  style="float:left;width:20%" >
				<label class="control-label" style="width:80px"><b>品检报告</b>:</label>
				<div class="controls" style="margin-left:100px">
					<span>
						<c:forEach items="${fn:split(test.reportFile,',')}" var="attchment">
						<a href="${ctx}/psi/lcPsiLadingBill/download?fileName=/${attchment}">报告${i.index+1}</a>
						&nbsp;&nbsp;&nbsp;  
					</c:forEach>
					</span>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:50%">
				<label class="control-label" style="width:80px"><b>AQL</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					${test.aql}
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:50%" >
				<label class="control-label" style="width:80px"><b>功能</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					${test.function}
				</span>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:50%">
				<label class="control-label" style="width:80px"><b>内观</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					${test.inView}
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:50%" >
				<label class="control-label" style="width:80px"><b>外观</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					${test.outView}
				</span>
				</div>
			</div>
		</div>
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:50%">
				<label class="control-label" style="width:80px"><b>包装</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					${test.packing}
				</span>
				</div>
			</div>
			<div class="control-group"  style="float:left;width:50%" >
				<label class="control-label" style="width:80px"><b>数量</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>
					${test.totalQuantity}
				</span>
				</div>
			</div>
		</div>
		
		
		<blockquote style="float:left;width:100%;">
			<p style="font-size: 14px">各方处理意见：</p>
		</blockquote>
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:70%;height:100px">
				<label class="control-label" style="width:80px"><b>采购意见</b>:</label>
				<div class="controls" style="margin-left:100px">
					<span>
						<textarea rows="4" cols="5" name="reviewRemark1" ${not empty test.reviewRemark1?'readonly':''} class="remark" style="width:90%">${test.reviewRemark1}</textarea>
					</span>
				</div>
			</div>
			<div class="control-group remarkSave" style="float:left;width:30%;height:100px;display:${not empty test.reviewRemark1?'none':''}" >
				<input id="btnSave1" class="btn btn-primary" type="button" value="保存采购部意见"/>
			</div>
		</div>
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:70%;height:100px">
				<label class="control-label" style="width:80px"><b>产品意见</b>:</label>
				<div class="controls" style="margin-left:100px">
					<span>
						<textarea rows="4" cols="5" name="reviewRemark2" ${not empty test.reviewRemark2?'readonly':''} class="remark" style="width:90%">${test.reviewRemark2}</textarea>
					</span>
				</div>
			</div>
			<div class="control-group remarkSave" style="float:left;width:30%;height:100px;display:${not empty test.reviewRemark2?'none':''}">
				<input id="btnSave2" class="btn btn-primary" type="button" value="保存产品部意见"/>
			</div>
		</div>
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:70%;height:100px">
				<label class="control-label" style="width:80px"><b>品检意见</b>:</label>
				<div class="controls" style="margin-left:100px">
					<span>
						<textarea rows="4" cols="5" name="reviewRemark3" ${not empty test.reviewRemark3?'readonly':''} class="remark"  style="width:90%;">${test.reviewRemark3}</textarea>
					</span>
				</div>
			</div>
			<div class="control-group remarkSave" style="float:left;width:30%;height:100px;display:${not empty test.reviewRemark3?'none':''}">
				<input id="btnSave3" class="btn btn-primary" type="button" value="保存品检意见"/>
			</div>
		</div>
		
		<shiro:hasPermission name="psi:ladingBill:managerTest">
			<blockquote style="float:left;width:100%;">
				<p style="font-size: 14px">处理结果：</p>
			</blockquote>
			
			
			<div class="control-group" style="float:left;width:100%">
				<label class="control-label"  style="width:80px"><b>处理方式</b>:</label>
				<div class="controls" style="margin-left:100px">
				<span>${test.dealWay}
				&nbsp;&nbsp;特采<input name="dealWay"  type="radio" value="0" ${test.dealWay eq '0'?'checked':''}  required="required"/>
				&nbsp;&nbsp;返工<input name="dealWay"  type="radio" value="2" ${test.dealWay eq '2'?'checked':''}   required="required"/>
				</span>
				</div>
			</div>
			
			<div style="float:left;width:100%;">
				<div class="control-group" style="float:left;width:90%;height:100px">
					<label class="control-label" style="width:80px"><b>原因</b>:</label>
					<div class="controls" style="margin-left:100px">
						<span>
							<textarea rows="4" cols="5" name="reviewRemark"  style="width:90%;">${test.reviewRemark}</textarea>
						</span>
					</div>
				</div>
			</div>
			
			
			<div class="control-group" style="float:left;width:100%">
			<label class="control-label"  style="width:80px"><b>供应商特采<br/>申请单</b>:</label>
				<div class="controls" style="margin-left:100px">
					<span class="help-inline">支持多附件</span>
					<span class="label label-warning" style="font-size: 18px;">No Chinese or space in the file name</span>
					<input name="giveInPath" type="file" id="myfileupload1"/>
				</div>
			</div>
						
			<c:if test="${empty test.dealWay}">
				<div class="form-actions" style="float:left;width:100%">
					<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存处理结果"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</div>
			</c:if>
		</shiro:hasPermission>
	</form:form>
</body>
</html>
