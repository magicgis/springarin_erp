<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品管理</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		pre{
			border-style: none
		}
		.sort{color:#0663A2;cursor:pointer;}
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
			
			$('.progressbar').progressbar({//初始化进度条
				warningMarker: 100,//设置预警开始的位置
				dangerMarker: 100,//设置危险开始的位置
				maximum: 70,//设置进度条的总大小
				step: 10//设置每次进度条被渲染(更新)自动增加的数字
			});
			$('.finish').each(function(){
				var step = $(this).find("input").val();
				var i = step*10;
				$(this).find('.progressbar').progressbar('setPosition',i);
			});		
			
			// 表格排序
			var orderBy = $("#orderBy").val().split(" ");
			$("#contentTable th.sort").each(function(){
				if ($(this).hasClass(orderBy[0])){
					orderBy[1] = orderBy[1]&&orderBy[1].toUpperCase()=="DESC"?"down":"up";
					$(this).html($(this).html()+" <i class=\"icon icon-arrow-"+orderBy[1]+"\"></i>");
				}
			});
			$("#contentTable th.sort").click(function(){
				var order = $(this).attr("class").split(" ");
				var sort = $("#orderBy").val().split(" ");
				for(var i=0; i<order.length; i++){
					if (order[i] == "sort"){order = order[i+1]; break;}
				}
				if (order == sort[0]){
					sort = (sort[1]&&sort[1].toUpperCase()=="DESC"?"ASC":"DESC");
					$("#orderBy").val(order+" DESC"!=order+" "+sort?"":order+" "+sort);
				}else{
					$("#orderBy").val(order+" ASC");
				}
				page();
			});
			
			$(".decodeHtml").each(function(){
				var str = $(this).text()+"";
				$(this).html(str.decodeHtml());
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
		<li class="active"><a href="${ctx}/plan/product/">产品开发列表</a></li>
		<shiro:hasPermission name="plan:product:edit"><li><a href="${ctx}/plan/product/form">新品添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="product" action="${ctx}/plan/product/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<label>产品类型：</label><form:input path="name" htmlEscape="false" maxlength="50" class="input-small"/>
		<select name="finish">
			<option value="0" ${'0' eq product.finish ?'selected':''} >正在开发</option>
			<option value="7" ${'7' eq product.finish ?'selected':''}>已完成</option>
		</select>
		
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width: 50px">图片</th><th>产品类型</th><th>功能描述</th><th>产品型号</th><th style="width: 100px" class="sort masterBy">负责人</th><th style="width: 100px">开发周期</th><th style="width: 100px"  class="sort startDate">开始时间</th><th style="width: 80px">结束时间</th><th class="sort finish" style="width: 80px">完成情况</th><shiro:hasPermission name="plan:product:edit"><th style="width: 80px">操作</th></shiro:hasPermission></tr></thead>
		<tbody>
			<c:forEach items="${page.list}" var="product">
				<tr>
				<td>
					<c:if test="${not empty product.imgPath}">
						<img style="width: 50px;height: 50px"  class="img-thumbnail"  src="<c:url value="${product.imgPath}"></c:url>">
					</c:if>
				</td>	
				<td>${product.name}</td>
				<td><pre class="decodeHtml">${product.remarks}</pre></td>
				<td>${product.model}</td>
				<td>${product.masterBy.name}</td>
				<td>${product.period}</td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${product.startDate}"/> </td>
				<td><fmt:formatDate pattern="yyyy-MM-dd" value="${product.endDate}"/></td>
				<td class="finish">
					<span class="progressbar"></span><span class="detail badge">${product.finishStr}</span>
					<input style="display: none" value="${product.finish}"/></td>
				<shiro:hasPermission name="plan:product:edit"><td>
    				<a href="${ctx}/plan/product/form?id=${product.id}">${product.finish eq '7' ?'查看':'修改'}</a>
					<a href="${ctx}/plan/product/delete?id=${product.id}" onclick="return confirmx('确认要删除该新品吗？', this.href)">删除</a>
				</td></shiro:hasPermission>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
