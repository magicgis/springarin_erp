<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>新增分组</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treeview.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	
		$(document).ready(function() {
		   $("#btnSubmit").click(function(){
			   if($("#name").val()==""){
					$.jBox.tip('分组名称不能为空'); 
					return;
			   }else{
				   $.ajax({
	      			   type: "POST",
	      			   url: "${ctx}/psi/psiProductTypeGroupDict/isExistName?name="+$("#name").val(),
	      			   async: false,
	      			   success: function(msg){
	      				  if(msg=="0"){
	      					$.jBox.tip('分组类型名称已经存在'); 
	    					return;
	      				  }else if(msg=="1"){
	      					$("#inputForm").submit();
	      				  }
	      			   }
	          		});
			   }
			   
		   });
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a href="${ctx}/psi/psiProductTypeGroupDict/psiProductTypeEdit">产品线</a></li>
		<li class="active"><a href="${ctx}/psi/psiProductTypeGroupDict/psiProductTypeForm?id=${dictType.id}&parent.id=${dictType.parent.id}">产品线${not empty dictType.id?'修改':'添加'}</a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="dictType" action="${ctx}/psi/psiProductTypeGroupDict/psiProductTypeGroupAdd" method="post" class="form-horizontal">
		<tags:message content="${message}"/>
		<form:hidden path="id"/>
			<%-- <div class="control-group">
			<label class="control-label">上级分组:</label>
			<div class="controls">
                <tags:treeselect id="dictType" name="parent.id" value="${dictType.parent.id}" labelName="parent.name" labelValue="${dictType.parent.name}"
					title="类型" url="/psi/psiProductTypeGroupDict/treeData" extId="${dictType.id}" />
			</div>
		</div> --%>
		<div class="control-group">
			<label class="control-label">产品线名称:</label>
			<div class="controls">
				<input id="name" name="name" type="text" value="${dictType.name }" maxlength="50"  class="required"/>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="button" id="btnSubmit" value="保 存"/>
			
		</div>
	</form:form>
</body>
</html>