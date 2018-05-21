<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>编辑问题类型</title>
<meta name="decorator" content="default" />
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
		$("a[rel='popover']").popover({
			trigger : 'hover'
		});
		
		$("#back").click(function(){
			window.history.go(-1);
		});

		$("#create").click(function(){
			 $("#inputForm").attr("action","${ctx}/custom/productProblem/save");
			 $("#inputForm").submit();
		});

		$("#inputForm").validate();
	});
	
</script>

</head>
<body>
	<ul class="nav nav-tabs" style="margin-top: 5px">
		<li><a href="${ctx}/custom/productProblem/problems">问题类型列表</a></li>
		<li class="active"><a href="${ctx}/custom/productProblem/form">${not empty customFilter.id?'修改':'添加'}任务</a></li>
	</ul>
	<div class="tab-content">
		<div class="tab-pane active" style="width: 98%">
		<tags:message content="${message}"/>
			<form id="inputForm" action="${ctx}/custom/productProblem/save" method="post" class="form-horizontal">
				<input type="hidden" name="id" value="${problems.id}" />
				<div class="control-group">
					<label class="control-label">产品分类</label>
					<div class="controls">
						<select name="productType" id="productType" style="width: 220px" class="required">
							<option value=""><spring:message code="amazon_order_tips4"/></option>
							<c:forEach items="${fns:getDictList('product_type')}" var="dic">
								<option value="${dic.value}" ${problems.productType eq dic.value?'selected':''}  >${dic.label}</option>
							</c:forEach>
						</select>&nbsp;&nbsp;
					</div>
				</div>
				
				<div class="control-group">
					<label class="control-label">问题类型</label>
					<div class="controls">
						<input name="problemType" value="${problems.problemType}" class="required"/>
					</div>
				</div>
				<div class="form-actions" style="text-align: center;">
					<input id="create" class="btn btn-primary" type="button" value="保存" />&nbsp;&nbsp;&nbsp;
					<input id="back" class="btn btn-info" type="button" value="返回" />&nbsp;&nbsp;&nbsp;
				</div>
			</form>
			
		</div>
	</div>

</body>
</html>
