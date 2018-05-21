]<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>权限管理</title>
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
			"iDisplayLength" : 15,
			"aLengthMenu" : [ [ 15, 30, 60, 100, -1 ],
					[ 15, 30, 60, 100, "All" ] ],
			"bScrollCollapse" : true,
			"oLanguage" : {
				"sLengthMenu" : "_MENU_ 条/页"
			},
			"ordering" : true,
			"aaSorting": [[ 1, "asc" ]]
		});
	});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
	    <li class="active"><a href="${ctx}/sys/role/positionList">权限分配列表</a></li>
	    <li><a href="${ctx}/sys/role/findRoleUser">用户列表</a></li>
	    <li><a href="${ctx}/sys/role/findSecondRoleUser">权限借调列表</a></li>
	</ul>
	<tags:message content="${message}"/>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th>名称</th><th>部门</th><th>类型</th><th style="width:55%">用户</th><th>操作</th></tr></thead>
		<tbody>
		<c:forEach items="${roleSet}" var="role">
			<tr>
				<td>${role.name}</td>
				<td>${role.office.name}</td>
				<td>
					<c:if test="${'1' eq role.type}">岗位</c:if>
					<c:if test="${'0' eq role.type}">普通角色</c:if>
					<c:if test="${'2' eq role.type}">特殊角色</c:if>
				</td>
				<td>
					<c:if test="${not empty role.userList }">
						${role.userNames}
					</c:if>
				</td>
			    <td>
					<a href="${ctx}/sys/role/positionAssign?id=${role.id}&type=1">分配</a>
					&nbsp;&nbsp;
					<a href="${ctx}/sys/role/positionAssign?id=${role.id}&type=2">借调</a>
					&nbsp;&nbsp;
					<a target="_blank" href="${ctx}/sys/role/viewPermissions?type=1&id=${role.id}">查看权限</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>