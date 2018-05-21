<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@include file="/WEB-INF/views/include/dialog.jsp" %>
<html>
<head>
	<title>产品评论扫描</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">	</style>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
	
		if(!(top)){
			top = self;
		}
		
		
		var myChart;
		$(document).ready(function(){
			//ecahrts-----------------
			require.config({
		        paths:{ 
		            echarts:'${ctxStatic}/echarts/js/echarts',
		            'echarts/chart/line': '${ctxStatic}/echarts/js/echarts',
		            'echarts/chart/bar': '${ctxStatic}/echarts/js/echarts'
		        }
		    });
		        require(
		        		 ['echarts','echarts/chart/line','echarts/chart/bar'],
				        function(ec) {
				            myChart = ec.init(document.getElementById("main"));
				            var option = {
				                tooltip : {
				                    trigger: 'item'
				                },
				                legend: {
				                	y:30,
				                	selected: {'差评':false,'好评':false},
				                    data:['总计','差评','好评']
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
											rotate: 30
										},
				                        type : 'category',
				                        data :${xAxis},
				                   		boundaryGap:false
				                    }
				                ],
				                
				                yAxis : [
				                    {
				                        type : 'value',
				                        splitArea : {show : true},
				                        boundaryGap:[0,0.5]
				                    }
				                ],
				                series : [{
									      name:'总计',
									      type:'line',
									      data:${yAxis},
										  markPoint : {
									    	  itemStyle : {
								                    normal: {
								                        color:'#FF2D2D',
								                        borderWidth:1,
								                        borderColor:'#FE9A2E'
								                    }
								                },
								          data : [
								              {type : 'max', name: '最大值'}
								          ]
								      },
								      markLine : {
								    	  itemStyle : {
							                    normal: {
							                        color:'#000000',
							                        borderWidth:1,
							                        borderColor:'#74DF00'
							                    }
							                },
									      	 data : [{type : 'average', name: '平均线'}]
									      }
				               		 },
				               		{
									      name:'好评',
									      type:'line',
									      data:${yAxisGood},
										  markPoint : {
									    	  itemStyle : {
								                    normal: {
								                        color:'#FF2D2D',
								                        borderWidth:1,
								                        borderColor:'#FE9A2E'
								                    }
								                },
								          data : [
								              {type : 'max', name: '最大值'}
								          ]
								      },
								      markLine : {
								    	  itemStyle : {
							                    normal: {
							                        color:'#000000',
							                        borderWidth:1,
							                        borderColor:'#74DF00'
							                    }
							                },
									      	 data : [{type : 'average', name: '平均线'}]
									      }
				               		 },
				               		{
									      name:'差评',
									      type:'line',
									      data:${yAxisBad},
										  markPoint : {
									    	  itemStyle : {
								                    normal: {
								                        color:'#FF2D2D',
								                        borderWidth:1,
								                        borderColor:'#FE9A2E'
								                    }
								                },
								          data : [
								              {type : 'max', name: '最大值'}
								          ]
								      },
								      markLine : {
								    	  itemStyle : {
							                    normal: {
							                        color:'#000000',
							                        borderWidth:1,
							                        borderColor:'#74DF00'
							                    }
							                },
									      	 data : [{type : 'average', name: '平均线'}]
									      }
				               		 }
				                ]
				            };
				            myChart.setOption(option);
			        }
			    );
			 
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a  href="${ctx}/amazoninfo/productDirectory">产品目录列表</a></li>
		<li class="active"><a href="#">单产品评论分布</a></li>
	</ul>
 <div style="height:50px;text-align:center;font-weight: bold;font-size:18px">${dirCom.title}</div>
	 <div style="width: 98%;border:1px solid #ccc ;margin-bottom:10px">
			
			<div id="main" style="height:420px" ></div>
		</div>
 
	 	<div class="form-actions" style="width:100%;text-align:center">
			<input id="btnCancel" class="btn" type="button" value="Back" onclick="history.go(-1)"/>
		</div>
 
	
</body>
</html>
