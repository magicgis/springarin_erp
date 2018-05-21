<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊帖子上架</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		if(!(top)){
			top = self; 
		}
		$(document).ready(function() {
			$("#inputForm").validate({
				submitHandler: function(form){
					var filepath = $("#excel").val();
					var extStart = filepath.lastIndexOf(".");

					var ext = filepath.substring(extStart, filepath.length).toUpperCase();
					if (ext != ".XLS" && ext != ".XLSX") {
						alert("帖子必须是excel格式!");
						return;
					}
					top.$.jBox.confirm('确定要提交帖子吗!','系统提示',function(v,h,f){
						if(v=='ok'){
							loading('正在提交，请稍等...');
							form.submit();
							$("#btnSubmit").attr("disabled","disabled");
						}
					},{buttonsFocus:1,persistent: true});
					top.$('.jbox-body .jbox-icon').css('top','55px');
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
		<li><a href="${ctx}/amazoninfo/feedSubmission/">帖子上架列表</a></li>
	<%-- 	<li  class="active"><a href="${ctx}/amazoninfo/feedSubmission/form">帖子上架</a></li> --%>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/changePostsList">帖子信息列表</a></li>	
	    <li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">新增管理<b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addParentsPostFrom">新建母帖</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom">新建普通帖</a></li>
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=8">新建本地帖</a></li>
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=2">复制帖</a></li>
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=3">Cross帖</a></li>
		    </ul>
	   </li>
	     <li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">其他管理<b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
			       <li><a href="${ctx}/amazoninfo/amazonPortsDetail/form">编辑帖子信息</a></li>
			         <li><a href="${ctx}/amazoninfo/amazonPortsDetail/commonForm">编辑帖子信息(英语国家)</a></li>		
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=4">帖子类型转换</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=5">帖子一键还原</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/deletePostsForm">删除帖子</a></li>	
		    </ul>
	   </li>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/postsRelationList">组合帖管理列表</a></li>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/formRelation">修改绑定关系</a></li>
	</ul>
	
	<form id="inputForm"  action="${ctx}/amazoninfo/feedSubmission/save" enctype="multipart/form-data" method="post" class="form-horizontal" >
		<tags:message content="${message}"/>
		<div class="alert alert-info"><strong>请将excel帖子瘦身,删除除数据页以外的tab页,以使帖子上传更快;保证帖子大小在<span style="font-size: 20px;color: orange">1.5Mb以内</span>,数据页命名为<span style="font-size: 20px;color: orange">"template"</span></strong></div>
		<div class="control-group">
			<label class="control-label">平台:</label>
			<div class="controls">
				<select name="accountName" style="width: 120px" class="required">
					<option value="" selected="selected">-请选择平台-</option>
					<shiro:hasPermission name="amazoninfo:feedSubmission:all">
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<c:forEach items="${accountMap[dic.value]}" var="account">
								<option value="${account}">${account}</option>
							</c:forEach>
						</c:forEach>
					</shiro:hasPermission>
					<shiro:lacksPermission name="amazoninfo:feedSubmission:all">
					   <c:forEach items="${fns:getDictList('platform')}" var="dic">
					        <shiro:hasPermission name="amazoninfo:feedSubmission:${dic.value}">
							  <c:forEach items="${accountMap[dic.value]}" var="account">
								  <option value="${account}">${account}</option>
							  </c:forEach>
							</shiro:hasPermission>  
						</c:forEach>
					</shiro:lacksPermission>
				</select>
				<script type="text/javascript">
					if($("option").size()==2){
						$("option:last").attr("selected","selected");				
					}
				</script>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">操作类型:</label>
			<div class="controls">
				<select name="state" style="width: 160px" class="required">
					<option value="0" selected="selected">仅存档</option>
					<option value="1">同步到亚马逊服务器</option>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">帖子文件(excel):</label>
			<div class="controls">
				<input type="hidden" value="0" name="delFlag" />
				<input type="file" name="excel"  id="excel" accept="application/msexcel" class="required"/> 
				<span class="help-inline">文件名最好不要为中/日文以及带有"+"、多空格等特殊字符</span>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="提  交"/>&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form>
</body>
</html>
