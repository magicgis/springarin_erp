<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品变更管理</title>
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
				"aaSorting": [[ 8, "desc" ]]
			});
			
			$("#status").change(function(){
				$("#searchForm").submit();
			});
		});

		function displaySuggestion(id, permission){
			var suggestionDiv = $("#suggestionDiv");
			$("#permission").val(permission);
			$("#id").val(id);
			suggestionDiv.modal();
		}
	</script> 
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/psi/productImprovement">产品变更列表</a></li>
		<shiro:hasPermission name="psi:productImprovement:edit">
			<li><a href="${ctx}/psi/productImprovement/form">产品变更申请</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="productImprovement" action="${ctx}/psi/productImprovement/" method="post" class="breadcrumb form-search" >
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<label>产品 ：</label>
		<input type="text" name="productName" value="${productImprovement.productName}" style="width:150px"/> 
		&nbsp;&nbsp;
		变更状态:
		<select id="status" style="width: 120px" name="status">
				<option value="">--全部(非取消)--</option>
				<option value="1" ${'0' eq productImprovement.status?'selected':''}>意见收集中</option>	
				<option value="2" ${'2' eq productImprovement.status?'selected':''}>待审批</option>	
				<option value="3" ${'3' eq productImprovement.status?'selected':''}>审批通过</option>	
				<option value="4" ${'4' eq productImprovement.status?'selected':''}>已取消</option>			
	    </select>
	    &nbsp;&nbsp;
	          创建时间：
	    <input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${productImprovement.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${productImprovement.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th>序号</th>
				<th style="width:15%">产品</th>
				<th>紧急度</th>
				<th style="width:15%">变更原因</th>
				<th style="width:15%">变更前说明</th>
				<th style="width:15%">变更后说明</th>
				<th>变更状态</th>
				<th>变更时间</th>
				<th>创建时间</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="productImprovement">
			<tr>
				<td>${productImprovement.id}</td>
				<td>
					<c:forEach items="${fn:split(productImprovement.productName,',')}" var ="pName" varStatus="i">
						<a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${pName}">${pName}</a>
						${i.last?'':'<br/>'}
					</c:forEach>
					(${productImprovement.line }线)
				</td>
				<td>
					<c:if test="${'1' eq productImprovement.type }">一般</c:if>
					<c:if test="${'2' eq productImprovement.type }">紧急</c:if>
					<c:if test="${'3' eq productImprovement.type }">特急</c:if>
				</td>
				<td><a rel="popover" data-content="${productImprovement.reason}">${empty fn:substring(productImprovement.reason,0,30)?'Empty':fn:substring(productImprovement.reason,0,30)}</a></td>
				<td><a rel="popover" data-content="${productImprovement.perRemark}">${empty fn:substring(productImprovement.perRemark,0,30)?'Empty':fn:substring(productImprovement.perRemark,0,30)}</a></td>
				<td><a rel="popover" data-content="${productImprovement.afterRemark}">${empty fn:substring(productImprovement.afterRemark,0,30)?'Empty':fn:substring(productImprovement.afterRemark,0,30)}</a></td>
				<td>
					<c:if test="${'0' eq productImprovement.status }">新建</c:if>
					<c:if test="${'1' eq productImprovement.status }">意见收集中</c:if>
					<c:if test="${'2' eq productImprovement.status }">待审批</c:if>
					<c:if test="${'3' eq productImprovement.status }">审批通过</c:if>
					<c:if test="${'4' eq productImprovement.status }">已取消</c:if>
				</td>
				<td><fmt:formatDate value="${productImprovement.improveDate}" pattern="yyyy-MM-dd"/></td>
				<td><fmt:formatDate value="${productImprovement.createDate}" pattern="yyyy-MM-dd"/></td>
				<td>
					<c:if test="${productImprovement.createBy.id eq fns:getUser().id && '0' eq productImprovement.status}">
						<a class="btn btn-small btn-primary" href="${ctx}/psi/productImprovement/delete?id=${productImprovement.id}" onclick="return confirmx('确认要删除该记录吗？', this.href)">删除</a>
						<a class="btn btn-small btn-primary" href="${ctx}/psi/productImprovement/form?id=${productImprovement.id}">编辑</a>
					</c:if>
					<c:if test="${'0' eq productImprovement.status || '1' eq productImprovement.status }">
						<shiro:hasPermission name="${productImprovement.permission}">
							<%--<input type="button" class="btn btn-small btn-primary" onclick="displaySuggestion('${productImprovement.id}','${productImprovement.permission}')" value="编辑意见"/> --%>
							<a class="btn btn-small btn-primary" href="${ctx}/psi/productImprovement/goApproval?type=1&id=${productImprovement.id}">编辑意见</a>
						</shiro:hasPermission>
					</c:if>
					<c:if test="${'2' eq productImprovement.status }">
						<shiro:hasPermission name="psi:productImprovement:approval">
							<a class="btn btn-small btn-primary" href="${ctx}/psi/productImprovement/goApproval?id=${productImprovement.id}">审批</a>
						</shiro:hasPermission>
					</c:if>
					<c:if test="${not empty productImprovement.filePath }">
						<a class="btn btn-small btn-primary" href="${ctx}/psi/productImprovement/downloadFile?id=${productImprovement.id}">下载附件</a>
					</c:if>
					<a class="btn btn-small btn-primary" href="${ctx}/psi/productImprovement/view?id=${productImprovement.id}">查看</a>
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
