<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊帖子信息管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript" src="${ctxStatic}/raty-master/lib/jquery.raty.js" ></script>
	<style>
		.rating-star {
		width: 0;
		margin: 0;
		padding: 0;
		border: 0;
		
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
			$(document).ready(function() {
				$(".countryHref").click(function(){
					$("input[name='country']").val($(this).attr("key"));
					$("#searchForm").submit();
				});
			});
			$("#treeTable").treeTable({expandLevel : 1});
			// 表格排序
		
			
            $('.star').raty({ readOnly: true,path:'${ctxStatic}/raty-master/lib/images',score: function() {
			    return $(this).attr('data-score');
			 } });
			
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
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${amazonPostsDetail.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
	
	</ul>
	<form:form id="searchForm" modelAttribute="amazonPostsDetail" action="${ctx}/amazoninfo/amazonPortsDetail/" method="post" class="breadcrumb form-search">
		<div style="height: 30px">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
			<label>名称/Asin ：</label><form:input path="asin" htmlEscape="false" maxlength="50" class="input-small"/>
			&nbsp;&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="queryTime" value="<fmt:formatDate value="${amazonPostsDetail.queryTime}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			<input  name="country" type="hidden" value="${amazonPostsDetail.country}" />
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="treeTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<th style="width: 15%">product_name</th>
			<th style="width: 10%">Star</th>
			<th style="width: 10%">Asin</th>
			<th style="width: 10%">Ean</th>
			<th style="width: 15%">Sku</th>
			<th style="width: 10%">Size</th>
			<th style="width: 10%">Color</th>
			<th style="width: 10%">Parent_Sku</th>
			<th style="width: 5%">Operate</th>
		<tbody>
		<c:forEach items="${page.list}" var="amazonPortsDetail">
			<tr id="${fn:length(amazonPortsDetail.children)==0?'nochild':''}${amazonPortsDetail.id}">
				<td ><c:if test="${fn:length(amazonPortsDetail.children)>0}"><b style="color:#6CA6CD">&nbsp;&nbsp;<a  href="${ctx}/amazoninfo/amazonPortsDetail/viewParent?id=${amazonPortsDetail.id}" target="_blank">组合贴</a>&nbsp;&nbsp;</b></c:if>
				<c:if test="${fn:length(amazonPortsDetail.children)==0}"><a  href="${ctx}/amazoninfo/amazonPortsDetail/view?id=${amazonPortsDetail.id}" target="_blank">${not empty amazonPortsDetail.productName?amazonPortsDetail.productName:'产品名称未匹配'}</a></c:if></td>
				<td><div class="star" data-score="${amazonPortsDetail.star}"></div></td>
				<td ><a href="http://www.amazon.${amazonPortsDetail.country eq 'jp' || amazonPortsDetail.country eq 'uk'?'co.':''}${amazonPortsDetail.country eq 'com.unitek'?'com':amazonPortsDetail.country}/dp/${amazonPortsDetail.asin}" target="_blank">${amazonPortsDetail.asin}</a></td>
			    <td>${amazonPortsDetail.ean}</td>
			    <td><c:if test="${not empty amazonPortsDetail.sku}">
				    <c:forEach items="${fn:split(amazonPortsDetail.sku,',')}" var="sku" varStatus="i">
				    <c:if test="${fn:length(amazonPortsDetail.children)>0}">
				      <c:if test="${i.count eq 1}"><a href="#" title="${amazonPortsDetail.sku}">${sku}</a></c:if>
				    </c:if>
				     <c:if test="${fn:length(amazonPortsDetail.children)==0}">
				        <c:if test="${i.count eq 1}"><a href="#" title="${amazonPortsDetail.sku}">${sku}...</a></c:if>
				    </c:if>
				    </c:forEach>
				  </c:if>
			  </td>
				<td>${amazonPortsDetail.size}</td>
				<td>${amazonPortsDetail.color}</td>
				
				<%-- <td><a href="http://www.amazon.${amazonPortsDetail.country eq 'jp' || amazonPortsDetail.country eq 'uk'?'co.':''}${amazonPortsDetail.country eq 'com.unitek'?'com':amazonPortsDetail.country}/dp/${amazonPortsDetail.parentPortsDetail.asin}" target="_blank">${amazonPortsDetail.parentPortsDetail.asin}</a></td>
			 --%>
			 <td><c:if test="${not empty amazonPortsDetail.parentPortsDetail }">
			    <a  href="${ctx}/amazoninfo/amazonPortsDetail/viewParent?id=${amazonPortsDetail.parentPortsDetail.id}" target="_blank">
			       ${amazonPortsDetail.parentPortsDetail.sku }
			    </a>
			  </c:if></td>
			 <td> 
				      <c:if test="${fn:length(amazonPortsDetail.children)>0}"> <a class="btn btn-warning btn-small" target="_blank" onClick="return confirm('建议在帖子编辑功能中修改帖子保证导出每项内容是实时的,帖子父帖因亚马逊原因可能获取不到最新的,请自行核查,确定导出?');" href="${ctx}/amazoninfo/amazonPortsDetail/exportParentModelExcel?country=${amazonPortsDetail.country }&asin=${amazonPortsDetail.asin}">Export</a>
					  </c:if>
					  <c:if test="${fn:length(amazonPortsDetail.children)==0}">
					    <a class="btn btn-warning btn-small" target="_blank" onClick="return confirm('建议在帖子编辑功能中修改帖子保证导出每项内容是实时的,帖子父帖因亚马逊原因可能获取不到最新的,请自行核查,确定导出?');"  href="${ctx}/amazoninfo/amazonPortsDetail/exportModelExcel?productName=${amazonPortsDetail.productName }&country=${amazonPortsDetail.country }&asin=${amazonPortsDetail.asin}">Export</a>
					  </c:if>
				</td>
			 </tr>
			<c:if test="${fn:length(amazonPortsDetail.children)>0}">
				<c:forEach items="${amazonPortsDetail.children}" var="child">
					<tr id="${child.id}"  pid="${amazonPortsDetail.id}" >
						<td><a  href="${ctx}/amazoninfo/amazonPortsDetail/view?id=${child.id}" target="_blank">${not empty child.productName?child.productName:'产品名称未匹配'}</a></td>
						<td><div class="star" data-score="${child.star}"></div></td>
						<td ><a href="http://www.amazon.${child.country eq 'jp' || child.country eq 'uk'?'co.':''}${child.country eq 'com.unitek'?'com':child.country}/dp/${child.asin}" target="_blank">${child.asin}</a></td>
						<td>${child.ean}</td>
			            <td><c:if test="${not empty child.sku}">
						    <c:forEach items="${fn:split(child.sku,',')}" var="sku" varStatus="i">
						      <c:if test="${i.count eq 1}"><a href="#" title="${child.sku}">${sku}...</a></c:if>
						    </c:forEach>
						  </c:if>
				        </td>
				        <td>${child.size}</td>
				        <td>${child.color}</td>
						<%-- <td ><a href="http://www.amazon.${child.country eq 'jp' || child.country eq 'uk'?'co.':''}${child.country eq 'com.unitek'?'com':child.country}/dp/${amazonPortsDetail.asin}" target="_blank">${amazonPortsDetail.asin}</a></td>
				 --%>	
				        <td>
				          <a  href="${ctx}/amazoninfo/amazonPortsDetail/viewParent?id=${amazonPortsDetail.id}" target="_blank">${amazonPortsDetail.sku}</a>
				        </td>
				        
				       <td> 
					    <a class="btn btn-warning btn-small" target="_blank" onClick="return confirm('建议在帖子编辑功能中修改帖子保证导出每项内容是实时的,帖子父帖因亚马逊原因可能获取不到最新的,请自行核查,确定导出?');"  href="${ctx}/amazoninfo/amazonPortsDetail/exportModelExcel?productName=${amazonPortsDetail.productName }&country=${amazonPortsDetail.country }&asin=${amazonPortsDetail.asin}">Export</a>
				    </td>
				  </tr>
				</c:forEach>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
