<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<!-- <meta name="decorator" content="default"/> -->
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
<div style="width:480px;border:1px solid #ccc">
	    <table class="table table-striped table-bordered table-condensed">
				<tbody>
					<tr>
						<td><b>购买数量</b></td>
						<td><b>折扣情况</b></td>	
					</tr>
					<c:forEach items="${checkMap}" var="check">
					   <c:if test="${check.key!=-1 }">
					       <tr>
					          <td>${check.key}</td>
					          <td>${checkMap[check.key]}</td>
					       </tr>
					   </c:if>
					    <c:if test="${check.key==-1 }">
					       <tr>
					          <td colspan='2'><span style='color:#ff0033;'><b>${checkMap[check.key]}</b></span></td>
					       </tr>
					   </c:if>
					</c:forEach>	
				</tbody>
		</table>
				
	</div>
</body>
</html>


	
	