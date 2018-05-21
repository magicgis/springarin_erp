<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<!-- <meta name="decorator" content="default"/> -->
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
  <script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		var myChart,myChart1,myChart2,myChart3,myChart4,myChart5,myChart6,myChart7,myChart8,myChart9,myChart10;
		
		$(document).ready(function() {
		//ecahrts-----------------
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
	            myChart = ec.init(document.getElementById('main0'));
	            myChart1 = ec.init(document.getElementById('main1'));
	            myChart2 = ec.init(document.getElementById('main2'));
	            myChart3 = ec.init(document.getElementById('main3'));
	            myChart4 = ec.init(document.getElementById('main4'));
	            myChart5 = ec.init(document.getElementById('main5'));
	            myChart6 = ec.init(document.getElementById('main6'));
	            myChart7 = ec.init(document.getElementById('main7'));
	            myChart8 = ec.init(document.getElementById('main8'));
	            myChart9 = ec.init(document.getElementById('main9'));
	            myChart10 = ec.init(document.getElementById('main10'));
	            myChart.showLoading({
				    text: '正在努力的读取数据中...',    //loading话术
				});
				//ecahrts-----------------
				myChart.hideLoading();
	            var option = {
	            	title:{text:"Amountspend统计图",x:'center'},		
	                tooltip : {
	                    trigger: 'item'
	                },
	                legend: {
	                	y:30,
	                	selected: {'${adName}':true},
	                    data:['${adName}']
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
	                            formatter: '{value}'
	                        },
	                        boundaryGap:[0,0.5]
	                    }
	                ],
	                series : [
						{
						      name:'${adName}',
						      type:'line',
						      data:[<c:forEach items="${xAxis}" var="x" varStatus="i">${empty amaouSpentMap[x]?0:amaouSpentMap[x].amountSpend}${i.last?'':','}</c:forEach>]
						  }
	                ]
	            };
	            myChart.setOption(option);
	            
	            
	            var option1 = {
		            	title:{text:"CPM统计图",x:'center'},		
		                tooltip : {
		                    trigger: 'item'
		                },
		                legend: {
		                	y:30,
		                	selected: {'${adName}':true},
		                    data:['${adName}']
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
		                            formatter: '{value}'
		                        },
		                        boundaryGap:[0,0.5]
		                    }
		                ],
		                series : [
							{
							      name:'${adName}',
							      type:'line',
							      data:[<c:forEach items="${xAxis}" var="x" varStatus="i">${empty amaouSpentMap[x]?0:amaouSpentMap[x].cpm}${i.last?'':','}</c:forEach>]
							  }
		                ]
		            };
		            myChart1.setOption(option1);
		            
		            
		            var option2 = {
			            	title:{text:"CTR统计图",x:'center'},		
			                tooltip : {
			                    trigger: 'item'
			                },
			                legend: {
			                	y:30,
			                	selected: {'${adName}':true},
			                    data:['${adName}']
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
			                            formatter: '{value}'
			                        },
			                        boundaryGap:[0,0.5]
			                    }
			                ],
			                series : [
								{
								      name:'${adName}',
								      type:'line',
								      data:[<c:forEach items="${xAxis}" var="x" varStatus="i">${empty amaouSpentMap[x]?0:amaouSpentMap[x].ctr}${i.last?'':','}</c:forEach>]
								}
			                ]
			            };
			            myChart2.setOption(option2);
		            
		            
			            var option3 = {
				            	title:{text:"CPC统计图",x:'center'},		
				                tooltip : {
				                    trigger: 'item'
				                },
				                legend: {
				                	y:30,
				                	selected: {'${adName}':true},
				                    data:['${adName}']
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
				                            formatter: '{value}'
				                        },
				                        boundaryGap:[0,0.5]
				                    }
				                ],
				                series : [
									{
									      name:'${adName}',
									      type:'line',
									      data:[<c:forEach items="${xAxis}" var="x" varStatus="i">${empty amaouSpentMap[x]?0:amaouSpentMap[x].cpc}${i.last?'':','}</c:forEach>]
									}
				                ]
				            };
				            myChart3.setOption(option3);
		            
				            
				            var option4 = {
					            	title:{text:"Total CR统计图",x:'center'},		
					                tooltip : {
					                    trigger: 'item'
					                },
					                legend: {
					                	y:30,
					                	selected: {'${adName}':true},
					                    data:['${adName}']
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
					                            formatter: '{value}'
					                        },
					                        boundaryGap:[0,0.5]
					                    }
					                ],
					                series : [
										{
										      name:'${adName}',
										      type:'line',
										      data:[<c:forEach items="${xAxis}" var="x" varStatus="i">${empty amaouSpentMap[x]?0:amaouSpentMap[x].totalCr}${i.last?'':','}</c:forEach>]
										}
					                ]
					            };
					            myChart4.setOption(option4);  
					            
					              var option5 = {
						            	title:{text:"绝对ROI统计图",x:'center'},		
						                tooltip : {
						                    trigger: 'item'
						                },
						                legend: {
						                	y:30,
						                	selected: {'${adName}':true},
						                    data:['${adName}']
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
						                            formatter: '{value}'
						                        },
						                        boundaryGap:[0,0.5]
						                    }
						                ],
						                series : [
											{
											      name:'${adName}',
											      type:'line',
											      data:[<c:forEach items="${xAxis}" var="x" varStatus="i">${empty amaouSpentMap[x]?0:amaouSpentMap[x].relativeRoi}${i.last?'':','}</c:forEach>]
											}
						                ]
						            };
						            myChart5.setOption(option5);
						            
						            var option6 = {
							            	title:{text:"系数ROI统计图",x:'center'},		
							                tooltip : {
							                    trigger: 'item'
							                },
							                legend: {
							                	y:30,
							                	selected: {'${adName}':true},
							                    data:['${adName}']
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
							                            formatter: '{value}'
							                        },
							                        boundaryGap:[0,0.5]
							                    }
							                ],
							                series : [
												{
												      name:'${adName}',
												      type:'line',
												      data:[<c:forEach items="${xAxis}" var="x" varStatus="i">${empty amaouSpentMap[x]?0:amaouSpentMap[x].roi}${i.last?'':','}</c:forEach>]
												}
							                ]
							            };
							            myChart6.setOption(option6);
		            
							            
							            
							            var option7 = {
								            	title:{text:"Relevance Score统计图",x:'center'},		
								                tooltip : {
								                    trigger: 'item'
								                },
								                legend: {
								                	y:30,
								                	selected: {'${adName}':true},
								                    data:['${adName}']
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
								                            formatter: '{value}'
								                        },
								                        boundaryGap:[0,0.5]
								                    }
								                ],
								                series : [
													{
													      name:'${adName}',
													      type:'line',
													      data:[<c:forEach items="${xAxis}" var="x" varStatus="i">${empty amaouSpentMap[x]?0:amaouSpentMap[x].relevanceScore}${i.last?'':','}</c:forEach>]
													}
								                ]
								            };
								            myChart7.setOption(option7);
			             
								            
								            var option8 = {
									            	title:{text:"Cost Per Post Engagement统计图",x:'center'},		
									                tooltip : {
									                    trigger: 'item'
									                },
									                legend: {
									                	y:30,
									                	selected: {'${adName}':true},
									                    data:['${adName}']
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
									                            formatter: '{value}'
									                        },
									                        boundaryGap:[0,0.5]
									                    }
									                ],
									                series : [
														{
														      name:'${adName}',
														      type:'line',
														      data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty amaouSpentMap[x]?0:amaouSpentMap[x].costPerPostEngagement}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>]
														}
									                ]
									            };
									            myChart8.setOption(option8);
									            
									            
									            
									            var option9 = {
										            	title:{text:"Cost Per Post Share统计图",x:'center'},		
										                tooltip : {
										                    trigger: 'item'
										                },
										                legend: {
										                	y:30,
										                	selected: {'${adName}':true},
										                    data:['${adName}']
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
										                            formatter: '{value}'
										                        },
										                        boundaryGap:[0,0.5]
										                    }
										                ],
										                series : [
															{
															      name:'${adName}',
															      type:'line',
															      data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty amaouSpentMap[x]?0:amaouSpentMap[x].costPerPostShare}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>]
															}
										                ]
										            };
										            myChart9.setOption(option9);
				             	            
										            var option10 = {
											            	title:{text:"Cost Per  Page Like统计图",x:'center'},		
											                tooltip : {
											                    trigger: 'item'
											                },
											                legend: {
											                	y:30,
											                	selected: {'${adName}':true},
											                    data:['${adName}']
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
											                            formatter: '{value}'
											                        },
											                        boundaryGap:[0,0.5]
											                    }
											                ],
											                series : [
																{
																      name:'${adName}',
																      type:'line',
																      data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty amaouSpentMap[x]?0:amaouSpentMap[x].costPerPageLike}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>]
																}
											                ]
											            };
											            myChart10.setOption(option10); 
	           }
	        );
		     $("#main1").hide();
			 $("#main2").hide();
			 $("#main3").hide();
			 $("#main4").hide();
			 $("#main5").hide();
			 $("#main6").hide();
			 $("#main7").hide();
			 $("#main8").hide();
			 $("#main9").hide();
			 $("#main10").hide();
			 
			 $("#type").change(function(){
				 var type=$(this).val();
				 if(type=="0"){
					 for(var i=0;i<=10;i++){
						 if(i=="0"){$("#main"+i).show();} else{$("#main"+i).hide(); }
					 }
				 }else if(type=="1"){
					 for(var i=0;i<=10;i++){
						 if(i=="1"){$("#main"+i).show();} else{$("#main"+i).hide(); }
					 }
				 }else if(type=="2"){
					 for(var i=0;i<=10;i++){
						 if(i=="2"){$("#main"+i).show();} else{$("#main"+i).hide(); }
					 }
				 }else if(type=="3"){
					 for(var i=0;i<=10;i++){
						 if(i=="3"){$("#main"+i).show();} else{$("#main"+i).hide(); }
					 }
				 }else if(type=="4"){
					 for(var i=0;i<=10;i++){
						 if(i=="4"){$("#main"+i).show();} else{$("#main"+i).hide(); }
					 }
				 }else if(type=="5"){
					 for(var i=0;i<=10;i++){
						 if(i=="5"){$("#main"+i).show();} else{$("#main"+i).hide(); }
					 }
				 }else if(type=="6"){
					 for(var i=0;i<=10;i++){
						 if(i=="6"){$("#main"+i).show();} else{$("#main"+i).hide(); }
					 }
				 }else if(type=="7"){
					 for(var i=0;i<=10;i++){
						 if(i=="7"){$("#main"+i).show();} else{$("#main"+i).hide(); }
					 }
				 }else if(type=="8"){
					 for(var i=0;i<=10;i++){
						 if(i=="8"){$("#main"+i).show();} else{$("#main"+i).hide(); }
					 }
				 }else if(type=="9"){
					 for(var i=0;i<=10;i++){
						 if(i=="9"){$("#main"+i).show();} else{$("#main"+i).hide(); }
					 }
				 }else if(type=="10"){
					 for(var i=0;i<=10;i++){
						 if(i=="10"){$("#main"+i).show();} else{$("#main"+i).hide(); }
					 }
				 }
			 });
			 
			 $("#countRoi").click(function(){
				 var forecastCPC=$("#forecastCPC").val();
				 var sameItemsShipped='${facebookDto.sameItemsShipped}';
				 var totalCr='${facebookDto.totalCr}';
				 var linkClicks='${facebookDto.linkClicks}';
				 var totalAffiliateFees='${facebookDto.totalAffiliateFees}';
				 var profit='${facebookDto.profit}';
				 var forecastROI=(profit/sameItemsShipped/forecastCPC*(totalCr/100)*1.5+totalAffiliateFees/linkClicks*500/forecastCPC)/500;
				 $("#forecastROI").val(toDecimal(forecastROI));
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
	</script>
</head>
<body>
<div style="width:800px;border:1px solid #ccc">
	    <table class="table table-striped table-bordered table-condensed">
					<tbody>
						<tr>
							<td><b>Ad ID</b></td>
							<td>${adId}</td>
							<td><b>Same CR</b></td>
							<td><fmt:formatNumber  pattern="#######.##" value="${facebookDto.sameCr}"  maxFractionDigits="2" /></td>
							<td><b>Impressions</b></td>
							<td>${facebookDto.impressions}</td>
						</tr>
						<tr>
							<td><b>Asin On Ad</b></td>
							<td>${facebookDto.asinOnAd }</td>
							<td><b>Total Revenue</b></td>
							<td>${facebookDto.totalRevenue}</td>
							<td><b>Page Likes</b></td>
							<td><fmt:formatNumber  pattern="#######.##" value="${facebookDto.pageLikes}"  maxFractionDigits="2" /></td>
						</tr>
						<tr>
							<td><b>Starts</b></td>
							<td>${facebookDto.adsDate}</td>
							<td><b>绝对ROI</b></td>
							<td><fmt:formatNumber  pattern="#######.##" value="${facebookDto.relativeRoi}"  maxFractionDigits="2" /></td>
							<td><b>Cost Per Post Share</b></td>
							<td><fmt:formatNumber  pattern="#######.##" value="${facebookDto.costPerPostShare}"  maxFractionDigits="2" /></td>
						</tr>
						<tr>
							<td><b>PostEngagement</b></td>
							<td><fmt:formatNumber  pattern="#######.##" value="${facebookDto.postEngagement}"  maxFractionDigits="2" /></td>
							<td><b>CostPerPostEngagement</b></td>
							<td><fmt:formatNumber  pattern="#######.##" value="${facebookDto.costPerPostEngagement}"  maxFractionDigits="2" /></td>
							<td><b>Post Shares</b></td>
							<td><fmt:formatNumber  pattern="#######.##" value="${facebookDto.postShares}"  maxFractionDigits="2" /></td>
						</tr>
						<tr>
							<td><b>Negative Feedback</b></td>
							<td>${facebookDto.negativeFeedback}</td>
							<td><b>ProductLine</b></td>
							<td>${facebookDto.productLine}</td>
							<td><b>CostPerPageLike</b></td>
							<td><fmt:formatNumber  pattern="#######.##" value="${facebookDto.costPerPageLike}"  maxFractionDigits="2" /></td>
						</tr>
						<tr>
							<td><b>Same Items Shipped</b></td>
							<td>${facebookDto.sameItemsShipped}</td>
							<td><b>Post Comments</b></td>
							<td>${facebookDto.postComments}</td>
							<td><b>All Items Shipped</b></td>
							<td>${facebookDto.allItemsShipped}</td>
						</tr>
						<tr>
							<td><b>Pre-View</b></td>
							<td colspan='5'><a href='${facebookDto.preView}' target="_blank">${facebookDto.preView}</a></td>
						</tr>
						<tr>
							<td><b>Tracking ID</b></td>
							<td colspan='5'>${facebookDto.trackingId}</td>
						</tr>	
					</tbody>
				</table>
				<br/>
				<form id="searchForm" method="post">
		            <b>预估CPC:</b><input name='forecastCPC' id='forecastCPC' class="input-small"/>
		             &nbsp;&nbsp;<input id="countRoi" class="input-small" type="button" value="计算"/>	&nbsp;&nbsp;
		            <b>预估系数ROI:</b><input name='forecastROI' id='forecastROI' class="input-small"/> &nbsp;&nbsp;
		            <select name="type" class="span2" id="type">
		                <option value="0">AmountSpend</option>
		                <option value="1">CPM</option>
		                <option value="2">CTR</option>
		                <option value="3">CPC</option>
		                <option value="4">TotalCR</option>
		                <option value="5">绝对ROI</option>
		                <option value="6">系数ROI</option>
		                <option value="7">Relevance Score</option>
		                <option value="8">Cost per Post Engagement</option>
		                <option value="9">Cost Per Post Share</option>
		                <option value="10">Cost per Page Like</option>
		            </select>
	            </form>
		       <div id="main0" style="height:260px;width:800px;"></div>
		       <div id="main1" style="height:260px;width:800px;"></div>
		       <div id="main2" style="height:260px;width:800px;"></div>
		       <div id="main3" style="height:260px;width:800px;"></div>
		       <div id="main4" style="height:260px;width:800px;"></div>
		       <div id="main5" style="height:260px;width:800px;"></div>
		       <div id="main6" style="height:260px;width:800px;"></div>
		       <div id="main7" style="height:260px;width:800px;"></div>
		       <div id="main8" style="height:260px;width:800px;"></div> 
		       <div id="main9" style="height:260px;width:800px;"></div> 
		       <div id="main10" style="height:260px;width:800px;"></div> 
	</div>
</body>
</html>


	
	