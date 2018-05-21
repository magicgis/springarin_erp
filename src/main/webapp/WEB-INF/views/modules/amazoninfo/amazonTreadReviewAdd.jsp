<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊踩评论</title>
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
					
					top.$.jBox.confirm('确定要踩差评？','系统提示',function(v,h,f){
						if(v=='ok'){
							if($("#asin1").val()!='asin'){
								$("#asin1").attr("name","asin");
							}else{
								$("#asin2").attr("name","asin");
							}	
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
			
			$("#country").change(function(){
				var params = {};
				params.country = $(this).val();
				window.location.href = "${ctx}/amazoninfo/amazonTreadReview/form?"+$.param(params);
		 });
			
			
			$("#asin1").change(function(){
				if($(this).val()=='asin'){
					$(this).parent().find("input[type='text']").show();
				}else if($(this).val()!=''){
					$(this).parent().find("input[type='text']").hide();
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/amazonTreadReview">踩差评列表</a></li>
		<li  class="active"><a href="${ctx}/amazoninfo/amazonTreadReview/form">新增踩差评</a></li>
	</ul>
	
	<form id="inputForm"  action="${ctx}/amazoninfo/amazonTreadReview/treadReview"  method="post" class="form-horizontal" >
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">平台:</label>
			<div class="controls">
				<select id='country' name="country" style="width: 120px" class="required">
					<option value="" selected="selected">-请选择平台-</option>
					<shiro:hasPermission name="amazoninfo:feedSubmission:all">
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<c:if test="${dic.value ne 'com.unitek'}">
								<option value="${dic.value}" >${dic.label}</option>
							</c:if>
						</c:forEach>
					</shiro:hasPermission>
					<shiro:lacksPermission name="amazoninfo:feedSubmission:all">
						<shiro:hasPermission name="amazoninfo:feedSubmission:de">
							<option value="de" >德国</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:com">
							<option value="com" >美国</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:fr">
							<option value="fr" >法国</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:jp">
							<option value="jp" >日本</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:es">
							<option value="es" >西班牙</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:it">
							<option value="it" >意大利</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:uk">
							<option value="uk" >英国</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:ca">
							<option value="ca" >加拿大</option>
						</shiro:hasPermission>
						<shiro:hasPermission name="amazoninfo:feedSubmission:mx">
							<option value="mx" >墨西哥</option>
						</shiro:hasPermission>
					</shiro:lacksPermission>
				</select>
				<script type="text/javascript">
					$("option[value='${amazonTreadReview.country}']").attr("selected","selected");				
				</script>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Asin:</label>
			<div class="controls">
				<select name="asin1" style="width: 300px" class="required" id='asin1'>
				  <c:forEach items='${asinAndNameList}' var='asinAndName'>
					<option value="${asinAndName.asin }">${asinAndName.productName}[${asinAndName.asin }]</option>
				  </c:forEach>
				  <option value='asin'>手动输入Asin</option>
				</select>
				<input name="asin2" onblur='queryManually(this)' id='asin2' type="text" style="margin-top:5px;display: none;"/>
			</div>
		</div>
		
		<div class="control-group">
			<label class="control-label">账号数：</label>
			<div class="controls">
				<div class="input-prepend input-append">
					<input class="number required"  name='accountNum' value='1' type='text'  style='width:100px;'/>
				</div>
			</div>
		</div>
		
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="提  交"/>&nbsp;&nbsp;&nbsp;
			&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form>
</body>
</html>
