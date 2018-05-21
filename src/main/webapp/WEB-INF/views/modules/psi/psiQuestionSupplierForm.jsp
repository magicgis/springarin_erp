<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>供应商异常记录</title>
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
			
			eval('var productMap=${productMap}');
			$(".icon-remove").on("click",function(){
				$(this).parent().css("display","none");
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
			$(".Wdate").on("click", function (){
				 WdatePicker({ dateFormat: "yyyy-MM-dd", changeMonth: true, changeYear: true });
			});
			
			$("#supplier").change(function(){
				var supplierId = $(this).val();
				var arr=productMap[supplierId];
				var optionStr="";
				for(var i=0;i<arr.length;i++){
					optionStr=optionStr+"<option value='"+arr[i]+"'>"+arr[i]+"</option>";
				}
				$("#product").select2().empty();
				$("#product").select2("data",[]);
				$("#product").append(optionStr);
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
		<li><a href="${ctx}/psi/psiQuestionSupplier/">供应商异常信息记录</a></li>
		<li class="active"><a href="${ctx}/psi/psiQuestionSupplier/form?id=${psiQuestionSupplier.id}">供应商异常信息${not empty psiQuestionSupplier.id?'编辑':'添加'}</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="psiQuestionSupplier" action="${ctx}/psi/psiQuestionSupplier/save" method="post" class="form-horizontal" enctype="multipart/form-data">
		<form:hidden path="id"/>
		<form:hidden path="delFlag"/>
		<form:hidden path="createUser.id" />
		<form:hidden path="updateUser.id" />
		<input type="hidden" name="createDate" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiQuestionSupplier.createDate}'/>"/>
		<input type="hidden" name="updateDate" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${psiQuestionSupplier.updateDate}'/>"/>
		<form:hidden path="filePath" id="filePath"/>
		<form:hidden path="result" id="result" value="${psiQuestionSupplier.result }"/>
		<tags:message content="${message}"/>
		
		<div class="control-group">
			<label class="control-label">供应商:</label>
			<div class="controls">
			<form:select path="supplier.id" style="width:43%" id="supplier" >
				<option value=""></option>
				<c:forEach items="${suppliers}" var="supplier">
					<option value="${supplier.id}" ${supplier.id eq psiQuestionSupplier.supplier.id ?'selected':''}  >${supplier.nikename}</option>
				</c:forEach>	
			</form:select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">质检结果:</label>
			<div class="controls">
			<select name="isPass" id="isPass" style="width:43%" >
				<option value=""></option>
				<option value="0" ${isPass eq '0'?'selected':''}  >不合格</option>
				<option value="1" ${isPass eq '1'?'selected':''}  >合格</option>
			</select>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">产品:</label>
			<div class="controls">
			<select name="productName" multiple  style="width:43%" class="multiSelect" id="product">
				<c:forEach items="${products}" var="productName">
					<option value="${productName}" ${fn:contains(psiQuestionSupplier.productName,productName) ?'selected':''}  >${productName}</option>
				</c:forEach>	
			</select>
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label">出错时间</label>
			<div class="controls">
				 <input type="text" name="questionDate" style="width:42%" required="required" class="Wdate" value="<fmt:formatDate value="${psiQuestionSupplier.questionDate}" pattern="yyyy-MM-dd" />" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">问题类型:</label>
			<div class="controls">
			<select name="questionType" multiple style="width:43%" class="multiSelect" >
				<option value="产品" ${fn:contains(psiQuestionSupplier.questionType,'产品')?'selected':''}>产品</option>
				<option value="交期" ${fn:contains(psiQuestionSupplier.questionType,'交期')?'selected':''}>交期</option>
				<option value="条码" ${fn:contains(psiQuestionSupplier.questionType,'条码')?'selected':''}>条码</option>
				<option value="包装" ${fn:contains(psiQuestionSupplier.questionType,'包装')?'selected':''}>包装</option>
				<option value="认证" ${fn:contains(psiQuestionSupplier.questionType,'认证')?'selected':''}>认证</option>
				<option value="其他" ${fn:contains(psiQuestionSupplier.questionType,'其他')?'selected':''}>其他</option>
			</select>
			</div>
		</div>
		
		
		<div class="control-group">
			<label class="control-label">采购批次:</label>    
			<div class="controls">
				<input name="orderNo" type="text" value="${psiQuestionSupplier.orderNo}" style="width:42%"/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">事件:</label>
			<div class="controls">
				<form:textarea path="event" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">后果:</label>
			<div class="controls">
				<form:textarea path="consequence" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">处理:</label>
			<div class="controls">
				<form:textarea path="deal" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge" />
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">赔偿/处罚:</label>
			<div class="controls">
				<form:textarea path="punishment" htmlEscape="false" rows="4" maxlength="200" class="input-xxlarge" />
			</div>
		</div>
		
		<c:if test="${not empty psiQuestionSupplier.filePath}">
		<div class="control-group" >
		<label class="control-label" >已上传附件:</label>
			<div class="controls">
				<c:forEach items="${fn:split(psiQuestionSupplier.filePath,',')}" var="attchment" varStatus="i">
					<span><a target="_blank" href="<c:url value='/data/site/${attchment}'/>">附件 ${i.index+1}</a><span class="icon-remove" type="${attchment}"></span></span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				</c:forEach> 
			</div>  
		</div>
		</c:if>
		
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
