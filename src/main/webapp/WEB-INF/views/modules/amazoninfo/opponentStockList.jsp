<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>竞争对手差额显示</title>
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
		});
	
	</script> 
</head>
<body>
<div style="float:left;width:98%;margin:10px 0px 10px 0px;" class="alert alert-info"><strong>Tips:月销量推算为连续两天有库存,并且后一天比前一天大,然后取差额平均,最后乘上30。</strong></div>
<div style="float:left;width:98%;margin-bottom:20px;text-align:left;font-weight: bold;font-size: 20px" ><a target="_black" href="http://www.amazon.de/dp/${asin}">${title}</a></div>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:5%">序号</th><th style="width:10%">数据日期</th><th style="width:10%">当天库存</th><th style="width:10%">相邻两天的差值</th></tr></thead>
		<tbody>
		<c:forEach items="${list}" var="oppo" varStatus="i">
			<tr>
				<td>${i.index+1}</td>
				<td><fmt:formatDate value="${oppo.dataDate}" pattern="yyyy-MM-dd"/></td>
				<td>
				<c:choose>
					<c:when test="${oppo.quantity>0}">${oppo.quantity}</c:when>
					<c:otherwise>抓取异常(代号：${oppo.quantity})</c:otherwise>
				</c:choose>
				</td>
				<td>${oppo.diffQuantity}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="form-actions" style="width:100%;text-align:center;pading-bottom:10px">
		<input id="btnCancel" class="btn" type="button" value="Back" onclick="history.go(-1)"/>
	</div>
</body>
</html>
