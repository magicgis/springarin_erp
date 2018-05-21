]<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>角色管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
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
		$(function(){
			$("#office").change(function(){
				$("#searchForm").submit();
			});
			
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
		<li class="active"><a href="${ctx}/sys/role/">角色列表</a></li>
		<shiro:hasPermission name="sys:role:edit"><li><a href="${ctx}/sys/role/form">角色添加</a></li></shiro:hasPermission>
	</ul>
	<tags:message content="${message}"/>
	
	<form:form id="searchForm" modelAttribute="role" action="${ctx}/sys/role" method="post" class="breadcrumb form-search" cssStyle="height: 30px;">
		<label>部门：</label>
		<select style="width:150px;" id="office" name="office.id">
			<option value="" ${role.office.id eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
			<c:forEach items="${offices}" var="office">
				 <option value='${office.id}' ${role.office.id eq office.id ?'selected':''} >${office.name}</option>;
			</c:forEach>
		</select>
	</form:form>
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr><th>角色名称</th><th>归属机构</th><th>角色类型</th><th>数据范围</th>
			<shiro:hasPermission name="sys:role:edit"><th>操作</th></shiro:hasPermission></tr>
		</thead>
		<tbody>
		<c:forEach items="${list}" var="role">
			<tr>
				<td><a href="form?id=${role.id}">${role.name}</a></td>
				<td>${role.office.name}</td>
				<td>
					<c:if test="${'1' eq role.type}">岗位</c:if>
					<c:if test="${'0' eq role.type}">普通角色</c:if>
					<c:if test="${'2' eq role.type}">特殊角色</c:if>
				</td>
				<td>${fns:getDictLabel(role.dataScope, 'sys_data_scope', '无')}</td>
				<shiro:hasPermission name="sys:role:edit"><td>
					<a href="${ctx}/sys/role/assign?id=${role.id}">分配</a>
					<a href="${ctx}/sys/role/form?id=${role.id}">修改</a>
					<a href="${ctx}/sys/role/delete?id=${role.id}" onclick="return confirmx('确认要删除该角色吗？', this.href)">删除</a>
				</td></shiro:hasPermission>	
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>