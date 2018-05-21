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
	   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/changePostsList">帖子信息列表</a></li>	
	   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/form">修改帖子信息</a></li>	
	   <li class="active"><a href="#">帖子详情</a></li>
	</ul><br/>
	<form  class="form-horizontal" >
		<blockquote>
			<p style="font-size: 14px">基本信息</p>
		</blockquote>
		<div class="control-group">
			<label class="control-label">创建人</label>
			<div class="controls">
				${amazonPostsFeed.createUser.name}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">平台</label>
			<div class="controls">
				${fns:getDictLabel(amazonPostsFeed.country,'platform','')}
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">创建时间</label>
			<div class="controls">
				<fmt:formatDate value="${amazonPostsFeed.createDate}" pattern="yyyy-MM-dd H:mm"/>
			</div>
		</div>
		<div class="control-group">
			<label class="control-label">状态</label>
			<div class="controls">
				${amazonPostsFeed.stateStr}
			</div>
		</div>
		<ul class="nav nav-tabs" id="cNav">
			<c:forEach items="${amazonPostsFeed.items}" var="feed" varStatus="i">
				<li ${i.index==0?'class=active':''}><a href="#panel-${i.index}" data-toggle="tab" title="${feed.sku}"><b>${empty feed.productName?feed.sku:feed.productName}</b></a></li>
			</c:forEach>
		</ul>
		<div class="tab-content" id="cTab">
			<c:forEach items="${amazonPostsFeed.items}" var="feed" varStatus="i">
				<div class="tab-pane ${i.index==0?'active':''}" id="panel-${i.index}">
					<blockquote>
						<p style="font-size: 14px">基本详情</p>
					</blockquote>
					<c:if test="${not empty feed.studio}">
						<div class="control-group">
							<label class="control-label">优化帖子</label>
							<div class="controls">
								${"0" eq feed.studio?'是':'否'}
							</div>
						</div>
					</c:if>
					<c:if test="${not empty feed.sku}">
					<div class="control-group">
						<label class="control-label">Sku</label>
						<div class="controls">
							${feed.sku}
						</div>
					</div>
					</c:if>
					<c:if test="${not empty feed.ean}">
					<div class="control-group">
						<label class="control-label">ean</label>
						<div class="controls">
							${feed.ean}
						</div>
					</div>
					</c:if>
					<c:if test="${not empty feed.asin}">
					<div class="control-group">
						<label class="control-label">asin</label>
						<div class="controls">
							${feed.asin}
						</div>
					</div>
					</c:if>
					<c:if test="${not empty feed.title}">
					<div class="control-group">
						<label class="control-label">标题</label>
						<div class="controls">
							${feed.title}
						</div>
					</div>
					</c:if>
					<c:if test="${not empty feed.description}">
					<div class="control-group">
						<label class="control-label">描述</label>
						<div class="controls">
							${feed.description}
						</div>
					</div>
					</c:if>
					<c:if test="${not empty feed.brand}">
					<div class="control-group">
						<label class="control-label">Brand</label>
						<div class="controls">
							${feed.brand}
						</div>
					</div>
					</c:if>
					<c:if test="${not empty feed.manufacturer}">
					<div class="control-group">
						<label class="control-label">Manufacturer</label>
						<div class="controls">
							${feed.manufacturer}
						</div>
					</div>
					</c:if>
					<c:if test="${not empty feed.partNumber}">
					<div class="control-group">
						<label class="control-label">PartNumber</label>
						<div class="controls">
							${feed.partNumber}
						</div>
					</div>
					</c:if>
					<c:if test="${not empty feed.packageLength}">
					<div class="control-group">
						<label class="control-label">PackageLength(inches)</label>
						<div class="controls">
							${feed.packageLength}
						</div>
					</div>
					</c:if>
					<c:if test="${not empty feed.packageWidth}">
					<div class="control-group">
						<label class="control-label">PackageWidth(inches)</label>
						<div class="controls">
							${feed.packageWidth}
						</div>
					</div>
					</c:if>
					<c:if test="${not empty feed.packageHeight}">
					<div class="control-group">
						<label class="control-label">PackageHeight(inches)</label>
						<div class="controls">
							${feed.packageHeight}
						</div>
					</div>
					</c:if>
					<c:if test="${not empty feed.packageWeight}">
					<div class="control-group">
						<label class="control-label">PackageWeight(pounds)</label>
						<div class="controls">
							${feed.packageWeight}
						</div>
					</div>
					</c:if>
					<c:if test="${not empty feed.feature1 }">
					<blockquote>
						<p style="font-size: 14px">产品卖点</p>
					</blockquote>
					<div class="control-group">
						<label class="control-label">卖点1</label>
						<div class="controls">
							${feed.feature1}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">卖点2</label>
						<div class="controls">
							${feed.feature2}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">卖点3</label>
						<div class="controls">
							${feed.feature3}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">卖点4</label>
						<div class="controls">
							${feed.feature4}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">卖点5</label>
						<div class="controls">
							${feed.feature5}
						</div>
					</div>
					</c:if>
					<c:if test="${not empty feed.keyword1}">
					<blockquote>
						<p style="font-size: 14px">产品关键字</p>
					</blockquote>
					<div class="control-group">
						<label class="control-label">关键字1</label>
						<div class="controls">
							${feed.keyword1}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">关键字2</label>
						<div class="controls">
							${feed.keyword2}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">关键字3</label>
						<div class="controls">
							${feed.keyword3}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">关键字4</label>
						<div class="controls">
							${feed.keyword4}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label">关键字5</label>
						<div class="controls">
							${feed.keyword5}
						</div>
					</div>
					</c:if>
					<c:if test="${not empty feed.catalog1||not empty feed.catalog2}">
					   <blockquote>
						<p style="font-size: 14px">产品目录</p>
					   </blockquote>
					   <c:if test="${not empty feed.catalog1}">
						   <div class="control-group">
							<label class="control-label">目录1</label>
							<div class="controls">
								${feed.catalog1}
							</div>
						   </div>
					   </c:if>
					   <c:if test="${not empty feed.catalog2}">
						   <div class="control-group">
							<label class="control-label">目录2</label>
							<div class="controls">
								${feed.catalog2}
							</div>
						   </div>
					   </c:if>
					</c:if>
					<c:if test="${not empty feed.salePrice }">
					   <blockquote>
						<p style="font-size: 14px">产品改价</p>
					   </blockquote>
					   <c:if test="${not empty feed.price}">
						   <div class="control-group">
							<label class="control-label">Price</label>
							<div class="controls">
								${feed.price}
							</div>
						   </div>
					   </c:if>
					   <c:if test="${not empty feed.salePrice}">
						   <div class="control-group">
							<label class="control-label">SalePrice</label>
							<div class="controls">
								${feed.salePrice}
							</div>
						   </div>
					   </c:if>
					   <c:if test="${not empty feed.reason}">
						   <div class="control-group">
							<label class="control-label">Reason</label>
							<div class="controls">
								${feed.reason}
							</div>
						   </div>
					   </c:if>
					</c:if>
				</div>
			</c:forEach>
		</div>
	</form>
		
</body>
</html>
