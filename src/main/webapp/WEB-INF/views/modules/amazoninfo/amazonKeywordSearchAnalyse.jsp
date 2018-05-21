<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%-- <%@include file="/WEB-INF/views/include/datatables.jsp" %>  --%>
<html>
<head>
<style type="text/css">
		.spanexr{ float:right;min-height:40px;padding-top: 5px}
		.spanexl{ float:left;}
		.blue{background-color:#D2E9FF;font-style: italic;font-weight: bold;}
</style>
  <script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();

		
		 $(document).ready(function() {
		/*    $("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap","sScrollX": "100%",
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
			 	"aoColumnDefs": [ { "bSortable": false, "aTargets": [ 0 ] }] , 
			 	"aaSorting": [[1, "desc" ]]
			}); */
		});	
		 
	</script>
</head>
<body>
<div style="width:480px;border:1px solid #ccc">
	    <table id="contentTable" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th>词</th>	
					<th>出现次数</th>		
					<th>占比(%)</th>			
				</tr>
			</thead>	
			<tbody>	
				<c:forEach items="${sysnMap}" var="sysn">
				    <c:if test="${'totalK' ne  sysn.key }">
				        <tr>
				          <td>${sysn.key }</td>
				          <td>${sysnMap[sysn.key] }</td>
				          <td><fmt:formatNumber value="${sysnMap[sysn.key]*100/sysnMap['totalK'] }" maxFractionDigits="2"/></td>
				        </tr>
				    </c:if>
				    
				</c:forEach>
			</tbody>
	   </table>
	</div>
</body>
</html>


	
	