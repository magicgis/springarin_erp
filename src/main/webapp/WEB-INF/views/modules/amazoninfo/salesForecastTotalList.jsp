<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>销量预测管理(数据来自产品当前预测方案)</title>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{background-color:#D2E9FF;font-style: italic;font-weight: bold;}
		/* .euro{background-color:#D2E9FF;} */
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
				window.location.href = "${ctx}/amazoninfo/salesForecastByMonth?country="+$(this).attr("key");
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
						$("#searchForm").attr("action","${ctx}/amazoninfo/salesForecastByMonth/exportSalesForecastByMonth");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/salesForecastByMonth/");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});	
			 
			 $("#export2").click(function(){
					top.$.jBox.confirm("导出指标汇总表吗?","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
						if(v=="ok"){
							$("#searchForm").attr("action","${ctx}/amazoninfo/salesForecastByMonth/exportSalesForecastByMonth2");
							$("#searchForm").submit();
							$("#searchForm").attr("action","${ctx}/amazoninfo/salesForecastByMonth/");
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
			 });	
			 
			 $("#export0").click(function(){
					top.$.jBox.confirm("导出普通产品指标汇总表吗?","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
						if(v=="ok"){
							$("#searchForm").attr("action","${ctx}/amazoninfo/salesForecastByMonth/exportSalesForecastByMonthIsNew?type=0");
							$("#searchForm").submit();
							$("#searchForm").attr("action","${ctx}/amazoninfo/salesForecastByMonth/");
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				});	
			 
			 $("#export1").click(function(){
					top.$.jBox.confirm("导出新品指标汇总表吗?","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
						if(v=="ok"){
							$("#searchForm").attr("action","${ctx}/amazoninfo/salesForecastByMonth/exportSalesForecastByMonthIsNew?type=1");
							$("#searchForm").submit();
							$("#searchForm").attr("action","${ctx}/amazoninfo/salesForecastByMonth/");
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
				});	
			
			<shiro:hasPermission name="amazoninfo:feedSubmission:all">
			$(".editor").editable({mode:'inline',validate:function(data){
				if(data){
					if(!ckeckNums(data)){
						return "销售量必须是大于0的正整数!!";						
					}
				}
			},success:function(response,newValue){
        			var param = {};
       				var $this = $(this);
       				var oldVal = $this.text();
       				var forecastId = $this.parent().find(".forecastId");
       				var country = $this.parent().find(".country").val();
       				if(forecastId.val()){
       					param.id = forecastId.val();
       					param.quantityForecast = newValue;
       				}else{
       					param.productName=$this.parent().find(".productName").val();
       					param.productId = $this.parent().find(".productId").val();
       					param.quantityForecast = newValue;
           				param.country = country;	
           				param.month = $this.parent().find(".month").val();
       				}
       				$.get("${ctx}/amazoninfo/salesForecastByMonth/save?"+$.param(param),function(data){
       					if(!$.isNumeric(data)){
       						$this.text(oldVal);
       					}else{
       						forecastId.val(data);
       						$.jBox.tip("预测值保存成功！", 'info',{timeout:1000});
       					}
       				});
       				return true;
			}});
			</shiro:hasPermission>
			
		});
		function ckeckNums(text) {
			var bool= /^(0|[1-9][0-9]*)$/.test(text);
			return bool;
		}
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$('#searchForm').attr('action','${ctx}/amazoninfo/salesForecastByMonth');
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<shiro:hasPermission name="amazoninfo:feedSubmission:all">
					<li class="${salesForecast.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
				</shiro:hasPermission>
				<shiro:lacksPermission name="amazoninfo:feedSubmission:all">
					<shiro:hasPermission name="amazoninfo:feedSubmission:${dic.value}">
						<li class="${salesForecast.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
					</shiro:hasPermission>
				</shiro:lacksPermission>
			</c:if>
		</c:forEach>	
		<li class="active"><a class="countryHref" href="#" key="total">指标汇总</a></li>
		<%--<li><a href="${ctx}/amazoninfo/salesForecastRecord/">销量预测审批
			<shiro:hasPermission name="amazoninfo:feedSubmission:all">
				<c:if test="${countQuantity>0}"><span style="color:red">(${countQuantity})</span></c:if>
			</shiro:hasPermission>
			</a>
		</li> --%>
	</ul>
	<form:form id="searchForm" modelAttribute="salesForecast" action="${ctx}/amazoninfo/salesForecastByMonth/" method="post" class="breadcrumb form-search">
		<input  name="country" type="hidden" value="${salesForecast.country}"/>
		<label>产品名称：</label><form:input path="productName" htmlEscape="false" maxlength="50" class="input-small"/>
		&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		 &nbsp;<!-- <input id="export" class="btn btn-primary" type="button" value="导出"/> -->
		 <div class="btn-group">
						   <button type="button" class="btn btn-primary">导出</button>
						   <button type="button" class="btn btn-primary dropdown-toggle"  data-toggle="dropdown">
						      <span class="caret"></span>
						      <span class="sr-only"></span>
						   </button>
						   <ul class="dropdown-menu" ><!-- href="${ctx}/psi/fbaInbound/printFbaInboundPdf?id=${fbaInbound.id}"  -->
						   	  <li><a id="export">导出</a></li>	
						   	  <li><a id="export2">导出(供应链)</a></li>
							  <%--<li><a id="export1">新品导出</a></li>
							  <li><a id="export0">普通品导出</a></li> --%>
						   </ul>
		</div>
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	</form:form>
	<div class="alert alert-info"><strong>预测产品为非新品和非淘汰品.</strong></div>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead>
			<tr><th>序号</th><th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;产品名称&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
				<th>&nbsp;&nbsp;&nbsp;&nbsp;平&nbsp;&nbsp;台&nbsp;&nbsp;&nbsp;&nbsp;</th>
				<c:forEach items="${dates}" var="month" varStatus="i">
					<th style="text-align: center;vertical-align: middle;">
						${month}月
					</th>
				</c:forEach>
				<c:forEach items="${dates1}" var="month" varStatus="i">
					<th style="text-align: center;vertical-align: middle${i.index==0?';background-color:#D2E9FF;':''}">
						${month}月
					</th>
				</c:forEach>
				<%--<shiro:hasPermission name="amazoninfo:feedSubmission:all">
				<th>操作</th>
				</shiro:hasPermission> --%>
			</tr>
		</thead>
		<tbody>
		<c:set var="pi" value="0" />
		<c:forEach items="${page.list}" var="product" >
			<c:set var="name" value="${product[0] }" />
			    <c:set var="pi" value="${pi+1}" />
				<c:set var="ii" value="0" />
				
				<c:forEach items="${countrys}" var="country" varStatus="f">
				    <c:if test="${fn:contains(product[1],country) || 'eu' eq country ||'total' eq country}">
				    <!-- 泛欧产品,欧洲市场只显示德国平台 -->
				    <c:if test="${hasPower[name] eq '2' ||(hasPower[name] eq '0' && !fn:contains('uk,fr,it,es,eu',country)) ||(hasPower[name] eq '1' && !fn:contains('fr,it,es,eu',country)) }">
						<tr pro="${name}" class="${'total' eq country?'':'euro'}" style="${country eq 'eu'?'background-color:#D2E9FF;':''}">
							 <c:if test="${ii == 0}">
								<td  style="text-align: center;vertical-align: middle" class="rowspan" >${pi}</td>
								<td  style="text-align: center;vertical-align: middle" class="rowspan">
									<a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${name}">${name}</a>
									<c:if test="${hasPower[name] eq '2' }"><span style="color: gray">非泛欧</span></c:if>
									<c:if test="${hasPower[name] eq '1' }"><span style="color: gray">四国泛欧</span></c:if>
								</td>
							 </c:if> 
							<td style="text-align: center;vertical-align: middle;"><!-- 平台栏 -->
								<c:if test="${country eq 'eu' }">
									Europe
								</c:if>
								<c:if test="${country ne 'eu'&&country ne 'total' }">
									${fns:getDictLabel(country,'platform','')}
								</c:if>
								<c:if test="${country eq 'total' }">
								   Total<a href="#" class="open" pro='${name}'><span class="icon-plus"></span></a>
								</c:if>
							</td>
							<!-- 前三个月实际销量和预测销量 -->
							<c:forEach items="${dates}" var="month" varStatus="i">
									<c:set var="authentication" value="${data[name][country][month].quantityAuthentication}" />
									<c:set var="forecast" value="${authentication>0?authentication:data[name][country][month].quantityForecast}" />
									<c:set var="realData" value="${empty saleData[name][country][month]?0:saleData[name][country][month]}" />
									<c:if test="${'de' eq country && hasPower[name] eq '0'}">
										<c:set var="realData" value="${saleData[name][country][month] + saleData[name]['uk'][month] + saleData[name]['fr'][month] + saleData[name]['it'][month] + saleData[name]['es'][month]}" />
									</c:if>
									<c:if test="${'de' eq country && hasPower[name] eq '1'}">
										<c:set var="realData" value="${saleData[name][country][month] + saleData[name]['fr'][month] + saleData[name]['it'][month] + saleData[name]['es'][month]}" />
									</c:if>
									<td  style="text-align: center;vertical-align: middle;">
										<c:if test="${empty realData}">
											${forecast}
										</c:if>
										<c:if test="${not empty realData && not empty forecast}">
											<c:choose>
												<%--<c:when test="${realData eq forecast}">
													<a href="#" style="color:green;font-weight: bold;" rel="popover" data-content="预测销量:${forecast}">${realData}</a>
												</c:when> --%>
												<c:when test="${realData gt forecast*1.2}">
													<a href="#" style="color:green" rel="popover" data-content="预测销量:${forecast}">${realData}</a>
												</c:when>
												<c:when test="${realData lt forecast/1.2}">
													<a href="#" style="color:red" rel="popover" data-content="预测销量:${forecast}">${realData}</a>
												</c:when>
												<c:otherwise>
													<a href="#" rel="popover" data-content="预测销量:${forecast}">${realData}</a>
												</c:otherwise>
											</c:choose>
										</c:if>
										<c:if test="${not empty realData && empty forecast }">
											${realData}
										</c:if>
									</td>
							</c:forEach>
							<!-- 后六个月预测 -->
							<c:forEach items="${dates1}" var="month" varStatus="i">
								<c:if test="${i.index>0}">
									<c:set var="oldForecast" value="${forecast}" />
								</c:if>
								<c:set var="authentication" value="${data[name][country][month].quantityAuthentication}" />
								<c:set var="forecast" value="${data[name][country][month].quantityForecast}" />
								<c:set var="realData" value="${empty saleData[name][country][month]?0:saleData[name][country][month]}" />
								<c:if test="${'de' eq country && hasPower[name] eq '0'}">
									<c:set var="realData" value="${saleData[name][country][month] + saleData[name]['uk'][month] + saleData[name]['fr'][month] + saleData[name]['it'][month] + saleData[name]['es'][month]}" />
								</c:if>
								<c:if test="${'de' eq country && hasPower[name] eq '1'}">
									<c:set var="realData" value="${saleData[name][country][month] + saleData[name]['fr'][month] + saleData[name]['it'][month] + saleData[name]['es'][month]}" />
								</c:if>
								<c:set var="realDataLast" value="${empty saleData[name][country][dates[2]]?0:saleData[name][country][dates[2]]}" />
								<c:if test="${'de' eq country && hasPower[name] eq '0'}">
									<c:set var="realDataLast" value="${saleData[name][country][dates[2]] + saleData[name]['uk'][dates[2]] + saleData[name]['fr'][dates[2]] + saleData[name]['it'][dates[2]] + saleData[name]['es'][dates[2]]}" />
								</c:if>
								<c:if test="${'de' eq country && hasPower[name] eq '1'}">
									<c:set var="realDataLast" value="${saleData[name][country][dates[2]] + saleData[name]['fr'][dates[2]] + saleData[name]['it'][dates[2]] + saleData[name]['es'][dates[2]]}" />
								</c:if>
								<td ${i.index==0?'class=blue':''} style="text-align: center;vertical-align: middle;">
									<c:if test="${realData==0}">
										<c:if test="${'eu' ne country&&'total' ne country}">
											<a href="#" style="${fns:getUser().id ==data[name][country][month].lastUpdateBy.id?'color: fuchsia;font-weight: bold;':''}"  data-type="text" data-pk="1"  data-title="update" data-original-title="" title="">${authentication>0?authentication:forecast}</a>
											<c:if test="${authentication>0 && authentication != forecast}"><br/><span style="color: #08c;">系统预测：${forecast}</span></c:if>
											<input class="forecastId" type="hidden" value="${data[name][country][month].id}"/>
											<input class="productName" type="hidden" value="${name}"/>
											<input class="productId" type="hidden" value="${product[2]}"/>
											<input class="month" type="hidden" value="${month}"/>
											<input class="country" type="hidden" value="${country}"/>
										</c:if>
										<c:if test="${'total' eq country}">
											<a href="#" style="${fns:getUser().id ==data[name][country][month].lastUpdateBy.id?'color: fuchsia;font-weight: bold;':''}"  data-type="text" data-pk="1"  data-title="update" data-original-title="" title="">${forecast}</a>
										</c:if>
										<c:if test="${'eu' eq country}">
											${forecast}
										</c:if>
									</c:if>
									<c:if test="${realData >0 && not empty forecast }">
										<c:if test="${'eu' ne country&&'total' ne country}">
											<a href="#" style="${fns:getUser().id ==data[name][country][month].lastUpdateBy.id?'color: fuchsia;font-weight: bold;':''}"  rel="popover" data-type="text" data-pk="1"  data-title="实时销量:${realData}" data-original-title="" title="">${authentication>0?authentication:forecast}</a>
											<c:if test="${authentication>0 && authentication != forecast}"><br/><span style="color: #08c;">系统预测：${forecast}</span></c:if>
											<input class="forecastId" type="hidden" value="${data[name][country][month].id}"/>
											<input class="productName" type="hidden" value="${name}"/>
											<input class="productId" type="hidden" value="${product[2]}"/>
											<input class="month" type="hidden" value="${month}"/>
											<input class="country" type="hidden" value="${country}"/>
										</c:if>
										<c:if test="${'eu' ne country&&'total' eq country}">
											<a href="#" rel="popover" style="${fns:getUser().id ==data[name][country][month].lastUpdateBy.id?'color: fuchsia;font-weight: bold;':''}"  data-type="text" data-pk="1"  data-title="实时销量:${realData}" data-original-title="" title="">${forecast}</a>
											
										</c:if>
										<c:if test="${i.index>0 && 'eu' eq country}">
											<a href="#" style="color:red" rel="popover" data-content="实时销量:${realData}">${forecast}</a>
										</c:if>
										<c:if test="${i.index==0 && 'eu' eq country}">
											<a href="#" rel="popover" data-content="实时销量:${realData}">${forecast}</a>
										</c:if>
									</c:if>
									<c:if test="${i.index==0 && not empty forecast && realDataLast >0 }">
										<c:if test="${(forecast/realDataLast) >1.15 }">
											<br/><span style="color: red;font-weight: bold;">预测增幅  ${fns:roundUp(((forecast/realDataLast)-1)*100)}%</span>											
										</c:if>									
									</c:if>
									<c:if test="${i.index>0 && not empty forecast && not empty oldForecast && oldForecast>0}">
										<c:if test="${(forecast/oldForecast) > 1.15}">
											<br/><span style="color: red;font-weight: bold;">预测增幅 ${fns:roundUp((forecast/oldForecast-1)*100)}%</span>											
										</c:if>									
									</c:if>
								</td>
							</c:forEach>
							<%--<td><c:if test="${'total' ne country && 'eu' ne country}">修改${country }</c:if></td>--%>
						</tr>
						<c:set var="ii" value="${ii+1}" />
						</c:if>
					</c:if>
				</c:forEach>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
