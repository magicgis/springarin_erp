<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>

<html>
<head>
<title>大订单</title>
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
					var flag=0;
					//<c:if test="${fn:length(data)==0}">
						$("#tip").modal();
					//</c:if>
					
			
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
					
					$("#btnExport").click(function(){
						$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/exportMaxOrder");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/salesReprots/getMaxOrder");
					});
			
					$(".countryHref").click(function(){
						$("input[name='country']").val($(this).attr("key"));
						$("#searchForm").submit();
					});
					
					<c:if test='${not empty productsName}'>
					   var myChart;
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
					    myChart = ec.init(document.getElementById("totalChart"));
			            myChart.showLoading({
						    text: '正在努力的读取数据中...',    //loading话术
						});
			            var option = {
				            	title:{text:'大订单统计',x:'center'},		
				                tooltip : {
				                    trigger: 'item'
				                },
				                legend: {
				                	y:30,
				                	selected: {<c:forEach var="nameMap" items="${data}" varStatus="i">'${nameMap.key}'${i.last?':true':':false,'}</c:forEach>},
				                    data:[<c:forEach var="nameMap" items="${data}" varStatus="i">'${nameMap.key}'${i.last?'':','}</c:forEach>]
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
				                            formatter: '{value}'
				                        },
				                        boundaryGap:[0,0.5]
				                    }
				                ],
				                series : [
									<c:forEach var="nameMap" items="${data}" varStatus="j">
										{
										      name:'${nameMap.key}',
										      type:'line',
										      data:[<c:forEach items="${xAxis}" var="x" varStatus="i">${empty data[nameMap.key][saleReport.country][x]?0:data[nameMap.key][saleReport.country][x]}${i.last?'':','}</c:forEach>],
										      markLine : {
										      	 data : [
										              {type : 'average', name: '平均线'}
										          ]
										      }
										}${j.last?'':','}
									</c:forEach>
										
				                ]
				            };
			            
			                myChart.setOption(option);
			});
			</c:if>	   
		    $("a[rel='popover']").popover({trigger:'hover'});
		    
		   
		   var oTable = $("#totalTb").dataTable({"searching":false,"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType": "bootstrap","sScrollX": "100%",
			 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
			 	"aaSorting": [[0, "desc" ]]
			});
		   
		    new FixedColumns( oTable,{
		 		"iLeftColumns":1,
				"iLeftWidth":100
		 	} );
		   
		});
		
		 
		function searchTypes(searchFlag){
			if(oldSearchFlag==searchFlag){
				return;
			}
			$("#searchType").val(searchFlag);
			//必须不传
			$("#start").val("");
			$("#end").val("");
			//$("input[name='country']").val("");
			$("#searchForm").submit();
		}
		
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ${'total' eq saleReport.country?'class=active':'' }><a class="countryHref"  key="total">总计</a></li>
		<li ${'eu' eq saleReport.country?'class=active':'' }><a class="countryHref"  key="eu">欧洲总计</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li ${dic.value eq saleReport.country?'class=active':'' }><a  class="countryHref"  key="${dic.value}" >${dic.label}</a></li>
			</c:if>
		</c:forEach>	
		
	</ul>
	
    <form:form id="searchForm" modelAttribute="saleReport" action="${ctx}/amazoninfo/salesReprots/getMaxOrder" method="post" class="breadcrumb form-search">
		<div style="height: 80px;line-height: 40px;">
		
            <spring:message code="amaInfo_businessReport_productName"/>:
			<select name="productsName" multiple class="multiSelect required"  style="width:80%" id="productsName">
				<c:forEach items="${productsName}" var="pName">
					<option value="${pName}" selected>${pName}</option>			
				</c:forEach>
				<c:forEach items="${productNames}" var="pName">
					<option value="${pName}">${pName}</option>			
				</c:forEach>
			</select>&nbsp;&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code="sys_but_search"/>"/>&nbsp;&nbsp;
			<input id="btnExport" class="btn btn-primary" type="button" value="Export"/>&nbsp;&nbsp;
		<input id="searchType" name="searchType" type="hidden" value="${saleReport.searchType}" />
		<input name="country" type="hidden" value="${saleReport.country}"/>
		<ul class="nav nav-pills" style="width:250px;float:left;" id="myTab">
			<li data-toggle="pills" id="showTab0"><a href="#" onclick="javaScript:searchTypes('1');return false;">By Day</a></li>
			<li data-toggle="pills" id="showTab1"><a href="#" onclick="javaScript:searchTypes('2');return false;">By Week</a></li>
			<li data-toggle="pills" id="showTab2"><a href="#" onclick="javaScript:searchTypes('3');return false;">By Month</a></li>
		</ul>&nbsp;&nbsp;
		<!-- <span style="float: center;"> -->
		<label></label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="start" value="<fmt:formatDate value="${saleReport.start}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
		&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${saleReport.end}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
		
		<!-- </span> -->
		</div>
	</form:form>

  <c:if test="${not empty data }">
     <div class="tab-content">
	
		<div id="total" class="tab-pane active">
			<table id="totalTb" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th style="text-align: center;vertical-align: middle;">产品</th>
						<th style="text-align: center;vertical-align: middle;">总计</th>
						
						<%-- <c:forEach items="${data}" var="productData"> 
						      <th style="text-align: center;vertical-align: middle;"> ${productData.key }</th>
						</c:forEach> --%>
						<c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
						   <th style="text-align: center;vertical-align: middle;">${xAxis[(fn:length(xAxis)-i.count)]}</th>
						</c:forEach>
					</tr>
				</thead>
				<tbody>
				    <c:forEach var="productData" items="${data}">
				    <tr>
				           <td style="text-align: center;vertical-align: middle;"> 
						        <a target='blank' href="${ctx}/psi/psiInventory/productInfoDetail?productName=${productData.key}"> ${productData.key}</a>
						    </td>
				           <td style="text-align: center;vertical-align: middle;"> 
						         ${data[productData.key][saleReport.country]['total'] }
						   </td> 
						   <c:forEach begin="1" end="${fn:length(xAxis)}" step="1" varStatus="i">
							   <td style="text-align: center;vertical-align: middle;"> 
							     <c:set var="date" value="${xAxis[(fn:length(xAxis)-i.count)]}" />
						         <a target='blank' href="${ctx}/amazoninfo/salesReprots/maxOrderList?country=${saleReport.country}&type=${saleReport.searchType}&time=${date}&productName=${productData.key}">
						             ${data[productData.key][saleReport.country][date] }
						         </a> 
							   </td>
						   </c:forEach>
					</tr>	
				    </c:forEach>
					
				</tbody>
				
			</table>
		 	<div style="width:98%;border:1px solid #ccc;">
				<div id="totalChart" style="height:400px"></div>
			</div> 
		  </div>
	</div>
  
  </c:if>
	
	
</body>
</html>
