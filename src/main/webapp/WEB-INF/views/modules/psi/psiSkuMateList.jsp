<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>Product Sku Manager</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	
	$(document).ready(function() {
		if(!(top)){
			top = self;			
		}	
		
		$(".countryHref").click(function(){
			$("input[name='platform']").val($(this).attr("key"));
			$("input[name='brand']").val('');
			$("#searchForm").submit();
		});
		
		$(".btnSkuMate").click(function(){
			var sku = $(this).parent().find(".skuMate").text();
			showChildrenDiv(sku);
		});
		
		function showChildrenDiv(sku){
			var submitChild = function (v, h, f){
				var productCon = h.find("select#productSelect").val();
				if(productCon==''){
					top.$.jBox.tip("产品不能为空","info",{timeout:3000});
					return false;
				}
				var params = {};
				params.sku=encodeURIComponent(encodeURIComponent(h.find("#choiceSku").text()));
				params.country = "${psiProduct.platform}";
				params.account = "${psiProduct.brand}";
				params.productCon=h.find("select#productSelect").val();
				if($("li.active a").attr("key")!='ebay'&&$("li.active a").attr("key")!='ebay_com'&&h.find(".isFnsku").attr("checked")=="checked"){
					params.isCheck=true;
				}else{
					params.isCheck=false;
				}
				
				window.location.href="${ctx}/psi/product/skuMateSave?"+$.param(params);
				loading('正在保存...');
				return true;
			};
			var enCodeSku=encodeURIComponent(encodeURIComponent(sku));
			var url="get:${ctx}/psi/product/goSkuMate?sku="+enCodeSku+"&country=${psiProduct.platform}";
			$.jBox(url, { title: "sku匹配产品",width:500,submit: submitChild,persistent: true});
		}
	});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<%-- <c:if test="${dic.value ne 'com.unitek'}">
				    <c:choose>
				       <c:when test="${fn:length(accountMap[dic.value])>1}">
				           <li class="dropdown ${psiProduct.platform eq dic.value ?'active':''}"  >
							    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">${dic.label}<b class="caret"></b> </a>
							    <ul class="dropdown-menu" style="min-width:110px">
							         <c:forEach items="${accountMap[dic.value]}" var="account">
							             <li><a href="${ctx}/psi/product/skuMate?platform=${dic.value}&brand=${account}">${account}</a></li>	
							         </c:forEach>
							    </ul>
			               </li>
				       </c:when>
				       <c:otherwise>
						    <li class="${psiProduct.platform eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
				       </c:otherwise>
				    </c:choose>
			</c:if> --%>
			 <c:if test="${dic.value ne 'com.unitek'}">
			    <c:choose>
			       <c:when test="${fn:length(accountMap[dic.value])>1}">
			           <li class="dropdown ${psiProduct.platform eq dic.value ?'active':''}"  >
						    <a class="dropdown-toggle"  data-toggle="dropdown" href="#">${dic.label}<c:if test="${dic.value eq psiProduct.platform}">${psiProduct.brand}</c:if><b class="caret"></b> </a>
						    <ul class="dropdown-menu" style="min-width:110px">
						         <c:forEach items="${accountMap[dic.value]}" var="account">
						              <li><a href="${ctx}/psi/product/skuMate?platform=${dic.value}&brand=${account}">${account}</a></li>	
						         </c:forEach>
						    </ul>
		               </li>
			       </c:when>
			        <c:when test="${fn:length(accountMap[dic.value])==1}">
			           <c:forEach items="${accountMap[dic.value]}" var="account">
						    <li class="${psiProduct.platform eq dic.value ?'active':''}"><a href="${ctx}/psi/product/skuMate?platform=${dic.value}&brand=${account}">${account}</a></li>	
						</c:forEach>	
			       </c:when>
			       <c:otherwise>
					    <li class="${psiProduct.platform eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			       </c:otherwise>
			    </c:choose>
		     </c:if>
		</c:forEach>	
		
		
		<li class="${psiProduct.platform eq 'ebay'?'active':''}"><a class="countryHref" href="#" key="ebay">德国Ebay</a></li>
		<li class="${psiProduct.platform eq 'ebay_com'?'active':''}"><a class="countryHref" href="#" key="ebay_com">美国Ebay</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="psiProduct" action="${ctx}/psi/product/skuMate" method="post" class="breadcrumb form-search">
		<div style="vertical-align: middle;height: 40px;line-height: 40px;text-align:center">
			<input  name="platform"  type="hidden" value="${psiProduct.platform}"/>
			<input  name="brand"  type="hidden" value="${psiProduct.brand}"/>
			<a href="${ctx}/psi/product/skulist?platform=${psiProduct.platform}&brand=${psiProduct.brand }"><input id="btnCancel" class="btn" style="text-glign:center" type="button" value="返 回"/></a>
		</div>
	</form:form>
	<tags:message content="${message}"/>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead>
			  <tr> 
			   <th width="5%" style="text-align:center;font-size: large" >序号</th>
			   <th width="25%" style="text-align:center;font-size: large" >sku</th>
			   <th width="25%" style="text-align:center;font-size: large" >sku</th>
			   <th width="25%" style="text-align:center;font-size: large" >sku</th>
			   <th width="25%" style="text-align:center;font-size: large" >sku</th>
			  </tr>
		</thead>
		<tbody >
			<c:forEach items="${skus}" var="sku" varStatus="i">
			
				<c:if test="${(i.index+1)%4==1}">
					<tr>
					<td style="text-align:center"><fmt:formatNumber value="${(i.index+4)/4}" minIntegerDigits="0"></fmt:formatNumber> </td>
					<td style="font-weight:bold;font-size: large;text-align:center"><span class="skuMate">${sku}</span><a style="margin-right:2px;float:right" href="#" class="grpOpen btnSkuMate" ><span class="icon-plus"></span></a></td>
				</c:if>
				
				<c:if test="${(i.index+1)%4==2}">
					<td style="font-weight:bold;font-size: large;text-align:center"><span class="skuMate">${sku}</span><a style="margin-right:2px;float:right" href="#" class="grpOpen btnSkuMate" ><span class="icon-plus"></span></a></td>
				</c:if>
				
				<c:if test="${(i.index+1)%4==3}">
					<td style="font-weight:bold;font-size: large;text-align:center"><span class="skuMate">${sku}</span><a style="margin-right:2px;float:right" href="#" class="grpOpen btnSkuMate" ><span class="icon-plus"></span></a></td>
				</c:if>
				
				<c:if test="${(i.index+1)%4==0||(i.index+1)==fn:length(skus)}">       
					<c:choose>
						<c:when test="${(i.index+1)%4==1||(i.index+1)%4==2||(i.index+1)%4==3}">   
						<c:if test="${(i.index+1)%4==1}"><td></td><td></td><td></td></c:if>
						<c:if test="${(i.index+1)%4==2}"><td></td><td></td></c:if>
						<c:if test="${(i.index+1)%4==3}"><td></td></c:if>
						</c:when>
						<c:otherwise>
							<td style="font-weight:bold;font-size: large;text-align:center"><span class="skuMate">${sku}</span><a style="margin-right:2px;float:right" href="#" class="grpOpen btnSkuMate" ><span class="icon-plus"></span></a></td>
						</c:otherwise>
					</c:choose>
					</tr>
				</c:if>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>
