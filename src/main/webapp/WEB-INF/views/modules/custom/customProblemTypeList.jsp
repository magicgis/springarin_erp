<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品问题管理</title>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@ include file="/WEB-INF/views/include/dialog.jsp"%>
	<meta name="decorator" content="default"/>
	<style type="text/css">
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

		$(document).ready(function() {

			$("a[rel='popover']").popover({html:true,trigger:'hover'});
			
			$("#productType").change(function(){
				$("#searchForm").submit();
			});
			
			$("#btnAdd").click(function(){
				window.location.href = "${ctx}/custom/productProblem/form";
			});
			
			$("#contentTable").dataTable({
				"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true
			});
			
		});
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="#">产品问题类型列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="problems" action="${ctx}/custom/productProblem/problems" method="post" class="breadcrumb form-search">
		<div style="height: 40px;line-height: 40px">
			<div style="height: 40px;">
				产品分类：<select name="productType" id="productType" style="width: 220px">
						<option value="" ${problems.productType eq ''?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<c:forEach items="${fns:getDictList('product_type')}" var="dic">
							<option value="${dic.value}" ${problems.productType eq dic.value?'selected':''}  >${dic.label}</option>
						</c:forEach>
				</select>&nbsp;&nbsp;
			     <input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>	
			     &nbsp;&nbsp;
			     <input id="btnAdd" class="btn btn-primary" type="button" value="<spring:message code="sys_but_add"/>"/>	
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
			   <th style="width: 10px">序号</th>
			   <th>产品类型</th>	
			   <th>问题类型</th>
			   <th><spring:message code="sys_label_tips_operate"/></th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${list}" var="problem" varStatus="i">
			<tr>
				<td style="text-align: center;vertical-align: middle;">${i.index+1}</td>
				<td>${problem.productType}</td>
				<td>${problem.problemType}</td>
				<td>
					<a class="btn btn-info btn-small" href="${ctx}/custom/productProblem/form?id=${problem.id}">编辑</a>
					&nbsp;&nbsp;
					<a class="btn btn-warning btn-small" href="${ctx}/custom/productProblem/delete?id=${problem.id}" onclick="return confirmx('确认要删除记录吗？', this.href)">删除</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</body>
</html>
