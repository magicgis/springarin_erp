<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊帖子上架管理</title>
	<meta name="decorator" content="default"/>
	<meta http-equiv="refresh" content="60"/>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			$("#aboutMe").click(function(){
				if(this.checked){
					$("#aboutMeVal").val('${cuser.id}');
				}else{
					$("#aboutMeVal").val('');
				}
				$("#searchForm").attr("action","${ctx}/amazoninfo/feedSubmission");
				$("#searchForm").submit();
			});
			
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
		
			$("a[rel='popover']").popover({trigger:'hover'});	
			
			$("#country").change(function(){
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
		<li class="active"><a href="${ctx}/amazoninfo/feedSubmission/">帖子上架列表</a></li>
	<%-- 	<li><a href="${ctx}/amazoninfo/feedSubmission/form">帖子上架</a></li> --%>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/changePostsList">帖子信息列表</a></li>	
	    <li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">新增管理<b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addParentsPostFrom">新建母帖</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom">新建普通帖</a></li>
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=8">新建本地帖</a></li>
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=2">复制帖</a></li>
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=3">Cross帖</a></li>
		    </ul>
	   </li>
	     <li class="dropdown">
		    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">其他管理<b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
			       <li><a href="${ctx}/amazoninfo/amazonPortsDetail/form">编辑帖子信息</a></li>	
			       <li><a href="${ctx}/amazoninfo/amazonPortsDetail/commonForm">编辑帖子信息(英语国家)</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=4">帖子类型转换</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/addPostFrom?addType=5">帖子一键还原</a></li>	
				   <li><a href="${ctx}/amazoninfo/amazonPortsDetail/deletePostsForm">删除帖子</a></li>	
		    </ul>
	   </li>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/postsRelationList">组合帖管理列表</a></li>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/formRelation">修改绑定关系</a></li>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/findEanList">Ean列表</a></li>
		<li><a href="${ctx}/amazoninfo/amazonPortsDetail/findProductTypeCodeList">品类代码</a></li>
	</ul>
	
	<form:form id="searchForm" modelAttribute="feedSubmission" action="${ctx}/amazoninfo/feedSubmission/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 30px;line-height: 30px">
			<div>
				<label>创建日期 ：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${feedSubmission.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;至&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${feedSubmission.endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				平台：<select name="country" id="country" style="width: 120px">
						<option value="" ${feedSubmission.country eq ''?'selected':''}>全部</option>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<option value="${dic.value}" ${feedSubmission.country eq dic.value ?'selected':''}  >${dic.label}</option>
						</c:forEach>
				</select>&nbsp;&nbsp;
				<label>sku：</label><form:input path="result" htmlEscape="false" maxlength="50" class="input-small"/>
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				<input type="checkbox" id="aboutMe" ${not empty feedSubmission.createBy.id?'checked':''}/>与我相关
				<input type="hidden" name="createBy.id" id="aboutMeVal" value="${not empty feedSubmission.createBy.id?cuser.id:''}">
			</div>
		</div>
	</form:form>
	<div class="alert alert-info"><strong >支持各种版本的帖子;程序只读取上传excel帖子里，下方tab页命名为<span style="font-size: 20px;color: orange">"template"</span>页里的数据作为上传数据</strong></div>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width: 15px">编号</th>
				<th style="width: 50px">平台</th>
				<th style="width: 60px">提交人</th>
				<th style="width: 100px">提交时间</th>
				<th style="width: 10px">帖子</th>
				<th style="width: 20px">结果报告</th>
				<th style="width: 120px">状态</th>
				<th>结果</th>
				<th style="width: 40px">操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="feedSubmission">
			<tr>
			<td rowspan="${fn:length(feedSubmission.feeds)>0?'2':'1'}" style="text-align: center;vertical-align: middle;">${feedSubmission.id}</td>
			<td>${fns:getDictLabel(feedSubmission.country,'platform','')}<br/>${feedSubmission.accountName }</td>
			<td>${feedSubmission.createBy.name}</td>
			<td><fmt:formatDate value="${feedSubmission.createDate}" pattern="yyyy-MM-dd H:mm" /></td>
			<td><a href="${ctx}/amazoninfo/feedSubmission/download?fileName=${feedSubmission.excelFile}">下载</a></td>
			<td><c:if test="${not empty  feedSubmission.resultFile}"><a href="${ctx}/amazoninfo/feedSubmission/download?fileName=${feedSubmission.resultFile}">下载</a> </c:if></td>
			<td>${feedSubmission.stateStr}</td>
			<td>${feedSubmission.result}</td>
			<td style="text-align: center;vertical-align: middle;" rowspan="${fn:length(feedSubmission.feeds)>0?'2':'1'}"><c:if test="${(feedSubmission.state eq '3' || feedSubmission.state eq '0') && fns:getUser().id eq feedSubmission.createBy.id}"><a href="${ctx}/amazoninfo/feedSubmission/delete?id=${feedSubmission.id}" onclick="return confirmx('确认要删除该帖子吗？', this.href)">删除</a></c:if>
				<c:if test="${imageFeed.state eq '4'}"><a class="btn btn-warning" href="${ctx}/amazoninfo/feedSubmission/resave?id=${feedSubmission.id}" onclick="return confirmx('确认要重新提交该帖子吗？', this.href)">重新提交</a></c:if>
			</td>
			</tr>
			<c:if test="${fn:length(feedSubmission.feeds)>0}">
			<tr>
				<td colspan="7">
					<c:forEach items="${feedSubmission.feeds}" var="feed">
						<b style="font-size: 16px">Sku:${feed.sku};<c:if test="${not empty feed.ean}">Ean Or Asin:${feed.ean};</c:if><c:if test="${not empty feed.price}">价格:${feed.price};</c:if> </b>
						<c:if test="${not empty feed.salePrice}">销售价格:${feed.salePrice};</c:if>
						<c:if test="${not empty feed.saleStartDate}">开始时间:<fmt:formatDate value="${feed.saleStartDate}" pattern="yyyy-MM-dd" />;</c:if>
						<c:if test="${not empty feed.saleEndDate}">结束时间:<fmt:formatDate value="${feed.saleEndDate}" pattern="yyyy-MM-dd" />;</c:if>
						<c:if test="${not empty feed.parentChild}">parentChild:${feed.parentChild};</c:if>
						<c:if test="${not empty feed.parentSku}">parentSku:${feed.parentSku};</c:if>
						<c:if test="${not empty feed.relationshipType}">relationshipType:${feed.relationshipType};</c:if>
						<br/>
					</c:forEach>
					<a href="${ctx}/amazoninfo/feedSubmission/view?id=${feedSubmission.id}">点击查看更多详情...</a>
				</td>
			</tr>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
