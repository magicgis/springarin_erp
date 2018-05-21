<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>客户统计</title>
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
			$("a[rel='popover']").popover({trigger:'hover'});
			
			
			$(".countryHref").click(function(e){
				e.preventDefault();
				var key = $(this).attr('key');
				if('de'==key){
					if(!(myChartde.series)){
						
						var total = $("#"+key).attr("total");	
						$("#"+key).find(".rows").each(function(){
							var num = $(this).find("td:eq(1)").text();	
							$(this).find("td:eq(3)").text((num*100/total).toFixed(0)+"%");
						});
						
						
						myChartde.setOption(optionde);
						$("#deChart").css("width",$("#deChart").parent().parent().parent().width()-20);
						myChartde.resize();
						
						
						$("#contentTable"+key).dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
							"sPaginationType": "bootstrap",
						 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"bDestroy":true
						});
						
						$("#blackTable"+key).dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
							"sPaginationType": "bootstrap",
						 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"aaSorting": [[7, "desc" ]],"bDestroy":true
						});
					}
				}else if('com'==key){
					if(!(myChartcom.series)){
						
						var total = $("#"+key).attr("total");	
						$("#"+key).find(".rows").each(function(){
							var num = $(this).find("td:eq(1)").text();	
							$(this).find("td:eq(3)").text((num*100/total).toFixed(0)+"%");
						});
						
						myChartcom.setOption(optioncom);
						$("#comChart").css("width",$("#comChart").parent().parent().parent().width()-20);
						myChartcom.resize();
						
						$("#contentTable"+key).dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
							"sPaginationType": "bootstrap",
						 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"bDestroy":true
						});
						
						$("#blackTable"+key).dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
							"sPaginationType": "bootstrap",
						 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"aaSorting": [[7, "desc" ]],"bDestroy":true
						});
					}
				}else if('uk'==key){
					if(!(myChartuk.series)){
						
						var total = $("#"+key).attr("total");	
						$("#"+key).find(".rows").each(function(){
							var num = $(this).find("td:eq(1)").text();	
							$(this).find("td:eq(3)").text((num*100/total).toFixed(0)+"%");
						});
						
						myChartuk.setOption(optionuk);
						$("#ukChart").css("width",$("#ukChart").parent().parent().parent().width()-20);
						myChartuk.resize();
						
						$("#contentTable"+key).dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
							"sPaginationType": "bootstrap",
						 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"bDestroy":true
						});
						
						$("#blackTable"+key).dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
							"sPaginationType": "bootstrap",
						 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"aaSorting": [[7, "desc" ]],"bDestroy":true
						});
					}
				}else if('ca'==key){
					if(!(myChartca.series)){
						
						var total = $("#"+key).attr("total");	
						$("#"+key).find(".rows").each(function(){
							var num = $(this).find("td:eq(1)").text();	
							$(this).find("td:eq(3)").text((num*100/total).toFixed(0)+"%");
						});
						
						myChartca.setOption(optionca);
						$("#caChart").css("width",$("#caChart").parent().parent().parent().width()-20);
						myChartca.resize();
						
						$("#contentTable"+key).dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
							"sPaginationType": "bootstrap",
						 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"bDestroy":true
						});
						
						$("#blackTable"+key).dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
							"sPaginationType": "bootstrap",
						 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"aaSorting": [[7, "desc" ]],"bDestroy":true
						});
					}
				}else if('mx'==key){
					if(!(myChartmx.series)){
						
						var total = $("#"+key).attr("total");	
						$("#"+key).find(".rows").each(function(){
							var num = $(this).find("td:eq(1)").text();	
							$(this).find("td:eq(3)").text((num*100/total).toFixed(0)+"%");
						});
						
						
						myChartmx.setOption(optionmx);
						$("#mxChart").css("width",$("#mxChart").parent().parent().parent().width()-20);
						myChartmx.resize();
						
						$("#contentTable"+key).dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
							"sPaginationType": "bootstrap",
						 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"bDestroy":true
						});
						
						$("#blackTable"+key).dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
							"sPaginationType": "bootstrap",
						 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"aaSorting": [[7, "desc" ]],"bDestroy":true
						});
					}
				}else if('jp'==key){
					if(!(myChartjp.series)){
						
						var total = $("#"+key).attr("total");	
						$("#"+key).find(".rows").each(function(){
							var num = $(this).find("td:eq(1)").text();	
							$(this).find("td:eq(3)").text((num*100/total).toFixed(0)+"%");
						});
						
						myChartjp.setOption(optionjp);
						$("#jpChart").css("width",$("#jpChart").parent().parent().parent().width()-20);
						myChartjp.resize();
						
						$("#contentTable"+key).dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
							"sPaginationType": "bootstrap",
						 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"bDestroy":true
						});
						
						$("#blackTable"+key).dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
							"sPaginationType": "bootstrap",
						 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"aaSorting": [[7, "desc" ]],"bDestroy":true
						});
					}
				}else if('es'==key){
					if(!(myChartes.series)){
						
						var total = $("#"+key).attr("total");	
						$("#"+key).find(".rows").each(function(){
							var num = $(this).find("td:eq(1)").text();	
							$(this).find("td:eq(3)").text((num*100/total).toFixed(0)+"%");
						});
						
						myChartes.setOption(optiones);
						$("#esChart").css("width",$("#esChart").parent().parent().parent().width()-20);
						myChartes.resize();
						
						$("#contentTable"+key).dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
							"sPaginationType": "bootstrap",
						 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"bDestroy":true
						});
						
						$("#blackTable"+key).dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
							"sPaginationType": "bootstrap",
						 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"aaSorting": [[7, "desc" ]],"bDestroy":true
						});
					}
				}else if('it'==key){
					if(!(myChartit.series)){
						
						var total = $("#"+key).attr("total");	
						$("#"+key).find(".rows").each(function(){
							var num = $(this).find("td:eq(1)").text();	
							$(this).find("td:eq(3)").text((num*100/total).toFixed(0)+"%");
						});
						
						myChartit.setOption(optionit);
						$("#itChart").css("width",$("#itChart").parent().parent().parent().width()-20);
						myChartit.resize();
						
						$("#contentTable"+key).dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
							"sPaginationType": "bootstrap",
						 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"bDestroy":true
						});
						
						$("#blackTable"+key).dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
							"sPaginationType": "bootstrap",
						 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"aaSorting": [[7, "desc" ]],"bDestroy":true
						});
					}
				}else if('fr'==key){
					if(!(myChartfr.series)){
						
						var total = $("#"+key).attr("total");	
						$("#"+key).find(".rows").each(function(){
							var num = $(this).find("td:eq(1)").text();	
							$(this).find("td:eq(3)").text((num*100/total).toFixed(0)+"%");
						});
						
						myChartfr.setOption(optionfr);
						$("#frChart").css("width",$("#frChart").parent().parent().parent().width()-20);
						myChartfr.resize();
						
						$("#contentTable"+key).dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
							"sPaginationType": "bootstrap",
						 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"bDestroy":true
						});
						
						$("#blackTable"+key).dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
							"sPaginationType": "bootstrap",
						 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"aaSorting": [[7, "desc" ]],"bDestroy":true
						});
					}
				}
				$("#contentTable"+key).css("width","98%");
				$("#blackTable"+key).css("width","98%");
				$(this).tab('show');
				
			});
			
			//图表
			var myChart;
			//<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">
				var myChart${dic.value};var option${dic.value};
			//</c:if></c:forEach>
			
			require.config({
		        paths:{ 
		            echarts:'${ctxStatic}/echarts/js/echarts',
		            'echarts/chart/pie': '${ctxStatic}/echarts/js/echarts'
		        }
		    });
		    // Step:4 require echarts and use it in the callback.
		    // Step:4 动态加载echarts然后在回调函数中开始使用，注意保持按需加载结构定义图表路径
		   /*  <c:set var="oldC" value="0"/>	
			<c:forEach items="${dataTotal}" var="data" varStatus="i">
				<c:if test="${i.first}">
					<c:set var="newC" value="${data[1]}"/>			
				</c:if>
				<c:if test="${!i.first}">
					<c:set var="oldC" value="${oldC+data[1]}"/>			
				</c:if>
			</c:forEach> 
			    <c:set var="totalCust" value="${newC+oldC}"/>
			*/
		    require(
		        ['echarts','echarts/chart/pie'],
		        function(ec) {
		            myChart = ec.init(document.getElementById("totalChart"));
		            myChart.showLoading({
					    text: '正在努力的读取数据中...',    //loading话术
					});
					//ecahrts-----------------
		            var option = {
		            	title:{text:'近三月客户回头率统计',x:'center',subtext:'合计活跃客户${totalCust}位',},		
		                tooltip : {
		                    trigger: 'item',
		                    formatter: "{a} <br/>{b} : {c} ({d}%)"
		                },
		                legend: {
		                    x : 'center',
		                    y:'bottom',
		                    data:['新客户','老客户']
		                },
		                toolbox: {
		                    show : false
		                },
		                calculable : false,
		                animation:false,
		                series : [
	                          {
	                              name:'客户属性',
	                              center: ['50%', '50%'],
	                              radius : '55%',
	                              type:'pie',
	                              data:[
	                                  {value:${newC}, name:'新客户'},
	                                  {value:${oldC}, name:'老客户'}
	                              ]
	                          }
		               ]
		            };
		            myChart.setOption(option);
		            
		            //<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i"><c:if test="${dic.value ne 'com.unitek'}">
					 //准备数据
		             /*  <c:set var="oldCtemp" value="0"/>	
					 	 <c:set var="newCtemp" value="0"/>		
						<c:forEach items="${dataByCountry[dic.value]}" var="data" varStatus="i">
							<c:if test="${i.first}">
								<c:set var="newCtemp" value="${data[1]}"/>			
							</c:if>
							<c:if test="${!i.first}">
								<c:set var="oldCtemp" value="${oldCtemp+data[1]}"/>			
							</c:if>
						</c:forEach> 
						    <c:set var="totalCusttemp" value="${newCtemp+oldCtemp}"/>
					 */
					 $("#${dic.value}Chart").parent().parent().attr("total","${totalCusttemp}");
					 
		              myChart${dic.value} = ec.init(document.getElementById("${dic.value}Chart"));
			          myChart${dic.value}.showLoading({
						    text: '正在努力的读取数据中...',    //loading话术
					  });
					//ecahrts-----------------
		            	option${dic.value} = {
		            	title:{text:'近三月客户回头率统计',x:'center',subtext:'${dic.label}活跃客户${totalCusttemp}位',},		
		                tooltip : {
		                    trigger: 'item',
		                    formatter: "{a} <br/>{b} : {c} ({d}%)"
		                },
		                legend: {
		                    x : 'center',
		                    y:'bottom',
		                    data:['新客户','老客户']
		                },
		                toolbox: {
		                    show : false
		                },
		                calculable : false,
		                animation:false,
		                series : [
	                        {
	                            name:'客户属性',
	                            center: ['50%', '50%'],
	                            radius : '55%',
	                            type:'pie',
	                            data:[
	                                {value:${newCtemp}, name:'新客户'},
	                                {value:${oldCtemp}, name:'老客户'}
	                            ]
	                        }
		               ]
		            };	
				 //</c:if></c:forEach>
		        }
		    );
		  
			$("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap",
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true
			});
			
			$("#blackTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap",
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,"aaSorting": [[7, "desc" ]]
			});
			
			
			$(".pagination a").addClass("nava");
			 
		});
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs" style="margin-top: 5px">
		<li class="active"><a  class="countryHref" href="#total" >全球</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li><a  class="countryHref" href="#${dic.value}" key="${dic.value}"  >${dic.label}</a></li>
			</c:if>
		</c:forEach>
		<li><a  href="${ctx}/amazoninfo/customers/query" >高级搜索</a></li>	
	</ul>
	<div class="tab-content">
	<div id="total" class="tab-pane active" key="total">
		<div style="border:1px solid #ccc;width: 98%">
			<div id="totalChart" style="height:300px;"></div>
			<table id="contentTable" class="table table-striped table-bordered table-condensed" >
				<thead>
					<tr>
						<th style="text-align: center;vertical-align: middle;">客户购买次数</th>
						<th style="text-align: center;vertical-align: middle;">客户数</th>
						<th style="text-align: center;vertical-align: middle;">平均购买周期(天)</th>
						<th style="text-align: center;vertical-align: middle;">占比(%)</th>
						<th>典型客户ID</th>
					</tr>
				</thead>			
				<tbody>
					<c:forEach items="${dataTotal}" var="data" varStatus="i">
						<tr>
							<td style="text-align: center;vertical-align: middle;">${data[0]}</td>
							<td style="text-align: center;vertical-align: middle;">${data[1]}</td>
							<td style="text-align: center;vertical-align: middle;">${data[2]}</td>
							<td style="text-align: center;vertical-align: middle;">
								${fns:roundUp((data[1]*100/totalCust))}%
							</td>
							<td><a target="_blank" href="${ctx}/amazoninfo/customers/view?customId=${data[3]}">${data[3]}</a></td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<br/>
			<h4 style="text-align: center;">老客户退货率高于50%列表</h4>
			<table id="blackTable" class="table table-striped table-bordered table-condensed" >
				<thead>
					<tr>
						<th style="width: 60px;text-align: center;vertical-align: middle;">平台</th>
						<th style="width: 60px;text-align: center;vertical-align: middle;">客户ID</th>
						<th style="width: 60px;text-align: center;vertical-align: middle;">购买次数</th>
						<th style="width: 120px;text-align: center;vertical-align: middle;">邮箱</th>
						<th style="width: 120px;text-align: center;vertical-align: middle;">最后购买</th>
						<th style="width: 60px;text-align: center;vertical-align: middle;">购买数</th>
						<th style="width: 60px;text-align: center;vertical-align: middle;">退货数</th>
						<th style="width: 60px;text-align: center;vertical-align: middle;">退货率(%)</th>
						<th >累计购买产品</th>
					</tr>
				</thead>			
				<tbody>
					<c:forEach items="${returnBlack}" var="dataEntry" >
						<c:forEach items="${dataEntry.value}" var="data">
							<tr>
								<td style="text-align: center;vertical-align: middle;">${fns:getDictLabel(data[1],'platform','')}</td>
								<td style="text-align: center;vertical-align: middle;"><a target="_blank" href="${ctx}/amazoninfo/customers/view?customId=${data[0]}">${data[0]}</a></td>
								<td style="text-align: center;vertical-align: middle;">${data[2]}</td>
								<td style="text-align: center;vertical-align: middle;">${data[4]}</td>
								<td style="text-align: center;vertical-align: middle;">${data[3]}</td>
								<td style="text-align: center;vertical-align: middle;">${data[6]}</td>
								<td style="text-align: center;vertical-align: middle;">${data[5]}</td>
								<td style="text-align: center;vertical-align: middle;">${data[7]>100?100:data[7]}</td>
								<td>${data[8]}</td>
							</tr>
						</c:forEach>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
	<c:forEach items="${fns:getDictList('platform')}" var="dic" varStatus="i">
		<c:if test="${dic.value ne 'com.unitek'}">
			<div id="${dic.value}" class="tab-pane country" key="${dic.value}">
				<div style="border:1px solid #ccc;width: 98%">
					<div id="${dic.value}Chart" style="height:300px;"></div>
					<table id="contentTable${dic.value}" class="table table-striped table-bordered table-condensed" >
						<thead>
							<tr>
								<th style="text-align: center;vertical-align: middle;">客户购买次数</th>
								<th style="text-align: center;vertical-align: middle;">客户数</th>
								<th style="text-align: center;vertical-align: middle;">平均购买周期(天)</th>
								<th style="text-align: center;vertical-align: middle;">占比(%)</th>
								<th>典型客户ID</th>
							</tr>
						</thead>			
						<tbody>
							<c:forEach items="${dataByCountry[dic.value]}" var="data" varStatus="i">
								<tr class="rows">
									<td style="text-align: center;vertical-align: middle;">${data[0]}</td>
									<td style="text-align: center;vertical-align: middle;">${data[1]}</td>
									<td style="text-align: center;vertical-align: middle;">${data[2]}</td>
									<td style="text-align: center;vertical-align: middle;"></td>
									<td><a target="_blank" href="${ctx}/amazoninfo/customers/view?customId=${data[3]}">${data[3]}</a></td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
					<br/>
					<h4 style="text-align: center;">老客户退货率高于50%列表</h4>
					<table id="blackTable${dic.value}" class="table table-striped table-bordered table-condensed">
						<thead>
							<tr>
								<th style="text-align: center;vertical-align: middle;">平台</th>
								<th style="text-align: center;vertical-align: middle;">客户ID</th>
								<th style="text-align: center;vertical-align: middle;">购买次数</th>
								<th style="text-align: center;vertical-align: middle;">邮箱</th>
								<th style="text-align: center;vertical-align: middle;">最后购买</th>
								<th style="text-align: center;vertical-align: middle;">购买数</th>
								<th style="text-align: center;vertical-align: middle;">退货数</th>
								<th style="text-align: center;vertical-align: middle;">退货率(%)</th>
								<th >累计购买产品</th>
							</tr>
						</thead>			
						<tbody>
							<c:forEach items="${returnBlack[dic.value]}" var="data" >
								<tr>
									<td style="text-align: center;vertical-align: middle;">${fns:getDictLabel(data[1],'platform','')}</td>
									<td style="text-align: center;vertical-align: middle;"><a target="_blank" href="${ctx}/amazoninfo/customers/view?customId=${data[0]}">${data[0]}</a></td>
									<td style="text-align: center;vertical-align: middle;">${data[2]}</td>
									<td style="text-align: center;vertical-align: middle;">${data[4]}</td>
									<td style="text-align: center;vertical-align: middle;">${data[3]}</td>
									<td style="text-align: center;vertical-align: middle;">${data[6]}</td>
									<td style="text-align: center;vertical-align: middle;">${data[5]}</td>
									<td style="text-align: center;vertical-align: middle;">${data[7]>100?100:data[7]}</td>
									<td>${data[8]}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>
		</c:if>
	</c:forEach>
	</div>
</body>
</html>
