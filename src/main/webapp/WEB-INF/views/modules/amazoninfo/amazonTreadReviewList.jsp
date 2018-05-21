<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊踩好评差评</title>
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
			
			$(".open").click(function(e){
				var className = $(this).parent().find("input[type='hidden']").val();
				if($(this).text()=='概要'){
					$(this).text('关闭');
				}else{
					$(this).text('概要');
				}
				
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
		<li  class="active"><a href="${ctx}/amazoninfo/amazonTreadReview">踩差评列表</a></li>
		<li><a href="${ctx}/amazoninfo/amazonTreadReview/form">新增踩差评</a></li>
		<li><a href="${ctx}/amazoninfo/amazonTreadReview/accountList">账号列表</a></li>
		<li><a href="${ctx}/amazoninfo/amazonTreadReview/accountAdd">新增账号</a></li>
	</ul>
	
	<form:form id="searchForm"  action="${ctx}/amazoninfo/amazonTreadReview" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 30px;line-height: 30px">
			<div> 
				<label>创建日期 ：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${amazonTreadReview.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;至&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${amazonTreadReview.endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				平台：<select name="country" id="country" style="width: 120px">
						<option value="" ${amazonTreadReview.country eq ''?'selected':''}>全部</option>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<option value="${dic.value}" ${amazonTreadReview.country eq dic.value ?'selected':''}  >${dic.label}</option>
						</c:forEach>
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
				<th style="width:8%">平台</th>
				<th style="width:10%">Asin</th>
				<th style="width:5%">账号数</th>
				<th style="width:35%">账号</th>
				<th style="width:8%">提交人</th>
				<th style="width:15%">提交时间</th>
				<th style="width:8%">操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="postsFeed" varStatus="i">
			<tr>
				<td style="text-align: center;vertical-align: middle;">${postsFeed.id}</td>
				<td>${fns:getDictLabel(postsFeed.country,'platform','')}</td>
				<td><a href='${postsFeed.reviewLink }' target='_blank'>${postsFeed.asin}</a></td>
				<td>${postsFeed.accountNum}</td>
				<td><a style="color: #08c;"  data-html="true" data-placement="right" data-html="true" rel="popover" data-content="${fn:replace(postsFeed.account,',', '<br/>')}">${fns:abbr(postsFeed.account,80)}</a></td>
				<td>${postsFeed.createUser.name}</td>
				<td><fmt:formatDate value="${postsFeed.createDate}" pattern="yyyy-MM-dd HH:mm" /></td>
				<td><input type='hidden' value='${postsFeed.id}'/><a class="btn btn-small btn-info open">概要</a></td>
			</tr>
			<tr style="background-color:#D2E9FF;display: none" name="${postsFeed.id}">
			   <td colspan='8'>${postsFeed.description}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
