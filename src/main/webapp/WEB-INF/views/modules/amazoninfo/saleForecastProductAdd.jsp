<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			$("#product").select2();
		});
	</script>
	<br/>
		<select id="product" multiple class="multiSelect" style="width:100%;">
			<c:forEach items="${addProducts}" var="product">
				<option value="${product}">${product}</option>
			</c:forEach>
		</select>