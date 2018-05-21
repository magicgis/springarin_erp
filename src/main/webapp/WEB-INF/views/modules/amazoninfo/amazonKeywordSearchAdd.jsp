<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>keyword</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/treeview.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a class="countryHref" href="#" key="">总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li ><a class="countryHref" href="${ctx}/amazoninfo/amazonKeyword/list?country=${dic.value}" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
		<li class='active'><a href="${ctx}/amazoninfo/amazonKeyword/add">关键字采集</a></li>	
	</ul>
	<form:form id="inputForm" modelAttribute="dictType" action="${ctx}/amazoninfo/amazonKeyword/save" method="post" class="form-horizontal">
		<tags:message content="${message}"/>
		<div class="control-group">
			<label class="control-label">国家:</label>
			<div class="controls">
			<select id="country" name="country" style="width: 200px" class="required">
			    <c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			      <c:if test="${dic.value ne 'com.unitek'}">
			           <option value="${dic.value}">${dic.label}</option>
			       </c:if>
			    </c:forEach>
			</select>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">关键字:</label>
			<div class="controls">
				<input id="keyword" name="keyword" type="text"  maxlength="200"  class="required"/>
			</div>
		</div>
		<div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" id="btnSubmit" value="保 存"/>
			&nbsp;&nbsp;&nbsp;<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>