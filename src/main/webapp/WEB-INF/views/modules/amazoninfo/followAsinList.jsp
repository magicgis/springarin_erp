<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品跟卖管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	
		$(document).ready(function() {
			$("#country").on("click",function(){
				$("#searchForm").submit();
			});
			
			$("#isCheck").on("click",function(){
				if(this.checked){
					$("input[name='isCheck']").val("1");
				}else{
					$("input[name='isCheck']").val("0");
				}
				$("#searchForm").submit();
			});
			
			
			
			$(".state").on("change",function(){
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
		<li ><a href="${ctx}/amazoninfo/followSeller">跟卖信息列表</a></li>
		<li class="active"><a href="#">监控跟卖产品列表</a></li>
		<li ><a href="${ctx}/amazoninfo/followAsin/form">新建产品跟卖监控</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="followAsin" action="${ctx}/amazoninfo/followAsin/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<label>平台：</label>
		<form:select path="country" style="width: 100px" id="country">
			<option value="" >全部</option>
			<c:forEach items="${fns:getDictList('platform')}" var="dic">
				<c:if test="${dic.value ne 'com.unitek'}">
					 <option value="${dic.value}" ${followSeller.country eq dic.value ?'selected':''}  >${dic.label}</option>
				</c:if>      
			</c:forEach>	
		</form:select>&nbsp;&nbsp;
		<label>Prouct Name：</label>	<input name="productName" type="text" maxlength="50" class="input-small" value="${followAsin.productName}"/>&nbsp;&nbsp;&nbsp;&nbsp;
		<label>Create Date：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${followAsin.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${followAsin.endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			&nbsp;&nbsp;&nbsp;&nbsp;<label>状态：</label>
			<select name="state" style="width:150px" class="state">
				<option value="" >All</option>
				<option value="0" ${followAsin.state eq '0'?'selected':''} >已取消</option>
				<option value="1" ${followAsin.state eq '1'?'selected':''}>正在监控</option>
			</select> 
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<label>与我相关：</label><input type="checkbox"  id="isCheck" value="${isCheck}" ${isCheck eq '1' ?'checked':'' }/>
			<input  name="isCheck" type="hidden" value="${isCheck}"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:5%">No</th><th style="width:10%">Country</th><th style="width:15%">ProductName</th><th style="width:10%">Asin</th><th style="width:10%">CreateUser</th><th style="width:10%">CreateDate</th><th style="width:15%">Status</th><th>Operation</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="followAsin">
			<tr>
				<td>${followAsin.id}</td>
				<td>${fns:getDictLabel(followAsin.country, 'platform', defaultValue)}</td>
				<td>${followAsin.productName}</td>
				<td><a href="${followAsin.productLink}" target="_blank" >${followAsin.asin}</a></td>
				<td>${followAsin.createUser.name}</td>
				<td><fmt:formatDate value="${followAsin.createDate}" pattern="yyyy-MM-dd"/> </td>
				<td>
					<c:if test="${followAsin.state eq '1'}"><span class="label label-success">正在监控</span></c:if>
					<c:if test="${followAsin.state eq '0'}"><span class="label label-warning">已取消</span></c:if>
				</td>
				<td>
					<c:if test="${followAsin.createUser.id eq fns:getUser().id && followAsin.state eq '1'}">
						<a class="btn btn-small" href="${ctx}/amazoninfo/followAsin/cancel?id=${followAsin.id}">取消</a>&nbsp;&nbsp;
					</c:if>
					<c:if test="${followAsin.createUser.id eq fns:getUser().id && followAsin.state eq '0'}">
						<a class="btn btn-small" href="${ctx}/amazoninfo/followAsin/noCancel?id=${followAsin.id}">恢复监控</a>&nbsp;&nbsp;
					</c:if>
					
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
