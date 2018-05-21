<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>${fns:getConfig('productName')}</title>
	<%@include file="/WEB-INF/views/mobile/include/head.jsp" %>
	<meta name="decorator" content="default_mb"/>
	<script type="text/javascript"> 
		$(document).ready(function() {
			<shiro:lacksPermission name="psi:ladingBill:qualityTest">
		    <shiro:lacksPermission name="psi:ladingBill:managerTest">
		    	window.location.href = "${ctx}/amazoninfo/salesReprots/mobileList";
	    	</shiro:lacksPermission>
		    </shiro:lacksPermission>
		});
	</script>
</head>
<body>
<%-- --%>
<div data-role="page">
  	<div data-role="header" data-theme="b">
    	<a href="${ctx}/changeToPc?type=1" data-role="button" class="ui-btn-left">PC版</a>
    	<h4>${fns:getConfig('productName')}</h4>
    	<a href="${ctx}/logout" data-role="button" class="ui-btn-right">Logout</a>
  	</div>
	<ul data-role="listview" data-ajax="false"  data-inset="true" data-divider-theme="b">
		<%--<li><a href="${ctx }/sys/user/info">个人信息</a></li>
	    <li><a href="${ctx }/sys/user/modifyPwd">修改密码</a></li> --%>
	    <li><a href="${ctx}/amazoninfo/salesReprots">销售统计</a></li>
	    <shiro:hasPermission name="psi:ladingBill:qualityTest">
	    	<li><a href="${ctx}/psi/lcPsiLadingBill">收货单品检</a></li>
	    </shiro:hasPermission>
	    <shiro:hasPermission name="psi:ladingBill:managerTest">
	    	<li><a href="${ctx}/psi/lcPsiLadingBill/managerReivew">确认品检单</a></li>
	    </shiro:hasPermission>
	</ul>
</div>
</body>
</html>