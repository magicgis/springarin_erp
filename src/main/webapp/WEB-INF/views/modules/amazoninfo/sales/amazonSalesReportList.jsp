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
						 $(".otherPlatform").text("Website_DE");
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
				if("euTotal" == key){
					key = "eu";
				}
				
				$("input[name='country']").val(key);
				if(key==""||key==undefined||key==null){
					if($("#add").size()==0){
						
						$("#allExport").append("<li id='add'><a id='byDateCountry'><spring:message code="amazon_sales_data_all_countries"/></a></li>");
						$("#allExport").append("<li id='add1'><a id='byProductTypeAll'><spring:message code="amazon_sales_product_summary_all_countries"/></a></li>");
						$("#allExport").append("<li id='add2'><a id='byProductGroupTypeAll'><spring:message code="amazon_sales_product_line_all_countries"/></a></li> ");
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
				}else if('eu'==key){
					if(!(myChartEu.series)){
						myChartEu.setOption(optionEu);
						$("#euTotalChart").css("width",$("#euTotalChart").parent().parent().parent().width()-20);
						myChartEu.resize();
					}
				}else if('en'==key){
					if(!(myChartEn.series)){
						myChartEn.setOption(optionEn);
						$("#enTotalChart").css("width",$("#enTotalChart").parent().parent().parent().width()-20);
						myChartEn.resize();
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
			var myChart;var myChartEu;var optionEu;var myChartEn;var optionEn;
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
		                	selected: {'欧洲':false,'英语国家':false,<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">'${dic.label}':false,</c:if></c:forEach>'Vendor_DE':false,'Ebay_DE':false,'Ebay_US':false,'Offline':false,'Website_DE':false,'Website_US':false,'check24':false,'Other':false},
		                    data:['总计','欧洲','英语国家',<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">'${dic.label}',</c:if></c:forEach>'Vendor_DE','Ebay_DE','Ebay_US','Offline','Website_DE','Website_US','check24','Other']
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
							      name:'欧洲',
							      type:'line',
							      data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty data['eu'][x].sales?0:data['eu'][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
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
		            
			            myChartEu = ec.init(document.getElementById("euTotalChart"));
			            myChartEu.showLoading({
						    text: '正在努力的读取数据中...',    //loading话术
						});
						//ecahrts-----------------
			            optionEu = {
			            	title:{text:'EU <spring:message code="amazon_product_sales_statistics"/>',x:'center'},		
			                tooltip : {
			                    trigger: 'item'
			                },
			                legend: {
			                	y:30,
			                	selected: {'总计':false},
			                    data:['欧洲','总计']
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
									      name:'欧洲',
									      type:'line',
									      data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty data['eu'][x].sales?0:data['eu'][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
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
			            myChartEu.setOption(optionEu);
			            
			            
			            myChartEn = ec.init(document.getElementById("enTotalChart"));
			            myChartEn.showLoading({
						    text: '正在努力的读取数据中...',    //loading话术
						});
						//ecahrts-----------------
			            optionEn = {
			            	title:{text:'<spring:message code="amazon_sales_english_country"/> <spring:message code="amazon_product_sales_statistics"/>',x:'center'},		
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
		    	var num = (tr.find("td:eq(4)").text()-tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'5':'4'})").text())*100/tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'5':'4'})").text();
			    tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'6':'5'})").html("<span class=\"badge badge-info\">"+(num.toFixed(2)=="NaN"?"0":num.toFixed(2))+"%</span>");
			    num = (tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'8':'7'})").text()-tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'9':'8'})").text())*100/tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'9':'8'})").text();
			    tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'10':'9'})").html("<span class=\"badge badge-info\">"+(num.toFixed(2)=="NaN"?"0":num.toFixed(2))+"%</span>");
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
		    
		    $("#euTotalTb").dataTable({
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
          //<c:forEach items="${otherData}" var="dic" varStatus="i"><c:if test="${!fn:startsWith(dic.key, '1')&&!fn:startsWith(dic.key, '4')&&!fn:startsWith(dic.key, '7')&&dic.key ne 'total'}">
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
		     $("#byUS").click(function(){
		  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
						if(v=="ok"){
							$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportSalesByUs");
							$("#searchForm").submit();
							$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots");
						}
					},{buttonsFocus:1});
					top.$('.jbox-body .jbox-icon').css('top','55px');
			    });
			  		$("#byDate").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportOriginal");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    });
			  		$("#byDateCountry").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportByDateCountry");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    });
					$("#byProductCountry").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportByProductCountry");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    });
					$("#byProduct").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportStatistics");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    });
					$("#byProductType").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportByProductTypeCountry");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    });
					
					$("#byProductLineProduct").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportProductLineProduct");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    });
					
					$("#byProductTypeAll").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportByProductTypeAllCountry");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    });
					$("#byProductGroupTypeAll").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportByGroupTypeAllCountry");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    });
					$("#byProfit").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportTotalProfit");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    });
					$("#byProductGroupTypePart").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportByGroupTypePartCountry");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    });
					
					$("#byOperationData").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/amazonOperationalReport/byTimeExport2");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    });
					
					$("#byYear").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportYearSales?type=0");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots");
							}
						},{buttonsFocus:1});
						top.$('.jbox-body .jbox-icon').css('top','55px');
				    });
					//byMonth
                    $("#byMonth").click(function(){
			  			top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
							if(v=="ok"){
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportYearSales?type=1");
								$("#searchForm").submit();
								$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots");
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
							} else if("euTotal" == key){
								key = "eu";
							}else if("enTotal" == key){
								key = "en";
							}
							var param = {};
							param.country = key;
							param.type = '${saleReport.searchType}';
							param.endTime = $(cks.get(0)).val();
							param.currencyType = '${saleReport.currencyType}';
							param.lineType='total';
							if(cks.size()>1){
								param.startTime = $(cks.get(1)).val();
							}
							$(this).attr("href","${ctx}/amazoninfo/salesReprots/contrastSaleView?"+$.param(param));
							return true;
						}
					});
					
					$(".compare1").click(function(e){
						var $div = $(".tab-pane.active");
						var cks = $div.find("input[type='checkbox']:checked");
						if(cks.size()!=2){
							e.preventDefault();
							$.jBox.tip("请选择2个区间比较!", 'error');
							return false;
						}else{
							var param = {};
							param.searchType = '${saleReport.searchType}';
							var date2 = $(cks.get(0)).val();
							var date1 = $(cks.get(1)).val();
							if('${saleReport.searchType}'=='1'){
								param.date2 = date2.substr(0,4)+"-"+ date2.substr(4,2)+"-"+date2.substr(6,2);
								param.date1 = date1.substr(0,4)+"-"+ date1.substr(4,2)+"-"+date1.substr(6,2);
							}else{
								param.date2 = date2.substr(0,4)+"-"+ date2.substr(4,2);
								param.date1 = date1.substr(0,4)+"-"+ date1.substr(4,2);
							}

							$(this).attr("href","${ctx}/amazoninfo/amazonOperationalReport/exportCompareData?"+$.param(param));
							return true;
						}
					});
					
					$(".compare2").click(function(e){
						var $div = $(".tab-pane.active");
						var cks = $div.find("input[type='checkbox']:checked");
						if(cks.size()!=2){
							e.preventDefault();
							$.jBox.tip("请选择2个区间比较!", 'error');
							return false;
						}else{
							var param = {};
							param.searchType = '${saleReport.searchType}';
							if('${saleReport.searchType}'=='1'){
								param.date2 = date2.substr(0,4)+"-"+ date2.substr(4,2)+"-"+date2.substr(6,2);
								param.date1 = date1.substr(0,4)+"-"+ date1.substr(4,2)+"-"+date1.substr(6,2);
							}else{
								param.date2 = date2.substr(0,4)+"-"+ date2.substr(4,2);
								param.date1 = date1.substr(0,4)+"-"+ date1.substr(4,2);
							}
							$(this).attr("href","${ctx}/amazoninfo/amazonOperationalReport/exportCompareData2?"+$.param(param));
							return true;
						}
					});
					
					$("#showDetailRate").click(function(){
						var endData=$("#end").val();
						var params={};
						params.start=$("#start").val();
						params.end=endData;
						
						var cssVal=$("#rateShow").css("display");
						if(cssVal=="none"){
							$("#rateShow").css("display","block");
							if(flag==0){
								console.log("query");
								$.ajax({
								    type: 'post',
								    async:false,
								    url: '${ctx}/amazoninfo/salesReprots/getRateDetail' ,
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
												   	    tr+="<td style='text-align: center;vertical-align: middle;'><a href='#' style='color: #08c;' data-toggle='popover' data-html='true' rel='popover' data-content='"+info+"'>"+toDecimal(t8)+"%</a></td>";
												    }); 
												}
											        
											 });
											
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
	</script>
</head>
<body>
	<%-- <ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/salesReprots/lineList">总计</a></li>
		<c:forEach var="temp" items="${allLine}" varStatus="i">
		  <li ${temp.key eq saleReport.groupName?'class=active':''}><a href="${ctx}/amazoninfo/salesReprots/list?groupName=${temp.key}">${temp.value}</a></li>
		</c:forEach>  
	</ul> --%>
  <!--    --> 
	<div style="font-size: 25px; font-weight: bold; text-align: center;">
		<c:choose>
			<c:when test="${'USD' eq saleReport.currencyType}">
				Amazon ${'total' eq saleReport.groupName?'':allLine[saleReport.groupName] } <spring:message code="amazon_sales_statistics"/>(USD)
			</c:when>
			<c:otherwise>
				Amazon ${'total' eq saleReport.groupName?'':allLine[saleReport.groupName] } <spring:message code="amazon_sales_statistics"/>(EUR)
			</c:otherwise>
		</c:choose>
	</div>	
	<br/>
	<!--  -->
	
	   <div class="alert alert-info">
	     <c:if test="${empty keyList1}">
	      <strong><spring:message code="amazon_sales_bulletin_for_today"/>：</strong><spring:message code="amazon_sales_greetings"/>,${fns:getUser().name},
	      <span class="badge badge-success"><spring:message code="amazon_sales_data_updating_time"/>:<fmt:formatDate value="${lastUpdateTime}" pattern="yyyy-MM-dd HH:mm"/></span> .
	      </c:if>
	      
	      <c:if test="${not empty keyList1}">
	       <strong><spring:message code="amazon_sales_bulletin_for_today"/>：</strong><spring:message code="amazon_sales_greetings"/>,${fns:getUser().name},
	 <span class="badge badge-success"><spring:message code="amazon_sales_data_updating_time"/>:<fmt:formatDate value="${lastUpdateTime}" pattern="yyyy-MM-dd HH:mm"/></span> .
	  <c:set  var='auto' value="${realList1[fns:getDate('yyyyMMdd')]['totalAvg'].autoDayGoal-realList1[fns:getDate('yyyyMMdd')]['totalAvg'].dayGoal}"/>
	  <c:if test="${not empty countryMonthGoal1 }" >
	 <c:if test="${realList1[fns:getDate('yyyyMMdd')]['totalAvg'].addUpSales<countryMonthGoal1 }">
		   <spring:message code="amazon_sales_dynamic_target"/>&nbsp;&nbsp;<strong><font size="3" color="${auto>0?'red':'green'}"><fmt:formatNumber maxFractionDigits="1"  value="${realList1[fns:getDate('yyyyMMdd')]['totalAvg'].autoDayGoal}" pattern="#0.0"/>${currencySymbol}</font>
		 </strong>.
	 </c:if>
	 <c:if test="${fns:endsWith(fns:getDate('yyyyMMdd'),'01')}">
	    <spring:message code="amazon_sales_forecast_target"/>&nbsp;&nbsp;<strong><font size="3" color="green"><fmt:formatNumber maxFractionDigits="1"  value="${realList1[fns:getDate('yyyyMMdd')]['totalAvg'].dayGoal}" pattern="#0.0"/>${currencySymbol}</font>
	 </strong>.
	 </c:if>
	  <c:if test="${!fns:endsWith(fns:getDate('yyyyMMdd'),'01')}">
	       <c:set var="sale1" value="${realList1[fns:getBeforeDate('yyyyMMdd')]['totalAvg'].addUpSales}" />
		    <c:set var="sale2" value="${realList1[fns:getBeforeDate('yyyyMMdd')]['totalAvg'].addUpGoal}" />
		    <c:set var="conf1" value="${realList1[fns:getBeforeDate('yyyyMMdd')]['totalAvg'].addUpGoal*100/countryMonthGoal1}" />
		    <c:set var="conf2" value="${realList1[fns:getBeforeDate('yyyyMMdd')]['totalAvg'].addUpSales*100/countryMonthGoal1}" />
		    <c:set var="realGoal" value="${realList1[fns:getBeforeDate('yyyyMMdd')]['totalAvg'].addUpGoal-realList1[fns:getBeforeDate('yyyyMMdd')]['totalAvg'].addUpSales}" />
	        <c:set var="rearch" value="0" />
	    <%--     <c:set var="showGoal" value="${realGoal+realList1[fns:getDate('yyyyMMdd')]['totalAvg'].dayGoal}" />
	        --%> 
	        <%--   <c:if test="${showGoal>0 }">     --%>
		          <spring:message code="amazon_sales_forecast_target"/>&nbsp;&nbsp;<strong>
		        <fmt:formatNumber maxFractionDigits="1"  value="${realList1[fns:getDate('yyyyMMdd')]['totalAvg'].dayGoal}" pattern="#0.0"/>${currencySymbol}
		     </strong>.
	   <%--   </c:if>     --%>
	        <c:forEach items="${fns:getDictList('platform')}" var="dic" >
					      <c:if test="${dic.value ne 'com.unitek'}">
					          <c:if test="${realList1[fns:getBeforeDate('yyyyMMdd')][dic.value].rate>=100}"><font color="green">
					             <c:set var="rearch" value="1" />
					          </font></c:if>
					      </c:if>
			</c:forEach>
		     
		   
		  <br/>  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;
		 <spring:message code="amazon_sales_until_yesterday"/>,
		    <c:if test="${rearch=='1' }">
		   	     <spring:message code="amazon_sales_accumulated_sales_standard"/>：<c:forEach items="${fns:getDictList('platform')}" var="dic" >
					      <c:if test="${dic.value ne 'com.unitek'}">
					          <c:if test="${realList1[fns:getBeforeDate('yyyyMMdd')][dic.value].rate>=100}"><font color="green" size=3>
					           <strong> ${dic.label}&nbsp;&nbsp;</strong>
					          </font></c:if>
					      </c:if>
					   </c:forEach>,</c:if><br/>  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;
		   <spring:message code="amazon_sales_accumulated_sales"/>&nbsp;&nbsp;<strong><font  size="3"><fmt:formatNumber maxFractionDigits="1"  value="${sale2 }" pattern="#0.0"/>${currencySymbol}</font></strong>&nbsp;&nbsp;,
		  <spring:message code="amazon_sales_accumulated_sales_rete"/>&nbsp;&nbsp;<strong><font  size="3"><fmt:formatNumber maxFractionDigits="1"  value="${conf1}" pattern="#0.0"/>%</font></strong>&nbsp;&nbsp;;
		   <spring:message code="amazon_sales_real_accumulated_sales"/>&nbsp;&nbsp;<c:if test="${sale1>=sale2 }"><strong><font color="green" size="3"><fmt:formatNumber maxFractionDigits="1"  value="${sale1 }" pattern="#0.0"/>${currencySymbol}</font></strong></c:if>
		   <c:if test="${sale1<sale2 }"><strong><font color="red" size="3"><fmt:formatNumber maxFractionDigits="1"  value="${sale1 }" pattern="#0.0"/>${currencySymbol}</font></strong></c:if>&nbsp;&nbsp;,
		   
		   
		    <spring:message code="amazon_sales_real_accumulated_sales_rate"/>&nbsp;&nbsp;
		     <c:if test="${conf1>conf2 }"><strong><font color="red" size="3"><fmt:formatNumber maxFractionDigits="1"  value="${conf2 }" pattern="#0.0"/>%</font></strong></c:if>
		     <c:if test="${conf1<=conf2 }"><strong><font color="green" size="3"><fmt:formatNumber maxFractionDigits="1"  value="${conf2 }" pattern="#0.0"/>%</font></strong></c:if>&nbsp;&nbsp;,
		     
		 <strong>
		   <br/>  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;
		   <c:if test="${sale1>=countryMonthGoal1}"><font style="color: #c09853" size="3"><spring:message code="amazon_sales_good1"/></font></c:if>
		   <c:if test="${sale1<countryMonthGoal1 }">
			   <c:if test="${conf1>conf2 }"><font color="red" size="3"><spring:message code="amazon_sales_behind"/></font></c:if> 
			   <c:if test="${conf1<=conf2 }"><font color="green" size="3"><spring:message code="amazon_sales_good2"/></font></c:if>
		     </c:if>
		 </strong>
	  </c:if> 
	   <spring:message code="amazon_sales_month_goal"/>&nbsp;&nbsp;<font size="3"><strong>${countryMonthGoal1 }${currencySymbol}</strong></font>&nbsp;&nbsp;,
	<%--    预计可完成&nbsp;&nbsp;<font size="3" color="${countryMonthGoal1*realList1[fns:getBeforeDate('yyyyMMdd')]['totalAvg'].rate/100>=countryMonthGoal1?'green':'red'}">
	   <strong><fmt:formatNumber maxFractionDigits="2"  value="${countryMonthGoal1*realList1[fns:getBeforeDate('yyyyMMdd')]['totalAvg'].rate/100}" pattern="#0.00"/>$</strong></font>&nbsp;&nbsp;,
	   --%>
	 
	 <c:set value="${sale1/(realList1[fns:getBeforeDate('yyyyMMdd')]['totalAvg'].addDayWeight/realList1[fns:getBeforeDate('yyyyMMdd')]['totalAvg'].totalWeight)}" var='forecastMoney'/>
	 <c:if test="${forecastMoney>=0&&day>='03'}">
	        <spring:message code="amazon_sales_expected_volume"/>&nbsp;&nbsp;<font size="3" color="${sale1/(realList1[fns:getBeforeDate('yyyyMMdd')]['totalAvg'].addDayWeight/realList1[fns:getBeforeDate('yyyyMMdd')]['totalAvg'].totalWeight)>=countryMonthGoal1?'green':'red'}">
	   <strong><fmt:formatNumber maxFractionDigits="1"  value="${forecastMoney }" pattern="#0.0"/>${currencySymbol}</strong></font>&nbsp;&nbsp;,
	 </c:if>
	 
	<c:set value="${sale1*100/(realList1[fns:getBeforeDate('yyyyMMdd')]['totalAvg'].addDayWeight/realList1[fns:getBeforeDate('yyyyMMdd')]['totalAvg'].totalWeight)/countryMonthGoal1}" var='forecastRate'/>
	 <c:if test="${forecastRate>=0&&day>='03' }">
	 <spring:message code="amazon_sales_forecast_rate"/>&nbsp;&nbsp;<font size="3"  color="${sale1/(realList1[fns:getBeforeDate('yyyyMMdd')]['totalAvg'].addDayWeight/realList1[fns:getBeforeDate('yyyyMMdd')]['totalAvg'].totalWeight)>=countryMonthGoal1?'green':'red'}"><strong><fmt:formatNumber maxFractionDigits="1"  value="${forecastRate }" pattern="#0.0"/>%</strong></font>&nbsp;&nbsp;.
	 </c:if>  
	  
		 &nbsp;&nbsp;
			<font color="red" size="2"><spring:message code="amazon_sales_exchange_rate_info"/> </font>
			</c:if>
	      </c:if>
	       <shiro:hasPermission name="amazoninfo:sale:accountBalance">
	        <br/>
	        <b>亚马逊账户余额(对应国家货币单位):</b> <br/>
	            <c:forEach var="temp" items="${balance}">
	                <b>${temp.key}:</b><fmt:formatNumber value="${balance[temp.key]}" />&nbsp;&nbsp;
	            </c:forEach>
			
			</shiro:hasPermission>	
		   <div id="showRate" style="position:relative; white-space:nowrap; overflow:hidden; height:20px;margin-left:50px;margin-right:250px;">
		      <div id="noticeList" style="position:absolute; top:0; height:20px;"><strong><spring:message code="amazon_sales_exchange_rate"/>：</strong>
		      	<%--
			       <span>1&nbsp;USD=<fmt:formatNumber maxFractionDigits="3"  value="${change['usdToCny'] }" pattern="#0.000"/>&nbsp;CNY</span>&nbsp;&nbsp;
			       <span>1&nbsp;JPY=<fmt:formatNumber maxFractionDigits="3"  value="${change['jpy'] }" pattern="#0.000"/>&nbsp;USD</span>&nbsp;&nbsp;
			       <span>1&nbsp;GBP=<fmt:formatNumber maxFractionDigits="3"  value="${change['gbp'] }" pattern="#0.000"/>&nbsp;USD</span>&nbsp;&nbsp;
			       <span>1&nbsp;EUR=<fmt:formatNumber maxFractionDigits="3"  value="${change['eur'] }" pattern="#0.000"/>&nbsp;USD</span>&nbsp;&nbsp;
			       <span>1&nbsp;CAD=<fmt:formatNumber maxFractionDigits="3"  value="${change['cad'] }" pattern="#0.000"/>&nbsp;USD</span>&nbsp;&nbsp;
			       <span>1&nbsp;MXN=<fmt:formatNumber maxFractionDigits="3"  value="${change['mxn'] }" pattern="#0.000"/>&nbsp;USD</span> --%>
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
<form:form id="searchForm" modelAttribute="saleReport" action="${ctx}/amazoninfo/salesReprots" method="post" class="form-search">
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
		
	           <spring:message code="amazon_sales_product_line"/>:<select id="typeGroup" style="width: 100px" name="groupName">
				<option value="">--All--</option>
				<option value="unGrouped" ${'unGrouped' eq fn:trim(saleReport.groupName)?'selected':''}>UnGrouped</option>
				<c:forEach items="${groupType}" var="groupType">
					<option value="${groupType.id}" ${fn:trim(groupType.id) eq fn:trim(saleReport.groupName)?'selected':''}>${groupType.name}</option>			
				</c:forEach>
			</select>
		<%-- <input type="hidden" value="${saleReport.groupName }" name="groupName" /> --%>
		<select name="productType" id="productType" style="width: 100px">
			<option value="">--All--</option>
			<%-- <c:forEach items="${typesAll}" var="it">
				<option value="${it}" ${it eq saleReport.productType?'selected':''}>${it}</option>
			</c:forEach> --%>
		</select>
	
		<input id="searchType" name="searchType" type="hidden" value="${saleReport.searchType}" />
		<input name="country" type="hidden" value="${saleReport.country}"/>
		
		
		<span style="float: center;">
		<label></label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="start" value="<fmt:formatDate value="${saleReport.start}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
		&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${saleReport.end}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code="sys_but_search"/>"/>
		&nbsp;
	            	<div class="btn-group">
						   <button type="button" class="btn btn-primary"><spring:message code="sys_but_export"/></button>
						   <button type="button" class="btn btn-primary dropdown-toggle"  data-toggle="dropdown">
						      <span class="caret"></span>
						      <span class="sr-only"></span>
						   </button>
						   <ul class="dropdown-menu" id="allExport">
						      <li><a  id="byDate"><spring:message code="amazon_sales_data_in_page"/></a></li>
						      <li><a id="byProductCountry"><spring:message code="amazon_sales_products_overall"/></a></li>
						      <li><a id="byProductType"><spring:message code="amazon_sales_product_summary"/></a></li> 
						      <li><a id="byProduct"><spring:message code="amazon_sales_product_details"/></a></li>
						      <li><a id="byOperationData"><spring:message code="amazon_sales_sales_data"/></a></li>
						      <shiro:hasPermission name="psi:inventory:stockPriceView">
						         <li><a id="byProfit"><spring:message code="amazon_sales_profit_statistics"/></a></li>
						      </shiro:hasPermission>
						      <li id='add'><a id="byDateCountry"><spring:message code="amazon_sales_data_all_countries"/></a></li> 
						      <li id='add1'><a id="byProductTypeAll"><spring:message code="amazon_sales_product_summary_all_countries"/></a></li> 
						      <li id='add2'><a id="byProductGroupTypeAll"><spring:message code="amazon_sales_product_line_all_countries"/></a></li> 
						      <li><a  id="byYear">Product By Year</a></li>
						      <li><a  id="byMonth">Product By Month</a></li>
						      <li><a  id="byUS">Export By US States</a></li>
						      <li><a  id="byProductLineProduct">Product By Country</a></li>
						      
						   </ul>
				 </div>
		</span>
		</div>
	</form:form>
	<%--隐藏预测完成率信息<c:if test="${not empty realList1}">
	<div class="accordion-heading" >
		<a id="showDetailRate" ><b><spring:message code="amazon_sales_completion_rate"/> ${formatDate} </b></a>
	</div>
	</c:if> --%>
	<div  style="display: none" id="rateShow">
	<table id="rate" class="desc table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_forecast_completion_rate"/></th>
				<th style="text-align: center;vertical-align: middle;">Total</th>
				<c:forEach items="${fns:getDictList('platform')}" var="dic">
					   <c:if test="${dic.value ne 'com.unitek'&&dic.value ne 'mx'}">
						  <th style="text-align: center;vertical-align: middle;">${dic.label}</th>
					  </c:if>
				</c:forEach>
				
			</tr>
		</thead>
		<tbody>
			<c:forEach var="temp" items="${keyList}" varStatus="i">
			<%-- 	<tr ${(fns:endsWith(temp,'10')||fns:endsWith(temp,'20')||fns:endsWith(temp,'30'))?'style=background-color:#D2E9FF;':''}> --%>
			 <tr ${(fns:getBeforeDate('yyyyMMdd') eq temp)?'style=background-color:#D2E9FF;':''}> 
				 <td style="text-align: center;vertical-align: middle;">${temp }</td>
				 <td style="text-align: center;vertical-align: middle;">
				   <a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="月权重 ${realList[temp]['totalAvg'].totalWeight}<br/>日销售额  ${realList[temp]['totalAvg'].sales}<br/>累计销售额  ${realList[temp]['totalAvg'].addUpSales}<br/>日目标 ${realList[temp]['totalAvg'].dayGoal}<br/>累计日目标  ${realList[temp]['totalAvg'].addUpGoal}<br/>动态日目标  ${realList[temp]['totalAvg'].autoDayGoal}<br/>日权重 ${realList[temp]['totalAvg'].dayWeight}"><fmt:formatNumber maxFractionDigits="1"  value="${realList[temp]['totalAvg'].rate }" pattern="#0.0"/>${not empty realList[temp]['totalAvg'].rate?'%':'' }</a>
				</td>
				  <c:forEach items="${fns:getDictList('platform')}" var="dic">
					   <c:if test="${dic.value ne 'com.unitek'}">
					       <td style="text-align: center;vertical-align: middle;">
					         <a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="月权重 ${realList[temp][dic.value].totalWeight}<br/>日销售额  ${realList[temp][dic.value].sales}<br/>累计销售额  ${realList[temp][dic.value].addUpSales}<br/>日目标 ${realList[temp][dic.value].dayGoal}<br/>累计日目标  ${realList[temp][dic.value].addUpGoal}<br/>动态日目标  ${realList[temp][dic.value].autoDayGoal}<br/>日权重 ${realList[temp][dic.value].dayWeight}"><fmt:formatNumber maxFractionDigits="1"  value="${realList[temp][dic.value].rate }" pattern="#0.0"/>${not empty realList[temp][dic.value].rate?'%':'' }</a>
					       </td>
					  </c:if>
				</c:forEach>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	</div>
	<table class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;" rowspan="2"><spring:message code="amazon_sales_rank"/></th>
				<th style="text-align: center;vertical-align: middle;" rowspan="2"><spring:message code="amazon_sales_country"/></th>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
				<c:if test="${not empty realList1&& not empty countryMonthGoal1}">
					    <c:if test="${saleReport.searchType=='1'&&day>='03'&& fns:getDate('yyyyMM') eq fns:getDateByPattern(saleReport.end,'yyyyMM')}">
					    <th colspan="4" style="text-align: center;vertical-align: middle;
					      ${i.index==1?'color: #08c;':''}">
						  ${xAxis[fn:length(xAxis)-i.index]}
						  </th>
						</c:if>	
					
						 <c:if test="${saleReport.searchType!='1'||day<'03'||fns:getDate('yyyyMM') ne fns:getDateByPattern(saleReport.end,'yyyyMM')}"><th colspan="3" style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
						  ${xAxis[fn:length(xAxis)-i.index]}
						  </th>
						</c:if>		
				</c:if>
				<c:if test="${empty realList1}">		
					<th colspan="3" style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
						  ${xAxis[fn:length(xAxis)-i.index]}
				    </th>
				</c:if>			
				</c:forEach>
			</tr>
			<tr>
			  	<c:forEach begin="1" end="3" step="1" varStatus="i">
					<th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><spring:message code="amazon_sales_volum"/>(${currencySymbol})</th>
					<th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><spring:message code="amazon_sales_quantity"/></th>
					<th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">Avg. Sales(${currencySymbol})</th>		
					<c:if test="${not empty realList1&& not empty countryMonthGoal1&& fns:getDate('yyyyMM') eq fns:getDateByPattern(saleReport.end,'yyyyMM') }">
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
		    <tr>
				<td colspan='2' style="text-align: center;vertical-align: middle;"><b><spring:message code="amazon_sales_english_country"/></b></td>
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
			 <%--  <c:if test="${'mx' ne temp.country}"> --%>
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
				<%-- </c:if> --%>
			</c:forEach>
			<!-- Amazon欧洲合计 -->
			<tr>
				<td colspan='2' style="text-align: center;vertical-align: middle;"><b>Amazon EU</b></td>
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
				<td colspan='2' style="text-align: center;vertical-align: middle;"><b>Amazon Total</b></td>
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
	
	<div class="accordion-heading" >
		<a class="accordion-toggle" data-toggle="collapse" data-parent="#accordion2" href="#otherThreeSales"><b>${xAxis[fn:length(xAxis)-1]}<spring:message code="amazon_sales_volume_other_marketplaces"/>(${currencySymbol }):<fmt:formatNumber value="${otherData['total'][xAxis[fn:length(xAxis)-1]].sales-data['total'][xAxis[fn:length(xAxis)-1]].sales}" maxFractionDigits="2" minFractionDigits="2" />,<spring:message code="amazon_sales_here_for_details"/></b></a>
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
			      <c:if test="${!fn:startsWith(otherOrder.key, '1')&&!fn:startsWith(otherOrder.key, 'amazonTotal')&&!fn:startsWith(otherOrder.key, '5')&&!fn:startsWith(otherOrder.key, '6')&&!fn:startsWith(otherOrder.key, '4')&&!fn:startsWith(otherOrder.key, '7')&&otherOrder.key!='total' }"> 
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
	<c:if test="${'1' eq saleReport.searchType }">
	<div id="returnGoodsRate">
		<table id="returnRate" class="table table-striped table-bordered table-condensed">
			<thead><tr>
					<th style="text-align: center;vertical-align: middle;" colspan="8">
						<%-- 上月退货率排名(月销量小于30的产品不计算)--%>
						<font style="color:red">
							<spring:message code="amazon_sales_return_goods_rate"/>
						</font>
						<a target="_blank" href="${ctx }/amazoninfo/returnGoods/differentialReturnCountList">more...</a>
					</th>
				</tr>
				<tr>
					<th style="text-align: center;vertical-align: middle;">
						<spring:message code="amazon_sales_country"/>
					</th>
					<c:forEach begin="1" end="7" step="1" varStatus="i">
						<th style="text-align: center;vertical-align: middle;">
							<span class="badge badge-${i.index<4?'important':'success'}">${i.index}</span>
						</th>
					</c:forEach>
				</tr>
			</thead>
			<tbody>
				<tr>
					<c:forEach items="${fns:getDictList('platform')}" var="dic">
						<c:if test="${dic.value ne 'com.unitek' && dic.value ne 'mx'}">
						<td style="text-align: center;vertical-align: middle;">${dic.label}</td>
						<c:forEach begin="1" end="7" step="1" varStatus="i">
								<c:set var="returnName" value="${returnRankMap[dic.value][i.index-1].key}"></c:set>
								<td style="text-align: center;vertical-align: middle;">
								<c:if test="${not empty returnName }">
								<a target="_blank" href="${ctx }/psi/psiInventory/productInfoDetail?productName=${returnName}"><span style="color:${repeatProduct[returnName]>1?'red':'#08c'}">${fns:getModelAndColor(returnName)}</span></a>&nbsp;
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="${i.index==1?'right':'left' }" rel="popover" 
									data-content="
										<spring:message code="sys_menu_saleV"/>:<fmt:formatNumber value="${sureQuantity[dic.value][returnName] }"></fmt:formatNumber><br/>
										<spring:message code="amazon_sales_return"/>:<fmt:formatNumber value="${retrunQuantity[dic.value][returnName]['total'] }"></fmt:formatNumber><br/>
										<c:if test="${not empty returnGoods[dic.value][returnName]['DEFECTIVE'] }">
											DEFECTIVE:<fmt:formatNumber type="percent" maxFractionDigits="2" value="${returnGoods[dic.value][returnName]['DEFECTIVE'] }"></fmt:formatNumber><br/>
										</c:if>
										<c:if test="${not empty returnGoods[dic.value][returnName]['QUALITY_UNACCEPTABLE'] }">
											QUALITY_UNACCEPTABLE:<fmt:formatNumber type="percent" maxFractionDigits="2" value="${returnGoods[dic.value][returnName]['QUALITY_UNACCEPTABLE'] }"></fmt:formatNumber><br/>
										</c:if>
										<c:if test="${not empty returnGoods[dic.value][returnName]['NOT_COMPATIBLE'] }">
											NOT_COMPATIBLE:<fmt:formatNumber type="percent" maxFractionDigits="2" value="${returnGoods[dic.value][returnName]['NOT_COMPATIBLE'] }"></fmt:formatNumber><br/>
										</c:if>
										<c:if test="${not empty returnGoods[dic.value][returnName]['NOT_AS_DESCRIBED'] }">
											NOT_AS_DESCRIBED:<fmt:formatNumber type="percent" maxFractionDigits="2" value="${returnGoods[dic.value][returnName]['NOT_AS_DESCRIBED'] }"></fmt:formatNumber>
										</c:if>
										">
									<fmt:formatNumber value="${returnRankMap[dic.value][i.index-1].value * 100}" maxFractionDigits="2" />%
								</a></c:if>
								</td>
						</c:forEach>
					</tr>
					</c:if>
				</c:forEach>
			</tbody>
		</table>
	</div>
	</c:if>
	
	<ul class="nav nav-tabs">
		<li class="active"><a class="countryHref" href="#total" >Amazon Total</a></li>
		<li><a class="countryHref" href="#euTotal" key="euTotal">EU</a></li>
		<li><a class="countryHref" href="#enTotal" key="en"><spring:message code="amazon_sales_english_country"/></a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li><a  class="countryHref" href="#${dic.value}"  key="${dic.value}" >${dic.label}</a></li>
			</c:if>
		</c:forEach>	
		 <li class="dropdown">
		   <a class="dropdown-toggle"  data-toggle="dropdown" href="#"><span class='otherPlatform'>Other Platform</span><b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
				   <c:forEach items="${otherData}" var="otherType">
									   <c:if test="${!fn:startsWith(otherType.key, '1')&&!fn:startsWith(otherType.key, 'amazonTotal')&&!fn:startsWith(otherType.key, '4')&&!fn:startsWith(otherType.key, '5')&&!fn:startsWith(otherType.key, '6')&&!fn:startsWith(otherType.key, '7')&&otherType.key ne 'total'}">
									      <li><a class="countryHref" href="#${otherType.key }"  key="${otherType.key }">${otherType.key eq '2-de'?'Vendor_DE':(otherType.key eq '2-com'?'Vendor_US':(otherType.key eq '3-com'?'Ebay_US':(otherType.key eq '2-uk'?'Vendor_UK':'Ebay_DE')))}</a></li>
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
	  	&nbsp;&nbsp;
	  	<a  class="compare1 btn btn-success"><spring:message code="amazon_sales_comparison1"/></a>
	  	&nbsp;&nbsp;
	  	<a  class="compare2 btn btn-success" ><spring:message code="amazon_sales_comparison2"/></a>
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
					<c:if test="${saleReport.searchType eq '9'}">
					  <th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_order"/></th>
					</c:if>  
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
									${fns:roundUp((data['total'][date].sureSales-data['total'][date].realSales)*100/data['total'][date].sureSales)}%
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
									${fns:roundUp((data['total'][date].sureSalesVolume-data['total'][date].realSalesVolume)*100/data['total'][date].sureSalesVolume)}%
					    		</span>
					    	</c:if>
					    </td>
					    <c:if test="${saleReport.searchType eq '9'}">
					    <td style="text-align: center;vertical-align: middle;">
							<c:if test="${orderNumMap['total'][date]>0}">
								 ${orderNumMap['total'][date]}
					    	</c:if>
					    </td>
					    </c:if>
						<td style="text-align: center;vertical-align: middle;">
							<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=total"><spring:message code="amazon_sales_order"/></a> &nbsp;
                   			<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/order/promotionsList?country=&byTime=${saleReport.searchType}&dateStr=${date}&lineType=total"><spring:message code="amazon_sales_discount_order"/></a> &nbsp;
                   			<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=total"><spring:message code="amazon_sales_sku"/></a>  &nbsp;
                    		<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=total"><spring:message code="amazon_sales_product"/></a>&nbsp;
                    		<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/contrastSaleView?type=${saleReport.searchType}&endTime=${date}&startTime=&country=&currencyType=${saleReport.currencyType}&lineType=total"><spring:message code="amazon_sales_comparison"/></a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr class="count">
					<td></td>
					<td style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_total"/></td>
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
					<c:if test="${saleReport.searchType eq '9'}">
					<td></td>
					</c:if>
					<td></td>
					
				</tr>
			</tfoot>
		</table>
		<div style="width:98%;border:1px solid #ccc;">
			<div id="totalChart" style="height:400px"></div>
		</div>
	</div>
	<!-- 欧洲汇总表格 -->
	<div id="euTotal"  class="hideCls tab-pane">
	<div class="alert">
	  <button type="button" class="close" data-dismiss="alert">&times;</button>
	  	<spring:message code="amazon_sales_pro_warn_info"/> &nbsp;&nbsp;&nbsp;<a  class="compare btn btn-success" target="_blank" href="#" ><spring:message code="amazon_sales_comparison"/></a>
	  	
	</div>
		<table id="euTotalTb" class="table table-striped table-bordered table-condensed">
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
					<c:if test="${saleReport.searchType eq '9'}">
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_order"/></th>
					</c:if>
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
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${data['de'][date].sales}<br/>UK:${data['uk'][date].sales}<br/>FR:${data['fr'][date].sales}<br/>IT:${data['it'][date].sales}<br/>ES:${data['es'][date].sales}"><fmt:formatNumber pattern="#######.##" value="${data['eu'][date].sales}" maxFractionDigits="2"  minFractionDigits="2"/> </a>
						</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${data['de'][date].sureSales}<br/>UK:${data['uk'][date].sureSales}<br/>FR:${data['fr'][date].sureSales}<br/>IT:${data['it'][date].sureSales}<br/>ES:${data['es'][date].sureSales}"><fmt:formatNumber pattern="#######.##" value="${data['eu'][date].sureSales}" maxFractionDigits="2"  minFractionDigits="2" /></a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="各国促销额 <br/>DE:${fns:roundUp(data['de'][date].sureSales-data['de'][date].realSales)}<br/>UK:${fns:roundUp(data['uk'][date].sureSales-data['uk'][date].realSales)}<br/>FR:${fns:roundUp(data['fr'][date].sureSales-data['fr'][date].realSales)}<br/>IT:${fns:roundUp(data['it'][date].sureSales-data['it'][date].realSales)}<br/>ES:${fns:roundUp(data['es'][date].sureSales-data['es'][date].realSales)}"><fmt:formatNumber pattern="#######.##" value="${data['eu'][date].realSales}" maxFractionDigits="2"  minFractionDigits="2" /></a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${data['eu'][date].sureSales>0}">
								<span class="badge badge-info">
									${fns:roundUp((data['eu'][date].sureSales-data['eu'][date].realSales)*100/data['eu'][date].sureSales)}%
								</span>
							</c:if>
						 </td>
						<td style="text-align: center;vertical-align: middle;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${data['de'][date].salesVolume}<br/>UK:${data['uk'][date].salesVolume}<br/>FR:${data['fr'][date].salesVolume}<br/>IT:${data['it'][date].salesVolume}<br/>ES:${data['es'][date].salesVolume}">${data['eu'][date].salesVolume}</a>
						</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="DE:${data['de'][date].sureSalesVolume}<br/>UK:${data['uk'][date].sureSalesVolume}<br/>FR:${data['fr'][date].sureSalesVolume}<br/>IT:${data['it'][date].sureSalesVolume}<br/>ES:${data['es'][date].sureSalesVolume}">${data['eu'][date].sureSalesVolume}</a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="各国促销量 <br/>DE:${data['de'][date].sureSalesVolume-data['de'][date].realSalesVolume}<br/>UK:${data['uk'][date].sureSalesVolume-data['uk'][date].realSalesVolume}<br/>FR:${data['fr'][date].sureSalesVolume-data['fr'][date].realSalesVolume}<br/>IT:${data['it'][date].sureSalesVolume-data['it'][date].realSalesVolume}<br/>ES:${data['es'][date].sureSalesVolume-data['es'][date].realSalesVolume}">${data['eu'][date].realSalesVolume}</a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${data['eu'][date].sureSalesVolume>0}">
								<span class="badge badge-info">
									${fns:roundUp((data['eu'][date].sureSalesVolume-data['eu'][date].realSalesVolume)*100/data['eu'][date].sureSalesVolume)}%
					    		</span>
					    	</c:if>
					    </td>
					    <c:if test="${saleReport.searchType eq '9'}">
					     <td style="text-align: center;vertical-align: middle;">
							<c:if test="${orderNumMap['eu'][date]>0}">
								 ${orderNumMap['eu'][date]}
					    	</c:if>
					    </td>
					    </c:if>
						<td style="text-align: center;vertical-align: middle;">
							<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=eu&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=total"><spring:message code="amazon_sales_order"/></a> &nbsp;
                   			<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/order/promotionsList?country=eu&byTime=${saleReport.searchType}&dateStr=${date}&lineType=total"><spring:message code="amazon_sales_discount_order"/></a> &nbsp;
                   			<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=eu&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=total"><spring:message code="amazon_sales_sku"/></a>  &nbsp;
                    		<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=eu&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=total"><spring:message code="amazon_sales_product"/></a>&nbsp;
                    		<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/contrastSaleView?type=${saleReport.searchType}&endTime=${date}&startTime=&country=eu&currencyType=${saleReport.currencyType}&lineType=total"><spring:message code="amazon_sales_comparison"/></a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr class="count">
					<td></td>
					<td style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_total"/></td>
					<c:if test="${saleReport.searchType eq '1' or saleReport.searchType eq '2'  }">
					  <td></td>
				    </c:if>
					<td tid="euTotalTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>
					<td tid="euTotalTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>	
					<td tid="euTotalTb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>	
					<td style="text-align: center;vertical-align: middle;"></td>
					<td tid="euTotalTb" class="total" style="text-align: center;vertical-align: middle;">0</td>
					<td tid="euTotalTb" class="total" style="text-align: center;vertical-align: middle;">0</td>
					<td tid="euTotalTb" class="total" style="text-align: center;vertical-align: middle;">0</td>		
					<td style="text-align: center;vertical-align: middle;"></td>
					<c:if test="${saleReport.searchType eq '9'}">
					<td></td>
					</c:if>
					<td></td>
				</tr>
			</tfoot>
		</table>
		<div style="width:98%;border:1px solid #ccc;">
			<div id="euTotalChart" style="height:400px"></div>
		</div>
	</div>
	
	<!-- 英语国家汇总表格 -->
	<div id="enTotal"  class="hideCls tab-pane">
	<div class="alert">
	  <button type="button" class="close" data-dismiss="alert">&times;</button>
<!-- 
	  	促销确认方法:订单的<strong>PromotionId</strong>含有"F-"被认为是促销订单 &nbsp;&nbsp;&nbsp;<a  class="compare btn btn-success" target="_blank" href="#" >对比</a> -->
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
					<c:if test="${saleReport.searchType eq '9'}"><th style="text-align: center;vertical-align: middle;">
					<spring:message code="amazon_sales_order"/></th>
					</c:if>
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
									${fns:roundUp((data['en'][date].sureSales-data['en'][date].realSales)*100/data['en'][date].sureSales)}%
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
									${fns:roundUp((data['en'][date].sureSalesVolume-data['en'][date].realSalesVolume)*100/data['en'][date].sureSalesVolume)}%
					    		</span>
					    	</c:if>
					    </td>
					    <c:if test="${saleReport.searchType eq '9'}">
					     <td style="text-align: center;vertical-align: middle;">
							<c:if test="${orderNumMap['en'][date]>0}">
								 ${orderNumMap['en'][date]}
					    	</c:if>
					    </td>
					    </c:if>
						<td style="text-align: center;vertical-align: middle;">
							<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=en&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=total"><spring:message code="amazon_sales_order"/></a> &nbsp;
                   			<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/order/promotionsList?country=en&byTime=${saleReport.searchType}&dateStr=${date}&lineType=total"><spring:message code="amazon_sales_discount_order"/></a> &nbsp;
                   			<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=en&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=total"><spring:message code="amazon_sales_sku"/></a>  &nbsp;
                    		<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=en&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=total"><spring:message code="amazon_sales_product"/></a>&nbsp;
                    		<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/contrastSaleView?type=${saleReport.searchType}&endTime=${date}&startTime=&country=en&currencyType=${saleReport.currencyType}&lineType=total"><spring:message code="amazon_sales_comparison"/></a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr class="count">
					<td></td>
					<td style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_total"/></td>
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
					<c:if test="${saleReport.searchType eq '9'}"><td></td></c:if>
					<td></td>
				</tr>
			</tfoot>
		</table>
		<div style="width:98%;border:1px solid #ccc;">
			<div id="enTotalChart" style="height:400px"></div>
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
					<c:if test="${saleReport.searchType eq '9'}">
					<th style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_order"/></th>
					</c:if>
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
										${fns:roundUp((data[dic.value][date].sureSales-data[dic.value][date].realSales)*100/data[dic.value][date].sureSales)}%
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
										${fns:roundUp((data[dic.value][date].sureSalesVolume-data[dic.value][date].realSalesVolume)*100/data[dic.value][date].sureSalesVolume)}%
									</span>
								</c:if>
								</span>
							</td>
							<c:if test="${saleReport.searchType eq '9'}">
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${orderNumMap[dic.value][date]>0}">
								 ${orderNumMap[dic.value][date]}
					    	</c:if>
					    </td>
					    </c:if>
							<td style="text-align: center;vertical-align: middle;">
								<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=${dic.value}&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=total"><spring:message code="amazon_sales_order"/></a> &nbsp;
								<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/order/promotionsList?country=${dic.value}&byTime=${saleReport.searchType}&dateStr=${date}&lineType=total"><spring:message code="amazon_sales_discount_order"/></a> &nbsp;
                   				<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=${dic.value}&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=total">SKU</a>  &nbsp;
                    			<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=${dic.value}&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=total"><spring:message code="amazon_sales_product"/></a>&nbsp;
                    			<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/contrastSaleView?type=${saleReport.searchType}&endTime=${date}&startTime=&country=${dic.value}&currencyType=${saleReport.currencyType}&lineType=total"><spring:message code="amazon_sales_comparison"/></a>
							</td>
						</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr class="count">
							<td></td>
							<td style="text-align: center;vertical-align: middle;"><spring:message code="amazon_sales_total"/></td>
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
							<c:if test="${saleReport.searchType eq '9'}">
							<td></td>
							</c:if>
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
		<c:if test="${!fn:startsWith(otherType.key, '1')&&!fn:startsWith(otherType.key, '4')&&!fn:startsWith(otherType.key, '5')&&!fn:startsWith(otherType.key, '6')&&!fn:startsWith(otherType.key, '7')&&otherType.key ne 'total'}">
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
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=${otherType.key}&lineType=total"><spring:message code="amazon_sales_order"/></a> 
							   &nbsp;&nbsp;
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=${otherType.key}&lineType=total">SKU</a>
                               &nbsp; &nbsp;
                               <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=${otherType.key}&lineType=total"><spring:message code="amazon_sales_product"/></a>
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
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=4&lineType=total"><spring:message code="amazon_sales_order"/></a> 
							   &nbsp;&nbsp;
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=4&lineType=total">SKU</a>
                               &nbsp; &nbsp;
                               <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=4&lineType=total"><spring:message code="amazon_sales_product"/></a>
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
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=5-de&lineType=total"><spring:message code="amazon_sales_order"/></a> 
							   &nbsp;&nbsp;
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=5-de&lineType=total">SKU</a>
                               &nbsp; &nbsp;
                               <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=5-de&lineType=total"><spring:message code="amazon_sales_product"/></a>
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
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=5-com&lineType=total"><spring:message code="amazon_sales_order"/></a> 
							   &nbsp;&nbsp;
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=5-com&lineType=total">SKU</a>
                               &nbsp; &nbsp;
                               <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=5-com&lineType=total"><spring:message code="amazon_sales_product"/></a>
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
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=6&lineType=total"><spring:message code="amazon_sales_order"/></a> 
							   &nbsp;&nbsp;
							   <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=6&lineType=total">SKU</a>
                               &nbsp; &nbsp;
                               <a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&orderType=6&lineType=total"><spring:message code="amazon_sales_product"/></a>
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
	<div class="modal hide fade" id="tip">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3 style="color: red">Info</h3>
		</div>
		<div class="modal-body">
			<p style="font-size: 18px;font-weight: bold;">销售数据正在更新预计1分钟以后恢复，请稍后再试...</p>
		</div>
		<div class="modal-footer">
			<button type="button" data-dismiss="modal" class="btn btn-primary">Close</button>
		</div>
	</div>
</body>
</html>
