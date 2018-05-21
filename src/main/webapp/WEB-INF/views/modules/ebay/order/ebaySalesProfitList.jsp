<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>利润分析</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/datatables.jsp"%>
<style type="text/css">
		.spanexr{ float:right;min-height:40px}
		.spanexl{ float:left;}
</style>		
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
	
	$(function() {
		
		if($("#type").val()==1){
	    	 $("#showTab1").addClass("active");
		}else if($("#type").val()==0){
	    	$("#showTab0").addClass("active");
	    }
		
		$(".countryHref").click(function(){
			$("#country").val($(this).attr("key"));
			$("#searchForm").submit();
		});
		 
	
		$("a[rel='popover']").popover({trigger:'hover'});	
		var arr = $("#contentTable tbody tr");
		var num1 = 0;
		var num2 = 0;
		var num3 = 0;
		var num4 = 0;
		var num5 = 0;
		var num6 = 0;
		var num7 = 0;
		var num8 = 0;
		$("#contentTable").dataTable({
			"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
			"sPaginationType" : "bootstrap",
			"iDisplayLength" : 10,
			"aLengthMenu" : [ [ 10, 20, 60, 100, -1 ],
					[ 10, 20, 60, 100, "All" ] ],
			"bScrollCollapse" : true,
			"oLanguage" : {
				"sLengthMenu" : "_MENU_ 条/页"
			},
			"ordering" : true,
			"aaSorting": [[ 0, "asc" ]],
            "fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
	             if(iDisplayIndex==0){
	            	 addd1=0;
	            	 addd2=0;
	            	 addd3=0;
	            	 addd4=0;
	            	 addd5=0;
	            	 addd6=0;
	            	 addd7=0;
	            	 addd8=0;
	             }

	             if($.isNumeric(aData[3])){
	            	 addd1 += parseFloat(aData[3]);//第几列
	             } else {
	            	 addd1 += parseFloat(aData[3].split(',').join(''));//第几列
	             }
	             if($.isNumeric(aData[4])){
	            	 addd2 += parseFloat(aData[4]);//第几列
	             } else {
	            	 addd2 += parseFloat(aData[4].split(',').join(''));//第几列
	             }
	             if($.isNumeric(aData[5])){
	            	 addd3 += parseFloat(aData[5]);//第几列
	             } else {
	            	 addd3 += parseFloat(aData[5].split(',').join(''));//第几列
	             }
	             if($.isNumeric(aData[6])){
	            	 addd4 += parseFloat(aData[6]);//第几列
	             } else {
	            	 addd4 += parseFloat(aData[6].split(',').join(''));//第几列
	             }
	             if($.isNumeric(aData[7])){
	            	 addd5 += parseFloat(aData[7]);//第几列
	             } else {
	            	 addd5 += parseFloat(aData[7].split(',').join(''));//第几列
	             }
	             if($.isNumeric(aData[8])){
	            	 addd6 += parseFloat(aData[8]);//第几列
	             } else {
	            	 addd6 += parseFloat(aData[8].split(',').join(''));//第几列
	             }
	             if($.isNumeric(aData[9])){
	            	 addd7 += parseFloat(aData[9]);//第几列
	             } else {
	            	 addd7 += parseFloat(aData[9].split(',').join(''));//第几列
	             }
	             if($.isNumeric(aData[10])){
	            	 addd8 += parseFloat(aData[10]);//第几列
	             } else {
	            	 addd8 += parseFloat(aData[10].split(',').join(''));//第几列
	             }
	          
	             $(".salesVolume").html(fmoney(addd1,2));
	             $(".sales").html(fmoney(addd2,2));
	             $(".salesNoTax").html(fmoney(addd3,2));
	             $(".ebayFee").html(fmoney(addd4,2));
	             $(".transportFee").html(fmoney(addd5,2));
	             $(".price").html(fmoney(addd6,2));
	             $(".buyCost").html(fmoney(addd7,2));
	             $(".profits").html(fmoney(addd8,2));
	            
	             return nRow;
	         },"fnPreDrawCallback": function( oSettings ) { 
	        	     $(".salesVolume").html(0);
		             $(".sales").html(0);
		             $(".salesNoTax").html(0);
		             $(".ebayFee").html(0);
		             $(".transportFee").html(0);
		             $(".price").html(0);
		             $(".buyCost").html(0);
		             $(".profits").html(0);
	         }  
		});
		
		 $(".row:first").append($("#searchContent").html());
		 arr.each(function() {
				num1 += parseInt($(this).find("td :eq(3)").text().replace(',',''));
				num2 += parseFloat($(this).find("td :eq(4)").text().replace(',',''));
				num3 += parseInt($(this).find("td :eq(5)").text().replace(',',''));	
				num4 += parseInt($(this).find("td :eq(6)").text().replace(',',''));	
				num5 += parseFloat($(this).find("td :eq(7)").text().replace(',',''));
				num6 += parseFloat($(this).find("td :eq(8)").text().replace(',',''));
				num7 += parseFloat($(this).find("td :eq(9)").text().replace(',',''));
				num8 += parseFloat($(this).find("td :eq(10)").text().replace(',',''));
		});
		var tr = $("#contentTable tfoot tr#totalTr");
		tr.find("td :eq(3)").text(fmoney(num1.toFixed(2),2));
		tr.find("td :eq(4)").text(fmoney(num2.toFixed(2),2));
		tr.find("td :eq(5)").text(fmoney(num3.toFixed(2),2));
		tr.find("td :eq(6)").text(fmoney(num4.toFixed(2),2));
		tr.find("td :eq(7)").text(fmoney(num5.toFixed(2),2));
		tr.find("td :eq(8)").text(fmoney(num6.toFixed(2),2));
		tr.find("td :eq(9)").text(fmoney(num7.toFixed(2),2));
		tr.find("td :eq(10)").text(fmoney(num8.toFixed(2),2));
	});
	
	function changeType(){
		$("#searchForm").submit();
	}
	
	function searchType(searchFlag){
		var oldSearchFlag= $("#type").val();
		if(oldSearchFlag==searchFlag){
			return;
		}
		$("#type").val(searchFlag);
		$("#start").val("");
		$("#end").val("");
		$("#searchForm").submit();
	}
	function timeOnChange(){
		$("#searchForm").submit();
	}

</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<shiro:hasPermission name="amazoninfo:profits:view">
		<li ><a class="countryHref" href="${ctx}/amazoninfo/salesProfits" key="">总计</a></li>
		<li ><a class="countryHref" href="${ctx}/amazoninfo/salesProfits?country=eu" key="eu">欧洲</a></li>
		<li><a class="countryHref" href="${ctx}/amazoninfo/salesProfits?country=en" key="en">英语国家</a></li>
		<li ><a class="countryHref" href="${ctx}/amazoninfo/salesProfits?country=nonEn" key="nonEn">非英语国家</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li ><a class="countryHref" href="${ctx}/amazoninfo/salesProfits?country=${dic.value}" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
		</shiro:hasPermission>
		<li class="active"><a class="countryHref" href="${ctx}/amazoninfo/salesProfits/ebayProfit?country=${profit.country}" key='${profit.country }'>${'de'  eq profit.country?'DE Ebay':'US Ebay'}</a></li>
		<li class="dropdown">
		   <a class="dropdown-toggle"  data-toggle="dropdown" href="#"><span class='otherPlatform'>Other</span><b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
				<li class="${'de'  eq profit.country?'active':''}"><a class="countryHref" href="${ctx}/amazoninfo/salesProfits/ebayProfit?country=de" key='de'>DE Ebay</a></li>
				<li class="${'com' eq profit.country?'active':''}"><a class="countryHref" href="${ctx}/amazoninfo/salesProfits/ebayProfit?country=com" key="com">US Ebay</a></li>
				<shiro:hasPermission name="amazoninfo:profits:view">
				<li><a href="${ctx}/amazoninfo/salesProfits/marketList">O2O</a></li>
				</shiro:hasPermission>
		    </ul>
	   </li>
	</ul>
	
	<form id="searchForm" action="${ctx}/amazoninfo/salesProfits/ebayProfit" method="post" class="breadcrumb form-search">
		<input type="hidden" id="country" name="country" value="${profit.country}"></input>
		<input type="hidden" id="type" name="type" value="${profit.type}" />
		<ul class="nav nav-pills" style="width:180px;float:left;" id="myTab">
			<li data-toggle="pills" id="showTab0"><a href="#" onclick="javaScript:searchType('0')">By Day</a></li>
			<li data-toggle="pills" id="showTab1"><a href="#" onclick="javaScript:searchType('1')">By Month</a></li>
		</ul>
<%-- 
		<label>统计日期：</label>
		<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="start" value="<fmt:formatDate value="${profit.start}" pattern="yyyy-MM-dd" />" id="start" class="input-small"/>
		-<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${profit.end}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
		 --%>
		<c:if test="${'1' ne profit.type }">
			<label>统计日期：</label>
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="start" value="<fmt:formatDate value="${date}" pattern="yyyy-MM-dd" />" id="start" class="input-small"/>
		   -<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd'});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${date1}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
		</c:if>
		<c:if test="${'1' eq profit.type }">
			<label>统计月份：</label>
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="start" value="<fmt:formatDate value="${date}" pattern="yyyy-MM" />" id="start" class="input-small"/>
			&nbsp;至&nbsp;
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM',onpicked:function(){timeOnChange();return true}});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${date1}" pattern="yyyy-MM" />" id="end" class="input-small"/>
		</c:if>
		
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
	</form>
	<div class="alert">
	  	<button type="button" class="close" data-dismiss="alert">&times;</button>
	  	<strong>Tips:货币单位统一为${'de' eq profit.country?'欧元':'美元'}(${'de' eq profit.country?'€':'$'})</strong> 
	</div>
	<tags:message content="${message}"/>
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<th>No.</th>
						<th>日期</th>
						<th>产品名称</th>
						<th>销量</th>
						<th>销售额(${'de' eq profit.country?'€':'$'})</th>
						<th>税后收入(${'de' eq profit.country?'€':'$'})</th>
						<th>Ebay处理费(${'de' eq profit.country?'€':'$'})</th>
						<th>运输费(${'de' eq profit.country?'€':'$'})</th>
						<th>邮费(${'de' eq profit.country?'€':'$'})</th>
						<th>采购成本(${'de' eq profit.country?'€':'$'})</th>
						<th>利润(${'de' eq profit.country?'€':'$'})</th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${profitList}" var="saleProfit" varStatus="i">
						<tr>
							<td>${i.index+1 }</td>
							<td>${saleProfit.date }</td>
							<td>${saleProfit.productName }</td>
							<td>${saleProfit.salesVolume }</td>
							<td>${saleProfit.sales }</td>
							<td>${saleProfit.salesNoTax }</td>
							<td>-${saleProfit.ebayFee }</td>
							<td>-${saleProfit.transportFee }</td>
							<td>${not empty saleProfit.price?-saleProfit.price:0 }</td>
							<td>-${saleProfit.buyCost }</td>
							<td>${saleProfit.profits }</td>
				       </tr>
				    </c:forEach>
				</tbody>
				<tfoot>
				<tr>
					    <td style="font-weight: bold;">Page Total</td>
					    <td></td>
				        <td></td>
				        <td id="salesVolume" class="salesVolume"></td>
						<td id="sales" class="sales"></td>
						<td id="salesNoTax" class="salesNoTax"></td>
						<td id="ebayFee" class="ebayFee"></td>
						<td id="transportFee" class="transportFee"></td>
						<td id="price" class="price"></td>
						<td id="buyCost" class="buyCost"></td>
						<td id="profits" class="profits"></td>
				  </tr> 
				  <tr id = "totalTr">
					    <td style="font-weight: bold;">Total</td>
					    <td></td>
				        <td></td>
				        <td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
				  </tr> 
				</tfoot>
			</table>
</body>
</html>
