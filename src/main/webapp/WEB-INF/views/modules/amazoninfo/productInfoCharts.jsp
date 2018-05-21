<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>${productName}目录、Session、历史价格图表</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/datatables.jsp"%>
	<style type="text/css">
		.desc th{
			text-align: center;
			vertical-align: middle;
		}
		.desc td{
			text-align: center;
			vertical-align: middle;
		}
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
		.modal.fade.in {
		 	top: 0%;
		}
		.modal{
			 width: auto;
			 margin-left:-500px 
		}
		#imgtest{  position:absolute;
	         top:100px; 
	         left:300px; 
	         z-index:1; 
	         } 
	</style>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<script type="text/javascript">
	
		$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn )
		{
			return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
				var rs = 0 ;
				var a = $('td:eq('+iColumn+')', tr).find("a:eq(0)");
				var clsAttr = a.attr("class");
				if(clsAttr){
					if(clsAttr.indexOf('warning')>0){
						rs = parseInt(a.text())*100000;
					}else if (clsAttr.indexOf('danger')>0){
						rs = parseInt(a.text())*10000;
					}else if (clsAttr.indexOf('primary')>0){
						rs = parseInt(a.text())*1000;
					}else{
						rs = parseInt(a.text());
					}
				}
				return rs;
			} );
		}	
	
	
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		
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
			</c:choose> 
		*/
		var map = {};
		map.de = 'EUR';
		map.fr = 'EUR';
		map.it = 'EUR';
		map.es = 'EUR';
		map.uk = 'GBP';
		map.ca = 'CDN$';
		map.com = 'USD';
		map.jp = 'JPY';
		map.mx = 'MXN$';
		
		$(document).ready(function() {
			var flag=0;
			//fba库存提示
			
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			
			
			//-----------------------------------------------------------
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
			
			$("#reback").click(function(){
				window.history.go(-1);				
			});
			
			$("table").css("margin-bottom","5px");
			
			$(".countryHref").click(function(e){e.preventDefault();
		     	var key = $(this).attr('key');
			    $("input[name='selCountry']").val($(this).attr("key"));
			    window.location.href='${ctx}/psi/psiInventory/getCharts?productName=${productName }&startDate='+$("#start").val()+'&endDate='+$("#end").val()+'&searchType='+${searchType}+'&selCountry='+$("#country").val();
		
			});
			
			
			$(".saleHref").click(function(e){
				e.preventDefault();
				if(!(myChart1.series)){
					myChart1.setOption(option1);
					$("#saleChart").css("width",$("#saleChart").parent().parent().parent().width()-20);
					myChart1.resize();
				}
				$(this).tab('show');
				var href=$(this).attr("href");
				if(href=="#saleVolume"){
					$("#exportDiv").show();
				}else{
					$("#exportDiv").hide();
				}
			});
			//下单数提示用
			var order ={};
			var adsMap ={};
			var amsMap ={};
			
			
			//销量图表  saleChart
			var myChart;
			var myChart1;
			var option1;
			<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">var myChartNew${dic.value};var optionNew${dic.value};var myChart${dic.value};var option${dic.value}; var myChartS${dic.value};var optionS${dic.value};var newOptionS${dic.value};</c:if></c:forEach>
			
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
		        	//历史价格图表
					var mapSku = {};
				//<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">
						order['${dic.value}']={};
						adsMap['${dic.value}']={};
						amsMap['${dic.value}']={};
					<c:forEach items="${xAxis}" var="x" varStatus="i">
						order['${dic.value}']['${x}${saleReport.searchType ne 1?type:""}']=${empty sessions[dic.value][x].ordersPlaced?0:sessions[dic.value][x].ordersPlaced};
						
					</c:forEach>
					<c:forEach items="${sessionXAxis}" var="x" varStatus="i">
					   adsMap['${dic.value}']['${x}']=${empty adsMap[dic.value][x]?0:adsMap[dic.value][x]};
					   amsMap['${dic.value}']['${x}']=${empty amsMap[dic.value][x]?0:amsMap[dic.value][x]};
					</c:forEach>
					mapSku['${dic.value}']={};
					<c:forEach items="${priceXAxis}" var="x" varStatus="i">
						mapSku['${dic.value}']['${priceXAxisMap[x]}'] = '${hisPriceMap[dic.value][x].sku}';
					</c:forEach>
				
		            	myChart${dic.value} = ec.init(document.getElementById("${dic.value}Chart"));
			            myChart${dic.value}.showLoading({
						    text: '${dic.label} <spring:message code="amazon_not_matched"/> sku',    //loading话术
						});
						//ecahrts-----------------
			            option${dic.value} = {
			            	title:{text:'<spring:message code="amazon_historical_price_curve"/>',x:'center'},		
			                tooltip : {
			                    trigger: 'item',
		                    	formatter:function(params){
		                    		 <c:if test="${saleReport.searchType eq 1}">
			                    	 	return '<spring:message code="amazon_lowest_price"/> '+params[2]+'<br/><spring:message code="amazon_sorted_by_sku"/><br/>' + mapSku['${dic.value}'][params[1]];
			                    	 </c:if>
		                    		 <c:if test="${saleReport.searchType ne 1}">
		                    		 return '<spring:message code="amazon_lowest_price"/> '+params[2]
			                    	 </c:if>
			                    }
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
			                        data :[<c:forEach items="${priceXAxis}" var="x" varStatus="i">'${x}'${i.last?'':','}</c:forEach>],
			                   		boundaryGap:false
			                    }
			                ],
			                yAxis : [
			                    {
			                        type : 'value',
			                        splitArea : {show : true},
				                    axisLabel : {
			                            formatter: '{value} '+map.${dic.value}
			                        },
			                        boundaryGap:[0,0.5]
			                    }
			                ],
			                series : [
								//<c:set var="key" value="${dic.value}"/>
								{
								    name:'${productName}',
								    type:'line',
								    data:[<c:forEach items="${priceXAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty hisPriceMap[key][x].salePrice?0:hisPriceMap[key][x].salePrice}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>]
								}${i.last?'':','}
			                ]
			            };
			            
			            
			          
			         
			            var saleMap = {}; 
			           <c:forEach items="${priceXAxis}" var="x" varStatus="i">
			                    saleMap['${x}']="${empty  salesMap[x]?0:salesMap[x]}";
			            </c:forEach>
			            <c:if test="${not empty catalogMap[dic.value]}">
			            myChartRank${dic.value}= ec.init(document.getElementById("${dic.value}RankChart"));
			            myChartRank${dic.value}.showLoading({
						    text: '${dic.label} <spring:message code="amazon_not_rank_record"/>',    //loading话术
						});
						//ecahrts-----------------
			            optionRank${dic.value}${j.count} = {
			            	title:{text:"${name.catalogName} <spring:message code='amazon_rank_curve'/>",x:'center',textStyle: {color:'blue'}},		
			                tooltip : {
			                    trigger: 'item',
			                    formatter:function(params){
			                    	 return params[0] + '<br/>'
		                                + params[1] + ' : ' + -params[2]
		                                +"<br/>销量:"+saleMap[params[1]];
			                    }
			                },
			                legend: {
			                	y:30,
			                    data:[<c:forEach items="${catalogMap[dic.value]}" var="rankMap" varStatus="i">"${rankMap.catalogName}_${rankMap.asin}"${i.last&&empty newReleasesRank[dic.value]?'':','}</c:forEach><c:forEach items="${newReleasesRank[dic.value]}" var="tempMap" varStatus="i">'NEW_${tempMap.key}'${i.last?'':','}</c:forEach>]
			                },
			                toolbox: {
			                    show : true,
			                    feature : {
			                        mark : false,
			                        dataView: {show: true, readOnly: true,optionToContent:function(datas){
			                        	console.log(datas)
			                        	var rs = '';
			                        	for ( var i = 0; i < datas.xAxis[0].data.length; i++) {
											rs+= datas.xAxis[0].data[i]+"\t\t"+(-(datas.series[0].data[i]))+"\n"
										}
			                        	return rs;
			                        }},
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
			                        data :[<c:forEach items="${priceXAxis}" var="x" varStatus="i">'${x}'${i.last?'':','}</c:forEach>],
			                   		boundaryGap:false
			                    }
			                ],
			                yAxis : [
			                    {
			                        type : 'value',
			                        scale: true,
			                        splitArea : {show : true},
				                    axisLabel : {
			                            formatter: function(value){
			                            	return -value;
			                            }
			                        },
			                        max:-1,
			                        boundaryGap:[0,0.5]
			                    }
			                ],
			                series : [
								//<c:forEach items="${catalogMap[dic.value]}" var="tempMap" varStatus="i">    
								{
								    name:"${tempMap.catalogName}_${tempMap.asin}",
								    type:'line',
								    data:[<c:forEach items="${priceXAxis}" var="x" varStatus="i"><c:if test="${!fn:contains(tempMap.rankXAxis,x)}">'-'</c:if><c:if test="${fn:contains(tempMap.rankXAxis,x)}"><c:set var="key1" value="0"/><c:if test="${i.index>0}"><c:set var="key1" value="${tempMap.asin}_${dic.value}_${tempMap.rankXAxis[(i.index-1)]}"/> <c:set var="val" value="${rankMap[key1][tempMap.catalog].rank>0?rankMap[key1][tempMap.catalog].rank:val}"/> </c:if><c:set var="key" value="${tempMap.asin}_${dic.value}_${x}"/>${rankMap[key][tempMap.catalog].rank>0?-rankMap[key][tempMap.catalog].rank:-val}</c:if>${i.last?'':','}</c:forEach>]
								}${i.last&&empty newReleasesRank[dic.value]?'':','}
								//</c:forEach>
								//<c:forEach items="${newReleasesRank[dic.value]}" var="tempMap" varStatus="i">    
								{
								    name:'NEW_${tempMap.key}',
								    type:'line',
								    data:[<c:forEach items="${priceXAxis}" var="x" varStatus="i"><c:set var="key1" value="${x}"/><c:if test="${empty newReleasesRank[dic.value][tempMap.key][key1].rank}">'-'</c:if><c:if test="${not empty newReleasesRank[dic.value][tempMap.key][key1].rank}">-${newReleasesRank[dic.value][tempMap.key][key1].rank}</c:if>${i.last?'':','}</c:forEach>]
								}${i.last?'':','}
								//</c:forEach>
			                ]
			            };
			            </c:if>
			            
			            
			           /*  <c:if test="${not empty newReleasesRank[dic.value]}">
			            myChartRankNew${dic.value} = ec.init(document.getElementById("${dic.value}NewRankChart"));
			            myChartRankNew${dic.value}.showLoading({
						    text: '${dic.label}无新品排名',    //loading话术
						});
						//ecahrts-----------------
			            optionRankNew${dic.value}${j.count} = {
			            		
			                tooltip : {
			                    trigger: 'item',
			                    formatter:function(params){
			                    	 return params[0] + '<br/>'
		                                + params[1] + ' : ' + -params[2];
			                    }
			                },
			                legend: {
			                	y:30,
			                    data:[<c:forEach items="${newReleasesRank[dic.value]}" var="tempMap" varStatus="i">'${tempMap.key}'${i.last?'':','}</c:forEach>]
			                },
			                toolbox: {
			                    show : true,
			                    feature : {
			                        mark : false,
			                        dataView: {show: true, readOnly: true,optionToContent:function(datas){
			                        	console.log(datas)
			                        	var rs = '';
			                        	for ( var i = 0; i < datas.xAxis[0].data.length; i++) {
											rs+= datas.xAxis[0].data[i]+"\t\t"+(-(datas.series[0].data[i]))+"\n"
										}
			                        	return rs;
			                        }},
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
			                        data :[<c:forEach items="${priceXAxis}" var="x" varStatus="i">'${x}'${i.last?'':','}</c:forEach>],
			                   		boundaryGap:false
			                    }
			                ],
			                yAxis : [
			                    {
			                        type : 'value',
			                        scale: true,
			                        splitArea : {show : true},
				                    axisLabel : {
			                            formatter: function(value){
			                            	return -value;
			                            }
			                        },
			                        max:-1,
			                        boundaryGap:[0,0.5]
			                    }
			                ],
			                series : [
								//<c:forEach items="${newReleasesRank[dic.value]}" var="tempMap" varStatus="i">    
								{
								    name:'${tempMap.key}',
								    type:'line',
								    data:[<c:forEach items="${priceXAxis}" var="x" varStatus="i"><c:set var="key1" value="${x}"/> <c:set var="val" value="${not empty newReleasesRank[dic.value][tempMap.key][key1].rank?-newReleasesRank[dic.value][tempMap.key][key1].rank:-101}"/><c:if test='${not empty newReleasesRank[dic.value][tempMap.key][key1].link}'><c:set var='link' value='${newReleasesRank[dic.value][tempMap.key][key1].link}'/></c:if>${val}${i.last?'':','}</c:forEach>]
								}${i.last?'':','}
								//</c:forEach>
			                ],
			                title:{text:"New Releases Rank",x:'center',textStyle: {color:'blue'}}
			            };
			            </c:if> */
			            
			          
			            //sessions--------------------------------------------------------
			            myChartS${dic.value} = ec.init(document.getElementById("${dic.value}SChart"));
			            myChartS${dic.value}.showLoading({
						    text: '${dic.label} not Sessions data',    //loading话术
						});
						//ecahrts-----------------
			            optionS${dic.value} = {
			            	title:{text:"<spring:message code='amazon_and_convension'/>",x:'center'},		
			                tooltip : {
			                	 trigger: 'axis'/* ,
			                     formatter: function(params) {
			                    	 if(params.length==2){
			                    		 var temp = order['${dic.value}'][params[0][1]];
				                         return params[0][1] + '<br/>'
				                                + params[0][0] + ' : ' + params[0][2] + '<br/>'+ params[1][0] + ' : ' + params[1][2] + ' (%)';
			                    	 }else{
			                    		 if('Session'!=params[0][0]){
			                    			 return params[0][1] + '<br/>'
				                                + params[0][0] + ' : ' + params[0][2];
			                    		 }else{
			                    			 return params[0][1] + '<br/>'
				                                + params[0][0] + ' : ' + params[0][2];
			                    		 }
			                    	 }
			                     } */
			                },
			                legend: {
			                	y:30,
			                    data:['Session','<spring:message code="amazon_conversion"/>']
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
			                dataZoom:{
			                	 show : true,  
			                     realtime : true,  
			                     start : 0,  
			                     end : 100 
			                },
			                calculable : false,
			                animation:false,
			                xAxis : [
			                    {
			                       axisLabel: {
										rotate: 60
									}, 
			                        type : 'category',
			                        data :[<c:forEach items="${sessionXAxis}" var="x" varStatus="i">'${x}${saleReport.searchType ne 1?type:""}'${i.last?'':','}</c:forEach>],
			                   		boundaryGap:false
			                    }
			                ],
			                grid: {
			                    y2: 100
			                },
			                yAxis : [
			                    {
			                    	name : 'Session',
			                        type : 'value',
			                        splitArea : {show : true},
				                    axisLabel : {
			                            formatter: '{value} '
			                        },
			                        boundaryGap:[0,0.5]
			                    },
			                    {
			                    	name : '<spring:message code="amazon_conversion"/>',
			                        type : 'value',
			                        splitArea : {show : true},
			                        axisLabel : {
			                            formatter: function(v){
			                                return v+'%';
			                            }
			                        },
			                    }
			                ],
			                series : [
								{
								    name:'Session',
								    type:'bar',
								    barWidth:16,
								    itemStyle : {
					                    normal: {
					                        color:'#8A8A8A',
					                        borderWidth:1,
					                        borderColor:'#8A8A8A'
					                    }
					                },
								    data:[<c:forEach items="${sessionXAxis}" var="x" varStatus="i">${empty sessions[dic.value][x].sessions?0:sessions[dic.value][x].sessions}${i.last?'':','}</c:forEach>]
								},
								{
								    name:'<spring:message code="amazon_conversion"/>',
								    yAxisIndex:1,
								    type:'line',
								    max : 100,
								    itemStyle : {
					                    normal: {
					                        color:'#87CEFA',
					                        borderWidth:1,
					                        borderColor:'#87CEFA'
					                    }
					                },
					                <c:if test="${searchType eq '1'}">
					                 markPoint : {
					                    symbolSize:0,  
					                    showDelay:0,  
					                    itemStyle:{  
					                        normal:{
				                        	    label:{  
				                                    show: true,  
				                                    position: 'top',  
				                                    formatter: function (param,d,val) { 
				                                    	if (val == 1)  
				                                           return '广告';  
				                                        else if (val == 2)  
				                                           return '捆绑'; 
				                                        else if (val == 3)  
					                                       return '解绑';  
				                                        else if (val == 4)  
					                                       return '断货';  
				                                        else if (val == 5)  
						                                   return '积压';  
				                                        else if (val == 6)  
							                               return '促销';
				                                        else if (val == 7)  
				                                           return '汇率';
				                                        else if (val == 8)  
					                                       return '防御';
				                                        else if (val == 9)  
						                                   return '调价';
				                                        else if (val == 10)  
							                               return '闪';
				                                        else if (val == 11)  
							                               return '首图';
				                                        else if (val == 12)  
							                               return '帖子优化';
			                                   		}    
		                                    	}  
			                                }  
					                          
					                    },
						                data : [
												<c:forEach items="${eventMap[dic.value]}" var="x" varStatus="i">
												 	{value :${x.value}, xAxis: '${x.key}',yAxis:'2000'}${i.last?'':','}
												</c:forEach>	
						                    ]
						                },
						                </c:if>
						                
								    data: (function(){
						                var oriData = [<c:forEach items="${sessionXAxis}" var="x" varStatus="i">${empty sessions[dic.value][x].conversionStr?0:sessions[dic.value][x].conversionStr}${i.last?'':','}</c:forEach>];
						                var len = oriData.length;
						                while(len--) {
						                    oriData[len];
						                }
						                return oriData;
						            }
								   
								    )()
								}
			                ]
			            };
			            
			            
			            newOptionS${dic.value} = {
				            	title:{text:"<spring:message code='amazon_and_convension'/>",x:'center'},		
				                tooltip : {
				                	trigger: 'axis' ,
				                	formatter: function(params) {
				                		console.log(params);
				                    	 if(params.length==4){
				                    		 var temp = order['${dic.value}'][params[0][1]];
				                    		 var asp = adsMap['${dic.value}'][params[0][1]];
				                    		 var ams = amsMap['${dic.value}'][params[0][1]];
					                         return params[0][1] + '<br/>'
					                                + params[0][0] + ' : ' + params[0][2]+ '<br/>'
					                                + params[1][0] + ' : ' + (params[1][2]+'(SPA:'+asp+' AMS:'+ams+')') + '<br/>'
					                                + params[2][0] + ' : ' + params[2][2]+ '<br/>'
					                                + params[3][0] + ' : ' + params[3][2] + ' (%)';
				                    	 }else{
				                    		 return params[0][1] + '<br/>'+ params[0][0] + ' : ' + params[0][2]+'<br/>'+ params[1][0] + ' : ' + params[1][2];
				                    	 }
				                     }
				            	/*  formatter: function(params) {
				                    	 if(params.length==3){
				                    		 var temp = order['${dic.value}'][params[0][1]];
				                    		 var asp = adsMap['${dic.value}'][params[0][1]];
				                    		 var ams = amsMap['${dic.value}'][params[0][1]];
					                         return params[0][1] + '<br/>'
					                                + params[0][0] + ' : ' + params[0][2]+'(SPA:'+asp+' AMS:'+ams+')'+ '<br/>'+ params[1][0] + ' : ' + (params[0][2]+params[1][2]) + '<br/>'+ params[2][0] + ' : ' + params[2][2] + ' (%)';
				                    	 }else{
				                    		 if('Session'!=params[0][0]){
				                    			 return params[0][1] + '<br/>'
					                                + params[0][0] + ' : ' + params[0][2];
				                    		 }else{
				                    			 return params[0][1] + '<br/>'
					                                + params[0][0] + ' : ' + params[0][2]+
					                                '<br/>'+ params[1][0] + ' : ' + (params[0][2]+params[1][2]) ;
				                    		 }
				                    	 }
				                     } */
				                },
				                legend: {
				                	y:30,
				                	selected: {'Session':true,'Ads':true,'B2bOrder':true,'<spring:message code="amazon_conversion"/>':true},
				                    data:['Session','Ads','B2bOrder','<spring:message code="amazon_conversion"/>']
				                },
				                toolbox: {
				                    show : true,
				                    feature : {
				                        mark : false,
				                        dataView :false,
				                        magicType:{type:['line','bar'],show:false},
				                        restore : {show:true},
				                        saveAsImage : {show:true}
				                    }
				                },
				                dataZoom:{
				                	 show : true,  
				                     realtime : true,  
				                     start : 0,  
				                     end : 100 
				                },
				                calculable : false,
				                animation:false,
				                xAxis : [
				                    {
				                       axisLabel: {
											rotate: 60
										}, 
				                        type : 'category',
				                        data :[<c:forEach items="${sessionXAxis}" var="x" varStatus="i">'${x}${saleReport.searchType ne 1?type:""}'${i.last?'':','}</c:forEach>],
				                   		boundaryGap:false
				                    }
				                ],
				                grid: {
				                    y2: 100
				                },
				                yAxis : [
				                    {
				                    	name : 'Session',
				                        type : 'value',
				                        splitArea : {show : true},
					                    axisLabel : {
				                            formatter: '{value} '
				                        },
				                        boundaryGap:[0,0.5]
				                    },
				                    
				                    {
				                    	name : '<spring:message code="amazon_conversion"/>',
				                        type : 'value',
				                        splitArea : {show : true},
				                        axisLabel : {
				                            formatter: function(v){
				                                return v+'%';
				                            }
				                        },
				                    }
				                ],
				                series : [
									
									{
									    name:'Session',
									    type:'bar',
									    barWidth:16,
									    itemStyle : {
						                    normal: {
						                        color:'#8A8A8A',
						                        borderWidth:1,
						                        borderColor:'#8A8A8A'
						                    }
						                },
									    data:[<c:forEach items="${sessionXAxis}" var="x" varStatus="i">${empty sessions[dic.value][x].sessions?0:sessions[dic.value][x].sessions}${i.last?'':','}</c:forEach>]
									},
									
									{
									    name:'Ads',
									    type:'bar',
									    barWidth:16,
									    data:[<c:forEach items="${sessionXAxis}" var="x" varStatus="i">${empty adsData[dic.value][x]?0:adsData[dic.value][x]}${i.last?'':','}</c:forEach>]
									},
									{
									    name:'B2bOrder',
									    type:'bar',
									    barWidth:16,
									    itemStyle : {
						                    normal: {
						                        color:'#5bb75b',
						                        borderWidth:1,
						                        borderColor:'#5bb75b'
						                    }
						                },
									    data:[<c:forEach items="${sessionXAxis}" var="x" varStatus="i">${empty sessions[dic.value][x].b2bOrdersPlaced?0:sessions[dic.value][x].b2bOrdersPlaced}${i.last?'':','}</c:forEach>]
									},
									{
									    name:'<spring:message code="amazon_conversion"/>',
									    yAxisIndex:1,
									    type:'line',
									    max : 100,
									    itemStyle : {
						                    normal: {
						                        color:'#87CEFA',
						                        borderWidth:1,
						                        borderColor:'#87CEFA'
						                    }
						                },
						                <c:if test="${searchType eq '1'}">
						                 markPoint : {
						                    symbolSize:0,  
						                    showDelay:0,  
						                    itemStyle:{  
						                        normal:{
					                        	    label:{  
					                                    show: true,  
					                                    position: 'top',  
					                                    formatter: function (param,d,val) { 
					                                    	if (val == 1)  
					                                           return '广告';  
					                                        else if (val == 2)  
					                                           return '捆绑'; 
					                                        else if (val == 3)  
						                                       return '解绑';  
					                                        else if (val == 4)  
						                                       return '断货';  
					                                        else if (val == 5)  
							                                   return '积压';  
					                                        else if (val == 6)  
								                               return '促销';
					                                        else if (val == 7)  
					                                           return '汇率';
					                                        else if (val == 8)  
						                                       return '防御';
					                                        else if (val == 9)  
							                                   return '调价';
					                                        else if (val == 10)  
								                                   return '闪';
					                                        else if (val == 11)  
								                                   return '首图';
					                                        else if (val == 12)  
								                                   return '帖子优化';
				                                   		}    
			                                    	}  
				                                }  
						                          
						                    },
							                data : [
													<c:forEach items="${eventMap[dic.value]}" var="x" varStatus="i">
													 	{value :${x.value}, xAxis: '${x.key}',yAxis:'2000'}${i.last?'':','}
													</c:forEach>	
							                    ]
							                },
							                </c:if>
							                
									    data: (function(){
							                var oriData = [<c:forEach items="${sessionXAxis}" var="x" varStatus="i">${empty sessions[dic.value][x].conversionStr?0:sessions[dic.value][x].conversionStr}${i.last?'':','}</c:forEach>];
							                var len = oriData.length;
							                while(len--) {
							                    oriData[len];
							                }
							                return oriData;
							            }
									   
									    )()
									}
				                ]
				            };
			            
		          //</c:if></c:forEach>
		          
		          
		          
				//	myChartde.setOption(optionde);
				//	myChartSde.setOption(optionSde);
					
				//	 <c:forEach items="${catalogMap['de']}" var="name"  varStatus="j">
					//     myChartRankde${j.count}.setOption(optionRankde${j.count});
				//	</c:forEach>
					
					myChart${selCountry}.setOption(option${selCountry});
					//myChartS${selCountry}.setOption(optionS${selCountry});
					myChartS${selCountry}.setOption(newOptionS${selCountry});
					 myChartRank${selCountry}.setOption(optionRank${selCountry});
					// myChartRankNew${selCountry}.setOption(optionRankNew${selCountry});
					 
		        }
		    );
			
		  	
		    
		    $("#btnSubmit").click(function(){
				window.location.href='${ctx}/psi/psiInventory/getCharts?productName=${productName }&startDate='+$("#start").val()+'&endDate='+$("#end").val()+'&searchType='+${searchType}+'&selCountry='+$("#country").val();
		    	
			});
		    
		   
			
			 $(".pagination a").addClass("nava");
			 
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
						//window.location.href="${ctx}/psi/psiInventory/productInfoDetail?productName="+encodeURIComponent(item);
						window.location.href='${ctx}/psi/psiInventory/getCharts?productName='+item+'&startDate='+$("#start").val()+'&endDate='+$("#end").val()+'&searchType='+$("#searchType").val()+'&selCountry='+$("#country").val();
	    	
						//$("#productName").val(item);
					//	$("#searchForm").submit();
						return item;
					}
				});

		});
		
		function toDecimal(x) {  
            var f = parseFloat(x);  
            if (isNaN(f)) {  
                return;  
            }  
            f = Math.round(x*100)/100;  
            return f;  
     }  
		
		function searchTypes(searchFlag){
			if(oldSearchFlag==searchFlag){
				return;
			}
			$("#searchType").val(searchFlag);
			window.location.href='${ctx}/psi/psiInventory/getCharts?productName=${productName }&startDate='+$("#start").val()+'&endDate='+$("#end").val()+'&searchType='+searchFlag+'&selCountry='+$("#country").val();
		}
		
	</script>
</head>
<body>
 <div class='tabbable tabs-right' style="margin-right: 100px">
	<ul class="nav nav-tabs" style="margin-top: 5px;position:fixed;right: 0px;top: 5px;z-index: 10000">
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${dic.value eq selCountry?' active':''}" ><a  class="countryHref" href="#${dic.value}"  key="${dic.value}"  >${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>
	<div style="font-weight: bold; text-align: center;">
	     <input id="typeahead" type="text" class="span3 search-query" value="${productName}" style="width:200px;margin-top: 5px"  autocomplete="off"  style="margin: 0 auto;" data-provide="typeahead" data-items="8" />
	</div>
	<br/>
	<form:form id="searchForm" modelAttribute="saleReport" action="${ctx}/psi/psiInventory/productInfoDetail" method="post" class="breadcrumb form-search">
		<div style="height:70px;margin-top:10px">
		<ul class="nav nav-pills" style="width:250px;float:left;" id="myTab">
			<li data-toggle="pills" id="showTab0"><a href="#" onclick="javaScript:searchTypes('1');return false">By Day</a></li>
			<li data-toggle="pills" id="showTab1"><a href="#" onclick="javaScript:searchTypes('2');return false">By Week</a></li>
			<li data-toggle="pills" id="showTab2"><a href="#" onclick="javaScript:searchTypes('3');return false">By Month</a></li>
		</ul>
        <input type="hidden" name="selCountry" id="country" value='${selCountry }'/>
		<input type="hidden" value="${productName}" name="productName" id='productName'/>
		<input id="searchType" name="searchType" type="hidden" value="${searchType}" />
		<span style="float: center;">
		
		<fmt:parseDate value="${startDate}" var="start"  pattern="yyyyMMdd"></fmt:parseDate> 
		<fmt:parseDate value="${endDate}" var="end"  pattern="yyyyMMdd"></fmt:parseDate> 
		<label></label><input style="width: 120px" onclick="WdatePicker({weekMethod:'MSExcel',firstDayOfWeek:1,isShowWeek:true,dateFmt:'yyyyMMdd'});" readonly="readonly"  class="Wdate" type="text" name="startDate" value="<fmt:formatDate value="${start}" pattern="yyyyMMdd"/>" class="input-small" id="start"/>
		&nbsp;-&nbsp;<input style="width: 120px" onclick="WdatePicker({weekMethod:'MSExcel',firstDayOfWeek:1,isShowWeek:true,dateFmt:'yyyyMMdd'});" readonly="readonly"  class="Wdate" type="text" name="endDate" value="<fmt:formatDate value="${end}" pattern="yyyyMMdd" />" id="end" class="input-small"/>
		&nbsp;&nbsp;
		<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code="sys_but_search"/>"/>
		
		</span>
		</div>
	</form:form>
	
	<div class="tab-content">
	<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
		<c:if test="${dic.value ne 'com.unitek'}">
			<div id="${dic.value}" class="tab-pane${dic.value eq selCountry?' active':''}">
				<div style="border:1px solid #ccc;width: 98%">
					<div id="${dic.value}SChart" style="height:400px;"></div>
				</div>
				
				<c:if test="${productIsSale eq '1'}">
				  <c:if test="${ not empty catalogMap[dic.value]}">
						<div style="border:1px solid #ccc;width: 98%;margin-top: 5px">
							<div id="${dic.value}RankChart" style="height:400px;"></div>
						</div>
					</c:if> 
				</c:if>
				<%--  <c:if test="${not empty newReleasesRank[dic.value]}"> 
						<div style="border:1px solid #ccc;width: 98%;margin-top: 5px">
							<div id="${dic.value}NewRankChart" style="height:400px;"></div>
						</div>
				</c:if>
					 --%>
				<div style="border:1px solid #ccc;width: 98%;margin-top: 5px">
					<div id="${dic.value}Chart" style="height:400px;"></div>
				</div>
			</div>
		</c:if>
	</c:forEach>
	</div> 
	</div>
</body>
</html>
