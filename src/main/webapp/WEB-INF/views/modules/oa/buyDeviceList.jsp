<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>设备采购一览</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	$(function(){
		$('.fancybox').fancybox({
			  padding : 0,
			  autoScale:true,
			  width:980
		 });
		$("a[rel='popover']").popover({trigger:'hover'});
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
		<li><a href="${ctx}/oa/buyDevice/list/task">待办列表</a></li>
		<li class="active"><a href="${ctx}/oa/buyDevice/list">办公设备采购列表</a></li>
		<shiro:hasPermission name="oa:buyDevice:view"><li><a href="${ctx}/oa/buyDevice/form">办公设备采购申请</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="buyDevice" action="${ctx}/oa/buyDevice/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div>
			<label>创建日期 ：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/oa/buyDevice/list');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDateStart" value="<fmt:formatDate value="${buyDevice.createDateStart}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;至&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/oa/buyDevice/list');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDateEnd" value="<fmt:formatDate value="${buyDevice.createDateEnd}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
		</div> 
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<th style="width: 100px">申请人</th>
			<th style="width: 100px">设备类型</th>
			<th style="width: 260px">设备清单</th>
			<th style="width: 260px">设备采购理由</th>
			<th style="width: 80px">总金额</th>
			<th style="width: 80px">申请时间</th>
			<th style="width: 80px">当前状态</th>
			<th >操作</th>
		</tr></thead>
		<tbody>
		<c:forEach items="${page.list}" var="buyDevice">
			<tr>
				<td>${buyDevice.createBy.name}</td>
				<td>${buyDevice.deviceTypeDictLabel}</td>
				<td><a href="${ctx}/oa/buyDevice/detail?id=${buyDevice.id}" rel="popover" data-content="${buyDevice.name}">${fn:substring(buyDevice.name,0,20)}</a></td>
				<td><a href="${ctx}/oa/buyDevice/detail?id=${buyDevice.id}" rel="popover" data-content="${buyDevice.reason}">${fn:substring(buyDevice.reason,0,20)}</a></td>
				<td>${buyDevice.price}</td>
				<td><fmt:formatDate value="${buyDevice.createDate}" pattern="yyyy-M-d"/></td>
				<td>${buyDevice.processStatus}</td>
				<td>
					<c:if test="${buyDevice.processStatus ne '已完成' }">
						<a href="${ctx}/sys/workflow/processMap?processInstanceId=${buyDevice.processInstanceId}" class="fancybox"  data-fancybox-type="iframe">跟踪</a>
					</c:if>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
