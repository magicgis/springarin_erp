<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>亚马逊商业报表</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.sort{color:#0663A2;cursor:pointer;}
		.blue{color:#8A2BE2;}
	</style>
	
	<script type="text/javascript">
		var _hmt = _hmt || [];
		(function() {
		  var hm = document.createElement("script");
		  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
		  var s = document.getElementsByTagName("script")[0]; 
		  s.parentNode.insertBefore(hm, s);
		})();
		var oldSearchFlag;
		$(document).ready(function() {
			oldSearchFlag= $("#searchFlag").val();	
			$(".asin").click(function(){
				var params = {};
				params.createDate = $("#start").val();
				params.dataDate = $("#end").val();
				params.childAsin =$(this).parent().find("input").val();
				params.country =  '${businessReport.country}';
				params.title = encodeURI($(this).text());
				window.location.href = "${ctx}/amazoninfo/businessReport/product?"+$.param(params);
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
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			
			$("#btnSubmit").click(function(){
				$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport');
				$("#searchForm").submit();
			});
			
			$(".countryHref").click(function(){
				$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport');
				$("input[name='country']").val($(this).attr("key"));
				$("#searchForm").submit();
			});
			
			$("#btnExport").click(function(){
				top.$.jBox.confirm("<spring:message code="sys_label_tips_export"/>","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/businessReport/export");
						$("#searchForm").submit();
					}
				},{buttonsFocus:1});
				top.$('.jbox-body .jbox-icon').css('top','55px');
			});
			
			if($("#searchFlag").val()==0){
		    	 $("#showTab0").addClass("active");
		    }else if($("#searchFlag").val()==2){
		    	$("#showTab2").addClass("active");
		    	$("#date").hide();
		    	var ul = "<ul>";
		    	for ( var i = 0; i < 12; i++) {
		    		var month = addMonth(0-i);
		    		if($("#start").val().indexOf(addMonth1(0-i)+"-")>=0){
		    			ul = ul +"<li class='active'><a class='month'>"+month+"</a></li>";
		    		}else{
						ul = ul +"<li><a class='month'>"+month+"</a></li>";
		    		}
				}
		    	ul = ul +"</ul>";
		 	  	$("#monthPage").html(ul);
		    	
		    }else{
		    	$("#showTab0").addClass("active");
		    }
			
			$("#searchForm").on("click",".month",function(){
				if($(this).parent().attr("class")!="active"){
					var month = $(this).text();
					$("#start").val(month+"-1");
					var date = StringToDate(month+"-1");
					date.setMonth((date.getMonth() + 1),0);
					$("#end").val(month+"-"+date.getDate());
					$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport');
					$("#searchForm").submit();
				}
			});
		});
		
		
		function StringToDate(DateStr){   
		    var converted = Date.parse(DateStr);  
		    var myDate = new Date(converted);  
		    if (isNaN(myDate))  
		    {   
		        //var delimCahar = DateStr.indexOf('/')!=-1?'/':'-';  
		        var arys= DateStr.split('-');  
		        myDate = new Date(arys[0],--arys[1],arys[2]);  
		    }  
		    return myDate;  
		}  
		
		function addMonth(i){
			var date = new Date();
			date.setDate(1);
	        date.setMonth(date.getMonth()+i);
	        var befD = date.getFullYear() + "-" + (date.getMonth() + 1) ;
			return befD;
		}
		
		function addMonth1(i){
			var date =  new Date();
			date.setDate(1);
	        date.setMonth(date.getMonth()+i);
	        var month = (date.getMonth() + 1);
	        if(month<10){
	        	month = "0"+month;
	        }
	        var befD = date.getFullYear() + "-" +month;
			return befD;
		}
		
		function page(n,s){
			if(n && s){
				$("#pageNo").val(n);
				$("#pageSize").val(s);
			}
			$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport');
			$("#searchForm").submit();
        	return false;
        }
		
		function searchType(searchFlag){
			if(oldSearchFlag==searchFlag){
				return;
			}
			if('2'==searchFlag){
				//处理月初
				var date = StringToDate($("#start").val());
				$("#start").val(date.getFullYear()+"-"+ (date.getMonth() + 1)+"-1");
				date.setMonth((date.getMonth() + 1),0);
				$("#end").val(date.getFullYear()+"-"+ (date.getMonth() + 1)+"-"+date.getDate());
			}
			$('#searchFlag').val(searchFlag);
			$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport');
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${businessReport.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>
	<form:form id="searchForm" modelAttribute="businessReport" action="${ctx}/amazoninfo/businessReport/" method="post" class="breadcrumb form-search">
		<div style="height: 30px">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<input id="orderBy" name="orderBy" type="hidden" value="${page.orderBy}"/>
		<input name="country" type="hidden" value="${businessReport.country}"/>
		<input id="searchFlag" name="searchFlag" type="hidden" value="${businessReport.searchFlag}" />
		<ul class="nav nav-pills" style="width:180px;float:left;" id="myTab">
			<li data-toggle="pills" id="showTab0"><a href="#" onclick="javaScript:searchType('0')">By Day</a></li>
			<li data-toggle="pills" id="showTab2"><a href="#" onclick="javaScript:searchType('2')">By Month</a></li>
		</ul>
		<div class="pagination" id="monthPage" style="display: inline;"></div>
		<div style="float: right;">
			<span id="date" >
				<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="createDate" value="<fmt:formatDate value="${businessReport.createDate}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
				&nbsp;-&nbsp;<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/amazoninfo/businessReport');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="dataDate" value="<fmt:formatDate value="${businessReport.dataDate}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
				&nbsp;&nbsp;&nbsp;&nbsp;
			</span>
			ASIN/Model：<input name="pAsin" type="text"   value="${businessReport.pAsin}" style="width: 100px"/>
			&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code="sys_but_search"/>"/>
			&nbsp;&nbsp;<input id="btnExport" class="btn btn-primary" type="button" value="<spring:message code="sys_but_export"/>"/>
		</div>
		</div>
	</form:form>
	<div class="alert alert-info"><strong ><spring:message code="sys_label_businessReport_tips"/></strong></div>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr> 
				<th style="text-align: center;vertical-align: middle;"><spring:message code="sys_label_country"/></th>
				<th style="width: 30px;text-align: center;vertical-align: middle">ASIN</th>
				<th style="width: 80px;text-align: center;vertical-align: middle">Model</th>
				<th style="text-align: center;vertical-align: middle;">Title</th>
				<th style="width: 30px;text-align: center;vertical-align: middle" class="sort sessions">Sessions</th>
				<th style="width: 50px;text-align: center;vertical-align: middle" class="sort conversion">Conversion</th>
				<th style="width: 50px;text-align: center;vertical-align: middle" class="sort sessionPercentage">Session(%)</th>
				<th style="width: 50px;text-align: center;vertical-align: middle" class="sort ordersPlaced">Orders<br/>Placed</th>
				<th style="width: 50px;text-align: center;vertical-align: middle" class="sort unitsOrdered">Units<br/>Ordered</th>
				<th style="width: 50px;text-align: center;vertical-align: middle" class="sort unitSessionPercentage">Unit<br/>Session(%)</th>
				<th style="width: 50px;text-align: center;vertical-align: middle" class="sort pageViews">Page<br/>Views</th>
				<th style="width: 50px;text-align: center;vertical-align: middle" class="sort pageViewsPercentage">Page<br/>Views(%)</th>
				<th style="width: 50px;text-align: center;vertical-align: middle" class="sort buyBoxPercentage">Buy<br/>Box(%)</th>
				<!-- <th style="width: 50px" class="sort grossProductSales">Cross<br/>Product Sales</th> -->
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${page.list}" var="rep" varStatus="i">
			<tr>
				<td style="text-align: center;vertical-align: middle;">${rep.countryStr}</td>
				<td style="text-align: center;vertical-align: middle;"><a target="_blank" href="${fn:replace(rep.href,'com2', 'com')}" rel="popover" data-content="Click to view the Amazon page">${rep.childAsin}</a></td>
				<td style="text-align: center;vertical-align: middle;"><a class="asin" rel="popover" data-content="Click here for details">${products[i.index]}</a>
					<input type="hidden" value="${rep.childAsin}"/>
				</td>
				<td style="text-align: center;vertical-align: middle;"><a rel="popover" data-content="${rep.title}">${rep.titleStr}</a></td>
				
				<td style="text-align: center;vertical-align: middle;">${rep.sessions}</td>
				<td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${rep.conversion}" minFractionDigits="2"/>%</td>
				<td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${rep.sessionPercentage}" minFractionDigits="2"/>%</td>
				<td style="text-align: center;vertical-align: middle;">${rep.ordersPlaced}</td>
				<td style="text-align: center;vertical-align: middle;">${rep.unitsOrdered}</td>
				<td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${rep.unitSessionPercentage}" minFractionDigits="2"/>%</td>
				<td style="text-align: center;vertical-align: middle;">${rep.pageViews}</td>
				<td style="text-align: center;vertical-align: middle;"><fmt:formatNumber value="${rep.pageViewsPercentage}" minFractionDigits="2"/>%</td>
				<td style="text-align: center;vertical-align: middle;">${rep.buyBoxPercentage}%</td>
				<%--<td><fmt:formatNumber value="${rep.grossProductSales}" minFractionDigits="2"/>
				<c:choose><c:when test="${businessReport.country eq 'jp'}">¥</c:when><c:when test="${businessReport.country eq 'com'}">$</c:when><c:when test="${businessReport.country eq 'uk'}">£</c:when><c:otherwise>€</c:otherwise></c:choose>
				</td> --%>
				
				
			</tr>
			</c:forEach>		
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>
