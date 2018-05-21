<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>分产品广告费用统计</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
		.spanexr {
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
	</style>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		
		if(!(top)){
			top = self;
		}
		var oldSearchFlag;
		$(document).ready(function() {
			$("#contentTable").dataTable({
				"sDom" : "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
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
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
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

			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("#end").removeAttr("name");
				$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/");
				$("#searchForm").submit();
			});
			
			
			$("#btnSubmit").click(function(){
				$('#searchForm').attr('action','${ctx}/amazoninfo/advertising/listByProduct?');
				$("#searchForm").submit();
			});
			
			$("#btnExport").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/exportProductReport");
						$("#searchForm").submit();
						$('#searchForm').attr('action','${ctx}/amazoninfo/advertising/listByProduct?');
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
		});
		
		function searchType(searchFlag){
			if(oldSearchFlag==searchFlag){
				return;
			}
			$("#day").val("");
			$("#end").val("");
			var params = {};
			params.active =  $(".btn-group .active").attr('act');
			$("#searchFlag").val(searchFlag);
			$('#searchForm').attr('action','${ctx}/amazoninfo/advertising/listByProduct?'+$.param(params));
			$("#searchForm").submit();
		}
		
		function timeOnChange(){
			var searchFlag = '${flag}';
			var params = {};
			params.active =  $(".btn-group .active").attr('act');
			if(country == null && searchFlag != 0){
				params.day = $("#day").val();
				params.end = $("#end").val();
			}
			$('#searchForm').attr('action','${ctx}/amazoninfo/advertising/listByProduct?'+$.param(params));
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
		
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
		<li class="active"><a href="${ctx}/amazoninfo/advertising/listByProduct" >分产品广告费用统计</a></li>
		<li><a href="${ctx}/amazoninfo/advertising/viewWeekReport" >广告统计</a></li>
	</ul>

	<form:form id="searchForm" modelAttribute="saleProfit" action="${ctx}/amazoninfo/advertising/listByProduct" method="post" class="breadcrumb form-search">
		<div style="height: 30px;margin-top:10px">
		<ul class="nav nav-pills" style="width:300px;float:left;" id="myTab">
		<li data-toggle="pills" id="showTab0"><a href="#" onclick="javaScript:searchType('0')">By Day</a></li>
		<li data-toggle="pills" id="showTab1"><a href="#" onclick="javaScript:searchType('1')">By Week</a></li>
		<li data-toggle="pills" id="showTab2"><a href="#" onclick="javaScript:searchType('2')">By Month</a></li>
		</ul>
		
		<input id="searchFlag" name="flag" type="hidden" value="${flag}" />
		<input name="country" type="hidden" value="${saleProfit.country}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${orderBy}"/>
		
		<span style="float: right;">
			<c:if test="${flag == '0'}"> <%--ByDay --%>
				<label>日期：</label>
				<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="day" value="<fmt:formatDate value="${date}" pattern="yyyy-MM-dd"/>" class="input-small" id="day"/>
				&nbsp;-&nbsp;
				<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${date1}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			</c:if>
			<c:if test="${flag == '1'}"> <%--ByWeek --%>
				<label>日期：</label><input style="width: 100px" onclick="WdatePicker({isShowWeek:true,weekMethod:'MSExcel',onpicked:date1Week,errDealMode:3,firstDayOfWeek:1});" readonly="readonly"  class="Wdate" type="text" name="day" value="${saleProfit.day}" class="input-small" id="day"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({isShowWeek:true,weekMethod:'MSExcel',onpicked:date2Week,errDealMode:3,firstDayOfWeek:1});" readonly="readonly"  class="Wdate" type="text" name="end" value="${saleProfit.end}" id="end" class="input-small"/>
			</c:if>
			<c:if test="${flag == '2'}"> <%--ByMonth --%>
				<label>日期：</label><input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="day" value="<fmt:formatDate value="${date}" pattern="yyyy-MM"/>" class="input-small" id="day"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${date1}" pattern="yyyy-MM" />" id="end" class="input-small"/>
			</c:if>
		&nbsp;&nbsp;
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
		&nbsp;&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="<spring:message code="sys_but_export"/>"/>
		</span>
		</div>
	</form:form>
	<div class="alert">
	  	<button type="button" class="close" data-dismiss="alert">&times;</button>
	  	<strong>Tips:所有货币单位统一为欧元(€)</strong> 
	</div>
	<div style="overflow-x:auto;">
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
				<th rowspan="2">日期</th>
				<th rowspan="2">产品</th>
				<th colspan="3">总计</th>
				<c:forEach items="${fns:getDictList('platform')}" var="dic">
					<c:if test="${dic.value ne 'com.unitek' && dic.value ne 'mx'}">
						<th colspan="3">${dic.label}</th>
					</c:if>
				</c:forEach>
			</tr>
			<tr>
				<th>费用</th>
				<th>销量</th>
				<th>销售额</th>
				<c:forEach items="${fns:getDictList('platform')}" var="dic">
					<c:if test="${dic.value ne 'com.unitek' && dic.value ne 'mx'}">
						<th>费用</th>
						<th>销量</th>
						<th>销售额</th>
					</c:if>
				</c:forEach>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${data}" var="dateMap" varStatus="i">
				<c:set var="day" value="${dateMap.key}"/>
				<c:forEach items="${data[day]}" var="productMap" varStatus="i">
					<c:set var="productName" value="${productMap.key}"/>
					<c:set var="saleProfit" value="${data[day][productName]['total']}"/>
					<c:if test="${saleProfit.adInEventFee+saleProfit.adAmsFee != 0}">
					<tr>
						<td>${day}</td>
						<td>${productName}(${saleProfit.line }线)</td>
						<td>
							<c:if test="${saleProfit.adInEventFee+saleProfit.adAmsFee != 0}">
								<fmt:formatNumber value="${saleProfit.adInEventFee+saleProfit.adAmsFee}" maxFractionDigits="2" />
							</c:if>
						</td>
						<td>
							<c:if test="${saleProfit.adInEventSalesVolume+saleProfit.adAmsSalesVolume != 0}">
								<fmt:formatNumber value="${saleProfit.adInEventSalesVolume+saleProfit.adAmsSalesVolume}" maxFractionDigits="2" />
							</c:if>
						</td>
						<td>
							<c:if test="${saleProfit.adInEventSales+saleProfit.adAmsSales != 0}">
								<fmt:formatNumber value="${saleProfit.adInEventSales+saleProfit.adAmsSales}" maxFractionDigits="2" />
							</c:if>
						</td>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<c:if test="${dic.value ne 'com.unitek' && dic.value ne 'mx'}">
							<c:set var="saleProfit" value="${data[day][productName][dic.value]}"/>
								<td>
									<c:if test="${saleProfit.adInEventFee+saleProfit.adAmsFee != 0}">
										<fmt:formatNumber value="${saleProfit.adInEventFee+saleProfit.adAmsFee}" maxFractionDigits="2" />
									</c:if>
								</td>
								<td>
									<c:if test="${saleProfit.adInEventSalesVolume+saleProfit.adAmsSalesVolume != 0}">
										<fmt:formatNumber value="${saleProfit.adInEventSalesVolume+saleProfit.adAmsSalesVolume}" maxFractionDigits="2" />
									</c:if>
								</td>
								<td>
									<c:if test="${saleProfit.adInEventSales+saleProfit.adAmsSales != 0}">
										<fmt:formatNumber value="${saleProfit.adInEventSales+saleProfit.adAmsSales}" maxFractionDigits="2" />
									</c:if>
								</td>
							</c:if>
						</c:forEach>
					</tr>
					</c:if>
				</c:forEach>
			</c:forEach>
		</tbody>
	</table></div>
</body>
</html>
