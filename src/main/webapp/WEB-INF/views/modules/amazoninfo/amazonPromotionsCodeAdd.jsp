<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<meta name="decorator" content="default" />
<title>codeAdd</title>
<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<script type="text/javascript">

var _hmt = _hmt || [];
(function() {
  var hm = document.createElement("script");
  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
  var s = document.getElementsByTagName("script")[0]; 
  s.parentNode.insertBefore(hm, s);
})();
	$(document).ready(function() {
		$("#inputForm").validate({
			submitHandler: function(form){
					top.$.jBox.confirm('确定申请一次性折扣？','系统提示',function(v,h,f){
						if(v=='ok'){
							form.submit();
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
		<li ><a href="${ctx}/amazoninfo/promotionsWarning/promotionsCodeList" >全品折扣列表</a></li>
		<li class="active"><a href='#'>新增全品折扣</a></li>
	</ul>
	<br />
	<tags:message content="${message}" />
	<form:form id="inputForm" modelAttribute="amazonSysPromotions"
		action="${ctx}/amazoninfo/promotionsWarning/savePromotions" method="post" class="form-horizontal" enctype="multipart/form-data">
		<div class="control-group">
			<label class="control-label">Country</label>
			<div class="controls">
				<select name="country" id="country" style="width: 222px">
					<option value="de">DE|德国</option>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Promotions Type</label>
			<div class="controls">
				<select name="promotionsType" id="promotionsType" style="width: 222px">
					<!-- <option value="0">15% discount</option> -->
					<option value="1">减20优惠</option>
				</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Num</label>
			<div class="controls">
				<form:input path="num" class="number required"  htmlEscape="false" maxlength="11" />
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">Remarks</label>
			<div class="controls">
				<div class="input-prepend input-append">
					<form:input path="remarks"  class='required' id="remarks" htmlEscape="false" maxlength="500" />
				</div>
			</div>
		</div>
		
		<div class="form-actions">
			<input  class="btn btn-primary" type="submit" value="保 存" />&nbsp;&nbsp;&nbsp;
			<input id="btnCancel" class="btn btn-primary" type="button" value="<spring:message code="sys_but_back"/>" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>