<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>OutsidePromotionList</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style>
	table {table-layout:fixed}
	td {word-break:break-all;}
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
			
			$("#isCheck").on("click",function(){
				if(this.checked){
					$("input[name='isCheck']").val("1");
				}else{
					$("input[name='isCheck']").val("0");
				}
				$("#searchForm").submit();
			});
			
			
			$("#website").change(function(){
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
		<li class="active"><a href="${ctx}/amazoninfo/outsidePromotion/list">站外促销分析列表</a></li>
		<shiro:hasPermission name="amazoninfo:outsidePromotion:edit"><li><a href="${ctx}/amazoninfo/outsidePromotion/add">新增站外促销分析</a></li></shiro:hasPermission>
		<shiro:hasPermission name="amazoninfo:outsidePromotion:edit"><li><a href="${ctx}/psi/product/listDict?type=website">站外平台信息维护</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="outsidePromotion" action="${ctx}/amazoninfo/outsidePromotion" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<label>促销code：</label><input type="text" name="promotionCode" value="${outside.promotionCode}" style="width:150px"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<label>asin：</label><input type="text" name="asin" value="${outside.asin}" style="width:150px"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<label>产品名：</label><input type="text" name="productName" value="${outside.productName}" style="width:150px"/>
		&nbsp;&nbsp;&nbsp;&nbsp;
		<label>创建日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="startDate" value="<fmt:formatDate value="${outside.startDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${outside.endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
		&nbsp;&nbsp;
			<label>与我相关：</label><input type="checkbox"  id="isCheck" value="${isCheck}" ${isCheck eq '1' ?'checked':'' }/>
			<input  name="isCheck" type="hidden" value="${isCheck}"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
				   <th style="width:3%;">序号</th>
				   <th style="width:3%">国家</th>
				   <th style="width:8%">折扣码</th>
				   <th style="width:15%">TrackId</th>
				   <th style="width:10%;">型号</th>
				   <th style="width:8%;">促销形式</th>
				   <th style="width:6%;">开始日期</th>
				   <th style="width:6%;">结束日期</th>
				   <th style="width:5%;">平台经费</th>
				   <th style="width:5%;">样品提供</th>
				   <th style="width:10%;">操作</th>
				   </tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="outsidePromotion">
			<tr>
				<td>${outsidePromotion.id}</td>
				<td>${outsidePromotion.country eq 'com'?'us':outsidePromotion.country}</td>
				<td>${outsidePromotion.promotionCode}</td>
				<td>${outsidePromotion.trackId}</td>
				<td>${outsidePromotion.productName}</td>
				<td>${outsidePromotion.buyerGets}</td>
				<td><fmt:formatDate value="${outsidePromotion.startDate}" pattern="yyyy-MM-dd"/> </td>
				<td><fmt:formatDate value="${outsidePromotion.endDate}" pattern="yyyy-MM-dd"/> </td>
				<td>${outsidePromotion.platformFunds}</td>
				<td>${outsidePromotion.sampleProvided}</td>
				<td>
				 <a href="${ctx}/amazoninfo/outsidePromotion/analysis?id=${outsidePromotion.id}">分析</a>
				 &nbsp;
				  <a href="${ctx}/amazoninfo/outsidePromotion/compare?id=${outsidePromotion.id}">对比</a>
				 &nbsp;
				  <a href="${ctx}/amazoninfo/outsidePromotion/refresh?id=${outsidePromotion.id}">刷新</a>
				 &nbsp;
				<shiro:hasPermission name="amazoninfo:outsidePromotion:edit">
					<a href="${ctx}/amazoninfo/outsidePromotion/edit?id=${outsidePromotion.id}">编辑站点</a>&nbsp;
			        <a href="${ctx}/amazoninfo/outsidePromotion/delete?id=${outsidePromotion.id}" onclick="return confirmx('确认要删除该站外促销分析吗？', this.href)">删除</a>
				</shiro:hasPermission>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
