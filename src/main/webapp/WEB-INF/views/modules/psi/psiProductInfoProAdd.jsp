<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>ProAdd</title>
<script type="text/javascript">
var _hmt = _hmt || [];
(function() {
  var hm = document.createElement("script");
  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
  var s = document.getElementsByTagName("script")[0]; 
  s.parentNode.insertBefore(hm, s);
})();
	$(function(){
		
		$("#inputForm").validate({
			rules:{
				"value":{
					"required":true,
					 remote: {
					    url: "${ctx}/psi/product/isExist",     //后台处理程序
					    type: "post",               //数据发送方式
					    dataType: "json",           //接受数据格式   
					    data: {                     //要传递的数据
					        "value": function() {
					            return $("input[name='value']").val();
					        },
					        "type": function() {
					            return '${dict.type}';
					        },
					        "dicId": function() {
					            return '${dict.id}';
					        }
					    }
					}
				}
			},
			messages:{
				"value":{"remote":'产品属性名不能重复！'}
			},
			submitHandler: function(form){
			},
			errorContainer: "#messageBox",
			errorPlacement: function(error, element) {
				$("#messageBox").text("Entered incorrectly, please correct");
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
	<form:form id="inputForm" modelAttribute="dict" class="form-horizontal">
	    <div style="text-align: left;line-height: 40px;height: 40px;margin-top: 20px">
	    	<div>
				<label>类型:</label>&nbsp;&nbsp;
				<input name="type" type="text" value="${dict.type}" readonly="readonly" />
			</div>
			<div>
				<label><b>&nbsp;&nbsp;&nbsp;值:</b></label>&nbsp;&nbsp;
				<input name="value" type="text" value="${dict.value}" required="required"/><span class="help-inline">请不要包含中文</span>
			</div>
			<div>
				<label><b>名称:</b></label>&nbsp;&nbsp;
				<input name="label" type="text" value="${dict.label}"  required="required"/><span class="help-inline">请不要包含中文</span>
			</div>
		</div>
	</form:form>
</body>
</html>
