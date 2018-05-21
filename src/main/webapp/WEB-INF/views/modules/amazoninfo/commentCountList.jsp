<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Return Count</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
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
	<br/>
	<h3 style="text-align: center;vertical-align: middle">评论信息详细列表</h3>
	<table  id="contentTable" class="table table-striped table-bordered table-condensed" style="margin-left:110px;width:85%;" >
		<tbody>
			<tr>
				<th>差评评论</th>
			</tr>
			<tr>
				<td>一共有${eventMap['size']}条差评，评价订单ID为：${eventMap['orderId'] }</td>
			</tr>
			
			<tr>
				<th>所有订单</th>
			</tr>
			<tr>
				<td>一共有${map['size']}条订单，评价订单ID为：${map['orderId'] }</td>
			</tr>
			
		</tbody>
		
	</table>
	
</body>
</html>
