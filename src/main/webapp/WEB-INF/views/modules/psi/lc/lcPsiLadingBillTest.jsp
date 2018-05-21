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
			
			$('#myfileupload1').MultiFile({
				max : 3,
				STRING : {
					remove : "[Delete]",
					selected : 'Selecionado: $file',
					denied : '不支持上传 $ext 格式的文件!',
					duplicate : '文件已经在上传列表中(is Exist): $file'
				}
			});
			
			$("input[name=isOk]").click(function(){
				if($(this).is(":checked")){
					if($(this).val()=="1"){
						$("input[name=dealWay]").each(function(){
							$(this).attr("disabled","disabled");
						});
					}else{
						$("input[name=dealWay]").each(function(){
							$(this).removeAttr("disabled");  
						});
					}
					$(this).attr("checked",true).siblings().attr("checked",false);
				}else{
					$(this).attr("checked",false).siblings().attr("checked",false);
				}
			});
			
			if('${test.isOk}'=='1'){
				$("input[name=dealWay]").each(function(){
					$(this).attr("disabled","disabled");
				});
			}
			
			$("input[name=dealWay]").click(function(){
				if($(this).is(":checked")){
					$(this).attr("checked",true).siblings().attr("checked",false);
				}else{
					$(this).attr("checked",false).siblings().attr("checked",false);
				}
			});
			
			
			$("#btnSureSubmit").on('click',function(e){
				
				 if($("#inputForm").valid()){
					 
					 if($("input[name='okQuantity']").val()&&(parseInt($("input[name='okQuantity']").val())>parseInt($("input[name='totalQuantity']").val()))){
							top.$.jBox.tip("合格数不能大于订单数","info",{timeout:3000});
							return false;
						}
						
						var totalQ=$(".totalQuantity").val();
						if(parseInt($("input[name='totalQuantity']").val())>parseInt(totalQ)){
							top.$.jBox.tip("订单数不能大于"+totalQ,"info",{timeout:3000});
							return false;
						}
						
						var isOk="";
						
						$("input[name=isOk]").each(function(){
							if($(this).is(":checked")){
								isOk=$(this).val();
								return false;
							}
						});
						
						if(isOk==2){
							if($("input[name='okQuantity']").val()==''){
								top.$.jBox.tip("部分合格必须填写合格数！","info",{timeout:3000});
								return false;
							}
						}
						
					 top.$.jBox.confirm('确认要申请审核？申请后将发送邮件通知品质主管进行审核！','系统提示',function(v,h,f){
							if(v=='ok'){
								$("#toReview").val("3");
								$("#inputForm").submit();
								$("#btnSubmit").attr("disabled","disabled");
								$("#btnSureSubmit").attr("disabled","disabled");
							}
							return true;
							},{buttonsFocus:1,persistent: true});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				 }else{
					 return false;
				 };
				
			});	
			
			
			$("#inputForm").validate({
				submitHandler: function(form){
					if($("input[name='okQuantity']").val()&&(parseInt($("input[name='okQuantity']").val())>parseInt($("input[name='totalQuantity']").val()))){
						top.$.jBox.tip("合格数不能大于订单数","info",{timeout:3000});
						return false;
					}
					
					var totalQ=$(".totalQuantity").val();
					if(parseInt($("input[name='totalQuantity']").val())>parseInt(totalQ)){
						top.$.jBox.tip("订单数不能大于"+totalQ,"info",{timeout:3000});
						return false;
					}
					
					var isOk="";
					
					$("input[name=isOk]").each(function(){
						if($(this).is(":checked")){
							isOk=$(this).val();
							return false;
						}
					});
					
					if(isOk==2){
						if($("input[name='okQuantity']").val()==''){
							top.$.jBox.tip("部分合格必须填写合格数！","info",{timeout:3000});
							return false;
						}
					}
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
	
	<form:form id="inputForm" modelAttribute="test" action="${ctx}/psi/lcPsiLadingBill/qualityTestSave" method="post" class="form-horizontal" enctype="multipart/form-data">
	    <input type='hidden' name="id" value="${test.id}">
	    <input type='hidden' name="ladingId" value="${test.ladingId}">
	    <input type='hidden' name="ladingBillNo" value="${test.ladingBillNo}">
	    <input type='hidden' name="productName" value="${test.productName}">
	     <input type='hidden' name="supplierId" value="${test.supplierId}">
	    <input type='hidden' name="color" value="${test.color}">
	    <input type='hidden' class="totalQuantity" value="${totalQuantity+test.totalQuantity}">
	     <input type="hidden" name="testSta" 	value="0" id="toReview" />
	       <div style="float:left;width:87%;height:30px" class="alert alert-info">1，"订单数"为本次“质检数”，如果本次只质检了一部分，请修改该数量！！！
	       <br/>2，如果选择“部分合格”，会拆成一个“合格”单（数量为所填写的合格数），一个“不合格”单（数量为订单数-合格数），“不合格处理方式”会继承到不合格单里面</div>
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
				<textarea rows="2" cols="2" name="aql" style="width:95%" >${test.aql}</textarea>
			</span>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>内观</b>:</label>
			<div class="controls" >
			<span>
			<textarea rows="2" cols="2" name="inView" style="width:95%" >${test.inView}</textarea>
			</span>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>功能</b>:</label>
			<div class="controls" >
			<span>
			<textarea rows="2" cols="2" name="function" style="width:95%" >${test.function}</textarea>
			</span>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>外观</b>:</label>
			<div class="controls" >
			<span>
			<textarea rows="2" cols="2" name="outView" style="width:95%" >${test.outView}</textarea>
			</span>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>包装</b>:</label>
			<div class="controls" >
			<span>
			<textarea rows="2" cols="2" name="packing" style="width:95%" >${test.packing}</textarea>
			</span>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>检验结果判定</b>:</label>
			<div class="controls" >
			<span>
			<c:choose>
				<c:when test="${canEdit eq '0' }">
					<input name="isOk" type="hidden" value="${test.isOk}"  />
					<c:if test="${test.isOk eq '1'}">合格</c:if>
					<c:if test="${test.isOk eq '0'}">不合格</c:if>
					<c:if test="${test.isOk eq '2'}">部分合格</c:if>
				</c:when>
				<c:otherwise>
				合格<input name="isOk" type="checkBox" value="1" ${test.isOk eq '1'?'checked':''} required="required"/> 
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;不合格<input name="isOk" type="checkBox" value="0" ${test.isOk eq '0'?'checked':''}  required="required"/>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;部分合格<input name="isOk" type="checkBox" value="2" ${test.isOk eq '2'?'checked':''}  required="required"/>
				</c:otherwise>
			</c:choose>
			</span>
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%">
			<label class="control-label"><b>不合格处理方式</b>:</label>
			<div class="controls" >
			<span>
			<c:choose>
				<c:when test="${canEdit eq '0' }">
					<input name="dealWay" type="hidden" value="${test.dealWay}"  />
					<c:if test="${test.isOk ne '1'}">
						${test.dealWay eq '2'?'直接返工':'各方协商'}
					</c:if>
				</c:when>
				<c:otherwise>
					各方协商<input name="dealWay" ${test.dealWay eq ''?'checked':''} type="checkBox" value=""  required="required"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					直接返工<input name="dealWay" ${test.dealWay eq '2'?'checked':''} type="checkBox" value="2"   required="required"/>
				</c:otherwise>
			</c:choose>
			
			</span>
			</div>
		</div>
		
		<div style="float:left;width:100%;">
			<div class="control-group" style="float:left;width:33%">
				<label class="control-label"><b>抽样数</b>:</label>
				<div class="controls" >
				<span>
				<input type="text"  name="testQuantity" style="width:200px" class="number" value="${test.reason}"/>
				</span>
				</div>
			</div>
			<div class="control-group" style="float:left;width:33%">
				<label class="control-label"><b>订单数</b>:</label>
				<div class="controls" >
				<span>
				<c:choose>
					<c:when test="${canEdit eq '0' }">
						<input name="totalQuantity" type="hidden" value="${test.totalQuantity}"  />
						${test.totalQuantity}
					</c:when>
					<c:otherwise>
						<input type="text"  name="totalQuantity" style="width:200px" class="number" value="${not empty test.totalQuantity?test.totalQuantity:totalQuantity}" required="required"/>
					</c:otherwise>
				</c:choose>
				</span>
				</div>
			</div>
			<div class="control-group" style="float:left;width:33%">
				<label class="control-label"><b>合格数(<span style='color:red'>部分合格时必填</span>)</b>:</label>
				<div class="controls" >
				<span>
				<c:choose>
					<c:when test="${canEdit eq '0' }">
						<input name="okQuantity" type="hidden" value="${test.okQuantity}"  />
						${test.okQuantity}
					</c:when>
					<c:otherwise>
						<input type="text"  name="okQuantity" style="width:200px" class="number" value="${test.okQuantity}"/>
					</c:otherwise>
				</c:choose>
				</span>
				</div>
			</div>
		</div>
		
		
		<div class="control-group" style="float:left;width:100%">
		<label class="control-label"><b>品检报告上传</b>:</label>
			<div class="controls">
				<span class="help-inline">支持多附件</span>
				<span class="label label-warning" style="font-size: 18px;">No Chinese or space in the file name</span>
				<input name="reportPath" type="file" id="myfileupload"/>
			</div>
		</div>
	
		<div class="form-actions" style="float:left;width:100%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保存预申请"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnSureSubmit" class="btn btn-primary" type="button" value="申请审核"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
