<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品保本价格比对</title>
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
		
		$.fn.dataTableExt.afnSortData['dom-html1'] = function ( oSettings, iColumn )
		{
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				return $('td:eq('+iColumn+')', tr).text();
			} );
		};
		
		
		$(document).ready(function(){
			
			$(".tip").popover({html:true,trigger:'hover',content:function(){
				var td=$(this).parent();
				params={};
				params.country=td.find(".country").val();
				params.productName=td.find(".productName").val();
				var $this = $(this);
				if(!$this.attr("content")){
					if(!$this.attr("data-content")){
						var content="";
						$.ajax({
						    type: 'get',
						    async:false,
						    url: '${ctx}/amazoninfo/productPrice/priceDetail' ,
						    data: $.param(params),
						    success:function(data){ 
						    	content = data;
						    	$this.attr("content",data);
					        }
						});
						return content;
					}
				}
				return $this.attr("content");
			}});
		    
			$("#tb").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 15,
				"aLengthMenu" : [ [ 15, 20, 60, 100, -1 ],
						[ 15, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"aoColumns": [
						         null
									//<c:forEach items="${countryList}" var="country">
							        , { "sSortDataType":"dom-html1", "sType":"numeric" },
								     { "sSortDataType":"dom-html1", "sType":"numeric" },
								     { "sSortDataType":"dom-html1", "sType":"numeric" }
									//</c:forEach>
							     ],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true
			});
			
			$("#tbDiv .row:eq(0)").append($("#searchDivs").html());
			
		});
	</script>
</head>
	<body>
	 <ul class="nav nav-tabs">
		<li class='active'><a href="${ctx}/amazoninfo/productPrice/salePrice">Amazon保本价</a></li>
	    <shiro:hasPermission name="amazoninfo:ebayProductSalePrice:view">
	       <li><a href="${ctx}/amazoninfo/productPrice/ebaySalePrice">Ebay保本价</a></li>
	    </shiro:hasPermission>
       
	</ul>
	<div class="alert">
	  <button type="button" class="close" data-dismiss="alert">&times;</button>
	  <strong>Warning!</strong> 
	  公式：A（1+关税+消费税）+运费+FBA处理费+利润+B*亚马逊佣金 = B/(1+VAT)<br/>
	 A:采购价 B:销售价   保本价为当利润为0时，倒推出的B值<br/>
	 利润率=[(B-保本价)/(1+VAT)-(B-保本价)*亚马逊佣金比例]/B
	  
	</div>
		<div id="searchDivs" style="display: none">
			<form  action="${ctx}/amazoninfo/productPrice/salePrice" method="post" >
				利润率:<input style="margin-left: 5px;width: 80px" type="number" name="number" value="${number}" />%
				<input type="submit" class="btn btn-primary" value="<spring:message code="sys_but_search"/>" />
			</form>
		</div>
		<div id= "tbDiv"  style="overflow-x:auto;">
		<table id="tb" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th style="width: 15%" rowspan="2"><spring:message code="psi_product_name"/></th>
						<c:forEach items="${countryList}" var="country">
							<th colspan="3">${fns:getDictLabel(country,'platform','')}</th>
						</c:forEach>
				</tr>
				<tr>
					<c:forEach items="${countryList}" var="country">
						<th><spring:message code="amazon_product_break-even_price"/></th>
						<th><spring:message code="psi_product_onSale"/></th>
						<th>
						   <c:choose>
							<c:when test="${empty number}"><spring:message code="amazon_product_profit_rate"/>(%)</c:when>
							<c:otherwise><spring:message code="amazon_product_standard_price"/></c:otherwise>
						   </c:choose>
						</th>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<c:set var="viewAll" value="false"/>
				<shiro:hasPermission  name="amazoninfo:productSalePrice:all">
					<c:set var="viewAll" value="true"/>
				</shiro:hasPermission>
				<c:forEach items="${products}" var="product">
					<c:set var="line" value="${typeLineMap[fn:toLowerCase(nameTypeMap[product[1]])] }"/>
					<c:if test="${viewAll || not empty countrys || (not empty line &&( fn:contains(lines, line)))}">
					<tr>
						<c:set var="index" value="1"/>
						<td>
							<b style="font-size: 14px">
							<a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${product[1]}" target="_blank">${fn:replace(product[1],'Inateck','')}</a></b>
							(${line }线)
						</td>
						<c:forEach items="${countryList}" var="country">
							<c:set var="countryLine" value="${line}_${country }"/>
							<c:set var="index" value="${index+1 }"/>
							<c:if test="${'mx' eq country }">
								<c:set var="index" value="${index+1 }"/>
							</c:if>
							<c:set value="${product[1]}_${country }" var="key" />
							<c:choose>
							<%-- 所有查看权或者平台负责人或者该平台产品线权限 --%>
							<c:when test="${viewAll || fn:contains(countrys, country) || fn:contains(lines, line)}">
							<!-- 保本价 -->
							<td>
								<input type="hidden" class="country" value="${country }"/>
								<input type="hidden" class="productName" value="${product[1]}"/>
								<a href="#" class="tip"  content="" >${product[index]}</a>
							</td>
							<!-- 在售 -->
							<td>
								<c:set var="lirun" value=""/>
								<c:if test="${not empty productWithCountry[key] || empty productWithCountry}">
									<c:set var="price" value="${productsCurrent[product[1]][country][0]}" />
									<c:if test="${price>product[index] }">
										<a href="${productsCurrent[product[1]][country][index]}" target="_blank">${price}</a>
										<c:choose>	<%--计算利润时考虑佣金比和增值税--%>
											<c:when test="${not empty number}">
												<c:if test="${not empty rate[key]}">
													<c:set var="lirun" value="${product[index]*(100-rate[key]*(1+vat[country]))/(100-(rate[key]+number)*(1+vat[country]))}"/>
												</c:if>
												<c:if test="${empty rate[key]}">
													<c:set var="lirun" value="${product[index]*100/(100-number*(1+vat[country]))}"/>
												</c:if>
											</c:when>
											<c:otherwise>
												<c:if test="${not empty rate[key]}">
													<c:set var="lirun" value="${((price-product[index])/(1+vat[country])*100-(price-product[index])*rate[key])/price}"/>
												</c:if>
												<c:if test="${empty rate[key]}">
													<c:set var="lirun" value="${(price-product[index])/(1+vat[country])*100/price}"/>
												</c:if>
											</c:otherwise>
										</c:choose>
									</c:if>
									<!-- 亏本的不需要考虑利润跟佣金比的关系 -->
									<c:if test="${not empty product[index] && price<=product[index] }">
										<a href="${productsCurrent[product[1]][country][index]}" style="color: red;" data-toggle="popover" data-html="true" rel="popover" data-content="${productsCurrent[product[1]][country][1]}:${price}">${price}</a>
										<%--<input type="hidden" value="德国亏本" /> --%>
										<c:choose>
											<c:when test="${not empty number}">
												<c:set var="lirun" value="${product[index]*(100+number)/100}"/>
											</c:when>
											<c:otherwise>
												<c:set var="lirun" value="${(price-product[index])*100/price}"/>
											</c:otherwise>
										</c:choose>
									</c:if>
								</c:if>
								
							</td>
							<!-- 利润率 -->
							<td style="background-color:#D2E9FF;">
								<fmt:formatNumber maxFractionDigits="2" value="${lirun}" pattern="#.00" />
							</td>
							</c:when>
							<c:otherwise>
								<td></td>
								<td></td>
								<td></td>
							</c:otherwise>
							</c:choose>
						</c:forEach>
					</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
		</div>
	</body>
</html>