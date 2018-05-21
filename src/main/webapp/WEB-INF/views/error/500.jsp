<%@page import="org.springframework.web.multipart.MultipartException"%>
<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@ page import="com.springrain.erp.common.beanvalidator.BeanValidators"%>
<%@ page import="org.slf4j.Logger,org.slf4j.LoggerFactory" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%response.setStatus(200);%>
<%
	Throwable ex = null;
	if (exception != null)
		ex = exception;
	if (request.getAttribute("javax.servlet.error.exception") != null)
		ex = (Throwable) request.getAttribute("javax.servlet.error.exception");
	//记录日志
	if (ex!=null){
		Logger logger = LoggerFactory.getLogger("500.jsp");
		logger.error(ex.getMessage(), ex);
	}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>500 - 系统内部错误</title>
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
		<div class="page-header"><h2>ERP has some BUG,please contact with system's developers(erp_development@inateck.com) to resolve this exception. Your notices can help other little friends to use normal system as soon as possible ,Thanks!</h2></div>
		<div style="width:98%;float:left">
			<div style="width:30%;float:left">Tips(Perhaps it's Operational problems)：<br/>
			<%
				if (ex!=null){
					out.print(ex+"<br/>");
				}
			%>
			</div>
			<div style="width:70%;float:left"><div id="dog"/> </div>
		</div>
		<div><a href="javascript:" onclick="history.go(-1);" class="btn">Back</a></div>
		<script>try{top.$.jBox.closeTip();}catch(e){}</script>
	</div>
</body>
</html>