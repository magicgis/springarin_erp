<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>销量预测管理</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{background-color:#D2E9FF;font-style: italic;font-weight: bold;}
		.euro{background-color:#D2E9FF;}
	</style>
	
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
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$(".euro").hide();
			
			$(".rowspan").each(function(){
				$(this).attr("rowspan",$("tr:visible[pro='"+$(this).parent().attr("pro")+"']").size());
			});
			
			var container = $('body');
			$(".open").click(function(){
				var tr = $(this).parent().parent();
				if($(this).find("span").attr("class")=='icon-plus'){
					$(".euro[pro='"+$(this).attr("pro")+"']").show();
					$(this).find("span").attr("class","icon-minus");
				}else{
					$(".euro[pro='"+$(this).attr("pro")+"']").hide();
					$(this).find("span").attr("class","icon-plus");
				}
				tr.find(".rowspan").each(function(){
					$(this).attr("rowspan",$("tr:visible[pro='"+tr.attr("pro")+"']").size());
				});
				$(this).focus();
				scrollTo = $(this);
				container.animate({
					  scrollTop: Math.abs(scrollTo.offset().top - container.offset().top)-20
				});
			});
			
			
			$("#export").click(function(){
				top.$.jBox.confirm("导出指标汇总表吗?","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/salesForecast/export");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/salesForecast");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});	
			
		});
		function ckeckNums(text) {
			var bool= /^(0|[1-9][0-9]*)$/.test(text);
			return bool;
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<li class="${salesForecast.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
		</c:forEach>	
		<li class="${salesForecast.country eq 'ebay' ?'active':''}"><a class="countryHref" href="#" key="ebay">德国Ebay</a></li>
		<li class="${salesForecast.country eq 'total' ?'active':''}"><a class="countryHref" href="#" key="total">指标汇总</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="salesForecast" action="${ctx}/amazoninfo/salesForecast/" method="post" class="breadcrumb form-search">
		<input id="selectM" style="width: 100px" onclick="WdatePicker({isShowWeek:true,firstDayOfWeek:1,onpicked:function(){$('input[name=dataDate]').val($(this).val());$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" value="<fmt:formatDate value="${salesForecast.dataDate}" pattern="yyyy-MM-dd"/>" class="input-small"/>
		<span style="font-size: 18px">当前选择日期为<fmt:formatDate value="${salesForecast.dataDate}" pattern="yyyy年第w周"/></span>
		<input  name="country" type="hidden" value="${salesForecast.country}"/>
		<input  name="dataDate" type="hidden" value="<fmt:formatDate value="${salesForecast.dataDate}" pattern="yyyy-MM-dd"/>"/>
		<label>产品名称：</label><form:input path="productName" htmlEscape="false" maxlength="50" class="input-small"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		&nbsp;<input id="export" class="btn btn-primary" type="button" value="导出"/>
	</form:form>
	<tags:message content="${message}"/>
	<div style="overflow-x:scroll;">
		<table id="contentTable" class="table table-bordered table-condensed">
			<thead>
				<tr><th>序号</th><th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;产品名称&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
					<th>&nbsp;&nbsp;&nbsp;&nbsp;平&nbsp;&nbsp;台&nbsp;&nbsp;&nbsp;&nbsp;</th>
					<c:forEach items="${dataDates}" var="week" varStatus="i">
						<th ${i.index==3?'class=blue':''}>${week}周
							<br/>[${dataDates1[i.index]}]
						</th>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${data}" var="salesForecastEntry" varStatus="i">
				<c:forEach items="${salesForecastEntry.value.data}" var="countryData" varStatus="j">
					<tr pro="${salesForecastEntry.key}" class="${fn:contains('fr,de,it,es,ebay',countryData.key)?' euro':''}">
						<c:if test="${j.index == 0}">
							<td  style="text-align: center;vertical-align: middle" class="rowspan" >${i.count}</td>
							<td  style="text-align: center;vertical-align: middle" class="rowspan"><a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${salesForecastEntry.key}">${salesForecastEntry.key}</a></td>
						</c:if>
						<td style="text-align: center;vertical-align: middle">
							<c:if test="${countryData.key eq 'eu' }">
								Europe<a href="#" class="open" pro='${salesForecastEntry.key}'><span class="icon-plus"></span></a>
							</c:if>
							<c:if test="${countryData.key ne 'eu' }">
								${fns:getDictLabel(countryData.key,'platform','德国Ebay')}
							</c:if>
						</td>
						<c:forEach items="${dataDates}" var="month" varStatus="i">
							<c:set var="data" value="${salesForecastEntry.value.data[countryData.key][week].quantityForecast}" />
							<c:set var="realData" value="${salesForecastEntry.value.realData[countryData.key][week]}" />
							<td ${i.index==3?'class=blue':''} style="text-align: center;vertical-align: middle;">
								<c:if test="${empty realData}">
									${data}
								</c:if>
								<c:if test="${not empty realData && not empty data }">
									<c:choose>
										<c:when test="${realData eq data}">
											<a href="#" style="color:green;font-weight: bold;" rel="popover" data-content="预测销量:${data}">${realData}</a>
										</c:when>
										<c:when test="${(realData gt data*1.2) || (realData lt data/1.2)}">
											<a href="#" style="color:red" rel="popover" data-content="预测销量:${data}">${realData}</a>
										</c:when>
										<c:otherwise>
											<a href="#" rel="popover" data-content="预测销量:${data}">${realData}</a>
										</c:otherwise>
									</c:choose>
								</c:if>
								<c:if test="${not empty realData && empty data }">
									${realData}
								</c:if>
							</td>
						</c:forEach>
					</tr>
				</c:forEach>
			</c:forEach>
			</tbody>
		</table>
	</div>
</body>
</html>
