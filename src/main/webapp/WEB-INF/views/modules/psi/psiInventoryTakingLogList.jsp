<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>人工盘点记录管理</title>
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
				window.location.href = "${ctx}/psi/psiInventoryTakingLog/exp?"+$.param(params);
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
		<li class="active"><a href="">人工盘点记录列表</a></li>
		<shiro:hasPermission name="psi:psiInventoryTakingLog:edit">
		    <li ><a href="${ctx}/psi/psiInventoryTakingLog/form">新增盘点记录</a></li>
		</shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="psiInventoryTakingLog" action="${ctx}/psi/psiInventoryTakingLog/" method="post" class="breadcrumb form-search" >
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<label>结果/备注：</label><input type="text" name="event" value="${psiInventoryTakingLog.result}" style="width:150px"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		
		<label>时间：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${psiInventoryTakingLog.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true},oncleared:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="takingDate" value="<fmt:formatDate value="${psiInventoryTakingLog.takingDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:3%">序号</th><th style="width:5%">盘点人</th><th style="width:5%">盘点时间</th><th style="width:5%">结果</th><th style="width:5%">备注</th><th style="width:5%">盘点文件</th><th style="width:10%">操作</th></tr></thead>   
		<tbody>
		<c:forEach items="${page.list}" var="psiInventoryTakingLog">
			<tr>
				<td>${psiInventoryTakingLog.id}</td>
				<td>${psiInventoryTakingLog.createUser.name}</td>
					<td><fmt:formatDate value="${psiInventoryTakingLog.createDate}" pattern="yyyy-MM-dd"/> </td>
				<td>${psiInventoryTakingLog.result}</td>
				<td>${psiInventoryTakingLog.remark}</td>
				<td>
					<c:forEach items="${fn:split(psiInventoryTakingLog.filePath,',')}" var="attchment" varStatus="i">
						<span>
							<a href="${ctx}/psi/psiInventoryTakingLog/download?fileName=${attchment}">附件 ${i.index+1}</a>
						</span>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</c:forEach> 
				</td>
				<td>
				<shiro:hasPermission name="psi:psiInventoryTakingLog:edit">
				    	<c:if test="${psiInventoryTakingLog.createUser.id eq fns:getUser().id}">
					      <a class="btn btn-small"  href="${ctx}/psi/psiInventoryTakingLog/cancel?id=${order.id}" onclick="return confirmx('确认要删除该盘点记录吗？', this.href)">删除</a>
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
