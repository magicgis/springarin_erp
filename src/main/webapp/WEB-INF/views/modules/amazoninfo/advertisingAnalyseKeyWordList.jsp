<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>广告报表</title>

	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		if(!(top)){
			top = self;			
		}	
		$(document).ready(function() {
			
			
			
		});
		
	
		
	</script>
</head>
<body>
	
	<!-- </div> -->
<div style="width:1000px;border:1px solid #ccc">
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;">关键字</th>
				<th style="text-align: center;vertical-align: middle;">类型</th>
                <c:forEach items="${xAxis}" var="xAxis">
                   <th style="text-align: center;vertical-align: middle;">${xAxis }</th>
                </c:forEach>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${keywordMap}" var="ads" varStatus="i">
			<tr >
				<th style="text-align: left;vertical-align: middle;" rowspan='3'>${ads.key}</th>
			    <th style="text-align: left;vertical-align: middle;">Clicks</th>
			    <c:forEach items="${xAxis}" var="xAxis">
                    <td style="text-align: center;vertical-align: middle;">${keywordMap[ads.key][xAxis].clicks }</td>
                </c:forEach>
			</tr>
			<tr>
				<%-- <th style="text-align: left;vertical-align: middle;">${ads.key}</th> --%>
			    <th style="text-align: left;vertical-align: middle;">Ads Quantity</th>
			     <c:forEach items="${xAxis}" var="xAxis">
                    <td style="text-align: center;vertical-align: middle;">${keywordMap[ads.key][xAxis].sameSkuOrdersPlaced }</td>
                </c:forEach>
			</tr>
			<tr>
				<%-- <th style="text-align: left;vertical-align: middle;">${ads.key}</th> --%>
				<th style="text-align: left;vertical-align: middle;">Ads Costs</th>
				 <c:forEach items="${xAxis}" var="xAxis">
                    <td style="text-align: center;vertical-align: middle;">${keywordMap[ads.key][xAxis].totalSpend }</td>
                </c:forEach>
			</tr>
		</c:forEach>	
		</tbody>
	</table>
</div>	
</body>
</html>
