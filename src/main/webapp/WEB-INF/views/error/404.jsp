<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%response.setStatus(200);%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>404 - 页面不存在</title>
	<%@include file="/WEB-INF/views/include/head.jsp" %>
</head>
<script type="text/javascript">
$(document).ready(function(){
	var aa="<img src='https://images-na.ssl-images-amazon.com/images/G/01/error/"+(Math.floor(Math.random()*43)+1)+"._TTD_.jpg' />";
	$("#dog").html(aa);
});
</script>
<body>
	<div class="container-fluid">
		<div class="page-header" style="text-align:center"><h1>Page does not exist!</h1></div>
		<div id="dog" style="text-align:center"></div>  
		<div style="text-align:center"><a href="javascript:" onclick="history.go(-1);" class="btn" style="text-align:center">Back</a></div>
		<script>try{top.$.jBox.closeTip();}catch(e){}</script>
	</div>
</body>
</html>