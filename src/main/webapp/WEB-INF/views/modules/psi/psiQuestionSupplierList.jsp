<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>供应商异常记录管理</title>
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
			
			$("#supplier").change(function(){
				$("#searchForm").submit();
			});
			
			$("#expExcel").click(function(){
				var params = {};
				params.event=$("input[name='event']").val();
				window.location.href = "${ctx}/psi/psiQuestionSupplier/exp?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
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
		<li class="active"><a href="">供应商异常记录列表</a></li>
		<shiro:hasPermission name="psi:questionSupplier:edit">
			<li ><a href="${ctx}/psi/psiQuestionSupplier/form">新增供应商异常记录</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="psiQuestionSupplier" action="${ctx}/psi/psiQuestionSupplier/" method="post" class="breadcrumb form-search" >
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<label>供应商：</label>
		<select style="width:100px;" id="supplier" name="supplier.id">
			<option value="" ${psiQuestionSupplier.supplier eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
			<c:forEach items="${suppliers}" var="supplier" varStatus="i">
				 <option value='${supplier.id}' ${supplier.id eq  psiQuestionSupplier.supplier.id?'selected':''}>${supplier.nikename}</option>;
			</c:forEach>
		</select>
		
		<label>事件/后果/处理/惩罚/赔偿 ：</label><input type="text" name="event" value="${psiQuestionSupplier.event}" style="width:150px"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="expExcel" class="btn btn-warning" type="button" value="导出excel"/>
	</form:form>
	<tags:message content="${message}"/>
	
	<div id="imgtest"></div> 
	
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:3%">序号</th><th style="width:5%">供应商</th><th style="width:6%">出错时间</th>
		<th style="width:6%">问题类型</th><th style="width:8%" >产品</th><th style="width:8%">质检结果</th>
		<th style="width:8%" class="sort orderNo" >采购批次</th><th style="width:15%">事件</th>
		<th style="width:15%">后果</th><th style="width:15%">处理</th><th style="width:15%">赔偿/处罚</th>
		<th style="width:8%">创建人</th><th style="width:10%">操作</th></tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="psiQuestionSupplier">
			<tr>
				<td>${psiQuestionSupplier.id}</td>
				<td>${psiQuestionSupplier.supplier.nikename}</td>
				<td><fmt:formatDate value="${psiQuestionSupplier.questionDate}" pattern="yyyy-MM-dd"/>   </td>
				<td>${psiQuestionSupplier.questionType}</td>
				<td>${fn:replace(psiQuestionSupplier.productName,'Inateck','')}</td>
				<td>${psiQuestionSupplier.result}</td>
				<td>${psiQuestionSupplier.orderNo}</td>
				<td>${psiQuestionSupplier.event}</td>
				<td>${psiQuestionSupplier.consequence}</td>
				<td>${psiQuestionSupplier.deal}</td>
				<td>${psiQuestionSupplier.punishment}</td>
				<td>${psiQuestionSupplier.createUser.name}</td>
				<td>
					    <shiro:hasPermission name="psi:questionSupplier:edit"> 
							<c:if test="${psiQuestionSupplier.createUser.id eq '1'}">
		                            <a class="btn btn-small" href="${ctx}/psi/psiQuestionSupplier/form?id=${psiQuestionSupplier.id}">修改</a>
		                            <a class="btn btn-small" href="${ctx}/psi/psiQuestionSupplier/delete?id=${psiQuestionSupplier.id}" onclick="return confirmx('确认要删除该贴码错误信息吗？', this.href)">删除</a>
		                    </c:if>  
							<c:if test="${psiQuestionSupplier.createUser.id eq fns:getUser().id && psiQuestionSupplier.createUser.id ne '1'}">
								<a class="btn btn-small" href="${ctx}/psi/psiQuestionSupplier/form?id=${psiQuestionSupplier.id}">修改</a>
								<a class="btn btn-small" href="${ctx}/psi/psiQuestionSupplier/delete?id=${psiQuestionSupplier.id}" onclick="return confirmx('确认要删除该贴码错误信息吗？', this.href)">删除</a>
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
