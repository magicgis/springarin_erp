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
				params.searchFlag = $("#searchFlag").val();
				params.productName = encodeURI($("#ps").val());
				params.active =  $(".btn-group .active").attr('act');
				$("#ps").prop("disabled","disabled");
				myChart.dispose();
				myChart1.dispose();
				window.location.href = "${ctx}/amazoninfo/businessReport/count?"+$.param(params);
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
		           
		           /*  var num = dayNumOfMonth('${year}','${month}');
		            var arr = [];
		            for ( var i = 1; i <= num; i++) {
		            	arr[i-1] = '${month}/'+i;
					} */
		            var option = {
		            	title:{text:'Sessions <spring:message code="sys_label_businessReport_statistics"/>',x:'center'},	
		                tooltip : {
		                    trigger: 'item'
		                },
		                legend: {
		                	y:30,
		                	${show}
		                    data:['德国','美国','英国','法国','日本','意大利','西班牙','加拿大','total']
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
							<c:forEach items="${sessionsData}" var='sess' varStatus="i">
								<c:set var="f1" value="true" />
							{
							      name:'${sess.key}',
							      type:'line',
							      data:${sess.value},
							      
							      markLine : {
							      	 data : [
							              {type : 'average', name: '平均线'}
							          ]
							      }
							  }
							${i.last?'':','}  
							</c:forEach>
		                ]
		            };
		           
		            
		            var newOption = {
			            	title:{text:'Sessions <spring:message code="sys_label_businessReport_statistics"/>',x:'center'},	
				             tooltip : {
				                 trigger: 'axis',
				                 axisPointer : {            // 坐标轴指示器，坐标轴触发有效
				                     type : 'axis'        // 默认为直线，可选为：'line' | 'shadow'
				                 },
				                 formatter:function(params){
					                 //console.log(newOption.series);
					                 console.log(params);
					                 var show=params[0][1]+"<br/>";
					                 for(var i=0;i<params.length-1;i=i+2){
					                    show+=params[i][0]+":"+params[i][2]+"<br/>"+params[i+1][0]+":"+(params[i+1][2]+params[i][2])+"<br/>";
					                 }  
			                    	 return show;
			                    }
				             }, 
				            /*  tooltip : {
				                    trigger: 'axis',
				                    formatter:function(params){
				                    //	console.log(newOption.series);
				                    	 return params[1][1]+"<br/>"+params[0][0]+":"+params[0][2]+"<br/>"+
				                    	 params[1][0]+":"+(params[1][2]+params[0][2]);
				                    }
				                },  */
			                legend: {
			                	y:30,
			                    selected: {'total':true,'total广告':true,<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'&&dic.value ne 'mx'}">'${dic.label}':false,'${dic.label}广告':false${i.last?'':','}</c:if></c:forEach>},
				                data:['total','total广告',<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'&&dic.value ne 'mx'}">'${dic.label}','${dic.label}广告'${i.last?'':','}</c:if></c:forEach>]
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
			                        splitArea : {show : true},
			                        min: 0,
			                        boundaryGap:[0,0.5]
			                    }
			                ],
			                series : [
									{
									    name:'total广告',
									    type:'bar',
									    stack:'total统计',
									    data:[<c:forEach items="${sessionsMap['total']}" var='sess'  varStatus="i">${empty adsData['total'][sess.key]?0:adsData['total'][sess.key]}${i.last?'':','}</c:forEach>],
									}, 
									{
									    name:'total',
									    type:'bar',
									    stack:'total统计',
									    data:[<c:forEach items="${sessionsMap['total']}" var='sess' varStatus="i"><c:set var="f1" value="true" /><c:if test="${not empty adsData['total'][sess.key]}">${sessionsMap['total'][sess.key]-adsData['total'][sess.key]}</c:if><c:if test="${empty adsData['total'][sess.key]}">${sessionsMap['total'][sess.key]}</c:if>${i.last?'':','}</c:forEach>],
									},
									//<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'&&dic.value ne 'mx'}">
									{
									      name:'${dic.label}广告',
									      type:'bar',
									      stack:'${dic.label}统计',
									      data:[<c:forEach items="${sessionsMap[dic.value]}" var='sess'  varStatus="i">${empty adsData[dic.value][sess.key]?0:adsData[dic.value][sess.key]}${i.last?'':','}</c:forEach>],
									},
									{
									      name:'${dic.label}',
									      type:'bar',
									      stack:'${dic.label}统计',
									      data:[<c:forEach items="${sessionsMap[dic.value]}" var='sess' varStatus="i"><c:if test="${not empty adsData[dic.value][sess.key]}">${sessionsMap[dic.value][sess.key]-adsData[dic.value][sess.key]}</c:if><c:if test="${empty adsData[dic.value][sess.key]}">${sessionsMap[dic.value][sess.key]}</c:if>${i.last?'':','}</c:forEach>],
									}
									${i.last?'':','}
								//</c:if></c:forEach>
			                ]
			            };
		            <c:if test="${not empty productName}" >
		               myChart.setOption(newOption);
		            </c:if>
		            
		            var ecConfig = require('echarts/config');
		            function eConsole(param) {
		                if(param.type=="magicTypeChanged"){
		                    if(param.magicType.bar==true||param.magicType.stack==true){
		                    	 myChart.clear(); 
		                    	 myChart.setOption(newOption);
		                    }else{
		                    	 myChart.clear(); 
		                    	 myChart.setOption(option);
		                    }
		                }
		            }
		            myChart.on(ecConfig.EVENT.MAGIC_TYPE_CHANGED, eConsole);
		            
		           myChart.on(ecConfig.EVENT.LEGEND_SELECTED, function (param){//
		            	var selected = param.selected; 
		               // console.log(param);
		               var flag=false;
		               if(newOption.legend.selected[param.target]){
		            	   newOption.legend.selected[param.target]=false;
		            	   for (var name in selected) { 
			            		 if(name.indexOf("广告")!=-1){
			            			 flag=true;
			            			 break;
			            		 }
		            	   }		 
		            	   if(param.target.indexOf("广告")==-1){
	            	        	 newOption.legend.selected[param.target+"广告"]=false; 
	            	       }else{
	            	        	 newOption.legend.selected[param.target.split("广")[0]]=false; 
	            	       }
		               }else{
		            	   newOption.legend.selected[param.target]=true;
			            	for (var name in selected) { 
			            		 if(name.indexOf("广告")!=-1){
			            			 flag=true;
			            		 }
			            	     if(selected[name]){
			            	         if(name.indexOf("广告")==-1){
			            	        	 newOption.legend.selected[name+"广告"]=true; 
			            	         }else{
			            	        	 newOption.legend.selected[name.split("广")[0]]=true; 
			            	         }
			            	     }
			            	 }
		               }
		                
		            	if(flag){
		            		 myChart.setOption(newOption);
		            	}
		            });
		            
		            
		             
		            var option1 = {
			            	title:{text:'Conversion <spring:message code="sys_label_businessReport_statistics"/>',x:'center'},	
			                tooltip : {
			                    trigger: 'item'
			                },
			                legend: {
			                	y:30,
			                	${show}
			                    data:['德国','美国','英国','法国','日本','意大利','西班牙','加拿大','total']
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
			                            formatter: '{value}%'
			                        },
			                    }
			                ],
			                series : [
								<c:forEach items="${conversionData}" var="conv" varStatus="i">
								{
								      name:'${conv.key}',
								      type:'bar',
								      data:${conv.value},
								     
								      markLine : {
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
		            initActive($(".btn-group .active").attr('act')-1);
		        }
		    );
		    <c:if test="${f1 != true && not empty productName}" >
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
			window.location.href = "${ctx}/amazoninfo/businessReport/count?"+$.param(params);
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
			$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/count?'+$.param(params));
			$("#searchForm").submit();
		}
		
		function timeOnChange(){
			var params = {};
			params.active =  $(".btn-group .active").attr('act');
			$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/count?'+$.param(params));
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
	<form:form id="searchForm" modelAttribute="businessReport" action="${ctx}/amazoninfo/businessReport/count" method="post" class="breadcrumb form-search">
		<div style="height: 50px;line-height: 50px;">
			<input id="searchFlag" name="searchFlag" type="hidden" value="${searchFlag}" />
			<spring:message code="amaInfo_businessReport_productName"/>:<select name="productName" id="ps" style="width: 280px">
				<option value=""  ${empty productName?'selected':''}>--<spring:message code="sys_label_businessReport_select_product"/>--</option>
				<c:forEach items="${productNames}" var="pName">
						<option value="${pName}" ${fn:trim(pName) eq fn:trim(productName)?'selected':''}>${pName}</option>			
				</c:forEach>
			</select>
			<ul class="nav nav-pills" style="width:300px;float:left;" id="myTab">
				<li data-toggle="pills" id="showTab0"><a href="#" onclick="javaScript:searchType('0')">By Day</a></li>
				<li data-toggle="pills" id="showTab1"><a href="#" onclick="javaScript:searchType('1')">By Week</a></li>
				<li data-toggle="pills" id="showTab2"><a href="#" onclick="javaScript:searchType('2')">By Month</a></li>
			</ul>
			<div style="float: right;">
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
			    <a class="btn btn-primary"  id="countExport" >导出</a>
			</div>
		</div>
		<div class="btn-group" data-toggle="buttons-radio">
			  <span act='1' class="btn btn-info ${active==1?'active':''}">&nbsp;&nbsp;<spring:message code="amaInfo_businessReport_columnStatistics"/>&nbsp;&nbsp;</span>
			  <span act='2' class="btn btn-info ${active==2?'active':''}"><spring:message code="amaInfo_businessReport_sessionStatistics"/></span>
			  <span act='3' class="btn btn-info ${active==3?'active':''}"><spring:message code="amaInfo_businessReport_conversionStatistics"/></span>
		</div>
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
						<td style="text-align: center;vertical-align: middle" rowspan="2">${productName}</td>
						<c:forEach items="${countrySet}" var="countryName">
							<c:if test="${'total' ne countryName }">
							<td style="text-align: center;vertical-align: middle" class="title" colspan="3">${fns:getDictLabel(countryName, 'platform', '总计')}</td>
							</c:if>
						</c:forEach>
							<td style="text-align: center;vertical-align: middle" class="title" colspan="3">总计</td>
					</tr>
					<tr>
						<c:forEach items="${countrySet}" var="countryName">
							<c:if test="${'total' ne countryName }">
							    <td class="session" style="text-align: center;"><b>Session</b></td>
							   <td class="ads" style="text-align: center;"><b>Session(ad)</b></td>
							   <td class="conver" style="text-align: center;"><b>Conversion</b></td>
							</c:if>
						</c:forEach>
						<td class="session" style="text-align: center;"><b>Session</b></td>
						<td class="ads" style="text-align: center;"><b>Session(ad)</b></td>
						<td class="conver" style="text-align: center;"><b>Conversion</b></td>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${sessionDataMap}" var="session" varStatus="i">
						<tr>
							<td style="text-align: center;vertical-align: middle">${session.key}${type }${tips[session.key] }</td>
							<c:forEach items="${countrySet}" var="countryName">
							<c:if test="${'total' ne countryName }">
								<td style="text-align: center;vertical-align: middle" class="session" >${session.value[countryName] }</td>
								<td style="text-align: center;vertical-align: middle"  class="ads">${adsData[countryName][session.key] }</td>
								<td  class="conver" style="background-color:#D2E9FF;text-align: center;vertical-align: middle">${conversionDataMap[session.key][countryName]}${empty conversionDataMap[session.key][countryName]?'':'%'}</td>
							</c:if>
							</c:forEach>
								<td style="text-align: center;vertical-align: middle" class="session" >${session.value['total'] }</td>
								<td style="text-align: center;vertical-align: middle" class="ads">${adsData['total'][session.key] }</td>
								<td  class="conver" style="background-color:#D2E9FF;text-align: center;vertical-align: middle">${conversionDataMap[session.key]['total']}${empty conversionDataMap[session.key]['total']?'':'%'}</td>
						</tr>	
					</c:forEach>
					<tr>
						<td style="text-align: center;vertical-align: middle">Total</td>
						<c:forEach items="${countrySet}" var="countryName">
							<td style="text-align: center;vertical-align: middle" class="session total" ></td>
							<td style="text-align: center;vertical-align: middle" class="ads adsTotal"></td>
							<td  class="conver" style="background-color:#D2E9FF;text-align: center;vertical-align: middle"></td>
						</c:forEach>
					</tr>
				</tbody>
			</table>
		</c:if>
		</div>
	</div>
</body>
</html>
