<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
<title>亚马逊产品线运营分析报告</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<%@include file="/WEB-INF/views/include/datatables.jsp"%>
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

			$(".countryHref").click(function(){
				$("#country").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			<c:forEach var="lightDeals" items="${lightDealsMap}">
				$("#${lightDeals.key}dealsDiv").click(function(){
					var cssVal=$("#${lightDeals.key}deals").css("display");
					if(cssVal=="none"){
						$("#${lightDeals.key}deals").css("display","block");
					}else{
						$("#${lightDeals.key}deals").css("display","none");
					}
				});
			</c:forEach>
			
          
			<c:forEach var="ads" items="${adsTypeMap}">
				$("#${ads.key}adsDiv").click(function(){
					var cssVal=$("#${ads.key}ads").css("display");
					if(cssVal=="none"){
						$("#${ads.key}ads").css("display","block");
					}else{
						$("#${ads.key}ads").css("display","none");
					}
				});
				
				$("#${ads.key}alladsDiv").click(function(){
					var cssVal=$("#${ads.key}allads").css("display");
					if(cssVal=="none"){
						$("#${ads.key}allads").css("display","block");
					}else{
						$("#${ads.key}allads").css("display","none");
					}
				});
		   </c:forEach>
		
			
			//ecahrts-----------------
			var marketing1Chart,myChart,rankChart,bestSellerChart;
			require.config({
		        paths:{ 
		            echarts:'${ctxStatic}/echarts/js/echarts',
		            'echarts/chart/line': '${ctxStatic}/echarts/js/echarts',
		            'echarts/chart/pie': '${ctxStatic}/echarts/js/echarts',
		            'echarts/chart/bar': '${ctxStatic}/echarts/js/echarts',
		            'echarts/chart/scatter': '${ctxStatic}/echarts/js/echarts'
		        }
		    });
		    require(
		    	['echarts','echarts/chart/line','echarts/chart/bar','echarts/chart/pie','echarts/chart/scatter'],
		        function(ec) {
		    		marketing1Chart = ec.init(document.getElementById('marketing1'));
		    		myChart = ec.init(document.getElementById('marketing2'));
		    		 <c:if test="${!fn:contains('total,noUs,eu,en,nonEn',saleProfit.country) }">
		    		    rankChart = ec.init(document.getElementById('rankChart'));
		    		 </c:if>
		    		
		    		 bestSellerChart =  ec.init(document.getElementById('bestSellerChart'));
		    		var marketing1Option = {
			            	title:{text:'营销占比',x:'center',y:10},
			            	tooltip : {
			                    trigger: 'item',
			                    formatter: "{a} <br/>{b} : {c} ({d}%)"
			                },
			            	legend: {
			            		orient : 'vertical',
			            	    x : 'left',
			                	data:[<c:forEach items="${legendList}" var="legend" varStatus="i">
			            		'${legend}'${i.last?'':','}
			            		</c:forEach>]
			                },
			                toolbox: {
			                    show : true,
			                    feature : {
			                        mark : false,
			                        dataView : false,
			                        restore : {show:true},
			                        saveAsImage : {show:true}
			                    }
			                },
			            	series: [
			            	      		{
			            	      			name:'营销占比',
			            	      			center: ['50%', '60%'],
				                            radius : '50%',
				                            selectedOffset: 200, 
				                            type:'pie',
				                            data:[<c:forEach items="${legendList}" var="legend" varStatus="i">
				                            {value:'${marketingTypeMap[i.count]}',name:'${legend}'}${i.last?'':','}
			            	        		</c:forEach>],
			            	                itemStyle: {
			            	                	normal: {
				            	                    label: {
				            	                        show: true,
				            	                        position: 'outer',
				            	                        formatter: '{b}({d}%)'
				            	                    },
				            	                    labelLine: {
				            	                        show: true,
				            	                        length: 20,
				            	                        lineStyle: {
				            	                            width: 1,
				            	                            type: 'solid'
				            	                        }
				            	                    }
			            	                	}
			            	                }
			            	      		}
			            	    ]
			            };
		    		marketing1Chart.setOption(marketing1Option);
		    		
		    		
		    		var option = {
		    			    tooltip : {
		    			        trigger: 'axis',
		    			        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
		    			            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
		    			        }
		    			    },
		    			    legend: {
		    			        data:['自然','营销']
		    			    },
		    			    grid: {
		    			        left: '3%',
		    			        right: '4%',
		    			        bottom: '3%',
		    			        containLabel: true
		    			    },
		    			    xAxis : [
		    			        {
		    			            type : 'category',
		    			            data : [<c:forEach items='${marketingMap}' var='marketing' varStatus="i">
				            		          '${marketing.key}'${i.last?'':','}
				            		        </c:forEach>]
		    			        }
		    			    ],
		    			    yAxis : [
		    			        {
		    			            type : 'value'
		    			        }
		    			    ],
		    			    series : [
		    			        
		    			        {
		    			            name:'自然',
		    			            type:'bar',
		    			            stack: '市场',
		    			            data:[<c:forEach items='${marketingMap}' var='marketing' varStatus="i">
		            		                '${marketingMap[marketing.key].realOrder}'${i.last?'':','}
			            		        </c:forEach>]
		    			        },
		    			        {
		    			            name:'营销',
		    			            type:'bar',
		    			            stack: '市场',
		    			            data:[<c:forEach items='${marketingMap}' var='marketing' varStatus="i">
            		                   '${marketingMap[marketing.key].amsOrder}'${i.last?'':','}
		            		        </c:forEach>]
		    			        }
		    			    ]
		    			};
		    		
		    		 myChart.setOption(option);
		    		 
		    		 <c:if test="${!fn:contains('total,noUs,eu,en,nonEn',saleProfit.country) }">
		    		 var  rankOption = {
			    				title:{text:'销售类目平均日销与排名',x:'center'},
			                    tooltip: {
			                        trigger: 'item',
			                        show: true,
			                        formatter: function (params) {
			                            return  params[0] + ':<br/>('+params[2]+')';
			                        }, 
			                       
			                        axisPointer: {
			                            show: true,
			                            type: 'cross',
			                            lineStyle: {
			                                type: 'dashed',
			                                width: 1
			                            }
			                        }
			                    },
			                    legend: {
			                    	y:30,
			                    	selected: {<c:forEach items="${rankMap}" var="rank" varStatus="i">
			            		       '${rank.key}':${i.last?true:false}${i.last?'':','}</c:forEach> },
			                        data: [<c:forEach items="${rankMap}" var="rank" varStatus="i">
				            		       '${rank.key}'${i.last?'':','}</c:forEach> ]
			                    },
			                    toolbox: {
			                        show: true,
			                        feature: {
			                             mark: { show: true },
			                             dataZoom: { show: true },
			                            dataView: { show: true, readOnly: false },
			                            restore: { show: true },
			                            saveAsImage: { show: true }
			                        }
			                    },
			                    xAxis: [
			                        {
			                            name: "平均日销",
			                            type: 'value',
			                            scale: true,
			                            axisLabel: {
			                                formatter: '{value} '   // **
			                            }
			                        }
			                    ],
			                    yAxis: [
			                        {
			                            name: "排名",
			                            type: 'value',
			                            scale: true,
			                            axisLabel: {
			                                formatter: '{value}'  // **
			                            }
			                        }
			                    ],
			                    series: [
									<c:forEach items='${rankMap}' var='rank' varStatus="j">
										{
				                            name: '${rank.key}',
				                            type: 'scatter',
				                            data: [<c:forEach items='${rankMap[rank.key]}' var='tempRank' varStatus="i">
		         		                            [${rankMap[rank.key][tempRank.key]},${tempRank.key}] ${i.last?'':','}
		         		                        </c:forEach> ]
				                        }   
									   ${j.last?'':','}
									</c:forEach>
			                             
			                        
			                    ]
			    		};
			    		rankChart.setOption(rankOption);
		    		 </c:if>
		    		
		    		

			    		var bestSellerOption = {
			    				title:{text:'销售类目数和Bestseller数',x:'center'},
			    			    tooltip : {
			    			        trigger: 'axis',
			    			        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
			    			            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
			    			        }
			    			    },
			    			    legend: {
			    			    	y:30,
			                    	selected: {<c:forEach items="${bestSellerMap}" var="seller" varStatus="i">
			            		       '${seller.key}Bestseller数':${i.last?true:false},'${seller.key}销售类目数':${i.last?true:false}${i.last?'':','}</c:forEach> },
			                        data: [<c:forEach items="${bestSellerMap}" var="seller" varStatus="i">
				            		       '${seller.key}Bestseller数','${seller.key}销售类目数'${i.last?'':','}</c:forEach> ]
			    			    },
			    			    grid: {
			    			        left: '3%',
			    			        right: '4%',
			    			        bottom: '3%',
			    			        containLabel: true
			    			    },
			    			    xAxis : [
			    			             {
					    			            type : 'value'
					    			     }
			    			       
			    			    ],
			    			    yAxis : [
			    			              {
					    			            type : 'category',
					    			            data : [<c:forEach items='${countryList}' var='country' varStatus="i">
							            		          '${country}'${i.last?'':','}
							            		        </c:forEach>]
					    			     }
			    			    ],
			    			    series : [
										<c:forEach items='${bestSellerMap}' var='seller' varStatus="i">
											{
											    name: '${seller.key}Bestseller数',
											    type: 'bar',
											    data: [<c:forEach items='${countryList}' var='country' varStatus="i">
											             ${bestSellerMap[seller.key][country][3]} ${i.last?'':','}
											         </c:forEach> ]
											},
											{
											    name: '${seller.key}销售类目数',
											    type: 'bar',
											    data: [<c:forEach items='${countryList}' var='country' varStatus="i">
											             ${bestSellerMap[seller.key][country][2]} ${i.last?'':','}
											         </c:forEach> ]
											}
										    ${i.last?'':','}
										</c:forEach>
			    			        
			    			    ]
			    			};
			    		
			    		bestSellerChart.setOption(bestSellerOption);
		        }   	
		    );
		});
		
		function doSubmit(){
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
  	<ul class="nav nav-tabs">
		<li class="${'total' eq saleProfit.country?'active':''}"><a class="countryHref" href="#" key="total">总计</a></li>
		<li class="${'noUs' eq saleProfit.country?'active':''}"><a class="countryHref" href="#" key="noUs">总计(不含美国)</a></li>
		<li class="${'eu' eq saleProfit.country?'active':''}"><a class="countryHref" href="#" key="eu">欧洲</a></li>
		<li class="${'en' eq saleProfit.country?'active':''}"><a class="countryHref" href="#" key="en">英语国家</a></li>
		<li class="${'nonEn' eq saleProfit.country?'active':''}"><a class="countryHref" href="#" key="nonEn">非英语国家</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${saleProfit.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
	</ul>

   <form:form id="searchForm" modelAttribute="saleReport" action="${ctx}/amazoninfo/salesAnalysisReport" method="post" class="form-search">
		<input type="hidden" id="country" name="country" value="${saleProfit.country}"></input>
		<input id="searchType" name="searchType" type="hidden" value="${saleReport.searchType}" />
		
		<label>类型:</label>
		<select name="typeFlag" id="typeFlag" style="width:180px" onchange="doSubmit()">
			<c:forEach var="dic" items="${typeMap }">
				<option value="${dic.key }" ${typeFlag eq dic.key?'selected':''}>${dic.value }</option>
			</c:forEach>
		</select>
		
		<label>产品线:</label>
		<select name="line" id="line" style="width:120px" onchange="doSubmit()">
			<c:forEach var="line" items="${lines}">
				<option value="${line }" ${saleProfit.line eq line?'selected':''}>${line }线</option>
			</c:forEach>
		</select>
		
		<label>月份:</label>
		<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyyMM',isShowClear:false,maxDate:new Date(),onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="day" value="<fmt:formatDate value="${startDate}" pattern="yyyyMM"/>" class="input-small" id="day"/>
		-
		<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyyMM',isShowClear:false,maxDate:new Date(),onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${lastDate}" pattern="yyyyMM"/>" class="input-small" id="end"/>
		<%-- &nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/> --%> 
	</form:form>
	<tags:message content="${message}"/>
	<table class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;">月份</th>
				<th style="text-align: center;">自然</th>
				<th style="text-align: center;">营销</th>
				<th style="text-align: center;">比例</th>
			</tr>
		</thead>
		<tbody>
		    <c:forEach items='${marketingMap}' var='marketing'>
		         <tr>
					<td style="text-align: center;vertical-align: middle;">${marketing.key}</td>
					<td style="text-align: center;vertical-align: middle;">${marketingMap[marketing.key].realOrder}</td>
					<td style="text-align: center;vertical-align: middle;">${marketingMap[marketing.key].amsOrder}</td>
					<td style="text-align: center;vertical-align: middle;">
					   <c:if test='${marketingMap[marketing.key].amsOrder>0}'>
					        <fmt:formatNumber value="${marketingMap[marketing.key].realOrder/marketingMap[marketing.key].amsOrder}" maxFractionDigits="2" pattern="#0.00"/>
					   </c:if>
					</td>
			   </tr>
		    </c:forEach>
		</tbody>
	</table>
	<br/>
	
	<div style="width: 100%;float:left;border:1px solid #ccc">
		<div id="marketing1" style="float:left;width:49%;height:350px"></div>
		<div id="marketing2" style="float:left;width:49%;height:350px"></div>
	</div>
	
	<br/><br/>
	<table class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;" colspan="12">${saleProfit.line}线月闪促统计</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<c:forEach var="lightDeals" items="${lightDealsMap}">
					<td style="text-align: center;vertical-align: middle;">${lightDeals.key }</td>
				</c:forEach>
			</tr>
			<tr>
				<c:forEach var="lightDeals" items="${lightDealsMap}">
					<td style="text-align: center;vertical-align: middle;">
						 <a id='${lightDeals.key}dealsDiv'>${lightDealsMap[lightDeals.key]['total']}</a>
						 <div id="${lightDeals.key}deals" style="display: none">
						     <table id="${lightDeals.key}detailTable"  class="desc table table-striped table-bordered table-condensed">
						         <thead>
									<tr>
										<th style="text-align: center;">产品</th>
										<th style="text-align: center;">次数</th>
									</tr>
								</thead>
							     <tbody>
							        <c:forEach var="deals" items="${lightDealsMap[lightDeals.key]}">
							          
							            <c:if test="${'total' ne deals.key }">
							               <tr>
							                <td style="text-align: center;vertical-align: middle;">${deals.key }</td>
							                <td style="text-align: center;vertical-align: middle;">${lightDealsMap[lightDeals.key][deals.key] }</td>
							               </tr>
							            </c:if>
							        </c:forEach>	
								</tbody>
		                     </table>
						 </div>
					</td>
				</c:forEach>
			</tr>
		</tbody>
	</table>
	
	<br/>
	<table class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;" colspan="12">${saleProfit.line}线广告产品统计</th>
			</tr>
			<tr>
			    <th style="text-align: center;">月份</th>
				<c:forEach var="adstype" items="${adsTypeMap}">
					<th style="text-align: center;vertical-align: middle;">${adstype.key }</th>
				</c:forEach>
			</tr>
			
		</thead>
		<tbody>
				      <tr>
				         <td style="text-align: center;">总产品数</td>
				         <c:forEach var="adstype" items="${adsTypeMap}">
				              <td style="text-align: center;vertical-align: middle;">
				                  <a id='${adstype.key}alladsDiv'>${fn:length(adsTypeMap[adstype.key]['1'])}</a>
							         <div id="${adstype.key}allads" style="display: none">
										     <table id="${adstype.key}alladsTable"  class="desc table table-striped table-bordered table-condensed">
										         <thead>
													<tr>
														<th style="text-align: center;">产品</th>
														<th style="text-align: center;">SPA平均点击数</th>
														<th style="text-align: center;">AMS平均点击数</th>
													</tr>
												</thead>
											     <tbody>
											        <c:forEach var="ads" items="${adsTypeMap[adstype.key]['1']}">
											                 <tr>
													              <td style="text-align: center;vertical-align: middle;">${ads}</td>
													              <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value='${adsMap[adstype.key][ads][4]}' maxFractionDigits='2' /></td>
													              <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value='${adsMap[adstype.key][ads][5]}' maxFractionDigits='2' /></td>
											                 </tr>
											          
											        </c:forEach>	
												</tbody>
						                     </table>
									</div>
									
				                  
				              </td>
				         </c:forEach>
				      </tr>
				      
				        <tr>
				         <td style="text-align: center;">有SPA,无AMS数</td>
				         <c:forEach var="adstype" items="${adsTypeMap}">
				              <td style="text-align: center;vertical-align: middle;">${fn:length(adsTypeMap[adstype.key]['2'])}</td>
				         </c:forEach>
				      </tr>
				      
				      <tr>
				         <td style="text-align: center;">有AMS,无SPA数</td>
				         <c:forEach var="adstype" items="${adsTypeMap}">
				              <td style="text-align: center;vertical-align: middle;">${fn:length(adsTypeMap[adstype.key]['3'])}</td>
				         </c:forEach>
				      </tr>
				      
				        <tr>
				        <td style="text-align: center;">有AMS和SPA数</td>
				         <c:forEach var="adstype" items="${adsTypeMap}">
				              <td style="text-align: center;vertical-align: middle;">
				                     <a id='${adstype.key}adsDiv'>${fn:length(adsTypeMap[adstype.key]['4'])}</a>
							         <div id="${adstype.key}ads" style="display: none">
										     <table id="${adstype.key}adsTable"  class="desc table table-striped table-bordered table-condensed">
										         <thead>
													<tr>
														<th style="text-align: center;">产品</th>
														<th style="text-align: center;">SPA平均点击数</th>
														<th style="text-align: center;">AMS平均点击数</th>
													</tr>
												</thead>
											     <tbody>
											        <c:forEach var="ads" items="${adsTypeMap[adstype.key]['4']}">
											                 <tr>
													              <td style="text-align: center;vertical-align: middle;">${ads}</td>
													              <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value='${adsMap[adstype.key][ads][4]}' maxFractionDigits='2' /></td>
													              <td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value='${adsMap[adstype.key][ads][5]}' maxFractionDigits='2' /></td>
											                 </tr>
											          
											        </c:forEach>	
												</tbody>
						                     </table>
									</div>
				              </td>
				         </c:forEach>
				      </tr>
		</tbody>
	</table>
	
	
	<br/>
	
	<c:if test="${!fn:contains('total,noUs,eu,en,nonEn',saleProfit.country) }">
	   <div style="width: 100%;float:left;border:1px solid #ccc">
		  <div id="rankChart" style="float:left;width:98%;height:350px"></div>
	   </div>
	</c:if>
	
	<br/>
	  <div style="width: 100%;float:left;border:1px solid #ccc">
		  <div id="bestSellerChart" style="float:left;width:98%;height:350px"></div>
	   </div>
	
	
</body>
</html>
