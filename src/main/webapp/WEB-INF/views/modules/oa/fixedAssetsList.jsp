<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>固定资产管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
		$(document).ready(function() {
			$("#officeId,#workSta").on("click",function(){
				$("#searchForm").submit();
			});
			
			

			$("#contentTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 15,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"aoColumns": [
			          null,
			          null,
			          null,
			          null,
			          null,
			          null,
			          null,
			          null,
			          null,
			          null
				],
				"ordering" : true,
				 "aaSorting": [[ 6, "desc" ]]
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
	</script> 
	 <style type="text/css">
	  #imgtest{  position:absolute;
	         top:100px; 
	         left:200px; 
	         z-index:1; 
	         } 
	  </style>  
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="">固定资产列表</a></li>
		<shiro:hasPermission name="oa:fixedAssets:edit">
			<li ><a href="${ctx}/oa/fixedAssets/form">新增</a></li>
		</shiro:hasPermission>
	</ul>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:5%">序号</th><th style="width:10%">资产名称</th><th style="width:10%">型号</th><th style="width:10%">编号</th>
		<th style="width:10%" >存放地点</th><th style="width:10%" >保管方</th><th style="width:10%" >购买日期</th><th style="width:5%">状态</th><th style="width:10%">备注</th><th style="width:5%">操作</th></tr></thead>
		<tbody>
			<c:forEach items="${list}" var="fixedAssets">
				<tr>
					<td>${fixedAssets.id}</td>
					<td>${fixedAssets.name}</td>
					<td>${fixedAssets.model}</td>
					<td>${fixedAssets.billNo}</td>
					<td>${fixedAssets.place}</td>
					<td>
						<c:choose>
							<c:when test="${not empty fixedAssets.ownerUser}">${fixedAssets.ownerUser.name}</c:when>
							<c:when test="${not empty fixedAssets.ownerOffice}">${fixedAssets.ownerOffice.name}</c:when>
							<c:otherwise>公司</c:otherwise>
						</c:choose>  
					</td>
					<td><fmt:formatDate value="${fixedAssets.buyDate}" pattern="yyyy-MM-dd"/> </td>
					<td>${fixedAssets.fixedSta}</td>
					<td>${fixedAssets.remark}</td>
					<td>
						<shiro:hasPermission name="oa:fixedAssets:edit">
		    				<a class="btn btn-small" href="${ctx}/oa/fixedAssets/form?id=${fixedAssets.id}">修改</a>&nbsp;&nbsp;&nbsp;&nbsp;
	    				</shiro:hasPermission>
					</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>
