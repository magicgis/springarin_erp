<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>运营利润分析</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/datatables.jsp"%>

<style type="text/css">
		.dataTables_scrollBody,.dataTables_scrollFoot,.dataTables_scrollHead {
			width: 100% !important;
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
	
	$.fn.dataTableExt.afnSortData['dom-html1'] = function ( oSettings, iColumn )
	{
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
			var txt = $('td:eq(1)', tr).text();
			var txt1 = $('td:eq(10)', tr).text();
			if((txt && txt.indexOf('未匹配')>0) || '0'==txt1){
				return -10000;
			}else{
				var newColumn = iColumn -2;
				return parseFloat($('td:eq('+newColumn+')', tr).text().split(',').join(''));
			}
		} );
	};
	
	$.fn.dataTableExt.afnSortData['dom-html'] = function ( oSettings, iColumn )
	{
		return $.map( oSettings.oApi._fnGetTrNodes(oSettings), function (tr, i) {
			var newColumn = iColumn -2;
			return parseFloat($('td:eq('+newColumn+')', tr).text().replace("%",""));
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
			 $("#searchForm").attr("action","${ctx}/amazoninfo/salesProfits/exportSales");
			 $("#searchForm").submit();
			 $("#searchForm").attr("action","${ctx}/amazoninfo/salesProfits/sales");
		 });
	
		
		var arr = $("#contentTable tbody tr");
		var qty = 0;	//销量
		var sales = 0;	//销售额
		var salesNoTax = 0;	//税后
		var refund = 0;	//退款
		var amazonFee = 0;	//亚马逊费用
		var transportFee = 0;	//运输费
		var buyCost = 0;	//采购成本
		//var salesCost = 0;	//运营成本
		var grossProfit = 0;	//毛利润
		var profit = 0;	//利润
		var adFee = 0;	//广告费用
		var adQty = 0;	//广告销量
		var adSales = 0;	//广告销售额
		var adProfit = 0;	//广告盈亏
		var dealFee = 0;	//闪促费用
		var dealQty = 0;	//闪促销量
		var dealProfit = 0;	//闪促盈亏
		var supportFee = 0;	//替代货费用
		var reviewFee = 0;	//评测单费用
		var recallCost = 0;	//召回单成本
		var recallFee = 0;	//召回单费用
		var storageFee = 0;	//仓储费
		var longStorageFee = 0;	//长期仓储费
		var financialCost = 0;	//财务成本
		var expressFee = 0;	//快递费
		var vineFee = 0;	//vine项目费
		
		arr.each(function() {
			var i = 2;
			if($(this).find("td :eq("+i+")").text())
				qty += parseInt($(this).find("td :eq("+i+")").text().split(',').join(''));
			i++;
			if($(this).find("td :eq("+i+")").text())
				sales += parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i = 6;
			if($(this).find("td :eq("+i+")").text())
				salesNoTax += parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i++;
			if($(this).find("td :eq("+i+")").text())
				refund += parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i++;
			if($(this).find("td :eq("+i+")").text())
				amazonFee += parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i++;
			<c:if test="${'1' eq groupType || '2' eq groupType}">
				if($(this).find("td :eq("+i+")").text())
					transportFee += parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
				i++;
				buyCost += parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
				i++;
			</c:if>
			<%--if($(this).find("td :eq("+i+")").text())
				salesCost += parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i++;--%>
			grossProfit += parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i = i+2;
			profit += parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i = i + 5;//跳过四行
			adFee+= parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i++;
			adQty+= parseInt($(this).find("td :eq("+i+")").text().split(',').join(''));
			i++;
			adSales+= parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i++;
			adProfit+= parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i++;
			dealFee+= parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i++;
			dealQty+= parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i++;
			dealProfit+= parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i++;
			supportFee+= parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i++;
			reviewFee+= parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i++;
			recallCost+= parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i++;
			recallFee+= parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i++;
			expressFee+= parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i++;
			vineFee+= parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			i++;
			<c:if test="${'2' ne flag }">
				storageFee+= parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
				i++;
				longStorageFee+= parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
				i = i+2;
				financialCost+= parseFloat($(this).find("td :eq("+i+")").text().split(',').join(''));
			</c:if>
		});

		var tr = $("#contentTable tfoot tr#totalTr");
		tr.find("td :eq(2)").text(fmoney(qty.toFixed(0),0));
		tr.find("td :eq(3)").text(fmoney(sales.toFixed(2),2));
		
		
		var totalV=0;
		var totalS=0;
		arr.each(function() {
			   if($(this).find("td :eq(2)").text()){
				   var tempV=parseInt($(this).find("td :eq(2)").text().split(",").join(''));	//销量
				   totalV += tempV;
				   $(this).find("td :eq(4)").text(fmoney((parseInt(tempV)*100/parseInt(qty)).toFixed(2),2)+"%");
			   }
				
			   if($(this).find("td :eq(3)").text()){
					var tempS=parseFloat($(this).find("td :eq(3)").text().split(",").join(''));	//销售额
					totalS += tempS;
					$(this).find("td :eq(5)").text(fmoney((parseFloat(tempS)*100/parseFloat(sales)).toFixed(2),2)+"%");
			   }
		});
		$("#allPV").html(fmoney((parseInt(totalV)*100/parseInt(qty)).toFixed(2),2)+"%");
	    $("#allPS").html(fmoney((parseFloat(totalS)*100/parseFloat(sales)).toFixed(2),2)+"%");
		
		var index = 6;
		tr.find("td :eq("+index+")").text(fmoney(salesNoTax.toFixed(2),2));
		index++;
		tr.find("td :eq("+index+")").text(fmoney(refund.toFixed(2),2));
		index++;
		tr.find("td :eq("+index+")").text(fmoney(amazonFee.toFixed(2),2));
		index++;
		<c:if test="${'1' eq groupType || '2' eq groupType}">
			tr.find("td :eq("+index+")").text(fmoney(transportFee.toFixed(2),2));
			index++;
			tr.find("td :eq("+index+")").text(fmoney(buyCost.toFixed(2),2));
			index++;
		</c:if>
		<%--tr.find("td :eq("+index+")").text(fmoney(salesCost.toFixed(2),2));
		index++;--%>
		tr.find("td :eq("+index+")").text(fmoney(grossProfit.toFixed(2),2));
		index++;
		//毛利率
		tr.find("td :eq("+index+")").text(fmoney((grossProfit*100/sales).toFixed(2),2) + "%");
		index++;
		tr.find("td :eq("+index+")").text(fmoney(profit.toFixed(2),2));
		index = index + 2;
		tr.find("td :eq("+index+")").text(fmoney((profit/qty).toFixed(2),2));
		index++;
		tr.find("td :eq("+index+")").text(fmoney((profit*100/sales).toFixed(2),2) + "%");
		index++;
		tr.find("td :eq("+index+")").text(fmoney((sales/qty).toFixed(2),2));
		index++;
		tr.find("td :eq("+index+")").text(fmoney(adFee.toFixed(0),2));
		index++;
		tr.find("td :eq("+index+")").text(fmoney(adQty.toFixed(2),0));
		index++;
		tr.find("td :eq("+index+")").text(fmoney(adSales.toFixed(2),2));
		index++;
		tr.find("td :eq("+index+")").text(fmoney(adProfit.toFixed(2),2));
		index++;
		tr.find("td :eq("+index+")").text(fmoney(dealFee.toFixed(0),0));
		index++;
		tr.find("td :eq("+index+")").text(fmoney(dealQty.toFixed(2),2));
		index++;
		tr.find("td :eq("+index+")").text(fmoney(dealProfit.toFixed(2),2));
		index++;
		tr.find("td :eq("+index+")").text(fmoney(supportFee.toFixed(2),2));
		index++;
		tr.find("td :eq("+index+")").text(fmoney(reviewFee.toFixed(2),2));
		index++;
		tr.find("td :eq("+index+")").text(fmoney(recallCost.toFixed(2),2));
		index++;
		tr.find("td :eq("+index+")").text(fmoney(recallFee.toFixed(2),2));
		index++;
		tr.find("td :eq("+index+")").text(fmoney(expressFee.toFixed(2),2));
		index++;
		tr.find("td :eq("+index+")").text(fmoney(vineFee.toFixed(2),2));
		index++;
		<c:if test="${'2' ne flag }">
			tr.find("td :eq("+index+")").text(fmoney(storageFee.toFixed(2),2));
			index++;
			tr.find("td :eq("+index+")").text(fmoney(longStorageFee.toFixed(2),2));
			index = index + 2;
			tr.find("td :eq("+index+")").text(fmoney(financialCost.toFixed(2),2));
		</c:if>
		
		arr.each(function() {
			<c:if test="${'1' eq groupType || '2' eq groupType}">
				$(this).find("td :eq(14)").text((parseFloat($(this).find("td :eq(11)").text().split(',').join(''))*100/grossProfit).toFixed(2)+'%');
			</c:if>
			<c:if test="${'1' ne groupType && '2' ne groupType}">
				$(this).find("td :eq(12)").text((parseFloat($(this).find("td :eq(9)").text().split(',').join(''))*100/grossProfit).toFixed(2)+'%');
			</c:if>
		});

		tr = $("#contentTable tfoot tr#rateTr");
		//<c:if test="${not empty totalSales && totalSales > 0}">
			var totalSales = '${totalSales}';
			tr.find("td :eq(3)").text(fmoney((sales*100/totalSales).toFixed(2),2) + "%");
		//</c:if>
		index = 6;
		tr.find("td :eq("+index+")").text(fmoney((salesNoTax*100/sales).toFixed(2),2) + "%");
		index++;
		tr.find("td :eq("+index+")").text(fmoney((refund*100/sales).toFixed(2),2) + "%");
		index++;
		tr.find("td :eq("+index+")").text(fmoney((amazonFee*100/sales).toFixed(2),2) + "%");
		index++;
		<c:if test="${'1' eq groupType || '2' eq groupType}">
			tr.find("td :eq("+index+")").text(fmoney((transportFee*100/sales).toFixed(2),2) + "%");
			index++;
			tr.find("td :eq("+index+")").text(fmoney((buyCost*100/sales).toFixed(2),2) + "%");
			index++;
		</c:if>
		<%--
		tr.find("td :eq("+index+")").text(fmoney((salesCost*100/sales).toFixed(2),2) + "%");
		index++;--%>
		tr.find("td :eq("+index+")").text(fmoney((grossProfit*100/sales).toFixed(2),2) + "%");
		index = index +2;
		tr.find("td :eq("+index+")").text(fmoney((profit*100/sales).toFixed(2),2) + "%");
		index = index +5;
		tr.find("td :eq("+index+")").text(fmoney((adFee*100/sales).toFixed(2),2) + "%");
		index++;
		tr.find("td :eq("+index+")").text(fmoney((adQty*100/sales).toFixed(2),2) + "%");
		index++;
		tr.find("td :eq("+index+")").text(fmoney((adSales*100/sales).toFixed(2),2) + "%");
		index++;
		tr.find("td :eq("+index+")").text(fmoney((adProfit*100/sales).toFixed(2),2) + "%");
		index++;
		tr.find("td :eq("+index+")").text(fmoney((dealFee*100/sales).toFixed(2),2) + "%");
		index = index + 2;	//跳过闪促数量列
		tr.find("td :eq("+index+")").text(fmoney((dealProfit*100/sales).toFixed(2),2) + "%");
		index++;
		tr.find("td :eq("+index+")").text(fmoney((supportFee*100/sales).toFixed(2),2) + "%");
		index++;
		tr.find("td :eq("+index+")").text(fmoney((reviewFee*100/sales).toFixed(2),2) + "%");
		index++;
		tr.find("td :eq("+index+")").text(fmoney((recallCost*100/sales).toFixed(2),2) + "%");
		index++;
		tr.find("td :eq("+index+")").text(fmoney((recallFee*100/sales).toFixed(2),2) + "%");
		index++;
		tr.find("td :eq("+index+")").text(fmoney((expressFee*100/sales).toFixed(2),2) + "%");
		index++;
		tr.find("td :eq("+index+")").text(fmoney((vineFee*100/sales).toFixed(2),2) + "%");
		index++;
		tr.find("td :eq("+index+")").text(fmoney((storageFee*100/sales).toFixed(2),2) + "%");
		index++;
		tr.find("td :eq("+index+")").text(fmoney((longStorageFee*100/sales).toFixed(2),2) + "%");
		index = index + 2;
		tr.find("td :eq("+index+")").text(fmoney((financialCost*100/sales).toFixed(2),2) + "%");
		
		
		$("a[rel='popover']").popover({trigger:'hover',container: 'body'});
		
		var oTable = $("#contentTable").dataTable({
				"sDom": "<'row'<'spanexl'l><'spanexr'f><'spanexr'p>r>t<'row'<'spanexl'i><'spanexr'p>>",
				"sPaginationType" : "bootstrap",
				"sScrollX": "100%",
				"iDisplayLength" : 10,
				"aLengthMenu" : [ [ 10, 20, 50, 100, -1 ],
						[ 10, 20, 50, 100, "All" ] ],
				"bScrollCollapse" : true,
				"aoColumns": [
						         null,
						         null,	
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     <c:if test="${'1' eq groupType || '2' eq groupType}">
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     </c:if>
							     <%--{ "sSortDataType":"dom-html1", "sType":"numeric" },--%>
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     <%--{ "sSortDataType":"dom-html1", "sType":"numeric" },--%>
							     <c:if test="${'2' ne flag }">
								     { "sSortDataType":"dom-html1", "sType":"numeric" },
								     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     	{ "sSortDataType":"dom-html1", "sType":"numeric" },
									{ "sSortDataType":"dom-html1", "sType":"numeric" },
							     </c:if>
									{ "sSortDataType":"dom-html1", "sType":"numeric" }
							     ],
				"oLanguage" : {
					"sLengthMenu" : "_MENU_ 条/页"
				},
				"ordering" : true,
				"aaSorting": [[ 0, "asc" ]],
				"fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
		             if(iDisplayIndex==0){
		            	 qtyPage=0;
		            	 salesPage=0;
		            	 salesNoTaxPage=0;
		            	 refundPage=0;
		            	 amazonFeePage=0;
		            	 transportFeePage=0;
		            	 buyCostPage=0;
		            	 salesCostPage=0;
		            	 grossProfitPage=0;
		            	 profitPage=0;
		            	 profitRatioPage=0;
		            	 adFeePage=0;
		            	 adQtyPage=0;
		            	 adSalesPage=0;
		            	 adProfitPage=0;
		            	 dealFeePage=0;
		            	 dealQtyPage=0;
		            	 dealProfitPage=0;
		            	 supportFeePage=0;
		            	 reviewFeePage=0;
		            	 recallCostPage=0;
		            	 recallFeePage=0;
		            	 storageFeePage=0;
		            	 longStorageFeePage=0;
		            	 financialCostPage=0;
		            	 expressFeePage=0;
		            	 vineFeePage=0;
		             }
		             var i = 2;
		             if($.isNumeric(aData[i])){
		            	 qtyPage += parseFloat(aData[i]);//第几列
		             }else{
		            	 qtyPage += parseFloat((aData[i]).split(',').join(''));//第几列
					 }
		             i++;
		             var saleData = aData[i];
		             if(!$.isNumeric(saleData) && saleData.indexOf('</') > 0){
		            	 saleData = $(aData[i]).text();
		             }
		             if($.isNumeric(saleData)){
		            	 salesPage += parseFloat(saleData);
		             } else {
		            	 salesPage += parseFloat(saleData.split(',').join(''));
		             }
		             i = 6;
		             if($.isNumeric(aData[i])){
		            	 salesNoTaxPage += parseFloat(aData[i]);//第几列
		             } else {
		            	 salesNoTaxPage += parseFloat(aData[i].split(',').join(''));//第几列
		             }
		             i++;
		             if($.isNumeric(aData[i])){
		            	 refundPage += parseFloat(aData[i]);//第几列
		             } else {
		            	 refundPage += parseFloat(aData[i].split(',').join(''));//第几列
		             }
		             i++;
		             if($.isNumeric(aData[i])){
		            	 amazonFeePage += parseFloat(aData[i]);//第几列
		             } else {
		            	 amazonFeePage += parseFloat(aData[i].split(',').join(''));//第几列
		             }
		             i++;
		             <c:if test="${'1' eq groupType || '2' eq groupType }">
			             if($.isNumeric(aData[i])){
			            	 transportFeePage += parseFloat(aData[i]);//第几列
			             } else {
			            	 transportFeePage += parseFloat(aData[i].split(',').join(''));//第几列
			             }
			             i++;
			             if($.isNumeric(aData[i])){
			            	 buyCostPage += parseFloat(aData[i]);//第几列
			             } else {
			            	 buyCostPage += parseFloat(aData[i].split(',').join(''));//第几列
			             }
			             i++;
			         </c:if>
		             <%--if($.isNumeric(aData[i])){
		            	 salesCostPage += parseFloat(aData[i]);//第几列
		             } else {
		            	 salesCostPage += parseFloat(aData[i].split(',').join(''));//第几列
		             }
		             i++;--%>
		             if($.isNumeric(aData[i])){
		            	 grossProfitPage += parseFloat(aData[i]);//第几列
		             } else {
		            	 grossProfitPage += parseFloat(aData[i].split(',').join(''));//第几列
		             }
		             i = i+ 2;
		             if($.isNumeric(aData[i])){
		            	 profitPage += parseFloat(aData[i]);//第几列
		             } else {
		            	 profitPage += parseFloat(aData[i].split(',').join(''));//第几列
		             }
		             i++;
		             if($.isNumeric(aData[i])){
		            	 profitRatioPage += parseFloat(aData[i]);//第几列
		             } else {
		            	 profitRatioPage += parseFloat(aData[i].split(',').join(''));//第几列
		             }
		             i = i+4;
		             var data = aData[i];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
			             data = $(aData[i]).text();
		             }
		             if($.isNumeric(data)){
		            	 adFeePage += parseFloat(data);
		             } else {
		            	 adFeePage += parseFloat(data.split(',').join(''));
		             }
		             i++;
		             data = aData[i];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
			             data = $(aData[i]).text();
		             }
		             if($.isNumeric(data)){
		            	 adQtyPage += parseFloat(data);
		             } else {
		            	 adQtyPage += parseFloat(data.split(',').join(''));
		             }
		             i++;
		             data = aData[i];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
			             data = $(aData[i]).text();
		             }
		             if($.isNumeric(data)){
		            	 adSalesPage += parseFloat(data);
		             } else {
		            	 adSalesPage += parseFloat(data.split(',').join(''));
		             }
		             i++;
		             data = aData[i];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
			             data = $(aData[i]).text();
		             }
		             if($.isNumeric(data)){
		            	 adProfitPage += parseFloat(data);
		             } else {
		            	 adProfitPage += parseFloat(data.split(',').join(''));
		             }
		             i++;
		             data = aData[i];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
			             data = $(aData[i]).text();
		             }
		             if($.isNumeric(data)){
		            	 dealFeePage += parseFloat(data);
		             } else {
		            	 dealFeePage += parseFloat(data.split(',').join(''));
		             }
		             i++;
		             data = aData[i];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
			             data = $(aData[i]).text();
		             }
		             if($.isNumeric(data)){
		            	 dealQtyPage += parseFloat(data);
		             } else {
		            	 dealQtyPage += parseFloat(data.split(',').join(''));
		             }
		             i++;
		             data = aData[i];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
			             data = $(aData[i]).text();
		             }
		             if($.isNumeric(data)){
		            	 dealProfitPage += parseFloat(data);
		             } else {
		            	 dealProfitPage += parseFloat(data.split(',').join(''));
		             }
		             i++;
		             data = aData[i];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
			             data = $(aData[i]).text();
		             }
		             if($.isNumeric(data)){
		            	 supportFeePage += parseFloat(data);
		             } else {
		            	 supportFeePage += parseFloat(data.split(',').join(''));
		             }
		             i++;
		             data = aData[i];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
			             data = $(aData[i]).text();
		             }
		             if($.isNumeric(data)){
		            	 reviewFeePage += parseFloat(data);
		             } else {
		            	 reviewFeePage += parseFloat(data.split(',').join(''));
		             }
		             i++;
		             data = aData[i];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
			             data = $(aData[i]).text();
		             }
		             if($.isNumeric(data)){
		            	 recallCostPage += parseFloat(data);
		             } else {
		            	 recallCostPage += parseFloat(data.split(',').join(''));
		             }
		             i++;
		             data = aData[i];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
			             data = $(aData[i]).text();
		             }
		             if($.isNumeric(data)){
		            	 recallFeePage += parseFloat(data);
		             } else {
		            	 recallFeePage += parseFloat(data.split(',').join(''));
		             }
		             i++;
		             data = aData[i];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
			             data = $(aData[i]).text();
		             }
		             if($.isNumeric(data)){
		            	 expressFeePage += parseFloat(data);
		             } else {
		            	 expressFeePage += parseFloat(data.split(',').join(''));
		             }
		             i++;
		             data = aData[i];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
			             data = $(aData[i]).text();
		             }
		             if($.isNumeric(data)){
		            	 vineFeePage += parseFloat(data);
		             } else {
		            	 vineFeePage += parseFloat(data.split(',').join(''));
		             }

				     <c:if test="${'2' ne flag }">
			             i++;
			             data = aData[i];
			             if(!$.isNumeric(data) && data.indexOf('</') > 0){
				             data = $(aData[i]).text();
			             }
			             if($.isNumeric(data)){
			            	 storageFeePage += parseFloat(data);
			             } else {
			            	 storageFeePage += parseFloat(data.split(',').join(''));
			             }
			             i++;
			             data = aData[i];
			             if(!$.isNumeric(data) && data.indexOf('</') > 0){
				             data = $(aData[i]).text();
			             }
			             if($.isNumeric(data)){
			            	 longStorageFeePage += parseFloat(data);
			             } else {
			            	 longStorageFeePage += parseFloat(data.split(',').join(''));
			             }
			             $(".totalStorageFee").html(fmoney(storageFeePage,2));
			             $(".totalLongStorageFee").html(fmoney(longStorageFeePage,2));
			             i = i+2;
			             data = aData[i];
			             if(!$.isNumeric(data) && data.indexOf('</') > 0){
				             data = $(aData[i]).text();
			             }
			             if($.isNumeric(data)){
			            	 financialCostPage += parseFloat(data);
			             } else {
			            	 financialCostPage += parseFloat(data.split(',').join(''));
			             }
			             $(".totalfinancialCost").html(fmoney(financialCostPage,2));
		             </c:if>
		             $(".total").html(fmoney(qtyPage,0));
		             $(".totalSales").html(fmoney(salesPage,2));
		             
		             $(".totalPV").html(fmoney((parseInt(qtyPage)*100/parseInt(qty)).toFixed(2),2)+"%");
			         $(".totalPS").html(fmoney((parseFloat(salesPage)*100/parseFloat(sales)).toFixed(2),2)+"%");
			         
		             $(".totalSalesNoTax").html(fmoney(salesNoTaxPage,2));
		             $(".totalRefund").html(fmoney(refundPage,2));
		             $(".totalAmazonFee").html(fmoney(amazonFeePage,2));
		             <c:if test="${'1' eq groupType || '2' eq groupType }">
		             	$(".totalTransportFee").html(fmoney(transportFeePage,2));
		             	$(".totalBuyCost").html(fmoney(buyCostPage,2));
		             </c:if>
		             <%--$(".saleCost").html(fmoney(salesCostPage,2));--%>
		             $(".grossProfit").html(fmoney(grossProfitPage,2));
		             $(".grossProfitRatio").html(Math.round(fmoney((grossProfitPage*100/salesPage),2)) +"%");
		             $(".totalProfit").html(fmoney(profitPage,2));
		             if(profitRatioPage + 0.02 > 100){
		            	 $(".totalProfitRate").html("100%");
		             } else {
		            	 $(".totalProfitRate").html(fmoney(profitRatioPage,2)+"%");
		             }
		             $(".totalAvg").html(fmoney((profitPage/qtyPage),2));
		             $(".totalProfitRatio").html(Math.round(fmoney((profitPage*100/salesPage),2)) +"%");
		             $(".totalAvgSale").html(fmoney((salesPage/qtyPage),2));
		             $(".totalAdFee").html(fmoney(adFeePage,2));
		             $(".totalAdSalesVolume").html(fmoney(adQtyPage,0));
		             $(".totalAdSales").html(fmoney(adSalesPage,2));
		             $(".totalAdProfit").html(fmoney(adProfitPage,2));
		             $(".totalDealFee").html(fmoney(dealFeePage,2));
		             $(".totalDealSalesVolume").html(fmoney(dealQtyPage,0));
		             $(".totalDealProfit").html(fmoney(dealProfitPage,2));
		             $(".totalSupportFee").html(fmoney(supportFeePage,2));
		             $(".totalReviewFee").html(fmoney(reviewFeePage,2));
		             $(".totalRecallCost").html(fmoney(recallCostPage,2));
		             $(".totalRecallFee").html(fmoney(recallFeePage,2));
		             $(".totalExpressFee").html(fmoney(expressFeePage,2));
		             $(".totalVine").html(fmoney(vineFeePage,2));
		             return nRow;
		         },"fnPreDrawCallback": function( oSettings ) { 
		        	 $(".total").html(0);
			         $(".totalSales").html(0);
		             $(".totalSalesNoTax").html(0);
		             $(".totalRefund").html(0);
		             $(".totalAmazonFee").html(0);
		             <c:if test="${'2' ne flag }">
		             	$(".totalTransportFee").html(0);
		             	$(".totalBuyCost").html(0);
		             </c:if>
		             <%--$(".saleCost").html(0);--%>
		             $(".grossProfit").html(0);
		             $(".grossProfitRate").html(0);
		             $(".totalProfit").html(0);
		             $(".totalProfitRate").html(0);
		             $(".totalAvg").html(0);
		             $(".totalProfitRatio").html(0);
		             $(".totalAvgSale").html(0);
		             $(".totalAdFee").html(0);
		             $(".totalAdSalesVolume").html(0);
		             $(".totalAdSales").html(0);
		             $(".totalAdProfit").html(0);
		             $(".totalDealFee").html(0);
		             $(".totalDealSalesVolume").html(0);
		             $(".totalDealProfit").html(0);
		             $(".totalSupportFee").html(0);
		             $(".totalReviewFee").html(0);
		             $(".totalRecallCost").html(0);
		             $(".totalRecallFee").html(0);
		             <%--$(".totalTariff").html(0);--%>
		             $(".totalExpress").html(0);
		             $(".totalVine").html(0);
				     <c:if test="${'2' ne flag }">
			             $(".totalStorageFee").html(0);
			             $(".totalLongStorageFee").html(0);
			             $(".totalfinancialCost").html(0);
		             </c:if>
		         }  
		});

		 new FixedColumns( oTable,{
			 "iLeftColumns": 2,
	 		"iLeftWidth": 290
		 });
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
	
	function timeOnChangeMonth(obj){
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
		<li class="${empty saleProfit.country?'active':''}"><a class="countryHref" href="#" key="">总计</a></li>
		<li class="${'noUs' eq saleProfit.country?'active':''}"><a class="countryHref" href="#" key="noUs">总计(不含美国)</a></li>
		<li class="${'eu' eq saleProfit.country?'active':''}"><a class="countryHref" href="#" key="eu">欧洲</a></li>
		<li class="${'en' eq saleProfit.country?'active':''}"><a class="countryHref" href="#" key="en">英语国家</a></li>
		<li class="${'nonEn' eq saleProfit.country?'active':''}"><a class="countryHref" href="#" key="nonEn">非英语国家</a></li>
		<c:forEach items="${fns:getDictList('platform')}" var="dic">
			<c:if test="${dic.value ne 'com.unitek'}">
				<li class="${saleProfit.country eq dic.value ?'active':''}"><a class="countryHref" href="#" key="${dic.value}">${dic.label}</a></li>
			</c:if>
		</c:forEach>
	</ul>
	<form id="searchForm" action="${ctx}/amazoninfo/salesProfits/sales" method="post" class="breadcrumb form-search">
		<input type="hidden" id="country" name="country" value="${saleProfit.country}"></input>
		<input type="hidden" id="flag" name="flag" value="${flag}" />
		<ul class="nav nav-pills" style="width:270px;float:left;" id="myTab">
			<%--<li data-toggle="pills" id="showTab2"><a href="#" onclick="javaScript:searchType('2')">By Day</a></li> --%>
			<li data-toggle="pills" id="showTab1"><a href="#" onclick="javaScript:searchType('1')">By Month</a></li>
			<li data-toggle="pills" id="showTab3"><a href="#" onclick="javaScript:searchType('3')">By Year</a></li>
		</ul>
		<%--<c:if test="${'2' eq flag }">
			<label>统计日期：</label>
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){timeOnChange(this);return true}});" readonly="readonly"  class="Wdate" type="text" name="day" value="<fmt:formatDate value="${date}" pattern="yyyy-MM-dd" />" id="day" class="input-small"/>
			&nbsp;至&nbsp;
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM-dd',onpicked:function(){timeOnChange(this);return true}});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${date1}" pattern="yyyy-MM-dd" />" id="end" class="input-small"/>
		</c:if>--%>
		<c:if test="${'1' eq flag }">
			<label>统计月份：</label>
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM',onpicked:function(){timeOnChangeMonth(this);return true}});" readonly="readonly"  class="Wdate" type="text" name="day" value="<fmt:formatDate value="${date}" pattern="yyyy-MM" />" id="day" class="input-small"/>
			&nbsp;至&nbsp;
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy-MM',onpicked:function(){timeOnChangeMonth(this);return true}});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${date1}" pattern="yyyy-MM" />" id="end" class="input-small"/>
		</c:if>
		<c:if test="${'3' eq flag }">
			<label>统计年度：</label>
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy',onpicked:function(){timeOnChange(this);return true}});" readonly="readonly"  class="Wdate" type="text" name="day" value="<fmt:formatDate value="${date}" pattern="yyyy" />" id="day" class="input-small"/>
			&nbsp;至&nbsp;
			<input style="width: 100px" onclick="WdatePicker({isShowClear:true,dateFmt:'yyyy',onpicked:function(){timeOnChange(this);return true}});" readonly="readonly"  class="Wdate" type="text" name="end" value="<fmt:formatDate value="${date1}" pattern="yyyy" />" id="end" class="input-small"/>
		</c:if>
		&nbsp;&nbsp;
		<label>统计类型：</label>
		<select id="groupType" name="groupType" onchange="changeType()">
			<option value="0">产品</option>
			<option value="1" ${'1' eq groupType?'selected':'' }>产品类型</option>
			<option value="2" ${'2' eq groupType?'selected':'' }>产品线</option>
		</select>
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="查询"/>
		&nbsp;&nbsp;<input id="export" class="btn btn-primary" type="button" value="导出"/>
	</form>
	<div class="alert">
	  	<button type="button" class="close" data-dismiss="alert">&times;</button>
	  	<strong>Tips:所有货币单位统一为欧元(€)</strong> 
	</div>
	<tags:message content="${message}"/>
	<%--<div id="contentTbDiv">
		<div style="overflow-x:auto"> --%>
			<table id="contentTable" class="table table-striped table-bordered table-condensed">
				<thead>
					<tr>
						<c:if test="${'2' eq flag }">
							<th>No.</th>
						</c:if>
						<c:if test="${'2' ne flag }">
							<th>日期</th>
						</c:if>
						<th>
							<c:if test="${'1' ne groupType && '2' ne groupType}"><div style="width:200px">产品名称</div></c:if>
							<c:if test="${'1' eq groupType }"><div style="width:180px">产品类型</div></c:if>
							<c:if test="${'2' eq groupType }">产品线</c:if>
						</th>
						<th>销量</th>
						<th>销售额<br/>(不含邮费)</th>
						<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="占选取时间总销量">销量<br/>占比</a></th>
						<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="占选取时间总销量额">销售额<br/>占比</a></th>
						<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="扣除增值税后的收入">税后收入<br/>(含邮费)</a></th>
						<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="包括退货和退款金额">退款</a></th>
						<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="含亚马逊FBAPerUnitFulfillmentFee、FBAWeightBasedFee、Commission">亚马逊费用</a></th>
						<c:if test="${'1' eq groupType || '2' eq groupType }">
							<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="加权平均运费">运输费</a></th>
							<th>采购成本</th>
						</c:if>
						<%--<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="销售额的10%">运营成本</a></th> --%>
						<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="未扣运营成本">毛利润</a></th>
						<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="未扣运营成本">毛利率</a></th>
						<th>利润</th>
						<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="毛利润">利润占比</a></th>
						<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="毛利润">单个利润</a></th>
						<th>利润率</th>
						<th>平均售价</th>
						<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="费用大于0表示返点收益">广告费用</a></th>
						<th>广告销量</th>
						<th>广告销售额</th>
						<th>广告盈亏</th>
						<th>闪促费用</th>
						<th>闪促销量</th>
						<th>闪促盈亏</th>
						<th>替代费用</th>
						<th>评测费用</th>
						<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="包含采购成本、运费和关税">销毁召回损失成本</a></th>
						<th>销毁召回费用</th>
						<th>快递费</th>
						<th>Vine费用</th>
						<c:if test="${'2' ne flag }">
							<th>月仓储费</th>
							<th>长期仓储费</th>
							<th>库存周转率</th>
							<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" data-content="(采购+运输)/周转率*0.15/12">财务成本</a></th>
						</c:if>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${list}" var="saleProfit" varStatus="i">
						<tr>
							<c:if test="${'2' eq flag }">
								<td>${i.count}</td>
							</c:if>
							<c:if test="${'2' ne flag }">
								<td>${saleProfit.day}</td>
							</c:if>
							
							<td>
								<c:if test="${empty groupType || '0' eq groupType }">
									<a target="_blank" href="${ctx}/psi/psiInventory/productInfoDetail?productName=${saleProfit.productName}">${saleProfit.productName}</a>
								</c:if>
								<c:if test="${'1' eq groupType }">${saleProfit.productName}</c:if>
								<c:if test="${'2' ne groupType }">(${saleProfit.line}线 &nbsp;${not empty saleProfit.productAttr&&'1' ne groupType?saleProfit.productAttr:"" })</c:if>
								<c:if test="${'2' eq groupType }">${saleProfit.line}线</c:if>
							</td>
							<td>
								<fmt:formatNumber value="${saleProfit.salesVolume}" maxFractionDigits="0" />
							</td>
							
							<td>
								<c:if test="${'2' ne flag }">
									<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" 
										data-content="库存周转率:<fmt:formatNumber value="${empty turnoverRate[saleProfit.day][saleProfit.productName]?0:turnoverRate[saleProfit.day][saleProfit.productName]}" maxFractionDigits="2" />">
										<fmt:formatNumber value="${saleProfit.sales}" maxFractionDigits="2" />
									</a>
								</c:if>
								<c:if test="${'2' eq flag }">
									<fmt:formatNumber value="${saleProfit.sales}" maxFractionDigits="2" />
								</c:if>
							</td>
							<td></td>
							<td></td>
							<td>
								<fmt:formatNumber value="${saleProfit.salesNoTax}" maxFractionDigits="2" /><%-- 税后收入 --%>
							</td>
							<td><fmt:formatNumber value="${saleProfit.refund}" maxFractionDigits="2" /></td><!-- 退款 -->
							<td><fmt:formatNumber value="${saleProfit.amazonFee}" maxFractionDigits="2" /></td><!-- 亚马逊佣金 -->
							<c:if test="${'1' eq groupType || '2' eq groupType }">
								<td><fmt:formatNumber value="${-saleProfit.transportFee}" maxFractionDigits="2" /></td><!-- 运输费 -->
								<td><fmt:formatNumber value="${-saleProfit.buyCost}" maxFractionDigits="2" /></td><!-- 采购成本 -->
							</c:if>
							<%--<td><fmt:formatNumber value="${saleProfit.sales*0.1}" maxFractionDigits="2" /></td><!-- 运营成本 --> --%>
							<!-- 周转率/财务成本/毛利润 -->
							<c:set var="turnover" value="${empty turnoverRate[saleProfit.day][saleProfit.productName]?0:turnoverRate[saleProfit.day][saleProfit.productName]}"></c:set>
							<c:set var="caiwuCost" value="0"></c:set>
							<c:set var="profits" value="${saleProfit.profits}"></c:set>
							<c:if test="${turnover>0 }">
								<c:set var="caiwuCost" value="${(-saleProfit.transportFee-saleProfit.buyCost)/turnover*0.15/12}"></c:set>
								<c:if test="${'3' eq flag }">
									<c:set var="caiwuCost" value="${(-saleProfit.transportFee-saleProfit.buyCost)/turnover*0.15}"></c:set>
								</c:if>
							</c:if>
							<td><fmt:formatNumber value="${profits}" maxFractionDigits="2" /></td><!-- 毛利润 -->
							<!-- 毛利率 -->
							<td>
								<c:if test="${saleProfit.sales>0}">
									<fmt:formatNumber value="${profits/saleProfit.sales*100}" maxFractionDigits="0" />%
								</c:if>
								<c:if test="${saleProfit.salesVolume<=0}">
									0%
								</c:if>
							</td>
							<c:set var="profits" value="${profits-saleProfit.sales*0.1}"></c:set>
							<td>
								<fmt:formatNumber value="${profits}" maxFractionDigits="2" /><%-- 利润(扣除运营成本) --%>
							</td>
							<!-- 利润占比 -->
							<td></td>
							<!-- 单个利润 -->
							<td>
								<c:if test="${saleProfit.salesVolume>0}">
									<fmt:formatNumber value="${saleProfit.profits/saleProfit.salesVolume}" maxFractionDigits="2" />
								</c:if>
								<c:if test="${saleProfit.salesVolume<=0}">
									0
								</c:if>
							</td>
							<!-- 利润率 -->
							<td>
								<c:if test="${saleProfit.sales>0}">
									<fmt:formatNumber value="${profits/saleProfit.sales*100}" maxFractionDigits="0" />%
								</c:if>
								<c:if test="${saleProfit.salesVolume<=0}">
									0%
								</c:if>
							</td>
							<!-- 平均售价 -->
							<td>
								<c:if test="${saleProfit.salesVolume>0}">
									<fmt:formatNumber value="${saleProfit.sales/saleProfit.salesVolume}" maxFractionDigits="2" />
								</c:if>
								<c:if test="${saleProfit.salesVolume<=0}">
									0
								</c:if>
							</td>
							<td>
								<c:set var="cpo" value="0"/>
								<c:if test="${saleProfit.adInEventSalesVolume+saleProfit.adInProfitSalesVolume+saleProfit.adOutEventSalesVolume+saleProfit.adOutProfitSalesVolume+saleProfit.adAmsSalesVolume>0 }">
									<c:set var="cpo" value="${(saleProfit.adOutEventFee+saleProfit.adOutProfitFee+saleProfit.adInEventFee+saleProfit.adInProfitFee+saleProfit.adAmsFee)/(saleProfit.adInEventSalesVolume+saleProfit.adInProfitSalesVolume+saleProfit.adOutEventSalesVolume+saleProfit.adOutProfitSalesVolume+saleProfit.adAmsSalesVolume) }"/>
								</c:if>
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" 
									data-content="
										CPO:<fmt:formatNumber value="${cpo }" maxFractionDigits="2"></fmt:formatNumber><br/>
										站内(market):<fmt:formatNumber value="${saleProfit.adInProfitFee }"></fmt:formatNumber><br/>
										站内(sales):<fmt:formatNumber value="${saleProfit.adInEventFee }"></fmt:formatNumber><br/>
										站外(market):<fmt:formatNumber value="${saleProfit.adOutProfitFee }"></fmt:formatNumber><br/>
										站外(sales):<fmt:formatNumber value="${saleProfit.adOutEventFee}"></fmt:formatNumber><br/>
										AMS:<fmt:formatNumber value="${saleProfit.adAmsFee}"></fmt:formatNumber>">
									<fmt:formatNumber value="${saleProfit.adOutEventFee+saleProfit.adOutProfitFee+saleProfit.adInEventFee+saleProfit.adInProfitFee+saleProfit.adAmsFee}" maxFractionDigits="2" />
								</a>
							</td>
							<td>
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" 
									data-content="
										站内(market):<fmt:formatNumber value="${saleProfit.adInProfitSalesVolume }"></fmt:formatNumber><br/>
										站内(sales):<fmt:formatNumber value="${saleProfit.adInEventSalesVolume }"></fmt:formatNumber><br/>
										站外(market):<fmt:formatNumber value="${saleProfit.adOutProfitSalesVolume }"></fmt:formatNumber><br/>
										站外(sales):<fmt:formatNumber value="${saleProfit.adOutEventSalesVolume}"></fmt:formatNumber><br/>
										AMS:<fmt:formatNumber value="${saleProfit.adAmsSalesVolume}"></fmt:formatNumber>">
									<fmt:formatNumber value="${saleProfit.adInEventSalesVolume+saleProfit.adInProfitSalesVolume+saleProfit.adOutEventSalesVolume+saleProfit.adOutProfitSalesVolume+saleProfit.adAmsSalesVolume}" maxFractionDigits="2" />
								</a>
							</td>
							<td>
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" 
									data-content="
										站内(market):<fmt:formatNumber value="${saleProfit.adInProfitSales }"></fmt:formatNumber><br/>
										站内(sales):<fmt:formatNumber value="${saleProfit.adInEventSales }"></fmt:formatNumber><br/>
										站外(market):<fmt:formatNumber value="${saleProfit.adOutProfitSales }"></fmt:formatNumber><br/>
										站外(sales):<fmt:formatNumber value="${saleProfit.adOutEventSales}"></fmt:formatNumber><br/>
										AMS:<fmt:formatNumber value="${saleProfit.adAmsSales}"></fmt:formatNumber>">
									<fmt:formatNumber value="${saleProfit.adInEventSales+saleProfit.adInProfitSales+saleProfit.adOutEventSales+saleProfit.adOutProfitSales+saleProfit.adAmsSales}" maxFractionDigits="2" />
								</a>
							</td>
							<!-- 广告盈亏 -->
							<td>
								<fmt:formatNumber value="${saleProfit.adProfit}" maxFractionDigits="2" />
							</td>
							<!-- 闪促费用 -->
							<td>
								<fmt:formatNumber value="${saleProfit.dealFee}" maxFractionDigits="2" />
							</td>
							<!-- 闪促销量 -->
							<td>
								<fmt:formatNumber value="${saleProfit.dealSalesVolume}" maxFractionDigits="2" />
							</td>
							<!-- 闪促盈亏 -->
							<td>
								<fmt:formatNumber value="${saleProfit.dealProfit}" maxFractionDigits="2" />
							</td>
							<td>
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" 
									data-content="
										数量:<fmt:formatNumber value="${saleProfit.supportNum }"></fmt:formatNumber><br/>
										成本:<fmt:formatNumber value="${-saleProfit.supportCost }"></fmt:formatNumber><br/>
										亚马逊费用:<fmt:formatNumber value="${saleProfit.supportAmazonFee}"></fmt:formatNumber>">
									<fmt:formatNumber value="${saleProfit.supportAmazonFee-saleProfit.supportCost}" maxFractionDigits="2" />
								</a>
							</td>
							<td>
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" 
									data-content="
										数量:<fmt:formatNumber value="${saleProfit.reviewNum }"></fmt:formatNumber><br/>
										成本:<fmt:formatNumber value="${-saleProfit.reviewCost }"></fmt:formatNumber><br/>
										亚马逊费用:<fmt:formatNumber value="${saleProfit.reviewAmazonFee}"></fmt:formatNumber>">
									<fmt:formatNumber value="${saleProfit.reviewAmazonFee-saleProfit.reviewCost}" maxFractionDigits="2" />
								</a>
							</td>
							<td>
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" 
									data-content="
										召回数量:<fmt:formatNumber value="${saleProfit.recallNum }"></fmt:formatNumber>">
									<fmt:formatNumber value="${-saleProfit.recallCost}" maxFractionDigits="2" />
								</a>
							</td>
							<td>
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" 
									data-content="
										召回数量:<fmt:formatNumber value="${saleProfit.recallNum }"></fmt:formatNumber>">
									<fmt:formatNumber value="${-saleProfit.recallFee}" maxFractionDigits="2" />
								</a>
							</td>
							<td>
								<fmt:formatNumber value="${-saleProfit.expressFee}" maxFractionDigits="2" />
							</td>
							<td>
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" 
									data-content="
										数量:<fmt:formatNumber value="${saleProfit.vineNum }"></fmt:formatNumber><br/>
										成本:<fmt:formatNumber value="${-saleProfit.vineCost }"></fmt:formatNumber><br/>
										费用:<fmt:formatNumber value="${-saleProfit.vineFee}"></fmt:formatNumber>">
									<fmt:formatNumber value="${-saleProfit.vineFee-saleProfit.vineCost}" maxFractionDigits="2" />
								</a>
							</td>
							<c:if test="${'2' ne flag }">
								<!-- 月仓储费 -->
								<td>
									<fmt:formatNumber value="${saleProfit.storageFee}" maxFractionDigits="2" />
								</td>
								<td>
									<fmt:formatNumber value="${saleProfit.longStorageFee}" maxFractionDigits="2" />
								</td>
								<c:set var="turnover" value="${empty turnoverRate[saleProfit.day][saleProfit.productName]?0:turnoverRate[saleProfit.day][saleProfit.productName]}"></c:set>
								<td>
									<fmt:formatNumber value="${turnover}" maxFractionDigits="2" />
								</td>
								<td>
									<c:choose>
										<c:when test="${turnover>0 }">
											<fmt:formatNumber value="${caiwuCost}" maxFractionDigits="2" />
										</c:when>
										<c:otherwise>
											0
										</c:otherwise>
									</c:choose>
								</td>
							</c:if>
						</tr>
					</c:forEach>
				</tbody>
				<tfoot>
					<tr >
						<td style="font-weight: bold;">Page Total</td>
						<td></td>
						<td id="total" class="total"></td>
						<td id="totalSales" class="totalSales"></td>
						<td id="totalPV" class='totalPV'></td>
						<td id="totalPS" class="totalPS"></td>
							
						<td id="totalSalesNoTax" class="totalSalesNoTax"></td>
						<td id="totalRefund" class="totalRefund"></td>
						<td id="totalAmazonFee" class="totalAmazonFee"></td>
						<c:if test="${'1' eq groupType || '2' eq groupType }">
							<td id="totalTransportFee" class="totalTransportFee"></td>
							<td id="totalBuyCost" class="totalBuyCost"></td>
						</c:if>
						<%--<td id="saleCost" class="saleCost"></td> --%>
						<td id="grossProfit" class="grossProfit"></td>
						<td id="grossProfitRatio" class="grossProfitRatio"></td>
						<td id="totalProfit" class="totalProfit"></td>
						<td id="totalProfitRate" class="totalProfitRate"></td>
						<td id="totalAvg" class="totalAvg"></td>
						<td id="totalProfitRatio" class="totalProfitRatio"></td>
						<td id="totalAvgSale" class="totalAvgSale"></td>
						<td id="totalAdFee" class="totalAdFee"></td>
						<td id="totalAdSalesVolume" class="totalAdSalesVolume"></td>
						<td id="totalAdSales" class="totalAdSales"></td>
						<td id="totalAdProfit" class="totalAdProfit"></td>
						<td id="totalDealFee" class="totalDealFee"></td>
						<td id="totalDealSalesVolume" class="totalDealSalesVolume"></td>
						<td id="totalDealProfit" class="totalDealProfit"></td>
						<td id="totalSupportFee" class="totalSupportFee"></td>
						<td id="totalReviewFee" class="totalReviewFee"></td>
						<td id="totalRecallCost" class="totalRecallCost"></td>
						<td id="totalRecallFee" class="totalRecallFee"></td>
						<td id="totalExpressFee" class="totalExpressFee"></td>
						<td id="totalVine" class="totalVine"></td>
						<c:if test="${'2' ne flag }">
							<td id="totalStorageFee" class="totalStorageFee"></td>
							<td id="totalLongStorageFee" class="totalLongStorageFee"></td>
							<td id="totalTurnoverRate" class="totalTurnoverRate"></td>
							<td id="totalfinancialCost" class="totalfinancialCost"></td>
						</c:if>
					</tr>
					<tr id = "totalTr">
						<td style="font-size: 18px; font-weight: bold;">Total</td>
						<td></td>
						<td></td>
						<td></td>
						<td id="allPV"></td>
						<td id="allPS"></td>
						<td></td>
						<td></td>
						<td></td>
						<c:if test="${'1' eq groupType || '2' eq groupType }">
							<td></td>
							<td></td>
						</c:if>
						<%--<td></td>--%>
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
						<c:if test="${'2' ne flag }">
							<td></td>
							<td></td>
							<td>
								<fmt:formatNumber value="${turnoverRate['total']['total']}" maxFractionDigits="2" />
							</td>
							<td></td>
						</c:if>
					</tr>
					<tr id = "rateTr">
						<td>列合计/销售额</td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<td></td>
						<c:if test="${'1' eq groupType || '2' eq groupType }">
							<td></td>
							<td></td>
						</c:if>
						<%--<td></td>--%>
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
						<c:if test="${'2' ne flag }">
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</c:if>
					</tr>
					
				</tfoot>
			</table>
		<%--</div>
	</div> --%>
</body>
</html>
