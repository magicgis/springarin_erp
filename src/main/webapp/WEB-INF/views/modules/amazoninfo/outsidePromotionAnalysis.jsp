<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<%@ include file="/WEB-INF/views/include/dialog.jsp" %>

<html>
<head>
<meta name="decorator" content="default"/>
<title>outsidePromotionView</title>
<%@include file="/WEB-INF/views/include/datatables.jsp" %>
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

	$(document).ready(function(){
		
		
		var myChart;
		//ecahrts-----------------
		require.config({
	        paths:{ 
	            echarts:'${ctxStatic}/echarts/js/echarts',
	            'echarts/chart/line': '${ctxStatic}/echarts/js/echarts',
	            'echarts/chart/bar': '${ctxStatic}/echarts/js/echarts'
	        }
	    });
	    require( ['echarts','echarts/chart/line','echarts/chart/bar'],
	        function(ec) {
	            myChart = ec.init(document.getElementById('main'));
	            var option = {
	            	title:{text:'销量图表',x:'center'},	
            	 	tooltip : {
            	        trigger: 'axis',
            	        axisPointer : {            // 坐标轴指示器，坐标轴触发有效
            	            type : 'shadow'        // 默认为直线，可选为：'line' | 'shadow'
            	        }
            	    },
	                legend: {
	                	padding:[0,0,0,0],
	                	y:30,
	                	data:['自然销量','促销销量']
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
	                        data :${axis}
	                    }
	                ],
	                yAxis : [
	                    {
	                        type : 'value',
	                    }
	                ],
	                series : [{
							      name:'自然销量',
							      type:'bar',
							      stack:'销量统计',
							      data:[<c:forEach items="${saleQuantityMap}" var='saleInfo'  varStatus="i">${fn:split(saleInfo.value,',')[0]}${i.last?'':','}</c:forEach>],
							},
							{
							      name:'促销销量',
							      type:'bar',
							      stack:'销量统计',
							      data:[<c:forEach items="${saleQuantityMap}" var='saleInfo'  varStatus="i">${fn:split(saleInfo.value,',')[1]}${i.last?'':','}</c:forEach>],
							}
	                ]
	            };
	            myChart.setOption(option);
	        });
		
		
		$("#contentTable").dataTable({
			"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
			"sPaginationType" : "bootstrap",
			"iDisplayLength" : 15,
			"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
					[ 10, 20, 60, 100, "All" ] ],
			"bScrollCollapse" : true,
			"oLanguage" : {
				"sLengthMenu" : "_MENU_ 条/页"
			},
			"aoColumns": [
		          null,
		          null,
		          null,
		          null,
		          null,
		          null,
		          null,
		          null,
		          null,
		          null,
		          null
			],
			"ordering" : true,
			 "aaSorting": [[ 0, "desc" ]]
		});
	
	});

</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/amazoninfo/outsidePromotion">站外促销分析列表</a></li>
		<li class="active"><a href="#">站外促销分析</a></li>
	</ul>
	
	<form:form id="searchForm" modelAttribute="" class="breadcrumb form-search" cssStyle="text-align:center;font-size:18px">
	
	<div >
		<div style="display:inline-block;font-size: 30px">${outsidePromotion.productName}&nbsp;&nbsp;&nbsp;&nbsp;</div>
		<div style="display:inline-block;">
			<c:forEach items="${websites}" var="site" >
				<a target="_blank" href="${site.url}">${site.website}</a> 发布日期：<fmt:formatDate value='${site.promoDate}' pattern='yyyy-MM-dd'/><br/>
			</c:forEach>
		</div>
		<div style="display:inline-block;">
		   <a href="${ctx}/amazoninfo/outsidePromotion/compare?id=${outsidePromotion.id}" ><span class="btn btn-success">对比</span></a>
		 </div>
	</div>
	</form:form>
	<table id="contentTable" class="table table-bordered table-condensed">
		<thead>
			<tr>
				<th width="10%" >日期</th>
				<th width="5%"  >库存</th>
				<th width="15%" >排名</th>
				<th width="5%" >流量</th>
				<th width="5%" >转化率(%)</th>
				<th width="5%" >总销量</th>
				<th width="5%" >促销销量</th>
				<th width="5%" >在售价格$</th>
				<th width="5%" >销售额$</th>
				<th width="5%" >利润$</th>
				<th width="5%" >促销优惠</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${rsMap}" var="dtoEntry">
				<tr>
					<c:set value="${dtoEntry.value}" var="dto" />
					<td>
						<fmt:formatDate value="${dto.dataDate}" pattern="yyyy-MM-dd"/>
						<c:if test="${dto.dataDate eq startDate}"><span style="color:green">(开始)</span></c:if>
						<c:if test="${dto.dataDate eq endDate}"><span style="color:blue">(结束)</span></c:if>
						<c:if test="${not empty promoDates&& fn:contains(promoDates,dto.dataDateStr)}"><span style="color:red">(站外发布)</span></c:if>
					 </td>
					<td>${dto.inventoryQuantity}</td>
					<td>
						<c:forEach items="${dto.ranks}" var="rank" varStatus="j">
							<a href="${rank.link}" title="${not empty rank.catalogName?rank.catalogName:rank.catalog}" target="_blank">${rank.rank}</a>&nbsp;&nbsp;
						</c:forEach>
					</td>
					<td>${dto.session}</td>
					<td><fmt:formatNumber value="${dto.conversion}" pattern="#.##"/></td>
					<td>${dto.saleQuantity}</td>
					<td>${dto.saleQuantityPro}</td>
					<td><fmt:formatNumber value="${dto.price}" pattern="#.##"/></td>
					<td><fmt:formatNumber value="${dto.saleAmount}" pattern="#.##"/></td>
					<td><fmt:formatNumber value="${dto.profitsAmount}" pattern="#.##"/></td>
					<td><fmt:formatNumber value="${dto.promoAmount}" pattern="#.##"/></td>
				</tr>
			</c:forEach>
		
		</tbody>
	</table>
	<div>
		<div id="nodata" class="alert alert-success" style="height: 100px;vertical-align: middle;text-align: center;display: none">
  			<br/>
  			<h3><spring:message code="sys_label_no_data"/>~~~~</h3>
		</div>
		<div id="main" style="height:460px"></div>
	
	</div>
</body>
</html>