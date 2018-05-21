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
			
			//ecahrts-----------------
			var countrySaleChart,countrySaleChart1,countryPieChart,countryPieChart1,monthLineChart,monthBarChart;
			require.config({
		        paths:{ 
		            echarts:'${ctxStatic}/echarts/js/echarts',
		            'echarts/chart/line': '${ctxStatic}/echarts/js/echarts',
		            'echarts/chart/pie': '${ctxStatic}/echarts/js/echarts',
		            'echarts/chart/bar': '${ctxStatic}/echarts/js/echarts'
		        }
		    });
		    require(
		    	['echarts','echarts/chart/line','echarts/chart/bar','echarts/chart/pie'],
		        function(ec) {
		        	monthLineChart = ec.init(document.getElementById('monthLine'));
		        	monthBarChart = ec.init(document.getElementById('monthBar'));
		        	<c:if test="${not empty countryData}">
		        	countrySaleChart = ec.init(document.getElementById('countrySale'));
		        	countrySaleChart1 = ec.init(document.getElementById('countrySale1'));
		        	countryPieChart = ec.init(document.getElementById('countryPie'));
		        	countryPieChart1 = ec.init(document.getElementById('countryPie1'));
		        	var countrySaleOption = {
		            	title:{text:'${saleProfit.line}线${currMonth}月各市场销售额&目标',x:'center'},
		            	tooltip : {
		                    trigger: 'item'
		                },
		            	legend: {
		                	y:30,
		                    data:['销售额','目标']
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
		            	xAxis: {
		            	        type: 'category',
		            	        data: [<c:forEach items="${countryDataXAxis}" var="country" varStatus="i">'${country}'${i.last?'':','}</c:forEach>],
		            	    },
		            	yAxis: [
		            	        {
		            	        	type: 'value',
			                        splitArea : {show : true},
			                        axisLabel : {
			                        	formatter: '{value}(${currencySymbol })'
			                        },
			                        boundaryGap:[0,0.5]
		            	        }
		            	    ],
		            	series: [
		            	      		{
		            	      			name:'销售额',
		            	        		data:[<c:forEach items="${countryDataXAxis}" var="country" varStatus="i"><fmt:formatNumber pattern="#######" value="${countryData[currMonth][country].sales}"  maxFractionDigits="0"/>${i.last?'':','}</c:forEach>],
		            	        		type: 'bar'
		            	      		},
		            	      		{
		            	      			name : '目标',	
		            	        		data:[<c:forEach items="${countryDataXAxis}" var="country" varStatus="i"><fmt:formatNumber pattern="#######" value="${countryGoal[country].goal>0?countryGoal[country].goal:0}"  maxFractionDigits="0"/>${i.last?'':','}</c:forEach>],
		            	        		type: 'bar'
		            	      		}
		            	    ]
		            };
			    	countrySaleChart.setOption(countrySaleOption);
			    	
			    	var countrySaleOption1 = {
		            	title:{text:'${saleProfit.line}线${currMonth}月各市场同比增长率',x:'center'},
		            	tooltip : {
		                    trigger: 'item'
		                },
		            	legend: {
		                	y:30,
		                    data:['同比增长率(%)']
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
		            	xAxis: {
		            	        type: 'category',
		            	        data: [<c:forEach items="${countryDataXAxis}" var="country" varStatus="i">'${country}'${i.last?'':','}</c:forEach>],
		            	},
		            	yAxis: [
		            	        {
		            	        	type: 'value',
			                        splitArea : {show : true},
			                        axisLabel : {
			                        	formatter: '{value}%'
			                        },
			                        boundaryGap:[0,0.5]
		            	        }
		            	    ],
		            	series: [
		            	      		{
		            	      			name:'同比增长率(%)',
		            	        		data:[<c:forEach items="${countryDataXAxis}" var="country" varStatus="i">
		            	        		<fmt:formatNumber pattern="#######.##" value="${countryData[lastYearMonth][country].sales>0?(countryData[currMonth][country].sales-countryData[lastYearMonth][country].sales)*100/countryData[lastYearMonth][country].sales:0}"  maxFractionDigits="2"/>${i.last?'':','}
		            	        		</c:forEach>],
		            	        		type: 'bar',
		            	        		formatter: function(params) {
		            	        			if (params.value > 0) {
		            	        				return params.value;
		            	        				} else {
		            	        					return '';
		            	        				}
		            	        		}
		            	      		}
		            	    ]
		            };
			    	countrySaleChart1.setOption(countrySaleOption1);
			    	
			    	var countryPieOption = {
			            	title:{text:'${currMonth}各市场占比',x:'center',y:10},
			            	tooltip : {
			                    trigger: 'item',
			                    formatter: "{a} <br/>{b} : {c} ({d}%)"
			                },
			            	legend: {
			                	y:35,
			                	data:[<c:forEach items="${countryDataXAxis}" var="country" varStatus="i">
			            		'${country}'${i.last?'':','}
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
			            	      			name:'市场占比',
			            	      			center: ['50%', '60%'],
				                            radius : '50%',
				                            selectedOffset: 200, 
				                            type:'pie',
				                            data:[<c:forEach items="${countryDataXAxis}" var="country" varStatus="i">
				                            {value:'${countryData[currMonth][country].sales}',name:'${country}'}${i.last?'':','}
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
				    	countryPieChart.setOption(countryPieOption);
				    	
				    	var countryPieOption1 = {
			            	title:{text:'${lastYearMonth}各市场占比',x:'center',y:10},
			            	tooltip : {
			                    trigger: 'item',
			                    formatter: "{a} <br/>{b} : {c} ({d}%)"
			                },
			            	legend: {
			                	y:35,
			                	data:[<c:forEach items="${countryDataXAxis}" var="country" varStatus="i">
			            		'${country}'${i.last?'':','}
			            		</c:forEach>]
			                },
			                toolbox: {
			                    show : true,
			                    feature : {
			                        mark : false,
			                        dataView : true,
			                        restore : {show:true},
			                        saveAsImage : {show:true}
			                    }
			                },
			            	series: [
			            	      		{
			            	      			name:'市场占比',
				                            center: ['50%', '60%'],
				                            radius : '50%',
				                            selectedOffset: 50, 
				                            type:'pie',
				                            data:[<c:forEach items="${countryDataXAxis}" var="country" varStatus="i">
				                            {value:'${countryData[lastYearMonth][country].sales}',name:'${country}'}${i.last?'':','}
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
				    countryPieChart1.setOption(countryPieOption1);
				    </c:if>
				    
		        	var monthLineOption = {
		            	title:{text:'${saleProfit.line}线分月销售额',x:'center'},
		            	tooltip : {
		                    trigger: 'item'
		                },
		            	legend: {
		                	y:30,
		                    data:['${currYear}','${lastYear}']
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
		            	xAxis: {
			            		axisLabel: {
									rotate: 40
								},
		            	        type: 'category',
		            	        data: [<c:forEach items="${monthXAxis}" var="month" varStatus="i">'${month}月'${i.last?'':','}</c:forEach>],
		            	    },
		            	yAxis: [
		            	        {
		            	        	type: 'value',
			                        splitArea : {show : true},
			                        axisLabel : {
			                        	formatter: '{value}(${currencySymbol })'
			                        },
			                        boundaryGap:[0,0.5]
		            	        }
		            	    ],
		            	series: [
		            	      		{
		            	      			name:'${currYear}',
		            	        		data:[<c:forEach items="${monthXAxis}" var="month" varStatus="i">
		            	        		<c:set var="dataMonth" value="${currYear}${month }"/>
		        						<c:if test="${month<10 }">
		        							<c:set var="dataMonth" value="${currYear}0${month }"/>
		        						</c:if>
		            	        		<fmt:formatNumber pattern="#######" value="${lineMonthData[dataMonth].sales}"  maxFractionDigits="0"/>${i.last?'':','}
		            	        		</c:forEach>],
		            	        		type: 'line'
		            	      		},
		            	      		{
		            	      			name : '${lastYear}',
		            	        		data:[<c:forEach items="${monthXAxis}" var="month" varStatus="i">
		            	        		<c:set var="dataMonth" value="${lastYear}${month }"/>
		        						<c:if test="${month<10 }">
		        							<c:set var="dataMonth" value="${lastYear}0${month }"/>
		        						</c:if>
		            	        		<fmt:formatNumber pattern="#######" value="${lineMonthData[dataMonth].sales}"  maxFractionDigits="0"/>${i.last?'':','}
		            	        		</c:forEach>],
		            	        		type: 'line'
		            	      		}
		            	    ]
		            };
		        	monthLineChart.setOption(monthLineOption);
				    
		        	var monthBarOption = {
		            	title:{text:'${saleProfit.line}线分月同比增长率',x:'center'},
		            	tooltip : {
		                    trigger: 'item'
		                },
		            	legend: {
		                	y:30,
		                    data:['同比增长率(%)']
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
		            	xAxis: {
			            		axisLabel: {
									rotate: 40
								},
		            	        type: 'category',
		            	        data: [<c:forEach items="${monthXAxis}" var="month" varStatus="i">'${month}月'${i.last?'':','}</c:forEach>],
		            	    },
		            	yAxis: [
		            	        {
		            	        	type: 'value',
			                        splitArea : {show : true},
			                        axisLabel : {
			                        	formatter: '{value}%'
			                        },
			                        boundaryGap:[0,0.5]
		            	        }
		            	    ],
		            	series: [
		            	      		{
		            	      			name:'同比增长率(%)',
		            	        		data:[<c:forEach items="${monthXAxis}" var="month" varStatus="i">
		            	        		<c:set var="currMonth" value="${currYear}${month }"/>
		            	        		<c:set var="dataMonth" value="${lastYear}${month }"/>
		            	        		<c:set var="rate" value="0"/>
		        						<c:if test="${month<10 }">
	        								<c:set var="currMonth" value="${currYear}0${month }"/>
		        							<c:set var="dataMonth" value="${lastYear}0${month }"/>
		        						</c:if>
		        						<c:if test="${lineMonthData[dataMonth].sales>0 }">
		        							<c:set var="rate" value="${(lineMonthData[currMonth].sales-lineMonthData[dataMonth].sales)/lineMonthData[dataMonth].sales}"/>
		        						</c:if>
		            	        		<fmt:formatNumber pattern="#######.##" value="${rate*100}"  maxFractionDigits="2"/>${i.last?'':','}
		            	        		</c:forEach>],
		            	        		type: 'bar'
		            	      		}
		            	    ]
		            };
		        	monthBarChart.setOption(monthBarOption);
				    
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
		<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyyMM',isShowClear:false,maxDate:new Date(),onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="day" value="<fmt:formatDate value="${currDate}" pattern="yyyyMM"/>" class="input-small" id="day"/>
		<%--&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/> --%>
	</form:form>
	<tags:message content="${message}"/>
	<table class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;" colspan="6">${currMonth }月${saleProfit.line }线销售额统计</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td style="text-align: center;vertical-align: middle;" rowspan="3">销售额目标(${currencySymbol })</td>
				<td style="text-align: center;vertical-align: middle;" rowspan="3">
					<fmt:formatNumber value="${goal }" maxFractionDigits="0" />
				</td>
				<td style="text-align: center;vertical-align: middle;">${currMonth }销售额(${currencySymbol })</td>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber value="${data[currMonth].sales }" maxFractionDigits="0" />
				</td>
				<td style="text-align: center;vertical-align: middle;">达成率</td>
				<td style="text-align: center;vertical-align: middle;">
					<c:if test="${goal > 0}">
						<fmt:formatNumber value="${data[currMonth].sales*100/goal}" maxFractionDigits="2" />%
					</c:if>
				</td>
			</tr>
			
			<tr>
				<td style="text-align: center;vertical-align: middle;">${lastYearMonth }销售额(${currencySymbol })</td>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber value="${data[lastYearMonth].sales }" maxFractionDigits="0" />
				</td>
				<td style="text-align: center;vertical-align: middle;">同比增长</td>
				<td style="text-align: center;vertical-align: middle;">
					<c:if test="${data[lastYearMonth].sales > 0}">
						<fmt:formatNumber value="${(data[currMonth].sales-data[lastYearMonth].sales)*100/data[lastYearMonth].sales}" maxFractionDigits="2" />%
					</c:if>
				</td>
			</tr>
			
			<tr>
				<td style="text-align: center;vertical-align: middle;">${lastMonth }销售额(${currencySymbol })</td>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber value="${data[lastMonth].sales }" maxFractionDigits="0" />
				</td>
				<td style="text-align: center;vertical-align: middle;">环比增长</td>
				<td style="text-align: center;vertical-align: middle;">
					<c:if test="${data[lastMonth].sales > 0}">
						<fmt:formatNumber value="${(data[currMonth].sales-data[lastMonth].sales)*100/data[lastMonth].sales}" maxFractionDigits="2" />%
					</c:if>
				</td>
			</tr>
		</tbody>
	</table>
	<%--分市场销售额统计分析 --%>
	<br/>
	<c:if test="${not empty countryData }">
		<div style="width: 100%;float:left;border:1px solid #ccc">
			<div id="countrySale" style="float:left;width:49%;height:350px"></div><%--分市场销售额目标 --%>
			<div id="countrySale1" style="float:left;width:49%;height:350px"></div><%--分市场销售额目标 --%>
		</div>
		<table class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th style="text-align: center;" colspan="12">${currMonth }月${saleProfit.line }线各市场销售额统计</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td style="text-align: center;vertical-align: middle;"></td>
					<c:forEach var="country" items="${countryDataXAxis }">
						<td style="text-align: center;vertical-align: middle;">${country }</td>
					</c:forEach>
					<td style="text-align: center;vertical-align: middle;">
						合计
					</td>
				</tr>
				<c:set var="lastYearMonthTotal" value="0"/>
				<tr>
					<td style="text-align: center;vertical-align: middle;">${lastYearMonth }月</td>
					<c:forEach var="country" items="${countryDataXAxis }">
						<c:set var="lastYearMonthTotal" value="${lastYearMonthTotal+countryData[lastYearMonth][country].sales }"/>
						<td style="text-align: center;vertical-align: middle;">
							<fmt:formatNumber pattern="#######" value="${countryData[lastYearMonth][country].sales }"  maxFractionDigits="0"/>
						</td>
					</c:forEach>
					<td style="text-align: center;vertical-align: middle;">
						<fmt:formatNumber pattern="#######" value="${lastYearMonthTotal }"  maxFractionDigits="0"/>
					</td>
				</tr>
				<c:set var="goalTotal" value="0"/>
				<tr>
					<td style="text-align: center;vertical-align: middle;">${currMonth }月目标</td>
					<c:forEach var="country" items="${countryDataXAxis }">
						<c:set var="goalTotal" value="${goalTotal+countryGoal[country].goal }"/>
						<td style="text-align: center;vertical-align: middle;">
							<fmt:formatNumber pattern="#######" value="${countryGoal[country].goal }"  maxFractionDigits="0"/>
						</td>
					</c:forEach>
					<td style="text-align: center;vertical-align: middle;">
						<fmt:formatNumber pattern="#######" value="${goalTotal }"  maxFractionDigits="0"/>
					</td>
				</tr>
				<c:set var="monthTotal" value="0"/>
				<tr>
					<td style="text-align: center;vertical-align: middle;">${currMonth }月</td>
					<c:forEach var="country" items="${countryDataXAxis }">
						<c:set var="monthTotal" value="${monthTotal+countryData[currMonth][country].sales }"/>
						<td style="text-align: center;vertical-align: middle;">
							<fmt:formatNumber pattern="#######" value="${countryData[currMonth][country].sales }"  maxFractionDigits="0"/>
						</td>
					</c:forEach>
					<td style="text-align: center;vertical-align: middle;">
						<fmt:formatNumber pattern="#######" value="${monthTotal }"  maxFractionDigits="0"/>
					</td>
				</tr>
				<tr>
					<td style="text-align: center;vertical-align: middle;">同比增长</td>
					<c:forEach var="country" items="${countryDataXAxis }">
						<td style="text-align: center;vertical-align: middle;color:${countryData[currMonth][country].sales>countryData[lastYearMonth][country].sales?'red':'#08c'}">
							<c:if test="${countryData[lastYearMonth][country].sales >0}">
								<fmt:formatNumber type="percent" value="${(countryData[currMonth][country].sales-countryData[lastYearMonth][country].sales)/countryData[lastYearMonth][country].sales }"  maxFractionDigits="2"/>
							</c:if>
						</td>
					</c:forEach>
					<td style="text-align: center;vertical-align: middle;">
						<fmt:formatNumber type="percent" value="${(monthTotal-lastYearMonthTotal)/lastYearMonthTotal }"  maxFractionDigits="2"/>
					</td>
				</tr>
				<tr>
					<td style="text-align: center;vertical-align: middle;">完成率</td>
					<c:forEach var="country" items="${countryDataXAxis }">
						<td style="text-align: center;vertical-align: middle;color:${countryData[currMonth][country].sales>countryGoal[country].goal?'red':'#08c'}">
							<c:if test="${countryGoal[country].goal >0}">
								<fmt:formatNumber type="percent" value="${countryData[currMonth][country].sales/countryGoal[country].goal }"  maxFractionDigits="2"/>
							</c:if>
						</td>
					</c:forEach>
					<td style="text-align: center;vertical-align: middle;">
						<fmt:formatNumber type="percent" value="${monthTotal/goalTotal }"  maxFractionDigits="2"/>
					</td>
				</tr>
				<tr>
					<td style="text-align: center;vertical-align: middle;">目标差额</td>
					<c:forEach var="country" items="${countryDataXAxis }">
						<td style="text-align: center;vertical-align: middle;">
							<fmt:formatNumber type="number" value="${countryData[currMonth][country].sales-countryGoal[country].goal }"  maxFractionDigits="0"/>
						</td>
					</c:forEach>
					<td style="text-align: center;vertical-align: middle;">
						<fmt:formatNumber type="number" value="${monthTotal-goalTotal }"  maxFractionDigits="0"/>
					</td>
				</tr>
			</tbody>
		</table>
		<br/>
		<div style="width: 100%;float:left;border:1px solid #ccc">
			<div id="countryPie" style="float:left;width:49%;height:350px"></div><%--分市场销售额占比情况 --%>
			<div id="countryPie1" style="float:left;width:49%;height:350px"></div><%--同比分市场销售额占比情况 --%>
		</div>
	</c:if>
	<br/>
	<div style="width: 100%;float:left;border:1px solid #ccc">
		<div id="monthLine" style="float:left;width:49%;height:350px"></div><%--分市场销售额占比情况 --%>
		<div id="monthBar" style="float:left;width:49%;height:350px"></div><%--同比分市场销售额占比情况 --%>
	</div>
	<table class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;" colspan="12">${saleProfit.line }线月度销售额统计</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td style="text-align: center;vertical-align: middle;"></td>
				<c:forEach var="month" items="${monthXAxis }">
					<td style="text-align: center;vertical-align: middle;">${month }月</td>
				</c:forEach>
			</tr>
			<tr>
				<td style="text-align: center;vertical-align: middle;">${lastYear }年</td>
				<c:forEach var="month" items="${monthXAxis }">
					<td style="text-align: center;vertical-align: middle;">
						<c:set var="dataMonth" value="${lastYear}${month }"/>
						<c:if test="${month<10 }">
							<c:set var="dataMonth" value="${lastYear}0${month }"/>
						</c:if>
						<fmt:formatNumber type="number" value="${lineMonthData[dataMonth].sales }"  maxFractionDigits="0"/>
					</td>
				</c:forEach>
			</tr>
			<tr>
				<td style="text-align: center;vertical-align: middle;">${currYear }年</td>
				<c:forEach var="month" items="${monthXAxis }">
					<td style="text-align: center;vertical-align: middle;">
						<c:set var="dataMonth" value="${currYear}${month }"/>
						<c:if test="${month<10 }">
							<c:set var="dataMonth" value="${currYear}0${month }"/>
						</c:if>
						<fmt:formatNumber type="number" value="${lineMonthData[dataMonth].sales }"  maxFractionDigits="0"/>
					</td>
				</c:forEach>
			</tr>
		</tbody>
	</table>
	<br/>
</body>
</html>
