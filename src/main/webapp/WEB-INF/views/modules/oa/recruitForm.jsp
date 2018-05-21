<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>招聘编辑</title>
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
			
			$(".Wtime").live("click", function (){
				 WdatePicker({ dateFmt: "yyyy-MM-dd HH:mm:ss"});
			});
			
			
			$(".Wdate").live("click", function (){
				 WdatePicker({ dateFmt: "yyyy-MM-dd"});
			});
			
		
			
			
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
		<li><a href="${ctx}/oa/recruit/">招聘列表</a></li>
		<li class="active"><a href="${ctx}/oa/recruit/form?id=${recruit.id}">招聘${not empty recruit.id?'编辑':'添加'}</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="recruit" action="${ctx}/oa/recruit/save" method="post" class="form-horizontal" enctype="multipart/form-data">
		<input type="hidden" name="id"                      value="${recruit.id}"/>
		<input type="hidden" name="delFlag"                 value="${recruit.delFlag}"/>
		<input type="hidden" name="createUser.id" 			value="${recruit.createUser.id}"/>
		<input type="hidden" name="updateUser.id" 			value="${recruit.updateUser.id}"/>
		<input type="hidden" name="createDate" 				value="<fmt:formatDate pattern='yyyy-MM-dd' value='${recruit.createDate}'/>"/>
		<input type="hidden" name="updateDate" 				value="<fmt:formatDate pattern='yyyy-MM-dd' value='${recruit.updateDate}'/>"/>
		<input type="hidden" name="resumeFile"              value="${recruit.resumeFile}"/> 	
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>姓名:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="name"  type="text" style="width:95%" value="${recruit.name}" class="required"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>部门:</b></label>
				<div class="controls" style="margin-left:120px" >
			       <select style="width: 100%" name="office.id" id="office" class="required">
			       		<option value="">请选择</option>
			       		<c:forEach items="${offices}" var="office">
			        		<option value="${office.id}" ${recruit.office.id eq office.id?'selected':''}>${office.name}</option>
			       		</c:forEach>
				   </select>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>职位:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="position"  type="text" style="width:95%" value="${recruit.position}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>性别:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<select style="width: 100%" name="sex" class="required">
			       		<option value="">请选择</option>
			        	<option value="男" ${recruit.sex eq '男'?'selected':''}>男</option>
			       		<option value="女" ${recruit.sex eq '女'?'selected':''}>女</option>
				   </select>
				</div>
			</div>
		</div>
		
		<div style="float:left;width:98%">
		
			<div class="control-group" style="float:left;width:25%;height:25px">
				<label class="control-label" style="width:100px"><b>电话:</b></label>
				<div class="controls" style="margin-left:120px" >
				 	<input name="phone"  type="text" style="width:95%" value="${recruit.phone}"/>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>邮箱:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="email"  type="text" style="width:95%" value="${recruit.email}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>简历来源:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="origin"  type="text" style="width:95%" value="${recruit.origin}"/>
				</div>
			</div>
			
		</div>
		
	
		
		<blockquote style="margin:0px;margin-top:10px;margin-bottom:5px;width:100%;float:left;">
			<p style="font-size: 14px;color:#8E8E8E;"><b>面试情况：</b></p>
		</blockquote>
		
		<div style="float:left;width:98%">
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>通知日期:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="noticeDate"  type="text" class="Wdate" style="width:95%" value="<fmt:formatDate value="${recruit.noticeDate}" pattern="yyyy-MM-dd"/>"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>面试时间:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="interviewDate"  type="text" class="Wtime required" style="width:95%" value="<fmt:formatDate value="${recruit.interviewDate}" pattern="yyyy-MM-dd HH:mm:ss"/>"/>
				</div>
			</div>
			
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>简历链接:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="resumeUrl"  type="text" style="width:95%" value="${recruit.resumeUrl}"/>
				</div>
			</div>
			<div class="control-group" style="float:left;width:25%;height:25px;">
				<label class="control-label" style="width:100px"><b>简历文件:</b></label>
				<div class="controls" style="margin-left:120px" >
			      <input name="resumePath"  type="file" style="width:95%" />
				</div>
			</div>
			
			
		</div>
		
		<div class="control-group" style="float:left;width:100%;">
			<label class="control-label" style="width:100px"><b>初试评价:</b></label>
			<div class="controls" style="margin-left:120px" >
			 	<textarea rows="3" cols="4" name="interviewReview1" style="width:98%">${recruit.interviewReview1}</textarea> 
			</div>
		</div>
		<div class="control-group" style="float:left;width:100%;">
			<label class="control-label" style="width:100px"><b>复试评价:</b></label>
			<div class="controls" style="margin-left:120px" >
		     <textarea rows="3" cols="4" name="interviewReview2" style="width:98%">${recruit.interviewReview2}</textarea> 
			</div>
		</div>
		
		<div class="form-actions" style="float:left;width:98%">
			<shiro:hasPermission name="oa:recruit:edit">
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
				<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
			</shiro:hasPermission>
		</div>
	</form:form>
</body>
</html>
