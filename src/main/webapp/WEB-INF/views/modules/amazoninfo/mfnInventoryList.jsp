<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>自发货库存管理管理</title>
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
					$("#searchForm").attr("action","${ctx}/amazoninfo/mfnInventory");
					$("#searchForm").submit();
				});
			</shiro:hasPermission>
			
			$(".countryHref").click(function(){
				window.location.href = "${ctx}/amazoninfo/amazonProduct/mfnInventoryView?country="+$(this).attr("key");
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
		<li><a class="countryHref" href="#" key="de">德国本地贴</a></li>
		<li ><a class="countryHref" href="#" key="com">美国本地贴</a></li>
		<!-- <li ><a class="countryHref" href="#" key="com1">美国1本地贴</a></li> -->
		<li ><a class="countryHref" href="#" key="jp">日本本地贴</a></li>
		<li class="active"><a href="${ctx}/amazoninfo/mfnInventory" >本地贴库存修改结果</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="mfnInventoryFeed" action="${ctx}/amazoninfo/mfnInventory" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 30px;line-height: 30px">
			<div>
				<label>发起修改日期 ：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="requestDate" value="<fmt:formatDate value="${mfnInventoryFeed.requestDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;至&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${mfnInventoryFeed.endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				仓库：<select name="country" id="country" style="width: 120px">
						<option value="" ${mfnInventoryFeed.country eq ''?'selected':''}>全部</option>
						<option value="de" ${mfnInventoryFeed.country eq 'de' ?'selected':''}>德国仓</option>
						<option value="com" ${mfnInventoryFeed.country eq 'com' ?'selected':''}>美国仓</option>
						<%-- <option value="com1" ${mfnInventoryFeed.country eq 'com1' ?'selected':''}>美国1仓</option> --%>
						<option value="jp" ${mfnInventoryFeed.country eq 'jp' ?'selected':''}>日本仓</option>
				</select>&nbsp;&nbsp;
				<label>Sku：</label><form:input path="result" htmlEscape="false" maxlength="50" class="input-small"/>
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				<shiro:hasPermission name="amazoninfo:feedSubmission:all">
					<input type="checkbox" id="aboutMe" ${not empty mfnInventoryFeed.createBy.id?'checked':''}/>与我相关
					<input type="hidden" name="createBy.id" id="aboutMeVal" value="${not empty mfnInventoryFeed.createBy.id?cuser.id:''}">
				</shiro:hasPermission>
			</div>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width: 15px">编号</th>
				<th style="width: 50px">平台</th>
				<th style="width: 60px">提交人</th>
				<th style="width: 100px">提交时间</th>
				<th style="width: 120px">状态</th>
				<th style="width: 300px">结果摘要</th>
				<th>详细结果文件</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="mfnInventoryFeed">
			<tr>
				<td rowspan="${fn:length(mfnInventoryFeed.items)>0?'2':'1'}" style="text-align: center;vertical-align: middle;">${mfnInventoryFeed.id}</td>
				<td>${fns:getDictLabel(mfnInventoryFeed.country,'platform','')} ${mfnInventoryFeed.accountName }</td>
				<td>${mfnInventoryFeed.createBy.name}</td>
				<td><fmt:formatDate value="${mfnInventoryFeed.requestDate}" pattern="yyyy-MM-dd H:mm" /></td>
				<td>${mfnInventoryFeed.stateStr}</td>
				<td>
					<c:if test="${fn:contains(mfnInventoryFeed.result,'&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;')}">
						<b style="color: green;">修改成功</b>
					</c:if>
					<c:if test="${not empty mfnInventoryFeed.result &&!fn:contains(mfnInventoryFeed.result,'&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;')}">
						<b style="color: red">修改失败</b>
					</c:if>
				</td>
				<td><c:if test="${mfnInventoryFeed.state eq '3'}"><a href="${ctx}/amazoninfo/mfnInventory/download?fileName=${mfnInventoryFeed.resultFile}/result.xml">下载</a></c:if></td>
			</tr>
			<c:if test="${fn:length(mfnInventoryFeed.items)>0}">
			<tr>
				<td colspan="7">
					<c:forEach items="${mfnInventoryFeed.items}" var="item">
						<b style="font-size: 16px">Sku:${item.sku}</b>修改为:${item.quantity};<br/>
					</c:forEach>
				</td>
			</tr>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
