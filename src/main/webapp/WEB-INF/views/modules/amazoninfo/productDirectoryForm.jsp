<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>添加产品目录</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#inputForm").validate({
				rules:{
					"url":{
						"required":true,
						 remote: {
						    url: "${ctx}/amazoninfo/productDirectory/isExistUrl",     //后台处理程序
						    type: "post",               //数据发送方式
						    dataType: "json",           //接受数据格式   
						    data: {                     //要传递的数据
						        "url": function() {
						            return $("input[name='url']").val();
						        },
						        "id": $("input[name='id']").val()
						    }
						}
					}
			
				},
				messages:{
					"url":{"remote":'此url已经存在，请重新填写!'}
				},
				submitHandler: function(form){
					
					if($("input[name='url']").val().indexOf("#")>0){
						top.$.jBox.tip("url中不能包含#号,请按要求填写",'info',{timeout:3000});
						return false;
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
		<li><a href="${ctx}/amazoninfo/productDirectory/">产品目录列表</a></li>
		<li class="active"><a href="#">${not empty productDirectory.id?'编辑':'新增'} 产品目录 </a></li>
	</ul>
	<div style="float:left;width:98%;margin:10px 0px 10px 0px;" class="alert alert-info"><strong>Tips:产品目录url,尽量不要有ref后的内容,不能包含分页符如：http:xxx/ref=xxx#1<br/>如果设置为冻结状态，则top100产品就是当天的，以后不会更新，评论和销量会照常扫描（为了加快扫描数据，建议扫描几天后就变成冻结状态！）</strong></div>
	<form:form id="inputForm" modelAttribute="productDirectory" action="${ctx}/amazoninfo/productDirectory/save" method="post" class="form-horizontal">
		<input type="hidden" name="id"			  value="${productDirectory.id}"/>
		<input type="hidden" name="directorySta"  value="${productDirectory.directorySta}"/>
		<input type="hidden" name="createUser.id" value="${productDirectory.createUser.id}"/>
		<input type="hidden" name="createDate"    value="<fmt:formatDate pattern='yyyy-MM-dd hh:mm:ss' value='${productDirectory.createDate}'/>"/>
		<input type="hidden" name="activeDate"    value="<fmt:formatDate pattern='yyyy-MM-dd' value='${productDirectory.activeDate}'/>"/>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:100%;">
			<label class="control-label" style="width:100px"><b>Subject:</b></label>
			<div class="controls" style="margin-left:120px;">
				<form:input path="subject" style="width:98%; height: 30px;" />
			</div>
		</div>
		</div>
		<div style="float:left;width:100%">
			<div class="control-group" style="float:left;width:100%;">
			<label class="control-label" style="width:100px"><b>Url:</b></label>
			<div class="controls" style="margin-left:120px;">
				<form:input path="url" style="width:98%; height: 30px;" />
			</div>
		</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%;">
			<label class="control-label" style="width:100px"><b>Remark:</b></label>
			<div class="controls" style="margin-left:120px;">
				<form:textarea path="remark" htmlEscape="false" rows="4" maxlength="200" style="width:98%; height: 60px;" />
			</div>
		</div>
		
		<div class="control-group" style="float:left;width:100%;">
			<label class="control-label" style="width:100px"><b>冻结状态:</b></label>
			<div class="controls" style="margin-left:120px;">
			<select name="lockSta" style="width:150px">
				<option value="0" ${productDirectory.lockSta eq '0'?'selected':''}>非冻结</option>
				<option value="1" ${productDirectory.lockSta eq '1'?'selected':''}>冻结</option>
			</select> 
			</div>
		</div>
		
		<div class="form-actions" style="float:left;width:100%">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="Save"/>&nbsp;&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn" type="button" value="Back" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>
