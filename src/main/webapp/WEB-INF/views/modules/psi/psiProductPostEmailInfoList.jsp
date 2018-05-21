<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>是否发送售后邮件管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
		$(document).ready(function() {
			
			$(".countryHref,#status").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			 $(".postStatus").editable({
				    value:$(this).val(),
				    source: [
				              {value: 0, text: '未发送'},
				              {value: 1, text: '已发送'},
				              {value: 2, text: '不发送'},
				           ],
					mode:'inline',
					showbuttons:'bottom',
					success:function(response,newValue){
						var param = {};
						var $this = $(this);
						var oldVal = $this.text();
						param.id = $this.parent().parent().find(".id").val();
						param.status = newValue;
						$.get("${ctx}/psi/productPostEmailInfo/updateStatus?"+$.param(param),function(data){
							if(!(data)){    
								$this.text(oldVal);						
							}else{
								top.$.jBox.tip("更改发送状态成功！", 'info',{timeout:2000});
							}
						});
						return true;
					}});
			  
			
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
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${psiProductPostMailInfo.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
		
	</ul>
	<form:form id="searchForm" modelAttribute="psiProductPostMailInfo" action="${ctx}/psi/productPostEmailInfo/" method="post" class="breadcrumb form-search" >
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input name="country" type="hidden" value="${psiProductPostMailInfo.country}"/>
		<label>产品名：</label><input type="text" name="productName" value="${psiProductPostMailInfo.productName}" style="width:150px"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<label>发送状态：</label>
		<select name="status" id="status">
			<option value="">所有</option>
			<option value="0" ${psiProductPostMailInfo.status eq '0'?'selected':'' }>未发送</option>
			<option value="1" ${psiProductPostMailInfo.status eq '1'?'selected':'' }>已发送</option>
			<option value="2" ${psiProductPostMailInfo.status eq '2'?'selected':'' }>不发送</option>
		</select>
		<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	</form:form>
	<tags:message content="${message}"/>
	<!-- 未开票的数量（含在产），未开票的数量（不含在产） -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr><th style="width:3%">序号</th><th style="width:10%">产品名</th><th style="width:10%">类型</th><th style="width:10%">售后邮件发送状态</th><th style="width:10%">更改发送状态人员</th><th style="width:10%">更改发送状态时间</th></tr></thead>   
		<tbody>
		<c:forEach items="${page.list}" var="info">
			<tr>
				<td>${info.id}<input type="hidden" class="id" value="${info.id}" /></td>
				<td>${info.productName}</td>
				<td>${info.type eq "0" ?"主力":"新品"}</td>
				<td>
				<c:if test="${info.status eq '0'}">
					<a href="#" class="postStatus"  data-type="select" data-pk="1" data-title="Enter lading Model " data-value="${info.status}">
						<c:if test="${info.status eq '0'}">未发送</c:if>
						<c:if test="${info.status eq '1'}">已发送</c:if>
						<c:if test="${info.status eq '2'}">不发送</c:if>
					</a>
				</c:if>
				<c:if test="${info.status ne '0'}">
					${info.status eq '1'?'已发送':'不发送'}
				</c:if>
				</td>
				<td>${info.updateUser.name}</td>
				<td><fmt:formatDate value="${info.updateDate}" pattern="yyyy-MM-dd"/> </td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
