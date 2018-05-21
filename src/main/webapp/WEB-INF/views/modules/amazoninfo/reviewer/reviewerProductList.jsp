<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Review product manager</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<link href="${ctxStatic}/common/mailstate.css" type="text/css" rel="stylesheet" />
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
			if(!(top)){
				top = self; 
			}
			$("#pType").val($("#productType").val());
			$("a[rel='popover']").popover({trigger:'hover'});
			
			// 表格排序
			var orderBy = $("#orderBy").val().split(" ");
			$("#contentTable th.sort").each(function(){
				if ($(this).hasClass(orderBy[0])){
					orderBy[1] = orderBy[1]&&orderBy[1].toUpperCase()=="DESC"?"down":"up";
					$(this).html($(this).html()+" <i class=\"icon icon-arrow-"+orderBy[1]+"\"></i>");
				}
			});
			$("#contentTable th.sort").click(function(){
				var order = $(this).attr("class").split(" ");
				var sort = $("#orderBy").val().split(" ");
				for(var i=0; i<order.length; i++){
					if (order[i] == "sort"){order = order[i+1]; break;}
				}
				if (order == sort[0]){
					sort = (sort[1]&&sort[1].toUpperCase()=="DESC"?"ASC":"DESC");
					$("#orderBy").val(order+" DESC"!=order+" "+sort?"":order+" "+sort);
				}else{
					$("#orderBy").val(order+" ASC");
				}
				page();
			});
			
			$(".back").click(function(){
				var country = '${reviewerContent.reviewer.country}';
				window.location.href = "${ctx}/amazoninfo/reviewer?country=" + country;
			});
		});
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		
		function changeBrandType(){
			$("#searchForm").submit();
		}
		
		function changeBrandType(){
			$("#searchForm").submit();
		}
		
		function changeReviewer(){
			$("#reviewer\\.id").val("");
		}
		
		function changeType(){
			var pType = encodeURIComponent($("#pType").val());
			$("#productType").val(pType);
		}
		
		function productDetail(productName){
			var url = "${ctx}/psi/psiInventory/productInfoDetail?productName=" + encodeURIComponent(productName);
			window.location.href = url;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/reviewer/">评测客户汇总</a></li>
		<li class="active"><a href="#">评测产品列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="reviewerContent" action="${ctx}/amazoninfo/reviewer/reviewerProductList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input id="reviewer.id" name="reviewer.id" type="hidden" value="${reviewerContent.reviewer.id}"/>
		<input id="reviewer.country" name="reviewer.country" type="hidden" value="${reviewerContent.reviewer.country}"/>
		<input id="productType" name="productType" type="hidden" value="${reviewerContent.productType}"/>
		<label>评测人:</label><form:input path="reviewer.name" htmlEscape="false" maxlength="50" class="input-small" onkeyup="changeReviewer()"/>
		&nbsp;&nbsp;&nbsp;<label>品牌类型:</label>
			<form:select path="brandType" onchange="changeBrandType()">
				<form:option value="" label="--All--"/>
				<form:option value="inateck" label="Inateck"/>
				<form:option value="anker" label="Anker"/>
				<form:option value="aukey" label="Aukey"/>
				<form:option value="taotronics" label="TaoTronics"/>
				<form:option value="easyacc" label="EasyAcc"/>
				<form:option value="mpow" label="Mpow"/>
				<form:option value="ravpower" label="RAVPower"/>
				<form:option value="csl" label="CSL"/>
				<form:option value="other" label="other"/>
			</form:select>
			<label>产品类型:</label><input id="pType" name="pType" maxlength="50" type="text" class="input-small" onkeyup="changeType()"/>
		&nbsp;&nbsp;&nbsp;<input class="btn btn-primary" type="submit" value="<spring:message code='sys_but_search' />"/>
		&nbsp;&nbsp;&nbsp;<input class="btn back" type="button" value="<spring:message code='sys_but_back'/> "/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
		<th class="sort reviewer.name">评测人</th>
		<th class="sort reviewer.name">国家</th>
		<c:if test="${'inateck' eq reviewerContent.brandType}"><th>产品名称</th></c:if>
		<th class="sort reviewTitle">reviewTitle</th>
		<th class="sort productTitle">productTitle</th>
		<th class="sort brandType">品牌类型</th>
		<th class="sort productType">产品类型</th>
		<th class="sort star">评分</th>
		<th class="sort reviewDate">评测时间</th>
		</tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="content">
			<tr>
				<td>${content.reviewer.name}</td>
				<td>${fns:getDictLabel(content.reviewer.country,'platform','')}</td>
				<c:if test="${'inateck' eq reviewerContent.brandType}">
					<td>
					<c:if test="${not empty productName[content.asin]}">
						<a onclick="productDetail('${productName[content.asin]}')" href="#">${productName[content.asin]}</a>
					</c:if>
					</td>
				</c:if>
				<td><a target="_blank" href="https://www.amazon.${content.stuffix}/review/${content.reviewId}" rel="popover" data-content="${content.reviewTitle}">${empty fn:substring(content.reviewTitle,0,30)?'Empty':fn:substring(content.reviewTitle,0,30)}</a></td>
				<td><a target="_blank" href="https://www.amazon.${content.stuffix}/review/${content.reviewId}" rel="popover" data-content="${content.productTitle}">${empty fn:substring(content.productTitle,0,30)?'Empty':fn:substring(content.productTitle,0,30)}</a></td>
				<td>${content.brandType}</td>
				<td>${content.productType}</td>
				<td>${content.star}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${content.reviewDate}"/></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
