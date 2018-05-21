<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>供应商准时交付率</title>
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
		
		
		$(document).ready(function() {
			
			
			var myChart;
			<c:forEach items="${rateType}" var="rateType" varStatus="i"></c:forEach>
			
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
				            myChart = ec.init(document.getElementById('main'));
				            var option = {
					            	title:{text:'供应商准时交付率按月统计',x:'center'},		
					                tooltip : {
					                    trigger: 'item'
					                },
					                legend: {
					                	y:30,
					                	selected: {'非供应商原因逾期':true,'供应商原因逾期':true,'提前':true,'正常':true},
					                    data:['非供应商原因逾期','供应商原因逾期','提前','正常']
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
				                            formatter: '{value}%'
				                        },
				                        boundaryGap:[0,0.5],
				                        min:0, 
				                        max:100 
				                    }
				                ], 
					                series : [
										{
										    name:'非供应商原因逾期',
										    type:'line',
										    data:[<c:forEach items="${xAxis}" var="x" varStatus="i">
										    <fmt:formatNumber  pattern="#######.##" value="${empty rate['0'][x]?0:rate['0'][x]}"  maxFractionDigits="2" />
										    ${i.last?'':','}</c:forEach>],
										    markLine : {
										    	 data : [
										            {type : 'average', name: '平均线'}
										        ]
										    }
										},
										{
										    name:'供应商原因逾期',
										    type:'line',
										    data:[<c:forEach items="${xAxis}" var="x" varStatus="i">
										    <fmt:formatNumber  pattern="#######.##" value="${empty rate['3'][x]?0:rate['3'][x]}"  maxFractionDigits="2" />
										    ${i.last?'':','}</c:forEach>],
										    markLine : {
										    	 data : [
										            {type : 'average', name: '平均线'}
										        ]
										    }
										},{
										    name:'提前',
										    type:'line',
										    data:[<c:forEach items="${xAxis}" var="x" varStatus="i">
										    <fmt:formatNumber  pattern="#######.##" value="${empty rate['1'][x]?0:rate['1'][x]}"  maxFractionDigits="2" />
										    ${i.last?'':','}</c:forEach>],
										    markLine : {
										    	 data : [
										            {type : 'average', name: '平均线'}
										        ]
										    }
										},{
										    name:'正常',
										    type:'line',
										    data:[<c:forEach items="${xAxis}" var="x" varStatus="i">
										    <fmt:formatNumber  pattern="#######.##" value="${empty rate['2'][x]?0:rate['2'][x]}"  maxFractionDigits="2" />
										    ${i.last?'':','}</c:forEach>],
										    markLine : {
										    	 data : [
										            {type : 'average', name: '平均线'}
										        ]
										    }
										}
					                ]
					                
					            };
				            
				            myChart.setOption(option);
				            initActive($(".btn-group .active").attr('act')-1);
				        }
				    );
			
			 
			 $("#supplier").change(function(){
				$("#searchForm").submit();
			});
			 
			 $("#modifyMemo").change(function(){
				 $("#searchForm").submit();
			 });
			 
			 
			 $("#expExcel").click(function(){
				 $("#searchForm").attr("action","${ctx}/psi/purchaseOrder/exportDeliveryRate");
				 $("#searchForm").submit();
				 $("#searchForm").attr("action","${ctx}/psi/purchaseOrder/deliveryRate");
			 });
		});
		 
		function queryDetail(month,type){
			var html="<div class='showChildrenHtml' style='text-align:center;margin-left:10px;' ><table style='width:98%;margin-top:10px'  class='table table-striped table-bordered table-condensed'><thead><tr><th style='width:8%;'>订单号</th><th  style='width:5%;'>供应商</th><th  style='width:13%;'>产品名称</th><th  style='width:8%;'>状态</th><th style='width:8%;'>订单交期</th><th style='width:8%;'>第一次收货</th><th style='width:8%;'>最新一次收货</th><th style='width:5%;'>已收批次</th><th>备注</th></tr></thead><tbody>";
			 <c:forEach items='${itemMap}' var='temp'>
			   if("${temp.key}"==type){
			   <c:forEach items="${itemMap[temp.key]}" var='temp2'>
			       if("${temp2.key}"==month){
			    	   <c:forEach items="${itemMap[temp.key][temp2.key]}" var='temp3'>
			    	       var purchaseOrderNo='${temp3.forecastRemark}';
			    	       var tempUrl="";
			    	       if(purchaseOrderNo.indexOf("_LC")>0){
			    	    	   tempUrl="${ctx}/psi/lcPurchaseOrder/view?id=${temp3.forecastItemId}";
			    	       }else{
			    	    	   tempUrl="${ctx}/psi/purchaseOrder/view?id=${temp3.forecastItemId}";
			    	       }
			    	      html=html+"<tr><td><a target='_blank' href='"+tempUrl+"'>${temp3.forecastRemark}</a></td><td>${temp3.colorCode}</td><td >${temp3.productName}</td><td>${temp3.delFlag}</td><td >${empty temp3.deliveryDate?'':fns:getDateByPattern(temp3.deliveryDate,'yyyy-MM-dd')}</td><td >${empty temp3.updateDate?'':fns:getDateByPattern(temp3.updateDate,'yyyy-MM-dd')}</td><td >${empty temp3.actualDeliveryDate?'':fns:getDateByPattern(temp3.actualDeliveryDate,'yyyy-MM-dd')}</td><td >${temp3.quantityOrdered==0?"":temp3.quantityOrdered}</td><td >${temp3.remark}</td></tr>";
			    	   </c:forEach>
			       }
			   </c:forEach>
		      }
			</c:forEach>
			html=html+"</tbody></table></div>";
			top.$.jBox.open(html,"查看明细",1000,350,{buttons: {'关闭': true}});  
		}
	</script>
</head>
<body>
 	<ul class="nav nav-tabs">
   		<li class="active"><a href="${ctx}/psi/purchaseOrder/deliveryRate">交货延期率</a></li>
    	<li><a href="${ctx}/psi/lcPsiLadingBill/testCount">产品合格率</a></li>
    	<li><a href="${ctx}/psi/lcPsiLadingBill/testCountSupplier">供应商合格率</a></li>
    	<li><a href="${ctx}/psi/purchaseOrder/supplierIndemnifyList">供应商赔偿记录</a></li>
    	<shiro:hasPermission name="psi:supplierIndemnify:edit">
    		<li><a href="${ctx}/psi/purchaseOrder/supplierIndemnifyForm">新增供应商赔偿记录</a></li>
    	</shiro:hasPermission>
	</ul>
	
	<form:form id="searchForm" modelAttribute="businessReport" action="${ctx}/psi/purchaseOrder/deliveryRate" method="post" class="breadcrumb form-search">
		<div style="height: 50px;line-height: 50px;">
		  <shiro:hasPermission name="psi:supplier:view">
		   <label>供应商：</label>
			<select style="width:150px;" id="supplier" name="supplier.id">
			    <option value="" ${purchaseOrder.supplier eq '' ?'selected':''}><spring:message code="amazon_order_tips4"/></option>
				<c:forEach items="${suppliers}" var="supplier" varStatus="i">
					 <option value='${supplier.id}' ${purchaseOrder.supplier.id eq supplier.id ?'selected':''}>${supplier.nikename}</option>;
				</c:forEach>
			</select>&nbsp;&nbsp;&nbsp;
			</shiro:hasPermission>
		    <label>产品名称：</label>
		    <select id="modifyMemo" name="modifyMemo" style="width:220px">
	    		<option value="">全部</option>
		    	<c:forEach items="${productList}" var="proEntry">
		    		<option value="${proEntry}" ${proEntry eq purchaseOrder.modifyMemo?'selected':'' }>${proEntry}</option>
		    	</c:forEach>
	       </select>
		    
		    &nbsp;&nbsp;&nbsp;
			
		   <input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();;return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${purchaseOrder.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();;return true}});" readonly="readonly"  class="Wdate" type="text" name="purchaseDate" value="<fmt:formatDate value="${purchaseOrder.purchaseDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="end"/>
			&nbsp;&nbsp;
			<shiro:hasPermission name="psi:supplier:view">
			    <input id="expExcel" class="btn btn-warning" type="button" value="导出excel"/>
			</shiro:hasPermission>  
		</div>
	</form:form>

     <div style="width: 100%;float:left;border:1px solid #ccc">
		<div id="nodata" class="alert alert-success" style="height: 100px;vertical-align: middle;text-align: center;display: none">
  			<br/>
  			<h3><spring:message code="sys_label_no_data"/>~~~~</h3>
		</div>
		<div id="main" style="height:460px"></div>
	</div>
	<div style="float: left;width:100%">
		<div style="margin-top:10px">
		<c:if test="${not empty rate }">
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr> 
						<td style="text-align: center;vertical-align: middle">${empty purchaseOrder.supplier.nikename?'总计':purchaseOrder.supplier.nikename}</td>
						<td style="text-align: center;vertical-align: middle" class="title">非供应商原因逾期</td>
					 	<td style="text-align: center;vertical-align: middle" class="title">供应商原因逾期</td> 
						<td style="text-align: center;vertical-align: middle" class="title">提前</td>
						<td style="text-align: center;vertical-align: middle" class="title">正常</td>
						<td style="text-align: center;vertical-align: middle;color: red" class="title">质检合格率</td>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${xAxis}" var="xAxis" varStatus="i">
						<tr>
						    <td style="text-align: center;vertical-align: middle">${xAxis}</td>
							<td style="text-align: center;vertical-align: middle"> 
							   <c:if test="${not empty mapNum['0'][xAxis] }"><a onclick='queryDetail("${xAxis}","0")'>${mapNum['0'][xAxis] }</a>(<fmt:formatNumber  pattern="#######.##" value="${rate['0'][xAxis]}"  maxFractionDigits="2" />%)</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle"> 
							   <c:if test="${not empty mapNum['3'][xAxis] }"><a onclick='queryDetail("${xAxis}","3")'>${mapNum['3'][xAxis] }</a>(<fmt:formatNumber  pattern="#######.##" value="${rate['3'][xAxis]}"  maxFractionDigits="2" />%)</c:if>
							</td> 
							<td style="text-align: center;vertical-align: middle">
							  <c:if test="${not empty mapNum['1'][xAxis] }"><a onclick='queryDetail("${xAxis}","1")'>${mapNum['1'][xAxis] }</a>(<fmt:formatNumber  pattern="#######.##" value="${rate['1'][xAxis]}"  maxFractionDigits="2" />%)</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle">
							  <c:if test="${not empty mapNum['2'][xAxis] }"><a onclick='queryDetail("${xAxis}","2")'>${mapNum['2'][xAxis] }</a>(<fmt:formatNumber  pattern="#######.##" value="${rate['2'][xAxis]}"  maxFractionDigits="2" />%)</c:if>
							</td>
							<td style="text-align: center;vertical-align: middle">
							  <c:if test="${not empty rateMap[tempSupplierId][xAxis] }">
							  		<c:set var="qStr" value="${rateMap[tempSupplierId][xAxis]}"/>
							  		<fmt:formatNumber pattern="#.00" value="${(fn:split(qStr,',')[0]-fn:split(qStr,',')[1])*100/fn:split(qStr,',')[0]}" />% 
							  </c:if>
							</td>
						</tr>	
					</c:forEach>
				</tbody>
			</table>
		</c:if>
		</div>
	</div>
	
	
</body>
</html>
