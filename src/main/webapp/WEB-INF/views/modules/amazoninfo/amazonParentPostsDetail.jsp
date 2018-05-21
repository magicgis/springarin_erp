<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>父帖详情</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
<script type="text/javascript" src="${ctxStatic}/raty-master/lib/jquery.raty.js" ></script>
<style>
.rating-star {
width: 0;
margin: 0;
padding: 0;
border: 0;

}
/*进度条样式*/ 
.progressbar_1{ 
    background-color:#eee; 
    height:16px; 
    width:180px; 
    border:1px solid #bbb; 
    color:#222; 
} 
.progressbar_1 .bar { 
    background-color:#FFB90F; 
    height:16px; 
    width:0; 
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

	$(document).ready(function() {
		$("a[rel='popover']").popover({
			trigger : 'hover'
		});
		
		 $('#star').raty({ readOnly: true, score:${empty portsDetail.star?0:portsDetail.star},path:'${ctxStatic}/raty-master/lib/images' });
			
		 
	});
</script>

</head>
<body>
	<br/> 
	<div class="container" id="page">
		<div id="content">
			<div class="container">
			<form:form id="searchForm" action="${ctx}/amazoninfo/amazonPortsDetail/queryParentDetail" method="post" >
			    <input type="hidden" name="productName" id="productName" value="${amazonPortsDetail.productName }"/>
			    <input type="hidden" name="asin"  value="${portsDetail.asin }"  />
			    <input type="hidden" name="country" value="${portsDetail.country}"  />
				<table class="table table-striped table-bordered table-condensed">
					<tbody>
					    <tr><td colspan="5" style="font-size:16px;text-align:center"><b>${portsDetail.productName }</b>
					    <a class="btn btn-warning btn-small" target="_blank" onClick="return confirm('建议在帖子编辑功能中修改帖子保证导出每项内容是实时的,帖子父帖因亚马逊原因可能获取不到最新的,请自行核查,确定导出?');" href="${ctx}/amazoninfo/amazonPortsDetail/exportParentModelExcel?country=${portsDetail.country }&asin=${portsDetail.asin}">帖子Excel</a>
					    </td>
					      </tr>
						<tr>
						    <td rowspan="4" style="width:30%">
						    <div style="float: left;">
						     <div id="star"></div>
						      <b>${portsDetail.star}</b> out of 5 stars
						     </div>  
						     &nbsp;
						    <c:set var="total" value="${portsDetail.star5+portsDetail.star4+portsDetail.star3+portsDetail.star2+portsDetail.star1 }"/>

							<div style="margin-bottom:5px;">
							 <c:set var="rate5" value="${portsDetail.star5/total*100 eq 'NaN'?0:portsDetail.star5/total*100}"/>
							<a target="_blank" href="${portsDetail.star5Link}" title="${fns:roundUp(rate5)}%">
								<div class="progressbar_1" style="float: left;"> 
								  
								   <div class="bar" style="width:${portsDetail.star5/total*100}%;"></div> 
								</div> 
								<span style="padding-right:8px;float: right;">${not empty portsDetail.star5&& portsDetail.star5!=0?"<b>":""}5 star(${empty portsDetail.star5?0:portsDetail.star5})${not empty portsDetail.star5&&portsDetail.star5!=0?"</b>":""}</span>&nbsp;
							</a>
							</div>&nbsp;
							
						    <div style="margin-top:5px;margin-bottom:5px;"> 
						     <c:set var="rate4" value="${portsDetail.star4/total*100 eq 'NaN'?0:portsDetail.star4/total*100}"/>
						     <a target="_blank" href="${portsDetail.star4Link}" title="${fns:roundUp(rate4)}%">
							    <div class="progressbar_1" style="float: left;"> 
							   
							      <div class="bar" style="width: ${portsDetail.star4/total*100}%;"></div>  
							    </div> 
						       <span style="padding-right:8px;float: right;">${not empty portsDetail.star4&& portsDetail.star4!=0?"<b>":""}4 star(${empty portsDetail.star4?0:portsDetail.star4})${not empty portsDetail.star4&& portsDetail.star4!=0?"</b>":""}</span>&nbsp;
						    </a>
						    </div>
						    
						      <div  style="margin-top:5px;margin-bottom:5px;">
						       <c:set var="rate3" value="${portsDetail.star3/total*100 eq 'NaN'?0:portsDetail.star3/total*100}"/>
						      <a target="_blank" href="${portsDetail.star3Link}" title="${fns:roundUp(rate3)}%">
							    <div class="progressbar_1" style="float: left;"> 
							    
						            <div class="bar" style="width: ${portsDetail.star3/total*100}%;"></div>  
						         </div> 
						       <span style="padding-right:8px;float: right;">${not empty portsDetail.star3&& portsDetail.star3!=0?"<b>":""}3 star(${empty portsDetail.star3?0:portsDetail.star3})${not empty portsDetail.star3&& portsDetail.star3!=0?"</b>":""}</span>&nbsp;
						    </a>
						    </div>
						    
						  <div   style="margin-top:5px;margin-bottom:5px;">
						  <c:set var="rate2" value="${portsDetail.star2/total*100 eq 'NaN'?0:portsDetail.star2/total*100}"/>
						  <a target="_blank" href="${portsDetail.star2Link}" title="${fns:roundUp(rate2)}%"> 
						    <div class="progressbar_1" style="float: left;"> 
						    
						       <div class="bar" style="width: ${portsDetail.star2/total*100}%;"></div> 
						    </div> 
						     <span style="padding-right:8px;float: right;"> ${not empty portsDetail.star2&& portsDetail.star2!=0?"<b>":""}2 star(${empty portsDetail.star2?0:portsDetail.star2})${not empty portsDetail.star2&& portsDetail.star2!=0?"</b>":""}</span>&nbsp;
						  </a>
						   </div>
						    
						    <div  style="margin-top:5px;margin-bottom:5px;">
						      <c:set var="rate1" value="${portsDetail.star1/total*100 eq 'NaN'?0:portsDetail.star1/total*100}"/>
							 <a target="_blank" href="${portsDetail.star1Link}" title="${fns:roundUp(rate1)}%">
							    <div class="progressbar_1" style="float: left;"> 
							     
							       <div class="bar" style="width: ${portsDetail.star1/total*100}%;"></div> 
							    </div> 
							    <span style="padding-right: 8px;float: right;"> ${not empty portsDetail.star1&& portsDetail.star1!=0?"<b>":""}1 star(${empty portsDetail.star1?0:portsDetail.star1})${not empty portsDetail.star1&& portsDetail.star1!=0?"</b>":""}</span>&nbsp;
						      </a>
						     </div>
						    </td>
						    <td style=" height:30px;vertical-align:middle; "><b>Country</b></td>
							<td style="vertical-align:middle; ">${fns:getDictLabel(portsDetail.country,'platform','')}</td>
						</tr>
						<tr>
						  <td style=" height:30px;vertical-align:middle; "><b>Asin</b></td>
						  <td style="vertical-align:middle; ">${portsDetail.asin }</td>
						</tr>
						<tr>
						  <td style=" height:30px;vertical-align:middle; "><b>Sku</b></td>
						  <td style="vertical-align:middle; "><c:if test="${not empty portsDetail.sku}">
							    <c:forEach items="${fn:split(portsDetail.sku,',')}" var="sku" varStatus="i">
							      <c:if test="${i.count eq 1}"><a href="#" title="${portsDetail.sku}">${sku}</a></c:if>
							    </c:forEach>
							  </c:if>
				          </td>
						</tr>
						<tr>
						  <td style=" height:30px;vertical-align:middle; "><b>Date</b></td>
						  <td style="vertical-align:middle; ">
							  <input style="width: 90px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});"  readonly="readonly"  class="Wdate" type="text" name="queryTime" value="<fmt:formatDate value="${portsDetail.queryTime}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
						   </td>
						</tr>
						<tr>
						 
						</tr>
						
					</tbody>
				</table>
				</form:form>
				
				<table class="table table-striped table-bordered table-condensed">
					<tbody>
						<tr class="info">
							<td colspan="4"><b>帖子信息</b></td>
						</tr>
						<tr>
							<td><b>Binding</b></td>
							<td>${portsDetail.binding}</td>
							<td><b>Brand</b></td>
							<td>${portsDetail.brand}</td>
						</tr>
						<tr>
							<td><b>Label</b></td>
							<td>${portsDetail.label}</td>
							<td><b>Manufacturer</b></td>
							<td>${portsDetail.manufacturer}</td>
						</tr>
					
						<tr>
							<td><b>PackageQuantity</b></td>
							<td>${portsDetail.packageQuantity}</td>
							<td><b>Publisher</b></td>
							<td>${portsDetail.publisher}</td>
						</tr>
						<tr>
							<td><b>ProductGroup</b></td>
							<td>${portsDetail.productGroup}</td>
							<td><b>ProductTypeName</b></td>
							<td>${portsDetail.productTypeName}</td>
						</tr>
						<tr>
							<td><b>Studio</b></td>
							<td>${portsDetail.studio}</td>
							<td><b>PartNumber</b></td>
							<td>${portsDetail.partNumber}</td>
						</tr>
						<tr>
							<td><b>Title</b></td>
							<td colspan="3">${portsDetail.title}</td>
						</tr>
					
					</tbody>
				</table>
				
			<table class="table table-striped table-bordered table-condensed">
				<tbody>
					<tr class="warning">
						<td colspan=10 ><b>子帖信息</b></td>
					</tr>
					<tr>
					   <td width="150px"><b>Product Name</b></td>
					    <td width="100px"><b>Asin</b></td>
						<td width="100px"><b>按大小组合</b></td>
						<td width="100px"><b>按颜色组合</b></td>
						<td width="100px"><b>Size</b></td>
						<td width="100px"><b>Color</b></td>
					</tr>
					<c:forEach var="child" items="${portsDetail.children}" varStatus="i">
						<tr>
						    <td><a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${child.productName }" target="_blank">${child.productName }</a></td>
						    <td><a href="${ctx}/amazoninfo/amazonPortsDetail/view?id=${child.id}" target="_blank">${child.asin }</a></td>
							<td>${child.bySize eq '1'?'是':'否'}</td>
					        <td>${child.byColor eq '1'?'是':'否'}</td>
					        <td>${child.size}</td>
					        <td>${child.color}</td>
						</tr>
					</c:forEach>
				</tbody>
				</table>
				
				
				

			</div>
		</div>
		<!-- content -->
		<div class="clear"></div>
	</div>

</body>
</html>
