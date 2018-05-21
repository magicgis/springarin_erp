<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>退货检测登记管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	
		$(document).ready(function() {
			$("#country").on("click",function(){
				$("#searchForm").submit();
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
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a  href="${ctx}/amazoninfo/returnTest/form">ReturnTestList</a></li>
		<li><a href="${ctx}/amazoninfo/returnTest/form">Add ReturnTest</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="returnTest" action="${ctx}/amazoninfo/returnTest/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>ProductName：</label> <input type="text" name="productName" value="${returnTest.productName}" />
		<label>Create Date：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${returnTest.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="updateDate" value="<fmt:formatDate value="${returnTest.updateDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="Search"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:5%">No</th><th style="width:15%">ProductName</th><th style="width:10%">Sku</th><th style="width:10%">TestQuantity</th><th style="width:10%">ReasonDetail</th><th style="width:10%">CreateUser</th><th style="width:10%">CreateDate</th><th style="width:15%">Status</th><th>Operation</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="returnTest">
			<tr>
				<td>${returnTest.id}</td>
				<td>${returnTest.productName}</td>
				<td>${returnTest.sku}</td>
				<td>${returnTest.quantity}</td>
				<td>${returnTest.reason}</td>
				<td>${returnTest.createUser.name}</td>
				<td><fmt:formatDate value="${returnTest.createDate}" pattern="yyyy-MM-dd"/> </td>
				<td>
					<c:if test="${returnTest.testSta eq '0'}"><span class="label label-success">Created</span></c:if>
					<c:if test="${returnTest.testSta eq '1'}"><span class="label label-warning">In-bounded</span></c:if>
					<c:if test="${returnTest.testSta eq '2'}"><span class="label label-inverse">Cancel</span></c:if>
				</td>
				<td>
					<c:if test="${returnTest.createUser.id eq fns:getUser().id && returnTest.testSta eq '0'}">
						<a class="btn btn-small" href="${ctx}/amazoninfo/returnTest/form?id=${returnTest.id}">Edit</a>&nbsp;&nbsp;
					</c:if>
					<a class="btn btn-small" href="${ctx}/amazoninfo/returnTest/view?id=${returnTest.id}">View</a>&nbsp;&nbsp;
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
