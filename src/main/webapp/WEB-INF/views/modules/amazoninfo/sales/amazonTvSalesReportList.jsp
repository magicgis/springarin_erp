<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>亚马逊产品销售报告明细</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<%@include file="/WEB-INF/views/include/datatables.jsp"%>
<style type="text/css">
.spanexr {
	float: right;
	min-height: 40px
}

.spanexl {
	float: left;
}

</style>
<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			var flag=0;
			//<c:if test="${fn:length(data)==0}">
				$("#tip").modal();
			//</c:if>
		
		    
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
		    
			
			//构造图表
			//ecahrts-----------------
			var myChart;
			
			require.config({
		        paths:{ 
		            echarts:'${ctxStatic}/echarts/js/echarts',
		            'echarts/chart/line': '${ctxStatic}/echarts/js/echarts',
		            'echarts/chart/bar': '${ctxStatic}/echarts/js/echarts'
		        }
		    });
		    // Step:4 require echarts and use it in the callback.
		    // Step:4 动态加载echarts然后在回调函数中开始使用，注意保持按需加载结构定义图表路径
		    require(
		    	['echarts','echarts/chart/line','echarts/chart/bar'],
		        function(ec) {
		            myChart = ec.init(document.getElementById("totalChart"));
		            myChart.showLoading({
					    text: '正在努力的读取数据中...',    //loading话术
					});
					//ecahrts-----------------
		            var option = {
		            	title:{text:'销售额按日统计',x:'center'},		
		                tooltip : {
		                    trigger: 'item'
		                },
		                legend: {
		                	y:30,
		                	selected: {'总计':true},
		                    data:['总计']
		                },
		                toolbox: {
		                    show : true,
		                    feature : {
		                        mark : false,
		                        dataView :false,
		                        magicType:{type:['line','bar'],show:true},
		                        restore : {show:true},
		                        saveAsImage : {show:true}
		                    }
		                },
		                calculable : false,
		                animation:false,
		                xAxis : [
			                    	{
			                       		axisLabel: {
											rotate: 50
										}, 
				                        type : 'category',
				                        data :[<c:forEach items="${xAxis}" var="x" varStatus="i">'${x}'${i.last?'':','}</c:forEach>],
				                   		boundaryGap:false
				                    }
		                ],
		                yAxis : [
		                    {
		                        type : 'value',
		                        splitArea : {show : true},
			                    axisLabel : {
		                            formatter: '{value}€'
		                        },
		                        boundaryGap:[0,0.5]
		                    }
		                ],
		                series : [
							{
						      name:'总计',
						      type:'line',
						      data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber pattern="#######.##" value="${empty otherData['total'][x].sales?0:otherData['total'][x].sales}"  maxFractionDigits="2"  />${i.last?'':','}</c:forEach>],
						      markLine : {
						      	 data : [
						              {type : 'average', name: '平均线'}
						          ]
						      }
							}
		                ]
		            };
				    myChart.setOption(option);
		    	}
			);
		});
		
		$.fn.textScroll=function(){
		    var speed=60,flag=null,tt,that=$(this),child=that.children();
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
		
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            f = Math.round(x*10)/10;  
	            return f;  
	    }
		 
		 function myrefresh() {
			 $.ajax({
			    type: 'post',
			    async:false,
			    url: '${pageContext.request.contextPath}/amazoninfo/tvSalesReprots/getConnect' ,
			    success:function(data){
			    	if(1==data){
			    		window.location.reload();
			    	} else {
			    		setTimeout('myrefresh()', 60000 * 30); //指定时间刷新一次 
			    	}
			    },
			    error: function(XMLHttpRequest, textStatus, errorThrown) {
			    	setTimeout('myrefresh()', 60000 * 30); //指定时间刷新一次 
			    }
			 });
		 } 
		 setTimeout('myrefresh()', 60000 * 30); //指定时间刷新一次30min
	</script>
</head>
<body>
	<br/>
	<div style="font-size: 25px; font-weight: bold; text-align: center;">
		<c:choose>
			<c:when test="${'USD' eq saleReport.currencyType}">
				亚马逊${'total' eq saleReport.groupName?'':allLine[saleReport.groupName] }销量销售额统计(美金)
			</c:when>
			<c:otherwise>
				亚马逊${'total' eq saleReport.groupName?'':allLine[saleReport.groupName] }销量销售额统计(欧元)
			</c:otherwise>
		</c:choose>
	</div>	
	<br/>
	
	   <div class="alert alert-info">
	      <strong>今日提醒：</strong>
	      <span class="badge badge-success">数据最后更新时间:<fmt:formatDate value="${lastUpdateTime}" pattern="yyyy-MM-dd HH:mm"/></span> .
	      
		   <div id="showRate" style="position:relative; white-space:nowrap; overflow:hidden; height:20px;margin-left:50px;margin-right:250px;">
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
	<form:form id="searchForm" modelAttribute="saleReport" action="${pageContext.request.contextPath}/amazoninfo/tvSalesReprots" method="post" class="form-search">
		
	</form:form>
	
	<table class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;" rowspan="2">排名</th>
				<th style="text-align: center;vertical-align: middle;" rowspan="2">平台</th>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
				<c:if test="${not empty realList1&& not empty countryMonthGoal1}">
					    <c:if test="${saleReport.searchType=='1'&&day>='03'&& fns:getDate('yyyyMM') eq fns:getDateByPattern(saleReport.end,'yyyyMM')}">
					    <th colspan="4" style="text-align: center;vertical-align: middle;
					      ${i.index==1?'color: #08c;':''}">
						  ${xAxis[fn:length(xAxis)-i.index]}${type}
						  </th>
						</c:if>	
					
						 <c:if test="${saleReport.searchType!='1'||day<'03'||fns:getDate('yyyyMM') ne fns:getDateByPattern(saleReport.end,'yyyyMM')}"><th colspan="3" style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
						  ${xAxis[fn:length(xAxis)-i.index]}${type}
						  </th>
						</c:if>		
				</c:if>
				<c:if test="${empty realList1}">		
					<th colspan="3" style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
						  ${xAxis[fn:length(xAxis)-i.index]}${type}
				    </th>
				</c:if>			
				</c:forEach>
			</tr>
			<tr>
			  	<c:forEach begin="1" end="3" step="1" varStatus="i">
					<th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">销售额(${currencySymbol})</th>
					<th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">销量</th>
					<th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">Avg. Sales(${currencySymbol})</th>		
					<c:if test="${not empty realList1&& not empty countryMonthGoal1&& fns:getDate('yyyyMM') eq fns:getDateByPattern(saleReport.end,'yyyyMM') }">
				    <c:if test="${saleReport.searchType=='1'&&day>='03'}"><th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
					  <c:if test="${fns:getDate('yyyyMMdd')==xAxis[fn:length(xAxis)-i.index]}">目标(${currencySymbol})</c:if>
					  <c:if test="${fns:getDate('yyyyMMdd')!=xAxis[fn:length(xAxis)-i.index]}">累计完成率(%)</c:if>
					  </th>
					</c:if>	
				</c:if>		
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		    <tr>
				<td colspan='2' style="text-align: center;vertical-align: middle;"><b>英语国家</b></td>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
					<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><fmt:formatNumber value="${data['en'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
					<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${data['en'][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
				   <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><fmt:formatNumber value="${data['en'][xAxis[fn:length(xAxis)-i.index]].sales/data['en'][xAxis[fn:length(xAxis)-i.index]].salesVolume}" maxFractionDigits="2" minFractionDigits="2" /></td>
				   <c:if test="${not empty realList1&& not empty countryMonthGoal1&& fns:getDate('yyyyMM') eq fns:getDateByPattern(saleReport.end,'yyyyMM')}">
				    <c:if test="${saleReport.searchType=='1'&&day>='03'&&fns:getDate('yyyyMMdd')==xAxis[fn:length(xAxis)-i.index]}">
					   <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
						    <c:if test="${realList1[xAxis[fn:length(xAxis)-i.index]]['com'].autoDayGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['ca'].autoDayGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['uk'].autoDayGoal>0 }">
						        <fmt:formatNumber maxFractionDigits="2"  value="${realList1[xAxis[fn:length(xAxis)-i.index]]['com'].autoDayGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['ca'].autoDayGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['uk'].autoDayGoal}" pattern="#0.00"/>
						    </c:if>
						     <c:if test="${realList1[xAxis[fn:length(xAxis)-i.index]]['com'].autoDayGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['ca'].autoDayGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['uk'].autoDayGoal<=0}">
						                           <font color='green'>已达成</font>
						    </c:if>
					  </td>
				   </c:if>	
				 <c:if test="${saleReport.searchType=='1'&&day>='03'&&fns:getDate('yyyyMMdd')!=xAxis[fn:length(xAxis)-i.index]}">
				    <c:set var='tempGoal' value="${realList1[xAxis[fn:length(xAxis)-i.index]]['com'].addUpGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['ca'].addUpGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['uk'].addUpGoal}"/>
				    <c:set var='tempSales' value="${realList1[xAxis[fn:length(xAxis)-i.index]]['com'].addUpSales+realList1[xAxis[fn:length(xAxis)-i.index]]['ca'].addUpSales+realList1[xAxis[fn:length(xAxis)-i.index]]['uk'].addUpSales}"/>
				    <c:set var="totalRate" value="${tempSales*100/tempGoal }" />
				   <td style="text-align: center;vertical-align: middle;${totalRate<100?'color: #F00;':'color:#009900'}">
				      <c:if test="${totalRate>=0}"><fmt:formatNumber maxFractionDigits="1"  value="${totalRate }" pattern="#0.0"/></c:if>
				      
				   </td>
				</c:if>	
			  </c:if> 
				</c:forEach>
			</tr>
			<c:forEach var="temp" items="${sec}" varStatus="i">
			  <c:if test="${'mx' ne temp.country}">
				<tr>
					<td style="text-align: center;vertical-align: middle;"><span class="badge badge-${i.count<4?'important':'success'}">${i.count}</span></td>				
					<td style="text-align: center;vertical-align: middle;">${fns:getDictLabel(temp.country,'platform','')}</td>
					<c:forEach begin="1" end="3" step="1" varStatus="i">
						
						<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><fmt:formatNumber value="${data[temp.country][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2"/></td>
						<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${data[temp.country][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
					   <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
					   <c:if test="${data[temp.country][xAxis[fn:length(xAxis)-i.index]].salesVolume!=0}">
					      <fmt:formatNumber value="${data[temp.country][xAxis[fn:length(xAxis)-i.index]].sales/data[temp.country][xAxis[fn:length(xAxis)-i.index]].salesVolume}" maxFractionDigits="2" minFractionDigits="2"/>
					   </c:if>
					   </td>
					<c:if test="${not empty realList1&& not empty countryMonthGoal1&& fns:getDate('yyyyMM') eq fns:getDateByPattern(saleReport.end,'yyyyMM')}">
					   <c:if test="${saleReport.searchType=='1'&&day>='03'}">
					       <c:if test="${fns:getDate('yyyyMMdd')==xAxis[fn:length(xAxis)-i.index]}">
					        <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
					          <c:if test="${realList1[xAxis[fn:length(xAxis)-i.index]][temp.country].autoDayGoal>0}">
					             <fmt:formatNumber maxFractionDigits="2"  value="${realList1[xAxis[fn:length(xAxis)-i.index]][temp.country].autoDayGoal}" pattern="#0.00"/>
					          </c:if>
					           <c:if test="${realList1[xAxis[fn:length(xAxis)-i.index]][temp.country].autoDayGoal<=0}">
					                                 <font color='green'>已达成</font>
					           </c:if>
					        </td>  
					       </c:if>
					       <c:if test="${fns:getDate('yyyyMMdd')!=xAxis[fn:length(xAxis)-i.index]}">
					          <c:set var="rate" value="${realList1[xAxis[fn:length(xAxis)-i.index]][temp.country].rate}" />
					           <td style="text-align: center;vertical-align: middle;${rate<100?'color: #F00;':'color:#009900'}">
					             <fmt:formatNumber maxFractionDigits="1"  value="${realList1[xAxis[fn:length(xAxis)-i.index]][temp.country].rate}" pattern="#0.0"/>
					           </td>
					       </c:if>
					   </c:if>
					 </c:if>
					</c:forEach>
				</tr>
				</c:if>
			</c:forEach>
			<!-- Amazon欧洲合计 -->
			<tr>
				<td colspan='2' style="text-align: center;vertical-align: middle;"><b>Amazon欧洲合计</b></td>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
					
					<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><fmt:formatNumber value="${data['eu'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
					<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${data['eu'][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
				   <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><fmt:formatNumber value="${data['eu'][xAxis[fn:length(xAxis)-i.index]].sales/data['eu'][xAxis[fn:length(xAxis)-i.index]].salesVolume}" maxFractionDigits="2" minFractionDigits="2" /></td>
				   
				   
				   <c:if test="${not empty realList1&& not empty countryMonthGoal1&& fns:getDate('yyyyMM') eq fns:getDateByPattern(saleReport.end,'yyyyMM')}">
				 <c:if test="${saleReport.searchType=='1'&&day>='03'&&fns:getDate('yyyyMMdd')==xAxis[fn:length(xAxis)-i.index]}">
				 	<c:set var="goal" value=""></c:set>
				    <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
				    <c:set var="goal" value="${realList1[xAxis[fn:length(xAxis)-i.index]]['de'].autoDayGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['fr'].autoDayGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['uk'].autoDayGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['it'].autoDayGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['es'].autoDayGoal}"></c:set>
				    <c:if test="${goal>0 }">
				        <fmt:formatNumber maxFractionDigits="2"  value="${goal }" pattern="#0.00"/>
				    </c:if>
				     <c:if test="${goal<=0}">
				                           <font color='green'>已达成</font>
				    </c:if>
				 </td>
				</c:if>	
				 <c:if test="${saleReport.searchType=='1'&&day>='03'&&fns:getDate('yyyyMMdd')!=xAxis[fn:length(xAxis)-i.index]}">
				  <c:set var='tempGoal' value="${realList1[xAxis[fn:length(xAxis)-i.index]]['de'].addUpGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['fr'].addUpGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['it'].addUpGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['es'].addUpGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['uk'].addUpGoal}"/>
				    <c:set var='tempSales' value="${realList1[xAxis[fn:length(xAxis)-i.index]]['de'].addUpSales+realList1[xAxis[fn:length(xAxis)-i.index]]['fr'].addUpSales+realList1[xAxis[fn:length(xAxis)-i.index]]['it'].addUpSales+realList1[xAxis[fn:length(xAxis)-i.index]]['es'].addUpSales+realList1[xAxis[fn:length(xAxis)-i.index]]['uk'].addUpSales}"/>
				    <c:set var="totalRate" value="${tempSales*100/tempGoal }" />
				   <td style="text-align: center;vertical-align: middle;${totalRate<100?'color: #F00;':'color:#009900'}">
				      <c:if test="${totalRate>=0}"><fmt:formatNumber maxFractionDigits="1"  value="${totalRate }" pattern="#0.0"/></c:if>
				   </td>
				</c:if>	
			  </c:if> 
				</c:forEach>
			</tr>
			
			<tr>
				<td colspan='2' style="text-align: center;vertical-align: middle;"><b>Amazon合计</b></td>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
					
					<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><fmt:formatNumber value="${data['total'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
					<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${data['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
				   <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><fmt:formatNumber value="${data['total'][xAxis[fn:length(xAxis)-i.index]].sales/data['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume}" maxFractionDigits="2" minFractionDigits="2" /></td>
				   <c:if test="${not empty realList1&& not empty countryMonthGoal1&& fns:getDate('yyyyMM') eq fns:getDateByPattern(saleReport.end,'yyyyMM')}">
				 <c:if test="${saleReport.searchType=='1'&&day>='03'&&fns:getDate('yyyyMMdd')==xAxis[fn:length(xAxis)-i.index]}">
				    <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
				    <c:if test="${realList1[xAxis[fn:length(xAxis)-i.index]]['totalAvg'].addUpSales<countryMonthGoal1 }">
				        <fmt:formatNumber maxFractionDigits="2"  value="${realList1[xAxis[fn:length(xAxis)-i.index]]['totalAvg'].autoDayGoal }" pattern="#0.00"/>
				    </c:if>
				     <c:if test="${realList1[xAxis[fn:length(xAxis)-i.index]]['totalAvg'].addUpSales>=countryMonthGoal1}">
				                           <font color='green'>已达成</font>
				    </c:if>
				 </td>
				</c:if>	
				 <c:if test="${saleReport.searchType=='1'&&day>='03'&&fns:getDate('yyyyMMdd')!=xAxis[fn:length(xAxis)-i.index]}">
				  <c:set var="totalRate" value="${realList1[xAxis[fn:length(xAxis)-i.index]]['totalAvg'].rate }" />
				   <td style="text-align: center;vertical-align: middle;${totalRate<100?'color: #F00;':'color:#009900'}">
				      <fmt:formatNumber maxFractionDigits="1"  value="${totalRate }" pattern="#0.0"/>
				 </td>
				</c:if>	
			  </c:if> 
				</c:forEach>
			</tr>
		</tbody>
	</table>
	<div style="width:98%;border:1px solid #ccc;">
		<div id="totalChart" style="height:400px"></div>
	</div>
	
</body>
</html>
