<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>利润分析</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/datatables.jsp"%>

<script type="text/javascript">


	var _hmt = _hmt || [];
	(function() {
	  var hm = document.createElement("script");
	  hm.src = "//hm.baidu.com/hm.js?91dbd75732c3d59d712b52c80c56fe80";
	  var s = document.getElementsByTagName("script")[0]; 
	  s.parentNode.insertBefore(hm, s);
	})();
	
	function fmoney(s, n){   
	   temp = s;	
	   if(s<0){
		  temp= -s;
	   }
	   n1 = n;
	   n = n > 0 && n <= 20 ? n : 2;   
	   temp  = parseFloat((temp + "").replace(/[^\d\.-]/g, "")).toFixed(n) + "";   
	   var l = temp.split(".")[0].split("").reverse(),   
	   r = temp.split(".")[1];   
	   t = "";   
	   for(i = 0; i < l.length; i ++ )   
	   {   
	      t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");   
	   }   
	   temp =  t.split("").reverse().join("") + "." + r;   
	   if(s<0){
		   temp= "-"+temp;
	   }
	   if(n1==0){
		   temp = temp.replace(".00","")
	   }
	   return temp;
	} 
	
	$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn )
	{
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
			return parseFloat($('td:eq('+iColumn+')', tr).text().split(',').join(''));
		});
	};
	
	$.fn.dataTableExt.afnSortData['dom-html1'] = function ( oSettings, iColumn )
	{
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
			return parseFloat($('td:eq('+iColumn+')', tr).text().replace("%",""));
		} );
	};
	
	$(function() {
		
		if($("#flag").val()==1){
	    	 $("#showTab1").addClass("active");
		}else if($("#flag").val()==2){
	    	$("#showTab2").addClass("active");
	    }else if($("#flag").val()==3){
	    	$("#showTab3").addClass("active");
	    }else{
	    	$("#showTab1").addClass("active");
	    }
		
		$(".countryHref").click(function(){
			$("#country").val($(this).attr("key"));
			$("#searchForm").submit();
		});
		 
		 $("#export").click(function(){
			 $("#searchForm").attr("action","${ctx}/amazoninfo/salesProfits/exportMarket");
			 $("#searchForm").submit();
			 $("#searchForm").attr("action","${ctx}/amazoninfo/salesProfits/marketList");
		 });
		
		$("#contentTable").dataTable({
			"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
			"sPaginationType" : "bootstrap",
			"sScrollX": "100%",
			"iDisplayLength" : 15,
			"aLengthMenu" : [ [ 15, 30, 60, 100, -1 ],
					[ 15, 30, 60, 100, "All" ] ],
			"bScrollCollapse" : true,
			"aoColumns": [
					         null,
					         null,	
					         null,	
					         null,	
						     { "sSortDataType":"dom-html", "sType":"numeric" },
						     { "sSortDataType":"dom-html", "sType":"numeric" },
						     { "sSortDataType":"dom-html1", "sType":"numeric" }
						   ],
			"oLanguage" : {
				"sLengthMenu" : "_MENU_ 条/页"
			},
			"ordering" : true
		});
		
		$(".countryHref").click(function(){
			$("#country").val($(this).attr("key"));
			$("#searchForm").submit();
		});

		 
		$("a[rel='popover']").popover({trigger:'hover'});
	});
	
	function changeType(){
		$("#searchForm").submit();
	}
	
	function searchType(searchFlag){
		var oldSearchFlag= $("#flag").val();
		if(oldSearchFlag==searchFlag){
			return;
		}
		$("#day").val("");
		$("#end").val("");
		$("#flag").val(searchFlag);
		$("#searchForm").submit();
	}
	
	function timeOnChange(obj){
		var id = obj.id;
		if('day' == id){
			$("#end").val(obj.value);
		}
		$("#searchForm").submit();
	}

</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li ><a class="countryHref" href="${ctx}/amazoninfo/salesProfits" key="">总计</a></li>
		<li ><a class="countryHref" href="${ctx}/amazoninfo/salesProfits?country=eu" key="eu">欧洲</a></li>
		<li><a class="countryHref" href="${ctx}/amazoninfo/salesProfits?country=en" key="en">英语国家</a></li>
		<li ><a class="countryHref" href="${ctx}/amazoninfo/salesProfits?country=nonEn" key="nonEn">非英语国家</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li ><a class="countryHref" href="${ctx}/amazoninfo/salesProfits?country=${dic.value}" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
		<li class="active"><a href="${ctx}/amazoninfo/salesProfits/marketList">O2O</a></li>
		<li class="dropdown">
		   <a class="dropdown-toggle"  data-toggle="dropdown" href="#"><span class='otherPlatform'>Other</span><b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
				<li class="${'de'  eq profit.country?'active':''}"><a class="countryHref" href="${ctx}/amazoninfo/salesProfits/ebayProfit?country=de" key='de'>DE Ebay</a></li>
				<li class="${'com' eq profit.country?'active':''}"><a class="countryHref" href="${ctx}/amazoninfo/salesProfits/ebayProfit?country=com" key="com">US Ebay</a></li>
				<li class="active"><a href="${ctx}/amazoninfo/salesProfits/marketList">O2O</a></li>
		    </ul>
	   </li>
	</ul>
	<form id="searchForm" action="${ctx}/amazoninfo/salesProfits/marketList" method="post" class="breadcrumb form-search">
		<input type="hidden" id="country" name="country" value="${saleProfit.country}"></input>
		<input type="hidden" id="flag" name="flag" value="${flag}" />
		<ul class="nav nav-pills" style="width:270px;float:left;" id="myTab">
			<li data-toggle="pills" id="showTab2"><a href="#" onclick="javaScript:searchType('2')">By Day</a></li>
			<li data-toggle="pills" id="showTab1"><a href="#" onclick="javaScript:searchType('1')">By Month</a></li>
			<li data-toggle="pills" id="showTab3"><a href="#" onclick="javaScript:searchType('3')">By Year</a></li>
		</ul>
		<c:if test="${'2' eq flag }">
			<label>统计日期：</label>
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){timeOnChange(this);return true}});" readonly="readonly"  class="Wdate" type="text" name="day" value="<fmt:formatDate value="${date}" pattern="yyyy-MM-dd" />" id="day" class="input-small"/>
			&nbsp;至&nbsp;
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){timeOnChange(this);return true}});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${date1}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
		</c:if>
		<c:if test="${'1' eq flag }">
			<label>统计月份：</label>
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM',onpicked:function(){timeOnChange(this);return true}});" readonly="readonly"  class="Wdate" type="text" name="day" value="<fmt:formatDate value="${date}" pattern="yyyy-MM" />" id="day" class="input-small"/>
			&nbsp;至&nbsp;
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM',onpicked:function(){timeOnChange(this);return true}});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${date1}" pattern="yyyy-MM" />" id="end" class="input-small"/>
		</c:if>
		<c:if test="${'3' eq flag }">
			<label>统计年度：</label>
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy',onpicked:function(){timeOnChange(this);return true}});" readonly="readonly"  class="Wdate" type="text" name="day" value="<fmt:formatDate value="${date}" pattern="yyyy" />" id="day" class="input-small"/>
			&nbsp;至&nbsp;
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy',onpicked:function(){timeOnChange(this);return true}});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${date1}" pattern="yyyy" />" id="end" class="input-small"/>
		</c:if>
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		&nbsp;&nbsp;<input id="export" class="btn btn-primary" type="button" value="导出"/>
	</form>
	<div class="alert">
	  	<button type="button" class="close" data-dismiss="alert">&times;</button>
	  	<strong>Tips:所有货币单位统一为欧元(€)</strong> 
	</div>
	<tags:message content="${message}"/>
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th>日期</th>
						<th>产品名称</th>
						<th>平台</th>
						<th>销量</th>
						<th>销售额(€)</th>
						<th>利润(€)</th>
						<th>利润率</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${list}" var="saleProfit" varStatus="i">
						<tr>
							<td>${saleProfit.day}</td>
							<td>
								<a href="${ctx}/psi/psiInventory/productInfoDetail?productName=${saleProfit.productName}">${saleProfit.productName}</a>
								(${saleProfit.line}线)
							</td>
							<td>${fns:getDictLabel(saleProfit.country,'platform','')}</td>
							<td>
								<fmt:formatNumber value="${saleProfit.salesVolume}" maxFractionDigits="0" />
							</td>
							<td>
								<fmt:formatNumber value="${saleProfit.sales}" maxFractionDigits="2" />
							</td>
							<td>
								<fmt:formatNumber value="${saleProfit.profits}" maxFractionDigits="2" />
							</td>
							<td>
								<fmt:formatNumber value="${saleProfit.profits*100/saleProfit.sales}" maxFractionDigits="2" />%
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
</body>
</html>
