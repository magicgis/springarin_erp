<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
 
	<%--<div style="text-align:center">
		<a href="${ctx}/changeToPc?type=1" class="btn btn-small" data-inline="true">切换PC版</a>
		<a href="${ctx}/changeToPc?type=1">切换PC版</a>
	</div> --%>
	<!-- /footer -->
	<div data-role="footer" data-theme="b" data-position="fixed" >
		<%--导航栏 --%>
		<div data-role="navbar">  
			<ul> 
			    <li><a href="${ctx}/amazoninfo/salesReprots" style="color:white">销售统计</a></li>
			    <shiro:hasPermission name="psi:ladingBill:qualityTest">
			    	<li><a href="${ctx}/psi/lcPsiLadingBill" style="color:white">收货单品检</a></li>
			    </shiro:hasPermission>
			    <shiro:hasPermission name="psi:ladingBill:managerTest">
			    	<li><a href="${ctx}/psi/lcPsiLadingBill/managerReivew" style="color:white">确认品检单</a></li>
			    </shiro:hasPermission>
			    <li><a href="${ctx}/changeToPc?type=1" style="color:white">切换PC版</a></li>
			</ul> 
		</div>
		<%--<h4>Copyright &copy; ${fns:getConfig('productName')}&nbsp;|&nbsp;<a href="${ctx}/changeToPc?type=1" style="color:blue">切换PC版</a></h4> --%>
	</div>
	<!-- /footer -->
