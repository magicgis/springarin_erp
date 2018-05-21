<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊商业报表</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			
			if(!(top)){
				top = self;
			}
			
			$("#operationalExp").click(function(){
				
				var refundBillHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' >";
				refundBillHtml+="订单时间:&nbsp;<input style='width: 100px' onclick=WdatePicker({dateFmt:'yyyy-MM-dd'}) readonly='readonly'  class='Wdate' type='text' name='createDate' id='start' class='input-small' value=${fns:getBeforeDate('yyyy-MM-dd')}  />";
				refundBillHtml+="-<input style='width: 100px' onclick=WdatePicker({dateFmt:'yyyy-MM-dd'})  readonly='readonly'  class='Wdate' type='text' name='endDate' value=${fns:getBeforeDate('yyyy-MM-dd')} id='end' class='input-small' />";
				refundBillHtml+="</div>";
				var submitChild = function (v, h, f) {
					var params = {};
					params.searchType=parseInt($("#searchFlag").val())+1;
					params.date1=h.find("#start").val();
					params.date2=h.find("#end").val();
					loading('正在生成运营数据报表...');
					$.post("${ctx}/amazoninfo/amazonOperationalReport/byTimeExport",$.param(params),function(data){
						top.$.jBox.closeTip(); 
						var link="${ctx}/../data/site/amazonOperationReport/"+data;
						if(data!=''){
							$("#downExcel").html('<a target="_blank" href="" id="linkHref">Download</a>');
							$("#linkHref").attr("href",link);
						}else{
							top.$.jBox.tip("文件下载失败！","error",{persistent:false,opacity:0});
						}
					});
				    return true;
				};
				$.jBox(refundBillHtml, { title: "Export",width:600,submit: submitChild,persistent: true});
				
			/* 	var params = {};
				var type=parseInt($("#searchFlag").val())+1;
				var refundBillHtml="<div class='showChildrenHtml' style='text-align:center;margin-left:10px' >";
				refundBillHtml+="<form id='searchForm'action='${ctx}/amazoninfo/amazonOperationalReport/byTimeExport' method='post' class='form-horizontal' >";
				refundBillHtml+="订单时间:&nbsp;<input style='width: 100px' onclick=WdatePicker({dateFmt:'yyyy-MM-dd'}) readonly='readonly'  class='Wdate' type='text' name='createDate' id='satrt' class='input-small' value=${fns:getBeforeDate('yyyy-MM-dd')}  />";
				refundBillHtml+="-<input style='width: 100px' onclick=WdatePicker({dateFmt:'yyyy-MM-dd'})  readonly='readonly'  class='Wdate' type='text' name='endDate' value=${fns:getBeforeDate('yyyy-MM-dd')} id='end' class='input-small' />";
				refundBillHtml+="<input  type='hidden' value="+type+" name='searchType'/>";
				refundBillHtml+="&nbsp;&nbsp;<input id='opeExp' class='btn btn-primary' type='submit' value='导出'/>";
				refundBillHtml+="</form></div>";
				var submitChild = function (v, h, f) {
				};
				$.jBox(refundBillHtml, { title: "Export",width:600,submit: submitChild,persistent: true});		 */
			});
			
			$("#compareExp").click(function(){
				var type=parseInt($("#searchFlag").val())+1;
				$("#searchForm").attr("action","${ctx}/amazoninfo/amazonOperationalReport/exportCompareData?searchType="+type);
				$("#searchForm").submit();
				$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/listByDate?');
			});
			
			$("#compareExp2").click(function(){
				var type=parseInt($("#searchFlag").val())+1;
				$("#searchForm").attr("action","${ctx}/amazoninfo/amazonOperationalReport/exportCompareData2?searchType="+type);
				$("#searchForm").submit();
				$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/listByDate?');
			});
			
			$(".countryHref").click(function(){
				var params ={};
				params.active=  $(".btn-group .active").attr('act');
				$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/listByDate?'+$.param(params));
				$("input[name='country']").val($(this).attr("key"));
				var country = '${businessReport.country}';
				if((country == null || country.length == 0) || $(this).attr("key") == null){
					$("#date1").val("");
					$("#date2").val("");
					$("#createDate").val("");
					$("#dataDate").val("");
				}
				$("#searchForm").submit();
			});
			
			var length = '${fn:length(xAxis)}';
			$(".sessionNum").each(function(){
				if(length == 3){
					var sessionFlag = $(this).parent().find("td:eq(7)").text();
					var index = $(this).index()-2;
					var session = $(this).parent().find("td:eq("+index+")").text();
					$(this).text(session-sessionFlag);
				} else {
					//计算session差
					var sessionFlag = $(this).parent().find("td:eq(1)").text();
					var session = $(this).parent().find("td:eq(3)").text();
					var cha = session-sessionFlag;
					if(cha < 0){
						$(this).css("color","red");
					} else if(cha > 0){
						$(this).css("color","#009900");
					}
					$(this).text(session-sessionFlag);
					//计算转化率增长率
					var conversionFlag = $(this).parent().find("td:eq(2)").text();
					var conversion = $(this).parent().find("td:eq(4)").text();
					if(conversionFlag != null && conversion != null){
						conversionFlag = parseFloat(conversionFlag);
						conversion = parseFloat(conversion);
						if(isNaN(conversionFlag)){
							conversionFlag = 0;
						}
						if(isNaN(conversion)){
							conversion = 0;
						}
						if(conversion - conversionFlag < 0){
							$(this).parent().find("td:eq(6)").css("color","red");
						} else {
							$(this).parent().find("td:eq(6)").css("color","#009900");
						}
						var conversionRate = (conversion - conversionFlag);
						$(this).parent().find("td:eq(6)").text(conversionRate.toFixed(2) + "%");
					}
				}
			});
			
			if($("#searchFlag").val()==0){
		    	 $("#showTab0").addClass("active");
		    }else if($("#searchFlag").val()==1){
		    	$("#showTab1").addClass("active");
		    }else if($("#searchFlag").val()==2){
		    	$("#showTab2").addClass("active");
		    }else{
		    	$("#showTab0").addClass("active");
		    }
			
			oldSearchFlag= $("#searchFlag").val();
			// 表格排序
			var orderBy = $("#orderBy").val().split(" ");
			$("#contentTable th.sort").each(function(){
				if ($(this).hasClass(orderBy[0])){
					orderBy[1] = orderBy[1]&&orderBy[1].toUpperCase()=="DESC"?"down":"up";
					$(this).html($(this).html()+" <i class=\"icon icon-arrow-"+orderBy[1]+"\"></i>");
				}
			});
			$("#contentTable th.sort").click(function(){
				var order = $(this).attr("class").split(" ");
				var sort = $("#orderBy").val().split(" ");
				for(var i=0; i<order.length; i++){
					if (order[i] == "sort"){order = order[i+1]; break;}
				}
				if (order == sort[0]){
					sort = (sort[1]&&sort[1].toUpperCase()=="DESC"?"ASC":"DESC");
					$("#orderBy").val(order+" DESC"!=order+" "+sort?"":order+" "+sort);
				}else{
					$("#orderBy").val(order+" ASC");
				}
				page();
			});
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			$("#selectC").change(function(){
				var params = {};
				params.active =  $(".btn-group .active").attr('act');
				$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/listByDate?'+$.param(params));
				//$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/listByDate');
				$("#searchForm").submit();
			});
			
			$("#btnSubmit").click(function(){
				var params = {};
				params.active =  $(".btn-group .active").attr('act');
				$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/listByDate?'+$.param(params));
				$("#searchForm").submit();
			});
			
			$("#btnExport").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						var country = '${businessReport.country}';
						if(country == null || country.length == 0){
							$("#searchForm").attr("action","${ctx}/amazoninfo/businessReport/exportSessionByDate");
							$("#searchForm").submit();
							$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/listByDate?');
						} else {
							$("#searchForm").attr("action","${ctx}/amazoninfo/businessReport/exportByDate");
							$("#searchForm").submit();
							$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/listByDate?');
						}
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
		});
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			
			var params = {};
			params.active =  $(".btn-group .active").attr('act');
			$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/listByDate?'+$.param(params));
			$("#searchForm").submit();
        	return false;
        }
		
		function searchType(searchFlag){
			if(oldSearchFlag==searchFlag){
				return;
			}
			$("#date1").val("");
			$("#date2").val("");
			$("#createDate").val("");
			$("#dataDate").val("");
			var params = {};
			params.active =  $(".btn-group .active").attr('act');
			$("#searchFlag").val(searchFlag);
			//$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/listByDate');
			$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/listByDate?'+$.param(params));
			$("#searchForm").submit();
		}
		
		
		function timeOnChange(){
			var country = '${businessReport.country}';
			var searchFlag = '${businessReport.searchFlag}';
			var params = {};
			params.active =  $(".btn-group .active").attr('act');
			if(country == null && searchFlag != 0){
				params.date1 = $("#date1").val();
				params.date2 = $("#date2").val();
			}
			$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/listByDate?'+$.param(params));
			$("#searchForm").submit();
		}
		
		//ByWeek
		function date1Week(){
			var week = $dp.cal.getP('y')+ "-" +$dp.cal.getP('W','WW');
			$("#date1").attr("value",week);
			timeOnChange();
		}
		
		function date2Week(){
			var week = $dp.cal.getP('y')+ "-" +$dp.cal.getP('W','WW');
			$("#date2").attr("value",week);
			timeOnChange();
		}
		
		 function initActive(index){
			 if(index==0){
					$("#main1").show();
					$("#main2").show();
					$("#main").show();
					$("#main").css("width","100%");
					$("#main").css("width","100%");
					$("#main").css("width","100%");
					$("#main").css("float","clear");
					$("#main1").css("float","clear");
					$("#main2").css("float","clear");
					
					myChart.resize();
					myChart1.resize();
					myChart2.resize();
				}else if (index ==1){
					$("#main").show();
					$("#main1").hide();
					$("#main2").hide();
					$("#main").css("width","100%");
					$("#main").css("float","clear");
					$("#main1").css("float","clear");
					$("#main2").css("float","clear");
					
					myChart.resize();
				}else if (index ==2){
					$("#main1").show();
					$("#main").hide();
					$("#main2").hide();
					$("#main1").css("width","100%");
					$("#main").css("float","clear");
					$("#main1").css("float","clear");
					$("#main2").css("float","clear");
					
					myChart1.resize();
				}else if (index ==3){
					$("#main2").show();
					$("#main1").hide();
					$("#main").hide();
					$("#main2").css("width","100%");
					$("#main").css("float","clear");
					$("#main1").css("float","clear");
					$("#main2").css("float","clear");
					
					myChart2.resize();
				}else{
					$("#main").show();
					$("#main2").hide();
					$("#main1").hide();
					$("#main").css("width","100%");
					
					$("#main").css("float","clear");
					$("#main1").css("float","clear");
					$("#main2").css("float","clear");
					myChart.resize();
				}
		 }
		 
		 
		 function getData (){
			var params = {};
			params.active =  $(".btn-group .active").attr('act');
			$("#ps").prop("disabled","disabled");
			myChart.dispose();
			myChart1.dispose();
			myChart2.dispose();
			//window.location.href = "${ctx}/amazoninfo/businessReport/listByDate?"+$.param(params);
			
			$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/listByDate');
			$("#searchForm").submit();
			loading('<spring:message code="sys_label_tips_load_charts"/>');
			return true;
		 }
		 
		//-----------------------------------------------------------------------------------------
		top.$.jBox.closeTip();
		var myChart ,myChart1,myChart2;
		$(document).ready(function() {
			
			$($(".month").get((${month}-1))).parent().addClass("active");
			
			$("#ps").change(function(){
				var params = {};
				//params.startDate = '<fmt:formatDate value="${startDate}" pattern="yyyy-MM-dd"/>'; ${businessReport.createDate}
				//params.endDate = '<fmt:formatDate value="${endDate}" pattern="yyyy-MM-dd"/>';
				params.startDate = '${businessReport.createDate}'; 
				params.endDate = '${businessReport.dataDate}';
				//params.productName = encodeURI($("#ps").val());
				params.active =  $(".btn-group .active").attr('act');
				console.log($(".btn-group .active").attr('act'));
				$("#ps").prop("disabled","disabled","disabled");
				myChart.dispose();
				myChart1.dispose();
				myChart2.dispose();
				//window.location.href = "${ctx}/amazoninfo/businessReport/count?"+$.param(params);
				
					$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/listByDate?'+$.param(params));
					$("#searchForm").submit();
				loading('<spring:message code="sys_label_tips_load_charts"/>');
				
			});
			
			$(".btn-group span").click(function(){
				if($(".btn-group .active").text() == $(this).text()){
					return ;
				}
				var index = $(".btn-group span").index(this);
				initActive(index);
			});
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
		            myChart = ec.init(document.getElementById('main'));
		            myChart1 = ec.init(document.getElementById('main1'));
		            myChart2 = ec.init(document.getElementById('main2'));
		           
		           /*  var num = dayNumOfMonth('${year}','${month}');
		            var arr = [];
		            for ( var i = 1; i <= num; i++) {
		            	arr[i-1] = '${month}/'+i;
					} */
		            var option = {
		            	title:{text:'Sessions <spring:message code="sys_label_businessReport_statistics"/>',x:'center'},	
		               /*  tooltip : {
		                    trigger: 'item'
		                },*/
		                 tooltip : {
		                    trigger: 'axis',
		                    formatter:function(params){
		                    	 return params[1][1]+"<br/>"+params[0][0]+":"+params[0][2]+"<br/>"+
		                    	 params[1][0]+":"+params[1][2];
		                    }
		                }, 
		              //  legend: {
		              //  	y:'bottom',
		              //  	${show}
		              //      data:['德国','美国','英国','法国','日本','意大利','西班牙','加拿大']
		              //  },
		              legend: {
		                	y:30,
		                	selected: {'session':true,'广告':true},
		                    data:['session','广告']
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
		                        data :${xAxis},
		                   		boundaryGap:false
		                    }
		                ],
		                yAxis : [
		                    {
		                        type : 'value',
		                        min: 0,
		                        splitArea : {show : true},
		                        boundaryGap:[0,0.5]
		                    }
		                ],
		                series : [
								     
								{
								    name:'广告',
								    type:'line',
								    stack:'统计',
								    
								    data:[<c:forEach items="${adsData[businessReport.country]}" var='sess'  varStatus="i">${adsData[businessReport.country][sess.key]}${i.last?'':','}</c:forEach>],
									/* markLine : {
								    	 data : [
								            {type : 'average', name: '平均线'}
								        ]
								    } */
								},  
								{
								    name:'session',
								    type:'line',
								    stack:'统计',
								    
								    data:[<c:forEach items="${sessionsMap}" var='sess' varStatus="i"><c:set var="f1" value="true" />${sessionsMap[sess.key]}${i.last?'':','}</c:forEach>],
								   /*  markLine : {
								    	 data : [
								            {type : 'average', name: '平均线'}
								        ]
								    } */
								}
		                          
							
		                ]
		            };
		           
		           /*  myChart.on('click', function (params) {
		               console.log(params);
		            }); */
		            var newOption = {
			            	title:{text:'Sessions <spring:message code="sys_label_businessReport_statistics"/>',x:'center'},	
			               /*  tooltip : {
			                    trigger: 'item'
			                },*/
			                 tooltip : {
			                    trigger: 'axis',
			                    formatter:function(params){
			                    	 return params[1][1]+"<br/>"+params[0][0]+":"+params[0][2]+"<br/>"+
			                    	 params[1][0]+":"+(params[1][2]+params[0][2]);
			                    }
			                }, 
			              //  legend: {
			              //  	y:'bottom',
			              //  	${show}
			              //      data:['德国','美国','英国','法国','日本','意大利','西班牙','加拿大']
			              //  },
			              legend: {
			                	y:30,
			                	selected: {'session':true,'广告':true},
			                    data:['session','广告']
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
			                        data :${xAxis},
			                   		boundaryGap:true
			                    }
			                ],
			                yAxis : [
			                    {
			                        type : 'value',
			                        min: 0,
			                        splitArea : {show : true},
			                        boundaryGap:[0,0.5]
			                    }
			                ],
			                series : [
									     
									{
									    name:'广告',
									    type:'bar',
									    stack:'统计',
									    
									    data:[<c:forEach items="${adsData[businessReport.country]}" var='sess'  varStatus="i">${adsData[businessReport.country][sess.key]}${i.last?'':','}</c:forEach>],
										/* markLine : {
									    	 data : [
									            {type : 'average', name: '平均线'}
									        ]
									    } */
									},  
									{
									    name:'session',
									    type:'bar',
									    stack:'统计',
									    
									    data:[<c:forEach items="${sessionsMap}" var='sess' varStatus="i"><c:set var="f1" value="true" />${sessionsMap[sess.key]-adsData[businessReport.country][sess.key]}${i.last?'':','}</c:forEach>],
									   /*  markLine : {
									    	 data : [
									            {type : 'average', name: '平均线'}
									        ]
									    } */
									}
			                          
								
			                ]
			            };
		            myChart.setOption(newOption);
		            var ecConfig = require('echarts/config');
		            function eConsole(param) {
		                if(param.type=="magicTypeChanged"){
		                    if(param.magicType.bar==true||param.magicType.stack==true){
		                    	 myChart.setOption(newOption);
		                    }else{
		                    	 myChart.setOption(option);
		                    }
		                }
		            }
		            myChart.on(ecConfig.EVENT.MAGIC_TYPE_CHANGED, eConsole);
		            
		            var option1 = {
			            	title:{text:'Conversion <spring:message code="sys_label_businessReport_statistics"/>',x:'center'},	
			                tooltip : {
			                    trigger: 'item'
			                },
			              //  legend: {
			               // 	y:'bottom',
			               // 	${show}
			               //     data:['德国','美国','英国','法国','日本','意大利','西班牙','加拿大']
			               // },
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
			                        data : ${xAxis},
			                   		boundaryGap:false
			                    }
			                ],
			                yAxis : [
			                    {
			                        type : 'value',
			                        splitArea : {show : true},
			                        axisLabel : {
			                            formatter: '{value}%'
			                        },
			                        boundaryGap:[0,0.5]
			                    }
			                ],
			                series : [
								<c:forEach items="${conversionData}" var="conv" varStatus="i">
								{
								      name:'${conv.key}',
								      type:'bar',
								      data:${conv.value},
								      markPoint : {
									    	  itemStyle : {
								                    normal: {
								                        color:'#FF2D2D',
								                        borderWidth:1,
								                        borderColor:'#FE9A2E'
								                    }
								                },
								          data : [
								              {type : 'max', name: '最大值'},
								              {type : 'min', name: '最小值'}
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
								      	 data : [
								              {type : 'average', name: '平均线'}
								          ]
								      }
								  }
								${i.last?'':','}  
								</c:forEach>
			                ]
			            };
		            
		            myChart1.setOption(option1);
		            
		            
		            var option2 = {
			            	title:{text:'Orders Placed <spring:message code="sys_label_businessReport_statistics"/>',x:'center'},	
			                tooltip : {
			                    trigger: 'item'
			                },
			              //  legend: {
			               // 	y:'bottom',
			               // 	${show}
			               //     data:['德国','美国','英国','法国','日本','意大利','西班牙','加拿大']
			               // },
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
			                        data : ${xAxis},
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
								<c:forEach items="${ordersPlacedData}" var="orderPd" varStatus="i">
								{
								      name:'${orderPd.key}',
								      type:'bar',
								      data:${orderPd.value},
								 markPoint : {
								    	  itemStyle : {
							                    normal: {
							                        color:'#FF2D2D',
							                        borderWidth:1,
							                        borderColor:'#FE9A2E'
							                    }
							                },
							          data : [
							              {type : 'max', name: '最大值'},
							              {type : 'min', name: '最小值'}
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
								      	 data : [
								              {type : 'average', name: '平均线'}
								          ]
								      }
								  }
								${i.last?'':','}  
								</c:forEach>
			                ]
			            };
		            
		            myChart2.setOption(option2);
		           
		            initActive($(".btn-group .active").attr('act')-1);
		        }
		    );
		    <c:if test="${f1 != true && not empty productName}" >
		    	$("#nodata").show();
		    </c:if>
		});
		
		function sessionDetail(country,timeStr){
			$("input[name='country']").val(country);
			var searchFlag = '${businessReport.searchFlag}';
			$("#date1").val(timeStr);
			$("#searchForm").submit();
		}
	</script>
</head>
<body>

<!-- 
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/businessReport"><spring:message code="amaInfo_businessReport_byAsin"/></a></li>
		<li><a href="${ctx}/amazoninfo/businessReport/count"><spring:message code="amaInfo_businessReport_byAsin_charts"/></a></li>
		<li class="active"><a href="${ctx}/amazoninfo/businessReport/listByDate?searchFlag=0"><spring:message code="amaInfo_businessReport_byDate"/></a></li>
	</ul>
	-->
	
	<ul class="nav nav-tabs">
		<li class="${empty businessReport.country?'active':''}"><a class="countryHref" href="#total" >总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${businessReport.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>

	<c:if test="${not empty businessReport.country}">
	<div class="btn-group" data-toggle="buttons-radio">    
		  <span act='1' class="btn btn-info ${active==1?'active':''}"><spring:message code="amaInfo_businessReport_columnStatistics"/></span>
		  <span act='2' class="btn btn-info ${active==2?'active':''}"><spring:message code="amaInfo_businessReport_sessionStatistics"/></span>
		  <span act='3' class="btn btn-info ${active==3?'active':''}"><spring:message code="amaInfo_businessReport_conversionStatistics"/></span>
		  <span act='4' class="btn btn-info ${active==4?'active':''}"><spring:message code="amaInfo_businessReport_ordersPlacedStatistics"/></span>
	</div> 
	
	
	
	<div style="width: 100%;float:left;border:1px solid #ccc ;margin-bottom:10px">
		<div id="nodata" class="alert alert-success" style="height: 100px;vertical-align: middle;text-align: center;display: none">
  			<br/>
  			<h3><spring:message code="sys_label_no_data"/>~~~~</h3>
		</div>
		<div id="main" style="height:215px"></div>
		<div id="main1" style="height:215px"></div>
		<div id="main2" style="height:215px"></div>
	</div>
	</c:if>

	
		<form:form id="searchForm" modelAttribute="businessReport" action="${ctx}/amazoninfo/businessReport/listByDate" method="post" class="breadcrumb form-search">
		<div style="height: 30px;margin-top:10px">
		<ul class="nav nav-pills" style="width:300px;float:left;" id="myTab">
		<li data-toggle="pills" id="showTab0"><a href="#" onclick="javaScript:searchType('0')">By Day</a></li>
		<li data-toggle="pills" id="showTab1"><a href="#" onclick="javaScript:searchType('1')">By Week</a></li>
		<li data-toggle="pills" id="showTab2"><a href="#" onclick="javaScript:searchType('2')">By Month</a></li>
		</ul>
	   
		<input id="searchFlag" name="searchFlag" type="hidden" value="${businessReport.searchFlag}" />
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input name="country" type="hidden" value="${businessReport.country}"/>
		
		<span style="float: right;">
		 <spring:message code="amazon_sales_product_line"/>:<select id="typeGroup" style="width: 100px" name="groupName">
				<option value="">--All--</option>
				<c:forEach items="${groupType}" var="groupType">
					<option value="${groupType.id}" ${fn:trim(groupType.id) eq fn:trim(groupName)?'selected':''}>${groupType.name}</option>			
				</c:forEach>
		 </select>
		<c:if test="${empty businessReport.country}">
			<c:if test="${businessReport.searchFlag == '0'}"> <%--ByDay --%>
				<label>节点一：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="date1" value="<fmt:formatDate value="${businessReport.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="date1"/>
				&nbsp;节点二：&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="date2" value="<fmt:formatDate value="${businessReport.dataDate}" pattern="yyyy-MM-dd" />" id="date2" class="input-small"/>
			</c:if>
			<c:if test="${businessReport.searchFlag == '1'}"> <%--ByWeek --%>
				<label>节点一：</label><input style="width: 100px" onclick="WdatePicker({isShowWeek:true,weekMethod:'MSExcel',onpicked:date1Week,errDealMode:3,firstDayOfWeek:1});" readonly="readonly"  class="Wdate" type="text" name="date1" value="${date1}" class="input-small" id="date1"/>
				&nbsp;节点二：&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowWeek:true,weekMethod:'MSExcel',weekMethod:'MSExcel',onpicked:date2Week,errDealMode:3,firstDayOfWeek:1});" readonly="readonly"  class="Wdate" type="text" name="date2" value="${date2}" id="date2" class="input-small"/>
			</c:if>
			<c:if test="${businessReport.searchFlag == '2'}"> <%--ByMonth --%>
				<label>节点一：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="date1" value="<fmt:formatDate value="${businessReport.createDate}" pattern="yyyy-MM"/>" class="input-small" id="date1"/>
				&nbsp;节点二：&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="date2" value="<fmt:formatDate value="${businessReport.dataDate}" pattern="yyyy-MM" />" id="date2" class="input-small"/>
			</c:if>
		</c:if>
		<c:if test="${not empty businessReport.country}">
			<label></label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${businessReport.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="createDate"/>
			&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="dataDate" value="<fmt:formatDate value="${businessReport.dataDate}" pattern="yyyy-MM-dd" />" id="dataDate" class="input-small"/>
		</c:if>
		&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code="sys_but_search"/>"/>
		&nbsp;&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="<spring:message code="sys_but_export"/>"/>
		<c:if test="${empty businessReport.country}">
		&nbsp;&nbsp;<input id="compareExp" class="btn btn-primary" type="button" value="对比导出"/>
		&nbsp;&nbsp;<input id="compareExp2" class="btn btn-primary" type="button" value="对比导出2"/>
		   <input id="operationalExp" class="btn btn-primary" type="button" value="运营数据导出"/>
	       <span id='downExcel'></span> 
		</c:if>
		</span>
		</div>
	</form:form>
	<c:if test="${not empty businessReport.country}">
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr> 
			    <th style="width: 80px"><spring:message code="sys_label_country"/></th>
				<th style="width: 100px" class="sort dataDate">Date</th>
				<th style="width: 60px" class="sort sessions">Sessions</th>
				<th style="width: 50px" class="sort conversion">Conversion</th>
				<th style="width: 50px" class="sort ordersPlaced">Orders&nbsp;&nbsp;Placed</th>
				<th style="width: 50px" class="sort unitsOrdered">Units&nbsp;&nbsp;Ordered</th>
				<th style="width: 50px" class="sort unitSessionPercentage">Unit&nbsp;&nbsp;Session(%)</th>
				<th style="width: 50px" class="sort pageViews">Page&nbsp;&nbsp;Views</th>
				<th style="width: 50px" class="sort buyBoxPercentage">Buy&nbsp;&nbsp;Box(%)</th>
				<th style="width: 50px" class="aveSalesPerItem" >Avg. Sales</th>
			</tr>
		</thead>
		<tbody>
			<c:if test="${fn:length(page.list)>0}">
				<c:forEach begin="0" end="${fn:length(page.list)-1}" varStatus="i">
					<c:set  value="${page.list[(fn:length(page.list)-1-i.index)]}"  var="rep" />
					<tr>
						<td>${rep.countryStr}</td>
						<td>
							<c:if test="${'1' eq businessReport.searchFlag }">
								${fns:getWeekOfYear(rep.dataDate)}周
								&nbsp;&nbsp;&nbsp;&nbsp;${rep.dateSpan}
							</c:if>
							<c:if test="${'0' eq businessReport.searchFlag }">
								<fmt:formatDate value="${rep.dataDate}" pattern="yyyy-MM-dd" />
							</c:if>
							<c:if test="${'2' eq businessReport.searchFlag }">
								<fmt:formatDate value="${fns:addMonths(rep.dataDate,-1)}" pattern="yyyy/M月" />
							</c:if>
						</td>	
						<td>${rep.sessions}</td>
						<td><fmt:formatNumber value="${rep.conversion}" minFractionDigits="2"/>%</td>
						<td>${rep.ordersPlaced}</td>
						<td>${rep.unitsOrdered}</td>
						<td><fmt:formatNumber value="${rep.unitSessionPercentage}" minFractionDigits="2"/>%</td>
						<td>${rep.pageViews}</td>
						<td>${rep.buyBoxPercentage}%</td>
						<td><fmt:formatNumber value="${rep.aveSalesPerItem}"  maxFractionDigits="2"/>
						<c:choose>
							<c:when test="${rep.country eq 'com' }">$</c:when>
							<c:when test="${rep.country eq 'jp'}">￥</c:when>
							<c:when test="${rep.country eq 'ca'}">C$</c:when>
							<c:when test="${rep.country eq 'mx'}">$</c:when>
							<c:when test="${rep.country eq 'uk'}">￡</c:when>
							<c:otherwise>€</c:otherwise>
						</c:choose>
						
						</td>
					</tr>
				</c:forEach>	
			</c:if>
		</tbody>
	</table>
	</c:if>
	<!-- session统计比较 -->
	<c:if test="${empty businessReport.country}">
	<table id="totalTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;" rowspan="2">平台</th>
				<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
					<th colspan="${fn:length(xAxis)=='2'?'2':'3' }" style="text-align: center;vertical-align: middle;
					    ${i.index==1?'color: #08c;':''}">
						${xAxis[i.index-1]}${type }
						<c:if test="${businessReport.searchFlag=='1' }">(${tip[xAxis[i.index-1]] })</c:if>
					</th> 
				</c:forEach>
				<c:if test="${fn:length(xAxis) == '2'}">
					<th style="text-align: center;vertical-align: middle;" rowspan="2">session差</th>
					<th style="text-align: center;vertical-align: middle;" rowspan="2">转化率差</th>
				</c:if>
			</tr>
			<tr>
			  	<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
				    <th style="text-align: center;vertical-align: middle;
					    ${i.index==1?'color: #08c;':''}">session</th>
					<th style="text-align: center;vertical-align: middle;
					    ${i.index==1?'color: #08c;':''}">转化率</th>
					<c:if test="${fn:length(xAxis)=='3' && fn:length(xAxis)!=i.index }">
						<th style="text-align: center;vertical-align: middle;
					    ${i.index==1?'color: #08c;':''}">session差</th>
					</c:if>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${fns:getDictList('platform')}" var="dic" >
				<c:if test="${dic.value ne 'com.unitek'}">
				<tr>
					<td style="text-align: center;vertical-align: middle;">${dic.label}</td>
					<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
						<c:choose>
						<c:when test="${data[dic.value][xAxis[i.index-1]].sessions >0}">
						<td style="text-align: center;vertical-align: middle; ${i.index==1?'color: #08c;':''}">
							<a href="#" onclick="sessionDetail('${dic.value}','${xAxis[i.index-1]}')" class="btn btn-small">${data[dic.value][xAxis[i.index-1]].sessions }</a>
						</td>
						<td style="text-align: center;vertical-align: middle;
					    	${i.index==1?'color: #08c;':''}">
							<fmt:formatNumber pattern="#######.##" value="${data[dic.value][xAxis[i.index-1]].conversion }" maxFractionDigits="2" minFractionDigits="2" />%
						</td>
						<c:if test="${fn:length(xAxis)=='3' && fn:length(xAxis)!=i.index }">
						<td style="text-align: center;vertical-align: middle;
					    	${i.index==1?'color: #08c;':''}" class="sessionNum"></td>
						</c:if>
						</c:when>
						<c:otherwise>
							<td style="text-align: center;vertical-align: middle;"></td>
							<td style="text-align: center;vertical-align: middle;"></td>
							<c:if test="${fn:length(xAxis)=='3' && fn:length(xAxis)!=i.index }">
								<td style="text-align: center;vertical-align: middle;"></td>
							</c:if>
						</c:otherwise>
						</c:choose>
					</c:forEach>
					<c:if test="${fn:length(xAxis) == '2'}">
						<td style="text-align: center;vertical-align: middle;" class="sessionNum"></td>
						<td style="text-align: center;vertical-align: middle;" class="sessionRate"></td>
					</c:if> 
				</tr>
				</c:if>
			</c:forEach>
			<tr>
				<td style="text-align: center;vertical-align: middle;">合计</td>
				<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
					<c:choose>
						<c:when test="${data['total'][xAxis[i.index-1]].sessions >0}">
							<td style="text-align: center;vertical-align: middle;
					    		${i.index==1?'color: #08c;':''}">${data['total'][xAxis[i.index-1]].sessions}</td>
							<td style="text-align: center;vertical-align: middle;
					    		${i.index==1?'color: #08c;':''}">
								<fmt:formatNumber pattern="#######.##" value="${data['total'][xAxis[i.index-1]].conversion/8}" maxFractionDigits="2" minFractionDigits="2" />%
							</td>
							<c:if test="${fn:length(xAxis)=='3' && fn:length(xAxis)!=i.index }">
							<td style="text-align: center;vertical-align: middle;
					    		${i.index==1?'color: #08c;':''}" class="sessionNum"></td>
							</c:if>
						</c:when>
						<c:otherwise>
							<td style="text-align: center;vertical-align: middle;"></td>
							<td style="text-align: center;vertical-align: middle;"></td>
							<c:if test="${fn:length(xAxis)=='3' && fn:length(xAxis)!=i.index }">
								<td style="text-align: center;vertical-align: middle;"></td>
							</c:if>
						</c:otherwise>
						</c:choose>
				</c:forEach>
				<c:if test="${fn:length(xAxis) == '2'}">
					<td style="text-align: center;vertical-align: middle;
					    ${i.index==1?'color: #08c;':''}" class="sessionNum"></td>
					<td style="text-align: center;vertical-align: middle;
					    ${i.index==1?'color: #08c;':''}" class="sessionRate"></td>
				</c:if>
			</tr>
		</tbody>
	</table>
	</c:if>
</body>
</html>
