<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品评论监控管理</title>
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
		<li class="${empty opponentAsin.country ?'active':''}"><a class="countryHref" href="#" key="">总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${opponentAsin.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
		<li><a href="${ctx}/amazoninfo/opponentAsin/form">新建对手产品销量监控</a></li>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/rankSalesAnalyse">排名销量分析</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="opponentAsin" action="${ctx}/amazoninfo/opponentAsin/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input name="country" type="hidden" value="${opponentAsin.country}"/>
		<label>Prouct Name：</label>	<input name="productName" type="text" maxlength="50" class="input-small" value="${opponentAsin.productName}"/>&nbsp;&nbsp;&nbsp;&nbsp;
		<label>Create Date：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${opponentAsin.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${opponentAsin.endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			&nbsp;&nbsp;&nbsp;&nbsp;<label>状态：</label>
			<select name="state" style="width:150px" class="state">
				<option value="" >All</option>
				<option value="0" ${opponentAsin.state eq '0'?'selected':''} >已取消</option>
				<option value="1" ${opponentAsin.state eq '1'?'selected':''}>正在监控</option>
			</select> 
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			<label>与我相关：</label><input type="checkbox"  id="isCheck" value="${isCheck}" ${isCheck eq '1' ?'checked':'' }/>
			<input  name="isCheck" type="hidden" value="${isCheck}"/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead><tr><th style="width:5%">No</th><th style="width:10%">Country</th><th style="width:15%">ProductName</th><th style="width:10%">Asin</th><th style="width:10%">预测近30天销量</th><th style="width:10%">CreateUser</th><th style="width:10%">CreateDate</th><th style="width:15%">Status</th><th>Operation</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="opponentAsin">
			<tr>
				<td>${opponentAsin.id}</td>
				<td>${fns:getDictLabel(opponentAsin.country, 'platform', defaultValue)}</td>
				<td>${opponentAsin.productName}</td>
				<td><a href="${opponentAsin.productLink}" target="_blank" >${opponentAsin.asin}</a></td>
				<td>
				<c:if test="${not empty saleMap[opponentAsin.country] && not empty saleMap[opponentAsin.country][opponentAsin.asin]}">
				<a class="btn btn-small" href="${ctx}/amazoninfo/opponentStock/list?asin=${opponentAsin.asin}&country=${opponentAsin.country}">
					${saleMap[opponentAsin.country][opponentAsin.asin]}
				</a>
				</c:if>
				</td>
				<td>${opponentAsin.createUser.name}</td>
				<td><fmt:formatDate value="${opponentAsin.createDate}" pattern="yyyy-MM-dd"/> </td>
				<td>
					<c:if test="${opponentAsin.state eq '1'}"><span class="label label-success">正在监控</span></c:if>
					<c:if test="${opponentAsin.state eq '0'}"><span class="label label-warning">已取消</span></c:if>
				</td>
				<td>
					<c:if test="${opponentAsin.createUser.id eq fns:getUser().id && opponentAsin.state eq '1'}">
						<a class="btn btn-small" href="${ctx}/amazoninfo/opponentAsin/cancel?id=${opponentAsin.id}">取消</a>&nbsp;&nbsp;
					</c:if>
					
					<a class="btn btn-small" href="${ctx}/amazoninfo/opponentStock/list?asin=${opponentAsin.asin}&country=${opponentAsin.country}">
					  查看销量
					</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
