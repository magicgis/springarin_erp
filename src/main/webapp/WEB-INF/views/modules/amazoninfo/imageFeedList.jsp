<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊产品图片管理</title>
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
			<shiro:hasPermission name="amazoninfo:feedSubmission:all">
				$("#aboutMe").click(function(){
					if(this.checked){
						$("#aboutMeVal").val('${cuser.id}');
					}else{
						$("#aboutMeVal").val('');
					}
					$("#searchForm").attr("action","${ctx}/amazoninfo/imageFeed");
					$("#searchForm").submit();
				});
			</shiro:hasPermission>
			
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
		
		function review(country, sku){
			var params = {};
			params.country = country;
			params.sku = sku;
			if(params.sku){
				$.get("${ctx}/amazoninfo/imageFeed/getLink?"+$.param(params),function(link){
					if(link){
						windowOpen(link,'review',800,600);
					}else{
						$.jBox.tip("查找不到对应的asin！", 'error',{timeout:2000});
					}
				});
			}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/amazoninfo/imageFeed/">产品图片管理列表</a></li>
		<li><a href="${ctx}/amazoninfo/imageFeed/form">修改产品图片</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="imageFeed" action="${ctx}/amazoninfo/imageFeed/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 30px;line-height: 30px">
			<div>
				<label>创建日期 ：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="requestDate" value="<fmt:formatDate value="${imageFeed.requestDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;至&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${imageFeed.endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				平台：<select name="country" id="country" style="width: 120px">
						<option value="" ${imageFeed.country eq ''?'selected':''}>全部</option>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<option value="${dic.value}" ${imageFeed.country eq dic.value ?'selected':''}  >${dic.label}</option>
						</c:forEach>
				</select>&nbsp;&nbsp;
				<label>sku：</label><form:input path="sku" htmlEscape="false" maxlength="50" class="input-small"/>
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				<shiro:hasPermission name="amazoninfo:feedSubmission:all">
					<input type="checkbox" id="aboutMe" ${not empty imageFeed.createBy.id?'checked':''}/>与我相关
					<input type="hidden" name="createBy.id" id="aboutMeVal" value="${not empty imageFeed.createBy.id?cuser.id:''}">
				</shiro:hasPermission>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width: 15px">编号</th>
				<th style="width: 100px">平台</th>
				<th style="width: 320px">产品名称</th>
				<th style="width: 60px">提交人</th>
				<th style="width: 100px">提交时间</th>
				<th style="width: 120px">状态</th>
				<th style="width: 100px">结果摘要</th>
				<th>详细结果文件</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="imageFeed">
			<tr>
			<td style="text-align: center;vertical-align: middle;">${imageFeed.id}</td>
			<td>${fns:getDictLabel(imageFeed.country,'platform','')}<br/>${imageFeed.accountName}</td>
			<td><a href="${ctx}/amazoninfo/imageFeed/view?id=${imageFeed.id}" >${imageFeed.proName}</a>
				&nbsp;&nbsp;
				<c:if test="${not empty imageFeed.linkSku }"><a class="btn btn-info" onclick="review('${imageFeed.country}','${imageFeed.linkSku}')">查看产品</a></c:if>
			</td>
			<td>${imageFeed.createBy.name}</td>
			<td><fmt:formatDate value="${imageFeed.requestDate}" pattern="yyyy-MM-dd H:mm" /></td>
			<td>${imageFeed.stateStr}</td>
			<td>
				<c:if test="${fn:contains(imageFeed.result,'&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;')}">
					<b style="color: green;">修改成功</b>
				</c:if>
				<c:if test="${not empty imageFeed.result &&!fn:contains(imageFeed.result,'&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;')}">
					<b style="color: red">修改失败</b>
				</c:if>
			</td>
			<td><c:if test="${imageFeed.state eq '3'}"><a href="${ctx}/amazoninfo/imageFeed/download?fileName=${imageFeed.resultFile}/result.xml">下载</a></c:if></td>
			<td><c:if test="${imageFeed.state eq '4'}"><a class="btn btn-warning" href="${ctx}/amazoninfo/imageFeed/save?id=${imageFeed.id}" onclick="return confirmx('确认上传该图片吗？', this.href)">重新提交</a></c:if></td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
