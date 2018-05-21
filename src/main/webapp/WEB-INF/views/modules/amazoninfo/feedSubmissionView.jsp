<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊帖子详情查看</title>
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
		<li><a href="${ctx}/amazoninfo/feedSubmission/">帖子上架列表</a></li>
		<li class="active"><a href="#">帖子详情</a></li>
		<%-- <li><a href="${ctx}/amazoninfo/feedSubmission/form">帖子上架</a></li> --%>
	</ul><br/>
	<form  class="form-horizontal" >
		<blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		<div class="control-group">
			<label class="control-label">创建人</label>
			<div class="controls">
				${feedSubmission.createBy.name}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">平台</label>
			<div class="controls">
				${fns:getDictLabel(feedSubmission.country,'platform','')}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">创建时间</label>
			<div class="controls">
				<fmt:formatDate value="${feedSubmission.createDate}" pattern="yyyy-MM-dd H:mm"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态</label>
			<div class="controls">
				${feedSubmission.stateStr}
			</div>
		</div>
		<ul class="nav nav-tabs" id="cNav">
			<c:forEach items="${feedSubmission.feeds}" var="feed" varStatus="i">
				<li ${i.index==0?'class=active':''}><a href="#panel-${i.index}" data-toggle="tab"><b>${feed.sku}</b></a></li>
			</c:forEach>
		</ul>
		<div class="tab-content" id="cTab">
			<c:forEach items="${feedSubmission.feeds}" var="feed" varStatus="i">
				<div class="tab-pane ${i.index==0?'active':''}" id="panel-${i.index}">
					<blockquote>
						<p style="font-size: 14px">基本详情</p>
					</blockquote>
					<div class="control-group">
						<label class="control-label">Ean Or Asin</label>
						<div class="controls">
							${feed.ean}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">标题</label>
						<div class="controls">
							${feed.subject}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">描述</label>
						<div class="controls">
							${feed.description}</b></p>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">价格</label>
						<div class="controls">
							${feed.price}
						</div>
					</div>
					<c:if test="${not empty feed.salePrice}">
						<div class="control-group">
							<label class="control-label">销售价格</label>
							<div class="controls">
								${feed.salePrice}
							</div>
						</div>
					</c:if>
					<c:if test="${not empty feed.saleStartDate}">
						<div class="control-group">
							<label class="control-label">开始时间</label>
							<div class="controls">
								<fmt:formatDate value="${feed.saleStartDate}" pattern="yyyy-MM-dd" />
							</div>
						</div>
					</c:if>
					<c:if test="${not empty feed.saleEndDate}">
						<div class="control-group">
							<label class="control-label">结束时间</label>
							<div class="controls">
								<fmt:formatDate value="${feed.saleEndDate}" pattern="yyyy-MM-dd" />
							</div>
						</div>
					</c:if>
					<c:if test="${not empty feed.parentChild}">
						<div class="control-group">
							<label class="control-label">parentChild</label>
							<div class="controls">
								${feed.parentChild}
							</div>
						</div>
					</c:if>
					<c:if test="${not empty feed.parentSku}">
						<div class="control-group">
							<label class="control-label">parentSku</label>
							<div class="controls">
								${feed.parentSku}
							</div>
						</div>
					</c:if>
					<c:if test="${not empty feed.relationshipType}">
						<div class="control-group">
							<label class="control-label">relationshipType</label>
							<div class="controls">
								${feed.relationshipType}
							</div>
						</div>
					</c:if>
					<blockquote>
						<p style="font-size: 14px">产品卖点</p>
					</blockquote>
					<div class="control-group">
						<label class="control-label">卖点1</label>
						<div class="controls">
							${feed.bulletPoint1}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">卖点2</label>
						<div class="controls">
							${feed.bulletPoint2}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">卖点3</label>
						<div class="controls">
							${feed.bulletPoint3}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">卖点4</label>
						<div class="controls">
							${feed.bulletPoint4}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">卖点5</label>
						<div class="controls">
							${feed.bulletPoint5}
						</div>
					</div>
					<blockquote>
						<p style="font-size: 14px">产品关键字</p>
					</blockquote>
					<div class="control-group">
						<label class="control-label">关键字1</label>
						<div class="controls">
							${feed.genericKeywords1}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">关键字2</label>
						<div class="controls">
							${feed.genericKeywords2}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">关键字3</label>
						<div class="controls">
							${feed.genericKeywords3}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">关键字4</label>
						<div class="controls">
							${feed.genericKeywords4}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">关键字5</label>
						<div class="controls">
							${feed.genericKeywords5}
						</div>
					</div>
				</div>
			</c:forEach>
		</div>
	</form>
		
</body>
</html>
