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
			} );
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
			var positionPieChart,positionPieChart1,positionPieChart2,newProductBarChart,hotProductBarChart,eliminatedProductBarChart;
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
		    		positionPieChart = ec.init(document.getElementById('positionPie'));
		    		positionPieChart1 = ec.init(document.getElementById('positionPie1'));
		    		positionPieChart2 = ec.init(document.getElementById('positionPie2'));
		    		newProductBarChart = ec.init(document.getElementById('newProductBar'));
		    		hotProductBarChart = ec.init(document.getElementById('hotProductBar'));
		    		eliminatedProductBarChart = ec.init(document.getElementById('eliminatedProductBar'));
			    	
			    	var positionPieOption = {
		            	title:{text:'${currMonth}分产品结构销售额占比',x:'center',y:10},
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
		            	      			name:'占比',
		            	      			center: ['50%', '60%'],
			                            radius : '50%',
			                            type:'pie',
			                            data:[<c:forEach items="${typeDataXAxis}" var="type" varStatus="i">
			                            {value:'${empty typeTotalData[currMonth][type].sales?0:typeTotalData[currMonth][type].sales}',name:'${type}'}${i.last?'':','}
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
		    		positionPieChart.setOption(positionPieOption);
			    	
			    	var positionPieOption1 = {
		            	title:{text:'${lastYearMonth}分产品结构销售额占比',x:'center',y:10},
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
			                            {value:'${empty typeTotalData[lastYearMonth][type].sales?0:typeTotalData[lastYearMonth][type].sales}',name:'${type}'}${i.last?'':','}
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
			    	positionPieChart1.setOption(positionPieOption1);
			    	
			    	var positionPieOption2 = {
		            	title:{text:'${currMonth}分产品结构销售额占比',x:'center',y:10},
		            	tooltip : {
		                    trigger: 'item',
		                    formatter: "{a} <br/>{b} : {c} ({d}%)"
		                },
		            	legend: {
		                	y:35,
		                	data:[<c:forEach items="${positionDataXAxis}" var="type" varStatus="i">
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
			                            data:[<c:forEach items="${positionDataXAxis}" var="type" varStatus="i">
			                            {value:'${empty totalData[currMonth][type].sales?0:totalData[currMonth][type].sales}',name:'${type}'}${i.last?'':','}
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
			    	positionPieChart2.setOption(positionPieOption2);
			    	
			    	var newProductBarOption = {
		            	title:{text:'${saleProfit.line}线${currMonth}月新品环比销售额明细',x:'center'},
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
		            	        data: [<c:forEach items="${newXAxis}" var="productName" varStatus="i">'${fns:getModelAndColor(productName)}'${i.last?'':','}</c:forEach>],
		            	    },
			            grid: { // 控制图的大小，调整下面这些值就可以，
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
		            	        		data:[<c:forEach items="${newXAxis}" var="productName" varStatus="i"><fmt:formatNumber pattern="#######.##" value="${tableData[currMonth][productName].sales}"  maxFractionDigits="2"/>${i.last?'':','}</c:forEach>],
		            	        		type: 'bar'
		            	      		},
		            	      		{
		            	      			name:'${lastMonth}',
		            	        		data:[<c:forEach items="${newXAxis}" var="productName" varStatus="i"><fmt:formatNumber pattern="#######.##" value="${empty tableData[lastMonth][productName].sales?0:tableData[lastMonth][productName].sales}"  maxFractionDigits="2"/>${i.last?'':','}</c:forEach>],
		            	        		type: 'bar'
		            	      		}
		            	    ]
		            };
			    	newProductBarChart.setOption(newProductBarOption);
			    	
			    	var hotProductBarOption = {
		            	title:{text:'${saleProfit.line}线${currMonth}月爆款环比销售额明细',x:'center'},
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
		            	        data: [<c:forEach items="${hotXAxis}" var="productName" varStatus="i">'${fns:getModelAndColor(productName)}'${i.last?'':','}</c:forEach>],
		            	    },
			            grid: { // 控制图的大小，调整下面这些值就可以，
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
		            	        		data:[<c:forEach items="${hotXAxis}" var="productName" varStatus="i"><fmt:formatNumber pattern="#######.##" value="${tableData[currMonth][productName].sales}"  maxFractionDigits="2"/>${i.last?'':','}</c:forEach>],
		            	        		type: 'bar'
		            	      		},
		            	      		{
		            	      			name:'${lastMonth}',
		            	        		data:[<c:forEach items="${hotXAxis}" var="productName" varStatus="i"><fmt:formatNumber pattern="#######.##" value="${empty tableData[lastMonth][productName].sales?0:tableData[lastMonth][productName].sales}"  maxFractionDigits="2"/>${i.last?'':','}</c:forEach>],
		            	        		type: 'bar'
		            	      		}
		            	    ]
		            };
			    	hotProductBarChart.setOption(hotProductBarOption);
			    	
			    	var eliminatedProductBarOption = {
		            	title:{text:'${saleProfit.line}线${currMonth}月淘汰品环比销售额明细',x:'center'},
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
		            	        data: [<c:forEach items="${eliminatedXAxis}" var="productName" varStatus="i">'${fns:getModelAndColor(productName)}'${i.last?'':','}</c:forEach>],
		            	    },
			            grid: { // 控制图的大小，调整下面这些值就可以，
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
		            	        		data:[<c:forEach items="${eliminatedXAxis}" var="productName" varStatus="i"><fmt:formatNumber pattern="#######.##" value="${tableData[currMonth][productName].sales}"  maxFractionDigits="2"/>${i.last?'':','}</c:forEach>],
		            	        		type: 'bar'
		            	      		},
		            	      		{
		            	      			name:'${lastMonth}',
		            	        		data:[<c:forEach items="${eliminatedXAxis}" var="productName" varStatus="i"><fmt:formatNumber pattern="#######.##" value="${empty tableData[lastMonth][productName].sales?0:tableData[lastMonth][productName].sales}"  maxFractionDigits="2"/>${i.last?'':','}</c:forEach>],
		            	        		type: 'bar'
		            	      		}
		            	    ]
		            };
			    	eliminatedProductBarChart.setOption(eliminatedProductBarOption);
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
	<br/>
	<div style="width: 100%;float:left;border:1px solid #ccc">
		<div id="positionPie" style="float:left;width:49%;height:350px"></div><%--分结构售额占比 --%>
		<div id="positionPie1" style="float:left;width:49%;height:350px"></div><%--同比分结构售额占比 --%>
	</div>
	<br/>
	<div style="width: 100%;float:left;border:1px solid #ccc;margin-top:10px;">
		<div id="positionPie2" style="float:left;width:49%;height:450px;"></div><%--分结构售额占比 --%>
		<div style="float:left;width:49%;height:450px;overflow:auto;margin-top:5px;"><%--产品明细 --%>
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th style="text-align: center;" colspan="12">${currMonth }月${saleProfit.line }线各产品销售额明细</th>
					</tr>
					<tr>
						<td style="text-align: center;vertical-align: middle;">产品(${currMonth }产品定位)</td>
						<td style="text-align: center;vertical-align: middle;">${currMonth }销售额</td>
						<td style="text-align: center;vertical-align: middle;">${lastMonth }销售额</td>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="productMap" items="${tableData[currMonth] }">
						<c:set var="productName" value="${productMap.key}"/>
						<c:set var="positionStr" value="${tableData[currMonth][productName].productAttr}"/>
						<c:if test="${'普通' eq positionStr || empty positionStr }">
							<c:set var="positionStr" value="other"/>
						</c:if>
						<tr>
							<td style="text-align: center;vertical-align: middle;">
								<a target="_blank" href="${ctx }/psi/psiInventory/productInfoDetail?productName=${productName}">${fns:getModelAndColor(productName) }</a>
								(${positionStr })
							</td>
							<td style="text-align: center;vertical-align: middle;">
								<fmt:formatNumber pattern="#######.##" value="${tableData[currMonth][productName].sales }"  maxFractionDigits="2"/>
							</td>
							<td style="text-align: center;vertical-align: middle;">
								<fmt:formatNumber pattern="#######.##" value="${tableData[lastMonth][productName].sales }"  maxFractionDigits="2"/>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	<br/>
	<div style="width: 100%;float:left;border:1px solid #ccc;margin-top:10px;">
		<div id="newProductBar" style="height:400px"></div><%--新品销售额明细 --%>
	</div>
	<br/>
	<div style="width: 100%;float:left;border:1px solid #ccc;margin-top:10px;">
		<div id="hotProductBar" style="height:400px"></div><%--爆款销售额明细 --%>
	</div>
	<br/>
	<div style="width: 100%;float:left;border:1px solid #ccc;margin-top:10px;">
		<div id="eliminatedProductBar" style="height:400px"></div><%--淘汰品销售额明细 --%>
	</div>
	
</body>
</html>
