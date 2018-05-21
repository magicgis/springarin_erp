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
		        	countrySaleChart = ec.init(document.getElementById('countrySale'));
		        	countrySaleChart1 = ec.init(document.getElementById('countrySale1'));
		        	countryPieChart = ec.init(document.getElementById('countryPie'));
		        	countryPieChart1 = ec.init(document.getElementById('countryPie1'));
		        	var countrySaleOption = {
		            	title:{text:'${saleProfit.line}线各品类销售额',x:'center'},
		            	tooltip : {
		                    trigger: 'item'
		                },
		            	legend: {
		                	y:30,
		                    data:['${currMonth}','${lastYearMonth}']
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
		            	        data: [<c:forEach items="${typeDataXAxis}" var="type" varStatus="i">'${type}'${i.last?'':','}</c:forEach>],
		            	    },
		            	grid: { // 控制图的大小，调整下面这些值就可以，
		                        y2: 100,// y2可以控制 X轴跟Zoom控件之间的间隔，避免以为倾斜后造成 label重叠到zoom上
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
		            	      			name:'${currMonth}',
		            	        		data:[<c:forEach items="${typeDataXAxis}" var="type" varStatus="i"><fmt:formatNumber pattern="#######" value="${typeData[currMonth][type].sales}"  maxFractionDigits="0"/>${i.last?'':','}</c:forEach>],
		            	        		type: 'bar'
		            	      		},
		            	      		{
		            	      			name:'${lastYearMonth}',
		            	        		data:[<c:forEach items="${typeDataXAxis}" var="type" varStatus="i"><fmt:formatNumber pattern="#######" value="${typeData[lastYearMonth][type].sales>0?typeData[lastYearMonth][type].sales:0}"  maxFractionDigits="0"/>${i.last?'':','}</c:forEach>],
		            	        		type: 'bar'
		            	      		}
		            	    ]
		            };
			    	countrySaleChart.setOption(countrySaleOption);
			    	
			    	var countrySaleOption1 = {
		            	title:{text:'${saleProfit.line}线各品类同比增长率',x:'center'},
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
		            	        data: [<c:forEach items="${typeDataXAxis}" var="type" varStatus="i">'${type}'${i.last?'':','}</c:forEach>],
		            	},
		            	grid: { // 控制图的大小
	                        y2: 100,// y2可以控制 X轴跟Zoom控件之间的间隔，避免以为倾斜后造成 label重叠到zoom上
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
		            	        		data:[<c:forEach items="${typeDataXAxis}" var="type" varStatus="i">
		            	        		<fmt:formatNumber pattern="#######.##" value="${typeData[lastYearMonth][type].sales>0?(typeData[currMonth][type].sales-typeData[lastYearMonth][type].sales)*100/typeData[lastYearMonth][type].sales:0}"  maxFractionDigits="2"/>${i.last?'':','}
		            	        		</c:forEach>],
		            	        		type: 'bar'
		            	      		}
		            	    ]
		            };
			    	countrySaleChart1.setOption(countrySaleOption1);
			    	
			    	var countryPieOption = {
			            	title:{text:'${currMonth}各品类占比',x:'center',y:10},
			            	tooltip : {
			                    trigger: 'item',
			                    formatter: "{a} <br/>{b} : {c} ({d}%)"
			                },
			            	legend: {
			                	y:35,
			                	data:[<c:forEach items="${typeDataXAxis}" var="type" varStatus="i">
			            		'${type}'${i.last?'':','}
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
			            	      			name:'品类占比',
			            	      			center: ['50%', '60%'],
				                            radius : '50%',
				                            type:'pie',
				                            data:[<c:forEach items="${typeDataXAxis}" var="type" varStatus="i">
				                            {value:'${typeData[currMonth][type].sales}',name:'${type}'}${i.last?'':','}
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
			            	title:{text:'${lastYearMonth}各品类占比',x:'center',y:10},
			            	tooltip : {
			                    trigger: 'item',
			                    formatter: "{a} <br/>{b} : {c} ({d}%)"
			                },
			            	legend: {
			                	y:35,
			                	data:[<c:forEach items="${typeDataXAxis}" var="type" varStatus="i">
			            		'${type}'${i.last?'':','}
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
			            	      			name:'品类占比',
				                            center: ['50%', '60%'],
				                            radius : '50%',
				                            selectedOffset: 50, 
				                            type:'pie',
				                            data:[<c:forEach items="${typeDataXAxis}" var="type" varStatus="i">
				                            {value:'${typeData[lastYearMonth][type].sales}',name:'${type}'}${i.last?'':','}
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
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
	</form:form>
	<tags:message content="${message}"/>
	<%--分市场销售额统计分析 --%>
	<br/>
	<c:if test="${not empty typeData }">
		<div style="width: 100%;float:left;border:1px solid #ccc">
			<div id="countrySale" style="float:left;width:49%;height:450px"></div><%--分市场销售额目标 --%>
			<div id="countrySale1" style="float:left;width:49%;height:450px"></div><%--分市场销售额目标 --%>
		</div>
		<table class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					<th style="text-align: center;" colspan="12">${currMonth }月${saleProfit.line }线各市场销售额统计(${currencySymbol })</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td style="text-align: center;vertical-align: middle;"></td>
					<c:forEach var="type" items="${typeDataXAxis }">
						<td style="text-align: center;vertical-align: middle;">${type }</td>
					</c:forEach>
					<td style="text-align: center;vertical-align: middle;">
						合计
					</td>
				</tr>
				<c:set var="lastYearMonthTotal" value="0"/>
				<tr>
					<td style="text-align: center;vertical-align: middle;">${lastYearMonth }月</td>
					<c:forEach var="type" items="${typeDataXAxis }">
						<c:set var="lastYearMonthTotal" value="${lastYearMonthTotal+typeData[lastYearMonth][type].sales }"/>
						<td style="text-align: center;vertical-align: middle;">
							<fmt:formatNumber pattern="#######" value="${typeData[lastYearMonth][type].sales }"  maxFractionDigits="0"/>
						</td>
					</c:forEach>
					<td style="text-align: center;vertical-align: middle;">
						<fmt:formatNumber pattern="#######" value="${lastYearMonthTotal }"  maxFractionDigits="0"/>
					</td>
				</tr>
				<c:set var="monthTotal" value="0"/>
				<tr>
					<td style="text-align: center;vertical-align: middle;">${currMonth }月</td>
					<c:forEach var="type" items="${typeDataXAxis }">
						<c:set var="monthTotal" value="${monthTotal+typeData[currMonth][type].sales }"/>
						<td style="text-align: center;vertical-align: middle;">
							<fmt:formatNumber pattern="#######" value="${typeData[currMonth][type].sales }"  maxFractionDigits="0"/>
						</td>
					</c:forEach>
					<td style="text-align: center;vertical-align: middle;">
						<fmt:formatNumber pattern="#######" value="${monthTotal }"  maxFractionDigits="0"/>
					</td>
				</tr>
				<tr>
					<td style="text-align: center;vertical-align: middle;">同比增长</td>
					<c:forEach var="type" items="${typeDataXAxis }">
						<td style="text-align: center;vertical-align: middle;color:${typeData[currMonth][type].sales>typeData[lastYearMonth][type].sales?'red':'#08c'}">
							<c:if test="${typeData[lastYearMonth][type].sales >0}">
								<fmt:formatNumber type="percent" value="${(typeData[currMonth][type].sales-typeData[lastYearMonth][type].sales)/typeData[lastYearMonth][type].sales }"  maxFractionDigits="2"/>
							</c:if>
						</td>
					</c:forEach>
					<td style="text-align: center;vertical-align: middle;">
						<fmt:formatNumber type="percent" value="${(monthTotal-lastYearMonthTotal)/lastYearMonthTotal }"  maxFractionDigits="2"/>
					</td>
				</tr>
			</tbody>
		</table>
		<br/>
		<div style="width: 100%;float:left;border:1px solid #ccc">
			<div id="countryPie" style="float:left;width:48%;height:500px"></div><%--分市场销售额占比情况 --%>
			<div id="countryPie1" style="float:left;width:48%;height:500px"></div><%--同比分市场销售额占比情况 --%>
		</div>
	</c:if>
	<br/>
</body>
</html>
