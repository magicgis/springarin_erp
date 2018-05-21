<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>采购计划管理</title>
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
			$(".euro").hide();
			
			$(".rowspan").each(function(){
				$(this).attr("rowspan",$("tr:visible[pro='"+$(this).parent().attr("pro")+"']").size());
			});
			
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
			});
			
			$("#expInventoryData").click(function(){
				window.location.href = "${ctx}/psi/purchaseOrder/expInventoryData";
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:30000});
			});
			
			
			$("#export").click(function(){
				top.$.jBox.confirm("导出采购计划汇总表吗?","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#forms").attr("action","${ctx}/amazoninfo/salesForecast/plan/export");
						$("#forms").submit();
						$("#forms").attr("action","${ctx}/amazoninfo/salesForecast/plan");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});	
			
			$("#operation").click(function(){
				loading('正在计算，请稍后...');
				$("#forms").submit();
			});	
			
			$(".editor").editable({validate:function(data){
				if(data){
					if(!ckeckNums(data)){
						return "库存必须是大于0的正整数!!";						
					}
				}
			},success:function(response,newValue){
    			$(this).parent().find(":hidden").val(newValue);
   				return true;
		}});
		});
		function ckeckNums(text) {
			var bool= /^(0|[1-9][0-9]*)$/.test(text);
			return bool;
		}
	</script>
</head>
<body>
	<form:form  class="breadcrumb form-search">
		<span style="font-size: 18px">当前为<fmt:formatDate value="${date}" pattern="yyyy年第w周"/></span>
		<input  name="dataDate" type="hidden" value="<fmt:formatDate value="${date}" pattern="yyyy-MM-dd"/>"/>
		&nbsp;<input id="operation" class="btn btn-info" type="button" value="运算采购数据"/>
		&nbsp;<input id="export" class="btn btn-primary" type="button" value="导出数据"/>
		&nbsp;&nbsp;&nbsp;&nbsp;<input id="expInventoryData" class="btn btn-info" type="button" value="采购参考报表"/>
	</form:form>
	<tags:message content="${message}"/>
	<form id="forms" action="${ctx}/amazoninfo/salesForecast/plan" method="post">
	<div style="overflow-x:scroll;">
		<table id="contentTable" class="table table-bordered table-condensed">
			<thead>
				<tr>
					<th colspan="5" style="text-align: center;vertical-align: middle">产品信息</th>
					<th colspan="8" style="text-align: center;vertical-align: middle">下单数量</th>
					<th colspan="8" style="text-align: center;vertical-align: middle">实际销售</th>
					<th colspan="20" style="text-align: center;vertical-align: middle">销售预测</th>
				</tr>
				<tr><th>序号</th><th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;产品名称&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
					<th>&nbsp;&nbsp;&nbsp;&nbsp;平&nbsp;&nbsp;台&nbsp;&nbsp;&nbsp;&nbsp;</th>
					<th>&nbsp;&nbsp;&nbsp;&nbsp;交&nbsp;&nbsp;期&nbsp;&nbsp;&nbsp;&nbsp;</th>
					<th>&nbsp;&nbsp;&nbsp;&nbsp;库&nbsp;&nbsp;存&nbsp;&nbsp;&nbsp;&nbsp;</th>
					<th>${dataDates[8]}周<br/>[${dataDates1[8]}]</th>
					<th>${dataDates[9]}周<br/>[${dataDates1[9]}]</th>
					<th>${dataDates[10]}周<br/>[${dataDates1[10]}]</th>
					<th>${dataDates[11]}周<br/>[${dataDates1[11]}]</th>
					<th>${dataDates[12]}周<br/>[${dataDates1[12]}]</th>
					<th>${dataDates[13]}周<br/>[${dataDates1[13]}]</th>
					<th>${dataDates[14]}周<br/>[${dataDates1[14]}]</th>
					<th>${dataDates[15]}周<br/>[${dataDates1[15]}]</th>
					<c:forEach items="${dataDates}" var="week" varStatus="i">
						<th ${i.index==8?'class=blue':''}>${week}周
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
							<td  style="text-align: center;vertical-align: middle" class="rowspan">${salesForecastEntry.key}</td>
						</c:if>
						<td style="text-align: center;vertical-align: middle">
							<c:if test="${countryData.key eq 'eu' }">
								Europe<a href="#" class="open" pro='${salesForecastEntry.key}'><span class="icon-plus"></span></a>
							</c:if>
							<c:if test="${countryData.key ne 'eu' }">
								${fns:getDictLabel(countryData.key,'platform','德国Ebay')}
							</c:if>
						</td>
						<td  style="text-align: center;vertical-align: middle" >${tranTime[salesForecastEntry.key][countryData.key]}</td>
						<td  style="text-align: center;vertical-align: middle" >
							<c:if test="${!fn:contains('fr,ebay,it,de,es',countryData.key)}">
								<a href="#"  data-type="text" data-pk="1"  data-title="Fill in Inventory" class="editable editable-click editor" data-original-title="" title="">
									${inventorys.inventorys[salesForecastEntry.key][countryData.key]}
								</a>
								<input value="${inventorys.inventorys[salesForecastEntry.key][countryData.key]}" type="hidden" name="inventorys['${salesForecastEntry.key}']['${countryData.key}']"/>
							</c:if>
						</td>
						<c:forEach var="i"  begin="0" end="7">
							<td  style="text-align: center;vertical-align: middle;color: ${salesForecastEntry.value.forecastData[countryData.key][dataDates[i+8]]<0?'red':''}"  >${salesForecastEntry.value.forecastData[countryData.key][dataDates[i+8]]}</td>
						</c:forEach>
						<c:forEach items="${dataDates}" var="week" varStatus="i">
							<c:set var="data" value="${salesForecastEntry.value.data[countryData.key][week].quantityForecast}" />
							<c:set var="realData" value="${salesForecastEntry.value.realData[countryData.key][week]}" />
							<td ${i.index==8?'class=blue':''} style="text-align: center;vertical-align: middle">
								<c:if test="${i.index<8}">
									${realData}
								</c:if>
								<c:if test="${i.index>=8}">
									${data}
								</c:if>
							</td>
						</c:forEach>
					</tr>
				</c:forEach>
			</c:forEach>
			</tbody>
		</table>
	</div>
	</form>
</body>
</html>
