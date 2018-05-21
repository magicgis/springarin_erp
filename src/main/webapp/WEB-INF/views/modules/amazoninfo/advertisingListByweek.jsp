<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>广告周报表</title>
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
				params.country = '${advertisingByWeek.country}';
				$(this).parent().find(":hidden").each(function(){
					params[$(this).attr('name')] = encodeURI($(this).val());					
				});
				window.location.href = "${ctx}/amazoninfo/advertisingByWeek/detail?"+$.param(params);
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
		});
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		
		//ByWeek
		function date1Week(){
			var week = $dp.cal.getP('y')+ "" +$dp.cal.getP('W','WW');
			$("#start").attr("value",week);
			$("#searchForm").submit();
		}
		
		function date2Week(){
			var week = $dp.cal.getP('y')+ "" +$dp.cal.getP('W','WW');
			$("#end").attr("value",week);
			$("#searchForm").submit();
		}
		
		function exportData(a){
			var params = {};
			params.startWeek = $("#start").val();
			params.endWeek = $("#end").val();
			params.country = $(".nav-tabs .active a").attr("key");
			$(a).attr("href","${ctx}/amazoninfo/advertising/exportWeek?"+$.param(params));
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${advertisingByWeek.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>
	<form:form id="searchForm" modelAttribute="advertisingByWeek" action="${ctx}/amazoninfo/advertising/listByWeek" method="post" class="breadcrumb form-search">
		<div style="height: 30px">
			<div style="float: left;">
				<span id="date" >
					<input  style="width: 100px" onclick="WdatePicker({onpicked:date1Week,isShowWeek:true,weekMethod:'MSExcel',errDealMode:3,firstDayOfWeek:0});" readonly="readonly"  class="Wdate" type="text" name="startWeek" value="${advertisingByWeek.startWeek}" class="input-small" id="start"/>
					&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({onpicked:date2Week,isShowWeek:true,weekMethod:'MSExcel',errDealMode:3,firstDayOfWeek:0});" readonly="readonly"  class="Wdate" type="text" name="endWeek" value="${advertisingByWeek.endWeek}" id="end" class="input-small"/>
					&nbsp;&nbsp;&nbsp;&nbsp;
				</span>
				<input  name="country" type="hidden" value="${advertisingByWeek.country}"/>
				<label>关键字/sku：</label><form:input path="keyword" htmlEscape="false" maxlength="50" class="input-small"/>
				&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
				&nbsp;&nbsp;<a href="#" class="btn btn-success" onclick="exportData(this)">导出</a>
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
				<th style="text-align: center;vertical-align: middle;" class="sort totalSpend">Total <br/>Spend</th>
				
				<th style="text-align: center;vertical-align: middle;" class="sort weekSameSkuUnitsOrdered">Orders<br/> Placed</th>
				<th style="text-align: center;vertical-align: middle;" class="sort weekSameSkuUnitsLirun">Orders<br/>Profit</th>
				<th style="text-align: center;vertical-align: middle;" class="sort weekOtherSkuUnitsOrdered">Other Orders<br/> Placed</th>
				<th style="text-align: center;vertical-align: middle;" class="sort weekParentSkuUnitsOrdered">Variations Orders<br/> Placed</th>
				<th style="text-align: center;vertical-align: middle;" class="sort weekParentSkuUnitsLirun">Variations <br/>Profit</th>
				
				<th style="text-align: center;vertical-align: middle;" class="sort conversion">Conversion</th>
				<th style="text-align: center;vertical-align: middle;" class="sort roi">ROI</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${total.dtos}" var="dto" varStatus="i">
			<tr>
				<td style="text-align: center;vertical-align: middle;">${i.count}</td>
				<td style="text-align: left;vertical-align: middle;">${dto.name}
					 <a href="#" class="grpOpen" pro='name${i.count}'><span class="icon-plus"></span></a>
					 <input type="hidden" value="${dto.name}" name="name"/>
				</td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;">${dto.clicks}</td>
				<td style="text-align: center;vertical-align: middle;">${dto.impressions}</td>
				<td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${dto.totalSpend}" maxFractionDigits="2"/></td>
				
				<td style="text-align: center;vertical-align: middle;">${dto.weekSameSkuUnitsOrdered}</td>
				<td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${dto.weekSameSkuUnitsLirun}" maxFractionDigits="2"/></td>
				<td style="text-align: center;vertical-align: middle;">${dto.weekOtherSkuUnitsOrdered}</td>
				<td style="text-align: center;vertical-align: middle;">${dto.weekParentSkuUnitsOrdered}</td>
				<td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${dto.weekParentSkuUnitsLirun}" maxFractionDigits="2"/></td>
				
				<td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${dto.conversion}" maxFractionDigits="2"/>%</td>
				<td style="text-align: center;vertical-align: middle;">
					<span><fmt:formatNumber value="${dto.roi}" maxFractionDigits="2"/></span>
				</td>
				
			</tr>
			<c:forEach items="${dto.groups}" var="group" varStatus="j">
				<tr pro="name${i.count}" style="display: none">
					<td></td>
					<td style="text-align: left;vertical-align: middle;background-color: #f9f9f9;font-weight: bold;">
						&nbsp;&nbsp;${group.groupName}
						<a href="#" class="wkeyOpen" pro='name${i.count}group${j.count}'><span class="icon-plus"></span></a>
						 <input type="hidden" value="${dto.name}" name="name"/>
						 <input type="hidden" value="${group.groupName}" name="groupName"/>
					</td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9"></td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9">${group.clicks}</td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9">${group.impressions}</td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9"><fmt:formatNumber value="${group.totalSpend}" maxFractionDigits="2"/></td>
					
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9">${group.weekSameSkuUnitsOrdered}</td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9"><fmt:formatNumber value="${group.weekSameSkuUnitsLirun}" maxFractionDigits="2"/></td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9">${group.weekOtherSkuUnitsOrdered}</td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9">${group.weekParentSkuUnitsOrdered}</td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9"><fmt:formatNumber value="${group.weekParentSkuUnitsLirun}" maxFractionDigits="2"/></td>
					
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9"><fmt:formatNumber value="${group.conversion}" maxFractionDigits="2"/>%</td>
					<td style="text-align: center;vertical-align: middle;background-color: #f9f9f9">
						<span><fmt:formatNumber value="${group.roi}" maxFractionDigits="2"/></span>
					</td>
				
				</tr>
				<c:forEach items="${group.advertisings}" var="advertisingByWeek">
					<tr style="display: none" pro='name${i.count}group${j.count}'>
						<td></td>
						<td style="text-align: left;vertical-align: middle;background-color:#D2E9FF;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;${advertisingByWeek.sku}[${advertisingByWeek.type}]
								<input type="hidden" value="${dto.name}" name="name"/>
						 		<input type="hidden" value="${group.groupName}" name="groupName"/>
						 		<input type="hidden" value="${advertisingByWeek.keyword}" name="keyword"/>
								<input type="hidden" value="${advertisingByWeek.sku}" name="sku"/>
						</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
							${advertisingByWeek.keyword}
							<input type="hidden" value="${dto.name}" name="name"/>
						 	<input type="hidden" value="${group.groupName}" name="groupName"/>
						 	<input type="hidden" value="${advertisingByWeek.keyword}" name="keyword"/>
							<input type="hidden" value="${advertisingByWeek.sku}" name="sku"/>
						</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">${advertisingByWeek.clicks}</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">${advertisingByWeek.impressions}</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">${advertisingByWeek.totalSpend}</td>
						
						<td style="text-align: center;vertical-align: middle;background-color: #D2E9FF;">${advertisingByWeek.weekSameSkuUnitsOrdered}</td>
						<td style="text-align: center;vertical-align: middle;background-color: #D2E9FF;"><fmt:formatNumber value="${advertisingByWeek.weekSameSkuUnitsLirun}" maxFractionDigits="2"/></td>
						<td style="text-align: center;vertical-align: middle;background-color: #D2E9FF;">${advertisingByWeek.weekOtherSkuUnitsOrdered}</td>
						<td style="text-align: center;vertical-align: middle;background-color: #D2E9FF;">${advertisingByWeek.weekParentSkuUnitsOrdered}</td>
						<td style="text-align: center;vertical-align: middle;background-color: #D2E9FF;"><fmt:formatNumber value="${advertisingByWeek.weekParentSkuUnitsLirun}" maxFractionDigits="2"/></td>
						
						<td style="text-align: center;vertical-align: middle;background-color: #D2E9FF;"><fmt:formatNumber value="${advertisingByWeek.conversion}" maxFractionDigits="2"/>%</td>
						<td style="text-align: center;vertical-align: middle;background-color: #D2E9FF;">
							<span><fmt:formatNumber value="${advertisingByWeek.roi}" maxFractionDigits="2"/></span>
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
			<td style="text-align: center;vertical-align: middle;background-color: #BBFFFF"><fmt:formatNumber value="${total.totalSpend}" maxFractionDigits="2"/></td>
			
			<td style="text-align: center;vertical-align: middle;background-color: #BBFFFF">${total.weekSameSkuUnitsOrdered}</td>
			<td style="text-align: center;vertical-align: middle;background-color: #BBFFFF"><fmt:formatNumber value="${total.weekSameSkuUnitsLirun}" maxFractionDigits="2"/></td>
			<td style="text-align: center;vertical-align: middle;background-color: #BBFFFF">${total.weekOtherSkuUnitsOrdered}</td>
			<td style="text-align: center;vertical-align: middle;background-color: #BBFFFF">${total.weekParentSkuUnitsOrdered}</td>
			<td style="text-align: center;vertical-align: middle;background-color: #BBFFFF"><fmt:formatNumber value="${total.weekParentSkuUnitsLirun}" maxFractionDigits="2"/></td>
			
			<td style="text-align: center;vertical-align: middle;background-color: #BBFFFF"><fmt:formatNumber value="${total.conversion}" maxFractionDigits="2"/>%</td>
			<td style="text-align: center;vertical-align: middle;background-color: #BBFFFF">
				<span><fmt:formatNumber value="${total.roi}" maxFractionDigits="2"/></span>
			</td>
		</tr>
		</tbody>
	</table>
</body>
</html>
