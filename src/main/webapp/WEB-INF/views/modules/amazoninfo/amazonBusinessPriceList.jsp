<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>B2B产品价格</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr {
			/*float: right;*/
			min-height: 40px
		}
		
		.spanexl {
			float: left;
		}
	</style>
	<script type="text/javascript">
		
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		
		$.fn.dataTableExt.afnSortData['dom-html1'] = function ( oSettings, iColumn )
		{
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				var text = $('td:eq('+iColumn+')', tr).text();
				if(!parseFloat(text) > 0){
					return 0;
				}
				return parseFloat(text);
			});
		};
	
		$(document).ready(function(){
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			$("#tb").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 15,
				"aLengthMenu" : [ [ 15, 20, 60, 100, -1 ],
						[ 15, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"aoColumns": [
						         null, 
						         { "sSortDataType":"dom-html1", "sType":"numeric" },
								 { "sSortDataType":"dom-html1", "sType":"numeric" },
								 { "sSortDataType":"dom-html1", "sType":"numeric" },
								 { "sSortDataType":"dom-html1", "sType":"numeric" },
								 { "sSortDataType":"dom-html1", "sType":"numeric" },
								 { "sSortDataType":"dom-html1", "sType":"numeric" },
								 { "sSortDataType":"dom-html1", "sType":"numeric" },
								 { "sSortDataType":"dom-html1", "sType":"numeric" }, 
						         null
							     ],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true
			});
		});
	</script>
</head>
	<body>
		 <ul class="nav nav-tabs">
			<li class='active'><a href="${ctx}/amazoninfo/amazonProduct/businessPriceList">B2B价格</a></li>
		</ul>
		<table id="tb" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th style="width: 20%" rowspan="2"><spring:message code="psi_product_name"/></th>
					<th style="width: 20%" colspan="2">${fns:getDictLabel('de','platform','')}(EUR)</th>
					<th style="width: 20%" colspan="2">${fns:getDictLabel('com','platform','')}(USD)</th>
					<th style="width: 20%" colspan="2">${fns:getDictLabel('uk','platform','')}(GBP)</th>
					<th style="width: 20%" colspan="2">${fns:getDictLabel('jp','platform','')}(JPY)</th>
					<th style="display:none"></th>
				</tr>
				<tr>
					<th>在售价</th>
					<th>B2B价</th>
					<th>在售价</th>
					<th>B2B价</th>
					<th>在售价</th>
					<th>B2B价</th>
					<th>在售价</th>
					<th>B2B价</th>
					<th style="display:none"></th>
				</tr>
			<tbody>
				<c:forEach items="${nameMap}" var="data">
					<tr>
						<c:set var="flag" value="0"></c:set>
						<c:set var="skus" value=""></c:set>
						<td><b style="font-size: 14px"><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${data.key}" target="_blank">${data.key}</a></b></td>
						<!-- de -->
						<c:set var="price" value="${nameMap[data.key]['de']}"></c:set>
						<td><a href="${price.link}" target="_blank">${(empty price.salePrice && not empty price.businessPrice)?'<b>不可售</b>':price.salePrice}</a></td>
						<td>
							<c:if test="${not empty price.businessPrice }">
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" 
									data-content="
										B2B阶梯价：<br/>
										<c:if test="${not empty price.quantity1}">
											购买数量：${price.quantity1 }，售价：${price.price1 }<br/>
										</c:if>
										<c:if test="${not empty price.quantity2}">
											购买数量：${price.quantity2 }，售价：${price.price2 }<br/>
										</c:if>
										<c:if test="${not empty price.quantity3}">
											购买数量：${price.quantity3 }，售价：${price.price3 }<br/>
										</c:if>
										<c:if test="${not empty price.quantity4}">
											购买数量：${price.quantity4 }，售价：${price.price4 }<br/>
										</c:if>
										<c:if test="${not empty price.quantity5}">
											购买数量：${price.quantity5 }，售价：${price.price5 }</c:if>">
									<c:choose>
										<c:when test="${price.businessPrice>=price.salePrice || price.price1>=price.businessPrice}">
											<c:set var="flag" value="1"></c:set>
											<font style="color:red">${price.businessPrice}</font>
										</c:when>
										<c:otherwise>
											${price.businessPrice}
										</c:otherwise>
									</c:choose>
									<c:set var="skus" value="${skus },${price.sku }"></c:set>
								</a>
							</c:if>
						</td>
						<!-- com -->
						<c:set var="price" value="${nameMap[data.key]['com']}"></c:set>
						<td><a href="${price.link}" target="_blank">${(empty price.salePrice && not empty price.businessPrice)?'<b>不可售</b>':price.salePrice}</a></td>
						<td>
							<c:if test="${not empty price.businessPrice }">
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" 
									data-content="
										B2B阶梯价：<br/>
										<c:if test="${not empty price.quantity1}">
											购买数量：${price.quantity1 }，售价：${price.price1 }<br/>
										</c:if>
										<c:if test="${not empty price.quantity2}">
											购买数量：${price.quantity2 }，售价：${price.price2 }<br/>
										</c:if>
										<c:if test="${not empty price.quantity3}">
											购买数量：${price.quantity3 }，售价：${price.price3 }<br/>
										</c:if>
										<c:if test="${not empty price.quantity4}">
											购买数量：${price.quantity4 }，售价：${price.price4 }<br/>
										</c:if>
										<c:if test="${not empty price.quantity5}">
											购买数量：${price.quantity5 }，售价：${price.price5 }</c:if>">
									<c:choose>
										<c:when test="${price.businessPrice>=price.salePrice || price.price1>=price.businessPrice}">
											<c:set var="flag" value="1"></c:set>
											<font style="color:red">${price.businessPrice}</font>
										</c:when>
										<c:otherwise>
											${price.businessPrice}
										</c:otherwise>
									</c:choose>
									<c:set var="skus" value="${skus },${price.sku }"></c:set>
								</a>
							</c:if>
						</td>
						<!-- uk-->
						<c:set var="price" value="${nameMap[data.key]['uk']}"></c:set>
						<td><a href="${price.link}" target="_blank">${(empty price.salePrice && not empty price.businessPrice)?'<b>不可售</b>':price.salePrice}</a></td>
						<td>
							<c:if test="${not empty price.businessPrice }">
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover"
									data-content="
										B2B阶梯价：<br/>
										<c:if test="${not empty price.quantity1}">
											购买数量：${price.quantity1 }，售价：${price.price1 }<br/>
										</c:if>
										<c:if test="${not empty price.quantity2}">
											购买数量：${price.quantity2 }，售价：${price.price2 }<br/>
										</c:if>
										<c:if test="${not empty price.quantity3}">
											购买数量：${price.quantity3 }，售价：${price.price3 }<br/>
										</c:if>
										<c:if test="${not empty price.quantity4}">
											购买数量：${price.quantity4 }，售价：${price.price4 }<br/>
										</c:if>
										<c:if test="${not empty price.quantity5}">
											购买数量：${price.quantity5 }，售价：${price.price5 }</c:if>">
									<c:choose>
										<c:when test="${price.businessPrice>=price.salePrice || price.price1>=price.businessPrice}">
											<c:set var="flag" value="1"></c:set>
											<font style="color:red">${price.businessPrice}</font>
										</c:when>
										<c:otherwise>
											${price.businessPrice}
										</c:otherwise>
									</c:choose>
									<c:set var="skus" value="${skus },${price.sku }"></c:set>
								</a>
							</c:if>
						</td>
						<!-- jp-->
						<c:set var="price" value="${nameMap[data.key]['jp']}"></c:set>
						<td><a href="${price.link}" target="_blank">${(empty price.salePrice && not empty price.businessPrice)?'<b>不可售</b>':price.salePrice}</a></td>
						<td>
							<c:if test="${not empty price.businessPrice }">
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-placement="left"
									data-content="
										B2B阶梯价：<br/>
										<c:if test="${not empty price.quantity1}">
											购买数量：${price.quantity1 }，售价：${price.price1 }<br/>
										</c:if>
										<c:if test="${not empty price.quantity2}">
											购买数量：${price.quantity2 }，售价：${price.price2 }<br/>
										</c:if>
										<c:if test="${not empty price.quantity3}">
											购买数量：${price.quantity3 }，售价：${price.price3 }<br/>
										</c:if>
										<c:if test="${not empty price.quantity4}">
											购买数量：${price.quantity4 }，售价：${price.price4 }<br/>
										</c:if>
										<c:if test="${not empty price.quantity5}">
											购买数量：${price.quantity5 }，售价：${price.price5 }</c:if>">
									<c:choose>
										<c:when test="${price.businessPrice>=price.salePrice || price.price1>=price.businessPrice}">
											<c:set var="flag" value="1"></c:set>
											<font style="color:red">${price.businessPrice}</font>
										</c:when>
										<c:otherwise>
											${price.businessPrice}
										</c:otherwise>
									</c:choose>
									<c:set var="skus" value="${skus },${price.sku }"></c:set>
								</a>
							</c:if>
						</td>
						<td style="display:none">
							<c:if test="${'1' eq flag }">红色 red</c:if>
							${skus }
						</td>
					</tr>		
				</c:forEach>
			</tbody>
		</table>
	</body>
</html>