<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊后台账号管理</title>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr {
			float: right;
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
		$(document).ready(function() {
			$("#country").change(function(){
				$("#searchForm").submit();
			});
			
			$("#contentTable").dataTable({
				"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 20,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
			    "aoColumnDefs": [
			                     { "bSortable": false, "aTargets": [ 5 ] }
			                    ],
				"aaSorting": [[ 1, "asc" ]]
			});
		});
		
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/amazoninfo/amazonUser/">亚马逊后台账号列表</a></li>
		<li><a href="${ctx}/amazoninfo/amazonUser/logList">亚马逊后台账号登录记录</a></li>
	</ul>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>平台</th>
				<th>账号</th>
				<th>备注</th>
				<th>最后修改人</th>
				<th>最后修改时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${list}" var="amazonUser">
			<tr>
				<td>
					${amazonUser.countryStr}
				</td>
				<td>
					${amazonUser.account}
				</td>
				<td>
					${amazonUser.roleName}
				</td>
				<td>
					${amazonUser.updateBy.name}
				</td>
				<td>
					<fmt:formatDate value="${amazonUser.updateDate}" pattern="yyyy-MM-dd HH:mm:ss" />
				</td>
				<td>
    				<a class="btn btn-info" href="${ctx}/amazoninfo/amazonUser/form?id=${amazonUser.id}">修改密码</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
