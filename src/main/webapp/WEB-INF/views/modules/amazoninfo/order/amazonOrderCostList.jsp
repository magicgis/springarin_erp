<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊结算报表</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<script type="text/javascript">
		
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
	
		
		$(document).ready(function(){
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$(this).tab('show');
			});
		    
		    
		    $(".totalf").each(function(){
				var i = $(this).parent().find("td").index($(this));
				var num = 0;
				$("#"+$(this).attr("tid")+" tr").find("td:eq("+i+")").each(function(){
					if($.isNumeric($(this).text())){
						num += parseFloat($(this).text());
					}
				});
				$(this).text(num.toFixed(2));
			});
			
			      
			$("#totalTb").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				 "aaSorting": [[ 3, "desc" ]]
			});
		    
			<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
			$("#${dic.value}Tb").dataTable({
			  	"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				"aaSorting": [[ 3, "desc" ]]
			});
			</c:if></c:forEach>
		
			
			$("#expExcel").click(function(){
				var params = {};
				params.createDate=$("input[name='createDate']").val();
				params.dataDate=$("input[name='dataDate']").val();
				window.location.href = "${ctx}/custom/productProblem/expProblem?"+$.param(params);
				top.$.jBox.tip("<spring:message code='amazon_order_tips25'/> ！", 'loading',{timeout:10000});
			});
			
			var country = $("#country").val();
			if(country != null){
			    $("#"+country+"A i").click();
			}
		
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a class="countryHref" href="#total" >Total</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li><a id="${dic.value}A"  class="countryHref" href="#${dic.value}"  key="${dic.value}" >${dic.label}<i></i></a></li>
			</c:if>
		</c:forEach>
	</ul>
	
	<form id="searchForm" action="${ctx}/amazoninfo/orderCost" method="post" class="breadcrumb form-search">
		<label>Settlement date：</label>
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="startTime" value="<fmt:formatDate value="${startTime}" pattern="yyyy-MM-dd" />" id="startTime" class="input-small"/>
				&nbsp;&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="endTime" value="<fmt:formatDate value="${endTime}" pattern="yyyy-MM-dd"/>" class="input-small" id="endTime"/>
		&nbsp;&nbsp;&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="Search"/>
		<input type="hidden" id="country" name="country" value="${country}"></input>	
	</form>
	<div class="tab-content">
	<div id="total" class="tab-pane active">
	<table id="totalTb" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="width: 8%">platfrom</th>
				<th style="width: 8%">settlement_id</th>
				<th style="width: 10%">start</th>
				<th style="width: 10%">end</th>
				<th style="width: 8%">order product<br/>charges</th>
				<th style="width: 8%">amazon fee</th>
				<th style="width: 8%">order<br/>other fee</th>
				<th style="width: 8%">refund total</th>
				<th style="width: 8%">storage fee</th>
				<th style="width: 8%">other total</th>
				<th style="width: 8%">income</th>
				<th style="width: 4%">currency</th>
				<th style="width: 20%">operator</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${results}" var="result">
			<tr>
				<td>${fns:getDictLabel(result[1],'platform','')}</td>
				<td>${result[0]}</td>
				<td>${result[2]}</td>
				<td>${result[3]}</td>
				<td>${result[4]}</td>
				<td>${result[5]}</td>
				<td>${result[6]}</td>
				<td>${result[7]}</td>
				<td>${result[8]}</td>
				<td>${result[9]}</td>
				<td>${result[4] + result[5] + result[6] + result[7] + result[8] + result[9]}</td>
				<td>${currencySymbol[countryMap[result[0]]] }</td>
				<td>
				<div class="btn-group">
					<button type="button" class="btn btn-primary" style="background:#08c">Export</button>
					<button type="button" class="btn btn-primary dropdown-toggle" style="background:#08c"  data-toggle="dropdown">
						<span class="caret"></span>
						<span class="sr-only"></span>
					</button>
					<ul class="dropdown-menu" id="allExport">
						<li><a href="${ctx}/amazoninfo/orderCost/expOrderCost?settlementId=${result[0]}">Export statements</a></li>
						<li><a href="${ctx}/amazoninfo/orderCost/expOrderCostDetail?settlementId=${result[0]}">Export orders billing details</a></li>
					</ul>
				</div>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</div>
	
	<c:forEach items="${fns:getDictList('platform')}" var="dic">
		<c:if test="${dic.value ne 'com.unitek'}">
			<div id="${dic.value}" class="hideCls tab-pane">
				<table id="${dic.value}Tb" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							<th style="width: 8%">platfrom</th>
							<th style="width: 8%">settlement_id</th>
							<th style="width: 10%">start&nbsp;&nbsp;&nbsp;&nbsp;</th>
							<th style="width: 10%">end&nbsp;&nbsp;&nbsp;&nbsp;</th>
							<th style="width: 8%">order product<br/>charges</th>
							<th style="width: 8%">amazon fee</th>
							<th style="width: 8%">order<br/>other fee</th>
							<th style="width: 8%">refund total</th>
							<th style="width: 8%">storage fee</th>
							<th style="width: 8%">other total</th>
							<th style="width: 8%">income</th>
							<th style="width: 4%">currency</th>
							<th style="width: 15%">operator</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${countrySettlementMap[dic.value]}" var="list">
						<tr>
							<td>${fns:getDictLabel(countryMap[list[0]],'platform','')}</td>
							<td>${list[0]}</td>
							<td>${list[2]}</td>
							<td>${list[3]}</td>
							<td>${list[4]}</td>
							<td>${list[5]}</td>
							<td>${list[6]}</td>
							<td>${list[7]}</td>
							<td>${list[8]}</td>
							<td>${list[9]}</td>
							<td>${list[4] + list[5] + list[6] + list[7] + list[8] + list[9]}</td>
							<td>${currencySymbol[countryMap[list[0]]] }</td>
							<td>
								<div class="btn-group">
									<button type="button" class="btn btn-primary" style="background:#08c">Export</button>
									<button type="button" class="btn btn-primary dropdown-toggle" style="background:#08c"  data-toggle="dropdown">
										<span class="caret"></span>
										<span class="sr-only"></span>
									</button>
									<ul class="dropdown-menu" id="allExport">
										<li><a href="${ctx}/amazoninfo/orderCost/expOrderCost?settlementId=${list[0]}">Export statements</a></li>
										<li><a href="${ctx}/amazoninfo/orderCost/expOrderCostDetail?settlementId=${list[0]}">Export orders billing details</a></li>
									</ul>
								</div>
								</td>
							</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr class="count">
							<td style="text-align: left;vertical-align: middle;">合计</td>
							<td></td>
							<td></td>
							<td></td>
							<td tid="${dic.value}Tb" class="totalf" style="text-align: left;vertical-align: middle;">0</td>
							<td tid="${dic.value}Tb" class="totalf" style="text-align: left;vertical-align: middle;">0</td>
							<td tid="${dic.value}Tb" class="totalf" style="text-align: left;vertical-align: middle;">0</td>
							<td tid="${dic.value}Tb" class="totalf" style="text-align: left;vertical-align: middle;">0</td>
							<td tid="${dic.value}Tb" class="totalf" style="text-align: left;vertical-align: middle;">0</td>
							<td tid="${dic.value}Tb" class="totalf" style="text-align: left;vertical-align: middle;">0</td>
							<td tid="${dic.value}Tb" class="totalf" style="text-align: left;vertical-align: middle;">0</td>
							<td></td>
							<td></td>
						</tr>
					</tfoot>
				</table>
				</div></c:if></c:forEach>
	</div>
</body>
</html>
