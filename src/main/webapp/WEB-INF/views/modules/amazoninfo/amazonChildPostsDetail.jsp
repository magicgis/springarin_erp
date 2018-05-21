<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>子帖详情</title>
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
		 /* $("#country").change(function(){    
			  $.ajax({  
			        type : 'POST', 
			        url : '${ctx}/amazoninfo/amazonPortsDetail/getAsin',  
			        dataType:"json",
			        data : 'productName='+encodeURI($("#productName").val())+"&country="+$("#country").val(),  
			        async: false,
			        success : function(msg){ 
			        	$("#asin").empty();  
			        	var option='';
			            for(var i=0;i<msg.length;i++){
							option += "<option  value=\"" + msg[i]+ "\">" + msg[i] + "</option>"; 
	                    }
			            $("#asin").append(option);
			            $("#asin").select2("val",msg[msg.length-1]);
			           
						$("#searchForm").submit();
			        }
		  }); 
   	    }); */
   	    var products="";
   	    $('#typeahead').typeahead({
			source: function (query, process) {
				if(!(products)){
					$.ajax({
					    type: 'post',
					    async:false,
					    url: '${ctx}/psi/psiInventory/getAllProductNames' ,
					    dataType: 'json',
					    success:function(data){ 
					    	products = data;
				        }
					});
				}
				process(products);
		    },
			updater:function(item){
				window.location.href="${ctx}/amazoninfo/amazonPortsDetail/queryPortsDetail?productName="+encodeURIComponent(item)+"&country="+$("input[name='country']").val();
				return item;
			}
		});
   	    
   	    
   	    $(".countryHref").click(function(){
   	    	$("#asin").val('');
			$("input[name='country']").val($(this).attr("key"));
			$("#searchForm").submit();
		});
   	    
		 $("#asinShow").change(function(){
			 $("#asin").val($("#asinShow").val());
			 var hrefStr=$("#changeAsin").attr("href");
			 $("#changeAsin").attr("href",hrefStr.substring(0,hrefStr.lastIndexOf('/'))+"/"+$("#asinShow").val());
			 $("#searchForm").submit();
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
			
			<form:form id="searchForm" action="${ctx}/amazoninfo/amazonPortsDetail/queryPortsDetail" method="post" >
			    <input type="hidden" name="productName" id="productName" value="${amazonPortsDetail.productName }"/>
			    <input type="hidden" name="country" id="country" value="${amazonPortsDetail.country}"/>
			    <input type="hidden" name="asin"  id="asin" value="${amazonPortsDetail.asin}"/>
				<table class="table table-striped table-bordered table-condensed">
					<tbody>
					    <tr><td colspan="5" style="font-size:16px;text-align:center"><b>
					  <%--   <a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${portsDetail.productName }" target="_blank">${portsDetail.productName }</a> --%>
						    <input id="typeahead" type="text" class="span3 search-query" value="${amazonPortsDetail.productName}" style="width:200px;margin-top: 5px"  autocomplete="off"  style="margin: 0 auto;" data-provide="typeahead" data-items="8" />
						    </b>
					        &nbsp;&nbsp;<a class="btn btn-success btn-small" target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${portsDetail.productName }">产品销量信息</a>
							<shiro:hasPermission name="amazoninfo:feedSubmission:all">
								&nbsp;&nbsp;<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/amazonPortsDetail/form?flag=1&country=${portsDetail.country }&asin=${amazonPortsDetail.asin}">帖子编辑</a>
							</shiro:hasPermission>
							<shiro:lacksPermission name="amazoninfo:feedSubmission:all">
								<shiro:hasPermission name="amazoninfo:feedSubmission:${portsDetail.country }">
									&nbsp;&nbsp;<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/amazonPortsDetail/form?flag=1&country=${portsDetail.country }&asin=${amazonPortsDetail.asin}">帖子编辑</a>
								</shiro:hasPermission>
							</shiro:lacksPermission>
							&nbsp;&nbsp;<a class="btn btn-warning btn-small" target="_blank" onClick="return confirm('建议在帖子编辑功能中修改帖子保证导出每项内容是实时的,帖子父帖因亚马逊原因可能获取不到最新的,请自行核查,确定导出?');"  href="${ctx}/amazoninfo/amazonPortsDetail/exportModelExcel?productName=${portsDetail.productName }&country=${portsDetail.country }&asin=${amazonPortsDetail.asin}">帖子Excel</a>
					    </td>
					   </tr>
					    <tr>
					       <td  colspan="5">
					          <ul class="nav nav-tabs">
								<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
									<c:if test="${dic.value ne 'com.unitek'}">
										<li class="${amazonPortsDetail.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
									</c:if>
								</c:forEach>	
						   </ul>
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
							</a></div>&nbsp;
							
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
							<td style=" height:30px;vertical-align:middle;"><b>Asin</b></td>
							<td style="vertical-align:middle; ">
							  <c:if test="${fn:length(asinList)>1}">
							      <select name="asinShow" id="asinShow" style="width: 130px">
									<c:forEach items="${asinList}" var="single" varStatus="i">
									      <option value="${single}" ${amazonPortsDetail.asin eq single ?'selected':''}>${single}</option>
							      </c:forEach>	
							     </select>
							  </c:if>
							  <c:if test="${fn:length(asinList)<=1}">
							       ${amazonPortsDetail.asin}
							  </c:if>
							  <b>${amazonPortsDetail.accountName }</b> <a id="changeAsin" href="http://www.amazon.${amazonPortsDetail.country eq 'jp' || amazonPortsDetail.country eq 'uk'?'co.':''}${amazonPortsDetail.country eq 'com.unitek'?'com':amazonPortsDetail.country}/dp/${amazonPortsDetail.asin}"  target="_blank">&nbsp;open</a>
							</td>
							<td style="vertical-align:middle; "><b>Date</b></td>
							<td style="vertical-align:middle; ">
							   <input style="width: 90px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});"  readonly="readonly"  class="Wdate" type="text" name="queryTime" value="<fmt:formatDate value="${amazonPortsDetail.queryTime}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			                </td>
						</tr>
						<tr>
						   <td style=" height:30px;vertical-align:middle; "><b>Ean</b></td>
						   <td style="vertical-align:middle; ">${portsDetail.ean}</td>
						   <td style="vertical-align:middle; "><b>Sku</b></td>
						   <td style="vertical-align:middle; "> <c:if test="${not empty portsDetail.sku}">
							    <c:forEach items="${fn:split(portsDetail.sku,',')}" var="sku" varStatus="i">
							      <c:if test="${i.count eq 1}"><a href="#" title="${portsDetail.sku}">${sku}...</a></c:if>
							    </c:forEach>
							  </c:if>
				            </td>
						</tr>
						<tr>
						  	<td style=" height:30px;vertical-align:middle; "><b>大小组合</b></td>
						  	<td style="vertical-align:middle; ">${portsDetail.bySize eq '1'?'是':'否'}</td>
							<td style="vertical-align:middle; "><b>颜色组合</b></td>
							<td style="vertical-align:middle; ">${portsDetail.byColor eq '1'?'是':'否'}</td>
						</tr>
						<tr>
						  	<td style=" height:30px;vertical-align:middle; "><b>Parent_Asin</b></td>
						  	<td style="vertical-align:middle; ">
						      ${portsDetail.parentPortsDetail.asin }
						      <c:if test="${not empty portsDetail.parentPortsDetail.asin}">
						         <a  href="http://www.amazon.${amazonPortsDetail.country eq 'jp' || amazonPortsDetail.country eq 'uk'?'co.':''}${amazonPortsDetail.country eq 'com.unitek'?'com':amazonPortsDetail.country}/dp/${portsDetail.parentPortsDetail.asin }"  target="_blank">&nbsp;open</a>
						      </c:if>
						    </td>
						    <td style="vertical-align:middle; "><b>Parent_Sku</b></td>
							<td style="vertical-align:middle; "><a  href="${ctx}/amazoninfo/amazonPortsDetail/viewParent?id=${portsDetail.parentPortsDetail.id}" target="_blank">${portsDetail.parentPortsDetail.sku }</a></td>
						</tr>
						<c:if test="${not empty reviewList}">
						    <tr>
							    <td colspan="5">
							                总共${total}条评论，其中评测邀评${fn:length(reviewList)}条<br/>
							        <c:forEach  items="${reviewList}" var="review" varStatus="i"><a target='_blank' href='${review}'>评论${i.index+1}&nbsp;&nbsp;</a></c:forEach>
							    </td>
						   </tr>
						</c:if>
						
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
							<td><b>Length*Height*Width(inches)</b></td>
							<td>${portsDetail.packageLength}*${portsDetail.packageWidth}*${portsDetail.packageHeight}</td>
							<td><b>Weight(pounds)</b></td>
							<td>${portsDetail.packageWeight}</td>
						</tr>
						<tr>
							<td><b>Color</b></td>
							<td>${portsDetail.color}</td>
							<td><b>Size</b></td>
							<td>${portsDetail.size}</td>
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
							<td><b>ItemType</b></td>
							<td colspan="3">${portsDetail.initPath}</td>
						</tr>
						<tr>
							<td><b>Title</b></td>
							<td colspan="3">${portsDetail.title}</td>
						</tr>
						<c:if test="${not empty changeMap&&not empty changeMap['title'] }">
						    <td><span style="color:red;"><b>Title[Latest Update]</b></span></td>
							<td colspan="3">${changeMap['title']}</td>
						</c:if>
						<tr>
							
							<c:choose>
							   <c:when test="${not empty changeMap&&not empty changeMap['description'] }">
							      <td><b>Description</b></td>
							      <td colspan="3">
							         <ul class="nav nav-tabs" id="cNav">
											<li class='active'><a href="#tab0" data-toggle="tab"><b>Description</b></a></li>
											<li><a href="#tab1" data-toggle="tab"><span style="color:red;"><b>Description[Latest Update]</b></span></a></li>
									 </ul>
									 <div class="tab-content" id="cTab">
										 <div class="tab-pane active" id="tab0">${portsDetail.description}</div>
							             <div class="tab-pane" id="tab1">${changeMap['description']}</div>
							         </div>    
							      
							      </td>
							   </c:when>
							   <c:otherwise>
							      <td><b>Description</b></td>
							      <td colspan="3">${portsDetail.description}</td>
							   </c:otherwise>
							</c:choose>
						</tr>
					</tbody>
				</table>
				
				
				<table class="table table-striped table-bordered table-condensed example">
					<tbody>
						<tr class="success">
							<td><b>产品卖点</b></td>
						</tr>
						<tr>
							<td>
								<b><span style="color:green;">${not empty portsDetail.feature1?"1.":""}</span></b>&nbsp;${portsDetail.feature1 }<br/>
								<b><span style="color:green;">${not empty portsDetail.feature2?"2.":""}</span></b>&nbsp;${portsDetail.feature2 }<br/>
								<b><span style="color:green;">${not empty portsDetail.feature3?"3.":""}</span></b>&nbsp;${portsDetail.feature3 }<br/>
								<b><span style="color:green;">${not empty portsDetail.feature4?"4.":""}</span></b>&nbsp;${portsDetail.feature4 }<br/>
								<b><span style="color:green;">${not empty portsDetail.feature5?"5.":""}</span></b>&nbsp;${portsDetail.feature5 }<br/>
							</td>
						</tr>
						<c:if test="${not empty changeMap&&(not empty changeMap['feature1']||not empty changeMap['feature2']||not empty changeMap['feature3']||not empty changeMap['feature4']||not empty changeMap['feature5']) }">
							<tr class="success">
								<td><span style="color:red;"><b>产品卖点[Latest Update]</b></span></td>
							</tr>
						   <tr>
							<td>
							   <c:if test="${not empty changeMap['feature1'] }">
							      <b><span style="color:red;">1.</span></b>&nbsp;${changeMap['feature1'] }<br/>
							   </c:if>
							    <c:if test="${not empty changeMap['feature2'] }">
							      <b><span style="color:red;">2.</span></b>&nbsp;${changeMap['feature2'] }<br/>
							   </c:if>
							    <c:if test="${not empty changeMap['feature3'] }">
							      <b><span style="color:red;">3.</span></b>&nbsp;${changeMap['feature3'] }<br/>
							   </c:if>
							    <c:if test="${not empty changeMap['feature4'] }">
							      <b><span style="color:red;">4.</span></b>&nbsp;${changeMap['feature4'] }<br/>
							   </c:if>
							    <c:if test="${not empty changeMap['feature5'] }">
							      <b><span style="color:red;">5.</span></b>&nbsp;${changeMap['feature5'] }<br/>
							   </c:if>
							 </td>
						   </tr>
						</c:if>
					</tbody>
				</table>
				
				<table class="table table-striped table-bordered table-condensed example">
					<tbody>
						<tr class="success">
							<td><b>关键字搜索</b></td>
						</tr>
						<tr>
							<td>
								<b><span style="color:green;">${not empty portsDetail.keyword1?"1.":""}</span></b>&nbsp;${portsDetail.keyword1 }<br/>
								<b><span style="color:green;">${not empty portsDetail.keyword2?"2.":""}</span></b>&nbsp;${portsDetail.keyword2 }<br/>
								<b><span style="color:green;">${not empty portsDetail.keyword3?"3.":""}</span></b>&nbsp;${portsDetail.keyword3 }<br/>
								<b><span style="color:green;">${not empty portsDetail.keyword4?"4.":""}</span></b>&nbsp;${portsDetail.keyword4 }<br/>
								<b><span style="color:green;">${not empty portsDetail.keyword5?"5.":""}</span></b>&nbsp;${portsDetail.keyword5 }<br/>
							</td>
						</tr>
						<c:if test="${not empty changeMap&&(not empty changeMap['keyword1']||not empty changeMap['keyword2']||not empty changeMap['keyword3']||not empty changeMap['keyword4']||not empty changeMap['keyword5']) }">
							<tr class="success">
								<td><span style="color:red;"><b>关键字搜索[Latest Update]</b></span></td>
							</tr>
						   <tr>
							<td>
							   <c:if test="${not empty changeMap['keyword1'] }">
							      <b><span style="color:red;">1.</span></b>&nbsp;${changeMap['keyword1'] }<br/>
							   </c:if>
							    <c:if test="${not empty changeMap['keyword2'] }">
							      <b><span style="color:red;">2.</span></b>&nbsp;${changeMap['keyword2'] }<br/>
							   </c:if>
							    <c:if test="${not empty changeMap['keyword3'] }">
							      <b><span style="color:red;">3.</span></b>&nbsp;${changeMap['keyword3'] }<br/>
							   </c:if>
							    <c:if test="${not empty changeMap['keyword4'] }">
							      <b><span style="color:red;">4.</span></b>&nbsp;${changeMap['keyword4'] }<br/>
							   </c:if>
							    <c:if test="${not empty changeMap['keyword5'] }">
							      <b><span style="color:red;">5.</span></b>&nbsp;${changeMap['keyword5'] }<br/>
							   </c:if>
							 </td>
						   </tr>
						</c:if>
					</tbody>
				</table>
				
				
				<table class="table table-striped table-bordered table-condensed">
				<tbody>
					<tr class="warning">
						<td colspan=10 ><b>排名目录排行</b></td>
					</tr>
					<tr>
					    <td width="200px"><b>目录名称</b></td>
						<td width="200px"><b>目录结构</b></td>
						<td width="100px"><b>URL</b></td>
						<td width="70px"><b>排行</b></td>
					</tr>
					<c:forEach var="rank" items="${portsDetail.rankItems}" varStatus="i">
					   <c:if test="${fns:getDateByPattern(rank.queryTime,'yyyyMMdd') eq fns:getDateByPattern(amazonPortsDetail.queryTime,'yyyyMMdd')&& !fns:endsWith(rank.catalog,'_on_website')}">
						<tr>
						    <td>${rank.catalogName }</td>
							<td>${rank.pathName }</td>
							<td><a href="${rank.link }" target="_blank">${rank.link }</a></td>
							<td>
							   <c:if test="${empty map[rank.catalog] }">${rank.rank }</c:if>
							    <c:if test="${not empty map[rank.catalog] }">
								    <c:if test="${rank.rank> map[rank.catalog] }"><a href="#" title="昨天排名:${map[rank.catalog]}"><span style="color:red;"><b>${rank.rank }&nbsp;&nbsp;↓${rank.rank-map[rank.catalog]}</b></span></a></c:if>
								    <c:if test="${rank.rank< map[rank.catalog] }"><a href="#" title="昨天排名:${map[rank.catalog]}"><span style="color:green;"><b>${rank.rank }&nbsp;&nbsp;↑${map[rank.catalog]-rank.rank }</b></span></a></c:if>
							        <c:if test="${rank.rank== map[rank.catalog] }">${rank.rank }</c:if>
							    </c:if>
							</td>
						</tr>
						</c:if>
					</c:forEach>
				</tbody>
				</table>
				
            <%--  <c:if test="${not empty changeList }">
                 
				<table class="table table-striped table-bordered table-condensed">
				<tbody>
					<tr class="info">
						<td colspan="2"><b>帖子优化详情</b></td>
					</tr>
					<c:forEach var="change" items="${changeList}" varStatus="i">
					   <tr class="success">
						  <td colspan="2"><fmt:formatDate value="${change.createDate}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
					   </tr>
					   <c:forEach items="${change.items}" var="item">
					        <c:if test="${not empty item.title&&item.title ne portsDetail.title}">
					        <tr>
					             <td><b>Title</b></td>
					             <td>${item.title }</td>
					        </tr>     
					        </c:if>
					         <c:if test="${not empty item.description&&item.description ne portsDetail.description}">
					         <tr>
					             <td><b>Description</b></td>
					             <td>${item.description }</td>
					         </tr>    
					        </c:if>
					          <c:if test="${not empty item.feature1&&item.feature1 ne portsDetail.feature1}">
					            <tr>
					             <td><b>Feature1</b></td>
					             <td>${item.feature1 }</td>
					            </tr>
					        </c:if>
					          <c:if test="${not empty item.feature1&&item.feature1 ne portsDetail.feature2}">
					            <tr>
					             <td><b>Feature2</b></td>
					             <td>${item.feature2 }</td>
					            </tr> 
					        </c:if>
					          <c:if test="${not empty item.feature1&&item.feature3 ne portsDetail.feature3}">
					            <tr>
					             <td><b>Feature3</b></td>
					             <td>${item.feature3 }</td>
					             </tr>
					        </c:if>
					          <c:if test="${not empty item.feature1&&item.feature4 ne portsDetail.feature4}">
					            <tr>
					             <td><b>Feature4</b></td>
					             <td>${item.feature4 }</td>
					             </tr>
					        </c:if>
					          <c:if test="${not empty item.feature5&&item.feature5 ne portsDetail.feature5}">
					            <tr>
					             <td><b>Feature5</b></td>
					             <td>${item.feature5 }</td>
					            </tr> 
					        </c:if>
					        <c:if test="${not empty item.keyword1&&item.keyword1 ne portsDetail.keyword1}">
					          <tr>
					             <td><b>Keyword1</b></td>
					             <td>${item.keyword1 }</td>
					           </tr>  
					        </c:if>
					         <c:if test="${not empty item.keyword2&&item.keyword2 ne portsDetail.keyword2}">
					             <tr> 
					             <td><b>Keyword2</b></td>
					             <td>${item.keyword2 }</td>
					            </tr> 
					        </c:if>
					         <c:if test="${not empty item.keyword3&&item.keyword3 ne portsDetail.keyword3}">
					           <tr>
					             <td><b>Keyword3</b></td>
					             <td>${item.keyword3 }</td>
					           </tr>  
					        </c:if>
					         <c:if test="${not empty item.keyword4&&item.keyword4 ne portsDetail.keyword4}">
					           <tr>
					             <td><b>Keyword4</b></td>
					             <td>${item.keyword4 }</td>
					            </tr> 
					        </c:if>
					         <c:if test="${not empty item.keyword5&&item.keyword5 ne portsDetail.keyword5}">
					           <tr>
					             <td><b>Keyword5</b></td>
					             <td>${item.keyword5 }</td>
					           </tr>  
					        </c:if>
					   </c:forEach>
					</c:forEach>
				</tbody>
				</table>
             
             
             </c:if> --%>

             <table class="table table-striped table-bordered table-condensed">
				<tbody>
					<tr class="warning">
						<td colspan="8"><b>图片</b></td>
					</tr>
					<tr>
					    <td colspan='4'><c:if test="${not empty portsDetail.picture1}"><img style="width: 100px;height: 100px" src="${portsDetail.picture1}" /> </c:if></td></td>
					</tr>
					<tr>
					    <td>
						    <c:if test="${not empty  portsDetail.picture2}">
							   <img style="width: 100px;height: 100px" src="${portsDetail.picture2}" /> 
							</c:if>
						</td>
					    <td><c:if test="${not empty  portsDetail.picture3}"><img style="width: 100px;height: 100px" src="${portsDetail.picture3}" /> </c:if></td>
					    <td><c:if test="${not empty  portsDetail.picture4}"><img style="width: 100px;height: 100px" src="${portsDetail.picture4}" /> </c:if></td>
					    <td><c:if test="${not empty  portsDetail.picture5}"><img style="width: 100px;height: 100px" src="${portsDetail.picture5}" /> </c:if></td>
					</tr>
					<tr>
					    <td><c:if test="${not empty  portsDetail.picture6}"><img style="width: 100px;height: 100px" src="${portsDetail.picture6}" /> </c:if></td>
					    <td><c:if test="${not empty  portsDetail.picture7}"><img style="width: 100px;height: 100px" src="${portsDetail.picture7}" /> </c:if></td>
					    <td><c:if test="${not empty  portsDetail.picture8}"><img style="width: 100px;height: 100px" src="${portsDetail.picture8}" /> </c:if></td>
					    <td><c:if test="${not empty  portsDetail.picture9}"><img style="width: 100px;height: 100px" src="${portsDetail.picture9}" /> </c:if></td>
					</tr>
				</tbody>
				</table> 
			</div>
		</div>
		<!-- content -->
		<div class="clear"></div>
	</div>

</body>
</html>
