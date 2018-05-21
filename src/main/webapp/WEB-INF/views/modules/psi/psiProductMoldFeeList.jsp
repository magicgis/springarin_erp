<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品模具费信息</title>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<%@ include file="/WEB-INF/views/include/dialog.jsp"%>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
		.footer {
		    padding: 20px 0;
		    margin-top: 20px;
		    border-top: 1px solid #e5e5e5;
		    background-color: #f5f5f5;
		}
		.modal.fade.in {
		 	top: 0%;
		}
		.modal{
			 width: auto;
			 margin-left:-500px 
		}
	</style>
	
	<script type="text/javascript">
		$(document).ready(function() {

			$("a[rel='popover']").popover({trigger:'hover'});
			
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
				"ordering" : true,
				"aoColumnDefs": [ { "bSortable": false, "aTargets": [ 0 ] }],
				"aaSorting": [[ 0, "desc" ]]
			});
		});
	</script> 
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/psi/productMoldFee">产品模具费列表</a></li>
		<shiro:hasPermission name="psi:moldFee:edit">
			<li><a href="${ctx}/psi/productMoldFee/form">新增模具费</a></li>
		</shiro:hasPermission>
	</ul>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>序号</th>
				<th>供应商</th>
				<th style="width:15%">产品</th>
				<th>模具费(CNY)</th>
				<th>是否返还</th>
				<th>返还数量</th>
				<th>创建人</th>
				<th>创建时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${list}" var="productMoldFee">
			<tr>
				<td>${productMoldFee.id}</td>
				<td>${productMoldFee.supplier.nikename}</td>
				<td>
					<c:forEach items="${fn:split(productMoldFee.productName,',')}" var ="pName" varStatus="i">
						<a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${pName}">${pName}</a>
						${i.last?'':'<br/>'}
					</c:forEach>
				</td>
				<td>${productMoldFee.moldFee}</td>
				<td>
					<c:if test="${'0' eq productMoldFee.returnFlag }">返还</c:if>
					<c:if test="${'1' eq productMoldFee.returnFlag }">不返还</c:if>
				</td>
				<td>${productMoldFee.returnNum}</td>
				<td>${productMoldFee.createBy.name}</td>
				<td><fmt:formatDate value="${productMoldFee.createDate}" pattern="yyyy-MM-dd"/></td>
				<td>
					<c:if test="${productMoldFee.createBy.id eq fns:getUser().id}">
						<a class="btn btn-small btn-primary" href="${ctx}/psi/productMoldFee/form?id=${productMoldFee.id}">编辑</a>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<%--<div class="pagination">${page}</div> --%>
	
	<div id="suggestionDiv" data-backdrop="static" class="modal hide fade" tabindex="-1" data-width="850">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3>产品变更意见</h3>
		</div>
		<form id="addSuggestion" action="${ctx}/psi/productImprovement/saveSuggestion" method="post" onkeydown="if(event.keyCode==13)return false;" >
		<div class="modal-body">
			<table class="table-striped table-bordered table-condensed ajaxtable">
				<tr>
					<td>处理意见</td>
					<td>
						<textarea name="content" rows="4" maxlength="200" class="input-xxlarge"></textarea>
						<input type="hidden" id="permission" name="permission"/>
						<input type="hidden" id="id" name="id"/>
					</td>
				</tr>
			</table>
		</div>
		<div class="modal-footer">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="提  交"/>&nbsp;&nbsp;&nbsp;
			<button type="button" data-dismiss="modal" class="btn btn-primary">关  闭</button>
		</div>
		</form>
	</div>
</body>
</html>
