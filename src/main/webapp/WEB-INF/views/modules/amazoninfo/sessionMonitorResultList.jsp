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
			
			$("a[rel='popover']").popover({trigger:'hover'});
			
			$("#btnSubmit").click(function(){
				$("#searchForm").submit();
			});
			
			$(".countryHref").click(function(){
				$("input[name='country']").val($(this).attr("key"));
				$("input[name='month']").removeAttr("name");
				$("#searchForm").submit();
			});
			
			$(".asin").click(function(){
				var params = {};
				params.createDate = addMonth2(-1,$("input[name='month']").val());
				params.dataDate = $("input[name='month']").val();
				params.childAsin =$(this).parent().find("input").val();
				params.country =  '${sessionMonitor.country}';
				params.title = encodeURI($(this).text());
				window.location.href = "${ctx}/amazoninfo/businessReport/product?"+$.param(params);
			});
			
			$("#export").click(function(){
				top.$.jBox.confirm("真的要导出产品预测指标结果吗?","<spring:message code="sys_label_tips_msg"/>",function(v,h,f){
					if(v=="ok"){
						$("#searchForm").attr("action","${ctx}/amazoninfo/sessionMonitor/result/export");
						$("#searchForm").submit();
						$("#searchForm").attr("action","${ctx}/amazoninfo/sessionMonitor/result");
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
		    	for ( var i = 0; i < 10; i++) {
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
					$("#searchForm").submit();
				}
			});
			<c:if test="${'2' eq sessionMonitor.searchFlag}">
				var total=0;
				$(".price b").each(function(){
					total = total+parseFloat($(this).text());
				});
				$("#total").text(total.toFixed(2));
			</c:if>
		});
		
		
		function StringToDate(DateStr){   
		    var converted = Date.parse(DateStr);  
		    var myDate = new Date(converted);  
		    if (isNaN(myDate))  
		    {   
		        var arys= DateStr.split('-');  
		        myDate = new Date(arys[0],--arys[1],arys[2]);  
		    }  
		    return myDate;  
		}  
		
		function addMonth(i){
			var	date = new Date();
			date.setDate(1);
	        date.setMonth(date.getMonth()+i);
	        var befD = date.getFullYear() + "-" + (date.getMonth() + 1) ;
			return befD;
		}
		
		function addMonth2(i,date){
			date = StringToDate(date);
	        date.setMonth(date.getMonth()+i);
	        var befD = date.getFullYear() + "-" + (date.getMonth() + 1)+"-"+date.getDate() ;
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
		function searchType(searchFlag){
			if(oldSearchFlag==searchFlag){
				return;
			}
			if('2'==searchFlag){
				//处理月初
				var date = StringToDate($("#start").val());
				$("#start").val(date.getFullYear()+"-"+ (date.getMonth() + 1)+"-1");
			}else{
				$("input[name='month']").removeAttr("name");
			}
			$('#searchFlag').val(searchFlag);
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${sessionMonitor.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>	
	</ul>
	<form:form id="searchForm" modelAttribute="sessionMonitor" action="${ctx}/amazoninfo/sessionMonitor/result" method="post" class="breadcrumb form-search">
		<div style="height: 30px">
		<input name="country" type="hidden" value="${sessionMonitor.country}"/>
		<input id="searchFlag" name="searchFlag" type="hidden" value="${sessionMonitor.searchFlag}" />
		<ul class="nav nav-pills" style="width:200px;float:left;" id="myTab">
			<li data-toggle="pills" id="showTab0"><a href="#" onclick="javaScript:searchType('0')">By Day</a></li>
			<li data-toggle="pills" id="showTab2"><a href="#" onclick="javaScript:searchType('2')">By Month</a></li>
		</ul>
		<div class="pagination" id="monthPage" style="display: inline;"></div>
		<div style="float: right;">
			<span id="date" >
				<input style="width: 100px" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',onpicked:function(){$('#searchForm').attr('action','${ctx}/amazoninfo/sessionMonitor/result');$('#searchForm').submit();return true}});" readonly="readonly"  class="Wdate" type="text" name="month" value="<fmt:formatDate value="${sessionMonitor.month}" pattern="yyyy-MM-dd"/>" class="input-small" id="start"/>
			</span>
			ProductName：<input name="productName" type="text" value="${sessionMonitor.productName}" style="width: 100px"/>
			&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="<spring:message code="sys_but_search"/>"/>
			&nbsp;&nbsp;<input id="export" class="btn btn-primary" type="button" value="导出"/>
		</div>
		</div>
	</form:form>
	<div class="alert alert-info"><strong ><spring:message code="sys_label_businessReport_tips"/></strong></div>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr> 
				<th style="text-align: center;vertical-align: middle;width: 5%">No.</th>
				<th style="text-align: center;vertical-align: middle;width: 20%">ProductName</th>
				<th style="width: 8%;text-align: center;vertical-align: middle">ASIN</th>
				<th style="width: 8%;text-align: center;vertical-align: middle">
					<a rel="popover" data-content="在按天模式下:该值为后期达标需要达到的每日均值.当值为负数时，即表示月指标已经达标.">Target<br/>Sessions</a>
				</th>
				<th style="width: 8%;text-align: center;vertical-align: middle" >Sessions</th>
				<th style="width: 8%;text-align: center;vertical-align: middle">Target<br/>Conversion</th>
				<th style="width: 8%;text-align: center;vertical-align: middle" >Conversion</th>
				<th style="width: 8%;text-align: center;vertical-align: middle" >Sale Price</th>
				<c:if test="${'2' eq sessionMonitor.searchFlag}"><th style="width: 8%;text-align: center;vertical-align: middle" >Sales forecast<br/>(EUR)</th></c:if>
				<th style="width: 8%;text-align: center;vertical-align: middle" >Pass</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${data}" var="product" varStatus="j">
				<c:forEach items="${product.asins}" var="asinEntry" varStatus="i">
					<c:if test="${i.index==0}">
						<tr>
							<td rowspan="${fn:length(product.asins)}" style="text-align: center;vertical-align: middle;">${j.count}</td>
							<td rowspan="${fn:length(product.asins)}" style="text-align: center;vertical-align: middle;">
							 	<a class="asin" rel="popover" data-content="Click here for details">${product.productName}</a>
								<input type="hidden" value="${asinEntry.key}"/>
							</td>
							<td style="text-align: center;vertical-align: middle;"><a target="_blank" href="${product.links[asinEntry.key] }" rel="popover" data-content="${asinEntry.value.tip}">${asinEntry.key}</a></td>
							<td rowspan="${fn:length(product.asins)}" style="text-align: center;vertical-align: middle;">
								${sessionMonitor.searchFlag eq '0'?product.sessionsByDate:product.sessions}
							</td>
							<td rowspan="${fn:length(product.asins)}" style="text-align: center;vertical-align: middle;">
								<c:choose>
									<c:when test="${not empty product.sessions || not empty product.sessionsByDate}">
										<c:if test="${sessionMonitor.searchFlag eq '0'}">
											<span style="color: ${product.realSessions>=product.sessionsByDate?'green':'red'}">${product.realSessions}</span>
										</c:if>
										<c:if test="${sessionMonitor.searchFlag eq '2'}">
											<span style="color: ${product.realSessions>=product.sessions?'green':'red'}">${product.realSessions}</span>
										</c:if>
									</c:when>
									<c:otherwise>
										<span>${product.realSessions}</span>
									</c:otherwise>
								</c:choose>
							</td>
							<td rowspan="${fn:length(product.asins)}" style="text-align: center;vertical-align: middle;">
								${product.conver}${not empty product.conver?'%':''}
							</td>
							<td rowspan="${fn:length(product.asins)}" style="text-align: center;vertical-align: middle;">
								<c:choose>
									<c:when test="${not empty product.conver}">
										<span style="color: ${product.realConver>=product.conver?'green':'red'}">${product.realConver}${not empty product.realConver?'%':''}</span>
									</c:when>
									<c:otherwise>
										<span>${product.realConver}${not empty product.realConver?'%':''}</span>
									</c:otherwise>
								</c:choose>
							</td>
							<td rowspan="${fn:length(product.asins)}" style="text-align: center;vertical-align: middle;">
								<b>${product.price}</b>	
							</td>
							<c:if test="${'2' eq sessionMonitor.searchFlag}"><td  class="price" rowspan="${fn:length(product.asins)}" style="font-size:14px;text-align: center;vertical-align: middle;">
								<b>${product.productsPrice}</b>	
							</td></c:if>
							<td  rowspan="${fn:length(product.asins)}" style="font-size:20px;text-align: center;vertical-align: middle;color:${product.isPass?'green':'red'}">
								<b>${product.isPass?'√':'×'}</b>	
							</td>
						</tr>
					</c:if>
					<c:if test="${i.index!=0}">
						<tr>
							<td style="text-align: center;vertical-align: middle;"><a target="_blank" href="${product.links[asinEntry.key] }" rel="popover" data-content="${asinEntry.value.tip}">${asinEntry.key}</a></td>
						</tr>
					</c:if>
				</c:forEach>
			</c:forEach>	
			<c:if test="${'2' eq sessionMonitor.searchFlag}">
				<tr>
						<td style="text-align: center;vertical-align: middle;font-size:14px"><b>Total</b></td>
						<td colspan="7"></td>
						<td style="text-align: center;vertical-align: middle;"><b style="font-size:14px" id="total"> </b></td>
						<td></td>
				</tr>	
			</c:if>
		</tbody>
	</table>
</body>
</html>
