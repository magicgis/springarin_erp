<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品目录扫描管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	
		$(document).ready(function() {
			
			$("#isCheck").on("click",function(){
				if(this.checked){
					$("input[name='isCheck']").val("1");
				}else{
					$("input[name='isCheck']").val("0");
				}
				$("#searchForm").submit();
			});
			
			
			$("input[name='directorySta']").on("change",function(){
				$("#searchForm").submit();
			});
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
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
		<li class="${empty productDirectory.country ?'active':''}"><a class="countryHref" href="#" key="">所有</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${productDirectory.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
		<li><a href="${ctx}/amazoninfo/productDirectory/form">添加产品目录</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="productDirectory" action="${ctx}/amazoninfo/productDirectory/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input name="country" type="hidden" value="${productDirectory.country}"/>
		<label>Subject/Url：</label> <input type="text" name="url" value="${productDirectory.url}" />
		&nbsp;&nbsp;&nbsp;&nbsp;
		<label>目录状态：</label>
		<select name="directorySta" style="width:150px">
			<option value="0" ${productDirectory.directorySta eq '0'?'selected':''} >正常</option>
			<option value="1" ${productDirectory.directorySta eq '1'?'selected':''}>已取消</option>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<label>冻结状态：</label>
		<select name="lockSta" style="width:150px">
			<option value="0" ${productDirectory.lockSta eq '0'?'selected':''}>非冻结</option>
			<option value="1" ${productDirectory.lockSta eq '1'?'selected':''}>冻结</option>
		</select> 
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<label>与我相关：</label><input type="checkbox"  id="isCheck" value="${isCheck}" ${isCheck eq '1' ?'checked':'' }/>
			<input  name="isCheck" type="hidden" value="${isCheck}"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="Search"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:5%">No</th><th style="width:10%">Subject</th><th style="width:10%">Country</th><th style="width:10%">Url</th><th style="width:15%">备注</th><th style="width:10%">CreateUser</th><th style="width:10%">CreateDate</th><th style="width:5%">冻结状态</th><th style="width:5%">系统自动冻结</th><th style="width:5%">目录状态</th><th>Operation</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="productDirectory">
			<tr>
				<td>${productDirectory.id}</td>
				<td>${productDirectory.subject}</td>
				<td>${fns:getDictLabel(productDirectory.country,'platform', '')}</td>
				<td><a target="_blank" href="${productDirectory.url}" tips="${productDirectory.url}">查看</a></td>
				<td>${productDirectory.remark}</td>
				<td>${productDirectory.createUser.name}</td>
				<td><fmt:formatDate value="${productDirectory.createDate}" pattern="yyyy-MM-dd"/> </td>
				<td>
					<c:if test="${productDirectory.lockSta eq '0'}"><span class="label label-success">非冻结</span></c:if>
					<c:if test="${productDirectory.lockSta eq '1'}"><span class="label label-inverse">冻结</span></c:if>
				</td>
				<td>
					<c:if test="${productDirectory.lockSta eq '0' }">
						剩${7-fns:pastDays(productDirectory.activeDate)}天
					</c:if>
				</td>
				<td>
					<c:if test="${productDirectory.directorySta eq '0'}"><span class="label label-success">正常</span></c:if>
					<c:if test="${productDirectory.directorySta eq '1'}"><span class="label label-inverse">已取消</span></c:if>
				</td>
				<td>
					<c:if test="${productDirectory.createUser.id eq fns:getUser().id && productDirectory.directorySta eq '0'}">
						<a class="btn btn-small" href="${ctx}/amazoninfo/productDirectory/form?id=${productDirectory.id}">编辑</a>&nbsp;&nbsp;
						<a class="btn btn-small" href="${ctx}/amazoninfo/productDirectory/cancel?id=${productDirectory.id}" onclick="return confirmx('确认要取消该目录扫描吗？', this.href)">取消</a>&nbsp;&nbsp;
					</c:if>
					<a class="btn btn-small" href="${ctx}/amazoninfo/productDirectory/unLock?id=${productDirectory.id}">解冻7天</a>&nbsp;&nbsp;
					<a class="btn btn-small" href="${ctx}/amazoninfo/productDirectoryComment/list?directoryId=${productDirectory.id}">分析</a>&nbsp;&nbsp;
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
