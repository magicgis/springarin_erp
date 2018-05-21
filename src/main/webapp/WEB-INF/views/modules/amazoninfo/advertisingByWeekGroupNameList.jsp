<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>advertising</title>
	<meta name="decorator" content="default"/>
	<%@include file="/WEB-INF/views/include/dialog.jsp" %>
	<%@include file="/WEB-INF/views/include/datatables.jsp" %>
	<link href="${ctxStatic}/x-editable/css/bootstrap-editable.css" rel="stylesheet" />
	<script src="${ctxStatic}/x-editable/js/bootstrap-editable.js" type="text/javascript"></script>
	<style type="text/css">
		.spanexr{ float:right;min-height:40px;padding-top: 5px}
		.spanexl{ float:left;}
		.blue{background-color:#D2E9FF;font-style: italic;font-weight: bold;}
	</style>
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		
		$(document).ready(function() {

			$("#exportAdv").click(function(){
				$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/exportWeekReport");
				$("#searchForm").submit();
				$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/viewWeekReport");
			});

			$(".countryHref").click(function(){
				$("#country").val($(this).attr("key"));
				$("#end").removeAttr("name");
				$("#searchForm").attr("action","${ctx}/amazoninfo/advertising/");
				$("#searchForm").submit();
			});
			
			 var oTable = $("#contentTable").dataTable({"sDom": "<'row'<'spanexl'l><'spanexr'f>r>t<'row'<'spanexl'i><'spanexr'p>>",
					"sPaginationType": "bootstrap","sScrollX": "100%",
				 	"bScrollCollapse": true,"oLanguage": {"sLengthMenu": "_MENU_ 条/页"},"ordering":true,
				 	"aaSorting": [[0, "desc" ]]
				});
			 new FixedColumns( oTable,{
			 		"iLeftColumns":4,
					"iLeftWidth": 490
			 	} );
			// $(".row:first").append($("#searchContent").html());
			  
		});
		
		
	</script>
</head>
<body>
   <ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek' && dic.value ne 'mx'}">
				<li><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
		<shiro:hasPermission name="spreadReport:price:view">
			<li><a href="${ctx}/amazoninfo/advertising/listByProduct" >分产品广告费用统计</a></li>
		</shiro:hasPermission>
		<li class='active'><a href="${ctx}/amazoninfo/advertising/viewWeekReport" >广告统计</a></li>
	</ul>
	<!-- <div style="display: none" id="searchContent"> -->
	<form:form id="searchForm"  modelAttribute="advertising" action="${ctx}/amazoninfo/advertising/viewWeekReport" method="post" class="breadcrumb form-search">
		&nbsp;&nbsp;&nbsp;&nbsp;时间:<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"  readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${advertising.createDate}" pattern="yyyy-MM-dd" />" id="satrt" class="input-small"/>
		&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd'});"  readonly="readonly"  class="Wdate" type="text" name="dataDate" value="<fmt:formatDate value="${advertising.dataDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
			国家：<select name="country" id="country" style="width: 120px">
						<option value="" ${advertising.country eq ''?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<c:forEach items="${fns:getDictList('platform')}" var="dic">
							<option value="${dic.value}" ${dic.value eq advertising.country ?'selected':''}  >${dic.label}</option>
						</c:forEach>
				</select>&nbsp;&nbsp;
			GroupName：<select name="groupName" id="groupName" style="width: 200px">
						<option value="" ${advertising.groupName eq ''?'selected':''}><spring:message code="amazon_order_tips4"/></option>
						<c:forEach items="${groupNameSet}" var="groupName">
							<option value="${groupName}" ${groupName eq advertising.groupName ?'selected':''}  >${groupName}</option>
						</c:forEach>
				</select>&nbsp;&nbsp;
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="<spring:message code="sys_but_search"/>"/>
			<input id="exportAdv" class="btn btn-primary" type="button" value="广告报表"/>
	</form:form>
	<!-- </div> -->
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
	
	   <c:if test="${not empty advertising.groupName}">
	      <thead>
			<tr>
				<th style="text-align: center;vertical-align: middle;width:20%;">国家</th>
				<th style="text-align: center;vertical-align: middle;width:10%;">name</th>
				<th style="text-align: center;vertical-align: middle;width:10%;">group_name</th>
				<th style="text-align: center;vertical-align: middle;width:10%;">sku</th>
                <th style="text-align: center;vertical-align: middle;width:10%;">week</th>
				<th style="text-align: center;vertical-align: middle;width:10%;">spend</th>
				<th style="text-align: center;vertical-align: middle;width:10%;"><a title='click>=200'>click</a></th>
				<th style="text-align: center;vertical-align: middle;width:10%;">order</th>
				<th style="text-align: center;vertical-align: middle;width:10%;"><a title='JP>700;Other>8'>cost/order</a></th>
				<th style="text-align: center;vertical-align: middle;width:10%;"><a title='conv_nature<0.05'>conv_nature</a></th>
				<th style="text-align: center;vertical-align: middle;width:10%;"><a title='conv_ads<0.05'>conv_ads</a></th>
				<th style="text-align: center;vertical-align: middle;width:10%;"><a title='ratio<0.7'>ratio</a></th>
			    <th style="text-align: center;vertical-align: middle;width:10%;">ACOS</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${totalMap}" var="map">
			    <c:forEach items="${totalWeek}" var="week"  varStatus="i">
			       <c:if test="${not empty totalMap[map.key][week] }">
			            <tr>
			         	       <td>${fns:getDictLabel(totalMap[map.key][week].country,'platform','') }</td>
				         	   <td>${totalMap[map.key][week].name }</td>
				         	   <td>${totalMap[map.key][week].groupName }</td>
				         	   <td>${totalMap[map.key][week].sku }</td>
				         	    <td>${week}</td>
				         	   <td><fmt:formatNumber value="${totalMap[map.key][week].totalSpend }" maxFractionDigits="2"/></td>
				         	   <td>${totalMap[map.key][week].clicks }</td>
				         	   <td>${totalMap[map.key][week].sameSkuOrdersPlaced }</td>
				         	   <td style="${totalMap[map.key][week].sameSkuOrdersPlaced>0&&totalMap[map.key][week].totalSpend/totalMap[map.key][week].sameSkuOrdersPlaced>('jp' eq totalMap[map.key][week].country?700:8)?'color:#ff0033;':'' }" ><c:if test='${totalMap[map.key][week].sameSkuOrdersPlaced>0 }'><fmt:formatNumber value="${totalMap[map.key][week].totalSpend/totalMap[map.key][week].sameSkuOrdersPlaced }" maxFractionDigits="2"/></c:if></td>
			         	       <td style="${not empty totalMap[map.key][week].conversion&&totalMap[map.key][week].conversion<0.05?'color:#ff0033;':'' }" ><c:if test='${not empty totalMap[map.key][week].conversion }'>${totalMap[map.key][week].conversion}</c:if></td>
			         	       <td style="${totalMap[map.key][week].clicks>0 &&totalMap[map.key][week].sameSkuOrdersPlaced/totalMap[map.key][week].clicks<0.05?'color:#ff0033;':'' }"><c:if test='${totalMap[map.key][week].clicks>0 }'><fmt:formatNumber value="${totalMap[map.key][week].sameSkuOrdersPlaced/totalMap[map.key][week].clicks }" maxFractionDigits="2"/></c:if></td>
			         	       <td style="${totalMap[map.key][week].clicks>0 &&not empty totalMap[map.key][week].conversion&&totalMap[map.key][week].sameSkuOrdersPlaced/totalMap[map.key][week].clicks/totalMap[map.key][week].conversion<0.7?'color:#ff0033;':'' }"><c:if test='${totalMap[map.key][week].clicks>0 &&not empty totalMap[map.key][week].conversion}'><fmt:formatNumber value="${totalMap[map.key][week].sameSkuOrdersPlaced/totalMap[map.key][week].clicks/totalMap[map.key][week].conversion}" maxFractionDigits="2"/></c:if></td>
			                   <td>
					              <c:if test="${totalMap[map.key][week].sameSkuOrderSales>0 }"><fmt:formatNumber value="${totalMap[map.key][week].totalSpend*100/totalMap[map.key][week].sameSkuOrderSales}" maxFractionDigits="2"/>%</c:if>
				               </td>
			          </tr>  
			       </c:if>
			        
			     </c:forEach>	
			</c:forEach>
		</tbody>
	   
	   </c:if>
	    <c:if test="${empty advertising.groupName}">
	       <thead>
			<tr>
				<th rowspan="2" style="text-align: center;vertical-align: middle;width:20%;">国家</th>
				<th rowspan="2" style="text-align: center;vertical-align: middle;width:10%;">name</th>
				<th rowspan="2" style="text-align: center;vertical-align: middle;width:10%;">group_name</th>
				<th rowspan="2" style="text-align: center;vertical-align: middle;width:10%;">sku</th>
				<c:forEach items="${totalWeek}" var="week">
				   <th colspan="7" style="text-align: center;vertical-align: middle;width:30%;">${week}</th>
				</c:forEach>
			</tr>
			<tr>
			    	<c:forEach items="${totalWeek}" var="week">
						<th style="text-align: center;vertical-align: middle;width:10%;">spend</th>
						<th style="text-align: center;vertical-align: middle;width:10%;"><a title='click>=200'>click</a></th>
						<th style="text-align: center;vertical-align: middle;width:10%;">order</th>
						<th style="text-align: center;vertical-align: middle;width:10%;"><a title='JP>700;Other>8'>cost/order</a></th>
						<th style="text-align: center;vertical-align: middle;width:10%;"><a title='conv_nature<0.05'>conv_nature</a></th>
						<th style="text-align: center;vertical-align: middle;width:10%;"><a title='conv_ads<0.05'>conv_ads</a></th>
						<th style="text-align: center;vertical-align: middle;width:10%;"><a title='ratio<0.7'>ratio</a></th>
			    	</c:forEach>
				
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${totalMap}" var="map">
			      <tr>
			            <c:set var='flag' value='true'/>   
			            <c:forEach items="${totalWeek}" var="week"  varStatus="i">
			         	   <c:if test="${not empty totalMap[map.key][week].name&&flag}">
			         	       <td>${fns:getDictLabel(totalMap[map.key][week].country,'platform','') }</td>
				         	   <td>${totalMap[map.key][week].name }</td>
				         	   <td>${totalMap[map.key][week].groupName }</td>
				         	   <td>${totalMap[map.key][week].sku }</td>
				         	   <c:set var='flag' value='false'/>     
			         	   </c:if>
			         	</c:forEach>      
			         	<c:forEach items="${totalWeek}" var="week"  varStatus="i">
				         	   <td><fmt:formatNumber value="${totalMap[map.key][week].totalSpend }" maxFractionDigits="2"/></td>
				         	   <td>${totalMap[map.key][week].clicks }</td>
				         	   <td>${totalMap[map.key][week].sameSkuOrdersPlaced }</td>
				         	   <td style="${totalMap[map.key][week].sameSkuOrdersPlaced>0&&totalMap[map.key][week].totalSpend/totalMap[map.key][week].sameSkuOrdersPlaced>('jp' eq totalMap[map.key][week].country?700:8)?'color:#ff0033;':'' }" ><c:if test='${totalMap[map.key][week].sameSkuOrdersPlaced>0 }'><fmt:formatNumber value="${totalMap[map.key][week].totalSpend/totalMap[map.key][week].sameSkuOrdersPlaced }" maxFractionDigits="2"/></c:if></td>
			         	       <td style="${not empty totalMap[map.key][week].conversion&&totalMap[map.key][week].conversion<0.05?'color:#ff0033;':'' }" ><c:if test='${not empty totalMap[map.key][week].conversion }'>${totalMap[map.key][week].conversion}</c:if></td>
			         	       <td style="${totalMap[map.key][week].clicks>0 &&totalMap[map.key][week].sameSkuOrdersPlaced/totalMap[map.key][week].clicks<0.05?'color:#ff0033;':'' }"><c:if test='${totalMap[map.key][week].clicks>0 }'><fmt:formatNumber value="${totalMap[map.key][week].sameSkuOrdersPlaced/totalMap[map.key][week].clicks }" maxFractionDigits="2"/></c:if></td>
			         	       <td style="${totalMap[map.key][week].clicks>0 &&not empty totalMap[map.key][week].conversion&&totalMap[map.key][week].sameSkuOrdersPlaced/totalMap[map.key][week].clicks/totalMap[map.key][week].conversion<0.7?'color:#ff0033;':'' }"><c:if test='${totalMap[map.key][week].clicks>0 &&not empty totalMap[map.key][week].conversion}'><fmt:formatNumber value="${totalMap[map.key][week].sameSkuOrdersPlaced/totalMap[map.key][week].clicks/totalMap[map.key][week].conversion}" maxFractionDigits="2"/></c:if></td>
			         	 
			         	</c:forEach>
			        </tr>   	
			</c:forEach>
		</tbody>
	   
	   </c:if>
		
		
	</table>
</body>
</html>
