<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊商业报表</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
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
		if(!top){
			top = self;
		}
		var oldSearchFlag = "";
		top.$.jBox.closeTip();
		var myChart ,myChart1;
		$(document).ready(function() {
			if($("#searchFlag").val()==0){
		    	 $("#showTab0").addClass("active");
		    }else if($("#searchFlag").val()==1){
		    	$("#showTab1").addClass("active");
		    }else if($("#searchFlag").val()==2){
		    	$("#showTab2").addClass("active");
		    }else{
		    	$("#showTab0").addClass("active");
		    }

			$(".isChange").click(function(){
				if(this.checked){
					$("#isChange").val("1");
				}else{
					$("#isChange").val("0");
				}
				$("#ps").val("");
				$("#searchForm").submit();
			});
			
			oldSearchFlag= $("#searchFlag").val();
			
			$("#countExport").click(function(){
				if($("#ps").val()==""){
					$.jBox.tip('请选择产品名');
					return;
				}
				var params = {};
				params.date1 = $("#date1").val();
				params.date2 = $("#date2").val();
				params.searchFlag = $("#searchFlag").val();
				params.productName = encodeURI($("#ps").val());
				params.active =  $(".btn-group .active").attr('act');
				$("#countExport").attr("href","${ctx}/amazoninfo/businessReport/countExport?"+$.param(params));
			});
			
			$(".total").each(function(){
				var i = $(this).parent().find("td").index($(this));
				var num = 0;
				$("tbody tr").find("td:eq("+i+")").each(function(){
					if($.isNumeric($(this).text())){
						num += parseInt($(this).text());
					}
				});
				$(this).text(num);
			});
			
			$(".adsTotal").each(function(){
				var i = $(this).parent().find("td").index($(this));
				var num = 0;
				$("tbody tr").find("td:eq("+i+")").each(function(){
					if($.isNumeric($(this).text())){
						num += parseInt($(this).text());
					}
				});
				$(this).text(num);
			});
			
			$("#ps").change(function(){
				
				var params = {};
				params.date1 = $("#date1").val();
				params.date2 = $("#date2").val();
				params.country = $("#country").val();
				params.isChange = $("#isChange").val();
				params.searchFlag = $("#searchFlag").val();
				params.productName = encodeURI($("#ps").val());
				params.active =  $(".btn-group .active").attr('act');
				$("#ps").prop("disabled","disabled");
				myChart.dispose();
				myChart1.dispose();
				window.location.href = "${ctx}/amazoninfo/businessReport/imageAnalysis?"+$.param(params);
				loading('<spring:message code="sys_label_tips_load_charts"/>');
			});
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("select[name='productName']").val("");
				$("select[name='productName']").removeClass('required');
				$("#searchForm").submit();
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
		            var newOption = {
			            	title:{text:'Sessions&Clicks <spring:message code="sys_label_businessReport_statistics"/>',x:'center'},	
				             tooltip : {
				                 trigger: 'axis',
				                 formatter:function(params){
					                 //console.log(newOption.series);
					                 console.log(params);
					                 var show=params[0][1]+"<br/>";
					                 for(var i=0;i<params.length;i++){
					                	 show+=params[i][0]+":"+params[i][2]+"<br/>";
						             }
			                    	 return show;
			                    }
				             }, 
			                legend: {
			                	y:25,
			                	data:['Session','ADClicks']
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
			                grid:{
			                	height:300
			                },
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
			                        splitArea : {show : true},
			                        min: 0,
			                        boundaryGap:[0,0.5]
			                    }
			                ],
			                series : [
									{
									    name:'Session',
									    type:'line',
									    stack:'Session统计',
									    data:[<c:forEach items="${sessionsMap[country]}" var='sess' varStatus="i"><c:if test="${not empty adsData[country][sess.key]}">${sessionsMap[country][sess.key]}</c:if><c:if test="${empty adsData[country][sess.key]}">${sessionsMap[country][sess.key]}</c:if>${i.last?'':','}</c:forEach>],
										markLine : {
										  	data : [
										            {type : 'average', name: '平均线'}
										        ]
										 },
						                 markPoint : {
						                    symbolSize:0,  
						                    showDelay:0,   
						                    itemStyle:{  
						                        normal:{
					                        	    label:{  
					                                    show: true,  
					                                    position: 'top',  
					                                    formatter: function (param,d,val) { 
								                        	return '首图';
				                                   		}    
			                                    	}  
				                                }  
						                    },
							                data : [
												<c:forEach items="${eventMap[country]}" var="x" varStatus="i">
												 	{value :${x.value}, xAxis: '${x.key}',yAxis:'2000'}${i.last?'':','}
												</c:forEach>	
						                    ]
							             },
									},
									{
									    name:'ADClicks',
									    type:'line',
									    stack:'ADClicks统计',
									    data:[<c:forEach items="${sessionsMap[country]}" var='sess'  varStatus="i">${empty adsData[country][sess.key]?0:adsData[country][sess.key].clicks}${i.last?'':','}</c:forEach>],
									    markLine : {
									    	data : [
									            {type : 'average', name: '平均线'}
									        ]
									    }
									}
			                ]
			            };
		            <c:if test="${not empty sessionsData && not empty productName}" >
		               myChart.setOption(newOption);
		            </c:if>
		             
		            var option1 = {
			            	title:{
			            		text:'Conversion&CTR <spring:message code="sys_label_businessReport_statistics"/>',
			            		x:'center'
			            	},	
			            	tooltip : {
				                 trigger: 'axis',
				                 formatter:function(params){
					                 //console.log(newOption.series);
					                 console.log(params);
					                 var show=params[0][1]+"<br/>";
					                 for(var i=0;i<params.length;i++){
					                	 show+=params[i][0]+":"+params[i][2]+"<br/>";
						             }
			                    	 return show;
			                    }
				             }, 
			                legend: {
			                	y:25,
			                    data:['Conversion','AD CTR']
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
			                grid:{
			                	height:300
			                },
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
			        	            precision: 2,
			                        boundaryGap:[0.01 ,0.01]
			                    }
			                ],
			                series : [
								<c:forEach items="${conversionData}" var="conv" varStatus="i">
								{
								      name:'Conversion',
								      type:'line',
								      data:${conv.value},
								      markLine : {
								      	 data : [
								              {type : 'average', name: '平均线'}
								          ]
								      },
								  }
								${i.last?'':','}  
								</c:forEach>
								,{
								    name:'AD CTR',
								    type:'line',
								    stack:'AD CTR统计',
								    data:[<c:forEach items="${sessionsMap[country]}" var='sess'  varStatus="i">${empty adsData[country][sess.key]?0:adsData[country][sess.key].onePageBid}${i.last?'':','}</c:forEach>],
								    markLine : {
								    	data : [
								            {type : 'average', name: '平均线'}
								        ]
								    },
					                 markPoint : {
						                    symbolSize:0,  
						                    showDelay:0, 
						                    itemStyle:{  
						                        normal:{
					                        	    label:{  
					                                    show: true,  
					                                    position: 'top',  
					                                    formatter: function (param,d,val) { 
								                        	return '首图';
				                                   		}    
			                                    	}  
				                                }  
						                    },
							                data : [
													<c:forEach items="${eventMap[country]}" var="x" varStatus="m">
													 	{value :${x.value}, xAxis: '${x.key}',yAxis:'2000'}${m.last?'':','}
													</c:forEach>	
							                    ]
							             },
								}
			                ]
			            };
		            
		            myChart1.setOption(option1);
		            <%--initActive($(".btn-group .active").attr('act')-1);--%>
		            initActive(${active-1});
		        }
		    );
		    <c:if test="${empty sessionsData && not empty productName}" >
		    	$("#nodata").show();
		    </c:if>
		});
		 function initActive(index){
			 if(index==1){
					$(".title").attr("colspan","2");
					$(".conver").hide();
					$(".session").show();
					$(".ads").show();
					$("#main1").hide();
					$("#main").show();
					$("#main").css("width","100%");
					$("#main1").css("float","clear");
					$("#main").css("float","clear");
					myChart.resize();
				}else if (index ==2){
					$(".title").attr("colspan","1");
					$(".conver").show();
					$(".session").hide();
					$(".ads").hide();
					$("#main").hide();
					$("#main1").show();
					$("#main1").css("width","100%");
					$("#main1").css("float","clear");
					$("#main").css("float","clear");
					myChart1.resize();
				}else{
					$(".title").attr("colspan","3");
					$(".conver").show();
					$(".session").show();
					$(".ads").show();
					$("#main").show();
					$("#main1").show();
					$("#main1").css("width","49%");
					$("#main").css("width","49%");
					myChart.resize();
					myChart1.resize();
					$("#main1").css("float","left");
					$("#main").css("float","left");
				}
		 }
		 
		 
		 function getData (){
			var params = {};
			params.date1 = $("#date1").val();
			params.date2 = $("#date2").val();
			params.searchFlag = $("#searchFlag").val();
			params.productName = encodeURI($("#ps").val());
			params.active =  $(".btn-group .active").attr('act');
			$("#ps").prop("disabled","disabled");
			myChart.dispose();
			myChart1.dispose();
			window.location.href = "${ctx}/amazoninfo/businessReport/imageAnalysis?"+$.param(params);
			loading('<spring:message code="sys_label_tips_load_charts"/>');
			return true;
		 }

		function searchType(searchFlag){
			if(oldSearchFlag==searchFlag){
				return;
			}
			$("#date1").val("");
			$("#date2").val("");
			$("#searchFlag").val(searchFlag);
			var params = {};
			params.active =  $(".btn-group .active").attr('act');
			$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/imageAnalysis?'+$.param(params));
			$("#searchForm").submit();
		}
		
		function timeOnChange(){
			var params = {};
			params.active =  $(".btn-group .active").attr('act');
			$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/imageAnalysis?'+$.param(params));
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
		
		/*  function dayNumOfMonth(Year,Month){
             var d = new Date(Year,Month,0);
             return d.getDate();
         } */
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
	</ul>
	<form:form id="searchForm" modelAttribute="businessReport" action="${ctx}/amazoninfo/businessReport/imageAnalysis" method="post" class="breadcrumb form-search">
		<div style="height: 50px;line-height: 50px;">
			<input  name="country" type="hidden" value="${country}" id="country"/>
			<input id="searchFlag" name="searchFlag" type="hidden" value="${searchFlag}" />
			<input id="isChange" name="isChange" type="hidden" value="${isChange}" />
			<%--<ul class="nav nav-pills" style="width:300px;float:left;" id="myTab">
				<li data-toggle="pills" id="showTab0"><a href="#" onclick="javaScript:searchType('0')">By Day</a></li>
				<li data-toggle="pills" id="showTab1"><a href="#" onclick="javaScript:searchType('1')">By Week</a></li>
				<li data-toggle="pills" id="showTab2"><a href="#" onclick="javaScript:searchType('2')">By Month</a></li>
			</ul> --%>
			<div style="float: right;">
				<spring:message code="amaInfo_businessReport_productName"/>:<select name="productName" id="ps" style="width: 280px">
					<option value=""  ${empty productName?'selected':''}>--<spring:message code="sys_label_businessReport_select_product"/>--</option>
					<c:forEach items="${productNames}" var="pName">
							<option value="${pName}" ${fn:trim(pName) eq fn:trim(productName)?'selected':''}>${pName}</option>			
					</c:forEach>
				</select>
				<%--<input type="checkbox" class="isChange" value="1" ${isChange eq '1'?'checked':''}/>改过首图 --%>
				<c:if test="${searchFlag == '0'}"> <%--ByDay --%>
					<label></label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="date1" value="${date1}" class="input-small" id="date1"/>
					&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="date2" value="${date2}" id="date2" class="input-small"/>
				</c:if>
				<c:if test="${searchFlag == '1'}"> <%--ByWeek --%>
					<label></label><input style="width: 100px" onclick="WdatePicker({isShowWeek:true,weekMethod:'MSExcel',onpicked:date1Week,errDealMode:3,firstDayOfWeek:1});" readonly="readonly"  class="Wdate" type="text" name="date1" value="${date1}" class="input-small" id="date1"/>
					&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowWeek:true,weekMethod:'MSExcel',onpicked:date2Week,errDealMode:3,firstDayOfWeek:1});" readonly="readonly"  class="Wdate" type="text" name="date2" value="${date2}" id="date2" class="input-small"/>
				</c:if>
				<c:if test="${searchFlag == '2'}"> <%--ByMonth --%>
					<label></label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="date1" value="${date1}" class="input-small" id="date1"/>
					&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="date2" value="${date2}" id="date2" class="input-small"/>
				</c:if>
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
			    <%--<a class="btn btn-primary"  id="countExport" >导出</a> --%>
			</div>
		</div>
		<%--<div class="btn-group" data-toggle="buttons-radio">
			  <span act='1' class="btn btn-info ${active==1?'active':''}">&nbsp;&nbsp;<spring:message code="amaInfo_businessReport_columnStatistics"/>&nbsp;&nbsp;</span>
			  <span act='2' class="btn btn-info ${active==2?'active':''}"><spring:message code="amaInfo_businessReport_sessionStatistics"/></span>
			  <span act='3' class="btn btn-info ${active==3?'active':''}"><spring:message code="amaInfo_businessReport_conversionStatistics"/></span>
		</div> --%>
	</form:form>
	<div style="width: 100%;float:left;border:1px solid #ccc">
		<div id="nodata" class="alert alert-success" style="height: 100px;vertical-align: middle;text-align: center;display: none">
  			<br/>
  			<h3><spring:message code="sys_label_no_data"/>~~~~</h3>
		</div>
		<div id="main" style="height:460px"></div>
		<div id="main1" style="height:460px"></div>
	</div>
	<div style="float: left;width:93%">
		<div style="margin-top:10px">
		<c:if test="${not empty sessionDataMap }">
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr> 
						<td style="text-align: center;vertical-align: middle">日期</td>
						<td style="text-align: center;vertical-align: middle">Session</td>
						<td style="text-align: center;vertical-align: middle">Conversion</td>
						<td style="text-align: center;vertical-align: middle">Clicks</td>
						<td style="text-align: center;vertical-align: middle">CTR</td>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${sessionDataMap}" var="session" varStatus="i">
						<tr>
							<td style="text-align: center;vertical-align: middle">${session.key}${type }${tips[session.key] }</td>
							<c:forEach items="${countrySet}" var="countryName">
								<td style="text-align: center;vertical-align: middle" class="session" >${session.value[countryName] }</td>
								<td class="conver" style="background-color:#D2E9FF;text-align: center;vertical-align: middle">${conversionDataMap[session.key][countryName]}${empty conversionDataMap[session.key][countryName]?'':'%'}</td>
								<td style="text-align: center;vertical-align: middle"  class="ads">${adsData[countryName][session.key].clicks }</td>
								<td style="text-align: center;vertical-align: middle"  class="ads">${adsData[countryName][session.key].onePageBid }${empty adsData[countryName][session.key].onePageBid?'':'%'}</td>
							</c:forEach>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
		</div>
	</div>
</body>
</html>
