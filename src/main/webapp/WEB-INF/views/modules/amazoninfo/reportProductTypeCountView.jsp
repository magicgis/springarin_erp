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
		 var flag=0;
		$(function() {
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
			
	       $("#countByProductTypeExport").click(function(){
				if($("select[name='types']").val()==""||$("select[name='types']").val()==null){
					$.jBox.tip('请选择产品类型');
					return;
				}
				var params = {};
				params.date1 = $("#date1").val();
				params.date2 = $("#date2").val();
				params.searchFlag = $("#searchFlag").val();
				params.types =encodeURI($("select[name='types']").val());
				params.active =  $(".btn-group .active").attr('act');
				params.country = $("#country").val();
				$("#countByProductTypeExport").attr("href","${ctx}/amazoninfo/businessReport/countByProductTypeExport?"+$.param(params));
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
			
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("select[name='types']").removeClass('required');
			/* 	$("select[name='types']").select2().val(null).trigger("change");
				$("input[name='endDate']").val(''); */
				$("#searchForm").submit();
			});
			
			$(".btn-group span").click(function(){
				if($(".btn-group .active").text() == $(this).text()){
					return ;
				}
				var index = $(".btn-group span").index(this);
				initActive(index);
			});
			
			$("#searchForm").validate({submitHandler: function(form){
					if($("select[name='types']").val()){
						loading('<spring:message code="sys_label_tips_load_charts"/>');
					}
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
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
		           
		            var option = {
		            	title:{text:'Sessions <spring:message code="sys_label_businessReport_statistics"/>',x:'center'},	
		                tooltip : {
		                    trigger: 'item'
		                },
		                legend: {
		                	padding:[0,0,0,0],
		                	y:30,
		                	${show}
		                    data:['total',<c:forEach var="name" items="${types}" varStatus="i">'${name}'${i.count!=fn:length(type)?',':''}</c:forEach>]
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
							//<c:forEach items="${sessionsData}" var='sess' varStatus="i">
							//	<c:set var="f1" value="true" />
							
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
							//</c:forEach>
		                ]
		            };
		           
		            
		            
		            var newOption = {
			            	title:{text:'Sessions <spring:message code="sys_label_businessReport_statistics"/>',x:'center'},	
				            /* tooltip : {
				                 trigger: 'axis',
				                 axisPointer : {            // 坐标轴指示器，坐标轴触发有效
				                     type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
				                 }
				             }, */
				             tooltip : {
				                    trigger: 'axis',
				                    formatter:function(params){
				                    	 var show=params[0][1]+"<br/>";
						                 for(var i=0;i<params.length-1;i=i+2){
						                    show+=params[i][0]+":"+params[i][2]+"<br/>"+params[i+1][0]+":"+(params[i+1][2]+params[i][2])+"<br/>";
						                 }  
				                    	 return show;
				                    }
				                }, 
			                legend: {
			                	y:30,
			                   
			                    selected: {<c:forEach var="name" items="${types}" varStatus="i">'${name}':true,'${name}广告':true ${i.count!=fn:length(types)?',':''}</c:forEach>}, 
			                    data:[<c:forEach var="name" items="${types}" varStatus="i">'${name}','${name}广告'${i.count!=fn:length(types)?',':''}</c:forEach>]
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
									
									//<c:forEach var="name" items="${types}" varStatus="i">
									{
									      name:'${name}广告',
									      type:'bar',
									      stack:'${name}统计',
									      data:[<c:forEach items="${sessionsMap[name]}" var='sess'  varStatus="i">${empty adsData[name][sess.key]?0:adsData[name][sess.key]}${i.last?'':','}</c:forEach>],
									},
									{
									      name:'${name}',
									      type:'bar',
									      stack:'${name}统计',
									      data:[<c:forEach items="${sessionsMap[name]}" var='sess' varStatus="i"><c:if test="${not empty adsData[name][sess.key]}">${sessionsMap[name][sess.key]-adsData[name][sess.key]}</c:if><c:if test="${empty adsData[name][sess.key]}">${sessionsMap[name][sess.key]}</c:if>${i.last?'':','}</c:forEach>],
									}${i.last?'':','}  
								//</c:forEach>
							
			                ]
			            };
		            
		               myChart.setOption(newOption);
		        
		            var ecConfig = require('echarts/config');
		            function eConsole(param) {
		            	console.log(param);
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
			                	padding:[0,0,0,0],
			                	y:30,
			                	${show}
			                	data:['total',<c:forEach var="name" items="${types}" varStatus="i">'${name}'${i.count!=fn:length(type)?',':''}</c:forEach>]
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
			                        boundaryGap:[0,0.5]
			                    }
			                ],
			                series : [
								//<c:forEach items="${conversionData}" var="conv" varStatus="i">
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
								//</c:forEach>
			                ]
			            };
		            
		            myChart1.setOption(option1);
		            initActive($(".btn-group .active").attr('act')-1);
		        }
		    );
		    <c:if test="${f1 != true && not empty type}" >
		    	$("#nodata").show();
		    </c:if> 
		    <c:if test="${not empty groupName}" >
	            queryType('${groupName}');	
	         </c:if>
	         <c:if test="${empty groupName}" >
	            queryType('0');	
	         </c:if>
	        
	         
		      $("#typeGroup").change(function(){    
	    	       queryType($("#typeGroup").val());
	    	  });  
			
		});
		
		 function queryType(id){
			 if(id==""||id==null||id=="0"){
				 $("#types").empty();
		         var option = "";   
				 option += "<c:forEach items='${typesAll}' var='type'><option  value='${type}'>${type}</option></c:forEach>"; 
				 option+=" <c:forEach items='${types}' var='type'><option value='${type}'>${type}</option></c:forEach>	";
		          $("#types").html(option);
		           var data="${types}";
		          var dataArr=new Array();
		          if(flag==0){
		        	  if(data!=null&&data!=""){
			        	  var arr=data.substring(1,data.length-1).split(",");
			        	  for(var i=0;i<arr.length;i++){
			        		dataArr[dataArr.length]=$.trim(arr[i]);
			        	  }
			        	  $("#types").select2("val",dataArr);
			          } 
		          }else{
		        	  $("#types").select2("val",[]);
		          }
		         
			 }else{
			   $.ajax({  
			        type : 'POST', 
			        url : '${ctx}/psi/psiProductTypeGroupDict/getProductType',  
			        dataType:"json",
			        data : 'groupId=' +id,  
			        async: false,
			        success : function(msg){ 
			        	$("#types").empty(); 
			        	var option = "";  
			        	var arr=new Array();
			            for(var i=0;i<msg.length;i++){
 							option += "<option  value=\"" + msg[i].value + "\" >" + msg[i].value + "</option>"; 
 							arr[i]=msg[i].value;
 	                    }
			            $("#types").append(option);
			            var data="${types}";
			            if(data!=""&&data!=null&&flag==0){
					         var dataArr=new Array();
				        	 var arr=data.substring(1,data.length-1).split(",");
				        	 for(var i=0;i<arr.length;i++){
				        		dataArr[dataArr.length]=$.trim(arr[i]);
				        	 }
				        	 $("#types").select2("val",dataArr);
			            }else{
			               $("#types").select2("val", arr); 
			            }
			            flag++;
			        }
			  }); 
			 }  
		 }
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

			function searchType(searchFlag){
				if(oldSearchFlag==searchFlag){
					return;
				}
				$("#date1").val("");
				$("#date2").val("");
				$("#searchFlag").val(searchFlag);
				var params = {};
				params.active =  $(".btn-group .active").attr('act');
				$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/countByProductType?'+$.param(params));
				$("#searchForm").submit();
			}
			
			function timeOnChange(){
				var params = {};
				params.active =  $(".btn-group .active").attr('act');
				$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/countByProductType?'+$.param(params));
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
	
	<form:form id="searchForm" modelAttribute="businessReport" action="${ctx}/amazoninfo/businessReport/countByProductType" method="post" class="breadcrumb form-search">
		<div style="height: 80px;line-height: 40px;">
			<input  name="country" type="hidden" value="${country}" id="country"/>
			<input id="searchFlag" name="searchFlag" type="hidden" value="${searchFlag}" />
			  产品分组:<select name="groupName" id="typeGroup" style="width: 15%">
				<option value="">--All--</option>
				<option value="unGrouped" ${'unGrouped' eq fn:trim(groupName)?'selected':''}>--UnGrouped--</option>
				<c:forEach items="${groupType}" var="groupType">
					<option value="${groupType.id}" ${fn:trim(groupType.id) eq fn:trim(groupName)?'selected':''}>${groupType.name}</option>			
				</c:forEach>
			</select>
			产品类型:
			<select name="types" multiple class="multiSelect required" id="types" style="width:60%">
			</select>
			&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="生成图表"/>
			 <a class="btn btn-primary"  id="countByProductTypeExport">导出</a>
			<ul class="nav nav-pills" style="width:300px;float:left;" id="myTab">
				<li data-toggle="pills" id="showTab0"><a href="#" onclick="javaScript:searchType('0')">By Day</a></li>
				<li data-toggle="pills" id="showTab1"><a href="#" onclick="javaScript:searchType('1')">By Week</a></li>
				<li data-toggle="pills" id="showTab2"><a href="#" onclick="javaScript:searchType('2')">By Month</a></li>
			</ul>
			<div class="btn-group" data-toggle="buttons-radio">
				  <span act='1' class="btn btn-info ${active==1?'active':''}">&nbsp;&nbsp;<spring:message code="amaInfo_businessReport_columnStatistics"/>&nbsp;&nbsp;</span>
				  <span act='2' class="btn btn-info ${active==2?'active':''}"><spring:message code="amaInfo_businessReport_sessionStatistics"/></span>
				  <span act='3' class="btn btn-info ${active==3?'active':''}"><spring:message code="amaInfo_businessReport_conversionStatistics"/></span>
			</div>
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
						<td style="text-align: center;vertical-align: middle" rowspan="2">日期</td>
						<c:forEach items="${types}" var="name">
							<td  style="text-align: center;vertical-align: middle" class="title" colspan="3">${name}</td>
						</c:forEach>
					</tr>
					<tr>
						<c:forEach items="${types}" var="name">
							<td class="session" style="text-align: center;"><b>Session</b></td>
							<td class="ads" style="text-align: center;"><b>Session(ad)</b></td>
							<td class="conver" style="text-align: center;"><b>Conversion</b></td>
						</c:forEach>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${sessionDataMap}" var="session" varStatus="i">
						<tr>
							<td style="text-align: center;vertical-align: middle">${session.key}${tips[session.key]}</td>
							<c:forEach items="${types}" var="name">
								<td class="session" style="text-align: center;vertical-align: middle" >${session.value[name] }</td>
								<td class='ads' style="text-align: center;vertical-align: middle" >${adviseDataMap[name][session.key] }</td>
								<td  class="conver" style="background-color:#D2E9FF;text-align: center;vertical-align: middle">${conversionDataMap[session.key][name]}${empty conversionDataMap[session.key][name]?'':'%'}</td>
							</c:forEach>
						</tr>	
					</c:forEach>
					<tr>
						<td style="text-align: center;vertical-align: middle">Total</td>
						<c:forEach items="${types}" var="name">
							<td style="text-align: center;vertical-align: middle" class="session total" ></td>
							<td style="text-align: center;vertical-align: middle" class="ads adsTotal" ></td>
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
