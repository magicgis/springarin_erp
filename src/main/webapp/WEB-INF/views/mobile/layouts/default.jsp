<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ taglib prefix="sitemesh" uri="http://www.opensymphony.com/sitemesh/decorator" %>
<!DOCTYPE html>
<html>
	<head>
		<title><sitemesh:title/> - Powered By SpringRain</title>
		<%@include file="/WEB-INF/views/mobile/include/head.jsp" %>
		<sitemesh:head/>
	</head>
	<body data-role="page">
		<div data-role="header" data-theme="b">
		    <a href="${ctx}" data-icon="home">首页</a>
		    <h1>Hello：${fns:getUser().name }-SpringRain</h1>
		    <a href="${ctx}/logout" data-icon="back">loginout</a>
		</div>
		<div role="main" class="ui-content">
			<sitemesh:body/>
		</div><!-- /content -->
		<div data-role="footer"  data-theme="b">
			<h4>Copyright &copy; 2012-${fns:getConfig('copyrightYear')} <a href="${pageContext.request.contextPath}${fns:getFrontPath()}">${fns:getConfig('productName')}</a> - Powered By <a href="http://www.springrain.eu" target="_blank">Inateck</a> ${fns:getConfig('version')}</h4>
		</div><!-- /footer -->
	</body>
</html>