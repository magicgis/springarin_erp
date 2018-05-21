<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
<title>亚马逊产品线销售报告明细</title>
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
			
			$("#btnSubmit").click(function(){
				$("#searchForm").submit();
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
			
			
			$(".lineHref").click(function(e){
				e.preventDefault();
				var key = $(this).attr('key');
				<c:forEach items="${allLine}" var="dic">
					if("${dic.key}"==key){
						if(!(myChart${dic.key}.series)){
							myChart${dic.key}.setOption(option${dic.key});
							$("#${dic.key}Chart").css("width",$("#${dic.key}Chart").parent().parent().parent().width()-20);
							myChart${dic.key}.resize();
						}
					}
				</c:forEach>
				$(this).tab('show');
				
			});
			 $("a[rel='popover']").popover({trigger:'hover'});
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
			    
			 
			 
			//构造图表
				//ecahrts-----------------
				var myChart;
				<c:forEach items="${allLine}" var="dic" varStatus="i">var Chart${dic.key};var option${dic.key};</c:forEach>
				
				
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
			                	selected: {<c:forEach items="${allLine}" var="dic" varStatus="i">'${dic.value}':false${i.last?'':','}</c:forEach>},
			                    data:['总计',<c:forEach items="${allLine}" var="dic" varStatus="i">'${dic.value}'${i.last?'':','}</c:forEach>]
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
								      data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber pattern="#######.##" value="${empty data['total'][x].sales?0:data['total'][x].sales}"  maxFractionDigits="2"  />${i.last?'':','}</c:forEach>],
								      markLine : {
								      	 data : [
								              {type : 'average', name: '平均线'}
								          ]
								      }
								},
								//<c:forEach items="${allLine}" var="dic" varStatus="i">
									{
									      name:'${dic.value}',
									      type:'line',
									      data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty data[dic.key][x].sales?0:data[dic.key][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
									      markLine : {
									      	 data : [
									              {type : 'average', name: '平均线'}
									          ]
									      }
									}${i.last?'':','}
								//</c:forEach>
									
			                ]
			            };
			            myChart.setOption(option);
			            
			          //<c:forEach items="${allLine}" var="dic" varStatus="i">
			            	myChart${dic.key} = ec.init(document.getElementById("${dic.key}Chart"));
				            myChart${dic.key}.showLoading({
							    text: '正在努力的读取数据中...',    //loading话术
							});
							//ecahrts-----------------
				            option${dic.key} = {
				            	title:{text:'${dic.value} <spring:message code="amazon_product_sales_statistics"/>',x:'center'},		
				                tooltip : {
				                    trigger: 'item'
				                },
				                legend: {
				                	y:30,
				                	selected: {'总计':false},
				                    data:['${dic.value}','总计']
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
									    name:'${dic.value}',
									    type:'line',
									    data:[<c:forEach items="${xAxis}" var="x" varStatus="i"><fmt:formatNumber  pattern="#######.##" value="${empty data[dic.key][x].sales?0:data[dic.key][x].sales}"  maxFractionDigits="2" />${i.last?'':','}</c:forEach>],
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
				            //myChart${dic.key}.setOption(option${dic.key});
			          //</c:forEach>
			        }
			    );
			    
			    
			    
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
			    	var num = (tr.find("td:eq(4)").text()-tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'5':'4'})").text())*1000/tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'5':'4'})").text();
				    tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'6':'5'})").html("<span class=\"badge badge-info\">"+num.toFixed(2)+"‰</span>");
				    num = (tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'8':'7'})").text()-tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'9':'8'})").text())*1000/tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'9':'8'})").text();
				    tr.find("td:eq(${saleReport.searchType=='1'||saleReport.searchType=='2'?'10':'9'})").html("<span class=\"badge badge-info\">"+num.toFixed(2)+"‰</span>");
			    })
			        
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
			    
			    //<c:forEach items="${allLine}" var="dic" varStatus="i">
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
		  //</c:forEach>
			
		});
		function searchTypes(searchFlag){
			if(oldSearchFlag==searchFlag){
				return;
			}
			$("#searchType").val(searchFlag);
			//必须不传
			$("#start").val("");
			$("#end").val("");
			$("#searchForm").submit();
		}
		
		function changeCurrecyType(){
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
  	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/amazoninfo/salesReprots/lineList">Total</a></li>
		<c:forEach var="temp" items="${allLine}" varStatus="i">
		  <li><a href="${ctx}/amazoninfo/salesReprots/list2?groupName=${temp.key}&searchType=${saleReport.searchType}">${temp.value}</a></li>
		</c:forEach>  
	</ul>
  <!--   
	<div style="font-size: 25px; font-weight: bold; text-align: center;">
		<c:choose>
			<c:when test="${'USD' eq saleReport.currencyType}">
				亚马逊按产品线销量销售额统计(美金)
			</c:when>
			<c:otherwise>
				亚马逊按产品线销量销售额统计(欧元)
			</c:otherwise>
		</c:choose>
	</div>	
	<br/>
	  -->

   <form:form id="searchForm" modelAttribute="saleReport" action="${ctx}/amazoninfo/salesReprots/lineList" method="post" class="form-search">
		<div style="height: 30px">
		<ul class="nav nav-pills" style="width:250px;float:left;" id="myTab">
			<li data-toggle="pills" id="showTab0"><a href="#" onclick="javaScript:searchTypes('1');return false;">By Day</a></li>
			<li data-toggle="pills" id="showTab1"><a href="#" onclick="javaScript:searchTypes('2');return false;">By Week</a></li>
			<li data-toggle="pills" id="showTab2"><a href="#" onclick="javaScript:searchTypes('3');return false;">By Month</a></li>
		</ul>
		
		<!-- 货币美元&欧元切换,默认为欧元版 -->
		 <spring:message code="amazon_sales_currency"/>:<select name="currencyType" id="currencyType" style="width:70px" onchange="changeCurrecyType()">
				<option value="EUR" ${'EUR' eq fn:trim(saleReport.currencyType)?'selected':''}>EUR</option>
				<option value="USD" ${'USD' eq fn:trim(saleReport.currencyType)?'selected':''}>USD</option>
		</select>
		
		<input id="searchType" name="searchType" type="hidden" value="${saleReport.searchType}" />
		
		<span style="float: center;">
		<label></label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});" readonly="readonly"  class="Wdate" type="text" name="start" value="<fmt:formatDate value="${saleReport.start}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
		&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${saleReport.end}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code="sys_but_search"/>"/>
		&nbsp;
	            	<div class="btn-group">
						   <button type="button" class="btn btn-primary"><spring:message code="sys_but_export"/></button>
						   <button type="button" class="btn btn-primary dropdown-toggle"  data-toggle="dropdown">
						      <span class="caret"></span>
						      <span class="sr-only"></span>
						   </button>
						   <%-- <ul class="dropdown-menu" id="allExport">
						      <li><a id="byProductCountry">分产品汇总统计</a></li>
						       <li><a id="byProductType">分产品类型统计</a></li> 
						      <li><a id="byProduct">分产品详情统计</a></li>
						      <shiro:hasPermission name="psi:inventory:stockPriceView">
						         <li><a id="byProfit">毛利统计</a></li>
						      </shiro:hasPermission>
						      <li id='add1'><a id="byProductTypeAll">分产品类型所有国家</a></li> 
						      <li id='add2'><a id="byProductGroupTypeAll">分产品线所有国家</a></li> 
						   </ul> --%>
						   
						   <ul class="dropdown-menu" id="allExport">
						      <li><a id="byProductCountry"><spring:message code="amazon_sales_products_overall"/></a></li>
						      <li><a id="byProductType"><spring:message code="amazon_sales_product_summary"/></a></li> 
						      <li><a id="byProduct"><spring:message code="amazon_sales_product_details"/></a></li>
						      <shiro:hasPermission name="psi:inventory:stockPriceView">
						         <li><a id="byProfit"><spring:message code="amazon_sales_profit_statistics"/></a></li>
						      </shiro:hasPermission>
						      <li id='add1'><a id="byProductTypeAll"><spring:message code="amazon_sales_product_summary_all_countries"/></a></li> 
						      <li id='add2'><a id="byProductGroupTypeAll"><spring:message code="amazon_sales_product_line_all_countries"/></a></li> 
						   </ul>
				 </div>
				  &nbsp;&nbsp;
			<font color="red" size="2">(<spring:message code="amazon_sales_product_desc"/>) </font>
		</span>
		</div>
	</form:form>
	
	<table class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;" rowspan="2"><spring:message code="amazon_sales_product_line"/></th>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
					<th colspan="3" style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
						  ${xAxis[fn:length(xAxis)-i.index]}${type}
				    </th>
				</c:forEach>		
			</tr>
			<tr>
			  	<c:forEach begin="1" end="3" step="1" varStatus="i">
					<th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><spring:message code="amazon_sales_volum"/>(${currencySymbol})</th>
					<th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><spring:message code="amazon_sales_quantity"/></th>
					<th style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">Avg. Sales(${currencySymbol})</th>		
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="temp" items="${allLine}" varStatus="i">
				<tr>
					<td style="text-align: center;vertical-align: middle;"><%-- <a href="${ctx}/amazoninfo/salesReprots/list?groupName=${temp.key}" target="_blank"> --%>${temp.value}<!-- </a> --></td>
					<c:forEach begin="1" end="3" step="1" varStatus="i">
						<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><fmt:formatNumber value="${data[temp.key][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2"/></td>
						<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${data[temp.key][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
					    <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
					     <c:if test="${data[temp.key][xAxis[fn:length(xAxis)-i.index]].salesVolume>0}">
					       <fmt:formatNumber value="${data[temp.key][xAxis[fn:length(xAxis)-i.index]].sales/data[temp.key][xAxis[fn:length(xAxis)-i.index]].salesVolume}" maxFractionDigits="2" minFractionDigits="2"/>
					     </c:if>
					    </td>
					</c:forEach>
				</tr>
			</c:forEach>
			
			<tr>
				<td  style="text-align: center;vertical-align: middle;"><b><%-- <a href="${ctx}/amazoninfo/salesReprots/list?groupName=total" target="_blank"></a> --%>Total </b> </td>
				<c:forEach begin="1" end="3" step="1" varStatus="i">
				
					<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}"><fmt:formatNumber value="${data['total'][xAxis[fn:length(xAxis)-i.index]].sales}" maxFractionDigits="2" minFractionDigits="2" /> </td>
					<td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">${data['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume}</td>		
				    <td style="text-align: center;vertical-align: middle;${i.index==1?'color: #08c;':''}">
				     <c:if test="${data['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume>0}">
				        <fmt:formatNumber value="${data['total'][xAxis[fn:length(xAxis)-i.index]].sales/data['total'][xAxis[fn:length(xAxis)-i.index]].salesVolume}" maxFractionDigits="2" minFractionDigits="2" />
				     </c:if>
				    </td>
				</c:forEach>
			</tr>
		</tbody>
	</table>
	
	<ul class="nav nav-tabs">
		<li class="active"><a class="lineHref" href="#total" >Amazon Total</a></li>
		<c:forEach items="${allLine}" var="dic">
				<li><a  class="lineHref" href="#${dic.key}"  key="${dic.key}" >${dic.value}</a></li>
		</c:forEach>	
	</ul>
	
    <div class="tab-content">
       <div id="total" class="tab-pane active">
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
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover"><fmt:formatNumber pattern="#######.##" value="${data['total'][date].sales}" maxFractionDigits="2"  minFractionDigits="2"/> </a>
						</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" ><fmt:formatNumber pattern="#######.##" value="${data['total'][date].sureSales}" maxFractionDigits="2"  minFractionDigits="2" /></a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover"><fmt:formatNumber pattern="#######.##" value="${data['total'][date].realSales}" maxFractionDigits="2"  minFractionDigits="2" /></a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${data['total'][date].sureSales>0}">
								<span class="badge badge-info">
									${fns:roundUp((data['total'][date].sureSales-data['total'][date].realSales)*1000/data['total'][date].sureSales)}‰
								</span>
							</c:if>
						 </td>
						<td style="text-align: center;vertical-align: middle;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" >${data['total'][date].salesVolume}</a>
						</td>
						<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" >${data['total'][date].sureSalesVolume}</a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" >${data['total'][date].realSalesVolume}</a>
						</td>
						<td style="text-align: center;vertical-align: middle;">
							<c:if test="${data['total'][date].sureSalesVolume>0}">
								<span class="badge badge-info">
									${fns:roundUp((data['total'][date].sureSalesVolume-data['total'][date].realSalesVolume)*1000/data['total'][date].sureSalesVolume)}‰
					    		</span>
					    	</c:if>
					    </td>
						<td style="text-align: center;vertical-align: middle;">
							<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=total"><spring:message code="amazon_sales_order"/></a> &nbsp;
                   			<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/order/promotionsList?country=&byTime=${saleReport.searchType}&dateStr=${date}&lineType=total"><spring:message code="amazon_sales_discount_order"/></a> &nbsp;
                   			<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=total">Sku</a>  &nbsp;
                    		<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=total"><spring:message code="amazon_sales_product"/></a>&nbsp;
                    		<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/contrastSaleView?type=${saleReport.searchType}&endTime=${date}&startTime=&country=&currencyType=${saleReport.currencyType}&lineType=total"><spring:message code="amazon_sales_comparison"/></a>
						</td>
					</tr>
				</c:forEach>
			</tbody>
			<tfoot>
				<tr class="count">
					<td></td>
					<td style="text-align: center;vertical-align: middle;">Total</td>
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
					<td></td>
				</tr>
			</tfoot>
		</table>
		<div style="width:98%;border:1px solid #ccc;">
			<div id="totalChart" style="height:400px"></div>
		</div>
	</div>
    
    
    <c:forEach items="${allLine}" var="dic">
			<div id="${dic.key}" class="hideCls tab-pane">
				<table id="${dic.key}Tb" class="table table-striped table-bordered table-condensed">
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
							<td style="text-align: center;vertical-align: middle;">${tip[date]}</td>
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${data[dic.key][date].sales >0}">
									<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="占总量(%) ${fns:roundUp(data[dic.key][date].sales*100/data['total'][date].sales)}%"><fmt:formatNumber pattern="#######.##" value="${data[dic.key][date].sales}" maxFractionDigits="2" minFractionDigits="2" /></a>
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
								<c:if test="${data[dic.key][date].sureSales >0}">
									<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="占总量(%) ${fns:roundUp(data[dic.key][date].sureSales*100/data['total'][date].sureSales)}%"><fmt:formatNumber pattern="#######.##" value="${data[dic.key][date].sureSales}" maxFractionDigits="2" minFractionDigits="2" /></a>
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${data[dic.key][date].realSales >0}">
									<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="占总量(%) ${fns:roundUp(data[dic.key][date].realSales*100/data['total'][date].realSales)}%"><fmt:formatNumber pattern="#######.##" value="${data[dic.key][date].realSales}" maxFractionDigits="2" minFractionDigits="2" /></a>
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${data[dic.key][date].sureSales >0}">
									<span class="badge badge-info">
										${fns:roundUp((data[dic.key][date].sureSales-data[dic.key][date].realSales)*1000/data[dic.key][date].sureSales)}‰
									</span>
								</c:if>
							</td>
							
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${data[dic.key][date].salesVolume>0}">
									<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="占总量(%) ${fns:roundUp(data[dic.key][date].salesVolume*100/data['total'][date].salesVolume)}%">${data[dic.key][date].salesVolume}</a>
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;background-color:#D2E9FF;">
								<c:if test="${data[dic.key][date].sureSalesVolume >0}">
									<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="占总量(%) ${fns:roundUp(data[dic.key][date].sureSalesVolume*100/data['total'][date].sureSalesVolume)}%">${data[dic.key][date].sureSalesVolume}</a>
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;">
								<c:if test="${data[dic.key][date].realSalesVolume >0}">
									<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="占总量(%) ${fns:roundUp(data[dic.key][date].realSalesVolume*100/data['total'][date].realSalesVolume)}%">${data[dic.key][date].realSalesVolume}</a>
								</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle;">
								<span class="badge badge-info">
								<c:if test="${data[dic.key][date].sureSalesVolume >0}">
									<span class="badge badge-info">
										${fns:roundUp((data[dic.key][date].sureSalesVolume-data[dic.key][date].realSalesVolume)*1000/data[dic.key][date].sureSalesVolume)}‰
									</span>
								</c:if>
								</span>
							</td>
							<td style="text-align: center;vertical-align: middle;">
								<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/orderList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=${dic.key}"><spring:message code="amazon_sales_order"/></a> &nbsp;
								<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/order/promotionsList?country=&byTime=${saleReport.searchType}&dateStr=${date}"><spring:message code="amazon_sales_discount_order"/></a> &nbsp;
                   				<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/skuList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=${dic.key}">Sku</a>  &nbsp;
                    			<a class="btn btn-warning btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/productList?country=total&type=${saleReport.searchType}&time=${date}&currencyType=${saleReport.currencyType}&lineType=${dic.key}"><spring:message code="amazon_sales_product"/></a>&nbsp;
                    			<a class="btn btn-success btn-small" target="_blank" href="${ctx}/amazoninfo/salesReprots/contrastSaleView?type=${saleReport.searchType}&endTime=${date}&startTime=&country=&currencyType=${saleReport.currencyType}&lineType=${dic.key}"><spring:message code="amazon_sales_comparison"/></a>
							</td>
						</tr>
						</c:forEach>
					</tbody>
					<tfoot>
						<tr class="count">
							<td></td>
							<td style="text-align: center;vertical-align: middle;">Total</td>
							<td></td>
							<td tid="${dic.key}Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>
							<td tid="${dic.key}Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>		
							<td tid="${dic.key}Tb" class="totalf" style="text-align: center;vertical-align: middle;">0</td>		
							<td style="text-align: center;vertical-align: middle;"></td>
							<td tid="${dic.key}Tb" class="total" style="text-align: center;vertical-align: middle;">0</td>
							<td tid="${dic.key}Tb" class="total" style="text-align: center;vertical-align: middle;">0</td>		
							<td tid="${dic.key}Tb" class="total" style="text-align: center;vertical-align: middle;">0</td>		
							<td style="text-align: center;vertical-align: middle;"></td>
							<td></td>
						</tr>
					</tfoot>
				</table>
				<div style="border:1px solid #ccc;width: 98%">
					<div id="${dic.key}Chart" style="height:400px;"></div>
				</div>
			</div>
	</c:forEach>
    
    </div>
	
	
	<div class="modal hide fade" id="tip">
		<div class="modal-header">
			<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
			<h3 style="color: red">提示</h3>
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
