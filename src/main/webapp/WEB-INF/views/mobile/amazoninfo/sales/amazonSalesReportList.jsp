<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/mobile/include/head.jsp" %>
<style type="text/css">
a:link {
text-decoration: none;
}
a:visited {
text-decoration: none;
}
a:hover {
text-decoration: none;
}
a:active {
text-decoration: none;
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
			
			if($("#searchType").val()==1){
		    	 $("#showTab0").addClass("active");
		    }else if($("#searchType").val()==2){
		    	$("#showTab1").addClass("active");
		    }else if($("#searchType").val()==3){
		    	$("#showTab2").addClass("active");
		    }else{
		    	$("#showTab0").addClass("active");
		    }
			
			oldSearchFlag= $("#searchType").val();
		    
		    $(".total").each(function(){
				var i = $(this).parent().find("td").index($(this));
				var num = 0;
				$("#"+$(this).attr("tid")+" tr").find("td:eq("+i+")").each(function(){
					if($.isNumeric($(this).text())){
						num += parseInt($(this).text());
					}
				});
				$(this).text(num);
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
		    
		    $(".count").each(function(){
		    	var tr = $(this);
		    	var num = (tr.find("td:eq(4)").text()-tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'5':'4'})").text())*1000/tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'5':'4'})").text();
			    tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'6':'5'})").html("<span class=\"badge badge-info\">"+num.toFixed(2)+"‰</span>");
			    num = (tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'8':'7'})").text()-tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'9':'8'})").text())*1000/tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'9':'8'})").text();
			    tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'10':'9'})").html("<span class=\"badge badge-info\">"+num.toFixed(2)+"‰</span>");
		    })
		    
		    $("#showRate").textScroll();		
		});
		
		$.fn.textScroll=function(){
		    var speed=30,flag=null,tt,that=$(this),child=that.children();
		    var p_w=that.width(), w=child.width();
		    child.css({left:p_w});
		    var t=(w+p_w)/speed * 1000;
		    function play(m){
		        var tm= m==undefined ? t : m;
		        child.animate({left:-w},tm,"linear",function(){             
		            $(this).css("left",p_w);
		            play();
		        });                 
		    }
		    child.on({
		        mouseenter:function(){
		            var l=$(this).position().left;
		            $(this).stop();
		            tt=(-(-w-l)/speed)*1000;
		        },
		        mouseleave:function(){
		            play(tt);
		            tt=undefined;
		        }
		    });
		    play();
		}
		
		function searchTypes(searchFlag){
			if(oldSearchFlag==searchFlag){
				return;
			}
			$("#searchType").val(searchFlag);
			//必须不传
			$("#start").val("");
			$("#end").val("");
			$("input[name='country']").val("");
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
<div data-role="page">
	<jsp:include page="../../sys/headDiv.jsp"></jsp:include>
  <%--<div data-role="header" data-theme="b" data-position="fixed">
    <a href="${ctx}" data-role="button">Home</a>
	<input id="typeahead" type="text" class="span3 search-query" style="width:100%;" 
			placeholder="查看产品" autocomplete="off"  style="margin: 0 auto;" data-provide="typeahead" data-items="8" />
    <a href="${ctx}/logout" data-role="button" class="ui-btn-right">Logout</a>
  </div> --%>
<div data-role="content">
	<div class="alert alert-info" style="padding:5px 5px 5px 5px;">
	      您好,${fns:getUser().name}
	      <span class="badge badge-success">数据最后更新时间:<fmt:formatDate value="${lastUpdateTime}" pattern="yyyy-MM-dd HH:mm"/></span>
	   
	     <shiro:hasPermission name="amazoninfo:sale:accountBalance">
	    	<br/>
	        <b>亚马逊账户余额: <br/>
				DE:<fmt:formatNumber value="${balance['de']}" />€&nbsp;&nbsp; US:<fmt:formatNumber value="${balance['com']}" />
	            $&nbsp;&nbsp; UK:<fmt:formatNumber value="${balance['uk']}" />£ &nbsp;&nbsp;FR:<fmt:formatNumber value="${balance['fr']}" />€ &nbsp;&nbsp;
				JP:<fmt:formatNumber value="${balance['jp']}" />￥&nbsp;&nbsp; IT:<fmt:formatNumber value="${balance['it']}" />€&nbsp;&nbsp; ES:<fmt:formatNumber value="${balance['es']}" />€ &nbsp;&nbsp;
				CA:<fmt:formatNumber value="${balance['ca']}" />CAD&nbsp;&nbsp; MX:<fmt:formatNumber value="${balance['mx']}" />MXN</b>
		   </shiro:hasPermission>   
	  <div id="showRate" style="position:relative; white-space:nowrap; overflow:hidden; height:20px;margin-left:5px;margin-right:5px;">
      <div id="noticeList" style="position:absolute; top:0; height:20px;"><strong>实时汇率播报：</strong>
	       <span>1&nbsp;${saleReport.currencyType}=<fmt:formatNumber maxFractionDigits="3"  value="${change['usdToCny'] }" pattern="#0.000"/>&nbsp;CNY</span>&nbsp;&nbsp;
	       <span>1&nbsp;JPY=<fmt:formatNumber maxFractionDigits="3"  value="${change['jpy'] }" pattern="#0.000"/>&nbsp;${saleReport.currencyType}</span>&nbsp;&nbsp;
	       <span>1&nbsp;GBP=<fmt:formatNumber maxFractionDigits="3"  value="${change['gbp'] }" pattern="#0.000"/>&nbsp;${saleReport.currencyType}</span>&nbsp;&nbsp;
	       <c:if test="${'USD' eq saleReport.currencyType}">
	       	<span>1&nbsp;EUR=<fmt:formatNumber maxFractionDigits="3"  value="${change['eur'] }" pattern="#0.000"/>&nbsp;${saleReport.currencyType}</span>&nbsp;&nbsp;
	       </c:if>
	       <c:if test="${'EUR' eq saleReport.currencyType}">
	       	<span>1&nbsp;USD=<fmt:formatNumber maxFractionDigits="3"  value="${change['usd'] }" pattern="#0.000"/>&nbsp;${saleReport.currencyType}</span>&nbsp;&nbsp;
	       </c:if>
	       <span>1&nbsp;CAD=<fmt:formatNumber maxFractionDigits="3"  value="${change['cad'] }" pattern="#0.000"/>&nbsp;${saleReport.currencyType}</span>&nbsp;&nbsp;
	       <span>1&nbsp;MXN=<fmt:formatNumber maxFractionDigits="3"  value="${change['mxn'] }" pattern="#0.000"/>&nbsp;${saleReport.currencyType}</span>
	      
      </div>
      </div>
	</div>
	<form:form id="searchForm" modelAttribute="saleReport" action="${ctx}/amazoninfo/salesReprots/mobileList" method="post" class="form-search">
		<div style="height: 30px">
		<ul class="nav nav-pills" style="width:250px;float:left;" id="myTab">
			<li data-toggle="pills" id="showTab0"><a href="#" onclick="javaScript:searchTypes('1');return false;">By Day</a></li>
			<li data-toggle="pills" id="showTab1"><a href="#" onclick="javaScript:searchTypes('2');return false;">By Week</a></li>
			<li data-toggle="pills" id="showTab2"><a href="#" onclick="javaScript:searchTypes('3');return false;">By Month</a></li>
		</ul>
		<input id="searchType" name="searchType" type="hidden" value="${saleReport.searchType}" />
		<input name="country" type="hidden" value="${saleReport.country}"/>
		</div>
	</form:form>
	<div>
	<div style="float:left;">
		<table class="table table-striped table-bordered table-condensed">
		<thead>
			<tr><th style="text-align: center;vertical-align: middle;height:49px" rowspan="2">平台</th></tr>
		</thead>
		<tbody>
			<c:forEach var="temp" items="${sec}" varStatus="i">
				<tr><td style="text-align: center;vertical-align: middle;height:40px">${fns:getDictLabel(temp.country,'platform','')}</td></tr>
			</c:forEach>
			<tr><td style="text-align: center;vertical-align: middle;height:40px"><b>Amazon合计</b></td></tr>
		</tbody>
		</table>
	</div>
	<div style="overflow:auto">
	<table class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
					<th colspan="2" style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
						  ${xAxis[fn:length(xAxis)-i.index]}${type}
					</th>		
				</c:forEach>
			</tr>
			<tr>
			  	<c:forEach begin="1" end="3" step="1" varStatus="i">
					<th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:20px">销售额(${currencySymbol})</th>
					<th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:20px">销量</th>		
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="temp" items="${sec}" varStatus="i">
				<tr>
					<c:forEach begin="1" end="3" step="1" varStatus="i">
						<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:40px"><fmt:formatNumber value="${data[temp.country][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2"/></td>
						<%--<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${data[temp.country][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td> --%>
						<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:40px">
							<c:if test="${data[temp.country][xAxis[fn:length(xAxis)-i.index]].salesVolume > 0}">
							<a href="${ctx}/amazoninfo/salesReprots/productList?country=${temp.country}&type=${saleReport.searchType}&time=${xAxis[(fn:length(xAxis)-i.count)]}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small">
								${data[temp.country][xAxis[fn:length(xAxis)-i.index]].salesVolume}
							</a>
							</c:if>
						</td>
						
					</c:forEach>
				</tr>
			</c:forEach>
			<tr>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
					<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:40px"><fmt:formatNumber value="${data['total'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
					<%--<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${data['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td> --%>
					<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:40px">
						<c:if test="${data['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume > 0}">
						<a href="${ctx}/amazoninfo/salesReprots/productList?country=total&type=${saleReport.searchType}&time=${xAxis[(fn:length(xAxis)-i.count)]}&currencyType=${saleReport.currencyType}&lineType=total" class="btn btn-small">
							${data['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume}
						</a>
						</c:if>
					</td>	
				</c:forEach>
			</tr>
		</tbody>
	</table>
	</div></div>
	
	<div data-role="collapsible" data-mini="true">
    	<h6><span style="color:#08c">${xAxis[fn:length(xAxis)-1]}${type}其他平台销售额(${currencySymbol }):<fmt:formatNumber value="${otherData['total'][xAxis[fn:length(xAxis)-1]].sales-data['total'][xAxis[fn:length(xAxis)-1]].sales}" maxFractionDigits="2" minFractionDigits="2" />,点击查看</span></h6>
      	<!-- other 默认隐藏 -->
	<div style="margin-left:-15px;margin-right:-15px">
	<div style="float:left;">
		<table class="table table-striped table-bordered table-condensed">
		<thead>
			<tr><th style="text-align: center;vertical-align: middle;height:49px" rowspan="2">平台</th></tr>
		</thead>
		<tbody>
			<c:forEach var="otherOrder" items="${otherData}">
				<c:if test="${!fn:startsWith(otherOrder.key, '1')&&!fn:startsWith(otherOrder.key, 'amazonTotal')&&!fn:startsWith(otherOrder.key, '5')&&!fn:startsWith(otherOrder.key, '6')&&!fn:startsWith(otherOrder.key, '4')&&otherOrder.key!='total' }"> 
					<tr>
			    		<td style="text-align: center;vertical-align: middle;height:40px">${otherOrder.key eq '2-de'?'Vendor_DE':(otherOrder.key eq '2-com'?'Vendor_US':(otherOrder.key eq '3-de'?'Ebay_DE':'Ebay_US'))}</td>
			    	</tr>
			     </c:if> 
			</c:forEach>
			<tr><td  style="text-align: center;vertical-align: middle;height:40px">Offline</td></tr>
			<tr><td  style="text-align: center;vertical-align: middle;height:40px">Website</td></tr>
			<tr><td  style="text-align: center;vertical-align: middle;height:40px">Check24</td></tr>
			<tr><td  style="text-align: center;vertical-align: middle;height:40px"><b>其他合计</b></td></tr>
			<tr><td  style="text-align: center;vertical-align: middle;height:40px"><b>总计</b></td></tr>
		</tbody>
		</table>
	</div>
	<div style="overflow:auto;">
	<table class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
					<th colspan="2" style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
						  ${xAxis[fn:length(xAxis)-i.index]}${type}
					</th>		
				</c:forEach>
			</tr>
			<tr>
			  	<c:forEach begin="1" end="3" step="1" varStatus="i">
					<th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:20px">销售额(${currencySymbol})</th>
					<th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:20px">销量</th>		
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			<!-- other vendor、ebay) -->
			<c:forEach var="otherOrder" items="${otherData}">
			      <c:if test="${!fn:startsWith(otherOrder.key, '1')&&!fn:startsWith(otherOrder.key, 'amazonTotal')&&!fn:startsWith(otherOrder.key, '5')&&!fn:startsWith(otherOrder.key, '6')&&!fn:startsWith(otherOrder.key, '4')&&otherOrder.key!='total' }"> 
			      	<tr>
						<c:forEach begin="1" end="3" step="1" varStatus="i">
						  <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:40px"><fmt:formatNumber value="${otherData[otherOrder.key][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
						  <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:40px">
						  <c:if test="${otherData[otherOrder.key][xAxis[fn:length(xAxis)-i.index]].salesVolume > 0}">
						  <a href="${ctx}/amazoninfo/salesReprots/productList?country=total&type=${saleReport.searchType}&time=${xAxis[(fn:length(xAxis)-i.count)]}&currencyType=${saleReport.currencyType}&orderType=${otherOrder.key}&lineType=total" class="btn btn-small">
						  	${otherData[otherOrder.key][xAxis[fn:length(xAxis)-i.index]].salesVolume}
						  </a></c:if>
						  </td>		
						</c:forEach>
			        </tr>
			     </c:if> 
			</c:forEach>
			<!-- other offline) -->
			<tr>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
			     <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:40px">
			     <c:if test="${otherData['4-de'][xAxis[fn:length(xAxis)-i.index]].sales+otherData['4-com'][xAxis[fn:length(xAxis)-i.index]].sales+otherData['4-cn'][xAxis[fn:length(xAxis)-i.index]].sales > 0}">
			     	<fmt:formatNumber value="${otherData['4-de'][xAxis[fn:length(xAxis)-i.index]].sales+otherData['4-com'][xAxis[fn:length(xAxis)-i.index]].sales+otherData['4-cn'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" />
			     </c:if></td>
			     <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:40px">
			     <c:if test="${otherData['4-de'][xAxis[fn:length(xAxis)-i.index]].salesVolume+otherData['4-com'][xAxis[fn:length(xAxis)-i.index]].salesVolume+otherData['4-cn'][xAxis[fn:length(xAxis)-i.index]].salesVolume >0}">
			     	<a href="${ctx}/amazoninfo/salesReprots/productList?country=total&type=${saleReport.searchType}&time=${xAxis[(fn:length(xAxis)-i.count)]}&currencyType=${saleReport.currencyType}&orderType=4&lineType=total" class="btn btn-small">
					${otherData['4-de'][xAxis[fn:length(xAxis)-i.index]].salesVolume+otherData['4-com'][xAxis[fn:length(xAxis)-i.index]].salesVolume+otherData['4-cn'][xAxis[fn:length(xAxis)-i.index]].salesVolume}
			     </a></c:if></td>		
			</c:forEach>
			</tr>
			<!-- Website -->
			<tr>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
			     <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:40px">
			     <fmt:formatNumber value="${otherData['5-de'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
			     <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:40px">${otherData['5-de'][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
				</c:forEach>
			</tr>
			<!-- Check24 -->
			<tr>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
				     <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:40px">
				     <fmt:formatNumber value="${otherData['6-de'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
				     <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:40px">${otherData['6-de'][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
				</c:forEach>
			</tr>
			<!-- other total) -->
			<tr>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
			    <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:40px"><fmt:formatNumber value="${otherData['total'][xAxis[fn:length(xAxis)-i.index]].sales-data['total'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
			    <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:40px">${otherData['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume-data['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
				</c:forEach>
			</tr>
			<!-- amazon&other total) -->
			<tr>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
			    <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:40px"><fmt:formatNumber value="${otherData['total'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
			    <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''};height:40px">${otherData['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
				</c:forEach>
			</tr>
		</tbody>
	</table>
	</div></div>
    </div>
	
	</div>
	<!-- /footer -->
	<jsp:include page="../../sys/footDiv.jsp"></jsp:include>
	<!-- /footer -->
	</div>
</body>
</html>
