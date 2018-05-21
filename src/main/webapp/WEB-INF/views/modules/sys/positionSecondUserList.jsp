<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>用户借调权限</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr {
			min-height: 40px
		}
		
		.spanexl {
			float: left;
		 }
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
		}
	</style>
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
			$("#contentTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				 "aaSorting": [[ 0, "desc" ]]
			});
			
			
		});
	</script>
</head>
<body>

	<ul class="nav nav-tabs">
		<li><a href="${ctx}/sys/role/positionList">权限分配列表</a></li>
		<li><a href="${ctx}/sys/role/findRoleUser">用户列表</a></li>
		<li class="active"><a href="${ctx}/sys/role/findSecondRoleUser">权限借调列表</a></li>
	</ul>

	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>归属部门</th><th>姓名</th><th>借调权限</th><th>时间</th><th>操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page}" var="user">
			<tr>
				<td>${user[0]}</td>
				<td>${user[1]}</td>
				<td>${user[4]}</td>
				<td><fmt:formatDate value="${user[5]}" pattern="yyyy-MM-dd HH:mm"/></td>
				<td><a href="${ctx}/sys/role/outPosition?userId=${user[2]}&roleId=${user[3]}&type=3" 
						onclick="return confirmx('确认要将用户<b>[${user[1]}]</b>从<b>[${user[4]}]</b>借调权限回收吗？', this.href)">回收</a></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	
</body>
</html>