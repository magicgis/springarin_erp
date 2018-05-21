<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊账号添加</title>
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
					top.$.jBox.confirm('确定要提交吗!','系统提示',function(v,h,f){
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
		<li><a href="${ctx}/amazoninfo/amazonTreadReview">踩差评列表</a></li>
		<li><a href="${ctx}/amazoninfo/amazonTreadReview/form">新增踩差评</a></li>
		<li><a href="${ctx}/amazoninfo/amazonTreadReview/accountList">账号列表</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/amazonTreadReview/accountAdd">新增账号</a></li>
	</ul>
	
	<form id="inputForm"  action="${ctx}/amazoninfo/amazonTreadReview/saveAccount" enctype="multipart/form-data" method="post" class="form-horizontal" >
		<tags:message content="${message}"/>
		<div class="alert alert-info"><strong>Excel文件请按照第一列账号,第二列密码格式上传</strong></div>
		<div class="control-group">
			<label class="control-label">平台:</label>
			<div class="controls">
				<select name="country" style="width: 120px" class="required">
					<option value="" selected="selected">-请选择平台-</option>
					<shiro:hasPermission name="amazoninfo:feedSubmission:all">
					    <option value="com" >欧美</option>
						<option value="jp" >日本</option>
					</shiro:hasPermission>
					<shiro:lacksPermission name="amazoninfo:feedSubmission:all">
					    <c:set var='flag' value='0' />
						<shiro:hasPermission name="amazoninfo:feedSubmission:de">
							 <c:set var='flag' value='1' />
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:com">
							 <c:set var='flag' value='1' />
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:fr">
							 <c:set var='flag' value='1' />
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:es">
							 <c:set var='flag' value='1' />
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:it">
							 <c:set var='flag' value='1' />
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:uk">
							 <c:set var='flag' value='1' />
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:ca">
							 <c:set var='flag' value='1' />
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:mx">
							 <c:set var='flag' value='1' />
						</shiro:hasPermission>
						<c:if test="${'1' eq flag }"><option value="com" >欧美</option></c:if>
						<shiro:hasPermission name="amazoninfo:feedSubmission:jp">
							<option value="jp" >日本</option>
						</shiro:hasPermission>
					</shiro:lacksPermission>
				</select>
				<script type="text/javascript">
					$("option[value='${amazonTreadReviewAccount.country}']").attr("selected","selected");				
				</script>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">账号文件(excel):</label>
			<div class="controls">
				<input type="hidden" value="0" name="delFlag" />
				<input type="file" name="excel"  id="excel" accept="application/msexcel" class="required"/> 
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="提  交"/>&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form>
</body>
</html>
