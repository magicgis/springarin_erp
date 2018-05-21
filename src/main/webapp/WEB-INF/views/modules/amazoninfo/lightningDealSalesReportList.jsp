<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
	<div style="width: 550px;border:1px solid #ccc">
		<div id="${type}${country}${productName}${start}${end}" style="height:260px"></div>
	</div>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		var myChart;
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
	            myChart = ec.init(document.getElementById('${type}${country}${productName}${start}${end}'));
	            myChart.showLoading({
				    text: '正在努力的读取数据中...',    //loading话术
				});
				//ecahrts-----------------
				myChart.hideLoading();
	            var option = {
	            	title:{text:"${productName}销量统计图",x:'center'},		
	                tooltip : {
	                    trigger: 'item'
	                },
	                legend: {
	                	y:30,
	                	selected: {'${productName}':true},
	                    data:['${productName}']
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
						      name:'${productName}',
						      type:'line',
						      data:[<c:forEach items="${xAxis}" var="x" varStatus="i">${empty salesMap[x]?0:salesMap[x]}${i.last?'':','}</c:forEach>]
						  }
	                ]
	            };
	            myChart.setOption(option);
	        }
	    );
	</script>