<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品优化管理</title>
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
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
		$(document).ready(function() {
			$("#isChangeSku").change(function(){
				$("#searchForm").submit();
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
		<li class="active"><a href="">产品优化列表</a></li>
		<shiro:hasPermission name="psi:productImprove:edit">
			<li ><a href="${ctx}/psi/productImprove/form">新增产品优化</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="productImprove" action="${ctx}/psi/productImprove/" method="post" class="breadcrumb form-search" >
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<label>产品/订单号/优化内容 ：</label><input type="text" name="improveContent" value="${productImprove.improveContent}" style="width:150px"/> 
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<label>是否更换了sku：</label>
		<form:select path="isChangeSku" style="width: 120px" id="isChangeSku">
			<option value=""></option>
			<option value="0" ${productImprove.isChangeSku eq '0' ?'selected':''} >否</option>
			<option value="1" ${productImprove.isChangeSku eq '1' ?'selected':''} >是</option>
		</form:select>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width:3%">序号</th><th style="width:15%">产品</th><th style="width:10%">订单号</th><th style="width:20%">优化内容</th><th style="width:10%">优化时间</th>
				<th style="width:8%">是否更换sku</th>	<th style="width:10%">创建人</th><th style="width:10%">创建时间</th><th style="width:10%">操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="productImprove">
			<tr>
				<td>${productImprove.id}</td>
				<td>${productImprove.productNameColor}</td>
				<td>${productImprove.orderNo}</td>
				<td>${productImprove.improveContent}</td>
				<td><fmt:formatDate value="${productImprove.improveDate}" pattern="yyyy-MM-dd"/>   </td>
				<td>${productImprove.isChangeSku eq '0'?'否':'是'}</td>
				<td>${productImprove.createUser.name}</td>
				<td><fmt:formatDate value="${productImprove.createDate}" pattern="yyyy-MM-dd"/>   </td>
				<td>
					<shiro:hasPermission name="psi:productImprove:edit">
						<c:if test="${productImprove.createUser.id eq fns:getUser().id}">
							<a class="btn btn-small" href="${ctx}/psi/productImprove/delete?id=${productImprove.id}" onclick="return confirmx('确认要删除该信息吗？', this.href)">删除</a>
						</c:if>  
					</shiro:hasPermission>  
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
