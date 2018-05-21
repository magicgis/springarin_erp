<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>固定资产编辑</title>
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
			
			$(".Wdate").live("click", function (){
				 WdatePicker({ dateFmt: "yyyy-MM-dd"});
			});
			
			$("#ownerId").on("change",function(){
				var  value= $(this).val();
				if(value=="1"){
					$("#ownerUserIdDiv").css("display","block");
					$("#ownerOfficeIdDiv").css("display","none");
				}else if(value=="2"){
					$("#ownerOfficeIdDiv").css("display","block");
					$("#ownerUserIdDiv").css("display","none");
				}else{
					$("#ownerOfficeIdDiv").css("display","none");
					$("#ownerUserIdDiv").css("display","none");
				}
				
			})
			
			
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
		<li><a href="${ctx}/oa/fixedAssets/">固定资产列表</a></li>
		<li class="active"><a href="${ctx}/oa/fixedAssets/form?id=${fixedAssets.id}">固定资产${not empty fixedAssets.id?'编辑':'添加'}</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="fixedAssets" action="${ctx}/oa/fixedAssets/save" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input type="hidden" name="id"                      value="${fixedAssets.id}"/>
		<input type="hidden" name="delFlag"                 value="${fixedAssets.delFlag}"/>
		<input type="hidden" name="createUser.id" 			value="${fixedAssets.createUser.id}"/>
		<input type="hidden" name="updateUser.id" 			value="${fixedAssets.updateUser.id}"/>
		<input type="hidden" name="createDate" 				value="<fmt:formatDate pattern='yyyy-MM-dd' value='${fixedAssets.createDate}'/>"/>
		<input type="hidden" name="updateDate" 				value="<fmt:formatDate pattern='yyyy-MM-dd' value='${fixedAssets.updateDate}'/>"/>
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>资产名称:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="name"  type="text" style="width:95%" value="${fixedAssets.name}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>型号:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="model"  type="text" style="width:95%" value="${fixedAssets.model}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>编号:</b></label>
				<div class="controls" style="margin-left:120px" >
				<input name="billNo"  type="text" style="width:95%" value="${fixedAssets.billNo}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>状态:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <select style="width: 100%" name="fixedSta" class="required">
			       		<option value="">请选择</option>
			       		<option value="使用" ${fixedAssets.fixedSta eq '使用'?'selected':''}>使用</option>
			       		<option value="闲置" ${fixedAssets.fixedSta eq '闲置'?'selected':''}>闲置</option>
			       		<option value="报废" ${fixedAssets.fixedSta eq '报废'?'selected':''}>报废</option>
				   </select>
				</div>
			</div>
				
		</div>
		
		<div style="float:left;width:98%">
		
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>存放地点:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="email"  type="text" style="width:95%" value="${fixedAssets.place}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>购买日期:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="buyDate" class="Wdate" type="text" style="width:95%" value="<fmt:formatDate pattern='yyyy-MM-dd' value='${fixedAssets.buyDate}'/>"/>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>保管方:</b></label>
				 <select style="width: 200px" name="owner" id="ownerId">
			       		<option value="0" ${fixedAssets.owner eq '0'?'selected':'' } >公司</option>
			       		<option value="1" ${fixedAssets.owner eq '1'?'selected':'' } >个人</option>
			       		<option value="2" ${fixedAssets.owner eq '2'?'selected':'' } >部门</option>
				   </select>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;display:${fixedAssets.owner eq '1'?'':'none' }" id="ownerUserIdDiv">
				<div class="controls" style="margin-left:120px;" >
				 	 <select style="width: 95%" name="ownerUser.id" id="ownerUserId">
			       		<option value="">请选择人员</option>
			       		<c:forEach items="${users}" var="user">
			        		<option value="${user.id}" ${fixedAssets.ownerUser.id eq user.id?'selected':''}>${user.name}</option>
			       		</c:forEach>
				   </select>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;display:${fixedAssets.owner eq '2'?'':'none' }" id="ownerOfficeIdDiv">
				<div class="controls" style="margin-left:120px;" >
					 	 <select style="width: 95%" name="ownerOffice.id" id="ownerOfficeId">
				       		<option value="">请选择部门</option>
				       		<c:forEach items="${offices}" var="office">
				        		<option value="${office.id}" ${fixedAssets.ownerOffice.id eq office.id?'selected':''}>${office.name}</option>
				       		</c:forEach>
					   </select>
					</div>
			</div>
		</div>
		
		
		<div class="control-group" style="float:left;width:100%;">
			<label class="control-label" style="width:100px"><b>备注:</b></label>
			<div class="controls" style="margin-left:120px" >
			 	<textarea rows="3" cols="4" name="remark" style="width:98%">${fixedAssets.remark}</textarea> 
			</div>
		</div>
		
		<div class="form-actions" style="float:left;width:98%">
			<shiro:hasPermission name="oa:fixedAssets:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</shiro:hasPermission>
		</div>
	</form:form>
</body>
</html>
