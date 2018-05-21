<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>折扣预警管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	
		$(document).ready(function() {
			$("#supplier").on("click",function(){
				$("#searchForm").submit();
			});
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$(".open").click(function(e){
				if($(this).text()=='概要'){
					$(this).text('关闭');
				}else{
					$(this).text('概要');
				}
				var className = $(this).parent().find("input[type='hidden']").val();
				$("*[name='"+className+"']").toggle();
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
		<li class="active"><a href="#">供应商税点调整列表</a></li>
		<shiro:hasPermission name="psi:supplierTaxAdjust:edit">
		<li><a href="${ctx}/psi/psiSupplierTaxAdjust/add">新建供应商税点调整</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="psiSupplierTaxAdjust" action="${ctx}/psi/psiSupplierTaxAdjust/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>供应商：</label>
		<select style="width:150px;" id="supplier" name="supplier.id">
			<option value="" ${psiSupplierTaxAdjust.supplier eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
			<c:forEach items="${suppliers}" var="supplier" varStatus="i">
				 <option value='${supplier.id}' ${supplier.id eq  psiSupplierTaxAdjust.supplier.id?'selected':''}>${supplier.nikename}</option>;
			</c:forEach>
		</select>
		<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${psiSupplierTaxAdjust.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="reviewDate" value="<fmt:formatDate value="${psiSupplierTaxAdjust.reviewDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:5%">No</th><th style="width:8%">供应商</th><th style="width:20%">改前税点</th><th style="width:8%">改后税点</th><th style="width:8%">创建人</th><th style="width:8%">创建日期</th><th style="width:5%">状态</th><th style="width:20%">备注</th><th>操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="psiSupplierTaxAdjust">
			<tr>
				<td>${psiSupplierTaxAdjust.id}</td>
				<td>${psiSupplierTaxAdjust.supplier.nikename}</td>
				<td>${psiSupplierTaxAdjust.oldTax}</td>
				<td>${psiSupplierTaxAdjust.tax}</td>
				<td>${psiSupplierTaxAdjust.createUser.name}</td>
				<td><fmt:formatDate value="${psiSupplierTaxAdjust.createDate}" pattern="yyyy-MM-dd"/> </td>
				<td>
					<c:if test="${psiSupplierTaxAdjust.adjustSta eq '1'}"><span class="label label-warning">申请</span></c:if>
					<c:if test="${psiSupplierTaxAdjust.adjustSta eq '2'}"><span class="label label-success">已审核</span></c:if>
					<c:if test="${psiSupplierTaxAdjust.adjustSta eq '8'}"><span class="label label-inverse">已取消</span></c:if>
				</td>
				<td>${psiSupplierTaxAdjust.remark}</td>
				<td>
					<shiro:hasPermission name="psi:supplierTaxAdjust:review">
						<c:if test="${psiSupplierTaxAdjust.adjustSta eq '1'}">
							<a class="btn btn-small" href="${ctx}/psi/psiSupplierTaxAdjust/review?id=${psiSupplierTaxAdjust.id}">审核</a>&nbsp;&nbsp;
							<a class="btn btn-small" href="${ctx}/psi/psiSupplierTaxAdjust/cancel?id=${psiSupplierTaxAdjust.id}">取消</a>
						</c:if>
					</shiro:hasPermission>
					<c:if test="${psiSupplierTaxAdjust.createUser.id eq fns:getUser().id && psiSupplierTaxAdjust.adjustSta eq '1'}">
						<a class="btn btn-small" href="${ctx}/psi/psiSupplierTaxAdjust/cancel?id=${psiSupplierTaxAdjust.id}">取消</a>&nbsp;&nbsp;
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
