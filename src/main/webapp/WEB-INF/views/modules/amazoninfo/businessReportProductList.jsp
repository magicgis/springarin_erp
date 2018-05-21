<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>产品详情报表</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	
	<script type="text/javascript">
		var oldSearchFlag;
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		$(document).ready(function() {
			oldSearchFlag= $("#searchFlag").val();	
			$("#reback").click(function(){
				window.history.go(-1);				
			});
			// 表格排序
			var orderBy = $("#orderBy").val().split(" ");
			$("#contentTable th.sort").each(function(){
				if ($(this).hasClass(orderBy[0])){
					orderBy[1] = orderBy[1]&&orderBy[1].toUpperCase()=="DESC"?"down":"up";
					$(this).html($(this).html()+" <i class=\"icon icon-arrow-"+orderBy[1]+"\"></i>");
				}
			});
			$("#contentTable th.sort").click(function(){
				var order = $(this).attr("class").split(" ");
				var sort = $("#orderBy").val().split(" ");
				for(var i=0; i<order.length; i++){
					if (order[i] == "sort"){order = order[i+1]; break;}
				}
				if (order == sort[0]){
					sort = (sort[1]&&sort[1].toUpperCase()=="DESC"?"ASC":"DESC");
					$("#orderBy").val(order+" DESC"!=order+" "+sort?"":order+" "+sort);
				}else{
					$("#orderBy").val(order+" ASC");
				}
				page();
			});
			
			$("span[rel='popover']").popover({trigger:'hover'});
			
			if($("#searchFlag").val()==0){
		    	 $("#showTab0").addClass("active");
		    }else if($("#searchFlag").val()==1){
		    	$("#showTab1").addClass("active");
		    }else if($("#searchFlag").val()==2){
		    	$("#showTab2").addClass("active");
		    }else{
		    	$("#showTab0").addClass("active");
		    } 
		});
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$("#searchForm").submit();
        	return false;
        }
		
		function searchType(searchFlag){
			if(oldSearchFlag==searchFlag){
				return;
			}
			$('#searchFlag').val(searchFlag);
			$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport/product');
			$("#searchForm").submit();
		}
		
	</script>
</head>
<body>
	<form:form id="searchForm" modelAttribute="businessReport" action="${ctx}/amazoninfo/businessReport/product" method="post" class="breadcrumb form-search">
		<input name="childAsin" type="hidden" value="${businessReport.childAsin}"/>
		<input id="selectC" name="country" type="hidden" value="${businessReport.country}"/>
		<input name="title" type="hidden" value="${businessReport.title}"/>
		<div style="height: 30px">
			<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
			<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
			<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
			<input id="searchFlag" name="searchFlag" type="hidden" value="${businessReport.searchFlag}" />
			日期区间：
			<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${businessReport.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			&nbsp;-&nbsp;&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="dataDate" value="<fmt:formatDate value="${businessReport.dataDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="end"/>
			<ul class="nav nav-pills" style="width:500px;float:right;" id="myTab">
				<li data-toggle="pills" id="showTab0"><a href="#" onclick="searchType('0')">By Day</a></li>
				<li data-toggle="pills" id="showTab1"><a href="#" onclick="searchType('1')">By Week</a></li>
				<li data-toggle="pills" id="showTab2"><a href="#" onclick="searchType('2')">By Month</a></li>
			</ul>
		</div>
	</form:form>
	<div class="alert alert-info"><strong>${fns:getDictLabel(businessReport.country,'platform','')}:${businessReport.title}&nbsp;&nbsp;Product Details</strong>&nbsp;&nbsp;&nbsp;<input class="btn" type="button" value="<spring:message code='sys_but_back'/>" id="reback"/></div>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr> 
				<th style="width: 100px" class="sort dataDate" >Date</th>
				<th style="width: 30px" class="sort sessions">Sessions</th>
				<th style="width: 50px" class="sort conversion">conversion</th>
				<th style="width: 50px" class="sort ordersPlaced">Orders<br/>Placed</th>
				<th style="width: 50px" class="sort unitsOrdered">Units<br/>Ordered</th>
				<th style="width: 50px" class="sort unitSessionPercentage">Unit<br/>Session(%)</th>
				<th style="width: 50px" class="sort pageViews">Page<br/>Views</th>
				<th style="width: 50px" class="sort buyBoxPercentage">Buy<br/>Box(%)</th>
				<!-- <th style="width: 50px" class="sort grossProductSales">Cross<br/>Product Sales</th> -->
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="rep">
			<tr>
				<c:set value="${fns:getDateByPattern(rep.dataDate,'yyyy-MM')}" var="date" />
				<td>
					<c:if test="${'1' eq businessReport.searchFlag }">
						<fmt:formatDate value="${rep.dataDate}" pattern="yyyy-w周" />
						&nbsp;&nbsp;&nbsp;&nbsp;${rep.dateSpan}
					</c:if>
					<c:if test="${'1' ne businessReport.searchFlag }">
						<fmt:formatDate value="${rep.dataDate}" pattern="yyyy-MM-dd" />
					</c:if>
				</td>
				
				<c:if test="${businessReport.searchFlag ne '0' || empty sessionMap[date] }">
					<td>${rep.sessions}</td>
					<td><fmt:formatNumber value="${rep.conversion}" minFractionDigits="2"/>%</td>
				</c:if>
				<c:if test="${businessReport.searchFlag eq '0' && not empty sessionMap[date]}">
					<c:if test="${empty sessionMap[date].sessionsByDate}">
						<td>${rep.sessions}</td>
					</c:if>
					<c:if test="${not empty sessionMap[date].sessionsByDate}">
						<td style="color:${rep.sessions>=sessionMap[date].sessionsByDate?'green':'red'}">
							<span rel="popover" data-content="Target Sessions:${sessionMap[date].sessionsByDate}">${rep.sessions}</span>
						</td>
					</c:if>
					<c:if test="${empty sessionMap[date].conver}">
						<td><fmt:formatNumber value="${rep.conversion}" minFractionDigits="2"/>%</td>
					</c:if>
					<c:if test="${not empty sessionMap[date].conver}">
						<td style="color:${rep.conversion>=sessionMap[date].conver?'green':'red'}">
							<span rel="popover" data-content="Target Conversion:${sessionMap[date].conver}%"><fmt:formatNumber value="${rep.conversion}" minFractionDigits="2"/>%
							</span></td>
					</c:if>
				</c:if>
				<td>${rep.ordersPlaced}</td>
				<td>${rep.unitsOrdered}</td>
				<td><fmt:formatNumber value="${rep.unitSessionPercentage}" minFractionDigits="2"/>%</td>
				<td>${rep.pageViews}</td>
				<td>${rep.buyBoxPercentage}%</td>
				<%--<td><fmt:formatNumber value="${rep.grossProductSales}" minFractionDigits="2"/>
				<c:choose><c:when test="${businessReport.country eq 'jp'}">¥</c:when><c:when test="${businessReport.country eq 'com'}">$</c:when><c:when test="${businessReport.country eq 'uk'}">£</c:when><c:otherwise>€</c:otherwise></c:choose>
				</td> --%>
			</tr>
			</c:forEach>		
		</tbody>
	</table>
</body>
</html>
