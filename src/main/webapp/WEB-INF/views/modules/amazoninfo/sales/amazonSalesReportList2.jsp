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
			
			/* <c:choose>
				<c:when test="${saleReport.searchType eq '1'}">
					<c:set var="type" value="日" />
				</c:when>
				<c:when test="${saleReport.searchType eq '2'}">
					<c:set var="type" value="周" />
				</c:when>
				<c:otherwise>
					<c:set var="type" value="月" />
				</c:otherwise>
		    </c:choose>  */
			
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
			
			$(".countryHref").click(function(e){
				e.preventDefault();
				var key = $(this).attr('key');
				if(key=='4'||key=='5-de'||key=='5-com'||key=='6'||key=='2-de'||key=='2-uk'||key=='2-com'||key=='3-com'||key=='3-de'||key=='7'){
					 if(key=='4'){
						 $(".otherPlatform").text("Offline");
					 }else if(key=='5-de'){
						 $(".otherPlatform").text("Website");
					 }else if(key=='6'){
						 $(".otherPlatform").text("Check24");
					 }else if(key=='2-de'){
						 $(".otherPlatform").text("Vendor_DE");
					 }else if(key=='2-uk'){
						 $(".otherPlatform").text("Vendor_UK");
					 }else if(key=='2-com'){
						 $(".otherPlatform").text("Vendor_US");
					 }else if(key=='3-com'){
						 $(".otherPlatform").text("Ebay_US");
					 }else if(key=='3-de'){
						 $(".otherPlatform").text("Ebay_DE");
					 }else if(key=='5-com'){
						 $(".otherPlatform").text("Website_US");
					 }else if(key=='7'){
						 $(".otherPlatform").text("Other");
					 }
				}else{
					$(".otherPlatform").text("Other Platform");
				}
				$("input[name='country']").val($(this).attr("key"));
				if(key==""||key==undefined||key==null){
					if($("#add").size()==0){
						//$("#allExport").append("<li id='add'><a id='byDateCountry'>页面数据所有国家</a></li>");
						//$("#allExport").append("<li id='add1'><a id='byProductTypeAll'>分产品类型所有国家</a></li>");
						//$("#allExport").append("<li id='add2'><a id='byProductGroupTypeAll'>分产品线所有国家</a></li> ");
						
						$("#allExport").append("<li id='add'><a id='byDateCountry' onclick='add();'><spring:message code="amazon_sales_data_all_countries"/></a></li>");
						$("#allExport").append("<li id='add1'><a id='byProductTypeAll'  onclick='add1();'><spring:message code="amazon_sales_product_summary_all_countries"/></a></li>");
						$("#allExport").append("<li id='add2'><a id='byProductGroupTypeAll'  onclick='add2();'><spring:message code="amazon_sales_product_line_all_countries"/></a></li> ");
					}
				}else{
					 $("#add").remove();
					 $("#add1").remove();
					 $("#add2").remove();
					
				}
				
				
				if('de'==key){
					if(!(myChartde.series)){
						myChartde.setOption(optionde);
						$("#deChart").css("width",$("#deChart").parent().parent().parent().width()-20);
						myChartde.resize();
					}
				}else if('com'==key){
					if(!(myChartcom.series)){
						myChartcom.setOption(optioncom);
						$("#comChart").css("width",$("#comChart").parent().parent().parent().width()-20);
						myChartcom.resize();
					}
				}else if('com2'==key){
					if(!(myChartcom2.series)){
						myChartcom2.setOption(optioncom2);
						$("#com2Chart").css("width",$("#com2Chart").parent().parent().parent().width()-20);
						myChartcom2.resize();
					}
				}else if('uk'==key){
					if(!(myChartuk.series)){
						myChartuk.setOption(optionuk);
						$("#ukChart").css("width",$("#ukChart").parent().parent().parent().width()-20);
						myChartuk.resize();
					}
				}else if('ca'==key){
					if(!(myChartca.series)){
						myChartca.setOption(optionca);
						$("#caChart").css("width",$("#caChart").parent().parent().parent().width()-20);
						myChartca.resize();
					}
				}else if('jp'==key){
					if(!(myChartjp.series)){
						myChartjp.setOption(optionjp);
						$("#jpChart").css("width",$("#jpChart").parent().parent().parent().width()-20);
						myChartjp.resize();
					}
				}else if('es'==key){
					if(!(myChartes.series)){
						myChartes.setOption(optiones);
						$("#esChart").css("width",$("#esChart").parent().parent().parent().width()-20);
						myChartes.resize();
					}
				}else if('it'==key){
					if(!(myChartit.series)){
						myChartit.setOption(optionit);
						$("#itChart").css("width",$("#itChart").parent().parent().parent().width()-20);
						myChartit.resize();
					}
				}else if('fr'==key){
					if(!(myChartfr.series)){
						myChartfr.setOption(optionfr);
						$("#frChart").css("width",$("#frChart").parent().parent().parent().width()-20);
						myChartfr.resize();
					}
				}else if('en'==key){
					if(!(myChartEn.series)){
						myChartEn.setOption(optionEn);
						$("#enTotalChart").css("width",$("#enTotalChart").parent().parent().parent().width()-20);
						myChartEn.resize();
					}
				}else if('unEn'==key){
					if(!(myChartUnEn.series)){
						myChartUnEn.setOption(optionUnEn);
						$("#unEnTotalChart").css("width",$("#unEnTotalChart").parent().parent().parent().width()-20);
						myChartUnEn.resize();
					}
				}else if('mx'==key){
					if(!(myChartmx.series)){
						myChartmx.setOption(optionmx);
						$("#mxChart").css("width",$("#mxChart").parent().parent().parent().width()-20);
						myChartmx.resize();
					}
				}
				$(this).tab('show');
				
			});
			
			$("#btnSubmit").click(function(){
				$("input[name='country']").val("");
				$("#searchForm").submit();
			});
			
			
			$("select[name='productType']").change(function(){
				$("input[name='country']").val("");
				$("#searchForm").submit();
			});
			
			//构造图表
			//ecahrts-----------------
			var myChart;var myChartEn;var optionEn;var myChartUnEn;var optionUnEn;
			<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">var myChart${dic.value};var option${dic.value}; </c:if></c:forEach>
			
			
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
		            	title:{text:'<spring:message code="amazon_product_sales_statistics"/>',x:'center'},		
		                tooltip : {
		                    trigger: 'item'
		                },
		                legend: {
		                	y:30,
		                	selected: {'英语国家':false,'非英语国家':false,<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">'${dic.label}':false,</c:if></c:forEach>'Vendor_DE':false,'Ebay_DE':false,'Ebay_US':false,'Offline':false,'Website_DE':false,'Website_US':false,'check24':false,'Other':false},
		                    data:['总计','英语国家','非英语国家',<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">'${dic.label}',</c:if></c:forEach>'Vendor_DE','Ebay_DE','Ebay_US','Offline','Website_DE','Website_US','check24','Other']
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
		                        data :[<c:forEach items="${xAxis}" var="x" varStatus="i">'${x}${saleReport.searchType ne 1?type:""}'${i.last?'':','}</c:forEach>],
		                   		boundaryGap:false
		                    }
		                ],
		                yAxis : [
		                    {
		                        type : 'value',
		                        splitArea : {show : true},
			                    axisLabel : {
		                            formatter: '{value}${currencySymbol}'
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
							},
							{
							      name:'英语国家',
							      type:'line',
							      data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty data['en'][x].sales?0:data['en'][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
							      markLine : {
							      	 data : [
							              {type : 'average', name: '平均线'}
							          ]
							      }
							},
							{
							      name:'非英语国家',
							      type:'line',
							      data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty data['unEn'][x].sales?0:data['unEn'][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
							      markLine : {
							      	 data : [
							              {type : 'average', name: '平均线'}
							          ]
							      }
							},
							//<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">
								{
								      name:'${dic.label}',
								      type:'line',
								      data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty data[dic.value][x].sales?0:data[dic.value][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
								      markLine : {
								      	 data : [
								              {type : 'average', name: '平均线'}
								          ]
								      }
								},
							//</c:if></c:forEach>
								{
								    name:'Vendor_DE',
								    type:'line',
								    data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty otherData['2-de'][x].sales?0:otherData['2-de'][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
								    markLine : {
								    	 data : [
								            {type : 'average', name: '平均线'}
								        ]
								    }
								},
								{
								    name:'Ebay_DE',
								    type:'line',
								    data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty otherData['3-de'][x].sales?0:otherData['3-de'][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
								    markLine : {
								    	 data : [
								            {type : 'average', name: '平均线'}
								        ]
								    }
								}, 
								{
								    name:'Ebay_US',
								    type:'line',
								    data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty otherData['3-com'][x].sales?0:otherData['3-com'][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
								    markLine : {
								    	 data : [
								            {type : 'average', name: '平均线'}
								        ]
								    }
								}, 
								{
								    name:'Offline',
								    type:'line',
								    data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty (otherData['4-de'][x].sales+otherData['4-com'][x].sales+otherData['4-cn'][x].sales)?0:(otherData['4-de'][x].sales+otherData['4-com'][x].sales+otherData['4-cn'][x].sales)}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
								    markLine : {
								    	 data : [
								            {type : 'average', name: '平均线'}
								        ]
								    }
								}, 
								{
								    name:'Website_DE',
								    type:'line',
								    data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty otherData['5-de'][x].sales?0:otherData['5-de'][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
								    markLine : {
								    	 data : [
								            {type : 'average', name: '平均线'}
								        ]
								    }
								}, 
								{
								    name:'Website_US',
								    type:'line',
								    data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty otherData['5-com'][x].sales?0:otherData['5-com'][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
								    markLine : {
								    	 data : [
								            {type : 'average', name: '平均线'}
								        ]
								    }
								}, 
								{
								    name:'check24',
								    type:'line',
								    data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty otherData['6-de'][x].sales?0:otherData['6-de'][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
								    markLine : {
								    	 data : [
								            {type : 'average', name: '平均线'}
								        ]
								    }
								},{
								    name:'Other',
								    type:'line',
								    data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty (otherData['7-de'][x].sales+otherData['7-com'][x].sales+otherData['7-cn'][x].sales)?0:(otherData['7-de'][x].sales+otherData['7-com'][x].sales+otherData['7-cn'][x].sales)}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
								    markLine : {
								    	 data : [
								            {type : 'average', name: '平均线'}
								        ]
								    }
								}
		                ]
		            };
		            myChart.setOption(option);
		            
		            
		            myChartEn = ec.init(document.getElementById("enTotalChart"));
		            myChartEn.showLoading({
					    text: '正在努力的读取数据中...',    //loading话术
					});
					//ecahrts-----------------
		            optionEn = {
		            	title:{text:'<spring:message code="amazon_sales_english_country"/>  <spring:message code="amazon_product_sales_statistics"/>',x:'center'},		
		                tooltip : {
		                    trigger: 'item'
		                },
		                legend: {
		                	y:30,
		                	selected: {'总计':false},
		                    data:['英语国家','总计']
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
		                        data :[<c:forEach items="${xAxis}" var="x" varStatus="i">'${x}${saleReport.searchType ne 1?type:""}'${i.last?'':','}</c:forEach>],
		                   		boundaryGap:false
		                    }
		                ],
		                yAxis : [
		                    {
		                        type : 'value',
		                        splitArea : {show : true},
			                    axisLabel : {
		                            formatter: '{value}${currencySymbol}'
		                        },
		                        boundaryGap:[0,0.5]
		                    }
		                ],
		                series : [
								{
								      name:'英语国家',
								      type:'line',
								      data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty data['en'][x].sales?0:data['en'][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
								      markLine : {
								      	 data : [
								              {type : 'average', name: '平均线'}
								          ]
								      }
								},
								{
								      name:'总计',
								      type:'line',
								      data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber pattern="#######.##" value="${empty data['total'][x].sales?0:data['total'][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
								      markLine : {
								      	 data : [
								              {type : 'average', name: '平均线'}
								          ]
								      }
								}
		                ]
		            };
		            myChartEn.setOption(optionEn);
		            
		            //-------------------------非英语国家
		            myChartUnEn = ec.init(document.getElementById("unEnTotalChart"));
		            myChartUnEn.showLoading({
					    text: '正在努力的读取数据中...',    //loading话术
					});
					//ecahrts-----------------
		            optionUnEn = {
		            	title:{text:'<spring:message code="amazon_sales_english_country"/> <spring:message code="amazon_product_sales_statistics"/>',x:'center'},		
		                tooltip : {
		                    trigger: 'item'
		                },
		                legend: {
		                	y:30,
		                	selected: {'总计':false},
		                    data:['非英语国家','总计']
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
		                        data :[<c:forEach items="${xAxis}" var="x" varStatus="i">'${x}${saleReport.searchType ne 1?type:""}'${i.last?'':','}</c:forEach>],
		                   		boundaryGap:false
		                    }
		                ],
		                yAxis : [
		                    {
		                        type : 'value',
		                        splitArea : {show : true},
			                    axisLabel : {
		                            formatter: '{value}${currencySymbol}'
		                        },
		                        boundaryGap:[0,0.5]
		                    }
		                ],
		                series : [
								{
								      name:'非英语国家',
								      type:'line',
								      data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty data['unEn'][x].sales?0:data['unEn'][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
								      markLine : {
								      	 data : [
								              {type : 'average', name: '平均线'}
								          ]
								      }
								},
								{
								      name:'总计',
								      type:'line',
								      data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber pattern="#######.##" value="${empty data['total'][x].sales?0:data['total'][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
								      markLine : {
								      	 data : [
								              {type : 'average', name: '平均线'}
								          ]
								      }
								}
		                ]
		            };
		            myChartUnEn.setOption(optionUnEn);
		            
		            
		            
		          //<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">
		            	myChart${dic.value} = ec.init(document.getElementById("${dic.value}Chart"));
			            myChart${dic.value}.showLoading({
						    text: '正在努力的读取数据中...',    //loading话术
						});
						//ecahrts-----------------
			            option${dic.value} = {
			            	title:{text:'${dic.label} <spring:message code="amazon_product_sales_statistics"/>',x:'center'},		
			                tooltip : {
			                    trigger: 'item'
			                },
			                legend: {
			                	y:30,
			                	selected: {'总计':false},
			                    data:['${dic.label}','总计']
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
			                        data :[<c:forEach items="${xAxis}" var="x" varStatus="i">'${x}${saleReport.searchType ne 1?type:""}'${i.last?'':','}</c:forEach>],
			                   		boundaryGap:false
			                    }
			                ],
			                yAxis : [
			                    {
			                        type : 'value',
			                        splitArea : {show : true},
				                    axisLabel : {
			                            formatter: '{value}${currencySymbol}'
			                        },
			                        boundaryGap:[0,0.5]
			                    }
			                ],
			                series : [
								{
								    name:'${dic.label}',
								    type:'line',
								    data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty data[dic.value][x].sales?0:data[dic.value][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
								    markLine : {
								    	 data : [
								            {type : 'average', name: '平均线'}
								        ]
								    }
								},
								{
								      name:'总计',
								      type:'line',
								      data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber pattern="#######.##" value="${empty data['total'][x].sales?0:data['total'][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
								      markLine : {
								      	 data : [
								              {type : 'average', name: '平均线'}
								          ]
								      }
								}
			                ]
			            };
			            //myChart${dic.value}.setOption(option${dic.value});
		          //</c:if></c:forEach>
		        }
		    );
		    
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
			    tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'6':'5'})").html("<span class=\"badge badge-info\">"+(num.toFixed(2)=="NaN"?"0":num.toFixed(2))+"‰</span>");
			    num = (tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'8':'7'})").text()-tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'9':'8'})").text())*1000/tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'9':'8'})").text();
			    tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'10':'9'})").html("<span class=\"badge badge-info\">"+(num.toFixed(2)=="NaN"?"0":num.toFixed(2))+"‰</span>");
		    })
		    
		    $("a[rel='popover']").popover({trigger:'hover'});
		    
		    $("#totalTb").dataTable({
		    	"searching":false,
				"sDom" : "t <'row'<'spanexl'l><'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				 "aaSorting": [[ 0, "desc" ]]
			});
		    
		    $("#enTotalTb").dataTable({
		    	"searching":false,
				"sDom" : "t <'row'<'spanexl'l><'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				 "aaSorting": [[ 0, "desc" ]]
			});
		    
		    $("#unEnTotalTb").dataTable({
		    	"searching":false,
				"sDom" : "t <'row'<'spanexl'l><'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
						[ 10, 20, 60, 100, "All" ] ],
				"bScrollCollapse" : true,
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				 "aaSorting": [[ 0, "desc" ]]
			});
		    
		  //<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">
		  		 $("#${dic.value}Tb").dataTable({
		  			"searching":false,
					"sDom" : "t <'row'<'spanexl'l><'spanexl'i><'spanexr'p>>",
					"sPaginationType" : "bootstrap",
					"iDisplayLength" : 10,
					"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
							[ 10, 20, 60, 100, "All" ] ],
					"bScrollCollapse" : true,
					"oLanguage" : {
						"sLengthMenu" : "_MENU_ 条/页"
					},
					"ordering" : true,
					 "aaSorting": [[ 0, "desc" ]]
				});
		  //</c:if></c:forEach>
          //<c:forEach items="${otherData}" var="dic" varStatus="i"><c:if test="${!fn:startsWith(dic.key, '1')&&!fn:startsWith(dic.key, '4')&&!fn:startsWith(dic.key, '5')&&!fn:startsWith(dic.key, '7')&&dic.key ne 'total'}">
		  		 $("#${dic.key}Tb").dataTable({
		  			"searching":false,
					"sDom" : "t <'row'<'spanexl'l><'spanexl'i><'spanexr'p>>",
					"sPaginationType" : "bootstrap",
					"iDisplayLength" : 10,
					"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
							[ 10, 20, 60, 100, "All" ] ],
					"bScrollCollapse" : true,
					"oLanguage" : {
						"sLengthMenu" : "_MENU_ 条/页"
					},
					"ordering" : true,
					 "aaSorting": [[ 0, "desc" ]]
				});
		  //</c:if></c:forEach>
		     $("#4Tb").dataTable({
		  			"searching":false,
					"sDom" : "t <'row'<'spanexl'l><'spanexl'i><'spanexr'p>>",
					"sPaginationType" : "bootstrap",
					"iDisplayLength" : 10,
					"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
							[ 10, 20, 60, 100, "All" ] ],
					"bScrollCollapse" : true,
					"oLanguage" : {
						"sLengthMenu" : "_MENU_ 条/页"
					},
					"ordering" : true,
					 "aaSorting": [[ 0, "desc" ]]
				});
		     $("#5Tb").dataTable({
		  			"searching":false,
					"sDom" : "t <'row'<'spanexl'l><'spanexl'i><'spanexr'p>>",
					"sPaginationType" : "bootstrap",
					"iDisplayLength" : 10,
					"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
							[ 10, 20, 60, 100, "All" ] ],
					"bScrollCollapse" : true,
					"oLanguage" : {
						"sLengthMenu" : "_MENU_ 条/页"
					},
					"ordering" : true,
					 "aaSorting": [[ 0, "desc" ]]
				});
		     $("#6Tb").dataTable({
		  			"searching":false,
					"sDom" : "t <'row'<'spanexl'l><'spanexl'i><'spanexr'p>>",
					"sPaginationType" : "bootstrap",
					"iDisplayLength" : 10,
					"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
							[ 10, 20, 60, 100, "All" ] ],
					"bScrollCollapse" : true,
					"oLanguage" : {
						"sLengthMenu" : "_MENU_ 条/页"
					},
					"ordering" : true,
					 "aaSorting": [[ 0, "desc" ]]
				});
		     $("#7Tb").dataTable({
		  			"searching":false,
					"sDom" : "t <'row'<'spanexl'l><'spanexl'i><'spanexr'p>>",
					"sPaginationType" : "bootstrap",
					"iDisplayLength" : 10,
					"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
							[ 10, 20, 60, 100, "All" ] ],
					"bScrollCollapse" : true,
					"oLanguage" : {
						"sLengthMenu" : "_MENU_ 条/页"
					},
					"ordering" : true,
					 "aaSorting": [[ 0, "desc" ]]
				});
			  		$("#byDate").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportOriginal");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/list2");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    });
			  		/* $("#byDateCountry").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportByDateCountry");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/list2");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    }); */
					$("#byProductCountry").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportByProductCountry");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/list2");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    });
					$("#byProduct").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportStatistics");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/list2");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    });
					$("#byProductType").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportByProductTypeCountry");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/list2");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    });
					/* $("#byProductTypeAll").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportByProductTypeAllCountry");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/list2");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    }); */
					/* $("#byProductGroupTypeAll").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportByGroupTypeAllCountry");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/list2");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    }); */
					$("#byProfit").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportTotalProfit");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/list2");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    });
					$("#byProductGroupTypePart").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportByGroupTypePartCountry");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/list2");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    });

					  $("#typeGroup").change(function(){    
			    	       queryType($("#typeGroup").val());
			    	  });  
					 <c:if test="${not empty saleReport.groupName}" >
			             queryType('${saleReport.groupName}');	
			         </c:if>
					
					$(".pagination a").addClass("nava");
					
					$(".compare").click(function(e){
						var $div = $(".tab-pane.active");
						var cks = $div.find("input[type='checkbox']:checked");
						if(cks.size()>2||cks.size()==0){
							e.preventDefault();
							$.jBox.tip("请选择至少1个最多2个区间比较!", 'error');
							return false;
						}else{
							var key = $div.attr("id");
							if("total" == key){
								key = "";
							}else if("enTotal" == key){
								key = "en";
							}else if("unEnTotal" == key){
								key = "unEn";
							}
							var param = {};
							param.country = key;
							param.type = '${saleReport.searchType}';
							param.endTime = $(cks.get(0)).val();
							param.currencyType = '${saleReport.currencyType}';
							param.lineType='${saleReport.groupName}';
							if(cks.size()>1){
								param.startTime = $(cks.get(1)).val();
							}
							$(this).attr("href","${ctx}/amazoninfo/salesReprots/contrastSaleView?"+$.param(param));
							return true;
						}
					});
					
					$("#showDetailRate").click(function(){
						var endData=$("#end").val();
						var params={};
						params.start=$("#start").val();
						params.end=endData;
						params.groupName="${saleReport.groupName}";
						var cssVal=$("#rateShow").css("display");
						if(cssVal=="none"){
							$("#rateShow").css("display","block");
							if(flag==0){
								$.ajax({
								    type: 'post',
								    async:false,
								    url: '${ctx}/amazoninfo/salesReprots/getRateDetail2' ,
								    data: $.param(params),
								    success:function(data){ 
								    	var tbody=$("#rate tbody");
										var tr="";
										
										$.each(data,function(i,map){  
											var total1=map['totalAvg'].totalWeight;
											var total2=map['totalAvg'].sales;
											var total3=map['totalAvg'].addUpSales;
											var total4=map['totalAvg'].dayGoal;
											var total5=map['totalAvg'].addUpGoal;
											var total6=map['totalAvg'].autoDayGoal;
											var total7=map['totalAvg'].dayWeight;
											var total8=map['totalAvg'].rate;
											var info="月权重 "+total1+"<br/>日销售额 "+total2+"<br/>累计销售额  "+total3+"<br/>日目标  "+total4+"<br/>累计日目标  "+total5+"<br/>动态日目标 "+total6+"<br/>日权重 "+total7;
											tr+="<tr>";
											tr+="<td style='text-align: center;vertical-align: middle;'>"+i+"</td>";
											
										   tr+="<td style='text-align: center;vertical-align: middle;'><a href='#' style='color: #08c;' data-toggle='popover' data-html='true' rel='popover' data-content='"+info+"' >"+toDecimal(total8)+"%</a></td>";
								            var enTr="";
								            var deTr="";
								            var frTr="";
								            var itTr="";
								            var esTr="";
								            var jpTr="";
											$.each(map,function(key,values){  
												if(key!='totalAvg'){
													$(values).each(function(){     
												        var t1=this.totalWeight;
														var t2=this.sales;
														var t3=this.addUpSales;
														var t4=this.dayGoal;
														var t5=this.addUpGoal;
														var t6=this.autoDayGoal;
														var t7=this.dayWeight;
														var t8=this.rate;
														var info="月权重 "+t1+"<br/>日销售额 "+t2+"<br/>累计销售额  "+t3+"<br/>日目标  "+t4+"<br/>累计日目标  "+t5+"<br/>动态日目标 "+t6+"<br/>日权重 "+t7;
												   	    if(key=='en'){
												   	    	enTr="<td style='text-align: center;vertical-align: middle;'><a href='#' style='color: #08c;' data-toggle='popover' data-html='true' rel='popover' data-content='"+info+"'>"+toDecimal(t8)+"%</a></td>";
												   	    }
												   	    if(key=='de'){
												   	    	deTr="<td style='text-align: center;vertical-align: middle;'><a href='#' style='color: #08c;' data-toggle='popover' data-html='true' rel='popover' data-content='"+info+"'>"+toDecimal(t8)+"%</a></td>";
												   	    }
													   	if(key=='fr'){
												   	    	frTr="<td style='text-align: center;vertical-align: middle;'><a href='#' style='color: #08c;' data-toggle='popover' data-html='true' rel='popover' data-content='"+info+"'>"+toDecimal(t8)+"%</a></td>";
												   	    }
													   	if(key=='it'){
												   	    	itTr="<td style='text-align: center;vertical-align: middle;'><a href='#' style='color: #08c;' data-toggle='popover' data-html='true' rel='popover' data-content='"+info+"'>"+toDecimal(t8)+"%</a></td>";
												   	    }
													   	if(key=='es'){
												   	    	esTr="<td style='text-align: center;vertical-align: middle;'><a href='#' style='color: #08c;' data-toggle='popover' data-html='true' rel='popover' data-content='"+info+"'>"+toDecimal(t8)+"%</a></td>";
												   	    }
													   	if(key=='jp'){
												   	    	jpTr="<td style='text-align: center;vertical-align: middle;'><a href='#' style='color: #08c;' data-toggle='popover' data-html='true' rel='popover' data-content='"+info+"'>"+toDecimal(t8)+"%</a></td>";
												   	    }
														//tr+="<td style='text-align: center;vertical-align: middle;'><a href='#' style='color: #08c;' data-toggle='popover' data-html='true' rel='popover' data-content='"+info+"'>"+toDecimal(t8)+"%</a></td>";
												    }); 
												}
											        
											 });
											tr+=enTr;
											tr+=deTr;
											tr+=frTr;
											tr+=jpTr;
											tr+=itTr;
											tr+=esTr;
											tr+="</tr>";
										});
										tbody.append(tr);
										 $("a[rel='popover']").popover({trigger:'hover'});
										 $("#rate").dataTable({
										    	"searching":false,
												"sDom" : "t <'row'<'spanexl'l><'spanexl'i><'spanexr'p>>",
												"sPaginationType" : "bootstrap",
												"iDisplayLength" : 10,
												"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
														[ 10, 20, 60, 100, "All" ] ],
												"bScrollCollapse" : true,
												"oLanguage" : {
													"sLengthMenu" : "_MENU_ 条/页"
												},
												"ordering" : true,
												 "bDestroy":true,
												 "sScrollX":"100%",
												 "aaSorting": [[ 0, "desc" ]]
											});
							        }
								});
							}
							flag=1;
						}else{
							$("#rateShow").css("display","none");
						}
						
					});
					
					 $("#rate").dataTable({
					    	"searching":false,
							"sDom" : "t <'row'<'spanexl'l><'spanexl'i><'spanexr'p>>",
							"sPaginationType" : "bootstrap",
							"iDisplayLength" : 10,
							"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
									[ 10, 20, 60, 100, "All" ] ],
							"bScrollCollapse" : true,
							"oLanguage" : {
								"sLengthMenu" : "_MENU_ 条/页"
							},
							"ordering" : true,
							 "aaSorting": [[ 0, "desc" ]]
						});
					    
					 $("#showRate").textScroll();
		});
		
		 function queryType(id){
			 $.ajax({  
			        type : 'POST', 
			        url : '${ctx}/psi/psiProductTypeGroupDict/getProductType',  
			        dataType:"json",
			        data : 'groupId=' +id,  
			        async: false,
			        success : function(msg){ 
			        	$("#productType").empty();   
			        	var option = "<option value=''>--All--</option>";   
			            for(var i=0;i<msg.length;i++){
 	            	        if(msg[i].value=="${saleReport.productType}"){
 								option += "<option selected value=\"" + msg[i].value + "\">" + msg[i].value + "</option>"; 
 							}else{
 								option += "<option  value=\"" + msg[i].value + "\">" + msg[i].value + "</option>"; 
 							}
 	                    }
			            $("#productType").append(option);
			            $("#productType").select2("val","${saleReport.productType}");
			        }
			  }); 
		 }
		function searchTypes(searchFlag){
			if(oldSearchFlag==searchFlag){
				return;
			}
			$("#searchType").val(searchFlag);
			//必须不传
			$("#start").val("");
			$("#end").val("");
			//$("#typeGroup").val("");
			$("input[name='country']").val("");
			$("#searchForm").submit();
		}
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
		
		function changeCurrecyType(){
			$("#searchForm").submit();
		}
		 function toDecimal(x) {  
	            var f = parseFloat(x);  
	            if (isNaN(f)) {  
	                return;  
	            }  
	            f = Math.round(x*10)/10;  
	            return f;  
	    }  
		 
		 function add(){
			 top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportByDateCountry");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/list2");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
		 }
		 
		 function add1(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportByProductTypeAllCountry");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/list2");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
		 }
		 
		 function add2(){
			 top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportByGroupTypeAllCountry");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/list2");
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
		 }
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/salesReprots/lineList?searchType=${saleReport.searchType}">总计</a></li>
		<c:forEach var="temp" items="${allLine}" varStatus="i">
		  <li ${temp.key eq saleReport.groupName?'class=active':''}><a href="${ctx}/amazoninfo/salesReprots/list2?groupName=${temp.key}&searchType=${saleReport.searchType}">${temp.value}</a></li>
		</c:forEach>  
	</ul>
	   <div class="alert alert-info">
	    	<c:forEach items="${tipList }" var="tip" varStatus="i">
	    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	    		<c:if test="${i.index==0}"><spring:message code="amazon_sales_month_goal"/>:</c:if><c:if test="${i.index==1}"><spring:message code="amazon_sales_english_country"/> <spring:message code="amazon_sales_goal"/>:</c:if><c:if test="${i.index==2}"><spring:message code="amazon_sales_non_english_country"/> <spring:message code="amazon_sales_goal"/>:</c:if>
	    		<b>${tip[0]}</b>${currencySymbol}
	    		,<spring:message code="amazon_sales_real_accumulated_sales_rate"/>:<span style="color:${tip[1]>100?'green':'red'}"><b><fmt:formatNumber value=" ${tip[1]}" pattern="0.##"/>%</b></span>
	    		,<spring:message code="amazon_sales_expected_volume"/>:<span style="color:${tip[2]>100?'green':'red'}"><b><fmt:formatNumber value=" ${tip[2]}" pattern="0.##"/>%</b></span><br/>
	    	</c:forEach>
	      <shiro:hasPermission name="psi:product:viewPrice">
	    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	               <b><spring:message code="amazon_sales_account"/> &nbsp;&nbsp;&nbsp;&nbsp; DE:<fmt:formatNumber value="${balance['de']}" />€&nbsp;&nbsp; US:<fmt:formatNumber value="${balance['com']}" />
	               $&nbsp;&nbsp; UK:<fmt:formatNumber value="${balance['uk']}" />£ &nbsp;&nbsp;FR:<fmt:formatNumber value="${balance['fr']}" />€ &nbsp;&nbsp;
				JP:<fmt:formatNumber value="${balance['jp']}" />￥&nbsp;&nbsp; IT:<fmt:formatNumber value="${balance['it']}" />€&nbsp;&nbsp; ES:<fmt:formatNumber value="${balance['es']}" />€ &nbsp;&nbsp;
				CA:<fmt:formatNumber value="${balance['ca']}" />CAD&nbsp;&nbsp; MX:<fmt:formatNumber value="${balance['mx']}" />MXN</b>
		   </shiro:hasPermission>
		   
	    
	    
	    
	  </div>
<form:form id="searchForm" modelAttribute="saleReport" action="${ctx}/amazoninfo/salesReprots/list2" method="post" class="form-search">
		<div style="height: 30px">
		<ul class="nav nav-pills" style="width:250px;float:left;" id="myTab">
			<li data-toggle="pills" id="showTab0"><a href="#" onclick="javaScript:searchTypes('1');return false;">By Day</a></li>
			<li data-toggle="pills" id="showTab1"><a href="#" onclick="javaScript:searchTypes('2');return false;">By Week</a></li>
			<li data-toggle="pills" id="showTab2"><a href="#" onclick="javaScript:searchTypes('3');return false;">By Month</a></li>
		</ul>
		
		<!-- 货币美元&欧元切换,默认为欧元版 -->
		 <spring:message code="amazon_sales_currency"/>:<select name="currencyType" id="currencyType" style="width: 100px" onchange="changeCurrecyType()">
				<option value="EUR" ${'EUR' eq fn:trim(saleReport.currencyType)?'selected':''}>EUR</option>
				<option value="USD" ${'USD' eq fn:trim(saleReport.currencyType)?'selected':''}>USD</option>
		</select>
		
	           <spring:message code="amazon_sales_product_line"/>:<select id="typeGroup" style="width: 100px" disabled>
				<option value="">--All--</option>
				<option value="unGrouped" ${'unGrouped' eq fn:trim(saleReport.groupName)?'selected':''}>UnGrouped</option>
				<c:forEach items="${groupType}" var="groupType">
					<option value="${groupType.id}" ${fn:trim(groupType.id) eq fn:trim(saleReport.groupName)?'selected':''}>${groupType.name}</option>			
				</c:forEach>
			</select>
		<input type="hidden" value="${saleReport.groupName }" name="groupName" />
		<select name="productType" id="productType" style="width: 100px">
			<option value="">--All--</option>
			<%-- <c:forEach items="${typesAll}" var="it">
				<option value="${it}" ${it eq saleReport.productType?'selected':''}>${it}</option>
			</c:forEach> --%>
		</select>
	
		<input id="searchType" name="searchType" type="hidden" value="${saleReport.searchType}" />
		<input name="country" type="hidden" value="${saleReport.country}"/>
		
		
		<span style="float: center;">
		<label></label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});" readonly="readonly"  class="Wdate" type="text" name="start" value="<fmt:formatDate value="${saleReport.start}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
		&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${saleReport.end}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code="sys_but_search"/>"/>
		&nbsp;
	            	<div class="btn-group">
						   <button type="button" class="btn btn-primary"><spring:message code="sys_but_export"/></button>
						   <button type="button" class="btn btn-primary dropdown-toggle"  data-toggle="dropdown">
						      <span class="caret"></span>
						      <span class="sr-only"></span>
						   </button>
						  <%--  <ul class="dropdown-menu" id="allExport">
						      <li><a  id="byDate">页面数据</a></li>
						      <li><a id="byProductCountry">分产品汇总统计</a></li>
						       <li><a id="byProductType">分产品类型统计</a></li> 
						      <li><a id="byProduct">分产品详情统计</a></li>
						      <shiro:hasPermission name="psi:inventory:stockPriceView">
						         <li><a id="byProfit">毛利统计</a></li>
						      </shiro:hasPermission>
						      <li id='add'><a id="byDateCountry">页面数据所有国家</a></li> 
						      <li id='add1'><a id="byProductTypeAll">分产品类型所有国家</a></li> 
						      <li id='add2'><a id="byProductGroupTypeAll">分产品线所有国家</a></li> 
						     
						   </ul> --%>
						   <ul class="dropdown-menu" id="allExport">
						      <li><a  id="byDate"><spring:message code="amazon_sales_data_in_page"/></a></li>
						      <li><a id="byProductCountry"><spring:message code="amazon_sales_products_overall"/></a></li>
						      <li><a id="byProductType"><spring:message code="amazon_sales_product_summary"/></a></li> 
						      <li><a id="byProduct"><spring:message code="amazon_sales_product_details"/></a></li>
	
						      <shiro:hasPermission name="psi:inventory:stockPriceView">
						         <li><a id="byProfit"><spring:message code="amazon_sales_profit_statistics"/></a></li>
						      </shiro:hasPermission>
						      <li id='add'><a id="byDateCountry" onclick='add();'><spring:message code="amazon_sales_data_all_countries"/></a></li> 
						      <li id='add1'><a id="byProductTypeAll" onclick='add1();'><spring:message code="amazon_sales_product_summary_all_countries"/></a></li> 
						      <li id='add2'><a id="byProductGroupTypeAll" onclick='add2();'><spring:message code="amazon_sales_product_line_all_countries"/></a></li> 
						   </ul>
				 </div>
		</span>
		</div>
	</form:form>
	<%-- <c:if test="${not empty realList1}">
	<div class="accordion-heading" >
		<a id="showDetailRate" ><b><spring:message code="amazon_sales_completion_rate"/> ${formatDate}</b></a>
	</div>
	</c:if> --%>
	<div  style="display: none" id="rateShow">
	<table id="rate" class="desc table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_forecast_completion_rate"/></th>
				<th style="text-align: center;vertical-align: middle;">Total</th>
				<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_english_country"/></th>
				<c:forEach items="${fns:getDictList('platform')}" var="dic">
					   <c:if test="${dic.value ne 'com.unitek'&&dic.value ne 'com'&&dic.value ne 'ca'&&dic.value ne 'uk'&&dic.value ne 'mx'}">
						  <th style="text-align: center;vertical-align: middle;">${dic.label}</th>
					  </c:if>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			
		</tbody>
	</table>
	</div>
	
	<table class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:15%" rowspan="2"><spring:message code="amazon_sales_country"/></th>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
				<c:if test="${not empty realList1}">
					    <c:if test="${saleReport.searchType=='1'&&day>='03'}">
					    <th colspan="4" style="text-align: center;vertical-align: middle;
					      ${i.index==1?'color: #08c;':''}">
						  ${xAxis[fn:length(xAxis)-i.index]}${type}
						  </th>
						</c:if>	
						 <c:if test="${saleReport.searchType!='1'||day<'03'}"><th colspan="3" style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
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
			  
					<th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><spring:message code="amazon_sales_volum"/>(${currencySymbol})</th>
					<th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><spring:message code="amazon_sales_quantity"/></th>	
					<th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">Avg. Sales(${currencySymbol})</th>
				    <c:if test="${not empty realList1}">
				    <c:if test="${saleReport.searchType=='1'&&day>='03'}"><th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
					  <c:if test="${fns:getDate('yyyyMMdd')==xAxis[fn:length(xAxis)-i.index]}"><spring:message code="amazon_sales_goal"/>(${currencySymbol})</c:if>
					  <c:if test="${fns:getDate('yyyyMMdd')!=xAxis[fn:length(xAxis)-i.index]}"><spring:message code="amazon_sales_accmulated_completion_rate"/></c:if>
					  </th>
					</c:if>	
				</c:if>		
				</c:forEach>
			</tr>
		</thead>
		<tbody>
		   
			<c:forEach var="temp" items="${sec}" varStatus="i">
			<%--   <c:if test="${'mx' ne temp.country}"> --%>
				<tr>
					<td style="text-align: center;vertical-align: middle;">${fns:getDictLabel(temp.country,'platform','')}</td>
					<c:forEach begin="1" end="3" step="1" varStatus="i">
						
						<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><fmt:formatNumber value="${data[temp.country][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2"/></td>
						<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${data[temp.country][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
					  <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
					     <c:if test="${data[temp.country][xAxis[fn:length(xAxis)-i.index]].salesVolume>0}">
					      <fmt:formatNumber value="${data[temp.country][xAxis[fn:length(xAxis)-i.index]].sales/data[temp.country][xAxis[fn:length(xAxis)-i.index]].salesVolume}" maxFractionDigits="2" minFractionDigits="2"/>
					     </c:if> 
					  </td>
					<c:if test="${not empty realList1}">
					   <c:if test="${saleReport.searchType=='1'&&day>='03'}">
					       <c:if test="${'com' ne temp.country&&'uk' ne temp.country&&'ca' ne temp.country }">
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
					       <c:if test="${'com' eq temp.country||'uk' eq temp.country||'ca' eq temp.country }">
					          <td></td>
					       </c:if>
					   </c:if>
					 </c:if>
					</c:forEach>
				</tr>
			<%-- 	</c:if> --%>
			</c:forEach>
			
			<tr>
				<td style="text-align: center;vertical-align: middle;"><b><spring:message code="amazon_sales_english_country"/></b></td>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
				
				  <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><fmt:formatNumber value="${data['uk'][xAxis[fn:length(xAxis)-i.index]].sales+data['ca'][xAxis[fn:length(xAxis)-i.index]].sales+data['com'][xAxis[fn:length(xAxis)-i.index]].sales+data['com2'][xAxis[fn:length(xAxis)-i.index]].sales+data['com3'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2"/></td>
				  <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${data['uk'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['ca'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['com'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['com2'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['com3'][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
			      <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
					     <c:if test="${data['uk'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['ca'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['com'][xAxis[fn:length(xAxis)-i.index]].salesVolume>0}">
					      <fmt:formatNumber value="${(data['uk'][xAxis[fn:length(xAxis)-i.index]].sales+data['ca'][xAxis[fn:length(xAxis)-i.index]].sales+data['com'][xAxis[fn:length(xAxis)-i.index]].sales+data['com2'][xAxis[fn:length(xAxis)-i.index]].sales+data['com3'][xAxis[fn:length(xAxis)-i.index]].sales)/(data['uk'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['ca'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['com'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['com2'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['com3'][xAxis[fn:length(xAxis)-i.index]].salesVolume)}" maxFractionDigits="2" minFractionDigits="2"/>
					     </c:if> 
				  </td>
			<c:if test="${not empty realList1}">
				 <c:if test="${saleReport.searchType=='1'&&day>='03'&&fns:getDate('yyyyMMdd')==xAxis[fn:length(xAxis)-i.index]}">
				    <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
					          <c:if test="${realList1[xAxis[fn:length(xAxis)-i.index]]['en'].autoDayGoal>0}">
					             <fmt:formatNumber maxFractionDigits="2"  value="${realList1[xAxis[fn:length(xAxis)-i.index]]['en'].autoDayGoal}" pattern="#0.00"/>
					          </c:if>
					           <c:if test="${realList1[xAxis[fn:length(xAxis)-i.index]]['en'].autoDayGoal<=0}">
					                                 <font color='green'>已达成</font>
					           </c:if>     
				    </td>
				</c:if>	
				 <c:if test="${saleReport.searchType=='1'&&day>='03'&&fns:getDate('yyyyMMdd')!=xAxis[fn:length(xAxis)-i.index]}">
				   <c:set var="totalRate" value="${realList1[xAxis[fn:length(xAxis)-i.index]]['en'].rate }" />
				   <td style="text-align: center;vertical-align: middle;${totalRate<100?'color: #F00;':'color:#009900'}">
				      <fmt:formatNumber maxFractionDigits="1"  value="${realList1[xAxis[fn:length(xAxis)-i.index]]['en'].rate}" pattern="#0.0"/>
				   </td>
				</c:if>	
			  </c:if>	
			</c:forEach>
			</tr> 
			 <tr>
				<td style="text-align: center;vertical-align: middle;"><b><spring:message code="amazon_sales_non_english_country"/></b></td>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
					<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><fmt:formatNumber value="${data['total'][xAxis[fn:length(xAxis)-i.index]].sales-(data['uk'][xAxis[fn:length(xAxis)-i.index]].sales+data['ca'][xAxis[fn:length(xAxis)-i.index]].sales+data['com'][xAxis[fn:length(xAxis)-i.index]].sales+data['com2'][xAxis[fn:length(xAxis)-i.index]].sales+data['com3'][xAxis[fn:length(xAxis)-i.index]].sales) }" maxFractionDigits="2" minFractionDigits="2" /> </td>
					<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${data['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume-(data['uk'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['ca'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['com'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['com2'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['com3'][xAxis[fn:length(xAxis)-i.index]].salesVolume) }</td>		
				    <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
					     <c:if test="${(data['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume-(data['uk'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['ca'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['com'][xAxis[fn:length(xAxis)-i.index]].salesVolume))>0}">
					      <fmt:formatNumber value="${(data['total'][xAxis[fn:length(xAxis)-i.index]].sales-(data['uk'][xAxis[fn:length(xAxis)-i.index]].sales+data['ca'][xAxis[fn:length(xAxis)-i.index]].sales+data['com'][xAxis[fn:length(xAxis)-i.index]].sales+data['com2'][xAxis[fn:length(xAxis)-i.index]].sales+data['com3'][xAxis[fn:length(xAxis)-i.index]].sales))/(data['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume-(data['uk'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['ca'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['com'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['com2'][xAxis[fn:length(xAxis)-i.index]].salesVolume+data['com3'][xAxis[fn:length(xAxis)-i.index]].salesVolume))}" maxFractionDigits="2" minFractionDigits="2"/>
					     </c:if> 
					  </td>
				    <c:if test="${not empty realList1}">
				    <c:if test="${saleReport.searchType=='1'&&day>='03'&&fns:getDate('yyyyMMdd')==xAxis[fn:length(xAxis)-i.index]}">
				    <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
				          <c:set var='tempAutoGoal' value="${realList1[xAxis[fn:length(xAxis)-i.index]]['de'].autoDayGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['fr'].autoDayGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['jp'].autoDayGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['it'].autoDayGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['es'].autoDayGoal}"/>
				              <c:if test="${tempAutoGoal>0}">
					             <fmt:formatNumber maxFractionDigits="2"  value="${tempAutoGoal}" pattern="#0.00"/>
					          </c:if>
					           <c:if test="${tempAutoGoal<=0}">
					               <font color='green'>已达成</font>
					           </c:if> 
				    </td>
				</c:if>	
				 <c:if test="${saleReport.searchType=='1'&&day>='03'&&fns:getDate('yyyyMMdd')!=xAxis[fn:length(xAxis)-i.index]}">
				    <c:set var='tempGoal' value="${realList1[xAxis[fn:length(xAxis)-i.index]]['de'].addUpGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['fr'].addUpGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['jp'].addUpGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['it'].addUpGoal+realList1[xAxis[fn:length(xAxis)-i.index]]['es'].addUpGoal}"/>
				    <c:set var='tempSales' value="${realList1[xAxis[fn:length(xAxis)-i.index]]['de'].addUpSales+realList1[xAxis[fn:length(xAxis)-i.index]]['fr'].addUpSales+realList1[xAxis[fn:length(xAxis)-i.index]]['jp'].addUpSales+realList1[xAxis[fn:length(xAxis)-i.index]]['it'].addUpSales+realList1[xAxis[fn:length(xAxis)-i.index]]['es'].addUpSales}"/>
				    <c:set var="totalRate" value="${tempSales*100/tempGoal }" />
				   <td style="text-align: center;vertical-align: middle;${totalRate<100?'color: #F00;':'color:#009900'}">
				    <fmt:formatNumber maxFractionDigits="1"  value="${tempSales*100/tempGoal}" pattern="#0.0"/>
				   </td>
				</c:if>	
			  </c:if>	   
				</c:forEach>
			</tr> 
			<tr>
				<td  style="text-align: center;vertical-align: middle;"><b>Amazon Total</b></td>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
					
					<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><fmt:formatNumber value="${data['total'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
					<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${data['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
				<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><fmt:formatNumber value="${data['total'][xAxis[fn:length(xAxis)-i.index]].sales/data['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume}" maxFractionDigits="2" minFractionDigits="2" /> </td>
					
				
				 <c:if test="${not empty realList1}">
				 <c:if test="${saleReport.searchType=='1'&&day>='03'&&fns:getDate('yyyyMMdd')==xAxis[fn:length(xAxis)-i.index]}">
				    <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
				 
				    <c:if test="${realList1[xAxis[fn:length(xAxis)-i.index]]['totalAvg'].addUpSales<countryMonthGoal1 }">
				        <fmt:formatNumber maxFractionDigits="2"  value="${realList1[xAxis[fn:length(xAxis)-i.index]]['totalAvg'].autoDayGoal }" pattern="#0.00"/>
				    </c:if>
				     <c:if test="${realList1[xAxis[fn:length(xAxis)-i.index]]['totalAvg'].addUpSales>=countryMonthGoal1 }">
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
	
	<div class="accordion-heading" >
		<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#otherThreeSales"><b>${xAxis[fn:length(xAxis)-1]}${type}<spring:message code="amazon_sales_volume_other_marketplaces"/>(${currencySymbol }):<fmt:formatNumber value="${otherData['total'][xAxis[fn:length(xAxis)-1]].sales-data['total'][xAxis[fn:length(xAxis)-1]].sales}" maxFractionDigits="2" minFractionDigits="2" />,<spring:message code="amazon_sales_here_for_details"/></b></a>
	</div>
	<div class="collapse" id="otherThreeSales">
	    <table class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:16%" rowspan="2"><spring:message code="amazon_sales_country"/></th>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
					<th colspan="2" style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
						  ${xAxis[fn:length(xAxis)-i.index]}${type}
				    </th>
				</c:forEach>
			</tr>
			<tr>
			  	<c:forEach begin="1" end="3" step="1" varStatus="i">
					<th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><spring:message code="amazon_sales_volum"/>(${currencySymbol})</th>
					<th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><spring:message code="amazon_sales_quantity"/></th>		
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			
			<c:forEach var="otherOrder" items="${otherData}">
			      <c:if test="${!fn:startsWith(otherOrder.key, '1')&&!fn:startsWith(otherOrder.key, 'amazonTotal')&&!fn:startsWith(otherOrder.key, '4')&&!fn:startsWith(otherOrder.key, '5')&&!fn:startsWith(otherOrder.key, '6')&&!fn:startsWith(otherOrder.key, '7')&&otherOrder.key!='total' }"> 
			          <tr>
						<td style="text-align: center;vertical-align: middle;">${otherOrder.key eq '2-de'?'Vendor_DE':(otherOrder.key eq '2-com'?'Vendor_US':(otherOrder.key eq '3-de'?'Ebay_DE':(otherOrder.key eq '2-uk'?'Vendor_UK':'Ebay_US')))}</td>
						<c:forEach begin="1" end="3" step="1" varStatus="i">
						  <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><fmt:formatNumber value="${otherData[otherOrder.key][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
						  <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${otherData[otherOrder.key][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
						</c:forEach>
			        </tr>
			     </c:if> 
			</c:forEach>
			<tr>
				<td  style="text-align: center;vertical-align: middle;">Offline</td>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
					
			     <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
			     <fmt:formatNumber value="${otherData['4-de'][xAxis[fn:length(xAxis)-i.index]].sales+otherData['4-com'][xAxis[fn:length(xAxis)-i.index]].sales+otherData['4-cn'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
			     <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${otherData['4-de'][xAxis[fn:length(xAxis)-i.index]].salesVolume+otherData['4-com'][xAxis[fn:length(xAxis)-i.index]].salesVolume+otherData['4-cn'][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
			</c:forEach>
			</tr>
				<tr>
				<td  style="text-align: center;vertical-align: middle;">Website_DE</td>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
					
			     <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
			     <fmt:formatNumber value="${otherData['5-de'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
			     <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${otherData['5-de'][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
			</c:forEach>
			</tr>
				<tr>
				<td  style="text-align: center;vertical-align: middle;">Website_US</td>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
					
			     <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
			     <fmt:formatNumber value="${otherData['5-com'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
			     <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${otherData['5-com'][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
			</c:forEach>
			</tr>
			<tr>
				<td  style="text-align: center;vertical-align: middle;">Check24</td>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
					
			     <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
			     <fmt:formatNumber value="${otherData['6-de'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
			     <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${otherData['6-de'][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
			</c:forEach>
			</tr>
			<tr>
				<td  style="text-align: center;vertical-align: middle;">Other</td>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
					
			     <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
			     <fmt:formatNumber value="${otherData['7-de'][xAxis[fn:length(xAxis)-i.index]].sales+otherData['7-com'][xAxis[fn:length(xAxis)-i.index]].sales+otherData['7-cn'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
			     <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${otherData['7-de'][xAxis[fn:length(xAxis)-i.index]].salesVolume+otherData['7-com'][xAxis[fn:length(xAxis)-i.index]].salesVolume+otherData['7-cn'][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
			</c:forEach>
			</tr>
			<tr>
				<td  style="text-align: center;vertical-align: middle;"><b><spring:message code="amazon_sales_other_total"/></b></td>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
			    <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><fmt:formatNumber value="${otherData['total'][xAxis[fn:length(xAxis)-i.index]].sales-data['total'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
			    <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${otherData['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume-data['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
				</c:forEach>
			</tr>
			<tr>
				<td  style="text-align: center;vertical-align: middle;"><b>Amazon+Other</b></td>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
			    <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><fmt:formatNumber value="${otherData['total'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
			    <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${otherData['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
				</c:forEach>
			</tr>
		</tbody>
	</table>
	
	</div>
	
	
	
	<ul class="nav nav-tabs">
		<li class="active"><a class="countryHref" href="#total" >Amazon Total</a></li>
		<li><a class="countryHref" href="#enTotal" key="en"><spring:message code="amazon_sales_english_country"/></a></li>
		<li><a class="countryHref" href="#unEnTotal" key="unEn"><spring:message code="amazon_sales_non_english_country"/></a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li><a  class="countryHref" href="#${dic.value}"  key="${dic.value}" >${dic.label}</a></li>
			</c:if>
		</c:forEach>	
		<%-- <c:forEach items="${otherData}" var="otherType">
		   <c:if test="${!fn:startsWith(otherType.key, '1')&&!fn:startsWith(otherType.key, 'amazonTotal')&&!fn:startsWith(otherType.key, '4')&&!fn:startsWith(otherType.key, '5')&&!fn:startsWith(otherType.key, '6')&&otherType.key ne 'total'}">
		      <li><a class="countryHref" href="#${otherType.key }"  key="${otherType.key }">${otherType.key eq '2-de'?'Vendor_DE':(otherType.key eq '2-com'?'Vendor_US':(otherType.key eq '3-com'?'Ebay_US':'Ebay_DE'))}</a></li>
		   </c:if>
		</c:forEach>   
		<li><a  class="countryHref" href="#4"  key="4" >Offline</a></li>
		<li><a  class="countryHref" href="#5"  key="5" >Website</a></li>
		<li><a  class="countryHref" href="#6"  key="6" >Check24</a></li> --%>
		 <li class="dropdown">
		   <a class="dropdown-toggle"  data-toggle="dropdown" href="#">Other Platform<b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
				   <c:forEach items="${otherData}" var="otherType">
									   <c:if test="${!fn:startsWith(otherType.key, '1')&&!fn:startsWith(otherType.key, 'amazonTotal')&&!fn:startsWith(otherType.key, '4')&&!fn:startsWith(otherType.key, '7')&&!fn:startsWith(otherType.key, '5')&&!fn:startsWith(otherType.key, '6')&&otherType.key ne 'total'}">
									      <li><a class="countryHref" href="#${otherType.key }"  key="${otherType.key }">${otherType.key eq '2-de'?'Vendor_DE':(otherType.key eq '2-com'?'Vendor_US':(otherType.key eq '3-com'?'Ebay_US':(otherOrder.key eq '2-uk'?'Vendor_UK':'Ebay_DE')))}</a></li>
									   </c:if>
								</c:forEach>   
								<li><a  class="countryHref" href="#4"  key="4" >Offline</a></li>
								<li><a  class="countryHref" href="#5-de"  key="5-de" >Website_DE</a></li>
								<li><a  class="countryHref" href="#5-com"  key="5-com" >Website_US</a></li>
								<li><a  class="countryHref" href="#6"  key="6" >Check24</a></li>
								<li><a  class="countryHref" href="#7"  key="7" >Other</a></li>
		    </ul>
	   </li>
	</ul>
	
	<div class="tab-content">
	
	<div id="total" class="tab-pane active">
	<div class="alert">
	  <button type="button" class="close" data-dismiss="alert">&times;</button>
	  
	  	<spring:message code="amazon_sales_pro_warn_info"/> &nbsp;&nbsp;&nbsp;<a  class="compare btn btn-success" target="_blank" href="#" ><spring:message code="amazon_sales_comparison"/></a>
	</div>
		<table id="totalTb" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
					
					<th style="width: 50px"></th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_order_date"/> </th>
					<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
					<th style="text-align: center;vertical-align: middle;" >${'1' eq saleReport.searchType?'Week':'Period'}</th>
					</c:if>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_volum"/>(${currencySymbol})</th>
					<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_volume"/>(${currencySymbol})</th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_promotion_volume_excluded"/>(${currencySymbol})</th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_percentage_of_promotion_volume"/></th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_quantity"/></th>
					<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_quantity"/></th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_promotion_quantity_excluded"/></th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_percentage_of_promotion_quantity"/></th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="sys_label_tips_operate"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
					<c:set var="date" value="${xAxis[(fn:length(xAxis)-i.count)]}" />	
					<tr>
						<td style="width: 20px;text-align: center;vertical-align: middle">
							<span>
								<input value="${date}" type="checkbox">
							</span>
						</td>
						
						<td style="text-align: center;vertical-align: middle;">${date}</td>
						<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
						<td style="text-align: center;vertical-align: middle;">${tip[date]}</td>
						</c:if>
						<td style="text-align: center;vertical-align: middle;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${data['de'][date].sales}<br/>US:${data['com'][date].sales}<br/>UK:${data['uk'][date].sales}<br/>FR:${data['fr'][date].sales}<br/>JP:${data['jp'][date].sales}<br/>IT:${data['it'][date].sales}<br/>ES:${data['es'][date].sales}<br/>CA:${data['ca'][date].sales}"><fmt:formatNumber pattern="#######.##" value="${data['total'][date].sales}" maxFractionDigits="2"  minFractionDigits="2"/> </a>
						</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${data['de'][date].sureSales}<br/>US:${data['com'][date].sureSales}<br/>UK:${data['uk'][date].sureSales}<br/>FR:${data['fr'][date].sureSales}<br/>JP:${data['jp'][date].sureSales}<br/>IT:${data['it'][date].sureSales}<br/>ES:${data['es'][date].sureSales}<br/>CA:${data['ca'][date].sureSales}"><fmt:formatNumber pattern="#######.##" value="${data['total'][date].sureSales}" maxFractionDigits="2"  minFractionDigits="2" /></a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="各国促销额 <br/>DE:${fns:roundUp(data['de'][date].sureSales-data['de'][date].realSales)}<br/>US:${fns:roundUp(data['com'][date].sureSales-data['com'][date].realSales)}<br/>UK:${fns:roundUp(data['uk'][date].sureSales-data['uk'][date].realSales)}<br/>FR:${fns:roundUp(data['fr'][date].sureSales-data['fr'][date].realSales)}<br/>JP:${fns:roundUp(data['jp'][date].sureSales-data['jp'][date].realSales)}<br/>IT:${fns:roundUp(data['it'][date].sureSales-data['it'][date].realSales)}<br/>ES:${fns:roundUp(data['es'][date].sureSales-data['es'][date].realSales)}<br/>CA:${fns:roundUp(data['ca'][date].sureSales-data['ca'][date].realSales)}"><fmt:formatNumber pattern="#######.##" value="${data['total'][date].realSales}" maxFractionDigits="2"  minFractionDigits="2" /></a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${data['total'][date].sureSales>0}">
								<span class="badge badge-info">
									${fns:roundUp((data['total'][date].sureSales-data['total'][date].realSales)*1000/data['total'][date].sureSales)}‰
								</span>
							</c:if>
						 </td>
						<td style="text-align: center;vertical-align: middle;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${data['de'][date].salesVolume}<br/>US:${data['com'][date].salesVolume}<br/>UK:${data['uk'][date].salesVolume}<br/>FR:${data['fr'][date].salesVolume}<br/>JP:${data['jp'][date].salesVolume}<br/>IT:${data['it'][date].salesVolume}<br/>ES:${data['es'][date].salesVolume}<br/>CA:${data['ca'][date].salesVolume}">${data['total'][date].salesVolume}</a>
						</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${data['de'][date].sureSalesVolume}<br/>US:${data['com'][date].sureSalesVolume}<br/>UK:${data['uk'][date].sureSalesVolume}<br/>FR:${data['fr'][date].sureSalesVolume}<br/>JP:${data['jp'][date].sureSalesVolume}<br/>IT:${data['it'][date].sureSalesVolume}<br/>ES:${data['es'][date].sureSalesVolume}<br/>CA:${data['ca'][date].sureSalesVolume}">${data['total'][date].sureSalesVolume}</a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="各国促销量 <br/>DE:${data['de'][date].sureSalesVolume-data['de'][date].realSalesVolume}<br/>US:${data['com'][date].sureSalesVolume-data['com'][date].realSalesVolume}<br/>UK:${data['uk'][date].sureSalesVolume-data['uk'][date].realSalesVolume}<br/>FR:${data['fr'][date].sureSalesVolume-data['fr'][date].realSalesVolume}<br/>JP:${data['jp'][date].sureSalesVolume-data['jp'][date].realSalesVolume}<br/>IT:${data['it'][date].sureSalesVolume-data['it'][date].realSalesVolume}<br/>ES:${data['es'][date].sureSalesVolume-data['es'][date].realSalesVolume}<br/>CA:${data['ca'][date].sureSalesVolume-data['ca'][date].realSalesVolume}">${data['total'][date].realSalesVolume}</a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${data['total'][date].sureSalesVolume>0}">
								<span class="badge badge-info">
									${fns:roundUp((data['total'][date].sureSalesVolume-data['total'][date].realSalesVolume)*1000/data['total'][date].sureSalesVolume)}‰
					    		</span>
					    	</c:if>
					    </td>
						<td style="text-align: center;vertical-align: middle;">
							<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_order"/></a> &nbsp;
                   			<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/order/promotionsList?country=&byTime=${saleReport.searchType}&dateStr=${date}"><spring:message code="amazon_sales_discount_order"/></a> &nbsp;
                   			<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=${saleReport.groupName }">Sku</a>  &nbsp;
                    		<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_product"/></a>&nbsp;
                    		<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/contrastSaleView?type=${saleReport.searchType}&endTime=${date}&startTime=&country=&currencyType=${saleReport.currencyType}&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_comparison"/></a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr class="count">
					<td></td>
					<td style="text-align: center;vertical-align: middle;">Total</td>
					<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
					  <td></td>
				    </c:if>
					<td tid="totalTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>
					<td tid="totalTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>	
					<td tid="totalTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>	
					<td style="text-align: center;vertical-align: middle;"></td>
					<td tid="totalTb" class="total" style="text-align: center;vertical-align: middle;">0</td>
					<td tid="totalTb" class="total" style="text-align: center;vertical-align: middle;">0</td>
					<td tid="totalTb" class="total" style="text-align: center;vertical-align: middle;">0</td>		
					<td style="text-align: center;vertical-align: middle;"></td>
					<td></td>
				</tr>
			</tfoot>
		</table>
		<div style="width:98%;border:1px solid #ccc;">
			<div id="totalChart" style="height:400px"></div>
		</div>
	</div>
	
	
	<!-- 英语国家汇总表格 -->
	<div id="enTotal"  class="hideCls tab-pane">
	<div class="alert">
	  <button type="button" class="close" data-dismiss="alert">&times;</button>
	 
	  	<spring:message code="amazon_sales_pro_warn_info"/> &nbsp;&nbsp;&nbsp;<a  class="compare btn btn-success" target="_blank" href="#" ><spring:message code="amazon_sales_comparison"/></a>
	
	</div>
		<table id="enTotalTb" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
						<th style="width: 50px"></th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_order_date"/> </th>
					<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
					<th style="text-align: center;vertical-align: middle;" >${'1' eq saleReport.searchType?'Week':'Period'}</th>
					</c:if>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_volum"/>(${currencySymbol})</th>
					<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_volume"/>(${currencySymbol})</th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_promotion_volume_excluded"/>(${currencySymbol})</th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_percentage_of_promotion_volume"/></th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_quantity"/></th>
					<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_quantity"/></th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_promotion_quantity_excluded"/></th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_percentage_of_promotion_quantity"/></th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="sys_label_tips_operate"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
					<c:set var="date" value="${xAxis[(fn:length(xAxis)-i.count)]}" />	
					<tr>
						<td style="width: 20px;text-align: center;vertical-align: middle">
							<span>
								<input value="${date}" type="checkbox">
							</span>
						</td>
						
						<td style="text-align: center;vertical-align: middle;">${date}</td>
						<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
						<td style="text-align: center;vertical-align: middle;">${tip[date]}</td>
						</c:if>
						<td style="text-align: center;vertical-align: middle;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="CA:${data['ca'][date].sales}<br/>UK:${data['uk'][date].sales}<br/>US:${data['com'][date].sales}"><fmt:formatNumber pattern="#######.##" value="${data['en'][date].sales}" maxFractionDigits="2"  minFractionDigits="2"/> </a>
						</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="CA:${data['ca'][date].sureSales}<br/>UK:${data['uk'][date].sureSales}<br/>US:${data['com'][date].sureSales}"><fmt:formatNumber pattern="#######.##" value="${data['en'][date].sureSales}" maxFractionDigits="2"  minFractionDigits="2" /></a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="各国促销额 <br/>CA:${fns:roundUp(data['ca'][date].sureSales-data['ca'][date].realSales)}<br/>UK:${fns:roundUp(data['uk'][date].sureSales-data['uk'][date].realSales)}<br/>US:${fns:roundUp(data['com'][date].sureSales-data['com'][date].realSales)}"><fmt:formatNumber pattern="#######.##" value="${data['en'][date].realSales}" maxFractionDigits="2"  minFractionDigits="2" /></a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${data['en'][date].sureSales>0}">
								<span class="badge badge-info">
									${fns:roundUp((data['en'][date].sureSales-data['en'][date].realSales)*1000/data['en'][date].sureSales)}‰
								</span>
							</c:if>
						 </td>
						<td style="text-align: center;vertical-align: middle;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="CA:${data['ca'][date].salesVolume}<br/>UK:${data['uk'][date].salesVolume}<br/>US:${data['com'][date].salesVolume}">${data['en'][date].salesVolume}</a>
						</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="CA:${data['ca'][date].sureSalesVolume}<br/>UK:${data['uk'][date].sureSalesVolume}<br/>US:${data['com'][date].sureSalesVolume}">${data['en'][date].sureSalesVolume}</a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="各国促销量 <br/>CA:${data['ca'][date].sureSalesVolume-data['ca'][date].realSalesVolume}<br/>UK:${data['uk'][date].sureSalesVolume-data['uk'][date].realSalesVolume}<br/>US:${data['com'][date].sureSalesVolume-data['com'][date].realSalesVolume}">${data['en'][date].realSalesVolume}</a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${data['en'][date].sureSalesVolume>0}">
								<span class="badge badge-info">
									${fns:roundUp((data['en'][date].sureSalesVolume-data['en'][date].realSalesVolume)*1000/data['en'][date].sureSalesVolume)}‰
					    		</span>
					    	</c:if>
					    </td>
						<td style="text-align: center;vertical-align: middle;">
							    <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=en&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_order"/></a> &nbsp;
								<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/order/promotionsList?country=en&byTime=${saleReport.searchType}&dateStr=${date}&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_discount_order"/></a> &nbsp;
                   				<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=en&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=${saleReport.groupName }">Sku</a>  &nbsp;
                    			<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=en&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_product"/></a>&nbsp;
                    			<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/contrastSaleView?type=${saleReport.searchType}&endTime=${date}&startTime=&country=en&currencyType=${saleReport.currencyType}&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_comparison"/></a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr class="count">
					<td></td>
					<td style="text-align: center;vertical-align: middle;">Total</td>
					<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
					  <td></td>
				    </c:if>
					<td tid="enTotalTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>
					<td tid="enTotalTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>	
					<td tid="enTotalTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>	
					<td style="text-align: center;vertical-align: middle;"></td>
					<td tid="enTotalTb" class="total" style="text-align: center;vertical-align: middle;">0</td>
					<td tid="enTotalTb" class="total" style="text-align: center;vertical-align: middle;">0</td>
					<td tid="enTotalTb" class="total" style="text-align: center;vertical-align: middle;">0</td>		
					<td style="text-align: center;vertical-align: middle;"></td>
					<td></td>
				</tr>
			</tfoot>
		</table>
		<div style="width:98%;border:1px solid #ccc;">
			<div id="enTotalChart" style="height:400px"></div>
		</div>
	</div>
	
	
	<!-- 非英语国家汇总表格 -->
	<div id="unEnTotal"  class="hideCls tab-pane">
	<div class="alert">
	  <button type="button" class="close" data-dismiss="alert">&times;</button>
	  <spring:message code="amazon_sales_pro_warn_info"/> &nbsp;&nbsp;&nbsp;<a  class="compare btn btn-success" target="_blank" href="#" ><spring:message code="amazon_sales_comparison"/></a>
	
	</div>
		<table id="unEnTotalTb" class="table table-striped table-bordered table-condensed">
			<thead>
				<tr>
						<th style="width: 50px"></th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_order_date"/> </th>
					<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
					<th style="text-align: center;vertical-align: middle;" >${'1' eq saleReport.searchType?'Week':'Period'}</th>
					</c:if>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_volum"/>(${currencySymbol})</th>
					<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_volume"/>(${currencySymbol})</th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_promotion_volume_excluded"/>(${currencySymbol})</th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_percentage_of_promotion_volume"/></th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_quantity"/></th>
					<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_quantity"/></th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_promotion_quantity_excluded"/></th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_percentage_of_promotion_quantity"/></th>
					<th style="text-align: center;vertical-align: middle;"><spring:message code="sys_label_tips_operate"/></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
					<c:set var="date" value="${xAxis[(fn:length(xAxis)-i.count)]}" />	
					<tr>
						<td style="width: 20px;text-align: center;vertical-align: middle">
							<span>
								<input value="${date}" type="checkbox">
							</span>
						</td>
						
						<td style="text-align: center;vertical-align: middle;">${date}</td>
						<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
						<td style="text-align: center;vertical-align: middle;">${tip[date]}</td>
						</c:if>
						<td style="text-align: center;vertical-align: middle;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${data['de'][date].sales}<br/>ES:${data['es'][date].sales}<br/>FR:${data['fr'][date].sales}<br/>IT:${data['it'][date].sales}<br/>JP:${data['jp'][date].sales}"><fmt:formatNumber pattern="#######.##" value="${data['unEn'][date].sales}" maxFractionDigits="2"  minFractionDigits="2"/> </a>
						</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${data['de'][date].sureSales}<br/>ES:${data['es'][date].sureSales}<br/>FR:${data['fr'][date].sureSales}<br/>IT:${data['it'][date].sureSales}<br/>JP:${data['jp'][date].sureSales}"><fmt:formatNumber pattern="#######.##" value="${data['unEn'][date].sureSales}" maxFractionDigits="2"  minFractionDigits="2" /></a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="各国促销额 <br/>DE:${fns:roundUp(data['de'][date].sureSales-data['de'][date].realSales)}<br/>ES:${fns:roundUp(data['es'][date].sureSales-data['es'][date].realSales)}<br/>FR:${fns:roundUp(data['fr'][date].sureSales-data['fr'][date].realSales)}<br/>IT:${fns:roundUp(data['it'][date].sureSales-data['it'][date].realSales)}<br/>JP:${fns:roundUp(data['jp'][date].sureSales-data['jp'][date].realSales)}"><fmt:formatNumber pattern="#######.##" value="${data['unEn'][date].realSales}" maxFractionDigits="2"  minFractionDigits="2" /></a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${data['unEn'][date].sureSales>0}">
								<span class="badge badge-info">
									${fns:roundUp((data['unEn'][date].sureSales-data['unEn'][date].realSales)*1000/data['unEn'][date].sureSales)}‰
								</span>
							</c:if>
						 </td>
						<td style="text-align: center;vertical-align: middle;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${data['de'][date].salesVolume}<br/>ES:${data['es'][date].salesVolume}<br/>FR:${data['fr'][date].salesVolume}<br/>IT:${data['it'][date].salesVolume}<br/>JP:${data['jp'][date].salesVolume}">${data['unEn'][date].salesVolume}</a>
						</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${data['de'][date].sureSalesVolume}<br/>ES:${data['es'][date].sureSalesVolume}<br/>FR:${data['fr'][date].sureSalesVolume}<br/>IT:${data['it'][date].sureSalesVolume}<br/>JP:${data['jp'][date].sureSalesVolume}">${data['unEn'][date].sureSalesVolume}</a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="各国促销量 <br/>DE:${data['de'][date].sureSalesVolume-data['de'][date].realSalesVolume}<br/>ES:${data['es'][date].sureSalesVolume-data['es'][date].realSalesVolume}<br/>FR:${data['fr'][date].sureSalesVolume-data['fr'][date].realSalesVolume}<br/>IT:${data['it'][date].sureSalesVolume-data['it'][date].realSalesVolume}<br/>JP:${data['jp'][date].sureSalesVolume-data['jp'][date].realSalesVolume}">${data['unEn'][date].realSalesVolume}</a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${data['unEn'][date].sureSalesVolume>0}">
								<span class="badge badge-info">
									${fns:roundUp((data['unEn'][date].sureSalesVolume-data['unEn'][date].realSalesVolume)*1000/data['unEn'][date].sureSalesVolume)}‰
					    		</span>
					    	</c:if>
					    </td>
						<td style="text-align: center;vertical-align: middle;">
							    <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=unEn&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_order"/></a> &nbsp;
								<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/order/promotionsList?country=unEn&byTime=${saleReport.searchType}&dateStr=${date}&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_discount_order"/></a> &nbsp;
                   				<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=unEn&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=${saleReport.groupName }">Sku</a>  &nbsp;
                    			<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=unEn&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_product"/></a>&nbsp;
                    			<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/contrastSaleView?type=${saleReport.searchType}&endTime=${date}&startTime=&country=unEn&currencyType=${saleReport.currencyType}&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_comparison"/></a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr class="count">
					<td></td>
					<td style="text-align: center;vertical-align: middle;">Total</td>
					<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
					  <td></td>
				    </c:if>
					<td tid="unEnTotalTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>
					<td tid="unEnTotalTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>	
					<td tid="unEnTotalTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>	
					<td style="text-align: center;vertical-align: middle;"></td>
					<td tid="unEnTotalTb" class="total" style="text-align: center;vertical-align: middle;">0</td>
					<td tid="unEnTotalTb" class="total" style="text-align: center;vertical-align: middle;">0</td>
					<td tid="unEnTotalTb" class="total" style="text-align: center;vertical-align: middle;">0</td>		
					<td style="text-align: center;vertical-align: middle;"></td>
					<td></td>
				</tr>
			</tfoot>
		</table>
		<div style="width:98%;border:1px solid #ccc;">
			<div id="unEnTotalChart" style="height:400px"></div>
		</div>
	</div>
	
	<c:forEach items="${fns:getDictList('platform')}" var="dic">
		<c:if test="${dic.value ne 'com.unitek'}">
			<div id="${dic.value}" class="hideCls tab-pane">
			<div class="alert">
			  <button type="button" class="close" data-dismiss="alert">&times;</button>
			<spring:message code="amazon_sales_pro_warn_info"/> &nbsp;&nbsp;&nbsp;<a  class="compare btn btn-success" target="_blank" href="#" ><spring:message code="amazon_sales_comparison"/></a>
	
			</div>
				<table id="${dic.value}Tb" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							<th style="width: 50px"></th>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_order_date"/> </th>
							<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<th style="text-align: center;vertical-align: middle;" >${'1' eq saleReport.searchType?'Week':'Period'}</th>
							</c:if>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_volum"/>(${currencySymbol})</th>
							<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_volume"/>(${currencySymbol})</th>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_promotion_volume_excluded"/>(${currencySymbol})</th>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_percentage_of_promotion_volume"/></th>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_quantity"/></th>
							<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_quantity"/></th>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_promotion_quantity_excluded"/></th>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_percentage_of_promotion_quantity"/></th>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="sys_label_tips_operate"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
						<c:set var="date" value="${xAxis[(fn:length(xAxis)-i.count)]}" />	
						<tr>
							<td style="width: 20px;text-align: center;vertical-align: middle">
								<span>
									<input value="${date}" type="checkbox">
								</span>
							</td>
							<td style="text-align: center;vertical-align: middle;">${date}</td>
								<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<td style="text-align: center;vertical-align: middle;">${tip[date]}</td>
							</c:if>
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${data[dic.value][date].sales >0}">
									<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="占总量(%) ${fns:roundUp(data[dic.value][date].sales*100/data['total'][date].sales)}%"><fmt:formatNumber pattern="#######.##" value="${data[dic.value][date].sales}" maxFractionDigits="2" minFractionDigits="2" /></a>
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
								<c:if test="${data[dic.value][date].sureSales >0}">
									<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="占总量(%) ${fns:roundUp(data[dic.value][date].sureSales*100/data['total'][date].sureSales)}%"><fmt:formatNumber pattern="#######.##" value="${data[dic.value][date].sureSales}" maxFractionDigits="2" minFractionDigits="2" /></a>
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${data[dic.value][date].realSales >0}">
									<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="占总量(%) ${fns:roundUp(data[dic.value][date].realSales*100/data['total'][date].realSales)}%"><fmt:formatNumber pattern="#######.##" value="${data[dic.value][date].realSales}" maxFractionDigits="2" minFractionDigits="2" /></a>
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${data[dic.value][date].sureSales >0}">
									<span class="badge badge-info">
										${fns:roundUp((data[dic.value][date].sureSales-data[dic.value][date].realSales)*1000/data[dic.value][date].sureSales)}‰
									</span>
								</c:if>
							</td>
							
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${data[dic.value][date].salesVolume>0}">
									<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="占总量(%) ${fns:roundUp(data[dic.value][date].salesVolume*100/data['total'][date].salesVolume)}%">${data[dic.value][date].salesVolume}</a>
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
								<c:if test="${data[dic.value][date].sureSalesVolume >0}">
									<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="占总量(%) ${fns:roundUp(data[dic.value][date].sureSalesVolume*100/data['total'][date].sureSalesVolume)}%">${data[dic.value][date].sureSalesVolume}</a>
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${data[dic.value][date].realSalesVolume >0}">
									<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="占总量(%) ${fns:roundUp(data[dic.value][date].realSalesVolume*100/data['total'][date].realSalesVolume)}%">${data[dic.value][date].realSalesVolume}</a>
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;">
								<span class="badge badge-info">
								<c:if test="${data[dic.value][date].sureSalesVolume >0}">
									<span class="badge badge-info">
										${fns:roundUp((data[dic.value][date].sureSalesVolume-data[dic.value][date].realSalesVolume)*1000/data[dic.value][date].sureSalesVolume)}‰
									</span>
								</c:if>
								</span>
							</td>
							<td style="text-align: center;vertical-align: middle;">
								<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=${dic.value}&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_order"/></a> &nbsp;
								<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/order/promotionsList?country=${dic.value}&byTime=${saleReport.searchType}&dateStr=${date}&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_discount_order"/></a> &nbsp;
                   				<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=${dic.value}&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=${saleReport.groupName }">Sku</a>  &nbsp;
                    			<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=${dic.value}&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_product"/></a>&nbsp;
                    			<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/contrastSaleView?type=${saleReport.searchType}&endTime=${date}&startTime=&country=${dic.value}&currencyType=${saleReport.currencyType}&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_comparison"/></a>
							</td>
						</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr class="count">
							<td></td>
							<td style="text-align: center;vertical-align: middle;">Total</td>
								<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<td></td>
							</c:if>
							<td tid="${dic.value}Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>
							<td tid="${dic.value}Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>		
							<td tid="${dic.value}Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>		
							<td style="text-align: center;vertical-align: middle;"></td>
							<td tid="${dic.value}Tb" class="total" style="text-align: center;vertical-align: middle;">0</td>
							<td tid="${dic.value}Tb" class="total" style="text-align: center;vertical-align: middle;">0</td>		
							<td tid="${dic.value}Tb" class="total" style="text-align: center;vertical-align: middle;">0</td>		
							<td style="text-align: center;vertical-align: middle;"></td>
							<td></td>
						</tr>
					</tfoot>
				</table>
				<div style="border:1px solid #ccc;width: 98%">
					<div id="${dic.value}Chart" style="height:400px;"></div>
				</div>
			</div>
		</c:if>
	</c:forEach>
	
      <!--  -->
      <c:forEach items="${otherData}" var="otherType">
		<c:if test="${!fn:startsWith(otherType.key, '1')&&!fn:startsWith(otherType.key, '4')&&!fn:startsWith(otherOrder.key, '5')&&otherType.key ne 'total'}">
			<div id="${otherType.key}" class="hideCls tab-pane">
				<table id="${otherType.key}Tb" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							
							
							<th style="width: 50px"></th>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_order_date"/> </th>
							<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<th style="text-align: center;vertical-align: middle;" >${'1' eq saleReport.searchType?'Week':'Period'}</th>
							</c:if>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_volum"/>(${currencySymbol})</th>
							<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_volume"/>(${currencySymbol})</th>
						
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_quantity"/></th>
							<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_quantity"/></th>
							
							<th style="text-align: center;vertical-align: middle;"><spring:message code="sys_label_tips_operate"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
						<c:set var="date" value="${xAxis[(fn:length(xAxis)-i.count)]}" />	
						<tr>
							<td style="width: 20px;text-align: center;vertical-align: middle">
								<span>
									<input value="${date}" type="checkbox">
								</span>
							</td>
							<td style="text-align: center;vertical-align: middle;">${date}</td>
								<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<td style="text-align: center;vertical-align: middle;">${tip[date]}</td>
							</c:if>
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${otherData[otherType.key][date].sales >0}">
									<fmt:formatNumber pattern="#######.##" value="${otherData[otherType.key][date].sales}" maxFractionDigits="2" minFractionDigits="2" />
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
								<c:if test="${otherData[otherType.key][date].sureSales >0}">
									<fmt:formatNumber pattern="#######.##" value="${otherData[otherType.key][date].sureSales}" maxFractionDigits="2" minFractionDigits="2" />
								</c:if>
							</td>
							
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${otherData[otherType.key][date].salesVolume>0}">
									${otherData[otherType.key][date].salesVolume}
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
								<c:if test="${otherData[otherType.key][date].sureSalesVolume >0}">
									${otherData[otherType.key][date].sureSalesVolume}
								</c:if>
							</td>
							
							<td style="text-align: center;vertical-align: middle;">
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=${otherType.key}&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_order"/></a> 
							   &nbsp;&nbsp;
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=${otherType.key}&lineType=${saleReport.groupName }">Sku</a>
                               &nbsp; &nbsp;
                               <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=${otherType.key}&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_product"/></a>
                            </td>
						</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td></td>
							<td style="text-align: center;vertical-align: middle;">Total</td>
								<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<td></td>
							</c:if>
							<td tid="${otherType.key}Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>
							<td tid="${otherType.key}Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>		
							<td tid="${otherType.key}Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>		
							<td tid="${otherType.key}Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>
							<td></td>
						</tr>
					</tfoot>
				</table>
			</div>
		</c:if>
		
	</c:forEach>
	  <div id="4" class="hideCls tab-pane">
				<table id="4Tb" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
								<th style="width: 50px"></th>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_order_date"/> </th>
							<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<th style="text-align: center;vertical-align: middle;" >${'1' eq saleReport.searchType?'Week':'Period'}</th>
							</c:if>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_volum"/>(${currencySymbol})</th>
							<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_volume"/>(${currencySymbol})</th>
						
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_quantity"/></th>
							<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_quantity"/></th>
							
							<th style="text-align: center;vertical-align: middle;"><spring:message code="sys_label_tips_operate"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
						<c:set var="date" value="${xAxis[(fn:length(xAxis)-i.count)]}" />	
						<tr>
							<td style="width: 20px;text-align: center;vertical-align: middle">
								<span>
									<input value="${date}" type="checkbox">
								</span>
							</td>
							<td style="text-align: center;vertical-align: middle;">${date}</td>
							<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<td style="text-align: center;vertical-align: middle;">${tip[date]}</td>
							</c:if>
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${otherData['4-de'][date].sales+otherData['4-com'][date].sales+otherData['4-cn'][date].sales >0}">
									<fmt:formatNumber pattern="#######.##" value="${otherData['4-de'][date].sales+otherData['4-com'][date].sales+otherData['4-cn'][date].sales}" maxFractionDigits="2" minFractionDigits="2" />
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
								<c:if test="${otherData['4-de'][date].sureSales+otherData['4-com'][date].sureSales+otherData['4-cn'][date].sureSales >0}">
									<fmt:formatNumber pattern="#######.##" value="${otherData['4-de'][date].sureSales+otherData['4-com'][date].sureSales+otherData['4-cn'][date].sureSales}" maxFractionDigits="2" minFractionDigits="2" />
								</c:if>
							</td>
							
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${otherData['4-de'][date].salesVolume+otherData['4-com'][date].salesVolume+otherData['4-cn'][date].salesVolume>0}">
									${otherData['4-de'][date].salesVolume+otherData['4-com'][date].salesVolume+otherData['4-cn'][date].salesVolume}
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
								<c:if test="${otherData['4-de'][date].sureSalesVolume+otherData['4-com'][date].sureSalesVolume+otherData['4-cn'][date].sureSalesVolume >0}">
									${otherData['4-de'][date].sureSalesVolume+otherData['4-com'][date].sureSalesVolume+otherData['4-cn'][date].sureSalesVolume}
								</c:if>
							</td>
							
							<td style="text-align: center;vertical-align: middle;">
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=4&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_order"/></a> 
							   &nbsp;&nbsp;
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=4&lineType=${saleReport.groupName }">Sku</a>
                               &nbsp; &nbsp;
                               <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=4&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_product"/></a>
                            </td>
						</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td></td>
							<td style="text-align: center;vertical-align: middle;">Total</td>
								<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<td></td>
							</c:if>
							<td tid="4Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>
							<td tid="4Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>		
							<td tid="4Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>		
							<td tid="4Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>
							<td></td>
						</tr>
					</tfoot>
				</table>
			</div>
	
	
	 <div id="5-de" class="hideCls tab-pane">
				<table id="5-deTb" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
								<th style="width: 50px"></th>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_order_date"/> </th>
							<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<th style="text-align: center;vertical-align: middle;" >${'1' eq saleReport.searchType?'Week':'Period'}</th>
							</c:if>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_volum"/>(${currencySymbol})</th>
							<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_volume"/>(${currencySymbol})</th>
						
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_quantity"/></th>
							<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_quantity"/></th>
							
							<th style="text-align: center;vertical-align: middle;"><spring:message code="sys_label_tips_operate"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
						<c:set var="date" value="${xAxis[(fn:length(xAxis)-i.count)]}" />	
						<tr>
							<td style="width: 20px;text-align: center;vertical-align: middle">
								<span>
									<input value="${date}" type="checkbox">
								</span>
							</td>
							<td style="text-align: center;vertical-align: middle;">${date}</td>
							<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<td style="text-align: center;vertical-align: middle;">${tip[date]}</td>
							</c:if>
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${otherData['5-de'][date].sales >0}">
									<fmt:formatNumber pattern="#######.##" value="${otherData['5-de'][date].sales}" maxFractionDigits="2" minFractionDigits="2" />
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
								<c:if test="${otherData['5-de'][date].sureSales>0}">
									<fmt:formatNumber pattern="#######.##" value="${otherData['5-de'][date].sureSales}" maxFractionDigits="2" minFractionDigits="2" />
								</c:if>
							</td>
							
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${otherData['5-de'][date].salesVolume>0}">
									${otherData['5-de'][date].salesVolume}
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
								<c:if test="${otherData['5-de'][date].sureSalesVolume >0}">
									${otherData['5-de'][date].sureSalesVolume}
								</c:if>
							</td>
							
							<td style="text-align: center;vertical-align: middle;">
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=5-de&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_order"/></a> 
							   &nbsp;&nbsp;
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=5-de&lineType=${saleReport.groupName }">Sku</a>
                               &nbsp; &nbsp;
                               <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=5-de&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_product"/></a>
                            </td>
						</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td></td>
							<td style="text-align: center;vertical-align: middle;">Total</td>
								<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<td></td>
							</c:if>
							<td tid="5-deTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>
							<td tid="5-deTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>		
							<td tid="5-deTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>		
							<td tid="5-deTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>
							<td></td>
						</tr>
					</tfoot>
				</table>
			</div>
			
			 <div id="5-com" class="hideCls tab-pane">
				<table id="5-comTb" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
								<th style="width: 50px"></th>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_order_date"/> </th>
							<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<th style="text-align: center;vertical-align: middle;" >${'1' eq saleReport.searchType?'Week':'Period'}</th>
							</c:if>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_volum"/>(${currencySymbol})</th>
							<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_volume"/>(${currencySymbol})</th>
						
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_quantity"/></th>
							<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_quantity"/></th>
							
							<th style="text-align: center;vertical-align: middle;"><spring:message code="sys_label_tips_operate"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
						<c:set var="date" value="${xAxis[(fn:length(xAxis)-i.count)]}" />	
						<tr>
							<td style="width: 20px;text-align: center;vertical-align: middle">
								<span>
									<input value="${date}" type="checkbox">
								</span>
							</td>
							<td style="text-align: center;vertical-align: middle;">${date}</td>
							<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<td style="text-align: center;vertical-align: middle;">${tip[date]}</td>
							</c:if>
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${otherData['5-com'][date].sales >0}">
									<fmt:formatNumber pattern="#######.##" value="${otherData['5-com'][date].sales}" maxFractionDigits="2" minFractionDigits="2" />
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
								<c:if test="${otherData['5-com'][date].sureSales>0}">
									<fmt:formatNumber pattern="#######.##" value="${otherData['5-com'][date].sureSales}" maxFractionDigits="2" minFractionDigits="2" />
								</c:if>
							</td>
							
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${otherData['5-com'][date].salesVolume>0}">
									${otherData['5-com'][date].salesVolume}
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
								<c:if test="${otherData['5-com'][date].sureSalesVolume >0}">
									${otherData['5-com'][date].sureSalesVolume}
								</c:if>
							</td>
							
							<td style="text-align: center;vertical-align: middle;">
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=5-com&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_order"/></a> 
							   &nbsp;&nbsp;
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=5-com&lineType=${saleReport.groupName }">Sku</a>
                               &nbsp; &nbsp;
                               <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=5-com&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_product"/></a>
                            </td>
						</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td></td>
							<td style="text-align: center;vertical-align: middle;">Total</td>
								<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<td></td>
							</c:if>
							<td tid="5-comTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>
							<td tid="5-comTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>		
							<td tid="5-comTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>		
							<td tid="5-comTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>
							<td></td>
						</tr>
					</tfoot>
				</table>
			</div>
			
			 <div id="6" class="hideCls tab-pane">
				<table id="6Tb" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
								<th style="width: 50px"></th>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_order_date"/> </th>
							<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<th style="text-align: center;vertical-align: middle;" >${'1' eq saleReport.searchType?'Week':'Period'}</th>
							</c:if>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_volum"/>(${currencySymbol})</th>
							<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_volume"/>(${currencySymbol})</th>
						
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_quantity"/></th>
							<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_quantity"/></th>
							
							<th style="text-align: center;vertical-align: middle;"><spring:message code="sys_label_tips_operate"/></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
						<c:set var="date" value="${xAxis[(fn:length(xAxis)-i.count)]}" />	
						<tr>
							<td style="width: 20px;text-align: center;vertical-align: middle">
								<span>
									<input value="${date}" type="checkbox">
								</span>
							</td>
							<td style="text-align: center;vertical-align: middle;">${date}</td>
							<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<td style="text-align: center;vertical-align: middle;">${tip[date]}</td>
							</c:if>
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${otherData['6-de'][date].sales >0}">
									<fmt:formatNumber pattern="#######.##" value="${otherData['6-de'][date].sales}" maxFractionDigits="2" minFractionDigits="2" />
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
								<c:if test="${otherData['6-de'][date].sureSales>0}">
									<fmt:formatNumber pattern="#######.##" value="${otherData['6-de'][date].sureSales}" maxFractionDigits="2" minFractionDigits="2" />
								</c:if>
							</td>
							
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${otherData['6-de'][date].salesVolume>0}">
									${otherData['6-de'][date].salesVolume}
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
								<c:if test="${otherData['6-de'][date].sureSalesVolume >0}">
									${otherData['6-de'][date].sureSalesVolume}
								</c:if>
							</td>
							
							<td style="text-align: center;vertical-align: middle;">
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=6&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_order"/></a> 
							   &nbsp;&nbsp;
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=6&lineType=${saleReport.groupName }">Sku</a>
                               &nbsp; &nbsp;
                               <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=6&lineType=${saleReport.groupName }"><spring:message code="amazon_sales_product"/></a>
                            </td>
						</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td></td>
							<td style="text-align: center;vertical-align: middle;">Total</td>
								<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<td></td>
							</c:if>
							<td tid="6Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>
							<td tid="6Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>		
							<td tid="6Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>		
							<td tid="6Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>
							<td></td>
						</tr>
					</tfoot>
				</table>
			</div>
			
			 <div id="7" class="hideCls tab-pane">
				<table id="7Tb" class="table table-striped table-bordered table-condensed">
					<thead>
						<tr>
							
							<th style="width: 50px"></th>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_order_date"/> </th>
							<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<th style="text-align: center;vertical-align: middle;" >${'1' eq saleReport.searchType?'Week':'Period'}</th>
							</c:if>
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_volum"/>(${currencySymbol})</th>
							<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_volume"/>(${currencySymbol})</th>
							
							<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_quantity"/></th>
							<th style="text-align: center;vertical-align: middle;background-color:#D2E9FF;"><spring:message code="amazon_sales_confirmed_quantity"/></th>
						
							<th style="text-align: center;vertical-align: middle;"><spring:message code="sys_label_tips_operate"/></th>
				
						</tr>
					</thead>
					<tbody>
						<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
						<c:set var="date" value="${xAxis[(fn:length(xAxis)-i.count)]}" />	
						<tr>
							<td style="width: 20px;text-align: center;vertical-align: middle">
								<span>
									<input value="${date}" type="checkbox">
								</span>
							</td>
							<td style="text-align: center;vertical-align: middle;">${date}</td>
							<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<td style="text-align: center;vertical-align: middle;">${tip[date]}</td>
							</c:if>
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${otherData['7-de'][date].sales+otherData['7-com'][date].sales+otherData['7-cn'][date].sales >0}">
									<fmt:formatNumber pattern="#######.##" value="${otherData['7-de'][date].sales+otherData['7-com'][date].sales+otherData['7-cn'][date].sales}" maxFractionDigits="2" minFractionDigits="2" />
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
								<c:if test="${otherData['7-de'][date].sureSales+otherData['7-com'][date].sureSales+otherData['7-cn'][date].sureSales >0}">
									<fmt:formatNumber pattern="#######.##" value="${otherData['7-de'][date].sureSales+otherData['7-com'][date].sureSales+otherData['7-cn'][date].sureSales}" maxFractionDigits="2" minFractionDigits="2" />
								</c:if>
							</td>
							
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${otherData['7-de'][date].salesVolume+otherData['7-com'][date].salesVolume+otherData['7-cn'][date].salesVolume>0}">
									${otherData['7-de'][date].salesVolume+otherData['7-com'][date].salesVolume+otherData['7-cn'][date].salesVolume}
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
								<c:if test="${otherData['7-de'][date].sureSalesVolume+otherData['7-com'][date].sureSalesVolume+otherData['7-cn'][date].sureSalesVolume >0}">
									${otherData['7-de'][date].sureSalesVolume+otherData['7-com'][date].sureSalesVolume+otherData['7-cn'][date].sureSalesVolume}
								</c:if>
							</td>
							
							<td style="text-align: center;vertical-align: middle;">
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=7&lineType=total"><spring:message code="amazon_sales_order"/></a> 
							   &nbsp;&nbsp;
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=7&lineType=total">SKU</a>
                               &nbsp; &nbsp;
                               <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=7&lineType=total"><spring:message code="amazon_sales_product"/></a>
                            </td>
						</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr>
							<td></td>
							<td style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_total"/></td>
							<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
							<td></td>
							</c:if>
							<td tid="7Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>
							<td tid="7Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>		
							<td tid="7Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>		
							<td tid="7Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>
							<td></td>
						</tr>
					</tfoot>
				</table>
			</div>
    
	</div>
	<!-- <div class="modal hide fade" id="tip">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3 style="color: red">提示</h3>
		</div>
		<div class="modal-body">
			<p style="font-size: 18px;font-weight: bold;">销售数据正在更新预计1分钟以后恢复，请稍后再试...</p>
		</div>
		<div class="modal-footer">
			<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
		</div>
	</div> -->
</body>
</html>
