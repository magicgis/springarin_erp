<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品价格管理管理</title>
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
			
			$("#cost,#sale,#amz,#fba").focusout(function(){
				var cost = $("#cost").val();
				var sale = $("#sale").val();
				var amz = $("#amz").val();
				var fba = $("#fba").val();
				if(cost&&sale&&amz&&fba){
					var sale1 = sale/1.19;
					sale1 = (sale1-fba-sale*amz/100-cost);
					//sale = (sale-fba-sale*amz/100-cost)
					$("#run").text("德国利润："+sale1.toFixed(2)+"$");					
				}
			});
			
			
			$(".countryHref").click(function(e){
				e.preventDefault();
				$("input[name='country']").val($(this).attr("key"));
				$(this).tab('show');
			});
		    
		    
		//	<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
		//	<c:if test="${dic.value ne 'com.unitek'}">
				$("#${dic.value}Tb").dataTable({
				  	"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
					"sPaginationType" : "bootstrap",
					"iDisplayLength" : 15,
					"aLengthMenu" : [ [ 15, 20, 60, 100, -1 ],
							[ 15, 20, 60, 100, "All" ] ],
					"bScrollCollapse" : true,
					"oLanguage" : {
						"sLengthMenu" : "_MENU_ 条/页"
					},
					"ordering" : true
				});
		//	</c:if></c:forEach>
		
			
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
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${'de' eq dic.value?'active':''}"><a id="${dic.value}A"  class="countryHref" href="#${dic.value}"  key="${dic.value}" >${dic.label}<i></i></a></li>
			</c:if>
		</c:forEach>
		<li><a  class="countryHref" href="#runDiv" ><i>利润试算</i></a></li>
	</ul>
	
	<div class="tab-content">
	<c:forEach items="${fns:getDictList('platform')}" var="dic">
		<c:set var="symbol" value="EUR"/>
		<c:if test="${dic.value ne 'com.unitek'}">
		<c:if test="${dic.value eq 'com'}"><c:set var="symbol" value="USD"/></c:if>
		<c:if test="${dic.value eq 'ca'}"><c:set var="symbol" value="CAD"/></c:if>
		<c:if test="${dic.value eq 'uk'}"><c:set var="symbol" value="GBP"/></c:if>
		<c:if test="${dic.value eq 'jp'}"><c:set var="symbol" value="JPY"/></c:if>
			<div id="${dic.value}" class="tab-pane${'de' eq dic.value?' active':' hideCls'}">
				<table id="${dic.value}Tb" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							<th style="width: 15%">产品名</th>
							<th style="width: 6%">成本价($)</th>
							<th style="width: 6%">亚马逊<br/>处理费($)</th>
							<th style="width: 6%">亚马逊<br/>佣金(%)</th>
							<th style="width: 6%">关税<br/>(含消费税)(%)</th>
							<th style="width: 6%">亚马逊<br/>保本价(${symbol})</th>
							<c:if test="${'ca' ne dic.value}">
								<th style="width: 6%">亚马逊<br/>空运保本价(${symbol})</th>
								<th style="width: 6%">亚马逊<br/>海运保本价(${symbol})</th>
							</c:if>
							<c:if test="${fn:contains('de,com',dic.value)}">
								<th style="width: 6%">本地贴<br/>保本价(${symbol})</th>
							</c:if>
							<c:if test="${fn:contains('de,fr,it,es,uk',dic.value)}">
							<th style="width: 20%" >欧洲<br/>共享保本价(${symbol})</th>
							</c:if>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="${products[dic.value]}" var="product">
							<tr>
								<td><b style="font-size: 14px"><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${product.productName}" target="_blank">${product.productName}</a></b></td>
								<td>${product.cost}</td>
								<td>${product.fba}</td>
								<td>${product.commissionPcent}</td>
								<td>${product.tariffPcent}</td>
								<td>
									<fmt:formatNumber maxFractionDigits="2" value="${product.amzPrice}" pattern="#.##" />
								</td>
								<c:if test="${'ca' ne dic.value}">
								<td>
									<fmt:formatNumber maxFractionDigits="2" value="${product.amzPriceBySky}" pattern="#.##" />
								</td>
								<td>
									<fmt:formatNumber maxFractionDigits="2" value="${product.amzPriceBySea}" pattern="#.##" />
								</td>
								</c:if>
								<c:if test="${fn:contains('de,com',dic.value)}">
								<td>
									<fmt:formatNumber maxFractionDigits="2" value="${product.localPrice}" pattern="#.##" />
								</td>
								</c:if>
								<c:if test="${fn:contains('de,fr,it,es,uk',dic.value)}">
								<td>${product.crossPrice}</td>
								</c:if>
							</tr>
						</c:forEach>
					</tbody>
				</table>
				</div>
			</c:if></c:forEach>
			<div id="runDiv" class="tab-pane hideCls">
				<form action="">
					<table style="margin-left: 100px">
						<tr style="height: 40px"><td style="width: 100px;text-align: right;font-weight: bold;">销售价:</td><td><input id="sale" class="price" /> $</td></tr>
				    	<tr style="height: 40px"><td style="width: 100px;text-align: right;font-weight: bold;">成本价:</td><td><input id="cost" class="price" /> $</td></tr>
						<tr style="height: 40px"><td style="width: 100px;text-align: right;font-weight: bold;">亚马逊佣金:</td><td><input id="amz" class="number" value="15" /> %</td></tr>
						<tr style="height: 40px"><td style="width: 100px;text-align: right;font-weight: bold;">FBA产品处理费:</td><td><input id="fba" class="price" value="2.33" /> $</td></tr>
						<tr style="height: 40px"><td style="width: 100px;text-align: right;"></td><td id="run">利润:</td></tr>
					</table>
				</form>
			</div>
	</div>
</body>
</html>