<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊踩好评差评账号列表</title>
	<meta name="decorator" content="default"/>

	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			$("a[rel='popover']").popover({trigger:'hover'});	
			
			$("#country").change(function(){
				$("#searchForm").submit();
			});
			$("#delFlag").change(function(){
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
		<li ><a href="${ctx}/amazoninfo/amazonTreadReview">踩差评列表</a></li>
		<li><a href="${ctx}/amazoninfo/amazonTreadReview/form">新增踩差评</a></li>
		<li  class="active"><a href="${ctx}/amazoninfo/amazonTreadReview/accountList">账号列表</a></li>
		<li><a href="${ctx}/amazoninfo/amazonTreadReview/accountAdd">新增账号</a></li>
	</ul>
	
	<form:form id="searchForm"  action="${ctx}/amazoninfo/amazonTreadReview/accountList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 30px;line-height: 30px">
			<div> 
				
				平台：<select name="country" id="country" style="width: 120px">
						<option value="" ${amazonTreadReviewAccount.country eq ''?'selected':''}>全部</option>
						<option value="com" ${amazonTreadReviewAccount.country eq dic.value ?'selected':''}  >欧美</option>
						<option value="jp" ${amazonTreadReviewAccount.country eq dic.value ?'selected':''}  >日本</option>
				</select>&nbsp;&nbsp;
				类型：<select name="delFlag" id="delFlag" style="width: 120px">
				      <option value="0" ${amazonTreadReviewAccount.delFlag eq  '0'?'selected':''} >有效</option>
					  <option value="1" ${amazonTreadReviewAccount.delFlag eq  '1'?'selected':''} >失效</option>		
				</select>&nbsp;&nbsp;
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
			</div> 
		</div>
	</form:form>
	
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width:5%">编号</th>
				<th style="width:10%">平台</th>
				<th style="width:30%">账号</th>
				<th style="width:20%">创建人</th>
				<th style="width:20%">创建时间</th>
				<th style="width:10%">操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="postsFeed" varStatus="i">
			<tr>
				<td style="text-align: center;vertical-align: middle;">${postsFeed.id}</td>
				<td>
				   	<c:choose>
						<c:when test="${'com' eq postsFeed.country}">欧美</c:when>
						<c:otherwise>日本</c:otherwise>
					</c:choose>
				</td>
				<td>${postsFeed.loginName}</td>
				<td>${postsFeed.createUser.name}</td>
				<td><fmt:formatDate value="${postsFeed.createDate}" pattern="yyyy-MM-dd HH:mm" /></td>
				<td>
				    <shiro:hasPermission name="amazoninfo:feedSubmission:all">
				       <input type='hidden' value='${postsFeed.id}'/><a class="btn btn-small btn-info open" href='${ctx}/amazoninfo/amazonTreadReview/updateDelFlag?id=${postsFeed.id}'>删除</a>
				    </shiro:hasPermission>
				    <shiro:lacksPermission name="amazoninfo:feedSubmission:all">
				        <shiro:hasPermission name="amazoninfo:feedSubmission:${postsFeed.country }">
				 	       <input type='hidden' value='${postsFeed.id}'/><a class="btn btn-small btn-info open" href='${ctx}/amazoninfo/amazonTreadReview/updateDelFlag?id=${postsFeed.id}'>删除</a>
				 	    </shiro:hasPermission>
				    </shiro:lacksPermission>
				</td>
			</tr>
			
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
