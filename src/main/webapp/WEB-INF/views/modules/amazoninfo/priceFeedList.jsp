<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊产品价格管理</title>
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
					$("#searchForm").attr("action","${ctx}/amazoninfo/priceFeed");
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
			
			$("#reason").change(function(){
				$("#searchForm").submit();
			});
			
			$("#btnExport").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/priceFeed/exportPriceFeed");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/priceFeed");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
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
		<li class="active"><a href="${ctx}/amazoninfo/priceFeed/">产品价格管理列表</a></li>
		<li><a href="${ctx}/amazoninfo/priceFeed/form">修改产品价格</a></li>
		<li><a href="${ctx}/amazoninfo/productPriceApproval">产品价格审批</a></li>
		<li><a href="${ctx}/amazoninfo/productPriceApproval/form">申请价格审批</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="priceFeed" action="${ctx}/amazoninfo/priceFeed/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<div style="height: 40px;line-height: 30px">
			<div>
				<label>创建日期 ：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="requestDate" value="<fmt:formatDate value="${priceFeed.requestDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;至&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${priceFeed.endDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
				平台：<select name="country" id="country" style="width: 120px">
						<option value="" ${priceFeed.country eq ''?'selected':''}>全部</option>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<option value="${dic.value}" ${priceFeed.country eq dic.value ?'selected':''}  >${dic.label}</option>
						</c:forEach>
				</select>&nbsp;&nbsp;
				&nbsp;&nbsp;
				改价原因：<select name="reason" id="reason" style="width: 120px">
						<option value="" ${priceFeed.reason eq ''?'selected':''}>全部</option>
						<option value="计划改价" ${priceFeed.reason eq '计划改价' ?'selected':''} >计划改价</option>
						<option value="汇率改价" ${priceFeed.reason eq '汇率改价' ?'selected':''} >汇率改价</option>
						<option value="防御性降价" ${priceFeed.reason eq '防御性降价' ?'selected':''}>防御性降价</option>
						<option value="积压降价" ${priceFeed.reason eq '积压降价' ?'selected':''}>积压降价</option>
						<option value="断货升价" ${priceFeed.reason eq '断货升价' ?'selected':''}>断货升价</option>
						<option value="促销调价" ${priceFeed.reason eq '促销调价' ?'selected':''}>促销调价</option>
						<option value="包邮调价" ${priceFeed.reason eq '包邮调价' ?'selected':''}>包邮调价(15分钟自动改回原价)</option>
				</select>&nbsp;&nbsp;
			</div>
		</div>
		<label>sku：</label><form:input path="result" htmlEscape="false" maxlength="50" class="input-medium"/>
		<input type="checkbox" id="aboutMe" ${not empty priceFeed.createBy.id?'checked':''}/>与我相关
		<input type="hidden" name="createBy.id" id="aboutMeVal" value="${not empty priceFeed.createBy.id?cuser.id:''}">
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="导出"/>
	</form:form>
	<div class="alert alert-info">支持多平台下批量修改产品价格</div>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width: 25px">编号</th>
				<th style="width: 100px">平台</th>
				<th style="width: 60px">提交人</th>
				<th style="width: 120px">提交时间</th>
				<th style="width: 120px">状态</th>
				<th style="width: 150px">改价原由</th>
				<th style="width: 200px">结果摘要</th>
				<th>详细结果文件</th>
				<th>操作</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="priceFeed">
			<tr>
			<td rowspan="${fn:length(priceFeed.prices)>0?'2':'1'}" style="text-align: center;vertical-align: middle;">${priceFeed.id}</td>
			<td>${fns:getDictLabel(priceFeed.country,'platform','')}<br/>${priceFeed.accountName}</td>
			<td>${priceFeed.createBy.name}</td>
			<td><fmt:formatDate value="${priceFeed.requestDate}" pattern="yyyy-MM-dd H:mm" /></td>
			<td>${priceFeed.stateStr}</td>
			<td>${priceFeed.reason}</td>
			<td>
				<c:if test="${fn:contains(priceFeed.result,'&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;')}">
					<b style="color: green;">修改成功</b>
				</c:if>
				<c:if test="${not empty priceFeed.result &&!fn:contains(priceFeed.result,'&lt;MessagesWithError&gt;0&lt;/MessagesWithError&gt;')}">
					<b style="color: red">修改失败</b>
				</c:if>
			</td>
			<td><c:if test="${priceFeed.state eq '3'}"><a href="${ctx}/amazoninfo/priceFeed/download?fileName=${priceFeed.resultFile}/result.xml">下载</a></c:if></td>
			<td><c:if test="${priceFeed.state eq '4'}"><a class="btn btn-warning" href="${ctx}/amazoninfo/priceFeed/save?id=${priceFeed.id}" onclick="return confirmx('确认重新修改该产品价格吗？', this.href)">重新提交</a></c:if></td>
			</tr>
			<c:if test="${fn:length(priceFeed.prices)>0}">
			<tr>
				<td colspan="8">
					<c:forEach items="${priceFeed.prices}" var="price">
						<b style="font-size: 16px">Sku:${price.sku};</b>
						价格:${price.price};
						<c:if test="${not empty price.salePrice}"><b style="font-size: 16px">销售价格:${price.salePrice};</b></c:if>
						<c:if test="${not empty price.saleStartDate}">开始时间:<fmt:formatDate value="${price.saleStartDate}" pattern="yyyy-MM-dd" />;</c:if>
						<c:if test="${not empty price.saleEndDate}">结束时间:<fmt:formatDate value="${price.saleEndDate}" pattern="yyyy-MM-dd" />;</c:if>
						<c:if test="${not empty price.businessPrice}">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" 
								data-content="
									B2B阶梯价：<br/>
									<c:if test="${not empty price.quantityLowerBound1}">
										购买数量：${price.quantityLowerBound1 }，售价：${price.quantityPrice1 }<br/>
									</c:if>
									<c:if test="${not empty price.quantityLowerBound2}">
										购买数量：${price.quantityLowerBound2 }，售价：${price.quantityPrice2 }<br/>
									</c:if>
									<c:if test="${not empty price.quantityLowerBound3}">
										购买数量：${price.quantityLowerBound3 }，售价：${price.quantityPrice3 }<br/>
									</c:if>
									<c:if test="${not empty price.quantityLowerBound4}">
										购买数量：${price.quantityLowerBound4 }，售价：${price.quantityPrice4 }<br/>
									</c:if>
									<c:if test="${not empty price.quantityLowerBound5}">
										购买数量：${price.quantityLowerBound5 }，售价：${price.quantityPrice5 }</c:if>">
									<b style="font-size:16px;">B2B价格：${price.businessPrice};</b>
								</a>
						</c:if>
						<br/>
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
