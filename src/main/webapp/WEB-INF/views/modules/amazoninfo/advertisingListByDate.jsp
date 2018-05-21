<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>广告费用汇总统计</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	<script type="text/javascript" src="${ctxStatic}/echarts/js/esl.js"></script>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		var oldSearchFlag;
		$(document).ready(function() {
			
			if(!(top)){
				top = self;
			}
			
			$(".countryHref").click(function(){
				var params ={};
				params.active=  $(".btn-group .active").attr('act');
				$('#searchForm').attr('action','${ctx}/amazoninfo/advertising/listByDate?'+$.param(params));
				$("input[name='country']").val($(this).attr("key"));
				$("#date1").val("");
				$("#date2").val("");
				$("#searchForm").submit();
			});
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			$(".clicksCha").each(function(){
				//计算clicks差
				var clicksFlag = $(this).parent().find("td:eq(1)").text();
				var clicks = $(this).parent().find("td:eq(5)").text();
				var cha = clicks-clicksFlag;
				if(cha < 0){
					$(this).css("color","red");
				} else if(cha > 0){
					$(this).css("color","#009900");
				}
				$(this).text(cha);
				//计算Acos差
				var aCosFlag = $(this).parent().find("td:eq(4)").text();
				var aCos = $(this).parent().find("td:eq(8)").text();
				aCosFlag = parseFloat(aCosFlag);
				aCos = parseFloat(aCos);
				if(isNaN(aCosFlag)){
					aCosFlag = 0;
				}
				if(isNaN(aCos)){
					aCos = 0;
				}
				if(aCos - aCosFlag < 0){
					$(this).parent().find("td:eq(10)").css("color","red");
				} else if(aCos - aCosFlag > 0){
					$(this).parent().find("td:eq(10)").css("color","#009900");
				}
				var aCosCha = aCos - aCosFlag;
				$(this).parent().find("td:eq(10)").text(aCosCha.toFixed(2) + "%");
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
				$("#searchForm").submit();
			});
			
			$("#btnSubmit").click(function(){
				$('#searchForm').attr('action','${ctx}/amazoninfo/advertising/listByDate?');
				$("#searchForm").submit();
			});
			
			$("#btnExport").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						var country = '${advertising.country}';
						if(country == null || country.length == 0){
							$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/exportTotalByDate");
							$("#searchForm").submit();
							$('#searchForm').attr('action','${ctx}/amazoninfo/advertising/listByDate?');
						} else {
							$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/exportByCountryAndDate");
							$("#searchForm").submit();
							$('#searchForm').attr('action','${ctx}/amazoninfo/advertising/listByDate?');
						}
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
		});
		
		
		function searchType(searchFlag){
			if(oldSearchFlag==searchFlag){
				return;
			}
			$("#date1").val("");
			$("#date2").val("");
			var params = {};
			params.active =  $(".btn-group .active").attr('act');
			$("#searchFlag").val(searchFlag);
			//$('#searchForm').attr('action','${ctx}/amazoninfo/advertising/listByDate');
			$('#searchForm').attr('action','${ctx}/amazoninfo/advertising/listByDate?'+$.param(params));
			$("#searchForm").submit();
		}
		
		
		function timeOnChange(){
			var country = '${advertising.country}';
			var searchFlag = '${advertising.searchFlag}';
			var params = {};
			params.active =  $(".btn-group .active").attr('act');
			if(country == null && searchFlag != 0){
				params.date1 = $("#date1").val();
				params.date2 = $("#date2").val();
			}
			$('#searchForm').attr('action','${ctx}/amazoninfo/advertising/listByDate?'+$.param(params));
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
		
		function detail(country,timeStr){
			$("input[name='country']").val(country);
			$("#date1").val(timeStr);
			$("#date2").val(timeStr);
			$("#searchForm").submit();
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
		 }//-----------------------------------------------------------------------------------------
			top.$.jBox.closeTip();
			var myChart ,myChart1,myChart2;
			$(document).ready(function() {
				
				$($(".month").get((${month}-1))).parent().addClass("active");
				
				$("#ps").change(function(){
					var params = {};
					params.startDate = '${advertising.createDate}'; 
					params.endDate = '${advertising.dataDate}';
					params.active =  $(".btn-group .active").attr('act');
					console.log($(".btn-group .active").attr('act'));
					$("#ps").prop("disabled","disabled","disabled");
					myChart.dispose();
					myChart1.dispose();
					myChart2.dispose();
					
					$('#searchForm').attr('action','${ctx}/amazoninfo/advertising/listByDate?'+$.param(params));
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
			           
			            var option = {
			            	title:{text:'Clicks <spring:message code="sys_label_businessReport_statistics"/>',x:'center'},	
			                tooltip : {
			                    trigger: 'item'
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
			                        splitArea : {show : true},
			                        boundaryGap:[0,0.5]
			                    }
			                ],
			                series : [
								<c:forEach items="${clicksData}" var='clicks' varStatus="i">
									<c:set var="f1" value="true" />
								{
								      name:'${clicks.key}',
								      type:'line',
								      data:${clicks.value},
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
			            myChart.setOption(option);
			            
			            var option1 = {
				            	title:{text:'Average CPC <spring:message code="sys_label_businessReport_statistics"/>',x:'center'},	
				                tooltip : {
				                    trigger: 'item'
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
				                        data : ${xAxis},
				                   		boundaryGap:false
				                    }
				                ],
				                yAxis : [
				                    {
				                        type : 'value',
				                        scale: true,
				                        precision: 2, 
				                        splitNumber: 4, 
				                        splitArea : {show : true},
				                        axisLabel : {
				                            formatter: '{value}'
				                        },
				                        boundaryGap:[0, 0.05]
				                    }
				                ],
				                series : [
									<c:forEach items="${averageCPCData}" var="cpc" varStatus="i">
									{
									      name:'${cpc.key}',
									      type:'line',
									      data:${cpc.value},
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
				            	title:{text:'Acos <spring:message code="sys_label_businessReport_statistics"/>',x:'center'},	
				                tooltip : {
				                    trigger: 'item'
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
									<c:forEach items="${acosData}" var="acos" varStatus="i">
									{
									      name:'${acos.key}',
									      type:'line',
									      data:${acos.value},
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
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="${empty advertising.country?'active':''}"><a class="countryHref" href="#total" >总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${advertising.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>

	<c:if test="${not empty advertising.country}">
	<div class="btn-group" data-toggle="buttons-radio">    
		  <span act='1' class="btn btn-info ${active==1?'active':''}"><spring:message code="amaInfo_businessReport_columnStatistics"/></span>
		  <span act='2' class="btn btn-info ${active==2?'active':''}">Clicks <spring:message code="sys_label_businessReport_statistics"/></span>
		  <span act='3' class="btn btn-info ${active==3?'active':''}">Average CPC <spring:message code="sys_label_businessReport_statistics"/></span>
		  <span act='4' class="btn btn-info ${active==4?'active':''}">Acos<spring:message code="sys_label_businessReport_statistics"/></span>
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
	
		<form:form id="searchForm" modelAttribute="advertising" action="${ctx}/amazoninfo/advertising/listByDate" method="post" class="breadcrumb form-search">
		<div style="height: 30px;margin-top:10px">
		<ul class="nav nav-pills" style="width:300px;float:left;" id="myTab">
		<li data-toggle="pills" id="showTab0"><a href="#" onclick="javaScript:searchType('0')">By Day</a></li>
		<li data-toggle="pills" id="showTab1"><a href="#" onclick="javaScript:searchType('1')">By Week</a></li>
		<li data-toggle="pills" id="showTab2"><a href="#" onclick="javaScript:searchType('2')">By Month</a></li>
		</ul>
		
		<input id="searchFlag" name="searchFlag" type="hidden" value="${advertising.searchFlag}" />
		<input name="country" type="hidden" value="${advertising.country}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${orderBy}"/>
		
		<span style="float: right;">
			<c:if test="${advertising.searchFlag == '0'}"> <%--ByDay --%>
				<label><c:if test="${empty advertising.country}">节点一：</c:if></label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="date1" value="<fmt:formatDate value="${advertising.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="date1"/>
				&nbsp;-<c:if test="${empty advertising.country}">节点二：</c:if>&nbsp;
				<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="date2" value="<fmt:formatDate value="${advertising.dataDate}" pattern="yyyy-MM-dd" />" id="date2" class="input-small"/>
			</c:if>
			<c:if test="${advertising.searchFlag == '1'}"> <%--ByWeek --%>
				<label><c:if test="${empty advertising.country}">节点一：</c:if></label><input style="width: 100px" onclick="WdatePicker({isShowWeek:true,weekMethod:'MSExcel',onpicked:date1Week,errDealMode:3,firstDayOfWeek:1});" readonly="readonly"  class="Wdate" type="text" name="date1" value="${date1}" class="input-small" id="date1"/>
				&nbsp;-<c:if test="${empty advertising.country}">节点二：</c:if>&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowWeek:true,weekMethod:'MSExcel',onpicked:date2Week,errDealMode:3,firstDayOfWeek:1});" readonly="readonly"  class="Wdate" type="text" name="date2" value="${date2}" id="date2" class="input-small"/>
			</c:if>
			<c:if test="${advertising.searchFlag == '2'}"> <%--ByMonth --%>
				<label><c:if test="${empty advertising.country}">节点一：</c:if></label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="date1" value="<fmt:formatDate value="${advertising.createDate}" pattern="yyyy-MM"/>" class="input-small" id="date1"/>
				&nbsp;-<c:if test="${empty advertising.country}">节点二：</c:if>&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="date2" value="<fmt:formatDate value="${advertising.dataDate}" pattern="yyyy-MM" />" id="date2" class="input-small"/>
			</c:if>
		&nbsp;&nbsp;&nbsp;&nbsp;
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code="sys_but_search"/>"/>
		&nbsp;&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="<spring:message code="sys_but_export"/>"/>
		</span>
		</div>
	</form:form>
	<c:if test="${not empty advertising.country}">
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr> 
			    <th style="width: 40px"><spring:message code="sys_label_country"/></th>
				<th style="width: 120px" class="sort dates">Date</th>
				<th style="width: 40px" class="sort clicks">Clicks</th>
				<th style="width: 40px" class="sort averageCPC">Average CPC</th>
				<th style="width: 40px" class="sort totalSpend">Total Spend</th>
				<th style="width: 40px" class="sort conversion">平均 Conversion</th>
				<th style="width: 40px" class="sort acos">平均 Acos</th>
				<th style="width: 40px" class="sort orderSales">Ordered Product Sales</th>
				<th style="width: 50px" class="sort ordersPlaced"><div style="width:130px">Orders Placed</div></th>
			</tr>
		</thead>
		<tbody>
				<c:forEach items="${list}" var="advertising">
					<tr>
						<td>${fns:getDictLabel(advertising.country,'platform','')}</td>
						<td>${advertising.groupName}${type}${advertising.name}</td>
						<td>${advertising.clicks}</td>
						<td>
							<fmt:formatNumber value="${advertising.averageCPC}" maxFractionDigits="2"/>
						</td>
						<td><fmt:formatNumber value="${advertising.totalSpend}"  maxFractionDigits="2"/></td>
						<td><fmt:formatNumber value="${advertising.conversion}" maxFractionDigits="2"/>%</td>
						<td>
							<span style="${advertising.acos>15?'color:red;font-weight: bold;':''}">
							<fmt:formatNumber value="${advertising.acos}" maxFractionDigits="2"/>%</span>
						</td>
						<td><a href="#"  rel="popover" data-content="Same:<fmt:formatNumber value="${advertising.sameSkuOrderSales}" maxFractionDigits="2"/>;Other:<fmt:formatNumber value="${advertising.otherSkuOrderSales}" maxFractionDigits="2"/>">
							<fmt:formatNumber value="${advertising.orderSales}" maxFractionDigits="2"/></a></td>
						<td><a href="#"  rel="popover" data-content="Same:${advertising.sameSkuOrdersPlaced};Other:${advertising.otherSkuOrdersPlaced}">${advertising.ordersPlaced}</a></td>
					</tr>
				</c:forEach>	
		</tbody>
	</table>
	</c:if>
	<!-- 广告汇总比较 -->
	<c:if test="${empty advertising.country}">
	<table id="totalTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;" rowspan="2">平台</th>
				<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
					<th colspan="${fn:length(xAxis)=='2'?'4':'3' }" style="text-align: center;vertical-align: middle;
					    ${i.index==1?'color: #08c;':''}">
						${xAxis[i.index-1]}${type }
						<c:if test="${advertising.searchFlag=='1' }">(${tip[xAxis[i.index-1]] })</c:if>
					</th> 
				</c:forEach>
				<c:if test="${fn:length(xAxis) == '2'}">
					<th style="text-align: center;vertical-align: middle;" rowspan="2">clicks差</th>
					<th style="text-align: center;vertical-align: middle;" rowspan="2">Acos差</th>
				</c:if>
			</tr>
			<tr>
			  	<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
				    <th style="text-align: center;vertical-align: middle;
					    ${i.index==1?'color: #08c;':''}">clicks</th>
					<th style="text-align: center;vertical-align: middle;
					    ${i.index==1?'color: #08c;':''}">Average CPC(€)</th>
					<th style="text-align: center;vertical-align: middle;
					    ${i.index==1?'color: #08c;':''}">Total Spend(€)</th>
					<th style="text-align: center;vertical-align: middle;
					    ${i.index==1?'color: #08c;':''}">Acos</th>
					
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${fns:getDictList('platform')}" var="dic" >
				<c:if test="${dic.value ne 'com.unitek' && dic.value ne 'mx'}">
				<tr>
					<td style="text-align: center;vertical-align: middle;">${dic.label}</td>
					<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
						<c:choose>
						<c:when test="${data[dic.value][xAxis[i.index-1]].clicks >0}">
						<td style="text-align: center;vertical-align: middle; ${i.index==1?'color: #08c;':''}">
							<a href="#" onclick="detail('${dic.value}','${xAxis[i.index-1]}')" class="btn btn-small">${data[dic.value][xAxis[i.index-1]].clicks }</a>
						</td>
						<td style="text-align: center;vertical-align: middle;
					    	${i.index==1?'color: #08c;':''}">
					    	<a href="#" onclick="detail('${dic.value}','${xAxis[i.index-1]}')" class="btn btn-small">
								<fmt:formatNumber pattern="#######.##" value="${data[dic.value][xAxis[i.index-1]].averageCPC }" maxFractionDigits="2" minFractionDigits="2" />
							</a>
						</td>
						<td style="text-align: center;vertical-align: middle;
					    	${i.index==1?'color: #08c;':''}">
					    	<a href="#" onclick="detail('${dic.value}','${xAxis[i.index-1]}')" class="btn btn-small">
							<fmt:formatNumber pattern="#######.##" value="${data[dic.value][xAxis[i.index-1]].totalSpend }" maxFractionDigits="2" minFractionDigits="2" />
							</a>
						</td>
						<td style="text-align: center;vertical-align: middle;
					    	${i.index==1?'color: #08c;':''}">
					    	<span style="${data[dic.value][xAxis[i.index-1]].acos>15?'color:red;font-weight: bold;':''}">
							<fmt:formatNumber pattern="#######.##" value="${data[dic.value][xAxis[i.index-1]].acos }" maxFractionDigits="2" minFractionDigits="2" />%</span>
						</td>
						<c:if test="${fn:length(xAxis)=='3' && fn:length(xAxis)!=i.index }">
						<td style="text-align: center;vertical-align: middle;
					    	${i.index==1?'color: #08c;':''}" class="clicksCha"></td>
						</c:if>
						</c:when>
						<c:otherwise>
							<td style="text-align: center;vertical-align: middle;"></td>
							<td style="text-align: center;vertical-align: middle;"></td>
							<td style="text-align: center;vertical-align: middle;"></td>
							<td style="text-align: center;vertical-align: middle;"></td>
						</c:otherwise>
						</c:choose>
					</c:forEach>
					<c:if test="${fn:length(xAxis) == '2'}">
						<td style="text-align: center;vertical-align: middle;" class="clicksCha"></td>
						<td style="text-align: center;vertical-align: middle;" class="spendCha"></td>
					</c:if> 
				</tr>
				</c:if>
			</c:forEach>
			<tr>
				<td style="text-align: center;vertical-align: middle;">合计</td>
				<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
					<c:choose>
						<c:when test="${data['total'][xAxis[i.index-1]].clicks >0}">
							<td style="text-align: center;vertical-align: middle;
					    		${i.index==1?'color: #08c;':''}">${data['total'][xAxis[i.index-1]].clicks}</td>
							<td style="text-align: center;vertical-align: middle;
					    		${i.index==1?'color: #08c;':''}">
								<fmt:formatNumber pattern="#######.##" value="${data['total'][xAxis[i.index-1]].averageCPC }" maxFractionDigits="2" minFractionDigits="2" />
							</td>
							<td style="text-align: center;vertical-align: middle;
					    		${i.index==1?'color: #08c;':''}">
								<fmt:formatNumber pattern="#######.##" value="${data['total'][xAxis[i.index-1]].totalSpend}" maxFractionDigits="2" minFractionDigits="2" />
							</td>
							<td style="text-align: center;vertical-align: middle;
					    		${i.index==1?'color: #08c;':''}">
					    		<span style="${data['total'][xAxis[i.index-1]].acos>15?'color:red;font-weight: bold;':''}">
									<fmt:formatNumber pattern="#######.##" value="${data['total'][xAxis[i.index-1]].acos }" maxFractionDigits="2" minFractionDigits="2" />%</span>
							</td>
						</c:when>
						<c:otherwise>
							<td style="text-align: center;vertical-align: middle;"></td>
							<td style="text-align: center;vertical-align: middle;"></td>
							<td style="text-align: center;vertical-align: middle;"></td>
							<td style="text-align: center;vertical-align: middle;"></td>
						</c:otherwise>
						</c:choose>
				</c:forEach>
				<c:if test="${fn:length(xAxis) == '2'}">
					<td style="text-align: center;vertical-align: middle;
					    ${i.index==1?'color: #08c;':''}" class="clicksCha"></td>
					<td style="text-align: center;vertical-align: middle;
					    ${i.index==1?'color: #08c;':''}" class="spendCha"></td>
				</c:if>
			</tr>
		</tbody>
	</table>
	</c:if>
</body>
</html>
