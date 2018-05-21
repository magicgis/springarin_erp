<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊产品图片修改查看</title>
	<meta name="decorator" content="default"/>
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
		<li><a href="${ctx}/amazoninfo/imageFeed/">产品图片管理列表</a></li>
		<li class="active"><a href="#">修改产品图片详情</a></li>
		<li><a href="${ctx}/amazoninfo/imageFeed/form">修改产品图片</a></li>
	</ul><br/>
	<form  class="form-horizontal" >
		<blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		<div class="control-group">
			<label class="control-label">创建人</label>
			<div class="controls">
				${imageFeed.createBy.name}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">平台</label>
			<div class="controls">
				${fns:getDictLabel(imageFeed.country,'platform','')}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">产品Sku</label>
			<div class="controls">
				<b style="font-size: 18px">${imageFeed.sku}</b>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">创建时间</label>
			<div class="controls">
				<fmt:formatDate value="${imageFeed.requestDate}" pattern="yyyy-MM-dd H:mm"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态</label>
			<div class="controls">
				${imageFeed.stateStr}
			</div>
		</div>
		<blockquote>
			<p style="font-size: 14px">图片修改项</p>
		</blockquote>
		<c:forEach items="${imageFeed.images}" var="image">
			  <span class="span3" style="text-align: center;">
			  	<c:if test="${not empty image.location}">
				    <a href="#" class="thumbnail">
				      <img src="${image.location}" alt="${type}">
				    </a>
			    </c:if>
			    <c:if test="${not empty image.isDelete}">
			    	删除图片
			    </c:if>
			    ${image.type}
			  </span>
		</c:forEach>
	</form>
		
</body>
</html>
