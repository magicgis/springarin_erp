<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="org.apache.shiro.web.filter.authc.FormAuthenticationFilter"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>${fns:getConfig('productName')} 登录</title>
	<meta name="decorator" content="default"/>
    <link rel="stylesheet" href="${ctxStatic}/common/typica-login.css">
	<style type="text/css">
		.control-group{border-bottom:0px;}
	</style>
    <script src="${ctxStatic}/common/backstretch.min.js"></script>
    <script src="//g.alicdn.com/dingding/dinglogin/0.0.5/ddLogin.js"></script>
	<script type="text/javascript">
		var hanndleMessage = function (event) {
	        var origin = event.origin;
	        if( origin == "https://login.dingtalk.com" ) { //判断是否来自ddLogin扫码事件。
	            var loginTmpCode = event.data; //拿到loginTmpCode后就可以在这里构造跳转链接进行跳转了
	            url = 'https://oapi.dingtalk.com/connect/oauth2/sns_authorize?appid=dingoateeb2gjgtmoyzgch&response_type=code&scope=snsapi_login&state=STATE&redirect_uri=${basePath}a/login&loginTmpCode='+loginTmpCode;
	            window.location.href=url;
	            
	        }
	 
		};
	     
	
	
		if (typeof window.addEventListener != 'undefined') {
		    window.addEventListener('message', hanndleMessage, false);
		} else if (typeof window.attachEvent != 'undefined') {
		    window.attachEvent('onmessage', hanndleMessage);
		}
		$(document).ready(function() {
		
			var url =encodeURIComponent('https://oapi.dingtalk.com/connect/oauth2/sns_authorize?appid=dingoateeb2gjgtmoyzgch&response_type=code&scope=snsapi_login&state=STATE&redirect_uri=${basePath}a/login');
			var obj = DDLogin({
			     id:"login_container",//这里需要你在自己的页面定义一个HTML标签并设置id，例如<div id="login_container"></div>或<span id="login_container"></span>
			     goto: url,
			     style: "border:none;background-color:#FFFFFF;",
			     width : "365",
			     height: "400"
			 });
			
			$.backstretch([
				"${ctxStatic}/images/banner.jpg", 
 		      	"${ctxStatic}/images/banner.jpg"
 		  	], {duration: 10000, fade: 2000});
			$("#loginForm").validate({
				rules: {
					validateCode: {remote: "${pageContext.request.contextPath}/servlet/validateCodeServlet"}
				},
				messages: {
					username: {required: "请填写用户名."},password: {required: "请填写密码."},
					validateCode: {remote: "验证码不正确.", required: "请填写验证码."}
				},
				errorLabelContainer: "#messageBox",
				errorPlacement: function(error, element) {
					error.appendTo($("#loginError").parent());
				} 
			});
		});
		// 如果在框架中，则跳转刷新上级页面
		if(self.frameElement && self.frameElement.tagName=="IFRAME"){
			parent.location.reload();
		}
	</script>
</head>
<body>
    <%--<div class="navbar navbar-fixed-top">
      <div class="navbar-inner">
        <div class="container">
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          <a href="${ctx}"><img src="${ctxStatic}/images/logo.png" alt="SpringRain Erp" style="height:70px"></a>
        </div>
      </div>
    </div> --%>

    <div class="container" >
		<!--[if lte IE 6]><br/><div class='alert alert-block' style="text-align:left;padding-bottom:10px;"><a class="close" data-dismiss="alert">x</a><h4>温馨提示：</h4><p>你使用的浏览器版本过低。为了获得更好的浏览体验，我们强烈建议您 <a href="http://browsehappy.com" target="_blank">升级</a> 到最新版本的IE浏览器，或者使用较新版本的 Chrome、Firefox、Safari 等。</p></div><![endif]-->
		<%String error = (String) request.getAttribute(FormAuthenticationFilter.DEFAULT_ERROR_KEY_ATTRIBUTE_NAME);%>
		<div id="messageBox" class="alert alert-error <%=error==null?"hide":""%>"><button data-dismiss="alert" class="close">×</button>
			<label id="loginError" class="error"><%=error==null?"":"com.thinkgem.jeesite.modules.sys.security.CaptchaException".equals(error)?"验证码错误, 请重试.":"com.springrain.erp.modules.sys.security.PasswordException".equals(error)?"动态密码已失效,请重新获取.":"用户名或密码错误, 请重试." %></label>
		</div>
		 <ul id="loginTab" class="nav nav-tabs">
		    	 <li >
			        <a href="#login-wraper" data-toggle="tab">
			            密码登录
			        </a>
			    </li>
   				<li class="active"><a href="#login_container" data-toggle="tab">钉钉扫码</a></li>
		  </ul>
		<div class="tab-content">
	        <div id="login-wraper" class="tab-pane fade">
	            <form id="loginForm"  class="form login-form" action="${ctx}/login" method="post">
	                <legend><span style="color:#08c;"><a href="${ctx}"><img src="${ctxStatic}/images/logo.png" alt="SpringRain Erp" style="height:70px"></a></span></legend>
	                <div class="body">
						<div class="control-group">
							<div class="controls">
								<input type="text" id="username" name="username" class="required" value="${username}" placeholder="登录名">
							</div>
						</div>
						
						<div class="control-group">
							<div class="controls">
								<input type="password" id="password" name="password" class="required" placeholder="密码"/>
							</div>
						</div>
						<c:if test="${isValidateCodeLogin}"><div class="validateCode">
							<label for="password">验证码：</label>
							<tags:validateCode name="validateCode" inputCssStyle="margin-bottom:0;"/>
						</div></c:if>
						
	                </div>
	                <div class="footer">
	                    <input class="btn btn-primary" type="submit" value="登 录"/>
	                </div>
					<div id="themeSwitch" class="dropdown pull-right" style="z-index:9999">
						<a class="dropdown-toggle" data-toggle="dropdown" href="#">${fns:getDictLabel(cookie.theme.value,'theme','默认主题')}<b class="caret"></b></a>
						<ul class="dropdown-menu">
						  <c:forEach items="${fns:getDictList('theme')}" var="dict"><li><a href="#" onclick="location='${pageContext.request.contextPath}/theme/${dict.value}?url='+location.href">${dict.label}</a></li></c:forEach>
						</ul>
						<!--[if lte IE 6]><script type="text/javascript">$('#themeSwitch').hide();</script><![endif]-->
					</div>
	            </form>
	        </div>
	        <div id="login_container" class="tab-pane fade in active"  ></div>
        </div>
    </div>
    <footer class="white navbar-fixed-bottom">
		Copyright &copy; 2012-${fns:getYear()} <a href="${pageContext.request.contextPath}${fns:getFrontPath()}">${fns:getConfig('productName')}</a> - Powered By <a href="http://www.inateck.com" target="_blank">Inateck</a> ${fns:getConfig('version')}
    </footer>
  </body>
</html>