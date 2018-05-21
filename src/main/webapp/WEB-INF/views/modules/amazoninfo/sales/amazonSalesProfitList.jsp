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
			 $("#searchForm").attr("action","${ctx}/amazoninfo/salesProfits/export");
			 $("#searchForm").submit();
			 $("#searchForm").attr("action","${ctx}/amazoninfo/salesProfits");
		 });
	
		
		var arr = $("#contentTable tbody tr");
		var num1 = 0;
		var num2 = 0;
		var num3 = 0;
		var num4 = 0;
		var num5 = 0;
		var num6 = 0;
		var num7 = 0;
		var num8 = 0;
		var num9 = 0;
		var num10 = 0;
		var num11 = 0;	//广告费用
		var num12 = 0;	//广告销量
		var num13 = 0;	//广告销售额
		var num14 = 0;	//广告盈亏
		var num15 = 0;	//闪促费用
		var num16 = 0;	//闪促销量
		var num17 = 0;	//闪促盈亏
		var num18 = 0;	//替代货费用
		var num19 = 0;	//评测单费用
		var num20 = 0;	//召回单成本
		var num21 = 0;	//召回单费用
		var num22 = 0;	//关税
		var num23 = 0;	//仓储费
		var num24 = 0;	//长期仓储费
		var num25 = 0;	//快递费
		var num26 = 0;	//vine项目费
		
		arr.each(function() {
			
			if($(this).find("td :eq(2)").text())
			num1 += parseInt($(this).find("td :eq(2)").text().split(',').join(''));
			if($(this).find("td :eq(3)").text())
			num2 += parseFloat($(this).find("td :eq(3)").text().split(',').join(''));
			if($(this).find("td :eq(6)").text())
			num3 += parseFloat($(this).find("td :eq(6)").text().split(',').join(''));
			
			if($(this).find("td :eq(7)").text())
			num4 += parseFloat($(this).find("td :eq(7)").text().split(',').join(''));
			if($(this).find("td :eq(8)").text())
			num5 += parseFloat($(this).find("td :eq(8)").text().split(',').join(''));
			if($(this).find("td :eq(9)").text())
			num6 += parseFloat($(this).find("td :eq(9)").text().split(',').join(''));
			if($(this).find("td :eq(10)").text())
			num7 += parseFloat($(this).find("td :eq(10)").text().split(',').join(''));
			num8 += parseFloat($(this).find("td :eq(11)").text().split(',').join(''));
			num9 += parseFloat($(this).find("td :eq(12)").text().split(',').join(''));
			num10 += parseFloat($(this).find("td :eq(13)").text().split(',').join(''));
			num11+= parseFloat($(this).find("td :eq(17)").text().split(',').join(''));
			num12+= parseInt($(this).find("td :eq(18)").text().split(',').join(''));
			num13+= parseFloat($(this).find("td :eq(19)").text().split(',').join(''));
			num14+= parseFloat($(this).find("td :eq(20)").text().split(',').join(''));
			num15+= parseFloat($(this).find("td :eq(21)").text().split(',').join(''));
			num16+= parseFloat($(this).find("td :eq(22)").text().split(',').join(''));
			num17+= parseFloat($(this).find("td :eq(23)").text().split(',').join(''));
			num18+= parseFloat($(this).find("td :eq(24)").text().split(',').join(''));
			num19+= parseFloat($(this).find("td :eq(25)").text().split(',').join(''));
			num20+= parseFloat($(this).find("td :eq(26)").text().split(',').join(''));
			num21+= parseFloat($(this).find("td :eq(27)").text().split(',').join(''));
			num22+= parseFloat($(this).find("td :eq(28)").text().split(',').join(''));
			num25+= parseFloat($(this).find("td :eq(29)").text().split(',').join(''));
			num26+= parseFloat($(this).find("td :eq(30)").text().split(',').join(''));
			<c:if test="${'2' ne flag }">
				num23+= parseFloat($(this).find("td :eq(31)").text().split(',').join(''));
				num24+= parseFloat($(this).find("td :eq(32)").text().split(',').join(''));
			</c:if>
		});

		var tr = $("#contentTable tfoot tr#totalTr");
		tr.find("td :eq(2)").text(fmoney(num1.toFixed(0),0));
		tr.find("td :eq(3)").text(fmoney(num2.toFixed(2),2));
		
		
		var totalV=0;
		var totalS=0;
		arr.each(function() {
			   if($(this).find("td :eq(2)").text()){
				   var tempV=parseInt($(this).find("td :eq(2)").text().split(",").join(''));	//销量
				   totalV += tempV;
				   $(this).find("td :eq(4)").text(fmoney((parseInt(tempV)*100/parseInt(num1)).toFixed(2),2)+"%");
			   }
				
			   if($(this).find("td :eq(3)").text()){
					var tempS=parseFloat($(this).find("td :eq(3)").text().split(",").join(''));	//销售额
					totalS += tempS;
					$(this).find("td :eq(5)").text(fmoney((parseFloat(tempS)*100/parseFloat(num2)).toFixed(2),2)+"%");
			   }
		});
		$("#allPV").html(fmoney((parseInt(totalV)*100/parseInt(num1)).toFixed(2),2)+"%");
	    $("#allPS").html(fmoney((parseFloat(totalS)*100/parseFloat(num2)).toFixed(2),2)+"%");
		
		
		tr.find("td :eq(6)").text(fmoney(num3.toFixed(2),2));
		tr.find("td :eq(7)").text(fmoney(num4.toFixed(2),2));
		tr.find("td :eq(8)").text(fmoney(num5.toFixed(2),2));
		tr.find("td :eq(9)").text(fmoney(num6.toFixed(2),2));
		tr.find("td :eq(10)").text(fmoney(num7.toFixed(2),2));
		tr.find("td :eq(11)").text(fmoney(num8.toFixed(2),2));
		tr.find("td :eq(12)").text(fmoney(num9.toFixed(2),2));
		tr.find("td :eq(14)").text(fmoney((num9/num1).toFixed(2),2));
		tr.find("td :eq(15)").text(fmoney((num9*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(16)").text(fmoney((num2/num1).toFixed(2),2));
		tr.find("td :eq(17)").text(fmoney(num11.toFixed(2),2));
		tr.find("td :eq(18)").text(fmoney(num12.toFixed(0),0));
		tr.find("td :eq(19)").text(fmoney(num13.toFixed(2),2));
		tr.find("td :eq(20)").text(fmoney(num14.toFixed(2),2));
		tr.find("td :eq(21)").text(fmoney(num15.toFixed(2),2));
		tr.find("td :eq(22)").text(fmoney(num16.toFixed(0),0));
		tr.find("td :eq(23)").text(fmoney(num17.toFixed(2),2));
		tr.find("td :eq(24)").text(fmoney(num18.toFixed(2),2));
		tr.find("td :eq(25)").text(fmoney(num19.toFixed(2),2));
		tr.find("td :eq(26)").text(fmoney(num20.toFixed(2),2));
		tr.find("td :eq(27)").text(fmoney(num21.toFixed(2),2));
		tr.find("td :eq(28)").text(fmoney(num22.toFixed(2),2));
		tr.find("td :eq(29)").text(fmoney(num25.toFixed(2),2));
		tr.find("td :eq(30)").text(fmoney(num26.toFixed(2),2));
		<c:if test="${'2' ne flag }">
			tr.find("td :eq(31)").text(fmoney(num23.toFixed(2),2));
			tr.find("td :eq(32)").text(fmoney(num24.toFixed(2),2));
			var totalRate = '${turnoverRate['total']['total']}';
			var cCost = fmoney(((num7+num8)/totalRate*0.15/12).toFixed(2),2);
			<c:if test="${'3' eq flag }">
				cCost = fmoney(((num7+num8)/totalRate*0.15).toFixed(2),2);
			</c:if>
			var pRate = fmoney(((num9+parseFloat(cCost.split(',').join('')))*100/num2).toFixed(2),1);
			var text = '<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" '+
			'data-content="扣除财务成本后利润率:'+pRate+'%">'+cCost+'</a>';
			if(totalRate!=null && totalRate>0){
				tr.find("td :eq(34)").html(text);
			}
		</c:if>
		
		arr.each(function() {
			$(this).find("td :eq(13)").text((parseFloat($(this).find("td :eq(12)").text().split(',').join(''))*100/num9).toFixed(2)+'%');
		});

		tr = $("#contentTable tfoot tr#rateTr");
		//tr.find("td :eq(2)").text(fmoney(num1.toFixed(0),0));
		//tr.find("td :eq(3)").text(fmoney(num2.toFixed(2),2));
		//<c:if test="${not empty totalSales && totalSales > 0}">
			var totalSales = '${totalSales}';
			tr.find("td :eq(3)").text(fmoney((num2*100/totalSales).toFixed(2),2) + "%");
		//</c:if>
		tr.find("td :eq(6)").text(fmoney((num3*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(7)").text(fmoney((num4*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(8)").text(fmoney((num5*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(9)").text(fmoney((num6*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(10)").text(fmoney((num7*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(11)").text(fmoney((num8*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(12)").text(fmoney((num9*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(17)").text(fmoney((num11*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(18)").text(fmoney((num12*100/num1).toFixed(2),2) + "%");
		tr.find("td :eq(19)").text(fmoney((num13*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(20)").text(fmoney((num14*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(21)").text(fmoney((num15*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(23)").text(fmoney((num17*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(24)").text(fmoney((num18*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(25)").text(fmoney((num19*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(26)").text(fmoney((num20*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(27)").text(fmoney((num21*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(28)").text(fmoney((num22*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(29)").text(fmoney((num25*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(30)").text(fmoney((num26*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(31)").text(fmoney((num23*100/num2).toFixed(2),2) + "%");
		tr.find("td :eq(32)").text(fmoney((num24*100/num2).toFixed(2),2) + "%");
		
		
		
		$("a[rel='popover']").popover({trigger:'hover',container: 'body'});
		
		var oTable = $("#contentTable").dataTable({
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
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
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
							     { "sSortDataType":"dom-html1", "sType":"numeric" },
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
				"ordering" : true
				,"fnRowCallback": function( nRow, aData, iDisplayIndex, iDisplayIndexFull ) {
		             if(iDisplayIndex==0){
		            	 addd=0;
		            	 addd1=0;
		            	 addd2=0;
		            	 addd3=0;
		            	 addd4=0;
		            	 addd5=0;
		            	 addd6=0;
		            	 addd7=0;
		            	 addd8=0;
		            	 addd9=0;
		            	 addd10=0;
		            	 addd11=0;
		            	 addd12=0;
		            	 addd13=0;
		            	 addd14=0;
		            	 addd15=0;
		            	 addd16=0;
		            	 addd17=0;
		            	 addd18=0;
		            	 addd19=0;
		            	 addd20=0;
		            	 addd21=0;
		            	 addd22=0;
		            	 addd23=0;
		            	 addd24=0;
		            	 addd25=0;
		             }
		             if($.isNumeric(aData[2])){
		            	 addd += parseFloat(aData[2]);//第几列
		             }else{
		            	 addd += parseFloat((aData[2]).split(',').join(''));//第几列
					 }
		             var saleData = aData[3];
		             if(!$.isNumeric(saleData) && saleData.indexOf('</') > 0){
		            	 saleData = $(aData[3]).text();
		             }
		             if($.isNumeric(saleData)){
		            	 addd1 += parseFloat(saleData);
		             } else {
		            	 addd1 += parseFloat(saleData.split(',').join(''));
		             }
		             if($.isNumeric(aData[6])){
		            	 addd2 += parseFloat(aData[6]);//第几列
		             } else {
		            	 addd2 += parseFloat(aData[6].split(',').join(''));//第几列
		             }
		             if($.isNumeric(aData[7])){
		            	 addd3 += parseFloat(aData[7]);//第几列
		             } else {
		            	 addd3 += parseFloat(aData[7].split(',').join(''));//第几列
		             }
		             if($.isNumeric(aData[8])){
		            	 addd4 += parseFloat(aData[8]);//第几列
		             } else {
		            	 addd4 += parseFloat(aData[8].split(',').join(''));//第几列
		             }
		             if($.isNumeric(aData[9])){
		            	 addd5 += parseFloat(aData[9]);//第几列
		             } else {
		            	 addd5 += parseFloat(aData[9].split(',').join(''));//第几列
		             }
		             if($.isNumeric(aData[10])){
		            	 addd6 += parseFloat(aData[10]);//第几列
		             } else {
		            	 addd6 += parseFloat(aData[10].split(',').join(''));//第几列
		             }
		             var data = aData[11];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
			             data = $(aData[11]).text();
		             }
		             if($.isNumeric(data)){
		            	 addd7 += parseFloat(data);
		             } else {
		            	 addd7 += parseFloat(data.split(',').join(''));
		             }
		             if($.isNumeric(aData[12])){
		            	 addd8 += parseFloat(aData[12]);//第几列
		             } else {
		            	 addd8 += parseFloat(aData[12].split(',').join(''));//第几列
		             }
		             if($.isNumeric(aData[13])){
		            	 addd9 += parseFloat(aData[13]);//第几列
		             } else {
		            	 addd9 += parseFloat(aData[13].split(',').join(''));//第几列
		             }
		             data = aData[17];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
			             data = $(aData[17]).text();
		             }
		             if($.isNumeric(data)){
		            	 addd10 += parseFloat(data);
		             } else {
		            	 addd10 += parseFloat(data.split(',').join(''));
		             }
		             data = aData[18];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
			             data = $(aData[18]).text();
		             }
		             if($.isNumeric(data)){
		            	 addd11 += parseFloat(data);
		             } else {
		            	 addd11 += parseFloat(data.split(',').join(''));
		             }
		             data = aData[19];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
			             data = $(aData[19]).text();
		             }
		             if($.isNumeric(data)){
		            	 addd12 += parseFloat(data);
		             } else {
		            	 addd12 += parseFloat(data.split(',').join(''));
		             }
		             data = aData[20];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
			             data = $(aData[20]).text();
		             }
		             if($.isNumeric(data)){
		            	 addd13 += parseFloat(data);
		             } else {
		            	 addd13 += parseFloat(data.split(',').join(''));
		             }
		             data = aData[21];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
		            	 data = $(aData[21]).text();
		             }
		             if($.isNumeric(data)){
		            	 addd14 += parseFloat(data);
		             } else {
		            	 addd14 += parseFloat(data.split(',').join(''));
		             }
		             data = aData[22];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
		            	 data = $(aData[22]).text();
		             }
		             if($.isNumeric(data)){
		            	 addd15 += parseFloat(data);
		             } else {
		            	 addd15 += parseFloat(data.split(',').join(''));
		             }
		             data = aData[23];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
		            	 data = $(aData[23]).text();
		             }
		             if($.isNumeric(data)){
		            	 addd16 += parseFloat(data);
		             } else {
		            	 addd16 += parseFloat(data.split(',').join(''));
		             }
		             data = aData[24];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
		            	 data = $(aData[24]).text();
		             }
		             if($.isNumeric(data)){
		            	 addd17 += parseFloat(data);
		             } else {
		            	 addd17 += parseFloat(data.split(',').join(''));
		             }
		             data = aData[25];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
		            	 data = $(aData[25]).text();
		             }
		             if($.isNumeric(data)){
		            	 addd18 += parseFloat(data);
		             } else {
		            	 addd18 += parseFloat(data.split(',').join(''));
		             }
		             data = aData[26];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
		            	 data = $(aData[26]).text();
		             }
		             if($.isNumeric(data)){
		            	 addd19 += parseFloat(data);
		             } else {
		            	 addd19 += parseFloat(data.split(',').join(''));
		             }
		             data = aData[27];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
		            	 data = $(aData[27]).text();
		             }
		             if($.isNumeric(data)){
		            	 addd20 += parseFloat(data);
		             } else {
		            	 addd20 += parseFloat(data.split(',').join(''));
		             }
		             data = aData[28];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
		            	 data = $(aData[28]).text();
		             }
		             if($.isNumeric(data)){
		            	 addd21 += parseFloat(data);
		             } else {
		            	 addd21 += parseFloat(data.split(',').join(''));
		             }
		             data = aData[29];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
		            	 data = $(aData[29]).text();
		             }
		             if($.isNumeric(data)){
		            	 addd24 += parseFloat(data);
		             } else {
		            	 addd24 += parseFloat(data.split(',').join(''));
		             }
		             data = aData[30];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
		            	 data = $(aData[30]).text();
		             }
		             if($.isNumeric(data)){
		            	 addd25 += parseFloat(data);
		             } else {
		            	 addd25 += parseFloat(data.split(',').join(''));
		             }

				     <c:if test="${'2' ne flag }">
		             data = aData[31];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
		            	 data = $(aData[31]).text();
		             }
		             if($.isNumeric(data)){
		            	 addd22 += parseFloat(data);
		             } else {
		            	 addd22 += parseFloat(data.split(',').join(''));
		             }
		             data = aData[32];
		             if(!$.isNumeric(data) && data.indexOf('</') > 0){
		            	 data = $(aData[32]).text();
		             }
		             if($.isNumeric(data)){
		            	 addd23 += parseFloat(data);
		             } else {
		            	 addd23 += parseFloat(data.split(',').join(''));
		             }
		             $(".totalStorageFee").html(fmoney(addd22,2));
		             $(".totalLongStorageFee").html(fmoney(addd23,2));
		             </c:if>
		             $(".total").html(fmoney(addd,0));
		             $(".totalSales").html(fmoney(addd1,2));
		             
		             $(".totalPV").html(fmoney((parseInt(addd)*100/parseInt(num1)).toFixed(2),2)+"%");
			         $(".totalPS").html(fmoney((parseFloat(addd1)*100/parseFloat(num2)).toFixed(2),2)+"%");
			         
		             $(".totalSalesNoTax").html(fmoney(addd2,2));
		             $(".totalRefund").html(fmoney(addd3,2));
		             $(".totalAmazonFee").html(fmoney(addd4,2));
		             $(".totalOtherFee").html(fmoney(addd5,2));
		             $(".totalTransportFee").html(fmoney(addd6,2));
		             $(".totalBuyCost").html(fmoney(addd7,2));
		             $(".totalProfit").html(fmoney(addd8,2));
		             if(addd9 + 0.02 > 100){
		            	 $(".totalProfitRate").html("100%");
		             } else {
		            	 $(".totalProfitRate").html(fmoney(addd9,2)+"%");
		             }
		             $(".totalAvg").html(fmoney((addd8/addd),2));
		             $(".totalProfitRatio").html(Math.round(fmoney((addd8*100/addd1),2)) +"%");
		             $(".totalAvgSale").html(fmoney((addd1/addd),2));
		             $(".totalAdFee").html(fmoney(addd10,2));
		             $(".totalAdSalesVolume").html(fmoney(addd11,0));
		             $(".totalAdSales").html(fmoney(addd12,2));
		             $(".totalAdProfit").html(fmoney(addd13,2));
		             $(".totalDealFee").html(fmoney(addd14,2));
		             $(".totalDealSalesVolume").html(fmoney(addd15,0));
		             $(".totalDealProfit").html(fmoney(addd16,2));
		             $(".totalSupportFee").html(fmoney(addd17,2));
		             $(".totalReviewFee").html(fmoney(addd18,2));
		             $(".totalRecallCost").html(fmoney(addd19,2));
		             $(".totalRecallFee").html(fmoney(addd20,2));
		             $(".totalTariff").html(fmoney(addd21,2));
		             $(".totalExpressFee").html(fmoney(addd24,2));
		             $(".totalVine").html(fmoney(addd25,2));
		             return nRow;
		         },"fnPreDrawCallback": function( oSettings ) { 
		        	 $(".total").html(0);
			         $(".totalSales").html(0);
		             $(".totalSalesNoTax").html(0);
		             $(".totalRefund").html(0);
		             $(".totalAmazonFee").html(0);
		             $(".totalOtherFee").html(0);
		             $(".totalTransportFee").html(0);
		             $(".totalBuyCost").html(0);
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
		             $(".totalTariff").html(0);
		             $(".totalExpressFee").html(0);
		             $(".totalVine").html(0);
				     <c:if test="${'2' ne flag }">
			             $(".totalStorageFee").html(0);
			             $(".totalLongStorageFee").html(0);
		             </c:if>
		         }  
		});

		 new FixedColumns( oTable,{
			 "iLeftColumns": 2,
	 		"iLeftWidth": 290
		 });
		
		
		<%-- 货币美元&欧元切换,默认为欧元版
		var html1 = " 货币类型:<select name=\"currencyType\" id=\"currencyType\" style=\"width: 100px\" onchange=\"changeCurrencyType()\">"+
			"<option value=\"EUR\" ${'EUR' eq fn:trim(currencyType)?'selected':''}>EUR</option>"+
			"<option value=\"USD\" ${'USD' eq fn:trim(currencyType)?'selected':''}>USD</option></select> &nbsp;&nbsp;&nbsp;";
		
		var html = "<a class=\"btn btn-warning btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/orderList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">订单</a>&nbsp;"+
		"<a class=\"btn btn-warning btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/skuList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">Sku</a>&nbsp;"+
		"<a class=\"btn btn-info btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/productList?country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">商品</a>&nbsp;"+
		"<a class=\"btn btn-warning btn-small\"  href=\"${ctx}/amazoninfo/salesReprots/exportAll?flag=2&country=${country}&type=${byTime}&time=${dateStr}&orderType=${orderType}&lineType=${lineType}&currencyType=${currencyType}\">导出</a>&nbsp; ";
		
		$("#contentTbDiv .spanexr div:first").append(html1 + html);	 --%>
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
		
		<li class="dropdown">
		   <a class="dropdown-toggle"  data-toggle="dropdown" href="#"><span class='otherPlatform'>Other</span><b class="caret"></b> </a>
		   <ul class="dropdown-menu" style="min-width:110px">
				<li><a class="countryHref" href="${ctx}/amazoninfo/salesProfits/ebayProfit?country=de" key='de'>DE Ebay</a></li>
				<li><a class="countryHref" href="${ctx}/amazoninfo/salesProfits/ebayProfit?country=com" key="com">US Ebay</a></li>
				<li><a href="${ctx}/amazoninfo/salesProfits/marketList">O2O</a></li>
		    </ul>
	   </li>
	</ul>
	<form id="searchForm" action="${ctx}/amazoninfo/salesProfits" method="post" class="breadcrumb form-search">
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
						<th><a  href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="占选取时间总销量">销量<br/>占比</a></th>
						<th><a  href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="占选取时间总销量额">销售额<br/>占比</a></th>
						<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="扣除增值税后的收入">税后收入<br/>(含邮费)</a></th>
						<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="包括退货和退款金额">退款</a></th>
						<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="含亚马逊FBAPerUnitFulfillmentFee、FBAWeightBasedFee、Commission">亚马逊费用</a></th>
						<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="欧洲共享库存费用、亚马逊包装费、自发货邮费收入等">杂费</a></th>
						<th><a href="#" style="color: #08c;" data-toggle="popover" data-html="true" rel="popover" data-content="加权平均运费">运输费</a></th>
						<th>采购成本</th>
						<th>利润</th>
						<th>利润占比</th>
						<th>单个利润</th>
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
						<th>关税</th>
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
								<%--<c:if test="${saleProfit.feeQuantity == 0 && saleProfit.salesVolume > 0}"><span style="color:red">[预估]</span></c:if> --%>
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
							<td><fmt:formatNumber value="${saleProfit.otherFee}" maxFractionDigits="2" /></td><!-- 杂费 -->
							<td><fmt:formatNumber value="${-saleProfit.transportFee}" maxFractionDigits="2" /></td><!-- 运输费 -->
							<!-- 采购成本 -->
							<td>
								<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" 
									data-content="占比：<fmt:formatNumber value="${-saleProfit.buyCost*100/saleProfit.sales}" maxFractionDigits="2" />%">
									<fmt:formatNumber value="${-saleProfit.buyCost}" maxFractionDigits="2" />
								</a>
							</td>
							<td>
								<fmt:formatNumber value="${saleProfit.profits}" maxFractionDigits="2" /><%-- 利润(未扣除财务成本) --%>
							</td>
							<td></td>
							<td>
								<c:if test="${saleProfit.salesVolume>0}">
									<fmt:formatNumber value="${saleProfit.profits/saleProfit.salesVolume}" maxFractionDigits="2" />
								</c:if>
								<c:if test="${saleProfit.salesVolume<=0}">
									0
								</c:if>
							</td>
							<td>
								<c:if test="${saleProfit.sales>0}">
									<fmt:formatNumber value="${saleProfit.profits/saleProfit.sales*100}" maxFractionDigits="0" />%
								</c:if>
								<c:if test="${saleProfit.salesVolume<=0}">
									0%
								</c:if>
							</td>
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
								<fmt:formatNumber value="${-saleProfit.tariff}" maxFractionDigits="2" />
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
									<c:set var="caiwuCost" value="0"></c:set>
									<c:choose>
										<c:when test="${turnover>0 }">
											<c:set var="caiwuCost" value="${(-saleProfit.transportFee-saleProfit.buyCost)/turnover*0.15/12}"></c:set>
											<c:if test="${'3' eq flag }">
												<c:set var="caiwuCost" value="${(-saleProfit.transportFee-saleProfit.buyCost)/turnover*0.15}"></c:set>
											</c:if>
											<a href="#" style="color: #08c;" data-toggle="popover" data-html="true" data-placement="left" rel="popover" 
												data-content="扣除财务成本后利润率:<fmt:formatNumber value="${(saleProfit.profits+caiwuCost)*100/saleProfit.sales}" maxFractionDigits="1" />%">
												<fmt:formatNumber value="${caiwuCost}" maxFractionDigits="2" />
											</a>
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
						<td id="totalOtherFee" class="totalOtherFee"></td>
						<td id="totalTransportFee" class="totalTransportFee"></td>
						<td id="totalBuyCost" class="totalBuyCost"></td>
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
						<td id="totalTariff" class="totalTariff"></td>
						<td id="totalExpressFee" class="totalExpressFee"></td>
						<td id="totalVine" class="totalVine"></td>
						<c:if test="${'2' ne flag }">
							<td id="totalStorageFee" class="totalStorageFee"></td>
							<td id="totalLongStorageFee" class="totalLongStorageFee"></td>
							<td id="totalTurnoverRate" class="totalTurnoverRate"></td>
							<td></td>
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
