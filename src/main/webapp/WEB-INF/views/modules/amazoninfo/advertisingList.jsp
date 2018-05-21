<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>广告报表</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
	</style>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		if(!(top)){
			top = self;			
		}	
		$(document).ready(function() {
			
			$(".detail").click(function(){
				var params = {};
				params.createDate = $("#start").val();
				params.dataDate = $("#end").val();
				params.country = '${advertising.country}';
				$(this).parent().find(":hidden").each(function(){
					params[$(this).attr('name')] = encodeURI($(this).val());					
				});
				window.location.href = "${ctx}/amazoninfo/advertising/detail?"+$.param(params);
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
					$("#orderBy").val(order+" DESC");
				}
				$("#searchForm").submit();
			});
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			var container = $('body');

			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#end").removeAttr("name");
				$("#searchForm").submit();
			});
			
			$(".grpOpen,.wkeyOpen").click(function(){
				if($(this).find("span").attr("class")=='icon-plus'){
					$("tr[pro='"+$(this).attr("pro")+"']").show();
					$(this).find("span").attr("class","icon-minus");
				}else{
					$("tr[pro='"+$(this).attr("pro")+"']").hide();
					$("tr[pro^='"+$(this).attr("pro")+"']").hide();
					$("a[pro^='"+$(this).attr('pro')+"'] span").attr("class","icon-plus");;
				}
				$(this).focus();
				scrollTo = $(this);
				container.animate({
					  scrollTop: Math.abs(scrollTo.offset().top - container.offset().top)-20
				});
			});
			
			$("#exportAdv").click(function(){
				$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/exportWeekReport");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/");
			});
			
			$("#exportByProduct").click(function(){
				$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/exportByProduct");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/");
			});
			
			$("#exportNegative").click(function(){
				console.log("${advertising.country}");
				$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/negativeExport?platform=${advertising.country}");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/");
			});
		});
		
		function exportData(a,type){
			var params = {};
			params.createDate = $("#start").val();
			params.dataDate = $("#end").val();
			params.country = $(".nav-tabs .active a").attr("key");
			params.keyword = $("#keyword").val();
			params.typeFlag = type;
			$(a).attr("href","${ctx}/amazoninfo/advertising/export?"+$.param(params));
		}
		
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
				<li class="${advertising.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
		<shiro:hasPermission name="spreadReport:price:view">
			<li><a href="${ctx}/amazoninfo/advertising/listByProduct" >分产品广告费用统计</a></li>
		</shiro:hasPermission>
		<li><a href="${ctx}/amazoninfo/advertising/viewWeekReport" >广告统计</a></li>
		
	</ul>
	<form:form id="searchForm" modelAttribute="advertising" action="${ctx}/amazoninfo/advertising/" method="post" class="breadcrumb form-search">
		<div style="height: 30px">
			<div style="float: left;">
				<span id="date" >
					<input  style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${advertising.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
					&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="dataDate" value="<fmt:formatDate value="${advertising.dataDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
					&nbsp;&nbsp;&nbsp;&nbsp;
				</span>
				<input  name="country" type="hidden" value="${advertising.country}"/>
				<label>关键字/sku/groupName：</label><form:input path="keyword" id="keyword" htmlEscape="false" maxlength="50" class="input-small"/>
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				&nbsp;<input id="exportAdv" class="btn btn-primary" type="button" value="广告报表"/>
				&nbsp;<input id="exportByProduct" class="btn btn-primary" type="button" value="按产品统计导出"/>
				&nbsp;<input id="exportNegative" class="btn btn-primary" type="button" value="否定关键字"/>
				&nbsp;&nbsp;
				<div class="btn-group">
					   <button type="button" class="btn btn-primary">导出</button>
					   <button type="button" class="btn btn-primary dropdown-toggle"  data-toggle="dropdown">
					      <span class="caret"></span>
					      <span class="sr-only"></span>
					   </button>
					   <ul class="dropdown-menu" >
					   	  <li><a href="#" onclick="exportData(this,3)">按天</a></li>	
					      <li><a href="#" onclick="exportData(this,1)">按周</a></li>
					      <li><a href="#" onclick="exportData(this,2)">按月</a></li>
					   </ul>
				</div>
			</div>
		</div>
		<input id="orderBy" name="orderBy" type="hidden" value="${orderBy}"/>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;">No.</th>
				<th style="text-align: center;vertical-align: middle;">广告名<br/>广告组<br/>Sku</th>
				<th style="text-align: center;vertical-align: middle;">关键字</th>
				<th style="text-align: center;vertical-align: middle;" class="sort clicks">Clicks</th>
				<th style="text-align: center;vertical-align: middle;" class="sort impressions">Impressions</th>
				<th style="text-align: center;vertical-align: middle;" class="sort ctr">CTR</th>
				<th style="text-align: center;vertical-align: middle;" class="sort totalSpend">Total <br/>Spend${currencySymbol }</th>
				<th style="text-align: center;vertical-align: middle;" class="sort averageCPC">Average<br/> CPC${currencySymbol }</th>
				<th style="text-align: center;vertical-align: middle;" class="sort ordersPlaced">Orders<br/> Placed</th>
				<th style="text-align: center;vertical-align: middle;" class="sort orderSales">Ordered <br/>Product<br/> Sales${currencySymbol }</th>
				<th style="text-align: center;vertical-align: middle;" class="sort conversion">Conversion</th>
				<th style="text-align: center;vertical-align: middle;" class="sort acos">ACos</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${total.dtos}" var="dto" varStatus="i">
			<tr>
				<td style="text-align: center;vertical-align: middle;">${i.count}</td>
				<td style="text-align: left;vertical-align: middle;"><a class="detail" rel="popover" data-content="Click here for details">${dto.name}</a>
					 <a href="#" class="grpOpen" pro='name${i.count}'><span class="icon-plus"></span></a>
					 <input type="hidden" value="${dto.name}" name="name"/>
				</td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;">${dto.clicks}</td>
				<td style="text-align: center;vertical-align: middle;">${dto.impressions}</td>
				<td style="text-align: center;vertical-align: middle;">${dto.ctr}%</td>
				<td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${dto.totalSpend}" maxFractionDigits="2"/></td>
				<td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${dto.averageCPC}" maxFractionDigits="2"/></td>
				<td style="text-align: center;vertical-align: middle;"><a href="#"  rel="popover" data-content="Same:${dto.sameSkuOrdersPlaced};Other:${dto.otherSkuOrdersPlaced}">${dto.ordersPlaced}</a></td>
				<td style="text-align: center;vertical-align: middle;"><a href="#"  rel="popover" data-content="Same:<fmt:formatNumber value="${dto.sameSkuOrderSales}" maxFractionDigits="2"/>;Other:<fmt:formatNumber value="${dto.otherSkuOrderSales}" maxFractionDigits="2"/>"><fmt:formatNumber value="${dto.orderSales}" maxFractionDigits="2"/></a></td>
				<td style="text-align: center;vertical-align: middle;">
					<c:if test="${dto.clicks>0}">
						<fmt:formatNumber value=" ${dto.ordersPlaced*100/dto.clicks}" maxFractionDigits="2"/>%
					</c:if>
					<c:if test="${dto.clicks==0}">
						0%
					</c:if>
				</td>
				<td style="text-align: center;vertical-align: middle;">
					<span style="${dto.acos>15?'color:red;font-weight: bold;':''}"><fmt:formatNumber value="${dto.acos}" maxFractionDigits="2"/>%</span>
				</td>
			</tr>
			<c:forEach items="${dto.groups}" var="group" varStatus="j">
				<tr pro="name${i.count}" style="display: none">
					<td></td>
					<td style="text-align: left;vertical-align: middle;background-color: #f9f9f9;font-weight: bold;">
						<a class="detail" rel="popover" data-content="Click here for details">&nbsp;&nbsp;${group.groupName}</a>
						<a href="#" class="wkeyOpen" pro='name${i.count}group${j.count}'><span class="icon-plus"></span></a>
						 <input type="hidden" value="${dto.name}" name="name"/>
						 <input type="hidden" value="${group.groupName}" name="groupName"/>
					</td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9"></td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9">${group.clicks}</td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9">${group.impressions}</td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9"><fmt:formatNumber value="${group.ctr}" maxFractionDigits="2"/>%</td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9"><fmt:formatNumber value="${group.totalSpend}" maxFractionDigits="2"/></td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9"><fmt:formatNumber value="${group.averageCPC}" maxFractionDigits="2"/></td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9"><a href="#"  rel="popover" data-content="Same:${group.sameSkuOrdersPlaced};Other:${group.otherSkuOrdersPlaced}">${group.ordersPlaced}</a></td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9"><a href="#"  rel="popover" data-content="Same:<fmt:formatNumber value="${group.sameSkuOrderSales}" maxFractionDigits="2"/>;Other:<fmt:formatNumber value="${group.otherSkuOrderSales}" maxFractionDigits="2"/>"><fmt:formatNumber value="${group.orderSales}" maxFractionDigits="2"/></a></td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9">
						<c:if test="${group.clicks>0}">
							<fmt:formatNumber value=" ${group.ordersPlaced*100/group.clicks}" maxFractionDigits="2"/>%
						</c:if>
						<c:if test="${group.clicks==0}">
							0%
						</c:if>
					</td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9">
						<span style="${group.acos>15?'color:red;font-weight: bold;':''}"><fmt:formatNumber value="${group.acos}" maxFractionDigits="2"/>%</span>
					</td>
				</tr>
				<c:forEach items="${group.advertisings}" var="advertising">
					<tr style="display: none" pro='name${i.count}group${j.count}'>
						<td></td>
						<td style="text-align: left;vertical-align: middle;background-color:#D2E9FF;"><a class="detail" rel="popover" data-content="Click here for details">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${advertising.sku}</a>
								<input type="hidden" value="${dto.name}" name="name"/>
						 		<input type="hidden" value="${group.groupName}" name="groupName"/>
						 		<input type="hidden" value="${advertising.keyword}" name="keyword"/>
								<input type="hidden" value="${advertising.sku}" name="sku"/>
						</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
							<a class="detail" rel="popover" data-content="Click here for details">${advertising.keyword}</a>
							<input type="hidden" value="${dto.name}" name="name"/>
						 	<input type="hidden" value="${group.groupName}" name="groupName"/>
						 	<input type="hidden" value="${advertising.keyword}" name="keyword"/>
							<input type="hidden" value="${advertising.sku}" name="sku"/>
						</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">${advertising.clicks}</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">${advertising.impressions}</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><fmt:formatNumber value="${advertising.ctr}" maxFractionDigits="2"/>%</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><fmt:formatNumber value="${advertising.totalSpend}" maxFractionDigits="2"/></td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><a href="#"  rel="popover" data-content="Your Max Cpc:${advertising.maxCpcBid > 0?advertising.maxCpcBid:''};Est 1 Page:${advertising.onePageBid >0?advertising.onePageBid:''}"><fmt:formatNumber value="${advertising.averageCPC}" maxFractionDigits="2"/></a></td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><a href="#"  rel="popover" data-content="Same:${advertising.sameSkuOrdersPlaced};Other:${advertising.otherSkuOrdersPlaced}">${advertising.ordersPlaced}</a></td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><a href="#"  rel="popover" data-content="Same:<fmt:formatNumber value="${advertising.sameSkuOrderSales}" maxFractionDigits="2"/>;Other:<fmt:formatNumber value="${advertising.otherSkuOrderSales}" maxFractionDigits="2"/>"><fmt:formatNumber value="${advertising.orderSales}" maxFractionDigits="2"/></a></td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
							<c:if test="${advertising.clicks>0}">
								<fmt:formatNumber value=" ${advertising.ordersPlaced*100/advertising.clicks}" maxFractionDigits="2"/>%
							</c:if>
							<c:if test="${advertising.clicks==0}">
								0%
							</c:if>
						</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
							<span style="${advertising.acos>15?'color:red;font-weight: bold;':''}"><fmt:formatNumber value="${advertising.acos}" maxFractionDigits="2"/>%</span>
						</td>
					</tr>
				</c:forEach>
			</c:forEach>
		</c:forEach>
		<tr>
			<td style="font-size: 18px;font-weight: bold;text-align: center;vertical-align: middle;background-color: #BBFFFF" colspan="2">Total</td>
			<td style="text-align: center;vertical-align: middle;background-color: #BBFFFF"></td>
			<td style="text-align: center;vertical-align: middle;background-color: #BBFFFF">${total.clicks}</td>
			<td style="text-align: center;vertical-align: middle;background-color: #BBFFFF">${total.impressions}</td>
			<td style="text-align: center;vertical-align: middle;background-color: #BBFFFF"><fmt:formatNumber value="${total.ctr}" maxFractionDigits="2"/>%</td>
			<td style="text-align: center;vertical-align: middle;background-color: #BBFFFF"><fmt:formatNumber value="${total.totalSpend}" maxFractionDigits="2"/></td>
			<td style="text-align: center;vertical-align: middle;background-color: #BBFFFF"><fmt:formatNumber value="${total.averageCPC}" maxFractionDigits="2"/></td>
			<td style="text-align: center;vertical-align: middle;background-color: #BBFFFF"><a href="#"  rel="popover" data-content="Same:${total.sameSkuOrdersPlaced};Other:${total.otherSkuOrdersPlaced}">${total.ordersPlaced}</a></td>
			<td style="text-align: center;vertical-align: middle;background-color: #BBFFFF"><a href="#"  rel="popover" data-content="Same:<fmt:formatNumber value="${total.sameSkuOrderSales}" maxFractionDigits="2"/>;Other:<fmt:formatNumber value="${total.otherSkuOrderSales}" maxFractionDigits="2"/>"><fmt:formatNumber value="${total.orderSales}" maxFractionDigits="2"/></a></td>
			<td style="text-align: center;vertical-align: middle;background-color: #BBFFFF">
				<c:if test="${total.clicks>0}">
					<fmt:formatNumber value=" ${total.ordersPlaced*100/total.clicks}" maxFractionDigits="2"/>%
				</c:if>
				<c:if test="${total.clicks==0}">
					0%
				</c:if>
			</td>
			<td style="text-align: center;vertical-align: middle;background-color: #BBFFFF">
				<span style="${total.acos>15?'color:red;font-weight: bold;':''}"><fmt:formatNumber value="${total.acos}" maxFractionDigits="2"/>%</span>
			</td>
		</tr>
		</tbody>
	</table>
</body>
</html>
