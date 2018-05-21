<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
<title>亚马逊产品线运营分析报告</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<%@include file="/WEB-INF/views/include/datatables.jsp"%>
<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
<style type="text/css">
	.sort{color:#0663A2;cursor:pointer;}
	.blue{color:#8A2BE2;}
	.spanexr {
		float: right;
		min-height: 40px
	}
	
	.spanexl {
		float: left;
	 }
	.footer {
	    padding: 20px 0;
	    margin-top: 20px;
	    border-top: 1px solid #e5e5e5;
	    background-color: #f5f5f5;
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
		
		$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn ) {
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				return $('td:eq('+iColumn+')', tr).text().replace("%","");
			});
		};

		$.fn.dataTableExt.afnSortData['dom-html1'] = function ( oSettings, iColumn ) {
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				return $('td:eq('+iColumn+')', tr).text().replace(",","");
			});
		};
		$(document).ready(function() {

			$(".countryHref").click(function(){
				$("#country").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			//ecahrts-----------------
			var countryProfitChart,countryPieChart,countryPieChart1,typeProfitChart,typePieChart,typePieChart1,hbLineChart,newHbProfitChart;
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
		        	typeProfitChart = ec.init(document.getElementById('typeProfit'));
		        	typePieChart = ec.init(document.getElementById('typePie'));
		        	typePieChart1 = ec.init(document.getElementById('typePie1'));
		    		hbLineChart = ec.init(document.getElementById('hbLine'));
		    		newHbProfitChart = ec.init(document.getElementById('newHbProfit'));
		        	<c:if test="${not empty countryData}">
		        	countryProfitChart = ec.init(document.getElementById('countryProfit'));
		        	countryPieChart = ec.init(document.getElementById('countryPie'));
		        	countryPieChart1 = ec.init(document.getElementById('countryPie1'));
		        	var countryProfitOption = {
		            	title:{text:'${saleProfit.line}线${currMonth}月各市场毛利润',x:'center'},
		            	tooltip : {
		                    trigger: 'item'
		                },
		            	legend: {
		                	y:30,
		                    data:['${currMonth}毛利润','${lastYearMonth}毛利润']
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
		            	      			name:'${currMonth}毛利润',
		            	        		data:[<c:forEach items="${countryDataXAxis}" var="country" varStatus="i"><fmt:formatNumber pattern="#######.##" value="${countryData[currMonth][country].profits}"  maxFractionDigits="2"/>${i.last?'':','}</c:forEach>],
		            	        		type: 'bar'
		            	      		},
		            	      		{
		            	      			name:'${lastYearMonth}毛利润',
		            	        		data:[<c:forEach items="${countryDataXAxis}" var="country" varStatus="i"><fmt:formatNumber pattern="#######.##" value="${empty countryData[lastYearMonth][country].profits?0:countryData[lastYearMonth][country].profits}"  maxFractionDigits="2"/>${i.last?'':','}</c:forEach>],
		            	        		type: 'bar'
		            	      		}
		            	    ]
		            };
		        	countryProfitChart.setOption(countryProfitOption);
			    	
			    	var countryPieOption = {
			            	title:{text:'${saleProfit.line }线${currMonth}月各市场毛利润占比',x:'center',y:10},
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
				                            {value:'${countryData[currMonth][country].profits}',name:'${country}'}${i.last?'':','}
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
			            	title:{text:'${saleProfit.line }线${lastYearMonth}月各市场毛利润占比',x:'center',y:10},
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
				                            {value:'${countryData[lastYearMonth][country].profits}',name:'${country}'}${i.last?'':','}
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
				    
				    var typeProfitOption = {
		            	title:{text:'${saleProfit.line}线${currMonth}月各品类毛利润',x:'center'},
		            	tooltip : {
		                    trigger: 'item'
		                },
		            	legend: {
		            		y:30,
		                    data:['${currMonth}毛利润','${lastYearMonth}毛利润']
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
		            	      			name:'${currMonth}毛利润',
		            	        		data:[<c:forEach items="${typeDataXAxis}" var="type" varStatus="i"><fmt:formatNumber pattern="#######.##" value="${typeData[currMonth][type].profits}"  maxFractionDigits="2"/>${i.last?'':','}</c:forEach>],
		            	        		type: 'bar'
		            	      		},
		            	      		{
		            	      			name:'${lastYearMonth}毛利润',
		            	        		data:[<c:forEach items="${typeDataXAxis}" var="type" varStatus="i"><fmt:formatNumber pattern="#######.##" value="${empty typeData[lastYearMonth][type].profits?0:typeData[lastYearMonth][type].profits}"  maxFractionDigits="2"/>${i.last?'':','}</c:forEach>],
		            	        		type: 'bar'
		            	      		}
		            	    ]
		            };
				    typeProfitChart.setOption(typeProfitOption);
			        
				    var typePieOption = {
			            	title:{text:'${currMonth}各品类利润占比',x:'center',y:10},
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
				                            radius : '40%',
				                            type:'pie',
				                            data:[<c:forEach items="${typeDataXAxis}" var="type" varStatus="i">
				                            {value:'${typeData[currMonth][type].profits}',name:'${type}'}${i.last?'':','}
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
				    	typePieChart.setOption(typePieOption);
				    	
				    	var typePieOption1 = {
			            	title:{text:'${lastYearMonth}各品类利润占比',x:'center',y:10},
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
				                            radius : '40%',
				                            selectedOffset: 50, 
				                            type:'pie',
				                            data:[<c:forEach items="${typeDataXAxis}" var="type" varStatus="i">
				                            {value:'${typeData[lastYearMonth][type].profits}',name:'${type}'}${i.last?'':','}
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
				    typePieChart1.setOption(typePieOption1);
			    	
		        	var hbLineOption = {
		            	title:{text:'${saleProfit.line}线${currMonth}月环比利润增长&下降前10产品',x:'center'},
		            	tooltip : {
		                    trigger: 'item'
		                },
		            	legend: {
		                	y:30,
		                    data:['环比增长']
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
		            	        data: [<c:forEach items="${hbList}" var="profit" varStatus="i">
	            	        	<c:if test="${i.count<10 || i.count>fn:length(hbList)-10}">'${fns:getModelAndColor(profit.productName)}'${i.last?'':','}</c:if>
	            	       		</c:forEach>],
		            	},
		            	grid: { // 控制图的大小
	                        y2: 120,// y2可以控制 X轴跟Zoom控件之间的间隔，避免以为倾斜后造成 label重叠到zoom上
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
		            	      			name:'环比增长',
		            	      			data:[<c:forEach items="${hbList}" var="profit" varStatus="i">
			            	        		<c:if test="${i.count<10 || i.count>fn:length(hbList)-10}">
			            	        			<fmt:formatNumber pattern="#######.##" value="${profit.profits}" maxFractionDigits="2"/>${i.last?'':','}
			            	        		</c:if>
	            	        			</c:forEach>],
		            	        		type: 'bar'
		            	      		}
		            	    ]
		            };
			    	hbLineChart.setOption(hbLineOption);
			    	
		        	var newHbProfitOption = {
		            	title:{text:'${saleProfit.line}线${currMonth}月新品环比毛利润明细',x:'center'},
		            	tooltip : {
		                    trigger: 'item'
		                },
		            	legend: {
		                	y:30,
		                    data:['${currMonth}','${lastMonth}']
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
		            	        data: [<c:forEach items="${newDataXAxis}" var="productName" varStatus="i">'${fns:getModelAndColor(productName)}'${i.last?'':','}</c:forEach>],
		            	},
		            	grid: { // 控制图的大小
	                        y2: 120,// y2可以控制 X轴跟Zoom控件之间的间隔，避免以为倾斜后造成 label重叠到zoom上
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
										data:[<c:forEach items="${newDataXAxis}" var="productName" varStatus="i">
		            	        			<fmt:formatNumber pattern="#######.##" value="${productData[currMonth][productName].profits}"  maxFractionDigits="2"/>${i.last?'':','}
		            	        		</c:forEach>],
										type: 'bar'
									},
									{
										name:'${lastMonth}',
										data:[<c:forEach items="${newDataXAxis}" var="productName" varStatus="i">
		            	        			<fmt:formatNumber pattern="#######.##" value="${empty productData[lastMonth][productName].profits?0:productData[lastMonth][productName].profits}"  maxFractionDigits="2"/>${i.last?'':','}
		            	        		</c:forEach>],
										type: 'bar'
									}
		            	    ]
		            };
		        	newHbProfitChart.setOption(newHbProfitOption);
		        }   	
		    );
		    
		    $("#contentTable").dataTable({
				"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"aoColumns": [
						         null,
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" }
							     ],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				"aaSorting": [[ 1, "desc" ]]
			});
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
	<table class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;" colspan="13">${currMonth }月${saleProfit.line }线利润数据&费用比例明细</th>
			</tr>
		</thead>
		<tbody>
			<tr>
				<td style="text-align: center;vertical-align: middle;" colspan="2">${currMonth }毛利润(${currencySymbol })</td>
				<td style="text-align: center;vertical-align: middle;" colspan="2">
					<fmt:formatNumber value="${totalData[currMonth].profits }" maxFractionDigits="2" />
				</td>
				<td style="text-align: center;vertical-align: middle;" colspan="2">${lastYearMonth }毛利润(${currencySymbol })</td>
				<td style="text-align: center;vertical-align: middle;" colspan="2">
					<fmt:formatNumber value="${totalData[lastYearMonth].profits }" maxFractionDigits="2" />
				</td>
				<td style="text-align: center;vertical-align: middle;" colspan="2">同比增长</td>
				<td style="text-align: center;vertical-align: middle;" colspan="3">
					<fmt:formatNumber type="percent" value="${(totalData[currMonth].profits-totalData[lastYearMonth].profits)/totalData[lastYearMonth].profits}" maxFractionDigits="2" />
				</td>
			</tr>
			<tr>
				<td style="text-align: center;vertical-align: middle;">税率</td>
				<td style="text-align: center;vertical-align: middle;">进口关税</td>
				<td style="text-align: center;vertical-align: middle;">采购成本</td>
				<td style="text-align: center;vertical-align: middle;">亚马逊费用</td>
				<td style="text-align: center;vertical-align: middle;">物流成本</td>
				<td style="text-align: center;vertical-align: middle;">营销费用</td>
				<td style="text-align: center;vertical-align: middle;">替代</td>
				<td style="text-align: center;vertical-align: middle;">退款</td>
				<td style="text-align: center;vertical-align: middle;">销毁召回损失成本</td>
				<td style="text-align: center;vertical-align: middle;">销毁召回费用</td>
				<td style="text-align: center;vertical-align: middle;">月仓储费</td>
				<td style="text-align: center;vertical-align: middle;">长期仓储费</td>
				<td style="text-align: center;vertical-align: middle;">毛利润</td>
			</tr>
			
			<tr>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber type="percent" value="${(totalData[currMonth].sales-totalData[currMonth].salesNoTax)/totalData[currMonth].sales}" maxFractionDigits="2" />
				</td>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber type="percent" value="${totalData[currMonth].tariff/totalData[currMonth].sales }" maxFractionDigits="2" />
				</td>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber type="percent" value="${totalData[currMonth].buyCost/totalData[currMonth].sales }" maxFractionDigits="2" />
				</td>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber type="percent" value="${totalData[currMonth].amazonFee*(-1)/totalData[currMonth].sales }" maxFractionDigits="2" />
				</td>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber type="percent" value="${totalData[currMonth].transportFee/totalData[currMonth].sales }" maxFractionDigits="2" />
				</td>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber type="percent" value="${(totalData[currMonth].adAmsFee+totalData[currMonth].adInEventFee+totalData[currMonth].adInProfitFee)*(-1)/totalData[currMonth].sales }" maxFractionDigits="2" />
				</td>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber type="percent" value="${(totalData[currMonth].supportAmazonFee+totalData[currMonth].supportCost)/totalData[currMonth].sales }" maxFractionDigits="2" />
				</td>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber type="percent" value="${totalData[currMonth].refund*(-1)/totalData[currMonth].sales }" maxFractionDigits="2" />
				</td>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber type="percent" value="${totalData[currMonth].recallCost/totalData[currMonth].sales }" maxFractionDigits="2" />
				</td>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber type="percent" value="${totalData[currMonth].recallFee/totalData[currMonth].sales }" maxFractionDigits="2" />
				</td>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber type="percent" value="${totalData[currMonth].storageFee*(-1)/totalData[currMonth].sales }" maxFractionDigits="2" />
				</td>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber type="percent" value="${(totalData[currMonth].longStorageFee<0?-totalData[currMonth].longStorageFee:totalData[currMonth].longStorageFee)/totalData[currMonth].sales }" maxFractionDigits="2" />
				</td>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber type="percent" value="${totalData[currMonth].profits/totalData[currMonth].sales }" maxFractionDigits="2" />
				</td>
			</tr>
		</tbody>
	</table>
	<%--分市场利润统计分析 --%>
	<br/>
	<c:if test="${not empty countryData }">
		<div style="width: 100%;float:left;border:1px solid #ccc;">
			<div id="countryProfit" style="float:left;width:49%;height:350px"></div><%--分市场利润 --%>
			<div id="countryProfit1" style="float:left;width:49%;height:350px;margin-top:30px">
				<table class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							<th style="text-align: center;" colspan="12">${currMonth }月${saleProfit.line }线各市场毛利润统计</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td style="text-align: center;vertical-align: middle;"></td>
							<td style="text-align: center;vertical-align: middle;">${lastYearMonth }毛利润</td>
							<td style="text-align: center;vertical-align: middle;">${currMonth }毛利润</td>
							<td style="text-align: center;vertical-align: middle;">${currMonth }毛利率</td>
						</tr>
						<c:forEach var="country" items="${countryDataXAxis }">
							<tr>
								<td style="text-align: center;vertical-align: middle;">${country}</td>
								<td style="text-align: center;vertical-align: middle;">
									<fmt:formatNumber pattern="#######.##" value="${countryData[lastYearMonth][country].profits }"  maxFractionDigits="2"/>
								</td>
								<td style="text-align: center;vertical-align: middle;">
									<fmt:formatNumber pattern="#######.##" value="${countryData[currMonth][country].profits }"  maxFractionDigits="2"/>
								</td>
								<td style="text-align: center;vertical-align: middle;">
									<fmt:formatNumber type="percent" value="${countryData[currMonth][country].profitRate }"  maxFractionDigits="2"/>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</div><%--分市场利润 --%>
		</div>
		<br/>
		<div style="width: 100%;float:left;border:1px solid #ccc;margin-top:10px;">
			<div id="countryPie" style="float:left;width:49%;height:350px"></div><%--分市场占比情况 --%>
			<div id="countryPie1" style="float:left;width:49%;height:350px"></div><%--同比分市场占比情况 --%>
		</div>
	</c:if>
	<div style="width: 100%;float:left;border:1px solid #ccc;margin-top:10px;">
		<div id="typeProfit" style="float:left;width:49%;height:450px"></div><%--分品类利润 --%>
		<div id="typeProfit1" style="float:left;width:49%;height:450px;margin-top:30px">
			<table class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th style="text-align: center;" colspan="12">${currMonth }月${saleProfit.line }线各品类毛利润统计</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td style="text-align: center;vertical-align: middle;"></td>
						<td style="text-align: center;vertical-align: middle;">${lastYearMonth }毛利润</td>
						<td style="text-align: center;vertical-align: middle;">${currMonth }毛利润</td>
						<td style="text-align: center;vertical-align: middle;">${currMonth }毛利率</td>
					</tr>
					<c:forEach var="type" items="${typeDataXAxis }">
						<tr>
							<td style="text-align: center;vertical-align: middle;">${type}</td>
							<td style="text-align: center;vertical-align: middle;">
								<fmt:formatNumber pattern="#######.##" value="${typeData[lastYearMonth][type].profits }"  maxFractionDigits="2"/>
							</td>
							<td style="text-align: center;vertical-align: middle;">
								<fmt:formatNumber pattern="#######.##" value="${typeData[currMonth][type].profits }"  maxFractionDigits="2"/>
							</td>
							<td style="text-align: center;vertical-align: middle;">
								<fmt:formatNumber type="percent" value="${typeData[currMonth][type].profitRate }"  maxFractionDigits="2"/>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div><%--分品类利润 --%>
	</div>
	<br/>
	<div style="width: 100%;float:left;border:1px solid #ccc;margin-top:10px;margin-bottom:10px;">
		<div id="typePie" style="float:left;width:48%;height:500px"></div><%--分品类占比情况 --%>
		<div id="typePie1" style="float:left;width:48%;height:500px"></div><%--同比分品类占比情况 --%>
	</div>
	<br/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;" colspan="7">${saleProfit.line }线各产品利润明细</th>
			</tr>
			<tr>
				<th style="text-align: center;vertical-align: middle;">产品名</th>
				<th style="text-align: center;vertical-align: middle;">${currMonth }利润(${currencySymbol })</th>
				<th style="text-align: center;vertical-align: middle;">${currMonth }占比</th>
				<th style="text-align: center;vertical-align: middle;">${lastYearMonth }利润(${currencySymbol })</th>
				<th style="text-align: center;vertical-align: middle;">${lastMonth }利润(${currencySymbol })</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${productDataXAxis }" var="productName">
				<tr>
					<td style="text-align: center;vertical-align: middle;">
						<a target="_blank" href="${ctx }/psi/psiInventory/productInfoDetail?productName=${productName}">${fns:getModelAndColor(productName) }</a>
					</td>
					<td style="text-align: center;vertical-align: middle;">
						<fmt:formatNumber value="${productData[currMonth][productName].profits }" maxFractionDigits="2" />
					</td>
					<td style="text-align: center;vertical-align: middle;">
						<fmt:formatNumber type="percent" value="${productData[currMonth][productName].profits/totalData[currMonth].profits }" maxFractionDigits="2" />
					</td>
					<td style="text-align: center;vertical-align: middle;">
						<fmt:formatNumber value="${productData[lastYearMonth][productName].profits }" maxFractionDigits="2" />
					</td>
					<td style="text-align: center;vertical-align: middle;">
						<fmt:formatNumber value="${productData[lastMonth][productName].profits }" maxFractionDigits="2" />
					</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td style="text-align: center;vertical-align: middle;">总计</td>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber value="${totalData[currMonth].profits }" maxFractionDigits="2" />
				</td>
				<td style="text-align: center;vertical-align: middle;"></td>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber value="${totalData[lastYearMonth].profits }" maxFractionDigits="2" />
				</td>
				<td style="text-align: center;vertical-align: middle;">
					<fmt:formatNumber value="${totalData[lastMonth].profits }" maxFractionDigits="2" />
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
	<div style="width: 100%;float:left;border:1px solid #ccc">
		<div id="hbLine" style="height:500px"></div><%--环比利润增长&下降前10 --%>
	</div>
	<br/>
	<div style="width: 100%;float:left;border:1px solid #ccc;margin-top:10px;">
		<div id="newHbProfit" style="height:500px"></div><%--新品环比利润 --%>
	</div>
</body>
</html>
