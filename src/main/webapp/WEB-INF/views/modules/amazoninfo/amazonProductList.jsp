<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊产品管理</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/treetable.jsp" %>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			$(document).ready(function() {
				$("#country").change(function(){
					$("#searchForm").submit();
				});
			});
			$("#treeTable").treeTable({expandLevel : 1});
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
			/* $("#btnExport").click(function(){
				top.$.jBox.confirm("确认要导出用户数据吗？","系统提示",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/amazonProduct/export");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			}); 
			$("#btnImport").click(function(){
				$.jBox($("#importBox").html(), {title:"导入数据", buttons:{"关闭":true}, 
					bottomText:"导入文件不能超过30M，仅允许导入“xls”或“xlsx”格式文件！"});
			});*/
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
		<li class="active"><a href="${ctx}/amazoninfo/amazonProduct">产品结构信息</a></li>
		<li><a href="${ctx}/amazoninfo/amazonProduct/list2">产品价格预警</a></li>
	</ul>
	<div id="importBox" class="hide">
		<form id="importForm" action="${ctx}/amazoninfo/amazonProduct/import" method="post" enctype="multipart/form-data"
			style="padding-left:20px;text-align:center;" class="form-search" onsubmit="loading('正在导入，请稍等...');"><br/>
			<input id="uploadFile" name="file" type="file" style="width:330px"/><br/><br/>　　
			<input id="btnImportSubmit" class="btn btn-primary" type="submit" value="导 入  "/>
			<a href="${ctx}/amazoninfo/amazonProduct/import/template">下载模板</a>
		</form>
	</div>
	<form:form id="searchForm" modelAttribute="amazonProduct" action="${ctx}/amazoninfo/amazonProduct/" method="post" class="breadcrumb form-search">
		<div style="height: 30px">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
				平台：<select name="country" id="country" style="width: 120px">
						<option value="" ${amazonProduct.country eq ''?'selected':''}>全部</option>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<option value="${dic.value}" ${amazonProduct.country eq dic.value ?'selected':''}  >${dic.label}</option>
						</c:forEach>
				</select>&nbsp;&nbsp;
				<label>名称/Asin/Ean/Sku ：</label><form:input path="name" htmlEscape="false" maxlength="50" class="input-small"/>
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				<!-- &nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
				&nbsp;<input id="btnImport" class="btn btn-primary" type="button" value="导入"/> -->
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="treeTable" class="table table-striped table-bordered table-condensed">
		<thead><tr>
			<th>名称</th>
			<th>Sku</th>
			<th>Asin</th>
			<th>Parent_Asin</th>
			<th>国家</th>
			<th>产品状态</th>
		<tbody>
		<c:forEach items="${page.list}" var="amazonProduct">
			<tr id="${fn:length(amazonProduct.children)==0?'nochild':''}${amazonProduct.id}">
				<td ${amazonProduct.active eq '1' ?'':'style= background-color:#C5C1AA'}><c:if test="${fn:length(amazonProduct.children)>0}"><b style="color:#6CA6CD">&nbsp;&nbsp;组合贴&nbsp;&nbsp;</b></c:if>${amazonProduct.name}</td>
				<td ${amazonProduct.active eq '1' ?'':'style= background-color:#C5C1AA'}>${amazonProduct.sku}</td>
				<td ${amazonProduct.active eq '1' ?'':'style= background-color:#C5C1AA'}><a href="http://www.amazon.${amazonProduct.country eq 'jp' || amazonProduct.country eq 'uk'?'co.':''}${amazonProduct.country eq 'com.unitek'||amazonProduct.country eq 'com2'?'com':amazonProduct.country}/dp/${amazonProduct.asin}" target="_blank">${amazonProduct.asin}</a></td>
				<td ${amazonProduct.active eq '1' ?'':'style= background-color:#C5C1AA'}><a href="http://www.amazon.${amazonProduct.country eq 'jp' || amazonProduct.country eq 'uk'?'co.':''}${amazonProduct.country eq 'com.unitek'||amazonProduct.country eq 'com2'?'com':amazonProduct.country}/dp/${amazonProduct.parentProduct.asin}" target="_blank">${amazonProduct.parentProduct.asin}</a></td>
				<td ${amazonProduct.active eq '1' ?'':'style= background-color:#C5C1AA'}>${fns:getDictLabel(amazonProduct.country,'platform','')}</td>
				<td ${amazonProduct.active eq '1' ?'':'style= background-color:#C5C1AA'}>${amazonProduct.active eq '1' ?'在售':'淘汰产品'}</td>
			</tr>
			<c:if test="${fn:length(amazonProduct.children)>0}">
				<c:forEach items="${amazonProduct.children}" var="child">
					<tr id="${child.id}"  pid="${amazonProduct.id}" >
						<td ${child.active eq '1' ?'':'style= background-color:#C5C1AA'}>${child.name}</td>
						<td ${child.active eq '1' ?'':'style= background-color:#C5C1AA'}>${child.sku}</td>
						<td ${child.active eq '1' ?'':'style= background-color:#C5C1AA'}><a href="http://www.amazon.${child.country eq 'jp' || child.country eq 'uk'?'co.':''}${child.country eq 'com.unitek'||child.country eq 'com2'?'com':child.country}/dp/${child.asin}" target="_blank">${child.asin}</a></td>
						<td ${child.active eq '1' ?'':'style= background-color:#C5C1AA'}><a href="http://www.amazon.${child.country eq 'jp' || child.country eq 'uk'?'co.':''}${child.country eq 'com.unitek'||child.country eq 'com2'?'com':child.country}/dp/${amazonProduct.asin}" target="_blank">${amazonProduct.asin}</a></td>
						<td ${child.active eq '1' ?'':'style= background-color:#C5C1AA'}>${fns:getDictLabel(child.country,'platform','')}</td>
						<td ${child.active eq '1' ?'':'style= background-color:#C5C1AA'}>${child.active eq '1'?'在售':'淘汰产品'}</td>
					</tr>
				</c:forEach>
			</c:if>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
