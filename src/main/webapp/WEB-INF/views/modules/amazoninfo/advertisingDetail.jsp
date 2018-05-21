<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>广告详情数据</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			$("a[rel='popover']").popover({trigger:'hover'});
			
			$("#reback").click(function(){
				window.history.go(-1);				
			});
		});
		
	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="advertising" action="${ctx}/amazoninfo/advertising/detail" method="post" class="breadcrumb form-search">
		<input name="country" type="hidden" value="${advertising.country}"/>
		<input type="hidden" value="${advertising.name}" name="name"/>
 		<input type="hidden" value="${advertising.groupName}" name="groupName"/>
 		<input type="hidden" value="${advertising.keyword}" name="keyword"/>
		<input type="hidden" value="${advertising.sku}" name="sku"/>
		
		<div style="height: 30px">
			日期区间：
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${advertising.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="dataDate" value="<fmt:formatDate value="${advertising.dataDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="end"/>
		</div>
	</form:form>
	<div class="alert alert-info"><strong>${advertising.name} ${advertising.groupName} ${advertising.sku} ${advertising.keyword} Details</strong>&nbsp;&nbsp;&nbsp;<input class="btn" type="button" value="<spring:message code='sys_but_back'/>" id="reback"/></div>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr> 
				<th style="text-align: center;vertical-align: middle;">DataDate</th>
				<th style="text-align: center;vertical-align: middle;" class="sort clicks">Clicks</th>
				<th style="text-align: center;vertical-align: middle;" class="sort impressions">Impressions</th>
				<th style="text-align: center;vertical-align: middle;" class="sort ctr">CTR</th>
				<th style="text-align: center;vertical-align: middle;" class="sort totalSpend">Total <br/>Spend${currencySymbol }</th>
				<th style="text-align: center;vertical-align: middle;" class="sort averageCPC">Average<br/> CPC${currencySymbol }</th>
				<th style="text-align: center;vertical-align: middle;" class="sort ordersPlaced">Orders<br/> Placed</th>
				<th style="text-align: center;vertical-align: middle;" class="sort orderSales">Ordered <br/>Product<br/> Sales${currencySymbol }</th>
				<th style="text-align: center;vertical-align: middle;" class="sort conversion">Conversion</th>
				<th style="text-align: center;vertical-align: middle;" class="sort acos">ACos</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${detail}" var="adver">
				<tr>
					<td style="text-align: center;vertical-align: middle;"><fmt:formatDate value="${adver.dataDate}" pattern="yyyy-MM-dd"/></td>
					<td style="text-align: center;vertical-align: middle;">${adver.clicks}</td>
					<td style="text-align: center;vertical-align: middle;">${adver.impressions}</td>
					<td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${adver.ctr}" maxFractionDigits="2"/>%</td>
					<td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${adver.totalSpend}" maxFractionDigits="2"/></td>
					<td style="text-align: center;vertical-align: middle;">
						<c:if test="${not empty adver.sku}">
							<a href="#"  rel="popover" data-content="Your Max Cpc:${adver.maxCpcBid > 0?adver.maxCpcBid:''};Est 1 Page:${adver.onePageBid >0?adver.onePageBid:''}"><fmt:formatNumber value="${adver.averageCPC}" maxFractionDigits="2"/></a>
						</c:if>
						<c:if test="${empty adver.sku}">
							<fmt:formatNumber value="${adver.averageCPC}" maxFractionDigits="2"/>
						</c:if>
					</td>
					<td style="text-align: center;vertical-align: middle;"><a href="#"  rel="popover" data-content="Same:${adver.sameSkuOrdersPlaced};Other:${adver.otherSkuOrdersPlaced}">${adver.ordersPlaced}</a></td>
					<td style="text-align: center;vertical-align: middle;"><a href="#"  rel="popover" data-content="Same:<fmt:formatNumber value="${adver.sameSkuOrderSales}" maxFractionDigits="2"/>;Other:<fmt:formatNumber value="${adver.otherSkuOrderSales}" maxFractionDigits="2"/>"><fmt:formatNumber value="${adver.orderSales}" maxFractionDigits="2"/></a></td>
					<td style="text-align: center;vertical-align: middle;">
						<c:if test="${adver.clicks>0}">
							<fmt:formatNumber value=" ${adver.ordersPlaced*100/adver.clicks}" maxFractionDigits="2"/>%
						</c:if>
						<c:if test="${adver.clicks==0}">
							0%
						</c:if>
					</td>
					<td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${adver.acos}" maxFractionDigits="2"/>%</td>
				</tr>
			</c:forEach>		
		</tbody>
	</table>
</body>
</html>
