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
			$("#productSelect").select2();
		});
	</script>
<table id="contentTable" style="height:90px;" class="table table-bordered table-condensed">
	<thead>
		  <tr> 
			  <th width="200px" style="text-align:center;font-size: large" >SKU</th> 
			  <th width="150px" style="font-size: large;">产品</th> 
			  <c:if test="${country ne 'ebay' }"> <th width="100px" style="font-size: large;">使用Fnsku</th></c:if>
		  </tr>
	</thead>
	<tbody>
		<tr style="height:20px">
		<td style="font-weight:bold;font-size: large;text-align:center"><span id='choiceSku' style='font-weight:bold;font-size: large;'>${sku}</span></td>
		<td >
		<select id="productSelect" ><option value=""/>
		<c:forEach items="${products}" var="product"><option value="${product[0]}">${product[1]}</option></c:forEach>
		</select>
		</td>
		<c:if test="${country ne 'ebay' }"><td style='text-align:center'><input type='checkBox' class='isFnsku'/></td></c:if>
		</tr> 
	</tbody>
</table>

